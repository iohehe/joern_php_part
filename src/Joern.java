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

import output.common.Writer;
import output.csv.MultiPairCSVWriterImpl;
import output.csv.exporters.CSVCFGExporter;
import output.csv.exporters.CSVCGExporter;
import output.csv.exporters.CSVDDGExporter;


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

    // output
    static CSVCFGExporter csvCFGExporter = new CSVCFGExporter();
    static CSVDDGExporter csvDDGExporter = new CSVDDGExporter();
    static CSVCGExporter csvCGExporter = new CSVCGExporter();

    public static void main(String[] args) throws IOException, InvalidCSVFile {
        String nodeFileName = "/Users/he/cpg/fix_cpg/if/nodes.csv";
        String relFileName = "/Users/he/cpg/fix_cpg/if/rels.csv";

        // init input
        FileReader nodeFileReader = new FileReader(nodeFileName);
        FileReader relFileNameReadeer = new FileReader(relFileName);
        extractor.initialize(nodeFileReader, relFileNameReadeer);

        // init output
        MultiPairCSVWriterImpl csvWriter = new MultiPairCSVWriterImpl();
        csvWriter.openEdgeFile( ".", "cpg_edges.csv");
        Writer.setWriterImpl( csvWriter);

        // let's go... :)
        FunctionDef root_node;
        while((root_node = (FunctionDef)extractor.getNextFunction()) != null)
        {
            //System.out.println(root_node);

            //cfg
            CFG cfg = ast2cfgConverter.convert(root_node);
            csvCFGExporter.writeCFGEdges(cfg);
            //System.out.println(cfg);

            // udg
            UseDefGraph udg = cfgToUDG.convert(cfg);
            //System.out.println(udg.toString());

            // ddg
            DefUseCFG defUseCFG = udgAndCfgToDefUseCFG.convert(cfg, udg);
            DDG ddg = ddgCreator.createForDefUseCFG(defUseCFG);
            csvDDGExporter.writeDDGEdges(ddg);

            // cg
            CGCreator.addFunctionDef(root_node);
        }

        //
        CG cg = CGCreator.createCG();
        csvCGExporter.writeCGEdges(cg);
        csvWriter.closeEdgeFile();
    }
}

