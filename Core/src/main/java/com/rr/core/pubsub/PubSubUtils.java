package com.rr.core.pubsub;

import com.rr.core.datarec.DataPoint;
import com.rr.core.lang.Constants;
import com.rr.core.lang.Env;
import com.rr.core.model.ExchangeInstrument;
import com.rr.core.model.Instrument;
import com.rr.core.model.SecurityType;
import com.rr.core.properties.AppProps;
import com.rr.core.properties.CoreProps;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.core.utils.StringUtils;

public class PubSubUtils {

    public static final int SLOW_MKT_DATA_DURATION_FROM_SECS = 6;

    public static String APP_SUB( final String componentId ) {
        return AppProps.instance().getAppName() + "_" + componentId;
    }

    public static String makeBarTopic( final Instrument inst, final int durationSecs ) {
        int days           = durationSecs / Constants.SECS_IN_DAY;
        int secsLeftInDay  = durationSecs - days * Constants.SECS_IN_DAY;
        int hours          = secsLeftInDay / Constants.SECS_IN_HOUR;
        int secsLeftInHour = secsLeftInDay - hours * Constants.SECS_IN_HOUR;
        int mins           = secsLeftInHour / 60;
        int secs           = (secsLeftInHour - mins * 60);

        if ( durationSecs <= 0 ) {
            throw new SMTRuntimeException( "PubSubUtils bars must have a positive duration in seconds, " + inst.id() + " given " + durationSecs );
        }

        MsgStream stream = getMsgStreamForBar( durationSecs );

        String topic = startTopic( stream ) + "bar.";

        if ( days > 0 ) {
            topic = topic + days + "d";
        }
        if ( hours > 0 ) {
            topic = topic + hours + "h";
        }
        if ( mins > 0 ) {
            topic = topic + mins + "m";
        }
        if ( secs > 0 ) {
            topic = topic + secs + "s";
        }

        topic = topic + "." + getSecType( inst ) + ".";

        if ( inst.getSecurityType() == SecurityType.Equity ) {
            ExchangeInstrument ei = (ExchangeInstrument) inst;

            String symbol = StringUtils.stripChar( ei.getSymbol().toString(), ' ' );
            symbol = StringUtils.stripChar( symbol, '.' );

            topic = topic + ei.getPrimaryExchangeCode() + "." + ei.getSymbol();

        } else {

            topic = topic + inst.id();
        }

        topic = StringUtils.stripChar( topic, ' ' );

        return topic;
    }

    public static String getAllBarsTopic( int durationSecs ) {
        MsgStream stream = getMsgStreamForBar( durationSecs );

        String topic = startTopic( stream ) + "allBars.";

        int days           = durationSecs / Constants.SECS_IN_DAY;
        int secsLeftInDay  = durationSecs - days * Constants.SECS_IN_DAY;
        int hours          = secsLeftInDay / Constants.SECS_IN_HOUR;
        int secsLeftInHour = secsLeftInDay - hours * Constants.SECS_IN_HOUR;
        int mins           = secsLeftInHour / 60;
        int secs           = (secsLeftInHour - mins * 60);

        if ( days > 0 ) {
            topic = topic + days + "d";
        }

        if ( hours > 0 ) {
            topic = topic + hours + "h";
        }

        if ( mins > 0 ) {
            topic = topic + mins + "m";
        }

        if ( secs > 0 ) {
            topic = topic + secs + "s";
        }

        return topic;
    }

    public static String makeInstSubscriptionsTopic() {
        String topic = startTopic( MsgStream.subscriptions );

        return topic + "instruments";
    }

    public static String makeInstRefTopic() {
        String topic = startTopic( MsgStream.instRef );

        return topic + "all";
    }

    public static String makeDataPointTopic( DataPoint dp, final boolean publishIndivEventTopic ) {
        String topic = startTopic( MsgStream.dataPoint );

        if ( publishIndivEventTopic ) {
            if ( dp.getGroupKey() == null || dp.getGroupKey().length() == 0 ) {
                return topic + dp.getEventType().toString();
            }

            return topic + dp.getEventType().toString() + "." + dp.getGroupKey();
        }

        return topic + "combined";
    }

    public static String changeTopicEnv( String subject, final Env env ) {
        int idx = subject.indexOf( '.' );

        if ( idx != -1 ) {
            subject = env.toString() + subject.substring( idx );
        } else {
            subject = env.toString() + "." + subject;
        }

        return subject;
    }

    public static void addAllStreams( final PubSubSess subsSession ) {
        for ( MsgStream s : MsgStream.values() ) {
            subsSession.stream( s );
        }
    }

    public static String getStreamTopicWildcard( MsgStream stream ) {
        String topic = startTopic( stream );
        return topic + ">";
    }

    public static MsgStream getMsgStreamForBar( final int durationSecs ) {
        return durationSecs >= SLOW_MKT_DATA_DURATION_FROM_SECS ? MsgStream.mktDataSlow : MsgStream.mktDataFast;
    }

    public static MsgStream getMsgStreamFromTopic( final String topic ) {
        String[] bits = StringUtils.split( topic.trim(), '.' );
        if ( bits.length < 2 ) throw new SMTRuntimeException( "Invalid topic missing stream " + topic );
        String stream = bits[ 1 ].trim();
        return MsgStream.valueOf( stream );
    }

    private static String getSecType( final Instrument inst ) {
        String t = inst.getSecurityType().name().toLowerCase();

        return (t.length() > 3) ? t.substring( 0, 3 ) : t;
    }

    private static String startTopic( final MsgStream stream ) {
        Env env = AppProps.instance().getProperty( CoreProps.RUN_ENV, Env.class );

        return env.toString() + "." + stream.name() + ".";
    }
}
