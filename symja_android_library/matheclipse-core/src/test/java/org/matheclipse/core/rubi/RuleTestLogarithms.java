package org.matheclipse.core.rubi;

public class RuleTestLogarithms extends AbstractRubiTestCase {

	public RuleTestLogarithms(String name) {
		super(name, false);
	}

	// {2315}
	public void test0001() {
		check("Integrate[Log[c*x]/(1 - c*x), x]", 2315);
	}

	// {2315}
	public void test0002() {
		check("Integrate[Log[x/c]/(c - x), x]", 2315);
	}

	// {2318}
	public void test0003() {
		check("Integrate[Sqrt[a + b*Log[c*x^n]]/(d + e*x)^2, x]", 2318);
	}

	// {2319}
	public void test0004() {
		check("Integrate[Sqrt[a + b*Log[c*x^n]]/(d + e*x)^3, x]", 2319);
	}

	// {2304}
	public void test0005() {
		check("Integrate[(f*x)^m*(a + b*Log[c*x^n]), x]", 2304);
	}

	// {2304}
	public void test0006() {
		check("Integrate[(f*x)^m*(a + b*Log[c*x^n]), x]", 2304);
	}

	// {2304}
	public void test0007() {
		check("Integrate[(f*x)^(-1 + m)*(a + b*Log[c*x^n]), x]", 2304);
	}

	// {2304}
	public void test0008() {
		check("Integrate[(f*x)^m*(a + b*Log[c*x^n]), x]", 2304);
	}

	// {2304}
	public void test0009() {
		check("Integrate[x^3*Log[c*x], x]", 2304);
	}

	// {2304}
	public void test0010() {
		check("Integrate[x^2*Log[c*x], x]", 2304);
	}

	// {2304}
	public void test0011() {
		check("Integrate[x*Log[c*x], x]", 2304);
	}

	// {2295}
	public void test0012() {
		check("Integrate[Log[c*x], x]", 2295);
	}

	// {2301}
	public void test0013() {
		check("Integrate[Log[c*x]/x, x]", 2301);
	}

	// {2304}
	public void test0014() {
		check("Integrate[Log[c*x]/x^2, x]", 2304);
	}

	// {2304}
	public void test0015() {
		check("Integrate[Log[c*x]/x^3, x]", 2304);
	}

	// {2298}
	public void test0016() {
		check("Integrate[Log[c*x]^(-1), x]", 2298);
	}

	// {2304}
	public void test0017() {
		check("Integrate[x^3*(a + b*Log[c*x^n]), x]", 2304);
	}

	// {2304}
	public void test0018() {
		check("Integrate[x^2*(a + b*Log[c*x^n]), x]", 2304);
	}

	// {2304}
	public void test0019() {
		check("Integrate[x*(a + b*Log[c*x^n]), x]", 2304);
	}

	// {2301}
	public void test0020() {
		check("Integrate[(a + b*Log[c*x^n])/x, x]", 2301);
	}

	// {2304}
	public void test0021() {
		check("Integrate[(a + b*Log[c*x^n])/x^2, x]", 2304);
	}

	// {2304}
	public void test0022() {
		check("Integrate[(a + b*Log[c*x^n])/x^3, x]", 2304);
	}

	// {2304}
	public void test0023() {
		check("Integrate[(d*x)^(5/2)*(a + b*Log[c*x^n]), x]", 2304);
	}

	// {2304}
	public void test0024() {
		check("Integrate[(d*x)^(3/2)*(a + b*Log[c*x^n]), x]", 2304);
	}

	// {2304}
	public void test0025() {
		check("Integrate[Sqrt[d*x]*(a + b*Log[c*x^n]), x]", 2304);
	}

	// {2304}
	public void test0026() {
		check("Integrate[(a + b*Log[c*x^n])/Sqrt[d*x], x]", 2304);
	}

	// {2304}
	public void test0027() {
		check("Integrate[(a + b*Log[c*x^n])/(d*x)^(3/2), x]", 2304);
	}

	// {2304}
	public void test0028() {
		check("Integrate[(a + b*Log[c*x^n])/(d*x)^(5/2), x]", 2304);
	}

