package org.matheclipse.core.reflection.system;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;

import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongNumberOfArguments;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.ast.ASTNode;

/**
 * Export some data from file system.
 *
 */
public class Export extends AbstractEvaluator {

	public Export() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 3, 4);

		if (!(ast.arg1() instanceof IStringX)) {
			throw new WrongNumberOfArguments(ast, 1, ast.size() - 1);
		}
		String format = "Data";
		if (ast.size() == 4) {
			if (!(ast.arg3() instanceof IStringX)) {
				throw new WrongNumberOfArguments(ast, 3, ast.size() - 1);
			}
			format = ((IStringX) ast.arg3()).toString();
		}
		IStringX arg1 = (IStringX) ast.arg1();
		IExpr arg2 = ast.arg2();
		FileWriter writer = null;
		try {
			writer = new FileWriter(arg1.toString());
			if (format.equals("Table")) {
				int[] dims = arg2.isMatrix();
				if (dims != null) {
					for (int j = 0; j < dims[0]; j++) {
						IAST rowList = (IAST) arg2.getAt(j + 1);
						for (int i = 1; i <= dims[1]; i++) {
							if (rowList.get(i).isSignedNumber()) {
								writer.append(rowList.get(i).toString());
							} else {
								writer.append("\"");
								writer.append(rowList.get(i).toString());
								writer.append("\"");
							}
							if (i < dims[1]) {
								writer.append(" ");
							}
						}
						writer.append("\n");
					}
					return arg1;
				} else {
					if (arg2.isList()) {

					}
				}
			} else if (format.equals("Data")) {
				File file = new File(arg1.toString());
				com.google.common.io.Files.write(arg2.toString(), file, Charset.defaultCharset());
				return arg1;
			}

		} catch (IOException ioe) {
			engine.printMessage("Export: file " + arg1.toString() + " not found!");
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
				}
			}
		}

		return F.NIL;
	}

}
