package org.matheclipse.core.system;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;

/** Tests for statistical moment functions */
public class StatisticalMomentTest extends ExprEvaluatorTestCase {

  @Test
  public void testCentralMoment() {
    check("CentralMoment({a,b,c}, z)", //
        "1/3*((a+1/3*(-a-b-c))^z+(b+1/3*(-a-b-c))^z+(1/3*(-a-b-c)+c)^z)");
    check("CentralMoment({{a,b},{c,d}}, 4) // Simplify", //
        "{(-a+c)^4/16,(-b+d)^4/16}");
    check("CentralMoment({1.1, 1.2, 1.4, 2.1, 2.4}, 4)", //
        "0.100845");
    check("CentralMoment(BernoulliDistribution(n),m)", //
        "Piecewise({{1,m==0},{((1-n)^(-1+m)-1/(-n)^(1-m))*(1-n)*n,m>0}},0)");
    check("CentralMoment(ChiSquareDistribution(n),m)", //
        "2^m*HypergeometricU(-m,1-m-n/2,-n/2)");
    check("CentralMoment(ExponentialDistribution(n),m)", //
        "Subfactorial(m)/n^m");
    check("CentralMoment(GammaDistribution(a,b),2)", //
        "a*b^2");
    check("CentralMoment(NormalDistribution(a,b),m)", //
        "Piecewise({{b^m*(-1+m)!!,Mod(m,2)==0&&m>=0}},0)");
  }

  @Test
  public void testCentralMomentScalar() {
    // 0-th central moment is 1
    check("CentralMoment({1.1, 1.2, 1.4, 2.1, 2.4}, 0)", //
        "1");

    // 1-st central moment is always 0
    check("CentralMoment({1, 2, 3, 4, 5}, 1)", //
        "0");

    // 2-nd central moment (Variance under population n)
    check("CentralMoment({1, 2, 3, 4, 5}, 2)", //
        "2");

    // Higher order moments
    check("CentralMoment({1, 2, 3}, 3)", //
        "0");
    check("CentralMoment({1, 2, 3}, 4)", //
        "2/3");
  }

  @Test
  public void testCentralMomentMatrixScalar() {
    // Column-wise evaluation when passed a scalar order
    check("CentralMoment({{1, 2}, {3, 4}, {5, 6}}, 2)", //
        "{8/3,8/3}");
    check("CentralMoment({{1, 2}, {3, 4}, {5, 6}}, 0)", //
        "{1,1}");
  }

  @Test
  public void testCentralMomentMultivariate() {
    // Covariance / co-moment
    check("CentralMoment({{1, 2}, {3, 4}, {5, 6}}, {1, 1})", //
        "8/3");

    // Omitting a column by specifying 0 for its dimension
    check("CentralMoment({{1, 2}, {3, 4}, {5, 6}}, {2, 0})", //
        "8/3");
    check("CentralMoment({{1, 2}, {3, 4}, {5, 6}}, {0, 2})", //
        "8/3");

    // Entirely zero dimension mapping
    check("CentralMoment({{1, 2}, {3, 4}, {5, 6}}, {0, 0})", //
        "1");
  }

  @Test
  public void testCentralMomentFormal() {
    // 1-argument formal definitions remain unevaluated
    check("CentralMoment(r)", //
        "CentralMoment(r)");
    check("CentralMoment(3)", //
        "CentralMoment(3)");
    check("CentralMoment({2, 3})", //
        "CentralMoment({2,3})");
  }

  @Test
  public void testCentralMomentSymbolic() {
    // Evaluates with symbols
    check("Simplify(CentralMoment({a, b, c}, 2))", //
        "2/9*(a^2-a*b+b^2-a*c-b*c+c^2)");
  }

  @Test
  public void testCumulant() {
    check("Cumulant({{a,b},{c,d}}, 4) //Simplify", //
        "{-(-a+c)^4/8,-(-b+d)^4/8}");
    check("Cumulant({a,b,c},4)", //
        "-((a+1/3*(-a-b-c))^2+(b+1/3*(-a-b-c))^2+(1/3*(-a-b-c)+c)^2)^2/3+1/3*((a+1/3*(-a-b-c))^\n"
            + "4+(b+1/3*(-a-b-c))^4+(1/3*(-a-b-c)+c)^4)");
    check("Cumulant({a,b,c,d},3)", //
        "1/4*((a+1/4*(-a-b-c-d))^3+(b+1/4*(-a-b-c-d))^3+(c+1/4*(-a-b-c-d))^3+(1/4*(-a-b-c-d)+d)^\n"
            + "3)");

  }

  @Test
  public void testCumulantUnivariate() {
    // k_1 = Mean
    check("Cumulant({1, 2, 3, 4}, 1)", //
        "5/2");

    // k_2 = Variance (CentralMoment 2)
    check("Cumulant({1, 2, 3, 4}, 2)", //
        "5/4");

    // k_3 = CentralMoment 3 (0 for symmetric distributions)
    check("Cumulant({1, 2, 3, 4}, 3)", //
        "0");

    // k_4 = CentralMoment 4 - 3 * (CentralMoment 2)^2
    check("Cumulant({1, 2, 3, 4}, 4)", //
        "-17/8");
  }

