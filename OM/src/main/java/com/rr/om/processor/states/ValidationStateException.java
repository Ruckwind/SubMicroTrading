/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.processor.states;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.TLC;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;

public class ValidationStateException extends StateException {

    private static final long serialVersionUID = 1L;

    private ReusableString _message;

    /**
     * create a validation exception
     *
     * @param message
     */
    public ValidationStateException( ZString message ) {
        super();

        ReusableString s = TLC.instance().pop();
        s.setValue( message );
        _message = s;
    }

    public ViewString getValidationError() {
        return _message;
    }

    public void recycle() {
        TLC.instance().pushback( _message );
        _message = null;
    }
}
