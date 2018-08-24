package org.matheclipse.core.rubi;

public class RuleTestInverseHyperbolicFunctions extends AbstractRubiTestCase {

	public RuleTestInverseHyperbolicFunctions(String name) {
		super(name, false);
	}

	// {5661}
	public void test0001() {
		check("Integrate[x^m*ArcSinh[a*x]^4, x]", 5661);
	}

	// {5661}
	public void test0002() {
		check("Integrate[x^m*ArcSinh[a*x]^3, x]", 5661);
	}

	// {5675}
	public void test0003() {
		check("Integrate[ArcSinh[a*x]/Sqrt[1 + a^2*x^2], x]", 5675);
	}

	// {5762}
	public void test0004() {
		check("Integrate[(x^m*ArcSinh[a*x])/Sqrt[1 + a^2*x^2], x]", 5762);
	}

	// {5675}
	public void test0005() {
		check("Integrate[ArcSinh[a*x]^2/Sqrt[1 + a^2*x^2], x]", 5675);
	}

	// {5675}
	public void test0006() {
		check("Integrate[ArcSinh[a*x]^3/Sqrt[1 + a^2*x^2], x]", 5675);
	}

	// {5673}
	public void test0007() {
		check("Integrate[1/(Sqrt[1 + a^2*x^2]*ArcSinh[a*x]), x]", 5673);
	}

	// {5673}
	public void test0008() {
		check("Integrate[1/(Sqrt[1 + c^2*x^2]*(a + b*ArcSinh[c*x])), x]", 5673);
	}

	// {5696}
	public void test0009() {
		check("Integrate[1/((c + a^2*c*x^2)*ArcSinh[a*x]^2), x]", 5696);
	}

	// {5696}
	public void test0010() {
		check("Integrate[1/((c + a^2*c*x^2)^2*ArcSinh[a*x]^2), x]", 5696);
	}

	// {5771}
	public void test0011() {
		check("Integrate[Sqrt[1 + c^2*x^2]/(x^2*(a + b*ArcSinh[c*x])^2), x]", 5771);
	}

	// {5777}
	public void test0012() {
		check("Integrate[(1 + c^2*x^2)^(3/2)/(x^2*(a + b*ArcSinh[c*x])^2), x]", 5777);
	}

	// {5771}
	public void test0013() {
		check("Integrate[(1 + c^2*x^2)^(3/2)/(x^4*(a + b*ArcSinh[c*x])^2), x]", 5771);
	}

	// {5777}
	public void test0014() {
		check("Integrate[(1 + c^2*x^2)^(5/2)/(x^2*(a + b*ArcSinh[c*x])^2), x]", 5777);
	}

	// {5774}
	public void test0015() {
		check("Integrate[x^m/(Sqrt[1 + c^2*x^2]*(a + b*ArcSinh[c*x])^2), x]", 5774);
	}

	// {5675}
	public void test0016() {
		check("Integrate[1/(Sqrt[1 + c^2*x^2]*(a + b*ArcSinh[c*x])^2), x]", 5675);
	}

	// {5774}
	public void test0017() {
		check("Integrate[1/(x*Sqrt[1 + c^2*x^2]*(a + b*ArcSinh[c*x])^2), x]", 5774);
	}

	// {5774}
	public void test0018() {
		check("Integrate[1/(x^2*Sqrt[1 + c^2*x^2]*(a + b*ArcSinh[c*x])^2), x]", 5774);
	}

	// {5771}
	public void test0019() {
		check("Integrate[x^2/((1 + c^2*x^2)^(3/2)*(a + b*ArcSinh[c*x])^2), x]", 5771);
	}

	// {5696}
	public void test0020() {
		check("Integrate[1/((1 + c^2*x^2)^(3/2)*(a + b*ArcSinh[c*x])^2), x]", 5696);
	}

	// {5696}
	public void test0021() {
		check("Integrate[1/((1 + c^2*x^2)^(5/2)*(a + b*ArcSinh[c*x])^2), x]", 5696);
	}

	// {5675}
	public void test0022() {
		check("Integrate[1/(Sqrt[1 + a^2*x^2]*ArcSinh[a*x]^3), x]", 5675);
	}

	// {5687}
	public void test0023() {
		check("Integrate[Sqrt[ArcSinh[a*x]]/(c + a^2*c*x^2)^(3/2), x]", 5687);
	}

	// {5687}
	public void test0024() {
		check("Integrate[ArcSinh[a*x]^(3/2)/(c + a^2*c*x^2)^(3/2), x]", 5687);
	}

