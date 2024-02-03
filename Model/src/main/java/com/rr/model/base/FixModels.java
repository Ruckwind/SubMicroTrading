/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class FixModels extends BaseModel {

    private final Map<String, String>   _versions = new LinkedHashMap<>();
    private final Map<String, FixModel> _models   = new LinkedHashMap<>();
    private final StringBuilder         _errors   = new StringBuilder();

    public void addFixModel( FixModel fixModel ) {
        _models.put( fixModel.getId(), fixModel );
    }

    public void addFixVersion( String name, String version ) {
        _versions.put( name, version );
    }

    public void clearErrors() {
        _errors.setLength( 0 );
    }

    public String getErrors() {
        return _errors.toString();
    }

    public FixModel getFixModel( String fixId ) {
        return _models.get( fixId );
    }

    public Collection<FixModel> getFixModels() {
        return _models.values();
    }

    public boolean verify( InternalModel internal ) {

        boolean valid = true;

        _errors.setLength( 0 );

        // check that fix version in fix model exists in the FixModels list of fix models

        for ( FixModel model : _models.values() ) {

            if ( !_versions.containsKey( model.getFixVersion() ) ) {
                valid = false;

                _errors.append( "FixModel has version which is not in version config " ).append( model.getFixVersion() ).append( ", id=" )
                       .append( model.getId() ).append( "\n" );
            }

            Collection<FixEventDefinition> msgs = model.getMessages();

            for ( FixEventDefinition def : msgs ) {
                Collection<Tag> tags = def.getTags().keySet();

                for ( Tag tag : tags ) {
                    if ( tag instanceof GroupPlaceholderTag ) {
                        GroupPlaceholderTag gTag = (GroupPlaceholderTag) tag;

                        if ( !def.hasTag( gTag.getCounterTag() ) ) {
                            _errors.append( "FixMessage repeating group counter tag=" ).append( gTag.getCounterTag() ).append( ", id=" ).append( gTag.getId() )
                                   .append( ", missing in " ).append( def.getName() );
                        }
                    }
                }
            }
        }

        return valid;
    }

}
