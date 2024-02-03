/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

import com.rr.model.generator.DecoderGenerator;
import com.rr.model.generator.TypeTransform;

import java.util.*;

public class BinaryCodecDefinition implements TypeTransforms {

    private final String _id;
    private final String _binaryModelId;
    private final String _extendsCodec;

    private final CodecType _cType;

    private final Map<String, BinaryEventMap> _msgMapsByEventId  = new LinkedHashMap<>( 64 );
    private final Map<String, BinaryEventMap> _binaryMsgMapsById = new LinkedHashMap<>( 64 );
    private final Map<String, TypeTransform>  _typeTransforms    = new HashMap<>();

    private String _decodeInclude = null;
    private String _encodeInclude = null;

    private Class<DecoderGenerator> _decodeGenerator;
    private Class<EncoderGenerator> _encodeGenerator;
    private boolean                 _isAbstract = false;

    public BinaryCodecDefinition( String id, String binaryModelId, CodecType cType, String extendsCodec ) {
        _id            = id;
        _binaryModelId = binaryModelId;
        _cType         = cType;
        _extendsCodec  = extendsCodec;
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
        return "CodecDefinition [ id=" + _id + ", cType=" + _cType + ", extends=" + _extendsCodec + ", BinaryId=" + _binaryModelId + "]";
    }

    public void addMessageMap( BinaryEventMap map ) {
        if ( map.getEventId() != null ) {
            _msgMapsByEventId.put( map.getEventId(), map );
        }
        _binaryMsgMapsById.put( map.getId(), map );
    }

    public void copy( BinaryCodecDefinition base ) {

        if ( base != null ) {
            _decodeInclude = base._decodeInclude;
            _encodeInclude = base._encodeInclude;

            for ( Map.Entry<String, BinaryEventMap> entry : base._binaryMsgMapsById.entrySet() ) {
                BinaryEventMap def    = entry.getValue();
                BinaryEventMap newDef = new BinaryEventMap( def );

                if ( newDef.getEventId() != null ) {
                    _msgMapsByEventId.put( newDef.getEventId(), newDef );
                }
                _binaryMsgMapsById.put( newDef.getId(), newDef );
            }

            // update parent in copied BinaryMessage to point to the new instances owned by this model
            for ( BinaryEventMap def : _binaryMsgMapsById.values() ) {
                BinaryEventMap parent = def.getParent();

                if ( parent != null ) {
                    BinaryEventMap newParent = _binaryMsgMapsById.get( parent.getId() );
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

    public BinaryEventMap getBinaryEventMapById( String id ) {
        return _binaryMsgMapsById.get( id );
    }

    public Collection<BinaryEventMap> getBinaryEventMaps() {
        return _binaryMsgMapsById.values();
    }

    public String getBinaryModelId() {
        return _binaryModelId;
    }

    public String getDecodeInclude() {
        return _decodeInclude;
    }

    public void setDecodeInclude( String decodeInclude ) {
        _decodeInclude = decodeInclude;
    }

    public Class<DecoderGenerator> getDecoderGenerator() {
        return _decodeGenerator;
    }

    public String getEncodeInclude() {
        return _encodeInclude;
    }

    public void setEncodeInclude( String encodeInclude ) {
        _encodeInclude = encodeInclude;
    }

    public Class<EncoderGenerator> getEncoderGenerator() {
        return _encodeGenerator;
    }

    public BinaryEventMap getMessageMapByEventId( String eventId ) {
        return _msgMapsByEventId.get( eventId );
    }

    public boolean isAbstract() {
        return _isAbstract;
    }

    public void setAbstract( boolean isAbstract ) {
        _isAbstract = isAbstract;
    }

    @SuppressWarnings( "unchecked" )
    public void setDecodeGenerator( String decodeGenClassName ) throws ClassNotFoundException {
        _decodeGenerator = (Class<DecoderGenerator>) Class.forName( decodeGenClassName );
    }

    @SuppressWarnings( "unchecked" )
    public void setEncodeGenerator( String encodeGenClassName ) throws ClassNotFoundException {
        _encodeGenerator = (Class<EncoderGenerator>) Class.forName( encodeGenClassName );
    }
}
