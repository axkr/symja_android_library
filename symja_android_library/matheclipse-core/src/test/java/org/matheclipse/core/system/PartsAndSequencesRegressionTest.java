package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Rules with sequence patterns, positions past the start of an expression, and the argument checks
 * of RealDigits, Outer, Options, Table, Do, Random and ListPlot.
 */
public class PartsAndSequencesRegressionTest extends ExprEvaluatorTestCase {

  @Test
  public void testRulesWithSequencesStaySeparate() {
    check("r1(u___) := many; r1(u__) := some; {r1(), r1(7), r1(7, 8)}", //
        "{many,some,some}");
    check("r2(u__) := some; r2(u___) := many; {r2(), r2(7)}", //
        "{many,some}");
    check("r3(___) := many; r3(__) := some; {r3(), r3(7)}", //
        "{many,some}");
    check("r4({u__}) := some; r4({u___}) := many; {r4({}), r4({7})}", //
        "{many,some}");
    check("r5(u__Integer) := some; r5(u___Integer) := many; {r5(), r5(7), r5(k)}", //
        "{many,some,r5(k)}");
    check("r6(u__?EvenQ) := some; r6(u___?EvenQ) := many; {r6(), r6(4)}", //
        "{many,some}");
    check("r7(k..) := some; r7(k...) := many; {r7(), r7(k, k)}", //
        "{many,some}");
    check("r11(Longest(u__),v___) := {u}; r11(u__,v___) := {v}; Length(DownValues(r11))", //
        "2");
    // a rule with the same left-hand side still replaces the old one
    check("r12(k..) := 1; r12(k..) := 2; Length(DownValues(r12))", //
        "1");
    check("r13(u_Integer..) := 1; r13(u_Integer..) := 2; Length(DownValues(r13))", //
        "1");
    check("r14(u_, OptionsPattern()) := 1; r14(v_, OptionsPattern()) := 2; "
        + "Length(DownValues(r14))", //
        "1");
    check("r15(Longest(u__),v___) := 1; r15(Longest(w__),m___) := 2; Length(DownValues(r15))", //
        "1");
    check("r8(u__) := 1; r8(v__) := 2; {r8(7), Length(DownValues(r8))}", //
        "{2,1}");
    check("r9(u___) := 1; r9(u___) := 2; Length(DownValues(r9))", //
        "1");
    // x_ and x__ were always kept apart
    check("r10(u_) := one; r10(u__) := some; {r10(7), r10(7, 8)}", //
        "{one,some}");
  }

  @Test
  public void testNegativePositionNeverReachesTheHead() {
    check("{5,6,7}[[-4]]", //
        "{5,6,7}[[-4]]");
    check("{5,6,7}[[-3]]", //
        "5");
    check("{5,6,7}[[0]]", //
        "List");
    check("{5,6,7}[[{-4,1}]]", //
        "{5,6,7}[[{-4,1}]]");
    check("g(5)[[-2]]", //
        "g(5)[[-2]]");
    check("{{5,6}}[[1,-3]]", //
        "{{5,6}}[[1,-3]]");
    check("<|k->5|>[[-1]]", //
        "5");
    check("Head(<|k->5|>[[-2]])", //
        "Part");
    check("Check(SparseArray({5,6})[[-3]], gone)", //
        "gone");
    check("SparseArray({5,6})[[-1]]", //
        "6");
    check("Check(NumericArray({5,6},\"Integer32\")[[-3]], gone)", //
        "gone");
    check("Delete({5,6,7},-4)", //
        "Delete({5,6,7},-4)");
    check("Delete({5,6,7},-3)", //
        "{6,7}");
    check("MapAt(g,{5,6,7},-3)", //
        "{g(5),6,7}");
    // MapAt and ReplacePart count the head in, Part and Delete don't
    check("MapAt(g,{5,6,7},-4)", //
        "g(List)[5,6,7]");
    check("MapAt(g,{5,6,7},-5)", //
        "MapAt(g,{5,6,7},-5)");
    check("ReplacePart({5,6,7},-4->k)", //
        "k(5,6,7)");
    check("ReplacePart({5,6,7},-5->k)", //
        "{5,6,7}");
    check("ReplacePart({5,6,7},-3->k)", //
        "{k,6,7}");
    check("ReplacePart({{5,6},7},{1,-3}->k)", //
        "{k(5,6),7}");
    check("ReplacePart({5,6,7},{{-4},{1}}->k)", //
        "k(k,6,7)");
    // a pattern for a position doesn't reach the head because of a negative position
    check("ReplacePart({5,6,7},{-1->k,_->m})", //
        "{m,m,k}");
    check("ReplacePart({5,6,7},{-1->k,j_?EvenQ->m})", //
        "{5,m,k}");
    check("ReplacePart({5,6,7},-4->k,Heads->False)", //
        "{5,6,7}");
    check("ReplacePart(<|c->5|>,-2->k)", //
        "k(5)");
    check("ReplacePart(<|c->5|>,-1->k)", //
        "<|c->k|>");
    check("Take({5,6,7},-4)", //
        "Take({5,6,7},-4)");
    check("Take({5,6,7},{-4})", //
        "Take({5,6,7},{-4})");
    check("Take({5,6,7},-3)", //
        "{5,6,7}");
    check("Check(Take(<|c->5|>,-2), bad)", //
        "bad");
    check("Take(<|c->5|>,-1)", //
        "<|c->5|>");
    check("Check(Take({5,6,7},{-4,2}), bad)", //
        "bad");
  }

