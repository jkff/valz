package org.valz.client;

import java.util.prefs.Preferences;

/**
 * Class contains client configuration:
 * - server url list;
 * - directory with aggregates;
 * - and other settings.
 */
public class ClientConfig {

    public static final String defaultServerUrl = "http://127.0.0.1:9125";
    public static final String defaultAggregatesDirectory = "aggregates";
    public static final String defaultTemporaryDatabaseFile = "h2client-storage.db";
    public static final int defaultFlushToServerInterval = 2000;
    public static final int defaultBigMapChunkSize = 100;

    /**
     * List of all avaiable.
     */
    public final String[] serverUrls;

    /**
     * Directory with aggregates as plugins.
     */
    public final String aggregatesDirectory;

    /**
     * File for h2 database, which will be used for local temporary
     * storing data if there is no server connection.
     */
    public final String temporaryDatabaseFile;

    /**
     * Interval between flushes data from local temporary database to server, milliseconds.
     */
    public final int flushToServerInterval;

    /**
     * Records count int bigmap chunk at flushing data to server.
     */
    public final int bigMapChunkSize;


    /**
     * Client config with explicit set parameters.
     *
     * @param serverUrls
     * @param aggregatesDirectory
     * @param temporaryDatabaseFile
     * @param flushToServerInterval
     * @param bigMapChunkSize
     */
    public ClientConfig(String[] serverUrls, String aggregatesDirectory, String temporaryDatabaseFile,
                        int flushToServerInterval, int bigMapChunkSize) {

        this.serverUrls = serverUrls;
        this.aggregatesDirectory = aggregatesDirectory;
        this.temporaryDatabaseFile = temporaryDatabaseFile;
        this.flushToServerInterval = flushToServerInterval;
        this.bigMapChunkSize = bigMapChunkSize;
    }

    public ClientConfig(String... serverUrls) {

        this(serverUrls, defaultAggregatesDirectory,
                defaultTemporaryDatabaseFile, defaultFlushToServerInterval, defaultBigMapChunkSize);
    }

    /**
     * Client config with default parameters.
     * Default parameters are constants in this class.
     */
    public ClientConfig() {
        this(new String[] {defaultServerUrl}, defaultAggregatesDirectory,
                defaultTemporaryDatabaseFile, defaultFlushToServerInterval, defaultBigMapChunkSize);
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
        String temporaryDatabaseFile = prefs.get("temporaryDatabaseFile", defaultTemporaryDatabaseFile);
        int flushToServerInterval = prefs.getInt("flushToServerInterval", defaultFlushToServerInterval);
        int bigMapChunkSize = prefs.getInt("bigMapChunkSize", defaultBigMapChunkSize);

        return new ClientConfig(serverUrls, aggregatesDirectory,
                temporaryDatabaseFile, flushToServerInterval, bigMapChunkSize);
    }
}
