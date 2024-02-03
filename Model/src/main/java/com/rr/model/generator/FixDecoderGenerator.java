/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.generator;

import com.rr.core.utils.FileException;
import com.rr.model.base.*;

import java.io.IOException;

public interface FixDecoderGenerator {

    void generate( FixModels fix, InternalModel internal, CodecModel codecs, CodecDefinition def, FixModel binModel ) throws FileException, IOException;
}
