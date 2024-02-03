package com.rr.om.router;

import com.rr.core.model.EventHandler;
import com.rr.core.model.Instrument;

public interface OrderHandler extends EventHandler {

    boolean canTrade( Instrument inst );
}
