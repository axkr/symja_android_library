package org.matheclipse.io.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;

/** Tests for SparseArray functions */
public class SparseArrayTest extends AbstractTestCase {

  public SparseArrayTest(String name) {
    super(name);
  }

  public void testArrayRules() {
    check("a = {{{0,0},{1,1}},{{0,1},{0,1}}}", //
        "{{{0,0},{1,1}},{{0,1},{0,1}}}");
    check("ArrayRules(a)", //
        "{{1,2,1}->1,{1,2,2}->1,{2,1,2}->1,{2,2,2}->1,{_,_,_}->0}");

    check("s1=SparseArray({{1, 1} -> 1, {2, 2} -> 2, {4, 3} -> 3, {1, 4} -> 4, {3, 5} -> 2} )", //
        "SparseArray(Number of elements: 5 Dimensions: {4,5} Default value: 0)");
    check("ar=ArrayRules(s1)", //
        "{{1,1}->1,{1,4}->4,{2,2}->2,{3,5}->2,{4,3}->3,{_,_}->0}");
    check("s2=SparseArray(ar)", //
        "SparseArray(Number of elements: 5 Dimensions: {4,5} Default value: 0)");
    check("s1===s2", //
        "True");
  }

  public void testAssociateTo() {
    check("data=<||>;\n" + "Do(\n" + //
        "   c = RandomInteger({1, 10});\n" + //
        "   PossibleColumns = RandomSample(Range(i + 1, 100), Min(100 - i - 1, 30));\n" + //
        "   Do(\n" + //
        "      AssociateTo(data, {PossibleColumns[[j]],i} -> c/RandomInteger({1, 10}));\n" + //
        "      AssociateTo(data, {i,PossibleColumns[[j]]} -> c/RandomInteger({1, 10}));\n" + //
        "      ,\n" + //
        "   {j, 1, Length(PossibleColumns)}\n" + //
        "   ),\n" + //
        "{i, 1, 99}\n" + //
        ");SparseArray(Normal(data));", //
        // SparseArray(Number of elements: 5010 Dimensions: {100,100} Default value: 0)
        "");
  }

  // public void testCoefficientArrays() {
  // // TODO
  // check(
  // "CoefficientArrays(2*x + 3*y^2 + 4*z + 5, {x, y, z}) // Normal", //
  // "{5,{2,0,4},{{0,0,0},{0,3,0},{0,0,0}}}");
  // check(
  // "CoefficientArrays(2*x + 3*y + 4*z + 5, {x, y, z}) // Normal", //
  // "{5,{2,3,4}}");
  //
  // check(
  // "CoefficientArrays({a + x - y - z == 0, b + x + 2 y + z == 0}, {x, y, z})", //
  // " ");
  //
  // check(
  // "CoefficientList({a + x - y - z , b + x + 2 y + z}, {x, y, z})", //
  // "{{{{a,-1},{-1,0}},{{1,0},{0,0}}},{{{b,1},{2,0}},{{1,0},{0,0}}}}");
  // check(
  // "CoefficientRules({a + x - y - z, b + x + 2 y + z}, {x, y, z})", //
  // "{{{1,0,0}->1,{0,1,0}->-1,{0,0,1}->-1,{0,0,0}->a},{{1,0,0}->1,{0,1,0}->2,{0,0,1}->\n"
  // + "1,{0,0,0}->b}}");
  // check(
  // "CoefficientRules(a+x - y - z, {x, y, z})", //
  // "{{1,0,0}->1,{0,1,0}->-1,{0,0,1}->-1,{0,0,0}->a}");
  // check(
  // "SparseArray({{2,1,1}->1,{1,2,1}->-1,{1,1,2}->-1,{1,1,1}->a}) // Normal", //
  // "{{{a,-1},{-1,0}},{{1,0},{0,0}}}");
  // }

