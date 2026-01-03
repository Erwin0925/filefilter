package com.filefilter.config;

import lombok.Data;

/**
 * Configuration for output file settings
 */
@Data
public class OutputConfig {

    /**
     * Base name for the output file (extension will be auto-appended)
     */
    private String outputFileName;

    /**
     * Whether to write rejected records to a separate file
     * Default: true
     */
    private Boolean needRejectedData = true;

    /**
     * Base name for the rejected data file (extension will be auto-appended)
     * Only used if needRejectedData is true
     */
    private String rejectedFileName;
}
