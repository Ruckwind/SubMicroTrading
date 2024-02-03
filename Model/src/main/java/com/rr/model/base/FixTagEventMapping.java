/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

import java.util.HashMap;
import java.util.Map;

public class FixTagEventMapping implements HookSupport {

    private final String                _eventAttr;
    private final String                _fixTag;
    private final int                   _maxOccurs;
    private final boolean               _retainSrc;
    private final Map<HookType, String> _hooks = new HashMap<>();

    public FixTagEventMapping( String eventAttr, String fixTag, int maxOccurs, boolean retainSrc ) {
        _eventAttr = eventAttr;
        _fixTag    = fixTag;
        _maxOccurs = maxOccurs;
        _retainSrc = retainSrc;
    }

    public FixTagEventMapping( FixTagEventMapping e ) {
        _eventAttr = e._eventAttr;
        _fixTag    = e._fixTag;
        _maxOccurs = e._maxOccurs;
        _retainSrc = e._retainSrc;
        _hooks.putAll( e._hooks );
    }

    @Override
    public void addHook( HookType type, String code ) {
        _hooks.put( type, code );
    }

    @Override
    public String toString() {
        return "FixTagEventMapping [ eventAttr=" + _eventAttr + ", fixTag=" + _fixTag + ", maxOccurs=" + _maxOccurs + ", retainSrc=" + _retainSrc + "]";
    }

    public String getEventAttr() {
        return _eventAttr;
    }

    public String getFixTag() {
        return _fixTag;
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

    public boolean hasDecodeRelatedHook() {
        return _hooks.containsKey( HookType.decode ) || _hooks.containsKey( HookType.predecode ) || _hooks.containsKey( HookType.postdecode );
    }

    public boolean hasEncodeHook() {
        return _hooks.containsKey( HookType.encode );
    }

    public boolean hasEncodeRelatedHook() {
        return _hooks.containsKey( HookType.encode ) || _hooks.containsKey( HookType.preencode ) || _hooks.containsKey( HookType.postencode );
    }

    public boolean isRetainSrc() {
        return _retainSrc;
    }
}
