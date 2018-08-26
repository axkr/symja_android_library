package org.matheclipse.core.rubi;

public class SpecialFunctions extends AbstractRubiTestCase {

	public SpecialFunctions(String name) {
		super(name, false);
	}
	
	// {6656}
	public void test0001() {
		check(//
				"Integrate[Derivative[1][f][x], x]", //
				"f[x]", //
				6656);
	}

	// {6656}
	public void test0002() {
		check(//
				"Integrate[Derivative[2][f][x], x]", //
				"Derivative[1][f][x]", //
				6656);
	}

	// {6656}
	public void test0003() {
		check(//
				"Integrate[Derivative[3][f][x], x]", //
				"Derivative[2][f][x]", //
				6656);
	}

	// {6656}
	public void test0004() {
		check(//
				"Integrate[Derivative[n][f][x], x]", //
				"Derivative[-1 + n][f][x]", //
				6656);
	}

	// {6675}
	public void test0007() {
		check(//
				"Integrate[(g[x]*Derivative[1][f][x] - f[x]*Derivative[1][g][x])/g[x]^2, x]", //
				"f[x]/g[x]", //
				6675);
	}

	// {6676}
	public void test0008() {
		check(//
				"Integrate[(g[x]*Derivative[1][f][x] - f[x]*Derivative[1][g][x])/(f[x]*g[x]), x]", //
				"Log[f[x]/g[x]]", //
				6676);
	}

	// {6358}
	public void test0015() {
		check(//
				"Integrate[Erf[b*x]/x, x]", //
				"(2*b*x*HypergeometricPFQ[{1/2, 1/2}, {3/2, 3/2}, -(b^2*x^2)])/Sqrt[Pi]", //
				6358);
	}

	// {6349}
	public void test0016() {
		check(//
				"Integrate[Erf[b*x], x]", //
				"1/(b*E^(b^2*x^2)*Sqrt[Pi]) + x*Erf[b*x]", //
				6349);
	}

	// {6349}
	public void test0017() {
		check(//
				"Integrate[Erf[a + b*x], x]", //
				"1/(b*E^(a + b*x)^2*Sqrt[Pi]) + ((a + b*x)*Erf[a + b*x])/b", //
				6349);
	}

	// {6388}
	public void test0019() {
		check(//
				"Integrate[(E^(c + b^2*x^2)*Erf[b*x])/x, x]", //
				"(2*b*E^c*x*HypergeometricPFQ[{1/2, 1}, {3/2, 3/2}, b^2*x^2])/Sqrt[Pi]", //
				6388);
	}

	// {6376}
	public void test0020() {
		check(//
				"Integrate[E^(c + b^2*x^2)*Erf[b*x], x]", //
				"(b*E^c*x^2*HypergeometricPFQ[{1, 1}, {3/2, 2}, b^2*x^2])/Sqrt[Pi]", //
				6376);
	}

	// {6350}
	public void test0022() {
		check(//
				"Integrate[Erfc[b*x], x]", //
				"-(1/(b*E^(b^2*x^2)*Sqrt[Pi])) + x*Erfc[b*x]", //
				6350);
	}

	// {6350}
	public void test0023() {
		check(//
				"Integrate[Erfc[a + b*x], x]", //
				"-(1/(b*E^(a + b*x)^2*Sqrt[Pi])) + ((a + b*x)*Erfc[a + b*x])/b", //
				6350);
	}

	// {6360}
	public void test0026() {
		check(//
				"Integrate[Erfi[b*x]/x, x]", //
				"(2*b*x*HypergeometricPFQ[{1/2, 1/2}, {3/2, 3/2}, b^2*x^2])/Sqrt[Pi]", //
				6360);
	}

	// {6351}
	public void test0027() {
		check(//
				"Integrate[Erfi[b*x], x]", //
				"-(E^(b^2*x^2)/(b*Sqrt[Pi])) + x*Erfi[b*x]", //
				6351);
	}

	// {6351}
	public void test0028() {
		check(//
				"Integrate[Erfi[a + b*x], x]", //
				"-(E^(a + b*x)^2/(b*Sqrt[Pi])) + ((a + b*x)*Erfi[a + b*x])/b", //
				6351);
	}

	// {6390}
	public void test0030() {
		check(//
				"Integrate[Erfi[b*x]/(E^(b^2*x^2)*x), x]", //
				"(2*b*x*HypergeometricPFQ[{1/2, 1}, {3/2, 3/2}, -(b^2*x^2)])/Sqrt[Pi]", //
				6390);
	}

	// {6378}
	public void test0031() {
		check(//
				"Integrate[Erfi[b*x]/E^(b^2*x^2), x]", //
				"(b*x^2*HypergeometricPFQ[{1, 1}, {3/2, 2}, -(b^2*x^2)])/Sqrt[Pi]", //
				6378);
	}

	// {6418}
	public void test0033() {
		check(//
				"Integrate[FresnelS[b*x], x]", //
				"Cos[(b^2*Pi*x^2)/2]/(b*Pi) + x*FresnelS[b*x]", //
				6418);
	}

	// {6418}
	public void test0034() {
		check(//
				"Integrate[FresnelS[a + b*x], x]", //
				"Cos[(Pi*(a + b*x)^2)/2]/(b*Pi) + ((a + b*x)*FresnelS[a + b*x])/b", //
				6418);
	}

	// {6418}
	public void test0035() {
		check(//
				"Integrate[FresnelS[a + b*x], x]", //
				"Cos[(Pi*(a + b*x)^2)/2]/(b*Pi) + ((a + b*x)*FresnelS[a + b*x])/b", //
				6418);
	}

	// {6446}
	public void test0038() {
		check(//
				"Integrate[Cos[(b^2*Pi*x^2)/2]*FresnelS[b*x], x]", //
				"(FresnelC[b*x]*FresnelS[b*x])/(2*b) - (I/8)*b*x^2*HypergeometricPFQ[{1, 1}, {3/2, 2}, (-I/2)*b^2*Pi*x^2] + (I/8)*b*x^2*HypergeometricPFQ[{1, 1}, {3/2, 2}, (I/2)*b^2*Pi*x^2]", //
				6446);
	}

	// {6419}
	public void test0039() {
		check(//
				"Integrate[FresnelC[b*x], x]", //
				"x*FresnelC[b*x] - Sin[(b^2*Pi*x^2)/2]/(b*Pi)", //
				6419);
	}

	// {6419}
	public void test0040() {
		check(//
				"Integrate[FresnelC[a + b*x], x]", //
				"((a + b*x)*FresnelC[a + b*x])/b - Sin[(Pi*(a + b*x)^2)/2]/(b*Pi)", //
				6419);
	}

	// {6419}
	public void test0041() {
		check(//
				"Integrate[FresnelC[a + b*x], x]", //
				"((a + b*x)*FresnelC[a + b*x])/b - Sin[(Pi*(a + b*x)^2)/2]/(b*Pi)", //
				6419);
	}

	// {6447}
	public void test0044() {
		check(//
				"Integrate[FresnelC[b*x]*Sin[(b^2*Pi*x^2)/2], x]", //
				"(FresnelC[b*x]*FresnelS[b*x])/(2*b) + (I/8)*b*x^2*HypergeometricPFQ[{1, 1}, {3/2, 2}, (-I/2)*b^2*Pi*x^2] - (I/8)*b*x^2*HypergeometricPFQ[{1, 1}, {3/2, 2}, (I/2)*b^2*Pi*x^2]", //
				6447);
	}

	// {6478}
	public void test0045() {
		check(//
				"Integrate[x^2*ExpIntegralE[1, b*x], x]", //
				"-(x^3*ExpIntegralE[-2, b*x])/3 + (x^3*ExpIntegralE[1, b*x])/3", //
				6478);
	}

	// {6478}
	public void test0046() {
		check(//
				"Integrate[x*ExpIntegralE[1, b*x], x]", //
				"-(x^2*ExpIntegralE[-1, b*x])/2 + (x^2*ExpIntegralE[1, b*x])/2", //
				6478);
	}

	// {6473}
	public void test0047() {
		check(//
				"Integrate[ExpIntegralE[1, b*x], x]", //
				"-(ExpIntegralE[2, b*x]/b)", //
				6473);
	}

	// {6475}
	public void test0048() {
		check(//
				"Integrate[ExpIntegralE[1, b*x]/x, x]", //
				"b*x*HypergeometricPFQ[{1, 1, 1}, {2, 2, 2}, -(b*x)] - EulerGamma*Log[x] - Log[b*x]^2/2", //
				6475);
	}

	// {6478}
	public void test0049() {
		check(//
				"Integrate[ExpIntegralE[1, b*x]/x^2, x]", //
				"-(ExpIntegralE[1, b*x]/x) + ExpIntegralE[2, b*x]/x", //
				6478);
	}

