import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.cli.ParseException;
import tools.CommandLineInterface;
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
    static CommandLineInterface cmdLine = new CommandLineInterface();

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
        //parse command line
        parseCommandLine(args);
        String nodeFileName = cmdLine.getNodeFile();
        String relFileName = cmdLine.getEdgeFile();

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
        // cg
        CG cg = CGCreator.createCG();
        csvCGExporter.writeCGEdges(cg);
        csvWriter.closeEdgeFile();
    }

    private static void parseCommandLine(String[] args) {
        try
        {
            cmdLine.parseCommandLine(args);
        }
        catch (RuntimeException | ParseException e)
        {
            printHelpAndTerminate(e);
        }
    }

    private static void printHelpAndTerminate(Exception e) {

        System.err.println(e.getMessage());
        cmdLine.printHelp();
        System.exit(0);
    }

}

