package org.matheclipse.core.rubi;

public class RuleTestInverseTrigFunctions extends AbstractRubiTestCase {

	public RuleTestInverseTrigFunctions(String name) {
		super(name, false);
	}

	// {4627}
	public void test0001() {
		check("Integrate[(b*x)^m*ArcSin[a*x]^4, x]", 4627);
	}

	// {4627}
	public void test0002() {
		check("Integrate[(b*x)^m*ArcSin[a*x]^3, x]", 4627);
	}

	// {4627}
	public void test0003() {
		check("Integrate[(d*x)^(3/2)*(a + b*ArcSin[c*x])^3, x]", 4627);
	}

	// {4627}
	public void test0004() {
		check("Integrate[Sqrt[d*x]*(a + b*ArcSin[c*x])^3, x]", 4627);
	}

	// {4627}
	public void test0005() {
		check("Integrate[(a + b*ArcSin[c*x])^3/Sqrt[d*x], x]", 4627);
	}

	// {4627}
	public void test0006() {
		check("Integrate[(a + b*ArcSin[c*x])^3/(d*x)^(3/2), x]", 4627);
	}

	// {4627}
	public void test0007() {
		check("Integrate[(a + b*ArcSin[c*x])^3/(d*x)^(5/2), x]", 4627);
	}

	// {4641}
	public void test0008() {
		check("Integrate[ArcSin[a*x]/Sqrt[1 - a^2*x^2], x]", 4641);
	}

	// {4711}
	public void test0009() {
		check("Integrate[((f*x)^(3/2)*(a + b*ArcSin[c*x]))/Sqrt[1 - c^2*x^2], x]", 4711);
	}

	// {4711}
	public void test0010() {
		check("Integrate[(x^m*ArcSin[a*x])/Sqrt[1 - a^2*x^2], x]", 4711);
	}

	// {4641}
	public void test0011() {
		check("Integrate[ArcSin[a*x]^2/Sqrt[1 - a^2*x^2], x]", 4641);
	}

	// {4641}
	public void test0012() {
		check("Integrate[ArcSin[a*x]^3/Sqrt[1 - a^2*x^2], x]", 4641);
	}

	// {4639}
	public void test0013() {
		check("Integrate[1/(Sqrt[1 - a^2*x^2]*ArcSin[a*x]), x]", 4639);
	}

	// {4639}
	public void test0014() {
		check("Integrate[1/(Sqrt[1 - c^2*x^2]*(a + b*ArcSin[c*x])), x]", 4639);
	}

	// {4659}
	public void test0015() {
		check("Integrate[1/((c - a^2*c*x^2)*ArcSin[a*x]^2), x]", 4659);
	}

	// {4659}
	public void test0016() {
		check("Integrate[1/((c - a^2*c*x^2)^2*ArcSin[a*x]^2), x]", 4659);
	}

	// {4717}
	public void test0017() {
		check("Integrate[Sqrt[1 - c^2*x^2]/(x^2*(a + b*ArcSin[c*x])^2), x]", 4717);
	}

	// {4721}
	public void test0018() {
		check("Integrate[(1 - c^2*x^2)^(3/2)/(x^2*(a + b*ArcSin[c*x])^2), x]", 4721);
	}

	// {4717}
	public void test0019() {
		check("Integrate[(1 - c^2*x^2)^(3/2)/(x^4*(a + b*ArcSin[c*x])^2), x]", 4717);
	}

	// {4721}
	public void test0020() {
		check("Integrate[(1 - c^2*x^2)^(5/2)/(x^2*(a + b*ArcSin[c*x])^2), x]", 4721);
	}

	// {4719}
	public void test0021() {
		check("Integrate[x^m/(Sqrt[1 - c^2*x^2]*(a + b*ArcSin[c*x])^2), x]", 4719);
	}

	// {4641}
	public void test0022() {
		check("Integrate[1/(Sqrt[1 - c^2*x^2]*(a + b*ArcSin[c*x])^2), x]", 4641);
	}

	// {4719}
	public void test0023() {
		check("Integrate[1/(x*Sqrt[1 - c^2*x^2]*(a + b*ArcSin[c*x])^2), x]", 4719);
	}

