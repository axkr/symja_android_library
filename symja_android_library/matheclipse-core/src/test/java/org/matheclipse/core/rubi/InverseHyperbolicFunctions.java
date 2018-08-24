package org.matheclipse.core.rubi;

public class InverseHyperbolicFunctions extends AbstractRubiTestCase {

	public InverseHyperbolicFunctions(String name) {
		super(name, false);
	}

	// {5675}
	public void test0003() {
		check(//
				"Integrate[ArcSinh[a*x]/Sqrt[1 + a^2*x^2], x]", //
				"ArcSinh[a*x]^2/(2*a)", //
				5675);
	}

	// {5762}
	public void test0004() {
		check(//
				"Integrate[(x^m*ArcSinh[a*x])/Sqrt[1 + a^2*x^2], x]", //
				"(x^(1 + m)*ArcSinh[a*x]*Hypergeometric2F1[1/2, (1 + m)/2, (3 + m)/2, -(a^2*x^2)])/(1 + m) - (a*x^(2 + m)*HypergeometricPFQ[{1, 1 + m/2, 1 + m/2}, {3/2 + m/2, 2 + m/2}, -(a^2*x^2)])/(2 + 3*m + m^2)", //
				5762);
	}

	// {5675}
	public void test0005() {
		check(//
				"Integrate[ArcSinh[a*x]^2/Sqrt[1 + a^2*x^2], x]", //
				"ArcSinh[a*x]^3/(3*a)", //
				5675);
	}

	// {5675}
	public void test0006() {
		check(//
				"Integrate[ArcSinh[a*x]^3/Sqrt[1 + a^2*x^2], x]", //
				"ArcSinh[a*x]^4/(4*a)", //
				5675);
	}

	// {5673}
	public void test0007() {
		check(//
				"Integrate[1/(Sqrt[1 + a^2*x^2]*ArcSinh[a*x]), x]", //
				"Log[ArcSinh[a*x]]/a", //
				5673);
	}

	// {5673}
	public void test0008() {
		check(//
				"Integrate[1/(Sqrt[1 + c^2*x^2]*(a + b*ArcSinh[c*x])), x]", //
				"Log[a + b*ArcSinh[c*x]]/(b*c)", //
				5673);
	}

	// {5675}
	public void test0016() {
		check(//
				"Integrate[1/(Sqrt[1 + c^2*x^2]*(a + b*ArcSinh[c*x])^2), x]", //
				"-(1/(b*c*(a + b*ArcSinh[c*x])))", //
				5675);
	}

	// {5675}
	public void test0022() {
		check(//
				"Integrate[1/(Sqrt[1 + a^2*x^2]*ArcSinh[a*x]^3), x]", //
				"-1/(2*a*ArcSinh[a*x]^2)", //
				5675);
	}

	// {5675}
	public void test0032() {
		check(//
				"Integrate[ArcSinh[a*x]^n/Sqrt[1 + a^2*x^2], x]", //
				"ArcSinh[a*x]^(1 + n)/(a*(1 + n))", //
				5675);
	}

	// {4816}
	public void test0041() {
		check(//
				"Integrate[(a + I*b*ArcSin[1 - I*d*x^2])^(-1), x]", //
				"(x*CosIntegral[((-I/2)*(a + I*b*ArcSin[1 - I*d*x^2]))/b]*(I*Cosh[a/(2*b)] - Sinh[a/(2*b)]))/(2*b*(Cos[ArcSin[1 - I*d*x^2]/2] - Sin[ArcSin[1 - I*d*x^2]/2])) - (x*(I*Cosh[a/(2*b)] + Sinh[a/(2*b)])*SinIntegral[((I/2)*a)/b - ArcSin[1 - I*d*x^2]/2])/(2*b*(Cos[ArcSin[1 - I*d*x^2]/2] - Sin[ArcSin[1 - I*d*x^2]/2]))", //
				4816);
	}

	// {4825}
	public void test0042() {
		check(//
				"Integrate[(a + I*b*ArcSin[1 - I*d*x^2])^(-2), x]", //
				"-Sqrt[(2*I)*d*x^2 + d^2*x^4]/(2*b*d*x*(a + I*b*ArcSin[1 - I*d*x^2])) + (x*CosIntegral[((-I/2)*(a + I*b*ArcSin[1 - I*d*x^2]))/b]*(Cosh[a/(2*b)] - I*Sinh[a/(2*b)]))/(4*b^2*(Cos[ArcSin[1 - I*d*x^2]/2] - Sin[ArcSin[1 - I*d*x^2]/2])) + (x*(Cosh[a/(2*b)] + I*Sinh[a/(2*b)])*SinIntegral[((I/2)*a)/b - ArcSin[1 - I*d*x^2]/2])/(4*b^2*(Cos[ArcSin[1 - I*d*x^2]/2] - Sin[ArcSin[1 - I*d*x^2]/2]))", //
				4825);
	}

	// {4816}
	public void test0043() {
		check(//
				"Integrate[(a - I*b*ArcSin[1 + I*d*x^2])^(-1), x]", //
				"-(x*CosIntegral[((I/2)*(a - I*b*ArcSin[1 + I*d*x^2]))/b]*(I*Cosh[a/(2*b)] + Sinh[a/(2*b)]))/(2*b*(Cos[ArcSin[1 + I*d*x^2]/2] - Sin[ArcSin[1 + I*d*x^2]/2])) + (x*(Cosh[a/(2*b)] + I*Sinh[a/(2*b)])*SinhIntegral[(a - I*b*ArcSin[1 + I*d*x^2])/(2*b)])/(2*b*(Cos[ArcSin[1 + I*d*x^2]/2] - Sin[ArcSin[1 + I*d*x^2]/2]))", //
				4816);
	}

