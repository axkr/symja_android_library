package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.Abs;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Cosh;
import static org.matheclipse.core.expression.F.Cot;
import static org.matheclipse.core.expression.F.Csc;
import static org.matheclipse.core.expression.F.Im;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Re;
import static org.matheclipse.core.expression.F.Sec;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Sinh;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Tan;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.integer;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.VisitorExpr;

/**
 * <pre>
 * ComplexExpand(expr)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * get the expanded <code>expr</code>. All variable symbols in <code>expr</code> are assumed to be non complex numbers.
 * </p>
 * </blockquote>
 * <p>
 * See:<br />
 * </p>
 * <ul>
 * <li><a href="http://en.wikipedia.org/wiki/List_of_trigonometric_identities">Wikipedia - List of trigonometric
 * identities</a></li>
 * </ul>
 * <h3>Examples</h3>
 * 
 * <pre>
 * &gt;&gt; ComplexExpand(Sin(x+I*y))
 * Cosh(y)*Sin(x)+I*Cos(x)*Sinh(y)
 * </pre>
 */
public class ComplexExpand extends AbstractEvaluator {

	public ComplexExpand() {

	}

	static class ComplexExpandVisitor extends VisitorExpr {
		final EvalEngine fEngine;

		public ComplexExpandVisitor(EvalEngine engine) {
			super();
			fEngine = engine;
		}

		@Override
		public IExpr visit(IASTMutable ast) {
			if (ast.isTimes()) {
				IExpr expanded = F.evalExpand(ast);
				if (expanded.isPlus()) {
					return F.ComplexExpand.of(fEngine, expanded);
				}
			}
			if (ast.isPower() && ast.base().isNegative() //
					&& ast.exponent().isRational()) {
				IExpr base = ast.base();
				if (base.isInteger()) {
					IExpr exponent = ast.exponent();
					// ((base^2)^(exponent/2))
					IExpr coeff = F.Power(F.Power(base, F.C2), F.C1D2.times(exponent));
					// exponent*Arg(base)
					IExpr inner = exponent.times(F.Arg(base));
					// coeff*Cos(inner) + I*coeff*Sin(inner);
					IExpr temp = F.Expand.of(fEngine, F.Plus(F.Times(coeff, F.Cos(inner)), F.Times(F.CI, coeff, F.Sin(inner))));
					return temp;
				}
			}
			return super.visit(ast);
		}

		@Override
		public IExpr visit2(IExpr head, IExpr arg1) {
			IExpr x = arg1;
			IExpr result = arg1.accept(this);
			if (result.isPresent()) {
				x = result;
			}
			IExpr reX = Re(x);
			IExpr imX = Im(x);
			if (x.isSymbol()) {
				if (head.equals(Re)) {
					return x;
				}
				if (head.equals(Im)) {
					return F.C0;
				}
				reX = x;
				imX = F.C0;
			} else {
				reX = x.re();
				imX = x.im();
				IExpr temp = complexExpandNull(reX, fEngine);
				if (temp.isPresent()) {
					reX = temp;
				}
				temp = complexExpandNull(imX, fEngine);
				if (temp.isPresent()) {
					imX = temp;
				}
			}

			if (head.equals(Abs)) {
				// Sqrt[reX^2 + imX^2]
				return complexExpand(Sqrt(Plus(Power(reX, C2), Power(imX, C2))), fEngine);
			}
			if (head.equals(Cos)) {
				// Cosh[Im[x]]*Cos[Re[x]]+I*Sinh[Im[x]]*Sin[Re[x]]
				return Plus(Times(Cos(reX), Cosh(imX)), Times(CI, Sin(reX), Sinh(imX)));
			}
			if (head.equals(Cot)) {
				// -(Sin[2*Re[x]]/(Cos[2*Re[x]]-Cosh[2*Im[x]]))+(I*Sinh[2*Im[x]])/(Cos[2*Re[x]]-Cosh[2*Im[x]])
				return Plus(
						Times(CN1, Sin(Times(C2, reX)),
								Power(Plus(Cos(Times(C2, reX)), Negate(Cosh(Times(C2, imX)))), CN1)),
						Times(CI, Sinh(Times(C2, imX)),
								Power(Plus(Cos(Times(C2, reX)), Negate(Cosh(Times(C2, imX)))), CN1)));
			}
			if (head.equals(Csc)) {
				// (-2 Cosh[Im[x]] Sin[Re[x]])/(Cos[2 Re[x]] - Cosh[2 Im[x]]) +
				// ((2 I) Cos[Re[x]] Sinh[Im[x]])/(Cos[2 Re[x]]-Cosh[2
				// Im[x]])
				return Plus(
						Times(integer(-2L), Cosh(imX), Sin(reX),
								Power(Plus(Cos(Times(C2, reX)), Times(CN1, Cosh(Times(C2, imX)))), CN1)),
						Times(C2, CI, Cos(reX), Sinh(imX),
								Power(Plus(Cos(Times(C2, reX)), Times(CN1, Cosh(Times(C2, imX)))), CN1)));
			}
			if (head.equals(Sec)) {
				// (2 Cos[Re[x]] Cosh[Im[x]])/(Cos[2 Re[x]] + Cosh[2 Im[x]]) +
				// ((2 I) Sin[Re[x]] Sinh[Im[x]])/(Cos[2 Re[x]] + Cosh[2
				// Im[x]])
				return Plus(Times(C2, Cos(reX), Cosh(imX), Power(Plus(Cos(Times(C2, reX)), Cosh(Times(C2, imX))), CN1)),
						Times(C2, CI, Sin(reX), Sinh(imX),
								Power(Plus(Cos(Times(C2, reX)), Cosh(Times(C2, imX))), CN1)));
			}
			if (head.equals(Sin)) {
				// Cosh[Im[x]]*Sin[Re[x]]+I*Sinh[Im[x]]*Cos[Re[x]]
				return Plus(Times(Cosh(imX), Sin(reX)), Times(CI, Sinh(imX), Cos(reX)));
			}
			if (head.equals(Tan)) {
				// Sin[2*Re[x]]/(Cos[2*Re[x]] + Cosh[2*Im[x]]) +
				// (I*Sinh[2*Im[x]])/(Cos[2*Re[x]] + Cosh[2*Im[x]])
				return Plus(Times(Sin(Times(C2, reX)), Power(Plus(Cos(Times(C2, reX)), Cosh(Times(C2, imX))), CN1)),
						Times(CI, Sinh(Times(C2, imX)), Power(Plus(Cos(Times(C2, reX)), Cosh(Times(C2, imX))), CN1)));
			}
			if (result.isPresent()) {
				return F.unaryAST1(head, result);
			}
			return F.NIL;
		}
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 1, 2);

		return complexExpand(ast.arg1(), engine);
	}

	private static IExpr complexExpand(IExpr arg1, EvalEngine engine) {
		return complexExpandNull(arg1, engine).orElse(arg1);
	}

	private static IExpr complexExpandNull(IExpr arg1, EvalEngine engine) {
		ComplexExpandVisitor tteVisitor = new ComplexExpandVisitor(engine);
		return arg1.accept(tteVisitor);
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE);
	}

}
