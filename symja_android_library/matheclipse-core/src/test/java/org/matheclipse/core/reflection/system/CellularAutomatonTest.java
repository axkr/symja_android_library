package org.matheclipse.core.reflection.system;

import org.junit.Test;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

public class CellularAutomatonTest extends ExprEvaluatorTestCase {

  @Test
  public void testCellularAutomaton001() {
    check("CellularAutomaton({{p_, q_, r_} -> Xor(p, Or(q, r))}, {{True}, False}, 2)", //
        "{{False,False,True,False,False},{False,True,True,True,False},{True,True,False,False,True}}");
    check("CellularAutomaton({{a, _, b} -> a, {b, _, a} -> b, {x_, _, x_} -> a}, {{b}, a}, 4)", //
        "{{b,a,a,a,a},{a,b,a,a,a},{a,a,b,a,a},{a,a,a,b,a},{a,a,a,a,b}}");

    check("CellularAutomaton(30, {{1}, 0}, 10) // MatrixForm", //
        "{{0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0},\n" //
            + " {0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0},\n" //
            + " {0,0,0,0,0,0,0,0,1,1,0,0,1,0,0,0,0,0,0,0,0},\n" //
            + " {0,0,0,0,0,0,0,1,1,0,1,1,1,1,0,0,0,0,0,0,0},\n" //
            + " {0,0,0,0,0,0,1,1,0,0,1,0,0,0,1,0,0,0,0,0,0},\n" //
            + " {0,0,0,0,0,1,1,0,1,1,1,1,0,1,1,1,0,0,0,0,0},\n" //
            + " {0,0,0,0,1,1,0,0,1,0,0,0,0,1,0,0,1,0,0,0,0},\n" //
            + " {0,0,0,1,1,0,1,1,1,1,0,0,1,1,1,1,1,1,0,0,0},\n" //
            + " {0,0,1,1,0,0,1,0,0,0,1,1,1,0,0,0,0,0,1,0,0},\n" //
            + " {0,1,1,0,1,1,1,1,0,1,1,0,0,1,0,0,0,1,1,1,0},\n" //
            + " {1,1,0,0,1,0,0,0,0,1,0,1,1,1,1,0,1,1,0,0,1}}");
    check("CellularAutomaton(30, {0, 0, 0, 1, 0, 0, 0}, 2)", //
        "{{0,0,0,1,0,0,0},{0,0,1,1,1,0,0},{0,1,1,0,0,1,0}}");

  }

  @Test
  public void testCellularAutomaton002() {
    check("CellularAutomaton({f(#) &, {}, 1}, {a, b, c}, 1)", //
        "{{a,b,c},{f({c,a,b}),f({a,b,c}),f({b,c,a})}}");
    check("CellularAutomaton({f(#) &, {}, {{-1}, {0}}}, {{1}, 0}, 2)", //
        "{{1,0,0},{f({0,1}),f({1,0}),f({0,0})},{f({f({0,0}),f({0,1})}),f({f({0,1}),f({1,0})}),f({f({\n"//
            + "1,0}),f({0,0})})}}");
    check("CellularAutomaton({Total(#) &, {}, 1/2}, {{1}, 0}, 3)", //
        "{{1,0,0,0},{1,1,0,0},{1,2,1,0},{1,3,3,1}}");
    check("CellularAutomaton({f(#, #2) &, {}, 1}, {a, b, c}, 2)", //
        "{{a,b,c},{f({c,a,b},1),f({a,b,c},1),f({b,c,a},1)},{f({f({b,c,a},1),f({c,a,b},1),f({a,b,c},\n" //
            + "1)},2),f({f({c,a,b},1),f({a,b,c},1),f({b,c,a},1)},2),f({f({a,b,c},1),f({b,c,a},1),f({c,a,b},\n" //
            + "1)},2)}}");
    check("CellularAutomaton({Mod(Total(#) + #2, 4) &, {}, 1}, {{1}, 0}, 3)", //
        "{{0,0,0,1,0,0,0},{1,1,2,2,2,1,1},{1,2,3,0,3,2,1},{3,1,0,1,0,1,3}}");
  }

