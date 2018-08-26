package org.matheclipse.core.rubi;

public class Exponentials extends AbstractRubiTestCase {

	public Exponentials(String name) {
		super(name, false);
	}

	// {2181}
	public void test0001() {
		check(//
				"Integrate[F^(c*(a + b*x))*(d + e*x)^m, x]", //
				"(F^(c*(a - (b*d)/e))*(d + e*x)^m*Gamma[1 + m, -((b*c*(d + e*x)*Log[F])/e)])/(b*c*Log[F]*(-((b*c*(d + e*x)*Log[F])/e))^m)", //
				2181);
	}

	// {2194}
	public void test0002() {
		check(//
				"Integrate[F^(c*(a + b*x)), x]", //
				"F^(c*(a + b*x))/(b*c*Log[F])", //
				2194);
	}

	// {2178}
	public void test0003() {
		check(//
				"Integrate[F^(c*(a + b*x))/(d + e*x), x]", //
				"(F^(c*(a - (b*d)/e))*ExpIntegralEi[(b*c*(d + e*x)*Log[F])/e])/e", //
				2178);
	}

	// {2181}
	public void test0004() {
		check(//
				"Integrate[F^(c*(a + b*x))*(d + e*x)^m, x]", //
				"(F^(c*(a - (b*d)/e))*(d + e*x)^m*Gamma[1 + m, -((b*c*(d + e*x)*Log[F])/e)])/(b*c*Log[F]*(-((b*c*(d + e*x)*Log[F])/e))^m)", //
				2181);
	}

	// {2181}
	public void test0005() {
		check(//
				"Integrate[F^(c*(a + b*x))/(d + e*x)^m, x]", //
				"(F^(c*(a - (b*d)/e))*Gamma[1 - m, -((b*c*(d + e*x)*Log[F])/e)]*(-((b*c*(d + e*x)*Log[F])/e))^m)/(b*c*(d + e*x)^m*Log[F])", //
				2181);
	}

	// {2194}
	public void test0006() {
		check(//
				"Integrate[F^(2 + 5*x), x]", //
				"F^(2 + 5*x)/(5*Log[F])", //
				2194);
	}

	// {2194}
	public void test0007() {
		check(//
				"Integrate[F^(a + b*x), x]", //
				"F^(a + b*x)/(b*Log[F])", //
				2194);
	}

	// {2194}
	public void test0008() {
		check(//
				"Integrate[10^(2 + 5*x), x]", //
				"(2^(2 + 5*x)*5^(1 + 5*x))/Log[10]", //
				"10^(2+5*x)/(5*Log[10])", //
				2194);
	}

	// {2181}
	public void test0009() {
		check(//
				"Integrate[F^(c*(a + b*x))*(d + e*x)^(4/3), x]", //
				"-((e*F^(c*(a - (b*d)/e))*(d + e*x)^(1/3)*Gamma[7/3, -((b*c*(d + e*x)*Log[F])/e)])/(b^2*c^2*Log[F]^2*(-((b*c*(d + e*x)*Log[F])/e))^(1/3)))", //
				2181);
	}

	// {2202}
	public void test0010() {
		check(//
				"Integrate[F^(c*(a + b*x))*x^m*Log[d*x]^n*(e + e*n + e*(1 + m + b*c*x*Log[F])*Log[d*x]), x]", //
				"e*F^(c*(a + b*x))*x^(1 + m)*Log[d*x]^(1 + n)", //
				2202);
	}

	// {2202}
	public void test0011() {
		check(//
				"Integrate[F^(c*(a + b*x))*x^2*Log[d*x]^n*(e + e*n + e*(3 + b*c*x*Log[F])*Log[d*x]), x]", //
				"e*F^(c*(a + b*x))*x^3*Log[d*x]^(1 + n)", //
				2202);
	}

	// {2202}
	public void test0012() {
		check(//
				"Integrate[F^(c*(a + b*x))*x*Log[d*x]^n*(e + e*n + e*(2 + b*c*x*Log[F])*Log[d*x]), x]", //
				"e*F^(c*(a + b*x))*x^2*Log[d*x]^(1 + n)", //
				2202);
	}

	// {2201}
	public void test0013() {
		check(//
				"Integrate[F^(c*(a + b*x))*Log[d*x]^n*(e + e*n + e*(1 + b*c*x*Log[F])*Log[d*x]), x]", //
				"e*F^(c*(a + b*x))*x*Log[d*x]^(1 + n)", //
				2201);
	}

	// {2202}
	public void test0014() {
		check(//
				"Integrate[(F^(c*(a + b*x))*Log[d*x]^n*(e + e*n + b*c*e*x*Log[F]*Log[d*x]))/x, x]", //
				"e*F^(c*(a + b*x))*Log[d*x]^(1 + n)", //
				2202);
	}

	// {2202}
	public void test0015() {
		check(//
				"Integrate[(F^(c*(a + b*x))*Log[d*x]^n*(e + e*n + e*(-1 + b*c*x*Log[F])*Log[d*x]))/x^2, x]", //
				"(e*F^(c*(a + b*x))*Log[d*x]^(1 + n))/x", //
				2202);
	}

	// {2202}
	public void test0016() {
		check(//
				"Integrate[(F^(c*(a + b*x))*Log[d*x]^n*(e + e*n + e*(-2 + b*c*x*Log[F])*Log[d*x]))/x^3, x]", //
				"(e*F^(c*(a + b*x))*Log[d*x]^(1 + n))/x^2", //
				2202);
	}

	// {2194}
	public void test0017() {
		check(//
				"Integrate[Sqrt[E^(a + b*x)], x]", //
				"(2*Sqrt[E^(a + b*x)])/b", //
				2194);
	}

	// {2218}
	public void test0031() {
		check(//
				"Integrate[f^(a + b*x^2)*x^m, x]", //
				"-(f^a*x^(1 + m)*Gamma[(1 + m)/2, -(b*x^2*Log[f])]*(-(b*x^2*Log[f]))^((-1 - m)/2))/2", //
				2218);
	}

	// {2218}
	public void test0032() {
		check(//
				"Integrate[f^(a + b*x^2)*x^11, x]", //
				"-(f^a*Gamma[6, -(b*x^2*Log[f])])/(2*b^6*Log[f]^6)", //
				2218);
	}

	// {2218}
	public void test0033() {
		check(//
				"Integrate[f^(a + b*x^2)*x^9, x]", //
				"(f^a*Gamma[5, -(b*x^2*Log[f])])/(2*b^5*Log[f]^5)", //
				2218);
	}

	// {2209}
	public void test0034() {
		check(//
				"Integrate[f^(a + b*x^2)*x, x]", //
				"f^(a + b*x^2)/(2*b*Log[f])", //
				2209);
	}

	// {2210}
	public void test0035() {
		check(//
				"Integrate[f^(a + b*x^2)/x, x]", //
				"(f^a*ExpIntegralEi[b*x^2*Log[f]])/2", //
				2210);
	}

