/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.exchange.loader;

import com.rr.core.lang.RTStartupException;
import com.rr.core.lang.TimeUtilsFactory;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.model.Auction;
import com.rr.core.model.ExchangeCode;
import com.rr.core.model.ExchangeSession;
import com.rr.model.xml.XMLException;
import com.rr.model.xml.XMLHelper;
import com.rr.model.xml.XMLMissingException;
import com.rr.om.exchange.ExchangeManager;
import com.rr.om.model.instrument.MultiMktExchangeSession;
import com.rr.om.model.instrument.SingleExchangeSession;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.*;

/**
 * XMLExchangeLoader loads XML definitions from file and merges with the hard coded info to instantiate the
 * exchange instance.
 */
public class XMLExchangeLoader {

    private final String    _configFile;
    private       XMLHelper _helper;

    public XMLExchangeLoader( String configFile ) {
        _configFile = configFile;
    }

    public void load() throws RTStartupException {
        _helper = new XMLHelper( _configFile );

        try {
            _helper.parse();

            Element exchanges = _helper.getElement( "Exchanges", true );

            loadExchanges( exchanges );

        } catch( Exception e ) {

            throw new RTStartupException( ", file=" + _configFile + " : " + e.getMessage(), e );
        }
    }

    private Calendar getAttrTime( Element node, String attrName, TimeZone timezone ) throws XMLException {
        String   time = _helper.getAttr( node, attrName, false );
        Calendar cal  = null;
        if ( time != null ) {
            cal = TimeUtilsFactory.safeTimeUtils().getTimeAsToday( time, timezone );
        }
        return cal;
    }

    /**
     * ref="OpenTime"  startOffset="+00:00:00" endOffset="+00:10:00"
     *
     * @param openTime
     * @param closeTime
     * @return
     * @throws XMLMissingException
     */
    private Auction getAuction( Node sessionElem,
                                String auctionElemName,
                                Auction.Type type,
                                Calendar openTime,
                                Calendar closeTime ) throws XMLException {

        Element auctionElem = _helper.getChildElement( sessionElem, auctionElemName, false );
        String  ref         = _helper.getAttr( auctionElem, "ref", true );
        String  startOffset = _helper.getAttr( auctionElem, "startOffset", true );
        String  endOffset   = _helper.getAttr( auctionElem, "endOffset", true );

        if ( ref.equalsIgnoreCase( "OpenTime" ) ) {
            return getAuction( type, openTime, startOffset, endOffset );
        } else if ( ref.equalsIgnoreCase( "CloseTime" ) ) {
            return getAuction( type, closeTime, startOffset, endOffset );
        } else {
            throw new XMLException( "XMLExchangeLoader.getAuction() ref attr must be one either OpenTime or CloseTime not " + ref );
        }
    }

    private Auction getAuction( Auction.Type type, Calendar base, String startOffset, String endOffset ) {
        Calendar startAuction = TimeUtilsFactory.safeTimeUtils().adjust( (Calendar) base.clone(), startOffset );
        Calendar endAuction   = TimeUtilsFactory.safeTimeUtils().adjust( (Calendar) base.clone(), endOffset );

        return new Auction( startAuction, endAuction, type );
    }

    /**
     * sample multi session exchange :-
     *
     * <Sessions expireTimeForSendEODEvents="17:00:00">
     * <Session>
     * <OpenAuction  ref="OpenTime"  startOffset="+00:00:00" endOffset="+00:10:00"/>
     * <CloseAuction ref="CloseTime" startOffset="-00:29:30" endOffset="-00:00:00"/>
     * </Session>
     * <Segments id="id" set="IOB,IOBE,IOBU,ITBB,ITBU">
     * <OpenAuction  ref="OpenTime"  startOffset="+00:10:00" endOffset="+00:25:00"/>
     * <CloseAuction ref="CloseTime" startOffset="-01:29:30" endOffset="-00:00:00"/>
     * </Segments>
     * </Sessions>
     *
     * @param openTime
     * @param closeTime
     * @param timezone
     * @return
     * @throws XMLMissingException
     */
    private ExchangeSession getExchangeSession( Element sessions,
                                                Calendar openTime,
                                                Calendar halfDayCloseAt,
                                                Calendar closeTime,
                                                TimeZone timezone,
                                                ExchangeCode exchangeCode ) throws XMLException {

        Element    session     = _helper.getChildElement( sessions, "Session", false );
        List<Node> segmentList = _helper.getChildElements( sessions, "Segments", false );

        SingleExchangeSession sess;

        ZString id = new ViewString( exchangeCode.getMIC() + " default" );
        if ( session == null ) {
            sess = new SingleExchangeSession( id, openTime, halfDayCloseAt, closeTime, null, null, exchangeCode );
        } else if ( segmentList == null ) {
            Auction openAuction  = getAuction( session, "OpenAuction", Auction.Type.Open, openTime, closeTime );
            Auction closeAuction = getAuction( session, "CloseAuction", Auction.Type.Close, openTime, closeTime );

            sess = new SingleExchangeSession( id, openTime, halfDayCloseAt, closeTime, openAuction, closeAuction, exchangeCode );
        } else {

            Map<ZString, ExchangeSession> sessMap = new HashMap<>();

            Auction openAuc  = getAuction( session, "OpenAuction", Auction.Type.Open, openTime, closeTime );
            Auction closeAuc = getAuction( session, "CloseAuction", Auction.Type.Close, openTime, closeTime );

            for ( Node segmentNode : segmentList ) {
                if ( segmentNode.getNodeType() == Node.ELEMENT_NODE ) {

                    String segId      = _helper.getAttr( segmentNode, "id", true );
                    String segmentSet = _helper.getAttr( segmentNode, "set", true );

                    Auction openSegAuction  = getAuction( segmentNode, "OpenAuction", Auction.Type.Open, openTime, closeTime );
                    Auction closeSegAuction = getAuction( segmentNode, "CloseAuction", Auction.Type.Close, openTime, closeTime );

                    ExchangeSession segSess = new SingleExchangeSession( new ViewString( segId ), openTime, halfDayCloseAt,
                                                                         closeTime, openSegAuction, closeSegAuction, exchangeCode );

                    String[] segments = segmentSet.split( "," );

                    for ( String segment : segments ) {
                        sessMap.put( new ViewString( segment ), segSess );
                    }
                }
            }

            sess = new MultiMktExchangeSession( id, openTime, null, null, halfDayCloseAt, closeTime, openAuc, null, closeAuc, sessMap, exchangeCode );
        }

        return sess;
    }

