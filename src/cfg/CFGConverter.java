package cfg;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import ast.ASTNode;
import ast.functionDef.FunctionDefBase;
import ast.functionDef.ParameterBase;
import ast.functionDef.ParameterList;
import ast.logical.statements.BreakOrContinueStatement;
import ast.logical.statements.CompoundStatement;
import ast.logical.statements.Label;
import ast.expressions.Expression;
import ast.logical.statements.Statement;
import ast.php.statements.blockstarters.IfElement;
import ast.php.statements.blockstarters.IfStatement;
import ast.statements.blockstarters.IfStatementBase;
import ast.statements.blockstarters.CatchStatement;
import ast.statements.blockstarters.DoStatement;
import ast.statements.blockstarters.ForStatement;
import ast.statements.blockstarters.ForEachStatement;
import ast.statements.blockstarters.SwitchStatement;
import ast.php.statements.blockstarters.SwitchCase;
import ast.php.statements.blockstarters.SwitchStatementPHP;
import ast.statements.blockstarters.TryStatement;
import ast.statements.blockstarters.WhileStatement;
import ast.statements.jump.BreakStatement;
import ast.statements.jump.ContinueStatement;
import ast.statements.jump.GotoStatement;
import ast.statements.jump.ReturnStatement;
import ast.statements.jump.ThrowStatement;
import cfg.nodes.ASTNodeContainer;
import cfg.nodes.CFGEntryNode;
import cfg.nodes.CFGErrorNode;
import cfg.nodes.CFGExceptionNode;
import cfg.nodes.CFGExitNode;
import cfg.nodes.CFGNode;
import cfg.nodes.InfiniteForNode;

public class CFGConverter
{
	protected static StructuredFlowVisitor structuredFlowVisitior;
	static {
		structuredFlowVisitior = new StructuredFlowVisitor();
	}

	// node id offsets for entry and exit nodes of function definitions
	private static final int CFG_ENTRY_OFFSET = 1;
	private static final int CFG_EXIT_OFFSET = 2;


	public CFG convert(FunctionDefBase functionDefinition) {
		// create cfg
		CFG cfg = newInstance(functionDefinition);

		// for PHP, we additionally set the node ids for the function entry and exit nodes:
		// their ids match the ids of the entry and exit nodes output by the PHP AST parser
		Long id = functionDefinition.getNodeId();
		((CFGEntryNode)cfg.getEntryNode()).setNodeId( id + CFG_ENTRY_OFFSET);
		((CFGExitNode)cfg.getExitNode()).setNodeId( id + CFG_EXIT_OFFSET);

		return cfg;
	}
	public CFG newInstance(FunctionDefBase functionDefinition)
	{
		try
		{
			CFG function = newInstance();
			CFG parameterBlock = convert(
					functionDefinition.getParameterList());
			CFG functionBody = convert(functionDefinition.getContent());
			parameterBlock.appendCFG(functionBody);
			function.appendCFG(parameterBlock);
			fixGotoStatements(function);
			fixReturnStatements(function);
			if (!function.getBreakStatements().isEmpty())
			{
				System.err.println("warning: unresolved break statement");
				fixBreakStatements(function, function.getErrorNode());
			}
			if (!function.getContinueStatements().isEmpty())
			{
				System.err.println("warning: unresolved continue statement");
				fixContinueStatement(function, function.getErrorNode());
			}
			if (function.hasExceptionNode())
			{
				function.addEdge(function.getExceptionNode(),
						function.getExitNode(), CFGEdge.UNHANDLED_EXCEPT_LABEL);
			}

			return function;
		}
		catch (Exception e)
		{
			// e.printStackTrace();
			return newErrorInstance();
		}
	}

	public static CFG newInstance(ASTNode... nodes)
	{
		try
		{
			CFG block = new CFG();
			CFGNode last = block.getEntryNode();
			for (ASTNode node : nodes)
			{
				CFGNode container = new ASTNodeContainer(node);
				block.addVertex(container);
				block.addEdge(last, container);
				last = container;
			}
			block.addEdge(last, block.getExitNode());
			return block;
		}
		catch (Exception e)
		{
			// e.printStackTrace();
			return newErrorInstance();
		}
	}

	public static CFG newErrorInstance()
	{
		CFG errorBlock = new CFG();
		CFGNode errorNode = new CFGErrorNode();
		errorBlock.addVertex(errorNode);
		errorBlock.addEdge(errorBlock.getEntryNode(), errorNode);
		errorBlock.addEdge(errorNode, errorBlock.getExitNode());
		return errorBlock;
	}


