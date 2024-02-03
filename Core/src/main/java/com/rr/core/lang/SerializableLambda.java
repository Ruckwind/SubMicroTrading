package com.rr.core.lang;

import java.io.Serializable;

/**
 * all lambda functions which need to be persisted must implement SerializableLambda or Serializable
 *
 * @WARNING MUST NOT BE USED IN CONCRETE CLASSES
 */
public interface SerializableLambda extends Serializable {
    // tag interface
}