	// {5687}
	public void test0025() {
		check("Integrate[ArcSinh[a*x]^(5/2)/(c + a^2*c*x^2)^(3/2), x]", 5687);
	}

	// {5687}
	public void test0026() {
		check("Integrate[Sqrt[ArcSinh[x/a]]/(a^2 + x^2)^(3/2), x]", 5687);
	}

	// {5687}
	public void test0027() {
		check("Integrate[ArcSinh[x/a]^(3/2)/(a^2 + x^2)^(3/2), x]", 5687);
	}

	// {5696}
	public void test0028() {
		check("Integrate[1/((c + a^2*c*x^2)^(3/2)*ArcSinh[a*x]^(3/2)), x]", 5696);
	}

	// {5696}
	public void test0029() {
		check("Integrate[1/((c + a^2*c*x^2)^(5/2)*ArcSinh[a*x]^(3/2)), x]", 5696);
	}

	// {5696}
	public void test0030() {
		check("Integrate[1/((c + a^2*c*x^2)^(3/2)*ArcSinh[a*x]^(5/2)), x]", 5696);
	}

	// {5696}
	public void test0031() {
		check("Integrate[1/((c + a^2*c*x^2)^(5/2)*ArcSinh[a*x]^(5/2)), x]", 5696);
	}

	// {5675}
	public void test0032() {
		check("Integrate[ArcSinh[a*x]^n/Sqrt[1 + a^2*x^2], x]", 5675);
	}

	// {5801}
	public void test0033() {
		check("Integrate[(d + e*x)^m*(a + b*ArcSinh[c*x])^2, x]", 5801);
	}

	// {5865}
	public void test0034() {
		check("Integrate[1/(x*ArcSinh[a + b*x]), x]", 5865);
	}

	// {5865}
	public void test0035() {
		check("Integrate[1/(x*ArcSinh[a + b*x]^2), x]", 5865);
	}

	// {5865}
	public void test0036() {
		check("Integrate[1/(x*ArcSinh[a + b*x]^3), x]", 5865);
	}

	// {5865}
	public void test0037() {
		check("Integrate[x^m*(a + b*ArcSinh[c + d*x])^n, x]", 5865);
	}

	// {5865}
	public void test0038() {
		check("Integrate[(a + b*ArcSinh[c + d*x])^n/x, x]", 5865);
	}

	// {5865}
	public void test0039() {
		check("Integrate[(c*e + d*e*x)^m/(a + b*ArcSinh[c + d*x]), x]", 5865);
	}

	// {5867}
	public void test0040() {
		check("Integrate[1/((1 + a^2 + 2*a*b*x + b^2*x^2)^(3/2)*ArcSinh[a + b*x]), x]", 5867);
	}

	// {4816}
	public void test0041() {
		check("Integrate[(a + I*b*ArcSin[1 - I*d*x^2])^(-1), x]", 4816);
	}

	// {4825}
	public void test0042() {
		check("Integrate[(a + I*b*ArcSin[1 - I*d*x^2])^(-2), x]", 4825);
	}

	// {4816}
	public void test0043() {
		check("Integrate[(a - I*b*ArcSin[1 + I*d*x^2])^(-1), x]", 4816);
	}

	// {4825}
	public void test0044() {
		check("Integrate[(a - I*b*ArcSin[1 + I*d*x^2])^(-2), x]", 4825);
	}

	// {4811}
	public void test0045() {
		check("Integrate[Sqrt[a + I*b*ArcSin[1 - I*d*x^2]], x]", 4811);
	}

	// {4819}
	public void test0046() {
		check("Integrate[1/Sqrt[a + I*b*ArcSin[1 - I*d*x^2]], x]", 4819);
	}

	// {4822}
	public void test0047() {
		check("Integrate[(a + I*b*ArcSin[1 - I*d*x^2])^(-3/2), x]", 4822);
	}

	// {4811}
	public void test0048() {
		check("Integrate[Sqrt[a - I*b*ArcSin[1 + I*d*x^2]], x]", 4811);
	}

	// {4819}
	public void test0049() {
		check("Integrate[1/Sqrt[a - I*b*ArcSin[1 + I*d*x^2]], x]", 4819);
	}

	// {4822}
	public void test0050() {
		check("Integrate[(a - I*b*ArcSin[1 + I*d*x^2])^(-3/2), x]", 4822);
	}

	// {5662}
	public void test0051() {
		check("Integrate[x^m*ArcCosh[a*x]^4, x]", 5662);
	}

	// {5662}
	public void test0052() {
		check("Integrate[x^m*ArcCosh[a*x]^3, x]", 5662);
	}

