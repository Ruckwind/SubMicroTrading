/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.us.cme.reader;

import com.rr.md.fastfix.template.MDIncRefreshFastFixTemplateReader;
import com.rr.md.fastfix.template.MDIncRefreshFastFixTemplateWriter;
import com.rr.md.us.cme.writer.MDIncRefresh_81_Writer;
import com.rr.model.generated.internal.events.impl.MDEntryImpl;

import static org.junit.Assert.assertEquals;

public class MDIncRefresh_81Test extends BaseMDIncRefreshTst {

    @Override
    protected MDIncRefreshFastFixTemplateReader makeReader() {
        return new MDIncRefresh_81_Reader( cf, name.getMethodName(), 81 );
    }

    @Override
    protected MDIncRefreshFastFixTemplateWriter makeWriter() {
        return new MDIncRefresh_81_Writer( cf, name.getMethodName(), 81 );
    }

    @Override
    protected void checkMDEntry( MDEntryImpl expEntry, MDEntryImpl decodedEntry ) {
        super.checkMDEntry( expEntry, decodedEntry );

        assertEquals( expEntry.getMdPriceLevel(), decodedEntry.getMdPriceLevel() );
        assertEquals( expEntry.getNumberOfOrders(), decodedEntry.getNumberOfOrders() );
    }
}