	// {6478}
	public void test0050() {
		check(//
				"Integrate[ExpIntegralE[1, b*x]/x^3, x]", //
				"-ExpIntegralE[1, b*x]/(2*x^2) + ExpIntegralE[3, b*x]/(2*x^2)", //
				6478);
	}

	// {6478}
	public void test0051() {
		check(//
				"Integrate[ExpIntegralE[1, b*x]/x^4, x]", //
				"-ExpIntegralE[1, b*x]/(3*x^3) + ExpIntegralE[4, b*x]/(3*x^3)", //
				6478);
	}

	// {6478}
	public void test0052() {
		check(//
				"Integrate[x^2*ExpIntegralE[2, b*x], x]", //
				"-(x^3*ExpIntegralE[-2, b*x])/4 + (x^3*ExpIntegralE[2, b*x])/4", //
				6478);
	}

	// {6478}
	public void test0053() {
		check(//
				"Integrate[x*ExpIntegralE[2, b*x], x]", //
				"-(x^2*ExpIntegralE[-1, b*x])/3 + (x^2*ExpIntegralE[2, b*x])/3", //
				6478);
	}

	// {6473}
	public void test0054() {
		check(//
				"Integrate[ExpIntegralE[2, b*x], x]", //
				"-(ExpIntegralE[3, b*x]/b)", //
				6473);
	}

	// {6478}
	public void test0055() {
		check(//
				"Integrate[ExpIntegralE[2, b*x]/x, x]", //
				"-ExpIntegralE[1, b*x] + ExpIntegralE[2, b*x]", //
				6478);
	}

	// {6478}
	public void test0056() {
		check(//
				"Integrate[ExpIntegralE[2, b*x]/x^3, x]", //
				"-(ExpIntegralE[2, b*x]/x^2) + ExpIntegralE[3, b*x]/x^2", //
				6478);
	}

	// {6478}
	public void test0057() {
		check(//
				"Integrate[ExpIntegralE[2, b*x]/x^4, x]", //
				"-ExpIntegralE[2, b*x]/(2*x^3) + ExpIntegralE[4, b*x]/(2*x^3)", //
				6478);
	}

	// {6478}
	public void test0058() {
		check(//
				"Integrate[ExpIntegralE[2, b*x]/x^5, x]", //
				"-ExpIntegralE[2, b*x]/(3*x^4) + ExpIntegralE[5, b*x]/(3*x^4)", //
				6478);
	}

	// {6478}
	public void test0059() {
		check(//
				"Integrate[x^2*ExpIntegralE[3, b*x], x]", //
				"-(x^3*ExpIntegralE[-2, b*x])/5 + (x^3*ExpIntegralE[3, b*x])/5", //
				6478);
	}

	// {6478}
	public void test0060() {
		check(//
				"Integrate[x*ExpIntegralE[3, b*x], x]", //
				"-(x^2*ExpIntegralE[-1, b*x])/4 + (x^2*ExpIntegralE[3, b*x])/4", //
				6478);
	}

	// {6473}
	public void test0061() {
		check(//
				"Integrate[ExpIntegralE[3, b*x], x]", //
				"-(ExpIntegralE[4, b*x]/b)", //
				6473);
	}

	// {6478}
	public void test0062() {
		check(//
				"Integrate[ExpIntegralE[3, b*x]/x, x]", //
				"-ExpIntegralE[1, b*x]/2 + ExpIntegralE[3, b*x]/2", //
				6478);
	}

	// {6478}
	public void test0063() {
		check(//
				"Integrate[ExpIntegralE[3, b*x]/x^2, x]", //
				"-(ExpIntegralE[2, b*x]/x) + ExpIntegralE[3, b*x]/x", //
				6478);
	}

	// {6478}
	public void test0064() {
		check(//
				"Integrate[ExpIntegralE[3, b*x]/x^4, x]", //
				"-(ExpIntegralE[3, b*x]/x^3) + ExpIntegralE[4, b*x]/x^3", //
				6478);
	}

	// {6478}
	public void test0065() {
		check(//
				"Integrate[ExpIntegralE[3, b*x]/x^5, x]", //
				"-ExpIntegralE[3, b*x]/(2*x^4) + ExpIntegralE[5, b*x]/(2*x^4)", //
				6478);
	}

	// {6478}
	public void test0066() {
		check(//
				"Integrate[ExpIntegralE[3, b*x]/x^6, x]", //
				"-ExpIntegralE[3, b*x]/(3*x^5) + ExpIntegralE[6, b*x]/(3*x^5)", //
				6478);
	}

	// {6478}
	public void test0067() {
		check(//
				"Integrate[x^3*ExpIntegralE[-1, b*x], x]", //
				"-(x^4*ExpIntegralE[-3, b*x])/2 + (x^4*ExpIntegralE[-1, b*x])/2", //
				6478);
	}

	// {6478}
	public void test0068() {
		check(//
				"Integrate[x^2*ExpIntegralE[-1, b*x], x]", //
				"-(x^3*ExpIntegralE[-2, b*x]) + x^3*ExpIntegralE[-1, b*x]", //
				6478);
	}

	// {6473}
	public void test0069() {
		check(//
				"Integrate[ExpIntegralE[-1, b*x], x]", //
				"-(1/(b^2*E^(b*x)*x))", //
				6473);
	}

	// {6478}
	public void test0070() {
		check(//
				"Integrate[ExpIntegralE[-1, b*x]/x, x]", //
				"-ExpIntegralE[-1, b*x]/2 + ExpIntegralE[1, b*x]/2", //
				6478);
	}

	// {6478}
	public void test0071() {
		check(//
				"Integrate[ExpIntegralE[-1, b*x]/x^2, x]", //
				"-ExpIntegralE[-1, b*x]/(3*x) + ExpIntegralE[2, b*x]/(3*x)", //
				6478);
	}

	// {6478}
	public void test0072() {
		check(//
				"Integrate[ExpIntegralE[-1, b*x]/x^3, x]", //
				"-ExpIntegralE[-1, b*x]/(4*x^2) + ExpIntegralE[3, b*x]/(4*x^2)", //
				6478);
	}

	// {6478}
	public void test0073() {
		check(//
				"Integrate[x^4*ExpIntegralE[-2, b*x], x]", //
				"-(x^5*ExpIntegralE[-4, b*x])/2 + (x^5*ExpIntegralE[-2, b*x])/2", //
				6478);
	}

	// {6478}
	public void test0074() {
		check(//
				"Integrate[x^3*ExpIntegralE[-2, b*x], x]", //
				"-(x^4*ExpIntegralE[-3, b*x]) + x^4*ExpIntegralE[-2, b*x]", //
				6478);
	}

	// {6478}
	public void test0075() {
		check(//
				"Integrate[x*ExpIntegralE[-2, b*x], x]", //
				"-(x^2*ExpIntegralE[-2, b*x]) + x^2*ExpIntegralE[-1, b*x]", //
				6478);
	}

	// {6473}
	public void test0076() {
		check(//
				"Integrate[ExpIntegralE[-1, b*x], x]", //
				"-(1/(b^2*E^(b*x)*x))", //
				6473);
	}

	// {6478}
	public void test0077() {
		check(//
				"Integrate[ExpIntegralE[-2, b*x]/x, x]", //
				"-ExpIntegralE[-2, b*x]/3 + ExpIntegralE[1, b*x]/3", //
				6478);
	}

	// {6478}
	public void test0078() {
		check(//
				"Integrate[ExpIntegralE[-2, b*x]/x^2, x]", //
				"-ExpIntegralE[-2, b*x]/(4*x) + ExpIntegralE[2, b*x]/(4*x)", //
				6478);
	}

	// {6478}
	public void test0079() {
		check(//
				"Integrate[ExpIntegralE[-2, b*x]/x^3, x]", //
				"-ExpIntegralE[-2, b*x]/(5*x^2) + ExpIntegralE[3, b*x]/(5*x^2)", //
				6478);
	}

	// {6478}
	public void test0080() {
		check(//
				"Integrate[x^5*ExpIntegralE[-3, b*x], x]", //
				"-(x^6*ExpIntegralE[-5, b*x])/2 + (x^6*ExpIntegralE[-3, b*x])/2", //
				6478);
	}

	// {6478}
	public void test0081() {
		check(//
				"Integrate[x^4*ExpIntegralE[-3, b*x], x]", //
				"-(x^5*ExpIntegralE[-4, b*x]) + x^5*ExpIntegralE[-3, b*x]", //
				6478);
	}

