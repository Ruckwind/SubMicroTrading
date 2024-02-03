package com.rr.core.session.file;

import com.rr.core.codec.Encoder;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.TimeUtils;
import com.rr.core.lang.ZString;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Event;

public class DummyEncoder implements Encoder {

    private Logger _log = LoggerFactory.create( DummyEncoder.class );

    @Override public void addStats( final ReusableString outBuf, final Event msg, final long time ) {
        /* nothing */
    }

    @Override public void encode( final Event msg ) {
        _log.warn( "DummyEncoder ignore encode of " + msg.toString() );
    }

    @Override public byte[] getBytes() {
        return new byte[ 0 ];
    }

    @Override public int getLength() {
        return 0;
    }

    @Override public int getOffset() {
        return 0;
    }

    @Override public void setNanoStats( final boolean nanoTiming ) {
        /* nothing */
    }

    @Override public void setTimeUtils( final TimeUtils calc ) {
        /* nothing */
    }

    @Override public Event unableToSend( final Event msg, final ZString errMsg ) {
        return null;
    }

    @Override public String getComponentId() { return "DummyEncoder"; }
}
