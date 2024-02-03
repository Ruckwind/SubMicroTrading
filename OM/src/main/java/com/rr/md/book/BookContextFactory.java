package com.rr.md.book;

import com.rr.core.component.SMTComponent;
import com.rr.core.model.BookContextImpl;
import com.rr.core.model.NoArgsFactory;

public class BookContextFactory implements NoArgsFactory<BookContextImpl>, SMTComponent {

    private final String _id;

    public BookContextFactory( final String id ) {
        _id = id;
    }

    @Override public BookContextImpl create() {
        return new BookContextImpl();
    }

    @Override public String getComponentId() {
        return _id;
    }

}
