package com.rr.core.lang;

import com.rr.core.model.Identifiable;

/**
 * proxy for use in backtesting where each control thread will have its own instance
 *
 * @param <S>
 */
public interface BackTestProxy<S> extends Identifiable {

    S getProxy();

}
