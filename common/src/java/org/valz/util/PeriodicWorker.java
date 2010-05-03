package org.valz.util;

import java.util.Calendar;

/**
 * Thread daemon for background periodic work.
 * Wakes up every intervalMillis milliseconds and calls action().
 */
public abstract class PeriodicWorker extends Thread {

    private final long intervalMillis;

    public PeriodicWorker(long intervalMillis) {
        this.intervalMillis = intervalMillis;
        setDaemon(true);
    }

    public void run() {
        long prevTime = Calendar.getInstance().getTimeInMillis();
        while (true) {
            long delayTime = intervalMillis + prevTime - Calendar.getInstance().getTimeInMillis();
            if (delayTime > 0) {
                try {
                    Thread.sleep(intervalMillis);
                } catch (InterruptedException e) {
                    break;
                }
            }
            prevTime = Calendar.getInstance().getTimeInMillis();
            action();
        }
    }

    public abstract void action();
}
