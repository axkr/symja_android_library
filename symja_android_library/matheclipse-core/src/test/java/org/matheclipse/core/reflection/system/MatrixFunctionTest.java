package org.matheclipse.core.reflection.system;

import org.junit.jupiter.api.Test;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

public class MatrixFunctionTest extends ExprEvaluatorTestCase {

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
        "{{2.000000000000001,2.0,0.9999999999999991},\n" //
            + " {4.000000000000002,5.999999999999997,3.9999999999999964},\n" //
            + " {3.0000000000000093,6.000000000000002,7.999999999999997}}");
    check("me==m", //
        "True");
  }

  @Test
  public void testMatrixFunction001() {
    check("MatrixFunction(x |-> x^5 + 2 x^2 + 1, {{a, 0}, {1, b}})", //
        "{{1+2*a^2+a^5,0},\n"//
            + " {2*a+a^4+2*b+a^3*b+a^2*b^2+a*b^3+b^4,1+2*b^2+b^5}}");
    check("MatrixFunction(1 &, {{1, 2}, {3, 4}})", //
        "{{1,0},\n" //
            + " {0,1}}");
  }
}
