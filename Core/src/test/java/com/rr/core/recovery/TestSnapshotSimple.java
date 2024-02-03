package com.rr.core.recovery;

import com.rr.core.component.*;
import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.Env;
import com.rr.core.lang.ReusableString;
import com.rr.core.properties.AppProps;
import com.rr.core.properties.CoreProps;
import com.rr.core.recovery.impl.SnapshotSingleFileReaderImpl;
import com.rr.core.recovery.impl.SnapshotSingleFileWriterImpl;
import com.rr.core.utils.FileException;
import com.rr.core.utils.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class TestSnapshotSimple extends BaseTestCase {

    private static final SnapshotType TEST_SNAP_ID = SnapshotType.devTest;

    private SnapshotUtils _snapshotUtils;

    private SMTComponentManager _mgrWriter   = new SMTComponentManager();
    private SMTComponentManager _mgrRestored = new SMTComponentManager();

    private SnapshotSingleFileWriterImpl _snapshotWriter;
    private SnapshotSingleFileReaderImpl _snapshotReader;

    @Before public void setup() throws FileException {
        AppProps.instance().override( CoreProps.APP_NAME, "TestSnapshotSimple" );

        _snapshotUtils = new SnapshotUtils( "./tmp" );

        String snapDir = _snapshotUtils.makeSnapshotDir( Env.TEST, TEST_SNAP_ID );

        FileUtils.rmRecurse( snapDir, true );

        FileUtils.mkDirIfNeeded( snapDir );

        final long maxFileSize = 100;
        final int  blockSize   = 25;

        _snapshotWriter = new SnapshotSingleFileWriterImpl( "SW1", "./tmp" );
        _snapshotWriter.setBlockSize( blockSize );
        _snapshotWriter.setMaxFileSize( maxFileSize );
        SMTStartContext ctxWriter = new SMTCoreContext( "CTXW", _mgrWriter );
        _snapshotWriter.init( ctxWriter, CreationPhase.Config );

        SMTStartContext ctxRestored = new SMTCoreContext( "CTXR", _mgrRestored );
        _snapshotReader = new SnapshotSingleFileReaderImpl( "SR1", "./tmp" );
        _snapshotReader.init( ctxRestored, CreationPhase.Config );
    }

    @Test public void snapshot1DynamicObject() throws Exception {
        _mgrWriter.setCreationPhase( CreationPhase.Runtime );

        RecoverySampleClasses.SMTComponentWithJustIntAndString c1 = new RecoverySampleClasses.SMTComponentWithJustIntAndString( "sampleComponent" );

        ReusableString b = new ReusableString( 512 );

        for ( int i = 0; i < 15; i++ ) {
            String istr = String.format( "%02d", i );

            b.append( "I" + istr + "abcdefghijklmnopqrstuvwxyz0123456789\n" );
        }

        c1.setStrValA( b.toString() );

        _mgrWriter.add( c1 );

        LinkedHashSet<SMTSnapshotMember> components = new LinkedHashSet<>();
        Collections.addAll( components, c1 );
        SnapshotDefinition sd = new SnapshotDefinitionImpl( TEST_SNAP_ID, components );

        _snapshotWriter.takeSnapshot( sd );

        ArrayList<Object> restoredColl = new ArrayList<>();
        _snapshotReader.restoreLastSnapshot( sd, restoredColl );

        assertEquals( 1, restoredColl.size() );

        assertEquals( c1, restoredColl.get( 0 ) );
    }

    @Test public void snapshot2DynamicObject() throws Exception {
        _mgrWriter.setCreationPhase( CreationPhase.Runtime );

        RecoverySampleClasses.SMTComponentWithJustIntAndString c1 = new RecoverySampleClasses.SMTComponentWithJustIntAndString( "sampleComponent1" );
        RecoverySampleClasses.SMTComponentWithJustIntAndString c2 = new RecoverySampleClasses.SMTComponentWithJustIntAndString( "sampleComponent2" );

        ReusableString b = new ReusableString( 512 );

        for ( int i = 0; i < 15; i++ ) {
            String istr = String.format( "%02d", i );

            b.append( "I" + istr + "abcdefghijklmnopqrstuvwxyz0123456789\n" );
        }

        c1.setStrValA( b.toString() );
        c2.setStrValA( "someOtherVal" );

        _mgrWriter.add( c1 );
        _mgrWriter.add( c2 );

        LinkedHashSet<SMTSnapshotMember> components = new LinkedHashSet<>();
        Collections.addAll( components, c1, c2 );
        SnapshotDefinition sd = new SnapshotDefinitionImpl( TEST_SNAP_ID, components );

        _snapshotWriter.takeSnapshot( sd );

        ArrayList<Object> restoredColl = new ArrayList<>();
        _snapshotReader.restoreLastSnapshot( sd, restoredColl );

        assertEquals( 2, restoredColl.size() );

        assertEquals( c1, restoredColl.get( 0 ) );
        assertEquals( c2, restoredColl.get( 1 ) );
    }

    @Test public void snapshotStaticObjectWith1PersistField() throws Exception {

        RecoverySampleClasses.SnapMemWith2PersistableFields c1 = new RecoverySampleClasses.SnapMemWith2PersistableFields( "sampleComponent" );
        RecoverySampleClasses.SnapMemWith2PersistableFields c2 = new RecoverySampleClasses.SnapMemWith2PersistableFields( "sampleComponent" );

        c1.setStrValA( "aPersistedValue" );

        _mgrWriter.add( c1 );
        _mgrRestored.add( c2 );

        LinkedHashSet<SMTSnapshotMember> components = new LinkedHashSet<>();
        Collections.addAll( components, c1 );
        SnapshotDefinition sd = new SnapshotDefinitionImpl( TEST_SNAP_ID, components );

        _snapshotWriter.takeSnapshot( sd );

        ArrayList<Object> restoredColl = new ArrayList<>();
        _snapshotReader.restoreLastSnapshot( sd, restoredColl );

        assertEquals( 1, restoredColl.size() );

        assertSame( c2, restoredColl.get( 0 ) );

        assertEquals( c2, c1 );
    }

    @Test public void snapshotStaticObjectWithNoDelta() throws Exception {

        RecoverySampleClasses.SMTComponentWithJustIntAndString c1 = new RecoverySampleClasses.SMTComponentWithJustIntAndString( "sampleComponent" );

        ReusableString b = new ReusableString( 512 );

        for ( int i = 0; i < 15; i++ ) {
            String istr = String.format( "%02d", i );

            b.append( "I" + istr + "abcdefghijklmnopqrstuvwxyz0123456789\n" );
        }

        c1.setStrValA( b.toString() );

        _mgrWriter.add( c1 );
        _mgrRestored.add( c1 );

        LinkedHashSet<SMTSnapshotMember> components = new LinkedHashSet<>();
        Collections.addAll( components, c1 );
        SnapshotDefinition sd = new SnapshotDefinitionImpl( TEST_SNAP_ID, components );

        _snapshotWriter.takeSnapshot( sd );

        ArrayList<Object> restoredColl = new ArrayList<>();
        _snapshotReader.restoreLastSnapshot( sd, restoredColl );

        assertEquals( 1, restoredColl.size() );
        assertSame( c1, restoredColl.get( 0 ) );
    }

    @After public void teardown() throws FileException {
        AppProps.instance().override( CoreProps.APP_NAME, "TestSnapshotSimple" );

        FileUtils.rmRecurse( "./tmp/TestSnapshotSimple", true );
    }
}
