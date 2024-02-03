/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

public class Model {

    private final InternalModel    _internal;
    private final FixModels        _fix;
    private final BinaryModels     _binary;
    private final CodecModel       _codecs;
    private final CodecModel       _exchangeCodecs;
    private final CodecModel       _clientCodecs;
    private final BinaryCodecModel _binaryCodecs;
    private final FactoryModel     _factory;

    public Model() {
        _internal       = new InternalModel();
        _fix            = new FixModels();
        _binary         = new BinaryModels();
        _codecs         = new CodecModel( CodecModel.Type.Base );
        _exchangeCodecs = new CodecModel( CodecModel.Type.Exchange, _codecs );
        _clientCodecs   = new CodecModel( CodecModel.Type.Client, _codecs );
        _binaryCodecs   = new BinaryCodecModel();
        _factory        = new FactoryModel();
    }

    public BinaryModels getBinary() {
        return _binary;
    }

    public BinaryCodecModel getBinaryCodecs() {
        return _binaryCodecs;
    }

    public CodecModel getClientCodecs() {
        return _clientCodecs;
    }

    public CodecModel getCodec() {
        return _codecs;
    }

    public String getErrors() {
        return (_internal.getErrors() +
                _fix.getErrors() +
                _binary.getErrors() +
                _codecs.getErrors() +
                _clientCodecs.getErrors() +
                _exchangeCodecs.getErrors() +
                _binaryCodecs.getErrors());
    }

    public CodecModel getExchangeCodecs() {
        return _exchangeCodecs;
    }

    public FactoryModel getFactoryModel() {
        return _factory;
    }

    public FixModels getFix() {
        return _fix;
    }

    public InternalModel getInternal() {
        return _internal;
    }

    public boolean verify() {

        boolean verified = false;

        _internal.clearErrors();
        _fix.clearErrors();
        _binary.clearErrors();
        _codecs.clearErrors();
        _clientCodecs.clearErrors();
        _exchangeCodecs.clearErrors();
        _binaryCodecs.clearErrors();

        if ( _internal.verify() ) {
            if ( _fix.verify( _internal ) ) {
                if ( _binary.verify( _internal ) ) {
                    if ( _codecs.verify( _internal, _fix ) ) {
                        if ( _clientCodecs.verify( _internal, _fix ) ) {
                            if ( _exchangeCodecs.verify( _internal, _fix ) ) {
                                if ( _binaryCodecs.verify( _internal, _binary ) ) {
                                    verified = true;
                                }
                            }
                        }
                    }
                }
            }
        }

        // TODO implement proper versioning of internal / fix and codec models

        return verified;
    }

}
