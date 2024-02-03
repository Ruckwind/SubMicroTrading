/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.us.cme.reader;

import com.rr.core.codec.binary.fastfix.common.ComponentFactory;
import com.rr.core.codec.binary.fastfix.common.FieldReader;
import com.rr.core.codec.binary.fastfix.msgdict.DictComponentFactory;
import com.rr.core.codec.binary.fastfix.msgdict.ReaderFieldClassLookup;
import com.rr.core.lang.BaseTestCase;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.md.fastfix.XMLFastFixTemplateLoader;
import com.rr.md.fastfix.meta.*;
import com.rr.md.fastfix.template.FastFixTemplateClassRegister;
import com.rr.md.fastfix.template.TemplateClassRegister;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

public class FastFixToFixDump extends BaseTestCase {

    private ComponentFactory cf = new DictComponentFactory();

    private MetaTemplates meta = new MetaTemplates();

    public FastFixToFixDump() {
        // nothing
    }

    @Before
    public void setUp() {
        loadTemplates();
    }

    @Test
    public void testCodec3() {
        MetaTemplate            mt = meta.getTemplate( 103 );
        Iterator<MetaBaseEntry> it = mt.getEntryIterator();

        int idx = 0;

        while( it.hasNext() ) {
            MetaBaseEntry e = it.next();

            addField( e, ++idx );
        }
    }

    private void addField( MetaBaseEntry e, int idx ) {

        if ( e.getId() == 52 ) {
            logField( (MetaFieldEntry) e, idx );
        } else if ( e.getClass() == MetaSequenceEntry.class ) {
            System.out.println( "\n" );

            MetaSequenceEntry metaEntry = (MetaSequenceEntry) e;

            addField( metaEntry.getLengthField(), idx );

            Iterator<MetaBaseEntry> it = metaEntry.getEntryIterator();

            while( it.hasNext() ) {
                MetaBaseEntry subE = it.next();

                addField( subE, ++idx );
            }

            System.out.println( "\n" );

        } else if ( e.getClass() == MetaFieldEntry.class ) {
            logField( (MetaFieldEntry) e, idx );
        } else if ( e.getClass() == DecimalMetaFieldEntry.class ) {
            logField( (DecimalMetaFieldEntry) e, idx );
        } else {
            throw new SMTRuntimeException( "Unsupported fast fix meta class " + e );
        }
    }

    private void loadTemplates() {
        TemplateClassRegister reg = new FastFixTemplateClassRegister();

        XMLFastFixTemplateLoader l = new XMLFastFixTemplateLoader( "data/cme/templates.xml" );

        l.load( reg, meta );
    }

    private void logEntry( MetaBaseEntry e, int idx, FieldReader r ) {
        System.out.println( "    private final " + r.getClass().getSimpleName() + " _f" + idx + "_" + e.getName() + ";" );
    }

    @SuppressWarnings( "boxing" )
    private void logField( DecimalMetaFieldEntry e, int idx ) {
        Class<? extends FieldReader> rdrClass = ReaderFieldClassLookup.getCustomReaderClass( e.getExp().getOperator(), e.getMant().getOperator(), e.isOptional() );

        FieldReader r = cf.getReader( rdrClass, cf, e.getName(), e.getId(), e.getExp().getInitValue(), e.getMant().getInitValue() );

        logEntry( e, idx, r );
    }

    private void logField( MetaFieldEntry e, int idx ) {
        Class<? extends FieldReader> rdrClass = ReaderFieldClassLookup.getReaderClass( e.getOperator(), e.getType(), e.isOptional() );

        @SuppressWarnings( "boxing" )
        FieldReader r = cf.getReader( rdrClass, e.getName(), e.getId(), e.getInitValue() );

        logEntry( e, idx, r );
    }
}
