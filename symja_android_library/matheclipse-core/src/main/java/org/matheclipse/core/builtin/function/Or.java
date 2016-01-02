package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
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
public class Or extends AbstractFunctionEvaluator {

	public Or() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (ast.size() == 1) {
			return F.False;
		}

		IExpr temp;
		IExpr sym;
		int[] symbols = new int[ast.size()];
		int[] notSymbols = new int[ast.size()];

		boolean evaled = false;
		IAST result = ast.clone();
		int index = 1;
		for (int i = 1; i < ast.size(); i++) {
			temp = ast.get(i);
			if (temp.isTrue()) {
				return F.True;
			}
			if (temp.isFalse()) {
				result.remove(index);
				evaled = true;
				continue;
			}

			temp = engine.evaluateNull(ast.get(i));
			if (temp == null) {
				temp = ast.get(i);
			} else {
				result.set(index, temp);
				evaled = true;
			}
			if (temp.isTrue()) {
				return F.True;
			}
			if (temp.isFalse()) {
				result.remove(index);
				evaled = true;
				continue;
			}
			if (temp.isSymbol()) {
				symbols[i] = ast.get(i).hashCode();
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
		if (evaled) {
			if (result.size() == 1) {
				return F.False;
			}
			return result;
		}
		return null;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL | ISymbol.ONEIDENTITY | ISymbol.FLAT);
	}
}