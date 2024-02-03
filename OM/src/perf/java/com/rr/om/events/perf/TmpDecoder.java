/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.events.perf;

import com.rr.core.codec.AbstractFixDecoder44;
import com.rr.core.codec.FixDecoder;
import com.rr.core.codec.FixField;
import com.rr.core.codec.RuntimeDecodingException;
import com.rr.core.lang.ReusableString;
import com.rr.core.model.Currency;
import com.rr.core.model.Event;
import com.rr.core.model.ExchangeCode;
import com.rr.core.model.SecurityIDSource;
import com.rr.core.pool.SuperPool;
import com.rr.core.pool.SuperpoolManager;
import com.rr.model.generated.internal.core.SizeType;
import com.rr.model.generated.internal.events.factory.ClientNewOrderSingleFactory;
import com.rr.model.generated.internal.events.impl.ClientNewOrderSingleImpl;
import com.rr.model.generated.internal.type.*;

@SuppressWarnings( "UnusedAssignment" ) public class TmpDecoder extends AbstractFixDecoder44 {

    private final ReusableString _tmpLookupKey = new ReusableString();

    // Pools

    private final SuperPool<ClientNewOrderSingleImpl> _newOrderSinglePool    = SuperpoolManager.instance().getSuperPool( ClientNewOrderSingleImpl.class );
    private final ClientNewOrderSingleFactory         _newOrderSingleFactory = new ClientNewOrderSingleFactory( _newOrderSinglePool );

    // Constructors
    public TmpDecoder( byte major, byte minor ) {
        super( major, minor );
    }

    @Override
    protected final Event doMessageDecode() {
        // get message type field
        if ( _fixMsg[ _idx ] != '3' || _fixMsg[ _idx + 1 ] != '5' || _fixMsg[ _idx + 2 ] != '=' )
            throwDecodeException( "Fix Messsage missing message type" );
        _idx += 3;

        byte msgType = _fixMsg[ _idx ];
        switch( msgType ) {
        case 8:
            if ( _fixMsg[ _idx + 1 ] != FixField.FIELD_DELIMITER ) { // 2 byte message type
                throwDecodeException( "Unsupported fix message type " + _fixMsg[ _idx ] + _fixMsg[ _idx + 1 ] );
            }
            return decodeExecReport();
        case 'D':
            if ( _fixMsg[ _idx + 1 ] != FixField.FIELD_DELIMITER ) { // 2 byte message type
                throwDecodeException( "Unsupported fix message type " + _fixMsg[ _idx ] + _fixMsg[ _idx + 1 ] );
            }
            return decodeNewOrderSingle();
        case 'G':
        case 'F':
        case '9':
        case '0':
        case 'A':
        case '5':
        case '3':
        case '2':
        case '4':
        case '1':
        case 'V':
        case 'W':
        case '6':
        case '7':
        case ':':
        case ';':
        case '<':
        case '=':
        case '>':
        case '?':
        case '@':
        case 'B':
        case 'C':
        case 'E':
        case 'H':
        case 'I':
        case 'J':
        case 'K':
        case 'L':
        case 'M':
        case 'N':
        case 'O':
        case 'P':
        case 'Q':
        case 'R':
        case 'S':
        case 'T':
        case 'U':
            break;
        }
        _idx += 2;
        throwDecodeException( "Unsupported fix message type " + msgType );
        return null;
    }

    @Override public String getComponentId()  { return null; }

    @Override public FixDecoder newInstance() { return null; }