  public void testDiagonalMatrix() {
    check("DiagonalMatrix(SparseArray(Range(3))) // MatrixForm", //
        "{{1,0,0},\n" //
            + " {0,2,0},\n" //
            + " {0,0,3}}");
    check("DiagonalMatrix(SparseArray(Range(4)),1)// MatrixForm", //
        "{{0,1,0,0},\n" //
            + " {0,0,2,0},\n" //
            + " {0,0,0,3},\n" //
            + " {0,0,0,0}}");
    check("DiagonalMatrix(SparseArray(Range(4)),-2)// MatrixForm", //
        "{{0,0,0,0},\n" //
            + " {0,0,0,0},\n" //
            + " {3,0,0,0},\n" //
            + " {0,4,0,0}}"); //
    check("DiagonalMatrix(SparseArray(Range(3))) // MatrixForm", //
        "{{1,0,0},\n" //
            + " {0,2,0},\n" //
            + " {0,0,3}}");
  }

  public void testDot() {
    check("s=SparseArray({{a, b}, {c, d}}).SparseArray({{u, v}, {w, x}}) ", //
        "SparseArray(Number of elements: 4 Dimensions: {2,2} Default value: 0)");
    check("s // Normal", //
        "{{a*u+b*w,a*v+b*x},\n" //
            + " {c*u+d*w,c*v+d*x}}");

    check("{1,2,3.0}.SparseArray({4,5.0,6}) ", //
        "32.0");
    check("SparseArray({1,2,3.0}).{4,5.0,6} ", //
        "32.0");
    check("{{1, 2}, {3, 4}, {5, 6}}.SparseArray({{1},{1}}) ", //
        "{{3},\n" + " {7},\n" + " {11}}");
    check("s=SparseArray({{1, 2}, {3, 4}, {5, 6}}).{{1},{1}}", //
        "SparseArray(Number of elements: 3 Dimensions: {3,1} Default value: 0)");
    check("s // Normal", //
        "{{3},\n" //
            + " {7},\n" + " {11}}");
    check("s=SparseArray({{1, 2}, {3.0, 4}, {5, 6}}).SparseArray({1,1})", //
        "SparseArray(Number of elements: 3 Dimensions: {3} Default value: 0)");
    check("s // Normal", //
        "{3,7.0,11}");
    check("SparseArray({1,1,1}).SparseArray({{1, 2}, {3.0, 4}, {5, 6}}) // Normal", //
        "{9.0,12}");
    check("s=SparseArray({{1, 2}, {3, 4}, {5, 6}}).SparseArray({{1},{1}}) ", //
        "SparseArray(Number of elements: 3 Dimensions: {3,1} Default value: 0)");
    check("s // Normal", //
        "{{3},\n" + " {7},\n" + " {11}}");
  }

  public void testFlatten() {
    check(
        "u=SparseArray(Automatic, {2, 2, 3}, 0, {1, {{0, 4, 7}, {{1, 3}, {2, 1}, {2, 2}, {2, 3}, {1, 2}, {2, 2}, {2, 3}}}, {3, 1, 1, 5, 1, 1, 2}})", //
        "SparseArray(Number of elements: 7 Dimensions: {2,2,3} Default value: 0)");
    check("f=Flatten(u)", //
        "SparseArray(Number of elements: 7 Dimensions: {12} Default value: 0)");
    check("MatrixForm(u)", //
        "{{{0,0,3},{1,1,5}},\n" + //
            " {{0,1,0},{0,1,2}}}");
    check("ArrayRules(u)", //
        "{{1,1,3}->3,{1,2,1}->1,{1,2,2}->1,{1,2,3}->5,{2,1,2}->1,{2,2,2}->1,{2,2,3}->2,{_,_,_}->\n"
            + "0}");
    check("MatrixForm(f)", //
        "{0,0,3,1,1,5,0,1,0,0,1,2}");
  }

