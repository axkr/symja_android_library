package org.matheclipse.core.reflection.system;

import org.matheclipse.core.builtin.StructureFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.VisitorReplaceAll;

/**
 *
 *
 * <pre>
 * ExpToTrig(expr)
 * </pre>
 */
public class ExpToTrig extends AbstractEvaluator {

  public ExpToTrig() {}

  /**
   * Exponential definitions for trigonometric functions
   *
   * <p>See <a href=
   * "http://en.wikipedia.org/wiki/List_of_trigonometric_identities#Exponential_definitions"> List
   * of trigonometric identities - Exponential definitions</a>,<br>
   * <a href="http://en.wikipedia.org/wiki/Hyperbolic_function">Hyperbolic function</a>
   */
  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {

    IExpr temp = StructureFunctions.threadLogicEquationOperators(ast.arg1(), ast, 1);
    if (temp.isPresent()) {
      return temp;
    }

    IExpr arg1 = ast.arg1();
    VisitorReplaceAll visitor =
        new VisitorReplaceAll(
            x -> {
              if (x.isPower()) {
                IExpr exponent = F.NIL;
                IExpr base = x.base();
                if (base.equals(S.E)) {
                  exponent = x.exponent();
                  // return F.Plus(F.Cosh(exponent), F.Sinh(exponent));
                } else if (base.isNumber()) {
                  // base^exponent => E ^(exponent*Log(base))
                  exponent = S.Expand.of(engine, F.Times(x.exponent(), F.Log(base)));
                }
                if (exponent.isPresent()) {
                  if (exponent.isPlus()) {
                    // E ^ (a+b+...) => map on plus args
                    IASTAppendable result = F.TimesAlloc(exponent.size());
                    ((IAST) exponent)
                        .forEach(arg -> result.append(F.Plus(F.Cosh(arg), F.Sinh(arg))));
                    return result;
                  }
                  return F.Plus(F.Cosh(exponent), F.Sinh(exponent));
                }
              }
              return F.NIL;
            });

    visitor.setPostProcessing(
        x -> {
          if (x.isTimes() && x.arg1().isNumber() && x.arg2().isPlus()) {
            return F.Expand(x);
          }
          return x;
        });

    temp = arg1.accept(visitor);

    if (temp.isPresent()) {
      return temp; // F.evalExpandAll(temp, engine);
    }
    return arg1;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_1;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.LISTABLE);
  }
}
