package org.matheclipse.core.builtin;

import java.util.List;
import org.matheclipse.core.annotation.AllowNIL;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.patternmatching.IPatternMatcher;

/**
 * Tree expressions <code>Tree(data, {child1, child2, ...})</code> and the functions on them. A
 * leaf is <code>Tree(data, None)</code>; an inner node may have an empty list of children.
 *
 * <p>
 * The traversals visit the nodes in post-order: the children from left to right before their
 * parent. The root is on level <code>0</code>. In a level specification a negative level counts
 * from the bottom: a node is on level <code>-k</code> if its subtree has the depth <code>k</code>,
 * a leaf is on level <code>-1</code>.
 */
public final class TreeFunctions {

  private static class Initializer {

    private static void init() {
      S.Tree.setEvaluator(new Tree());
      S.TreeQ.setEvaluator(new TreeQ(false));
      S.TreeLeafQ.setEvaluator(new TreeQ(true));
      S.TreeData.setEvaluator(new TreePart(S.TreeData));
      S.TreeChildren.setEvaluator(new TreePart(S.TreeChildren));
      S.TreeSize.setEvaluator(new TreePart(S.TreeSize));
      S.TreeDepth.setEvaluator(new TreePart(S.TreeDepth));
      S.TreeLeafCount.setEvaluator(new TreePart(S.TreeLeafCount));
      S.TreeLeaves.setEvaluator(new TreePart(S.TreeLeaves));
      S.TreeRules.setEvaluator(new TreePart(S.TreeRules));
      S.TreeExpression.setEvaluator(new TreePart(S.TreeExpression));
      S.RootTree.setEvaluator(new RootTree());
      S.TreeLevel.setEvaluator(new TreeLevel());
      S.TreeSelect.setEvaluator(new TreeSelect());
      S.TreePosition.setEvaluator(new TreeMatch(S.TreePosition));
      S.TreeCases.setEvaluator(new TreeMatch(S.TreeCases));
      S.TreeCount.setEvaluator(new TreeMatch(S.TreeCount));
      S.TreeMap.setEvaluator(new TreeMap());
      S.TreeScan.setEvaluator(new TreeScan());
      S.TreeFold.setEvaluator(new TreeFold());
      S.TreeExtract.setEvaluator(new TreeExtract());
      S.TreeReplacePart.setEvaluator(new TreeReplacePart());
      S.TreeInsert.setEvaluator(new TreeInsert());
      S.TreeDelete.setEvaluator(new TreeDelete());
      S.ExpressionTree.setEvaluator(new ExpressionTree());
    }
  }

  // ---------------------------------------------------------------------------------------------
  // the tree representation

  /** <code>true</code> if <code>expr</code> has the form <code>Tree(data, children)</code>. */
  static boolean isTree(IExpr expr) {
    return expr.isAST(S.Tree, 3);
  }

  /** <code>true</code> if <code>expr</code> is a tree whose children are trees, recursively. */
  static boolean isValidTree(IExpr expr) {
    if (!isTree(expr)) {
      return false;
    }
    IExpr children = expr.second();
    if (children == S.None) {
      return true;
    }
    if (!children.isList()) {
      return false;
    }
    for (IExpr child : (IAST) children) {
      if (!isValidTree(child)) {
        return false;
      }
    }
    return true;
  }

  /** The children of a tree; a leaf has none. */
  static IAST children(IExpr tree) {
    IExpr children = tree.second();
    return children.isList() ? (IAST) children : F.CEmptyList;
  }

  /** A leaf <code>Tree(data, None)</code>. */
  static IAST leaf(IExpr data) {
    return F.binaryAST2(S.Tree, data, S.None);
  }

  /** A tree stays as it is, every other expression becomes a leaf. */
  static IExpr asTree(IExpr expr) {
    return isTree(expr) ? expr : leaf(expr);
  }

  /** The tree with a new list of children. */
  static IAST withChildren(IExpr tree, IAST children) {
    return F.binaryAST2(S.Tree, tree.first(), children);
  }

  /** Print <code>head::tree</code> for a non-tree at the given position and return NIL. */
  private static IExpr treeExpected(IAST ast, int position, EvalEngine engine) {
    // Tree expected at position `1` in `2`.
    return Errors.printMessage(ast.topHead(), "tree", F.list(F.ZZ(position), ast), engine);
  }

