package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;
import org.hipparchus.linear.FieldMatrix;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.data.SparseArrayExpr.SparseExprMatrix;
import org.matheclipse.core.expression.data.SparseArrayExpr.SparseExprVector;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.trie.Trie;
import org.matheclipse.parser.trie.TrieBuilder;
import org.matheclipse.parser.trie.TrieMatch;
import org.matheclipse.parser.trie.TrieSequencerIntArray;

/** Tests for SparseArray functions */
public class SparseArrayTest extends ExprEvaluatorTestCase {

  @Test
  public void testArrayRules() {
    check("a = {{{0,0},{1,1}},{{0,1},{0,1}}}", //
        "{{{0,0},{1,1}},{{0,1},{0,1}}}");
    check("ArrayRules(a)", //
        "{{1,2,1}->1,{1,2,2}->1,{2,1,2}->1,{2,2,2}->1,{_,_,_}->0}");

    check("s1=SparseArray({{1, 1} -> 1, {2, 2} -> 2, {4, 3} -> 3, {1, 4} -> 4, {3, 5} -> 2} )", //
        "SparseArray(Number of elements: 5 Dimensions: {4,5} Default value: 0)");
    check("ar=ArrayRules(s1, 2)", //
        "{{1,1}->1,{1,4}->4,{4,3}->3,{_,_}->2}");
    check("ar=ArrayRules(s1)", //
        "{{1,1}->1,{1,4}->4,{2,2}->2,{3,5}->2,{4,3}->3,{_,_}->0}");
    check("ar=ArrayRules(s1,0)", //
        "{{1,1}->1,{1,4}->4,{2,2}->2,{3,5}->2,{4,3}->3,{_,_}->0}");
    check("s2=SparseArray(ar)", //
        "SparseArray(Number of elements: 5 Dimensions: {4,5} Default value: 0)");
    check("s1===s2", //
        "True");
  }

  @Test
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

  // @Test
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

  @Test
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

  @Test
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

  @Test
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

  @Test
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

  @Test
  public void testIdentityMatrix() {
    check("IdentityMatrix(4,SparseArray)", //
        "SparseArray(Number of elements: 4 Dimensions: {4,4} Default value: 0)");
    check("IdentityMatrix(3,SparseArray) // MatrixForm", //
        "{{1,0,0},\n" //
            + " {0,1,0},\n" //
            + " {0,0,1}}");
  }

  @Test
  public void testNormal() {
    check("s=SparseArray({11 -> a, 17 -> b})", //
        "SparseArray(Number of elements: 2 Dimensions: {17} Default value: 0)");
    check("Normal(s)", //
        "{0,0,0,0,0,0,0,0,0,0,a,0,0,0,0,0,b}");
  }

