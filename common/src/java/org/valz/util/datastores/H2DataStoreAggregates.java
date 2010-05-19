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
import org.valz.util.keytypes.KeyTypeRegistry;
import org.valz.util.keytypes.MultiKey;
import org.valz.util.protocol.messages.BigMapChunkValue;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class H2DataStoreAggregates {

    private final Database database;
    private final AggregateRegistry aggregateRegistry;
    private final KeyTypeRegistry keyTypeRegistry;

    public H2DataStoreAggregates(Database database, KeyTypeRegistry keyTypeRegistry,
                       AggregateRegistry aggregateRegistry) {

        this.database = database;
        this.keyTypeRegistry = keyTypeRegistry;
        this.aggregateRegistry = aggregateRegistry;

        database.execute("CREATE TABLE IF NOT EXISTS Valz ( name varchar PRIMARY KEY, aggregate varchar, value varchar )");
    }



    public Collection<String> listVars() {
        return database.executeQuery(new Database.ResultSetParser<Collection<String>>() {

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
        return database.executeGet(new Database.JSONValueParser<Aggregate<T>>() {

            public Aggregate<T> apply(JSONValue jsonValue) throws ParserException {
                return AggregateFormatter.fromJson(aggregateRegistry, jsonValue);
            }
        }, "SELECT aggregate FROM Valz WHERE name = ?", name);
    }

    public <T> Value<T> getValue(String name) {
        final Aggregate<T> aggregate = getAggregate(name);
        return database.executeGet(new Database.JSONValueParser<Value<T>>() {

            public Value<T> apply(JSONValue jsonValue) throws ParserException {
                return new Value<T>(aggregate, (T)aggregate.dataFromJson(jsonValue));
            }
        }, "SELECT value FROM Valz WHERE name = ?", name);
    }

    public void removeAggregate(String name) {
        database.execute("DELETE Valz WHERE name = ?", name);
    }

    protected <T> void createAggregate(String name, Aggregate<T> aggregate, T value) {
        database.execute("INSERT INTO Valz(name, aggregate, value) VALUES(?, ?, ?)", name,
                AggregateFormatter.toJson(aggregateRegistry, aggregate).render(false),
                aggregate.dataToJson(value).render(false));
    }

    protected <T> void setAggregateValue(String name, T newValue) {
        Aggregate<T> aggregate = getAggregate(name);
        database.execute("UPDATE Valz SET value = ? WHERE name = ?", aggregate.dataToJson(newValue).render(false),
                name);
    }
}