package org.matheclipse.core.rubi.step02;

public class InverseHyperbolicFunctions extends AbstractRubiTestCase {

	static boolean init = true;

	public InverseHyperbolicFunctions(String name) {
		super(name, false);
	}

	@Override
	protected void setUp() {
		try {
			super.setUp();
			if (init) {
				System.out.println("InverseHyperbolicFunctions");
				init = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// {5772, 267}
	public void test0001() {
		check(//
				"Integrate[ArcSinh[a*x], x]", //
				"-(Sqrt[1 + a^2*x^2]/a) + x*ArcSinh[a*x]", //
				5772, 267);
	}

	// {5776, 270}
	public void test0002() {
		check(//
				"Integrate[ArcSinh[a*x]/x^3, x]", //
				"-(a*Sqrt[1 + a^2*x^2])/(2*x) - ArcSinh[a*x]/(2*x^2)", //
				5776, 270);
	}

	// {5774, 3382}
	public void test0003() {
		check(//
				"Integrate[ArcSinh[a*x]^(-1), x]", //
				"CoshIntegral[ArcSinh[a*x]]/a", //
				5774, 3382);
	}

	// {5778, 3382}
	public void test0004() {
		check(//
				"Integrate[x/ArcSinh[a*x]^2, x]", //
				"-((x*Sqrt[1 + a^2*x^2])/(a*ArcSinh[a*x])) + CoshIntegral[2*ArcSinh[a*x]]/a^2", //
				5778, 3382);
	}

	// {5776, 5817}
	public void test0005() {
		check(//
				"Integrate[x^m*ArcSinh[a*x]^2, x]", //
				"(x^(1 + m)*ArcSinh[a*x]^2)/(1 + m) - (2*a*x^(2 + m)*ArcSinh[a*x]*Hypergeometric2F1[1/2, (2 + m)/2, (4 + m)/2, -(a^2*x^2)])/(2 + 3*m + m^2) + (2*a^2*x^(3 + m)*HypergeometricPFQ[{1, 3/2 + m/2, 3/2 + m/2}, {2 + m/2, 5/2 + m/2}, -(a^2*x^2)])/(6 + 11*m + 6*m^2 + m^3)", //
				5776, 5817);
	}

	// {5776, 371}
	public void test0006() {
		check(//
				"Integrate[x^m*ArcSinh[a*x], x]", //
				"(x^(1 + m)*ArcSinh[a*x])/(1 + m) - (a*x^(2 + m)*Hypergeometric2F1[1/2, (2 + m)/2, (4 + m)/2, -(a^2*x^2)])/(2 + 3*m + m^2)", //
				5776, 371);
	}

	// {5798, 197}
	public void test0007() {
		check(//
				"Integrate[(x*(a + b*ArcSinh[c*x]))/(d + c^2*d*x^2)^2, x]", //
				"(b*x)/(2*c*d^2*Sqrt[1 + c^2*x^2]) - (a + b*ArcSinh[c*x])/(2*c^2*d^2*(1 + c^2*x^2))", //
				5798, 197);
	}

	// {5798}
	public void test0008() {
		check(//
				"Integrate[x*Sqrt[d + c^2*d*x^2]*(a + b*ArcSinh[c*x]), x]", //
				"-(b*x*Sqrt[d + c^2*d*x^2])/(3*c*Sqrt[1 + c^2*x^2]) - (b*c*x^3*Sqrt[d + c^2*d*x^2])/(9*Sqrt[1 + c^2*x^2]) + ((d + c^2*d*x^2)^(3/2)*(a + b*ArcSinh[c*x]))/(3*c^2*d)", //
				5798);
	}

	// {5798, 8}
	public void test0009() {
		check(//
				"Integrate[(x*(a + b*ArcSinh[c*x]))/Sqrt[d + c^2*d*x^2], x]", //
				"-((b*x*Sqrt[1 + c^2*x^2])/(c*Sqrt[d + c^2*d*x^2])) + (Sqrt[d + c^2*d*x^2]*(a + b*ArcSinh[c*x]))/(c^2*d)", //
				5798, 8);
	}

	// {5783}
	public void test0010() {
		check(//
				"Integrate[(a + b*ArcSinh[c*x])/Sqrt[d + c^2*d*x^2], x]", //
				"(Sqrt[1 + c^2*x^2]*(a + b*ArcSinh[c*x])^2)/(2*b*c*Sqrt[d + c^2*d*x^2])", //
				5783);
	}

	// {5800, 29}
	public void test0011() {
		check(//
				"Integrate[(a + b*ArcSinh[c*x])/(x^2*Sqrt[d + c^2*d*x^2]), x]", //
				"-((Sqrt[d + c^2*d*x^2]*(a + b*ArcSinh[c*x]))/(d*x)) + (b*c*Sqrt[1 + c^2*x^2]*Log[x])/Sqrt[d + c^2*d*x^2]", //
				5800, 29);
	}

	// {5798, 209}
	public void test0012() {
		check(//
				"Integrate[(x*(a + b*ArcSinh[c*x]))/(d + c^2*d*x^2)^(3/2), x]", //
				"-((a + b*ArcSinh[c*x])/(c^2*d*Sqrt[d + c^2*d*x^2])) + (b*Sqrt[1 + c^2*x^2]*ArcTan[c*x])/(c^2*d*Sqrt[d + c^2*d*x^2])", //
				5798, 209);
	}

	// {5787, 266}
	public void test0013() {
		check(//
				"Integrate[(a + b*ArcSinh[c*x])/(d + c^2*d*x^2)^(3/2), x]", //
				"(x*(a + b*ArcSinh[c*x]))/(d*Sqrt[d + c^2*d*x^2]) - (b*Sqrt[1 + c^2*x^2]*Log[1 + c^2*x^2])/(2*c*d*Sqrt[d + c^2*d*x^2])", //
				5787, 266);
	}

	// {5798, 8}
	public void test0014() {
		check(//
				"Integrate[(x*ArcSinh[a*x])/Sqrt[1 + a^2*x^2], x]", //
				"-(x/a) + (Sqrt[1 + a^2*x^2]*ArcSinh[a*x])/a^2", //
				5798, 8);
	}

	// {5800, 29}
	public void test0015() {
		check(//
				"Integrate[ArcSinh[a*x]/(x^2*Sqrt[1 + a^2*x^2]), x]", //
				"-((Sqrt[1 + a^2*x^2]*ArcSinh[a*x])/x) + a*Log[x]", //
				5800, 29);
	}

	// {5817}
	public void test0017() {
		check(//
				"Integrate[(x^m*(a + b*ArcSinh[c*x]))/Sqrt[d + c^2*d*x^2], x]", //
				"(x^(1 + m)*Sqrt[1 + c^2*x^2]*(a + b*ArcSinh[c*x])*Hypergeometric2F1[1/2, (1 + m)/2, (3 + m)/2, -(c^2*x^2)])/((1 + m)*Sqrt[d + c^2*d*x^2]) - (b*c*x^(2 + m)*Sqrt[1 + c^2*x^2]*HypergeometricPFQ[{1, 1 + m/2, 1 + m/2}, {3/2 + m/2, 2 + m/2}, -(c^2*x^2)])/((2 + 3*m + m^2)*Sqrt[d + c^2*d*x^2])", //
				5817);
	}

	// {5783}
	public void test0018() {
		check(//
				"Integrate[(a + b*ArcSinh[c*x])^2/Sqrt[d + c^2*d*x^2], x]", //
				"(Sqrt[1 + c^2*x^2]*(a + b*ArcSinh[c*x])^3)/(3*b*c*Sqrt[d + c^2*d*x^2])", //
				5783);
	}

	// {5783}
	public void test0019() {
		check(//
				"Integrate[ArcSinh[a*x]^3/Sqrt[c + a^2*c*x^2], x]", //
				"(Sqrt[1 + a^2*x^2]*ArcSinh[a*x]^4)/(4*a*Sqrt[c + a^2*c*x^2])", //
				5783);
	}

	// {5819, 3379}
	public void test0020() {
		check(//
				"Integrate[x/(Sqrt[1 + a^2*x^2]*ArcSinh[a*x]), x]", //
				"SinhIntegral[ArcSinh[a*x]]/a^2", //
				5819, 3379);
	}

	// {5783}
	public void test0021() {
		check(//
				"Integrate[Sqrt[ArcSinh[a*x]]/Sqrt[c + a^2*c*x^2], x]", //
				"(2*Sqrt[1 + a^2*x^2]*ArcSinh[a*x]^(3/2))/(3*a*Sqrt[c + a^2*c*x^2])", //
				5783);
	}

	// {5783}
	public void test0023() {
		check(//
				"Integrate[ArcSinh[a*x]^(3/2)/Sqrt[c + a^2*c*x^2], x]", //
				"(2*Sqrt[1 + a^2*x^2]*ArcSinh[a*x]^(5/2))/(5*a*Sqrt[c + a^2*c*x^2])", //
				5783);
	}

	// {5783}
	public void test0024() {
		check(//
				"Integrate[ArcSinh[a*x]^(5/2)/Sqrt[c + a^2*c*x^2], x]", //
				"(2*Sqrt[1 + a^2*x^2]*ArcSinh[a*x]^(7/2))/(7*a*Sqrt[c + a^2*c*x^2])", //
				5783);
	}

	// {5783}
	public void test0025() {
		check(//
				"Integrate[Sqrt[ArcSinh[x/a]]/Sqrt[a^2 + x^2], x]", //
				"(2*a*Sqrt[1 + x^2/a^2]*ArcSinh[x/a]^(3/2))/(3*Sqrt[a^2 + x^2])", //
				5783);
	}

	// {5783}
	public void test0027() {
		check(//
				"Integrate[ArcSinh[x/a]^(3/2)/Sqrt[a^2 + x^2], x]", //
				"(2*a*Sqrt[1 + x^2/a^2]*ArcSinh[x/a]^(5/2))/(5*Sqrt[a^2 + x^2])", //
				5783);
	}

	// {5783}
	public void test0028() {
		check(//
				"Integrate[1/(Sqrt[c + a^2*c*x^2]*Sqrt[ArcSinh[a*x]]), x]", //
				"(2*Sqrt[1 + a^2*x^2]*Sqrt[ArcSinh[a*x]])/(a*Sqrt[c + a^2*c*x^2])", //
				5783);
	}

	// {5783}
	public void test0029() {
		check(//
				"Integrate[1/(Sqrt[c + a^2*c*x^2]*ArcSinh[a*x]^(3/2)), x]", //
				"(-2*Sqrt[1 + a^2*x^2])/(a*Sqrt[c + a^2*c*x^2]*Sqrt[ArcSinh[a*x]])", //
				5783);
	}

	// {5783}
	public void test0030() {
		check(//
				"Integrate[1/(Sqrt[c + a^2*c*x^2]*ArcSinh[a*x]^(5/2)), x]", //
				"(-2*Sqrt[1 + a^2*x^2])/(3*a*Sqrt[c + a^2*c*x^2]*ArcSinh[a*x]^(3/2))", //
				5783);
	}

	// {5796, 5783}
	public void test0031() {
		check(//
				"Integrate[(a + b*ArcSinh[c*x])/(Sqrt[d + I*c*d*x]*Sqrt[f - I*c*f*x]), x]", //
				"(Sqrt[1 + c^2*x^2]*(a + b*ArcSinh[c*x])^2)/(2*b*c*Sqrt[d + I*c*d*x]*Sqrt[f - I*c*f*x])", //
				5796, 5783);
	}

	// {5796, 5783}
	public void test0032() {
		check(//
				"Integrate[(a + b*ArcSinh[c*x])^2/(Sqrt[d + I*c*d*x]*Sqrt[f - I*c*f*x]), x]", //
				"(Sqrt[1 + c^2*x^2]*(a + b*ArcSinh[c*x])^3)/(3*b*c*Sqrt[d + I*c*d*x]*Sqrt[f - I*c*f*x])", //
				5796, 5783);
	}

	// {5783}
	public void test0033() {
		check(//
				"Integrate[(a + b*ArcSinh[c*x])/Sqrt[d + c^2*d*x^2], x]", //
				"(Sqrt[1 + c^2*x^2]*(a + b*ArcSinh[c*x])^2)/(2*b*c*Sqrt[d + c^2*d*x^2])", //
				5783);
	}

	// {5860, 5783}
	public void test0064() {
		check(//
				"Integrate[ArcSinh[a + b*x]^3/Sqrt[1 + a^2 + 2*a*b*x + b^2*x^2], x]", //
				"ArcSinh[a + b*x]^4/(4*b)", //
				5860, 5783);
	}

	// {5860, 5783}
	public void test0065() {
		check(//
				"Integrate[ArcSinh[a + b*x]^2/Sqrt[1 + a^2 + 2*a*b*x + b^2*x^2], x]", //
				"ArcSinh[a + b*x]^3/(3*b)", //
				5860, 5783);
	}

	// {5860, 5783}
	public void test0066() {
		check(//
				"Integrate[ArcSinh[a + b*x]/Sqrt[1 + a^2 + 2*a*b*x + b^2*x^2], x]", //
				"ArcSinh[a + b*x]^2/(2*b)", //
				5860, 5783);
	}

	// {5860, 5782}
	public void test0067() {
		check(//
				"Integrate[1/(Sqrt[1 + a^2 + 2*a*b*x + b^2*x^2]*ArcSinh[a + b*x]), x]", //
				"Log[ArcSinh[a + b*x]]/b", //
				5860, 5782);
	}

	// {5860, 5783}
	public void test0068() {
		check(//
				"Integrate[1/(Sqrt[1 + a^2 + 2*a*b*x + b^2*x^2]*ArcSinh[a + b*x]^2), x]", //
				"-(1/(b*ArcSinh[a + b*x]))", //
				5860, 5783);
	}

	// {5860, 5783}
	public void test0069() {
		check(//
				"Integrate[1/(Sqrt[1 + a^2 + 2*a*b*x + b^2*x^2]*ArcSinh[a + b*x]^3), x]", //
				"-1/(2*b*ArcSinh[a + b*x]^2)", //
				5860, 5783);
	}

	// {4898, 8}
	public void test0071() {
		check(//
				"Integrate[(a + I*b*ArcSin[1 - I*d*x^2])^2, x]", //
				"8*b^2*x - (4*b*Sqrt[(2*I)*d*x^2 + d^2*x^4]*(a + I*b*ArcSin[1 - I*d*x^2]))/(d*x) + x*(a + I*b*ArcSin[1 - I*d*x^2])^2", //
				4898, 8);
	}

	// {4912, 4900}
	public void test0072() {
		check(//
				"Integrate[(a + I*b*ArcSin[1 - I*d*x^2])^(-3), x]", //
				"-Sqrt[(2*I)*d*x^2 + d^2*x^4]/(4*b*d*x*(a + I*b*ArcSin[1 - I*d*x^2])^2) - x/(8*b^2*(a + I*b*ArcSin[1 - I*d*x^2])) + (x*CosIntegral[((-I/2)*(a + I*b*ArcSin[1 - I*d*x^2]))/b]*(I*Cosh[a/(2*b)] - Sinh[a/(2*b)]))/(16*b^3*(Cos[ArcSin[1 - I*d*x^2]/2] - Sin[ArcSin[1 - I*d*x^2]/2])) - (x*(I*Cosh[a/(2*b)] + Sinh[a/(2*b)])*SinIntegral[((I/2)*a)/b - ArcSin[1 - I*d*x^2]/2])/(16*b^3*(Cos[ArcSin[1 - I*d*x^2]/2] - Sin[ArcSin[1 - I*d*x^2]/2]))", //
				4912, 4900);
	}

	// {4898, 8}
	public void test0073() {
		check(//
				"Integrate[(a - I*b*ArcSin[1 + I*d*x^2])^2, x]", //
				"8*b^2*x - (4*b*Sqrt[(-2*I)*d*x^2 + d^2*x^4]*(a - I*b*ArcSin[1 + I*d*x^2]))/(d*x) + x*(a - I*b*ArcSin[1 + I*d*x^2])^2", //
				4898, 8);
	}

	// {4912, 4900}
	public void test0074() {
		check(//
				"Integrate[(a - I*b*ArcSin[1 + I*d*x^2])^(-3), x]", //
				"-Sqrt[(-2*I)*d*x^2 + d^2*x^4]/(4*b*d*x*(a - I*b*ArcSin[1 + I*d*x^2])^2) - x/(8*b^2*(a - I*b*ArcSin[1 + I*d*x^2])) - (x*CosIntegral[((I/2)*(a - I*b*ArcSin[1 + I*d*x^2]))/b]*(I*Cosh[a/(2*b)] + Sinh[a/(2*b)]))/(16*b^3*(Cos[ArcSin[1 + I*d*x^2]/2] - Sin[ArcSin[1 + I*d*x^2]/2])) + (x*(Cosh[a/(2*b)] + I*Sinh[a/(2*b)])*SinhIntegral[(a - I*b*ArcSin[1 + I*d*x^2])/(2*b)])/(16*b^3*(Cos[ArcSin[1 + I*d*x^2]/2] - Sin[ArcSin[1 + I*d*x^2]/2]))", //
				4912, 4900);
	}

	// {4898, 4895}
	public void test0075() {
		check(//
				"Integrate[(a + I*b*ArcSin[1 - I*d*x^2])^(5/2), x]", //
				"15*b^2*x*Sqrt[a + I*b*ArcSin[1 - I*d*x^2]] - (5*b*Sqrt[(2*I)*d*x^2 + d^2*x^4]*(a + I*b*ArcSin[1 - I*d*x^2])^(3/2))/(d*x) + x*(a + I*b*ArcSin[1 - I*d*x^2])^(5/2) + (15*b^2*Sqrt[Pi]*x*FresnelS[(Sqrt[(-I)/b]*Sqrt[a + I*b*ArcSin[1 - I*d*x^2]])/Sqrt[Pi]]*(Cosh[a/(2*b)] + I*Sinh[a/(2*b)]))/(Sqrt[(-I)/b]*(Cos[ArcSin[1 - I*d*x^2]/2] - Sin[ArcSin[1 - I*d*x^2]/2])) - (15*Sqrt[(-I)/b]*b^3*Sqrt[Pi]*x*FresnelC[(Sqrt[(-I)/b]*Sqrt[a + I*b*ArcSin[1 - I*d*x^2]])/Sqrt[Pi]]*(I*Cosh[a/(2*b)] + Sinh[a/(2*b)]))/(Cos[ArcSin[1 - I*d*x^2]/2] - Sin[ArcSin[1 - I*d*x^2]/2])", //
				4898, 4895);
	}

	// {4898, 4903}
	public void test0076() {
		check(//
				"Integrate[(a + I*b*ArcSin[1 - I*d*x^2])^(3/2), x]", //
				"(-3*b*Sqrt[(2*I)*d*x^2 + d^2*x^4]*Sqrt[a + I*b*ArcSin[1 - I*d*x^2]])/(d*x) + x*(a + I*b*ArcSin[1 - I*d*x^2])^(3/2) + (3*Sqrt[I*b]*b*Sqrt[Pi]*x*FresnelC[Sqrt[a + I*b*ArcSin[1 - I*d*x^2]]/(Sqrt[I*b]*Sqrt[Pi])]*(I*Cosh[a/(2*b)] - Sinh[a/(2*b)]))/(Cos[ArcSin[1 - I*d*x^2]/2] - Sin[ArcSin[1 - I*d*x^2]/2]) - (3*b^2*Sqrt[Pi]*x*FresnelS[Sqrt[a + I*b*ArcSin[1 - I*d*x^2]]/(Sqrt[I*b]*Sqrt[Pi])]*(Cosh[a/(2*b)] - I*Sinh[a/(2*b)]))/(Sqrt[I*b]*(Cos[ArcSin[1 - I*d*x^2]/2] - Sin[ArcSin[1 - I*d*x^2]/2]))", //
				4898, 4903);
	}

	// {4912, 4903}
	public void test0077() {
		check(//
				"Integrate[(a + I*b*ArcSin[1 - I*d*x^2])^(-5/2), x]", //
				"-Sqrt[(2*I)*d*x^2 + d^2*x^4]/(3*b*d*x*(a + I*b*ArcSin[1 - I*d*x^2])^(3/2)) - x/(3*b^2*Sqrt[a + I*b*ArcSin[1 - I*d*x^2]]) - (Sqrt[Pi]*x*FresnelS[Sqrt[a + I*b*ArcSin[1 - I*d*x^2]]/(Sqrt[I*b]*Sqrt[Pi])]*(Cosh[a/(2*b)] - I*Sinh[a/(2*b)]))/(3*Sqrt[I*b]*b^2*(Cos[ArcSin[1 - I*d*x^2]/2] - Sin[ArcSin[1 - I*d*x^2]/2])) - (Sqrt[Pi]*x*FresnelC[Sqrt[a + I*b*ArcSin[1 - I*d*x^2]]/(Sqrt[I*b]*Sqrt[Pi])]*(Cosh[a/(2*b)] + I*Sinh[a/(2*b)]))/(3*Sqrt[I*b]*b^2*(Cos[ArcSin[1 - I*d*x^2]/2] - Sin[ArcSin[1 - I*d*x^2]/2]))", //
				4912, 4903);
	}

	// {4912, 4906}
	public void test0078() {
		check(//
				"Integrate[(a + I*b*ArcSin[1 - I*d*x^2])^(-7/2), x]", //
				"-Sqrt[(2*I)*d*x^2 + d^2*x^4]/(5*b*d*x*(a + I*b*ArcSin[1 - I*d*x^2])^(5/2)) - x/(15*b^2*(a + I*b*ArcSin[1 - I*d*x^2])^(3/2)) - Sqrt[(2*I)*d*x^2 + d^2*x^4]/(15*b^3*d*x*Sqrt[a + I*b*ArcSin[1 - I*d*x^2]]) - (((-I)/b)^(3/2)*Sqrt[Pi]*x*FresnelC[(Sqrt[(-I)/b]*Sqrt[a + I*b*ArcSin[1 - I*d*x^2]])/Sqrt[Pi]]*(Cosh[a/(2*b)] - I*Sinh[a/(2*b)]))/(15*b^2*(Cos[ArcSin[1 - I*d*x^2]/2] - Sin[ArcSin[1 - I*d*x^2]/2])) + (((-I)/b)^(3/2)*Sqrt[Pi]*x*FresnelS[(Sqrt[(-I)/b]*Sqrt[a + I*b*ArcSin[1 - I*d*x^2]])/Sqrt[Pi]]*(Cosh[a/(2*b)] + I*Sinh[a/(2*b)]))/(15*b^2*(Cos[ArcSin[1 - I*d*x^2]/2] - Sin[ArcSin[1 - I*d*x^2]/2]))", //
				4912, 4906);
	}

	// {4898, 4895}
	public void test0079() {
		check(//
				"Integrate[(a - I*b*ArcSin[1 + I*d*x^2])^(5/2), x]", //
				"15*b^2*x*Sqrt[a - I*b*ArcSin[1 + I*d*x^2]] - (5*b*Sqrt[(-2*I)*d*x^2 + d^2*x^4]*(a - I*b*ArcSin[1 + I*d*x^2])^(3/2))/(d*x) + x*(a - I*b*ArcSin[1 + I*d*x^2])^(5/2) + (15*b^2*Sqrt[Pi]*x*FresnelS[(Sqrt[I/b]*Sqrt[a - I*b*ArcSin[1 + I*d*x^2]])/Sqrt[Pi]]*(Cosh[a/(2*b)] - I*Sinh[a/(2*b)]))/(Sqrt[I/b]*(Cos[ArcSin[1 + I*d*x^2]/2] - Sin[ArcSin[1 + I*d*x^2]/2])) - (15*b^2*Sqrt[Pi]*x*FresnelC[(Sqrt[I/b]*Sqrt[a - I*b*ArcSin[1 + I*d*x^2]])/Sqrt[Pi]]*(Cosh[a/(2*b)] + I*Sinh[a/(2*b)]))/(Sqrt[I/b]*(Cos[ArcSin[1 + I*d*x^2]/2] - Sin[ArcSin[1 + I*d*x^2]/2]))", //
				4898, 4895);
	}

	// {4898, 4903}
	public void test0080() {
		check(//
				"Integrate[(a - I*b*ArcSin[1 + I*d*x^2])^(3/2), x]", //
				"(-3*b*Sqrt[(-2*I)*d*x^2 + d^2*x^4]*Sqrt[a - I*b*ArcSin[1 + I*d*x^2]])/(d*x) + x*(a - I*b*ArcSin[1 + I*d*x^2])^(3/2) - (3*b^2*Sqrt[Pi]*x*FresnelS[Sqrt[a - I*b*ArcSin[1 + I*d*x^2]]/(Sqrt[(-I)*b]*Sqrt[Pi])]*(Cosh[a/(2*b)] + I*Sinh[a/(2*b)]))/(Sqrt[(-I)*b]*(Cos[ArcSin[1 + I*d*x^2]/2] - Sin[ArcSin[1 + I*d*x^2]/2])) - (3*Sqrt[(-I)*b]*b*Sqrt[Pi]*x*FresnelC[Sqrt[a - I*b*ArcSin[1 + I*d*x^2]]/(Sqrt[(-I)*b]*Sqrt[Pi])]*(I*Cosh[a/(2*b)] + Sinh[a/(2*b)]))/(Cos[ArcSin[1 + I*d*x^2]/2] - Sin[ArcSin[1 + I*d*x^2]/2])", //
				4898, 4903);
	}

	// {4912, 4903}
	public void test0081() {
		check(//
				"Integrate[(a - I*b*ArcSin[1 + I*d*x^2])^(-5/2), x]", //
				"-Sqrt[(-2*I)*d*x^2 + d^2*x^4]/(3*b*d*x*(a - I*b*ArcSin[1 + I*d*x^2])^(3/2)) - x/(3*b^2*Sqrt[a - I*b*ArcSin[1 + I*d*x^2]]) - (Sqrt[Pi]*x*FresnelS[Sqrt[a - I*b*ArcSin[1 + I*d*x^2]]/(Sqrt[(-I)*b]*Sqrt[Pi])]*(Cosh[a/(2*b)] + I*Sinh[a/(2*b)]))/(3*Sqrt[(-I)*b]*b^2*(Cos[ArcSin[1 + I*d*x^2]/2] - Sin[ArcSin[1 + I*d*x^2]/2])) - (Sqrt[(-I)*b]*Sqrt[Pi]*x*FresnelC[Sqrt[a - I*b*ArcSin[1 + I*d*x^2]]/(Sqrt[(-I)*b]*Sqrt[Pi])]*(I*Cosh[a/(2*b)] + Sinh[a/(2*b)]))/(3*b^3*(Cos[ArcSin[1 + I*d*x^2]/2] - Sin[ArcSin[1 + I*d*x^2]/2]))", //
				4912, 4903);
	}

	// {4912, 4906}
	public void test0082() {
		check(//
				"Integrate[(a - I*b*ArcSin[1 + I*d*x^2])^(-7/2), x]", //
				"-Sqrt[(-2*I)*d*x^2 + d^2*x^4]/(5*b*d*x*(a - I*b*ArcSin[1 + I*d*x^2])^(5/2)) - x/(15*b^2*(a - I*b*ArcSin[1 + I*d*x^2])^(3/2)) - Sqrt[(-2*I)*d*x^2 + d^2*x^4]/(15*b^3*d*x*Sqrt[a - I*b*ArcSin[1 + I*d*x^2]]) - ((I/b)^(3/2)*Sqrt[Pi]*x*FresnelC[(Sqrt[I/b]*Sqrt[a - I*b*ArcSin[1 + I*d*x^2]])/Sqrt[Pi]]*(Cosh[a/(2*b)] + I*Sinh[a/(2*b)]))/(15*b^2*(Cos[ArcSin[1 + I*d*x^2]/2] - Sin[ArcSin[1 + I*d*x^2]/2])) + (Sqrt[I/b]*Sqrt[Pi]*x*FresnelS[(Sqrt[I/b]*Sqrt[a - I*b*ArcSin[1 + I*d*x^2]])/Sqrt[Pi]]*(I*Cosh[a/(2*b)] + Sinh[a/(2*b)]))/(15*b^3*(Cos[ArcSin[1 + I*d*x^2]/2] - Sin[ArcSin[1 + I*d*x^2]/2]))", //
				4912, 4906);
	}

	// {5819, 3379}
	public void test0083() {
		check(//
				"Integrate[x/(Sqrt[1 + x^2]*ArcSinh[x]), x]", //
				"SinhIntegral[ArcSinh[x]]", //
				5819, 3379);
	}

	// {5871, 5783}
	public void test0084() {
		check(//
				"Integrate[ArcSinh[Sqrt[-1 + b*x^2]]^n/Sqrt[-1 + b*x^2], x]", //
				"(Sqrt[b*x^2]*ArcSinh[Sqrt[-1 + b*x^2]]^(1 + n))/(b*(1 + n)*x)", //
				5871, 5783);
	}

	// {5871, 5782}
	public void test0085() {
		check(//
				"Integrate[1/(Sqrt[-1 + b*x^2]*ArcSinh[Sqrt[-1 + b*x^2]]), x]", //
				"(Sqrt[b*x^2]*Log[ArcSinh[Sqrt[-1 + b*x^2]]])/(b*x)", //
				5871, 5782);
	}

	// {5879, 75}
	public void test0086() {
		check(//
				"Integrate[ArcCosh[a*x], x]", //
				"-((Sqrt[-1 + a*x]*Sqrt[1 + a*x])/a) + x*ArcCosh[a*x]", //
				5879, 75);
	}

	// {5883, 97}
	public void test0087() {
		check(//
				"Integrate[ArcCosh[a*x]/x^3, x]", //
				"(a*Sqrt[-1 + a*x]*Sqrt[1 + a*x])/(2*x) - ArcCosh[a*x]/(2*x^2)", //
				5883, 97);
	}

	// {5881, 3379}
	public void test0088() {
		check(//
				"Integrate[ArcCosh[a*x]^(-1), x]", //
				"SinhIntegral[ArcCosh[a*x]]/a", //
				5881, 3379);
	}

	// {5885, 3382}
	public void test0089() {
		check(//
				"Integrate[x/ArcCosh[a*x]^2, x]", //
				"-((x*Sqrt[-1 + a*x]*Sqrt[1 + a*x])/(a*ArcCosh[a*x])) + CoshIntegral[2*ArcCosh[a*x]]/a^2", //
				5885, 3382);
	}

	// {5883, 5949}
	public void test0090() {
		check(//
				"Integrate[x^m*ArcCosh[a*x]^2, x]", //
				"(x^(1 + m)*ArcCosh[a*x]^2)/(1 + m) - (2*a*x^(2 + m)*Sqrt[1 - a*x]*ArcCosh[a*x]*Hypergeometric2F1[1/2, (2 + m)/2, (4 + m)/2, a^2*x^2])/((2 + 3*m + m^2)*Sqrt[-1 + a*x]) - (2*a^2*x^(3 + m)*HypergeometricPFQ[{1, 3/2 + m/2, 3/2 + m/2}, {2 + m/2, 5/2 + m/2}, a^2*x^2])/(6 + 11*m + 6*m^2 + m^3)", //
				5883, 5949);
	}

	// {5883, 97}
	public void test0091() {
		check(//
				"Integrate[(a + b*ArcCosh[c*x])/x^3, x]", //
				"(b*c*Sqrt[-1 + c*x]*Sqrt[1 + c*x])/(2*x) - (a + b*ArcCosh[c*x])/(2*x^2)", //
				5883, 97);
	}

	// {5883, 5949}
	public void test0092() {
		check(//
				"Integrate[Sqrt[f*x]*(a + b*ArcCosh[c*x])^2, x]", //
				"(2*(f*x)^(3/2)*(a + b*ArcCosh[c*x])^2)/(3*f) - (8*b*c*(f*x)^(5/2)*Sqrt[1 - c*x]*(a + b*ArcCosh[c*x])*Hypergeometric2F1[1/2, 5/4, 9/4, c^2*x^2])/(15*f^2*Sqrt[-1 + c*x]) - (16*b^2*c^2*(f*x)^(7/2)*HypergeometricPFQ[{1, 7/4, 7/4}, {9/4, 11/4}, c^2*x^2])/(105*f^3)", //
				5883, 5949);
	}

	// {5883, 5949}
	public void test0093() {
		check(//
				"Integrate[(d*x)^m*(a + b*ArcCosh[c*x])^2, x]", //
				"((d*x)^(1 + m)*(a + b*ArcCosh[c*x])^2)/(d*(1 + m)) - (2*b*c*(d*x)^(2 + m)*Sqrt[1 - c*x]*(a + b*ArcCosh[c*x])*Hypergeometric2F1[1/2, (2 + m)/2, (4 + m)/2, c^2*x^2])/(d^2*(1 + m)*(2 + m)*Sqrt[-1 + c*x]) - (2*b^2*c^2*(d*x)^(3 + m)*HypergeometricPFQ[{1, 3/2 + m/2, 3/2 + m/2}, {2 + m/2, 5/2 + m/2}, c^2*x^2])/(d^3*(1 + m)*(2 + m)*(3 + m))", //
				5883, 5949);
	}

	// {5914, 39}
	public void test0094() {
		check(//
				"Integrate[(x*(a + b*ArcCosh[c*x]))/(d - c^2*d*x^2)^2, x]", //
				"-(b*x)/(2*c*d^2*Sqrt[-1 + c*x]*Sqrt[1 + c*x]) + (a + b*ArcCosh[c*x])/(2*c^2*d^2*(1 - c^2*x^2))", //
				5914, 39);
	}

	// {5892}
	public void test0095() {
		check(//
				"Integrate[(a + b*ArcCosh[c*x])/Sqrt[d - c^2*d*x^2], x]", //
				"(Sqrt[-1 + c*x]*Sqrt[1 + c*x]*(a + b*ArcCosh[c*x])^2)/(2*b*c*Sqrt[d - c^2*d*x^2])", //
				5892);
	}

	// {5892}
	public void test0096() {
		check(//
				"Integrate[ArcCosh[a*x]/Sqrt[1 - a^2*x^2], x]", //
				"(Sqrt[-1 + a*x]*ArcCosh[a*x]^2)/(2*a*Sqrt[1 - a*x])", //
				5892);
	}

	// {5948}
	public void test0097() {
		check(//
				"Integrate[((f*x)^(3/2)*(a + b*ArcCosh[c*x]))/Sqrt[1 - c^2*x^2], x]", //
				"(2*(f*x)^(5/2)*(a + b*ArcCosh[c*x])*Hypergeometric2F1[1/2, 5/4, 9/4, c^2*x^2])/(5*f) + (4*b*c*(f*x)^(7/2)*Sqrt[-1 + c*x]*HypergeometricPFQ[{1, 7/4, 7/4}, {9/4, 11/4}, c^2*x^2])/(35*f^2*Sqrt[1 - c*x])", //
				5948);
	}

	// {5948}
	public void test0098() {
		check(//
				"Integrate[((f*x)^(3/2)*(a + b*ArcCosh[c*x]))/Sqrt[d - c^2*d*x^2], x]", //
				"(2*(f*x)^(5/2)*Sqrt[1 - c^2*x^2]*(a + b*ArcCosh[c*x])*Hypergeometric2F1[1/2, 5/4, 9/4, c^2*x^2])/(5*f*Sqrt[d - c^2*d*x^2]) + (4*b*c*(f*x)^(7/2)*Sqrt[-1 + c*x]*Sqrt[1 + c*x]*HypergeometricPFQ[{1, 7/4, 7/4}, {9/4, 11/4}, c^2*x^2])/(35*f^2*Sqrt[d - c^2*d*x^2])", //
				5948);
	}

	// {5948}
	public void test0099() {
		check(//
				"Integrate[(x^m*(a + b*ArcCosh[c*x]))/Sqrt[d - c^2*d*x^2], x]", //
				"(x^(1 + m)*Sqrt[1 - c^2*x^2]*(a + b*ArcCosh[c*x])*Hypergeometric2F1[1/2, (1 + m)/2, (3 + m)/2, c^2*x^2])/((1 + m)*Sqrt[d - c^2*d*x^2]) + (b*c*x^(2 + m)*Sqrt[-1 + c*x]*Sqrt[1 + c*x]*HypergeometricPFQ[{1, 1 + m/2, 1 + m/2}, {3/2 + m/2, 2 + m/2}, c^2*x^2])/((2 + 3*m + m^2)*Sqrt[d - c^2*d*x^2])", //
				5948);
	}

	// {5948}
	public void test0100() {
		check(//
				"Integrate[(x^m*ArcCosh[a*x])/Sqrt[1 - a^2*x^2], x]", //
				"(x^(1 + m)*ArcCosh[a*x]*Hypergeometric2F1[1/2, (1 + m)/2, (3 + m)/2, a^2*x^2])/(1 + m) + (a*x^(2 + m)*Sqrt[-1 + a*x]*HypergeometricPFQ[{1, 1 + m/2, 1 + m/2}, {3/2 + m/2, 2 + m/2}, a^2*x^2])/((2 + 3*m + m^2)*Sqrt[1 - a*x])", //
				5948);
	}

	// {5892}
	public void test0101() {
		check(//
				"Integrate[(a + b*ArcCosh[c*x])^2/Sqrt[d - c^2*d*x^2], x]", //
				"(Sqrt[-1 + c*x]*Sqrt[1 + c*x]*(a + b*ArcCosh[c*x])^3)/(3*b*c*Sqrt[d - c^2*d*x^2])", //
				5892);
	}

	// {5892}
	public void test0102() {
		check(//
				"Integrate[ArcCosh[a*x]^2/Sqrt[1 - a^2*x^2], x]", //
				"(Sqrt[-1 + a*x]*ArcCosh[a*x]^3)/(3*a*Sqrt[1 - a*x])", //
				5892);
	}

	// {5892}
	public void test0103() {
		check(//
				"Integrate[ArcCosh[a*x]^2/Sqrt[c - a^2*c*x^2], x]", //
				"(Sqrt[-1 + a*x]*Sqrt[1 + a*x]*ArcCosh[a*x]^3)/(3*a*Sqrt[c - a^2*c*x^2])", //
				5892);
	}

	// {5892}
	public void test0104() {
		check(//
				"Integrate[ArcCosh[a*x]^3/Sqrt[c - a^2*c*x^2], x]", //
				"(Sqrt[-1 + a*x]*Sqrt[1 + a*x]*ArcCosh[a*x]^4)/(4*a*Sqrt[c - a^2*c*x^2])", //
				5892);
	}

	// {5892}
	public void test0105() {
		check(//
				"Integrate[ArcCosh[a*x]^3/Sqrt[1 - a^2*x^2], x]", //
				"(Sqrt[-1 + a*x]*ArcCosh[a*x]^4)/(4*a*Sqrt[1 - a*x])", //
				5892);
	}

	// {5890}
	public void test0106() {
		check(//
				"Integrate[1/(Sqrt[1 - a^2*x^2]*ArcCosh[a*x]), x]", //
				"(Sqrt[-1 + a*x]*Log[ArcCosh[a*x]])/(a*Sqrt[1 - a*x])", //
				5890);
	}

	// {5890}
	public void test0107() {
		check(//
				"Integrate[1/(Sqrt[1 - c^2*x^2]*(a + b*ArcCosh[c*x])), x]", //
				"(Sqrt[-1 + c*x]*Log[a + b*ArcCosh[c*x]])/(b*c*Sqrt[1 - c*x])", //
				5890);
	}

	// {5892}
	public void test0112() {
		check(//
				"Integrate[1/(Sqrt[1 - c^2*x^2]*(a + b*ArcCosh[c*x])^2), x]", //
				"-(Sqrt[-1 + c*x]/(b*c*Sqrt[1 - c*x]*(a + b*ArcCosh[c*x])))", //
				5892);
	}

	// {5892}
	public void test0119() {
		check(//
				"Integrate[1/(Sqrt[1 - a^2*x^2]*ArcCosh[a*x]^3), x]", //
				"-Sqrt[-1 + a*x]/(2*a*Sqrt[1 - a*x]*ArcCosh[a*x]^2)", //
				5892);
	}

	// {5892}
	public void test0120() {
		check(//
				"Integrate[ArcCosh[a*x]^n/Sqrt[1 - a^2*x^2], x]", //
				"(Sqrt[-1 + a*x]*ArcCosh[a*x]^(1 + n))/(a*(1 + n)*Sqrt[1 - a*x])", //
				5892);
	}

	// {5892}
	public void test0121() {
		check(//
				"Integrate[Sqrt[ArcCosh[a*x]]/Sqrt[c - a^2*c*x^2], x]", //
				"(2*Sqrt[-1 + a*x]*Sqrt[1 + a*x]*ArcCosh[a*x]^(3/2))/(3*a*Sqrt[c - a^2*c*x^2])", //
				5892);
	}

	// {5892}
	public void test0123() {
		check(//
				"Integrate[ArcCosh[a*x]^(3/2)/Sqrt[c - a^2*c*x^2], x]", //
				"(2*Sqrt[-1 + a*x]*Sqrt[1 + a*x]*ArcCosh[a*x]^(5/2))/(5*a*Sqrt[c - a^2*c*x^2])", //
				5892);
	}

	// {5892}
	public void test0125() {
		check(//
				"Integrate[ArcCosh[a*x]^(5/2)/Sqrt[c - a^2*c*x^2], x]", //
				"(2*Sqrt[-1 + a*x]*Sqrt[1 + a*x]*ArcCosh[a*x]^(7/2))/(7*a*Sqrt[c - a^2*c*x^2])", //
				5892);
	}

	// {5892}
	public void test0127() {
		check(//
				"Integrate[Sqrt[ArcCosh[x/a]]/Sqrt[a^2 - x^2], x]", //
				"(2*a*Sqrt[-1 + x/a]*Sqrt[1 + x/a]*ArcCosh[x/a]^(3/2))/(3*Sqrt[a^2 - x^2])", //
				5892);
	}

	// {5892}
	public void test0129() {
		check(//
				"Integrate[ArcCosh[x/a]^(3/2)/Sqrt[a^2 - x^2], x]", //
				"(2*a*Sqrt[-1 + x/a]*Sqrt[1 + x/a]*ArcCosh[x/a]^(5/2))/(5*Sqrt[a^2 - x^2])", //
				5892);
	}

	// {5892}
	public void test0131() {
		check(//
				"Integrate[1/(Sqrt[c - a^2*c*x^2]*Sqrt[ArcCosh[a*x]]), x]", //
				"(2*Sqrt[-1 + a*x]*Sqrt[1 + a*x]*Sqrt[ArcCosh[a*x]])/(a*Sqrt[c - a^2*c*x^2])", //
				5892);
	}

	// {5892}
	public void test0132() {
		check(//
				"Integrate[1/(Sqrt[c - a^2*c*x^2]*ArcCosh[a*x]^(3/2)), x]", //
				"(-2*Sqrt[-1 + a*x]*Sqrt[1 + a*x])/(a*Sqrt[c - a^2*c*x^2]*Sqrt[ArcCosh[a*x]])", //
				5892);
	}

	// {5892}
	public void test0135() {
		check(//
				"Integrate[1/(Sqrt[c - a^2*c*x^2]*ArcCosh[a*x]^(5/2)), x]", //
				"(-2*Sqrt[-1 + a*x]*Sqrt[1 + a*x])/(3*a*Sqrt[c - a^2*c*x^2]*ArcCosh[a*x]^(3/2))", //
				5892);
	}

	// {6001, 8}
	public void test0164() {
		check(//
				"Integrate[(a + b*ArcCosh[1 + d*x^2])^2, x]", //
				"8*b^2*x - (4*b*(2*x^2 + d*x^4)*(a + b*ArcCosh[1 + d*x^2]))/(x*Sqrt[d*x^2]*Sqrt[2 + d*x^2]) + x*(a + b*ArcCosh[1 + d*x^2])^2", //
				6001, 8);
	}

	// {6010, 6002}
	public void test0165() {
		check(//
				"Integrate[(a + b*ArcCosh[1 + d*x^2])^(-3), x]", //
				"-(2*x^2 + d*x^4)/(4*b*x*Sqrt[d*x^2]*Sqrt[2 + d*x^2]*(a + b*ArcCosh[1 + d*x^2])^2) - x/(8*b^2*(a + b*ArcCosh[1 + d*x^2])) + (x*Cosh[a/(2*b)]*CoshIntegral[(a + b*ArcCosh[1 + d*x^2])/(2*b)])/(8*Sqrt[2]*b^3*Sqrt[d*x^2]) - (x*Sinh[a/(2*b)]*SinhIntegral[(a + b*ArcCosh[1 + d*x^2])/(2*b)])/(8*Sqrt[2]*b^3*Sqrt[d*x^2])", //
				6010, 6002);
	}

	// {6001, 8}
	public void test0166() {
		check(//
				"Integrate[(a + b*ArcCosh[-1 + d*x^2])^2, x]", //
				"8*b^2*x + (4*b*(2*x^2 - d*x^4)*(a + b*ArcCosh[-1 + d*x^2]))/(x*Sqrt[d*x^2]*Sqrt[-2 + d*x^2]) + x*(a + b*ArcCosh[-1 + d*x^2])^2", //
				6001, 8);
	}

	// {6010, 6003}
	public void test0167() {
		check(//
				"Integrate[(a + b*ArcCosh[-1 + d*x^2])^(-3), x]", //
				"(2*x^2 - d*x^4)/(4*b*x*Sqrt[d*x^2]*Sqrt[-2 + d*x^2]*(a + b*ArcCosh[-1 + d*x^2])^2) - x/(8*b^2*(a + b*ArcCosh[-1 + d*x^2])) - (x*CoshIntegral[(a + b*ArcCosh[-1 + d*x^2])/(2*b)]*Sinh[a/(2*b)])/(8*Sqrt[2]*b^3*Sqrt[d*x^2]) + (x*Cosh[a/(2*b)]*SinhIntegral[(a + b*ArcCosh[-1 + d*x^2])/(2*b)])/(8*Sqrt[2]*b^3*Sqrt[d*x^2])", //
				6010, 6003);
	}

	// {6001, 5999}
	public void test0168() {
		check(//
				"Integrate[(a + b*ArcCosh[1 + d*x^2])^(5/2), x]", //
				"(-5*b*(2*x^2 + d*x^4)*(a + b*ArcCosh[1 + d*x^2])^(3/2))/(x*Sqrt[d*x^2]*Sqrt[2 + d*x^2]) + x*(a + b*ArcCosh[1 + d*x^2])^(5/2) - (15*b^(5/2)*Sqrt[Pi/2]*Erfi[Sqrt[a + b*ArcCosh[1 + d*x^2]]/(Sqrt[2]*Sqrt[b])]*(Cosh[a/(2*b)] - Sinh[a/(2*b)])*Sinh[ArcCosh[1 + d*x^2]/2])/(d*x) + (15*b^(5/2)*Sqrt[Pi/2]*Erf[Sqrt[a + b*ArcCosh[1 + d*x^2]]/(Sqrt[2]*Sqrt[b])]*(Cosh[a/(2*b)] + Sinh[a/(2*b)])*Sinh[ArcCosh[1 + d*x^2]/2])/(d*x) + (30*b^2*Sqrt[a + b*ArcCosh[1 + d*x^2]]*Sinh[ArcCosh[1 + d*x^2]/2]^2)/(d*x)", //
				6001, 5999);
	}

	// {6001, 6004}
	public void test0169() {
		check(//
				"Integrate[(a + b*ArcCosh[1 + d*x^2])^(3/2), x]", //
				"(-3*b*(2*x^2 + d*x^4)*Sqrt[a + b*ArcCosh[1 + d*x^2]])/(x*Sqrt[d*x^2]*Sqrt[2 + d*x^2]) + x*(a + b*ArcCosh[1 + d*x^2])^(3/2) + (3*b^(3/2)*Sqrt[Pi/2]*Erfi[Sqrt[a + b*ArcCosh[1 + d*x^2]]/(Sqrt[2]*Sqrt[b])]*(Cosh[a/(2*b)] - Sinh[a/(2*b)])*Sinh[ArcCosh[1 + d*x^2]/2])/(d*x) + (3*b^(3/2)*Sqrt[Pi/2]*Erf[Sqrt[a + b*ArcCosh[1 + d*x^2]]/(Sqrt[2]*Sqrt[b])]*(Cosh[a/(2*b)] + Sinh[a/(2*b)])*Sinh[ArcCosh[1 + d*x^2]/2])/(d*x)", //
				6001, 6004);
	}

	// {6010, 6004}
	public void test0170() {
		check(//
				"Integrate[(a + b*ArcCosh[1 + d*x^2])^(-5/2), x]", //
				"-(2*x^2 + d*x^4)/(3*b*x*Sqrt[d*x^2]*Sqrt[2 + d*x^2]*(a + b*ArcCosh[1 + d*x^2])^(3/2)) - x/(3*b^2*Sqrt[a + b*ArcCosh[1 + d*x^2]]) + (Sqrt[Pi/2]*Erfi[Sqrt[a + b*ArcCosh[1 + d*x^2]]/(Sqrt[2]*Sqrt[b])]*(Cosh[a/(2*b)] - Sinh[a/(2*b)])*Sinh[ArcCosh[1 + d*x^2]/2])/(3*b^(5/2)*d*x) + (Sqrt[Pi/2]*Erf[Sqrt[a + b*ArcCosh[1 + d*x^2]]/(Sqrt[2]*Sqrt[b])]*(Cosh[a/(2*b)] + Sinh[a/(2*b)])*Sinh[ArcCosh[1 + d*x^2]/2])/(3*b^(5/2)*d*x)", //
				6010, 6004);
	}

	// {6010, 6006}
	public void test0171() {
		check(//
				"Integrate[(a + b*ArcCosh[1 + d*x^2])^(-7/2), x]", //
				"-(2*x^2 + d*x^4)/(5*b*x*Sqrt[d*x^2]*Sqrt[2 + d*x^2]*(a + b*ArcCosh[1 + d*x^2])^(5/2)) - x/(15*b^2*(a + b*ArcCosh[1 + d*x^2])^(3/2)) - (Sqrt[d*x^2]*Sqrt[2 + d*x^2])/(15*b^3*d*x*Sqrt[a + b*ArcCosh[1 + d*x^2]]) + (Sqrt[Pi/2]*Erfi[Sqrt[a + b*ArcCosh[1 + d*x^2]]/(Sqrt[2]*Sqrt[b])]*(Cosh[a/(2*b)] - Sinh[a/(2*b)])*Sinh[ArcCosh[1 + d*x^2]/2])/(15*b^(7/2)*d*x) - (Sqrt[Pi/2]*Erf[Sqrt[a + b*ArcCosh[1 + d*x^2]]/(Sqrt[2]*Sqrt[b])]*(Cosh[a/(2*b)] + Sinh[a/(2*b)])*Sinh[ArcCosh[1 + d*x^2]/2])/(15*b^(7/2)*d*x)", //
				6010, 6006);
	}

	// {6001, 6000}
	public void test0172() {
		check(//
				"Integrate[(a + b*ArcCosh[-1 + d*x^2])^(5/2), x]", //
				"(5*b*(2*x^2 - d*x^4)*(a + b*ArcCosh[-1 + d*x^2])^(3/2))/(x*Sqrt[d*x^2]*Sqrt[-2 + d*x^2]) + x*(a + b*ArcCosh[-1 + d*x^2])^(5/2) + (30*b^2*Sqrt[a + b*ArcCosh[-1 + d*x^2]]*Cosh[ArcCosh[-1 + d*x^2]/2]^2)/(d*x) - (15*b^(5/2)*Sqrt[Pi/2]*Cosh[ArcCosh[-1 + d*x^2]/2]*Erfi[Sqrt[a + b*ArcCosh[-1 + d*x^2]]/(Sqrt[2]*Sqrt[b])]*(Cosh[a/(2*b)] - Sinh[a/(2*b)]))/(d*x) - (15*b^(5/2)*Sqrt[Pi/2]*Cosh[ArcCosh[-1 + d*x^2]/2]*Erf[Sqrt[a + b*ArcCosh[-1 + d*x^2]]/(Sqrt[2]*Sqrt[b])]*(Cosh[a/(2*b)] + Sinh[a/(2*b)]))/(d*x)", //
				6001, 6000);
	}

	// {6001, 6005}
	public void test0173() {
		check(//
				"Integrate[(a + b*ArcCosh[-1 + d*x^2])^(3/2), x]", //
				"(3*b*(2*x^2 - d*x^4)*Sqrt[a + b*ArcCosh[-1 + d*x^2]])/(x*Sqrt[d*x^2]*Sqrt[-2 + d*x^2]) + x*(a + b*ArcCosh[-1 + d*x^2])^(3/2) + (3*b^(3/2)*Sqrt[Pi/2]*Cosh[ArcCosh[-1 + d*x^2]/2]*Erfi[Sqrt[a + b*ArcCosh[-1 + d*x^2]]/(Sqrt[2]*Sqrt[b])]*(Cosh[a/(2*b)] - Sinh[a/(2*b)]))/(d*x) - (3*b^(3/2)*Sqrt[Pi/2]*Cosh[ArcCosh[-1 + d*x^2]/2]*Erf[Sqrt[a + b*ArcCosh[-1 + d*x^2]]/(Sqrt[2]*Sqrt[b])]*(Cosh[a/(2*b)] + Sinh[a/(2*b)]))/(d*x)", //
				6001, 6005);
	}

	// {6010, 6005}
	public void test0174() {
		check(//
				"Integrate[(a + b*ArcCosh[-1 + d*x^2])^(-5/2), x]", //
				"(2*x^2 - d*x^4)/(3*b*x*Sqrt[d*x^2]*Sqrt[-2 + d*x^2]*(a + b*ArcCosh[-1 + d*x^2])^(3/2)) - x/(3*b^2*Sqrt[a + b*ArcCosh[-1 + d*x^2]]) + (Sqrt[Pi/2]*Cosh[ArcCosh[-1 + d*x^2]/2]*Erfi[Sqrt[a + b*ArcCosh[-1 + d*x^2]]/(Sqrt[2]*Sqrt[b])]*(Cosh[a/(2*b)] - Sinh[a/(2*b)]))/(3*b^(5/2)*d*x) - (Sqrt[Pi/2]*Cosh[ArcCosh[-1 + d*x^2]/2]*Erf[Sqrt[a + b*ArcCosh[-1 + d*x^2]]/(Sqrt[2]*Sqrt[b])]*(Cosh[a/(2*b)] + Sinh[a/(2*b)]))/(3*b^(5/2)*d*x)", //
				6010, 6005);
	}

	// {6010, 6007}
	public void test0175() {
		check(//
				"Integrate[(a + b*ArcCosh[-1 + d*x^2])^(-7/2), x]", //
				"(2*x^2 - d*x^4)/(5*b*x*Sqrt[d*x^2]*Sqrt[-2 + d*x^2]*(a + b*ArcCosh[-1 + d*x^2])^(5/2)) - x/(15*b^2*(a + b*ArcCosh[-1 + d*x^2])^(3/2)) - (Sqrt[d*x^2]*Sqrt[-2 + d*x^2])/(15*b^3*d*x*Sqrt[a + b*ArcCosh[-1 + d*x^2]]) + (Sqrt[Pi/2]*Cosh[ArcCosh[-1 + d*x^2]/2]*Erfi[Sqrt[a + b*ArcCosh[-1 + d*x^2]]/(Sqrt[2]*Sqrt[b])]*(Cosh[a/(2*b)] - Sinh[a/(2*b)]))/(15*b^(7/2)*d*x) + (Sqrt[Pi/2]*Cosh[ArcCosh[-1 + d*x^2]/2]*Erf[Sqrt[a + b*ArcCosh[-1 + d*x^2]]/(Sqrt[2]*Sqrt[b])]*(Cosh[a/(2*b)] + Sinh[a/(2*b)]))/(15*b^(7/2)*d*x)", //
				6010, 6007);
	}

	// {5953, 3382}
	public void test0176() {
		check(//
				"Integrate[x/(Sqrt[-1 + x]*Sqrt[1 + x]*ArcCosh[x]), x]", //
				"CoshIntegral[ArcCosh[x]]", //
				5953, 3382);
	}

	// {6013, 5893}
	public void test0177() {
		check(//
				"Integrate[ArcCosh[Sqrt[1 + b*x^2]]^n/Sqrt[1 + b*x^2], x]", //
				"(Sqrt[-1 + Sqrt[1 + b*x^2]]*Sqrt[1 + Sqrt[1 + b*x^2]]*ArcCosh[Sqrt[1 + b*x^2]]^(1 + n))/(b*(1 + n)*x)", //
				6013, 5893);
	}

	// {6013, 5891}
	public void test0178() {
		check(//
				"Integrate[1/(Sqrt[1 + b*x^2]*ArcCosh[Sqrt[1 + b*x^2]]), x]", //
				"(Sqrt[-1 + Sqrt[1 + b*x^2]]*Sqrt[1 + Sqrt[1 + b*x^2]]*Log[ArcCosh[Sqrt[1 + b*x^2]]])/(b*x)", //
				6013, 5891);
	}

	// {6049, 371}
	public void test0179() {
		check(//
				"Integrate[(d*x)^m*(a + b*ArcTanh[c*x]), x]", //
				"((d*x)^(1 + m)*(a + b*ArcTanh[c*x]))/(d*(1 + m)) - (b*c*(d*x)^(2 + m)*Hypergeometric2F1[1, (2 + m)/2, (4 + m)/2, c^2*x^2])/(d^2*(1 + m)*(2 + m))", //
				6049, 371);
	}

	// {6037, 266}
	public void test0180() {
		check(//
				"Integrate[x*(a + b*ArcTanh[c*x^2]), x]", //
				"(x^2*(a + b*ArcTanh[c*x^2]))/2 + (b*Log[1 - c^2*x^4])/(4*c)", //
				6037, 266);
	}

	// {6035, 6031}
	public void test0181() {
		check(//
				"Integrate[(a + b*ArcTanh[c*x^2])/x, x]", //
				"a*Log[x] - (b*PolyLog[2, -(c*x^2)])/4 + (b*PolyLog[2, c*x^2])/4", //
				6035, 6031);
	}

	// {6049, 371}
	public void test0182() {
		check(//
				"Integrate[(d*x)^m*(a + b*ArcTanh[c*x^2]), x]", //
				"((d*x)^(1 + m)*(a + b*ArcTanh[c*x^2]))/(d*(1 + m)) - (2*b*c*(d*x)^(3 + m)*Hypergeometric2F1[1, (3 + m)/4, (7 + m)/4, c^2*x^4])/(d^3*(1 + m)*(3 + m))", //
				6049, 371);
	}

	// {6037, 266}
	public void test0183() {
		check(//
				"Integrate[x^2*(a + b*ArcTanh[c*x^3]), x]", //
				"(x^3*(a + b*ArcTanh[c*x^3]))/3 + (b*Log[1 - c^2*x^6])/(6*c)", //
				6037, 266);
	}

	// {6035, 6031}
	public void test0184() {
		check(//
				"Integrate[(a + b*ArcTanh[c*x^3])/x, x]", //
				"a*Log[x] - (b*PolyLog[2, -(c*x^3)])/6 + (b*PolyLog[2, c*x^3])/6", //
				6035, 6031);
	}

	// {6049, 371}
	public void test0185() {
		check(//
				"Integrate[(d*x)^m*(a + b*ArcTanh[c*x^3]), x]", //
				"((d*x)^(1 + m)*(a + b*ArcTanh[c*x^3]))/(d*(1 + m)) - (3*b*c*(d*x)^(4 + m)*Hypergeometric2F1[1, (4 + m)/6, (10 + m)/6, c^2*x^6])/(d^4*(1 + m)*(4 + m))", //
				6049, 371);
	}

	// {6035, 6031}
	public void test0186() {
		check(//
				"Integrate[(a + b*ArcTanh[c/x])/x, x]", //
				"a*Log[x] + (b*PolyLog[2, -(c/x)])/2 - (b*PolyLog[2, c/x])/2", //
				6035, 6031);
	}

	// {6037, 266}
	public void test0187() {
		check(//
				"Integrate[(a + b*ArcTanh[c/x])/x^2, x]", //
				"-((a + b*ArcTanh[c/x])/x) - (b*Log[1 - c^2/x^2])/(2*c)", //
				6037, 266);
	}

	// {6035, 6031}
	public void test0188() {
		check(//
				"Integrate[(a + b*ArcTanh[c/x^2])/x, x]", //
				"a*Log[x] + (b*PolyLog[2, -(c/x^2)])/4 - (b*PolyLog[2, c/x^2])/4", //
				6035, 6031);
	}

	// {6037, 266}
	public void test0189() {
		check(//
				"Integrate[(a + b*ArcTanh[c/x^2])/x^3, x]", //
				"-(a + b*ArcTanh[c/x^2])/(2*x^2) - (b*Log[1 - c^2/x^4])/(4*c)", //
				6037, 266);
	}

	// {6035, 6031}
	public void test0190() {
		check(//
				"Integrate[(a + b*ArcTanh[c*Sqrt[x]])/x, x]", //
				"a*Log[x] - b*PolyLog[2, -(c*Sqrt[x])] + b*PolyLog[2, c*Sqrt[x]]", //
				6035, 6031);
	}

	// {6037, 31}
	public void test0191() {
		check(//
				"Integrate[ArcTanh[Sqrt[x]]/Sqrt[x], x]", //
				"2*Sqrt[x]*ArcTanh[Sqrt[x]] + Log[1 - x]", //
				6037, 31);
	}

	// {6035, 6031}
	public void test0192() {
		check(//
				"Integrate[(a + b*ArcTanh[c*x^(3/2)])/x, x]", //
				"a*Log[x] - (b*PolyLog[2, -(c*x^(3/2))])/3 + (b*PolyLog[2, c*x^(3/2)])/3", //
				6035, 6031);
	}

	// {6037, 371}
	public void test0193() {
		check(//
				"Integrate[x^2*(a + b*ArcTanh[c*x^n]), x]", //
				"(x^3*(a + b*ArcTanh[c*x^n]))/3 - (b*c*n*x^(3 + n)*Hypergeometric2F1[1, (3 + n)/(2*n), (3*(1 + n))/(2*n), c^2*x^(2*n)])/(3*(3 + n))", //
				6037, 371);
	}

	// {6037, 371}
	public void test0194() {
		check(//
				"Integrate[x*(a + b*ArcTanh[c*x^n]), x]", //
				"(x^2*(a + b*ArcTanh[c*x^n]))/2 - (b*c*n*x^(2 + n)*Hypergeometric2F1[1, (2 + n)/(2*n), (3 + 2/n)/2, c^2*x^(2*n)])/(2*(2 + n))", //
				6037, 371);
	}

	// {6035, 6031}
	public void test0195() {
		check(//
				"Integrate[(a + b*ArcTanh[c*x^n])/x, x]", //
				"a*Log[x] - (b*PolyLog[2, -(c*x^n)])/(2*n) + (b*PolyLog[2, c*x^n])/(2*n)", //
				6035, 6031);
	}

	// {6037, 371}
	public void test0196() {
		check(//
				"Integrate[(a + b*ArcTanh[c*x^n])/x^2, x]", //
				"-((a + b*ArcTanh[c*x^n])/x) - (b*c*n*x^(-1 + n)*Hypergeometric2F1[1, -(1 - n)/(2*n), (3 - n^(-1))/2, c^2*x^(2*n)])/(1 - n)", //
				6037, 371);
	}

	// {6037, 371}
	public void test0197() {
		check(//
				"Integrate[(a + b*ArcTanh[c*x^n])/x^3, x]", //
				"-(a + b*ArcTanh[c*x^n])/(2*x^2) - (b*c*n*x^(-2 + n)*Hypergeometric2F1[1, (1 - 2/n)/2, (3 - 2/n)/2, c^2*x^(2*n)])/(2*(2 - n))", //
				6037, 371);
	}

	// {6037, 371}
	public void test0198() {
		check(//
				"Integrate[(a + b*ArcTanh[c*x^n])/x^4, x]", //
				"-(a + b*ArcTanh[c*x^n])/(3*x^3) - (b*c*n*x^(-3 + n)*Hypergeometric2F1[1, -(3 - n)/(2*n), (-3*(1 - n))/(2*n), c^2*x^(2*n)])/(3*(3 - n))", //
				6037, 371);
	}

	// {6035, 6031}
	public void test0199() {
		check(//
				"Integrate[ArcTanh[a*x^n]/x, x]", //
				"-PolyLog[2, -(a*x^n)]/(2*n) + PolyLog[2, a*x^n]/(2*n)", //
				6035, 6031);
	}

	// {6035, 6031}
	public void test0200() {
		check(//
				"Integrate[ArcTanh[a*x^5]/x, x]", //
				"-PolyLog[2, -(a*x^5)]/10 + PolyLog[2, a*x^5]/10", //
				6035, 6031);
	}

	// {6079, 2497}
	public void test0201() {
		check(//
				"Integrate[(a + b*ArcTanh[c*x])/(x*(d + c*d*x)), x]", //
				"((a + b*ArcTanh[c*x])*Log[2 - 2/(1 + c*x)])/d - (b*PolyLog[2, -1 + 2/(1 + c*x)])/(2*d)", //
				6079, 2497);
	}

	// {6141}
	public void test0202() {
		check(//
				"Integrate[x*(1 - a^2*x^2)*ArcTanh[a*x], x]", //
				"x/(4*a) - (a*x^3)/12 - ((1 - a^2*x^2)^2*ArcTanh[a*x])/(4*a^2)", //
				6141);
	}

	// {6145, 6095}
	public void test0203() {
		check(//
				"Integrate[(x^2*ArcTanh[a*x])/(1 - a^2*x^2)^2, x]", //
				"-1/(4*a^3*(1 - a^2*x^2)) + (x*ArcTanh[a*x])/(2*a^2*(1 - a^2*x^2)) - ArcTanh[a*x]^2/(4*a^3)", //
				6145, 6095);
	}

	// {6103, 267}
	public void test0204() {
		check(//
				"Integrate[ArcTanh[a*x]/(1 - a^2*x^2)^2, x]", //
				"-1/(4*a*(1 - a^2*x^2)) + (x*ArcTanh[a*x])/(2*(1 - a^2*x^2)) + ArcTanh[a*x]^2/(4*a)", //
				6103, 267);
	}

	// {6141, 222}
	public void test0205() {
		check(//
				"Integrate[(x*ArcTanh[a*x])/Sqrt[1 - a^2*x^2], x]", //
				"ArcSin[a*x]/a^2 - (Sqrt[1 - a^2*x^2]*ArcTanh[a*x])/a^2", //
				6141, 222);
	}

	// {6141, 6097}
	public void test0206() {
		check(//
				"Integrate[(x*ArcTanh[a*x]^2)/Sqrt[1 - a^2*x^2], x]", //
				"(-4*ArcTan[Sqrt[1 - a*x]/Sqrt[1 + a*x]]*ArcTanh[a*x])/a^2 - (Sqrt[1 - a^2*x^2]*ArcTanh[a*x]^2)/a^2 - ((2*I)*PolyLog[2, ((-I)*Sqrt[1 - a*x])/Sqrt[1 + a*x]])/a^2 + ((2*I)*PolyLog[2, (I*Sqrt[1 - a*x])/Sqrt[1 + a*x]])/a^2", //
				6141, 6097);
	}

	// {6155, 6165}
	public void test0207() {
		check(//
				"Integrate[ArcTanh[a*x]^2/(x^2*Sqrt[1 - a^2*x^2]), x]", //
				"-((Sqrt[1 - a^2*x^2]*ArcTanh[a*x]^2)/x) - 4*a*ArcTanh[a*x]*ArcTanh[Sqrt[1 - a*x]/Sqrt[1 + a*x]] + 2*a*PolyLog[2, -(Sqrt[1 - a*x]/Sqrt[1 + a*x])] - 2*a*PolyLog[2, Sqrt[1 - a*x]/Sqrt[1 + a*x]]", //
				6155, 6165);
	}

	// {6145, 6097}
	public void test0208() {
		check(//
				"Integrate[(x^2*ArcTanh[a*x])/(1 - a^2*x^2)^(3/2), x]", //
				"-(1/(a^3*Sqrt[1 - a^2*x^2])) + (x*ArcTanh[a*x])/(a^2*Sqrt[1 - a^2*x^2]) + (2*ArcTan[Sqrt[1 - a*x]/Sqrt[1 + a*x]]*ArcTanh[a*x])/a^3 + (I*PolyLog[2, ((-I)*Sqrt[1 - a*x])/Sqrt[1 + a*x]])/a^3 - (I*PolyLog[2, (I*Sqrt[1 - a*x])/Sqrt[1 + a*x]])/a^3", //
				6145, 6097);
	}

	// {6141, 197}
	public void test0209() {
		check(//
				"Integrate[(x*ArcTanh[a*x])/(1 - a^2*x^2)^(3/2), x]", //
				"-(x/(a*Sqrt[1 - a^2*x^2])) + ArcTanh[a*x]/(a^2*Sqrt[1 - a^2*x^2])", //
				6141, 197);
	}

	// {6141, 6105}
	public void test0210() {
		check(//
				"Integrate[(x*ArcTanh[a*x]^2)/(1 - a^2*x^2)^(3/2), x]", //
				"2/(a^2*Sqrt[1 - a^2*x^2]) - (2*x*ArcTanh[a*x])/(a*Sqrt[1 - a^2*x^2]) + ArcTanh[a*x]^2/(a^2*Sqrt[1 - a^2*x^2])", //
				6141, 6105);
	}

	// {6109, 197}
	public void test0211() {
		check(//
				"Integrate[ArcTanh[a*x]^2/(1 - a^2*x^2)^(3/2), x]", //
				"(2*x)/Sqrt[1 - a^2*x^2] - (2*ArcTanh[a*x])/(a*Sqrt[1 - a^2*x^2]) + (x*ArcTanh[a*x]^2)/Sqrt[1 - a^2*x^2]", //
				6109, 197);
	}

	// {6109, 6105}
	public void test0212() {
		check(//
				"Integrate[ArcTanh[a*x]^3/(1 - a^2*x^2)^(3/2), x]", //
				"-6/(a*Sqrt[1 - a^2*x^2]) + (6*x*ArcTanh[a*x])/Sqrt[1 - a^2*x^2] - (3*ArcTanh[a*x]^2)/(a*Sqrt[1 - a^2*x^2]) + (x*ArcTanh[a*x]^3)/Sqrt[1 - a^2*x^2]", //
				6109, 6105);
	}

	// {6181, 3379}
	public void test0213() {
		check(//
				"Integrate[x/((1 - a^2*x^2)^(3/2)*ArcTanh[a*x]), x]", //
				"SinhIntegral[ArcTanh[a*x]]/a^2", //
				6181, 3379);
	}

	// {6115, 3382}
	public void test0214() {
		check(//
				"Integrate[1/((1 - a^2*x^2)^(3/2)*ArcTanh[a*x]), x]", //
				"CoshIntegral[ArcTanh[a*x]]/a", //
				6115, 3382);
	}

	// {6089, 6097}
	public void test0215() {
		check(//
				"Integrate[Sqrt[1 - a^2*x^2]*ArcTanh[a*x], x]", //
				"Sqrt[1 - a^2*x^2]/(2*a) + (x*Sqrt[1 - a^2*x^2]*ArcTanh[a*x])/2 - (ArcTan[Sqrt[1 - a*x]/Sqrt[1 + a*x]]*ArcTanh[a*x])/a - ((I/2)*PolyLog[2, ((-I)*Sqrt[1 - a*x])/Sqrt[1 + a*x]])/a + ((I/2)*PolyLog[2, (I*Sqrt[1 - a*x])/Sqrt[1 + a*x]])/a", //
				6089, 6097);
	}

	// {6089, 6097}
	public void test0216() {
		check(//
				"Integrate[Sqrt[1 - a^2*x^2]*ArcTanh[a*x], x]", //
				"Sqrt[1 - a^2*x^2]/(2*a) + (x*Sqrt[1 - a^2*x^2]*ArcTanh[a*x])/2 - (ArcTan[Sqrt[1 - a*x]/Sqrt[1 + a*x]]*ArcTanh[a*x])/a - ((I/2)*PolyLog[2, ((-I)*Sqrt[1 - a*x])/Sqrt[1 + a*x]])/a + ((I/2)*PolyLog[2, (I*Sqrt[1 - a*x])/Sqrt[1 + a*x]])/a", //
				6089, 6097);
	}

	// {6107, 6105}
	public void test0217() {
		check(//
				"Integrate[ArcTanh[a*x]/(1 - a^2*x^2)^(5/2), x]", //
				"-1/(9*a*(1 - a^2*x^2)^(3/2)) - 2/(3*a*Sqrt[1 - a^2*x^2]) + (x*ArcTanh[a*x])/(3*(1 - a^2*x^2)^(3/2)) + (2*x*ArcTanh[a*x])/(3*Sqrt[1 - a^2*x^2])", //
				6107, 6105);
	}

	// {6101, 6097}
	public void test0218() {
		check(//
				"Integrate[ArcTanh[a*x]/Sqrt[c - a^2*c*x^2], x]", //
				"(-2*Sqrt[1 - a^2*x^2]*ArcTan[Sqrt[1 - a*x]/Sqrt[1 + a*x]]*ArcTanh[a*x])/(a*Sqrt[c - a^2*c*x^2]) - (I*Sqrt[1 - a^2*x^2]*PolyLog[2, ((-I)*Sqrt[1 - a*x])/Sqrt[1 + a*x]])/(a*Sqrt[c - a^2*c*x^2]) + (I*Sqrt[1 - a^2*x^2]*PolyLog[2, (I*Sqrt[1 - a*x])/Sqrt[1 + a*x]])/(a*Sqrt[c - a^2*c*x^2])", //
				6101, 6097);
	}

	// {6107, 6105}
	public void test0219() {
		check(//
				"Integrate[ArcTanh[a*x]/(c - a^2*c*x^2)^(5/2), x]", //
				"-1/(9*a*c*(c - a^2*c*x^2)^(3/2)) - 2/(3*a*c^2*Sqrt[c - a^2*c*x^2]) + (x*ArcTanh[a*x])/(3*c*(c - a^2*c*x^2)^(3/2)) + (2*x*ArcTanh[a*x])/(3*c^2*Sqrt[c - a^2*c*x^2])", //
				6107, 6105);
	}

	// {6115, 3382}
	public void test0220() {
		check(//
				"Integrate[1/((1 - a^2*x^2)^(3/2)*ArcTanh[a*x]), x]", //
				"CoshIntegral[ArcTanh[a*x]]/a", //
				6115, 3382);
	}

	// {6101, 6097}
	public void test0221() {
		check(//
				"Integrate[ArcTanh[x]/Sqrt[a - a*x^2], x]", //
				"(-2*Sqrt[1 - x^2]*ArcTan[Sqrt[1 - x]/Sqrt[1 + x]]*ArcTanh[x])/Sqrt[a - a*x^2] - (I*Sqrt[1 - x^2]*PolyLog[2, ((-I)*Sqrt[1 - x])/Sqrt[1 + x]])/Sqrt[a - a*x^2] + (I*Sqrt[1 - x^2]*PolyLog[2, (I*Sqrt[1 - x])/Sqrt[1 + x]])/Sqrt[a - a*x^2]", //
				6101, 6097);
	}

	// {6107, 6105}
	public void test0222() {
		check(//
				"Integrate[ArcTanh[x]/(a - a*x^2)^(5/2), x]", //
				"-1/(9*a*(a - a*x^2)^(3/2)) - 2/(3*a^2*Sqrt[a - a*x^2]) + (x*ArcTanh[x])/(3*a*(a - a*x^2)^(3/2)) + (2*x*ArcTanh[x])/(3*a^2*Sqrt[a - a*x^2])", //
				6107, 6105);
	}

	// {6246, 6059}
	public void test0223() {
		check(//
				"Integrate[ArcTanh[a + b*x]^2/x, x]", //
				"-(ArcTanh[a + b*x]^2*Log[2/(1 + a + b*x)]) + ArcTanh[a + b*x]^2*Log[(2*b*x)/((1 - a)*(1 + a + b*x))] + ArcTanh[a + b*x]*PolyLog[2, 1 - 2/(1 + a + b*x)] - ArcTanh[a + b*x]*PolyLog[2, 1 - (2*b*x)/((1 - a)*(1 + a + b*x))] + PolyLog[3, 1 - 2/(1 + a + b*x)]/2 - PolyLog[3, 1 - (2*b*x)/((1 - a)*(1 + a + b*x))]/2", //
				6246, 6059);
	}

	// {6246, 6059}
	public void test0224() {
		check(//
				"Integrate[(a + b*ArcTanh[c + d*x])^2/(e + f*x), x]", //
				"-(((a + b*ArcTanh[c + d*x])^2*Log[2/(1 + c + d*x)])/f) + ((a + b*ArcTanh[c + d*x])^2*Log[(2*d*(e + f*x))/((d*e + f - c*f)*(1 + c + d*x))])/f + (b*(a + b*ArcTanh[c + d*x])*PolyLog[2, 1 - 2/(1 + c + d*x)])/f - (b*(a + b*ArcTanh[c + d*x])*PolyLog[2, 1 - (2*d*(e + f*x))/((d*e + f - c*f)*(1 + c + d*x))])/f + (b^2*PolyLog[3, 1 - 2/(1 + c + d*x)])/(2*f) - (b^2*PolyLog[3, 1 - (2*d*(e + f*x))/((d*e + f - c*f)*(1 + c + d*x))])/(2*f)", //
				6246, 6059);
	}

	// {6246, 6061}
	public void test0225() {
		check(//
				"Integrate[(a + b*ArcTanh[c + d*x])^3/(e + f*x), x]", //
				"-(((a + b*ArcTanh[c + d*x])^3*Log[2/(1 + c + d*x)])/f) + ((a + b*ArcTanh[c + d*x])^3*Log[(2*d*(e + f*x))/((d*e + f - c*f)*(1 + c + d*x))])/f + (3*b*(a + b*ArcTanh[c + d*x])^2*PolyLog[2, 1 - 2/(1 + c + d*x)])/(2*f) - (3*b*(a + b*ArcTanh[c + d*x])^2*PolyLog[2, 1 - (2*d*(e + f*x))/((d*e + f - c*f)*(1 + c + d*x))])/(2*f) + (3*b^2*(a + b*ArcTanh[c + d*x])*PolyLog[3, 1 - 2/(1 + c + d*x)])/(2*f) - (3*b^2*(a + b*ArcTanh[c + d*x])*PolyLog[3, 1 - (2*d*(e + f*x))/((d*e + f - c*f)*(1 + c + d*x))])/(2*f) + (3*b^3*PolyLog[4, 1 - 2/(1 + c + d*x)])/(4*f) - (3*b^3*PolyLog[4, 1 - (2*d*(e + f*x))/((d*e + f - c*f)*(1 + c + d*x))])/(4*f)", //
				6246, 6061);
	}

	// {6261, 138}
	public void test0226() {
		check(//
				"Integrate[E^(ArcTanh[a*x]/2)*x^m, x]", //
				"(x^(1 + m)*AppellF1[1 + m, 1/4, -1/4, 2 + m, a*x, -(a*x)])/(1 + m)", //
				6261, 138);
	}

	// {6261, 138}
	public void test0227() {
		check(//
				"Integrate[E^((3*ArcTanh[a*x])/2)*x^m, x]", //
				"(x^(1 + m)*AppellF1[1 + m, 3/4, -3/4, 2 + m, a*x, -(a*x)])/(1 + m)", //
				6261, 138);
	}

	// {6261, 138}
	public void test0228() {
		check(//
				"Integrate[E^((5*ArcTanh[a*x])/2)*x^m, x]", //
				"(x^(1 + m)*AppellF1[1 + m, 5/4, -5/4, 2 + m, a*x, -(a*x)])/(1 + m)", //
				6261, 138);
	}

	// {6261, 138}
	public void test0229() {
		check(//
				"Integrate[x^m/E^(ArcTanh[a*x]/2), x]", //
				"(x^(1 + m)*AppellF1[1 + m, -1/4, 1/4, 2 + m, a*x, -(a*x)])/(1 + m)", //
				6261, 138);
	}

	// {6261, 138}
	public void test0230() {
		check(//
				"Integrate[x^m/E^((3*ArcTanh[a*x])/2), x]", //
				"(x^(1 + m)*AppellF1[1 + m, -3/4, 3/4, 2 + m, a*x, -(a*x)])/(1 + m)", //
				6261, 138);
	}

	// {6261, 138}
	public void test0231() {
		check(//
				"Integrate[x^m/E^((5*ArcTanh[a*x])/2), x]", //
				"(x^(1 + m)*AppellF1[1 + m, -5/4, 5/4, 2 + m, a*x, -(a*x)])/(1 + m)", //
				6261, 138);
	}

	// {6261, 138}
	public void test0232() {
		check(//
				"Integrate[E^(ArcTanh[x]/3)*x^m, x]", //
				"(x^(1 + m)*AppellF1[1 + m, 1/6, -1/6, 2 + m, x, -x])/(1 + m)", //
				6261, 138);
	}

	// {6261, 138}
	public void test0233() {
		check(//
				"Integrate[E^((2*ArcTanh[x])/3)*x^m, x]", //
				"(x^(1 + m)*AppellF1[1 + m, 1/3, -1/3, 2 + m, x, -x])/(1 + m)", //
				6261, 138);
	}

	// {6261, 138}
	public void test0234() {
		check(//
				"Integrate[E^(ArcTanh[a*x]/4)*x^m, x]", //
				"(x^(1 + m)*AppellF1[1 + m, 1/8, -1/8, 2 + m, a*x, -(a*x)])/(1 + m)", //
				6261, 138);
	}

	// {6261, 138}
	public void test0235() {
		check(//
				"Integrate[E^(n*ArcTanh[a*x])*x^m, x]", //
				"(x^(1 + m)*AppellF1[1 + m, n/2, -n/2, 2 + m, a*x, -(a*x)])/(1 + m)", //
				6261, 138);
	}

	// {6260, 71}
	public void test0236() {
		check(//
				"Integrate[E^(n*ArcTanh[a*x]), x]", //
				"-((2^(1 + n/2)*(1 - a*x)^(1 - n/2)*Hypergeometric2F1[1 - n/2, -n/2, 2 - n/2, (1 - a*x)/2])/(a*(2 - n)))", //
				6260, 71);
	}

	// {6261, 133}
	public void test0237() {
		check(//
				"Integrate[E^(n*ArcTanh[a*x])/x^2, x]", //
				"(-4*a*(1 - a*x)^(1 - n/2)*(1 + a*x)^((-2 + n)/2)*Hypergeometric2F1[2, 1 - n/2, 2 - n/2, (1 - a*x)/(1 + a*x)])/(2 - n)", //
				6261, 133);
	}

	// {6262, 665}
	public void test0238() {
		check(//
				"Integrate[E^ArcTanh[a*x]/(c - a*c*x)^2, x]", //
				"(1 - a^2*x^2)^(3/2)/(3*a*c^2*(1 - a*x)^3)", //
				6262, 665);
	}

	// {6264, 34}
	public void test0239() {
		check(//
				"Integrate[E^(2*ArcTanh[a*x])/(c - a*c*x)^2, x]", //
				"x/(c^2*(1 - a*x)^2)", //
				6264, 34);
	}

	// {6262, 665}
	public void test0240() {
		check(//
				"Integrate[E^(3*ArcTanh[a*x])/(c - a*c*x)^2, x]", //
				"(1 - a^2*x^2)^(5/2)/(5*a*c^2*(1 - a*x)^5)", //
				6262, 665);
	}

	// {6264, 32}
	public void test0241() {
		check(//
				"Integrate[E^(4*ArcTanh[a*x])*(c - a*c*x)^2, x]", //
				"(c^2*(1 + a*x)^3)/(3*a)", //
				6264, 32);
	}

	// {6264, 37}
	public void test0242() {
		check(//
				"Integrate[E^(4*ArcTanh[a*x])/(c - a*c*x)^2, x]", //
				"(1 + a*x)^3/(6*a*c^2*(1 - a*x)^3)", //
				6264, 37);
	}

	// {6262, 222}
	public void test0243() {
		check(//
				"Integrate[1/(E^ArcTanh[a*x]*(c - a*c*x)), x]", //
				"ArcSin[a*x]/(a*c)", //
				6262, 222);
	}

	// {6262, 665}
	public void test0244() {
		check(//
				"Integrate[1/(E^ArcTanh[a*x]*(c - a*c*x)^2), x]", //
				"Sqrt[1 - a^2*x^2]/(a*c^2*(1 - a*x))", //
				6262, 665);
	}

	// {6264, 31}
	public void test0245() {
		check(//
				"Integrate[1/(E^(2*ArcTanh[a*x])*(c - a*c*x)), x]", //
				"Log[1 + a*x]/(a*c)", //
				6264, 31);
	}

	// {6262, 651}
	public void test0246() {
		check(//
				"Integrate[1/(E^(3*ArcTanh[a*x])*(c - a*c*x)^2), x]", //
				"-((1 - a*x)/(a*c^2*Sqrt[1 - a^2*x^2]))", //
				6262, 651);
	}

	// {6262, 197}
	public void test0247() {
		check(//
				"Integrate[1/(E^(3*ArcTanh[a*x])*(c - a*c*x)^3), x]", //
				"x/(c^3*Sqrt[1 - a^2*x^2])", //
				6262, 197);
	}

	// {6262, 663}
	public void test0248() {
		check(//
				"Integrate[E^ArcTanh[a*x]*Sqrt[c - a*c*x], x]", //
				"(2*c^2*(1 - a^2*x^2)^(3/2))/(3*a*(c - a*c*x)^(3/2))", //
				6262, 663);
	}

	// {6262, 663}
	public void test0249() {
		check(//
				"Integrate[E^(3*ArcTanh[a*x])*(c - a*c*x)^(3/2), x]", //
				"(2*c^4*(1 - a^2*x^2)^(5/2))/(5*a*(c - a*c*x)^(5/2))", //
				6262, 663);
	}

	// {6262, 663}
	public void test0250() {
		check(//
				"Integrate[1/(E^ArcTanh[a*x]*Sqrt[c - a*c*x]), x]", //
				"(2*Sqrt[1 - a^2*x^2])/(a*Sqrt[c - a*c*x])", //
				6262, 663);
	}

	// {6262, 663}
	public void test0251() {
		check(//
				"Integrate[1/(E^(3*ArcTanh[a*x])*(c - a*c*x)^(3/2)), x]", //
				"(-2*Sqrt[c - a*c*x])/(a*c^2*Sqrt[1 - a^2*x^2])", //
				6262, 663);
	}

	// {6263, 267}
	public void test0252() {
		check(//
				"Integrate[E^ArcTanh[a*x]*x*(c - a*c*x), x]", //
				"-(c*(1 - a^2*x^2)^(3/2))/(3*a^2)", //
				6263, 267);
	}

	// {6263, 270}
	public void test0253() {
		check(//
				"Integrate[(E^ArcTanh[a*x]*(c - a*c*x))/x^4, x]", //
				"-(c*(1 - a^2*x^2)^(3/2))/(3*x^3)", //
				6263, 270);
	}

	// {6262, 665}
	public void test0254() {
		check(//
				"Integrate[E^ArcTanh[a*x]/(c - a*c*x)^2, x]", //
				"(1 - a^2*x^2)^(3/2)/(3*a*c^2*(1 - a*x)^3)", //
				6262, 665);
	}

	// {6264, 75}
	public void test0255() {
		check(//
				"Integrate[(E^ArcTanh[x]*x)/(1 + x), x]", //
				"-(Sqrt[1 - x]*Sqrt[1 + x])", //
				6264, 75);
	}

	// {6264, 37}
	public void test0256() {
		check(//
				"Integrate[E^ArcTanh[x]/(1 + x)^2, x]", //
				"-(Sqrt[1 - x]/Sqrt[1 + x])", //
				6264, 37);
	}

	// {6264, 32}
	public void test0257() {
		check(//
				"Integrate[E^ArcTanh[x]/Sqrt[1 + x], x]", //
				"-2*Sqrt[1 - x]", //
				6264, 32);
	}

	// {6262, 663}
	public void test0258() {
		check(//
				"Integrate[E^ArcTanh[a*x]*Sqrt[c - a*c*x], x]", //
				"(2*c^2*(1 - a^2*x^2)^(3/2))/(3*a*(c - a*c*x)^(3/2))", //
				6262, 663);
	}

	// {6264, 71}
	public void test0259() {
		check(//
				"Integrate[E^(n*ArcTanh[a*x])*(c - a*c*x)^3, x]", //
				"-((2^(1 + n/2)*c^3*(1 - a*x)^(4 - n/2)*Hypergeometric2F1[4 - n/2, -n/2, 5 - n/2, (1 - a*x)/2])/(a*(8 - n)))", //
				6264, 71);
	}

	// {6264, 71}
	public void test0260() {
		check(//
				"Integrate[E^(n*ArcTanh[a*x])*(c - a*c*x)^2, x]", //
				"-((2^(1 + n/2)*c^2*(1 - a*x)^(3 - n/2)*Hypergeometric2F1[3 - n/2, -n/2, 4 - n/2, (1 - a*x)/2])/(a*(6 - n)))", //
				6264, 71);
	}

	// {6264, 71}
	public void test0261() {
		check(//
				"Integrate[E^(n*ArcTanh[a*x])*(c - a*c*x), x]", //
				"-((2^(1 + n/2)*c*(1 - a*x)^(2 - n/2)*Hypergeometric2F1[2 - n/2, -n/2, 3 - n/2, (1 - a*x)/2])/(a*(4 - n)))", //
				6264, 71);
	}

	// {6264, 71}
	public void test0262() {
		check(//
				"Integrate[E^(n*ArcTanh[a*x])/(c - a*c*x), x]", //
				"(2^(1 + n/2)*Hypergeometric2F1[-n/2, -n/2, 1 - n/2, (1 - a*x)/2])/(a*c*n*(1 - a*x)^(n/2))", //
				6264, 71);
	}

	// {6264, 37}
	public void test0263() {
		check(//
				"Integrate[E^(n*ArcTanh[a*x])/(c - a*c*x)^2, x]", //
				"((1 - a*x)^(-1 - n/2)*(1 + a*x)^((2 + n)/2))/(a*c^2*(2 + n))", //
				6264, 37);
	}

	// {6299, 37}
	public void test0264() {
		check(//
				"Integrate[E^ArcTanh[a + b*x]/(1 - a^2 - 2*a*b*x - b^2*x^2), x]", //
				"Sqrt[1 + a + b*x]/(b*Sqrt[1 - a - b*x])", //
				6299, 37);
	}

	// {6296, 71}
	public void test0265() {
		check(//
				"Integrate[E^(n*ArcTanh[a + b*x]), x]", //
				"-((2^(1 + n/2)*(1 - a - b*x)^(1 - n/2)*Hypergeometric2F1[1 - n/2, -n/2, 2 - n/2, (1 - a - b*x)/2])/(b*(2 - n)))", //
				6296, 71);
	}

	// {6298, 133}
	public void test0266() {
		check(//
				"Integrate[E^(n*ArcTanh[a + b*x])/x^2, x]", //
				"(-4*b*(1 - a - b*x)^(1 - n/2)*(1 + a + b*x)^((-2 + n)/2)*Hypergeometric2F1[2, 1 - n/2, 2 - n/2, ((1 + a)*(1 - a - b*x))/((1 - a)*(1 + a + b*x))])/((1 - a)^2*(2 - n))", //
				6298, 133);
	}

	// {6275, 31}
	public void test0267() {
		check(//
				"Integrate[E^ArcTanh[a*x]/Sqrt[1 - a^2*x^2], x]", //
				"-(Log[1 - a*x]/a)", //
				6275, 31);
	}

	// {6285, 66}
	public void test0268() {
		check(//
				"Integrate[(E^ArcTanh[a*x]*x^m)/Sqrt[1 - a^2*x^2], x]", //
				"(x^(1 + m)*Hypergeometric2F1[1, 1 + m, 2 + m, a*x])/(1 + m)", //
				6285, 66);
	}

	// {6275, 71}
	public void test0269() {
		check(//
				"Integrate[E^ArcTanh[a*x]*(1 - a^2*x^2)^p, x]", //
				"-((2^(3/2 + p)*(1 - a*x)^(1/2 + p)*Hypergeometric2F1[-1/2 - p, 1/2 + p, 3/2 + p, (1 - a*x)/2])/(a*(1 + 2*p)))", //
				6275, 71);
	}

	// {6275, 32}
	public void test0270() {
		check(//
				"Integrate[E^(2*ArcTanh[a*x])*(c - a^2*c*x^2), x]", //
				"(c*(1 + a*x)^3)/(3*a)", //
				6275, 32);
	}

	// {6285, 37}
	public void test0271() {
		check(//
				"Integrate[(E^(2*ArcTanh[a*x])*(c - a^2*c*x^2))/x^4, x]", //
				"-(c*(1 + a*x)^3)/(3*x^3)", //
				6285, 37);
	}

	// {6285, 75}
	public void test0272() {
		check(//
				"Integrate[(E^(2*ArcTanh[a*x])*(c - a^2*c*x^2)^2)/x^3, x]", //
				"-(c^2*(1 + a*x)^4)/(2*x^2)", //
				6285, 75);
	}

	// {6275, 32}
	public void test0273() {
		check(//
				"Integrate[E^(2*ArcTanh[a*x])/(c - a^2*c*x^2), x]", //
				"1/(a*c*(1 - a*x))", //
				6275, 32);
	}

	// {6285, 82}
	public void test0274() {
		check(//
				"Integrate[(E^(2*ArcTanh[a*x])*x^2)/(c - a^2*c*x^2)^3, x]", //
				"-(1 - 2*a*x)/(6*a^3*c^3*(1 - a*x)^3*(1 + a*x))", //
				6285, 82);
	}

	// {6285, 66}
	public void test0275() {
		check(//
				"Integrate[(E^(2*ArcTanh[a*x])*x^m)/(c - a^2*c*x^2), x]", //
				"(x^(1 + m)*Hypergeometric2F1[2, 1 + m, 2 + m, a*x])/(c*(1 + m))", //
				6285, 66);
	}

	// {6275, 32}
	public void test0276() {
		check(//
				"Integrate[E^(4*ArcTanh[a*x])*(c - a^2*c*x^2)^2, x]", //
				"(c^2*(1 + a*x)^5)/(5*a)", //
				6275, 32);
	}

	// {6275, 34}
	public void test0277() {
		check(//
				"Integrate[E^(4*ArcTanh[a*x])/(c - a^2*c*x^2), x]", //
				"x/(c*(1 - a*x)^2)", //
				6275, 34);
	}

	// {6275, 32}
	public void test0278() {
		check(//
				"Integrate[E^(4*ArcTanh[a*x])/(c - a^2*c*x^2)^2, x]", //
				"1/(3*a*c^2*(1 - a*x)^3)", //
				6275, 32);
	}

	// {6275, 71}
	public void test0279() {
		check(//
				"Integrate[(1 - a^2*x^2)^p/E^ArcTanh[a*x], x]", //
				"-((2^(1/2 + p)*(1 - a*x)^(3/2 + p)*Hypergeometric2F1[1/2 - p, 3/2 + p, 5/2 + p, (1 - a*x)/2])/(a*(3 + 2*p)))", //
				6275, 71);
	}

	// {6275, 32}
	public void test0280() {
		check(//
				"Integrate[(c - a^2*c*x^2)/E^(2*ArcTanh[a*x]), x]", //
				"-(c*(1 - a*x)^3)/(3*a)", //
				6275, 32);
	}

	// {6275, 32}
	public void test0281() {
		check(//
				"Integrate[1/(E^(2*ArcTanh[a*x])*(c - a^2*c*x^2)), x]", //
				"-(1/(a*c*(1 + a*x)))", //
				6275, 32);
	}

	// {6271, 6270}
	public void test0282() {
		check(//
				"Integrate[E^(ArcTanh[a*x]/2)/(1 - a^2*x^2)^(5/2), x]", //
				"(-2*E^(ArcTanh[a*x]/2)*(1 - 6*a*x))/(35*a*(1 - a^2*x^2)^(3/2)) - (16*E^(ArcTanh[a*x]/2)*(1 - 2*a*x))/(35*a*Sqrt[1 - a^2*x^2])", //
				6271, 6270);
	}

	// {6271, 6270}
	public void test0283() {
		check(//
				"Integrate[E^(ArcTanh[a*x]/2)/(c - a^2*c*x^2)^(5/2), x]", //
				"(-2*E^(ArcTanh[a*x]/2)*(1 - 6*a*x))/(35*a*c*(c - a^2*c*x^2)^(3/2)) - (16*E^(ArcTanh[a*x]/2)*(1 - 2*a*x))/(35*a*c^2*Sqrt[c - a^2*c*x^2])", //
				6271, 6270);
	}

	// {6275, 71}
	public void test0284() {
		check(//
				"Integrate[E^(n*ArcTanh[a*x])*(c - a^2*c*x^2), x]", //
				"-((2^(2 + n/2)*c*(1 - a*x)^(2 - n/2)*Hypergeometric2F1[-1 - n/2, 2 - n/2, 3 - n/2, (1 - a*x)/2])/(a*(4 - n)))", //
				6275, 71);
	}

	// {6275, 71}
	public void test0285() {
		check(//
				"Integrate[E^(n*ArcTanh[a*x])*(c - a^2*c*x^2)^2, x]", //
				"-((2^(3 + n/2)*c^2*(1 - a*x)^(3 - n/2)*Hypergeometric2F1[-2 - n/2, 3 - n/2, 4 - n/2, (1 - a*x)/2])/(a*(6 - n)))", //
				6275, 71);
	}

	// {6275, 71}
	public void test0286() {
		check(//
				"Integrate[E^(n*ArcTanh[a*x])*(c - a^2*c*x^2)^3, x]", //
				"-((2^(4 + n/2)*c^3*(1 - a*x)^(4 - n/2)*Hypergeometric2F1[-3 - n/2, 4 - n/2, 5 - n/2, (1 - a*x)/2])/(a*(8 - n)))", //
				6275, 71);
	}

	// {6282, 6272}
	public void test0287() {
		check(//
				"Integrate[(E^(n*ArcTanh[a*x])*x^2)/(c - a^2*c*x^2)^2, x]", //
				"-((E^(n*ArcTanh[a*x])*(2 - n^2))/(a^3*c^2*n*(4 - n^2))) - (E^(n*ArcTanh[a*x])*(n - 2*a*x))/(a^3*c^2*(4 - n^2)*(1 - a^2*x^2))", //
				6282, 6272);
	}

	// {6271, 6272}
	public void test0288() {
		check(//
				"Integrate[E^(n*ArcTanh[a*x])/(c - a^2*c*x^2)^2, x]", //
				"(2*E^(n*ArcTanh[a*x]))/(a*c^2*n*(4 - n^2)) - (E^(n*ArcTanh[a*x])*(n - 2*a*x))/(a*c^2*(4 - n^2)*(1 - a^2*x^2))", //
				6271, 6272);
	}

	// {6282, 6270}
	public void test0289() {
		check(//
				"Integrate[(E^(n*ArcTanh[a*x])*x^2)/(c - a^2*c*x^2)^(5/2), x]", //
				"-((E^(n*ArcTanh[a*x])*(n - 3*a*x))/(a^3*c*(9 - n^2)*(c - a^2*c*x^2)^(3/2))) + (E^(n*ArcTanh[a*x])*(3 - n^2)*(n - a*x))/(a^3*c^2*(9 - 10*n^2 + n^4)*Sqrt[c - a^2*c*x^2])", //
				6282, 6270);
	}

	// {6271, 6270}
	public void test0290() {
		check(//
				"Integrate[E^(n*ArcTanh[a*x])/(c - a^2*c*x^2)^(5/2), x]", //
				"-((E^(n*ArcTanh[a*x])*(n - 3*a*x))/(a*c*(9 - n^2)*(c - a^2*c*x^2)^(3/2))) - (6*E^(n*ArcTanh[a*x])*(n - a*x))/(a*c^2*(1 - n^2)*(9 - n^2)*Sqrt[c - a^2*c*x^2])", //
				6271, 6270);
	}

	// {6285, 138}
	public void test0291() {
		check(//
				"Integrate[E^(n*ArcTanh[a*x])*x^m*(c - a^2*c*x^2)^2, x]", //
				"(c^2*x^(1 + m)*AppellF1[1 + m, (-4 + n)/2, -2 - n/2, 2 + m, a*x, -(a*x)])/(1 + m)", //
				6285, 138);
	}

	// {6285, 138}
	public void test0292() {
		check(//
				"Integrate[E^(n*ArcTanh[a*x])*x^m*(c - a^2*c*x^2), x]", //
				"(c*x^(1 + m)*AppellF1[1 + m, (-2 + n)/2, -1 - n/2, 2 + m, a*x, -(a*x)])/(1 + m)", //
				6285, 138);
	}

	// {6285, 138}
	public void test0293() {
		check(//
				"Integrate[(E^(n*ArcTanh[a*x])*x^m)/(c - a^2*c*x^2), x]", //
				"(x^(1 + m)*AppellF1[1 + m, (2 + n)/2, 1 - n/2, 2 + m, a*x, -(a*x)])/(c*(1 + m))", //
				6285, 138);
	}

	// {6285, 138}
	public void test0294() {
		check(//
				"Integrate[(E^(n*ArcTanh[a*x])*x^m)/(c - a^2*c*x^2)^2, x]", //
				"(x^(1 + m)*AppellF1[1 + m, (4 + n)/2, 2 - n/2, 2 + m, a*x, -(a*x)])/(c^2*(1 + m))", //
				6285, 138);
	}

	// {6285, 82}
	public void test0295() {
		check(//
				"Integrate[(E^(6*ArcTanh[a*x])*x^2)/(c - a^2*c*x^2)^19, x]", //
				"-(1 - 6*a*x)/(210*a^3*c^19*(1 - a*x)^21*(1 + a*x)^15)", //
				6285, 82);
	}

	// {6285, 82}
	public void test0296() {
		check(//
				"Integrate[(E^(4*ArcTanh[a*x])*x^2)/(c - a^2*c*x^2)^9, x]", //
				"-(1 - 4*a*x)/(60*a^3*c^9*(1 - a*x)^10*(1 + a*x)^6)", //
				6285, 82);
	}

	// {6285, 82}
	public void test0297() {
		check(//
				"Integrate[(E^(2*ArcTanh[a*x])*x^2)/(c - a^2*c*x^2)^3, x]", //
				"-(1 - 2*a*x)/(6*a^3*c^3*(1 - a*x)^3*(1 + a*x))", //
				6285, 82);
	}

	// {6285, 82}
	public void test0298() {
		check(//
				"Integrate[x^2/(E^(2*ArcTanh[a*x])*(c - a^2*c*x^2)^3), x]", //
				"(1 + 2*a*x)/(6*a^3*c^3*(1 - a*x)*(1 + a*x)^3)", //
				6285, 82);
	}

	// {6285, 82}
	public void test0299() {
		check(//
				"Integrate[x^2/(E^(4*ArcTanh[a*x])*(c - a^2*c*x^2)^9), x]", //
				"(1 + 4*a*x)/(60*a^3*c^9*(1 - a*x)^6*(1 + a*x)^10)", //
				6285, 82);
	}

	// {6356, 270}
	public void test0300() {
		check(//
				"Integrate[ArcTanh[(Sqrt[e]*x)/Sqrt[d + e*x^2]]/x^3, x]", //
				"-(Sqrt[e]*Sqrt[d + e*x^2])/(2*d*x) - ArcTanh[(Sqrt[e]*x)/Sqrt[d + e*x^2]]/(2*x^2)", //
				6356, 270);
	}

	// {6352, 267}
	public void test0301() {
		check(//
				"Integrate[ArcTanh[(Sqrt[e]*x)/Sqrt[d + e*x^2]], x]", //
				"-(Sqrt[d + e*x^2]/Sqrt[e]) + x*ArcTanh[(Sqrt[e]*x)/Sqrt[d + e*x^2]]", //
				6352, 267);
	}

	// {6813, 6031}
	public void test0302() {
		check(//
				"Integrate[(a + b*ArcTanh[Sqrt[1 - c*x]/Sqrt[1 + c*x]])/(1 - c^2*x^2), x]", //
				"-((a*Log[Sqrt[1 - c*x]/Sqrt[1 + c*x]])/c) + (b*PolyLog[2, -(Sqrt[1 - c*x]/Sqrt[1 + c*x])])/(2*c) - (b*PolyLog[2, Sqrt[1 - c*x]/Sqrt[1 + c*x]])/(2*c)", //
				6813, 6031);
	}

	// {2199, 30}
	public void test0303() {
		check(//
				"Integrate[x^m*ArcTanh[Tanh[a + b*x]], x]", //
				"-((b*x^(2 + m))/(2 + 3*m + m^2)) + (x^(1 + m)*ArcTanh[Tanh[a + b*x]])/(1 + m)", //
				2199, 30);
	}

	// {2199, 30}
	public void test0304() {
		check(//
				"Integrate[x^2*ArcTanh[Tanh[a + b*x]], x]", //
				"-(b*x^4)/12 + (x^3*ArcTanh[Tanh[a + b*x]])/3", //
				2199, 30);
	}

	// {6374, 30}
	public void test0305() {
		check(//
				"Integrate[x*ArcTanh[Tanh[a + b*x]], x]", //
				"-(b*x^3)/6 + (x^2*ArcTanh[Tanh[a + b*x]])/2", //
				6374, 30);
	}

	// {2188, 30}
	public void test0306() {
		check(//
				"Integrate[ArcTanh[Tanh[a + b*x]], x]", //
				"ArcTanh[Tanh[a + b*x]]^2/(2*b)", //
				2188, 30);
	}

	// {2189, 29}
	public void test0307() {
		check(//
				"Integrate[ArcTanh[Tanh[a + b*x]]/x, x]", //
				"b*x - (b*x - ArcTanh[Tanh[a + b*x]])*Log[x]", //
				2189, 29);
	}

	// {2199, 29}
	public void test0308() {
		check(//
				"Integrate[ArcTanh[Tanh[a + b*x]]/x^2, x]", //
				"-(ArcTanh[Tanh[a + b*x]]/x) + b*Log[x]", //
				2199, 29);
	}

	// {2199, 30}
	public void test0309() {
		check(//
				"Integrate[ArcTanh[Tanh[a + b*x]]/x^3, x]", //
				"-b/(2*x) - ArcTanh[Tanh[a + b*x]]/(2*x^2)", //
				2199, 30);
	}

	// {2199, 30}
	public void test0310() {
		check(//
				"Integrate[ArcTanh[Tanh[a + b*x]]/x^4, x]", //
				"-b/(6*x^2) - ArcTanh[Tanh[a + b*x]]/(3*x^3)", //
				2199, 30);
	}

	// {2188, 30}
	public void test0311() {
		check(//
				"Integrate[ArcTanh[Tanh[a + b*x]]^2, x]", //
				"ArcTanh[Tanh[a + b*x]]^3/(3*b)", //
				2188, 30);
	}

	// {2188, 30}
	public void test0312() {
		check(//
				"Integrate[ArcTanh[Tanh[a + b*x]]^3, x]", //
				"ArcTanh[Tanh[a + b*x]]^4/(4*b)", //
				2188, 30);
	}

	// {2202, 2198}
	public void test0313() {
		check(//
				"Integrate[ArcTanh[Tanh[a + b*x]]^3/x^6, x]", //
				"(b*ArcTanh[Tanh[a + b*x]]^4)/(20*x^4*(b*x - ArcTanh[Tanh[a + b*x]])^2) + ArcTanh[Tanh[a + b*x]]^4/(5*x^5*(b*x - ArcTanh[Tanh[a + b*x]]))", //
				2202, 2198);
	}

	// {2188, 30}
	public void test0314() {
		check(//
				"Integrate[ArcTanh[Tanh[a + b*x]]^4, x]", //
				"ArcTanh[Tanh[a + b*x]]^5/(5*b)", //
				2188, 30);
	}

	// {2202, 2198}
	public void test0315() {
		check(//
				"Integrate[ArcTanh[Tanh[a + b*x]]^4/x^7, x]", //
				"(b*ArcTanh[Tanh[a + b*x]]^5)/(30*x^5*(b*x - ArcTanh[Tanh[a + b*x]])^2) + ArcTanh[Tanh[a + b*x]]^5/(6*x^6*(b*x - ArcTanh[Tanh[a + b*x]]))", //
				2202, 2198);
	}

	// {2188, 29}
	public void test0316() {
		check(//
				"Integrate[ArcTanh[Tanh[a + b*x]]^(-1), x]", //
				"Log[ArcTanh[Tanh[a + b*x]]]/b", //
				2188, 29);
	}

	// {2199, 2195}
	public void test0317() {
		check(//
				"Integrate[x^m/ArcTanh[Tanh[a + b*x]]^2, x]", //
				"-(x^m/(b*ArcTanh[Tanh[a + b*x]])) - (x^m*Hypergeometric2F1[1, m, 1 + m, (b*x)/(b*x - ArcTanh[Tanh[a + b*x]])])/(b*(b*x - ArcTanh[Tanh[a + b*x]]))", //
				2199, 2195);
	}

	// {2188, 30}
	public void test0318() {
		check(//
				"Integrate[ArcTanh[Tanh[a + b*x]]^(-2), x]", //
				"-(1/(b*ArcTanh[Tanh[a + b*x]]))", //
				2188, 30);
	}

	// {2188, 30}
	public void test0319() {
		check(//
				"Integrate[ArcTanh[Tanh[a + b*x]]^(-3), x]", //
				"-1/(2*b*ArcTanh[Tanh[a + b*x]]^2)", //
				2188, 30);
	}

	// {2188, 30}
	public void test0320() {
		check(//
				"Integrate[Sqrt[ArcTanh[Tanh[a + b*x]]], x]", //
				"(2*ArcTanh[Tanh[a + b*x]]^(3/2))/(3*b)", //
				2188, 30);
	}

	// {2190, 2192}
	public void test0321() {
		check(//
				"Integrate[Sqrt[ArcTanh[Tanh[a + b*x]]]/x, x]", //
				"-2*ArcTan[Sqrt[ArcTanh[Tanh[a + b*x]]]/Sqrt[b*x - ArcTanh[Tanh[a + b*x]]]]*Sqrt[b*x - ArcTanh[Tanh[a + b*x]]] + 2*Sqrt[ArcTanh[Tanh[a + b*x]]]", //
				2190, 2192);
	}

	// {2199, 2192}
	public void test0322() {
		check(//
				"Integrate[Sqrt[ArcTanh[Tanh[a + b*x]]]/x^2, x]", //
				"(b*ArcTan[Sqrt[ArcTanh[Tanh[a + b*x]]]/Sqrt[b*x - ArcTanh[Tanh[a + b*x]]]])/Sqrt[b*x - ArcTanh[Tanh[a + b*x]]] - Sqrt[ArcTanh[Tanh[a + b*x]]]/x", //
				2199, 2192);
	}

	// {2188, 30}
	public void test0323() {
		check(//
				"Integrate[ArcTanh[Tanh[a + b*x]]^(3/2), x]", //
				"(2*ArcTanh[Tanh[a + b*x]]^(5/2))/(5*b)", //
				2188, 30);
	}

	// {2188, 30}
	public void test0324() {
		check(//
				"Integrate[ArcTanh[Tanh[a + b*x]]^(5/2), x]", //
				"(2*ArcTanh[Tanh[a + b*x]]^(7/2))/(7*b)", //
				2188, 30);
	}

	// {2188, 30}
	public void test0325() {
		check(//
				"Integrate[1/Sqrt[ArcTanh[Tanh[a + b*x]]], x]", //
				"(2*Sqrt[ArcTanh[Tanh[a + b*x]]])/b", //
				2188, 30);
	}

	// {2188, 30}
	public void test0326() {
		check(//
				"Integrate[ArcTanh[Tanh[a + b*x]]^(-3/2), x]", //
				"-2/(b*Sqrt[ArcTanh[Tanh[a + b*x]]])", //
				2188, 30);
	}

	// {2194, 2192}
	public void test0327() {
		check(//
				"Integrate[1/(x*ArcTanh[Tanh[a + b*x]]^(3/2)), x]", //
				"(-2*ArcTan[Sqrt[ArcTanh[Tanh[a + b*x]]]/Sqrt[b*x - ArcTanh[Tanh[a + b*x]]]])/(b*x - ArcTanh[Tanh[a + b*x]])^(3/2) - 2/((b*x - ArcTanh[Tanh[a + b*x]])*Sqrt[ArcTanh[Tanh[a + b*x]]])", //
				2194, 2192);
	}

	// {2188, 30}
	public void test0328() {
		check(//
				"Integrate[ArcTanh[Tanh[a + b*x]]^(-5/2), x]", //
				"-2/(3*b*ArcTanh[Tanh[a + b*x]]^(3/2))", //
				2188, 30);
	}

	// {2199, 30}
	public void test0329() {
		check(//
				"Integrate[x^(7/2)*ArcTanh[Tanh[a + b*x]], x]", //
				"(-4*b*x^(11/2))/99 + (2*x^(9/2)*ArcTanh[Tanh[a + b*x]])/9", //
				2199, 30);
	}

	// {2199, 30}
	public void test0330() {
		check(//
				"Integrate[x^(5/2)*ArcTanh[Tanh[a + b*x]], x]", //
				"(-4*b*x^(9/2))/63 + (2*x^(7/2)*ArcTanh[Tanh[a + b*x]])/7", //
				2199, 30);
	}

	// {2199, 30}
	public void test0331() {
		check(//
				"Integrate[x^(3/2)*ArcTanh[Tanh[a + b*x]], x]", //
				"(-4*b*x^(7/2))/35 + (2*x^(5/2)*ArcTanh[Tanh[a + b*x]])/5", //
				2199, 30);
	}

	// {2199, 30}
	public void test0332() {
		check(//
				"Integrate[Sqrt[x]*ArcTanh[Tanh[a + b*x]], x]", //
				"(-4*b*x^(5/2))/15 + (2*x^(3/2)*ArcTanh[Tanh[a + b*x]])/3", //
				2199, 30);
	}

	// {2199, 30}
	public void test0333() {
		check(//
				"Integrate[ArcTanh[Tanh[a + b*x]]/Sqrt[x], x]", //
				"(-4*b*x^(3/2))/3 + 2*Sqrt[x]*ArcTanh[Tanh[a + b*x]]", //
				2199, 30);
	}

	// {2199, 30}
	public void test0334() {
		check(//
				"Integrate[ArcTanh[Tanh[a + b*x]]/x^(3/2), x]", //
				"4*b*Sqrt[x] - (2*ArcTanh[Tanh[a + b*x]])/Sqrt[x]", //
				2199, 30);
	}

	// {2199, 30}
	public void test0335() {
		check(//
				"Integrate[ArcTanh[Tanh[a + b*x]]/x^(5/2), x]", //
				"(-4*b)/(3*Sqrt[x]) - (2*ArcTanh[Tanh[a + b*x]])/(3*x^(3/2))", //
				2199, 30);
	}

	// {2199, 30}
	public void test0336() {
		check(//
				"Integrate[ArcTanh[Tanh[a + b*x]]/x^(7/2), x]", //
				"(-4*b)/(15*x^(3/2)) - (2*ArcTanh[Tanh[a + b*x]])/(5*x^(5/2))", //
				2199, 30);
	}

	// {2190, 2193}
	public void test0337() {
		check(//
				"Integrate[Sqrt[x]/ArcTanh[Tanh[a + b*x]], x]", //
				"(2*Sqrt[x])/b - (2*ArcTanh[(Sqrt[b]*Sqrt[x])/Sqrt[b*x - ArcTanh[Tanh[a + b*x]]]]*Sqrt[b*x - ArcTanh[Tanh[a + b*x]]])/b^(3/2)", //
				2190, 2193);
	}

	// {2194, 2193}
	public void test0338() {
		check(//
				"Integrate[1/(x^(3/2)*ArcTanh[Tanh[a + b*x]]), x]", //
				"(-2*Sqrt[b]*ArcTanh[(Sqrt[b]*Sqrt[x])/Sqrt[b*x - ArcTanh[Tanh[a + b*x]]]])/(b*x - ArcTanh[Tanh[a + b*x]])^(3/2) + 2/(Sqrt[x]*(b*x - ArcTanh[Tanh[a + b*x]]))", //
				2194, 2193);
	}

	// {2199, 2193}
	public void test0339() {
		check(//
				"Integrate[Sqrt[x]/ArcTanh[Tanh[a + b*x]]^2, x]", //
				"-(ArcTanh[(Sqrt[b]*Sqrt[x])/Sqrt[b*x - ArcTanh[Tanh[a + b*x]]]]/(b^(3/2)*Sqrt[b*x - ArcTanh[Tanh[a + b*x]]])) - Sqrt[x]/(b*ArcTanh[Tanh[a + b*x]])", //
				2199, 2193);
	}

	// {2200, 2196}
	public void test0340() {
		check(//
				"Integrate[Sqrt[ArcTanh[Tanh[a + b*x]]]/Sqrt[x], x]", //
				"-((ArcTanh[(Sqrt[b]*Sqrt[x])/Sqrt[ArcTanh[Tanh[a + b*x]]]]*(b*x - ArcTanh[Tanh[a + b*x]]))/Sqrt[b]) + Sqrt[x]*Sqrt[ArcTanh[Tanh[a + b*x]]]", //
				2200, 2196);
	}

	// {2199, 2196}
	public void test0341() {
		check(//
				"Integrate[Sqrt[ArcTanh[Tanh[a + b*x]]]/x^(3/2), x]", //
				"2*Sqrt[b]*ArcTanh[(Sqrt[b]*Sqrt[x])/Sqrt[ArcTanh[Tanh[a + b*x]]]] - (2*Sqrt[ArcTanh[Tanh[a + b*x]]])/Sqrt[x]", //
				2199, 2196);
	}

	// {2202, 2198}
	public void test0342() {
		check(//
				"Integrate[Sqrt[ArcTanh[Tanh[a + b*x]]]/x^(7/2), x]", //
				"(4*b*ArcTanh[Tanh[a + b*x]]^(3/2))/(15*x^(3/2)*(b*x - ArcTanh[Tanh[a + b*x]])^2) + (2*ArcTanh[Tanh[a + b*x]]^(3/2))/(5*x^(5/2)*(b*x - ArcTanh[Tanh[a + b*x]]))", //
				2202, 2198);
	}

	// {2202, 2198}
	public void test0343() {
		check(//
				"Integrate[ArcTanh[Tanh[a + b*x]]^(3/2)/x^(9/2), x]", //
				"(4*b*ArcTanh[Tanh[a + b*x]]^(5/2))/(35*x^(5/2)*(b*x - ArcTanh[Tanh[a + b*x]])^2) + (2*ArcTanh[Tanh[a + b*x]]^(5/2))/(7*x^(7/2)*(b*x - ArcTanh[Tanh[a + b*x]]))", //
				2202, 2198);
	}

	// {2202, 2198}
	public void test0344() {
		check(//
				"Integrate[ArcTanh[Tanh[a + b*x]]^(5/2)/x^(11/2), x]", //
				"(4*b*ArcTanh[Tanh[a + b*x]]^(7/2))/(63*x^(7/2)*(b*x - ArcTanh[Tanh[a + b*x]])^2) + (2*ArcTanh[Tanh[a + b*x]]^(7/2))/(9*x^(9/2)*(b*x - ArcTanh[Tanh[a + b*x]]))", //
				2202, 2198);
	}

	// {2200, 2196}
	public void test0345() {
		check(//
				"Integrate[Sqrt[x]/Sqrt[ArcTanh[Tanh[a + b*x]]], x]", //
				"(ArcTanh[(Sqrt[b]*Sqrt[x])/Sqrt[ArcTanh[Tanh[a + b*x]]]]*(b*x - ArcTanh[Tanh[a + b*x]]))/b^(3/2) + (Sqrt[x]*Sqrt[ArcTanh[Tanh[a + b*x]]])/b", //
				2200, 2196);
	}

	// {2202, 2198}
	public void test0346() {
		check(//
				"Integrate[1/(x^(5/2)*Sqrt[ArcTanh[Tanh[a + b*x]]]), x]", //
				"(4*b*Sqrt[ArcTanh[Tanh[a + b*x]]])/(3*Sqrt[x]*(b*x - ArcTanh[Tanh[a + b*x]])^2) + (2*Sqrt[ArcTanh[Tanh[a + b*x]]])/(3*x^(3/2)*(b*x - ArcTanh[Tanh[a + b*x]]))", //
				2202, 2198);
	}

	// {2199, 2196}
	public void test0347() {
		check(//
				"Integrate[Sqrt[x]/ArcTanh[Tanh[a + b*x]]^(3/2), x]", //
				"(2*ArcTanh[(Sqrt[b]*Sqrt[x])/Sqrt[ArcTanh[Tanh[a + b*x]]]])/b^(3/2) - (2*Sqrt[x])/(b*Sqrt[ArcTanh[Tanh[a + b*x]]])", //
				2199, 2196);
	}

	// {2202, 2198}
	public void test0348() {
		check(//
				"Integrate[1/(x^(3/2)*ArcTanh[Tanh[a + b*x]]^(3/2)), x]", //
				"(-4*b*Sqrt[x])/((b*x - ArcTanh[Tanh[a + b*x]])^2*Sqrt[ArcTanh[Tanh[a + b*x]]]) + 2/(Sqrt[x]*(b*x - ArcTanh[Tanh[a + b*x]])*Sqrt[ArcTanh[Tanh[a + b*x]]])", //
				2202, 2198);
	}

	// {2202, 2198}
	public void test0349() {
		check(//
				"Integrate[1/(Sqrt[x]*ArcTanh[Tanh[a + b*x]]^(5/2)), x]", //
				"(-2*Sqrt[x])/(3*(b*x - ArcTanh[Tanh[a + b*x]])*ArcTanh[Tanh[a + b*x]]^(3/2)) + (4*Sqrt[x])/(3*(b*x - ArcTanh[Tanh[a + b*x]])^2*Sqrt[ArcTanh[Tanh[a + b*x]]])", //
				2202, 2198);
	}

	// {2188, 30}
	public void test0350() {
		check(//
				"Integrate[ArcTanh[Tanh[a + b*x]]^n, x]", //
				"ArcTanh[Tanh[a + b*x]]^(1 + n)/(b*(1 + n))", //
				2188, 30);
	}

	// {2199, 2195}
	public void test0351() {
		check(//
				"Integrate[ArcTanh[Tanh[a + b*x]]^n/x^2, x]", //
				"-(ArcTanh[Tanh[a + b*x]]^n/x) + (b*ArcTanh[Tanh[a + b*x]]^n*Hypergeometric2F1[1, n, 1 + n, -(ArcTanh[Tanh[a + b*x]]/(b*x - ArcTanh[Tanh[a + b*x]]))])/(b*x - ArcTanh[Tanh[a + b*x]])", //
				2199, 2195);
	}

	// {2199, 30}
	public void test0352() {
		check(//
				"Integrate[x^m*ArcCoth[Tanh[a + b*x]], x]", //
				"-((b*x^(2 + m))/(2 + 3*m + m^2)) + (x^(1 + m)*ArcCoth[Tanh[a + b*x]])/(1 + m)", //
				2199, 30);
	}

	// {2199, 30}
	public void test0353() {
		check(//
				"Integrate[x^2*ArcTanh[Coth[a + b*x]], x]", //
				"-(b*x^4)/12 + (x^3*ArcTanh[Coth[a + b*x]])/3", //
				2199, 30);
	}

	// {6376, 30}
	public void test0354() {
		check(//
				"Integrate[x*ArcTanh[Coth[a + b*x]], x]", //
				"-(b*x^3)/6 + (x^2*ArcTanh[Coth[a + b*x]])/2", //
				6376, 30);
	}

	// {2188, 30}
	public void test0355() {
		check(//
				"Integrate[ArcTanh[Coth[a + b*x]], x]", //
				"ArcTanh[Coth[a + b*x]]^2/(2*b)", //
				2188, 30);
	}

	// {2189, 29}
	public void test0356() {
		check(//
				"Integrate[ArcTanh[Coth[a + b*x]]/x, x]", //
				"b*x - (b*x - ArcTanh[Coth[a + b*x]])*Log[x]", //
				2189, 29);
	}

	// {2199, 29}
	public void test0357() {
		check(//
				"Integrate[ArcTanh[Coth[a + b*x]]/x^2, x]", //
				"-(ArcTanh[Coth[a + b*x]]/x) + b*Log[x]", //
				2199, 29);
	}

	// {2199, 30}
	public void test0358() {
		check(//
				"Integrate[ArcTanh[Coth[a + b*x]]/x^3, x]", //
				"-b/(2*x) - ArcTanh[Coth[a + b*x]]/(2*x^2)", //
				2199, 30);
	}

	// {2320, 6031}
	public void test0359() {
		check(//
				"Integrate[ArcTanh[E^x], x]", //
				"-PolyLog[2, -E^x]/2 + PolyLog[2, E^x]/2", //
				2320, 6031);
	}

	// {2320, 6031}
	public void test0360() {
		check(//
				"Integrate[ArcTanh[E^(a + b*x)], x]", //
				"-PolyLog[2, -E^(a + b*x)]/(2*b) + PolyLog[2, E^(a + b*x)]/(2*b)", //
				2320, 6031);
	}

	// {6022, 266}
	public void test0361() {
		check(//
				"Integrate[ArcCoth[a*x], x]", //
				"x*ArcCoth[a*x] + Log[1 - a^2*x^2]/(2*a)", //
				6022, 266);
	}

	// {6102, 6098}
	public void test0362() {
		check(//
				"Integrate[ArcCoth[x]/Sqrt[a - a*x^2], x]", //
				"(-2*Sqrt[1 - x^2]*ArcCoth[x]*ArcTan[Sqrt[1 - x]/Sqrt[1 + x]])/Sqrt[a - a*x^2] - (I*Sqrt[1 - x^2]*PolyLog[2, ((-I)*Sqrt[1 - x])/Sqrt[1 + x]])/Sqrt[a - a*x^2] + (I*Sqrt[1 - x^2]*PolyLog[2, (I*Sqrt[1 - x])/Sqrt[1 + x]])/Sqrt[a - a*x^2]", //
				6102, 6098);
	}

	// {6108, 6106}
	public void test0363() {
		check(//
				"Integrate[ArcCoth[x]/(a - a*x^2)^(5/2), x]", //
				"-1/(9*a*(a - a*x^2)^(3/2)) - 2/(3*a^2*Sqrt[a - a*x^2]) + (x*ArcCoth[x])/(3*a*(a - a*x^2)^(3/2)) + (2*x*ArcCoth[x])/(3*a^2*Sqrt[a - a*x^2])", //
				6108, 6106);
	}

	// {6104, 267}
	public void test0364() {
		check(//
				"Integrate[ArcCoth[x]/(1 - x^2)^2, x]", //
				"-1/(4*(1 - x^2)) + (x*ArcCoth[x])/(2*(1 - x^2)) + ArcCoth[x]^2/4", //
				6104, 267);
	}

	// {6247, 6060}
	public void test0365() {
		check(//
				"Integrate[ArcCoth[a + b*x]^2/x, x]", //
				"-(ArcCoth[a + b*x]^2*Log[2/(1 + a + b*x)]) + ArcCoth[a + b*x]^2*Log[(2*b*x)/((1 - a)*(1 + a + b*x))] + ArcCoth[a + b*x]*PolyLog[2, 1 - 2/(1 + a + b*x)] - ArcCoth[a + b*x]*PolyLog[2, 1 - (2*b*x)/((1 - a)*(1 + a + b*x))] + PolyLog[3, 1 - 2/(1 + a + b*x)]/2 - PolyLog[3, 1 - (2*b*x)/((1 - a)*(1 + a + b*x))]/2", //
				6247, 6060);
	}

	// {6036, 6032}
	public void test0366() {
		check(//
				"Integrate[ArcCoth[Sqrt[x]]/x, x]", //
				"PolyLog[2, -(1/Sqrt[x])] - PolyLog[2, 1/Sqrt[x]]", //
				6036, 6032);
	}

	// {6038, 31}
	public void test0367() {
		check(//
				"Integrate[ArcCoth[Sqrt[x]]/Sqrt[x], x]", //
				"2*Sqrt[x]*ArcCoth[Sqrt[x]] + Log[1 - x]", //
				6038, 31);
	}

	// {6036, 6032}
	public void test0368() {
		check(//
				"Integrate[ArcCoth[a*x^5]/x, x]", //
				"PolyLog[2, -(1/(a*x^5))]/10 - PolyLog[2, 1/(a*x^5)]/10", //
				6036, 6032);
	}

	// {6036, 6032}
	public void test0369() {
		check(//
				"Integrate[ArcCoth[a*x^n]/x, x]", //
				"PolyLog[2, -(1/(a*x^n))]/(2*n) - PolyLog[2, 1/(a*x^n)]/(2*n)", //
				6036, 6032);
	}

	// {6243, 6032}
	public void test0370() {
		check(//
				"Integrate[ArcCoth[a + b*x]/(a + b*x), x]", //
				"PolyLog[2, -(a + b*x)^(-1)]/(2*b) - PolyLog[2, (a + b*x)^(-1)]/(2*b)", //
				6243, 6032);
	}

	// {6247, 6060}
	public void test0371() {
		check(//
				"Integrate[(a + b*ArcCoth[c + d*x])^2/(e + f*x), x]", //
				"-(((a + b*ArcCoth[c + d*x])^2*Log[2/(1 + c + d*x)])/f) + ((a + b*ArcCoth[c + d*x])^2*Log[(2*d*(e + f*x))/((d*e + f - c*f)*(1 + c + d*x))])/f + (b*(a + b*ArcCoth[c + d*x])*PolyLog[2, 1 - 2/(1 + c + d*x)])/f - (b*(a + b*ArcCoth[c + d*x])*PolyLog[2, 1 - (2*d*(e + f*x))/((d*e + f - c*f)*(1 + c + d*x))])/f + (b^2*PolyLog[3, 1 - 2/(1 + c + d*x)])/(2*f) - (b^2*PolyLog[3, 1 - (2*d*(e + f*x))/((d*e + f - c*f)*(1 + c + d*x))])/(2*f)", //
				6247, 6060);
	}

	// {6247, 6062}
	public void test0372() {
		check(//
				"Integrate[(a + b*ArcCoth[c + d*x])^3/(e + f*x), x]", //
				"-(((a + b*ArcCoth[c + d*x])^3*Log[2/(1 + c + d*x)])/f) + ((a + b*ArcCoth[c + d*x])^3*Log[(2*d*(e + f*x))/((d*e + f - c*f)*(1 + c + d*x))])/f + (3*b*(a + b*ArcCoth[c + d*x])^2*PolyLog[2, 1 - 2/(1 + c + d*x)])/(2*f) - (3*b*(a + b*ArcCoth[c + d*x])^2*PolyLog[2, 1 - (2*d*(e + f*x))/((d*e + f - c*f)*(1 + c + d*x))])/(2*f) + (3*b^2*(a + b*ArcCoth[c + d*x])*PolyLog[3, 1 - 2/(1 + c + d*x)])/(2*f) - (3*b^2*(a + b*ArcCoth[c + d*x])*PolyLog[3, 1 - (2*d*(e + f*x))/((d*e + f - c*f)*(1 + c + d*x))])/(2*f) + (3*b^3*PolyLog[4, 1 - 2/(1 + c + d*x)])/(4*f) - (3*b^3*PolyLog[4, 1 - (2*d*(e + f*x))/((d*e + f - c*f)*(1 + c + d*x))])/(4*f)", //
				6247, 6062);
	}

	// {6813, 6032}
	public void test0373() {
		check(//
				"Integrate[(a + b*ArcCoth[Sqrt[1 - c*x]/Sqrt[1 + c*x]])/(1 - c^2*x^2), x]", //
				"-((a*Log[Sqrt[1 - c*x]/Sqrt[1 + c*x]])/c) - (b*PolyLog[2, -(Sqrt[1 + c*x]/Sqrt[1 - c*x])])/(2*c) + (b*PolyLog[2, Sqrt[1 + c*x]/Sqrt[1 - c*x]])/(2*c)", //
				6813, 6032);
	}

	// {2199, 30}
	public void test0374() {
		check(//
				"Integrate[x^m*ArcCoth[Tanh[a + b*x]], x]", //
				"-((b*x^(2 + m))/(2 + 3*m + m^2)) + (x^(1 + m)*ArcCoth[Tanh[a + b*x]])/(1 + m)", //
				2199, 30);
	}

	// {2199, 30}
	public void test0375() {
		check(//
				"Integrate[x^2*ArcCoth[Tanh[a + b*x]], x]", //
				"-(b*x^4)/12 + (x^3*ArcCoth[Tanh[a + b*x]])/3", //
				2199, 30);
	}

	// {6375, 30}
	public void test0376() {
		check(//
				"Integrate[x*ArcCoth[Tanh[a + b*x]], x]", //
				"-(b*x^3)/6 + (x^2*ArcCoth[Tanh[a + b*x]])/2", //
				6375, 30);
	}

	// {2188, 30}
	public void test0377() {
		check(//
				"Integrate[ArcCoth[Tanh[a + b*x]], x]", //
				"ArcCoth[Tanh[a + b*x]]^2/(2*b)", //
				2188, 30);
	}

	// {2189, 29}
	public void test0378() {
		check(//
				"Integrate[ArcCoth[Tanh[a + b*x]]/x, x]", //
				"b*x - (b*x - ArcCoth[Tanh[a + b*x]])*Log[x]", //
				2189, 29);
	}

	// {2199, 29}
	public void test0379() {
		check(//
				"Integrate[ArcCoth[Tanh[a + b*x]]/x^2, x]", //
				"-(ArcCoth[Tanh[a + b*x]]/x) + b*Log[x]", //
				2199, 29);
	}

	// {2199, 30}
	public void test0380() {
		check(//
				"Integrate[ArcCoth[Tanh[a + b*x]]/x^3, x]", //
				"-b/(2*x) - ArcCoth[Tanh[a + b*x]]/(2*x^2)", //
				2199, 30);
	}

	// {2199, 30}
	public void test0381() {
		check(//
				"Integrate[ArcCoth[Tanh[a + b*x]]/x^4, x]", //
				"-b/(6*x^2) - ArcCoth[Tanh[a + b*x]]/(3*x^3)", //
				2199, 30);
	}

	// {2188, 30}
	public void test0382() {
		check(//
				"Integrate[ArcCoth[Tanh[a + b*x]]^2, x]", //
				"ArcCoth[Tanh[a + b*x]]^3/(3*b)", //
				2188, 30);
	}

	// {2202, 2198}
	public void test0383() {
		check(//
				"Integrate[ArcCoth[Tanh[a + b*x]]^2/x^5, x]", //
				"(b*ArcCoth[Tanh[a + b*x]]^3)/(12*x^3*(b*x - ArcCoth[Tanh[a + b*x]])^2) + ArcCoth[Tanh[a + b*x]]^3/(4*x^4*(b*x - ArcCoth[Tanh[a + b*x]]))", //
				2202, 2198);
	}

	// {2188, 30}
	public void test0384() {
		check(//
				"Integrate[ArcCoth[Tanh[a + b*x]]^3, x]", //
				"ArcCoth[Tanh[a + b*x]]^4/(4*b)", //
				2188, 30);
	}

	// {2202, 2198}
	public void test0385() {
		check(//
				"Integrate[ArcCoth[Tanh[a + b*x]]^3/x^6, x]", //
				"(b*ArcCoth[Tanh[a + b*x]]^4)/(20*x^4*(b*x - ArcCoth[Tanh[a + b*x]])^2) + ArcCoth[Tanh[a + b*x]]^4/(5*x^5*(b*x - ArcCoth[Tanh[a + b*x]]))", //
				2202, 2198);
	}

	// {2188, 29}
	public void test0386() {
		check(//
				"Integrate[ArcCoth[Tanh[a + b*x]]^(-1), x]", //
				"Log[ArcCoth[Tanh[a + b*x]]]/b", //
				2188, 29);
	}

	// {2199, 2195}
	public void test0387() {
		check(//
				"Integrate[x^m/ArcCoth[Tanh[a + b*x]]^2, x]", //
				"-(x^m/(b*ArcCoth[Tanh[a + b*x]])) - (x^m*Hypergeometric2F1[1, m, 1 + m, (b*x)/(b*x - ArcCoth[Tanh[a + b*x]])])/(b*(b*x - ArcCoth[Tanh[a + b*x]]))", //
				2199, 2195);
	}

	// {2188, 30}
	public void test0388() {
		check(//
				"Integrate[ArcCoth[Tanh[a + b*x]]^(-2), x]", //
				"-(1/(b*ArcCoth[Tanh[a + b*x]]))", //
				2188, 30);
	}

	// {2188, 30}
	public void test0389() {
		check(//
				"Integrate[ArcCoth[Tanh[a + b*x]]^(-3), x]", //
				"-1/(2*b*ArcCoth[Tanh[a + b*x]]^2)", //
				2188, 30);
	}

	// {2188, 30}
	public void test0390() {
		check(//
				"Integrate[ArcCoth[Tanh[a + b*x]]^n, x]", //
				"ArcCoth[Tanh[a + b*x]]^(1 + n)/(b*(1 + n))", //
				2188, 30);
	}

	// {2199, 2195}
	public void test0391() {
		check(//
				"Integrate[ArcCoth[Tanh[a + b*x]]^n/x^2, x]", //
				"-(ArcCoth[Tanh[a + b*x]]^n/x) + (b*ArcCoth[Tanh[a + b*x]]^n*Hypergeometric2F1[1, n, 1 + n, -(ArcCoth[Tanh[a + b*x]]/(b*x - ArcCoth[Tanh[a + b*x]]))])/(b*x - ArcCoth[Tanh[a + b*x]])", //
				2199, 2195);
	}

	// {2199, 30}
	public void test0392() {
		check(//
				"Integrate[x^m*ArcCoth[Tanh[a + b*x]], x]", //
				"-((b*x^(2 + m))/(2 + 3*m + m^2)) + (x^(1 + m)*ArcCoth[Tanh[a + b*x]])/(1 + m)", //
				2199, 30);
	}

	// {2199, 30}
	public void test0393() {
		check(//
				"Integrate[x^2*ArcCoth[Coth[a + b*x]], x]", //
				"-(b*x^4)/12 + (x^3*ArcCoth[Coth[a + b*x]])/3", //
				2199, 30);
	}

	// {6377, 30}
	public void test0394() {
		check(//
				"Integrate[x*ArcCoth[Coth[a + b*x]], x]", //
				"-(b*x^3)/6 + (x^2*ArcCoth[Coth[a + b*x]])/2", //
				6377, 30);
	}

	// {2188, 30}
	public void test0395() {
		check(//
				"Integrate[ArcCoth[Coth[a + b*x]], x]", //
				"ArcCoth[Coth[a + b*x]]^2/(2*b)", //
				2188, 30);
	}

	// {2189, 29}
	public void test0396() {
		check(//
				"Integrate[ArcCoth[Coth[a + b*x]]/x, x]", //
				"b*x - (b*x - ArcCoth[Coth[a + b*x]])*Log[x]", //
				2189, 29);
	}

	// {2199, 29}
	public void test0397() {
		check(//
				"Integrate[ArcCoth[Coth[a + b*x]]/x^2, x]", //
				"-(ArcCoth[Coth[a + b*x]]/x) + b*Log[x]", //
				2199, 29);
	}

	// {2199, 30}
	public void test0398() {
		check(//
				"Integrate[ArcCoth[Coth[a + b*x]]/x^3, x]", //
				"-b/(2*x) - ArcCoth[Coth[a + b*x]]/(2*x^2)", //
				2199, 30);
	}

	// {2320, 6032}
	public void test0399() {
		check(//
				"Integrate[ArcCoth[E^x], x]", //
				"PolyLog[2, -E^(-x)]/2 - PolyLog[2, E^(-x)]/2", //
				2320, 6032);
	}

	// {2320, 6032}
	public void test0400() {
		check(//
				"Integrate[ArcCoth[E^(a + b*x)], x]", //
				"PolyLog[2, -E^(-a - b*x)]/(2*b) - PolyLog[2, E^(-a - b*x)]/(2*b)", //
				2320, 6032);
	}

	// {6308, 138}
	public void test0401() {
		check(//
				"Integrate[E^((5*ArcCoth[a*x])/2)*x^m, x]", //
				"(x^(1 + m)*AppellF1[-1 - m, 5/4, -5/4, -m, 1/(a*x), -(1/(a*x))])/(1 + m)", //
				6308, 138);
	}

	// {6308, 138}
	public void test0402() {
		check(//
				"Integrate[E^((3*ArcCoth[a*x])/2)*x^m, x]", //
				"(x^(1 + m)*AppellF1[-1 - m, 3/4, -3/4, -m, 1/(a*x), -(1/(a*x))])/(1 + m)", //
				6308, 138);
	}

	// {6308, 138}
	public void test0403() {
		check(//
				"Integrate[E^(ArcCoth[a*x]/2)*x^m, x]", //
				"(x^(1 + m)*AppellF1[-1 - m, 1/4, -1/4, -m, 1/(a*x), -(1/(a*x))])/(1 + m)", //
				6308, 138);
	}

	// {6308, 138}
	public void test0404() {
		check(//
				"Integrate[x^m/E^(ArcCoth[a*x]/2), x]", //
				"(x^(1 + m)*AppellF1[-1 - m, -1/4, 1/4, -m, 1/(a*x), -(1/(a*x))])/(1 + m)", //
				6308, 138);
	}

	// {6308, 138}
	public void test0405() {
		check(//
				"Integrate[x^m/E^((3*ArcCoth[a*x])/2), x]", //
				"(x^(1 + m)*AppellF1[-1 - m, -3/4, 3/4, -m, 1/(a*x), -(1/(a*x))])/(1 + m)", //
				6308, 138);
	}

	// {6308, 138}
	public void test0406() {
		check(//
				"Integrate[x^m/E^((5*ArcCoth[a*x])/2), x]", //
				"(x^(1 + m)*AppellF1[-1 - m, -5/4, 5/4, -m, 1/(a*x), -(1/(a*x))])/(1 + m)", //
				6308, 138);
	}

	// {6308, 138}
	public void test0407() {
		check(//
				"Integrate[E^((2*ArcCoth[x])/3)*x^m, x]", //
				"(x^(1 + m)*AppellF1[-1 - m, 1/3, -1/3, -m, x^(-1), -x^(-1)])/(1 + m)", //
				6308, 138);
	}

	// {6308, 138}
	public void test0408() {
		check(//
				"Integrate[E^(ArcCoth[x]/3)*x^m, x]", //
				"(x^(1 + m)*AppellF1[-1 - m, 1/6, -1/6, -m, x^(-1), -x^(-1)])/(1 + m)", //
				6308, 138);
	}

	// {6308, 138}
	public void test0409() {
		check(//
				"Integrate[E^(ArcCoth[a*x]/4)*x^m, x]", //
				"(x^(1 + m)*AppellF1[-1 - m, 1/8, -1/8, -m, 1/(a*x), -(1/(a*x))])/(1 + m)", //
				6308, 138);
	}

	// {6308, 138}
	public void test0410() {
		check(//
				"Integrate[E^(n*ArcCoth[a*x])*x^m, x]", //
				"(x^(1 + m)*AppellF1[-1 - m, n/2, -n/2, -m, 1/(a*x), -(1/(a*x))])/(1 + m)", //
				6308, 138);
	}

	// {6305, 133}
	public void test0411() {
		check(//
				"Integrate[E^(n*ArcCoth[a*x]), x]", //
				"(4*(1 - 1/(a*x))^(1 - n/2)*(1 + 1/(a*x))^((-2 + n)/2)*Hypergeometric2F1[2, 1 - n/2, 2 - n/2, (a - x^(-1))/(a + x^(-1))])/(a*(2 - n))", //
				6305, 133);
	}

	// {6306, 71}
	public void test0412() {
		check(//
				"Integrate[E^(n*ArcCoth[a*x])/x^2, x]", //
				"(2^(1 + n/2)*a*(1 - 1/(a*x))^(1 - n/2)*Hypergeometric2F1[1 - n/2, -n/2, 2 - n/2, (a - x^(-1))/(2*a)])/(2 - n)", //
				6306, 71);
	}

	// {6312, 270}
	public void test0413() {
		check(//
				"Integrate[1/(E^ArcCoth[a*x]*(c - c/(a*x))), x]", //
				"(Sqrt[1 - 1/(a^2*x^2)]*x)/c", //
				6312, 270);
	}

	// {6313, 663}
	public void test0414() {
		check(//
				"Integrate[(E^ArcCoth[a*x]*Sqrt[c - c/(a*x)])/x^2, x]", //
				"(-2*a*c^2*(1 - 1/(a^2*x^2))^(3/2))/(3*(c - c/(a*x))^(3/2))", //
				6313, 663);
	}

	// {6320, 6318}
	public void test0415() {
		check(//
				"Integrate[E^ArcCoth[a*x]/(c - a^2*c*x^2)^2, x]", //
				"(2*E^ArcCoth[a*x])/(3*a*c^2) - (E^ArcCoth[a*x]*(1 - 2*a*x))/(3*a*c^2*(1 - a^2*x^2))", //
				6320, 6318);
	}

	// {6320, 6318}
	public void test0416() {
		check(//
				"Integrate[E^(3*ArcCoth[a*x])/(c - a^2*c*x^2)^2, x]", //
				"(-2*E^(3*ArcCoth[a*x]))/(15*a*c^2) + (E^(3*ArcCoth[a*x])*(3 - 2*a*x))/(5*a*c^2*(1 - a^2*x^2))", //
				6320, 6318);
	}

	// {6320, 6318}
	public void test0417() {
		check(//
				"Integrate[1/(E^ArcCoth[a*x]*(c - a^2*c*x^2)^2), x]", //
				"-2/(3*a*c^2*E^ArcCoth[a*x]) + (1 + 2*a*x)/(3*a*c^2*E^ArcCoth[a*x]*(1 - a^2*x^2))", //
				6320, 6318);
	}

	// {6320, 6318}
	public void test0418() {
		check(//
				"Integrate[1/(E^(3*ArcCoth[a*x])*(c - a^2*c*x^2)^2), x]", //
				"2/(15*a*c^2*E^(3*ArcCoth[a*x])) - (3 + 2*a*x)/(5*a*c^2*E^(3*ArcCoth[a*x])*(1 - a^2*x^2))", //
				6320, 6318);
	}

	// {6305, 133}
	public void test0419() {
		check(//
				"Integrate[E^(n*ArcCoth[a*x]), x]", //
				"(4*(1 - 1/(a*x))^(1 - n/2)*(1 + 1/(a*x))^((-2 + n)/2)*Hypergeometric2F1[2, 1 - n/2, 2 - n/2, (a - x^(-1))/(a + x^(-1))])/(a*(2 - n))", //
				6305, 133);
	}

	// {6320, 6318}
	public void test0420() {
		check(//
				"Integrate[E^(n*ArcCoth[a*x])/(c - a^2*c*x^2)^2, x]", //
				"(2*E^(n*ArcCoth[a*x]))/(a*c^2*n*(4 - n^2)) - (E^(n*ArcCoth[a*x])*(n - 2*a*x))/(a*c^2*(4 - n^2)*(1 - a^2*x^2))", //
				6320, 6318);
	}

	// {6320, 6319}
	public void test0421() {
		check(//
				"Integrate[E^(n*ArcCoth[a*x])/(c - a^2*c*x^2)^(5/2), x]", //
				"-((E^(n*ArcCoth[a*x])*(n - 3*a*x))/(a*c*(9 - n^2)*(c - a^2*c*x^2)^(3/2))) - (6*E^(n*ArcCoth[a*x])*(n - a*x))/(a*c^2*(1 - n^2)*(9 - n^2)*Sqrt[c - a^2*c*x^2])", //
				6320, 6319);
	}

	// {6324, 6319}
	public void test0422() {
		check(//
				"Integrate[(E^(n*ArcCoth[a*x])*x^2)/(c - a^2*c*x^2)^(5/2), x]", //
				"-((E^(n*ArcCoth[a*x])*(n - 3*a*x))/(a^3*c*(9 - n^2)*(c - a^2*c*x^2)^(3/2))) + (E^(n*ArcCoth[a*x])*(3 - n^2)*(n - a*x))/(a^3*c^2*(9 - 10*n^2 + n^4)*Sqrt[c - a^2*c*x^2])", //
				6324, 6319);
	}

	// {6322, 6319}
	public void test0423() {
		check(//
				"Integrate[(E^(n*ArcCoth[a*x])*x)/(c - a^2*c*x^2)^(5/2), x]", //
				"(E^(n*ArcCoth[a*x])*(3 - a*n*x))/(a^2*c*(9 - n^2)*(c - a^2*c*x^2)^(3/2)) + (2*E^(n*ArcCoth[a*x])*n*(n - a*x))/(a^2*c^2*(9 - 10*n^2 + n^4)*Sqrt[c - a^2*c*x^2])", //
				6322, 6319);
	}

	// {6320, 6319}
	public void test0424() {
		check(//
				"Integrate[E^(n*ArcCoth[a*x])/(c - a^2*c*x^2)^(5/2), x]", //
				"-((E^(n*ArcCoth[a*x])*(n - 3*a*x))/(a*c*(9 - n^2)*(c - a^2*c*x^2)^(3/2))) - (6*E^(n*ArcCoth[a*x])*(n - a*x))/(a*c^2*(1 - n^2)*(9 - n^2)*Sqrt[c - a^2*c*x^2])", //
				6320, 6319);
	}

	// {6418, 75}
	public void test0425() {
		check(//
				"Integrate[x*(a + b*ArcSech[c*x]), x]", //
				"-(b*Sqrt[1 - c*x])/(2*c^2*Sqrt[(1 + c*x)^(-1)]) + (x^2*(a + b*ArcSech[c*x]))/2", //
				6418, 75);
	}

	// {6418, 97}
	public void test0426() {
		check(//
				"Integrate[(a + b*ArcSech[c*x])/x^2, x]", //
				"(b*Sqrt[1 - c*x])/(x*Sqrt[(1 + c*x)^(-1)]) - (a + b*ArcSech[c*x])/x", //
				6418, 97);
	}

	// {6419, 197}
	public void test0427() {
		check(//
				"Integrate[x*(a + b*ArcCsch[c*x]), x]", //
				"(b*Sqrt[1 + 1/(c^2*x^2)]*x)/(2*c) + (x^2*(a + b*ArcCsch[c*x]))/2", //
				6419, 197);
	}

	// {6419, 267}
	public void test0428() {
		check(//
				"Integrate[(a + b*ArcCsch[c*x])/x^2, x]", //
				"b*c*Sqrt[1 + 1/(c^2*x^2)] - (a + b*ArcCsch[c*x])/x", //
				6419, 267);
	}

}
