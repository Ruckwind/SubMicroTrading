package com.rr.core.recovery.json;

public class JSONException extends Exception {

    public JSONException( final String message ) {
        super( message );
    }

    public JSONException( final String s, final Throwable e ) { super( s, e ); }
}