	// {5798}
	public void test0053() {
		check("Integrate[(f*x)^m*(d - c^2*d*x^2)^(5/2)*(a + b*ArcCosh[c*x])^2, x]", 5798);
	}

	// {5798}
	public void test0054() {
		check("Integrate[(f*x)^m*(d - c^2*d*x^2)^(3/2)*(a + b*ArcCosh[c*x])^2, x]", 5798);
	}

	// {5798}
	public void test0055() {
		check("Integrate[(f*x)^m*Sqrt[d - c^2*d*x^2]*(a + b*ArcCosh[c*x])^2, x]", 5798);
	}

	// {5798}
	public void test0056() {
		check("Integrate[((f*x)^m*(a + b*ArcCosh[c*x])^2)/Sqrt[d - c^2*d*x^2], x]", 5798);
	}

	// {5798}
	public void test0057() {
		check("Integrate[((f*x)^m*(a + b*ArcCosh[c*x])^2)/(d - c^2*d*x^2)^(3/2), x]", 5798);
	}

	// {5798}
	public void test0058() {
		check("Integrate[((f*x)^m*(a + b*ArcCosh[c*x])^2)/(d - c^2*d*x^2)^(5/2), x]", 5798);
	}

	// {5798}
	public void test0059() {
		check("Integrate[((f*x)^m*ArcCosh[c*x]^2)/Sqrt[1 - c^2*x^2], x]", 5798);
	}

	// {5798}
	public void test0060() {
		check("Integrate[((f*x)^m*(a + b*ArcCosh[c*x])^3)/Sqrt[1 - c^2*x^2], x]", 5798);
	}

	// {5798}
	public void test0061() {
		check("Integrate[Sqrt[1 - c^2*x^2]/(x^3*(a + b*ArcCosh[c*x])), x]", 5798);
	}

	// {5798}
	public void test0062() {
		check("Integrate[Sqrt[1 - c^2*x^2]/(x^4*(a + b*ArcCosh[c*x])), x]", 5798);
	}

	// {5798}
	public void test0063() {
		check("Integrate[(1 - c^2*x^2)^(3/2)/(x^3*(a + b*ArcCosh[c*x])), x]", 5798);
	}

	// {5798}
	public void test0064() {
		check("Integrate[(1 - c^2*x^2)^(3/2)/(x^4*(a + b*ArcCosh[c*x])), x]", 5798);
	}

	// {5798}
	public void test0065() {
		check("Integrate[(1 - c^2*x^2)^(5/2)/(x^3*(a + b*ArcCosh[c*x])), x]", 5798);
	}

	// {5798}
	public void test0066() {
		check("Integrate[(1 - c^2*x^2)^(5/2)/(x^4*(a + b*ArcCosh[c*x])), x]", 5798);
	}

	// {5798}
	public void test0067() {
		check("Integrate[1/(x*Sqrt[1 - a^2*x^2]*ArcCosh[a*x]), x]", 5798);
	}

	// {5798}
	public void test0068() {
		check("Integrate[1/(x^2*Sqrt[1 - a^2*x^2]*ArcCosh[a*x]), x]", 5798);
	}

	// {5798}
	public void test0069() {
		check("Integrate[1/(x*Sqrt[1 - c^2*x^2]*(a + b*ArcCosh[c*x])), x]", 5798);
	}

	// {5798}
	public void test0070() {
		check("Integrate[1/(x^2*Sqrt[1 - c^2*x^2]*(a + b*ArcCosh[c*x])), x]", 5798);
	}

	// {5798}
	public void test0071() {
		check("Integrate[x^2/((1 - c^2*x^2)^(3/2)*(a + b*ArcCosh[c*x])), x]", 5798);
	}

	// {5798}
	public void test0072() {
		check("Integrate[x/((1 - c^2*x^2)^(3/2)*(a + b*ArcCosh[c*x])), x]", 5798);
	}

	// {5713}
	public void test0073() {
		check("Integrate[1/((1 - c^2*x^2)^(3/2)*(a + b*ArcCosh[c*x])), x]", 5713);
	}

	// {5798}
	public void test0074() {
		check("Integrate[1/(x*(1 - c^2*x^2)^(3/2)*(a + b*ArcCosh[c*x])), x]", 5798);
	}

	// {5798}
	public void test0075() {
		check("Integrate[1/(x^2*(1 - c^2*x^2)^(3/2)*(a + b*ArcCosh[c*x])), x]", 5798);
	}

	// {5798}
	public void test0076() {
		check("Integrate[x^2/((1 - c^2*x^2)^(5/2)*(a + b*ArcCosh[c*x])), x]", 5798);
	}

