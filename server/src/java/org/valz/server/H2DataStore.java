package org.valz.server;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.LongSum;
import org.valz.util.io.IOUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class H2DataStore implements DataStore {

    private final Connection conn;



    public H2DataStore(String filename) {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            conn = DriverManager.getConnection(String.format("jdbc:h2:%s", filename), "sa", "");
            conn.createStatement().execute("CREATE TABLE IF NOT EXISTS Aggregates ( name varchar PRIMARY KEY, aggregate varchar, value varchar);");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    public void createAggregate(String name, Aggregate<?> aggregate, Object value) {
        final String strName = name;
        final String strAggregate = new JSONSerializer().serialize(aggregate);
        final String strValue = new JSONSerializer().serialize(value);
        executeQuery(String.format("SELECT name, aggregate, value FROM Aggregates WHERE name = '%s';", name),
            new Function<Void>() {
                public Void apply(ResultSet resultSet) throws SQLException {
                    if (resultSet.next()) {
                        throw new RuntimeException("Aggregate already existed.");
                    }
                    resultSet.moveToInsertRow();
                    resultSet.updateString(1, strName);
                    resultSet.updateString(2, strAggregate);
                    resultSet.updateString(3, strValue);
                    resultSet.insertRow();
                    return null;
                }
            },
            true);
    }

    public Collection<String> listVars() {
        return executeQuery("SELECT name FROM Aggregates;",
            new Function<Collection<String>>() {
                public Collection<String> apply(ResultSet resultSet) throws SQLException {
                    Collection<String> list = new ArrayList<String>();
                    while (resultSet.next()) {
                        list.add(resultSet.getString(1));
                    }
                    return list;
                }
            });
    }

    public Aggregate getAggregate(String name) {
        return executeQuery(String.format("SELECT aggregate FROM Aggregates WHERE name = '%s';", name),
            new Function<Aggregate>() {
                public Aggregate apply(ResultSet resultSet) throws SQLException {
                    if (!resultSet.next()) {
                        return null;
                    }
                    String str = resultSet.getString(1);
                    return new JSONDeserializer<Aggregate>().deserialize(str);
                }
            });
    }

    public Object getValue(String name) {
        return executeQuery(String.format("SELECT value FROM Aggregates WHERE name = '%s';", name),
            new Function<Object>() {
                public Object apply(ResultSet resultSet) throws SQLException {
                    if (!resultSet.next()) {
                        return null;
                    }
                    String str = resultSet.getString(1);
                    return new JSONDeserializer().deserialize(str);
                }
            });
    }

    public void setValue(String name, Object value) {
        final String strValue = new JSONSerializer().serialize(value);
        executeQuery(String.format("SELECT name, aggregate, value FROM Aggregates WHERE name = '%s';", name),
            new Function<Void>() {
                public Void apply(ResultSet resultSet) throws SQLException {
                    if (!resultSet.next()) {
                        throw new RuntimeException("No previous value existed.");
                    }
                    resultSet.updateString(3, strValue);
                    resultSet.updateRow();
                    return null;
                }
            },
            true);
    }

    private <T> T executeQuery(String query, Function<T> func) {
        return executeQuery(query, func, false);
    }

    private <T> T executeQuery(String query, Function<T> func, boolean updatable) {
        ResultSet resultSet = null;
        try {
            if (updatable) {
                resultSet = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE).executeQuery(query);
            } else {
                resultSet = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY).executeQuery(query);
            }
            return func.apply(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeSilently(resultSet);
        }
    }

    private interface Function<T> {
        T apply(ResultSet resultSet) throws SQLException;
    }


    // Now without junit
    private static class Test {
        public void testBasics() {
            H2DataStore dataStore = new H2DataStore("h2test");

            String varName = "var1";

            Collection<String> list = dataStore.listVars();
            dataStore.createAggregate(varName, new LongSum(), 1L);
            Object value = dataStore.getValue(varName);
            Aggregate aggregate = dataStore.getAggregate(varName);
            dataStore.setValue(varName, 2);
            Object value2 = dataStore.getValue(varName);
            Collection<String> list2 = dataStore.listVars();

            int x = 0;
        }
    }
}