/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.us.cme.reader;

import com.rr.core.codec.binary.fastfix.FastFixDecodeBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapReader;
import com.rr.core.codec.binary.fastfix.common.ComponentFactory;
import com.rr.core.codec.binary.fastfix.common.constant.int32.IntMandReaderConst;
import com.rr.core.codec.binary.fastfix.common.constant.string.StringMandReaderConst;
import com.rr.core.codec.binary.fastfix.common.def.int32.UIntOptReaderDefault;
import com.rr.core.codec.binary.fastfix.common.def.string.StringReaderDefault;
import com.rr.core.codec.binary.fastfix.common.noop.int32.UIntMandReaderNoOp;
import com.rr.core.codec.binary.fastfix.common.noop.int32.UIntOptReaderNoOp;
import com.rr.core.codec.binary.fastfix.common.noop.int64.ULongMandReaderNoOp;
import com.rr.core.codec.binary.fastfix.msgdict.copy.int32.UIntMandReaderCopy;
import com.rr.core.codec.binary.fastfix.msgdict.copy.string.StringReaderCopy;
import com.rr.core.codec.binary.fastfix.msgdict.custom.defexp.delmant.MandDefExpDeltaMantDecimalReader;
import com.rr.core.codec.binary.fastfix.msgdict.custom.defexp.delmant.OptDefExpDeltaMantDecimalReader;
import com.rr.core.codec.binary.fastfix.msgdict.delta.int32.IntOptReaderDelta;
import com.rr.core.codec.binary.fastfix.msgdict.delta.int32.UIntOptReaderDelta;
import com.rr.core.codec.binary.fastfix.msgdict.increment.int32.UIntMandReaderIncrement;
import com.rr.core.lang.Constants;
import com.rr.core.lang.ReusableString;
import com.rr.core.model.SecurityIDSource;
import com.rr.core.pool.SuperPool;
import com.rr.core.pool.SuperpoolManager;
import com.rr.md.fastfix.template.MDIncRefreshFastFixTemplateReader;
import com.rr.model.generated.internal.events.factory.MDEntryFactory;
import com.rr.model.generated.internal.events.factory.MDIncRefreshFactory;
import com.rr.model.generated.internal.events.impl.MDEntryImpl;
import com.rr.model.generated.internal.events.impl.MDIncRefreshImpl;
import com.rr.model.generated.internal.type.MDEntryType;
import com.rr.model.generated.internal.type.MDUpdateAction;

/*
 * Hand coded template for CME MDIncRefresh #103
 * 
 * only boxes ints in constructor
 
    <template name="MDIncRefresh_103" id="103" dictionary="103"
        <string name="ApplVerID" id="1128"> <constant value="9" /> </string>
        <string name="MessageType" id="35"> <constant value="X" /> </string>
        <string name="SenderCompID" id="49"> <constant value="CME" /> </string>
        <uInt32 name="MsgSeqNum" id="34"></uInt32>
        <uInt64 name="SendingTime" id="52"></uInt64>
        <string name="PosDupFlag" id="43" presence="optional"> <default /> </string>
        <uInt32 name="TradeDate" id="75"></uInt32>
        <sequence name="MDEntries">
            <length name="NoMDEntries" id="268"></length>
            <uInt32 name="MDUpdateAction" id="279"> <copy value="1" />
            <uInt32 name="MDPriceLevel" id="1023" presence="optional"> <default value="1" />
            <string name="MDEntryType" id="269"> <copy value="0" />
            <uInt32 name="OpenCloseSettleFlag" id="286" presence="optional">
            <uInt32 name="SettlDate" id="64" presence="optional"> <default />
            <uInt32 name="SecurityIDSource" id="22"> <constant value="8" />
            <uInt32 name="SecurityID" id="48"> <copy />
            <uInt32 name="RptSeq" id="83"> <increment />
            <decimal name="MDEntryPx" id="270"> <exponent> <default value="0" /> <mantissa> <delta />
            <uInt32 name="MDEntryTime" id="273"> <copy />
            <int32 name="MDEntrySize" id="271" presence="optional"> <delta />
            <uInt32 name="NumberOfOrders" id="346" presence="optional"> <delta />
            <string name="TradingSessionID" id="336" presence="optional"> <default value="2" />
            <decimal name="NetChgPrevDay" id="451" presence="optional"> <exponent> <default /> <mantissa> <delta />
            <uInt32 name="TradeVolume" id="1020" presence="optional"> <default />
            <string name="TradeCondition" id="277" presence="optional"> <default />
            <string name="TickDirection" id="274" presence="optional"> <default />
            <string name="QuoteCondition" id="276" presence="optional"> <default />
            <uInt32 name="AggressorSide" id="5797" presence="optional"> <default />
            <string name="MatchEventIndicator" id="5799" presence="optional"> <default value="1" />
        </sequence>
    </template> 

 
 */

