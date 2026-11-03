package org.matheclipse.core.sympy.core.traversal;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;
import org.matheclipse.core.expression.ASTAssociation;
import org.matheclipse.core.expression.ASTRealMatrix;
import org.matheclipse.core.expression.ASTRealVector;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IDataExpr;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import com.google.common.collect.AbstractIterator;

/**
 * Port of the sympy module <code>sympy.core.traversal</code>: walks over an expression tree which
 * are independent of what is done with the visited nodes.
 *
 * <h3>Compared with the Symja visitors</h3>
 *
 * <table border="1">
 * <caption>The walks of this package and the nearest code in Symja</caption>
 * <tr>
 * <th>This package</th>
 * <th>Order</th>
 * <th>Nearest Symja code</th>
 * <th>What is different</th>
 * </tr>
 * <tr>
 * <td>{@link #iterArgs(IExpr)}</td>
 * <td>breadth first</td>
 * <td>none</td>
 * <td>Symja has no breadth first walk. {@link org.matheclipse.core.visit.VisitorReplaceAllBFS} is,
 * despite its name, a pre-order walk which applies its function twice to a replaced node.</td>
 * </tr>
 * <tr>
 * <td>{@link #iterFreeArgs(IExpr)}</td>
 * <td>breadth first</td>
 * <td>none</td>
 * <td><code>has()</code> and <code>isFree()</code> ignore binders. Only
 * {@link org.matheclipse.core.visit.ModuleReplaceAll} and the capture-avoiding substitution know
 * the scoping constructs, and only to rewrite.</td>
 * </tr>
 * <tr>
 * <td>{@link PreorderTraversal}</td>
 * <td>pre-order</td>
 * <td><code>IExpr.has(Predicate, boolean)</code>,
 * {@link org.matheclipse.core.visit.VisitorPredicate}</td>
 * <td>A pull iterator with <code>skip()</code> instead of a callback; no recursion.</td>
 * </tr>
 * <tr>
 * <td>{@link PostorderTraversal}</td>
 * <td>post-order</td>
 * <td>{@link org.matheclipse.core.visit.VisitorLevelSpecification} over all levels</td>
 * <td>A pull iterator; no level state, and no recursion.</td>
 * </tr>
 * <tr>
 * <td>{@link #bottomUp(IExpr, Function)}</td>
 * <td>post-order rewrite</td>
 * <td>{@link org.matheclipse.core.visit.VisitorReplaceAllDFS}</td>
 * <td>Both show a node to the rule once, after its arguments. Heads and atoms are not visited here
 * by default, and a <code>Sequence</code> isn't spliced into the parent.</td>
 * </tr>
 * <tr>
 * <td>{@link #use(IExpr, Function, int)}</td>
 * <td>one exact level</td>
 * <td><code>Map(f, expr, {level})</code>,
 * {@link org.matheclipse.core.visit.VisitorLevelSpecification}</td>
 * <td>Levels are counted from the root, there are no negative levels, and nothing is
 * evaluated.</td>
 * </tr>
 * <tr>
 * <td>{@link #walk(IExpr, Predicate)}</td>
 * <td>pre-order, gated</td>
 * <td>{@link org.matheclipse.core.visit.VisitorPredicateReplaceAll}</td>
 * <td>The same gate, but it selects nodes instead of rewriting the ones it stops at.</td>
 * </tr>
 * </table>
 *
 * <h3>Conventions shared with the visitors</h3>
 * <ul>
 * <li>"No change" is {@link F#NIL} and not, as in sympy, the same object. To make the two agree,
 * these walks accept both from a rule: a rule that answers its own argument doesn't copy any
 * ancestor. In the visitors, such a rule makes every ancestor look changed.
 * <li>Changed nodes are copied once and lazily, as in
 * {@link org.matheclipse.core.visit.VisitorExpr}.
 * <li>A rebuilt node is <b>not</b> evaluated, unlike sympy's <code>rv.func(*args)</code>. The rule
 * or the caller evaluates; use a {@link Fns} with an evaluating {@link Fns#rebuild(IASTMutable)} if
 * every level has to be canonical.
 * <li>Hold attributes are ignored, which is what all visitors do except
 * <code>SimplifyUtil</code>. The walks are structural and don't know about evaluation.
 * <li>The arguments of an association are its values, as in <code>has()</code> and
 * {@link org.matheclipse.core.visit.VisitorReplaceAll}. Keys are not visited.
 * </ul>
 *
 * <h3>Deep recursion</h3>
 *
 * <p>
 * All visitors, <code>has()</code>, <code>depth()</code> and <code>leafCount()</code> recurse on
 * the Java stack with no check of their own, except
 * {@link org.matheclipse.core.visit.VisitorLevelSpecification}, which tests
 * <code>$RecursionLimit</code>. The walks of this package use the explicit {@link FrameStack}
 * instead, so they survive any depth the heap can hold.
 *
 * <p>
 * That removes only the walk from the stack, and it has a price: a plain recursion like
 * <code>has()</code> is faster per node, while the replacing visitors are slower than
 * {@link #bottomUp(IExpr, Function)}. A rule still recurses if it evaluates, prints or
 * compares: <code>evaluate()</code> stops at
 * <code>$RecursionLimit</code>, <code>toString()</code> and <code>compareTo()</code> run out of stack
 * as early as the visitors, <code>hashCode()</code> and <code>equals()</code> later, and
 * <code>AST1.equals()</code> raises <code>$RecursionLimit</code> on a long chain of unary functions.
 * So the use of these walks is a structural pass which doesn't call any of them.
 */
