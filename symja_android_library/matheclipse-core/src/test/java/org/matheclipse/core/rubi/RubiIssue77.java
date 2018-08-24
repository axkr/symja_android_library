package org.matheclipse.core.rubi;

import org.matheclipse.core.interfaces.IExpr;

//Integrate[x*(2*c+3*d*x)*(a+c*x^2+d*x^3)^n,x]
/**
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
			IExpr expr = (IExpr) fEvaluator
					.eval("myfunction[(pp_)*(qq_)^(m_.), x_Symbol] := {pp,qq,m}/;Print[{pp,qq,m}]");
			IExpr lhsEval = (IExpr) fEvaluator.eval("myfunction[x*(2*c+3*d*x)*(a+c*x^2+d*x^3)^n, x]");
			assertEquals(lhsEval.toString(), "myfunction[x*(2*c+3*d*x)*(a+c*x^2+d*x^3)^n,x]");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void testRuleNo1588b() {
		try {
			IExpr lhsEval = (IExpr) fEvaluator.eval("Integrate[x*(2*c+3*d*x)*(a+c*x^2+d*x^3)^n,x]");
			assertEquals(lhsEval.toString(), "(a+c*x^2+d*x^3)^(1+n)/(1+n)");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void test0001() {
		check("Integrate::Coeff[x*(2*c + 3*d*x), x, 2]", "3*d");
	}

	public void test0002() {
		check("Integrate::Coeff[a + c*x^2 + d*x^3, x, 3]", "d");
	}

	public void test0003() {
		check("Integrate::EqQ[0, 0]", "True");
	}

	public void test0004() {
		check("Integrate::EqQ[1, 0]", "False");
	}

	public void test0005() {
		check("Integrate::EqQ[1, 1]", "True");
	}

	public void test0006() {
		check("Integrate::EqQ[2, -1]", "False");
	}

	public void test0007() {
		check("Integrate::EqQ[2, 6]", "False");
	}

	public void test0008() {
		check("Integrate::EqQ[3, -1]", "False");
	}

	public void test0009() {
		check("Integrate::EqQ[3, 1/2]", "False");
	}

	public void test0010() {
		check("Integrate::EqQ[3, 1]", "False");
	}

	public void test0011() {
		check("Integrate::EqQ[3, 4]", "False");
	}

	public void test0012() {
		check("Integrate::EqQ[a, 0]", "False");
	}

	public void test0013() {
		check("Integrate::EqQ[c, 1/2]", "False");
	}

	public void test0014() {
		check("Integrate::EqQ[2*c, 0]", "False");
	}

	public void test0015() {
		check("Integrate::EqQ[a + (4*c^3)/(27*d^2), 0]", "False");
	}

	public void test0016() {
		check("Integrate::EqQ[(-2*c)/(3*d), 0]", "False");
	}

	public void test0017() {
		check("Integrate::EqQ[d, 1/2]", "False");
	}

	public void test0018() {
		check("Integrate::EqQ[3*d, 0]", "False");
	}

	public void test0019() {
		check("Integrate::EqQ[x, 0]", "False");
	}

	public void test0020() {
		check("Integrate::EqQ[c + x, 0]", "False");
	}

	public void test0021() {
		check("Integrate::EqQ[d + x, 0]", "False");
	}

	public void test0022() {
		check("Integrate::EqQ[1 + n + x, 0]", "False");
	}

	public void test0023() {
		check("Integrate::EqQ[(1 + n)^(-1) + x, 0]", "False");
	}

	public void test0024() {
		check("Integrate::EqQ[2*c + 3*d*x, 0]", "False");
	}

	public void test0025() {
		check("Integrate::EqQ[d*(3 + 3*n)*x*(2*c + 3*d*x), 3*d*(1 + n)*(2*c*x + 3*d*x^2)]", "True");
	}

	public void test0026() {
		check("Integrate::EqQ[1 + n + x^2, 0]", "False");
	}

	public void test0027() {
		check("Integrate::Expon[x*(2*c + 3*d*x), x]", "2");
	}

	public void test0028() {
		check("Integrate::Expon[a + c*x^2 + d*x^3, x]", "3");
	}

	public void test0029() {
		check("Integrate::FactorNumericGcd[a]", "a");
	}

	public void test0030() {
		check("Integrate::FactorNumericGcd[c]", "c");
	}

	public void test0031() {
		check("Integrate::FactorNumericGcd[d]", "d");
	}

	public void test0032() {
		check("Integrate::FactorNumericGcd[1 + n]", "1 + n");
	}

	public void test0033() {
		check("Integrate::FactorNumericGcd[(3 + 3*n)^(-1)]", "1/(3*(1 + n))");
	}

	public void test0034() {
		check("Integrate::FactorNumericGcd[3 + 3*n]", "3*(1 + n)");
	}

	public void test0035() {
		check("Integrate::FactorNumericGcd[x]", "x");
	}

	public void test0036() {
		check("Integrate::FactorNumericGcd[x^2]", "x^2");
	}

	public void test0037() {
		check("Integrate::FactorNumericGcd[c*x^2]", "c*x^2");
	}

	public void test0038() {
		check("Integrate::FactorNumericGcd[d*x^2]", "d*x^2");
	}

	public void test0039() {
		check("Integrate::FactorNumericGcd[x^2/(3 + 3*n)]", "x^2/(3*(1 + n))");
	}

	public void test0040() {
		check("Integrate::FactorNumericGcd[x^3]", "x^3");
	}

	public void test0041() {
		check("Integrate::FactorNumericGcd[(a + c*x^2 + d*x^3)^(1 + n)]", "(a + c*x^2 + d*x^3)^(1 + n)");
	}

	public void test0042() {
		check("Integrate::FalseQ[False]", "True");
	}

	public void test0043() {
		check("Integrate::FixSimplify[a]", "a");
	}

	public void test0044() {
		check("Integrate::FixSimplify[1 + n]", "1 + n");
	}

	public void test0045() {
		check("Integrate::FixSimplify[c*x^2]", "c*x^2");
	}

	public void test0046() {
		check("Integrate::FixSimplify[d*x^2]", "d*x^2");
	}

	public void test0047() {
		check("Integrate::FixSimplify[x^2/(3*(1 + n))]", "x^2/(3*(1 + n))");
	}

	public void test0048() {
		check("Integrate::FractionalPowerOfSquareQ[-1]", "False");
	}

	public void test0049() {
		check("Integrate::FractionalPowerOfSquareQ[1]", "False");
	}

	public void test0050() {
		check("Integrate::FractionalPowerOfSquareQ[2]", "False");
	}

	public void test0051() {
		check("Integrate::FractionalPowerOfSquareQ[3]", "False");
	}

	public void test0052() {
		check("Integrate::FractionalPowerOfSquareQ[a]", "False");
	}

	public void test0053() {
		check("Integrate::FractionalPowerOfSquareQ[c]", "False");
	}

	public void test0054() {
		check("Integrate::FractionalPowerOfSquareQ[d]", "False");
	}

	public void test0055() {
		check("Integrate::FractionalPowerOfSquareQ[n]", "False");
	}

	public void test0056() {
		check("Integrate::FractionalPowerOfSquareQ[3*n]", "False");
	}

	public void test0057() {
		check("Integrate::FractionalPowerOfSquareQ[1 + n]", "False");
	}

	public void test0058() {
		check("Integrate::FractionalPowerOfSquareQ[(3 + 3*n)^(-1)]", "False");
	}

	public void test0059() {
		check("Integrate::FractionalPowerOfSquareQ[3 + 3*n]", "False");
	}

	public void test0060() {
		check("Integrate::FractionalPowerOfSquareQ[x]", "False");
	}

	public void test0061() {
		check("Integrate::FractionalPowerOfSquareQ[x^2]", "False");
	}

	public void test0062() {
		check("Integrate::FractionalPowerOfSquareQ[c*x^2]", "False");
	}

	public void test0063() {
		check("Integrate::FractionalPowerOfSquareQ[d*x^2]", "False");
	}

	public void test0064() {
		check("Integrate::FractionalPowerOfSquareQ[x^2/(3 + 3*n)]", "False");
	}

	public void test0065() {
		check("Integrate::FractionQ[-1]", "False");
	}

	public void test0066() {
		check("Integrate::FractionQ[2]", "False");
	}

	public void test0067() {
		check("Integrate::FreeFactors[c*x^2, x]", "c");
	}

	public void test0068() {
		check("Integrate::FreeFactors[d*x^3, x]", "d");
	}

	public void test0069() {
		check("Integrate::FreeFactors[(3*(a + c*x^2 + d*x^3)^(1 + n))/(3 + 3*n), x]", "3/(3 + 3*n)");
	}

	public void test0070() {
		check("Integrate::HyperbolicQ[x^2]", "False");
	}

	public void test0071() {
		check("Integrate::HyperbolicQ[x^3]", "False");
	}

	public void test0072() {
		check("Integrate::HyperbolicQ[(a + c*x^2 + d*x^3)^(1 + n)]", "False");
	}

	public void test0073() {
		check("Integrate::IntegersQ[1, 1]", "True");
	}

	public void test0074() {
		check("Integrate::IntegersQ[n, n^(-1)]", "False");
	}

	public void test0075() {
		check("Integrate::LinearQ[{x, 2*c + 3*d*x, a + c*x^2 + d*x^3}, x]", "False");
	}

	public void test0076() {
		check("Integrate::LinearQ[{x, 2*c + 3*d*x, (a + c*x^2 + d*x^3)^n}, x]", "False");
	}

	public void test0077() {
		check("Integrate::LinearQ[{x, a + c*x^2 + d*x^3, 2*c + 3*d*x}, x]", "False");
	}

	public void test0078() {
		check("Integrate::LinearQ[{x, (a + c*x^2 + d*x^3)^n, 2*c + 3*d*x}, x]", "False");
	}

	public void test0079() {
		check("Integrate::LinearQ[{2*c + 3*d*x, x, a + c*x^2 + d*x^3}, x]", "False");
	}

	public void test0080() {
		check("Integrate::LinearQ[{2*c + 3*d*x, x, (a + c*x^2 + d*x^3)^n}, x]", "False");
	}

	public void test0081() {
		check("Integrate::LinearQ[{2*c + 3*d*x, a + c*x^2 + d*x^3, x}, x]", "False");
	}

	public void test0082() {
		check("Integrate::LinearQ[{2*c + 3*d*x, (a + c*x^2 + d*x^3)^n, x}, x]", "False");
	}

	public void test0083() {
		check("Integrate::LinearQ[{a + c*x^2 + d*x^3, x, 2*c + 3*d*x}, x]", "False");
	}

	public void test0084() {
		check("Integrate::LinearQ[{a + c*x^2 + d*x^3, 2*c + 3*d*x, x}, x]", "False");
	}

	public void test0085() {
		check("Integrate::LinearQ[{(a + c*x^2 + d*x^3)^n, x, 2*c + 3*d*x}, x]", "False");
	}

	public void test0086() {
		check("Integrate::LinearQ[{(a + c*x^2 + d*x^3)^n, 2*c + 3*d*x, x}, x]", "False");
	}

	public void test0087() {
		check("Integrate::MergeableFactorQ[c, 1, x^2]", "False");
	}

	public void test0088() {
		check("Integrate::MergeableFactorQ[d, 1, x^3]", "False");
	}

	public void test0089() {
		check("Integrate::MergeableFactorQ[1 + n, -1, (a + c*x^2 + d*x^3)^(1 + n)]", "False");
	}

	public void test0090() {
		check("Integrate::MergeFactors[c, x^2]", "c*x^2");
	}

	public void test0091() {
		check("Integrate::MergeFactors[d, x^3]", "d*x^3");
	}

	public void test0092() {
		check("Integrate::MergeFactors[(1 + n)^(-1), (a + c*x^2 + d*x^3)^(1 + n)]",
				"(a + c*x^2 + d*x^3)^(1 + n)/(1 + n)");
	}

	public void test0093() {
		check("Integrate::NeQ[1, 0]", "True");
	}

	public void test0094() {
		check("Integrate::NeQ[3*d, 0]", "True");
	}

	public void test0095() {
		check("Integrate::NeQ[n, -1]", "True");
	}

	public void test0096() {
		check("Integrate::NeQ[3 + 3*n, 0]", "True");
	}

	public void test0097() {
		check("Integrate::NonfreeFactors[c*x^2, x]", "x^2");
	}

	public void test0098() {
		check("Integrate::NonfreeFactors[d*x^3, x]", "x^3");
	}

	public void test0099() {
		check("Integrate::NonfreeFactors[(3*(a + c*x^2 + d*x^3)^(1 + n))/(3 + 3*n), x]", "(a + c*x^2 + d*x^3)^(1 + n)");
	}

	public void test0100() {
		check("Integrate::NonnumericFactors[3]", "1");
	}

	public void test0101() {
		check("Integrate::NonnumericFactors[c]", "c");
	}

	public void test0102() {
		check("Integrate::NonnumericFactors[d]", "d");
	}

	public void test0103() {
		check("Integrate::NonnumericFactors[(3 + 3*n)^(-1)]", "(3 + 3*n)^(-1)");
	}

	public void test0104() {
		check("Integrate::NonnumericFactors[3/(3 + 3*n)]", "(3 + 3*n)^(-1)");
	}

	public void test0105() {
		check("Integrate::NormalizeSumFactors[-1]", "-1");
	}

	public void test0106() {
		check("Integrate::NormalizeSumFactors[1]", "1");
	}

	public void test0107() {
		check("Integrate::NormalizeSumFactors[2]", "2");
	}

	public void test0108() {
		check("Integrate::NormalizeSumFactors[3]", "3");
	}

	public void test0109() {
		check("Integrate::NormalizeSumFactors[a]", "a");
	}

	public void test0110() {
		check("Integrate::NormalizeSumFactors[c]", "c");
	}

	public void test0111() {
		check("Integrate::NormalizeSumFactors[d]", "d");
	}

	public void test0112() {
		check("Integrate::NormalizeSumFactors[n]", "n");
	}

	public void test0113() {
		check("Integrate::NormalizeSumFactors[(1 + n)^(-1)]", "(1 + n)^(-1)");
	}

	public void test0114() {
		check("Integrate::NormalizeSumFactors[1 + n]", "1 + n");
	}

	public void test0115() {
		check("Integrate::NormalizeSumFactors[x]", "x");
	}

	public void test0116() {
		check("Integrate::NormalizeSumFactors[x^2]", "x^2");
	}

	public void test0117() {
		check("Integrate::NormalizeSumFactors[c*x^2]", "c*x^2");
	}

	public void test0118() {
		check("Integrate::NormalizeSumFactors[x^3]", "x^3");
	}

	public void test0119() {
		check("Integrate::NormalizeSumFactors[d*x^3]", "d*x^3");
	}

	public void test0120() {
		check("Integrate::NormalizeSumFactors[a + c*x^2 + d*x^3]", "a + c*x^2 + d*x^3");
	}

	public void test0121() {
		check("Integrate::NormalizeSumFactors[(a + c*x^2 + d*x^3)^(1 + n)]", "(a + c*x^2 + d*x^3)^(1 + n)");
	}

	public void test0122() {
		check("Integrate::NormalizeSumFactors[(a + c*x^2 + d*x^3)^(1 + n)/(1 + n)]",
				"(a + c*x^2 + d*x^3)^(1 + n)/(1 + n)");
	}

	public void test0123() {
		check("Integrate::NumericFactor[1]", "1");
	}

	public void test0124() {
		check("Integrate::NumericFactor[3]", "3");
	}

	public void test0125() {
		check("Integrate::NumericFactor[c]", "1");
	}

	public void test0126() {
		check("Integrate::NumericFactor[d]", "1");
	}

	public void test0127() {
		check("Integrate::NumericFactor[n]", "1");
	}

	public void test0128() {
		check("Integrate::NumericFactor[3*n]", "3");
	}

	public void test0129() {
		check("Integrate::NumericFactor[(3 + 3*n)^(-1)]", "1");
	}

	public void test0130() {
		check("Integrate::NumericFactor[3/(3 + 3*n)]", "3");
	}

	public void test0131() {
		check("Integrate::PolyQ[x, x]", "True");
	}

	public void test0132() {
		check("Integrate::PolyQ[2*c + 3*d*x, x]", "True");
	}

	public void test0133() {
		check("Integrate::PolyQ[x*(2*c + 3*d*x), x]", "True");
	}

	public void test0134() {
		check("Integrate::PolyQ[a + c*x^2 + d*x^3, x]", "True");
	}

	public void test0135() {
		check("Integrate::PolyQ[(a + c*x^2 + d*x^3)^n, x]", "False");
	}

	public void test0136() {
		check("Integrate::PolyQ[x*(a + c*x^2 + d*x^3)^n, x]", "False");
	}

	public void test0137() {
		check("Integrate::PolyQ[(2*c + 3*d*x)*(a + c*x^2 + d*x^3)^n, x]", "False");
	}

	public void test0138() {
		check("Integrate::PolyQ[x, x, 1]", "True");
	}

	public void test0139() {
		check("Integrate::PolyQ[2*c + 3*d*x, x, 1]", "True");
	}

	public void test0140() {
		check("Integrate::PolyQ[a + c*x^2 + d*x^3, x, 1]", "False");
	}

	public void test0141() {
		check("Integrate::PolyQ[(a + c*x^2 + d*x^3)^n, x, 1]", "False");
	}

	public void test0142() {
		check("Integrate::PowerQ[a]", "False");
	}

	public void test0143() {
		check("Integrate::PowerQ[c]", "False");
	}

	public void test0144() {
		check("Integrate::PowerQ[d]", "False");
	}

	public void test0145() {
		check("Integrate::PowerQ[n]", "False");
	}

	public void test0146() {
		check("Integrate::PowerQ[3*n]", "False");
	}

	public void test0147() {
		check("Integrate::PowerQ[(1 + n)^(-1)]", "True");
	}

	public void test0148() {
		check("Integrate::PowerQ[1 + n]", "False");
	}

	public void test0149() {
		check("Integrate::PowerQ[(3 + 3*n)^(-1)]", "True");
	}

	public void test0150() {
		check("Integrate::PowerQ[3/(3 + 3*n)]", "False");
	}

	public void test0151() {
		check("Integrate::PowerQ[3 + 3*n]", "False");
	}

	public void test0152() {
		check("Integrate::PowerQ[x]", "False");
	}

	public void test0153() {
		check("Integrate::PowerQ[x^2]", "True");
	}

	public void test0154() {
		check("Integrate::PowerQ[c*x^2]", "False");
	}

	public void test0155() {
		check("Integrate::PowerQ[d*x^2]", "False");
	}

	public void test0156() {
		check("Integrate::PowerQ[x^2/(3 + 3*n)]", "False");
	}

	public void test0157() {
		check("Integrate::PowerQ[x^3]", "True");
	}

	public void test0158() {
		check("Integrate::PowerQ[(a + c*x^2 + d*x^3)^(1 + n)]", "True");
	}

	public void test0159() {
		check("Integrate::ProductQ[a]", "False");
	}

	public void test0160() {
		check("Integrate::ProductQ[c]", "False");
	}

	public void test0161() {
		check("Integrate::ProductQ[d]", "False");
	}

	public void test0162() {
		check("Integrate::ProductQ[n]", "False");
	}

	public void test0163() {
		check("Integrate::ProductQ[3*n]", "True");
	}

	public void test0164() {
		check("Integrate::ProductQ[(1 + n)^(-1)]", "False");
	}

	public void test0165() {
		check("Integrate::ProductQ[1 + n]", "False");
	}

	public void test0166() {
		check("Integrate::ProductQ[3/(3 + 3*n)]", "True");
	}

	public void test0167() {
		check("Integrate::ProductQ[3 + 3*n]", "False");
	}

	public void test0168() {
		check("Integrate::ProductQ[x]", "False");
	}

	public void test0169() {
		check("Integrate::ProductQ[x^2]", "False");
	}

	public void test0170() {
		check("Integrate::ProductQ[c*x^2]", "True");
	}

	public void test0171() {
		check("Integrate::ProductQ[d*x^2]", "True");
	}

	public void test0172() {
		check("Integrate::ProductQ[x^2/(3 + 3*n)]", "True");
	}

	public void test0173() {
		check("Integrate::ProductQ[x^3]", "False");
	}

	public void test0174() {
		check("Integrate::ProductQ[d*x^3]", "True");
	}

	public void test0175() {
		check("Integrate::ProductQ[a + c*x^2 + d*x^3]", "False");
	}

	public void test0176() {
		check("Integrate::ProductQ[(a + c*x^2 + d*x^3)^(1 + n)]", "False");
	}

	public void test0177() {
		check("Integrate::ProductQ[(a + c*x^2 + d*x^3)^(1 + n)/(1 + n)]", "True");
	}

	public void test0178() {
		check("Integrate::ProductQ[(3*(a + c*x^2 + d*x^3)^(1 + n))/(3 + 3*n)]", "True");
	}

	public void test0179() {
		check("Integrate::PseudoBinomialPairQ[x*(2*c + 3*d*x), a + c*x^2 + d*x^3, x]", "False");
	}

	public void test0180() {
		check("Integrate::PseudoBinomialPairQ[x*(2*c + 3*d*x), (a + c*x^2 + d*x^3)^n, x]", "False");
	}

	public void test0181() {
		check("Integrate::PseudoBinomialPairQ[x*(a + c*x^2 + d*x^3)^n, 2*c + 3*d*x, x]", "False");
	}

	public void test0182() {
		check("Integrate::PseudoBinomialParts[x*(2*c + 3*d*x), x]", "False");
	}

	public void test0183() {
		check("Integrate::PseudoBinomialParts[x*(a + c*x^2 + d*x^3)^n, x]", "False");
	}

	public void test0184() {
		check("Integrate::RationalQ[-1]", "True");
	}

	public void test0185() {
		check("Integrate::RationalQ[1/3]", "True");
	}

	public void test0186() {
		check("Integrate::RationalQ[1]", "True");
	}

	public void test0187() {
		check("Integrate::RationalQ[2]", "True");
	}

	public void test0188() {
		check("Integrate::RationalQ[3]", "True");
	}

	public void test0189() {
		check("Integrate::RationalQ[c]", "False");
	}

	public void test0190() {
		check("Integrate::RationalQ[d]", "False");
	}

	public void test0191() {
		check("Integrate::RationalQ[n]", "False");
	}

	public void test0192() {
		check("Integrate::RationalQ[(1 + n)^(-1)]", "False");
	}

	public void test0193() {
		check("Integrate::RationalQ[1 + n]", "False");
	}

	public void test0194() {
		check("Integrate::RationalQ[3 + 3*n]", "False");
	}

	public void test0195() {
		check("Integrate::RationalQ[x]", "False");
	}

	public void test0196() {
		check("Integrate::RationalQ[x^2]", "False");
	}

	public void test0197() {
		check("Integrate::RationalQ[c*x^2]", "False");
	}

	public void test0198() {
		check("Integrate::RationalQ[x^3]", "False");
	}

	public void test0199() {
		check("Integrate::RationalQ[d*x^3]", "False");
	}

	public void test0200() {
		check("Integrate::RationalQ[(a + c*x^2 + d*x^3)^(1 + n)]", "False");
	}

	public void test0201() {
		check("Integrate::RationalQ[(a + c*x^2 + d*x^3)^(1 + n)/(1 + n)]", "False");
	}

	public void test0202() {
		check("Integrate::SignOfFactor[c]", "{1, c}");
	}

	public void test0203() {
		check("Integrate::SignOfFactor[d]", "{1, d}");
	}

	public void test0204() {
		check("Integrate::SignOfFactor[(1 + n)^(-1)]", "{1, (1 + n)^(-1)}");
	}

	public void test0205() {
		check("Integrate::SignOfFactor[x^2]", "{1, x^2}");
	}

	public void test0206() {
		check("Integrate::SignOfFactor[c*x^2]", "{1, c*x^2}");
	}

	public void test0207() {
		check("Integrate::SignOfFactor[x^3]", "{1, x^3}");
	}

	public void test0208() {
		check("Integrate::SignOfFactor[d*x^3]", "{1, d*x^3}");
	}

	public void test0209() {
		check("Integrate::SignOfFactor[(a + c*x^2 + d*x^3)^(1 + n)]", "{1, (a + c*x^2 + d*x^3)^(1 + n)}");
	}

	public void test0210() {
		check("Integrate::SignOfFactor[(a + c*x^2 + d*x^3)^(1 + n)/(1 + n)]",
				"{1, (a + c*x^2 + d*x^3)^(1 + n)/(1 + n)}");
	}

	public void test0211() {
		check("Integrate::Simp[(3*(a + c*x^2 + d*x^3)^(1 + n))/(3 + 3*n), x]", "(a + c*x^2 + d*x^3)^(1 + n)/(1 + n)");
	}

	public void test0212() {
		check("Integrate::SimpFixFactor[c, x]", "c");
	}

	public void test0213() {
		check("Integrate::SimpFixFactor[d, x]", "d");
	}

	public void test0214() {
		check("Integrate::SimpFixFactor[(1 + n)^(-1), x]", "(1 + n)^(-1)");
	}

	public void test0215() {
		check("Integrate::SimpFixFactor[x^2, x]", "x^2");
	}

	public void test0216() {
		check("Integrate::SimpFixFactor[x^3, x]", "x^3");
	}

	public void test0217() {
		check("Integrate::SimpFixFactor[(a + c*x^2 + d*x^3)^(1 + n), x]", "(a + c*x^2 + d*x^3)^(1 + n)");
	}

	public void test0218() {
		check("Integrate::SimpHelp[2, x]", "2");
	}

	public void test0219() {
		check("Integrate::SimpHelp[3, x]", "3");
	}

	public void test0220() {
		check("Integrate::SimpHelp[1 + n, x]", "1 + n");
	}

	public void test0221() {
		check("Integrate::SimpHelp[x, x]", "x");
	}

	public void test0222() {
		check("Integrate::SimpHelp[x^2, x]", "x^2");
	}

	public void test0223() {
		check("Integrate::SimpHelp[c*x^2, x]", "c*x^2");
	}

	public void test0224() {
		check("Integrate::SimpHelp[x^3, x]", "x^3");
	}

	public void test0225() {
		check("Integrate::SimpHelp[d*x^3, x]", "d*x^3");
	}

	public void test0226() {
		check("Integrate::SimpHelp[a + c*x^2 + d*x^3, x]", "a + c*x^2 + d*x^3");
	}

	public void test0227() {
		check("Integrate::SimpHelp[(a + c*x^2 + d*x^3)^(1 + n), x]", "(a + c*x^2 + d*x^3)^(1 + n)");
	}

	public void test0228() {
		check("Integrate::SimpHelp[(3*(a + c*x^2 + d*x^3)^(1 + n))/(3 + 3*n), x]",
				"(a + c*x^2 + d*x^3)^(1 + n)/(1 + n)");
	}

	public void test0229() {
		check("Integrate::SmartSimplify[a]", "a");
	}

	public void test0230() {
		check("Integrate::SmartSimplify[1 + n]", "1 + n");
	}

	public void test0231() {
		check("Integrate::SmartSimplify[c*x^2]", "c*x^2");
	}

	public void test0232() {
		check("Integrate::SmartSimplify[d*x^2]", "d*x^2");
	}

	public void test0233() {
		check("Integrate::SmartSimplify[x^2/(3 + 3*n)]", "x^2/(3*(1 + n))");
	}

	public void test0234() {
		check("Integrate::StopFunctionQ[(1 + n)^(-1)]", "False");
	}

	public void test0235() {
		check("Integrate::StopFunctionQ[1 + n]", "False");
	}

	public void test0236() {
		check("Integrate::StopFunctionQ[x^2]", "False");
	}

	public void test0237() {
		check("Integrate::StopFunctionQ[c*x^2]", "False");
	}

	public void test0238() {
		check("Integrate::StopFunctionQ[x^3]", "False");
	}

	public void test0239() {
		check("Integrate::StopFunctionQ[d*x^3]", "False");
	}

	public void test0240() {
		check("Integrate::StopFunctionQ[a + c*x^2 + d*x^3]", "False");
	}

	public void test0241() {
		check("Integrate::StopFunctionQ[(a + c*x^2 + d*x^3)^(1 + n)]", "False");
	}

	public void test0242() {
		check("Integrate::StopFunctionQ[(a + c*x^2 + d*x^3)^(1 + n)/(1 + n)]", "False");
	}

	public void test0243() {
		check("Integrate::StopFunctionQ[(3*(a + c*x^2 + d*x^3)^(1 + n))/(3 + 3*n)]", "False");
	}

	public void test0244() {
		check("Integrate::SumQ[a]", "False");
	}

	public void test0245() {
		check("Integrate::SumQ[c]", "False");
	}

	public void test0246() {
		check("Integrate::SumQ[d]", "False");
	}

	public void test0247() {
		check("Integrate::SumQ[n]", "False");
	}

	public void test0248() {
		check("Integrate::SumQ[(1 + n)^(-1)]", "False");
	}

	public void test0249() {
		check("Integrate::SumQ[1 + n]", "True");
	}

	public void test0250() {
		check("Integrate::SumQ[3 + 3*n]", "True");
	}

	public void test0251() {
		check("Integrate::SumQ[x]", "False");
	}

	public void test0252() {
		check("Integrate::SumQ[x^2]", "False");
	}

	public void test0253() {
		check("Integrate::SumQ[c*x^2]", "False");
	}

	public void test0254() {
		check("Integrate::SumQ[x^3]", "False");
	}

	public void test0255() {
		check("Integrate::SumQ[d*x^3]", "False");
	}

	public void test0256() {
		check("Integrate::SumQ[c*x^2 + d*x^3]", "True");
	}

	public void test0257() {
		check("Integrate::SumQ[a + c*x^2 + d*x^3]", "True");
	}

	public void test0258() {
		check("Integrate::SumQ[(2*c + 3*d*x)*(a + c*x^2 + d*x^3)^n]", "False");
	}

	public void test0259() {
		check("Integrate::SumQ[x*(2*c + 3*d*x)*(a + c*x^2 + d*x^3)^n]", "False");
	}

	public void test0260() {
		check("Integrate::SumQ[(a + c*x^2 + d*x^3)^(1 + n)]", "False");
	}

	public void test0261() {
		check("Integrate::SumQ[(a + c*x^2 + d*x^3)^(1 + n)/(1 + n)]", "False");
	}

	public void test0262() {
		check("Integrate::TrigQ[x^2]", "False");
	}

	public void test0263() {
		check("Integrate::TrigQ[x^3]", "False");
	}

	public void test0264() {
		check("Integrate::TrigQ[(a + c*x^2 + d*x^3)^(1 + n)]", "False");
	}

	public void test0265() {
		check("PolynomialQ[(2*c+3*d*x)*(a+c*x^2+d*x^3)^n,x]", "False");
		check("Integrate::PolyQ[(2*c+3*d*x)*(a+c*x^2+d*x^3)^n,x]", "False");
	}

	public void test0266() {
		check("FreeQ[1,x]&&Integrate::PolyQ[(2*c+3*d*x)*(a+c*x^2+d*x^3)^n,x]&&Integrate::PolyQ[x,x]&&Integrate::NeQ[1,-1]",
				"False");
	}

	public void test0268a() {
		check("Integrate::Coeff[x,x,1]*x^(1+(-1)*1+(-1)*3)*((1+(-1)*1+(-1)*3+1)*(2*c+3*d*x)*(a+c*x^2+d*x^3)+(1+1)*x*(a+c*x^2+d*x^3)*D[2*c+3*d*x,x]+(1+n)*x*(2*c+3*d*x)*D[a+c*x^2+d*x^3,x])", //
				"((1+n)*x*(2*c+3*d*x)*(2*c*x+3*d*x^2)+6*d*x*(a+c*x^2+d*x^3)-2*(2*c+3*d*x)*(a+c*x^\n" + "2+d*x^3))/x^3");

		check("Integrate::EqQ[3*d^2*(3+3*n)*x,((1+n)*x*(2*c+3*d*x)*(2*c*x+3*d*x^2)+6*d*x*(a+c*x^2+d*x^3)-2*(2*c+3*d*x)*(a+c*x^2+d*x^3))/x^3]", //
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