package org.matheclipse.core.rubi;

public class RuleTestLogarithms extends AbstractRubiTestCase {

	public RuleTestLogarithms(String name) {
		super(name, false);
	}

	// {2315}
	public void test0001() {
		check("Integrate[Log[c*x]/(1 - c*x), x]", 2315, "PolyLog[2, 1 - c*x]/c");
	}

	// {2315}
	public void test0002() {
		check("Integrate[Log[x/c]/(c - x), x]", 2315, "PolyLog[2, 1 - x/c]");
	}

	// {2304}
	public void test0005() {
		check("Integrate[(f*x)^m*(a + b*Log[c*x^n]), x]", 2304, "-((b*n*(f*x)^(1 + m))/(f*(1 + m)^2)) + ((f*x)^(1 + m)*(a + b*Log[c*x^n]))/(f*(1 + m))");
	}

	// {2304}
	public void test0006() {
		check("Integrate[(f*x)^m*(a + b*Log[c*x^n]), x]", 2304, "-((b*n*(f*x)^(1 + m))/(f*(1 + m)^2)) + ((f*x)^(1 + m)*(a + b*Log[c*x^n]))/(f*(1 + m))");
	}

	// {2304}
	public void test0007() {
		check("Integrate[(f*x)^(-1 + m)*(a + b*Log[c*x^n]), x]", 2304, "-((b*n*(f*x)^m)/(f*m^2)) + ((f*x)^m*(a + b*Log[c*x^n]))/(f*m)");
	}

	// {2304}
	public void test0008() {
		check("Integrate[(f*x)^m*(a + b*Log[c*x^n]), x]", 2304, "-((b*n*(f*x)^(1 + m))/(f*(1 + m)^2)) + ((f*x)^(1 + m)*(a + b*Log[c*x^n]))/(f*(1 + m))");
	}

	// {2304}
	public void test0009() {
		check("Integrate[x^3*Log[c*x], x]", 2304, "-x^4/16 + (x^4*Log[c*x])/4");
	}

	// {2304}
	public void test0010() {
		check("Integrate[x^2*Log[c*x], x]", 2304, "-x^3/9 + (x^3*Log[c*x])/3");
	}

	// {2304}
	public void test0011() {
		check("Integrate[x*Log[c*x], x]", 2304, "-x^2/4 + (x^2*Log[c*x])/2");
	}

	// {2295}
	public void test0012() {
		check("Integrate[Log[c*x], x]", 2295, "-x + x*Log[c*x]");
	}

	// {2301}
	public void test0013() {
		check("Integrate[Log[c*x]/x, x]", 2301, "Log[c*x]^2/2");
	}

	// {2304}
	public void test0014() {
		check("Integrate[Log[c*x]/x^2, x]", 2304, "-x^(-1) - Log[c*x]/x");
	}

	// {2304}
	public void test0015() {
		check("Integrate[Log[c*x]/x^3, x]", 2304, "-1/(4*x^2) - Log[c*x]/(2*x^2)");
	}

	// {2298}
	public void test0016() {
		check("Integrate[Log[c*x]^(-1), x]", 2298, "LogIntegral[c*x]/c");
	}

	// {2304}
	public void test0017() {
		check("Integrate[x^3*(a + b*Log[c*x^n]), x]", 2304, "-(b*n*x^4)/16 + (x^4*(a + b*Log[c*x^n]))/4");
	}

	// {2304}
	public void test0018() {
		check("Integrate[x^2*(a + b*Log[c*x^n]), x]", 2304, "-(b*n*x^3)/9 + (x^3*(a + b*Log[c*x^n]))/3");
	}

	// {2304}
	public void test0019() {
		check("Integrate[x*(a + b*Log[c*x^n]), x]", 2304, "-(b*n*x^2)/4 + (x^2*(a + b*Log[c*x^n]))/2");
	}

	// {2301}
	public void test0020() {
		check("Integrate[(a + b*Log[c*x^n])/x, x]", 2301, "(a + b*Log[c*x^n])^2/(2*b*n)");
	}

	// {2304}
	public void test0021() {
		check("Integrate[(a + b*Log[c*x^n])/x^2, x]", 2304, "-((b*n)/x) - (a + b*Log[c*x^n])/x");
	}

