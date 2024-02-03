/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.generator;

import com.rr.core.utils.FileException;
import com.rr.model.base.BinaryCodecDefinition;
import com.rr.model.base.BinaryCodecModel;
import com.rr.model.base.BinaryModel;
import com.rr.model.base.InternalModel;

import java.io.IOException;

public interface DecoderGenerator {

    void generate( InternalModel internal, BinaryCodecModel codecs, BinaryCodecDefinition def, BinaryModel binModel ) throws FileException, IOException;
}