	// {2303}
	public void test0029() {
		check("Integrate[(d*x)^m*(a + (a*(1 + m)*Log[c*x^n])/n), x]", 2303);
	}

	// {2304}
	public void test0030() {
		check("Integrate[(d*x)^m*(a + b*Log[c*x^n]), x]", 2304);
	}

	// {2304}
	public void test0031() {
		check("Integrate[(d*x)^(-1 + n)*Log[c*x^n], x]", 2304);
	}

	// {2384}
	public void test0032() {
		check("Integrate[PolyLog[k, e*x^q]/(x*(a + b*Log[c*x^n])^2), x]", 2384);
	}

	// {2391}
	public void test0033() {
		check("Integrate[Log[1 + e*x]/x, x]", 2391);
	}

	// {2301}
	public void test0034() {
		check("Integrate[Log[e*x]/x, x]", 2301);
	}

	// {2301}
	public void test0035() {
		check("Integrate[(a + b*Log[e*x])/x, x]", 2301);
	}

	// {2397}
	public void test0036() {
		check("Integrate[Sqrt[a + b*Log[c*(d + e*x)^n]]/(f + g*x)^2, x]", 2397);
	}

	// {2398}
	public void test0037() {
		check("Integrate[Sqrt[a + b*Log[c*(d + e*x)^n]]/(f + g*x)^3, x]", 2398);
	}

	// {2397}
	public void test0038() {
		check("Integrate[(a + b*Log[c*(d + e*x)^n])^(3/2)/(f + g*x)^2, x]", 2397);
	}

	// {2398}
	public void test0039() {
		check("Integrate[(a + b*Log[c*(d + e*x)^n])^(3/2)/(f + g*x)^3, x]", 2398);
	}

	// {2397}
	public void test0040() {
		check("Integrate[(a + b*Log[c*(d + e*x)^n])^(5/2)/(f + g*x)^2, x]", 2397);
	}

	// {2398}
	public void test0041() {
		check("Integrate[(a + b*Log[c*(d + e*x)^n])^(5/2)/(f + g*x)^3, x]", 2398);
	}

	// {2398}
	public void test0042() {
		check("Integrate[Sqrt[f + g*x]*Sqrt[a + b*Log[c*(d + e*x)^n]], x]", 2398);
	}

	// {2398}
	public void test0043() {
		check("Integrate[Sqrt[a + b*Log[c*(d + e*x)^n]]/Sqrt[f + g*x], x]", 2398);
	}

	// {2398}
	public void test0044() {
		check("Integrate[Sqrt[a + b*Log[c*(d + e*x)^n]]/(f + g*x)^(3/2), x]", 2398);
	}

	// {2447}
	public void test0045() {
		check("Integrate[Log[(a*(1 - c) + b*(1 + c)*x)/(a + b*x)]/(a^2 - b^2*x^2), x]", 2447);
	}

	// {2447}
	public void test0046() {
		check("Integrate[Log[1 - (c*(a - b*x))/(a + b*x)]/(a^2 - b^2*x^2), x]", 2447);
	}

	// {2435}
	public void test0047() {
		check("Integrate[(Log[a + b*x]*Log[c + d*x])/x, x]", 2435);
	}

	// {2391}
	public void test0048() {
		check("Integrate[Log[1 + b/x]/x, x]", 2391);
	}

	// {2457}
	public void test0049() {
		check("Integrate[Log[c*(a + b*x^2)^p]^3/x^2, x]", 2457);
	}

	// {2457}
	public void test0050() {
		check("Integrate[(f*x)^m*Log[c*(d + e*x^2)^p]^3, x]", 2457);
	}

	// {2457}
	public void test0051() {
		check("Integrate[(f*x)^m*Log[c*(d + e*x^2)^p]^2, x]", 2457);
	}

	// {2391}
	public void test0052() {
		check("Integrate[Log[1 + e*x^n]/x, x]", 2391);
	}

	// {2465}
	public void test0053() {
		check("Integrate[Log[(a + b*x^n)/x^n]/(c + d*x), x]", 2465);
	}

