/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.asia.bse.fastfix.reader;

import com.rr.core.codec.FixField;
import com.rr.core.codec.binary.fastfix.FastFixDecodeBuilder;
import com.rr.core.codec.binary.fastfix.FastFixDecoder;
import com.rr.core.codec.binary.fastfix.PresenceMapReader;
import com.rr.core.codec.binary.fastfix.common.ComponentFactory;
import com.rr.core.codec.binary.fastfix.fulldict.FullDictComponentFactory;
import com.rr.core.codec.binary.fastfix.fulldict.entry.DictEntry;
import com.rr.core.codec.binary.fastfix.msgdict.copy.int32.UIntMandReaderCopy;
import com.rr.core.collections.IntHashMap;
import com.rr.core.collections.IntMap;
import com.rr.core.lang.ErrorCode;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.TimeUtils;
import com.rr.core.lang.TimeUtilsFactory;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.ClientProfile;
import com.rr.core.model.Event;
import com.rr.core.model.ExchangeCode;
import com.rr.core.model.InstrumentLocator;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.core.utils.ShutdownManager;
import com.rr.md.asia.bse.fastfix.BSEPacketHeader;
import com.rr.md.fastfix.XMLFastFixTemplateLoader;
import com.rr.md.fastfix.meta.MetaTemplate;
import com.rr.md.fastfix.meta.MetaTemplates;
import com.rr.md.fastfix.template.FastFixTemplateClassRegister;
import com.rr.md.fastfix.template.TemplateClassRegister;
import com.rr.model.generated.fix.codec.MDBSEDecoder;
import com.rr.model.generated.internal.core.EventIds;
import com.rr.model.generated.internal.events.impl.MDIncRefreshImpl;
import com.rr.model.generated.internal.events.impl.MDSnapshotFullRefreshImpl;

import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;

public final class BSEFastFixDecoder implements FastFixDecoder {

    private static final ErrorCode ERR_FF1 = new ErrorCode( "BSEFF01", "BSE Decoding Error" );

    private static final int EMD_TEMPLATE_HEADER = 112;
    private static final int MDI_TEMPLATE_HEADER = 117;

    private static final int BSE_FAST_RESET = 120;

    private final MetaTemplates meta = new MetaTemplates();

    private final Logger _log = LoggerFactory.create( BSEFastFixDecoder.class );

    private final ComponentFactory _cf = new FullDictComponentFactory();

    private final IntMap<BSEFastFixToFixReader> _activeReaders   = new IntHashMap<>( 200, 0.75f );
    private final IntMap<BSEFastFixToFixReader> _preparedReaders = new IntHashMap<>( 200, 0.75f );
    private final BSEPacketHeader _curPacketHeader = new BSEPacketHeader();
    private final byte[]    _today        = new byte[ TimeUtils.DATE_STR_LEN ];
    private final MDBSEDecoder _softDecoder = new MDBSEDecoder();
    private final FastFixDecodeBuilder _binDecodeBuilder = new FastFixDecodeBuilder();
    private final PresenceMapReader    _pMap             = new PresenceMapReader();
    private final ReusableString       _destFixMsg       = new ReusableString();
    private final UIntMandReaderCopy _templateIdReader = new UIntMandReaderCopy( "TemplateId", 0, 0 );
    private final BitSet  _subChannelOnMask;
    private final String  _id;
    private final boolean _logIntermediateFix;
    private DictEntry[] _dictionaryActiveEntryArray = new DictEntry[ 0 ];
    private boolean _debug;
    private       TimeUtils _tzCalculator = TimeUtilsFactory.createTimeUtils();
    private int _templateId = 0;

    private long _receivedTS;

    // stats
    private int _dups            = 0;
    private int _unknown         = 0;
    private int _packets         = 0;
    private int _subChannelSkips = 0;
    private int _handPackets;

    private boolean _logStats;

    private int _msgInPkt = 0;

    private BSEFastFixToFixReader _reader = null;

    public BSEFastFixDecoder( String id, String templateFile, BitSet subChannelOnMask, boolean debug, boolean logIntermediateFix ) {

        _logIntermediateFix = logIntermediateFix;

        _id    = id;
        _debug = debug;
        _tzCalculator.getToday( _today );
        _subChannelOnMask = subChannelOnMask;

        loadTemplates( templateFile );

        _softDecoder.setDefaultExchange( ExchangeCode.XBOM );

        Iterator<Integer> it = meta.templateIterator();

        while( it.hasNext() ) {
            Integer tid = it.next();

            preloadReader( tid ); // force create the reader
        }

        ShutdownManager.instance().register( "StatsLog" + _id, this::logStats, ShutdownManager.Priority.Medium );
    }

