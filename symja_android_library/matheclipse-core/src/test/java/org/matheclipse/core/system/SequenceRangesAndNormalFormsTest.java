package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * The ranges of Take, Drop and TakeDrop, positions of Extract which are keys, Pick on
 * associations, the Heads option of Replace, the levels of Reverse, Commonest with UpTo, exact
 * totals of deep lists, SubsetMap, Majority, the normal forms of BooleanConvert, the order of
 * negations and a difference of lists of different lengths.
 */
public class SequenceRangesAndNormalFormsTest extends ExprEvaluatorTestCase {

  @Test
  public void testTakeEmptyRanges() {
    // the end lies one position before the start, seen in the direction of the step
    check("{Take({p,q,r,s,t}, {3,2}), Take({p,q,r,s,t}, {6,5}), Take({p,q,r,s,t}, {3,2,3}), "
        + "Take({p,q,r,s,t}, {2,3,-3}), Take({p,q,r,s,t}, {1,0,5})}", //
        "{{},{},{},{},{}}");
    check("{Take(<|u->1,v->2,w->3|>, {2,1}), Take({{1,2,3},{4,5,6}}, All, {2,1}), "
        + "Take({p,q}, 2;;1), Take({p,q}, 3;;)}", //
        "{<||>,{{},{}},{},{}}");
    check("Normal(Take(SparseArray({0,7,0,8,9}), {3,2}))", //
        "{}");
  }

  @Test
  public void testTakeRangesWhichDoNotFit() {
    check("{Take({p,q,r,s,t}, {4,2}), Take({p,q,r,s,t}, {4,1,3}), Take({p,q,r,s,t}, {2,5,-1})}", //
        "{Take({p,q,r,s,t},{4,2}),Take({p,q,r,s,t},{4,1,3}),Take({p,q,r,s,t},{2,5,-1})}");
    check("{Take({p,q,r,s,t}, {0,2}), Take({p,q,r,s,t}, {2,6}), Take({p,q,r,s,t}, {7,6}), "
        + "Take({p,q,r,s,t}, {1,3,0})}", //
        "{Take({p,q,r,s,t},{0,2}),Take({p,q,r,s,t},{2,6}),Take({p,q,r,s,t},{7,6}),Take({p,q,r,s,t},{\n"
            + "1,3,0})}");
    // a span stays as it was written
    check("Take({{p}}, 1;;-3)", //
        "Take({{p}},1;;-3)");
    check("Check(Take({p,q,r}, {3,1}), bad)", //
        "bad");
  }

  @Test
  public void testTakeWithSteps() {
    check("{Take({p,q,r,s,t}, {5,1,-2}), Take({p,q,r,s,t}, {-1,-4,-3}), "
        + "Take({p,q,r,s,t}, {2,-1,2}), Take(<|u->1,v->2,w->3|>, {3,1,-2})}", //
        "{{t,r,p},{t,q},{q,s},<|w->3,u->1|>}");
    check("Normal(Take(SparseArray({0,7,0,8,9}), {5,1,-2}))", //
        "{9,0,0}");
  }

  @Test
  public void testDropRanges() {
    // an empty range drops nothing
    check("{Drop({p,q,r,s,t}, {3,2}), Drop({p,q,r,s,t}, {6,5}), "
        + "Drop({{1,2,3},{4,5,6}}, None, {2,1})}", //
        "{{p,q,r,s,t},{p,q,r,s,t},{{1,2,3},{4,5,6}}}");
    check("{Drop({p,q,r,s,t}, {5,1,-2}), Drop({p,q,r,s,t}, {2,-1,2}), "
        + "Drop(<|u->1,v->2,w->3|>, {3,1,-2}), Drop({{1,2,3},{4,5,6}}, {1}, {3,1,-2})}", //
        "{{q,s},{p,r,t},<|v->2|>,{{5}}}");
    check("{Drop({p,q,r,s,t}, {4,2}), Drop({p,q,r,s,t}, {7,3}), Drop({p,q,r,s,t}, {3,0,-1})}", //
        "{Drop({p,q,r,s,t},{4,2}),Drop({p,q,r,s,t},{7,3}),Drop({p,q,r,s,t},{3,0,-1})}");
    check("{Drop({p,q,r,s,t}, {0,3}), Drop({p,q,r,s,t}, {2,8}), Drop({p,q}, 1;;4)}", //
        "{Drop({p,q,r,s,t},{0,3}),Drop({p,q,r,s,t},{2,8}),Drop({p,q},1;;4)}");
    check("Check(Drop({p,q,r}, {2,7}), bad)", //
        "bad");
  }