	// {5798}
	public void test0077() {
		check("Integrate[x/((1 - c^2*x^2)^(5/2)*(a + b*ArcCosh[c*x])), x]", 5798);
	}

	// {5713}
	public void test0078() {
		check("Integrate[1/((1 - c^2*x^2)^(5/2)*(a + b*ArcCosh[c*x])), x]", 5713);
	}

	// {5798}
	public void test0079() {
		check("Integrate[1/(x*(1 - c^2*x^2)^(5/2)*(a + b*ArcCosh[c*x])), x]", 5798);
	}

	// {5798}
	public void test0080() {
		check("Integrate[1/(x^2*(1 - c^2*x^2)^(5/2)*(a + b*ArcCosh[c*x])), x]", 5798);
	}

	// {5798}
	public void test0081() {
		check("Integrate[(x^m*(1 - c^2*x^2)^(5/2))/(a + b*ArcCosh[c*x]), x]", 5798);
	}

	// {5798}
	public void test0082() {
		check("Integrate[(x^m*(1 - c^2*x^2)^(3/2))/(a + b*ArcCosh[c*x]), x]", 5798);
	}

	// {5798}
	public void test0083() {
		check("Integrate[(x^m*Sqrt[1 - c^2*x^2])/(a + b*ArcCosh[c*x]), x]", 5798);
	}

	// {5798}
	public void test0084() {
		check("Integrate[x^m/(Sqrt[1 - a^2*x^2]*ArcCosh[a*x]), x]", 5798);
	}

	// {5798}
	public void test0085() {
		check("Integrate[x^m/(Sqrt[1 - c^2*x^2]*(a + b*ArcCosh[c*x])), x]", 5798);
	}

	// {5798}
	public void test0086() {
		check("Integrate[x^m/((1 - c^2*x^2)^(3/2)*(a + b*ArcCosh[c*x])), x]", 5798);
	}

	// {5798}
	public void test0087() {
		check("Integrate[x^m/((1 - c^2*x^2)^(5/2)*(a + b*ArcCosh[c*x])), x]", 5798);
	}

	// {5695}
	public void test0088() {
		check("Integrate[1/((c - a^2*c*x^2)*ArcCosh[a*x]^2), x]", 5695);
	}

	// {5695}
	public void test0089() {
		check("Integrate[1/((c - a^2*c*x^2)^2*ArcCosh[a*x]^2), x]", 5695);
	}

	// {5798}
	public void test0090() {
		check("Integrate[Sqrt[1 - c^2*x^2]/(x^3*(a + b*ArcCosh[c*x])^2), x]", 5798);
	}

	// {5798}
	public void test0091() {
		check("Integrate[Sqrt[1 - c^2*x^2]/(x^4*(a + b*ArcCosh[c*x])^2), x]", 5798);
	}

	// {5798}
	public void test0092() {
		check("Integrate[(1 - c^2*x^2)^(3/2)/(x^3*(a + b*ArcCosh[c*x])^2), x]", 5798);
	}

	// {5798}
	public void test0093() {
		check("Integrate[(1 - c^2*x^2)^(3/2)/(x^5*(a + b*ArcCosh[c*x])^2), x]", 5798);
	}

	// {5798}
	public void test0094() {
		check("Integrate[(1 - c^2*x^2)^(5/2)/(x^3*(a + b*ArcCosh[c*x])^2), x]", 5798);
	}

	// {5798}
	public void test0095() {
		check("Integrate[(1 - c^2*x^2)^(5/2)/(x^4*(a + b*ArcCosh[c*x])^2), x]", 5798);
	}

	// {5798}
	public void test0096() {
		check("Integrate[x^3/((1 - c^2*x^2)^(3/2)*(a + b*ArcCosh[c*x])^2), x]", 5798);
	}

	// {5798}
	public void test0097() {
		check("Integrate[x/((1 - c^2*x^2)^(3/2)*(a + b*ArcCosh[c*x])^2), x]", 5798);
	}

	// {5798}
	public void test0098() {
		check("Integrate[1/(x*(1 - c^2*x^2)^(3/2)*(a + b*ArcCosh[c*x])^2), x]", 5798);
	}

	// {5798}
	public void test0099() {
		check("Integrate[1/(x^2*(1 - c^2*x^2)^(3/2)*(a + b*ArcCosh[c*x])^2), x]", 5798);
	}

	// {5798}
	public void test0100() {
		check("Integrate[x^3/((1 - c^2*x^2)^(5/2)*(a + b*ArcCosh[c*x])^2), x]", 5798);
	}

