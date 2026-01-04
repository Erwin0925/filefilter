package com.filefilter.processor.base;

import lombok.Builder;
import lombok.Getter;

/**
 * Immutable result object containing processing statistics
 * Thread-safe by design (all fields are final and immutable)
 *
 * This class is returned by processors to communicate processing results
 * instead of using mutable instance fields, ensuring thread safety.
 */
@Getter
@Builder
public class ProcessingResult {
    private final long totalRecords;
    private final long successRecords;
    private final long rejectRecords;
    private final long processingTimeMs;
    private final boolean success;
    private final Exception error;
}
