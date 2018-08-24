package org.matheclipse.core.rubi;

public class InverseTrigFunctions extends AbstractRubiTestCase {

	public InverseTrigFunctions(String name) {
		super(name, false);
	}

	// {4641}
	public void test0008() {
		check("Integrate[ArcSin[a*x]/Sqrt[1 - a^2*x^2], x]", "ArcSin[a*x]^2/(2*a)", //
				4641);
	}

	// {4711}
	public void test0009() {
		check("Integrate[((f*x)^(3/2)*(a + b*ArcSin[c*x]))/Sqrt[1 - c^2*x^2], x]", "(2*(f*x)^(5/2)*(a + b*ArcSin[c*x])*Hypergeometric2F1[1/2, 5/4, 9/4, c^2*x^2])/(5*f) - (4*b*c*(f*x)^(7/2)*HypergeometricPFQ[{1, 7/4, 7/4}, {9/4, 11/4}, c^2*x^2])/(35*f^2)", //
				4711);
	}

	// {4711}
	public void test0010() {
		check("Integrate[(x^m*ArcSin[a*x])/Sqrt[1 - a^2*x^2], x]", "(x^(1 + m)*ArcSin[a*x]*Hypergeometric2F1[1/2, (1 + m)/2, (3 + m)/2, a^2*x^2])/(1 + m) - (a*x^(2 + m)*HypergeometricPFQ[{1, 1 + m/2, 1 + m/2}, {3/2 + m/2, 2 + m/2}, a^2*x^2])/(2 + 3*m + m^2)", //
				4711);
	}

	// {4641}
	public void test0011() {
		check("Integrate[ArcSin[a*x]^2/Sqrt[1 - a^2*x^2], x]", "ArcSin[a*x]^3/(3*a)", //
				4641);
	}

	// {4641}
	public void test0012() {
		check("Integrate[ArcSin[a*x]^3/Sqrt[1 - a^2*x^2], x]", "ArcSin[a*x]^4/(4*a)", //
				4641);
	}

	// {4639}
	public void test0013() {
		check("Integrate[1/(Sqrt[1 - a^2*x^2]*ArcSin[a*x]), x]", "Log[ArcSin[a*x]]/a", //
				4639);
	}

	// {4639}
	public void test0014() {
		check("Integrate[1/(Sqrt[1 - c^2*x^2]*(a + b*ArcSin[c*x])), x]", "Log[a + b*ArcSin[c*x]]/(b*c)", //
				4639);
	}

	// {4641}
	public void test0022() {
		check("Integrate[1/(Sqrt[1 - c^2*x^2]*(a + b*ArcSin[c*x])^2), x]", "-(1/(b*c*(a + b*ArcSin[c*x])))", //
				4641);
	}

	// {4641}
	public void test0028() {
		check("Integrate[1/(Sqrt[1 - a^2*x^2]*ArcSin[a*x]^3), x]", "-1/(2*a*ArcSin[a*x]^2)", //
				4641);
	}

	// {4641}
	public void test0038() {
		check("Integrate[ArcSin[a*x]^n/Sqrt[1 - a^2*x^2], x]", "ArcSin[a*x]^(1 + n)/(a*(1 + n))", //
				4641);
	}

	// {4816}
	public void test0047() {
		check("Integrate[(a + b*ArcSin[1 + d*x^2])^(-1), x]", "-(x*CosIntegral[(a + b*ArcSin[1 + d*x^2])/(2*b)]*(Cos[a/(2*b)] - Sin[a/(2*b)]))/(2*b*(Cos[ArcSin[1 + d*x^2]/2] - Sin[ArcSin[1 + d*x^2]/2])) - (x*(Cos[a/(2*b)] + Sin[a/(2*b)])*SinIntegral[(a + b*ArcSin[1 + d*x^2])/(2*b)])/(2*b*(Cos[ArcSin[1 + d*x^2]/2] - Sin[ArcSin[1 + d*x^2]/2]))", //
				4816);
	}

