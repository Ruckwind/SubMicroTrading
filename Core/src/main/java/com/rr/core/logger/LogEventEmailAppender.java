package com.rr.core.logger;

import com.rr.core.lang.ClockFactory;
import com.rr.core.lang.Constants;
import com.rr.core.lang.ErrorCode;
import com.rr.core.lang.ReusableString;
import com.rr.core.tasks.BaseTimerTask;
import com.rr.core.tasks.ZTimerFactory;
import com.rr.core.tasks.ZTimerTask;
import com.rr.core.utils.EmailProxy;
import com.rr.core.utils.SMSProxy;
import com.rr.core.utils.ShutdownManager;
import com.rr.core.utils.StringUtils;

import java.nio.ByteBuffer;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

public class LogEventEmailAppender implements Appender {

    private static final Logger _log = ConsoleFactory.console( LogEventFileAppender.class, Level.info );

    private static final ErrorCode ERR_ENCODING = new ErrorCode( "LEE100", "Error encoding event for email " );

    private static final int  MAX_LINE_SIZE       = 16384;
    private static final int  MAX_LINES_PER_MAIL  = 1000;
    private static final int  MIN_BATCH_SIZE_SECS = 60;
    private static final long MAX_BATCH_SIZE_MS   = 10 * 60 * 1000; // 10 minutes

    private static final long SMS_ALERT_1ST_DELAY   = 0;
    private static final long SMS_ALERT_2ND_DELAY   = 15 * 60 * 1000; // 15 mins
    private static final long SMS_ALERT_OTHER_DELAY = Constants.MS_IN_DAY;

    private static class MailFields {

        private final    String         _subjectHdr;
        private final    String[]       _receiverList;
        private          ReusableString _msgBody = new ReusableString( 1024 );
        private volatile int            _lines   = 0;
        private          int            _numErrorsInBatch;
        private          int            _numWarningsInBatch;
        private          int            _numVHighInBatch;

        public MailFields( String subjectHdr, final String[] receiverList ) {
            _subjectHdr   = subjectHdr;
            _receiverList = receiverList;
        }

        public void reset() {
            _msgBody.reset();
            _numWarningsInBatch = 0;
            _numErrorsInBatch   = 0;
            _numVHighInBatch    = 0;
            _lines              = 0;
        }
    }
    private final ByteBuffer     _directBlockBuf;
    private final MailFields _defaultMail;
    private final ConcurrentHashMap<String, MailFields> _customMails = new ConcurrentHashMap<String, MailFields>();
    private       ReusableString _subjectLine        = new ReusableString( 100 );
    private       long           _batchSizeSecsMS;
    private       Level          _logLevel;
    private       ReusableString _logMsg             = new ReusableString( 100 );
    private       int            _totErrors;
    private       int            _totWarnings;
    private       boolean        _init;
    private       ZTimerTask     _flushTask;
    private       Appender       _chainAppender;
    private       long           _defaultIntervalMS;
    private       long           _lastSMSAlert       = 0;
    private       long           _nextSMSThresholdMS = SMS_ALERT_1ST_DELAY;

    public LogEventEmailAppender( final String[] receiverList, int batchSizeSecs, String subject ) {

        if ( batchSizeSecs < MIN_BATCH_SIZE_SECS ) {
            _log.info( "LogEventEmailAppender batchSizeSecs of " + batchSizeSecs + " is too small, changing to " + MIN_BATCH_SIZE_SECS );
            batchSizeSecs = MIN_BATCH_SIZE_SECS;
        }

        _log.info( "LogEventEmailAppender batchSizeSecs of " + batchSizeSecs + " seconds" );

        _defaultMail = new MailFields( subject, receiverList );

        _batchSizeSecsMS   = batchSizeSecs * 1000;
        _defaultIntervalMS = _batchSizeSecsMS;

        _directBlockBuf = ByteBuffer.allocate( MAX_LINE_SIZE );

        ShutdownManager.instance().register( "LogEventEmailAppenderFlush", () -> doFlush(), ShutdownManager.Priority.Low );
    }

    @Override public void chain( final Appender dest ) {
        _chainAppender = dest;
    }

    @Override public void close() {
        flush();
    }

    @Override public void flush() {
        // flush via timed task

        if ( _chainAppender != null ) _chainAppender.flush();
    }

