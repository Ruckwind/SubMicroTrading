package com.rr.model.generated.internal.events.impl;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.interfaces.MDSnapEntry;
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

public final class MDSnapshotFullRefreshImpl implements BaseMDResponse, MDSnapshotFullRefreshWrite, Copyable<MDSnapshotFullRefresh>, Reusable<MDSnapshotFullRefreshImpl> {

   // Attrs

    private transient          MDSnapshotFullRefreshImpl _next = null;
    private transient volatile Event        _nextMessage    = null;
    private transient          EventHandler _messageHandler = null;
    private long _received = Constants.UNSET_LONG;
    private int _lastMsgSeqNumProcessed = Constants.UNSET_INT;
    private int _totNumReports = Constants.UNSET_INT;
    private int _rptSeq = Constants.UNSET_INT;
    private int _mdBookType = Constants.UNSET_INT;
    private final ReusableString _securityID = new ReusableString( SizeType.SYMBOL_LENGTH.getSize() );
    private int _mdSecurityTradingStatus = Constants.UNSET_INT;
    private int _noMDEntries = Constants.UNSET_INT;
    private int _msgSeqNum = Constants.UNSET_INT;
    @TimestampMS private long _eventTimestamp = Constants.UNSET_LONG;

    private SecurityIDSource _securityIDSource = SecurityIDSource.ExchangeSymbol;
    private ExchangeCode _securityExchange;
    private MDSnapEntry _MDEntries;

    private int           _flags          = 0;

   // Getters and Setters
    @Override public final long getReceived() { return _received; }
    @Override public final void setReceived( long val ) { _received = val; }

    @Override public final int getLastMsgSeqNumProcessed() { return _lastMsgSeqNumProcessed; }
    @Override public final void setLastMsgSeqNumProcessed( int val ) { _lastMsgSeqNumProcessed = val; }

    @Override public final int getTotNumReports() { return _totNumReports; }
    @Override public final void setTotNumReports( int val ) { _totNumReports = val; }

    @Override public final int getRptSeq() { return _rptSeq; }
    @Override public final void setRptSeq( int val ) { _rptSeq = val; }

    @Override public final int getMdBookType() { return _mdBookType; }
    @Override public final void setMdBookType( int val ) { _mdBookType = val; }

    @Override public final SecurityIDSource getSecurityIDSource() { return _securityIDSource; }
    @Override public final void setSecurityIDSource( SecurityIDSource val ) { _securityIDSource = val; }

    @Override public final ViewString getSecurityID() { return _securityID; }

    @Override public final void setSecurityID( byte[] buf, int offset, int len ) { _securityID.setValue( buf, offset, len ); }
    @Override public final ReusableString getSecurityIDForUpdate() { return _securityID; }

    @Override public final ExchangeCode getSecurityExchange() { return _securityExchange; }
    @Override public final void setSecurityExchange( ExchangeCode val ) { _securityExchange = val; }

    @Override public final int getMdSecurityTradingStatus() { return _mdSecurityTradingStatus; }
    @Override public final void setMdSecurityTradingStatus( int val ) { _mdSecurityTradingStatus = val; }

    @Override public final int getNoMDEntries() { return _noMDEntries; }
    @Override public final void setNoMDEntries( int val ) { _noMDEntries = val; }

    @Override public final MDSnapEntry getMDEntries() { return _MDEntries; }
    @Override public final void setMDEntries( MDSnapEntry val ) { _MDEntries = val; }

    @Override public final int getMsgSeqNum() { return _msgSeqNum; }
    @Override public final void setMsgSeqNum( int val ) { _msgSeqNum = val; }

    @Override public final long getEventTimestamp() { return _eventTimestamp; }
    @Override public final void setEventTimestamp( long val ) { _eventTimestamp = val; }


    @Override public final boolean getPossDupFlag() { return isFlagSet( MsgFlag.PossDupFlag ); }
    @Override public final void setPossDupFlag( boolean val ) { setFlag( MsgFlag.PossDupFlag, val ); }

   // Reusable Contract

    @Override
    public final void reset() {
        _received = Constants.UNSET_LONG;
        _lastMsgSeqNumProcessed = Constants.UNSET_INT;
        _totNumReports = Constants.UNSET_INT;
        _rptSeq = Constants.UNSET_INT;
        _mdBookType = Constants.UNSET_INT;
        _securityIDSource = SecurityIDSource.ExchangeSymbol;
        _securityID.reset();
        _securityExchange = null;
        _mdSecurityTradingStatus = Constants.UNSET_INT;
        _noMDEntries = Constants.UNSET_INT;
        _MDEntries = null;
        _msgSeqNum = Constants.UNSET_INT;
        _eventTimestamp = Constants.UNSET_LONG;
        _flags = 0;
        _next = null;
        _nextMessage = null;
        _messageHandler = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.MDSnapshotFullRefresh;
    }