	// {4825}
	public void test0044() {
		check(//
				"Integrate[(a - I*b*ArcSin[1 + I*d*x^2])^(-2), x]", //
				"-Sqrt[(-2*I)*d*x^2 + d^2*x^4]/(2*b*d*x*(a - I*b*ArcSin[1 + I*d*x^2])) + (x*CosIntegral[((I/2)*(a - I*b*ArcSin[1 + I*d*x^2]))/b]*(Cosh[a/(2*b)] + I*Sinh[a/(2*b)]))/(4*b^2*(Cos[ArcSin[1 + I*d*x^2]/2] - Sin[ArcSin[1 + I*d*x^2]/2])) - (x*(I*Cosh[a/(2*b)] + Sinh[a/(2*b)])*SinhIntegral[(a - I*b*ArcSin[1 + I*d*x^2])/(2*b)])/(4*b^2*(Cos[ArcSin[1 + I*d*x^2]/2] - Sin[ArcSin[1 + I*d*x^2]/2]))", //
				4825);
	}

	// {4811}
	public void test0045() {
		check(//
				"Integrate[Sqrt[a + I*b*ArcSin[1 - I*d*x^2]], x]", //
				"x*Sqrt[a + I*b*ArcSin[1 - I*d*x^2]] + (Sqrt[Pi]*x*FresnelS[(Sqrt[(-I)/b]*Sqrt[a + I*b*ArcSin[1 - I*d*x^2]])/Sqrt[Pi]]*(Cosh[a/(2*b)] + I*Sinh[a/(2*b)]))/(Sqrt[(-I)/b]*(Cos[ArcSin[1 - I*d*x^2]/2] - Sin[ArcSin[1 - I*d*x^2]/2])) - (Sqrt[(-I)/b]*b*Sqrt[Pi]*x*FresnelC[(Sqrt[(-I)/b]*Sqrt[a + I*b*ArcSin[1 - I*d*x^2]])/Sqrt[Pi]]*(I*Cosh[a/(2*b)] + Sinh[a/(2*b)]))/(Cos[ArcSin[1 - I*d*x^2]/2] - Sin[ArcSin[1 - I*d*x^2]/2])", //
				4811);
	}

	// {4819}
	public void test0046() {
		check(//
				"Integrate[1/Sqrt[a + I*b*ArcSin[1 - I*d*x^2]], x]", //
				"-((Sqrt[Pi]*x*FresnelS[Sqrt[a + I*b*ArcSin[1 - I*d*x^2]]/(Sqrt[I*b]*Sqrt[Pi])]*(Cosh[a/(2*b)] - I*Sinh[a/(2*b)]))/(Sqrt[I*b]*(Cos[ArcSin[1 - I*d*x^2]/2] - Sin[ArcSin[1 - I*d*x^2]/2]))) - (Sqrt[Pi]*x*FresnelC[Sqrt[a + I*b*ArcSin[1 - I*d*x^2]]/(Sqrt[I*b]*Sqrt[Pi])]*(Cosh[a/(2*b)] + I*Sinh[a/(2*b)]))/(Sqrt[I*b]*(Cos[ArcSin[1 - I*d*x^2]/2] - Sin[ArcSin[1 - I*d*x^2]/2]))", //
				4819);
	}

	// {4822}
	public void test0047() {
		check(//
				"Integrate[(a + I*b*ArcSin[1 - I*d*x^2])^(-3/2), x]", //
				"-(Sqrt[(2*I)*d*x^2 + d^2*x^4]/(b*d*x*Sqrt[a + I*b*ArcSin[1 - I*d*x^2]])) - (((-I)/b)^(3/2)*Sqrt[Pi]*x*FresnelC[(Sqrt[(-I)/b]*Sqrt[a + I*b*ArcSin[1 - I*d*x^2]])/Sqrt[Pi]]*(Cosh[a/(2*b)] - I*Sinh[a/(2*b)]))/(Cos[ArcSin[1 - I*d*x^2]/2] - Sin[ArcSin[1 - I*d*x^2]/2]) + (((-I)/b)^(3/2)*Sqrt[Pi]*x*FresnelS[(Sqrt[(-I)/b]*Sqrt[a + I*b*ArcSin[1 - I*d*x^2]])/Sqrt[Pi]]*(Cosh[a/(2*b)] + I*Sinh[a/(2*b)]))/(Cos[ArcSin[1 - I*d*x^2]/2] - Sin[ArcSin[1 - I*d*x^2]/2])", //
				4822);
	}

	// {4811}
	public void test0048() {
		check(//
				"Integrate[Sqrt[a - I*b*ArcSin[1 + I*d*x^2]], x]", //
				"x*Sqrt[a - I*b*ArcSin[1 + I*d*x^2]] + (Sqrt[Pi]*x*FresnelS[(Sqrt[I/b]*Sqrt[a - I*b*ArcSin[1 + I*d*x^2]])/Sqrt[Pi]]*(Cosh[a/(2*b)] - I*Sinh[a/(2*b)]))/(Sqrt[I/b]*(Cos[ArcSin[1 + I*d*x^2]/2] - Sin[ArcSin[1 + I*d*x^2]/2])) - (Sqrt[Pi]*x*FresnelC[(Sqrt[I/b]*Sqrt[a - I*b*ArcSin[1 + I*d*x^2]])/Sqrt[Pi]]*(Cosh[a/(2*b)] + I*Sinh[a/(2*b)]))/(Sqrt[I/b]*(Cos[ArcSin[1 + I*d*x^2]/2] - Sin[ArcSin[1 + I*d*x^2]/2]))", //
				4811);
	}

	// {4819}
	public void test0049() {
		check(//
				"Integrate[1/Sqrt[a - I*b*ArcSin[1 + I*d*x^2]], x]", //
				"-((Sqrt[Pi]*x*FresnelC[Sqrt[a - I*b*ArcSin[1 + I*d*x^2]]/(Sqrt[(-I)*b]*Sqrt[Pi])]*(Cosh[a/(2*b)] - I*Sinh[a/(2*b)]))/(Sqrt[(-I)*b]*(Cos[ArcSin[1 + I*d*x^2]/2] - Sin[ArcSin[1 + I*d*x^2]/2]))) - (Sqrt[Pi]*x*FresnelS[Sqrt[a - I*b*ArcSin[1 + I*d*x^2]]/(Sqrt[(-I)*b]*Sqrt[Pi])]*(Cosh[a/(2*b)] + I*Sinh[a/(2*b)]))/(Sqrt[(-I)*b]*(Cos[ArcSin[1 + I*d*x^2]/2] - Sin[ArcSin[1 + I*d*x^2]/2]))", //
				4819);
	}

