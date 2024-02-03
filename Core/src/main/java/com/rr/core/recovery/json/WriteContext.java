package com.rr.core.recovery.json;

public enum WriteContext {

    Snapshot, // default context, eg persisting state to be replayed on process restart

    Export    // eg writing state to be imported into new process

}
