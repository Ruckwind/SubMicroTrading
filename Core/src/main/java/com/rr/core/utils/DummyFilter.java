package com.rr.core.utils;

public final class DummyFilter implements OutFilter<Object> {

    @Override public boolean filterOut( final Object datum ) { return false; }
}
