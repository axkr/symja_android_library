package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <pre>
 * JavaForm(expr)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * returns the Symja Java form of the <code>expr</code>. In Java you can use the
 * created Symja expressions.
 * </p>
 * </blockquote>
 * <h3>Examples</h3>
 * 
 * <pre>
 * &gt;&gt;&gt; JavaForm[I/2*E^((-I)*x)-I/2*E^(I*x)]
 * "Plus(Times(CC(0L,1L,1L,2L),Power(E,Times(CNI,x))),Times(CC(0L,1L,-1L,2L),Power(E,Times(CI,x))))"
 * </pre>
 * <p>
 * JavaForm evaluates its argument before creating the Java form:
 * </p>
 * 
 * <pre>
 * &gt;&gt;&gt; JavaForm(D(sin(x)*cos(x),x))
 * "Plus(Sqr(Cos(x)),Negate(Sqr(Sin(x))))"
 * </pre>
 * <p>
 * You can use <code>Hold</code> to suppress the evaluation:
 * </p>
 * 
 * <pre>
 * &gt;&gt;&gt; JavaForm(Hold(D(sin(x)*cos(x),x)))
 * "D(Times(Sin(x),Cos(x)),x)"
 * </pre>
 */
public class JavaForm extends AbstractCoreFunctionEvaluator {

	public JavaForm() {
	}

	/**
	 * <p>
	 * Return the internal Java form of this expression. The Java form is useful for
	 * generating Symja programming expressions.
	 * </p>
	 * <p>
	 * See the online Symja function reference: <a href=
	 * "https://bitbucket.org/axelclk/symja_android_library/wiki/Symbols/JavaForm">JavaForm</a>
	 * </p>
	 */
	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);

		IExpr arg1 = engine.evaluate(ast.arg1());
		boolean strictJava = false;
		if (ast.isAST2()) {
			IExpr arg2 = engine.evaluate(ast.arg2());
			final Options options = new Options(ast.topHead(), arg2, engine);
			strictJava = options.isOption("Strict");
		}
		String resultStr = javaForm(arg1, strictJava);
		return F.$str(resultStr);
	}

	public static String javaForm(IExpr arg1, boolean strictJava) {
		// necessary for MathMLContentUtilities#toJava() method
		// if (arg1.isAST()) {
		// arg1 = PatternMatcher.evalLeftHandSide((IAST) arg1);
		// }
		return arg1.internalJavaString(strictJava, 0, false);
	}

}
