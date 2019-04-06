package org.matheclipse.core.rubi.step02;

public class InverseTrigFunctions extends AbstractRubiTestCase {

	static boolean init = true;
	
	public InverseTrigFunctions(String name) {
		super(name, false);
	}

	@Override
	protected void setUp() {
		try {
			super.setUp();
			if (init) {
				System.out.println("InverseTrigFunctions");
				init = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	// {4715, 267}
	public void test0001() {
		check(//
				"Integrate[ArcSin[a*x], x]", //
				"Sqrt[1 - a^2*x^2]/a + x*ArcSin[a*x]", //
				4715, 267);
	}

	// {4723, 270}
	public void test0002() {
		check(//
				"Integrate[ArcSin[a*x]/x^3, x]", //
				"-(a*Sqrt[1 - a^2*x^2])/(2*x) - ArcSin[a*x]/(2*x^2)", //
				4723, 270);
	}

	// {4719, 3383}
	public void test0003() {
		check(//
				"Integrate[ArcSin[a*x]^(-1), x]", //
				"CosIntegral[ArcSin[a*x]]/a", //
				4719, 3383);
	}

	// {4727, 3383}
	public void test0004() {
		check(//
				"Integrate[x/ArcSin[a*x]^2, x]", //
				"-((x*Sqrt[1 - a^2*x^2])/(a*ArcSin[a*x])) + CosIntegral[2*ArcSin[a*x]]/a^2", //
				4727, 3383);
	}

	// {4723, 4805}
	public void test0005() {
		check(//
				"Integrate[(b*x)^m*ArcSin[a*x]^2, x]", //
				"((b*x)^(1 + m)*ArcSin[a*x]^2)/(b*(1 + m)) - (2*a*(b*x)^(2 + m)*ArcSin[a*x]*Hypergeometric2F1[1/2, (2 + m)/2, (4 + m)/2, a^2*x^2])/(b^2*(1 + m)*(2 + m)) + (2*a^2*(b*x)^(3 + m)*HypergeometricPFQ[{1, 3/2 + m/2, 3/2 + m/2}, {2 + m/2, 5/2 + m/2}, a^2*x^2])/(b^3*(1 + m)*(2 + m)*(3 + m))", //
				4723, 4805);
	}

	// {4723, 371}
	public void test0006() {
		check(//
				"Integrate[(b*x)^m*ArcSin[a*x], x]", //
				"((b*x)^(1 + m)*ArcSin[a*x])/(b*(1 + m)) - (a*(b*x)^(2 + m)*Hypergeometric2F1[1/2, (2 + m)/2, (4 + m)/2, a^2*x^2])/(b^2*(1 + m)*(2 + m))", //
				4723, 371);
	}

	// {4723, 270}
	public void test0007() {
		check(//
				"Integrate[(a + b*ArcSin[c*x])/x^3, x]", //
				"-(b*c*Sqrt[1 - c^2*x^2])/(2*x) - (a + b*ArcSin[c*x])/(2*x^2)", //
				4723, 270);
	}

	// {4723, 4805}
	public void test0008() {
		check(//
				"Integrate[(d*x)^(5/2)*(a + b*ArcSin[c*x])^2, x]", //
				"(2*(d*x)^(7/2)*(a + b*ArcSin[c*x])^2)/(7*d) - (8*b*c*(d*x)^(9/2)*(a + b*ArcSin[c*x])*Hypergeometric2F1[1/2, 9/4, 13/4, c^2*x^2])/(63*d^2) + (16*b^2*c^2*(d*x)^(11/2)*HypergeometricPFQ[{1, 11/4, 11/4}, {13/4, 15/4}, c^2*x^2])/(693*d^3)", //
				4723, 4805);
	}

	// {4723, 4805}
	public void test0009() {
		check(//
				"Integrate[(d*x)^(3/2)*(a + b*ArcSin[c*x])^2, x]", //
				"(2*(d*x)^(5/2)*(a + b*ArcSin[c*x])^2)/(5*d) - (8*b*c*(d*x)^(7/2)*(a + b*ArcSin[c*x])*Hypergeometric2F1[1/2, 7/4, 11/4, c^2*x^2])/(35*d^2) + (16*b^2*c^2*(d*x)^(9/2)*HypergeometricPFQ[{1, 9/4, 9/4}, {11/4, 13/4}, c^2*x^2])/(315*d^3)", //
				4723, 4805);
	}

	// {4723, 4805}
	public void test0010() {
		check(//
				"Integrate[Sqrt[d*x]*(a + b*ArcSin[c*x])^2, x]", //
				"(2*(d*x)^(3/2)*(a + b*ArcSin[c*x])^2)/(3*d) - (8*b*c*(d*x)^(5/2)*(a + b*ArcSin[c*x])*Hypergeometric2F1[1/2, 5/4, 9/4, c^2*x^2])/(15*d^2) + (16*b^2*c^2*(d*x)^(7/2)*HypergeometricPFQ[{1, 7/4, 7/4}, {9/4, 11/4}, c^2*x^2])/(105*d^3)", //
				4723, 4805);
	}

	// {4723, 4805}
	public void test0011() {
		check(//
				"Integrate[(a + b*ArcSin[c*x])^2/Sqrt[d*x], x]", //
				"(2*Sqrt[d*x]*(a + b*ArcSin[c*x])^2)/d - (8*b*c*(d*x)^(3/2)*(a + b*ArcSin[c*x])*Hypergeometric2F1[1/2, 3/4, 7/4, c^2*x^2])/(3*d^2) + (16*b^2*c^2*(d*x)^(5/2)*HypergeometricPFQ[{1, 5/4, 5/4}, {7/4, 9/4}, c^2*x^2])/(15*d^3)", //
				4723, 4805);
	}

	// {4723, 4805}
	public void test0012() {
		check(//
				"Integrate[(a + b*ArcSin[c*x])^2/(d*x)^(3/2), x]", //
				"(-2*(a + b*ArcSin[c*x])^2)/(d*Sqrt[d*x]) + (8*b*c*Sqrt[d*x]*(a + b*ArcSin[c*x])*Hypergeometric2F1[1/4, 1/2, 5/4, c^2*x^2])/d^2 - (16*b^2*c^2*(d*x)^(3/2)*HypergeometricPFQ[{3/4, 3/4, 1}, {5/4, 7/4}, c^2*x^2])/(3*d^3)", //
				4723, 4805);
	}

	// {4723, 4805}
	public void test0013() {
		check(//
				"Integrate[(a + b*ArcSin[c*x])^2/(d*x)^(5/2), x]", //
				"(-2*(a + b*ArcSin[c*x])^2)/(3*d*(d*x)^(3/2)) - (8*b*c*(a + b*ArcSin[c*x])*Hypergeometric2F1[-1/4, 1/2, 3/4, c^2*x^2])/(3*d^2*Sqrt[d*x]) + (16*b^2*c^2*Sqrt[d*x]*HypergeometricPFQ[{1/4, 1/4, 1}, {3/4, 5/4}, c^2*x^2])/(3*d^3)", //
				4723, 4805);
	}

	// {4767, 197}
	public void test0014() {
		check(//
				"Integrate[(x*(a + b*ArcSin[c*x]))/(d - c^2*d*x^2)^2, x]", //
				"-(b*x)/(2*c*d^2*Sqrt[1 - c^2*x^2]) + (a + b*ArcSin[c*x])/(2*c^2*d^2*(1 - c^2*x^2))", //
				4767, 197);
	}

	// {4767}
	public void test0015() {
		check(//
				"Integrate[x*Sqrt[d - c^2*d*x^2]*(a + b*ArcSin[c*x]), x]", //
				"(b*x*Sqrt[d - c^2*d*x^2])/(3*c*Sqrt[1 - c^2*x^2]) - (b*c*x^3*Sqrt[d - c^2*d*x^2])/(9*Sqrt[1 - c^2*x^2]) - ((d - c^2*d*x^2)^(3/2)*(a + b*ArcSin[c*x]))/(3*c^2*d)", //
				4767);
	}

	// {4767, 8}
	public void test0016() {
		check(//
				"Integrate[(x*ArcSin[a*x])/Sqrt[1 - a^2*x^2], x]", //
				"x/a - (Sqrt[1 - a^2*x^2]*ArcSin[a*x])/a^2", //
				4767, 8);
	}

	// {4771, 29}
	public void test0017() {
		check(//
				"Integrate[ArcSin[a*x]/(x^2*Sqrt[1 - a^2*x^2]), x]", //
				"-((Sqrt[1 - a^2*x^2]*ArcSin[a*x])/x) + a*Log[x]", //
				4771, 29);
	}

	// {4767, 8}
	public void test0018() {
		check(//
				"Integrate[(x*(a + b*ArcSin[c*x]))/Sqrt[d - c^2*d*x^2], x]", //
				"(b*x*Sqrt[1 - c^2*x^2])/(c*Sqrt[d - c^2*d*x^2]) - (Sqrt[d - c^2*d*x^2]*(a + b*ArcSin[c*x]))/(c^2*d)", //
				4767, 8);
	}

	// {4737}
	public void test0019() {
		check(//
				"Integrate[(a + b*ArcSin[c*x])/Sqrt[d - c^2*d*x^2], x]", //
				"(Sqrt[1 - c^2*x^2]*(a + b*ArcSin[c*x])^2)/(2*b*c*Sqrt[d - c^2*d*x^2])", //
				4737);
	}

	// {4771, 29}
	public void test0020() {
		check(//
				"Integrate[(a + b*ArcSin[c*x])/(x^2*Sqrt[d - c^2*d*x^2]), x]", //
				"-((Sqrt[d - c^2*d*x^2]*(a + b*ArcSin[c*x]))/(d*x)) + (b*c*Sqrt[1 - c^2*x^2]*Log[x])/Sqrt[d - c^2*d*x^2]", //
				4771, 29);
	}

	// {4767, 212}
	public void test0021() {
		check(//
				"Integrate[(x*(a + b*ArcSin[c*x]))/(d - c^2*d*x^2)^(3/2), x]", //
				"(a + b*ArcSin[c*x])/(c^2*d*Sqrt[d - c^2*d*x^2]) - (b*Sqrt[1 - c^2*x^2]*ArcTanh[c*x])/(c^2*d*Sqrt[d - c^2*d*x^2])", //
				4767, 212);
	}

	// {4745, 266}
	public void test0022() {
		check(//
				"Integrate[(a + b*ArcSin[c*x])/(d - c^2*d*x^2)^(3/2), x]", //
				"(x*(a + b*ArcSin[c*x]))/(d*Sqrt[d - c^2*d*x^2]) + (b*Sqrt[1 - c^2*x^2]*Log[1 - c^2*x^2])/(2*c*d*Sqrt[d - c^2*d*x^2])", //
				4745, 266);
	}

	// {4805}
	public void test0023() {
		check(//
				"Integrate[((f*x)^(3/2)*(a + b*ArcSin[c*x]))/Sqrt[d - c^2*d*x^2], x]", //
				"(2*(f*x)^(5/2)*Sqrt[1 - c^2*x^2]*(a + b*ArcSin[c*x])*Hypergeometric2F1[1/2, 5/4, 9/4, c^2*x^2])/(5*f*Sqrt[d - c^2*d*x^2]) - (4*b*c*(f*x)^(7/2)*Sqrt[1 - c^2*x^2]*HypergeometricPFQ[{1, 7/4, 7/4}, {9/4, 11/4}, c^2*x^2])/(35*f^2*Sqrt[d - c^2*d*x^2])", //
				4805);
	}

	// {4805}
	public void test0025() {
		check(//
				"Integrate[(x^m*(a + b*ArcSin[c*x]))/Sqrt[d - c^2*d*x^2], x]", //
				"(x^(1 + m)*Sqrt[1 - c^2*x^2]*(a + b*ArcSin[c*x])*Hypergeometric2F1[1/2, (1 + m)/2, (3 + m)/2, c^2*x^2])/((1 + m)*Sqrt[d - c^2*d*x^2]) - (b*c*x^(2 + m)*Sqrt[1 - c^2*x^2]*HypergeometricPFQ[{1, 1 + m/2, 1 + m/2}, {3/2 + m/2, 2 + m/2}, c^2*x^2])/((2 + 3*m + m^2)*Sqrt[d - c^2*d*x^2])", //
				4805);
	}

	// {4737}
	public void test0026() {
		check(//
				"Integrate[(a + b*ArcSin[c*x])^2/Sqrt[d - c^2*d*x^2], x]", //
				"(Sqrt[1 - c^2*x^2]*(a + b*ArcSin[c*x])^3)/(3*b*c*Sqrt[d - c^2*d*x^2])", //
				4737);
	}

	// {4737}
	public void test0027() {
		check(//
				"Integrate[ArcSin[a*x]^2/Sqrt[c - a^2*c*x^2], x]", //
				"(Sqrt[1 - a^2*x^2]*ArcSin[a*x]^3)/(3*a*Sqrt[c - a^2*c*x^2])", //
				4737);
	}

	// {4737}
	public void test0028() {
		check(//
				"Integrate[ArcSin[a*x]^3/Sqrt[c - a^2*c*x^2], x]", //
				"(Sqrt[1 - a^2*x^2]*ArcSin[a*x]^4)/(4*a*Sqrt[c - a^2*c*x^2])", //
				4737);
	}

	// {4809, 3380}
	public void test0029() {
		check(//
				"Integrate[x/(Sqrt[1 - a^2*x^2]*ArcSin[a*x]), x]", //
				"SinIntegral[ArcSin[a*x]]/a^2", //
				4809, 3380);
	}

	// {4751}
	public void test0030() {
		check(//
				"Integrate[1/((1 - x^2)*ArcSin[x]^2) - x/((1 - x^2)^(3/2)*ArcSin[x]), x]", //
				"-(1/(Sqrt[1 - x^2]*ArcSin[x]))", //
				4751);
	}

	// {4737}
	public void test0031() {
		check(//
				"Integrate[Sqrt[ArcSin[a*x]]/Sqrt[c - a^2*c*x^2], x]", //
				"(2*Sqrt[1 - a^2*x^2]*ArcSin[a*x]^(3/2))/(3*a*Sqrt[c - a^2*c*x^2])", //
				4737);
	}

	// {4737}
	public void test0033() {
		check(//
				"Integrate[ArcSin[a*x]^(3/2)/Sqrt[c - a^2*c*x^2], x]", //
				"(2*Sqrt[1 - a^2*x^2]*ArcSin[a*x]^(5/2))/(5*a*Sqrt[c - a^2*c*x^2])", //
				4737);
	}

	// {4737}
	public void test0034() {
		check(//
				"Integrate[ArcSin[a*x]^(5/2)/Sqrt[c - a^2*c*x^2], x]", //
				"(2*Sqrt[1 - a^2*x^2]*ArcSin[a*x]^(7/2))/(7*a*Sqrt[c - a^2*c*x^2])", //
				4737);
	}

	// {4737}
	public void test0035() {
		check(//
				"Integrate[Sqrt[ArcSin[x/a]]/Sqrt[a^2 - x^2], x]", //
				"(2*a*Sqrt[1 - x^2/a^2]*ArcSin[x/a]^(3/2))/(3*Sqrt[a^2 - x^2])", //
				4737);
	}

	// {4737}
	public void test0037() {
		check(//
				"Integrate[ArcSin[x/a]^(3/2)/Sqrt[a^2 - x^2], x]", //
				"(2*a*Sqrt[1 - x^2/a^2]*ArcSin[x/a]^(5/2))/(5*Sqrt[a^2 - x^2])", //
				4737);
	}

	// {4737}
	public void test0038() {
		check(//
				"Integrate[1/(Sqrt[c - a^2*c*x^2]*Sqrt[ArcSin[a*x]]), x]", //
				"(2*Sqrt[1 - a^2*x^2]*Sqrt[ArcSin[a*x]])/(a*Sqrt[c - a^2*c*x^2])", //
				4737);
	}

	// {4737}
	public void test0039() {
		check(//
				"Integrate[1/(Sqrt[c - a^2*c*x^2]*ArcSin[a*x]^(3/2)), x]", //
				"(-2*Sqrt[1 - a^2*x^2])/(a*Sqrt[c - a^2*c*x^2]*Sqrt[ArcSin[a*x]])", //
				4737);
	}

	// {4737}
	public void test0040() {
		check(//
				"Integrate[1/(Sqrt[c - a^2*c*x^2]*ArcSin[a*x]^(5/2)), x]", //
				"(-2*Sqrt[1 - a^2*x^2])/(3*a*Sqrt[c - a^2*c*x^2]*ArcSin[a*x]^(3/2))", //
				4737);
	}

	// {4763, 4737}
	public void test0041() {
		check(//
				"Integrate[(a + b*ArcSin[c*x])/(Sqrt[d + c*d*x]*Sqrt[f - c*f*x]), x]", //
				"(Sqrt[1 - c^2*x^2]*(a + b*ArcSin[c*x])^2)/(2*b*c*Sqrt[d + c*d*x]*Sqrt[f - c*f*x])", //
				4763, 4737);
	}

	// {4763, 4737}
	public void test0042() {
		check(//
				"Integrate[(a + b*ArcSin[c*x])^2/(Sqrt[d + c*d*x]*Sqrt[e - c*e*x]), x]", //
				"(Sqrt[1 - c^2*x^2]*(a + b*ArcSin[c*x])^3)/(3*b*c*Sqrt[d + c*d*x]*Sqrt[e - c*e*x])", //
				4763, 4737);
	}

	// {4763, 4737}
	public void test0043() {
		check(//
				"Integrate[(a + b*ArcSin[c*x])^2/(Sqrt[d + c*d*x]*Sqrt[e - c*e*x]), x]", //
				"(Sqrt[1 - c^2*x^2]*(a + b*ArcSin[c*x])^3)/(3*b*c*Sqrt[d + c*d*x]*Sqrt[e - c*e*x])", //
				4763, 4737);
	}

	// {4891, 4737}
	public void test0066() {
		check(//
				"Integrate[ArcSin[a + b*x]^n/Sqrt[1 - a^2 - 2*a*b*x - b^2*x^2], x]", //
				"ArcSin[a + b*x]^(1 + n)/(b*(1 + n))", //
				4891, 4737);
	}

	// {4891, 4737}
	public void test0067() {
		check(//
				"Integrate[ArcSin[a + b*x]^2/Sqrt[1 - a^2 - 2*a*b*x - b^2*x^2], x]", //
				"ArcSin[a + b*x]^3/(3*b)", //
				4891, 4737);
	}

	// {4891, 4737}
	public void test0068() {
		check(//
				"Integrate[ArcSin[a + b*x]/Sqrt[1 - a^2 - 2*a*b*x - b^2*x^2], x]", //
				"ArcSin[a + b*x]^2/(2*b)", //
				4891, 4737);
	}

	// {4891, 4735}
	public void test0069() {
		check(//
				"Integrate[1/(Sqrt[1 - a^2 - 2*a*b*x - b^2*x^2]*ArcSin[a + b*x]), x]", //
				"Log[ArcSin[a + b*x]]/b", //
				4891, 4735);
	}

	// {4891, 4737}
	public void test0070() {
		check(//
				"Integrate[1/(Sqrt[1 - a^2 - 2*a*b*x - b^2*x^2]*ArcSin[a + b*x]^2), x]", //
				"-(1/(b*ArcSin[a + b*x]))", //
				4891, 4737);
	}

	// {4891, 4737}
	public void test0071() {
		check(//
				"Integrate[1/(Sqrt[1 - a^2 - 2*a*b*x - b^2*x^2]*ArcSin[a + b*x]^3), x]", //
				"-1/(2*b*ArcSin[a + b*x]^2)", //
				4891, 4737);
	}

	// {4898, 8}
	public void test0073() {
		check(//
				"Integrate[(a + b*ArcSin[1 + d*x^2])^2, x]", //
				"-8*b^2*x + (4*b*Sqrt[-2*d*x^2 - d^2*x^4]*(a + b*ArcSin[1 + d*x^2]))/(d*x) + x*(a + b*ArcSin[1 + d*x^2])^2", //
				4898, 8);
	}

	// {4912, 4900}
	public void test0074() {
		check(//
				"Integrate[(a + b*ArcSin[1 + d*x^2])^(-3), x]", //
				"-Sqrt[-2*d*x^2 - d^2*x^4]/(4*b*d*x*(a + b*ArcSin[1 + d*x^2])^2) + x/(8*b^2*(a + b*ArcSin[1 + d*x^2])) + (x*CosIntegral[(a + b*ArcSin[1 + d*x^2])/(2*b)]*(Cos[a/(2*b)] - Sin[a/(2*b)]))/(16*b^3*(Cos[ArcSin[1 + d*x^2]/2] - Sin[ArcSin[1 + d*x^2]/2])) + (x*(Cos[a/(2*b)] + Sin[a/(2*b)])*SinIntegral[(a + b*ArcSin[1 + d*x^2])/(2*b)])/(16*b^3*(Cos[ArcSin[1 + d*x^2]/2] - Sin[ArcSin[1 + d*x^2]/2]))", //
				4912, 4900);
	}

	// {4898, 8}
	public void test0075() {
		check(//
				"Integrate[(a - b*ArcSin[1 - d*x^2])^2, x]", //
				"-8*b^2*x + (4*b*Sqrt[2*d*x^2 - d^2*x^4]*(a - b*ArcSin[1 - d*x^2]))/(d*x) + x*(a - b*ArcSin[1 - d*x^2])^2", //
				4898, 8);
	}

	// {4912, 4900}
	public void test0076() {
		check(//
				"Integrate[(a - b*ArcSin[1 - d*x^2])^(-3), x]", //
				"-Sqrt[2*d*x^2 - d^2*x^4]/(4*b*d*x*(a - b*ArcSin[1 - d*x^2])^2) + x/(8*b^2*(a - b*ArcSin[1 - d*x^2])) - (x*CosIntegral[-(a - b*ArcSin[1 - d*x^2])/(2*b)]*(Cos[a/(2*b)] + Sin[a/(2*b)]))/(16*b^3*(Cos[ArcSin[1 - d*x^2]/2] - Sin[ArcSin[1 - d*x^2]/2])) + (x*(Cos[a/(2*b)] - Sin[a/(2*b)])*SinIntegral[a/(2*b) - ArcSin[1 - d*x^2]/2])/(16*b^3*(Cos[ArcSin[1 - d*x^2]/2] - Sin[ArcSin[1 - d*x^2]/2]))", //
				4912, 4900);
	}

	// {4898, 8}
	public void test0077() {
		check(//
				"Integrate[ArcSin[1 + x^2]^2, x]", //
				"-8*x + (4*Sqrt[-2*x^2 - x^4]*ArcSin[1 + x^2])/x + x*ArcSin[1 + x^2]^2", //
				4898, 8);
	}

	// {4898, 8}
	public void test0078() {
		check(//
				"Integrate[ArcSin[1 - x^2]^2, x]", //
				"-8*x - (4*Sqrt[2*x^2 - x^4]*ArcSin[1 - x^2])/x + x*ArcSin[1 - x^2]^2", //
				4898, 8);
	}

	// {4898, 4895}
	public void test0079() {
		check(//
				"Integrate[(a + b*ArcSin[1 + d*x^2])^(5/2), x]", //
				"-15*b^2*x*Sqrt[a + b*ArcSin[1 + d*x^2]] + (5*b*Sqrt[-2*d*x^2 - d^2*x^4]*(a + b*ArcSin[1 + d*x^2])^(3/2))/(d*x) + x*(a + b*ArcSin[1 + d*x^2])^(5/2) - (15*Sqrt[Pi]*x*FresnelS[(Sqrt[b^(-1)]*Sqrt[a + b*ArcSin[1 + d*x^2]])/Sqrt[Pi]]*(Cos[a/(2*b)] - Sin[a/(2*b)]))/((b^(-1))^(5/2)*(Cos[ArcSin[1 + d*x^2]/2] - Sin[ArcSin[1 + d*x^2]/2])) + (15*Sqrt[Pi]*x*FresnelC[(Sqrt[b^(-1)]*Sqrt[a + b*ArcSin[1 + d*x^2]])/Sqrt[Pi]]*(Cos[a/(2*b)] + Sin[a/(2*b)]))/((b^(-1))^(5/2)*(Cos[ArcSin[1 + d*x^2]/2] - Sin[ArcSin[1 + d*x^2]/2]))", //
				4898, 4895);
	}

	// {4898, 4903}
	public void test0080() {
		check(//
				"Integrate[(a + b*ArcSin[1 + d*x^2])^(3/2), x]", //
				"(3*b*Sqrt[-2*d*x^2 - d^2*x^4]*Sqrt[a + b*ArcSin[1 + d*x^2]])/(d*x) + x*(a + b*ArcSin[1 + d*x^2])^(3/2) + (3*b^(3/2)*Sqrt[Pi]*x*FresnelC[Sqrt[a + b*ArcSin[1 + d*x^2]]/(Sqrt[b]*Sqrt[Pi])]*(Cos[a/(2*b)] - Sin[a/(2*b)]))/(Cos[ArcSin[1 + d*x^2]/2] - Sin[ArcSin[1 + d*x^2]/2]) + (3*b^(3/2)*Sqrt[Pi]*x*FresnelS[Sqrt[a + b*ArcSin[1 + d*x^2]]/(Sqrt[b]*Sqrt[Pi])]*(Cos[a/(2*b)] + Sin[a/(2*b)]))/(Cos[ArcSin[1 + d*x^2]/2] - Sin[ArcSin[1 + d*x^2]/2])", //
				4898, 4903);
	}

	// {4912, 4903}
	public void test0081() {
		check(//
				"Integrate[(a + b*ArcSin[1 + d*x^2])^(-5/2), x]", //
				"-Sqrt[-2*d*x^2 - d^2*x^4]/(3*b*d*x*(a + b*ArcSin[1 + d*x^2])^(3/2)) + x/(3*b^2*Sqrt[a + b*ArcSin[1 + d*x^2]]) + (Sqrt[Pi]*x*FresnelC[Sqrt[a + b*ArcSin[1 + d*x^2]]/(Sqrt[b]*Sqrt[Pi])]*(Cos[a/(2*b)] - Sin[a/(2*b)]))/(3*b^(5/2)*(Cos[ArcSin[1 + d*x^2]/2] - Sin[ArcSin[1 + d*x^2]/2])) + (Sqrt[Pi]*x*FresnelS[Sqrt[a + b*ArcSin[1 + d*x^2]]/(Sqrt[b]*Sqrt[Pi])]*(Cos[a/(2*b)] + Sin[a/(2*b)]))/(3*b^(5/2)*(Cos[ArcSin[1 + d*x^2]/2] - Sin[ArcSin[1 + d*x^2]/2]))", //
				4912, 4903);
	}

	// {4912, 4906}
	public void test0082() {
		check(//
				"Integrate[(a + b*ArcSin[1 + d*x^2])^(-7/2), x]", //
				"-Sqrt[-2*d*x^2 - d^2*x^4]/(5*b*d*x*(a + b*ArcSin[1 + d*x^2])^(5/2)) + x/(15*b^2*(a + b*ArcSin[1 + d*x^2])^(3/2)) + Sqrt[-2*d*x^2 - d^2*x^4]/(15*b^3*d*x*Sqrt[a + b*ArcSin[1 + d*x^2]]) - ((b^(-1))^(7/2)*Sqrt[Pi]*x*FresnelS[(Sqrt[b^(-1)]*Sqrt[a + b*ArcSin[1 + d*x^2]])/Sqrt[Pi]]*(Cos[a/(2*b)] - Sin[a/(2*b)]))/(15*(Cos[ArcSin[1 + d*x^2]/2] - Sin[ArcSin[1 + d*x^2]/2])) + ((b^(-1))^(7/2)*Sqrt[Pi]*x*FresnelC[(Sqrt[b^(-1)]*Sqrt[a + b*ArcSin[1 + d*x^2]])/Sqrt[Pi]]*(Cos[a/(2*b)] + Sin[a/(2*b)]))/(15*(Cos[ArcSin[1 + d*x^2]/2] - Sin[ArcSin[1 + d*x^2]/2]))", //
				4912, 4906);
	}

	// {4898, 4895}
	public void test0083() {
		check(//
				"Integrate[(a - b*ArcSin[1 - d*x^2])^(5/2), x]", //
				"-15*b^2*x*Sqrt[a - b*ArcSin[1 - d*x^2]] + (5*b*Sqrt[2*d*x^2 - d^2*x^4]*(a - b*ArcSin[1 - d*x^2])^(3/2))/(d*x) + x*(a - b*ArcSin[1 - d*x^2])^(5/2) + (15*Sqrt[Pi]*x*FresnelC[(Sqrt[-b^(-1)]*Sqrt[a - b*ArcSin[1 - d*x^2]])/Sqrt[Pi]]*(Cos[a/(2*b)] - Sin[a/(2*b)]))/((-b^(-1))^(5/2)*(Cos[ArcSin[1 - d*x^2]/2] - Sin[ArcSin[1 - d*x^2]/2])) - (15*Sqrt[Pi]*x*FresnelS[(Sqrt[-b^(-1)]*Sqrt[a - b*ArcSin[1 - d*x^2]])/Sqrt[Pi]]*(Cos[a/(2*b)] + Sin[a/(2*b)]))/((-b^(-1))^(5/2)*(Cos[ArcSin[1 - d*x^2]/2] - Sin[ArcSin[1 - d*x^2]/2]))", //
				4898, 4895);
	}

	// {4898, 4903}
	public void test0084() {
		check(//
				"Integrate[(a - b*ArcSin[1 - d*x^2])^(3/2), x]", //
				"(3*b*Sqrt[2*d*x^2 - d^2*x^4]*Sqrt[a - b*ArcSin[1 - d*x^2]])/(d*x) + x*(a - b*ArcSin[1 - d*x^2])^(3/2) + (3*(-b)^(3/2)*Sqrt[Pi]*x*FresnelS[Sqrt[a - b*ArcSin[1 - d*x^2]]/(Sqrt[-b]*Sqrt[Pi])]*(Cos[a/(2*b)] - Sin[a/(2*b)]))/(Cos[ArcSin[1 - d*x^2]/2] - Sin[ArcSin[1 - d*x^2]/2]) + (3*(-b)^(3/2)*Sqrt[Pi]*x*FresnelC[Sqrt[a - b*ArcSin[1 - d*x^2]]/(Sqrt[-b]*Sqrt[Pi])]*(Cos[a/(2*b)] + Sin[a/(2*b)]))/(Cos[ArcSin[1 - d*x^2]/2] - Sin[ArcSin[1 - d*x^2]/2])", //
				4898, 4903);
	}

	// {4912, 4903}
	public void test0085() {
		check(//
				"Integrate[(a - b*ArcSin[1 - d*x^2])^(-5/2), x]", //
				"-Sqrt[2*d*x^2 - d^2*x^4]/(3*b*d*x*(a - b*ArcSin[1 - d*x^2])^(3/2)) + x/(3*b^2*Sqrt[a - b*ArcSin[1 - d*x^2]]) + (Sqrt[Pi]*x*FresnelS[Sqrt[a - b*ArcSin[1 - d*x^2]]/(Sqrt[-b]*Sqrt[Pi])]*(Cos[a/(2*b)] - Sin[a/(2*b)]))/(3*(-b)^(5/2)*(Cos[ArcSin[1 - d*x^2]/2] - Sin[ArcSin[1 - d*x^2]/2])) + (Sqrt[Pi]*x*FresnelC[Sqrt[a - b*ArcSin[1 - d*x^2]]/(Sqrt[-b]*Sqrt[Pi])]*(Cos[a/(2*b)] + Sin[a/(2*b)]))/(3*(-b)^(5/2)*(Cos[ArcSin[1 - d*x^2]/2] - Sin[ArcSin[1 - d*x^2]/2]))", //
				4912, 4903);
	}

	// {4912, 4906}
	public void test0086() {
		check(//
				"Integrate[(a - b*ArcSin[1 - d*x^2])^(-7/2), x]", //
				"-Sqrt[2*d*x^2 - d^2*x^4]/(5*b*d*x*(a - b*ArcSin[1 - d*x^2])^(5/2)) + x/(15*b^2*(a - b*ArcSin[1 - d*x^2])^(3/2)) + Sqrt[2*d*x^2 - d^2*x^4]/(15*b^3*d*x*Sqrt[a - b*ArcSin[1 - d*x^2]]) + ((-b^(-1))^(7/2)*Sqrt[Pi]*x*FresnelC[(Sqrt[-b^(-1)]*Sqrt[a - b*ArcSin[1 - d*x^2]])/Sqrt[Pi]]*(Cos[a/(2*b)] - Sin[a/(2*b)]))/(15*(Cos[ArcSin[1 - d*x^2]/2] - Sin[ArcSin[1 - d*x^2]/2])) - ((-b^(-1))^(7/2)*Sqrt[Pi]*x*FresnelS[(Sqrt[-b^(-1)]*Sqrt[a - b*ArcSin[1 - d*x^2]])/Sqrt[Pi]]*(Cos[a/(2*b)] + Sin[a/(2*b)]))/(15*(Cos[ArcSin[1 - d*x^2]/2] - Sin[ArcSin[1 - d*x^2]/2]))", //
				4912, 4906);
	}

	// {4920, 4518}
	public void test0087() {
		check(//
				"Integrate[E^ArcSin[a*x], x]", //
				"(E^ArcSin[a*x]*x)/2 + (E^ArcSin[a*x]*Sqrt[1 - a^2*x^2])/(2*a)", //
				4920, 4518);
	}

	// {4920, 4518}
	public void test0090() {
		check(//
				"Integrate[E^ArcSin[a + b*x], x]", //
				"(E^ArcSin[a + b*x]*(a + b*x))/(2*b) + (E^ArcSin[a + b*x]*Sqrt[1 - (a + b*x)^2])/(2*b)", //
				4920, 4518);
	}

	// {4918, 4737}
	public void test0091() {
		check(//
				"Integrate[ArcSin[Sqrt[1 + b*x^2]]^n/Sqrt[1 + b*x^2], x]", //
				"(Sqrt[-(b*x^2)]*ArcSin[Sqrt[1 + b*x^2]]^(1 + n))/(b*(1 + n)*x)", //
				4918, 4737);
	}

	// {4918, 4735}
	public void test0092() {
		check(//
				"Integrate[1/(Sqrt[1 + b*x^2]*ArcSin[Sqrt[1 + b*x^2]]), x]", //
				"(Sqrt[-(b*x^2)]*Log[ArcSin[Sqrt[1 + b*x^2]]])/(b*x)", //
				4918, 4735);
	}

	// {4716, 267}
	public void test0093() {
		check(//
				"Integrate[ArcCos[a*x], x]", //
				"-(Sqrt[1 - a^2*x^2]/a) + x*ArcCos[a*x]", //
				4716, 267);
	}

	// {4724, 270}
	public void test0094() {
		check(//
				"Integrate[ArcCos[a*x]/x^3, x]", //
				"(a*Sqrt[1 - a^2*x^2])/(2*x) - ArcCos[a*x]/(2*x^2)", //
				4724, 270);
	}

	// {4720, 3380}
	public void test0095() {
		check(//
				"Integrate[ArcCos[a*x]^(-1), x]", //
				"-(SinIntegral[ArcCos[a*x]]/a)", //
				4720, 3380);
	}

	// {4728, 3383}
	public void test0096() {
		check(//
				"Integrate[x/ArcCos[a*x]^2, x]", //
				"(x*Sqrt[1 - a^2*x^2])/(a*ArcCos[a*x]) - CosIntegral[2*ArcCos[a*x]]/a^2", //
				4728, 3383);
	}

	// {4724, 4806}
	public void test0097() {
		check(//
				"Integrate[(b*x)^m*ArcCos[a*x]^2, x]", //
				"((b*x)^(1 + m)*ArcCos[a*x]^2)/(b*(1 + m)) + (2*a*(b*x)^(2 + m)*ArcCos[a*x]*Hypergeometric2F1[1/2, (2 + m)/2, (4 + m)/2, a^2*x^2])/(b^2*(1 + m)*(2 + m)) + (2*a^2*(b*x)^(3 + m)*HypergeometricPFQ[{1, 3/2 + m/2, 3/2 + m/2}, {2 + m/2, 5/2 + m/2}, a^2*x^2])/(b^3*(1 + m)*(2 + m)*(3 + m))", //
				4724, 4806);
	}

	// {4724, 371}
	public void test0098() {
		check(//
				"Integrate[(b*x)^m*ArcCos[a*x], x]", //
				"((b*x)^(1 + m)*ArcCos[a*x])/(b*(1 + m)) + (a*(b*x)^(2 + m)*Hypergeometric2F1[1/2, (2 + m)/2, (4 + m)/2, a^2*x^2])/(b^2*(1 + m)*(2 + m))", //
				4724, 371);
	}

	// {4724, 270}
	public void test0099() {
		check(//
				"Integrate[(a + b*ArcCos[c*x])/x^3, x]", //
				"(b*c*Sqrt[1 - c^2*x^2])/(2*x) - (a + b*ArcCos[c*x])/(2*x^2)", //
				4724, 270);
	}

	// {4724, 4806}
	public void test0100() {
		check(//
				"Integrate[(d*x)^(5/2)*(a + b*ArcCos[c*x])^2, x]", //
				"(2*(d*x)^(7/2)*(a + b*ArcCos[c*x])^2)/(7*d) + (8*b*c*(d*x)^(9/2)*(a + b*ArcCos[c*x])*Hypergeometric2F1[1/2, 9/4, 13/4, c^2*x^2])/(63*d^2) + (16*b^2*c^2*(d*x)^(11/2)*HypergeometricPFQ[{1, 11/4, 11/4}, {13/4, 15/4}, c^2*x^2])/(693*d^3)", //
				4724, 4806);
	}

	// {4724, 4806}
	public void test0101() {
		check(//
				"Integrate[(d*x)^(3/2)*(a + b*ArcCos[c*x])^2, x]", //
				"(2*(d*x)^(5/2)*(a + b*ArcCos[c*x])^2)/(5*d) + (8*b*c*(d*x)^(7/2)*(a + b*ArcCos[c*x])*Hypergeometric2F1[1/2, 7/4, 11/4, c^2*x^2])/(35*d^2) + (16*b^2*c^2*(d*x)^(9/2)*HypergeometricPFQ[{1, 9/4, 9/4}, {11/4, 13/4}, c^2*x^2])/(315*d^3)", //
				4724, 4806);
	}

	// {4724, 4806}
	public void test0102() {
		check(//
				"Integrate[Sqrt[d*x]*(a + b*ArcCos[c*x])^2, x]", //
				"(2*(d*x)^(3/2)*(a + b*ArcCos[c*x])^2)/(3*d) + (8*b*c*(d*x)^(5/2)*(a + b*ArcCos[c*x])*Hypergeometric2F1[1/2, 5/4, 9/4, c^2*x^2])/(15*d^2) + (16*b^2*c^2*(d*x)^(7/2)*HypergeometricPFQ[{1, 7/4, 7/4}, {9/4, 11/4}, c^2*x^2])/(105*d^3)", //
				4724, 4806);
	}

	// {4724, 4806}
	public void test0103() {
		check(//
				"Integrate[(a + b*ArcCos[c*x])^2/Sqrt[d*x], x]", //
				"(2*Sqrt[d*x]*(a + b*ArcCos[c*x])^2)/d + (8*b*c*(d*x)^(3/2)*(a + b*ArcCos[c*x])*Hypergeometric2F1[1/2, 3/4, 7/4, c^2*x^2])/(3*d^2) + (16*b^2*c^2*(d*x)^(5/2)*HypergeometricPFQ[{1, 5/4, 5/4}, {7/4, 9/4}, c^2*x^2])/(15*d^3)", //
				4724, 4806);
	}

	// {4724, 4806}
	public void test0104() {
		check(//
				"Integrate[(a + b*ArcCos[c*x])^2/(d*x)^(3/2), x]", //
				"(-2*(a + b*ArcCos[c*x])^2)/(d*Sqrt[d*x]) - (8*b*c*Sqrt[d*x]*(a + b*ArcCos[c*x])*Hypergeometric2F1[1/4, 1/2, 5/4, c^2*x^2])/d^2 - (16*b^2*c^2*(d*x)^(3/2)*HypergeometricPFQ[{3/4, 3/4, 1}, {5/4, 7/4}, c^2*x^2])/(3*d^3)", //
				4724, 4806);
	}

	// {4724, 4806}
	public void test0105() {
		check(//
				"Integrate[(a + b*ArcCos[c*x])^2/(d*x)^(5/2), x]", //
				"(-2*(a + b*ArcCos[c*x])^2)/(3*d*(d*x)^(3/2)) + (8*b*c*(a + b*ArcCos[c*x])*Hypergeometric2F1[-1/4, 1/2, 3/4, c^2*x^2])/(3*d^2*Sqrt[d*x]) + (16*b^2*c^2*Sqrt[d*x]*HypergeometricPFQ[{1/4, 1/4, 1}, {3/4, 5/4}, c^2*x^2])/(3*d^3)", //
				4724, 4806);
	}

	// {4768, 197}
	public void test0106() {
		check(//
				"Integrate[(x*(a + b*ArcCos[c*x]))/(d - c^2*d*x^2)^2, x]", //
				"(b*x)/(2*c*d^2*Sqrt[1 - c^2*x^2]) + (a + b*ArcCos[c*x])/(2*c^2*d^2*(1 - c^2*x^2))", //
				4768, 197);
	}

	// {4899, 8}
	public void test0107() {
		check(//
				"Integrate[(a + b*ArcCos[1 + d*x^2])^2, x]", //
				"-8*b^2*x - (4*b*Sqrt[-2*d*x^2 - d^2*x^4]*(a + b*ArcCos[1 + d*x^2]))/(d*x) + x*(a + b*ArcCos[1 + d*x^2])^2", //
				4899, 8);
	}

	// {4913, 4901}
	public void test0108() {
		check(//
				"Integrate[(a + b*ArcCos[1 + d*x^2])^(-3), x]", //
				"Sqrt[-2*d*x^2 - d^2*x^4]/(4*b*d*x*(a + b*ArcCos[1 + d*x^2])^2) + x/(8*b^2*(a + b*ArcCos[1 + d*x^2])) - (x*Cos[a/(2*b)]*CosIntegral[(a + b*ArcCos[1 + d*x^2])/(2*b)])/(8*Sqrt[2]*b^3*Sqrt[-(d*x^2)]) - (x*Sin[a/(2*b)]*SinIntegral[(a + b*ArcCos[1 + d*x^2])/(2*b)])/(8*Sqrt[2]*b^3*Sqrt[-(d*x^2)])", //
				4913, 4901);
	}

	// {4899, 8}
	public void test0109() {
		check(//
				"Integrate[(a + b*ArcCos[-1 + d*x^2])^2, x]", //
				"-8*b^2*x - (4*b*Sqrt[2*d*x^2 - d^2*x^4]*(a + b*ArcCos[-1 + d*x^2]))/(d*x) + x*(a + b*ArcCos[-1 + d*x^2])^2", //
				4899, 8);
	}

	// {4913, 4902}
	public void test0110() {
		check(//
				"Integrate[(a + b*ArcCos[-1 + d*x^2])^(-3), x]", //
				"Sqrt[2*d*x^2 - d^2*x^4]/(4*b*d*x*(a + b*ArcCos[-1 + d*x^2])^2) + x/(8*b^2*(a + b*ArcCos[-1 + d*x^2])) - (x*CosIntegral[(a + b*ArcCos[-1 + d*x^2])/(2*b)]*Sin[a/(2*b)])/(8*Sqrt[2]*b^3*Sqrt[d*x^2]) + (x*Cos[a/(2*b)]*SinIntegral[(a + b*ArcCos[-1 + d*x^2])/(2*b)])/(8*Sqrt[2]*b^3*Sqrt[d*x^2])", //
				4913, 4902);
	}

	// {4899, 4896}
	public void test0111() {
		check(//
				"Integrate[(a + b*ArcCos[1 + d*x^2])^(5/2), x]", //
				"(-5*b*Sqrt[-2*d*x^2 - d^2*x^4]*(a + b*ArcCos[1 + d*x^2])^(3/2))/(d*x) + x*(a + b*ArcCos[1 + d*x^2])^(5/2) - (30*Sqrt[Pi]*Cos[a/(2*b)]*FresnelS[(Sqrt[b^(-1)]*Sqrt[a + b*ArcCos[1 + d*x^2]])/Sqrt[Pi]]*Sin[ArcCos[1 + d*x^2]/2])/((b^(-1))^(5/2)*d*x) + (30*Sqrt[Pi]*FresnelC[(Sqrt[b^(-1)]*Sqrt[a + b*ArcCos[1 + d*x^2]])/Sqrt[Pi]]*Sin[a/(2*b)]*Sin[ArcCos[1 + d*x^2]/2])/((b^(-1))^(5/2)*d*x) + (30*b^2*Sqrt[a + b*ArcCos[1 + d*x^2]]*Sin[ArcCos[1 + d*x^2]/2]^2)/(d*x)", //
				4899, 4896);
	}

	// {4899, 4904}
	public void test0112() {
		check(//
				"Integrate[(a + b*ArcCos[1 + d*x^2])^(3/2), x]", //
				"(-3*b*Sqrt[-2*d*x^2 - d^2*x^4]*Sqrt[a + b*ArcCos[1 + d*x^2]])/(d*x) + x*(a + b*ArcCos[1 + d*x^2])^(3/2) + (6*Sqrt[Pi]*Cos[a/(2*b)]*FresnelC[(Sqrt[b^(-1)]*Sqrt[a + b*ArcCos[1 + d*x^2]])/Sqrt[Pi]]*Sin[ArcCos[1 + d*x^2]/2])/((b^(-1))^(3/2)*d*x) + (6*Sqrt[Pi]*FresnelS[(Sqrt[b^(-1)]*Sqrt[a + b*ArcCos[1 + d*x^2]])/Sqrt[Pi]]*Sin[a/(2*b)]*Sin[ArcCos[1 + d*x^2]/2])/((b^(-1))^(3/2)*d*x)", //
				4899, 4904);
	}

	// {4913, 4904}
	public void test0113() {
		check(//
				"Integrate[(a + b*ArcCos[1 + d*x^2])^(-5/2), x]", //
				"Sqrt[-2*d*x^2 - d^2*x^4]/(3*b*d*x*(a + b*ArcCos[1 + d*x^2])^(3/2)) + x/(3*b^2*Sqrt[a + b*ArcCos[1 + d*x^2]]) + (2*(b^(-1))^(5/2)*Sqrt[Pi]*Cos[a/(2*b)]*FresnelC[(Sqrt[b^(-1)]*Sqrt[a + b*ArcCos[1 + d*x^2]])/Sqrt[Pi]]*Sin[ArcCos[1 + d*x^2]/2])/(3*d*x) + (2*(b^(-1))^(5/2)*Sqrt[Pi]*FresnelS[(Sqrt[b^(-1)]*Sqrt[a + b*ArcCos[1 + d*x^2]])/Sqrt[Pi]]*Sin[a/(2*b)]*Sin[ArcCos[1 + d*x^2]/2])/(3*d*x)", //
				4913, 4904);
	}

	// {4913, 4907}
	public void test0114() {
		check(//
				"Integrate[(a + b*ArcCos[1 + d*x^2])^(-7/2), x]", //
				"Sqrt[-2*d*x^2 - d^2*x^4]/(5*b*d*x*(a + b*ArcCos[1 + d*x^2])^(5/2)) + x/(15*b^2*(a + b*ArcCos[1 + d*x^2])^(3/2)) - Sqrt[-2*d*x^2 - d^2*x^4]/(15*b^3*d*x*Sqrt[a + b*ArcCos[1 + d*x^2]]) - (2*(b^(-1))^(7/2)*Sqrt[Pi]*Cos[a/(2*b)]*FresnelS[(Sqrt[b^(-1)]*Sqrt[a + b*ArcCos[1 + d*x^2]])/Sqrt[Pi]]*Sin[ArcCos[1 + d*x^2]/2])/(15*d*x) + (2*(b^(-1))^(7/2)*Sqrt[Pi]*FresnelC[(Sqrt[b^(-1)]*Sqrt[a + b*ArcCos[1 + d*x^2]])/Sqrt[Pi]]*Sin[a/(2*b)]*Sin[ArcCos[1 + d*x^2]/2])/(15*d*x)", //
				4913, 4907);
	}

	// {4899, 4897}
	public void test0115() {
		check(//
				"Integrate[(a + b*ArcCos[-1 + d*x^2])^(5/2), x]", //
				"(-5*b*Sqrt[2*d*x^2 - d^2*x^4]*(a + b*ArcCos[-1 + d*x^2])^(3/2))/(d*x) + x*(a + b*ArcCos[-1 + d*x^2])^(5/2) - (30*b^2*Sqrt[a + b*ArcCos[-1 + d*x^2]]*Cos[ArcCos[-1 + d*x^2]/2]^2)/(d*x) + (30*Sqrt[Pi]*Cos[a/(2*b)]*Cos[ArcCos[-1 + d*x^2]/2]*FresnelC[(Sqrt[b^(-1)]*Sqrt[a + b*ArcCos[-1 + d*x^2]])/Sqrt[Pi]])/((b^(-1))^(5/2)*d*x) + (30*Sqrt[Pi]*Cos[ArcCos[-1 + d*x^2]/2]*FresnelS[(Sqrt[b^(-1)]*Sqrt[a + b*ArcCos[-1 + d*x^2]])/Sqrt[Pi]]*Sin[a/(2*b)])/((b^(-1))^(5/2)*d*x)", //
				4899, 4897);
	}

	// {4899, 4905}
	public void test0116() {
		check(//
				"Integrate[(a + b*ArcCos[-1 + d*x^2])^(3/2), x]", //
				"(-3*b*Sqrt[2*d*x^2 - d^2*x^4]*Sqrt[a + b*ArcCos[-1 + d*x^2]])/(d*x) + x*(a + b*ArcCos[-1 + d*x^2])^(3/2) + (6*Sqrt[Pi]*Cos[a/(2*b)]*Cos[ArcCos[-1 + d*x^2]/2]*FresnelS[(Sqrt[b^(-1)]*Sqrt[a + b*ArcCos[-1 + d*x^2]])/Sqrt[Pi]])/((b^(-1))^(3/2)*d*x) - (6*Sqrt[Pi]*Cos[ArcCos[-1 + d*x^2]/2]*FresnelC[(Sqrt[b^(-1)]*Sqrt[a + b*ArcCos[-1 + d*x^2]])/Sqrt[Pi]]*Sin[a/(2*b)])/((b^(-1))^(3/2)*d*x)", //
				4899, 4905);
	}

	// {4913, 4905}
	public void test0117() {
		check(//
				"Integrate[(a + b*ArcCos[-1 + d*x^2])^(-5/2), x]", //
				"Sqrt[2*d*x^2 - d^2*x^4]/(3*b*d*x*(a + b*ArcCos[-1 + d*x^2])^(3/2)) + x/(3*b^2*Sqrt[a + b*ArcCos[-1 + d*x^2]]) + (2*(b^(-1))^(5/2)*Sqrt[Pi]*Cos[a/(2*b)]*Cos[ArcCos[-1 + d*x^2]/2]*FresnelS[(Sqrt[b^(-1)]*Sqrt[a + b*ArcCos[-1 + d*x^2]])/Sqrt[Pi]])/(3*d*x) - (2*(b^(-1))^(5/2)*Sqrt[Pi]*Cos[ArcCos[-1 + d*x^2]/2]*FresnelC[(Sqrt[b^(-1)]*Sqrt[a + b*ArcCos[-1 + d*x^2]])/Sqrt[Pi]]*Sin[a/(2*b)])/(3*d*x)", //
				4913, 4905);
	}

	// {4913, 4908}
	public void test0118() {
		check(//
				"Integrate[(a + b*ArcCos[-1 + d*x^2])^(-7/2), x]", //
				"Sqrt[2*d*x^2 - d^2*x^4]/(5*b*d*x*(a + b*ArcCos[-1 + d*x^2])^(5/2)) + x/(15*b^2*(a + b*ArcCos[-1 + d*x^2])^(3/2)) - Sqrt[2*d*x^2 - d^2*x^4]/(15*b^3*d*x*Sqrt[a + b*ArcCos[-1 + d*x^2]]) + (2*(b^(-1))^(7/2)*Sqrt[Pi]*Cos[a/(2*b)]*Cos[ArcCos[-1 + d*x^2]/2]*FresnelC[(Sqrt[b^(-1)]*Sqrt[a + b*ArcCos[-1 + d*x^2]])/Sqrt[Pi]])/(15*d*x) + (2*(b^(-1))^(7/2)*Sqrt[Pi]*Cos[ArcCos[-1 + d*x^2]/2]*FresnelS[(Sqrt[b^(-1)]*Sqrt[a + b*ArcCos[-1 + d*x^2]])/Sqrt[Pi]]*Sin[a/(2*b)])/(15*d*x)", //
				4913, 4908);
	}

	// {4921, 4517}
	public void test0119() {
		check(//
				"Integrate[E^ArcCos[a*x], x]", //
				"(E^ArcCos[a*x]*x)/2 - (E^ArcCos[a*x]*Sqrt[1 - a^2*x^2])/(2*a)", //
				4921, 4517);
	}

	// {4810, 3383}
	public void test0120() {
		check(//
				"Integrate[x/(Sqrt[1 - x^2]*ArcCos[x]), x]", //
				"-CosIntegral[ArcCos[x]]", //
				4810, 3383);
	}

	// {4919, 4738}
	public void test0121() {
		check(//
				"Integrate[ArcCos[Sqrt[1 + b*x^2]]^n/Sqrt[1 + b*x^2], x]", //
				"-((Sqrt[-(b*x^2)]*ArcCos[Sqrt[1 + b*x^2]]^(1 + n))/(b*(1 + n)*x))", //
				4919, 4738);
	}

	// {4919, 4736}
	public void test0122() {
		check(//
				"Integrate[1/(Sqrt[1 + b*x^2]*ArcCos[Sqrt[1 + b*x^2]]), x]", //
				"-((Sqrt[-(b*x^2)]*Log[ArcCos[Sqrt[1 + b*x^2]]])/(b*x))", //
				4919, 4736);
	}

	// {4958, 371}
	public void test0123() {
		check(//
				"Integrate[(d*x)^m*(a + b*ArcTan[c*x]), x]", //
				"((d*x)^(1 + m)*(a + b*ArcTan[c*x]))/(d*(1 + m)) - (b*c*(d*x)^(2 + m)*Hypergeometric2F1[1, (2 + m)/2, (4 + m)/2, -(c^2*x^2)])/(d^2*(1 + m)*(2 + m))", //
				4958, 371);
	}

	// {4946, 266}
	public void test0124() {
		check(//
				"Integrate[x*(a + b*ArcTan[c*x^2]), x]", //
				"(x^2*(a + b*ArcTan[c*x^2]))/2 - (b*Log[1 + c^2*x^4])/(4*c)", //
				4946, 266);
	}

	// {4958, 371}
	public void test0125() {
		check(//
				"Integrate[(d*x)^m*(a + b*ArcTan[c*x^2]), x]", //
				"((d*x)^(1 + m)*(a + b*ArcTan[c*x^2]))/(d*(1 + m)) - (2*b*c*(d*x)^(3 + m)*Hypergeometric2F1[1, (3 + m)/4, (7 + m)/4, -(c^2*x^4)])/(d^3*(1 + m)*(3 + m))", //
				4958, 371);
	}

	// {4946, 266}
	public void test0126() {
		check(//
				"Integrate[x^2*(a + b*ArcTan[c*x^3]), x]", //
				"(x^3*(a + b*ArcTan[c*x^3]))/3 - (b*Log[1 + c^2*x^6])/(6*c)", //
				4946, 266);
	}

	// {4958, 371}
	public void test0127() {
		check(//
				"Integrate[(d*x)^m*(a + b*ArcTan[c*x^3]), x]", //
				"((d*x)^(1 + m)*(a + b*ArcTan[c*x^3]))/(d*(1 + m)) - (3*b*c*(d*x)^(4 + m)*Hypergeometric2F1[1, (4 + m)/6, (10 + m)/6, -(c^2*x^6)])/(d^4*(1 + m)*(4 + m))", //
				4958, 371);
	}

	// {4946, 266}
	public void test0128() {
		check(//
				"Integrate[(a + b*ArcTan[c/x])/x^2, x]", //
				"-((a + b*ArcTan[c/x])/x) + (b*Log[1 + c^2/x^2])/(2*c)", //
				4946, 266);
	}

	// {4946, 31}
	public void test0129() {
		check(//
				"Integrate[ArcTan[Sqrt[x]]/Sqrt[x], x]", //
				"2*Sqrt[x]*ArcTan[Sqrt[x]] - Log[1 + x]", //
				4946, 31);
	}

	// {4988, 2497}
	public void test0130() {
		check(//
				"Integrate[(a + b*ArcTan[c*x])/(x*(d + I*c*d*x)), x]", //
				"((a + b*ArcTan[c*x])*Log[2 - 2/(1 + I*c*x)])/d + ((I/2)*b*PolyLog[2, -1 + 2/(1 + I*c*x)])/d", //
				4988, 2497);
	}

	// {5050}
	public void test0131() {
		check(//
				"Integrate[x*(c + a^2*c*x^2)*ArcTan[a*x], x]", //
				"-(c*x)/(4*a) - (a*c*x^3)/12 + (c*(1 + a^2*x^2)^2*ArcTan[a*x])/(4*a^2)", //
				5050);
	}

	// {5054, 5004}
	public void test0132() {
		check(//
				"Integrate[(x^2*ArcTan[a*x])/(c + a^2*c*x^2)^2, x]", //
				"-1/(4*a^3*c^2*(1 + a^2*x^2)) - (x*ArcTan[a*x])/(2*a^2*c^2*(1 + a^2*x^2)) + ArcTan[a*x]^2/(4*a^3*c^2)", //
				5054, 5004);
	}

	// {5012, 267}
	public void test0133() {
		check(//
				"Integrate[ArcTan[a*x]/(c + a^2*c*x^2)^2, x]", //
				"1/(4*a*c^2*(1 + a^2*x^2)) + (x*ArcTan[a*x])/(2*c^2*(1 + a^2*x^2)) + ArcTan[a*x]^2/(4*a*c^2)", //
				5012, 267);
	}

	// {5010, 5006}
	public void test0134() {
		check(//
				"Integrate[ArcTan[a*x]/Sqrt[c + a^2*c*x^2], x]", //
				"((-2*I)*Sqrt[1 + a^2*x^2]*ArcTan[a*x]*ArcTan[Sqrt[1 + I*a*x]/Sqrt[1 - I*a*x]])/(a*Sqrt[c + a^2*c*x^2]) + (I*Sqrt[1 + a^2*x^2]*PolyLog[2, ((-I)*Sqrt[1 + I*a*x])/Sqrt[1 - I*a*x]])/(a*Sqrt[c + a^2*c*x^2]) - (I*Sqrt[1 + a^2*x^2]*PolyLog[2, (I*Sqrt[1 + I*a*x])/Sqrt[1 - I*a*x]])/(a*Sqrt[c + a^2*c*x^2])", //
				5010, 5006);
	}

	// {5078, 5074}
	public void test0135() {
		check(//
				"Integrate[ArcTan[a*x]/(x*Sqrt[c + a^2*c*x^2]), x]", //
				"(-2*Sqrt[1 + a^2*x^2]*ArcTan[a*x]*ArcTanh[Sqrt[1 + I*a*x]/Sqrt[1 - I*a*x]])/Sqrt[c + a^2*c*x^2] + (I*Sqrt[1 + a^2*x^2]*PolyLog[2, -(Sqrt[1 + I*a*x]/Sqrt[1 - I*a*x])])/Sqrt[c + a^2*c*x^2] - (I*Sqrt[1 + a^2*x^2]*PolyLog[2, Sqrt[1 + I*a*x]/Sqrt[1 - I*a*x]])/Sqrt[c + a^2*c*x^2]", //
				5078, 5074);
	}

	// {5050, 197}
	public void test0136() {
		check(//
				"Integrate[(x*ArcTan[a*x])/(c + a^2*c*x^2)^(3/2), x]", //
				"x/(a*c*Sqrt[c + a^2*c*x^2]) - ArcTan[a*x]/(a^2*c*Sqrt[c + a^2*c*x^2])", //
				5050, 197);
	}

	// {5016, 5014}
	public void test0137() {
		check(//
				"Integrate[ArcTan[a*x]/(c + a^2*c*x^2)^(5/2), x]", //
				"1/(9*a*c*(c + a^2*c*x^2)^(3/2)) + 2/(3*a*c^2*Sqrt[c + a^2*c*x^2]) + (x*ArcTan[a*x])/(3*c*(c + a^2*c*x^2)^(3/2)) + (2*x*ArcTan[a*x])/(3*c^2*Sqrt[c + a^2*c*x^2])", //
				5016, 5014);
	}

	// {5050, 5014}
	public void test0138() {
		check(//
				"Integrate[(x*ArcTan[a*x]^2)/(c + a^2*c*x^2)^(3/2), x]", //
				"2/(a^2*c*Sqrt[c + a^2*c*x^2]) + (2*x*ArcTan[a*x])/(a*c*Sqrt[c + a^2*c*x^2]) - ArcTan[a*x]^2/(a^2*c*Sqrt[c + a^2*c*x^2])", //
				5050, 5014);
	}

	// {5018, 197}
	public void test0139() {
		check(//
				"Integrate[ArcTan[a*x]^2/(c + a^2*c*x^2)^(3/2), x]", //
				"(-2*x)/(c*Sqrt[c + a^2*c*x^2]) + (2*ArcTan[a*x])/(a*c*Sqrt[c + a^2*c*x^2]) + (x*ArcTan[a*x]^2)/(c*Sqrt[c + a^2*c*x^2])", //
				5018, 197);
	}

	// {5018, 5014}
	public void test0140() {
		check(//
				"Integrate[ArcTan[a*x]^3/(c + a^2*c*x^2)^(3/2), x]", //
				"-6/(a*c*Sqrt[c + a^2*c*x^2]) - (6*x*ArcTan[a*x])/(c*Sqrt[c + a^2*c*x^2]) + (3*ArcTan[a*x]^2)/(a*c*Sqrt[c + a^2*c*x^2]) + (x*ArcTan[a*x]^3)/(c*Sqrt[c + a^2*c*x^2])", //
				5018, 5014);
	}

	// {5046}
	public void test0141() {
		check(//
				"Integrate[x^3/((1 + a^2*x^2)*ArcTan[a*x]^3) - (3*x^2)/(2*a*ArcTan[a*x]^2), x]", //
				"-x^3/(2*a*ArcTan[a*x]^2)", //
				5046);
	}

	// {5155, 4968}
	public void test0168() {
		check(//
				"Integrate[(a + b*ArcTan[c + d*x])^2/(e + f*x), x]", //
				"-(((a + b*ArcTan[c + d*x])^2*Log[2/(1 - I*(c + d*x))])/f) + ((a + b*ArcTan[c + d*x])^2*Log[(2*d*(e + f*x))/((d*e + I*f - c*f)*(1 - I*(c + d*x)))])/f + (I*b*(a + b*ArcTan[c + d*x])*PolyLog[2, 1 - 2/(1 - I*(c + d*x))])/f - (I*b*(a + b*ArcTan[c + d*x])*PolyLog[2, 1 - (2*d*(e + f*x))/((d*e + I*f - c*f)*(1 - I*(c + d*x)))])/f - (b^2*PolyLog[3, 1 - 2/(1 - I*(c + d*x))])/(2*f) + (b^2*PolyLog[3, 1 - (2*d*(e + f*x))/((d*e + I*f - c*f)*(1 - I*(c + d*x)))])/(2*f)", //
				5155, 4968);
	}

	// {5155, 4970}
	public void test0169() {
		check(//
				"Integrate[(a + b*ArcTan[c + d*x])^3/(e + f*x), x]", //
				"-(((a + b*ArcTan[c + d*x])^3*Log[2/(1 - I*(c + d*x))])/f) + ((a + b*ArcTan[c + d*x])^3*Log[(2*d*(e + f*x))/((d*e + I*f - c*f)*(1 - I*(c + d*x)))])/f + (((3*I)/2)*b*(a + b*ArcTan[c + d*x])^2*PolyLog[2, 1 - 2/(1 - I*(c + d*x))])/f - (((3*I)/2)*b*(a + b*ArcTan[c + d*x])^2*PolyLog[2, 1 - (2*d*(e + f*x))/((d*e + I*f - c*f)*(1 - I*(c + d*x)))])/f - (3*b^2*(a + b*ArcTan[c + d*x])*PolyLog[3, 1 - 2/(1 - I*(c + d*x))])/(2*f) + (3*b^2*(a + b*ArcTan[c + d*x])*PolyLog[3, 1 - (2*d*(e + f*x))/((d*e + I*f - c*f)*(1 - I*(c + d*x)))])/(2*f) - (((3*I)/4)*b^3*PolyLog[4, 1 - 2/(1 - I*(c + d*x))])/f + (((3*I)/4)*b^3*PolyLog[4, 1 - (2*d*(e + f*x))/((d*e + I*f - c*f)*(1 - I*(c + d*x)))])/f", //
				5155, 4970);
	}

	// {5163, 5006}
	public void test0170() {
		check(//
				"Integrate[ArcTan[a + b*x]/Sqrt[1 + a^2 + 2*a*b*x + b^2*x^2], x]", //
				"((-2*I)*ArcTan[a + b*x]*ArcTan[Sqrt[1 + I*(a + b*x)]/Sqrt[1 - I*(a + b*x)]])/b + (I*PolyLog[2, ((-I)*Sqrt[1 + I*(a + b*x)])/Sqrt[1 - I*(a + b*x)]])/b - (I*PolyLog[2, (I*Sqrt[1 + I*(a + b*x)])/Sqrt[1 - I*(a + b*x)]])/b", //
				5163, 5006);
	}

	// {5170, 138}
	public void test0171() {
		check(//
				"Integrate[E^(((5*I)/2)*ArcTan[a*x])*x^m, x]", //
				"(x^(1 + m)*AppellF1[1 + m, 5/4, -5/4, 2 + m, I*a*x, (-I)*a*x])/(1 + m)", //
				5170, 138);
	}

	// {5170, 138}
	public void test0172() {
		check(//
				"Integrate[E^(((3*I)/2)*ArcTan[a*x])*x^m, x]", //
				"(x^(1 + m)*AppellF1[1 + m, 3/4, -3/4, 2 + m, I*a*x, (-I)*a*x])/(1 + m)", //
				5170, 138);
	}

	// {5170, 138}
	public void test0173() {
		check(//
				"Integrate[E^((I/2)*ArcTan[a*x])*x^m, x]", //
				"(x^(1 + m)*AppellF1[1 + m, 1/4, -1/4, 2 + m, I*a*x, (-I)*a*x])/(1 + m)", //
				5170, 138);
	}

	// {5170, 138}
	public void test0174() {
		check(//
				"Integrate[x^m/E^((I/2)*ArcTan[a*x]), x]", //
				"(x^(1 + m)*AppellF1[1 + m, -1/4, 1/4, 2 + m, I*a*x, (-I)*a*x])/(1 + m)", //
				5170, 138);
	}

	// {5170, 138}
	public void test0175() {
		check(//
				"Integrate[x^m/E^(((3*I)/2)*ArcTan[a*x]), x]", //
				"(x^(1 + m)*AppellF1[1 + m, -3/4, 3/4, 2 + m, I*a*x, (-I)*a*x])/(1 + m)", //
				5170, 138);
	}

	// {5170, 138}
	public void test0176() {
		check(//
				"Integrate[x^m/E^(((5*I)/2)*ArcTan[a*x]), x]", //
				"(x^(1 + m)*AppellF1[1 + m, -5/4, 5/4, 2 + m, I*a*x, (-I)*a*x])/(1 + m)", //
				5170, 138);
	}

	// {5170, 138}
	public void test0177() {
		check(//
				"Integrate[E^((2*ArcTan[x])/3)*x^m, x]", //
				"(x^(1 + m)*AppellF1[1 + m, -I/3, I/3, 2 + m, I*x, (-I)*x])/(1 + m)", //
				5170, 138);
	}

	// {5170, 138}
	public void test0178() {
		check(//
				"Integrate[E^(ArcTan[x]/3)*x^m, x]", //
				"(x^(1 + m)*AppellF1[1 + m, -I/6, I/6, 2 + m, I*x, (-I)*x])/(1 + m)", //
				5170, 138);
	}

	// {5170, 138}
	public void test0179() {
		check(//
				"Integrate[E^((I/4)*ArcTan[a*x])*x^m, x]", //
				"(x^(1 + m)*AppellF1[1 + m, 1/8, -1/8, 2 + m, I*a*x, (-I)*a*x])/(1 + m)", //
				5170, 138);
	}

	// {5170, 138}
	public void test0180() {
		check(//
				"Integrate[E^(I*n*ArcTan[a*x])*x^m, x]", //
				"(x^(1 + m)*AppellF1[1 + m, n/2, -n/2, 2 + m, I*a*x, (-I)*a*x])/(1 + m)", //
				5170, 138);
	}

	// {5169, 71}
	public void test0181() {
		check(//
				"Integrate[E^(I*n*ArcTan[a*x]), x]", //
				"(I*2^(1 + n/2)*(1 - I*a*x)^(1 - n/2)*Hypergeometric2F1[1 - n/2, -n/2, 2 - n/2, (1 - I*a*x)/2])/(a*(2 - n))", //
				5169, 71);
	}

	// {5170, 133}
	public void test0182() {
		check(//
				"Integrate[E^(I*n*ArcTan[a*x])/x^2, x]", //
				"((-4*I)*a*(1 - I*a*x)^(1 - n/2)*(1 + I*a*x)^((-2 + n)/2)*Hypergeometric2F1[2, 1 - n/2, 2 - n/2, (1 - I*a*x)/(1 + I*a*x)])/(2 - n)", //
				5170, 133);
	}

	// {5201, 71}
	public void test0183() {
		check(//
				"Integrate[E^(n*ArcTan[a + b*x]), x]", //
				"-((2^(1 - (I/2)*n)*(1 - I*a - I*b*x)^(1 + (I/2)*n)*Hypergeometric2F1[1 + (I/2)*n, (I/2)*n, 2 + (I/2)*n, (1 - I*a - I*b*x)/2])/(b*(2*I - n)))", //
				5201, 71);
	}

	// {5203, 133}
	public void test0184() {
		check(//
				"Integrate[E^(n*ArcTan[a + b*x])/x^2, x]", //
				"(-4*b*(1 - I*a - I*b*x)^(1 + (I/2)*n)*(1 + I*a + I*b*x)^(-1 - (I/2)*n)*Hypergeometric2F1[2, 1 + (I/2)*n, 2 + (I/2)*n, ((I - a)*(1 - I*a - I*b*x))/((I + a)*(1 + I*a + I*b*x))])/((I + a)^2*(2*I - n))", //
				5203, 133);
	}

	// {5181, 71}
	public void test0185() {
		check(//
				"Integrate[E^ArcTan[a*x]*(c + a^2*c*x^2)^2, x]", //
				"((1/37 + (6*I)/37)*2^(3 - I/2)*c^2*(1 - I*a*x)^(3 + I/2)*Hypergeometric2F1[-2 + I/2, 3 + I/2, 4 + I/2, (1 - I*a*x)/2])/a", //
				5181, 71);
	}

	// {5181, 71}
	public void test0186() {
		check(//
				"Integrate[E^ArcTan[a*x]*(c + a^2*c*x^2), x]", //
				"((1/17 + (4*I)/17)*2^(2 - I/2)*c*(1 - I*a*x)^(2 + I/2)*Hypergeometric2F1[-1 + I/2, 2 + I/2, 3 + I/2, (1 - I*a*x)/2])/a", //
				5181, 71);
	}

	// {5169, 71}
	public void test0187() {
		check(//
				"Integrate[E^ArcTan[a*x], x]", //
				"((1/5 + (2*I)/5)*2^(1 - I/2)*(1 - I*a*x)^(1 + I/2)*Hypergeometric2F1[I/2, 1 + I/2, 2 + I/2, (1 - I*a*x)/2])/a", //
				5169, 71);
	}

	// {5178, 5179}
	public void test0188() {
		check(//
				"Integrate[E^ArcTan[a*x]/(c + a^2*c*x^2)^2, x]", //
				"(2*E^ArcTan[a*x])/(5*a*c^2) + (E^ArcTan[a*x]*(1 + 2*a*x))/(5*a*c^2*(1 + a^2*x^2))", //
				5178, 5179);
	}

	// {5178, 5177}
	public void test0189() {
		check(//
				"Integrate[E^ArcTan[a*x]/(c + a^2*c*x^2)^(5/2), x]", //
				"(E^ArcTan[a*x]*(1 + 3*a*x))/(10*a*c*(c + a^2*c*x^2)^(3/2)) + (3*E^ArcTan[a*x]*(1 + a*x))/(10*a*c^2*Sqrt[c + a^2*c*x^2])", //
				5178, 5177);
	}

	// {5181, 71}
	public void test0190() {
		check(//
				"Integrate[E^(2*ArcTan[a*x])*(c + a^2*c*x^2)^2, x]", //
				"((1/5 + (3*I)/5)*2^(1 - I)*c^2*(1 - I*a*x)^(3 + I)*Hypergeometric2F1[-2 + I, 3 + I, 4 + I, (1 - I*a*x)/2])/a", //
				5181, 71);
	}

	// {5181, 71}
	public void test0191() {
		check(//
				"Integrate[E^(2*ArcTan[a*x])*(c + a^2*c*x^2), x]", //
				"((1/5 + (2*I)/5)*2^(1 - I)*c*(1 - I*a*x)^(2 + I)*Hypergeometric2F1[-1 + I, 2 + I, 3 + I, (1 - I*a*x)/2])/a", //
				5181, 71);
	}

	// {5169, 71}
	public void test0192() {
		check(//
				"Integrate[E^(2*ArcTan[a*x]), x]", //
				"((1 + I)*(1 - I*a*x)^(1 + I)*Hypergeometric2F1[I, 1 + I, 2 + I, (1 - I*a*x)/2])/(2^(1 + I)*a)", //
				5169, 71);
	}

	// {5178, 5179}
	public void test0193() {
		check(//
				"Integrate[E^(2*ArcTan[a*x])/(c + a^2*c*x^2)^2, x]", //
				"E^(2*ArcTan[a*x])/(8*a*c^2) + (E^(2*ArcTan[a*x])*(1 + a*x))/(4*a*c^2*(1 + a^2*x^2))", //
				5178, 5179);
	}

	// {5178, 5177}
	public void test0194() {
		check(//
				"Integrate[E^(2*ArcTan[a*x])/(c + a^2*c*x^2)^(5/2), x]", //
				"(E^(2*ArcTan[a*x])*(2 + 3*a*x))/(13*a*c*(c + a^2*c*x^2)^(3/2)) + (6*E^(2*ArcTan[a*x])*(2 + a*x))/(65*a*c^2*Sqrt[c + a^2*c*x^2])", //
				5178, 5177);
	}

	// {5181, 71}
	public void test0195() {
		check(//
				"Integrate[(c + a^2*c*x^2)^2/E^ArcTan[a*x], x]", //
				"((-1/37 + (6*I)/37)*2^(3 + I/2)*c^2*(1 - I*a*x)^(3 - I/2)*Hypergeometric2F1[-2 - I/2, 3 - I/2, 4 - I/2, (1 - I*a*x)/2])/a", //
				5181, 71);
	}

	// {5181, 71}
	public void test0196() {
		check(//
				"Integrate[(c + a^2*c*x^2)/E^ArcTan[a*x], x]", //
				"((-1/17 + (4*I)/17)*2^(2 + I/2)*c*(1 - I*a*x)^(2 - I/2)*Hypergeometric2F1[-1 - I/2, 2 - I/2, 3 - I/2, (1 - I*a*x)/2])/a", //
				5181, 71);
	}

	// {5169, 71}
	public void test0197() {
		check(//
				"Integrate[E^(-ArcTan[a*x]), x]", //
				"((-1/5 + (2*I)/5)*2^(1 + I/2)*(1 - I*a*x)^(1 - I/2)*Hypergeometric2F1[-I/2, 1 - I/2, 2 - I/2, (1 - I*a*x)/2])/a", //
				5169, 71);
	}

	// {5178, 5179}
	public void test0198() {
		check(//
				"Integrate[1/(E^ArcTan[a*x]*(c + a^2*c*x^2)^2), x]", //
				"-2/(5*a*c^2*E^ArcTan[a*x]) - (1 - 2*a*x)/(5*a*c^2*E^ArcTan[a*x]*(1 + a^2*x^2))", //
				5178, 5179);
	}

	// {5178, 5177}
	public void test0199() {
		check(//
				"Integrate[1/(E^ArcTan[a*x]*(c + a^2*c*x^2)^(5/2)), x]", //
				"-(1 - 3*a*x)/(10*a*c*E^ArcTan[a*x]*(c + a^2*c*x^2)^(3/2)) - (3*(1 - a*x))/(10*a*c^2*E^ArcTan[a*x]*Sqrt[c + a^2*c*x^2])", //
				5178, 5177);
	}

	// {5181, 71}
	public void test0200() {
		check(//
				"Integrate[(c + a^2*c*x^2)^2/E^(2*ArcTan[a*x]), x]", //
				"((-1/5 + (3*I)/5)*2^(1 + I)*c^2*(1 - I*a*x)^(3 - I)*Hypergeometric2F1[-2 - I, 3 - I, 4 - I, (1 - I*a*x)/2])/a", //
				5181, 71);
	}

	// {5181, 71}
	public void test0201() {
		check(//
				"Integrate[(c + a^2*c*x^2)/E^(2*ArcTan[a*x]), x]", //
				"((-1/5 + (2*I)/5)*2^(1 + I)*c*(1 - I*a*x)^(2 - I)*Hypergeometric2F1[-1 - I, 2 - I, 3 - I, (1 - I*a*x)/2])/a", //
				5181, 71);
	}

	// {5169, 71}
	public void test0202() {
		check(//
				"Integrate[E^(-2*ArcTan[a*x]), x]", //
				"((-1 + I)*(1 - I*a*x)^(1 - I)*Hypergeometric2F1[-I, 1 - I, 2 - I, (1 - I*a*x)/2])/(2^(1 - I)*a)", //
				5169, 71);
	}

	// {5178, 5179}
	public void test0203() {
		check(//
				"Integrate[1/(E^(2*ArcTan[a*x])*(c + a^2*c*x^2)^2), x]", //
				"-1/(8*a*c^2*E^(2*ArcTan[a*x])) - (1 - a*x)/(4*a*c^2*E^(2*ArcTan[a*x])*(1 + a^2*x^2))", //
				5178, 5179);
	}

	// {5178, 5177}
	public void test0204() {
		check(//
				"Integrate[1/(E^(2*ArcTan[a*x])*(c + a^2*c*x^2)^(5/2)), x]", //
				"-(2 - 3*a*x)/(13*a*c*E^(2*ArcTan[a*x])*(c + a^2*c*x^2)^(3/2)) - (6*(2 - a*x))/(65*a*c^2*E^(2*ArcTan[a*x])*Sqrt[c + a^2*c*x^2])", //
				5178, 5177);
	}

	// {5181, 31}
	public void test0205() {
		check(//
				"Integrate[E^(I*ArcTan[a*x])/Sqrt[1 + a^2*x^2], x]", //
				"(I*Log[I + a*x])/a", //
				5181, 31);
	}

	// {5181, 31}
	public void test0206() {
		check(//
				"Integrate[1/(E^(I*ArcTan[a*x])*Sqrt[1 + a^2*x^2]), x]", //
				"((-I)*Log[I - a*x])/a", //
				5181, 31);
	}

	// {5181, 32}
	public void test0207() {
		check(//
				"Integrate[E^((3*I)*ArcTan[a*x])/(1 + a^2*x^2)^(3/2), x]", //
				"(-I/2)/(a*(1 - I*a*x)^2)", //
				5181, 32);
	}

	// {5181, 32}
	public void test0208() {
		check(//
				"Integrate[1/(E^((3*I)*ArcTan[a*x])*(1 + a^2*x^2)^(3/2)), x]", //
				"(I/2)/(a*(1 + I*a*x)^2)", //
				5181, 32);
	}

	// {5181, 71}
	public void test0209() {
		check(//
				"Integrate[E^(n*ArcTan[a*x])*(c + a^2*c*x^2)^2, x]", //
				"-((2^(3 - (I/2)*n)*c^2*(1 - I*a*x)^(3 + (I/2)*n)*Hypergeometric2F1[-2 + (I/2)*n, 3 + (I/2)*n, 4 + (I/2)*n, (1 - I*a*x)/2])/(a*(6*I - n)))", //
				5181, 71);
	}

	// {5181, 71}
	public void test0210() {
		check(//
				"Integrate[E^(n*ArcTan[a*x])*(c + a^2*c*x^2), x]", //
				"-((2^(2 - (I/2)*n)*c*(1 - I*a*x)^(2 + (I/2)*n)*Hypergeometric2F1[-1 + (I/2)*n, 2 + (I/2)*n, 3 + (I/2)*n, (1 - I*a*x)/2])/(a*(4*I - n)))", //
				5181, 71);
	}

	// {5169, 71}
	public void test0211() {
		check(//
				"Integrate[E^(n*ArcTan[a*x]), x]", //
				"-((2^(1 - (I/2)*n)*(1 - I*a*x)^(1 + (I/2)*n)*Hypergeometric2F1[1 + (I/2)*n, (I/2)*n, 2 + (I/2)*n, (1 - I*a*x)/2])/(a*(2*I - n)))", //
				5169, 71);
	}

	// {5190, 138}
	public void test0212() {
		check(//
				"Integrate[E^(n*ArcTan[a*x])*x^m*(c + a^2*c*x^2), x]", //
				"(c*x^(1 + m)*AppellF1[1 + m, -1 - (I/2)*n, -1 + (I/2)*n, 2 + m, I*a*x, (-I)*a*x])/(1 + m)", //
				5190, 138);
	}

	// {5190, 138}
	public void test0213() {
		check(//
				"Integrate[(E^(n*ArcTan[a*x])*x^m)/(c + a^2*c*x^2), x]", //
				"(x^(1 + m)*AppellF1[1 + m, 1 - (I/2)*n, 1 + (I/2)*n, 2 + m, I*a*x, (-I)*a*x])/(c*(1 + m))", //
				5190, 138);
	}

	// {5190, 138}
	public void test0214() {
		check(//
				"Integrate[(E^(n*ArcTan[a*x])*x^m)/(c + a^2*c*x^2)^2, x]", //
				"(x^(1 + m)*AppellF1[1 + m, 2 - (I/2)*n, 2 + (I/2)*n, 2 + m, I*a*x, (-I)*a*x])/(c^2*(1 + m))", //
				5190, 138);
	}

	// {5190, 138}
	public void test0215() {
		check(//
				"Integrate[(E^(n*ArcTan[a*x])*x^m)/(c + a^2*c*x^2)^3, x]", //
				"(x^(1 + m)*AppellF1[1 + m, 3 - (I/2)*n, 3 + (I/2)*n, 2 + m, I*a*x, (-I)*a*x])/(c^3*(1 + m))", //
				5190, 138);
	}

	// {5190, 82}
	public void test0216() {
		check(//
				"Integrate[(E^((6*I)*ArcTan[a*x])*x^2)/(c + a^2*c*x^2)^19, x]", //
				"-(I + 6*a*x)/(210*a^3*c^19*(1 - I*a*x)^21*(1 + I*a*x)^15)", //
				5190, 82);
	}

	// {5190, 82}
	public void test0217() {
		check(//
				"Integrate[(E^((4*I)*ArcTan[a*x])*x^2)/(c + a^2*c*x^2)^9, x]", //
				"-(I + 4*a*x)/(60*a^3*c^9*(1 - I*a*x)^10*(1 + I*a*x)^6)", //
				5190, 82);
	}

	// {5190, 82}
	public void test0218() {
		check(//
				"Integrate[(E^((2*I)*ArcTan[a*x])*x^2)/(c + a^2*c*x^2)^3, x]", //
				"-(I + 2*a*x)/(6*a^3*c^3*(1 - I*a*x)^3*(1 + I*a*x))", //
				5190, 82);
	}

	// {5190, 82}
	public void test0219() {
		check(//
				"Integrate[x^2/(E^((2*I)*ArcTan[a*x])*(c + a^2*c*x^2)^3), x]", //
				"(I - 2*a*x)/(6*a^3*c^3*(1 - I*a*x)*(1 + I*a*x)^3)", //
				5190, 82);
	}

	// {5190, 82}
	public void test0220() {
		check(//
				"Integrate[x^2/(E^((4*I)*ArcTan[a*x])*(c + a^2*c*x^2)^9), x]", //
				"(I - 4*a*x)/(60*a^3*c^9*(1 - I*a*x)^6*(1 + I*a*x)^10)", //
				5190, 82);
	}

	// {5259, 270}
	public void test0221() {
		check(//
				"Integrate[ArcTan[(Sqrt[-e]*x)/Sqrt[d + e*x^2]]/x^3, x]", //
				"-(Sqrt[-e]*Sqrt[d + e*x^2])/(2*d*x) - ArcTan[(Sqrt[-e]*x)/Sqrt[d + e*x^2]]/(2*x^2)", //
				5259, 270);
	}

	// {5255, 267}
	public void test0222() {
		check(//
				"Integrate[ArcTan[(Sqrt[-e]*x)/Sqrt[d + e*x^2]], x]", //
				"Sqrt[d + e*x^2]/Sqrt[-e] + x*ArcTan[(Sqrt[-e]*x)/Sqrt[d + e*x^2]]", //
				5255, 267);
	}

	// {2199, 30}
	public void test0223() {
		check(//
				"Integrate[x^m*ArcTan[Tan[a + b*x]], x]", //
				"-((b*x^(2 + m))/(2 + 3*m + m^2)) + (x^(1 + m)*ArcTan[Tan[a + b*x]])/(1 + m)", //
				2199, 30);
	}

	// {2199, 30}
	public void test0224() {
		check(//
				"Integrate[x^2*ArcTan[Tan[a + b*x]], x]", //
				"-(b*x^4)/12 + (x^3*ArcTan[Tan[a + b*x]])/3", //
				2199, 30);
	}

	// {5279, 30}
	public void test0225() {
		check(//
				"Integrate[x*ArcTan[Tan[a + b*x]], x]", //
				"-(b*x^3)/6 + (x^2*ArcTan[Tan[a + b*x]])/2", //
				5279, 30);
	}

	// {2188, 30}
	public void test0226() {
		check(//
				"Integrate[ArcTan[Tan[a + b*x]], x]", //
				"ArcTan[Tan[a + b*x]]^2/(2*b)", //
				2188, 30);
	}

	// {2189, 29}
	public void test0227() {
		check(//
				"Integrate[ArcTan[Tan[a + b*x]]/x, x]", //
				"b*x - (b*x - ArcTan[Tan[a + b*x]])*Log[x]", //
				2189, 29);
	}

	// {2199, 30}
	public void test0228() {
		check(//
				"Integrate[x^m*ArcTan[Cot[a + b*x]], x]", //
				"(b*x^(2 + m))/(2 + 3*m + m^2) + (x^(1 + m)*ArcTan[Cot[a + b*x]])/(1 + m)", //
				2199, 30);
	}

	// {2199, 30}
	public void test0229() {
		check(//
				"Integrate[x^2*ArcTan[Cot[a + b*x]], x]", //
				"(b*x^4)/12 + (x^3*ArcTan[Cot[a + b*x]])/3", //
				2199, 30);
	}

	// {5281, 30}
	public void test0230() {
		check(//
				"Integrate[x*ArcTan[Cot[a + b*x]], x]", //
				"(b*x^3)/6 + (x^2*ArcTan[Cot[a + b*x]])/2", //
				5281, 30);
	}

	// {2188, 30}
	public void test0231() {
		check(//
				"Integrate[ArcTan[Cot[a + b*x]], x]", //
				"-ArcTan[Cot[a + b*x]]^2/(2*b)", //
				2188, 30);
	}

	// {2189, 29}
	public void test0232() {
		check(//
				"Integrate[ArcTan[Cot[a + b*x]]/x, x]", //
				"-(b*x) + (b*x + ArcTan[Cot[a + b*x]])*Log[x]", //
				2189, 29);
	}

	// {2188, 30}
	public void test0233() {
		check(//
				"Integrate[ArcTan[Tan[a + b*x]], x]", //
				"ArcTan[Tan[a + b*x]]^2/(2*b)", //
				2188, 30);
	}

	// {2188, 30}
	public void test0234() {
		check(//
				"Integrate[ArcTan[Cot[a + b*x]], x]", //
				"-ArcTan[Cot[a + b*x]]^2/(2*b)", //
				2188, 30);
	}

	// {5265, 5263}
	public void test0235() {
		check(//
				"Integrate[ArcTan[(c*x)/Sqrt[a - c^2*x^2]]^m/Sqrt[d - (c^2*d*x^2)/a], x]", //
				"(Sqrt[a - c^2*x^2]*ArcTan[(c*x)/Sqrt[a - c^2*x^2]]^(1 + m))/(c*(1 + m)*Sqrt[d - (c^2*d*x^2)/a])", //
				5265, 5263);
	}

	// {5265, 5263}
	public void test0236() {
		check(//
				"Integrate[ArcTan[(c*x)/Sqrt[a - c^2*x^2]]^2/Sqrt[d - (c^2*d*x^2)/a], x]", //
				"(Sqrt[a - c^2*x^2]*ArcTan[(c*x)/Sqrt[a - c^2*x^2]]^3)/(3*c*Sqrt[d - (c^2*d*x^2)/a])", //
				5265, 5263);
	}

	// {5265, 5263}
	public void test0237() {
		check(//
				"Integrate[ArcTan[(c*x)/Sqrt[a - c^2*x^2]]/Sqrt[d - (c^2*d*x^2)/a], x]", //
				"(Sqrt[a - c^2*x^2]*ArcTan[(c*x)/Sqrt[a - c^2*x^2]]^2)/(2*c*Sqrt[d - (c^2*d*x^2)/a])", //
				5265, 5263);
	}

	// {5265, 5261}
	public void test0238() {
		check(//
				"Integrate[1/(Sqrt[d - (c^2*d*x^2)/a]*ArcTan[(c*x)/Sqrt[a - c^2*x^2]]), x]", //
				"(Sqrt[a - c^2*x^2]*Log[ArcTan[(c*x)/Sqrt[a - c^2*x^2]]])/(c*Sqrt[d - (c^2*d*x^2)/a])", //
				5265, 5261);
	}

	// {5265, 5263}
	public void test0239() {
		check(//
				"Integrate[1/(Sqrt[d - (c^2*d*x^2)/a]*ArcTan[(c*x)/Sqrt[a - c^2*x^2]]^2), x]", //
				"-(Sqrt[a - c^2*x^2]/(c*Sqrt[d - (c^2*d*x^2)/a]*ArcTan[(c*x)/Sqrt[a - c^2*x^2]]))", //
				5265, 5263);
	}

	// {5265, 5263}
	public void test0240() {
		check(//
				"Integrate[1/(Sqrt[d - (c^2*d*x^2)/a]*ArcTan[(c*x)/Sqrt[a - c^2*x^2]]^3), x]", //
				"-Sqrt[a - c^2*x^2]/(2*c*Sqrt[d - (c^2*d*x^2)/a]*ArcTan[(c*x)/Sqrt[a - c^2*x^2]]^2)", //
				5265, 5263);
	}

	// {5265, 5263}
	public void test0241() {
		check(//
				"Integrate[ArcTan[(e*x)/Sqrt[-((a*e^2)/b) - e^2*x^2]]^m/Sqrt[a + b*x^2], x]", //
				"(Sqrt[-((a*e^2)/b) - e^2*x^2]*ArcTan[(e*x)/Sqrt[-((a*e^2)/b) - e^2*x^2]]^(1 + m))/(e*(1 + m)*Sqrt[a + b*x^2])", //
				5265, 5263);
	}

	// {5265, 5263}
	public void test0242() {
		check(//
				"Integrate[ArcTan[(e*x)/Sqrt[-((a*e^2)/b) - e^2*x^2]]^2/Sqrt[a + b*x^2], x]", //
				"(Sqrt[-((a*e^2)/b) - e^2*x^2]*ArcTan[(e*x)/Sqrt[-((a*e^2)/b) - e^2*x^2]]^3)/(3*e*Sqrt[a + b*x^2])", //
				5265, 5263);
	}

	// {5265, 5263}
	public void test0243() {
		check(//
				"Integrate[ArcTan[(e*x)/Sqrt[-((a*e^2)/b) - e^2*x^2]]/Sqrt[a + b*x^2], x]", //
				"(Sqrt[-((a*e^2)/b) - e^2*x^2]*ArcTan[(e*x)/Sqrt[-((a*e^2)/b) - e^2*x^2]]^2)/(2*e*Sqrt[a + b*x^2])", //
				5265, 5263);
	}

	// {5265, 5261}
	public void test0244() {
		check(//
				"Integrate[1/(Sqrt[a + b*x^2]*ArcTan[(e*x)/Sqrt[-((a*e^2)/b) - e^2*x^2]]), x]", //
				"(Sqrt[-((a*e^2)/b) - e^2*x^2]*Log[ArcTan[(e*x)/Sqrt[-((a*e^2)/b) - e^2*x^2]]])/(e*Sqrt[a + b*x^2])", //
				5265, 5261);
	}

	// {5265, 5263}
	public void test0245() {
		check(//
				"Integrate[1/(Sqrt[a + b*x^2]*ArcTan[(e*x)/Sqrt[-((a*e^2)/b) - e^2*x^2]]^2), x]", //
				"-(Sqrt[-((a*e^2)/b) - e^2*x^2]/(e*Sqrt[a + b*x^2]*ArcTan[(e*x)/Sqrt[-((a*e^2)/b) - e^2*x^2]]))", //
				5265, 5263);
	}

	// {5265, 5263}
	public void test0246() {
		check(//
				"Integrate[1/(Sqrt[a + b*x^2]*ArcTan[(e*x)/Sqrt[-((a*e^2)/b) - e^2*x^2]]^3), x]", //
				"-Sqrt[-((a*e^2)/b) - e^2*x^2]/(2*e*Sqrt[a + b*x^2]*ArcTan[(e*x)/Sqrt[-((a*e^2)/b) - e^2*x^2]]^2)", //
				5265, 5263);
	}

	// {4931, 266}
	public void test0247() {
		check(//
				"Integrate[ArcCot[a*x], x]", //
				"x*ArcCot[a*x] + Log[1 + a^2*x^2]/(2*a)", //
				4931, 266);
	}

	// {4947, 371}
	public void test0248() {
		check(//
				"Integrate[x^m*ArcCot[a*x], x]", //
				"(x^(1 + m)*ArcCot[a*x])/(1 + m) + (a*x^(2 + m)*Hypergeometric2F1[1, (2 + m)/2, (4 + m)/2, -(a^2*x^2)])/(2 + 3*m + m^2)", //
				4947, 371);
	}

	// {5011, 5007}
	public void test0249() {
		check(//
				"Integrate[ArcCot[x]/Sqrt[a + a*x^2], x]", //
				"((-2*I)*Sqrt[1 + x^2]*ArcCot[x]*ArcTan[Sqrt[1 + I*x]/Sqrt[1 - I*x]])/Sqrt[a + a*x^2] - (I*Sqrt[1 + x^2]*PolyLog[2, ((-I)*Sqrt[1 + I*x])/Sqrt[1 - I*x]])/Sqrt[a + a*x^2] + (I*Sqrt[1 + x^2]*PolyLog[2, (I*Sqrt[1 + I*x])/Sqrt[1 - I*x]])/Sqrt[a + a*x^2]", //
				5011, 5007);
	}

	// {5017, 5015}
	public void test0250() {
		check(//
				"Integrate[ArcCot[x]/(a + a*x^2)^(5/2), x]", //
				"-1/(9*a*(a + a*x^2)^(3/2)) - 2/(3*a^2*Sqrt[a + a*x^2]) + (x*ArcCot[x])/(3*a*(a + a*x^2)^(3/2)) + (2*x*ArcCot[x])/(3*a^2*Sqrt[a + a*x^2])", //
				5017, 5015);
	}

	// {5013, 267}
	public void test0251() {
		check(//
				"Integrate[ArcCot[x]/(1 + x^2)^2, x]", //
				"-1/(4*(1 + x^2)) + (x*ArcCot[x])/(2*(1 + x^2)) - ArcCot[x]^2/4", //
				5013, 267);
	}

	// {4947, 266}
	public void test0252() {
		check(//
				"Integrate[x*ArcCot[a*x^2], x]", //
				"(x^2*ArcCot[a*x^2])/2 + Log[1 + a^2*x^4]/(4*a)", //
				4947, 266);
	}

	// {4947, 31}
	public void test0253() {
		check(//
				"Integrate[ArcCot[Sqrt[x]]/Sqrt[x], x]", //
				"2*Sqrt[x]*ArcCot[Sqrt[x]] + Log[1 + x]", //
				4947, 31);
	}

	// {5164, 5007}
	public void test0254() {
		check(//
				"Integrate[ArcCot[a + b*x]/Sqrt[1 + a^2 + 2*a*b*x + b^2*x^2], x]", //
				"((-2*I)*ArcCot[a + b*x]*ArcTan[Sqrt[1 + I*(a + b*x)]/Sqrt[1 - I*(a + b*x)]])/b - (I*PolyLog[2, ((-I)*Sqrt[1 + I*(a + b*x)])/Sqrt[1 - I*(a + b*x)]])/b + (I*PolyLog[2, (I*Sqrt[1 + I*(a + b*x)])/Sqrt[1 - I*(a + b*x)]])/b", //
				5164, 5007);
	}

	// {5156, 4969}
	public void test0255() {
		check(//
				"Integrate[(a + b*ArcCot[c + d*x])^2/(e + f*x), x]", //
				"-(((a + b*ArcCot[c + d*x])^2*Log[2/(1 - I*(c + d*x))])/f) + ((a + b*ArcCot[c + d*x])^2*Log[(2*d*(e + f*x))/((d*e + I*f - c*f)*(1 - I*(c + d*x)))])/f - (I*b*(a + b*ArcCot[c + d*x])*PolyLog[2, 1 - 2/(1 - I*(c + d*x))])/f + (I*b*(a + b*ArcCot[c + d*x])*PolyLog[2, 1 - (2*d*(e + f*x))/((d*e + I*f - c*f)*(1 - I*(c + d*x)))])/f - (b^2*PolyLog[3, 1 - 2/(1 - I*(c + d*x))])/(2*f) + (b^2*PolyLog[3, 1 - (2*d*(e + f*x))/((d*e + I*f - c*f)*(1 - I*(c + d*x)))])/(2*f)", //
				5156, 4969);
	}

	// {5156, 4971}
	public void test0256() {
		check(//
				"Integrate[(a + b*ArcCot[c + d*x])^3/(e + f*x), x]", //
				"-(((a + b*ArcCot[c + d*x])^3*Log[2/(1 - I*(c + d*x))])/f) + ((a + b*ArcCot[c + d*x])^3*Log[(2*d*(e + f*x))/((d*e + I*f - c*f)*(1 - I*(c + d*x)))])/f - (((3*I)/2)*b*(a + b*ArcCot[c + d*x])^2*PolyLog[2, 1 - 2/(1 - I*(c + d*x))])/f + (((3*I)/2)*b*(a + b*ArcCot[c + d*x])^2*PolyLog[2, 1 - (2*d*(e + f*x))/((d*e + I*f - c*f)*(1 - I*(c + d*x)))])/f - (3*b^2*(a + b*ArcCot[c + d*x])*PolyLog[3, 1 - 2/(1 - I*(c + d*x))])/(2*f) + (3*b^2*(a + b*ArcCot[c + d*x])*PolyLog[3, 1 - (2*d*(e + f*x))/((d*e + I*f - c*f)*(1 - I*(c + d*x)))])/(2*f) + (((3*I)/4)*b^3*PolyLog[4, 1 - 2/(1 - I*(c + d*x))])/f - (((3*I)/4)*b^3*PolyLog[4, 1 - (2*d*(e + f*x))/((d*e + I*f - c*f)*(1 - I*(c + d*x)))])/f", //
				5156, 4971);
	}

	// {2188, 30}
	public void test0257() {
		check(//
				"Integrate[ArcCot[Tan[a + b*x]], x]", //
				"-ArcCot[Tan[a + b*x]]^2/(2*b)", //
				2188, 30);
	}

	// {2188, 30}
	public void test0258() {
		check(//
				"Integrate[ArcCot[Cot[a + b*x]], x]", //
				"ArcCot[Cot[a + b*x]]^2/(2*b)", //
				2188, 30);
	}

	// {5210, 133}
	public void test0259() {
		check(//
				"Integrate[E^ArcCot[x], x]", //
				"((4/5 + (8*I)/5)*((-I + x)/x)^(1 + I/2)*Hypergeometric2F1[1 + I/2, 2, 2 + I/2, (1 - I/x)/(1 + I/x)])/((I + x)/x)^(1 + I/2)", //
				5210, 133);
	}

	// {5223, 5221}
	public void test0260() {
		check(//
				"Integrate[E^ArcCot[x]/(a + a*x^2)^2, x]", //
				"(-2*E^ArcCot[x])/(5*a^2) - (E^ArcCot[x]*(1 - 2*x))/(5*a^2*(1 + x^2))", //
				5223, 5221);
	}

	// {5223, 5222}
	public void test0261() {
		check(//
				"Integrate[E^ArcCot[x]/(a + a*x^2)^(5/2), x]", //
				"-(E^ArcCot[x]*(1 - 3*x))/(10*a*(a + a*x^2)^(3/2)) - (3*E^ArcCot[x]*(1 - x))/(10*a^2*Sqrt[a + a*x^2])", //
				5223, 5222);
	}

	// {5328, 197}
	public void test0262() {
		check(//
				"Integrate[x*(a + b*ArcSec[c*x]), x]", //
				"-(b*Sqrt[1 - 1/(c^2*x^2)]*x)/(2*c) + (x^2*(a + b*ArcSec[c*x]))/2", //
				5328, 197);
	}

	// {5328, 267}
	public void test0263() {
		check(//
				"Integrate[(a + b*ArcSec[c*x])/x^2, x]", //
				"b*c*Sqrt[1 - 1/(c^2*x^2)] - (a + b*ArcSec[c*x])/x", //
				5328, 267);
	}

	// {5329, 197}
	public void test0264() {
		check(//
				"Integrate[x*(a + b*ArcCsc[c*x]), x]", //
				"(b*Sqrt[1 - 1/(c^2*x^2)]*x)/(2*c) + (x^2*(a + b*ArcCsc[c*x]))/2", //
				5329, 197);
	}

	// {5329, 267}
	public void test0265() {
		check(//
				"Integrate[(a + b*ArcCsc[c*x])/x^2, x]", //
				"-(b*c*Sqrt[1 - 1/(c^2*x^2)]) - (a + b*ArcCsc[c*x])/x", //
				5329, 267);
	}

}
