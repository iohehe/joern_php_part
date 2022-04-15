import java.io.FileReader;
import java.io.IOException;

import csv.CSVFunctionExtractor;
import csv.KeyedCSV.exceptions.InvalidCSVFile;

import ast.php.functionDef.FunctionDef;

import cfg.CFG;
import cfg.CFGConverter;

import udg.CFGToUDGConverter;
import udg.useDefGraph.UseDefGraph;

public class Joern {
    // extract every function from the csv and recover it to ast
    static CSVFunctionExtractor extractor = new CSVFunctionExtractor();

    // analyze the cfg for a function's ast
    static CFGConverter ast2cfgConverter = new CFGConverter();

    // analyze the use-define relationship for every identifier.
    static CFGToUDGConverter cfgToUDG = new CFGToUDGConverter();

    public static void main(String[] args) throws IOException, InvalidCSVFile {
        String nodeFileName = "demos/nodes.csv";
        String relFileName = "demos/rels.csv";

        FileReader nodeFileReader = new FileReader(nodeFileName);
        FileReader relFileNameReadeer = new FileReader(relFileName);
        extractor.initialize(nodeFileReader, relFileNameReadeer);

        // let's go... :)
        FunctionDef root_node;
        while((root_node = (FunctionDef)extractor.getNextFunction()) != null)
        {
            //System.out.println(root_node);

            //cfg
            CFG cfg = ast2cfgConverter.convert(root_node);
            //System.out.println(cfg);

            // udg
            UseDefGraph udg = cfgToUDG.convert(cfg);
            System.out.println(udg.toString());
        }
    }
}

