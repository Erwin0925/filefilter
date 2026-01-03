package com.filefilter.model;

import lombok.Data;
import java.util.List;

/**
 * Main configuration class that maps to filter-config.yaml
 */
@Data
public class FilterConfig {

    /**
     * Input file path (relative to resources/sourcefile/)
     * Example: "SampleData.csv"
     */
    private String inputFile;

    /**
     * File type: CSV, EXCEL, TXT (case-insensitive)
     */
    private String fileType;

    /**
     * Delimiter for TXT files (e.g., ",", "|", "/")
     * Only used when fileType is TXT
     */
    private String delimiter = ",";

    /**
     * File encoding (default: UTF-8)
     */
    private String encoding = "UTF-8";

    /**
     * Number of header lines to skip
     * Default: 0
     */
    private Integer skipHeaderLines = 0;

    /**
     * Expected total number of columns
     * Nullable - if null, no column count check is performed
     */
    private Integer expectedTotalColumn;

    /**
     * List of validation rules
     */
    private List<ValidationRule> validations;

    /**
     * Output configuration
     */
    private OutputConfig output;
}
