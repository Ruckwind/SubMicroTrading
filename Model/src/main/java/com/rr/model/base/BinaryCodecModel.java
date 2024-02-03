/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

import com.rr.model.generator.TypeTransform;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class BinaryCodecModel extends BaseModel {

    private final Map<String, BinaryCodecDefinition>      _codecs     = new LinkedHashMap<>();
    private final Map<String, HandCraftedCodecDefinition> _handCodecs = new LinkedHashMap<>();

    private final StringBuilder    _errors = new StringBuilder();
    private final BinaryCodecModel _parentModel;

    public BinaryCodecModel() {
        _parentModel = null;
    }

    public BinaryCodecModel( BinaryCodecModel codecs ) {
        _parentModel = codecs;
    }

    public void addBinaryCodecDefinition( BinaryCodecDefinition codec ) {
        _codecs.put( codec.getId(), codec );
    }

    public void addHandCraftedBinaryCodecDefinition( HandCraftedCodecDefinition codec ) {
        _handCodecs.put( codec.getId(), codec );
    }

    public void clearErrors() {
        _errors.setLength( 0 );
    }

    public BinaryCodecDefinition getCodec( String id ) {
        if ( id == null ) return null;

        BinaryCodecDefinition codec = _codecs.get( id );

        if ( codec == null && _parentModel != null ) {
            codec = _parentModel.getCodec( id );
        }

        return codec;
    }

    public Collection<BinaryCodecDefinition> getCodecs() {
        return _codecs.values();
    }

    public String getErrors() {
        return _errors.toString();
    }

    public Collection<HandCraftedCodecDefinition> getHandCodecs() {
        return _handCodecs.values();
    }

    public boolean verify( InternalModel internal, BinaryModels Binary ) {
        _errors.setLength( 0 );

        boolean valid = true;

        for ( BinaryCodecDefinition codec : _codecs.values() ) {
            // verify the BinaryId in the codec is valid

            BinaryModel BinaryModel = Binary.getBinaryModel( codec.getBinaryModelId() );

            if ( BinaryModel == null ) {
                _errors.append( "Codec " ).append( codec.getId() ).append( " BinaryModel " ).append( codec.getBinaryModelId() ).append( " doesnt exist\n" );
                valid = false;
            }

            for ( BinaryEventMap map : codec.getBinaryEventMaps() ) {
                String eventId     = map.getEventId();
                String BinaryMsgId = map.getBinaryMsgId();

                if ( !map.isIgnore() ) {
                    ClassDefinition event = internal.getClassDefinition( eventId );

                    if ( event == null ) {
                        valid = false;
                        _errors.append( "Codec " ).append( codec.getId() ).append( ", messageMap=" ).append( map.getId() ).append( ", eventId=" )
                               .append( eventId ).append( ", BinaryMsgId=" ).append( BinaryMsgId ).append( "  event doesnt exist\n" );
                    }

                    map.setClassDefinition( event );
                }

                BinaryEventDefinition BinaryDefn = null;

                if ( BinaryModel != null ) BinaryDefn = BinaryModel.getBinaryEvent( BinaryMsgId );

                if ( BinaryDefn == null && !map.isIgnore() ) {
                    valid = false;
                    _errors.append( "Codec " ).append( codec.getId() ).append( ", messageMap=" ).append( map.getId() ).append( ", eventId=" ).append( eventId )
                           .append( ", BinaryMsgId=" ).append( BinaryMsgId ).append( "  BinaryMsg doesnt exist in BinaryModel\n" );
                }

                map.setMessageDefinition( BinaryDefn );
            }

            valid = verifyTransforms( valid, codec );
        }

        return valid;
    }

    private boolean verifyTransforms( boolean valid, TypeTransforms codec ) {
        for ( String trId : codec.getTypeTransformIds() ) {
            TypeTransform tr = codec.getTypeTransform( trId );

            // for each external value, if count of matched internal vals > 1, check a decodePriority assigned
            for ( String extVal : tr.getDecodeExternalVals() ) {
                int cntExtMatches = 0;
                for ( String intVal : tr.getEncodeInternalVals() ) {
                    String extValTmp = tr.getExternalVal( intVal );
                    if ( extVal.equals( extValTmp ) ) {
                        ++cntExtMatches;
                    }
                }
                if ( cntExtMatches > 1 ) {
                    if ( tr.getPriorityDecode( extVal ) == null ) {
                        valid = false;
                        _errors.append( "Codec " ).append( codec.getId() ).append( ", transform=" ).append( trId )
                               .append( ", cannot determine which internalVal to map for externalVal=" ).append( extVal )
                               .append( ", need flag one mapping with decodePriority=\"Y\"\n" );
                    }
                }
            }
        }
        return valid;
    }
}
