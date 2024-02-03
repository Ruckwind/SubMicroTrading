package com.rr.core.model;

import com.rr.core.collections.LongHashMap;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ZString;
import com.rr.core.lang.stats.SizeConstants;

import java.util.List;

public class ParentCompanyImpl implements ParentCompany {

    private final long                          _startTime;
    private final long                          _parentCompanyId;
    private final LongHashMap<CommonInstrument> _commonInstruments = new LongHashMap<>( 4, 0.75f );
    private final ReusableString                _companyName       = new ReusableString( SizeConstants.DEFAULT_COMPANYNAME_LENGTH );

    public ParentCompanyImpl( ZString companyName, final long parentCompanyId, final long startTime ) {
        _parentCompanyId = parentCompanyId;
        _startTime       = startTime;
        _companyName.copy( companyName );
    }

    @Override public ReusableString getCompanyName()                                        { return _companyName; }

    @Override public long getParentCompanyId()                                              { return _parentCompanyId; }

    @Override public CommonInstrument getCommonInstrument( final long commmonInstrumentId ) { return _commonInstruments.get( commmonInstrumentId ); }

    @Override public void getCommonInstruments( final List<CommonInstrument> dest )         { _commonInstruments.forEach( val -> dest.add( val ) ); }

    @Override public void attach( final CommonInstrument ci )                               { _commonInstruments.put( ci.getCommonInstrumentId(), ci ); }

    @Override public int getNumCommonInsts()                                                { return _commonInstruments.size(); }

    @Override public long getEventTimestamp()                                               { return _startTime; }

    ;

}
