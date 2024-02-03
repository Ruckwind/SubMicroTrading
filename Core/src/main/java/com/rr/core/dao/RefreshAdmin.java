/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.dao;

import com.rr.core.admin.AdminAgent;
import com.rr.core.admin.AdminReply;
import com.rr.core.admin.AdminTableReply;
import com.rr.core.lang.Refreshable;
import com.rr.core.lang.ZString;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Book;
import com.rr.core.model.book.ApiMutatableBook;
import com.rr.core.model.book.DoubleSidedBookEntry;
import com.rr.core.model.book.DoubleSidedBookEntryImpl;
import com.rr.core.model.book.UnsafeL2Book;

import java.util.Iterator;

public class RefreshAdmin implements RefreshAdminMBean {

    private static final Logger _log = LoggerFactory.create( RefreshAdmin.class );

    private final Refreshable _src;
    private final String      _name;

    public RefreshAdmin( Refreshable src ) {
        _src  = src;
        _name = "RefreshAdmin-" + src.id();
    }

    @Override public String getName() {
        return _name;
    }

    @Override public String refresh() {
        String info = "RefreshAdmin.refresh " + _name;

        _log.info( info );

        try {
            _src.refresh();

            return " invoked";

        } catch( Exception e ) {
            return " ERROR " + e.getMessage();
        }
    }
}
