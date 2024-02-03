/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.utils;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.rr.core.lang.Env;
import com.rr.core.lang.ErrorCode;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.properties.AppProps;
import com.rr.core.properties.CoreProps;

import java.util.ArrayList;
import java.util.List;

public class SMSProxy {

    private static final Logger _log = ConsoleFactory.console( SMSProxy.class, Level.WARN );

    private static final ErrorCode CANT_SEND_MAIL = new ErrorCode( "SMS100", "Error sending text" );

    private static boolean  _restrictToProd = true;
    private static SMSProxy _instance       = new SMSProxy();
    private final PhoneNumber       _fromPhone;
    private final List<PhoneNumber> _destPhoneList = new ArrayList<>();
    private final String            _accountSID;
    private final String            _authToken;
    private final String            _hdr;

    public static SMSProxy instance() {
        return _instance;
    }

    public static boolean isRestrictToProd() { return _restrictToProd; }

    public static void setRestrictToProd( final boolean restrictToProd ) {
        _log.info( "Texting is now set to " + restrictToProd );

        _restrictToProd = restrictToProd;
    }

    SMSProxy() {

        AppProps props = AppProps.instance();

        Env    env       = props.getProperty( CoreProps.RUN_ENV, Env.class );
        String phoneList = null;

        if ( !isRestrictToProd() || (env.isProd() && env != Env.CORE) ) {
            phoneList = props.getProperty( CoreProps.SMS_TO_PHONES, false, null );

            _log.info( "Texting is enabled to " + phoneList );

        } else {
            _log.info( "Texting is disabled for non prod envs" );
        }

        _hdr = props.getProperty( CoreProps.APP_NAME );

        if ( phoneList != null ) {
            String fromPhone = props.getProperty( CoreProps.SMS_FROM_PHONE );
            _accountSID = props.getProperty( CoreProps.SMS_ACCOUNT_SID );
            _authToken  = props.getProperty( CoreProps.SMS_AUTH_TOKEN );

            String[] destPhones = phoneList.split( "," );
            for ( String dest : destPhones ) {
                _destPhoneList.add( new PhoneNumber( dest.trim() ) );
            }

            _fromPhone = new PhoneNumber( fromPhone.trim() );
        } else {
            _fromPhone  = null;
            _accountSID = null;
            _authToken  = null;
        }
    }

    public void init() {
        if ( _destPhoneList.size() > 0 ) {
            Twilio.init( _accountSID, _authToken );
        }
    }

    @SuppressWarnings( "boxing" )
    public void sendText( String body ) {
        if ( _destPhoneList.size() == 0 || body == null || body.length() == 0 ) {
            return; // IGNORE
        }

        try {

            _log.info( "About to send text message" );

            for ( PhoneNumber dest : _destPhoneList ) {

                String msg = _hdr + " : " + body;

                Message message = Message.creator( dest, _fromPhone, msg ).create();

                _log.info( "Sent text message to " + dest.getEndpoint() + ", sid=" + message.getSid() + ", status=" + message.getStatus() );
            }

            _log.info( "Sent text messages" );

        } catch( Exception e ) {
            _log.error( CANT_SEND_MAIL, e.getMessage() );
        }
    }
}