  public void testFullForm() {
    check("t=SparseArray({{1}->1,{2}->2,{4}->3,{1}->4,{3}->2},Automatic,0)", //
        "SparseArray(Number of elements: 4 Dimensions: {4} Default value: 0)");
    check("FullForm(t)", //
        "SparseArray(Automatic, List(4), 0, List(1, List(List(0, 4), List(List(1), List(2), List(3), List(4))), List(1, 2, 2, 3)))");
    check("MatrixForm(t)", //
        "{1,2,2,3}");

    check(
        "u=SparseArray(Automatic, List(4), 0, List(1, List(List(0, 4), List(List(1), List(2), List(4), List(3))), List(1, 2, 3, 2)))", //
        "SparseArray(Number of elements: 4 Dimensions: {4} Default value: 0)");
    // Symja FullForm returns non-zero vector elements in order:
    check("FullForm(u)", //
        "SparseArray(Automatic, List(4), 0, List(1, List(List(0, 4), List(List(1), List(2), List(3), List(4))), List(1, 2, 2, 3)))");

    check(
        "u=SparseArray(Automatic, {2, 2, 3}, 0, {1, {{0, 4, 7}, {{1, 3}, {2, 1}, {2, 2}, {2, 3}, {1, 2}, {2, 2}, {2, 3}}}, {3, 1, 1, 5, 1, 1, 2}})", //
        "SparseArray(Number of elements: 7 Dimensions: {2,2,3} Default value: 0)");
    // {{{0,0,3},{1,1,5}},{{0,1,0},{0,1,2}}}
    check("FullForm(u)", //
        "SparseArray(Automatic, List(2, 2, 3), 0, List(1, List(List(0, 4, 7), List(List(1, 3), List(2, 1), List(2, 2), List(2, 3), List(1, 2), List(2, 2), List(2, 3))), List(3, 1, 1, 5, 1, 1, 2)))");
  }

  public void testIdentityMatrix() {
    check("IdentityMatrix(4,SparseArray)", //
        "SparseArray(Number of elements: 4 Dimensions: {4,4} Default value: 0)");
    check("IdentityMatrix(3,SparseArray) // MatrixForm", //
        "{{1,0,0},\n" //
            + " {0,1,0},\n" //
            + " {0,0,1}}");
  }

  public void testNormal() {
    check("s=SparseArray({11 -> a, 17 -> b})", //
        "SparseArray(Number of elements: 2 Dimensions: {17} Default value: 0)");
    check("Normal(s)", //
        "{0,0,0,0,0,0,0,0,0,0,a,0,0,0,0,0,b}");
  }

  public void testNormMatrix() {
    check("s = SparseArray({{1, 1} -> 1, {2, 2} -> 2, {3, 3} -> 3, {1, 3} -> 4}, Automatic, 0)", //
        "SparseArray(Number of elements: 4 Dimensions: {3,3} Default value: 0)");
    check("s = SparseArray({{1, 1} -> 1, {2, 2} -> 2, {3, 3} -> 3, {1, 3} -> 4})", //
        "SparseArray(Number of elements: 4 Dimensions: {3,3} Default value: 0)");
    check("t=Norm(s)", //
        "5.0645");
    check("f=Norm(s,\"Frobenius\")", //
        "Sqrt(30)");
    check("Sqrt(30) // N", //
        "5.47723");
    check("Norm[Flatten[s]] // N", //
        "5.47723");
    check("Normal(s)", //
        "{{1,0,4},\n" //
            + " {0,2,0},\n" + " {0,0,3}}");
  }

  public void testNormVector() {
    check("v = {1, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1}", //
        "{1,0,1,0,0,1,0,0,0,1,0,0,0,0,1}");
    check("s = SparseArray(v)", //
        "SparseArray(Number of elements: 5 Dimensions: {15} Default value: 0)");
    check("t=Norm(s)", //
        "Sqrt(5)");
  }

  public void testPart() {
    check("s=SparseArray({{1,1}->1,{2,2}->2,{4,3}->3,{1,4}->4,{3,5}->2},Automatic,0)", //
        "SparseArray(Number of elements: 5 Dimensions: {4,5} Default value: 0)");
    check("t=s[[1 ;; 3, 1 ;; 3]]", //
        "SparseArray(Number of elements: 2 Dimensions: {3,3} Default value: 0)");
    check("t // MatrixForm", //
        "{{1,0,0},\n" + //
            " {0,2,0},\n" + //
            " {0,0,0}}");

    check("t=Part[s,1,All]", //
        "SparseArray(Number of elements: 2 Dimensions: {5} Default value: 0)");
    check("t // MatrixForm", //
        "{1,0,0,4,0}");
  }

