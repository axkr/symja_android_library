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

	private final static ConstantDefinitions CONST = new ConstantDefinitions();

	public static ConstantDefinitions initialize() {
		return CONST;
	}

	private ConstantDefinitions() {

	}

	/**
	 * <blockquote>
	 * <p>
	 * Catalan's constant
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:
	 * </p>
	 * <ul>
	 * <li><a href="http://en.wikipedia.org/wiki/Catalan%27s_constant">Wikipedia - Catalan's constant</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; N(Catalan)
	 * 0.915965594177219
	 * 
	 * &gt;&gt; PolyGamma(1,1/4)
	 * 8*Catalan+Pi^2
	 * </pre>
	 */
	private static class Catalan extends AbstractSymbolEvaluator implements ISignedNumberConstant {
		final static public double CATALAN = 0.91596559417721901505460351493238411077414937428167;

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
	 * <pre>
	 * ComplexInfinity
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * represents an infinite complex quantity of undetermined direction.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; 1 / ComplexInfinity
	 * 0
	 * 
	 * &gt;&gt; ComplexInfinity + ComplexInfinity
	 * ComplexInfinity
	 * 
	 * &gt;&gt; ComplexInfinity * Infinity
	 * ComplexInfinity
	 * 
	 * &gt;&gt; FullForm(ComplexInfinity)
	 * DirectedInfinity()
	 * </pre>
	 */
	private static class ComplexInfinity extends AbstractSymbolEvaluator {

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
	 * <pre>
	 * Degree
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * the constant <code>Degree</code> converts angles from degree to <code>Pi/180</code> radians.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:
	 * </p>
	 * <ul>
	 * <li><a href="http://en.wikipedia.org/wiki/Degree_(angle)">Wikipedia - Degree (angle)</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Sin(30*Degree)
	 * 1/2
	 * </pre>
	 * <p>
	 * Degree has the value of Pi / 180
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Degree == Pi / 180
	 * True
	 * 
	 * &gt;&gt; Cos(Degree(x))
	 * Cos(Degree(x))
	 * 
	 * &gt;&gt; N(Degree)    
	 * 0.017453292519943295
	 * </pre>
	 */
	private static class Degree extends AbstractSymbolEvaluator implements ISignedNumberConstant {
		final static public double DEGREE = 0.017453292519943295769236907684886127134428718885417;

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
	 * <blockquote>
	 * <p>
	 * Euler's constant E
	 * </p>
	 * </blockquote>
	 * <p>
	 * <strong>Note</strong>: the upper case identifier <code>E</code> is different from the lower case identifier
	 * <code>e</code>.
	 * </p>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Exp(1)
	 * E
	 * 
	 * &gt;&gt; N(E)
	 * 2.718281828459045
	 * </pre>
	 */
	private static class E extends AbstractSymbolEvaluator implements ISignedNumberConstant {

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
	 * <blockquote>
	 * <p>
	 * Glaisher constant.
	 * </p>
	 * </blockquote>
	 * <p>
	 * The <code>Glaisher</code> constant is named after mathematicians James Whitbread Lee Glaisher and Hermann
	 * Kinkelin. Its approximate value is: <code>1.2824271291...</code>
	 * </p>
	 * <p>
	 * See:
	 * </p>
	 * <ul>
	 * <li><a href="http://en.wikipedia.org/wiki/Glaisher-Kinkelin_constant">Wikipedia - Glaisher-Kinkelin
	 * constant</a></li>
	 * </ul>
	 */
	private static class Glaisher extends AbstractSymbolEvaluator implements ISignedNumberConstant {
		final static public double GLAISHER = 1.2824271291006226368753425688697917277676889273250;

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
	 * <pre>
	 * GoldenRatio
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * is the golden ratio.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:
	 * </p>
	 * <ul>
	 * <li><a href="https://en.wikipedia.org/wiki/Golden_ratio">Wikipedia: Golden ratio</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; N(GoldenRatio)
	 * 1.618033988749895
	 * </pre>
	 */
	private static class GoldenRatio extends AbstractSymbolEvaluator implements ISignedNumberConstant {
		final static public double GOLDEN_RATIO = 1.6180339887498948482045868343656381177203091798058;

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
	 * <pre>
	 * I
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * Imaginary unit - internally converted to the complex number <code>0+1*i</code>. <code>I</code> represents the
	 * imaginary number <code>Sqrt(-1)</code>. <code>I^2</code> will be evaluated to <code>-1</code>.
	 * </p>
	 * </blockquote>
	 * <p>
	 * <strong>Note</strong>: the upper case identifier <code>I</code> is different from the lower case identifier
	 * <code>i</code>.
	 * </p>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; I^2
	 * -1
	 * 
	 * &gt;&gt; (3+I)*(3-I)
	 * 10
	 * </pre>
	 */
	private static class I extends AbstractSymbolEvaluator {

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
	 * <pre>
	 * Infinity
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * represents an infinite real quantity.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; 1 / Infinity
	 * 0
	 * 
	 * &gt;&gt; Infinity + 100
	 * Infinity
	 * </pre>
	 * <p>
	 * Use <code>Infinity</code> in sum and limit calculations:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Sum(1/x^2, {x, 1, Infinity})
	 * Pi ^ 2 / 6
	 * 
	 * &gt;&gt; FullForm(Infinity)
	 * DirectedInfinity(1)
	 * 
	 * &gt;&gt; (2 + 3.5*I) / Infinity
	 * 0.0"
	 * 
	 * &gt;&gt; Infinity + Infinity
	 * Infinity
	 * </pre>
	 * <p>
	 * Indeterminate expression <code>0</code> Infinity encountered.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Infinity / Infinity
	 * Indeterminate
	 * </pre>
	 */
	private static class Infinity extends AbstractSymbolEvaluator {

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
	 * <pre>
	 * Khinchin
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * Khinchin's constant
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:
	 * </p>
	 * <ul>
	 * <li><a href="http://en.wikipedia.org/wiki/Khinchin%27s_constant">Wikipedia:Khinchin's constant</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; N(Khinchin)
	 * 2.6854520010653062
	 * </pre>
	 */
	private static class Khinchin extends AbstractSymbolEvaluator implements ISignedNumberConstant {
		final static public double KHINCHIN = 2.6854520010653064453097148354817956938203822939945;

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
	 * <pre>
	 * Pi
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * is the constant <code>Pi</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; N(Pi)
	 * 3.141592653589793
	 * </pre>
	 */
	private static class Pi extends AbstractSymbolEvaluator implements ISignedNumberConstant {

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
