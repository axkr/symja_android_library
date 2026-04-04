package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

public class MeijerGReduceTest extends ExprEvaluatorTestCase {

  @Test
  public void testMeijerGReduceExp() {
    check("MeijerGReduce(Exp(x), x)", //
        "Inactive(MeijerG)[{{},{}},{{0,1/2},{}},-x/2,1/2]/Sqrt(Pi)");

    check("MeijerGReduce(Exp(-2*x), x)", //
        "Inactive(MeijerG)[{{},{}},{{0,1/2},{}},x,1/2]/Sqrt(Pi)");
  }

  @Test
  public void testMeijerGReduceTrig() {
    // Sin(x) -> Sqrt(Pi) * MeijerG({{}, {}}, {{1/2}, {0}}, x^2/4)
    check("MeijerGReduce(Sin(x), x)", //
        "Sqrt(Pi)*Inactive(MeijerG)[{{},{}},{{1/2},{0}},x/2,1/2]");

    // Cos(3*x) -> Sqrt(Pi) * MeijerG({{}, {}}, {{0}, {1/2}}, 9*x^2/4)
    check("MeijerGReduce(Cos(3*x), x)", //
        "Sqrt(Pi)*Inactive(MeijerG)[{{},{}},{{0},{1/2}},3/2*x,1/2]");
  }

  @Test
  public void testMeijerGReduceHyperbolic() {
    // Sinh(x) -> -I * Sqrt(Pi) * MeijerG({{}, {}}, {{1/2}, {0}}, -x^2/4)
    check("MeijerGReduce(Sinh(x), x)", //
        "Sqrt(2)*Pi^(3/2)*Inactive(MeijerG)[{{},{3/4}},{{1/2},{3/4,0}},x/2,1/2]");

    // Cosh(x) -> Sqrt(Pi) * MeijerG({{}, {}}, {{0}, {1/2}}, -x^2/4)
    check("MeijerGReduce(Cosh(x), x)", //
        "Sqrt(2)*Pi^(3/2)*Inactive(MeijerG)[{{},{3/4}},{{0},{3/4,1/2}},x/2,1/2]");
  }

  @Test
  public void testMeijerGReduceNested() {
    check("MeijerGReduce(x * Exp(x) + Sin(x), x)", //
        "(Pi*Inactive(MeijerG)[{{},{}},{{1/2},{0}},x/2,1/2]+x*Inactive(MeijerG)[{{},{}},{{\n" //
            + "0,1/2},{}},-x/2,1/2])/Sqrt(Pi)");
  }
}