	// {5798}
	public void test0101() {
		check("Integrate[x^2/((1 - c^2*x^2)^(5/2)*(a + b*ArcCosh[c*x])^2), x]", 5798);
	}

	// {5798}
	public void test0102() {
		check("Integrate[x/((1 - c^2*x^2)^(5/2)*(a + b*ArcCosh[c*x])^2), x]", 5798);
	}

	// {5798}
	public void test0103() {
		check("Integrate[1/(x*(1 - c^2*x^2)^(5/2)*(a + b*ArcCosh[c*x])^2), x]", 5798);
	}

	// {5798}
	public void test0104() {
		check("Integrate[1/(x^2*(1 - c^2*x^2)^(5/2)*(a + b*ArcCosh[c*x])^2), x]", 5798);
	}

	// {5798}
	public void test0105() {
		check("Integrate[(x^m*(1 - c^2*x^2)^(5/2))/(a + b*ArcCosh[c*x])^2, x]", 5798);
	}

	// {5798}
	public void test0106() {
		check("Integrate[(x^m*(1 - c^2*x^2)^(3/2))/(a + b*ArcCosh[c*x])^2, x]", 5798);
	}

	// {5798}
	public void test0107() {
		check("Integrate[(x^m*Sqrt[1 - c^2*x^2])/(a + b*ArcCosh[c*x])^2, x]", 5798);
	}

	// {5798}
	public void test0108() {
		check("Integrate[x^m/((1 - c^2*x^2)^(3/2)*(a + b*ArcCosh[c*x])^2), x]", 5798);
	}

	// {5798}
	public void test0109() {
		check("Integrate[x^m/((1 - c^2*x^2)^(5/2)*(a + b*ArcCosh[c*x])^2), x]", 5798);
	}

	// {5798}
	public void test0110() {
		check("Integrate[(x^m*ArcCosh[a*x]^n)/Sqrt[1 - a^2*x^2], x]", 5798);
	}

	// {5798}
	public void test0111() {
		check("Integrate[ArcCosh[a*x]^n/(x*Sqrt[1 - a^2*x^2]), x]", 5798);
	}

	// {5798}
	public void test0112() {
		check("Integrate[ArcCosh[a*x]^n/(x^2*Sqrt[1 - a^2*x^2]), x]", 5798);
	}

	// {5713}
	public void test0113() {
		check("Integrate[1/((c - a^2*c*x^2)^(3/2)*Sqrt[ArcCosh[a*x]]), x]", 5713);
	}

	// {5713}
	public void test0114() {
		check("Integrate[1/((c - a^2*c*x^2)^(5/2)*Sqrt[ArcCosh[a*x]]), x]", 5713);
	}

	// {5802}
	public void test0115() {
		check("Integrate[(d + e*x)^m*(a + b*ArcCosh[c*x])^3, x]", 5802);
	}

	// {5802}
	public void test0116() {
		check("Integrate[(d + e*x)^m*(a + b*ArcCosh[c*x])^2, x]", 5802);
	}

	// {5841}
	public void test0117() {
		check("Integrate[((a + b*ArcCosh[c*x])^n*Log[h*(f + g*x)^m])/Sqrt[1 - c^2*x^2], x]", 5841);
	}

	// {5841}
	public void test0118() {
		check("Integrate[Log[h*(f + g*x)^m]/(Sqrt[1 - c^2*x^2]*(a + b*ArcCosh[c*x])), x]", 5841);
	}

	// {5866}
	public void test0119() {
		check("Integrate[(c*e + d*e*x)^m/(a + b*ArcCosh[c + d*x]), x]", 5866);
	}

	// {5881}
	public void test0120() {
		check("Integrate[(a + b*ArcCosh[1 + d*x^2])^(-1), x]", 5881);
	}

	// {5887}
	public void test0121() {
		check("Integrate[(a + b*ArcCosh[1 + d*x^2])^(-2), x]", 5887);
	}

	// {5882}
	public void test0122() {
		check("Integrate[(a + b*ArcCosh[-1 + d*x^2])^(-1), x]", 5882);
	}

	// {5888}
	public void test0123() {
		check("Integrate[(a + b*ArcCosh[-1 + d*x^2])^(-2), x]", 5888);
	}

	// {5878}
	public void test0124() {
		check("Integrate[Sqrt[a + b*ArcCosh[1 + d*x^2]], x]", 5878);
	}

	// {5883}
	public void test0125() {
		check("Integrate[1/Sqrt[a + b*ArcCosh[1 + d*x^2]], x]", 5883);
	}

