import java.io.FileReader;
import java.io.IOException;

import csv.CSVFunctionExtractor;
import csv.KeyedCSV.exceptions.InvalidCSVFile;

import ast.php.functionDef.FunctionDef;

import cfg.CFG;
import cfg.CFGConverter;


import udg.CFGToUDGConverter;
import udg.useDefGraph.UseDefGraph;

import ddg.DDGCreator;
import ddg.DefUseCFG.DefUseCFG;
import ddg.CFGAndUDGToDefUseCFG;
import ddg.DataDependenceGraph.DDG;

import cg.CG;
import cg.CGCreator;

public class Joern {
    // extract every function from the csv and recover it to ast
    static CSVFunctionExtractor extractor = new CSVFunctionExtractor();

    // analyze the cfg for a function's ast
    static CFGConverter ast2cfgConverter = new CFGConverter();

    // analyze the use-define relationship for every identifier.
    static CFGToUDGConverter cfgToUDG = new CFGToUDGConverter();

    // analyze data dependency flow
    static CFGAndUDGToDefUseCFG udgAndCfgToDefUseCFG = new CFGAndUDGToDefUseCFG();
    static DDGCreator ddgCreator = new DDGCreator();

    // analyze call graph


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
            //System.out.println(udg.toString());

            // ddg
            DefUseCFG defUseCFG = udgAndCfgToDefUseCFG.convert(cfg, udg);
            DDG ddg = ddgCreator.createForDefUseCFG(defUseCFG);
            System.out.println("~~~~~~~~~~");
            System.out.println(ddg.toString());

            // cg
            CGCreator.addFunctionDef(root_node);
        }

        //
        CG cg = CGCreator.createCG();
    }
}