public final class MDIncRefresh_103_Reader implements MDIncRefreshFastFixTemplateReader {

    private final String _name;
    private final int    _id;

    private final PresenceMapReader seqPMap = new PresenceMapReader();

    private final StringMandReaderConst _f1_ApplVerID;
    private final StringMandReaderConst _f2_MessageType;
    private final StringMandReaderConst _f3_SenderCompID;
    private final UIntMandReaderNoOp    _f4_MsgSeqNum;
    private final ULongMandReaderNoOp   _f5_SendingTime;
    private final StringReaderDefault   _f6_PosDupFlag;
    private final UIntMandReaderNoOp    _f6b_TradeDate;

    private final UIntMandReaderNoOp   _f7_NoMDEntries;
    private final UIntMandReaderCopy   _f8_MDUpdateAction;
    private final UIntOptReaderDefault _f9_PriceLevel;
    private final StringReaderCopy     _f10_MDEntryType;
    private final UIntOptReaderNoOp    _f10b_OpenCloseSettleFlag;
    private final UIntOptReaderDefault _f10c_SettlDate;

    private final IntMandReaderConst               _f11_SecurityIDSource; // should be UInt const
    private final UIntMandReaderCopy               _f12_SecurityID;
    private final UIntMandReaderIncrement          _f13_RptSeq;
    private final MandDefExpDeltaMantDecimalReader _f14_MDEntryPx;
    private final UIntMandReaderCopy               _f15_MDEntryTime;
    private final IntOptReaderDelta                _f16_MDEntrySize;
    private final UIntOptReaderDelta               _f18_NumberOfOrders;
    private final StringReaderDefault              _f19_TradingSessionID;
    private final OptDefExpDeltaMantDecimalReader  _f20_NetChgPrevDay;
    private final UIntOptReaderDefault             _f21_TradeVolume;
    private final StringReaderDefault              _f22_TradeCondition;
    private final StringReaderDefault              _f23_TickDirection;
    private final StringReaderDefault              _f24_QuoteCondition;
    private final UIntOptReaderDefault             _f25_AggressorSide;
    private final StringReaderDefault              _f26_MatchEventIndicator;

    private final ReusableString _mdEntryType = new ReusableString( 2 );

    private final SuperPool<MDIncRefreshImpl> _mdIncPool    = SuperpoolManager.instance().getSuperPool( MDIncRefreshImpl.class );
    private final MDIncRefreshFactory         _mdIncFactory = new MDIncRefreshFactory( _mdIncPool );

    private final SuperPool<MDEntryImpl> _entryPool    = SuperpoolManager.instance().getSuperPool( MDEntryImpl.class );
    private final MDEntryFactory         _entryFactory = new MDEntryFactory( _entryPool );

