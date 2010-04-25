//package org.valz.util.aggregates;
//
//import com.sdicons.json.model.JSONInteger;
//import com.sdicons.json.model.JSONString;
//import com.sdicons.json.model.JSONValue;
//import org.jetbrains.annotations.NotNull;
//
//import java.math.BigInteger;
//import java.util.Iterator;
//
//public class MinLong extends AbstractAggregate<Long> {
//    @Override
//    public Long reduce(@NotNull Iterator<Long> stream) {
//        long res = stream.next();
//        while (stream.hasNext()) {
//            long value = stream.next();
//            if (value < res) {
//                res = value;
//            }
//        }
//        return res;
//    }
//
//    @Override
//    public Long reduce(Long item1, Long item2) {
//        return Math.min(item1, item2);
//    }
//
//    public JSONValue dataToJson(Long item) {
//        return new JSONInteger(new BigInteger(item.toString()));
//    }
//
//    public Long parseData(JSONValue json) {
//        return ((JSONInteger)json).getValue().longValue();
//    }
//
//    public String getName() {
//        return "MinLong";
//    }
//
//    public JSONValue configToJson() {
//        return new JSONString("");
//    }
//
//
//
//    public static class ConfigParser implements AggregateConfigParser<Long> {
//        public MinLong parse(JSONValue json) {
//            return new MinLong();
//        }
//    }
//}