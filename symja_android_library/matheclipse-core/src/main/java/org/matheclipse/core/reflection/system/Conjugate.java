package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Conjugate the given argument.
 * 
 * See <a href="http://en.wikipedia.org/wiki/Complex_conjugation">Wikipedia:Complex conjugation</a>
 */
public class Conjugate extends AbstractEvaluator implements INumeric {

	public Conjugate() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		IExpr arg1 = ast.arg1();
		IExpr temp = conjugate(arg1);
		if (temp != null) {
			return temp;
		}
		if (arg1.isPlus() || arg1.isTimes()) {
			IAST result = null;
			IAST clone = ((IAST) arg1).clone();
			int i = 1;
			while (i < clone.size()) {
				temp = conjugate(clone.get(i));
				if (temp != null) {
					clone.remove(i);
					if (result == null) {
						result = ((IAST) arg1).copyHead();
					}
					result.add(temp);
					continue;
				}
				i++;
			}
			if (result != null) {
				if (clone.size() == 1) {
					return result;
				}
				if (clone.size() == 1) {
					result.add(F.Conjugate(clone.arg1()));
					return result;
				}
				result.add(F.Conjugate(clone));
				return result;
			}
		}
		return null;
	}

	/**
	 * Conjugate numbers and special objects like <code>Infinity</code> and <code>Indeterminate</code>.
	 * 
	 * @param arg1
	 * @return <code>null</code> if the evaluation wasn't possible
	 */
	private IExpr conjugate(IExpr arg1) {
		if (arg1.isNumber()) {
			return ((INumber) arg1).conjugate();
		}
		if (arg1.isRealResult()) {
			return arg1;
		}
		if (arg1.isDirectedInfinity()) {
			IAST directedInfininty = (IAST) arg1;
			if (directedInfininty.isComplexInfinity()) {
				return F.CComplexInfinity;
			}
			if (directedInfininty.size() == 2) {
				if (directedInfininty.isInfinity()) {
					return F.CInfinity;
				}
				IExpr conjug = F.eval(F.Conjugate(directedInfininty.arg1()));
				return F.Times(conjug, F.CInfinity);
			}
		}
		if (arg1.isIndeterminate()) {
			return F.Indeterminate;
		}
		return null;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE);
	}

	@Override
	public double evalReal(final double[] stack, final int top, final int size) {
		if (size != 1) {
			throw new UnsupportedOperationException();
		}
		return stack[top];
	}
}
