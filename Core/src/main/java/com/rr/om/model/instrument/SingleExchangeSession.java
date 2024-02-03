/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.model.instrument;

import com.rr.core.hols.HolidayLoader;
import com.rr.core.lang.*;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.*;
import com.rr.core.model.Auction.Type;
import com.rr.core.utils.Utils;

import java.util.Calendar;
import java.util.LinkedHashMap;

public class SingleExchangeSession implements ExchangeSession {

    private static final Logger _log = LoggerFactory.create( SingleExchangeSession.class );

    private final ZString                              _id;
    private final Auction                              _closeAuction;
    private final Auction                              _openAuction;
    private final Auction                              _intradayAuction;
    private final Calendar                             _openCal;
    private final Calendar                             _endCal;
    private final Calendar                             _startContCal;
    private final Calendar                             _endContCal;
    private final Calendar                             _endDayCal;
    private final Calendar                             _startDayCal;
    private       Calendar                             _halfDayCal;
    private       long                                 _halfDayClose;
    private       long                                 _openTime;
    private       long                                 _closeTime;
    private       boolean                              _isHalfDay;
    private       boolean                              _todayClosed;
    private       long                                 _startContinuous;
    private       long                                 _endContinuous;
    private       int                                  _openHHMMSS;
    private       int                                  _openAuctionHHMMSS;
    private       int                                  _startContinuousHHMMSS;
    private       int                                  _closeAuctionHHMMSS;
    private       int                                  _closeHHMMSS;
    private       int                                  _closeAuctionOffsetSecs = Constants.UNSET_INT;
    private       LinkedHashMap<Integer, HolidayEntry> _holidays;
    private       long                                 _startOfDayMS;
    private       long                                 _endOfDayMS;
    private       boolean                              _overnight;

    public SingleExchangeSession( ZString id, Calendar openCal, Calendar halfDayCal, Calendar endCal, Auction openA, Auction closeA, ExchangeCode exchangeCode ) {
        this( id, openCal, null, null, halfDayCal, endCal, openA, null, closeA, exchangeCode );
    }

    public SingleExchangeSession( ZString id,
                                  Calendar openCal,
                                  Calendar startContinuous,
                                  Calendar endContinuous,
                                  Calendar halfDayCal,
                                  Calendar endCal,
                                  Auction openAuction,
                                  Auction intraDayAuction,
                                  Auction closeAuction,
                                  ExchangeCode exchangeCode ) {

        _id              = id;
        _openCal         = openCal;
        _halfDayCal      = halfDayCal;
        _endCal          = endCal;
        _openAuction     = (openAuction != null) ? openAuction : new Auction( null, null, Type.Null );
        _intradayAuction = (intraDayAuction != null) ? intraDayAuction : new Auction( null, null, Type.Null );
        _closeAuction    = (closeAuction != null) ? closeAuction : new Auction( null, null, Type.Null );

        _endDayCal = (Calendar) _endCal.clone();
        _endDayCal.set( Calendar.HOUR_OF_DAY, 23 );
        _endDayCal.set( Calendar.MINUTE, 59 );
        _endDayCal.set( Calendar.SECOND, 59 );
        _endDayCal.set( Calendar.MILLISECOND, 999 );

        _startDayCal = (Calendar) _endCal.clone();
        _startDayCal.set( Calendar.HOUR_OF_DAY, 00 );
        _startDayCal.set( Calendar.MINUTE, 00 );
        _startDayCal.set( Calendar.SECOND, 00 );
        _startDayCal.set( Calendar.MILLISECOND, 1 );

        if ( startContinuous == null ) {
            if ( openAuction == null ) {
                startContinuous = (Calendar) openCal.clone();
            } else {
                startContinuous = (Calendar) openAuction.getEndTimeCalendar().clone();
                startContinuous.add( Calendar.MILLISECOND, 1 );
            }
        }

        if ( endContinuous == null ) {
            if ( closeAuction == null ) {
                endContinuous = (Calendar) endCal.clone();
            } else {
                endContinuous = (Calendar) closeAuction.getStartTimeCalendar().clone();
                endContinuous.add( Calendar.MILLISECOND, -1 );
            }
        }

        _startContCal = startContinuous;
        _endContCal   = endContinuous;

        _holidays = HolidayLoader.instance().getHolidays( exchangeCode );

        _openHHMMSS            = getTimeHHMMSS( _openCal );
        _openAuctionHHMMSS     = (openAuction != null) ? getTimeHHMMSS( _openAuction.getStartTimeCalendar() ) : Constants.UNSET_INT;
        _startContinuousHHMMSS = getTimeHHMMSS( startContinuous );
        _closeAuctionHHMMSS    = (closeAuction != null) ? getTimeHHMMSS( _closeAuction.getStartTimeCalendar() ) : Constants.UNSET_INT;
        _closeHHMMSS           = getTimeHHMMSS( endCal );

        if ( closeAuction != null ) {
            long endTimeUTC       = endCal.getTimeInMillis();
            long startCloseAucUTC = _closeAuction.getStartTimeCalendar().getTimeInMillis();
            _closeAuctionOffsetSecs = (int) ((endTimeUTC - startCloseAucUTC) / 1000);
        }

        setToday();
    }

