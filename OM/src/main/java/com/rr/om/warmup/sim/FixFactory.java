/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.warmup.sim;

import com.rr.core.codec.FixDecoder;
import com.rr.core.codec.FixEncoder;
import com.rr.core.lang.ErrorCode;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.model.FixVersion;
import com.rr.model.generated.fix.codec.*;

// @TODO generate this from model

public class FixFactory {

    private static final Logger _log = ConsoleFactory.console( FixFactory.class, Level.WARN );

    private static final ErrorCode UNSUPPORTED_FIX = new ErrorCode( "FIF100", "Fix Generator currently only generates fix 4.4" );

    public static FixDecoder createFixFullDecoder( FixVersion ver ) {

        switch( ver ) {
        case Fix4_4:
            return new Standard44DecoderFull( ver._major, ver._minor );
        case DCFix4_4:
            return new DropCopy44DecoderFull( ver._major, ver._minor );
        case MDFix4_4:
            return new MD44Decoder( ver._major, ver._minor );
        case MDFix5_0:
            return new MD50Decoder( ver._major, ver._minor );
        case Fix4_2:
            return new Standard42DecoderFull( ver._major, ver._minor );
        case Fix5_0:
            return new Standard50DecoderFull( ver._major, ver._minor );
        case Fix4_0:
        case Fix4_1:
            _log.error( UNSUPPORTED_FIX, ver.toString() );

            return new Standard44DecoderFull( ver._major, ver._minor );
        default:
            break;
        }

        return null;
    }

    public static FixEncoder createFixEncoder( FixVersion ver, byte[] buf, int offset ) {

        switch( ver ) {
        case Fix5_0:
            return new Standard50Encoder( ver._major, ver._minor, buf, offset );
        case Fix4_4:
            return new Standard44Encoder( ver._major, ver._minor, buf, offset );
        case Fix4_2:
            return new Standard42Encoder( ver._major, ver._minor, buf, offset );
        case DCFix4_4:
            return new DropCopy44Encoder( ver._major, ver._minor, buf, offset );
        case MDFix4_4:
            return new MD44Encoder( ver._major, ver._minor, buf, offset );
        case MDFix5_0:
            return new MD50Encoder( ver._major, ver._minor, buf, offset );
        case Fix4_0:
        case Fix4_1:
            _log.error( UNSUPPORTED_FIX, ver.toString() );

            return new Standard44Encoder( ver._major, ver._minor, buf, offset );
        }

        return null;
    }

    public static FixDecoder createFixOMSDecoder( FixVersion ver ) {

        switch( ver ) {
        case Fix5_0:
            return new Standard50DecoderOMS( ver._major, ver._minor );
        case Fix4_4:
            return new Standard44DecoderOMS( ver._major, ver._minor );
        case Fix4_2:
            return new Standard42DecoderOMS( ver._major, ver._minor );
        case DCFix4_4:
            return new DropCopy44DecoderOMS( ver._major, ver._minor );
        case MDFix4_4:
            return new MD44Decoder( ver._major, ver._minor );
        case MDFix5_0:
            return new MD50Decoder( ver._major, ver._minor );
        case Fix4_0:
        case Fix4_1:
            _log.error( UNSUPPORTED_FIX, ver.toString() );

            return new Standard44DecoderOMS( ver._major, ver._minor );
        }

        return null;
    }

}
