package org.matheclipse.core.rubi.issues;

import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.rubi.AbstractRubiTestCase;

// Integrate[x*(2*c+3*d*x)*(a+c*x^2+d*x^3)^n,x]
/**
 *
 *
 * <pre>
 *   Int[(Pp_)*(Qq_)^(m_.), x_Symbol] :=
 *       With[{p = Expon[Pp, x], q = Expon[Qq, x]}, Simp[(Coeff[Pp, x, p]*x^(p - q + 1)*Qq^(m + 1))/((p + m*q + 1)*Coeff[Qq, x, q]), x]
 *                 /; NeQ[p + m*q + 1, 0] && EqQ[(p + m*q + 1)*Coeff[Qq, x, q]*Pp, Coeff[Pp, x, p]*x^(p - q)*((p - q + 1)*Qq + (m + 1)*x*D[Qq, x])]]
 *       /; FreeQ[m, x] && PolyQ[Pp, x] && PolyQ[Qq, x] && NeQ[m, -1]
 * </pre>
 *
 * <pre>
 * IIntegrate(1588,Int(Times($p("§pp"),Power($p("§qq"),m_DEFAULT)),x_Symbol),
 *   Condition(With(List(Set(p,Expon($s("§pp"),x)),Set(q,Expon($s("§qq"),x))),
 *              Condition(Simp(Times(Coeff($s("§pp"),x,p),Power(x,Plus(p,Negate(q),C1)),Power($s("§qq"),Plus(m,C1)),Power(Times(Plus(p,Times(m,q),C1),Coeff($s("§qq"),x,q)),-1)),x),And(NeQ(Plus(p,Times(m,q),C1),C0),EqQ(Times(Plus(p,Times(m,q),C1),Coeff($s("§qq"),x,q),$s("§pp")),Times(Coeff($s("§pp"),x,p),Power(x,Plus(p,Negate(q))),Plus(Times(Plus(p,Negate(q),C1),$s("§qq")),Times(Plus(m,C1),x,D($s("§qq"),x)))))))),And(FreeQ(m,x),PolyQ($s("§pp"),x),PolyQ($s("§qq"),x),NeQ(m,CN1)))),
 * </pre>
 */
public class RubiIssue77 extends AbstractRubiTestCase {

  public RubiIssue77(String name) {
    super(name, false);
  }

