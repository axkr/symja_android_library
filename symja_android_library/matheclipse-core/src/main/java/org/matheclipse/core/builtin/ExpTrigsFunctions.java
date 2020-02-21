package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.F.ArcCot;
import static org.matheclipse.core.expression.F.ArcCoth;
import static org.matheclipse.core.expression.F.ArcCsc;
import static org.matheclipse.core.expression.F.ArcCsch;
import static org.matheclipse.core.expression.F.ArcSin;
import static org.matheclipse.core.expression.F.ArcSinh;
import static org.matheclipse.core.expression.F.ArcTan;
import static org.matheclipse.core.expression.F.ArcTanh;
import static org.matheclipse.core.expression.F.CD1;
import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.CNI;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Cosh;
import static org.matheclipse.core.expression.F.Cot;
import static org.matheclipse.core.expression.F.Coth;
import static org.matheclipse.core.expression.F.Csc;
import static org.matheclipse.core.expression.F.Csch;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Pi;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sec;
import static org.matheclipse.core.expression.F.Sech;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Sinc;
import static org.matheclipse.core.expression.F.Sinh;
import static org.matheclipse.core.expression.F.Tan;
import static org.matheclipse.core.expression.F.Tanh;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.num;

import java.util.function.DoubleUnaryOperator;

import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.hipparchus.complex.Complex;
import org.hipparchus.util.FastMath;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ComplexResultException;
import org.matheclipse.core.eval.interfaces.AbstractArg1;
import org.matheclipse.core.eval.interfaces.AbstractArg12;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.eval.interfaces.IRewrite;
import org.matheclipse.core.eval.util.AbstractAssumptions;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.IntervalSym;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.rules.ArcCosRules;
import org.matheclipse.core.reflection.system.rules.ArcCoshRules;
import org.matheclipse.core.reflection.system.rules.ArcCotRules;
import org.matheclipse.core.reflection.system.rules.ArcCothRules;
import org.matheclipse.core.reflection.system.rules.ArcCscRules;
import org.matheclipse.core.reflection.system.rules.ArcCschRules;
import org.matheclipse.core.reflection.system.rules.ArcSecRules;
import org.matheclipse.core.reflection.system.rules.ArcSechRules;
import org.matheclipse.core.reflection.system.rules.ArcSinRules;
import org.matheclipse.core.reflection.system.rules.ArcSinhRules;
import org.matheclipse.core.reflection.system.rules.ArcTanRules;
import org.matheclipse.core.reflection.system.rules.ArcTanhRules;
import org.matheclipse.core.reflection.system.rules.CosRules;
import org.matheclipse.core.reflection.system.rules.CoshRules;
import org.matheclipse.core.reflection.system.rules.CotRules;
import org.matheclipse.core.reflection.system.rules.CothRules;
import org.matheclipse.core.reflection.system.rules.CscRules;
import org.matheclipse.core.reflection.system.rules.CschRules;
import org.matheclipse.core.reflection.system.rules.LogRules;
import org.matheclipse.core.reflection.system.rules.SecRules;
import org.matheclipse.core.reflection.system.rules.SechRules;
import org.matheclipse.core.reflection.system.rules.SinRules;
import org.matheclipse.core.reflection.system.rules.SincRules;
import org.matheclipse.core.reflection.system.rules.SinhRules;
import org.matheclipse.core.reflection.system.rules.TanRules;
import org.matheclipse.core.reflection.system.rules.TanhRules;

public class ExpTrigsFunctions {
	/**
	 * 
	 * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation in static
	 * initializer</a>
	 */
	private static class Initializer {

		private static void init() {
			F.AngleVector.setEvaluator(new AngleVector());
			F.ArcCos.setEvaluator(new ArcCos());
			F.ArcCosh.setEvaluator(new ArcCosh());
			F.ArcCot.setEvaluator(new ArcCot());
			F.ArcCoth.setEvaluator(new ArcCoth());
			F.ArcCsc.setEvaluator(new ArcCsc());
			F.ArcCsch.setEvaluator(new ArcCsch());
			F.ArcSec.setEvaluator(new ArcSec());
			F.ArcSech.setEvaluator(new ArcSech());
			F.ArcSin.setEvaluator(new ArcSin());
			F.ArcSinh.setEvaluator(new ArcSinh());
			F.ArcTan.setEvaluator(new ArcTan());
			F.ArcTanh.setEvaluator(new ArcTanh());
			F.CirclePoints.setEvaluator(new CirclePoints());
			F.Cos.setEvaluator(new Cos());
			F.Cosh.setEvaluator(new Cosh());
			F.Cot.setEvaluator(new Cot());
			F.Coth.setEvaluator(new Coth());
			F.Csc.setEvaluator(new Csc());
			F.Csch.setEvaluator(new Csch());
			F.Exp.setEvaluator(new Exp());
			F.Haversine.setEvaluator(new Haversine());
			F.InverseHaversine.setEvaluator(new InverseHaversine());
			F.Log.setEvaluator(new Log());
			F.LogisticSigmoid.setEvaluator(new LogisticSigmoid());
			F.Log10.setEvaluator(new Log10());
			F.Log2.setEvaluator(new Log2());
			F.Sec.setEvaluator(new Sec());
			F.Sech.setEvaluator(new Sech());
			F.Sin.setEvaluator(new Sin());
			F.Sinc.setEvaluator(new Sinc());
			F.Sinh.setEvaluator(new Sinh());
			F.Tan.setEvaluator(new Tan());
			F.Tanh.setEvaluator(new Tanh());

		}
	}

	private final static class AngleVector extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr arg1 = ast.first();
			IExpr phi;
			if (ast.isAST2()) {
				IExpr arg2 = ast.second();

				if (arg1.isAST(F.List, 3)) {
					IExpr x = arg1.first();
					IExpr y = arg1.second();
					if (arg2.isAST(F.List, 3)) {
						// AngleVector({x_, y_}, {r_, phi_}) := {x + r * Cos(phi), y + r * Sin(phi)}
						IExpr r = arg2.first();
						phi = arg2.second();
						return F.List(F.Plus(x, F.Times(r, F.Cos(phi))), F.Plus(y, F.Times(r, F.Sin(phi))));
					} else {
						phi = arg2;
					}
					// AngleVector({x_, y_}, phi_) := {x + Cos(phi), y + Sin(phi)}
					return F.List(F.Plus(x, F.Cos(phi)), F.Plus(y, F.Sin(phi)));
				}
				return F.NIL;
			}