    public final Event XXdecodeNewOrderSingle() {
        ClientNewOrderSingleImpl msg = _newOrderSingleFactory.get();
        int                      tag = getTag();

        int start;
        int valLen;

        while( tag != 0 ) {
            switch( tag ) {
            case 1:
                start = _idx;
                valLen = getValLength();
                msg.setAccount( start, valLen );
                break;
            case 11:
                start = _idx;
                valLen = getValLength();
                msg.setClOrdId( start, valLen );
                break;
            case 15:
                start = _idx;
                valLen = getValLength();
                _tmpLookupKey.setValue( _fixMsg, start, valLen );
                msg.setCurrency( Currency.getVal( _tmpLookupKey ) );
                break;
            case 21:
                msg.setHandlInst( HandlInst.getVal( _fixMsg[ _idx++ ] ) );
                break;
            case 22:
                msg.setSecurityIDSource( SecurityIDSource.getVal( _fixMsg[ _idx++ ] ) );
                break;
            case 38:
                msg.setOrderQty( getIntVal() );
                break;
            case 40:
                msg.setOrdType( OrdType.getVal( _fixMsg[ _idx++ ] ) );
                break;
            case 44:
                msg.setPrice( getDoubleVal() );
                break;
            case 48:
                start = _idx;
                valLen = getValLength();
                msg.setSecurityId( start, valLen );
                break;
            case 49:
                decodeSenderCompID();
                break;
            case 50:
                decodeSenderSubID();
                break;
            case 52:
                msg.setEventTimestamp( getInternalTime() );
                break;
            case 54:
                msg.setSide( Side.getVal( _fixMsg[ _idx++ ] ) );
                break;
            case 55:
                start = _idx;
                valLen = getValLength();
                msg.setSymbol( start, valLen );
                break;
            case 56:
                decodeTargetCompID();
                break;
            case 57:
                decodeTargetSubID();
                break;
            case 58:
                start = _idx;
                valLen = getValLength();
                msg.setText( start, valLen );
                break;
            case 59:
                msg.setTimeInForce( TimeInForce.getVal( _fixMsg[ _idx++ ] ) );
                break;
            case 60:
                msg.setTransactTime( getInternalTime() );
                break;
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 12:
            case 13:
            case 14:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 39:
            case 41:
            case 42:
            case 43:
            case 45:
            case 46:
            case 47:
            case 51:
            case 53:
                getValLength();
                break;
            default:
                switch( tag ) {
                case 100:
                    start = _idx;
                    valLen = getValLength();
                    msg.setExDest( start, valLen );
                    break;
                case 528:
                    msg.setOrderCapacity( OrderCapacity.getVal( _fixMsg[ _idx++ ] ) );
                    break;
                case 207:
                    start = _idx;
                    valLen = getValLength();
                    _tmpLookupKey.setValue( _fixMsg, start, valLen );
                    msg.setSecurityExchange( ExchangeCode.getVal( _tmpLookupKey ) );
                    break;
                case 211:
                    getValLength();
                    throwDecodeException( "Tag 211 not supported in NewOrderSingle" );
                    break;
                }
                break;
            }
            _idx++; /* past delimiter */
            tag = getTag();
        }

        if ( _idx > SizeType.VIEW_NOS_BUFFER.getSize() ) {
            throw new RuntimeDecodingException( "NewOrderSingleMessage too big " + _idx + ", max=" + SizeType.VIEW_NOS_BUFFER.getSize() );
        }
        msg.setViewBuf( _fixMsg, _offset, _idx );

        // TODO instrument enrichment & ccy major/minor changes

        return msg;
    }

    public final Event decodeExecReport() {
        return null;
    }

