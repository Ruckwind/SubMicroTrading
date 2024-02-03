/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.admin;

import com.rr.core.lang.BaseTestCase;
import com.rr.core.utils.ThreadUtilsFactory;
import org.junit.Test;

import javax.management.ObjectInstance;

import static org.junit.Assert.*;

public class TestAdminAgent extends BaseTestCase {

    public static void main( String args[] ) {

        TestAdminAgent a = new TestAdminAgent();
        try {
            a.testAgent();

            System.out.println( "Start JConsole to test" );
            while( true ) {
                ThreadUtilsFactory.get().sleep( 1000 );
            }
        } catch( AdminException e ) {
            System.out.println( "Disabled" );
        }
    }

    @Test
    public void testAgent() throws AdminException {
        try {
            AdminAgent.init( 8000 );
            DummyAdminCommand beanA = new DummyAdminCommand( "AAA", "one" );
            DummyAdminCommand beanB = new DummyAdminCommand( "BBB", "two" );
            AdminAgent.register( beanA );
            AdminAgent.register( beanB );
            ObjectInstance mbA = AdminAgent.find( beanA.getName() );
            ObjectInstance mbB = AdminAgent.find( beanB.getName() );
            assertNotSame( mbA, mbB );
            assertNotNull( mbA );
            assertNotNull( mbB );
            Object aVal = AdminAgent.getAttribute( beanA.getName(), "Message" );
            assertEquals( beanA.getMessage(), aVal );
        } finally {
            AdminAgent.close();
        }
    }
}