	// {4825}
	public void test0048() {
		check("Integrate[(a + b*ArcSin[1 + d*x^2])^(-2), x]", "-Sqrt[-2*d*x^2 - d^2*x^4]/(2*b*d*x*(a + b*ArcSin[1 + d*x^2])) - (x*CosIntegral[(a + b*ArcSin[1 + d*x^2])/(2*b)]*(Cos[a/(2*b)] + Sin[a/(2*b)]))/(4*b^2*(Cos[ArcSin[1 + d*x^2]/2] - Sin[ArcSin[1 + d*x^2]/2])) + (x*(Cos[a/(2*b)] - Sin[a/(2*b)])*SinIntegral[(a + b*ArcSin[1 + d*x^2])/(2*b)])/(4*b^2*(Cos[ArcSin[1 + d*x^2]/2] - Sin[ArcSin[1 + d*x^2]/2]))", //
				4825);
	}

	// {4816}
	public void test0049() {
		check("Integrate[(a - b*ArcSin[1 - d*x^2])^(-1), x]", "(x*CosIntegral[-(a - b*ArcSin[1 - d*x^2])/(2*b)]*(Cos[a/(2*b)] + Sin[a/(2*b)]))/(2*b*(Cos[ArcSin[1 - d*x^2]/2] - Sin[ArcSin[1 - d*x^2]/2])) - (x*(Cos[a/(2*b)] - Sin[a/(2*b)])*SinIntegral[a/(2*b) - ArcSin[1 - d*x^2]/2])/(2*b*(Cos[ArcSin[1 - d*x^2]/2] - Sin[ArcSin[1 - d*x^2]/2]))", //
				4816);
	}

	// {4825}
	public void test0050() {
		check("Integrate[(a - b*ArcSin[1 - d*x^2])^(-2), x]", "-Sqrt[2*d*x^2 - d^2*x^4]/(2*b*d*x*(a - b*ArcSin[1 - d*x^2])) - (x*CosIntegral[-(a - b*ArcSin[1 - d*x^2])/(2*b)]*(Cos[a/(2*b)] - Sin[a/(2*b)]))/(4*b^2*(Cos[ArcSin[1 - d*x^2]/2] - Sin[ArcSin[1 - d*x^2]/2])) - (x*(Cos[a/(2*b)] + Sin[a/(2*b)])*SinIntegral[a/(2*b) - ArcSin[1 - d*x^2]/2])/(4*b^2*(Cos[ArcSin[1 - d*x^2]/2] - Sin[ArcSin[1 - d*x^2]/2]))", //
				4825);
	}

	// {4811}
	public void test0051() {
		check("Integrate[Sqrt[a + b*ArcSin[1 + d*x^2]], x]", "x*Sqrt[a + b*ArcSin[1 + d*x^2]] + (Sqrt[Pi]*x*FresnelS[(Sqrt[b^(-1)]*Sqrt[a + b*ArcSin[1 + d*x^2]])/Sqrt[Pi]]*(Cos[a/(2*b)] - Sin[a/(2*b)]))/(Sqrt[b^(-1)]*(Cos[ArcSin[1 + d*x^2]/2] - Sin[ArcSin[1 + d*x^2]/2])) - (Sqrt[Pi]*x*FresnelC[(Sqrt[b^(-1)]*Sqrt[a + b*ArcSin[1 + d*x^2]])/Sqrt[Pi]]*(Cos[a/(2*b)] + Sin[a/(2*b)]))/(Sqrt[b^(-1)]*(Cos[ArcSin[1 + d*x^2]/2] - Sin[ArcSin[1 + d*x^2]/2]))", //
				4811);
	}

	// {4819}
	public void test0052() {
		check("Integrate[1/Sqrt[a + b*ArcSin[1 + d*x^2]], x]", "-((Sqrt[Pi]*x*FresnelC[Sqrt[a + b*ArcSin[1 + d*x^2]]/(Sqrt[b]*Sqrt[Pi])]*(Cos[a/(2*b)] - Sin[a/(2*b)]))/(Sqrt[b]*(Cos[ArcSin[1 + d*x^2]/2] - Sin[ArcSin[1 + d*x^2]/2]))) - (Sqrt[Pi]*x*FresnelS[Sqrt[a + b*ArcSin[1 + d*x^2]]/(Sqrt[b]*Sqrt[Pi])]*(Cos[a/(2*b)] + Sin[a/(2*b)]))/(Sqrt[b]*(Cos[ArcSin[1 + d*x^2]/2] - Sin[ArcSin[1 + d*x^2]/2]))", //
				4819);
	}

