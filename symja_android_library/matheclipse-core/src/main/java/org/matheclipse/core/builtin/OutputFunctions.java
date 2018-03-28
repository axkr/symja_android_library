
package org.matheclipse.core.builtin;

import java.io.StringWriter;

import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.MathMLUtilities;
import org.matheclipse.core.eval.TeXUtilities;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.HornerScheme;

public final class OutputFunctions {

	static {
		F.FullForm.setEvaluator(new FullForm());
		F.HoldForm.setEvaluator(new HoldForm());
		F.HornerForm.setEvaluator(new HornerForm());
		F.JavaForm.setEvaluator(new JavaForm());
		F.MathMLForm.setEvaluator(new MathMLForm());
		F.TeXForm.setEvaluator(new TeXForm());
	}

	/**
	 * <pre>
	 * FullForm(expression)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * shows the internal representation of the given <code>expression</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * <p>
	 * FullForm shows the difference in the internal expression representation:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt;&gt; FullForm(x(x+1))
	 * "x(Plus(1, x))"
	 * 
	 * &gt;&gt;&gt; FullForm(x*(x+1))
	 * "Times(x, Plus(1, x))"
	 * </pre>
	 */
	private static class FullForm extends AbstractCoreFunctionEvaluator {

		public FullForm() {
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			return F.stringx(engine.evaluate(ast.arg1()).fullFormString());
		}

		@Override
		public void setUp(ISymbol newSymbol) {
		}
	}

	/**
	 * <pre>
	 * HoldForm(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * <code>HoldForm</code> doesn't evaluate <code>expr</code> and didn't appear in the output
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; HoldForm(3*2)
	 * 3*2
	 * </pre>
	 */
	private static class HoldForm extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	/**
	 * <pre>
	 * HornerForm(polynomial)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * Generate the horner scheme for a univariate <code>polynomial</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * HornerForm(polynomial, x)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * Generate the horner scheme for a univariate <code>polynomial</code> in <code>x</code>.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:
	 * </p>
	 * <ul>
	 * <li><a href="http://en.wikipedia.org/wiki/Horner_scheme">Wikipedia - Horner scheme</a></li>
	 * <li><a href="https://rosettacode.org/wiki/">Rosetta Code - Horner's rule for polynomial evaluation</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; HornerForm(3+4*x+5*x^2+33*x^6+x^8)
	 * 3+x*(4+x*(5+(33+x^2)*x^4))
	 * 
	 * &gt;&gt; HornerForm(a+b*x+c*x^2,x)
	 * a+x*(b+c*x)
	 * </pre>
	 */
	private static class HornerForm extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			IExpr arg1 = ast.arg1();
			if (arg1.isAST()) {

				IAST poly = (IAST) arg1;
				VariablesSet eVar;
				IAST variables;
				if (ast.isAST2()) {
					variables = Validate.checkIsVariableOrVariableList(ast, 2, engine);
				} else {
					eVar = new VariablesSet(ast.arg1());
					variables = eVar.getVarList();
				}

				if (variables.size() >= 2) {
					ISymbol sym = (ISymbol) variables.arg1();
					if (poly.isASTSizeGE(F.Plus, 2)) {
						HornerScheme scheme = new HornerScheme();
						return scheme.generate(engine.isNumericMode(), poly, sym);
					}
				}

			}
			return arg1;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	/**
	 * <pre>
	 * JavaForm(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the Symja Java form of the <code>expr</code>. In Java you can use the created Symja expressions.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * <p>
	 * JavaForm can add the <code>F.</code> prefix for class <code>org.matheclipse.core.expression.F</code> if you set
	 * <code>prefix-&gt;True</code>:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; JavaForm(D(sin(x)*cos(x),x), prefix-&gt;True)
	 * "F.Plus(F.Sqr(F.Cos(F.x)),F.Negate(F.Sqr(F.Sin(F.x))))"
	 * 
	 * &gt;&gt; JavaForm(I/2*E^((-I)*x)-I/2*E^(I*x))
	 * "Plus(Times(CC(0L,1L,1L,2L),Power(E,Times(CNI,x))),Times(CC(0L,1L,-1L,2L),Power(E,Times(CI,x))))"
	 * </pre>
	 * <p>
	 * JavaForm evaluates its argument before creating the Java form:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; JavaForm(D(sin(x)*cos(x),x))
	 * "Plus(Sqr(Cos(x)),Negate(Sqr(Sin(x))))"
	 * </pre>
	 * <p>
	 * You can use <code>Hold</code> to suppress the evaluation:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; JavaForm(Hold(D(sin(x)*cos(x),x)))
	 * "D(Times(Sin(x),Cos(x)),x)"
	 * 
	 * &gt;&gt; JavaForm(Hold(D(sin(x)*cos(x),x)), prefix-&gt;True)
	 * "F.D(F.Times(F.Sin(F.x),F.Cos(F.x)),F.x)"
	 * </pre>
	 */
	private static class JavaForm extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			IExpr arg1 = engine.evaluate(ast.arg1());
			boolean strictJava = false;
			boolean usePrefix = false;
			if (ast.isAST2()) {
				IExpr arg2 = engine.evaluate(ast.arg2());
				final Options options = new Options(ast.topHead(), arg2, engine);
				strictJava = options.isOption("Strict");
				usePrefix = options.isOption("Prefix");
			}
			String resultStr = javaForm(arg1, strictJava, usePrefix);
			return F.$str(resultStr);
		}

		public static String javaForm(IExpr arg1, boolean strictJava, boolean usePrefix) {
			return arg1.internalJavaString(strictJava, 0, false, usePrefix, false);
		}

	}

	/**
	 * <pre>
	 * MathMLForm(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the MathMLForm form of the evaluated <code>expr</code>.
	 * </p>
	 * </blockquote>
	 */
	private static class MathMLForm extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			MathMLUtilities mathMLUtil = new MathMLUtilities(engine, false, engine.isRelaxedSyntax());
			IExpr arg1 = ast.arg1();
			StringWriter stw = new StringWriter();
			mathMLUtil.toMathML(arg1, stw);
			return F.$str(stw.toString());
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	/**
	 * <pre>
	 * TeXForm(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the TeX form of the evaluated <code>expr</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt;&gt; TeXForm(D(sin(x)*cos(x),x))
	 * "{\cos(x)}^{2}-{\sin(x)}^{2}"
	 * </pre>
	 */
	private static class TeXForm extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			TeXUtilities texUtil = new TeXUtilities(engine, engine.isRelaxedSyntax());
			IExpr arg1 = engine.evaluate(ast.arg1());
			StringWriter stw = new StringWriter();
			texUtil.toTeX(arg1, stw);
			return F.$str(stw.toString());
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	private final static OutputFunctions CONST = new OutputFunctions();

	public static OutputFunctions initialize() {
		return CONST;
	}

	private OutputFunctions() {

	}

}