public final class Traversal {

  /**
   * How a generic tree walk sees an expression, the Java form of the dictionaries
   * <code>basic_fns</code> and <code>expr_fns</code> in <code>sympy.strategies.util</code>.
   */
  public interface Fns {
    /**
     * Test if the walk stops at this expression. This is <code>true</code> for atoms and for
     * functions without arguments.
     *
     * @param expr
     * @return
     */
    default boolean isLeaf(IExpr expr) {
      return !expr.isASTOrAssociation() || expr.size() <= 1;
    }

    /**
     * Called for a node with changed arguments.
     *
     * @param ast a copy of the node, which holds the new arguments
     * @return the new node
     */
    default IExpr rebuild(IASTMutable ast) {
      return ast;
    }
  }

  /** Rebuild nodes without evaluating them. */
  public static final Fns BASIC_FNS = new Fns() {};

  private Traversal() {}

  /**
   * Yield the nodes of an expression in a breadth first traversal, without the heads. See
   * {@link #iterArgs(IExpr, boolean)}.
   *
   * @param expr
   * @return a lazy iterable, which can be iterated more than once
   */
  public static Iterable<IExpr> iterArgs(IExpr expr) {
    return iterArgs(expr, false);
  }

  /**
   * Yield the nodes of an expression in a breadth first traversal, the port of
   * <code>iterargs</code>. All nodes of one level are returned before the next level, which finds
   * the shallowest occurrence of a subexpression first.
   *
   * <p>
   * Symja has no other breadth first walk. In exchange for that order the queue holds a whole level
   * of the tree, where {@link PreorderTraversal} needs only one frame per level. Prefer the latter
   * if the order doesn't matter.
   *
   * @param expr
   * @param heads if <code>true</code>, heads are visited too
   * @return a lazy iterable, which can be iterated more than once
   */
  public static Iterable<IExpr> iterArgs(IExpr expr, boolean heads) {
    final int start = heads ? 0 : 1;
    return () -> new Iterator<IExpr>() {
      private final ArrayDeque<IExpr> queue = queueOf(expr);

      @Override
      public boolean hasNext() {
        return !queue.isEmpty();
      }

      @Override
      public IExpr next() {
        final IExpr node = queue.poll();
        if (node == null) {
          throw new NoSuchElementException();
        }
        addArgs(queue, node, start);
        return node;
      }
    };
  }

  /**
   * Yield the nodes of an expression in a breadth first traversal, leaving out what depends on a
   * bound variable. The port of <code>iterfreeargs</code>.
   *
   * <p>
   * sympy finds the variables through <code>bound_symbols</code>. Here the binders are
   * <code>Integrate, NIntegrate, Sum, NSum, Product, NProduct, Table, Do, Function, Module, With,
   * Block</code>. As in sympy, a variable can be a non-symbol like <code>f(x)</code> in
   * <code>Integrate(f(x),{f(x),1})</code>, and only the outermost binder on a path is looked at: the
   * variables of a binder nested in it are returned.
   *
   * <p>
   * <code>has()</code> and <code>isFree()</code> don't know binders. Each node inside of a binder is
   * tested with <code>has()</code>, which makes this quadratic in the depth of the binder's body.
   *
   * @param expr
   * @return a lazy iterable, which can be iterated more than once
   */
  public static Iterable<IExpr> iterFreeArgs(IExpr expr) {
    return () -> new AbstractIterator<IExpr>() {
      private final ArrayDeque<IExpr> queue = queueOf(expr);
      /** The rest of the current binder. It runs before the queue, as sympy's nested generator. */
      private final ArrayDeque<IExpr> bound = new ArrayDeque<IExpr>();
      private List<IExpr> variables = List.of();

      @Override
      protected IExpr computeNext() {
        while (!bound.isEmpty()) {
          final IExpr node = bound.poll();
          if (!isVariable(node)) {
            addArgs(bound, node, 1);
            if (isFreeOfVariables(node)) {
              return node;
            }
          }
        }
        final IExpr node = queue.poll();
        if (node == null) {
          return endOfData();
        }
        variables = node.isAST() ? boundVariables((IAST) node) : List.of();
        addArgs(variables.isEmpty() ? queue : bound, node, 1);
        return node;
      }

      /** A variable is replaced by a dummy in sympy, so it has no arguments to look at. */
      private boolean isVariable(IExpr node) {
        for (IExpr variable : variables) {
          if (variable.equals(node)) {
            return true;
          }
        }
        return false;
      }

      private boolean isFreeOfVariables(IExpr node) {
        for (IExpr variable : variables) {
          if (node.has(variable::equals, true)) {
            return false;
          }
        }
        return true;
      }
    };
  }