	// {2304}
	public void test0022() {
		check("Integrate[(a + b*Log[c*x^n])/x^3, x]", 2304, "-(b*n)/(4*x^2) - (a + b*Log[c*x^n])/(2*x^2)");
	}

	// {2304}
	public void test0023() {
		check("Integrate[(d*x)^(5/2)*(a + b*Log[c*x^n]), x]", 2304, "(-4*b*n*(d*x)^(7/2))/(49*d) + (2*(d*x)^(7/2)*(a + b*Log[c*x^n]))/(7*d)");
	}

	// {2304}
	public void test0024() {
		check("Integrate[(d*x)^(3/2)*(a + b*Log[c*x^n]), x]", 2304, "(-4*b*n*(d*x)^(5/2))/(25*d) + (2*(d*x)^(5/2)*(a + b*Log[c*x^n]))/(5*d)");
	}

	// {2304}
	public void test0025() {
		check("Integrate[Sqrt[d*x]*(a + b*Log[c*x^n]), x]", 2304, "(-4*b*n*(d*x)^(3/2))/(9*d) + (2*(d*x)^(3/2)*(a + b*Log[c*x^n]))/(3*d)");
	}

	// {2304}
	public void test0026() {
		check("Integrate[(a + b*Log[c*x^n])/Sqrt[d*x], x]", 2304, "(-4*b*n*Sqrt[d*x])/d + (2*Sqrt[d*x]*(a + b*Log[c*x^n]))/d");
	}

	// {2304}
	public void test0027() {
		check("Integrate[(a + b*Log[c*x^n])/(d*x)^(3/2), x]", 2304, "(-4*b*n)/(d*Sqrt[d*x]) - (2*(a + b*Log[c*x^n]))/(d*Sqrt[d*x])");
	}

	// {2304}
	public void test0028() {
		check("Integrate[(a + b*Log[c*x^n])/(d*x)^(5/2), x]", 2304, "(-4*b*n)/(9*d*(d*x)^(3/2)) - (2*(a + b*Log[c*x^n]))/(3*d*(d*x)^(3/2))");
	}

	// {2303}
	public void test0029() {
		check("Integrate[(d*x)^m*(a + (a*(1 + m)*Log[c*x^n])/n), x]", 2303, "(a*(d*x)^(1 + m)*Log[c*x^n])/(d*n)");
	}

	// {2304}
	public void test0030() {
		check("Integrate[(d*x)^m*(a + b*Log[c*x^n]), x]", 2304, "-((b*n*(d*x)^(1 + m))/(d*(1 + m)^2)) + ((d*x)^(1 + m)*(a + b*Log[c*x^n]))/(d*(1 + m))");
	}

	// {2304}
	public void test0031() {
		check("Integrate[(d*x)^(-1 + n)*Log[c*x^n], x]", 2304, "-((d*x)^n/(d*n)) + ((d*x)^n*Log[c*x^n])/(d*n)");
	}

	// {2391}
	public void test0033() {
		check("Integrate[Log[1 + e*x]/x, x]", 2391, "-PolyLog[2, -(e*x)]");
	}

	// {2301}
	public void test0034() {
		check("Integrate[Log[e*x]/x, x]", 2301, "Log[e*x]^2/2");
	}

	// {2301}
	public void test0035() {
		check("Integrate[(a + b*Log[e*x])/x, x]", 2301, "(a + b*Log[e*x])^2/(2*b)");
	}

	// {2447}
	public void test0045() {
		check("Integrate[Log[(a*(1 - c) + b*(1 + c)*x)/(a + b*x)]/(a^2 - b^2*x^2), x]", 2447, "PolyLog[2, 1 - (a*(1 - c) + b*(1 + c)*x)/(a + b*x)]/(2*a*b)");
	}

	// {2447}
	public void test0046() {
		check("Integrate[Log[1 - (c*(a - b*x))/(a + b*x)]/(a^2 - b^2*x^2), x]", 2447, "PolyLog[2, (c*(a - b*x))/(a + b*x)]/(2*a*b)");
	}

