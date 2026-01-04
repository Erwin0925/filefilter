package com.filefilter.processor.base;

import com.filefilter.model.FilterConfig;
import com.filefilter.util.FileNameUtil;
import com.filefilter.validator.ValidationEngine;
import lombok.extern.slf4j.Slf4j;

/**
 * Base processor implementing Template Method Pattern
 * Provides automatic logging and exception handling for all processors
 *
 * Child classes only need to:
 * 1. Override doProcess() - implement business logic and return ProcessingResult
 * 2. Override getProcessorName() - provide processor name for logging
 */
@Slf4j
public abstract class BaseProcessor implements FileProcessor {

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

        // Create validation engine for this processing call
        ValidationEngine validationEngine = new ValidationEngine(config);

        try {
            // Step 1: Log start
            logProcessStart(processorName);

            // Step 2: Execute child-specific logic
            ProcessingResult result = doProcess(config, validationEngine);

            // Step 3: Add timing information and log completion
            long timeTaken = System.currentTimeMillis() - startTime;
            ProcessingResult finalResult = ProcessingResult.builder()
                    .totalRecords(result.getTotalRecords())
                    .successRecords(result.getSuccessRecords())
                    .rejectRecords(result.getRejectRecords())
                    .processingTimeMs(timeTaken)
                    .success(true)
                    .build();

            logProcessComplete(processorName, finalResult);

        } catch (Exception e) {
            // Step 3 (error case): Log completion with failure
            long timeTaken = System.currentTimeMillis() - startTime;
            ProcessingResult errorResult = ProcessingResult.builder()
                    .processingTimeMs(timeTaken)
                    .success(false)
                    .error(e)
                    .build();

            logProcessComplete(processorName, errorResult);
            throw new RuntimeException("Processing failed in " + processorName, e);
        }
    }

    /**
     * Child classes MUST implement this method
     * Focus only on business logic - no logging, no exception handling needed
     *
     * @param config Configuration for processing
     * @param validationEngine Engine for validating records
     * @return ProcessingResult containing statistics (totalRecords, successRecords, rejectRecords)
     * @throws Exception if any error occurs during processing
     */
    protected abstract ProcessingResult doProcess(FilterConfig config,
                                                   ValidationEngine validationEngine) throws Exception;

    /**
     * Child classes MUST provide processor name for logging
     * Examples: "csvParser", "excelParser", "txtParser"
     *
     * @return Name of the processor
     */
    protected abstract String getProcessorName();

    /**
     * Generate output filename from input filename
     * Example: "SampleData.csv" → "output/data_Filtered.csv"
     *
     * @param config Configuration containing input file name
     * @return Full path to filtered output file
     */
    protected String getFilteredOutputPath(FilterConfig config) {
        return FileNameUtil.getFilteredFilePath(config.getInputFile());
    }

    /**
     * Generate rejected filename from input filename
     * Example: "SampleData.csv" → "output/data_Rejected.csv"
     *
     * @param config Configuration containing input file name
     * @return Full path to rejected output file
     */
    protected String getRejectedOutputPath(FilterConfig config) {
        return FileNameUtil.getRejectedFilePath(config.getInputFile());
    }

    /**
     * Log process start
     */
    private void logProcessStart(String processorName) {
        log.info("Starting {} processing...", processorName);
    }

    /**
     * Log process completion with statistics
     */
    private void logProcessComplete(String processorName, ProcessingResult result) {
        if (result.isSuccess()) {
            // Detailed logs
            log.info("Processing completed in {}ms", result.getProcessingTimeMs());
            log.info("Total records: {}", result.getTotalRecords());
            log.info("Valid records: {}", result.getSuccessRecords());
            log.info("Rejected records: {}", result.getRejectRecords());

            // Overall summary (condensed format)
            log.info("{}, {}ms, totalRecords={}, successRecord={}, rejectRecord={}, success=true",
                    processorName, result.getProcessingTimeMs(), result.getTotalRecords(),
                    result.getSuccessRecords(), result.getRejectRecords());
        } else {
            // Error logs
            log.error("Processing failed in {}ms", result.getProcessingTimeMs());
            log.error("Error: {}", result.getError() != null ? result.getError().getMessage() : "Unknown error");

            // Overall summary with failure
            log.error("{}, {}ms, success=false", processorName, result.getProcessingTimeMs());
        }
    }
}
