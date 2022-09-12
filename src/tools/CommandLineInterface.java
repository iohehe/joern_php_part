package tools;

import org.apache.commons.cli.ParseException;


import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;


public class CommandLineInterface
{
	protected Options options = new Options();
	protected CommandLineParser parser = new BasicParser();
	protected HelpFormatter formatter = new HelpFormatter();
	protected CommandLine cmd = null;

	String nodeFile;
	String edgeFile;

	public String getNodeFile()
	{
		return nodeFile;
	}

	public String getEdgeFile()
	{
		return edgeFile;
	}

	public void printHelp()
	{
		formatter.printHelp("importer <nodes.csv> <edges.csv> ...", options);
	}

	public void parseCommandLine(String[] args) throws ParseException
	{
		if (args.length != 2)
			throw new RuntimeException("Please supply a node and an edge file");

		cmd = parser.parse(options, args);

		String[] arguments = cmd.getArgs();
		nodeFile = arguments[0];
		edgeFile = arguments[1];
	}

}
