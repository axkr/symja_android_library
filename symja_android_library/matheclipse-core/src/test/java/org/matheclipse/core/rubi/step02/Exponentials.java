package org.matheclipse.core.rubi.step02;

public class Exponentials extends AbstractRubiTestCase {

	static boolean init = true;

	public Exponentials(String name) {
		super(name, false);
	}

	@Override
	protected void setUp() {
		try {
			super.setUp();
			if (init) {
				System.out.println("Exponentials");
				init = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// {2207, 2225}
	public void test0001() {
		check(//
				"Integrate[F^(c*(a + b*x))*(d + e*x), x]", //
				"-((e*F^(c*(a + b*x)))/(b^2*c^2*Log[F]^2)) + (F^(c*(a + b*x))*(d + e*x))/(b*c*Log[F])", //
				2207, 2225);
	}

	// {2208, 2209}
	public void test0002() {
		check(//
				"Integrate[F^(c*(a + b*x))/(d + e*x)^2, x]", //
				"-(F^(c*(a + b*x))/(e*(d + e*x))) + (b*c*F^(c*(a - (b*d)/e))*ExpIntegralEi[(b*c*(d + e*x)*Log[F])/e]*Log[F])/e^2", //
				2208, 2209);
	}

	// {1973, 2212}
	public void test0003() {
		check(//
				"Integrate[F^(c*(a + b*x))*((d + e*x)^n)^m, x]", //
				"(F^(c*(a - (b*d)/e))*((d + e*x)^n)^m*Gamma[1 + m*n, -((b*c*(d + e*x)*Log[F])/e)])/(b*c*Log[F]*(-((b*c*(d + e*x)*Log[F])/e))^(m*n))", //
				1973, 2212);
	}

	// {2219, 2212}
	public void test0004() {
		check(//
				"Integrate[F^(c*(a + b*x))*(d^4 + 4*d^3*e*x + 6*d^2*e^2*x^2 + 4*d*e^3*x^3 + e^4*x^4)^m, x]", //
				"(F^(c*(a - (b*d)/e))*((d + e*x)^4)^m*Gamma[1 + 4*m, -((b*c*(d + e*x)*Log[F])/e)])/(b*c*Log[F]*(-((b*c*(d + e*x)*Log[F])/e))^(4*m))", //
				2219, 2212);
	}

	// {2219, 2212}
	public void test0005() {
		check(//
				"Integrate[F^(c*(a + b*x))*(d^3 + 3*d^2*e*x + 3*d*e^2*x^2 + e^3*x^3)^m, x]", //
				"(F^(c*(a - (b*d)/e))*((d + e*x)^3)^m*Gamma[1 + 3*m, -((b*c*(d + e*x)*Log[F])/e)])/(b*c*Log[F]*(-((b*c*(d + e*x)*Log[F])/e))^(3*m))", //
				2219, 2212);
	}

	// {2219, 2212}
	public void test0006() {
		check(//
				"Integrate[F^(c*(a + b*x))*(d^2 + 2*d*e*x + e^2*x^2)^m, x]", //
				"(F^(c*(a - (b*d)/e))*((d + e*x)^2)^m*Gamma[1 + 2*m, -((b*c*(d + e*x)*Log[F])/e)])/(b*c*Log[F]*(-((b*c*(d + e*x)*Log[F])/e))^(2*m))", //
				2219, 2212);
	}

	// {2219, 2212}
	public void test0007() {
		check(//
				"Integrate[F^(c*(a + b*x))/(d^2 + 2*d*e*x + e^2*x^2)^m, x]", //
				"(F^(c*(a - (b*d)/e))*Gamma[1 - 2*m, -((b*c*(d + e*x)*Log[F])/e)]*(-((b*c*(d + e*x)*Log[F])/e))^(2*m))/(b*c*((d + e*x)^2)^m*Log[F])", //
				2219, 2212);
	}

	// {2219, 2212}
	public void test0008() {
		check(//
				"Integrate[F^(c*(a + b*x))/(d^3 + 3*d^2*e*x + 3*d*e^2*x^2 + e^3*x^3)^m, x]", //
				"(F^(c*(a - (b*d)/e))*Gamma[1 - 3*m, -((b*c*(d + e*x)*Log[F])/e)]*(-((b*c*(d + e*x)*Log[F])/e))^(3*m))/(b*c*((d + e*x)^3)^m*Log[F])", //
				2219, 2212);
	}

	// {2211, 2235}
	public void test0009() {
		check(//
				"Integrate[F^(a + b*x)/Sqrt[x], x]", //
				"(F^a*Sqrt[Pi]*Erfi[Sqrt[b]*Sqrt[x]*Sqrt[Log[F]]])/(Sqrt[b]*Sqrt[Log[F]])", //
				2211, 2235);
	}

	// {2211, 2235}
	public void test0010() {
		check(//
				"Integrate[F^(c*(a + b*x))/Sqrt[d + e*x], x]", //
				"(F^(c*(a - (b*d)/e))*Sqrt[Pi]*Erfi[(Sqrt[b]*Sqrt[c]*Sqrt[d + e*x]*Sqrt[Log[F]])/Sqrt[e]])/(Sqrt[b]*Sqrt[c]*Sqrt[e]*Sqrt[Log[F]])", //
				2211, 2235);
	}

	// {2213, 2212}
	public void test0011() {
		check(//
				"Integrate[(F^(c*(a + b*x)))^n*(d + e*x)^(4/3), x]", //
				"-((e*F^(c*(a - (b*d)/e)*n - c*n*(a + b*x))*(F^(c*(a + b*x)))^n*(d + e*x)^(1/3)*Gamma[7/3, -((b*c*n*(d + e*x)*Log[F])/e)])/(b^2*c^2*n^2*Log[F]^2*(-((b*c*n*(d + e*x)*Log[F])/e))^(1/3)))", //
				2213, 2212);
	}

	// {2207, 2225}
	public void test0012() {
		check(//
				"Integrate[F^(c*(a + b*x))*(d + e*x), x]", //
				"-((e*F^(c*(a + b*x)))/(b^2*c^2*Log[F]^2)) + (F^(c*(a + b*x))*(d + e*x))/(b*c*Log[F])", //
				2207, 2225);
	}

	// {2207, 2225}
	public void test0013() {
		check(//
				"Integrate[Sqrt[E^(a + b*x)]*x, x]", //
				"(-4*Sqrt[E^(a + b*x)])/b^2 + (2*Sqrt[E^(a + b*x)]*x)/b", //
				2207, 2225);
	}

	// {2213, 2209}
	public void test0014() {
		check(//
				"Integrate[Sqrt[E^(a + b*x)]/x, x]", //
				"(Sqrt[E^(a + b*x)]*ExpIntegralEi[(b*x)/2])/E^((b*x)/2)", //
				2213, 2209);
	}

	// {2225}
	public void test0015() {
		check(//
				"Integrate[a + b*(F^(g*(e + f*x)))^n, x]", //
				"a*x + (b*(F^(g*(e + f*x)))^n)/(f*g*n*Log[F])", //
				2225);
	}

	// {2278, 31}
	public void test0016() {
		check(//
				"Integrate[F^(c + d*x)/(a + b*F^(c + d*x)), x]", //
				"Log[a + b*F^(c + d*x)]/(b*d*Log[F])", //
				2278, 31);
	}

	// {2278, 32}
	public void test0017() {
		check(//
				"Integrate[F^(c + d*x)/(a + b*F^(c + d*x))^2, x]", //
				"-(1/(b*d*(a + b*F^(c + d*x))*Log[F]))", //
				2278, 32);
	}

	// {2278, 32}
	public void test0018() {
		check(//
				"Integrate[F^(c + d*x)/(a + b*F^(c + d*x))^3, x]", //
				"-1/(2*b*d*(a + b*F^(c + d*x))^2*Log[F])", //
				2278, 32);
	}

	// {2278, 31}
	public void test0019() {
		check(//
				"Integrate[E^x/(4 + 6*E^x), x]", //
				"Log[2 + 3*E^x]/6", //
				2278, 31);
	}

	// {2278, 31}
	public void test0020() {
		check(//
				"Integrate[E^x/(a + b*E^x), x]", //
				"Log[a + b*E^x]/b", //
				2278, 31);
	}

	// {2278, 31}
	public void test0021() {
		check(//
				"Integrate[E^(c + d*x)/(a + b*E^(c + d*x)), x]", //
				"Log[a + b*E^(c + d*x)]/(b*d)", //
				2278, 31);
	}

	// {2278, 32}
	public void test0022() {
		check(//
				"Integrate[E^x*(a + b*E^x)^n, x]", //
				"(a + b*E^x)^(1 + n)/(b*(1 + n))", //
				2278, 32);
	}

	// {2278, 32}
	public void test0023() {
		check(//
				"Integrate[E^(c + d*x)*(a + b*E^(c + d*x))^n, x]", //
				"(a + b*E^(c + d*x))^(1 + n)/(b*d*(1 + n))", //
				2278, 32);
	}

	// {2278, 31}
	public void test0024() {
		check(//
				"Integrate[F^x/(a + b*F^x), x]", //
				"Log[a + b*F^x]/(b*Log[F])", //
				2278, 31);
	}

	// {2278, 31}
	public void test0025() {
		check(//
				"Integrate[F^(c + d*x)/(a + b*F^(c + d*x)), x]", //
				"Log[a + b*F^(c + d*x)]/(b*d*Log[F])", //
				2278, 31);
	}

	// {2278, 32}
	public void test0026() {
		check(//
				"Integrate[F^x*(a + b*F^x)^n, x]", //
				"(a + b*F^x)^(1 + n)/(b*(1 + n)*Log[F])", //
				2278, 32);
	}

	// {2278, 32}
	public void test0027() {
		check(//
				"Integrate[F^(c + d*x)*(a + b*F^(c + d*x))^n, x]", //
				"(a + b*F^(c + d*x))^(1 + n)/(b*d*(1 + n)*Log[F])", //
				2278, 32);
	}

	// {2278, 32}
	public void test0028() {
		check(//
				"Integrate[(E^x)^n*(a + b*(E^x)^n)^p, x]", //
				"(a + b*(E^x)^n)^(1 + p)/(b*n*(1 + p))", //
				2278, 32);
	}

	// {2278, 32}
	public void test0029() {
		check(//
				"Integrate[(F^(e*(c + d*x)))^n*(a + b*(F^(e*(c + d*x)))^n)^p, x]", //
				"(a + b*(F^(e*(c + d*x)))^n)^(1 + p)/(b*d*e*n*(1 + p)*Log[F])", //
				2278, 32);
	}

	// {2280, 37}
	public void test0030() {
		check(//
				"Integrate[E^(2*x)/(a + b*E^x)^3, x]", //
				"E^(2*x)/(2*a*(a + b*E^x)^2)", //
				2280, 37);
	}

	// {2280, 37}
	public void test0031() {
		check(//
				"Integrate[E^(4*x)/(a + b*E^(2*x))^3, x]", //
				"E^(4*x)/(4*a*(a + b*E^(2*x))^2)", //
				2280, 37);
	}

	// {2281, 211}
	public void test0032() {
		check(//
				"Integrate[f^(a + b*x)/(c + d*f^(e + 2*b*x)), x]", //
				"(f^(a - e/2)*ArcTan[(Sqrt[d]*f^(e/2 + b*x))/Sqrt[c]])/(b*Sqrt[c]*Sqrt[d]*Log[f])", //
				2281, 211);
	}

	// {2281, 209}
	public void test0033() {
		check(//
				"Integrate[E^x/(1 + E^(2*x)), x]", //
				"ArcTan[E^x]", //
				2281, 209);
	}

	// {2281, 212}
	public void test0034() {
		check(//
				"Integrate[E^x/(1 - E^(2*x)), x]", //
				"ArcTanh[E^x]", //
				2281, 212);
	}

	// {2281, 211}
	public void test0035() {
		check(//
				"Integrate[f^x/(a + b*f^(2*x)), x]", //
				"ArcTan[(Sqrt[b]*f^x)/Sqrt[a]]/(Sqrt[a]*Sqrt[b]*Log[f])", //
				2281, 211);
	}

	// {2320, 211}
	public void test0036() {
		check(//
				"Integrate[(b/f^x + a*f^x)^(-1), x]", //
				"ArcTan[(Sqrt[a]*f^x)/Sqrt[b]]/(Sqrt[a]*Sqrt[b]*Log[f])", //
				2320, 211);
	}

	// {2320, 267}
	public void test0037() {
		check(//
				"Integrate[(b/f^x + a*f^x)^(-2), x]", //
				"-1/(2*a*(b + a*f^(2*x))*Log[f])", //
				2320, 267);
	}

	// {2284, 2283}
	public void test0038() {
		check(//
				"Integrate[F^(e*(c + d*x))*(a + b*G^(h*(f + g*x)))^n, x]", //
				"(F^(e*(c + d*x))*(a + b*G^(h*(f + g*x)))^n*Hypergeometric2F1[-n, (d*e*Log[F])/(g*h*Log[G]), 1 + (d*e*Log[F])/(g*h*Log[G]), -((b*G^(h*(f + g*x)))/a)])/(d*e*(1 + (b*G^(h*(f + g*x)))/a)^n*Log[F])", //
				2284, 2283);
	}

	// {2288, 2283}
	public void test0039() {
		fSeconds=20;
		check(//
				"Integrate[(F^(e*(c + d*x))*H^(t*(r + s*x)))/(a + b*F^(e*(c + d*x))), x]", //
				"(H^(t*(r + s*x))*Hypergeometric2F1[1, -((s*t*Log[H])/(d*e*Log[F])), 1 - (s*t*Log[H])/(d*e*Log[F]), -(a/(b*F^(e*(c + d*x))))])/(b*s*t*Log[H])", //
				2288, 2283);
	}

	// {2288, 2283}
	public void test0040() {
		fSeconds=20;
		check(//
				"Integrate[(F^(e*(f + d*x))*H^(t*(r + s*x)))/(a + b*F^(e*(c + d*x))), x]", //
				"(H^(t*(r + s*x))*Hypergeometric2F1[1, -((s*t*Log[H])/(d*e*Log[F])), 1 - (s*t*Log[H])/(d*e*Log[F]), -(a/(b*F^(e*(c + d*x))))])/(b*F^(e*(c - f))*s*t*Log[H])", //
				2288, 2283);
	}

	// {2243, 2240}
	public void test0041() {
		check(//
				"Integrate[f^(a + b*x^2)*x^3, x]", //
				"-f^(a + b*x^2)/(2*b^2*Log[f]^2) + (f^(a + b*x^2)*x^2)/(2*b*Log[f])", //
				2243, 2240);
	}

	// {2245, 2241}
	public void test0042() {
		check(//
				"Integrate[f^(a + b*x^2)/x^3, x]", //
				"-f^(a + b*x^2)/(2*x^2) + (b*f^a*ExpIntegralEi[b*x^2*Log[f]]*Log[f])/2", //
				2245, 2241);
	}

	// {2243, 2235}
	public void test0043() {
		check(//
				"Integrate[f^(a + b*x^2)*x^2, x]", //
				"-(f^a*Sqrt[Pi]*Erfi[Sqrt[b]*x*Sqrt[Log[f]]])/(4*b^(3/2)*Log[f]^(3/2)) + (f^(a + b*x^2)*x)/(2*b*Log[f])", //
				2243, 2235);
	}

	// {2245, 2235}
	public void test0044() {
		check(//
				"Integrate[f^(a + b*x^2)/x^2, x]", //
				"-(f^(a + b*x^2)/x) + Sqrt[b]*f^a*Sqrt[Pi]*Erfi[Sqrt[b]*x*Sqrt[Log[f]]]*Sqrt[Log[f]]", //
				2245, 2235);
	}

	// {2243, 2240}
	public void test0045() {
		check(//
				"Integrate[f^(a + b*x^3)*x^5, x]", //
				"-f^(a + b*x^3)/(3*b^2*Log[f]^2) + (f^(a + b*x^3)*x^3)/(3*b*Log[f])", //
				2243, 2240);
	}

	// {2245, 2241}
	public void test0046() {
		check(//
				"Integrate[f^(a + b*x^3)/x^4, x]", //
				"-f^(a + b*x^3)/(3*x^3) + (b*f^a*ExpIntegralEi[b*x^3*Log[f]]*Log[f])/3", //
				2245, 2241);
	}

	// {2237, 2241}
	public void test0047() {
		check(//
				"Integrate[f^(a + b/x), x]", //
				"f^(a + b/x)*x - b*f^a*ExpIntegralEi[(b*Log[f])/x]*Log[f]", //
				2237, 2241);
	}

	// {2243, 2240}
	public void test0048() {
		check(//
				"Integrate[f^(a + b/x)/x^3, x]", //
				"f^(a + b/x)/(b^2*Log[f]^2) - f^(a + b/x)/(b*x*Log[f])", //
				2243, 2240);
	}

	// {2245, 2241}
	public void test0049() {
		check(//
				"Integrate[f^(a + b/x^2)*x, x]", //
				"(f^(a + b/x^2)*x^2)/2 - (b*f^a*ExpIntegralEi[(b*Log[f])/x^2]*Log[f])/2", //
				2245, 2241);
	}

	// {2243, 2240}
	public void test0050() {
		check(//
				"Integrate[f^(a + b/x^2)/x^5, x]", //
				"f^(a + b/x^2)/(2*b^2*Log[f]^2) - f^(a + b/x^2)/(2*b*x^2*Log[f])", //
				2243, 2240);
	}

	// {2242, 2235}
	public void test0051() {
		check(//
				"Integrate[f^(a + b/x^2)/x^2, x]", //
				"-(f^a*Sqrt[Pi]*Erfi[(Sqrt[b]*Sqrt[Log[f]])/x])/(2*Sqrt[b]*Sqrt[Log[f]])", //
				2242, 2235);
	}

	// {2245, 2241}
	public void test0052() {
		check(//
				"Integrate[f^(a + b/x^3)*x^2, x]", //
				"(f^(a + b/x^3)*x^3)/3 - (b*f^a*ExpIntegralEi[(b*Log[f])/x^3]*Log[f])/3", //
				2245, 2241);
	}

	// {2243, 2240}
	public void test0053() {
		check(//
				"Integrate[f^(a + b/x^3)/x^7, x]", //
				"f^(a + b/x^3)/(3*b^2*Log[f]^2) - f^(a + b/x^3)/(3*b*x^3*Log[f])", //
				2243, 2240);
	}

	// {2244, 2240}
	public void test0054() {
		check(//
				"Integrate[f^(a + b*x^n)*x^(-1 + 2*n), x]", //
				"-(f^(a + b*x^n)/(b^2*n*Log[f]^2)) + (f^(a + b*x^n)*x^n)/(b*n*Log[f])", //
				2244, 2240);
	}

	// {2246, 2241}
	public void test0055() {
		check(//
				"Integrate[f^(a + b*x^n)*x^(-1 - n), x]", //
				"-(f^(a + b*x^n)/(n*x^n)) + (b*f^a*ExpIntegralEi[b*x^n*Log[f]]*Log[f])/n", //
				2246, 2241);
	}

	// {2242, 2235}
	public void test0056() {
		check(//
				"Integrate[f^(a + b*x^n)*x^(-1 + n/2), x]", //
				"(f^a*Sqrt[Pi]*Erfi[Sqrt[b]*x^(n/2)*Sqrt[Log[f]]])/(Sqrt[b]*n*Sqrt[Log[f]])", //
				2242, 2235);
	}

	// {2207, 2225}
	public void test0057() {
		check(//
				"Integrate[x/E^(0.1*x), x]", //
				"-100./E^(0.1*x) - (10.*x)/E^(0.1*x)", //
				2207, 2225);
	}

	// {2259, 2239}
	public void test0059() {
		check(//
				"Integrate[E^(a^3 + 3*a^2*b*x + 3*a*b^2*x^2 + b^3*x^3), x]", //
				"-((a + b*x)*Gamma[1/3, -(a + b*x)^3])/(3*b*(-(a + b*x)^3)^(1/3))", //
				2259, 2239);
	}

	// {2237, 2241}
	public void test0060() {
		check(//
				"Integrate[f^(c/(a + b*x)), x]", //
				"(f^(c/(a + b*x))*(a + b*x))/b - (c*ExpIntegralEi[(c*Log[f])/(a + b*x)]*Log[f])/b", //
				2237, 2241);
	}

	// {2243, 2240}
	public void test0061() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^2)*(c + d*x)^3, x]", //
				"-F^(a + b*(c + d*x)^2)/(2*b^2*d*Log[F]^2) + (F^(a + b*(c + d*x)^2)*(c + d*x)^2)/(2*b*d*Log[F])", //
				2243, 2240);
	}

	// {2245, 2241}
	public void test0062() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^2)/(c + d*x)^3, x]", //
				"-F^(a + b*(c + d*x)^2)/(2*d*(c + d*x)^2) + (b*F^a*ExpIntegralEi[b*(c + d*x)^2*Log[F]]*Log[F])/(2*d)", //
				2245, 2241);
	}

	// {2243, 2235}
	public void test0063() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^2)*(c + d*x)^2, x]", //
				"-(F^a*Sqrt[Pi]*Erfi[Sqrt[b]*(c + d*x)*Sqrt[Log[F]]])/(4*b^(3/2)*d*Log[F]^(3/2)) + (F^(a + b*(c + d*x)^2)*(c + d*x))/(2*b*d*Log[F])", //
				2243, 2235);
	}

	// {2245, 2235}
	public void test0064() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^2)/(c + d*x)^2, x]", //
				"-(F^(a + b*(c + d*x)^2)/(d*(c + d*x))) + (Sqrt[b]*F^a*Sqrt[Pi]*Erfi[Sqrt[b]*(c + d*x)*Sqrt[Log[F]]]*Sqrt[Log[F]])/d", //
				2245, 2235);
	}

	// {2243, 2240}
	public void test0065() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^3)*(c + d*x)^5, x]", //
				"-F^(a + b*(c + d*x)^3)/(3*b^2*d*Log[F]^2) + (F^(a + b*(c + d*x)^3)*(c + d*x)^3)/(3*b*d*Log[F])", //
				2243, 2240);
	}

	// {2245, 2241}
	public void test0066() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^3)/(c + d*x)^4, x]", //
				"-F^(a + b*(c + d*x)^3)/(3*d*(c + d*x)^3) + (b*F^a*ExpIntegralEi[b*(c + d*x)^3*Log[F]]*Log[F])/(3*d)", //
				2245, 2241);
	}

	// {2237, 2241}
	public void test0067() {
		check(//
				"Integrate[F^(a + b/(c + d*x)), x]", //
				"(F^(a + b/(c + d*x))*(c + d*x))/d - (b*F^a*ExpIntegralEi[(b*Log[F])/(c + d*x)]*Log[F])/d", //
				2237, 2241);
	}

	// {2243, 2240}
	public void test0068() {
		check(//
				"Integrate[F^(a + b/(c + d*x))/(c + d*x)^3, x]", //
				"F^(a + b/(c + d*x))/(b^2*d*Log[F]^2) - F^(a + b/(c + d*x))/(b*d*(c + d*x)*Log[F])", //
				2243, 2240);
	}

	// {2245, 2241}
	public void test0069() {
		check(//
				"Integrate[F^(a + b/(c + d*x)^2)*(c + d*x), x]", //
				"(F^(a + b/(c + d*x)^2)*(c + d*x)^2)/(2*d) - (b*F^a*ExpIntegralEi[(b*Log[F])/(c + d*x)^2]*Log[F])/(2*d)", //
				2245, 2241);
	}

	// {2243, 2240}
	public void test0070() {
		check(//
				"Integrate[F^(a + b/(c + d*x)^2)/(c + d*x)^5, x]", //
				"F^(a + b/(c + d*x)^2)/(2*b^2*d*Log[F]^2) - F^(a + b/(c + d*x)^2)/(2*b*d*(c + d*x)^2*Log[F])", //
				2243, 2240);
	}

	// {2242, 2235}
	public void test0071() {
		check(//
				"Integrate[F^(a + b/(c + d*x)^2)/(c + d*x)^2, x]", //
				"-(F^a*Sqrt[Pi]*Erfi[(Sqrt[b]*Sqrt[Log[F]])/(c + d*x)])/(2*Sqrt[b]*d*Sqrt[Log[F]])", //
				2242, 2235);
	}

	// {2245, 2241}
	public void test0072() {
		check(//
				"Integrate[F^(a + b/(c + d*x)^3)*(c + d*x)^2, x]", //
				"(F^(a + b/(c + d*x)^3)*(c + d*x)^3)/(3*d) - (b*F^a*ExpIntegralEi[(b*Log[F])/(c + d*x)^3]*Log[F])/(3*d)", //
				2245, 2241);
	}

	// {2243, 2240}
	public void test0073() {
		check(//
				"Integrate[F^(a + b/(c + d*x)^3)/(c + d*x)^7, x]", //
				"F^(a + b/(c + d*x)^3)/(3*b^2*d*Log[F]^2) - F^(a + b/(c + d*x)^3)/(3*b*d*(c + d*x)^3*Log[F])", //
				2243, 2240);
	}

	// {2244, 2240}
	public void test0074() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^n)*(c + d*x)^(-1 + 2*n), x]", //
				"-(F^(a + b*(c + d*x)^n)/(b^2*d*n*Log[F]^2)) + (F^(a + b*(c + d*x)^n)*(c + d*x)^n)/(b*d*n*Log[F])", //
				2244, 2240);
	}

	// {2246, 2241}
	public void test0075() {
		check(//
				"Integrate[F^(a + b*(c + d*x)^n)*(c + d*x)^(-1 - n), x]", //
				"-(F^(a + b*(c + d*x)^n)/(d*n*(c + d*x)^n)) + (b*F^a*ExpIntegralEi[b*(c + d*x)^n*Log[F]]*Log[F])/(d*n)", //
				2246, 2241);
	}

	// {2242, 2235}
	public void test0076() {
		check(//
				"Integrate[F^(c*(a + b*x)^n)*(a + b*x)^(-1 + n/2), x]", //
				"(Sqrt[Pi]*Erfi[Sqrt[c]*(a + b*x)^(n/2)*Sqrt[Log[F]]])/(b*Sqrt[c]*n*Sqrt[Log[F]])", //
				2242, 2235);
	}

	// {2242, 2236}
	public void test0077() {
		check(//
				"Integrate[(a + b*x)^(-1 + n/2)/F^(c*(a + b*x)^n), x]", //
				"(Sqrt[Pi]*Erf[Sqrt[c]*(a + b*x)^(n/2)*Sqrt[Log[F]]])/(b*Sqrt[c]*n*Sqrt[Log[F]])", //
				2242, 2236);
	}

	// {2237, 2241}
	public void test0079() {
		check(//
				"Integrate[E^(e/(c + d*x)), x]", //
				"(E^(e/(c + d*x))*(c + d*x))/d - (e*ExpIntegralEi[e/(c + d*x)])/d", //
				2237, 2241);
	}

	// {2266, 2235}
	public void test0080() {
		check(//
				"Integrate[f^(a + b*x + c*x^2), x]", //
				"(f^(a - b^2/(4*c))*Sqrt[Pi]*Erfi[((b + 2*c*x)*Sqrt[Log[f]])/(2*Sqrt[c])])/(2*Sqrt[c]*Sqrt[Log[f]])", //
				2266, 2235);
	}

	// {2266, 2236}
	public void test0081() {
		check(//
				"Integrate[E^(a + b*x - c*x^2), x]", //
				"-(E^(a + b^2/(4*c))*Sqrt[Pi]*Erf[(b - 2*c*x)/(2*Sqrt[c])])/(2*Sqrt[c])", //
				2266, 2236);
	}

	// {2269, 2268}
	public void test0082() {
		check(//
				"Integrate[f^(a + b*x + c*x^2)*(b + 2*c*x)^3, x]", //
				"(-4*c*f^(a + b*x + c*x^2))/Log[f]^2 + (f^(a + b*x + c*x^2)*(b + 2*c*x)^2)/Log[f]", //
				2269, 2268);
	}

	// {2271, 2270}
	public void test0083() {
		check(//
				"Integrate[f^(a + b*x + c*x^2)/(b + 2*c*x)^3, x]", //
				"-f^(a + b*x + c*x^2)/(4*c*(b + 2*c*x)^2) + (f^(a - b^2/(4*c))*ExpIntegralEi[((b + 2*c*x)^2*Log[f])/(4*c)]*Log[f])/(16*c^2)", //
				2271, 2270);
	}

	// {2269, 2268}
	public void test0084() {
		check(//
				"Integrate[f^(b*x + c*x^2)*(b + 2*c*x)^3, x]", //
				"(-4*c*f^(b*x + c*x^2))/Log[f]^2 + (f^(b*x + c*x^2)*(b + 2*c*x)^2)/Log[f]", //
				2269, 2268);
	}

	// {2271, 2270}
	public void test0085() {
		check(//
				"Integrate[f^(b*x + c*x^2)/(b + 2*c*x)^3, x]", //
				"-f^(b*x + c*x^2)/(4*c*(b + 2*c*x)^2) + (ExpIntegralEi[((b + 2*c*x)^2*Log[f])/(4*c)]*Log[f])/(16*c^2*f^(b^2/(4*c)))", //
				2271, 2270);
	}

	// {2281, 211}
	public void test0086() {
		check(//
				"Integrate[2^x/(a + 4^x*b), x]", //
				"ArcTan[(2^x*Sqrt[b])/Sqrt[a]]/(Sqrt[a]*Sqrt[b]*Log[2])", //
				2281, 211);
	}

	// {2281, 211}
	public void test0087() {
		check(//
				"Integrate[2^x/(a + 2^(2*x)*b), x]", //
				"ArcTan[(2^x*Sqrt[b])/Sqrt[a]]/(Sqrt[a]*Sqrt[b]*Log[2])", //
				2281, 211);
	}

	// {2281, 214}
	public void test0088() {
		check(//
				"Integrate[2^x/(a - 4^x*b), x]", //
				"ArcTanh[(2^x*Sqrt[b])/Sqrt[a]]/(Sqrt[a]*Sqrt[b]*Log[2])", //
				2281, 214);
	}

	// {2281, 214}
	public void test0089() {
		check(//
				"Integrate[2^x/(a - 2^(2*x)*b), x]", //
				"ArcTanh[(2^x*Sqrt[b])/Sqrt[a]]/(Sqrt[a]*Sqrt[b]*Log[2])", //
				2281, 214);
	}

	// {2281, 197}
	public void test0090() {
		check(//
				"Integrate[2^x/Sqrt[a + b/4^x], x]", //
				"(2^x*Sqrt[a + b/2^(2*x)])/(a*Log[2])", //
				2281, 197);
	}

	// {2281, 197}
	public void test0091() {
		check(//
				"Integrate[2^x/Sqrt[a + b/2^(2*x)], x]", //
				"(2^x*Sqrt[a + b/2^(2*x)])/(a*Log[2])", //
				2281, 197);
	}

	// {2281, 197}
	public void test0092() {
		check(//
				"Integrate[2^x/Sqrt[a - b/4^x], x]", //
				"(2^x*Sqrt[a - b/2^(2*x)])/(a*Log[2])", //
				2281, 197);
	}

	// {2281, 197}
	public void test0093() {
		check(//
				"Integrate[2^x/Sqrt[a - b/2^(2*x)], x]", //
				"(2^x*Sqrt[a - b/2^(2*x)])/(a*Log[2])", //
				2281, 197);
	}

	// {2320, 32}
	public void test0094() {
		check(//
				"Integrate[(2 + E^(-x) + E^x)^(-1), x]", //
				"-(1 + E^x)^(-1)", //
				2320, 32);
	}

	// {2320, 32}
	public void test0095() {
		check(//
				"Integrate[(2 + f^(-c - d*x) + f^(c + d*x))^(-1), x]", //
				"-(1/(d*(1 + f^(c + d*x))*Log[f]))", //
				2320, 32);
	}

	// {2320, 32}
	public void test0096() {
		check(//
				"Integrate[(2 + 3^(-x) + 3^x)^(-1), x]", //
				"-(1/((1 + 3^x)*Log[3]))", //
				2320, 32);
	}

	// {2329, 2209}
	public void test0097() {
		check(//
				"Integrate[F^((3*Sqrt[1 - a*x])/Sqrt[1 + a*x])/(1 - a^2*x^2), x]", //
				"-(ExpIntegralEi[(3*Sqrt[1 - a*x]*Log[F])/Sqrt[1 + a*x]]/a)", //
				2329, 2209);
	}

	// {2329, 2209}
	public void test0098() {
		check(//
				"Integrate[F^((2*Sqrt[1 - a*x])/Sqrt[1 + a*x])/(1 - a^2*x^2), x]", //
				"-(ExpIntegralEi[(2*Sqrt[1 - a*x]*Log[F])/Sqrt[1 + a*x]]/a)", //
				2329, 2209);
	}

	// {2329, 2209}
	public void test0099() {
		check(//
				"Integrate[F^(Sqrt[1 - a*x]/Sqrt[1 + a*x])/(1 - a^2*x^2), x]", //
				"-(ExpIntegralEi[(Sqrt[1 - a*x]*Log[F])/Sqrt[1 + a*x]]/a)", //
				2329, 2209);
	}

	// {2329, 2209}
	public void test0100() {
		check(//
				"Integrate[1/(F^(Sqrt[1 - a*x]/Sqrt[1 + a*x])*(1 - a^2*x^2)), x]", //
				"-(ExpIntegralEi[-((Sqrt[1 - a*x]*Log[F])/Sqrt[1 + a*x])]/a)", //
				2329, 2209);
	}

	// {2329, 2209}
	public void test0101() {
		check(//
				"Integrate[1/(F^((2*Sqrt[1 - a*x])/Sqrt[1 + a*x])*(1 - a^2*x^2)), x]", //
				"-(ExpIntegralEi[(-2*Sqrt[1 - a*x]*Log[F])/Sqrt[1 + a*x]]/a)", //
				2329, 2209);
	}

	// {2325, 2225}
	public void test0102() {
		check(//
				"Integrate[a^x*b^x, x]", //
				"(a^x*b^x)/(Log[a] + Log[b])", //
				2325, 2225);
	}

	// {2325, 2209}
	public void test0103() {
		check(//
				"Integrate[(a^x*b^x)/x, x]", //
				"ExpIntegralEi[x*(Log[a] + Log[b])]", //
				2325, 2209);
	}

	// {2325, 2225}
	public void test0104() {
		check(//
				"Integrate[a^x/b^x, x]", //
				"a^x/(b^x*(Log[a] - Log[b]))", //
				2325, 2225);
	}

	// {2308, 2235}
	public void test0107() {
		check(//
				"Integrate[F^(f*(a + b*Log[c*(d + e*x)^n]^2))/(d*g + e*g*x), x]", //
				"(F^(a*f)*Sqrt[Pi]*Erfi[Sqrt[b]*Sqrt[f]*Sqrt[Log[F]]*Log[c*(d + e*x)^n]])/(2*Sqrt[b]*e*Sqrt[f]*g*n*Sqrt[Log[F]])", //
				2308, 2235);
	}

	// {6839, 2212}
	public void test0108() {
		check(//
				"Integrate[E^(a + b*x + c*x^2)*(b + 2*c*x)*(a + b*x + c*x^2)^m, x]", //
				"((a + b*x + c*x^2)^m*Gamma[1 + m, -a - b*x - c*x^2])/(-a - b*x - c*x^2)^m", //
				6839, 2212);
	}

	// {6839, 2209}
	public void test0109() {
		check(//
				"Integrate[(E^(a + b*x + c*x^2)*(b + 2*c*x))/(a + b*x + c*x^2), x]", //
				"ExpIntegralEi[a + b*x + c*x^2]", //
				6839, 2209);
	}

	// {2281, 222}
	public void test0110() {
		check(//
				"Integrate[1/(E^x*Sqrt[1 - E^(-2*x)]), x]", //
				"-ArcSin[E^(-x)]", //
				2281, 222);
	}

	// {2281, 209}
	public void test0111() {
		check(//
				"Integrate[E^x/(4 + E^(2*x)), x]", //
				"ArcTan[E^x/2]/2", //
				2281, 209);
	}

	// {2281, 212}
	public void test0112() {
		check(//
				"Integrate[E^x/(1 - E^(2*x)), x]", //
				"ArcTanh[E^x]", //
				2281, 212);
	}

	// {2281, 212}
	public void test0113() {
		check(//
				"Integrate[E^x/(3 - 4*E^(2*x)), x]", //
				"ArcTanh[(2*E^x)/Sqrt[3]]/(2*Sqrt[3])", //
				2281, 212);
	}

	// {2243, 2240}
	public void test0114() {
		check(//
				"Integrate[E^x^2*x^3, x]", //
				"-E^x^2/2 + (E^x^2*x^2)/2", //
				2243, 2240);
	}

	// {2281, 213}
	public void test0115() {
		check(//
				"Integrate[E^x/(-4 + E^(2*x)), x]", //
				"-ArcTanh[E^x/2]/2", //
				2281, 213);
	}

	// {2225}
	public void test0116() {
		check(//
				"Integrate[E^x - x^E, x]", //
				"E^x - x^(1 + E)/(1 + E)", //
				2225);
	}

	// {2281, 222}
	public void test0117() {
		check(//
				"Integrate[E^x/Sqrt[1 - E^(2*x)], x]", //
				"ArcSin[E^x]", //
				2281, 222);
	}

	// {2281, 209}
	public void test0118() {
		check(//
				"Integrate[E^(2*x)/(1 + E^(4*x)), x]", //
				"ArcTan[E^(2*x)]/2", //
				2281, 209);
	}

	// {2281, 213}
	public void test0119() {
		check(//
				"Integrate[E^x/(-1 + E^(2*x)), x]", //
				"-ArcTanh[E^x]", //
				2281, 213);
	}

	// {2281, 209}
	public void test0120() {
		check(//
				"Integrate[E^x/(1 + E^(2*x)), x]", //
				"ArcTan[E^x]", //
				2281, 209);
	}

	// {2281, 221}
	public void test0121() {
		check(//
				"Integrate[E^x/Sqrt[1 + E^(2*x)], x]", //
				"ArcSinh[E^x]", //
				2281, 221);
	}

	// {2281, 212}
	public void test0122() {
		check(//
				"Integrate[E^x/(16 - E^(2*x)), x]", //
				"ArcTanh[E^x/4]/4", //
				2281, 212);
	}

	// {2281, 209}
	public void test0123() {
		check(//
				"Integrate[E^(5*x)/(1 + E^(10*x)), x]", //
				"ArcTan[E^(5*x)]/5", //
				2281, 209);
	}

	// {2281, 221}
	public void test0124() {
		check(//
				"Integrate[E^(4*x)/Sqrt[16 + E^(8*x)], x]", //
				"ArcSinh[E^(4*x)/4]/4", //
				2281, 221);
	}

	// {6847, 4518}
	public void test0125() {
		check(//
				"Integrate[E^(4*x^3)*x^2*Cos[7*x^3], x]", //
				"(4*E^(4*x^3)*Cos[7*x^3])/195 + (7*E^(4*x^3)*Sin[7*x^3])/195", //
				6847, 4518);
	}

	// {2320, 32}
	public void test0126() {
		check(//
				"Integrate[E^x/(1 + 2*E^x + E^(2*x)), x]", //
				"-(1 + E^x)^(-1)", //
				2320, 32);
	}

	// {2320, 3855}
	public void test0127() {
		check(//
				"Integrate[E^x*Sech[E^x], x]", //
				"ArcTan[Sinh[E^x]]", //
				2320, 3855);
	}

	// {2320, 30}
	public void test0128() {
		check(//
				"Integrate[(E^(5*x) + E^(7*x))/(E^(-x) + E^x), x]", //
				"E^(6*x)/6", //
				2320, 30);
	}

	// {2281, 197}
	public void test0129() {
		check(//
				"Integrate[1/(E^x*Sqrt[1 + E^(2*x)]), x]", //
				"-(Sqrt[1 + E^(2*x)]/E^x)", //
				2281, 197);
	}

	// {2320, 2225}
	public void test0130() {
		check(//
				"Integrate[E^(E^x + x), x]", //
				"E^E^x", //
				2320, 2225);
	}

	// {2320, 209}
	public void test0131() {
		check(//
				"Integrate[(E^(-x) + E^x)^(-1), x]", //
				"ArcTan[E^x]", //
				2320, 209);
	}

	// {2320, 267}
	public void test0132() {
		check(//
				"Integrate[(E^(-x) + E^x)^(-2), x]", //
				"-1/(2*(1 + E^(2*x)))", //
				2320, 267);
	}

	// {2320, 213}
	public void test0133() {
		check(//
				"Integrate[(-E^(-x) + E^x)^(-1), x]", //
				"-ArcTanh[E^x]", //
				2320, 213);
	}

	// {2320, 267}
	public void test0134() {
		check(//
				"Integrate[(-E^(-x) + E^x)^(-2), x]", //
				"1/(2*(1 - E^(2*x)))", //
				2320, 267);
	}

	// {2245, 2235}
	public void test0135() {
		check(//
				"Integrate[E^x^2/x^2, x]", //
				"-(E^x^2/x) + Sqrt[Pi]*Erfi[x]", //
				2245, 2235);
	}

	// {2225}
	public void test0136() {
		check(//
				"Integrate[k^(x/2) + x^Sqrt[k], x]", //
				"x^(1 + Sqrt[k])/(1 + Sqrt[k]) + (2*k^(x/2))/Log[k]", //
				2225);
	}

	// {2293}
	public void test0137() {
		check(//
				"Integrate[1/Sqrt[E^x + x] + E^x/Sqrt[E^x + x], x]", //
				"2*Sqrt[E^x + x]", //
				2293);
	}

	// {2305}
	public void test0139() {
		check(//
				"Integrate[-(E^x + x)^(-1/3) + x/(E^x + x)^(1/3) - (E^x + x)^(2/3), x]", //
				"(-3*(E^x + x)^(2/3))/2", //
				2305);
	}

	// {6873, 2239}
	public void test0144() {
		check(//
				"Integrate[E^(a + c + b*x^n + d*x^n), x]", //
				"-((E^(a + c)*x*Gamma[n^(-1), -((b + d)*x^n)])/(n*(-((b + d)*x^n))^n^(-1)))", //
				6873, 2239);
	}

	// {2325, 2239}
	public void test0145() {
		check(//
				"Integrate[f^(a + b*x^n)*g^(c + d*x^n), x]", //
				"-((f^a*g^c*x*Gamma[n^(-1), -(x^n*(b*Log[f] + d*Log[g]))])/(n*(-(x^n*(b*Log[f] + d*Log[g])))^n^(-1)))", //
				2325, 2239);
	}

}
