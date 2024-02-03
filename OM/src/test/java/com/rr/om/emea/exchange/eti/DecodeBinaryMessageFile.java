/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.eti;

import com.rr.core.codec.BinaryDecoder;
import com.rr.core.codec.CodecFactory;
import com.rr.core.dummy.warmup.TestStats;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.stats.StatsMgr;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Event;
import com.rr.core.utils.FileUtils;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.core.utils.Utils;
import com.rr.model.generated.fix.codec.CodecFactoryPopulator;
import com.rr.model.generated.fix.codec.CodecId;
import com.rr.om.exchange.CodecLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class DecodeBinaryMessageFile {

    private static Logger _log;

    public static void main( String args[] ) throws IOException {

        LoggerFactory.setForceConsole( true );
        StatsMgr.setStats( new TestStats() );

        _log = LoggerFactory.create( DecodeBinaryMessageFile.class );

        if ( args.length != 2 ) {
            System.err.println( "Usage DecodeBinaryMessageFile <codecId> <fileName>" );
            Utils.exit( 99 );
        }

        String idStr    = args[ 0 ];
        String fileName = args[ 1 ];

        CodecId codecId;

        try {
            codecId = Enum.valueOf( CodecId.class, idStr );
        } catch( Exception e ) {
            throw new SMTRuntimeException( "Bad CodecId " + idStr + " not in " + CodecId.class.getCanonicalName() );
        }

        CodecFactory          codecFactory = new CodecFactory();
        CodecFactoryPopulator pop          = new CodecLoader();
        pop.register( codecFactory );

        BinaryDecoder decoder = (BinaryDecoder) codecFactory.getOMSDecoder( codecId );

        decoder.setDebug( true );

        File file = new File( fileName );

        byte[] inBuf = new byte[ 8192 ];

        FileInputStream stream = new FileInputStream( file );

        try {
            int bytesRead;
            int offset = 0;
            while( (bytesRead = stream.read( inBuf, offset, inBuf.length )) != -1 ) {
                int leftOverBytes = procBytes( decoder, bytesRead, inBuf );

                if ( leftOverBytes < 0 ) {
                    break;
                }

                if ( leftOverBytes > 0 ) {
                    int copyFrom = bytesRead - leftOverBytes;
                    System.arraycopy( inBuf, 0, inBuf, copyFrom, leftOverBytes ); // dont need zero out bytes out on the right
                }

                offset = leftOverBytes;
            }
        } finally {
            FileUtils.close( stream );
        }

        System.out.println( "FINISHED" );
    }

    private static int procBytes( BinaryDecoder decoder, int bytesRead, byte[] inBuf ) {
        int offset = 0;

        int remainingBytes = bytesRead - offset;

        ReusableString dump = new ReusableString();

        while( remainingBytes > 0 ) {

            int expLen = decoder.parseHeader( inBuf, offset, bytesRead );

            if ( expLen <= 0 ) {
                return -1;
            }

            if ( expLen > remainingBytes ) {
                return remainingBytes;
            }

            Event msg = decoder.postHeaderDecode();

            msg.dump( dump );

            _log.info( dump );

            offset += expLen;
            remainingBytes = bytesRead - offset;
        }

        return 0;
    }
}
