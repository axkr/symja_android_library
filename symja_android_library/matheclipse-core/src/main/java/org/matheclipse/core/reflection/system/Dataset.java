package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.data.DataSetExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import jdk.nashorn.internal.ir.ExpressionStatement;

/**
 * Import semantic data into a DataSet
 *
 */
public class Dataset extends AbstractEvaluator {

	public Dataset() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (Config.isFileSystemEnabled(engine)) {
			if (ast.head() instanceof DataSetExpr) {
				DataSetExpr dataSet = (DataSetExpr) ast.head();
				IExpr arg1 = ast.arg1();
				try {
					if (ast.isAST1()) {
						return dataSet.select(arg1, F.All);
					}
					if (ast.isAST2()) {
						IExpr arg2 = ast.arg2();
						IExpr result= dataSet.select(arg1, arg2);
						if (result.isPresent()) {
							return result;
						}
						if (ast.arg2().isString() && !arg1.equals(F.All)) {
							IExpr expr = dataSet.select(F.All, arg2);
							if (expr instanceof DataSetExpr) {
								return F.unaryAST1(arg1, ((DataSetExpr) expr).normal());
							}
						}
						
					}
				} catch (RuntimeException rex) {
					return engine.printMessage("Dataset: " + rex.getMessage());
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
