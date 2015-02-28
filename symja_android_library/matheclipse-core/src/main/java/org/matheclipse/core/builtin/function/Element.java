package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.ISignedNumberConstant;
import org.matheclipse.core.eval.util.AbstractAssumptions;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
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
			if (arg2.equals(F.Algebraics)) {
				if (arg1.isNumber()) {
					return F.True;
				}
				if (arg1.isSymbol()) {
					if (((ISymbol) arg1).getEvaluator() instanceof ISignedNumberConstant) {
						return F.True;
					}
				}
				if (AbstractAssumptions.assumeAlgebraic(arg1)) {
					return F.True;
				}
			} else if (arg2.equals(F.Booleans)) {
				if (arg1.isTrue() || arg1.isFalse()) {
					return F.True;
				}
				if (arg1.isNumber()) {
					return F.False;
				}
				if (AbstractAssumptions.assumeBoolean(arg1)) {
					return F.True;
				}
			} else if (arg2.equals(F.Complexes)) {
				if (arg1.isNumber()) {
					return F.True;
				}
				if (arg1.isSymbol()) {
					if (((ISymbol) arg1).getEvaluator() instanceof ISignedNumberConstant) {
						return F.True;
					}
				}
				if (AbstractAssumptions.assumeComplex(arg1)) {
					return F.True;
				}
			} else if (arg2.equals(F.Integers)) {
				if (arg1.isInteger()) {
					return F.True;
				}
				if (arg1.isNumber()) {
					return F.False;
				}
				if (AbstractAssumptions.assumeInteger(arg1)) {
					return F.True;
				}
			} else if (arg2.equals(F.Primes)) {
				if (arg1.isInteger() && ((IInteger) arg1).isProbablePrime()) {
					return F.True;
				}
				if (arg1.isNumber()) {
					return F.False;
				}
				if (AbstractAssumptions.assumePrime(arg1)) {
					return F.True;
				}
			} else if (arg2.equals(F.Rationals)) {
				if (arg1.isRational()) {
					return F.True;
				}
				if (arg1.isNumber()) {
					return F.False;
				}
				if (AbstractAssumptions.assumeRational(arg1)) {
					return F.True;
				}
			} else if (arg2.equals(F.Reals)) {
				if (arg1.isSignedNumber()) {
					return F.True;
				}
				if (arg1.isNumber()) {
					return F.False;
				}
				if (arg1.isSymbol()) {
					if (((ISymbol) arg1).getEvaluator() instanceof ISignedNumberConstant) {
						return F.True;
					}
				}
				if (AbstractAssumptions.assumeReal(arg1)) {
					return F.True;
				}
			}
		}
		return null;
	}

}