	// {6478}
	public void test0082() {
		check(//
				"Integrate[x^2*ExpIntegralE[-3, b*x], x]", //
				"-(x^3*ExpIntegralE[-3, b*x]) + x^3*ExpIntegralE[-2, b*x]", //
				6478);
	}

	// {6478}
	public void test0083() {
		check(//
				"Integrate[x*ExpIntegralE[-3, b*x], x]", //
				"-(x^2*ExpIntegralE[-3, b*x])/2 + (x^2*ExpIntegralE[-1, b*x])/2", //
				6478);
	}

	// {6473}
	public void test0084() {
		check(//
				"Integrate[ExpIntegralE[-1, b*x], x]", //
				"-(1/(b^2*E^(b*x)*x))", //
				6473);
	}

	// {6478}
	public void test0085() {
		check(//
				"Integrate[ExpIntegralE[-3, b*x]/x, x]", //
				"-ExpIntegralE[-3, b*x]/4 + ExpIntegralE[1, b*x]/4", //
				6478);
	}

	// {6478}
	public void test0086() {
		check(//
				"Integrate[ExpIntegralE[-3, b*x]/x^2, x]", //
				"-ExpIntegralE[-3, b*x]/(5*x) + ExpIntegralE[2, b*x]/(5*x)", //
				6478);
	}

	// {6478}
	public void test0087() {
		check(//
				"Integrate[ExpIntegralE[-3, b*x]/x^3, x]", //
				"-ExpIntegralE[-3, b*x]/(6*x^2) + ExpIntegralE[3, b*x]/(6*x^2)", //
				6478);
	}

	// {6475}
	public void test0088() {
		check(//
				"Integrate[ExpIntegralE[1, b*x]/x, x]", //
				"b*x*HypergeometricPFQ[{1, 1, 1}, {2, 2, 2}, -(b*x)] - EulerGamma*Log[x] - Log[b*x]^2/2", //
				6475);
	}

	// {6477}
	public void test0089() {
		check(//
				"Integrate[(d*x)^(3/2)*ExpIntegralE[-3/2, b*x], x]", //
				"(-4*(d*x)^(5/2)*HypergeometricPFQ[{5/2, 5/2}, {7/2, 7/2}, -(b*x)])/(25*d) + (3*Sqrt[Pi]*(d*x)^(3/2)*Log[x])/(4*b*(b*x)^(3/2))", //
				6477);
	}

	// {6477}
	public void test0090() {
		check(//
				"Integrate[Sqrt[d*x]*ExpIntegralE[-1/2, b*x], x]", //
				"(-4*(d*x)^(3/2)*HypergeometricPFQ[{3/2, 3/2}, {5/2, 5/2}, -(b*x)])/(9*d) + (Sqrt[Pi]*Sqrt[d*x]*Log[x])/(2*b*Sqrt[b*x])", //
				6477);
	}

	// {6477}
	public void test0091() {
		check(//
				"Integrate[ExpIntegralE[1/2, b*x]/Sqrt[d*x], x]", //
				"(-4*Sqrt[d*x]*HypergeometricPFQ[{1/2, 1/2}, {3/2, 3/2}, -(b*x)])/d + (Sqrt[Pi]*Sqrt[b*x]*Log[x])/(b*Sqrt[d*x])", //
				6477);
	}

	// {6477}
	public void test0092() {
		check(//
				"Integrate[ExpIntegralE[3/2, b*x]/(d*x)^(3/2), x]", //
				"(-4*HypergeometricPFQ[{-1/2, -1/2}, {1/2, 1/2}, -(b*x)])/(d*Sqrt[d*x]) - (2*Sqrt[Pi]*(b*x)^(3/2)*Log[x])/(b*(d*x)^(3/2))", //
				6477);
	}

	// {6477}
	public void test0093() {
		check(//
				"Integrate[ExpIntegralE[5/2, b*x]/(d*x)^(5/2), x]", //
				"(-4*HypergeometricPFQ[{-3/2, -3/2}, {-1/2, -1/2}, -(b*x)])/(9*d*(d*x)^(3/2)) + (4*Sqrt[Pi]*(b*x)^(5/2)*Log[x])/(3*b*(d*x)^(5/2))", //
				6477);
	}

	// {6478}
	public void test0094() {
		check(//
				"Integrate[x^m*ExpIntegralE[n, x], x]", //
				"-((x^(1 + m)*ExpIntegralE[-m, x])/(m + n)) + (x^(1 + m)*ExpIntegralE[n, x])/(m + n)", //
				6478);
	}

	// {6478}
	public void test0095() {
		check(//
				"Integrate[x^m*ExpIntegralE[n, b*x], x]", //
				"-((x^(1 + m)*ExpIntegralE[-m, b*x])/(m + n)) + (x^(1 + m)*ExpIntegralE[n, b*x])/(m + n)", //
				6478);
	}

	// {6478}
	public void test0096() {
		check(//
				"Integrate[(d*x)^m*ExpIntegralE[n, x], x]", //
				"-(((d*x)^(1 + m)*ExpIntegralE[-m, x])/(d*(m + n))) + ((d*x)^(1 + m)*ExpIntegralE[n, x])/(d*(m + n))", //
				6478);
	}

	// {6478}
	public void test0097() {
		check(//
				"Integrate[(d*x)^m*ExpIntegralE[n, b*x], x]", //
				"-(((d*x)^(1 + m)*ExpIntegralE[-m, b*x])/(d*(m + n))) + ((d*x)^(1 + m)*ExpIntegralE[n, b*x])/(d*(m + n))", //
				6478);
	}

	// {6477}
	public void test0098() {
		check(//
				"Integrate[ExpIntegralE[n, x]/x^n, x]", //
				"-((x^(1 - n)*HypergeometricPFQ[{1 - n, 1 - n}, {2 - n, 2 - n}, -x])/(1 - n)^2) + Gamma[1 - n]*Log[x]", //
				6477);
	}

	// {6477}
	public void test0099() {
		check(//
				"Integrate[ExpIntegralE[n, b*x]/x^n, x]", //
				"-((x^(1 - n)*HypergeometricPFQ[{1 - n, 1 - n}, {2 - n, 2 - n}, -(b*x)])/(1 - n)^2) + ((b*x)^n*Gamma[1 - n]*Log[x])/(b*x^n)", //
				6477);
	}

	// {6477}
	public void test0100() {
		check(//
				"Integrate[ExpIntegralE[n, x]/(d*x)^n, x]", //
				"-(((d*x)^(1 - n)*HypergeometricPFQ[{1 - n, 1 - n}, {2 - n, 2 - n}, -x])/(d*(1 - n)^2)) + (x^n*Gamma[1 - n]*Log[x])/(d*x)^n", //
				6477);
	}

	// {6477}
	public void test0101() {
		check(//
				"Integrate[ExpIntegralE[n, b*x]/(d*x)^n, x]", //
				"-(((d*x)^(1 - n)*HypergeometricPFQ[{1 - n, 1 - n}, {2 - n, 2 - n}, -(b*x)])/(d*(1 - n)^2)) + ((b*x)^n*Gamma[1 - n]*Log[x])/(b*(d*x)^n)", //
				6477);
	}

	// {6478}
	public void test0102() {
		check(//
				"Integrate[x^2*ExpIntegralE[n, b*x], x]", //
				"-((x^3*ExpIntegralE[-2, b*x])/(2 + n)) + (x^3*ExpIntegralE[n, b*x])/(2 + n)", //
				6478);
	}

	// {6478}
	public void test0103() {
		check(//
				"Integrate[x*ExpIntegralE[n, b*x], x]", //
				"-((x^2*ExpIntegralE[-1, b*x])/(1 + n)) + (x^2*ExpIntegralE[n, b*x])/(1 + n)", //
				6478);
	}

	// {6473}
	public void test0104() {
		check(//
				"Integrate[ExpIntegralE[n, b*x], x]", //
				"-(ExpIntegralE[1 + n, b*x]/b)", //
				6473);
	}

	// {6478}
	public void test0105() {
		check(//
				"Integrate[ExpIntegralE[n, b*x]/x, x]", //
				"ExpIntegralE[1, b*x]/(1 - n) - ExpIntegralE[n, b*x]/(1 - n)", //
				6478);
	}

	// {6478}
	public void test0106() {
		check(//
				"Integrate[ExpIntegralE[n, b*x]/x^2, x]", //
				"ExpIntegralE[2, b*x]/((2 - n)*x) - ExpIntegralE[n, b*x]/((2 - n)*x)", //
				6478);
	}

	// {6478}
	public void test0107() {
		check(//
				"Integrate[ExpIntegralE[n, b*x]/x^3, x]", //
				"ExpIntegralE[3, b*x]/((3 - n)*x^2) - ExpIntegralE[n, b*x]/((3 - n)*x^2)", //
				6478);
	}

