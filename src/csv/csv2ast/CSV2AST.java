package csv.csv2ast;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.NoSuchElementException;

import ast.functionDef.FunctionDefBase;
import csv.KeyedCSV.KeyedCSVReader;
import csv.KeyedCSV.KeyedCSVRow;
import csv.KeyedCSV.exceptions.InvalidCSVFile;
import csv.PHPCSVNodeTypes;


public class CSV2AST
{
	CSVRowInterpreter nodeInterpreter;
	CSVRowInterpreter edgeInterpreter;

	public CSV2AST() {
		this.nodeInterpreter = new PHPCSVNodeInterpreter();
		this.edgeInterpreter = new PHPCSVEdgeInterpreter();
	}
	public FunctionDefBase convert(String nodeFilename, String edgeFilename)
			throws IOException, InvalidCSVFile
	{
		FileReader nodeStrReader = new FileReader(nodeFilename);
		FileReader edgeStrReader = new FileReader(edgeFilename);

		return convert(nodeStrReader, edgeStrReader);
	}

	public FunctionDefBase convert(Reader nodeStrReader, Reader edgeStrReader)
			throws IOException, InvalidCSVFile
	{
		KeyedCSVReader nodeReader = new KeyedCSVReader();
		KeyedCSVReader edgeReader = new KeyedCSVReader();
		nodeReader.init(nodeStrReader);
		edgeReader.init(edgeStrReader);

		csv.csv2ast.CSVAST csvAST = new csv.csv2ast.CSVAST();
		while( nodeReader.hasNextRow())
			csvAST.addNodeRow(nodeReader.getNextRow());
		while( edgeReader.hasNextRow())
			csvAST.addEdgeRow(edgeReader.getNextRow());

		return convert(csvAST);
	}

	public FunctionDefBase convert(CSVAST csvAST)
			throws IOException, InvalidCSVFile
	{
		ASTUnderConstruction ast = new ASTUnderConstruction();

		createASTNodes(csvAST, ast);
		createASTEdges(csvAST, ast);

		return ast.getRootNode();
	}

	protected void createASTNodes(CSVAST csvAST, ASTUnderConstruction ast) throws InvalidCSVFile
	{
		Iterator<KeyedCSVRow> nodeRows = csvAST.nodeIterator();
		KeyedCSVRow keyedRow = getFirstKeyedRow(nodeRows);

		// first row must be a function type;
		// otherwise we cannot create a function node
		if( !PHPCSVNodeTypes.funcTypes.contains(keyedRow.getFieldForKey(PHPCSVNodeTypes.TYPE)))
			throw new InvalidCSVFile( "Type of first row is not a function declaration.");

		createASTForFunction(ast, nodeRows, keyedRow);
	}

	protected KeyedCSVRow getFirstKeyedRow(Iterator<KeyedCSVRow> nodeRows) throws InvalidCSVFile {
		try {
			return nodeRows.next();
		}
		catch( NoSuchElementException ex) {
			throw new InvalidCSVFile( "Empty CSV files are not permissible.");
		}

	}

	protected void createASTForFunction(ASTUnderConstruction ast, Iterator<KeyedCSVRow> nodeRows, KeyedCSVRow keyedRow)
			throws InvalidCSVFile
	{
		FunctionDefBase root = (FunctionDefBase) ast.getNodeById( nodeInterpreter.handle(keyedRow, ast));
		ast.setRootNode(root);

		while (nodeRows.hasNext())
		{
			keyedRow = nodeRows.next();
			nodeInterpreter.handle(keyedRow, ast);
		}
	}

	private void createASTEdges(CSVAST csvAST, ASTUnderConstruction ast) throws InvalidCSVFile
	{
		Iterator<KeyedCSVRow> edgeRows = csvAST.edgeIterator();
		KeyedCSVRow keyedRow;

		while (edgeRows.hasNext())
		{
			keyedRow = edgeRows.next();
			edgeInterpreter.handle(keyedRow, ast);
		}
	}

	public void setInterpreters(CSVRowInterpreter nodeInterpreter, CSVRowInterpreter edgeInterpreter)
	{
		this.nodeInterpreter = nodeInterpreter;
		this.edgeInterpreter = edgeInterpreter;
	}

}
