package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.VisitorExpr;

/**
 *
 *
 * <pre>
 * OptimizeExpression(function)
 * </pre>
 *
 * <blockquote>
 *
 * <p>common subexpressions elimination for a complicated <code>function</code> by generating
 * &ldquo;dummy&rdquo; variables for these subexpressions.
 *
 * </blockquote>
 *
 * <pre>
 * &gt;&gt; OptimizeExpression( Sin(x) + Cos(Sin(x)) )
 * {v1+Cos(v1),{v1-&gt;Sin(x)}}
 *
 * &gt;&gt; OptimizeExpression((3 + 3*a^2 + Sqrt(5 + 6*a + 5*a^2) + a*(4 + Sqrt(5 + 6*a + 5*a^2)))/6)
 * {1/6*(3+3*v1+v2+a*(4+v2)),{v1-&gt;a^2,v2-&gt;Sqrt(5+6*a+5*v1)}}
 * </pre>
 *
 * <p>Create the original expression:
 *
 * <pre>
 * &gt;&gt; ReplaceRepeated(1/6*(3+3*v1+v2+a*(4+v2)), {v1-&gt;a^2, v2-&gt;Sqrt(5+6*a+5*v1)})
 * 1/6*(3+3*a^2+Sqrt(5+6*a+5*a^2)+a*(4+Sqrt(5+6*a+5*a^2)))
 * </pre>
 */
public class OptimizeExpression extends AbstractFunctionEvaluator {

  private static class ReferenceCounter implements Comparable<ReferenceCounter> {
    IASTMutable reference;
    int counter;

    public ReferenceCounter(IASTMutable reference) {
      this.reference = reference;
      counter = 1;
    }

    public void incCounter() {
      ++counter;
    }

    @Override
    public int compareTo(ReferenceCounter o) {
      return counter > o.counter ? 1 : counter == o.counter ? 0 : -1;
    }
  }

  private static class ShareFunction implements Function<IASTMutable, IASTMutable> {
    java.util.Map<IASTMutable, ReferenceCounter> map;

    public ShareFunction() {
      map = new TreeMap<IASTMutable, ReferenceCounter>();
    }

    @Override
    public IASTMutable apply(IASTMutable t) {
      ReferenceCounter value = map.get(t);
      if (value == null) {
        value = new ReferenceCounter(t);
        map.put(t, value);
        return F.NIL;
      } else {
        value.incCounter();
        if (value.reference == t) {
          return F.NIL;
        }
      }
      return value.reference;
    }
  }

  /**
   * Replace all occurrences of expressions where the given <code>function.apply()</code> method
   * returns a non <code>F.NIL</code> value. The visitors <code>visit()</code> methods return <code>
   * F.NIL</code> if no substitution occurred.
   */
  private static class ShareReplaceAll extends VisitorExpr {
    final Function<IASTMutable, IASTMutable> fFunction;

    public ShareReplaceAll(Function<IASTMutable, IASTMutable> function) {
      super();
      this.fFunction = function;
    }

    @Override
    public IExpr visit(IASTMutable ast) {
      if (ast.size() <= 1) {
        return F.NIL;
      }
      IExpr temp = fFunction.apply(ast);
      if (temp.isPresent()) {
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

  public OptimizeExpression() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.arg1() instanceof IASTMutable) {
      return optimizeExpression((IASTMutable) ast.arg1());
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_1;
  }

  /**
   * Try to optimize/extract common sub-<code>IASTMutables</code> expressions to minimize the number
   * of operations
   *
   * @param ast the ast whose internal memory consumption should be minimized
   * @return the number of shared sub-expressions
   */
  private static IExpr optimizeExpression(final IASTMutable ast) {
    ShareFunction function = new ShareFunction();
    ShareReplaceAll sra = new ShareReplaceAll(function);
    IExpr sharedExpr = ast.accept(sra);
    if (sharedExpr.isPresent()) {
      ArrayList<ReferenceCounter> list = new ArrayList<ReferenceCounter>();
      for (Map.Entry<IASTMutable, ReferenceCounter> entry : function.map.entrySet()) {
        ReferenceCounter rc = entry.getValue();
        if (rc.counter > 1) {
          list.add(rc);
        }
      }

      int varCounter = 1;
      Collections.sort(list, Collections.reverseOrder());
      IASTAppendable variableSubstitutions = F.ListAlloc(list.size());
      IASTAppendable replaceList = F.ListAlloc(list.size());
      for (ReferenceCounter referenceCounter : list) {
        IExpr reference = referenceCounter.reference;
        IExpr temp = reference.replaceAll(variableSubstitutions).orElse(reference);
        ISymbol dummyVariable = F.Dummy("v" + varCounter);
        replaceList.append(F.Rule(dummyVariable, temp));
        variableSubstitutions.append(F.Rule(reference, dummyVariable));
        varCounter++;
      }
      sharedExpr = sharedExpr.replaceRepeated(variableSubstitutions);
      if (sharedExpr.isPresent()) {
        return F.List(sharedExpr, replaceList);
      }
    }
    return F.List(ast);
  }

  public static IAST cseArray(final IAST ast, int minReferences, int minLeafCounter) {
    ShareFunction function = new ShareFunction();
    ShareReplaceAll sra = new ShareReplaceAll(function);
    IExpr sharedExpr = ast.accept(sra);
    if (sharedExpr.isPresent()) {
      ArrayList<ReferenceCounter> list = new ArrayList<ReferenceCounter>();
      for (Map.Entry<IASTMutable, ReferenceCounter> entry : function.map.entrySet()) {
        ReferenceCounter rc = entry.getValue();
        if (rc.counter >= minReferences && rc.reference.leafCount() > minLeafCounter) {
          list.add(rc);
        }
      }

      Collections.sort(list, Collections.reverseOrder());
      IASTAppendable result = F.ListAlloc(list.size());
      for (ReferenceCounter rc : list) {
        IASTMutable ref = rc.reference;
        result.append(ref);
      }
      return result;
    }
    return F.NIL;
  }
}