	// {6473}
	public void test0108() {
		check(//
				"Integrate[ExpIntegralE[1, a + b*x], x]", //
				"-(ExpIntegralE[2, a + b*x]/b)", //
				6473);
	}

	// {6473}
	public void test0109() {
		check(//
				"Integrate[ExpIntegralE[2, a + b*x], x]", //
				"-(ExpIntegralE[3, a + b*x]/b)", //
				6473);
	}

	// {6473}
	public void test0111() {
		check(//
				"Integrate[ExpIntegralE[3, a + b*x], x]", //
				"-(ExpIntegralE[4, a + b*x]/b)", //
				6473);
	}

	// {6473}
	public void test0113() {
		check(//
				"Integrate[ExpIntegralE[-1, a + b*x], x]", //
				"-(E^(-a - b*x)/(b*(a + b*x)))", //
				6473);
	}

	// {6473}
	public void test0114() {
		check(//
				"Integrate[ExpIntegralE[-2, a + b*x], x]", //
				"-(ExpIntegralE[-1, a + b*x]/b)", //
				6473);
	}

	// {6473}
	public void test0115() {
		check(//
				"Integrate[ExpIntegralE[-3, a + b*x], x]", //
				"-(ExpIntegralE[-2, a + b*x]/b)", //
				6473);
	}

	// {6473}
	public void test0118() {
		check(//
				"Integrate[ExpIntegralE[n, a + b*x], x]", //
				"-(ExpIntegralE[1 + n, a + b*x]/b)", //
				6473);
	}

	// {6482}
	public void test0119() {
		check(//
				"Integrate[ExpIntegralEi[b*x], x]", //
				"-(E^(b*x)/b) + x*ExpIntegralEi[b*x]", //
				6482);
	}

	// {6482}
	public void test0120() {
		check(//
				"Integrate[ExpIntegralEi[a + b*x], x]", //
				"-(E^(a + b*x)/b) + ((a + b*x)*ExpIntegralEi[a + b*x])/b", //
				6482);
	}

	// {6686}
	public void test0123() {
		check(//
				"Integrate[(E^(b*x)*ExpIntegralEi[b*x])/x, x]", //
				"ExpIntegralEi[b*x]^2/2", //
				6686);
	}

	// {6495}
	public void test0124() {
		check(//
				"Integrate[LogIntegral[b*x], x]", //
				"-(ExpIntegralEi[2*Log[b*x]]/b) + x*LogIntegral[b*x]", //
				6495);
	}

	// {6496}
	public void test0125() {
		check(//
				"Integrate[LogIntegral[b*x]/x, x]", //
				"-(b*x) + Log[b*x]*LogIntegral[b*x]", //
				6496);
	}

	// {6495}
	public void test0126() {
		check(//
				"Integrate[LogIntegral[a + b*x], x]", //
				"-(ExpIntegralEi[2*Log[a + b*x]]/b) + ((a + b*x)*LogIntegral[a + b*x])/b", //
				6495);
	}

	// {6499}
	public void test0129() {
		check(//
				"Integrate[SinIntegral[b*x], x]", //
				"Cos[b*x]/b + x*SinIntegral[b*x]", //
				6499);
	}

	// {6501}
	public void test0130() {
		check(//
				"Integrate[SinIntegral[b*x]/x, x]", //
				"(b*x*HypergeometricPFQ[{1, 1, 1}, {2, 2, 2}, (-I)*b*x])/2 + (b*x*HypergeometricPFQ[{1, 1, 1}, {2, 2, 2}, I*b*x])/2", //
				6501);
	}

	// {6499}
	public void test0132() {
		check(//
				"Integrate[SinIntegral[a + b*x], x]", //
				"Cos[a + b*x]/b + ((a + b*x)*SinIntegral[a + b*x])/b", //
				6499);
	}

	// {6686}
	public void test0133() {
		check(//
				"Integrate[(Sin[b*x]*SinIntegral[b*x])/x, x]", //
				"SinIntegral[b*x]^2/2", //
				6686);
	}

	// {6500}
	public void test0134() {
		check(//
				"Integrate[CosIntegral[b*x], x]", //
				"x*CosIntegral[b*x] - Sin[b*x]/b", //
				6500);
	}

	// {6502}
	public void test0135() {
		check(//
				"Integrate[CosIntegral[b*x]/x, x]", //
				"(-I/2)*b*x*HypergeometricPFQ[{1, 1, 1}, {2, 2, 2}, (-I)*b*x] + (I/2)*b*x*HypergeometricPFQ[{1, 1, 1}, {2, 2, 2}, I*b*x] + EulerGamma*Log[x] + Log[b*x]^2/2", //
				6502);
	}

	// {6500}
	public void test0137() {
		check(//
				"Integrate[CosIntegral[a + b*x], x]", //
				"((a + b*x)*CosIntegral[a + b*x])/b - Sin[a + b*x]/b", //
				6500);
	}

	// {6686}
	public void test0138() {
		check(//
				"Integrate[(Cos[b*x]*CosIntegral[b*x])/x, x]", //
				"CosIntegral[b*x]^2/2", //
				6686);
	}

	// {6528}
	public void test0139() {
		check(//
				"Integrate[SinhIntegral[b*x], x]", //
				"-(Cosh[b*x]/b) + x*SinhIntegral[b*x]", //
				6528);
	}

	// {6530}
	public void test0140() {
		check(//
				"Integrate[SinhIntegral[b*x]/x, x]", //
				"(b*x*HypergeometricPFQ[{1, 1, 1}, {2, 2, 2}, -(b*x)])/2 + (b*x*HypergeometricPFQ[{1, 1, 1}, {2, 2, 2}, b*x])/2", //
				6530);
	}

	// {6528}
	public void test0142() {
		check(//
				"Integrate[SinhIntegral[a + b*x], x]", //
				"-(Cosh[a + b*x]/b) + ((a + b*x)*SinhIntegral[a + b*x])/b", //
				6528);
	}

	// {6686}
	public void test0143() {
		check(//
				"Integrate[(Sinh[b*x]*SinhIntegral[b*x])/x, x]", //
				"SinhIntegral[b*x]^2/2", //
				6686);
	}

	// {6529}
	public void test0144() {
		check(//
				"Integrate[CoshIntegral[b*x], x]", //
				"x*CoshIntegral[b*x] - Sinh[b*x]/b", //
				6529);
	}

	// {6531}
	public void test0145() {
		check(//
				"Integrate[CoshIntegral[b*x]/x, x]", //
				"-(b*x*HypergeometricPFQ[{1, 1, 1}, {2, 2, 2}, -(b*x)])/2 + (b*x*HypergeometricPFQ[{1, 1, 1}, {2, 2, 2}, b*x])/2 + EulerGamma*Log[x] + Log[b*x]^2/2", //
				6531);
	}

	// {6529}
	public void test0147() {
		check(//
				"Integrate[CoshIntegral[a + b*x], x]", //
				"((a + b*x)*CoshIntegral[a + b*x])/b - Sinh[a + b*x]/b", //
				6529);
	}

	// {6686}
	public void test0148() {
		check(//
				"Integrate[(Cosh[b*x]*CoshIntegral[b*x])/x, x]", //
				"CoshIntegral[b*x]^2/2", //
				6686);
	}

	// {6562}
	public void test0149() {
		check(//
				"Integrate[x^100*Gamma[0, a*x], x]", //
				"(x^101*Gamma[0, a*x])/101 - Gamma[101, a*x]/(101*a^101)", //
				6562);
	}

	// {6562}
	public void test0150() {
		check(//
				"Integrate[x^2*Gamma[0, a*x], x]", //
				"(x^3*Gamma[0, a*x])/3 - Gamma[3, a*x]/(3*a^3)", //
				6562);
	}

	// {6562}
	public void test0151() {
		check(//
				"Integrate[x*Gamma[0, a*x], x]", //
				"(x^2*Gamma[0, a*x])/2 - Gamma[2, a*x]/(2*a^2)", //
				6562);
	}

	// {6557}
	public void test0152() {
		check(//
				"Integrate[Gamma[0, a*x], x]", //
				"-(1/(a*E^(a*x))) + x*Gamma[0, a*x]", //
				6557);
	}

	// {6558}
	public void test0153() {
		check(//
				"Integrate[Gamma[0, a*x]/x, x]", //
				"a*x*HypergeometricPFQ[{1, 1, 1}, {2, 2, 2}, -(a*x)] - EulerGamma*Log[x] - Log[a*x]^2/2", //
				6558);
	}

	// {6562}
	public void test0154() {
		check(//
				"Integrate[Gamma[0, a*x]/x^2, x]", //
				"a*Gamma[-1, a*x] - Gamma[0, a*x]/x", //
				6562);
	}

