/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.xml;

import org.w3c.dom.Node;

public class XMLException extends Exception {

    private static final long serialVersionUID = 1L;

    private Node _node;

    public XMLException( String msg, Exception e ) {
        super( msg );
        _node = null;
    }

    public XMLException( String msg, Node node, Exception e ) {
        super( msg, e );

        _node = node;
    }

    public XMLException( String msg, Node node ) {
        super( msg );

        _node = node;
    }

    public XMLException( String msg ) {
        super( msg );
        _node = null;
    }

    @Override
    public String getMessage() {
        String msg = super.getMessage() + ((_node == null) ? "" : ", node=" + XMLHelper.getNodeDesc( _node ) + ", node=" + _node + ", attrs=");

        msg += XMLHelper.getAttrsAsString( _node );

        return msg;
    }
}