	// {4822}
	public void test0050() {
		check(//
				"Integrate[(a - I*b*ArcSin[1 + I*d*x^2])^(-3/2), x]", //
				"-(Sqrt[(-2*I)*d*x^2 + d^2*x^4]/(b*d*x*Sqrt[a - I*b*ArcSin[1 + I*d*x^2]])) + ((I/b)^(3/2)*Sqrt[Pi]*x*FresnelS[(Sqrt[I/b]*Sqrt[a - I*b*ArcSin[1 + I*d*x^2]])/Sqrt[Pi]]*(Cosh[a/(2*b)] - I*Sinh[a/(2*b)]))/(Cos[ArcSin[1 + I*d*x^2]/2] - Sin[ArcSin[1 + I*d*x^2]/2]) - ((I/b)^(3/2)*Sqrt[Pi]*x*FresnelC[(Sqrt[I/b]*Sqrt[a - I*b*ArcSin[1 + I*d*x^2]])/Sqrt[Pi]]*(Cosh[a/(2*b)] + I*Sinh[a/(2*b)]))/(Cos[ArcSin[1 + I*d*x^2]/2] - Sin[ArcSin[1 + I*d*x^2]/2])", //
				4822);
	}

	// {5881}
	public void test0120() {
		check(//
				"Integrate[(a + b*ArcCosh[1 + d*x^2])^(-1), x]", //
				"(x*Cosh[a/(2*b)]*CoshIntegral[(a + b*ArcCosh[1 + d*x^2])/(2*b)])/(Sqrt[2]*b*Sqrt[d*x^2]) - (x*Sinh[a/(2*b)]*SinhIntegral[(a + b*ArcCosh[1 + d*x^2])/(2*b)])/(Sqrt[2]*b*Sqrt[d*x^2])", //
				5881);
	}

	// {5887}
	public void test0121() {
		check(//
				"Integrate[(a + b*ArcCosh[1 + d*x^2])^(-2), x]", //
				"-(Sqrt[d*x^2]*Sqrt[2 + d*x^2])/(2*b*d*x*(a + b*ArcCosh[1 + d*x^2])) - (x*CoshIntegral[(a + b*ArcCosh[1 + d*x^2])/(2*b)]*Sinh[a/(2*b)])/(2*Sqrt[2]*b^2*Sqrt[d*x^2]) + (x*Cosh[a/(2*b)]*SinhIntegral[(a + b*ArcCosh[1 + d*x^2])/(2*b)])/(2*Sqrt[2]*b^2*Sqrt[d*x^2])", //
				5887);
	}

	// {5882}
	public void test0122() {
		check(//
				"Integrate[(a + b*ArcCosh[-1 + d*x^2])^(-1), x]", //
				"-((x*CoshIntegral[(a + b*ArcCosh[-1 + d*x^2])/(2*b)]*Sinh[a/(2*b)])/(Sqrt[2]*b*Sqrt[d*x^2])) + (x*Cosh[a/(2*b)]*SinhIntegral[(a + b*ArcCosh[-1 + d*x^2])/(2*b)])/(Sqrt[2]*b*Sqrt[d*x^2])", //
				5882);
	}

	// {5888}
	public void test0123() {
		check(//
				"Integrate[(a + b*ArcCosh[-1 + d*x^2])^(-2), x]", //
				"-(Sqrt[d*x^2]*Sqrt[-2 + d*x^2])/(2*b*d*x*(a + b*ArcCosh[-1 + d*x^2])) + (x*Cosh[a/(2*b)]*CoshIntegral[(a + b*ArcCosh[-1 + d*x^2])/(2*b)])/(2*Sqrt[2]*b^2*Sqrt[d*x^2]) - (x*Sinh[a/(2*b)]*SinhIntegral[(a + b*ArcCosh[-1 + d*x^2])/(2*b)])/(2*Sqrt[2]*b^2*Sqrt[d*x^2])", //
				5888);
	}

	// {5878}
	public void test0124() {
		check(//
				"Integrate[Sqrt[a + b*ArcCosh[1 + d*x^2]], x]", //
				"-((Sqrt[b]*Sqrt[Pi/2]*Erfi[Sqrt[a + b*ArcCosh[1 + d*x^2]]/(Sqrt[2]*Sqrt[b])]*(Cosh[a/(2*b)] - Sinh[a/(2*b)])*Sinh[ArcCosh[1 + d*x^2]/2])/(d*x)) + (Sqrt[b]*Sqrt[Pi/2]*Erf[Sqrt[a + b*ArcCosh[1 + d*x^2]]/(Sqrt[2]*Sqrt[b])]*(Cosh[a/(2*b)] + Sinh[a/(2*b)])*Sinh[ArcCosh[1 + d*x^2]/2])/(d*x) + (2*Sqrt[a + b*ArcCosh[1 + d*x^2]]*Sinh[ArcCosh[1 + d*x^2]/2]^2)/(d*x)", //
				5878);
	}

	// {5883}
	public void test0125() {
		check(//
				"Integrate[1/Sqrt[a + b*ArcCosh[1 + d*x^2]], x]", //
				"(Sqrt[Pi/2]*Erfi[Sqrt[a + b*ArcCosh[1 + d*x^2]]/(Sqrt[2]*Sqrt[b])]*(Cosh[a/(2*b)] - Sinh[a/(2*b)])*Sinh[ArcCosh[1 + d*x^2]/2])/(Sqrt[b]*d*x) + (Sqrt[Pi/2]*Erf[Sqrt[a + b*ArcCosh[1 + d*x^2]]/(Sqrt[2]*Sqrt[b])]*(Cosh[a/(2*b)] + Sinh[a/(2*b)])*Sinh[ArcCosh[1 + d*x^2]/2])/(Sqrt[b]*d*x)", //
				5883);
	}

