package com.filefilter.processor;

import com.filefilter.processor.base.BaseProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel file processor with streaming support
 * Extends BaseProcessor to inherit automatic logging and exception handling
 * Uses Apache POI Streaming API for large file processing
 */
@Slf4j
public class ExcelFileProcessor extends BaseProcessor {

    @Override
    protected String getProcessorName() {
        return "excelParser";
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

        // Create streaming workbooks for output
        SXSSFWorkbook validWorkbook = new SXSSFWorkbook(100); // Keep 100 rows in memory
        SXSSFWorkbook rejectedWorkbook = config.getOutput().getNeedRejectedData()
                ? new SXSSFWorkbook(100)
                : null;

        try (Workbook inputWorkbook = new XSSFWorkbook(inputStream)) {

            Sheet inputSheet = inputWorkbook.getSheetAt(0);
            Sheet validSheet = validWorkbook.createSheet("FilteredData");
            Sheet rejectedSheet = rejectedWorkbook != null ? rejectedWorkbook.createSheet("RejectedData") : null;

            int validRowNum = 0;
            int rejectedRowNum = 0;

            // Process each row
            for (Row inputRow : inputSheet) {
                int rowIndex = inputRow.getRowNum();

                // Handle header rows
                if (rowIndex < config.getSkipHeaderLines()) {
                    copyRow(inputRow, validSheet.createRow(validRowNum++));
                    if (rejectedSheet != null) {
                        copyRow(inputRow, rejectedSheet.createRow(rejectedRowNum++));
                    }
                    continue;
                }

                // Convert row to string array for validation
                String[] rowData = rowToStringArray(inputRow);
                totalRecords++;

                if (validationEngine.validate(rowData)) {
                    copyRow(inputRow, validSheet.createRow(validRowNum++));
                    successRecords++;
                } else {
                    if (rejectedSheet != null) {
                        copyRow(inputRow, rejectedSheet.createRow(rejectedRowNum++));
                    }
                    rejectRecords++;
                }
            }

            // Write valid workbook
            try (FileOutputStream validOut = new FileOutputStream(outputFilePath)) {
                validWorkbook.write(validOut);
                log.info("Output written to: {}", outputFilePath);
            }

            // Write rejected workbook if needed
            if (rejectedWorkbook != null) {
                try (FileOutputStream rejectedOut = new FileOutputStream(rejectedFilePath)) {
                    rejectedWorkbook.write(rejectedOut);
                    log.info("Rejected data written to: {}", rejectedFilePath);
                }
            }

        } finally {
            validWorkbook.close();
            if (rejectedWorkbook != null) {
                rejectedWorkbook.close();
            }
        }
    }

    /**
     * Convert Excel row to string array
     */
    private String[] rowToStringArray(Row row) {
        List<String> cells = new ArrayList<>();
        int lastCell = row.getLastCellNum();

        for (int i = 0; i < lastCell; i++) {
            Cell cell = row.getCell(i);
            cells.add(getCellValueAsString(cell));
        }

        return cells.toArray(new String[0]);
    }

    /**
     * Get cell value as string
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getDateCellValue().toString();
                } else {
                    yield String.valueOf(cell.getNumericCellValue());
                }
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }

    /**
     * Copy row from source to destination
     */
    private void copyRow(Row sourceRow, Row destRow) {
        for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
            Cell sourceCell = sourceRow.getCell(i);
            Cell destCell = destRow.createCell(i);

            if (sourceCell != null) {
                switch (sourceCell.getCellType()) {
                    case STRING -> destCell.setCellValue(sourceCell.getStringCellValue());
                    case NUMERIC -> destCell.setCellValue(sourceCell.getNumericCellValue());
                    case BOOLEAN -> destCell.setCellValue(sourceCell.getBooleanCellValue());
                    case FORMULA -> destCell.setCellFormula(sourceCell.getCellFormula());
                    default -> destCell.setCellValue("");
                }
            }
        }
    }
}
