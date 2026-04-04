package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.hipparchus.util.RosenNumberPartitionIterator;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.combinatoric.MultisetPartitionsIterator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.patternmatching.FlatOrderlessStepVisitor;
import org.matheclipse.core.patternmatching.IPatternMap;
import org.matheclipse.core.patternmatching.PatternMatcher;

/** Tests for combinatorial functions */
public class CombinatoricTestCase extends ExprEvaluatorTestCase {

  /** Test combinatorial functions */
  @Test
  public void testCombinatoric() {

    // check("KOrderlessPartitions(f(g),1)", //
    // "{f(g)}");
    // check("KOrderlessPartitions(f(),1)", //
    // "KOrderlessPartitions(f(),1)");
    check("KOrderlessPartitions(w+x+x+y+z,5)", //
        "KOrderlessPartitions(w+2*x+y+z,5)");
    check("KOrderlessPartitions(w+x+x+y+z,6)", //
        "KOrderlessPartitions(w+2*x+y+z,6)");
    check("KOrderlessPartitions(w+x+x+y+z,3)", //
        "{{w,2*x,y+z},{w,2*x+y,z},{w+2*x,y,z},{w,2*x+z,y},{w+2*x,z,y},{w,y,2*x+z},{w+y,2*x,z},{w,y+z,\n"
            + "2*x},{w+y,z,2*x},{w,z,2*x+y},{w+z,2*x,y},{w+z,y,2*x},{2*x,w,y+z},{2*x,w+y,z},{2*x,w+z,y},{\n"
            + "2*x,y,w+z},{2*x+y,w,z},{2*x,y+z,w},{2*x+y,z,w},{2*x,z,w+y},{2*x+z,w,y},{2*x+z,y,w},{y,w,\n"
            + "2*x+z},{y,w+2*x,z},{y,w+z,2*x},{y,2*x,w+z},{y,2*x+z,w},{y,z,w+2*x},{y+z,w,2*x},{y+z,\n"
            + "2*x,w},{z,w,2*x+y},{z,w+2*x,y},{z,w+y,2*x},{z,2*x,w+y},{z,2*x+y,w},{z,y,w+2*x}}");

    check("KPartitions({v,w,x,y,z},3)", //
        "{{{v},{w},{x,y,z}},{{v},{w,x},{y,z}},{{v},{w,x,y},{z}},{{v,w},{x},{y,z}},{{v,w},{x,y},{z}},{{v,w,x},{y},{z}}}");

    check("IntegerPartitions(3)", //
        "{{3},{2,1},{1,1,1}}");
    check("IntegerPartitions(5)", //
        "{{5},{4,1},{3,2},{3,1,1},{2,2,1},{2,1,1,1},{1,1,1,1,1}}");
    check("IntegerPartitions(10)", //
        "{{10},{9,1},{8,2},{8,1,1},{7,3},{7,2,1},{7,1,1,1},{6,4},{6,3,1},{6,2,2},{6,2,1,1},{\n"
            + "6,1,1,1,1},{5,5},{5,4,1},{5,3,2},{5,3,1,1},{5,2,2,1},{5,2,1,1,1},{5,1,1,1,1,1},{\n"
            + "4,4,2},{4,4,1,1},{4,3,3},{4,3,2,1},{4,3,1,1,1},{4,2,2,2},{4,2,2,1,1},{4,2,1,1,1,\n"
            + "1},{4,1,1,1,1,1,1},{3,3,3,1},{3,3,2,2},{3,3,2,1,1},{3,3,1,1,1,1},{3,2,2,2,1},{3,\n"
            + "2,2,1,1,1},{3,2,1,1,1,1,1},{3,1,1,1,1,1,1,1},{2,2,2,2,2},{2,2,2,2,1,1},{2,2,2,1,\n"
            + "1,1,1},{2,2,1,1,1,1,1,1},{2,1,1,1,1,1,1,1,1},{1,1,1,1,1,1,1,1,1,1}}");

    check("Permutations({1,1,1})", //
        "{{1,1,1}}");
    check("Permutations({2,1,0})", //
        "{{2,1,0},{2,0,1},{1,2,0},{1,0,2},{0,2,1},{0,1,2}}");
    check("Permutations({1,2,2,3})", //
        "{{1,2,2,3},{1,2,3,2},{1,3,2,2},{2,1,2,3},{2,1,3,2},{2,2,1,3},{2,2,3,1},{2,3,1,2},{\n"
            + "2,3,2,1},{3,1,2,2},{3,2,1,2},{3,2,2,1}}");
    check("Partition({t,u,v,w,x,y,z},3,2)", //
        "{{t,u,v},{v,w,x},{x,y,z}}");
    check("Partition({a, b, c, d, e, f, g, h}, 2, 3)", //
        "{{a,b},{d,e},{g,h}}");
  }

