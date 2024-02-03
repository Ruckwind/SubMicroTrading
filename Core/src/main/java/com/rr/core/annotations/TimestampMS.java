package com.rr.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.FIELD )

/**
 * mark a long field as a UTC timestamp in ms
 */
public @interface TimestampMS {

    /**
     * @return true if field should be encoded at readable string timestamp
     */
    boolean encodeAsString() default false;

}
