package com.rr.core.model;

public interface Identifiable {

    /**
     * uniquely identifies the object (doesnt mean id() cant change over time, if so then isSame must be overridden cater for that
     */
    String id();

    default boolean isSame( Identifiable that ) {
        return this == that || (that != null && that.id() != null && that.id().equals( this.id() ));
    }
}
