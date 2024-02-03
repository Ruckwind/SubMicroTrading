package com.rr.core.recovery;

/**
 * BackTest generates an EXPLICIT export snapshot that requires an EXPLICIT import in Prod
 * <p>
 * only top level components that implement ComponentExportClient   will participate in the BT to PROD export process
 *
 * @WARNING : ONLY EXPORT ITEMS THAT ARE COMPLETELY SELF CONTAINED
 * REFS TO INSTRUMENTS OR STRATEGY_INSTRUMENTS ARE FINE  (AS THEY ARE REFS)
 * ... LINKS TO STRATEGIES ARE NOT !!!!! YOU WILL CREATE DUPLICATES !!!
 */
public interface ComponentExportClient {

    /**
     * export specific data from backtest for delivery to prod
     * <p>
     * populate a map of kay value pairs with state to persist
     *
     * @param exportContainer the container with the
     * @WARNING DONT persist strategy objects or other persisted TOP level objects .. instead store the id()
     */
    void exportData( ExportContainer exportContainer );

    /**
     * import into PROD the data from backtest
     * <p>
     * function should load what was exported
     *
     * @param importContainer the import container with decoded values from backtest
     * @WARNING import occurs BEFORE init() so json decoder will create StratInstProxy's for strat instruments that dont exist yet
     * Components must ensure that before startWork is invoked that those proxies are replaced
     */
    void importData( ExportContainer importContainer, long btSnapshotTime );
}
