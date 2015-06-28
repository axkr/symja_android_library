package org.matheclipse.core.builtin.function;

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
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);

		final IExpr arg2 = F.eval(ast.arg2());
		if (arg2.isSymbol()) {
			final IExpr arg1 = F.eval(ast.arg1());
			if (arg1.isAST(F.Alternatives)) {
				IAST list = (IAST) arg1;
				for (int i = 1; i < list.size(); i++) {
					if (!assumeDomain(arg1, (ISymbol) arg2)) {
						return null;
					}
				}
				return F.True;
			} else if (assumeDomain(arg1, (ISymbol) arg2)) {
				return F.True;
			}
			return null;
		}
		return null;
	}

	private boolean assumeDomain(final IExpr arg1, final ISymbol arg2) {
		if (arg2.equals(F.Algebraics)) {
			if (AbstractAssumptions.assumeAlgebraic(arg1)) {
				return true;
			}
		} else if (arg2.equals(F.Booleans)) {
			if (AbstractAssumptions.assumeBoolean(arg1)) {
				return true;
			}
		} else if (arg2.equals(F.Complexes)) {
			if (AbstractAssumptions.assumeComplex(arg1)) {
				return true;
			}
		} else if (arg2.equals(F.Integers)) {
			if (AbstractAssumptions.assumeInteger(arg1)) {
				return true;
			}
		} else if (arg2.equals(F.Primes)) {
			if (AbstractAssumptions.assumePrime(arg1)) {
				return true;
			}
		} else if (arg2.equals(F.Rationals)) {
			if (AbstractAssumptions.assumeRational(arg1)) {
				return true;
			}
		} else if (arg2.equals(F.Reals)) {
			if (AbstractAssumptions.assumeReal(arg1)) {
				return true;
			}
		}
		return false;
	}

}