  private static ArrayDeque<IExpr> queueOf(IExpr expr) {
    final ArrayDeque<IExpr> queue = new ArrayDeque<IExpr>();
    queue.add(expr);
    return queue;
  }

  private static void addArgs(ArrayDeque<IExpr> queue, IExpr node, int start) {
    if (node.isASTOrAssociation()) {
      final IAST ast = (IAST) node;
      final int size = ast.size();
      for (int i = start; i < size; i++) {
        queue.add(ast.get(i));
      }
    }
  }

  /**
   * The variables bound by <code>ast</code>.
   *
   * @param ast
   * @return an empty list, if <code>ast</code> is no binder
   */
  private static List<IExpr> boundVariables(IAST ast) {
    final List<IExpr> variables = new ArrayList<IExpr>();
    final IExpr head = ast.head();
    if (head == S.Integrate || head == S.NIntegrate) {
      addIterators(ast, variables, true);
    } else if (head == S.Sum || head == S.NSum || head == S.Product || head == S.NProduct
        || head == S.Table || head == S.Do) {
      // without a list the argument is a repeat count
      addIterators(ast, variables, false);
    } else if (head == S.Function) {
      if (ast.argSize() >= 2) {
        addParameters(ast.arg1(), variables);
      }
    } else if (head == S.Module || head == S.With || head == S.Block) {
      if (ast.argSize() >= 1) {
        addParameters(ast.arg1(), variables);
      }
    }
    return variables;
  }

  private static void addIterators(IAST ast, List<IExpr> variables, boolean bareVariable) {
    for (int i = 2; i < ast.size(); i++) {
      final IExpr spec = ast.get(i);
      if (spec.isList()) {
        // the n in {n} is a repeat count
        if (spec.argSize() >= 2) {
          variables.add(spec.first());
        }
      } else if (bareVariable) {
        variables.add(spec);
      }
    }
  }

  private static void addParameters(IExpr parameters, List<IExpr> variables) {
    if (parameters.isList()) {
      ((IAST) parameters)
          .forEach(x -> variables.add(x.isAST(S.Set) || x.isAST(S.SetDelayed) ? x.first() : x));
    } else {
      variables.add(parameters);
    }
  }

  /**
   * Use <code>function</code> to transform <code>expr</code> at the given level. The port of
   * <code>use</code>: level <code>0</code> is <code>expr</code> itself, level <code>1</code> are its
   * arguments, and atoms above the level stay as they are.
   *
   * <p>
   * This is <code>Map(f, expr, {level})</code> as a Java call. Unlike sympy nothing is evaluated, so
   * the caller decides if the result is to be canonical.
   *
   * @param expr
   * @param function a transformation for a single expression, which may return {@link F#NIL}
   * @param level
   * @return the transformed expression, or <code>expr</code> if nothing was changed
   */
  public static IExpr use(IExpr expr, Function<IExpr, IExpr> function, int level) {
    return use(expr, function, level, BASIC_FNS);
  }

  /**
   * Use <code>function</code> to transform <code>expr</code> at the given level.
   *
   * @param expr
   * @param function a transformation for a single expression, which may return {@link F#NIL}
   * @param level
   * @param fns where the descent stops, and how a node with changed arguments is rebuilt
   * @return the transformed expression, or <code>expr</code> if nothing was changed
   * @see #use(IExpr, Function, int)
   */
  public static IExpr use(IExpr expr, Function<IExpr, IExpr> function, int level, Fns fns) {
    if (level == 0) {
      return function.apply(expr).orElse(expr);
    }
    if (fns.isLeaf(expr)) {
      return expr;
    }
    final IAST ast = (IAST) expr;
    IASTMutable result = F.NIL;
    for (int i = 1; i < ast.size(); i++) {
      final IExpr arg = ast.get(i);
      final IExpr temp = use(arg, function, level - 1, fns);
      if (temp != arg) {
        result = setArgument(ast, result, i, temp);
      }
    }
    return result.isNIL() ? expr : fns.rebuild(result);
  }

