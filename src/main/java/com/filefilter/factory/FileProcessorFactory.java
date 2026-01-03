package com.filefilter.factory;

import com.filefilter.processor.*;
import com.filefilter.processor.base.FileProcessor;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Factory for creating appropriate file processor based on file type
 * Uses Registry Pattern with Map for extensibility
 */
public class FileProcessorFactory {

    private final Map<String, Supplier<FileProcessor>> processors;

    /**
     * Constructor - builds processor registry from list at startup
     * Each entry defines its supported types and processor creator
     */
    public FileProcessorFactory() {
        // Define all processor registrations
        List<ProcessorRegistration> registrations = Arrays.asList(
                new ProcessorRegistration(new String[]{"CSV"}, CsvFileProcessor::new),
                new ProcessorRegistration(new String[]{"EXCEL", "XLSX", "XLS"}, ExcelFileProcessor::new),
                new ProcessorRegistration(new String[]{"TXT"}, TxtFileProcessor::new)
        );

        // Build map: fileType -> processor creator
        this.processors = registrations.stream()
                .flatMap(reg -> Arrays.stream(reg.types())
                        .map(type -> Map.entry(type, reg.creator())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Get processor based on file type
     *
     * @param fileType File type (e.g., "CSV", "EXCEL", "TXT")
     * @return Appropriate FileProcessor implementation
     * @throws IllegalArgumentException if file type is not supported
     */
    public FileProcessor getProcessor(String fileType) {
        if (fileType == null || fileType.isEmpty()) {
            throw new IllegalArgumentException("File type is required");
        }

        String fileTypeUpper = fileType.toUpperCase();
        Supplier<FileProcessor> processorCreator = processors.get(fileTypeUpper);

        if (processorCreator == null) {
            throw new IllegalArgumentException(
                    "Unsupported file type: " + fileType + ". Supported types: " + processors.keySet());
        }

        return processorCreator.get();
    }

    /**
     * Internal record to hold processor registration
     * Associates file types with their processor creator
     */
    private record ProcessorRegistration(
            String[] types,
            Supplier<FileProcessor> creator
    ) {}
}
