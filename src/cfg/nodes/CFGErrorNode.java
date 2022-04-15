package cfg.nodes;

import java.util.Map;

import tools.databaseNodes.NodeKeys;

public class CFGErrorNode extends AbstractCFGNode
{

	@Override
	public String toString()
	{
		return "[ERROR]";
	}

	@Override
	public Map<String, Object> getProperties()
	{
		Map<String, Object> properties = super.getProperties();
		properties.put(NodeKeys.CODE, "ERROR");
		return properties;
	}

}
