package com.rr.core.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BbgExchangeCodeTest {

    @Test
    public void mappings() {
        assertEquals( BbgExchangeCode.CME, BbgExchangeCode.fromExchangeCode( ExchangeCode.XCME ) );
    }
}
