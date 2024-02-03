package com.rr.core.model;

/**
 * Represents an instance in a time series
 * <p>
 * Core TimeSeriesEntry will have a start and end timestamp
 * <p>
 * The timeframe for the version is shown as :-
 * <p>
 * startTime <= T < endTime
 * <p>
 * so endTime is not inclusive when considering valid range
 */
public interface PointInTime {

    /**
     * @return timestamp of the event in INTERNAL TIME format
     */
    long getEventTimestamp();
}