	public static CFG newInstance(WhileStatement whileStatement)
	{
		try
		{
			CFG whileBlock = new CFG();
			CFGNode conditionContainer = new ASTNodeContainer(
					whileStatement.getCondition());
			whileBlock.addVertex(conditionContainer);
			whileBlock.addEdge(whileBlock.getEntryNode(), conditionContainer);

			CFG whileBody = convert(whileStatement.getStatement());

			whileBlock.mountCFG(conditionContainer, conditionContainer,
					whileBody, CFGEdge.TRUE_LABEL);
			whileBlock.addEdge(conditionContainer, whileBlock.getExitNode(),
					CFGEdge.FALSE_LABEL);

			fixBreakStatements(whileBlock, whileBlock.getExitNode());
			fixContinueStatement(whileBlock, conditionContainer);

			return whileBlock;
		}
		catch (Exception e)
		{
			// e.printStackTrace();
			return newErrorInstance();
		}
	}

	public static CFG newInstance(ForStatement forStatement)
	{
		try
		{
			CFG forBlock = new CFG();

			ASTNode initialization = forStatement.getForInitExpression();
			ASTNode condition = forStatement.getCondition();
			ASTNode expression = forStatement.getForLoopExpression();

			CFG forBody = convert(forStatement.getStatement());
			CFGNode conditionContainer;

			if (condition != null)
			{
				conditionContainer = new ASTNodeContainer(condition);
			}
			else
			{
				conditionContainer = new InfiniteForNode();
			}

			forBlock.addVertex(conditionContainer);
			forBlock.addEdge(conditionContainer, forBlock.getExitNode(),
					CFGEdge.FALSE_LABEL);

			if (initialization != null)
			{
				CFGNode initializationContainer = new ASTNodeContainer(
						initialization);
				forBlock.addVertex(initializationContainer);
				forBlock.addEdge(forBlock.getEntryNode(),
						initializationContainer);
				forBlock.addEdge(initializationContainer, conditionContainer);
			}
			else
			{
				forBlock.addEdge(forBlock.getEntryNode(), conditionContainer);
			}

			if (expression != null)
			{
				CFGNode expressionContainer = new ASTNodeContainer(expression);
				forBlock.addVertex(expressionContainer);
				forBlock.addEdge(expressionContainer, conditionContainer);
				forBlock.mountCFG(conditionContainer, expressionContainer,
						forBody, CFGEdge.TRUE_LABEL);
			}
			else
			{
				forBlock.mountCFG(conditionContainer, conditionContainer,
						forBody, CFGEdge.TRUE_LABEL);
			}

			fixBreakStatements(forBlock, forBlock.getExitNode());
			fixContinueStatement(forBlock, conditionContainer);

			return forBlock;
		}
		catch (Exception e)
		{
			// e.printStackTrace();
			return newErrorInstance();
		}
	}

	public static CFG newInstance(DoStatement doStatement)
	{
		try
		{
			CFG doBlock = new CFG();

			CFGNode conditionContainer = new ASTNodeContainer(
					doStatement.getCondition());

			doBlock.addVertex(conditionContainer);
			doBlock.addEdge(conditionContainer, doBlock.getExitNode(),
					CFGEdge.FALSE_LABEL);

			CFG doBody = convert(doStatement.getStatement());

			doBlock.mountCFG(doBlock.getEntryNode(), conditionContainer, doBody,
					CFGEdge.EMPTY_LABEL);

			int nVerticesOfBody = doBody.getVertices().size();
			if(nVerticesOfBody == 2){
				doBlock.addEdge(conditionContainer, conditionContainer,
						CFGEdge.TRUE_LABEL);
			}else{

				for (CFGEdge edge : doBody.outgoingEdges(doBody.getEntryNode()))
				{
					doBlock.addEdge(conditionContainer, edge.getDestination(),
							CFGEdge.TRUE_LABEL);
				}

			}

			fixBreakStatements(doBlock, doBlock.getExitNode());
			fixContinueStatement(doBlock, conditionContainer);

			return doBlock;
		}
		catch (Exception e)
		{
			// e.printStackTrace();
			return newErrorInstance();
		}
	}

