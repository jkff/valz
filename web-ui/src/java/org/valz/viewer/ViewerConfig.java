package org.valz.viewer;

import java.util.*;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class ViewerConfig {
	
	public static final String DEFAULT_URL = "http://localhost:9125";

    public final List<String> urls;

    public ViewerConfig(List<String> urls) {
        this.urls = urls;
    }

    public static ViewerConfig read() {
        Preferences prefs = Preferences.userRoot().node("server.config");
        Preferences urlPrefs = prefs.node("urls");
        urlPrefs.node("default").put("defaultUrl", DEFAULT_URL);

        Set<String> urls = new HashSet<String>();
        try {
            for (String key : urlPrefs.childrenNames()) {
                urls.add(urlPrefs.get(key, "http://localhost:9125"));
            }
        } catch (BackingStoreException e) {
            // Ignore
        }

        return new ViewerConfig(new ArrayList<String>(urls));
    }
}