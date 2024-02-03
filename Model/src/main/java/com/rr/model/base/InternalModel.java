/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

import com.rr.model.base.ClassDefinition.Type;

import java.util.*;

@SuppressWarnings( "MismatchedQueryAndUpdateOfCollection" ) public class InternalModel extends BaseModel {

    private Map<String, Integer>          _defaultSizes = new LinkedHashMap<>();
    private Set<InternalModelInstruction> _instructions = EnumSet.noneOf( InternalModelInstruction.class );
    private Map<String, ClassDefinition>  _classes      = new LinkedHashMap<>();
    private Map<String, TypeDefinition>   _types        = new LinkedHashMap<>();
    private StringBuilder                 _errors       = new StringBuilder();

    public InternalModel() {
        //
    }

    public void addClassDefinition( ClassDefinition defn ) {
        _classes.put( defn.getId(), defn );
    }

    public void addDefaultSize( String id, int len ) {
        _defaultSizes.put( id.toUpperCase(), len );
    }

    /**
     * @param inst
     * @return false if not valid instruction
     */
    public void addInstruction( InternalModelInstruction inst ) {
        _instructions.add( inst );
    }

    public void addTypeDefinition( TypeDefinition type ) {
        _types.put( type.getId(), type );
    }

    public void clearErrors() {
        _errors.setLength( 0 );
    }

    public ClassDefinition getClassDefinition( String id ) {
        return _classes.get( id );
    }

    public Collection<ClassDefinition> getClassDefinitions() {
        return _classes.values();
    }

    public int getDefaultSize( String sizeStr ) {

        Integer size = _defaultSizes.get( sizeStr.toUpperCase() );
        int     iSize;

        if ( size == null ) {
            iSize = Integer.parseInt( sizeStr );
        } else {
            iSize = size;
        }

        return iSize;
    }

    public Map<String, Integer> getDefaultSizes() {
        return _defaultSizes;
    }

    public String getErrors() {
        return _errors.toString();
    }

    public TypeDefinition getTypeDefinition( String id ) {
        return _types.get( id );
    }

    public Collection<TypeDefinition> getTypeDefinitions() {
        return _types.values();
    }

    public boolean isSubElement( String Id ) {
        ClassDefinition cd = getClassDefinition( Id );

        return cd != null && cd.getType() == Type.SubEvent;
    }

    public boolean verify() {

        boolean valid = true;

        _errors.setLength( 0 );

        // check that the typeId in TypeDefination attribute list matches another type entry or is primitive

        for ( TypeDefinition type : _types.values() ) {

            Collection<AttributeDefinition> attributes = type.getAttributes();

            if ( attributes != null ) {

                for ( AttributeDefinition attr : attributes ) {

                    if ( !attr.isPrimitive() ) {

                        TypeDefinition attrType = _types.get( attr.getTypeId() );

                        if ( attrType == null ) {
                            valid = false;

                            _errors.append( "TypeId " ).append( attr.getTypeId() ).append( " definition is missing, referenced from attr " )
                                   .append( attr.getAttrName() ).append( " in type " ).append( type.getId() ).append( "\n" );
                        } else {
                            attr.setType( attrType );
                        }
                    }
                }
            }
        }

        // check that every event base does exist

        for ( ClassDefinition defn : _classes.values() ) {
            String base = defn.getBase();

            if ( base != null ) {
                ClassDefinition baseDef = _classes.get( base );

                if ( baseDef == null ) {
                    valid = false;

                    _errors.append( "Class " ).append( defn.getId() ).append( " extends " ).append( base ).append( " which is undefined\n" );
                } else {
                    defn.setBaseDefinition( baseDef );
                }
            }

            Collection<AttributeDefinition> attributes = defn.getAttributes( true );

            if ( attributes != null ) {

                // check that typeId in events is valid type or int/string etc

                for ( AttributeDefinition attr : attributes ) {

                    if ( attr instanceof SubEventAttributeDefinition ) {
                        ClassDefinition cd = getClassDefinition( attr.getTypeId() );
                        if ( cd == null ) {
                            valid = false;

                            _errors.append( "TypeId " ).append( attr.getTypeId() ).append( " SubEvent definition is missing, used by attr " )
                                   .append( attr.getAttrName() ).append( " in event " ).append( defn.getId() ).append( "\n" );
                        } else {
                            ((SubEventAttributeDefinition) attr).setDefinition( cd );

                            SubEventAttrType subEventType = new SubEventAttrType( attr.getTypeId() );
                            subEventType.setCd( cd );

                            attr.setType( subEventType );
                        }
                    } else if ( !attr.isPrimitive() ) {

                        TypeDefinition attrType = _types.get( attr.getTypeId() );

                        if ( attrType == null ) {
                            valid = false;

                            _errors.append( "TypeId " ).append( attr.getTypeId() ).append( " definition is missing, referenced from attr " )
                                   .append( attr.getAttrName() ).append( " in class " ).append( defn.getId() ).append( "\n" );
                        } else {
                            attr.setType( attrType );
                        }
                    }
                }
            }

        }

        // check every type has an Unknown entry

        for ( TypeDefinition type : _types.values() ) {

            if ( !type.containsEntry( "Unknown" ) ) {
                type.addUnknown();
            }
        }

        return valid;
    }
}
