package com.filefilter.model;

import lombok.Data;
import java.util.List;

/**
 * Represents a single validation rule for a column
 * Supports: notEmpty, valueInList, and regex validation
 */
@Data
public class ValidationRule {

    /**
     * Column index (1-based)
     */
    private Integer column;

    /**
     * If true, the column value must not be empty/null
     * Nullable - if null, no empty check is performed
     */
    private Boolean notEmpty;

    /**
     * List of acceptable values for this column
     * Nullable - if null, no value list check is performed
     */
    private List<String> valueInList;

    /**
     * Regex pattern that the column value must match
     * Nullable - if null, no regex check is performed
     */
    private String regex;
}
