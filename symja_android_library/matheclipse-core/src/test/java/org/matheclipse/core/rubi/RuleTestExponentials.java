package org.matheclipse.core.rubi;

public class RuleTestExponentials extends AbstractRubiTestCase {

	public RuleTestExponentials(String name) {
		super(name, false);
	}

	// {2181}
	public void test0001() {
		check("Integrate[F^(c*(a + b*x))*(d + e*x)^m, x]", 2181);
	}

	// {2194}
	public void test0002() {
		check("Integrate[F^(c*(a + b*x)), x]", 2194);
	}

	// {2178}
	public void test0003() {
		check("Integrate[F^(c*(a + b*x))/(d + e*x), x]", 2178);
	}

	// {2181}
	public void test0004() {
		check("Integrate[F^(c*(a + b*x))*(d + e*x)^m, x]", 2181);
	}

	// {2181}
	public void test0005() {
		check("Integrate[F^(c*(a + b*x))/(d + e*x)^m, x]", 2181);
	}

	// {2194}
	public void test0006() {
		check("Integrate[F^(2 + 5*x), x]", 2194);
	}

	// {2194}
	public void test0007() {
		check("Integrate[F^(a + b*x), x]", 2194);
	}

	// {2194}
	public void test0008() {
		check("Integrate[10^(2 + 5*x), x]", 2194);
	}

	// {2181}
	public void test0009() {
		check("Integrate[F^(c*(a + b*x))*(d + e*x)^(4/3), x]", 2181);
	}

	// {2202}
	public void test0010() {
		check("Integrate[F^(c*(a + b*x))*x^m*Log[d*x]^n*(e + e*n + e*(1 + m + b*c*x*Log[F])*Log[d*x]), x]", 2202);
	}

	// {2202}
	public void test0011() {
		check("Integrate[F^(c*(a + b*x))*x^2*Log[d*x]^n*(e + e*n + e*(3 + b*c*x*Log[F])*Log[d*x]), x]", 2202);
	}

	// {2202}
	public void test0012() {
		check("Integrate[F^(c*(a + b*x))*x*Log[d*x]^n*(e + e*n + e*(2 + b*c*x*Log[F])*Log[d*x]), x]", 2202);
	}

	// {2201}
	public void test0013() {
		check("Integrate[F^(c*(a + b*x))*Log[d*x]^n*(e + e*n + e*(1 + b*c*x*Log[F])*Log[d*x]), x]", 2201);
	}

	// {2202}
	public void test0014() {
		check("Integrate[(F^(c*(a + b*x))*Log[d*x]^n*(e + e*n + b*c*e*x*Log[F]*Log[d*x]))/x, x]", 2202);
	}

	// {2202}
	public void test0015() {
		check("Integrate[(F^(c*(a + b*x))*Log[d*x]^n*(e + e*n + e*(-1 + b*c*x*Log[F])*Log[d*x]))/x^2, x]", 2202);
	}

	// {2202}
	public void test0016() {
		check("Integrate[(F^(c*(a + b*x))*Log[d*x]^n*(e + e*n + e*(-2 + b*c*x*Log[F])*Log[d*x]))/x^3, x]", 2202);
	}

	// {2194}
	public void test0017() {
		check("Integrate[Sqrt[E^(a + b*x)], x]", 2194);
	}

	// {2187}
	public void test0018() {
		check("Integrate[1/((a + b*(F^(g*(e + f*x)))^n)*(c + d*x)), x]", 2187);
	}

	// {2187}
	public void test0019() {
		check("Integrate[1/((a + b*(F^(g*(e + f*x)))^n)*(c + d*x)^2), x]", 2187);
	}

	// {2187}
	public void test0020() {
		check("Integrate[1/((a + b*(F^(g*(e + f*x)))^n)^2*(c + d*x)), x]", 2187);
	}

