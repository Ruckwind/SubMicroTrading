package com.rr.md.book.l3;

import com.rr.model.generated.internal.type.Side;

public interface BookChangeListener {

    void clear();

    void set( Side side, double px, double totalQty );

    void trade( Side aggressor, double lastPx, double lastQty );
}
