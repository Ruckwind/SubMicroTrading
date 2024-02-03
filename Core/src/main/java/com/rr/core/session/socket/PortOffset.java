package com.rr.core.session.socket;

public class PortOffset {

    private static int _portOffset = (int) (15000 + 20000 * Math.random());

    public static synchronized int getNext() {
        int p = _portOffset;

        _portOffset += 257;

        return p;
    }
}
