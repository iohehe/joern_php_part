package output.common;

import tools.cdg.CDG;
import tools.cdg.CDGEdge;
import cfg.nodes.CFGNode;

public abstract class CDGExporter
{
	public void addCDGToDatabase(CDG cdg)
	{

		for (CFGNode src : cdg.getVertices())
		{
			for (CDGEdge edge : cdg.outgoingEdges(src))
			{
				CFGNode dst = edge.getDestination();
				if (!src.equals(dst))
				{
					addControlsEdge(src, dst);
				}
			}
		}
	}

	protected abstract void addControlsEdge(CFGNode src, CFGNode dst);

}
