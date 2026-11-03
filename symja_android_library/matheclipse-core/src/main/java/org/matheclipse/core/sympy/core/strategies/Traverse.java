package org.matheclipse.core.sympy.core.strategies;

import java.util.function.Function;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.sympy.core.traversal.Traversal;
import org.matheclipse.core.sympy.core.traversal.Traversal.Fns;

/**
 * Port of <code>sympy.strategies.traverse</code>, the strategies which traverse a tree.
 *
 * <p>
 * sympy defines these through mutual recursion of <code>chain</code> and <code>sall</code>. Here
 * {@link #topDown(Function)} and {@link #bottomUp(Function)} run on the iterative
 * {@link Traversal#transform(IExpr, Function, Function, Fns)}, so the depth of a tree costs no Java
 * stack. Unlike {@link Traversal#bottomUp(IExpr, Function)}, the rule also sees the leaves.
 *
 * <p>
 * Compared with the visitors: {@link #topDown(Function)} descends into what the rule returned, while
 * {@link org.matheclipse.core.visit.VisitorReplaceAll} stops at a replacement. Heads are not
 * visited.
 */
public final class Traverse {

  private Traverse() {}

  /**
   * Apply a rule down a tree, running it on the top nodes first.
   *
   * @param rule
   * @return
   */
  public static Function<IExpr, IExpr> topDown(Function<IExpr, IExpr> rule) {
    return topDown(rule, Util.BASIC_FNS);
  }

  /**
   * Apply a rule down a tree, running it on the top nodes first.
   *
   * @param rule
   * @param fns
   * @return
   */
  public static Function<IExpr, IExpr> topDown(Function<IExpr, IExpr> rule, Fns fns) {
    return expr -> Traversal.transform(expr, rule, null, fns).orElse(expr);
  }

  /**
   * Apply a rule up a tree, running it on the bottom nodes first.
   *
   * @param rule
   * @return
   */
  public static Function<IExpr, IExpr> bottomUp(Function<IExpr, IExpr> rule) {
    return bottomUp(rule, Util.BASIC_FNS);
  }

  /**
   * Apply a rule up a tree, running it on the bottom nodes first.
   *
   * @param rule
   * @param fns
   * @return
   */
  public static Function<IExpr, IExpr> bottomUp(Function<IExpr, IExpr> rule, Fns fns) {
    return expr -> Traversal.transform(expr, null, rule, fns).orElse(expr);
  }

  /**
   * Apply a rule down a tree, and stop on success. As defined in sympy this stops only at the top
   * level: if the rule has no effect on a node, {@link #topDown(Function)} runs on all its arguments.
   *
   * @param rule
   * @return
   */
  public static Function<IExpr, IExpr> topDownOnce(Function<IExpr, IExpr> rule) {
    return topDownOnce(rule, Util.BASIC_FNS);
  }

  /**
   * Apply a rule down a tree, and stop on success.
   *
   * @param rule
   * @param fns
   * @return
   * @see #topDownOnce(Function)
   */
  @SuppressWarnings("unchecked")
  public static Function<IExpr, IExpr> topDownOnce(Function<IExpr, IExpr> rule, Fns fns) {
    return Core.doOne(rule, sall(topDown(rule, fns), fns));
  }

  /**
   * Apply a rule up a tree, and stop on success. As defined in sympy this stops only at the top
   * level: if {@link #bottomUp(Function)} changed an argument, the rule doesn't run on this node.
   *
   * @param rule
   * @return
   */
  public static Function<IExpr, IExpr> bottomUpOnce(Function<IExpr, IExpr> rule) {
    return bottomUpOnce(rule, Util.BASIC_FNS);
  }

  /**
   * Apply a rule up a tree, and stop on success.
   *
   * @param rule
   * @param fns
   * @return
   * @see #bottomUpOnce(Function)
   */
  @SuppressWarnings("unchecked")
  public static Function<IExpr, IExpr> bottomUpOnce(Function<IExpr, IExpr> rule, Fns fns) {
    return Core.doOne(sall(bottomUp(rule, fns), fns), rule);
  }

  /**
   * Strategic all: apply a rule to the arguments of a node, one level only.
   *
   * @param rule
   * @return
   */
  public static Function<IExpr, IExpr> sall(Function<IExpr, IExpr> rule) {
    return sall(rule, Util.BASIC_FNS);
  }

  /**
   * Strategic all: apply a rule to the arguments of a node, one level only.
   *
   * @param rule
   * @param fns
   * @return
   */
  public static Function<IExpr, IExpr> sall(Function<IExpr, IExpr> rule, Fns fns) {
    return expr -> Traversal.use(expr, rule, 1, fns);
  }
}