    @Override public void handle( final LogEvent curEvent ) {

        MailFields mf = getMailFields( curEvent );

        if ( curEvent.getLevel().ordinal() >= _logLevel.ordinal() ) {

            if ( curEvent.getLevel() == Level.ERROR ) ++mf._numErrorsInBatch;
            else if ( curEvent.getLevel() == Level.WARN ) ++mf._numWarningsInBatch;
            else if ( curEvent.getLevel() == Level.vhigh ) ++mf._numVHighInBatch;

            if ( ++mf._lines <= MAX_LINES_PER_MAIL ) {
                _directBlockBuf.clear();
                try {
                    curEvent.encode( _directBlockBuf );
                    synchronized( this ) {
                        mf._msgBody.append( _directBlockBuf.array(), 0, _directBlockBuf.position() ).append( "\n\n" );

                        if ( mf._lines == MAX_LINES_PER_MAIL ) {
                            mf._msgBody.append( "<TRUNCATED>\n" );
                        }
                    }
                } catch( Exception e ) {
                    _log.error( ERR_ENCODING, " event : " + curEvent.toString(), e );
                }
            }
        }

        if ( _chainAppender != null ) {
            _chainAppender.handle( curEvent );
        } else {
            LogEventRecyler.recycle( curEvent );
        }
    }

    @Override public void init( Level level ) {

        if ( level.ordinal() < Level.vhigh.ordinal() ) {
            level = Level.vhigh;
        }

        if ( !_init ) {
            _logLevel = level;

            _init = true;
        }
    }

    @Override public boolean isEnabledFor( final Level level ) {
        return (level.ordinal() >= _logLevel.ordinal());
    }

    @Override public void open() {

        if ( _flushTask == null ) {
            final long timeMS = ClockFactory.getLiveClock().currentTimeMillis() + _batchSizeSecsMS;

            _flushTask = new BaseTimerTask( "LogEventEmailAppenderFlush", TimeZone.getDefault() ) {

                @Override public void fire() {
                    doFlush();
                }
            };

            ZTimerFactory.get().schedule( _flushTask, timeMS, _batchSizeSecsMS );

            EmailProxy.instance().init();
            SMSProxy.instance().init();
        }
    }

    private void checkForTextAlert( final int numErrors, boolean forceNow ) {
        if ( numErrors == 0 ) return;

        long now = ClockFactory.get().currentInternalTime();

        if ( (now - _lastSMSAlert) > _nextSMSThresholdMS || forceNow ) {

            SMSProxy.instance().sendText( "application has " + _totErrors + " errors and " + _totWarnings + " warnings" );

            _totErrors    = 0;
            _totWarnings  = 0;
            _lastSMSAlert = now;

            if ( _nextSMSThresholdMS == SMS_ALERT_1ST_DELAY ) {
                _nextSMSThresholdMS = SMS_ALERT_2ND_DELAY;
            } else if ( _nextSMSThresholdMS == SMS_ALERT_2ND_DELAY ) {
                _nextSMSThresholdMS = SMS_ALERT_OTHER_DELAY; // no more alerts for 24 hours
            }
        }
    }

    private MailFields createMailFields( final LoggerArgs custom ) {
        String subjectHeader = custom.getSubjectHeader();
        if ( subjectHeader == null || subjectHeader.length() == 0 ) subjectHeader = _defaultMail._subjectHdr;
        String     emailRecipients = custom.getEmailRecipients();
        String[]   receiverList    = StringUtils.split( emailRecipients, ',' );
        MailFields mf              = new MailFields( subjectHeader, receiverList );
        return mf;
    }