    private ExchangeCode getMIC( Element node ) throws XMLMissingException {
        String micCode = _helper.getAttr( node, "mic", true );

        return ExchangeCode.getFromMktSegmentMIC( new ViewString( micCode ) );
    }

    private Calendar getTime( Element node, String elemName, TimeZone timezone ) throws XMLException {
        String   time = _helper.getChildElementValue( node, elemName, false );
        Calendar cal  = null;
        if ( time != null ) {
            cal = TimeUtilsFactory.safeTimeUtils().getTimeAsToday( time, timezone );
        }
        return cal;
    }

    private TimeZone getTimeZone( Element node ) throws XMLMissingException {
        String   tzs = _helper.getAttr( node, "timezone", true );
        TimeZone t   = TimeZone.getTimeZone( tzs );
        return t;
    }

    private void instantiateExchange( ExchangeCode micCode,
                                      TimeZone timezone,
                                      Calendar eodExpireEventSend,
                                      ExchangeSession session,
                                      Calendar resetTime ) {

        ExchangeManager.instance().register( micCode, timezone, eodExpireEventSend, session, resetTime );
    }

    /**
     * <Exchange mic="XLON" timezone="Europe/London">
     * <HalfDays>24/12/2010,31/12/2010</HalfDays>
     * <ResetTime>06:00:01</ResetTime>
     * <OpenTime>07:50:00</OpenTime>
     * <CloseTime>17:00:00</CloseTime>
     * <HalfDayClose>13:00:00</HalfDayClose>
     * <Sessions expireTimeForSendEODEvents="17:00:00">
     * <Session>
     * <OpenAuction  ref="OpenTime"  startOffset="+00:00:00" endOffset="+00:10:00"/>
     * <CloseAuction ref="CloseTime" startOffset="-00:29:30" endOffset="-00:00:00"/>
     * </Session>
     * <Segmets set="IOB,IOBE,IOBU,ITBB,ITBU">
     * <OpenAuction  ref="OpenTime"  startOffset="+00:10:00" endOffset="+00:25:00"/>
     * <CloseAuction ref="CloseTime" startOffset="-01:29:30" endOffset="-00:00:00"/>
     * </Segments>
     * </Sessions>
     * </Exchange>
     *
     * @throws XMLMissingException
     */
    private void loadExchange( Element node ) throws XMLException {
        ExchangeCode    micCode          = getMIC( node );
        TimeZone        timezone         = getTimeZone( node );
        Calendar        openTime         = getTime( node, "OpenTime", timezone );
        Calendar        closeTime        = getTime( node, "CloseTime", timezone );
        Calendar        resetTime        = getTime( node, "ResetTime", timezone );
        Calendar        halfDayCloseTime = getTime( node, "HalfDayClose", timezone );
        Element         sessions         = _helper.getChildElement( node, "Sessions", true );
        ExchangeSession session          = getExchangeSession( sessions, openTime, halfDayCloseTime, closeTime, timezone, micCode );

        if ( resetTime == null ) {
            resetTime = getTime( node, "CloseTime", timezone );
            resetTime.add( Calendar.MILLISECOND, 250 );
        }

        Calendar eodSendEventTime = getAttrTime( sessions, "expireTimeForSendEODEvents", timezone );

        instantiateExchange( micCode, timezone, eodSendEventTime, session, resetTime );
    }

    private void loadExchanges( Element exchanges ) throws XMLException {
        List<Node> exchangeList = _helper.getChildElements( exchanges, "Exchange", true );

        for ( Node node : exchangeList ) {
            if ( node.getNodeType() == Node.ELEMENT_NODE ) {
                loadExchange( (Element) node );
            }
        }
    }
}