  @Test
  public void testCellularAutomaton003() {
    check("CellularAutomaton(73, {{1}, 0}, 4) // MatrixForm", //
        "{{0,0,0,0,1,0,0,0,0},\n" //
            + " {1,1,1,0,0,0,1,1,1},\n" //
            + " {0,0,1,0,1,0,1,0,0},\n" //
            + " {1,0,0,0,0,0,0,0,1},\n" //
            + " {1,0,1,1,1,1,1,0,1}}");
    check("CellularAutomaton({39421042, 2, 2}, {{1}, 0},4)// MatrixForm", //
        "{{0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0},\n" //
            + " {0,0,0,0,0,0,1,0,1,0,1,0,0,0,0,0,0},\n" //
            + " {0,0,0,0,1,0,1,1,0,1,1,0,1,0,0,0,0},\n" //
            + " {0,0,1,0,1,0,1,0,0,1,0,0,1,0,1,0,0},\n" //
            + " {1,0,1,1,0,1,1,0,0,1,0,0,1,1,1,0,1}}");
  }

  @Test
  public void testCellularAutomaton004() {
    check("CellularAutomaton(90)[{1, 0, 0, 0, 0, 0}]", //
        "{0,1,0,0,0,1}");
    check("CellularAutomaton(90, {1, 0, 0, 0, 0, 0}, 5)", //
        "{{1,0,0,0,0,0},{0,1,0,0,0,1},{0,0,1,0,1,0},{0,1,0,0,0,1},{0,0,1,0,1,0},{0,1,0,0,\n" //
            + "0,1}}");
    check("CellularAutomaton({f(#) &, {}, 1}, {a, b, c, d}, 2)", //
        "{{a,b,c,d},{f({d,a,b}),f({a,b,c}),f({b,c,d}),f({c,d,a})},{f({f({c,d,a}),f({d,a,b}),f({a,b,c})}),f({f({d,a,b}),f({a,b,c}),f({b,c,d})}),f({f({a,b,c}),f({b,c,d}),f({c,d,a})}),f({f({b,c,d}),f({c,d,a}),f({d,a,b})})}}");
  }

  @Test
  public void testCellularAutomaton005() {
    check("CellularAutomaton({14, {2, 1}, {1, 1}}, {{{1}}, 0}, 2)// MatrixForm", //
        "{{{0,0,0,0,0},{0,0,0,0,0},{0,0,1,0,0},{0,0,0,0,0},{0,0,0,0,0}},\n" //
            + " {{0,0,0,0,0},{0,1,1,1,0},{0,1,1,1,0},{0,1,1,1,0},{0,0,0,0,0}},\n" //
            + " {{1,1,1,1,1},{1,0,0,0,1},{1,0,0,0,1},{1,0,0,0,1},{1,1,1,1,1}}}");
    check("CellularAutomaton({14, {2, 1}, {1, 1}}, {{{1}}, 0}, {{{2}}})", //
        "{{1,1,1,1,1},{1,0,0,0,1},{1,0,0,0,1},{1,0,0,0,1},{1,1,1,1,1}}");
  }

  @Test
  public void testCellularAutomatonBooleSlots() {
    check("CellularAutomaton(Xor(#, #3) &, {{1}, 0}, 3)", //
        "{{0,0,0,1,0,0,0},{0,0,1,0,1,0,0},{0,1,0,0,0,1,0},{1,0,1,0,1,0,1}}");
    check("CellularAutomaton(Xor(#, #5) &, {{1}, 0}, 3)", //
        "{{0,0,0,0,0,0,1,0,0,0,0,0,0},{0,0,0,0,1,0,0,0,1,0,0,0,0},{0,0,1,0,0,0,0,0,0,0,1,\n" //
            + "0,0},{1,0,0,0,1,0,0,0,1,0,0,0,1}}");

  }

