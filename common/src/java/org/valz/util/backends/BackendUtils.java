package org.valz.util.backends;

import org.valz.util.aggregates.Aggregate;
import org.valz.util.aggregates.BigMap;
import org.valz.util.datastores.Calculator;
import org.valz.util.datastores.DataStore;

import java.util.Map;

class BackendUtils {
//    public static <T> void submit(DataStore dataStore, String name, final Aggregate<T> aggregate,
//                                  final T value) throws InvalidAggregateException {
//        Aggregate<?> existingAggregate = dataStore.getAggregate(name);
//        if (existingAggregate == null) {
//            dataStore.submit(name, aggregate, value);
//        } else {
//            if (!existingAggregate.equals(aggregate)) {
//                throw new InvalidAggregateException(
//                        "Val with same name and different aggregate already exists.");
//            }
//
//            dataStore.modify(name, new Calculator<T>() {
//                public T calculate(T calcValue) {
//                    return aggregate.reduce(calcValue, value);
//                }
//            });
//        }
//    }
//
//    public static <T> void submitBigMap(DataStore dataStore, String name, final Aggregate<T> aggregate,
//                                        final Map<String, T> value) throws InvalidAggregateException {
//        BigMap<T> bigMap = (BigMap<T>)dataStore.getBigMap(name);
//        if (bigMap == null) {
//            dataStore.createBigMap(name, aggregate, value);
//        } else {
//            if (!bigMap.aggregate.equals(aggregate)) {
//                throw new InvalidAggregateException(
//                        "Val with same name and different aggregate already exists.");
//            }
//            bigMap.append(value);
//        }
//    }

    private BackendUtils() {
    }
}