    @Override public String getComponentId() {
        return _id;
    }

    @Override
    public boolean isDebug() {
        return _debug;
    }

    @Override
    public void setDebug( boolean isDebugOn ) {
        _debug = isDebugOn;
    }    @Override
    public int parseHeader( byte[] inBuffer, int inHdrLen, int bytesRead ) {
        throw new SMTRuntimeException( "BSEFastFixDecoder doesnt use parseHeader as message variable length" );
    }

    @Override
    public void logStats() {

        _log.info( "BSEFastFixReader STATS id=" + _id +
                   ", handPackets=" + _handPackets +
                   ", packets=" + _packets +
                   ", unknown=" + _unknown +
                   ", dups=" + _dups +
                   ", subChannelSkips=" + _subChannelSkips );
    }    @Override
    public Event postHeaderDecode() {
        throw new SMTRuntimeException( "BSEFastFixDecoder doesnt use postHeaderDecode as message variable length" );
    }

    @Override
    public void logLastMsg() {
        logBinary( _log );
    }

    @Override
    public void setNextDummy() {
        // nothing
    }

    @Override public void setClientProfile( ClientProfile client )                    { /* nothing */ }

    public boolean decodeStartPacket( byte[] msg, int offset, int maxIdx ) {
        _binDecodeBuilder.start( msg, offset, maxIdx );

        _msgInPkt = 0;

        if ( !decodeHeader() ) return false;

        if ( !isSubscribedChannel( _curPacketHeader._partitionId ) ) {
            ++_subChannelSkips;
            return false;
        }

        return decodeFastResetMessage();
    }

    public BSEPacketHeader getCurPacketHeader() {
        return _curPacketHeader;
    }

    public int getCurrentOffset() {
        return _binDecodeBuilder.getCurrentIndex();
    }

    public void logError( Logger log, ReusableString errMsg, Exception e ) {
        errMsg.copy( _id );
        errMsg.append( "  DECODING EXCEPTION, templateId=" ).append( _templateId ).append( " : " ).append( e.getMessage() );
        log.error( ERR_FF1, errMsg, e );
        logBinary( log );
    }    /**
     * decode NEXT BSE fast fix message
     *
     * @WARNING application is responsible for gap fill checking
     */
    @Override
    public Event decode( byte[] msg, int offset, int maxIdx ) {
        if ( _binDecodeBuilder.getCurrentIndex() < _binDecodeBuilder.getMaxIdx() ) {

            ++_msgInPkt;

            _binDecodeBuilder.start( msg, offset, maxIdx );

            // have space so assume another message present

            _pMap.readMap( _binDecodeBuilder );

            _templateId = _templateIdReader.read( _binDecodeBuilder, _pMap );

            if ( _templateId == BSE_FAST_RESET ) {
                resetActiveFields();
            }

            // @TODO add hard coded generated templates

            //        Message m = null;
            //
            //        // HAND TEMPLATES - ENSURE SWITCH STATEMENT HAS FILLERS
            //
            //        if ( m != null ) {
            //            _binDecodeBuilder.end(); // only one message per packet in BSE
            //
            //            return m;
            //        }

            return processsSoftTemplateDecode( _templateId );
        }

        return null;
    }

    public void testStart( byte[] buf, int offset, int maxOffset ) {
        _binDecodeBuilder.start( buf, offset, maxOffset );
    }

    private boolean decodeFastResetMessage() {
        _pMap.readMap( _binDecodeBuilder );

        _templateId = _templateIdReader.read( _binDecodeBuilder, _pMap );

        if ( _templateId != BSE_FAST_RESET ) {
            _destFixMsg.copy( _id ).append( "  Expected fast reset template 120 on BSE not templateId=" ).append( _templateId );
            _log.warn( _destFixMsg );
            return false; // NOT SUPPORTED
        }

        _binDecodeBuilder.end();

        resetActiveFields();

        return true;
    }

    private boolean decodeHeader() {
        _pMap.readMap( _binDecodeBuilder );

        _templateId = _templateIdReader.read( _binDecodeBuilder, _pMap );

        if ( _templateId == EMD_TEMPLATE_HEADER ) {
            return procEMDHeader();
        }

        if ( _templateId == MDI_TEMPLATE_HEADER ) {
            return procMDIHeader();
        }

        logSKIPBinary( _log );
        _destFixMsg.copy( _id ).append( "  Only support packet header template 112/117 on BSE not templateId=" ).append( _templateId );
        _log.warn( _destFixMsg );

        return false;
    }

