package tools.cdg;

import tools.graphutils.IncidenceListGraph;
import cfg.nodes.CFGNode;

public class CDG extends IncidenceListGraph<CFGNode, CDGEdge>
{

	void addEdge(CFGNode source, CFGNode destination)
	{
		addEdge(new CDGEdge(source, destination));
	}

}