	// {6562}
	public void test0155() {
		check(//
				"Integrate[Gamma[0, a*x]/x^3, x]", //
				"(a^2*Gamma[-2, a*x])/2 - Gamma[0, a*x]/(2*x^2)", //
				6562);
	}

	// {6562}
	public void test0156() {
		check(//
				"Integrate[Gamma[0, a*x]/x^4, x]", //
				"(a^3*Gamma[-3, a*x])/3 - Gamma[0, a*x]/(3*x^3)", //
				6562);
	}

	// {2194}
	public void test0157() {
		check(//
				"Integrate[E^(-(a*x)), x]", //
				"-(1/(a*E^(a*x)))", //
				2194);
	}

	// {2178}
	public void test0158() {
		check(//
				"Integrate[1/(E^(a*x)*x), x]", //
				"ExpIntegralEi[-(a*x)]", //
				2178);
	}

	// {6562}
	public void test0159() {
		check(//
				"Integrate[x^100*Gamma[2, a*x], x]", //
				"(x^101*Gamma[2, a*x])/101 - Gamma[103, a*x]/(101*a^101)", //
				6562);
	}

	// {6562}
	public void test0160() {
		check(//
				"Integrate[x^2*Gamma[2, a*x], x]", //
				"(x^3*Gamma[2, a*x])/3 - Gamma[5, a*x]/(3*a^3)", //
				6562);
	}

	// {6562}
	public void test0161() {
		check(//
				"Integrate[x*Gamma[2, a*x], x]", //
				"(x^2*Gamma[2, a*x])/2 - Gamma[4, a*x]/(2*a^2)", //
				6562);
	}

	// {6557}
	public void test0162() {
		check(//
				"Integrate[Gamma[2, a*x], x]", //
				"x*Gamma[2, a*x] - Gamma[3, a*x]/a", //
				6557);
	}

	// {6562}
	public void test0163() {
		check(//
				"Integrate[Gamma[2, a*x]/x^2, x]", //
				"a/E^(a*x) - Gamma[2, a*x]/x", //
				6562);
	}

	// {6562}
	public void test0164() {
		check(//
				"Integrate[Gamma[2, a*x]/x^3, x]", //
				"(a^2*Gamma[0, a*x])/2 - Gamma[2, a*x]/(2*x^2)", //
				6562);
	}

	// {6562}
	public void test0165() {
		check(//
				"Integrate[Gamma[2, a*x]/x^4, x]", //
				"(a^3*Gamma[-1, a*x])/3 - Gamma[2, a*x]/(3*x^3)", //
				6562);
	}

	// {6562}
	public void test0166() {
		check(//
				"Integrate[x^100*Gamma[3, a*x], x]", //
				"(x^101*Gamma[3, a*x])/101 - Gamma[104, a*x]/(101*a^101)", //
				6562);
	}

	// {6562}
	public void test0167() {
		check(//
				"Integrate[x^2*Gamma[3, a*x], x]", //
				"(x^3*Gamma[3, a*x])/3 - Gamma[6, a*x]/(3*a^3)", //
				6562);
	}

	// {6562}
	public void test0168() {
		check(//
				"Integrate[x*Gamma[3, a*x], x]", //
				"(x^2*Gamma[3, a*x])/2 - Gamma[5, a*x]/(2*a^2)", //
				6562);
	}

	// {6557}
	public void test0169() {
		check(//
				"Integrate[Gamma[3, a*x], x]", //
				"x*Gamma[3, a*x] - Gamma[4, a*x]/a", //
				6557);
	}

	// {6562}
	public void test0170() {
		check(//
				"Integrate[Gamma[3, a*x]/x^2, x]", //
				"a*Gamma[2, a*x] - Gamma[3, a*x]/x", //
				6562);
	}

	// {6562}
	public void test0171() {
		check(//
				"Integrate[Gamma[3, a*x]/x^3, x]", //
				"a^2/(2*E^(a*x)) - Gamma[3, a*x]/(2*x^2)", //
				6562);
	}

	// {6562}
	public void test0172() {
		check(//
				"Integrate[Gamma[3, a*x]/x^4, x]", //
				"(a^3*Gamma[0, a*x])/3 - Gamma[3, a*x]/(3*x^3)", //
				6562);
	}

	// {6562}
	public void test0173() {
		check(//
				"Integrate[x^100*Gamma[-1, a*x], x]", //
				"(x^101*Gamma[-1, a*x])/101 - Gamma[100, a*x]/(101*a^101)", //
				6562);
	}

	// {6562}
	public void test0174() {
		check(//
				"Integrate[x^3*Gamma[-1, a*x], x]", //
				"(x^4*Gamma[-1, a*x])/4 - Gamma[3, a*x]/(4*a^4)", //
				6562);
	}

	// {6562}
	public void test0175() {
		check(//
				"Integrate[x^2*Gamma[-1, a*x], x]", //
				"(x^3*Gamma[-1, a*x])/3 - Gamma[2, a*x]/(3*a^3)", //
				6562);
	}

	// {6562}
	public void test0176() {
		check(//
				"Integrate[x*Gamma[-1, a*x], x]", //
				"-1/(2*a^2*E^(a*x)) + (x^2*Gamma[-1, a*x])/2", //
				6562);
	}

	// {6557}
	public void test0177() {
		check(//
				"Integrate[Gamma[-1, a*x], x]", //
				"x*Gamma[-1, a*x] - Gamma[0, a*x]/a", //
				6557);
	}

	// {6562}
	public void test0178() {
		check(//
				"Integrate[Gamma[-1, a*x]/x^2, x]", //
				"a*Gamma[-2, a*x] - Gamma[-1, a*x]/x", //
				6562);
	}

	// {6562}
	public void test0179() {
		check(//
				"Integrate[Gamma[-1, a*x]/x^3, x]", //
				"(a^2*Gamma[-3, a*x])/2 - Gamma[-1, a*x]/(2*x^2)", //
				6562);
	}

	// {6562}
	public void test0180() {
		check(//
				"Integrate[Gamma[-1, a*x]/x^4, x]", //
				"(a^3*Gamma[-4, a*x])/3 - Gamma[-1, a*x]/(3*x^3)", //
				6562);
	}

	// {6562}
	public void test0181() {
		check(//
				"Integrate[x^100*Gamma[-2, a*x], x]", //
				"(x^101*Gamma[-2, a*x])/101 - Gamma[99, a*x]/(101*a^101)", //
				6562);
	}

	// {6562}
	public void test0182() {
		check(//
				"Integrate[x^3*Gamma[-2, a*x], x]", //
				"(x^4*Gamma[-2, a*x])/4 - Gamma[2, a*x]/(4*a^4)", //
				6562);
	}

	// {6562}
	public void test0183() {
		check(//
				"Integrate[x^2*Gamma[-2, a*x], x]", //
				"-1/(3*a^3*E^(a*x)) + (x^3*Gamma[-2, a*x])/3", //
				6562);
	}

	// {6562}
	public void test0184() {
		check(//
				"Integrate[x*Gamma[-2, a*x], x]", //
				"(x^2*Gamma[-2, a*x])/2 - Gamma[0, a*x]/(2*a^2)", //
				6562);
	}

	// {6557}
	public void test0185() {
		check(//
				"Integrate[Gamma[-2, a*x], x]", //
				"x*Gamma[-2, a*x] - Gamma[-1, a*x]/a", //
				6557);
	}

	// {6562}
	public void test0186() {
		check(//
				"Integrate[Gamma[-2, a*x]/x^2, x]", //
				"a*Gamma[-3, a*x] - Gamma[-2, a*x]/x", //
				6562);
	}

	// {6562}
	public void test0187() {
		check(//
				"Integrate[Gamma[-2, a*x]/x^3, x]", //
				"(a^2*Gamma[-4, a*x])/2 - Gamma[-2, a*x]/(2*x^2)", //
				6562);
	}

	// {6562}
	public void test0188() {
		check(//
				"Integrate[Gamma[-2, a*x]/x^4, x]", //
				"(a^3*Gamma[-5, a*x])/3 - Gamma[-2, a*x]/(3*x^3)", //
				6562);
	}

	// {6562}
	public void test0189() {
		check(//
				"Integrate[x^100*Gamma[-3, a*x], x]", //
				"(x^101*Gamma[-3, a*x])/101 - Gamma[98, a*x]/(101*a^101)", //
				6562);
	}

	// {6562}
	public void test0190() {
		check(//
				"Integrate[x^3*Gamma[-3, a*x], x]", //
				"-1/(4*a^4*E^(a*x)) + (x^4*Gamma[-3, a*x])/4", //
				6562);
	}

