package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** Lists, options and control flow. */
public class ListOptionControlFlowTest extends ExprEvaluatorTestCase {

  @Test
  public void testFoldPairListTakeDrop() {
    check("TakeDrop({p, q, r, s}, 2)", //
        "{{p,q},{r,s}}");
    check("TakeDrop({p, q, r, s}, -1)", //
        "{{s},{p,q,r}}");
    check("FoldPairList(TakeDrop, {1, 2, 3, 4, 5}, {2, 1, 2})", //
        "{{1,2},{3},{4,5}}");
    // the output keeps the head of the list
    check("FoldPairList(TakeDrop, {u, v, w}, g(1, 2))", //
        "g({u},{v,w})");
    check("FoldPairList({#1 + #2, #1*#2} &, 2, {3, 4})", //
        "{5,10}");
    check("FoldPairList({#1, #1 + #2} &, 0, {1, 2, 3}, k)", //
        "{k({0,1}),k({1,3}),k({3,6})}");
    check("Catch(FoldPairList({If(#1 > 20, Throw(#1)), #1 + #2} &, 0, Range(10)))", //
        "21");
  }

  @Test
  public void testNProductStep() {
    check("NProduct(k, {k, 2, 8, 3})", //
        "80.0");
  }

  @Test
  public void testClockStartingValue() {
    check("{Clock({5, 9}), Clock({c, d}, 3), Clock(4), Clock({1/3, 2, 1/3})}", //
        "{5,c,0,1/3}");
  }

  @Test
  public void testHarmonicNumberThreeArguments() {
    check("HarmonicNumber(3, 1, t)", //
        "t+t^2/2+t^3/3");
    check("HarmonicNumber(2, 2, 3)", //
        "21/4");
    check("HarmonicNumber(Infinity, 3, w)", //
        "PolyLog(3,w)");
    check("HarmonicNumber(m, 3, 1)", //
        "HarmonicNumber(m,3)");
    check("HarmonicNumber(0, 2, w)", //
        "0");
  }

  @Test
  public void testKroneckerDeltaArrays() {
    check("KroneckerDelta({2, 3}, {3, 2})", //
        "0");
    check("KroneckerDelta({2, 3}, {2, 3}, {2, 3})", //
        "1");
    check("KroneckerDelta({{1}}, {1})", //
        "0");
  }

  @Test
  public void testUpsamplePadding() {
    check("Upsample({a, b}, 2, 1, z)", //
        "{a,z,b,z}");
  }

  @Test
  public void testFileNameOfFile() {
    check("{FileExtension(File(\"dir/data.csv\")), FileBaseName(File(\"dir/data.csv\"))}", //
        "{csv,data}");
  }

  @Test
  public void testProjectionOnSpan() {
    check("Projection({4, 5, 6}, {{1, 0, 0}, {0, 1, 0}})", //
        "{4,5,0}");
    check("Projection({2, 0, 1}, {{1, 1, 0}, {3, 3, 0}})", //
        "{1,1,0}");
    check("Projection({1, I, 0}, {{1, 0, 0}, {0, I, 0}})", //
        "{1,I,0}");
  }

  @Test
  public void testShiftRegisterSequenceDefaultPolynomial() {
    check("ShiftRegisterSequence(6) === ShiftRegisterSequence(1 + x + x^6)", //
        "True");
    check("ShiftRegisterSequence(2)", //
        "{0,1,1}");
  }

  @Test
  public void testArrayPadExtrapolated() {
    check("ArrayPad({2, 4, 8}, {2, 1}, \"Extrapolated\")", //
        "{-2,0,2,4,8,12}");
    check("ArrayPad({p, q}, {1, 0}, \"Extrapolated\")", //
        "{2*p-q,p,q}");
    check("ArrayPad({0, 1, 4}, {0, 2}, \"Extrapolated\", InterpolationOrder -> 2)", //
        "{0,1,4,9,16}");
    check("ArrayPad({3, 5}, {1, 1}, \"Extrapolated\", InterpolationOrder -> 0)", //
        "{3,3,5,5}");
  }

