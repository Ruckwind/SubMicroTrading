/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

import com.rr.model.generator.FixEventType;
import com.rr.model.generator.TypeTransform;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class CodecModel extends BaseModel {

    public enum Type {Base, Client, Exchange}

    private final Map<String, CodecDefinition> _codecs = new LinkedHashMap<>();

    @SuppressWarnings( "unused" )
    private final Type _type;

    private final StringBuilder _errors = new StringBuilder();
    private final CodecModel    _parentModel;

    public CodecModel( Type type ) {
        _type        = type;
        _parentModel = null;
    }

    public CodecModel( Type type, CodecModel codecs ) {
        _type        = type;
        _parentModel = codecs;
    }

    public void addCodecDefinition( CodecDefinition codec ) {

        _codecs.put( codec.getId(), codec );
    }

    public void clearErrors() {
        _errors.setLength( 0 );
    }

    public CodecDefinition getCodec( String id ) {
        if ( id == null ) return null;

        CodecDefinition codec = _codecs.get( id );

        if ( codec == null && _parentModel != null ) {
            codec = _parentModel.getCodec( id );
        }

        return codec;
    }

    public Collection<CodecDefinition> getCodecs() {

        return _codecs.values();
    }

    public String getErrors() {
        return _errors.toString();
    }

    public boolean verify( InternalModel internal, FixModels fix ) {
        _errors.setLength( 0 );

        boolean valid = true;

        for ( CodecDefinition codec : _codecs.values() ) {
            // verify the fixId in the codec is valid

            FixModel fixModel = fix.getFixModel( codec.getFixId() );

            if ( fixModel == null ) {
                _errors.append( "Codec " ).append( codec.getId() ).append( " fixModel " ).append( codec.getFixId() ).append( " doesnt exist\n" );
                valid = false;
            }
            // verify the typeId in the complexType is valid

            Map<Integer, String> complexTypes = codec.getComplexTypes();

            for ( Map.Entry<Integer, String> entry : complexTypes.entrySet() ) {
                Integer tag      = entry.getKey();
                String  typeName = entry.getValue();

                TypeDefinition defn = internal.getTypeDefinition( typeName );

                if ( defn == null ) {
                    valid = false;
                    _errors.append( "Codec " ).append( codec.getId() ).append( " complexType " ).append( typeName ).append( " tag=" ).append( tag )
                           .append( " not defined\n" );
                }
            }

            //          <MessageMap id="NOS" eventId="NewOrderSingle" fixMessageId="NewOrderSingle">

            for ( FixEventMap map : codec.getFixEventMaps() ) {
                String       eventId  = map.getEventId();
                FixEventType fixMsgId = map.getFixMsgId();

                if ( !map.isIgnore() ) {
                    ClassDefinition event = internal.getClassDefinition( eventId );

                    if ( event == null ) {
                        valid = false;
                        _errors.append( "Codec " ).append( codec.getId() ).append( ", messageMap=" ).append( map.getId() ).append( ", eventId=" )
                               .append( eventId ).append( ", fixMsgId=" ).append( fixMsgId ).append( "  event doesnt exist\n" );
                    }

                    map.setClassDefinition( event );
                }

                FixEventDefinition fixDefn = null;

                if ( fixModel != null ) {
                    fixDefn = fixModel.getFixMessage( fixMsgId );

                    if ( fixDefn == null ) fixDefn = fixModel.getSubFixMessage( fixMsgId.toString() );
                }

                if ( fixDefn == null && !map.isIgnore() ) {
                    valid = false;
                    _errors.append( "Codec " ).append( codec.getId() ).append( ", messageMap=" ).append( map.getId() ).append( ", eventId=" ).append( eventId )
                           .append( ", fixMsgId=" ).append( fixMsgId ).append( "  fixMsg doesnt exist in fixModel\n" );
                }

                map.setMessageDefinition( fixDefn );
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
