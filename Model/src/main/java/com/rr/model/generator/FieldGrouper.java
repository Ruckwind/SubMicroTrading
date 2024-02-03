package com.rr.model.generator;

import java.util.*;

public class FieldGrouper {

    public static class Band {

        private static final int SPARSE_SIZE = 6;

        private final SortedSet<Integer> _vals = new TreeSet<>();
        private       int                _low;
        private       int                _high;
        private       long               _tot;

        public Band() {
            _low  = Integer.MAX_VALUE;
            _high = 0;
        }

        public Band( final int low, final int high ) {
            _low  = low;
            _high = high;
        }

        public int getHigh()                    { return _high; }

        public int getLow()                     { return _low; }

        public int getMean() {
            return (int) (_tot / _vals.size());
        }

        public int getMedian() {

            Integer[] vals = new Integer[ _vals.size() ];
            _vals.toArray( vals );

            return vals[ _vals.size() / 2 ];
        }

        public Set<Integer> getVals()           { return _vals; }

        public boolean has( final Integer val ) { return _vals.contains( val ); }

        public boolean inBand( int val ) {
            if ( val < _low || val > _high ) return false;

            return true;
        }

        public boolean isSparse( int maxBand ) { return range() > maxBand; }

        public boolean isWellGrouped( int maxBand ) {
            return range() < maxBand || _vals.size() < SPARSE_SIZE;
        }

        public int range() {
            return (_high - _low) + 1;
        }

        public int size()                       { return _vals.size(); }

        public List<Band> split( int maxBand ) {
            Integer[] vals = new Integer[ _vals.size() ];
            _vals.toArray( vals );

            int medianIdx = _vals.size() / 2;
            int median    = vals[ medianIdx ];

            int inBandAroundMedian = inBandAtIdx( vals, medianIdx, maxBand );

            int meanIdx = getMeanIdx( vals, _tot / _vals.size() );

            int inBandAroundMean = inBandAtIdx( vals, meanIdx, maxBand );

            int inBandAroundMax = inBandAtIdx( vals, _vals.size() - 1, maxBand );

            int inBandAroundMin = inBandAtIdx( vals, 0, maxBand );

            if ( inBandAroundMean >= inBandAroundMedian && inBandAroundMean >= inBandAroundMin && inBandAroundMean >= inBandAroundMax ) {
                return splitBandAt( vals, meanIdx, maxBand );
            }

            if ( inBandAroundMedian >= inBandAroundMean && inBandAroundMedian >= inBandAroundMin && inBandAroundMedian >= inBandAroundMax ) {
                return splitBandAt( vals, medianIdx, maxBand );
            }

            if ( inBandAroundMin >= inBandAroundMean && inBandAroundMin >= inBandAroundMedian && inBandAroundMin >= inBandAroundMax ) {
                return splitBandAt( vals, 0, maxBand );
            }

            return splitBandAt( vals, _vals.size() - 1, maxBand );
        }

        private void add( int val ) {
            if ( val < _low ) _low = val;
            if ( val > _high ) _high = val;

            _tot += val;

            _vals.add( val );
        }

        private void addBand( List<Band> bands, Integer[] vals, int startIdx, int maxIdx ) {

            Band b = new Band();

            for ( int i = startIdx; i <= maxIdx && i < vals.length; i++ ) {
                b.add( vals[ i ] );
            }

            if ( b.size() > 0 ) bands.add( b );
        }

        private int getMeanIdx( Integer[] vals, long mean ) {
            for ( int i = 0; i < vals.length; i++ ) {
                if ( vals[ i ] >= mean ) {
                    return i;
                }
            }

            return vals.length - 1;
        }

