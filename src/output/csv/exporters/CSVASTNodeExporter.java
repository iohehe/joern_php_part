package output.csv.exporters;

import java.util.Map;

import ast.ASTNode;
import tools.databaseNodes.DatabaseNode;
import output.common.ASTNodeExporter;
import output.common.Writer;

public abstract class CSVASTNodeExporter extends ASTNodeExporter
{
	public abstract void addToDatabaseSafe(ASTNode node);

	@Override
	protected void addMainNode(DatabaseNode dbNode)
	{
		Map<String, Object> properties = dbNode.createProperties();
		Writer.addNode(dbNode, properties);

		mainNodeId = Writer.getIdForObject(dbNode);
	}

}