	// {2187}
	public void test0021() {
		check("Integrate[1/((a + b*(F^(g*(e + f*x)))^n)^2*(c + d*x)^2), x]", 2187);
	}

	// {2187}
	public void test0022() {
		check("Integrate[1/((a + b*(F^(g*(e + f*x)))^n)^3*(c + d*x)), x]", 2187);
	}

	// {2187}
	public void test0023() {
		check("Integrate[1/((a + b*(F^(g*(e + f*x)))^n)^3*(c + d*x)^2), x]", 2187);
	}

	// {2187}
	public void test0024() {
		check("Integrate[(c + d*x)^m/(a + b*(F^(g*(e + f*x)))^n), x]", 2187);
	}

	// {2187}
	public void test0025() {
		check("Integrate[(c + d*x)^m/(a + b*(F^(g*(e + f*x)))^n)^2, x]", 2187);
	}

	// {2187}
	public void test0026() {
		check("Integrate[(a + b*(F^(g*(e + f*x)))^n)^p*(c + d*x)^m, x]", 2187);
	}

	// {2191}
	public void test0027() {
		check("Integrate[F^(c + d*x)/((a + b*F^(c + d*x))^2*x), x]", 2191);
	}

	// {2191}
	public void test0028() {
		check("Integrate[F^(c + d*x)/((a + b*F^(c + d*x))^2*x^2), x]", 2191);
	}

	// {2191}
	public void test0029() {
		check("Integrate[F^(c + d*x)/((a + b*F^(c + d*x))^3*x), x]", 2191);
	}

	// {2191}
	public void test0030() {
		check("Integrate[F^(c + d*x)/((a + b*F^(c + d*x))^3*x^2), x]", 2191);
	}

	// {2218}
	public void test0031() {
		check("Integrate[f^(a + b*x^2)*x^m, x]", 2218);
	}

	// {2218}
	public void test0032() {
		check("Integrate[f^(a + b*x^2)*x^11, x]", 2218);
	}

	// {2218}
	public void test0033() {
		check("Integrate[f^(a + b*x^2)*x^9, x]", 2218);
	}

	// {2209}
	public void test0034() {
		check("Integrate[f^(a + b*x^2)*x, x]", 2209);
	}

	// {2210}
	public void test0035() {
		check("Integrate[f^(a + b*x^2)/x, x]", 2210);
	}

	// {2218}
	public void test0036() {
		check("Integrate[f^(a + b*x^2)/x^9, x]", 2218);
	}

	// {2218}
	public void test0037() {
		check("Integrate[f^(a + b*x^2)/x^11, x]", 2218);
	}

	// {2218}
	public void test0038() {
		check("Integrate[f^(a + b*x^2)*x^12, x]", 2218);
	}

	// {2218}
	public void test0039() {
		check("Integrate[f^(a + b*x^2)*x^10, x]", 2218);
	}

	// {2204}
	public void test0040() {
		check("Integrate[f^(a + b*x^2), x]", 2204);
	}

	// {2218}
	public void test0041() {
		check("Integrate[f^(a + b*x^2)/x^10, x]", 2218);
	}

	// {2218}
	public void test0042() {
		check("Integrate[f^(a + b*x^2)/x^12, x]", 2218);
	}

	// {2218}
	public void test0043() {
		check("Integrate[f^(a + b*x^3)*x^m, x]", 2218);
	}

	// {2218}
	public void test0044() {
		check("Integrate[f^(a + b*x^3)*x^17, x]", 2218);
	}

	// {2218}
	public void test0045() {
		check("Integrate[f^(a + b*x^3)*x^14, x]", 2218);
	}

	// {2209}
	public void test0046() {
		check("Integrate[f^(a + b*x^3)*x^2, x]", 2209);
	}

	// {2210}
	public void test0047() {
		check("Integrate[f^(a + b*x^3)/x, x]", 2210);
	}

	// {2218}
	public void test0048() {
		check("Integrate[f^(a + b*x^3)/x^13, x]", 2218);
	}

