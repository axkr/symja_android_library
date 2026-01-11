package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
import org.matheclipse.core.expression.S;
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

  private static class ReferenceCounter<T extends IExpr>
      implements Comparable<ReferenceCounter<T>> {
    /**
     * The expression being referenced.
     */
    final private T expr;

    /**
     * The number of indivisible subexpressions (atoms/leaves) of the expression.
     */
    final private long leafCount;

    /**
     * The number of times the expression is referenced.
     */
    private int counter;

    public ReferenceCounter(T referenceExpr) {
      this.expr = referenceExpr;
      this.leafCount = referenceExpr.leafCount();
      this.counter = 1;
    }

    @Override
    public int compareTo(ReferenceCounter<T> o) {
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
      ReferenceCounter<T> other = (ReferenceCounter<T>) obj;
      return counter == other.counter && expr.equals(other.expr);
    }

    public T getExpr() {
      return expr;
    }

    @Override
    public int hashCode() {
      return Objects.hash(counter);
    }

    public void incCounter() {
      ++counter;
    }

    public boolean isGreaterOne() {
      return counter > 1;
    }

    @Override
    public String toString() {
      return "ReferenceCounter: [count: " + counter + ", expr: " + expr + "]";
    }
  }

  private static class ShareFunction implements Function<IASTMutable, IASTMutable> {
    java.util.Map<IASTMutable, ReferenceCounter<IASTMutable>> map;

    public ShareFunction() {
      map = new TreeMap<IASTMutable, ReferenceCounter<IASTMutable>>();
    }

    @Override
    public IASTMutable apply(IASTMutable t) {
      ReferenceCounter<IASTMutable> value = map.get(t);
      if (value == null) {
        value = new ReferenceCounter<IASTMutable>(t);
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

  private static void collectSubExpressions(IExpr expr,
      Map<IExpr, ReferenceCounter<IExpr>> counts) {
    if (expr.isAtom()) {
      return;
    }
    if (expr.isAST()) {
      ReferenceCounter<IExpr> rf = counts.get(expr);
      if (rf != null) {
        rf.incCounter();
      } else {
        counts.put(expr, new ReferenceCounter<IExpr>(expr));
      }
      ((IAST) expr).forEach(child -> collectSubExpressions(child, counts));
    }
  }

  /**
   * Perform common subexpression elimination on an expression.Try to optimize/extract common
   * sub-{@link IASTMutable} expressions to minimize the number of operations
   *
   * @param ast the ast whose internal memory consumption should be minimized
   * @return the pair of <code>{shared-expressions, recursive-replacement-rules}</code>
   */
  public static IAST cse(final IAST ast) {
    IAST[] cse = cse(ast, S.Rule, () -> "v");
    return F.list(cse[1], cse[0]);
  }

  /**
   * Analyzes an expression and performs Common Sub-expression Elimination (CSE).
   *
   * @param astExpr the input expression to optimize.
   * @param assignmentsHead the head which wraps the (variable, sub-expression) pair. Typically
   *        heads are {@link S#Set} or {@link S#Rule}
   * @return an array of 2 IAST values: [0] - the list of assignments like Set(v1, ...), [1] - the
   *         optimized expression.
   */
  public static IAST[] cse(IAST astExpr, ISymbol assignmentsHead, Supplier<String> variablePrefix) {

    // Traverse the expression and count occurrences of each sub-expression.
    Map<IExpr, ReferenceCounter<IExpr>> subExprCounts = new HashMap<>();
    collectSubExpressions(astExpr, subExprCounts);

    // Filter for candidates for optimization.
    List<ReferenceCounter<IExpr>> candidates = new ArrayList<>();
    for (Map.Entry<IExpr, ReferenceCounter<IExpr>> entry : subExprCounts.entrySet()) {
      if (entry.getValue().isGreaterOne() && entry.getKey().isAST()) {
        candidates.add(entry.getValue());
      }
    }

    if (candidates.isEmpty()) {
      return new IAST[] {F.CEmptyList, astExpr};
    }

    // Sort candidates by leafCount in descending order.
    // This ensures that we replace larger expressions like Sin(a+b) before
    // smaller, nested ones like a+b.
    Collections.sort(candidates, //
        (a, b) -> compareLeafCountDescending(a, b));

    IAST modifiedExpr = astExpr;
    IASTAppendable assignments = F.ListAlloc(candidates.size());
    int varCounter = 1;

    // Replace sub-expressions in the main expression.
    for (ReferenceCounter<IExpr> rc : candidates) {
      IExpr candidate = rc.getExpr();
      ISymbol tempVar = F.Dummy(variablePrefix.get() + varCounter);
      IAST temp = (IAST) modifiedExpr.replaceAll(e -> e.equals(candidate) ? tempVar : F.NIL);
      if (temp.isPresent()) {
        varCounter++;
        modifiedExpr = temp;
        assignments.append(F.Set(tempVar, candidate));
      }
    }

    // Substitute common parts within the assignments themselves.
    // For example, if we have {v1=Sin(a+b), v2=a+b}, this pass
    // will transform the first assignment into v1=Sin(v2).
    for (int i = 1; i < assignments.size(); i++) {
      IExpr currentAssignment = assignments.get(i);
      // The right-hand side of the Set(lhs, rhs) expression
      IExpr rhs = currentAssignment.second();

      // Check all other assignments to see if their RHS can be substituted into the current RHS.
      for (int j = 1; j < assignments.size(); j++) {
        if (i == j)
          continue;

        IExpr otherAssignment = assignments.get(j);
        ISymbol otherVar = (ISymbol) otherAssignment.first();
        IExpr otherRHS = otherAssignment.second();

        // Replace occurrences of `otherRHS` with `otherVar` in our `rhs`.
        rhs = F.subst(rhs, e -> e.equals(otherRHS) ? otherVar : F.NIL);
      }
      // Update the assignment in the list with its newly optimized RHS.
      assignments.set(i, F.binaryAST2(assignmentsHead, currentAssignment.first(), rhs));
    }

    // Reverse the list of assignments. For Module evaluation, variables for smaller
    // sub-expressions (like a+b) must be defined before they are used in larger ones (like
    // Sin(v..)).
    if (!assignments.isEmpty()) {
      assignments = assignments.reverse(F.ListAlloc(assignments.argSize()));
    } else {
      // No effective optimization was performed.
      return new IAST[] {F.CEmptyList, astExpr};
    }

    return new IAST[] {assignments, modifiedExpr};
  }

  /**
   * Compare two ReferenceCounter objects by their expression leaf count in descending order.
   * 
   * @param a
   * @param b
   * @return
   */
  private static int compareLeafCountDescending(ReferenceCounter<IExpr> a,
      ReferenceCounter<IExpr> b) {
    IExpr ax = a.getExpr();
    IExpr bx = b.getExpr();
    // descending by leaf count:
    int compare = Long.compare(bx.leafCount(), ax.leafCount());
    if (compare == 0) {
      return bx.compareTo(ax);
    }
    return compare;
  }

  /**
   * Generate Java code for a CSE list recursively into the {@link StringBuilder}.
   * 
   * @param cseList
   * @param buf
   */
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

  /**
   * Generate Java code for a CSE pair.
   * 
   * @param csePair
   * @param buf
   */
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

  public OptimizeExpression() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.arg1() instanceof IAST) {
      // IAST[] cse = cse((IAST) ast.arg1(), S.Set, () -> "v");
      // return F.Hold(F.Module(cse[0], cse[1]));
      return cse((IASTMutable) ast.arg1());
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_1;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

}
