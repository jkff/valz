package org.valz.datastores.h2;

import com.sdicons.json.model.JSONValue;
import org.valz.util.JsonUtils;
import org.valz.model.*;
import org.valz.keytypes.*;
import org.valz.protocol.messages.BigMapChunkValue;
import org.valz.util.ParserException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class H2DataStoreBigMaps {

    private final Database database;
    private final AggregateRegistry aggregateRegistry;
    private final KeyTypeRegistry keyTypeRegistry;

    public H2DataStoreBigMaps(Database database, KeyTypeRegistry keyTypeRegistry,
                              AggregateRegistry aggregateRegistry) {

        this.database = database;
        this.keyTypeRegistry = keyTypeRegistry;
        this.aggregateRegistry = aggregateRegistry;
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        database.execute(
                "CREATE TABLE IF NOT EXISTS BigMaps ( name varchar PRIMARY KEY, keyType varchar, aggregate varchar )");
        database.execute("CREATE SCHEMA IF NOT EXISTS BM");
    }


    public <K, T> void createBigMap(String name, KeyType<K> keyType, Aggregate<T> aggregate, Map<K, T> map) {

        name = name.toUpperCase();
        // TODO: check for correct identifier

        database.execute("INSERT INTO BigMaps(name, keyType, aggregate) VALUES(?, ?, ?)", name,
                KeyTypeFormat.toJson(keyTypeRegistry, keyType).render(false),
                AggregateFormat.toJson(aggregateRegistry, aggregate).render(false));

        String columnsDeclaration = formatKeyType(keyType, new KeyTypeStringFormatter() {
            public String apply(KeyType keyType, int index) {
                return String.format("key%d %s NOT NULL", index, toDbType(keyType));
            }
        }, ", ");

        String columnsNames = formatKeyType(keyType, "key%d", ", ");


        database.execute(
                String.format("CREATE TABLE BM.%s (%s, value varchar, CONSTRAINT PK_BM_%s PRIMARY KEY(%s))", name,
                        columnsDeclaration, name, columnsNames));
        for (Map.Entry<K, T> entry : map.entrySet()) {
            insertBigMapItem(name, keyType, aggregate, entry.getKey(), entry.getValue());
        }
    }

    public <K, T> void insertBigMapItem(String name, KeyType<K> keyType, Aggregate<T> aggregate, K key,
                                        T value) {
        name = name.toUpperCase();

        String columnsNames = formatKeyType(keyType, "key%d", ", ");
        String whatSignes = formatKeyType(keyType, "?", ", ");

        database.execute(
                String.format("INSERT INTO BM.%s (%s, value) VALUES(?, %s)", name, columnsNames, whatSignes),
                toArray(key, aggregate.dataToJson(value).render(false)));
    }

    public <K, T> void updateBigMapItem(
            String name, KeyType<K> keyType, Aggregate<T> aggregate, K key, T newValue)
    {
        name = name.toUpperCase();

        String keyEquals = formatKeyType(keyType, "key%d = ?", " AND ");
        List keys = keyToList(key);

        database.execute(String.format("UPDATE Bm.%s SET value = ? WHERE %s", name, keyEquals),
                toArray(aggregate.dataToJson(newValue).render(false), keys));
    }

    public <K, T> T getBigMapItem(String name, final KeyType<K> keyType, final Aggregate<T> aggregate,
                                  K key) {
        name = name.toUpperCase();

        String keyEquals = formatKeyType(keyType, "key%d = ?", " AND ");
        List keys = keyToList(key);

        return database.executeGet(new Database.JSONValueParser<T>() {
            public T apply(JSONValue jsonValue) throws ParserException {
                return aggregate.dataFromJson(jsonValue);
            }
        }, String.format("SELECT value FROM Bm.%s WHERE %s", name, keyEquals), toArray(keys));
    }

    public <T> Aggregate<T> getBigMapAggregate(String name) {
        name = name.toUpperCase();

        return database.executeGet(new Database.JSONValueParser<Aggregate<T>>() {
            public Aggregate<T> apply(JSONValue jsonValue) throws ParserException {
                return AggregateFormat.fromJson(aggregateRegistry, jsonValue);
            }
        }, "SELECT aggregate FROM BigMaps WHERE name = ?", name);
    }

    public <K> KeyType<K> getBigMapKeyType(String name) {
        name = name.toUpperCase();

        return database.executeGet(new Database.JSONValueParser<KeyType<K>>() {
            public KeyType<K> apply(JSONValue jsonValue) throws ParserException {
                return KeyTypeFormat.fromJson(keyTypeRegistry, jsonValue);
            }
        }, "SELECT keyType FROM BigMaps WHERE name = ?", name);
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

    public <K, T> BigMapChunkValue<K, T> popBigMapChunk(String name, K key, int count) {
        name = name.toUpperCase();

        KeyType<K> keyType = getBigMapKeyType(name);
        Aggregate<T> aggregate = getBigMapAggregate(name);
        return popBigMapChunk(name, keyType, aggregate, key, count);
    }

    public synchronized <K, T> BigMapChunkValue<K, T> popBigMapChunk(String name, KeyType<K> keyType,
                                                                              Aggregate<T> aggregate,
                                                                              K fromKey, int count) {
        name = name.toUpperCase();

        String keyGreater = formatKeyType(keyType, "key%d >= ?", " AND ");
        String columnsNames = formatKeyType(keyType, "key%d", ", ");
        List keys = keyToList(fromKey);

        BigMapChunkValue<K, T> chunk = getBigMapChunk(name, keyType, aggregate, fromKey, count);
        database.execute(
                String.format("DELETE BM.%s WHERE %s ORDER BY %s LIMIT ?", name, keyGreater, columnsNames),
                toArray(keys, count));
        return chunk;
    }

    public <K, T> BigMapChunkValue<K, T> getBigMapChunk(String name, K key, int count) {
        name = name.toUpperCase();

        KeyType<K> keyType = getBigMapKeyType(name);
        Aggregate<T> aggregate = getBigMapAggregate(name);
        return getBigMapChunk(name, keyType, aggregate, key, count);
    }

    public <K, T> BigMapChunkValue<K, T> getBigMapChunk(String name, final KeyType<K> keyType,
                                                        final Aggregate<T> aggregate, K fromKey, int count) {
        name = name.toUpperCase();

        if (fromKey == null) {
            fromKey = keyType.getMinValue();
        }

        String keyGreater = formatKeyType(keyType, "key%d >= ?", " AND ");
        String columnsNames = formatKeyType(keyType, "key%d", ", ");
        final List keys = keyToList(fromKey);

        TreeMap<K, T> map = database.executeQuery(new Database.ResultSetParser<TreeMap<K, T>>() {
            public TreeMap<K, T> apply(ResultSet resultSet) throws SQLException {
                TreeMap<K, T> map = new TreeMap<K, T>(keyType);
                while (resultSet.next()) {
                    List list = new ArrayList();
                    for (int i = 0; i < keys.size(); i++) {
                        list.add(getResultSetValue(keyType, resultSet, i));
                    }

                    K key;
                    if (keyType instanceof MultiKey) {
                        key = (K)list;
                    } else {
                        key = (K)list.get(0);
                    }
                    map.put(key, aggregate.dataFromJson(
                            JsonUtils.jsonFromString((resultSet.getString(keys.size() + 1)))));
                }
                return map;
            }
        }, String.format("SELECT TOP(?) %s, value FROM BM.%s WHERE %s ORDER BY %s", columnsNames, name,
                keyGreater, columnsNames), toArray(count, keys));
        return new BigMapChunkValue<K, T>(keyType, aggregate, map);
    }

    public void removeBigMap(String name) {
        name = name.toUpperCase();

        database.execute(
                database.createQuery(String.format("DROP TABLE BM.%s", name)),
                database.createQuery("DELETE FROM BigMaps WHERE name = ?", name));
    }



    private Object getResultSetValue(KeyType keyType, ResultSet resultSet, int i) throws SQLException {
        i++; // because indices in resultSet are one-based
        if (keyType instanceof LongKey) {
            return resultSet.getLong(i);
        } else if (keyType instanceof StringKey) {
            return resultSet.getString(i);
        }
        throw new IllegalArgumentException("Unrecognized type of keyType.");
    }

    private String toDbType(KeyType keyType) {
        if (keyType instanceof LongKey) {
            return "LONG";
        } else if (keyType instanceof StringKey) {
            return "VARCHAR";
        }
        throw new IllegalArgumentException("Unrecognized type of keyType.");
    }

    private String formatKeyType(KeyType keyType, KeyTypeStringFormatter formatter, String joiner) {
        // if keyType already MultiKey, tree of Lists will be flatten
        MultiKey wrapper = new MultiKey(Collections.singletonList(keyType));

        List<KeyType> keys = wrapper.getKeys();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            sb.append(String.format("%s%s", joiner, formatter.apply(keys.get(i), i)));
        }
        return sb.substring(joiner.length());
    }

    private String formatKeyType(KeyType keyType, final String formatString, String joiner) {
        return formatKeyType(keyType, new KeyTypeStringFormatter() {
            public String apply(KeyType keyType, int index) {
                return String.format(formatString, index);
            }
        }, joiner);
    }

    private <K> List keyToList(K key) {
        List keys;
        if (key instanceof List) {
            keys = ((List)key);
        } else {
            keys = Arrays.asList((Object)key);
        }
        return keys;
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



    private interface KeyTypeStringFormatter {
        String apply(KeyType keyType, int index);
    }
}