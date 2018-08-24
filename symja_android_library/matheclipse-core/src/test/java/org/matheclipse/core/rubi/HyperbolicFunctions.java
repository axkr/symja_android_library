package org.matheclipse.core.rubi;

public class HyperbolicFunctions extends AbstractRubiTestCase {

	public HyperbolicFunctions(String name) {
		super(name, false);
	}

	// {2638}
	public void test0009() {
		check("Integrate[Sinh[a + b*x], x]", "Cosh[a + b*x]/b", //
				2638);
	}

	// {2639}
	public void test0010() {
		check("Integrate[Sqrt[I*Sinh[c + d*x]], x]", "((-2*I)*EllipticE[(I*c - Pi/2 + I*d*x)/2, 2])/d", //
				2639);
	}

	// {2641}
	public void test0011() {
		check("Integrate[1/Sqrt[I*Sinh[c + d*x]], x]", "((-2*I)*EllipticF[(I*c - Pi/2 + I*d*x)/2, 2])/d", //
				2641);
	}

	// {2643}
	public void test0012() {
		check("Integrate[(b*Sinh[c + d*x])^(4/3), x]", "(3*Cosh[c + d*x]*Hypergeometric2F1[1/2, 7/6, 13/6, -Sinh[c + d*x]^2]*(b*Sinh[c + d*x])^(7/3))/(7*b*d*Sqrt[Cosh[c + d*x]^2])", //
				2643);
	}

	// {2643}
	public void test0013() {
		check("Integrate[(b*Sinh[c + d*x])^(2/3), x]", "(3*Cosh[c + d*x]*Hypergeometric2F1[1/2, 5/6, 11/6, -Sinh[c + d*x]^2]*(b*Sinh[c + d*x])^(5/3))/(5*b*d*Sqrt[Cosh[c + d*x]^2])", //
				2643);
	}

	// {2643}
	public void test0014() {
		check("Integrate[(b*Sinh[c + d*x])^(1/3), x]", "(3*Cosh[c + d*x]*Hypergeometric2F1[1/2, 2/3, 5/3, -Sinh[c + d*x]^2]*(b*Sinh[c + d*x])^(4/3))/(4*b*d*Sqrt[Cosh[c + d*x]^2])", //
				2643);
	}

	// {2643}
	public void test0015() {
		check("Integrate[(b*Sinh[c + d*x])^(-1/3), x]", "(3*Cosh[c + d*x]*Hypergeometric2F1[1/3, 1/2, 4/3, -Sinh[c + d*x]^2]*(b*Sinh[c + d*x])^(2/3))/(2*b*d*Sqrt[Cosh[c + d*x]^2])", //
				2643);
	}

	// {2643}
	public void test0016() {
		check("Integrate[(b*Sinh[c + d*x])^(-2/3), x]", "(3*Cosh[c + d*x]*Hypergeometric2F1[1/6, 1/2, 7/6, -Sinh[c + d*x]^2]*(b*Sinh[c + d*x])^(1/3))/(b*d*Sqrt[Cosh[c + d*x]^2])", //
				2643);
	}

	// {2643}
	public void test0017() {
		check("Integrate[(b*Sinh[c + d*x])^(-4/3), x]", "(-3*Cosh[c + d*x]*Hypergeometric2F1[-1/6, 1/2, 5/6, -Sinh[c + d*x]^2])/(b*d*Sqrt[Cosh[c + d*x]^2]*(b*Sinh[c + d*x])^(1/3))", //
				2643);
	}

	// {2643}
	public void test0018() {
		check("Integrate[(b*Sinh[c + d*x])^n, x]", "(Cosh[c + d*x]*Hypergeometric2F1[1/2, (1 + n)/2, (3 + n)/2, -Sinh[c + d*x]^2]*(b*Sinh[c + d*x])^(1 + n))/(b*d*(1 + n)*Sqrt[Cosh[c + d*x]^2])", //
				2643);
	}

	// {2643}
	public void test0019() {
		check("Integrate[(I*Sinh[c + d*x])^n, x]", "((-I)*Cosh[c + d*x]*Hypergeometric2F1[1/2, (1 + n)/2, (3 + n)/2, -Sinh[c + d*x]^2]*(I*Sinh[c + d*x])^(1 + n))/(d*(1 + n)*Sqrt[Cosh[c + d*x]^2])", //
				2643);
	}

