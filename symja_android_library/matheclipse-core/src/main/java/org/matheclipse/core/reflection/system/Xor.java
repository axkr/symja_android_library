package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * 
 * See <a href="https://en.wikipedia.org/wiki/Exclusive_or">Wikipedia: Exclusive
 * or</a>
 * 
 */
public class Xor extends AbstractFunctionEvaluator {
	public Xor() {
		// default ctor
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (ast.size() == 1) {
			return F.False;
		}
		if (ast.size() == 2) {
			return ast.arg1();
		}

		IExpr temp;
		IExpr result = ast.arg1();
		boolean evaled = false;
		for (int i = 2; i < ast.size(); i++) {
			temp = ast.get(i);
			if (temp.isTrue()) {
				if (result.isTrue()) {
					result = F.False;
				} else if (result.isFalse()) {
					result = F.True;
				} else {
					result = F.eval(F.Not(result));
				}
				evaled = true;
			} else if (temp.isFalse()) {
				if (result.isTrue()) {
					result = F.True;
				} else if (result.isFalse()) {
					result = F.False;
				}
				evaled = true;
			} else {
				if (temp.equals(result)) {
					result = F.False;
					evaled = true;
				} else {
					if (result.isTrue()) {
						result = F.eval(F.Not(result));
						evaled = true;
					} else if (result.isFalse()) {
						result = temp;
						evaled = true;
					} else {
						if (evaled) {
							IAST xor = F.ast(F.Xor, ast.size() - i + 1, false);
							xor.append(result);
							xor.append(temp);
							for (int j = i + 1; j < ast.size(); j++) {
								xor.append(ast.get(j));
							}
							return xor;
						}
						return F.NIL;
					}
				}
			}
		}

		return result;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.ORDERLESS | ISymbol.ONEIDENTITY | ISymbol.FLAT);
	}
}
