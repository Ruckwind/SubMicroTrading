/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.generator;

import com.rr.model.base.HookSupport;
import com.rr.model.base.HookType;
import com.rr.model.base.TypeDefinition;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class TypeTransform implements HookSupport {

    private final String                _id;
    private final TypeDefinition        _td;
    private final Map<HookType, String> _hooks                 = new HashMap<>();
    private final Map<String, String>   _internalToExternal    = new LinkedHashMap<>();
    private final Map<String, String>   _externalToInternal    = new LinkedHashMap<>();
    private final Map<String, String>   _priorityDecode        = new LinkedHashMap<>();
    private final Map<String, String>   _extValComments        = new HashMap<>();
    private       boolean               _externalBinaryFormat  = false;
    private       boolean               _rejectUnmatchedEncode = false;
    private       boolean               _rejectUnmatchedDecode = false;
    private       String                _defaultValEncode      = null;
    private       String                _defaultValDecode      = null;

    public TypeTransform( String id, TypeDefinition td ) {
        _id = id;
        _td = td;
    }

    public TypeTransform( TypeTransform from ) {
        _id = from._id;
        _td = from._td;
        _hooks.putAll( from._hooks );
        _externalBinaryFormat  = from._externalBinaryFormat;
        _rejectUnmatchedEncode = from._rejectUnmatchedEncode;
        _rejectUnmatchedDecode = from._rejectUnmatchedDecode;
        _defaultValEncode      = from._defaultValEncode;
        _defaultValDecode      = from._defaultValDecode;
        _internalToExternal.putAll( from._internalToExternal );
        _externalToInternal.putAll( from._externalToInternal );
        _priorityDecode.putAll( from._priorityDecode );
        _extValComments.putAll( from._extValComments );
    }

    @Override
    public void addHook( HookType type, String code ) {
        _hooks.put( type, code );
    }

    public String getComment( String internalVal ) {
        String c = _extValComments.get( internalVal );

        return c;
    }

    public Set<String> getDecodeExternalVals() {
        return _externalToInternal.keySet();
    }

    public String getDefaultValDecode() {
        return _defaultValDecode;
    }

    public void setDefaultValDecode( String defaultValDecode ) {
        _defaultValDecode = defaultValDecode;
    }

    public String getDefaultValEncode() {
        return _defaultValEncode;
    }

    public void setDefaultValEncode( String defaultValEncode ) {
        _defaultValEncode = defaultValEncode;
    }

    public Set<String> getEncodeInternalVals() {
        return _internalToExternal.keySet();
    }

    public String getExternalVal( String internalVal ) {
        String extVal = _internalToExternal.get( internalVal );

        if ( extVal == null ) extVal = _defaultValEncode;

        return extVal;
    }

    public Map<HookType, String> getHooks() {
        return _hooks;
    }

    public String getId() {
        return _id;
    }

    public String getInternalVal( String externalVal ) {
        String intVal = _externalToInternal.get( externalVal );

        if ( intVal == null ) intVal = _defaultValDecode;

        return intVal;
    }

    public int getMaxExternalEntryValueLen() {
        Set<String> externalVals = getDecodeExternalVals();
        int         max          = 0;
        if ( _externalBinaryFormat ) {
            for ( String externalVal : externalVals ) {
                int val = externalVal.substring( 2 ).length() / 2;
                if ( val > max ) max = val;
            }
        } else {
            for ( String externalVal : externalVals ) {
                int val = externalVal.length();
                if ( val > max ) max = val;
            }
        }
        return max;
    }

    public String getPriorityDecode( String extVal ) {
        return _priorityDecode.get( extVal );
    }

    public TypeDefinition getTypeDefinition() {
        return _td;
    }

    public boolean hasDecodeHook() {
        return _hooks.containsKey( HookType.decode );
    }

    public boolean hasEncodeHook() {
        return _hooks.containsKey( HookType.encode );
    }

    public boolean isExternalBinaryFormat() {
        return _externalBinaryFormat;
    }

    public void setExternalBinaryFormat( boolean isBinary ) {
        _externalBinaryFormat = isBinary;
    }

    public boolean isRejectUnmatchedDecode() {
        return _rejectUnmatchedDecode;
    }

    public void setRejectUnmatchedDecode( boolean rejectUnmatchedDecode ) {
        _rejectUnmatchedDecode = rejectUnmatchedDecode;
    }

    public boolean isRejectUnmatchedEncode() {
        return _rejectUnmatchedEncode;
    }

    public void setRejectUnmatchedEncode( boolean rejectUnmatchedEncode ) {
        _rejectUnmatchedEncode = rejectUnmatchedEncode;
    }

    /**
     * add transform entry
     * <p>
     * consider for sample internal value's Limit, LimitOrBetter, LimitWithOrWithOut may all map to same external value
     * when need map from external value need determine which internal value to use .. this is what decodePriority identifies
     */
    public void map( String internalVal, String externalVal, String comment, boolean decodePriority ) throws Exception {
        _internalToExternal.put( internalVal, externalVal );

        String existingPriorityInternalVal = _priorityDecode.get( externalVal );
        if ( decodePriority ) {
            if ( _priorityDecode.containsKey( externalVal ) ) {
                throw new Exception( "Only one transform mapping for externalVal=" + externalVal + " can be marked as decode priority " +
                                     ", marked for both internalVals " + internalVal + " and " + existingPriorityInternalVal );
            }
            _priorityDecode.put( externalVal, internalVal );
        }
        if ( existingPriorityInternalVal == null ) {
            _externalToInternal.put( externalVal, internalVal );
        }
        _extValComments.put( internalVal, comment );
    }
}