	// {2643}
	public void test0020() {
		check("Integrate[((-I)*Sinh[c + d*x])^n, x]", "(I*Cosh[c + d*x]*Hypergeometric2F1[1/2, (1 + n)/2, (3 + n)/2, -Sinh[c + d*x]^2]*((-I)*Sinh[c + d*x])^(1 + n))/(d*(1 + n)*Sqrt[Cosh[c + d*x]^2])", //
				2643);
	}

	// {2648}
	public void test0021() {
		check("Integrate[(1 + I*Sinh[c + d*x])^(-1), x]", "(I*Cosh[c + d*x])/(d*(1 + I*Sinh[c + d*x]))", //
				2648);
	}

	// {2648}
	public void test0022() {
		check("Integrate[(1 - I*Sinh[c + d*x])^(-1), x]", "((-I)*Cosh[c + d*x])/(d*(1 - I*Sinh[c + d*x]))", //
				2648);
	}

	// {2646}
	public void test0023() {
		check("Integrate[Sqrt[a + I*a*Sinh[c + d*x]], x]", "((2*I)*a*Cosh[c + d*x])/(d*Sqrt[a + I*a*Sinh[c + d*x]])", //
				2646);
	}

	// {2657}
	public void test0024() {
		check("Integrate[(5 + (3*I)*Sinh[c + d*x])^(-1), x]", "x/4 - ((I/2)*ArcTan[Cosh[c + d*x]/(3 + I*Sinh[c + d*x])])/d", //
				2657);
	}

	// {2644}
	public void test0025() {
		check("Integrate[(a + b*Sinh[c + d*x])^2, x]", "((2*a^2 - b^2)*x)/2 + (2*a*b*Cosh[c + d*x])/d + (b^2*Cosh[c + d*x]*Sinh[c + d*x])/(2*d)", //
				2644);
	}

	// {2671}
	public void test0026() {
		check("Integrate[Cosh[x]^2/(1 + I*Sinh[x])^3, x]", "((I/3)*Cosh[x]^3)/(1 + I*Sinh[x])^3", //
				2671);
	}

	// {2671}
	public void test0027() {
		check("Integrate[Cosh[x]^2/(1 - I*Sinh[x])^3, x]", "((-I/3)*Cosh[x]^3)/(1 - I*Sinh[x])^3", //
				2671);
	}

	// {5517}
	public void test0030() {
		check("Integrate[Sinh[a + b*Log[c*x^n]], x]", "-((b*n*x*Cosh[a + b*Log[c*x^n]])/(1 - b^2*n^2)) + (x*Sinh[a + b*Log[c*x^n]])/(1 - b^2*n^2)", //
				5517);
	}

	// {5527}
	public void test0031() {
		check("Integrate[x^m*Sinh[a + b*Log[c*x^n]], x]", "-((b*n*x^(1 + m)*Cosh[a + b*Log[c*x^n]])/((1 + m)^2 - b^2*n^2)) + ((1 + m)*x^(1 + m)*Sinh[a + b*Log[c*x^n]])/((1 + m)^2 - b^2*n^2)", //
				5527);
	}

	// {5474}
	public void test0032() {
		check("Integrate[F^(c*(a + b*x))*Sinh[d + e*x], x]", "(e*F^(c*(a + b*x))*Cosh[d + e*x])/(e^2 - b^2*c^2*Log[F]^2) - (b*c*F^(c*(a + b*x))*Log[F]*Sinh[d + e*x])/(e^2 - b^2*c^2*Log[F]^2)", //
				5474);
	}

	// {5493}
	public void test0033() {
		check("Integrate[F^(c*(a + b*x))*Csch[d + e*x], x]", "(-2*E^(d + e*x)*F^(c*(a + b*x))*Hypergeometric2F1[1, (e + b*c*Log[F])/(2*e), (3 + (b*c*Log[F])/e)/2, E^(2*(d + e*x))])/(e + b*c*Log[F])", //
				5493);
	}

	// {5493}
	public void test0034() {
		check("Integrate[F^(c*(a + b*x))*Csch[d + e*x]^2, x]", "(4*E^(2*(d + e*x))*F^(c*(a + b*x))*Hypergeometric2F1[2, 1 + (b*c*Log[F])/(2*e), 2 + (b*c*Log[F])/(2*e), E^(2*(d + e*x))])/(2*e + b*c*Log[F])", //
				5493);
	}

