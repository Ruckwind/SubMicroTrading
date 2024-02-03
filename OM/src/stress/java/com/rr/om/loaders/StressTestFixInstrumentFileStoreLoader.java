package com.rr.om.loaders;

import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.utils.SMTException;
import org.junit.Test;

public class StressTestFixInstrumentFileStoreLoader extends TestFixInstrumentFileStoreLoader {

    private final static Logger _log = LoggerFactory.create( StressTestFixInstrumentFileStoreLoader.class );

    @Test
    public void stressTestConcurrent() throws SMTException {
        int numReaders      = 10;
        int numWriters      = 1;
        int readIterations  = 100;
        int writeIterations = 100;

        doTestConcurrent( numReaders, numWriters, readIterations, writeIterations );
    }
}
