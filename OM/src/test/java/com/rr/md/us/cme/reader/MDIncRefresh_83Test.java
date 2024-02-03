/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.us.cme.reader;

import com.rr.md.fastfix.template.MDIncRefreshFastFixTemplateReader;
import com.rr.md.fastfix.template.MDIncRefreshFastFixTemplateWriter;
import com.rr.md.us.cme.writer.MDIncRefresh_83_Writer;

public class MDIncRefresh_83Test extends BaseMDIncRefreshTst {

    @Override
    protected MDIncRefreshFastFixTemplateReader makeReader() {
        return new MDIncRefresh_83_Reader( cf, name.getMethodName(), 83 );
    }

    @Override
    protected MDIncRefreshFastFixTemplateWriter makeWriter() {
        return new MDIncRefresh_83_Writer( cf, name.getMethodName(), 83 );
    }
}