	// {5474}
	public void test0035() {
		check("Integrate[E^x*Sinh[a + b*x], x]", "-((b*E^x*Cosh[a + b*x])/(1 - b^2)) + (E^x*Sinh[a + b*x])/(1 - b^2)", //
				5474);
	}

	// {3179}
	public void test0036() {
		check("Integrate[(a + b*Sinh[c + d*x]^2)^2, x]", "((8*a^2 - 8*a*b + 3*b^2)*x)/8 + ((8*a - 3*b)*b*Cosh[c + d*x]*Sinh[c + d*x])/(8*d) + (b^2*Cosh[c + d*x]*Sinh[c + d*x]^3)/(4*d)", //
				3179);
	}

	// {3177}
	public void test0037() {
		check("Integrate[Sqrt[1 - Sinh[x]^2], x]", "(-I)*EllipticE[I*x, -1]", //
				3177);
	}

	// {3182}
	public void test0038() {
		check("Integrate[1/Sqrt[1 - Sinh[x]^2], x]", "(-I)*EllipticF[I*x, -1]", //
				3182);
	}

	// {2637}
	public void test0043() {
		check("Integrate[Cosh[a + b*x], x]", "Sinh[a + b*x]/b", //
				2637);
	}

	// {2639}
	public void test0044() {
		check("Integrate[Sqrt[Cosh[a + b*x]], x]", "((-2*I)*EllipticE[(I/2)*(a + b*x), 2])/b", //
				2639);
	}

	// {2641}
	public void test0045() {
		check("Integrate[1/Sqrt[Cosh[a + b*x]], x]", "((-2*I)*EllipticF[(I/2)*(a + b*x), 2])/b", //
				2641);
	}

	// {2643}
	public void test0046() {
		check("Integrate[(b*Cosh[c + d*x])^n, x]", "-(((b*Cosh[c + d*x])^(1 + n)*Hypergeometric2F1[1/2, (1 + n)/2, (3 + n)/2, Cosh[c + d*x]^2]*Sinh[c + d*x])/(b*d*(1 + n)*Sqrt[-Sinh[c + d*x]^2]))", //
				2643);
	}

	// {2648}
	public void test0047() {
		check("Integrate[(1 + Cosh[c + d*x])^(-1), x]", "Sinh[c + d*x]/(d*(1 + Cosh[c + d*x]))", //
				2648);
	}

	// {2648}
	public void test0048() {
		check("Integrate[(1 - Cosh[c + d*x])^(-1), x]", "-(Sinh[c + d*x]/(d*(1 - Cosh[c + d*x])))", //
				2648);
	}

	// {2646}
	public void test0049() {
		check("Integrate[Sqrt[a + a*Cosh[c + d*x]], x]", "(2*a*Sinh[c + d*x])/(d*Sqrt[a + a*Cosh[c + d*x]])", //
				2646);
	}

	// {2646}
	public void test0050() {
		check("Integrate[Sqrt[a - a*Cosh[c + d*x]], x]", "(-2*a*Sinh[c + d*x])/(d*Sqrt[a - a*Cosh[c + d*x]])", //
				2646);
	}

	// {2644}
	public void test0051() {
		check("Integrate[(a + b*Cosh[c + d*x])^2, x]", "((2*a^2 + b^2)*x)/2 + (2*a*b*Sinh[c + d*x])/d + (b^2*Cosh[c + d*x]*Sinh[c + d*x])/(2*d)", //
				2644);
	}

	// {2657}
	public void test0052() {
		check("Integrate[(5 + 3*Cosh[c + d*x])^(-1), x]", "x/4 - ArcTanh[Sinh[c + d*x]/(3 + Cosh[c + d*x])]/(2*d)", //
				2657);
	}

	// {2671}
	public void test0053() {
		check("Integrate[Sinh[x]^2/(1 + Cosh[x])^3, x]", "Sinh[x]^3/(3*(1 + Cosh[x])^3)", //
				2671);
	}

	// {2671}
	public void test0054() {
		check("Integrate[Sinh[x]^2/(1 - Cosh[x])^3, x]", "-Sinh[x]^3/(3*(1 - Cosh[x])^3)", //
				2671);
	}

	// {5518}
	public void test0057() {
		check("Integrate[Cosh[a + b*Log[c*x^n]], x]", "(x*Cosh[a + b*Log[c*x^n]])/(1 - b^2*n^2) - (b*n*x*Sinh[a + b*Log[c*x^n]])/(1 - b^2*n^2)", //
				5518);
	}

