package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.List;

import java.util.ArrayList;
import java.util.List;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.Iterator;
import org.matheclipse.core.expression.AST;
import org.matheclipse.core.generic.UnaryArrayFunction;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.generic.nested.TableGenerator;

/**
 * Table structure generator (i.e. lists, vectors, matrices, tensors)
 */
public class Table implements IFunctionEvaluator {

	public Table() {
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 3);

		return evaluateTable(ast, List(), null);
	}

	/**
	 * Generate a table.
	 * 
	 * @param ast
	 *            an AST with at least 3 arguments
	 * @param resultList
	 * @param defaultValue
	 * @return
	 */
	protected static IExpr evaluateTable(final IAST ast, final IAST resultList, IExpr defaultValue) {
		try {
			final EvalEngine engine = EvalEngine.get();
			final List<Iterator> iterList = new ArrayList<Iterator>();
			for (int i = 2; i < ast.size(); i++) {
				iterList.add(new Iterator((IAST) ast.get(i), engine));
			}

			final TableGenerator<IExpr, IAST> generator = new TableGenerator<IExpr, IAST>(iterList, resultList,
					new UnaryArrayFunction(engine, ast.get(1)), AST.COPY, defaultValue);
			return generator.table();

		} catch (final ClassCastException e) {
			// the iterators are generated only from IASTs
		}
		return null;
	}

	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}
}
