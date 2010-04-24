package org.valz.server;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import com.sdicons.json.mapper.JSONMapper;
import com.sdicons.json.mapper.MapperException;
import com.sdicons.json.model.JSONValue;
import com.sdicons.json.parser.JSONParser;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.valz.util.AggregateRegistry;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.ParserException;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class H2DataStore implements DataStore, Closeable {

    private final DataSource dataSource;
    private final ObjectPool connectionPool;
    private final AggregateRegistry registry;



    public H2DataStore(String filename, AggregateRegistry registry) {

        this.registry = registry;
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



    public <T> void createAggregate(String name, Aggregate<T> aggregate, T value) {
        execute("INSERT INTO Aggregates(name, aggregate, value) VALUES(?, ?, ?);",
                name,
                registry.pickleAggregate(aggregate).render(false),
                aggregate.dataToJson(value).render(false));
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

    public <T> Aggregate<T> getAggregate(String name) {
        return executeQuery(new Function<Aggregate<T>>() {
            public Aggregate<T> apply(ResultSet resultSet) throws SQLException {
                if (!resultSet.next()) {
                    return null;
                }
                String str = resultSet.getString(1);
                JSONValue jsonValue = null;
                try {
                    jsonValue = new JSONParser(new StringReader(str)).nextValue();
                } catch (TokenStreamException e) {
                    throw new RuntimeException(e);
                } catch (RecognitionException e) {
                    throw new RuntimeException(e);
                }
                try {
                    return registry.unpickleAggregate(jsonValue);
                } catch (ParserException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "SELECT aggregate FROM Aggregates WHERE name = ?;", name);
    }

    public <T> T getValue(String name) {
        final Aggregate<T> aggregate = getAggregate(name);
        return executeQuery(new Function<T>() {
            public T apply(ResultSet resultSet) throws SQLException {
                if (!resultSet.next()) {
                    return null;
                }
                String str = resultSet.getString(1);
                JSONValue jsonValue = null;
                try {
                    jsonValue = new JSONParser(new StringReader(str)).nextValue();
                } catch (TokenStreamException e) {
                    throw new RuntimeException(e);
                } catch (RecognitionException e) {
                    throw new RuntimeException(e);
                }
                try {
                    return (T)aggregate.parseData(jsonValue);
                } catch (ParserException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "SELECT value FROM Aggregates WHERE name = ?;", name);
    }

    public <T> void setValue(String name, T value) {
        Aggregate<T> aggregate = getAggregate(name);
        try {
            execute("UPDATE Aggregates SET value = ? WHERE name = ?;",
                    JSONMapper.toJSON(aggregate.dataToJson(value)).render(false),
                    name);
        } catch (MapperException e) {
            throw new RuntimeException(e);
        }
    }


    private <T> T executeQuery(Function<T> func, String query, String... params) {
        Connection conn = null;
        PreparedStatement statement = null;
        try {
            conn = dataSource.getConnection();

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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                // Ignore
            }
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