	// {2458}
	public void test0054() {
		check("Integrate[(a + b*Log[c*(d + e*Sqrt[x])])^p/x, x]", 2458);
	}

	// {2458}
	public void test0055() {
		check("Integrate[(a + b*Log[c*(d + e*Sqrt[x])])^p/x^2, x]", 2458);
	}

	// {2458}
	public void test0056() {
		check("Integrate[(a + b*Log[c*(d + e*Sqrt[x])^2])^p/x, x]", 2458);
	}

	// {2458}
	public void test0057() {
		check("Integrate[(a + b*Log[c*(d + e*Sqrt[x])^2])^p/x^2, x]", 2458);
	}

	// {2458}
	public void test0058() {
		check("Integrate[x*(a + b*Log[c*(d + e/Sqrt[x])])^p, x]", 2458);
	}

	// {2451}
	public void test0059() {
		check("Integrate[(a + b*Log[c*(d + e/Sqrt[x])])^p, x]", 2451);
	}

	// {2458}
	public void test0060() {
		check("Integrate[(a + b*Log[c*(d + e/Sqrt[x])])^p/x, x]", 2458);
	}

	// {2458}
	public void test0061() {
		check("Integrate[x*(a + b*Log[c*(d + e/Sqrt[x])^2])^p, x]", 2458);
	}

	// {2451}
	public void test0062() {
		check("Integrate[(a + b*Log[c*(d + e/Sqrt[x])^2])^p, x]", 2451);
	}

	// {2458}
	public void test0063() {
		check("Integrate[(a + b*Log[c*(d + e/Sqrt[x])^2])^p/x, x]", 2458);
	}

	// {2458}
	public void test0064() {
		check("Integrate[(a + b*Log[c*(d + e*x^(1/3))])^p/x, x]", 2458);
	}

	// {2458}
	public void test0065() {
		check("Integrate[(a + b*Log[c*(d + e*x^(1/3))])^p/x^2, x]", 2458);
	}

	// {2458}
	public void test0066() {
		check("Integrate[(a + b*Log[c*(d + e*x^(1/3))^2])^p/x, x]", 2458);
	}

	// {2458}
	public void test0067() {
		check("Integrate[(a + b*Log[c*(d + e*x^(1/3))^2])^p/x^2, x]", 2458);
	}

	// {2458}
	public void test0068() {
		check("Integrate[(a + b*Log[c*(d + e*x^(2/3))])^p/x, x]", 2458);
	}

	// {2458}
	public void test0069() {
		check("Integrate[(a + b*Log[c*(d + e*x^(2/3))])^p/x^3, x]", 2458);
	}

	// {2458}
	public void test0070() {
		check("Integrate[x^2*(a + b*Log[c*(d + e*x^(2/3))])^p, x]", 2458);
	}

	// {2451}
	public void test0071() {
		check("Integrate[(a + b*Log[c*(d + e*x^(2/3))])^p, x]", 2451);
	}

	// {2458}
	public void test0072() {
		check("Integrate[(a + b*Log[c*(d + e*x^(2/3))])^p/x^2, x]", 2458);
	}

	// {2458}
	public void test0073() {
		check("Integrate[(a + b*Log[c*(d + e*x^(2/3))^2])^p/x, x]", 2458);
	}

	// {2458}
	public void test0074() {
		check("Integrate[(a + b*Log[c*(d + e*x^(2/3))^2])^p/x^3, x]", 2458);
	}

	// {2458}
	public void test0075() {
		check("Integrate[x^2*(a + b*Log[c*(d + e*x^(2/3))^2])^p, x]", 2458);
	}

	// {2451}
	public void test0076() {
		check("Integrate[(a + b*Log[c*(d + e*x^(2/3))^2])^p, x]", 2451);
	}

	// {2458}
	public void test0077() {
		check("Integrate[(a + b*Log[c*(d + e*x^(2/3))^2])^p/x^2, x]", 2458);
	}

	// {2458}
	public void test0078() {
		check("Integrate[x*(a + b*Log[c*(d + e/x^(1/3))])^p, x]", 2458);
	}