    private BSEFastFixToFixReader getReader( int templateId ) {

        if ( _reader != null && templateId == _reader.getId() ) {
            return _reader;
        }

        MetaTemplate mt = meta.getTemplate( templateId );
        if ( mt == null ) return null;

        BSEFastFixToFixReader reader = _activeReaders.get( templateId );

        if ( reader == null ) {
            reader = preloadReader( templateId );

            _log.warn( "BSEFastFixReader using softTemplate " + templateId );

            Collection<DictEntry> entries = _cf.getDictEntries();
            _dictionaryActiveEntryArray = new DictEntry[ entries.size() ];
            entries.toArray( _dictionaryActiveEntryArray );

            _activeReaders.put( templateId, reader );
        }

        return reader;
    }

    private boolean isSubscribedChannel( int partitionId ) {
        return (_subChannelOnMask == null || _subChannelOnMask.get( partitionId ) == true);
    }

    private void loadTemplates( String templateFile ) {
        TemplateClassRegister reg = new FastFixTemplateClassRegister();

        String[] files = templateFile.split( "," );

        for ( String file : files ) {
            XMLFastFixTemplateLoader l = new XMLFastFixTemplateLoader( file );
            l.load( reg, meta );
        }
    }    @Override
    public void setReceived( long nanos ) {
        _receivedTS = nanos;
    }

    private void logBinary( Logger log ) {
        _destFixMsg.copy( _id );
        _destFixMsg.append( "  IN  [" ).append( _curPacketHeader._packetSeqNum ).append( " / " ).append( _msgInPkt )
                   .append( "] [s#" ).append( _curPacketHeader._partitionId )
                   .append( "] [p#" ).append( _curPacketHeader._perfIndicator )
                   .append( "] [t#" ).append( _templateId ).append( "] " );

        final int len = _binDecodeBuilder.getMaxIdx() - _binDecodeBuilder.getOffset();

        if ( len > 0 ) {
            _destFixMsg.appendHEX( _binDecodeBuilder.getBuffer(), _binDecodeBuilder.getOffset(), len );
        }

        log.info( _destFixMsg );
    }    @Override
    public long getReceived() {
        return _receivedTS;
    }

    private void logSKIPBinary( Logger log ) {
        _destFixMsg.copy( _id );
        _destFixMsg.append( "  SKIP PACKET : " );

        final int len = _binDecodeBuilder.getMaxIdx() - _binDecodeBuilder.getOffset();

        if ( len > 0 ) {
            _destFixMsg.appendHEX( _binDecodeBuilder.getBuffer(), _binDecodeBuilder.getOffset(), len );
        }

        log.info( _destFixMsg );
    }    @Override
    public void setTimeUtils( TimeUtils calc ) {
        _tzCalculator = calc;
    }

    private BSEFastFixToFixReader preloadReader( int templateId ) {
        MetaTemplate mt = meta.getTemplate( templateId );
        if ( mt == null ) return null;

        BSEFastFixToFixReader reader = _preparedReaders.get( templateId );

        if ( reader == null ) {
            reader = new BSEFastFixToFixReader( mt, "T" + templateId, templateId, FixField.FIELD_DELIMITER );

            _preparedReaders.put( templateId, reader );

            reader.init( _cf );
        }

        return reader;
    }

    private boolean procEMDHeader() {
        _curPacketHeader._partitionId = _binDecodeBuilder.decodeMandUInt();

        if ( _curPacketHeader._partitionId > 255 ) {
            logSKIPBinary( _log );
            _destFixMsg.copy( _id ).append( "  Only support packet header partitionId < 256, not " ).append( _curPacketHeader._partitionId );
            _log.warn( _destFixMsg );
            return false; // NOT SUPPORTED
        }

        _curPacketHeader._senderCompId = _binDecodeBuilder.decodeMandUInt();

        /**
         * dont use the ByteVector decoders as they will copy the bytes into temp arrays
         * and we know the format
         */

        final int lenPacketSeqNumVector = _binDecodeBuilder.decodeMandInt();
        if ( lenPacketSeqNumVector != 4 ) {
            logSKIPBinary( _log );
            _destFixMsg.copy( _id ).append( "  Expected ByteVector of len 4 for PacketSeqNum not " ).append( lenPacketSeqNumVector );
            _log.warn( _destFixMsg );
            return false; // NOT SUPPORTED
        }
        _curPacketHeader._packetSeqNum = _binDecodeBuilder.decodeSeqNum();

        final int lenSendingTime = _binDecodeBuilder.decodeMandInt();
        if ( lenSendingTime != 8 ) {
            logSKIPBinary( _log );
            _destFixMsg.copy( _id ).append( "  Expected ByteVector of len 8 for SendingTime not " ).append( lenSendingTime );
            _log.warn( _destFixMsg );
            return false; // NOT SUPPORTED
        }
        _curPacketHeader._sendingTime = _binDecodeBuilder.decodeLargeSeqNum();

        final int lenPerfIndicator = _binDecodeBuilder.decodeMandInt();
        if ( lenPerfIndicator != 4 ) {
            logSKIPBinary( _log );
            _destFixMsg.copy( _id ).append( "  Expected ByteVector of len 4 for PerfIndicator not " ).append( lenPerfIndicator );
            _log.warn( _destFixMsg );
            return false; // NOT SUPPORTED
        }
        _curPacketHeader._perfIndicator = _binDecodeBuilder.decodeSeqNum();

        return true;
    }