  @Test
  public void testCellularAutomatonHighOrder() {
    check("CellularAutomaton({30, 2, 1, 2}, {{1, 0, 1, 1}, {1, 1, 0, 1}}, {{-1, 4}})", //
        "{{1,0,1,1},{1,1,0,1},{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0}}");
    check("CellularAutomaton({30, 2, 1, 1}, {{1, 0, 1, 1}}, 4)", //
        "{{1,0,1,1},{0,0,1,0},{0,1,1,1},{0,1,0,0},{1,1,1,0}}");
    check("CellularAutomaton({30, 2, 1, 2}, {{1, 0, 1, 1}, {1, 1, 0, 1}}, 4)", //
        "{{1,1,0,1},{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0}}");
    check("CellularAutomaton({1008, 2, 1, 2}, {{{1}, {1}}, 0},5)", //
        "{{0,0,0,1},{0,0,1,0},{0,0,0,0},{0,1,0,0},{0,0,1,0},{1,0,0,1}}");
    check("CellularAutomaton({1008, 2, 1, 2}, {{{1}, {1}}, 0}, {{-1, 10}})", //
        "{{0,0,0,0,0,1,0,0,0,0,0},{0,0,0,0,0,1,0,0,0,0,0},{0,0,0,0,1,0,0,0,0,0,0},{0,0,0,\n"//
            + "0,0,0,0,0,0,0,0},{0,0,0,1,0,0,0,0,0,0,0},{0,0,0,0,1,0,0,0,0,0,0},{0,0,1,0,0,1,0,\n"//
            + "0,0,0,0},{0,0,0,0,0,0,1,0,0,0,0},{0,1,0,0,1,0,0,1,0,0,0},{0,0,1,0,0,0,0,0,1,0,0},{\n"//
            + "1,0,0,0,0,0,1,0,0,1,0},{0,0,0,0,0,0,0,0,0,0,1}}");
    check("CellularAutomaton({10, {2, 1}, 1, 12}, {Table({1}, {12}), 0},10)", //
        "{{0,1,0},{0,0,0},{0,0,0},{0,0,0},{0,0,0},{0,0,0},{0,0,0},{0,0,0},{0,0,0},{0,0,0},{\n" //
            + "1,1,1}}");
  }

  @Test
  public void testOperatorFormGeneration() {
    check("CellularAutomaton(30)", //
        "CellularAutomaton(30)");
    check("CellularAutomaton({30, 2, 1})", //
        "CellularAutomaton({30,2,1})");
    check("CellularAutomaton(\"Rule30\")", //
        "CellularAutomaton(Rule30)");
    check("CellularAutomaton(f(#, #2)&)", //
        "CellularAutomaton(f(#1,#2)&)");
  }

  @Test
  public void testOperatorFormExecution1D_InfinitePadding() {
    // Rule 30 on a single active cell
    check("CellularAutomaton(30)[{{1}, 0}]", //
        "{{1,1,1},{0}}");

    // Rule 90 (XOR) on a single active cell
    check("CellularAutomaton(90)[{{1}, 0}]", //
        "{{1,0,1},{0}}");

    // Rule 110 on a single active cell
    check("CellularAutomaton(110)[{{1}, 0}]", //
        "{{1,1,0},{0}}");
  }

  @Test
  public void testOperatorFormExecution1D_Cyclic() {
    // Tests standard cyclic bounded lists
    check("CellularAutomaton(30)[{0, 1, 0}]", //
        "{1,1,1}");
    check("CellularAutomaton(90)[{0, 1, 0}]", //
        "{1,0,1}");
  }

  @Test
  public void testOperatorFormExecutionOrderS() {
    check("CellularAutomaton({30, 2, 1, 2})[{{1, 0, 1, 1}, {1, 1, 0, 1}}]", //
        "{{1,1,0,1},{0,0,0,0}}");
  }

  @Test
  public void testOperatorFormExecution2D_Totalistic() {
    check("CellularAutomaton({14, {2, 1}, {1, 1}})[{{{1}}, 0}]", //
        "{{{1,1,1},{1,1,1},{1,1,1}},{{0}}}");
  }
}
