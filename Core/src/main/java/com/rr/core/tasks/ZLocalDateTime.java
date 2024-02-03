package com.rr.core.tasks;

import java.util.TimeZone;

public class ZLocalDateTime {

    private TimeZone _tz;
    private String   _timeStampFormat;
    private String   _localTimeStamp;

    public ZLocalDateTime() {
    }

    public ZLocalDateTime( TimeZone tz, String timeStampFormat, String localTimeStamp ) {
        _tz              = tz;
        _timeStampFormat = timeStampFormat;
        _localTimeStamp  = localTimeStamp;
    }

    public String getLocalTimeStamp()  { return _localTimeStamp; }

    public String getTimeStampFormat() { return _timeStampFormat; }

    public TimeZone getTz()            { return _tz; }

    public void set( TimeZone tz, String timeStampFormat, String localTimeStamp ) {
        _tz              = tz;
        _timeStampFormat = timeStampFormat;
        _localTimeStamp  = localTimeStamp;
    }
}
