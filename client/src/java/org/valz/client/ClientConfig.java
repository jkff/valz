package org.valz.client;

import java.util.*;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class ClientConfig {

    public final Collection<String> urls;

    public ClientConfig(Collection<String> urls) {
        this.urls = urls;
    }

    public static ClientConfig read() throws BackingStoreException {
        Preferences prefs = Preferences.userRoot().node("server.config");
        Preferences urlPrefs = prefs.node("urls");

        Set<String> urls = new HashSet<String>();
        for (String key : urlPrefs.childrenNames()) {
            urls.add(urlPrefs.get("key", "http://localhost:8080"));
        }

        return new ClientConfig(urls);
    }
}
