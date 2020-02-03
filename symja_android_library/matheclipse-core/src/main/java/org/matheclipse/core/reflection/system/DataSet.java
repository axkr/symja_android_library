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
public class DataSet extends AbstractEvaluator {

	public DataSet() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (Config.isFileSystemEnabled(engine)) {
			if (ast.head() instanceof DataSetExpr) {
				DataSetExpr dataSet = (DataSetExpr) ast.head();
				IExpr arg1 = ast.arg1();
				try {
					if (ast.isAST2()) {
						IExpr arg2 = ast.arg2();
						if (arg1.equals(F.All)) {
							if (arg2.isList()) {
								IAST list = (IAST) arg2;
								return dataSet.retainColumns(list);
							}
							int columnPosition = arg2.toIntDefault();
							if (columnPosition > 0) {
								return dataSet.retainColumns(columnPosition);
							}
						}
					}
				} catch (RuntimeException rex) {
					return engine.printMessage("DataSet: " + rex.getMessage());
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