	// {2218}
	public void test0049() {
		check("Integrate[f^(a + b*x^3)/x^16, x]", 2218);
	}

	// {2218}
	public void test0050() {
		check("Integrate[f^(a + b*x^3)*x^4, x]", 2218);
	}

	// {2218}
	public void test0051() {
		check("Integrate[f^(a + b*x^3)*x^3, x]", 2218);
	}

	// {2218}
	public void test0052() {
		check("Integrate[f^(a + b*x^3)*x, x]", 2218);
	}

	// {2208}
	public void test0053() {
		check("Integrate[f^(a + b*x^3), x]", 2208);
	}

	// {2218}
	public void test0054() {
		check("Integrate[f^(a + b*x^3)/x^2, x]", 2218);
	}

	// {2218}
	public void test0055() {
		check("Integrate[f^(a + b*x^3)/x^3, x]", 2218);
	}

	// {2209}
	public void test0056() {
		check("Integrate[E^(4*x^3)*x^2, x]", 2209);
	}

	// {2218}
	public void test0057() {
		check("Integrate[f^(a + b/x)*x^m, x]", 2218);
	}

	// {2218}
	public void test0058() {
		check("Integrate[f^(a + b/x)*x^4, x]", 2218);
	}

	// {2218}
	public void test0059() {
		check("Integrate[f^(a + b/x)*x^3, x]", 2218);
	}

	// {2210}
	public void test0060() {
		check("Integrate[f^(a + b/x)/x, x]", 2210);
	}

	// {2209}
	public void test0061() {
		check("Integrate[f^(a + b/x)/x^2, x]", 2209);
	}

	// {2218}
	public void test0062() {
		check("Integrate[f^(a + b/x)/x^6, x]", 2218);
	}

	// {2218}
	public void test0063() {
		check("Integrate[f^(a + b/x)/x^7, x]", 2218);
	}

	// {2218}
	public void test0064() {
		check("Integrate[f^(a + b/x^2)*x^m, x]", 2218);
	}

	// {2218}
	public void test0065() {
		check("Integrate[f^(a + b/x^2)*x^9, x]", 2218);
	}

	// {2218}
	public void test0066() {
		check("Integrate[f^(a + b/x^2)*x^7, x]", 2218);
	}

	// {2210}
	public void test0067() {
		check("Integrate[f^(a + b/x^2)/x, x]", 2210);
	}

	// {2209}
	public void test0068() {
		check("Integrate[f^(a + b/x^2)/x^3, x]", 2209);
	}

	// {2218}
	public void test0069() {
		check("Integrate[f^(a + b/x^2)/x^11, x]", 2218);
	}

	// {2218}
	public void test0070() {
		check("Integrate[f^(a + b/x^2)/x^13, x]", 2218);
	}

	// {2218}
	public void test0071() {
		check("Integrate[f^(a + b/x^2)*x^10, x]", 2218);
	}

	// {2218}
	public void test0072() {
		check("Integrate[f^(a + b/x^2)*x^8, x]", 2218);
	}

	// {2218}
	public void test0073() {
		check("Integrate[f^(a + b/x^2)/x^12, x]", 2218);
	}

	// {2218}
	public void test0074() {
		check("Integrate[f^(a + b/x^2)/x^14, x]", 2218);
	}

	// {2218}
	public void test0075() {
		check("Integrate[f^(a + b/x^3)*x^m, x]", 2218);
	}

	// {2218}
	public void test0076() {
		check("Integrate[f^(a + b/x^3)*x^14, x]", 2218);
	}

	// {2218}
	public void test0077() {
		check("Integrate[f^(a + b/x^3)*x^11, x]", 2218);
	}

	// {2210}
	public void test0078() {
		check("Integrate[f^(a + b/x^3)/x, x]", 2210);
	}