  /**
   * Change an argument of a node without touching the original, the one write path of all walks in
   * these packages. It takes care of the two kinds of node for which <code>copy()</code> and
   * <code>set()</code> are not enough:
   * <ul>
   * <li>A packed vector or matrix holds only machine reals, so it's unpacked for another value.
   * <li>The argument of an association is its value, while <code>set()</code> takes a rule as a
   * whole new entry. So a rewrite to a rule stays the value of its key.
   * </ul>
   *
   * @param node the original node
   * @param copy the copy of <code>node</code> made by an earlier call, or {@link F#NIL} for the first
   *        change
   * @param position
   * @param value
   * @return the copy which holds <code>value</code>; use it for the next call
   */
  public static IASTMutable setArgument(IAST node, IASTMutable copy, int position, IExpr value) {
    if (node instanceof ASTAssociation) {
      final IASTMutable result = copy.isNIL() ? node.copy() : copy;
      ((ASTAssociation) result).setValue(position, value);
      return result;
    }
    if (copy.isNIL()) {
      return node.setAtCopy(position, value);
    }
    if (copy instanceof ASTRealVector || copy instanceof ASTRealMatrix) {
      return copy.setAtCopy(position, value);
    }
    copy.set(position, value);
    return copy;
  }

  /**
   * Iterate through the nodes selected by <code>target</code>; nodes it rejects are not traversed.
   * The port of <code>walk</code>, where the target is a list of types.
   *
   * <p>
   * The gate is the one of {@link org.matheclipse.core.visit.VisitorPredicateReplaceAll}, turned
   * around: that visitor rewrites the nodes where its predicate stops the descent, this iterator
   * returns the nodes where the descent continues.
   *
   * @param expr
   * @param target
   * @return a lazy iterable, which can be iterated more than once
   */
  public static Iterable<IExpr> walk(IExpr expr, Predicate<IExpr> target) {
    return () -> new AbstractIterator<IExpr>() {
      private final PreorderTraversal traversal = new PreorderTraversal(expr);

      @Override
      protected IExpr computeNext() {
        while (traversal.hasNext()) {
          final IExpr node = traversal.next();
          if (target.test(node)) {
            return node;
          }
          traversal.skip();
        }
        return endOfData();
      }
    };
  }

  /**
   * Iterate through the nodes with one of the given heads; nodes with another head are not
   * traversed.
   *
   * @param expr
   * @param heads
   * @return a lazy iterable, which can be iterated more than once
   * @see #walk(IExpr, Predicate)
   */
  public static Iterable<IExpr> walk(IExpr expr, ISymbol... heads) {
    return walk(expr, x -> {
      final IExpr head = x.head();
      for (ISymbol symbol : heads) {
        if (head == symbol) {
          return true;
        }
      }
      return false;
    });
  }

  /**
   * Apply <code>function</code> to all expressions in an expression tree from the bottom up.
   *
   * @param expr
   * @param function does a transformation on a single expression, or returns {@link F#NIL}
   * @return the transformed expression or the original expression if the expression tree wasn't
   *         changed
   * @see #bottomUp(IExpr, Function, boolean, boolean)
   */
  public static IExpr bottomUp(IExpr expr, Function<IExpr, IExpr> function) {
    return bottomUpNIL(expr, function, false, false).orElse(expr);
  }

  /**
   * Apply <code>function</code> to all expressions in an expression tree from the bottom up. The
   * port of <code>bottom_up</code>.
   *
   * <p>
   * {@link org.matheclipse.core.visit.VisitorReplaceAllDFS} is the visitor with this order. The
   * differences are:
   * <ul>
   * <li>Heads are not visited, and neither are atoms by default.
   * <li>A <code>function</code> which returns its argument is the same as one which returns
   * {@link F#NIL}, so an unchanged tree isn't copied.
   * <li>The walk is iterative. See {@link Traversal} for what that doesn't cover.
   * </ul>
   *
   * @param expr
   * @param function does a transformation on a single expression, or returns {@link F#NIL}
   * @param atoms if <code>true</code>, apply <code>function</code> to symbols, numbers and functions
   *        without arguments too; sympy's atoms are nodes without arguments.
   * @param nonbasic if <code>true</code>, apply <code>function</code> to strings and data objects
   *        too, which have no counterpart in the sympy expression tree.
   * @return the transformed expression or the original expression if the expression tree wasn't
   *         changed
   */
  public static IExpr bottomUp(IExpr expr, Function<IExpr, IExpr> function, boolean atoms,
      boolean nonbasic) {
    return bottomUpNIL(expr, function, atoms, nonbasic).orElse(expr);
  }

