package org.matheclipse.core.examples;

import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

/**
 * Used as an example for the Java usage in the github wiki. See:
 * <a href="https://github.com/axkr/symja_android_library/wiki/Java-usage">/wiki/Java-usage</a>
 *
 */
public class Example {
	public static void main(String[] args) {
		try {
			ExprEvaluator util = new ExprEvaluator(false, 100);

			// Convert an expression to the internal Java form:
			// Note: single character identifiers are case sensitive
			// (the "D()" function identifier must be written as upper case
			// character)
			String javaForm = util.toJavaForm("D(sin(x)*cos(x),x)");
			// prints: D(Times(Sin(x),Cos(x)),x)
			System.out.println("Out[1]: " + javaForm.toString());

			// Use the Java form to create an expression with F.* static
			// methods:
			ISymbol x = F.Dummy("x");
			IAST function = F.D(F.Times(F.Sin(x), F.Cos(x)), x);
			IExpr result = util.eval(function);
			// print: Cos(x)^2-Sin(x)^2
			System.out.println("Out[2]: " + result.toString());

			// Note "diff" is an alias for the "D" function
			result = util.eval("diff(sin(x)*cos(x),x)");
			// print: Cos(x)^2-Sin(x)^2
			System.out.println("Out[3]: " + result.toString());

			// evaluate the last result (% or $ans contains "last answer")
			result = util.eval("%+cos(x)^2");
			// print: 2*Cos(x)^2-Sin(x)^2
			System.out.println("Out[4]: " + result.toString());

			// evaluate an Integrate[] expression
			result = util.eval("integrate(sin(x)^5,x)");
			// print: 2/3*Cos(x)^3-1/5*Cos(x)^5-Cos(x)
			System.out.println("Out[5]: " + result.toString());

			// set the value of a variable "a" to 10
			// Note: in server mode the variable name must have a preceding '$'
			// character
			result = util.eval("a=10");
			// print: 10
			System.out.println("Out[6]: " + result.toString());

			// do a calculation with variable "a"
			result = util.eval("a*3+b");
			// print: 30+b
			System.out.println("Out[7]: " + result.toString());

			// Do a calculation in "numeric mode" with the N() function
			// Note: single character identifiers are case sensistive
			// (the "N()" function identifier must be written as upper case
			// character)
			result = util.eval("N(sinh(5))");
			// print: 74.20321057778875
			System.out.println("Out[8]: " + result.toString());

			// define a function with a recursive factorial function definition.
			// Note: fac(0) is the stop condition.
			result = util.eval("fac(x_Integer):=x*fac(x-1);fac(0)=1");
			// now calculate factorial of 10:
			result = util.eval("fac(10)");
			// print: 3628800
			System.out.println("Out[9]: " + result.toString());

			function = F.Function(F.Divide(F.Gamma(F.Plus(F.C1, F.Slot1)), F.Gamma(F.Plus(F.C1, F.Slot2))));
			// eval function ( Gamma(1+#1)/Gamma(1+#2) ) & [23,20]
			result = util.evalFunction(function, "23", "20");
			System.out.println("Out[10]: " + result.toString());
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