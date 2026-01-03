package com.filefilter.processor;

import com.filefilter.processor.base.BaseProcessor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * TXT file processor with custom delimiter support
 * Extends BaseProcessor to inherit automatic logging and exception handling
 * Supports custom delimiters like ",", "|", "/", etc.
 */
@Slf4j
public class TxtFileProcessor extends BaseProcessor {

    @Override
    protected String getProcessorName() {
        return "txtParser";
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

        // Prepare output file paths (auto-generated from input filename)
        String outputFilePath = getFilteredOutputPath();
        String rejectedFilePath = getRejectedOutputPath();

        // Get delimiter (escape special regex characters)
        String delimiter = config.getDelimiter();
        String delimiterRegex = java.util.regex.Pattern.quote(delimiter);

        // Open readers and writers with large buffers for performance
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, config.getEncoding()), 65536);
             BufferedWriter validWriter = new BufferedWriter(
                     new FileWriter(outputFilePath), 65536);
             BufferedWriter rejectedWriter = config.getOutput().getNeedRejectedData()
                     ? new BufferedWriter(new FileWriter(rejectedFilePath), 65536)
                     : null) {

            // Skip header lines
            for (int i = 0; i < config.getSkipHeaderLines(); i++) {
                String headerLine = reader.readLine();
                if (headerLine != null) {
                    validWriter.write(headerLine);
                    validWriter.newLine();
                    if (rejectedWriter != null) {
                        rejectedWriter.write(headerLine);
                        rejectedWriter.newLine();
                    }
                }
            }

            // Process data rows
            String line;
            while ((line = reader.readLine()) != null) {
                totalRecords++;

                // Split by delimiter
                String[] row = line.split(delimiterRegex, -1); // -1 to preserve trailing empty strings

                if (validationEngine.validate(row)) {
                    validWriter.write(line);
                    validWriter.newLine();
                    successRecords++;
                } else {
                    if (rejectedWriter != null) {
                        rejectedWriter.write(line);
                        rejectedWriter.newLine();
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