  @Test
  public void testTakeDrop() {
    check("{TakeDrop({p,q,r}, 3), TakeDrop({p,q,r}, {2,1}), TakeDrop({p,q,r}, -1), "
        + "TakeDrop(<|u->1,v->2|>, 1)}", //
        "{{{p,q,r},{}},{{},{p,q,r}},{{r},{p,q}},{<|u->1|>,<|v->2|>}}");
    // a specification which doesn't fit is reported for TakeDrop itself
    check("Check(TakeDrop({p,q,r}, 4), bad, TakeDrop::iseq)", //
        "bad");
    check("TakeDrop({p,q,r}, {3,1})", //
        "TakeDrop({p,q,r},{3,1})");
  }

  @Test
  public void testExtractKeysOfAssociations() {
    check("k = <|\"u\" :> 2 + 3, \"v\" -> 7, w -> {4, 1 + 1}|>;", //
        "");
    // the value of a delayed rule is wrapped before it is evaluated
    check("{Extract(k, Key(\"u\"), Hold), Extract(k, \"u\"), Extract(k, \"v\", f), "
        + "Extract(k, Key(w))}", //
        "{Hold(2+3),5,f(7),{4,2}}");
    check("{Extract(k, {Key(w), 2}), Extract(k, {{\"u\"}, {\"v\"}}, Hold), "
        + "Extract({k, 9}, {1, \"u\"}, Hold), Extract(k, \"none\")}", //
        "{2,{Hold(2+3),Hold(7)},Hold(2+3),Missing(KeyAbsent,none)}");
    check("m = <|\"u\" :> {2 + 3, 5*6}|>;", //
        "");
    check("{Extract(m, {\"u\", 2}, Hold), Extract(m, {\"u\", 2}), Extract(m, {\"u\", 1}, Hold)}", //
        "{Hold(5*6),30,Hold(2+3)}");
  }

  @Test
  public void testExtractSinglePositions() {
    check("{Extract({p, q, r}, 2;;3), Extract({p, q, r}, All), Extract(g(p, q), 0), "
        + "Extract({p, q, r}, {})}", //
        "{{q,r},{p,q,r},g,{}}");
    check("{Check(Extract({p, q, r}, w), bad), Check(Extract({p, q, r}, \"u\"), bad), "
        + "Check(Extract({p, q, r}, 4), bad)}", //
        "{bad,bad,bad}");
  }

  @Test
  public void testPickFromAssociations() {
    check("Pick(<|u -> 5, v -> 6, w -> 7|>, {0, 1, 1}, 1)", //
        "<|v->6,w->7|>");
    check("Pick(<|u -> {5, 6}, v -> 7|>, {{1, 0}, 0}, 1)", //
        "<|u->{5}|>");
    check("Pick(<|u -> 5, v :> 6 + 1|>, {True, True})", //
        "<|u->5,v:>6+1|>");
    // the values of an association select
    check("{Pick({5, 6}, <|u -> True, v -> False|>), "
        + "Pick(<|u -> 5, v -> 6|>, <|p -> False, q -> True|>), Pick(<|u -> 5|>, {0}, 1)}", //
        "{{5},<|v->6|>,<||>}");
    check("Check(Pick(<|u -> 5, v -> 6|>, {1}, 1), bad)", //
        "bad");
    check("{Pick({5, {6, 7}}, {1, {0, 1}}, 1), Pick(g(5, 6, 7), h(0, 1, 1), 1)}", //
        "{{5,{7}},g(6,7)}");
  }

  @Test
  public void testReplaceHeadsOption() {
    // without a level the whole expression is the only candidate
    check("{Replace(g(7), g -> h, Heads -> True), Replace({7}, 7 -> 8, Heads -> True), "
        + "Replace(g(7), g(u_) :> u + 1, Heads -> True)}", //
        "{g(7),{7},8}");
    check("{Replace(g(7), g -> h, {1}, Heads -> True), Replace({7}, 7 -> 8, {1}, Heads -> True)}", //
        "{h(7),{8}}");
    check("{Replace(g(g(7))[g], g -> h, {0, 2}, Heads -> True), "
        + "Replace(g(g(7))[g], g -> h, {0, 2}, Heads -> False), Replace(g(g(7))[g], g -> h, {0, 2})}", //
        "{h(g(7))[h],g(g(7))[h],g(g(7))[h]}");
    check("Replace(g -> h)[g]", //
        "h");
    check("Options(Replace)", //
        "{Heads->False}");
    check("Check(Replace({7}, 7 -> 8, w), bad)", //
        "bad");
  }

