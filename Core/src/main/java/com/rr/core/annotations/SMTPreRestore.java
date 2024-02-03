package com.rr.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.METHOD )

/**
 * invoked by component loader before persistence / snapshot / imports are loaded
 *
 * For example GFUT's referenced in import MUST be registered before the import
 * and if its a new GFUT for this process it must be registered before the standard customInit or the import will fail
 */
public @interface SMTPreRestore {

}
