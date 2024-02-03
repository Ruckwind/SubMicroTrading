package com.rr.model.generated.internal.events.impl;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.interfaces.TickUpdate;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;
import com.rr.model.internal.type.*;
import com.rr.model.generated.internal.core.ModelReusableTypes;
import com.rr.model.generated.internal.core.SizeType;
import com.rr.model.generated.internal.core.EventIds;
import com.rr.model.generated.internal.events.interfaces.*;

@SuppressWarnings( { "unused", "override"  })

public final class MDUpdateImpl implements BaseMDResponse, MDUpdateWrite, Copyable<MDUpdate>, Reusable<MDUpdateImpl> {

   // Attrs

    private transient          MDUpdateImpl _next = null;
    private transient volatile Event        _nextMessage    = null;
    private transient          EventHandler _messageHandler = null;
    private final ReusableString _mdReqId = new ReusableString( SizeType.MD_REQ_LEN.getSize() );
    private int _noMDEntries = Constants.UNSET_INT;
    private int _msgSeqNum = Constants.UNSET_INT;
    @TimestampMS private long _eventTimestamp = Constants.UNSET_LONG;

    private Book _book;
    private TickUpdate _tickUpdates;

    private int           _flags          = 0;

   // Getters and Setters
    @Override public final ViewString getMdReqId() { return _mdReqId; }

    @Override public final void setMdReqId( byte[] buf, int offset, int len ) { _mdReqId.setValue( buf, offset, len ); }
    @Override public final ReusableString getMdReqIdForUpdate() { return _mdReqId; }

    @Override public final Book getBook() { return _book; }
    @Override public final void setBook( Book val ) { _book = val; }

    @Override public final int getNoMDEntries() { return _noMDEntries; }
    @Override public final void setNoMDEntries( int val ) { _noMDEntries = val; }

    @Override public final TickUpdate getTickUpdates() { return _tickUpdates; }
    @Override public final void setTickUpdates( TickUpdate val ) { _tickUpdates = val; }

    @Override public final int getMsgSeqNum() { return _msgSeqNum; }
    @Override public final void setMsgSeqNum( int val ) { _msgSeqNum = val; }

    @Override public final long getEventTimestamp() { return _eventTimestamp; }
    @Override public final void setEventTimestamp( long val ) { _eventTimestamp = val; }


    @Override public final boolean getPossDupFlag() { return isFlagSet( MsgFlag.PossDupFlag ); }
    @Override public final void setPossDupFlag( boolean val ) { setFlag( MsgFlag.PossDupFlag, val ); }

   // Reusable Contract

    @Override
    public final void reset() {
        _mdReqId.reset();
        _book = null;
        _noMDEntries = Constants.UNSET_INT;
        _tickUpdates = null;
        _msgSeqNum = Constants.UNSET_INT;
        _eventTimestamp = Constants.UNSET_LONG;
        _flags = 0;
        _next = null;
        _nextMessage = null;
        _messageHandler = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.MDUpdate;
    }

    @Override
    public final MDUpdateImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( MDUpdateImpl nxt ) {
        _next = nxt;
    }

    @Override
    public final void detachQueue() {
        _nextMessage = null;
    }

    @Override
    public final Event getNextQueueEntry() {
        return _nextMessage;
    }

    @Override
    public final void attachQueue( Event nxt ) {
        _nextMessage = nxt;
    }

    @Override
    public final EventHandler getEventHandler() {
        return _messageHandler;
    }

    @Override
    public final void setEventHandler( EventHandler handler ) {
        _messageHandler = handler;
    }


   // Helper methods
    @Override
    public void setFlag( MsgFlag flag, boolean isOn ) {
        _flags = MsgFlag.setFlag( _flags, flag, isOn );
    }

    @Override
    public boolean isFlagSet( MsgFlag flag ) {
        return MsgFlag.isOn( _flags, flag );
    }

    @Override
    public int getFlags() {
        return _flags;
    }

    @Override
    public String toString() {
        ReusableString buf = TLC.instance().pop();
        dump( buf );
        String rs = buf.toString();
        TLC.instance().pushback( buf );
        return rs;
    }

    @Override
    public final void dump( final ReusableString out ) {
        out.append( "MDUpdateImpl" ).append( ' ' );
        if ( getMdReqId().length() > 0 )             out.append( ", mdReqId=" ).append( getMdReqId() );
        if ( getBook() != null )             out.append( ", book=" );
        if ( getBook() != null ) out.append( getBook().id() );
        if ( Constants.UNSET_INT != getNoMDEntries() && 0 != getNoMDEntries() )             out.append( ", noMDEntries=" ).append( getNoMDEntries() );

        TickUpdateImpl tPtrtickUpdates = (TickUpdateImpl) getTickUpdates();
        int tIdxtickUpdates=0;

        while( tPtrtickUpdates != null ) {
            out.append( " {#" ).append( ++tIdxtickUpdates ).append( "} " );
            tPtrtickUpdates.dump( out );
            tPtrtickUpdates = tPtrtickUpdates.getNext();
        }

        if ( Constants.UNSET_INT != getMsgSeqNum() && 0 != getMsgSeqNum() )             out.append( ", msgSeqNum=" ).append( getMsgSeqNum() );
        out.append( ", possDupFlag=" ).append( getPossDupFlag() );
        if ( Constants.UNSET_LONG != getEventTimestamp() && 0 != getEventTimestamp() ) {
            out.append( ", eventTimestamp=" );
            TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( out, getEventTimestamp() );
            out.append( " / " );
            TimeUtilsFactory.safeTimeUtils().unixTimeToUTCTimestamp( out, getEventTimestamp() );
            out.append( " ( " );
            out.append( getEventTimestamp() ).append( " ) " );
        }
    }

    @Override public final void snapTo( MDUpdate dest ) {
        ((MDUpdateImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( MDUpdate src ) {
        getMdReqIdForUpdate().copy( src.getMdReqId() );
        setBook( src.getBook() );
        setNoMDEntries( src.getNoMDEntries() );
        TickUpdateImpl tSrcPtrTickUpdates = (TickUpdateImpl) src.getTickUpdates();
        TickUpdateImpl tNewPtrTickUpdates = null;
        while( tSrcPtrTickUpdates != null ) {
            if ( tNewPtrTickUpdates == null ) {
                tNewPtrTickUpdates = new TickUpdateImpl();
                setTickUpdates( tNewPtrTickUpdates );
            } else {
                tNewPtrTickUpdates.setNext( new TickUpdateImpl() );
                tNewPtrTickUpdates = tNewPtrTickUpdates.getNext();
            }
            tNewPtrTickUpdates.deepCopyFrom( tSrcPtrTickUpdates );
            tSrcPtrTickUpdates = tSrcPtrTickUpdates.getNext();
        }
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( MDUpdate src ) {
        getMdReqIdForUpdate().copy( src.getMdReqId() );
        setBook( src.getBook() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( MDUpdate src ) {
        if ( src.getMdReqId().length() > 0 ) getMdReqIdForUpdate().copy( src.getMdReqId() );
        if ( getBook() != null )  setBook( src.getBook() );
        if ( Constants.UNSET_INT != src.getMsgSeqNum() ) setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        if ( Constants.UNSET_LONG != src.getEventTimestamp() ) setEventTimestamp( src.getEventTimestamp() );
    }

}
