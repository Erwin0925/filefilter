package com.filefilter.base;

import com.filefilter.config.FilterConfig;
import com.filefilter.processor.FileProcessor;
import com.filefilter.validator.ValidationEngine;
import lombok.extern.slf4j.Slf4j;

/**
 * Base processor implementing Template Method Pattern
 * Provides automatic logging and exception handling for all processors
 *
 * Child classes only need to:
 * 1. Override doProcess() - implement business logic
 * 2. Override getProcessorName() - provide processor name for logging
 */
@Slf4j
public abstract class BaseProcessor implements FileProcessor {

    protected long totalRecords = 0;
    protected long successRecords = 0;
    protected long rejectRecords = 0;
    protected ValidationEngine validationEngine;
    protected FilterConfig config;

    /**
     * Template method - defines the processing algorithm skeleton
     * Child classes should NOT override this method
     *
     * This method automatically:
     * - Logs process start
     * - Executes child-specific logic (doProcess)
     * - Handles exceptions
     * - Logs process completion with statistics
     */
    @Override
    public final void process(FilterConfig config) {
        long startTime = System.currentTimeMillis();
        String processorName = getProcessorName();

        // Initialize config and validation engine
        this.config = config;
        this.validationEngine = new ValidationEngine(config);

        try {
            // Step 1: Log start
            logProcessStart(processorName);

            // Step 2: Execute child-specific logic (no exception handling needed in child)
            doProcess();

            // Step 3: Log completion with success
            logProcessComplete(processorName, startTime, true, null);

        } catch (Exception e) {
            // Step 3 (error case): Log completion with failure
            logProcessComplete(processorName, startTime, false, e);
            throw new RuntimeException("Processing failed in " + processorName, e);
        }
    }

    /**
     * Child classes MUST implement this method
     * Focus only on business logic - no logging, no exception handling needed
     * Config and ValidationEngine are available as protected fields
     *
     * @throws Exception if any error occurs during processing
     */
    protected abstract void doProcess() throws Exception;

    /**
     * Child classes MUST provide processor name for logging
     * Examples: "csvParser", "excelParser", "txtParser"
     *
     * @return Name of the processor
     */
    protected abstract String getProcessorName();

    /**
     * Log process start
     */
    private void logProcessStart(String processorName) {
        log.info("Starting {} processing...", processorName);
    }

    /**
     * Log process completion with statistics
     */
    private void logProcessComplete(String processorName, long startTime,
                                     boolean success, Exception error) {
        long endTime = System.currentTimeMillis();
        long timeTaken = endTime - startTime;

        if (success) {
            // Detailed logs
            log.info("Processing completed in {}ms", timeTaken);
            log.info("Total records: {}", totalRecords);
            log.info("Valid records: {}", successRecords);
            log.info("Rejected records: {}", rejectRecords);

            // Overall summary (condensed format)
            log.info("{}, {}ms, totalRecords={}, successRecord={}, rejectRecord={}, success=true",
                    processorName, timeTaken, totalRecords, successRecords, rejectRecords);
        } else {
            // Error logs
            log.error("Processing failed in {}ms", timeTaken);
            log.error("Error: {}", error != null ? error.getMessage() : "Unknown error");

            // Overall summary with failure
            log.error("{}, {}ms, totalRecords={}, successRecord={}, rejectRecord={}, success=false",
                    processorName, timeTaken, totalRecords, successRecords, rejectRecords);
        }
    }
}