	// {2209}
	public void test0079() {
		check("Integrate[f^(a + b/x^3)/x^4, x]", 2209);
	}

	// {2218}
	public void test0080() {
		check("Integrate[f^(a + b/x^3)/x^16, x]", 2218);
	}

	// {2218}
	public void test0081() {
		check("Integrate[f^(a + b/x^3)/x^19, x]", 2218);
	}

	// {2218}
	public void test0082() {
		check("Integrate[f^(a + b/x^3)*x^4, x]", 2218);
	}

	// {2218}
	public void test0083() {
		check("Integrate[f^(a + b/x^3)*x^3, x]", 2218);
	}

	// {2218}
	public void test0084() {
		check("Integrate[f^(a + b/x^3)*x, x]", 2218);
	}

	// {2208}
	public void test0085() {
		check("Integrate[f^(a + b/x^3), x]", 2208);
	}

	// {2218}
	public void test0086() {
		check("Integrate[f^(a + b/x^3)/x^2, x]", 2218);
	}

	// {2218}
	public void test0087() {
		check("Integrate[f^(a + b/x^3)/x^3, x]", 2218);
	}

	// {2218}
	public void test0088() {
		check("Integrate[f^(a + b/x^3)/x^5, x]", 2218);
	}

	// {2218}
	public void test0089() {
		check("Integrate[f^(a + b*x^n)*x^m, x]", 2218);
	}

	// {2218}
	public void test0090() {
		check("Integrate[f^(a + b*x^n)*x^3, x]", 2218);
	}

	// {2218}
	public void test0091() {
		check("Integrate[f^(a + b*x^n)*x^2, x]", 2218);
	}

	// {2218}
	public void test0092() {
		check("Integrate[f^(a + b*x^n)*x, x]", 2218);
	}

	// {2208}
	public void test0093() {
		check("Integrate[f^(a + b*x^n), x]", 2208);
	}

	// {2210}
	public void test0094() {
		check("Integrate[f^(a + b*x^n)/x, x]", 2210);
	}

	// {2218}
	public void test0095() {
		check("Integrate[f^(a + b*x^n)/x^2, x]", 2218);
	}

	// {2218}
	public void test0096() {
		check("Integrate[f^(a + b*x^n)/x^3, x]", 2218);
	}

	// {2218}
	public void test0097() {
		check("Integrate[f^(a + b*x^n)/x^4, x]", 2218);
	}

	// {2209}
	public void test0098() {
		check("Integrate[f^(a + b*x^n)*x^(-1 + n), x]", 2209);
	}

	// {2210}
	public void test0099() {
		check("Integrate[f^(a + b*x^n)/x, x]", 2210);
	}

	// {2204}
	public void test0100() {
		check("Integrate[f^(c*(a + b*x)^2), x]", 2204);
	}

	// {2208}
	public void test0101() {
		check("Integrate[f^(c*(a + b*x)^3), x]", 2208);
	}

	// {2208}
	public void test0102() {
		check("Integrate[f^(c/(a + b*x)^3), x]", 2208);
	}

	// {2244}
	public void test0103() {
		check("Integrate[f^(c*(a + b*x)^2)*x^m, x]", 2244);
	}

	// {2181}
	public void test0104() {
		check("Integrate[f^(c*(a + b*x))*x^m, x]", 2181);
	}

	// {2208}
	public void test0105() {
		check("Integrate[f^(c*(a + b*x)^n), x]", 2208);
	}

	// {2218}
	public void test0106() {
		check("Integrate[F^(a + b*(c + d*x)^2)*(c + d*x)^m, x]", 2218);
	}

	// {2218}
	public void test0107() {
		check("Integrate[F^(a + b*(c + d*x)^2)*(c + d*x)^11, x]", 2218);
	}

	// {2218}
	public void test0108() {
		check("Integrate[F^(a + b*(c + d*x)^2)*(c + d*x)^9, x]", 2218);
	}