  /** The height of a tree: <code>0</code> for a leaf or a node without children. */
  static int height(IExpr tree) {
    int height = 0;
    for (IExpr child : children(tree)) {
      height = Math.max(height, height(child) + 1);
    }
    return height;
  }

  // ---------------------------------------------------------------------------------------------
  // level specifications

  /** A level specification <code>{lo, hi}</code>; <code>Integer.MAX_VALUE</code> is Infinity. */
  static final class LevelSpec {
    final int lo;
    final int hi;

    LevelSpec(int lo, int hi) {
      this.lo = lo;
      this.hi = hi;
    }

    /** Every node, the root included. */
    static final LevelSpec ALL = new LevelSpec(0, Integer.MAX_VALUE);

    /**
     * <code>n</code> means <code>{1, n}</code>, <code>{n}</code> means <code>{n, n}</code>,
     * <code>Infinity</code> means <code>{1, Infinity}</code> and <code>All</code> every level.
     *
     * @return <code>null</code> if <code>spec</code> isn't a level specification
     */
    static LevelSpec of(IExpr spec) {
      if (spec.isInfinity()) {
        return new LevelSpec(1, Integer.MAX_VALUE);
      }
      if (spec == S.All) {
        return ALL;
      }
      if (spec.isInteger()) {
        int n = spec.toIntDefault();
        return n == Integer.MIN_VALUE ? null : new LevelSpec(1, n);
      }
      if (spec.isList1()) {
        int n = spec.first().toIntDefault();
        return n == Integer.MIN_VALUE ? null : new LevelSpec(n, n);
      }
      if (spec.isList2()) {
        int lo = spec.first().toIntDefault();
        int hi = spec.second().isInfinity() ? Integer.MAX_VALUE : spec.second().toIntDefault();
        return lo == Integer.MIN_VALUE || hi == Integer.MIN_VALUE ? null : new LevelSpec(lo, hi);
      }
      return null;
    }

    /**
     * Is a node on the level <code>level</code> whose subtree has the depth <code>depth</code>
     * (<code>height + 1</code>) inside the specification?
     */
    boolean contains(int level, int depth) {
      boolean lower = lo >= 0 ? level >= lo : depth <= -lo;
      boolean upper = hi >= 0 ? level <= hi : depth >= -hi;
      return lower && upper;
    }
  }

  /**
   * Visit the subtrees of <code>tree</code> inside the level specification in post-order.
   *
   * @return the height of <code>tree</code>
   */
  static int postOrder(IExpr tree, int level, LevelSpec spec, IAST path, Visitor visitor,
      EvalEngine engine) {
    int height = 0;
    IAST children = children(tree);
    for (int i = 1; i < children.size(); i++) {
      int childHeight = postOrder(children.get(i), level + 1, spec,
          path == null ? null : path.appendClone(F.ZZ(i)), visitor, engine);
      height = Math.max(height, childHeight + 1);
    }
    if (spec.contains(level, height + 1)) {
      engine.checkTimeLimit();
      visitor.visit(tree, path);
    }
    return height;
  }

  @FunctionalInterface
  interface Visitor {
    void visit(IExpr subtree, IAST path);
  }

  // ---------------------------------------------------------------------------------------------
  // Tree, TreeQ, TreeLeafQ

