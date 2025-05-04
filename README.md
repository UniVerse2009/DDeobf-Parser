**DDeobf**

DDeobf is a simple Java-based tool to deobfuscate source code files using a provided dictionary.
It automatically scans all files in a specified directory (and its subdirectories), replaces obfuscated terms based on a CSV mapping, and saves the modified files.

This tool is useful for cleaning up obfuscated Java source files, configuration files, or any other text-based formats.

**Features**

Recursive folder scan and processing

Fast search and replace using a simple dictionary

Lightweight and easy to use

No external dependencies


**Usage**

```java -jar DDeobf.jar <folderPath> <dictionaryFilePath> <threadCount>```

**Arguments:**

>`<folderPath>: Path to the root directory containing the files you want to deobfuscate.`

>`<dictionaryFilePath>: Path to the CSV file containing obfuscation mappings.`
Each line must be in the format:

obfuscatedName,deobfuscatedName

>`<threadCount>: Amount of threads to use.Opsional`


**Example**

Suppose you have a folder src/ containing obfuscated Java files and a dictionary dict.csv like:

a,myVariable
b,myMethod

**Run:**

```java -jar DDeobf.jar src/ dict.csv```

DDeobf will:

Scan all files inside src/

Replace every exact match of a with myVariable, and b with myMethod

Overwrite the original files with the modified content


**Build**

If you want to build it yourself:

```javac DDeobf.java```

```jar cfe DDeobf.jar DDeobf DDeobf.class```

**Notes**


The original files are directly overwritten. Make sure to back up your files if needed.

Supports only basic plain-text replacements.

