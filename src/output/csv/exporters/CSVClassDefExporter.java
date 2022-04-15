package output.csv.exporters;

import java.util.Map;

import tools.databaseNodes.ClassDefDatabaseNode;
import tools.databaseNodes.DatabaseNode;
import tools.databaseNodes.EdgeTypes;
import tools.databaseNodes.FileDatabaseNode;
import output.common.ClassDefExporter;
import output.common.Writer;

public class CSVClassDefExporter extends ClassDefExporter
{

	@Override
	protected void linkClassDefToFileNode(ClassDefDatabaseNode classDefNode,
			FileDatabaseNode fileNode)
	{

		long fileId = fileNode.getId();
		long functionId = Writer.getIdForObject(classDefNode);

		Writer.addEdge(fileId, functionId, null, EdgeTypes.IS_FILE_OF);
	}

	@Override
	protected void addMainNode(DatabaseNode dbNode)
	{
		Map<String, Object> properties = dbNode.createProperties();
		Writer.addNode(dbNode, properties);
		mainNodeId = Writer.getIdForObject(dbNode);
	}

}