    private boolean procMDIHeader() {
        _curPacketHeader._partitionId = 0;

        _curPacketHeader._senderCompId = _binDecodeBuilder.decodeMandUInt();

        /**
         * dont use the ByteVector decoders as they will copy the bytes into temp arrays
         * and we know the format
         */

        final int lenPacketSeqNumVector = _binDecodeBuilder.decodeMandInt();
        if ( lenPacketSeqNumVector != 4 ) {
            logSKIPBinary( _log );
            _destFixMsg.copy( _id ).append( "  Expected ByteVector of len 4 for PacketSeqNum not " ).append( lenPacketSeqNumVector );
            _log.warn( _destFixMsg );
            return false; // NOT SUPPORTED
        }
        _curPacketHeader._packetSeqNum = _binDecodeBuilder.decodeSeqNum();

        final int lenSendingTime = _binDecodeBuilder.decodeMandInt();
        if ( lenSendingTime != 8 ) {
            logSKIPBinary( _log );
            _destFixMsg.copy( _id ).append( "  Expected ByteVector of len 8 for SendingTime not " ).append( lenSendingTime );
            _log.warn( _destFixMsg );
            return false; // NOT SUPPORTED
        }
        _curPacketHeader._sendingTime = _binDecodeBuilder.decodeLargeSeqNum();

        _curPacketHeader._perfIndicator = 0;

        return true;
    }    @Override public void setNanoStats( boolean nanoTiming ) {
        _logStats = nanoTiming;
    }

    private Event processsSoftTemplateDecode( int templateId ) {

        _destFixMsg.reset();

        if ( _logIntermediateFix ) {
            _destFixMsg.copy( _id );
            _destFixMsg.append( "  IN  [" ).append( _curPacketHeader._packetSeqNum ).append( " / " ).append( _msgInPkt )
                       .append( "] [s#" ).append( _curPacketHeader._partitionId )
                       .append( "] [t#" ).append( templateId ).append( "] " );
        }

        _reader = getReader( templateId );

        if ( _reader != null ) {

            final int offset = _destFixMsg.length();

            _reader.read( _binDecodeBuilder, _pMap, _destFixMsg );

            _binDecodeBuilder.end(); // end of current message

            ++_packets;

            if ( _logIntermediateFix ) {
                _log.info( _destFixMsg ); // logger the intermediate readable fix message
            }

            Event m = _softDecoder.decode( _destFixMsg.getBytes(), offset, _destFixMsg.length() );

            if ( _logStats ) {
                switch( m.getReusableType().getSubId() ) {
                case EventIds.ID_MDINCREFRESH:
                    MDIncRefreshImpl inc = (MDIncRefreshImpl) m;
                    inc.setReceived( _receivedTS );
                    break;
                case EventIds.ID_MDSNAPSHOTFULLREFRESH:
                    MDSnapshotFullRefreshImpl snap = (MDSnapshotFullRefreshImpl) m;
                    snap.setReceived( _receivedTS );
                    break;
                }
            }

            return m;
        }

        if ( _debug ) {
            _destFixMsg.copy( _id ).append( " SKIPPING templateId=" ).append( templateId );
            _log.info( _destFixMsg );
        }

        ++_unknown;

        return null;
    }    @Override public void setInstrumentLocator( InstrumentLocator instrumentLocator ) { /* nothing */ }

    /**
     * dont reset all fields just those required
     */
    private void resetActiveFields() {
        for ( int i = 0; i < _dictionaryActiveEntryArray.length; i++ ) {
            _dictionaryActiveEntryArray[ i ].reset();
        }
    }    @Override public InstrumentLocator getInstrumentLocator()                         { return null; }



    @Override public ResyncCode resync( byte[] fixMsg, int offset, int maxIdx )       { return null; }

    @Override public int getSkipBytes()                                               { return 0; }







    @Override
    public int getLength() {
        return _binDecodeBuilder.getLength();
    }










}
