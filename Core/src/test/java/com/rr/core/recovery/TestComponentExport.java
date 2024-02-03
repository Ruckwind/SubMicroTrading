package com.rr.core.recovery;

import com.rr.core.component.*;
import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.Env;
import com.rr.core.lang.ReusableString;
import com.rr.core.properties.AppProps;
import com.rr.core.properties.CoreProps;
import com.rr.core.recovery.impl.ComponentMFileExportWriter;
import com.rr.core.recovery.impl.ComponentMFileImportReader;
import com.rr.core.utils.FileException;
import com.rr.core.utils.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class TestComponentExport extends BaseTestCase {

    private static final SnapshotType TEST_SNAP_ID = SnapshotType.exportBT;

    private SnapshotUtils _snapshotUtils;

    private SMTComponentManager _mgrWriter   = new SMTComponentManager();
    private SMTComponentManager _mgrRestored = new SMTComponentManager();

    private ComponentMFileExportWriter _exportWriter;
    private ComponentMFileImportReader _importReader;

    @Before public void setup() throws FileException {
        AppProps.instance().override( CoreProps.APP_NAME, "TestSnapshotSimple" );

        _snapshotUtils = new SnapshotUtils( "./tmp" );

        String snapDir = _snapshotUtils.makeSnapshotDir( Env.TEST, TEST_SNAP_ID );

        FileUtils.rmRecurse( snapDir, true );

        FileUtils.mkDirIfNeeded( snapDir );

        final long maxFileSize = 100;
        final int  blockSize   = 25;

        _exportWriter = new ComponentMFileExportWriter( "SW1", "./tmp" );
        _exportWriter.setBlockSize( blockSize );
        _exportWriter.setMaxFileSize( maxFileSize );
        SMTStartContext ctxWriter = new SMTCoreContext( "CTXW", _mgrWriter );
        _exportWriter.init( ctxWriter, CreationPhase.Config );

        SMTStartContext ctxRestored = new SMTCoreContext( "CTXR", _mgrRestored );
        _importReader = new ComponentMFileImportReader( "SR1", "./tmp" );
        _importReader.init( ctxRestored, CreationPhase.Config );
    }

    @Test public void snapshotExportOneEntry() throws Exception {

        RecoverySampleClasses.SampleBTExportComponent c1 = new RecoverySampleClasses.SampleBTExportComponent( "sampleComponent", "aStrRef" );

        ReusableString b = new ReusableString( 512 );

        String strExportVal = "aaabbbcccddd";

        for ( int i = 0; i < 15; i++ ) {
            String istr = String.format( "%02d", i );

            b.append( "I" + istr + "abcdefghijklmnopqrstuvwxyz0123456789\n" );
        }

        RecoverySampleClasses.SampleContainer[] exportContainer = new RecoverySampleClasses.SampleContainer[ 3 ];

        exportContainer[ 0 ] = makeContainerEntry( 1.2, 1.5, 2.1 );
        exportContainer[ 1 ] = makeContainerEntry( 1.5, 2.1, 2.5 );
        exportContainer[ 2 ] = makeContainerEntry( 3.1, 3.6, 4.1 );

        c1.setStrValA( b.toString() );
        c1.setStrForBTExport( strExportVal );
        c1.setSampleContainerArr( exportContainer );

        _mgrWriter.add( c1 );
        _mgrRestored.add( c1 );

        LinkedHashSet<SMTSnapshotMember> components = new LinkedHashSet<>();
        Collections.addAll( components, c1 );
        SnapshotDefinition sd = new SnapshotDefinitionImpl( TEST_SNAP_ID, components );

        _exportWriter.exportSnapshot( sd );

        c1.setSampleContainerArr( null );
        c1.setStrForBTExport( null );

        ArrayList<Object> restoredColl = new ArrayList<>();
        _importReader.importLastSnapshot( sd, restoredColl, 0 );

        assertEquals( 1, restoredColl.size() );
        assertSame( c1, restoredColl.get( 0 ) );

        assertEquals( "aStrRef", c1.getRef() );
        assertEquals( exportContainer[ 0 ], c1.getSampleContainerArr()[ 0 ] );
        assertEquals( exportContainer[ 1 ], c1.getSampleContainerArr()[ 1 ] );
        assertEquals( exportContainer[ 2 ], c1.getSampleContainerArr()[ 2 ] );
    }

    @Test public void snapshotExportTwoComponents() throws Exception {

        RecoverySampleClasses.SampleBTExportComponent c1 = new RecoverySampleClasses.SampleBTExportComponent( "sampleComponentA", "aStrRefA" );
        RecoverySampleClasses.SampleBTExportComponent c2 = new RecoverySampleClasses.SampleBTExportComponent( "sampleComponentB", "aStrRefB" );

        ReusableString b1 = new ReusableString( 512 );
        ReusableString b2 = new ReusableString( "zzaayytt" );

        String strExportVal = "aaabbbcccddd";

        for ( int i = 0; i < 15; i++ ) {
            String istr = String.format( "%02d", i );

            b1.append( "I" + istr + "abcdefghijklmnopqrstuvwxyz0123456789\n" );
        }

        RecoverySampleClasses.SampleContainer[] exportContainer = new RecoverySampleClasses.SampleContainer[ 3 ];

        exportContainer[ 0 ] = makeContainerEntry( 1.2, 1.5, 2.1 );
        exportContainer[ 1 ] = makeContainerEntry( 1.5, 2.1, 2.5 );
        exportContainer[ 2 ] = makeContainerEntry( 3.1, 3.6, 4.1 );

        c1.setStrValA( b1.toString() );
        c1.setStrForBTExport( strExportVal );
        c1.setSampleContainerArr( exportContainer );

        c2.setStrValA( b2.toString() );
        c2.setStrForBTExport( strExportVal );
        c2.setSampleContainerArr( exportContainer );

        _mgrWriter.add( c1 );
        _mgrWriter.add( c2 );
        _mgrRestored.add( c1 );
        _mgrRestored.add( c2 );

        LinkedHashSet<SMTSnapshotMember> components = new LinkedHashSet<>();
        Collections.addAll( components, c1, c2 );
        SnapshotDefinition sd = new SnapshotDefinitionImpl( TEST_SNAP_ID, components );

        _exportWriter.exportSnapshot( sd );

        c1.setSampleContainerArr( null );
        c1.setStrForBTExport( null );

        c2.setSampleContainerArr( null );
        c2.setStrForBTExport( null );

        ArrayList<Object> restoredColl = new ArrayList<>();
        _importReader.importLastSnapshot( sd, restoredColl, 0 );

        assertEquals( 2, restoredColl.size() );
        assertSame( c1, restoredColl.get( 0 ) );
        assertSame( c2, restoredColl.get( 1 ) );

        assertEquals( "aStrRefA", c1.getRef() );
        assertEquals( "aStrRefB", c2.getRef() );
        assertEquals( exportContainer[ 0 ], c1.getSampleContainerArr()[ 0 ] );
        assertEquals( exportContainer[ 1 ], c1.getSampleContainerArr()[ 1 ] );
        assertEquals( exportContainer[ 2 ], c1.getSampleContainerArr()[ 2 ] );

        assertNotSame( c1.getSampleContainerArr(), exportContainer );
        assertSame( c1.getSampleContainerArr(), c2.getSampleContainerArr() );
        assertEquals( c1.getStrForBTExport(), c2.getStrForBTExport() );
    }

    @After public void teardown() throws FileException {
        AppProps.instance().override( CoreProps.APP_NAME, "TestSnapshotSimple" );

        FileUtils.rmRecurse( "./tmp/TestSnapshotSimple", true );
    }

    private RecoverySampleClasses.SampleContainer makeContainerEntry( final double v, final double v1, final double v2 ) {

        Map<String, Object> map   = new HashMap<>( 3 );
        double[]            dvals = { v, v1, v2 };

        map.put( "K1", v );
        map.put( "K2", v1 );
        map.put( "K3", v2 );

        RecoverySampleClasses.SampleContainer c = new RecoverySampleClasses.SampleContainer( map, dvals );

        return c;
    }
}
