import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.mortbay.jetty.Server;
import org.valz.server.ValzServer;
import org.valz.viewer.ValzWebServer;

import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final Logger log = Logger.getLogger(Main.class);


    public static void main(String[] args) throws Exception {

        PropertyConfigurator.configure("log4j.properties");

        Server valzServer0 = ValzServer.startServer(ValzServer.getServerConfiguration("h2store0", 8800));
        Server valzServer1 = ValzServer.startServer(ValzServer.getServerConfiguration("h2store1", 8801));
        Server valzServer2 = ValzServer.startServer(ValzServer.getServerConfiguration("h2store", 8802));

        Server valzWebServer = ValzWebServer.startServer(ValzWebServer.getWebServerConfiguration(8900,
                portsToLocalAddresses(8800, 8801, 8802)));


        try {
            valzWebServer.join();
        } catch (InterruptedException e) {
            log.error("Could not stop valz web server", e);
        }
        try {
            valzServer0.join();
        } catch (InterruptedException e) {
            log.error("Could not stop valz server 0", e);
        }
        try {
            valzServer1.join();
        } catch (InterruptedException e) {
            log.error("Could not stop valz server 1", e);
        }
    }

    private static List<String> portsToLocalAddresses(int... ports) {
        List<String> list = new ArrayList<String>();
        for (int item : ports) {
            list.add(String.format("http://localhost:%d", item));
        }
        return list;
    }


    private Main() { }
}
