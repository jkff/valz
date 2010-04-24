package org.valz.examples.hitcount;

import org.h2.jdbc.JdbcSQLException;
import org.valz.client.Val;
import org.valz.client.Valz;
import org.valz.util.aggregates.LongSum;
import org.valz.util.protocol.InteractionType;
import org.valz.util.protocol.ReadBackend;
import org.valz.util.protocol.RemoteReadBackend;
import org.valz.util.protocol.WriteConfiguration;
import org.valz.util.protocol.messages.RequestMessage;
import org.valz.util.protocol.messages.SubmitRequest;

import java.io.StringReader;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.HashMap;

import com.sdicons.json.model.*;
import com.sdicons.json.parser.*;
import com.sdicons.json.mapper.*;

public class Main {
    public static void main(String[] args) throws Exception {

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("x", 1);
        map.put("s", "Hello");

        JSONValue jsonValue = JSONMapper.toJSON(map);
        String s = jsonValue.render(true);


        JSONParser parser = new JSONParser(new StringReader(s));
        JSONObject jsonObject = (JSONObject)parser.nextValue();
        HashMap<String, JSONValue> map2 = jsonObject.getValue();

//        int val = 5;
//        JSONNumber jsonNum = new JSONInteger(new BigInteger(new Integer(val).toString()));
//        String s2 = jsonNum.render(true);
//        JSONParser parser = new JSONParser(new StringReader(s2));
//        JSONValue jsonValue = parser.nextValue();
//        JSONInteger jsonNum2 = (JSONInteger)jsonValue;
//        int val2 = jsonNum2.getValue().intValue();

//        int x = 0;

//        WriteConfiguration conf = new WriteConfiguration();
//        conf.setServerURL("http://localhost:8081");
//
//        Valz.init(conf);
//
//        Val<Long> hitCount = Valz.register("org.valz.examples.hitcount.hitCount", new LongSum());
//
//        for(int i = 0; i < 10; ++i) {
//            hitCount.submit(1L);
//            Thread.sleep(50);
//        }
//
//
//        ReadBackend readBackend = new RemoteReadBackend("http://localhost:8081");
//
//        System.out.println("Count of vars: " + readBackend.listVars().size());


//        SubmitRequest<Long> submitRequest = new SubmitRequest<Long>("foo", new LongSum(), 1L);
//        RequestMessage msg = new RequestMessage(InteractionType.SUBMIT, submitRequest);
//
//
//        String s = new JSONSerializer()
//                .serialize(msg);
//
//        RequestMessage msg2 = new JSONDeserializer<RequestMessage>()
//                .use("data.class", SubmitRequest.class)
//                .use("data.aggregate.class", LongSum.class)
//                .deserialize(s);
//
//        int x = 0;

//        Class.forName("org.h2.Driver");
//        Connection conn = null;
//        try {
//            conn = DriverManager.getConnection("jdbc:h2:h2test", "sa", "");
//            conn.createStatement().execute("CREATE TABLE IF NOT EXISTS name2agg(name varchar(1048576) PRIMARY KEY," +
//                    " aggregate varchar(1048576));");
////            conn.createStatement().execute("INSERT INTO name2agg VALUES('var1', '');");
////            conn.createStatement().execute("INSERT INTO name2agg VALUES('var2', '');");
//            ResultSet res = conn.createStatement().executeQuery("SELECT * FROM name2agg;");
//
//            int x = 0;
//        } finally {
//            try {
//                if (conn != null) {
//                    conn.close();
//                }
//            } catch(JdbcSQLException e) {
//                // Ignore
//            }
//        }



//        WriteConfiguration conf = new WriteConfiguration();
//        conf.setServerURL("http://localhost:8080");
//
//        Valz.init(conf);
//
//        Val<Long> hitCount = Valz.register("org.valz.examples.hitcount.hitCount", new LongSum());
//
//        for(int i = 0; i < 10; ++i) {
//            hitCount.submit(1L);
//            Thread.sleep(50);
//        }
//
//
//        ReadConfiguration readConf = new ReadConfiguration();
//        readConf.setServerUrls(Arrays.asList("http://localhost:8080"));
//        RemoteReadBackend readBackend = new RemoteReadBackend(readConf, new AggregateRegistry());
//
//        System.out.println("Count of vars: " + readBackend.listVars().size());
    }
}
