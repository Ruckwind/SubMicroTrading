package com.rr.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.METHOD )

/**
 * used by JSONReader to invoke methods to run after an object has been restored from snapshot
 *
 * be careful implementing this because two scenarious
 *
 * a) object was instantiated  by  JSON and no arg constructor called (or single String arg constructor if smtcomponent
 * b) object already existed and was created by normal startup process
 *
 */
public @interface PostRestoreObject {

}
