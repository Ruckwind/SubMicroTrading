/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

public class BinaryEventDefinition extends BaseBinaryTagSet {

    private final String _binaryMsgType;

    private final String _msgTypeCode;  // eg "D" for NOS

    private boolean _isSubMsg = false;

    private int _blockLen = 0;

    public BinaryEventDefinition( String id, String msgType, BinaryEventDefinition parent, int blockLen, boolean isSubMessage ) {
        super( id, parent );

        _msgTypeCode   = msgType;
        _binaryMsgType = id;
        _blockLen      = blockLen;
        _isSubMsg      = isSubMessage;
    }

    public BinaryEventDefinition( BinaryEventDefinition BinaryDefn ) {
        this( BinaryDefn._binaryMsgType, BinaryDefn._msgTypeCode, (BinaryEventDefinition) BinaryDefn.getParent(), BinaryDefn._blockLen, BinaryDefn.isSubMessage() );
        getTags().putAll( BinaryDefn.getTags() );
    }

    @Override
    public String toString() {
        return "BinaryMessageDefinition [ msgType=" + _msgTypeCode + " : " + super.toString() + "]";
    }

    public void clearFields() {
        getTags().clear();
    }

    public String getBinaryMsgType() {
        return _binaryMsgType;
    }

    public final int getBlockLen() {
        return _blockLen;
    }

    public String getMsgType() {
        return _msgTypeCode;
    }

    public boolean isSubMessage() {
        return _isSubMsg;
    }

    public void setSubMessage( boolean isSubMsg ) {
        _isSubMsg = isSubMsg;
    }
}
