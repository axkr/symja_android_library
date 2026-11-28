package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** Tests for Tree and the tree functions. */
public class TreeFunctionsTest extends ExprEvaluatorTestCase {

  @Test
  public void testTree() {
    check("Tree(r, {u, Tree(v, {w})})", //
        "Tree(r,{Tree(u,None),Tree(v,{Tree(w,None)})})");
    check("Tree(r, None)", //
        "Tree(r,None)");
    check("Tree(r, {u}, w)", //
        "Tree(r,{u},w)");
  }

  @Test
  public void testTreeQ() {
    check("TreeQ(Tree(r, {u, Tree(v, {w})}))", //
        "True");
    check("TreeQ(Tree(r, {u, 5}))", //
        "True");
    check("TreeQ({1, 2})", //
        "False");
  }

  @Test
  public void testTreeLeafQ() {
    check("TreeLeafQ(Tree(u, None))", //
        "True");
    check("TreeLeafQ(Tree(u, {}))", //
        "False");
  }

  @Test
  public void testTreeData() {
    check("TreeData(Tree(r, {u, Tree(v, {w})}))", //
        "r");
    check("TreeData(r)", //
        "TreeData(r)");
  }

  @Test
  public void testTreeChildren() {
    check("TreeChildren(Tree(r, {u}))", //
        "{Tree(u,None)}");
  }

  @Test
  public void testTreeSize() {
    check("TreeSize(Tree(r, {u, Tree(v, {w, z})}))", //
        "5");
  }

  @Test
  public void testTreeDepth() {
    check("TreeDepth(Tree(r, {u, Tree(v, {w, z})}))", //
        "2");
    check("TreeDepth(Tree(u, None))", //
        "0");
  }

  @Test
  public void testTreeLeafCount() {
    check("TreeLeafCount(Tree(r, {u, Tree(v, {w, z})}))", //
        "3");
  }

  @Test
  public void testTreeLeaves() {
    check("TreeLeaves(Tree(r, {u, Tree(v, {w})}))", //
        "{Tree(u,None),Tree(w,None)}");
  }

  @Test
  public void testTreeRules() {
    check("TreeRules(Tree(1, {2, Tree(3, {4})}))", //
        "1->{2,3->{4}}");
  }

  @Test
  public void testRootTree() {
    check("RootTree(Tree(r, {u, Tree(v, {w})}))", //
        "Tree(r,{})");
    check("RootTree(Tree(r, {u, Tree(v, {w})}), 1)", //
        "Tree(r,{Tree(u,None),Tree(v,{})})");
    check("RootTree(Tree(r, {u, Tree(v, {w})}), Infinity)", //
        "Tree(r,{Tree(u,None),Tree(v,{Tree(w,None)})})");
  }

  @Test
  public void testTreeLevel() {
    check("TreeLevel(Tree(r, {u, Tree(v, {w})}), {1})", //
        "{Tree(u,None),Tree(v,{Tree(w,None)})}");
    check("TreeLevel(Tree(r, {u, Tree(v, {w})}), {0, Infinity})", //
        "{Tree(u,None),Tree(w,None),Tree(v,{Tree(w,None)}),Tree(r,{Tree(u,None),Tree(v,{Tree(w,None)})})}");
    check("TreeLevel(Tree(r, {u, Tree(v, {w})}), {-1})", //
        "{Tree(u,None),Tree(w,None)}");
    check("TreeLevel(Tree(r, {u, Tree(v, {w})}), 2)", //
        "{Tree(u,None),Tree(w,None),Tree(v,{Tree(w,None)})}");
  }

  @Test
  public void testTreeSelect() {
    check("TreeSelect(Tree(1, {2, Tree(3, {4, 5})}), EvenQ(TreeData(#)) &)", //
        "{Tree(2,None),Tree(4,None)}");
    check("TreeSelect(Tree(1, {2, Tree(3, {4, 5})}), OddQ(TreeData(#)) &, 2)", //
        "{Tree(5,None),Tree(3,{Tree(4,None),Tree(5,None)})}");
    check("TreeSelect(OddQ(TreeData(#)) &)[Tree(1, {2, 3})]", //
        "{Tree(3,None),Tree(1,{Tree(2,None),Tree(3,None)})}");
  }

  @Test
  public void testTreePosition() {
    check("TreePosition(Tree(a, {b, Tree(a, {a})}), a)", //
        "{{2,1},{2},{}}");
    check("TreePosition(Tree(a, {b, Tree(a, {a})}), a, {1})", //
        "{{2}}");
  }

  @Test
  public void testTreeCases() {
    check("TreeCases(Tree(1, {x, Tree(2, {y})}), _Integer)", //
        "{Tree(2,{Tree(y,None)}),Tree(1,{Tree(x,None),Tree(2,{Tree(y,None)})})}");
  }

  @Test
  public void testTreeCount() {
    check("TreeCount(Tree(1, {x, Tree(2, {y, 3})}), _Integer)", //
        "3");
    check("TreeCount(Tree(1, {x, Tree(2, {y, 3})}), _Integer, {-1})", //
        "1");
  }

