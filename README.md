# File Filter Application

A Java application for filtering and validating data files (CSV, Excel, TXT) based on configurable validation rules.

## Features

- **Multiple file format support**: CSV, Excel, and TXT with custom delimiters
- **Flexible validation rules**:
  - Not empty validation
  - Value-in-list validation (enum-like checking)
  - Regex pattern matching
  - Column count validation

## Prerequisites

- **Java 21** or higher
- **Maven 3.8+** (for building)

## Project Setup (Maven Configuration)

### Step 1: Create Another New .m2 Directory

Create a new directory for this project's Maven repository:

```
# Choose a location (examples):
# Option 1: In your user directory
C:/Users/YOUR_USERNAME/Own/.m2
```

### Step 2: Create settings.xml

Create a `settings.xml` file inside your new `.m2` directory with the following content:

**File:** `C:/Users/YOUR_USERNAME/Own/.m2/settings.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
          http://maven.apache.org/xsd/settings-1.0.0.xsd">

    <!-- Local repository path for this project -->
    <localRepository>C:/Users/YOUR_USERNAME/Own/.m2/repository</localRepository>

    <!-- Use Maven Central (default public repository) -->
    <mirrors>
        <mirror>
            <id>central</id>
            <name>Maven Central</name>
            <url>https://repo.maven.apache.org/maven2</url>
            <mirrorOf>central</mirrorOf>
        </mirror>
    </mirrors>

</settings>
```

**Important:** Replace `YOUR_USERNAME` with your actual username (e.g., `001535`)

### Step 3: Configure Maven to Use Custom settings.xml

**IntelliJ IDEA:**
1. File → Settings (Ctrl+Alt+S)
2. Build, Execution, Deployment → Build Tools → Maven
3. User settings file: `C:/Users/YOUR_USERNAME/Own/.m2/settings.xml` (check "Override")
4. Local repository: `C:/Users/YOUR_USERNAME/Own/.m2/repository`
5. Click Apply → OK

**Why This is Needed:**
- Isolates dependencies from other projects
- Prevents conflicts with company's default Maven settings

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
  needRejectedData: true  # Output filenames auto-generated from input
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