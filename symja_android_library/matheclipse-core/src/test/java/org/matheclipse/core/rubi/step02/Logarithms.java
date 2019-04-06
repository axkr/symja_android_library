package org.matheclipse.core.rubi.step02;

public class Logarithms extends AbstractRubiTestCase {

	static boolean init = true;

	public Logarithms(String name) {
		super(name, false);
	}


	@Override
	protected void setUp() {
		try {
			super.setUp();
			if (init) {
				System.out.println("Logarithms");
				init = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// {2342, 2341}
	public void test0001() {
		check(//
				"Integrate[x^3*Log[c*x]^2, x]", //
				"x^4/32 - (x^4*Log[c*x])/8 + (x^4*Log[c*x]^2)/4", //
				2342, 2341);
	}

	// {2342, 2341}
	public void test0002() {
		check(//
				"Integrate[x^2*Log[c*x]^2, x]", //
				"(2*x^3)/27 - (2*x^3*Log[c*x])/9 + (x^3*Log[c*x]^2)/3", //
				2342, 2341);
	}

	// {2342, 2341}
	public void test0003() {
		check(//
				"Integrate[x*Log[c*x]^2, x]", //
				"x^2/4 - (x^2*Log[c*x])/2 + (x^2*Log[c*x]^2)/2", //
				2342, 2341);
	}

	// {2333, 2332}
	public void test0004() {
		check(//
				"Integrate[Log[c*x]^2, x]", //
				"2*x - 2*x*Log[c*x] + x*Log[c*x]^2", //
				2333, 2332);
	}

	// {2339, 30}
	public void test0005() {
		check(//
				"Integrate[Log[c*x]^2/x, x]", //
				"Log[c*x]^3/3", //
				2339, 30);
	}

	// {2342, 2341}
	public void test0006() {
		check(//
				"Integrate[Log[c*x]^2/x^2, x]", //
				"-2/x - (2*Log[c*x])/x - Log[c*x]^2/x", //
				2342, 2341);
	}

	// {2342, 2341}
	public void test0007() {
		check(//
				"Integrate[Log[c*x]^2/x^3, x]", //
				"-1/(4*x^2) - Log[c*x]/(2*x^2) - Log[c*x]^2/(2*x^2)", //
				2342, 2341);
	}

	// {2339, 30}
	public void test0008() {
		check(//
				"Integrate[Log[c*x]^3/x, x]", //
				"Log[c*x]^4/4", //
				2339, 30);
	}

	// {2346, 2209}
	public void test0009() {
		check(//
				"Integrate[x^3/Log[c*x], x]", //
				"ExpIntegralEi[4*Log[c*x]]/c^4", //
				2346, 2209);
	}

	// {2346, 2209}
	public void test0010() {
		check(//
				"Integrate[x^2/Log[c*x], x]", //
				"ExpIntegralEi[3*Log[c*x]]/c^3", //
				2346, 2209);
	}

	// {2346, 2209}
	public void test0011() {
		check(//
				"Integrate[x/Log[c*x], x]", //
				"ExpIntegralEi[2*Log[c*x]]/c^2", //
				2346, 2209);
	}

	// {2339, 29}
	public void test0012() {
		check(//
				"Integrate[1/(x*Log[c*x]), x]", //
				"Log[Log[c*x]]", //
				2339, 29);
	}

	// {2346, 2209}
	public void test0013() {
		check(//
				"Integrate[1/(x^2*Log[c*x]), x]", //
				"c*ExpIntegralEi[-Log[c*x]]", //
				2346, 2209);
	}

	// {2346, 2209}
	public void test0014() {
		check(//
				"Integrate[1/(x^3*Log[c*x]), x]", //
				"c^2*ExpIntegralEi[-2*Log[c*x]]", //
				2346, 2209);
	}

	// {2334, 2335}
	public void test0015() {
		check(//
				"Integrate[Log[c*x]^(-2), x]", //
				"-(x/Log[c*x]) + LogIntegral[c*x]/c", //
				2334, 2335);
	}

	// {2339, 30}
	public void test0016() {
		check(//
				"Integrate[1/(x*Log[c*x]^2), x]", //
				"-Log[c*x]^(-1)", //
				2339, 30);
	}

	// {2339, 30}
	public void test0017() {
		check(//
				"Integrate[1/(x*Log[c*x]^3), x]", //
				"-1/(2*Log[c*x]^2)", //
				2339, 30);
	}

	// {2332}
	public void test0018() {
		check(//
				"Integrate[a + b*Log[c*x^n], x]", //
				"a*x - b*n*x + b*x*Log[c*x^n]", //
				2332);
	}

	// {2342, 2341}
	public void test0019() {
		check(//
				"Integrate[x^3*(a + b*Log[c*x^n])^2, x]", //
				"(b^2*n^2*x^4)/32 - (b*n*x^4*(a + b*Log[c*x^n]))/8 + (x^4*(a + b*Log[c*x^n])^2)/4", //
				2342, 2341);
	}

	// {2342, 2341}
	public void test0020() {
		check(//
				"Integrate[x^2*(a + b*Log[c*x^n])^2, x]", //
				"(2*b^2*n^2*x^3)/27 - (2*b*n*x^3*(a + b*Log[c*x^n]))/9 + (x^3*(a + b*Log[c*x^n])^2)/3", //
				2342, 2341);
	}

	// {2342, 2341}
	public void test0021() {
		check(//
				"Integrate[x*(a + b*Log[c*x^n])^2, x]", //
				"(b^2*n^2*x^2)/4 - (b*n*x^2*(a + b*Log[c*x^n]))/2 + (x^2*(a + b*Log[c*x^n])^2)/2", //
				2342, 2341);
	}

	// {2339, 30}
	public void test0022() {
		check(//
				"Integrate[(a + b*Log[c*x^n])^2/x, x]", //
				"(a + b*Log[c*x^n])^3/(3*b*n)", //
				2339, 30);
	}

	// {2342, 2341}
	public void test0023() {
		check(//
				"Integrate[(a + b*Log[c*x^n])^2/x^2, x]", //
				"(-2*b^2*n^2)/x - (2*b*n*(a + b*Log[c*x^n]))/x - (a + b*Log[c*x^n])^2/x", //
				2342, 2341);
	}

	// {2342, 2341}
	public void test0024() {
		check(//
				"Integrate[(a + b*Log[c*x^n])^2/x^3, x]", //
				"-(b^2*n^2)/(4*x^2) - (b*n*(a + b*Log[c*x^n]))/(2*x^2) - (a + b*Log[c*x^n])^2/(2*x^2)", //
				2342, 2341);
	}

	// {2339, 30}
	public void test0025() {
		check(//
				"Integrate[(a + b*Log[c*x^n])^3/x, x]", //
				"(a + b*Log[c*x^n])^4/(4*b*n)", //
				2339, 30);
	}

	// {2347, 2209}
	public void test0026() {
		check(//
				"Integrate[x^3/(a + b*Log[c*x^n]), x]", //
				"(x^4*ExpIntegralEi[(4*(a + b*Log[c*x^n]))/(b*n)])/(b*E^((4*a)/(b*n))*n*(c*x^n)^(4/n))", //
				2347, 2209);
	}

	// {2347, 2209}
	public void test0027() {
		check(//
				"Integrate[x^2/(a + b*Log[c*x^n]), x]", //
				"(x^3*ExpIntegralEi[(3*(a + b*Log[c*x^n]))/(b*n)])/(b*E^((3*a)/(b*n))*n*(c*x^n)^(3/n))", //
				2347, 2209);
	}

	// {2347, 2209}
	public void test0028() {
		check(//
				"Integrate[x/(a + b*Log[c*x^n]), x]", //
				"(x^2*ExpIntegralEi[(2*(a + b*Log[c*x^n]))/(b*n)])/(b*E^((2*a)/(b*n))*n*(c*x^n)^(2/n))", //
				2347, 2209);
	}

	// {2337, 2209}
	public void test0029() {
		check(//
				"Integrate[(a + b*Log[c*x^n])^(-1), x]", //
				"(x*ExpIntegralEi[(a + b*Log[c*x^n])/(b*n)])/(b*E^(a/(b*n))*n*(c*x^n)^n^(-1))", //
				2337, 2209);
	}

	// {2339, 29}
	public void test0030() {
		check(//
				"Integrate[1/(x*(a + b*Log[c*x^n])), x]", //
				"Log[a + b*Log[c*x^n]]/(b*n)", //
				2339, 29);
	}

	// {2347, 2209}
	public void test0031() {
		check(//
				"Integrate[1/(x^2*(a + b*Log[c*x^n])), x]", //
				"(E^(a/(b*n))*(c*x^n)^n^(-1)*ExpIntegralEi[-((a + b*Log[c*x^n])/(b*n))])/(b*n*x)", //
				2347, 2209);
	}

	// {2347, 2209}
	public void test0032() {
		check(//
				"Integrate[1/(x^3*(a + b*Log[c*x^n])), x]", //
				"(E^((2*a)/(b*n))*(c*x^n)^(2/n)*ExpIntegralEi[(-2*(a + b*Log[c*x^n]))/(b*n)])/(b*n*x^2)", //
				2347, 2209);
	}

	// {2347, 2209}
	public void test0033() {
		check(//
				"Integrate[1/(x^4*(a + b*Log[c*x^n])), x]", //
				"(E^((3*a)/(b*n))*(c*x^n)^(3/n)*ExpIntegralEi[(-3*(a + b*Log[c*x^n]))/(b*n)])/(b*n*x^3)", //
				2347, 2209);
	}

	// {2339, 30}
	public void test0034() {
		check(//
				"Integrate[1/(x*(a + b*Log[c*x^n])^2), x]", //
				"-(1/(b*n*(a + b*Log[c*x^n])))", //
				2339, 30);
	}

	// {2339, 30}
	public void test0035() {
		check(//
				"Integrate[1/(x*(a + b*Log[c*x^n])^3), x]", //
				"-1/(2*b*n*(a + b*Log[c*x^n])^2)", //
				2339, 30);
	}

	// {2342, 2341}
	public void test0036() {
		check(//
				"Integrate[(d*x)^(5/2)*(a + b*Log[c*x^n])^2, x]", //
				"(16*b^2*n^2*(d*x)^(7/2))/(343*d) - (8*b*n*(d*x)^(7/2)*(a + b*Log[c*x^n]))/(49*d) + (2*(d*x)^(7/2)*(a + b*Log[c*x^n])^2)/(7*d)", //
				2342, 2341);
	}

	// {2342, 2341}
	public void test0037() {
		check(//
				"Integrate[(d*x)^(3/2)*(a + b*Log[c*x^n])^2, x]", //
				"(16*b^2*n^2*(d*x)^(5/2))/(125*d) - (8*b*n*(d*x)^(5/2)*(a + b*Log[c*x^n]))/(25*d) + (2*(d*x)^(5/2)*(a + b*Log[c*x^n])^2)/(5*d)", //
				2342, 2341);
	}

	// {2342, 2341}
	public void test0038() {
		check(//
				"Integrate[Sqrt[d*x]*(a + b*Log[c*x^n])^2, x]", //
				"(16*b^2*n^2*(d*x)^(3/2))/(27*d) - (8*b*n*(d*x)^(3/2)*(a + b*Log[c*x^n]))/(9*d) + (2*(d*x)^(3/2)*(a + b*Log[c*x^n])^2)/(3*d)", //
				2342, 2341);
	}

	// {2342, 2341}
	public void test0039() {
		check(//
				"Integrate[(a + b*Log[c*x^n])^2/Sqrt[d*x], x]", //
				"(16*b^2*n^2*Sqrt[d*x])/d - (8*b*n*Sqrt[d*x]*(a + b*Log[c*x^n]))/d + (2*Sqrt[d*x]*(a + b*Log[c*x^n])^2)/d", //
				2342, 2341);
	}

	// {2342, 2341}
	public void test0040() {
		check(//
				"Integrate[(a + b*Log[c*x^n])^2/(d*x)^(3/2), x]", //
				"(-16*b^2*n^2)/(d*Sqrt[d*x]) - (8*b*n*(a + b*Log[c*x^n]))/(d*Sqrt[d*x]) - (2*(a + b*Log[c*x^n])^2)/(d*Sqrt[d*x])", //
				2342, 2341);
	}

	// {2342, 2341}
	public void test0041() {
		check(//
				"Integrate[(a + b*Log[c*x^n])^2/(d*x)^(5/2), x]", //
				"(-16*b^2*n^2)/(27*d*(d*x)^(3/2)) - (8*b*n*(a + b*Log[c*x^n]))/(9*d*(d*x)^(3/2)) - (2*(a + b*Log[c*x^n])^2)/(3*d*(d*x)^(3/2))", //
				2342, 2341);
	}

	// {2347, 2209}
	public void test0042() {
		check(//
				"Integrate[(d*x)^(5/2)/(a + b*Log[c*x^n]), x]", //
				"((d*x)^(7/2)*ExpIntegralEi[(7*(a + b*Log[c*x^n]))/(2*b*n)])/(b*d*E^((7*a)/(2*b*n))*n*(c*x^n)^(7/(2*n)))", //
				2347, 2209);
	}

	// {2347, 2209}
	public void test0043() {
		check(//
				"Integrate[(d*x)^(3/2)/(a + b*Log[c*x^n]), x]", //
				"((d*x)^(5/2)*ExpIntegralEi[(5*(a + b*Log[c*x^n]))/(2*b*n)])/(b*d*E^((5*a)/(2*b*n))*n*(c*x^n)^(5/(2*n)))", //
				2347, 2209);
	}

	// {2347, 2209}
	public void test0044() {
		check(//
				"Integrate[Sqrt[d*x]/(a + b*Log[c*x^n]), x]", //
				"((d*x)^(3/2)*ExpIntegralEi[(3*(a + b*Log[c*x^n]))/(2*b*n)])/(b*d*E^((3*a)/(2*b*n))*n*(c*x^n)^(3/(2*n)))", //
				2347, 2209);
	}

	// {2347, 2209}
	public void test0045() {
		check(//
				"Integrate[1/(Sqrt[d*x]*(a + b*Log[c*x^n])), x]", //
				"(Sqrt[d*x]*ExpIntegralEi[(a + b*Log[c*x^n])/(2*b*n)])/(b*d*E^(a/(2*b*n))*n*(c*x^n)^(1/(2*n)))", //
				2347, 2209);
	}

	// {2347, 2209}
	public void test0046() {
		check(//
				"Integrate[1/((d*x)^(3/2)*(a + b*Log[c*x^n])), x]", //
				"(E^(a/(2*b*n))*(c*x^n)^(1/(2*n))*ExpIntegralEi[-(a + b*Log[c*x^n])/(2*b*n)])/(b*d*n*Sqrt[d*x])", //
				2347, 2209);
	}

	// {2347, 2209}
	public void test0047() {
		check(//
				"Integrate[1/((d*x)^(5/2)*(a + b*Log[c*x^n])), x]", //
				"(E^((3*a)/(2*b*n))*(c*x^n)^(3/(2*n))*ExpIntegralEi[(-3*(a + b*Log[c*x^n]))/(2*b*n)])/(b*d*n*(d*x)^(3/2))", //
				2347, 2209);
	}

	// {2339, 30}
	public void test0048() {
		check(//
				"Integrate[Sqrt[Log[a*x^n]]/x, x]", //
				"(2*Log[a*x^n]^(3/2))/(3*n)", //
				2339, 30);
	}

	// {2339, 30}
	public void test0049() {
		check(//
				"Integrate[Log[a*x^n]^(3/2)/x, x]", //
				"(2*Log[a*x^n]^(5/2))/(5*n)", //
				2339, 30);
	}

	// {2339, 30}
	public void test0050() {
		check(//
				"Integrate[1/(x*Sqrt[Log[a*x^n]]), x]", //
				"(2*Sqrt[Log[a*x^n]])/n", //
				2339, 30);
	}

	// {2339, 30}
	public void test0051() {
		check(//
				"Integrate[1/(x*Log[a*x^n]^(3/2)), x]", //
				"-2/(n*Sqrt[Log[a*x^n]])", //
				2339, 30);
	}

	// {2339, 30}
	public void test0052() {
		check(//
				"Integrate[1/(x*Log[a*x^n]^(5/2)), x]", //
				"-2/(3*n*Log[a*x^n]^(3/2))", //
				2339, 30);
	}

	// {2342, 2341}
	public void test0053() {
		check(//
				"Integrate[(d*x)^m*(a + b*Log[c*x^n])^2, x]", //
				"(2*b^2*n^2*(d*x)^(1 + m))/(d*(1 + m)^3) - (2*b*n*(d*x)^(1 + m)*(a + b*Log[c*x^n]))/(d*(1 + m)^2) + ((d*x)^(1 + m)*(a + b*Log[c*x^n])^2)/(d*(1 + m))", //
				2342, 2341);
	}

	// {2347, 2209}
	public void test0054() {
		check(//
				"Integrate[(d*x)^m/(a + b*Log[c*x^n]), x]", //
				"((d*x)^(1 + m)*ExpIntegralEi[((1 + m)*(a + b*Log[c*x^n]))/(b*n)])/(b*d*E^((a*(1 + m))/(b*n))*n*(c*x^n)^((1 + m)/n))", //
				2347, 2209);
	}

	// {2342, 2341}
	public void test0055() {
		check(//
				"Integrate[(d*x)^(-1 + n)*Log[c*x^n]^2, x]", //
				"(2*(d*x)^n)/(d*n) - (2*(d*x)^n*Log[c*x^n])/(d*n) + ((d*x)^n*Log[c*x^n]^2)/(d*n)", //
				2342, 2341);
	}

	// {2347, 2212}
	public void test0056() {
		check(//
				"Integrate[(d*x)^m*(a + b*Log[c*x^n])^p, x]", //
				"((d*x)^(1 + m)*Gamma[1 + p, -(((1 + m)*(a + b*Log[c*x^n]))/(b*n))]*(a + b*Log[c*x^n])^p)/(d*E^((a*(1 + m))/(b*n))*(1 + m)*(c*x^n)^((1 + m)/n)*(-(((1 + m)*(a + b*Log[c*x^n]))/(b*n)))^p)", //
				2347, 2212);
	}

	// {2347, 2212}
	public void test0057() {
		check(//
				"Integrate[x^2*(a + b*Log[c*x^n])^p, x]", //
				"(3^(-1 - p)*x^3*Gamma[1 + p, (-3*(a + b*Log[c*x^n]))/(b*n)]*(a + b*Log[c*x^n])^p)/(E^((3*a)/(b*n))*(c*x^n)^(3/n)*(-((a + b*Log[c*x^n])/(b*n)))^p)", //
				2347, 2212);
	}

	// {2347, 2212}
	public void test0058() {
		check(//
				"Integrate[x*(a + b*Log[c*x^n])^p, x]", //
				"(2^(-1 - p)*x^2*Gamma[1 + p, (-2*(a + b*Log[c*x^n]))/(b*n)]*(a + b*Log[c*x^n])^p)/(E^((2*a)/(b*n))*(c*x^n)^(2/n)*(-((a + b*Log[c*x^n])/(b*n)))^p)", //
				2347, 2212);
	}

	// {2337, 2212}
	public void test0059() {
		check(//
				"Integrate[(a + b*Log[c*x^n])^p, x]", //
				"(x*Gamma[1 + p, -((a + b*Log[c*x^n])/(b*n))]*(a + b*Log[c*x^n])^p)/(E^(a/(b*n))*(c*x^n)^n^(-1)*(-((a + b*Log[c*x^n])/(b*n)))^p)", //
				2337, 2212);
	}

	// {2339, 30}
	public void test0060() {
		check(//
				"Integrate[(a + b*Log[c*x^n])^p/x, x]", //
				"(a + b*Log[c*x^n])^(1 + p)/(b*n*(1 + p))", //
				2339, 30);
	}

	// {2347, 2212}
	public void test0061() {
		check(//
				"Integrate[(a + b*Log[c*x^n])^p/x^2, x]", //
				"-((E^(a/(b*n))*(c*x^n)^n^(-1)*Gamma[1 + p, (a + b*Log[c*x^n])/(b*n)]*(a + b*Log[c*x^n])^p)/(x*((a + b*Log[c*x^n])/(b*n))^p))", //
				2347, 2212);
	}

	// {2347, 2212}
	public void test0062() {
		check(//
				"Integrate[(a + b*Log[c*x^n])^p/x^3, x]", //
				"-((2^(-1 - p)*E^((2*a)/(b*n))*(c*x^n)^(2/n)*Gamma[1 + p, (2*(a + b*Log[c*x^n]))/(b*n)]*(a + b*Log[c*x^n])^p)/(x^2*((a + b*Log[c*x^n])/(b*n))^p))", //
				2347, 2212);
	}

	// {2347, 2212}
	public void test0063() {
		check(//
				"Integrate[(a + b*Log[c*x^n])^p/x^4, x]", //
				"-((3^(-1 - p)*E^((3*a)/(b*n))*(c*x^n)^(3/n)*Gamma[1 + p, (3*(a + b*Log[c*x^n]))/(b*n)]*(a + b*Log[c*x^n])^p)/(x^3*((a + b*Log[c*x^n])/(b*n))^p))", //
				2347, 2212);
	}

	// {2347, 2212}
	public void test0064() {
		check(//
				"Integrate[(d*x)^m*(a + b*Log[c*x])^p, x]", //
				"((c*x)^(-1 - m)*(d*x)^(1 + m)*Gamma[1 + p, -(((1 + m)*(a + b*Log[c*x]))/b)]*(a + b*Log[c*x])^p)/(d*E^((a*(1 + m))/b)*(1 + m)*(-(((1 + m)*(a + b*Log[c*x]))/b))^p)", //
				2347, 2212);
	}

	// {2346, 2212}
	public void test0065() {
		check(//
				"Integrate[x^2*(a + b*Log[c*x])^p, x]", //
				"(3^(-1 - p)*Gamma[1 + p, (-3*(a + b*Log[c*x]))/b]*(a + b*Log[c*x])^p)/(c^3*E^((3*a)/b)*(-((a + b*Log[c*x])/b))^p)", //
				2346, 2212);
	}

	// {2346, 2212}
	public void test0066() {
		check(//
				"Integrate[x*(a + b*Log[c*x])^p, x]", //
				"(2^(-1 - p)*Gamma[1 + p, (-2*(a + b*Log[c*x]))/b]*(a + b*Log[c*x])^p)/(c^2*E^((2*a)/b)*(-((a + b*Log[c*x])/b))^p)", //
				2346, 2212);
	}

	// {2336, 2212}
	public void test0067() {
		check(//
				"Integrate[(a + b*Log[c*x])^p, x]", //
				"(Gamma[1 + p, -((a + b*Log[c*x])/b)]*(a + b*Log[c*x])^p)/(c*E^(a/b)*(-((a + b*Log[c*x])/b))^p)", //
				2336, 2212);
	}

	// {2339, 30}
	public void test0068() {
		check(//
				"Integrate[(a + b*Log[c*x])^p/x, x]", //
				"(a + b*Log[c*x])^(1 + p)/(b*(1 + p))", //
				2339, 30);
	}

	// {2346, 2212}
	public void test0069() {
		check(//
				"Integrate[(a + b*Log[c*x])^p/x^2, x]", //
				"-((c*E^(a/b)*Gamma[1 + p, (a + b*Log[c*x])/b]*(a + b*Log[c*x])^p)/((a + b*Log[c*x])/b)^p)", //
				2346, 2212);
	}

	// {2346, 2212}
	public void test0070() {
		check(//
				"Integrate[(a + b*Log[c*x])^p/x^3, x]", //
				"-((2^(-1 - p)*c^2*E^((2*a)/b)*Gamma[1 + p, (2*(a + b*Log[c*x]))/b]*(a + b*Log[c*x])^p)/((a + b*Log[c*x])/b)^p)", //
				2346, 2212);
	}

	// {2346, 2212}
	public void test0071() {
		check(//
				"Integrate[(a + b*Log[c*x])^p/x^4, x]", //
				"-((3^(-1 - p)*c^3*E^((3*a)/b)*Gamma[1 + p, (3*(a + b*Log[c*x]))/b]*(a + b*Log[c*x])^p)/((a + b*Log[c*x])/b)^p)", //
				2346, 2212);
	}

	// {2347, 2212}
	public void test0072() {
		check(//
				"Integrate[(d*x)^m*(a + b*Log[c*Sqrt[x]])^p, x]", //
				"((d*x)^(1 + m)*Gamma[1 + p, (-2*(1 + m)*(a + b*Log[c*Sqrt[x]]))/b]*(a + b*Log[c*Sqrt[x]])^p)/(2^p*d*E^((2*a*(1 + m))/b)*(1 + m)*(c*Sqrt[x])^(2*(1 + m))*(-(((1 + m)*(a + b*Log[c*Sqrt[x]]))/b))^p)", //
				2347, 2212);
	}

	// {2347, 2212}
	public void test0073() {
		check(//
				"Integrate[x^2*(a + b*Log[c*Sqrt[x]])^p, x]", //
				"(3^(-1 - p)*Gamma[1 + p, (-6*(a + b*Log[c*Sqrt[x]]))/b]*(a + b*Log[c*Sqrt[x]])^p)/(2^p*c^6*E^((6*a)/b)*(-((a + b*Log[c*Sqrt[x]])/b))^p)", //
				2347, 2212);
	}

	// {2347, 2212}
	public void test0074() {
		check(//
				"Integrate[x*(a + b*Log[c*Sqrt[x]])^p, x]", //
				"(2^(-1 - 2*p)*Gamma[1 + p, (-4*(a + b*Log[c*Sqrt[x]]))/b]*(a + b*Log[c*Sqrt[x]])^p)/(c^4*E^((4*a)/b)*(-((a + b*Log[c*Sqrt[x]])/b))^p)", //
				2347, 2212);
	}

	// {2336, 2212}
	public void test0075() {
		check(//
				"Integrate[(a + b*Log[c*Sqrt[x]])^p, x]", //
				"(Gamma[1 + p, (-2*(a + b*Log[c*Sqrt[x]]))/b]*(a + b*Log[c*Sqrt[x]])^p)/(2^p*c^2*E^((2*a)/b)*(-((a + b*Log[c*Sqrt[x]])/b))^p)", //
				2336, 2212);
	}

	// {2339, 30}
	public void test0076() {
		check(//
				"Integrate[(a + b*Log[c*Sqrt[x]])^p/x, x]", //
				"(2*(a + b*Log[c*Sqrt[x]])^(1 + p))/(b*(1 + p))", //
				2339, 30);
	}

	// {2347, 2212}
	public void test0077() {
		check(//
				"Integrate[(a + b*Log[c*Sqrt[x]])^p/x^2, x]", //
				"-((c^2*E^((2*a)/b)*Gamma[1 + p, (2*(a + b*Log[c*Sqrt[x]]))/b]*(a + b*Log[c*Sqrt[x]])^p)/(2^p*((a + b*Log[c*Sqrt[x]])/b)^p))", //
				2347, 2212);
	}

	// {2347, 2212}
	public void test0078() {
		check(//
				"Integrate[(a + b*Log[c*Sqrt[x]])^p/x^3, x]", //
				"-((2^(-1 - 2*p)*c^4*E^((4*a)/b)*Gamma[1 + p, (4*(a + b*Log[c*Sqrt[x]]))/b]*(a + b*Log[c*Sqrt[x]])^p)/((a + b*Log[c*Sqrt[x]])/b)^p)", //
				2347, 2212);
	}

	// {2347, 2212}
	public void test0079() {
		check(//
				"Integrate[(a + b*Log[c*Sqrt[x]])^p/x^4, x]", //
				"-((3^(-1 - p)*c^6*E^((6*a)/b)*Gamma[1 + p, (6*(a + b*Log[c*Sqrt[x]]))/b]*(a + b*Log[c*Sqrt[x]])^p)/(2^p*((a + b*Log[c*Sqrt[x]])/b)^p))", //
				2347, 2212);
	}

	// {2347, 2212}
	public void test0080() {
		check(//
				"Integrate[x^(-1 + n)*(a + b*Log[c*x^n])^p, x]", //
				"(Gamma[1 + p, -((a + b*Log[c*x^n])/b)]*(a + b*Log[c*x^n])^p)/(c*E^(a/b)*n*(-((a + b*Log[c*x^n])/b))^p)", //
				2347, 2212);
	}

	// {2350}
	public void test0081() {
		check(//
				"Integrate[(d + e*x)*(a + b*Log[c*x^n]), x]", //
				"-(b*d*n*x) - (b*e*n*x^2)/4 + d*x*(a + b*Log[c*x^n]) + (e*x^2*(a + b*Log[c*x^n]))/2", //
				2350);
	}

	// {2354, 2438}
	public void test0082() {
		check(//
				"Integrate[(a + b*Log[c*x^n])/(d + e*x), x]", //
				"((a + b*Log[c*x^n])*Log[1 + (e*x)/d])/e + (b*n*PolyLog[2, -((e*x)/d)])/e", //
				2354, 2438);
	}

	// {2379, 2438}
	public void test0083() {
		check(//
				"Integrate[(a + b*Log[c*x^n])/(x*(d + e*x)), x]", //
				"-((Log[1 + d/(e*x)]*(a + b*Log[c*x^n]))/d) + (b*n*PolyLog[2, -(d/(e*x))])/d", //
				2379, 2438);
	}

	// {2351, 31}
	public void test0084() {
		check(//
				"Integrate[(a + b*Log[c*x^n])/(d + e*x)^2, x]", //
				"(x*(a + b*Log[c*x^n]))/(d*(d + e*x)) - (b*n*Log[d + e*x])/(d*e)", //
				2351, 31);
	}

	// {2356, 67}
	public void test0085() {
		check(//
				"Integrate[(a + b*x)^m*Log[c*x^n], x]", //
				"(n*(a + b*x)^(2 + m)*Hypergeometric2F1[1, 2 + m, 3 + m, 1 + (b*x)/a])/(a*b*(2 + 3*m + m^2)) + ((a + b*x)^(1 + m)*Log[c*x^n])/(b*(1 + m))", //
				2356, 67);
	}

	// {2371}
	public void test0086() {
		check(//
				"Integrate[x^5*(d + e*x^2)*(a + b*Log[c*x^n]), x]", //
				"-(b*d*n*x^6)/36 - (b*e*n*x^8)/64 + ((4*d*x^6 + 3*e*x^8)*(a + b*Log[c*x^n]))/24", //
				2371);
	}

	// {2371}
	public void test0087() {
		check(//
				"Integrate[x^3*(d + e*x^2)*(a + b*Log[c*x^n]), x]", //
				"-(b*d*n*x^4)/16 - (b*e*n*x^6)/36 + ((3*d*x^4 + 2*e*x^6)*(a + b*Log[c*x^n]))/12", //
				2371);
	}

	// {2371}
	public void test0088() {
		check(//
				"Integrate[x^4*(d + e*x^2)*(a + b*Log[c*x^n]), x]", //
				"-(b*d*n*x^5)/25 - (b*e*n*x^7)/49 + ((7*d*x^5 + 5*e*x^7)*(a + b*Log[c*x^n]))/35", //
				2371);
	}

	// {2371}
	public void test0089() {
		check(//
				"Integrate[x^2*(d + e*x^2)*(a + b*Log[c*x^n]), x]", //
				"-(b*d*n*x^3)/9 - (b*e*n*x^5)/25 + ((5*d*x^3 + 3*e*x^5)*(a + b*Log[c*x^n]))/15", //
				2371);
	}

	// {2350}
	public void test0090() {
		check(//
				"Integrate[(d + e*x^2)*(a + b*Log[c*x^n]), x]", //
				"-(b*d*n*x) - (b*e*n*x^3)/9 + d*x*(a + b*Log[c*x^n]) + (e*x^3*(a + b*Log[c*x^n]))/3", //
				2350);
	}

	// {2372}
	public void test0091() {
		check(//
				"Integrate[((d + e*x^2)*(a + b*Log[c*x^n]))/x^2, x]", //
				"-((b*d*n)/x) - b*e*n*x - (d*(a + b*Log[c*x^n]))/x + e*x*(a + b*Log[c*x^n])", //
				2372);
	}

	// {2371}
	public void test0092() {
		check(//
				"Integrate[x^4*(d + e*x^2)^2*(a + b*Log[c*x^n]), x]", //
				"-(b*d^2*n*x^5)/25 - (2*b*d*e*n*x^7)/49 - (b*e^2*n*x^9)/81 + ((63*d^2*x^5 + 90*d*e*x^7 + 35*e^2*x^9)*(a + b*Log[c*x^n]))/315", //
				2371);
	}

	// {2371}
	public void test0093() {
		check(//
				"Integrate[x^2*(d + e*x^2)^2*(a + b*Log[c*x^n]), x]", //
				"-(b*d^2*n*x^3)/9 - (2*b*d*e*n*x^5)/25 - (b*e^2*n*x^7)/49 + ((35*d^2*x^3 + 42*d*e*x^5 + 15*e^2*x^7)*(a + b*Log[c*x^n]))/105", //
				2371);
	}

	// {2350}
	public void test0094() {
		check(//
				"Integrate[(d + e*x^2)^2*(a + b*Log[c*x^n]), x]", //
				"-(b*d^2*n*x) - (2*b*d*e*n*x^3)/9 - (b*e^2*n*x^5)/25 + d^2*x*(a + b*Log[c*x^n]) + (2*d*e*x^3*(a + b*Log[c*x^n]))/3 + (e^2*x^5*(a + b*Log[c*x^n]))/5", //
				2350);
	}

	// {2372}
	public void test0095() {
		check(//
				"Integrate[((d + e*x^2)^2*(a + b*Log[c*x^n]))/x^2, x]", //
				"-((b*d^2*n)/x) - 2*b*d*e*n*x - (b*e^2*n*x^3)/9 - (d^2*(a + b*Log[c*x^n]))/x + 2*d*e*x*(a + b*Log[c*x^n]) + (e^2*x^3*(a + b*Log[c*x^n]))/3", //
				2372);
	}

	// {2372}
	public void test0096() {
		check(//
				"Integrate[((d + e*x^2)^2*(a + b*Log[c*x^n]))/x^4, x]", //
				"-(b*d^2*n)/(9*x^3) - (2*b*d*e*n)/x - b*e^2*n*x - (d^2*(a + b*Log[c*x^n]))/(3*x^3) - (2*d*e*(a + b*Log[c*x^n]))/x + e^2*x*(a + b*Log[c*x^n])", //
				2372);
	}

	// {2371}
	public void test0097() {
		check(//
				"Integrate[x^4*(d + e*x^2)^3*(a + b*Log[c*x^n]), x]", //
				"-(b*d^3*n*x^5)/25 - (3*b*d^2*e*n*x^7)/49 - (b*d*e^2*n*x^9)/27 - (b*e^3*n*x^11)/121 + ((231*d^3*x^5 + 495*d^2*e*x^7 + 385*d*e^2*x^9 + 105*e^3*x^11)*(a + b*Log[c*x^n]))/1155", //
				2371);
	}

	// {2371}
	public void test0098() {
		check(//
				"Integrate[x^2*(d + e*x^2)^3*(a + b*Log[c*x^n]), x]", //
				"-(b*d^3*n*x^3)/9 - (3*b*d^2*e*n*x^5)/25 - (3*b*d*e^2*n*x^7)/49 - (b*e^3*n*x^9)/81 + ((105*d^3*x^3 + 189*d^2*e*x^5 + 135*d*e^2*x^7 + 35*e^3*x^9)*(a + b*Log[c*x^n]))/315", //
				2371);
	}

	// {2350}
	public void test0099() {
		check(//
				"Integrate[(d + e*x^2)^3*(a + b*Log[c*x^n]), x]", //
				"-(b*d^3*n*x) - (b*d^2*e*n*x^3)/3 - (3*b*d*e^2*n*x^5)/25 - (b*e^3*n*x^7)/49 + d^3*x*(a + b*Log[c*x^n]) + d^2*e*x^3*(a + b*Log[c*x^n]) + (3*d*e^2*x^5*(a + b*Log[c*x^n]))/5 + (e^3*x^7*(a + b*Log[c*x^n]))/7", //
				2350);
	}

	// {2372}
	public void test0100() {
		check(//
				"Integrate[((d + e*x^2)^3*(a + b*Log[c*x^n]))/x^2, x]", //
				"-((b*d^3*n)/x) - 3*b*d^2*e*n*x - (b*d*e^2*n*x^3)/3 - (b*e^3*n*x^5)/25 - (d^3*(a + b*Log[c*x^n]))/x + 3*d^2*e*x*(a + b*Log[c*x^n]) + d*e^2*x^3*(a + b*Log[c*x^n]) + (e^3*x^5*(a + b*Log[c*x^n]))/5", //
				2372);
	}

	// {2372}
	public void test0101() {
		check(//
				"Integrate[((d + e*x^2)^3*(a + b*Log[c*x^n]))/x^6, x]", //
				"-(b*d^3*n)/(25*x^5) - (b*d^2*e*n)/(3*x^3) - (3*b*d*e^2*n)/x - b*e^3*n*x - (d^3*(a + b*Log[c*x^n]))/(5*x^5) - (d^2*e*(a + b*Log[c*x^n]))/x^3 - (3*d*e^2*(a + b*Log[c*x^n]))/x + e^3*x*(a + b*Log[c*x^n])", //
				2372);
	}

	// {2375, 2438}
	public void test0102() {
		check(//
				"Integrate[(x*(a + b*Log[c*x^n]))/(d + e*x^2), x]", //
				"((a + b*Log[c*x^n])*Log[1 + (e*x^2)/d])/(2*e) + (b*n*PolyLog[2, -((e*x^2)/d)])/(4*e)", //
				2375, 2438);
	}

	// {2379, 2438}
	public void test0103() {
		check(//
				"Integrate[(a + b*Log[c*x^n])/(x*(d + e*x^2)), x]", //
				"-(Log[1 + d/(e*x^2)]*(a + b*Log[c*x^n]))/(2*d) + (b*n*PolyLog[2, -(d/(e*x^2))])/(4*d)", //
				2379, 2438);
	}

	// {2373, 266}
	public void test0104() {
		check(//
				"Integrate[(x*(a + b*Log[c*x^n]))/(d + e*x^2)^2, x]", //
				"(x^2*(a + b*Log[c*x^n]))/(2*d*(d + e*x^2)) - (b*n*Log[d + e*x^2])/(4*d*e)", //
				2373, 266);
	}

	// {2374, 2352}
	public void test0105() {
		check(//
				"Integrate[(x*Log[c*x^2])/(1 - c*x^2), x]", //
				"PolyLog[2, 1 - c*x^2]/(2*c)", //
				2374, 2352);
	}

	// {2374, 2352}
	public void test0106() {
		check(//
				"Integrate[(x*Log[x^2/c])/(c - x^2), x]", //
				"PolyLog[2, 1 - x^2/c]/2", //
				2374, 2352);
	}

	// {2361, 6031}
	public void test0107() {
		check(//
				"Integrate[Log[x]/(1 - x^2), x]", //
				"ArcTanh[x]*Log[x] + PolyLog[2, -x]/2 - PolyLog[2, x]/2", //
				2361, 6031);
	}

	// {2375, 2438}
	public void test0108() {
		check(//
				"Integrate[(a + b*Log[c*x^n])/((d + e/x)*x^2), x]", //
				"-((Log[1 + e/(d*x)]*(a + b*Log[c*x^n]))/e) + (b*n*PolyLog[2, -(e/(d*x))])/e", //
				2375, 2438);
	}

	// {2375, 2438}
	public void test0109() {
		check(//
				"Integrate[(a + b*Log[c*x])/((d + e/x)*x^2), x]", //
				"-((Log[1 + e/(d*x)]*(a + b*Log[c*x]))/e) + (b*PolyLog[2, -(e/(d*x))])/e", //
				2375, 2438);
	}

	// {2374, 2352}
	public void test0110() {
		check(//
				"Integrate[(x^(-1 + n)*Log[e*x^n])/(1 - e*x^n), x]", //
				"PolyLog[2, 1 - e*x^n]/(e*n)", //
				2374, 2352);
	}

	// {2374, 2352}
	public void test0111() {
		check(//
				"Integrate[(x^(-1 + n)*Log[x^n/d])/(d - x^n), x]", //
				"PolyLog[2, 1 - x^n/d]/n", //
				2374, 2352);
	}

	// {2374, 2352}
	public void test0112() {
		check(//
				"Integrate[(x^(-1 + n)*Log[-((e*x^n)/d)])/(d + e*x^n), x]", //
				"-(PolyLog[2, 1 + (e*x^n)/d]/(e*n))", //
				2374, 2352);
	}

	// {2342, 2341}
	public void test0113() {
		check(//
				"Integrate[(f*x)^(-1 + m)*(a + b*Log[c*x^n])^2, x]", //
				"(2*b^2*n^2*(f*x)^m)/(f*m^3) - (2*b*n*(f*x)^m*(a + b*Log[c*x^n]))/(f*m^2) + ((f*x)^m*(a + b*Log[c*x^n])^2)/(f*m)", //
				2342, 2341);
	}

	// {2350}
	public void test0114() {
		check(//
				"Integrate[(d + e*x^r)^2*(a + b*Log[c*x^n]), x]", //
				"-(b*d^2*n*x) - (2*b*d*e*n*x^(1 + r))/(1 + r)^2 - (b*e^2*n*x^(1 + 2*r))/(1 + 2*r)^2 + d^2*x*(a + b*Log[c*x^n]) + (2*d*e*x^(1 + r)*(a + b*Log[c*x^n]))/(1 + r) + (e^2*x^(1 + 2*r)*(a + b*Log[c*x^n]))/(1 + 2*r)", //
				2350);
	}

	// {2350}
	public void test0115() {
		check(//
				"Integrate[(d + e*x^r)^3*(a + b*Log[c*x^n]), x]", //
				"-(b*d^3*n*x) - (3*b*d^2*e*n*x^(1 + r))/(1 + r)^2 - (3*b*d*e^2*n*x^(1 + 2*r))/(1 + 2*r)^2 - (b*e^3*n*x^(1 + 3*r))/(1 + 3*r)^2 + d^3*x*(a + b*Log[c*x^n]) + (3*d^2*e*x^(1 + r)*(a + b*Log[c*x^n]))/(1 + r) + (3*d*e^2*x^(1 + 2*r)*(a + b*Log[c*x^n]))/(1 + 2*r) + (e^3*x^(1 + 3*r)*(a + b*Log[c*x^n]))/(1 + 3*r)", //
				2350);
	}

	// {2379, 2438}
	public void test0116() {
		check(//
				"Integrate[(a + b*Log[c*x^n])/(x*(d + e*x^r)), x]", //
				"-(((a + b*Log[c*x^n])*Log[1 + d/(e*x^r)])/(d*r)) + (b*n*PolyLog[2, -(d/(e*x^r))])/(d*r^2)", //
				2379, 2438);
	}

	// {2379, 2438}
	public void test0117() {
		check(//
				"Integrate[(a + b*Log[c*x^n])/(x*(d + e*x^r)), x]", //
				"-(((a + b*Log[c*x^n])*Log[1 + d/(e*x^r)])/(d*r)) + (b*n*PolyLog[2, -(d/(e*x^r))])/(d*r^2)", //
				2379, 2438);
	}

	// {2347, 2212}
	public void test0118() {
		check(//
				"Integrate[(f*x)^m*(a + b*Log[c*x^n])^p, x]", //
				"((f*x)^(1 + m)*Gamma[1 + p, -(((1 + m)*(a + b*Log[c*x^n]))/(b*n))]*(a + b*Log[c*x^n])^p)/(E^((a*(1 + m))/(b*n))*f*(1 + m)*(c*x^n)^((1 + m)/n)*(-(((1 + m)*(a + b*Log[c*x^n]))/(b*n)))^p)", //
				2347, 2212);
	}

	// {2421, 6724}
	public void test0119() {
		check(//
				"Integrate[((a + b*Log[c*x^n])*Log[1 + e*x])/x, x]", //
				"-((a + b*Log[c*x^n])*PolyLog[2, -(e*x)]) + b*n*PolyLog[3, -(e*x)]", //
				2421, 6724);
	}

	// {2421, 6724}
	public void test0120() {
		check(//
				"Integrate[((a + b*Log[c*x^n])*Log[d*(d^(-1) + f*x^2)])/x, x]", //
				"-((a + b*Log[c*x^n])*PolyLog[2, -(d*f*x^2)])/2 + (b*n*PolyLog[3, -(d*f*x^2)])/4", //
				2421, 6724);
	}

	// {2421, 6724}
	public void test0121() {
		check(//
				"Integrate[(Log[d*(d^(-1) + f*Sqrt[x])]*(a + b*Log[c*x^n]))/x, x]", //
				"-2*(a + b*Log[c*x^n])*PolyLog[2, -(d*f*Sqrt[x])] + 4*b*n*PolyLog[3, -(d*f*Sqrt[x])]", //
				2421, 6724);
	}

	// {2421, 6724}
	public void test0122() {
		check(//
				"Integrate[((a + b*Log[c*x^n])*Log[d*(d^(-1) + f*x^m)])/x, x]", //
				"-(((a + b*Log[c*x^n])*PolyLog[2, -(d*f*x^m)])/m) + (b*n*PolyLog[3, -(d*f*x^m)])/m^2", //
				2421, 6724);
	}

	// {2413, 2341}
	public void test0123() {
		check(//
				"Integrate[((a + b*Log[c*x^n])*(d + e*Log[f*x^r]))/x^2, x]", //
				"-((b*e*n*r)/x) - (e*r*(a + b*n + b*Log[c*x^n]))/x - (b*n*(d + e*Log[f*x^r]))/x - ((a + b*Log[c*x^n])*(d + e*Log[f*x^r]))/x", //
				2413, 2341);
	}

	// {2413, 2601}
	public void test0124() {
		check(//
				"Integrate[(a + b*Log[c*x^n])/(x*Log[x]), x]", //
				"b*n*Log[x] - b*n*Log[x]*Log[Log[x]] + (a + b*Log[c*x^n])*Log[Log[x]]", //
				2413, 2601);
	}

	// {2430, 6724}
	public void test0125() {
		check(//
				"Integrate[((a + b*Log[c*x^n])*PolyLog[k, e*x^q])/x, x]", //
				"((a + b*Log[c*x^n])*PolyLog[1 + k, e*x^q])/q - (b*n*PolyLog[2 + k, e*x^q])/q^2", //
				2430, 6724);
	}

	// {2430, 6724}
	public void test0127() {
		check(//
				"Integrate[(Log[x]*PolyLog[n, a*x])/x, x]", //
				"Log[x]*PolyLog[1 + n, a*x] - PolyLog[2 + n, a*x]", //
				2430, 6724);
	}

	// {2431}
	public void test0128() {
		check(//
				"Integrate[(q*PolyLog[-1 + k, e*x^q])/(b*n*x*(a + b*Log[c*x^n])) - PolyLog[k, e*x^q]/(x*(a + b*Log[c*x^n])^2), x]", //
				"PolyLog[k, e*x^q]/(b*n*(a + b*Log[c*x^n]))", //
				2431);
	}

	// {2430, 6724}
	public void test0129() {
		check(//
				"Integrate[((a + b*Log[c*x^n])*PolyLog[2, e*x])/x, x]", //
				"(a + b*Log[c*x^n])*PolyLog[3, e*x] - b*n*PolyLog[4, e*x]", //
				2430, 6724);
	}

	// {2430, 6724}
	public void test0130() {
		check(//
				"Integrate[((a + b*Log[c*x^n])*PolyLog[3, e*x])/x, x]", //
				"(a + b*Log[c*x^n])*PolyLog[4, e*x] - b*n*PolyLog[5, e*x]", //
				2430, 6724);
	}

	// {2495, 2341}
	public void test0131() {
		check(//
				"Integrate[x^2*Log[c*(b*x^n)^p], x]", //
				"-(n*p*x^3)/9 + (x^3*Log[c*(b*x^n)^p])/3", //
				2495, 2341);
	}

	// {2495, 2341}
	public void test0132() {
		check(//
				"Integrate[x*Log[c*(b*x^n)^p], x]", //
				"-(n*p*x^2)/4 + (x^2*Log[c*(b*x^n)^p])/2", //
				2495, 2341);
	}

	// {2495, 2332}
	public void test0133() {
		check(//
				"Integrate[Log[c*(b*x^n)^p], x]", //
				"-(n*p*x) + x*Log[c*(b*x^n)^p]", //
				2495, 2332);
	}

	// {2495, 2338}
	public void test0134() {
		check(//
				"Integrate[Log[c*(b*x^n)^p]/x, x]", //
				"Log[c*(b*x^n)^p]^2/(2*n*p)", //
				2495, 2338);
	}

	// {2495, 2341}
	public void test0135() {
		check(//
				"Integrate[Log[c*(b*x^n)^p]/x^2, x]", //
				"-((n*p)/x) - Log[c*(b*x^n)^p]/x", //
				2495, 2341);
	}

	// {2495, 2341}
	public void test0136() {
		check(//
				"Integrate[Log[c*(b*x^n)^p]/x^3, x]", //
				"-(n*p)/(4*x^2) - Log[c*(b*x^n)^p]/(2*x^2)", //
				2495, 2341);
	}

	// {2495, 2341}
	public void test0137() {
		check(//
				"Integrate[Log[c*(b*x^n)^p]/x^4, x]", //
				"-(n*p)/(9*x^3) - Log[c*(b*x^n)^p]/(3*x^3)", //
				2495, 2341);
	}

	// {2495, 2341}
	public void test0138() {
		check(//
				"Integrate[(e*x)^q*(a + b*Log[c*(d*x^m)^n]), x]", //
				"-((b*m*n*(e*x)^(1 + q))/(e*(1 + q)^2)) + ((e*x)^(1 + q)*(a + b*Log[c*(d*x^m)^n]))/(e*(1 + q))", //
				2495, 2341);
	}

	// {2562, 2341}
	public void test0139() {
		check(//
				"Integrate[((c*i + d*i*x)*(A + B*Log[(e*(a + b*x))/(c + d*x)]))/(a*g + b*g*x)^3, x]", //
				"-(B*i*(c + d*x)^2)/(4*(b*c - a*d)*g^3*(a + b*x)^2) - (i*(c + d*x)^2*(A + B*Log[(e*(a + b*x))/(c + d*x)]))/(2*(b*c - a*d)*g^3*(a + b*x)^2)", //
				2562, 2341);
	}

	// {2562, 2341}
	public void test0140() {
		check(//
				"Integrate[((c*i + d*i*x)^2*(A + B*Log[(e*(a + b*x))/(c + d*x)]))/(a*g + b*g*x)^4, x]", //
				"-(B*i^2*(c + d*x)^3)/(9*(b*c - a*d)*g^4*(a + b*x)^3) - (i^2*(c + d*x)^3*(A + B*Log[(e*(a + b*x))/(c + d*x)]))/(3*(b*c - a*d)*g^4*(a + b*x)^3)", //
				2562, 2341);
	}

	// {2562, 2341}
	public void test0141() {
		check(//
				"Integrate[((c*i + d*i*x)^3*(A + B*Log[(e*(a + b*x))/(c + d*x)]))/(a*g + b*g*x)^5, x]", //
				"-(B*i^3*(c + d*x)^4)/(16*(b*c - a*d)*g^5*(a + b*x)^4) - (i^3*(c + d*x)^4*(A + B*Log[(e*(a + b*x))/(c + d*x)]))/(4*(b*c - a*d)*g^5*(a + b*x)^4)", //
				2562, 2341);
	}

	// {2562, 2338}
	public void test0142() {
		check(//
				"Integrate[(A + B*Log[(e*(a + b*x))/(c + d*x)])/((a*g + b*g*x)*(c*i + d*i*x)), x]", //
				"(A + B*Log[(e*(a + b*x))/(c + d*x)])^2/(2*B*(b*c - a*d)*g*i)", //
				2562, 2338);
	}

	// {2562, 2341}
	public void test0143() {
		check(//
				"Integrate[((a*g + b*g*x)*(A + B*Log[(e*(a + b*x))/(c + d*x)]))/(c*i + d*i*x)^3, x]", //
				"-(B*g*(a + b*x)^2)/(4*(b*c - a*d)*i^3*(c + d*x)^2) + (g*(a + b*x)^2*(A + B*Log[(e*(a + b*x))/(c + d*x)]))/(2*(b*c - a*d)*i^3*(c + d*x)^2)", //
				2562, 2341);
	}

	// {2561, 2341}
	public void test0144() {
		check(//
				"Integrate[((c*i + d*i*x)*(A + B*Log[e*((a + b*x)/(c + d*x))^n]))/(a*g + b*g*x)^3, x]", //
				"-(B*i*n*(c + d*x)^2)/(4*(b*c - a*d)*g^3*(a + b*x)^2) - (i*(c + d*x)^2*(A + B*Log[e*((a + b*x)/(c + d*x))^n]))/(2*(b*c - a*d)*g^3*(a + b*x)^2)", //
				2561, 2341);
	}

	// {2561, 2341}
	public void test0145() {
		check(//
				"Integrate[((c*i + d*i*x)^2*(A + B*Log[e*((a + b*x)/(c + d*x))^n]))/(a*g + b*g*x)^4, x]", //
				"-(B*i^2*n*(c + d*x)^3)/(9*(b*c - a*d)*g^4*(a + b*x)^3) - (i^2*(c + d*x)^3*(A + B*Log[e*((a + b*x)/(c + d*x))^n]))/(3*(b*c - a*d)*g^4*(a + b*x)^3)", //
				2561, 2341);
	}

	// {2561, 2338}
	public void test0146() {
		check(//
				"Integrate[(A + B*Log[e*((a + b*x)/(c + d*x))^n])/((a*g + b*g*x)*(c*i + d*i*x)), x]", //
				"(A + B*Log[e*((a + b*x)/(c + d*x))^n])^2/(2*B*(b*c - a*d)*g*i*n)", //
				2561, 2338);
	}

	// {2561, 2341}
	public void test0147() {
		check(//
				"Integrate[((a*g + b*g*x)*(A + B*Log[e*((a + b*x)/(c + d*x))^n]))/(c*i + d*i*x)^3, x]", //
				"-(B*g*n*(a + b*x)^2)/(4*(b*c - a*d)*i^3*(c + d*x)^2) + (g*(a + b*x)^2*(A + B*Log[e*((a + b*x)/(c + d*x))^n]))/(2*(b*c - a*d)*i^3*(c + d*x)^2)", //
				2561, 2341);
	}

	// {2563, 2341}
	public void test0148() {
		check(//
				"Integrate[(a*g + b*g*x)^m*(c*i + d*i*x)^(-2 - m)*(A + B*Log[e*((a + b*x)/(c + d*x))^n]), x]", //
				"-((B*n*(a + b*x)*(g*(a + b*x))^m)/((b*c - a*d)*i^2*(1 + m)^2*(c + d*x)*(i*(c + d*x))^m)) + ((a + b*x)*(g*(a + b*x))^m*(A + B*Log[e*((a + b*x)/(c + d*x))^n]))/((b*c - a*d)*i^2*(1 + m)*(c + d*x)*(i*(c + d*x))^m)", //
				2563, 2341);
	}

	// {2563, 2341}
	public void test0149() {
		check(//
				"Integrate[(a*g + b*g*x)^(-2 - m)*(c*i + d*i*x)^m*(A + B*Log[e*((a + b*x)/(c + d*x))^n]), x]", //
				"-((B*n*(a + b*x)*(g*(a + b*x))^(-2 - m)*(i*(c + d*x))^(2 + m))/((b*c - a*d)*i^2*(1 + m)^2*(c + d*x))) - ((a + b*x)*(g*(a + b*x))^(-2 - m)*(i*(c + d*x))^(2 + m)*(A + B*Log[e*((a + b*x)/(c + d*x))^n]))/((b*c - a*d)*i^2*(1 + m)*(c + d*x))", //
				2563, 2341);
	}

	// {2565, 2352}
	public void test0150() {
		check(//
				"Integrate[Log[(c + d*x)/(a + b*x)]/((a + b*x)*((a - c)*h + (b - d)*h*x)), x]", //
				"-(PolyLog[2, 1 - (c + d*x)/(a + b*x)]/((b*c - a*d)*h))", //
				2565, 2352);
	}

	// {2565, 2352}
	public void test0151() {
		check(//
				"Integrate[Log[(a - c*g + (b - d*g)*x)/(a + b*x)]/((a + b*x)*(c + d*x)), x]", //
				"PolyLog[2, (g*(c + d*x))/(a + b*x)]/(b*c - a*d)", //
				2565, 2352);
	}

	// {2573, 6818}
	public void test0152() {
		check(//
				"Integrate[(a + b*Log[Sqrt[1 - c*x]/Sqrt[1 + c*x]])^n/(1 - c^2*x^2), x]", //
				"-((a + b*Log[Sqrt[1 - c*x]/Sqrt[1 + c*x]])^(1 + n)/(b*c*(1 + n)))", //
				2573, 6818);
	}

	// {2573, 6816}
	public void test0153() {
		check(//
				"Integrate[1/((1 - c^2*x^2)*(a + b*Log[Sqrt[1 - c*x]/Sqrt[1 + c*x]])), x]", //
				"-(Log[a + b*Log[Sqrt[1 - c*x]/Sqrt[1 + c*x]]]/(b*c))", //
				2573, 6816);
	}

	// {2573, 6818}
	public void test0154() {
		check(//
				"Integrate[1/((1 - c^2*x^2)*(a + b*Log[Sqrt[1 - c*x]/Sqrt[1 + c*x]])^2), x]", //
				"1/(b*c*(a + b*Log[Sqrt[1 - c*x]/Sqrt[1 + c*x]]))", //
				2573, 6818);
	}

	// {2573, 6818}
	public void test0155() {
		check(//
				"Integrate[1/((1 - c^2*x^2)*(a + b*Log[Sqrt[1 - c*x]/Sqrt[1 + c*x]])^3), x]", //
				"1/(2*b*c*(a + b*Log[Sqrt[1 - c*x]/Sqrt[1 + c*x]])^2)", //
				2573, 6818);
	}

	// {2536, 31}
	public void test0159() {
		check(//
				"Integrate[Log[(c*(b + a*x)^2)/x^2], x]", //
				"(2*b*Log[b + a*x])/a + x*Log[(c*(b + a*x)^2)/x^2]", //
				2536, 31);
	}

	// {2536, 31}
	public void test0160() {
		check(//
				"Integrate[Log[(c*x^2)/(b + a*x)^2], x]", //
				"x*Log[(c*x^2)/(b + a*x)^2] - (2*b*Log[b + a*x])/a", //
				2536, 31);
	}

	// {2588, 6745}
	public void test0161() {
		check(//
				"Integrate[(Log[(-(b*c) + a*d)/(d*(a + b*x))]*Log[(e*(c + d*x))/(a + b*x)])/((a + b*x)*(c + d*x)), x]", //
				"(Log[(e*(c + d*x))/(a + b*x)]*PolyLog[2, 1 + (b*c - a*d)/(d*(a + b*x))])/(b*c - a*d) - PolyLog[3, 1 + (b*c - a*d)/(d*(a + b*x))]/(b*c - a*d)", //
				2588, 6745);
	}

	// {2588, 6745}
	public void test0162() {
		check(//
				"Integrate[(Log[(e*(c + d*x))/(a + b*x)]*Log[((-(b*c) + a*d)*(e + f*x))/((d*e - c*f)*(a + b*x))])/((a + b*x)*(c + d*x)), x]", //
				"(Log[(e*(c + d*x))/(a + b*x)]*PolyLog[2, 1 + ((b*c - a*d)*(e + f*x))/((d*e - c*f)*(a + b*x))])/(b*c - a*d) - PolyLog[3, 1 + ((b*c - a*d)*(e + f*x))/((d*e - c*f)*(a + b*x))]/(b*c - a*d)", //
				2588, 6745);
	}

	// {2436, 2332}
	public void test0163() {
		check(//
				"Integrate[Log[c*(d + e*x)], x]", //
				"-x + ((d + e*x)*Log[c*(d + e*x)])/e", //
				2436, 2332);
	}

	// {2436, 2335}
	public void test0164() {
		check(//
				"Integrate[Log[c*(d + e*x)]^(-1), x]", //
				"LogIntegral[c*(d + e*x)]/(c*e)", //
				2436, 2335);
	}

	// {2436, 2332}
	public void test0165() {
		check(//
				"Integrate[Log[a + b*x], x]", //
				"-x + ((a + b*x)*Log[a + b*x])/b", //
				2436, 2332);
	}

	// {2436, 2332}
	public void test0166() {
		check(//
				"Integrate[Log[c*(d + e*x)^n], x]", //
				"-(n*x) + ((d + e*x)*Log[c*(d + e*x)^n])/e", //
				2436, 2332);
	}

	// {2440, 2438}
	public void test0167() {
		check(//
				"Integrate[Log[-((g*(d + e*x))/(e*f - d*g))]/(f + g*x), x]", //
				"-(PolyLog[2, (e*(f + g*x))/(e*f - d*g)]/g)", //
				2440, 2438);
	}

	// {2439, 2438}
	public void test0168() {
		check(//
				"Integrate[(a + b*Log[c*(c^(-1) + e*x)])/x, x]", //
				"a*Log[x] - b*PolyLog[2, -(c*e*x)]", //
				2439, 2438);
	}

	// {2439, 2438}
	public void test0169() {
		check(//
				"Integrate[Log[3 + e*x]/x, x]", //
				"Log[3]*Log[x] - PolyLog[2, -(e*x)/3]", //
				2439, 2438);
	}

	// {2439, 2438}
	public void test0170() {
		check(//
				"Integrate[Log[2 + e*x]/x, x]", //
				"Log[2]*Log[x] - PolyLog[2, -(e*x)/2]", //
				2439, 2438);
	}

	// {2441, 2352}
	public void test0171() {
		check(//
				"Integrate[Log[-1 + e*x]/x, x]", //
				"Log[e*x]*Log[-1 + e*x] + PolyLog[2, 1 - e*x]", //
				2441, 2352);
	}

	// {2441, 2352}
	public void test0172() {
		check(//
				"Integrate[Log[-2 + e*x]/x, x]", //
				"Log[(e*x)/2]*Log[-2 + e*x] + PolyLog[2, 1 - (e*x)/2]", //
				2441, 2352);
	}

	// {2439, 2438}
	public void test0173() {
		check(//
				"Integrate[(a + b*Log[3 + e*x])/x, x]", //
				"(a + b*Log[3])*Log[x] - b*PolyLog[2, -(e*x)/3]", //
				2439, 2438);
	}

	// {2439, 2438}
	public void test0174() {
		check(//
				"Integrate[(a + b*Log[2 + e*x])/x, x]", //
				"(a + b*Log[2])*Log[x] - b*PolyLog[2, -(e*x)/2]", //
				2439, 2438);
	}

	// {2439, 2438}
	public void test0175() {
		check(//
				"Integrate[(a + b*Log[1 + e*x])/x, x]", //
				"a*Log[x] - b*PolyLog[2, -(e*x)]", //
				2439, 2438);
	}

	// {2441, 2352}
	public void test0176() {
		check(//
				"Integrate[(a + b*Log[-1 + e*x])/x, x]", //
				"Log[e*x]*(a + b*Log[-1 + e*x]) + b*PolyLog[2, 1 - e*x]", //
				2441, 2352);
	}

	// {2441, 2352}
	public void test0177() {
		check(//
				"Integrate[(a + b*Log[-2 + e*x])/x, x]", //
				"Log[(e*x)/2]*(a + b*Log[-2 + e*x]) + b*PolyLog[2, 1 - (e*x)/2]", //
				2441, 2352);
	}

	// {2442, 70}
	public void test0178() {
		check(//
				"Integrate[(f + g*x)^m*(a + b*Log[c*(d + e*x)^n]), x]", //
				"(b*e*n*(f + g*x)^(2 + m)*Hypergeometric2F1[1, 2 + m, 3 + m, (e*(f + g*x))/(e*f - d*g)])/(g*(e*f - d*g)*(1 + m)*(2 + m)) + ((f + g*x)^(1 + m)*(a + b*Log[c*(d + e*x)^n]))/(g*(1 + m))", //
				2442, 70);
	}

	// {2449, 2352}
	public void test0183() {
		check(//
				"Integrate[Log[(2*e)/(e + f*x)]/(e^2 - f^2*x^2), x]", //
				"PolyLog[2, 1 - (2*e)/(e + f*x)]/(2*e*f)", //
				2449, 2352);
	}

	// {2449, 2352}
	public void test0185() {
		check(//
				"Integrate[Log[(2*a)/(a + b*x)]/(a^2 - b^2*x^2), x]", //
				"PolyLog[2, 1 - (2*a)/(a + b*x)]/(2*a*b)", //
				2449, 2352);
	}

	// {2565, 2352}
	public void test0186() {
		check(//
				"Integrate[Log[(a*(1 - c) + b*(1 + c)*x)/(a + b*x)]/((a - b*x)*(a + b*x)), x]", //
				"PolyLog[2, (c*(a - b*x))/(a + b*x)]/(2*a*b)", //
				2565, 2352);
	}

	// {2505, 211}
	public void test0188() {
		check(//
				"Integrate[Log[c*(a + b*x^2)^p]/x^2, x]", //
				"(2*Sqrt[b]*p*ArcTan[(Sqrt[b]*x)/Sqrt[a]])/Sqrt[a] - Log[c*(a + b*x^2)^p]/x", //
				2505, 211);
	}

	// {2442, 66}
	public void test0189() {
		check(//
				"Integrate[(f*x)^m*Log[c*(d + e*x)^p], x]", //
				"-((e*p*(f*x)^(2 + m)*Hypergeometric2F1[1, 2 + m, 3 + m, -((e*x)/d)])/(d*f^2*(1 + m)*(2 + m))) + ((f*x)^(1 + m)*Log[c*(d + e*x)^p])/(f*(1 + m))", //
				2442, 66);
	}

	// {2505, 371}
	public void test0190() {
		check(//
				"Integrate[x^2*Log[c*(d + e*x^n)^p], x]", //
				"-(e*n*p*x^(3 + n)*Hypergeometric2F1[1, (3 + n)/n, 2 + 3/n, -((e*x^n)/d)])/(3*d*(3 + n)) + (x^3*Log[c*(d + e*x^n)^p])/3", //
				2505, 371);
	}

	// {2505, 371}
	public void test0191() {
		check(//
				"Integrate[x*Log[c*(d + e*x^n)^p], x]", //
				"-(e*n*p*x^(2 + n)*Hypergeometric2F1[1, (2 + n)/n, 2*(1 + n^(-1)), -((e*x^n)/d)])/(2*d*(2 + n)) + (x^2*Log[c*(d + e*x^n)^p])/2", //
				2505, 371);
	}

	// {2498, 371}
	public void test0192() {
		check(//
				"Integrate[Log[c*(d + e*x^n)^p], x]", //
				"-((e*n*p*x^(1 + n)*Hypergeometric2F1[1, 1 + n^(-1), 2 + n^(-1), -((e*x^n)/d)])/(d*(1 + n))) + x*Log[c*(d + e*x^n)^p]", //
				2498, 371);
	}

	// {2505, 371}
	public void test0193() {
		check(//
				"Integrate[Log[c*(d + e*x^n)^p]/x^2, x]", //
				"-((e*n*p*x^(-1 + n)*Hypergeometric2F1[1, -((1 - n)/n), 2 - n^(-1), -((e*x^n)/d)])/(d*(1 - n))) - Log[c*(d + e*x^n)^p]/x", //
				2505, 371);
	}

	// {2505, 371}
	public void test0194() {
		check(//
				"Integrate[Log[c*(d + e*x^n)^p]/x^3, x]", //
				"-(e*n*p*x^(-2 + n)*Hypergeometric2F1[1, -((2 - n)/n), 2*(1 - n^(-1)), -((e*x^n)/d)])/(2*d*(2 - n)) - Log[c*(d + e*x^n)^p]/(2*x^2)", //
				2505, 371);
	}

	// {2505, 371}
	public void test0195() {
		check(//
				"Integrate[Log[c*(d + e*x^n)^p]/x^4, x]", //
				"-(e*n*p*x^(-3 + n)*Hypergeometric2F1[1, -((3 - n)/n), 2 - 3/n, -((e*x^n)/d)])/(3*d*(3 - n)) - Log[c*(d + e*x^n)^p]/(3*x^3)", //
				2505, 371);
	}

	// {2436, 2332}
	public void test0196() {
		check(//
				"Integrate[Log[c*(a + b*x)^p], x]", //
				"-(p*x) + ((a + b*x)*Log[c*(a + b*x)^p])/b", //
				2436, 2332);
	}

	// {2442, 70}
	public void test0197() {
		check(//
				"Integrate[(d + e*x)^m*Log[c*(a + b*x)^p], x]", //
				"(b*p*(d + e*x)^(2 + m)*Hypergeometric2F1[1, 2 + m, 3 + m, (b*(d + e*x))/(b*d - a*e)])/(e*(b*d - a*e)*(1 + m)*(2 + m)) + ((d + e*x)^(1 + m)*Log[c*(a + b*x)^p])/(e*(1 + m))", //
				2442, 70);
	}

	// {2498, 371}
	public void test0198() {
		check(//
				"Integrate[Log[c*(d + e*x^n)^p], x]", //
				"-((e*n*p*x^(1 + n)*Hypergeometric2F1[1, 1 + n^(-1), 2 + n^(-1), -((e*x^n)/d)])/(d*(1 + n))) + x*Log[c*(d + e*x^n)^p]", //
				2498, 371);
	}

	// {2511, 2438}
	public void test0199() {
		check(//
				"Integrate[Log[(a + x^2)/x^2]/x, x]", //
				"PolyLog[2, -(a/x^2)]/2", //
				2511, 2438);
	}

	// {2511, 2438}
	public void test0200() {
		check(//
				"Integrate[Log[(a + x^n)/x^n]/x, x]", //
				"PolyLog[2, -(a/x^n)]/n", //
				2511, 2438);
	}

	// {2421, 6724}
	public void test0201() {
		check(//
				"Integrate[(Log[f*x^p]*Log[1 + e*x^m])/x, x]", //
				"-((Log[f*x^p]*PolyLog[2, -(e*x^m)])/m) + (p*PolyLog[3, -(e*x^m)])/m^2", //
				2421, 6724);
	}

	// {2339, 30}
	public void test0202() {
		check(//
				"Integrate[Log[c*x^n]^(-1 + q)/x, x]", //
				"Log[c*x^n]^q/(n*q)", //
				2339, 30);
	}

	// {2338}
	public void test0203() {
		check(//
				"Integrate[a + (2*b*n*Log[c*x^n])/x, x]", //
				"a*x + b*Log[c*x^n]^2", //
				2338);
	}

	// {2641, 2621}
	public void test0204() {
		check(//
				"Integrate[(a*x + 2*b*n*Log[c*x^n])/(a*x^2 + b*x*Log[c*x^n]^2), x]", //
				"Log[a*x + b*Log[c*x^n]^2]", //
				2641, 2621);
	}

	// {2641, 2621}
	public void test0205() {
		check(//
				"Integrate[(a*(-1 + m)*x^(-1 + m) + b*n*q*Log[c*x^n]^(-1 + q))/(a*x^m + b*x*Log[c*x^n]^q), x]", //
				"Log[a*x^(-1 + m) + b*Log[c*x^n]^q]", //
				2641, 2621);
	}

	// {2600, 2335}
	public void test0206() {
		check(//
				"Integrate[Log[c*Log[d*x]^p], x]", //
				"x*Log[c*Log[d*x]^p] - (p*LogIntegral[d*x])/d", //
				2600, 2335);
	}

	// {2317, 2438}
	public void test0207() {
		check(//
				"Integrate[Log[1 + e*(f^(c*(a + b*x)))^n], x]", //
				"-(PolyLog[2, -(e*(f^(c*(a + b*x)))^n)]/(b*c*n*Log[f]))", //
				2317, 2438);
	}

	// {2339, 29}
	public void test0208() {
		check(//
				"Integrate[1/(x*(3 + Log[x])), x]", //
				"Log[3 + Log[x]]", //
				2339, 29);
	}

	// {2339, 30}
	public void test0209() {
		check(//
				"Integrate[Sqrt[1 + Log[x]]/x, x]", //
				"(2*(1 + Log[x])^(3/2))/3", //
				2339, 30);
	}

	// {2339, 30}
	public void test0210() {
		check(//
				"Integrate[(1 + Log[x])^5/x, x]", //
				"(1 + Log[x])^6/6", //
				2339, 30);
	}

	// {2339, 30}
	public void test0211() {
		check(//
				"Integrate[1/(x*Sqrt[Log[x]]), x]", //
				"2*Sqrt[Log[x]]", //
				2339, 30);
	}

	// {209}
	public void test0212() {
		check(//
				"Integrate[1/(x*(1 + Log[x]^2)), x]", //
				"ArcTan[Log[x]]", //
				209);
	}

	// {222}
	public void test0213() {
		check(//
				"Integrate[1/(x*Sqrt[4 - 9*Log[x]^2]), x]", //
				"ArcSin[(3*Log[x])/2]/3", //
				222);
	}

	// {221}
	public void test0214() {
		check(//
				"Integrate[1/(x*Sqrt[4 + Log[x]^2]), x]", //
				"ArcSinh[Log[x]/2]", //
				221);
	}

	// {2338}
	public void test0215() {
		check(//
				"Integrate[Log[Log[6*x]]/(x*Log[6*x]), x]", //
				"Log[Log[6*x]]^2/2", //
				2338);
	}

	// {4423, 2338}
	public void test0216() {
		check(//
				"Integrate[Cot[x]*Log[Sin[x]], x]", //
				"Log[Sin[x]]^2/2", //
				4423, 2338);
	}

	// {2634, 2717}
	public void test0217() {
		check(//
				"Integrate[Cos[a + b*x]*Log[Cos[a/2 + (b*x)/2]*Sin[a/2 + (b*x)/2]], x]", //
				"-(Sin[a + b*x]/b) + (Log[Cos[a/2 + (b*x)/2]*Sin[a/2 + (b*x)/2]]*Sin[a + b*x])/b", //
				2634, 2717);
	}

	// {4424, 2338}
	public void test0218() {
		check(//
				"Integrate[Log[Cos[x]]*Tan[x], x]", //
				"-Log[Cos[x]]^2/2", //
				4424, 2338);
	}

	// {2634, 2718}
	public void test0219() {
		check(//
				"Integrate[Log[Cos[x]]*Sin[x], x]", //
				"Cos[x] - Cos[x]*Log[Cos[x]]", //
				2634, 2718);
	}

	// {2634, 2717}
	public void test0220() {
		check(//
				"Integrate[Cos[x]*Log[Sin[x]], x]", //
				"-Sin[x] + Log[Sin[x]]*Sin[x]", //
				2634, 2717);
	}

	// {2634, 2717}
	public void test0221() {
		check(//
				"Integrate[Cosh[a + b*x]*Log[Cosh[a/2 + (b*x)/2]*Sinh[a/2 + (b*x)/2]], x]", //
				"-(Sinh[a + b*x]/b) + (Log[Cosh[a/2 + (b*x)/2]*Sinh[a/2 + (b*x)/2]]*Sinh[a + b*x])/b", //
				2634, 2717);
	}

	// {222}
	public void test0222() {
		check(//
				"Integrate[1/(x*Sqrt[1 - Log[x]^2]), x]", //
				"ArcSin[Log[x]]", //
				222);
	}

	// {2436, 2332}
	public void test0223() {
		check(//
				"Integrate[Log[Sqrt[a + b*x]], x]", //
				"-x/2 + ((a + b*x)*Log[Sqrt[a + b*x]])/b", //
				2436, 2332);
	}

	// {2614, 267}
	public void test0224() {
		check(//
				"Integrate[Log[x + Sqrt[1 + x^2]], x]", //
				"-Sqrt[1 + x^2] + x*Log[x + Sqrt[1 + x^2]]", //
				2614, 267);
	}

	// {2614, 267}
	public void test0225() {
		check(//
				"Integrate[Log[x + Sqrt[-1 + x^2]], x]", //
				"-Sqrt[-1 + x^2] + x*Log[x + Sqrt[-1 + x^2]]", //
				2614, 267);
	}

	// {2614, 267}
	public void test0226() {
		check(//
				"Integrate[Log[x - Sqrt[-1 + x^2]], x]", //
				"Sqrt[-1 + x^2] + x*Log[x - Sqrt[-1 + x^2]]", //
				2614, 267);
	}

	// {2306, 30}
	public void test0227() {
		check(//
				"Integrate[2^Log[x], x]", //
				"x^(1 + Log[2])/(1 + Log[2])", //
				2306, 30);
	}

	// {2306, 32}
	public void test0228() {
		check(//
				"Integrate[2^Log[-8 + 7*x], x]", //
				"(-8 + 7*x)^(1 + Log[2])/(7*(1 + Log[2]))", //
				2306, 32);
	}

	// {2535, 31}
	public void test0229() {
		check(//
				"Integrate[Log[(-11 + 5*x)/(5 + 76*x)], x]", //
				"-((11 - 5*x)*Log[-((11 - 5*x)/(5 + 76*x))])/5 - (861*Log[5 + 76*x])/380", //
				2535, 31);
	}

	// {2436, 2332}
	public void test0230() {
		check(//
				"Integrate[Log[(13 + x)^(-1)], x]", //
				"x + (13 + x)*Log[(13 + x)^(-1)]", //
				2436, 2332);
	}

	// {2437, 2341}
	public void test0231() {
		check(//
				"Integrate[(a + b*x)*Log[a + b*x], x]", //
				"-(a + b*x)^2/(4*b) + ((a + b*x)^2*Log[a + b*x])/(2*b)", //
				2437, 2341);
	}

	// {2437, 2341}
	public void test0232() {
		check(//
				"Integrate[(a + b*x)^2*Log[a + b*x], x]", //
				"-(a + b*x)^3/(9*b) + ((a + b*x)^3*Log[a + b*x])/(3*b)", //
				2437, 2341);
	}

	// {2437, 2338}
	public void test0233() {
		check(//
				"Integrate[Log[a + b*x]/(a + b*x), x]", //
				"Log[a + b*x]^2/(2*b)", //
				2437, 2338);
	}

	// {2437, 2341}
	public void test0234() {
		check(//
				"Integrate[Log[a + b*x]/(a + b*x)^2, x]", //
				"-(1/(b*(a + b*x))) - Log[a + b*x]/(b*(a + b*x))", //
				2437, 2341);
	}

	// {2437, 2341}
	public void test0235() {
		check(//
				"Integrate[(a + b*x)^n*Log[a + b*x], x]", //
				"-((a + b*x)^(1 + n)/(b*(1 + n)^2)) + ((a + b*x)^(1 + n)*Log[a + b*x])/(b*(1 + n))", //
				2437, 2341);
	}

	// {31}
	public void test0236() {
		check(//
				"Integrate[(a*x + b*x*Log[c*x^n])^(-1), x]", //
				"Log[a + b*Log[c*x^n]]/(b*n)", //
				31);
	}

	// {211}
	public void test0237() {
		check(//
				"Integrate[(a*x + b*x*Log[c*x^n]^2)^(-1), x]", //
				"ArcTan[(Sqrt[b]*Log[c*x^n])/Sqrt[a]]/(Sqrt[a]*Sqrt[b]*n)", //
				211);
	}

	// {2342, 2341}
	public void test0238() {
		check(//
				"Integrate[Log[x^(-1)]^2/x^5, x]", //
				"-1/(32*x^4) + Log[x^(-1)]/(8*x^4) - Log[x^(-1)]^2/(4*x^4)", //
				2342, 2341);
	}

	// {4607}
	public void test0239() {
		check(//
				"Integrate[Sin[x*Log[x]] + Log[x]*Sin[x*Log[x]], x]", //
				"-Cos[x*Log[x]]", //
				4607);
	}

	// {2536, 31}
	public void test0240() {
		check(//
				"Integrate[Log[-(x/(1 + x))], x]", //
				"x*Log[-(x/(1 + x))] - Log[1 + x]", //
				2536, 31);
	}

	// {2535, 31}
	public void test0241() {
		check(//
				"Integrate[Log[(-1 + x)/(1 + x)], x]", //
				"-((1 - x)*Log[-((1 - x)/(1 + x))]) - 2*Log[1 + x]", //
				2535, 31);
	}

	// {2188, 30}
	public void test0242() {
		check(//
				"Integrate[Log[E^(a + b*x)], x]", //
				"Log[E^(a + b*x)]^2/(2*b)", //
				2188, 30);
	}

	// {266}
	public void test0243() {
		check(//
				"Integrate[Log[x]/(x + 4*x*Log[x]^2), x]", //
				"Log[1 + 4*Log[x]^2]/8", //
				266);
	}

	// {6844, 31}
	public void test0244() {
		check(//
				"Integrate[(1 - Log[x])/(x*(x + Log[x])), x]", //
				"Log[1 + Log[x]/x]", //
				6844, 31);
	}

	// {2633}
	public void test0245() {
		check(//
				"Integrate[x^(a*x) + x^(a*x)*Log[x], x]", //
				"x^(a*x)/a", //
				2633);
	}

}
