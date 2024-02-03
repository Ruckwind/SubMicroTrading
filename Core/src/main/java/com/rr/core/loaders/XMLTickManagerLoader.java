package com.rr.core.loaders;

import com.rr.core.component.SMTComponent;
import com.rr.core.component.SMTSingleComponentLoader;
import com.rr.core.lang.Constants;
import com.rr.core.lang.RTStartupException;
import com.rr.core.lang.ViewString;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.ExchangeCode;
import com.rr.core.model.TickBand;
import com.rr.core.model.TickManager;
import com.rr.core.model.TickScale;
import com.rr.core.utils.FileUtils;
import com.rr.model.xml.XMLException;
import com.rr.model.xml.XMLHelper;
import com.rr.model.xml.XMLMissingException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings( "unchecked" )
public class XMLTickManagerLoader implements SMTSingleComponentLoader {

    private static final Logger _log = LoggerFactory.create( XMLTickManagerLoader.class );

    private String _tickScaleFile = "./core/tickScales.xml";

    private XMLHelper _helper;

    @Override public SMTComponent create( String id ) {

        TickManager mgr = new TickManager( id );

        String[] files = _tickScaleFile.split( "," );

        for ( String file : files ) {
            load( mgr, file );
        }

        return mgr;

    }

    public String getTickScaleFile() {
        return _tickScaleFile;
    }

    public void setTickScaleFile( final String tickScaleFile ) {
        _tickScaleFile = tickScaleFile;
    }

    public void setTickScaleFiles( String file ) {
        _tickScaleFile = file;
    }

    private void addTickBand( ArrayList<TickBand> bands, final List<Node> tickRanges, final Element node ) throws XMLException {
        double lowerBand = _helper.getAttrDouble( node, "lowerBound", true, Constants.UNSET_DOUBLE );
        double upperBand = _helper.getAttrDouble( node, "upperBound", true, Constants.UNSET_DOUBLE );
        double tickSize  = _helper.getAttrDouble( node, "tickSize", true, Constants.UNSET_DOUBLE );

        TickBand b = new TickBand( lowerBand, upperBand, tickSize );

        bands.add( b );
    }

    private String getID( Element node ) throws XMLMissingException {
        return _helper.getAttr( node, "id", true );
    }

    private String getMIC( Element node ) throws XMLMissingException {
        return _helper.getAttr( node, "operatingMIC", true );
    }

    private void load( final TickManager mgr, String fileName ) {

        if ( !FileUtils.isFile( fileName ) ) {
            _log.warn( "XMLTickManagerLoader skipping missing file " + fileName );
            return;
        }

        _helper = new XMLHelper( fileName );

        try {
            _helper.parse();

            {
                NodeList tickScales = _helper.getElements( "TickScales", false );
                int      items      = tickScales.getLength();
                for ( int i = 0; i < items; i++ ) {
                    final Node item = tickScales.item( i );
                    if ( item.getNodeType() == Node.ELEMENT_NODE ) {
                        loadTickScales( fileName, mgr, (Element) item, false );
                    }
                }
            }

            {
                NodeList tickScales = _helper.getElements( "UniversalTickScales", false );
                if ( tickScales != null ) {
                    int items = tickScales.getLength();
                    for ( int i = 0; i < items; i++ ) {
                        final Node item = tickScales.item( i );
                        if ( item.getNodeType() == Node.ELEMENT_NODE ) {
                            loadTickScales( fileName, mgr, (Element) item, true );
                        }
                    }
                }
            }

        } catch( Exception e ) {

            throw new RTStartupException( ", file=" + _tickScaleFile + " : " + e.getMessage(), e );
        }
    }

    private void loadTickScale( final TickManager mgr, final ExchangeCode mic, Element node, final boolean isUniversal ) throws XMLException {
        String id = getID( node );

        ArrayList<TickBand> bands = new ArrayList<>();

        List<Node> tickRanges = _helper.getChildElements( node, "TickRange", false );

        if ( tickRanges.size() > 0 ) {
            for ( Node tickRange : tickRanges ) {
                if ( tickRange.getNodeType() == Node.ELEMENT_NODE ) {
                    addTickBand( bands, tickRanges, (Element) tickRange );
                }
            }

            final TickBand[] arr = bands.toArray( new TickBand[ bands.size() ] );

            TickScale ts = new TickScale( new ViewString( id ), arr );

            if ( isUniversal ) {
                mgr.addUniversalTickType( Integer.parseInt( id ), ts );
            } else {
                mgr.addTickType( mic, ts );
            }
        }
    }

    /**
     * <Root>
     * <TickScales operatingMIC="XCME">
     * <TickScale id="1">
     * <TickRange lowerBound=""        upperBound="-500"   tickSize="10"/>
     * <TickRange lowerBound="-500"    upperBound="500"    tickSize="5"/>
     * <TickRange lowerBound="500"     upperBound=""       tickSize="10"/>
     * </TickScale>
     * </TickScales>
     *
     * <UniversalTickScales>
     * <TickScale id="1">
     * <TickRange lowerBound=""        upperBound="-500"   tickSize="10"/>
     * <TickRange lowerBound="-500"    upperBound="500"    tickSize="5"/>
     * <TickRange lowerBound="500"     upperBound=""       tickSize="10"/>
     * </TickScale>
     * </UniversalTickScales>
     * </Root>
     *
     * @throws XMLMissingException
     */
    private void loadTickScales( final String fileName, final TickManager mgr, Element node, boolean isUniversal ) throws XMLException {
        List<Node> tickScales = _helper.getChildElements( node, "TickScale", true );

        if ( isUniversal ) {
            for ( Node tickScale : tickScales ) {
                if ( tickScale.getNodeType() == Node.ELEMENT_NODE ) {
                    loadTickScale( mgr, null, (Element) tickScale, isUniversal );
                }
            }

            _log.info( "XMLTickManagerLoader() loaded " + tickScales.size() + " universal tick scales from " + fileName );

        } else {
            String       micCode = getMIC( node );
            ExchangeCode mic     = ExchangeCode.valueOf( micCode );

            for ( Node tickScale : tickScales ) {
                if ( tickScale.getNodeType() == Node.ELEMENT_NODE ) {
                    loadTickScale( mgr, mic, (Element) tickScale, isUniversal );
                }
            }

            _log.info( "XMLTickManagerLoader() loaded " + tickScales.size() + " exchange tick scales from " + fileName );
        }
    }
}
