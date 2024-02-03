/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.us.cme;

import com.rr.core.codec.RuntimeDecodingException;
import com.rr.core.codec.binary.fastfix.FastFixDecoder;
import com.rr.core.codec.binary.fastfix.FastFixEncoder;
import com.rr.core.codec.binary.fastfix.NonBlockingFastFixSocketSession;
import com.rr.core.collections.EventQueue;
import com.rr.core.lang.ReusableString;
import com.rr.core.model.Event;
import com.rr.core.pool.SuperPool;
import com.rr.core.pool.SuperpoolManager;
import com.rr.core.session.*;
import com.rr.core.session.socket.SocketConfig;
import com.rr.core.utils.HexUtils;
import com.rr.core.utils.Utils;
import com.rr.md.channel.MktDataChannel;
import com.rr.md.fastfix.DummyFastFixEncoder;
import com.rr.md.fastfix.DummyQueue;
import com.rr.md.fastfix.FastSocketConfig;
import com.rr.md.us.cme.reader.CMEFastFixDecoder;
import com.rr.model.generated.internal.events.factory.MsgSeqNumGapFactory;
import com.rr.model.generated.internal.events.impl.MsgSeqNumGapImpl;

/**
 * @WARN CHANGES TO THIS CLASS SHOULD BE CHECKED AGAINST CMEFastFixSession
 */

public final class CMENonBlockingFastFixSession extends NonBlockingFastFixSocketSession implements MktDataChannel<Integer> {

    private static final int MIN_BYTES = 30;

    private final SuperPool<MsgSeqNumGapImpl> _gapPool    = SuperpoolManager.instance().getSuperPool( MsgSeqNumGapImpl.class );
    private final MsgSeqNumGapFactory         _gapFactory = new MsgSeqNumGapFactory( _gapPool );

    private final ReusableString _binMsg      = new ReusableString();
    private final int _initialBytesToRead;
    private       Integer[]      _channelKeys = new Integer[ 0 ];
    private int _inMsgLen;                                   // length of current inbound message
    private int _lastSeqNum;
    private int _dups;
    private int _gaps;

    public CMENonBlockingFastFixSession( String name,
                                         EventRouter inboundRouter,
                                         SocketConfig config,
                                         MultiSessionDispatcher dispatcher,
                                         MultiSessionReceiver receiver,
                                         FastFixEncoder encoder,
                                         FastFixDecoder decoder,
                                         EventQueue dispatchQueue ) {

        super( name, inboundRouter, config, dispatcher, receiver, encoder, decoder, dispatchQueue );

        String msg = "00 00 00 00 00 C0 D3 80 28 2F 26 75 53 E4 80 82 2F 80 81 30 6A 80 0F 7F 7F 7F FE 81 FB 2F 58 22 B9 07 E8 8A 31 80 81 B1 FB 8A 80 80";

        HexUtils.hexStringToBytes( msg.getBytes(), 0, _binMsg );

        _initialBytesToRead = MIN_BYTES;
    }

    /**
     * CMENonBlockingFastFixSession for use by builder, uses dummies for encoding, just does decoding (as normal)
     */
    public CMENonBlockingFastFixSession( String name,
                                         EventRouter inboundRouter,
                                         FastSocketConfig config,
                                         MultiSessionThreadedReceiver multiplexReceiver,
                                         FastFixDecoder decoder ) {

        super( name,
               inboundRouter,
               config,
               new DummyMultiSessionDispatcher(),
               multiplexReceiver,
               new DummyFastFixEncoder(),
               decoder,
               new DummyQueue() );

        _initialBytesToRead = MIN_BYTES;
    }

    @Override
    public synchronized void addChannelKey( Integer channelKey ) {
        if ( !hasChannelKey( channelKey ) ) {
            _channelKeys = Utils.arrayCopyAndAddEntry( _channelKeys, channelKey );
        }
    }

    @Override
    public Integer[] getChannelKeys() {
        return _channelKeys;
    }