  public void testPlus() {
    check("SparseArray({11,1,19,2})+SparseArray({5,7,1,23}) // MatrixForm", //
        "{16,8,20,25}");
  }

  public void testTimes() {
    check("s=SparseArray({{1,1}->1,{2,2}->2,{4,3}->3,{1,4}->4,{3,5}->2},Automatic,0)", //
        "SparseArray(Number of elements: 5 Dimensions: {4,5} Default value: 0)");
    check("r=2*s", //
        "SparseArray(Number of elements: 5 Dimensions: {4,5} Default value: 0)");
    check("r // MatrixForm", //
        "{{2,0,0,8,0},\n" + //
            " {0,4,0,0,0},\n" + //
            " {0,0,0,0,4},\n" + //
            " {0,0,6,0,0}}");
  }

  public void testTotal() {
    check("s=SparseArray({{1,1}->1,{2,2}->2,{4,3}->3,{1,4}->4,{3,5}->2},Automatic,0)", //
        "SparseArray(Number of elements: 5 Dimensions: {4,5} Default value: 0)");
    check("s // MatrixForm", //
        "{{1,0,0,4,0},\n" + //
            " {0,2,0,0,0},\n" + //
            " {0,0,0,0,2},\n" + //
            " {0,0,3,0,0}}");
    check("Total(s,2)", //
        "12");
    check("Total(s,17)", //
        "12");

    check("t=SparseArray({{1}->1,{2}->2,{4}->3,{1}->4,{3}->2},Automatic,0)", //
        "SparseArray(Number of elements: 4 Dimensions: {4} Default value: 0)");
    check("Total(t,1)", //
        "8");
    check("Total(t,3)", //
        "8");

    check("s = SparseArray({{i_, i_} -> -2, {i_, j_} /; Abs(i - j) == 1 -> 1}, {10, 10})", //
        "SparseArray(Number of elements: 28 Dimensions: {10,10} Default value: 0)");
    check("Total(s)", //
        "{-1,0,0,0,0,0,0,0,0,-1}");
  }

  public void testVector01() {
    check("s=SparseArray(Automatic, {4}, 0, {1, {{0, 2}, {{1}, {3}}}, {1, 1}})", //
        "SparseArray(Number of elements: 2 Dimensions: {4} Default value: 0)");
    check("s // Normal", //
        "{1,0,1,0}");
  }

