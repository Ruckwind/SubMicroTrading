package com.rr.core.logger;

public class LoggerArgs {

    private final String _emailRecipients;
    private final String _subjectHeader;

    public LoggerArgs( final String emailRecipients, final String subjectHeader ) {
        _emailRecipients = emailRecipients;
        _subjectHeader   = subjectHeader;
    }

    public String getEmailRecipients() { return _emailRecipients; }

    public String getSubjectHeader()   { return _subjectHeader; }
}
