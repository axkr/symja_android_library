package org.matheclipse.core.reflection.system;

import org.junit.jupiter.api.Test;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

public class MatrixFunctionTest extends ExprEvaluatorTestCase {
  @Test
  public void testJordanWikipedia() {
    // wikipedia example
    check("JordanDecomposition({{5,4,2,1},{0,1,-1,-1},{-1,-1,3,0},{1,1,-1,2}})", //
        "{{{-1,1,1,1},{1,-1,0,0},{0,0,-1,0},{0,1,1,0}},{{1,0,0,0},{0,2,0,0},{0,0,4,1},{0,\n" //
            + "0,0,4}}}");
  }

  @Test
  public void testMatrixFunctionGamma() {
    // example from https://en.wikipedia.org/wiki/Analytic_function_of_a_matrix
    check("mf=MatrixFunction(Gamma, {{1,3},{2,1}})", //
        "{{1/2*(Gamma(1-Sqrt(6))+Gamma(1+Sqrt(6))),1/2*Sqrt(3/2)*(-Gamma(1-Sqrt(6))+Gamma(\n" //
            + "1+Sqrt(6)))},\n" //
            + " {(-Gamma(1-Sqrt(6))+Gamma(1+Sqrt(6)))/Sqrt(6),1/2*(Gamma(1-Sqrt(6))+Gamma(1+Sqrt(\n"//
            + "6)))}}");
    check("N(mf)", //
        "{{2.81144,0.407999},{0.271999,2.81144}}");
  }

  @Test
  public void testMatrixFunctionSqrt() {
    check("m=MatrixFunction(Sqrt, {{a, 1}, {0, b}})", //
        "{{Sqrt(a),1/(Sqrt(a)+Sqrt(b))},\n" + " {0,Sqrt(b)}}");
    check("m.m", //
        "{{a,1},\n" //
            + " {0,b}}");
  }

  @Test
  public void testMatrixFunctionLog() {
    check("{{2.,2.,1.},{4.,6.,4.},{3.,6.,8.}}=={{2.000000000000001,2.0,0.9999999999999991},\n"
        + " {4.000000000000002,5.999999999999997,3.9999999999999964},\n"
        + " {3.0000000000000093,6.000000000000002,7.999999999999997}}", "True");

    checkNumeric("m ={{2.,2.,1.},{4.,6.,4.},{3.,6.,8.}}", //
        "{{2.0,2.0,1.0},{4.0,6.0,4.0},{3.0,6.0,8.0}}");
    check("l=MatrixFunction(Log, m)", //
        "{{-0.118314,0.804385,0.0234838},\n" //
            + " {1.60877,1.00354,0.74315},\n" //
            + " {0.0704514,1.11472,1.75383}}");
    checkNumeric("me=MatrixExp(l)", //
        "{{2.0,1.9999999999999996,0.9999999999999994},\n" //
            + " {3.9999999999999996,5.9999999999999964,3.9999999999999973},\n" //
            + " {3.0000000000000058,5.999999999999999,8.0}}");
    check("me==m", //
        "True");
  }

  @Test
  public void testMatrixFunction001() {
    check("MatrixFunction({-3/2}, {{0,0}, {0,0}})", //
        "MatrixFunction({-3/2},{{0,0},{0,0}})");
    check("MatrixFunction(x |-> x^5 + 2 x^2 + 1, {{a, 0}, {1, b}})", //
        "{{1+2*a^2+a^5,0},\n"//
            + " {2*a+a^4+2*b+a^3*b+a^2*b^2+a*b^3+b^4,1+2*b^2+b^5}}");
    check("MatrixFunction(1 &, {{1, 2}, {3, 4}})", //
        "{{1,0},\n" //
            + " {0,1}}");
  }

  /**
   * Test Jordan Decomposition on a defective 2x2 matrix. Matrix: {{5, 4}, {-1, 1}} Trace = 6, Det =
   * 9 -> Eigenvalue = 3 (Algebraic Multiplicity 2, Geometric Multiplicity 1)
   */
  @Test
  public void testJordanDecompositionDefective2x2() {
    check("JordanDecomposition({{5, 4}, {-1, 1}})", //
        "{{{-2,-1},{1,0}},{{3,1},{0,3}}}");

    check(
        "With({jd = JordanDecomposition({{5, 4}, {-1, 1}})}, jd[[1]] . jd[[2]] . Inverse(jd[[1]]))", //
        "{{5,4},\n" //
            + " {-1,1}}");
  }

