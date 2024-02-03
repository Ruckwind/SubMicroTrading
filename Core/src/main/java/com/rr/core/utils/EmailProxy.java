/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.utils;

import com.rr.core.lang.Env;
import com.rr.core.lang.ErrorCode;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.properties.AppProps;
import com.rr.core.properties.CoreProps;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.Properties;

public class EmailProxy {

    private static final Logger _log = ConsoleFactory.console( EmailProxy.class, Level.info );

    private static final ErrorCode CANT_SEND_MAIL = new ErrorCode( "EMP100", "Error sending mail" );

    private static EmailProxy _instance = new EmailProxy();
    private final String _user;
    private final String _pwd;
    private Session _mailSession;

    public static EmailProxy instance() {
        return _instance;
    }

    private EmailProxy() {
        AppProps props = AppProps.instance();

        _user = props.getProperty( CoreProps.EMAIL_USER );
        _pwd  = props.getProperty( CoreProps.EMAIL_PASSWORD );
    }

    public void init() {
        getSession();
    }

    @SuppressWarnings( "boxing" )
    public void sendMail( String to[], String subject, String body ) {

        if ( to == null || to.length == 0 ) return;

        getSession(); // lazy create session

        String fromUser              = "";
        String fromUserEmailPassword = "";
        String emailHost             = "";
        String protocol              = "";

        try {
            /**
             * Sender's credentials
             * */
            Properties emailProperties = new Properties( System.getProperties() );
            emailProperties.putIfAbsent( "mail.protocol", "smtp" );
            emailProperties.putIfAbsent( "mail.smtp.starttls.enable", true );
            emailProperties.putIfAbsent( "mail.smtp.host", "smtp.gmail.com" );
            emailProperties.putIfAbsent( "mail.smtp.port", "587" );
            emailProperties.putIfAbsent( "mail.smtp.auth", true );
            emailProperties.putIfAbsent( "mail.smtp.user", _user );
            emailProperties.putIfAbsent( "mail.smtp.password", _pwd );

            fromUser              = emailProperties.getProperty( "mail.smtp.user" );
            fromUserEmailPassword = emailProperties.getProperty( "mail.smtp.password" );
            emailHost             = emailProperties.getProperty( "mail.smtp.host" );
            protocol              = emailProperties.getProperty( "mail.protocol" );

            Transport transport = _mailSession.getTransport( protocol );
            transport.connect( emailHost, fromUser, fromUserEmailPassword );

            /**
             * Draft the message
             * */
            MimeMessage emailMessage = makeMessage( to, subject, body );

            /**
             * Send the mail
             * */
            transport.sendMessage( emailMessage, emailMessage.getAllRecipients() );
            transport.close();

        } catch( MessagingException e ) {
            _log.error( CANT_SEND_MAIL,
                        "emailHost=" + emailHost +
                        ", protocol=" + protocol +
                        ", to=" + Arrays.toString( to ) +
                        ", subject=" + subject +
                        ", user=" + fromUser +
                        ", pwd=" + fromUserEmailPassword +
                        ", subject=" + subject +
                        " : " +
                        e.getMessage(), e );
        }
    }

    @SuppressWarnings( "boxing" )
    private synchronized Session getSession() {
        if ( _mailSession == null ) {
            Properties emailProperties = new Properties( System.getProperties() );
            emailProperties.putIfAbsent( "mail.smtp.starttls.enable", true );
            emailProperties.putIfAbsent( "mail.smtp.host", "smtp.gmail.com" );
            emailProperties.putIfAbsent( "mail.smtp.port", "587" );
            emailProperties.putIfAbsent( "mail.smtp.auth", true );
            emailProperties.putIfAbsent( "mail.smtp.user", _user );
            emailProperties.putIfAbsent( "mail.smtp.password", _pwd );
            _mailSession = Session.getDefaultInstance( emailProperties, null );
            // _mailSession.setDebug( true );
        }
        return _mailSession;
    }

    private MimeMessage makeMessage( String[] to, String subject, String body ) throws MessagingException {
        try {
            final String env = AppProps.instance().getEnv().name();
            final String app = AppProps.instance().getAppName();

            if ( !subject.contains( env ) && !subject.contains( app ) ) {
                subject = subject + " (" + env + "/" + app + ")";
            }
            if ( !subject.contains( env ) ) {
                subject = subject + " (" + env + ")";
            }
        } catch( Exception e ) {
            // ignore
        }
        MimeMessage emailMessage = new MimeMessage( _mailSession );
        for ( int i = 0; i < to.length; i++ ) {
            emailMessage.addRecipient( Message.RecipientType.TO, new InternetAddress( to[ i ] ) );
        }
        emailMessage.setSubject( subject );
        emailMessage.setText( body );// for a text email
        return emailMessage;
    }
}