  @Test
  public void testPartAssignmentChecksThePositions() {
    check("v1={5,6,7}; v1[[-4]]=0; v1", //
        "{5,6,7}");
    check("v2={5,6,7}; v2[[{-4,2}]]={k,m}; v2", //
        "{5,6,7}");
    check("v3={5,6,7}; v3[[-4]]+=1; v3", //
        "{5,6,7}");
    check("v4={{5,6},{7,8}}; v4[[All,-3]]=0; v4", //
        "{{5,6},{7,8}}");
    check("v5=g(5,6); v5[[-3]]=0; v5", //
        "g(5,6)");
    check("v6={5,6,7}; v6[[-3]]=k; v6", //
        "{k,6,7}");
    check("v7={5,6}; v7[[0]]=g; v7", //
        "g(5,6)");
    check("v8={5,6,7}; Check(v8[[g(k)]]=1, bad)", //
        "bad");
    check("v9={5,6,7}; Check(v9[[{2,g(k)}]]=1, bad)", //
        "bad");
    check("v10={5,6,7}; v10[[{2,1.5}]]=1; v10", //
        "{5,6,7}");
  }

  @Test
  public void testRealDigitsCutsOffDigits() {
    // the digits behind the ones asked for decide
    check("RealDigits(1-Exp(-8),10,3)", //
        "{{9,9,9},0}");
    check("RealDigits(7-Exp(-8),10,1)", //
        "{{6},1}");
    check("RealDigits(1-Exp(-8),2,4)", //
        "{{1,1,1,1},0}");
    check("RealDigits(Sqrt(17),10,2,-1)", //
        "{{1,2},0}");
    // a position far below the leading digit
    check("RealDigits(Exp(90)*10^25,10,4,0)", //
        "{{8,2,4,3},1}");
    // a value which stays on a boundary is that boundary
    check("RealDigits((Sqrt(3)+Sqrt(5))^2-2*Sqrt(15),10,3)", //
        "{{8,0,0},1}");
    check("RealDigits(1-Exp(-50),10,6)", //
        "{{1,0,0,0,0,0},1}");
    check("RealDigits(0,10,4)", //
        "{{0,0,0,0},0}");
    check("RealDigits(0)", //
        "{{0},1}");
    check("RealDigits(Pi,10,4)", //
        "{{3,1,4,1},1}");
    check("RealDigits(1/7,10,4)", //
        "{{1,4,2,8},0}");
  }

