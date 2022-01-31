package org.matheclipse.core.rubi.issues;

import org.matheclipse.core.rubi.AbstractRubiTestCase;

public class RubiIssue76 extends AbstractRubiTestCase {
  public RubiIssue76(String name) {
    super(name, false);
  }

  public void test0001() {
    check("Integrate[F^(a+b/(c+d*x)^3)*(c+d*x),x]",
        "(F^a*(c+d*x)^2*Gamma[-2/3,(-b*Log[F])/(c+d*x)^3]*((-b*Log[F])/(c+d*x)^3)^(2/3))/(\n" + //
            "3*d)");
  }

  public void test0072() {
    check(
        "Rubi`FreeFactors[-(F^a*(c + d*x)^2*Gamma[-2/3, -((b*Log[F])/(c + d*x)^3)]*(-((b*Log[F])/(c + d*x)^3))^(2/3))/(3*d), x]",
        "-F^a/(3*d)");
  }

  public void test0134() {
    check(
        "Rubi`NonfreeFactors[-(F^a*(c + d*x)^2*Gamma[-2/3, -((b*Log[F])/(c + d*x)^3)]*(-((b*Log[F])/(c + d*x)^3))^(2/3))/(3*d), x]",
        "(c + d*x)^2*Gamma[-2/3, -((b*Log[F])/(c + d*x)^3)]*(-((b*Log[F])/(c + d*x)^3))^(2/3)");
  }

  public void test0182() {
    check("PolynomialQ[F, x]", "True");
    check("Rubi`PolyQ[F, x]", "True");
  }

  public void test0183() {
    check("PolynomialQ[F, x^2]", "True");
    check("Rubi`PolyQ[F, x^2]", "True");
  }

  public void test0184() {
    check("Rubi`PowerQ[(-((b*Log[F])/(c + d*x)^3))^(2/3)]", "True");
  }

  public void test0185() {
    check("Rubi`ProductQ[(-((b*Log[F])/(c + d*x)^3))^(2/3)]", "False");
  }

  public void test0220() {
    // check("Integrate[1/((2+3*x)*(52-54*x+27*x^2)^(1/3)), x]",//
    // "");
  }

  public void test0221() {
    check("3/Sqrt[3]", "Sqrt[3]");
    check("1/Sqrt[3]", "1/Sqrt[3]");
    check("Rubi`PowerQ[1/Sqrt[3]]", "True");
    check("Rubi`RationalQ[1/Sqrt[3]]", "False");
    check("Rubi`FractionQ[1/Sqrt[3]]", "False");
    check("Rubi`NumericFactor[1/Sqrt[3]]", "1/3");
    check("(1/Sqrt[3])/(1/3)", "3/Sqrt[3]");
    check("Rubi`ContentFactor[1/Sqrt[3]]", "1/Sqrt[3]");
    check("Rubi`NonnumericFactors[1/Sqrt[3]]", "Sqrt[3]");
  }

  public void test0222() {
    check("Rubi`NonnumericFactors[5^(-1/3)]", "5^(2/3)");
  }

  public void test0223() {
    check("Rubi`NonnumericFactors[2^(2/3)/(27*Sqrt[3]*5^(1/3))]", "Sqrt[3]*10^(2/3)");
  }

  public void test0224() {
    check("Rubi`NonnumericFactors[1/(6*10^(2/3))]", "10^(1/3)");
  }

  public void test0225() {
    check("Rubi`NonnumericFactors[10^(-2/3)]", "10^(1/3)");
  }

  public void test0226() {
    check("Rubi`NonnumericFactors[1/(3*Sqrt[3]*10^(2/3))]", "Sqrt[3]*10^(1/3)");
  }
}
