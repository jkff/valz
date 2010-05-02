package org.valz.examples.hitcount;

public class Main {
    public static void main(String[] args) throws Exception {

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
