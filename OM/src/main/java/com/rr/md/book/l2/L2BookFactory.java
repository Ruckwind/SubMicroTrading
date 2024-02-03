/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.book.l2;

import com.rr.core.lang.Constants;
import com.rr.core.lang.ErrorCode;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.ExchangeInstrument;
import com.rr.core.model.Instrument;
import com.rr.core.model.InstrumentLocator;
import com.rr.core.model.book.*;
import com.rr.core.utils.ReflectUtils;
import com.rr.md.book.BookFactory;
import com.rr.md.book.MutableFixBook;

/**
 * @WARNING the book can only be updated by one thread and it MUST be created on the thread that will apply the updates
 * otherwise the recyler will be corrupted !
 */
public class L2BookFactory<T extends MutableFixBook> implements BookFactory<T> {

    public static final int DEFAULT_MAX_LEVELS = 10;
    private static final ErrorCode ERR_MISSING_INSTRUMENT = new ErrorCode( "BKF100", "No instrument found for specified key : " );
    private static final Logger _log = LoggerFactory.create( L2BookFactory.class );
    private final boolean           _useThreadSafeBook;
    private final InstrumentLocator _instrumentLocator;

    private final int _bookLevels;

    private final Class<T> _adapterClass;
    private final T        _dummyBookInstance;

    public L2BookFactory( Class<T> adapterClass, boolean useThreadSafeBook, InstrumentLocator locator, int bookLevels ) {
        _useThreadSafeBook = useThreadSafeBook;
        _instrumentLocator = locator;
        _bookLevels        = bookLevels;
        _adapterClass      = adapterClass;

        _dummyBookInstance = ReflectUtils.getPublicStaticMember( adapterClass, "DUMMY" );
    }

    /**
     * create the required book,
     * <p>
     * If instrument not found returns NULL, doesnt throw exception due to overhead of exception handling per tick
     *
     * @param inst
     * @return new L2FixBook or NULL if inst not located
     */
    @Override
    public T create( final Instrument inst ) {
        return create( inst, _bookLevels );
    }

    @Override
    public T create( final Instrument inst, int levels ) {

        _log.info( "L2BookFactory create book for inst=" + inst.getExchangeSymbol() );

        if ( inst == null ) {
            return null;
        }

        int instLevels = ((ExchangeInstrument) inst).getBookLevels();

        // check instrument supports the requested number of levels
        if ( instLevels > 0 && instLevels != Constants.UNSET_INT && instLevels < levels ) {
            levels = instLevels;
        }

        ApiMutatableBook book;

        if ( _useThreadSafeBook ) {
            book = (levels == 1) ? new LockableL1Book( inst ) : new LockableL2Book( inst, levels );
        } else {
            book = (levels == 1) ? new UnsafeL1Book( inst ) : new UnsafeL2Book( inst, levels );
        }

        Class<?>[] cargs = { ApiMutatableBook.class };
        Object[]   cvals = { book };

        return ReflectUtils.create( _adapterClass, cargs, cvals );
    }
}
