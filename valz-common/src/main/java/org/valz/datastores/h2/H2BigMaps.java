package org.valz.datastores.h2;

import com.sdicons.json.model.JSONValue;
import org.valz.util.JsonUtils;
import org.valz.model.*;
import org.valz.protocol.messages.BigMapChunkValue;
import org.valz.util.ParserException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class H2BigMaps {

    private final Database database;
    private final AggregateRegistry aggregateRegistry;

    public H2BigMaps(Database database, AggregateRegistry aggregateRegistry) {
        this.database = database;
        this.aggregateRegistry = aggregateRegistry;
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        database.execute(
                "CREATE TABLE IF NOT EXISTS BigMaps ( name varchar PRIMARY KEY, aggregate varchar )");
        database.execute("CREATE SCHEMA IF NOT EXISTS BM");
    }


    public <T> void createBigMap(String name, Aggregate<T> aggregate, Map<String, T> map) {

        name = name.toUpperCase();
        // TODO: check for correct identifier

        database.execute("INSERT INTO BigMaps(name, aggregate) VALUES(?, ?)", name,
                AggregateFormat.toJson(aggregateRegistry, aggregate).render(false));

        database.execute(
                String.format(
                        "CREATE TABLE BM.%s " +
                        "(key varchar, value varchar, CONSTRAINT PK_BM_%s PRIMARY KEY(key))",
                        name, name));
        for (Map.Entry<String, T> entry : map.entrySet()) {
            insertBigMapItem(name, aggregate, entry.getKey(), entry.getValue());
        }
    }

    public <T> void insertBigMapItem(String name, Aggregate<T> aggregate, String key,
                                        T value) {
        name = name.toUpperCase();

        database.execute(
                String.format("INSERT INTO BM.%s (key, value) VALUES(?, ?)", name),
                toArray(key, aggregate.dataToJson(value).render(false)));
    }

    public <T> void updateBigMapItem(
            String name, Aggregate<T> aggregate, String key, T newValue)
    {
        name = name.toUpperCase();

        database.execute(
                String.format("UPDATE Bm.%s SET value = ? WHERE key = ?", name),
                aggregate.dataToJson(newValue).render(false), key);
    }

    public <T> T getBigMapItem(String name, final Aggregate<T> aggregate,
                                  String key) {
        name = name.toUpperCase();

        return database.executeGet(new Database.JSONValueParser<T>() {
            public T apply(JSONValue jsonValue) throws ParserException {
                return aggregate.dataFromJson(jsonValue);
            }
        }, String.format("SELECT value FROM Bm.%s WHERE key = ?", name), key);
    }

    public <T> Aggregate<T> getBigMapAggregate(String name) {
        name = name.toUpperCase();

        return database.executeGet(new Database.JSONValueParser<Aggregate<T>>() {
            public Aggregate<T> apply(JSONValue jsonValue) throws ParserException {
                return AggregateFormat.fromJson(aggregateRegistry, jsonValue);
            }
        }, "SELECT aggregate FROM BigMaps WHERE name = ?", name);
    }

    public Collection<String> listBigMaps() {
        return database.executeQuery(new Database.ResultSetParser<Collection<String>>() {
            public Collection<String> apply(ResultSet resultSet) throws SQLException {
                Collection<String> list = new ArrayList<String>();
                while (resultSet.next()) {
                    list.add(resultSet.getString(1));
                }
                return list;
            }
        }, "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='BM'");
    }

    public <T> BigMapChunkValue<T> popBigMapChunk(String name, String key, int count) {
        name = name.toUpperCase();

        Aggregate<T> aggregate = getBigMapAggregate(name);
        return popBigMapChunk(name, aggregate, key, count);
    }

    public synchronized <T> BigMapChunkValue<T> popBigMapChunk(
            String name, Aggregate<T> aggregate, String fromKey, int count)
    {
        name = name.toUpperCase();

        BigMapChunkValue<T> chunk = getBigMapChunk(name, aggregate, fromKey, count);
        database.execute(
                String.format("DELETE BM.%s WHERE key >= ? ORDER BY key LIMIT ?", name),
                fromKey, count);
        return chunk;
    }

    public <T> BigMapChunkValue<T> getBigMapChunk(String name, String key, int count) {
        name = name.toUpperCase();

        Aggregate<T> aggregate = getBigMapAggregate(name);
        return getBigMapChunk(name, aggregate, key, count);
    }

    public <T> BigMapChunkValue<T> getBigMapChunk(
            String name, final Aggregate<T> aggregate, String fromKey, int count) {
        name = name.toUpperCase();

        if (fromKey == null) {
            fromKey = "";
        }

        TreeMap<String, T> map = database.executeQuery(new Database.ResultSetParser<TreeMap<String, T>>() {
            public TreeMap<String, T> apply(ResultSet resultSet) throws SQLException {
                TreeMap<String, T> map = new TreeMap<String, T>();
                while (resultSet.next()) {
                    String key = resultSet.getString(1);
                    map.put(key, aggregate.dataFromJson(
                            JsonUtils.jsonFromString((resultSet.getString(2)))));
                }
                return map;
            }
        }, String.format("SELECT TOP(?) key, value FROM BM.%s WHERE key > ? ORDER BY key", name),
                count, fromKey);
        return new BigMapChunkValue(aggregate, map);
    }

    public void removeBigMap(String name) {
        name = name.toUpperCase();

        database.execute(
                database.createQuery(String.format("DROP TABLE BM.%s", name)),
                database.createQuery("DELETE FROM BigMaps WHERE name = ?", name));
    }

    private Object[] toArray(Object... objects) {
        List res = new ArrayList();
        for (Object item : objects) {
            if (item instanceof Collection) {
                res.addAll((Collection)item);
            } else {
                res.add(item);
            }
        }
        return res.toArray();
    }
}