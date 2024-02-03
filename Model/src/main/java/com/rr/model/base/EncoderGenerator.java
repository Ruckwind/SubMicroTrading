/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

import com.rr.core.utils.FileException;

import java.io.IOException;

public interface EncoderGenerator {

    void generate( InternalModel internal, BinaryCodecModel codecs, BinaryCodecDefinition def, BinaryModel binModel ) throws FileException, IOException;
}