	// {2218}
	public void test0036() {
		check(//
				"Integrate[f^(a + b*x^2)/x^9, x]", //
				"-(b^4*f^a*Gamma[-4, -(b*x^2*Log[f])]*Log[f]^4)/2", //
				2218);
	}

	// {2218}
	public void test0037() {
		check(//
				"Integrate[f^(a + b*x^2)/x^11, x]", //
				"(b^5*f^a*Gamma[-5, -(b*x^2*Log[f])]*Log[f]^5)/2", //
				2218);
	}

	// {2218}
	public void test0038() {
		check(//
				"Integrate[f^(a + b*x^2)*x^12, x]", //
				"-(f^a*x^13*Gamma[13/2, -(b*x^2*Log[f])])/(2*(-(b*x^2*Log[f]))^(13/2))", //
				2218);
	}

	// {2218}
	public void test0039() {
		check(//
				"Integrate[f^(a + b*x^2)*x^10, x]", //
				"-(f^a*x^11*Gamma[11/2, -(b*x^2*Log[f])])/(2*(-(b*x^2*Log[f]))^(11/2))", //
				2218);
	}

	// {2204}
	public void test0040() {
		check(//
				"Integrate[f^(a + b*x^2), x]", //
				"(f^a*Sqrt[Pi]*Erfi[Sqrt[b]*x*Sqrt[Log[f]]])/(2*Sqrt[b]*Sqrt[Log[f]])", //
				2204);
	}

	// {2218}
	public void test0041() {
		check(//
				"Integrate[f^(a + b*x^2)/x^10, x]", //
				"-(f^a*Gamma[-9/2, -(b*x^2*Log[f])]*(-(b*x^2*Log[f]))^(9/2))/(2*x^9)", //
				2218);
	}

	// {2218}
	public void test0042() {
		check(//
				"Integrate[f^(a + b*x^2)/x^12, x]", //
				"-(f^a*Gamma[-11/2, -(b*x^2*Log[f])]*(-(b*x^2*Log[f]))^(11/2))/(2*x^11)", //
				2218);
	}

	// {2218}
	public void test0043() {
		check(//
				"Integrate[f^(a + b*x^3)*x^m, x]", //
				"-(f^a*x^(1 + m)*Gamma[(1 + m)/3, -(b*x^3*Log[f])]*(-(b*x^3*Log[f]))^((-1 - m)/3))/3", //
				2218);
	}

	// {2218}
	public void test0044() {
		check(//
				"Integrate[f^(a + b*x^3)*x^17, x]", //
				"-(f^a*Gamma[6, -(b*x^3*Log[f])])/(3*b^6*Log[f]^6)", //
				2218);
	}

	// {2218}
	public void test0045() {
		check(//
				"Integrate[f^(a + b*x^3)*x^14, x]", //
				"(f^a*Gamma[5, -(b*x^3*Log[f])])/(3*b^5*Log[f]^5)", //
				2218);
	}

	// {2209}
	public void test0046() {
		check(//
				"Integrate[f^(a + b*x^3)*x^2, x]", //
				"f^(a + b*x^3)/(3*b*Log[f])", //
				2209);
	}

	// {2210}
	public void test0047() {
		check(//
				"Integrate[f^(a + b*x^3)/x, x]", //
				"(f^a*ExpIntegralEi[b*x^3*Log[f]])/3", //
				2210);
	}

	// {2218}
	public void test0048() {
		check(//
				"Integrate[f^(a + b*x^3)/x^13, x]", //
				"-(b^4*f^a*Gamma[-4, -(b*x^3*Log[f])]*Log[f]^4)/3", //
				2218);
	}

	// {2218}
	public void test0049() {
		check(//
				"Integrate[f^(a + b*x^3)/x^16, x]", //
				"(b^5*f^a*Gamma[-5, -(b*x^3*Log[f])]*Log[f]^5)/3", //
				2218);
	}

	// {2218}
	public void test0050() {
		check(//
				"Integrate[f^(a + b*x^3)*x^4, x]", //
				"-(f^a*x^5*Gamma[5/3, -(b*x^3*Log[f])])/(3*(-(b*x^3*Log[f]))^(5/3))", //
				2218);
	}

	// {2218}
	public void test0051() {
		check(//
				"Integrate[f^(a + b*x^3)*x^3, x]", //
				"-(f^a*x^4*Gamma[4/3, -(b*x^3*Log[f])])/(3*(-(b*x^3*Log[f]))^(4/3))", //
				2218);
	}

	// {2218}
	public void test0052() {
		check(//
				"Integrate[f^(a + b*x^3)*x, x]", //
				"-(f^a*x^2*Gamma[2/3, -(b*x^3*Log[f])])/(3*(-(b*x^3*Log[f]))^(2/3))", //
				2218);
	}

	// {2208}
	public void test0053() {
		check(//
				"Integrate[f^(a + b*x^3), x]", //
				"-(f^a*x*Gamma[1/3, -(b*x^3*Log[f])])/(3*(-(b*x^3*Log[f]))^(1/3))", //
				2208);
	}

	// {2218}
	public void test0054() {
		check(//
				"Integrate[f^(a + b*x^3)/x^2, x]", //
				"-(f^a*Gamma[-1/3, -(b*x^3*Log[f])]*(-(b*x^3*Log[f]))^(1/3))/(3*x)", //
				2218);
	}

	// {2218}
	public void test0055() {
		check(//
				"Integrate[f^(a + b*x^3)/x^3, x]", //
				"-(f^a*Gamma[-2/3, -(b*x^3*Log[f])]*(-(b*x^3*Log[f]))^(2/3))/(3*x^2)", //
				2218);
	}

	// {2209}
	public void test0056() {
		check(//
				"Integrate[E^(4*x^3)*x^2, x]", //
				"E^(4*x^3)/12", //
				2209);
	}

	// {2218}
	public void test0057() {
		check(//
				"Integrate[f^(a + b/x)*x^m, x]", //
				"f^a*x^(1 + m)*Gamma[-1 - m, -((b*Log[f])/x)]*(-((b*Log[f])/x))^(1 + m)", //
				2218);
	}

	// {2218}
	public void test0058() {
		check(//
				"Integrate[f^(a + b/x)*x^4, x]", //
				"-(b^5*f^a*Gamma[-5, -((b*Log[f])/x)]*Log[f]^5)", //
				2218);
	}

	// {2218}
	public void test0059() {
		check(//
				"Integrate[f^(a + b/x)*x^3, x]", //
				"b^4*f^a*Gamma[-4, -((b*Log[f])/x)]*Log[f]^4", //
				2218);
	}

	// {2210}
	public void test0060() {
		check(//
				"Integrate[f^(a + b/x)/x, x]", //
				"-(f^a*ExpIntegralEi[(b*Log[f])/x])", //
				2210);
	}

	// {2209}
	public void test0061() {
		check(//
				"Integrate[f^(a + b/x)/x^2, x]", //
				"-(f^(a + b/x)/(b*Log[f]))", //
				2209);
	}

