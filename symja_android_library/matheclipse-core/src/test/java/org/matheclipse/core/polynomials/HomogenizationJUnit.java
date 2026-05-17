package org.matheclipse.core.polynomials;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

public class HomogenizationJUnit extends ExprEvaluatorTestCase {

  public static final IBuiltInSymbol Homogenization =
      S.initFinalSymbol("Homogenization", ID.Zeta + 9);

  static {
    Homogenization.setEvaluator(new Homogenization());
  }

  private static class Homogenization extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isAST()) {
        VariablesSet eVar = new VariablesSet(arg1);
        PolynomialHomogenization substitutions = new PolynomialHomogenization(engine);
        IExpr temp = substitutions.replaceForward(arg1);
        IASTAppendable list = substitutions.listOfBackwardSubstitutions();

        // sort for canonical expressions:
        EvalAttributes.sort(list);
        return F.List(temp, list);
      }
      return arg1;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return IFunctionEvaluator.ARGS_1_1;
    }
  }


  @Test
  public void testHomogenization001() {
    EvalEngine.resetModuleCounter4JUnit();
    check("Homogenization(9*6^(2*x) - 10*6^x + 1)", //
        "{1-10*jas$1+9*jas$1^2,{jas$1->6^x}}");
  }

  @Test
  public void testHomogenization002() {
    EvalEngine.resetModuleCounter4JUnit();
    check("Homogenization( (E^x)^3 - 4*E^x + 3/(E^x))", //
        "{3/jas$1-4*jas$1+jas$1^3,{jas$1->E^x}}");

    // TODO jas$1->Sqrt(x)
    EvalEngine.resetModuleCounter4JUnit();
    check("Homogenization(x+2*Sqrt(x)+1)", //
        "{1+2*jas$1+jas$1^2,{jas$1->Sqrt(x)}}");
    EvalEngine.resetModuleCounter4JUnit();
    check("Homogenization(Sin(x))", //
        "{jas$1,{jas$1->Sin(x)}}");

    EvalEngine.resetModuleCounter4JUnit();
    check("Homogenization(x^2+Sin(x)+Sin(x)^3)", //
        "{jas$1^2+jas$2+jas$2^3,{jas$1->x,jas$2->Sin(x)}}");

    EvalEngine.resetModuleCounter4JUnit();
    check("Homogenization((1+x^2)^(-1))", //
        "{1/jas$1,{jas$1->1+x^2}}");

    EvalEngine.resetModuleCounter4JUnit();
    check("Homogenization(f(x)^(-1))", //
        "{1/jas$1,{jas$1->f(x)}}");
  }

  @Test
  public void testHomogenization003_Logarithmic() {
    // log_2(x) + 4*log_x(2) = 5 => t + 4/t = 5 where t = log_2(x)
    EvalEngine.resetModuleCounter4JUnit();
    check("Homogenization(Log(2, x) + 4*Log(x, 2))", //
        "{4/jas$1+jas$1,{jas$1->Log(x)/Log(2)}}");
  }

  @Test
  public void testHomogenization004_Hyperbolic() {
    // 3*Sech(x)^2 + 4*Tanh(x) + 1 => 3*(1-t^2) + 4*t + 1 where t = Tanh(x)
    EvalEngine.resetModuleCounter4JUnit();
    check("Homogenization(3*Sech(x)^2 + 4*Tanh(x) + 1)", //
        "{1+3*jas$1^2+4*jas$2,{jas$1->Sech(x),jas$2->Tanh(x)}}");
  }

  @Test
  public void testHomogenization005_TanMultiAngle() {
    // 3*Tan(3x) - Tan(x) + 2 => polynomial in t = Tan(x)
    EvalEngine.resetModuleCounter4JUnit();
    check("Homogenization(3*Tan(3*x) - Tan(x) + 2)", //
        "{2-jas$1+3*jas$2,{jas$1->Tan(x),jas$2->Tan(3*x)}}");
  }

  @Test
  public void testSechTransform() {
    EvalEngine engine = new EvalEngine();
    VariablesSet vars = new VariablesSet(F.x);
    PolynomialHomogenization polyHomogenization = new PolynomialHomogenization(engine);
    PolynomialHomogenization.TanhSechTransform transform =
        polyHomogenization.new TanhSechTransform();

    // Test: Sech(x)^2 -> 1 - Tanh(x)^2
    IExpr expr1 = F.Power(F.Sech(F.x), F.C2);
    IExpr expected1 = F.Plus(F.C1, F.Negate(F.Power(F.Tanh(F.x), F.C2)));
    assertEquals(expected1, transform.apply(expr1));

    // Test: Sech(x)^4 -> (1 - Tanh(x)^2)^2
    IExpr expr2 = F.Power(F.Sech(F.x), F.C4);
    IExpr expected2 = F.Power(F.Plus(F.C1, F.Negate(F.Power(F.Tanh(F.x), F.C2))), F.C2);
    assertEquals(expected2, transform.apply(expr2));
  }

  @Test
  public void testCschTransform() {
    EvalEngine engine = new EvalEngine();
    VariablesSet vars = new VariablesSet(F.x);
    PolynomialHomogenization polyHomogenization = new PolynomialHomogenization(engine);
    PolynomialHomogenization.TanhSechTransform transform =
        polyHomogenization.new TanhSechTransform();

    // Test: Csch(x)^2 -> Coth(x)^2 - 1
    IExpr expr1 = F.Power(F.Csch(F.x), F.C2);
    IExpr expected1 = F.Plus(F.Power(F.Coth(F.x), F.C2), F.CN1);
    assertEquals(expected1, transform.apply(expr1));

    // Test: Csch(x)^4 -> (Coth(x)^2 - 1)^2
    IExpr expr2 = F.Power(F.Csch(F.x), F.C4);
    IExpr expected2 = F.Power(F.Plus(F.Power(F.Coth(F.x), F.C2), F.CN1), F.C2);
    assertEquals(expected2, transform.apply(expr2));
  }

  @Test
  public void testNoTransform() {
    EvalEngine engine = new EvalEngine();
    VariablesSet vars = new VariablesSet(F.x);
    PolynomialHomogenization polyHomogenization = new PolynomialHomogenization(engine);
    PolynomialHomogenization.TanhSechTransform transform =
        polyHomogenization.new TanhSechTransform();

    // Test: Sech(x)^3 -> NIL (odd exponent, transform should not apply)
    IExpr expr1 = F.Power(F.Sech(F.x), F.C3);
    assertEquals(F.NIL, transform.apply(expr1));

    // Test: Csch(x)^1 -> NIL (odd exponent, transform should not apply)
    IExpr expr2 = F.Power(F.Csch(F.x), F.C1);
    assertEquals(F.NIL, transform.apply(expr2));

    // Test: x^2 -> NIL (not a supported hyperbolic base)
    IExpr expr3 = F.Power(F.x, F.C2);
    assertEquals(F.NIL, transform.apply(expr3));
  }

}
