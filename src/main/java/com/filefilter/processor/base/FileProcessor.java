package com.filefilter.processor.base;

import com.filefilter.model.FilterConfig;

/**
 * Interface for file processors
 * All processors must implement this interface
 */
public interface FileProcessor {

    /**
     * Process the file - reads, validates, and writes filtered data
     *
     * @param config Filter configuration
     */
    void process(FilterConfig config);
}
