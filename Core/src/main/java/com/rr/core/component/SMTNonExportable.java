package com.rr.core.component;

/**
 * marker interfce given to components to be excluded from EXPORT snapshot
 */
public interface SMTNonExportable extends SMTComponent {
    /* dont include object within export */
}