	// {5885}
	public void test0126() {
		check(//
				"Integrate[(a + b*ArcCosh[1 + d*x^2])^(-3/2), x]", //
				"-((Sqrt[d*x^2]*Sqrt[2 + d*x^2])/(b*d*x*Sqrt[a + b*ArcCosh[1 + d*x^2]])) + (Sqrt[Pi/2]*Erfi[Sqrt[a + b*ArcCosh[1 + d*x^2]]/(Sqrt[2]*Sqrt[b])]*(Cosh[a/(2*b)] - Sinh[a/(2*b)])*Sinh[ArcCosh[1 + d*x^2]/2])/(b^(3/2)*d*x) - (Sqrt[Pi/2]*Erf[Sqrt[a + b*ArcCosh[1 + d*x^2]]/(Sqrt[2]*Sqrt[b])]*(Cosh[a/(2*b)] + Sinh[a/(2*b)])*Sinh[ArcCosh[1 + d*x^2]/2])/(b^(3/2)*d*x)", //
				5885);
	}

	// {5879}
	public void test0127() {
		check(//
				"Integrate[Sqrt[a + b*ArcCosh[-1 + d*x^2]], x]", //
				"(2*Sqrt[a + b*ArcCosh[-1 + d*x^2]]*Cosh[ArcCosh[-1 + d*x^2]/2]^2)/(d*x) - (Sqrt[b]*Sqrt[Pi/2]*Cosh[ArcCosh[-1 + d*x^2]/2]*Erfi[Sqrt[a + b*ArcCosh[-1 + d*x^2]]/(Sqrt[2]*Sqrt[b])]*(Cosh[a/(2*b)] - Sinh[a/(2*b)]))/(d*x) - (Sqrt[b]*Sqrt[Pi/2]*Cosh[ArcCosh[-1 + d*x^2]/2]*Erf[Sqrt[a + b*ArcCosh[-1 + d*x^2]]/(Sqrt[2]*Sqrt[b])]*(Cosh[a/(2*b)] + Sinh[a/(2*b)]))/(d*x)", //
				5879);
	}

	// {5884}
	public void test0128() {
		check(//
				"Integrate[1/Sqrt[a + b*ArcCosh[-1 + d*x^2]], x]", //
				"(Sqrt[Pi/2]*Cosh[ArcCosh[-1 + d*x^2]/2]*Erfi[Sqrt[a + b*ArcCosh[-1 + d*x^2]]/(Sqrt[2]*Sqrt[b])]*(Cosh[a/(2*b)] - Sinh[a/(2*b)]))/(Sqrt[b]*d*x) - (Sqrt[Pi/2]*Cosh[ArcCosh[-1 + d*x^2]/2]*Erf[Sqrt[a + b*ArcCosh[-1 + d*x^2]]/(Sqrt[2]*Sqrt[b])]*(Cosh[a/(2*b)] + Sinh[a/(2*b)]))/(Sqrt[b]*d*x)", //
				5884);
	}

	// {5886}
	public void test0129() {
		check(//
				"Integrate[(a + b*ArcCosh[-1 + d*x^2])^(-3/2), x]", //
				"-((Sqrt[d*x^2]*Sqrt[-2 + d*x^2])/(b*d*x*Sqrt[a + b*ArcCosh[-1 + d*x^2]])) + (Sqrt[Pi/2]*Cosh[ArcCosh[-1 + d*x^2]/2]*Erfi[Sqrt[a + b*ArcCosh[-1 + d*x^2]]/(Sqrt[2]*Sqrt[b])]*(Cosh[a/(2*b)] - Sinh[a/(2*b)]))/(b^(3/2)*d*x) + (Sqrt[Pi/2]*Cosh[ArcCosh[-1 + d*x^2]/2]*Erf[Sqrt[a + b*ArcCosh[-1 + d*x^2]]/(Sqrt[2]*Sqrt[b])]*(Cosh[a/(2*b)] + Sinh[a/(2*b)]))/(b^(3/2)*d*x)", //
				5886);
	}

	// {5912}
	public void test0130() {
		check(//
				"Integrate[(a + b*ArcTanh[c*x])/x, x]", //
				"a*Log[x] - (b*PolyLog[2, -(c*x)])/2 + (b*PolyLog[2, c*x])/2", //
				5912);
	}

	// {5922}
	public void test0131() {
		check(//
				"Integrate[(a + b*ArcTanh[c*x])^2/(d + e*x), x]", //
				"-(((a + b*ArcTanh[c*x])^2*Log[2/(1 + c*x)])/e) + ((a + b*ArcTanh[c*x])^2*Log[(2*c*(d + e*x))/((c*d + e)*(1 + c*x))])/e + (b*(a + b*ArcTanh[c*x])*PolyLog[2, 1 - 2/(1 + c*x)])/e - (b*(a + b*ArcTanh[c*x])*PolyLog[2, 1 - (2*c*(d + e*x))/((c*d + e)*(1 + c*x))])/e + (b^2*PolyLog[3, 1 - 2/(1 + c*x)])/(2*e) - (b^2*PolyLog[3, 1 - (2*c*(d + e*x))/((c*d + e)*(1 + c*x))])/(2*e)", //
				5922);
	}