	// {2209}
	public void test0109() {
		check("Integrate[F^(a + b*(c + d*x)^2)*(c + d*x), x]", 2209);
	}

	// {2210}
	public void test0110() {
		check("Integrate[F^(a + b*(c + d*x)^2)/(c + d*x), x]", 2210);
	}

	// {2218}
	public void test0111() {
		check("Integrate[F^(a + b*(c + d*x)^2)/(c + d*x)^9, x]", 2218);
	}

	// {2218}
	public void test0112() {
		check("Integrate[F^(a + b*(c + d*x)^2)/(c + d*x)^11, x]", 2218);
	}

	// {2218}
	public void test0113() {
		check("Integrate[F^(a + b*(c + d*x)^2)*(c + d*x)^12, x]", 2218);
	}

	// {2218}
	public void test0114() {
		check("Integrate[F^(a + b*(c + d*x)^2)*(c + d*x)^10, x]", 2218);
	}

	// {2204}
	public void test0115() {
		check("Integrate[F^(a + b*(c + d*x)^2), x]", 2204);
	}

	// {2218}
	public void test0116() {
		check("Integrate[F^(a + b*(c + d*x)^2)/(c + d*x)^10, x]", 2218);
	}

	// {2218}
	public void test0117() {
		check("Integrate[F^(a + b*(c + d*x)^2)/(c + d*x)^12, x]", 2218);
	}

	// {2218}
	public void test0118() {
		check("Integrate[F^(a + b*(c + d*x)^3)*(c + d*x)^m, x]", 2218);
	}

	// {2218}
	public void test0119() {
		check("Integrate[F^(a + b*(c + d*x)^3)*(c + d*x)^17, x]", 2218);
	}

	// {2218}
	public void test0120() {
		check("Integrate[F^(a + b*(c + d*x)^3)*(c + d*x)^14, x]", 2218);
	}

	// {2209}
	public void test0121() {
		check("Integrate[F^(a + b*(c + d*x)^3)*(c + d*x)^2, x]", 2209);
	}

	// {2210}
	public void test0122() {
		check("Integrate[F^(a + b*(c + d*x)^3)/(c + d*x), x]", 2210);
	}

	// {2218}
	public void test0123() {
		check("Integrate[F^(a + b*(c + d*x)^3)/(c + d*x)^13, x]", 2218);
	}

	// {2218}
	public void test0124() {
		check("Integrate[F^(a + b*(c + d*x)^3)/(c + d*x)^16, x]", 2218);
	}

	// {2218}
	public void test0125() {
		check("Integrate[F^(a + b*(c + d*x)^3)*(c + d*x)^3, x]", 2218);
	}

	// {2218}
	public void test0126() {
		check("Integrate[F^(a + b*(c + d*x)^3)*(c + d*x), x]", 2218);
	}

	// {2208}
	public void test0127() {
		check("Integrate[F^(a + b*(c + d*x)^3), x]", 2208);
	}

	// {2218}
	public void test0128() {
		check("Integrate[F^(a + b*(c + d*x)^3)/(c + d*x)^2, x]", 2218);
	}

	// {2218}
	public void test0129() {
		check("Integrate[F^(a + b*(c + d*x)^3)/(c + d*x)^3, x]", 2218);
	}

	// {2218}
	public void test0130() {
		check("Integrate[F^(a + b*(c + d*x)^3)/(c + d*x)^5, x]", 2218);
	}

	// {2218}
	public void test0131() {
		check("Integrate[F^(a + b/(c + d*x))*(c + d*x)^m, x]", 2218);
	}

	// {2218}
	public void test0132() {
		check("Integrate[F^(a + b/(c + d*x))*(c + d*x)^4, x]", 2218);
	}

	// {2218}
	public void test0133() {
		check("Integrate[F^(a + b/(c + d*x))*(c + d*x)^3, x]", 2218);
	}