    @Override public ZString getId() {
        return _id;
    }

    @Override public ExchangeSession getExchangeSession( ZString marketSegment ) {
        return this;
    }

    @Override public ExchangeState getExchangeStateNow() {
        long todayUnixTimeMS = ClockFactory.get().currentTimeMillis();

        if ( todayUnixTimeMS < _startOfDayMS || todayUnixTimeMS > _endOfDayMS ) {
            setToday();
        }

        return getExchangeStateToday( todayUnixTimeMS );
    }

    @Override public ExchangeState getExchangeStateToday( final long todayUnixTimeMS ) {
        if ( _todayClosed )
            return ExchangeState.Closed;

        if ( _intradayAuction.isIn( todayUnixTimeMS ) )
            return ExchangeState.IntradayAuction;

        if ( _overnight ) { // spans midnight .. no auction handling as yet for those exchanges

            if ( todayUnixTimeMS > _endContinuous && todayUnixTimeMS < _startContinuous ) {
                return ExchangeState.Closed;
            } else {
                return ExchangeState.Continuous;
            }

        } else {
            if ( todayUnixTimeMS >= _startContinuous && todayUnixTimeMS <= _endContinuous ) {
                return ExchangeState.Continuous;
            }
        }

        if ( _openAuction.isIn( todayUnixTimeMS ) )
            return ExchangeState.OpeningAuction;
        if ( _closeAuction.isIn( todayUnixTimeMS ) )
            return ExchangeState.ClosingAuction;
        if ( _intradayAuction.isIn( todayUnixTimeMS ) )
            return ExchangeState.IntradayAuction;
        if ( todayUnixTimeMS >= _openTime && todayUnixTimeMS < _openAuction.getStartTime() )
            return ExchangeState.PreOpen;

        return ExchangeState.Closed;
    }

    @Override public boolean isOpen()              { return false; }

    @Override public ExchangeState getExchangeStateAt( Calendar dateTime ) {

        int yyyymmdd = CommonTimeUtils.calendarToYYYYMMDD( dateTime );

        int timeHHMMSS = getTimeHHMMSS( dateTime );

        HolidayEntry h = (_holidays != null) ? _holidays.get( yyyymmdd ) : null;

        boolean halfDay = false;

        if ( h != null ) {
            if ( h.isHalfDay() ) {
                return exchangeStateFor( timeHHMMSS, h.getCloseHHMMSS() );
            }

            return ExchangeState.Closed; // full day holiday
        }

        int     dayOfWeek = dateTime.get( Calendar.DAY_OF_WEEK );
        boolean weekEnd   = (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY);

        if ( weekEnd ) return ExchangeState.Closed;

        return exchangeStateFor( timeHHMMSS, _closeHHMMSS );
    }

    @Override public boolean isOpenAt( Calendar dateTime ) {
        ExchangeState state = getExchangeStateAt( dateTime );

        switch( state ) {
        case PreOpen:
        case OpeningAuction:
        case Continuous:
        case IntradayAuction:
        case ClosingAuction:
            return true;
        case PostClose:
        case Closed:
        case Unknown:
        default:
            return false;
        }
    }

    @Override public boolean isHalfDayAt( Calendar dateTime ) {
        int yyyymmdd = CommonTimeUtils.calendarToYYYYMMDD( dateTime );

        HolidayEntry h = (_holidays != null) ? _holidays.get( yyyymmdd ) : null;

        boolean halfDay = false;

        if ( h != null ) {
            halfDay = h.isHalfDay();
        }

        return halfDay;
    }

    @Override public boolean isOpenToday( long time ) {
        if ( _todayClosed ) return false;

        if ( _overnight ) {

            if ( time > _closeTime && time < _openTime ) {
                return false;
            }

            return true;
        }

        return time >= _openTime && time <= _closeTime;
    }

