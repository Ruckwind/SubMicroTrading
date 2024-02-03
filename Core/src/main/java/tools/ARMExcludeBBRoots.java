package tools;

import com.rr.core.lang.ZString;
import com.rr.core.model.ExchangeCode;
import com.rr.core.model.FutureExchangeSymbol;
import com.rr.core.utils.FileUtils;

import java.io.BufferedWriter;
import java.util.HashSet;
import java.util.Set;

public class ARMExcludeBBRoots {

    private static final String DEF_FILE = "armBBRootFilters.csv";

    public static void main( String[] args ) throws Exception {
        Set<ExchangeCode> out = new HashSet<>();

        out.add( ExchangeCode.XCME );
        out.add( ExchangeCode.XCBT );
        out.add( ExchangeCode.XNYM );
        out.add( ExchangeCode.XCEC );

        final BufferedWriter writer = FileUtils.bufFileWriter( DEF_FILE );

        writer.write( "MIC,BBROOT" );
        writer.newLine();

        for ( ExchangeCode outMic : out ) {
            String outList = "";

            for ( FutureExchangeSymbol s : FutureExchangeSymbol.values() ) {
                final Set<ExchangeCode> mics = s.getValidMICs();

                boolean filter = false;

                if ( mics.contains( outMic ) ) {
                    filter = true;
                }

                if ( filter ) {
                    ZString bbRootSym = s.getBbRootSym();
                    writer.write( outMic.getMIC().toString() + "," + bbRootSym );
                    writer.newLine();
                }
            }
        }

        writer.close();
    }
}