  public void testSparseArray001() {
    check(
        "s=SparseArray[Automatic, {5}, 0, {1, {{0, 5}, {{1}, {2}, {3}, {4}, {5}}},  {1, 2, 3, 4, 5}}]", //
        "SparseArray(Number of elements: 5 Dimensions: {5} Default value: 0)");
    check("ArrayRules(s)", //
        "{{1}->1,{2}->2,{3}->3,{4}->4,{5}->5,{_}->0}");
    check("s = SparseArray({i_} -> i, {5})", //
        "SparseArray(Number of elements: 5 Dimensions: {5} Default value: 0)");
    check("ArrayRules(s)", //
        "{{1}->1,{2}->2,{3}->3,{4}->4,{5}->5,{_}->0}");

    check(
        "s = SparseArray(Automatic, {5}, 0, {1, {{0, 5}, {{1}, {2}, {3}, {4}, {5}}},  {1, 2, 3, 4, 5}})", //
        "SparseArray(Number of elements: 5 Dimensions: {5} Default value: 0)");

    check("u=SparseArray(Automatic, {2, 2, 3}, 0, {1, {{0, 0, 2}, {{2, 2}, {2, 3}}}, {1, 2}})", //
        "SparseArray(Number of elements: 2 Dimensions: {2,2,3} Default value: 0)");
    check("ArrayRules(u)", //
        "{{2,2,2}->1,{2,2,3}->2,{_,_,_}->0}");

    check(
        "u=SparseArray(Automatic, {2, 2, 3}, 0, {1, {{0, 4, 7}, {{1, 3}, {2, 1}, {2, 2}, {2, 3}, {1, 2}, {2, 2}, {2, 3}}}, {3, 1, 1, 5, 1, 1, 2}})", //
        "SparseArray(Number of elements: 7 Dimensions: {2,2,3} Default value: 0)");
    check("ArrayRules(u)", //
        "{{1,1,3}->3,{1,2,1}->1,{1,2,2}->1,{1,2,3}->5,{2,1,2}->1,{2,2,2}->1,{2,2,3}->2,{_,_,_}->\n"
            + //
            "0}");
    check("MatrixForm(u)", //
        "{{{0,0,3},{1,1,5}},\n" + //
            " {{0,1,0},{0,1,2}}}");

    check("s = SparseArray({{i_, i_} -> -2, {i_, j_} /; Abs(i - j) == 1 -> 1}, {100, 100})", //
        "SparseArray(Number of elements: 298 Dimensions: {100,100} Default value: 0)");
    check("s = SparseArray({{i_, i_} -> -2, {i_, j_} /; Abs(i - j) == 1 -> 1}, {5, 5})", //
        "SparseArray(Number of elements: 13 Dimensions: {5,5} Default value: 0)");
    check("MatrixForm(s)", //
        "{{-2,1,0,0,0},\n" + " {1,-2,1,0,0},\n" + " {0,1,-2,1,0},\n" + " {0,0,1,-2,1},\n"
            + " {0,0,0,1,-2}}");

    check("SparseArray(Table({2^i, 3^i + i} -> 1, {i, 10}))", //
        "SparseArray(Number of elements: 10 Dimensions: {1024,59059} Default value: 0)");
    check("r=SparseArray({{1, 1} -> 1, {2, 2} -> 2, {4, 3} -> 3, {1, 4} -> 4, {3, 5} -> 2})", //
        "SparseArray(Number of elements: 5 Dimensions: {4,5} Default value: 0)");
    check("r[[1,All]]", //
        "SparseArray(Number of elements: 2 Dimensions: {5} Default value: 0)");
    check("r[[{1},All]]", //
        "SparseArray(Number of elements: 2 Dimensions: {1,5} Default value: 0)");
    check("Transpose(r[[{1},All]])", //
        "SparseArray(Number of elements: 2 Dimensions: {5,1} Default value: 0)");
    check("SparseArray({{1,1,1,1,1}}).Transpose(r[[{1},All]]) ", //
        "SparseArray(Number of elements: 1 Dimensions: {1,1} Default value: 0)");
    check("r=SparseArray({{{0,0,3},{1,1,5}},{{0,1,0},{0,1,2}}})", //
        "SparseArray(Number of elements: 7 Dimensions: {2,2,3} Default value: 0)");
    check("ArrayRules(r)", //
        "{{1,1,3}->3,{1,2,1}->1,{1,2,2}->1,{1,2,3}->5,{2,1,2}->1,{2,2,2}->1,{2,2,3}->2,{_,_,_}->\n"
            + "0}");
    check("r[[2,All]] // Normal", //
        "{{0,1,0},\n" //
            + " {0,1,2}}");
    // index 3 does not exist
    check("r[[All,3]] // Normal", //
        "{{{0,0,3},{1,1,5}},{{0,1,0},{0,1,2}}}[[All,3]]");
    check("r[[All,1]] // Normal", //
        "{{0,0,3},\n" //
            + " {0,1,0}}");
    check("r[[All,All]] // Normal", //
        "{{{0,0,3},{1,1,5}},{{0,1,0},{0,1,2}}}");
    check("r[[1,All,3]] // Normal", //
        "{3,5}");
    check("s=SparseArray({{1, 1} -> 1, {2, 2} -> 2, {4, 3} -> 3, {1, 4} -> 4, {3, 5} -> 2} )", //
        "SparseArray(Number of elements: 5 Dimensions: {4,5} Default value: 0)");
    check("s[[1,1]]  ", //
        "1");
    check("s[[1,1,2]]  ", //
        "(SparseArray(Number of elements: 5 Dimensions: {4,5} Default value: 0))[[1,1,2]]");
    check("s[[All,1]] // Normal", //
        "{1,0,0,0}");
    check("s[[All,2]] // Normal", //
        "{0,2,0,0}");
    check("s[[2,All ]] // Normal", //
        "{0,2,0,0,0}");
    check("Normal(s)", //
        "{{1,0,0,4,0},\n" //
            + " {0,2,0,0,0},\n" + " {0,0,0,0,2},\n" + " {0,0,3,0,0}}");
    check("Normal(SparseArray({{1, 1} -> 1, {1, 1} -> 2}))", //
        "{{1}}");
    check("Normal(SparseArray({1 -> 2, 10 -> 7, 3 -> 2}))", //
        "{2,0,2,0,0,0,0,0,0,7}");
    check("s=SparseArray({3, 3} -> 1, 5)", //
        "SparseArray(Number of elements: 1 Dimensions: {5,5} Default value: 0)");
    check("Normal(s)", //
        "{{0,0,0,0,0},\n" //
            + " {0,0,0,0,0},\n" + " {0,0,1,0,0},\n" + " {0,0,0,0,0},\n" + " {0,0,0,0,0}}");
    check("Normal(SparseArray(10 -> 1, 19))", //
        "{0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0}");
    check("s = SparseArray({{1, 1} -> 1, {2, 2} -> 2, {3, 3} -> 3, {1, 3} -> 4})", //
        "SparseArray(Number of elements: 4 Dimensions: {3,3} Default value: 0)");
    check("Normal(s)", //
        "{{1,0,4},\n" //
            + " {0,2,0},\n" + " {0,0,3}}");
  }

