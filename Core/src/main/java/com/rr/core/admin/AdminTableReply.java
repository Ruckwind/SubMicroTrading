/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.admin;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ZString;
import com.rr.core.model.Currency;

public class AdminTableReply implements AdminReply {

    private final int            _cols;
    private final String[]       _align;
    private final ReusableString _res    = new ReusableString();
    private       int            _curCol = 0;

    public AdminTableReply( String[] columns ) {
        this( columns, null );
    }

    public AdminTableReply( String[] columns, String[] align ) {
        _cols  = columns.length;
        _align = align;

        _res.append( "<table border=\"1\"><tr>" );

        if ( align != null ) {
            _res.append( "<colgroup>" );
            for ( int i = 0; i < align.length; i++ ) {
                _res.append( "<col align=\"" + align[ i ] + "\" />" );
            }
            _res.append( "</colgroup>" );
        }

        for ( int i = 0; i < _cols; ++i ) {
            _res.append( "<th>" ).append( columns[ i ] ).append( "</th>" );
        }
        _res.append( "</tr>" );
    }

    @Override
    public void add( ZString val ) {
        startCol();
        _res.append( val );
        endCol();
    }

    @Override
    public void add( String val ) {
        startCol();
        _res.append( val );
        endCol();
    }

    @Override
    public void add( boolean val ) {
        startCol();
        _res.append( (val) ? "true" : "false" );
        endCol();
    }

    @Override
    public void add( long val ) {
        startCol();
        _res.append( val );
        endCol();
    }

    @Override
    public void add( int val ) {
        startCol();
        _res.append( val );
        endCol();
    }

    @Override
    public void add( double val ) {
        startCol();
        _res.append( val, 2 );
        endCol();
    }

    @Override
    public void add( double val, int dp ) {
        startCol();
        _res.append( val, dp );
        endCol();
    }

    @Override
    public void add( double val, boolean cashFmt ) {
        startCol();
        _res.append( val, cashFmt );
        endCol();
    }

    @Override public void add( final double val, final boolean cashFmt, final Currency ccy ) {
        startCol();
        _res.append( val, cashFmt );
        if ( ccy != null ) {
            _res.append( " (" ).append( ccy.name() ).append( ")" );
        }
        endCol();
    }

    @Override
    public String end() {
        _res.append( "</table>" );
        return _res.toString();
    }

    private void endCol() {
        _res.append( "</td>" );
        if ( _curCol % _cols == 0 ) {
            _res.append( "</tr>" );
        }
    }

    private void startCol() {
        int colIdx = _curCol++ % _cols;

        if ( colIdx == 0 ) {
            _res.append( "<tr>" );
        }

        if ( _align == null ) {
            _res.append( "<td>" );
        } else {
            _res.append( "<td style=\"text-align:" + _align[ colIdx ] + "\">" );
        }
    }
}
