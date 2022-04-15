import java.io.FileReader;
import java.io.IOException;

import csv.CSVFunctionExtractor;
import ast.php.functionDef.FunctionDef;

public class Joern {
    static CSVFunctionExtractor extractor = new CSVFunctionExtractor();

    public static void main(String[] args) throws IOException {
        String nodeFileName = "/Users/he/cpg/nodes.csv";
        String relFileName = "/Users/he/cpg/rels.csv";

        FileReader nodeFileReader = new FileReader(nodeFileName);
        FileReader relFileNameReadeer = new FileReader(relFileName);

        FunctionDef root_node;

    }
}
