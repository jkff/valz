package org.valz.examples;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.mortbay.jetty.Server;
import org.valz.client.Val;
import org.valz.client.Valz;
import org.valz.server.ServerUtils;
import org.valz.model.LongSum;
import org.valz.backends.RoundRobinWriteBackend;
import org.valz.backends.WriteBackend;
import org.valz.viewer.ValzWebServer;
import org.valz.viewer.ViewerConfig;
import org.valz.viewer.ViewerInternalConfig;

import java.util.ArrayList;
import java.util.List;

public class OldTutorial {
    private static final Logger log = Logger.getLogger(OldTutorial.class);


    public static void main(String[] args) throws Exception {
        PropertyConfigurator.configure("log4j.properties");
    }

    private OldTutorial() {
    }
}
