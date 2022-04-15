package output.csv;

import ast.declarations.ClassDefStatement;
import ast.functionDef.FunctionDefBase;
import ast.statements.IdentifierDeclStatement;
import tools.databaseNodes.EdgeTypes;
import output.common.ASTNodeExporter;
import output.common.OutModASTNodeVisitor;
import output.common.Writer;
import output.csv.exporters.CSVClassDefExporter;
import output.csv.exporters.CSVDeclStmtExporter;
import output.csv.exporters.CSVFunctionExporter;

public class CSVASTNodeVisitor extends OutModASTNodeVisitor
{

	private CSVFunctionExporter functionExporter;

	@Override
	public void visit(FunctionDefBase node)
	{
		ASTNodeExporter importer = functionExporter;
		importNode(importer, node);
	}

	@Override
	public void visit(ClassDefStatement node)
	{

		ASTNodeExporter importer = new CSVClassDefExporter();
		long classNodeId = importNode(importer, node);
		visitClassDefContent(node, classNodeId);
	}

	@Override
	public void visit(IdentifierDeclStatement node)
	{
		ASTNodeExporter importer = new CSVDeclStmtExporter();
		importNode(importer, node);
	}

	@Override
	protected void addEdgeFromClassToFunc(long dstNodeId, Long classId)
	{
		Writer.addEdge(classId, dstNodeId, null, EdgeTypes.IS_CLASS_OF);
	}

	public void setFunctionExporter(CSVFunctionExporter functionExporter)
	{
		this.functionExporter = functionExporter;
	}

}
