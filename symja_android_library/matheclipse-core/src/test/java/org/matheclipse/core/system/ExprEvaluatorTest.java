package org.matheclipse.core.system;

import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.D;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.x;

import java.io.StringWriter;
import java.math.BigInteger;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.ExprRingFactory;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.polynomials.ExprMonomial;
import org.matheclipse.core.polynomials.ExprPolynomial;
import org.matheclipse.core.polynomials.ExprPolynomialRing;
import org.matheclipse.core.polynomials.ExprTermOrderByName;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

import junit.framework.TestCase;

public class ExprEvaluatorTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		// wait for initializing of Integrate() rules:
		F.join();
	}

	public void testStringEval001() {
		EvalEngine engine = new EvalEngine(true);
		ExprEvaluator eval = new ExprEvaluator(engine, true, 20);

		String str = "sin(x)";
		IExpr e = eval.eval(str);
		int i = 100;
		eval.defineVariable("x", (double) i);
		double result = e.evalDouble();
		assertEquals(-0.5063656411097588, result, 0E-10);
	}

	public void testStringEval002() {
		try {
			// don't distinguish between lower and upper case identifiers
			// Config.PARSER_USE_LOWERCASE_SYMBOLS = true;

			ExprEvaluator util = new ExprEvaluator(false, 100);

			// Convert an expression to the internal Java form:
			// Note: single character identifiers are case sensitive
			// (the "D()" function input must be written as upper case
			// character)
			String javaForm = util.toJavaForm("D(sin(x)*cos(x),x)");
			assertEquals("D(Times(Sin(x),Cos(x)),x)", javaForm.toString());

			// Use the Java form to create an expression with F.* static
			// methods:
			IAST function = D(Times(Sin(x), Cos(x)), x);
			IExpr result = util.eval(function);
			assertEquals("Cos(x)^2-Sin(x)^2", result.toString());

			// Note "diff" is an alias for the "D" function
			result = util.eval("diff(sin(x)*cos(x),x)");
			assertEquals("Cos(x)^2-Sin(x)^2", result.toString());

			// evaluate the last result ($ans contains "last answer")
			result = util.eval("$ans+cos(x)^2");
			assertEquals("2*Cos(x)^2-Sin(x)^2", result.toString());

			// evaluate an Integrate[] expression
			result = util.eval("integrate(sin(x)^5,x)");
			assertEquals("-Cos(x)+2/3*Cos(x)^3-Cos(x)^5/5", result.toString());

			// set the value of a variable "a" to 10
			// Note: in server mode the variable name must have a preceding '$'
			// character
			result = util.eval("a=10");
			assertEquals("10", result.toString());

			// do a calculation with variable "a"
			result = util.eval("a*3+b");
			assertEquals("30+b", result.toString());

			// Do a calculation in "numeric mode" with the N() function
			// Note: single character identifiers are case sensistive
			// (the "N()" function input must be written as upper case
			// character)
			result = util.eval("N(sinh(5))");
			assertEquals("74.20321057778875", result.toString());

			// define a function with a recursive factorial function definition.
			// Note: fac(0) is the stop condition.
			result = util.eval("fac(x_IntegerQ):=x*fac(x-1);fac(0)=1");
			// now calculate factorial of 10:
			result = util.eval("fac(10)");
			assertEquals("3628800", result.toString());

		} catch (SyntaxError e) {
			// catch Symja parser errors here
			assertTrue(false);
		} catch (MathException me) {
			// catch Symja math errors here
			assertTrue(false);
		} catch (final Exception ex) {
			assertTrue(false);
		} catch (final StackOverflowError soe) {
			assertTrue(false);
		} catch (final OutOfMemoryError oome) {
			assertTrue(false);
		}
	}

	/**
	 * Test the <a href=
	 * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/FactorInteger.md">FactorInteger</a>
	 * function.
	 */
	public void testFactorInteger() {
		try {
			ExprEvaluator util = new ExprEvaluator(false, 100);

			IAST function = F.FactorInteger(F.ZZ(new BigInteger("44343535354351600000003434353")));
			IExpr result = util.eval(function);
			assertEquals("{{149,1},{329569479697,1},{903019357561501,1}}", result.toString());

		} catch (SyntaxError e) {
			// catch Symja parser errors here
			assertTrue(false);
		} catch (MathException me) {
			// catch Symja math errors here
			assertTrue(false);
		} catch (final Exception ex) {
			assertTrue(false);
		} catch (final StackOverflowError soe) {
			assertTrue(false);
		} catch (final OutOfMemoryError oome) {
			assertTrue(false);
		}
	}

	public void testStringEval003() {
		try {
			ExprEvaluator util = new ExprEvaluator();
			IExpr expr = util.eval("x^2+y+a*x+b*y+c");
			assertEquals("c+a*x+x^2+y+b*y", expr.toString());

			final IAST variables = F.List(F.symbol("x"), F.symbol("y"));
			ExprPolynomialRing ring = new ExprPolynomialRing(ExprRingFactory.CONST, variables, variables.argSize(),
					ExprTermOrderByName.Lexicographic, false);

			ExprPolynomial poly = ring.create(expr);
			assertEquals("x^2 + a x + ( 1+b ) y + c ", poly.toString());

			// x degree
			assertEquals(2, poly.degree(0));
			// y degree
			assertEquals(1, poly.degree(1));

			// show internal structure:
			assertEquals("{{2,0}->1,{1,0}->a,{0,1}->1+b,{0,0}->c}", poly.coefficientRules().toString());

			System.out.println(poly.coefficientRules());
			for (ExprMonomial monomial : poly) {
				System.out.println(monomial.toString());
			}
		} catch (SyntaxError e) {
			// catch Symja parser errors here
			System.out.println(e.getMessage());
		} catch (MathException me) {
			// catch Symja math errors here
			System.out.println(me.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		} catch (final StackOverflowError soe) {
			System.out.println(soe.getMessage());
		} catch (final OutOfMemoryError oome) {
			System.out.println(oome.getMessage());
		}
	}

	public void testStringEval004() {
		try {
			ExprEvaluator util = new ExprEvaluator();
			util.defineVariable("x", 1.0);
			util.defineVariable("y", 1.0);
			IExpr expr = util.eval("If(x*x+y*y==0,1,Sin(x*x+y*y)/(x*x+y*y))");
			assertEquals("0.45464871341284085", expr.toString());

		} catch (SyntaxError e) {
			// catch Symja parser errors here
			System.out.println(e.getMessage());
		} catch (MathException me) {
			// catch Symja math errors here
			System.out.println(me.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		} catch (final StackOverflowError soe) {
			System.out.println(soe.getMessage());
		} catch (final OutOfMemoryError oome) {
			System.out.println(oome.getMessage());
		}
	}

	/**
	 * See: https://github.com/axkr/symja_android_library/issues/48 why the toString() method output of numeric values
	 * is different from OutputFormFactory#convert() method.
	 */
	public void testStringEval005() {
		try {
			ExprEvaluator util = new ExprEvaluator();
			IExpr expr = util.eval("1.2 * 1.5");
			assertEquals("1.7999999999999998", expr.toString());

			StringWriter buf = new StringWriter();
			OutputFormFactory.get(true).convert(buf, expr);
			assertEquals("1.7999999999999998", buf.toString());

			expr = util.eval("10.0^-15");
			assertEquals("1.0E-15", expr.toString());

			buf = new StringWriter();
			OutputFormFactory.get(true).convert(buf, expr);
			assertEquals("0.0", buf.toString());

		} catch (SyntaxError e) {
			// catch Symja parser errors here
			System.out.println(e.getMessage());
		} catch (MathException me) {
			// catch Symja math errors here
			System.out.println(me.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		} catch (final StackOverflowError soe) {
			System.out.println(soe.getMessage());
		} catch (final OutOfMemoryError oome) {
			System.out.println(oome.getMessage());
		}
	}

	public void testX2() {
		ExprEvaluator evaluator = new ExprEvaluator();
		evaluator.defineVariable("X", evaluator.parse("2"));
		IExpr evaluate = evaluator.evaluate("2+X");
		assertEquals(evaluate.toString(), "4");
	}
}
