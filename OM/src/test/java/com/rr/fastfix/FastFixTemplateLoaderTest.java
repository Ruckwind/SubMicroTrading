/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.fastfix;

import com.rr.core.lang.BaseTestCase;
import com.rr.md.fastfix.XMLFastFixTemplateLoader;
import com.rr.md.fastfix.meta.MetaTemplates;
import com.rr.md.fastfix.template.FastFixTemplateClassRegister;
import com.rr.md.fastfix.template.TemplateClassRegister;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FastFixTemplateLoaderTest extends BaseTestCase {

    @Test
    public void testLoadTemplates() {

        TemplateClassRegister reg  = new FastFixTemplateClassRegister();
        MetaTemplates         meta = new MetaTemplates();

        XMLFastFixTemplateLoader l = new XMLFastFixTemplateLoader( "data/cme/templates.xml" );

        l.load( reg, meta );

        assertEquals( 93, meta.size() );
    }
}
