package com.rr.core.model;

import java.util.Set;

public interface InstrumentSubscriptionListener {

    /**
     * update of subscription list
     *
     * @param insts         - the set of instruments added or removed
     * @param isAdded       - true if the instrument is now subscribed too, false is no longer subscribed too
     * @param fromTimestamp - time from which subscription required
     */
    void changed( Set<Instrument> insts, boolean isAdded, long fromTimestamp );
}