	// {4822}
	public void test0053() {
		check("Integrate[(a + b*ArcSin[1 + d*x^2])^(-3/2), x]", "-(Sqrt[-2*d*x^2 - d^2*x^4]/(b*d*x*Sqrt[a + b*ArcSin[1 + d*x^2]])) + ((b^(-1))^(3/2)*Sqrt[Pi]*x*FresnelS[(Sqrt[b^(-1)]*Sqrt[a + b*ArcSin[1 + d*x^2]])/Sqrt[Pi]]*(Cos[a/(2*b)] - Sin[a/(2*b)]))/(Cos[ArcSin[1 + d*x^2]/2] - Sin[ArcSin[1 + d*x^2]/2]) - ((b^(-1))^(3/2)*Sqrt[Pi]*x*FresnelC[(Sqrt[b^(-1)]*Sqrt[a + b*ArcSin[1 + d*x^2]])/Sqrt[Pi]]*(Cos[a/(2*b)] + Sin[a/(2*b)]))/(Cos[ArcSin[1 + d*x^2]/2] - Sin[ArcSin[1 + d*x^2]/2])", //
				4822);
	}

	// {4811}
	public void test0054() {
		check("Integrate[Sqrt[a - b*ArcSin[1 - d*x^2]], x]", "x*Sqrt[a - b*ArcSin[1 - d*x^2]] - (Sqrt[Pi]*x*FresnelC[(Sqrt[-b^(-1)]*Sqrt[a - b*ArcSin[1 - d*x^2]])/Sqrt[Pi]]*(Cos[a/(2*b)] - Sin[a/(2*b)]))/(Sqrt[-b^(-1)]*(Cos[ArcSin[1 - d*x^2]/2] - Sin[ArcSin[1 - d*x^2]/2])) + (Sqrt[Pi]*x*FresnelS[(Sqrt[-b^(-1)]*Sqrt[a - b*ArcSin[1 - d*x^2]])/Sqrt[Pi]]*(Cos[a/(2*b)] + Sin[a/(2*b)]))/(Sqrt[-b^(-1)]*(Cos[ArcSin[1 - d*x^2]/2] - Sin[ArcSin[1 - d*x^2]/2]))", //
				4811);
	}

	// {4819}
	public void test0055() {
		check("Integrate[1/Sqrt[a - b*ArcSin[1 - d*x^2]], x]", "-((Sqrt[Pi]*x*FresnelS[Sqrt[a - b*ArcSin[1 - d*x^2]]/(Sqrt[-b]*Sqrt[Pi])]*(Cos[a/(2*b)] - Sin[a/(2*b)]))/(Sqrt[-b]*(Cos[ArcSin[1 - d*x^2]/2] - Sin[ArcSin[1 - d*x^2]/2]))) - (Sqrt[Pi]*x*FresnelC[Sqrt[a - b*ArcSin[1 - d*x^2]]/(Sqrt[-b]*Sqrt[Pi])]*(Cos[a/(2*b)] + Sin[a/(2*b)]))/(Sqrt[-b]*(Cos[ArcSin[1 - d*x^2]/2] - Sin[ArcSin[1 - d*x^2]/2]))", //
				4819);
	}

