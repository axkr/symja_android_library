package org.matheclipse.core.reflection.system;

import java.util.function.Predicate;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;

public class Activate extends AbstractFunctionOptionEvaluator {

  public Activate() {}

  @Override
  public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] option,
      final EvalEngine engine, IAST originalAST) {
    IExpr expr = ast.arg1();
    boolean heads = option[0].isTrue();

    Predicate<IExpr> matcher;
    if (argSize == 2) {
      final IPatternMatcher pm = engine.evalPatternMatcher(ast.arg2());
      matcher = s -> pm.test(s, engine);
    } else {
      matcher = s -> true;
    }

    return activate(expr, matcher, heads, engine);
  }

  private IExpr activate(IExpr expr, Predicate<IExpr> matcher, boolean heads, EvalEngine engine) {
    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      if (ast.isAST() && ast.head().isAST(S.Inactive, 2)) {
        if (!heads) {
          return expr;
        }
        IExpr arg1 = ast.head().first();
        if (matcher.test(arg1)) {
          IASTAppendable result = F.ast(arg1, ast.size());
          for (int i = 1; i < ast.size(); i++) {
            result.append(activate(ast.getRule(i), matcher, heads, engine));
          }
          return engine.evaluate(result);
        }
      }
      IASTMutable result = ast.copy();
      for (int i = 1; i < ast.size(); i++) {
        result.set(i, activate(ast.getRule(i), matcher, heads, engine));
      }
      return result;
    }
    return expr;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }

  @Override
  public void setUp(ISymbol newSymbol) {
    setOptions(newSymbol, S.Heads, S.True);
  }
}
