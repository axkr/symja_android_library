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

```
public class DifferentiationExample {
	public static void main(String[] args) {
		try {
			// create an evaluation utility
			EvalUtilities util = new EvalUtilities(false, true);
			// evaluate an expression
			IExpr result = util.evaluate("d(sin(x)*cos(x),x)");
			// print: -Sin(x)^2+Cos(x)^2
			System.out.println(result.toString());
		} catch (SyntaxError e) {
			// catch parser errors here
			System.out.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
...


Example 2:
```
import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;

public class IntegrateExample {
	public static void main(String[] args) {
		try {
			// create an evaluation utility
			EvalUtilities util = new EvalUtilities(false, true);
			// evaluate an expression
			IExpr result = util.evaluate("integrate(sin(x)^5,x)");
			// print: -1/5*Cos(x)^5+2/3*Cos(x)^3-Cos(x)
			System.out.println(result.toString());
		} catch (SyntaxError e) {
			// catch parser errors here
			System.out.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
...


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