	// {4719}
	public void test0024() {
		check("Integrate[1/(x^2*Sqrt[1 - c^2*x^2]*(a + b*ArcSin[c*x])^2), x]", 4719);
	}

	// {4717}
	public void test0025() {
		check("Integrate[x^2/((1 - c^2*x^2)^(3/2)*(a + b*ArcSin[c*x])^2), x]", 4717);
	}

	// {4659}
	public void test0026() {
		check("Integrate[1/((1 - c^2*x^2)^(3/2)*(a + b*ArcSin[c*x])^2), x]", 4659);
	}

	// {4659}
	public void test0027() {
		check("Integrate[1/((1 - c^2*x^2)^(5/2)*(a + b*ArcSin[c*x])^2), x]", 4659);
	}

	// {4641}
	public void test0028() {
		check("Integrate[1/(Sqrt[1 - a^2*x^2]*ArcSin[a*x]^3), x]", 4641);
	}

	// {4653}
	public void test0029() {
		check("Integrate[Sqrt[ArcSin[a*x]]/(c - a^2*c*x^2)^(3/2), x]", 4653);
	}

	// {4653}
	public void test0030() {
		check("Integrate[ArcSin[a*x]^(3/2)/(c - a^2*c*x^2)^(3/2), x]", 4653);
	}

	// {4653}
	public void test0031() {
		check("Integrate[ArcSin[a*x]^(5/2)/(c - a^2*c*x^2)^(3/2), x]", 4653);
	}

	// {4653}
	public void test0032() {
		check("Integrate[Sqrt[ArcSin[x/a]]/(a^2 - x^2)^(3/2), x]", 4653);
	}

	// {4653}
	public void test0033() {
		check("Integrate[ArcSin[x/a]^(3/2)/(a^2 - x^2)^(3/2), x]", 4653);
	}

	// {4659}
	public void test0034() {
		check("Integrate[1/((c - a^2*c*x^2)^(3/2)*ArcSin[a*x]^(3/2)), x]", 4659);
	}

	// {4659}
	public void test0035() {
		check("Integrate[1/((c - a^2*c*x^2)^(5/2)*ArcSin[a*x]^(3/2)), x]", 4659);
	}

	// {4659}
	public void test0036() {
		check("Integrate[1/((c - a^2*c*x^2)^(3/2)*ArcSin[a*x]^(5/2)), x]", 4659);
	}

	// {4659}
	public void test0037() {
		check("Integrate[1/((c - a^2*c*x^2)^(5/2)*ArcSin[a*x]^(5/2)), x]", 4659);
	}

	// {4641}
	public void test0038() {
		check("Integrate[ArcSin[a*x]^n/Sqrt[1 - a^2*x^2], x]", 4641);
	}

	// {4743}
	public void test0039() {
		check("Integrate[(d + e*x)^m*(a + b*ArcSin[c*x])^2, x]", 4743);
	}

	// {4805}
	public void test0040() {
		check("Integrate[1/(x*ArcSin[a + b*x]), x]", 4805);
	}

	// {4805}
	public void test0041() {
		check("Integrate[1/(x*ArcSin[a + b*x]^2), x]", 4805);
	}

	// {4805}
	public void test0042() {
		check("Integrate[1/(x*ArcSin[a + b*x]^3), x]", 4805);
	}

	// {4805}
	public void test0043() {
		check("Integrate[x^m*(a + b*ArcSin[c + d*x])^n, x]", 4805);
	}

	// {4805}
	public void test0044() {
		check("Integrate[(a + b*ArcSin[c + d*x])^n/x, x]", 4805);
	}

	// {4805}
	public void test0045() {
		check("Integrate[(c*e + d*e*x)^m/(a + b*ArcSin[c + d*x]), x]", 4805);
	}

	// {4807}
	public void test0046() {
		check("Integrate[1/((1 - a^2 - 2*a*b*x - b^2*x^2)^(3/2)*ArcSin[a + b*x]), x]", 4807);
	}

	// {4816}
	public void test0047() {
		check("Integrate[(a + b*ArcSin[1 + d*x^2])^(-1), x]", 4816);
	}

