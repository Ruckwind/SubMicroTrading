/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.generator;

/*
issue is how to handle client and market side events ... ideally support multiple models

a) use same event for both .. simply add extra fields as needed  -> only works for 1-1 

b) have client and mkt side and delegate, .. in XML dont have both .. 
have just NewOrderSingle and add attr to attribute to denote delegate  on mktSide,
also need attr in message to denote if client or mkt side
Attrs not marked as delegate will go in the MarketUpdate interface

c) need 
*/

import com.rr.core.lang.ReusableCategoryEnum;
import com.rr.core.utils.FileException;
import com.rr.model.base.*;
import com.rr.model.base.ClassDefinition.Type;
import com.rr.model.base.type.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class InternalModelGenerator {

    private static final char MAIN_SEP = ',';
    private static final char SUB_SEP  = ';';

    private final InternalModel _internal;
    private final String        _constantImport;

    public static void addInternalEventsFactoryWildImport( StringBuilder b, InternalModel internal ) {
        b.append( "import " ).append( internal.getRootPackage() ).append( "." ).append( ModelConstants.EVENT_PACKAGE ).append( "." )
         .append( ModelConstants.FACTORY_PACKAGE ).append( ".*;\n" );
    }

    public static void addInternalEventsImplWildImport( StringBuilder b, InternalModel internal ) {
        b.append( "import " ).append( internal.getRootPackage() ).append( "." ).append( ModelConstants.EVENT_PACKAGE ).append( "." )
         .append( ModelConstants.IMPL_PACKAGE ).append( ".*;\n" );
    }

    /**
     * TYPE GENERATION 
     */

    public static void addInternalTypeWildImport( StringBuilder b, InternalModel internal ) {
        b.append( "import " ).append( internal.getRootPackage() ).append( "." ).append( ModelConstants.TYPE_PACKAGE ).append( ".*;\n" );
    }

    public static void addInternalEventsInterfacesWildImport( StringBuilder b, InternalModel internal ) {
        b.append( "import " ).append( internal.getRootPackage() ).append( "." ).append( ModelConstants.EVENT_PACKAGE ).append( "." )
         .append( ModelConstants.INTERFACES_PACKAGE ).append( ".*;\n" );
    }

    public static void addInternalEventsCoreSizeTypeImport( StringBuilder b, InternalModel internal ) {
        b.append( "import " ).append( internal.getRootPackage() ).append( "." ).append( ModelConstants.CORE_PACKAGE ).append( "." )
         .append( ModelConstants.SIZE_TYPE_FILE_NAME ).append( ";\n" );
    }

    public static void addInternalEventsCoreEventIdsImport( StringBuilder b, InternalModel internal ) {
        b.append( "import " ).append( internal.getRootPackage() ).append( "." ).append( ModelConstants.CORE_PACKAGE ).append( "." )
         .append( ModelConstants.EVENT_ID_FILE_NAME ).append( ";\n" );
    }

    public static void addInternalEventsCoreFullEventIdsImport( StringBuilder b, InternalModel internal ) {
        b.append( "import " ).append( internal.getRootPackage() ).append( "." ).append( ModelConstants.CORE_PACKAGE ).append( "." )
         .append( ModelConstants.FULL_EVENT_ID_FILE_NAME ).append( ";\n" );
    }

    public InternalModelGenerator( Model model ) {
        _internal = model.getInternal();

        _constantImport = _internal.getRootPackage() + "." + ModelConstants.CORE_PACKAGE + "." + ModelConstants.SIZE_TYPE_FILE_NAME;
    }

    public void generate() throws Exception {
        GenUtils.makeVersionDirectory( _internal );
        GenUtils.createRootPackageDir( _internal );

        generateConstants();
        generateFullEventIds();
        generateEventIds();
        generateReusableTypes();
        generateTypeIds();
        generateTypes();
        generateSubEvents();
        generateEvents();
        generateFactories();
        generateUtils();
    }

    public String getConstantImport() {
        return _constantImport;
    }

    private void addAnAttrMethods( EventStreamSrc src, String eventId, AttributeDefinition attr, StringBuilder b, boolean isInterface, boolean addGetter,
                                   boolean addSetter, DelegateType delegate, String attrName, String methodBase, boolean isSrcSide, boolean attrInherited,
                                   final OutboundInstruction inst, boolean isUpdInt ) {

        if ( delegate == DelegateType.EmptyDelegateThrowException ) {
            addSetter = false;
        }

        if ( attr.isPrimitive() ) {

            PrimitiveType type = (PrimitiveType) attr.getType();
            int           size = type.getArraySize();

            String typeDef = attr.getType().getTypeDefinition();

            boolean overrideViewStringToReusableString = checkOverrideAsReusableString( src, attr, isSrcSide );

            if ( attr.getType().getClass() == ReusableStringType.class || overrideViewStringToReusableString ) {
                addReusableStringMethods( src, b, eventId, isInterface, addGetter, addSetter, delegate, attrName, methodBase, attrInherited, inst, isUpdInt, attr.isForceOverride() );

            } else if ( attr.getType().getClass() == ViewStringType.class ) {
                addViewStringMethods( src, b, eventId, isInterface, addGetter, addSetter, delegate, attrName, methodBase, attrInherited, inst, isUpdInt, attr.isForceOverride() );

            } else if ( size > 1 ) {

                throw new RuntimeException( "Array types not yet implemented " + attrName + ", type=" + type.getClass() + ", len=" + size );

            } else {
                String desc = attr.getDesc();
                addPrimitiveMethods( src, b, eventId, typeDef, isInterface, addGetter, addSetter, delegate, attrName, methodBase, attrInherited, inst, isUpdInt, desc, attr.isForceOverride() );
            }

        } else {
            String typeDef = attr.getTypeId();
            addTypeMethods( src, b, eventId, typeDef, isInterface, addGetter, addSetter, delegate, attrName, methodBase, attrInherited, isUpdInt, attr.isForceOverride() );
        }
    }

    private void addClassImports( EventStreamSrc src,
                                  StringBuilder b,
                                  ClassDefinition def,
                                  boolean isInterface,
                                  boolean isUpdateInterface,
                                  boolean isSrcSide,
                                  boolean isSrcClient ) {

        Set<String> importSet = new HashSet<>();

        for ( AttributeDefinition attr : def.getAttributes( !isInterface || isUpdateInterface ) ) {
            if ( includeImport( src, isUpdateInterface, isSrcSide, isSrcClient, attr ) ) {

                String typeId = attr.getTypeId();

                if ( typeId != null && importSet.contains( typeId ) ) {
                    continue;
                }

                importSet.add( typeId );

                if ( attr.isHandcrafted() ) {
                    if ( attr.getHandcraftedPackage() != null ) {
                        if ( !attr.getHandcraftedPackage().equals( "com.rr.core.model" ) ) {
                            b.append( "import " ).append( attr.getHandcraftedPackage() ).append( "." ).append( attr.getTypeId() ).append( ";\n" );
                        }
                    } else {
                        b.append( "import com.rr.model.internal.type." ).append( attr.getTypeId() ).append( ";\n" );
                    }
                } else if ( !attr.isPrimitive() ) {
                    if ( attr instanceof SubEventAttributeDefinition ) {
                        b.append( "import " ).append( _internal.getRootPackage() ).append( "." ).append( ModelConstants.EVENT_PACKAGE ).append( ".interfaces." )
                         .append( attr.getTypeId() ).append( ";\n" );
                    } else {
                        b.append( "import " ).append( _internal.getRootPackage() ).append( "." ).append( ModelConstants.TYPE_PACKAGE ).append( "." )
                         .append( attr.getTypeId() ).append( ";\n" );
                    }
                }
            }
        }

        b.append( "import com.rr.core.utils.Utils;\n" );
        b.append( "import com.rr.core.lang.*;\n" );
        b.append( "import com.rr.core.model.*;\n" );
        b.append( "import com.rr.core.annotations.*;\n" );

        if ( !isInterface ) {
            b.append( "import com.rr.model.internal.type.*;\n" );
            b.append( "import " ).append( _internal.getRootPackage() ).append( "." ).append( ModelConstants.CORE_PACKAGE ).append( "." )
             .append( ModelConstants.REUSABLE_TYPES_FILE_NAME ).append( ";\n" );
            b.append( "import " ).append( _internal.getRootPackage() ).append( "." ).append( ModelConstants.CORE_PACKAGE ).append( "." )
             .append( ModelConstants.SIZE_TYPE_FILE_NAME ).append( ";\n" );
            b.append( "import " ).append( _internal.getRootPackage() ).append( "." ).append( ModelConstants.CORE_PACKAGE ).append( "." )
             .append( ModelConstants.EVENT_ID_FILE_NAME ).append( ";\n" );
            b.append( "import " ).append( _internal.getRootPackage() ).append( "." ).append( ModelConstants.EVENT_PACKAGE ).append( "." )
             .append( ModelConstants.INTERFACES_PACKAGE ).append( ".*;\n" );
        }
    }

    private void addExceptionGetMethod( StringBuilder b, String className, String attrName, String methodBase, String type ) {
        String msg = "Getter for " + attrName + " event " + className + " is a delegate field from order request base";

        b.append( "    @Override public " ).append( type ).append( " get" ).append( methodBase ).append( "() { throw new IllegalFieldAccess( \"" ).append( msg )
         .append( "\" ); }\n\n" );
    }

    private void addPrimitiveMethods( EventStreamSrc src, StringBuilder b, String className, String typeDef, boolean isInterface, boolean addGetter,
                                      boolean addSetter, DelegateType delegate, String attrName, String methodBase, boolean attrInherited,
                                      final OutboundInstruction inst, boolean isUpdInt, String desc, boolean forceOverride ) {

        String getMethod = "get" + methodBase;

        if ( isInterface ) {
            String override = hackSetOverride( attrName, src, attrInherited, isUpdInt ) ? "@Override " : "";
            if ( addSetter ) {
                b.append( "    " ).append( override ).append( "void set" ).append( methodBase ).append( "( " ).append( typeDef ).append( " val );\n" );
            }

            if ( addGetter ) {
                if ( desc != null ) {
                    b.append( "    /**\n     *" ).append( desc ).append( "\n     */\n" );
                }
                override = hackGetOverride( attrName, attrInherited, forceOverride ) ? "@Override " : "";
                b.append( "    " ).append( override ).append( typeDef ).append( " " ).append( getMethod ).append( "();\n" );
            }
        } else {
            if ( delegate == DelegateType.Delegate ) {
                b.append( "    @Override public final " ).append( typeDef ).append( " " ).append( getMethod ).append( "() { return _srcEvent." )
                 .append( getMethod ).append( "(); }\n" );
            } else if ( delegate == DelegateType.DelegateGetAndSet ) {
                b.append( "    @Override public final " ).append( typeDef ).append( " " ).append( getMethod ).append( "() { return _srcEvent." )
                 .append( getMethod ).append( "(); }\n" );
                b.append( "    @Override public final void set" ).append( methodBase ).append( "( " ).append( typeDef ).append( " val ) { _srcEvent.set" )
                 .append( methodBase ).append( "( val ); }\n" );
            } else if ( delegate == DelegateType.EmptyDelegateThrowException ) {

                addExceptionGetMethod( b, className, attrName, methodBase, typeDef );
            } else {
                b.append( "    @Override public final " ).append( typeDef ).append( " " ).append( getMethod ).append( "() { return _" ).append( attrName )
                 .append( "; }\n" );

                b.append( "    @Override public final void set" ).append( methodBase ).append( "( " ).append( typeDef ).append( " val ) { _" )
                 .append( attrName ).append( " = val; }\n" );
            }
        }
    }

    private void addReusableStringMethods( EventStreamSrc src, StringBuilder b, String className, boolean isInterface, boolean addGetter, boolean addSetter,
                                           DelegateType delegate, String attrName, String methodBase, boolean attrInherited, OutboundInstruction inst,
                                           boolean isUpdInt, boolean forceOverride ) {

        String getMethod = "get" + methodBase;
        String override  = hackSetOverride( attrName, src, attrInherited, isUpdInt ) ? "@Override " : "";

        if ( isInterface ) {
            if ( addSetter ) {
                b.append( "    " ).append( override ).append( "void set" ).append( methodBase ).append( "( byte[] buf, int offset, int len );\n" );
                b.append( "    " ).append( override ).append( "ReusableString " ).append( getMethod ).append( "ForUpdate();\n" );
            }

            if ( addGetter ) {
                override = hackGetOverride( attrName, attrInherited, forceOverride ) ? "@Override " : "";
                b.append( "    " ).append( override ).append( "ViewString " ).append( getMethod ).append( "();\n" );
            }
        } else {
            if ( delegate == DelegateType.Delegate ) {
                b.append( "    @Override public final ViewString " ).append( getMethod ).append( "() { return _srcEvent." ).append( getMethod )
                 .append( "(); }\n\n" );
            } else if ( delegate == DelegateType.EmptyDelegateThrowException ) {

                addExceptionGetMethod( b, className, attrName, methodBase, "ViewString" );
            } else {
                b.append( "    @Override public final ViewString " ).append( getMethod ).append( "() { return _" ).append( attrName ).append( "; }\n\n" );

                b.append( "    @Override public final void set" ).append( methodBase ).append( "( byte[] buf, int offset, int len ) " ).append( "{ _" )
                 .append( attrName ).append( ".setValue( buf, offset, len ); }\n" );

                b.append( "    @Override public final ReusableString " ).append( getMethod ).append( "ForUpdate() { return _" ).append( attrName )
                 .append( "; }\n" );
            }
        }
    }

    private void addSubEventRecycling( ClassDefinition def, EventStreamSrc src, String prefix, StringBuilder b ) {
        for ( AttributeDefinition attr : def.getAttributes( true ) ) {

            if ( attr instanceof SubEventAttributeDefinition ) {
                String attrName      = attr.getAttrName();
                String className     = prefix + GenUtils.toUpperFirstChar( attr.getTypeId() );
                String implClassName = className + "Impl";
                String recyclerVar   = "_" + attrName + "Recycler";
                String getter        = "get" + GenUtils.toUpperFirstChar( attrName );
                String attrVar       = attrName;

                b.append( "            " ).append( implClassName ).append( " " ).append( attrVar ).append( " = (" ).append( implClassName ).append( ") obj." )
                 .append( getter ).append( "();\n" );
                b.append( "            while ( " ).append( attrVar ).append( " != null ) {\n" );
                b.append( "                " ).append( implClassName ).append( " t = " ).append( attrVar ).append( ";\n" );
                b.append( "                " ).append( attrVar ).append( " = " ).append( attrVar ).append( ".getNext();\n" );
                b.append( "                t.setNext( null );\n" );
                b.append( "                " ).append( recyclerVar ).append( ".recycle( t );\n" );
                b.append( "            }\n\n" );
            }
        }
    }

    private void addSubRecyclerImports( ClassDefinition def, EventStreamSrc src, String prefix, StringBuilder b ) {
        int cnt = 0;

        for ( AttributeDefinition attr : def.getAttributes( true ) ) {

            if ( attr instanceof SubEventAttributeDefinition ) {
                String className = prefix + GenUtils.toUpperFirstChar( attr.getTypeId() ) + "Impl";

                b.append( "import " ).append( _internal.getRootPackage() ).append( "." ).append( ModelConstants.EVENT_PACKAGE ).append( "." )
                 .append( ModelConstants.IMPL_PACKAGE ).append( "." ).append( className ).append( ";\n" );

                ++cnt;
            }
        }

        if ( cnt > 0 ) {
            b.append( "import com.rr.core.pool.SuperpoolManager;\n" );
        }

    }

    private void addSubRecyclerVars( ClassDefinition def, EventStreamSrc src, String prefix, StringBuilder b ) {
        for ( AttributeDefinition attr : def.getAttributes( true ) ) {

            if ( attr instanceof SubEventAttributeDefinition ) {
                String attrName      = attr.getAttrName();
                String className     = prefix + GenUtils.toUpperFirstChar( attr.getTypeId() ) + "Recycler";
                String implClassName = prefix + GenUtils.toUpperFirstChar( attr.getTypeId() ) + "Impl";
                String recyclerVar   = "_" + attrName + "Recycler";

                b.append( "\n    private " ).append( className ).append( " " ).append( recyclerVar ).append( " = SuperpoolManager.instance().getRecycler( " )
                 .append( className ).append( ".class, " ).append( implClassName ).append( ".class );\n\n" );
            }
        }
    }

    private void addTypeConstructor( StringBuilder b, TypeDefinition type ) {

        StringBuilder init = new StringBuilder();

        b.append( "\n    " ).append( type.getId() );

        int valLen = type.getMaxEntryValueLen();

        if ( type.isByteValType() ) {
            b.append( "( int id, byte val" );
            init.append( "        _val = val;\n" );
        } else {
            b.append( "( int id, String val" );
            if ( valLen == 1 ) {
                init.append( "        _val = val.getBytes()[0];\n" );
            } else {
                init.append( "        _val = val.getBytes();\n" );
            }
        }
        init.append( "        _id = id;\n" );

        Collection<AttributeDefinition> attributes = type.getAttributes();

        if ( attributes != null ) {

            for ( AttributeDefinition attr : attributes ) {
                b.append( ",  " ).append( attr.getType().getTypeDeclaration() ).append( " " ).append( attr.getAttrName() );

                init.append( "        _" ).append( attr.getAttrName() ).append( " = " ).append( attr.getAttrName() ).append( ";\n" );
            }
        }

        b.append( " ) {\n" ).append( init ).append( "    }\n" );
    }

    private void addTypeEntry( StringBuilder b, TypeEntry typeEntry, TypeDefinition type ) {

        b.append( "\n    " ).append( typeEntry.getInstanceName() ).append( "( " ).append( ModelConstants.TYPE_ID_FILE_NAME ).append( "." )
         .append( GenUtils.makeTypeId( type, typeEntry ) );

        if ( "byte".equalsIgnoreCase( type.getValType() ) ) {
            b.append( ", (byte)" ).append( typeEntry.getInstanceValue() );
        } else {
            b.append( ", \"" ).append( typeEntry.getInstanceValue() ).append( "\"" );
        }

        Collection<AttributeDefinition> attributes = type.getAttributes();

        if ( attributes != null ) {

            for ( AttributeDefinition defAttr : attributes ) {

                AttributeDefinition attr = typeEntry.getAttributeDefinition( defAttr.getAttrName() );

                String val = defAttr.getDefaultValue();

                if ( attr != null ) {
                    val = attr.getDefaultValue();
                }

                if ( defAttr.isPrimitive() ) {
                    if ( defAttr.getType().getClass() == ReusableStringType.class ) {
                        b.append( ", \"" ).append( val ).append( "\"" );
                    } else {
                        b.append( ", " ).append( val );
                    }
                } else {
                    b.append( ", " ).append( defAttr.getType().getTypeDeclaration() ).append( "." ).append( val );
                }
            }
        }

        b.append( " )" );
    }

    private void addTypeMethods( EventStreamSrc src,
                                 StringBuilder b,
                                 String className,
                                 String typeDef,
                                 boolean isInterface,
                                 boolean addGetter,
                                 boolean addSetter,
                                 DelegateType delegate,
                                 String attrName,
                                 String methodBase,
                                 boolean attrInherited,
                                 boolean isUpdInt,
                                 boolean isForceOverride ) {

        String getMethod = "get" + methodBase;

        if ( isInterface ) {
            String override = hackSetOverride( attrName, src, attrInherited, isUpdInt ) ? "@Override " : "";
            if ( addSetter ) {
                b.append( "    " ).append( override ).append( "void set" ).append( methodBase ).append( "( " ).append( typeDef ).append( " val );\n" );
            }
            if ( addGetter ) {
                override = hackGetOverride( attrName, attrInherited, isForceOverride ) ? "@Override " : "";
                b.append( "    " ).append( override ).append( typeDef ).append( " " ).append( getMethod ).append( "();\n" );
            }
        } else {
            if ( delegate == DelegateType.Delegate ) {
                b.append( "    @Override public final " ).append( typeDef ).append( " " ).append( getMethod ).append( "() { return _srcEvent." )
                 .append( getMethod ).append( "(); }\n" );
            } else if ( delegate == DelegateType.EmptyDelegateThrowException ) {

                addExceptionGetMethod( b, className, attrName, methodBase, typeDef );
            } else {
                b.append( "    @Override public final " ).append( typeDef ).append( " " ).append( getMethod ).append( "() { return _" ).append( attrName )
                 .append( "; }\n" );

                b.append( "    @Override public final void set" ).append( methodBase ).append( "( " ).append( typeDef ).append( " val ) { _" )
                 .append( attrName ).append( " = val; }\n" );
            }
        }
    }

//    public static void write( ReusableString dest, StratInstrumentState msg, Procedure2Args<Long,ReusableString> timeConverter ) {
//        dest.append( msg.getId() );
//        dest.append( MAIN_SEP );
//        if ( timeConverter != null ) timeConverter.invoke( msg.getStratTimestamp(), dest ) ; dest.append( msg.getStratTimestamp() );

    private void addTypeValueOf( StringBuilder b, TypeDefinition type ) {
        int maxEntryValLen = type.getMaxEntryValueLen();

        String enumType = type.getTypeDeclaration();

        if ( maxEntryValLen == 1 ) {
            byte        max          = 0;
            byte        min          = Byte.MAX_VALUE;
            Set<String> internalVals = type.getValues();
            for ( String internalVal : internalVals ) {
                byte val;
                if ( type.isByteValType() ) {
                    val = Byte.parseByte( internalVal );
                } else {
                    val = (byte) internalVal.charAt( 0 );
                }
                if ( val > max ) max = val;
                if ( val < min ) min = val;
            }
            if ( min < 0 ) min = 0;
            int size = (max - min) + 1;

            b.append( "    private static final int _indexOffset = " ).append( min ).append( ";\n" );
            b.append( "    private static final " ).append( enumType ).append( "[] _entries = new " ).append( enumType ).append( "[" ).append( size )
             .append( "];\n" );
            b.append( "\n    static {\n" );
            b.append( "        for ( int i=0 ; i < _entries.length ; i++ ) {\n             _entries[i] = Unknown; }\n\n" );
            b.append( "        for ( " ).append( enumType ).append( " en : " ).append( enumType ).append( ".values() ) {\n " );
            b.append( "            if ( en == Unknown ) continue;\n" );
            b.append( "            _entries[ en.getVal() - _indexOffset ] = en;\n" );
            b.append( "        }\n" );
            b.append( "    }\n\n" );

            String cast = (type.isByteValType()) ? "" : "(char)";

            b.append( "    public static " ).append( enumType ).append( " getVal( byte val ) {\n" );

            b.append( "        final int arrIdx = val - _indexOffset;\n" );
            b.append( "        if ( arrIdx < 0 || arrIdx >= _entries.length ) {\n" );
            b.append( "            throw new RuntimeDecodingException( \"Unsupported value of \" + " ).append( cast ).append( "val + \" for " )
             .append( enumType ).append( "\" );\n" );
            b.append( "        }\n" );

            b.append( "        " ).append( enumType ).append( " eval;\n" );
            b.append( "        eval = _entries[ arrIdx ];\n" );
            b.append( "        if ( eval == Unknown ) throw new RuntimeDecodingException( \"Unsupported value of \" + " ).append( cast )
             .append( "val + \" for " ).append( enumType ).append( "\" );\n" );
            b.append( "        return eval;\n" );
            b.append( "    }\n\n" );

            b.append( "    @Override\n" );
            b.append( "    public final byte getVal() {\n" );
            b.append( "        return _val;\n" );
            b.append( "    }\n\n" );

        } else if ( maxEntryValLen == 2 ) {

            // @TODO test and re-evaluate effect on L1 and L2 cache from this !

            // create an array of 64K * 8bytes = very wasteful on space BUT
            // fastest to lookup as requires single bit shift of key;

            b.append( "    private static " ).append( enumType ).append( "[] _entries = new " ).append( enumType ).append( "[256*256];\n" );
            b.append( "\n    static {\n" );
            b.append( "        for ( int i=0 ; i < _entries.length ; i++ ) \n            { _entries[i] = Unknown; }\n\n" );
            b.append( "        for ( " ).append( enumType ).append( " en : " ).append( enumType ).append( ".values() ) {\n " );
            b.append( "            if ( en == Unknown ) continue;\n" );
            b.append( "            byte[] val = en.getVal();\n" );
            b.append( "            int key = val[0] << 8;\n" );
            b.append( "            if ( val.length == 2 ) key += val[1];\n" );
            b.append( "            _entries[ key ] = en;\n" );
            b.append( "        }\n" );
            b.append( "    }\n\n" );

            b.append( "    public static " ).append( enumType ).append( " getVal( byte[] val, int offset, int len ) {\n" );
            b.append( "        int key = val[offset++] << 8;\n" );
            b.append( "        if ( len == 2 ) key += val[offset];\n" );

            b.append( "        " ).append( enumType ).append( " eval;\n" );
            b.append( "        eval = _entries[ key ];\n" );
            b.append(
                     "        if ( eval == Unknown ) throw new RuntimeDecodingException( \"Unsupported value of \" + ((key>0xFF) ? (\"\" + (char)(key>>8) + (char)(key&0xFF)) : (\"\" + (char)(key&0xFF))) + \" for " )
             .append( enumType ).append( "\" );\n" );
            b.append( "        return eval;\n" );
            b.append( "    }\n\n" );

            b.append( "    public static " ).append( enumType ).append( " getVal( byte[] val ) {\n" );
            b.append( "        int offset = 0;\n" );
            b.append( "        int key = val[offset++] << 8;\n" );
            b.append( "        if ( val.length == 2 ) key += val[offset];\n" );

            b.append( "        " ).append( enumType ).append( " eval;\n" );
            b.append( "        eval = _entries[ key ];\n" );
            b.append(
                     "        if ( eval == Unknown ) throw new RuntimeDecodingException( \"Unsupported value of \" + ((key>0xFF) ? (\"\" + (char)(key>>8) + (char)(key&0xFF)) : (\"\" + (char)(key&0xFF))) + \" for " )
             .append( enumType ).append( "\" );\n" );
            b.append( "        return eval;\n" );
            b.append( "    }\n\n" );

            b.append( "    @Override\n" );
            b.append( "    public final byte[] getVal() {\n" );
            b.append( "        return _val;\n" );
            b.append( "    }\n\n" );

        } else {
            b.append( "    private static Map<ViewString," ).append( enumType ).append( "> _map = new HashMap<>();\n" );
            b.append( "\n    static {\n" );
            b.append( "        for ( " ).append( enumType ).append( " en : " ).append( enumType ).append( ".values() ) {\n " );
            b.append( "            byte[] val = en.getVal();\n" );
            b.append( "            ViewString zVal = new ViewString( val );\n" );
            b.append( "            _map.put( zVal, en );\n" );
            b.append( "        }\n" );
            b.append( "    }\n\n" );

            b.append( "    public static " ).append( enumType ).append( " getVal( final ViewString key ) {\n" );
            b.append( "        " ).append( enumType ).append( " val = _map.get( key );\n" );
            b.append( "        if ( val == null ) throw new RuntimeDecodingException( \"Unsupported value of \" + key + \" for " ).append( enumType )
             .append( "\" );\n" );
            b.append( "        return val;\n" );
            b.append( "    }\n\n" );

            b.append( "    @Override\n" );
            b.append( "    public final byte[] getVal() {\n" );
            b.append( "        return _val;\n" );
            b.append( "    }\n\n" );
        }
    }

    private void addViewStringMethods( EventStreamSrc src, StringBuilder b, String className, boolean isInterface, boolean addGetter, boolean addSetter,
                                       DelegateType delegate, String attrName, String methodBase, boolean attrInherited, OutboundInstruction inst,
                                       final boolean isUpdInt, boolean forceOverride ) {

        String getMethod = "get" + methodBase;

        if ( isInterface ) {
            String override = hackSetOverride( attrName, src, attrInherited, isUpdInt ) ? "@Override " : "";
            if ( addSetter ) {
                b.append( "    " ).append( override ).append( "AssignableString " ).append( getMethod ).append( "ForUpdate();\n" );
            }

            if ( addGetter ) {
                override = hackGetOverride( attrName, attrInherited, forceOverride ) ? "@Override " : "";
                b.append( "    " ).append( override ).append( "ViewString " ).append( getMethod ).append( "();\n" );
            }
        } else {
            if ( delegate == DelegateType.Delegate ) {
                b.append( "    @Override public final ViewString " ).append( getMethod ).append( "() { return _srcEvent." ).append( getMethod )
                 .append( "(); }\n\n" );
            } else if ( delegate == DelegateType.EmptyDelegateThrowException ) {

                addExceptionGetMethod( b, className, attrName, methodBase, "ViewString" );
            } else {
                b.append( "    @Override public final ViewString " ).append( getMethod ).append( "() { return _" ).append( attrName ).append( "; }\n\n" );

                b.append( "              public final void set" ).append( methodBase ).append( "( int offset, int len ) " ).append( "{ _" ).append( attrName )
                 .append( ".setValue( offset, len ); }\n" );

                b.append( "    @Override public final AssignableString " ).append( getMethod ).append( "ForUpdate() { return _" ).append( attrName )
                 .append( "; }\n" );
            }
        }
    }

    private boolean checkOverrideAsReusableString( EventStreamSrc src, AttributeDefinition attr, boolean isSrcSide ) {
        boolean override = false;

        AttrType type = attr.getType();

        if ( type.getClass() == ViewStringType.class && src == EventStreamSrc.recovery ) return true;

        if ( isSrcSide == false && attr.isPrimitive() ) {
            if ( type.getClass() == ViewStringType.class && attr.getInstruction() == OutboundInstruction.seperate ) {

                override = true;
            }
        }

        return override;
    }

    private boolean doWriteClassMethods( EventStreamSrc src, StringBuilder b, String eventId, ClassDefinition def, boolean isSrcSide, boolean isSrcClient, boolean isUpdInt ) {
        Collection<AttributeDefinition> attrs = def.getAttributes( true );

        boolean baseRequired = false;

        for ( AttributeDefinition attr : attrs ) {
            if ( hackIgnore( attr ) ) continue;

            String              attrName   = attr.getAttrName();
            String              methodBase = GenUtils.toUpperFirstChar( attrName );
            OutboundInstruction inst       = attr.getInstruction();

            DelegateType delegate = GenUtils.getDelegateType( src, isSrcSide, isSrcClient, inst );

            if ( delegate == DelegateType.Delegate || delegate == DelegateType.DelegateGetAndSet ) baseRequired = true;

            boolean attrInherited = def.isAttrInherited( attrName );
            addAnAttrMethods( src, eventId, attr, b, false, true, true, delegate, attrName, methodBase, isSrcSide, attrInherited, inst, isUpdInt );

            b.append( "\n" );
        }
        return baseRequired;
    }

    private boolean doWriteResetMethodAttrs( EventStreamSrc src, StringBuilder b, ClassDefinition def, boolean isSrcSide, boolean isSrcClient ) {
        Collection<AttributeDefinition> attrs = def.getAttributes( def.isInterface() == false );

        boolean needBase = false;

        for ( AttributeDefinition attr : attrs ) {
            if ( hackIgnore( attr ) ) continue;

            OutboundInstruction inst         = attr.getInstruction();
            DelegateType        delegateType = GenUtils.getDelegateType( src, isSrcSide, isSrcClient, inst );

            if ( delegateType == DelegateType.Delegate ) {
                needBase = true;
            } else if ( delegateType != DelegateType.EmptyDelegateThrowException ) {
                b.append( "        " );
                writeAttrResetValue( b, attr );
            }
        }
        return needBase;
    }

    /**
     * package com.rr.core.lang.stats;
     * <p>
     * public enum SizeType {
     * DEFAULT_STRING_LENGTH( 16 ),
     * DEFAULT_VIEW_NOS_BUFFER( 400 );  // default size of the buffer backing the ViewStrings in a NOS - holds all input msg
     * <p>
     * private int _size;
     * <p>
     * private SizeType( int val ) {
     * _size = val;
     * }
     * <p>
     * public int getSize() { return _size; }
     * }
     *
     * @throws IOException
     * @throws FileException
     */
    private void generateConstants() throws FileException, IOException {

        StringBuilder b = new StringBuilder();
        GenUtils.addPackageDef( b, _internal, ModelConstants.CORE_PACKAGE, ModelConstants.SIZE_TYPE_FILE_NAME );
        GenUtils.addGenerated( b, _internal, ModelConstants.CORE_PACKAGE, ModelConstants.SIZE_TYPE_FILE_NAME );

        b.append( "public enum " + ModelConstants.SIZE_TYPE_FILE_NAME + " {\n" );

        Map<String, Integer> defaultVals = _internal.getDefaultSizes();

        boolean first = true;

        for ( Map.Entry<String, Integer> entry : defaultVals.entrySet() ) {
            String  id  = entry.getKey();
            Integer val = entry.getValue();

            if ( first ) {
                first = false;
            } else {
                b.append( ",\n" );
            }
            b.append( "    " ).append( id ).append( "( " ).append( val ).append( " )" );
        }

        b.append( ";\n\n" );

        b.append( "    private int _size;\n\n" );

        b.append( "    private " + ModelConstants.SIZE_TYPE_FILE_NAME + "( int val ) {\n" );
        b.append( "        _size = val;\n" );
        b.append( "    }\n\n" );
        b.append( "    public int getSize() { return _size; }\n" );
        b.append( "}\n" );

        File file = GenUtils.getJavaFile( _internal, ModelConstants.CORE_PACKAGE, ModelConstants.SIZE_TYPE_FILE_NAME );

        GenUtils.writeFile( file, b );
    }

    private void generateEventIds() throws FileException, IOException {

        // write the reusableTypes
        StringBuilder b = new StringBuilder();
        GenUtils.addPackageDef( b, _internal, ModelConstants.CORE_PACKAGE, ModelConstants.EVENT_ID_FILE_NAME );

        GenUtils.addGenerated( b, _internal, ModelConstants.CORE_PACKAGE, ModelConstants.EVENT_ID_FILE_NAME );

        b.append( "public interface " + ModelConstants.EVENT_ID_FILE_NAME + " {\n\n" );

        int nxt = 1;

        for ( ClassDefinition def : _internal.getClassDefinitions() ) {
            if ( def.isInterface() == false ) {
                def.setEventId( nxt );
                b.append( "    public int ID_" ).append( def.getReusableType().toUpperCase() ).append( " = " ).append( nxt++ ).append( ";\n" );
            }
        }

        b.append( "}\n" );

        File file = GenUtils.getJavaFile( _internal, ModelConstants.CORE_PACKAGE, ModelConstants.EVENT_ID_FILE_NAME );

        GenUtils.writeFile( file, b );
    }

    private void generateEvents() throws FileException, IOException {

        for ( ClassDefinition def : _internal.getClassDefinitions() ) {
            if ( def.getType() != Type.SubEvent ) {
                writeClassDefn( def );
            }
        }
    }

    private void generateFactories() throws FileException, IOException {
        GenUtils.createPackage( _internal.getVersionDir(), ModelConstants.EVENT_PACKAGE, ModelConstants.FACTORY_PACKAGE );
        GenUtils.createPackage( _internal.getVersionDir(), ModelConstants.EVENT_PACKAGE, ModelConstants.RECYCLER_PACKAGE );

        for ( ClassDefinition def : _internal.getClassDefinitions() ) {

            if ( def.getStreamSrc() == EventStreamSrc.both ) {
                writeFactory( def, EventStreamSrc.both );
                writeRecycler( def, EventStreamSrc.both );
            } else {
                writeFactory( def, EventStreamSrc.exchange );
                writeFactory( def, EventStreamSrc.client );
                writeFactory( def, EventStreamSrc.recovery );
                writeRecycler( def, EventStreamSrc.exchange );
                writeRecycler( def, EventStreamSrc.client );
                writeRecycler( def, EventStreamSrc.recovery );
            }
        }

        writeEventRecycler( _internal );
    }

    private void generateFullEventIds() throws FileException, IOException {

        // write the reusableTypes
        StringBuilder b = new StringBuilder();
        GenUtils.addPackageDef( b, _internal, ModelConstants.CORE_PACKAGE, ModelConstants.FULL_EVENT_ID_FILE_NAME );

        GenUtils.addGenerated( b, _internal, ModelConstants.CORE_PACKAGE, ModelConstants.FULL_EVENT_ID_FILE_NAME );

        b.append( "public class " + ModelConstants.FULL_EVENT_ID_FILE_NAME + " {\n\n" );

        int nxt = ReusableCategoryEnum.Event.getBaseId();

        for ( ClassDefinition def : _internal.getClassDefinitions() ) {
            if ( def.isInterface() == false ) {
                EventStreamSrc src = def.getStreamSrc();
                if ( src == EventStreamSrc.both ) {
                    b.append( "    public static final int ID_" ).append( def.getReusableType().toUpperCase() ).append( " = " ).append( nxt++ ).append( ";\n" );
                } else {
                    b.append( "    public static final int ID_CLIENT_" ).append( def.getReusableType().toUpperCase() ).append( " = " ).append( nxt++ )
                     .append( ";\n" );
                }
            }
        }

        for ( ClassDefinition def : _internal.getClassDefinitions() ) {
            if ( def.isInterface() == false ) {
                EventStreamSrc src = def.getStreamSrc();
                if ( src != EventStreamSrc.both ) {
                    b.append( "    public static final int ID_MARKET_" ).append( def.getReusableType().toUpperCase() ).append( " = " ).append( nxt++ )
                     .append( ";\n" );
                }
            }
        }

        for ( ClassDefinition def : _internal.getClassDefinitions() ) {
            if ( def.isInterface() == false ) {
                EventStreamSrc src = def.getStreamSrc();
                if ( src != EventStreamSrc.both ) {
                    b.append( "    public static final int ID_" ).append( def.getReusableType().toUpperCase() ).append( " = " ).append( nxt++ ).append( ";\n" );
                }
            }
        }

        b.append( "}\n" );

        File file = GenUtils.getJavaFile( _internal, ModelConstants.CORE_PACKAGE, ModelConstants.FULL_EVENT_ID_FILE_NAME );

        GenUtils.writeFile( file, b );
    }

    private void generateReusableTypes() throws FileException, IOException {

        // write the reusableTypes
        StringBuilder b = new StringBuilder();
        GenUtils.addPackageDef( b, _internal, ModelConstants.CORE_PACKAGE, ModelConstants.REUSABLE_TYPES_FILE_NAME );

        b.append( "\nimport com.rr.core.lang.ReusableCategory;\n\n" );
        b.append( "\nimport com.rr.core.lang.ReusableCategoryEnum;\n\n" );
        b.append( "\nimport com.rr.core.lang.ReusableType;\n\n" );
        b.append( "\nimport com.rr.core.lang.ReusableTypeIDFactory;\n\n" );

        GenUtils.addGenerated( b, _internal, ModelConstants.CORE_PACKAGE, ModelConstants.REUSABLE_TYPES_FILE_NAME );

        b.append( "public enum " + ModelConstants.REUSABLE_TYPES_FILE_NAME + " implements ReusableType {\n\n" );

        boolean first = true;

        for ( ClassDefinition def : _internal.getClassDefinitions() ) {
            if ( def.isInterface() == false ) {

                if ( first )
                    first = false;
                else
                    b.append( ",\n" );

                EventStreamSrc src = def.getStreamSrc();

                if ( src == EventStreamSrc.both ) {
                    b.append( "    " ).append( def.getReusableType() ).append( "( ReusableCategoryEnum.Event, " )
                     .append( GenUtils.getFullEventId( def, EventStreamSrc.both ) ).append( ", " ).append( GenUtils.getEventId( def ) ).append( " )" );
                } else {
                    b.append( "    " + ModelConstants.FULL_EVENT_PRENAME ).append( def.getReusableType() ).append( "( ReusableCategoryEnum.Event, " )
                     .append( GenUtils.getFullEventId( def, EventStreamSrc.recovery ) ).append( ", " ).append( GenUtils.getEventId( def ) ).append( " ),\n" );
                    b.append( "    Client" ).append( def.getReusableType() ).append( "( ReusableCategoryEnum.Event, " )
                     .append( GenUtils.getFullEventId( def, EventStreamSrc.client ) ).append( ", " ).append( GenUtils.getEventId( def ) ).append( " ),\n" );
                    b.append( "    Market" ).append( def.getReusableType() ).append( "( ReusableCategoryEnum.Event, " )
                     .append( GenUtils.getFullEventId( def, EventStreamSrc.exchange ) ).append( ", " ).append( GenUtils.getEventId( def ) ).append( " )" );
                }
            }
        }

        b.append( ";\n\n" );

        b.append( "    private final int              _eventId;\n" );
        b.append( "    private final int              _id;\n" );
        b.append( "    private final ReusableCategory _cat;\n\n" );

        b.append( "    private " + ModelConstants.REUSABLE_TYPES_FILE_NAME + "( ReusableCategory cat, int catId, int eventId ) {\n" );
        b.append( "        _cat     = cat;\n" );
        b.append( "        _id      = ReusableTypeIDFactory.setID( cat, catId );\n" );
        b.append( "        _eventId = eventId;\n" );
        b.append( "    }\n\n" );

        b.append( "    @Override\n" );
        b.append( "    public int getSubId() {\n" );
        b.append( "        return _eventId;\n" );
        b.append( "    }\n\n" );

        b.append( "    @Override\n" );
        b.append( "    public int getId() {\n" );
        b.append( "        return _id;\n" );
        b.append( "    }\n\n" );

        b.append( "    @Override\n" );
        b.append( "    public ReusableCategory getReusableCategory() {\n" );
        b.append( "        return _cat;\n" );
        b.append( "    }\n\n" );

        b.append( "}\n" );

        File file = GenUtils.getJavaFile( _internal, ModelConstants.CORE_PACKAGE, ModelConstants.REUSABLE_TYPES_FILE_NAME );

        GenUtils.writeFile( file, b );
    }

    /**
     * EVENT GENERATION
     *
     * @throws FileException
     * @throws IOException
     */

    private void generateSubEvents() throws FileException, IOException {
        GenUtils.createPackage( _internal.getVersionDir(), ModelConstants.EVENT_PACKAGE );
        GenUtils.createPackage( _internal.getVersionDir(), ModelConstants.EVENT_PACKAGE, ModelConstants.INTERFACES_PACKAGE );
        GenUtils.createPackage( _internal.getVersionDir(), ModelConstants.EVENT_PACKAGE, ModelConstants.IMPL_PACKAGE );

        for ( ClassDefinition def : _internal.getClassDefinitions() ) {
            if ( def.getType() == Type.SubEvent ) {

                EventStreamSrc src = def.getStreamSrc();

                writeSubEventInterface( src, def, def.getId() );
                writeSubEventImpl( src, def, def.getId() );
            }
        }
    }

    private void generateTypeIds() throws FileException, IOException {
        String        fileName = ModelConstants.TYPE_ID_FILE_NAME;
        StringBuilder b        = new StringBuilder();

        File file = GenUtils.getJavaFile( _internal, ModelConstants.TYPE_PACKAGE, fileName );
        GenUtils.addPackageDef( b, _internal, ModelConstants.TYPE_PACKAGE, fileName );

        b.append( "\npublic interface " ).append( fileName ).append( " {\n" );

        for ( TypeDefinition type : _internal.getTypeDefinitions() ) {
            if ( type.isHandCrafted() ) continue;

            Collection<TypeEntry> entries = type.getEntries();

            String typeName = type.getId().toUpperCase();

            int cnt = 0;

            b.append( "\n\n   // type ids for " ).append( typeName ).append( "\n" );

            for ( TypeEntry typeEntry : entries ) {

                b.append( "    int " ).append( GenUtils.makeTypeId( type, typeEntry ) ).append( " = " ).append( cnt++ ).append( ";\n" );
            }
        }

        b.append( "}\n" );

        GenUtils.writeFile( file, b );
    }

    private void generateTypes() throws FileException, IOException {
        for ( TypeDefinition type : _internal.getTypeDefinitions() ) {

            writeTypeDefinition( type );
        }
    }

    private void generateUtils() throws FileException, IOException {
        GenUtils.createPackage( _internal.getVersionDir(), ModelConstants.EVENT_PACKAGE, ModelConstants.UTILS_PACKAGE );

        writeCSVUtils();
    }

    private Set<String> getCounterSubEventTags( final Collection<AttributeDefinition> attrs ) {

        Set<String> counters = new HashSet<String>();

        for ( AttributeDefinition attr : attrs ) {
            if ( attr instanceof SubEventAttributeDefinition ) {
                counters.add( ((SubEventAttributeDefinition) attr).getCounterAttr() );
            }
        }

        return counters;
    }

    private String getPrefix( EventStreamSrc src ) {
        if ( src == EventStreamSrc.client ) return "Client";
        if ( src == EventStreamSrc.exchange ) return "Market";
        if ( src == EventStreamSrc.recovery ) return ModelConstants.FULL_EVENT_PRENAME;
        return "";
    }

    private boolean hackGetOverride( final String attrName, final boolean isInherited, boolean forceOverride ) {
        if ( forceOverride || "msgSeqNum".equalsIgnoreCase( attrName ) ) {
            return true;
        }

        return isInherited;

    }

    /**
     * the first real model hack, possDupFlag has been moved into a bitset
     *
     * @TODO replace this hack by changing the type in the event to be Flag, flags must be member of MsgFlag
     */
    private boolean hackIgnore( AttributeDefinition attr ) {
        return attr.getAttrName().equals( "possDupFlag" );
    }

    private boolean hackSetOverride( final String attrName, final EventStreamSrc src, final boolean isInherited, final boolean isWriteInterface ) {
        if ( "msgSeqNum".equalsIgnoreCase( attrName ) || ("orderSent".equalsIgnoreCase( attrName ) && isInherited) ) {
            return true;
        }

        return !isSrcClientMarket( src ) && isInherited;

    }

    private boolean includeImport( EventStreamSrc src,
                                   boolean isUpdateInterface,
                                   boolean isSrcSide,
                                   boolean isSrcClient,
                                   AttributeDefinition attr ) {

        OutboundInstruction inst         = attr.getInstruction();
        DelegateType        delegateType = GenUtils.getDelegateType( src, isSrcSide, isSrcClient, inst );

        boolean include = true;

        if ( isUpdateInterface ) {
            include = (delegateType != DelegateType.Delegate && delegateType != DelegateType.EmptyDelegateThrowException);
        }

        return include;
    }

    private boolean isSrcClientMarket( final EventStreamSrc src ) {
        return (src == EventStreamSrc.client || src == EventStreamSrc.exchange);
    }

    private void writeAttrResetValue( StringBuilder b, AttributeDefinition attr ) {
        String defaultVal = attr.getDefaultValue();

        b.append( "_" ).append( attr.getAttrName() );

        if ( attr.isPrimitive() ) {
            AttrType attrType = attr.getType();
            Class<?> type     = attrType.getClass();

            if ( type == BooleanType.class ) {
                b.append( " = " ).append( (defaultVal == null) ? "false" : defaultVal ).append( ";\n" );
            } else if ( type == CharType.class ) {
                b.append( " = (byte)" ).append( (defaultVal == null) ? "'?'" : "'" + defaultVal + "'" ).append( ";\n" );
            } else if ( type == LongType.class || type == UTCTimestampType.class ) {
                b.append( " = " ).append( (defaultVal == null) ? "Constants.UNSET_LONG" : defaultVal ).append( ";\n" );
            } else if ( type == ShortType.class ) {
                b.append( " = " ).append( (defaultVal == null) ? "Constants.UNSET_SHORT" : defaultVal ).append( ";\n" );
            } else if ( type == DateType.class || type == IntType.class ) {
                b.append( " = " ).append( (defaultVal == null) ? "Constants.UNSET_INT" : defaultVal ).append( ";\n" );
            } else if ( type == DoubleType.class ) {
                b.append( " = " ).append( (defaultVal == null) ? "Constants.UNSET_DOUBLE" : defaultVal ).append( ";\n" );
            } else if ( type == FloatType.class ) {
                b.append( " = " ).append( (defaultVal == null) ? "0.0f" : defaultVal ).append( ";\n" );
            } else if ( type == ViewStringType.class || type == ReusableStringType.class ) {
                b.append( ".reset();\n" );
            } else {
                throw new RuntimeException( "Bad event attr type of " + type + " for attr " + attr );
            }
        } else {
            if ( defaultVal != null ) {
                b.append( " = " ).append( attr.getTypeId() ).append( "." ).append( defaultVal ).append( ";\n" );
            } else {
                b.append( " = null;\n" );
            }
        }
    }

    private void writeCSVUtils() throws IOException, FileException {
        StringBuilder b = new StringBuilder();

        String fullPre = ModelConstants.FULL_EVENT_PRENAME;

        String className = "EventCSVWriter";

        File file = GenUtils.getJavaFile( _internal, ModelConstants.EVENT_PACKAGE, ModelConstants.UTILS_PACKAGE, className );
        GenUtils.addPackageDef( b, _internal, ModelConstants.EVENT_PACKAGE, ModelConstants.UTILS_PACKAGE, className );

        b.append( "import " ).append( _internal.getRootPackage() ).append( "." ).append( ModelConstants.EVENT_PACKAGE ).append( "." )
         .append( ModelConstants.INTERFACES_PACKAGE ).append( ".*;\n" );
        b.append( "import " ).append( _internal.getRootPackage() ).append( "." ).append( ModelConstants.EVENT_PACKAGE ).append( "." )
         .append( ModelConstants.RECYCLER_PACKAGE ).append( ".*;\n" );
        b.append( "import " ).append( _internal.getRootPackage() ).append( "." ).append( ModelConstants.CORE_PACKAGE ).append( "." )
         .append( ModelConstants.EVENT_ID_FILE_NAME ).append( ";\n" );

        b.append( "import com.rr.core.recycler.EventRecycler;\n" );
        b.append( "import com.rr.core.pool.SuperpoolManager;\n" );
        b.append( "import com.rr.core.lang.*;\n" );

        b.append( "import com.rr.core.model.Event;\n" );

        b.append( "\npublic final class " ).append( className ).append( " {\n\n" );

        b.append( "    private static final char MAIN_SEP = '" + MAIN_SEP + "';\n" );
        b.append( "    private static final char SUB_SEP  = '" + SUB_SEP + "';\n" );
        b.append( "\n" );

        for ( ClassDefinition def : _internal.getClassDefinitions() ) {
            boolean isInterface = def.isInterface();

            if ( !isInterface ) {
                writeCSVWriterMethodHdr( b, def );

                writeCSVWriterMethodBody( b, def );
            }
        }

        b.append( "}\n" );

        GenUtils.writeFile( file, b );
    }

    private String writeCSVWriterMethodBody( final StringBuilder b, final ClassDefinition def ) {
        String id = def.getId();
        b.append( "\n    public static void write( ReusableString dest, " ).append( id ).append( " msg, ZConsumer2Args<Long,ReusableString> timeConverter ) {\n" );

        Collection<AttributeDefinition> attrs = def.getAttributes( true );

        for ( AttributeDefinition attr : attrs ) {
            if ( !(attr instanceof SubEventAttributeDefinition) ) {
                String attrName   = attr.getAttrName();
                String methodBase = GenUtils.toUpperFirstChar( attrName );

                if ( attr.hasAnnotation( "CSVDate" ) ) {

                    b.append( "        CommonTimeUtils.formatDate( msg.get" + methodBase + "(), dest );\n" );

                } else if ( attr.hasAnnotation( "CSVTime" ) ) {

                    b.append( "        CommonTimeUtils.formatTime( msg.get" + methodBase + "(), dest );\n" );

                } else if ( attr.getType().getClass() == UTCTimestampType.class ) {
                    b.append( "        if ( timeConverter != null ) timeConverter.accept( msg.get" + methodBase + "(), dest ); else dest.append( msg.get" + methodBase + "() );\n" );
                } else {
                    b.append( "        dest.append( msg.get" + methodBase + "() );\n" );
                }

                b.append( "        dest.append( MAIN_SEP );\n" );
            }
        }

        b.append( "    }\n" );

        b.append( "\n" );
        return id;
    }

    private void writeCSVWriterMethodHdr( final StringBuilder b, final ClassDefinition def ) {
        String id = def.getId();
        b.append( "    public static void writeHeader( ReusableString dest, " ).append( id ).append( " msg ) {\n" );
        Collection<AttributeDefinition> attrs = def.getAttributes( true );

        for ( AttributeDefinition attr : attrs ) {
            if ( !(attr instanceof SubEventAttributeDefinition) ) {
                String attrName   = attr.getAttrName();
                String methodBase = GenUtils.toUpperFirstChar( attrName );

                b.append( "        dest.append( \"" + attrName + "\" );\n" );
                b.append( "        dest.append( MAIN_SEP );\n" );
            }
        }

        b.append( "    }\n" );
    }

    private void writeClassAttrs( EventStreamSrc src, String className, StringBuilder b, ClassDefinition def, boolean isSrcSide, boolean isSrcClient, boolean isSubEvent ) {
        b.append( "\n" );

        Collection<AttributeDefinition> attrs = def.getAttributes( def.isInterface() == false );

        int primCnt = 0;

        // only have a buf  on  clientSide
        if ( src != EventStreamSrc.recovery && isSrcClient && isSrcSide && def.hasUseViewString() ) {
            b.append( "    private final ReusableString _buf = new ReusableString( " + ModelConstants.SIZE_TYPE_FILE_NAME + ".VIEW_NOS_BUFFER.getSize() );\n" );
        }

        b.append( "    private transient          " ).append( className ).append( " _next = null;\n" );

        if ( !isSubEvent ) {
            b.append( "    private transient volatile Event        _nextMessage    = null;\n" );
            b.append( "    private transient          EventHandler _messageHandler = null;\n" );
        }

        boolean needBase = false;

        for ( AttributeDefinition attr : attrs ) {
            OutboundInstruction inst         = attr.getInstruction();
            DelegateType        delegateType = GenUtils.getDelegateType( src, isSrcSide, isSrcClient, inst );

            if ( hackIgnore( attr ) ) continue;

            if ( delegateType != DelegateType.Delegate && delegateType != DelegateType.EmptyDelegateThrowException ) {
                if ( attr.isPrimitive() ) {
                    ++primCnt;

                    String defaultVal = attr.getDefaultValue();

                    if ( GenUtils.isStringAttr( attr ) ) {
                        boolean overrideViewStringToReusableString = checkOverrideAsReusableString( src, attr, isSrcSide );

                        if ( overrideViewStringToReusableString ) {
                            b.append( "    private final ReusableString  _" ).append( attr.getAttrName() );
                        } else {
                            b.append( "    private " ).append( attr.getType().getTypeDefinition() ).append( " _" ).append( attr.getAttrName() );
                        }

                        if ( defaultVal != null ) {
                            throw new RuntimeException( "writeClasssAttrs() dont support default vals for Strings yet attr=" +
                                                        attr.getAttrName() + ", val=" + defaultVal );
                        }

                        if ( overrideViewStringToReusableString ) {
                            b.append( " = new ReusableString( " + ModelConstants.SIZE_TYPE_FILE_NAME + "." ).append( attr.getType().getSize() )
                             .append( ".getSize() )" );
                        } else {
                            b.append( " = new " ).append( attr.getType().getTypeDeclaration() );
                        }

                        b.append( ";\n" );
                    } else {

                        PrimitiveType attrType = (PrimitiveType) attr.getType();
                        Class<?>      type     = attrType.getClass();

                        b.append( "    " );

                        String fieldAnnotations = attr.getAnnotations();

                        if ( attr.getType().getClass() == UTCTimestampType.class ) {
                            if ( fieldAnnotations == null || !fieldAnnotations.contains( "@TimestampMS" ) ) {
                                if ( fieldAnnotations == null ) {
                                    fieldAnnotations = "@TimestampMS";
                                } else {
                                    fieldAnnotations += " @TimestampMS";
                                }
                            }
                        }

                        if ( fieldAnnotations != null ) {
                            b.append( fieldAnnotations + " " );
                        }

                        b.append( "private " ).append( attr.getType().getTypeDefinition() ).append( " " );

                        writeAttrResetValue( b, attr );
                    }
                }
            } else {
                needBase = true;
            }
        }

        if ( primCnt > 0 ) b.append( "\n" );

        for ( AttributeDefinition attr : attrs ) {
            OutboundInstruction inst         = attr.getInstruction();
            DelegateType        delegateType = GenUtils.getDelegateType( src, isSrcSide, isSrcClient, inst );

            if ( delegateType != DelegateType.Delegate && delegateType != DelegateType.EmptyDelegateThrowException ) {
                if ( !attr.isPrimitive() ) {
                    b.append( "    private " ).append( attr.getType().getTypeDefinition() ).append( " _" ).append( attr.getAttrName() );

                    String defaultVal = attr.getDefaultValue();

                    if ( defaultVal != null ) {
                        b.append( " = " ).append( attr.getTypeId() ).append( "." ).append( defaultVal );
                    }

                    b.append( ";\n" );
                }
            }
        }

        b.append( "\n" );

        if ( needBase ) {
            b.append( "    private OrderRequest " + " _srcEvent;\n" );
        }

        b.append( "    private int           _flags          = 0;\n" );
    }

    private void writeClassDefn( ClassDefinition def ) throws FileException, IOException {

        EventStreamSrc src         = def.getStreamSrc();
        boolean        isInterface = def.isInterface();
        boolean        isSrcClient = (src != EventStreamSrc.exchange);

        // generate the read interface
        writeInterface( src, "", def, def.getId(), false, false, isSrcClient );
        // generate the write interface
        writeInterface( EventStreamSrc.recovery, "", def, def.getId(), true, true, isSrcClient );

        if ( !isInterface ) {
            // write the Write implementation for the full/recovery classes
            writeImpl( EventStreamSrc.recovery, def, def.getId(), true, isSrcClient );

            if ( isSrcClientMarket( src ) ) {
                String prefix = getPrefix( src );

                // write the optimised write interface for the
                writeInterface( src, prefix, def, def.getId(), true, true, isSrcClient );

                // write the source side implementation ... it can reuse the recovery write interface
                writeImpl( src, def, def.getId(), true, isSrcClient );

                EventStreamSrc other = isSrcClient ? EventStreamSrc.exchange : EventStreamSrc.client;

                // write the delegate side Update interface
                String otherPrefix = isSrcClient ? "Market" : "Client";
                writeInterface( other, otherPrefix, def, def.getId(), true, false, isSrcClient );

                // write the delegate side implementation
                writeImpl( other, def, def.getId(), false, isSrcClient );
            }
        }
    }

    private void writeClassHooks( EventStreamSrc src, String className, StringBuilder b, ClassDefinition def ) {

        List<String> hooks = def.getEventHooks();

        if ( hooks.size() > 0 ) {
            b.append( "\n    // hooks\n" );

            for ( String s : hooks ) {
                b.append( "    " );
                b.append( s );
                b.append( "\n" );
                b.append( "\n" );
            }
        }
    }

    private void writeClassMethods( EventStreamSrc src, StringBuilder b, String eventId, ClassDefinition def, boolean isSrcSide, boolean isSrcClient, boolean isUpdInt ) {

        boolean baseRequired = doWriteClassMethods( src, b, eventId, def, isSrcSide, isSrcClient, isUpdInt );

        // @TODO remove this hack when added Flag as type to the event model
        b.append( "\n    @Override public final boolean getPossDupFlag() { return isFlagSet( MsgFlag.PossDupFlag ); }\n" );
        b.append( "    @Override public final void setPossDupFlag( boolean val ) { setFlag( MsgFlag.PossDupFlag, val ); }\n" );

        if ( baseRequired ) {
            b.append( "\n    @Override public final void setSrcEvent( OrderRequest srcEvent ) { _srcEvent = srcEvent; }\n" );
            b.append( "    @Override public final OrderRequest getSrcEvent() { return _srcEvent; }\n\n" );
        }

        if ( src != EventStreamSrc.recovery && isSrcClient && isSrcSide && def.hasUseViewString() ) {
            b.append( "    public final ViewString getViewBuf() { return _buf; }\n" );
            b.append( "    public final void setViewBuf( byte[] buf, int offset, int len ) { \n" );
            b.append( "        _buf.setValue( buf, offset, len );\n" );
            b.append( "    }\n" );
        }
    }

    private void writeDeepCopy( EventStreamSrc src, String className, StringBuilder b, ClassDefinition def, boolean isSrcSide, boolean isSrcClient ) {

        if ( src == EventStreamSrc.client || src == EventStreamSrc.exchange ) return;

        b.append( "    @Override public final void snapTo( " + def.getId() + " dest ) {\n" );
        b.append( "        ((" + def.getId() + "Impl)dest).deepCopyFrom( this );\n" );
        b.append( "    }\n\n" );

        b.append( "    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */\n" );
        b.append( "    @Override public final void deepCopyFrom( " + def.getId() + " src ) {\n" );

        Collection<AttributeDefinition> attrs = def.getAttributes( true );

        for ( AttributeDefinition attr : attrs ) {
            String attrName = attr.getAttrName();

            String              methodBase   = GenUtils.toUpperFirstChar( attrName );
            OutboundInstruction inst         = attr.getInstruction();
            DelegateType        delegateType = GenUtils.getDelegateType( src, isSrcSide, isSrcClient, inst );

            if ( delegateType != DelegateType.EmptyDelegateThrowException ) {
                if ( attr.isPrimitive() ) {
                    PrimitiveType type = (PrimitiveType) attr.getType();
                    int           size = type.getArraySize();

                    String typeDef = attr.getType().getTypeDefinition();

                    boolean overrideViewStringToReusableString = checkOverrideAsReusableString( src, attr, isSrcSide );

                    if ( attr.getType().getClass() == ReusableStringType.class || overrideViewStringToReusableString ) {
                        b.append( "        get" ).append( methodBase ).append( "ForUpdate().copy( " ).append( "src.get" ).append( methodBase ).append( "() );\n" );
                    } else if ( attr.getType().getClass() == ViewStringType.class ) {
                        b.append( "        // IGNORE VIEWSTRING : " ).append( attrName ).append( "\n" );
                    } else if ( size > 1 ) {

                        throw new RuntimeException( "Array types not yet implemented " + attrName + ", type=" + type.getClass() + ", len=" + size );

                    } else {
                        b.append( "        set" ).append( methodBase ).append( "( " ).append( "src.get" ).append( methodBase ).append( "() );\n" );
                    }

                } else {
                    if ( attr instanceof SubEventAttributeDefinition ) {

                        /*
                        SecDefLegImpl tSrcPtrlegs = (SecDefLegImpl) src.getLegs();
                        SecDefLegImpl tNewPtrlegs = null;

                        while( tSrcPtrlegs != null ) {

                            if ( tNewPtrlegs == null ) {
                                tNewPtrlegs = new SecDefLegImpl();
                                setLegs( tNewPtrlegs );
                            } else {
                                tNewPtrlegs.setNext( new SecDefLegImpl() );
                                tNewPtrlegs = tNewPtrlegs.getNext();
                            }

                            tNewPtrlegs.deepCopyFrom( tSrcPtrlegs );

                            tSrcPtrlegs = tSrcPtrlegs.getNext();
                        }
                        */

                        String typeClass = attr.getTypeId() + "Impl";
                        String tmpSrcPtr = "tSrcPtr" + methodBase;
                        String tmpNewPtr = "tNewPtr" + methodBase;

                        b.append( "        " + typeClass + " " + tmpSrcPtr + " = (" + typeClass + ") src.get" + methodBase + "();\n" );
                        b.append( "        " + typeClass + " " + tmpNewPtr + " = null;\n" );

                        b.append( "        while( " + tmpSrcPtr + " != null ) {\n" );

                        b.append( "            if ( " + tmpNewPtr + " == null ) {\n" );
                        b.append( "                " + tmpNewPtr + " = new " + typeClass + "();\n" );
                        b.append( "                set" + methodBase + "( " + tmpNewPtr + " );\n" );
                        b.append( "            } else {\n" );
                        b.append( "                " + tmpNewPtr + ".setNext( new " + typeClass + "() );\n" );
                        b.append( "                " + tmpNewPtr + " = " + tmpNewPtr + ".getNext();\n" );
                        b.append( "            }\n" );

                        b.append( "            " + tmpNewPtr + ".deepCopyFrom( " + tmpSrcPtr + " );\n" );

                        b.append( "            " + tmpSrcPtr + " = " + tmpSrcPtr + ".getNext();\n" );
                        b.append( "        }\n" );

                    } else if ( attr.isHandcrafted() ) {
                        b.append( "        set" ).append( methodBase ).append( "( " ).append( "src.get" ).append( methodBase ).append( "() );\n" );
                    } else { // internal types are enum and toString generates NO temp objects
                        b.append( "        set" ).append( methodBase ).append( "( " ).append( "src.get" ).append( methodBase ).append( "() );\n" );
                    }
                }
            }
        }

        b.append( "    }\n\n" );
    }

    private void writeDumpMethod( EventStreamSrc src, String className, StringBuilder b, ClassDefinition def, boolean isSrcSide, boolean isSrcClient ) {
        b.append( "    @Override\n" );
        b.append( "    public String toString() {\n" );
        b.append( "        ReusableString buf = TLC.instance().pop();\n" );
        b.append( "        dump( buf );\n" );
        b.append( "        String rs = buf.toString();\n" );
        b.append( "        TLC.instance().pushback( buf );\n" );
        b.append( "        return rs;\n" );
        b.append( "    }\n\n" );

        b.append( "    @Override\n" );
        b.append( "    public final void dump( final ReusableString out ) {\n" );

        Collection<AttributeDefinition> attrs = def.getAttributes( true );

        b.append( "        out.append( \"" ).append( className ).append( "\" ).append( ' ' );\n" );
        for ( AttributeDefinition attr : attrs ) {
            String              attrName     = attr.getAttrName();
            String              methodBase   = GenUtils.toUpperFirstChar( attrName );
            OutboundInstruction inst         = attr.getInstruction();
            DelegateType        delegateType = GenUtils.getDelegateType( src, isSrcSide, isSrcClient, inst );

            if ( delegateType != DelegateType.EmptyDelegateThrowException ) {
                if ( attr.isPrimitive() ) {
                    PrimitiveType attrType = (PrimitiveType) attr.getType();
                    Class<?>      type     = attrType.getClass();

                    String  typeDef                            = attr.getType().getTypeDefinition();
                    boolean overrideViewStringToReusableString = checkOverrideAsReusableString( src, attr, isSrcSide );

                    if ( type == BooleanType.class || type == CharType.class ) {
                        b.append( "        out.append( \", " ).append( attrName ).append( "=\" ).append( get" ).append( methodBase ).append( "() );\n" );
                    } else if ( type == LongType.class ) {
                        b.append( "        if ( Constants.UNSET_LONG != get" ).append( methodBase ).append( "() && 0 != get" ).append( methodBase ).append( "() ) " );
                        b.append( "            out.append( \", " ).append( attrName ).append( "=\" ).append( get" ).append( methodBase ).append( "() );\n" );
                    } else if ( type == UTCTimestampType.class ) {
                        b.append( "        if ( Constants.UNSET_LONG != get" ).append( methodBase ).append( "() && 0 != get" ).append( methodBase ).append( "() ) {\n" );
                        b.append( "            out.append( \", " ).append( attrName ).append( "=\" );\n" );
                        b.append( "            TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( out, get" ).append( methodBase ).append( "() );\n" );
                        b.append( "            out.append( \" / \" );\n" );
                        b.append( "            TimeUtilsFactory.safeTimeUtils().unixTimeToUTCTimestamp( out, get" ).append( methodBase ).append( "() );\n" );
                        b.append( "            out.append( \" ( \" );\n" );
                        b.append( "            out.append( get" ).append( methodBase ).append( "() ).append( \" ) \" );\n" );
                        b.append( "        }\n" );
                    } else if ( type == ShortType.class ) {
                        b.append( "        if ( Constants.UNSET_SHORT != get" ).append( methodBase ).append( "() && 0 != get" ).append( methodBase ).append( "() ) " );
                        b.append( "            out.append( \", " ).append( attrName ).append( "=\" ).append( get" ).append( methodBase ).append( "() );\n" );
                    } else if ( type == DateType.class || type == IntType.class ) {
                        b.append( "        if ( Constants.UNSET_INT != get" ).append( methodBase ).append( "() && 0 != get" ).append( methodBase ).append( "() ) " );
                        b.append( "            out.append( \", " ).append( attrName ).append( "=\" ).append( get" ).append( methodBase ).append( "() );\n" );
                    } else if ( type == DoubleType.class ) {
                        b.append( "        if ( Utils.hasVal( get" ).append( methodBase ).append( "() ) ) out.append( \", " ).append( attrName ).append( "=\" ).append( get" ).append( methodBase ).append( "() );\n" );
                    } else if ( type == FloatType.class ) {
                        b.append( "        if ( Utils.hasVal( get" ).append( methodBase ).append( "() ) ) out.append( \", " ).append( attrName ).append( "=\" ).append( get" ).append( methodBase ).append( "() ) );\n" );
                    } else if ( attr.getType().getClass() == ReusableStringType.class || overrideViewStringToReusableString ) {
                        b.append( "        if ( get" ).append( methodBase + "().length() > 0 ) " );
                        b.append( "            out.append( \", " ).append( attrName ).append( "=\" ).append( get" ).append( methodBase ).append( "() );\n" );
                    } else if ( attr.getType().getClass() == ViewStringType.class ) {
                        b.append( "            out.append( \", " ).append( attrName ).append( "=\" ).append( get" ).append( methodBase ).append( "() );\n" );
                    } else {
                        b.append( "        if ( get" ).append( methodBase + "() != null ) " );
                        b.append( "            out.append( \", " ).append( attrName ).append( "=\" ).append( get" ).append( methodBase ).append( "() );\n" );
                    }

                } else {
                    if ( attr instanceof SubEventAttributeDefinition ) {

                        String typeClass = attr.getTypeId() + "Impl";
                        String tmpPtr    = "tPtr" + attrName;
                        String tmpIdx    = "tIdx" + attrName;

                        b.append( "\n        " ).append( typeClass ).append( " " ).append( tmpPtr ).append( " = (" ).append( typeClass ).append( ") get" )
                         .append( methodBase ).append( "();\n" );
                        b.append( "        int " ).append( tmpIdx ).append( "=0;\n" );
                        b.append( "\n        while( " ).append( tmpPtr ).append( " != null ) {\n" );
                        b.append( "            out.append( \" {#\" ).append( ++" ).append( tmpIdx ).append( " ).append( \"} \" );\n" );
                        b.append( "            " ).append( tmpPtr ).append( ".dump( out );\n" );
                        b.append( "            " ).append( tmpPtr ).append( " = " ).append( tmpPtr ).append( ".getNext();\n" );
                        b.append( "        }\n\n" );
                    } else if ( attr.isHandcrafted() ) {
                        b.append( "        if ( get" ).append( methodBase + "() != null ) " );
                        b.append( "            out.append( \", " ).append( attrName ).append( "=\" );\n" );
                        b.append( "        if ( get" ).append( methodBase ).append( "() != null ) out.append( get" ).append( methodBase ).append( "().id() );\n" );
                    } else { // internal types are enum and toString generates NO temp objects
                        b.append( "        if ( get" ).append( methodBase + "() != null ) " );
                        b.append( "            out.append( \", " ).append( attrName ).append( "=\" ).append( get" ).append( methodBase ).append( "() );\n" );
                    }
                }
            }
        }

        b.append( "    }\n\n" );
    }

    private void writeEventRecycler( InternalModel internal ) throws FileException, IOException {
        StringBuilder b = new StringBuilder();

        String fullPre = ModelConstants.FULL_EVENT_PRENAME;

        String className = "AllEventRecycler";

        File file = GenUtils.getJavaFile( _internal, ModelConstants.EVENT_PACKAGE, ModelConstants.FACTORY_PACKAGE, className );
        GenUtils.addPackageDef( b, _internal, ModelConstants.EVENT_PACKAGE, ModelConstants.FACTORY_PACKAGE, className );

        b.append( "import " ).append( _internal.getRootPackage() ).append( "." ).append( ModelConstants.EVENT_PACKAGE ).append( "." )
         .append( ModelConstants.IMPL_PACKAGE ).append( ".*;\n" );
        b.append( "import " ).append( _internal.getRootPackage() ).append( "." ).append( ModelConstants.EVENT_PACKAGE ).append( "." )
         .append( ModelConstants.RECYCLER_PACKAGE ).append( ".*;\n" );
        b.append( "import " ).append( _internal.getRootPackage() ).append( "." ).append( ModelConstants.CORE_PACKAGE ).append( "." )
         .append( ModelConstants.FULL_EVENT_ID_FILE_NAME ).append( ";\n" );

        b.append( "import com.rr.core.recycler.EventRecycler;\n" );
        b.append( "import com.rr.core.pool.SuperpoolManager;\n" );
        b.append( "import com.rr.core.lang.ReusableType;\n" );
        b.append( "import com.rr.core.lang.HasReusableType;\n" );

        b.append( "\npublic class " ).append( className ).append( " implements EventRecycler {\n\n" );

        b.append( "\n" );

        for ( ClassDefinition def : _internal.getClassDefinitions() ) {
            boolean isInterface = def.isInterface();

            if ( !isInterface ) {
                String id = def.getId();

                if ( def.getStreamSrc() == EventStreamSrc.both ) {
                    b.append( "    private " ).append( id ).append( "Recycler _" ).append( GenUtils.toLowerFirstChar( id ) ).append( "Recycler;\n" );
                } else {
                    b.append( "    private Client" ).append( id ).append( "Recycler _client" ).append( id ).append( "Recycler;\n" );
                    b.append( "    private Market" ).append( id ).append( "Recycler _market" ).append( id ).append( "Recycler;\n\n" );
                    b.append( "    private " ).append( fullPre ).append( id ).append( "Recycler _" ).append( GenUtils.toLowerFirstChar( id ) )
                     .append( "Recycler;\n\n" );
                }
            }
        }

        b.append( "\n" );

        b.append( "    public " ).append( className ).append( "() {\n" );

        b.append( "        SuperpoolManager sp = SuperpoolManager.instance();\n" );

        for ( ClassDefinition def : _internal.getClassDefinitions() ) {
            boolean isInterface = def.isInterface();

            if ( !isInterface ) {
                String id = def.getId();

                if ( def.getStreamSrc() == EventStreamSrc.both ) {
                    b.append( "        _" ).append( GenUtils.toLowerFirstChar( id ) ).append( "Recycler = sp.getRecycler( " ).append( id )
                     .append( "Recycler.class, " ).append( id ).append( "Impl.class );\n" );
                } else {
                    b.append( "        _client" ).append( id ).append( "Recycler = sp.getRecycler( Client" ).append( id ).append( "Recycler.class, Client" )
                     .append( id ).append( "Impl.class );\n" );
                    b.append( "        _market" ).append( id ).append( "Recycler = sp.getRecycler( Market" ).append( id ).append( "Recycler.class, Market" )
                     .append( id ).append( "Impl.class );\n\n" );
                    b.append( "        _" ).append( GenUtils.toLowerFirstChar( id ) ).append( "Recycler = sp.getRecycler( " ).append( fullPre ).append( id )
                     .append( "Recycler.class, " ).append( fullPre ).append( id ).append( "Impl.class );\n\n" );
                }
            }
        }

        b.append( "    }\n" );

        for ( ClassDefinition def : _internal.getClassDefinitions() ) {
            boolean isInterface = def.isInterface();

            if ( !isInterface ) {
                String id = def.getId();

                if ( def.getStreamSrc() == EventStreamSrc.both ) {
                    b.append( "    public void recycle( " ).append( id ).append( "Impl msg ) {\n" );
                    b.append( "        _" ).append( GenUtils.toLowerFirstChar( id ) ).append( "Recycler.recycle( msg );\n" );
                    b.append( "    }\n\n" );
                } else {
                    b.append( "    public void recycle( Client" ).append( id ).append( "Impl msg ) {\n" );
                    b.append( "        _client" ).append( id ).append( "Recycler.recycle( msg );\n" );
                    b.append( "    }\n\n" );

                    b.append( "    public void recycle( Market" ).append( id ).append( "Impl msg ) {\n" );
                    b.append( "        _market" ).append( id ).append( "Recycler.recycle( msg );\n" );
                    b.append( "    }\n\n" );

                    b.append( "    public void recycle( " ).append( fullPre ).append( id ).append( "Impl msg ) {\n" );
                    b.append( "        _" ).append( GenUtils.toLowerFirstChar( id ) ).append( "Recycler.recycle( msg );\n" );
                    b.append( "    }\n\n" );
                }
            }
        }

        b.append( "    @Override public void recycle( HasReusableType msg ) {\n" );
        b.append( "        if ( msg == null ) return;\n\n" );
        b.append( "        final ReusableType type = msg.getReusableType();\n\n" );
        b.append( "        switch( type.getId() ) {\n" );

        for ( ClassDefinition def : _internal.getClassDefinitions() ) {
            boolean isInterface = def.isInterface();

            if ( !isInterface ) {
                String id = def.getId();

                EventStreamSrc src = def.getStreamSrc();

                if ( src == EventStreamSrc.both ) {
                    b.append( "        case " ).append( GenUtils.getFullEventId( def, EventStreamSrc.both ) ).append( ":\n" );
                    b.append( "            _" ).append( GenUtils.toLowerFirstChar( id ) ).append( "Recycler.recycle( (" ).append( id )
                     .append( "Impl) msg );\n" );
                    b.append( "            break;\n" );
                } else {
                    b.append( "        case " ).append( GenUtils.getFullEventId( def, EventStreamSrc.client ) ).append( ":\n" );
                    b.append( "            _client" ).append( id ).append( "Recycler.recycle( (Client" ).append( id ).append( "Impl) msg );\n" );
                    b.append( "            break;\n" );
                }
            }
        }

        for ( ClassDefinition def : _internal.getClassDefinitions() ) {
            boolean isInterface = def.isInterface();

            if ( !isInterface ) {
                String id = def.getId();

                EventStreamSrc src = def.getStreamSrc();

                if ( src != EventStreamSrc.both ) {
                    b.append( "        case " ).append( GenUtils.getFullEventId( def, EventStreamSrc.exchange ) ).append( ":\n" );
                    b.append( "            _market" ).append( id ).append( "Recycler.recycle( (Market" ).append( id ).append( "Impl) msg );\n" );
                    b.append( "            break;\n" );
                }
            }
        }

        for ( ClassDefinition def : _internal.getClassDefinitions() ) {
            boolean isInterface = def.isInterface();

            if ( !isInterface ) {
                String id = def.getId();

                EventStreamSrc src = def.getStreamSrc();

                if ( src != EventStreamSrc.both ) {
                    b.append( "        case " ).append( GenUtils.getFullEventId( def, EventStreamSrc.recovery ) ).append( ":\n" );
                    b.append( "            _" ).append( GenUtils.toLowerFirstChar( id ) ).append( "Recycler.recycle( (" ).append( fullPre ).append( id )
                     .append( "Impl) msg );\n" );
                    b.append( "            break;\n" );
                }
            }
        }

        b.append( "        }\n" );
        b.append( "    }\n\n" );

        b.append( "}\n" );

        GenUtils.writeFile( file, b );
    }

    private void writeFactory( ClassDefinition def, EventStreamSrc src ) throws FileException, IOException {

        boolean isInterface = def.isInterface();

        String prefix = getPrefix( src );

        if ( !isInterface ) {
            String        id = def.getId();
            StringBuilder b  = new StringBuilder();

            String className    = prefix + id + "Factory";
            String factoryClass = prefix + id + "Impl";

            File file = GenUtils.getJavaFile( _internal, ModelConstants.EVENT_PACKAGE, ModelConstants.FACTORY_PACKAGE, className );
            GenUtils.addPackageDef( b, _internal, ModelConstants.EVENT_PACKAGE, ModelConstants.FACTORY_PACKAGE, className );

            b.append( "import " ).append( _internal.getRootPackage() ).append( "." ).append( ModelConstants.EVENT_PACKAGE ).append( "." )
             .append( ModelConstants.IMPL_PACKAGE ).append( "." ).append( factoryClass ).append( ";\n" );
            b.append( "import com.rr.core.pool.PoolFactory;\n" );
            b.append( "import com.rr.core.pool.SuperPool;\n" );

            b.append( "\npublic class " ).append( className ).append( " implements PoolFactory<" ).append( factoryClass ).append( "> {\n\n" );
            b.append( "    private SuperPool<" ).append( factoryClass ).append( "> _superPool;\n\n" );
            b.append( "    private " ).append( factoryClass ).append( " _root;\n\n" );
            b.append( "    public " ).append( className ).append( "(  SuperPool<" ).append( factoryClass ).append( "> superPool ) {\n" );
            b.append( "        _superPool = superPool;\n" );
            b.append( "        _root = _superPool.getChain();\n" );
            b.append( "    }\n\n" );
            b.append( "\n" );
            b.append( "    @Override public " ).append( factoryClass ).append( " get() {\n" );
            b.append( "        if ( _root == null ) {\n" );
            b.append( "            _root = _superPool.getChain();\n" );
            b.append( "        }\n" );
            b.append( "        " ).append( factoryClass ).append( " obj = _root;\n" );
            b.append( "        _root = _root.getNext();\n" );
            b.append( "        obj.setNext( null );\n" );
            b.append( "        return obj;\n" );
            b.append( "    }\n" );
            b.append( "}\n" );

            GenUtils.writeFile( file, b );
        }
    }

    private void writeHelperMethods( EventStreamSrc src,
                                     String className,
                                     StringBuilder b,
                                     ClassDefinition def,
                                     boolean isSrcSide,
                                     boolean isSrcClient,
                                     String prefix ) {

        b.append( "    @Override\n" );
        b.append( "    public void setFlag( MsgFlag flag, boolean isOn ) {\n" );
        b.append( "        _flags = MsgFlag.setFlag( _flags, flag, isOn );\n" );
        b.append( "    }\n\n" );

        b.append( "    @Override\n" );
        b.append( "    public boolean isFlagSet( MsgFlag flag ) {\n" );
        b.append( "        return MsgFlag.isOn( _flags, flag );\n" );
        b.append( "    }\n\n" );

        b.append( "    @Override\n" );
        b.append( "    public int getFlags() {\n" );
        b.append( "        return _flags;\n" );
        b.append( "    }\n\n" );

        writeDumpMethod( src, className, b, def, isSrcSide, isSrcClient );
        writeDeepCopy( src, className, b, def, isSrcSide, isSrcClient );
        writeShallowCopy( src, className, b, def, isSrcSide, isSrcClient );
        writeShallowMerge( src, className, b, def, isSrcSide, isSrcClient );
    }

    private void writeImpl( EventStreamSrc src,
                            ClassDefinition def,
                            String id,
                            boolean isSrcSide,
                            boolean isSrcClient ) throws FileException, IOException {

        StringBuilder b = new StringBuilder();

        String prefix = getPrefix( src );

        String className = prefix + id + "Impl";

        File file = GenUtils.getJavaFile( _internal, ModelConstants.EVENT_PACKAGE, ModelConstants.IMPL_PACKAGE, className );
        GenUtils.addPackageDef( b, _internal, ModelConstants.EVENT_PACKAGE, ModelConstants.IMPL_PACKAGE, className );

        ClassDefinition base = def.getBaseDefinition();
        String          writeInterface;

        // Recovery interface must extend the same interface as the Client/Market side Write interface to make them interchangeable
        String extraWriteInterface = "";

        if ( src == EventStreamSrc.recovery ) {
            writeInterface = id + "Write";

            extraWriteInterface = ", Copyable<" + def.getId() + ">";

            if ( isSrcClientMarket( def.getStreamSrc() ) ) {
                extraWriteInterface += ", " + (((isSrcClient) ? "Client" : "Market") + id + "Write");
            }
        } else if ( isSrcSide ) {
            writeInterface = prefix + id + "Write";
        } else {
            writeInterface = prefix + id + "Update";
        }

        addClassImports( src, b, def, false, false, isSrcSide, isSrcClient );

        b.append( "\n@SuppressWarnings( { \"unused\", \"override\"  })\n" );

        b.append( "\npublic final class " ).append( className );

        String interfaces = writeInterface + extraWriteInterface + ", Reusable<" + className + ">";

        if ( base != null ) {
            b.append( " implements " ).append( base.getId() ).append( ", " ).append( interfaces );
        } else {
            b.append( " implements " ).append( interfaces );
        }

        b.append( " {\n" );

        b.append( "\n   // Attrs\n" );
        writeClassAttrs( src, className, b, def, isSrcSide, isSrcClient, false );

        writeClassHooks( src, className, b, def );

        b.append( "\n   // Getters and Setters\n" );
        writeClassMethods( src, b, id, def, isSrcSide, isSrcClient, false );

        b.append( "\n   // Reusable Contract\n" );
        writeReusableMethods( src, className, b, def, isSrcSide, isSrcClient, prefix );

        b.append( "\n   // Helper methods\n" );
        writeHelperMethods( src, className, b, def, isSrcSide, isSrcClient, prefix );

        b.append( "}\n" );

        GenUtils.writeFile( file, b );
    }

    private void writeInterface( EventStreamSrc src,
                                 String prefix,
                                 ClassDefinition def,
                                 String id,
                                 boolean isUpdateInterface,
                                 boolean isSrcSide,
                                 boolean isSrcClient ) throws FileException, IOException {

        StringBuilder b = new StringBuilder();

        String fileName = prefix + id;

        if ( isUpdateInterface ) {
            if ( isSrcSide || def.isInterface() || def.isSubEvent() ) {
                fileName = fileName + "Write";
            } else {
                fileName = fileName + "Update";
            }
        }

        File file;

        file = GenUtils.getJavaFile( _internal, ModelConstants.EVENT_PACKAGE, ModelConstants.INTERFACES_PACKAGE, fileName );
        GenUtils.addPackageDef( b, _internal, ModelConstants.EVENT_PACKAGE, ModelConstants.INTERFACES_PACKAGE, fileName );

        addClassImports( src, b, def, true, isUpdateInterface, isSrcSide, isSrcClient );

        ClassDefinition base = def.getBaseDefinition();

        b.append( "\n@SuppressWarnings( { \"unused\", \"override\"  })\n" );

        b.append( "\npublic interface " ).append( fileName ).append( " extends " );

        if ( base != null ) {
            b.append( base.getId() );

            if ( !isSrcClientMarket( src ) ) b.append( "Write" );

            b.append( ", " );
        }

        if ( isUpdateInterface ) {
            b.append( id );
        } else {
            b.append( "Event" );
        }

        if ( def.getExtraInterfaces() != null ) {
            b.append( ", " ).append( def.getExtraInterfaces() );
        }

        b.append( " {\n\n   // Getters and Setters\n" );
        writeInterfaceMethods( src, b, def, isUpdateInterface, isSrcSide, isSrcClient );

        b.append( "}\n" );

        GenUtils.writeFile( file, b );
    }

    private void writeInterfaceMethods( EventStreamSrc src, StringBuilder b, ClassDefinition def, boolean isUpdInt, boolean isSrcSide, boolean isSrcClient ) {

        Collection<AttributeDefinition> attrs = def.getAttributes( isUpdInt && isSrcClientMarket( src ) );

        String id = def.getId();

        boolean baseRequired = false;

        for ( AttributeDefinition attr : attrs ) {
            String              attrName   = attr.getAttrName();
            String              methodBase = GenUtils.toUpperFirstChar( attrName );
            OutboundInstruction inst       = attr.getInstruction();

            DelegateType delegateType  = GenUtils.getDelegateType( src, isSrcSide, isSrcClient, inst );
            boolean      attrInherited = def.isAttrInherited( attrName );

            if ( isUpdInt ) {

                if ( delegateType == DelegateType.Delegate ) {
                    baseRequired = true;
                } else {
                    if ( delegateType != DelegateType.EmptyDelegateThrowException ) {
                        // src side interface (Write) ... all attrs have setters
                        // or other side interface (Update)  ... and is not specified as seperate not delegated

                        boolean writeSetter = true;

                        addAnAttrMethods( src, id, attr, b, true, false, writeSetter, delegateType, attrName, methodBase, isSrcSide, attrInherited, inst, isUpdInt );
                        b.append( "\n" );
                    }
                }
            } else {

                boolean writeSetter = (delegateType == DelegateType.DelegateGetAndSet);

                addAnAttrMethods( src, id, attr, b, true, true, writeSetter, delegateType, attrName, methodBase, isSrcSide, attrInherited, inst, isUpdInt );
                b.append( "\n" );
            }
        }

        if ( baseRequired ) {
            b.append( "\n    void setSrcEvent( OrderRequest request );\n" );
            b.append( "    OrderRequest getSrcEvent();\n\n" );
        }

        if ( !isUpdInt ) {
            b.append( "    @Override void dump( ReusableString out );\n\n" );
        }

    }

    private void writeRecycler( ClassDefinition def, EventStreamSrc src ) throws FileException, IOException {

        boolean isInterface = def.isInterface();

        String prefix = getPrefix( src );

        if ( !isInterface ) {
            String        id = def.getId();
            StringBuilder b  = new StringBuilder();

            String className    = prefix + id + "Recycler";
            String factoryClass = prefix + id + "Impl";

            File file = GenUtils.getJavaFile( _internal, ModelConstants.EVENT_PACKAGE, ModelConstants.RECYCLER_PACKAGE, className );
            GenUtils.addPackageDef( b, _internal, ModelConstants.EVENT_PACKAGE, ModelConstants.RECYCLER_PACKAGE, className );

            b.append( "import " ).append( _internal.getRootPackage() ).append( "." ).append( ModelConstants.EVENT_PACKAGE ).append( "." )
             .append( ModelConstants.IMPL_PACKAGE ).append( "." ).append( factoryClass ).append( ";\n" );
            b.append( "import com.rr.core.pool.Recycler;\n" );
            b.append( "import com.rr.core.pool.SuperPool;\n" );
            b.append( "import com.rr.core.lang.Constants;\n" );

            b.append( "import com.rr.core.pool.RuntimePoolingException;\n" );

            addSubRecyclerImports( def, src, prefix, b );

            b.append( "\npublic class " ).append( className ).append( " implements Recycler<" ).append( factoryClass ).append( "> {\n\n" );
            b.append( "    private SuperPool<" ).append( factoryClass ).append( "> _superPool;\n\n" );

            addSubRecyclerVars( def, src, prefix, b );

            b.append( "    private " ).append( factoryClass ).append( " _root;\n\n" );
            b.append( "    private int          _recycleSize;\n" );
            b.append( "    private int          _count = 0;\n" );
            b.append( "    public " ).append( className ).append( "( int recycleSize, SuperPool<" ).append( factoryClass ).append( "> superPool ) {\n" );
            b.append( "        _superPool = superPool;\n" );
            b.append( "        _recycleSize = recycleSize;\n" );
            b.append( "        try {\n" );
            b.append( "            _root    = " ).append( factoryClass ).append( ".class.newInstance();\n" );
            b.append( "        } catch( Exception e ) {\n" );
            b.append( "            throw new RuntimePoolingException( \"Unable to create recycle root for " ).append( factoryClass )
             .append( " : \" + e.getMessage(), e );\n" );
            b.append( "        }\n" );
            b.append( "    }\n\n" );
            b.append( "\n" );
            b.append( "    @Override public void recycle( " ).append( factoryClass ).append( " obj ) {\n" );
            b.append( "        if ( Constants.DISABLE_RECYCLING ) return;\n" );
            b.append( "        if ( obj == null ) return;\n" );
            b.append( "        if ( obj.getNext() == null ) {\n" );

            addSubEventRecycling( def, src, prefix, b );

            b.append( "            obj.reset();\n" );
            b.append( "            obj.setNext( _root.getNext() );\n" );
            b.append( "            _root.setNext( obj );\n" );
            b.append( "            if ( ++_count == _recycleSize ) {\n" );
            b.append( "                _superPool.returnChain( _root.getNext() );\n" );
            b.append( "                _root.setNext( null );\n" );
            b.append( "                _count = 0;\n" );
            b.append( "            }\n" );
            b.append( "        }\n" );
            b.append( "    }\n" );
            b.append( "}\n" );

            GenUtils.writeFile( file, b );
        }
    }

    private void writeReusableMethods( EventStreamSrc src,
                                       String className,
                                       StringBuilder b,
                                       ClassDefinition def,
                                       boolean isSrcSide,
                                       boolean isSrcClient,
                                       String prefix ) {

        b.append( "\n" );

        b.append( "    @Override\n" );
        b.append( "    public final void reset() {\n" );

        boolean needBase = doWriteResetMethodAttrs( src, b, def, isSrcSide, isSrcClient );

        if ( needBase ) {
            b.append( "        _srcEvent = null;\n" );
        }

        b.append( "        _flags = 0;\n" );
        b.append( "        _next = null;\n" );
        b.append( "        _nextMessage = null;\n" );
        b.append( "        _messageHandler = null;\n" );

        b.append( "    }\n\n" );

        b.append( "    @Override\n" );
        b.append( "    public final ReusableType getReusableType() {\n" );
        b.append( "        return " + ModelConstants.REUSABLE_TYPES_FILE_NAME + "." ).append( prefix ).append( def.getReusableType() ).append( ";\n" );
        b.append( "    }\n\n" );

        b.append( "    @Override\n" );
        b.append( "    public final " ).append( className ).append( " getNext() {\n" );
        b.append( "        return _next;\n" );
        b.append( "    }\n\n" );

        b.append( "    @Override\n" );
        b.append( "    public final void setNext( " ).append( className ).append( " nxt ) {\n" );
        b.append( "        _next = nxt;\n" );
        b.append( "    }\n\n" );

        b.append( "    @Override\n" );
        b.append( "    public final void detachQueue() {\n" );
        b.append( "        _nextMessage = null;\n" );
        b.append( "    }\n\n" );

        b.append( "    @Override\n" );
        b.append( "    public final Event getNextQueueEntry() {\n" );
        b.append( "        return _nextMessage;\n" );
        b.append( "    }\n\n" );

        b.append( "    @Override\n" );
        b.append( "    public final void attachQueue( Event nxt ) {\n" );
        b.append( "        _nextMessage = nxt;\n" );
        b.append( "    }\n\n" );

        b.append( "    @Override\n" );
        b.append( "    public final EventHandler getEventHandler() {\n" );
        b.append( "        return _messageHandler;\n" );
        b.append( "    }\n\n" );

        b.append( "    @Override\n" );
        b.append( "    public final void setEventHandler( EventHandler handler ) {\n" );
        b.append( "        _messageHandler = handler;\n" );
        b.append( "    }\n\n" );
    }

    private void writeShallowCopy( EventStreamSrc src, String className, StringBuilder b, ClassDefinition def, boolean isSrcSide, boolean isSrcClient ) {

        if ( src == EventStreamSrc.client || src == EventStreamSrc.exchange ) return;

        b.append( "    /** shallow copy all primitive members ... EXCLUDING subEvents */\n" );
        b.append( "    @Override public final void shallowCopyFrom( " + def.getId() + " src ) {\n" );

        Collection<AttributeDefinition> attrs = def.getAttributes( true );

        Set<String> counterTags = getCounterSubEventTags( attrs );

        for ( AttributeDefinition attr : attrs ) {
            String attrName = attr.getAttrName();

            if ( counterTags.contains( attrName ) ) continue; // SKIP

            String              methodBase   = GenUtils.toUpperFirstChar( attrName );
            OutboundInstruction inst         = attr.getInstruction();
            DelegateType        delegateType = GenUtils.getDelegateType( src, isSrcSide, isSrcClient, inst );

            if ( delegateType != DelegateType.EmptyDelegateThrowException ) {
                if ( attr.isPrimitive() ) {
                    PrimitiveType type = (PrimitiveType) attr.getType();
                    int           size = type.getArraySize();

                    String typeDef = attr.getType().getTypeDefinition();

                    boolean overrideViewStringToReusableString = checkOverrideAsReusableString( src, attr, isSrcSide );

                    if ( attr.getType().getClass() == ReusableStringType.class || overrideViewStringToReusableString ) {
                        b.append( "        get" ).append( methodBase ).append( "ForUpdate().copy( " ).append( "src.get" ).append( methodBase ).append( "() );\n" );
                    } else if ( attr.getType().getClass() == ViewStringType.class ) {
                        b.append( "        // IGNORE VIEWSTRING : " ).append( attrName ).append( "\n" );
                    } else if ( size > 1 ) {

                        throw new RuntimeException( "Array types not yet implemented " + attrName + ", type=" + type.getClass() + ", len=" + size );

                    } else {
                        b.append( "        set" ).append( methodBase ).append( "( " ).append( "src.get" ).append( methodBase ).append( "() );\n" );
                    }

                } else {
                    if ( attr instanceof SubEventAttributeDefinition ) {
                        // skip
                    } else if ( attr.isHandcrafted() ) {
                        b.append( "        set" ).append( methodBase ).append( "( " ).append( "src.get" ).append( methodBase ).append( "() );\n" );
                    } else { // internal types are enum and toString generates NO temp objects
                        b.append( "        set" ).append( methodBase ).append( "( " ).append( "src.get" ).append( methodBase ).append( "() );\n" );
                    }
                }
            }
        }

        b.append( "    }\n\n" );
    }

    private void writeShallowMerge( EventStreamSrc src, String className, StringBuilder b, ClassDefinition def, boolean isSrcSide, boolean isSrcClient ) {

        if ( src == EventStreamSrc.client || src == EventStreamSrc.exchange ) return;

        b.append( "    /** shallow copy all primitive members ... EXCLUDING subEvents */\n" );
        b.append( "    @Override public final void shallowMergeFrom( " + def.getId() + " src ) {\n" );

        Collection<AttributeDefinition> attrs = def.getAttributes( true );

        Set<String> counterTags = getCounterSubEventTags( attrs );

        for ( AttributeDefinition attr : attrs ) {
            String attrName = attr.getAttrName();

            if ( counterTags.contains( attrName ) ) continue; // SKIP

            String              methodBase   = GenUtils.toUpperFirstChar( attrName );
            OutboundInstruction inst         = attr.getInstruction();
            DelegateType        delegateType = GenUtils.getDelegateType( src, isSrcSide, isSrcClient, inst );

            if ( delegateType != DelegateType.EmptyDelegateThrowException ) {
                if ( attr.isPrimitive() ) {
                    PrimitiveType attrType = (PrimitiveType) attr.getType();
                    Class<?>      type     = attrType.getClass();

                    String  typeDef                            = attr.getType().getTypeDefinition();
                    boolean overrideViewStringToReusableString = checkOverrideAsReusableString( src, attr, isSrcSide );

                    if ( type == BooleanType.class || type == CharType.class ) {
                        b.append( "        set" ).append( methodBase ).append( "( " ).append( "src.get" ).append( methodBase ).append( "() );\n" );
                    } else if ( type == LongType.class || type == UTCTimestampType.class ) {
                        b.append( "        if ( Constants.UNSET_LONG != src.get" ).append( methodBase ).append( "() ) " );
                        b.append( "set" ).append( methodBase ).append( "( " ).append( "src.get" ).append( methodBase ).append( "() );\n" );
                    } else if ( type == ShortType.class ) {
                        b.append( "        if ( Constants.UNSET_SHORT != src.get" ).append( methodBase ).append( "() ) " );
                        b.append( "set" ).append( methodBase ).append( "( " ).append( "src.get" ).append( methodBase ).append( "() );\n" );
                    } else if ( type == DateType.class || type == IntType.class ) {
                        b.append( "        if ( Constants.UNSET_INT != src.get" ).append( methodBase ).append( "() ) " );
                        b.append( "set" ).append( methodBase ).append( "( " ).append( "src.get" ).append( methodBase ).append( "() );\n" );
                    } else if ( type == DoubleType.class ) {
                        b.append( "        if ( Utils.hasVal( src.get" ).append( methodBase ).append( "() ) ) " );
                        b.append( "set" ).append( methodBase ).append( "( " ).append( "src.get" ).append( methodBase ).append( "() );\n" );
                    } else if ( type == FloatType.class ) {
                        b.append( "        if ( Utils.hasVal( src.get" ).append( methodBase ).append( "() ) ) " );
                        b.append( "set" ).append( methodBase ).append( "( " ).append( "src.get" ).append( methodBase ).append( "() );\n" );
                    } else if ( attr.getType().getClass() == ReusableStringType.class || overrideViewStringToReusableString ) {
                        b.append( "        if ( src.get" ).append( methodBase + "().length() > 0 ) get" ).append( methodBase ).append( "ForUpdate().copy( " ).append( "src.get" ).append( methodBase ).append( "() );\n" );
                    } else if ( attr.getType().getClass() == ViewStringType.class ) {
                        b.append( "        // IGNORE VIEWSTRING : " ).append( attrName ).append( "\n" );
                    } else {
                        throw new RuntimeException( "Bad event attr type of " + type + " for attr " + attr );
                    }

                } else {
                    if ( attr instanceof SubEventAttributeDefinition ) {
                        // skip
                    } else if ( attr.isHandcrafted() ) {
                        b.append( "        if ( get" ).append( methodBase ).append( "() != null ) " );
                        b.append( " set" ).append( methodBase ).append( "( " ).append( "src.get" ).append( methodBase ).append( "() );\n" );
                    } else { // internal types are enum and toString generates NO temp objects
                        b.append( "        set" ).append( methodBase ).append( "( " ).append( "src.get" ).append( methodBase ).append( "() );\n" );
                    }
                }
            }
        }

        b.append( "    }\n\n" );
    }

    private void writeSubEventImpl( EventStreamSrc src, ClassDefinition def, String id ) throws FileException, IOException {

        StringBuilder b = new StringBuilder();

        boolean isSrcSide   = true;
        boolean isSrcClient = true;

        String className = id + "Impl";

        File file = GenUtils.getJavaFile( _internal, ModelConstants.EVENT_PACKAGE, ModelConstants.IMPL_PACKAGE, className );
        GenUtils.addPackageDef( b, _internal, ModelConstants.EVENT_PACKAGE, ModelConstants.IMPL_PACKAGE, className );

        addClassImports( src, b, def, false, false, isSrcSide, isSrcClient );

        b.append( "\n@SuppressWarnings( { \"unused\", \"override\"  })\n" );

        b.append( "\npublic final class " ).append( className );

        b.append( " implements " ).append( id ).append( ", Reusable<" ).append( className ).append( ">" ).append( ", Copyable<" ).append( def.getId() ).append( ">" );

        b.append( " {\n" );

        b.append( "\n   // Attrs\n" );
        writeClassAttrs( src, className, b, def, isSrcSide, isSrcClient, true );

        writeClassHooks( src, className, b, def );

        b.append( "\n   // Getters and Setters\n" );
        doWriteClassMethods( src, b, id, def, isSrcSide, isSrcClient, false );

        b.append( "\n   // Reusable Contract\n" );
        writeSubEventReusableMethods( src, className, b, def );

        b.append( "\n   // Helper methods\n" );
        writeDumpMethod( src, className, b, def, isSrcSide, isSrcClient );
        writeDeepCopy( src, className, b, def, isSrcSide, isSrcClient );
        writeShallowCopy( src, className, b, def, isSrcSide, isSrcClient );
        writeShallowMerge( src, className, b, def, isSrcSide, isSrcClient );

        b.append( "}\n" );

        GenUtils.writeFile( file, b );
    }

    private void writeSubEventInterface( EventStreamSrc src, ClassDefinition def, String id ) throws FileException, IOException {

        StringBuilder b = new StringBuilder();

        String fileName = id;

        File file;

        file = GenUtils.getJavaFile( _internal, ModelConstants.EVENT_PACKAGE, ModelConstants.INTERFACES_PACKAGE, fileName );
        GenUtils.addPackageDef( b, _internal, ModelConstants.EVENT_PACKAGE, ModelConstants.INTERFACES_PACKAGE, fileName );

        addClassImports( src, b, def, true, false, false, false );
        b.append( "import com.rr.model.internal.type.SubEvent;\n" );

        b.append( "\n@SuppressWarnings( { \"unused\", \"override\"  })\n" );

        b.append( "\npublic interface " ).append( fileName ).append( " extends SubEvent" );

        if ( def.getExtraInterfaces() != null ) {
            b.append( ", " ).append( def.getExtraInterfaces() );
        }

        b.append( " {\n\n   // Getters and Setters\n" );
        writeInterfaceMethods( src, b, def, false, false, true );
        writeInterfaceMethods( src, b, def, true, false, true );

        b.append( "}\n" );

        GenUtils.writeFile( file, b );
    }

    private void writeSubEventReusableMethods( EventStreamSrc src, String className, StringBuilder b, ClassDefinition def ) {

        b.append( "\n" );

        b.append( "    @Override\n" );
        b.append( "    public final void reset() {\n" );

        doWriteResetMethodAttrs( src, b, def, false, true );

        b.append( "        _flags = 0;\n" );
        b.append( "        _next = null;\n" );

        b.append( "    }\n\n" );

        b.append( "    @Override\n" );
        b.append( "    public final ReusableType getReusableType() {\n" );
        b.append( "        return " + ModelConstants.REUSABLE_TYPES_FILE_NAME + "." ).append( def.getReusableType() ).append( ";\n" );
        b.append( "    }\n\n" );

        b.append( "    @Override\n" );
        b.append( "    public final " ).append( className ).append( " getNext() {\n" );
        b.append( "        return _next;\n" );
        b.append( "    }\n\n" );

        b.append( "    @Override\n" );
        b.append( "    public final void setNext( " ).append( className ).append( " nxt ) {\n" );
        b.append( "        _next = nxt;\n" );
        b.append( "    }\n\n" );
    }

    /**
     * <Type id="BookingType">
     * <Description>Method for booking out this order. Used when notifying a broker that an order to be settled by
     * that broker is to be booked out as an OTC derivative (e.g. CFD or similar)</Description>
     * <Entry name="Regular" value="0"/>
     */
    private void writeTypeDefinition( TypeDefinition type ) throws FileException, IOException {

        if ( type.isHandCrafted() ) return;

        String        fileName = type.getId();
        StringBuilder b        = new StringBuilder();

        int maxOccurs      = type.getMaxMultiValue();
        int maxEntryValLen = type.getMaxEntryValueLen();

        File file = GenUtils.getJavaFile( _internal, ModelConstants.TYPE_PACKAGE, fileName );
        GenUtils.addPackageDef( b, _internal, ModelConstants.TYPE_PACKAGE, fileName );

        b.append( "\n/**\n " ).append( type.getDesc() ).append( "\n*/\n\n" );

        if ( maxEntryValLen > 2 ) {
            b.append( "import java.util.Map;\n" );
            b.append( "import java.util.HashMap;\n" );
            b.append( "import com.rr.core.lang.*;\n" );
        }

        b.append( "import com.rr.core.utils.*;\n" );
        b.append( "import com.rr.model.internal.type.*;\n" );
        b.append( "import com.rr.core.model.*;\n" );
        b.append( "import com.rr.core.codec.RuntimeDecodingException;\n" );
        b.append( "import " ).append( _internal.getRootPackage() ).append( "." ).append( ModelConstants.TYPE_PACKAGE ).append( "." )
         .append( ModelConstants.TYPE_ID_FILE_NAME ).append( ";\n" );

        b.append( "\n@SuppressWarnings( { \"unused\", \"override\"  })\n" );

        b.append( "\npublic enum " ).append( fileName );

        if ( maxEntryValLen == 1 ) {
            b.append( " implements SingleByteLookup" );
        } else if ( maxEntryValLen == 2 ) {
            b.append( " implements TwoByteLookup" );
        } else {
            b.append( " implements MultiByteLookup" );
        }

        if ( type.getExtraInterfaces() != null ) {
            b.append( ", " ).append( type.getExtraInterfaces() );
        }

        b.append( " {\n" );

        Collection<TypeEntry> entries = type.getEntries();

        boolean first = true;

        for ( TypeEntry typeEntry : entries ) {
            if ( first ) {
                first = false;
            } else {
                b.append( "," );
            }
            addTypeEntry( b, typeEntry, type );
        }

        b.append( ";\n\n" );

        b.append( "    public static int getMaxOccurs() { return " ).append( maxOccurs ).append( "; }\n\n" );
        b.append( "    public static int getMaxValueLen() { return " ).append( maxEntryValLen ).append( "; }\n\n" );

        Collection<AttributeDefinition> attributes = type.getAttributes();

        if ( attributes != null ) {

            int    valLen  = type.getMaxEntryValueLen();
            String valType = (valLen > 1) ? "byte[]" : "byte";

            b.append( "    private final " ).append( valType ).append( " _val;\n" );
            b.append( "    private final int _id;\n" );

            for ( AttributeDefinition attr : attributes ) {

                AttrType attrType = attr.getType();

                if ( attr.isPrimitive() ) {
                    b.append( "    private " ).append( attrType.getTypeDeclaration() ).append( " _" ).append( attr.getAttrName() ).append( " = " )
                     .append( attr.getDefaultValue() ).append( ";\n" );
                } else {
                    b.append( "    private " ).append( attrType.getTypeDeclaration() ).append( " _" ).append( attr.getAttrName() ).append( " = " )
                     .append( attrType.getTypeDeclaration() ).append( "." ).append( attr.getDefaultValue() ).append( ";\n" );
                }
            }
        }

        addTypeConstructor( b, type );
        addTypeValueOf( b, type );

        b.append( "    public final int getID() {\n" );
        b.append( "        return _id;\n" );
        b.append( "    }\n\n" );

        if ( attributes != null ) { // add getters

            for ( AttributeDefinition attr : attributes ) {

                AttrType attrType = attr.getType();

                b.append( "    public " ).append( attrType.getTypeDeclaration() ).append( " get" ).append( GenUtils.toUpperFirstChar( attr.getAttrName() ) )
                 .append( "() { return _" ).append( attr.getAttrName() ).append( "; }\n" );

                b.append( "    public void set" ).append( GenUtils.toUpperFirstChar( attr.getAttrName() ) ).append( "( " )
                 .append( attrType.getTypeDeclaration() ).append( " val ) { _" ).append( attr.getAttrName() ).append( " = val; }\n" );
            }
        }

        b.append( "}\n" );

        GenUtils.writeFile( file, b );
    }
}