  @Test
  public void testTreeMap() {
    check("TreeMap(f, Tree(r, {u, Tree(v, {w})}))", //
        "Tree(f(r),{Tree(f(u),None),Tree(f(v),{Tree(f(w),None)})})");
    check("TreeMap(f, Tree(r, {u, Tree(v, {w})}), {1})", //
        "Tree(r,{Tree(f(u),None),Tree(f(v),{Tree(w,None)})})");
    check("TreeMap(g)[Tree(r, {u})]", //
        "Tree(g(r),{Tree(g(u),None)})");
    check("TreeMap(f, r)", //
        "TreeMap(f,r)");
  }

  @Test
  public void testModule() {
    check("Module({s = {}}, TreeScan(AppendTo(s, #) &, Tree(r, {u, Tree(v, {w})})); s)", //
        "{u,w,v,r}");
  }

  @Test
  public void testTreeFold() {
    check("TreeFold(h, Tree(r, {u, Tree(v, {w, z})}))", //
        "h(r,{u,h(v,{w,z})})");
    check("TreeFold(Plus @@ Prepend(#2, #1) &, Tree(1, {2, Tree(3, {4, 5})}))", //
        "15");
    check("TreeFold(h)[Tree(r, {u})]", //
        "h(r,{u})");
  }

  @Test
  public void testTreeExtract() {
    check("TreeExtract(Tree(r, {u, Tree(v, {w})}), {2, 1})", //
        "Tree(w,None)");
    check("TreeExtract(Tree(r, {u, Tree(v, {w})}), {{1}, {2}})", //
        "{Tree(u,None),Tree(v,{Tree(w,None)})}");
    check("TreeExtract(Tree(r, {u}), {5})", //
        "TreeExtract(Tree(r,{Tree(u,None)}),{5})");
  }

  @Test
  public void testTreeReplacePart() {
    check("TreeReplacePart(Tree(r, {u, Tree(v, {w})}), {2, 1} -> n)", //
        "Tree(r,{Tree(u,None),Tree(v,{Tree(n,None)})})");
    check("TreeReplacePart(Tree(r, {u, v}), {{1} -> p, {2} -> Tree(q, {s})})", //
        "Tree(r,{Tree(p,None),Tree(q,{Tree(s,None)})})");
    check("TreeReplacePart({1} -> p)[Tree(r, {u, v})]", //
        "Tree(r,{Tree(p,None),Tree(v,None)})");
  }

  @Test
  public void testTreeInsert() {
    check("TreeInsert(Tree(r, {u, v}), n, 2)", //
        "Tree(r,{Tree(u,None),Tree(n,None),Tree(v,None)})");
    check("TreeInsert(Tree(r, {u, v}), n, -1)", //
        "Tree(r,{Tree(u,None),Tree(v,None),Tree(n,None)})");
    check("TreeInsert(Tree(r, {u, Tree(v, {w})}), n, {2, 1})", //
        "Tree(r,{Tree(u,None),Tree(v,{Tree(n,None),Tree(w,None)})})");
    check("TreeInsert(Tree(r, {u}), n, 7)", //
        "TreeInsert(Tree(r,{Tree(u,None)}),n,7)");
  }

  @Test
  public void testTreeDelete() {
    check("TreeDelete(Tree(r, {u, v, w}), 2)", //
        "Tree(r,{Tree(u,None),Tree(w,None)})");
    check("TreeDelete(Tree(r, {u, Tree(v, {w})}), {2, 1})", //
        "Tree(r,{Tree(u,None),Tree(v,{})})");
    check("TreeDelete(Tree(r, {u}), {})", //
        "TreeDelete(Tree(r,{Tree(u,None)}),{})");
  }

  @Test
  public void testExpressionTree() {
    check("ExpressionTree(f(x, g(y, 2)))", //
        "Tree(f,{Tree(x,None),Tree(g,{Tree(y,None),Tree(2,None)})})");
    check("ExpressionTree(a + b*c)", //
        "Tree(Plus,{Tree(a,None),Tree(Times,{Tree(b,None),Tree(c,None)})})");
    check("ExpressionTree(f(x, g(y)), \"Subexpressions\")", //
        "Tree(f(x,g(y)),{Tree(x,None),Tree(g(y),{Tree(y,None)})})");
    check("ExpressionTree(f(x, g(y)), \"Atoms\")", //
        "Tree(Null,{Tree(x,None),Tree(Null,{Tree(y,None)})})");
    check("ExpressionTree(f(x)[y], \"HeadTrees\")", //
        "Tree(Tree(f,{Tree(x,None)}),{Tree(y,None)})");
    check("ExpressionTree(f(x), \"Branches\")", //
        "ExpressionTree(f(x),Branches)");
    check("ExpressionTree(7)", //
        "Tree(7,None)");
  }

  @Test
  public void testTreeExpression() {
    check("TreeExpression(ExpressionTree(f(x, g(y, 2))))", //
        "f(x,g(y,2))");
    check("TreeExpression(Tree(Plus, {1, 2, Tree(Times, {3, 4})}))", //
        "15");
    check("TreeExpression(Tree(u, None))", //
        "u");
    check("TreeExpression(ExpressionTree(f(x)[y], \"HeadTrees\"))", //
        "f(x)[y]");
  }
}
