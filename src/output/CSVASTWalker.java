package output;

import output.csv.CSVASTNodeVisitor;
import output.csv.exporters.CSVFunctionExporter;
import output.parser.ParserASTWalker;

public class CSVASTWalker extends ParserASTWalker
{
	public CSVASTWalker(CSVFunctionExporter functionExporter)
	{
		astVisitor = new CSVASTNodeVisitor();
		((CSVASTNodeVisitor) astVisitor).setFunctionExporter(functionExporter);
	}
}