	// {2218}
	public void test0062() {
		check(//
				"Integrate[f^(a + b/x)/x^6, x]", //
				"-((f^a*Gamma[5, -((b*Log[f])/x)])/(b^5*Log[f]^5))", //
				2218);
	}

	// {2218}
	public void test0063() {
		check(//
				"Integrate[f^(a + b/x)/x^7, x]", //
				"(f^a*Gamma[6, -((b*Log[f])/x)])/(b^6*Log[f]^6)", //
				2218);
	}

	// {2218}
	public void test0064() {
		check(//
				"Integrate[f^(a + b/x^2)*x^m, x]", //
				"(f^a*x^(1 + m)*Gamma[(-1 - m)/2, -((b*Log[f])/x^2)]*(-((b*Log[f])/x^2))^((1 + m)/2))/2", //
				2218);
	}

	// {2218}
	public void test0065() {
		check(//
				"Integrate[f^(a + b/x^2)*x^9, x]", //
				"-(b^5*f^a*Gamma[-5, -((b*Log[f])/x^2)]*Log[f]^5)/2", //
				2218);
	}

	// {2218}
	public void test0066() {
		check(//
				"Integrate[f^(a + b/x^2)*x^7, x]", //
				"(b^4*f^a*Gamma[-4, -((b*Log[f])/x^2)]*Log[f]^4)/2", //
				2218);
	}

	// {2210}
	public void test0067() {
		check(//
				"Integrate[f^(a + b/x^2)/x, x]", //
				"-(f^a*ExpIntegralEi[(b*Log[f])/x^2])/2", //
				2210);
	}

	// {2209}
	public void test0068() {
		check(//
				"Integrate[f^(a + b/x^2)/x^3, x]", //
				"-f^(a + b/x^2)/(2*b*Log[f])", //
				2209);
	}

	// {2218}
	public void test0069() {
		check(//
				"Integrate[f^(a + b/x^2)/x^11, x]", //
				"-(f^a*Gamma[5, -((b*Log[f])/x^2)])/(2*b^5*Log[f]^5)", //
				2218);
	}

	// {2218}
	public void test0070() {
		check(//
				"Integrate[f^(a + b/x^2)/x^13, x]", //
				"(f^a*Gamma[6, -((b*Log[f])/x^2)])/(2*b^6*Log[f]^6)", //
				2218);
	}

	// {2218}
	public void test0071() {
		check(//
				"Integrate[f^(a + b/x^2)*x^10, x]", //
				"(f^a*x^11*Gamma[-11/2, -((b*Log[f])/x^2)]*(-((b*Log[f])/x^2))^(11/2))/2", //
				2218);
	}

	// {2218}
	public void test0072() {
		check(//
				"Integrate[f^(a + b/x^2)*x^8, x]", //
				"(f^a*x^9*Gamma[-9/2, -((b*Log[f])/x^2)]*(-((b*Log[f])/x^2))^(9/2))/2", //
				2218);
	}

	// {2218}
	public void test0073() {
		check(//
				"Integrate[f^(a + b/x^2)/x^12, x]", //
				"(f^a*Gamma[11/2, -((b*Log[f])/x^2)])/(2*x^11*(-((b*Log[f])/x^2))^(11/2))", //
				2218);
	}

	// {2218}
	public void test0074() {
		check(//
				"Integrate[f^(a + b/x^2)/x^14, x]", //
				"(f^a*Gamma[13/2, -((b*Log[f])/x^2)])/(2*x^13*(-((b*Log[f])/x^2))^(13/2))", //
				2218);
	}

	// {2218}
	public void test0075() {
		check(//
				"Integrate[f^(a + b/x^3)*x^m, x]", //
				"(f^a*x^(1 + m)*Gamma[(-1 - m)/3, -((b*Log[f])/x^3)]*(-((b*Log[f])/x^3))^((1 + m)/3))/3", //
				2218);
	}

	// {2218}
	public void test0076() {
		check(//
				"Integrate[f^(a + b/x^3)*x^14, x]", //
				"-(b^5*f^a*Gamma[-5, -((b*Log[f])/x^3)]*Log[f]^5)/3", //
				2218);
	}

	// {2218}
	public void test0077() {
		check(//
				"Integrate[f^(a + b/x^3)*x^11, x]", //
				"(b^4*f^a*Gamma[-4, -((b*Log[f])/x^3)]*Log[f]^4)/3", //
				2218);
	}

	// {2210}
	public void test0078() {
		check(//
				"Integrate[f^(a + b/x^3)/x, x]", //
				"-(f^a*ExpIntegralEi[(b*Log[f])/x^3])/3", //
				2210);
	}

	// {2209}
	public void test0079() {
		check(//
				"Integrate[f^(a + b/x^3)/x^4, x]", //
				"-f^(a + b/x^3)/(3*b*Log[f])", //
				2209);
	}

	// {2218}
	public void test0080() {
		check(//
				"Integrate[f^(a + b/x^3)/x^16, x]", //
				"-(f^a*Gamma[5, -((b*Log[f])/x^3)])/(3*b^5*Log[f]^5)", //
				2218);
	}

	// {2218}
	public void test0081() {
		check(//
				"Integrate[f^(a + b/x^3)/x^19, x]", //
				"(f^a*Gamma[6, -((b*Log[f])/x^3)])/(3*b^6*Log[f]^6)", //
				2218);
	}

	// {2218}
	public void test0082() {
		check(//
				"Integrate[f^(a + b/x^3)*x^4, x]", //
				"(f^a*x^5*Gamma[-5/3, -((b*Log[f])/x^3)]*(-((b*Log[f])/x^3))^(5/3))/3", //
				2218);
	}

	// {2218}
	public void test0083() {
		check(//
				"Integrate[f^(a + b/x^3)*x^3, x]", //
				"(f^a*x^4*Gamma[-4/3, -((b*Log[f])/x^3)]*(-((b*Log[f])/x^3))^(4/3))/3", //
				2218);
	}

	// {2218}
	public void test0084() {
		check(//
				"Integrate[f^(a + b/x^3)*x, x]", //
				"(f^a*x^2*Gamma[-2/3, -((b*Log[f])/x^3)]*(-((b*Log[f])/x^3))^(2/3))/3", //
				2218);
	}

	// {2208}
	public void test0085() {
		check(//
				"Integrate[f^(a + b/x^3), x]", //
				"(f^a*x*Gamma[-1/3, -((b*Log[f])/x^3)]*(-((b*Log[f])/x^3))^(1/3))/3", //
				2208);
	}

	// {2218}
	public void test0086() {
		check(//
				"Integrate[f^(a + b/x^3)/x^2, x]", //
				"(f^a*Gamma[1/3, -((b*Log[f])/x^3)])/(3*x*(-((b*Log[f])/x^3))^(1/3))", //
				2218);
	}

