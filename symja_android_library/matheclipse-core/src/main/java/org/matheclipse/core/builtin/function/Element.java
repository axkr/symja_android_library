package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.util.AbstractAssumptions;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Element(x, domain) - test if x is in the given domain.
 * 
 */
public class Element extends AbstractCoreFunctionEvaluator {

	public Element() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);

		final IExpr arg2 = F.eval(ast.arg2());
		ISymbol truthValue;
		if (arg2.isSymbol()) {
			final IExpr arg1 = F.eval(ast.arg1());
			if (arg1.isAST(F.Alternatives)) {
				IAST list = (IAST) arg1;
				for (int i = 1; i < list.size(); i++) {
					truthValue = assumeDomain(arg1, (ISymbol) arg2);
					if (truthValue != null) {
						return truthValue;
					}
				}
				return F.True;
			} else {
				return assumeDomain(arg1, (ISymbol) arg2);
			}
		}
		return null;
	}

	private ISymbol assumeDomain(final IExpr arg1, final ISymbol arg2) {
		if (arg2.equals(F.Algebraics)) {
			ISymbol truthValue = AbstractAssumptions.assumeAlgebraic(arg1);
			if (truthValue != null) {
				return truthValue;
			}
		} else if (arg2.equals(F.Booleans)) {
			ISymbol truthValue = AbstractAssumptions.assumeBoolean(arg1);
			if (truthValue != null) {
				return truthValue;
			}
		} else if (arg2.equals(F.Complexes)) {
			ISymbol truthValue = AbstractAssumptions.assumeComplex(arg1);
			if (truthValue != null) {
				return truthValue;
			}
		} else if (arg2.equals(F.Integers)) {
			ISymbol truthValue = AbstractAssumptions.assumeInteger(arg1);
			if (truthValue != null) {
				return truthValue;
			}
		} else if (arg2.equals(F.Primes)) {
			ISymbol truthValue = AbstractAssumptions.assumePrime(arg1);
			if (truthValue != null) {
				return truthValue;
			}
		} else if (arg2.equals(F.Rationals)) {
			ISymbol truthValue = AbstractAssumptions.assumeRational(arg1);
			if (truthValue != null) {
				return truthValue;
			}
		} else if (arg2.equals(F.Reals)) {
			ISymbol truthValue = AbstractAssumptions.assumeReal(arg1);
			if (truthValue != null) {
				return truthValue;
			}
		}
		return null;
	}

}