package output.csv.exporters;

import cfg.nodes.CFGNode;
import output.common.DOMExporter;
import cfg.nodes.ASTNodeContainer;
import tools.databaseNodes.EdgeTypes;
import output.common.Writer;

public class CSVDOMExporter extends DOMExporter
{

	@Override
	protected void addDomEdge(CFGNode vertex, CFGNode dominator)
	{
		long srcId = getId(dominator);
		long dstId = getId(vertex);
		Writer.addEdge(srcId, dstId, null, EdgeTypes.DOM);
	}

	@Override
	protected void addPostDomEdge(CFGNode vertex, CFGNode postDominator)
	{
		long srcId = getId(postDominator);
		long dstId = getId(vertex);
		Writer.addEdge(srcId, dstId, null, EdgeTypes.POST_DOM);
	}

	private long getId(CFGNode node)
	{
		if (node instanceof ASTNodeContainer)
		{
			return Writer
					.getIdForObject(((ASTNodeContainer) node).getASTNode());
		}
		else
		{
			return Writer.getIdForObject(node);
		}
	}

}