	// {4825}
	public void test0048() {
		check("Integrate[(a + b*ArcSin[1 + d*x^2])^(-2), x]", 4825);
	}

	// {4816}
	public void test0049() {
		check("Integrate[(a - b*ArcSin[1 - d*x^2])^(-1), x]", 4816);
	}

	// {4825}
	public void test0050() {
		check("Integrate[(a - b*ArcSin[1 - d*x^2])^(-2), x]", 4825);
	}

	// {4811}
	public void test0051() {
		check("Integrate[Sqrt[a + b*ArcSin[1 + d*x^2]], x]", 4811);
	}

	// {4819}
	public void test0052() {
		check("Integrate[1/Sqrt[a + b*ArcSin[1 + d*x^2]], x]", 4819);
	}

	// {4822}
	public void test0053() {
		check("Integrate[(a + b*ArcSin[1 + d*x^2])^(-3/2), x]", 4822);
	}

	// {4811}
	public void test0054() {
		check("Integrate[Sqrt[a - b*ArcSin[1 - d*x^2]], x]", 4811);
	}

	// {4819}
	public void test0055() {
		check("Integrate[1/Sqrt[a - b*ArcSin[1 - d*x^2]], x]", 4819);
	}

	// {4822}
	public void test0056() {
		check("Integrate[(a - b*ArcSin[1 - d*x^2])^(-3/2), x]", 4822);
	}

	// {4628}
	public void test0057() {
		check("Integrate[(b*x)^m*ArcCos[a*x]^4, x]", 4628);
	}

	// {4628}
	public void test0058() {
		check("Integrate[(b*x)^m*ArcCos[a*x]^3, x]", 4628);
	}

	// {4628}
	public void test0059() {
		check("Integrate[(d*x)^(3/2)*(a + b*ArcCos[c*x])^3, x]", 4628);
	}

	// {4628}
	public void test0060() {
		check("Integrate[Sqrt[d*x]*(a + b*ArcCos[c*x])^3, x]", 4628);
	}

	// {4628}
	public void test0061() {
		check("Integrate[(a + b*ArcCos[c*x])^3/Sqrt[d*x], x]", 4628);
	}

	// {4628}
	public void test0062() {
		check("Integrate[(a + b*ArcCos[c*x])^3/(d*x)^(3/2), x]", 4628);
	}

	// {4628}
	public void test0063() {
		check("Integrate[(a + b*ArcCos[c*x])^3/(d*x)^(5/2), x]", 4628);
	}

	// {4817}
	public void test0064() {
		check("Integrate[(a + b*ArcCos[1 + d*x^2])^(-1), x]", 4817);
	}

	// {4826}
	public void test0065() {
		check("Integrate[(a + b*ArcCos[1 + d*x^2])^(-2), x]", 4826);
	}

	// {4818}
	public void test0066() {
		check("Integrate[(a + b*ArcCos[-1 + d*x^2])^(-1), x]", 4818);
	}

	// {4827}
	public void test0067() {
		check("Integrate[(a + b*ArcCos[-1 + d*x^2])^(-2), x]", 4827);
	}

	// {4812}
	public void test0068() {
		check("Integrate[Sqrt[a + b*ArcCos[1 + d*x^2]], x]", 4812);
	}

	// {4820}
	public void test0069() {
		check("Integrate[1/Sqrt[a + b*ArcCos[1 + d*x^2]], x]", 4820);
	}

	// {4823}
	public void test0070() {
		check("Integrate[(a + b*ArcCos[1 + d*x^2])^(-3/2), x]", 4823);
	}

	// {4813}
	public void test0071() {
		check("Integrate[Sqrt[a + b*ArcCos[-1 + d*x^2]], x]", 4813);
	}

	// {4821}
	public void test0072() {
		check("Integrate[1/Sqrt[a + b*ArcCos[-1 + d*x^2]], x]", 4821);
	}

	// {4824}
	public void test0073() {
		check("Integrate[(a + b*ArcCos[-1 + d*x^2])^(-3/2), x]", 4824);
	}

	// {4858}
	public void test0074() {
		check("Integrate[(a + b*ArcTan[c*x])^2/(d + e*x), x]", 4858);
	}