  @Test
  public void testReverseLevels() {
    check("{Reverse({{1, 2, 3}, {4, 5, 6}}, 2), Reverse({{1, 2, 3}, {4, 5, 6}}, {1, 2}), "
        + "Reverse({{1, 2, 3}, {4, 5, 6}}, {})}", //
        "{{{3,2,1},{6,5,4}},{{6,5,4},{3,2,1}},{{1,2,3},{4,5,6}}}");
    check("{Reverse({1, 2, 3}, 4), Reverse(g(1, 2, 3), 1)}", //
        "{{1,2,3},g(3,2,1)}");
    check("{Check(Reverse({1, 2, 3}, w), bad, Reverse::ilsmp), "
        + "Check(Reverse({1, 2, 3}, 0), bad, Reverse::ilsmp), "
        + "Check(Reverse({1, 2, 3}, {1, -1}), bad, Reverse::ilsmp), "
        + "Check(Reverse({1, 2, 3}, 2.0), bad, Reverse::ilsmp), "
        + "Check(Reverse({1, 2, 3}, Infinity), bad, Reverse::ilsmp)}", //
        "{bad,bad,bad,bad,bad}");
    check("Reverse({1, 2, 3}, w)", //
        "Reverse({1,2,3},w)");
    check("Check(Reverse(7), bad)", //
        "bad");
  }

  @Test
  public void testCommonestUpTo() {
    check("{Commonest({p, q, q, r, r, s}, UpTo(5)), Commonest({p, q, q, r, r, s}, UpTo(1)), "
        + "Commonest({p, q, q}, UpTo(0))}", //
        "{{q,r,p,s},{q},{}}");
    // fewer distinct elements than UpTo allows are no reason for a message
    check("{Check(Commonest({p, q, q}, UpTo(7)), bad), "
        + "Check(Commonest({p, q, q}, 7), bad, Commonest::dstlms)}", //
        "{{q,p},bad}");
  }

  @Test
  public void testTotalOfDeepLists() {
    check("{Total({{1, 2}, {3, 1/2}}, 2), Total({{{1, 2}}, {{3, 4}}}, 2), "
        + "Total({{1, u}, {3, 1/2}}, 2), Total({{1, 2.5}, {3, 1/2}}, 2)}", //
        "{13/2,{4,6},9/2+u,7.0}");
    check("{Total(<|u -> {1, 2}, v -> {3}|>, 2), Total({{}, {}}, 2), "
        + "Total({{1, {2, 3}}, {4, 5}}, Infinity)}", //
        "{6,0,15}");
    check("Total(ConstantArray({2, 0, 1/3}, {40, 50}), 3)", //
        "14000/3");
  }

  @Test
  public void testPartAssignmentKeepsEqualRowsApart() {
    check("u = ConstantArray({1, 0}, {3, 2}); u[[2, 1, 2]] = 5; v = u; v[[1, 1]] = {8, 8}; {u, v}", //
        "{{{{1,0},{1,0}},{{1,5},{1,0}},{{1,0},{1,0}}},{{{8,8},{1,0}},{{1,5},{1,0}},{{1,0},{\n"
            + "1,0}}}}");
    check("u = {<|p -> 1|>, <|p -> 2|>}; AssociateTo(u[[2]], q -> 3); u", //
        "{<|p->1|>,<|p->2,q->3|>}");
  }

  @Test
  public void testNormalFormsMergeTerms() {
    // two terms which differ in the sign of one literal are one term without it
    check("{BooleanConvert((p && q) || (p && !q) || (!p && q)), "
        + "BooleanConvert((p && q && r) || (p && q && !r)), "
        + "BooleanConvert((p && q) || (!p && q && r))}", //
        "{p||q,p&&q,(p&&q)||(q&&r)}");
    // a literal strikes its negation from the other terms
    check("{BooleanConvert(p || (!p && q && r)), BooleanConvert((p || q) && !p, \"CNF\"), "
        + "BooleanConvert((p || q || r) && (!p || q), \"CNF\")}", //
        "{p||(q&&r),!p&&q,(!p||q)&&(q||r)}");
    check("{BooleanConvert((p && q) || (p && !q) || (!p && q) || (!p && !q)), "
        + "BooleanConvert((p || q) && (p || !q) && (!p || q) && (!p || !q), \"CNF\")}", //
        "{True,False}");
    // terms which cannot be merged stay
    check("{BooleanConvert((p && q) || (!p && r)), BooleanConvert(p && !q || !p && r || q && !r)}", //
        "{(p&&q)||(!p&&r),(p&&!q)||(!p&&r)||(q&&!r)}");
    check("BooleanConvert((p && !q && !r) || (!p && q && !r) || (!p && !q && r), \"CNF\")", //
        "(!p||!q)&&(!p||!r)&&(p||q||r)&&(!q||!r)");
  }

