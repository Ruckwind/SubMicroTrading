/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

import com.rr.model.generator.FixEventType;

import java.util.*;

public class FixEventMap implements HookSupport {

    public static final int MAX_FORCE_TABLESWITCH    = 128;
    public static final int MAX_SUB_BAND             = 128;
    public static final int MIN_SUB_SWITCH_PACK_SIZE = 8;

    private final String       _id;
    private final String       _eventId;
    private final FixEventType _fixMsgId;

    private final Map<String, FixTagEventMapping> _tags       = new LinkedHashMap<>();
    private final Map<String, FixTagEventMapping> _tagsById   = new LinkedHashMap<>();
    private final Map<HookType, String>           _hooks      = new HashMap<>();
    private final Set<Integer>                    _rejectTags = new LinkedHashSet<>();
    private final boolean     _ignore;
    private ClassDefinition    _eventClassDefn;
    private FixEventDefinition _fixDefn;
    private       FixEventMap _parent;
    private       boolean     _handWrittenDecode;
    private       boolean     _handWrittenEncode;

    private int _maxForceTableSwitch  = MAX_FORCE_TABLESWITCH;
    private int _maxSubBand           = MAX_SUB_BAND;
    private int _minSubSwitchPackSize = MIN_SUB_SWITCH_PACK_SIZE;

    public FixEventMap( String id, String eventId, FixEventType fixMsgId ) {
        _id       = id;
        _eventId  = eventId;
        _fixMsgId = fixMsgId;
        _ignore   = false;
    }

    public FixEventMap( String id, FixEventType fixMsgId, boolean ignore ) {
        _ignore   = ignore;
        _id       = id;
        _fixMsgId = fixMsgId;
        _eventId  = null;
    }

    public FixEventMap( FixEventMap value ) {
        _id                   = value._id;
        _eventId              = value._eventId;
        _ignore               = value._ignore;
        _fixMsgId             = value._fixMsgId;
        _parent               = value._parent;
        _maxForceTableSwitch  = value._maxForceTableSwitch;
        _maxSubBand           = value._maxSubBand;
        _minSubSwitchPackSize = value._minSubSwitchPackSize;
        _handWrittenDecode    = value._handWrittenDecode;
        _handWrittenEncode    = value._handWrittenEncode;

        for ( FixTagEventMapping e : value._tags.values() ) {
            FixTagEventMapping newE = new FixTagEventMapping( e );

            if ( newE.getEventAttr() != null ) _tags.put( newE.getEventAttr(), newE );
            if ( newE.getFixTag() != null ) _tagsById.put( newE.getFixTag(), newE );
        }

        for ( FixTagEventMapping e : value._tagsById.values() ) {
            if ( !_tagsById.containsKey( e.getFixTag() ) ) {
                FixTagEventMapping newE = new FixTagEventMapping( e );

                if ( newE.getEventAttr() != null ) _tags.put( newE.getEventAttr(), newE );
                if ( newE.getFixTag() != null ) _tagsById.put( newE.getFixTag(), newE );
            }
        }

        _hooks.putAll( value._hooks );
        _rejectTags.addAll( value._rejectTags );

        if ( value._fixDefn != null ) {
            _fixDefn = new FixEventDefinition( value._fixDefn );
        }

        _eventClassDefn = value._eventClassDefn;
    }

    @Override
    public void addHook( HookType type, String code ) {
        _hooks.put( type, code );
    }

    public void addMapping( FixTagEventMapping tagMap ) {
        if ( tagMap.getEventAttr() != null ) _tags.put( tagMap.getEventAttr(), tagMap );
        if ( tagMap.getFixTag() != null ) _tagsById.put( tagMap.getFixTag(), tagMap );
    }

    public void addRejectTag( Integer tag ) {
        _rejectTags.add( tag );
    }

    public void clear() {
        _tags.clear();
        _tagsById.clear();
        _hooks.clear();
        _rejectTags.clear();
    }

    public ClassDefinition getClassDefinition() {
        return _eventClassDefn;
    }

    public void setClassDefinition( ClassDefinition event ) {
        _eventClassDefn = event;
    }

    public FixEventDefinition getEventDefinition() {
        return _fixDefn;
    }

    public String getEventId() {
        return _eventId;
    }

    public FixEventType getFixMsgId() {
        return _fixMsgId;
    }

    public FixTagEventMapping getFixTagEventMapping( String attr ) {
        FixTagEventMapping fte = _tags.get( attr );

        if ( fte == null && _parent != null ) fte = _parent.getFixTagEventMapping( attr );

        return fte;
    }

    public FixTagEventMapping getFixTagEventMapping( String attr, int id ) {
        FixTagEventMapping map = _tags.get( attr );

        if ( map == null ) {
            map = _tagsById.get( "" + id );
        }

        if ( map == null && _parent != null ) map = _parent.getFixTagEventMapping( attr, id );

        return map;
    }

    /**
     * @return COPY of hooks map
     * @NOTE INefficient .. ok for Generator
     */
    public Map<HookType, String> getHooks() {
        Map<HookType, String> copy = new HashMap<>( _hooks );

        if ( _parent != null ) {
            copy.putAll( _parent.getHooks() );
        }

        return copy;
    }

    public String getId() {
        return _id;
    }

    public int getMaxForceTableSwitch()                                   { return _maxForceTableSwitch; }

    public void setMaxForceTableSwitch( final int maxForceTableSwitch )   { _maxForceTableSwitch = maxForceTableSwitch; }

    public int getMaxSubBand()                                            { return _maxSubBand; }

    public void setMaxSubBand( final int maxSubBand )                     { _maxSubBand = maxSubBand; }

    public int getMinSubSwitchPackSize()                                  { return _minSubSwitchPackSize; }

    public void setMinSubSwitchPackSize( final int minSubSwitchPackSize ) { _minSubSwitchPackSize = minSubSwitchPackSize; }

    public FixEventMap getParent() {
        return _parent;
    }

    public void setParent( FixEventMap parent ) {
        _parent = parent;
    }

    public Set<Integer> getRejectTags() {
        Set<Integer> copy = new LinkedHashSet<>( _rejectTags );

        if ( _parent != null ) {
            copy.addAll( _parent.getRejectTags() );
        }

        return copy;
    }

    public boolean isHandWrittenDecode() {
        return _handWrittenDecode;
    }

    public void setHandWrittenDecode( boolean handWrittenDecode ) {
        _handWrittenDecode = handWrittenDecode;
    }

    public boolean isHandWrittenEncode() {
        return _handWrittenEncode;
    }

    public void setHandWrittenEncode( boolean handWrittenEncode ) {
        _handWrittenEncode = handWrittenEncode;
    }

    public boolean isIgnore() {
        return _ignore;
    }

    public boolean isRejectTag( Integer tag ) {
        boolean reject = _rejectTags.contains( tag );

        if ( !reject && _parent != null ) reject = _parent.isRejectTag( tag );

        return reject;
    }

    public void setMessageDefinition( FixEventDefinition fixDefn ) {
        _fixDefn = fixDefn;
    }
}
