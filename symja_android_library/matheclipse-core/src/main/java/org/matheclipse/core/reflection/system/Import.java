package org.matheclipse.core.reflection.system;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset; 

import javax.imageio.ImageIO;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.matheclipse.core.basic.Config;
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
import org.matheclipse.core.io.Filename;
import org.matheclipse.core.io.ImageFormat;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.SyntaxError;
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
		if (Config.isFileSystemEnabled(engine)) {
			Validate.checkRange(ast, 2, 3);

			if (!(ast.arg1() instanceof IStringX)) {
				throw new WrongNumberOfArguments(ast, 1, ast.argSize());
			}

			IStringX arg1 = (IStringX) ast.arg1();
			String fileName = arg1.toString();
			String format = "String";
			if (ast.size() > 2) {
				if (!(ast.arg2() instanceof IStringX)) {
					throw new WrongNumberOfArguments(ast, 2, ast.argSize());
				}
				format = ((IStringX) ast.arg2()).toString();
			}
			FileReader reader = null;

			try {

				if (format.equals("Table")) {
					reader = new FileReader(fileName);
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
				} else if (format.equals("String")) {
					File file = new File(fileName);
					return of(file, engine);
				}

			} catch (IOException ioe) {
				engine.printMessage("Import: file " + fileName + " not found!");
			} catch (SyntaxError se) {
				engine.printMessage("Import: file " + fileName + " syntax error!");
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
					}
				}
			}
		}
		return F.NIL;
	}

	public static IExpr of(File file, EvalEngine engine) throws IOException {
		Filename filename = new Filename(file);
		if (filename.hasExtension("jpg") || filename.hasExtension("png")) {
			return ImageFormat.from(ImageIO.read(file));
		}
		AST2Expr ast2Expr = new AST2Expr(engine.isRelaxedSyntax(), engine);
		final Parser parser = new Parser(engine.isRelaxedSyntax(), true);
		String str = com.google.common.io.Files.toString(file, Charset.defaultCharset());
		final ASTNode node = parser.parse(str);
		return ast2Expr.convert(node);
	}

}
