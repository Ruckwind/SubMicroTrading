package com.rr.core.codec;

/**
 * used by decoder where some properties of decoded object will depend on the ID of the stream
 * this could be current file being processed or simply identifier associated with a src session
 */
public interface StreamDependant {

    String getStreamID();

    void setStreamID( String newStreamID );
}