  @Test
  public void testCumulantMultivariate() {
    // Co-cumulant for a 2D array
    check("Cumulant({{1, 2}, {3, 4}, {5, 6}}, {1, 1})", //
        "8/3");

    // Order 2 for a single variable in a matrix
    check("Cumulant({{1, 2}, {3, 4}, {5, 6}}, {2, 0})", //
        "8/3");

    // Total order 3 multivariate
    check("Cumulant({{1, 5, 9}, {2, 6, 10}, {3, 7, 11}}, {1, 1, 1})", //
        "0");
  }

  @Test
  public void testCumulantFormal() {
    // Should remain unevaluated for formal r
    check("Cumulant(r)", //
        "Cumulant(r)");
    check("Cumulant(3)", //
        "Cumulant(3)");
    check("Cumulant({2, 3})", //
        "Cumulant({2,3})");
  }

  @Test
  public void testCumulantSymbolic() {
    // Order 1 symbolic
    check("Cumulant({a, b}, 1)", //
        "1/2*(a+b)");

    // Order 2 symbolic, simplified to match exact algebraic result
    check("Simplify(Cumulant({a, b}, 2))", //
        "(-a+b)^2/4");

    // Fallback to CentralMoment for symbolic distributions
    check("Cumulant(NormalDistribution(mu, sigma), 2)", //
        "sigma^2");
  }

  @Test
  public void testCumulantEdgeCases() {
    // Order 0 always returns 0
    check("Cumulant({1, 2, 3}, 0)", //
        "0");
    check("Cumulant({{1, 2}, {3, 4}}, {0, 0})", //
        "0");

    // Negative order remains unevaluated
    check("Cumulant({1, 2, 3}, -1)", //
        "Cumulant({1,2,3},-1)");
    check("Cumulant({{1, 2}, {3, 4}}, {-1, 1})", //
        "Cumulant({{1,2},{3,4}},{-1,1})");
  }

  @Test
  public void testFactorialMoment() {

    check("FactorialMoment(Array(Subscript(a, ##) &, {2, 2, 2}), 2) // Simplify // MatrixForm",
        //
        "{{1/2*((-1+Subscript(a,1,1,1))*Subscript(a,1,1,1)+(-1+Subscript(a,2,1,1))*Subscript(a,\n"
            + "2,1,1)),1/2*((-1+Subscript(a,1,1,2))*Subscript(a,1,1,2)+(-1+Subscript(a,2,1,2))*Subscript(a,\n"
            + "2,1,2))},\n"
            + " {1/2*((-1+Subscript(a,1,2,1))*Subscript(a,1,2,1)+(-1+Subscript(a,2,2,1))*Subscript(a,\n"
            + "2,2,1)),1/2*((-1+Subscript(a,1,2,2))*Subscript(a,1,2,2)+(-1+Subscript(a,2,2,2))*Subscript(a,\n"
            + "2,2,2))}}");
    check("FactorialMoment({a,b,c},z)", //
        "1/3*(FactorialPower(a,z)+FactorialPower(b,z)+FactorialPower(c,z))");

    check("FactorialMoment({a,b,c}, 1)", //
        "1/3*(a+b+c)");
    check("FactorialMoment({a,b,c}, 2)", //
        "1/3*((-1+a)*a+(-1+b)*b+(-1+c)*c)");
    check("FactorialMoment({a,b,c}, 3)", //
        "1/3*((-2+a)*(-1+a)*a+(-2+b)*(-1+b)*b+(-2+c)*(-1+c)*c)");
  }

  @Test
  public void testScalarFactorialMoment() {
    check("FactorialMoment({1, 2, 3, 4}, 3)", //
        "15/2");

    // 0-th factorial moment is 1
    check("FactorialMoment({1.1, 1.2, 1.4, 2.1, 2.4}, 0)", "1");

    // 1-st factorial moment is the same as the mean
    check("FactorialMoment({1, 2, 3, 4}, 1)", "5/2");

    // 2-nd factorial moment: Mean(x * (x - 1)) -> Mean({0, 2, 6}) = 8/3
    check("FactorialMoment({1, 2, 3}, 2)", "8/3");

    // 3-rd factorial moment: Mean(x * (x - 1) * (x - 2)) -> Mean({0, 0, 6, 24}) = 15/2
    check("FactorialMoment({1, 2, 3, 4}, 3)", //
        "15/2");
  }

  @Test
  public void testMatrixScalarFactorialMoment() {
    // Column-wise evaluation when passed a scalar order
    // Column 1: Mean of FactorialPower({1, 3, 5}, 2) -> Mean({0, 6, 20}) = 26/3
    // Column 2: Mean of FactorialPower({2, 4, 6}, 2) -> Mean({2, 12, 30}) = 44/3
    check("FactorialMoment({{1, 2}, {3, 4}, {5, 6}}, 2)", //
        "{26/3,44/3}");
    check("FactorialMoment({{1, 2}, {3, 4}, {5, 6}}, 0)", //
        "{1,1}");

  }

