package com.rr.core.utils;

public class AppState {

    private static State _state = State.Bootstrap;

    public enum State { // dont change ... ordinal value is critical !
        Bootstrap,
        Initialising,
        Preparing,
        Starting,
        Running,
        Stopping,
        Stopped,
        Exiting
    }

    public static synchronized State getState() { return _state; }

    public static synchronized boolean setState( final State state ) {
        boolean change = state.ordinal() > _state.ordinal();

        if ( change ) {
            AppState._state = state;
        }

        return change;
    }

    public static boolean isTerminal() {
        State state = getState();
        return state == AppState.State.Stopping || state == State.Stopped || state == State.Exiting;
    }
}
