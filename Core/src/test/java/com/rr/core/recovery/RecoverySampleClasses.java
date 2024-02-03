package com.rr.core.recovery;

import com.rr.core.annotations.Persist;
import com.rr.core.component.SMTSnapshotMemberAllFields;
import com.rr.core.component.SMTSnapshotMemberOnlyPersistFields;
import com.rr.core.component.SMTStartContext;
import com.rr.core.lang.Constants;
import com.rr.core.lang.ZFunction;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class RecoverySampleClasses {

    public static interface SampleInterfaceA {

        void doSomthing();
    }

    public static class ClassWithJustRef {

        private Object _ref;

        public ClassWithJustRef()                   { /* nothing */ }

        public ClassWithJustRef( final Object ref ) { _ref = ref; }

        public Object getRef()                      { return _ref; }

        public void setRef( final Object ref )      { _ref = ref; }
    }

    public static class ClassWithJustInt {

        private int _intVal = 123;

        public static ClassWithJustInt[] makeSample() {
            ClassWithJustInt[] two = new ClassWithJustInt[ 2 ];
            two[ 0 ] = new ClassWithJustInt( 345 );
            two[ 1 ] = new ClassWithJustInt( 678 );
            return two;
        }

        public ClassWithJustInt() { /* nothing */ }

        public ClassWithJustInt( final int i ) {
            _intVal = i;
        }

        @Override public int hashCode() {
            return _intVal;
        }

        @Override public boolean equals( final Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass() != o.getClass() ) return false;

            final ClassWithJustInt that = (ClassWithJustInt) o;

            return _intVal == that._intVal;
        }
    }

    public static class ClassWithJustDouble {

        private double _dblVal = 123;

        public static ClassWithJustDouble[] makeSample() {
            ClassWithJustDouble[] two = new ClassWithJustDouble[ 2 ];
            two[ 0 ] = new ClassWithJustDouble( 345 );
            two[ 1 ] = new ClassWithJustDouble( 678 );
            return two;
        }

        public ClassWithJustDouble()                 { /* nothing */ }

        public ClassWithJustDouble( final double i ) { _dblVal = i; }

        @Override public int hashCode() {
            return Double.hashCode( _dblVal );
        }

        @Override public boolean equals( final Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass() != o.getClass() ) return false;

            final ClassWithJustDouble that = (ClassWithJustDouble) o;

            return _dblVal == that._dblVal;
        }

        public double getDblVal()                    { return _dblVal; }
    }

    public static final class ClassWithMapAndInt extends ClassWithJustInt {

        private Map<String, String> _map;

        public ClassWithMapAndInt( final int val, final Map<String, String> map ) {
            super( val );
            _map = map;
        }

        @Override public boolean equals( final Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass() != o.getClass() ) return false;
            if ( !super.equals( o ) ) return false;

            final ClassWithMapAndInt that = (ClassWithMapAndInt) o;

            return _map == that._map;
        }

        @Override public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (_map != null ? _map.hashCode() : 0);
            return result;
        }
    }

    public static class ClassWithJustIntAndString {

        private int    _intValA = 123;
        private String _strValA = "abc";

        public ClassWithJustIntAndString() { /* nothing */ }

        public ClassWithJustIntAndString( final int iVal, final String strVal ) {
            _intValA = iVal;
            _strValA = strVal;
        }

        public ClassWithJustIntAndString( final String strVal ) {
            _strValA = strVal;
        }

        @Override public int hashCode() {
            int result = _intValA;
            result = 31 * result + (_strValA != null ? _strValA.hashCode() : 0);
            return result;
        }

        @Override public boolean equals( final Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass() != o.getClass() ) return false;

            final ClassWithJustIntAndString that = (ClassWithJustIntAndString) o;

            if ( _intValA != that._intValA ) return false;
            return _strValA != null ? _strValA.equals( that._strValA ) : that._strValA == null;
        }

        public String getStrValA() {
            return _strValA;
        }

        public void setStrValA( final String strValA ) {
            _strValA = strValA;
        }
    }

    public static class ClassWithClassAndField {

        private int      _intValA = 123;
        private Class<?> _someClass;
        private Field    _someField;

        public ClassWithClassAndField() {
            // for json
        }

        public ClassWithClassAndField( final int intValA, final Class<?> someClass, final Field someField ) {
            _intValA   = intValA;
            _someClass = someClass;
            _someField = someField;
        }

        public int getIntValA()        { return _intValA; }

        public Class<?> getSomeClass() { return _someClass; }

        public Field getSomeField()    { return _someField; }
    }

    public static class ClassWithJustStringAndLambda {

        private String                    _strValA = "abc";
        private ZFunction<String, String> _proc;

        public ClassWithJustStringAndLambda() { /* nothing */ }

        public ClassWithJustStringAndLambda( final String strVal, ZFunction<String, String> p ) {
            _strValA = strVal;
            _proc    = p;
        }

        @Override public int hashCode() {
            int result = _strValA != null ? _strValA.hashCode() : 0;
            result = 31 * result + (_proc != null ? _proc.hashCode() : 0);
            return result;
        }

        @Override public boolean equals( final Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass() != o.getClass() ) return false;

            final ClassWithJustStringAndLambda that = (ClassWithJustStringAndLambda) o;

            if ( _strValA != null ? !_strValA.equals( that._strValA ) : that._strValA != null ) return false;
            return _proc != null ? _proc.equals( that._proc ) : that._proc == null;
        }

        public String doStuff( String in ) {
            return _proc.apply( in ) + _strValA;
        }

        public Object getProc() { return _proc; }

        public String getStrValA() {
            return _strValA;
        }

        public void setStrValA( final String strValA ) {
            _strValA = strValA;
        }
    }

    public static final class SMTComponentWithJustIntAndString extends ClassWithJustIntAndString implements SMTSnapshotMemberAllFields {

        public SMTComponentWithJustIntAndString( final String id ) {
            super( id );
        }

        @Override public String getComponentId() {
            return getStrValA();
        }
    }

    public static class SampleContainer {

        private Map<String, Object> _aMap;
        private double[]            _dvals;

        public SampleContainer() { /* reflection */ }

        public SampleContainer( final Map<String, Object> aMap, final double[] dvals ) {
            _aMap  = aMap;
            _dvals = dvals;
        }

        @Override public int hashCode() {
            int result = _aMap != null ? _aMap.hashCode() : 0;
            result = 31 * result + Arrays.hashCode( _dvals );
            return result;
        }

        @Override public boolean equals( final Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass() != o.getClass() ) return false;

            final SampleContainer that = (SampleContainer) o;

            if ( !Objects.equals( _aMap, that._aMap ) ) return false;

            return Arrays.equals( _dvals, that._dvals );
        }
    }

    public static final class SampleBTExportComponent extends ClassWithJustIntAndString implements SMTSnapshotMemberAllFields, ComponentExportClient {

        private String          _id;
        private Object          _ref;
        private double          _dblVal = Constants.UNSET_DOUBLE;
        private String          _strForBTExport;
        private SampleContainer _sampleContainerArr[];

        public SampleBTExportComponent() { /* reflection */ }

        public SampleBTExportComponent( final String id, final Object someRef ) {
            super( id );
            _id  = id;
            _ref = someRef;
        }

        @Override public boolean equals( final Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass() != o.getClass() ) return false;
            if ( !super.equals( o ) ) return false;

            final SampleBTExportComponent that = (SampleBTExportComponent) o;

            if ( _ref != null ? !_ref.equals( that._ref ) : that._ref != null ) return false;
            if ( _strForBTExport != null ? !_strForBTExport.equals( that._strForBTExport ) : that._strForBTExport != null ) return false;
            return Arrays.equals( _sampleContainerArr, that._sampleContainerArr );
        }

        @Override public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (_ref != null ? _ref.hashCode() : 0);
            result = 31 * result + (_strForBTExport != null ? _strForBTExport.hashCode() : 0);
            result = 31 * result + Arrays.hashCode( _sampleContainerArr );
            return result;
        }

        @Override public void exportData( final ExportContainer exportContainer ) {
            exportContainer.put( "dblVal", _dblVal );
            exportContainer.put( "myStr", _strForBTExport );
            exportContainer.put( "myContainerArr", _sampleContainerArr );
        }

        @Override public void importData( final ExportContainer importContainer, final long btSnapshotTime ) {
            _dblVal         = importContainer.getDouble( "dblVal" );
            _strForBTExport = importContainer.get( "myStr" );
            Object sampleContainerArr = importContainer.get( "myContainerArr" );
            _sampleContainerArr = (SampleContainer[]) sampleContainerArr;
        }

        @Override public String getComponentId()                                        { return _id; }

        public Object getRef()                                                          { return _ref; }

        public void setRef( final Object ref )                                          { _ref = ref; }

        public SampleContainer[] getSampleContainerArr()                                { return _sampleContainerArr; }

        public void setSampleContainerArr( final SampleContainer[] sampleContainerArr ) { _sampleContainerArr = sampleContainerArr; }

        public String getStrForBTExport()                                               { return _strForBTExport; }

        public void setStrForBTExport( final String strForBTExport )                    { _strForBTExport = strForBTExport; }
    }

    public static class SnapMemWith2PersistableFields implements SMTSnapshotMemberOnlyPersistFields {

        private          String _id;
        private          int    _intValA = 123;
        private @Persist double _dblVal  = Constants.UNSET_DOUBLE;
        private @Persist String _strValA = "abc";
        private          String _strValB = "def";

        public SnapMemWith2PersistableFields( final int iVal, final String strVal ) {
            _intValA = iVal;
            _strValA = strVal;
        }

        public SnapMemWith2PersistableFields( final String id ) { _id = id; }

        @Override public String getComponentId()                                                    { return _id; }

        @Override public int hashCode() {
            int result = _intValA;
            result = 31 * result + (_strValA != null ? _strValA.hashCode() : 0);
            result = 31 * result + (_strValB != null ? _strValB.hashCode() : 0);
            return result;
        }

        @Override public boolean equals( final Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass() != o.getClass() ) return false;

            final SnapMemWith2PersistableFields that = (SnapMemWith2PersistableFields) o;

            if ( _intValA != that._intValA ) return false;
            if ( _strValA != null ? !_strValA.equals( that._strValA ) : that._strValA != null ) return false;
            return _strValB != null ? _strValB.equals( that._strValB ) : that._strValB == null;
        }

        @Override public void postRestore( final long snapshotTime, final SMTStartContext context ) { _strValB = "ghi"; }

        public String getStrValA()                              { return _strValA; }

        public void setStrValA( final String strValA )          { _strValA = strValA; }
    }

    public static final class ClassWithStringArray {

        private int      _intValB = 123;
        private String[] _strValB = { "abc", "def" };

        @Override public int hashCode() {
            int result = _intValB;
            result = 31 * result + Arrays.hashCode( _strValB );
            return result;
        }

        @Override public boolean equals( final Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass() != o.getClass() ) return false;

            final ClassWithStringArray that = (ClassWithStringArray) o;

            if ( _intValB != that._intValB ) return false;
            // Probably incorrect - comparing Object[] arrays with Arrays.equals
            return Arrays.equals( _strValB, that._strValB );
        }
    }

    public static final class ClassWithIntArray {

        private int[]  _intValC = { 123, 345 };
        private String _strValC = "def";

        @Override public int hashCode() {
            int result = Arrays.hashCode( _intValC );
            result = 31 * result + (_strValC != null ? _strValC.hashCode() : 0);
            return result;
        }

        @Override public boolean equals( final Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass() != o.getClass() ) return false;

            final ClassWithIntArray that = (ClassWithIntArray) o;

            if ( !Arrays.equals( _intValC, that._intValC ) ) return false;
            return _strValC != null ? _strValC.equals( that._strValC ) : that._strValC == null;
        }
    }

    public static final class ClassWithRef {

        private int                       _intValD    = 123;
        private ClassWithJustIntAndString _intAndStrD = new ClassWithJustIntAndString( 456, "def" );
        private String                    _strValD    = "abc";

        @Override public int hashCode() {
            int result = _intValD;
            result = 31 * result + (_intAndStrD != null ? _intAndStrD.hashCode() : 0);
            result = 31 * result + (_strValD != null ? _strValD.hashCode() : 0);
            return result;
        }

        @Override public boolean equals( final Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass() != o.getClass() ) return false;

            final ClassWithRef that = (ClassWithRef) o;

            if ( _intValD != that._intValD ) return false;
            if ( _intAndStrD != null ? !_intAndStrD.equals( that._intAndStrD ) : that._intAndStrD != null ) return false;
            return _strValD != null ? _strValD.equals( that._strValD ) : that._strValD == null;
        }
    }

    public static final class ClassWithCompArray {

        private int                _intValE    = 123;
        private ClassWithJustInt[] _intAndStrE = ClassWithJustInt.makeSample();
        private String             _strValE    = "abc";

        @Override public int hashCode() {
            int result = _intValE;
            result = 31 * result + Arrays.hashCode( _intAndStrE );
            result = 31 * result + (_strValE != null ? _strValE.hashCode() : 0);
            return result;
        }

        @Override public boolean equals( final Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass() != o.getClass() ) return false;

            final ClassWithCompArray that = (ClassWithCompArray) o;

            if ( _intValE != that._intValE ) return false;
            // Probably incorrect - comparing Object[] arrays with Arrays.equals
            if ( !Arrays.equals( _intAndStrE, that._intAndStrE ) ) return false;
            return _strValE != null ? _strValE.equals( that._strValE ) : that._strValE == null;
        }
    }

    public static final class ClassWithByteArray {

        private byte[] _byteArrValF  = { (byte) 185, 21, -1 };
        private String _strValF      = "def";
        private Byte[] _aByteArrValF = { 123, (byte) -1, 23 };

        @Override public int hashCode() {
            int result = Arrays.hashCode( _byteArrValF );
            result = 31 * result + (_strValF != null ? _strValF.hashCode() : 0);
            result = 31 * result + Arrays.hashCode( _aByteArrValF );
            return result;
        }

        @Override public boolean equals( final Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass() != o.getClass() ) return false;

            final ClassWithByteArray that = (ClassWithByteArray) o;

            if ( !Arrays.equals( _byteArrValF, that._byteArrValF ) ) return false;
            if ( _strValF != null ? !_strValF.equals( that._strValF ) : that._strValF != null ) return false;
            // Probably incorrect - comparing Object[] arrays with Arrays.equals
            return Arrays.equals( _aByteArrValF, that._aByteArrValF );
        }
    }

    public static final class ClassWithDoubleArray {

        private byte[]   _byteArrValF = { (byte) 185, 21, -1 };
        private String   _strValF     = "def";
        private double[] _aDblArrValF = { 123.456, -1.234, 23.987 };

        @Override public int hashCode() {
            int result = Arrays.hashCode( _byteArrValF );
            result = 31 * result + (_strValF != null ? _strValF.hashCode() : 0);
            result = 31 * result + Arrays.hashCode( _aDblArrValF );
            return result;
        }

        @Override public boolean equals( final Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass() != o.getClass() ) return false;

            final ClassWithDoubleArray that = (ClassWithDoubleArray) o;

            if ( !Arrays.equals( _byteArrValF, that._byteArrValF ) ) return false;
            if ( _strValF != null ? !_strValF.equals( that._strValF ) : that._strValF != null ) return false;
            return Arrays.equals( _aDblArrValF, that._aDblArrValF );
        }

        public byte[] getByteArrValF()                         { return _byteArrValF; }

        public void setByteArrValF( final byte[] byteArrValF ) { _byteArrValF = byteArrValF; }

        public double[] getDblArrVal() { return _aDblArrValF; }
    }

    public static class SampleInterfaceAImpl implements SampleInterfaceA {

        private double  _covariance;
        private long    _timeSpan;
        private boolean _paused;

        public SampleInterfaceAImpl() { /* for reflection */ }

        public SampleInterfaceAImpl( final double covariance, final long timeSpan, final boolean paused ) {
            _covariance = covariance;
            _timeSpan   = timeSpan;
            _paused     = paused;
        }

        @Override public void doSomthing() {
            /* nothing */
        }

        @Override public int hashCode() {
            int  result;
            long temp;
            temp   = Double.doubleToLongBits( _covariance );
            result = (int) (temp ^ (temp >>> 32));
            result = 31 * result + (int) (_timeSpan ^ (_timeSpan >>> 32));
            result = 31 * result + (_paused ? 1 : 0);
            return result;
        }

        @Override public boolean equals( final Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass() != o.getClass() ) return false;

            final SampleInterfaceAImpl that = (SampleInterfaceAImpl) o;

            if ( Double.compare( that._covariance, _covariance ) != 0 ) return false;
            if ( _timeSpan != that._timeSpan ) return false;
            return _paused == that._paused;
        }
    }

    public static class SampleWith2DArray {

        private int                  _size;
        private SampleInterfaceA[][] _arr2D;
        private int[][]              _arr2DInt;

        public SampleWith2DArray() { /* for reflection */ }

        public SampleWith2DArray( int size ) {
            _size = size;
        }

        @Override public int hashCode() {
            int result = _size;
            result = 31 * result + Arrays.deepHashCode( _arr2D );
            result = 31 * result + Arrays.deepHashCode( _arr2DInt );
            return result;
        }

        @Override public boolean equals( final Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass() != o.getClass() ) return false;

            final SampleWith2DArray that = (SampleWith2DArray) o;

            if ( _size != that._size ) return false;
            if ( !Arrays.deepEquals( _arr2D, that._arr2D ) ) return false;
            return Arrays.deepEquals( _arr2DInt, that._arr2DInt );
        }

        public void init() {
            _arr2D    = new SampleInterfaceA[ _size ][];
            _arr2DInt = new int[ _size ][];

            for ( int i = 0; i < _size; i++ ) {
                _arr2D[ i ]    = new SampleInterfaceA[ i + 1 ];
                _arr2DInt[ i ] = new int[ i + 1 ];

                for ( int j = 0; j <= i; j++ ) {
                    _arr2D[ i ][ j ]    = new SampleInterfaceAImpl( 0.12345 * (i + 1) * (j + 1), 1000 * (i + 1) * (j + 1) * _size, true );
                    _arr2DInt[ i ][ j ] = 1000 * (i + 1) * (j + 1) * _size;
                }
            }
        }
    }

    public static final class ClassWithEnum {

        private anEnum _enumF = anEnum.BBB;
    }

    public enum anEnum {AAA, BBB, CCC}
}