	// {6562}
	public void test0191() {
		check(//
				"Integrate[x^2*Gamma[-3, a*x], x]", //
				"(x^3*Gamma[-3, a*x])/3 - Gamma[0, a*x]/(3*a^3)", //
				6562);
	}

	// {6562}
	public void test0192() {
		check(//
				"Integrate[x*Gamma[-3, a*x], x]", //
				"(x^2*Gamma[-3, a*x])/2 - Gamma[-1, a*x]/(2*a^2)", //
				6562);
	}

	// {6557}
	public void test0193() {
		check(//
				"Integrate[Gamma[-3, a*x], x]", //
				"x*Gamma[-3, a*x] - Gamma[-2, a*x]/a", //
				6557);
	}

	// {6562}
	public void test0194() {
		check(//
				"Integrate[Gamma[-3, a*x]/x^2, x]", //
				"a*Gamma[-4, a*x] - Gamma[-3, a*x]/x", //
				6562);
	}

	// {6562}
	public void test0195() {
		check(//
				"Integrate[Gamma[-3, a*x]/x^3, x]", //
				"(a^2*Gamma[-5, a*x])/2 - Gamma[-3, a*x]/(2*x^2)", //
				6562);
	}

	// {6562}
	public void test0196() {
		check(//
				"Integrate[Gamma[-3, a*x]/x^4, x]", //
				"(a^3*Gamma[-6, a*x])/3 - Gamma[-3, a*x]/(3*x^3)", //
				6562);
	}

	// {6562}
	public void test0197() {
		check(//
				"Integrate[x^100*Gamma[1/2, a*x], x]", //
				"(x^101*Gamma[1/2, a*x])/101 - Gamma[203/2, a*x]/(101*a^101)", //
				6562);
	}

	// {6562}
	public void test0198() {
		check(//
				"Integrate[x^2*Gamma[1/2, a*x], x]", //
				"(x^3*Gamma[1/2, a*x])/3 - Gamma[7/2, a*x]/(3*a^3)", //
				6562);
	}

	// {6562}
	public void test0199() {
		check(//
				"Integrate[x*Gamma[1/2, a*x], x]", //
				"(x^2*Gamma[1/2, a*x])/2 - Gamma[5/2, a*x]/(2*a^2)", //
				6562);
	}

	// {6557}
	public void test0200() {
		check(//
				"Integrate[Gamma[1/2, a*x], x]", //
				"x*Gamma[1/2, a*x] - Gamma[3/2, a*x]/a", //
				6557);
	}

	// {6561}
	public void test0201() {
		check(//
				"Integrate[Gamma[1/2, a*x]/x, x]", //
				"-4*Sqrt[a*x]*HypergeometricPFQ[{1/2, 1/2}, {3/2, 3/2}, -(a*x)] + Sqrt[Pi]*Log[x]", //
				6561);
	}

	// {6562}
	public void test0202() {
		check(//
				"Integrate[Gamma[1/2, a*x]/x^2, x]", //
				"a*Gamma[-1/2, a*x] - Gamma[1/2, a*x]/x", //
				6562);
	}

	// {6562}
	public void test0203() {
		check(//
				"Integrate[Gamma[1/2, a*x]/x^3, x]", //
				"(a^2*Gamma[-3/2, a*x])/2 - Gamma[1/2, a*x]/(2*x^2)", //
				6562);
	}

	// {6562}
	public void test0204() {
		check(//
				"Integrate[Gamma[1/2, a*x]/x^4, x]", //
				"(a^3*Gamma[-5/2, a*x])/3 - Gamma[1/2, a*x]/(3*x^3)", //
				6562);
	}

	// {6562}
	public void test0205() {
		check(//
				"Integrate[x^100*Gamma[3/2, a*x], x]", //
				"(x^101*Gamma[3/2, a*x])/101 - Gamma[205/2, a*x]/(101*a^101)", //
				6562);
	}

	// {6562}
	public void test0206() {
		check(//
				"Integrate[x^2*Gamma[3/2, a*x], x]", //
				"(x^3*Gamma[3/2, a*x])/3 - Gamma[9/2, a*x]/(3*a^3)", //
				6562);
	}

	// {6562}
	public void test0207() {
		check(//
				"Integrate[x*Gamma[3/2, a*x], x]", //
				"(x^2*Gamma[3/2, a*x])/2 - Gamma[7/2, a*x]/(2*a^2)", //
				6562);
	}

	// {6557}
	public void test0208() {
		check(//
				"Integrate[Gamma[3/2, a*x], x]", //
				"x*Gamma[3/2, a*x] - Gamma[5/2, a*x]/a", //
				6557);
	}

	// {6561}
	public void test0209() {
		check(//
				"Integrate[Gamma[3/2, a*x]/x, x]", //
				"(-4*(a*x)^(3/2)*HypergeometricPFQ[{3/2, 3/2}, {5/2, 5/2}, -(a*x)])/9 + (Sqrt[Pi]*Log[x])/2", //
				6561);
	}

	// {6562}
	public void test0210() {
		check(//
				"Integrate[Gamma[3/2, a*x]/x^2, x]", //
				"a*Gamma[1/2, a*x] - Gamma[3/2, a*x]/x", //
				6562);
	}

	// {6562}
	public void test0211() {
		check(//
				"Integrate[Gamma[3/2, a*x]/x^3, x]", //
				"(a^2*Gamma[-1/2, a*x])/2 - Gamma[3/2, a*x]/(2*x^2)", //
				6562);
	}

	// {6562}
	public void test0212() {
		check(//
				"Integrate[Gamma[3/2, a*x]/x^4, x]", //
				"(a^3*Gamma[-3/2, a*x])/3 - Gamma[3/2, a*x]/(3*x^3)", //
				6562);
	}

	// {6562}
	public void test0213() {
		check(//
				"Integrate[(d*x)^m*Gamma[3, b*x], x]", //
				"((d*x)^(1 + m)*Gamma[3, b*x])/(d*(1 + m)) - ((d*x)^m*Gamma[4 + m, b*x])/(b*(1 + m)*(b*x)^m)", //
				6562);
	}

	// {6562}
	public void test0214() {
		check(//
				"Integrate[(d*x)^m*Gamma[2, b*x], x]", //
				"((d*x)^(1 + m)*Gamma[2, b*x])/(d*(1 + m)) - ((d*x)^m*Gamma[3 + m, b*x])/(b*(1 + m)*(b*x)^m)", //
				6562);
	}

	// {2181}
	public void test0215() {
		check(//
				"Integrate[(d*x)^m/E^(b*x), x]", //
				"-(((d*x)^m*Gamma[1 + m, b*x])/(b*(b*x)^m))", //
				2181);
	}

	// {6562}
	public void test0216() {
		check(//
				"Integrate[(d*x)^m*Gamma[0, b*x], x]", //
				"((d*x)^(1 + m)*Gamma[0, b*x])/(d*(1 + m)) - ((d*x)^m*Gamma[1 + m, b*x])/(b*(1 + m)*(b*x)^m)", //
				6562);
	}

	// {6562}
	public void test0217() {
		check(//
				"Integrate[(d*x)^m*Gamma[-1, b*x], x]", //
				"((d*x)^(1 + m)*Gamma[-1, b*x])/(d*(1 + m)) - ((d*x)^m*Gamma[m, b*x])/(b*(1 + m)*(b*x)^m)", //
				6562);
	}

	// {6562}
	public void test0218() {
		check(//
				"Integrate[(d*x)^m*Gamma[-2, b*x], x]", //
				"((d*x)^(1 + m)*Gamma[-2, b*x])/(d*(1 + m)) - ((d*x)^m*Gamma[-1 + m, b*x])/(b*(1 + m)*(b*x)^m)", //
				6562);
	}

	// {6562}
	public void test0219() {
		check(//
				"Integrate[x^m*Gamma[n, x], x]", //
				"(x^(1 + m)*Gamma[n, x])/(1 + m) - Gamma[1 + m + n, x]/(1 + m)", //
				6562);
	}

	// {6562}
	public void test0220() {
		check(//
				"Integrate[x^m*Gamma[n, b*x], x]", //
				"(x^(1 + m)*Gamma[n, b*x])/(1 + m) - (x^m*Gamma[1 + m + n, b*x])/(b*(1 + m)*(b*x)^m)", //
				6562);
	}

	// {6562}
	public void test0221() {
		check(//
				"Integrate[(d*x)^m*Gamma[n, x], x]", //
				"((d*x)^(1 + m)*Gamma[n, x])/(d*(1 + m)) - ((d*x)^m*Gamma[1 + m + n, x])/((1 + m)*x^m)", //
				6562);
	}

