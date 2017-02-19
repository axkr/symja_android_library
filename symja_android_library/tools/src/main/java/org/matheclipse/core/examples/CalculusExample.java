package org.matheclipse.core.examples;

import static org.matheclipse.core.expression.F.*;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

public class CalculusExample {
	public static void main(String[] args) {
		try {
			// don't distinguish between lower- and uppercase identifiers
			Config.PARSER_USE_LOWERCASE_SYMBOLS = true;

			ExprEvaluator util = new ExprEvaluator(false, 100);

			// Show an expression in the Java form:
			// Note: single character identifiers are case sensistive
			// (the "D()" function input must be written as upper case
			// character)
			String javaForm = util.toJavaForm("D(sin(x)*cos(x),x)");
			// prints: D(Times(Sin(x),Cos(x)),x)
			System.out.println(javaForm.toString());

			// Use the Java form to create an expression with F.* static
			// methods:
			IAST function = D(Times(Sin(x), Cos(x)), x);
			IExpr result = util.evaluate(function);
			// print: Cos(x)^2-Sin(x)^2
			System.out.println(result.toString());

			// Note "diff" is an alias for the "D" function
			result = util.evaluate("diff(sin(x)*cos(x),x)");
			// print: Cos(x)^2-Sin(x)^2
			System.out.println(result.toString());

			// evaluate the last result ($ans contains "last answer")
			result = util.evaluate("$ans+cos(x)^2");
			// print: 2*Cos(x)^2-Sin(x)^2
			System.out.println(result.toString());

			// evaluate an Integrate[] expression
			result = util.evaluate("integrate(sin(x)^5,x)");
			// print: 2/3*Cos(x)^3-1/5*Cos(x)^5-Cos(x)
			System.out.println(result.toString());

			// set the value of a variable "a" to 10
			// Note: in server mode the variable name must have a preceding '$'
			// character
			result = util.evaluate("a=10");
			// print: 10
			System.out.println(result.toString());

			// do a calculation with variable "a"
			result = util.evaluate("a*3+b");
			// print: 30+b
			System.out.println(result.toString());

			// Do a calculation in "numeric mode" with the N() function
			// Note: single character identifiers are case sensistive
			// (the "N()" function input must be written as upper case
			// character)
			result = util.evaluate("N(sinh(5))");
			// print: 74.20321057778875
			System.out.println(result.toString());

			// define a function with a recursive factorial function definition.
			// Note: fac(0) is the stop condition.
			result = util.evaluate("fac(x_IntegerQ):=x*fac(x-1);fac(0)=1");
			// now calculate factorial of 10:
			result = util.evaluate("fac(10)");
			// print: 3628800
			System.out.println(result.toString());

		} catch (SyntaxError e) {
			// catch Symja parser errors here
			System.out.println(e.getMessage());
		} catch (MathException me) {
			// catch Symja math errors here
			System.out.println(me.getMessage());
		} catch (final Exception ex) {
			System.out.println(ex.getMessage());
		} catch (final StackOverflowError soe) {
			System.out.println(soe.getMessage());
		} catch (final OutOfMemoryError oome) {
			System.out.println(oome.getMessage());
		}
	}
}