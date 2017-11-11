package org.matheclipse.core.reflection.system;

import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongNumberOfArguments;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.ast.ASTNode;

/**
 * Import some data from file system.
 *
 */
public class Import extends AbstractEvaluator {

	public Import() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);

		if (!(ast.arg1() instanceof IStringX)) {
			throw new WrongNumberOfArguments(ast, 1, ast.size() - 1);
		}
		if (!(ast.arg2() instanceof IStringX)) {
			throw new WrongNumberOfArguments(ast, 2, ast.size() - 1);
		}
		IStringX arg1 = (IStringX) ast.arg1();
		IStringX arg2 = (IStringX) ast.arg2();
		FileReader reader = null;
		try {
			reader = new FileReader(arg1.toString());
			if (arg2.contentEquals("Table")) {
				AST2Expr ast2Expr = new AST2Expr(engine.isRelaxedSyntax(), engine);
				final Parser parser = new Parser(engine.isRelaxedSyntax(), true);

				CSVFormat csvFormat = CSVFormat.RFC4180.withDelimiter(' ');
				Iterable<CSVRecord> records = csvFormat.parse(reader);
				IASTAppendable rowList = F.ListAlloc(256);
				for (CSVRecord record : records) {
					IASTAppendable columnList = F.ListAlloc(record.size());
					for (String string : record) {
						final ASTNode node = parser.parse(string);
						IExpr temp = ast2Expr.convert(node);
						columnList.append(temp);
					}
					rowList.append(columnList);
				}
				return rowList;
			}

		} catch (IOException ioe) {
			engine.printMessage("Import: file " + arg1.toString() + " not found!");
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}

		return F.NIL;
	}

}
