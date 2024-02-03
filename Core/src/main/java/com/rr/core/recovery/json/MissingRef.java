package com.rr.core.recovery.json;

import com.rr.core.lang.ZConsumer;

/**
 * forward reference
 * <p>
 * can either have a set function or field and member to set
 */
public interface MissingRef {

    /**
     * add a funtion to be invoked when resolving this missing reference
     *
     * @param setter
     */
    void addResolver( ZConsumer setter );

    /**
     * @retrun the component ID that the ref should be set too
     */
    String getRefComponentId();

    void setRefComponentId( String refCompId );

    /**
     * @return name of the field that should be set to the referenced object
     */
    String getRefFieldName();

    void setRefFieldName( String refFieldName );

    /**
     * @return the object with the missing ref
     */
    Object getSrcObject();

    void setSrcObject( Object srcObject );

    void resolve( Object actualRef );
}
