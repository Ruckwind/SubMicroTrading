package com.rr.core.utils.file;

import com.rr.core.lang.SerializableLambda;

@FunctionalInterface
public interface RollableFileNameGenerator extends SerializableLambda {

    String formName( String fname, int rollNumber, String extension );
}
