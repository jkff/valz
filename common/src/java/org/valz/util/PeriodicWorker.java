package org.valz.util;

import org.apache.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Thread daemon for background periodic work.
 * Wakes up every intervalMillis milliseconds and calls tick().
 */
public abstract class PeriodicWorker {
    private static final Logger LOG = Logger.getLogger(PeriodicWorker.class);
    private String name;
    private long intervalMillis;

    public PeriodicWorker(final String name, long intervalMillis) {
        this.name = name;
        this.intervalMillis = intervalMillis;
    }

    public abstract void tick();

    public void start() {

        new Timer(name, true).schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    tick();
                } catch (Exception e) {
                    LOG.error("Tick of " + name + " failed", e);
                }
            }
        }, intervalMillis, intervalMillis);
    }
}
