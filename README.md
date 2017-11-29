# google-search-parser

A set of command line tools to work with your personal search data
exported from Google search.

These tools help you manage your search history so that you can remove
it from Google.

You can download the data as follows:
- Go to https://takeout.google.com/settings/takeout
- Click on "Select None"
- Scroll down to "Searches" and enable it using the slider
- Select your favorite file type and delivery method
- Hit "Create Archive"

Once you have obtained your archive, extract it to a known location.
The tool will need the path to the extracted directory that contains
the JSON files.

Example:
  sbt "runMain com.github.fedeoasi.SearchMain <EXTRACTED_FOLDER>/Takeout/Searches"

On its first run, the tool will create a new file called all-queries.csv,
a file containing all of your searches along with the timestamp when
they occurred.

On successive runs, the tool will incrementally append only the queries
that occurred after the latest one in the existing file.
This matches the usage pattern where you get new archives from Google
and only add the new queries to your csv file.
The tool will work whether or not you delete the data from Google.