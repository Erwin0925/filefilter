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

    /**
     * Custom filename for filtered output (without extension)
     * If null, defaults to {inputBaseName}_Filtered.{ext}
     * Example: "customFiltered" will become "customFiltered.csv"
     */
    private String filteredFileName;

    /**
     * Custom filename for rejected output (without extension)
     * If null, defaults to {inputBaseName}_Rejected.{ext}
     * Example: "customRejected" will become "customRejected.csv"
     */
    private String rejectedFileName;
}
