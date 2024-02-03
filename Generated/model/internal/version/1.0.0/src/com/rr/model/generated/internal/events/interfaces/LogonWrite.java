package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.EncryptMethod;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface LogonWrite extends SessionHeaderWrite, Logon {

   // Getters and Setters
    void setSenderCompId( byte[] buf, int offset, int len );
    ReusableString getSenderCompIdForUpdate();

    void setSenderSubId( byte[] buf, int offset, int len );
    ReusableString getSenderSubIdForUpdate();

    void setTargetCompId( byte[] buf, int offset, int len );
    ReusableString getTargetCompIdForUpdate();

    void setTargetSubId( byte[] buf, int offset, int len );
    ReusableString getTargetSubIdForUpdate();

    void setOnBehalfOfId( byte[] buf, int offset, int len );
    ReusableString getOnBehalfOfIdForUpdate();

    void setEncryptMethod( EncryptMethod val );

    void setHeartBtInt( int val );

    void setRawDataLen( int val );

    void setRawData( byte[] buf, int offset, int len );
    ReusableString getRawDataForUpdate();

    void setResetSeqNumFlag( boolean val );

    void setNextExpectedMsgSeqNum( int val );

}
