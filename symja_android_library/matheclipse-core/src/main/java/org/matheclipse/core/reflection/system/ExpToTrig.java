package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.CompareUtil;
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

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {

    IExpr temp = CompareUtil.threadListLogicEquationOperators(ast.arg1(), ast, 1);
    if (temp.isPresent()) {
      return temp;
    }

    IExpr arg1 = ast.arg1();

    // Flatten nested exponentials before applying TrigToExp definitions: (E^A)^B -> E^(A*B)
    IExpr flattened = arg1.replaceAll(x -> {
      if (x.isPower()) {
        IExpr base = x.base();
        if (base.isPower() && base.base().equals(S.E)) {
          return F.Power(S.E, engine.evaluate(F.Times(base.exponent(), x.exponent())));
        }
      }
      return F.NIL;
    });

    if (flattened.isPresent()) {
      arg1 = engine.evaluate(flattened);
    }

    VisitorReplaceAll visitor = new VisitorReplaceAll(x -> {
      if (x.isPower()) {
        IExpr exponent = F.NIL;
        IExpr base = x.base();
        if (base.equals(S.E)) {
          exponent = x.exponent();
        } else if (base.isNumber()) {
          // base^exponent => E ^(exponent*Log(base))
          exponent = S.Expand.of(engine, F.Times(x.exponent(), F.Log(base)));
        }
        if (exponent.isPresent()) {

          // Partition Plus exponents to isolate Imaginary components from Real components
          // E^(A + I*B) -> (Cosh(A) + Sinh(A)) * (Cos(B) + I*Sin(B))
          if (exponent.isPlus()) {
            IASTAppendable hasI = F.PlusAlloc(exponent.size());
            IASTAppendable noI = F.PlusAlloc(exponent.size());
            for (int i = 1; i < exponent.size(); i++) {
              IExpr arg = ((IAST) exponent).get(i);
              if (!arg.isFree(
                  y -> y.isComplex() || y.isComplexNumeric() || y.equals(S.I) || y.equals(F.CNI),
                  true)) {
                hasI.append(arg);
              } else {
                noI.append(arg);
              }
            }
            if (hasI.argSize() > 0 && noI.argSize() > 0) {
              IExpr complexExp = hasI.argSize() == 1 ? hasI.arg1() : hasI;
              IExpr realExp = noI.argSize() == 1 ? noI.arg1() : noI;

              IExpr complexCosh = engine.evaluate(F.Cosh(complexExp));
              if (complexCosh.isTimes())
                complexCosh = F.evalExpand(complexCosh);
              IExpr complexSinh = engine.evaluate(F.Sinh(complexExp));
              if (complexSinh.isTimes())
                complexSinh = F.evalExpand(complexSinh);
              IExpr complexPart = engine.evaluate(F.Plus(complexCosh, complexSinh));

              IExpr realCosh = engine.evaluate(F.Cosh(realExp));
              if (realCosh.isTimes())
                realCosh = F.evalExpand(realCosh);
              IExpr realSinh = engine.evaluate(F.Sinh(realExp));
              if (realSinh.isTimes())
                realSinh = F.evalExpand(realSinh);
              IExpr realPart = engine.evaluate(F.Plus(realCosh, realSinh));

              return F.Times(complexPart, realPart);
            }
          }

          IExpr cosh = engine.evaluate(F.Cosh(exponent));
          if (cosh.isTimes()) {
            cosh = F.evalExpand(cosh);
          }
          IExpr sinh = engine.evaluate(F.Sinh(exponent));
          if (sinh.isTimes()) {
            sinh = F.evalExpand(sinh);
          }
          return F.Plus(cosh, sinh);
        }
      } else if (x.isPlus()) {
        IExpr logRes = convertLogDifferences((IAST) x, engine);
        if (logRes.isPresent()) {
          return logRes;
        }
      }
      return F.NIL;
    });

    visitor.setPostProcessing(x -> {
      if (x.isTimes() && x.arg1().isNumber() && x.arg2().isPlus()) {
        return F.Expand(x);
      }
      return x;
    });

    temp = arg1.accept(visitor);

    if (temp.isPresent()) {
      // Evaluate the generated AST so elements like 1/2*I*Sin(x) and -1/2*I*Sin(x) naturally cancel
      return engine.evaluate(temp);
    }
    return arg1;
  }

  /**
   * Identifies pairs of Log expressions C*Log(A) and -C*Log(B) inside a Plus AST and maps them to
   * their inverse Hyperbolic/Trigonometric identity: C*Log(A) - C*Log(B) -> 2*C*ArcTanh((A - B) /
   * (A + B))
   */
  private static IExpr convertLogDifferences(IAST plus, EvalEngine engine) {
    IExpr[][] extracted = new IExpr[plus.argSize() + 1][];
    int logCount = 0;
    for (int i = 1; i <= plus.argSize(); i++) {
      IExpr arg = plus.get(i);
      IExpr[] logExtract = extractLog(arg);
      if (logExtract != null) {
        extracted[i] = logExtract;
        logCount++;
      }
    }

    if (logCount < 2)
      return F.NIL;

    boolean[] used = new boolean[plus.argSize() + 1];
    boolean matchedAny = false;
    IASTAppendable newPlus = F.PlusAlloc(plus.argSize());

    for (int i = 1; i <= plus.argSize(); i++) {
      if (extracted[i] == null || used[i])
        continue;
      IExpr c_i = extracted[i][0];
      IExpr a_i = extracted[i][1];

      for (int j = i + 1; j <= plus.argSize(); j++) {
        if (extracted[j] == null || used[j])
          continue;
        IExpr c_j = extracted[j][0];
        IExpr a_j = extracted[j][1];

        IExpr sum = engine.evaluate(F.Plus(c_i, c_j));
        if (sum.isZero()) {
          IExpr num = S.Expand.of(engine, F.Subtract(a_i, a_j));
          IExpr den = S.Expand.of(engine, F.Plus(a_i, a_j));
          IExpr ratio = engine.evaluate(F.Divide(num, den));

          // Log(A) - Log(B) = 2 * ArcTanh((A - B) / (A + B))
          IExpr newTerm = engine.evaluate(F.Times(F.C2, c_i, F.ArcTanh(ratio)));

          newPlus.append(newTerm);
          used[i] = true;
          used[j] = true;
          matchedAny = true;
          break;
        }
      }
    }

    if (!matchedAny)
      return F.NIL;

    for (int i = 1; i <= plus.argSize(); i++) {
      if (!used[i]) {
        newPlus.append(plus.get(i));
      }
    }

    return engine.evaluate(newPlus.oneIdentity0());
  }

  /**
   * Extracts the coefficient and argument of a Log term. C*Log(A) -> {C, A}
   */
  private static IExpr[] extractLog(IExpr expr) {
    if (expr.isAST1() && expr.head().equals(S.Log)) {
      return new IExpr[] {F.C1, expr.first()};
    }
    if (expr.isTimes()) {
      IAST times = (IAST) expr;
      int logIndex = -1;
      int logCount = 0;
      for (int i = 1; i <= times.argSize(); i++) {
        if (times.get(i).isAST1() && times.get(i).head().equals(S.Log)) {
          logIndex = i;
          logCount++;
        }
      }
      if (logCount == 1) {
        IExpr c = times.removeAtCopy(logIndex).oneIdentity1();
        return new IExpr[] {c, times.get(logIndex).first()};
      }
    }
    return null;
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