  @Test
  public void testCycles() {

    check("Cycles({{4, 10, 2, 5}, {9}, {7, 1, 18}})", //
        "Cycles({{1,18,7},{2,5,4,10}})");

    check("Cycles({{4, 5},{1, 2, 3},{}})", //
        "Cycles({{1,2,3},{4,5}})");
    // message: Cycles: Cycles({{7,2},{},{5,6,7}}) contains repeated integers.
    check("Cycles({{7,2},{},{5,6,7}})", //
        "Cycles({{7,2},{},{5,6,7}})");

    check("Cycles({{1000000, 100}, {10000, 1, 100000000}})", //
        "Cycles({{1,100000000,10000},{100,1000000}})");
    check("Cycles({})", //
        "Cycles({})");
    check("Cycles({{1}, {2}, {3}, {4}})", //
        "Cycles({})");
    check("Cycles({SparseArray({1 -> 1000, 2 -> 10}), {100, 1, 10000}})", //
        "Cycles({{1,10000,100},{10,1000}})");
  }

  @Test
  public void testFindPermutation() {
    check("FindPermutation(CharacterRange(\"a\",\"d\"), {\"a\",\"d\",\"c\",\"b\"})", //
        "Cycles({{2,4}})");
    check("FindPermutation(h(a,c,d,e,b),h(c,a,b,d,e))", //
        "Cycles({{1,2},{3,4,5}})");
    check("PermutationCycles({2,1,4,5,3})", //
        "Cycles({{1,2},{3,4,5}})");
    check("FindPermutation(h(a,c,c,d,e,b),h(c,c,a,b,d,e))", //
        "Cycles({{1,3,2},{4,5,6}})");
    // FindPermutation: Expressions h(a,c,c,d,e,b,f) and h(c,c,c,a,b,d,e) cannot be related by a
    // permutation.
    check("FindPermutation(h(a,c,c,d,e,b,f),h(c,c,c,a,b,d,e))", //
        "FindPermutation(h(a,c,c,d,e,b,f),h(c,c,c,a,b,d,e))");
    check("FindPermutation({a, d, c, g, h, f})", //
        "Cycles({{2,3},{4,6,5}})");
  }

