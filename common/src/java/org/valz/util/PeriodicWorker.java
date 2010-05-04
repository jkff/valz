package org.valz.util;

import java.util.Calendar;

/**
 * Thread daemon for background periodic work.
 * Wakes up every intervalMillis milliseconds and calls execute().
 */
public abstract class PeriodicWorker extends Thread {

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
            execute();
        }
    }

    public abstract void execute();
}
