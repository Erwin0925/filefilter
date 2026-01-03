package com.filefilter.model;

import lombok.Data;

/**
 * Configuration for output file settings
 */
@Data
public class OutputConfig {

    /**
     * Whether to write rejected records to a separate file
     * Default: true
     */
    private Boolean needRejectedData = true;
}
