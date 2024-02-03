/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.us.cme.reader;

import com.rr.core.codec.binary.fastfix.PresenceMapReader;
import com.rr.core.utils.HexUtils;
import com.rr.md.fastfix.template.MDIncRefreshFastFixTemplateReader;
import com.rr.md.fastfix.template.MDIncRefreshFastFixTemplateWriter;
import com.rr.md.us.cme.writer.MDIncRefresh_103_Writer;
import com.rr.model.generated.internal.events.impl.MDIncRefreshImpl;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MDIncRefresh_103Test extends BaseMDIncRefreshTst {

    @Override
    protected MDIncRefreshFastFixTemplateReader makeReader() {
        return new MDIncRefresh_103_Reader( cf, name.getMethodName(), 103 );
    }

    @Override
    protected MDIncRefreshFastFixTemplateWriter makeWriter() {
        return new MDIncRefresh_103_Writer( cf, name.getMethodName(), 103 );
    }

    @Test
    public void testFailed103() {

        String hexMsg =
                "C0 E7 01 5A 33 F9 23 5E 6C 66 4B 69 07 AD 09 4C 06 D3 83 76 78 C0 80 80 B2 80 01 53 BD 02 3B 3F FE 08 4E F7 3F 33 06 C8 84 80 80 81 7F " +
                "B5 0E 4B 85 83 20 38 E0 80 80 80 83 80 80 81 80 0E 4B 8A 83 80 50 00 A0 81 B0 80 80 02 E9 00 E7 80 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00";

        byte[] binaryMsg = HexUtils.hexStringToBytes( hexMsg );
        byte[] buf       = new byte[ 8192 ];
        System.arraycopy( binaryMsg, 0, buf, 0, binaryMsg.length );

        PresenceMapReader pMapIn = new PresenceMapReader();

        MDIncRefreshImpl last;

        decoder.start( buf, 0, binaryMsg.length );
        pMapIn.readMap( decoder );

        last = reader.read( decoder, pMapIn );

        assertEquals( 103, last.getMsgSeqNum() );
        assertEquals( 3, last.getNoMDEntries() );
    }
}