	// {6562}
	public void test0222() {
		check(//
				"Integrate[(d*x)^m*Gamma[n, b*x], x]", //
				"((d*x)^(1 + m)*Gamma[n, b*x])/(d*(1 + m)) - ((d*x)^m*Gamma[1 + m + n, b*x])/(b*(1 + m)*(b*x)^m)", //
				6562);
	}

	// {6562}
	public void test0223() {
		check(//
				"Integrate[x^100*Gamma[n, a*x], x]", //
				"(x^101*Gamma[n, a*x])/101 - Gamma[101 + n, a*x]/(101*a^101)", //
				6562);
	}

	// {6562}
	public void test0224() {
		check(//
				"Integrate[x^2*Gamma[n, a*x], x]", //
				"(x^3*Gamma[n, a*x])/3 - Gamma[3 + n, a*x]/(3*a^3)", //
				6562);
	}

	// {6562}
	public void test0225() {
		check(//
				"Integrate[x*Gamma[n, a*x], x]", //
				"(x^2*Gamma[n, a*x])/2 - Gamma[2 + n, a*x]/(2*a^2)", //
				6562);
	}

	// {6557}
	public void test0226() {
		check(//
				"Integrate[Gamma[n, a*x], x]", //
				"x*Gamma[n, a*x] - Gamma[1 + n, a*x]/a", //
				6557);
	}

	// {6561}
	public void test0227() {
		check(//
				"Integrate[Gamma[n, a*x]/x, x]", //
				"-(((a*x)^n*HypergeometricPFQ[{n, n}, {1 + n, 1 + n}, -(a*x)])/n^2) + Gamma[n]*Log[x]", //
				6561);
	}

	// {6562}
	public void test0228() {
		check(//
				"Integrate[Gamma[n, a*x]/x^2, x]", //
				"a*Gamma[-1 + n, a*x] - Gamma[n, a*x]/x", //
				6562);
	}

	// {6562}
	public void test0229() {
		check(//
				"Integrate[Gamma[n, a*x]/x^3, x]", //
				"(a^2*Gamma[-2 + n, a*x])/2 - Gamma[n, a*x]/(2*x^2)", //
				6562);
	}

	// {6562}
	public void test0230() {
		check(//
				"Integrate[Gamma[n, a*x]/x^4, x]", //
				"(a^3*Gamma[-3 + n, a*x])/3 - Gamma[n, a*x]/(3*x^3)", //
				6562);
	}

	// {6562}
	public void test0231() {
		check(//
				"Integrate[x^100*Gamma[n, 2*x], x]", //
				"(x^101*Gamma[n, 2*x])/101 - Gamma[101 + n, 2*x]/256065421246102339102334047485952", //
				6562);
	}

	// {6562}
	public void test0232() {
		check(//
				"Integrate[x^2*Gamma[n, 2*x], x]", //
				"(x^3*Gamma[n, 2*x])/3 - Gamma[3 + n, 2*x]/24", //
				6562);
	}

	// {6562}
	public void test0233() {
		check(//
				"Integrate[x*Gamma[n, 2*x], x]", //
				"(x^2*Gamma[n, 2*x])/2 - Gamma[2 + n, 2*x]/8", //
				6562);
	}

	// {6557}
	public void test0234() {
		check(//
				"Integrate[Gamma[n, 2*x], x]", //
				"x*Gamma[n, 2*x] - Gamma[1 + n, 2*x]/2", //
				6557);
	}

	// {6561}
	public void test0235() {
		check(//
				"Integrate[Gamma[n, 2*x]/x, x]", //
				"-((2^n*x^n*HypergeometricPFQ[{n, n}, {1 + n, 1 + n}, -2*x])/n^2) + Gamma[n]*Log[x]", //
				6561);
	}

	// {6562}
	public void test0236() {
		check(//
				"Integrate[Gamma[n, 2*x]/x^2, x]", //
				"2*Gamma[-1 + n, 2*x] - Gamma[n, 2*x]/x", //
				6562);
	}

	// {6562}
	public void test0237() {
		check(//
				"Integrate[Gamma[n, 2*x]/x^3, x]", //
				"2*Gamma[-2 + n, 2*x] - Gamma[n, 2*x]/(2*x^2)", //
				6562);
	}

	// {6562}
	public void test0238() {
		check(//
				"Integrate[Gamma[n, 2*x]/x^4, x]", //
				"(8*Gamma[-3 + n, 2*x])/3 - Gamma[n, 2*x]/(3*x^3)", //
				6562);
	}

	// {6557}
	public void test0239() {
		check(//
				"Integrate[Gamma[0, a + b*x], x]", //
				"-(E^(-a - b*x)/b) + ((a + b*x)*Gamma[0, a + b*x])/b", //
				6557);
	}

	// {2194}
	public void test0240() {
		check(//
				"Integrate[E^(-a - b*x), x]", //
				"-(E^(-a - b*x)/b)", //
				2194);
	}

	// {2178}
	public void test0241() {
		check(//
				"Integrate[E^(-a - b*x)/(c + d*x), x]", //
				"(E^(-a + (b*c)/d)*ExpIntegralEi[-((b*(c + d*x))/d)])/d", //
				2178);
	}

	// {6557}
	public void test0242() {
		check(//
				"Integrate[Gamma[2, a + b*x], x]", //
				"((a + b*x)*Gamma[2, a + b*x])/b - Gamma[3, a + b*x]/b", //
				6557);
	}

	// {6557}
	public void test0243() {
		check(//
				"Integrate[Gamma[3, a + b*x], x]", //
				"((a + b*x)*Gamma[3, a + b*x])/b - Gamma[4, a + b*x]/b", //
				6557);
	}

	// {6557}
	public void test0244() {
		check(//
				"Integrate[Gamma[-1, a + b*x], x]", //
				"((a + b*x)*Gamma[-1, a + b*x])/b - Gamma[0, a + b*x]/b", //
				6557);
	}

	// {6557}
	public void test0245() {
		check(//
				"Integrate[Gamma[-2, a + b*x], x]", //
				"((a + b*x)*Gamma[-2, a + b*x])/b - Gamma[-1, a + b*x]/b", //
				6557);
	}

	// {6557}
	public void test0246() {
		check(//
				"Integrate[Gamma[-3, a + b*x], x]", //
				"((a + b*x)*Gamma[-3, a + b*x])/b - Gamma[-2, a + b*x]/b", //
				6557);
	}

	// {2181}
	public void test0247() {
		check(//
				"Integrate[E^(-a - b*x)*(c + d*x)^m, x]", //
				"-((E^(-a + (b*c)/d)*(c + d*x)^m*Gamma[1 + m, (b*(c + d*x))/d])/(b*((b*(c + d*x))/d)^m))", //
				2181);
	}

	// {6557}
	public void test0248() {
		check(//
				"Integrate[Gamma[n, a + b*x], x]", //
				"((a + b*x)*Gamma[n, a + b*x])/b - Gamma[1 + n, a + b*x]/b", //
				6557);
	}

	// {6567}
	public void test0249() {
		check(//
				"Integrate[LogGamma[a + b*x], x]", //
				"PolyGamma[-2, a + b*x]/b", //
				6567);
	}

	// {6570}
	public void test0250() {
		check(//
				"Integrate[PolyGamma[n, a + b*x], x]", //
				"PolyGamma[-1 + n, a + b*x]/b", //
				6570);
	}

	// {6574}
	public void test0254() {
		check(//
				"Integrate[Gamma[a + b*x]^n*PolyGamma[0, a + b*x], x]", //
				"Gamma[a + b*x]^n/(b*n)", //
				6574);
	}

	// {6575}
	public void test0255() {
		check(//
				"Integrate[(a + b*x)!^n*PolyGamma[0, 1 + a + b*x], x]", //
				"(a + b*x)!^n/(b*n)", //
				6575);
	}

	// {6582}
	public void test0257() {
		check(//
				"Integrate[Zeta[s, a + b*x], x]", //
				"Zeta[-1 + s, a + b*x]/(b*(1 - s))", //
				6582);
	}

	// {6589}
	public void test0259() {
		check(//
				"Integrate[PolyLog[2, a*x]/x, x]", //
				"PolyLog[3, a*x]", //
				6589);
	}

	// {6589}
	public void test0260() {
		check(//
				"Integrate[PolyLog[3, a*x]/x, x]", //
				"PolyLog[4, a*x]", //
				6589);
	}

	// {6589}
	public void test0261() {
		check(//
				"Integrate[PolyLog[2, a*x^2]/x, x]", //
				"PolyLog[3, a*x^2]/2", //
				6589);
	}

	// {6589}
	public void test0262() {
		check(//
				"Integrate[PolyLog[3, a*x^2]/x, x]", //
				"PolyLog[4, a*x^2]/2", //
				6589);
	}

