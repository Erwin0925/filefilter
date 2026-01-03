package com.filefilter.util;

/**
 * Utility class for file name operations
 * Provides methods to extract file name parts and generate output file names
 */
public class FileNameUtil {

    private FileNameUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Get file name without extension
     * Example: "SampleData.csv" -> "data"
     */
    public static String getFileNameWithoutExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(0, lastDotIndex) : fileName;
    }

    /**
     * Get file extension from filename
     * Example: "SampleData.csv" -> ".csv"
     */
    public static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex) : "";
    }

    /**
     * Generate filtered output file name
     * Example: "SampleData.csv" -> "data_Filtered.csv"
     */
    public static String getFilteredFileName(String inputFileName) {
        String baseName = getFileNameWithoutExtension(inputFileName);
        String extension = getFileExtension(inputFileName);
        return baseName + "_Filtered" + extension;
    }

    /**
     * Generate rejected output file name
     * Example: "SampleData.csv" -> "data_Rejected.csv"
     */
    public static String getRejectedFileName(String inputFileName) {
        String baseName = getFileNameWithoutExtension(inputFileName);
        String extension = getFileExtension(inputFileName);
        return baseName + "_Rejected" + extension;
    }

    /**
     * Generate filtered output file path with output directory
     * Example: "SampleData.csv" -> "output/data_Filtered.csv"
     */
    public static String getFilteredFilePath(String inputFileName) {
        return "output/" + getFilteredFileName(inputFileName);
    }

    /**
     * Generate rejected output file path with output directory
     * Example: "SampleData.csv" -> "output/data_Rejected.csv"
     */
    public static String getRejectedFilePath(String inputFileName) {
        return "output/" + getRejectedFileName(inputFileName);
    }
}
