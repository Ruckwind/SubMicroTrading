package com.rr.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.FIELD )

public @interface Persist {

    /**
     * @return true if field should be forced to be written as a jsonId of smtId reference
     */
    boolean forceSMTRef() default false;

    /**
     * when using ExportContainer addPersistableFields  if field has Persist annotation with nonExportable=true then field WONT be exported
     *
     * @return
     */
    boolean nonExportable() default false;
}
