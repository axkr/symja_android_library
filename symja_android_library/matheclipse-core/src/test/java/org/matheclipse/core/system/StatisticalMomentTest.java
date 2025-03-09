package org.matheclipse.core.system;

import org.junit.Test;
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
  public void testCumulant() {
    // TODO
    // check("Cumulant({{a,b},{c,d}}, 4) //Simplify", //
    // "");
    check("Cumulant({a,b,c},4)", //
        "1/3*((a+1/3*(-a-b-c))^4+(b+1/3*(-a-b-c))^4+(1/3*(-a-b-c)+c)^4)");
    check("Cumulant({a,b,c,d},3)", //
        "1/4*((a+1/4*(-a-b-c-d))^3+(b+1/4*(-a-b-c-d))^3+(c+1/4*(-a-b-c-d))^3+(1/4*(-a-b-c-d)+d)^\n"
            + "3)");

  }

  @Test
  public void testFactorialMoment() {

    check("FactorialMoment(Array(Subscript(a, ##) &, {2, 2, 2}), 2) //  Simplify // MatrixForm", //
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
  public void testMoment() {
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

  /** The JUnit setup method */
  @Override
  public void setUp() {
    super.setUp();
    Config.SHORTEN_STRING_LENGTH = 1024;
    Config.MAX_AST_SIZE = 1000000;
    EvalEngine.get().setIterationLimit(50000);
  }

  @Override
  public void tearDown() throws Exception {
    super.tearDown();
    Config.SHORTEN_STRING_LENGTH = 80;
  }
}