	public static CFG newInstance(ParameterList paramList)
	{
		try
		{
			CFG parameterListBlock = newInstance();
			for (ParameterBase parameter : paramList)
			{
				parameterListBlock.appendCFG(convert(parameter));
			}
			return parameterListBlock;
		}
		catch (Exception e)
		{
			// e.printStackTrace();
			return newErrorInstance();
		}
	}

	public static CFG newInstance(CompoundStatement content)
	{
		try
		{
			CFG compoundBlock = newInstance();
			for (ASTNode statement : content.getStatements())
			{
				compoundBlock.appendCFG(convert(statement));
			}
			return compoundBlock;
		}
		catch (Exception e)
		{
			// e.printStackTrace();
			return newErrorInstance();
		}
	}

	public static CFG newInstance(ReturnStatement returnStatement)
	{
		try
		{
			CFG returnBlock = new CFG();
			CFGNode returnContainer = new ASTNodeContainer(returnStatement);
			returnBlock.addVertex(returnContainer);
			returnBlock.addEdge(returnBlock.getEntryNode(), returnContainer);
			returnBlock.addEdge(returnContainer, returnBlock.getExitNode());
			returnBlock.addReturnStatement(returnContainer);
			return returnBlock;
		}
		catch (Exception e)
		{
			// e.printStackTrace();
			return newErrorInstance();
		}
	}

	public static CFG newInstance(GotoStatement gotoStatement)
	{
		try
		{
			CFG gotoBlock = new CFG();
			CFGNode gotoContainer = new ASTNodeContainer(gotoStatement);
			gotoBlock.addVertex(gotoContainer);
			gotoBlock.addEdge(gotoBlock.getEntryNode(), gotoContainer);
			gotoBlock.addEdge(gotoContainer, gotoBlock.getExitNode());
			gotoBlock.addGotoStatement(gotoContainer,
					gotoStatement.getTargetName());
			return gotoBlock;
		}
		catch (Exception e)
		{
			// e.printStackTrace();
			return newErrorInstance();
		}
	}

	public static CFG newInstance(Label labelStatement)
	{
		try
		{
			CFG continueBlock = new CFG();
			CFGNode labelContainer = new ASTNodeContainer(labelStatement);
			continueBlock.addVertex(labelContainer);
			continueBlock.addEdge(continueBlock.getEntryNode(), labelContainer);
			continueBlock.addEdge(labelContainer, continueBlock.getExitNode());
			String label = labelStatement.getLabelName();
			continueBlock.addBlockLabel(label, labelContainer);
			return continueBlock;
		}
		catch (Exception e)
		{
			// e.printStackTrace();
			return newErrorInstance();
		}
	}

	public static CFG newInstance(ContinueStatement continueStatement)
	{
		try
		{
			CFG continueBlock = new CFG();
			CFGNode continueContainer = new ASTNodeContainer(continueStatement);
			continueBlock.addVertex(continueContainer);
			continueBlock.addEdge(continueBlock.getEntryNode(),
					continueContainer);
			continueBlock.addEdge(continueContainer,
					continueBlock.getExitNode());
			continueBlock.addContinueStatement(continueContainer);
			return continueBlock;
		}
		catch (Exception e)
		{
			// e.printStackTrace();
			return newErrorInstance();
		}
	}

	public static CFG newInstance(BreakStatement breakStatement)
	{
		try
		{
			CFG breakBlock = new CFG();
			CFGNode breakContainer = new ASTNodeContainer(breakStatement);
			breakBlock.addVertex(breakContainer);
			breakBlock.addEdge(breakBlock.getEntryNode(), breakContainer);
			breakBlock.addEdge(breakContainer, breakBlock.getExitNode());
			breakBlock.addBreakStatement(breakContainer);
			return breakBlock;
		}
		catch (Exception e)
		{
			// e.printStackTrace();
			return newErrorInstance();
		}
	}

	public static CFG newInstance(ThrowStatement throwStatement)
	{
		try
		{
			CFG throwBlock = new CFG();
			CFGNode throwContainer = new ASTNodeContainer(throwStatement);
			CFGExceptionNode exceptionNode = new CFGExceptionNode();
			throwBlock.addVertex(throwContainer);
			throwBlock.setExceptionNode(exceptionNode);
			throwBlock.addEdge(throwBlock.getEntryNode(), throwContainer);
			throwBlock.addEdge(throwContainer, exceptionNode,
					CFGEdge.EXCEPT_LABEL);
			// throwBlock.addEdge(throwContainer, throwBlock.getExitNode());
			return throwBlock;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return newErrorInstance();
		}
	}

