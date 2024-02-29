package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Supplier;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.SourceCodeProperties;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
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
 * <p>
 * common subexpressions elimination for a complicated <code>function</code> by generating
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
 * <p>
 * Create the original expression:
 *
 * <pre>
 * &gt;&gt; ReplaceRepeated(1/6*(3+3*v1+v2+a*(4+v2)), {v1-&gt;a^2, v2-&gt;Sqrt(5+6*a+5*v1)})
 * 1/6*(3+3*a^2+Sqrt(5+6*a+5*a^2)+a*(4+Sqrt(5+6*a+5*a^2)))
 * </pre>
 */
public class OptimizeExpression extends AbstractFunctionEvaluator {

  private static class ReferenceCounter implements Comparable<ReferenceCounter> {
    final private IASTMutable expr;

    final private long leafCount;
    private int counter;

    public ReferenceCounter(IASTMutable referenceExpr) {
      this.expr = referenceExpr;
      this.leafCount = referenceExpr.leafCount();
      counter = 1;
    }

    @Override
    public int compareTo(ReferenceCounter o) {
      return leafCount < o.leafCount ? 1
          : leafCount != o.leafCount ? -1
              : (counter > o.counter ? 1 : counter != o.counter ? -1 : 0);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      ReferenceCounter other = (ReferenceCounter) obj;
      return counter == other.counter;
    }

    @Override
    public int hashCode() {
      return Objects.hash(counter);
    }


    public void incCounter() {
      ++counter;
    }

    @Override
    public String toString() {
      return "ReferenceCounter: [count: " + counter + ", expr: " + expr + "]";
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
        if (value.expr == t) {
          return F.NIL;
        }
      }
      return value.expr;
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
      IASTMutable result = visitAST(ast);
      if (result.isNIL()) {
        result = ast;
      }
      IExpr temp = fFunction.apply(result);
      if (temp.isPresent()) {
        return temp;
      }
      return result;
    }

    @Override
    protected IASTMutable visitAST(IAST ast) {
      IExpr temp;
      IASTMutable result = F.NIL;
      int i = 1;
      while (i < ast.size()) {
        IExpr arg = ast.getRule(i);
        if (arg instanceof IASTMutable) {
          temp = visit((IASTMutable) arg);
          if (temp.isPresent()) {
            // share the object with the same id:
            if (result.isNIL()) {
              result = ast.copy();
            }
            result.set(i, temp);
          }
        }
        i++;
      }
      return result;
    }
  }

  public static IAST cseArray(final IAST ast, int minReferences, int minLeafCounter) {
    ShareFunction function = new ShareFunction();
    ShareReplaceAll sra = new ShareReplaceAll(function);
    IExpr sharedExpr = ast.accept(sra);
    if (sharedExpr.isPresent()) {
      ArrayList<ReferenceCounter> list = new ArrayList<ReferenceCounter>();
      for (Map.Entry<IASTMutable, ReferenceCounter> entry : function.map.entrySet()) {
        ReferenceCounter rc = entry.getValue();
        if (rc.counter >= minReferences && rc.expr.leafCount() > minLeafCounter) {
          list.add(rc);
        }
      }

      Collections.sort(list, Collections.reverseOrder());
      IASTAppendable result = F.ListAlloc(list.size());
      for (ReferenceCounter rc : list) {
        IASTMutable ref = rc.expr;
        result.append(ref);
      }
      return result;
    }
    return F.NIL;
  }

  /**
   * Perform common subexpression elimination on an expression.Try to optimize/extract common
   * sub-{@link IASTMutable} expressions to minimize the number of operations
   *
   * @param ast the ast whose internal memory consumption should be minimized
   * @return the pair of <code>{shared-expressions, recursive-replacement-rules}</code>
   */
  public static IAST cse(final IASTMutable ast) {
    return cse(ast, () -> "v");
  }

  /**
   * Perform common subexpression elimination on an expression.Try to optimize/extract common
   * sub-{@link IASTMutable} expressions to minimize the number of operations
   *
   * @param ast the ast whose internal memory consumption should be minimized
   * @param variablePrefix the prefix string, which should be used for the variable names
   * @return the pair of <code>{shared-expressions, recursive-replacement-rules}</code>
   */
  public static IAST cse(final IASTMutable ast, Supplier<String> variablePrefix) {
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
      Collections.sort(list);
      IASTAppendable variableSubstitutions = F.ListAlloc(list.size());
      IASTAppendable replaceList = F.ListAlloc(list.size());

      for (ReferenceCounter referenceCounter : list) {
        IExpr reference = referenceCounter.expr;
        IExpr temp1 = reference.replaceAll(variableSubstitutions).orElse(reference);
        ISymbol dummyVariable = F.Dummy(variablePrefix.get() + varCounter);

        IAST subs = F.Rule(reference, dummyVariable);
        IExpr temp2 = sharedExpr.replaceRepeated(subs);
        if (temp2 != sharedExpr && temp2.isPresent()) {
          sharedExpr = temp2;
          replaceList.append(F.Rule(dummyVariable, temp1));
          variableSubstitutions.append(subs);
          varCounter++;
        }
      }

      if (replaceList.argSize() > 1) {
        // replace expressions with variables `vN` inside the substitution rules:
        IASTAppendable resultReplaceList2 = F.ListAlloc(replaceList.argSize());
        resultReplaceList2.append(replaceList.last());
        for (int i = replaceList.size() - 2; i > 0; i--) {
          final IExpr lhs = replaceList.get(i).first();
          IExpr rhs = replaceList.get(i).second();
          for (int j = replaceList.size() - 1; j > i; j--) {
            IExpr temp = rhs.replaceAll((IAST) variableSubstitutions.get(j));
            if (temp.isPresent()) {
              rhs = temp;
            }
          }
          resultReplaceList2.append(F.Rule(lhs, rhs));
        }
        return F.list(sharedExpr, resultReplaceList2);
      }
      return F.list(sharedExpr, replaceList);
    }
    return F.list(ast, F.CEmptyList);
  }

  public OptimizeExpression() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.arg1() instanceof IASTMutable) {
      return cse((IASTMutable) ast.arg1());
    }
    return F.NIL;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_1;
  }

  public static void csePairAsJava(IAST csePair, StringBuilder buf) {
    IExpr arg1 = csePair.arg1();
    IExpr arg2 = csePair.arg2();
    // replacement rules
    cseAsJavaRecursive((IAST) arg2, buf);
    buf.append("return ");
    buf.append(arg1.internalJavaString(SourceCodeProperties.JAVA_FORM_PROPERTIES_NO_SYMBOL_PREFIX,
        1, x -> null));
    buf.append(";\n");
  }

  private static void cseAsJavaRecursive(IAST cseList, StringBuilder buf) {
    for (int i = 1; i < cseList.size(); i++) {
      IExpr element = cseList.get(i);
      if (element.isList()) {
        cseAsJavaRecursive((IAST) element, buf);
      } else {
        if (element.isRuleAST()) {
          buf.append("IExpr ");
          IExpr variable = element.first();
          buf.append(variable.toString());
          buf.append(" = ");
          IExpr expr = element.second();
          buf.append(expr.internalJavaString(
              SourceCodeProperties.JAVA_FORM_PROPERTIES_NO_SYMBOL_PREFIX, 1, x -> null));
          buf.append(";\n");
        } else {
          buf.append("return ");
          buf.append(element.internalJavaString(
              SourceCodeProperties.JAVA_FORM_PROPERTIES_NO_SYMBOL_PREFIX, 1, x -> null));
          buf.append(";\n");
        }
      }
    }
  }

}
