package org.matheclipse.io.others;

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
import org.matheclipse.core.polynomials.PolynomialHomogenization;
import org.matheclipse.io.system.AbstractTestCase;

public class HomogenizationJUnit extends AbstractTestCase {
  public HomogenizationJUnit() {
    super("HomogenizationTest");
  }

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
        PolynomialHomogenization substitutions =
            new PolynomialHomogenization(eVar.getVarList(), engine);
        IExpr temp = substitutions.replaceForward(arg1);
        IASTAppendable list = substitutions.listOfBackwardSubstitutions();

        // sort for canonical expressions:
        EvalAttributes.sort(list);
        return F.List(temp, list);
      }
      return arg1;
    }

    public int[] expectedArgSize(IAST ast) {
      return IFunctionEvaluator.ARGS_1_1;
    }
  }

  public void testHomogenization() {
    EvalEngine engine = EvalEngine.get();
    engine.resetModuleCounter4JUnit();
    // TODO jas$1->Sqrt(x)
    check(
        "Homogenization(x+2*Sqrt(x)+1)", //
        "{1+2*jas$1+jas$1^2,{jas$1->Sqrt(x)}}");
    engine.resetModuleCounter4JUnit();
    check(
        "Homogenization(Sin(x))", //
        "{jas$1,{jas$1->Sin(x)}}");

    engine.resetModuleCounter4JUnit();
    check(
        "Homogenization(x^2+Sin(x)+Sin(x)^3)", //
        "{jas$1^2+jas$2+jas$2^3,{jas$1->x,jas$2->Sin(x)}}");

    engine.resetModuleCounter4JUnit();
    check(
        "Homogenization((1+x^2)^(-1))", //
        "{1/jas$1,{jas$1->1+x^2}}");

    engine.resetModuleCounter4JUnit();
    check(
        "Homogenization(f(x)^(-1))", //
        "{1/jas$1,{jas$1->f(x)}}");
  }
}