	// {5528}
	public void test0058() {
		check("Integrate[x^m*Cosh[a + b*Log[c*x^n]], x]", "((1 + m)*x^(1 + m)*Cosh[a + b*Log[c*x^n]])/((1 + m)^2 - b^2*n^2) - (b*n*x^(1 + m)*Sinh[a + b*Log[c*x^n]])/((1 + m)^2 - b^2*n^2)", //
				5528);
	}

	// {5475}
	public void test0059() {
		check("Integrate[F^(c*(a + b*x))*Cosh[d + e*x], x]", "-((b*c*F^(c*(a + b*x))*Cosh[d + e*x]*Log[F])/(e^2 - b^2*c^2*Log[F]^2)) + (e*F^(c*(a + b*x))*Sinh[d + e*x])/(e^2 - b^2*c^2*Log[F]^2)", //
				5475);
	}

	// {5492}
	public void test0060() {
		check("Integrate[F^(c*(a + b*x))*Sech[d + e*x], x]", "(2*E^(d + e*x)*F^(c*(a + b*x))*Hypergeometric2F1[1, (e + b*c*Log[F])/(2*e), (3 + (b*c*Log[F])/e)/2, -E^(2*(d + e*x))])/(e + b*c*Log[F])", //
				5492);
	}

	// {5492}
	public void test0061() {
		check("Integrate[F^(c*(a + b*x))*Sech[d + e*x]^2, x]", "(4*E^(2*(d + e*x))*F^(c*(a + b*x))*Hypergeometric2F1[2, 1 + (b*c*Log[F])/(2*e), 2 + (b*c*Log[F])/(2*e), -E^(2*(d + e*x))])/(2*e + b*c*Log[F])", //
				5492);
	}

	// {5475}
	public void test0062() {
		check("Integrate[E^x*Cosh[a + b*x], x]", "(E^x*Cosh[a + b*x])/(1 - b^2) - (b*E^x*Sinh[a + b*x])/(1 - b^2)", //
				5475);
	}

	// {3177}
	public void test0063() {
		check("Integrate[Sqrt[1 + Cosh[x]^2], x]", "(-I)*EllipticE[Pi/2 + I*x, -1]", //
				3177);
	}

	// {3182}
	public void test0064() {
		check("Integrate[1/Sqrt[1 + Cosh[x]^2], x]", "(-I)*EllipticF[Pi/2 + I*x, -1]", //
				3182);
	}

	// {3475}
	public void test0065() {
		check("Integrate[Tanh[a + b*x], x]", "Log[Cosh[a + b*x]]/b", //
				3475);
	}

	// {3475}
	public void test0066() {
		check("Integrate[Coth[a + b*x], x]", "Log[Sinh[a + b*x]]/b", //
				3475);
	}

	// {3488}
	public void test0067() {
		check("Integrate[Sech[x]/(1 + Tanh[x]), x]", "-(Sech[x]/(1 + Tanh[x]))", //
				3488);
	}

	// {3488}
	public void test0068() {
		check("Integrate[Csch[x]/(1 + Coth[x]), x]", "-(Csch[x]/(1 + Coth[x]))", //
				3488);
	}

	// {3770}
	public void test0070() {
		check("Integrate[Sech[a + b*x], x]", "ArcTan[Sinh[a + b*x]]/b", //
				3770);
	}

	// {3794}
	public void test0071() {
		check("Integrate[Sech[x]/(a + a*Sech[x]), x]", "Tanh[x]/(a + a*Sech[x])", //
				3794);
	}

	// {3784}
	public void test0072() {
		check("Integrate[1/Sqrt[a + b*Sech[c + d*x]], x]", "(2*Sqrt[a + b]*Coth[c + d*x]*EllipticPi[(a + b)/a, ArcSin[Sqrt[a + b*Sech[c + d*x]]/Sqrt[a + b]], (a + b)/(a - b)]*Sqrt[(b*(1 - Sech[c + d*x]))/(a + b)]*Sqrt[-((b*(1 + Sech[c + d*x]))/(a - b))])/(a*d)", //
				3784);
	}

	// {3780}
	public void test0073() {
		check("Integrate[Sqrt[a + b*Sech[c + d*x]], x]", "(2*Coth[c + d*x]*EllipticPi[a/(a + b), ArcSin[Sqrt[a + b]/Sqrt[a + b*Sech[c + d*x]]], (a - b)/(a + b)]*Sqrt[-((b*(1 - Sech[c + d*x]))/(a + b*Sech[c + d*x]))]*Sqrt[(b*(1 + Sech[c + d*x]))/(a + b*Sech[c + d*x])]*(a + b*Sech[c + d*x]))/(Sqrt[a + b]*d)", //
				3780);
	}

