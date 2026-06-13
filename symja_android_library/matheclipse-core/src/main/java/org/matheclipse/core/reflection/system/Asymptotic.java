package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>
 * Asymptotic(expr, x -&gt; x0)
 * </pre>
 *
 * <blockquote>
 * <p>
 * gives an asymptotic approximation for expr near x0. Computes the leading term.
 * </p>
 * </blockquote> *
 * 
 * <pre>
 * Asymptotic(expr, {x, x0, n})
 * </pre>
 *
 * <blockquote>
 * <p>
 * gives an asymptotic approximation for expr near x0 to order n.
 * </p>
 * </blockquote>
 */
public class Asymptotic extends AbstractFunctionOptionEvaluator {

  public Asymptotic() {}

  @Override
  public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
      IAST originalAST) {
    IExpr expr = ast.arg1();
    IExpr spec = ast.arg2();

    IExpr xVar = F.NIL;
    IExpr x0 = F.NIL;
    int order = 3;
    boolean isLeadingTerm = false;

    // 1. Parse expansion point specification & determine if "Leading Term" is requested
    if (spec.isRule()) {
      xVar = spec.first();
      x0 = spec.second();
      isLeadingTerm = true;
    } else if (spec.isList()) {
      IAST list = (IAST) spec;
      if (list.argSize() >= 2) {
        xVar = list.arg1();
        x0 = list.arg2();
        if (list.argSize() >= 3 && list.arg3().isInteger()) {
          order = list.arg3().toIntDefault();
        } else {
          isLeadingTerm = true;
        }
      }
    } else if (spec.isSymbol()) {
      xVar = spec;
      x0 = S.Infinity;
      isLeadingTerm = true;
    }

    if (xVar.isNIL() || x0.isNIL()) {
      return F.NIL;
    }

    // 2. Parse SeriesTermGoal from options (index 7 based on setUp mapping).
    if (options != null && options.length > 7) {
      IExpr termGoal = options[7];
      if (termGoal.isInteger()) {
        order = termGoal.toIntDefault();
        isLeadingTerm = false;
      }
    }

    // 3. Thread over structural containers (List, Rule, RuleDelayed, Equal)
    // This intercepts evaluated outputs from Solve/DSolve and maps recursively,
    // avoiding erroneous calculus derivatives on structural heads.
    if (expr.isList()) {
      IAST list = (IAST) expr;
      IASTAppendable result = F.ListAlloc(list.argSize());
      for (int i = 1; i <= list.argSize(); i++) {
        result.append(engine.evaluate(ast.setAtCopy(1, list.get(i))));
      }
      return result;
    } else if (expr.isRule() || expr.isRuleDelayed()) {
      // Map Asymptotic strictly over the right-hand side of the rule
      return F.binaryAST2(expr.head(), expr.first(),
          engine.evaluate(ast.setAtCopy(1, expr.second())));
    } else if (expr.isAST(S.Equal)) {
      // Map Asymptotic over both sides of an equation
      return F.Equal(engine.evaluate(ast.setAtCopy(1, expr.first())),
          engine.evaluate(ast.setAtCopy(1, expr.second())));
    }

    // 4. Delegate to domain-specific asymptotic solvers via Ordinal Switch
    // Handles unevaluated equations (e.g., failed quintic algebraic curves)
    if (expr.isAST()) {
      IAST exprAST = (IAST) expr;
      IExpr head = exprAST.head();

      if (head.isBuiltInSymbol()) {
        switch (((IBuiltInSymbol) head).ordinal()) {
          case ID.DSolveValue:
            return engine.evaluate(F.AsymptoticDSolveValue(exprAST.arg1(), exprAST.arg2(), spec));
          case ID.RSolveValue:
            return engine.evaluate(F.AsymptoticRSolveValue(exprAST.arg1(), exprAST.arg2(), spec));
          case ID.Solve:
            return engine.evaluate(F.AsymptoticSolve(exprAST.arg1(), exprAST.arg2(), spec));
          case ID.Integrate:
            return engine.evaluate(F.AsymptoticIntegrate(exprAST.arg1(), exprAST.arg2(), spec));
        }
      }

      // 5. Structural Asymptotic Expansion using generalized BellY polynomials
      if (exprAST.argSize() == 1 && exprAST.arg1().has(xVar)) {
        IExpr fHead = exprAST.head();
        IExpr gExpr = exprAST.arg1();

        IExpr zDummy = F.Dummy("z");
        IExpr gMapped;
        if (x0.isInfinity() || x0.isNegativeInfinity()) {
          gMapped = engine.evaluate(F.subst(gExpr, xVar, F.Power(zDummy, F.CN1)));
        } else {
          gMapped = engine.evaluate(F.subst(gExpr, xVar, F.Plus(x0, zDummy)));
        }

        IExpr gX0 = engine.evaluate(F.Limit(gMapped, F.Rule(zDummy, F.C0)));
        IExpr f0 = engine.evaluate(F.unaryAST1(fHead, gX0));

        if (isLeadingTerm && !f0.isZero()) {
          return f0;
        }

        IASTAppendable seriesPlus = F.PlusAlloc();
        seriesPlus.append(f0);

        IExpr z = (x0.isInfinity() || x0.isNegativeInfinity()) ? F.Power(xVar, F.CN1)
            : F.Subtract(xVar, x0);

        boolean successfulFaaDiBruno = true;
        int maxOrder = isLeadingTerm ? Math.max(order, 6) : order;

        for (int n = 1; n <= maxOrder; n++) {
          IASTAppendable termSum = F.PlusAlloc();

          for (int k = 1; k <= n; k++) {
            IASTAppendable mMatrix = F.ListAlloc(n - k + 1);
            for (int j = 1; j <= n - k + 1; j++) {
              IExpr gDeriv = engine.evaluate(F.D(gMapped, F.List(zDummy, F.ZZ(j))));
              IExpr gDerivZ0 = engine.evaluate(F.Limit(gDeriv, F.Rule(zDummy, F.C0)));
              mMatrix.append(gDerivZ0);
            }

            IExpr bellTerm = engine.evaluate(F.ternaryAST3(S.BellY, F.ZZ(n), F.ZZ(k), mMatrix));

            IExpr fDeriv = engine.evaluate(F.D(F.unaryAST1(fHead, xVar), F.List(xVar, F.ZZ(k))));
            IExpr fDerivX0 = engine.evaluate(F.subst(fDeriv, xVar, gX0));

            termSum.append(F.Times(fDerivX0, bellTerm));
          }

          IExpr coeff = engine.evaluate(termSum);

          if (coeff.isAST(S.Limit) || coeff.isAST(S.Derivative)) {
            successfulFaaDiBruno = false;
            break;
          }

          IExpr factor = engine.evaluate(F.Divide(F.Power(z, F.ZZ(n)), F.Factorial(F.ZZ(n))));
          IExpr evalTerm = engine.evaluate(F.Times(coeff, factor));
          seriesPlus.append(evalTerm);

          if (isLeadingTerm && !evalTerm.isZero()) {
            return engine.evaluate(seriesPlus);
          }
        }

        if (successfulFaaDiBruno) {
          return engine.evaluate(seriesPlus);
        }
      }
    }

    // 6. Base Case Fallback: Standard Series Expansion
    int searchOrder = isLeadingTerm ? Math.max(order, 6) : order;
    IAST seriesSpec = F.List(xVar, x0, F.ZZ(searchOrder));
    IExpr seriesRes = engine.evaluate(F.Series(expr, seriesSpec));

    if (seriesRes.isPresent() && !seriesRes.isAST(S.Series)) {
      return engine.evaluate(seriesRes.normal(false));
    }

    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_3;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    super.setUp(newSymbol);
    setOptions(newSymbol,
        new IBuiltInSymbol[] {S.AccuracyGoal, S.Assumptions, S.GenerateConditions,
            S.GeneratedParameters, S.Method, S.PerformanceGoal, S.PrecisionGoal, S.SeriesTermGoal,
            S.WorkingPrecision},
        new IExpr[] {S.Automatic, S.$Assumptions, S.Automatic, S.None, S.Automatic,
            S.$PerformanceGoal, S.Automatic, S.Automatic, S.Automatic});
  }
}
