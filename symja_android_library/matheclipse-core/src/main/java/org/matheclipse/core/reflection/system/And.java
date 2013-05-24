package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * 
 * 
 * See <a href="http://en.wikipedia.org/wiki/Logical_conjunction">Logical
 * conjunction</a>
 * 
 */
public class And extends AbstractFunctionEvaluator {
	public And() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 3);

		boolean evaled = false;
		final EvalEngine engine = EvalEngine.get();
		IAST result = ast.clone();
		int index = 1;
		IExpr temp;
		IExpr sym;
		int[] symbols = new int[ast.size()];
		int[] notSymbols = new int[ast.size()];

		for (int i = 1; i < ast.size(); i++) {
			temp = engine.evaluateNull(ast.get(i));
			if (temp == null) {
				temp = ast.get(i);
			} else {
				result.set(index, temp);
				evaled = true;
			}
			if (temp.isFalse()) {
				return F.False;
			}
			if (temp.isTrue()) {
				result.remove(index);
				evaled = true;
				continue;
			}
			if (temp.isSymbol()) {
				symbols[i] = ast.get(i).hashCode();
				continue;
			}
			if (temp.isNot()) {
				sym = ((IAST) temp).getAt(1);
				if (sym.isSymbol()) {
					notSymbols[i] = sym.hashCode();
				}
				continue;
			}
			index++;
		}
		for (int i = 1; i < symbols.length; i++) {
			if (symbols[i] != 0) {
				for (int j = 1; j < notSymbols.length; j++) {
					if (i != j && symbols[i] == notSymbols[j] && (result.get(i).equals(result.get(j).getAt(1)))) {
						// And[a, Not[a]] => True
						return F.False;
					}
				}
			}
		}
		if (evaled) {
			if (result.size() == 1) {
				return F.True;
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
