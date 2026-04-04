package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.DifferenceRootExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class DifferenceRoot extends AbstractFunctionEvaluator {

  public DifferenceRoot() {}

  @Override
  public IExpr evaluate(final IAST ast, final EvalEngine engine) {
    IExpr f = ast.arg1();
    if (f.isFunction()) {
      IAST function = (IAST) f;

      // 1. Standardize variables to \[FormalY] and \[FormalN] to prevent global leakage
      if (function.argSize() == 2 && function.arg1().isList()) {
        IAST vars = (IAST) function.arg1();
        if (vars.argSize() == 2 && vars.arg1().isSymbol() && vars.arg2().isSymbol()) {
          ISymbol y = (ISymbol) vars.arg1();
          ISymbol n = (ISymbol) vars.arg2();

          ISymbol formalY = F.y;
          ISymbol formalN = F.n;

          if (!y.equals(formalY) || !n.equals(formalN)) {
            IExpr newBody = F.subst(function.arg2(), x -> {
              if (x.equals(y))
                return formalY;
              if (x.equals(n))
                return formalN;
              return F.NIL;
            });
            IAST newFunction = F.Function(F.List(formalY, formalN), newBody);

            // Re-evaluate with the protected formal variables
            return engine.evaluate(F.DifferenceRoot(newFunction));
          }
        }
      }

      // 2. Intercept and transform inhomogeneous recurrences before instantiation
      IExpr transformed = transformInhomogeneous(function, engine);
      if (transformed.isPresent()) {
        return transformed;
      }
      return new DifferenceRootExpr(function);
    }
    return F.NIL;
  }

  private static IExpr transformInhomogeneous(IAST function, EvalEngine engine) {
    if (function.argSize() != 2 || !function.arg1().isList() || !function.arg2().isList()) {
      return F.NIL;
    }
    IAST vars = (IAST) function.arg1();
    if (vars.argSize() != 2 || !vars.arg1().isSymbol() || !vars.arg2().isSymbol()) {
      return F.NIL;
    }

    ISymbol y = (ISymbol) vars.arg1();
    ISymbol n = (ISymbol) vars.arg2();
    IAST list = (IAST) function.arg2();

    IExpr recurrence = null;
    IASTAppendable initialConditions = F.ListAlloc();
    int maxInitialIndex = Integer.MIN_VALUE;

    for (int i = 1; i < list.size(); i++) {
      IExpr eq = list.get(i);
      if (eq.isAST(S.Equal, 3)) {
        IExpr lhs = eq.first();
        if (lhs.isAST(y, 2) && lhs.first().isInteger()) {
          initialConditions.append(eq);
          int idx = lhs.first().toIntDefault();
          if (idx > maxInitialIndex) {
            maxInitialIndex = idx;
          }
        } else {
          recurrence = eq;
        }
      }
    }

    if (recurrence != null) {
      ISymbol dY = F.Dummy("y");
      ISymbol dN = F.Dummy("n");

      IExpr safeRecurrence = F.subst(recurrence, x -> {
        if (x.equals(y))
          return dY;
        if (x.equals(n))
          return dN;
        return F.NIL;
      });

      IExpr expr =
          engine.evaluate(F.Expand(F.Subtract(safeRecurrence.first(), safeRecurrence.second())));
      IExpr inhomo = engine.evaluate(F.ReplaceAll(expr, F.Rule(F.unaryAST1(dY, F.$b()), F.C0)));

      if (!inhomo.isZero()) {
        IExpr homo = engine.evaluate(F.Subtract(expr, inhomo));
        IExpr iN = engine.evaluate(F.Negate(inhomo));
        IExpr iNPlus1 = engine.evaluate(F.subst(iN, dN, F.Plus(dN, F.C1)));

        IExpr ratio = engine.evaluate(F.Simplify(F.Divide(iNPlus1, iN)));
        IExpr num = ratio.asNumerDenom().first();
        IExpr den = ratio.asNumerDenom().second();

        if (!num.isPolynomial(dN) || !den.isPolynomial(dN)) {
          return F.NIL;
        }

        IExpr homoPlus1 = engine.evaluate(F.subst(homo, dN, F.Plus(dN, F.C1)));
        IExpr newHomo =
            engine.evaluate(F.Expand(F.Subtract(F.Times(den, homoPlus1), F.Times(num, homo))));

        if (maxInitialIndex != Integer.MIN_VALUE) {
          int nextIndex = maxInitialIndex + 1;
          DifferenceRootExpr dre = new DifferenceRootExpr(function);
          IAST diffRootCall = F.unaryAST1(dre, F.ZZ(nextIndex));
          IExpr nextVal = engine.evaluate(diffRootCall);

          if (nextVal.isPresent() && !nextVal.isAST(S.DifferenceRoot)) {
            initialConditions.append(F.Equal(F.unaryAST1(y, F.ZZ(nextIndex)), nextVal));

            IASTAppendable newList = F.ListAlloc();
            IExpr collectedHomo = engine.evaluate(F.Collect(newHomo, F.unaryAST1(dY, F.$b())));

            // Revert safe dummies back to the original function variables
            IExpr finalHomo = F.subst(collectedHomo, x -> {
              if (x.equals(dY))
                return y;
              if (x.equals(dN))
                return n;
              return F.NIL;
            });

            newList.append(F.Equal(finalHomo, F.C0));
            newList.appendArgs(initialConditions);

            IAST newFunction = F.Function(F.List(y, n), newList);
            return F.DifferenceRoot(newFunction);
          }
        }
      }
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    super.setUp(newSymbol);
    newSymbol.setAttributes(ISymbol.NHOLDALL);
  }
}