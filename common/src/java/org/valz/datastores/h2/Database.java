package org.valz.datastores.h2;

import com.sdicons.json.model.JSONValue;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.valz.util.JsonUtils;
import org.valz.util.ParserException;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database implements Closeable {
    private final DataSource dataSource;
    private final ObjectPool connectionPool;

    public Database(String driverName, String connectionString) {

        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        connectionPool = new GenericObjectPool(null);
        ConnectionFactory connectionFactory =
                new DriverManagerConnectionFactory(connectionString, null);
        dataSource = new PoolingDataSource(connectionPool);
    }

    public <T> T executeGet(JSONValueParser<T> func, String query, Object... params) {
        final JSONValueParser<T> finalFunc = func;
        return executeQuery(new ResultSetParser<T>() {
            public T apply(ResultSet resultSet) throws SQLException {
                if (!resultSet.next()) {
                    return null;
                }
                String str = resultSet.getString(1);
                JSONValue jsonValue = JsonUtils.jsonFromString(str);
                return finalFunc.apply(jsonValue);
            }
        }, query, params);
    }

    public <T> T executeQuery(ResultSetParser<T> func, StatementCreator... creators) {
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

    public <T> T executeQuery(ResultSetParser<T> func, String query, Object... params) {
        return executeQuery(func, new SimpleStatementCreator(query, params));
    }

    public void execute(String query, Object... params) {
        executeQuery(null, query, params);
    }

    public void execute(StatementCreator... creators) {
        executeQuery(null, creators);
    }

    public StatementCreator createQuery(String query, Object... params) {
        return new SimpleStatementCreator(query, params);
    }

    public void close() throws IOException {
        try {
            connectionPool.close();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }


    public interface ResultSetParser<T> {
        T apply(ResultSet resultSet) throws SQLException;
    }

    public interface JSONValueParser<T> {
        T apply(JSONValue jsonValue) throws ParserException;
    }

    public interface StatementCreator {
        PreparedStatement create(Connection conn) throws SQLException;
    }

    public class SimpleStatementCreator implements StatementCreator {
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