    @SuppressWarnings( "boxing" )
    public MDIncRefresh_103_Reader( ComponentFactory cf, String name, int id ) {
        _name = name;
        _id   = id;

        _f1_ApplVerID      = cf.getReader( StringMandReaderConst.class, "ApplVerID", 1128, new ReusableString( "9" ) );
        _f2_MessageType    = cf.getReader( StringMandReaderConst.class, "MessageType", 35, new ReusableString( "X" ) );
        _f3_SenderCompID   = cf.getReader( StringMandReaderConst.class, "SenderCompID", 49, new ReusableString( "CME" ) );
        _f4_MsgSeqNum      = cf.getReader( UIntMandReaderNoOp.class, "MsgSeqNum", 49 );
        _f5_SendingTime    = cf.getReader( ULongMandReaderNoOp.class, "SendingTime", 52 );
        _f6_PosDupFlag     = cf.getReader( StringReaderDefault.class, "PosDupFlag", 43, new ReusableString( "" ) );
        _f6b_TradeDate     = cf.getReader( UIntMandReaderNoOp.class, "TradeDate", 75 );
        _f7_NoMDEntries    = cf.getReader( UIntMandReaderNoOp.class, "NoMDEntries", 268 );
        _f8_MDUpdateAction = cf.getReader( UIntMandReaderCopy.class, "MDUpdateAction", 279, 1 );
        _f9_PriceLevel     = cf.getReader( UIntOptReaderDefault.class, "PriceLevel", 1023, 1 );
        _f10_MDEntryType   = cf.getReader( StringReaderCopy.class, "MDEntryType", 269, new ReusableString( "0" ) );

        _f10b_OpenCloseSettleFlag = cf.getReader( UIntOptReaderNoOp.class, "OpenCloseSettleFlag", 286 );
        _f10c_SettlDate           = cf.getReader( UIntOptReaderDefault.class, "SettlDate", 64 );

        _f11_SecurityIDSource = cf.getReader( IntMandReaderConst.class, "SecurityIDSource", 22, 8 );
        _f12_SecurityID       = cf.getReader( UIntMandReaderCopy.class, "SecurityID", 48 );
        _f13_RptSeq           = cf.getReader( UIntMandReaderIncrement.class, "RptSeq", 83 );

        _f14_MDEntryPx = cf.getReader( MandDefExpDeltaMantDecimalReader.class, cf, "MDEntryPx", 270, 0, 0L );

        _f15_MDEntryTime      = cf.getReader( UIntMandReaderCopy.class, "MDEntryTime", 273 );
        _f16_MDEntrySize      = cf.getReader( IntOptReaderDelta.class, "MDEntrySize", 271 );
        _f18_NumberOfOrders   = cf.getReader( UIntOptReaderDelta.class, "NumberOfOrders", 346 );
        _f19_TradingSessionID = cf.getReader( StringReaderDefault.class, "TradingSessionID", 336, new ReusableString( "2" ) );

        _f20_NetChgPrevDay       = cf.getReader( OptDefExpDeltaMantDecimalReader.class, cf, "NetChgPrevDay", 451, Constants.UNSET_INT, 0L );
        _f21_TradeVolume         = cf.getReader( UIntOptReaderDefault.class, "TradeVolume", 1020 );
        _f22_TradeCondition      = cf.getReader( StringReaderDefault.class, "TradeCondition", 277, new ReusableString( "" ) );
        _f23_TickDirection       = cf.getReader( StringReaderDefault.class, "TickDirection", 274, new ReusableString( "" ) );
        _f24_QuoteCondition      = cf.getReader( StringReaderDefault.class, "QuoteCondition", 276, new ReusableString( "" ) );
        _f25_AggressorSide       = cf.getReader( UIntOptReaderDefault.class, "AggressorSide", 5797 );
        _f26_MatchEventIndicator = cf.getReader( StringReaderDefault.class, "MatchEventIndicator", 5799, new ReusableString( "1" ) );

    }

