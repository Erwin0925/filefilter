package com.filefilter.processor;

import com.filefilter.model.FilterConfig;
import com.filefilter.processor.base.BaseProcessor;
import com.filefilter.processor.base.ProcessingResult;
import com.filefilter.validator.ValidationEngine;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.ICSVParser;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * CSV file processor
 * Extends BaseProcessor to inherit automatic logging and exception handling
 */
@Slf4j
public class CsvFileProcessor extends BaseProcessor {

    @Override
    protected String getProcessorName() {
        return "csvParser";
    }

    @Override
    protected ProcessingResult doProcess(FilterConfig config, ValidationEngine validationEngine) throws Exception {
        // Local variables for thread-safe counter tracking
        long totalRecords = 0;
        long successRecords = 0;
        long rejectRecords = 0;

        // Get input stream from resources
        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("sourcefile/" + config.getInputFile());

        if (inputStream == null) {
            throw new FileNotFoundException("Input file not found: sourcefile/" + config.getInputFile());
        }

        // Create output directory if not exists
        Files.createDirectories(Paths.get("output"));

        // Prepare output file paths (auto-generated from input filename)
        String outputFilePath = getFilteredOutputPath(config);
        String rejectedFilePath = getRejectedOutputPath(config);

        // Open CSV reader and writers
        // Configure reader to NOT treat backslash as escape character (preserve literal backslashes)
        try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(inputStream, config.getEncoding()))
                     .withCSVParser(new com.opencsv.CSVParserBuilder()
                             .withSeparator(ICSVParser.DEFAULT_SEPARATOR)
                             .withQuoteChar(ICSVParser.DEFAULT_QUOTE_CHARACTER)
                             .withEscapeChar(ICSVParser.NULL_CHARACTER)  // No escape character - backslash is literal
                             .build())
                     .build();
             CSVWriter validWriter = new CSVWriter(new FileWriter(outputFilePath),
                     CSVWriter.DEFAULT_SEPARATOR,
                     CSVWriter.DEFAULT_QUOTE_CHARACTER,
                     CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                     CSVWriter.DEFAULT_LINE_END);
             CSVWriter rejectedWriter = config.getOutput().getNeedRejectedData()
                     ? new CSVWriter(new FileWriter(rejectedFilePath),
                     CSVWriter.DEFAULT_SEPARATOR,
                     CSVWriter.DEFAULT_QUOTE_CHARACTER,
                     CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                     CSVWriter.DEFAULT_LINE_END)
                     : null) {

            // Skip header lines
            for (int i = 0; i < config.getSkipHeaderLines(); i++) {
                String[] headerLine = reader.readNext();
                if (headerLine != null) {
                    // Write headers to both output files
                    validWriter.writeNext(headerLine);
                    if (rejectedWriter != null) {
                        rejectedWriter.writeNext(headerLine);
                    }
                }
            }

            // Process data rows
            String[] row;
            while ((row = reader.readNext()) != null) {
                totalRecords++;

                if (validationEngine.validate(row)) {
                    validWriter.writeNext(row);
                    successRecords++;
                } else {
                    if (rejectedWriter != null) {
                        rejectedWriter.writeNext(row);
                    }
                    rejectRecords++;
                }
            }

            log.info("Output written to: {}", outputFilePath);
            if (rejectedWriter != null) {
                log.info("Rejected data written to: {}", rejectedFilePath);
            }
        }

        // Return immutable result with statistics
        return ProcessingResult.builder()
                .totalRecords(totalRecords)
                .successRecords(successRecords)
                .rejectRecords(rejectRecords)
                .build();
    }
}