    // decode methods
    public Event decodeNewOrderSingle() {
        // TODO get from pool
        ClientNewOrderSingleImpl msg = _newOrderSingleFactory.get();
        int                      tag = getTag();

        int start;
        int valLen;

        while( tag != 0 ) {
            switch( tag ) {
            case 0:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 12:
            case 13:
            case 14:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
                // forced table switch from  0 to 60
                valLen = getValLength();
                break;
            case 1:
                start = _idx;
                valLen = getValLength();
                msg.setAccount( start, valLen );
                break;
            case 11:
                start = _idx;
                valLen = getValLength();
                msg.setClOrdId( start, valLen );
                break;
            case 15:
                start = _idx;
                valLen = getValLength();
                _tmpLookupKey.setValue( _fixMsg, start, valLen );
                msg.setCurrency( Currency.getVal( _tmpLookupKey ) );
                break;
            case 21:
                msg.setHandlInst( HandlInst.getVal( _fixMsg[ _idx++ ] ) );
                break;
            case 22:
                msg.setSecurityIDSource( SecurityIDSource.getVal( _fixMsg[ _idx++ ] ) );
                break;
            case 38:
                msg.setOrderQty( getIntVal() );
                break;
            case 40:
                msg.setOrdType( OrdType.getVal( _fixMsg[ _idx++ ] ) );
                break;
            case 44:
                msg.setPrice( getDoubleVal() );
                break;
            case 48:
                start = _idx;
                valLen = getValLength();
                msg.setSecurityId( start, valLen );
                break;
            case 49:
                decodeSenderCompID();
                break;
            case 52:
                msg.setEventTimestamp( getInternalTime() );
                break;
            case 54:
                msg.setSide( Side.getVal( _fixMsg[ _idx++ ] ) );
                break;
            case 55:
                start = _idx;
                valLen = getValLength();
                msg.setSymbol( start, valLen );
                break;
            case 56:
                decodeTargetCompID();
                break;
            case 58:
                start = _idx;
                valLen = getValLength();
                msg.setText( start, valLen );
                break;
            case 59:
                msg.setTimeInForce( TimeInForce.getVal( _fixMsg[ _idx++ ] ) );
                break;
            case 60:
                msg.setTransactTime( getInternalTime() );
                break;
            default: //  SKIP
                switch( tag ) {
                case 100: // exDest
                    start = _idx;
                    valLen = getValLength();
                    msg.setExDest( start, valLen );
                    break;
                case 207: // securityExchange
                    start = _idx;
                    valLen = getValLength();
                    _tmpLookupKey.setValue( _fixMsg, start, valLen );
                    msg.setSecurityExchange( ExchangeCode.getVal( _tmpLookupKey ) );
                    break;
                case 528:
                    msg.setOrderCapacity( OrderCapacity.getVal( _fixMsg[ _idx++ ] ) );
                    break;
                default:
                    valLen = getValLength();
                    break;
                }
            }

            _idx++; // past delimiter

            tag = getTag();
        }

        if ( _idx > SizeType.VIEW_NOS_BUFFER.getSize() ) {

            throw new RuntimeDecodingException( "NewOrderSingle Message too big " + _idx + ", max=" + SizeType.VIEW_NOS_BUFFER.getSize() );
        }

        msg.setViewBuf( _fixMsg, _offset, _idx );

        // TODO instrument enrichment & ccy major/minor changes

        return msg;
    }

    public final Event populateExecRptCalculated() {
        return null; // flagged in model with IGNORE
    }

    public final Event populateExecRptCanceled() {
        return null;
    }

    public final Event populateExecRptDoneForDay() {
        return null;
    }

    public final Event populateExecRptExpired() {
        return null;
    }

    public final Event populateExecRptFill() {
        throwDecodeException( "ExecRpt type Fill not supported" );
        return null;
    }

    public final Event populateExecRptNew() {
        return null;
    }

    public final Event populateExecRptOrderStatus() {
        return null;
    }

    public final Event populateExecRptPartialFill() {
        throwDecodeException( "ExecRpt type PartialFill not supported" );
        return null;
    }

    public final Event populateExecRptPendingCancel() {
        return null; // flagged in model with IGNORE
    }

    public final Event populateExecRptPendingNew() {
        return null; // flagged in model with IGNORE
    }

    public final Event populateExecRptPendingReplace() {
        return null; // flagged in model with IGNORE
    }

    public final Event populateExecRptRejected() {
        return null;
    }

    public final Event populateExecRptReplace() {
        return null;
    }

    public final Event populateExecRptRestated() {
        return null;
    }

    public final Event populateExecRptStopped() {
        return null;
    }

    public final Event populateExecRptSuspended() {
        return null;
    }

    public final Event populateExecRptTrade() {
        return null;
    }

    public final Event populateExecRptTradeCancel() {
        return null;
    }

    public final Event populateExecRptTradeCorrect() {
        return null;
    }

    public final Event populateExecRptUnknown() {
        throwDecodeException( "ExecRpt type Unknown not supported" );
        return null;
    }
}
