package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** Construct, OperatorApplied, CurryApplied, Comap, AllMatch, AnyMatch, FoldWhileList, SequenceFold, LexicographicOrder, DeleteElements, UniqueElements, OrderingBy, BlockMap, Downsample and MovingMap. */
public class ListOperatorFunctionsTest extends ExprEvaluatorTestCase {

  @Test
  public void testConstruct() {
    check("Construct(g, 4, y)", //
        "g(4,y)");
    check("Construct(Plus)", //
        "0");
    check("Construct(Times, 3, 5)", //
        "15");
  }

  @Test
  public void testOperatorApplied() {
    check("OperatorApplied(g)[u][v]", //
        "g(v,u)");
    check("OperatorApplied(Subtract)[3][20]", //
        "17");
    check("OperatorApplied(g, 3)[p][q][r]", //
        "g(p,q,r)");
    check("OperatorApplied(g, {3, 1, 2})[p][q][r]", //
        "g(r,p,q)");
    check("OperatorApplied(g)[u]", //
        "OperatorApplied(g)[u]");
  }

  @Test
  public void testCurryApplied() {
    check("CurryApplied(g, 2)[p][q]", //
        "g(p,q)");
    check("CurryApplied(Times, 3)[2][5][7]", //
        "70");
    check("CurryApplied(g)[p]", //
        "CurryApplied(g)[p]");
    check("CurryApplied(2)[g][p][q]", //
        "g(p,q)");
  }

  @Test
  public void testComap() {
    check("Comap({Sin, Cos, k}, t)", //
        "{Sin(t),Cos(t),k(t)}");
    check("Comap({Min, Max, Length}, {7, 2, 9})", //
        "{2,9,3}");
    check("Comap(<|\"lo\" -> Min, \"hi\" -> Max|>, {4, 8, 1})", //
        "<|lo->1,hi->8|>");
    check("Comap(w(f1, f2), z)", //
        "w(f1(z),f2(z))");
    check("Comap(f1, z)", //
        "f1");
    check("Comap({Abs, Sign})[-6]", //
        "{6,-1}");
    check("Comap({}, z)", //
        "{}");
  }

  @Test
  public void testAllMatch() {
    check("AllMatch({2, 4, 6}, _?EvenQ)", //
        "True");
    check("AllMatch({2, 5, 6}, _?EvenQ)", //
        "False");
    check("AllMatch({}, _String)", //
        "True");
    check("AllMatch(q, _Integer)", //
        "True");
    check("AllMatch({{1, 2}, {3, x}}, _Integer, {2})", //
        "False");
    check("AllMatch({{1, 2}, {3, 4}}, _Integer, {2})", //
        "True");
    check("AllMatch(_Real)[{1.5, 2.25}]", //
        "True");
    check("AllMatch(<|1 -> a, 2 -> b|>, _Symbol)", //
        "True");
  }

  @Test
  public void testAnyMatch() {
    check("AnyMatch({a, \"s\", 3}, _String)", //
        "True");
    check("AnyMatch({a, b}, _String)", //
        "False");
    check("AnyMatch({}, _)", //
        "False");
    check("AnyMatch(_Integer)[{x, 7}]", //
        "True");
  }

  @Test
  public void testFoldWhileList() {
    check("FoldWhileList(Plus, 1, {2, 3, 4, 5, 6}, # < 8 &)", //
        "{1,3,6,10}");
    check("FoldWhileList(Times, {2, 3, 4, 5}, # < 30 &)", //
        "{2,6,24,120}");
    check("FoldWhileList(Plus, 0, {5, 5, 5, 5}, # < 7 &, 1, 1)", //
        "{0,5,10,15}");
    check("FoldWhileList(Plus, 0, {5, 5, 5, 5}, # < 12 &, 1, -1)", //
        "{0,5,10}");
    check("FoldWhileList(Plus, # < 7 &)[{3, 3, 3, 3}]", //
        "{3,6,9}");
    check("FoldWhileList(Plus, 0, {}, True &)", //
        "{0}");
  }

  @Test
  public void testSequenceFoldList() {
    check("SequenceFoldList(Plus, {1, 1}, {0, 0, 0, 0})", //
        "{1,1,2,3,5,8}");
    check("SequenceFoldList(g, {s}, {a, b, c})", //
        "{s,g(s,a),g(g(s,a),b),g(g(g(s,a),b),c)}");
    check("SequenceFoldList(g, {s, t}, {a})", //
        "{s,t,g(s,t,a)}");
  }

  @Test
  public void testSequenceFold() {
    check("SequenceFold(Plus, {1, 1}, {0, 0, 0, 0})", //
        "8");
    check("SequenceFold(g, {s}, 3)", //
        "SequenceFold(g,{s},3)");
  }