    @Override
    public final MDSnapshotFullRefreshImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( MDSnapshotFullRefreshImpl nxt ) {
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
        out.append( "MDSnapshotFullRefreshImpl" ).append( ' ' );
        if ( Constants.UNSET_LONG != getReceived() && 0 != getReceived() )             out.append( ", received=" ).append( getReceived() );
        if ( Constants.UNSET_INT != getLastMsgSeqNumProcessed() && 0 != getLastMsgSeqNumProcessed() )             out.append( ", lastMsgSeqNumProcessed=" ).append( getLastMsgSeqNumProcessed() );
        if ( Constants.UNSET_INT != getTotNumReports() && 0 != getTotNumReports() )             out.append( ", totNumReports=" ).append( getTotNumReports() );
        if ( Constants.UNSET_INT != getRptSeq() && 0 != getRptSeq() )             out.append( ", rptSeq=" ).append( getRptSeq() );
        if ( Constants.UNSET_INT != getMdBookType() && 0 != getMdBookType() )             out.append( ", mdBookType=" ).append( getMdBookType() );
        if ( getSecurityIDSource() != null )             out.append( ", securityIDSource=" );
        if ( getSecurityIDSource() != null ) out.append( getSecurityIDSource().id() );
        if ( getSecurityID().length() > 0 )             out.append( ", securityID=" ).append( getSecurityID() );
        if ( getSecurityExchange() != null )             out.append( ", securityExchange=" );
        if ( getSecurityExchange() != null ) out.append( getSecurityExchange().id() );
        if ( Constants.UNSET_INT != getMdSecurityTradingStatus() && 0 != getMdSecurityTradingStatus() )             out.append( ", mdSecurityTradingStatus=" ).append( getMdSecurityTradingStatus() );
        if ( Constants.UNSET_INT != getNoMDEntries() && 0 != getNoMDEntries() )             out.append( ", noMDEntries=" ).append( getNoMDEntries() );

        MDSnapEntryImpl tPtrMDEntries = (MDSnapEntryImpl) getMDEntries();
        int tIdxMDEntries=0;

        while( tPtrMDEntries != null ) {
            out.append( " {#" ).append( ++tIdxMDEntries ).append( "} " );
            tPtrMDEntries.dump( out );
            tPtrMDEntries = tPtrMDEntries.getNext();
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

    @Override public final void snapTo( MDSnapshotFullRefresh dest ) {
        ((MDSnapshotFullRefreshImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( MDSnapshotFullRefresh src ) {
        setReceived( src.getReceived() );
        setLastMsgSeqNumProcessed( src.getLastMsgSeqNumProcessed() );
        setTotNumReports( src.getTotNumReports() );
        setRptSeq( src.getRptSeq() );
        setMdBookType( src.getMdBookType() );
        setSecurityIDSource( src.getSecurityIDSource() );
        getSecurityIDForUpdate().copy( src.getSecurityID() );
        setSecurityExchange( src.getSecurityExchange() );
        setMdSecurityTradingStatus( src.getMdSecurityTradingStatus() );
        setNoMDEntries( src.getNoMDEntries() );
        MDSnapEntryImpl tSrcPtrMDEntries = (MDSnapEntryImpl) src.getMDEntries();
        MDSnapEntryImpl tNewPtrMDEntries = null;
        while( tSrcPtrMDEntries != null ) {
            if ( tNewPtrMDEntries == null ) {
                tNewPtrMDEntries = new MDSnapEntryImpl();
                setMDEntries( tNewPtrMDEntries );
            } else {
                tNewPtrMDEntries.setNext( new MDSnapEntryImpl() );
                tNewPtrMDEntries = tNewPtrMDEntries.getNext();
            }
            tNewPtrMDEntries.deepCopyFrom( tSrcPtrMDEntries );
            tSrcPtrMDEntries = tSrcPtrMDEntries.getNext();
        }
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( MDSnapshotFullRefresh src ) {
        setReceived( src.getReceived() );
        setLastMsgSeqNumProcessed( src.getLastMsgSeqNumProcessed() );
        setTotNumReports( src.getTotNumReports() );
        setRptSeq( src.getRptSeq() );
        setMdBookType( src.getMdBookType() );
        setSecurityIDSource( src.getSecurityIDSource() );
        getSecurityIDForUpdate().copy( src.getSecurityID() );
        setSecurityExchange( src.getSecurityExchange() );
        setMdSecurityTradingStatus( src.getMdSecurityTradingStatus() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( MDSnapshotFullRefresh src ) {
        if ( Constants.UNSET_LONG != src.getReceived() ) setReceived( src.getReceived() );
        if ( Constants.UNSET_INT != src.getLastMsgSeqNumProcessed() ) setLastMsgSeqNumProcessed( src.getLastMsgSeqNumProcessed() );
        if ( Constants.UNSET_INT != src.getTotNumReports() ) setTotNumReports( src.getTotNumReports() );
        if ( Constants.UNSET_INT != src.getRptSeq() ) setRptSeq( src.getRptSeq() );
        if ( Constants.UNSET_INT != src.getMdBookType() ) setMdBookType( src.getMdBookType() );
        if ( getSecurityIDSource() != null )  setSecurityIDSource( src.getSecurityIDSource() );
        if ( src.getSecurityID().length() > 0 ) getSecurityIDForUpdate().copy( src.getSecurityID() );
        if ( getSecurityExchange() != null )  setSecurityExchange( src.getSecurityExchange() );
        if ( Constants.UNSET_INT != src.getMdSecurityTradingStatus() ) setMdSecurityTradingStatus( src.getMdSecurityTradingStatus() );
        if ( Constants.UNSET_INT != src.getMsgSeqNum() ) setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        if ( Constants.UNSET_LONG != src.getEventTimestamp() ) setEventTimestamp( src.getEventTimestamp() );
    }

}
