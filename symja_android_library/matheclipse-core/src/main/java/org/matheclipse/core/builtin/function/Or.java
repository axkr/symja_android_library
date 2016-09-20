package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * 
 * See <a href="http://en.wikipedia.org/wiki/Logical_disjunction">Logical
 * disjunction</a>
 * 
 */
public class Or extends AbstractCoreFunctionEvaluator {

	public Or() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (ast.isAST0()) {
			return F.False;
		}

		boolean evaled = false;
		IAST flattenedAST = EvalAttributes.flatten(ast);
		if (flattenedAST.isPresent()) {
			evaled = true;
		} else {
			flattenedAST = ast;
		}

		IAST result = flattenedAST.clone();
		IExpr temp;
		IExpr sym;
		int[] symbols = new int[flattenedAST.size()];
		int[] notSymbols = new int[flattenedAST.size()];
		int index = 1;

		for (int i = 1; i < flattenedAST.size(); i++) {
			temp = flattenedAST.get(i);
			if (temp.isTrue()) {
				return F.True;
			}
			if (temp.isFalse()) {
				result.remove(index);
				evaled = true;
				continue;
			}

			temp = engine.evaluateNull(flattenedAST.get(i));
			if (temp.isPresent()) {
				if (temp.isTrue()) {
					return F.True;
				}
				if (temp.isFalse()) {
					result.remove(index);
					evaled = true;
					continue;
				}
				result.set(index, temp);
				evaled = true;
			} else {
				temp = flattenedAST.get(i);
			}

			if (temp.isSymbol()) {
				symbols[i] = flattenedAST.get(i).hashCode();
			} else if (temp.isNot()) {
				sym = ((IAST) temp).getAt(1);
				if (sym.isSymbol()) {
					notSymbols[i] = sym.hashCode();
				}
			}
			index++;
		}
		for (int i = 1; i < symbols.length; i++) {
			if (symbols[i] != 0) {
				for (int j = 1; j < notSymbols.length; j++) {
					if (i != j && symbols[i] == notSymbols[j] && (result.equalsAt(i, result.get(j).getAt(1)))) {
						// Or[a, Not[a]] => True
						return F.True;
					}
				}
			}
		}
		if (result.isAST1()) {
			return result.arg1();
		}
		if (evaled) {
			if (result.isAST0()) {
				return F.False;
			}
			return result;
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.ONEIDENTITY | ISymbol.FLAT);
	}
}