	// {4860}
	public void test0075() {
		check("Integrate[(a + b*ArcTan[c*x])^3/(d + e*x), x]", 4860);
	}

	// {4858}
	public void test0076() {
		check("Integrate[(a + b*ArcTan[c*x])^2/(d + e*x), x]", 4858);
	}

	// {4884}
	public void test0077() {
		check("Integrate[ArcTan[a*x]/(c + a^2*c*x^2), x]", 4884);
	}

	// {4894}
	public void test0078() {
		check("Integrate[ArcTan[a*x]/(c + a^2*c*x^2)^(3/2), x]", 4894);
	}

	// {4884}
	public void test0079() {
		check("Integrate[ArcTan[a*x]^2/(c + a^2*c*x^2), x]", 4884);
	}

	// {4884}
	public void test0080() {
		check("Integrate[ArcTan[a*x]^3/(c + a^2*c*x^2), x]", 4884);
	}

	// {4882}
	public void test0081() {
		check("Integrate[1/((c + a^2*c*x^2)*ArcTan[a*x]), x]", 4882);
	}

	// {4926}
	public void test0082() {
		check("Integrate[x^3/((c + a^2*c*x^2)*ArcTan[a*x]^2), x]", 4926);
	}

	// {4926}
	public void test0083() {
		check("Integrate[x^2/((c + a^2*c*x^2)*ArcTan[a*x]^2), x]", 4926);
	}

	// {4922}
	public void test0084() {
		check("Integrate[x/((c + a^2*c*x^2)*ArcTan[a*x]^2), x]", 4922);
	}

	// {4884}
	public void test0085() {
		check("Integrate[1/((c + a^2*c*x^2)*ArcTan[a*x]^2), x]", 4884);
	}

	// {4926}
	public void test0086() {
		check("Integrate[1/(x*(c + a^2*c*x^2)*ArcTan[a*x]^2), x]", 4926);
	}

	// {4926}
	public void test0087() {
		check("Integrate[1/(x^2*(c + a^2*c*x^2)*ArcTan[a*x]^2), x]", 4926);
	}

	// {4926}
	public void test0088() {
		check("Integrate[1/(x^3*(c + a^2*c*x^2)*ArcTan[a*x]^2), x]", 4926);
	}

	// {4926}
	public void test0089() {
		check("Integrate[1/(x^4*(c + a^2*c*x^2)*ArcTan[a*x]^2), x]", 4926);
	}

	// {4942}
	public void test0090() {
		check("Integrate[1/(x*Sqrt[c + a^2*c*x^2]*ArcTan[a*x]^2), x]", 4942);
	}

	// {4926}
	public void test0091() {
		check("Integrate[x^m/((c + a^2*c*x^2)*ArcTan[a*x]^2), x]", 4926);
	}

	// {4926}
	public void test0092() {
		check("Integrate[x^3/((c + a^2*c*x^2)*ArcTan[a*x]^3), x]", 4926);
	}

	// {4926}
	public void test0093() {
		check("Integrate[x^2/((c + a^2*c*x^2)*ArcTan[a*x]^3), x]", 4926);
	}

	// {4922}
	public void test0094() {
		check("Integrate[x/((c + a^2*c*x^2)*ArcTan[a*x]^3), x]", 4922);
	}

	// {4884}
	public void test0095() {
		check("Integrate[1/((c + a^2*c*x^2)*ArcTan[a*x]^3), x]", 4884);
	}

	// {4926}
	public void test0096() {
		check("Integrate[1/(x*(c + a^2*c*x^2)*ArcTan[a*x]^3), x]", 4926);
	}

	// {4926}
	public void test0097() {
		check("Integrate[1/(x^2*(c + a^2*c*x^2)*ArcTan[a*x]^3), x]", 4926);
	}

	// {4926}
	public void test0098() {
		check("Integrate[1/(x^3*(c + a^2*c*x^2)*ArcTan[a*x]^3), x]", 4926);
	}

	// {4926}
	public void test0099() {
		check("Integrate[1/(x^4*(c + a^2*c*x^2)*ArcTan[a*x]^3), x]", 4926);
	}

	// {4942}
	public void test0100() {
		check("Integrate[1/(x*Sqrt[c + a^2*c*x^2]*ArcTan[a*x]^3), x]", 4942);
	}