	// {4822}
	public void test0056() {
		check("Integrate[(a - b*ArcSin[1 - d*x^2])^(-3/2), x]", "-(Sqrt[2*d*x^2 - d^2*x^4]/(b*d*x*Sqrt[a - b*ArcSin[1 - d*x^2]])) - ((-b^(-1))^(3/2)*Sqrt[Pi]*x*FresnelC[(Sqrt[-b^(-1)]*Sqrt[a - b*ArcSin[1 - d*x^2]])/Sqrt[Pi]]*(Cos[a/(2*b)] - Sin[a/(2*b)]))/(Cos[ArcSin[1 - d*x^2]/2] - Sin[ArcSin[1 - d*x^2]/2]) + ((-b^(-1))^(3/2)*Sqrt[Pi]*x*FresnelS[(Sqrt[-b^(-1)]*Sqrt[a - b*ArcSin[1 - d*x^2]])/Sqrt[Pi]]*(Cos[a/(2*b)] + Sin[a/(2*b)]))/(Cos[ArcSin[1 - d*x^2]/2] - Sin[ArcSin[1 - d*x^2]/2])", //
				4822);
	}

	// {4817}
	public void test0064() {
		check("Integrate[(a + b*ArcCos[1 + d*x^2])^(-1), x]", "(x*Cos[a/(2*b)]*CosIntegral[(a + b*ArcCos[1 + d*x^2])/(2*b)])/(Sqrt[2]*b*Sqrt[-(d*x^2)]) + (x*Sin[a/(2*b)]*SinIntegral[(a + b*ArcCos[1 + d*x^2])/(2*b)])/(Sqrt[2]*b*Sqrt[-(d*x^2)])", //
				4817);
	}

	// {4826}
	public void test0065() {
		check("Integrate[(a + b*ArcCos[1 + d*x^2])^(-2), x]", "Sqrt[-2*d*x^2 - d^2*x^4]/(2*b*d*x*(a + b*ArcCos[1 + d*x^2])) + (x*CosIntegral[(a + b*ArcCos[1 + d*x^2])/(2*b)]*Sin[a/(2*b)])/(2*Sqrt[2]*b^2*Sqrt[-(d*x^2)]) - (x*Cos[a/(2*b)]*SinIntegral[(a + b*ArcCos[1 + d*x^2])/(2*b)])/(2*Sqrt[2]*b^2*Sqrt[-(d*x^2)])", //
				4826);
	}

	// {4818}
	public void test0066() {
		check("Integrate[(a + b*ArcCos[-1 + d*x^2])^(-1), x]", "(x*CosIntegral[(a + b*ArcCos[-1 + d*x^2])/(2*b)]*Sin[a/(2*b)])/(Sqrt[2]*b*Sqrt[d*x^2]) - (x*Cos[a/(2*b)]*SinIntegral[(a + b*ArcCos[-1 + d*x^2])/(2*b)])/(Sqrt[2]*b*Sqrt[d*x^2])", //
				4818);
	}

	// {4827}
	public void test0067() {
		check("Integrate[(a + b*ArcCos[-1 + d*x^2])^(-2), x]", "Sqrt[2*d*x^2 - d^2*x^4]/(2*b*d*x*(a + b*ArcCos[-1 + d*x^2])) - (x*Cos[a/(2*b)]*CosIntegral[(a + b*ArcCos[-1 + d*x^2])/(2*b)])/(2*Sqrt[2]*b^2*Sqrt[d*x^2]) - (x*Sin[a/(2*b)]*SinIntegral[(a + b*ArcCos[-1 + d*x^2])/(2*b)])/(2*Sqrt[2]*b^2*Sqrt[d*x^2])", //
				4827);
	}

	// {4812}
	public void test0068() {
		check("Integrate[Sqrt[a + b*ArcCos[1 + d*x^2]], x]", "(2*Sqrt[Pi]*Cos[a/(2*b)]*FresnelS[(Sqrt[b^(-1)]*Sqrt[a + b*ArcCos[1 + d*x^2]])/Sqrt[Pi]]*Sin[ArcCos[1 + d*x^2]/2])/(Sqrt[b^(-1)]*d*x) - (2*Sqrt[Pi]*FresnelC[(Sqrt[b^(-1)]*Sqrt[a + b*ArcCos[1 + d*x^2]])/Sqrt[Pi]]*Sin[a/(2*b)]*Sin[ArcCos[1 + d*x^2]/2])/(Sqrt[b^(-1)]*d*x) - (2*Sqrt[a + b*ArcCos[1 + d*x^2]]*Sin[ArcCos[1 + d*x^2]/2]^2)/(d*x)", //
				4812);
	}