	public static CFG convert(ASTNode node)
	{
		if (node == null)
			return newInstance();

		node.accept(structuredFlowVisitior);
		return structuredFlowVisitior.getCFG();
	}

	public static void fixBreakStatements(CFG thisCFG, CFGNode target)
	{
		List<CFGNode> breakStatements = thisCFG.getBreakStatements();
		Iterator<CFGNode> it = breakStatements.iterator();

		fixBreakOrContinueStatements(thisCFG, target, it);
	}

	public static void fixContinueStatement(CFG thisCFG, CFGNode target)
	{
		List<CFGNode> continueStatements = thisCFG.getContinueStatements();
		Iterator<CFGNode> it = continueStatements.iterator();

		fixBreakOrContinueStatements(thisCFG, target, it);
	}

	private static void fixBreakOrContinueStatements(CFG thisCFG, CFGNode target, Iterator<CFGNode> it)
	{
		while(it.hasNext())
		{
			CFGNode breakOrContinueNode = it.next();

			ASTNodeContainer nodeContainer = (ASTNodeContainer) breakOrContinueNode;
			BreakOrContinueStatement statement = (BreakOrContinueStatement) nodeContainer.getASTNode();

			Integer depth = statement.getDepthAsInteger();
			if(depth != 0 && depth != 1){
				statement.decrementDepth();
				continue;
			}

			thisCFG.removeEdgesFrom(breakOrContinueNode);
			thisCFG.addEdge(breakOrContinueNode, target);
			it.remove();
		}
	}

	public static void fixGotoStatements(CFG thisCFG)
	{
		for (Entry<CFGNode, String> entry : thisCFG.getGotoStatements()
				.entrySet())
		{
			CFGNode gotoStatement = entry.getKey();
			String label = entry.getValue();
			thisCFG.removeEdgesFrom(gotoStatement);
			thisCFG.addEdge(gotoStatement, thisCFG.getBlockByLabel(label));
		}
		thisCFG.getGotoStatements().clear();
	}

	public static void fixReturnStatements(CFG thisCFG)
	{
		for (CFGNode returnStatement : thisCFG.getReturnStatements())
		{
			thisCFG.removeEdgesFrom(returnStatement);
			thisCFG.addEdge(returnStatement, thisCFG.getExitNode());
		}
		thisCFG.getReturnStatements().clear();
	}

	public static CFG newInstance(IfStatementBase ifStatement)
	{
		try
		{
			IfStatement phpIfStatement = (IfStatement)ifStatement;

			Iterator<IfElement> iterator = phpIfStatement.iterator();
			if(!iterator.hasNext())
				return newErrorInstance();

			CFG ifElemCFG = convertIfElem(iterator);
			return ifElemCFG;
		}
		catch (Exception e)
		{
			// e.printStackTrace();
			return newErrorInstance();
		}
	}

	private static CFG convertIfElem(Iterator<IfElement> iterator)
	{
		CFG block = new CFG();

		IfElement ifElem = iterator.next();
		Expression condition = ifElem.getCondition();
		Statement statement = ifElem.getStatement();
		CFG ifBlock = convert(statement);


		if(condition == null)
		{
			// this is the 'else' case
			return ifBlock;
		}

		CFGNode conditionContainer = new ASTNodeContainer(condition);

		block.addVertex(conditionContainer);
		block.addEdge(block.getEntryNode(), conditionContainer);

		block.mountCFG(conditionContainer, block.getExitNode(), ifBlock,
				CFGEdge.TRUE_LABEL);

		if(iterator.hasNext())
		{
			CFG elseBlock = convertIfElem(iterator);
			block.mountCFG(conditionContainer, block.getExitNode(),
					elseBlock, CFGEdge.FALSE_LABEL);
		}
		else
		{
			block.addEdge(conditionContainer, block.getExitNode(),
					CFGEdge.FALSE_LABEL);
		}

		return block;
	}