	// {6589}
	public void test0263() {
		check(//
				"Integrate[PolyLog[2, a*x^q]/x, x]", //
				"PolyLog[3, a*x^q]/q", //
				6589);
	}

	// {6589}
	public void test0264() {
		check(//
				"Integrate[PolyLog[3, a*x^q]/x, x]", //
				"PolyLog[4, a*x^q]/q", //
				6589);
	}

	// {6589}
	public void test0267() {
		check(//
				"Integrate[PolyLog[n, a*x]/x, x]", //
				"PolyLog[1 + n, a*x]", //
				6589);
	}

	// {6589}
	public void test0268() {
		check(//
				"Integrate[PolyLog[n, a*x^q]/x, x]", //
				"PolyLog[1 + n, a*x^q]/q", //
				6589);
	}

	// {6610}
	public void test0270() {
		check(//
				"Integrate[PolyLog[n, e*((a + b*x)/(c + d*x))^n]/((a + b*x)*(c + d*x)), x]", //
				"PolyLog[1 + n, e*((a + b*x)/(c + d*x))^n]/((b*c - a*d)*n)", //
				6610);
	}

	// {6610}
	public void test0271() {
		check(//
				"Integrate[PolyLog[3, e*((a + b*x)/(c + d*x))^n]/((a + b*x)*(c + d*x)), x]", //
				"PolyLog[4, e*((a + b*x)/(c + d*x))^n]/((b*c - a*d)*n)", //
				6610);
	}

	// {6610}
	public void test0272() {
		check(//
				"Integrate[PolyLog[2, e*((a + b*x)/(c + d*x))^n]/((a + b*x)*(c + d*x)), x]", //
				"PolyLog[3, e*((a + b*x)/(c + d*x))^n]/((b*c - a*d)*n)", //
				6610);
	}

	// {2518}
	public void test0273() {
		check(//
				"Integrate[-(Log[1 - e*((a + b*x)/(c + d*x))^n]/((a + b*x)*(c + d*x))), x]", //
				"PolyLog[2, e*((a + b*x)/(c + d*x))^n]/((b*c - a*d)*n)", //
				2518);
	}

	// {6601}
	public void test0275() {
		check(//
				"Integrate[(Log[1 - c*x]*PolyLog[2, c*x])/x, x]", //
				"-PolyLog[2, c*x]^2/2", //
				6601);
	}

	// {6622}
	public void test0282() {
		check(//
				"Integrate[(d + d*ProductLog[a + b*x])^(-1), x]", //
				"(a + b*x)/(b*d*ProductLog[a + b*x])", //
				6622);
	}

	// {6622}
	public void test0292() {
		check(//
				"Integrate[(1 + ProductLog[a*x])^(-1), x]", //
				"x/ProductLog[a*x]", //
				6622);
	}

	// {6641}
	public void test0293() {
		check(//
				"Integrate[1/(x*(1 + ProductLog[a*x])), x]", //
				"Log[ProductLog[a*x]]", //
				6641);
	}

	// {6644}
	public void test0294() {
		check(//
				"Integrate[1/(x*(1 + ProductLog[a*x^2])), x]", //
				"Log[ProductLog[a*x^2]]/2", //
				6644);
	}

	// {6644}
	public void test0295() {
		check(//
				"Integrate[1/(x*(1 + ProductLog[a/x])), x]", //
				"-Log[ProductLog[a/x]]", //
				6644);
	}

	// {6644}
	public void test0296() {
		check(//
				"Integrate[1/(x*(1 + ProductLog[a/x^2])), x]", //
				"-Log[ProductLog[a/x^2]]/2", //
				6644);
	}

	// {6643}
	public void test0302() {
		check(//
				"Integrate[x^m/(d + d*ProductLog[a*x]), x]", //
				"(x^m*Gamma[1 + m, -((1 + m)*ProductLog[a*x])])/(a*d*E^(m*ProductLog[a*x])*(1 + m)*(-((1 + m)*ProductLog[a*x]))^m)", //
				6643);
	}

	// {6633}
	public void test0303() {
		check(//
				"Integrate[ProductLog[a/x^(1/4)]^5/(1 + ProductLog[a/x^(1/4)]), x]", //
				"x*ProductLog[a/x^(1/4)]^4", //
				6633);
	}

	// {6633}
	public void test0304() {
		check(//
				"Integrate[ProductLog[a/x^(1/3)]^4/(1 + ProductLog[a/x^(1/3)]), x]", //
				"x*ProductLog[a/x^(1/3)]^3", //
				6633);
	}

	// {6633}
	public void test0305() {
		check(//
				"Integrate[ProductLog[a/Sqrt[x]]^3/(1 + ProductLog[a/Sqrt[x]]), x]", //
				"x*ProductLog[a/Sqrt[x]]^2", //
				6633);
	}

	// {6633}
	public void test0306() {
		check(//
				"Integrate[ProductLog[a/x]^2/(1 + ProductLog[a/x]), x]", //
				"x*ProductLog[a/x]", //
				6633);
	}

	// {6633}
	public void test0307() {
		check(//
				"Integrate[1/(ProductLog[a*Sqrt[x]]*(1 + ProductLog[a*Sqrt[x]])), x]", //
				"x/ProductLog[a*Sqrt[x]]^2", //
				6633);
	}

	// {6633}
	public void test0308() {
		check(//
				"Integrate[1/(ProductLog[a*x^(1/3)]^2*(1 + ProductLog[a*x^(1/3)])), x]", //
				"x/ProductLog[a*x^(1/3)]^3", //
				6633);
	}

	// {6633}
	public void test0309() {
		check(//
				"Integrate[1/(ProductLog[a*x^(1/4)]^3*(1 + ProductLog[a*x^(1/4)])), x]", //
				"x/ProductLog[a*x^(1/4)]^4", //
				6633);
	}

	// {6634}
	public void test0310() {
		check(//
				"Integrate[ProductLog[a/x^(1/4)]^4/(1 + ProductLog[a/x^(1/4)]), x]", //
				"-4*a^4*ExpIntegralEi[-4*ProductLog[a/x^(1/4)]]", //
				6634);
	}

	// {6634}
	public void test0311() {
		check(//
				"Integrate[ProductLog[a/x^(1/3)]^3/(1 + ProductLog[a/x^(1/3)]), x]", //
				"-3*a^3*ExpIntegralEi[-3*ProductLog[a/x^(1/3)]]", //
				6634);
	}

	// {6634}
	public void test0312() {
		check(//
				"Integrate[ProductLog[a/Sqrt[x]]^2/(1 + ProductLog[a/Sqrt[x]]), x]", //
				"-2*a^2*ExpIntegralEi[-2*ProductLog[a/Sqrt[x]]]", //
				6634);
	}

	// {6634}
	public void test0313() {
		check(//
				"Integrate[ProductLog[a/x]/(1 + ProductLog[a/x]), x]", //
				"-(a*ExpIntegralEi[-ProductLog[a/x]])", //
				6634);
	}

	// {6625}
	public void test0314() {
		check(//
				"Integrate[1/(ProductLog[a*x]*(1 + ProductLog[a*x])), x]", //
				"ExpIntegralEi[ProductLog[a*x]]/a", //
				6625);
	}

	// {6634}
	public void test0315() {
		check(//
				"Integrate[1/(ProductLog[a*Sqrt[x]]^2*(1 + ProductLog[a*Sqrt[x]])), x]", //
				"(2*ExpIntegralEi[2*ProductLog[a*Sqrt[x]]])/a^2", //
				6634);
	}

	// {6634}
	public void test0316() {
		check(//
				"Integrate[1/(ProductLog[a*x^(1/3)]^3*(1 + ProductLog[a*x^(1/3)])), x]", //
				"(3*ExpIntegralEi[3*ProductLog[a*x^(1/3)]])/a^3", //
				6634);
	}

	// {6634}
	public void test0317() {
		check(//
				"Integrate[1/(ProductLog[a*x^(1/4)]^4*(1 + ProductLog[a*x^(1/4)])), x]", //
				"(4*ExpIntegralEi[4*ProductLog[a*x^(1/4)]])/a^4", //
				6634);
	}

	// {6633}
	public void test0318() {
		check(//
				"Integrate[ProductLog[a*x^n]^(1 - n^(-1))/(1 + ProductLog[a*x^n]), x]", //
				"x/ProductLog[a*x^n]^n^(-1)", //
				6633);
	}

	// {6633}
	public void test0319() {
		check(//
				"Integrate[ProductLog[a*x^(1 - p)^(-1)]^p/(1 + ProductLog[a*x^(1 - p)^(-1)]), x]", //
				"x*ProductLog[a*x^(1 - p)^(-1)]^(-1 + p)", //
				6633);
	}
}