  @Test
  public void testRealDigitsMessages() {
    check("RealDigits((Sqrt(3)+Sqrt(5))^2-2*Sqrt(15)-8,10,3)", //
        "RealDigits(-8-2*Sqrt(15)+(Sqrt(3)+Sqrt(5))^2,10,3)");
    check("Check(RealDigits((Sqrt(3)+Sqrt(5))^2-2*Sqrt(15)-8,10,3), hidden)", //
        "hidden");
    check("Check(RealDigits((Sqrt(3)+Sqrt(5))^2-2*Sqrt(15)-8), hidden)", //
        "hidden");
    check("Check(RealDigits(Sqrt(-3),10,3), notreal)", //
        "notreal");
    check("Check(RealDigits(Sqrt(-3)), notreal)", //
        "notreal");
    check("RealDigits(Sqrt(-3),10,3)", //
        "RealDigits(I*Sqrt(3),10,3)");
    check("RealDigits(k,10,3)", //
        "RealDigits(k,10,3)");
    // the digits behind the precision limit are 0
    check("RealDigits(10^600*Pi,10,3,-1)", //
        "{{0,0,0},0}");
  }

  @Test
  public void testOuterLevels() {
    check("Outer(g,{5,6},2)", //
        "{g(5),g(6)}");
    check("Outer(g,{{5},{6}},{k},1)", //
        "{{g({5},k)},{g({6},k)}}");
    check("Outer(g,{{5}},{{k}},2,1)", //
        "{{{g(5,{k})}}}");
    check("Outer(g,{{5}},{{k}},Infinity)", //
        "{{{{g(5,k)}}}}");
    // a list without a level of its own is taken apart completely, extra levels are ignored
    check("Outer(g,{{5}},{{k}},{{m}},2,1)", //
        "{{{{{g(5,{k},m)}}}}}");
    check("Outer(g,{{5}},{{k}},1,1,1)", //
        "{{g({5},{k})}}");
    check("Outer(g,{{5}},{{k}},Infinity,1)", //
        "{{{g(5,{k})}}}");
    check("Outer(g,{5,6})", //
        "{g(5),g(6)}");
    check("Outer(g,k)", //
        "Outer(g,k)");
    check("Check(Outer(g,k), bad)", //
        "bad");
    check("Check(Outer(g,{5},0), bad)", //
        "bad");
    check("Check(Outer(g,{5},k,1), bad)", //
        "bad");
    check("Outer(Plus,{1,2},{3})", //
        "{{4},{5}}");
    check("Check(Outer(g,{5},{6},2.0), bad)", //
        "bad");
    // an array object is an atom
    check("Check(Outer(g,{1,2},NumericArray({3},\"Integer32\")), bad)", //
        "bad");
    check("Check(Outer(g,NumericArray({3},\"Integer32\"),{1,2}), bad)", //
        "bad");
    check("Check(Outer(g,{1},ByteArray({3,4})), bad)", //
        "bad");
  }

  @Test
  public void testOptionsKeepTheirOrder() {
    check("Options(o1)={m->1,c->2,t->3}; Options(o1)", //
        "{m->1,c->2,t->3}");
    check("SetOptions(o1,c->9)", //
        "{m->1,c->9,t->3}");
    check("Take(Options(o1),2)", //
        "{m->1,c->9}");
    check("Options(o1,t)", //
        "{t->3}");
  }

  @Test
  public void testIteratorBounds() {
    check("Check(Table(1,{k}), bad)", //
        "bad");
    check("Check(Table(1,{g(k)}), bad)", //
        "bad");
    check("Check(Table(1,{Infinity}), bad)", //
        "bad");
    check("Check(Table(j,{j,3,Infinity}), bad)", //
        "bad");
    check("Check(Do(1,{g(k)}), bad)", //
        "bad");
    check("Check(Do(1,{j,2},{m,k}), bad)", //
        "bad");
    check("Check(Table(1,{j,2},{m,k}), bad)", //
        "bad");
    check("Check(Array(g,k), bad)", //
        "bad");
    check("Table(1,{7/3})", //
        "{1,1}");
    check("Table(j,{j,1,Infinity})", //
        "Table(j,{j,1,Infinity})");
    // the difference of the bounds is known
    check("Check(Table(j,{j,k,k+1}), bad)", //
        "{k,1+k}");
    check("Module({c=0}, Do(c++; If(j>4,Break()), {j,2,Infinity}); c)", //
        "4");
    // a symbolic sum has no bounds to complain about
    check("Check(Sum(g(j),{j,1,k}), bad)", //
        "Sum(g(j),{j,1,k})");
  }