	// {2451}
	public void test0079() {
		check("Integrate[(a + b*Log[c*(d + e/x^(1/3))])^p, x]", 2451);
	}

	// {2458}
	public void test0080() {
		check("Integrate[(a + b*Log[c*(d + e/x^(1/3))])^p/x, x]", 2458);
	}

	// {2458}
	public void test0081() {
		check("Integrate[x*(a + b*Log[c*(d + e/x^(1/3))^2])^p, x]", 2458);
	}

	// {2451}
	public void test0082() {
		check("Integrate[(a + b*Log[c*(d + e/x^(1/3))^2])^p, x]", 2451);
	}

	// {2458}
	public void test0083() {
		check("Integrate[(a + b*Log[c*(d + e/x^(1/3))^2])^p/x, x]", 2458);
	}

	// {2458}
	public void test0084() {
		check("Integrate[x^3*(a + b*Log[c*(d + e/x^(2/3))])^p, x]", 2458);
	}

	// {2458}
	public void test0085() {
		check("Integrate[x^2*(a + b*Log[c*(d + e/x^(2/3))])^p, x]", 2458);
	}

	// {2458}
	public void test0086() {
		check("Integrate[x*(a + b*Log[c*(d + e/x^(2/3))])^p, x]", 2458);
	}

	// {2451}
	public void test0087() {
		check("Integrate[(a + b*Log[c*(d + e/x^(2/3))])^p, x]", 2451);
	}

	// {2458}
	public void test0088() {
		check("Integrate[(a + b*Log[c*(d + e/x^(2/3))])^p/x, x]", 2458);
	}

	// {2458}
	public void test0089() {
		check("Integrate[(a + b*Log[c*(d + e/x^(2/3))])^p/x^2, x]", 2458);
	}

	// {2458}
	public void test0090() {
		check("Integrate[x^3*(a + b*Log[c*(d + e/x^(2/3))^2])^p, x]", 2458);
	}

	// {2458}
	public void test0091() {
		check("Integrate[x^2*(a + b*Log[c*(d + e/x^(2/3))^2])^p, x]", 2458);
	}

	// {2458}
	public void test0092() {
		check("Integrate[x*(a + b*Log[c*(d + e/x^(2/3))^2])^p, x]", 2458);
	}

	// {2451}
	public void test0093() {
		check("Integrate[(a + b*Log[c*(d + e/x^(2/3))^2])^p, x]", 2451);
	}

	// {2458}
	public void test0094() {
		check("Integrate[(a + b*Log[c*(d + e/x^(2/3))^2])^p/x, x]", 2458);
	}

	// {2458}
	public void test0095() {
		check("Integrate[(a + b*Log[c*(d + e/x^(2/3))^2])^p/x^2, x]", 2458);
	}

	// {2481}
	public void test0096() {
		check("Integrate[(a + b*Log[c*(d + e*x^m)^n])/(x*Log[f*x^p]^2), x]", 2481);
	}

	// {2481}
	public void test0097() {
		check("Integrate[(a + b*Log[c*(d + e*x^m)^n])/(x*Log[f*x^p]^3), x]", 2481);
	}

	// {2447}
	public void test0098() {
		check("Integrate[Log[1 + (a + b*x)^(-1)]/(a + b*x), x]", 2447);
	}

	// {2447}
	public void test0099() {
		check("Integrate[Log[1 - (a + b*x)^(-1)]/(a + b*x), x]", 2447);
	}

	// {2505}
	public void test0100() {
		check("Integrate[Log[e*((f*(a + b*x))/(c + d*x))^r]/((a + b*x)*(c + d*x)), x]", 2505);
	}

	// {2505}
	public void test0101() {
		check("Integrate[Log[e*((a + b*x)/(c + d*x))^n]^p/((a + b*x)*(c + d*x)), x]", 2505);
	}

	// {2505}
	public void test0102() {
		check("Integrate[Log[e*((a + b*x)/(c + d*x))^n]^p/(a*c + (b*c + a*d)*x + b*d*x^2), x]", 2505);
	}