  public void testSparseArray002() {
    check(
        "s=SparseArray(Automatic, {5}, 0, {1, {{0, 5}, {{1}, {2}, {3}, {4}, {5}}},  {1, 2, 3, 4, 5}})", //
        "SparseArray(Number of elements: 5 Dimensions: {5} Default value: 0)");
    check("SparseArray(s, {10}, 2)", //
        "SparseArray(Number of elements: 9 Dimensions: {10} Default value: 2)");
    check("SparseArray(s, Automatic, 2)", //
        "SparseArray(Number of elements: 4 Dimensions: {5} Default value: 2)");
    check("SparseArray(s)", //
        "SparseArray(Number of elements: 5 Dimensions: {5} Default value: 0)");
    check("SparseArray(s, Automatic, 0)", //
        "SparseArray(Number of elements: 5 Dimensions: {5} Default value: 0)");
    check("ArrayRules(s)", //
        "{{1}->1,{2}->2,{3}->3,{4}->4,{5}->5,{_}->0}");
    check("s = SparseArray({i_} -> i, {5})", //
        "SparseArray(Number of elements: 5 Dimensions: {5} Default value: 0)");
    check("ArrayRules(s)", //
        "{{1}->1,{2}->2,{3}->3,{4}->4,{5}->5,{_}->0}");

    check(
        "s = SparseArray(Automatic, {5}, 0, {1, {{0, 5}, {{1}, {2}, {3}, {4}, {5}}},  {1, 2, 3, 4, 5}})", //
        "SparseArray(Number of elements: 5 Dimensions: {5} Default value: 0)");

    check("u=SparseArray(Automatic, {2, 2, 3}, 0, {1, {{0, 0, 2}, {{2, 2}, {2, 3}}}, {1, 2}})", //
        "SparseArray(Number of elements: 2 Dimensions: {2,2,3} Default value: 0)");
    check("ArrayRules(u)", //
        "{{2,2,2}->1,{2,2,3}->2,{_,_,_}->0}");

    check(
        "u=SparseArray(Automatic, {2, 2, 3}, 0, {1, {{0, 4, 7}, {{1, 3}, {2, 1}, {2, 2}, {2, 3}, {1, 2}, {2, 2}, {2, 3}}}, {3, 1, 1, 5, 1, 1, 2}})", //
        "SparseArray(Number of elements: 7 Dimensions: {2,2,3} Default value: 0)");
    check("ArrayRules(u)", //
        "{{1,1,3}->3,{1,2,1}->1,{1,2,2}->1,{1,2,3}->5,{2,1,2}->1,{2,2,2}->1,{2,2,3}->2,{_,_,_}->\n"
            + //
            "0}");
    check("MatrixForm(u)", //
        "{{{0,0,3},{1,1,5}},\n" + //
            " {{0,1,0},{0,1,2}}}");

    check("s = SparseArray({{i_, i_} -> -2, {i_, j_} /; Abs(i - j) == 1 -> 1}, {100, 100})", //
        "SparseArray(Number of elements: 298 Dimensions: {100,100} Default value: 0)");
    check("s = SparseArray({{i_, i_} -> -2, {i_, j_} /; Abs(i - j) == 1 -> 1}, {5, 5})", //
        "SparseArray(Number of elements: 13 Dimensions: {5,5} Default value: 0)");
    check("MatrixForm(s)", //
        "{{-2,1,0,0,0},\n" + " {1,-2,1,0,0},\n" + " {0,1,-2,1,0},\n" + " {0,0,1,-2,1},\n"
            + " {0,0,0,1,-2}}");

    check("SparseArray(Table({2^i, 3^i + i} -> 1, {i, 10}))", //
        "SparseArray(Number of elements: 10 Dimensions: {1024,59059} Default value: 0)");
    check("r=SparseArray({{1, 1} -> 1, {2, 2} -> 2, {4, 3} -> 3, {1, 4} -> 4, {3, 5} -> 2})", //
        "SparseArray(Number of elements: 5 Dimensions: {4,5} Default value: 0)");
    check("r[[1,All]]", //
        "SparseArray(Number of elements: 2 Dimensions: {5} Default value: 0)");
    check("r[[{1},All]]", //
        "SparseArray(Number of elements: 2 Dimensions: {1,5} Default value: 0)");
    check("Transpose(r[[{1},All]])", //
        "SparseArray(Number of elements: 2 Dimensions: {5,1} Default value: 0)");
    check("SparseArray({{1,1,1,1,1}}).Transpose(r[[{1},All]]) ", //
        "SparseArray(Number of elements: 1 Dimensions: {1,1} Default value: 0)");
    check("r=SparseArray({{{0,0,3},{1,1,5}},{{0,1,0},{0,1,2}}})", //
        "SparseArray(Number of elements: 7 Dimensions: {2,2,3} Default value: 0)");
    check("ArrayRules(r)", //
        "{{1,1,3}->3,{1,2,1}->1,{1,2,2}->1,{1,2,3}->5,{2,1,2}->1,{2,2,2}->1,{2,2,3}->2,{_,_,_}->\n"
            + "0}");
    check("r[[2,All]] // Normal", //
        "{{0,1,0},\n" //
            + " {0,1,2}}");
    // index 3 does not exist
    check("r[[All,3]] // Normal", //
        "{{{0,0,3},{1,1,5}},{{0,1,0},{0,1,2}}}[[All,3]]");
    check("r[[All,1]] // Normal", //
        "{{0,0,3},\n" //
            + " {0,1,0}}");
    check("r[[All,All]] // Normal", //
        "{{{0,0,3},{1,1,5}},{{0,1,0},{0,1,2}}}");
    check("r[[1,All,3]] // Normal", //
        "{3,5}");
    check("s=SparseArray({{1, 1} -> 1, {2, 2} -> 2, {4, 3} -> 3, {1, 4} -> 4, {3, 5} -> 2} )", //
        "SparseArray(Number of elements: 5 Dimensions: {4,5} Default value: 0)");
    check("s[[1,1]]  ", //
        "1");
    check("s[[1,1,2]]  ", //
        "(SparseArray(Number of elements: 5 Dimensions: {4,5} Default value: 0))[[1,1,2]]");
    check("s[[All,1]] // Normal", //
        "{1,0,0,0}");
    check("s[[All,2]] // Normal", //
        "{0,2,0,0}");
    check("s[[2,All ]] // Normal", //
        "{0,2,0,0,0}");
    check("Normal(s)", //
        "{{1,0,0,4,0},\n" //
            + " {0,2,0,0,0},\n" + " {0,0,0,0,2},\n" + " {0,0,3,0,0}}");
    check("Normal(SparseArray({{1, 1} -> 1, {1, 1} -> 2}))", //
        "{{1}}");
    check("Normal(SparseArray({1 -> 2, 10 -> 7, 3 -> 2}))", //
        "{2,0,2,0,0,0,0,0,0,7}");
    check("s=SparseArray({3, 3} -> 1, 5)", //
        "SparseArray(Number of elements: 1 Dimensions: {5,5} Default value: 0)");
    check("Normal(s)", //
        "{{0,0,0,0,0},\n" //
            + " {0,0,0,0,0},\n" + " {0,0,1,0,0},\n" + " {0,0,0,0,0},\n" + " {0,0,0,0,0}}");
    check("Normal(SparseArray(10 -> 1, 19))", //
        "{0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0}");
    check("s = SparseArray({{1, 1} -> 1, {2, 2} -> 2, {3, 3} -> 3, {1, 3} -> 4})", //
        "SparseArray(Number of elements: 4 Dimensions: {3,3} Default value: 0)");
    check("Normal(s)", //
        "{{1,0,4},\n" //
            + " {0,2,0},\n" + " {0,0,3}}");
  }

