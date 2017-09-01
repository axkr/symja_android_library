package org.matheclipse.core.examples;

import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

public class SolveIssue176 {

	public static void main(String[] args) {
		try {
			ExprEvaluator util = new ExprEvaluator();
			util.eval("x0=20.796855124168776");
			IExpr result = util.eval("Solve(x0==(-1.0000000000000002)*Sqrt(y^2.0),y)");
			// {}
			System.out.println(result.toString());

			result = util.eval("Solve(x1==(-1.0000000000000002)*Sqrt(y^2.0),y)");
			// {{y->-x1},{y->x1}}
			System.out.println(result.toString());

			IExpr exp1 = util.eval("x2=985.3698808807793");
			IExpr exp2 = util.eval("y=89.73311105248706");
			IExpr exp3 = util.eval("z=30.358930164378133");
			IExpr exp4 = util.eval("w=143.26674070021394");
			IExpr exp5 = util.eval("q=923.282853878973");
			IExpr exp6 = util.eval("j=955.7677262340256");
			result = util.eval(" N(Solve(q/w==(m+2.0*y)/(m+z+x2+j),m))");
			// {{m->-2300.6414589715228}}
			System.out.println(result.toString());

			result = util.eval(
					"Solve(923.282853878973/143.26674070021394==(m+2.0*89.73311105248706)/(m+30.358930164378133+985.3698808807793+955.7677262340256),m)");
			// {{m->-2300.6414589715228}}
			System.out.println(result.toString());

			result = util.eval("Solve(4.027433300199394==(110.70534+x3)/(1015.3739400000001+x3),x3)");
			// {{x3->-1314.197567242396}}
			System.out.println(result.toString());
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