	public static CFG newInstance(SwitchStatement switchStatement)
	{
		try
		{
			SwitchStatementPHP phpSwitchStatement = (SwitchStatementPHP)switchStatement;

			// create new CFG block
			CFG switchBlock = new CFG();

			// "head" vertex: the expression to be compared to the individual cases
			CFGNode conditionContainer = new ASTNodeContainer(
					phpSwitchStatement.getExpression());
			switchBlock.addVertex(conditionContainer);
			switchBlock.addEdge(switchBlock.getEntryNode(), conditionContainer);

			// create a CFG block for each case and connect them
			boolean defaultExists = false;
			for( SwitchCase switchCase : phpSwitchStatement.getSwitchList()) {

				// convert case block and add to main switch block
				CFG caseBody = convert(switchCase.getStatement());
				switchBlock.appendCFG(caseBody);

				// determine label
				String label = switchCase.getValue() != null
						? switchCase.getValue().getEscapedCodeStr()
						: "default";
				if( label.equals("default")) defaultExists = true;

				// connect condition to first statement of case
				// if case is empty, simply connect to current exit node
				if( !caseBody.isEmpty()) {
					for( CFGEdge caseEntryEdge : caseBody.outgoingEdges( caseBody.getEntryNode())) {
						switchBlock.addEdge( conditionContainer, caseEntryEdge.getDestination(), label);
					}
				}
				else {
					switchBlock.addEdge( conditionContainer, switchBlock.getExitNode(), label);
				}
			}
			// default edge to exit node (if no default case is found)
			if( !defaultExists) {
				switchBlock.addEdge( conditionContainer, switchBlock.getExitNode(), "default");
			}

			// finally, fix break statements and rejoice, for we are done
			fixBreakStatements( switchBlock, switchBlock.getExitNode());

			return switchBlock;
		}
		catch (Exception e)
		{
			//e.printStackTrace();
			return newErrorInstance();
		}
	}

	public static CFG newInstance(ForEachStatement forEachStatement)
	{
		try
		{
			CFG forEachBlock = new CFG();
			// for now, let's consider the iterated object as the condition only
			// TODO actually, it would be nicer to have the ForEachCondition explicitly
			// be a part of the AST
			CFGNode conditionContainer = new ASTNodeContainer(
					forEachStatement.getCondition().getIteratedObject());
			forEachBlock.addVertex(conditionContainer);
			forEachBlock.addEdge(forEachBlock.getEntryNode(), conditionContainer);

			CFG forEachBody = convert(forEachStatement.getStatement());

			forEachBlock.mountCFG(conditionContainer, conditionContainer,
					forEachBody, CFGEdge.NEXT_LABEL);
			forEachBlock.addEdge(conditionContainer, forEachBlock.getExitNode(),
					CFGEdge.COMPLETE_LABEL);

			fixBreakStatements(forEachBlock, forEachBlock.getExitNode());
			fixContinueStatement(forEachBlock, conditionContainer);

			return forEachBlock;
		}
		catch (Exception e)
		{
			// e.printStackTrace();
			return newErrorInstance();
		}
	}

	public static CFG newInstance(TryStatement tryStatement)
	{
		try
		{
			CFG tryCFG = convert(tryStatement.getContent());
			List<CFGNode> statements = new LinkedList<CFGNode>();

			// Get all nodes within try not connected to an exception node.
			for (CFGNode node : tryCFG.getVertices())
			{
				if (!(node instanceof CFGEntryNode)
						&& !(node instanceof CFGExitNode))
				{
					boolean b = true;
					for (CFGEdge edge : tryCFG.outgoingEdges(node))
					{
						CFGNode destination = edge.getDestination();
						if (destination instanceof CFGExceptionNode)
						{
							b = false;
							break;
						}
					}
					if (b)
						statements.add(node);
				}
			}

			// Add exception node for current try block
			if (!statements.isEmpty())
			{
				CFGExceptionNode exceptionNode = new CFGExceptionNode();
				tryCFG.setExceptionNode(exceptionNode);
				for (CFGNode node : statements)
				{
					tryCFG.addEdge(node, exceptionNode, CFGEdge.EXCEPT_LABEL);
				}
			}

			if (tryStatement.getCatchList().size() == 0)
			{
				System.err.println("warning: cannot find catch for try");
				return tryCFG;
			}

			// Mount exception handlers
			for (CatchStatement catchStatement : tryStatement.getCatchList())
			{
				CFG catchBlock = convert(catchStatement.getStatement());
				tryCFG.mountCFG(tryCFG.getExceptionNode(), tryCFG.getExitNode(),
						catchBlock, CFGEdge.HANDLED_EXCEPT_LABEL);
			}

			// now deal with the finally block

			CompoundStatement finallyContent = tryStatement.getFinallyContent();
			tryCFG.appendCFG(convert(finallyContent));

			return tryCFG;

		}
		catch (Exception e)
		{
			// e.printStackTrace();
			return newErrorInstance();
		}
	}

}