	// {3784}
	public void test0074() {
		check("Integrate[1/Sqrt[a + b*Sech[c + d*x]], x]", "(2*Sqrt[a + b]*Coth[c + d*x]*EllipticPi[(a + b)/a, ArcSin[Sqrt[a + b*Sech[c + d*x]]/Sqrt[a + b]], (a + b)/(a - b)]*Sqrt[(b*(1 - Sech[c + d*x]))/(a + b)]*Sqrt[-((b*(1 + Sech[c + d*x]))/(a - b))])/(a*d)", //
				3784);
	}

	// {3770}
	public void test0077() {
		check("Integrate[Csch[a + b*x], x]", "-(ArcTanh[Cosh[a + b*x]]/b)", //
				3770);
	}

	// {3794}
	public void test0078() {
		check("Integrate[Csch[x]/(I + Csch[x]), x]", "(I*Coth[x])/(I + Csch[x])", //
				3794);
	}

	// {2563}
	public void test0079() {
		check("Integrate[Cosh[x]^(2/3)/Sinh[x]^(8/3), x]", "(-3*Cosh[x]^(5/3))/(5*Sinh[x]^(5/3))", //
				2563);
	}

	// {2563}
	public void test0080() {
		check("Integrate[Sinh[x]^(2/3)/Cosh[x]^(8/3), x]", "(3*Sinh[x]^(5/3))/(5*Cosh[x]^(5/3))", //
				2563);
	}

	// {4282}
	public void test0081() {
		check("Integrate[Sinh[x]*Sinh[3*x], x]", "-Sinh[2*x]/4 + Sinh[4*x]/8", //
				4282);
	}

	// {4282}
	public void test0082() {
		check("Integrate[Sinh[x]*Sinh[4*x], x]", "-Sinh[3*x]/6 + Sinh[5*x]/10", //
				4282);
	}

	// {4284}
	public void test0083() {
		check("Integrate[Cosh[2*x]*Sinh[x], x]", "-Cosh[x]/2 + Cosh[3*x]/6", //
				4284);
	}

	// {4284}
	public void test0084() {
		check("Integrate[Cosh[3*x]*Sinh[x], x]", "-Cosh[2*x]/4 + Cosh[4*x]/8", //
				4284);
	}

	// {4284}
	public void test0085() {
		check("Integrate[Cosh[4*x]*Sinh[x], x]", "-Cosh[3*x]/6 + Cosh[5*x]/10", //
				4284);
	}

	// {4284}
	public void test0086() {
		check("Integrate[Cosh[x]*Sinh[3*x], x]", "Cosh[2*x]/4 + Cosh[4*x]/8", //
				4284);
	}

	// {4284}
	public void test0087() {
		check("Integrate[Cosh[x]*Sinh[4*x], x]", "Cosh[3*x]/6 + Cosh[5*x]/10", //
				4284);
	}

	// {4283}
	public void test0088() {
		check("Integrate[Cosh[x]*Cosh[2*x], x]", "Sinh[x]/2 + Sinh[3*x]/6", //
				4283);
	}

	// {4283}
	public void test0089() {
		check("Integrate[Cosh[x]*Cosh[3*x], x]", "Sinh[2*x]/4 + Sinh[4*x]/8", //
				4283);
	}

	// {4283}
	public void test0090() {
		check("Integrate[Cosh[x]*Cosh[4*x], x]", "Sinh[3*x]/6 + Sinh[5*x]/10", //
				4283);
	}

	// {3475}
	public void test0091() {
		check("Integrate[Tanh[a + b*x], x]", "Log[Cosh[a + b*x]]/b", //
				3475);
	}

	// {3475}
	public void test0095() {
		check("Integrate[Coth[a + b*x], x]", "Log[Sinh[a + b*x]]/b", //
				3475);
	}

	// {3075}
	public void test0105() {
		check("Integrate[(a*Cosh[x] + b*Sinh[x])^(-2), x]", "Sinh[x]/(a*(a*Cosh[x] + b*Sinh[x]))", //
				3075);
	}

	// {3071}
	public void test0106() {
		check("Integrate[(a*Cosh[c + d*x] + a*Sinh[c + d*x])^2, x]", "(a*Cosh[c + d*x] + a*Sinh[c + d*x])^2/(2*d)", //
				3071);
	}