	// {2435}
	public void test0047() {
		check("Integrate[(Log[a + b*x]*Log[c + d*x])/x, x]", 2435, "Log[-((b*x)/a)]*Log[a + b*x]*Log[c + d*x] + ((Log[-((b*x)/a)] + Log[(b*c - a*d)/(b*(c + d*x))] - Log[-(((b*c - a*d)*x)/(a*(c + d*x)))])*Log[(a*(c + d*x))/(c*(a + b*x))]^2)/2 - ((Log[-((b*x)/a)] - Log[-((d*x)/c)])*(Log[a + b*x] + Log[(a*(c + d*x))/(c*(a + b*x))])^2)/2 + (Log[c + d*x] - Log[(a*(c + d*x))/(c*(a + b*x))])*PolyLog[2, 1 + (b*x)/a] + Log[(a*(c + d*x))/(c*(a + b*x))]*PolyLog[2, (c*(a + b*x))/(a*(c + d*x))] - Log[(a*(c + d*x))/(c*(a + b*x))]*PolyLog[2, (d*(a + b*x))/(b*(c + d*x))] + (Log[a + b*x] + Log[(a*(c + d*x))/(c*(a + b*x))])*PolyLog[2, 1 + (d*x)/c] - PolyLog[3, 1 + (b*x)/a] + PolyLog[3, (c*(a + b*x))/(a*(c + d*x))] - PolyLog[3, (d*(a + b*x))/(b*(c + d*x))] - PolyLog[3, 1 + (d*x)/c]");
	}

	// {2391}
	public void test0048() {
		check("Integrate[Log[1 + b/x]/x, x]", 2391, "PolyLog[2, -(b/x)]");
	}

	// {2391}
	public void test0052() {
		check("Integrate[Log[1 + e*x^n]/x, x]", 2391, "-(PolyLog[2, -(e*x^n)]/n)");
	}

	// {2447}
	public void test0098() {
		check("Integrate[Log[1 + (a + b*x)^(-1)]/(a + b*x), x]", 2447, "PolyLog[2, -(a + b*x)^(-1)]/b");
	}

	// {2447}
	public void test0099() {
		check("Integrate[Log[1 - (a + b*x)^(-1)]/(a + b*x), x]", 2447, "PolyLog[2, (a + b*x)^(-1)]/b");
	}

	// {2505}
	public void test0100() {
		check("Integrate[Log[e*((f*(a + b*x))/(c + d*x))^r]/((a + b*x)*(c + d*x)), x]", 2505, "Log[e*((f*(a + b*x))/(c + d*x))^r]^2/(2*(b*c - a*d)*r)");
	}

	// {2505}
	public void test0101() {
		check("Integrate[Log[e*((a + b*x)/(c + d*x))^n]^p/((a + b*x)*(c + d*x)), x]", 2505, "Log[e*((a + b*x)/(c + d*x))^n]^(1 + p)/((b*c - a*d)*n*(1 + p))");
	}

	// {2505}
	public void test0102() {
		check("Integrate[Log[e*((a + b*x)/(c + d*x))^n]^p/(a*c + (b*c + a*d)*x + b*d*x^2), x]", 2505, "Log[e*((a + b*x)/(c + d*x))^n]^(1 + p)/((b*c - a*d)*n*(1 + p))");
	}

	// {2505}
	public void test0103() {
		check("Integrate[Log[e*((a + b*x)^n1/(c + d*x)^n1)^n]^3/((a + b*x)*(c + d*x)), x]", 2505, "Log[e*((a + b*x)^n1/(c + d*x)^n1)^n]^4/(4*(b*c - a*d)*n*n1)");
	}

	// {2505}
	public void test0104() {
		check("Integrate[Log[e*((a + b*x)^n1/(c + d*x)^n1)^n]^2/((a + b*x)*(c + d*x)), x]", 2505, "Log[e*((a + b*x)^n1/(c + d*x)^n1)^n]^3/(3*(b*c - a*d)*n*n1)");
	}

	// {2505}
	public void test0105() {
		check("Integrate[Log[e*((a + b*x)^n1/(c + d*x)^n1)^n]/((a + b*x)*(c + d*x)), x]", 2505, "Log[e*((a + b*x)^n1/(c + d*x)^n1)^n]^2/(2*(b*c - a*d)*n*n1)");
	}

	// {2504}
	public void test0106() {
		check("Integrate[1/((a + b*x)*(c + d*x)*Log[e*((a + b*x)^n1/(c + d*x)^n1)^n]), x]", 2504, "Log[Log[e*((a + b*x)^n1/(c + d*x)^n1)^n]]/((b*c - a*d)*n*n1)");
	}

