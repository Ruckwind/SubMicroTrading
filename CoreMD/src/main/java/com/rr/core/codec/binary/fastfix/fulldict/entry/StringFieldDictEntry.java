/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.fulldict.entry;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ZString;

public final class StringFieldDictEntry implements DictEntry {

    private final ReusableString _init = new ReusableString();
    private final ReusableString _val  = new ReusableString();

    public StringFieldDictEntry( ZString init ) {
        this( init.toString() );
    }

    public StringFieldDictEntry( String init ) {
        _init.copy( init );
    }

    @Override
    public boolean hasValue() {
        return _val != null && _val.length() > 0;
    }

    @Override
    public void log( ReusableString dest ) {
        dest.append( _val );
    }

    @Override
    public void reset() {
        _val.copy( _init );
    }

    public ReusableString getInit() {
        return _init;
    }

    public ReusableString getVal() {
        return _val;
    }

    public void setVal( ReusableString val ) {
        _val.copy( val );
    }
}
