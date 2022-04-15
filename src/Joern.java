import java.io.FileReader;
import java.io.IOException;

import cfg.CFGConverter;
import csv.CSVFunctionExtractor;
import csv.KeyedCSV.exceptions.InvalidCSVFile;

import ast.php.functionDef.FunctionDef;

import cfg.CFG;

public class Joern {
    static CSVFunctionExtractor extractor = new CSVFunctionExtractor();

    static CFGConverter ast2cfgConverter = new CFGConverter();

    public static void main(String[] args) throws IOException, InvalidCSVFile {
        String nodeFileName = "/Users/he/cpg/mantisbt_cpg/nodes.csv";
        String relFileName = "/Users/he/cpg/mantisbt_cpg/rels.csv";

        FileReader nodeFileReader = new FileReader(nodeFileName);
        FileReader relFileNameReadeer = new FileReader(relFileName);
        extractor.initialize(nodeFileReader, relFileNameReadeer);

        FunctionDef root_node;
        while((root_node = (FunctionDef)extractor.getNextFunction()) != null)
        {
            //System.out.println(root_node);
            CFG cfg = ast2cfgConverter.convert(root_node);
            System.out.println(cfg);
        }
    }
}
