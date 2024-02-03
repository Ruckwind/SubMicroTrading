/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package memmap;

import com.rr.core.lang.ViewString;
import com.rr.core.persister.memmap.TestMemMapPersister;

public class StressTestMemMapPersister extends TestMemMapPersister {

    @Override protected ViewString getFileNameBase() {
        return new ViewString( "./tmp/StressTestMemMapPersister" );
    }

    @Override protected int getFilePreSize()  { return 1000000; }

    @Override protected int getPageSize()     { return 2048; }

    @Override protected int getUnmapNumRecs() { return 1000000; }
}
