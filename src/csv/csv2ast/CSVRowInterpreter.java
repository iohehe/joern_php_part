package csv.csv2ast;

import csv.KeyedCSV.KeyedCSVRow;
import csv.KeyedCSV.exceptions.InvalidCSVFile;

public interface CSVRowInterpreter
{
	public long handle(KeyedCSVRow row, ASTUnderConstruction ast) throws InvalidCSVFile;
}
