package com.rr.core.component;

import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;

/**
 * component run state, order is critical precedence is via ordinal
 */
public enum CompRunState {

    Unset,
    Initial,
    Initialised,
    Started,
    PassivePause, // still active, can for example process market data
    Active,
    Broken,
    Fixing,
    HardPause,    // cant process, eg pause while preparing to stop
    /**
     * all below states are one way stopping can only change to stopped
     */
    Complete,
    Stopping,
    Stopped,
    Dead;

    private static final Logger _log = LoggerFactory.create( CompRunState.class );

    public static boolean canProcess( CompRunState state ) {
        switch( state ) {
        case Unset:
        case Initial:
        case Initialised:
        case Started:
        case PassivePause:
        case Active:
            return true;
        case Broken:
        case Fixing:
        case HardPause:
        case Complete:
        case Stopping:
        case Stopped:
        case Dead:
            break;
        }

        return false;
    }

    public static boolean isStopping( CompRunState state ) {
        switch( state ) {
        case Unset:
        case Initial:
        case Initialised:
        case Started:
        case PassivePause:
        case Active:
        case Broken:
        case Fixing:
            return false;
        case HardPause:
        case Complete:
        case Stopping:
        case Stopped:
        case Dead:
            break;
        }

        return true;
    }

    public static boolean isComplete( CompRunState state ) {
        return state.ordinal() >= Complete.ordinal();
    }

    public static boolean procStateChange( String compId, final CompRunState curState, final CompRunState proposedState ) {

        boolean canChange = false;

        if ( proposedState != null && curState != proposedState ) {
            if ( curState == null ) {
                canChange = true;
            } else if ( curState == Broken && proposedState != Fixing ) {
                _log.warn( compId + " cannot change from " + curState + " to " + proposedState + " only to Fixing" );
            } else if ( curState == Fixing && proposedState != PassivePause ) {
                _log.warn( compId + " cannot change from " + curState + " to " + proposedState + " only to PassivePause" );
            } else if ( curState == HardPause ) {
                if ( proposedState == Active || proposedState.ordinal() > curState.ordinal() ) {
                    canChange = true;
                } else {
                    _log.warn( compId + " cannot change from " + curState + " to " + proposedState );
                }
            } else if ( curState.ordinal() >= Stopping.ordinal() && proposedState.ordinal() <= curState.ordinal() ) {
                _log.log( Level.trace, compId + " ignore change request from " + curState + " to " + proposedState );
            } else {
                canChange = true;
            }
        }

        return canChange;
    }

    public boolean canProcess() { return canProcess( this ); }

    public boolean isStopping() { return isStopping( this ); }

    public boolean isComplete() { return isComplete( this ); }

    public boolean isPaused() { return this == PassivePause || this == HardPause; }
}