	// {4926}
	public void test0101() {
		check("Integrate[x^m/((c + a^2*c*x^2)*ArcTan[a*x]^3), x]", 4926);
	}

	// {4930}
	public void test0102() {
		check("Integrate[x*(c + a^2*c*x^2)*Sqrt[ArcTan[a*x]], x]", 4930);
	}

	// {4930}
	public void test0103() {
		check("Integrate[x*(c + a^2*c*x^2)^2*Sqrt[ArcTan[a*x]], x]", 4930);
	}

	// {4930}
	public void test0104() {
		check("Integrate[x*(c + a^2*c*x^2)^3*Sqrt[ArcTan[a*x]], x]", 4930);
	}

	// {4922}
	public void test0105() {
		check("Integrate[(x*Sqrt[ArcTan[a*x]])/(c + a^2*c*x^2), x]", 4922);
	}

	// {4884}
	public void test0106() {
		check("Integrate[Sqrt[ArcTan[a*x]]/(c + a^2*c*x^2), x]", 4884);
	}

	// {4924}
	public void test0107() {
		check("Integrate[Sqrt[ArcTan[a*x]]/(x*(c + a^2*c*x^2)), x]", 4924);
	}

	// {4930}
	public void test0108() {
		check("Integrate[x*Sqrt[c + a^2*c*x^2]*Sqrt[ArcTan[a*x]], x]", 4930);
	}

	// {4930}
	public void test0109() {
		check("Integrate[x*(c + a^2*c*x^2)^(3/2)*Sqrt[ArcTan[a*x]], x]", 4930);
	}

	// {4930}
	public void test0110() {
		check("Integrate[x*(c + a^2*c*x^2)^(5/2)*Sqrt[ArcTan[a*x]], x]", 4930);
	}

	// {4952}
	public void test0111() {
		check("Integrate[(x^2*Sqrt[ArcTan[a*x]])/Sqrt[c + a^2*c*x^2], x]", 4952);
	}

	// {4930}
	public void test0112() {
		check("Integrate[(x*Sqrt[ArcTan[a*x]])/Sqrt[c + a^2*c*x^2], x]", 4930);
	}

	// {4944}
	public void test0113() {
		check("Integrate[Sqrt[ArcTan[a*x]]/(x^2*Sqrt[c + a^2*c*x^2]), x]", 4944);
	}

	// {4962}
	public void test0114() {
		check("Integrate[Sqrt[ArcTan[a*x]]/(x^3*Sqrt[c + a^2*c*x^2]), x]", 4962);
	}

	// {4930}
	public void test0115() {
		check("Integrate[x*(c + a^2*c*x^2)*ArcTan[a*x]^(3/2), x]", 4930);
	}

	// {4880}
	public void test0116() {
		check("Integrate[(c + a^2*c*x^2)*ArcTan[a*x]^(3/2), x]", 4880);
	}

	// {4930}
	public void test0117() {
		check("Integrate[x*(c + a^2*c*x^2)^2*ArcTan[a*x]^(3/2), x]", 4930);
	}

	// {4930}
	public void test0118() {
		check("Integrate[x*(c + a^2*c*x^2)^3*ArcTan[a*x]^(3/2), x]", 4930);
	}

	// {4922}
	public void test0119() {
		check("Integrate[(x*ArcTan[a*x]^(3/2))/(c + a^2*c*x^2), x]", 4922);
	}

	// {4884}
	public void test0120() {
		check("Integrate[ArcTan[a*x]^(3/2)/(c + a^2*c*x^2), x]", 4884);
	}

	// {4924}
	public void test0121() {
		check("Integrate[ArcTan[a*x]^(3/2)/(x*(c + a^2*c*x^2)), x]", 4924);
	}

	// {4930}
	public void test0122() {
		check("Integrate[x*Sqrt[c + a^2*c*x^2]*ArcTan[a*x]^(3/2), x]", 4930);
	}

	// {4880}
	public void test0123() {
		check("Integrate[Sqrt[c + a^2*c*x^2]*ArcTan[a*x]^(3/2), x]", 4880);
	}