    @Override
    public MDIncRefreshImpl read( final FastFixDecodeBuilder decoder, final PresenceMapReader pMap ) {

        final MDIncRefreshImpl dest = _mdIncFactory.get();

        _f1_ApplVerID.read( decoder, null );                        // pass null to skip field
        _f2_MessageType.read( decoder, null );
        _f3_SenderCompID.read( decoder, null );

        dest.setMsgSeqNum( _f4_MsgSeqNum.read( decoder ) );
        dest.setEventTimestamp( _f5_SendingTime.read( decoder ) );

        _f6_PosDupFlag.read( decoder, pMap, (ReusableString) null );                 // skip pos dup as we dont care
        _f6b_TradeDate.read( decoder );                                             // dont need trade date

        int NoMDEntries = _f7_NoMDEntries.read( decoder );    // requires local var to hold var

        dest.setNoMDEntries( NoMDEntries );

        if ( NoMDEntries > 0 ) {
            MDEntryImpl _MDEntries = _entryFactory.get();
            dest.setMDEntries( _MDEntries );

            MDEntryImpl tmp;

            do {
                seqPMap.readMap( decoder );

                // encode fields

                _MDEntries.setMdUpdateAction( MDUpdateAction.getVal( (byte) (_f8_MDUpdateAction.read( decoder, seqPMap ) + '0') ) );
                _MDEntries.setMdPriceLevel( _f9_PriceLevel.read( decoder, seqPMap ) );

                _mdEntryType.reset();
                _f10_MDEntryType.read( decoder, seqPMap, _mdEntryType );
                if ( _mdEntryType.length() > 0 ) {
                    _MDEntries.setMdEntryType( MDEntryType.getVal( _mdEntryType.getByte( 0 ) ) );
                }

                _f10b_OpenCloseSettleFlag.read( decoder );  // read and ignore
                _f10c_SettlDate.read( decoder, seqPMap );      // read and ignore

                _MDEntries.setSecurityIDSource( SecurityIDSource.getVal( (byte) (_f11_SecurityIDSource.read() + '0') ) );
                _MDEntries.getSecurityIDForUpdate().setValue( _f12_SecurityID.read( decoder, seqPMap ) );

                _MDEntries.setRepeatSeq( _f13_RptSeq.read( decoder, seqPMap ) );

                _MDEntries.setMdEntryPx( _f14_MDEntryPx.read( decoder, seqPMap ) );
                _MDEntries.setMdEntryTime( _f15_MDEntryTime.read( decoder, seqPMap ) );
                _MDEntries.setMdEntrySize( _f16_MDEntrySize.read( decoder ) );

                _MDEntries.setNumberOfOrders( _f18_NumberOfOrders.read( decoder ) );

                _f19_TradingSessionID.read( decoder, seqPMap, (ReusableString) null );

                _f20_NetChgPrevDay.read( decoder, seqPMap );
                _f21_TradeVolume.read( decoder, seqPMap );
                _f22_TradeCondition.read( decoder, seqPMap, (ReusableString) null );
                _f23_TickDirection.read( decoder, seqPMap, (ReusableString) null );
                _f24_QuoteCondition.read( decoder, seqPMap, (ReusableString) null );
                _f25_AggressorSide.read( decoder, seqPMap );
                _f26_MatchEventIndicator.read( decoder, seqPMap, (ReusableString) null );

                if ( --NoMDEntries == 0 ) {
                    break;
                }

                tmp        = _MDEntries;
                _MDEntries = _entryFactory.get();
                tmp.setNext( _MDEntries );

            } while( true );
        }

        return dest;
    }

    @Override
    public void reset() {
        _f1_ApplVerID.reset();
        _f2_MessageType.reset();
        _f3_SenderCompID.reset();
        _f4_MsgSeqNum.reset();
        _f5_SendingTime.reset();
        _f6_PosDupFlag.reset();
        _f6b_TradeDate.reset();
        _f7_NoMDEntries.reset();
        _f8_MDUpdateAction.reset();
        _f9_PriceLevel.reset();
        _f10_MDEntryType.reset();
        _f10b_OpenCloseSettleFlag.reset();
        _f10c_SettlDate.reset();
        _f11_SecurityIDSource.reset();
        _f12_SecurityID.reset();
        _f13_RptSeq.reset();
        _f14_MDEntryPx.reset();
        _f15_MDEntryTime.reset();
        _f16_MDEntrySize.reset();
        _f18_NumberOfOrders.reset();
        _f19_TradingSessionID.reset();
        _f20_NetChgPrevDay.reset();
        _f21_TradeVolume.reset();
        _f22_TradeCondition.reset();
        _f23_TickDirection.reset();
        _f24_QuoteCondition.reset();
        _f25_AggressorSide.reset();
        _f26_MatchEventIndicator.reset();
    }

    public int getId() {
        return _id;
    }

    public String getName() {
        return _name;
    }
}
