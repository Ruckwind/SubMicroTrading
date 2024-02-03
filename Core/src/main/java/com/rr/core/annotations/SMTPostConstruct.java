package com.rr.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.METHOD )

/**
 * invoked by component loader after all properties set
 */
public @interface SMTPostConstruct {

}
