import java.io.FileReader;
import java.io.IOException;

import csv.CSVFunctionExtractor;
import ast.php.functionDef.FunctionDef;
import csv.KeyedCSV.exceptions.InvalidCSVFile;

public class Joern {
    static CSVFunctionExtractor extractor = new CSVFunctionExtractor();

    public static void main(String[] args) throws IOException, InvalidCSVFile {
        String nodeFileName = "/Users/he/cpg/mantisbt_cpg/nodes.csv";
        String relFileName = "/Users/he/cpg/mantisbt_cpg/rels.csv";

        FileReader nodeFileReader = new FileReader(nodeFileName);
        FileReader relFileNameReadeer = new FileReader(relFileName);
        extractor.initialize(nodeFileReader, relFileNameReadeer);

        FunctionDef root_node;
        while((root_node = (FunctionDef)extractor.getNextFunction()) != null)
        {
            System.out.println(root_node);
        }
    }
}