	// {4930}
	public void test0124() {
		check("Integrate[x*(c + a^2*c*x^2)^(3/2)*ArcTan[a*x]^(3/2), x]", 4930);
	}

	// {4930}
	public void test0125() {
		check("Integrate[x*(c + a^2*c*x^2)^(5/2)*ArcTan[a*x]^(3/2), x]", 4930);
	}

	// {4930}
	public void test0126() {
		check("Integrate[(x*ArcTan[a*x]^(3/2))/Sqrt[c + a^2*c*x^2], x]", 4930);
	}

	// {4944}
	public void test0127() {
		check("Integrate[ArcTan[a*x]^(3/2)/(x^2*Sqrt[c + a^2*c*x^2]), x]", 4944);
	}

	// {4880}
	public void test0128() {
		check("Integrate[(c + a^2*c*x^2)*ArcTan[a*x]^(5/2), x]", 4880);
	}

	// {4922}
	public void test0129() {
		check("Integrate[(x*ArcTan[a*x]^(5/2))/(c + a^2*c*x^2), x]", 4922);
	}

	// {4884}
	public void test0130() {
		check("Integrate[ArcTan[a*x]^(5/2)/(c + a^2*c*x^2), x]", 4884);
	}

	// {4924}
	public void test0131() {
		check("Integrate[ArcTan[a*x]^(5/2)/(x*(c + a^2*c*x^2)), x]", 4924);
	}

	// {4880}
	public void test0132() {
		check("Integrate[Sqrt[c + a^2*c*x^2]*ArcTan[a*x]^(5/2), x]", 4880);
	}

	// {4930}
	public void test0133() {
		check("Integrate[(x*ArcTan[a*x]^(5/2))/Sqrt[c + a^2*c*x^2], x]", 4930);
	}

	// {4944}
	public void test0134() {
		check("Integrate[ArcTan[a*x]^(5/2)/(x^2*Sqrt[c + a^2*c*x^2]), x]", 4944);
	}

	// {4922}
	public void test0135() {
		check("Integrate[x/((c + a^2*c*x^2)*Sqrt[ArcTan[a*x]]), x]", 4922);
	}

	// {4884}
	public void test0136() {
		check("Integrate[1/((c + a^2*c*x^2)*Sqrt[ArcTan[a*x]]), x]", 4884);
	}

	// {4926}
	public void test0137() {
		check("Integrate[x^m/((c + a^2*c*x^2)*ArcTan[a*x]^(3/2)), x]", 4926);
	}

	// {4922}
	public void test0138() {
		check("Integrate[x/((c + a^2*c*x^2)*ArcTan[a*x]^(3/2)), x]", 4922);
	}

	// {4884}
	public void test0139() {
		check("Integrate[1/((c + a^2*c*x^2)*ArcTan[a*x]^(3/2)), x]", 4884);
	}

	// {4926}
	public void test0140() {
		check("Integrate[1/(x*(c + a^2*c*x^2)*ArcTan[a*x]^(3/2)), x]", 4926);
	}

	// {4968}
	public void test0141() {
		check("Integrate[x^4/((c + a^2*c*x^2)^2*ArcTan[a*x]^(3/2)), x]", 4968);
	}

	// {4968}
	public void test0142() {
		check("Integrate[1/(x^2*(c + a^2*c*x^2)^2*ArcTan[a*x]^(3/2)), x]", 4968);
	}

	// {4968}
	public void test0143() {
		check("Integrate[1/(x^3*(c + a^2*c*x^2)^2*ArcTan[a*x]^(3/2)), x]", 4968);
	}

	// {4968}
	public void test0144() {
		check("Integrate[1/(x^4*(c + a^2*c*x^2)^2*ArcTan[a*x]^(3/2)), x]", 4968);
	}

	// {4968}
	public void test0145() {
		check("Integrate[1/(x^2*(c + a^2*c*x^2)^3*ArcTan[a*x]^(3/2)), x]", 4968);
	}

	// {4968}
	public void test0146() {
		check("Integrate[1/(x^3*(c + a^2*c*x^2)^3*ArcTan[a*x]^(3/2)), x]", 4968);
	}