    @Override
    public boolean hasChannelKey( Integer channelKey ) {
        if ( _channelKeys == null ) return false;

        final int key = channelKey;

        for ( int i = 0; i < _channelKeys.length; ++i ) {
            if ( _channelKeys[ i ] == key ) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected final void finalLog( ReusableString msg ) {
        super.finalLog( msg );
        msg.append( ", dups=" + _dups + ", gaps=" + _gaps );
    }

    @Override
    public void logInboundDecodingError( RuntimeDecodingException e ) {
        logInboundError( e );
    }

    @Override
    protected final void persistIntegrityCheck( boolean inbound, long key, Event msg ) {
        // nothing
    }

    @Override
    public void logInboundError( Exception e ) {
        _logInErrMsg.copy( getComponentId() ).append( " lastSeqNum=" ).append( ((CMEFastFixDecoder) _decoder).getLastSeqNum() );
        _logInErrMsg.append( ' ' ).append( e.getMessage() );
        _log.error( ERR_IN_MSG, _logInErrMsg, e );
        ((FastFixDecoder) _decoder).logLastMsg();
    }

    @Override
    protected void dispatchMsgGap( int channelId, int lastSeqNum, int seqNum ) {
        MsgSeqNumGapImpl gap = _gapFactory.get();

        gap.setChannelId( channelId );
        gap.setMsgSeqNum( seqNum );
        gap.setPrevSeqNum( lastSeqNum );

        dispatchInbound( gap );
    }

    /**
     * fast fix messages are encoded and length is unknown
     */
    @Override
    public final void processNextInbound() throws Exception {

        final int preBuffered = prepareForReadMessage();

        int bytesRead = nonBlockingRead( preBuffered, _initialBytesToRead );

        if ( bytesRead < _initialBytesToRead ) {
            _inPreBuffered = bytesRead;
            return;
        }

        // at this point we have the packet and assume will contain whole message as UDP
        if ( _logStats ) _decoder.setReceived( Utils.nanoTime() );

        _inMsgLen = bytesRead;

        final int hdrLenPlusMsgLen = _inHdrLen + _inMsgLen;

        _inByteBuffer.position( _inHdrLen );

        if ( _stopping ) return;

        _inLogBuf.setLength( hdrLenPlusMsgLen );

        final int extraBytes = bytesRead - _inMsgLen;
        _inPreBuffered = extraBytes;                    // set preBuffered here incase decoder throws exception

        Event msg;

        msg = decode( _inHdrLen, bytesRead );

        logInEvent( null );

        // @TODO should check actual decode left and shift left / set prebuffered for remaining
        // CME only put 1 message per packet

        if ( msg != null ) {
            logInEventPojo( msg );

            invokeController( msg );
        }

        _inPreBuffered = 0;
    }

    private int getChannelId() {
        if ( _channelKeys.length == 1 ) {
            return _channelKeys[ 0 ];
        }
        return 0; // ALL channels
    }

    private void invokeController( Event msg ) {
        final int seqNum        = msg.getMsgSeqNum();
        final int nextExpSeqNum = _lastSeqNum + 1;

        if ( seqNum == nextExpSeqNum || _lastSeqNum == 0 ) {
            _lastSeqNum = seqNum;
        } else if ( seqNum > nextExpSeqNum ) {

            ++_gaps;

            final int subChannel = ((CMEFastFixDecoder) _decoder).getSubchannel();

            _logInErrMsg.reset();
            _logInErrMsg.append( "Gap detected " ).append( getComponentId() ).append( " last=" ).append( _lastSeqNum ).
                        append( ", gapSeq=" ).append( seqNum ).
                        append( ", subChannel=" ).append( subChannel );

            _log.info( _logInErrMsg );

            dispatchMsgGap( getChannelId(), _lastSeqNum, seqNum );

            _lastSeqNum = seqNum;
        } else if ( seqNum == _lastSeqNum ) { // DUP

            ++_dups;

            inboundRecycle( msg );

            return;
        }

        dispatchInbound( msg );
    }
}
