package output.csv.exporters;

import java.util.Map;

import ast.ASTNode;
import tools.databaseNodes.ASTDatabaseNode;
import tools.databaseNodes.EdgeTypes;
import tools.databaseNodes.NodeKeys;
import output.common.ASTExporter;
import output.common.Writer;

public class CSVASTExporter extends ASTExporter
{

	@Override
	protected void addASTLink(ASTNode parent, ASTNode child)
	{
		long srcId = Writer.getIdForObject(parent);
		long dstId = Writer.getIdForObject(child);
		Writer.addEdge(srcId, dstId, null, EdgeTypes.IS_AST_PARENT);
	}

	@Override
	protected void addASTNode(ASTNode node)
	{
		ASTDatabaseNode astDatabaseNode = new ASTDatabaseNode();
		astDatabaseNode.initialize(node);
		astDatabaseNode.setCurrentFunction(currentFunction);
		Map<String, Object> properties = astDatabaseNode.createProperties();

		properties.put(NodeKeys.FUNCTION_ID,
				Writer.getIdForObject(currentFunction).toString());
		Writer.addNode(node, properties);
	}
}
