package com.rr.model.generated.internal.events.impl;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.ETIOrderProcessingType;
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

public final class ETISessionLogonRequestImpl implements BaseETIRequest, ETISessionLogonRequestWrite, Copyable<ETISessionLogonRequest>, Reusable<ETISessionLogonRequestImpl> {

   // Attrs

    private transient          ETISessionLogonRequestImpl _next = null;
    private transient volatile Event        _nextMessage    = null;
    private transient          EventHandler _messageHandler = null;
    private int _heartBtIntMS = Constants.UNSET_INT;
    private int _partyIDSessionID = Constants.UNSET_INT;
    private final ReusableString _defaultCstmApplVerID = new ReusableString( SizeType.ETI_INTERFACE_VERSION_LENGTH.getSize() );
    private final ReusableString _password = new ReusableString( SizeType.ETI_PASSWORD_LENGTH.getSize() );
    private boolean _orderRoutingIndicator = false;
    private final ReusableString _applicationSystemName = new ReusableString( SizeType.ETI_APP_SYS_NAME_LENGTH.getSize() );
    private final ReusableString _applicationSystemVer = new ReusableString( SizeType.ETI_APP_SYS_VER_LENGTH.getSize() );
    private final ReusableString _applicationSystemVendor = new ReusableString( SizeType.ETI_APP_SYS_VENDOR_LENGTH.getSize() );
    private int _msgSeqNum = Constants.UNSET_INT;
    @TimestampMS private long _eventTimestamp = Constants.UNSET_LONG;

    private ETIOrderProcessingType _applUsageOrders;
    private ETIOrderProcessingType _applUsageQuotes;

    private int           _flags          = 0;

   // Getters and Setters
    @Override public final int getHeartBtIntMS() { return _heartBtIntMS; }
    @Override public final void setHeartBtIntMS( int val ) { _heartBtIntMS = val; }

    @Override public final int getPartyIDSessionID() { return _partyIDSessionID; }
    @Override public final void setPartyIDSessionID( int val ) { _partyIDSessionID = val; }

    @Override public final ViewString getDefaultCstmApplVerID() { return _defaultCstmApplVerID; }

    @Override public final void setDefaultCstmApplVerID( byte[] buf, int offset, int len ) { _defaultCstmApplVerID.setValue( buf, offset, len ); }
    @Override public final ReusableString getDefaultCstmApplVerIDForUpdate() { return _defaultCstmApplVerID; }

    @Override public final ViewString getPassword() { return _password; }

    @Override public final void setPassword( byte[] buf, int offset, int len ) { _password.setValue( buf, offset, len ); }
    @Override public final ReusableString getPasswordForUpdate() { return _password; }

    @Override public final ETIOrderProcessingType getApplUsageOrders() { return _applUsageOrders; }
    @Override public final void setApplUsageOrders( ETIOrderProcessingType val ) { _applUsageOrders = val; }

    @Override public final ETIOrderProcessingType getApplUsageQuotes() { return _applUsageQuotes; }
    @Override public final void setApplUsageQuotes( ETIOrderProcessingType val ) { _applUsageQuotes = val; }

    @Override public final boolean getOrderRoutingIndicator() { return _orderRoutingIndicator; }
    @Override public final void setOrderRoutingIndicator( boolean val ) { _orderRoutingIndicator = val; }

    @Override public final ViewString getApplicationSystemName() { return _applicationSystemName; }

    @Override public final void setApplicationSystemName( byte[] buf, int offset, int len ) { _applicationSystemName.setValue( buf, offset, len ); }
    @Override public final ReusableString getApplicationSystemNameForUpdate() { return _applicationSystemName; }

    @Override public final ViewString getApplicationSystemVer() { return _applicationSystemVer; }

    @Override public final void setApplicationSystemVer( byte[] buf, int offset, int len ) { _applicationSystemVer.setValue( buf, offset, len ); }
    @Override public final ReusableString getApplicationSystemVerForUpdate() { return _applicationSystemVer; }

    @Override public final ViewString getApplicationSystemVendor() { return _applicationSystemVendor; }

    @Override public final void setApplicationSystemVendor( byte[] buf, int offset, int len ) { _applicationSystemVendor.setValue( buf, offset, len ); }
    @Override public final ReusableString getApplicationSystemVendorForUpdate() { return _applicationSystemVendor; }

    @Override public final int getMsgSeqNum() { return _msgSeqNum; }
    @Override public final void setMsgSeqNum( int val ) { _msgSeqNum = val; }

    @Override public final long getEventTimestamp() { return _eventTimestamp; }
    @Override public final void setEventTimestamp( long val ) { _eventTimestamp = val; }


    @Override public final boolean getPossDupFlag() { return isFlagSet( MsgFlag.PossDupFlag ); }
    @Override public final void setPossDupFlag( boolean val ) { setFlag( MsgFlag.PossDupFlag, val ); }

   // Reusable Contract

