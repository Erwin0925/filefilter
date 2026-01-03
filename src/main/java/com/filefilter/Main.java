package com.filefilter;

import com.filefilter.config.FilterConfig;
import com.filefilter.factory.FileProcessorFactory;
import com.filefilter.processor.FileProcessor;
import com.filefilter.util.ConfigLoader;
import lombok.extern.slf4j.Slf4j;

/**
 * Main entry point for File Filter application
 * Right-click and run this class to execute the file filtering process
 */
@Slf4j
public class Main {

    private static final String DEFAULT_CONFIG = "filter-config.yaml";

    public static void main(String[] args) {
        log.info("=".repeat(60));
        log.info("File Filter Application Started");
        log.info("=".repeat(60));

        try {
            // Load configuration
            String configPath = args.length > 0 ? args[0] : DEFAULT_CONFIG;
            log.info("Loading configuration from: {}", configPath);
            FilterConfig config = ConfigLoader.load(configPath);

            // Validate configuration
            validateConfig(config);

            // Display configuration summary
            displayConfigSummary(config);

            // Create factory and get appropriate processor
            FileProcessorFactory factory = new FileProcessorFactory();
            FileProcessor processor = factory.getProcessor(config.getFileType());

            // Process the file (logging handled by BaseProcessor)
            processor.process(config);

            log.info("=".repeat(60));
            log.info("File Filter Application Completed Successfully");
            log.info("=".repeat(60));

        } catch (Exception e) {
            log.error("=".repeat(60));
            log.error("File Filter Application Failed");
            log.error("=".repeat(60));
            log.error("Error: {}", e.getMessage(), e);
            System.exit(1);
        }
    }

    /**
     * Validate configuration
     */
    private static void validateConfig(FilterConfig config) {
        if (config.getInputFile() == null || config.getInputFile().isEmpty()) {
            throw new IllegalArgumentException("Input file is required in configuration");
        }

        if (config.getFileType() == null || config.getFileType().isEmpty()) {
            throw new IllegalArgumentException("File type is required in configuration");
        }

        if (config.getOutput() == null) {
            throw new IllegalArgumentException("Output configuration is required");
        }

        if (config.getOutput().getOutputFileName() == null || config.getOutput().getOutputFileName().isEmpty()) {
            throw new IllegalArgumentException("Output file name is required in configuration");
        }

        if (config.getOutput().getNeedRejectedData() &&
                (config.getOutput().getRejectedFileName() == null || config.getOutput().getRejectedFileName().isEmpty())) {
            throw new IllegalArgumentException("Rejected file name is required when needRejectedData is true");
        }
    }

    /**
     * Display configuration summary
     */
    private static void displayConfigSummary(FilterConfig config) {
        log.info("-".repeat(60));
        log.info("Configuration Summary:");
        log.info("  Input File: sourcefile/{}", config.getInputFile());
        log.info("  File Type: {}", config.getFileType());
        log.info("  Encoding: {}", config.getEncoding());
        log.info("  Skip Header Lines: {}", config.getSkipHeaderLines());
        log.info("  Expected Columns: {}", config.getExpectedTotalColumn() != null ? config.getExpectedTotalColumn() : "No limit");
        log.info("  Validation Rules: {} rule(s)", config.getValidations() != null ? config.getValidations().size() : 0);
        log.info("  Output File: output/{}", getOutputFileName(config));
        log.info("  Rejected Data File: {}", config.getOutput().getNeedRejectedData()
                ? "output/" + getRejectedFileName(config)
                : "Disabled");
        log.info("-".repeat(60));
    }

    /**
     * Get output file name with extension
     */
    private static String getOutputFileName(FilterConfig config) {
        String extension = getFileExtension(config);
        return config.getOutput().getOutputFileName() + extension;
    }

    /**
     * Get rejected file name with extension
     */
    private static String getRejectedFileName(FilterConfig config) {
        String extension = getFileExtension(config);
        return config.getOutput().getRejectedFileName() + extension;
    }

    /**
     * Get file extension based on file type
     */
    private static String getFileExtension(FilterConfig config) {
        return switch (config.getFileTypeUpper()) {
            case "CSV" -> ".csv";
            case "EXCEL", "XLSX", "XLS" -> ".xlsx";
            case "TXT" -> ".txt";
            default -> "";
        };
    }
}
