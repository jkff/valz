package org.valz.util.backends;

import java.util.Calendar;

/**
 * Thread daemon for background periodic work.
 * Wakes up every intervalMillis milliseconds and calls action().
 */
public abstract class PeriodicWorker implements Runnable {

    private final int intervalMillis;

    public PeriodicWorker(int intervalMillis) {
        this.intervalMillis = intervalMillis;
    }

    public void run() {
        Thread.currentThread().setDaemon(true);

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