	// {2218}
	public void test0087() {
		check(//
				"Integrate[f^(a + b/x^3)/x^3, x]", //
				"(f^a*Gamma[2/3, -((b*Log[f])/x^3)])/(3*x^2*(-((b*Log[f])/x^3))^(2/3))", //
				2218);
	}

	// {2218}
	public void test0088() {
		check(//
				"Integrate[f^(a + b/x^3)/x^5, x]", //
				"(f^a*Gamma[4/3, -((b*Log[f])/x^3)])/(3*x^4*(-((b*Log[f])/x^3))^(4/3))", //
				2218);
	}

	// {2218}
	public void test0089() {
		check(//
				"Integrate[f^(a + b*x^n)*x^m, x]", //
				"-((f^a*x^(1 + m)*Gamma[(1 + m)/n, -(b*x^n*Log[f])])/(n*(-(b*x^n*Log[f]))^((1 + m)/n)))", //
				2218);
	}

	// {2218}
	public void test0090() {
		check(//
				"Integrate[f^(a + b*x^n)*x^3, x]", //
				"-((f^a*x^4*Gamma[4/n, -(b*x^n*Log[f])])/(n*(-(b*x^n*Log[f]))^(4/n)))", //
				2218);
	}

	// {2218}
	public void test0091() {
		check(//
				"Integrate[f^(a + b*x^n)*x^2, x]", //
				"-((f^a*x^3*Gamma[3/n, -(b*x^n*Log[f])])/(n*(-(b*x^n*Log[f]))^(3/n)))", //
				2218);
	}

	// {2218}
	public void test0092() {
		check(//
				"Integrate[f^(a + b*x^n)*x, x]", //
				"-((f^a*x^2*Gamma[2/n, -(b*x^n*Log[f])])/(n*(-(b*x^n*Log[f]))^(2/n)))", //
				2218);
	}

	// {2208}
	public void test0093() {
		check(//
				"Integrate[f^(a + b*x^n), x]", //
				"-((f^a*x*Gamma[n^(-1), -(b*x^n*Log[f])])/(n*(-(b*x^n*Log[f]))^n^(-1)))", //
				2208);
	}

	// {2210}
	public void test0094() {
		check(//
				"Integrate[f^(a + b*x^n)/x, x]", //
				"(f^a*ExpIntegralEi[b*x^n*Log[f]])/n", //
				2210);
	}

	// {2218}
	public void test0095() {
		check(//
				"Integrate[f^(a + b*x^n)/x^2, x]", //
				"-((f^a*Gamma[-n^(-1), -(b*x^n*Log[f])]*(-(b*x^n*Log[f]))^n^(-1))/(n*x))", //
				2218);
	}

	// {2218}
	public void test0096() {
		check(//
				"Integrate[f^(a + b*x^n)/x^3, x]", //
				"-((f^a*Gamma[-2/n, -(b*x^n*Log[f])]*(-(b*x^n*Log[f]))^(2/n))/(n*x^2))", //
				2218);
	}

	// {2218}
	public void test0097() {
		check(//
				"Integrate[f^(a + b*x^n)/x^4, x]", //
				"-((f^a*Gamma[-3/n, -(b*x^n*Log[f])]*(-(b*x^n*Log[f]))^(3/n))/(n*x^3))", //
				2218);
	}

	// {2209}
	public void test0098() {
		check(//
				"Integrate[f^(a + b*x^n)*x^(-1 + n), x]", //
				"f^(a + b*x^n)/(b*n*Log[f])", //
				2209);
	}

	// {2210}
	public void test0099() {
		check(//
				"Integrate[f^(a + b*x^n)/x, x]", //
				"(f^a*ExpIntegralEi[b*x^n*Log[f]])/n", //
				2210);
	}

	// {2204}
	public void test0100() {
		check(//
				"Integrate[f^(c*(a + b*x)^2), x]", //
				"(Sqrt[Pi]*Erfi[Sqrt[c]*(a + b*x)*Sqrt[Log[f]]])/(2*b*Sqrt[c]*Sqrt[Log[f]])", //
				2204);
	}

	// {2208}
	public void test0101() {
		check(//
				"Integrate[f^(c*(a + b*x)^3), x]", //
				"-((a + b*x)*Gamma[1/3, -(c*(a + b*x)^3*Log[f])])/(3*b*(-(c*(a + b*x)^3*Log[f]))^(1/3))", //
				2208);
	}

	// {2208}
	public void test0102() {
		check(//
				"Integrate[f^(c/(a + b*x)^3), x]", //
				"((a + b*x)*Gamma[-1/3, -((c*Log[f])/(a + b*x)^3)]*(-((c*Log[f])/(a + b*x)^3))^(1/3))/(3*b)", //
				2208);
	}

	// {2181}
	public void test0104() {
		check(//
				"Integrate[f^(c*(a + b*x))*x^m, x]", //
				"(f^(a*c)*x^m*Gamma[1 + m, -(b*c*x*Log[f])])/(b*c*Log[f]*(-(b*c*x*Log[f]))^m)", //
				2181);
	}

	// {2208}
	public void test0105() {
		check(//
				"Integrate[f^(c*(a + b*x)^n), x]", //
				"-(((a + b*x)*Gamma[n^(-1), -(c*(a + b*x)^n*Log[f])])/(b*n*(-(c*(a + b*x)^n*Log[f]))^n^(-1)))", //
				2208);
	}

