package com.filefilter.processor;

import com.filefilter.processor.base.BaseProcessor;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
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
    protected void doProcess() throws Exception {
        // Get input stream from resources
        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("sourcefile/" + config.getInputFile());

        if (inputStream == null) {
            throw new FileNotFoundException("Input file not found: sourcefile/" + config.getInputFile());
        }

        // Create output directory if not exists
        Files.createDirectories(Paths.get("output"));

        // Prepare output file paths
        String outputFilePath = "output/" + config.getOutput().getOutputFileName() + ".csv";
        String rejectedFilePath = "output/" + config.getOutput().getRejectedFileName() + ".csv";

        // Open CSV reader and writers
        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream, config.getEncoding()));
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
    }
}