	// {5924}
	public void test0132() {
		check(//
				"Integrate[(a + b*ArcTanh[c*x])^3/(d + e*x), x]", //
				"-(((a + b*ArcTanh[c*x])^3*Log[2/(1 + c*x)])/e) + ((a + b*ArcTanh[c*x])^3*Log[(2*c*(d + e*x))/((c*d + e)*(1 + c*x))])/e + (3*b*(a + b*ArcTanh[c*x])^2*PolyLog[2, 1 - 2/(1 + c*x)])/(2*e) - (3*b*(a + b*ArcTanh[c*x])^2*PolyLog[2, 1 - (2*c*(d + e*x))/((c*d + e)*(1 + c*x))])/(2*e) + (3*b^2*(a + b*ArcTanh[c*x])*PolyLog[3, 1 - 2/(1 + c*x)])/(2*e) - (3*b^2*(a + b*ArcTanh[c*x])*PolyLog[3, 1 - (2*c*(d + e*x))/((c*d + e)*(1 + c*x))])/(2*e) + (3*b^3*PolyLog[4, 1 - 2/(1 + c*x)])/(4*e) - (3*b^3*PolyLog[4, 1 - (2*c*(d + e*x))/((c*d + e)*(1 + c*x))])/(4*e)", //
				5924);
	}

	// {5922}
	public void test0133() {
		check(//
				"Integrate[(a + b*ArcTanh[c*x])^2/(d + e*x), x]", //
				"-(((a + b*ArcTanh[c*x])^2*Log[2/(1 + c*x)])/e) + ((a + b*ArcTanh[c*x])^2*Log[(2*c*(d + e*x))/((c*d + e)*(1 + c*x))])/e + (b*(a + b*ArcTanh[c*x])*PolyLog[2, 1 - 2/(1 + c*x)])/e - (b*(a + b*ArcTanh[c*x])*PolyLog[2, 1 - (2*c*(d + e*x))/((c*d + e)*(1 + c*x))])/e + (b^2*PolyLog[3, 1 - 2/(1 + c*x)])/(2*e) - (b^2*PolyLog[3, 1 - (2*c*(d + e*x))/((c*d + e)*(1 + c*x))])/(2*e)", //
				5922);
	}

	// {5948}
	public void test0134() {
		check(//
				"Integrate[ArcTanh[a*x]/(1 - a^2*x^2), x]", //
				"ArcTanh[a*x]^2/(2*a)", //
				5948);
	}

	// {5948}
	public void test0135() {
		check(//
				"Integrate[ArcTanh[a*x]^2/(1 - a^2*x^2), x]", //
				"ArcTanh[a*x]^3/(3*a)", //
				5948);
	}

	// {5948}
	public void test0136() {
		check(//
				"Integrate[ArcTanh[a*x]^3/(1 - a^2*x^2), x]", //
				"ArcTanh[a*x]^4/(4*a)", //
				5948);
	}

	// {5948}
	public void test0137() {
		check(//
				"Integrate[Sqrt[ArcTanh[a*x]]/(1 - a^2*x^2), x]", //
				"(2*ArcTanh[a*x]^(3/2))/(3*a)", //
				5948);
	}

	// {5946}
	public void test0138() {
		check(//
				"Integrate[1/((1 - a^2*x^2)*ArcTanh[a*x]), x]", //
				"Log[ArcTanh[a*x]]/a", //
				5946);
	}

	// {5948}
	public void test0140() {
		check(//
				"Integrate[1/((1 - a^2*x^2)*ArcTanh[a*x]^2), x]", //
				"-(1/(a*ArcTanh[a*x]))", //
				5948);
	}

	// {5948}
	public void test0143() {
		check(//
				"Integrate[1/((1 - a^2*x^2)*ArcTanh[a*x]^3), x]", //
				"-1/(2*a*ArcTanh[a*x]^2)", //
				5948);
	}

	// {5948}
	public void test0145() {
		check(//
				"Integrate[ArcTanh[a*x]^p/(1 - a^2*x^2), x]", //
				"ArcTanh[a*x]^(1 + p)/(a*(1 + p))", //
				5948);
	}

	// {5950}
	public void test0146() {
		check(//
				"Integrate[ArcTanh[a*x]/Sqrt[1 - a^2*x^2], x]", //
				"(-2*ArcTan[Sqrt[1 - a*x]/Sqrt[1 + a*x]]*ArcTanh[a*x])/a - (I*PolyLog[2, ((-I)*Sqrt[1 - a*x])/Sqrt[1 + a*x]])/a + (I*PolyLog[2, (I*Sqrt[1 - a*x])/Sqrt[1 + a*x]])/a", //
				5950);
	}

	// {6018}
	public void test0147() {
		check(//
				"Integrate[ArcTanh[a*x]/(x*Sqrt[1 - a^2*x^2]), x]", //
				"-2*ArcTanh[a*x]*ArcTanh[Sqrt[1 - a*x]/Sqrt[1 + a*x]] + PolyLog[2, -(Sqrt[1 - a*x]/Sqrt[1 + a*x])] - PolyLog[2, Sqrt[1 - a*x]/Sqrt[1 + a*x]]", //
				6018);
	}

	// {5958}
	public void test0148() {
		check(//
				"Integrate[ArcTanh[a*x]/(1 - a^2*x^2)^(3/2), x]", //
				"-(1/(a*Sqrt[1 - a^2*x^2])) + (x*ArcTanh[a*x])/Sqrt[1 - a^2*x^2]", //
				5958);
	}

	// {5958}
	public void test0149() {
		check(//
				"Integrate[ArcTanh[a*x]/(c - a^2*c*x^2)^(3/2), x]", //
				"-(1/(a*c*Sqrt[c - a^2*c*x^2])) + (x*ArcTanh[a*x])/(c*Sqrt[c - a^2*c*x^2])", //
				5958);
	}

	// {5946}
	public void test0150() {
		check(//
				"Integrate[1/((a - a*x^2)*(b - 2*b*ArcTanh[x])), x]", //
				"-Log[1 - 2*ArcTanh[x]]/(2*a*b)", //
				5946);
	}

	// {5958}
	public void test0151() {
		check(//
				"Integrate[ArcTanh[x]/(a - a*x^2)^(3/2), x]", //
				"-(1/(a*Sqrt[a - a*x^2])) + (x*ArcTanh[x])/(a*Sqrt[a - a*x^2])", //
				5958);
	}

	// {2167}
	public void test0154() {
		check(//
				"Integrate[ArcTanh[Tanh[a + b*x]]^2/x^4, x]", //
				"ArcTanh[Tanh[a + b*x]]^3/(3*x^3*(b*x - ArcTanh[Tanh[a + b*x]]))", //
				2167);
	}

