/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class BinaryModels extends BaseModel {

    private final Map<String, BinaryModel> _models = new LinkedHashMap<>();
    private final StringBuilder            _errors = new StringBuilder();

    public void addBinaryModel( BinaryModel BinaryModel ) {
        _models.put( BinaryModel.getId(), BinaryModel );
    }

    public void clearErrors() {
        _errors.setLength( 0 );
    }

    public BinaryModel getBinaryModel( String BinaryId ) {
        return _models.get( BinaryId );
    }

    public Collection<BinaryModel> getBinaryModels() {
        return _models.values();
    }

    public String getErrors() {
        return _errors.toString();
    }

    public boolean verify( InternalModel internal ) {
        boolean valid = true;
        _errors.setLength( 0 );
        return valid;
    }

}
