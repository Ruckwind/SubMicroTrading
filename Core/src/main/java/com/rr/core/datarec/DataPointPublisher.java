package com.rr.core.datarec;

import com.rr.core.component.SMTInitialisableComponent;

public interface DataPointPublisher extends SMTInitialisableComponent {

    void close();

    void flush();

    DataPointPublisher getNextPublisher();

    void setNextPublisher( DataPointPublisher nextInChain );

    /**
     * publish the datapoint as it is .. assume time already stamped
     *
     * @param dp
     */
    void publish( DataPoint dp );

    /**
     * set time in datapoint then publish
     *
     * @param dp
     */
    void stampAndPublish( DataPoint dp );
}