  public void testRuleNo1588a() {
    try {
      // prints 4 combinations
      // {(2*c+3*d*x)*(a+c*x^2+d*x^3)^n,x,1}
      // {x*(a+c*x^2+d*x^3)^n,2*c+3*d*x,1}
      // {x*(2*c+3*d*x),a+c*x^2+d*x^3,n}
      // {x*(2*c+3*d*x),(a+c*x^2+d*x^3)^n,1}
      IExpr expr =
          fEvaluator.eval("myfunction[(pp_)*(qq_)^(m_.), x_Symbol] := {pp,qq,m}/;Print[{pp,qq,m}]");
      IExpr lhsEval = fEvaluator.eval("myfunction[x*(2*c+3*d*x)*(a+c*x^2+d*x^3)^n, x]");
      assertEquals(lhsEval.toString(), "myfunction(x*(2*c+3*d*x)*(a+c*x^2+d*x^3)^n,x)");
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public void testRuleNo1588b() {
    try {
      IExpr lhsEval = fEvaluator.eval("Integrate[x*(2*c+3*d*x)*(a+c*x^2+d*x^3)^n,x]");
      assertEquals(lhsEval.toString(), "(a+c*x^2+d*x^3)^(1+n)/(1+n)");
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public void test0001() {
    check("Rubi`Coeff[x*(2*c + 3*d*x), x, 2]", "3*d");
  }

  public void test0002() {
    check("Rubi`Coeff[a + c*x^2 + d*x^3, x, 3]", "d");
  }

  public void test0003() {
    check("Rubi`EqQ[0, 0]", "True");
  }

  public void test0004() {
    check("Rubi`EqQ[1, 0]", "False");
  }

  public void test0005() {
    check("Rubi`EqQ[1, 1]", "True");
  }

  public void test0006() {
    check("Rubi`EqQ[2, -1]", "False");
  }

  public void test0007() {
    check("Rubi`EqQ[2, 6]", "False");
  }

  public void test0008() {
    check("Rubi`EqQ[3, -1]", "False");
  }

  public void test0009() {
    check("Rubi`EqQ[3, 1/2]", "False");
  }

  public void test0010() {
    check("Rubi`EqQ[3, 1]", "False");
  }

  public void test0011() {
    check("Rubi`EqQ[3, 4]", "False");
  }

  public void test0012() {
    check("Rubi`EqQ[a, 0]", "False");
  }

  public void test0013() {
    check("Rubi`EqQ[c, 1/2]", "False");
  }

  public void test0014() {
    check("Rubi`EqQ[2*c, 0]", "False");
  }

  public void test0015() {
    check("Rubi`EqQ[a + (4*c^3)/(27*d^2), 0]", "False");
  }

  public void test0016() {
    check("Rubi`EqQ[(-2*c)/(3*d), 0]", "False");
  }

  public void test0017() {
    check("Rubi`EqQ[d, 1/2]", "False");
  }

  public void test0018() {
    check("Rubi`EqQ[3*d, 0]", "False");
  }

  public void test0019() {
    check("Rubi`EqQ[x, 0]", "False");
  }

  public void test0020() {
    check("Rubi`EqQ[c + x, 0]", "False");
  }

  public void test0021() {
    check("Rubi`EqQ[d + x, 0]", "False");
  }

  public void test0022() {
    check("Rubi`EqQ[1 + n + x, 0]", "False");
  }

  public void test0023() {
    check("Rubi`EqQ[(1 + n)^(-1) + x, 0]", "False");
  }

  public void test0024() {
    check("Rubi`EqQ[2*c + 3*d*x, 0]", "False");
  }

  public void test0025() {
    check("Rubi`EqQ[d*(3 + 3*n)*x*(2*c + 3*d*x), 3*d*(1 + n)*(2*c*x + 3*d*x^2)]", "True");
  }

  public void test0026() {
    check("Rubi`EqQ[1 + n + x^2, 0]", "False");
  }

  public void test0027() {
    check("Rubi`Expon[x*(2*c + 3*d*x), x]", "2");
  }

  public void test0028() {
    check("Rubi`Expon[a + c*x^2 + d*x^3, x]", "3");
  }

  public void test0029() {
    check("Rubi`FactorNumericGcd[a]", "a");
  }

  public void test0030() {
    check("Rubi`FactorNumericGcd[c]", "c");
  }

  public void test0031() {
    check("Rubi`FactorNumericGcd[d]", "d");
  }

  public void test0032() {
    check("Rubi`FactorNumericGcd[1 + n]", "1 + n");
  }

  public void test0033() {
    check("Rubi`FactorNumericGcd[(3 + 3*n)^(-1)]", "1/(3*(1 + n))");
  }

  public void test0034() {
    check("Rubi`FactorNumericGcd[3 + 3*n]", "3*(1 + n)");
  }

  public void test0035() {
    check("Rubi`FactorNumericGcd[x]", "x");
  }

  public void test0036() {
    check("Rubi`FactorNumericGcd[x^2]", "x^2");
  }

  public void test0037() {
    check("Rubi`FactorNumericGcd[c*x^2]", "c*x^2");
  }

  public void test0038() {
    check("Rubi`FactorNumericGcd[d*x^2]", "d*x^2");
  }

  public void test0039() {
    check("Rubi`FactorNumericGcd[x^2/(3 + 3*n)]", "x^2/(3*(1 + n))");
  }

  public void test0040() {
    check("Rubi`FactorNumericGcd[x^3]", "x^3");
  }

  public void test0041() {
    check("Rubi`FactorNumericGcd[(a + c*x^2 + d*x^3)^(1 + n)]", "(a + c*x^2 + d*x^3)^(1 + n)");
  }

  public void test0042() {
    check("Rubi`FalseQ[False]", "True");
  }

  public void test0043() {
    check("Rubi`FixSimplify[a]", "a");
  }

  public void test0044() {
    check("Rubi`FixSimplify[1 + n]", "1 + n");
  }

  public void test0045() {
    check("Rubi`FixSimplify[c*x^2]", "c*x^2");
  }

  public void test0046() {
    check("Rubi`FixSimplify[d*x^2]", "d*x^2");
  }

  public void test0047() {
    check("Rubi`FixSimplify[x^2/(3*(1 + n))]", "x^2/(3*(1 + n))");
  }

  public void test0048() {
    check("Rubi`FractionalPowerOfSquareQ[-1]", "False");
  }

  public void test0049() {
    check("Rubi`FractionalPowerOfSquareQ[1]", "False");
  }

  public void test0050() {
    check("Rubi`FractionalPowerOfSquareQ[2]", "False");
  }

  public void test0051() {
    check("Rubi`FractionalPowerOfSquareQ[3]", "False");
  }

  public void test0052() {
    check("Rubi`FractionalPowerOfSquareQ[a]", "False");
  }

  public void test0053() {
    check("Rubi`FractionalPowerOfSquareQ[c]", "False");
  }

  public void test0054() {
    check("Rubi`FractionalPowerOfSquareQ[d]", "False");
  }

  public void test0055() {
    check("Rubi`FractionalPowerOfSquareQ[n]", "False");
  }

  public void test0056() {
    check("Rubi`FractionalPowerOfSquareQ[3*n]", "False");
  }

  public void test0057() {
    check("Rubi`FractionalPowerOfSquareQ[1 + n]", "False");
  }

  public void test0058() {
    check("Rubi`FractionalPowerOfSquareQ[(3 + 3*n)^(-1)]", "False");
  }

  public void test0059() {
    check("Rubi`FractionalPowerOfSquareQ[3 + 3*n]", "False");
  }

  public void test0060() {
    check("Rubi`FractionalPowerOfSquareQ[x]", "False");
  }

  public void test0061() {
    check("Rubi`FractionalPowerOfSquareQ[x^2]", "False");
  }

  public void test0062() {
    check("Rubi`FractionalPowerOfSquareQ[c*x^2]", "False");
  }

  public void test0063() {
    check("Rubi`FractionalPowerOfSquareQ[d*x^2]", "False");
  }

  public void test0064() {
    check("Rubi`FractionalPowerOfSquareQ[x^2/(3 + 3*n)]", "False");
  }

  public void test0065() {
    check("Rubi`FractionQ[-1]", "False");
  }

  public void test0066() {
    check("Rubi`FractionQ[2]", "False");
  }

  public void test0067() {
    check("Rubi`FreeFactors[c*x^2, x]", "c");
  }

  public void test0068() {
    check("Rubi`FreeFactors[d*x^3, x]", "d");
  }

  public void test0069() {
    check("Rubi`FreeFactors[(3*(a + c*x^2 + d*x^3)^(1 + n))/(3 + 3*n), x]", "3/(3 + 3*n)");
  }

  public void test0070() {
    check("Rubi`HyperbolicQ[x^2]", "False");
  }

  public void test0071() {
    check("Rubi`HyperbolicQ[x^3]", "False");
  }

  public void test0072() {
    check("Rubi`HyperbolicQ[(a + c*x^2 + d*x^3)^(1 + n)]", "False");
  }

  public void test0073() {
    check("Rubi`IntegersQ[1, 1]", "True");
  }

  public void test0074() {
    check("Rubi`IntegersQ[n, n^(-1)]", "False");
  }

  public void test0075() {
    check("Rubi`LinearQ[{x, 2*c + 3*d*x, a + c*x^2 + d*x^3}, x]", "False");
  }

  public void test0076() {
    check("Rubi`LinearQ[{x, 2*c + 3*d*x, (a + c*x^2 + d*x^3)^n}, x]", "False");
  }

  public void test0077() {
    check("Rubi`LinearQ[{x, a + c*x^2 + d*x^3, 2*c + 3*d*x}, x]", "False");
  }

  public void test0078() {
    check("Rubi`LinearQ[{x, (a + c*x^2 + d*x^3)^n, 2*c + 3*d*x}, x]", "False");
  }

  public void test0079() {
    check("Rubi`LinearQ[{2*c + 3*d*x, x, a + c*x^2 + d*x^3}, x]", "False");
  }

  public void test0080() {
    check("Rubi`LinearQ[{2*c + 3*d*x, x, (a + c*x^2 + d*x^3)^n}, x]", "False");
  }

  public void test0081() {
    check("Rubi`LinearQ[{2*c + 3*d*x, a + c*x^2 + d*x^3, x}, x]", "False");
  }

  public void test0082() {
    check("Rubi`LinearQ[{2*c + 3*d*x, (a + c*x^2 + d*x^3)^n, x}, x]", "False");
  }

  public void test0083() {
    check("Rubi`LinearQ[{a + c*x^2 + d*x^3, x, 2*c + 3*d*x}, x]", "False");
  }

  public void test0084() {
    check("Rubi`LinearQ[{a + c*x^2 + d*x^3, 2*c + 3*d*x, x}, x]", "False");
  }

  public void test0085() {
    check("Rubi`LinearQ[{(a + c*x^2 + d*x^3)^n, x, 2*c + 3*d*x}, x]", "False");
  }

  public void test0086() {
    check("Rubi`LinearQ[{(a + c*x^2 + d*x^3)^n, 2*c + 3*d*x, x}, x]", "False");
  }

  public void test0087() {
    check("Rubi`MergeableFactorQ[c, 1, x^2]", "False");
  }

  public void test0088() {
    check("Rubi`MergeableFactorQ[d, 1, x^3]", "False");
  }

  public void test0089() {
    check("Rubi`MergeableFactorQ[1 + n, -1, (a + c*x^2 + d*x^3)^(1 + n)]", "False");
  }

  public void test0090() {
    check("Rubi`MergeFactors[c, x^2]", "c*x^2");
  }

  public void test0091() {
    check("Rubi`MergeFactors[d, x^3]", "d*x^3");
  }

  public void test0092() {
    check("Rubi`MergeFactors[(1 + n)^(-1), (a + c*x^2 + d*x^3)^(1 + n)]",
        "(a + c*x^2 + d*x^3)^(1 + n)/(1 + n)");
  }

  public void test0093() {
    check("Rubi`NeQ[1, 0]", "True");
  }

  public void test0094() {
    check("Rubi`NeQ[3*d, 0]", "True");
  }

  public void test0095() {
    check("Rubi`NeQ[n, -1]", "True");
  }

  public void test0096() {
    check("Rubi`NeQ[3 + 3*n, 0]", "True");
  }

  public void test0097() {
    check("Rubi`NonfreeFactors[c*x^2, x]", "x^2");
  }

  public void test0098() {
    check("Rubi`NonfreeFactors[d*x^3, x]", "x^3");
  }

  public void test0099() {
    check("Rubi`NonfreeFactors[(3*(a + c*x^2 + d*x^3)^(1 + n))/(3 + 3*n), x]",
        "(a + c*x^2 + d*x^3)^(1 + n)");
  }

  public void test0100() {
    check("Rubi`NonnumericFactors[3]", "1");
  }

  public void test0101() {
    check("Rubi`NonnumericFactors[c]", "c");
  }

  public void test0102() {
    check("Rubi`NonnumericFactors[d]", "d");
  }

  public void test0103() {
    check("Rubi`NonnumericFactors[(3 + 3*n)^(-1)]", "(3 + 3*n)^(-1)");
  }

  public void test0104() {
    check("Rubi`NonnumericFactors[3/(3 + 3*n)]", "(3 + 3*n)^(-1)");
  }

  public void test0105() {
    check("Rubi`NormalizeSumFactors[-1]", "-1");
  }

  public void test0106() {
    check("Rubi`NormalizeSumFactors[1]", "1");
  }

  public void test0107() {
    check("Rubi`NormalizeSumFactors[2]", "2");
  }

  public void test0108() {
    check("Rubi`NormalizeSumFactors[3]", "3");
  }

  public void test0109() {
    check("Rubi`NormalizeSumFactors[a]", "a");
  }

  public void test0110() {
    check("Rubi`NormalizeSumFactors[c]", "c");
  }

  public void test0111() {
    check("Rubi`NormalizeSumFactors[d]", "d");
  }

  public void test0112() {
    check("Rubi`NormalizeSumFactors[n]", "n");
  }

  public void test0113() {
    check("Rubi`NormalizeSumFactors[(1 + n)^(-1)]", "(1 + n)^(-1)");
  }

  public void test0114() {
    check("Rubi`NormalizeSumFactors[1 + n]", "1 + n");
  }

  public void test0115() {
    check("Rubi`NormalizeSumFactors[x]", "x");
  }

  public void test0116() {
    check("Rubi`NormalizeSumFactors[x^2]", "x^2");
  }

  public void test0117() {
    check("Rubi`NormalizeSumFactors[c*x^2]", "c*x^2");
  }

  public void test0118() {
    check("Rubi`NormalizeSumFactors[x^3]", "x^3");
  }

  public void test0119() {
    check("Rubi`NormalizeSumFactors[d*x^3]", "d*x^3");
  }

  public void test0120() {
    check("Rubi`NormalizeSumFactors[a + c*x^2 + d*x^3]", "a + c*x^2 + d*x^3");
  }

  public void test0121() {
    check("Rubi`NormalizeSumFactors[(a + c*x^2 + d*x^3)^(1 + n)]", "(a + c*x^2 + d*x^3)^(1 + n)");
  }

  public void test0122() {
    check("Rubi`NormalizeSumFactors[(a + c*x^2 + d*x^3)^(1 + n)/(1 + n)]",
        "(a + c*x^2 + d*x^3)^(1 + n)/(1 + n)");
  }

  public void test0123() {
    check("Rubi`NumericFactor[1]", "1");
  }

  public void test0124() {
    check("Rubi`NumericFactor[3]", "3");
  }

  public void test0125() {
    check("Rubi`NumericFactor[c]", "1");
  }

  public void test0126() {
    check("Rubi`NumericFactor[d]", "1");
  }

  public void test0127() {
    check("Rubi`NumericFactor[n]", "1");
  }

  public void test0128() {
    check("Rubi`NumericFactor[3*n]", "3");
  }

  public void test0129() {
    check("Rubi`NumericFactor[(3 + 3*n)^(-1)]", "1");
  }

  public void test0130() {
    check("Rubi`NumericFactor[3/(3 + 3*n)]", "3");
  }

  public void test0131() {
    check("Rubi`PolyQ[x, x]", "True");
  }

  public void test0132() {
    check("Rubi`PolyQ[2*c + 3*d*x, x]", "True");
  }

  public void test0133() {
    check("Rubi`PolyQ[x*(2*c + 3*d*x), x]", "True");
  }

  public void test0134() {
    check("Rubi`PolyQ[a + c*x^2 + d*x^3, x]", "True");
  }

  public void test0135() {
    check("Rubi`PolyQ[(a + c*x^2 + d*x^3)^n, x]", "False");
  }

  public void test0136() {
    check("Rubi`PolyQ[x*(a + c*x^2 + d*x^3)^n, x]", "False");
  }

  public void test0137() {
    check("Rubi`PolyQ[(2*c + 3*d*x)*(a + c*x^2 + d*x^3)^n, x]", "False");
  }

  public void test0138() {
    check("Rubi`PolyQ[x, x, 1]", "True");
  }

  public void test0139() {
    check("Rubi`PolyQ[2*c + 3*d*x, x, 1]", "True");
  }

  public void test0140() {
    check("Rubi`PolyQ[a + c*x^2 + d*x^3, x, 1]", "False");
  }

  public void test0141() {
    check("Rubi`PolyQ[(a + c*x^2 + d*x^3)^n, x, 1]", "False");
  }

  public void test0142() {
    check("Rubi`PowerQ[a]", "False");
  }

  public void test0143() {
    check("Rubi`PowerQ[c]", "False");
  }

  public void test0144() {
    check("Rubi`PowerQ[d]", "False");
  }

  public void test0145() {
    check("Rubi`PowerQ[n]", "False");
  }

  public void test0146() {
    check("Rubi`PowerQ[3*n]", "False");
  }

  public void test0147() {
    check("Rubi`PowerQ[(1 + n)^(-1)]", "True");
  }

  public void test0148() {
    check("Rubi`PowerQ[1 + n]", "False");
  }

  public void test0149() {
    check("Rubi`PowerQ[(3 + 3*n)^(-1)]", "True");
  }

  public void test0150() {
    check("Rubi`PowerQ[3/(3 + 3*n)]", "False");
  }

  public void test0151() {
    check("Rubi`PowerQ[3 + 3*n]", "False");
  }

  public void test0152() {
    check("Rubi`PowerQ[x]", "False");
  }

  public void test0153() {
    check("Rubi`PowerQ[x^2]", "True");
  }

  public void test0154() {
    check("Rubi`PowerQ[c*x^2]", "False");
  }

  public void test0155() {
    check("Rubi`PowerQ[d*x^2]", "False");
  }

  public void test0156() {
    check("Rubi`PowerQ[x^2/(3 + 3*n)]", "False");
  }

  public void test0157() {
    check("Rubi`PowerQ[x^3]", "True");
  }

  public void test0158() {
    check("Rubi`PowerQ[(a + c*x^2 + d*x^3)^(1 + n)]", "True");
  }

  public void test0159() {
    check("Rubi`ProductQ[a]", "False");
  }

  public void test0160() {
    check("Rubi`ProductQ[c]", "False");
  }

  public void test0161() {
    check("Rubi`ProductQ[d]", "False");
  }

  public void test0162() {
    check("Rubi`ProductQ[n]", "False");
  }

  public void test0163() {
    check("Rubi`ProductQ[3*n]", "True");
  }

  public void test0164() {
    check("Rubi`ProductQ[(1 + n)^(-1)]", "False");
  }

  public void test0165() {
    check("Rubi`ProductQ[1 + n]", "False");
  }

  public void test0166() {
    check("Rubi`ProductQ[3/(3 + 3*n)]", "True");
  }

  public void test0167() {
    check("Rubi`ProductQ[3 + 3*n]", "False");
  }

  public void test0168() {
    check("Rubi`ProductQ[x]", "False");
  }

  public void test0169() {
    check("Rubi`ProductQ[x^2]", "False");
  }

  public void test0170() {
    check("Rubi`ProductQ[c*x^2]", "True");
  }

  public void test0171() {
    check("Rubi`ProductQ[d*x^2]", "True");
  }

  public void test0172() {
    check("Rubi`ProductQ[x^2/(3 + 3*n)]", "True");
  }

  public void test0173() {
    check("Rubi`ProductQ[x^3]", "False");
  }

  public void test0174() {
    check("Rubi`ProductQ[d*x^3]", "True");
  }

  public void test0175() {
    check("Rubi`ProductQ[a + c*x^2 + d*x^3]", "False");
  }

  public void test0176() {
    check("Rubi`ProductQ[(a + c*x^2 + d*x^3)^(1 + n)]", "False");
  }

  public void test0177() {
    check("Rubi`ProductQ[(a + c*x^2 + d*x^3)^(1 + n)/(1 + n)]", "True");
  }

  public void test0178() {
    check("Rubi`ProductQ[(3*(a + c*x^2 + d*x^3)^(1 + n))/(3 + 3*n)]", "True");
  }

  public void test0179() {
    check("Rubi`PseudoBinomialPairQ[x*(2*c + 3*d*x), a + c*x^2 + d*x^3, x]", "False");
  }

  public void test0180() {
    check("Rubi`PseudoBinomialPairQ[x*(2*c + 3*d*x), (a + c*x^2 + d*x^3)^n, x]", "False");
  }

  public void test0181() {
    check("Rubi`PseudoBinomialPairQ[x*(a + c*x^2 + d*x^3)^n, 2*c + 3*d*x, x]", "False");
  }

  public void test0182() {
    check("Rubi`PseudoBinomialParts[x*(2*c + 3*d*x), x]", "False");
  }

  public void test0183() {
    check("Rubi`PseudoBinomialParts[x*(a + c*x^2 + d*x^3)^n, x]", "False");
  }

  public void test0184() {
    check("Rubi`RationalQ[-1]", "True");
  }

  public void test0185() {
    check("Rubi`RationalQ[1/3]", "True");
  }

  public void test0186() {
    check("Rubi`RationalQ[1]", "True");
  }

  public void test0187() {
    check("Rubi`RationalQ[2]", "True");
  }

  public void test0188() {
    check("Rubi`RationalQ[3]", "True");
  }

  public void test0189() {
    check("Rubi`RationalQ[c]", "False");
  }

  public void test0190() {
    check("Rubi`RationalQ[d]", "False");
  }

  public void test0191() {
    check("Rubi`RationalQ[n]", "False");
  }

  public void test0192() {
    check("Rubi`RationalQ[(1 + n)^(-1)]", "False");
  }

  public void test0193() {
    check("Rubi`RationalQ[1 + n]", "False");
  }

  public void test0194() {
    check("Rubi`RationalQ[3 + 3*n]", "False");
  }

  public void test0195() {
    check("Rubi`RationalQ[x]", "False");
  }

  public void test0196() {
    check("Rubi`RationalQ[x^2]", "False");
  }

  public void test0197() {
    check("Rubi`RationalQ[c*x^2]", "False");
  }

  public void test0198() {
    check("Rubi`RationalQ[x^3]", "False");
  }

  public void test0199() {
    check("Rubi`RationalQ[d*x^3]", "False");
  }

  public void test0200() {
    check("Rubi`RationalQ[(a + c*x^2 + d*x^3)^(1 + n)]", "False");
  }

  public void test0201() {
    check("Rubi`RationalQ[(a + c*x^2 + d*x^3)^(1 + n)/(1 + n)]", "False");
  }

  public void test0202() {
    check("Rubi`SignOfFactor[c]", "{1, c}");
  }

  public void test0203() {
    check("Rubi`SignOfFactor[d]", "{1, d}");
  }

  public void test0204() {
    check("Rubi`SignOfFactor[(1 + n)^(-1)]", "{1, (1 + n)^(-1)}");
  }

  public void test0205() {
    check("Rubi`SignOfFactor[x^2]", "{1, x^2}");
  }

  public void test0206() {
    check("Rubi`SignOfFactor[c*x^2]", "{1, c*x^2}");
  }

  public void test0207() {
    check("Rubi`SignOfFactor[x^3]", "{1, x^3}");
  }

  public void test0208() {
    check("Rubi`SignOfFactor[d*x^3]", "{1, d*x^3}");
  }

  public void test0209() {
    check("Rubi`SignOfFactor[(a + c*x^2 + d*x^3)^(1 + n)]", "{1, (a + c*x^2 + d*x^3)^(1 + n)}");
  }

  public void test0210() {
    check("Rubi`SignOfFactor[(a + c*x^2 + d*x^3)^(1 + n)/(1 + n)]",
        "{1, (a + c*x^2 + d*x^3)^(1 + n)/(1 + n)}");
  }

  public void test0211() {
    check("Rubi`Simp[(3*(a + c*x^2 + d*x^3)^(1 + n))/(3 + 3*n), x]",
        "(a + c*x^2 + d*x^3)^(1 + n)/(1 + n)");
  }

  public void test0212() {
    check("Rubi`SimpFixFactor[c, x]", "c");
  }

  public void test0213() {
    check("Rubi`SimpFixFactor[d, x]", "d");
  }

  public void test0214() {
    check("Rubi`SimpFixFactor[(1 + n)^(-1), x]", "(1 + n)^(-1)");
  }

  public void test0215() {
    check("Rubi`SimpFixFactor[x^2, x]", "x^2");
  }

  public void test0216() {
    check("Rubi`SimpFixFactor[x^3, x]", "x^3");
  }

  public void test0217() {
    check("Rubi`SimpFixFactor[(a + c*x^2 + d*x^3)^(1 + n), x]", "(a + c*x^2 + d*x^3)^(1 + n)");
  }

  public void test0218() {
    check("Rubi`SimpHelp[2, x]", "2");
  }

  public void test0219() {
    check("Rubi`SimpHelp[3, x]", "3");
  }

  public void test0220() {
    check("Rubi`SimpHelp[1 + n, x]", "1 + n");
  }

  public void test0221() {
    check("Rubi`SimpHelp[x, x]", "x");
  }

  public void test0222() {
    check("Rubi`SimpHelp[x^2, x]", "x^2");
  }

  public void test0223() {
    check("Rubi`SimpHelp[c*x^2, x]", "c*x^2");
  }

  public void test0224() {
    check("Rubi`SimpHelp[x^3, x]", "x^3");
  }

  public void test0225() {
    check("Rubi`SimpHelp[d*x^3, x]", "d*x^3");
  }

  public void test0226() {
    check("Rubi`SimpHelp[a + c*x^2 + d*x^3, x]", "a + c*x^2 + d*x^3");
  }

  public void test0227() {
    check("Rubi`SimpHelp[(a + c*x^2 + d*x^3)^(1 + n), x]", "(a + c*x^2 + d*x^3)^(1 + n)");
  }

  public void test0228() {
    check("Rubi`SimpHelp[(3*(a + c*x^2 + d*x^3)^(1 + n))/(3 + 3*n), x]",
        "(a + c*x^2 + d*x^3)^(1 + n)/(1 + n)");
  }

  public void test0229() {
    check("Rubi`SmartSimplify[a]", "a");
  }

  public void test0230() {
    check("Rubi`SmartSimplify[1 + n]", "1 + n");
  }

  public void test0231() {
    check("Rubi`SmartSimplify[c*x^2]", "c*x^2");
  }

  public void test0232() {
    check("Rubi`SmartSimplify[d*x^2]", "d*x^2");
  }

  public void test0233() {
    check("Rubi`SmartSimplify[x^2/(3 + 3*n)]", "x^2/(3*(1 + n))");
  }

  public void test0234() {
    check("Rubi`StopFunctionQ[(1 + n)^(-1)]", "False");
  }

  public void test0235() {
    check("Rubi`StopFunctionQ[1 + n]", "False");
  }

  public void test0236() {
    check("Rubi`StopFunctionQ[x^2]", "False");
  }

  public void test0237() {
    check("Rubi`StopFunctionQ[c*x^2]", "False");
  }

  public void test0238() {
    check("Rubi`StopFunctionQ[x^3]", "False");
  }

  public void test0239() {
    check("Rubi`StopFunctionQ[d*x^3]", "False");
  }

  public void test0240() {
    check("Rubi`StopFunctionQ[a + c*x^2 + d*x^3]", "False");
  }

  public void test0241() {
    check("Rubi`StopFunctionQ[(a + c*x^2 + d*x^3)^(1 + n)]", "False");
  }

  public void test0242() {
    check("Rubi`StopFunctionQ[(a + c*x^2 + d*x^3)^(1 + n)/(1 + n)]", "False");
  }

  public void test0243() {
    check("Rubi`StopFunctionQ[(3*(a + c*x^2 + d*x^3)^(1 + n))/(3 + 3*n)]", "False");
  }

  public void test0244() {
    check("Rubi`SumQ[a]", "False");
  }

  public void test0245() {
    check("Rubi`SumQ[c]", "False");
  }

  public void test0246() {
    check("Rubi`SumQ[d]", "False");
  }

  public void test0247() {
    check("Rubi`SumQ[n]", "False");
  }

  public void test0248() {
    check("Rubi`SumQ[(1 + n)^(-1)]", "False");
  }

  public void test0249() {
    check("Rubi`SumQ[1 + n]", "True");
  }

  public void test0250() {
    check("Rubi`SumQ[3 + 3*n]", "True");
  }

  public void test0251() {
    check("Rubi`SumQ[x]", "False");
  }

  public void test0252() {
    check("Rubi`SumQ[x^2]", "False");
  }

  public void test0253() {
    check("Rubi`SumQ[c*x^2]", "False");
  }

  public void test0254() {
    check("Rubi`SumQ[x^3]", "False");
  }

  public void test0255() {
    check("Rubi`SumQ[d*x^3]", "False");
  }

  public void test0256() {
    check("Rubi`SumQ[c*x^2 + d*x^3]", "True");
  }

  public void test0257() {
    check("Rubi`SumQ[a + c*x^2 + d*x^3]", "True");
  }

  public void test0258() {
    check("Rubi`SumQ[(2*c + 3*d*x)*(a + c*x^2 + d*x^3)^n]", "False");
  }

  public void test0259() {
    check("Rubi`SumQ[x*(2*c + 3*d*x)*(a + c*x^2 + d*x^3)^n]", "False");
  }

  public void test0260() {
    check("Rubi`SumQ[(a + c*x^2 + d*x^3)^(1 + n)]", "False");
  }

  public void test0261() {
    check("Rubi`SumQ[(a + c*x^2 + d*x^3)^(1 + n)/(1 + n)]", "False");
  }

  public void test0262() {
    check("Rubi`TrigQ[x^2]", "False");
  }

  public void test0263() {
    check("Rubi`TrigQ[x^3]", "False");
  }

  public void test0264() {
    check("Rubi`TrigQ[(a + c*x^2 + d*x^3)^(1 + n)]", "False");
  }

  public void test0265() {
    check("PolynomialQ[(2*c+3*d*x)*(a+c*x^2+d*x^3)^n,x]", "False");
    check("Rubi`PolyQ[(2*c+3*d*x)*(a+c*x^2+d*x^3)^n,x]", "False");
  }

  public void test0266() {
    check(
        "FreeQ[1,x]&&Rubi`PolyQ[(2*c+3*d*x)*(a+c*x^2+d*x^3)^n,x]&&Rubi`PolyQ[x,x]&&Rubi`NeQ[1,-1]",
        "False");
  }

  public void test0268a() {
    check(
        "Rubi`Coeff[x,x,1]*x^(1+(-1)*1+(-1)*3)*((1+(-1)*1+(-1)*3+1)*(2*c+3*d*x)*(a+c*x^2+d*x^3)+(1+1)*x*(a+c*x^2+d*x^3)*D[2*c+3*d*x,x]+(1+n)*x*(2*c+3*d*x)*D[a+c*x^2+d*x^3,x])", //
        "((1+n)*x*(2*c+3*d*x)*(2*c*x+3*d*x^2)+6*d*x*(a+c*x^2+d*x^3)-2*(2*c+3*d*x)*(a+c*x^\n"
            + "2+d*x^3))/x^3");

    check(
        "Rubi`EqQ[3*d^2*(3+3*n)*x,((1+n)*x*(2*c+3*d*x)*(2*c*x+3*d*x^2)+6*d*x*(a+c*x^2+d*x^3)-2*(2*c+3*d*x)*(a+c*x^2+d*x^3))/x^3]", //
        "False");
  }

  public void test0268b() {
    check("With[{a = 3}, True /; a > 1]", //
        "True");
  }

  public void test0268c() {
    check("D[2*c+3*d*x,x]+(1+n)*x*(2*c+3*d*x)*D[a+c*x^2+d*x^3,x]", //
        "3*d+(1+n)*x*(2*c+3*d*x)*(2*c*x+3*d*x^2)");
  }
}