	// {4820}
	public void test0069() {
		check("Integrate[1/Sqrt[a + b*ArcCos[1 + d*x^2]], x]", "(-2*Sqrt[b^(-1)]*Sqrt[Pi]*Cos[a/(2*b)]*FresnelC[(Sqrt[b^(-1)]*Sqrt[a + b*ArcCos[1 + d*x^2]])/Sqrt[Pi]]*Sin[ArcCos[1 + d*x^2]/2])/(d*x) - (2*Sqrt[b^(-1)]*Sqrt[Pi]*FresnelS[(Sqrt[b^(-1)]*Sqrt[a + b*ArcCos[1 + d*x^2]])/Sqrt[Pi]]*Sin[a/(2*b)]*Sin[ArcCos[1 + d*x^2]/2])/(d*x)", //
				4820);
	}

	// {4823}
	public void test0070() {
		check("Integrate[(a + b*ArcCos[1 + d*x^2])^(-3/2), x]", "Sqrt[-2*d*x^2 - d^2*x^4]/(b*d*x*Sqrt[a + b*ArcCos[1 + d*x^2]]) + (2*(b^(-1))^(3/2)*Sqrt[Pi]*Cos[a/(2*b)]*FresnelS[(Sqrt[b^(-1)]*Sqrt[a + b*ArcCos[1 + d*x^2]])/Sqrt[Pi]]*Sin[ArcCos[1 + d*x^2]/2])/(d*x) - (2*(b^(-1))^(3/2)*Sqrt[Pi]*FresnelC[(Sqrt[b^(-1)]*Sqrt[a + b*ArcCos[1 + d*x^2]])/Sqrt[Pi]]*Sin[a/(2*b)]*Sin[ArcCos[1 + d*x^2]/2])/(d*x)", //
				4823);
	}

	// {4813}
	public void test0071() {
		check("Integrate[Sqrt[a + b*ArcCos[-1 + d*x^2]], x]", "(2*Sqrt[a + b*ArcCos[-1 + d*x^2]]*Cos[ArcCos[-1 + d*x^2]/2]^2)/(d*x) - (2*Sqrt[Pi]*Cos[a/(2*b)]*Cos[ArcCos[-1 + d*x^2]/2]*FresnelC[(Sqrt[b^(-1)]*Sqrt[a + b*ArcCos[-1 + d*x^2]])/Sqrt[Pi]])/(Sqrt[b^(-1)]*d*x) - (2*Sqrt[Pi]*Cos[ArcCos[-1 + d*x^2]/2]*FresnelS[(Sqrt[b^(-1)]*Sqrt[a + b*ArcCos[-1 + d*x^2]])/Sqrt[Pi]]*Sin[a/(2*b)])/(Sqrt[b^(-1)]*d*x)", //
				4813);
	}

	// {4821}
	public void test0072() {
		check("Integrate[1/Sqrt[a + b*ArcCos[-1 + d*x^2]], x]", "(-2*Sqrt[b^(-1)]*Sqrt[Pi]*Cos[a/(2*b)]*Cos[ArcCos[-1 + d*x^2]/2]*FresnelS[(Sqrt[b^(-1)]*Sqrt[a + b*ArcCos[-1 + d*x^2]])/Sqrt[Pi]])/(d*x) + (2*Sqrt[b^(-1)]*Sqrt[Pi]*Cos[ArcCos[-1 + d*x^2]/2]*FresnelC[(Sqrt[b^(-1)]*Sqrt[a + b*ArcCos[-1 + d*x^2]])/Sqrt[Pi]]*Sin[a/(2*b)])/(d*x)", //
				4821);
	}