	// {5885}
	public void test0126() {
		check("Integrate[(a + b*ArcCosh[1 + d*x^2])^(-3/2), x]", 5885);
	}

	// {5879}
	public void test0127() {
		check("Integrate[Sqrt[a + b*ArcCosh[-1 + d*x^2]], x]", 5879);
	}

	// {5884}
	public void test0128() {
		check("Integrate[1/Sqrt[a + b*ArcCosh[-1 + d*x^2]], x]", 5884);
	}

	// {5886}
	public void test0129() {
		check("Integrate[(a + b*ArcCosh[-1 + d*x^2])^(-3/2), x]", 5886);
	}

	// {5912}
	public void test0130() {
		check("Integrate[(a + b*ArcTanh[c*x])/x, x]", 5912);
	}

	// {5922}
	public void test0131() {
		check("Integrate[(a + b*ArcTanh[c*x])^2/(d + e*x), x]", 5922);
	}

	// {5924}
	public void test0132() {
		check("Integrate[(a + b*ArcTanh[c*x])^3/(d + e*x), x]", 5924);
	}

	// {5922}
	public void test0133() {
		check("Integrate[(a + b*ArcTanh[c*x])^2/(d + e*x), x]", 5922);
	}

	// {5948}
	public void test0134() {
		check("Integrate[ArcTanh[a*x]/(1 - a^2*x^2), x]", 5948);
	}

	// {5948}
	public void test0135() {
		check("Integrate[ArcTanh[a*x]^2/(1 - a^2*x^2), x]", 5948);
	}

	// {5948}
	public void test0136() {
		check("Integrate[ArcTanh[a*x]^3/(1 - a^2*x^2), x]", 5948);
	}

	// {5948}
	public void test0137() {
		check("Integrate[Sqrt[ArcTanh[a*x]]/(1 - a^2*x^2), x]", 5948);
	}

	// {5946}
	public void test0138() {
		check("Integrate[1/((1 - a^2*x^2)*ArcTanh[a*x]), x]", 5946);
	}

	// {5986}
	public void test0139() {
		check("Integrate[x/((1 - a^2*x^2)*ArcTanh[a*x]^2), x]", 5986);
	}

	// {5948}
	public void test0140() {
		check("Integrate[1/((1 - a^2*x^2)*ArcTanh[a*x]^2), x]", 5948);
	}

	// {5990}
	public void test0141() {
		check("Integrate[1/(x*(1 - a^2*x^2)*ArcTanh[a*x]^2), x]", 5990);
	}

	// {5986}
	public void test0142() {
		check("Integrate[x/((1 - a^2*x^2)*ArcTanh[a*x]^3), x]", 5986);
	}

	// {5948}
	public void test0143() {
		check("Integrate[1/((1 - a^2*x^2)*ArcTanh[a*x]^3), x]", 5948);
	}

	// {5990}
	public void test0144() {
		check("Integrate[1/(x*(1 - a^2*x^2)*ArcTanh[a*x]^3), x]", 5990);
	}

	// {5948}
	public void test0145() {
		check("Integrate[ArcTanh[a*x]^p/(1 - a^2*x^2), x]", 5948);
	}

	// {5950}
	public void test0146() {
		check("Integrate[ArcTanh[a*x]/Sqrt[1 - a^2*x^2], x]", 5950);
	}

	// {6018}
	public void test0147() {
		check("Integrate[ArcTanh[a*x]/(x*Sqrt[1 - a^2*x^2]), x]", 6018);
	}

	// {5958}
	public void test0148() {
		check("Integrate[ArcTanh[a*x]/(1 - a^2*x^2)^(3/2), x]", 5958);
	}

	// {5958}
	public void test0149() {
		check("Integrate[ArcTanh[a*x]/(c - a^2*c*x^2)^(3/2), x]", 5958);
	}

	// {5946}
	public void test0150() {
		check("Integrate[1/((a - a*x^2)*(b - 2*b*ArcTanh[x])), x]", 5946);
	}

	// {5958}
	public void test0151() {
		check("Integrate[ArcTanh[x]/(a - a*x^2)^(3/2), x]", 5958);
	}

	// {6111}
	public void test0152() {
		check("Integrate[(e + f*x)^m*(a + b*ArcTanh[c + d*x])^3, x]", 6111);
	}

	// {6111}
	public void test0153() {
		check("Integrate[(e + f*x)^m*(a + b*ArcTanh[c + d*x])^2, x]", 6111);
	}

