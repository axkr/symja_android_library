package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.data.DatasetExpr;
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
			if (ast.isAST1() && ast.arg1().isList()) {
				if (((IAST) ast.arg1()).forAll(x -> x.isAssociation())) {
					// return DataSetExpr.newInstance((IAST) ast.arg1());
				}
			}
			if (ast.head() instanceof DatasetExpr) {
				DatasetExpr dataSet = (DatasetExpr) ast.head();
				IExpr arg1 = ast.arg1();
				try {
					if (ast.isAST1()) {
						return dataSet.select(arg1, F.All);
					}
					if (ast.isAST2()) {
						IExpr arg2 = ast.arg2();
						IExpr result = dataSet.select(arg1, arg2);
						if (result.isPresent()) {
							return result;
						}
						if (!arg1.equals(F.All)) {
							if (arg1.isBuiltInSymbol()) {
								IExpr expr = dataSet.select(F.All, arg2);
								if (expr instanceof DatasetExpr) {
									return F.unaryAST1(arg1, ((DatasetExpr) expr).normal());
								}
							} else {
//								IExpr normal = dataSet.normal();
								IExpr expr = engine.evaluate(F.unaryAST1(arg1, dataSet));
								if (expr instanceof DatasetExpr) {
									return ((DatasetExpr) expr).select(F.All, arg2);
								} else if (expr.isList() && ((IAST) expr).forAll(x -> x.isAssociation())) {
//								  return DataSetExpr.newInstance((IAST) ast.arg1());
								}
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
