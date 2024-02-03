package com.rr.core.model.book;

import com.rr.core.model.SnapableMktData;
import com.rr.core.utils.Utils;
import com.rr.core.utils.lock.OptimisticReadWriteLock;

@SuppressWarnings( "unchecked" )
public final class MktDataUtils {

    /**
     * check if src book is ahead of copy and if so lock and snap it
     *
     * @param mktDataSrc
     * @param mktDataDest
     * @param <T>
     * @return false if the book dest is uptodate/ahead of the src book, true if src book later and book has been snapped to the dest book
     */
    public static <T extends SnapableMktData> boolean safeSnap( final T mktDataSrc, final T mktDataDest ) {
        if ( !shouldSnap( mktDataSrc, mktDataDest ) ) {
            return false;
        }

        final OptimisticReadWriteLock lock = mktDataSrc.getLock();

        long stamp = lock.tryOptimisticRead();

        mktDataSrc.snapTo( mktDataDest );

        if ( !lock.validate( stamp ) ) {

            stamp = lock.readLock();

            try {
                mktDataSrc.snapTo( mktDataDest );
            } finally {
                lock.unlockRead( stamp );
            }
        }

        return true;
    }

    public static <T extends SnapableMktData> boolean shouldSnap( final T mktDataSrc, final T mktDataCopy ) {

        if ( mktDataCopy == mktDataSrc ) return false;

        final long srcEventTimestamp  = mktDataSrc.getEventTimestamp();
        final long copyEventTimestamp = mktDataCopy.getEventTimestamp();

        if ( srcEventTimestamp > copyEventTimestamp || Utils.isNullOrZero( srcEventTimestamp ) || Utils.isNullOrZero( copyEventTimestamp ) ) {
            return true;
        }

        if ( srcEventTimestamp == copyEventTimestamp ) {
            if ( Utils.isNull( mktDataSrc.getDataSeqNum() ) ) {
                return true;
            }

            if ( mktDataSrc.getDataSeqNum() > mktDataCopy.getDataSeqNum() ) {
                return true;
            }

            if ( mktDataSrc.getDataSeqNum() <= 0 || mktDataCopy.getDataSeqNum() <= 0 ) {
                return true; // @TODO CHANGE DataSeqNum TO LONG SO EDGE CASE WONT REALISTICALLY HAPPEN
            }
        }

        return false;
    }
}