  @Test
  public void testConjunctiveNormalFormOrder() {
    // the clauses are compared literal by literal, a negated literal first
    check("{BooleanConvert(Xor(p, q), \"CNF\"), BooleanConvert(Equivalent(p, q), \"CNF\"), "
        + "BooleanConvert((r && p) || Xor(p, q), \"CNF\"), BooleanConvert(Xor(p, q), \"POS\")}", //
        "{(!p||!q)&&(p||q),(!p||q)&&(p||!q),(!p||!q||r)&&(p||q),(!p||!q)&&(p||q)}");
    check("{BooleanConvert(Xor(p, q)), BooleanConvert(Xor(p, q), \"SOP\"), "
        + "BooleanConvert(u > 1 || Implies(p, q))}", //
        "{(p&&!q)||(!p&&q),(p&&!q)||(!p&&q),!p||q||u>1}");
  }

  @Test
  public void testSingleConnectiveForms() {
    check("{BooleanConvert(Xor(p, q), \"NAND\"), BooleanConvert((p || q) && r, \"NAND\"), "
        + "BooleanConvert(p || q, \"NAND\"), BooleanConvert(Implies(p, q), \"NAND\")}", //
        "{(p\u22BC!q)\u22BC(!p\u22BCq),(p\u22BCr)\u22BC(q\u22BCr),!p\u22BC!q,p\u22BC!q}");
    check("{BooleanConvert(p && q, \"NOR\"), BooleanConvert((p || q) && r, \"NOR\"), "
        + "BooleanConvert(Xor(p, q), \"NOR\"), BooleanConvert((p || p || q) && !p, \"NOR\")}", //
        "{!p\u22BD!q,(p\u22BDq)\u22BD!r,(!p\u22BD!q)\u22BD(p\u22BDq),p\u22BD!q}");
    // a single term is negated twice
    check("{BooleanConvert(p && q, \"NAND\"), BooleanConvert(p || q, \"NOR\")}", //
        "{!(p\u22BCq),!(p\u22BDq)}");
    check("{BooleanConvert(p || q, \"AND\"), BooleanConvert((p || q) && r, \"AND\"), "
        + "BooleanConvert(p && q, \"AND\"), BooleanConvert(u > 1 || Implies(p, q), \"AND\")}", //
        "{!(!p&&!q),!(!p&&!q)&&r,p&&q,!(p&&!q&&u<=1)}");
    check("{BooleanConvert(p && q, \"OR\"), BooleanConvert((p || q) && r, \"OR\"), "
        + "BooleanConvert(p || q, \"OR\")}", //
        "{!(!p||!q),!(!p||!r)||!(!q||!r),p||q}");
    // a literal or a constant needs no connective
    check("{BooleanConvert(!p, \"NAND\"), BooleanConvert(p, \"NOR\"), BooleanConvert(p, \"AND\"), "
        + "BooleanConvert(p && !p, \"NOR\"), BooleanConvert(p || !p, \"NAND\"), "
        + "BooleanConvert(p || !p, \"AND\")}", //
        "{!p,p,p,False,True,True}");
    check("BooleanConvert(p && q, \"Foo\")", //
        "BooleanConvert(p&&q,Foo)");
  }

