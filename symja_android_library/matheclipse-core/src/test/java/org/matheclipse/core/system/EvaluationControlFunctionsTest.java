package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** Tests for Assert, Goto/Label, BlockRandom, TracePrint/TraceScan, Threaded and the other evaluation control functions. */
public class EvaluationControlFunctionsTest extends ExprEvaluatorTestCase {

  @Test
  public void testAssert() {
    check("Assert(1 > 2)", //
        "Assert(1>2)");
    check("Assert(Print(xyz))", //
        "Assert(Print(xyz))");
  }

  @Test
  public void testAssertOn() {
    check("On(Assert);Assert(7 < 3);Off(Assert);u", //
        "u");
    check("On(Assert);Assert(2 < 3);Off(Assert)", //
        "");
  }

  @Test
  public void testGotoLabel() {
    check("Module({k = 0}, Label(top); k++; If(k < 4, Goto(top)); k)", //
        "4");
    check("Module({s = {}, j = 0}, Label(a); j++; AppendTo(s, j); If(j < 3, Goto(a), Goto(b)); AppendTo(s, never); Label(b); s)", //
        "{1,2,3}");
    check("Module({c = 0}, TraceScan((c++) &, 2*3 + 1); c)", //
        "4");
  }

  @Test
  public void testGoto() {
    check("Goto(nowhere)", //
        "Hold(Goto(nowhere))");
  }

  @Test
  public void testLabel() {
    check("Label(x7)", //
        "Label(x7)");
  }

  @Test
  public void testBlockRandom() {
    check("SeedRandom(42); a1 = RandomInteger(1000); SeedRandom(42); {BlockRandom(RandomInteger(1000)), RandomInteger(1000)} == {a1, a1}", //
        "True");
    check("SeedRandom(7); BlockRandom(SeedRandom(1); RandomReal()); r1 = RandomInteger(99); SeedRandom(7); r1 == RandomInteger(99)", //
        "True");
  }

  @Test
  public void testMemoryConstrained() {
    check("MemoryConstrained(2 + 3, 10^6)", //
        "5");
    check("MemoryConstrained(Factor(x^4 - 1), 10^6, failed)", //
        "(-1+x)*(1+x)*(1+x^2)");
  }

  @Test
  public void testPreemptProtect() {
    check("PreemptProtect(3*4)", //
        "12");
  }

  @Test
  public void testRelease() {
    check("Release(Hold(1 + 2))", //
        "3");
  }

  @Test
  public void testHoldCompleteForm() {
    check("HoldCompleteForm(1 + 1)", //
        "1+1");
    check("HoldCompleteForm(1 + 1) // InputForm", //
        "HoldCompleteForm(1 + 1)");
  }

  @Test
  public void testInterpretation() {
    check("Interpretation(shown, 2 + 3)", //
        "Interpretation(shown,2+3)");
    check("Interpretation(shown, 2 + 3) // FullForm", //
        "Interpretation(shown, Plus(2, 3))");
  }

  @Test
  public void testClearSystemCache() {
    check("ClearSystemCache()", //
        "");
  }

  @Test
  public void testUpdate() {
    check("Update()", //
        "");
  }

  @Test
  public void testSyntaxInformation() {
    check("SyntaxInformation(sinf)", //
        "{}");
    check("SyntaxInformation(sinf) = {\"ArgumentsPattern\" -> {_, _.}}; SyntaxInformation(sinf)", //
        "{ArgumentsPattern->{_,_.}}");
  }

  @Test
  public void testTrace() {
    check("Trace(f1(1 + 2), TraceDepth -> 1)", //
        "{f1(3)}");
  }

  @Test
  public void testTracePrint() {
    check("TracePrint(1 + 2 + 3)", //
        "6");
    check("TracePrint(g2(1 + 1), TraceDepth -> 1)", //
        "g2(2)");
  }

  @Test
  public void testTraceScan() {
    check("TraceScan(Print, 2*3 + 1)", //
        "7");
  }

  @Test
  public void testPrintTemporary() {
    check("PrintTemporary(\"temp \", 1 + 1)", //
        "");
  }

  @Test
  public void testParallelDo() {
    check("ParallelDo(Print(i^2), {i, 3})", //
        "");
  }

  @Test
  public void testParallelArray() {
    check("ParallelArray(h1, {2, 2}, Method -> \"CoarsestGrained\")", //
        "{{h1(1,1),h1(1,2)},{h1(2,1),h1(2,2)}}");
  }

  @Test
  public void testParallelCases() {
    check("ParallelCases({1, a, 2, b}, _Integer)", //
        "{1,2}");
  }

  @Test
  public void testCompressedData() {
    check("CompressedData(Compress({1, x, \"s\"}))", //
        "{1,x,s}");
  }

  @Test
  public void testRawArray() {
    check("RawArray(\"Integer8\", {1, 2, 3})", //
        "NumericArray(Type: Integer8 Dimensions: {3})");
  }

  @Test
  public void testThreadedLists() {
    check("{{1, 2}, {3, 4}} + Threaded({10, 20})", //
        "{{11,22},{13,24}}");
    check("{{1, 2}, {3, 4}} * Threaded({a, b})", //
        "{{a,2*b},{3*a,4*b}}");
    check("{1, 2} + Threaded({5, 6})", //
        "{6,8}");
    check("{{{1, 2}, {3, 4}}, {{5, 6}, {7, 8}}} + Threaded({{0, 100}, {200, 300}})", //
        "{{{1,102},{203,304}},{{5,106},{207,308}}}");
    check("1/0; MessageList($Line)", //
        "{Power::infy}");
    // the messages of an earlier input don't carry over
    check("MessageList($Line)", //
        "{}");
  }

  @Test
  public void testThreaded() {
    check("Threaded({1, 2}) + 5", //
        "Threaded({6,7})");
  }

  @Test
  public void testThreadedWithoutList() {
    check("f3(Threaded({1, 2}))", //
        "f3(Threaded({1,2}))");
  }

  @Test
  public void testDollarDisplayFunction() {
    check("$DisplayFunction", //
        "Identity");
  }

  @Test
  public void testDisplayFunction() {
    check("Show(Graphics(Point({0, 0})), DisplayFunction -> Head)", //
        "Graphics");
  }
}