  @Test
  public void testRankDecompositionModulus() {
    check("RankDecomposition({{2, 4}, {1, 2}}, Modulus -> 3)", //
        "{{{2},{1}},{{1,2}}}");
  }

  @Test
  public void testIdentityMatrixTargetStructure() {
    check("IdentityMatrix(2, TargetStructure -> \"Dense\") // Normal", //
        "{{1,0},\n" //
            + " {0,1}}");
    check("IdentityMatrix(2, TargetStructure -> \"Sparse\") === IdentityMatrix(2, SparseArray)", //
        "True");
  }

  @Test
  public void testNearestMachineDistances() {
    // |1.2-1.1| is a little less than |1.3-1.2|
    check("Nearest({1.1, 1.3}, 1.2)", //
        "{1.1}");
    check("Nearest({4.0, 6.0}, 5.0)", //
        "{4.0,6.0}");
  }

  @Test
  public void testInsertIntoAssociationValue() {
    check("Insert(<|\"u\" -> {a, b}, \"v\" -> {c}|>, z, {\"u\", 1})", //
        "<|u->{z,a,b},v->{c}|>");
  }

  @Test
  public void testRandomChoiceSpan() {
    check("Length(RandomChoice(3 ;; 7, 6))", //
        "6");
    check("SubsetQ({2, 5, 8}, RandomChoice(2 ;; 8 ;; 3, 9))", //
        "True");
  }

  @Test
  public void testRowToStringOptions() {
    check("ToString(Row({\"p\", 7}, FontSize -> 12))", //
        "p7");
    check("ToString(Row({\"p\", 7, q}, \"/\", FontColor -> Red))", //
        "p/7/q");
  }

  @Test
  public void testURLQueries() {
    check("URLQueryDecode(\"b=2&a=x+y\")", //
        "{b->2,a->x y}");
    check("URLQueryDecode(\"q=1&p\", \"SortQueries\" -> True)", //
        "{p->None,q->1}");
    check("URLBuild(\"http://host.org/p?k=1\", {\"m\" -> 2})", //
        "http://host.org/p?k=1&m=2");
    check("URLBuild(\"http://host.org?k=1\", {\"b\" -> 2, \"a\" -> 3}, \"SortQueries\" -> True)", //
        "http://host.org?a=3&b=2&k=1");
    check("URLBuild(\"http://host.org\", <|\"t\" -> {7, 8}|>)", //
        "http://host.org?t=7&t=8");
    check("URLBuild(<|\"Scheme\" -> \"http\", \"Domain\" -> \"host.org\", \"Port\" -> \"8080\", "
        + "\"Path\" -> {\"a\", \"b\"}|>)", //
        "http://host.org:8080/a/b");
    check("URLQueryEncode(<|\"k\" -> None, \"m\" -> {1, 2}|>)", //
        "k&m=1&m=2");
    check("URLQueryEncode({\"k\" -> None, \"m\" -> 1}, \"RemoveEmptyQueries\" -> True)", //
        "m=1");
  }

  @Test
  public void testJoinAcrossCollisions() {
    check("JoinAcross({<|k -> 1, v -> p|>}, {<|k -> 1, v -> q|>}, Key(k), \"Inner\", "
        + "KeyCollisionFunction -> Right)", //
        "{<|k->1,v->q|>}");
    check("JoinAcross({<|k -> 1, v -> p, w -> 0|>}, {<|k -> 1, v -> q|>}, Key(k), \"Inner\", "
        + "KeyCollisionFunction -> None)", //
        "{<|k->1,w->0|>}");
    check("JoinAcross({<|\"id\" -> 2, \"n\" -> 5|>}, {<|\"key\" -> 2, \"m\" -> 6|>}, "
        + "\"id\" -> \"key\")", //
        "{<|id->2,n->5,key->2,m->6|>}");
  }

