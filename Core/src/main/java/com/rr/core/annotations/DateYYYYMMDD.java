package com.rr.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.FIELD )

/**
 * mark a int field as a int in YYYYMMDD
 */
public @interface DateYYYYMMDD {

}