    @Override public long getOpenTime() {
        return _openTime;
    }

    @Override public long getContinuousStartTime() { return _startContinuous; }

    @Override public long getContinuousEndTime()   { return _endContinuous; }

    @Override public long getCloseTime() {
        return _closeTime;
    }

    @Override public long getHalfDayCloseTime() {
        return _halfDayClose;
    }

    @Override public Auction getOpenAuction() {
        return _openAuction;
    }

    @Override public Auction getIntradayAuction() {
        return _intradayAuction;
    }

    @Override public Auction getCloseAuction() {
        return _closeAuction;
    }

    @Override public void setToday() {

        _openAuction.setToday();
        _closeAuction.setToday();

        Calendar c = TimeUtilsFactory.safeTimeUtils().getCalendar( _openCal.getTimeZone() );

        _openCal.set( c.get( Calendar.YEAR ),
                      c.get( Calendar.MONTH ),
                      c.get( Calendar.DAY_OF_MONTH ) );
        _endCal.set( c.get( Calendar.YEAR ),
                     c.get( Calendar.MONTH ),
                     c.get( Calendar.DAY_OF_MONTH ) );
        _startContCal.set( c.get( Calendar.YEAR ),
                           c.get( Calendar.MONTH ),
                           c.get( Calendar.DAY_OF_MONTH ) );
        _endContCal.set( c.get( Calendar.YEAR ),
                         c.get( Calendar.MONTH ),
                         c.get( Calendar.DAY_OF_MONTH ) );
        _startDayCal.set( c.get( Calendar.YEAR ),
                          c.get( Calendar.MONTH ),
                          c.get( Calendar.DAY_OF_MONTH ) );
        _endDayCal.set( c.get( Calendar.YEAR ),
                        c.get( Calendar.MONTH ),
                        c.get( Calendar.DAY_OF_MONTH ) );

        _startOfDayMS = _startDayCal.getTimeInMillis();
        _endOfDayMS   = _endDayCal.getTimeInMillis();

        if ( _halfDayCal != null ) {
            _halfDayCal.set( c.get( Calendar.YEAR ),
                             c.get( Calendar.MONTH ),
                             c.get( Calendar.DAY_OF_MONTH ) );
        }

        _openTime        = _openCal.getTimeInMillis();
        _startContinuous = _startContCal.getTimeInMillis();
        _endContinuous   = _endContCal.getTimeInMillis();

        int yyyymmdd = CommonTimeUtils.calendarToYYYYMMDD( c );

        HolidayEntry h = (_holidays != null) ? _holidays.get( yyyymmdd ) : null;

        setHalfDay( false );
        setCloseTime();

        if ( h != null ) {
            _log.info( "Today is a holiday for " + h.toString() );

            if ( h.isHalfDay() && !Utils.isNull( h.getCloseTime() ) ) {

                if ( _halfDayCal == null ) _halfDayCal = Calendar.getInstance( _startContCal.getTimeZone() );

                _halfDayCal.setTimeInMillis( CommonTimeUtils.internalTimeToUnixTime( h.getCloseTime() ) );

                setHalfDay( true );
                _todayClosed = false;
            } else {
                _todayClosed = true;
            }

        } else {
            int     dayOfWeek = c.get( Calendar.DAY_OF_WEEK );
            boolean weekEnd   = (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY);

            _todayClosed = weekEnd;
        }

        setCloseTime();

        if ( _log.isEnabledFor( Level.debug ) ) {
            ReusableString rs = TLC.strPop();

            rs.copy( "session setToday " );

            String s = dump( rs ).toString();

            TLC.strPush( rs );

            _log.log( Level.debug, s );
        }
    }

    @Override public void setOpen( long openUTC ) { _openTime = openUTC; }