  @Test
  public void testLexicographicOrder() {
    check("LexicographicOrder({1, 4}, {1, 5})", //
        "1");
    check("LexicographicOrder({3}, {2, 9, 9})", //
        "-1");
    check("LexicographicOrder({x, y}, {x, y})", //
        "0");
    check("LexicographicOrder({5, 5}, {5, 5, 1})", //
        "1");
    check("LexicographicOrder({5, 5, 1}, {5, 5})", //
        "-1");
    check("LexicographicOrder(\"cat\", \"car\")", //
        "-1");
    check("LexicographicOrder({1, 4}, {1, 5}, Greater)", //
        "-1");
  }

  @Test
  public void testDeleteElements() {
    check("DeleteElements({3, 1, 3, 2, 1}, {3})", //
        "{1,2,1}");
    check("DeleteElements({3, 1, 3, 2, 1}, {1, 2, 9})", //
        "{3,3}");
    check("DeleteElements({3, 1, 3, 2, 1}, 1 -> {3, 1})", //
        "{3,2,1}");
    check("DeleteElements({3, 3, 3, 3}, Infinity -> {3})", //
        "{}");
    check("DeleteElements({3, 1, 3, 2, 1}, {1, 2} -> {3, 1})", //
        "{3,2}");
    check("DeleteElements({2.0, 2}, {2})", //
        "{2.0}");
    check("DeleteElements(h(x, y, x), {x})", //
        "h(y)");
    check("DeleteElements({4})[{4, 5, 4}]", //
        "{5}");
    check("DeleteElements({1, 2}, 3)", //
        "DeleteElements({1,2},Infinity->3)");
  }

  @Test
  public void testUniqueElements() {
    check("UniqueElements({{1, 3, 3, c, d}, {3, 5, d}})", //
        "{{1,c},{5}}");
    check("UniqueElements({{7, 8, 9}, {8, 9, 10}, {9, 10, 11}})", //
        "{{7},{},{11}}");
    check("UniqueElements({{a, b}, {a, b}})", //
        "{{},{}}");
    check("UniqueElements({g(1, 2), g(2, 4)})", //
        "{g(1),g(4)}");
    check("UniqueElements({{1, 5}, {7, 3}}, Mod(#1, 2) == Mod(#2, 2) &)", //
        "{{},{}}");
    check("UniqueElements(7)", //
        "UniqueElements(7)");
  }

  @Test
  public void testOrderingBy() {
    check("OrderingBy({4, -9, 2}, Abs)", //
        "{3,1,2}");
    check("OrderingBy({{2, z}, {1, y}, {3, x}}, Last)", //
        "{3,2,1}");
    check("OrderingBy({4, -9, 2}, Abs, 2)", //
        "{3,1}");
    check("OrderingBy({4, -9, 2}, Abs, -1)", //
        "{2}");
    check("OrderingBy({4, -9, 2}, Abs, All, Greater)", //
        "{2,1,3}");
    check("OrderingBy(StringLength)[{\"three\", \"is\", \"fun\"}]", //
        "{2,3,1}");
  }

  @Test
  public void testBlockMap() {
    check("BlockMap(g, {1, 2, 3, 4, 5}, 2)", //
        "{g({1,2}),g({3,4})}");
    check("BlockMap(Total, Range(9), {3})", //
        "{6,15,24}");
    check("BlockMap(g, {1, 2, 3, 4, 5}, 3, 1)", //
        "{g({1,2,3}),g({2,3,4}),g({3,4,5})}");
  }

  @Test
  public void testDownsample() {
    check("Downsample({p, q, r, s, t, u, v}, 3)", //
        "{p,s,v}");
    check("Downsample({p, q, r, s, t, u, v}, 2, 2)", //
        "{q,s,u}");
    check("Downsample({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}, {2, 2})", //
        "{{1,3},{7,9}}");
    check("Downsample({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}, 2)", //
        "{{1,3},{7,9}}");
  }

  @Test
  public void testMovingMap() {
    check("MovingMap(Total, {2, 4, 6, 8}, 1)", //
        "{6,10,14}");
    check("MovingMap(g, {2, 4, 6, 8}, {2})", //
        "{g({2,4,6}),g({4,6,8})}");
    check("MovingMap(Max, {2, 4, 6, 8}, 0)", //
        "{2,4,6,8}");
    check("MovingMap(g, {a, b, c, d}, 1, \"Fixed\")", //
        "{g({a,a}),g({a,b}),g({b,c}),g({c,d})}");
    check("MovingMap(g, {a, b, c, d}, 2, \"Periodic\")", //
        "{g({c,d,a}),g({d,a,b}),g({a,b,c}),g({b,c,d})}");
    check("MovingMap(g, {a, b, c, d}, 2, \"Reflected\")", //
        "{g({c,b,a}),g({b,a,b}),g({a,b,c}),g({b,c,d})}");
    check("MovingMap(g, {a, b, c}, 1, None)", //
        "{g({a}),g({a,b}),g({b,c})}");
    check("MovingMap(g, {a, b, c}, 2, 0)", //
        "{g({0,0,a}),g({0,a,b}),g({a,b,c})}");
    check("MovingMap(g, {a, b}, 5, \"Periodic\")", //
        "{g({b,a,b,a,b,a}),g({a,b,a,b,a,b})}");
  }
}