	// {2505}
	public void test0103() {
		check("Integrate[Log[e*((a + b*x)^n1/(c + d*x)^n1)^n]^3/((a + b*x)*(c + d*x)), x]", 2505);
	}

	// {2505}
	public void test0104() {
		check("Integrate[Log[e*((a + b*x)^n1/(c + d*x)^n1)^n]^2/((a + b*x)*(c + d*x)), x]", 2505);
	}

	// {2505}
	public void test0105() {
		check("Integrate[Log[e*((a + b*x)^n1/(c + d*x)^n1)^n]/((a + b*x)*(c + d*x)), x]", 2505);
	}

	// {2504}
	public void test0106() {
		check("Integrate[1/((a + b*x)*(c + d*x)*Log[e*((a + b*x)^n1/(c + d*x)^n1)^n]), x]", 2504);
	}

	// {2505}
	public void test0107() {
		check("Integrate[1/((a + b*x)*(c + d*x)*Log[e*((a + b*x)^n1/(c + d*x)^n1)^n]^2), x]", 2505);
	}

	// {2505}
	public void test0108() {
		check("Integrate[1/((a + b*x)*(c + d*x)*Log[e*((a + b*x)^n1/(c + d*x)^n1)^n]^3), x]", 2505);
	}

	// {2505}
	public void test0109() {
		check("Integrate[Log[e*((a + b*x)^n1/(c + d*x)^n1)^n]^p/((a + b*x)*(c + d*x)), x]", 2505);
	}

	// {2505}
	public void test0110() {
		check("Integrate[Log[e*((a + b*x)^n1/(c + d*x)^n1)^n]^p/((a*f + b*f*x)*(c*g + d*g*x)), x]", 2505);
	}

	// {2505}
	public void test0111() {
		check("Integrate[Log[e*((a + b*x)^n1/(c + d*x)^n1)^n]^p/(a*c*f + (b*c + a*d)*f*x + b*d*f*x^2), x]", 2505);
	}

	// {2504}
	public void test0112() {
		check("Integrate[1/((a + b*x)*(c + d*x)*Log[e*((a + b*x)^n1/(c + d*x)^n1)^n]), x]", 2504);
	}

	// {2504}
	public void test0113() {
		check("Integrate[1/((a*f + b*f*x)*(c*g + d*g*x)*Log[e*((a + b*x)^n1/(c + d*x)^n1)^n]), x]", 2504);
	}

	// {2504}
	public void test0114() {
		check("Integrate[1/((a*c*f + (b*c + a*d)*f*x + b*d*f*x^2)*Log[e*((a + b*x)^n1/(c + d*x)^n1)^n]), x]", 2504);
	}

	// {2507}
	public void test0115() {
		check("Integrate[Log[h*(f + g*x)^m]/((a + b*x)*(c + d*x)*Log[e*((a + b*x)/(c + d*x))^n]^2), x]", 2507);
	}

	// {2510}
	public void test0116() {
		check("Integrate[((a + b*x)^m*(c + d*x)^(-2 - m))/Log[e*((f*(a + b*x)^p)/(c + d*x)^p)^r], x]", 2510);
	}

	// {2493}
	public void test0117() {
		check("Integrate[1/((a*h + b*h*x)^2*Log[e*((f*(a + b*x)^p)/(c + d*x)^p)^r]), x]", 2493);
	}

	// {2510}
	public void test0118() {
		check("Integrate[(a + b*x)^3/((c + d*x)^5*Log[e*((a + b*x)/(c + d*x))^n]), x]", 2510);
	}

	// {2510}
	public void test0119() {
		check("Integrate[(a + b*x)^2/((c + d*x)^4*Log[e*((a + b*x)/(c + d*x))^n]), x]", 2510);
	}

	// {2510}
	public void test0120() {
		check("Integrate[(a + b*x)/((c + d*x)^3*Log[e*((a + b*x)/(c + d*x))^n]), x]", 2510);
	}

	// {2493}
	public void test0121() {
		check("Integrate[1/((c + d*x)^2*Log[e*((a + b*x)/(c + d*x))^n]), x]", 2493);
	}

