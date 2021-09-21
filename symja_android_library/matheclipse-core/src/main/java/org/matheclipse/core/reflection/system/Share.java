package org.matheclipse.core.reflection.system;

import java.util.HashMap;
import java.util.function.Function;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.visit.VisitorExpr;

/**
 * Try to share common sub-<code>IASTs</code> expressions with the same object-id internally to
 * minimize memory consumption. Returns the number f shared sub-expressions
 */
public class Share extends AbstractFunctionEvaluator {

  private static class ShareFunction implements Function<IASTMutable, IExpr> {
    java.util.Map<IASTMutable, IASTMutable> map;

    public ShareFunction() {
      map = new HashMap<IASTMutable, IASTMutable>(128);
    }

    @Override
    public IExpr apply(IASTMutable t) {
      IExpr value = map.get(t);
      if (value == null) {
        map.put(t, t);
        return F.NIL;
      } else {
        if (value == t) {
          return F.NIL;
        }
      }
      return value;
    }
  }

  /**
   * Replace all occurrences of expressions where the given <code>function.apply()</code> method
   * returns a non <code>F.NIL</code> value. The visitors <code>visit()</code> methods return <code>
   * F.NIL</code> if no substitution occurred.
   */
  private static class ShareReplaceAll extends VisitorExpr {
    final Function<IASTMutable, IExpr> fFunction;
    public int fCounter;

    public ShareReplaceAll(Function<IASTMutable, IExpr> function) {
      super();
      this.fFunction = function;
      this.fCounter = 0;
    }

    @Override
    public IExpr visit(IASTMutable ast) {
      if (ast.size() <= 1) {
        return F.NIL;
      }
      IExpr temp = fFunction.apply(ast);
      if (temp.isPresent()) {
        fCounter++;
        return temp;
      }
      return visitAST(ast);
    }

    @Override
    protected IExpr visitAST(IAST ast) {
      IExpr temp;
      boolean evaled = false;
      int i = 1;
      while (i < ast.size()) {
        IExpr arg = ast.getRule(i);
        if (arg instanceof IASTMutable) {
          temp = visit((IASTMutable) arg);
          if (temp.isPresent()) {
            // share the object with the same id:
            ((IASTMutable) ast).set(i, temp);
            evaled = true;
          }
        }
        i++;
      }
      return evaled ? ast : F.NIL;
    }
  }

  public Share() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.arg1() instanceof IASTMutable) {
      return F.ZZ(shareAST((IASTMutable) ast.arg1()));
    }
    return F.C0;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_1;
  }

  /**
   * Try to share common sub-<code>IASTs</code> expressions with the same object-id internally to
   * minimize memory consumption and return the number of shared sub-expressions
   *
   * @param ast the ast whose internal memory consumption should be minimized
   * @return the number of shared sub-expressions
   */
  private static int shareAST(final IASTMutable ast) {
    ShareReplaceAll sra = createVisitor();
    ast.accept(sra);
    return sra.fCounter;
  }

  public static ShareReplaceAll createVisitor() {
    return new ShareReplaceAll(new ShareFunction());
  }
}