	// {2210}
	public void test0134() {
		check("Integrate[F^(a + b/(c + d*x))/(c + d*x), x]", 2210);
	}

	// {2209}
	public void test0135() {
		check("Integrate[F^(a + b/(c + d*x))/(c + d*x)^2, x]", 2209);
	}

	// {2218}
	public void test0136() {
		check("Integrate[F^(a + b/(c + d*x))/(c + d*x)^6, x]", 2218);
	}

	// {2218}
	public void test0137() {
		check("Integrate[F^(a + b/(c + d*x))/(c + d*x)^7, x]", 2218);
	}

	// {2218}
	public void test0138() {
		check("Integrate[F^(a + b/(c + d*x)^2)*(c + d*x)^m, x]", 2218);
	}

	// {2218}
	public void test0139() {
		check("Integrate[F^(a + b/(c + d*x)^2)*(c + d*x)^9, x]", 2218);
	}

	// {2218}
	public void test0140() {
		check("Integrate[F^(a + b/(c + d*x)^2)*(c + d*x)^7, x]", 2218);
	}

	// {2210}
	public void test0141() {
		check("Integrate[F^(a + b/(c + d*x)^2)/(c + d*x), x]", 2210);
	}

	// {2209}
	public void test0142() {
		check("Integrate[F^(a + b/(c + d*x)^2)/(c + d*x)^3, x]", 2209);
	}

	// {2218}
	public void test0143() {
		check("Integrate[F^(a + b/(c + d*x)^2)/(c + d*x)^11, x]", 2218);
	}

	// {2218}
	public void test0144() {
		check("Integrate[F^(a + b/(c + d*x)^2)/(c + d*x)^13, x]", 2218);
	}

	// {2218}
	public void test0145() {
		check("Integrate[F^(a + b/(c + d*x)^2)*(c + d*x)^10, x]", 2218);
	}

	// {2218}
	public void test0146() {
		check("Integrate[F^(a + b/(c + d*x)^2)*(c + d*x)^8, x]", 2218);
	}

	// {2218}
	public void test0147() {
		check("Integrate[F^(a + b/(c + d*x)^2)/(c + d*x)^12, x]", 2218);
	}

	// {2218}
	public void test0148() {
		check("Integrate[F^(a + b/(c + d*x)^2)/(c + d*x)^14, x]", 2218);
	}

	// {2218}
	public void test0149() {
		check("Integrate[F^(a + b/(c + d*x)^3)*(c + d*x)^m, x]", 2218);
	}

	// {2218}
	public void test0150() {
		check("Integrate[F^(a + b/(c + d*x)^3)*(c + d*x)^14, x]", 2218);
	}

	// {2218}
	public void test0151() {
		check("Integrate[F^(a + b/(c + d*x)^3)*(c + d*x)^11, x]", 2218);
	}

	// {2210}
	public void test0152() {
		check("Integrate[F^(a + b/(c + d*x)^3)/(c + d*x), x]", 2210);
	}

	// {2209}
	public void test0153() {
		check("Integrate[F^(a + b/(c + d*x)^3)/(c + d*x)^4, x]", 2209);
	}

	// {2218}
	public void test0154() {
		check("Integrate[F^(a + b/(c + d*x)^3)/(c + d*x)^16, x]", 2218);
	}

	// {2218}
	public void test0155() {
		check("Integrate[F^(a + b/(c + d*x)^3)/(c + d*x)^19, x]", 2218);
	}

	// {2218}
	public void test0156() {
		check("Integrate[F^(a + b/(c + d*x)^3)*(c + d*x)^3, x]", 2218);
	}

	// {2218}
	public void test0157() {
		check("Integrate[F^(a + b/(c + d*x)^3)*(c + d*x), x]", 2218);
	}

	// {2208}
	public void test0158() {
		check("Integrate[F^(a + b/(c + d*x)^3), x]", 2208);
	}