  @Test
  public void testIntegerPartitions() {
    // TODO
    check("IntegerPartitions(1,7,{-1,-2,3})", //
        "IntegerPartitions(1,7,{-1,-2,3})");

    check("IntegerPartitions(8, All, {1, 2, 5})", //
        "{{5,2,1},{5,1,1,1},{2,2,2,2},{2,2,2,1,1},{2,2,1,1,1,1},{2,1,1,1,1,1,1},{1,1,1,1,\n" //
            + "1,1,1,1}}");

    check("IntegerPartitions(5)", //
        "{{5},{4,1},{3,2},{3,1,1},{2,2,1},{2,1,1,1},{1,1,1,1,1}}");
    check("IntegerPartitions(8,3)", //
        "{{8},{7,1},{6,2},{6,1,1},{5,3},{5,2,1},{4,4},{4,3,1},{4,2,2},{3,3,2}}");
    check("IntegerPartitions(8,{3})", //
        "{{6,1,1},{5,2,1},{4,3,1},{4,2,2},{3,3,2}}");
    check("IntegerPartitions(8, All, {1, 2, 5})", //
        "{{5,2,1},{5,1,1,1},{2,2,2,2},{2,2,2,1,1},{2,2,1,1,1,1},{2,1,1,1,1,1,1},{1,1,1,1,\n" //
            + "1,1,1,1}}");
    check("IntegerPartitions(8, All, All, 3)", //
        "{{8},{7,1},{6,2}}");
    check("IntegerPartitions(4, {2}, {-1, 0, 1, 4, 5})", //
        "{{5,-1},{4,0}}");

    // message Maximum AST dimension 2147483647 exceededs
    check("IntegerPartitions(2147483647)", //
        "IntegerPartitions(2147483647)");

    // TODO improve performance
    // check("IntegerPartitions(1009,2)", //
    // "{{1009}}");
    check("IntegerPartitions(1009,1)", //
        "{{1009}}");
    check("IntegerPartitions(1009,0)", //
        "{}");

    check("IntegerPartitions(50, All, {6, 9, 20})", //
        "{{20,9,9,6,6},{20,6,6,6,6,6}}");
    // https://oeis.org/A214772 - McNugget partitions - Number of partitions of n into parts 6, 9 or
    // 20.
    check("Table(Length(IntegerPartitions(i, All, {6, 9, 20})), {i,0, 100, 1})", //
        "{1,0,0,0,0,0,1,0,0,1,0,0,1,0,0,1,0,0,2,0,1,1,0,0,2,0,1,2,0,1,2,0,1,2,0,1,3,0,2,2,\n"
            + "1,1,3,0,2,3,1,2,3,1,2,3,1,2,4,1,3,3,2,2,5,1,3,4,2,3,5,2,3,5,2,3,6,2,4,5,3,3,7,2,\n"
            + "5,6,3,4,7,3,5,7,3,5,8,3,6,7,4,5,9,3,7,8,5}");

    // check("IntegerPartitions(50, All, {6, 9, 20})", //
    // "{{20,9,9,6,6},{20,6,6,6,6,6}}");
    check("IntegerPartitions(156, {9,10}, {1, 5, 10, 25})", //
        "{{25,25,25,25,25,10,10,10,1},{25,25,25,25,25,10,10,5,5,1}}");
    check("IntegerPartitions(156, 10, {1, 5, 10, 25})", //
        "{{25,25,25,25,25,25,5,1},{25,25,25,25,25,10,10,10,1},{25,25,25,25,25,10,10,5,5,1}}");
    check("IntegerPartitions(4)", //
        "{{4},{3,1},{2,2},{2,1,1},{1,1,1,1}}");
    check("IntegerPartitions(6)", //
        "{{6},{5,1},{4,2},{4,1,1},{3,3},{3,2,1},{3,1,1,1},{2,2,2},{2,2,1,1},{2,1,1,1,1},{\n" //
            + "1,1,1,1,1,1}}");
    check("IntegerPartitions(6, {3,4})", //
        "{{4,1,1},{3,2,1},{3,1,1,1},{2,2,2},{2,2,1,1}}");
    check("IntegerPartitions(10,2)", //
        "{{10},{9,1},{8,2},{7,3},{6,4},{5,5}}");
    check("IntegerPartitions(10,{2})", //
        "{{9,1},{8,2},{7,3},{6,4},{5,5}}");
    check("IntegerPartitions(0)", //
        "{{}}");
    check("IntegerPartitions(1)", //
        "{{1}}");
    check("IntegerPartitions(-1)", //
        "{}");
    check("IntegerPartitions(.5)", //
        "IntegerPartitions(0.5)");
    check("IntegerPartitions(1/2)", //
        "{}");
  }

  @Test
  public void testPermute() {
    check("Permute(CharacterRange(\"v\", \"z\"), Cycles({{1, 5, 3}}))", //
        "{x,w,z,y,v}");
    check("Permute({a, b, c, d, e}, {3, 1, 2})", //
        "{b,c,a,d,e}");
    check("Permute({a, b, c, d, e}, Cycles({{1, 3, 2}}))", //
        "{b,c,a,d,e}");
    check(
        "Permute({\"a\",\"b\",\"c\",\"d\",\"e\",\"f\",\"g\",\"h\",\"i\",\"j\",\"k\",\"l\",\"m\",\"n\",\"o\",\"p\", \"q\",\"r\",\"s\",\"t\",\"u\",\"v\",\"w\",\"x\",\"y\",\"z\"},"
            + "Cycles({{1, 6, 18, 2}, {3, 20, 11}}))", //
        "{b,r,k,d,e,a,g,h,i,j,t,l,m,n,o,p,q,f,s,c,u,v,w,x,y,z}");
    check("Permute(f(a, b, c, d, e), Cycles({{1, 3}, {2, 4}}))", //
        "f(c,d,a,b,e)");

    check("perm=Cycles({{1,5,3}, {4,2,8}})", //
        "Cycles({{1,5,3},{2,8,4}})");
    check("res=Permute(Range(10),perm)", //
        "{3,4,5,8,1,6,7,2,9,10}");
    check("SameQ(PermutationReplace(res, perm), Range(10))", //
        "True");
  }

