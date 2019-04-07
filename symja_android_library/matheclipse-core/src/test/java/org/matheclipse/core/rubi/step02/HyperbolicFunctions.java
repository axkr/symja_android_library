package org.matheclipse.core.rubi.step02;

public class HyperbolicFunctions extends AbstractRubiTestCase {

	static boolean init = true;

	public HyperbolicFunctions(String name) {
		super(name, false);
	}

	@Override
	protected void setUp() {
		try {
			super.setUp();
			if (init) {
				System.out.println("HyperbolicFunctions");
				init = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// {3377, 2717}
	public void test0001() {
		check(//
				"Integrate[(c + d*x)*Sinh[a + b*x], x]", //
				"((c + d*x)*Cosh[a + b*x])/b - (d*Sinh[a + b*x])/b^2", //
				3377, 2717);
	}

	// {3391}
	public void test0002() {
		check(//
				"Integrate[(c + d*x)*Sinh[a + b*x]^2, x]", //
				"-(c*x)/2 - (d*x^2)/4 + ((c + d*x)*Cosh[a + b*x]*Sinh[a + b*x])/(2*b) - (d*Sinh[a + b*x]^2)/(4*b^2)", //
				3391);
	}

	// {4269, 3556}
	public void test0003() {
		check(//
				"Integrate[(c + d*x)*Csch[a + b*x]^2, x]", //
				"-(((c + d*x)*Coth[a + b*x])/b) + (d*Log[Sinh[a + b*x]])/b^2", //
				4269, 3556);
	}

	// {3396}
	public void test0004() {
		check(//
				"Integrate[x/Sinh[x]^(3/2) - x*Sqrt[Sinh[x]], x]", //
				"(-2*x*Cosh[x])/Sqrt[Sinh[x]] + 4*Sqrt[Sinh[x]]", //
				3396);
	}

	// {3396}
	public void test0005() {
		check(//
				"Integrate[x/Sinh[x]^(5/2) + x/(3*Sqrt[Sinh[x]]), x]", //
				"(-2*x*Cosh[x])/(3*Sinh[x]^(3/2)) - 4/(3*Sqrt[Sinh[x]])", //
				3396);
	}

	// {2814, 2727}
	public void test0006() {
		check(//
				"Integrate[Sinh[c + d*x]/(a + I*a*Sinh[c + d*x]), x]", //
				"((-I)*x)/a - Cosh[c + d*x]/(d*(a + I*a*Sinh[c + d*x]))", //
				2814, 2727);
	}

	// {2846, 2813}
	public void test0007() {
		check(//
				"Integrate[Sinh[c + d*x]^3/(a + I*a*Sinh[c + d*x]), x]", //
				"(((3*I)/2)*x)/a + (2*Cosh[c + d*x])/(a*d) - (((3*I)/2)*Cosh[c + d*x]*Sinh[c + d*x])/(a*d) - (Cosh[c + d*x]*Sinh[c + d*x]^2)/(d*(a + I*a*Sinh[c + d*x]))", //
				2846, 2813);
	}

	// {2746, 31}
	public void test0008() {
		check(//
				"Integrate[Cosh[c + d*x]/(a + I*a*Sinh[c + d*x]), x]", //
				"((-I)*Log[I - Sinh[c + d*x]])/(a*d)", //
				2746, 31);
	}

	// {2761, 8}
	public void test0009() {
		check(//
				"Integrate[Cosh[c + d*x]^2/(a + I*a*Sinh[c + d*x]), x]", //
				"x/a - (I*Cosh[c + d*x])/(a*d)", //
				2761, 8);
	}

	// {2746}
	public void test0010() {
		check(//
				"Integrate[Cosh[c + d*x]^3/(a + I*a*Sinh[c + d*x]), x]", //
				"Sinh[c + d*x]/(a*d) - ((I/2)*Sinh[c + d*x]^2)/(a*d)", //
				2746);
	}

	// {2747, 31}
	public void test0011() {
		check(//
				"Integrate[Cosh[c + d*x]/(a + b*Sinh[c + d*x]), x]", //
				"Log[a + b*Sinh[c + d*x]]/(b*d)", //
				2747, 31);
	}

	// {5428, 2718}
	public void test0012() {
		check(//
				"Integrate[x*Sinh[a + b*x^2], x]", //
				"Cosh[a + b*x^2]/(2*b)", //
				5428, 2718);
	}

	// {5428, 2718}
	public void test0013() {
		check(//
				"Integrate[x^3*Sinh[a + b*x^4], x]", //
				"Cosh[a + b*x^4]/(4*b)", //
				5428, 2718);
	}

	// {5428, 2718}
	public void test0014() {
		check(//
				"Integrate[Sinh[a + b/x]/x^2, x]", //
				"-(Cosh[a + b/x]/b)", //
				5428, 2718);
	}

	// {5428, 2718}
	public void test0015() {
		check(//
				"Integrate[Sinh[a + b/x^2]/x^3, x]", //
				"-Cosh[a + b/x^2]/(2*b)", //
				5428, 2718);
	}

	// {5428, 2718}
	public void test0016() {
		check(//
				"Integrate[Sinh[Sqrt[x]]/Sqrt[x], x]", //
				"2*Cosh[Sqrt[x]]", //
				5428, 2718);
	}

	// {2715, 8}
	public void test0021() {
		check(//
				"Integrate[Sinh[a + b*x]^2, x]", //
				"-x/2 + (Cosh[a + b*x]*Sinh[a + b*x])/(2*b)", //
				2715, 8);
	}

	// {2713}
	public void test0022() {
		check(//
				"Integrate[Sinh[a + b*x]^3, x]", //
				"-(Cosh[a + b*x]/b) + Cosh[a + b*x]^3/(3*b)", //
				2713);
	}

	// {2713}
	public void test0023() {
		check(//
				"Integrate[Sinh[a + b*x]^5, x]", //
				"Cosh[a + b*x]/b - (2*Cosh[a + b*x]^3)/(3*b) + Cosh[a + b*x]^5/(5*b)", //
				2713);
	}

	// {2721, 2719}
	public void test0024() {
		check(//
				"Integrate[Sqrt[Sinh[a + b*x]], x]", //
				"((-2*I)*EllipticE[(I*a - Pi/2 + I*b*x)/2, 2]*Sqrt[Sinh[a + b*x]])/(b*Sqrt[I*Sinh[a + b*x]])", //
				2721, 2719);
	}

	// {2721, 2720}
	public void test0025() {
		check(//
				"Integrate[1/Sqrt[Sinh[a + b*x]], x]", //
				"((-2*I)*EllipticF[(I*a - Pi/2 + I*b*x)/2, 2]*Sqrt[I*Sinh[a + b*x]])/(b*Sqrt[Sinh[a + b*x]])", //
				2721, 2720);
	}

	// {2721, 2719}
	public void test0026() {
		check(//
				"Integrate[Sqrt[b*Sinh[c + d*x]], x]", //
				"((-2*I)*EllipticE[(I*c - Pi/2 + I*d*x)/2, 2]*Sqrt[b*Sinh[c + d*x]])/(d*Sqrt[I*Sinh[c + d*x]])", //
				2721, 2719);
	}

	// {2721, 2720}
	public void test0027() {
		check(//
				"Integrate[1/Sqrt[b*Sinh[c + d*x]], x]", //
				"((-2*I)*EllipticF[(I*c - Pi/2 + I*d*x)/2, 2]*Sqrt[I*Sinh[c + d*x]])/(d*Sqrt[b*Sinh[c + d*x]])", //
				2721, 2720);
	}

	// {2715, 2719}
	public void test0028() {
		check(//
				"Integrate[(I*Sinh[c + d*x])^(5/2), x]", //
				"(((-6*I)/5)*EllipticE[(I*c - Pi/2 + I*d*x)/2, 2])/d + (((2*I)/5)*Cosh[c + d*x]*(I*Sinh[c + d*x])^(3/2))/d", //
				2715, 2719);
	}

	// {2715, 2720}
	public void test0029() {
		check(//
				"Integrate[(I*Sinh[c + d*x])^(3/2), x]", //
				"(((-2*I)/3)*EllipticF[(I*c - Pi/2 + I*d*x)/2, 2])/d + (((2*I)/3)*Cosh[c + d*x]*Sqrt[I*Sinh[c + d*x]])/d", //
				2715, 2720);
	}

	// {2716, 2719}
	public void test0030() {
		check(//
				"Integrate[(I*Sinh[c + d*x])^(-3/2), x]", //
				"((2*I)*EllipticE[(I*c - Pi/2 + I*d*x)/2, 2])/d + ((2*I)*Cosh[c + d*x])/(d*Sqrt[I*Sinh[c + d*x]])", //
				2716, 2719);
	}

	// {2716, 2720}
	public void test0031() {
		check(//
				"Integrate[(I*Sinh[c + d*x])^(-5/2), x]", //
				"(((-2*I)/3)*EllipticF[(I*c - Pi/2 + I*d*x)/2, 2])/d + (((2*I)/3)*Cosh[c + d*x])/(d*(I*Sinh[c + d*x])^(3/2))", //
				2716, 2720);
	}

	// {2846, 2813}
	public void test0032() {
		check(//
				"Integrate[Sinh[x]^3/(I + Sinh[x]), x]", //
				"(-3*x)/2 - (2*I)*Cosh[x] + (3*Cosh[x]*Sinh[x])/2 - (Cosh[x]*Sinh[x]^2)/(I + Sinh[x])", //
				2846, 2813);
	}

	// {2814, 2727}
	public void test0033() {
		check(//
				"Integrate[Sinh[x]/(I + Sinh[x]), x]", //
				"x - Cosh[x]/(I + Sinh[x])", //
				2814, 2727);
	}

	// {2829, 2727}
	public void test0034() {
		check(//
				"Integrate[Sinh[x]/(I + Sinh[x])^2, x]", //
				"-Cosh[x]/(3*(I + Sinh[x])^2) - (((2*I)/3)*Cosh[x])/(I + Sinh[x])", //
				2829, 2727);
	}

	// {2729, 2727}
	public void test0035() {
		check(//
				"Integrate[(1 + I*Sinh[c + d*x])^(-2), x]", //
				"((I/3)*Cosh[c + d*x])/(d*(1 + I*Sinh[c + d*x])^2) + ((I/3)*Cosh[c + d*x])/(d*(1 + I*Sinh[c + d*x]))", //
				2729, 2727);
	}

	// {2729, 2727}
	public void test0036() {
		check(//
				"Integrate[(1 - I*Sinh[c + d*x])^(-2), x]", //
				"((-I/3)*Cosh[c + d*x])/(d*(1 - I*Sinh[c + d*x])^2) - ((I/3)*Cosh[c + d*x])/(d*(1 - I*Sinh[c + d*x]))", //
				2729, 2727);
	}

	// {2726, 2725}
	public void test0037() {
		check(//
				"Integrate[(a + I*a*Sinh[c + d*x])^(3/2), x]", //
				"(((8*I)/3)*a^2*Cosh[c + d*x])/(d*Sqrt[a + I*a*Sinh[c + d*x]]) + (((2*I)/3)*a*Cosh[c + d*x]*Sqrt[a + I*a*Sinh[c + d*x]])/d", //
				2726, 2725);
	}

	// {2728, 212}
	public void test0038() {
		check(//
				"Integrate[1/Sqrt[a + I*a*Sinh[c + d*x]], x]", //
				"(I*Sqrt[2]*ArcTanh[(Sqrt[a]*Cosh[c + d*x])/(Sqrt[2]*Sqrt[a + I*a*Sinh[c + d*x]])])/(Sqrt[a]*d)", //
				2728, 212);
	}

	// {2735, 2813}
	public void test0039() {
		check(//
				"Integrate[(a + b*Sinh[c + d*x])^3, x]", //
				"(a*(2*a^2 - 3*b^2)*x)/2 + (2*b*(4*a^2 - b^2)*Cosh[c + d*x])/(3*d) + (5*a*b^2*Cosh[c + d*x]*Sinh[c + d*x])/(6*d) + (b*Cosh[c + d*x]*(a + b*Sinh[c + d*x])^2)/(3*d)", //
				2735, 2813);
	}

	// {2718}
	public void test0040() {
		check(//
				"Integrate[a + b*Sinh[c + d*x], x]", //
				"a*x + (b*Cosh[c + d*x])/d", //
				2718);
	}

	// {2734, 2732}
	public void test0041() {
		fSeconds=20;
		check(//
				"Integrate[Sqrt[a + b*Sinh[x]], x]", //
				"((2*I)*EllipticE[Pi/4 - (I/2)*x, (2*b)/(I*a + b)]*Sqrt[a + b*Sinh[x]])/Sqrt[(a + b*Sinh[x])/(a - I*b)]", //
				2734, 2732);
	}

	// {2742, 2740}
	public void test0042() {
		check(//
				"Integrate[1/Sqrt[a + b*Sinh[x]], x]", //
				"((2*I)*EllipticF[Pi/4 - (I/2)*x, (2*b)/(I*a + b)]*Sqrt[(a + b*Sinh[x])/(a - I*b)])/Sqrt[a + b*Sinh[x]]", //
				2742, 2740);
	}

	// {2830, 2725}
	public void test0043() {
		check(//
				"Integrate[Sqrt[a + I*a*Sinh[x]]*(A + B*Sinh[x]), x]", //
				"(2*a*((3*I)*A + B)*Cosh[x])/(3*Sqrt[a + I*a*Sinh[x]]) + (2*B*Cosh[x]*Sqrt[a + I*a*Sinh[x]])/3", //
				2830, 2725);
	}

	// {2814, 2727}
	public void test0044() {
		check(//
				"Integrate[(A + B*Sinh[x])/(I + Sinh[x]), x]", //
				"B*x - ((I*A + B)*Cosh[x])/(I + Sinh[x])", //
				2814, 2727);
	}

	// {2829, 2727}
	public void test0045() {
		check(//
				"Integrate[(A + B*Sinh[x])/(I + Sinh[x])^2, x]", //
				"-((I*A + B)*Cosh[x])/(3*(I + Sinh[x])^2) - ((A + (2*I)*B)*Cosh[x])/(3*(I + Sinh[x]))", //
				2829, 2727);
	}

	// {2814, 2727}
	public void test0046() {
		check(//
				"Integrate[(A + B*Sinh[x])/(I - Sinh[x]), x]", //
				"-(B*x) + ((I*A - B)*Cosh[x])/(I - Sinh[x])", //
				2814, 2727);
	}

	// {2829, 2727}
	public void test0047() {
		check(//
				"Integrate[(A + B*Sinh[x])/(I - Sinh[x])^2, x]", //
				"((I*A - B)*Cosh[x])/(3*(I - Sinh[x])^2) + ((A - (2*I)*B)*Cosh[x])/(3*(I - Sinh[x]))", //
				2829, 2727);
	}

	// {21, 8}
	public void test0048() {
		check(//
				"Integrate[((a*B)/b + B*Sinh[x])/(a + b*Sinh[x]), x]", //
				"(B*x)/b", //
				21, 8);
	}

	// {2833, 8}
	public void test0049() {
		check(//
				"Integrate[(a - b*Sinh[x])/(b + a*Sinh[x])^2, x]", //
				"-(Cosh[x]/(b + a*Sinh[x]))", //
				2833, 8);
	}

	// {2814, 2736}
	public void test0050() {
		check(//
				"Integrate[(2 - Sinh[x])/(2 + Sinh[x]), x]", //
				"-x + (4*x)/Sqrt[5] - (8*ArcTanh[Cosh[x]/(2 + Sqrt[5] + Sinh[x])])/Sqrt[5]", //
				2814, 2736);
	}

	// {3286, 2718}
	public void test0051() {
		check(//
				"Integrate[Sqrt[a*Sinh[x]^2], x]", //
				"Coth[x]*Sqrt[a*Sinh[x]^2]", //
				3286, 2718);
	}

	// {3286, 3855}
	public void test0052() {
		check(//
				"Integrate[1/Sqrt[a*Sinh[x]^2], x]", //
				"-((ArcTanh[Cosh[x]]*Sinh[x])/Sqrt[a*Sinh[x]^2])", //
				3286, 3855);
	}

	// {2746}
	public void test0053() {
		check(//
				"Integrate[Cosh[x]^3/(I + Sinh[x]), x]", //
				"(-I)*Sinh[x] + Sinh[x]^2/2", //
				2746);
	}

	// {2761, 8}
	public void test0054() {
		check(//
				"Integrate[Cosh[x]^2/(I + Sinh[x]), x]", //
				"(-I)*x + Cosh[x]", //
				2761, 8);
	}

	// {2746, 31}
	public void test0055() {
		check(//
				"Integrate[Cosh[x]/(I + Sinh[x]), x]", //
				"Log[I + Sinh[x]]", //
				2746, 31);
	}

	// {2746, 32}
	public void test0056() {
		check(//
				"Integrate[Cosh[x]^5/(I + Sinh[x])^2, x]", //
				"-(I - Sinh[x])^3/3", //
				2746, 32);
	}

	// {2759, 8}
	public void test0057() {
		check(//
				"Integrate[Cosh[x]^2/(I + Sinh[x])^2, x]", //
				"x - (2*Cosh[x])/(I + Sinh[x])", //
				2759, 8);
	}

	// {2746, 32}
	public void test0058() {
		check(//
				"Integrate[Cosh[x]/(I + Sinh[x])^2, x]", //
				"-(I + Sinh[x])^(-1)", //
				2746, 32);
	}

	// {2746, 32}
	public void test0059() {
		check(//
				"Integrate[Cosh[x]/(1 + I*Sinh[x])^3, x]", //
				"(I/2)/(1 + I*Sinh[x])^2", //
				2746, 32);
	}

	// {2746, 32}
	public void test0060() {
		check(//
				"Integrate[Cosh[x]/(1 - I*Sinh[x])^3, x]", //
				"(-I/2)/(1 - I*Sinh[x])^2", //
				2746, 32);
	}

	// {2747, 31}
	public void test0061() {
		check(//
				"Integrate[Cosh[x]/(a + b*Sinh[x]), x]", //
				"Log[a + b*Sinh[x]]/b", //
				2747, 31);
	}

	// {2747, 32}
	public void test0062() {
		check(//
				"Integrate[Cosh[x]/(a + b*Sinh[x])^2, x]", //
				"-(1/(b*(a + b*Sinh[x])))", //
				2747, 32);
	}

	// {6813, 3379}
	public void test0063() {
		check(//
				"Integrate[Sinh[Sqrt[1 - a*x]/Sqrt[1 + a*x]]/(1 - a^2*x^2), x]", //
				"-(SinhIntegral[Sqrt[1 - a*x]/Sqrt[1 + a*x]]/a)", //
				6813, 3379);
	}

	// {5630, 8}
	public void test0064() {
		check(//
				"Integrate[Sinh[a + b*Log[c*x^n]]^2, x]", //
				"(2*b^2*n^2*x)/(1 - 4*b^2*n^2) - (2*b*n*x*Cosh[a + b*Log[c*x^n]]*Sinh[a + b*Log[c*x^n]])/(1 - 4*b^2*n^2) + (x*Sinh[a + b*Log[c*x^n]]^2)/(1 - 4*b^2*n^2)", //
				5630, 8);
	}

	// {5630, 5628}
	public void test0065() {
		check(//
				"Integrate[Sinh[a + b*Log[c*x^n]]^3, x]", //
				"(-6*b^3*n^3*x*Cosh[a + b*Log[c*x^n]])/(1 - 10*b^2*n^2 + 9*b^4*n^4) + (6*b^2*n^2*x*Sinh[a + b*Log[c*x^n]])/(1 - 10*b^2*n^2 + 9*b^4*n^4) - (3*b*n*x*Cosh[a + b*Log[c*x^n]]*Sinh[a + b*Log[c*x^n]]^2)/(1 - 9*b^2*n^2) + (x*Sinh[a + b*Log[c*x^n]]^3)/(1 - 9*b^2*n^2)", //
				5630, 5628);
	}

	// {5640, 30}
	public void test0066() {
		check(//
				"Integrate[x^m*Sinh[a + b*Log[c*x^n]]^2, x]", //
				"(2*b^2*n^2*x^(1 + m))/((1 + m)*((1 + m)^2 - 4*b^2*n^2)) - (2*b*n*x^(1 + m)*Cosh[a + b*Log[c*x^n]]*Sinh[a + b*Log[c*x^n]])/((1 + m)^2 - 4*b^2*n^2) + ((1 + m)*x^(1 + m)*Sinh[a + b*Log[c*x^n]]^2)/((1 + m)^2 - 4*b^2*n^2)", //
				5640, 30);
	}

	// {5640, 5638}
	public void test0067() {
		check(//
				"Integrate[x^m*Sinh[a + b*Log[c*x^n]]^3, x]", //
				"(-6*b^3*n^3*x^(1 + m)*Cosh[a + b*Log[c*x^n]])/((1 + m)^4 - 10*b^2*(1 + m)^2*n^2 + 9*b^4*n^4) + (6*b^2*(1 + m)*n^2*x^(1 + m)*Sinh[a + b*Log[c*x^n]])/((1 + m)^4 - 10*b^2*(1 + m)^2*n^2 + 9*b^4*n^4) - (3*b*n*x^(1 + m)*Cosh[a + b*Log[c*x^n]]*Sinh[a + b*Log[c*x^n]]^2)/((1 + m)^2 - 9*b^2*n^2) + ((1 + m)*x^(1 + m)*Sinh[a + b*Log[c*x^n]]^3)/((1 + m)^2 - 9*b^2*n^2)", //
				5640, 5638);
	}

	// {2718}
	public void test0068() {
		check(//
				"Integrate[Sinh[a + b*Log[c*x^n]]/x, x]", //
				"Cosh[a + b*Log[c*x^n]]/(b*n)", //
				2718);
	}

	// {5584, 5582}
	public void test0069() {
		check(//
				"Integrate[F^(c*(a + b*x))*Sinh[d + e*x]^3, x]", //
				"(-6*e^3*F^(c*(a + b*x))*Cosh[d + e*x])/(9*e^4 - 10*b^2*c^2*e^2*Log[F]^2 + b^4*c^4*Log[F]^4) + (6*b*c*e^2*F^(c*(a + b*x))*Log[F]*Sinh[d + e*x])/(9*e^4 - 10*b^2*c^2*e^2*Log[F]^2 + b^4*c^4*Log[F]^4) + (3*e*F^(c*(a + b*x))*Cosh[d + e*x]*Sinh[d + e*x]^2)/(9*e^2 - b^2*c^2*Log[F]^2) - (b*c*F^(c*(a + b*x))*Log[F]*Sinh[d + e*x]^3)/(9*e^2 - b^2*c^2*Log[F]^2)", //
				5584, 5582);
	}

	// {5584, 2225}
	public void test0070() {
		check(//
				"Integrate[F^(c*(a + b*x))*Sinh[d + e*x]^2, x]", //
				"(-2*e^2*F^(c*(a + b*x)))/(b*c*Log[F]*(4*e^2 - b^2*c^2*Log[F]^2)) + (2*e*F^(c*(a + b*x))*Cosh[d + e*x]*Sinh[d + e*x])/(4*e^2 - b^2*c^2*Log[F]^2) - (b*c*F^(c*(a + b*x))*Log[F]*Sinh[d + e*x]^2)/(4*e^2 - b^2*c^2*Log[F]^2)", //
				5584, 2225);
	}

	// {5599, 5601}
	public void test0071() {
		check(//
				"Integrate[F^(c*(a + b*x))*Csch[d + e*x]^3, x]", //
				"-(F^(c*(a + b*x))*Coth[d + e*x]*Csch[d + e*x])/(2*e) - (b*c*F^(c*(a + b*x))*Csch[d + e*x]*Log[F])/(2*e^2) + (E^(d + e*x)*F^(c*(a + b*x))*Hypergeometric2F1[1, (e + b*c*Log[F])/(2*e), (3 + (b*c*Log[F])/e)/2, E^(2*(d + e*x))]*(e - b*c*Log[F]))/e^2", //
				5599, 5601);
	}

	// {5599, 5601}
	public void test0072() {
		check(//
				"Integrate[F^(c*(a + b*x))*Csch[d + e*x]^4, x]", //
				"-(F^(c*(a + b*x))*Coth[d + e*x]*Csch[d + e*x]^2)/(3*e) - (b*c*F^(c*(a + b*x))*Csch[d + e*x]^2*Log[F])/(6*e^2) - (2*E^(2*(d + e*x))*F^(c*(a + b*x))*Hypergeometric2F1[2, 1 + (b*c*Log[F])/(2*e), 2 + (b*c*Log[F])/(2*e), E^(2*(d + e*x))]*(2*e - b*c*Log[F]))/(3*e^2)", //
				5599, 5601);
	}

	// {3092}
	public void test0073() {
		check(//
				"Integrate[Sinh[c + d*x]*(a + b*Sinh[c + d*x]^2), x]", //
				"((a - b)*Cosh[c + d*x])/d + (b*Cosh[c + d*x]^3)/(3*d)", //
				3092);
	}

	// {3093, 3855}
	public void test0074() {
		check(//
				"Integrate[Csch[c + d*x]*(a + b*Sinh[c + d*x]^2), x]", //
				"-((a*ArcTanh[Cosh[c + d*x]])/d) + (b*Cosh[c + d*x])/d", //
				3093, 3855);
	}

	// {3091, 8}
	public void test0075() {
		check(//
				"Integrate[Csch[c + d*x]^2*(a + b*Sinh[c + d*x]^2), x]", //
				"b*x - (a*Coth[c + d*x])/d", //
				3091, 8);
	}

	// {3091, 3855}
	public void test0076() {
		check(//
				"Integrate[Csch[c + d*x]^3*(a + b*Sinh[c + d*x]^2), x]", //
				"((a - 2*b)*ArcTanh[Cosh[c + d*x]])/(2*d) - (a*Coth[c + d*x]*Csch[c + d*x])/(2*d)", //
				3091, 3855);
	}

	// {3259, 3248}
	public void test0077() {
		check(//
				"Integrate[(a + b*Sinh[c + d*x]^2)^3, x]", //
				"((2*a - b)*(8*a^2 - 8*a*b + 5*b^2)*x)/16 + (b*(64*a^2 - 54*a*b + 15*b^2)*Cosh[c + d*x]*Sinh[c + d*x])/(48*d) + (5*(2*a - b)*b^2*Cosh[c + d*x]*Sinh[c + d*x]^3)/(24*d) + (b*Cosh[c + d*x]*Sinh[c + d*x]*(a + b*Sinh[c + d*x]^2)^2)/(6*d)", //
				3259, 3248);
	}

	// {3265, 211}
	public void test0078() {
		check(//
				"Integrate[Sinh[c + d*x]/(a + b*Sinh[c + d*x]^2), x]", //
				"ArcTan[(Sqrt[b]*Cosh[c + d*x])/Sqrt[a - b]]/(Sqrt[a - b]*Sqrt[b]*d)", //
				3265, 211);
	}

	// {3260, 214}
	public void test0079() {
		check(//
				"Integrate[(a + b*Sinh[c + d*x]^2)^(-1), x]", //
				"ArcTanh[(Sqrt[a - b]*Tanh[c + d*x])/Sqrt[a]]/(Sqrt[a]*Sqrt[a - b]*d)", //
				3260, 214);
	}

	// {3260, 212}
	public void test0080() {
		check(//
				"Integrate[(1 - Sinh[x]^2)^(-1), x]", //
				"ArcTanh[Sqrt[2]*Tanh[x]]/Sqrt[2]", //
				3260, 212);
	}

	// {3257, 3256}
	public void test0081() {
		check(//
				"Integrate[Sqrt[a + b*Sinh[e + f*x]^2], x]", //
				"((-I)*EllipticE[I*e + I*f*x, b/a]*Sqrt[a + b*Sinh[e + f*x]^2])/(f*Sqrt[1 + (b*Sinh[e + f*x]^2)/a])", //
				3257, 3256);
	}

	// {3257, 3256}
	public void test0082() {
		check(//
				"Integrate[Sqrt[-1 + Sinh[x]^2], x]", //
				"((-I)*EllipticE[I*x, -1]*Sqrt[-1 + Sinh[x]^2])/Sqrt[1 - Sinh[x]^2]", //
				3257, 3256);
	}

	// {3257, 3256}
	public void test0083() {
		check(//
				"Integrate[Sqrt[a + b*Sinh[x]^2], x]", //
				"((-I)*EllipticE[I*x, b/a]*Sqrt[a + b*Sinh[x]^2])/Sqrt[1 + (b*Sinh[x]^2)/a]", //
				3257, 3256);
	}

	// {3262, 3261}
	public void test0084() {
		check(//
				"Integrate[1/Sqrt[a + b*Sinh[e + f*x]^2], x]", //
				"((-I)*EllipticF[I*e + I*f*x, b/a]*Sqrt[1 + (b*Sinh[e + f*x]^2)/a])/(f*Sqrt[a + b*Sinh[e + f*x]^2])", //
				3262, 3261);
	}

	// {3265, 197}
	public void test0085() {
		check(//
				"Integrate[Sinh[e + f*x]/(a + b*Sinh[e + f*x]^2)^(3/2), x]", //
				"Cosh[e + f*x]/((a - b)*f*Sqrt[a - b + b*Cosh[e + f*x]^2])", //
				3265, 197);
	}

	// {3262, 3261}
	public void test0086() {
		check(//
				"Integrate[1/Sqrt[-1 + Sinh[x]^2], x]", //
				"((-I)*EllipticF[I*x, -1]*Sqrt[1 - Sinh[x]^2])/Sqrt[-1 + Sinh[x]^2]", //
				3262, 3261);
	}

	// {3262, 3261}
	public void test0087() {
		check(//
				"Integrate[1/Sqrt[a + b*Sinh[x]^2], x]", //
				"((-I)*EllipticF[I*x, b/a]*Sqrt[1 + (b*Sinh[x]^2)/a])/Sqrt[a + b*Sinh[x]^2]", //
				3262, 3261);
	}

	// {3294}
	public void test0088() {
		check(//
				"Integrate[Sinh[c + d*x]*(a + b*Sinh[c + d*x]^4), x]", //
				"((a + b)*Cosh[c + d*x])/d - (2*b*Cosh[c + d*x]^3)/(3*d) + (b*Cosh[c + d*x]^5)/(5*d)", //
				3294);
	}

	// {3254, 2717}
	public void test0089() {
		check(//
				"Integrate[Cosh[x]^3/(a + a*Sinh[x]^2), x]", //
				"Sinh[x]/a", //
				3254, 2717);
	}

	// {3254, 8}
	public void test0090() {
		check(//
				"Integrate[Cosh[x]^2/(a + a*Sinh[x]^2), x]", //
				"x/a", //
				3254, 8);
	}

	// {3254, 3855}
	public void test0091() {
		check(//
				"Integrate[Cosh[x]/(a + a*Sinh[x]^2), x]", //
				"ArcTan[Sinh[x]]/a", //
				3254, 3855);
	}

	// {3269}
	public void test0092() {
		check(//
				"Integrate[Cosh[c + d*x]*(a + b*Sinh[c + d*x]^2), x]", //
				"(a*Sinh[c + d*x])/d + (b*Sinh[c + d*x]^3)/(3*d)", //
				3269);
	}

	// {3270}
	public void test0093() {
		check(//
				"Integrate[Sech[c + d*x]^4*(a + b*Sinh[c + d*x]^2), x]", //
				"(a*Tanh[c + d*x])/d - ((a - b)*Tanh[c + d*x]^3)/(3*d)", //
				3270);
	}

	// {3269, 211}
	public void test0094() {
		check(//
				"Integrate[Cosh[c + d*x]/(a + b*Sinh[c + d*x]^2), x]", //
				"ArcTan[(Sqrt[b]*Sinh[c + d*x])/Sqrt[a]]/(Sqrt[a]*Sqrt[b]*d)", //
				3269, 211);
	}

	// {3257, 3256}
	public void test0095() {
		check(//
				"Integrate[Sqrt[a + b*Sinh[e + f*x]^2], x]", //
				"((-I)*EllipticE[I*e + I*f*x, b/a]*Sqrt[a + b*Sinh[e + f*x]^2])/(f*Sqrt[1 + (b*Sinh[e + f*x]^2)/a])", //
				3257, 3256);
	}

	// {3271, 422}
	public void test0096() {
		check(//
				"Integrate[Sech[e + f*x]^2*Sqrt[a + b*Sinh[e + f*x]^2], x]", //
				"(EllipticE[ArcTan[Sinh[e + f*x]], 1 - b/a]*Sech[e + f*x]*Sqrt[a + b*Sinh[e + f*x]^2])/(f*Sqrt[(Sech[e + f*x]^2*(a + b*Sinh[e + f*x]^2))/a])", //
				3271, 422);
	}

	// {3262, 3261}
	public void test0097() {
		check(//
				"Integrate[1/Sqrt[a + b*Sinh[e + f*x]^2], x]", //
				"((-I)*EllipticF[I*e + I*f*x, b/a]*Sqrt[1 + (b*Sinh[e + f*x]^2)/a])/(f*Sqrt[a + b*Sinh[e + f*x]^2])", //
				3262, 3261);
	}

	// {3269, 197}
	public void test0098() {
		check(//
				"Integrate[Cosh[e + f*x]/(a + b*Sinh[e + f*x]^2)^(3/2), x]", //
				"Sinh[e + f*x]/(a*f*Sqrt[a + b*Sinh[e + f*x]^2])", //
				3269, 197);
	}

	// {3271, 422}
	public void test0099() {
		check(//
				"Integrate[Cosh[e + f*x]^2/(a + b*Sinh[e + f*x]^2)^(3/2), x]", //
				"(Cosh[e + f*x]*EllipticE[ArcTan[(Sqrt[b]*Sinh[e + f*x])/Sqrt[a]], 1 - a/b])/(Sqrt[a]*Sqrt[b]*f*Sqrt[(a*Cosh[e + f*x]^2)/(a + b*Sinh[e + f*x]^2)]*Sqrt[a + b*Sinh[e + f*x]^2])", //
				3271, 422);
	}

	// {3302, 251}
	public void test0100() {
		check(//
				"Integrate[Cosh[c + d*x]/(a + b*Sinh[c + d*x]^n), x]", //
				"(Hypergeometric2F1[1, n^(-1), 1 + n^(-1), -((b*Sinh[c + d*x]^n)/a)]*Sinh[c + d*x])/(a*d)", //
				3302, 251);
	}

	// {3302, 251}
	public void test0101() {
		check(//
				"Integrate[Cosh[c + d*x]/(a + b*Sinh[c + d*x]^n)^2, x]", //
				"(Hypergeometric2F1[2, n^(-1), 1 + n^(-1), -((b*Sinh[c + d*x]^n)/a)]*Sinh[c + d*x])/(a^2*d)", //
				3302, 251);
	}

	// {3257, 3256}
	public void test0102() {
		check(//
				"Integrate[Sqrt[a + b*Sinh[e + f*x]^2], x]", //
				"((-I)*EllipticE[I*e + I*f*x, b/a]*Sqrt[a + b*Sinh[e + f*x]^2])/(f*Sqrt[1 + (b*Sinh[e + f*x]^2)/a])", //
				3257, 3256);
	}

	// {3262, 3261}
	public void test0103() {
		check(//
				"Integrate[1/Sqrt[a + b*Sinh[e + f*x]^2], x]", //
				"((-I)*EllipticF[I*e + I*f*x, b/a]*Sqrt[1 + (b*Sinh[e + f*x]^2)/a])/(f*Sqrt[a + b*Sinh[e + f*x]^2])", //
				3262, 3261);
	}

	// {3273, 70}
	public void test0104() {
		check(//
				"Integrate[(a + b*Sinh[c + d*x]^2)^p*Tanh[c + d*x], x]", //
				"-(Hypergeometric2F1[1, 1 + p, 2 + p, (a + b*Sinh[c + d*x]^2)/(a - b)]*(a + b*Sinh[c + d*x]^2)^(1 + p))/(2*(a - b)*d*(1 + p))", //
				3273, 70);
	}

	// {3273, 67}
	public void test0105() {
		check(//
				"Integrate[Coth[c + d*x]*(a + b*Sinh[c + d*x]^2)^p, x]", //
				"-(Hypergeometric2F1[1, 1 + p, 2 + p, 1 + (b*Sinh[c + d*x]^2)/a]*(a + b*Sinh[c + d*x]^2)^(1 + p))/(2*a*d*(1 + p))", //
				3273, 67);
	}

	// {3377, 2718}
	public void test0106() {
		check(//
				"Integrate[(c + d*x)*Cosh[a + b*x], x]", //
				"-((d*Cosh[a + b*x])/b^2) + ((c + d*x)*Sinh[a + b*x])/b", //
				3377, 2718);
	}

	// {3391}
	public void test0107() {
		check(//
				"Integrate[(c + d*x)*Cosh[a + b*x]^2, x]", //
				"(c*x)/2 + (d*x^2)/4 - (d*Cosh[a + b*x]^2)/(4*b^2) + ((c + d*x)*Cosh[a + b*x]*Sinh[a + b*x])/(2*b)", //
				3391);
	}

	// {4269, 3556}
	public void test0108() {
		check(//
				"Integrate[(c + d*x)*Sech[a + b*x]^2, x]", //
				"-((d*Log[Cosh[a + b*x]])/b^2) + ((c + d*x)*Tanh[a + b*x])/b", //
				4269, 3556);
	}

	// {3396}
	public void test0109() {
		check(//
				"Integrate[x/Cosh[x]^(3/2) + x*Sqrt[Cosh[x]], x]", //
				"-4*Sqrt[Cosh[x]] + (2*x*Sinh[x])/Sqrt[Cosh[x]]", //
				3396);
	}

	// {3396}
	public void test0110() {
		check(//
				"Integrate[x/Cosh[x]^(5/2) - x/(3*Sqrt[Cosh[x]]), x]", //
				"4/(3*Sqrt[Cosh[x]]) + (2*x*Sinh[x])/(3*Cosh[x]^(3/2))", //
				3396);
	}

	// {3400, 3382}
	public void test0111() {
		check(//
				"Integrate[Sqrt[a + a*Cosh[x]]/x, x]", //
				"Sqrt[a + a*Cosh[x]]*CoshIntegral[x/2]*Sech[x/2]", //
				3400, 3382);
	}

	// {3377, 2718}
	public void test0112() {
		check(//
				"Integrate[(a + b*x)*Cosh[c + d*x], x]", //
				"-((b*Cosh[c + d*x])/d^2) + ((a + b*x)*Sinh[c + d*x])/d", //
				3377, 2718);
	}

	// {5429, 2717}
	public void test0113() {
		check(//
				"Integrate[x*Cosh[a + b*x^2], x]", //
				"Sinh[a + b*x^2]/(2*b)", //
				5429, 2717);
	}

	// {5429, 2717}
	public void test0114() {
		check(//
				"Integrate[x^2*Cosh[x^3], x]", //
				"Sinh[x^3]/3", //
				5429, 2717);
	}

	// {5429, 2717}
	public void test0115() {
		check(//
				"Integrate[Cosh[x^(-5)]/x^6, x]", //
				"-Sinh[x^(-5)]/5", //
				5429, 2717);
	}

	// {5429, 2717}
	public void test0116() {
		check(//
				"Integrate[Cosh[a + b/x]/x^2, x]", //
				"-(Sinh[a + b/x]/b)", //
				5429, 2717);
	}

	// {5429, 2717}
	public void test0117() {
		check(//
				"Integrate[Cosh[a + b/x^2]/x^3, x]", //
				"-Sinh[a + b/x^2]/(2*b)", //
				5429, 2717);
	}

	// {2715, 8}
	public void test0122() {
		check(//
				"Integrate[Cosh[a + b*x]^2, x]", //
				"x/2 + (Cosh[a + b*x]*Sinh[a + b*x])/(2*b)", //
				2715, 8);
	}

	// {2713}
	public void test0123() {
		check(//
				"Integrate[Cosh[a + b*x]^3, x]", //
				"Sinh[a + b*x]/b + Sinh[a + b*x]^3/(3*b)", //
				2713);
	}

	// {2713}
	public void test0124() {
		check(//
				"Integrate[Cosh[a + b*x]^5, x]", //
				"Sinh[a + b*x]/b + (2*Sinh[a + b*x]^3)/(3*b) + Sinh[a + b*x]^5/(5*b)", //
				2713);
	}

	// {2715, 2719}
	public void test0125() {
		check(//
				"Integrate[Cosh[a + b*x]^(5/2), x]", //
				"(((-6*I)/5)*EllipticE[(I/2)*(a + b*x), 2])/b + (2*Cosh[a + b*x]^(3/2)*Sinh[a + b*x])/(5*b)", //
				2715, 2719);
	}

	// {2715, 2720}
	public void test0126() {
		check(//
				"Integrate[Cosh[a + b*x]^(3/2), x]", //
				"(((-2*I)/3)*EllipticF[(I/2)*(a + b*x), 2])/b + (2*Sqrt[Cosh[a + b*x]]*Sinh[a + b*x])/(3*b)", //
				2715, 2720);
	}

	// {2716, 2719}
	public void test0127() {
		check(//
				"Integrate[Cosh[a + b*x]^(-3/2), x]", //
				"((2*I)*EllipticE[(I/2)*(a + b*x), 2])/b + (2*Sinh[a + b*x])/(b*Sqrt[Cosh[a + b*x]])", //
				2716, 2719);
	}

	// {2716, 2720}
	public void test0128() {
		check(//
				"Integrate[Cosh[a + b*x]^(-5/2), x]", //
				"(((-2*I)/3)*EllipticF[(I/2)*(a + b*x), 2])/b + (2*Sinh[a + b*x])/(3*b*Cosh[a + b*x]^(3/2))", //
				2716, 2720);
	}

	// {2721, 2719}
	public void test0129() {
		check(//
				"Integrate[Sqrt[a*Cosh[x]], x]", //
				"((-2*I)*Sqrt[a*Cosh[x]]*EllipticE[(I/2)*x, 2])/Sqrt[Cosh[x]]", //
				2721, 2719);
	}

	// {2721, 2720}
	public void test0130() {
		check(//
				"Integrate[1/Sqrt[a*Cosh[x]], x]", //
				"((-2*I)*Sqrt[Cosh[x]]*EllipticF[(I/2)*x, 2])/Sqrt[a*Cosh[x]]", //
				2721, 2720);
	}

	// {2846, 2813}
	public void test0131() {
		check(//
				"Integrate[Cosh[x]^3/(a + a*Cosh[x]), x]", //
				"(3*x)/(2*a) - (2*Sinh[x])/a + (3*Cosh[x]*Sinh[x])/(2*a) - (Cosh[x]^2*Sinh[x])/(a + a*Cosh[x])", //
				2846, 2813);
	}

	// {2814, 2727}
	public void test0132() {
		check(//
				"Integrate[Cosh[x]/(a + a*Cosh[x]), x]", //
				"x/a - Sinh[x]/(a + a*Cosh[x])", //
				2814, 2727);
	}

	// {2729, 2727}
	public void test0133() {
		check(//
				"Integrate[(1 + Cosh[c + d*x])^(-2), x]", //
				"Sinh[c + d*x]/(3*d*(1 + Cosh[c + d*x])^2) + Sinh[c + d*x]/(3*d*(1 + Cosh[c + d*x]))", //
				2729, 2727);
	}

	// {2729, 2727}
	public void test0134() {
		check(//
				"Integrate[(1 - Cosh[c + d*x])^(-2), x]", //
				"-Sinh[c + d*x]/(3*d*(1 - Cosh[c + d*x])^2) - Sinh[c + d*x]/(3*d*(1 - Cosh[c + d*x]))", //
				2729, 2727);
	}

	// {2726, 2725}
	public void test0135() {
		check(//
				"Integrate[(a + a*Cosh[c + d*x])^(3/2), x]", //
				"(8*a^2*Sinh[c + d*x])/(3*d*Sqrt[a + a*Cosh[c + d*x]]) + (2*a*Sqrt[a + a*Cosh[c + d*x]]*Sinh[c + d*x])/(3*d)", //
				2726, 2725);
	}

	// {2728, 212}
	public void test0136() {
		check(//
				"Integrate[1/Sqrt[a + a*Cosh[c + d*x]], x]", //
				"(Sqrt[2]*ArcTan[(Sqrt[a]*Sinh[c + d*x])/(Sqrt[2]*Sqrt[a + a*Cosh[c + d*x]])])/(Sqrt[a]*d)", //
				2728, 212);
	}

	// {2726, 2725}
	public void test0137() {
		check(//
				"Integrate[(a - a*Cosh[c + d*x])^(3/2), x]", //
				"(-8*a^2*Sinh[c + d*x])/(3*d*Sqrt[a - a*Cosh[c + d*x]]) - (2*a*Sqrt[a - a*Cosh[c + d*x]]*Sinh[c + d*x])/(3*d)", //
				2726, 2725);
	}

	// {2728, 212}
	public void test0138() {
		check(//
				"Integrate[1/Sqrt[a - a*Cosh[c + d*x]], x]", //
				"-((Sqrt[2]*ArcTan[(Sqrt[a]*Sinh[c + d*x])/(Sqrt[2]*Sqrt[a - a*Cosh[c + d*x]])])/(Sqrt[a]*d))", //
				2728, 212);
	}

	// {2735, 2813}
	public void test0139() {
		check(//
				"Integrate[(a + b*Cosh[c + d*x])^3, x]", //
				"(a*(2*a^2 + 3*b^2)*x)/2 + (2*b*(4*a^2 + b^2)*Sinh[c + d*x])/(3*d) + (5*a*b^2*Cosh[c + d*x]*Sinh[c + d*x])/(6*d) + (b*(a + b*Cosh[c + d*x])^2*Sinh[c + d*x])/(3*d)", //
				2735, 2813);
	}

	// {2717}
	public void test0140() {
		check(//
				"Integrate[a + b*Cosh[c + d*x], x]", //
				"a*x + (b*Sinh[c + d*x])/d", //
				2717);
	}

	// {2738, 211}
	public void test0141() {
		check(//
				"Integrate[(a + b*Cosh[c + d*x])^(-1), x]", //
				"(2*ArcTanh[(Sqrt[a - b]*Tanh[(c + d*x)/2])/Sqrt[a + b]])/(Sqrt[a - b]*Sqrt[a + b]*d)", //
				2738, 211);
	}

	// {2738, 212}
	public void test0142() {
		check(//
				"Integrate[(3 + 5*Cosh[c + d*x])^(-1), x]", //
				"ArcTan[Tanh[(c + d*x)/2]/2]/(2*d)", //
				2738, 212);
	}

	// {2734, 2732}
	public void test0143() {
		check(//
				"Integrate[Sqrt[a + b*Cosh[c + d*x]], x]", //
				"((-2*I)*Sqrt[a + b*Cosh[c + d*x]]*EllipticE[(I/2)*(c + d*x), (2*b)/(a + b)])/(d*Sqrt[(a + b*Cosh[c + d*x])/(a + b)])", //
				2734, 2732);
	}

	// {2742, 2740}
	public void test0144() {
		check(//
				"Integrate[1/Sqrt[a + b*Cosh[x]], x]", //
				"((-2*I)*Sqrt[(a + b*Cosh[x])/(a + b)]*EllipticF[(I/2)*x, (2*b)/(a + b)])/Sqrt[a + b*Cosh[x]]", //
				2742, 2740);
	}

	// {2830, 2725}
	public void test0145() {
		check(//
				"Integrate[Sqrt[a + a*Cosh[x]]*(A + B*Cosh[x]), x]", //
				"(2*a*(3*A + B)*Sinh[x])/(3*Sqrt[a + a*Cosh[x]]) + (2*B*Sqrt[a + a*Cosh[x]]*Sinh[x])/3", //
				2830, 2725);
	}

	// {2830, 2725}
	public void test0146() {
		check(//
				"Integrate[Sqrt[a - a*Cosh[x]]*(A + B*Cosh[x]), x]", //
				"(-2*a*(3*A - B)*Sinh[x])/(3*Sqrt[a - a*Cosh[x]]) + (2*B*Sqrt[a - a*Cosh[x]]*Sinh[x])/3", //
				2830, 2725);
	}

	// {2814, 2727}
	public void test0147() {
		check(//
				"Integrate[(A + B*Cosh[x])/(1 + Cosh[x]), x]", //
				"B*x + ((A - B)*Sinh[x])/(1 + Cosh[x])", //
				2814, 2727);
	}

	// {2829, 2727}
	public void test0148() {
		check(//
				"Integrate[(A + B*Cosh[x])/(1 + Cosh[x])^2, x]", //
				"((A - B)*Sinh[x])/(3*(1 + Cosh[x])^2) + ((A + 2*B)*Sinh[x])/(3*(1 + Cosh[x]))", //
				2829, 2727);
	}

	// {2814, 2727}
	public void test0149() {
		check(//
				"Integrate[(A + B*Cosh[x])/(1 - Cosh[x]), x]", //
				"-(B*x) - ((A + B)*Sinh[x])/(1 - Cosh[x])", //
				2814, 2727);
	}

	// {2829, 2727}
	public void test0150() {
		check(//
				"Integrate[(A + B*Cosh[x])/(1 - Cosh[x])^2, x]", //
				"-((A + B)*Sinh[x])/(3*(1 - Cosh[x])^2) - ((A - 2*B)*Sinh[x])/(3*(1 - Cosh[x]))", //
				2829, 2727);
	}

	// {21, 8}
	public void test0151() {
		check(//
				"Integrate[((a*B)/b + B*Cosh[x])/(a + b*Cosh[x]), x]", //
				"(B*x)/b", //
				21, 8);
	}

	// {2833, 8}
	public void test0152() {
		check(//
				"Integrate[(a + b*Cosh[x])/(b + a*Cosh[x])^2, x]", //
				"Sinh[x]/(b + a*Cosh[x])", //
				2833, 8);
	}

	// {2814, 2736}
	public void test0153() {
		check(//
				"Integrate[(3 + Cosh[x])/(2 - Cosh[x]), x]", //
				"-x + (5*x)/Sqrt[3] + (10*ArcTanh[Sinh[x]/(2 + Sqrt[3] - Cosh[x])])/Sqrt[3]", //
				2814, 2736);
	}

	// {3286, 2717}
	public void test0154() {
		check(//
				"Integrate[Sqrt[a*Cosh[x]^2], x]", //
				"Sqrt[a*Cosh[x]^2]*Tanh[x]", //
				3286, 2717);
	}

	// {3286, 3855}
	public void test0155() {
		check(//
				"Integrate[1/Sqrt[a*Cosh[x]^2], x]", //
				"(ArcTan[Sinh[x]]*Cosh[x])/Sqrt[a*Cosh[x]^2]", //
				3286, 3855);
	}

	// {2746, 32}
	public void test0156() {
		check(//
				"Integrate[Sinh[x]/(1 + Cosh[x])^2, x]", //
				"-(1 + Cosh[x])^(-1)", //
				2746, 32);
	}

	// {2746, 32}
	public void test0157() {
		check(//
				"Integrate[Sinh[x]/(1 - Cosh[x])^2, x]", //
				"(1 - Cosh[x])^(-1)", //
				2746, 32);
	}

	// {2759, 8}
	public void test0158() {
		check(//
				"Integrate[Sinh[x]^2/(1 + Cosh[x])^2, x]", //
				"x - (2*Sinh[x])/(1 + Cosh[x])", //
				2759, 8);
	}

	// {2759, 8}
	public void test0159() {
		check(//
				"Integrate[Sinh[x]^2/(1 - Cosh[x])^2, x]", //
				"x + (2*Sinh[x])/(1 - Cosh[x])", //
				2759, 8);
	}

	// {2746, 32}
	public void test0160() {
		check(//
				"Integrate[Sinh[x]/(1 + Cosh[x])^3, x]", //
				"-1/(2*(1 + Cosh[x])^2)", //
				2746, 32);
	}

	// {2746, 32}
	public void test0161() {
		check(//
				"Integrate[Sinh[x]/(1 - Cosh[x])^3, x]", //
				"1/(2*(1 - Cosh[x])^2)", //
				2746, 32);
	}

	// {2746}
	public void test0162() {
		check(//
				"Integrate[Sinh[x]^3/(a + a*Cosh[x]), x]", //
				"-(Cosh[x]/a) + Cosh[x]^2/(2*a)", //
				2746);
	}

	// {2761, 8}
	public void test0163() {
		check(//
				"Integrate[Sinh[x]^2/(a + a*Cosh[x]), x]", //
				"-(x/a) + Sinh[x]/a", //
				2761, 8);
	}

	// {2746, 31}
	public void test0164() {
		check(//
				"Integrate[Sinh[x]/(a + a*Cosh[x]), x]", //
				"Log[1 + Cosh[x]]/a", //
				2746, 31);
	}

	// {2747, 31}
	public void test0165() {
		check(//
				"Integrate[Sinh[x]/(a + b*Cosh[x]), x]", //
				"Log[a + b*Cosh[x]]/b", //
				2747, 31);
	}

	// {6813, 3382}
	public void test0166() {
		check(//
				"Integrate[Cosh[Sqrt[1 - a*x]/Sqrt[1 + a*x]]/(1 - a^2*x^2), x]", //
				"-(CoshIntegral[Sqrt[1 - a*x]/Sqrt[1 + a*x]]/a)", //
				6813, 3382);
	}

	// {2747, 31}
	public void test0167() {
		check(//
				"Integrate[Sinh[c + d*x]/(a + b*Cosh[c + d*x]), x]", //
				"Log[a + b*Cosh[c + d*x]]/(b*d)", //
				2747, 31);
	}

	// {5631, 8}
	public void test0168() {
		check(//
				"Integrate[Cosh[a + b*Log[c*x^n]]^2, x]", //
				"(-2*b^2*n^2*x)/(1 - 4*b^2*n^2) + (x*Cosh[a + b*Log[c*x^n]]^2)/(1 - 4*b^2*n^2) - (2*b*n*x*Cosh[a + b*Log[c*x^n]]*Sinh[a + b*Log[c*x^n]])/(1 - 4*b^2*n^2)", //
				5631, 8);
	}

	// {5631, 5629}
	public void test0169() {
		check(//
				"Integrate[Cosh[a + b*Log[c*x^n]]^3, x]", //
				"(-6*b^2*n^2*x*Cosh[a + b*Log[c*x^n]])/(1 - 10*b^2*n^2 + 9*b^4*n^4) + (x*Cosh[a + b*Log[c*x^n]]^3)/(1 - 9*b^2*n^2) + (6*b^3*n^3*x*Sinh[a + b*Log[c*x^n]])/(1 - 10*b^2*n^2 + 9*b^4*n^4) - (3*b*n*x*Cosh[a + b*Log[c*x^n]]^2*Sinh[a + b*Log[c*x^n]])/(1 - 9*b^2*n^2)", //
				5631, 5629);
	}

	// {5641, 30}
	public void test0170() {
		check(//
				"Integrate[x^m*Cosh[a + b*Log[c*x^n]]^2, x]", //
				"(-2*b^2*n^2*x^(1 + m))/((1 + m)*((1 + m)^2 - 4*b^2*n^2)) + ((1 + m)*x^(1 + m)*Cosh[a + b*Log[c*x^n]]^2)/((1 + m)^2 - 4*b^2*n^2) - (2*b*n*x^(1 + m)*Cosh[a + b*Log[c*x^n]]*Sinh[a + b*Log[c*x^n]])/((1 + m)^2 - 4*b^2*n^2)", //
				5641, 30);
	}

	// {5641, 5639}
	public void test0171() {
		check(//
				"Integrate[x^m*Cosh[a + b*Log[c*x^n]]^3, x]", //
				"(-6*b^2*(1 + m)*n^2*x^(1 + m)*Cosh[a + b*Log[c*x^n]])/((1 + m)^4 - 10*b^2*(1 + m)^2*n^2 + 9*b^4*n^4) + ((1 + m)*x^(1 + m)*Cosh[a + b*Log[c*x^n]]^3)/((1 + m)^2 - 9*b^2*n^2) + (6*b^3*n^3*x^(1 + m)*Sinh[a + b*Log[c*x^n]])/((1 + m)^4 - 10*b^2*(1 + m)^2*n^2 + 9*b^4*n^4) - (3*b*n*x^(1 + m)*Cosh[a + b*Log[c*x^n]]^2*Sinh[a + b*Log[c*x^n]])/((1 + m)^2 - 9*b^2*n^2)", //
				5641, 5639);
	}

	// {2717}
	public void test0172() {
		check(//
				"Integrate[Cosh[a + b*Log[c*x^n]]/x, x]", //
				"Sinh[a + b*Log[c*x^n]]/(b*n)", //
				2717);
	}

	// {2719}
	public void test0173() {
		check(//
				"Integrate[Sqrt[Cosh[a + b*Log[c*x^n]]]/x, x]", //
				"((-2*I)*EllipticE[(I/2)*(a + b*Log[c*x^n]), 2])/(b*n)", //
				2719);
	}

	// {2720}
	public void test0174() {
		check(//
				"Integrate[1/(x*Sqrt[Cosh[a + b*Log[c*x^n]]]), x]", //
				"((-2*I)*EllipticF[(I/2)*(a + b*Log[c*x^n]), 2])/(b*n)", //
				2720);
	}

	// {5585, 5583}
	public void test0175() {
		check(//
				"Integrate[F^(c*(a + b*x))*Cosh[d + e*x]^3, x]", //
				"-((b*c*F^(c*(a + b*x))*Cosh[d + e*x]^3*Log[F])/(9*e^2 - b^2*c^2*Log[F]^2)) - (6*b*c*e^2*F^(c*(a + b*x))*Cosh[d + e*x]*Log[F])/(9*e^4 - 10*b^2*c^2*e^2*Log[F]^2 + b^4*c^4*Log[F]^4) + (3*e*F^(c*(a + b*x))*Cosh[d + e*x]^2*Sinh[d + e*x])/(9*e^2 - b^2*c^2*Log[F]^2) + (6*e^3*F^(c*(a + b*x))*Sinh[d + e*x])/(9*e^4 - 10*b^2*c^2*e^2*Log[F]^2 + b^4*c^4*Log[F]^4)", //
				5585, 5583);
	}

	// {5585, 2225}
	public void test0176() {
		check(//
				"Integrate[F^(c*(a + b*x))*Cosh[d + e*x]^2, x]", //
				"(2*e^2*F^(c*(a + b*x)))/(b*c*Log[F]*(4*e^2 - b^2*c^2*Log[F]^2)) - (b*c*F^(c*(a + b*x))*Cosh[d + e*x]^2*Log[F])/(4*e^2 - b^2*c^2*Log[F]^2) + (2*e*F^(c*(a + b*x))*Cosh[d + e*x]*Sinh[d + e*x])/(4*e^2 - b^2*c^2*Log[F]^2)", //
				5585, 2225);
	}

	// {5598, 5600}
	public void test0177() {
		check(//
				"Integrate[F^(c*(a + b*x))*Sech[d + e*x]^3, x]", //
				"(E^(d + e*x)*F^(c*(a + b*x))*Hypergeometric2F1[1, (e + b*c*Log[F])/(2*e), (3 + (b*c*Log[F])/e)/2, -E^(2*(d + e*x))]*(e - b*c*Log[F]))/e^2 + (b*c*F^(c*(a + b*x))*Log[F]*Sech[d + e*x])/(2*e^2) + (F^(c*(a + b*x))*Sech[d + e*x]*Tanh[d + e*x])/(2*e)", //
				5598, 5600);
	}

	// {5598, 5600}
	public void test0178() {
		check(//
				"Integrate[F^(c*(a + b*x))*Sech[d + e*x]^4, x]", //
				"(2*E^(2*(d + e*x))*F^(c*(a + b*x))*Hypergeometric2F1[2, 1 + (b*c*Log[F])/(2*e), 2 + (b*c*Log[F])/(2*e), -E^(2*(d + e*x))]*(2*e - b*c*Log[F]))/(3*e^2) + (b*c*F^(c*(a + b*x))*Log[F]*Sech[d + e*x]^2)/(6*e^2) + (F^(c*(a + b*x))*Sech[d + e*x]^2*Tanh[d + e*x])/(3*e)", //
				5598, 5600);
	}

	// {3396}
	public void test0179() {
		check(//
				"Integrate[x/Cosh[x]^(3/2) + x*Sqrt[Cosh[x]], x]", //
				"-4*Sqrt[Cosh[x]] + (2*x*Sinh[x])/Sqrt[Cosh[x]]", //
				3396);
	}

	// {3396}
	public void test0180() {
		check(//
				"Integrate[x/Cosh[x]^(5/2) - x/(3*Sqrt[Cosh[x]]), x]", //
				"4/(3*Sqrt[Cosh[x]]) + (2*x*Sinh[x])/(3*Cosh[x]^(3/2))", //
				3396);
	}

	// {3254, 2718}
	public void test0181() {
		check(//
				"Integrate[Sinh[x]^3/(a - a*Cosh[x]^2), x]", //
				"-(Cosh[x]/a)", //
				3254, 2718);
	}

	// {3254, 8}
	public void test0182() {
		check(//
				"Integrate[Sinh[x]^2/(a - a*Cosh[x]^2), x]", //
				"-(x/a)", //
				3254, 8);
	}

	// {3269, 211}
	public void test0183() {
		check(//
				"Integrate[Sinh[x]/(a + b*Cosh[x]^2), x]", //
				"ArcTan[(Sqrt[b]*Cosh[x])/Sqrt[a]]/(Sqrt[a]*Sqrt[b])", //
				3269, 211);
	}

	// {3260, 214}
	public void test0184() {
		check(//
				"Integrate[(a + b*Cosh[x]^2)^(-1), x]", //
				"ArcTanh[(Sqrt[a]*Tanh[x])/Sqrt[a + b]]/(Sqrt[a]*Sqrt[a + b])", //
				3260, 214);
	}

	// {3265, 211}
	public void test0185() {
		check(//
				"Integrate[Cosh[x]/(a + b*Cosh[x]^2), x]", //
				"ArcTan[(Sqrt[b]*Sinh[x])/Sqrt[a + b]]/(Sqrt[b]*Sqrt[a + b])", //
				3265, 211);
	}

	// {3260, 214}
	public void test0186() {
		check(//
				"Integrate[(a + b*Cosh[x]^2)^(-1), x]", //
				"ArcTanh[(Sqrt[a]*Tanh[x])/Sqrt[a + b]]/(Sqrt[a]*Sqrt[a + b])", //
				3260, 214);
	}

	// {3260, 212}
	public void test0187() {
		check(//
				"Integrate[(1 + Cosh[x]^2)^(-1), x]", //
				"ArcTanh[Tanh[x]/Sqrt[2]]/Sqrt[2]", //
				3260, 212);
	}

	// {3257, 3256}
	public void test0188() {
		check(//
				"Integrate[Sqrt[a + b*Cosh[x]^2], x]", //
				"((-I)*Sqrt[a + b*Cosh[x]^2]*EllipticE[Pi/2 + I*x, -(b/a)])/Sqrt[1 + (b*Cosh[x]^2)/a]", //
				3257, 3256);
	}

	// {3257, 3256}
	public void test0189() {
		check(//
				"Integrate[Sqrt[-1 - Cosh[x]^2], x]", //
				"((-I)*Sqrt[-1 - Cosh[x]^2]*EllipticE[Pi/2 + I*x, -1])/Sqrt[1 + Cosh[x]^2]", //
				3257, 3256);
	}

	// {3262, 3261}
	public void test0190() {
		check(//
				"Integrate[1/Sqrt[a + b*Cosh[x]^2], x]", //
				"((-I)*Sqrt[1 + (b*Cosh[x]^2)/a]*EllipticF[Pi/2 + I*x, -(b/a)])/Sqrt[a + b*Cosh[x]^2]", //
				3262, 3261);
	}

	// {3262, 3261}
	public void test0191() {
		check(//
				"Integrate[1/Sqrt[-1 - Cosh[x]^2], x]", //
				"((-I)*Sqrt[1 + Cosh[x]^2]*EllipticF[Pi/2 + I*x, -1])/Sqrt[-1 - Cosh[x]^2]", //
				3262, 3261);
	}

	// {3808, 2212}
	public void test0192() {
		check(//
				"Integrate[(c + d*x)^m/(a + a*Tanh[e + f*x]), x]", //
				"(c + d*x)^(1 + m)/(2*a*d*(1 + m)) - (2^(-2 - m)*E^(-2*e + (2*c*f)/d)*(c + d*x)^m*Gamma[1 + m, (2*f*(c + d*x))/d])/(a*f*((f*(c + d*x))/d)^m)", //
				3808, 2212);
	}

	// {3554, 3556}
	public void test0193() {
		check(//
				"Integrate[Tanh[a + b*x]^3, x]", //
				"Log[Cosh[a + b*x]]/b - Tanh[a + b*x]^2/(2*b)", //
				3554, 3556);
	}

	// {3554, 8}
	public void test0194() {
		check(//
				"Integrate[Tanh[a + b*x]^2, x]", //
				"x - Tanh[a + b*x]/b", //
				3554, 8);
	}

	// {3554, 8}
	public void test0195() {
		check(//
				"Integrate[Coth[a + b*x]^2, x]", //
				"x - Coth[a + b*x]/b", //
				3554, 8);
	}

	// {3554, 3556}
	public void test0196() {
		check(//
				"Integrate[Coth[a + b*x]^3, x]", //
				"-Coth[a + b*x]^2/(2*b) + Log[Sinh[a + b*x]]/b", //
				3554, 3556);
	}

	// {3557, 371}
	public void test0197() {
		check(//
				"Integrate[Tanh[a + b*x]^n, x]", //
				"(Hypergeometric2F1[1, (1 + n)/2, (3 + n)/2, Tanh[a + b*x]^2]*Tanh[a + b*x]^(1 + n))/(b*(1 + n))", //
				3557, 371);
	}

	// {3557, 371}
	public void test0198() {
		check(//
				"Integrate[(b*Tanh[c + d*x])^n, x]", //
				"(Hypergeometric2F1[1, (1 + n)/2, (3 + n)/2, Tanh[c + d*x]^2]*(b*Tanh[c + d*x])^(1 + n))/(b*d*(1 + n))", //
				3557, 371);
	}

	// {3739, 3556}
	public void test0199() {
		check(//
				"Integrate[Sqrt[a*Tanh[x]^2], x]", //
				"Coth[x]*Log[Cosh[x]]*Sqrt[a*Tanh[x]^2]", //
				3739, 3556);
	}

	// {3739, 3556}
	public void test0200() {
		check(//
				"Integrate[1/Sqrt[a*Tanh[x]^2], x]", //
				"(Log[Sinh[x]]*Tanh[x])/Sqrt[a*Tanh[x]^2]", //
				3739, 3556);
	}

	// {3739, 3556}
	public void test0201() {
		check(//
				"Integrate[Sqrt[-Tanh[c + d*x]^2], x]", //
				"(Coth[c + d*x]*Log[Cosh[c + d*x]]*Sqrt[-Tanh[c + d*x]^2])/d", //
				3739, 3556);
	}

	// {3739, 3556}
	public void test0202() {
		check(//
				"Integrate[1/Sqrt[-Tanh[c + d*x]^2], x]", //
				"(Log[Sinh[c + d*x]]*Tanh[c + d*x])/(d*Sqrt[-Tanh[c + d*x]^2])", //
				3739, 3556);
	}

	// {3558, 3556}
	public void test0203() {
		check(//
				"Integrate[(a + a*Tanh[c + d*x])^2, x]", //
				"2*a^2*x + (2*a^2*Log[Cosh[c + d*x]])/d - (a^2*Tanh[c + d*x])/d", //
				3558, 3556);
	}

	// {3560, 8}
	public void test0204() {
		check(//
				"Integrate[(a + a*Tanh[c + d*x])^(-1), x]", //
				"x/(2*a) - 1/(2*d*(a + a*Tanh[c + d*x]))", //
				3560, 8);
	}

	// {3561, 212}
	public void test0205() {
		check(//
				"Integrate[Sqrt[1 + Tanh[x]], x]", //
				"Sqrt[2]*ArcTanh[Sqrt[1 + Tanh[x]]/Sqrt[2]]", //
				3561, 212);
	}

	// {3558, 3556}
	public void test0206() {
		check(//
				"Integrate[(a + b*Tanh[c + d*x])^2, x]", //
				"(a^2 + b^2)*x + (2*a*b*Log[Cosh[c + d*x]])/d - (b^2*Tanh[c + d*x])/d", //
				3558, 3556);
	}

	// {3565, 3611}
	public void test0207() {
		check(//
				"Integrate[(a + b*Tanh[c + d*x])^(-1), x]", //
				"(a*x)/(a^2 - b^2) - (b*Log[a*Cosh[c + d*x] + b*Sinh[c + d*x]])/((a^2 - b^2)*d)", //
				3565, 3611);
	}

	// {3565, 3611}
	public void test0208() {
		check(//
				"Integrate[(4 + 6*Tanh[c + d*x])^(-1), x]", //
				"-x/5 + (3*Log[2*Cosh[c + d*x] + 3*Sinh[c + d*x]])/(10*d)", //
				3565, 3611);
	}

	// {3565, 3611}
	public void test0209() {
		check(//
				"Integrate[(4 - 6*Tanh[c + d*x])^(-1), x]", //
				"-x/5 - (3*Log[2*Cosh[c + d*x] - 3*Sinh[c + d*x]])/(10*d)", //
				3565, 3611);
	}

	// {3583, 2717}
	public void test0210() {
		check(//
				"Integrate[Cosh[x]/(1 + Tanh[x]), x]", //
				"(2*Sinh[x])/3 - Cosh[x]/(3*(1 + Tanh[x]))", //
				3583, 2717);
	}

	// {3568, 31}
	public void test0211() {
		check(//
				"Integrate[Sech[x]^2/(1 + Tanh[x]), x]", //
				"Log[1 + Tanh[x]]", //
				3568, 31);
	}

	// {3582, 3855}
	public void test0212() {
		check(//
				"Integrate[Sech[x]^3/(1 + Tanh[x]), x]", //
				"ArcTan[Sinh[x]] + Sech[x]", //
				3582, 3855);
	}

	// {3568}
	public void test0213() {
		check(//
				"Integrate[Sech[x]^4/(1 + Tanh[x]), x]", //
				"Tanh[x] - Tanh[x]^2/2", //
				3568);
	}

	// {3587, 31}
	public void test0214() {
		check(//
				"Integrate[Sech[x]^2/(a + b*Tanh[x]), x]", //
				"Log[a + b*Tanh[x]]/b", //
				3587, 31);
	}

	// {3565, 3611}
	public void test0215() {
		check(//
				"Integrate[(a + b*Tanh[x])^(-1), x]", //
				"(a*x)/(a^2 - b^2) - (b*Log[a*Cosh[x] + b*Sinh[x]])/(a^2 - b^2)", //
				3565, 3611);
	}

	// {3590, 212}
	public void test0216() {
		check(//
				"Integrate[Sech[x]/(a + b*Tanh[x]), x]", //
				"ArcTan[(Cosh[x]*(b + a*Tanh[x]))/Sqrt[a^2 - b^2]]/Sqrt[a^2 - b^2]", //
				3590, 212);
	}

	// {3607, 8}
	public void test0217() {
		check(//
				"Integrate[Tanh[x]/(1 + Tanh[x]), x]", //
				"x/2 + 1/(2*(1 + Tanh[x]))", //
				3607, 8);
	}

	// {3560, 8}
	public void test0218() {
		check(//
				"Integrate[(1 + Tanh[x])^(-1), x]", //
				"x/2 - 1/(2*(1 + Tanh[x]))", //
				3560, 8);
	}

	// {3612, 3611}
	public void test0219() {
		check(//
				"Integrate[Tanh[x]/(a + b*Tanh[x]), x]", //
				"-((b*x)/(a^2 - b^2)) + (a*Log[a*Cosh[x] + b*Sinh[x]])/(a^2 - b^2)", //
				3612, 3611);
	}

	// {3565, 3611}
	public void test0220() {
		check(//
				"Integrate[(a + b*Tanh[x])^(-1), x]", //
				"(a*x)/(a^2 - b^2) - (b*Log[a*Cosh[x] + b*Sinh[x]])/(a^2 - b^2)", //
				3565, 3611);
	}

	// {3556}
	public void test0221() {
		check(//
				"Integrate[Tanh[a + b*Log[c*x^n]]/x, x]", //
				"Log[Cosh[a + b*Log[c*x^n]]]/(b*n)", //
				3556);
	}

	// {3757}
	public void test0222() {
		check(//
				"Integrate[Cosh[c + d*x]^3*(a + b*Tanh[c + d*x]^2), x]", //
				"(a*Sinh[c + d*x])/d + ((a + b)*Sinh[c + d*x]^3)/(3*d)", //
				3757);
	}

	// {3756}
	public void test0223() {
		check(//
				"Integrate[Sech[c + d*x]^2*(a + b*Tanh[c + d*x]^2), x]", //
				"(a*Tanh[c + d*x])/d + (b*Tanh[c + d*x]^3)/(3*d)", //
				3756);
	}

	// {3757, 211}
	public void test0224() {
		check(//
				"Integrate[Sech[c + d*x]/(a + b*Tanh[c + d*x]^2), x]", //
				"ArcTan[(Sqrt[a + b]*Sinh[c + d*x])/Sqrt[a]]/(Sqrt[a]*Sqrt[a + b]*d)", //
				3757, 211);
	}

	// {3756, 211}
	public void test0225() {
		check(//
				"Integrate[Sech[c + d*x]^2/(a + b*Tanh[c + d*x]^2), x]", //
				"ArcTan[(Sqrt[b]*Tanh[c + d*x])/Sqrt[a]]/(Sqrt[a]*Sqrt[b]*d)", //
				3756, 211);
	}

	// {3712, 3556}
	public void test0226() {
		check(//
				"Integrate[Tanh[c + d*x]*(a + b*Tanh[c + d*x]^2), x]", //
				"((a + b)*Log[Cosh[c + d*x]])/d - (b*Tanh[c + d*x]^2)/(2*d)", //
				3712, 3556);
	}

	// {3710, 8}
	public void test0227() {
		check(//
				"Integrate[Coth[c + d*x]^2*(a + b*Tanh[c + d*x]^2), x]", //
				"(a + b)*x - (a*Coth[c + d*x])/d", //
				3710, 8);
	}

	// {3808, 2212}
	public void test0228() {
		check(//
				"Integrate[(c + d*x)^m/(a + a*Coth[e + f*x]), x]", //
				"(c + d*x)^(1 + m)/(2*a*d*(1 + m)) + (2^(-2 - m)*E^(-2*e + (2*c*f)/d)*(c + d*x)^m*Gamma[1 + m, (2*f*(c + d*x))/d])/(a*f*((f*(c + d*x))/d)^m)", //
				3808, 2212);
	}

	// {3557, 371}
	public void test0229() {
		check(//
				"Integrate[Coth[a + b*x]^n, x]", //
				"(Coth[a + b*x]^(1 + n)*Hypergeometric2F1[1, (1 + n)/2, (3 + n)/2, Coth[a + b*x]^2])/(b*(1 + n))", //
				3557, 371);
	}

	// {3557, 371}
	public void test0230() {
		check(//
				"Integrate[(b*Coth[c + d*x])^n, x]", //
				"((b*Coth[c + d*x])^(1 + n)*Hypergeometric2F1[1, (1 + n)/2, (3 + n)/2, Coth[c + d*x]^2])/(b*d*(1 + n))", //
				3557, 371);
	}

	// {3739, 3556}
	public void test0231() {
		check(//
				"Integrate[Sqrt[b*Coth[c + d*x]^2], x]", //
				"(Sqrt[b*Coth[c + d*x]^2]*Log[Sinh[c + d*x]]*Tanh[c + d*x])/d", //
				3739, 3556);
	}

	// {3739, 3556}
	public void test0232() {
		check(//
				"Integrate[1/Sqrt[b*Coth[c + d*x]^2], x]", //
				"(Coth[c + d*x]*Log[Cosh[c + d*x]])/(d*Sqrt[b*Coth[c + d*x]^2])", //
				3739, 3556);
	}

	// {3739, 3556}
	public void test0233() {
		check(//
				"Integrate[(b*Coth[c + d*x]^3)^(1/3), x]", //
				"((b*Coth[c + d*x]^3)^(1/3)*Log[Sinh[c + d*x]]*Tanh[c + d*x])/d", //
				3739, 3556);
	}

	// {3739, 3556}
	public void test0234() {
		check(//
				"Integrate[(b*Coth[c + d*x]^3)^(-1/3), x]", //
				"(Coth[c + d*x]*Log[Cosh[c + d*x]])/(d*(b*Coth[c + d*x]^3)^(1/3))", //
				3739, 3556);
	}

	// {3558, 3556}
	public void test0235() {
		check(//
				"Integrate[(1 + Coth[x])^2, x]", //
				"2*x - Coth[x] + 2*Log[Sinh[x]]", //
				3558, 3556);
	}

	// {3560, 8}
	public void test0236() {
		check(//
				"Integrate[(1 + Coth[x])^(-1), x]", //
				"x/2 - 1/(2*(1 + Coth[x]))", //
				3560, 8);
	}

	// {3561, 212}
	public void test0237() {
		check(//
				"Integrate[Sqrt[1 + Coth[x]], x]", //
				"Sqrt[2]*ArcTanh[Sqrt[1 + Coth[x]]/Sqrt[2]]", //
				3561, 212);
	}

	// {3558, 3556}
	public void test0238() {
		check(//
				"Integrate[(a + b*Coth[c + d*x])^2, x]", //
				"(a^2 + b^2)*x - (b^2*Coth[c + d*x])/d + (2*a*b*Log[Sinh[c + d*x]])/d", //
				3558, 3556);
	}

	// {3565, 3611}
	public void test0239() {
		check(//
				"Integrate[(a + b*Coth[c + d*x])^(-1), x]", //
				"(a*x)/(a^2 - b^2) - (b*Log[b*Cosh[c + d*x] + a*Sinh[c + d*x]])/((a^2 - b^2)*d)", //
				3565, 3611);
	}

	// {3565, 3611}
	public void test0240() {
		check(//
				"Integrate[(4 + 6*Coth[c + d*x])^(-1), x]", //
				"-x/5 + (3*Log[3*Cosh[c + d*x] + 2*Sinh[c + d*x]])/(10*d)", //
				3565, 3611);
	}

	// {3565, 3611}
	public void test0241() {
		check(//
				"Integrate[(4 - 6*Coth[c + d*x])^(-1), x]", //
				"-x/5 - (3*Log[3*Cosh[c + d*x] - 2*Sinh[c + d*x]])/(10*d)", //
				3565, 3611);
	}

	// {3583, 2718}
	public void test0242() {
		check(//
				"Integrate[Sinh[x]/(1 + Coth[x]), x]", //
				"(2*Cosh[x])/3 - Sinh[x]/(3*(1 + Coth[x]))", //
				3583, 2718);
	}

	// {3568, 31}
	public void test0243() {
		check(//
				"Integrate[Csch[x]^2/(1 + Coth[x]), x]", //
				"-Log[1 + Coth[x]]", //
				3568, 31);
	}

	// {3582, 3855}
	public void test0244() {
		check(//
				"Integrate[Csch[x]^3/(1 + Coth[x]), x]", //
				"ArcTanh[Cosh[x]] - Csch[x]", //
				3582, 3855);
	}

	// {3568}
	public void test0245() {
		check(//
				"Integrate[Csch[x]^4/(1 + Coth[x]), x]", //
				"Coth[x] - Coth[x]^2/2", //
				3568);
	}

	// {3590, 212}
	public void test0246() {
		check(//
				"Integrate[Csch[x]/(a + b*Coth[x]), x]", //
				"((-I)*ArcTan[(((-I)*b - I*a*Coth[x])*Sinh[x])/Sqrt[a^2 - b^2]])/Sqrt[a^2 - b^2]", //
				3590, 212);
	}

	// {3587, 31}
	public void test0247() {
		check(//
				"Integrate[Csch[x]^2/(a + b*Coth[x]), x]", //
				"-(Log[a + b*Coth[x]]/b)", //
				3587, 31);
	}

	// {3560, 8}
	public void test0248() {
		check(//
				"Integrate[(1 + Coth[x])^(-1), x]", //
				"x/2 - 1/(2*(1 + Coth[x]))", //
				3560, 8);
	}

	// {3607, 8}
	public void test0249() {
		check(//
				"Integrate[Coth[x]/(1 + Coth[x]), x]", //
				"x/2 + 1/(2*(1 + Coth[x]))", //
				3607, 8);
	}

	// {3565, 3611}
	public void test0250() {
		check(//
				"Integrate[(a + b*Coth[x])^(-1), x]", //
				"(a*x)/(a^2 - b^2) - (b*Log[b*Cosh[x] + a*Sinh[x]])/(a^2 - b^2)", //
				3565, 3611);
	}

	// {3612, 3611}
	public void test0251() {
		check(//
				"Integrate[Coth[x]/(a + b*Coth[x]), x]", //
				"-((b*x)/(a^2 - b^2)) + (a*Log[b*Cosh[x] + a*Sinh[x]])/(a^2 - b^2)", //
				3612, 3611);
	}

	// {3556}
	public void test0252() {
		check(//
				"Integrate[Coth[a + b*Log[c*x^n]]/x, x]", //
				"Log[Sinh[a + b*Log[c*x^n]]]/(b*n)", //
				3556);
	}

	// {4269, 3556}
	public void test0253() {
		check(//
				"Integrate[(c + d*x)*Sech[a + b*x]^2, x]", //
				"-((d*Log[Cosh[a + b*x]])/b^2) + ((c + d*x)*Tanh[a + b*x])/b", //
				4269, 3556);
	}

	// {3852, 8}
	public void test0264() {
		check(//
				"Integrate[Sech[a + b*x]^2, x]", //
				"Tanh[a + b*x]/b", //
				3852, 8);
	}

	// {3853, 3855}
	public void test0265() {
		check(//
				"Integrate[Sech[a + b*x]^3, x]", //
				"ArcTan[Sinh[a + b*x]]/(2*b) + (Sech[a + b*x]*Tanh[a + b*x])/(2*b)", //
				3853, 3855);
	}

	// {3852}
	public void test0266() {
		check(//
				"Integrate[Sech[a + b*x]^4, x]", //
				"Tanh[a + b*x]/b - Tanh[a + b*x]^3/(3*b)", //
				3852);
	}

	// {3852}
	public void test0267() {
		check(//
				"Integrate[Sech[a + b*x]^6, x]", //
				"Tanh[a + b*x]/b - (2*Tanh[a + b*x]^3)/(3*b) + Tanh[a + b*x]^5/(5*b)", //
				3852);
	}

	// {3852}
	public void test0268() {
		check(//
				"Integrate[Sech[7*x]^4, x]", //
				"Tanh[7*x]/7 - Tanh[7*x]^3/21", //
				3852);
	}

	// {3852}
	public void test0269() {
		check(//
				"Integrate[Sech[Pi*x]^6, x]", //
				"Tanh[Pi*x]/Pi - (2*Tanh[Pi*x]^3)/(3*Pi) + Tanh[Pi*x]^5/(5*Pi)", //
				3852);
	}

	// {3856, 2720}
	public void test0270() {
		check(//
				"Integrate[Sqrt[Sech[a + b*x]], x]", //
				"((-2*I)*Sqrt[Cosh[a + b*x]]*EllipticF[(I/2)*(a + b*x), 2]*Sqrt[Sech[a + b*x]])/b", //
				3856, 2720);
	}

	// {3856, 2719}
	public void test0271() {
		check(//
				"Integrate[1/Sqrt[Sech[a + b*x]], x]", //
				"((-2*I)*Sqrt[Cosh[a + b*x]]*EllipticE[(I/2)*(a + b*x), 2]*Sqrt[Sech[a + b*x]])/b", //
				3856, 2719);
	}

	// {3856, 2720}
	public void test0272() {
		check(//
				"Integrate[Sqrt[b*Sech[c + d*x]], x]", //
				"((-2*I)*Sqrt[Cosh[c + d*x]]*EllipticF[(I/2)*(c + d*x), 2]*Sqrt[b*Sech[c + d*x]])/d", //
				3856, 2720);
	}

	// {3856, 2719}
	public void test0273() {
		check(//
				"Integrate[1/Sqrt[b*Sech[c + d*x]], x]", //
				"((-2*I)*EllipticE[(I/2)*(c + d*x), 2])/(d*Sqrt[Cosh[c + d*x]]*Sqrt[b*Sech[c + d*x]])", //
				3856, 2719);
	}

	// {3857, 2722}
	public void test0274() {
		check(//
				"Integrate[(b*Sech[c + d*x])^n, x]", //
				"-((Cosh[c + d*x]*Hypergeometric2F1[1/2, (1 - n)/2, (3 - n)/2, Cosh[c + d*x]^2]*(b*Sech[c + d*x])^n*Sinh[c + d*x])/(d*(1 - n)*Sqrt[-Sinh[c + d*x]^2]))", //
				3857, 2722);
	}

	// {4207, 222}
	public void test0275() {
		check(//
				"Integrate[Sqrt[Sech[a + b*x]^2], x]", //
				"ArcSin[Tanh[a + b*x]]/b", //
				4207, 222);
	}

	// {4207, 197}
	public void test0276() {
		check(//
				"Integrate[1/Sqrt[Sech[a + b*x]^2], x]", //
				"Tanh[a + b*x]/(b*Sqrt[Sech[a + b*x]^2])", //
				4207, 197);
	}

	// {4207, 197}
	public void test0277() {
		check(//
				"Integrate[1/Sqrt[a*Sech[x]^2], x]", //
				"Tanh[x]/Sqrt[a*Sech[x]^2]", //
				4207, 197);
	}

	// {3862, 8}
	public void test0278() {
		check(//
				"Integrate[(a + a*Sech[c + d*x])^(-1), x]", //
				"x/a - Tanh[c + d*x]/(d*(a + a*Sech[c + d*x]))", //
				3862, 8);
	}

	// {3862, 8}
	public void test0279() {
		check(//
				"Integrate[(a - a*Sech[c + d*x])^(-1), x]", //
				"x/a - Tanh[c + d*x]/(d*(a - a*Sech[c + d*x]))", //
				3862, 8);
	}

	// {3859, 209}
	public void test0280() {
		check(//
				"Integrate[Sqrt[a + a*Sech[c + d*x]], x]", //
				"(2*Sqrt[a]*ArcTanh[(Sqrt[a]*Tanh[c + d*x])/Sqrt[a + a*Sech[c + d*x]]])/d", //
				3859, 209);
	}

	// {3859, 209}
	public void test0281() {
		check(//
				"Integrate[Sqrt[a - a*Sech[c + d*x]], x]", //
				"(2*Sqrt[a]*ArcTanh[(Sqrt[a]*Tanh[c + d*x])/Sqrt[a - a*Sech[c + d*x]]])/d", //
				3859, 209);
	}

	// {3859, 209}
	public void test0282() {
		check(//
				"Integrate[Sqrt[3 + 3*Sech[x]], x]", //
				"2*Sqrt[3]*ArcTanh[Tanh[x]/Sqrt[1 + Sech[x]]]", //
				3859, 209);
	}

	// {3859, 209}
	public void test0283() {
		check(//
				"Integrate[Sqrt[3 - 3*Sech[x]], x]", //
				"2*Sqrt[3]*ArcTanh[Tanh[x]/Sqrt[1 - Sech[x]]]", //
				3859, 209);
	}

	// {3855}
	public void test0284() {
		check(//
				"Integrate[a + b*Sech[c + d*x], x]", //
				"a*x + (b*ArcTan[Sinh[c + d*x]])/d", //
				3855);
	}

	// {3964, 31}
	public void test0285() {
		check(//
				"Integrate[Tanh[x]/(a + a*Sech[x]), x]", //
				"Log[1 + Cosh[x]]/a", //
				3964, 31);
	}

	// {3855}
	public void test0286() {
		check(//
				"Integrate[Sech[a + b*Log[c*x^n]]/x, x]", //
				"ArcTan[Sinh[a + b*Log[c*x^n]]]/(b*n)", //
				3855);
	}

	// {4130, 8}
	public void test0287() {
		check(//
				"Integrate[Cosh[c + d*x]^2*(a + b*Sech[c + d*x]^2), x]", //
				"((a + 2*b)*x)/2 + (a*Cosh[c + d*x]*Sinh[c + d*x])/(2*d)", //
				4130, 8);
	}

	// {4130, 3855}
	public void test0288() {
		check(//
				"Integrate[Cosh[c + d*x]*(a + b*Sech[c + d*x]^2), x]", //
				"(b*ArcTan[Sinh[c + d*x]])/d + (a*Sinh[c + d*x])/d", //
				4130, 3855);
	}

	// {4131, 3855}
	public void test0289() {
		check(//
				"Integrate[Sech[c + d*x]*(a + b*Sech[c + d*x]^2), x]", //
				"((2*a + b)*ArcTan[Sinh[c + d*x]])/(2*d) + (b*Sech[c + d*x]*Tanh[c + d*x])/(2*d)", //
				4131, 3855);
	}

	// {4232, 211}
	public void test0290() {
		check(//
				"Integrate[Sech[c + d*x]/(a + b*Sech[c + d*x]^2), x]", //
				"ArcTan[(Sqrt[a]*Sinh[c + d*x])/Sqrt[a + b]]/(Sqrt[a]*Sqrt[a + b]*d)", //
				4232, 211);
	}

	// {4231, 214}
	public void test0291() {
		check(//
				"Integrate[Sech[c + d*x]^2/(a + b*Sech[c + d*x]^2), x]", //
				"ArcTanh[(Sqrt[b]*Tanh[c + d*x])/Sqrt[a + b]]/(Sqrt[b]*Sqrt[a + b]*d)", //
				4231, 214);
	}

	// {4223, 266}
	public void test0292() {
		check(//
				"Integrate[Tanh[c + d*x]/(a + b*Sech[c + d*x]^2), x]", //
				"Log[b + a*Cosh[c + d*x]^2]/(2*a*d)", //
				4223, 266);
	}

	// {4269, 3556}
	public void test0293() {
		check(//
				"Integrate[(c + d*x)*Csch[a + b*x]^2, x]", //
				"-(((c + d*x)*Coth[a + b*x])/b) + (d*Log[Sinh[a + b*x]])/b^2", //
				4269, 3556);
	}

	// {3852, 8}
	public void test0304() {
		check(//
				"Integrate[Csch[a + b*x]^2, x]", //
				"-(Coth[a + b*x]/b)", //
				3852, 8);
	}

	// {3853, 3855}
	public void test0305() {
		check(//
				"Integrate[Csch[a + b*x]^3, x]", //
				"ArcTanh[Cosh[a + b*x]]/(2*b) - (Coth[a + b*x]*Csch[a + b*x])/(2*b)", //
				3853, 3855);
	}

	// {3852}
	public void test0306() {
		check(//
				"Integrate[Csch[a + b*x]^4, x]", //
				"Coth[a + b*x]/b - Coth[a + b*x]^3/(3*b)", //
				3852);
	}

	// {3852}
	public void test0307() {
		check(//
				"Integrate[Csch[a + b*x]^6, x]", //
				"-(Coth[a + b*x]/b) + (2*Coth[a + b*x]^3)/(3*b) - Coth[a + b*x]^5/(5*b)", //
				3852);
	}

	// {3856, 2720}
	public void test0308() {
		check(//
				"Integrate[Sqrt[Csch[a + b*x]], x]", //
				"((-2*I)*Sqrt[Csch[a + b*x]]*EllipticF[(I*a - Pi/2 + I*b*x)/2, 2]*Sqrt[I*Sinh[a + b*x]])/b", //
				3856, 2720);
	}

	// {3856, 2719}
	public void test0309() {
		check(//
				"Integrate[1/Sqrt[Csch[a + b*x]], x]", //
				"((-2*I)*EllipticE[(I*a - Pi/2 + I*b*x)/2, 2])/(b*Sqrt[Csch[a + b*x]]*Sqrt[I*Sinh[a + b*x]])", //
				3856, 2719);
	}

	// {3856, 2720}
	public void test0310() {
		check(//
				"Integrate[Sqrt[b*Csch[c + d*x]], x]", //
				"((-2*I)*Sqrt[b*Csch[c + d*x]]*EllipticF[(I*c - Pi/2 + I*d*x)/2, 2]*Sqrt[I*Sinh[c + d*x]])/d", //
				3856, 2720);
	}

	// {3856, 2719}
	public void test0311() {
		check(//
				"Integrate[1/Sqrt[b*Csch[c + d*x]], x]", //
				"((-2*I)*EllipticE[(I*c - Pi/2 + I*d*x)/2, 2])/(d*Sqrt[b*Csch[c + d*x]]*Sqrt[I*Sinh[c + d*x]])", //
				3856, 2719);
	}

	// {3857, 2722}
	public void test0312() {
		check(//
				"Integrate[(b*Csch[c + d*x])^n, x]", //
				"(Cosh[c + d*x]*(b*Csch[c + d*x])^n*Hypergeometric2F1[1/2, (1 - n)/2, (3 - n)/2, -Sinh[c + d*x]^2]*Sinh[c + d*x])/(d*(1 - n)*Sqrt[Cosh[c + d*x]^2])", //
				3857, 2722);
	}

	// {4207, 222}
	public void test0313() {
		check(//
				"Integrate[Sqrt[-Csch[x]^2], x]", //
				"ArcSin[Coth[x]]", //
				4207, 222);
	}

	// {4207, 197}
	public void test0314() {
		check(//
				"Integrate[1/Sqrt[-Csch[x]^2], x]", //
				"Coth[x]/Sqrt[-Csch[x]^2]", //
				4207, 197);
	}

	// {4207, 197}
	public void test0315() {
		check(//
				"Integrate[1/Sqrt[a*Csch[x]^2], x]", //
				"Coth[x]/Sqrt[a*Csch[x]^2]", //
				4207, 197);
	}

	// {3862, 8}
	public void test0316() {
		check(//
				"Integrate[(a + I*a*Csch[a + b*x])^(-1), x]", //
				"x/a - Coth[a + b*x]/(b*(a + I*a*Csch[a + b*x]))", //
				3862, 8);
	}

	// {3862, 8}
	public void test0317() {
		check(//
				"Integrate[(a - I*a*Csch[a + b*x])^(-1), x]", //
				"x/a - Coth[a + b*x]/(b*(a - I*a*Csch[a + b*x]))", //
				3862, 8);
	}

	// {3859, 209}
	public void test0318() {
		check(//
				"Integrate[Sqrt[a + I*a*Csch[c + d*x]], x]", //
				"(2*Sqrt[a]*ArcTanh[(Sqrt[a]*Coth[c + d*x])/Sqrt[a + I*a*Csch[c + d*x]]])/d", //
				3859, 209);
	}

	// {3859, 209}
	public void test0319() {
		check(//
				"Integrate[Sqrt[a - I*a*Csch[c + d*x]], x]", //
				"(2*Sqrt[a]*ArcTanh[(Sqrt[a]*Coth[c + d*x])/Sqrt[a - I*a*Csch[c + d*x]]])/d", //
				3859, 209);
	}

	// {3859, 209}
	public void test0320() {
		check(//
				"Integrate[Sqrt[3 + (3*I)*Csch[x]], x]", //
				"2*Sqrt[3]*ArcTanh[Coth[x]/Sqrt[1 + I*Csch[x]]]", //
				3859, 209);
	}

	// {3859, 209}
	public void test0321() {
		check(//
				"Integrate[Sqrt[3 - (3*I)*Csch[x]], x]", //
				"2*Sqrt[3]*ArcTanh[Coth[x]/Sqrt[1 - I*Csch[x]]]", //
				3859, 209);
	}

	// {3859, 213}
	public void test0322() {
		check(//
				"Integrate[Sqrt[-3 + (3*I)*Csch[x]], x]", //
				"-2*Sqrt[3]*ArcTan[Coth[x]/Sqrt[-1 + I*Csch[x]]]", //
				3859, 213);
	}

	// {3859, 213}
	public void test0323() {
		check(//
				"Integrate[Sqrt[-3 - (3*I)*Csch[x]], x]", //
				"-2*Sqrt[3]*ArcTan[Coth[x]/Sqrt[-1 - I*Csch[x]]]", //
				3859, 213);
	}

	// {3855}
	public void test0324() {
		check(//
				"Integrate[a + b*Csch[c + d*x], x]", //
				"a*x - (b*ArcTanh[Cosh[c + d*x]])/d", //
				3855);
	}

	// {3964, 31}
	public void test0325() {
		check(//
				"Integrate[Coth[x]/(I + Csch[x]), x]", //
				"(-I)*Log[I - Sinh[x]]", //
				3964, 31);
	}

	// {3855}
	public void test0326() {
		check(//
				"Integrate[Csch[a + b*Log[c*x^n]]/x, x]", //
				"-(ArcTanh[Cosh[a + b*Log[c*x^n]]]/(b*n))", //
				3855);
	}

	// {209}
	public void test0327() {
		check(//
				"Integrate[(Cosh[2 + 3*x]^2 + 2*Sinh[2 + 3*x]^2)^(-1), x]", //
				"ArcTan[Sqrt[2]*Tanh[2 + 3*x]]/(3*Sqrt[2])", //
				209);
	}

	// {3756, 209}
	public void test0328() {
		check(//
				"Integrate[Sech[2 + 3*x]^2/(1 + 2*Tanh[2 + 3*x]^2), x]", //
				"ArcTan[Sqrt[2]*Tanh[2 + 3*x]]/(3*Sqrt[2])", //
				3756, 209);
	}

	// {3756, 209}
	public void test0329() {
		check(//
				"Integrate[Csch[2 + 3*x]^2/(2 + Coth[2 + 3*x]^2), x]", //
				"ArcTan[Sqrt[2]*Tanh[2 + 3*x]]/(3*Sqrt[2])", //
				3756, 209);
	}

	// {3756, 212}
	public void test0330() {
		check(//
				"Integrate[Csch[2 + 3*x]^2/(2 - Coth[2 + 3*x]^2), x]", //
				"-ArcTanh[Sqrt[2]*Tanh[2 + 3*x]]/(3*Sqrt[2])", //
				3756, 212);
	}

	// {3756, 209}
	public void test0331() {
		check(//
				"Integrate[Csch[2 + 3*x]^2/(1 + 2*Coth[2 + 3*x]^2), x]", //
				"ArcTan[Tanh[2 + 3*x]/Sqrt[2]]/(3*Sqrt[2])", //
				3756, 209);
	}

	// {3756, 212}
	public void test0332() {
		check(//
				"Integrate[Csch[2 + 3*x]^2/(1 - 2*Coth[2 + 3*x]^2), x]", //
				"-ArcTanh[Tanh[2 + 3*x]/Sqrt[2]]/(3*Sqrt[2])", //
				3756, 212);
	}

	// {2644, 30}
	public void test0333() {
		check(//
				"Integrate[Cosh[a + b*x]*Sinh[a + b*x], x]", //
				"Sinh[a + b*x]^2/(2*b)", //
				2644, 30);
	}

	// {2644, 30}
	public void test0334() {
		check(//
				"Integrate[Cosh[a + b*x]*Sinh[a + b*x]^n, x]", //
				"Sinh[a + b*x]^(1 + n)/(b*(1 + n))", //
				2644, 30);
	}

	// {2645, 30}
	public void test0335() {
		check(//
				"Integrate[Cosh[a + b*x]^m*Sinh[a + b*x], x]", //
				"Cosh[a + b*x]^(1 + m)/(b*(1 + m))", //
				2645, 30);
	}

	// {2700, 29}
	public void test0336() {
		check(//
				"Integrate[Csch[a + b*x]*Sech[a + b*x], x]", //
				"Log[Tanh[a + b*x]]/b", //
				2700, 29);
	}

	// {2701, 30}
	public void test0337() {
		check(//
				"Integrate[Cosh[x]*Csch[x]^(7/3), x]", //
				"(-3*Csch[x]^(4/3))/4", //
				2701, 30);
	}

	// {2686, 8}
	public void test0338() {
		check(//
				"Integrate[Sech[a + b*x]*Tanh[a + b*x], x]", //
				"-(Sech[a + b*x]/b)", //
				2686, 8);
	}

	// {2686, 30}
	public void test0339() {
		check(//
				"Integrate[Sech[a + b*x]^2*Tanh[a + b*x], x]", //
				"-Sech[a + b*x]^2/(2*b)", //
				2686, 30);
	}

	// {2702, 30}
	public void test0340() {
		check(//
				"Integrate[Sech[a + b*x]^(1 + n)*Sinh[a + b*x], x]", //
				"-(Sech[a + b*x]^n/(b*n))", //
				2702, 30);
	}

	// {2687, 30}
	public void test0341() {
		check(//
				"Integrate[Sech[a + b*x]^2*Tanh[a + b*x]^2, x]", //
				"Tanh[a + b*x]^3/(3*b)", //
				2687, 30);
	}

	// {2687, 30}
	public void test0342() {
		check(//
				"Integrate[Sech[a + b*x]^2*Tanh[a + b*x]^3, x]", //
				"Tanh[a + b*x]^4/(4*b)", //
				2687, 30);
	}

	// {2687, 32}
	public void test0343() {
		check(//
				"Integrate[Sech[a + b*x]^2*Tanh[a + b*x]^n, x]", //
				"Tanh[a + b*x]^(1 + n)/(b*(1 + n))", //
				2687, 32);
	}

	// {2686}
	public void test0344() {
		check(//
				"Integrate[Sech[a + b*x]*Tanh[a + b*x]^3, x]", //
				"-(Sech[a + b*x]/b) + Sech[a + b*x]^3/(3*b)", //
				2686);
	}

	// {2691, 3855}
	public void test0345() {
		check(//
				"Integrate[Sech[a + b*x]*Tanh[a + b*x]^2, x]", //
				"ArcTan[Sinh[a + b*x]]/(2*b) - (Sech[a + b*x]*Tanh[a + b*x])/(2*b)", //
				2691, 3855);
	}

	// {2686, 8}
	public void test0346() {
		check(//
				"Integrate[Coth[a + b*x]*Csch[a + b*x], x]", //
				"-(Csch[a + b*x]/b)", //
				2686, 8);
	}

	// {2686, 30}
	public void test0347() {
		check(//
				"Integrate[Coth[a + b*x]*Csch[a + b*x]^2, x]", //
				"-Csch[a + b*x]^2/(2*b)", //
				2686, 30);
	}

	// {2701, 30}
	public void test0348() {
		check(//
				"Integrate[Cosh[a + b*x]*Csch[a + b*x]^(1 + n), x]", //
				"-(Csch[a + b*x]^n/(b*n))", //
				2701, 30);
	}

	// {2687, 30}
	public void test0349() {
		check(//
				"Integrate[Coth[a + b*x]^2*Csch[a + b*x]^2, x]", //
				"-Coth[a + b*x]^3/(3*b)", //
				2687, 30);
	}

	// {2687, 30}
	public void test0350() {
		check(//
				"Integrate[Coth[a + b*x]^3*Csch[a + b*x]^2, x]", //
				"-Coth[a + b*x]^4/(4*b)", //
				2687, 30);
	}

	// {2687, 32}
	public void test0351() {
		check(//
				"Integrate[Coth[a + b*x]^n*Csch[a + b*x]^2, x]", //
				"-(Coth[a + b*x]^(1 + n)/(b*(1 + n)))", //
				2687, 32);
	}

	// {2686}
	public void test0352() {
		check(//
				"Integrate[Coth[a + b*x]^3*Csch[a + b*x], x]", //
				"-(Csch[a + b*x]/b) - Csch[a + b*x]^3/(3*b)", //
				2686);
	}

	// {2691, 3855}
	public void test0353() {
		check(//
				"Integrate[Coth[a + b*x]^2*Csch[a + b*x], x]", //
				"-ArcTanh[Cosh[a + b*x]]/(2*b) - (Coth[a + b*x]*Csch[a + b*x])/(2*b)", //
				2691, 3855);
	}

	// {4442, 213}
	public void test0354() {
		check(//
				"Integrate[Sech[2*x]*Sinh[x], x]", //
				"-(ArcTanh[Sqrt[2]*Cosh[x]]/Sqrt[2])", //
				4442, 213);
	}

	// {4373, 3855}
	public void test0355() {
		check(//
				"Integrate[Csch[2*x]*Sinh[x], x]", //
				"ArcTan[Sinh[x]]/2", //
				4373, 3855);
	}

	// {209}
	public void test0356() {
		check(//
				"Integrate[Csch[3*x]*Sinh[x], x]", //
				"ArcTan[Tanh[x]/Sqrt[3]]/Sqrt[3]", //
				209);
	}

	// {4441, 209}
	public void test0357() {
		check(//
				"Integrate[Cosh[x]*Sech[2*x], x]", //
				"ArcTan[Sqrt[2]*Sinh[x]]/Sqrt[2]", //
				4441, 209);
	}

	// {209}
	public void test0358() {
		check(//
				"Integrate[Cosh[x]*Sech[3*x], x]", //
				"ArcTan[Sqrt[3]*Tanh[x]]/Sqrt[3]", //
				209);
	}

	// {4372, 3855}
	public void test0359() {
		check(//
				"Integrate[Cosh[x]*Csch[2*x], x]", //
				"-ArcTanh[Cosh[x]]/2", //
				4372, 3855);
	}

	// {2644, 30}
	public void test0360() {
		check(//
				"Integrate[Cosh[a + b*x]*Sinh[a + b*x], x]", //
				"Sinh[a + b*x]^2/(2*b)", //
				2644, 30);
	}

	// {2645, 30}
	public void test0361() {
		check(//
				"Integrate[Cosh[a + b*x]^2*Sinh[a + b*x], x]", //
				"Cosh[a + b*x]^3/(3*b)", //
				2645, 30);
	}

	// {2645, 30}
	public void test0362() {
		check(//
				"Integrate[Cosh[a + b*x]^3*Sinh[a + b*x], x]", //
				"Cosh[a + b*x]^4/(4*b)", //
				2645, 30);
	}

	// {2644, 30}
	public void test0363() {
		check(//
				"Integrate[Cosh[a + b*x]*Sinh[a + b*x]^2, x]", //
				"Sinh[a + b*x]^3/(3*b)", //
				2644, 30);
	}

	// {2644, 30}
	public void test0364() {
		check(//
				"Integrate[Cosh[a + b*x]*Sinh[a + b*x]^3, x]", //
				"Sinh[a + b*x]^4/(4*b)", //
				2644, 30);
	}

	// {5526, 3855}
	public void test0365() {
		check(//
				"Integrate[x*Sech[a + b*x]*Tanh[a + b*x], x]", //
				"ArcTan[Sinh[a + b*x]]/b^2 - (x*Sech[a + b*x])/b", //
				5526, 3855);
	}

	// {2686, 8}
	public void test0366() {
		check(//
				"Integrate[Sech[a + b*x]*Tanh[a + b*x], x]", //
				"-(Sech[a + b*x]/b)", //
				2686, 8);
	}

	// {2686, 30}
	public void test0367() {
		check(//
				"Integrate[Sech[a + b*x]^2*Tanh[a + b*x], x]", //
				"-Sech[a + b*x]^2/(2*b)", //
				2686, 30);
	}

	// {3554, 8}
	public void test0368() {
		check(//
				"Integrate[Tanh[a + b*x]^2, x]", //
				"x - Tanh[a + b*x]/b", //
				3554, 8);
	}

	// {2691, 3855}
	public void test0369() {
		check(//
				"Integrate[Sech[a + b*x]*Tanh[a + b*x]^2, x]", //
				"ArcTan[Sinh[a + b*x]]/(2*b) - (Sech[a + b*x]*Tanh[a + b*x])/(2*b)", //
				2691, 3855);
	}

	// {3554, 3556}
	public void test0370() {
		check(//
				"Integrate[Tanh[a + b*x]^3, x]", //
				"Log[Cosh[a + b*x]]/b - Tanh[a + b*x]^2/(2*b)", //
				3554, 3556);
	}

	// {5527, 3855}
	public void test0371() {
		check(//
				"Integrate[x*Coth[a + b*x]*Csch[a + b*x], x]", //
				"-(ArcTanh[Cosh[a + b*x]]/b^2) - (x*Csch[a + b*x])/b", //
				5527, 3855);
	}

	// {2686, 8}
	public void test0372() {
		check(//
				"Integrate[Coth[a + b*x]*Csch[a + b*x], x]", //
				"-(Csch[a + b*x]/b)", //
				2686, 8);
	}

	// {3554, 8}
	public void test0373() {
		check(//
				"Integrate[Coth[a + b*x]^2, x]", //
				"x - Coth[a + b*x]/b", //
				3554, 8);
	}

	// {2686, 30}
	public void test0374() {
		check(//
				"Integrate[Coth[a + b*x]*Csch[a + b*x]^2, x]", //
				"-Csch[a + b*x]^2/(2*b)", //
				2686, 30);
	}

	// {2691, 3855}
	public void test0375() {
		check(//
				"Integrate[Coth[a + b*x]^2*Csch[a + b*x], x]", //
				"-ArcTanh[Cosh[a + b*x]]/(2*b) - (Coth[a + b*x]*Csch[a + b*x])/(2*b)", //
				2691, 3855);
	}

	// {3554, 3556}
	public void test0376() {
		check(//
				"Integrate[Coth[a + b*x]^3, x]", //
				"-Coth[a + b*x]^2/(2*b) + Log[Sinh[a + b*x]]/b", //
				3554, 3556);
	}

	// {2700, 29}
	public void test0377() {
		check(//
				"Integrate[Csch[a + b*x]*Sech[a + b*x], x]", //
				"Log[Tanh[a + b*x]]/b", //
				2700, 29);
	}

	// {5481, 2719}
	public void test0378() {
		check(//
				"Integrate[(x*Sinh[a + b*x])/Sqrt[Cosh[a + b*x]], x]", //
				"(2*x*Sqrt[Cosh[a + b*x]])/b + ((4*I)*EllipticE[(I/2)*(a + b*x), 2])/b^2", //
				5481, 2719);
	}

	// {5481, 2720}
	public void test0379() {
		check(//
				"Integrate[(x*Sinh[a + b*x])/Cosh[a + b*x]^(3/2), x]", //
				"(-2*x)/(b*Sqrt[Cosh[a + b*x]]) - ((4*I)*EllipticF[(I/2)*(a + b*x), 2])/b^2", //
				5481, 2720);
	}

	// {3152, 8}
	public void test0380() {
		check(//
				"Integrate[(a*Cosh[x] + b*Sinh[x])^2, x]", //
				"((a^2 - b^2)*x)/2 + ((b*Cosh[x] + a*Sinh[x])*(a*Cosh[x] + b*Sinh[x]))/2", //
				3152, 8);
	}

	// {3151}
	public void test0381() {
		check(//
				"Integrate[(a*Cosh[x] + b*Sinh[x])^3, x]", //
				"(a^2 - b^2)*(b*Cosh[x] + a*Sinh[x]) + (b*Cosh[x] + a*Sinh[x])^3/3", //
				3151);
	}

	// {3153, 212}
	public void test0382() {
		check(//
				"Integrate[(a*Cosh[x] + b*Sinh[x])^(-1), x]", //
				"ArcTan[(b*Cosh[x] + a*Sinh[x])/Sqrt[a^2 - b^2]]/Sqrt[a^2 - b^2]", //
				3153, 212);
	}

	// {3155, 3154}
	public void test0383() {
		check(//
				"Integrate[(a*Cosh[x] + b*Sinh[x])^(-4), x]", //
				"(b*Cosh[x] + a*Sinh[x])/(3*(a^2 - b^2)*(a*Cosh[x] + b*Sinh[x])^3) + (2*Sinh[x])/(3*a*(a^2 - b^2)*(a*Cosh[x] + b*Sinh[x]))", //
				3155, 3154);
	}

	// {3157, 2719}
	public void test0384() {
		check(//
				"Integrate[Sqrt[a*Cosh[x] + b*Sinh[x]], x]", //
				"((-2*I)*EllipticE[(I*x - ArcTan[a, (-I)*b])/2, 2]*Sqrt[a*Cosh[x] + b*Sinh[x]])/Sqrt[(a*Cosh[x] + b*Sinh[x])/Sqrt[a^2 - b^2]]", //
				3157, 2719);
	}

	// {3157, 2720}
	public void test0385() {
		check(//
				"Integrate[1/Sqrt[a*Cosh[x] + b*Sinh[x]], x]", //
				"((-2*I)*EllipticF[(I*x - ArcTan[a, (-I)*b])/2, 2]*Sqrt[(a*Cosh[x] + b*Sinh[x])/Sqrt[a^2 - b^2]])/Sqrt[a*Cosh[x] + b*Sinh[x]]", //
				3157, 2720);
	}

	// {3176, 3212}
	public void test0386() {
		check(//
				"Integrate[Sinh[x]/(a*Cosh[x] + b*Sinh[x]), x]", //
				"-((b*x)/(a^2 - b^2)) + (a*Log[a*Cosh[x] + b*Sinh[x]])/(a^2 - b^2)", //
				3176, 3212);
	}

	// {3177, 3212}
	public void test0387() {
		check(//
				"Integrate[Cosh[x]/(a*Cosh[x] + b*Sinh[x]), x]", //
				"(a*x)/(a^2 - b^2) - (b*Log[a*Cosh[x] + b*Sinh[x]])/(a^2 - b^2)", //
				3177, 3212);
	}

	// {3166, 37}
	public void test0388() {
		check(//
				"Integrate[Sinh[x]/(a*Cosh[x] + b*Sinh[x])^3, x]", //
				"Tanh[x]^2/(2*a*(a + b*Tanh[x])^2)", //
				3166, 37);
	}

	// {3167, 37}
	public void test0389() {
		check(//
				"Integrate[Cosh[x]/(a*Cosh[x] + b*Sinh[x])^3, x]", //
				"-Coth[x]^2/(2*b*(b + a*Coth[x])^2)", //
				3167, 37);
	}

	// {3203, 31}
	public void test0390() {
		check(//
				"Integrate[(a + a*Cosh[x] + c*Sinh[x])^(-1), x]", //
				"Log[a + c*Tanh[x/2]]/c", //
				3203, 31);
	}

	// {3195, 3193}
	public void test0391() {
		check(//
				"Integrate[(Sqrt[b^2 - c^2] + b*Cosh[x] + c*Sinh[x])^(-2), x]", //
				"(c*Cosh[x] + b*Sinh[x])/(3*Sqrt[b^2 - c^2]*(Sqrt[b^2 - c^2] + b*Cosh[x] + c*Sinh[x])^2) - (c + Sqrt[b^2 - c^2]*Sinh[x])/(3*c*Sqrt[b^2 - c^2]*(c*Cosh[x] + b*Sinh[x]))", //
				3195, 3193);
	}

	// {3198, 2732}
	public void test0392() {
		check(//
				"Integrate[Sqrt[a + b*Cosh[x] + c*Sinh[x]], x]", //
				"((-2*I)*EllipticE[(I*x - ArcTan[b, (-I)*c])/2, (2*Sqrt[b^2 - c^2])/(a + Sqrt[b^2 - c^2])]*Sqrt[a + b*Cosh[x] + c*Sinh[x]])/Sqrt[(a + b*Cosh[x] + c*Sinh[x])/(a + Sqrt[b^2 - c^2])]", //
				3198, 2732);
	}

	// {3206, 2740}
	public void test0393() {
		check(//
				"Integrate[1/Sqrt[a + b*Cosh[x] + c*Sinh[x]], x]", //
				"((-2*I)*EllipticF[(I*x - ArcTan[b, (-I)*c])/2, (2*Sqrt[b^2 - c^2])/(a + Sqrt[b^2 - c^2])]*Sqrt[(a + b*Cosh[x] + c*Sinh[x])/(a + Sqrt[b^2 - c^2])])/Sqrt[a + b*Cosh[x] + c*Sinh[x]]", //
				3206, 2740);
	}

	// {3192, 3191}
	public void test0394() {
		check(//
				"Integrate[(Sqrt[b^2 - c^2] + b*Cosh[x] + c*Sinh[x])^(3/2), x]", //
				"(8*Sqrt[b^2 - c^2]*(c*Cosh[x] + b*Sinh[x]))/(3*Sqrt[Sqrt[b^2 - c^2] + b*Cosh[x] + c*Sinh[x]]) + (2*(c*Cosh[x] + b*Sinh[x])*Sqrt[Sqrt[b^2 - c^2] + b*Cosh[x] + c*Sinh[x]])/3", //
				3192, 3191);
	}

	// {3192, 3191}
	public void test0395() {
		check(//
				"Integrate[(-Sqrt[b^2 - c^2] + b*Cosh[x] + c*Sinh[x])^(3/2), x]", //
				"(-8*Sqrt[b^2 - c^2]*(c*Cosh[x] + b*Sinh[x]))/(3*Sqrt[-Sqrt[b^2 - c^2] + b*Cosh[x] + c*Sinh[x]]) + (2*(c*Cosh[x] + b*Sinh[x])*Sqrt[-Sqrt[b^2 - c^2] + b*Cosh[x] + c*Sinh[x]])/3", //
				3192, 3191);
	}

	// {209}
	public void test0396() {
		check(//
				"Integrate[(Cosh[x]^2 + Sinh[x]^2)^(-1), x]", //
				"ArcTan[Tanh[x]]", //
				209);
	}

	// {391}
	public void test0397() {
		check(//
				"Integrate[(Cosh[x]^2 + Sinh[x]^2)^(-2), x]", //
				"Tanh[x]/(1 + Tanh[x]^2)", //
				391);
	}

	// {4465, 8}
	public void test0398() {
		check(//
				"Integrate[(Cosh[x]^2 - Sinh[x]^2)^(-1), x]", //
				"x", //
				4465, 8);
	}

	// {4465, 8}
	public void test0399() {
		check(//
				"Integrate[(Cosh[x]^2 - Sinh[x]^2)^(-2), x]", //
				"x", //
				4465, 8);
	}

	// {4465, 8}
	public void test0400() {
		check(//
				"Integrate[(Cosh[x]^2 - Sinh[x]^2)^(-3), x]", //
				"x", //
				4465, 8);
	}

	// {4466, 8}
	public void test0401() {
		check(//
				"Integrate[(Sech[x]^2 + Tanh[x]^2)^(-1), x]", //
				"x", //
				4466, 8);
	}

	// {4466, 8}
	public void test0402() {
		check(//
				"Integrate[(Sech[x]^2 + Tanh[x]^2)^(-2), x]", //
				"x", //
				4466, 8);
	}

	// {4466, 8}
	public void test0403() {
		check(//
				"Integrate[(Sech[x]^2 + Tanh[x]^2)^(-3), x]", //
				"x", //
				4466, 8);
	}

	// {4467, 8}
	public void test0404() {
		check(//
				"Integrate[(Coth[x]^2 - Csch[x]^2)^(-1), x]", //
				"x", //
				4467, 8);
	}

	// {4467, 8}
	public void test0405() {
		check(//
				"Integrate[(Coth[x]^2 - Csch[x]^2)^(-2), x]", //
				"x", //
				4467, 8);
	}

	// {4467, 8}
	public void test0406() {
		check(//
				"Integrate[(Coth[x]^2 - Csch[x]^2)^(-3), x]", //
				"x", //
				4467, 8);
	}

	// {2745, 2723}
	public void test0407() {
		check(//
				"Integrate[(a + b*Cosh[c + d*x]*Sinh[c + d*x])^2, x]", //
				"((8*a^2 - b^2)*x)/8 + (a*b*Cosh[2*c + 2*d*x])/(2*d) + (b^2*Cosh[2*c + 2*d*x]*Sinh[2*c + 2*d*x])/(16*d)", //
				2745, 2723);
	}

	// {5590, 2291}
	public void test0408() {
		check(//
				"Integrate[F^(c*(a + b*x))*Sinh[d + e*x]^n, x]", //
				"-((F^(c*(a + b*x))*Hypergeometric2F1[-n, -(e*n - b*c*Log[F])/(2*e), (2 - n + (b*c*Log[F])/e)/2, E^(2*(d + e*x))]*Sinh[d + e*x]^n)/((1 - E^(2*(d + e*x)))^n*(e*n - b*c*Log[F])))", //
				5590, 2291);
	}

	// {5584, 5582}
	public void test0409() {
		check(//
				"Integrate[E^(a + b*x)*Sinh[c + d*x]^3, x]", //
				"(-6*d^3*E^(a + b*x)*Cosh[c + d*x])/(b^4 - 10*b^2*d^2 + 9*d^4) + (6*b*d^2*E^(a + b*x)*Sinh[c + d*x])/(b^4 - 10*b^2*d^2 + 9*d^4) - (3*d*E^(a + b*x)*Cosh[c + d*x]*Sinh[c + d*x]^2)/(b^2 - 9*d^2) + (b*E^(a + b*x)*Sinh[c + d*x]^3)/(b^2 - 9*d^2)", //
				5584, 5582);
	}

	// {5584, 2225}
	public void test0410() {
		check(//
				"Integrate[E^(a + b*x)*Sinh[c + d*x]^2, x]", //
				"(2*d^2*E^(a + b*x))/(b*(b^2 - 4*d^2)) - (2*d*E^(a + b*x)*Cosh[c + d*x]*Sinh[c + d*x])/(b^2 - 4*d^2) + (b*E^(a + b*x)*Sinh[c + d*x]^2)/(b^2 - 4*d^2)", //
				5584, 2225);
	}

	// {5599, 5601}
	public void test0411() {
		check(//
				"Integrate[E^(c + d*x)*Csch[a + b*x]^3, x]", //
				"-(d*E^(c + d*x)*Csch[a + b*x])/(2*b^2) - (E^(c + d*x)*Coth[a + b*x]*Csch[a + b*x])/(2*b) + ((b - d)*E^(a + c + b*x + d*x)*Hypergeometric2F1[1, (b + d)/(2*b), (3 + d/b)/2, E^(2*(a + b*x))])/b^2", //
				5599, 5601);
	}

	// {5591, 2291}
	public void test0412() {
		check(//
				"Integrate[F^(c*(a + b*x))*Cosh[d + e*x]^n, x]", //
				"-((F^(c*(a + b*x))*Cosh[d + e*x]^n*Hypergeometric2F1[-n, -(e*n - b*c*Log[F])/(2*e), (2 - n + (b*c*Log[F])/e)/2, -E^(2*(d + e*x))])/((1 + E^(2*(d + e*x)))^n*(e*n - b*c*Log[F])))", //
				5591, 2291);
	}

	// {5585, 5583}
	public void test0413() {
		check(//
				"Integrate[E^(a + b*x)*Cosh[c + d*x]^3, x]", //
				"(-6*b*d^2*E^(a + b*x)*Cosh[c + d*x])/(b^4 - 10*b^2*d^2 + 9*d^4) + (b*E^(a + b*x)*Cosh[c + d*x]^3)/(b^2 - 9*d^2) + (6*d^3*E^(a + b*x)*Sinh[c + d*x])/(b^4 - 10*b^2*d^2 + 9*d^4) - (3*d*E^(a + b*x)*Cosh[c + d*x]^2*Sinh[c + d*x])/(b^2 - 9*d^2)", //
				5585, 5583);
	}

	// {5585, 2225}
	public void test0414() {
		check(//
				"Integrate[E^(a + b*x)*Cosh[c + d*x]^2, x]", //
				"(-2*d^2*E^(a + b*x))/(b*(b^2 - 4*d^2)) + (b*E^(a + b*x)*Cosh[c + d*x]^2)/(b^2 - 4*d^2) - (2*d*E^(a + b*x)*Cosh[c + d*x]*Sinh[c + d*x])/(b^2 - 4*d^2)", //
				5585, 2225);
	}

	// {5598, 5600}
	public void test0415() {
		check(//
				"Integrate[E^(a + b*x)*Sech[c + d*x]^3, x]", //
				"-(((b - d)*E^(a + c + b*x + d*x)*Hypergeometric2F1[1, (b + d)/(2*d), (3 + b/d)/2, -E^(2*(c + d*x))])/d^2) + (b*E^(a + b*x)*Sech[c + d*x])/(2*d^2) + (E^(a + b*x)*Sech[c + d*x]*Tanh[c + d*x])/(2*d)", //
				5598, 5600);
	}

	// {5602, 2291}
	public void test0416() {
		check(//
				"Integrate[F^(c*(a + b*x))*Sech[d + e*x]^n, x]", //
				"((1 + E^(2*(d + e*x)))^n*F^(a*c + b*c*x)*Hypergeometric2F1[n, (e*n + b*c*Log[F])/(2*e), 1 + (e*n + b*c*Log[F])/(2*e), -E^(2*(d + e*x))]*Sech[d + e*x]^n)/(e*n + b*c*Log[F])", //
				5602, 2291);
	}

	// {5603, 2291}
	public void test0417() {
		check(//
				"Integrate[F^(c*(a + b*x))*Csch[d + e*x]^n, x]", //
				"-(((1 - E^(-2*(d + e*x)))^n*F^(a*c + b*c*x)*Csch[d + e*x]^n*Hypergeometric2F1[n, (e*n - b*c*Log[F])/(2*e), (2 + n - (b*c*Log[F])/e)/2, E^(-2*(d + e*x))])/(e*n - b*c*Log[F]))", //
				5603, 2291);
	}

	// {5604, 5600}
	public void test0418() {
		check(//
				"Integrate[F^(c*(a + b*x))/(f + I*f*Sinh[d + e*x]), x]", //
				"(2*E^((2*d + I*Pi + 2*e*x)/2)*F^(c*(a + b*x))*Hypergeometric2F1[2, 1 + (b*c*Log[F])/e, 2 + (b*c*Log[F])/e, -E^((2*d + I*Pi + 2*e*x)/2)])/(f*(e + b*c*Log[F]))", //
				5604, 5600);
	}

	// {5605, 5600}
	public void test0419() {
		check(//
				"Integrate[F^(c*(a + b*x))/(f + f*Cosh[d + e*x]), x]", //
				"(2*E^(d + e*x)*F^(c*(a + b*x))*Hypergeometric2F1[2, 1 + (b*c*Log[F])/e, 2 + (b*c*Log[F])/e, -E^(d + e*x)])/(f*(e + b*c*Log[F]))", //
				5605, 5600);
	}

	// {5585, 2225}
	public void test0420() {
		check(//
				"Integrate[E^(c + d*x)*Cosh[a + b*x]^2, x]", //
				"(2*b^2*E^(c + d*x))/(d*(4*b^2 - d^2)) - (d*E^(c + d*x)*Cosh[a + b*x]^2)/(4*b^2 - d^2) + (2*b*E^(c + d*x)*Cosh[a + b*x]*Sinh[a + b*x])/(4*b^2 - d^2)", //
				5585, 2225);
	}

	// {5585, 5583}
	public void test0421() {
		check(//
				"Integrate[E^(c + d*x)*Cosh[a + b*x]^3, x]", //
				"(-6*b^2*d*E^(c + d*x)*Cosh[a + b*x])/(9*b^4 - 10*b^2*d^2 + d^4) - (d*E^(c + d*x)*Cosh[a + b*x]^3)/(9*b^2 - d^2) + (6*b^3*E^(c + d*x)*Sinh[a + b*x])/(9*b^4 - 10*b^2*d^2 + d^4) + (3*b*E^(c + d*x)*Cosh[a + b*x]^2*Sinh[a + b*x])/(9*b^2 - d^2)", //
				5585, 5583);
	}

	// {4422, 2225}
	public void test0422() {
		check(//
				"Integrate[E^(n*Cosh[a + b*x])*Sinh[a + b*x], x]", //
				"E^(n*Cosh[a + b*x])/(b*n)", //
				4422, 2225);
	}

	// {4422, 2225}
	public void test0423() {
		check(//
				"Integrate[E^(n*Cosh[a*c + b*c*x])*Sinh[c*(a + b*x)], x]", //
				"E^(n*Cosh[c*(a + b*x)])/(b*c*n)", //
				4422, 2225);
	}

	// {4422, 2225}
	public void test0424() {
		check(//
				"Integrate[E^(n*Cosh[c*(a + b*x)])*Sinh[a*c + b*c*x], x]", //
				"E^(n*Cosh[a*c + b*c*x])/(b*c*n)", //
				4422, 2225);
	}

	// {4426, 2209}
	public void test0425() {
		check(//
				"Integrate[E^(n*Cosh[a + b*x])*Tanh[a + b*x], x]", //
				"ExpIntegralEi[n*Cosh[a + b*x]]/b", //
				4426, 2209);
	}

	// {4426, 2209}
	public void test0426() {
		check(//
				"Integrate[E^(n*Cosh[a*c + b*c*x])*Tanh[c*(a + b*x)], x]", //
				"ExpIntegralEi[n*Cosh[c*(a + b*x)]]/(b*c)", //
				4426, 2209);
	}

	// {4426, 2209}
	public void test0427() {
		check(//
				"Integrate[E^(n*Cosh[c*(a + b*x)])*Tanh[a*c + b*c*x], x]", //
				"ExpIntegralEi[n*Cosh[a*c + b*c*x]]/(b*c)", //
				4426, 2209);
	}

	// {4421, 2225}
	public void test0428() {
		check(//
				"Integrate[E^(n*Sinh[a + b*x])*Cosh[a + b*x], x]", //
				"E^(n*Sinh[a + b*x])/(b*n)", //
				4421, 2225);
	}

	// {4421, 2225}
	public void test0429() {
		check(//
				"Integrate[E^(n*Sinh[a*c + b*c*x])*Cosh[c*(a + b*x)], x]", //
				"E^(n*Sinh[c*(a + b*x)])/(b*c*n)", //
				4421, 2225);
	}

	// {4421, 2225}
	public void test0430() {
		check(//
				"Integrate[E^(n*Sinh[c*(a + b*x)])*Cosh[a*c + b*c*x], x]", //
				"E^(n*Sinh[a*c + b*c*x])/(b*c*n)", //
				4421, 2225);
	}

	// {4425, 2209}
	public void test0431() {
		check(//
				"Integrate[E^(n*Sinh[a + b*x])*Coth[a + b*x], x]", //
				"ExpIntegralEi[n*Sinh[a + b*x]]/b", //
				4425, 2209);
	}

	// {4425, 2209}
	public void test0432() {
		check(//
				"Integrate[E^(n*Sinh[a*c + b*c*x])*Coth[c*(a + b*x)], x]", //
				"ExpIntegralEi[n*Sinh[c*(a + b*x)]]/(b*c)", //
				4425, 2209);
	}

	// {4425, 2209}
	public void test0433() {
		check(//
				"Integrate[E^(n*Sinh[c*(a + b*x)])*Coth[a*c + b*c*x], x]", //
				"ExpIntegralEi[n*Sinh[a*c + b*c*x]]/(b*c)", //
				4425, 2209);
	}

	// {3587, 31}
	public void test0434() {
		check(//
				"Integrate[Sech[x]^2/(a + b*Tanh[x]), x]", //
				"Log[a + b*Tanh[x]]/b", //
				3587, 31);
	}

	// {3756, 209}
	public void test0435() {
		check(//
				"Integrate[Sech[x]^2/(1 + Tanh[x]^2), x]", //
				"ArcTan[Tanh[x]]", //
				3756, 209);
	}

	// {3756, 209}
	public void test0436() {
		check(//
				"Integrate[Sech[x]^2/(9 + Tanh[x]^2), x]", //
				"ArcTan[Tanh[x]/3]/3", //
				3756, 209);
	}

	// {3587, 32}
	public void test0437() {
		check(//
				"Integrate[Sech[x]^2*(a + b*Tanh[x])^n, x]", //
				"(a + b*Tanh[x])^(1 + n)/(b*(1 + n))", //
				3587, 32);
	}

	// {4427, 267}
	public void test0438() {
		check(//
				"Integrate[(Sech[x]^2*Tanh[x]^2)/(2 + Tanh[x]^3)^2, x]", //
				"-1/(3*(2 + Tanh[x]^3))", //
				4427, 267);
	}

	// {3091, 8}
	public void test0439() {
		check(//
				"Integrate[(1 + Cosh[x]^2)*Sech[x]^2, x]", //
				"x + Tanh[x]", //
				3091, 8);
	}

	// {4231, 221}
	public void test0440() {
		check(//
				"Integrate[Sech[x]^2/Sqrt[4 - Sech[x]^2], x]", //
				"ArcSinh[Tanh[x]/Sqrt[3]]", //
				4231, 221);
	}

	// {3756, 222}
	public void test0441() {
		check(//
				"Integrate[Sech[x]^2/Sqrt[1 - 4*Tanh[x]^2], x]", //
				"ArcSin[2*Tanh[x]]/2", //
				3756, 222);
	}

	// {3587, 31}
	public void test0442() {
		check(//
				"Integrate[Csch[x]^2/(a + b*Coth[x]), x]", //
				"-(Log[a + b*Coth[x]]/b)", //
				3587, 31);
	}

	// {3587, 32}
	public void test0443() {
		check(//
				"Integrate[(a + b*Coth[x])^n*Csch[x]^2, x]", //
				"-((a + b*Coth[x])^(1 + n)/(b*(1 + n)))", //
				3587, 32);
	}

	// {3091, 8}
	public void test0444() {
		check(//
				"Integrate[Csch[x]^2*(-1 + Sinh[x]^2), x]", //
				"x + Coth[x]", //
				3091, 8);
	}

	// {3277, 267}
	public void test0445() {
		check(//
				"Integrate[Cosh[x]*Sinh[x]*Sqrt[a + b*Sinh[x]^2], x]", //
				"(a + b*Sinh[x]^2)^(3/2)/(3*b)", //
				3277, 267);
	}

	// {32}
	public void test0446() {
		check(//
				"Integrate[(-Csch[a + b*x] + Sech[a + b*x])/(Csch[a + b*x] + Sech[a + b*x]), x]", //
				"1/(b*(1 + Tanh[a + b*x]))", //
				32);
	}

	// {210}
	public void test0447() {
		check(//
				"Integrate[(-Csch[a + b*x]^2 + Sech[a + b*x]^2)/(Csch[a + b*x]^2 + Sech[a + b*x]^2), x]", //
				"-(ArcTan[Tanh[a + b*x]]/b)", //
				210);
	}

}
