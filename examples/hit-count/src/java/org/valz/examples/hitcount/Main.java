package org.valz.examples.hitcount;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import org.json.simple.parser.JSONParser;
import org.valz.util.aggregates.LongSum;
import org.valz.util.protocol.RequestType;
import org.valz.util.protocol.messages.RequestMessage;
import org.valz.util.protocol.messages.SubmitRequest;

import java.util.Date;

public class Main {
    public static void main(String[] args) throws Exception {

        SubmitRequest submitRequest = new SubmitRequest("foo", new LongSum(), 1);
        RequestMessage msg = new RequestMessage();
        msg.type = RequestType.SUBMIT;
        msg.data = submitRequest;


        String s = new JSONSerializer()
                .include("aggregate.class")
                .serialize(submitRequest);

        SubmitRequest msg2 = new JSONDeserializer<SubmitRequest>()
                .use("aggregate.class", LongSum.class)
                .deserialize(s);

        int x = 0;

//        Class.forName("org.h2.Driver");
//        Connection conn = DriverManager.getConnection("jdbc:h2:h2test", "sa", "");
//        conn.createStatement().execute("CREATE TABLE Foo(id int);");
//        conn.createStatement().execute("INSERT INTO Foo VALUES(1);");
//        conn.createStatement().execute("INSERT INTO Foo VALUES(2);");
//        ResultSet res = conn.createStatement().executeQuery("SELECT * FROM Foo;");
//        // add application code here
//        conn.close();


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