	// {2218}
	public void test0159() {
		check("Integrate[F^(a + b/(c + d*x)^3)/(c + d*x)^2, x]", 2218);
	}

	// {2218}
	public void test0160() {
		check("Integrate[F^(a + b/(c + d*x)^3)/(c + d*x)^3, x]", 2218);
	}

	// {2218}
	public void test0161() {
		check("Integrate[F^(a + b/(c + d*x)^3)/(c + d*x)^5, x]", 2218);
	}

	// {2218}
	public void test0162() {
		check("Integrate[F^(a + b*(c + d*x)^n)*(c + d*x)^m, x]", 2218);
	}

	// {2218}
	public void test0163() {
		check("Integrate[F^(a + b*(c + d*x)^n)*(c + d*x)^3, x]", 2218);
	}

	// {2218}
	public void test0164() {
		check("Integrate[F^(a + b*(c + d*x)^n)*(c + d*x)^2, x]", 2218);
	}

	// {2218}
	public void test0165() {
		check("Integrate[F^(a + b*(c + d*x)^n)*(c + d*x), x]", 2218);
	}

	// {2208}
	public void test0166() {
		check("Integrate[F^(a + b*(c + d*x)^n), x]", 2208);
	}

	// {2210}
	public void test0167() {
		check("Integrate[F^(a + b*(c + d*x)^n)/(c + d*x), x]", 2210);
	}

	// {2218}
	public void test0168() {
		check("Integrate[F^(a + b*(c + d*x)^n)/(c + d*x)^2, x]", 2218);
	}

	// {2218}
	public void test0169() {
		check("Integrate[F^(a + b*(c + d*x)^n)/(c + d*x)^3, x]", 2218);
	}

	// {2218}
	public void test0170() {
		check("Integrate[F^(a + b*(c + d*x)^n)/(c + d*x)^4, x]", 2218);
	}

	// {2218}
	public void test0171() {
		check("Integrate[F^(a + b*(c + d*x)^n)*(c + d*x)^(-1 + 6*n), x]", 2218);
	}

	// {2218}
	public void test0172() {
		check("Integrate[F^(a + b*(c + d*x)^n)*(c + d*x)^(-1 + 5*n), x]", 2218);
	}

	// {2209}
	public void test0173() {
		check("Integrate[F^(a + b*(c + d*x)^n)*(c + d*x)^(-1 + n), x]", 2209);
	}

	// {2210}
	public void test0174() {
		check("Integrate[F^(a + b*(c + d*x)^n)/(c + d*x), x]", 2210);
	}

	// {2218}
	public void test0175() {
		check("Integrate[F^(a + b*(c + d*x)^n)*(c + d*x)^(-1 - 4*n), x]", 2218);
	}

	// {2218}
	public void test0176() {
		check("Integrate[F^(a + b*(c + d*x)^n)*(c + d*x)^(-1 - 5*n), x]", 2218);
	}

	// {2204}
	public void test0177() {
		check("Integrate[F^(a + b*(c + d*x)^2), x]", 2204);
	}

	// {2208}
	public void test0178() {
		check("Integrate[E^(e*(c + d*x)^3), x]", 2208);
	}

	// {2208}
	public void test0179() {
		check("Integrate[E^(e/(c + d*x)^3), x]", 2208);
	}

	// {2244}
	public void test0180() {
		check("Integrate[E^((a + b*x)*(c + d*x))/x, x]", 2244);
	}

	// {2236}
	public void test0181() {
		check("Integrate[f^(a + b*x + c*x^2)*(b + 2*c*x), x]", 2236);
	}

	// {2238}
	public void test0182() {
		check("Integrate[f^(a + b*x + c*x^2)/(b + 2*c*x), x]", 2238);
	}

	// {2236}
	public void test0183() {
		check("Integrate[f^(b*x + c*x^2)*(b + 2*c*x), x]", 2236);
	}

	// {2238}
	public void test0184() {
		check("Integrate[f^(b*x + c*x^2)/(b + 2*c*x), x]", 2238);
	}

