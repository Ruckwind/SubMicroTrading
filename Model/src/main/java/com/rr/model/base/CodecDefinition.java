/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

import com.rr.model.generator.*;

import java.util.*;

public class CodecDefinition implements TypeTransforms {

    private final String _id;
    private final String _fixId;
    private final String _extendsCodec;

    private final CodecType _cType;

    private final Map<Integer, String>           _complexTypes   = new LinkedHashMap<>();
    private final Map<FixEventType, FixEventMap> _fixMsgMaps     = new LinkedHashMap<>( 64 );
    private final Map<String, FixEventMap>       _fixMsgMapsById = new LinkedHashMap<>( 64 );
    private final Map<String, TypeTransform>     _typeTransforms = new HashMap<>();

    private String                               _decodeInclude   = null;
    private String                               _encodeInclude   = null;
    private String                               _encodeBuilder   = null;
    private Class<? extends FixDecoderGenerator> _decodeGenerator = BaseFixDecoderGenerator.class;
    private Class<? extends FixEncoderGenerator> _encodeGenerator = BaseFixEncoderGenerator.class;

    public CodecDefinition( String id, String fixId, CodecType cType, String extendsCodec ) {
        _id           = id;
        _fixId        = fixId;
        _cType        = cType;
        _extendsCodec = extendsCodec;
    }

    @Override
    public String getId() {
        return _id;
    }

    @Override
    public void addTypeTransform( String typeId, TypeTransform t ) {
        _typeTransforms.put( typeId, t );
    }

    @Override
    public TypeTransform getTypeTransform( String typeId ) {
        return _typeTransforms.get( typeId );
    }

    @Override
    public Set<String> getTypeTransformIds() {
        return _typeTransforms.keySet();
    }

    @Override
    public String toString() {
        return "CodecDefinition [ id=" + _id + ", cType=" + _cType + ", extends=" + _extendsCodec +
               ", complexTypes=" + _complexTypes + ", fixId=" + _fixId + "]";
    }

    public void addComplexType( int iTag, String typeId ) {
        _complexTypes.put( iTag, typeId );
    }

    public void addMessageMap( FixEventMap map ) {
        _fixMsgMaps.put( map.getFixMsgId(), map );
        _fixMsgMapsById.put( map.getId(), map );
    }

    public void copy( CodecDefinition base ) {

        if ( base != null ) {
            _complexTypes.putAll( base._complexTypes );
            _decodeInclude = base._decodeInclude;
            _encodeInclude = base._encodeInclude;
            _encodeBuilder = base._encodeBuilder;

            for ( Map.Entry<FixEventType, FixEventMap> entry : base._fixMsgMaps.entrySet() ) {
                FixEventMap def    = entry.getValue();
                FixEventMap newDef = new FixEventMap( def );

                _fixMsgMaps.put( entry.getKey(), newDef );
                _fixMsgMapsById.put( newDef.getId(), newDef );
            }

            // update parent in copied FixMessage to point to the new instances owned by this model
            for ( FixEventMap def : _fixMsgMaps.values() ) {
                FixEventMap parent = def.getParent();

                if ( parent != null ) {
                    FixEventMap newParent = _fixMsgMapsById.get( parent.getId() );
                    if ( newParent != null ) {
                        def.setParent( newParent );
                    }
                }
            }

            for ( Map.Entry<String, TypeTransform> entry : base._typeTransforms.entrySet() ) {
                TypeTransform copy = new TypeTransform( entry.getValue() );

                _typeTransforms.put( entry.getKey(), copy );
            }
        }
    }

    public Map<Integer, String> getComplexTypes() {
        return _complexTypes;
    }

    public String getDecodeInclude() {
        return _decodeInclude;
    }

    public void setDecodeInclude( String decodeInclude ) {
        _decodeInclude = decodeInclude;
    }

    public Class<? extends FixDecoderGenerator> getDecoderGenerator() {
        return _decodeGenerator;
    }

    public String getEncodeBuilder() {
        return _encodeBuilder;
    }

    public void setEncodeBuilder( String encodeBuilder ) {
        _encodeBuilder = encodeBuilder;
    }

    public String getEncodeInclude() {
        return _encodeInclude;
    }

    public void setEncodeInclude( String encodeInclude ) {
        _encodeInclude = encodeInclude;
    }

    public Class<? extends FixEncoderGenerator> getEncoderGenerator() {
        return _encodeGenerator;
    }

    public FixEventMap getFixEventMap( FixEventType fixMsgId ) {
        return _fixMsgMaps.get( fixMsgId );
    }

    public FixEventMap getFixEventMap( String id ) {
        return _fixMsgMapsById.get( id );
    }

    public Collection<FixEventMap> getFixEventMaps() {
        return _fixMsgMaps.values();
    }

    public String getFixId() {
        return _fixId;
    }

    public boolean hasOMSEvents() {
        Collection<FixEventMap> fixMaps = getFixEventMaps();

        for ( FixEventMap map : fixMaps ) {
            String eventId = map.getEventId();

            if ( eventId == null ) continue;

            ClassDefinition event = map.getClassDefinition();
            EventStreamSrc  src   = event.getStreamSrc();

            if ( src == EventStreamSrc.client ) return true;
            if ( src == EventStreamSrc.exchange ) return true;
        }

        return false;
    }

    @SuppressWarnings( "unchecked" )
    public void setDecodeGenerator( String decodeGenClassName ) throws ClassNotFoundException {
        _decodeGenerator = (Class<FixDecoderGenerator>) Class.forName( decodeGenClassName );
    }

    @SuppressWarnings( "unchecked" )
    public void setEncodeGenerator( String encodeGenClassName ) throws ClassNotFoundException {
        _encodeGenerator = (Class<FixEncoderGenerator>) Class.forName( encodeGenClassName );
    }
}