	// {2167}
	public void test0155() {
		check(//
				"Integrate[ArcTanh[Tanh[a + b*x]]^3/x^5, x]", //
				"ArcTanh[Tanh[a + b*x]]^4/(4*x^4*(b*x - ArcTanh[Tanh[a + b*x]]))", //
				2167);
	}

	// {2167}
	public void test0156() {
		check(//
				"Integrate[ArcTanh[Tanh[a + b*x]]^4/x^6, x]", //
				"ArcTanh[Tanh[a + b*x]]^5/(5*x^5*(b*x - ArcTanh[Tanh[a + b*x]]))", //
				2167);
	}

	// {2164}
	public void test0157() {
		check(//
				"Integrate[x^m/ArcTanh[Tanh[a + b*x]], x]", //
				"-((x^(1 + m)*Hypergeometric2F1[1, 1 + m, 2 + m, (b*x)/(b*x - ArcTanh[Tanh[a + b*x]])])/((1 + m)*(b*x - ArcTanh[Tanh[a + b*x]])))", //
				2164);
	}

	// {2161}
	public void test0158() {
		check(//
				"Integrate[1/(x*Sqrt[ArcTanh[Tanh[a + b*x]]]), x]", //
				"(2*ArcTan[Sqrt[ArcTanh[Tanh[a + b*x]]]/Sqrt[b*x - ArcTanh[Tanh[a + b*x]]]])/Sqrt[b*x - ArcTanh[Tanh[a + b*x]]]", //
				2161);
	}

	// {2162}
	public void test0159() {
		check(//
				"Integrate[1/(Sqrt[x]*ArcTanh[Tanh[a + b*x]]), x]", //
				"(-2*ArcTanh[(Sqrt[b]*Sqrt[x])/Sqrt[b*x - ArcTanh[Tanh[a + b*x]]]])/(Sqrt[b]*Sqrt[b*x - ArcTanh[Tanh[a + b*x]]])", //
				2162);
	}

	// {2167}
	public void test0160() {
		check(//
				"Integrate[Sqrt[ArcTanh[Tanh[a + b*x]]]/x^(5/2), x]", //
				"(2*ArcTanh[Tanh[a + b*x]]^(3/2))/(3*x^(3/2)*(b*x - ArcTanh[Tanh[a + b*x]]))", //
				2167);
	}

	// {2167}
	public void test0161() {
		check(//
				"Integrate[ArcTanh[Tanh[a + b*x]]^(3/2)/x^(7/2), x]", //
				"(2*ArcTanh[Tanh[a + b*x]]^(5/2))/(5*x^(5/2)*(b*x - ArcTanh[Tanh[a + b*x]]))", //
				2167);
	}

	// {2167}
	public void test0162() {
		check(//
				"Integrate[ArcTanh[Tanh[a + b*x]]^(5/2)/x^(9/2), x]", //
				"(2*ArcTanh[Tanh[a + b*x]]^(7/2))/(7*x^(7/2)*(b*x - ArcTanh[Tanh[a + b*x]]))", //
				2167);
	}

	// {2165}
	public void test0163() {
		check(//
				"Integrate[1/(Sqrt[x]*Sqrt[ArcTanh[Tanh[a + b*x]]]), x]", //
				"(2*ArcTanh[(Sqrt[b]*Sqrt[x])/Sqrt[ArcTanh[Tanh[a + b*x]]]])/Sqrt[b]", //
				2165);
	}

	// {2167}
	public void test0164() {
		check(//
				"Integrate[1/(x^(3/2)*Sqrt[ArcTanh[Tanh[a + b*x]]]), x]", //
				"(2*Sqrt[ArcTanh[Tanh[a + b*x]]])/(Sqrt[x]*(b*x - ArcTanh[Tanh[a + b*x]]))", //
				2167);
	}

	// {2167}
	public void test0165() {
		check(//
				"Integrate[1/(Sqrt[x]*ArcTanh[Tanh[a + b*x]]^(3/2)), x]", //
				"(-2*Sqrt[x])/((b*x - ArcTanh[Tanh[a + b*x]])*Sqrt[ArcTanh[Tanh[a + b*x]]])", //
				2167);
	}

	// {2167}
	public void test0166() {
		check(//
				"Integrate[Sqrt[x]/ArcTanh[Tanh[a + b*x]]^(5/2), x]", //
				"(-2*x^(3/2))/(3*(b*x - ArcTanh[Tanh[a + b*x]])*ArcTanh[Tanh[a + b*x]]^(3/2))", //
				2167);
	}

	// {2173}
	public void test0167() {
		check(//
				"Integrate[x^m*ArcTanh[Tanh[a + b*x]]^n, x]", //
				"(x^m*ArcTanh[Tanh[a + b*x]]^(1 + n)*Hypergeometric2F1[-m, 1 + n, 2 + n, -(ArcTanh[Tanh[a + b*x]]/(b*x - ArcTanh[Tanh[a + b*x]]))])/(b*(1 + n)*((b*x)/(b*x - ArcTanh[Tanh[a + b*x]]))^m)", //
				2173);
	}

	// {2164}
	public void test0168() {
		check(//
				"Integrate[ArcTanh[Tanh[a + b*x]]^n/x, x]", //
				"(ArcTanh[Tanh[a + b*x]]^(1 + n)*Hypergeometric2F1[1, 1 + n, 2 + n, -(ArcTanh[Tanh[a + b*x]]/(b*x - ArcTanh[Tanh[a + b*x]]))])/((1 + n)*(b*x - ArcTanh[Tanh[a + b*x]]))", //
				2164);
	}

	// {6137}
	public void test0169() {
		check(//
				"Integrate[E^ArcTanh[a*x]/(c - a^2*c*x^2), x]", //
				"E^ArcTanh[a*x]/(a*c)", //
				6137);
	}