	// {4824}
	public void test0073() {
		check("Integrate[(a + b*ArcCos[-1 + d*x^2])^(-3/2), x]", "Sqrt[2*d*x^2 - d^2*x^4]/(b*d*x*Sqrt[a + b*ArcCos[-1 + d*x^2]]) - (2*(b^(-1))^(3/2)*Sqrt[Pi]*Cos[a/(2*b)]*Cos[ArcCos[-1 + d*x^2]/2]*FresnelC[(Sqrt[b^(-1)]*Sqrt[a + b*ArcCos[-1 + d*x^2]])/Sqrt[Pi]])/(d*x) - (2*(b^(-1))^(3/2)*Sqrt[Pi]*Cos[ArcCos[-1 + d*x^2]/2]*FresnelS[(Sqrt[b^(-1)]*Sqrt[a + b*ArcCos[-1 + d*x^2]])/Sqrt[Pi]]*Sin[a/(2*b)])/(d*x)", //
				4824);
	}

	// {4858}
	public void test0074() {
		check("Integrate[(a + b*ArcTan[c*x])^2/(d + e*x), x]", "-(((a + b*ArcTan[c*x])^2*Log[2/(1 - I*c*x)])/e) + ((a + b*ArcTan[c*x])^2*Log[(2*c*(d + e*x))/((c*d + I*e)*(1 - I*c*x))])/e + (I*b*(a + b*ArcTan[c*x])*PolyLog[2, 1 - 2/(1 - I*c*x)])/e - (I*b*(a + b*ArcTan[c*x])*PolyLog[2, 1 - (2*c*(d + e*x))/((c*d + I*e)*(1 - I*c*x))])/e - (b^2*PolyLog[3, 1 - 2/(1 - I*c*x)])/(2*e) + (b^2*PolyLog[3, 1 - (2*c*(d + e*x))/((c*d + I*e)*(1 - I*c*x))])/(2*e)", //
				4858);
	}

	// {4860}
	public void test0075() {
		check("Integrate[(a + b*ArcTan[c*x])^3/(d + e*x), x]", "-(((a + b*ArcTan[c*x])^3*Log[2/(1 - I*c*x)])/e) + ((a + b*ArcTan[c*x])^3*Log[(2*c*(d + e*x))/((c*d + I*e)*(1 - I*c*x))])/e + (((3*I)/2)*b*(a + b*ArcTan[c*x])^2*PolyLog[2, 1 - 2/(1 - I*c*x)])/e - (((3*I)/2)*b*(a + b*ArcTan[c*x])^2*PolyLog[2, 1 - (2*c*(d + e*x))/((c*d + I*e)*(1 - I*c*x))])/e - (3*b^2*(a + b*ArcTan[c*x])*PolyLog[3, 1 - 2/(1 - I*c*x)])/(2*e) + (3*b^2*(a + b*ArcTan[c*x])*PolyLog[3, 1 - (2*c*(d + e*x))/((c*d + I*e)*(1 - I*c*x))])/(2*e) - (((3*I)/4)*b^3*PolyLog[4, 1 - 2/(1 - I*c*x)])/e + (((3*I)/4)*b^3*PolyLog[4, 1 - (2*c*(d + e*x))/((c*d + I*e)*(1 - I*c*x))])/e", //
				4860);
	}

	// {4858}
	public void test0076() {
		check("Integrate[(a + b*ArcTan[c*x])^2/(d + e*x), x]", "-(((a + b*ArcTan[c*x])^2*Log[2/(1 - I*c*x)])/e) + ((a + b*ArcTan[c*x])^2*Log[(2*c*(d + e*x))/((c*d + I*e)*(1 - I*c*x))])/e + (I*b*(a + b*ArcTan[c*x])*PolyLog[2, 1 - 2/(1 - I*c*x)])/e - (I*b*(a + b*ArcTan[c*x])*PolyLog[2, 1 - (2*c*(d + e*x))/((c*d + I*e)*(1 - I*c*x))])/e - (b^2*PolyLog[3, 1 - 2/(1 - I*c*x)])/(2*e) + (b^2*PolyLog[3, 1 - (2*c*(d + e*x))/((c*d + I*e)*(1 - I*c*x))])/(2*e)", //
				4858);
	}

