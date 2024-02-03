package com.rr.core.pubsub.codecs;

import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTStartContext;
import com.rr.core.model.Event;

import java.io.Closeable;

public interface SMTExporter extends Closeable {

    String getExportId();

    void init( SMTStartContext ctx, CreationPhase creationPhase );

    void setBlockSize( int bufSize );

    void write( Event obj ) throws Exception;
}