	// {2167}
	public void test0154() {
		check("Integrate[ArcTanh[Tanh[a + b*x]]^2/x^4, x]", 2167);
	}

	// {2167}
	public void test0155() {
		check("Integrate[ArcTanh[Tanh[a + b*x]]^3/x^5, x]", 2167);
	}

	// {2167}
	public void test0156() {
		check("Integrate[ArcTanh[Tanh[a + b*x]]^4/x^6, x]", 2167);
	}

	// {2164}
	public void test0157() {
		check("Integrate[x^m/ArcTanh[Tanh[a + b*x]], x]", 2164);
	}

	// {2161}
	public void test0158() {
		check("Integrate[1/(x*Sqrt[ArcTanh[Tanh[a + b*x]]]), x]", 2161);
	}

	// {2162}
	public void test0159() {
		check("Integrate[1/(Sqrt[x]*ArcTanh[Tanh[a + b*x]]), x]", 2162);
	}

	// {2167}
	public void test0160() {
		check("Integrate[Sqrt[ArcTanh[Tanh[a + b*x]]]/x^(5/2), x]", 2167);
	}

	// {2167}
	public void test0161() {
		check("Integrate[ArcTanh[Tanh[a + b*x]]^(3/2)/x^(7/2), x]", 2167);
	}

	// {2167}
	public void test0162() {
		check("Integrate[ArcTanh[Tanh[a + b*x]]^(5/2)/x^(9/2), x]", 2167);
	}

	// {2165}
	public void test0163() {
		check("Integrate[1/(Sqrt[x]*Sqrt[ArcTanh[Tanh[a + b*x]]]), x]", 2165);
	}

	// {2167}
	public void test0164() {
		check("Integrate[1/(x^(3/2)*Sqrt[ArcTanh[Tanh[a + b*x]]]), x]", 2167);
	}

	// {2167}
	public void test0165() {
		check("Integrate[1/(Sqrt[x]*ArcTanh[Tanh[a + b*x]]^(3/2)), x]", 2167);
	}

	// {2167}
	public void test0166() {
		check("Integrate[Sqrt[x]/ArcTanh[Tanh[a + b*x]]^(5/2), x]", 2167);
	}

	// {2173}
	public void test0167() {
		check("Integrate[x^m*ArcTanh[Tanh[a + b*x]]^n, x]", 2173);
	}

	// {2164}
	public void test0168() {
		check("Integrate[ArcTanh[Tanh[a + b*x]]^n/x, x]", 2164);
	}

	// {6137}
	public void test0169() {
		check("Integrate[E^ArcTanh[a*x]/(c - a^2*c*x^2), x]", 6137);
	}

	// {6137}
	public void test0170() {
		check("Integrate[E^(3*ArcTanh[a*x])/(c - a^2*c*x^2), x]", 6137);
	}

	// {6137}
	public void test0171() {
		check("Integrate[1/(E^ArcTanh[a*x]*(c - a^2*c*x^2)), x]", 6137);
	}

	// {6137}
	public void test0172() {
		check("Integrate[1/(E^(3*ArcTanh[a*x])*(c - a^2*c*x^2)), x]", 6137);
	}

	// {6135}
	public void test0173() {
		check("Integrate[E^(ArcTanh[a*x]/2)/(1 - a^2*x^2)^(3/2), x]", 6135);
	}

	// {6135}
	public void test0174() {
		check("Integrate[E^(ArcTanh[a*x]/2)/(c - a^2*c*x^2)^(3/2), x]", 6135);
	}

	// {6146}
	public void test0175() {
		check("Integrate[(E^(ArcTanh[a*x]/2)*x^2)/(c - a^2*c*x^2)^(9/8), x]", 6146);
	}

	// {6137}
	public void test0176() {
		check("Integrate[E^(n*ArcTanh[a*x])/(c - a^2*c*x^2), x]", 6137);
	}

	// {6144}
	public void test0177() {
		check("Integrate[(E^(n*ArcTanh[a*x])*x)/(c - a^2*c*x^2)^(3/2), x]", 6144);
	}

	// {6135}
	public void test0178() {
		check("Integrate[E^(n*ArcTanh[a*x])/(c - a^2*c*x^2)^(3/2), x]", 6135);
	}

	// {6146}
	public void test0179() {
		check("Integrate[E^(n*ArcTanh[a*x])*x^2*(c - a^2*c*x^2)^(-1 - n^2/2), x]", 6146);
	}

	// {5913}
	public void test0180() {
		check("Integrate[ArcCoth[a*x]/x, x]", 5913);
	}

