package com.rr.core.recovery.json;

import com.rr.core.lang.ZConsumer2Args;

public class DualMissingRefResolver {

    private final ZConsumer2Args<Object, Object> _setter;
    private       Object                         _resolvedA;
    private       Object                         _resolvedB;
    private       boolean                        _isAResolved;
    private       boolean                        _isBResolved;

    public DualMissingRefResolver( MissingRef a, MissingRef b, Resolver r, ZConsumer2Args<Object, Object> setter ) {

        MissingRefImpl refAWrapper = new MissingRefImpl( a.getRefComponentId() );
        refAWrapper.addResolver( ( ra ) -> resolved( true, ra ) );

        MissingRefImpl refBWrapper = new MissingRefImpl( b.getRefComponentId() );
        refBWrapper.addResolver( ( rb ) -> resolved( false, rb ) );

        r.addMissingRef( refAWrapper );
        r.addMissingRef( refBWrapper );

        _setter = setter;
    }

    private void resolved( final boolean isObjectA, final Object resolved ) {
        if ( isObjectA ) {
            _isAResolved = true;
            _resolvedA   = resolved;
        } else {
            _isBResolved = true;
            _resolvedB   = resolved;
        }

        if ( _isAResolved && _isBResolved ) {
            _setter.accept( _resolvedA, _resolvedB );
        }
    }
}