	// {4884}
	public void test0077() {
		check("Integrate[ArcTan[a*x]/(c + a^2*c*x^2), x]", "ArcTan[a*x]^2/(2*a*c)", //
				4884);
	}

	// {4894}
	public void test0078() {
		check("Integrate[ArcTan[a*x]/(c + a^2*c*x^2)^(3/2), x]", "1/(a*c*Sqrt[c + a^2*c*x^2]) + (x*ArcTan[a*x])/(c*Sqrt[c + a^2*c*x^2])", //
				4894);
	}

	// {4884}
	public void test0079() {
		check("Integrate[ArcTan[a*x]^2/(c + a^2*c*x^2), x]", "ArcTan[a*x]^3/(3*a*c)", //
				4884);
	}

	// {4884}
	public void test0080() {
		check("Integrate[ArcTan[a*x]^3/(c + a^2*c*x^2), x]", "ArcTan[a*x]^4/(4*a*c)", //
				4884);
	}

	// {4882}
	public void test0081() {
		check("Integrate[1/((c + a^2*c*x^2)*ArcTan[a*x]), x]", "Log[ArcTan[a*x]]/(a*c)", //
				4882);
	}

	// {4884}
	public void test0085() {
		check("Integrate[1/((c + a^2*c*x^2)*ArcTan[a*x]^2), x]", "-(1/(a*c*ArcTan[a*x]))", //
				4884);
	}

	// {4884}
	public void test0095() {
		check("Integrate[1/((c + a^2*c*x^2)*ArcTan[a*x]^3), x]", "-1/(2*a*c*ArcTan[a*x]^2)", //
				4884);
	}

	// {4884}
	public void test0106() {
		check("Integrate[Sqrt[ArcTan[a*x]]/(c + a^2*c*x^2), x]", "(2*ArcTan[a*x]^(3/2))/(3*a*c)", //
				4884);
	}

	// {4884}
	public void test0120() {
		check("Integrate[ArcTan[a*x]^(3/2)/(c + a^2*c*x^2), x]", "(2*ArcTan[a*x]^(5/2))/(5*a*c)", //
				4884);
	}

	// {4884}
	public void test0130() {
		check("Integrate[ArcTan[a*x]^(5/2)/(c + a^2*c*x^2), x]", "(2*ArcTan[a*x]^(7/2))/(7*a*c)", //
				4884);
	}

	// {4884}
	public void test0136() {
		check("Integrate[1/((c + a^2*c*x^2)*Sqrt[ArcTan[a*x]]), x]", "(2*Sqrt[ArcTan[a*x]])/(a*c)", //
				4884);
	}

	// {4884}
	public void test0139() {
		check("Integrate[1/((c + a^2*c*x^2)*ArcTan[a*x]^(3/2)), x]", "-2/(a*c*Sqrt[ArcTan[a*x]])", //
				4884);
	}

	// {4884}
	public void test0158() {
		check("Integrate[1/((c + a^2*c*x^2)*ArcTan[a*x]^(5/2)), x]", "-2/(3*a*c*ArcTan[a*x]^(3/2))", //
				4884);
	}

	// {4884}
	public void test0162() {
		check("Integrate[ArcTan[a*x]^n/(c + a^2*c*x^2), x]", "ArcTan[a*x]^(1 + n)/(a*c*(1 + n))", //
				4884);
	}

	// {4882}
	public void test0169() {
		check("Integrate[1/((1 + x^2)*(2 + ArcTan[x])), x]", "Log[2 + ArcTan[x]]", //
				4882);
	}

	// {4882}
	public void test0170() {
		check("Integrate[1/((a + a*x^2)*(b - 2*b*ArcTan[x])), x]", "-Log[1 - 2*ArcTan[x]]/(2*a*b)", //
				4882);
	}

	// {5071}
	public void test0171() {
		check("Integrate[E^ArcTan[a*x]/(c + a^2*c*x^2), x]", "E^ArcTan[a*x]/(a*c)", //
				5071);
	}

