package org.valz.viewer;

import java.util.*;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class ViewerConfig {

    public final List<String> urls;

    public ViewerConfig(List<String> urls) {
        this.urls = urls;
    }

    public static ViewerConfig read() {
        Preferences prefs = Preferences.userRoot().node("server.config");
        Preferences urlPrefs = prefs.node("urls");

        Set<String> urls = new HashSet<String>();
        try {
            for (String key : urlPrefs.childrenNames()) {
                urls.add(urlPrefs.get("key", "http://localhost:8080"));
            }
        } catch (BackingStoreException e) {
            // Ignore
        }

        return new ViewerConfig(new ArrayList<String>(urls));
    }
}