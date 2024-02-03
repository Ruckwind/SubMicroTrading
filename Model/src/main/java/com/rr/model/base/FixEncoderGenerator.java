/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

import com.rr.core.utils.FileException;

import java.io.IOException;

public interface FixEncoderGenerator {

    void generate( FixModels fix, InternalModel internal, CodecModel codecs, CodecDefinition def, FixModel foxModel ) throws FileException, IOException;
}