	// {5923}
	public void test0181() {
		check("Integrate[ArcCoth[c*x]^2/(d + e*x), x]", 5923);
	}

	// {5959}
	public void test0182() {
		check("Integrate[ArcCoth[x]/(a - a*x^2)^(3/2), x]", 5959);
	}

	// {5947}
	public void test0183() {
		check("Integrate[1/((1 - x^2)*ArcCoth[x]), x]", 5947);
	}

	// {5949}
	public void test0184() {
		check("Integrate[ArcCoth[x]^n/(1 - x^2), x]", 5949);
	}

	// {5949}
	public void test0185() {
		check("Integrate[ArcCoth[x]/(1 - x^2), x]", 5949);
	}

	// {6112}
	public void test0186() {
		check("Integrate[(e + f*x)^m*(a + b*ArcCoth[c + d*x])^2, x]", 6112);
	}

	// {6112}
	public void test0187() {
		check("Integrate[(e + f*x)^m*(a + b*ArcCoth[c + d*x])^3, x]", 6112);
	}

	// {2167}
	public void test0188() {
		check("Integrate[ArcCoth[Tanh[a + b*x]]^2/x^4, x]", 2167);
	}

	// {2167}
	public void test0189() {
		check("Integrate[ArcCoth[Tanh[a + b*x]]^3/x^5, x]", 2167);
	}

	// {2164}
	public void test0190() {
		check("Integrate[x^m/ArcCoth[Tanh[a + b*x]], x]", 2164);
	}

	// {2173}
	public void test0191() {
		check("Integrate[x^m*ArcCoth[Tanh[a + b*x]]^n, x]", 2173);
	}

	// {2164}
	public void test0192() {
		check("Integrate[ArcCoth[Tanh[a + b*x]]^n/x, x]", 2164);
	}

	// {5947}
	public void test0193() {
		check("Integrate[1/((a - a*x^2)*(b - 2*b*ArcCoth[x])), x]", 5947);
	}

	// {6174}
	public void test0194() {
		check("Integrate[E^ArcCoth[a*x]*Sqrt[c - a*c*x], x]", 6174);
	}

	// {6174}
	public void test0195() {
		check("Integrate[E^(3*ArcCoth[a*x])*(c - a*c*x)^(3/2), x]", 6174);
	}

	// {6174}
	public void test0196() {
		check("Integrate[1/(E^ArcCoth[a*x]*Sqrt[c - a*c*x]), x]", 6174);
	}

	// {6174}
	public void test0197() {
		check("Integrate[1/(E^(3*ArcCoth[a*x])*(c - a*c*x)^(3/2)), x]", 6174);
	}

	// {6174}
	public void test0198() {
		check("Integrate[E^ArcCoth[a*x]*Sqrt[c - a*c*x], x]", 6174);
	}

	// {6174}
	public void test0199() {
		check("Integrate[E^ArcCoth[x]*Sqrt[1 - x], x]", 6174);
	}

	// {6174}
	public void test0200() {
		check("Integrate[E^(n*ArcCoth[a*x])*(c - a*c*x)^(n/2), x]", 6174);
	}

	// {6183}
	public void test0201() {
		check("Integrate[E^ArcCoth[a*x]/(c - a^2*c*x^2), x]", 6183);
	}

	// {6183}
	public void test0202() {
		check("Integrate[E^(3*ArcCoth[a*x])/(c - a^2*c*x^2), x]", 6183);
	}

	// {6183}
	public void test0203() {
		check("Integrate[1/(E^ArcCoth[a*x]*(c - a^2*c*x^2)), x]", 6183);
	}

	// {6183}
	public void test0204() {
		check("Integrate[1/(E^(3*ArcCoth[a*x])*(c - a^2*c*x^2)), x]", 6183);
	}

	// {6183}
	public void test0205() {
		check("Integrate[E^(n*ArcCoth[a*x])/(c - a^2*c*x^2), x]", 6183);
	}

	// {6184}
	public void test0206() {
		check("Integrate[E^(n*ArcCoth[a*x])/(c - a^2*c*x^2)^(3/2), x]", 6184);
	}

	// {6186}
	public void test0207() {
		check("Integrate[(E^(n*ArcCoth[a*x])*x)/(c - a^2*c*x^2)^(3/2), x]", 6186);
	}

	// {6184}
	public void test0208() {
		check("Integrate[E^(n*ArcCoth[a*x])/(c - a^2*c*x^2)^(3/2), x]", 6184);
	}

	// {6288}
	public void test0209() {
		check("Integrate[(d + e*x)^m*(a + b*ArcSech[c*x]), x]", 6288);
	}
}