			if (arg1.isAST(F.List, 3)) {
				// AngleVector({r_, phi_}) := {r * Cos(phi), r * Sin(phi)}
				IExpr r = ((IAST) arg1).arg1();
				phi = ((IAST) arg1).arg2();
				return F.List(F.Times(r, F.Cos(phi)), F.Times(r, F.Sin(phi)));
			} else {
				phi = arg1;
			}
			// AngleVector(phi_) := {Cos(phi), Sin(phi)}
			return F.List(F.Cos(phi), F.Sin(phi));
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_2;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			// don't set NUMERICFUNCTION
		}

	}

	/**
	 * Arccosine
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Inverse_trigonometric functions" > Inverse_trigonometric functions</a>
	 */
	private final static class ArcCos extends AbstractTrigArg1
			implements INumeric, IRewrite, ArcCosRules, DoubleUnaryOperator {

		@Override
		public double applyAsDouble(double operand) {
			double val = Math.acos(operand);
			if (Double.isNaN(val)) {
				throw new ComplexResultException("ArcCos(NaN)");
			}
			return val;
		}

		@Override
		public IExpr e1ApcomplexArg(Apcomplex arg1) {
			return F.complexNum(ApcomplexMath.acos(arg1));
		}

		@Override
		public IExpr e1ApfloatArg(Apfloat arg1) {
			return F.num(ApfloatMath.acos(arg1));
		}

		@Override
		public IExpr e1ComplexArg(final Complex arg1) {
			return F.complexNum(arg1.acos());
		}

		@Override
		public IExpr e1DblArg(final double arg1) {
			double val = Math.acos(arg1);
			if (Double.isNaN(val)) {
				return F.complexNum(Complex.valueOf(arg1).acos());
			}
			return F.num(val);

		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			return Math.acos(stack[top]);
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
			// don't check negative argument here - doesn't work with Rubi/MMA pattern matching
			// IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1, false);
			// if (negExpr.isPresent()) {
			// return Subtract( Pi , F.ArcCos(negExpr));
			// }

			if (arg1.isCos() && arg1.first().isPositive()) {
				// ArcCos(Cos(z))
				return arcCosCos(arg1);
			}
			if (arg1.isInterval()) {
				return IntervalSym.arccos((IAST) arg1);
			}
			return F.NIL;
		}

		@Override
		public IAST getRuleAST() {
			return RULES;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}

		public IExpr rewriteLog(IExpr arg1, EvalEngine engine) {
			return
			// [$ Pi/2 + I*Log(I*arg1 + Sqrt(1 - arg1^2)) $]
			F.Plus(F.CPiHalf, F.Times(F.CI, F.Log(F.Plus(F.Times(F.CI, arg1), F.Sqrt(F.Subtract(F.C1, F.Sqr(arg1))))))); // $$;
		}
	}

	/**
	 * Inverse hyperbolic cosine
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Inverse_hyperbolic_function"> Inverse hyperbolic functions</a>
	 */
	private final static class ArcCosh extends AbstractTrigArg1
			implements INumeric, IRewrite, ArcCoshRules, DoubleUnaryOperator {

		@Override
		public double applyAsDouble(double operand) {
			return FastMath.acosh(operand);
		}

		@Override
		public IExpr e1ApcomplexArg(Apcomplex arg1) {
			return F.complexNum(ApcomplexMath.acosh(arg1));
		}

		@Override
		public IExpr e1ComplexArg(final Complex arg1) {
			double re = arg1.getReal();
			double im = arg1.getImaginary();
			Complex temp = new Complex(re * re - im * im - 1, 2 * re * im).sqrt();
			return F.complexNum(new Complex(temp.getReal() + re, temp.getImaginary() + im).log());
		}

		@Override
		public IExpr e1ApfloatArg(Apfloat arg1) {
			return F.num(ApfloatMath.acosh(arg1));
		}

		@Override
		public IExpr e1DblArg(final double arg1) {
			double val = FastMath.acosh(arg1);
			if (Double.isNaN(val)) {
				return e1ComplexArg(new Complex(arg1));
			}
			return F.num(val);

		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			return FastMath.acosh(stack[top]);
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
			if (arg1.isInterval()) {
				return IntervalSym.arccosh((IAST) arg1);
			}
			return F.NIL;
		}

		@Override
		public IAST getRuleAST() {
			return RULES;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}

		public IExpr rewriteLog(IExpr arg1, EvalEngine engine) {
			return
			// [$ Log(arg1 + Sqrt(-1 + arg1)*Sqrt(1 + arg1)) $]
			F.Log(F.Plus(arg1, F.Times(F.Sqrt(F.Plus(F.CN1, arg1)), F.Sqrt(F.Plus(F.C1, arg1))))); // $$;
		}

	}

	/**
	 * Arccotangent
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Inverse_trigonometric functions" > Inverse_trigonometric functions</a>
	 */
	private final static class ArcCot extends AbstractTrigArg1 implements IRewrite, ArcCotRules, DoubleUnaryOperator {

		@Override
		public double applyAsDouble(double operand) {
			if (F.isZero(operand)) {
				// Pi / 2
				return Math.PI / 2.0;
			}
			return Math.atan(1 / operand);
		}

		@Override
		public IExpr e1ApcomplexArg(Apcomplex arg1) {
			// I/arg1
			Apcomplex ac = Apcomplex.I.divide(arg1);

			// (I/2) (Log(1 - I/arg1) - Log(1 + I/arg1))
			Apcomplex result = Apcomplex.I.divide(new Apfloat(2)).multiply(
					ApcomplexMath.log(Apcomplex.ONE.subtract(ac)).subtract(ApcomplexMath.log(Apcomplex.ONE.add(ac))));
			return F.complexNum(result);
		}

		@Override
		public IExpr e1ApfloatArg(Apfloat arg1) {
			if (arg1.equals(Apcomplex.ZERO)) {
				// Pi / 2
				return F.num(ApfloatMath.pi(arg1.precision()).divide(new Apfloat(2)));
			}
			return F.num(ApfloatMath.atan(ApfloatMath.inverseRoot(arg1, 1)));
		}

		@Override
		public IExpr e1ComplexArg(final Complex arg1) {
			// I/arg1
			Complex c = Complex.I.divide(arg1);

			// (I/2) (Log(1 - I/arg1) - Log(1 + I/arg1))
			Complex result = Complex.I.divide(new Complex(2.0))
					.multiply(Complex.ONE.subtract(c).log().subtract(Complex.ONE.add(c).log()));
			return F.complexNum(result);
		}

		@Override
		public IExpr e1DblArg(final double arg1) {
			if (F.isZero(arg1)) {
				// Pi / 2
				return F.num(Math.PI / 2.0);
			}
			return F.num(Math.atan(1 / arg1));
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Plus(Negate(ArcCot(negExpr)));
			}
			IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
			if (imPart.isPresent()) {
				return F.Times(F.CNI, F.ArcCoth(imPart));
			}
			if (arg1.isAST(F.Cot, 2) && arg1.first().isPositive()) {
				// ArcCot(Cot(z))
				return arcTanArcCotInverse(arg1);
			}

			if (arg1.isInterval()) {
				return IntervalSym.arccot((IAST) arg1);
			}
			return F.NIL;
		}

		@Override
		public IAST getRuleAST() {
			return RULES;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}

		public IExpr rewriteLog(IExpr arg1, EvalEngine engine) {
			return
			// [$ (1/2)*I*Log(1 - I/arg1) - (1/2)*I*Log(1 + I/arg1) $]
			F.Plus(F.Times(F.C1D2, F.CI, F.Log(F.Plus(F.C1, F.Times(F.CNI, F.Power(arg1, F.CN1))))),
					F.Times(F.CN1D2, F.CI, F.Log(F.Plus(F.C1, F.Times(F.CI, F.Power(arg1, F.CN1)))))); // $$;
		}
	}

	/**
	 * Arccotangent hyperbolic
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Inverse_hyperbolic_function"> Inverse hyperbolic functions</a>
	 */
	private final static class ArcCoth extends AbstractTrigArg1 implements IRewrite, ArcCothRules, DoubleUnaryOperator {

		@Override
		public double applyAsDouble(double operand) {
			if (F.isZero(operand)) {
				throw new ComplexResultException("ArcCoth(0)");
			}
			double c = 1.0 / operand;
			return (Math.log(1.0 + c) - Math.log(1.0 - c)) / 2.0;
		}

		@Override
		public IExpr e1ApcomplexArg(Apcomplex arg1) {
			// 1/arg1
			Apcomplex c = ApcomplexMath.inverseRoot(arg1, 1);

			// (1/2) (Log(1 + 1/arg1) - Log(1 - 1/arg1))
			Apcomplex result = ApcomplexMath.log(Apcomplex.ONE.add(c))
					.subtract(ApcomplexMath.log(Apcomplex.ONE.subtract(c))).divide(new Apfloat(2));
			return F.complexNum(result);
		}

		@Override
		public IExpr e1ApfloatArg(Apfloat arg1) {
			if (arg1.equals(Apcomplex.ZERO)) {
				// I*Pi / 2
				return F.complexNum(
						new Apcomplex(Apcomplex.ZERO, ApfloatMath.pi(arg1.precision())).divide(new Apfloat(2)));
			}
			return F.num(ApfloatMath.atanh(ApfloatMath.inverseRoot(arg1, 1)));
		}

		@Override
		public IExpr e1ComplexArg(final Complex arg1) {
			// 1/arg1
			Complex c = arg1.reciprocal();

			// (1/2) (Log(1 + 1/arg1) - Log(1 - 1/arg1))
			Complex result = new Complex(0.5)
					.multiply(Complex.ONE.add(c).log().subtract(Complex.ONE.subtract(c).log()));
			return F.complexNum(result);
		}

		@Override
		public IExpr e1DblArg(final double arg1) {
			if (F.isZero(arg1)) {
				// I*Pi / 2
				return F.complexNum(new Complex(0.0, Math.PI).divide(new Complex(2.0)));
			}
			if (F.isZero(arg1)) {
				return e1ComplexArg(new Complex(arg1));
			}
			double c = 1.0 / arg1;
			double val = (Math.log(1.0 + c) - Math.log(1.0 - c)) / 2.0;
			if (Double.isNaN(val)) {
				return e1ComplexArg(new Complex(arg1));
			}
			return F.num(val);
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Negate(ArcCoth(negExpr));
			}
			IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
			if (imPart.isPresent()) {
				return F.Times(F.CNI, F.ArcCot(imPart));
			}
			return F.NIL;
		}

		@Override
		public IAST getRuleAST() {
			return RULES;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}

		public IExpr rewriteLog(IExpr arg1, EvalEngine engine) {
			return
			// [$ (-(1/2))*Log(1 - 1/arg1) + (1/2)*Log(1 + 1/arg1) $]
			F.Plus(F.Times(F.CN1D2, F.Log(F.Subtract(F.C1, F.Power(arg1, F.CN1)))),
					F.Times(F.C1D2, F.Log(F.Plus(F.C1, F.Power(arg1, F.CN1))))); // $$;
		}
	}

	/**
	 * Inverse hyperbolic tangent
	 * 
	 * See <a href="https://en.wikipedia.org/wiki/Inverse_trigonometric_functions">Inverse trigonometric functions</a>
	 */
	private final static class ArcCsc extends AbstractTrigArg1 implements IRewrite, ArcCscRules {
		@Override
		public IExpr e1ComplexArg(final Complex arg1) {
			if (arg1.equals(Complex.ZERO)) {
				return F.CComplexInfinity;
			}
			return F.complexNum(Complex.ONE.divide(arg1).asin());
		}

		@Override
		public IExpr e1DblArg(final double arg1) {
			if (F.isZero(arg1)) {
				return F.CComplexInfinity;
			}
			return F.num(Math.asin(1 / arg1));
		}

		@Override
		public IAST getRuleAST() {
			return RULES;
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
			IExpr imPart = AbstractFunctionEvaluator.getComplexExpr(arg1, F.CNI);
			if (imPart.isPresent()) {
				return F.Times(F.CNI, F.ArcCsch(imPart));
			}
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Negate(ArcCsc(negExpr));
			}
			// IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
			// if (imPart.isPresent()) {
			// return F.Times(F.CNI, F.ArcCsch(imPart));
			// }
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}

		public IExpr rewriteLog(IExpr arg1, EvalEngine engine) {
			return
			// [$ (-I)*Log(Sqrt(1 - 1/arg1^2) + I/arg1) $]
			F.Times(F.CNI,
					F.Log(F.Plus(F.Sqrt(F.Subtract(F.C1, F.Power(arg1, F.CN2))), F.Times(F.CI, F.Power(arg1, F.CN1))))); // $$;
		}
	}

	/**
	 * Inverse hyperbolic tangent
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Inverse_hyperbolic_function"> Inverse hyperbolic functions</a>
	 */
	private final static class ArcCsch extends AbstractTrigArg1 implements IRewrite, ArcCschRules {

		@Override
		public IAST getRuleAST() {
			return RULES;
		}

		@Override
		public IExpr e1DblArg(final double d) {
			// log(1+Sqrt(1+d^2) / d)
			return F.num(Math.log((1 + Math.sqrt(1 + d * d)) / d));
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
			IExpr imPart = AbstractFunctionEvaluator.getComplexExpr(arg1, F.CI);
			if (imPart.isPresent()) {
				return F.Times(F.CI, F.ArcCsc(imPart));
			}
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Negate(ArcCsch(negExpr));
			}
			// IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
			// if (imPart.isPresent()) {
			// return F.Times(F.CNI, F.ArcCsc(imPart));
			// }
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}

		public IExpr rewriteLog(IExpr arg1, EvalEngine engine) {
			return
			// [$ Log(Sqrt(1 + 1/arg1^2) + 1/arg1) $]
			F.Log(F.Plus(F.Sqrt(F.Plus(F.C1, F.Power(arg1, F.CN2))), F.Power(arg1, F.CN1))); // $$;
		}
	}

	/**
	 * Inverse hyperbolic secant
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Inverse_hyperbolic_function"> Inverse hyperbolic functions</a>
	 */
	private final static class ArcSech extends AbstractTrigArg1 implements IRewrite, ArcSechRules {

		@Override
		public IAST getRuleAST() {
			return RULES;
		}

		@Override
		public IExpr e1DblArg(final double d) {
			// log(1+Sqrt(1-d^2) / d)
			if (F.isZero(d)) {
				return F.Indeterminate;
			}
			return F.num(Math.log((1 + Math.sqrt(1 - d * d)) / d));
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}

		public IExpr rewriteLog(IExpr arg1, EvalEngine engine) {
			return
			// [$ Log(Sqrt(-1 + 1/arg1)*Sqrt(1 + 1/arg1) + 1/arg1) $]
			F.Log(F.Plus(
					F.Times(F.Sqrt(F.Plus(F.CN1, F.Power(arg1, F.CN1))), F.Sqrt(F.Plus(F.C1, F.Power(arg1, F.CN1)))),
					F.Power(arg1, F.CN1))); // $$;
		}

	}

	private final static class ArcSec extends AbstractTrigArg1 implements IRewrite, ArcSecRules {
		@Override
		public IExpr e1ComplexArg(final Complex arg1) {
			if (arg1.equals(Complex.ZERO)) {
				return F.CComplexInfinity;
			}
			return F.complexNum(Complex.ONE.divide(arg1).acos());
		}

		@Override
		public IExpr e1DblArg(final double arg1) {
			if (F.isZero(arg1)) {
				return F.CComplexInfinity;
			}
			return F.num(Math.acos(1 / arg1));
		}

		@Override
		public IAST getRuleAST() {
			return RULES;
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}

		public IExpr rewriteLog(IExpr arg1, EvalEngine engine) {
			return
			// [$ Pi/2 + I*Log(Sqrt(1 - 1/arg1^2) + I/arg1) $]
			F.Plus(F.CPiHalf, F.Times(F.CI, F
					.Log(F.Plus(F.Sqrt(F.Subtract(F.C1, F.Power(arg1, F.CN2))), F.Times(F.CI, F.Power(arg1, F.CN1)))))); // $$;
		}
	}

	/**
	 * Arcsine
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Inverse_trigonometric functions" > Inverse_trigonometric functions</a>
	 */
	private final static class ArcSin extends AbstractTrigArg1
			implements INumeric, IRewrite, ArcSinRules, DoubleUnaryOperator {

		@Override
		public double applyAsDouble(double operand) {
			double val = Math.asin(operand);
			if (Double.isNaN(val)) {
				throw new ComplexResultException("");
			}
			return val;
		}

		@Override
		public IExpr e1ApcomplexArg(Apcomplex arg1) {
			return F.complexNum(ApcomplexMath.asin(arg1));
		}

		@Override
		public IExpr e1ApfloatArg(Apfloat arg1) {
			try {
				return F.num(ApfloatMath.asin(arg1));
			} catch (ArithmeticException ae) {
				return F.complexNum(ApcomplexMath.asin(new Apcomplex(arg1, Apcomplex.ZERO)));
			}
		}

		@Override
		public IExpr e1ComplexArg(final Complex arg1) {
			return F.complexNum(arg1.asin());
		}

		@Override
		public IExpr e1DblArg(final double arg1) {
			double val = Math.asin(arg1);
			if (Double.isNaN(val)) {
				return F.complexNum(Complex.valueOf(arg1).asin());
			}
			return F.num(val);
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			return Math.asin(stack[top]);
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
			IExpr imPart = AbstractFunctionEvaluator.getComplexExpr(arg1, F.CNI);
			if (imPart.isPresent()) {
				return F.Times(F.CI, F.ArcSinh(imPart));
			}
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Negate(ArcSin(negExpr));
			}
			// IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
			// if (imPart.isPresent()) {
			// return F.Times(F.CI, F.ArcSinh(imPart));
			// }
			if (arg1.isSin() && arg1.first().isPositive()) {
				// ArcSin(Sin(z))
				return arcSinSin(arg1);
			}

			if (arg1.isInterval()) {
				return IntervalSym.arcsin((IAST) arg1);
			}
			return F.NIL;
		}

		@Override
		public IAST getRuleAST() {
			return RULES;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}

		public IExpr rewriteLog(IExpr arg1, EvalEngine engine) {
			return
			// [$ -I*Log(I*arg1+Sqrt(1-arg1^2)) $]
			F.Times(F.CNI, F.Log(F.Plus(F.Times(F.CI, arg1), F.Sqrt(F.Subtract(F.C1, F.Sqr(arg1)))))); // $$;
		}
	}

	/**
	 * Arcsin hyperbolic
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Inverse_hyperbolic_function"> Inverse hyperbolic functions</a>
	 */
	private final static class ArcSinh extends AbstractTrigArg1
			implements INumeric, IRewrite, ArcSinhRules, DoubleUnaryOperator {

		@Override
		public double applyAsDouble(double operand) {
			return FastMath.asinh(operand);
		}

		@Override
		public IExpr e1ApcomplexArg(Apcomplex arg1) {
			return F.complexNum(ApcomplexMath.asinh(arg1));
		}

		@Override
		public IExpr e1ApfloatArg(Apfloat arg1) {
			return F.num(ApfloatMath.asinh(arg1));
		}

		@Override
		public IExpr e1DblArg(final double arg1) {
			double val = FastMath.asinh(arg1);
			return F.num(val);
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			return FastMath.asinh(stack[top]);
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
			if (arg1.isInterval()) {
				return IntervalSym.arcsinh((IAST) arg1);
			}
			IExpr imPart = AbstractFunctionEvaluator.getComplexExpr(arg1, F.CNI);
			if (imPart.isPresent()) {
				return F.Times(F.CI, F.ArcSin(imPart));
			}
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Negate(ArcSinh(negExpr));
			}
			// IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
			// if (imPart.isPresent()) {
			// return F.Times(F.CI, F.ArcSin(imPart));
			// }
			return F.NIL;
		}

		@Override
		public IAST getRuleAST() {
			return RULES;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}

		public IExpr rewriteLog(IExpr arg1, EvalEngine engine) {
			return
			// [$ Log(arg1 + Sqrt(1 + arg1^2)) $]
			F.Log(F.Plus(arg1, F.Sqrt(F.Plus(F.C1, F.Sqr(arg1))))); // $$;
		}
	}

	/**
	 * Arctangent
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Inverse_trigonometric functions" > Inverse_trigonometric functions</a>
	 */
	private final static class ArcTan extends AbstractArg12 implements INumeric, IRewrite, ArcTanRules {

		@Override
		public IAST getRuleAST() {
			return RULES;
		}

		@Override
		public IExpr e1ObjArg(final IExpr arg1) {
			IExpr imPart = AbstractFunctionEvaluator.getComplexExpr(arg1, F.CNI);
			if (imPart.isPresent()) {
				return F.Times(F.CI, F.ArcTanh(imPart));
			}
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Negate(ArcTan(negExpr));
			}
			// IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
			// if (imPart.isPresent()) {
			// return F.Times(F.CI, F.ArcTanh(imPart));
			// }
			if (arg1.isTan() && arg1.first().isPositive()) {
				// ArcTan(Tan(z))
				return arcTanArcCotInverse(arg1);
			}

			if (arg1.isInterval()) {
				return IntervalSym.arctan((IAST) arg1);
			}
			return F.NIL;
		}

		@Override
		public IExpr e2ObjArg(final IExpr x, final IExpr y) {
			if (x.isZero() && y.isRealResult()) {
				if (y.isZero()) {
					return F.Indeterminate;
				}
				if (y.isPositiveResult()) {
					return F.CPiHalf;
				}
				if (y.isNegativeResult()) {
					return F.CNPiHalf;
				}
				return F.NIL;
			}
			if (y.isZero() && x.isNumericFunction() && !x.isZero()) {
				return F.Times(F.Subtract(F.C1, x.unitStep()), F.Pi);
			}
			IExpr yUnitStep = y.unitStep();
			if (x.isNumericFunction() && yUnitStep.isInteger()) {
				if (x.re().isNegative()) {
					return F.Plus(F.ArcTan(F.Divide(y, x)), F.Times(F.Subtract(F.Times(F.C2, yUnitStep), F.C1), F.Pi));
				}
				IExpr argX = x.complexArg();
				// -Pi/2 < Arg(x) <= Pi/2
				if (argX.isPresent() && F.evalTrue(F.And(F.Less(F.CNPiHalf, argX), F.LessEqual(argX, F.CPiHalf)))) {
					return F.ArcTan(F.Divide(y, x));
				}
			}
			if (x.isInfinity()) {
				return F.C0;
			}
			if (x.isNegativeInfinity()) {
				return F.Times(F.Subtract(F.Times(F.C2, F.UnitStep(F.Re(y))), F.C1), F.Pi);
			}
			return F.NIL;
		}

		@Override
		public IExpr e1DblArg(INum arg1) {
			return F.num(Math.atan(arg1.getRealPart()));
		}

		@Override
		public IExpr e1DblComArg(final IComplexNum val) {
			if (val instanceof ApcomplexNum) {
				return F.complexNum(ApcomplexMath.atan(((ApcomplexNum) val).apcomplexValue()));
			}
			ComplexNum z = (ComplexNum) val;
			if (z.isNaN()) {
				return F.Indeterminate;
			}

			IComplexNum temp = ComplexNum.I.add(z).divide(ComplexNum.I.subtract(z));
			IComplexNum logValue = log(temp.complexNumValue());
			return ComplexNum.I.multiply(logValue).divide(ComplexNum.valueOf(2.0, 0.0)).complexNumValue();
		}

		@Override
		public IExpr e2DblArg(final INum d0, final INum d1) {
			return F.num(Math.atan2(d0.getRealPart(), d1.getRealPart()));
		}

		@Override
		public IExpr e1ApfloatArg(Apfloat arg1) {
			return F.num(ApfloatMath.atan(arg1));
		}

		@Override
		public IExpr e1ApcomplexArg(Apcomplex arg1) {
			return F.complexNum(ApcomplexMath.atan(arg1));
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1 && size != 2) {
				throw new UnsupportedOperationException();
			}
			if (size == 2) {
				return Math.atan2(stack[top - 1], stack[top]);
			}

			return Math.atan(stack[top]);
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}

		public IExpr rewriteLog(IExpr arg1, EvalEngine engine) {
			return
			// [$ (1/2)*I*Log(1 - I*arg1) - (1/2)*I*Log(1 + I*arg1) $]
			F.Plus(F.Times(F.C1D2, F.CI, F.Log(F.Plus(F.C1, F.Times(F.CNI, arg1)))),
					F.Times(F.CN1D2, F.CI, F.Log(F.Plus(F.C1, F.Times(F.CI, arg1))))); // $$;
		}

		public IExpr rewriteLog(IExpr arg1, IExpr arg2, EvalEngine engine) {
			return
			// [$ (-I)*Log((arg1 + I*arg2)/Sqrt(arg1^2 + arg2^2)) $]
			F.Times(F.CNI, F.Log(
					F.Times(F.Plus(arg1, F.Times(F.CI, arg2)), F.Power(F.Plus(F.Sqr(arg1), F.Sqr(arg2)), F.CN1D2)))); // $$;
		}

	}

	/**
	 * Inverse hyperbolic tangent
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Inverse_hyperbolic_function"> Inverse hyperbolic functions</a>
	 */
	private final static class ArcTanh extends AbstractTrigArg1
			implements INumeric, IRewrite, ArcTanhRules, DoubleUnaryOperator {

		@Override
		public double applyAsDouble(double operand) {
			return FastMath.atanh(operand);
		}

		@Override
		public IExpr e1ComplexArg(final Complex z) {
			// 1/2 * Ln( (1+z) / (1-z) )
			Complex temp = Complex.ONE.add(z).divide(Complex.ONE.subtract(z)).log();
			return F.complexNum(new Complex(temp.getReal() / 2, temp.getImaginary() / 2));
		}

		@Override
		public IExpr e1ApcomplexArg(Apcomplex arg1) {
			return F.complexNum(ApcomplexMath.atanh(arg1));
		}

		@Override
		public IExpr e1ApfloatArg(Apfloat arg1) {
			return F.num(ApfloatMath.atanh(arg1));
		}

		@Override
		public IExpr e1DblArg(final double arg1) {
			double val = FastMath.atanh(arg1);
			if (Double.isNaN(val)) {
				return e1ComplexArg(new Complex(arg1));
			}
			return F.num(val);

		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			return FastMath.atanh(stack[top]);
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
			if (arg1.isInterval()) {
				return IntervalSym.arctanh((IAST) arg1);
			}
			IExpr imPart = AbstractFunctionEvaluator.getComplexExpr(arg1, F.CNI);
			if (imPart.isPresent()) {
				return F.Times(F.CI, F.ArcTan(imPart));
			}
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Negate(ArcTanh(negExpr));
			}
			// IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
			// if (imPart.isPresent()) {
			// return F.Times(F.CI, F.ArcTan(imPart));
			// }
			return F.NIL;
		}

		@Override
		public IAST getRuleAST() {
			return RULES;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}

		public IExpr rewriteLog(IExpr arg1, EvalEngine engine) {
			return
			// [$ (-(1/2))*Log(1 - arg1) + (1/2)*Log(1 + arg1) $]
			F.Plus(F.Times(F.CN1D2, F.Log(F.Subtract(F.C1, arg1))), F.Times(F.C1D2, F.Log(F.Plus(F.C1, arg1)))); // $$;
		}
	}

	private static class CirclePoints extends AbstractFunctionEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			final IExpr arg1 = ast.arg1();
			if (arg1.isReal()) {
				if (arg1.isInteger()) {
					int i = ((IInteger) arg1).toIntDefault(Integer.MIN_VALUE);
					if (i > 0) {
						// Pi/i - Pi/2
						final IExpr start = engine.evaluate(F.Plus(F.Times(F.QQ(1, i), F.Pi), F.Times(F.CN1D2, F.Pi)));
						// (2/i)*Pi
						final IExpr angle = engine.evaluate(F.Times(F.QQ(2, i), F.Pi));

						IASTAppendable result = F.ListAlloc(10);
						for (int j = 0; j < i; j++) {
							result.append(F.AngleVector(F.Plus(start, F.ZZ(j).times(angle))));
						}
						return result;
					}
				}
			}
			return F.NIL;
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	/**
	 * Cosine function
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Trigonometric_functions">Trigonometric functions</a> and
	 * <a href="http://en.wikipedia.org/wiki/Exact_trigonometric_constants"> Wikipedia - Exact trigonometric
	 * constants</a>
	 */
	private final static class Cos extends AbstractTrigArg1
			implements INumeric, IRewrite, CosRules, DoubleUnaryOperator {

		@Override
		public double applyAsDouble(double operand) {
			return Math.cos(operand);
		}

		@Override
		public IExpr e1ApcomplexArg(Apcomplex arg1) {
			return F.complexNum(ApcomplexMath.cos(arg1));
		}

		@Override
		public IExpr e1ApfloatArg(Apfloat arg1) {
			return F.num(ApfloatMath.cos(arg1));
		}

		@Override
		public IExpr e1ComplexArg(final Complex arg1) {
			return F.complexNum(arg1.cos());
		}

		@Override
		public IExpr e1DblArg(final double arg1) {
			return F.num(Math.cos(arg1));
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			return Math.cos(stack[top]);
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {

			if (arg1.isPlus()) {
				IExpr k = AbstractFunctionEvaluator.peelOffPlusRational((IAST) arg1, engine);
				if (k != null) {
					// arg1 == x + k*Pi
					if (k.isRational()) {
						IRational t = (IRational) k;
						if (t.isLT(F.C0) || F.C2.isLE(t)) {
							// Cos(arg1-2*Pi*Floor(1/2*t))
							return F.Cos(F.Plus(arg1, F.Times(F.CN2, F.Pi, F.Floor(F.Times(F.C1D2, t)))));
						} else if (t.isLT(F.C1D2)) {// t < 1/2
							IExpr temp = engine.evaluate(arg1.subtract(t.times(F.Pi)));
							IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(temp);
							if (negExpr.isPresent()) {
								// Sin(1/2*Pi - arg1)
								return F.Sin(F.Subtract(F.Times(F.C1D2, F.Pi), arg1));
							}
							return F.NIL;
						} else if (t.isLT(F.C1)) {// t < 1
							// -Sin(arg1-1/2*Pi)
							return F.Negate(F.Sin(F.Plus(arg1, F.Times(F.CN1D2, F.Pi))));
						} else if (t.isLT(F.C3D2)) {// t < 3/2
							// -Cos(arg1-Pi)
							return F.Negate(F.Cos(F.Subtract(arg1, F.Pi)));
						} else {
							// Sin(arg1-3/2*Pi)
							return F.Sin(F.Plus(arg1, F.Times(F.CN3D2, F.Pi)));
						}
					} else if (k.isIntegerResult()) {
						// (-1)^k * Cos( arg1 - k*Pi )
						return F.Times(F.Power(F.CN1, k), F.Cos(F.Subtract(arg1, F.Times(k, F.Pi))));
					}
				}
			}
			if (arg1.isInterval()) {
				return IntervalSym.cos((IAST) arg1);
			}
			IExpr imPart = AbstractFunctionEvaluator.getComplexExpr(arg1, F.CNI);
			if (imPart.isPresent()) {
				return F.Cosh(imPart);
			}
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Cos(negExpr);
			}

			if (arg1.isTimes()) {
				IAST timesAST = (IAST) arg1;
				if (timesAST.size() == 3 && timesAST.arg1().isRational() && timesAST.arg2().isPi()) {
					// t should be positive here!
					IRational t = (IRational) timesAST.arg1();
					if (t.isLT(F.C1D2)) {// t < 1/2
						return F.NIL;
					} else if (t.isLT(F.C1)) {// t < 1
						// -Cos((1-t)*Pi)
						return F.Negate(F.Cos(F.Times(F.Subtract(F.C1, t), F.Pi)));
					} else if (t.isLT(F.C3D2)) {// t < 3/2
						// -Cos((t-1)*Pi)
						return F.Negate(F.Cos(F.Times(F.Subtract(t, F.C1), F.Pi)));
					} else if (t.isLT(F.C2)) {// t < 2
						// Cos((2-t)*Pi)
						return F.Cos(F.Times(F.Subtract(F.C2, t), F.Pi));
					}
					// Cos((t-2*Quotient(IntegerPart(t),2))*Pi)
					return F.Cos(F.Times(F.Pi, F.Plus(t, ExpTrigsFunctions.integerPartFolded2(t))));
				}

				IExpr t = AbstractFunctionEvaluator.peelOfTimes(timesAST, Pi);
				if (t.isPresent() && t.im().isZero()) {
					// 1/2 * t
					IExpr temp = engine.evaluate(F.Times(F.C1D2, t));
					if (temp.isIntegerResult()) {
						return F.C1;
					}
					// 1/2 * t - 1/2
					temp = engine.evaluate(F.Subtract(temp, F.C1D2));
					if (temp.isIntegerResult()) {
						return F.CN1;
					}

					if (t.isIntegerResult()) {
						return F.Power(F.CN1, t);
					}

					// t - 1/2
					temp = engine.evaluate(F.Subtract(t, F.C1D2));
					if (temp.isIntegerResult()) {
						return F.C0;
					}
				}

			}

			// IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
			// if (imPart.isPresent()) {
			// return F.Cosh(imPart);
			// }

			return F.NIL;
		}

		@Override
		public IAST getRuleAST() {
			return RULES;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}

		public IExpr rewriteExp(IExpr arg1, EvalEngine engine) {
			// if (arg1.isTrigFunction() || arg1.isHyperbolicFunction()) {
			// arg1 = arg1.rewrite(ID.Exp);
			// }
			return
			// [$ Exp(arg1*I)/ 2 + Exp(-arg1*I)/ 2 $]
			F.Plus(F.Times(F.C1D2, F.Exp(F.Times(arg1, F.CI))), F.Times(F.C1D2, F.Exp(F.Times(F.CN1, arg1, F.CI)))); // $$;
		}
	}

	/**
	 * Hyperbolic cotangent
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Hyperbolic_function">Hyperbolic function</a>
	 */
	private final static class Coth extends AbstractTrigArg1
			implements INumeric, IRewrite, CothRules, DoubleUnaryOperator {

		@Override
		public double applyAsDouble(double operand) {
			return Math.cosh(operand) / Math.sinh(operand);
		}

		@Override
		public IExpr e1ApcomplexArg(Apcomplex arg1) {
			return F.complexNum(ApcomplexMath.cosh(arg1).divide(ApcomplexMath.sinh(arg1)));
		}

		@Override
		public IExpr e1ApfloatArg(Apfloat arg1) {
			return F.num(ApfloatMath.cosh(arg1).divide(ApfloatMath.sinh(arg1)));
		}

		@Override
		public IExpr e1ComplexArg(final Complex arg1) {
			return F.complexNum(arg1.cosh().divide(arg1.sinh()));
		}

		@Override
		public IExpr e1DblArg(final double arg1) {
			return F.num(Math.cosh(arg1) / Math.sinh(arg1));
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			return Math.cosh(stack[top]) / Math.sinh(stack[top]);
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Negate(Coth(negExpr));
			}
			IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
			if (imPart.isPresent()) {
				return F.Times(F.CNI, F.Cot(imPart));
			}
			if (arg1.isZero()) {
				return F.CComplexInfinity;
			}

			if (arg1.isPlus()) {
				IAST list = AbstractFunctionEvaluator.peelOffPlusI((IAST) arg1, engine);
				if (list.isPresent()) {
					IExpr k = list.arg1();
					// arg1 == x - k/I * Pi
					if (k.isRational()) {
						IRational t = (IRational) k;
						if (t.isLT(F.C0)) {
							// Coth(arg1-I*Pi*IntegerPart(t) + I*Pi )
							return F.Coth(F.Plus(arg1, F.Times(F.CNI, F.Pi, t.integerPart()), F.Times(F.CI, F.Pi)));
						} else if (t.isLT(F.C1D2)) {// t < 1/2
							return F.NIL;
						} else if (t.isLT(F.C1)) {// t < 1
							// Tanh(arg1-1/2*I*Pi)
							return F.Tanh(F.Plus(arg1, F.Times(F.CN1D2, F.CI, F.Pi)));
						}
						// Coth( arg1 - I*Pi*IntegerPart(t) )
						return F.Coth(F.Plus(arg1, F.Times(F.CNI, F.Pi, t.integerPart())));
					} else if (k.isIntegerResult()) {
						// Coth( arg1 - list.arg2() )
						return F.Coth(F.Subtract(arg1, list.arg2()));
					}
				}
			}
			if (arg1.isInterval()) {
				return IntervalSym.coth((IAST) arg1);
			}
			return F.NIL;
		}

		@Override
		public IAST getRuleAST() {
			return RULES;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}

		public IExpr rewriteExp(IExpr arg, EvalEngine engine) {
			IAST negexp = F.Exp(F.Times(F.CN1, arg));
			IAST posexp = F.Exp(arg);
			return
			// [$ (posexp + negexp)/(posexp - negexp) $]
			F.Times(F.Plus(posexp, negexp), F.Power(F.Plus(F.Negate(negexp), posexp), F.CN1)); // $$;
		}
	}

	/**
	 * Cosecant function
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Trigonometric_functions">Trigonometric functions</a>
	 */
	private final static class Csc extends AbstractTrigArg1
			implements INumeric, IRewrite, CscRules, DoubleUnaryOperator {

		@Override
		public double applyAsDouble(double operand) {
			return 1.0D / Math.sin(operand);
		}

		@Override
		public IExpr e1ApcomplexArg(Apcomplex arg1) {
			return F.complexNum(ApcomplexMath.inverseRoot(ApcomplexMath.sin(arg1), 1));
		}

		@Override
		public IExpr e1ApfloatArg(Apfloat arg1) {
			return F.num(ApfloatMath.inverseRoot(ApfloatMath.sin(arg1), 1));
		}

		@Override
		public IExpr e1ComplexArg(final Complex arg1) {
			return F.complexNum(arg1.sin().reciprocal());
		}

		@Override
		public IExpr e1DblArg(final double arg1) {
			return F.num(1.0D / Math.sin(arg1));
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			return 1.0D / Math.sin(stack[top]);
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {

			if (arg1.isPlus()) {
				IExpr k = AbstractFunctionEvaluator.peelOffPlusRational((IAST) arg1, engine);
				if (k != null) {
					// arg1 == x + k*Pi
					if (k.isRational()) {
						IRational t = (IRational) k;
						if (t.isLT(F.C0)) {
							// Csc(arg1 - 2*Pi*IntegerPart(1/2*t) + 2*Pi)
							return F.Csc(F.Plus(arg1, F.Times(F.CN2Pi, F.IntegerPart(F.Times(F.C1D2, t))), F.C2Pi));
						} else if (t.isLT(F.C1D2)) {// t < 1/2
							return F.NIL;
						} else if (t.isLT(F.C1)) {// t < 1
							// Sec(arg1-1/2*Pi)
							return F.Sec(F.Plus(arg1, F.Times(F.CN1D2, F.Pi)));
						} else if (t.isLT(F.C2)) { // t < 2
							// -Csc(arg1-Pi)
							return F.Negate(F.Csc(F.Plus(arg1, F.CNPi)));
						}
						// Csc(arg1 - 2*Pi*IntegerPart(1/2*t) )
						return F.Csc(F.Plus(arg1, F.Times(F.CN2Pi, F.IntegerPart(F.Times(F.C1D2, t)))));
					} else if (k.isIntegerResult()) {
						// (-1)^k * Csc( arg1 - k*Pi )
						return F.Times(F.Power(F.CN1, k), F.Csc(F.Subtract(arg1, F.Times(k, F.Pi))));
					}
				}
			}

			IExpr imPart = AbstractFunctionEvaluator.getComplexExpr(arg1, F.CNI);
			if (imPart.isPresent()) {
				return F.Times(F.CNI, F.Csch(imPart));
			}
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Negate(Csc(negExpr));
			}

			if (arg1.isTimes()) {
				IAST timesAST = (IAST) arg1;
				if (timesAST.size() == 3 && timesAST.arg1().isRational() && timesAST.arg2().isPi()) {
					// t should be positive here!
					IRational t = (IRational) timesAST.arg1();
					if (t.isLT(F.C1D2)) {// t < 1/2
						return F.NIL;
					} else if (t.isLT(F.C1)) {// t < 1
						// Csc((1-t)*Pi)
						return F.Csc(F.Times(F.Subtract(F.C1, t), F.Pi));
					} else if (t.isLT(F.C2)) {// t < 2
						// -Csc((2-t)*Pi)
						return F.Negate(F.Csc(F.Times(F.Subtract(F.C2, t), F.Pi)));
					}
					// Csc((t-2*Quotient(IntegerPart(t),2))*Pi)
					return F.Csc(F.Times(F.Pi, F.Plus(t, ExpTrigsFunctions.integerPartFolded2(t))));
				}

				IExpr t = AbstractFunctionEvaluator.peelOfTimes(timesAST, Pi);
				if (t.isPresent() && t.im().isZero()) {
					if (t.isIntegerResult()) {
						return F.CComplexInfinity;
					}

					// 1/2 * t
					IExpr temp1 = engine.evaluate(F.Times(F.C1D2, t));
					// 1/2 * t - 1/4
					IExpr temp2 = engine.evaluate(F.Plus(temp1, F.CN1D4));
					if (temp2.isIntegerResult()) {
						return F.C1;
					}
					// 1/2 * t + 1/4
					temp2 = engine.evaluate(F.Plus(temp1, F.C1D4));
					if (temp2.isIntegerResult()) {
						return F.CN1;
					}
					// t - 1/2
					temp2 = engine.evaluate(F.Plus(t, F.CN1D2));
					if (temp2.isIntegerResult()) {
						// (-1) ^ (t-1/2)
						return F.Power(F.CN1, temp2);
					}

				}

			}
			if (arg1.isInterval()) {
				return IntervalSym.csc((IAST) arg1);
			}
			// IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
			// if (imPart.isPresent()) {
			// return F.Times(F.CNI, F.Csch(imPart));
			// }

			return F.NIL;
		}

		@Override
		public IAST getRuleAST() {
			return RULES;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}

		public IExpr rewriteExp(IExpr arg1, EvalEngine engine) {
			return
			// [$ -((2*I)/(E^((-I)*arg1) - E^(I*arg1))) $]
			F.Times(F.CN1, F.C2, F.CI,
					F.Power(F.Subtract(F.Exp(F.Times(F.CNI, arg1)), F.Exp(F.Times(F.CI, arg1))), F.CN1)); // $$;
		}
	}

	/**
	 * Hyperbolic cosine
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Hyperbolic_function">Hyperbolic function</a>
	 */
	private final static class Cosh extends AbstractTrigArg1
			implements INumeric, IRewrite, CoshRules, DoubleUnaryOperator {

		@Override
		public double applyAsDouble(double operand) {
			return Math.cosh(operand);
		}

		@Override
		public IExpr e1ApcomplexArg(Apcomplex arg1) {
			return F.complexNum(ApcomplexMath.cosh(arg1));
		}

		@Override
		public IExpr e1ApfloatArg(Apfloat arg1) {
			return F.num(ApfloatMath.cosh(arg1));
		}

		@Override
		public IExpr e1ComplexArg(final Complex arg1) {
			return F.complexNum(arg1.cosh());
		}

		@Override
		public IExpr e1DblArg(final double arg1) {
			return F.num(Math.cosh(arg1));
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			return Math.cosh(stack[top]);
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Cosh(negExpr);
			}
			if (arg1.isZero()) {
				return F.C1;
			}
			if (arg1.isPlus()) {
				IAST list = AbstractFunctionEvaluator.peelOffPlusI((IAST) arg1, engine);
				if (list.isPresent()) {
					IExpr k = list.arg1();
					// arg1 == x - k/I * Pi
					if (k.isRational()) {
						IRational t = (IRational) k;
						if (t.isLT(F.C0)) {
							// Cosh(arg1-2*I*Pi*IntegerPart(1/2*t) + 2*I*Pi)
							return F.Cosh(F.Plus(arg1, F.Times(F.CN2, F.CI, F.Pi, F.IntegerPart(F.Times(F.C1D2, t))),
									F.Times(F.C2, F.CI, F.Pi)));
						} else if (t.isLT(F.C1D2)) {// t < 1/2
							return F.NIL;
						} else if (t.isLT(F.C1)) {// t < 1
							// I * Sinh(arg1-1/2*I*Pi)
							return F.Times(F.CI, F.Sinh(F.Plus(arg1, F.Times(F.CN1D2, F.CI, F.Pi))));
						} else if (t.isLT(F.C2)) {// t < 2
							// -Cosh(arg1 - I*Pi)
							return F.Negate(F.Cosh(F.Subtract(arg1, F.Times(F.CI, F.Pi))));
						}
						// Cosh(arg1-2*I*Pi*IntegerPart(1/2*t) )
						return F.Cosh(F.Plus(arg1, F.Times(F.CN2, F.CI, F.Pi, F.IntegerPart(F.Times(F.C1D2, t)))));
					} else if (k.isIntegerResult()) {
						// (-1)^k * Cosh( arg1 - list.arg2() )
						return F.Times(F.Power(F.CN1, k), F.Cosh(F.Subtract(arg1, list.arg2())));
					}
				}
			}

			IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
			if (imPart.isPresent()) {
				return F.Cos(imPart);
			}
			if (arg1.isInterval()) {
				return IntervalSym.cosh((IAST) arg1);
			}
			return F.NIL;
		}

		@Override
		public IAST getRuleAST() {
			return RULES;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}

		public IExpr rewriteExp(IExpr arg1, EvalEngine engine) {
			return
			// [$ 1/(E^arg1*2) + E^arg1/2 $]
			F.Plus(F.Power(F.Times(F.Exp(arg1), F.C2), F.CN1), F.Times(F.C1D2, F.Exp(arg1))); // $$;
		}
	}

	/**
	 * Cotangent function
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Trigonometric_functions">Trigonometric functions</a>
	 */
	private final static class Cot extends AbstractTrigArg1
			implements INumeric, IRewrite, CotRules, DoubleUnaryOperator {

		@Override
		public double applyAsDouble(double operand) {
			return 1.0D / Math.tan(operand);
		}

		@Override
		public IExpr e1ApcomplexArg(Apcomplex arg1) {
			Apcomplex denominator = ApcomplexMath.sin(arg1);
			if (denominator.equals(Apcomplex.ZERO)) {
				return F.CComplexInfinity;
			}
			return F.complexNum(ApcomplexMath.cos(arg1).divide(denominator));
		}

		@Override
		public IExpr e1ApfloatArg(Apfloat arg1) {
			Apfloat denominator = ApfloatMath.sin(arg1);
			if (denominator.equals(Apfloat.ZERO)) {
				return F.CComplexInfinity;
			}
			return F.num(ApfloatMath.cos(arg1).divide(denominator));
		}

		@Override
		public IExpr e1ComplexArg(final Complex arg1) {
			Complex denominator = arg1.sin();
			if (denominator.equals(Complex.ZERO)) {
				return F.CComplexInfinity;
			}
			return F.complexNum(arg1.cos().divide(denominator));
		}

		@Override
		public IExpr e1DblArg(final double arg1) {
			double denom = Math.sin(arg1);
			if (F.isZero(denom)) {
				return F.CComplexInfinity;
			}
			return F.num(Math.cos(arg1) / denom);
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			return 1.0D / Math.tan(stack[top]);
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {

			if (arg1.isPlus()) {
				IExpr k = AbstractFunctionEvaluator.peelOffPlusRational((IAST) arg1, engine);
				if (k != null) {
					// arg1 == x + k*Pi
					if (k.isRational()) {
						IRational t = (IRational) k;
						if (t.isLT(F.C0)) { // t < 0
							// Cot(arg1 + Pi - Pi*IntegerPart(t))
							return F.Cot(F.Plus(arg1, F.Pi, F.Times(F.CN1, F.Pi, t.integerPart())));
						} else if (t.isLT(F.C1D2)) {// t < 1/2
							return F.NIL;
						} else if (t.isLT(F.C1)) {// t < 1
							// -Tan(arg1-1/2*Pi)
							return F.Negate(F.Tan(F.Plus(arg1, F.Times(F.CN1D2, F.Pi))));
						} else {
							// Cot(arg1 - Pi*IntegerPart(t))
							return F.Cot(F.Plus(arg1, F.Times(F.CN1, F.Pi, t.integerPart())));
						}
					} else if (k.isIntegerResult()) {
						// Cot( arg1 - k*Pi )
						return F.Cot(F.Subtract(arg1, F.Times(k, F.Pi)));
					}
				}
			}

			IExpr imPart = AbstractFunctionEvaluator.getComplexExpr(arg1, F.CNI);
			if (imPart.isPresent()) {
				return Times(CNI, Coth(imPart));
			}
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Negate(Cot(negExpr));
			}

			if (arg1.isTimes()) {
				IAST timesAST = (IAST) arg1;
				if (timesAST.size() == 3 && timesAST.arg1().isRational() && timesAST.arg2().isPi()) {
					// t should be positive here!
					IRational t = (IRational) timesAST.arg1();
					if (t.isLT(F.C1D2)) {// t < 1/2
						return F.NIL;
					} else if (t.isLT(F.C1)) {// t < 1
						// -Cot((1-t)*Pi)
						return F.Negate(F.Cot(F.Times(F.Subtract(F.C1, t), F.Pi)));
					} else if (t.isLT(F.C2)) {// t < 2
						// Cot((t-1)*Pi)
						return F.Cot(F.Times(F.Subtract(t, F.C1), F.Pi));
					}
					// Cot((t-2*Quotient(IntegerPart(t),2))*Pi)
					return F.Cot(F.Times(F.Pi, F.Plus(t, ExpTrigsFunctions.integerPartFolded2(t))));
				}
				IExpr t = AbstractFunctionEvaluator.peelOfTimes(timesAST, Pi);
				if (t.isPresent() && t.im().isZero()) {
					if (t.isIntegerResult()) {
						return F.CComplexInfinity;
					}
					// t - 1/2
					IExpr temp = engine.evaluate(F.Plus(t, F.CN1D2));
					if (temp.isIntegerResult()) {
						return F.C0;
					}
				}
			}

			// IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
			// if (imPart.isPresent()) {
			// return Times(CNI, Coth(imPart));
			// }

			if (arg1.isInterval()) {
				return IntervalSym.cot((IAST) arg1);
			}

			return F.NIL;
		}

		@Override
		public IAST getRuleAST() {
			return RULES;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}

		public IExpr rewriteExp(IExpr arg1, EvalEngine engine) {
			return
			// [$ -((I*(E^((-I)*arg1) + E^(I*arg1)))/(E^((-I)*arg1) - E^(I*arg1))) $]
			F.Times(F.CN1, F.CI, F.Plus(F.Exp(F.Times(F.CNI, arg1)), F.Exp(F.Times(F.CI, arg1))),
					F.Power(F.Subtract(F.Exp(F.Times(F.CNI, arg1)), F.Exp(F.Times(F.CI, arg1))), F.CN1)); // $$;
		}

	}

	/**
	 * Hyperbolic Cosecant function
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Hyperbolic_function">Hyperbolic functions</a>
	 */
	private final static class Csch extends AbstractTrigArg1
			implements INumeric, IRewrite, CschRules, DoubleUnaryOperator {

		@Override
		public double applyAsDouble(double operand) {
			return 1.0D / Math.sinh(operand);
		}

		@Override
		public IExpr e1ApcomplexArg(Apcomplex arg1) {
			return F.complexNum(ApcomplexMath.inverseRoot(ApcomplexMath.sinh(arg1), 1));
		}

		@Override
		public IExpr e1ApfloatArg(Apfloat arg1) {
			return F.num(ApfloatMath.inverseRoot(ApfloatMath.sinh(arg1), 1));
		}

		@Override
		public IExpr e1ComplexArg(final Complex arg1) {
			return F.complexNum(arg1.sinh().reciprocal());
		}

		@Override
		public IExpr e1DblArg(final double arg1) {
			return F.num(1.0D / Math.sinh(arg1));
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			return 1.0D / Math.sinh(stack[top]);
		}

		@Override
		public IExpr evaluateArg1(IExpr arg1, EvalEngine engine) {
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Negate(Csch(negExpr));
			}
			IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
			if (imPart.isPresent()) {
				return F.Times(F.CNI, F.Csc(imPart));
			}
			if (arg1.isZero()) {
				return F.CComplexInfinity;
			}
			if (arg1.isPlus()) {
				IAST list = AbstractFunctionEvaluator.peelOffPlusI((IAST) arg1, engine);
				if (list.isPresent()) {
					IExpr k = list.arg1();
					// arg1 == x - k/I * Pi
					if (k.isRational()) {
						IRational t = (IRational) k;
						if (t.isLT(F.C0)) {
							// Csch(arg1-2*I*Pi*IntegerPart(1/2*t) + 2*I*Pi)
							return F.Csch(F.Plus(arg1, F.Times(F.CN2, F.CI, F.Pi, F.IntegerPart(F.Times(F.C1D2, t))),
									F.Times(F.C2, F.CI, F.Pi)));
						} else if (t.isLT(F.C1D2)) {// t < 1/2
							return F.NIL;
						} else if (t.isLT(F.C1)) {// t < 1
							// -I * Sech(arg1-1/2*I*Pi)
							return F.Times(F.CNI, F.Sech(F.Plus(arg1, F.Times(F.CN1D2, F.CI, F.Pi))));
						} else if (t.isLT(F.C2)) {// t < 2
							// -Csch(arg1 - I*Pi)
							return F.Negate(F.Csch(F.Subtract(arg1, F.Times(F.CI, F.Pi))));
						}
						// Csch(arg1-2*I*Pi*IntegerPart(1/2*t) )
						return F.Csch(F.Plus(arg1, F.Times(F.CN2, F.CI, F.Pi, F.IntegerPart(F.Times(F.C1D2, t)))));
					} else if (k.isIntegerResult()) {
						// (-1)^k * Csch( arg1 - list.arg2() )
						return F.Times(F.Power(F.CN1, k), F.Csch(F.Subtract(arg1, list.arg2())));
					}
				}
			}
			if (arg1.isInterval()) {
				return IntervalSym.csch((IAST) arg1);
			}
			return F.NIL;
		}

		@Override
		public IAST getRuleAST() {
			return RULES;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}

		public IExpr rewriteExp(IExpr arg1, EvalEngine engine) {
			return
			// [$ 2/(-E^(-arg1) + E^arg1) $]
			F.Times(F.C2, F.Power(F.Plus(F.Negate(F.Exp(F.Negate(arg1))), F.Exp(arg1)), F.CN1)); // $$;
		}
	}

	private final static class Exp extends AbstractArg1 implements INumeric {
		public Exp() {
		}

		@Override
		public IExpr e1ObjArg(final IExpr o) {
			return Power(F.E, o);
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			return Math.exp(stack[top]);
		}
	}

	private final static class Haversine extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			final IExpr arg1 = ast.arg1();
			if (arg1.isNumber() || arg1.isNumericFunction()) {
				// 1/2 * (1-Cos(x))
				return F.Times(F.C1D2, F.Subtract(F.C1, F.Cos(arg1)));
				// return F.Power(F.Sin(F.C1D2.times(ast.arg1())), F.C2);
			}
			return F.NIL;
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		}

	}

	private final static class InverseHaversine extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			final IExpr arg1 = ast.arg1();
			if (arg1.isNumber() || arg1.isNumericFunction()) {
				return F.Times(F.C2, F.ArcSin(F.Sqrt(arg1)));
			}
			return F.NIL;
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		}

	}

	/**
	 * See <a href="http://en.wikipedia.org/wiki/Logarithm">Wikipedia - Logarithm</a>
	 * 
	 */
	private final static class Log extends AbstractArg12 implements INumeric, LogRules {

		@Override
		public IAST getRuleAST() {
			return RULES;
		}

		@Override
		public IExpr e1ApfloatArg(Apfloat arg1) {
			return F.num(ApfloatMath.log(arg1));
		}

		@Override
		public IExpr e1ApcomplexArg(Apcomplex arg1) {
			return F.complexNum(ApcomplexMath.log(arg1));
		}

		@Override
		public IExpr e1DblArg(final INum arg1) {
			return Num.valueOf(Math.log(arg1.getRealPart()));
		}

		public IExpr e1FraArg(final IFraction f) {
			if (f.isPositive() && f.isLT(F.C1)) {
				return F.Negate(F.Log(f.inverse()));
			}
			return F.NIL;
		}

		@Override
		public IExpr e2ApfloatArg(final ApfloatNum d0, final ApfloatNum d1) {
			long precision = d0.precision();
			return F.complexNum(ApcomplexMath.log(d0.apfloatValue(precision), d1.apfloatValue(precision)));
		}

		@Override
		public IExpr e2ApcomplexArg(final ApcomplexNum c0, final ApcomplexNum c1) {
			return F.complexNum(ApcomplexMath.log(c0.apcomplexValue(), c1.apcomplexValue()));
		}

		@Override
		public IExpr e1DblComArg(final IComplexNum arg1) {
			return log(arg1);
		}

		@Override
		public IExpr e2DblArg(final INum arg1, final INum arg2) {
			return Num.valueOf(Math.log(arg2.getRealPart()) / Math.log(arg1.getRealPart()));
		}

		@Override
		public IExpr e2IntArg(final IInteger arg1, final IInteger arg2) {
			return baseBLog(arg1, arg2);
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			return Math.log(stack[top]);
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}

		@Override
		public IExpr e1ObjArg(IExpr arg1) {
			if (arg1.isPower()) {
				IExpr base = arg1.base();
				IExpr exponent = arg1.exponent();
				// arg2*Log(arg1)
				IExpr temp = F.eval(Times(exponent, F.Log(base)));
				IExpr imTemp = F.eval(F.Im(temp));
				if (imTemp.isReal()) {
					if (((ISignedNumber) imTemp).isGT(F.num(-1 * Math.PI))
							&& ((ISignedNumber) imTemp).isLT(F.num(Math.PI))) {
						// Log(arg1 ^ arg2) == arg2*Log(arg1) ||| -Pi <
						// Im(arg2*Log(arg1)) < Pi
						return temp;
					}
				}
				if (AbstractAssumptions.assumePositive(base) && exponent.isRealResult()) {
					// Log(arg1 ^ arg2) == arg2*Log(arg1) ||| arg1 > 0 && arg2 is
					// Real
					return temp;
				}

			}
			if (arg1.isNegativeResult()) {
				return F.Plus(F.Log(F.Negate(arg1)), F.Times(CI, F.Pi));
			}
			if (arg1.isInterval()) {
				return IntervalSym.log((IAST) arg1);
			}
			return F.NIL;
		}

		@Override
		public IExpr e2ObjArg(IExpr o0, IExpr o1) {
			if (o0.isZero()) {
				if (o1.isZero()) {
					return F.Indeterminate;
				}
				return F.C0;
			}
			return F.NIL;
		}
	}

	/**
	 * Logistic function
	 * 
	 * See <a href="https://en.wikipedia.org/wiki/Logistic_function">Wikipedia: Logistic function</a>
	 * 
	 */
	private final static class LogisticSigmoid extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr arg1 = ast.arg1();
			if (arg1.isNumber()) {
				if (arg1.isZero()) {
					return F.C1D2;
				}
				// 1 / (1 + Exp(-arg1))
				return F.Power(F.Plus(F.C1, F.Power(F.E, F.Times(F.CN1, arg1))), F.CN1);
			}
			if (arg1.isInfinity()) {
				return F.C1;
			}
			if (arg1.isNegativeInfinity()) {
				return F.C0;
			}
			return F.NIL;
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		}

	}

	private final static class Log10 extends AbstractArg1 implements INumeric {

		@Override
		public IExpr e1ObjArg(final IExpr o) {
			return F.Log(F.C10, o);
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			return Math.log10(stack[top]);
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	private final static class Log2 extends AbstractArg1 implements INumeric {

		@Override
		public IExpr e1ObjArg(final IExpr o) {
			return F.Log(F.C2, o);
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			return Math.log(stack[top] / 2.0d);
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	/**
	 * Secant function
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Trigonometric_functions">Trigonometric functions</a>
	 */
	private final static class Sec extends AbstractTrigArg1
			implements INumeric, IRewrite, SecRules, DoubleUnaryOperator {

		@Override
		public double applyAsDouble(double operand) {
			return 1.0D / Math.cos(operand);
		}

		@Override
		public IExpr e1ApcomplexArg(Apcomplex arg1) {
			return F.complexNum(ApcomplexMath.inverseRoot(ApcomplexMath.cos(arg1), 1));
		}

		@Override
		public IExpr e1ApfloatArg(Apfloat arg1) {
			return F.num(ApfloatMath.inverseRoot(ApfloatMath.cos(arg1), 1));
		}

		@Override
		public IExpr e1ComplexArg(final Complex arg1) {
			return F.complexNum(arg1.cos().reciprocal());
		}

		@Override
		public IExpr e1DblArg(final double arg1) {
			return F.num(1.0D / Math.cos(arg1));
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			return 1.0D / Math.cos(stack[top]);
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
			if (arg1.isPlus()) {
				IExpr k = AbstractFunctionEvaluator.peelOffPlusRational((IAST) arg1, engine);
				if (k != null) {
					// arg1 == x + k*Pi
					if (k.isRational()) {
						IRational t = (IRational) k;
						if (t.isLT(F.C0)) {
							// Sec(arg1 - 2*Pi*IntegerPart(1/2*t) + 2*Pi)
							return F.Sec(F.Plus(arg1, F.Times(F.CN2Pi, F.IntegerPart(F.Times(F.C1D2, t))), F.C2Pi));
						} else if (t.isLT(F.C1D2)) {// t < 1/2
							return F.NIL;
						} else if (t.isLT(F.C1)) {// t < 1
							// -Csc(arg1-1/2*Pi)
							return F.Negate(F.Csc(F.Plus(arg1, F.Times(F.CN1D2, F.Pi))));
						} else if (t.isLT(F.C2)) { // t < 2
							// -Sec(arg1-Pi)
							return F.Negate(F.Sec(F.Plus(arg1, F.CNPi)));
						}
						// Sec(arg1 - 2*Pi*IntegerPart(1/2*t) )
						return F.Sec(F.Plus(arg1, F.Times(F.CN2Pi, F.IntegerPart(F.Times(F.C1D2, t)))));
					} else if (k.isIntegerResult()) {
						// (-1)^k * Sec( arg1 - k*Pi )
						return F.Times(F.Power(F.CN1, k), F.Sec(F.Subtract(arg1, F.Times(k, F.Pi))));
					}
				}
			}

			IExpr imPart = AbstractFunctionEvaluator.getComplexExpr(arg1, F.CNI);
			if (imPart.isPresent()) {
				return F.Sech(imPart);
			}
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Sec(negExpr);
			}

			if (arg1.isTimes()) {
				IAST timesAST = (IAST) arg1;
				if (timesAST.size() == 3 && timesAST.arg1().isRational() && timesAST.arg2().isPi()) {
					// t should be positive here!
					IRational t = (IRational) timesAST.arg1();
					if (t.isLT(F.C1D2)) {// t < 1/2
						return F.NIL;
					} else if (t.isLT(F.C1)) {// t < 1
						// -Sec((1-t)*Pi)
						return F.Negate(F.Sec(F.Times(F.Subtract(F.C1, t), F.Pi)));
					} else if (t.isLT(F.C2)) {// t < 2
						// Sec((2-t)*Pi)
						return F.Sec(F.Times(F.Subtract(F.C2, t), F.Pi));
					}
					// Sec((t-2*Quotient(IntegerPart(t),2))*Pi)
					return F.Sec(F.Times(F.Pi, F.Plus(t, ExpTrigsFunctions.integerPartFolded2(t))));
				}

				IExpr t = AbstractFunctionEvaluator.peelOfTimes(timesAST, Pi);
				if (t.isPresent() && t.im().isZero()) {
					// 1/2 * t
					IExpr temp1 = engine.evaluate(F.Times(F.C1D2, t));
					if (temp1.isIntegerResult()) {
						return F.C1;
					}
					// 1/2 * t - 1/2
					IExpr temp2 = engine.evaluate(F.Plus(temp1, F.CN1D2));
					if (temp2.isIntegerResult()) {
						return F.CN1;
					}
					if (t.isIntegerResult()) {
						return F.Power(F.CN1, t);
					}

					// t - 1/2
					temp2 = engine.evaluate(F.Plus(t, F.CN1D2));
					if (temp2.isIntegerResult()) {
						return F.CComplexInfinity;
					}

				}

			}
			if (arg1.isInterval()) {
				return IntervalSym.sec((IAST) arg1);
			}
			// IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
			// if (imPart.isPresent()) {
			// return F.Sech(imPart);
			// }

			return F.NIL;
		}

		@Override
		public IAST getRuleAST() {
			return RULES;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}

		public IExpr rewriteExp(IExpr arg1, EvalEngine engine) {
			return
			// [$ 2/(E^((-I)*arg1) + E^(I*arg1)) $]
			F.Times(F.C2, F.Power(F.Plus(F.Exp(F.Times(F.CNI, arg1)), F.Exp(F.Times(F.CI, arg1))), F.CN1)); // $$;
		}
	}

	/**
	 * Hyperbolic Secant function
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Hyperbolic_function">Hyperbolic functions</a>
	 */
	private final static class Sech extends AbstractTrigArg1
			implements INumeric, IRewrite, SechRules, DoubleUnaryOperator {

		@Override
		public double applyAsDouble(double operand) {
			return 1.0D / Math.cosh(operand);
		}

		@Override
		public IExpr e1ApcomplexArg(Apcomplex arg1) {
			return F.complexNum(ApcomplexMath.inverseRoot(ApcomplexMath.cosh(arg1), 1));
		}

		@Override
		public IExpr e1ApfloatArg(Apfloat arg1) {
			return F.num(ApfloatMath.inverseRoot(ApfloatMath.cosh(arg1), 1));
		}

		@Override
		public IExpr e1ComplexArg(final Complex arg1) {
			return F.complexNum(arg1.cosh().reciprocal());
		}

		@Override
		public IExpr e1DblArg(final double arg1) {
			return F.num(1.0D / Math.cosh(arg1));
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			return 1.0D / Math.cosh(stack[top]);
		}

		@Override
		public IExpr evaluateArg1(IExpr arg1, EvalEngine engine) {
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Sech(negExpr);
			}
			IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
			if (imPart.isPresent()) {
				return F.Sec(imPart);
			}
			if (arg1.isZero()) {
				return F.C0;
			}
			if (arg1.isPlus()) {
				IAST list = AbstractFunctionEvaluator.peelOffPlusI((IAST) arg1, engine);
				if (list.isPresent()) {
					IExpr k = list.arg1();
					// arg1 == x - k/I * Pi
					if (k.isRational()) {
						IRational t = (IRational) k;
						if (t.isLT(F.C0)) {
							// Sech(arg1-2*I*Pi*IntegerPart(1/2*t) + 2*I*Pi)
							return F.Sech(F.Plus(arg1, F.Times(F.CN2, F.CI, F.Pi, F.IntegerPart(F.Times(F.C1D2, t))),
									F.Times(F.C2, F.CI, F.Pi)));
						} else if (t.isLT(F.C1D2)) {// t < 1/2
							return F.NIL;
						} else if (t.isLT(F.C1)) {// t < 1
							// -I * Csch(arg1-1/2*I*Pi)
							return F.Times(F.CNI, F.Csch(F.Plus(arg1, F.Times(F.CN1D2, F.CI, F.Pi))));
						} else if (t.isLT(F.C2)) {// t < 2
							// -Sech(arg1 - I*Pi)
							return F.Negate(F.Sech(F.Subtract(arg1, F.Times(F.CI, F.Pi))));
						}
						// Sech(arg1-2*I*Pi*IntegerPart(1/2*t) )
						return F.Sech(F.Plus(arg1, F.Times(F.CN2, F.CI, F.Pi, F.IntegerPart(F.Times(F.C1D2, t)))));
					} else if (k.isIntegerResult()) {
						// (-1)^k * Sech( arg1 - list.arg2() )
						return F.Times(F.Power(F.CN1, k), F.Sech(F.Subtract(arg1, list.arg2())));
					}
				}
			}
			if (arg1.isInterval()) {
				return IntervalSym.sech((IAST) arg1);
			}
			return F.NIL;
		}

		@Override
		public IAST getRuleAST() {
			return RULES;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}

		public IExpr rewriteExp(IExpr arg1, EvalEngine engine) {
			return
			// [$ 2/(E^(-arg1) + E^arg1) $]
			F.Times(F.C2, F.Power(F.Plus(F.Exp(F.Negate(arg1)), F.Exp(arg1)), F.CN1)); // $$;
		}
	}

	/**
	 * Sine function.
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Trigonometric_functions">Trigonometric functions</a>
	 */
	private final static class Sin extends AbstractTrigArg1
			implements INumeric, IRewrite, SinRules, DoubleUnaryOperator {

		@Override
		public double applyAsDouble(double operand) {
			return Math.sin(operand);
		}

		@Override
		public IExpr e1ApcomplexArg(Apcomplex arg1) {
			return F.complexNum(ApcomplexMath.sin(arg1));
		}

		@Override
		public IExpr e1ApfloatArg(Apfloat arg1) {
			return F.num(ApfloatMath.sin(arg1));
		}

		@Override
		public IExpr e1ComplexArg(final Complex arg1) {
			return F.complexNum(arg1.sin());
		}

		@Override
		public IExpr e1DblArg(final double arg1) {
			return num(Math.sin(arg1));
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			return Math.sin(stack[top]);
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {

			if (arg1.isPlus()) {
				IExpr k = AbstractFunctionEvaluator.peelOffPlusRational((IAST) arg1, engine);
				if (k != null) {
					// arg1 == x + k*Pi
					if (k.isRational()) {
						IRational t = (IRational) k;
						if (t.isLT(F.C0) || F.C2.isLE(t)) {
							// Sin(arg1-2*Pi*Floor(1/2*t))
							return F.Sin(F.Plus(arg1, F.Times(F.CN2, F.Pi, F.Floor(F.Times(F.C1D2, t)))));
						} else if (t.isLT(F.C1D2)) {// t < 1/2
							// (arg1-t*Pi)
							IExpr temp = engine.evaluate(arg1.subtract(t.times(F.Pi)));
							IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(temp);
							if (negExpr.isPresent()) {
								// Cos(1/2*Pi - arg1)
								return F.Cos(F.Subtract(F.Times(F.C1D2, F.Pi), arg1));
							}

						} else if (t.isLT(F.C1)) {// t < 1
							// Cos(arg1-1/2*Pi)
							return F.Cos(F.Plus(arg1, F.Times(F.CN1D2, F.Pi)));
						} else if (t.isLT(F.C3D2)) {// t < 3/2
							// -Sin(arg1-Pi)
							return F.Negate(F.Sin(F.Subtract(arg1, F.Pi)));
						} else { // t < 2
							// -Cos(arg1-3/2*Pi)
							return F.Negate(F.Cos(F.Plus(arg1, F.Times(F.CN3D2, F.Pi))));
						}
					} else if (k.isIntegerResult()) {
						// (-1)^k * Sin( arg1 - k*Pi )
						return F.Times(F.Power(F.CN1, k), F.Sin(F.Subtract(arg1, F.Times(k, F.Pi))));
					}
				}
			}
			if (arg1.isInterval()) {
				return IntervalSym.sin((IAST) arg1);
			}
			IExpr imPart = AbstractFunctionEvaluator.getComplexExpr(arg1, F.CNI);
			if (imPart.isPresent()) {
				return F.Times(F.CI, F.Sinh(imPart));
			}
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Negate(Sin(negExpr));
			}

			if (arg1.isTimes()) {
				IAST timesAST = (IAST) arg1;
				if (timesAST.size() == 3 && timesAST.arg1().isRational() && timesAST.arg2().isPi()) {
					// t should be positive here!
					IRational t = (IRational) timesAST.arg1();
					if (t.isLT(F.C1D2)) {// t < 1/2
						return F.NIL;
					} else if (t.isLT(F.C1)) {// t < 1
						// Sin((1-t)*Pi)
						return F.Sin(F.Times(F.Subtract(F.C1, t), F.Pi));
					} else if (t.isLT(F.C3D2)) {// t < 3/2
						// -Sin((t-1)*Pi)
						return F.Negate(F.Sin(F.Times(F.Subtract(t, F.C1), F.Pi)));
					} else if (t.isLT(F.C2)) {// t < 2
						// -Sin((2-t)*Pi)
						return F.Negate(F.Sin(F.Times(F.Subtract(F.C2, t), F.Pi)));
					}
					// Sin((t-2*Quotient(IntegerPart(t),2))*Pi)
					return F.Sin(F.Times(F.Pi, F.Plus(t, ExpTrigsFunctions.integerPartFolded2(t))));
				}

				IExpr t = AbstractFunctionEvaluator.peelOfTimes(timesAST, Pi);
				if (t.isPresent() && t.im().isZero()) {
					if (t.isIntegerResult()) {
						return F.C0;
					}

					// 1/2 * t
					IExpr temp1 = engine.evaluate(F.Times(F.C1D2, t));
					// 1/2 * t - 1/4
					IExpr temp2 = engine.evaluate(F.Plus(temp1, F.CN1D4));
					if (temp2.isIntegerResult()) {
						return F.C1;
					}
					// 1/2 * t + 1/4
					temp2 = engine.evaluate(F.Plus(temp1, F.C1D4));
					if (temp2.isIntegerResult()) {
						return F.CN1;
					}
					// t - 1/2
					temp2 = engine.evaluate(F.Plus(t, F.CN1D2));
					if (temp2.isIntegerResult()) {
						// (-1) ^ (t-1/2)
						return F.Power(F.CN1, temp2);
					}

				}

			}

			// IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
			// if (imPart.isPresent()) {
			// return F.Times(F.CI, F.Sinh(imPart));
			// }

			return F.NIL;
		}

		@Override
		public IAST getRuleAST() {
			return RULES;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}

		public IExpr rewriteExp(IExpr arg1, EvalEngine engine) {
			// if (arg1.isTrigFunction() || arg1.isHyperbolicFunction()) {
			// arg1 = arg1.rewrite(ID.Exp);
			// }
			return
			// [$ Exp(arg1*I) / (2*I) - Exp(-arg1*I) / (2*I) $]
			F.Plus(F.Times(F.CN1, F.Power(F.Times(F.C2, F.CI), F.CN1), F.Exp(F.Times(F.CN1, arg1, F.CI))),
					F.Times(F.Power(F.Times(F.C2, F.CI), F.CN1), F.Exp(F.Times(arg1, F.CI)))); // $$;
		}
	}

	/**
	 * Sinc function.
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Sinc_function">Sinc function</a>
	 */
	private static class Sinc extends AbstractTrigArg1 implements INumeric, SincRules, DoubleUnaryOperator {

		@Override
		public double applyAsDouble(double operand) {
			if (F.isZero(operand)) {
				return 1.0;
			}
			return Math.sin(operand) / operand;
		}

		@Override
		public IExpr e1ApcomplexArg(Apcomplex arg1) {
			if (arg1.equals(Apcomplex.ZERO)) {
				return F.num(Apcomplex.ONE);
			}
			return F.complexNum(ApcomplexMath.sin(arg1).divide(arg1));
		}

		@Override
		public IExpr e1ApfloatArg(Apfloat arg1) {
			if (arg1.equals(Apcomplex.ZERO)) {
				return F.num(Apcomplex.ONE);
			}
			return F.num(ApfloatMath.sin(arg1).divide(arg1));
		}

		@Override
		public IExpr e1ComplexArg(Complex arg1) {
			if (arg1.equals(Complex.ZERO)) {
				return F.complexNum(Complex.ONE);
			}
			return F.complexNum(arg1.sin().divide(arg1));
		}

		@Override
		public IExpr e1DblArg(final double arg1) {
			if (F.isZero(arg1)) {
				return CD1;
			}
			return num(Math.sin(arg1) / arg1);
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			double a1 = stack[top];
			if (a1 == 0.0) {
				return 1.0;
			}
			return Math.sin(a1) / a1;
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
			if (arg1.isZero()) {
				return F.C1;
			}
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Sinc(negExpr);
			}
			return F.NIL;
		}

		@Override
		public IAST getRuleAST() {
			return RULES;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	// public static BinaryFunctorImpl<IExpr> integerLogFunction() {
	// return new BinaryFunctorImpl<IExpr>() {
	// /**
	// * Evaluate if Log(arg2)/Log(arg1) has an integer number result
	// */
	// @Override
	// public IExpr apply(IExpr arg1, IExpr arg2) {
	// if (arg1.isInteger() && arg2.isInteger()) {
	// return baseBLog((IInteger) arg2, (IInteger) arg1);
	// }
	// return F.NIL;
	// }
	// };
	// }

	/**
	 * Hyperbolic sine
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Hyperbolic_function">Hyperbolic function</a>
	 */
	private final static class Sinh extends AbstractTrigArg1
			implements INumeric, IRewrite, SinhRules, DoubleUnaryOperator {

		@Override
		public double applyAsDouble(double operand) {
			return Math.sinh(operand);
		}

		@Override
		public IExpr e1ApcomplexArg(Apcomplex arg1) {
			return F.complexNum(ApcomplexMath.sinh(arg1));
		}

		@Override
		public IExpr e1ApfloatArg(Apfloat arg1) {
			return F.num(ApfloatMath.sinh(arg1));
		}

		@Override
		public IExpr e1ComplexArg(final Complex arg1) {
			return F.complexNum(arg1.sinh());
		}

		@Override
		public IExpr e1DblArg(final double arg1) {
			return F.num(Math.sinh(arg1));
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			return Math.sinh(stack[top]);
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Negate(Sinh(negExpr));
			}
			IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
			if (imPart.isPresent()) {
				return F.Times(F.CI, F.Sin(imPart));
			}
			if (arg1.isZero()) {
				return F.C0;
			}
			if (arg1.isPlus()) {
				IAST list = AbstractFunctionEvaluator.peelOffPlusI((IAST) arg1, engine);
				if (list.isPresent()) {
					IExpr k = list.arg1();
					// arg1 == x - k/I * Pi
					if (k.isRational()) {
						IRational t = (IRational) k;
						if (t.isLT(F.C0)) {
							// Sinh(arg1-2*I*Pi*IntegerPart(1/2*t) + 2*I*Pi)
							return F.Sinh(F.Plus(arg1, F.Times(F.CN2, F.CI, F.Pi, F.IntegerPart(F.Times(F.C1D2, t))),
									F.Times(F.C2, F.CI, F.Pi)));
						} else if (t.isLT(F.C1D2)) {// t < 1/2
							return F.NIL;
						} else if (t.isLT(F.C1)) {// t < 1
							// I * Cosh(arg1-1/2*I*Pi)
							return F.Times(F.CI, F.Cosh(F.Plus(arg1, F.Times(F.CN1D2, F.CI, F.Pi))));
						} else if (t.isLT(F.C2)) {// t < 2
							// -Sinh(arg1 - I*Pi)
							return F.Negate(F.Sinh(F.Subtract(arg1, F.Times(F.CI, F.Pi))));
						}
						// Sinh(arg1-2*I*Pi*IntegerPart(1/2*t) )
						return F.Sinh(F.Plus(arg1, F.Times(F.CN2, F.CI, F.Pi, F.IntegerPart(F.Times(F.C1D2, t)))));
					} else if (k.isIntegerResult()) {
						// (-1)^k * Sinh( arg1 - list.arg2() )
						return F.Times(F.Power(F.CN1, k), F.Sinh(F.Subtract(arg1, list.arg2())));
					}
				}
			}
			if (arg1.isInterval()) {
				return IntervalSym.sinh((IAST) arg1);
			}
			return F.NIL;
		}

//		private static IExpr evalInterval(final IExpr arg1) {
//			IExpr l = arg1.lower();
//			IExpr u = arg1.upper();
//			if (l.isReal() || l.isNegativeInfinity()) {
//				l = F.Sinh.of(l);
//				if (l.isReal() || l.isNegativeInfinity()) {
//					if (u.isReal() || u.isInfinity()) {
//						u = F.Sinh.of(u);
//						if (u.isReal() || u.isInfinity()) {
//							return F.Interval(F.List(l, u));
//						}
//					}
//				}
//			}
//			return F.NIL;
//		}

		@Override
		public IAST getRuleAST() {
			return RULES;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}

		public IExpr rewriteExp(IExpr arg1, EvalEngine engine) {
			return
			// [$ -(1/(E^arg1*2)) + E^arg1/2 $]
			F.Plus(F.Negate(F.Power(F.Times(F.Exp(arg1), F.C2), F.CN1)), F.Times(F.C1D2, F.Exp(arg1))); // $$;
		}
	}

	/**
	 * Tan
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Trigonometric_functions">Trigonometric functions</a>
	 */
	private final static class Tan extends AbstractTrigArg1
			implements INumeric, IRewrite, TanRules, DoubleUnaryOperator {

		@Override
		public double applyAsDouble(double operand) {
			return Math.tan(operand);
		}

		@Override
		public IExpr e1ApcomplexArg(Apcomplex arg1) {
			return F.complexNum(ApcomplexMath.tan(arg1));
		}

		@Override
		public IExpr e1ApfloatArg(Apfloat arg1) {
			return F.num(ApfloatMath.tan(arg1));
		}

		@Override
		public IExpr e1ComplexArg(final Complex arg1) {
			return F.complexNum(arg1.tan());
		}

		@Override
		public IExpr e1DblArg(final double arg1) {
			return F.num(Math.tan(arg1));
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			return Math.tan(stack[top]);
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {

			if (arg1.isPlus()) {
				IExpr k = AbstractFunctionEvaluator.peelOffPlusRational((IAST) arg1, engine);
				if (k != null) {
					// arg1 == x + k*Pi
					if (k.isRational()) {
						IRational t = (IRational) k;
						if (t.isLT(F.C0)) { // t < 0
							// Tan(arg1 + Pi - Pi*IntegerPart(t))
							return F.Tan(F.Plus(arg1, F.Pi, F.Times(F.CNPi, t.integerPart())));
						} else if (t.isLT(F.C1D2)) {// t < 1/2
							return F.NIL;
						} else if (t.isLT(F.C1)) {// t < 1
							// -Cot(arg1-1/2*Pi)
							return F.Negate(F.Cot(F.Plus(arg1, F.Times(F.CN1D2, F.Pi))));
						}
						// Tan(arg1 - Pi*IntegerPart(t))
						return F.Tan(F.Plus(arg1, F.Times(F.CNPi, t.integerPart())));
					} else if (k.isIntegerResult()) {
						// Tan( arg1 - k*Pi )
						return F.Tan(F.Subtract(arg1, F.Times(k, F.Pi)));
					}
				}
			}

			IExpr imPart = AbstractFunctionEvaluator.getComplexExpr(arg1, F.CNI);
			if (imPart.isPresent()) {
				return F.Times(F.CI, F.Tanh(imPart));
			}
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Negate(Tan(negExpr));
			}

			if (arg1.isTimes()) {
				IAST timesAST = (IAST) arg1;
				if (timesAST.size() == 3 && timesAST.arg1().isRational() && timesAST.arg2().isPi()) {
					// t should be positive here!
					IRational t = (IRational) timesAST.arg1();
					if (t.isLT(F.C1D2)) {// t < 1/2
						return F.NIL;
					} else if (t.equals(F.C1D2)) {// t == 1/2
						return F.CComplexInfinity;
					} else if (t.isLT(F.C1)) {// t < 1
						// -Tan((1-t)*Pi)
						return F.Negate(F.Tan(F.Times(F.Subtract(F.C1, t), F.Pi)));
					} else if (t.isLT(F.C2)) {// t < 2
						// Tan((t-1)*Pi)
						return F.Tan(F.Times(F.Subtract(t, F.C1), F.Pi));
					}
					// Tan((t-2*Quotient(IntegerPart(t),2))*Pi)
					return F.Tan(F.Times(F.Pi, F.Plus(t, ExpTrigsFunctions.integerPartFolded2(t))));
				}
				IExpr t = AbstractFunctionEvaluator.peelOfTimes(timesAST, Pi);
				if (t.isPresent() && t.im().isZero()) {
					if (t.isIntegerResult()) {
						return F.C0;
					}
					// t - 1/2
					IExpr temp = engine.evaluate(F.Plus(t, F.CN1D2));
					if (temp.isIntegerResult()) {
						return F.CComplexInfinity;
					}
				}
			}

			if (arg1.isInterval()) {
				return IntervalSym.tan((IAST) arg1);
			}

			// IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
			// if (imPart.isPresent()) {
			// return F.Times(F.CI, F.Tanh(imPart));
			// }

			return F.NIL;
		}

		@Override
		public IAST getRuleAST() {
			return RULES;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}

		public IExpr rewriteExp(IExpr arg1, EvalEngine engine) {
			// if (arg1.isTrigFunction() || arg1.isHyperbolicFunction()) {
			// arg1 = arg1.rewrite(ID.Exp);
			// }
			return
			// [$ (I*(E^((-I)*arg1) - E^(I*arg1)))/(E^((-I)*arg1) + E^(I*arg1)) $]
			F.Times(F.CI, F.Subtract(F.Exp(F.Times(F.CNI, arg1)), F.Exp(F.Times(F.CI, arg1))),
					F.Power(F.Plus(F.Exp(F.Times(F.CNI, arg1)), F.Exp(F.Times(F.CI, arg1))), F.CN1)); // $$;
		}
	}

	/**
	 * Hyperbolic tangent
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Hyperbolic_function">Hyperbolic function</a>
	 */
	private final static class Tanh extends AbstractTrigArg1
			implements INumeric, IRewrite, TanhRules, DoubleUnaryOperator {

		@Override
		public double applyAsDouble(double operand) {
			return Math.tanh(operand);
		}

		@Override
		public IExpr e1ApcomplexArg(Apcomplex arg1) {
			return F.complexNum(ApcomplexMath.tanh(arg1));
		}

		@Override
		public IExpr e1ApfloatArg(Apfloat arg1) {
			return F.num(ApfloatMath.tanh(arg1));
		}

		@Override
		public IExpr e1ComplexArg(final Complex arg1) {
			return F.complexNum(arg1.tanh());
		}

		@Override
		public IExpr e1DblArg(final double arg1) {
			return F.num(Math.tanh(arg1));
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			return Math.tanh(stack[top]);
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Negate(Tanh(negExpr));
			}
			IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
			if (imPart.isPresent()) {
				return F.Times(F.CI, F.Tan(imPart));
			}
			if (arg1.isZero()) {
				return F.C0;
			}

			if (arg1.isPlus()) {
				IAST list = AbstractFunctionEvaluator.peelOffPlusI((IAST) arg1, engine);
				if (list.isPresent()) {
					IExpr k = list.arg1();
					// arg1 == x - k/I * Pi
					if (k.isRational()) {
						IRational t = (IRational) k;
						if (t.isLT(F.C0)) {
							// Tanh(arg1-I*Pi*IntegerPart(t) + I*Pi )
							return F.Tanh(F.Plus(arg1, F.Times(F.CNI, F.Pi, t.integerPart()), F.Times(F.CI, F.Pi)));
						} else if (t.isLT(F.C1D2)) {// t < 1/2
							return F.NIL;
						} else if (t.isLT(F.C1)) {// t < 1
							// Coth(arg1-1/2*I*Pi)
							return F.Coth(F.Plus(arg1, F.Times(F.CN1D2, F.CI, F.Pi)));
						}
						// Tanh( arg1 - I*Pi*IntegerPart(t) )
						return F.Tanh(F.Plus(arg1, F.Times(F.CNI, F.Pi, t.integerPart())));
					} else if (k.isIntegerResult()) {
						// Tanh( arg1 - list.arg2() )
						return F.Tanh(F.Subtract(arg1, list.arg2()));
					}
				}
			}
			if (arg1.isInterval()) {
				return IntervalSym.tanh((IAST) arg1);
			}
			return F.NIL;
		}

		@Override
		public IAST getRuleAST() {
			return RULES;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}

		public IExpr rewriteExp(IExpr arg1, EvalEngine engine) {
			return
			// [$ -(1/(E^arg1*(E^(-arg1) + E^arg1))) + E^arg1/(E^(-arg1) + E^arg1) $]
			F.Plus(F.Negate(F.Power(F.Times(F.Exp(arg1), F.Plus(F.Exp(F.Negate(arg1)), F.Exp(arg1))), F.CN1)),
					F.Times(F.Exp(arg1), F.Power(F.Plus(F.Exp(F.Negate(arg1)), F.Exp(arg1)), F.CN1))); // $$;
		}
	}

	/**
	 * Try simplifying <code>ArcTan(Tan(z))</code> or <code>ArcCot(Cot(z))</code>
	 * 
	 * @param arg1
	 *            is assumed to be <code>Tan(z)</code> or <code>Cot(z)</code>
	 * @return
	 */
	private static IExpr arcTanArcCotInverse(final IExpr arg1) {
		IExpr z = arg1.first();
		IExpr zRe = z.re();
		IExpr k = F.Quotient.of(F.Subtract(zRe, F.CPiHalf), F.Pi).inc();
		if (k.isInteger()) {
			// -Pi/2 + k*Pi
			IExpr min = F.Times.of(F.Plus(F.CN1D2, k), Pi);
			// Pi/2 + k*Pi
			IExpr max = F.Times.of(F.Plus(F.C1D2, k), Pi);
			// z - k*Pi
			IAST result = F.Subtract(z, F.Times(k, F.Pi));
			// (-(Pi/2) + k*Pi < Re(z) < Pi/2 + k*Pi
			if (F.Less.ofQ(min, zRe) && F.Greater.ofQ(max, zRe)) {
				// z - k*Pi
				return result;
			}

			IExpr zIm = z.im();
			// (Re(z) == -(Pi/2) + k*Pi && Im(z) < 0)
			if (zIm.isNegativeResult() && F.Equal.ofQ(zRe, min)) {
				// z - k*Pi
				return result;
			}

			if (arg1.isTan()) { // Tan(z)
				// (Re(z) == Pi/2 + k*Pi && Im(z) > 0)
				if (zIm.isPositiveResult() && F.Equal.ofQ(zRe, max)) {
					// z - k*Pi
					return result;
				}
			} else { // Cot(z)
				// (Re(z) == Pi/2 + k*Pi && Im(z) >= 0)
				if (zIm.isNonNegativeResult() && F.Equal.ofQ(zRe, max)) {
					// z - k*Pi
					return result;
				}
			}
		}
		return F.NIL;
	}

	private static IComplexNum log(final IComplexNum val) {
		if (val instanceof ApcomplexNum) {
			return ApcomplexNum.valueOf(ApcomplexMath.log(((ApcomplexNum) val).apcomplexValue()));
		}
		ComplexNum z = (ComplexNum) val;
		if (z.isNaN()) {
			return ComplexNum.NaN;
		}
		return ComplexNum.valueOf(Math.log(z.dabs()), Math.atan2(z.imDoubleValue(), z.reDoubleValue()));
	}

	/**
	 * Try simplifying <code>ArcSin(Sin(z))</code>
	 * 
	 * @param arg1
	 *            is assumed to be <code>Tan(z)</code> or <code>Cot(z)</code>
	 * @return
	 */
	private static IExpr arcSinSin(final IExpr arg1) {
		IExpr z = arg1.first();
		IExpr zRe = z.re();
		IExpr k = F.Quotient.of(F.Subtract(zRe, F.CPiHalf), F.Pi).inc();
		if (k.isInteger()) {
			// -Pi/2 + k*Pi
			IExpr min = F.Times.of(F.Plus(F.CN1D2, k), Pi);
			// Pi/2 + k*Pi
			IExpr max = F.Times.of(F.Plus(F.C1D2, k), Pi);
			// (-1)^k * (z - Pi*k)
			IAST result = F.Times(F.Power(F.CN1, k), F.Subtract(z, F.Times(k, F.Pi)));
			// (-(Pi/2) + k*Pi < Re(z) < Pi/2 + k*Pi
			if (F.Less.ofQ(min, zRe) && F.Greater.ofQ(max, zRe)) {
				// (-1)^k * (z - Pi*k)
				return result;
			}

			IExpr zIm = z.im();
			// (Re(z) == -(Pi/2) + k*Pi && Im(z) < 0)
			if (zIm.isNonNegativeResult() && F.Equal.ofQ(zRe, min)) {
				// (-1)^k * (z - Pi*k)
				return result;
			}

			// (Re(z) == Pi/2 + k*Pi && Im(z) > 0)
			if ((zIm.isNegativeResult() || zIm.isZero()) && F.Equal.ofQ(zRe, max)) {
				// (-1)^k * (z - Pi*k)
				return result;
			}
		}
		return F.NIL;
	}

	/**
	 * Try simplifying <code>ArcCos(Cos(z))</code>
	 * 
	 * @param arg1
	 *            is assumed to be <code>Tan(z)</code> or <code>Cot(z)</code>
	 * @return
	 */
	private static IExpr arcCosCos(final IExpr arg1) {
		IExpr z = arg1.first();
		IExpr zRe = z.re();
		IExpr k = F.Quotient.of(F.Subtract(zRe, F.Pi), F.Pi).inc();
		if (k.isInteger()) {
			// k*Pi
			IExpr min = F.Times.of(k, Pi);
			// (k+1)*Pi
			IExpr max = F.Times.of(k.inc(), Pi);
			// (-1)^k * (z - Pi*k - Pi/2) + Pi/2
			IAST result = F.Plus(F.Times(F.Power(F.CN1, k), F.Plus(z, F.Times(F.CN1, k, F.Pi), F.CNPiHalf)), F.CPiHalf);

			// (k*Pi < Re(z) < (k + 1)*Pi
			if (F.Less.ofQ(min, zRe) && F.Greater.ofQ(max, zRe)) {
				return result;
			}

			IExpr zIm = z.im();
			// (Re(z) == k*Pi && Im(z) >= 0)
			if (zIm.isNonNegativeResult() && F.Equal.ofQ(zRe, min)) {
				return result;
			}

			// (Re(z) == (k + 1)*Pi && Im(z) <= 0)
			if ((zIm.isNegativeResult() || zIm.isZero()) && F.Equal.ofQ(zRe, max)) {
				return result;
			}
		}
		return F.NIL;
	}

	/**
	 * Integer logarithm of <code>arg</code> for base <code>b</code>. Gives Log <sub>b</sub>(arg) or
	 * <code>Log(arg)/Log(b)</code>.
	 * 
	 * @param b
	 *            the base of the logarithm
	 * @param arg
	 * @return
	 */
	public static IExpr baseBLog(final IInteger b, final IInteger arg) {
		try {
			long l1 = b.toLong();
			long l2 = arg.toLong();
			double res = Math.log(l2) / Math.log(l1);
			if (F.isNumIntValue(res)) {
				int r = Double.valueOf(Math.round(res)).intValue();
				if (arg.equals(b.pow(r))) {
					return F.ZZ(r);
				}
			}
		} catch (ArithmeticException ae) {
			// toLong() method failed
		}
		return F.NIL;
	}

	public static void initialize() {
		Initializer.init();
	}

	private ExpTrigsFunctions() {

	}

	/**
	 * <code>-2 * IntegerPart( IntegerPart(r) / 2 )</code>
	 * 
	 * @param r
	 *            a rational number
	 * @return
	 */
	private static IRational integerPartFolded2(IRational r) {
		return r.integerPart().multiply(F.C1D2).integerPart().multiply(F.CN2);
	}

}