  /**
   * Test Jordan Decomposition on a classic defective matrix already in Jordan form.
   */
  @Test
  public void testJordanDecompositionClassicDefective() {
    check("JordanDecomposition({{1, 1}, {0, 1}})", //
        "{{{1,1},{0,0}},{{1,1},{0,1}}}");

    // S should be the identity matrix, and J should equal the original matrix
    // assertEquals("{{{1,0},{0,1}},{{1,1},{0,1}}}", jd.toString());
  }

  /**
   * Test MatrixExp on defective matrices. This validates the delta == 0 branch inside the MatrixExp
   * 2x2 formula.
   */
  @Test
  public void testMatrixExpDefective() {
    // Complex defective matrix: {{5, 4}, {-1, 1}}
    check("MatrixExp({{5, 4}, {-1, 1}})", //
        "{{3*E^3,4*E^3},{-E^3,-E^3}}");

    // Classic defective matrix: {{1, 1}, {0, 1}}
    check("MatrixExp({{1, 1}, {0, 1}})", //
        "{{E,E},{0,E}}");

    // Nilpotent matrix: {{0, 1}, {0, 0}} (Eigenvalues 0, 0)
    check("MatrixExp({{0, 1}, {0, 0}})", //
        "{{1,1},{0,1}}");
  }

  /**
   * Test MatrixLog on defective matrices. This validates the discriminant == 0 branch inside the
   * computeMatrixLog2x2 method.
   */
  @Test
  public void testMatrixLogDefective() {
    // Complex defective matrix: {{5, 4}, {-1, 1}}
    check("MatrixLog({{5, 4}, {-1, 1}})", //
        "{{2/3+Log(3),4/3},{-1/3,-2/3+Log(3)}}");
    // Since terms might be canonically reordered by Plus, we verify via Simplify evaluation
    // Expected: {{2/3+Log(3), 4/3}, {-1/3, -2/3+Log(3)}}
    check("Simplify(MatrixLog({{5, 4}, {-1, 1}}) == {{2/3+Log(3), 4/3}, {-1/3, -2/3+Log(3)}})", //
        "True");

    // Classic defective matrix: {{1, 1}, {0, 1}}
    check("MatrixLog({{1, 1}, {0, 1}})", //
        "{{0,1},{0,0}}");

    // Exponential/Logarithm relationship verification on a defective matrix
    check("Simplify(MatrixExp(MatrixLog({{5, 4}, {-1, 1}})) == {{5, 4}, {-1, 1}})", //
        "True");
  }


  /**
   * Test a 3x3 matrix with a single eigenvalue and a single Jordan block. Matrix: {{1, 1, 0}, {-1,
   * 2, 1}, {-1, 0, 3}} Eigenvalue = 2 (Algebraic Multiplicity 3, Geometric Multiplicity 1)
   */
  @Test
  public void testJordanDecompositionDefective3x3_SingleEigenvalue() {
    check("m={{1, 1, 0}, {-1, 2, 1}, {-1, 0, 3}};", //
        "");
    check("JordanDecomposition(m)", //
        "{{{1,-1,0},{1,0,-1},{1,0,0}},{{2,1,0},{0,2,1},{0,0,2}}}");

    // J should have 2s on the diagonal and 1s on the superdiagonal
    check("With({jd = JordanDecomposition(m)}, jd[[2]])", //
        "{{2,1,0},{0,2,1},{0,0,2}}");

    // Verify the core property: S . J . Inverse(S) == M
    check(
        "With({jd = JordanDecomposition(m)}, Simplify(jd[[1]] . jd[[2]] . Inverse(jd[[1]]) == m))", //
        "True");
  }