	// {2504}
	public void test0122() {
		check("Integrate[1/((a + b*x)*(c + d*x)*Log[e*((a + b*x)/(c + d*x))^n]), x]", 2504);
	}

	// {2493}
	public void test0123() {
		check("Integrate[1/((a + b*x)^2*Log[e*((a + b*x)/(c + d*x))^n]), x]", 2493);
	}

	// {2510}
	public void test0124() {
		check("Integrate[(c + d*x)/((a + b*x)^3*Log[e*((a + b*x)/(c + d*x))^n]), x]", 2510);
	}

	// {2510}
	public void test0125() {
		check("Integrate[(c + d*x)^2/((a + b*x)^4*Log[e*((a + b*x)/(c + d*x))^n]), x]", 2510);
	}

	// {2505}
	public void test0126() {
		check("Integrate[Log[(c*x)/(a + b*x)]^2/(x*(a + b*x)), x]", 2505);
	}

	// {6610}
	public void test0127() {
		check("Integrate[PolyLog[2, 1 + (b*c - a*d)/(d*(a + b*x))]/((a + b*x)*(c + d*x)), x]", 6610);
	}

	// {2447}
	public void test0128() {
		check("Integrate[Log[(2*x*(d*Sqrt[-(e/d)] + e*x))/(d + e*x^2)]/(d + e*x^2), x]", 2447);
	}

	// {2447}
	public void test0129() {
		check("Integrate[Log[(-2*x*(d*Sqrt[-(e/d)] - e*x))/(d + e*x^2)]/(d + e*x^2), x]", 2447);
	}

	// {2447}
	public void test0130() {
		check("Integrate[Log[(2*x*((d*Sqrt[e])/Sqrt[-d] + e*x))/(d + e*x^2)]/(d + e*x^2), x]", 2447);
	}

	// {2447}
	public void test0131() {
		check("Integrate[Log[(-2*x*((d*Sqrt[e])/Sqrt[-d] - e*x))/(d + e*x^2)]/(d + e*x^2), x]", 2447);
	}

	// {2447}
	public void test0132() {
		check("Integrate[Log[(2*x*(Sqrt[d]*Sqrt[-e] + e*x))/(d + e*x^2)]/(d + e*x^2), x]", 2447);
	}

	// {2447}
	public void test0133() {
		check("Integrate[Log[(-2*x*(Sqrt[d]*Sqrt[-e] - e*x))/(d + e*x^2)]/(d + e*x^2), x]", 2447);
	}

	// {2521}
	public void test0134() {
		check("Integrate[(a + b*Log[c*Log[d*x^n]^p])/x, x]", 2521);
	}

	// {2521}
	public void test0135() {
		check("Integrate[Log[c*Log[d*x]^p]/x, x]", 2521);
	}

	// {2521}
	public void test0136() {
		check("Integrate[Log[c*Log[d*x^n]^p]/x, x]", 2521);
	}

	// {2537}
	public void test0137() {
		check("Integrate[Log[-1 + 4*x + 4*Sqrt[(-1 + x)*x]]/x, x]", 2537);
	}

	// {2505}
	public void test0138() {
		check("Integrate[Log[Sqrt[1 - a*x]/Sqrt[1 + a*x]]/(1 - a^2*x^2), x]", 2505);
	}

	// {2304}
	public void test0139() {
		check("Integrate[Log[x]/Sqrt[x], x]", 2304);
	}

	// {2304}
	public void test0140() {
		check("Integrate[x^(1/3)*Log[x], x]", 2304);
	}

	// {2303}
	public void test0141() {
		check("Integrate[(1 - Log[x])/x^2, x]", 2303);
	}

	// {2315}
	public void test0142() {
		check("Integrate[Log[x]/(-1 + x), x]", 2315);
	}

	// {2518}
	public void test0143() {
		check("Integrate[Log[1 + (I*Sqrt[1 - a*x])/Sqrt[1 + a*x]]/(1 - a^2*x^2), x]", 2518);
	}

	// {2518}
	public void test0144() {
		check("Integrate[Log[1 - (I*Sqrt[1 - a*x])/Sqrt[1 + a*x]]/(1 - a^2*x^2), x]", 2518);
	}
}
