package com.rr.core.datarec;

import com.rr.core.lang.Env;
import com.rr.core.lang.ReusableString;
import com.rr.core.model.Event;

import java.util.TimeZone;

/**
 * DataPoint
 * <p>
 * each data point contains duplicated information .. ENV/appName event eventType all could be condensed
 * <p>
 * it isnt to allow for simple contextless processing by Python aggregated not just across process but across ENV
 * <p>
 * mgsSeqNum a counter which when combined with srcAppName and eventTimestamp gives a unique identifier .. can be reset to 0 on proc restart
 */

public interface DataPoint extends Event {

    /**
     * @return identifier of component that owned the data point eg creating strategy id
     */
    ReusableString getCompOwnerId();

    /**
     * @return the actual data in the data point (will depend on concrete implementation)
     */
    Object getDatum();

    Env getEnv();

    String getEventType();

    /**
     * @return grouping key for event .. eg instrument id / factor id / strategy id
     */
    ReusableString getGroupKey();

    /**
     * @return get real world time of event
     */
    long getLiveTS();

    String getSrcAppName();

    TimeZone getTz();

    void nextSeqNum();

    /**
     * set timezone to use with encoding timestamp
     *
     * @param timeZone
     */
    void setTimeZone( TimeZone timeZone );

    /**
     * stamp datapoint with current time
     * increment the msgSeqNum by 1
     * ensure appName + eventTimeStamp + subId   is unique
     */
    void stamp();
}