  @Test
  public void testPolygonalNumber() {
    check("Interval(1/2*(1+Sqrt(5)))", //
        "Interval({1/2*(1+Sqrt(5)),1/2*(1+Sqrt(5))})");
    check("PolygonalNumber(1-10007,Interval({1/2*(1+Sqrt(5)),1/2*(1+Sqrt(5))}))", //
        "Interval({1/4*(1+Sqrt(5))*(10010-5004*(1+Sqrt(5))),1/4*(1+Sqrt(5))*(10010-5004*(\n"//
            + "1+Sqrt(5)))})");
    check("Table(PolygonalNumber(r, 3), {r, 1, 5})", //
        "{0,3,6,9,12}");
    check("PolygonalNumber(4,-2)", //
        "4");
    check("PolygonalNumber(-3,-2)", //
        "-17");
    check("Table(PolygonalNumber(n), {n, 10})", //
        "{1,3,6,10,15,21,28,36,45,55}");
    check("Table(PolygonalNumber(r, 10), {r, 3, 10})", //
        "{55,100,145,190,235,280,325,370}");

  }

  @Test
  public void testPermutationCycles() {
    check("PermutationCycles({4,2,7,6,5,8,1,3})", //
        "Cycles({{1,4,6,8,3,7}})");
    check("PermutationCycles({3, 1, 2, 5, 4})", //
        "Cycles({{1,3,2},{4,5}})");
    check("PermutationCycles({6, 3, 2, 5, 4, 1})", //
        "Cycles({{1,6},{2,3},{4,5}})");
    check("PermutationCycles(Cycles({{1, 3, 5}, {2, 4, 6}}))", //
        "Cycles({{1,3,5},{2,4,6}})");
    check("PermutationCycles({2, 5, 3, 6, 1, 8, 7, 9, 4, 10})", //
        "Cycles({{1,2,5},{4,6,8,9}})");
    check("PermutationCycles({2, 5, 3, 6, 1, 8, 7, 9, 4, 10}, f)", //
        "f({{1,2,5},{3},{4,6,8,9},{7},{10}})");
  }

  @Test
  public void testPermutationCyclesQ() {
    check("PermutationCyclesQ(Cycles({{1, 6, 2}, {4, 11, 12, 3}}))", //
        "True");
    check("PermutationCyclesQ(Cycles({a1, a2}))", //
        "False");
    check("PermutationCyclesQ(Cycles({2, a2}))", //
        "False");
    check("PermutationCyclesQ(Cycles({{1, 1, 2}, {3, 4}}))", //
        "False");
  }

  @Test
  public void testPermutationList() {
    check("PermutationList(Cycles({{3, 2}, { 6, 7},{11,17}}))", //
        "{1,3,2,4,5,7,6,8,9,10,17,12,13,14,15,16,11}");
    check("PermutationList(Cycles({{3, 2}, {1, 6, 7}}))", //
        "{6,3,2,4,5,7,1}");
    check("PermutationList(Cycles({{3, 2}, {1, 6, 7}}),10)", //
        "{6,3,2,4,5,7,1,8,9,10}");
    // message - PermutationList: Required length 5 is smaller than maximum 7 of support of
    // PermutationList(Cycles({{1,6,7},{2,3}}),5).
    check("PermutationList(Cycles({{3, 2}, {1, 6, 7}}),5)", //
        "PermutationList(Cycles({{1,6,7},{2,3}}),5)");
    check("PermutationList(Cycles({{1, 5}, {2, 9, 3}}))", //
        "{5,9,2,4,1,6,7,8,3}");
  }

  @Test
  public void testPermutationListQ() {
    check("PermutationListQ({5, 7, 6, 1, 3, 4, 2, 8})", //
        "True");
    check("PermutationListQ({3, 2, 6, 1, 5, 4, f(7), 8})", //
        "False");
    check("PermutationListQ({3, 2, 6, 1, 5, 4, 9, 7})", //
        "False");
    check("PermutationListQ({3,2,2,1})", //
        "False");
    check("PermutationListQ({0,1})", //
        "False");
    check("PermutationListQ({-1,1})", //
        "False");
  }