	// {4968}
	public void test0147() {
		check("Integrate[1/(x^4*(c + a^2*c*x^2)^3*ArcTan[a*x]^(3/2)), x]", 4968);
	}

	// {4942}
	public void test0148() {
		check("Integrate[1/(x*Sqrt[c + a^2*c*x^2]*ArcTan[a*x]^(3/2)), x]", 4942);
	}

	// {4968}
	public void test0149() {
		check("Integrate[x^3/((c + a^2*c*x^2)^(3/2)*ArcTan[a*x]^(3/2)), x]", 4968);
	}

	// {4968}
	public void test0150() {
		check("Integrate[1/(x^2*(c + a^2*c*x^2)^(3/2)*ArcTan[a*x]^(3/2)), x]", 4968);
	}

	// {4968}
	public void test0151() {
		check("Integrate[1/(x^3*(c + a^2*c*x^2)^(3/2)*ArcTan[a*x]^(3/2)), x]", 4968);
	}

	// {4968}
	public void test0152() {
		check("Integrate[1/(x^4*(c + a^2*c*x^2)^(3/2)*ArcTan[a*x]^(3/2)), x]", 4968);
	}

	// {4968}
	public void test0153() {
		check("Integrate[1/(x^2*(c + a^2*c*x^2)^(5/2)*ArcTan[a*x]^(3/2)), x]", 4968);
	}

	// {4968}
	public void test0154() {
		check("Integrate[1/(x^3*(c + a^2*c*x^2)^(5/2)*ArcTan[a*x]^(3/2)), x]", 4968);
	}

	// {4968}
	public void test0155() {
		check("Integrate[1/(x^4*(c + a^2*c*x^2)^(5/2)*ArcTan[a*x]^(3/2)), x]", 4968);
	}

	// {4926}
	public void test0156() {
		check("Integrate[x^m/((c + a^2*c*x^2)*ArcTan[a*x]^(5/2)), x]", 4926);
	}

	// {4922}
	public void test0157() {
		check("Integrate[x/((c + a^2*c*x^2)*ArcTan[a*x]^(5/2)), x]", 4922);
	}

	// {4884}
	public void test0158() {
		check("Integrate[1/((c + a^2*c*x^2)*ArcTan[a*x]^(5/2)), x]", 4884);
	}

	// {4926}
	public void test0159() {
		check("Integrate[1/(x*(c + a^2*c*x^2)*ArcTan[a*x]^(5/2)), x]", 4926);
	}

	// {4942}
	public void test0160() {
		check("Integrate[1/(x*Sqrt[c + a^2*c*x^2]*ArcTan[a*x]^(5/2)), x]", 4942);
	}

	// {4922}
	public void test0161() {
		check("Integrate[(x*ArcTan[a*x]^n)/(c + a^2*c*x^2), x]", 4922);
	}

	// {4884}
	public void test0162() {
		check("Integrate[ArcTan[a*x]^n/(c + a^2*c*x^2), x]", 4884);
	}

	// {5047}
	public void test0163() {
		check("Integrate[(e + f*x)^m*(a + b*ArcTan[c + d*x])^2, x]", 5047);
	}

	// {5047}
	public void test0164() {
		check("Integrate[(e + f*x)^m*(a + b*ArcTan[c + d*x])^3, x]", 5047);
	}

	// {5055}
	public void test0165() {
		check("Integrate[ArcTan[a + b*x]/(1 + a^2 + 2*a*b*x + b^2*x^2)^(1/3), x]", 5055);
	}

	// {5055}
	public void test0166() {
		check("Integrate[ArcTan[a + b*x]/((1 + a^2)*c + 2*a*b*c*x + b^2*c*x^2)^(1/3), x]", 5055);
	}

	// {5057}
	public void test0167() {
		check("Integrate[((a + b*x)^2*ArcTan[a + b*x])/(1 + a^2 + 2*a*b*x + b^2*x^2)^(1/3), x]", 5057);
	}

	// {5057}
	public void test0168() {
		check("Integrate[((a + b*x)^2*ArcTan[a + b*x])/((1 + a^2)*c + 2*a*b*c*x + b^2*c*x^2)^(1/3), x]", 5057);
	}

	// {4882}
	public void test0169() {
		check("Integrate[1/((1 + x^2)*(2 + ArcTan[x])), x]", 4882);
	}