	// {2505}
	public void test0107() {
		check("Integrate[1/((a + b*x)*(c + d*x)*Log[e*((a + b*x)^n1/(c + d*x)^n1)^n]^2), x]", 2505, "-(1/((b*c - a*d)*n*n1*Log[e*((a + b*x)^n1/(c + d*x)^n1)^n]))");
	}

	// {2505}
	public void test0108() {
		check("Integrate[1/((a + b*x)*(c + d*x)*Log[e*((a + b*x)^n1/(c + d*x)^n1)^n]^3), x]", 2505, "-1/(2*(b*c - a*d)*n*n1*Log[e*((a + b*x)^n1/(c + d*x)^n1)^n]^2)");
	}

	// {2505}
	public void test0109() {
		check("Integrate[Log[e*((a + b*x)^n1/(c + d*x)^n1)^n]^p/((a + b*x)*(c + d*x)), x]", 2505, "Log[e*((a + b*x)^n1/(c + d*x)^n1)^n]^(1 + p)/((b*c - a*d)*n*n1*(1 + p))");
	}

	// {2505}
	public void test0110() {
		check("Integrate[Log[e*((a + b*x)^n1/(c + d*x)^n1)^n]^p/((a*f + b*f*x)*(c*g + d*g*x)), x]", 2505, "Log[e*((a + b*x)^n1/(c + d*x)^n1)^n]^(1 + p)/((b*c - a*d)*f*g*n*n1*(1 + p))");
	}

	// {2505}
	public void test0111() {
		check("Integrate[Log[e*((a + b*x)^n1/(c + d*x)^n1)^n]^p/(a*c*f + (b*c + a*d)*f*x + b*d*f*x^2), x]", 2505, "Log[e*((a + b*x)^n1/(c + d*x)^n1)^n]^(1 + p)/((b*c - a*d)*f*n*n1*(1 + p))");
	}

	// {2504}
	public void test0112() {
		check("Integrate[1/((a + b*x)*(c + d*x)*Log[e*((a + b*x)^n1/(c + d*x)^n1)^n]), x]", 2504, "Log[Log[e*((a + b*x)^n1/(c + d*x)^n1)^n]]/((b*c - a*d)*n*n1)");
	}

	// {2504}
	public void test0113() {
		check("Integrate[1/((a*f + b*f*x)*(c*g + d*g*x)*Log[e*((a + b*x)^n1/(c + d*x)^n1)^n]), x]", 2504, "Log[Log[e*((a + b*x)^n1/(c + d*x)^n1)^n]]/((b*c - a*d)*f*g*n*n1)");
	}

	// {2504}
	public void test0114() {
		check("Integrate[1/((a*c*f + (b*c + a*d)*f*x + b*d*f*x^2)*Log[e*((a + b*x)^n1/(c + d*x)^n1)^n]), x]", 2504, "Log[Log[e*((a + b*x)^n1/(c + d*x)^n1)^n]]/((b*c - a*d)*f*n*n1)");
	}

	// {2510}
	public void test0116() {
		check("Integrate[((a + b*x)^m*(c + d*x)^(-2 - m))/Log[e*((f*(a + b*x)^p)/(c + d*x)^p)^r], x]", 2510, "((a + b*x)^(1 + m)*(c + d*x)^(-1 - m)*ExpIntegralEi[((1 + m)*Log[e*((f*(a + b*x)^p)/(c + d*x)^p)^r])/(p*r)])/((b*c - a*d)*p*r*(e*((f*(a + b*x)^p)/(c + d*x)^p)^r)^((1 + m)/(p*r)))");
	}

	// {2493}
	public void test0117() {
		check("Integrate[1/((a*h + b*h*x)^2*Log[e*((f*(a + b*x)^p)/(c + d*x)^p)^r]), x]", 2493, "((c + d*x)*(e*((f*(a + b*x)^p)/(c + d*x)^p)^r)^(1/(p*r))*ExpIntegralEi[-(Log[e*((f*(a + b*x)^p)/(c + d*x)^p)^r]/(p*r))])/((b*c - a*d)*h^2*p*r*(a + b*x))");
	}

	// {2510}
	public void test0118() {
		check("Integrate[(a + b*x)^3/((c + d*x)^5*Log[e*((a + b*x)/(c + d*x))^n]), x]", 2510, "((a + b*x)^4*ExpIntegralEi[(4*Log[e*((a + b*x)/(c + d*x))^n])/n])/((b*c - a*d)*n*(e*((a + b*x)/(c + d*x))^n)^(4/n)*(c + d*x)^4)");
	}

