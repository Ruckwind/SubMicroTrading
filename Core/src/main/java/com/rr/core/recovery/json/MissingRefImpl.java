package com.rr.core.recovery.json;

import com.rr.core.lang.ZConsumer;
import com.rr.core.model.Identifiable;
import com.rr.core.utils.ReflectUtils;

import java.util.ArrayList;
import java.util.List;

public class MissingRefImpl implements MissingRef {

    private Object                  _srcObject;
    private String                  _refCompId;
    private String                  _refFieldName;
    private List<ZConsumer<Object>> _setters = new ArrayList<>( 1 );

    public MissingRefImpl( final String refCompId ) {
        this( null, null, refCompId );
    }

    public MissingRefImpl( final Object srcObject, final String refFieldName, final String refCompId ) {
        _srcObject    = srcObject;
        _refFieldName = refFieldName;
        _refCompId    = refCompId;
    }

    @Override public void addResolver( final ZConsumer setter ) {
        _setters.add( setter );
    }

    @Override public String getRefComponentId()                        { return _refCompId; }

    @Override public void setRefComponentId( final String refCompId )  { _refCompId = refCompId; }

    @Override public String getRefFieldName()                          { return _refFieldName; }

    @Override public void setRefFieldName( final String refFieldName ) { _refFieldName = refFieldName; }

    @Override public Object getSrcObject()                             { return _srcObject; }

    @Override public void setSrcObject( final Object srcObject )       { _srcObject = srcObject; }

    @Override public void resolve( Object actualRef ) {
        if ( _setters.size() > 0 ) {
            for ( ZConsumer<Object> c : _setters ) {
                c.accept( actualRef );
            }
        } else {
            ReflectUtils.setMember( _srcObject, _refFieldName, actualRef );
        }
    }

    @Override public String toString() {
        String str = "MissingRefImpl srcObj=";
        if ( _srcObject instanceof Identifiable ) {
            str += ((Identifiable) _srcObject).id();
        } else {
            if ( _srcObject != null ) str += _srcObject.getClass().getSimpleName();
        }

        return str +
               ", refCompId='" + _refCompId + '\'' +
               ", refFieldName='" + _refFieldName + '\'' +
               '}';
    }
}
