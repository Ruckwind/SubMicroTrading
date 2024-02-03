package com.rr.core.recovery;

import com.rr.core.component.SMTInitialisableComponent;

import java.util.Set;

/**
 * Writer to export data from BackTest that needs to be imported to Prod
 * <p>
 * Rather then export components and components which implement  ComponentExportClient  will have their export methods invoked
 * with data stored in a ExportContainer ... its this ExportContainer that is persisted for later import
 */
public interface ComponentExportWriter extends SMTInitialisableComponent {

    void exportSnapshot( SnapshotDefinition snapshot ) throws Exception;

    void setFilterClasses( Set<Class> filterClasses );
}