	// {2510}
	public void test0119() {
		check("Integrate[(a + b*x)^2/((c + d*x)^4*Log[e*((a + b*x)/(c + d*x))^n]), x]", 2510, "((a + b*x)^3*ExpIntegralEi[(3*Log[e*((a + b*x)/(c + d*x))^n])/n])/((b*c - a*d)*n*(e*((a + b*x)/(c + d*x))^n)^(3/n)*(c + d*x)^3)");
	}

	// {2510}
	public void test0120() {
		check("Integrate[(a + b*x)/((c + d*x)^3*Log[e*((a + b*x)/(c + d*x))^n]), x]", 2510, "((a + b*x)^2*ExpIntegralEi[(2*Log[e*((a + b*x)/(c + d*x))^n])/n])/((b*c - a*d)*n*(e*((a + b*x)/(c + d*x))^n)^(2/n)*(c + d*x)^2)");
	}

	// {2493}
	public void test0121() {
		check("Integrate[1/((c + d*x)^2*Log[e*((a + b*x)/(c + d*x))^n]), x]", 2493, "((a + b*x)*ExpIntegralEi[Log[e*((a + b*x)/(c + d*x))^n]/n])/((b*c - a*d)*n*(e*((a + b*x)/(c + d*x))^n)^n^(-1)*(c + d*x))");
	}

	// {2504}
	public void test0122() {
		check("Integrate[1/((a + b*x)*(c + d*x)*Log[e*((a + b*x)/(c + d*x))^n]), x]", 2504, "Log[Log[e*((a + b*x)/(c + d*x))^n]]/((b*c - a*d)*n)");
	}

	// {2493}
	public void test0123() {
		check("Integrate[1/((a + b*x)^2*Log[e*((a + b*x)/(c + d*x))^n]), x]", 2493, "((e*((a + b*x)/(c + d*x))^n)^n^(-1)*(c + d*x)*ExpIntegralEi[-(Log[e*((a + b*x)/(c + d*x))^n]/n)])/((b*c - a*d)*n*(a + b*x))");
	}

	// {2510}
	public void test0124() {
		check("Integrate[(c + d*x)/((a + b*x)^3*Log[e*((a + b*x)/(c + d*x))^n]), x]", 2510, "((e*((a + b*x)/(c + d*x))^n)^(2/n)*(c + d*x)^2*ExpIntegralEi[(-2*Log[e*((a + b*x)/(c + d*x))^n])/n])/((b*c - a*d)*n*(a + b*x)^2)");
	}

	// {2510}
	public void test0125() {
		check("Integrate[(c + d*x)^2/((a + b*x)^4*Log[e*((a + b*x)/(c + d*x))^n]), x]", 2510, "((e*((a + b*x)/(c + d*x))^n)^(3/n)*(c + d*x)^3*ExpIntegralEi[(-3*Log[e*((a + b*x)/(c + d*x))^n])/n])/((b*c - a*d)*n*(a + b*x)^3)");
	}

	// {2505}
	public void test0126() {
		check("Integrate[Log[(c*x)/(a + b*x)]^2/(x*(a + b*x)), x]", 2505, "Log[(c*x)/(a + b*x)]^3/(3*a)");
	}

	// {6610}
	public void test0127() {
		check("Integrate[PolyLog[2, 1 + (b*c - a*d)/(d*(a + b*x))]/((a + b*x)*(c + d*x)), x]", 6610, "-(PolyLog[3, 1 + (b*c - a*d)/(d*(a + b*x))]/(b*c - a*d))");
	}

	// {2447}
	public void test0128() {
		check("Integrate[Log[(2*x*(d*Sqrt[-(e/d)] + e*x))/(d + e*x^2)]/(d + e*x^2), x]", 2447, "-(Sqrt[-(e/d)]*PolyLog[2, 1 - (2*x*(d*Sqrt[-(e/d)] + e*x))/(d + e*x^2)])/(2*e)");
	}

	// {2447}
	public void test0129() {
		check("Integrate[Log[(-2*x*(d*Sqrt[-(e/d)] - e*x))/(d + e*x^2)]/(d + e*x^2), x]", 2447, "(Sqrt[-(e/d)]*PolyLog[2, 1 + (2*x*(d*Sqrt[-(e/d)] - e*x))/(d + e*x^2)])/(2*e)");
	}

