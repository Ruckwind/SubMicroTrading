package com.rr.core.model;

import com.rr.core.collections.TimeSeries;

/**
 * historically represents a parent company
 * <p>
 * acts as a proxy to the current latest version of the ParentCompany
 */
public interface HistoricalParentCompany extends ParentCompany, TimeSeries<ParentCompany> {

}