  /**
   * Apply <code>function</code> to all expressions in an expression tree from the bottom up.
   *
   * @param expr
   * @param function does a transformation on a single expression, or returns {@link F#NIL}
   * @return {@link F#NIL} if the expression tree wasn't changed through the applying of the
   *         <code>function</code>.
   * @see #bottomUp(IExpr, Function, boolean, boolean)
   */
  public static IExpr bottomUpNIL(IExpr expr, Function<IExpr, IExpr> function) {
    return bottomUpNIL(expr, function, false, false);
  }

  private static IExpr bottomUpNIL(IExpr expr, Function<IExpr, IExpr> function, boolean atoms,
      boolean nonbasic) {
    final Function<IExpr, IExpr> leafFunction;
    if (atoms && nonbasic) {
      leafFunction = function;
    } else if (atoms || nonbasic) {
      leafFunction = x -> isBasicLeaf(x) == atoms ? function.apply(x) : F.NIL;
    } else {
      leafFunction = null;
    }
    return rewrite(expr, null, function, leafFunction, BASIC_FNS);
  }

  /** Strings and data objects are the leaves which aren't nodes of a sympy expression tree. */
  private static boolean isBasicLeaf(IExpr leaf) {
    return !leaf.isString() && !(leaf instanceof IDataExpr);
  }

  /**
   * The walk behind {@link #bottomUp(IExpr, Function)} and
   * {@link org.matheclipse.core.sympy.core.strategies.Traverse}. For each node, in this order:
   * <code>pre</code> rewrites it, its arguments are transformed, it is rebuilt if an argument
   * changed, and <code>post</code> rewrites the result. The arguments of what <code>pre</code>
   * returned are the ones which are visited.
   *
   * @param expr
   * @param pre a rule applied on the way down, or <code>null</code>. It may return {@link F#NIL}.
   * @param post a rule applied on the way up, or <code>null</code>. It may return {@link F#NIL}.
   * @param fns where the walk stops, and how nodes are rebuilt
   * @return {@link F#NIL} if the expression tree wasn't changed
   */
  public static IExpr transform(IExpr expr, Function<IExpr, IExpr> pre,
      Function<IExpr, IExpr> post, Fns fns) {
    return rewrite(expr, pre, post, post, fns);
  }

  private static IExpr rewrite(IExpr root, Function<IExpr, IExpr> pre, Function<IExpr, IExpr> post,
      Function<IExpr, IExpr> leafPost, Fns fns) {
    final IExpr start = applyRule(pre, root);
    if (fns.isLeaf(start)) {
      final IExpr result = applyRule(leafPost, start);
      return result == root ? F.NIL : result;
    }
    final FrameStack frames = new FrameStack();
    frames.push((IAST) start, null, 1);
    while (true) {
      final int top = frames.top;
      final IAST node = frames.nodes[top];
      final int i = frames.index[top];
      if (i < node.size()) {
        frames.index[top] = i + 1;
        final IExpr arg = node.get(i);
        final IExpr child = applyRule(pre, arg);
        if (fns.isLeaf(child)) {
          setChanged(frames, i, arg, applyRule(leafPost, child));
        } else {
          frames.child[top] = arg;
          frames.push((IAST) child, null, 1);
        }
      } else {
        final IAST copy = frames.aux[top];
        final IExpr rebuilt = copy == null ? node : fns.rebuild((IASTMutable) copy);
        final IExpr result = applyRule(post, rebuilt);
        frames.pop();
        if (frames.isEmpty()) {
          return result == root ? F.NIL : result;
        }
        setChanged(frames, frames.index[frames.top] - 1, frames.child[frames.top], result);
      }
    }
  }

  private static IExpr applyRule(Function<IExpr, IExpr> rule, IExpr expr) {
    return rule == null ? expr : rule.apply(expr).orElse(expr);
  }

  /** Copy the top node on the first changed argument. */
  private static void setChanged(FrameStack frames, int position, IExpr arg, IExpr value) {
    if (value != arg) {
      final int top = frames.top;
      final IASTMutable copy = frames.aux[top] == null ? F.NIL : (IASTMutable) frames.aux[top];
      frames.aux[top] = setArgument(frames.nodes[top], copy, position, value);
    }
  }
}
