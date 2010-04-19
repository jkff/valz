package org.valz.examples.hitcount;

import org.valz.client.Val;
import org.valz.client.Valz;
import org.valz.util.aggregates.LongSum;
import org.valz.util.protocol.ReadBackend;
import org.valz.util.protocol.RemoteReadBackend;
import org.valz.util.protocol.WriteConfiguration;

public class Main {
    public static void main(String[] args) throws Exception {

        WriteConfiguration conf = new WriteConfiguration();
        conf.setServerURL("http://localhost:8081");

        Valz.init(conf);

        Val<Long> hitCount = Valz.register("org.valz.examples.hitcount.hitCount", new LongSum());

        for(int i = 0; i < 10; ++i) {
            hitCount.submit(1L);
            Thread.sleep(50);
        }


        ReadBackend readBackend = new RemoteReadBackend("http://localhost:8081");

        System.out.println("Count of vars: " + readBackend.listVars().size());


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
