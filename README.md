# File Filter Application

A Java application for filtering and validating data files (CSV, Excel, TXT) based on configurable validation rules.

## Features

- **Multiple file format support**: CSV, Excel, and TXT with custom delimiters
- **Flexible validation rules**:
  - Not empty validation
  - Value-in-list validation (enum-like checking)
  - Regex pattern matching
  - Column count validation
- **High performance**:
  - Streaming processing for large files (up to 2M+ rows)
  - Buffered I/O with 64KB buffers
  - Apache POI streaming for Excel files
  - Memory-efficient design
- **Dual output**: Separate files for valid and rejected records
- **Console logging**: Real-time logging with performance metrics
- **Clean architecture**: Template Method Pattern, Factory Pattern, separation of concerns

## Prerequisites

- **Java 21** or higher
- **Maven 3.8+** (for building)

## Quick Start

### 1. Add Your Input File

Place your input file in `src/main/resources/sourcefile/`:

```
src/main/resources/sourcefile/data.csv
```

### 2. Configure Validation Rules

Edit `src/main/resources/filter-config.yaml`:

```yaml
inputFile: "data.csv"
fileType: CSV
skipHeaderLines: 1
expectedTotalColumn: 10

validations:
  - column: 1
    notEmpty: true
    regex: "^99\\d{5}$"

  - column: 3
    valueInList: ["101", "110", "040"]

output:
  outputFileName: "FilteredData"
  needRejectedData: true
  rejectedFileName: "RejectedData"
```

### 3. Run the Application

**Option 1: Run from IDE (Recommended)**
- Open the project in IntelliJ IDEA / Eclipse
- Right-click `Main.java` → Run 'Main.main()'

**Option 2: Run with Maven**
```bash
mvn clean compile exec:java -Dexec.mainClass="com.filefilter.Main"
```

**Option 3: Build and Run JAR**
```bash
mvn clean package
java -jar target/filefilter-1.0.0.jar
```

### 4. Check Results

Output files will be generated in the `output/` directory:
- `output/FilteredData.csv` - Valid records
- `output/RejectedData.csv` - Rejected records (if enabled)

Logs will be displayed in the **console output**.

## Configuration Guide

### File Type Configuration

```yaml
# CSV Files
fileType: CSV

# Excel Files (supports .xlsx)
fileType: EXCEL

# TXT Files with custom delimiter
fileType: TXT
delimiter: "|"  # Can be: ",", "|", "/", "\t", etc.
```

### Validation Rules

All validation rules use **AND logic** - a row must pass ALL rules to be considered valid.

#### 1. Not Empty Validation

```yaml
validations:
  - column: 5
    notEmpty: true
```

#### 2. Value-in-List Validation

```yaml
validations:
  - column: 3
    valueInList: ["101", "110", "040"]
```

#### 3. Regex Validation

```yaml
validations:
  # ID must start with "99" and have total length of 7
  - column: 1
    regex: "^99\\d{5}$"

  # Code must be 12-13 characters long
  - column: 7
    regex: ".{12,13}"

  # Email validation
  - column: 5
    regex: "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
```

#### 4. Combined Validation

```yaml
validations:
  - column: 1
    notEmpty: true           # Must not be empty
    valueInList: ["A", "B"]  # AND must be "A" or "B"
    regex: "^[A-Z]$"        # AND must be single uppercase letter
```

#### 5. Column Count Validation

```yaml
expectedTotalColumn: 20  # Reject rows that don't have exactly 20 columns
```

### Output Configuration

```yaml
output:
  outputFileName: "FilteredData"     # Name without extension
  needRejectedData: true             # Set to false to skip rejected file
  rejectedFileName: "RejectedData"   # Name for rejected records file
```