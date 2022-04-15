package output.csv.exporters;

import java.util.Map;

import tools.databaseNodes.DatabaseNode;
import tools.databaseNodes.EdgeTypes;
import output.common.DeclStmtExporter;
import output.common.Writer;

public class CSVDeclStmtExporter extends DeclStmtExporter
{

	public CSVDeclStmtExporter()
	{
		declImporter = new CSVDeclExporter();
	}

	@Override
	protected void addLinkFromStmtToDecl(long mainNodeId, long declId)
	{
		Writer.addEdge(mainNodeId, declId, null, EdgeTypes.DECLARES);
	}

	@Override
	protected void addMainNode(DatabaseNode dbNode)
	{
		Map<String, Object> properties = dbNode.createProperties();
		Writer.addNode(dbNode, properties);
		mainNodeId = Writer.getIdForObject(dbNode);
	}

}
