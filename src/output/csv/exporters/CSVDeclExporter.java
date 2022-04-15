package output.csv.exporters;

import ast.ASTNode;
import tools.databaseNodes.DeclDatabaseNode;

public class CSVDeclExporter extends CSVASTNodeExporter
{

	@Override
	public void addToDatabaseSafe(ASTNode node)
	{
		DeclDatabaseNode dbNode = new DeclDatabaseNode();
		dbNode.initialize(node);
		addMainNode(dbNode);

	}

}
