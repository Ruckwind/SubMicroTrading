/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.generator;

import com.rr.core.logger.LoggerFactory;
import com.rr.core.utils.FileException;
import com.rr.core.utils.Utils;
import com.rr.model.base.Model;
import com.rr.model.xml.XMLHelper;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;
import java.util.Properties;

public class Generator {

    private static Logger _logger;

    private XMLHelper _helper;

    private Model _model;

    /**
     * @param args
     */
    public static void main( String[] args ) {

        initLogger();

        if ( args.length != 2 ) {
            usage();
        }

        String type = args[ 0 ].toLowerCase().trim();
        String file = args[ 1 ];

        try {
            Generator gen = new Generator( file );

            // TODO add encode/decoder include directory

            gen.parse();

            switch( type ) {
            case "all":
                gen.generateInternal();
                gen.generateFix();
                gen.generateBaseCodecs();
                gen.generateClients();
                gen.generateExchanges();
                gen.generateBinaryCodecs();
                gen.generateCodecFactory();
                break;
            case "internal":
                gen.generateInternal();
                break;
            case "fix":
                gen.generateFix();
                gen.generateCodecFactory();
                break;
            case "baseCodecs":
                gen.generateBaseCodecs();
                gen.generateCodecFactory();
                break;
            case "client":
                gen.generateClients();
                gen.generateCodecFactory();
                break;
            case "exchange":
                gen.generateExchanges();
                gen.generateCodecFactory();
                break;
            case "binary":
                gen.generateBinaryCodecs();
                gen.generateCodecFactory();
                break;
            default:
                usage();
                break;
            }
        } catch( Exception e ) {
            _logger.error( "Exception : " + e.getMessage(), e );
        }
    }

    private static void usage() {
        _logger.error( "Usage : Generator [all | internal | fix | baseCodecs | client | exchange] modelXmlFile" );
        Utils.exit( -1 );
    }

    private static void initLogger() {
        Properties cfg = new Properties();
        cfg.setProperty( "log4j.rootCategory", "INFO, CONSOLE" );
        cfg.setProperty( "log4j.appender.CONSOLE", "org.apache.log4j.ConsoleAppender" );
        cfg.setProperty( "log4j.appender.CONSOLE.Target", "System.out" );
        cfg.setProperty( "log4j.appender.CONSOLE.layout", "org.apache.log4j.PatternLayout" );
        cfg.setProperty( "log4j.appender.CONSOLE.layout.ConversionPattern", "%d{ABSOLUTE} %-5p [%c{1}] %m%n" );
        PropertyConfigurator.configure( cfg );

        _logger = Logger.getLogger( "Generator" );

        LoggerFactory.setForceConsole( true );
    }

    public Generator( String file ) {
        _helper = new XMLHelper( file );
    }

    private void abort() {
        _logger.error( "Aborting" );
        System.exit( -1 );
    }

    private void generateBaseCodecs() throws FileException, IOException {
        FixCodecDecoderGenerator decoder = new FixCodecDecoderGenerator( _model, _model.getCodec() );
        decoder.generate();

        FixCodecEncoderGenerator encoder = new FixCodecEncoderGenerator( _model, _model.getCodec() );
        encoder.generate();
    }

    private void generateBinaryCodecs() throws FileException, IOException {
        BinaryGenerator gen = new BinaryGenerator( _model );
        gen.generate();

        BinaryDecoderGenerator decoder = new BinaryDecoderGenerator( _model, _model.getBinaryCodecs() );
        decoder.generate();

        BinaryEncoderGenerator encoder = new BinaryEncoderGenerator( _model, _model.getBinaryCodecs() );
        encoder.generate();
    }

    private void generateClients() throws FileException, IOException {
        FixCodecDecoderGenerator decoder = new FixCodecDecoderGenerator( _model, _model.getClientCodecs() );
        decoder.generate();

        FixCodecEncoderGenerator encoder = new FixCodecEncoderGenerator( _model, _model.getClientCodecs() );
        encoder.generate();
    }

    private void generateCodecFactory() throws FileException, IOException {
        CodecFactoryGenerator gen = new CodecFactoryGenerator( _model );
        gen.generate();
    }

    private void generateExchanges() throws FileException, IOException {
        FixCodecDecoderGenerator decoder = new FixCodecDecoderGenerator( _model, _model.getExchangeCodecs() );
        decoder.generate();

        FixCodecEncoderGenerator encoder = new FixCodecEncoderGenerator( _model, _model.getExchangeCodecs() );
        encoder.generate();
    }

    private void generateFix() throws FileException, IOException {
        FixGenerator gen = new FixGenerator( _model );

        gen.generate();
    }

    private void generateInternal() throws Exception {
        InternalModelGenerator gen = new InternalModelGenerator( _model );

        gen.generate();
    }

    private void parse() {

        try {
            _helper.parse();

            XMLToModelBuilder builder = new XMLToModelBuilder( _helper );

            _model = builder.create();

        } catch( Exception e ) {

            _logger.error( e.getMessage(), e );

            abort();
        }
    }

}
