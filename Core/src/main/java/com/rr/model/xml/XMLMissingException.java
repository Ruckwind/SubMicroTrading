/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.xml;

import org.w3c.dom.Node;

public class XMLMissingException extends XMLException {

    private static final long serialVersionUID = 1L;

    public XMLMissingException( String msg, Node node, Exception e ) {
        super( msg, node, e );
    }

    public XMLMissingException( String msg, Node node ) {
        super( msg, node );
    }
}