  @Test
  public void testPermutationReplace() {
    check("PermutationReplace({1, b, 3, 4, 5}, Cycles({{1, 5,8}, {2, 7}}))", //
        "{5,b,3,4,8}");
    check("PermutationReplace(4, {Cycles({{3, 4, 5}}), Cycles({}), Cycles({{4, 100, 10}})})", //
        "{5,4,100}");
    check("PermutationReplace({{1, 2}, {3, 4}, {a, 6}}, Cycles({{1, 5, 3}, {2, 7}}))", //
        "{{5,7},{1,4},{a,6}}");
    check("PermutationReplace({1, 2, 3, 4, 5, 6}, Cycles({{1, 5, 3}, {2, 7}}))", //
        "{5,7,1,4,3,6}");
    check("PermutationReplace(4, Cycles({{2, 3, 4, 6}}))", //
        "6");
  }

  @Test
  public void testPermutations() {
    check("Permutations({x,0,0,0})", //
        "{{x,0,0,0},{0,x,0,0},{0,0,x,0},{0,0,0,x}}");
    check("Permutations({1,2,1})", //
        "{{1,2,1},{1,1,2},{2,1,1}}");
  }

  @Test
  public void testRosenIterator() {
    IAST lhsPatternAST = F.Plus(F.x_, F.y_, F.z_);
    IAST lhsEvalAST = F.Plus(F.a, F.b, F.c, F.d);

    PatternMatcher patternMatcher = new PatternMatcher(lhsPatternAST);
    IPatternMap patternMap = patternMatcher.createPatternMap();
    // StackMatcher stackMatcher = patternMatcher.new StackMatcher(EvalEngine.get());
    FlatOrderlessStepVisitor visitor =
        new FlatOrderlessStepVisitor(F.Plus, lhsPatternAST, lhsEvalAST, patternMatcher, patternMap);
    MultisetPartitionsIterator iter =
        new MultisetPartitionsIterator(visitor, lhsPatternAST.argSize());
    boolean b = false;
    while (!b) {
      b = iter.execute();
      if (!b) {
        System.out.println(iter.toString());
        // iter.initPatternMap();
      }
    }
    assertEquals(true, b);
  }

  @Test
  public void testRosenNumberPartitionIterator() {
    RosenNumberPartitionIterator i = new RosenNumberPartitionIterator(10, 4);
    StringBuilder buf = new StringBuilder(256);
    while (i.hasNext()) {
      int[] t = i.next();
      for (int j = 0; j < t.length; j++) {
        // System.out.print(t(j));
        buf.append(t[j]);
        buf.append(" ");
      }
      // System.out.println();
      buf.append("|");
    }
    assertEquals(
        "1 1 1 7 |1 1 2 6 |1 1 3 5 |1 1 4 4 |1 1 5 3 |1 1 6 2 |1 1 7 1 |1 2 1 6 |1 2 2 5 |1 2 3 4 |1 2 4 3 |1 2 5 2 |1 2 6 1 |1 3 1 5 |1 3 2 4 |1 3 3 3 |1 3 4 2 |1 3 5 1 |1 4 1 4 |1 4 2 3 |1 4 3 2 |1 4 4 1 |1 5 1 3 |1 5 2 2 |1 5 3 1 |1 6 1 2 |1 6 2 1 |1 7 1 1 |2 1 1 6 |2 1 2 5 |2 1 3 4 |2 1 4 3 |2 1 5 2 |2 1 6 1 |2 2 1 5 |2 2 2 4 |2 2 3 3 |2 2 4 2 |2 2 5 1 |2 3 1 4 |2 3 2 3 |2 3 3 2 |2 3 4 1 |2 4 1 3 |2 4 2 2 |2 4 3 1 |2 5 1 2 |2 5 2 1 |2 6 1 1 |3 1 1 5 |3 1 2 4 |3 1 3 3 |3 1 4 2 |3 1 5 1 |3 2 1 4 |3 2 2 3 |3 2 3 2 |3 2 4 1 |3 3 1 3 |3 3 2 2 |3 3 3 1 |3 4 1 2 |3 4 2 1 |3 5 1 1 |4 1 1 4 |4 1 2 3 |4 1 3 2 |4 1 4 1 |4 2 1 3 |4 2 2 2 |4 2 3 1 |4 3 1 2 |4 3 2 1 |4 4 1 1 |5 1 1 3 |5 1 2 2 |5 1 3 1 |5 2 1 2 |5 2 2 1 |5 3 1 1 |6 1 1 2 |6 1 2 1 |6 2 1 1 |7 1 1 1 |", //
        buf.toString());
  }