	// {6137}
	public void test0170() {
		check(//
				"Integrate[E^(3*ArcTanh[a*x])/(c - a^2*c*x^2), x]", //
				"E^(3*ArcTanh[a*x])/(3*a*c)", //
				6137);
	}

	// {6137}
	public void test0171() {
		check(//
				"Integrate[1/(E^ArcTanh[a*x]*(c - a^2*c*x^2)), x]", //
				"-(1/(a*c*E^ArcTanh[a*x]))", //
				6137);
	}

	// {6137}
	public void test0172() {
		check(//
				"Integrate[1/(E^(3*ArcTanh[a*x])*(c - a^2*c*x^2)), x]", //
				"-1/(3*a*c*E^(3*ArcTanh[a*x]))", //
				6137);
	}

	// {6135}
	public void test0173() {
		check(//
				"Integrate[E^(ArcTanh[a*x]/2)/(1 - a^2*x^2)^(3/2), x]", //
				"(-2*E^(ArcTanh[a*x]/2)*(1 - 2*a*x))/(3*a*Sqrt[1 - a^2*x^2])", //
				6135);
	}

	// {6135}
	public void test0174() {
		check(//
				"Integrate[E^(ArcTanh[a*x]/2)/(c - a^2*c*x^2)^(3/2), x]", //
				"(-2*E^(ArcTanh[a*x]/2)*(1 - 2*a*x))/(3*a*c*Sqrt[c - a^2*c*x^2])", //
				6135);
	}

	// {6146}
	public void test0175() {
		check(//
				"Integrate[(E^(ArcTanh[a*x]/2)*x^2)/(c - a^2*c*x^2)^(9/8), x]", //
				"(4*E^(ArcTanh[a*x]/2)*(2 - a*x))/(3*a^3*c*(c - a^2*c*x^2)^(1/8))", //
				6146);
	}

	// {6137}
	public void test0176() {
		check(//
				"Integrate[E^(n*ArcTanh[a*x])/(c - a^2*c*x^2), x]", //
				"E^(n*ArcTanh[a*x])/(a*c*n)", //
				6137);
	}

	// {6144}
	public void test0177() {
		check(//
				"Integrate[(E^(n*ArcTanh[a*x])*x)/(c - a^2*c*x^2)^(3/2), x]", //
				"(E^(n*ArcTanh[a*x])*(1 - a*n*x))/(a^2*c*(1 - n^2)*Sqrt[c - a^2*c*x^2])", //
				6144);
	}

	// {6135}
	public void test0178() {
		check(//
				"Integrate[E^(n*ArcTanh[a*x])/(c - a^2*c*x^2)^(3/2), x]", //
				"-((E^(n*ArcTanh[a*x])*(n - a*x))/(a*c*(1 - n^2)*Sqrt[c - a^2*c*x^2]))", //
				6135);
	}

	// {6146}
	public void test0179() {
		check(//
				"Integrate[E^(n*ArcTanh[a*x])*x^2*(c - a^2*c*x^2)^(-1 - n^2/2), x]", //
				"(E^(n*ArcTanh[a*x])*(1 - a*n*x))/(a^3*c*n*(1 - n^2)*(c - a^2*c*x^2)^(n^2/2))", //
				6146);
	}

	// {5913}
	public void test0180() {
		check(//
				"Integrate[ArcCoth[a*x]/x, x]", //
				"PolyLog[2, -(1/(a*x))]/2 - PolyLog[2, 1/(a*x)]/2", //
				5913);
	}

	// {5923}
	public void test0181() {
		check(//
				"Integrate[ArcCoth[c*x]^2/(d + e*x), x]", //
				"-((ArcCoth[c*x]^2*Log[2/(1 + c*x)])/e) + (ArcCoth[c*x]^2*Log[(2*c*(d + e*x))/((c*d + e)*(1 + c*x))])/e + (ArcCoth[c*x]*PolyLog[2, 1 - 2/(1 + c*x)])/e - (ArcCoth[c*x]*PolyLog[2, 1 - (2*c*(d + e*x))/((c*d + e)*(1 + c*x))])/e + PolyLog[3, 1 - 2/(1 + c*x)]/(2*e) - PolyLog[3, 1 - (2*c*(d + e*x))/((c*d + e)*(1 + c*x))]/(2*e)", //
				5923);
	}

	// {5959}
	public void test0182() {
		check(//
				"Integrate[ArcCoth[x]/(a - a*x^2)^(3/2), x]", //
				"-(1/(a*Sqrt[a - a*x^2])) + (x*ArcCoth[x])/(a*Sqrt[a - a*x^2])", //
				5959);
	}

	// {5947}
	public void test0183() {
		check(//
				"Integrate[1/((1 - x^2)*ArcCoth[x]), x]", //
				"Log[ArcCoth[x]]", //
				5947);
	}

	// {5949}
	public void test0184() {
		check(//
				"Integrate[ArcCoth[x]^n/(1 - x^2), x]", //
				"ArcCoth[x]^(1 + n)/(1 + n)", //
				5949);
	}

	// {5949}
	public void test0185() {
		check(//
				"Integrate[ArcCoth[x]/(1 - x^2), x]", //
				"ArcCoth[x]^2/2", //
				5949);
	}

	// {2167}
	public void test0188() {
		check(//
				"Integrate[ArcCoth[Tanh[a + b*x]]^2/x^4, x]", //
				"ArcCoth[Tanh[a + b*x]]^3/(3*x^3*(b*x - ArcCoth[Tanh[a + b*x]]))", //
				2167);
	}

	// {2167}
	public void test0189() {
		check(//
				"Integrate[ArcCoth[Tanh[a + b*x]]^3/x^5, x]", //
				"ArcCoth[Tanh[a + b*x]]^4/(4*x^4*(b*x - ArcCoth[Tanh[a + b*x]]))", //
				2167);
	}

	// {2164}
	public void test0190() {
		check(//
				"Integrate[x^m/ArcCoth[Tanh[a + b*x]], x]", //
				"-((x^(1 + m)*Hypergeometric2F1[1, 1 + m, 2 + m, (b*x)/(b*x - ArcCoth[Tanh[a + b*x]])])/((1 + m)*(b*x - ArcCoth[Tanh[a + b*x]])))", //
				2164);
	}

