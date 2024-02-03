package com.rr.core.recovery;

import com.rr.core.component.SMTControllableComponent;
import com.rr.core.component.SMTSnapshotMember;
import com.rr.core.component.SMTStartContext;

import java.util.Collection;

/**
 * snapshot caretaker
 * <p>
 * must be threadsafe
 *
 * @WARNING init method will be invoked twice so ensure its idempotent
 */
public interface SnapshotCaretaker extends SMTControllableComponent {

    /**
     * add an extra member to snapshot list .... only SPECIAL uses eg backtest proxy OR a sub component you want in its own file
     * <p>
     * extra entries processed BEFORE components taken from component manager
     */
    void addExtra( SMTSnapshotMember extra );

    void disableSnapshot( boolean isDisableSnapshot );

    long getTimeLastSnapshot();

    /**
     * restore last snapshot
     *
     * @param restoredComponents
     * @param context
     * @return the time of the last restore or 0 if none
     * @WARNING failure will result in system exit call
     * @WARNING import occurs BEFORE init() so json decoder will create StratInstProxy's for strat instruments that dont exist yet
     * Components must ensure that before startWork is invoked that those proxies are replaced
     */
    long restoreLast( Collection<Object> restoredComponents, final SMTStartContext context );

    void takeSnapshot();
}
