package org.valz.util.datastores;

import com.sdicons.json.model.JSONValue;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.valz.util.JsonUtils;
import org.valz.util.aggregates.*;
import org.valz.util.keytypes.KeyType;
import org.valz.util.keytypes.KeyTypeFormatter;
import org.valz.util.protocol.messages.BigMapChunkValue;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class H2DataStore extends AbstractDataStore implements Closeable {

    private final DataSource dataSource;
    private final ObjectPool connectionPool;
    private final AggregateRegistry aggregateRegistry;

    // TODO: realize methods for BigMap

    public H2DataStore(String filename, AggregateRegistry aggregateRegistry) {

        this.aggregateRegistry = aggregateRegistry;
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        connectionPool = new GenericObjectPool(null);
        ConnectionFactory connectionFactory =
                new DriverManagerConnectionFactory(String.format("jdbc:h2:%s;MVCC=TRUE", filename), null);
        PoolableConnectionFactory poolableConnectionFactory =
                new PoolableConnectionFactory(connectionFactory, connectionPool, null, null, false, true);
        dataSource = new PoolingDataSource(connectionPool);

        execute("CREATE TABLE IF NOT EXISTS Valz ( name varchar PRIMARY KEY, aggregate varchar, value varchar )");
        execute("CREATE TABLE IF NOT EXISTS BigMaps ( name varchar PRIMARY KEY, aggregate varchar )");
        execute("CREATE SCHEMA IF NOT EXISTS BM");
    }



    public Collection<String> listVars() {
        return executeQuery(new ResultSetParser<Collection<String>>() {

            public Collection<String> apply(ResultSet resultSet) throws SQLException {
                Collection<String> list = new ArrayList<String>();
                while (resultSet.next()) {
                    list.add(resultSet.getString(1));
                }
                return list;
            }
        }, "SELECT name FROM Valz");
    }

    public <T> Aggregate<T> getAggregate(String name) {
        return executeGet(new JSONValueParser<Aggregate<T>>() {

            public Aggregate<T> apply(JSONValue jsonValue) throws ParserException {
                return AggregateFormatter.fromJson(aggregateRegistry, jsonValue);
            }
        }, "SELECT aggregate FROM Valz WHERE name = ?", name);
    }

    public <T> Value<T> getValue(String name) {
        final Aggregate<T> aggregate = getAggregate(name);
        return executeGet(new JSONValueParser<Value<T>>() {

            public Value<T> apply(JSONValue jsonValue) throws ParserException {
                return new Value<T>(aggregate, (T)aggregate.dataFromJson(jsonValue));
            }
        }, "SELECT value FROM Valz WHERE name = ?", name);
    }

    public void removeAggregate(String name) {
        execute("DELETE Valz WHERE name = ?", name);
    }

    @Override
    protected <T> void createAggregate(String name, Aggregate<T> aggregate, T value) {
        execute("INSERT INTO Valz(name, aggregate, value) VALUES(?, ?, ?)", name,
                AggregateFormatter.toJson(aggregateRegistry, aggregate).render(false),
                aggregate.dataToJson(value).render(false));
    }

    @Override
    protected <T> void setAggregateValue(String name, T newValue) {
        Aggregate<T> aggregate = getAggregate(name);
        execute("UPDATE Valz SET value = ? WHERE name = ?", aggregate.dataToJson(newValue).render(false),
                name);
    }



    @Override
    protected <K, T> void createBigMap(String name, KeyType<K> keyType, Aggregate<T> aggregate, Map<K, T> map) {

        name = name.toUpperCase();

        execute("INSERT INTO BigMaps(name, aggregate) VALUES(?, ?)", name,
                AggregateFormatter.toJson(aggregateRegistry, aggregate).render(false));
        execute(String.format("CREATE TABLE BM.%s ( key varchar PRIMARY KEY, value varchar )", name));
        for (Map.Entry<K, T> entry : map.entrySet()) {
            execute(String.format("INSERT INTO BM.%s (key, value) VALUES(?, ?)", name), entry.getKey(),
                    aggregate.dataToJson(entry.getValue()).render(false));
        }
    }

    @Override
    protected <K, T> void insertBigMapItem(String name, K key, T value) {
        Aggregate<T> aggregate = getBigMapAggregate(name);
        execute(String.format("INSERT INTO BM.%s (key, value) VALUES(?, ?)", name), key,
                    aggregate.dataToJson(value).render(false));
    }

    @Override
    protected <K, T> void updateBigMapItem(String name, K key, T newValue) {
        Aggregate<T> aggregate = getBigMapAggregate(name);
        execute(String.format("UPDATE Bm.%s SET value = ? WHERE key = ?", name),
                aggregate.dataToJson(newValue).render(false), key);
    }

    @Override
    protected <K, T> T getBigMapItem(String name, K key) {
        final Aggregate<T> aggregate = getBigMapAggregate(name);
        return executeGet(new JSONValueParser<T>() {
            public T apply(JSONValue jsonValue) throws ParserException {
                return (T)aggregate.dataFromJson(jsonValue);
            }
        }, String.format("SELECT value FROM Bm.%s WHERE key = ?", name), key);
    }

    public <T> Aggregate<T> getBigMapAggregate(String name) {
        name = name.toUpperCase();

        return executeGet(new JSONValueParser<Aggregate<T>>() {
            public Aggregate<T> apply(JSONValue jsonValue) throws ParserException {
                return AggregateFormatter.fromJson(aggregateRegistry, jsonValue);
            }
        }, "SELECT aggregate FROM BigMaps WHERE name = ?", name);
    }

    public <K> KeyType<K> getBigMapKeyType(String name) {
        name = name.toUpperCase();

        return executeGet(new JSONValueParser<KeyType<K>>() {
            public KeyType<K> apply(JSONValue jsonValue) throws ParserException {
                return KeyTypeFormatter.fromJson(aggregateRegistry, jsonValue);
            }
        }, "SELECT keyType FROM BigMaps WHERE name = ?", name);
    }

    public Collection<String> listBigMaps() {
        return executeQuery(new ResultSetParser<Collection<String>>() {
            public Collection<String> apply(ResultSet resultSet) throws SQLException {
                Collection<String> list = new ArrayList<String>();
                while (resultSet.next()) {
                    list.add(resultSet.getString(1));
                }
                return list;
            }
        }, "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='BM'");
    }

    public synchronized <K, T> BigMapChunkValue<K, T> getBigMapChunkForSubmit(String name, String fromKey,
                                                                        int count) {
        name = name.toUpperCase();

        BigMapChunkValue<K, T> chunk = getBigMapChunk(name, fromKey, count);
        execute(String.format("DELETE BM.%s WHERE key >= ? ORDER BY key LIMIT ?", name), fromKey, count);
        return chunk;
    }

    public <K, T> BigMapChunkValue<K, T> getBigMapChunk(String name, String fromKey, int count) {
        name = name.toUpperCase();

        final Aggregate<T> aggregate = getBigMapAggregate(name);
        final KeyType<K> keyType = getBigMapKeyType(name);
        Map<K, T> map = executeQuery(new ResultSetParser<Map<K, T>>() {
            public Map<K, T> apply(ResultSet resultSet) throws SQLException {
                Map<K, T> amap = new TreeMap<K, T>();
                while (resultSet.next()) {
                    try {
                        amap.put(keyType.dataFromJson(JsonUtils.jsonFromString(resultSet.getString(1))),
                                aggregate.dataFromJson(JsonUtils.jsonFromString((resultSet.getString(2)))));
                    } catch (ParserException e) {
                        throw new RuntimeException(e);
                    }
                }
                return amap;
            }
        }, String.format("SELECT TOP(?) key, value FROM BM.%s WHERE key >= ? ORDER BY key", name), count, fromKey);
        return new BigMapChunkValue<K, T>(aggregate, keyType, map);
    }

    public void removeBigMap(String name) {
        name = name.toUpperCase();

        execute(createQuery(String.format("DROP TABLE bm.%s", name)),
                createQuery("DELETE BigMaps WHERE name = ?", name));
    }

    private <T> T executeGet(JSONValueParser<T> func, String query, Object... params) {
        final JSONValueParser<T> finalFunc = func;
        return executeQuery(new ResultSetParser<T>() {
            public T apply(ResultSet resultSet) throws SQLException {
                if (!resultSet.next()) {
                    return null;
                }
                String str = resultSet.getString(1);
                try {
                    JSONValue jsonValue = JsonUtils.jsonFromString(str);
                    return finalFunc.apply(jsonValue);
                } catch (ParserException e) {
                    throw new RuntimeException(e);
                }
            }
        }, query, params);
    }



    private <T> T executeQuery(ResultSetParser<T> func, StatementCreator... creators) {
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            for (int i = 0; i < creators.length; i++) {
                try {
                    statement = creators[i].create(conn);
                    if (i == creators.length - 1 && func != null) {
                        resultSet = statement.executeQuery();
                        return func.apply(resultSet);
                    } else {
                        statement.execute();
                    }
                } finally {
                    try {
                        if (resultSet != null) {
                            resultSet.close();
                        }
                    } catch (SQLException e) {
                        // Ignore
                    }
                    try {
                        if (statement != null) {
                            statement.close();
                        }
                        statement = null;
                    } catch (SQLException e) {
                        // Ignore
                    }
                }
            }
            return null;
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                // Ignore
            }
            throw new RuntimeException(e);
        } finally {
            try {
                if (conn != null) {
                    conn.commit();
                    conn.close();
                }
            } catch (SQLException e) {
                // Ignore
            }
        }
    }

    private <T> T executeQuery(ResultSetParser<T> func, String query, Object... params) {
        return executeQuery(func, new SimpleStatementCreator(query, params));
    }

    private void execute(String query, Object... params) {
        executeQuery(null, query, params);
    }

    private void execute(StatementCreator... creators) {
        executeQuery(null, creators);
    }

    private StatementCreator createQuery(String query, Object... params) {
        return new SimpleStatementCreator(query, params);
    }

    public void close() throws IOException {
        try {
            connectionPool.close();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    private interface ResultSetParser<T> {
        T apply(ResultSet resultSet) throws SQLException;
    }

    private interface JSONValueParser<T> {
        T apply(JSONValue jsonValue) throws ParserException;
    }

    private interface StatementCreator {
        PreparedStatement create(Connection conn) throws SQLException;
    }

    private class SimpleStatementCreator implements StatementCreator {
        private final String query;
        private final Object[] params;

        public SimpleStatementCreator(String query, Object... params) {
            this.query = query;
            this.params = params;
        }

        public PreparedStatement create(Connection conn) throws SQLException {
            PreparedStatement statement = conn.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                if (params[i] instanceof String) {
                    statement.setString(i + 1, (String)params[i]);
                } else if (params[i] instanceof Integer) {
                    statement.setInt(i + 1, (Integer)params[i]);
                } else if (params[i] instanceof Long) {
                    statement.setLong(i + 1, (Long)params[i]);
                } else if (params[i] instanceof Double) {
                    statement.setDouble(i + 1, (Double)params[i]);
                }
            }
            return statement;
        }
    }
}