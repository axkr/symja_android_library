package org.matheclipse.io.system;

import org.matheclipse.core.combinatoric.MultisetPartitionsIterator;
import org.matheclipse.core.combinatoric.RosenNumberPartitionIterator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.patternmatching.FlatOrderlessStepVisitor;
import org.matheclipse.core.patternmatching.IPatternMap;
import org.matheclipse.core.patternmatching.PatternMatcher;

/** Tests for combinatorial functions */
public class CombinatoricTestCase extends AbstractTestCase {
  public CombinatoricTestCase(String name) {
    super(name);
  }

  /** Test combinatorial functions */
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
    check("Cycles({SparseArray[{1 -> 1000, 2 -> 10}], {100, 1, 10000}})", //
        "Cycles({{1,10000,100},{10,1000}})");
  }

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

  public void testPolygonalNumber() {
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

  public void testPermutatins() {
    check("Permutations({1,2,1})", //
        "{{1,2,1},{1,1,2},{2,1,1}}");
  }

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
        iter.initPatternMap();
      }
    }
    assertEquals(true, b);
  }

  public static void testRosenNumberPartitionIterator() {
    RosenNumberPartitionIterator i = new RosenNumberPartitionIterator(10, 4);
    StringBuilder buf = new StringBuilder(256);
    while (i.hasNext()) {
      int[] t = i.next();
      for (int j = 0; j < t.length; j++) {
        // System.out.print(t[j]);
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
}
