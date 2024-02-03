/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.us.cme.reader;

import com.rr.core.codec.binary.fastfix.PresenceMapReader;
import com.rr.core.codec.binary.fastfix.msgdict.copy.int32.UIntMandReaderCopy;
import com.rr.core.utils.HexUtils;
import com.rr.md.fastfix.template.MDIncRefreshFastFixTemplateReader;
import com.rr.md.fastfix.template.MDIncRefreshFastFixTemplateWriter;
import com.rr.md.us.cme.writer.MDIncRefresh_84_Writer;
import com.rr.model.generated.internal.events.impl.MDIncRefreshImpl;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MDIncRefresh_84Test extends BaseMDIncRefreshTst {

    private final UIntMandReaderCopy _templateIdReader = new UIntMandReaderCopy( "TemplateId", 0, 0 );

    @Override
    protected MDIncRefreshFastFixTemplateReader makeReader() {
        return new MDIncRefresh_84_Reader( cf, name.getMethodName(), 84 );
    }

    @Override
    protected MDIncRefreshFastFixTemplateWriter makeWriter() {
        return new MDIncRefresh_84_Writer( cf, name.getMethodName(), 84 );
    }

    @Test
    public void testFailed84() {

        String hexMsg =
                "C0 D4 01 5A 17 96 23 5E 6C 66 4B 62 20 84 09 4C 06 D3 93 18 B0 01 53 BD 02 3B 15 AD 08 4F 90 00 F0 CE 0E 33 C1 3F 2C 23" +
                "B0 82 00 98 80 7F 94 80 84 82 80 00 98 80 81 80 84 82 80 00 98 80 FE 80 82 82 80 00 98 80 81 80 82 82 80 00 98 80 81 80 82 82 80 00 98" +
                "80 81 80 82 82 80 00 98 80 81 80 82 82 80 00 98 80 81 80 82 82 80 00 98 80 81 80 82 82 80 00 98 80 81 80 82 82 80 00 98 80 81 80 82 82" +
                "80 00 98 80 81 80 82 82 80 00 98 80 85 80 86 82 80 00 98 80 FC 80 82 82 80 00 98 80 86 80 87 82 80 00 98 80 FE 80 85 82 80 00 98 80 FD" +
                "80 82 82 80 00 98 80 81 80 82 82 80";

        byte[] binaryMsg = HexUtils.hexStringToBytes( hexMsg );
        byte[] buf       = new byte[ 8192 ];
        System.arraycopy( binaryMsg, 0, buf, 0, binaryMsg.length );

        PresenceMapReader pMapIn = new PresenceMapReader();

        MDIncRefreshImpl last;

        decoder.start( buf, 0, binaryMsg.length );
        pMapIn.readMap( decoder );
        int templateId = _templateIdReader.read( decoder, pMapIn );
        assertEquals( 84, templateId );

        last = reader.read( decoder, pMapIn );

        assertEquals( 3574678, last.getMsgSeqNum() );
        assertEquals( 19, last.getNoMDEntries() );
    }
}