	// {2218}
	public void test0106() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^2)*(c + d*x)^m, x]", //
				"-(F^a*(c + d*x)^(1 + m)*Gamma[(1 + m)/2, -(b*(c + d*x)^2*Log[F])]*(-(b*(c + d*x)^2*Log[F]))^((-1 - m)/2))/(2*d)", //
				2218);
	}

	// {2218}
	public void test0107() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^2)*(c + d*x)^11, x]", //
				"-(F^a*Gamma[6, -(b*(c + d*x)^2*Log[F])])/(2*b^6*d*Log[F]^6)", //
				2218);
	}

	// {2218}
	public void test0108() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^2)*(c + d*x)^9, x]", //
				"(F^a*Gamma[5, -(b*(c + d*x)^2*Log[F])])/(2*b^5*d*Log[F]^5)", //
				2218);
	}

	// {2209}
	public void test0109() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^2)*(c + d*x), x]", //
				"F^(a + b*(c + d*x)^2)/(2*b*d*Log[F])", //
				2209);
	}

	// {2210}
	public void test0110() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^2)/(c + d*x), x]", //
				"(F^a*ExpIntegralEi[b*(c + d*x)^2*Log[F]])/(2*d)", //
				2210);
	}

	// {2218}
	public void test0111() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^2)/(c + d*x)^9, x]", //
				"-(b^4*F^a*Gamma[-4, -(b*(c + d*x)^2*Log[F])]*Log[F]^4)/(2*d)", //
				2218);
	}

	// {2218}
	public void test0112() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^2)/(c + d*x)^11, x]", //
				"(b^5*F^a*Gamma[-5, -(b*(c + d*x)^2*Log[F])]*Log[F]^5)/(2*d)", //
				2218);
	}

	// {2218}
	public void test0113() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^2)*(c + d*x)^12, x]", //
				"-(F^a*(c + d*x)^13*Gamma[13/2, -(b*(c + d*x)^2*Log[F])])/(2*d*(-(b*(c + d*x)^2*Log[F]))^(13/2))", //
				2218);
	}

	// {2218}
	public void test0114() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^2)*(c + d*x)^10, x]", //
				"-(F^a*(c + d*x)^11*Gamma[11/2, -(b*(c + d*x)^2*Log[F])])/(2*d*(-(b*(c + d*x)^2*Log[F]))^(11/2))", //
				2218);
	}

	// {2204}
	public void test0115() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^2), x]", //
				"(F^a*Sqrt[Pi]*Erfi[Sqrt[b]*(c + d*x)*Sqrt[Log[F]]])/(2*Sqrt[b]*d*Sqrt[Log[F]])", //
				2204);
	}

	// {2218}
	public void test0116() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^2)/(c + d*x)^10, x]", //
				"-(F^a*Gamma[-9/2, -(b*(c + d*x)^2*Log[F])]*(-(b*(c + d*x)^2*Log[F]))^(9/2))/(2*d*(c + d*x)^9)", //
				2218);
	}

	// {2218}
	public void test0117() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^2)/(c + d*x)^12, x]", //
				"-(F^a*Gamma[-11/2, -(b*(c + d*x)^2*Log[F])]*(-(b*(c + d*x)^2*Log[F]))^(11/2))/(2*d*(c + d*x)^11)", //
				2218);
	}

	// {2218}
	public void test0118() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^3)*(c + d*x)^m, x]", //
				"-(F^a*(c + d*x)^(1 + m)*Gamma[(1 + m)/3, -(b*(c + d*x)^3*Log[F])]*(-(b*(c + d*x)^3*Log[F]))^((-1 - m)/3))/(3*d)", //
				2218);
	}

	// {2218}
	public void test0119() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^3)*(c + d*x)^17, x]", //
				"-(F^a*Gamma[6, -(b*(c + d*x)^3*Log[F])])/(3*b^6*d*Log[F]^6)", //
				2218);
	}

	// {2218}
	public void test0120() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^3)*(c + d*x)^14, x]", //
				"(F^a*Gamma[5, -(b*(c + d*x)^3*Log[F])])/(3*b^5*d*Log[F]^5)", //
				2218);
	}

	// {2209}
	public void test0121() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^3)*(c + d*x)^2, x]", //
				"F^(a + b*(c + d*x)^3)/(3*b*d*Log[F])", //
				2209);
	}

	// {2210}
	public void test0122() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^3)/(c + d*x), x]", //
				"(F^a*ExpIntegralEi[b*(c + d*x)^3*Log[F]])/(3*d)", //
				2210);
	}

	// {2218}
	public void test0123() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^3)/(c + d*x)^13, x]", //
				"-(b^4*F^a*Gamma[-4, -(b*(c + d*x)^3*Log[F])]*Log[F]^4)/(3*d)", //
				2218);
	}

	// {2218}
	public void test0124() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^3)/(c + d*x)^16, x]", //
				"(b^5*F^a*Gamma[-5, -(b*(c + d*x)^3*Log[F])]*Log[F]^5)/(3*d)", //
				2218);
	}

	// {2218}
	public void test0125() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^3)*(c + d*x)^3, x]", //
				"-(F^a*(c + d*x)^4*Gamma[4/3, -(b*(c + d*x)^3*Log[F])])/(3*d*(-(b*(c + d*x)^3*Log[F]))^(4/3))", //
				2218);
	}

	// {2218}
	public void test0126() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^3)*(c + d*x), x]", //
				"-(F^a*(c + d*x)^2*Gamma[2/3, -(b*(c + d*x)^3*Log[F])])/(3*d*(-(b*(c + d*x)^3*Log[F]))^(2/3))", //
				2218);
	}

	// {2208}
	public void test0127() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^3), x]", //
				"-(F^a*(c + d*x)*Gamma[1/3, -(b*(c + d*x)^3*Log[F])])/(3*d*(-(b*(c + d*x)^3*Log[F]))^(1/3))", //
				2208);
	}

	// {2218}
	public void test0128() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^3)/(c + d*x)^2, x]", //
				"-(F^a*Gamma[-1/3, -(b*(c + d*x)^3*Log[F])]*(-(b*(c + d*x)^3*Log[F]))^(1/3))/(3*d*(c + d*x))", //
				2218);
	}

	// {2218}
	public void test0129() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^3)/(c + d*x)^3, x]", //
				"-(F^a*Gamma[-2/3, -(b*(c + d*x)^3*Log[F])]*(-(b*(c + d*x)^3*Log[F]))^(2/3))/(3*d*(c + d*x)^2)", //
				2218);
	}

	// {2218}
	public void test0130() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^3)/(c + d*x)^5, x]", //
				"-(F^a*Gamma[-4/3, -(b*(c + d*x)^3*Log[F])]*(-(b*(c + d*x)^3*Log[F]))^(4/3))/(3*d*(c + d*x)^4)", //
				2218);
	}

	// {2218}
	public void test0131() {
		check(//
				"Integrate[F^(a + b/(c + d*x))*(c + d*x)^m, x]", //
				"(F^a*(c + d*x)^(1 + m)*Gamma[-1 - m, -((b*Log[F])/(c + d*x))]*(-((b*Log[F])/(c + d*x)))^(1 + m))/d", //
				2218);
	}

	// {2218}
	public void test0132() {
		check(//
				"Integrate[F^(a + b/(c + d*x))*(c + d*x)^4, x]", //
				"-((b^5*F^a*Gamma[-5, -((b*Log[F])/(c + d*x))]*Log[F]^5)/d)", //
				2218);
	}

	// {2218}
	public void test0133() {
		check(//
				"Integrate[F^(a + b/(c + d*x))*(c + d*x)^3, x]", //
				"(b^4*F^a*Gamma[-4, -((b*Log[F])/(c + d*x))]*Log[F]^4)/d", //
				2218);
	}

	// {2210}
	public void test0134() {
		check(//
				"Integrate[F^(a + b/(c + d*x))/(c + d*x), x]", //
				"-((F^a*ExpIntegralEi[(b*Log[F])/(c + d*x)])/d)", //
				2210);
	}

	// {2209}
	public void test0135() {
		check(//
				"Integrate[F^(a + b/(c + d*x))/(c + d*x)^2, x]", //
				"-(F^(a + b/(c + d*x))/(b*d*Log[F]))", //
				2209);
	}

	// {2218}
	public void test0136() {
		check(//
				"Integrate[F^(a + b/(c + d*x))/(c + d*x)^6, x]", //
				"-((F^a*Gamma[5, -((b*Log[F])/(c + d*x))])/(b^5*d*Log[F]^5))", //
				2218);
	}

	// {2218}
	public void test0137() {
		check(//
				"Integrate[F^(a + b/(c + d*x))/(c + d*x)^7, x]", //
				"(F^a*Gamma[6, -((b*Log[F])/(c + d*x))])/(b^6*d*Log[F]^6)", //
				2218);
	}

	// {2218}
	public void test0138() {
		check(//
				"Integrate[F^(a + b/(c + d*x)^2)*(c + d*x)^m, x]", //
				"(F^a*(c + d*x)^(1 + m)*Gamma[(-1 - m)/2, -((b*Log[F])/(c + d*x)^2)]*(-((b*Log[F])/(c + d*x)^2))^((1 + m)/2))/(2*d)", //
				2218);
	}

	// {2218}
	public void test0139() {
		check(//
				"Integrate[F^(a + b/(c + d*x)^2)*(c + d*x)^9, x]", //
				"-(b^5*F^a*Gamma[-5, -((b*Log[F])/(c + d*x)^2)]*Log[F]^5)/(2*d)", //
				2218);
	}

	// {2218}
	public void test0140() {
		check(//
				"Integrate[F^(a + b/(c + d*x)^2)*(c + d*x)^7, x]", //
				"(b^4*F^a*Gamma[-4, -((b*Log[F])/(c + d*x)^2)]*Log[F]^4)/(2*d)", //
				2218);
	}

	// {2210}
	public void test0141() {
		check(//
				"Integrate[F^(a + b/(c + d*x)^2)/(c + d*x), x]", //
				"-(F^a*ExpIntegralEi[(b*Log[F])/(c + d*x)^2])/(2*d)", //
				2210);
	}

	// {2209}
	public void test0142() {
		check(//
				"Integrate[F^(a + b/(c + d*x)^2)/(c + d*x)^3, x]", //
				"-F^(a + b/(c + d*x)^2)/(2*b*d*Log[F])", //
				2209);
	}

	// {2218}
	public void test0143() {
		check(//
				"Integrate[F^(a + b/(c + d*x)^2)/(c + d*x)^11, x]", //
				"-(F^a*Gamma[5, -((b*Log[F])/(c + d*x)^2)])/(2*b^5*d*Log[F]^5)", //
				2218);
	}

	// {2218}
	public void test0144() {
		check(//
				"Integrate[F^(a + b/(c + d*x)^2)/(c + d*x)^13, x]", //
				"(F^a*Gamma[6, -((b*Log[F])/(c + d*x)^2)])/(2*b^6*d*Log[F]^6)", //
				2218);
	}

	// {2218}
	public void test0145() {
		check(//
				"Integrate[F^(a + b/(c + d*x)^2)*(c + d*x)^10, x]", //
				"(F^a*(c + d*x)^11*Gamma[-11/2, -((b*Log[F])/(c + d*x)^2)]*(-((b*Log[F])/(c + d*x)^2))^(11/2))/(2*d)", //
				2218);
	}

	// {2218}
	public void test0146() {
		check(//
				"Integrate[F^(a + b/(c + d*x)^2)*(c + d*x)^8, x]", //
				"(F^a*(c + d*x)^9*Gamma[-9/2, -((b*Log[F])/(c + d*x)^2)]*(-((b*Log[F])/(c + d*x)^2))^(9/2))/(2*d)", //
				2218);
	}

	// {2218}
	public void test0147() {
		check(//
				"Integrate[F^(a + b/(c + d*x)^2)/(c + d*x)^12, x]", //
				"(F^a*Gamma[11/2, -((b*Log[F])/(c + d*x)^2)])/(2*d*(c + d*x)^11*(-((b*Log[F])/(c + d*x)^2))^(11/2))", //
				2218);
	}

	// {2218}
	public void test0148() {
		check(//
				"Integrate[F^(a + b/(c + d*x)^2)/(c + d*x)^14, x]", //
				"(F^a*Gamma[13/2, -((b*Log[F])/(c + d*x)^2)])/(2*d*(c + d*x)^13*(-((b*Log[F])/(c + d*x)^2))^(13/2))", //
				2218);
	}

	// {2218}
	public void test0149() {
		check(//
				"Integrate[F^(a + b/(c + d*x)^3)*(c + d*x)^m, x]", //
				"(F^a*(c + d*x)^(1 + m)*Gamma[(-1 - m)/3, -((b*Log[F])/(c + d*x)^3)]*(-((b*Log[F])/(c + d*x)^3))^((1 + m)/3))/(3*d)", //
				2218);
	}

	// {2218}
	public void test0150() {
		check(//
				"Integrate[F^(a + b/(c + d*x)^3)*(c + d*x)^14, x]", //
				"-(b^5*F^a*Gamma[-5, -((b*Log[F])/(c + d*x)^3)]*Log[F]^5)/(3*d)", //
				2218);
	}

	// {2218}
	public void test0151() {
		check(//
				"Integrate[F^(a + b/(c + d*x)^3)*(c + d*x)^11, x]", //
				"(b^4*F^a*Gamma[-4, -((b*Log[F])/(c + d*x)^3)]*Log[F]^4)/(3*d)", //
				2218);
	}

	// {2210}
	public void test0152() {
		check(//
				"Integrate[F^(a + b/(c + d*x)^3)/(c + d*x), x]", //
				"-(F^a*ExpIntegralEi[(b*Log[F])/(c + d*x)^3])/(3*d)", //
				2210);
	}

	// {2209}
	public void test0153() {
		check(//
				"Integrate[F^(a + b/(c + d*x)^3)/(c + d*x)^4, x]", //
				"-F^(a + b/(c + d*x)^3)/(3*b*d*Log[F])", //
				2209);
	}

	// {2218}
	public void test0154() {
		check(//
				"Integrate[F^(a + b/(c + d*x)^3)/(c + d*x)^16, x]", //
				"-(F^a*Gamma[5, -((b*Log[F])/(c + d*x)^3)])/(3*b^5*d*Log[F]^5)", //
				2218);
	}

	// {2218}
	public void test0155() {
		check(//
				"Integrate[F^(a + b/(c + d*x)^3)/(c + d*x)^19, x]", //
				"(F^a*Gamma[6, -((b*Log[F])/(c + d*x)^3)])/(3*b^6*d*Log[F]^6)", //
				2218);
	}

	// {2218}
	public void test0156() {
		check(//
				"Integrate[F^(a + b/(c + d*x)^3)*(c + d*x)^3, x]", //
				"(F^a*(c + d*x)^4*Gamma[-4/3, -((b*Log[F])/(c + d*x)^3)]*(-((b*Log[F])/(c + d*x)^3))^(4/3))/(3*d)", //
				2218);
	}

	// {2218}
	public void test0157() {
		check(//
				"Integrate[F^(a + b/(c + d*x)^3)*(c + d*x), x]", //
				"(F^a*(c + d*x)^2*Gamma[-2/3, -((b*Log[F])/(c + d*x)^3)]*(-((b*Log[F])/(c + d*x)^3))^(2/3))/(3*d)", //
				2218);
	}

	// {2208}
	public void test0158() {
		check(//
				"Integrate[F^(a + b/(c + d*x)^3), x]", //
				"(F^a*(c + d*x)*Gamma[-1/3, -((b*Log[F])/(c + d*x)^3)]*(-((b*Log[F])/(c + d*x)^3))^(1/3))/(3*d)", //
				2208);
	}

	// {2218}
	public void test0159() {
		check(//
				"Integrate[F^(a + b/(c + d*x)^3)/(c + d*x)^2, x]", //
				"(F^a*Gamma[1/3, -((b*Log[F])/(c + d*x)^3)])/(3*d*(c + d*x)*(-((b*Log[F])/(c + d*x)^3))^(1/3))", //
				2218);
	}

	// {2218}
	public void test0160() {
		check(//
				"Integrate[F^(a + b/(c + d*x)^3)/(c + d*x)^3, x]", //
				"(F^a*Gamma[2/3, -((b*Log[F])/(c + d*x)^3)])/(3*d*(c + d*x)^2*(-((b*Log[F])/(c + d*x)^3))^(2/3))", //
				2218);
	}

	// {2218}
	public void test0161() {
		check(//
				"Integrate[F^(a + b/(c + d*x)^3)/(c + d*x)^5, x]", //
				"(F^a*Gamma[4/3, -((b*Log[F])/(c + d*x)^3)])/(3*d*(c + d*x)^4*(-((b*Log[F])/(c + d*x)^3))^(4/3))", //
				2218);
	}

	// {2218}
	public void test0162() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^n)*(c + d*x)^m, x]", //
				"-((F^a*(c + d*x)^(1 + m)*Gamma[(1 + m)/n, -(b*(c + d*x)^n*Log[F])])/(d*n*(-(b*(c + d*x)^n*Log[F]))^((1 + m)/n)))", //
				2218);
	}

	// {2218}
	public void test0163() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^n)*(c + d*x)^3, x]", //
				"-((F^a*(c + d*x)^4*Gamma[4/n, -(b*(c + d*x)^n*Log[F])])/(d*n*(-(b*(c + d*x)^n*Log[F]))^(4/n)))", //
				2218);
	}

	// {2218}
	public void test0164() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^n)*(c + d*x)^2, x]", //
				"-((F^a*(c + d*x)^3*Gamma[3/n, -(b*(c + d*x)^n*Log[F])])/(d*n*(-(b*(c + d*x)^n*Log[F]))^(3/n)))", //
				2218);
	}

	// {2218}
	public void test0165() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^n)*(c + d*x), x]", //
				"-((F^a*(c + d*x)^2*Gamma[2/n, -(b*(c + d*x)^n*Log[F])])/(d*n*(-(b*(c + d*x)^n*Log[F]))^(2/n)))", //
				2218);
	}

	// {2208}
	public void test0166() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^n), x]", //
				"-((F^a*(c + d*x)*Gamma[n^(-1), -(b*(c + d*x)^n*Log[F])])/(d*n*(-(b*(c + d*x)^n*Log[F]))^n^(-1)))", //
				2208);
	}

	// {2210}
	public void test0167() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^n)/(c + d*x), x]", //
				"(F^a*ExpIntegralEi[b*(c + d*x)^n*Log[F]])/(d*n)", //
				2210);
	}

	// {2218}
	public void test0168() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^n)/(c + d*x)^2, x]", //
				"-((F^a*Gamma[-n^(-1), -(b*(c + d*x)^n*Log[F])]*(-(b*(c + d*x)^n*Log[F]))^n^(-1))/(d*n*(c + d*x)))", //
				2218);
	}

	// {2218}
	public void test0169() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^n)/(c + d*x)^3, x]", //
				"-((F^a*Gamma[-2/n, -(b*(c + d*x)^n*Log[F])]*(-(b*(c + d*x)^n*Log[F]))^(2/n))/(d*n*(c + d*x)^2))", //
				2218);
	}

	// {2218}
	public void test0170() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^n)/(c + d*x)^4, x]", //
				"-((F^a*Gamma[-3/n, -(b*(c + d*x)^n*Log[F])]*(-(b*(c + d*x)^n*Log[F]))^(3/n))/(d*n*(c + d*x)^3))", //
				2218);
	}

	// {2218}
	public void test0171() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^n)*(c + d*x)^(-1 + 6*n), x]", //
				"-((F^a*Gamma[6, -(b*(c + d*x)^n*Log[F])])/(b^6*d*n*Log[F]^6))", //
				2218);
	}

	// {2218}
	public void test0172() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^n)*(c + d*x)^(-1 + 5*n), x]", //
				"(F^a*Gamma[5, -(b*(c + d*x)^n*Log[F])])/(b^5*d*n*Log[F]^5)", //
				2218);
	}

	// {2209}
	public void test0173() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^n)*(c + d*x)^(-1 + n), x]", //
				"F^(a + b*(c + d*x)^n)/(b*d*n*Log[F])", //
				2209);
	}

	// {2210}
	public void test0174() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^n)/(c + d*x), x]", //
				"(F^a*ExpIntegralEi[b*(c + d*x)^n*Log[F]])/(d*n)", //
				2210);
	}

	// {2218}
	public void test0175() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^n)*(c + d*x)^(-1 - 4*n), x]", //
				"-((b^4*F^a*Gamma[-4, -(b*(c + d*x)^n*Log[F])]*Log[F]^4)/(d*n))", //
				2218);
	}

	// {2218}
	public void test0176() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^n)*(c + d*x)^(-1 - 5*n), x]", //
				"(b^5*F^a*Gamma[-5, -(b*(c + d*x)^n*Log[F])]*Log[F]^5)/(d*n)", //
				2218);
	}

	// {2204}
	public void test0177() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^2), x]", //
				"(F^a*Sqrt[Pi]*Erfi[Sqrt[b]*(c + d*x)*Sqrt[Log[F]]])/(2*Sqrt[b]*d*Sqrt[Log[F]])", //
				2204);
	}

	// {2208}
	public void test0178() {
		check(//
				"Integrate[E^(e*(c + d*x)^3), x]", //
				"-((c + d*x)*Gamma[1/3, -(e*(c + d*x)^3)])/(3*d*(-(e*(c + d*x)^3))^(1/3))", //
				2208);
	}

	// {2208}
	public void test0179() {
		check(//
				"Integrate[E^(e/(c + d*x)^3), x]", //
				"((-(e/(c + d*x)^3))^(1/3)*(c + d*x)*Gamma[-1/3, -(e/(c + d*x)^3)])/(3*d)", //
				2208);
	}

	// {2236}
	public void test0181() {
		check(//
				"Integrate[f^(a + b*x + c*x^2)*(b + 2*c*x), x]", //
				"f^(a + b*x + c*x^2)/Log[f]", //
				2236);
	}

	// {2238}
	public void test0182() {
		check(//
				"Integrate[f^(a + b*x + c*x^2)/(b + 2*c*x), x]", //
				"(f^(a - b^2/(4*c))*ExpIntegralEi[((b + 2*c*x)^2*Log[f])/(4*c)])/(4*c)", //
				2238);
	}

	// {2236}
	public void test0183() {
		check(//
				"Integrate[f^(b*x + c*x^2)*(b + 2*c*x), x]", //
				"f^(b*x + c*x^2)/Log[f]", //
				2236);
	}

	// {2238}
	public void test0184() {
		check(//
				"Integrate[f^(b*x + c*x^2)/(b + 2*c*x), x]", //
				"ExpIntegralEi[((b + 2*c*x)^2*Log[f])/(4*c)]/(4*c*f^(b^2/(4*c)))", //
				2238);
	}

	// {208}
	public void test0185() {
		check(//
				"Integrate[(d^2 - e^2*x^2)^(-1), x]", //
				"ArcTanh[(e*x)/d]/(d*e)", //
				208);
	}

	// {6706}
	public void test0186() {
		check(//
				"Integrate[F^(a + b*x + c*x^3)*(b + 3*c*x^2), x]", //
				"F^(a + b*x + c*x^3)/Log[F]", //
				6706);
	}

	// {6706}
	public void test0187() {
		check(//
				"Integrate[(F^(a + b*x + c*x^2)^(-1)*(b + 2*c*x))/(a + b*x + c*x^2)^2, x]", //
				"-(F^(a + b*x + c*x^2)^(-1)/Log[F])", //
				6706);
	}

	// {2236}
	public void test0188() {
		check(//
				"Integrate[E^(a + b*x + c*x^2)*(b + 2*c*x), x]", //
				"E^(a + b*x + c*x^2)", //
				2236);
	}

	// {2209}
	public void test0189() {
		check(//
				"Integrate[E^(2 - x^2)*x, x]", //
				"-E^(2 - x^2)/2", //
				2209);
	}

	// {2209}
	public void test0190() {
		check(//
				"Integrate[E^Sqrt[4 + x]/Sqrt[4 + x], x]", //
				"2*E^Sqrt[4 + x]", //
				2209);
	}

	// {2209}
	public void test0191() {
		check(//
				"Integrate[E^(1 + x^2)*x, x]", //
				"E^(1 + x^2)/2", //
				2209);
	}

	// {2209}
	public void test0192() {
		check(//
				"Integrate[E^(1 + x^3)*x^2, x]", //
				"E^(1 + x^3)/3", //
				2209);
	}

	// {2209}
	public void test0193() {
		check(//
				"Integrate[E^Sqrt[x]/Sqrt[x], x]", //
				"2*E^Sqrt[x]", //
				2209);
	}

	// {2209}
	public void test0194() {
		check(//
				"Integrate[E^x^(1/3)/x^(2/3), x]", //
				"3*E^x^(1/3)", //
				2209);
	}

	// {4433}
	public void test0195() {
		check(//
				"Integrate[Cos[3*x]/E^x, x]", //
				"-Cos[3*x]/(10*E^x) + (3*Sin[3*x])/(10*E^x)", //
				4433);
	}

	// {4433}
	public void test0196() {
		check(//
				"Integrate[E^(3*x)*Cos[5*x], x]", //
				"(3*E^(3*x)*Cos[5*x])/34 + (5*E^(3*x)*Sin[5*x])/34", //
				4433);
	}

	// {4433}
	public void test0197() {
		check(//
				"Integrate[E^x*Cos[4 + 3*x], x]", //
				"(E^x*Cos[4 + 3*x])/10 + (3*E^x*Sin[4 + 3*x])/10", //
				4433);
	}

	// {4432}
	public void test0198() {
		check(//
				"Integrate[E^(6*x)*Sin[3*x], x]", //
				"-(E^(6*x)*Cos[3*x])/15 + (2*E^(6*x)*Sin[3*x])/15", //
				4432);
	}

	// {2289}
	public void test0199() {
		check(//
				"Integrate[(E^x^2*x^3)/(1 + x^2)^2, x]", //
				"E^x^2/(2*(1 + x^2))", //
				2289);
	}

	// {6686}
	public void test0200() {
		check(//
				"Integrate[(1 + E^x)/Sqrt[E^x + x], x]", //
				"2*Sqrt[E^x + x]", //
				6686);
	}

	// {6684}
	public void test0201() {
		check(//
				"Integrate[(1 + E^x)/(E^x + x), x]", //
				"Log[E^x + x]", //
				6684);
	}

	// {2209}
	public void test0202() {
		check(//
				"Integrate[3^(1 + x^2)*x, x]", //
				"3^(1 + x^2)/(2*Log[3])", //
				2209);
	}

	// {2209}
	public void test0203() {
		check(//
				"Integrate[2^Sqrt[x]/Sqrt[x], x]", //
				"2^(1 + Sqrt[x])/Log[2]", //
				2209);
	}

	// {2209}
	public void test0204() {
		check(//
				"Integrate[2^x^(-1)/x^2, x]", //
				"-(2^x^(-1)/Log[2])", //
				2209);
	}

	// {2209}
	public void test0205() {
		check(//
				"Integrate[10^Sqrt[x]/Sqrt[x], x]", //
				"(2^(1 + Sqrt[x])*5^Sqrt[x])/Log[10]", //
				"(2*10^Sqrt[x])/Log[10]",//
				2209);
	}

	// {6686}
	public void test0207() {
		check(//
				"Integrate[-((1 + E^x)/(E^x + x)^(1/3)), x]", //
				"(-3*(E^x + x)^(2/3))/2", //
				6686);
	}

	// {2218}
	public void test0209() {
		fSeconds=0;
		check(//
				"Integrate[E^x^n*x^m, x]", //
				"-((x^(1 + m)*Gamma[(1 + m)/n, -x^n])/(n*(-x^n)^((1 + m)/n)))", //
				2218);
	}

	// {2218}
	public void test0210() {
		check(//
				"Integrate[f^x^n*x^m, x]", //
				"-((x^(1 + m)*Gamma[(1 + m)/n, -(x^n*Log[f])])/(n*(-(x^n*Log[f]))^((1 + m)/n)))", //
				2218);
	}

	// {2218}
	public void test0211() {
		check(//
				"Integrate[E^(a + b*x)^n*(a + b*x)^m, x]", //
				"-(((a + b*x)^(1 + m)*Gamma[(1 + m)/n, -(a + b*x)^n])/(b*n*(-(a + b*x)^n)^((1 + m)/n)))", //
				2218);
	}

	// {2218}
	public void test0212() {
		check(//
				"Integrate[f^(a + b*x)^n*(a + b*x)^m, x]", //
				"-(((a + b*x)^(1 + m)*Gamma[(1 + m)/n, -((a + b*x)^n*Log[f])])/(b*n*(-((a + b*x)^n*Log[f]))^((1 + m)/n)))", //
				2218);
	}
}
