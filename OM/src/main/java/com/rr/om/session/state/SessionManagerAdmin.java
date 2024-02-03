/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.session.state;

import com.rr.core.admin.AdminReply;
import com.rr.core.admin.AdminTableReply;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.session.NonBlockingSession;
import com.rr.core.session.RecoverableSession;
import com.rr.core.utils.ThreadUtilsFactory;
import com.rr.om.session.SessionManager;
import com.rr.om.session.fixsocket.FixSocketSession;

public class SessionManagerAdmin implements SessionManagerAdminMBean {

    private static final Logger   _log                   = LoggerFactory.create( SessionManagerAdmin.class );
    private static final String[] _columns               = { "SessionName", "Status", "NextExpSeqNumIn", "NextSeqNumOut" };
    private static final int      SEQ_NUM_RESET_PAUSE_MS = 100;

    private static int _nextInstance = 1;

    private final SessionManager _sessMgr;
    private final String         _name;

    private static int nextId() {
        return _nextInstance++;
    }

    public SessionManagerAdmin( SessionManager sessionManager ) {
        _sessMgr = sessionManager;
        _name    = "SessionManagerAdmin" + nextId();
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public String listAllSessions() {
        AdminReply           reply    = new AdminTableReply( _columns );
        RecoverableSession[] sessions = _sessMgr.getUpStreamSessions();
        for ( RecoverableSession session : sessions ) {
            list( reply, session );
        }
        sessions = _sessMgr.getDownStreamSessions();
        for ( RecoverableSession session : sessions ) {
            list( reply, session );
        }
        list( reply, _sessMgr.getHub() );
        sessions = _sessMgr.getOtherSessions();
        for ( RecoverableSession session : sessions ) {
            list( reply, session );
        }
        return reply.end();
    }

    @Override
    public String listClientSessions() {
        AdminReply           reply    = new AdminTableReply( _columns );
        RecoverableSession[] sessions = _sessMgr.getUpStreamSessions();
        for ( RecoverableSession session : sessions ) {
            list( reply, session );
        }
        return reply.end();
    }

    @Override
    public String listExchangeSessions() {
        AdminReply           reply    = new AdminTableReply( _columns );
        RecoverableSession[] sessions = _sessMgr.getDownStreamSessions();
        for ( RecoverableSession session : sessions ) {
            list( reply, session );
        }
        return reply.end();
    }

    @Override
    public String loginSession( String sessionName ) {
        RecoverableSession session = _sessMgr.getSession( sessionName );

        _log.info( "SessionManagerAdmin.logoutSession " + sessionName );

        if ( session == null ) return "Unable to find session " + sessionName;

        if ( session instanceof FixSocketSession ) {
            FixSocketSession fsess = (FixSocketSession) session;

            try {

                if ( fsess instanceof NonBlockingSession ) {

                    fsess.getController().forceLogOn();

                    return "Session " + sessionName + " LogOn started " + fsess.getController().info();
                }

                fsess.requestConnect();

                return "Session " + sessionName + " requested LogOn " + fsess.getController().info();

            } catch( Exception e ) {
                return "Exception " + e.getMessage();
            }
        }

        return "Session " + sessionName + " is NOT a fix session";
    }

    @Override
    public String logoutSession( String sessionName ) {
        RecoverableSession session = _sessMgr.getSession( sessionName );

        _log.info( "SessionManagerAdmin.logoutSession " + sessionName );

        if ( session == null ) return "Unable to find session " + sessionName;

        if ( session instanceof FixSocketSession ) {
            FixSocketSession fsess = (FixSocketSession) session;

            try {
                fsess.getController().forceLogOut();

                return "Session " + sessionName + " LoggedOut " + fsess.getController().info();

            } catch( Exception e ) {
                return "Exception " + e.getMessage();
            }
        }

        return "Session " + sessionName + " is NOT a fix session";
    }

    @Override
    public String pauseSession( String sessionName ) {
        RecoverableSession session = _sessMgr.getSession( sessionName );

        _log.info( "SessionManagerAdmin.pauseSession " + sessionName );

        if ( session == null ) return "Unable to find session " + sessionName;

        if ( session instanceof AbstractStatefulSocketSession ) {
            AbstractStatefulSocketSession<?, ?> fsess = (AbstractStatefulSocketSession<?, ?>) session;

            try {
                fsess.setPaused( true );

                return "Session " + sessionName + " PAUSED " + fsess.getController().info();

            } catch( Exception e ) {
                return "Exception " + e.getMessage();
            }
        }

        return "Session " + sessionName + " is NOT a fix session";
    }

    @Override
    public String resumeSession( String sessionName ) {
        RecoverableSession session = _sessMgr.getSession( sessionName );

        _log.info( "SessionManagerAdmin.resumeSession " + sessionName );

        if ( session == null ) return "Unable to find session " + sessionName;

        if ( session instanceof AbstractStatefulSocketSession ) {
            AbstractStatefulSocketSession<?, ?> fsess = (AbstractStatefulSocketSession<?, ?>) session;

            try {
                fsess.setPaused( false );

                fsess.requestConnect();

                return "Session " + sessionName + " UNPAUSED " + fsess.getController().info();

            } catch( Exception e ) {
                return "Exception " + e.getMessage();
            }
        }

        return "Session " + sessionName + " is NOT a fix session";
    }

    @Override public String safeResetSeqNums( String sessionName ) {
        RecoverableSession session = _sessMgr.getSession( sessionName );

        _log.info( "SessionManagerAdmin.safeResetSeqNums " + sessionName );

        if ( session == null ) return "safeResetSeqNums Unable to find session " + sessionName;

        if ( session instanceof FixSocketSession ) {
            FixSocketSession fsess = (FixSocketSession) session;

            Object ctl = fsess.getController();

            if ( ctl instanceof SessionSeqNumController ) {
                try {
                    _log.info( "SessionManagerAdmin.safeResetSeqNums " + sessionName + " pausings session" );

                    fsess.setPaused( true );

                    ThreadUtilsFactory.getLive().sleep( SEQ_NUM_RESET_PAUSE_MS );

                    SessionSeqNumController c = (SessionSeqNumController) ctl;

                    _log.info( "SessionManagerAdmin.safeResetSeqNums " + sessionName + " resetting seq nums" );

                    c.setSeqNums( 1, 1, true );

                    _log.info( "SessionManagerAdmin.safeResetSeqNums " + sessionName + " unpausing session" );

                    fsess.setPaused( false );

                    _log.info( "SessionManagerAdmin.safeResetSeqNums " + sessionName + " forcing connection" );

                    if ( fsess instanceof NonBlockingSession ) {

                        fsess.getController().forceLogOn();

                        return "Session " + sessionName + " safeResetSeqNums : LogOn started " + fsess.getController().info();
                    }

                    fsess.requestConnect();

                } catch( Exception e ) {
                    return "Exception " + e.getMessage();
                }

                return "Session " + sessionName + " safeResetSeqNums : LoggedOut " + fsess.getController().info();
            }
        }

        return "Session " + sessionName + " safeResetSeqNums : is NOT a fix session";
    }

    @Override
    public String setSessionSeqNums( String sessionName, int nextInSeqNum, int nextOutSeqNum, boolean passiveReset ) {
        boolean forcedReset = !passiveReset;

        _log.info( "SessionManagerAdmin.setSessionSeqNums " + sessionName + " nextInSeqNum=" + nextInSeqNum + ", nextOutSeqNum=" + nextOutSeqNum + ", forcedReset=" + forcedReset );

        RecoverableSession session = _sessMgr.getSession( sessionName );

        if ( session == null ) return "Unable to find session " + sessionName;

        if ( session instanceof AbstractStatefulSocketSession ) {
            AbstractStatefulSocketSession<?, ?> fsess = (AbstractStatefulSocketSession<?, ?>) session;

            Object ctl = fsess.getController();

            if ( ctl instanceof SessionSeqNumController ) {
                SessionSeqNumController c = (SessionSeqNumController) ctl;

                try {
                    c.setSeqNums( nextInSeqNum, nextOutSeqNum, forcedReset );
                } catch( Exception e ) {
                    return "Exception " + e.getMessage();
                }
            }
        }

        return "Session " + sessionName + " nextExpectedInSeqNum=" + nextInSeqNum + ", nextOutSeqNum=" + nextOutSeqNum;
    }

    private void list( AdminReply reply, RecoverableSession sess ) {
        if ( sess != null ) {
            reply.add( sess.getComponentId() );
            if ( sess instanceof AbstractStatefulSocketSession ) {
                AbstractStatefulSocketSession<?, ?> fsess = (AbstractStatefulSocketSession<?, ?>) sess;

                Object ctl = fsess.getController();

                if ( ctl instanceof SessionSeqNumController ) {
                    SessionSeqNumController c = (SessionSeqNumController) ctl;

                    reply.add( c.getState() );
                    reply.add( c.getNextExpectedInSeqNo() );
                    reply.add( c.getNextOutSeqNum() );

                } else {
                    reply.add( sess.isConnected() ? "Connected" : "Disconnected" );
                    reply.add( "" );
                    reply.add( "" );
                }

            } else {
                reply.add( sess.isConnected() ? "Connected" : "Disconnected" );
                reply.add( "" );
                reply.add( "" );
            }
        }
    }
}