	// {2447}
	public void test0130() {
		check("Integrate[Log[(2*x*((d*Sqrt[e])/Sqrt[-d] + e*x))/(d + e*x^2)]/(d + e*x^2), x]", 2447, "-PolyLog[2, 1 + (2*Sqrt[e]*x*(Sqrt[-d] - Sqrt[e]*x))/(d + e*x^2)]/(2*Sqrt[-d]*Sqrt[e])");
	}

	// {2447}
	public void test0131() {
		check("Integrate[Log[(-2*x*((d*Sqrt[e])/Sqrt[-d] - e*x))/(d + e*x^2)]/(d + e*x^2), x]", 2447, "PolyLog[2, 1 - (2*Sqrt[e]*x*(Sqrt[-d] + Sqrt[e]*x))/(d + e*x^2)]/(2*Sqrt[-d]*Sqrt[e])");
	}

	// {2447}
	public void test0132() {
		check("Integrate[Log[(2*x*(Sqrt[d]*Sqrt[-e] + e*x))/(d + e*x^2)]/(d + e*x^2), x]", 2447, "PolyLog[2, 1 - (2*x*(Sqrt[d]*Sqrt[-e] + e*x))/(d + e*x^2)]/(2*Sqrt[d]*Sqrt[-e])");
	}

	// {2447}
	public void test0133() {
		check("Integrate[Log[(-2*x*(Sqrt[d]*Sqrt[-e] - e*x))/(d + e*x^2)]/(d + e*x^2), x]", 2447, "-PolyLog[2, 1 + (2*x*(Sqrt[d]*Sqrt[-e] - e*x))/(d + e*x^2)]/(2*Sqrt[d]*Sqrt[-e])");
	}

	// {2521}
	public void test0134() {
		check("Integrate[(a + b*Log[c*Log[d*x^n]^p])/x, x]", 2521, "-(b*p*Log[x]) + (Log[d*x^n]*(a + b*Log[c*Log[d*x^n]^p]))/n");
	}

	// {2521}
	public void test0135() {
		check("Integrate[Log[c*Log[d*x]^p]/x, x]", 2521, "-(p*Log[x]) + Log[d*x]*Log[c*Log[d*x]^p]");
	}

	// {2521}
	public void test0136() {
		check("Integrate[Log[c*Log[d*x^n]^p]/x, x]", 2521, "-(p*Log[x]) + (Log[d*x^n]*Log[c*Log[d*x^n]^p])/n");
	}

	// {2505}
	public void test0138() {
		check("Integrate[Log[Sqrt[1 - a*x]/Sqrt[1 + a*x]]/(1 - a^2*x^2), x]", 2505, "-Log[Sqrt[1 - a*x]/Sqrt[1 + a*x]]^2/(2*a)");
	}

	// {2304}
	public void test0139() {
		check("Integrate[Log[x]/Sqrt[x], x]", 2304, "-4*Sqrt[x] + 2*Sqrt[x]*Log[x]");
	}

	// {2304}
	public void test0140() {
		check("Integrate[x^(1/3)*Log[x], x]", 2304, "(-9*x^(4/3))/16 + (3*x^(4/3)*Log[x])/4");
	}

	// {2303}
	public void test0141() {
		check("Integrate[(1 - Log[x])/x^2, x]", 2303, "Log[x]/x");
	}

	// {2315}
	public void test0142() {
		check("Integrate[Log[x]/(-1 + x), x]", 2315, "-PolyLog[2, 1 - x]");
	}

	// {2518}
	public void test0143() {
		check("Integrate[Log[1 + (I*Sqrt[1 - a*x])/Sqrt[1 + a*x]]/(1 - a^2*x^2), x]", 2518, "PolyLog[2, ((-I)*Sqrt[1 - a*x])/Sqrt[1 + a*x]]/a");
	}

	// {2518}
	public void test0144() {
		check("Integrate[Log[1 - (I*Sqrt[1 - a*x])/Sqrt[1 + a*x]]/(1 - a^2*x^2), x]", 2518, "PolyLog[2, (I*Sqrt[1 - a*x])/Sqrt[1 + a*x]]/a");
	}
}
