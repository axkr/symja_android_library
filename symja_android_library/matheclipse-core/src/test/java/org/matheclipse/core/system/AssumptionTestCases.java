package org.matheclipse.core.system;

import static org.matheclipse.core.expression.F.Abs;
import static org.matheclipse.core.expression.F.Floor;
import static org.matheclipse.core.expression.F.x;
import static org.matheclipse.core.expression.F.y;
import junit.framework.TestCase;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.eval.util.AbstractAssumptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Tests for the Java port of the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi - rule-based integrator</a>.
 * 
 */
public class AssumptionTestCases extends TestCase {
	/**
	 * Assumption which implements <code>x > 0</code> or <code>y is integer number</code>
	 *
	 */
	public class XGreaterZeroOrYInteger extends AbstractAssumptions {

		@Override
		public boolean isNegative(IExpr expr) {
			return false;
		}

		@Override
		public boolean isPositive(IExpr expr) {
			if (expr.equals(x)) {
				return true;
			}
			return false;
		}
		
		@Override
		public boolean isNonNegative(IExpr expr) {
			if (expr.equals(x)) {
				return true;
			}
			return false;
		}

		@Override
		public boolean isInteger(IExpr expr) {
			if (expr.equals(y)) {
				return true;
			}
			return false;
		}

	}

	public AssumptionTestCases(String name) {
		super(name);
	}

	public void testGreaterZeroOrInteger001() {
		// don't distinguish between lower- and uppercase identifiers
		Config.PARSER_USE_LOWERCASE_SYMBOLS = true;

		EvalUtilities util = new EvalUtilities(false, true);
		util.getEvalEngine().setAssumptions(new XGreaterZeroOrYInteger());
		IAST function = Abs(x);
		IExpr result = util.evaluate(function);
		assertEquals(result.toString(), "x");

		function = Abs(y);
		result = util.evaluate(function);
		assertEquals(result.toString(), "Abs(y)");
		
		function = Floor(x);
		result = util.evaluate(function);
		assertEquals(result.toString(), "Floor(x)");
		
		function = Floor(y);
		result = util.evaluate(function);
		assertEquals(result.toString(), "y");
	}
}
