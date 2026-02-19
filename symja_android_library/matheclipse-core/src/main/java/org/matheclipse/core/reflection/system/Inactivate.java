package org.matheclipse.core.reflection.system;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;

public class Inactivate extends AbstractFunctionOptionEvaluator {
  private static final Set<IBuiltInSymbol> EXCLUDED_HEADS = new HashSet<IBuiltInSymbol>();

  static {
    EXCLUDED_HEADS.add(S.List);
    EXCLUDED_HEADS.add(S.Rule);
    EXCLUDED_HEADS.add(S.RuleDelayed);
    EXCLUDED_HEADS.add(S.Set);
    EXCLUDED_HEADS.add(S.SetDelayed);
    EXCLUDED_HEADS.add(S.Blank);
    EXCLUDED_HEADS.add(S.BlankSequence);
    EXCLUDED_HEADS.add(S.BlankNullSequence);
    EXCLUDED_HEADS.add(S.Pattern);
    EXCLUDED_HEADS.add(S.Optional);
    EXCLUDED_HEADS.add(S.Slot);
    EXCLUDED_HEADS.add(S.SlotSequence);
    EXCLUDED_HEADS.add(S.Condition);
    EXCLUDED_HEADS.add(S.PatternTest);
    EXCLUDED_HEADS.add(S.Alternatives);
  }

  public Inactivate() {}

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

    return inactivate(expr, matcher, heads);
  }

  private IExpr inactivate(IExpr expr, Predicate<IExpr> matcher, boolean heads) {
    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      IExpr head = ast.head();
      IExpr newHead = head;
      if (head.isSymbol()) {
        ISymbol sym = (ISymbol) head;
        if (EXCLUDED_HEADS.contains(sym)) {
          newHead = sym;
        } else if (matcher.test(sym)) {
          newHead = F.unaryAST1(S.Inactive, sym);
        }
      }

      IASTAppendable result = F.ast(newHead, ast.size());
      for (IExpr arg : ast) {
        result.append(inactivate(arg, matcher, heads));
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
    newSymbol.setAttributes(ISymbol.HOLDFIRST);
    setOptions(newSymbol, S.Heads, S.True);
  }
}
