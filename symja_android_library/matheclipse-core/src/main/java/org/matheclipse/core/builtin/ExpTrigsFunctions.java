package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.F.ArcCos;
import static org.matheclipse.core.expression.F.ArcCot;
import static org.matheclipse.core.expression.F.ArcCoth;
import static org.matheclipse.core.expression.F.ArcCsc;
import static org.matheclipse.core.expression.F.ArcCsch;
import static org.matheclipse.core.expression.F.ArcSin;
import static org.matheclipse.core.expression.F.ArcSinh;
import static org.matheclipse.core.expression.F.ArcTan;
import static org.matheclipse.core.expression.F.ArcTanh;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CD1;
import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CNI;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Cosh;
import static org.matheclipse.core.expression.F.Cot;
import static org.matheclipse.core.expression.F.Coth;
import static org.matheclipse.core.expression.F.Csc;
import static org.matheclipse.core.expression.F.Csch;
import static org.matheclipse.core.expression.F.Divide;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Pi;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sec;
import static org.matheclipse.core.expression.F.Sech;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Sinc;
import static org.matheclipse.core.expression.F.Sinh;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Tan;
import static org.matheclipse.core.expression.F.Tanh;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.num;

import com.duy.lambda.DoubleUnaryOperator;

import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.hipparchus.complex.Complex;
import org.hipparchus.util.FastMath;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ComplexResultException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractArg1;
import org.matheclipse.core.eval.interfaces.AbstractArg12;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.eval.util.AbstractAssumptions;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.ComplexUtils;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.expression.NumberUtil;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
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
	static {

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

	private final static class AngleVector extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			IExpr arg1 = ast.arg1();
			IExpr phi;
			if (ast.isAST2()) {
				IExpr arg2 = ast.arg2();

				if (arg1.isAST(F.List, 3)) {
					IExpr x = ((IAST) arg1).arg1();
					IExpr y = ((IAST) arg1).arg2();
					if (arg2.isAST(F.List, 3)) {
						// 'AngleVector[{x_, y_}, {r_, phi_}]': '{x + r * Cos[phi], y + r * Sin[phi]}'
						IExpr r = ((IAST) arg2).arg1();
						phi = ((IAST) arg2).arg2();
						return F.List(F.Plus(x, F.Times(r, F.Cos(phi))), F.Plus(y, F.Times(r, F.Sin(phi))));
					} else {
						phi = arg2;
					}
					// 'AngleVector[{x_, y_}, phi_]': '{x + Cos[phi], y + Sin[phi]}'
					return F.List(F.Plus(x, F.Cos(phi)), F.Plus(y, F.Sin(phi)));
				}
				return F.NIL;
			}

			if (arg1.isAST(F.List, 3)) {
				// 'AngleVector[{r_, phi_}]': '{r * Cos[phi], r * Sin[phi]}'
				IExpr r = ((IAST) arg1).arg1();
				phi = ((IAST) arg1).arg2();
				return F.List(F.Times(r, F.Cos(phi)), F.Times(r, F.Sin(phi)));
			} else {
				phi = arg1;
			}
			// 'AngleVector[phi_]': '{Cos[phi], Sin[phi]}'
			return F.List(F.Cos(phi), F.Sin(phi));
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.NUMERICFUNCTION);
		}

	}

	/**
	 * Arccosine
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Inverse_trigonometric functions" > Inverse_trigonometric functions</a>
	 */
	private final static class ArcCos extends AbstractTrigArg1 implements INumeric, ArcCosRules, DoubleUnaryOperator {

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
		public IExpr evaluateArg1(final IExpr arg1) {
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Plus(Negate(Pi), ArcCos(negExpr));
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

	/**
	 * Inverse hyperbolic cosine
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Inverse_hyperbolic_function"> Inverse hyperbolic functions</a>
	 */
	private final static class ArcCosh extends AbstractTrigArg1 implements INumeric, ArcCoshRules, DoubleUnaryOperator {

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
		public IExpr evaluateArg1(final IExpr arg1) {
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

	/**
	 * Arccotangent
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Inverse_trigonometric functions" > Inverse_trigonometric functions</a>
	 */
	private final static class ArcCot extends AbstractTrigArg1 implements ArcCotRules, DoubleUnaryOperator {

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
		public IExpr evaluateArg1(final IExpr arg1) {
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Plus(Negate(Pi), ArcCot(negExpr));
			}
			IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
			if (imPart.isPresent()) {
				return F.Times(F.CNI, F.ArcCoth(imPart));
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

	/**
	 * Arccotangent hyperbolic
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Inverse_hyperbolic_function"> Inverse hyperbolic functions</a>
	 */
	private final static class ArcCoth extends AbstractTrigArg1 implements ArcCothRules, DoubleUnaryOperator {

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
		public IExpr evaluateArg1(final IExpr arg1) {
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
	}

	/**
	 * Inverse hyperbolic tangent
	 * 
	 * See <a href="https://en.wikipedia.org/wiki/Inverse_trigonometric_functions">Inverse trigonometric functions</a>
	 */
	private final static class ArcCsc extends AbstractTrigArg1 implements ArcCscRules {
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
		public IExpr evaluateArg1(final IExpr arg1) {
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Negate(ArcCsc(negExpr));
			}
			IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
			if (imPart.isPresent()) {
				return F.Times(F.CNI, F.ArcCsch(imPart));
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	/**
	 * Inverse hyperbolic tangent
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Inverse_hyperbolic_function"> Inverse hyperbolic functions</a>
	 */
	private final static class ArcCsch extends AbstractTrigArg1 implements ArcCschRules {

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
		public IExpr evaluateArg1(final IExpr arg1) {
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Negate(ArcCsch(negExpr));
			}
			IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
			if (imPart.isPresent()) {
				return F.Times(F.CNI, F.ArcCsc(imPart));
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	/**
	 * Inverse hyperbolic secant
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Inverse_hyperbolic_function"> Inverse hyperbolic functions</a>
	 */
	private final static class ArcSech extends AbstractTrigArg1 implements ArcSechRules {

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
		public IExpr evaluateArg1(final IExpr arg1) {
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	private final static class ArcSec extends AbstractTrigArg1 implements ArcSecRules {
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
		public IExpr evaluateArg1(final IExpr arg1) {
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	/**
	 * Arcsine
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Inverse_trigonometric functions" > Inverse_trigonometric functions</a>
	 */
	private final static class ArcSin extends AbstractTrigArg1 implements INumeric, ArcSinRules, DoubleUnaryOperator {

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
		public IExpr evaluateArg1(final IExpr arg1) {
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Negate(ArcSin(negExpr));
			}
			IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
			if (imPart.isPresent()) {
				return F.Times(F.CI, F.ArcSinh(imPart));
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

	/**
	 * Arcsin hyperbolic
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Inverse_hyperbolic_function"> Inverse hyperbolic functions</a>
	 */
	private final static class ArcSinh extends AbstractTrigArg1 implements INumeric, ArcSinhRules, DoubleUnaryOperator {

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
		public IExpr evaluateArg1(final IExpr arg1) {
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Negate(ArcSinh(negExpr));
			}
			IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
			if (imPart.isPresent()) {
				return F.Times(F.CI, F.ArcSin(imPart));
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

	/**
	 * Arctangent
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Inverse_trigonometric functions" > Inverse_trigonometric functions</a>
	 */
	private final static class ArcTan extends AbstractArg12 implements INumeric, ArcTanRules {

		@Override
		public IAST getRuleAST() {
			return RULES;
		}

		@Override
		public IExpr e1ObjArg(final IExpr arg1) {
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Negate(ArcTan(negExpr));
			}
			IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
			if (imPart.isPresent()) {
				return F.Times(F.CI, F.ArcTanh(imPart));
			}
			return F.NIL;
		}

		@Override
		public IExpr e2ObjArg(final IExpr x, final IExpr y) {
			if (x.isZero() && y.isSignedNumber()) {
				if (y.isZero()) {
					return F.Indeterminate;
				}
				if (y.isPositive()) {
					return F.Times(F.C1D2, F.Pi);
				}
				return F.Times(F.CN1D2, F.Pi);
			}
			if (y.isZero() && x.isSignedNumber() && !x.isZero()) {
				return F.Times(F.Subtract(F.C1, F.UnitStep(x)), F.Pi);
			}
			if (x.isNumber() && y.isSignedNumber()) {
				if (((INumber) x).re().isNegative()) {
					return F.Plus(F.ArcTan(F.Divide(y, x)),
							F.Times(F.Subtract(F.Times(F.C2, F.UnitStep(y)), F.C1), F.Pi));
				}
				IExpr argX = x.complexArg();
				// -Pi/2 < Arg(x) <= Pi/2
				if (argX.isPresent() && F.evalTrue(
						F.And(F.Less(F.Times(F.CN1D2, F.Pi), argX), F.LessEqual(argX, F.Times(F.C1D2, F.Pi))))) {
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
		public IExpr e1DblComArg(final IComplexNum c) {
			return ComplexUtils.atan((ComplexNum) c);
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
	}

	/**
	 * Inverse hyperbolic tangent
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Inverse_hyperbolic_function"> Inverse hyperbolic functions</a>
	 */
	private final static class ArcTanh extends AbstractTrigArg1 implements INumeric, ArcTanhRules, DoubleUnaryOperator {

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
		public IExpr evaluateArg1(final IExpr arg1) {
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Negate(ArcTanh(negExpr));
			}
			IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
			if (imPart.isPresent()) {
				return F.Times(F.CI, F.ArcTan(imPart));
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

	/**
	 * Cosine function
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Trigonometric_functions">Trigonometric functions</a> and
	 * <a href="http://en.wikipedia.org/wiki/Exact_trigonometric_constants"> Wikipedia - Exact trigonometric
	 * constants</a>
	 */
	private final static class Cos extends AbstractTrigArg1 implements INumeric, CosRules, DoubleUnaryOperator {

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
		public IExpr evaluateArg1(final IExpr arg1) {
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Cos(negExpr);
			}

			IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
			if (imPart.isPresent()) {
				return F.Cosh(imPart);
			}

			IAST parts = AbstractFunctionEvaluator.getPeriodicParts(arg1, Pi);
			if (parts.isPresent()) {
				if (parts.arg2().isInteger()) {
					// period n*Pi
					IInteger i = (IInteger) parts.arg2();
					if (i.isEven()) {
						return Cos(parts.arg1());
					} else {
						return Negate(F.Cos(parts.arg1()));
					}
				}
				if (parts.arg2().isFraction()) {
					// period (n/m)*Pi
					IFraction f = (IFraction) parts.arg2();
					IInteger[] divRem = f.divideAndRemainder();
					IFraction rest = F.fraction(divRem[1], f.getDenominator());
					if (!NumberUtil.isZero(divRem[0])) {

						if (divRem[0].isEven()) {
							return Cos(Plus(parts.arg1(), Times(rest, Pi)));
						} else {
							return Negate(Cos(Plus(parts.arg1(), Times(rest, Pi))));
						}
					}

					if (rest.equals(C1D2)) {
						// Cos(z) == Sin(Pi/2 - z)
						return Sin(Subtract(Divide(Pi, C2), arg1));
					}
				}
				if (parts.arg2().isIntegerResult()) {
					// period n*Pi
					return Times(Power(CN1, parts.arg2()), Cos(parts.arg1()));
				}
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

	/**
	 * Hyperbolic cotangent
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Hyperbolic_function">Hyperbolic function</a>
	 */
	private final static class Coth extends AbstractTrigArg1 implements INumeric, CothRules, DoubleUnaryOperator {

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
		public IExpr evaluateArg1(final IExpr arg1) {
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

	/**
	 * Cosecant function
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Trigonometric_functions">Trigonometric functions</a>
	 */
	private final static class Csc extends AbstractTrigArg1 implements INumeric, CscRules, DoubleUnaryOperator {

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
		public IExpr evaluateArg1(final IExpr arg1) {
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Negate(Csc(negExpr));
			}
			IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
			if (imPart.isPresent()) {
				return F.Times(F.CNI, F.Csch(imPart));
			}
			IAST parts = AbstractFunctionEvaluator.getPeriodicParts(arg1, Pi);
			if (parts.isPresent()) {
				if (parts.arg2().isInteger()) {
					// period 2*Pi
					IInteger i = (IInteger) parts.arg2();
					if (i.isEven()) {
						return F.Csc(parts.arg1());
					} else {
						return F.Times(F.CN1, F.Csc(parts.arg1()));
					}
				}
				if (parts.arg2().isFraction()) {
					// period (n/m)*Pi
					IFraction f = (IFraction) parts.arg2();
					IInteger[] divRem = f.divideAndRemainder();
					if (!divRem[0].isZero()) {
						IFraction rest = F.fraction(divRem[1], f.getDenominator());
						if (divRem[0].isEven()) {
							return Csc(Plus(parts.arg1(), Times(rest, Pi)));
						} else {
							return Times(CN1, Csc(Plus(parts.arg1(), Times(rest, Pi))));
						}
					}

					if (f.equals(C1D2)) {
						// Csc(z) == Sec(Pi/2 - z)
						return Sec(Subtract(Divide(Pi, C2), arg1));
					}
				}

				if (parts.arg2().isIntegerResult()) {
					// period n*Pi
					return Times(Power(CN1, parts.arg2()), Csc(parts.arg1()));
				}
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

	/**
	 * Hyperbolic cosine
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Hyperbolic_function">Hyperbolic function</a>
	 */
	private final static class Cosh extends AbstractTrigArg1 implements INumeric, CoshRules, DoubleUnaryOperator {

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
		public IExpr evaluateArg1(final IExpr arg1) {
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Cosh(negExpr);
			}
			IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
			if (imPart.isPresent()) {
				return F.Cos(imPart);
			}
			if (arg1.isZero()) {
				return F.C1;
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

	/**
	 * Cotangent function
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Trigonometric_functions">Trigonometric functions</a>
	 */
	private final static class Cot extends AbstractTrigArg1 implements INumeric, CotRules, DoubleUnaryOperator {

		@Override
		public double applyAsDouble(double operand) {
			return Math.cos(operand) / Math.sin(operand);
		}

		@Override
		public IExpr e1ApcomplexArg(Apcomplex arg1) {
			return F.complexNum(ApcomplexMath.cos(arg1).divide(ApcomplexMath.sin(arg1)));
		}

		@Override
		public IExpr e1ApfloatArg(Apfloat arg1) {
			return F.num(ApfloatMath.cos(arg1).divide(ApfloatMath.sin(arg1)));
		}

		@Override
		public IExpr e1ComplexArg(final Complex arg1) {
			return F.complexNum(arg1.cos().divide(arg1.sin()));
		}

		@Override
		public IExpr e1DblArg(final double arg1) {
			return F.num(Math.cos(arg1) / Math.sin(arg1));
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			return 1.0D / Math.tan(stack[top]);
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1) {
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Negate(Cot(negExpr));
			}
			IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
			if (imPart.isPresent()) {
				return Times(CNI, Coth(imPart));
			}
			IAST parts = AbstractFunctionEvaluator.getPeriodicParts(arg1, Pi);
			if (parts.isPresent()) {
				if (parts.arg2().isInteger()) {
					// period Pi
					return Cot(parts.arg1());
				}
				if (parts.arg2().isFraction()) {
					// period (n/m)*Pi
					IFraction f = (IFraction) parts.arg2();
					IInteger[] divRem = f.divideAndRemainder();
					IFraction rest = F.fraction(divRem[1], f.getDenominator());
					if (!divRem[0].isZero()) {
						return Cot(Plus(parts.arg1(), Times(rest, Pi)));
					}

					if (rest.equals(C1D2)) {
						// Cot(z) == Tan(Pi/2 - z)
						return Tan(Subtract(Divide(Pi, C2), arg1));
					}

				}

				if (parts.arg2().isIntegerResult()) {
					// period Pi
					return Cot(parts.arg1());
				}

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

	/**
	 * Hyperbolic Cosecant function
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Hyperbolic_function">Hyperbolic functions</a>
	 */
	private final static class Csch extends AbstractTrigArg1 implements INumeric, CschRules, DoubleUnaryOperator {

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
		public IExpr evaluateArg1(IExpr arg1) {
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
			Validate.checkSize(ast, 2);
			return F.Power(F.Sin(F.C1D2.times(ast.arg1())), F.C2);
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		}

	}

	private final static class InverseHaversine extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			return F.Times(F.C2, F.ArcSin(F.Sqrt(ast.arg1())));
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

		/**
		 * <pre>
		 * { Log[1]=0, Log[E]=1, Log[E^(x_Integer)]:=x, Log[E^(x_Rational)]:=x, Log[E^(I)]=I, Log[Exp[-I]]=(-I),
		 * Log[0]=(-Infinity) }
		 * </pre>
		 */

		@Override
		public IAST getRuleAST() {
			// if (RULES == null) {
			// RULES = List(Set(Log(Power(E, Times(CN1, CI))), Times(CN1, CI)),
			// Set(Log(Power(E, CI)), CI),
			// Set(Log(C0), Times(CN1, CInfinity)), Set(Log(C1), C0), Set(Log(E),
			// C1),
			// SetDelayed(Log(Power(E, $p("x", $s("Integer")))), $s("x")),
			// SetDelayed(Log(Power(E, $p("x", $s("Rational")))), $s("x")));
			// }
			return RULES;
		}

		@Override
		public IExpr e1DblArg(final INum arg1) {
			return Num.valueOf(Math.log(arg1.getRealPart()));
		}

		@Override
		public IExpr e1DblComArg(final IComplexNum arg1) {
			return ComplexUtils.log((ComplexNum) arg1);
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
		public IExpr e1ObjArg(IExpr expr) {
			if (expr.isPower()) {
				IExpr base = expr.getAt(1);
				IExpr exponent = expr.getAt(2);
				// arg2*Log(arg1)
				IExpr temp = F.eval(Times(exponent, F.Log(base)));
				IExpr imTemp = F.eval(F.Im(temp));
				if (imTemp.isSignedNumber()) {
					if (((ISignedNumber) imTemp).isGreaterThan(F.num(-1 * Math.PI))
							&& ((ISignedNumber) imTemp).isLessThan(F.num(Math.PI))) {
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
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(expr);
			if (negExpr.isPresent()) {
				if (negExpr.isPositiveResult()) {
					return F.Plus(F.Log(negExpr), F.Times(CI, F.Pi));
				}
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
			Validate.checkSize(ast, 2);
			IExpr arg1 = ast.arg1();
			if (arg1.isZero()) {
				return F.C1D2;
			}
			if (arg1.isInfinity()) {
				return F.C1;
			}
			if (arg1.isNegativeInfinity()) {
				return F.C0;
			}
			if (arg1.isNumber()) {
				// 1 / (1 + Exp(-arg1))
				return F.Power(F.Plus(F.C1, F.Power(F.E, F.Times(F.CN1, arg1))), F.CN1);
			}
			return F.NIL;
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
	private final static class Sec extends AbstractTrigArg1 implements INumeric, SecRules, DoubleUnaryOperator {

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
		public IExpr evaluateArg1(final IExpr arg1) {
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Sec(negExpr);
			}
			IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
			if (imPart.isPresent()) {
				return F.Sech(imPart);
			}
			IAST parts = AbstractFunctionEvaluator.getPeriodicParts(arg1, Pi);
			if (parts.isPresent()) {
				if (parts.arg2().isInteger()) {
					// period 2*Pi
					IInteger i = (IInteger) parts.arg2();
					if (i.isEven()) {
						return Sec(parts.arg1());
					} else {
						return Negate(Sec(parts.arg1()));
					}
				}

				if (parts.arg2().isFraction()) {
					// period (n/m)*Pi
					IFraction f = (IFraction) parts.arg2();
					IInteger[] divRem = f.divideAndRemainder();
					if (!divRem[0].isZero()) {
						IFraction rest = F.fraction(divRem[1], f.getDenominator());
						if (divRem[0].isEven()) {
							return Sec(Plus(parts.arg1(), Times(rest, Pi)));
						} else {
							return Times(CN1, Sec(Plus(parts.arg1(), Times(rest, Pi))));
						}
					}

					if (f.equals(C1D2)) {
						return Times(CN1, Csc(parts.arg1()));
					}
				}

				if (parts.arg2().isIntegerResult()) {
					// period n*Pi
					return Times(Power(CN1, parts.arg2()), Sec(parts.arg1()));
				}
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

	/**
	 * Hyperbolic Secant function
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Hyperbolic_function">Hyperbolic functions</a>
	 */
	private final static class Sech extends AbstractTrigArg1 implements INumeric, SechRules, DoubleUnaryOperator {

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
		public IExpr evaluateArg1(IExpr arg1) {
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

	/**
	 * Sine function.
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Trigonometric_functions">Trigonometric functions</a>
	 */
	private final static class Sin extends AbstractTrigArg1 implements INumeric, SinRules, DoubleUnaryOperator {

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
		public IExpr evaluateArg1(final IExpr arg1) {
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Negate(Sin(negExpr));
			}
			IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
			if (imPart.isPresent()) {
				return F.Times(F.CI, F.Sinh(imPart));
			}
			IAST parts = AbstractFunctionEvaluator.getPeriodicParts(arg1, Pi);
			if (parts.isPresent()) {
				if (parts.arg2().isInteger()) {
					// period n*Pi
					IInteger i = (IInteger) parts.arg2();
					if (i.isEven()) {
						return Sin(parts.arg1());
					} else {
						return Times(CN1, Sin(parts.arg1()));
					}
				}

				if (parts.arg2().isFraction()) {
					// period (n/m)*Pi
					IFraction f = (IFraction) parts.arg2();
					IInteger[] divRem = f.divideAndRemainder();
					IFraction rest = F.fraction(divRem[1], f.getDenominator());
					if (!divRem[0].isZero()) {

						if (divRem[0].isEven()) {
							return Sin(Plus(parts.arg1(), Times(rest, Pi)));
						} else {
							return Times(CN1, Sin(Plus(parts.arg1(), Times(rest, Pi))));
						}
					}

					if (rest.equals(C1D2)) {
						// Sin(z) == Cos(Pi/2 - z)
						return Cos(Subtract(Divide(Pi, C2), arg1));
					}
				}

				if (parts.arg2().isIntegerResult()) {
					// period n*Pi
					return Times(Power(CN1, parts.arg2()), Sin(parts.arg1()));
				}

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

	/**
	 * Sinc function.
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Sinc_function">Sinc function</a>
	 */
	private static class Sinc extends AbstractTrigArg1 implements INumeric, SincRules, DoubleUnaryOperator {

		@Override
		public double applyAsDouble(double operand) {
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
			if (arg1 == 0.0) {
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
		public IExpr evaluateArg1(final IExpr arg1) {
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
	private final static class Sinh extends AbstractTrigArg1 implements INumeric, SinhRules, DoubleUnaryOperator {

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
		public IExpr evaluateArg1(final IExpr arg1) {
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

	/**
	 * Tan
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Trigonometric_functions">Trigonometric functions</a>
	 */
	private final static class Tan extends AbstractTrigArg1 implements INumeric, TanRules, DoubleUnaryOperator {

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
		public IExpr evaluateArg1(final IExpr arg1) {
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Negate(Tan(negExpr));
			}
			IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
			if (imPart.isPresent()) {
				return F.Times(F.CI, F.Tanh(imPart));
			}
			IAST parts = AbstractFunctionEvaluator.getPeriodicParts(arg1, F.Pi);
			if (parts.isPresent()) {
				if (parts.arg2().isInteger()) {
					// period Pi
					return F.Tan(parts.arg1());
				}

				if (parts.arg2().isFraction()) {
					// period (n/m)*Pi
					IFraction f = (IFraction) parts.arg2();
					IInteger[] divRem = f.divideAndRemainder();
					IFraction rest = F.fraction(divRem[1], f.getDenominator());
					if (!divRem[0].isZero()) {
						return Tan(Plus(parts.arg1(), Times(rest, Pi)));
					}

					if (rest.equals(C1D2)) {
						// Tan(z) == Cot(Pi/2 - z)
						return Cot(Subtract(Divide(Pi, C2), arg1));
					}
				}

				if (parts.arg2().isIntegerResult()) {
					// period Pi
					return F.Tan(parts.arg1());
				}
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

	/**
	 * Hyperbolic tangent
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Hyperbolic_function">Hyperbolic function</a>
	 */
	private final static class Tanh extends AbstractTrigArg1 implements INumeric, TanhRules, DoubleUnaryOperator {

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
		public IExpr evaluateArg1(final IExpr arg1) {
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
					return F.integer(r);
				}
			}
		} catch (ArithmeticException ae) {
			// toLong() method failed
		}
		return F.NIL;
	}

	private final static ExpTrigsFunctions CONST = new ExpTrigsFunctions();

	public static ExpTrigsFunctions initialize() {
		return CONST;
	}

	private ExpTrigsFunctions() {

	}

}
