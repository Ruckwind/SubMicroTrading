/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

import java.util.*;

public class BinaryEventMap implements HookSupport {

    private final String _id;
    private final String _eventId;
    private final String _binaryMsgId;
    private final String _conditionalKey;
    private final String _conditionalVal;

    private final Map<String, BinaryTagEventMapping> _tags       = new LinkedHashMap<>();
    private final Map<String, BinaryTagEventMapping> _tagsById   = new LinkedHashMap<>();
    private final Map<HookType, String>              _hooks      = new HashMap<>();
    private final Set<Integer>                       _rejectTags = new LinkedHashSet<>();
    private final boolean        _ignore;
    private ClassDefinition       _eventClassDefn;
    private BinaryEventDefinition _binaryDefn;
    private       BinaryEventMap _parent;
    private       String         _decodeSplitAttr;
    private       String         _encodeOverrideMethod;

    public BinaryEventMap( String id, String eventId, String binaryMsgId ) {
        _id             = id;
        _eventId        = eventId;
        _binaryMsgId    = binaryMsgId;
        _ignore         = false;
        _conditionalKey = null;
        _conditionalVal = null;
    }

    public BinaryEventMap( String id, String binaryMsgId, boolean ignore ) {
        _ignore         = ignore;
        _id             = id;
        _binaryMsgId    = binaryMsgId;
        _eventId        = null;
        _conditionalKey = null;
        _conditionalVal = null;
    }

    public BinaryEventMap( BinaryEventMap value ) {
        _id             = value._id;
        _eventId        = value._eventId;
        _ignore         = value._ignore;
        _binaryMsgId    = value._binaryMsgId;
        _parent         = value._parent;
        _conditionalKey = value._conditionalKey;
        _conditionalVal = value._conditionalVal;

        for ( BinaryTagEventMapping e : value._tags.values() ) {
            BinaryTagEventMapping newE = new BinaryTagEventMapping( e );

            _tags.put( newE.getEventAttr(), newE );
            _tagsById.put( newE.getBinaryTag(), newE );
        }

        for ( BinaryTagEventMapping e : value._tagsById.values() ) {
            if ( !_tagsById.containsKey( e.getBinaryTag() ) ) {
                BinaryTagEventMapping newE = new BinaryTagEventMapping( e );

                _tags.put( newE.getEventAttr(), newE );
                _tagsById.put( newE.getBinaryTag(), newE );
            }
        }

        _hooks.putAll( value._hooks );
        _rejectTags.addAll( value._rejectTags );

        if ( value._binaryDefn != null ) {
            _binaryDefn = new BinaryEventDefinition( value._binaryDefn );
        }

        _eventClassDefn  = value._eventClassDefn;
        _decodeSplitAttr = value._decodeSplitAttr;
    }

    public BinaryEventMap( String id, String eventId, String binaryMsgId, String conditionalKey, String conditionalVal ) {
        _ignore         = false;
        _id             = id;
        _binaryMsgId    = binaryMsgId;
        _eventId        = eventId;
        _conditionalKey = conditionalKey;
        _conditionalVal = conditionalVal;
    }

    @Override
    public void addHook( HookType type, String code ) {
        _hooks.put( type, code );
    }

    public void addMapping( BinaryTagEventMapping tagMap ) {

        _tags.put( tagMap.getEventAttr(), tagMap );
        _tagsById.put( tagMap.getBinaryTag(), tagMap );
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

    public boolean decodeRepeatingGroupIntoSeperateMsgs( String eventAttr ) {
        if ( _decodeSplitAttr != null ) return false;

        _decodeSplitAttr = eventAttr;

        return true;
    }

    public String getBinaryMsgId() {
        return _binaryMsgId;
    }

    public BinaryTagEventMapping getBinaryTagEventMapping( String attr ) {
        BinaryTagEventMapping fte = _tags.get( attr );

        if ( fte == null && _parent != null ) fte = _parent.getBinaryTagEventMapping( attr );

        return fte;
    }

    public BinaryTagEventMapping getBinaryTagEventMapping( String attr, String id ) {
        BinaryTagEventMapping map = null;

        if ( attr != null ) {
            map = _tags.get( attr );
        }

        if ( map == null ) {
            map = _tagsById.get( id );
        }

        if ( map == null && _parent != null ) map = _parent.getBinaryTagEventMapping( attr, id );

        return map;
    }

    public ClassDefinition getClassDefinition() {
        return _eventClassDefn;
    }

    public void setClassDefinition( ClassDefinition event ) {
        _eventClassDefn = event;
    }

    public String getConditionalKey() {
        return _conditionalKey;
    }

    public String getConditionalVal() {
        return _conditionalVal;
    }

    public String getDecodeSplitAttr() {
        return _decodeSplitAttr;
    }

    public String getEncodeOverrideMethod() {
        return _encodeOverrideMethod;
    }

    public final void setEncodeOverrideMethod( String encoderConditionalMethod ) {
        _encodeOverrideMethod = encoderConditionalMethod;
    }

    public BinaryEventDefinition getEventDefinition() {
        return _binaryDefn;
    }

    public String getEventId() {
        return _eventId;
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

    public BinaryEventMap getParent() {
        return _parent;
    }

    public void setParent( BinaryEventMap parent ) {
        _parent = parent;
    }

    public Set<Integer> getRejectTags() {
        Set<Integer> copy = new LinkedHashSet<>( _rejectTags );

        if ( _parent != null ) {
            copy.addAll( _parent.getRejectTags() );
        }

        return copy;
    }

    public Map<String, BinaryTagEventMapping> getTags() {
        return _tags;
    }

    public Map<String, BinaryTagEventMapping> getTagsById() {
        return _tagsById;
    }

    public boolean isIgnore() {
        return _ignore;
    }

    public boolean isRejectTag( Integer tag ) {
        boolean reject = _rejectTags.contains( tag );

        if ( !reject && _parent != null ) reject = _parent.isRejectTag( tag );

        return reject;
    }

    public boolean isSubEvent() {
        return (_eventClassDefn != null && _eventClassDefn.isSubEvent());
    }

    public void setMessageDefinition( BinaryEventDefinition BinaryDefn ) {
        _binaryDefn = BinaryDefn;
    }
}