  @Test
  public void testFiniteGroupCount001() {
    // Base cases and explicitly known small group counts
    check("FiniteGroupCount(1)", "1");
    check("FiniteGroupCount(2)", "1");
    check("FiniteGroupCount(3)", "1");

    // Order 4: Z4, Z2 x Z2
    check("FiniteGroupCount(4)", "2");

    // Order 8: Z8, Z4 x Z2, Z2 x Z2 x Z2, D4, Q8
    check("FiniteGroupCount(8)", "5");

    // Order 15: Z15
    check("FiniteGroupCount(15)", "1");

    // Order 16
    check("FiniteGroupCount(16)", "14");

    // Upper boundary of the lookup table
    check("FiniteGroupCount(2047)", "1");
  }

  @Test
  public void testFiniteGroupCount002() {
    // Out of bounds / invalid inputs should remain unevaluated
    check("FiniteGroupCount(2048)", "FiniteGroupCount(2048)");
    check("FiniteGroupCount(5000)", "FiniteGroupCount(5000)");

    check("FiniteGroupCount(0)", "FiniteGroupCount(0)");
    check("FiniteGroupCount(-5)", "FiniteGroupCount(-5)");
    check("FiniteGroupCount(x)", "FiniteGroupCount(x)");
    check("FiniteGroupCount(8.0)", "FiniteGroupCount(8.0)");

  }

  @Test
  public void testCyclicNumbers() {
    // A number n is cyclic if GCD(n, EulerPhi(n)) == 1.
    // In this case, there is exactly 1 group of order n (the cyclic group Z_n).

    // n = 2739 (3 * 11 * 83).
    // EulerPhi(2739) = 2 * 10 * 82 = 1640.
    // GCD(2739, 1640) == 1.
    check("FiniteGroupCount(2739)", "1");

    // n = 4255 (5 * 23 * 37)
    // EulerPhi(4255) = 4 * 22 * 36 = 3168.
    // GCD(4255, 3168) == 1.
    check("FiniteGroupCount(4255)", "1");
  }

  @Test
  public void testPrimePowers() {
    // Single prime p (p^1) -> exactly 1 group
    // n = 2053 (a prime number > 2048)
    check("FiniteGroupCount(2053)", "1");

    // Prime squared (p^2) -> exactly 2 groups (Z_{p^2} and Z_p x Z_p)
    // n = 47^2 = 2209
    check("FiniteGroupCount(2209)", "2");

    // Prime cubed (p^3) -> exactly 5 groups
    // n = 13^3 = 2197
    check("FiniteGroupCount(2197)", "5");

    // Prime to the 4th power (p^4) -> exactly 15 groups for p > 2
    // n = 7^4 = 2401
    check("FiniteGroupCount(2401)", "15");
  }

  @Test
  public void testSemiprimes() {
    // Semiprime n = p * q (where p > q).
    // If q divides p - 1, there are 2 groups. Otherwise, there is 1 group.

    // Case 1: q divides (p - 1) -> 2 groups
    // Let q = 3. We need a prime p = 1 (mod 3). Let p = 733.
    // n = 3 * 733 = 2199. 3 divides 732.
    check("FiniteGroupCount(2199)", "2");

    // Let q = 5. We need a prime p = 1 (mod 5). Let p = 431.
    // n = 5 * 431 = 2155. 5 divides 430.
    check("FiniteGroupCount(2155)", "2");

    // Case 2: q does NOT divide (p - 1) -> 1 group
    // Let q = 3. We need a prime p = 2 (mod 3). Let p = 743.
    // n = 3 * 743 = 2229. 3 does not divide 742.
    check("FiniteGroupCount(2229)", "1");
  }

  @Test
  public void testUnevaluatedComplexOrders() {
    // For numbers beyond the lookup table that don't fit the simple
    // algorithmic rules, the function should safely remain unevaluated.

    // n = 2048 = 2^11. Our prime power logic stops at p^4.
    check("FiniteGroupCount(2048)", "FiniteGroupCount(2048)");

    // n = 2052 = 2^2 * 3^3 * 19. Highly composite, no simple theorem applies.
    check("FiniteGroupCount(2052)", "FiniteGroupCount(2052)");
  }

