package tools.cdg;

import java.util.Map;

import cfg.nodes.CFGNode;
import tools.graphutils.Edge;

public class CDGEdge extends Edge<CFGNode>
{

	public CDGEdge(CFGNode source, CFGNode destination)
	{
		super(source, destination);
	}

	@Override
	public Map<String, Object> getProperties()
	{
		return null;
	}

}