        private int inBandAtIdx( final Integer[] vals, final int startIdx, int maxBand ) {
            int startVal = vals[ startIdx ];
            int halfBand = maxBand / 2;

            int cnt = 1;

            for ( int i = startIdx + 1; i < vals.length; i++ ) {
                int v = vals[ i ];
                if ( (v - startVal) < halfBand ) {
                    ++cnt;
                } else {
                    break;
                }
            }
            for ( int i = startIdx - 1; i >= 0; i-- ) {
                int v = vals[ i ];
                if ( (startVal - v) < halfBand ) {
                    ++cnt;
                } else {
                    break;
                }
            }

            return cnt;
        }

        private List<Band> splitBandAt( final Integer[] vals, int startIdx, int maxBand ) {

            List<Band> bands = new ArrayList<>();

            int startVal = vals[ startIdx ];
            int halfBand = maxBand / 2;

            int startBandIdx = startIdx;
            int endBandIdx   = startIdx;

            for ( int i = startIdx + 1; i < vals.length; i++ ) {
                int v = vals[ i ];
                if ( (v - startVal) < halfBand ) {
                    ++endBandIdx;
                } else {
                    break;
                }
            }

            for ( int i = startIdx - 1; i >= 0; i-- ) {
                int v = vals[ i ];
                if ( (startVal - v) < halfBand ) {
                    --startBandIdx;
                } else {
                    break;
                }
            }

            addBand( bands, vals, startBandIdx, endBandIdx );

            if ( startBandIdx > 0 ) {
                addBand( bands, vals, 0, startBandIdx - 1 );
            }

            if ( endBandIdx < (_vals.size() - 1) ) {
                addBand( bands, vals, endBandIdx + 1, _vals.size() );
            }

            return bands;
        }
    }

    private Set<Integer> _vals   = new HashSet<>();
    private List<Band>   _bands;
    private int          _totCnt;
    private int          _minTag = Integer.MAX_VALUE;
    private int          _maxTag = 0;

    public FieldGrouper() {
        _bands = new ArrayList<>();
    }

    public void addValue( int tag ) {
        _vals.add( tag );
        if ( tag < _minTag ) _minTag = tag;
        if ( tag > _maxTag ) _maxTag = tag;
    }

    public List<Band> getBands( int maxBandSize, final int minSubSwitchPackSize ) {
        _bands.clear();

        Band b = new Band();
        for ( int v : _vals ) {
            b.add( v );
        }

        _bands.add( b );

        List<Band> bands = processBands( _bands, maxBandSize );

        bands = mergeSparseNonPackedBands( bands, maxBandSize, minSubSwitchPackSize );

        _bands = bands;

        return _bands;
    }

    private Band getBand( int tag ) {
        for ( Band b : _bands ) {
            if ( b.inBand( tag ) ) {
                return b;
            }
        }
        throw new RuntimeException( "Missing band for tag " + tag );
    }

    private List<Band> mergeSparseNonPackedBands( final List<Band> bands, final int maxBandSize, final int minSubSwitchPackSize ) {
        List<Band> newList = new ArrayList<>();

        Band sparseNonPackedBand = null;

        for ( Band b : bands ) {
            if ( b.isSparse( maxBandSize ) || b.size() < minSubSwitchPackSize ) {

                if ( sparseNonPackedBand == null ) {
                    sparseNonPackedBand = b;
                    newList.add( b );
                } else {                                    // merge band
                    for ( int v : b.getVals() ) {
                        sparseNonPackedBand.add( v );
                    }
                }

            } else {
                newList.add( b );
            }
        }

        return newList;
    }

    private List<Band> processBands( List<Band> bands, int maxBandSize ) {
        boolean ok = true;

        for ( Band b : bands ) {
            if ( !b.isWellGrouped( maxBandSize ) ) {
                ok = false;
            }
        }

        if ( ok ) return bands;

        List<Band> newList = new ArrayList<>();

        for ( Band b : bands ) {
            if ( b.isWellGrouped( maxBandSize ) ) {
                newList.add( b );
            } else {
                List<Band> tmpList = b.split( maxBandSize );
                newList.addAll( processBands( tmpList, maxBandSize ) );
            }
        }

        return newList;
    }
}