  @Test
  public void testModelFitRuleData() {
    check("LinearModelFit({0 -> 1, 1 -> 3, 2 -> 5}, x, x)[\"BestFitParameters\"]", //
        "{1.0,2.0}");
    check("LinearModelFit({0, 1, 2} -> {1, 3, 5}, x, x)[\"BestFitParameters\"]", //
        "{1.0,2.0}");
  }

  @Test
  public void testDivideSidesGenerateConditions() {
    check("DivideSides(p*q < r, q, GenerateConditions -> True)", //
        "ConditionalExpression(p<r/q,q>0)");
    check("DivideSides(p*q < r, q, GenerateConditions -> False)", //
        "p<r/q");
    check("DivideSides(p*q == r, q, GenerateConditions -> True)", //
        "ConditionalExpression(p==r/q,q!=0)");
    check("DivideSides(p*q == r, q, GenerateConditions -> Automatic)", //
        "p==r/q");
    check("MultiplySides(0 <= y < 3, -2)", //
        "-6<-2*y<=0");
    check("AddSides(0 <= y < 3, 1)", //
        "1<=1+y<4");
  }

  @Test
  public void testPermutationProductPowers() {
    check("PermutationProduct(p, p, p)", //
        "PermutationPower(p,3)");
    check("PermutationProduct(q, InversePermutation(p), p)", //
        "q");
    check("PermutationProduct(p, PermutationPower(p, 2), q)", //
        "PermutationProduct(PermutationPower(p,3),q)");
  }

  @Test
  public void testStringTemplateDefaults() {
    check("TemplateApply(StringTemplate(\"`u`-`v`\", <|\"u\" -> 1|>), <|\"u\" -> 2, \"v\" -> 3|>)", //
        "1-3");
    check("StringTemplate(\"`u`:`v`\", <|\"v\" -> 9|>)[<|\"u\" -> 8|>]", //
        "8:9");
    check("TemplateApply(StringTemplate(\"[`u`][`v`]\", <|\"u\" -> 4|>))", //
        "[4][]");
  }

  @Test
  public void testTextStringOptions() {
    check("TextString({False, True}, BooleanStrings -> {\"yes\", \"no\"})", //
        "{no, yes}");
    check("TextString({4, 5}, ListFormat -> {\"(\", \"|\", \")\"})", //
        "(4|5)");
    check("TextString(<|u -> 1|>, AssociationFormat -> {\"[\", \",\", \"]\", \"=\"})", //
        "[u=1]");
    check("TextString({k(1), m(2)}, ElidedForms -> k)", //
        "{k[…], m(2)}");
    check("TextString({Missing(\"Gone\"), 1}, MissingString -> \"-\")", //
        "{-, 1}");
    check("TextString({Missing(\"Gone\"), 1}, MissingString -> Automatic)", //
        "{Gone, 1}");
  }

  @Test
  public void testLerchPhiSingularTerm() {
    check("LerchPhi(w, 3, -1)", //
        "1+w*PolyLog(3,w)");
    check("LerchPhi(3, 2, -1, IncludeSingularTerm -> True)", //
        "ComplexInfinity");
    check("LerchPhi(3, -2, -1, IncludeSingularTerm -> True) == LerchPhi(3, -2, -1)", //
        "True");
  }

  @Test
  public void testReadListRecordLists() {
    check("ReadList(StringToStream(\"1 2\\n3 4 5\"), Number, RecordLists -> True)", //
        "{{1,2},{3,4,5}}");
  }

  @Test
  public void testRootsOptions() {
    check("Roots(x^2 == 9, x, EquatedTo -> w^2)", //
        "w^2==-3||w^2==3");
    check("Roots(x^2 == 9, x, Multiplicity -> 0.5)", //
        "x==-3||x==3");
  }

  @Test
  public void testFindMinValueArgMin() {
    check("FindMinValue((y + 2)^2 + 5, {y, 1})", //
        "5.0");
    check("FindArgMin((y + 2)^2 + 5, {y, 1})", //
        "{-2.0}");
    check("FindArgMax(-(y - 1)^2, {y, 0})", //
        "{1.0}");
  }

