package com.rr.model.generated.fix.codec;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.core.codec.CodecFactory;
import com.rr.model.generated.codec.*;

public class CodecFactoryPopulator {

    public void register( CodecFactory factory ) {
        factory.register( CodecId.MD44, 
                          MD44Encoder.class, 
                          MD44Decoder.class, 
                          MD44Decoder.class ); 
        factory.register( CodecId.MD50, 
                          MD50Encoder.class, 
                          MD50Decoder.class, 
                          MD50Decoder.class ); 
        factory.register( CodecId.Standard44, 
                          Standard44Encoder.class, 
                          Standard44DecoderOMS.class, 
                          Standard44DecoderFull.class ); 
        factory.register( CodecId.Standard50, 
                          Standard50Encoder.class, 
                          Standard50DecoderOMS.class, 
                          Standard50DecoderFull.class ); 
        factory.register( CodecId.Standard42, 
                          Standard42Encoder.class, 
                          Standard42DecoderOMS.class, 
                          Standard42DecoderFull.class ); 
        factory.register( CodecId.SampleBroker1Fix44, 
                          SampleBroker1Fix44Encoder.class, 
                          SampleBroker1Fix44DecoderOMS.class, 
                          SampleBroker1Fix44DecoderFull.class ); 
        factory.register( CodecId.StratInternalFix44, 
                          StratInternalFix44Encoder.class, 
                          StratInternalFix44DecoderOMS.class, 
                          StratInternalFix44DecoderFull.class ); 
        factory.register( CodecId.DropCopy44, 
                          DropCopy44Encoder.class, 
                          DropCopy44DecoderOMS.class, 
                          DropCopy44DecoderFull.class ); 
        factory.register( CodecId.ClientX_44, 
                          ClientX_44Encoder.class, 
                          ClientX_44DecoderOMS.class, 
                          ClientX_44DecoderFull.class ); 
        factory.register( CodecId.CHIX, 
                          CHIXEncoder.class, 
                          CHIXDecoderOMS.class, 
                          CHIXDecoderFull.class ); 
        factory.register( CodecId.MDBSE, 
                          MDBSEEncoder.class, 
                          MDBSEDecoder.class, 
                          MDBSEDecoder.class ); 
        factory.register( CodecId.CMEMD, 
                          CMEMDEncoder.class, 
                          CMEMDDecoder.class, 
                          CMEMDDecoder.class ); 
        factory.register( CodecId.CME, 
                          CMEEncoder.class, 
                          CMEDecoderOMS.class, 
                          CMEDecoderFull.class ); 
        factory.register( CodecId.UTPEuronextCash, 
                          UTPEuronextCashEncoder.class, 
                          UTPEuronextCashDecoder.class, 
                          UTPEuronextCashDecoder.class ); 
        factory.register( CodecId.MilleniumLSE, 
                          MilleniumLSEEncoder.class, 
                          MilleniumLSEDecoder.class, 
                          MilleniumLSEDecoder.class ); 
        factory.register( CodecId.ItchLSE, 
                          ItchLSEEncoder.class, 
                          ItchLSEDecoder.class, 
                          ItchLSEDecoder.class ); 
        factory.register( CodecId.ETIEurexHFT, 
                          ETIEurexHFTEncoder.class, 
                          ETIEurexHFTDecoder.class, 
                          ETIEurexHFTDecoder.class ); 
        factory.register( CodecId.ETIEurexLFT, 
                          ETIEurexLFTEncoder.class, 
                          ETIEurexLFTDecoder.class, 
                          ETIEurexLFTDecoder.class ); 
        factory.register( CodecId.ETIBSE, 
                          ETIBSEEncoder.class, 
                          ETIBSEDecoder.class, 
                          ETIBSEDecoder.class ); 
        factory.register( CodecId.CMESimpleBinary, 
                          CMESimpleBinaryEncoder.class, 
                          CMESimpleBinaryDecoder.class, 
                          CMESimpleBinaryDecoder.class ); 
        factory.register( CodecId.TCPPitchCHIX, 
                          TCPPitchCHIXEncoder.class, 
                          TCPPitchCHIXDecoder.class, 
                          TCPPitchCHIXDecoder.class ); 
        factory.register( CodecId.TCPHistoricPitchCHIX, 
                          TCPHistoricPitchCHIXEncoder.class, 
                          TCPHistoricPitchCHIXDecoder.class, 
                          TCPHistoricPitchCHIXDecoder.class ); 
        factory.register( CodecId.SOUP2, 
                          SOUP2Encoder.class, 
                          SOUP2Decoder.class, 
                          SOUP2Decoder.class ); 
        factory.register( CodecId.SoupBin3, 
                          SoupBin3Encoder.class, 
                          SoupBin3Decoder.class, 
                          SoupBin3Decoder.class ); 
    }
}
