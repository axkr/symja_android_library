package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.List;

import java.util.ArrayList;
import java.util.List;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.NoEvalException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.Iterator;
import org.matheclipse.core.eval.util.TableGenerator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.UnaryArrayFunction;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Table structure generator (i.e. lists, vectors, matrices, tensors)
 */
public class Table extends AbstractFunctionEvaluator {

	public Table() {
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 3);

		return evaluateTable(ast, List(), List());
	}

	/**
	 * Generate a table from standard iterator notation.
	 * 
	 * @param ast
	 * @param resultList
	 *            the result list to which the generated expressions should be appended.
	 * @param defaultValue
	 *            the default value used in the iterator
	 * @return <code>null</code> if no evaluation is possible
	 */
	protected static IExpr evaluateTable(final IAST ast, final IAST resultList, IExpr defaultValue) {
		try {
			if (ast.size() > 2) {
				final EvalEngine engine = EvalEngine.get();
				final List<Iterator> iterList = new ArrayList<Iterator>();
				for (int i = 2; i < ast.size(); i++) {
					iterList.add(new Iterator((IAST) ast.get(i), engine));
				}

				final TableGenerator generator = new TableGenerator(iterList, resultList,
						new UnaryArrayFunction(engine, ast.arg1()), defaultValue);
				return generator.table();
			}
		} catch (final ClassCastException e) {
			// the iterators are generated only from IASTs
		} catch (final NoEvalException e) {
		}
		return null;
	}

	/**
	 * Evaluate only the last iterator in <code>ast</code> (i.e. <code>ast.get(ast.size() - 1)</code>) for <code>Sum()</code> or
	 * <code>Product()</code> function calls.
	 * 
	 * @param ast
	 * @param resultList
	 *            the result list to which the generated expressions should be appended.
	 * @param defaultValue
	 *            the default value used in the iterator
	 * @return <code>null</code> if no evaluation is possible
	 * @see Product
	 * @see Sum
	 */
	protected static IExpr evaluateLast(final IExpr arg1, final Iterator iter, final IAST resultList, IExpr defaultValue) {
		try {
			final List<Iterator> iterList = new ArrayList<Iterator>();
			iterList.add(iter);

			final TableGenerator generator = new TableGenerator(iterList, resultList,
					new UnaryArrayFunction(EvalEngine.get(), arg1), defaultValue);
			return generator.table();
		} catch (final ClassCastException e) {
			// the iterators are generated only from IASTs
		} catch (final NoEvalException e) {
		}
		return null;
	}

	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}

	/**
	 * Determine all local variables of the iterators starting with index <code>2</code>.
	 * 
	 * @param ast
	 * @return
	 */
	public IAST determineIteratorVariables(final IAST ast) {
		IAST variableList = F.List();
		for (int i = 2; i < ast.size(); i++) {
			if (ast.get(i).isVariable()) {
				variableList.add(ast.get(i));
			} else {
				if (ast.get(i).isList()) {
					IAST list = (IAST) ast.get(i);
					if (list.size() >= 2) {
						if (list.arg1().isVariable()) {
							variableList.add(list.arg1());
						}
					}
				}
			}
		}
		return variableList;
	}

	/**
	 * Disable the <code>Reap() and Sow()</code> mode temporary and evaluate an expression for the given &quot;local variables
	 * list&quot;. If evaluation is not possible return the input object.
	 * 
	 * @param expr
	 *            the expression which should be evaluated
	 * @param localVariablesList
	 *            a list of symbols which should be used as local variables inside the block
	 * @return the evaluated object
	 */
	public static IExpr evalBlockWithoutReap(IExpr expr, IAST localVariablesList) {
		EvalEngine engine = EvalEngine.get();
		IAST reapList = engine.getReapList();
		try {
			engine.setReapList(null);
			return engine.evalBlock(expr, localVariablesList);
		} catch (RuntimeException rex) {
			//
		} finally {
			engine.setReapList(reapList);
		}
		return expr;
	}
}
