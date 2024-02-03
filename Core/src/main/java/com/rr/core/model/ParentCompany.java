package com.rr.core.model;

import com.rr.core.lang.ZString;

import java.util.List;

public interface ParentCompany extends PointInTime {

    void attach( CommonInstrument ci );

    CommonInstrument getCommonInstrument( long commmonInstrumentId );

    void getCommonInstruments( List<CommonInstrument> dest );

    ZString getCompanyName();

    int getNumCommonInsts();

    long getParentCompanyId();
}
