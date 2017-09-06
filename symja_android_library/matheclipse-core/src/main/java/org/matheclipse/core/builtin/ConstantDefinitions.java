package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Times;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractSymbolEvaluator;
import org.matheclipse.core.eval.interfaces.ISignedNumberConstant;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class ConstantDefinitions {

	static {
		F.Catalan.setEvaluator(new Catalan());
		F.ComplexInfinity.setEvaluator(new ComplexInfinity());
		F.Degree.setEvaluator(new Degree());
		F.E.setEvaluator(new E());
		F.EulerGamma.setEvaluator(new EulerGamma());
		F.Glaisher.setEvaluator(new Glaisher());
		F.GoldenRatio.setEvaluator(new GoldenRatio());
		F.I.setEvaluator(new I());
		F.Infinity.setEvaluator(new Infinity());
		F.Khinchin.setEvaluator(new Khinchin());
		F.Pi.setEvaluator(new Pi());
	}

	final static ConstantDefinitions CONST = new ConstantDefinitions();

	public static ConstantDefinitions initialize() {
		return CONST;
	}

	private ConstantDefinitions() {

	}

	/**
	 * Catalan constant
	 * 
	 * See: <a href="http://en.wikipedia.org/wiki/Catalan%27s_constant">Wikipedia: Catalan 's constant</a>
	 * 
	 */
	private static class Catalan extends AbstractSymbolEvaluator implements ISignedNumberConstant {
		final static public double CATALAN = 0.91596559417721901505460351493238411077414937428167;

		public Catalan() {
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.CONSTANT);
		}

		@Override
		public IExpr numericEval(final ISymbol symbol) {
			return F.num(CATALAN);
		}

		@Override
		public double evalReal() {
			return CATALAN;
		}

	}

	/**
	 * 
	 */
	private static class ComplexInfinity extends AbstractSymbolEvaluator {
		public ComplexInfinity() {
		}

		@Override
		public IExpr evaluate(final ISymbol symbol) {
			return F.CComplexInfinity;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			// don't set CONSTANT attribute !
		}
	}

	/**
	 * The constant Degree converts to Pi/180 radians
	 * 
	 * See: <a href="http://en.wikipedia.org/wiki/Degree_(angle)">Degree (angle)</a>
	 * 
	 */
	private static class Degree extends AbstractSymbolEvaluator implements ISignedNumberConstant {
		final static public double DEGREE = 0.017453292519943295769236907684886127134428718885417;

		public Degree() {
		}

		/**
		 * Constant Degree converted to Pi/180
		 */
		@Override
		public IExpr evaluate(final ISymbol symbol) {
			return Times(F.Pi, Power(F.integer(180), F.CN1));
		}

		@Override
		public IExpr numericEval(final ISymbol symbol) {
			return F.num(DEGREE);
		}

		@Override
		public double evalReal() {
			return DEGREE;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.CONSTANT);
		}
	}

	/**
	 * Euler's constant E
	 * 
	 * See: <a href="http://en.wikipedia.org/wiki/Exponential_function">Wikipedia - Exponential function</a>
	 */
	private static class E extends AbstractSymbolEvaluator implements ISignedNumberConstant {
		public E() {
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.CONSTANT);
		}

		@Override
		public IExpr numericEval(final ISymbol symbol) {
			return F.num(Math.E);
		}

		@Override
		public IExpr apfloatEval(ISymbol symbol, EvalEngine engine) {
			return F.num(ApfloatMath.exp(new Apfloat(1, engine.getNumericPrecision())));
		}

		@Override
		public double evalReal() {
			return Math.E;
		}

	}

	/**
	 * Euler gamma constant
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Euler–Mascheroni_constant">Euler– Mascheroni constant</a>
	 */
	private static class EulerGamma extends AbstractSymbolEvaluator implements ISignedNumberConstant {
		final static public double EULER_GAMMA = 0.57721566490153286060651209008240243104215933593992;

		public EulerGamma() {
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.CONSTANT);
		}

		@Override
		public IExpr numericEval(final ISymbol symbol) {
			return F.num(EULER_GAMMA);
		}

		@Override
		public double evalReal() {
			return EULER_GAMMA;
		}

	}

	/**
	 * Glaisher constant
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Glaisher-Kinkelin_constant"> Wikipedia: Glaisher-Kinkelin constant</a>
	 * 
	 */
	private static class Glaisher extends AbstractSymbolEvaluator implements ISignedNumberConstant {
		final static public double GLAISHER = 1.2824271291006226368753425688697917277676889273250;

		public Glaisher() {
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.CONSTANT);
		}

		@Override
		public IExpr numericEval(final ISymbol symbol) {
			return F.num(GLAISHER);
		}

		@Override
		public double evalReal() {
			return GLAISHER;
		}

	}

	/**
	 * Golden ratio
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Golden_ratio">Wikipedia:Golden ratio</a>
	 */
	private static class GoldenRatio extends AbstractSymbolEvaluator implements ISignedNumberConstant {
		final static public double GOLDEN_RATIO = 1.6180339887498948482045868343656381177203091798058;

		public GoldenRatio() {
		}

		@Override
		public IExpr evaluate(final ISymbol symbol) {
			// 1/2*(1+5^(1/2))
			// return F.Times(F.C1D2, F.Plus(F.C1, F.Power(F.integer(5), F.C1D2)));
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.CONSTANT);
		}

		@Override
		public IExpr numericEval(final ISymbol symbol) {
			return F.num(GOLDEN_RATIO);
		}

		@Override
		public double evalReal() {
			return GOLDEN_RATIO;
		}

	}

	/**
	 * Constant <i>I</i> converted to the complex unit ( 0+1*I )
	 *
	 */
	private static class I extends AbstractSymbolEvaluator {
		public I() {
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.CONSTANT);
		}

		@Override
		public IExpr numericEval(final ISymbol symbol) {
			return F.complexNum(0.0, 1.0);
		}

		@Override
		public IExpr evaluate(final ISymbol symbol) {
			return F.complex(F.C0, F.C1);
		}
	}

	/**
	 * See <a href="http://en.wikipedia.org/wiki/Infinity">Infinity</a>
	 */
	private static class Infinity extends AbstractSymbolEvaluator {
		public Infinity() {
		}

		@Override
		public IExpr evaluate(final ISymbol symbol) {
			return F.CInfinity;// unaryAST1(F.DirectedInfinity, F.C1);
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			// don't set CONSTANT attribute !
		}
	}

	/**
	 * Khinchin constant
	 * 
	 * See: <a href="http://en.wikipedia.org/wiki/Khinchin%27s_constant">Wikipedia: Khinchin's constant</a>
	 * 
	 */
	private static class Khinchin extends AbstractSymbolEvaluator implements ISignedNumberConstant {
		final static public double KHINCHIN = 2.6854520010653064453097148354817956938203822939945;

		public Khinchin() {
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.CONSTANT);
		}

		@Override
		public IExpr numericEval(final ISymbol symbol) {
			return F.num(KHINCHIN);
		}

		@Override
		public double evalReal() {
			return KHINCHIN;
		}

	}

	/**
	 * Constant Pi
	 * 
	 */
	private static class Pi extends AbstractSymbolEvaluator implements ISignedNumberConstant {
		public Pi() {
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.CONSTANT);
		}

		@Override
		public IExpr numericEval(final ISymbol symbol) {
			return F.num(Math.PI);
		}

		@Override
		public IExpr apfloatEval(ISymbol symbol, EvalEngine engine) {
			return F.num(ApfloatMath.pi(engine.getNumericPrecision()));
		}

		@Override
		public double evalReal() {
			return Math.PI;
		}

	}
}
