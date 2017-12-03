# google-search-archive-parser

A set of command line tools to work with your personal search data
exported from Google search.

These tools help you manage your search history so that you can remove
it from Google.

## Getting Started

### Get the data from Google

You can download your search history data from Google as follows:
- Go to https://takeout.google.com/settings/takeout
- Click on "Select None"
- Scroll down to "Searches" and enable it using the slider
- Select your favorite file type and delivery method
- Hit "Create Archive"

### Run the Tool

Once you have obtained your archive, extract it to a known location.
The tool will need the path to the extracted directory that contains
the JSON files.

Requirement: Java has to be installed in your system

- Download the latest [release](https://github.com/fedeoasi/google-search-archive-parser/releases/download/v0.1.0/google-search-archive-parser-assembly-0.1.0.jar
).
- To incrementally create a csv version of your queries (all-queries.csv):
```sh
java -cp google-search-archive-parser-assembly-0.1.0.jar com.github.fedeoasi.SearchApplication -s <EXTRACTED_FOLDER>/Takeout/Searches
```
- To view a summary of your search archive:
```sh
java -cp google-search-archive-parser-assembly-0.1.0.jar com.github.fedeoasi.SearchEntrySummarizer <EXTRACTED_FOLDER>/Takeout/Searches
```

## Build from Source

Run this from the project directory:
```sh
  sbt "runMain com.github.fedeoasi.SearchApplication -s <EXTRACTED_FOLDER>/Takeout/Searches"
```

On its first run, the tool will create a new file called all-queries.csv,
a file containing all of your searches along with the timestamp when
they occurred.

On successive runs, the tool will incrementally append only the queries
that occurred after the latest one in the existing file.
This matches the usage pattern where you get new archives from Google
and only add the new queries to your csv file.
The tool will work whether or not you delete the data from Google.