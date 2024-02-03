/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package memmap;

import com.rr.core.lang.ViewString;
import com.rr.core.persister.memmap.TestIndexedMemMapPersister;

public class StressTestIndexedMemMapPersister extends TestIndexedMemMapPersister {

    @Override protected ViewString getFileNameBase() {
        return new ViewString( "./tmp/StressTestIndexMemMapPersister" );
    }

    @Override protected int getFilePreSize()  { return 100000; }

    @Override protected int getPageSize()     { return 2048; }

    @Override protected int getUnmapNumRecs() { return 100; }
}
