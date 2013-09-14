=============================================
  Symja Library - Java Symbolic Math System
=============================================

Features:
* arbitrary precision integers, rational and complex numbers
* differentiation, integration
* polynomials
* pattern matching
* linear algebra

Online demo: 
    http://symjaweb.appspot.com/
    
Include the symja_android_library.jar in your classpath and start coding.

Example 1:
:::java
import static org.matheclipse.core.expression.F.*;
import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

public class CalculusExample {
    public static void main(String[] args) {
		try {
			EvalUtilities util = new EvalUtilities(false, true);
			// Show an expression in the Java form:
			String javaForm = util.toJavaForm("d(sin(x)*cos(x),x)");
			// prints: D(Times(Sin(x),Cos(x)),x)
			System.out.println(javaForm.toString());

			// Use the Java form to create an expression with F.* static methods:
			IAST function = D(Times(Sin(x), Cos(x)), x);
			IExpr result = util.evaluate(function);
			// print: -Sin(x)^2+Cos(x)^2
			System.out.println(result.toString());

			// evaluate the string directly
			result = util.evaluate("d(sin(x)*cos(x),x)");
			// print: -Sin(x)^2+Cos(x)^2
			System.out.println(result.toString());

			// evaluate an Integrate[] expression
			result = util.evaluate("integrate(sin(x)^5,x)");
			// print: -1/5*Cos(x)^5+2/3*Cos(x)^3-Cos(x)
			System.out.println(result.toString());
		} catch (SyntaxError e) {
			// catch Symja parser errors here
			System.out.println(e.getMessage());
		} catch (MathException me) {
			// catch Symja math errors here
			System.out.println(me.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}


The Symja library uses the Apache Commons Mathematics Library:
    http://commons.apache.org/math/
and the JAS - Java Algebra System:
    http://krum.rz.uni-mannheim.de/jas/ 
    http://code.google.com/p/java-algebra-system/
    
Symja is the underlying library for the following projects:
* SymjaDroid Android app:
    https://bitbucket.org/axelclk/symjadroid   
* Symja AJAX web interface:
    https://bitbucket.org/axelclk/symja.ajax
* Symja Java Swing GUI:
    https://bitbucket.org/axelclk/symjaswing
* Symja Unit tests:
    https://bitbucket.org/axelclk/symjaunittests
	
axelclk_AT_gmail_DOT_com 