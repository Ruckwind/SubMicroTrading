package com.rr.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.METHOD )

/**
 * marks a method to be run after initialisation .... for use by components such as services which are not allocated to backtest pipelines
 *
 * expect to be used in conjunction with {@link UniquePerBackTestPipeLine}
 */

public @interface StandAloneThreadedInit {

}