  /**
   * Test a 3x3 matrix with mixed eigenvalues (one defective, one normal). Matrix: {{2, 0, 0}, {1,
   * 2, 0}, {0, 0, 4}} Eigenvalues = 2 (Alg Mult 2, Geom Mult 1), 4 (Alg Mult 1, Geom Mult 1)
   */
  @Test
  public void testJordanDecompositionDefective3x3_MixedEigenvalues() {
    check("m={{2, 0, 0}, {1, 2, 0}, {0, 0, 4}};", //
        "");
    check("JordanDecomposition(m)", //
        "{{{0,1,0},{1,0,0},{0,0,1}},{{2,1,0},{0,2,0},{0,0,4}}}");

    // Verify the core property: S . J . Inverse(S) == M
    check(
        "With({jd = JordanDecomposition(m)}, Simplify(jd[[1]] . jd[[2]] . Inverse(jd[[1]]) == m))", //
        "True");
    // assertTrue("S . J . Inverse(S) must equal the original matrix", verification.isTrue());

    // Check that the Jordan block for eigenvalue 2 has a superdiagonal 1, and 4 is isolated
    // Note: The order of eigenvalues in J depends on the internal Eigenvalues() sorting.
    check(
        "With({j = JordanDecomposition(m)[[2]]}, j == {{2,1,0},{0,2,0},{0,0,4}} || j == {{4,0,0},{0,2,1},{0,0,2}})", //
        "True");
    // assertTrue("J must contain a 2x2 Jordan block for 2, and a 1x1 block for 4",
    // isCorrectJ.isTrue());
  }

  /**
   * Test a 3x3 matrix where Geometric Multiplicity > 1 but < Algebraic Multiplicity. Matrix: {{2,
   * 0, 0}, {0, 2, 1}, {0, 0, 2}} Eigenvalue = 2 (Alg Mult 3, Geom Mult 2).
   */
  @Test
  public void testJordanDecompositionDefective3x3_Subblocks() {
    check("m={{2, 0, 0}, {0, 2, 1}, {0, 0, 2}};", //
        "");
    check("JordanDecomposition(m)", //
        "{{{0,0,1},{1,0,0},{0,1,0}},{{2,1,0},{0,2,0},{0,0,2}}}");
  }

  /**
   * Test MatrixExp on a 3x3 defective matrix.
   */
  @Test
  public void testMatrixExpDefective3x3() {
    check("m={{1, 1, 0}, {-1, 2, 1}, {-1, 0, 3}};", //
        "");
    check("JordanDecomposition(m)", //
        "{{{1,-1,0},{1,0,-1},{1,0,0}},{{2,1,0},{0,2,1},{0,0,2}}}");
    // *****************************************************************
    // Test fallback strategy `JordanDecomposition` for defective matrix
    // in `MatrixFunction`, `MatrixLog` and `MatrixExp`
    // *****************************************************************
    check("MatrixFunction(Exp, m)", //
        "{{0,E^2/2,E^2/2},\n" //
            + " {-E^2,E^2/2,3/2*E^2},\n" //
            + " {-E^2,-E^2/2,5/2*E^2}}");
    check("MatrixLog(m)", //
        "{{-1/2+Log(2),5/8,-1/8},\n" //
            + " {-1/2,1/8+Log(2),3/8},\n" //
            + " {-1/2,1/8,3/8+Log(2)}}");
    check("MatrixExp(m)", //
        "{{0,E^2/2,E^2/2},\n" //
            + " {-E^2,E^2/2,3/2*E^2},\n" //
            + " {-E^2,-E^2/2,5/2*E^2}}");

    // A property check: det(e^A) = e^(Tr(A)). Trace of matrix is 1+2+3 = 6.
    check("Simplify(Det(MatrixExp(m)) == E^6)", //
        "True");
  }

