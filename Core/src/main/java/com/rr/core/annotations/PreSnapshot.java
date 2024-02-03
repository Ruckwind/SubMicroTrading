package com.rr.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.METHOD )

/**
 * invoked by snapshot caretaker on a REGISTERED component under component manager before taking snapshot
 *
 * check value of  BATCH_SNAPSHOT_NOTIF (default false) to see if  all components have pre snapshot invoked before any conversions take place
 */
public @interface PreSnapshot {

}
