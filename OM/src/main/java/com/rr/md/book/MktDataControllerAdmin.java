/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.book;

import com.rr.core.admin.AdminReply;
import com.rr.core.admin.AdminTableReply;
import com.rr.core.lang.ZString;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Book;
import com.rr.core.model.book.ApiMutatableBook;
import com.rr.core.model.book.DoubleSidedBookEntry;
import com.rr.core.model.book.DoubleSidedBookEntryImpl;
import com.rr.core.model.book.UnsafeL2Book;

import java.util.Iterator;

public class MktDataControllerAdmin implements MktDataControllerAdminMBean {

    private static final Logger   _log             = LoggerFactory.create( MktDataControllerAdmin.class );
    private static final String[] _columnsFullBook = { "Level", "IsValid", "BidQty", "BidPx", "AskPx", "AckQty" };
    private static final String[] _columnsBBO      = { "SecDesc", "IsValid", "BidQty", "BidPx", "AskPx", "AckQty" };

    private final MktDataController<?> _bookController;
    private final String               _name;

    private final int                  _depth    = 10;
    private final ApiMutatableBook     _tmpBook  = new UnsafeL2Book( null, _depth );
    private final DoubleSidedBookEntry _tmpEntry = new DoubleSidedBookEntryImpl();

    public MktDataControllerAdmin( MktDataController<?> ctl ) {
        _bookController = ctl;
        _name           = "Admin-" + ctl.getComponentId();
    }

    @Override
    public String clearAllBooks() {
        _log.info( "MktDataControllerAdmin.clearAllBooks" );

        try {
            _bookController.clearAllBooks();

            return "cleared all books for controller";

        } catch( Exception e ) {
            return "Exception " + e.getMessage();
        }
    }

    @Override
    public String clearBook( String securityDesc ) {
        _log.info( "MktDataControllerAdmin.clearBook " + securityDesc );

        try {
            return (_bookController.clearBook( securityDesc ) ? "Cleared Book " + securityDesc : "Failed to Clear Book " + securityDesc);

        } catch( Exception e ) {
            return "Exception " + e.getMessage();
        }
    }

    @Override
    public String listAllTopBook() {
        @SuppressWarnings( "unchecked" )
        Iterator<Book> bookIter = (Iterator<Book>) _bookController.getBookIterator();

        AdminReply reply = new AdminTableReply( _columnsBBO );
        while( bookIter.hasNext() ) {
            Book book = bookIter.next();

            book.getLevel( 0, _tmpEntry );

            list( reply, book.getInstrument().getSecurityDesc(), book.isValid(), _tmpEntry );
        }

        return "getAllTopBook</br>" + reply.end();
    }

    @Override
    public String listBook( String securityDesc ) {
        String info = "MktDataControllerAdmin.getBook " + securityDesc;
        _log.info( info );

        if ( _bookController.getBook( securityDesc, _tmpBook ) ) {
            AdminReply reply = new AdminTableReply( _columnsFullBook );
            for ( int i = 0; i < _depth; i++ ) {
                _tmpBook.getLevel( i, _tmpEntry );

                list( reply, i, _tmpEntry );
            }
            return info + "</br>" + reply.end();
        }

        return "Unable to get book " + securityDesc;
    }

    @Override
    public String getName() {
        return _name;
    }

    private void list( AdminReply reply, int lvl, DoubleSidedBookEntry tmpEntry ) {
        reply.add( lvl );
        reply.add( tmpEntry.isValid() );
        reply.add( tmpEntry.getBidQty() );
        reply.add( tmpEntry.getBidPx() );
        reply.add( tmpEntry.getAskPx() );
        reply.add( tmpEntry.getAskQty() );
    }

    private void list( AdminReply reply, ZString secDesc, boolean isValid, DoubleSidedBookEntry tmpEntry ) {
        reply.add( secDesc );
        reply.add( isValid );
        reply.add( tmpEntry.getBidQty() );
        reply.add( tmpEntry.getBidPx() );
        reply.add( tmpEntry.getAskPx() );
        reply.add( tmpEntry.getAskQty() );
    }
}
