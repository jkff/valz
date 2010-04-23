package org.valz.server;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.valz.util.aggregates.Aggregate;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class H2DataStore implements DataStore, Closeable {

    private final DataSource dataSource;
    private final ObjectPool connectionPool;



    public H2DataStore(String filename) {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        connectionPool = new GenericObjectPool(null);
        ConnectionFactory connectionFactory =
                new DriverManagerConnectionFactory(String.format("jdbc:h2:%s", filename), null);
        PoolableConnectionFactory poolableConnectionFactory =
                new PoolableConnectionFactory(connectionFactory, connectionPool, null, null, false, true);
        dataSource = new PoolingDataSource(connectionPool);

        execute("CREATE TABLE IF NOT EXISTS Aggregates ( name varchar PRIMARY KEY, aggregate varchar, value varchar);");
    }



    public void createAggregate(String name, Aggregate<?> aggregate, Object value) {
        execute("INSERT INTO Aggregates(name, aggregate, value) VALUES(?, ?, ?);", name,
                new JSONSerializer().serialize(aggregate), new JSONSerializer().serialize(value));
    }

    public Collection<String> listVars() {
        return executeQuery(new Function<Collection<String>>() {
            public Collection<String> apply(ResultSet resultSet) throws SQLException {
                Collection<String> list = new ArrayList<String>();
                while (resultSet.next()) {
                    list.add(resultSet.getString(1));
                }
                return list;
            }
        }, "SELECT name FROM Aggregates;");
    }

    public Aggregate getAggregate(String name) {
        return executeQuery(new Function<Aggregate>() {
            public Aggregate apply(ResultSet resultSet) throws SQLException {
                if (!resultSet.next()) {
                    return null;
                }
                String str = resultSet.getString(1);
                return new JSONDeserializer<Aggregate>().deserialize(str);
            }
        }, "SELECT aggregate FROM Aggregates WHERE name = ?;", name);
    }

    public Object getValue(String name) {
        return executeQuery(new Function<Object>() {
            public Object apply(ResultSet resultSet) throws SQLException {
                if (!resultSet.next()) {
                    return null;
                }
                String str = resultSet.getString(1);
                return new JSONDeserializer().deserialize(str);
            }
        }, "SELECT value FROM Aggregates WHERE name = ?;", name);
    }

    public void setValue(String name, Object value) {
        execute("UPDATE Aggregates SET value = ? WHERE name = ?;", new JSONSerializer().serialize(value), name);
    }


    private <T> T executeQuery(Function<T> func, String query, String... params) {
        Connection conn = null;

        try {
            conn = dataSource.getConnection();
            PreparedStatement statement = null;
            try {
                statement = conn.prepareStatement(query);
                for (int i = 0; i < params.length; i++) {
                    statement.setString(i + 1, params[i]);
                }
                if (func == null) {
                    statement.execute();
                    return null;
                } else {
                    ResultSet resultSet = statement.executeQuery();
                    return func.apply(resultSet);
                }
            } finally {
                try {
                    if (statement != null) {
                        statement.close();
                    }
                } catch (SQLException e) {
                    // Ignore
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                // Ignore
            }
        }
    }

    private void execute(String query, String... params) {
        executeQuery(null, query, params);
    }

    public void close() throws IOException {
        try {
            connectionPool.close();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    private interface Function<T> {
        T apply(ResultSet resultSet) throws SQLException;
    }
}