	// {5069}
	public void test0172() {
		check("Integrate[E^ArcTan[a*x]/(c + a^2*c*x^2)^(3/2), x]", "(E^ArcTan[a*x]*(1 + a*x))/(2*a*c*Sqrt[c + a^2*c*x^2])", //
				5069);
	}

	// {5071}
	public void test0173() {
		check("Integrate[E^(2*ArcTan[a*x])/(c + a^2*c*x^2), x]", "E^(2*ArcTan[a*x])/(2*a*c)", //
				5071);
	}

	// {5069}
	public void test0174() {
		check("Integrate[E^(2*ArcTan[a*x])/(c + a^2*c*x^2)^(3/2), x]", "(E^(2*ArcTan[a*x])*(2 + a*x))/(5*a*c*Sqrt[c + a^2*c*x^2])", //
				5069);
	}

	// {5071}
	public void test0175() {
		check("Integrate[1/(E^ArcTan[a*x]*(c + a^2*c*x^2)), x]", "-(1/(a*c*E^ArcTan[a*x]))", //
				5071);
	}

	// {5069}
	public void test0176() {
		check("Integrate[1/(E^ArcTan[a*x]*(c + a^2*c*x^2)^(3/2)), x]", "-(1 - a*x)/(2*a*c*E^ArcTan[a*x]*Sqrt[c + a^2*c*x^2])", //
				5069);
	}

	// {5071}
	public void test0177() {
		check("Integrate[1/(E^(2*ArcTan[a*x])*(c + a^2*c*x^2)), x]", "-1/(2*a*c*E^(2*ArcTan[a*x]))", //
				5071);
	}

	// {5069}
	public void test0178() {
		check("Integrate[1/(E^(2*ArcTan[a*x])*(c + a^2*c*x^2)^(3/2)), x]", "-(2 - a*x)/(5*a*c*E^(2*ArcTan[a*x])*Sqrt[c + a^2*c*x^2])", //
				5069);
	}

	// {5071}
	public void test0179() {
		check("Integrate[E^(n*ArcTan[a*x])/(c + a^2*c*x^2), x]", "E^(n*ArcTan[a*x])/(a*c*n)", //
				5071);
	}

	// {5079}
	public void test0180() {
		check("Integrate[E^(I*n*ArcTan[a*x])*x^2*(c + a^2*c*x^2)^(-1 - n^2/2), x]", "(I*E^(I*n*ArcTan[a*x])*(1 - I*a*n*x))/(a^3*c*n*(1 - n^2)*(c + a^2*c*x^2)^(n^2/2))", //
				5079);
	}

	// {4885}
	public void test0181() {
		check("Integrate[ArcCot[x]/(1 + x^2), x]", "-ArcCot[x]^2/2", //
				4885);
	}

	// {4883}
	public void test0182() {
		check("Integrate[1/((1 + x^2)*ArcCot[x]), x]", "-Log[ArcCot[x]]", //
				4883);
	}

	// {4885}
	public void test0183() {
		check("Integrate[ArcCot[x]^n/(1 + x^2), x]", "-(ArcCot[x]^(1 + n)/(1 + n))", //
				4885);
	}

	// {4895}
	public void test0184() {
		check("Integrate[ArcCot[x]/(a + a*x^2)^(3/2), x]", "-(1/(a*Sqrt[a + a*x^2])) + (x*ArcCot[x])/(a*Sqrt[a + a*x^2])", //
				4895);
	}

	// {4883}
	public void test0191() {
		check("Integrate[1/((a + a*x^2)*(b - 2*b*ArcCot[x])), x]", "Log[1 - 2*ArcCot[x]]/(2*a*b)", //
				4883);
	}

	// {5113}
	public void test0192() {
		check("Integrate[E^ArcCot[x]/(a + a*x^2), x]", "-(E^ArcCot[x]/a)", //
				5113);
	}

	// {5114}
	public void test0193() {
		check("Integrate[E^ArcCot[x]/(a + a*x^2)^(3/2), x]", "-(E^ArcCot[x]*(1 - x))/(2*a*Sqrt[a + a*x^2])", //
				5114);
	}
}
