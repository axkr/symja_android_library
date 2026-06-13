package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.PiecewiseUtil;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.util.Assumptions;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.VisitorExpr;

public final class PiecewiseExpand extends AbstractFunctionOptionEvaluator {
  private static class PiecewiseExpandVisitor extends VisitorExpr {
    private final IBuiltInSymbol domain;

    public PiecewiseExpandVisitor(IBuiltInSymbol domain) {
      super();
      this.domain = domain;
    }

    @Override
    public IExpr visit(IASTMutable ast) {
      IExpr expr = visitAST(ast).orElse(ast);
      if (expr.isAST()) {
        // Retrieve expansions via the central PiecewiseUtil ordinal switch logic
        IExpr expansion = PiecewiseUtil.piecewiseExpand((IAST) expr, domain);
        if (expansion.isPresent()) {
          return expansion;
        }
        return visitAST((IAST) expr).orElse(F.NIL);
      }
      return F.NIL;
    }
  }

  @Override
  public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] options,
      final EvalEngine engine, IAST originalAST) {
    IExpr arg1 = ast.arg1();
    if (arg1.isAST()) {
      IBuiltInSymbol domain = S.Complexes;
      IAssumptions assumptions = null;

      if (argSize == 2) {
        IExpr arg2 = ast.arg2();
        if (arg2.equals(S.Reals) || arg2.equals(S.Complexes)) {
          domain = ((IBuiltInSymbol) arg2);
        } else {
          assumptions = Assumptions.getInstance(arg2);
        }
      } else if (argSize == 3) {
        IExpr arg2 = ast.arg2();
        IExpr arg3 = ast.arg3();
        if (arg3.equals(S.Reals) || arg3.equals(S.Complexes)) {
          domain = ((IBuiltInSymbol) arg3);
        }
        assumptions = Assumptions.getInstance(arg2);
      }

      PiecewiseExpandVisitor visitor = new PiecewiseExpandVisitor(domain);
      IAssumptions oldAssumptions = engine.getAssumptions();
      try {
        if (assumptions != null) {
          engine.setAssumptions(assumptions);
        }
        return arg1.accept(visitor).evaluateOrElse(engine, arg1);
      } finally {
        engine.setAssumptions(oldAssumptions);
      }
    }
    return arg1;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_3;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    IBuiltInSymbol[] optionKeys = new IBuiltInSymbol[] {S.Assumptions, S.Method};
    IExpr[] optionValues = new IExpr[] {S.$Assumptions, S.Automatic};
    setOptions(newSymbol, optionKeys, optionValues);
  }
}