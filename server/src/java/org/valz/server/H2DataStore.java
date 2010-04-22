package org.valz.server;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import org.valz.util.aggregates.Aggregate;


import java.sql.*;
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
        execute("INSERT INTO Aggregates(name, aggregate, value) VALUES(?, ?, ?);",
                name,
                new JSONSerializer().serialize(aggregate),
                new JSONSerializer().serialize(value));
    }

    public Collection<String> listVars() {
        return executeQuery(
            new Function<Collection<String>>() {
                public Collection<String> apply(ResultSet resultSet) throws SQLException {
                    Collection<String> list = new ArrayList<String>();
                    while (resultSet.next()) {
                        list.add(resultSet.getString(1));
                    }
                    return list;
                }
            },
            "SELECT name FROM Aggregates;");
    }

    public Aggregate getAggregate(String name) {
        return executeQuery(
            new Function<Aggregate>() {
                public Aggregate apply(ResultSet resultSet) throws SQLException {
                    if (!resultSet.next()) {
                        return null;
                    }
                    String str = resultSet.getString(1);
                    return new JSONDeserializer<Aggregate>().deserialize(str);
                }
            },
            "SELECT aggregate FROM Aggregates WHERE name = ?;",
                name);
    }

    public Object getValue(String name) {
        return executeQuery(
            new Function<Object>() {
                public Object apply(ResultSet resultSet) throws SQLException {
                    if (!resultSet.next()) {
                        return null;
                    }
                    String str = resultSet.getString(1);
                    return new JSONDeserializer().deserialize(str);
                }
            },
            "SELECT value FROM Aggregates WHERE name = ?;", name);
    }

    public void setValue(String name, Object value) {
        execute("UPDATE Aggregates SET value = ? WHERE name = ?;",
                new JSONSerializer().serialize(value),
                name);
    }


    private <T> T executeQuery(Function<T> func, String query, String... params) {
        ResultSet resultSet = null;
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            for (int i=0; i<params.length; i++) {
                statement.setString(i + 1, params[i]);
            }
            resultSet = statement.executeQuery();
            return func.apply(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch(SQLException e) {
                // Ignore
            }
        }
    }

    private void execute(String query, String... params) {
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            for (int i=0; i<params.length; i++) {
                statement.setString(i + 1, params[i]);
            }
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private interface Function<T> {
        T apply(ResultSet resultSet) throws SQLException;
    }
}