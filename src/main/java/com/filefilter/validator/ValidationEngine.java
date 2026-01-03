package com.filefilter.validator;

import com.filefilter.config.FilterConfig;
import com.filefilter.config.ValidationRule;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Validation engine that applies validation rules to data rows
 * Supports: notEmpty, valueInList, and regex validation
 */
@Slf4j
public class ValidationEngine {

    private final FilterConfig config;

    public ValidationEngine(FilterConfig config) {
        this.config = config;
    }

    /**
     * Validate a row of data against all configured validation rules
     * Uses AND logic - ALL rules must pass for the row to be valid
     *
     * @param rowData Array of column values
     * @return true if all validations pass, false otherwise
     */
    public boolean validate(String[] rowData) {
        // Check total column count if specified
        if (config.getExpectedTotalColumn() != null) {
            if (rowData.length != config.getExpectedTotalColumn()) {
                log.debug("Column count mismatch: expected={}, actual={}",
                        config.getExpectedTotalColumn(), rowData.length);
                return false;
            }
        }

        // Apply all validation rules (AND logic)
        if (config.getValidations() != null) {
            for (ValidationRule rule : config.getValidations()) {
                if (!validateRule(rowData, rule)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Validate a single rule against the row data
     *
     * @param rowData Array of column values
     * @param rule Validation rule to apply
     * @return true if rule passes, false otherwise
     */
    private boolean validateRule(String[] rowData, ValidationRule rule) {
        Integer columnIndex = rule.getColumn();

        // Validate column index
        if (columnIndex == null || columnIndex < 1 || columnIndex > rowData.length) {
            log.warn("Invalid column index: {}", columnIndex);
            return false;
        }

        // Get column value (convert from 1-based to 0-based index)
        String columnValue = rowData[columnIndex - 1];

        // Apply notEmpty check
        if (rule.getNotEmpty() != null && rule.getNotEmpty()) {
            if (columnValue == null || columnValue.trim().isEmpty()) {
                log.debug("Column {} failed notEmpty check", columnIndex);
                return false;
            }
        }

        // Apply valueInList check
        if (rule.getValueInList() != null && !rule.getValueInList().isEmpty()) {
            if (!rule.getValueInList().contains(columnValue)) {
                log.debug("Column {} failed valueInList check: value='{}', expectedValues={}",
                        columnIndex, columnValue, rule.getValueInList());
                return false;
            }
        }

        // Apply regex check
        if (rule.getRegex() != null && !rule.getRegex().isEmpty()) {
            try {
                Pattern pattern = Pattern.compile(rule.getRegex());
                if (!pattern.matcher(columnValue != null ? columnValue : "").matches()) {
                    log.debug("Column {} failed regex check: value='{}', pattern='{}'",
                            columnIndex, columnValue, rule.getRegex());
                    return false;
                }
            } catch (PatternSyntaxException e) {
                log.error("Invalid regex pattern for column {}: {}", columnIndex, rule.getRegex(), e);
                return false;
            }
        }

        return true;
    }
}