    private void doFlush() {

        if ( _flushTask == null ) return;

        int batchLineCnt = 0;
        int batchErrs    = 0;

        String msgBody;
        int    numWarnings;
        int    lines;
        int    numErrors;
        int    numVHigh;

        lines = _defaultMail._lines;

        if ( lines > 0 ) {
            synchronized( this ) {
                lines = _defaultMail._lines;

                msgBody     = _defaultMail._msgBody.toString();
                numWarnings = _defaultMail._numWarningsInBatch;
                numErrors   = _defaultMail._numErrorsInBatch;
                numVHigh    = _defaultMail._numVHighInBatch;

                _defaultMail.reset();
            }

            _totErrors += numErrors;
            _totWarnings += numWarnings;

            if ( lines > 0 ) {
                batchLineCnt += lines;

                int skipped = (lines > MAX_LINES_PER_MAIL) ? lines - MAX_LINES_PER_MAIL : 0;

                _subjectLine.copy( _defaultMail._subjectHdr );

                if ( numErrors > 0 ) _subjectLine.append( ", numErrors=" ).append( numErrors );
                if ( numWarnings > 0 ) _subjectLine.append( ", numWarnings=" ).append( numWarnings );
                if ( numVHigh > 0 ) _subjectLine.append( ", numVHigh=" ).append( numVHigh );
                if ( skipped > 0 ) _subjectLine.append( ", skipped=" ).append( skipped );

                batchErrs += numErrors;

                _log.info( _logMsg.copy( "LogEventEmailAppender.flush about to send mail, errors=" )
                                  .append( numErrors ).append( ", warnings=" ).append( numWarnings ).append( ", SKIPPED=" ).append( skipped ) );

                EmailProxy.instance().sendMail( _defaultMail._receiverList, _subjectLine.toString(), msgBody );

                _log.info( _logMsg.copy( "LogEventEmailAppender.flush sent mail, errors=" ).append( numErrors )
                                  .append( ", warnings=" ).append( numWarnings )
                                  .append( ", numVHigh=" ).append( numVHigh )
                                  .append( ", SKIPPED=" ).append( ", SKIPPED=" )
                                  .append( skipped ) );
            }
        }

        if ( _customMails.size() > 0 ) {
            for ( final MailFields value : _customMails.values() ) {

                if ( value._lines == 0 ) continue;

                synchronized( value ) { // concurrent : at this point lines could be > 0

                    lines = value._lines;

                    msgBody     = value._msgBody.toString();
                    numWarnings = value._numWarningsInBatch;
                    numErrors   = value._numErrorsInBatch;
                    numVHigh    = value._numVHighInBatch;

                    value.reset();
                }

                if ( lines == 0 ) {
                    continue; // empty check NEXT
                }

                batchLineCnt += lines;
                batchErrs += numErrors;

                _totErrors += numErrors;
                _totWarnings += numWarnings;

                int skipped = (lines > MAX_LINES_PER_MAIL) ? lines - MAX_LINES_PER_MAIL : 0;

                _subjectLine.copy( value._subjectHdr );

                if ( numErrors > 0 ) _subjectLine.append( ", numErrors=" ).append( numErrors );
                if ( numWarnings > 0 ) _subjectLine.append( ", numWarnings=" ).append( numWarnings );
                if ( numVHigh > 0 ) _subjectLine.append( ", numVHigh=" ).append( numVHigh );
                if ( skipped > 0 ) _subjectLine.append( ", skipped=" ).append( skipped );

                EmailProxy.instance().sendMail( value._receiverList, _subjectLine.toString(), msgBody );
            }
        }

        if ( batchLineCnt == 0 ) {
            if ( _log.isEnabledFor( Level.trace ) ) {
                _log.log( Level.trace, "LogEventEmailAppender no events to email skip timer callback" );
            }

            _batchSizeSecsMS = _defaultIntervalMS;
            _flushTask.setTaskInterval( _defaultIntervalMS );

        } else {
            _batchSizeSecsMS = _batchSizeSecsMS * 2;
            if ( _batchSizeSecsMS > MAX_BATCH_SIZE_MS ) {
                _batchSizeSecsMS = MAX_BATCH_SIZE_MS;
            }

            _flushTask.setTaskInterval( _batchSizeSecsMS );
        }

        if ( batchErrs > 0 ) {
            checkForTextAlert( batchErrs, true );
        }

        if ( _chainAppender != null ) _chainAppender.flush();
    }

    private MailFields getMailFields( final LogEvent curEvent ) {
        final LoggerArgs custom = curEvent.getCustomLogArgs();

        if ( custom == null ) return _defaultMail;

        String emailRecipients = custom.getEmailRecipients();

        if ( emailRecipients == null || emailRecipients.length() == 0 ) return _defaultMail;

        return _customMails.computeIfAbsent( emailRecipients, ( s ) -> createMailFields( custom ) );
    }
}
