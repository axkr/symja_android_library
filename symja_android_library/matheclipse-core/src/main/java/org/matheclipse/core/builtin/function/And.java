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
 * 
 * See <a href="http://en.wikipedia.org/wiki/Logical_conjunction">Logical
 * conjunction</a>
 * 
 * <p>
 * See the online Symja function reference: <a href=
 * "https://bitbucket.org/axelclk/symja_android_library/wiki/Symbols/And">And
 * </a>
 * </p>
 */
public class And extends AbstractCoreFunctionEvaluator {
	public And() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (ast.size() == 1) {
			return F.True;
		}

		boolean evaled = false;

		int index = 1;
		IExpr temp;
		IExpr sym;

		IAST flattenedAST = EvalAttributes.flatten(ast);
		if (flattenedAST.isPresent()) {
			evaled = true;
		} else {
			flattenedAST = ast;
		}

		IAST result = flattenedAST.clone();
		int[] symbols = new int[flattenedAST.size()];
		int[] notSymbols = new int[flattenedAST.size()];
		for (int i = 1; i < flattenedAST.size(); i++) {
			temp = flattenedAST.get(i);
			if (temp.isFalse()) {
				return F.False;
			}
			if (temp.isTrue()) {
				result.remove(index);
				evaled = true;
				continue;
			}

			temp = engine.evaluateNull(temp);
			if (temp.isPresent()) {
				if (temp.isFalse()) {
					return F.False;
				}
				if (temp.isTrue()) {
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
						// And[a, Not[a]] => True
						return F.False;
					}
				}
			}
		}
		if (result.size() == 2) {
			return result.arg1();
		}
		if (evaled) {
			if (result.size() == 1) {
				return F.True;
			}

			return result;
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL | ISymbol.ONEIDENTITY | ISymbol.FLAT);
	}
}
