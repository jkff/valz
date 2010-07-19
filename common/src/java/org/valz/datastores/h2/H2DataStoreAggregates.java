package org.valz.datastores.h2;

import com.sdicons.json.model.JSONValue;
import org.valz.model.*;
import org.valz.util.ParserException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class H2DataStoreAggregates {

    private final Database database;
    private final AggregateRegistry aggregateRegistry;

    public H2DataStoreAggregates(Database database, AggregateRegistry aggregateRegistry) {
        this.database = database;
        this.aggregateRegistry = aggregateRegistry;

        database.execute(
                "CREATE TABLE IF NOT EXISTS Valz ( " +
                "   name varchar PRIMARY KEY, aggregate varchar, value varchar " +
                ")");
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
                return AggregateFormat.fromJson(aggregateRegistry, jsonValue);
            }
        }, "SELECT aggregate FROM Valz WHERE name = ?", name);
    }

    public <T> Sample<T> getValue(String name) {
        final Aggregate<T> aggregate = getAggregate(name);
        return database.executeGet(new Database.JSONValueParser<Sample<T>>() {
            public Sample<T> apply(JSONValue jsonValue) throws ParserException {
                return new Sample<T>(aggregate, aggregate.dataFromJson(jsonValue));
            }
        }, "SELECT value FROM Valz WHERE name = ?", name);
    }

    public void removeAggregate(String name) {
        database.execute("DELETE from Valz WHERE name = ?", name);
    }

    protected <T> void createAggregate(String name, Aggregate<T> aggregate, T value) {
        database.execute("INSERT INTO Valz(name, aggregate, value) VALUES(?, ?, ?)", name,
                AggregateFormat.toJson(aggregateRegistry, aggregate).render(false),
                aggregate.dataToJson(value).render(false));
    }

    protected <T> void setAggregateValue(String name, T newValue) {
        Aggregate<T> aggregate = getAggregate(name);
        database.execute("UPDATE Valz SET value = ? WHERE name = ?",
                aggregate.dataToJson(newValue).render(false),
                name);
    }
}