	// {208}
	public void test0185() {
		check("Integrate[(d^2 - e^2*x^2)^(-1), x]", 208);
	}

	// {6706}
	public void test0186() {
		check("Integrate[F^(a + b*x + c*x^3)*(b + 3*c*x^2), x]", 6706);
	}

	// {6706}
	public void test0187() {
		check("Integrate[(F^(a + b*x + c*x^2)^(-1)*(b + 2*c*x))/(a + b*x + c*x^2)^2, x]", 6706);
	}

	// {2236}
	public void test0188() {
		check("Integrate[E^(a + b*x + c*x^2)*(b + 2*c*x), x]", 2236);
	}

	// {2209}
	public void test0189() {
		check("Integrate[E^(2 - x^2)*x, x]", 2209);
	}

	// {2209}
	public void test0190() {
		check("Integrate[E^Sqrt[4 + x]/Sqrt[4 + x], x]", 2209);
	}

	// {2209}
	public void test0191() {
		check("Integrate[E^(1 + x^2)*x, x]", 2209);
	}

	// {2209}
	public void test0192() {
		check("Integrate[E^(1 + x^3)*x^2, x]", 2209);
	}

	// {2209}
	public void test0193() {
		check("Integrate[E^Sqrt[x]/Sqrt[x], x]", 2209);
	}

	// {2209}
	public void test0194() {
		check("Integrate[E^x^(1/3)/x^(2/3), x]", 2209);
	}

	// {4433}
	public void test0195() {
		check("Integrate[Cos[3*x]/E^x, x]", 4433);
	}

	// {4433}
	public void test0196() {
		check("Integrate[E^(3*x)*Cos[5*x], x]", 4433);
	}

	// {4433}
	public void test0197() {
		check("Integrate[E^x*Cos[4 + 3*x], x]", 4433);
	}

	// {4432}
	public void test0198() {
		check("Integrate[E^(6*x)*Sin[3*x], x]", 4432);
	}

	// {2289}
	public void test0199() {
		check("Integrate[(E^x^2*x^3)/(1 + x^2)^2, x]", 2289);
	}

	// {6686}
	public void test0200() {
		check("Integrate[(1 + E^x)/Sqrt[E^x + x], x]", 6686);
	}

	// {6684}
	public void test0201() {
		check("Integrate[(1 + E^x)/(E^x + x), x]", 6684);
	}

	// {2209}
	public void test0202() {
		check("Integrate[3^(1 + x^2)*x, x]", 2209);
	}

	// {2209}
	public void test0203() {
		check("Integrate[2^Sqrt[x]/Sqrt[x], x]", 2209);
	}

	// {2209}
	public void test0204() {
		check("Integrate[2^x^(-1)/x^2, x]", 2209);
	}

	// {2209}
	public void test0205() {
		check("Integrate[10^Sqrt[x]/Sqrt[x], x]", 2209);
	}

	// {2262}
	public void test0206() {
		check("Integrate[(E^x*x^2)/Sqrt[5*E^x + x^3], x]", 2262);
	}

	// {6686}
	public void test0207() {
		check("Integrate[-((1 + E^x)/(E^x + x)^(1/3)), x]", 6686);
	}

	// {2273}
	public void test0208() {
		check("Integrate[x/(E^x + x)^(1/3), x]", 2273);
	}

	// {2218}
	public void test0209() {
		check("Integrate[E^x^n*x^m, x]", 2218);
	}

	// {2218}
	public void test0210() {
		check("Integrate[f^x^n*x^m, x]", 2218);
	}

	// {2218}
	public void test0211() {
		check("Integrate[E^(a + b*x)^n*(a + b*x)^m, x]", 2218);
	}

	// {2218}
	public void test0212() {
		check("Integrate[f^(a + b*x)^n*(a + b*x)^m, x]", 2218);
	}
}