    @Override
    public final void reset() {
        _heartBtIntMS = Constants.UNSET_INT;
        _partyIDSessionID = Constants.UNSET_INT;
        _defaultCstmApplVerID.reset();
        _password.reset();
        _applUsageOrders = null;
        _applUsageQuotes = null;
        _orderRoutingIndicator = false;
        _applicationSystemName.reset();
        _applicationSystemVer.reset();
        _applicationSystemVendor.reset();
        _msgSeqNum = Constants.UNSET_INT;
        _eventTimestamp = Constants.UNSET_LONG;
        _flags = 0;
        _next = null;
        _nextMessage = null;
        _messageHandler = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.ETISessionLogonRequest;
    }

    @Override
    public final ETISessionLogonRequestImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( ETISessionLogonRequestImpl nxt ) {
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
        out.append( "ETISessionLogonRequestImpl" ).append( ' ' );
        if ( Constants.UNSET_INT != getHeartBtIntMS() && 0 != getHeartBtIntMS() )             out.append( ", heartBtIntMS=" ).append( getHeartBtIntMS() );
        if ( Constants.UNSET_INT != getPartyIDSessionID() && 0 != getPartyIDSessionID() )             out.append( ", partyIDSessionID=" ).append( getPartyIDSessionID() );
        if ( getDefaultCstmApplVerID().length() > 0 )             out.append( ", defaultCstmApplVerID=" ).append( getDefaultCstmApplVerID() );
        if ( getPassword().length() > 0 )             out.append( ", password=" ).append( getPassword() );
        if ( getApplUsageOrders() != null )             out.append( ", applUsageOrders=" ).append( getApplUsageOrders() );
        if ( getApplUsageQuotes() != null )             out.append( ", applUsageQuotes=" ).append( getApplUsageQuotes() );
        out.append( ", orderRoutingIndicator=" ).append( getOrderRoutingIndicator() );
        if ( getApplicationSystemName().length() > 0 )             out.append( ", applicationSystemName=" ).append( getApplicationSystemName() );
        if ( getApplicationSystemVer().length() > 0 )             out.append( ", applicationSystemVer=" ).append( getApplicationSystemVer() );
        if ( getApplicationSystemVendor().length() > 0 )             out.append( ", applicationSystemVendor=" ).append( getApplicationSystemVendor() );
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

    @Override public final void snapTo( ETISessionLogonRequest dest ) {
        ((ETISessionLogonRequestImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( ETISessionLogonRequest src ) {
        setHeartBtIntMS( src.getHeartBtIntMS() );
        setPartyIDSessionID( src.getPartyIDSessionID() );
        getDefaultCstmApplVerIDForUpdate().copy( src.getDefaultCstmApplVerID() );
        getPasswordForUpdate().copy( src.getPassword() );
        setApplUsageOrders( src.getApplUsageOrders() );
        setApplUsageQuotes( src.getApplUsageQuotes() );
        setOrderRoutingIndicator( src.getOrderRoutingIndicator() );
        getApplicationSystemNameForUpdate().copy( src.getApplicationSystemName() );
        getApplicationSystemVerForUpdate().copy( src.getApplicationSystemVer() );
        getApplicationSystemVendorForUpdate().copy( src.getApplicationSystemVendor() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( ETISessionLogonRequest src ) {
        setHeartBtIntMS( src.getHeartBtIntMS() );
        setPartyIDSessionID( src.getPartyIDSessionID() );
        getDefaultCstmApplVerIDForUpdate().copy( src.getDefaultCstmApplVerID() );
        getPasswordForUpdate().copy( src.getPassword() );
        setApplUsageOrders( src.getApplUsageOrders() );
        setApplUsageQuotes( src.getApplUsageQuotes() );
        setOrderRoutingIndicator( src.getOrderRoutingIndicator() );
        getApplicationSystemNameForUpdate().copy( src.getApplicationSystemName() );
        getApplicationSystemVerForUpdate().copy( src.getApplicationSystemVer() );
        getApplicationSystemVendorForUpdate().copy( src.getApplicationSystemVendor() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( ETISessionLogonRequest src ) {
        if ( Constants.UNSET_INT != src.getHeartBtIntMS() ) setHeartBtIntMS( src.getHeartBtIntMS() );
        if ( Constants.UNSET_INT != src.getPartyIDSessionID() ) setPartyIDSessionID( src.getPartyIDSessionID() );
        if ( src.getDefaultCstmApplVerID().length() > 0 ) getDefaultCstmApplVerIDForUpdate().copy( src.getDefaultCstmApplVerID() );
        if ( src.getPassword().length() > 0 ) getPasswordForUpdate().copy( src.getPassword() );
        setApplUsageOrders( src.getApplUsageOrders() );
        setApplUsageQuotes( src.getApplUsageQuotes() );
        setOrderRoutingIndicator( src.getOrderRoutingIndicator() );
        if ( src.getApplicationSystemName().length() > 0 ) getApplicationSystemNameForUpdate().copy( src.getApplicationSystemName() );
        if ( src.getApplicationSystemVer().length() > 0 ) getApplicationSystemVerForUpdate().copy( src.getApplicationSystemVer() );
        if ( src.getApplicationSystemVendor().length() > 0 ) getApplicationSystemVendorForUpdate().copy( src.getApplicationSystemVendor() );
        if ( Constants.UNSET_INT != src.getMsgSeqNum() ) setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        if ( Constants.UNSET_LONG != src.getEventTimestamp() ) setEventTimestamp( src.getEventTimestamp() );
    }

}