  @Test
  public void testMultivariateFactorialMoment() {
    // Expected value of X * Y = (1*2 + 3*4 + 5*6)/3 = 44/3
    check("FactorialMoment({{1, 2}, {3, 4}, {5, 6}}, {1, 1})", "44/3");

    // Omitting a column by specifying 0 for its dimension
    // Expected value of X(X-1) * 1 = (1*0 + 3*2 + 5*4)/3 = 26/3
    check("FactorialMoment({{1, 2}, {3, 4}, {5, 6}}, {2, 0})", "26/3");

    // Expected value of 1 * Y(Y-1) = (2*1 + 4*3 + 6*5)/3 = 44/3
    check("FactorialMoment({{1, 2}, {3, 4}, {5, 6}}, {0, 2})", "44/3");

    // Entirely zero dimension mapping
    check("FactorialMoment({{1, 2}, {3, 4}, {5, 6}}, {0, 0})", "1");
  }

  @Test
  public void testFormalFactorialMoment() {
    // 1-argument formal definitions remain unevaluated
    check("FactorialMoment(r)", "FactorialMoment(r)");
    check("FactorialMoment(3)", "FactorialMoment(3)");
    check("FactorialMoment({2, 3})", "FactorialMoment({2,3})");
  }

  @Test
  public void testSymbolicFactorialMoment() {
    check("FactorialMoment({a, b}, 2)", //
        "1/2*((-1+a)*a+(-1+b)*b)");
  }

  @Test
  public void testMoment() {
    // message: Moment: The first argument {} is expected to be a vector, matrix or a distribution.
    check("Moment({}, 3)", //
        "Moment({},3)");
    check("Moment({a,b}, 0)", //
        "1");
    check("Moment({a,b,c}, z)", //
        "1/3*(a^z+b^z+c^z)");
    check("Moment({{a,b},{c,d}}, 4) // Simplify", //
        "{1/2*(a^4+c^4),1/2*(b^4+d^4)}");
    check("Moment({a,b}, 3)", //
        "1/2*(a^3+b^3)");
    // TODO
    // check("Moment({{a,b},{c,d}}, {1, 2})", //
    // "");
  }

  @Test
  public void testMomentScalar() {
    // 0-th moment is always 1
    check("Moment({1.1, 1.2, 1.4, 2.1, 2.4}, 0)", //
        "1");

    // 1-st moment is the Mean
    check("Moment({1, 2, 3, 4}, 1)", //
        "5/2");

    // 2-nd moment: Mean of {1^2, 2^2, 3^2, 4^2} -> (1+4+9+16)/4 = 30/4 = 15/2
    check("Moment({1, 2, 3, 4}, 2)", //
        "15/2");

    // 3-rd moment: Mean of {1^3, 2^3, 3^3} -> (1+8+27)/3 = 36/3 = 12
    check("Moment({1, 2, 3}, 3)", //
        "12");
  }

  @Test
  public void testMomentMatrixScalar() {
    // Column-wise evaluation when passed a scalar order
    // Column 1: Mean({1^2, 3^2, 5^2}) -> (1+9+25)/3 = 35/3
    // Column 2: Mean({2^2, 4^2, 6^2}) -> (4+16+36)/3 = 56/3
    check("Moment({{1, 2}, {3, 4}, {5, 6}}, 2)", //
        "{35/3,56/3}");
    check("Moment({{1, 2}, {3, 4}, {5, 6}}, 0)", //
        "{1,1}");
  }

  @Test
  public void testMomentMultivariate() {
    // Expected value of X^1 * Y^1 = (1*2 + 3*4 + 5*6)/3 = 44/3
    check("Moment({{1, 2}, {3, 4}, {5, 6}}, {1, 1})", //
        "44/3");

    // Expected value of X^2 * Y^0 = (1*1 + 9*1 + 25*1)/3 = 35/3
    check("Moment({{1, 2}, {3, 4}, {5, 6}}, {2, 0})", //
        "35/3");

    // Expected value of X^0 * Y^2 = (1*4 + 1*16 + 1*36)/3 = 56/3
    check("Moment({{1, 2}, {3, 4}, {5, 6}}, {0, 2})", //
        "56/3");

    // Expected value of X^2 * Y^1 = (1*2 + 9*4 + 25*6)/3 = 188/3
    check("Moment({{1, 2}, {3, 4}, {5, 6}}, {2, 1})", //
        "188/3");

    // Entirely zero dimension mapping
    check("Moment({{1, 2}, {3, 4}, {5, 6}}, {0, 0})", "1");
  }

  @Test
  public void testMomentFormal() {
    // 1-argument formal definitions remain unevaluated
    check("Moment(r)", //
        "Moment(r)");
    check("Moment(3)", //
        "Moment(3)");
    check("Moment({2, 3})", //
        "Moment({2,3})");
  }

  @Test
  public void testMomentSymbolic() {
    check("Moment({a, b}, 2)", //
        "1/2*(a^2+b^2)");
    check("Moment({{a, b}, {c, d}}, {1, 1})", //
        "1/2*(a*b+c*d)");
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
