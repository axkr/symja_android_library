package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;

/**
 * Norm of a given argument
 */
public class Norm extends AbstractEvaluator {

	public Norm() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);

		IExpr arg1 = ast.arg1();
		int dim = arg1.isVector();
		if (dim > (-1)) {
			if (dim == 0) {
				return F.NIL;
			}
			if (ast.isAST2()) {
				IExpr arg2 = ast.arg2();
				if (arg2.isInfinity()) {
					return ((IAST) arg1).map(F.Max, Functors.replaceAll(F.Abs(F.Null), F.Null));
				} else {
					if (arg2.isSymbol() || arg2.isSignedNumber()) {
						if (arg2.isZero()) {
							throw new WrongArgumentType(ast, ast.get(2), 2, "0 not allowed as second argument!");
						}
						if (arg2.isSignedNumber() && arg2.lessThan(F.C1).isTrue()) {
							throw new WrongArgumentType(ast, ast.get(2), 2, "Second argument is < 1!");
						}
						return F.Power(
								((IAST) arg1).map(F.Plus, Functors.replaceAll(F.Power(F.Abs(F.Null), arg2), F.Null)),
								arg2.inverse());
					}
				}
				return F.NIL;
			}
			return F.Sqrt(((IAST) arg1).map(F.Plus, Functors.replaceAll(F.Sqr(F.Abs(F.Null)), F.Null)));
		}
		if (arg1.isNumber()) {
			if (ast.isAST2()) {
				return F.NIL;
			}
			// absolute Value of a number
			return ((INumber) arg1).abs();
		}
		if (arg1.isNumericFunction()) {
			if (ast.isAST2()) {
				return F.NIL;
			}
			// absolute Value
			return F.Abs(arg1);
		}
		return F.NIL;
	}

}
