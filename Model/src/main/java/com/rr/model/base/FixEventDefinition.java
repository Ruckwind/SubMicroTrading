/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

import com.rr.model.generator.FixEventType;
import com.rr.model.internal.type.ExecType;

public class FixEventDefinition extends BaseFixTagSet {

    private final FixEventType _fixMsgType;

    private final String _msgTypeCode;  // eg "D" for NOS

    private ExecType _execType;

    public FixEventDefinition( FixEventType id, String msgType, FixEventDefinition parent ) {
        this( id.toString(), id, msgType, parent );
    }

    public FixEventDefinition( String idStr, FixEventType id, String msgType, FixEventDefinition parent ) {
        super( idStr, parent );

        _msgTypeCode = msgType;
        _fixMsgType  = id;
    }

    public FixEventDefinition( FixEventDefinition fixDefn ) {
        this( fixDefn._fixMsgType, fixDefn._msgTypeCode, (FixEventDefinition) fixDefn.getParent() );
        _execType = fixDefn._execType;
        getRepeatingGroup().putAll( fixDefn.getRepeatingGroup() );
        getTags().putAll( fixDefn.getTags() );
    }

    @Override
    public String toString() {
        return "FixMessageDefinition [ msgType=" + _msgTypeCode + ", execType=" + _execType + " : " + super.toString() + "]";
    }

    public void clearFields() {
        getTags().clear();
    }

    public ExecType getExecType() {
        return _execType;
    }

    public void setExecType( ExecType type ) {
        _execType = type;
    }

    public FixEventType getFixMsgType() {
        return _fixMsgType;
    }

    public String getMsgType() {
        return _msgTypeCode;
    }
}