	// {3071}
	public void test0107() {
		check("Integrate[(a*Cosh[c + d*x] + a*Sinh[c + d*x])^3, x]", "(a*Cosh[c + d*x] + a*Sinh[c + d*x])^3/(3*d)", //
				3071);
	}

	// {3071}
	public void test0108() {
		check("Integrate[(a*Cosh[c + d*x] + a*Sinh[c + d*x])^n, x]", "(a*Cosh[c + d*x] + a*Sinh[c + d*x])^n/(d*n)", //
				3071);
	}

	// {3071}
	public void test0109() {
		check("Integrate[(a*Cosh[c + d*x] + a*Sinh[c + d*x])^(-1), x]", "-(1/(d*(a*Cosh[c + d*x] + a*Sinh[c + d*x])))", //
				3071);
	}

	// {3071}
	public void test0110() {
		check("Integrate[(a*Cosh[c + d*x] + a*Sinh[c + d*x])^(-2), x]", "-1/(2*d*(a*Cosh[c + d*x] + a*Sinh[c + d*x])^2)", //
				3071);
	}

	// {3071}
	public void test0111() {
		check("Integrate[(a*Cosh[c + d*x] + a*Sinh[c + d*x])^(-3), x]", "-1/(3*d*(a*Cosh[c + d*x] + a*Sinh[c + d*x])^3)", //
				3071);
	}

	// {3071}
	public void test0112() {
		check("Integrate[Sqrt[a*Cosh[c + d*x] + a*Sinh[c + d*x]], x]", "(2*Sqrt[a*Cosh[c + d*x] + a*Sinh[c + d*x]])/d", //
				3071);
	}

	// {3071}
	public void test0113() {
		check("Integrate[1/Sqrt[a*Cosh[c + d*x] + a*Sinh[c + d*x]], x]", "-2/(d*Sqrt[a*Cosh[c + d*x] + a*Sinh[c + d*x]])", //
				3071);
	}

	// {3071}
	public void test0114() {
		check("Integrate[(a*Cosh[c + d*x] - a*Sinh[c + d*x])^2, x]", "-(a*Cosh[c + d*x] - a*Sinh[c + d*x])^2/(2*d)", //
				3071);
	}

	// {3071}
	public void test0115() {
		check("Integrate[(a*Cosh[c + d*x] - a*Sinh[c + d*x])^3, x]", "-(a*Cosh[c + d*x] - a*Sinh[c + d*x])^3/(3*d)", //
				3071);
	}

	// {3071}
	public void test0116() {
		check("Integrate[(a*Cosh[c + d*x] - a*Sinh[c + d*x])^n, x]", "-((a*Cosh[c + d*x] - a*Sinh[c + d*x])^n/(d*n))", //
				3071);
	}

	// {3071}
	public void test0117() {
		check("Integrate[(a*Cosh[c + d*x] - a*Sinh[c + d*x])^(-1), x]", "1/(d*(a*Cosh[c + d*x] - a*Sinh[c + d*x]))", //
				3071);
	}

	// {3071}
	public void test0118() {
		check("Integrate[(a*Cosh[c + d*x] - a*Sinh[c + d*x])^(-2), x]", "1/(2*d*(a*Cosh[c + d*x] - a*Sinh[c + d*x])^2)", //
				3071);
	}

	// {3071}
	public void test0119() {
		check("Integrate[(a*Cosh[c + d*x] - a*Sinh[c + d*x])^(-3), x]", "1/(3*d*(a*Cosh[c + d*x] - a*Sinh[c + d*x])^3)", //
				3071);
	}

	// {3071}
	public void test0120() {
		check("Integrate[Sqrt[a*Cosh[c + d*x] - a*Sinh[c + d*x]], x]", "(-2*Sqrt[a*Cosh[c + d*x] - a*Sinh[c + d*x]])/d", //
				3071);
	}

	// {3071}
	public void test0121() {
		check("Integrate[1/Sqrt[a*Cosh[c + d*x] - a*Sinh[c + d*x]], x]", "2/(d*Sqrt[a*Cosh[c + d*x] - a*Sinh[c + d*x]])", //
				3071);
	}

	// {4385}
	public void test0122() {
		check("Integrate[(Cosh[x] + Sinh[x])/(Cosh[x] - Sinh[x]), x]", "(Cosh[x] + Sinh[x])^2/2", //
				4385);
	}