	// {2173}
	public void test0191() {
		check(//
				"Integrate[x^m*ArcCoth[Tanh[a + b*x]]^n, x]", //
				"(x^m*ArcCoth[Tanh[a + b*x]]^(1 + n)*Hypergeometric2F1[-m, 1 + n, 2 + n, -(ArcCoth[Tanh[a + b*x]]/(b*x - ArcCoth[Tanh[a + b*x]]))])/(b*(1 + n)*((b*x)/(b*x - ArcCoth[Tanh[a + b*x]]))^m)", //
				2173);
	}

	// {2164}
	public void test0192() {
		check(//
				"Integrate[ArcCoth[Tanh[a + b*x]]^n/x, x]", //
				"(ArcCoth[Tanh[a + b*x]]^(1 + n)*Hypergeometric2F1[1, 1 + n, 2 + n, -(ArcCoth[Tanh[a + b*x]]/(b*x - ArcCoth[Tanh[a + b*x]]))])/((1 + n)*(b*x - ArcCoth[Tanh[a + b*x]]))", //
				2164);
	}

	// {5947}
	public void test0193() {
		check(//
				"Integrate[1/((a - a*x^2)*(b - 2*b*ArcCoth[x])), x]", //
				"-Log[1 - 2*ArcCoth[x]]/(2*a*b)", //
				5947);
	}

	// {6174}
	public void test0194() {
		check(//
				"Integrate[E^ArcCoth[a*x]*Sqrt[c - a*c*x], x]", //
				"(2*E^ArcCoth[a*x]*(1 + a*x)*Sqrt[c - a*c*x])/(3*a)", //
				6174);
	}

	// {6174}
	public void test0195() {
		check(//
				"Integrate[E^(3*ArcCoth[a*x])*(c - a*c*x)^(3/2), x]", //
				"(2*E^(3*ArcCoth[a*x])*(1 + a*x)*(c - a*c*x)^(3/2))/(5*a)", //
				6174);
	}

	// {6174}
	public void test0196() {
		check(//
				"Integrate[1/(E^ArcCoth[a*x]*Sqrt[c - a*c*x]), x]", //
				"(2*(1 + a*x))/(a*E^ArcCoth[a*x]*Sqrt[c - a*c*x])", //
				6174);
	}

	// {6174}
	public void test0197() {
		check(//
				"Integrate[1/(E^(3*ArcCoth[a*x])*(c - a*c*x)^(3/2)), x]", //
				"(-2*(1 + a*x))/(a*E^(3*ArcCoth[a*x])*(c - a*c*x)^(3/2))", //
				6174);
	}

	// {6174}
	public void test0198() {
		check(//
				"Integrate[E^ArcCoth[a*x]*Sqrt[c - a*c*x], x]", //
				"(2*E^ArcCoth[a*x]*(1 + a*x)*Sqrt[c - a*c*x])/(3*a)", //
				6174);
	}

	// {6174}
	public void test0199() {
		check(//
				"Integrate[E^ArcCoth[x]*Sqrt[1 - x], x]", //
				"(2*E^ArcCoth[x]*Sqrt[1 - x]*(1 + x))/3", //
				6174);
	}

	// {6174}
	public void test0200() {
		check(//
				"Integrate[E^(n*ArcCoth[a*x])*(c - a*c*x)^(n/2), x]", //
				"(2*E^(n*ArcCoth[a*x])*(1 + a*x)*(c - a*c*x)^(n/2))/(a*(2 + n))", //
				6174);
	}

	// {6183}
	public void test0201() {
		check(//
				"Integrate[E^ArcCoth[a*x]/(c - a^2*c*x^2), x]", //
				"E^ArcCoth[a*x]/(a*c)", //
				6183);
	}

	// {6183}
	public void test0202() {
		check(//
				"Integrate[E^(3*ArcCoth[a*x])/(c - a^2*c*x^2), x]", //
				"E^(3*ArcCoth[a*x])/(3*a*c)", //
				6183);
	}

	// {6183}
	public void test0203() {
		check(//
				"Integrate[1/(E^ArcCoth[a*x]*(c - a^2*c*x^2)), x]", //
				"-(1/(a*c*E^ArcCoth[a*x]))", //
				6183);
	}

	// {6183}
	public void test0204() {
		check(//
				"Integrate[1/(E^(3*ArcCoth[a*x])*(c - a^2*c*x^2)), x]", //
				"-1/(3*a*c*E^(3*ArcCoth[a*x]))", //
				6183);
	}

	// {6183}
	public void test0205() {
		check(//
				"Integrate[E^(n*ArcCoth[a*x])/(c - a^2*c*x^2), x]", //
				"E^(n*ArcCoth[a*x])/(a*c*n)", //
				6183);
	}

	// {6184}
	public void test0206() {
		check(//
				"Integrate[E^(n*ArcCoth[a*x])/(c - a^2*c*x^2)^(3/2), x]", //
				"-((E^(n*ArcCoth[a*x])*(n - a*x))/(a*c*(1 - n^2)*Sqrt[c - a^2*c*x^2]))", //
				6184);
	}

	// {6186}
	public void test0207() {
		check(//
				"Integrate[(E^(n*ArcCoth[a*x])*x)/(c - a^2*c*x^2)^(3/2), x]", //
				"(E^(n*ArcCoth[a*x])*(1 - a*n*x))/(a^2*c*(1 - n^2)*Sqrt[c - a^2*c*x^2])", //
				6186);
	}

	// {6184}
	public void test0208() {
		check(//
				"Integrate[E^(n*ArcCoth[a*x])/(c - a^2*c*x^2)^(3/2), x]", //
				"-((E^(n*ArcCoth[a*x])*(n - a*x))/(a*c*(1 - n^2)*Sqrt[c - a^2*c*x^2]))", //
				6184);
	}
}
