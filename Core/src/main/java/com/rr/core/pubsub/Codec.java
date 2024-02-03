package com.rr.core.pubsub;

import com.rr.core.lang.ReusableString;

public interface Codec {

    <M> M decode( ReusableString encodedMsg ) throws Exception;

    /**
     * encode the object to the destination ReusableString ... can be binary format
     *
     * @param obj
     * @param dest -> destination buffer will grow as needed, you should presize for efficiency
     */
    void encode( Object obj, ReusableString dest ) throws Exception;

    /**
     * set encoding to encode to single line ie without '\n'
     */
    void setEncodeNewLineChar( String newLine );

    void setExcludeNullFields( boolean excludeNullFields );

    void setVerboseSpacing( boolean isEnabled );
}
