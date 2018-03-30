package edu.uab.registry.util;

public class StopWatch {
    private static final double MILLISECONDS_IN_ONE_SECOND = 1000;
    private static final double MILLISECONDS_IN_ONE_MINUTE = (1000 * 60);
    private long startTime = -1;
    private long stopTime = -1;

    /**
     * Default constructor
     */
    public StopWatch() {
    }

    /**
     * Captures the current system time
     * @return StopWatch
     */
    public StopWatch restart() {
        return start();
    }

    /**
     * Captures the current system time
     * @return StopWatch
     */
    public StopWatch start() {
        startTime = System.currentTimeMillis();
        return this;
    }

    /**
     * Captures the current system time.
     * @return StopWatch
     */
    public StopWatch stop() {
        stopTime = System.currentTimeMillis();

        return this;
    }

    /**
     * Obtains the elapsed time in milliseconds
     * @return long
     */
    public long getElapsedTimeInMilliSeconds() {
        if (startTime == -1) {
            return 0L;
        } else if (stopTime > 0) {
            return (stopTime - startTime);
        } else {
            return (System.currentTimeMillis() - startTime);
        }
    }

    /**
     * Obtains the elapsed time in seconds
     * @return double
     */
    public double getElapsedTimeInSeconds() {
        if (startTime == -1) {
            return 0.0;
        } else if (stopTime > 0) {
            return (stopTime - startTime) / MILLISECONDS_IN_ONE_SECOND;
        } else {
            return (System.currentTimeMillis() - startTime) / MILLISECONDS_IN_ONE_SECOND;
        }
    }

    /**
     * Obtains the elasped time in minutes
     * @return double
     */
    public double getElapsedTimeInMinutes() {
        if (startTime == -1) {
            return 0.0;
        } else if (stopTime > 0) {
            return ((stopTime - startTime) / MILLISECONDS_IN_ONE_MINUTE);
        } else {
            return (System.currentTimeMillis() - startTime) / MILLISECONDS_IN_ONE_MINUTE;
        }
    }

    /**
     * The method resets the class variables
     * @return StopWatch
     */
    public StopWatch reset() {
        startTime = -1;
        stopTime = -1;

        return this;
    }

    /**
     * Calculate and return the remaining seconds of this with passed milliseconds
     * @return double
     */
    public int getRemainingSecondsForMilliSeconds(long theTime) {
        int remaining = new Double((theTime - startTime) / MILLISECONDS_IN_ONE_SECOND).intValue();

        if (remaining < 0) {
            remaining = 0;
        }
        return remaining;
    }
}
