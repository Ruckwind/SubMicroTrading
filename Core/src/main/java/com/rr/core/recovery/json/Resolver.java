package com.rr.core.recovery.json;

import com.rr.core.lang.ZString;

public interface Resolver {

    /**
     * register a component link as missing
     *
     * @param src                the object with the missing link
     * @param fieldName          the name of the field in "src" that is unresolved
     * @param missingComponentId the id of the root components that "src" should be set too
     */
    void addMissingRef( Object src, String fieldName, String missingComponentId );

    void addMissingRef( MissingRef ref );

    /**
     * clean caches .. should be called before snapshot taken, before AND after restore
     */
    void clear();

    /**
     * fetch an object previously stored with specified jsonId
     *
     * @param jsonId
     * @return
     */
    Object fetch( int jsonId );

    /**
     * @param refId either the jsonId OR the smtId of top level component
     * @return
     */
    Object find( ZString refId );

    Object findBySMTComponentId( ZString tmpVal );

    /**
     * resolve any missing links
     *
     * @return true if all missing links resolved
     */
    boolean resolveMissing();

    /**
     * register the object in the resolver
     * <p>
     * register jsonId -> object AND smtComponentId to object  IF applicable
     *
     * @param id
     * @param res
     * @param registerWithSMTCompMgr if true register the component with SMT component manager IF not already registered
     */
    void store( int id, Object res, final boolean registerWithSMTCompMgr ) throws JSONException;
}