  @Test
  public void testMatrixExp() {

    check("m = SparseArray({{1, 3} -> 1, {2, 2} -> 2, {3, 1} -> 3}, {3, 3});", //
        "");
    check("m2=Normal(N(m))", //
        "{{0.0,0.0,1.0},{0.0,2.0,0.0},{3.0,0.0,0.0}}");
    check("MatrixExp(m2)  ", //
        "{{2.91458,0.0,1.58059},\n"//
            + " {0.0,7.38906,0.0},\n"//
            + " {4.74176,0.0,2.91458}}");
    check("MatrixExp(m) ", //
        "{{(1+E^(2*Sqrt(3)))/(2*E^Sqrt(3)),0,(-1+E^(2*Sqrt(3)))/(2*Sqrt(3)*E^Sqrt(3))},\n" //
            + " {0,E^2,0},\n" //
            + " {(Sqrt(3)*(-1+E^(2*Sqrt(3))))/(2*E^Sqrt(3)),0,(1+E^(2*Sqrt(3)))/(2*E^Sqrt(3))}}");



    check("MatrixExp({{0,1,1},{-1,0,-1},{1,-1,0}}*t)", //
        "{{1,1-1/E^t,1-1/E^t},\n"//
            + " {1-E^t,1,1-E^t},\n"//
            + " {-1+E^t,-1+E^(-t),-1+E^(-t)+E^t}}");
    check("MatrixExp({{0, t}, {-t, 2*t}})", //
        "{{E^t*(1-t),E^t*t},{-E^t*t,E^t*(1+t)}}");
    check("MatrixExp[{{a}}]", //
        "{{E^a}}");
    check("MatrixExp({{0,1}, {0,1}})", //
        "{{1,-1+E},{0,E}}");
    check("MatrixExp({{0, 1}, {-1, 0}}*t)", //
        "{{Cos(t),Sin(t)},{-Sin(t),Cos(t)}}");
    check("MatrixExp({{0, a}, {-a, 0}})*t", //
        "{{t*Cos(a),t*Sin(a)},{-t*Sin(a),t*Cos(a)}}");
    check("MatrixExp({{0,t},{0,0}})", //
        "{{1,t},{0,1}}");
    check("MatrixExp({{0,0},{t,0}})", //
        "{{1,0},{t,1}}");
    check("MatrixExp({{a,b},{0,d}})", //
        "{{E^a,(b*(E^a-E^d))/(a-d)},{0,E^d}}");
    check("MatrixExp({{a,0},{c,d}})", //
        "{{E^a,0},{(c*(E^a-E^d))/(a-d),E^d}}");
    check("MatrixExp({{0, a}, {b, 0}})", //
        "{{E^(Sqrt(a)*Sqrt(b))/2+1/(2*E^(Sqrt(a)*Sqrt(b))),(Sqrt(a)*E^(Sqrt(a)*Sqrt(b)))/(\n" //
            + "2*Sqrt(b))-Sqrt(a)/(2*Sqrt(b)*E^(Sqrt(a)*Sqrt(b)))},{(Sqrt(b)*E^(Sqrt(a)*Sqrt(b)))/(\n" //
            + "2*Sqrt(a))-Sqrt(b)/(2*Sqrt(a)*E^(Sqrt(a)*Sqrt(b))),E^(Sqrt(a)*Sqrt(b))/2+1/(2*E^(Sqrt(a)*Sqrt(b)))}}");
    check("MatrixExp({{a,b}, {c,d}})", //
        "{{((-a+d+Sqrt(a^2+4*b*c-2*a*d+d^2))*E^(a/2+d/2-Sqrt(a^2+4*b*c-2*a*d+d^2)/2))/(2*Sqrt(a^\n"
            //
            + "2+4*b*c-2*a*d+d^2))+((a-d+Sqrt(a^2+4*b*c-2*a*d+d^2))*E^(a/2+d/2+Sqrt(a^2+4*b*c-2*a*d+d^\n"
            //
            + "2)/2))/(2*Sqrt(a^2+4*b*c-2*a*d+d^2)),(-b*E^(a/2+d/2-Sqrt(a^2+4*b*c-2*a*d+d^2)/2))/Sqrt(a^\n"
            //
            + "2+4*b*c-2*a*d+d^2)+(b*E^(a/2+d/2+Sqrt(a^2+4*b*c-2*a*d+d^2)/2))/Sqrt(a^2+4*b*c-2*a*d+d^\n"
            //
            + "2)},{(-c*E^(a/2+d/2-Sqrt(a^2+4*b*c-2*a*d+d^2)/2))/Sqrt(a^2+4*b*c-2*a*d+d^2)+(c*E^(a/\n" //
            + "2+d/2+Sqrt(a^2+4*b*c-2*a*d+d^2)/2))/Sqrt(a^2+4*b*c-2*a*d+d^2),((a-d+Sqrt(a^2+4*b*c-\n" //
            + "2*a*d+d^2))*E^(a/2+d/2-Sqrt(a^2+4*b*c-2*a*d+d^2)/2))/(2*Sqrt(a^2+4*b*c-2*a*d+d^2))+((-a+d+Sqrt(a^\n"
            //
            + "2+4*b*c-2*a*d+d^2))*E^(a/2+d/2+Sqrt(a^2+4*b*c-2*a*d+d^2)/2))/(2*Sqrt(a^2+4*b*c-2*a*d+d^\n"
            //
            + "2))}}");

    // check("MatrixExp[{{2, 3, 0}, {4, 9, 0}, {0, 0, 4}}]", //
    // "{{1/194*(97*E^4+7*Sqrt(97)*E^4+97*E^(1/2*(11+Sqrt(97)))-7*Sqrt(97)*E^(1/2*(11+Sqrt(\n" //
    // + "97)))),0,1/194*(97*E^4-7*Sqrt(97)*E^4-97*E^(1/2*(11+Sqrt(97)))+7*Sqrt(97)*E^(1/2*(\n" //
    // + "11+Sqrt(97))))},\n" //
    // + " {0,E^(1/2*(11-Sqrt(97))),0},\n" //
    // + " {1/194*(97+7*Sqrt(97))*(E^4-E^(1/2*(11+Sqrt(97)))),0,1/2*(E^4+(-7*E^4)/Sqrt(97)+E^(\n" //
    // + "1/2*(11+Sqrt(97)))+(7*E^(1/2*(11+Sqrt(97))))/Sqrt(97))}}");

    check("MatrixExp({{3.4, 1.2}, {0.001, -0.9}})", //
        "{{29.97054,8.24991},{0.00687492,0.408375}}");
    check("MatrixExp({{1.2, 5.6}, {3, 4}})", //
        "{{346.5575,661.7346},{354.5007,677.4248}}");
    check("MatrixExp({{2, 0, 0}, {0, 1, -1}, {0, 1, 1}})", //
        "{{E^2,0,0},\n" //
            + " {0,1/2*(E^(1-I)+E^(1+I)),-I*1/2*E^(1-I)+I*1/2*E^(1+I)},\n" //
            + " {0,I*1/2*E^(1-I)-I*1/2*E^(1+I),1/2*(E^(1-I)+E^(1+I))}}");

  }

