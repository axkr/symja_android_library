package org.matheclipse.core.examples;

import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

/**
 * See <a href="http://stackoverflow.com/questions/39753012">Stackoverflow 39753012</a>
 */
public class SolveIssue171 {

	public static void main(String[] args) {
		try {
			ExprEvaluator util = new ExprEvaluator();
//			IExpr exp1 = util.evaluate("x=985.3698808807793");
		    IExpr exp2 = util.eval("y=89.73311105248706");
		    IExpr exp3 = util.eval("z=30.358930164378133");
		    IExpr exp4 = util.eval("w=143.26674070021394");
		    IExpr exp5 = util.eval("q=923.282853878973");
		    IExpr exp6 = util.eval("j=955.7677262340256");
//		    IExpr exp = util.evaluate(" N(Solve({q/w==(m+2.0*y)/(m+z+x+j)},m))");
		    IExpr exp = util.eval("Solve(4.027433300199394==(110.70534+x)/(1015.3739400000001+x),x)");
		    // -1314.2
		    System.out.println(exp.toString()); 
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
}
