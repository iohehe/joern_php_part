package output.csv;

import java.util.Map;

import tools.databaseNodes.EdgeTypes;
import tools.databaseNodes.FileDatabaseNode;
import output.common.DirectoryTreeImporter;
import output.common.Writer;

public class SingleDirCSVDirectoryTreeImporter extends DirectoryTreeImporter {

	@Override
	protected void insertNode(FileDatabaseNode node) {

		Writer.changeOutputDir(outputDir);
		Writer.reset();

		Map<String, Object> properties = node.createProperties();
		long nodeId = Writer.addNode(node, properties);
		node.setId(nodeId);
	}

	@Override
	protected void linkWithParentDirectory(FileDatabaseNode node) {
		long srcId = getSourceIdFromStack();
		long dstId = node.getId();
		Writer.addEdge(srcId, dstId, null, EdgeTypes.IS_PARENT_DIR_OF);
	}

}
