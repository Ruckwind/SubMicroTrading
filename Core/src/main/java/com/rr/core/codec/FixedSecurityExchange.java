package com.rr.core.codec;

import com.rr.core.model.ExchangeCode;

public interface FixedSecurityExchange {

    ExchangeCode getSecurityExchange();

    void setSecurityExchange( final ExchangeCode securityExchange );
}
