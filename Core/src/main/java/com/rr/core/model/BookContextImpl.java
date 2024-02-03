package com.rr.core.model;

import com.rr.core.model.book.SafeBookReserver;

public class BookContextImpl implements BookContext {

    private BookReserver _askReserver = new SafeBookReserver();
    private BookReserver _bidReserver = new SafeBookReserver();

    @Override public BookReserver getAskBookReserver() {
        return _askReserver;
    }

    @Override public BookReserver getBidBookReserver() {
        return _bidReserver;
    }
}