    @Override public ReusableString dump( ReusableString buf ) {
        buf.append( " id=" ).append( _id )
           .append( ", isHalfDayToday=" ).append( _isHalfDay )
           .append( ", isClosedToday=" ).append( _todayClosed )
           .append( ", overnight=" ).append( _overnight );

        buf.append( "\nstartOfDayMS=" );
        TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( buf, _startOfDayMS );
        buf.append( "\nendOfDayMS=" );
        TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( buf, _endOfDayMS );
        buf.append( "\nopenTimeLocal=" );
        TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( buf, _openTime );
        buf.append( "\ncloseTimeLocal=" );
        TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( buf, _closeTime );
        buf.append( "\nstartContLocal=" );
        TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( buf, _startContinuous );
        buf.append( "\nendContLocal=" );
        TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( buf, _endContinuous );

        if ( _openAuction != null || _closeAuction != null ) {
            buf.append( "\n        openAuction=(" );
            if ( _openAuction != null || _openAuction.getType() == Auction.Type.Null ) {
                _openAuction.toString( buf );
            } else {
                buf.append( "N/A" );
            }
            buf.append( ")\n        closeAuction=(" );
            if ( _closeAuction != null || _closeAuction.getType() == Auction.Type.Null ) {
                _closeAuction.toString( buf );
            } else {
                buf.append( "N/A" );
            }
            buf.append( ")" );
        }

        return buf;
    }

    @Override public void setHolidays( LinkedHashMap<Integer, HolidayEntry> holidays ) {
        _holidays = holidays;
    }

    @Override public boolean isHalfDay() { return _isHalfDay; }

    @Override public void setHalfDay( boolean isHalfDay ) {
        _isHalfDay = isHalfDay;
        setCloseTime();
    }

    private ExchangeState exchangeStateFor( final int timeHHMMSS, final int closeHHMMSS ) {

        if ( _openHHMMSS > closeHHMMSS ) { // spans midnight .. no auction handling as yet for those exchanges
            if ( timeHHMMSS > closeHHMMSS && timeHHMMSS < _openHHMMSS ) {
                return ExchangeState.Closed;
            } else {
                return ExchangeState.Continuous;
            }
        } else {
            if ( timeHHMMSS < _openHHMMSS ) {
                return ExchangeState.Closed;
            }
            if ( timeHHMMSS >= closeHHMMSS ) {
                return ExchangeState.Closed;
            }
        }

        if ( Utils.hasVal( _openAuctionHHMMSS ) ) {
            if ( timeHHMMSS < _openAuctionHHMMSS ) {
                return ExchangeState.PreOpen;
            }

            if ( timeHHMMSS < _startContinuousHHMMSS ) {
                return ExchangeState.OpeningAuction;
            }
        } else {
            if ( _openHHMMSS < closeHHMMSS && timeHHMMSS < _startContinuousHHMMSS ) {
                return ExchangeState.Closed;
            }
        }

        if ( Utils.hasVal( _closeAuctionOffsetSecs ) ) {
            int closeAuctionSecs        = toSecs( closeHHMMSS ) - _closeAuctionOffsetSecs;
            int closeAuctionStartHHMMSS = secsToHHMMSS( closeAuctionSecs );

            if ( timeHHMMSS < closeAuctionStartHHMMSS ) {
                return ExchangeState.Continuous;
            }

            if ( timeHHMMSS < closeHHMMSS ) {
                return ExchangeState.ClosingAuction;
            }
        } else {
            if ( timeHHMMSS < closeHHMMSS ) {
                return ExchangeState.Continuous;
            }
        }

        return ExchangeState.Closed;
    }

    private int getTimeHHMMSS( final Calendar dateTime ) {
        int hour = dateTime.get( Calendar.HOUR_OF_DAY );
        int min  = dateTime.get( Calendar.MINUTE );
        int sec  = dateTime.get( Calendar.SECOND );
        return (hour * 10000) + (min * 100) + sec;
    }

    private int secsToHHMMSS( final int secs ) {
        int ss        = secs % 60;
        int minsHours = secs / 60;
        int mm        = minsHours % 60;
        int hh        = minsHours / 60;

        return hh * 10000 + mm * 100 + ss;
    }

    private void setCloseTime() {
        if ( _isHalfDay ) {

            _closeTime = _halfDayCal.getTimeInMillis();

            long auctionLengthMS = _closeAuction.getEndTime() - _closeAuction.getStartTime();

            if ( auctionLengthMS == 0 ) {
                _endContinuous = _closeTime;
            } else {
                _endContinuous = _closeTime - auctionLengthMS;

                _closeAuction.setHalfDay( _endContinuous );
            }

        } else {
            _endContinuous = _endContCal.getTimeInMillis();
            _closeTime     = _endCal.getTimeInMillis();
        }

        if ( _closeTime < _startContinuous ) {
            _overnight = true;
        }
    }

    private int toSecs( final int HHMMSS ) {
        int hh = HHMMSS / 10000;
        int mm = (HHMMSS / 100) % 100;
        int ss = HHMMSS % 100;

        return ((hh * 60) + mm) * 60 + ss;
    }
}