	// {4385}
	public void test0123() {
		check("Integrate[(Cosh[x] - Sinh[x])/(Cosh[x] + Sinh[x]), x]", "-1/(2*(Cosh[x] + Sinh[x])^2)", //
				4385);
	}

	// {3133}
	public void test0124() {
		check("Integrate[(Cosh[x] - I*Sinh[x])/(Cosh[x] + I*Sinh[x]), x]", "(-I)*Log[Cosh[x] + I*Sinh[x]]", //
				3133);
	}

	// {3133}
	public void test0125() {
		check("Integrate[(B*Cosh[x] + C*Sinh[x])/(b*Cosh[x] + c*Sinh[x]), x]", "((b*B - c*C)*x)/(b^2 - c^2) - ((B*c - b*C)*Log[b*Cosh[x] + c*Sinh[x]])/(b^2 - c^2)", //
				3133);
	}

	// {3114}
	public void test0126() {
		check("Integrate[(Sqrt[b^2 - c^2] + b*Cosh[x] + c*Sinh[x])^(-1), x]", "-((c + Sqrt[b^2 - c^2]*Sinh[x])/(c*(c*Cosh[x] + b*Sinh[x])))", //
				3114);
	}

	// {3112}
	public void test0127() {
		check("Integrate[Sqrt[Sqrt[b^2 - c^2] + b*Cosh[x] + c*Sinh[x]], x]", "(2*(c*Cosh[x] + b*Sinh[x]))/Sqrt[Sqrt[b^2 - c^2] + b*Cosh[x] + c*Sinh[x]]", //
				3112);
	}

	// {3112}
	public void test0128() {
		check("Integrate[Sqrt[-Sqrt[b^2 - c^2] + b*Cosh[x] + c*Sinh[x]], x]", "(2*(c*Cosh[x] + b*Sinh[x]))/Sqrt[-Sqrt[b^2 - c^2] + b*Cosh[x] + c*Sinh[x]]", //
				3112);
	}

	// {3131}
	public void test0129() {
		check("Integrate[Sinh[x]/(1 + Cosh[x] + Sinh[x]), x]", "x/2 + Cosh[x]/2 - Sinh[x]/2", //
				3131);
	}

	// {3150}
	public void test0130() {
		check("Integrate[(b^2 - c^2 + a*b*Cosh[x] + a*c*Sinh[x])/(a + b*Cosh[x] + c*Sinh[x])^2, x]", "(c*Cosh[x] + b*Sinh[x])/(a + b*Cosh[x] + c*Sinh[x])", //
				3150);
	}

	// {3131}
	public void test0131() {
		check("Integrate[(A + C*Sinh[x])/(a + b*Cosh[x] + b*Sinh[x]), x]", "((2*a*A + b*C)*x)/(2*a^2) + (C*Cosh[x])/(2*a) - (((2*A)/a - C/b + (b*C)/a^2)*Log[a + b*Cosh[x] + b*Sinh[x]])/2 - (C*Sinh[x])/(2*a)", //
				3131);
	}

	// {3132}
	public void test0132() {
		check("Integrate[(A + B*Cosh[x])/(a + b*Cosh[x] + b*Sinh[x]), x]", "((2*a*A - b*B)*x)/(2*a^2) - (B*Cosh[x])/(2*a) - ((2*a*A*b - a^2*B - b^2*B)*Log[a + b*Cosh[x] + b*Sinh[x]])/(2*a^2*b) + (B*Sinh[x])/(2*a)", //
				3132);
	}

	// {3130}
	public void test0133() {
		check("Integrate[(A + B*Cosh[x] + C*Sinh[x])/(a + b*Cosh[x] + b*Sinh[x]), x]", "((2*a*A - b*(B - C))*x)/(2*a^2) - ((2*a*A*b - b^2*(B - C) - a^2*(B + C))*Log[a + b*Cosh[x] + b*Sinh[x]])/(2*a^2*b) - ((B - C)*(Cosh[x] - Sinh[x]))/(2*a)", //
				3130);
	}

	// {3131}
	public void test0134() {
		check("Integrate[(A + C*Sinh[x])/(a + b*Cosh[x] - b*Sinh[x]), x]", "((2*a*A - b*C)*x)/(2*a^2) + (C*Cosh[x])/(2*a) + ((2*a*A*b + a^2*C - b^2*C)*Log[a + b*Cosh[x] - b*Sinh[x]])/(2*a^2*b) + (C*Sinh[x])/(2*a)", //
				3131);
	}

