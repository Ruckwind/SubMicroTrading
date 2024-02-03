/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.loaders;

import com.rr.core.codec.Decoder;
import com.rr.core.component.SMTComponent;
import com.rr.core.component.SMTSingleComponentLoader;
import com.rr.core.lang.ZConsumer;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Event;
import com.rr.core.session.EventConsumerRouter;
import com.rr.core.session.EventRouter;
import com.rr.core.session.file.FileSessionConfig;
import com.rr.core.session.file.TextFileSession;

public class TextFileSessionLoader implements SMTSingleComponentLoader {

    private static final Logger _log = LoggerFactory.create( TextFileSessionLoader.class );

    private FileSessionConfig _config;
    private Decoder           _decoder;
    private EventRouter       _inboundRouter;

    public TextFileSessionLoader() {
    }

    public TextFileSessionLoader( String id, String rootDir, String filePtn, Decoder decoder, ZConsumer<Event> consumer ) {
        _config = new FileSessionConfig( id + "Cfg" );
        _config.setPathRoots( rootDir );
        _config.setPatternMatch( filePtn );

        _decoder = decoder;

        _inboundRouter = new EventConsumerRouter( id + "Router", consumer );
    }

    @Override
    public SMTComponent create( String id ) {
        return createFileSession( id );
    }

    public TextFileSession createFileSession( final String id ) {
        TextFileSession session;

        session = new TextFileSession( id, _inboundRouter, _config, _decoder );

        _log.info( "NonBlockingTextFileSession created for " + id );

        return session;
    }

    public FileSessionConfig getConfig() {
        return _config;
    }

    public void setConfig( final FileSessionConfig config ) {
        _config = config;
    }

    public Decoder getDecoder() {
        return _decoder;
    }

    public void setDecoder( final Decoder decoder ) {
        _decoder = decoder;
    }

    public EventRouter getInboundRouter() {
        return _inboundRouter;
    }

    public void setInboundRouter( final EventRouter inboundRouter ) {
        _inboundRouter = inboundRouter;
    }
}
