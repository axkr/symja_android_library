package org.matheclipse.io.system;

import static org.matheclipse.core.expression.F.Abs;
import static org.matheclipse.core.expression.F.Floor;
import static org.matheclipse.core.expression.F.x;
import static org.matheclipse.core.expression.F.y;

import java.util.HashMap;

import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.eval.util.AbstractAssumptions;
import org.matheclipse.core.eval.util.Assumptions;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.FEConfig;

import junit.framework.TestCase;

public class AssumptionTestCase extends TestCase {
  @Override
  protected void tearDown() throws Exception {
    // System.out.println(EvalEngine.STATISTICS.toString());
    super.tearDown();
  }

  /** Assumption which implements <code>x > 0</code> or <code>y is integer number</code> */
  public class XGreaterZeroOrYInteger extends AbstractAssumptions {

    public IAssumptions copy() {
      XGreaterZeroOrYInteger assumptions = new XGreaterZeroOrYInteger();
      return assumptions;
    }

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

    @Override
    public int[] reduceRange(IExpr x, int[] range) {
      return range;
    }

    @Override
    public IExpr get$Assumptions() {
      return F.NIL;
    }

    @Override
    public void set$Assumptions(IExpr expr) {

    }
  }

  public AssumptionTestCase(String name) {
    super(name);
  }

  public void testGreaterZeroOrInteger001() {
    // don't distinguish between lower- and uppercase identifiers
    FEConfig.PARSER_USE_LOWERCASE_SYMBOLS = true;

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

  public void testSqrt001() {
    // don't distinguish between lower- and uppercase identifiers
    FEConfig.PARSER_USE_LOWERCASE_SYMBOLS = true;

    EvalUtilities util = new EvalUtilities(false, true);

    // define "t" with "t" assumed greater than 0
    // use #1 (Slot1) as placeholder for a new symbol!
    ISymbol t = F.symbol("t", F.Greater(F.Slot1, F.C10));

    // (t^2) ^ (1/2)
    IAST function = F.Sqrt(F.Sqr(t));
    IExpr result = util.evaluate(function);
    assertEquals(result.toString(), "t");
  }

  public void testFloor001() {
    // don't distinguish between lower- and uppercase identifiers
    FEConfig.PARSER_USE_LOWERCASE_SYMBOLS = true;

    EvalUtilities util = new EvalUtilities(false, true);

    // define "t" with "t" assumed to be an element of the integers
    // use #1 (Slot1) as placeholder for a new symbol!
    ISymbol t = F.symbol("t", F.Element(F.Slot1, F.Integers));

    IAST function = F.Floor(t);
    IExpr result = util.evaluate(function);
    assertEquals(result.toString(), "t");
  }
}