  public void testSparseArrayPattern() {
    check("SparseArray({{i_, j_} /; (i > j+1)-> (i+j ) }, {4, 4}) // MatrixForm", //
        "{{0,0,0,0},\n" //
            + " {0,0,0,0},\n" + " {4,0,0,0},\n" + " {5,6,0,0}}");

    check("SparseArray({{6, _} -> 11.5, {_, 6} -> 21.5, {i_, i_} -> i}, {10, 10}) // MatrixForm", //
        "{{1,0,0,0,0,21.5,0,0,0,0},\n" + " {0,2,0,0,0,21.5,0,0,0,0},\n"
            + " {0,0,3,0,0,21.5,0,0,0,0},\n" + " {0,0,0,4,0,21.5,0,0,0,0},\n"
            + " {0,0,0,0,5,21.5,0,0,0,0},\n"
            + " {11.5,11.5,11.5,11.5,11.5,11.5,11.5,11.5,11.5,11.5},\n"
            + " {0,0,0,0,0,21.5,7,0,0,0},\n" + " {0,0,0,0,0,21.5,0,8,0,0},\n"
            + " {0,0,0,0,0,21.5,0,0,9,0},\n" + " {0,0,0,0,0,21.5,0,0,0,10}}");

    check("s=SparseArray({{6, _} -> 11.5, {_, 6} -> 21.5, {i_, i_} -> i}, {50, 50})", //
        "SparseArray(Number of elements: 148 Dimensions: {50,50} Default value: 0)");
    check("Eigenvalues(s, 4)", //
        "{130.066,-92.43031,49.78784,48.74938}");
    check("Eigenvalues(s, -4)", //
        "{4.32993,3.29103,2.25761,1.21517}");
  }

  public void testSparseArrayOfSparseArray01() {
    check("s=SparseArray({SparseArray({11,1,19,2}),SparseArray({11,1,19,2})})", //
        "SparseArray(Number of elements: 8 Dimensions: {2,4} Default value: 0)");
    check("s // Normal", //
        "{{11,1,19,2},\n" //
            + " {11,1,19,2}}");
  }

  /** The JUnit setup method */
  @Override
  protected void setUp() {
    super.setUp();
    Config.SHORTEN_STRING_LENGTH = 1024;
    Config.MAX_AST_SIZE = 1000000;
    EvalEngine.get().setIterationLimit(50000);
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    Config.SHORTEN_STRING_LENGTH = 80;
  }
}