	// {4882}
	public void test0170() {
		check("Integrate[1/((a + a*x^2)*(b - 2*b*ArcTan[x])), x]", 4882);
	}

	// {5071}
	public void test0171() {
		check("Integrate[E^ArcTan[a*x]/(c + a^2*c*x^2), x]", 5071);
	}

	// {5069}
	public void test0172() {
		check("Integrate[E^ArcTan[a*x]/(c + a^2*c*x^2)^(3/2), x]", 5069);
	}

	// {5071}
	public void test0173() {
		check("Integrate[E^(2*ArcTan[a*x])/(c + a^2*c*x^2), x]", 5071);
	}

	// {5069}
	public void test0174() {
		check("Integrate[E^(2*ArcTan[a*x])/(c + a^2*c*x^2)^(3/2), x]", 5069);
	}

	// {5071}
	public void test0175() {
		check("Integrate[1/(E^ArcTan[a*x]*(c + a^2*c*x^2)), x]", 5071);
	}

	// {5069}
	public void test0176() {
		check("Integrate[1/(E^ArcTan[a*x]*(c + a^2*c*x^2)^(3/2)), x]", 5069);
	}

	// {5071}
	public void test0177() {
		check("Integrate[1/(E^(2*ArcTan[a*x])*(c + a^2*c*x^2)), x]", 5071);
	}

	// {5069}
	public void test0178() {
		check("Integrate[1/(E^(2*ArcTan[a*x])*(c + a^2*c*x^2)^(3/2)), x]", 5069);
	}

	// {5071}
	public void test0179() {
		check("Integrate[E^(n*ArcTan[a*x])/(c + a^2*c*x^2), x]", 5071);
	}

	// {5079}
	public void test0180() {
		check("Integrate[E^(I*n*ArcTan[a*x])*x^2*(c + a^2*c*x^2)^(-1 - n^2/2), x]", 5079);
	}

	// {4885}
	public void test0181() {
		check("Integrate[ArcCot[x]/(1 + x^2), x]", 4885);
	}

	// {4883}
	public void test0182() {
		check("Integrate[1/((1 + x^2)*ArcCot[x]), x]", 4883);
	}

	// {4885}
	public void test0183() {
		check("Integrate[ArcCot[x]^n/(1 + x^2), x]", 4885);
	}

	// {4895}
	public void test0184() {
		check("Integrate[ArcCot[x]/(a + a*x^2)^(3/2), x]", 4895);
	}

	// {5056}
	public void test0185() {
		check("Integrate[ArcCot[a + b*x]/(1 + a^2 + 2*a*b*x + b^2*x^2)^(1/3), x]", 5056);
	}

	// {5056}
	public void test0186() {
		check("Integrate[ArcCot[a + b*x]/((1 + a^2)*c + 2*a*b*c*x + b^2*c*x^2)^(1/3), x]", 5056);
	}

	// {5058}
	public void test0187() {
		check("Integrate[((a + b*x)^2*ArcCot[a + b*x])/(1 + a^2 + 2*a*b*x + b^2*x^2)^(1/3), x]", 5058);
	}

	// {5058}
	public void test0188() {
		check("Integrate[((a + b*x)^2*ArcCot[a + b*x])/((1 + a^2)*c + 2*a*b*c*x + b^2*c*x^2)^(1/3), x]", 5058);
	}

	// {5048}
	public void test0189() {
		check("Integrate[(e + f*x)^m*(a + b*ArcCot[c + d*x])^2, x]", 5048);
	}

	// {5048}
	public void test0190() {
		check("Integrate[(e + f*x)^m*(a + b*ArcCot[c + d*x])^3, x]", 5048);
	}

	// {4883}
	public void test0191() {
		check("Integrate[1/((a + a*x^2)*(b - 2*b*ArcCot[x])), x]", 4883);
	}

	// {5113}
	public void test0192() {
		check("Integrate[E^ArcCot[x]/(a + a*x^2), x]", 5113);
	}

	// {5114}
	public void test0193() {
		check("Integrate[E^ArcCot[x]/(a + a*x^2)^(3/2), x]", 5114);
	}
}