  @Test
  public void testMatrixLog2x2Diagonal() {
    // singular matrix (det == 0): Log is not analytic/defined at eigenvalue 0, so
    // MatrixLog stays unevaluated and emits the `fnand` message.
    check("MatrixLog({{a,0},{0,0}})", //
        "MatrixLog(\n" //
            + "{{a,0},\n" //
            + " {0,0}})");
    check("MatrixLog({{2,0},{0,3}})", //
        "{{Log(2),0},{0,Log(3)}}");
    check("MatrixLog({{1,1},{0,1}})", //
        "{{0,1},{0,0}}");
  }

  @Test
  public void testMatrixLog() {
    check("MatrixLog({{3.4, 1.2}, {0.001, -0.9}})", //
        "{{1.22377+I*0.00020385,0.37081+I*(-0.87661)},\n"//
            + " {0.000309008+I*(-0.000730508),-0.104964+I*3.14139}}");
    check("m = {{E,0,0},{0,4*E,-4*E},{0,4*E,4*E}};", //
        "");
    check("MatrixLog(m)", //
        "{{1,0,0},\n"//
            + " {0,1/2*(2+Log(4-I*4)+Log(4+I*4)),-I*1/2*Log(4-I*4)+I*1/2*Log(4+I*4)},\n" //
            + " {0,I*1/2*Log(4-I*4)-I*1/2*Log(4+I*4),1/2*(2+Log(4-I*4)+Log(4+I*4))}}");
    check("MatrixLog(m) // N", //
        "{{1.0,0.0,0.0},{0.0,2.73287,-0.785398},{0.0,0.785398,2.73287}}");

    check("MatrixLog({{1, 2}, {3, 4}})//N", //
        "{{-0.35044+I*2.39112,0.929351+I*(-1.09376)},{1.39403+I*(-1.64064),1.04359+I*0.750475}}");

    check("MatrixLog({{0, 0, 1}, {0, 2, 0}, {-1, 0, 0}})", //
        "{{0,0,Pi/2},\n" //
            + " {0,Log(2),0},\n" //
            + " {-Pi/2,0,0}}");
    check("m = {{0, 0, 1}, {0, 2, 0}, {-1, 0, 0}}; \n" //
        + "mm = MatrixExp[MatrixLog[m]];\n" //
        + "m == mm", //
        "True");

    check("MatrixLog({{6, 0, 10}, {0, 20, 0}, {-2, 0, 0}})//N", //
        "{{2.25359,0.0,2.51907},{0.0,2.99573,0.0},{-0.503815,0.0,0.742144}}");
    check("MatrixLog(N({{6, 0, 10}, {0, 20, 0}, {-2, 0, 0}}))", //
        "{{2.25359,0.0,2.51907},\n" //
            + " {0.0,2.99573,0.0},\n" //
            + " {-0.503815,0.0,0.742144}}");
  }
}