  @Test
  public void testNormMatrix() {
    check("s = SparseArray({{1, 1} -> 1, {2, 2} -> 2, {3, 3} -> 3, {1, 3} -> 4}, Automatic, 0)", //
        "SparseArray(Number of elements: 4 Dimensions: {3,3} Default value: 0)");
    check("s = SparseArray({{1, 1} -> 1, {2, 2} -> 2, {3, 3} -> 3, {1, 3} -> 4})", //
        "SparseArray(Number of elements: 4 Dimensions: {3,3} Default value: 0)");
    check("t=Norm(s)", //
        "Sqrt(13+4*Sqrt(10))");
    check("Sqrt(13+4*Sqrt(10)) // N", //
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

  @Test
  public void testNormVector() {
    check("v = {1, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1}", //
        "{1,0,1,0,0,1,0,0,0,1,0,0,0,0,1}");
    check("s = SparseArray(v)", //
        "SparseArray(Number of elements: 5 Dimensions: {15} Default value: 0)");
    check("t=Norm(s)", //
        "Sqrt(5)");
  }

  @Test
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

  @Test
  public void testPlus() {
    check("SparseArray({11,1,19,2})+SparseArray({5,7,1,23}) // MatrixForm", //
        "{16,8,20,25}");
  }

  @Test
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

  @Test
  public void testTranspose() {
    check("s = SparseArray({{1, 1} -> 1, {2, 3} -> 4, {3, 1} -> -1})", //
        "SparseArray(Number of elements: 3 Dimensions: {3,3} Default value: 0)");
    check("t=Transpose(s)", //
        "SparseArray(Number of elements: 3 Dimensions: {3,3} Default value: 0)");
    check("t // Normal", //
        "{{1,0,-1},\n" //
            + " {0,0,0},\n" //
            + " {0,4,0}}");
    check("ArrayRules(t)", //
        "{{1,1}->1,{1,3}->-1,{3,2}->4,{_,_}->0}");
  }

  @Test
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

  @Test
  public void testVector01() {
    check("s=SparseArray(Automatic, {4}, 0, {1, {{0, 2}, {{1}, {3}}}, {1, 1}})", //
        "SparseArray(Number of elements: 2 Dimensions: {4} Default value: 0)");
    check("s // Normal", //
        "{1,0,1,0}");
  }

  @Test
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

  @Test
  public void testSparseArray002() {
    check(
        "s=SparseArray(Automatic, {5}, 0, {1, {{0, 5}, {{1}, {2}, {3}, {4}, {5}}},  {1, 2, 3, 4, 5}})", //
        "SparseArray(Number of elements: 5 Dimensions: {5} Default value: 0)");
    check("s // Normal", //
        "{1,2,3,4,5}");
    check("t=SparseArray(s, {10}, 2)", //
        "SparseArray(Number of elements: 4 Dimensions: {10} Default value: 2)");
    check("t // Normal", //
        "{1,2,3,4,5,2,2,2,2,2}");
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

  @Test
  public void testSparseArrayParts() {
    check("t=SparseArray(Table({2^i, 3^i + i} -> 1, {i, 3}))", //
        "SparseArray(Number of elements: 3 Dimensions: {8,30} Default value: 0)");
    check("Length(t)", //
        "8");
    check("Normal(t)", //
        "{{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},\n" //
            + " {0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},\n" //
            + " {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},\n" //
            + " {0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},\n" //
            + " {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},\n" //
            + " {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},\n" //
            + " {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},\n" //
            + " {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1}}");
    check("Most(t)", //
        "SparseArray(Number of elements: 2 Dimensions: {7,30} Default value: 0)");
    check("Most(t) // Normal", //
        "{{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},\n"//
            + " {0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},\n"//
            + " {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},\n"//
            + " {0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},\n"//
            + " {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},\n"//
            + " {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},\n"//
            + " {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}}");
    check("Rest(t)", //
        "SparseArray(Number of elements: 3 Dimensions: {7,30} Default value: 0)");
    check("Rest(t) // Normal", //
        "{{0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},\n" //
            + " {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},\n" //
            + " {0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},\n" //
            + " {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},\n" //
            + " {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},\n" //
            + " {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},\n" //
            + " {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1}}");

    check("First(t)", //
        "SparseArray(Number of elements: 0 Dimensions: {30} Default value: 0)");
    check("First(t) // Normal", //
        "{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}");
    check("Last(t)", //
        "SparseArray(Number of elements: 1 Dimensions: {30} Default value: 0)");
    check("Last(t) // Normal", //
        "{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1}");

  }

  @Test
  public void testSparseArrayQ() {
    check("s=SparseArray(SparseArray({1 -> a, 2 -> b}, {2}), {4}) ", //
        "SparseArray(Number of elements: 2 Dimensions: {4} Default value: 0)");
    check("SparseArrayQ(s)", //
        "True");
    check("s // Normal", //
        "{a,b,0,0}");
    check("s = SparseArray({{1, 1} -> 1, {2, 3} -> 4}, {5,5})", //
        "SparseArray(Number of elements: 2 Dimensions: {5,5} Default value: 0)");
    check("SparseArrayQ(s)", //
        "True");
    check("s // Normal", //
        "{{1,0,0,0,0},\n" //
            + " {0,0,4,0,0},\n" //
            + " {0,0,0,0,0},\n" //
            + " {0,0,0,0,0},\n" //
            + " {0,0,0,0,0}}");
    check("s = SparseArray({{1, 1} -> 1, {2, 3} -> 4, {3, 1} -> -1})", //
        "SparseArray(Number of elements: 3 Dimensions: {3,3} Default value: 0)");
    check("SparseArrayQ(s)", //
        "True");
    check("s // Normal", //
        "{{1,0,0},\n" //
            + " {0,0,4},\n" //
            + " {-1,0,0}}");
  }

  @Test
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

  @Test
  public void testSparseArrayOfSparseArray01() {
    check("s=SparseArray({SparseArray({11,1,19,2}),SparseArray({11,1,19,2})})", //
        "SparseArray(Number of elements: 8 Dimensions: {2,4} Default value: 0)");
    check("s // Normal", //
        "{{11,1,19,2},\n" //
            + " {11,1,19,2}}");
  }

  @Test
  public void testSparseMatrixVectorMultiplicationAPI() {
    // Create 3x3 sparse matrix
    SparseExprMatrix matrix = new SparseExprMatrix(3, 3, F.C0);
    matrix.setEntry(0, 0, F.num(2));
    matrix.setEntry(1, 1, F.num(3));
    matrix.setEntry(2, 2, F.num(4));
    matrix.setEntry(0, 2, F.num(5)); // M(0, 2) = 5

    // Create 3x1 sparse vector
    SparseExprVector vector = new SparseExprVector(3, F.C0);
    vector.setEntry(0, F.a);
    vector.setEntry(1, F.b);
    vector.setEntry(2, F.c);

    // Native API operation
    SparseExprVector result = matrix.operate(vector);
    assertEquals(result.toString(), //
        "SparseArrayExpr$SparseExprVector{2.0*a+5.0*c,3.0*b,4.0*c}");
    assertEquals(F.Plus(F.Times(F.num(2), F.a), F.Times(F.num(5), F.c)), result.getEntry(0));
    assertEquals(F.Times(F.num(3), F.b), result.getEntry(1));
    assertEquals(F.Times(F.num(4), F.c), result.getEntry(2));
  }

  @Test
  public void testTrie() {
    final Trie<int[], Number> resultTrie =
        new TrieBuilder<int[], Number, ArrayList<Number>>(TrieSequencerIntArray.INSTANCE,
            TrieMatch.EXACT, () -> new ArrayList<Number>(), (Number) null, false)//
                .build();
    resultTrie.putIfAbsent(new int[] {4, 1}, 1);
    resultTrie.putIfAbsent(new int[] {3, 2}, 1);
    resultTrie.putIfAbsent(new int[] {1, 3}, 1);
    for (Entry<int[], Number> entry : resultTrie.entrySet()) {
      System.out.println("Entry-ID" + entry + " Key: " + Arrays.toString(entry.getKey())
          + " Value: "
          + entry.getValue());
    }
  }

  @Test
  public void testSparseMatrixMatrixMultiplicationAPI() {
    // Matrix A (2x3)
    SparseExprMatrix matrixA = new SparseExprMatrix(2, 3, F.C0);
    matrixA.setEntry(0, 0, F.num(1));
    matrixA.setEntry(0, 2, F.num(2));
    matrixA.setEntry(1, 1, F.num(3));

    // Matrix B (3x2)
    SparseExprMatrix matrixB = new SparseExprMatrix(3, 2, F.C0);
    matrixB.setEntry(0, 1, F.x);
    matrixB.setEntry(1, 0, F.y);
    matrixB.setEntry(2, 1, F.z);

    // Multiply A * B -> Result should be 2x2
    FieldMatrix<IExpr> result = matrixA.multiply(matrixB);
    assertEquals(result.toString(), //
        "SparseArrayExpr$SparseExprMatrix{{0,x+2.0*z},{3.0*y,0}}");
    assertEquals(2, result.getRowDimension());
    assertEquals(2, result.getColumnDimension());

    // Row 0
    assertEquals(F.C0, result.getEntry(0, 0));
    assertEquals(F.Plus(F.x, F.Times(F.num(2), F.z)), result.getEntry(0, 1));

    // Row 1
    assertEquals(F.Times(F.num(3), F.y), result.getEntry(1, 0));
    assertEquals(F.C0, result.getEntry(1, 1));
  }

  @Test
  public void testSparseDotProductEvaluation() {
    // Ensure that Symja's Dot operator correctly evaluates the nested sparse array mechanics
    // natively
    String matrixDef = "SparseArray({{1, 1} -> 2, {2, 2} -> 3, {1, 3} -> 5}, {3, 3})";
    String vectorDef = "SparseArray({1 -> a, 2 -> b, 3 -> c}, {3})";

    // Dot[Matrix, Vector]
    IExpr resultVec = evaluator.eval("Dot(" + matrixDef + ", " + vectorDef + ")");

    assertEquals(resultVec.toString(), //
        "SparseArray(Number of elements: 2 Dimensions: {3} Default value: 0)");
    // Output should be evaluated back out to a SparseArray / List
    IExpr expectedVec = evaluator.eval("{2*a + 5*c, 3*b, 0}");
    assertEquals(expectedVec.normal(false), resultVec.normal(false));

    // Dot[Matrix, Matrix]
    String matrixBDef = "SparseArray({{1, 2} -> x, {2, 1} -> y, {3, 2} -> z}, {3, 2})";
    IExpr resultMat = evaluator.eval("Dot(" + matrixDef + ", " + matrixBDef + ")");

    assertEquals(resultMat.toString(), //
        "SparseArray(Number of elements: 2 Dimensions: {3,2} Default value: 0)");
    IExpr expectedMat = evaluator.eval("{{0, 2*x + 5*z}, {3*y, 0}, {0, 0}}");
    assertEquals(expectedMat.normal(false), resultMat.normal(false));
  }

  /** The JUnit setup method */
  @Override
  public void setUp() {
    super.setUp();
    Config.SHORTEN_STRING_LENGTH = 1024;
    Config.MAX_AST_SIZE = 1000000;
    EvalEngine.get().setIterationLimit(50000);
  }

  @AfterEach
  public void tearDown() throws Exception {
    // super.tearDown();
    Config.SHORTEN_STRING_LENGTH = 80;
  }
}