  @Test
  public void testRandom() {
    check("Head(Random(BinomialDistribution(9,0.25)))", //
        "Integer");
    check("Head(Random(ExponentialDistribution(2)))", //
        "Real");
    check("Head(Random(Complex))", //
        "Complex");
    check("Random(g(1))", //
        "Random(g(1))");
    check("RandomVariate(BinomialDistribution(0,1/4),2)", //
        "{0,0}");
    check("Check(RandomVariate(BinomialDistribution(6,3/2)), bad)", //
        "bad");
    check("Check(RandomVariate(BinomialDistribution(-6,1/2)), bad)", //
        "bad");
    check("Check(RandomVariate(BinomialDistribution(6,1/2),-2), bad)", //
        "bad");
    check("Check(RandomVariate(BinomialDistribution(6,1/2),{2,k}), bad)", //
        "bad");
    check("Check(RandomVariate(ExponentialDistribution(2),2.5), bad)", //
        "bad");
    check("Head(RandomVariate(ExponentialDistribution(2),{}))", //
        "Real");
  }

  @Test
  public void testListPlotValuesWhichAreNotNumbers() {
    check("Cases(ListPlot({4,k,6}),_Point,Infinity)", //
        "{Point({{1.0,4},{3.0,6}})}");
    check("Cases(ListPlot({{1,k},{2,5},{3,6}}),_Point,Infinity)", //
        "{Point({{2,5},{3,6}})}");
    check("Cases(ListPlot({1,2,k,4,5},Joined->True),_Line,Infinity)", //
        "{Line({{1.0,1},{2.0,2}}),Line({{4.0,4},{5.0,5}})}");
    check("Cases(ListPlot({{1,1},{2,k},{3,3},{4,4}},Joined->True),_Line,Infinity)", //
        "{Line({{3,3},{4,4}})}");
    // a point alone isn't drawn
    check("Cases(ListPlot({1,k,3,m,5},Joined->True),_Line,Infinity)", //
        "{}");
    // an uncertain value is drawn
    check("Length(Cases(ListPlot({Around(1,0.25),2,3}),_Point,Infinity)[[1,1]])", //
        "3");
  }

  @Test
  public void testRepeatedBeforeFixedElements() {
    check("MatchQ(Table(1,{j,15000}),{(1|2)..,3})", //
        "False");
    check("MatchQ(Append(Table(1,{j,15000}),3),{(1|2)..,3})", //
        "True");
    // the names of a unit are searched without the Java stack
    check("MatchQ(Table(5,{j,15000}),{(k:(4|5))..})", //
        "True");
    check("MatchQ(Append(Table(5,{j,15000}),4),{(k:(4|5))..})", //
        "False");
    check("MatchQ({1,2,1,2},{PatternSequence(u_,v_)..})", //
        "True");
    check("MatchQ({1,2,1,3},{PatternSequence(u_,v_)..})", //
        "False");
    check("ReplaceList({1,2,3},{(u__)..}:>{u})", //
        "{{1,2,3}}");
    check("MatchQ({1,2,3,4},{p:(1|2)..,k_,m_})", //
        "True");
    check("MatchQ({1,3},{(1|2)..,_,_})", //
        "False");
    check("{1,2,3}/.{u__,k_}:>{{u},k}", //
        "{{1,2},3}");
    check("{3}/.{u___,3}:>{u}", //
        "{}");
    check("{1,2}/.{u__,k_:9}:>{{u},k}", //
        "{{1},2}");
    check("{1,2,3}/.{Longest(u__),k_}:>{{u},k}", //
        "{{1,2},3}");
    check("{1,2,3}/.{u__,k__}:>{{u},{k}}", //
        "{{1},{2,3}}");
    check("{1,2,{3}}/.{u__,{3}}:>{u}", //
        "{1,2}");
    // a Flat head can give several elements to a pattern
    check("SetAttributes(fl,Flat); fl(1,2,3)/.fl(u__,k_):>{{u},k}", //
        "{{1},fl(2,3)}");
  }
}