	// {3132}
	public void test0135() {
		check("Integrate[(A + B*Cosh[x])/(a + b*Cosh[x] - b*Sinh[x]), x]", "((2*a*A - b*B)*x)/(2*a^2) + (B*Cosh[x])/(2*a) + ((2*a*A*b - a^2*B - b^2*B)*Log[a + b*Cosh[x] - b*Sinh[x]])/(2*a^2*b) + (B*Sinh[x])/(2*a)", //
				3132);
	}

	// {3130}
	public void test0136() {
		check("Integrate[(A + B*Cosh[x] + C*Sinh[x])/(a + b*Cosh[x] - b*Sinh[x]), x]", "((2*a*A - b*(B + C))*x)/(2*a^2) + ((2*a*A*b - a^2*(B - C) - b^2*(B + C))*Log[a + b*Cosh[x] - b*Sinh[x]])/(2*a^2*b) + ((B + C)*(Cosh[x] + Sinh[x]))/(2*a)", //
				3130);
	}

	// {5474}
	public void test0138() {
		check("Integrate[E^(a + b*x)*Sinh[c + d*x], x]", "-((d*E^(a + b*x)*Cosh[c + d*x])/(b^2 - d^2)) + (b*E^(a + b*x)*Sinh[c + d*x])/(b^2 - d^2)", //
				5474);
	}

	// {5493}
	public void test0139() {
		check("Integrate[E^(a + b*x)*Csch[c + d*x], x]", "(-2*E^(a + c + b*x + d*x)*Hypergeometric2F1[1, (b + d)/(2*d), (3 + b/d)/2, E^(2*(c + d*x))])/(b + d)", //
				5493);
	}

	// {5493}
	public void test0140() {
		check("Integrate[E^(c + d*x)*Csch[a + b*x]^2, x]", "(4*E^(c + d*x + 2*(a + b*x))*Hypergeometric2F1[2, 1 + d/(2*b), 2 + d/(2*b), E^(2*(a + b*x))])/(2*b + d)", //
				5493);
	}

	// {5475}
	public void test0141() {
		check("Integrate[E^(a + b*x)*Cosh[c + d*x], x]", "(b*E^(a + b*x)*Cosh[c + d*x])/(b^2 - d^2) - (d*E^(a + b*x)*Sinh[c + d*x])/(b^2 - d^2)", //
				5475);
	}

	// {5492}
	public void test0142() {
		check("Integrate[E^(a + b*x)*Sech[c + d*x], x]", "(2*E^(a + c + b*x + d*x)*Hypergeometric2F1[1, (b + d)/(2*d), (3 + b/d)/2, -E^(2*(c + d*x))])/(b + d)", //
				5492);
	}

	// {5492}
	public void test0143() {
		check("Integrate[E^(a + b*x)*Sech[c + d*x]^2, x]", "(4*E^(a + b*x + 2*(c + d*x))*Hypergeometric2F1[2, 1 + b/(2*d), 2 + b/(2*d), -E^(2*(c + d*x))])/(b + 2*d)", //
				5492);
	}

	// {5475}
	public void test0144() {
		check("Integrate[E^(c + d*x)*Cosh[a + b*x], x]", "-((d*E^(c + d*x)*Cosh[a + b*x])/(b^2 - d^2)) + (b*E^(c + d*x)*Sinh[a + b*x])/(b^2 - d^2)", //
				5475);
	}

	// {6686}
	public void test0145() {
		check("Integrate[Csch[x]*Log[Tanh[x]]*Sech[x], x]", "Log[Tanh[x]]^2/2", //
				6686);
	}

	// {6686}
	public void test0146() {
		check("Integrate[Csch[2*x]*Log[Tanh[x]], x]", "Log[Tanh[x]]^2/4", //
				6686);
	}

	// {5370}
	public void test0151() {
		check("Integrate[(Cosh[Sqrt[x]]*Sinh[Sqrt[x]])/Sqrt[x], x]", "Sinh[Sqrt[x]]^2", //
				5370);
	}

	// {4385}
	public void test0152() {
		check("Integrate[(Cosh[a + b*x] - Sinh[a + b*x])/(Cosh[a + b*x] + Sinh[a + b*x]), x]", "-1/(2*b*(Cosh[a + b*x] + Sinh[a + b*x])^2)", //
				4385);
	}
}
