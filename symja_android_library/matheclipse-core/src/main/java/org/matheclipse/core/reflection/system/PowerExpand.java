package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.Arg;
import static org.matheclipse.core.expression.F.Assumptions;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.Divide;
import static org.matheclipse.core.expression.F.E;
import static org.matheclipse.core.expression.F.Floor;
import static org.matheclipse.core.expression.F.I;
import static org.matheclipse.core.expression.F.Im;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Null;
import static org.matheclipse.core.expression.F.Pi;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.VisitorExpr;
import org.matheclipse.parser.client.SyntaxError;

/**
 * Expand the powers of a given expression.
 */
public class PowerExpand extends AbstractFunctionEvaluator {

	private static class PowerExpandVisitor extends VisitorExpr {
		final boolean assumptions;

		public PowerExpandVisitor(boolean assumptions) {
			super();
			this.assumptions = assumptions;
		}

		/** {@inheritDoc} */
		@Override
		public IExpr visit2(IExpr head, IExpr arg1) {
			boolean evaled = false;
			IExpr x1 = arg1;
			IExpr result = arg1.accept(this);
			if (result != null) {
				evaled = true;
				x1 = result;
			}
			if (head.equals(Log)) {
				if (x1.isPower()) {
					IAST powerAST = (IAST) x1;
					// Log[x_ ^ y_] :> y * Log(x)
					IAST logResult = Times(powerAST.arg2(), Log(powerAST.arg1()));
					if (assumptions) {
						IAST floorResult = Floor(Divide(Subtract(Pi, Im(logResult)), Times(C2, Pi)));
						IAST timesResult = Times(C2, I, Pi, floorResult);
						return Plus(logResult, timesResult);
					}
					return logResult;
				}
			}
			if (evaled) {
				return $(head, x1);
			}
			return null;
		}

		/** {@inheritDoc} */
		@Override
		public IExpr visit3(IExpr head, IExpr arg1, IExpr arg2) {
			boolean evaled = false;
			IExpr x1 = arg1;
			IExpr result = arg1.accept(this);
			if (result != null) {
				evaled = true;
				x1 = result;
			}
			IExpr x2 = arg2;
			result = arg2.accept(this);
			if (result != null) {
				evaled = true;
				x2 = result;
			}
			if (head.equals(Power)) {
				if (x1.isTimes()) {
					// Power[x_ * y_, z_] :> x^z * y^z
					IAST timesAST = (IAST) x1;
					IAST timesResult = timesAST.mapAt(Power(Null, x2), 1);
					if (assumptions) {
						IAST plusResult = Plus(C1D2);
						for (int i = 1; i < timesAST.size(); i++) {
							plusResult.add(Negate(Divide(Arg(timesAST.get(i)), Times(C2, Pi))));
						}
						IAST expResult = Power(E, Times(C2, I, Pi, x2, Floor(plusResult)));
						timesResult.add(expResult);
						return timesResult;
					}
					return timesResult;
				}
				if (x1.isPower()) {
					// Power[x_ ^ y_, z_] :> x ^(y*z)
					IAST powerAST = (IAST) x1;
					IAST powerResult = Power(powerAST.arg1(), Times(powerAST.arg2(), x2));
					if (assumptions) {
						IAST floorResult = Floor(Divide(Subtract(Pi, Im(Times(powerAST.arg2(), Log(powerAST.arg1())))),
								Times(C2, Pi)));
						IAST expResult = Power(E, Times(C2, I, Pi, x2, floorResult));
						IAST timesResult = Times(powerResult, expResult);
						return timesResult;
					}
					return powerResult;
				}
			}
			if (evaled) {
				return $(head, x1, x2);
			}
			return null;
		}
	}

	public PowerExpand() {
	}

	/** {@inheritDoc} */
	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 2, 3);
		if (ast.arg1().isAST()) {
			boolean assumptions = false;
			if (ast.size() == 3) {
				final Options options = new Options(ast.topHead(), ast, ast.size() - 1);
				IExpr option = options.getOption(Assumptions);
				if (option != null && option.isTrue()) {
					// found "Assumptions -> True"
					assumptions = true;
				}
			}

			return powerExpand((IAST)ast.arg1(), assumptions);

		}
		return ast.arg1();
	}

	public static IExpr powerExpand(final IAST ast, boolean assumptions) {
		PowerExpandVisitor pweVisitor = new PowerExpandVisitor(assumptions);
		IExpr result = ast.accept(pweVisitor);
		if (result != null) {
			return result;
		}
		return ast;
	}

	/** {@inheritDoc} */
	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE);
		super.setUp(symbol);
	}
}
