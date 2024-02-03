package com.rr.model.generator;

import com.rr.model.xml.XMLException;
import com.rr.model.xml.XMLHelper;
import com.rr.model.xml.XMLMissingException;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XPointerTest {

    @Test
    public void testPointer1() throws XMLException {
        final XMLHelper helper = new XMLHelper( "xml/test/ptr1.xml" );

        helper.parse();

        Element  internalModelElem = helper.getElement( "InternalModel", true );
        NodeList sizes             = helper.getElements( internalModelElem, "DefaultSize", true );

        Assert.assertEquals( 4, sizes.getLength() );

        chk( helper, sizes.item( 0 ), "P2B" );
        chk( helper, sizes.item( 1 ), "P1A" );
        chk( helper, sizes.item( 2 ), "P1B" );
        chk( helper, sizes.item( 3 ), "P2A" );
    }

    private void chk( final XMLHelper helper, final Node node, final String expId ) throws XMLMissingException {
        String id = helper.getAttr( node, "id", true );

        Assert.assertEquals( expId, id );
    }
}