  @Test
  public void testMajority() {
    check("Attributes(Majority)", //
        "{Orderless,Protected}");
    check("{Majority(True, True, False, False, True), Majority(False, True), Majority(), "
        + "Majority(True, True, False, u)}", //
        "{True,False,False,u}");
    // a True and a False cancel each other
    check("{Majority(True, False, u), Majority(True, False, u, w), Majority(True, u)}", //
        "{u,u&&w,u}");
    check("{Majority(w, u, v), Majority(u, u, False, w, v)}", //
        "{Majority(u,v,w),Majority(False,u,u,v,w)}");
    check("{SatisfiabilityCount(Majority(p, q, r, s, t)), "
        + "BooleanConvert(Majority(p, q, r)), BooleanConvert(Majority(p, q, r), \"CNF\")}", //
        "{16,(p&&q)||(p&&r)||(q&&r),(p||q)&&(p||r)&&(q||r)}");
    check("TautologyQ(Equivalent(Majority(p, q, r, s), "
        + "BooleanConvert(Majority(p, q, r, s), \"NOR\")))", //
        "True");
  }

  @Test
  public void testSubsetMap() {
    check("Attributes(SubsetMap)", //
        "{Protected,ReadProtected}");
    check("{SubsetMap(Reverse, {p, q, r, s, t}, {1, 4}), SubsetMap(RotateRight, {p, q, r, s, t}, {1, 2, 5}), "
        + "SubsetMap(Accumulate, {2, 4, 6, 8}, 2;;3)}", //
        "{{s,q,r,p,t},{t,p,r,s,q},{2,4,10,8}}");
    // positions as lists, and one specification for each level
    check("{SubsetMap(Reverse, {{1, 2}, {3, 4}}, {{1, 2}, {2, 1}}), "
        + "SubsetMap(Reverse, {{1, 2}, {3, 4}, {5, 6}}, {All, 1}), "
        + "SubsetMap(Reverse, {p, q, r, s, t}, -1;;1;;-2)}", //
        "{{{1,3},{2,4}},{{5,2},{3,4},{1,6}},{t,q,r,s,p}}");
    check("{SubsetMap(Reverse, {2, 3}) @ {p, q, r, s}, SubsetMap(Reverse, {2, 3})}", //
        "{{p,r,q,s},SubsetMap(Reverse,{2,3})}");
    // the function has to keep the length
    check("Check(SubsetMap(Total, {1, 2, 3}, {1, 2}), bad, SubsetMap::newls)", //
        "bad");
    check("SubsetMap(g, {1, 2, 3}, {{1}, {3}})", //
        "SubsetMap(g,{1,2,3},{{1},{3}})");
    // the positions are taken with Part
    check("{Check(SubsetMap(Reverse, {p}, {1, 4}), bad), Check(SubsetMap(Reverse, {}, 1;;3), bad)}", //
        "{bad,bad}");
  }

  @Test
  public void testNegationsKeepTheirPartner() {
    // a variable and its negation are not reduced by And and Or
    check("{u && !u, u || !u, And(w, u, True, !u), Or(u, False, w, !u)}", //
        "{u&&!u,u||!u,w&&u&&!u,u||w||!u}");
    check("{Simplify(u && !u), Simplify(u || !u), BooleanConvert(u && !u), TautologyQ(u || !u)}", //
        "{False,True,False,True}");
    // a negation sorts after compound expressions, not next to its variable
    check("Sort({!u, f(u), w, u && w, Implies(u, w), u})", //
        "{u,w,u&&w,f(u),u\u21D2w,!u}");
    check("LogicalExpand((!u || 2) || Equivalent(u, w))", //
        "2||w||!u");
    // the parts of a term in canonical order: a negation after the symbols
    check("LogicalExpand(Xor(p, q))", //
        "(p&&!q)||(q&&!p)");
  }

  @Test
  public void testSubtractOfListsOfDifferentLengths() {
    check("{Quiet(Subtract({5, 6}, {u, w, v})), Quiet(Subtract({5, {6, 7}}, {u, {w}})), "
        + "Subtract(4, {u, w}), Subtract({5, 6}, {u, w})}", //
        "{{5,6}-{u,w,v},{5-u,{6,7}-{w}},{4-u,4-w},{5-u,6-w}}");
    check("Quiet(Subtract({5, 6}, {u, w, v})) // FullForm", //
        "Subtract(List(5, 6), List(u, w, v))");
  }

  @Test
  public void testMessageTexts() {
    check("Check({w}[[{1, 3}]], bad)", //
        "bad");
    check("{w}[[{1, 3}]]", //
        "{w}[[{1,3}]]");
  }

  @Test
  public void testLogicalExpandTermOrder() {
    // the parts of each term in canonical order: the negations after the symbols
    check("LogicalExpand(Xor(u, v, w))", //
        "(u&&v&&w)||(u&&!v&&!w)||(v&&!u&&!w)||(w&&!u&&!v)");
  }
}
