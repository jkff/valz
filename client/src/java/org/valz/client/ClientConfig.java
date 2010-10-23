package org.valz.client;

import java.util.prefs.Preferences;

/**
 * Class contains client configuration:
 * - server url list;
 * - directory with aggregates;
 * - and other settings.
 */
public class ClientConfig {

    public static final String defaultServerUrl = "127.0.0.1:9125";
    public static final String defaultAggregatesDirectory = "aggregates";
    public static final boolean defaultUseNonBlockingSubmit = false;
    public static final boolean defaultTemporarySaveSubmitsAtHdd = false;

    /**
     * List of all avaiable.
     */
    public final String[] serverUrls;

    /**
     * Directory with aggregates as plugins.
     */
    public final String aggregatesDirectory;

    /**
     * If true, submit will push data to non blocking queue,
     * special thread will send data from this queue to valz servers.
     *
     * Else, submit will immediately send data to valz servers.
     * If client is stopped, data in this queue will be lost.
     */
    public final boolean useNonBlockingSubmit;

    /**
     * If true, data will be saved in local database.
     * If you use client without server connection (for example, server temporary is down),
     * you can close client and data will not be lost.
     * Client will send saved data to server at first time when both of them will work.  
     */
    public final boolean temporarySaveSubmitsAtHdd;


    /**
     * Client config with explicit set parameters.
     *
     * @param serverUrls
     * @param aggregatesDirectory
     * @param useNonBlockingSubmit
     * @param temporarySaveSubmitsAtHdd
     */
    public ClientConfig(String[] serverUrls, String aggregatesDirectory, boolean useNonBlockingSubmit,
                        boolean temporarySaveSubmitsAtHdd) {

        this.serverUrls = serverUrls;
        this.aggregatesDirectory = aggregatesDirectory;
        this.useNonBlockingSubmit = useNonBlockingSubmit;
        this.temporarySaveSubmitsAtHdd = temporarySaveSubmitsAtHdd;
    }

    /**
     * Client config with default parameters.
     * Default parameters are constants in this class.
     */
    public ClientConfig() {
        this(new String[] {defaultServerUrl},
                defaultAggregatesDirectory,
                defaultUseNonBlockingSubmit,
                defaultTemporarySaveSubmitsAtHdd);
    }

    /**
     * Reads client config from default preferences store from user root node.
     *
     * @return ClientConfig with read preferences.
     * @see java.util.prefs
     */
    public static ClientConfig read() {
        Preferences prefs = Preferences.userRoot().node("client.config");

        String allServers = prefs.get("servers", defaultServerUrl);
        String[] serverUrls = allServers.split("[\\s]+");
        String aggregatesDirectory = prefs.get("aggregates", defaultAggregatesDirectory);
        boolean useNonBlockingSubmit = prefs.getBoolean("useNonBlockingSubmit", defaultUseNonBlockingSubmit);
        boolean temporarySaveSubmitsAtHdd = prefs.getBoolean("temporarySaveSubmitsAtHdd", defaultTemporarySaveSubmitsAtHdd);

        return new ClientConfig(
                serverUrls,
                aggregatesDirectory,
                useNonBlockingSubmit,
                temporarySaveSubmitsAtHdd);
    }
}
