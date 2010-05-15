package org.valz.util;

import org.apache.log4j.Logger;

import java.util.Calendar;

/**
 * Thread daemon for background periodic work.
 * Wakes up every intervalMillis milliseconds and calls execute().
 */
public abstract class PeriodicWorker extends Thread {
private static final Logger LOG = Logger.getLogger(PeriodicWorker.class);

    private final long intervalMillis;

    public PeriodicWorker(long intervalMillis) {
        this.intervalMillis = intervalMillis;
        setDaemon(true);
    }

    public void run() {
        long prevTime = System.currentTimeMillis();
        while (true) {
            long delayTime = intervalMillis + prevTime - System.currentTimeMillis();
            if (delayTime > 0) {
                try {
                    Thread.sleep(intervalMillis);
                } catch (InterruptedException e) {
                    break;
                }
            }
            prevTime = System.currentTimeMillis();
            try {
                execute();
            } catch (Exception e) {
                LOG.error("Unknown error in PeriodicWorker.execute()", e);
            }
        }
    }

    public abstract void execute();
}