  @Test
  public void testHeavisideEven() {
    check("{HeavisidePi(-3*y), HeavisideLambda(-y/2)}", //
        "{HeavisidePi(3*y),HeavisideLambda(y/2)}");
  }

  @Test
  public void testAbsoluteTimeTimeZone() {
    check("AbsoluteTime({2021, 1, 5}, TimeZone -> 2) - AbsoluteTime({2021, 1, 5}, TimeZone -> 1)", //
        "3600");
  }

  @Test
  public void testRefineCombinedAssumptions() {
    check("Refine(Sqrt(p^2) + Abs(q), p < 0, Assumptions -> q > 0)", //
        "-p+q");
    check("FullSimplify(Sqrt(p^2) + Abs(q), p < 0, Assumptions -> q > 0)", //
        "-p+q");
  }

  @Test
  public void testLogicalExpandEquations() {
    check("LogicalExpand({p, {q}} == {1, {2}})", //
        "p==1&&q==2");
    check("LogicalExpand({p, q} == {1})", //
        "False");
    check("LogicalExpand(u == w == v)", //
        "v==u&&w==u");
    check("LogicalExpand(3 == p)", //
        "p==3");
  }

  @Test
  public void testWithPatternShadowing() {
    check("With({b = 5}, {7, 8} /. b_Integer :> b + 1)", //
        "{8,9}");
    check("With({b = 5}, h(b_) := b^2; h(3))", //
        "9");
    check("With({b = 5}, {7, 8} /. c_Integer :> b)", //
        "{5,5}");
  }

  @Test
  public void testOrthogonalizeInnerProduct() {
    check("Orthogonalize({{p, q}}, Dot)", //
        "{{p/Sqrt(p^2+q^2),q/Sqrt(p^2+q^2)}}");
    check("Orthogonalize({1, t}, Integrate(#1*#2, {t, 0, 1}) &)", //
        "{1,2*Sqrt(3)*(-1/2+t)}");
  }

  @Test
  public void testCancelTogetherExtension() {
    check("Cancel((x^2 - 3)/(x + Sqrt(3)), Extension -> Automatic)", //
        "-Sqrt(3)+x");
    check("Together(x/(x - Sqrt(3)) - Sqrt(3)/(x - Sqrt(3)), Extension -> Sqrt(3))", //
        "1");
    check("Cancel((x^2 + 4)/(x + 2*I), Extension -> Automatic)", //
        "-I*2+x");
    check("FactorSquareFree(x^2 - 2*Sqrt(3)*x + 3, Extension -> Automatic)", //
        "(Sqrt(3)-x)^2");
    check("FactorSquareFreeList(2*x^2 + 4*x + 2, Extension -> Sqrt(5))", //
        "{{2,1},{1+x,2}}");
  }

  @Test
  public void testSubstitutionSystem() {
    check("SubstitutionSystem({\"x\" -> \"xy\", \"y\" -> \"x\"}, \"y\", 3)", //
        "{y,x,xy,xyx}");
    check("SubstitutionSystem({a -> {a, b}, b -> {a}}, {b}, {2, 4, 2})", //
        "{{a,b},{a,b,a,a,b}}");
  }

  @Test
  public void testFindIntegerNullVector() {
    check("FindIntegerNullVector({2, Sqrt(3), 4 - Sqrt(3)})", //
        "{2,-1,-1}");
    check("FindIntegerNullVector({0.25, 0.5, 1.0})", //
        "{2,-1,0}");
  }

  @Test
  public void testRegionMeasureOptions() {
    check("Volume(Cube(), WorkingPrecision -> MachinePrecision)", //
        "1.0");
    check("RegionMeasure(Disk({0, 0}, 2), PerformanceGoal -> \"Quality\")", //
        "4*Pi");
  }
}
