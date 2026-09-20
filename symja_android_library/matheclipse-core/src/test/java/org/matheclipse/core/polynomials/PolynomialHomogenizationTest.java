package org.matheclipse.core.polynomials;

import org.junit.jupiter.api.Test;
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

/**
 * Tests for {@link PolynomialHomogenization}. The examples (1) - (7) are taken from <a href=
 * "https://www.research.ed.ac.uk/portal/files/413486/Solving_Symbolic_Equations_%20with_PRESS.pdf">Solving
 * Symbolic Equations with PRESS</a>
 */
public class PolynomialHomogenizationTest extends ExprEvaluatorTestCase {

  public static final IBuiltInSymbol Homogenization =
      S.initFinalSymbol("Homogenization", ID.Zeta + 9);

  static {
    Homogenization.setEvaluator(new Homogenization());
  }

  /**
   * <code>Homogenization(expr)</code>, <code>Homogenization({expr1, expr2,...})</code> or
   * <code>Homogenization(expr, False)</code> without rewriting trigonometric functions.
   */
  private static class Homogenization extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isAST()) {
        boolean trig = !(ast.isAST2() && ast.arg2().isFalse());
        PolynomialHomogenization substitutions = new PolynomialHomogenization(engine, trig);
        IExpr temp = arg1.isList() ? substitutions.replaceForwardList((IAST) arg1)
            : substitutions.replaceForward(arg1);
        IASTAppendable list = substitutions.listOfBackwardSubstitutions();

        // sort for canonical expressions:
        EvalAttributes.sort(list);
        return F.List(temp, list);
      }
      return arg1;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return IFunctionEvaluator.ARGS_1_2;
    }
  }

  private void checkHomogenization(String input, String expected) {
    EvalEngine.resetModuleCounter4JUnit();
    check(input, expected);
  }

  @Test
  public void testExponential() {
    checkHomogenization("Homogenization(9*6^(2*x) - 10*6^x + 1)", //
        "{1-10*jas$1+9*jas$1^2,{jas$1->6^x}}");
    // PRESS (7)
    checkHomogenization("Homogenization(E^(3*x) - 4*E^x + 3*E^(-x))", //
        "{3/jas$1-4*jas$1+jas$1^3,{jas$1->E^x}}");
    checkHomogenization("Homogenization( (E^x)^3 - 4*E^x + 3/(E^x))", //
        "{3/jas$1-4*jas$1+jas$1^3,{jas$1->E^x}}");
    // E^(-I*x) is a power of the kernel E^(I*x)
    checkHomogenization("Homogenization(E^(I*x) + E^(-I*x) + E^(2*I*x))", //
        "{1/jas$1+jas$1+jas$1^2,{jas$1->E^(I*x)}}");
  }

  @Test
  public void testSharedIntegerBase() {
    // 4 and 2 are powers of 2
    checkHomogenization("Homogenization(4^x - 2^x - 2)", //
        "{-2-jas$1+jas$1^2,{jas$1->2^x}}");
    checkHomogenization("Homogenization(9^x - 4*3^x + 3)", //
        "{3-4*jas$1+jas$1^2,{jas$1->3^x}}");
    // the analysis and the rewrite phase have to agree on the kernel of 4^(x/2)
    checkHomogenization("Homogenization(4^(1+x/2) + 2^x)", //
        "{5*jas$1,{jas$1->2^x}}");
    // no common base
    checkHomogenization("Homogenization(4^x - 3^x)", //
        "{-jas$1+jas$2,{jas$1->3^x,jas$2->4^x}}");
  }

  @Test
  public void testFractionalPowers() {
    checkHomogenization("Homogenization(x+2*Sqrt(x)+1)", //
        "{1+2*jas$1+jas$1^2,{jas$1->Sqrt(x)}}");
    checkHomogenization("Homogenization(Sqrt(x)+x^(1/3))", //
        "{jas$1^2+jas$1^3,{jas$1->x^(1/6)}}");
    // a sum is only a kernel, if it occurs with a fractional exponent
    checkHomogenization("Homogenization((1+x)^2+Sqrt(1+x))", //
        "{jas$1+jas$1^4,{jas$1->Sqrt(1+x)}}");
  }

  @Test
  public void testListOfExpressions() {
    // x^(1/6) has to be the kernel in both expressions
    checkHomogenization("Homogenization({Sqrt(x)+y, x^(1/3)+y})", //
        "{{jas$1^3+jas$2,jas$1^2+jas$2},{jas$1->x^(1/6),jas$2->y}}");
  }

  @Test
  public void testKernels() {
    checkHomogenization("Homogenization(Sin(x))", //
        "{jas$1,{jas$1->Sin(x)}}");
    checkHomogenization("Homogenization(x^2+Sin(x)+Sin(x)^3)", //
        "{jas$1^2+jas$2+jas$2^3,{jas$1->x,jas$2->Sin(x)}}");
    checkHomogenization("Homogenization(f(x)^(-1))", //
        "{1/jas$1,{jas$1->f(x)}}");
    // a sum with an integer exponent is walked
    checkHomogenization("Homogenization((1+x^2)^(-1))", //
        "{1/(1+jas$1^2),{jas$1->x}}");
    checkHomogenization("Homogenization((1+Sin(x))^2+Sin(x))", //
        "{1+3*jas$1+jas$1^2,{jas$1->Sin(x)}}");
  }

  @Test
  public void testLogarithmic() {
    // PRESS (4): log_2(x) + 4*log_x(2) = 5 => t + 4/t = 5 where t = log_2(x)
    checkHomogenization("Homogenization(Log(2, x) + 4*Log(x, 2))", //
        "{4/jas$1+jas$1,{jas$1->Log(x)/Log(2)}}");
  }

  @Test
  public void testCosSin() {
    checkHomogenization("Homogenization(Cos(x)^2+Sin(x))", //
        "{1+jas$1-jas$1^2,{jas$1->Sin(x)}}");
    checkHomogenization("Homogenization(Sin(x)^2+Cos(x))", //
        "{1+jas$1-jas$1^2,{jas$1->Cos(x)}}");
    checkHomogenization("Homogenization(Sin(x)^4+Cos(x))", //
        "{1+jas$1-2*jas$1^2+jas$1^4,{jas$1->Cos(x)}}");
    // Cos and Sin are independent kernels without the trig option
    checkHomogenization("Homogenization(Cos(x)^2+Sin(x), False)", //
        "{jas$1^2+jas$2,{jas$1->Cos(x),jas$2->Sin(x)}}");
  }

  @Test
  public void testHyperbolic() {
    // PRESS (5): 3*Sech(x)^2 + 4*Tanh(x) + 1 => 3*(1-t^2) + 4*t + 1 where t = Tanh(x)
    checkHomogenization("Homogenization(3*Sech(x)^2 + 4*Tanh(x) + 1)", //
        "{4+4*jas$1-3*jas$1^2,{jas$1->Tanh(x)}}");
    checkHomogenization("Homogenization(Sech(x)^4 + Tanh(x))", //
        "{1+jas$1-2*jas$1^2+jas$1^4,{jas$1->Tanh(x)}}");
    checkHomogenization("Homogenization(3*Csch(x)^2 - Coth(x) - 1)", //
        "{-4-jas$1+3*jas$1^2,{jas$1->Coth(x)}}");
    // Sech(x) on its own is a kernel
    checkHomogenization("Homogenization(4*Sech(x)^4 - 17*Sech(x)^2 + 4)", //
        "{4-17*jas$1^2+4*jas$1^4,{jas$1->Sech(x)}}");
    // odd exponent
    checkHomogenization("Homogenization(Sech(x)^3 + Tanh(x))", //
        "{jas$1^3+jas$2,{jas$1->Sech(x),jas$2->Tanh(x)}}");
    // Tanh of another argument
    checkHomogenization("Homogenization(Sech(x)^2 + Tanh(y))", //
        "{jas$1^2+jas$2,{jas$1->Sech(x),jas$2->Tanh(y)}}");
    checkHomogenization("Homogenization(3*Sech(x)^2 + 4*Tanh(x) + 1, False)", //
        "{1+3*jas$1^2+4*jas$2,{jas$1->Sech(x),jas$2->Tanh(x)}}");
  }

  @Test
  public void testTanMultipleAngle() {
    // PRESS (3): 3*Tan(3x) - Tan(x) + 2 => rational function in t = Tan(x)
    // (Together() used the dummy variables jas$1 ... jas$3 internally)
    checkHomogenization("Homogenization(3*Tan(3*x) - Tan(x) + 2)", //
        "{(2+8*jas$4-6*jas$4^2)/(1-3*jas$4^2),{jas$4->Tan(x)}}");
    // a single multiple is a kernel
    checkHomogenization("Homogenization(Tan(3*x)^2 - Tan(3*x))", //
        "{-jas$1+jas$1^2,{jas$1->Tan(3*x)}}");
    checkHomogenization("Homogenization(3*Tan(3*x) - Tan(x) + 2, False)", //
        "{2-jas$1+3*jas$2,{jas$1->Tan(x),jas$2->Tan(3*x)}}");
  }

}