  @Test
  public void testFiniteAbelianGroupCount001() {
    // Order 1: Trivial group
    check("FiniteAbelianGroupCount(1)", "1");

    // Prime orders
    check("FiniteAbelianGroupCount(2)", "1");
    check("FiniteAbelianGroupCount(7)", "1");

    // Composite and prime power orders
    check("FiniteAbelianGroupCount(4)", "2");
    check("FiniteAbelianGroupCount(8)", "3");
    check("FiniteAbelianGroupCount(16)", "5");
    check("FiniteAbelianGroupCount(36)", "4");

    // Large composite order (100000 = 2^5 * 5^5 -> P(5) * P(5) = 7 * 7 = 49)
    check("FiniteAbelianGroupCount(100000)", "49");
  }

  @Test
  public void testFiniteAbelianGroupCount002() {
    // Out of bounds / invalid inputs should remain unevaluated
    check("FiniteAbelianGroupCount(0)", "FiniteAbelianGroupCount(0)");
    check("FiniteAbelianGroupCount(-10)", "FiniteAbelianGroupCount(-10)");
    check("FiniteAbelianGroupCount(n)", "FiniteAbelianGroupCount(n)");
    check("FiniteAbelianGroupCount(36.0)", "FiniteAbelianGroupCount(36.0)");

  }


  @Test
  public void testDeBruijnSequence001() {
    // Integer alphabet sizes: DeBruijnSequence(k, n) -> implies alphabet {0, 1, ..., k-1}

    // k=2, n=3 (binary alphabet, length 3 combinations)
    check("DeBruijnSequence(2, 3)", //
        "{0,0,0,1,0,1,1,1}");

    // k=3, n=2 (ternary alphabet, length 2 combinations)
    check("DeBruijnSequence(3, 2)", //
        "{0,0,1,0,2,1,1,2,2}");

    // k=2, n=4
    check("DeBruijnSequence(2, 4)", //
        "{0,0,0,0,1,0,0,1,1,0,1,0,1,1,1,1}");

    // Base cases for k=1 and k=0
    check("DeBruijnSequence(1, 5)", //
        "{0}");
    check("DeBruijnSequence(0, 3)", //
        "{}");
  }


  @Test
  public void testDeBruijnSequence002() {
    check("DeBruijnSequence(\"abcd\", 2)", //
        "aabacadbbcbdccdd");

    // Explicit alphabet lists: DeBruijnSequence({a, b, ...}, n)

    // Character/Symbol alphabet
    check("DeBruijnSequence({a, b}, 3)", //
        "{a,a,a,b,a,b,b,b}");
    check("DeBruijnSequence({x, y, z}, 2)", //
        "{x,x,y,x,z,y,y,z,z}");

    // Explicit integer values in the alphabet list
    check("DeBruijnSequence({10, 20}, 3)", //
        "{10,10,10,20,10,20,20,20}");

    // Base cases for single element and empty list
    check("DeBruijnSequence({a}, 4)", //
        "{a}");
    check("DeBruijnSequence({}, 4)", //
        "{}");
  }


  @Test
  public void testDeBruijnSequence003() {
    // Invalid inputs that should remain unevaluated (F.NIL fallback)

    // Negative or zero 'n' (length of sub-sequences)
    check("DeBruijnSequence(2, 0)", //
        "DeBruijnSequence(2,0)");
    check("DeBruijnSequence({a, b}, -2)", //
        "DeBruijnSequence({a,b},-2)");

    // Negative 'k' (alphabet size)
    check("DeBruijnSequence(-2, 3)", //
        "DeBruijnSequence(-2,3)");

    // Non-integer sequence lengths
    check("DeBruijnSequence(2,3.5)", //
        "DeBruijnSequence(2,3.5)");
    check("DeBruijnSequence(2, n)", //
        "DeBruijnSequence(2,n)");

    // Symbolic alphabet size instead of list/integer
    check("DeBruijnSequence(k, 3)", //
        "DeBruijnSequence(k,3)");

    // Wrong number of arguments
    check("DeBruijnSequence()", //
        "DeBruijnSequence()");
    check("DeBruijnSequence(2)", //
        "DeBruijnSequence(2)");
    check("DeBruijnSequence(2, 3, 4)", //
        "DeBruijnSequence(2,3,4)");
  }
}
