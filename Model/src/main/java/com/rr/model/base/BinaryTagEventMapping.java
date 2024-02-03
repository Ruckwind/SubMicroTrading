/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

import java.util.HashMap;
import java.util.Map;

public class BinaryTagEventMapping implements HookSupport {

    private final String _eventAttr;
    private final String _binaryTag;
    private final int    _maxOccurs;
    private final int    _fixedWidth; // only for fixed width string fields

    private final Map<HookType, String> _hooks = new HashMap<>();

    public BinaryTagEventMapping( String eventAttr, String binaryTag, int maxOccurs, int fixedWidth ) {
        _eventAttr  = eventAttr;
        _binaryTag  = binaryTag;
        _maxOccurs  = maxOccurs;
        _fixedWidth = fixedWidth;
    }

    public BinaryTagEventMapping( BinaryTagEventMapping e ) {
        _eventAttr  = e._eventAttr;
        _binaryTag  = e._binaryTag;
        _maxOccurs  = e._maxOccurs;
        _fixedWidth = e._fixedWidth;
        _hooks.putAll( e._hooks );
    }

    @Override
    public void addHook( HookType type, String code ) {
        _hooks.put( type, code );
    }

    @Override
    public String toString() {
        return "BinaryTagEventMapping [ eventAttr=" + _eventAttr + ", BinaryTag=" + _binaryTag + ", maxOccurs=" + _maxOccurs + "]";
    }

    public String getBinaryTag() {
        return _binaryTag;
    }

    public String getEventAttr() {
        return _eventAttr;
    }

    public int getFixedWidth() {
        return _fixedWidth;
    }

    public Map<HookType, String> getHooks() {
        return _hooks;
    }

    public int getMaxOccurs() {
        return _maxOccurs;
    }

    public boolean hasDecodeHook() {
        return _hooks.containsKey( HookType.decode );
    }

    public boolean hasEncodeHook() {
        return _hooks.containsKey( HookType.encode );
    }
}
