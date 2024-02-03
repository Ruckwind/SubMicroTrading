/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.lang;

import com.rr.core.admin.AdminReply;
import com.rr.core.admin.AdminTableReply;
import com.rr.core.lang.ZString;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Book;
import com.rr.core.model.Identifiable;
import com.rr.core.model.book.ApiMutatableBook;
import com.rr.core.model.book.DoubleSidedBookEntry;
import com.rr.core.model.book.DoubleSidedBookEntryImpl;
import com.rr.core.model.book.UnsafeL2Book;

import java.util.Iterator;

public interface Refreshable extends Identifiable {

    void refresh();
}
