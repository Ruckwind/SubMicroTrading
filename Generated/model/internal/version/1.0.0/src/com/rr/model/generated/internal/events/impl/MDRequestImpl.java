package com.rr.model.generated.internal.events.impl;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.SubsReqType;
import com.rr.model.generated.internal.events.interfaces.SymbolRepeatingGrp;
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

public final class MDRequestImpl implements BaseMDRequest, MDRequestWrite, Copyable<MDRequest>, Reusable<MDRequestImpl> {

   // Attrs

    private transient          MDRequestImpl _next = null;
    private transient volatile Event        _nextMessage    = null;
    private transient          EventHandler _messageHandler = null;
    private final ReusableString _mdReqId = new ReusableString( SizeType.MD_REQ_LEN.getSize() );
    private int _marketDepth = Constants.UNSET_INT;
    private int _numRelatedSym = Constants.UNSET_INT;
    private int _msgSeqNum = Constants.UNSET_INT;
    @TimestampMS private long _eventTimestamp = Constants.UNSET_LONG;

    private SubsReqType _subsReqType;
    private SymbolRepeatingGrp _symbolGrp;

    private int           _flags          = 0;

   // Getters and Setters
    @Override public final ViewString getMdReqId() { return _mdReqId; }

    @Override public final void setMdReqId( byte[] buf, int offset, int len ) { _mdReqId.setValue( buf, offset, len ); }
    @Override public final ReusableString getMdReqIdForUpdate() { return _mdReqId; }

    @Override public final SubsReqType getSubsReqType() { return _subsReqType; }
    @Override public final void setSubsReqType( SubsReqType val ) { _subsReqType = val; }

    @Override public final int getMarketDepth() { return _marketDepth; }
    @Override public final void setMarketDepth( int val ) { _marketDepth = val; }

    @Override public final int getNumRelatedSym() { return _numRelatedSym; }
    @Override public final void setNumRelatedSym( int val ) { _numRelatedSym = val; }

    @Override public final SymbolRepeatingGrp getSymbolGrp() { return _symbolGrp; }
    @Override public final void setSymbolGrp( SymbolRepeatingGrp val ) { _symbolGrp = val; }

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
        _subsReqType = null;
        _marketDepth = Constants.UNSET_INT;
        _numRelatedSym = Constants.UNSET_INT;
        _symbolGrp = null;
        _msgSeqNum = Constants.UNSET_INT;
        _eventTimestamp = Constants.UNSET_LONG;
        _flags = 0;
        _next = null;
        _nextMessage = null;
        _messageHandler = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.MDRequest;
    }

    @Override
    public final MDRequestImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( MDRequestImpl nxt ) {
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
        out.append( "MDRequestImpl" ).append( ' ' );
        if ( getMdReqId().length() > 0 )             out.append( ", mdReqId=" ).append( getMdReqId() );
        if ( getSubsReqType() != null )             out.append( ", subsReqType=" ).append( getSubsReqType() );
        if ( Constants.UNSET_INT != getMarketDepth() && 0 != getMarketDepth() )             out.append( ", marketDepth=" ).append( getMarketDepth() );
        if ( Constants.UNSET_INT != getNumRelatedSym() && 0 != getNumRelatedSym() )             out.append( ", numRelatedSym=" ).append( getNumRelatedSym() );

        SymbolRepeatingGrpImpl tPtrsymbolGrp = (SymbolRepeatingGrpImpl) getSymbolGrp();
        int tIdxsymbolGrp=0;

        while( tPtrsymbolGrp != null ) {
            out.append( " {#" ).append( ++tIdxsymbolGrp ).append( "} " );
            tPtrsymbolGrp.dump( out );
            tPtrsymbolGrp = tPtrsymbolGrp.getNext();
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

    @Override public final void snapTo( MDRequest dest ) {
        ((MDRequestImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( MDRequest src ) {
        getMdReqIdForUpdate().copy( src.getMdReqId() );
        setSubsReqType( src.getSubsReqType() );
        setMarketDepth( src.getMarketDepth() );
        setNumRelatedSym( src.getNumRelatedSym() );
        SymbolRepeatingGrpImpl tSrcPtrSymbolGrp = (SymbolRepeatingGrpImpl) src.getSymbolGrp();
        SymbolRepeatingGrpImpl tNewPtrSymbolGrp = null;
        while( tSrcPtrSymbolGrp != null ) {
            if ( tNewPtrSymbolGrp == null ) {
                tNewPtrSymbolGrp = new SymbolRepeatingGrpImpl();
                setSymbolGrp( tNewPtrSymbolGrp );
            } else {
                tNewPtrSymbolGrp.setNext( new SymbolRepeatingGrpImpl() );
                tNewPtrSymbolGrp = tNewPtrSymbolGrp.getNext();
            }
            tNewPtrSymbolGrp.deepCopyFrom( tSrcPtrSymbolGrp );
            tSrcPtrSymbolGrp = tSrcPtrSymbolGrp.getNext();
        }
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( MDRequest src ) {
        getMdReqIdForUpdate().copy( src.getMdReqId() );
        setSubsReqType( src.getSubsReqType() );
        setMarketDepth( src.getMarketDepth() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( MDRequest src ) {
        if ( src.getMdReqId().length() > 0 ) getMdReqIdForUpdate().copy( src.getMdReqId() );
        setSubsReqType( src.getSubsReqType() );
        if ( Constants.UNSET_INT != src.getMarketDepth() ) setMarketDepth( src.getMarketDepth() );
        if ( Constants.UNSET_INT != src.getMsgSeqNum() ) setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        if ( Constants.UNSET_LONG != src.getEventTimestamp() ) setEventTimestamp( src.getEventTimestamp() );
    }

}
