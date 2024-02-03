package com.rr.model.generated.internal.core;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/


import javax.annotation.Generated;

@Generated( "com.rr.model.generated.internal.core.EventIds" )

public interface EventIds {

    public int ID_HEARTBEAT = 1;
    public int ID_ENDOFSESSION = 2;
    public int ID_LOGOUTREQUEST = 3;
    public int ID_TESTREQUEST = 4;
    public int ID_LOGON = 5;
    public int ID_LOGOUT = 6;
    public int ID_SESSIONREJECT = 7;
    public int ID_RESENDREQUEST = 8;
    public int ID_CLIENTRESYNCSENTMSGS = 9;
    public int ID_SEQUENCERESET = 10;
    public int ID_TRADINGSESSIONSTATUS = 11;
    public int ID_SECMASSSTATGRP = 12;
    public int ID_MASSINSTRUMENTSTATECHANGE = 13;
    public int ID_NEWORDERSINGLE = 14;
    public int ID_CANCELREPLACEREQUEST = 15;
    public int ID_CANCELREQUEST = 16;
    public int ID_FORCECANCEL = 17;
    public int ID_VAGUEORDERREJECT = 18;
    public int ID_CANCELREJECT = 19;
    public int ID_NEWORDERACK = 20;
    public int ID_TRADENEW = 21;
    public int ID_REJECTED = 22;
    public int ID_CANCELLED = 23;
    public int ID_REPLACED = 24;
    public int ID_DONEFORDAY = 25;
    public int ID_STOPPED = 26;
    public int ID_EXPIRED = 27;
    public int ID_SUSPENDED = 28;
    public int ID_RESTATED = 29;
    public int ID_TRADECORRECT = 30;
    public int ID_TRADECANCEL = 31;
    public int ID_PENDINGCANCEL = 32;
    public int ID_PENDINGREPLACE = 33;
    public int ID_PENDINGNEW = 34;
    public int ID_ORDERSTATUS = 35;
    public int ID_IGNOREDEXEC = 36;
    public int ID_CALCULATED = 37;
    public int ID_ALERTLIMITBREACH = 38;
    public int ID_ALERTTRADEMISSINGORDERS = 39;
    public int ID_SYMBOLREPEATINGGRP = 40;
    public int ID_MDREQUEST = 41;
    public int ID_TICKUPDATE = 42;
    public int ID_MDUPDATE = 43;
    public int ID_SECDEFEVENT = 44;
    public int ID_SECURITYALTID = 45;
    public int ID_SDFEEDTYPE = 46;
    public int ID_SECDEFLEG = 47;
    public int ID_MDENTRY = 48;
    public int ID_MDSNAPENTRY = 49;
    public int ID_MSGSEQNUMGAP = 50;
    public int ID_MDINCREFRESH = 51;
    public int ID_MDSNAPSHOTFULLREFRESH = 52;
    public int ID_SECURITYDEFINITION = 53;
    public int ID_SECURITYDEFINITIONUPDATE = 54;
    public int ID_PRODUCTSNAPSHOT = 55;
    public int ID_SECURITYSTATUS = 56;
    public int ID_SETTLEMENTPRICEEVENT = 57;
    public int ID_CLOSINGPRICEEVENT = 58;
    public int ID_OPENPRICEEVENT = 59;
    public int ID_OPENINTERESTEVENT = 60;
    public int ID_NEWS = 61;
    public int ID_CORPORATEACTIONEVENT = 62;
    public int ID_INSTRUMENTSIMDATA = 63;
    public int ID_REFPRICEEVENT = 64;
    public int ID_BROKERLOANRESPONSE = 65;
    public int ID_PRICELIMITCOLLAREVENT = 66;
    public int ID_SECURITYTRADINGSTATUSEVENT = 67;
    public int ID_LEANHOGINDEXEVENT = 68;
    public int ID_FORCEFLATTENCOMMAND = 69;
    public int ID_APPRUN = 70;
    public int ID_STRATINSTRUMENT = 71;
    public int ID_STRATEGYRUN = 72;
    public int ID_STRATINSTRUMENTSTATE = 73;
    public int ID_STRATEGYSTATE = 74;
    public int ID_UTPLOGON = 75;
    public int ID_UTPLOGONREJECT = 76;
    public int ID_UTPTRADINGSESSIONSTATUS = 77;
    public int ID_ETICONNECTIONGATEWAYREQUEST = 78;
    public int ID_ETICONNECTIONGATEWAYRESPONSE = 79;
    public int ID_ETISESSIONLOGONREQUEST = 80;
    public int ID_ETISESSIONLOGONRESPONSE = 81;
    public int ID_ETISESSIONLOGOUTREQUEST = 82;
    public int ID_ETISESSIONLOGOUTRESPONSE = 83;
    public int ID_ETISESSIONLOGOUTNOTIFICATION = 84;
    public int ID_ETIUSERLOGONREQUEST = 85;
    public int ID_ETIUSERLOGONRESPONSE = 86;
    public int ID_ETIUSERLOGOUTREQUEST = 87;
    public int ID_ETIUSERLOGOUTRESPONSE = 88;
    public int ID_ETITHROTTLEUPDATENOTIFICATION = 89;
    public int ID_ETISUBSCRIBE = 90;
    public int ID_ETISUBSCRIBERESPONSE = 91;
    public int ID_ETIUNSUBSCRIBE = 92;
    public int ID_ETIUNSUBSCRIBERESPONSE = 93;
    public int ID_ETIRETRANSMIT = 94;
    public int ID_ETIRETRANSMITRESPONSE = 95;
    public int ID_ETIRETRANSMITORDEREVENTS = 96;
    public int ID_ETIRETRANSMITORDEREVENTSRESPONSE = 97;
    public int ID_MILLENIUMLOGON = 98;
    public int ID_MILLENIUMLOGONREPLY = 99;
    public int ID_MILLENIUMLOGOUT = 100;
    public int ID_MILLENIUMMISSEDMESSAGEREQUEST = 101;
    public int ID_MILLENIUMMISSEDMSGREQUESTACK = 102;
    public int ID_MILLENIUMMISSEDMSGREPORT = 103;
    public int ID_BOOKADDORDER = 104;
    public int ID_BOOKDELETEORDER = 105;
    public int ID_BOOKMODIFYORDER = 106;
    public int ID_BOOKCLEAR = 107;
    public int ID_PITCHSYMBOLCLEAR = 108;
    public int ID_PITCHBOOKADDORDER = 109;
    public int ID_PITCHBOOKORDEREXECUTED = 110;
    public int ID_PITCHOFFBOOKTRADE = 111;
    public int ID_PITCHBOOKCANCELORDER = 112;
    public int ID_PITCHPRICESTATISTIC = 113;
    public int ID_AUCTIONUPDATE = 114;
    public int ID_AUCTIONSUMMARY = 115;
    public int ID_SOUPDEBUGPACKET = 116;
    public int ID_SOUPLOGINACCEPTED = 117;
    public int ID_SOUPLOGINREJECTED = 118;
    public int ID_SOUPLOGINREQUEST = 119;
    public int ID_UNSEQUENCEDDATAPACKET = 120;
}