  /**
   * <code>Tree(data, {child1, child2, ...})</code> - a tree; a child which isn't a tree becomes the
   * leaf <code>Tree(child, None)</code>. <code>Tree(data, None)</code> is a leaf.
   */
  private static final class Tree extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.argSize() > 2) {
        IExpr last = ast.last();
        if (!last.isRuleAST() && !(last.isList() && ((IAST) last).forAll(x -> x.isRuleAST()))) {
          // Options expected (instead of `1`) beyond position `2` in `3`.
          return Errors.printMessage(S.Tree, "nonopt", F.list(last, F.C2, ast), engine);
        }
        return F.NIL;
      }
      if (ast.isAST2() && ast.arg2().isList()) {
        IAST children = (IAST) ast.arg2();
        IASTMutable canonical = null;
        for (int i = 1; i < children.size(); i++) {
          if (!isTree(children.get(i))) {
            if (canonical == null) {
              canonical = children.copy();
            }
            canonical.set(i, leaf(children.get(i)));
          }
        }
        if (canonical != null) {
          return F.binaryAST2(S.Tree, ast.arg1(), canonical);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }
  }

  /**
   * <code>TreeQ(expr)</code> - <code>True</code> if <code>expr</code> is a valid tree.
   * <code>TreeLeafQ(expr)</code> - <code>True</code> if <code>expr</code> is a leaf
   * <code>Tree(data, None)</code>.
   */
  private static final class TreeQ extends AbstractFunctionEvaluator {

    private final boolean leaf;

    TreeQ(boolean leaf) {
      this.leaf = leaf;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (leaf) {
        return F.booleSymbol(isTree(arg1) && arg1.second() == S.None);
      }
      return F.booleSymbol(isValidTree(arg1));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  // ---------------------------------------------------------------------------------------------
  // the functions of one tree argument

  /**
   * <code>TreeData(tree)</code>, <code>TreeChildren(tree)</code>, <code>TreeSize(tree)</code>,
   * <code>TreeDepth(tree)</code>, <code>TreeLeafCount(tree)</code>, <code>TreeLeaves(tree)</code>,
   * <code>TreeRules(tree)</code> and <code>TreeExpression(tree)</code>.
   */
  private static final class TreePart extends AbstractFunctionEvaluator {

    private final IBuiltInSymbol function;

    TreePart(IBuiltInSymbol function) {
      this.function = function;
    }

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr tree = ast.arg1();
      if (!isTree(tree)) {
        return treeExpected(ast, 1, engine);
      }
      if (function == S.TreeData) {
        return tree.first();
      }
      if (function == S.TreeChildren) {
        return tree.second();
      }
      if (function == S.TreeSize) {
        return F.ZZ(size(tree));
      }
      if (function == S.TreeDepth) {
        return F.ZZ(height(tree));
      }
      if (function == S.TreeLeafCount) {
        return F.ZZ(leafCount(tree));
      }
      if (function == S.TreeLeaves) {
        IASTAppendable leaves = F.ListAlloc();
        leaves(tree, leaves);
        return leaves;
      }
      if (function == S.TreeRules) {
        return rules(tree);
      }
      return expression(tree, engine);
    }

    private static int size(IExpr tree) {
      int size = 1;
      for (IExpr child : children(tree)) {
        size += size(child);
      }
      return size;
    }

    private static int leafCount(IExpr tree) {
      IAST children = children(tree);
      if (children.isEmpty()) {
        return 1;
      }
      int count = 0;
      for (IExpr child : children) {
        count += leafCount(child);
      }
      return count;
    }

    private static void leaves(IExpr tree, IASTAppendable leaves) {
      IAST children = children(tree);
      if (children.isEmpty()) {
        leaves.append(tree);
        return;
      }
      for (IExpr child : children) {
        leaves(child, leaves);
      }
    }

    /** <code>data</code> for a leaf, <code>data -&gt; {rules of the children}</code> otherwise. */
    private static IExpr rules(IExpr tree) {
      IAST children = children(tree);
      if (children.isEmpty()) {
        return tree.first();
      }
      return F.Rule(tree.first(), children.map(TreePart::rules));
    }

    /**
     * The expression the tree stands for: the data of a leaf, or the data of an inner node applied
     * to the expressions of its children. A head which is a tree is converted too.
     */
    private static IExpr expression(IExpr tree, EvalEngine engine) {
      if (tree.second() == S.None) {
        return tree.first();
      }
      IExpr head = tree.first();
      if (isTree(head)) {
        head = expression(head, engine);
      }
      IAST children = children(tree);
      IASTAppendable result = F.ast(head, children.argSize());
      for (IExpr child : children) {
        result.append(expression(child, engine));
      }
      return engine.evaluate(result);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   * <code>RootTree(tree)</code> - the root of the tree with an empty list of children.
   * <code>RootTree(tree, n)</code> - the tree down to the level <code>n</code>.
   */
  private static final class RootTree extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!isTree(ast.arg1())) {
        return treeExpected(ast, 1, engine);
      }
      int n = 0;
      if (ast.isAST2()) {
        if (ast.arg2().isInfinity()) {
          return ast.arg1();
        }
        n = ast.arg2().toIntDefault();
        if (n < 0) {
          return F.NIL;
        }
      }
      return truncate(ast.arg1(), 0, n);
    }

    private static IExpr truncate(IExpr tree, int level, int n) {
      if (!tree.second().isList()) {
        return tree;
      }
      if (level >= n) {
        return withChildren(tree, F.CEmptyList);
      }
      return withChildren(tree, children(tree).map(child -> truncate(child, level + 1, n)));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  // ---------------------------------------------------------------------------------------------
  // traversals

  /** <code>TreeLevel(tree, levelspec)</code> - the subtrees on the given levels, in post-order. */
  private static final class TreeLevel extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!isTree(ast.arg1())) {
        return treeExpected(ast, 1, engine);
      }
      LevelSpec spec = LevelSpec.of(ast.arg2());
      if (spec == null) {
        return F.NIL;
      }
      IASTAppendable result = F.ListAlloc();
      postOrder(ast.arg1(), 0, spec, null, (subtree, path) -> result.append(subtree), engine);
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   * <code>TreeSelect(tree, crit)</code>, <code>TreeSelect(tree, crit, n)</code>,
   * <code>TreeSelect(tree, crit, levelspec, n)</code> - the subtrees for which
   * <code>crit(subtree)</code> is <code>True</code>, in post-order. <code>TreeSelect(crit)</code>
   * is the operator form.
   */
  private static final class TreeSelect extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        return F.NIL;
      }
      if (!isTree(ast.arg1())) {
        return treeExpected(ast, 1, engine);
      }
      LevelSpec spec = LevelSpec.ALL;
      IExpr limit = F.CInfinity;
      if (ast.argSize() == 3) {
        limit = ast.arg3();
      } else if (ast.argSize() == 4) {
        spec = LevelSpec.of(ast.arg3());
        if (spec == null) {
          return F.NIL;
        }
        limit = ast.arg4();
      }
      final int max = limit.isInfinity() ? Integer.MAX_VALUE : limit.toIntDefault();
      if (max < 0) {
        // Non-negative integer or Infinity expected at position `1` in `2`.
        return Errors.printMessage(S.TreeSelect, "innf", F.list(F.ZZ(ast.argSize()), ast),
            engine);
      }
      final IExpr criterion = ast.arg2();
      IASTAppendable result = F.ListAlloc();
      postOrder(ast.arg1(), 0, spec, null, (subtree, path) -> {
        if (result.argSize() < max && engine.evalTrue(F.unaryAST1(criterion, subtree))) {
          result.append(subtree);
        }
      }, engine);
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_4_1;
    }
  }

  /**
   * <code>TreePosition(tree, pattern)</code>, <code>TreeCases(tree, pattern)</code>,
   * <code>TreeCount(tree, pattern)</code> - the positions, the subtrees and the number of the
   * nodes whose data matches <code>pattern</code>, in post-order. A level specification may follow.
   * The position of the root is <code>{}</code>.
   */
  private static final class TreeMatch extends AbstractFunctionEvaluator {

    private final IBuiltInSymbol function;

    TreeMatch(IBuiltInSymbol function) {
      this.function = function;
    }

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!isTree(ast.arg1())) {
        return treeExpected(ast, 1, engine);
      }
      LevelSpec spec = LevelSpec.ALL;
      if (ast.isAST3()) {
        spec = LevelSpec.of(ast.arg3());
        if (spec == null) {
          return F.NIL;
        }
      }
      final IPatternMatcher matcher = engine.evalPatternMatcher(ast.arg2());
      IASTAppendable result = F.ListAlloc();
      postOrder(ast.arg1(), 0, spec, F.CEmptyList, (subtree, path) -> {
        if (matcher.test(subtree.first(), engine)) {
          result.append(function == S.TreePosition ? path : subtree);
        }
      }, engine);
      return function == S.TreeCount ? F.ZZ(result.argSize()) : result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }
  }

  /**
   * <code>TreeMap(f, tree)</code>, <code>TreeMap(f, tree, levelspec)</code> - apply
   * <code>f</code> to the data of the nodes. <code>TreeMap(f)</code> is the operator form.
   */
  private static final class TreeMap extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        return F.NIL;
      }
      if (!isTree(ast.arg2())) {
        return treeExpected(ast, 2, engine);
      }
      LevelSpec spec = LevelSpec.ALL;
      if (ast.isAST3()) {
        spec = LevelSpec.of(ast.arg3());
        if (spec == null) {
          return F.NIL;
        }
      }
      int[] height = new int[1];
      return map(ast.arg1(), ast.arg2(), 0, spec, height, engine);
    }

    private static IExpr map(IExpr function, IExpr tree, int level, LevelSpec spec, int[] height,
        EvalEngine engine) {
      IExpr children = tree.second();
      int h = 0;
      if (children.isList()) {
        IASTAppendable mapped = F.ListAlloc(children.argSize());
        for (IExpr child : (IAST) children) {
          int[] childHeight = new int[1];
          mapped.append(map(function, child, level + 1, spec, childHeight, engine));
          h = Math.max(h, childHeight[0] + 1);
        }
        children = mapped;
      }
      height[0] = h;
      IExpr data = tree.first();
      if (spec.contains(level, h + 1)) {
        engine.checkTimeLimit();
        data = engine.evaluate(F.unaryAST1(function, data));
      }
      return F.binaryAST2(S.Tree, data, children);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3_2;
    }
  }

  /** <code>TreeScan(f, tree)</code> - apply <code>f</code> to the data of the nodes in post-order. */
  private static final class TreeScan extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        return F.NIL;
      }
      if (!isTree(ast.arg2())) {
        return treeExpected(ast, 2, engine);
      }
      LevelSpec spec = LevelSpec.ALL;
      if (ast.isAST3()) {
        spec = LevelSpec.of(ast.arg3());
        if (spec == null) {
          return F.NIL;
        }
      }
      final IExpr function = ast.arg1();
      postOrder(ast.arg2(), 0, spec, null,
          (subtree, path) -> engine.evaluate(F.unaryAST1(function, subtree.first())), engine);
      return S.Null;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3_2;
    }
  }

  /**
   * <code>TreeFold(f, tree)</code> - a leaf gives its data; an inner node gives
   * <code>f(data, {results of the children})</code>. <code>TreeFold(f)</code> is the operator
   * form.
   */
  private static final class TreeFold extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        return F.NIL;
      }
      if (!isTree(ast.arg2())) {
        return treeExpected(ast, 2, engine);
      }
      return fold(ast.arg1(), ast.arg2(), engine);
    }

    private static IExpr fold(IExpr function, IExpr tree, EvalEngine engine) {
      IAST children = children(tree);
      if (children.isEmpty()) {
        return tree.first();
      }
      engine.checkTimeLimit();
      IAST folded = children.map(child -> fold(function, child, engine));
      return engine.evaluate(F.binaryAST2(function, tree.first(), folded));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2_2;
    }
  }

  // ---------------------------------------------------------------------------------------------
  // positions

  /** The integer path of a position <code>{i, j, ...}</code>, or <code>null</code>. */
  private static int[] path(IExpr position) {
    if (position.isInteger()) {
      int i = position.toIntDefault();
      return i == Integer.MIN_VALUE ? null : new int[] {i};
    }
    if (!position.isList()) {
      return null;
    }
    IAST list = (IAST) position;
    int[] path = new int[list.argSize()];
    for (int k = 0; k < path.length; k++) {
      path[k] = list.get(k + 1).toIntDefault();
      if (path[k] == Integer.MIN_VALUE) {
        return null;
      }
    }
    return path;
  }

  /**
   * The 1-based index of the slot <code>index</code> among <code>length</code> children, where
   * <code>extra</code> slots after the last child are allowed; a negative index counts from the end.
   * <code>-1</code> if there is no such slot.
   */
  private static int slot(int index, int length, int extra) {
    int position = index < 0 ? length + index + 1 + extra : index;
    return position < 1 || position > length + extra ? -1 : position;
  }

  /** The subtree at the path, or <code>null</code>. */
  private static IExpr navigate(IExpr tree, int[] path) {
    for (int index : path) {
      IAST children = children(tree);
      if (index < 1 || index > children.argSize()) {
        return null;
      }
      tree = children.get(index);
    }
    return tree;
  }

  /**
   * <code>TreeExtract(tree, pos)</code>, <code>TreeExtract(tree, {pos1, pos2, ...})</code> - the
   * subtree at the position, or the list of the subtrees at the positions.
   */
  private static final class TreeExtract extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr tree = ast.arg1();
      if (!isTree(tree)) {
        return treeExpected(ast, 1, engine);
      }
      final IExpr positions = ast.arg2();
      if (positions.isList() && ((IAST) positions).forAll(x -> x.isList())) {
        IASTAppendable result = F.ListAlloc(positions.argSize());
        for (IExpr position : (IAST) positions) {
          int[] path = path(position);
          IExpr subtree = path == null ? null : navigate(tree, path);
          if (subtree == null) {
            return notApplicable(ast, engine);
          }
          result.append(subtree);
        }
        return result;
      }
      int[] path = path(positions);
      IExpr subtree = path == null || !positions.isList() ? null : navigate(tree, path);
      if (subtree == null) {
        return notApplicable(ast, engine);
      }
      return subtree;
    }

    private static IExpr notApplicable(IAST ast, EvalEngine engine) {
      // Position specification `1` in `2` is not applicable.
      return Errors.printMessage(S.TreeExtract, "psl1", F.list(ast.arg2(), ast), engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   * The tree with the subtree at the path replaced by <code>value</code>, or <code>null</code> if
   * there is no such subtree.
   */
  private static IExpr replaceAt(IExpr tree, int[] path, int k, IExpr value) {
    if (k == path.length) {
      return value;
    }
    if (!tree.second().isList()) {
      return null;
    }
    IAST children = children(tree);
    int index = path[k];
    if (index < 1 || index > children.argSize()) {
      return null;
    }
    IExpr replaced = replaceAt(children.get(index), path, k + 1, value);
    return replaced == null ? null : withChildren(tree, children.setAtCopy(index, replaced));
  }

  /**
   * <code>TreeReplacePart(tree, pos -&gt; new)</code>,
   * <code>TreeReplacePart(tree, {pos1 -&gt; new1, ...})</code> - replace the subtrees at the
   * positions; a <code>new</code> which isn't a tree becomes a leaf. Positions which aren't in the
   * tree and the root position <code>{}</code> are ignored. <code>TreeReplacePart(rules)</code> is
   * the operator form.
   */
  private static final class TreeReplacePart extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        return F.NIL;
      }
      if (!isTree(ast.arg1())) {
        return treeExpected(ast, 1, engine);
      }
      IExpr rules = ast.arg2();
      IAST ruleList = rules.isList() ? (IAST) rules : F.list(rules);
      if (ruleList.isEmpty() || !ruleList.forAll(x -> x.isRuleAST())) {
        return F.NIL;
      }
      IExpr tree = ast.arg1();
      for (IExpr rule : ruleList) {
        int[] path = path(rule.first());
        if (path == null || path.length == 0) {
          continue;
        }
        IExpr replaced = replaceAt(tree, path, 0, asTree(rule.second()));
        if (replaced != null) {
          tree = replaced;
        }
      }
      return tree;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2_1;
    }
  }

  /**
   * <code>TreeInsert(tree, child, pos)</code> - insert <code>child</code> at the position; the last
   * index of <code>pos</code> is the place among the siblings, <code>-1</code> after the last one.
   */
  private static final class TreeInsert extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!isTree(ast.arg1())) {
        return treeExpected(ast, 1, engine);
      }
      int[] path = path(ast.arg3());
      if (path == null || path.length == 0) {
        return F.NIL;
      }
      IExpr inserted = insert(ast.arg1(), path, 0, asTree(ast.arg2()));
      if (inserted == null) {
        // Cannot insert at position `1` in `2`.
        return Errors.printMessage(S.TreeInsert, "ins", F.list(ast.arg3(), ast.arg1()), engine);
      }
      return inserted;
    }

    private static IExpr insert(IExpr tree, int[] path, int k, IExpr child) {
      if (!tree.second().isList()) {
        return null;
      }
      IAST children = children(tree);
      if (k == path.length - 1) {
        int slot = slot(path[k], children.argSize(), 1);
        if (slot < 0) {
          return null;
        }
        IASTAppendable result = children.copyAppendable();
        result.append(slot, child);
        return withChildren(tree, result);
      }
      int slot = slot(path[k], children.argSize(), 0);
      if (slot < 0) {
        return null;
      }
      IExpr replaced = insert(children.get(slot), path, k + 1, child);
      return replaced == null ? null : withChildren(tree, children.setAtCopy(slot, replaced));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }
  }

  /**
   * <code>TreeDelete(tree, pos)</code> - remove the subtree at the position. The parent keeps its
   * list of children, which may become empty.
   */
  private static final class TreeDelete extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!isTree(ast.arg1())) {
        return treeExpected(ast, 1, engine);
      }
      int[] path = path(ast.arg2());
      if (path == null || path.length == 0) {
        return F.NIL;
      }
      IExpr deleted = delete(ast.arg1(), path, 0);
      return deleted == null ? F.NIL : deleted;
    }

    private static IExpr delete(IExpr tree, int[] path, int k) {
      if (!tree.second().isList()) {
        return null;
      }
      IAST children = children(tree);
      int slot = slot(path[k], children.argSize(), 0);
      if (slot < 0) {
        return null;
      }
      if (k == path.length - 1) {
        return withChildren(tree, children.removeAtCopy(slot));
      }
      IExpr replaced = delete(children.get(slot), path, k + 1);
      return replaced == null ? null : withChildren(tree, children.setAtCopy(slot, replaced));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  // ---------------------------------------------------------------------------------------------
  // ExpressionTree

  /**
   * <code>ExpressionTree(expr)</code>, <code>ExpressionTree(expr, structure)</code> - the tree of
   * an expression: an atom is a leaf, a compound expression is a node with its arguments as
   * children. The data of an inner node is its head (<code>"Heads"</code>, the default), its head
   * as a tree if that is compound (<code>"HeadTrees"</code>), the whole subexpression
   * (<code>"Subexpressions"</code>) or <code>Null</code> (<code>"Atoms"</code>).
   */
  private static final class ExpressionTree extends AbstractFunctionEvaluator {

    private static final List<String> STRUCTURES =
        List.of("Heads", "HeadTrees", "Subexpressions", "Atoms");

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      String structure = "Heads";
      if (ast.isAST2()) {
        structure = ast.arg2().isString() ? ast.arg2().toString() : "";
        if (!STRUCTURES.contains(structure)) {
          // `1` is not a valid expression structure. ...
          return Errors.printMessage(S.ExpressionTree, "struct", F.list(ast.arg2()), engine);
        }
      }
      return build(ast.arg1(), structure, engine);
    }

    private static IExpr build(IExpr expr, String structure, EvalEngine engine) {
      if (!expr.isAST()) {
        return leaf(expr);
      }
      engine.checkTimeLimit();
      IAST ast = (IAST) expr;
      IASTAppendable children = F.ListAlloc(ast.argSize());
      for (IExpr argument : ast) {
        children.append(build(argument, structure, engine));
      }
      IExpr data;
      switch (structure) {
        case "Subexpressions":
          data = expr;
          break;
        case "Atoms":
          data = S.Null;
          break;
        case "HeadTrees":
          data = ast.head().isAST() ? build(ast.head(), structure, engine) : ast.head();
          break;
        default:
          data = ast.head();
      }
      return F.binaryAST2(S.Tree, data, children);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /** One to four arguments; the operator form <code>f(x)(tree)</code> is <code>f(tree, x)</code>. */
  private static final int[] ARGS_1_4_1 = new int[] {1, 4, 1};

  /** One to three arguments; the operator form <code>f(x)(tree)</code> is <code>f(x, tree)</code>. */
  private static final int[] ARGS_1_3_2 = new int[] {1, 3, 2};

  public static void initialize() {
    Initializer.init();
  }

  private TreeFunctions() {}
}
