package cfg;

import ast.ASTNode;
import ast.logical.statements.CompoundStatement;
import ast.walking.ASTNodeVisitor;
import ast.functionDef.ParameterBase;
import ast.functionDef.ParameterList;
import ast.logical.statements.Label;
import ast.statements.blockstarters.DoStatement;
import ast.statements.blockstarters.ForEachStatement;
import ast.statements.blockstarters.ForStatement;
import ast.statements.blockstarters.IfStatementBase;
import ast.statements.blockstarters.SwitchStatement;
import ast.statements.blockstarters.TryStatement;
import ast.statements.blockstarters.WhileStatement;
import ast.statements.jump.BreakStatement;
import ast.statements.jump.ContinueStatement;
import ast.statements.jump.GotoStatement;
import ast.statements.jump.ReturnStatement;
import ast.statements.jump.ThrowStatement;
import cfg.CFGConverter;
import cfg.StructuredFlowVisitor;
import cfg.nodes.ASTNodeContainer;
import cfg.nodes.CFGNode;

public class StructuredFlowVisitor extends ASTNodeVisitor {

	protected CFG returnCFG;

	public CFG getCFG() {
		return returnCFG;
	}

	public void visit(ParameterList paramList) {
		returnCFG = CFGConverter.newInstance(paramList);
	}

	public void visit(CompoundStatement content) {
		returnCFG = CFGConverter.newInstance(content);
	}

	public void visit(ASTNode expression) {
		returnCFG = CFGConverter.newInstance(expression);
	}

	@Override
	public void visit(ParameterBase param) {
		returnCFG = CFGConverter.newInstance(param);

		for (CFGNode node : returnCFG.getVertices()) {
			if (!(node instanceof ASTNodeContainer))
				continue;
			returnCFG.registerParameter(node);
		}

	}

	@Override
	public void visit(IfStatementBase node) {
		returnCFG = CFGConverter.newInstance(node);
	}

	@Override
	public void visit(Label node) {
		returnCFG = CFGConverter.newInstance(node);
	}

	@Override
	public void visit(WhileStatement node) {
		returnCFG = CFGConverter.newInstance(node);
	}

	@Override
	public void visit(DoStatement node) {
		returnCFG = CFGConverter.newInstance(node);
	}

	@Override
	public void visit(ForStatement node) {
		returnCFG = CFGConverter.newInstance(node);
	}

	@Override
	public void visit(ForEachStatement node) {
		returnCFG = CFGConverter.newInstance(node);
	}

	@Override
	public void visit(ReturnStatement expression) {
		returnCFG = CFGConverter.newInstance(expression);
	}

	@Override
	public void visit(GotoStatement expression) {
		returnCFG = CFGConverter.newInstance(expression);
	}

	@Override
	public void visit(SwitchStatement node) {
		returnCFG = CFGConverter.newInstance(node);
	}

	@Override
	public void visit(ContinueStatement expression) {
		returnCFG = CFGConverter.newInstance(expression);
	}

	@Override
	public void visit(BreakStatement expression) {
		returnCFG = CFGConverter.newInstance(expression);
	}

	@Override
	public void visit(TryStatement node) {
		returnCFG = CFGConverter.newInstance(node);
	}

	@Override
	public void visit(ThrowStatement node) {
		returnCFG = CFGConverter.newInstance(node);

	}
}
