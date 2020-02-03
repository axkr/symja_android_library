package org.matheclipse.core.reflection.system;

import java.io.IOException;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.data.DataSetExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.io.Extension;
import org.matheclipse.parser.client.SyntaxError;

import tech.tablesaw.api.Table;

/**
 * Import semantic data into a DataSet
 *
 */
public class SemanticImport extends AbstractEvaluator {

	public SemanticImport() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (Config.isFileSystemEnabled(engine)) {
			if (!(ast.arg1() instanceof IStringX)) {
				return F.NIL;
			}

			IStringX arg1 = (IStringX) ast.arg1();
			Extension format = Extension.importFilename(arg1.toString());
			String fileName = arg1.toString();
			if (format.equals(Extension.CSV)) {
				try {
					Table table = Table.read().csv(arg1.toString());

					// System.out.println(table.printAll());
					// System.out.println(table.structure().printAll());
					return DataSetExpr.newInstance(table);

				} catch (IOException ioe) {
					return engine.printMessage("SemanticImport: file " + fileName + " not found!");
				} catch (RuntimeException rex) {
					return engine.printMessage("SemanticImport: file " + fileName + " - " + rex.getMessage());
				} finally {
				}
			}
		}
		return F.NIL;
	}

	public int[] expectedArgSize() {
		return IOFunctions.ARGS_1_2;
	}

}
