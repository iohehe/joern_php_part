package csv;

import java.io.IOException;
import java.io.Reader;

import csv.KeyedCSV.KeyedCSVReader;

public class CSVFunctionExtractor {
    KeyedCSVReader nodeReader;
    KeyedCSVReader relReader;

    public void initialize(Reader nodeStrReader, Reader relStrReader) throws IOException {
        nodeReader = new KeyedCSVReader();
        relReader = new KeyedCSVReader();
        //
        nodeReader.init(nodeStrReader);
        relReader.init(relStrReader);
    }
}
