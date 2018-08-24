package org.matheclipse.core.rubi;

public class RuleTestSpecialFunctions extends AbstractRubiTestCase {

	public RuleTestSpecialFunctions(String name) {
		super(name, false);
	}

	// {6656}
	public void test0001() {
		check("Integrate[Derivative[1][f][x], x]", 6656);
	}

	// {6656}
	public void test0002() {
		check("Integrate[Derivative[2][f][x], x]", 6656);
	}

	// {6656}
	public void test0003() {
		check("Integrate[Derivative[3][f][x], x]", 6656);
	}

	// {6656}
	public void test0004() {
		check("Integrate[Derivative[n][f][x], x]", 6656);
	}

	// {6663}
	public void test0005() {
		check("Integrate[f[g[x]]*Derivative[1][g][x], x]", 6663);
	}

	// {6663}
	public void test0006() {
		check("Integrate[f[Derivative[1][g][x]]*Derivative[2][g][x], x]", 6663);
	}

	// {6675}
	public void test0007() {
		check("Integrate[(g[x]*Derivative[1][f][x] - f[x]*Derivative[1][g][x])/g[x]^2, x]", 6675);
	}

	// {6676}
	public void test0008() {
		check("Integrate[(g[x]*Derivative[1][f][x] - f[x]*Derivative[1][g][x])/(f[x]*g[x]), x]", 6676);
	}

	// {6657}
	public void test0009() {
		check("Integrate[F^(a + b*x)*Derivative[1][f][x], x]", 6657);
	}

	// {6658}
	public void test0010() {
		check("Integrate[F^(a + b*x)*Derivative[-1][f][x], x]", 6658);
	}

	// {6659}
	public void test0011() {
		check("Integrate[Sin[a + b*x]*Derivative[1][f][x], x]", 6659);
	}

	// {6661}
	public void test0012() {
		check("Integrate[Sin[a + b*x]*Derivative[-1][f][x], x]", 6661);
	}

	// {6660}
	public void test0013() {
		check("Integrate[Cos[a + b*x]*Derivative[1][f][x], x]", 6660);
	}

	// {6662}
	public void test0014() {
		check("Integrate[Cos[a + b*x]*Derivative[-1][f][x], x]", 6662);
	}

	// {6358}
	public void test0015() {
		check("Integrate[Erf[b*x]/x, x]", 6358);
	}

	// {6349}
	public void test0016() {
		check("Integrate[Erf[b*x], x]", 6349);
	}

	// {6349}
	public void test0017() {
		check("Integrate[Erf[a + b*x], x]", 6349);
	}

	// {6361}
	public void test0018() {
		check("Integrate[Erf[a + b*x]/(c + d*x)^2, x]", 6361);
	}

	// {6388}
	public void test0019() {
		check("Integrate[(E^(c + b^2*x^2)*Erf[b*x])/x, x]", 6388);
	}

	// {6376}
	public void test0020() {
		check("Integrate[E^(c + b^2*x^2)*Erf[b*x], x]", 6376);
	}

	// {6391}
	public void test0021() {
		check("Integrate[(E^(c + d*x^2)*Erf[a + b*x])/x^2, x]", 6391);
	}

	// {6350}
	public void test0022() {
		check("Integrate[Erfc[b*x], x]", 6350);
	}

	// {6350}
	public void test0023() {
		check("Integrate[Erfc[a + b*x], x]", 6350);
	}

	// {6362}
	public void test0024() {
		check("Integrate[Erfc[a + b*x]/(c + d*x)^2, x]", 6362);
	}

	// {6392}
	public void test0025() {
		check("Integrate[(E^(c + d*x^2)*Erfc[a + b*x])/x^2, x]", 6392);
	}

	// {6360}
	public void test0026() {
		check("Integrate[Erfi[b*x]/x, x]", 6360);
	}

	// {6351}
	public void test0027() {
		check("Integrate[Erfi[b*x], x]", 6351);
	}

	// {6351}
	public void test0028() {
		check("Integrate[Erfi[a + b*x], x]", 6351);
	}

	// {6363}
	public void test0029() {
		check("Integrate[Erfi[a + b*x]/(c + d*x)^2, x]", 6363);
	}

	// {6390}
	public void test0030() {
		check("Integrate[Erfi[b*x]/(E^(b^2*x^2)*x), x]", 6390);
	}

	// {6378}
	public void test0031() {
		check("Integrate[Erfi[b*x]/E^(b^2*x^2), x]", 6378);
	}

	// {6393}
	public void test0032() {
		check("Integrate[(E^(c + d*x^2)*Erfi[a + b*x])/x^2, x]", 6393);
	}

	// {6418}
	public void test0033() {
		check("Integrate[FresnelS[b*x], x]", 6418);
	}

	// {6418}
	public void test0034() {
		check("Integrate[FresnelS[a + b*x], x]", 6418);
	}

	// {6418}
	public void test0035() {
		check("Integrate[FresnelS[a + b*x], x]", 6418);
	}

	// {6430}
	public void test0036() {
		check("Integrate[FresnelS[b*x]^2/x^2, x]", 6430);
	}

	// {6430}
	public void test0037() {
		check("Integrate[FresnelS[b*x]^2/x^3, x]", 6430);
	}

	// {6446}
	public void test0038() {
		check("Integrate[Cos[(b^2*Pi*x^2)/2]*FresnelS[b*x], x]", 6446);
	}

	// {6419}
	public void test0039() {
		check("Integrate[FresnelC[b*x], x]", 6419);
	}

	// {6419}
	public void test0040() {
		check("Integrate[FresnelC[a + b*x], x]", 6419);
	}

	// {6419}
	public void test0041() {
		check("Integrate[FresnelC[a + b*x], x]", 6419);
	}

	// {6431}
	public void test0042() {
		check("Integrate[FresnelC[b*x]^2/x^2, x]", 6431);
	}

	// {6431}
	public void test0043() {
		check("Integrate[FresnelC[b*x]^2/x^3, x]", 6431);
	}

	// {6447}
	public void test0044() {
		check("Integrate[FresnelC[b*x]*Sin[(b^2*Pi*x^2)/2], x]", 6447);
	}

	// {6478}
	public void test0045() {
		check("Integrate[x^2*ExpIntegralE[1, b*x], x]", 6478);
	}

	// {6478}
	public void test0046() {
		check("Integrate[x*ExpIntegralE[1, b*x], x]", 6478);
	}

	// {6473}
	public void test0047() {
		check("Integrate[ExpIntegralE[1, b*x], x]", 6473);
	}

	// {6475}
	public void test0048() {
		check("Integrate[ExpIntegralE[1, b*x]/x, x]", 6475);
	}

	// {6478}
	public void test0049() {
		check("Integrate[ExpIntegralE[1, b*x]/x^2, x]", 6478);
	}

	// {6478}
	public void test0050() {
		check("Integrate[ExpIntegralE[1, b*x]/x^3, x]", 6478);
	}

	// {6478}
	public void test0051() {
		check("Integrate[ExpIntegralE[1, b*x]/x^4, x]", 6478);
	}

	// {6478}
	public void test0052() {
		check("Integrate[x^2*ExpIntegralE[2, b*x], x]", 6478);
	}

	// {6478}
	public void test0053() {
		check("Integrate[x*ExpIntegralE[2, b*x], x]", 6478);
	}

	// {6473}
	public void test0054() {
		check("Integrate[ExpIntegralE[2, b*x], x]", 6473);
	}

	// {6478}
	public void test0055() {
		check("Integrate[ExpIntegralE[2, b*x]/x, x]", 6478);
	}

	// {6478}
	public void test0056() {
		check("Integrate[ExpIntegralE[2, b*x]/x^3, x]", 6478);
	}

	// {6478}
	public void test0057() {
		check("Integrate[ExpIntegralE[2, b*x]/x^4, x]", 6478);
	}

	// {6478}
	public void test0058() {
		check("Integrate[ExpIntegralE[2, b*x]/x^5, x]", 6478);
	}

	// {6478}
	public void test0059() {
		check("Integrate[x^2*ExpIntegralE[3, b*x], x]", 6478);
	}

	// {6478}
	public void test0060() {
		check("Integrate[x*ExpIntegralE[3, b*x], x]", 6478);
	}

	// {6473}
	public void test0061() {
		check("Integrate[ExpIntegralE[3, b*x], x]", 6473);
	}

	// {6478}
	public void test0062() {
		check("Integrate[ExpIntegralE[3, b*x]/x, x]", 6478);
	}

	// {6478}
	public void test0063() {
		check("Integrate[ExpIntegralE[3, b*x]/x^2, x]", 6478);
	}

	// {6478}
	public void test0064() {
		check("Integrate[ExpIntegralE[3, b*x]/x^4, x]", 6478);
	}

	// {6478}
	public void test0065() {
		check("Integrate[ExpIntegralE[3, b*x]/x^5, x]", 6478);
	}

	// {6478}
	public void test0066() {
		check("Integrate[ExpIntegralE[3, b*x]/x^6, x]", 6478);
	}

	// {6478}
	public void test0067() {
		check("Integrate[x^3*ExpIntegralE[-1, b*x], x]", 6478);
	}

	// {6478}
	public void test0068() {
		check("Integrate[x^2*ExpIntegralE[-1, b*x], x]", 6478);
	}

	// {6473}
	public void test0069() {
		check("Integrate[ExpIntegralE[-1, b*x], x]", 6473);
	}

	// {6478}
	public void test0070() {
		check("Integrate[ExpIntegralE[-1, b*x]/x, x]", 6478);
	}

	// {6478}
	public void test0071() {
		check("Integrate[ExpIntegralE[-1, b*x]/x^2, x]", 6478);
	}

	// {6478}
	public void test0072() {
		check("Integrate[ExpIntegralE[-1, b*x]/x^3, x]", 6478);
	}

	// {6478}
	public void test0073() {
		check("Integrate[x^4*ExpIntegralE[-2, b*x], x]", 6478);
	}

	// {6478}
	public void test0074() {
		check("Integrate[x^3*ExpIntegralE[-2, b*x], x]", 6478);
	}

	// {6478}
	public void test0075() {
		check("Integrate[x*ExpIntegralE[-2, b*x], x]", 6478);
	}

	// {6473}
	public void test0076() {
		check("Integrate[ExpIntegralE[-1, b*x], x]", 6473);
	}

	// {6478}
	public void test0077() {
		check("Integrate[ExpIntegralE[-2, b*x]/x, x]", 6478);
	}

	// {6478}
	public void test0078() {
		check("Integrate[ExpIntegralE[-2, b*x]/x^2, x]", 6478);
	}

	// {6478}
	public void test0079() {
		check("Integrate[ExpIntegralE[-2, b*x]/x^3, x]", 6478);
	}

	// {6478}
	public void test0080() {
		check("Integrate[x^5*ExpIntegralE[-3, b*x], x]", 6478);
	}

	// {6478}
	public void test0081() {
		check("Integrate[x^4*ExpIntegralE[-3, b*x], x]", 6478);
	}

	// {6478}
	public void test0082() {
		check("Integrate[x^2*ExpIntegralE[-3, b*x], x]", 6478);
	}

	// {6478}
	public void test0083() {
		check("Integrate[x*ExpIntegralE[-3, b*x], x]", 6478);
	}

	// {6473}
	public void test0084() {
		check("Integrate[ExpIntegralE[-1, b*x], x]", 6473);
	}

	// {6478}
	public void test0085() {
		check("Integrate[ExpIntegralE[-3, b*x]/x, x]", 6478);
	}

	// {6478}
	public void test0086() {
		check("Integrate[ExpIntegralE[-3, b*x]/x^2, x]", 6478);
	}

	// {6478}
	public void test0087() {
		check("Integrate[ExpIntegralE[-3, b*x]/x^3, x]", 6478);
	}

	// {6475}
	public void test0088() {
		check("Integrate[ExpIntegralE[1, b*x]/x, x]", 6475);
	}

	// {6477}
	public void test0089() {
		check("Integrate[(d*x)^(3/2)*ExpIntegralE[-3/2, b*x], x]", 6477);
	}

	// {6477}
	public void test0090() {
		check("Integrate[Sqrt[d*x]*ExpIntegralE[-1/2, b*x], x]", 6477);
	}

	// {6477}
	public void test0091() {
		check("Integrate[ExpIntegralE[1/2, b*x]/Sqrt[d*x], x]", 6477);
	}

	// {6477}
	public void test0092() {
		check("Integrate[ExpIntegralE[3/2, b*x]/(d*x)^(3/2), x]", 6477);
	}

	// {6477}
	public void test0093() {
		check("Integrate[ExpIntegralE[5/2, b*x]/(d*x)^(5/2), x]", 6477);
	}

	// {6478}
	public void test0094() {
		check("Integrate[x^m*ExpIntegralE[n, x], x]", 6478);
	}

	// {6478}
	public void test0095() {
		check("Integrate[x^m*ExpIntegralE[n, b*x], x]", 6478);
	}

	// {6478}
	public void test0096() {
		check("Integrate[(d*x)^m*ExpIntegralE[n, x], x]", 6478);
	}

	// {6478}
	public void test0097() {
		check("Integrate[(d*x)^m*ExpIntegralE[n, b*x], x]", 6478);
	}

	// {6477}
	public void test0098() {
		check("Integrate[ExpIntegralE[n, x]/x^n, x]", 6477);
	}

	// {6477}
	public void test0099() {
		check("Integrate[ExpIntegralE[n, b*x]/x^n, x]", 6477);
	}

	// {6477}
	public void test0100() {
		check("Integrate[ExpIntegralE[n, x]/(d*x)^n, x]", 6477);
	}

	// {6477}
	public void test0101() {
		check("Integrate[ExpIntegralE[n, b*x]/(d*x)^n, x]", 6477);
	}

	// {6478}
	public void test0102() {
		check("Integrate[x^2*ExpIntegralE[n, b*x], x]", 6478);
	}

	// {6478}
	public void test0103() {
		check("Integrate[x*ExpIntegralE[n, b*x], x]", 6478);
	}

	// {6473}
	public void test0104() {
		check("Integrate[ExpIntegralE[n, b*x], x]", 6473);
	}

	// {6478}
	public void test0105() {
		check("Integrate[ExpIntegralE[n, b*x]/x, x]", 6478);
	}

	// {6478}
	public void test0106() {
		check("Integrate[ExpIntegralE[n, b*x]/x^2, x]", 6478);
	}

	// {6478}
	public void test0107() {
		check("Integrate[ExpIntegralE[n, b*x]/x^3, x]", 6478);
	}

	// {6473}
	public void test0108() {
		check("Integrate[ExpIntegralE[1, a + b*x], x]", 6473);
	}

	// {6473}
	public void test0109() {
		check("Integrate[ExpIntegralE[2, a + b*x], x]", 6473);
	}

	// {6480}
	public void test0110() {
		check("Integrate[ExpIntegralE[2, a + b*x]/(c + d*x)^2, x]", 6480);
	}

	// {6473}
	public void test0111() {
		check("Integrate[ExpIntegralE[3, a + b*x], x]", 6473);
	}

	// {6480}
	public void test0112() {
		check("Integrate[ExpIntegralE[3, a + b*x]/(c + d*x)^2, x]", 6480);
	}

	// {6473}
	public void test0113() {
		check("Integrate[ExpIntegralE[-1, a + b*x], x]", 6473);
	}

	// {6473}
	public void test0114() {
		check("Integrate[ExpIntegralE[-2, a + b*x], x]", 6473);
	}

	// {6473}
	public void test0115() {
		check("Integrate[ExpIntegralE[-3, a + b*x], x]", 6473);
	}

	// {6480}
	public void test0116() {
		check("Integrate[(c + d*x)^m*ExpIntegralE[1, a + b*x], x]", 6480);
	}

	// {6479}
	public void test0117() {
		check("Integrate[(c + d*x)^m*ExpIntegralE[-1, a + b*x], x]", 6479);
	}

	// {6473}
	public void test0118() {
		check("Integrate[ExpIntegralE[n, a + b*x], x]", 6473);
	}

	// {6482}
	public void test0119() {
		check("Integrate[ExpIntegralEi[b*x], x]", 6482);
	}

	// {6482}
	public void test0120() {
		check("Integrate[ExpIntegralEi[a + b*x], x]", 6482);
	}

	// {-1}
	public void test0121() {
		check("Integrate[ExpIntegralEi[a + b*x]^3, x]", -1);
	}

	// {6485}
	public void test0122() {
		check("Integrate[(c + d*x)^m*ExpIntegralEi[a + b*x], x]", 6485);
	}

	// {6686}
	public void test0123() {
		check("Integrate[(E^(b*x)*ExpIntegralEi[b*x])/x, x]", 6686);
	}

	// {6495}
	public void test0124() {
		check("Integrate[LogIntegral[b*x], x]", 6495);
	}

	// {6496}
	public void test0125() {
		check("Integrate[LogIntegral[b*x]/x, x]", 6496);
	}

	// {6495}
	public void test0126() {
		check("Integrate[LogIntegral[a + b*x], x]", 6495);
	}

	// {6498}
	public void test0127() {
		check("Integrate[LogIntegral[a + b*x]/x^2, x]", 6498);
	}

	// {6498}
	public void test0128() {
		check("Integrate[(d*x)^m*LogIntegral[a + b*x], x]", 6498);
	}

	// {6499}
	public void test0129() {
		check("Integrate[SinIntegral[b*x], x]", 6499);
	}

	// {6501}
	public void test0130() {
		check("Integrate[SinIntegral[b*x]/x, x]", 6501);
	}

	// {6503}
	public void test0131() {
		check("Integrate[x^m*SinIntegral[a + b*x], x]", 6503);
	}

	// {6499}
	public void test0132() {
		check("Integrate[SinIntegral[a + b*x], x]", 6499);
	}

	// {6686}
	public void test0133() {
		check("Integrate[(Sin[b*x]*SinIntegral[b*x])/x, x]", 6686);
	}

	// {6500}
	public void test0134() {
		check("Integrate[CosIntegral[b*x], x]", 6500);
	}

	// {6502}
	public void test0135() {
		check("Integrate[CosIntegral[b*x]/x, x]", 6502);
	}

	// {6504}
	public void test0136() {
		check("Integrate[x^m*CosIntegral[a + b*x], x]", 6504);
	}

	// {6500}
	public void test0137() {
		check("Integrate[CosIntegral[a + b*x], x]", 6500);
	}

	// {6686}
	public void test0138() {
		check("Integrate[(Cos[b*x]*CosIntegral[b*x])/x, x]", 6686);
	}

	// {6528}
	public void test0139() {
		check("Integrate[SinhIntegral[b*x], x]", 6528);
	}

	// {6530}
	public void test0140() {
		check("Integrate[SinhIntegral[b*x]/x, x]", 6530);
	}

	// {6532}
	public void test0141() {
		check("Integrate[x^m*SinhIntegral[a + b*x], x]", 6532);
	}

	// {6528}
	public void test0142() {
		check("Integrate[SinhIntegral[a + b*x], x]", 6528);
	}

	// {6686}
	public void test0143() {
		check("Integrate[(Sinh[b*x]*SinhIntegral[b*x])/x, x]", 6686);
	}

	// {6529}
	public void test0144() {
		check("Integrate[CoshIntegral[b*x], x]", 6529);
	}

	// {6531}
	public void test0145() {
		check("Integrate[CoshIntegral[b*x]/x, x]", 6531);
	}

	// {6533}
	public void test0146() {
		check("Integrate[x^m*CoshIntegral[a + b*x], x]", 6533);
	}

	// {6529}
	public void test0147() {
		check("Integrate[CoshIntegral[a + b*x], x]", 6529);
	}

	// {6686}
	public void test0148() {
		check("Integrate[(Cosh[b*x]*CoshIntegral[b*x])/x, x]", 6686);
	}

	// {6562}
	public void test0149() {
		check("Integrate[x^100*Gamma[0, a*x], x]", 6562);
	}

	// {6562}
	public void test0150() {
		check("Integrate[x^2*Gamma[0, a*x], x]", 6562);
	}

	// {6562}
	public void test0151() {
		check("Integrate[x*Gamma[0, a*x], x]", 6562);
	}

	// {6557}
	public void test0152() {
		check("Integrate[Gamma[0, a*x], x]", 6557);
	}

	// {6558}
	public void test0153() {
		check("Integrate[Gamma[0, a*x]/x, x]", 6558);
	}

	// {6562}
	public void test0154() {
		check("Integrate[Gamma[0, a*x]/x^2, x]", 6562);
	}

	// {6562}
	public void test0155() {
		check("Integrate[Gamma[0, a*x]/x^3, x]", 6562);
	}

	// {6562}
	public void test0156() {
		check("Integrate[Gamma[0, a*x]/x^4, x]", 6562);
	}

	// {2194}
	public void test0157() {
		check("Integrate[E^(-(a*x)), x]", 2194);
	}

	// {2178}
	public void test0158() {
		check("Integrate[1/(E^(a*x)*x), x]", 2178);
	}

	// {6562}
	public void test0159() {
		check("Integrate[x^100*Gamma[2, a*x], x]", 6562);
	}

	// {6562}
	public void test0160() {
		check("Integrate[x^2*Gamma[2, a*x], x]", 6562);
	}

	// {6562}
	public void test0161() {
		check("Integrate[x*Gamma[2, a*x], x]", 6562);
	}

	// {6557}
	public void test0162() {
		check("Integrate[Gamma[2, a*x], x]", 6557);
	}

	// {6562}
	public void test0163() {
		check("Integrate[Gamma[2, a*x]/x^2, x]", 6562);
	}

	// {6562}
	public void test0164() {
		check("Integrate[Gamma[2, a*x]/x^3, x]", 6562);
	}

	// {6562}
	public void test0165() {
		check("Integrate[Gamma[2, a*x]/x^4, x]", 6562);
	}

	// {6562}
	public void test0166() {
		check("Integrate[x^100*Gamma[3, a*x], x]", 6562);
	}

	// {6562}
	public void test0167() {
		check("Integrate[x^2*Gamma[3, a*x], x]", 6562);
	}

	// {6562}
	public void test0168() {
		check("Integrate[x*Gamma[3, a*x], x]", 6562);
	}

	// {6557}
	public void test0169() {
		check("Integrate[Gamma[3, a*x], x]", 6557);
	}

	// {6562}
	public void test0170() {
		check("Integrate[Gamma[3, a*x]/x^2, x]", 6562);
	}

	// {6562}
	public void test0171() {
		check("Integrate[Gamma[3, a*x]/x^3, x]", 6562);
	}

	// {6562}
	public void test0172() {
		check("Integrate[Gamma[3, a*x]/x^4, x]", 6562);
	}

	// {6562}
	public void test0173() {
		check("Integrate[x^100*Gamma[-1, a*x], x]", 6562);
	}

	// {6562}
	public void test0174() {
		check("Integrate[x^3*Gamma[-1, a*x], x]", 6562);
	}

	// {6562}
	public void test0175() {
		check("Integrate[x^2*Gamma[-1, a*x], x]", 6562);
	}

	// {6562}
	public void test0176() {
		check("Integrate[x*Gamma[-1, a*x], x]", 6562);
	}

	// {6557}
	public void test0177() {
		check("Integrate[Gamma[-1, a*x], x]", 6557);
	}

	// {6562}
	public void test0178() {
		check("Integrate[Gamma[-1, a*x]/x^2, x]", 6562);
	}

	// {6562}
	public void test0179() {
		check("Integrate[Gamma[-1, a*x]/x^3, x]", 6562);
	}

	// {6562}
	public void test0180() {
		check("Integrate[Gamma[-1, a*x]/x^4, x]", 6562);
	}

	// {6562}
	public void test0181() {
		check("Integrate[x^100*Gamma[-2, a*x], x]", 6562);
	}

	// {6562}
	public void test0182() {
		check("Integrate[x^3*Gamma[-2, a*x], x]", 6562);
	}

	// {6562}
	public void test0183() {
		check("Integrate[x^2*Gamma[-2, a*x], x]", 6562);
	}

	// {6562}
	public void test0184() {
		check("Integrate[x*Gamma[-2, a*x], x]", 6562);
	}

	// {6557}
	public void test0185() {
		check("Integrate[Gamma[-2, a*x], x]", 6557);
	}

	// {6562}
	public void test0186() {
		check("Integrate[Gamma[-2, a*x]/x^2, x]", 6562);
	}

	// {6562}
	public void test0187() {
		check("Integrate[Gamma[-2, a*x]/x^3, x]", 6562);
	}

	// {6562}
	public void test0188() {
		check("Integrate[Gamma[-2, a*x]/x^4, x]", 6562);
	}

	// {6562}
	public void test0189() {
		check("Integrate[x^100*Gamma[-3, a*x], x]", 6562);
	}

	// {6562}
	public void test0190() {
		check("Integrate[x^3*Gamma[-3, a*x], x]", 6562);
	}

	// {6562}
	public void test0191() {
		check("Integrate[x^2*Gamma[-3, a*x], x]", 6562);
	}

	// {6562}
	public void test0192() {
		check("Integrate[x*Gamma[-3, a*x], x]", 6562);
	}

	// {6557}
	public void test0193() {
		check("Integrate[Gamma[-3, a*x], x]", 6557);
	}

	// {6562}
	public void test0194() {
		check("Integrate[Gamma[-3, a*x]/x^2, x]", 6562);
	}

	// {6562}
	public void test0195() {
		check("Integrate[Gamma[-3, a*x]/x^3, x]", 6562);
	}

	// {6562}
	public void test0196() {
		check("Integrate[Gamma[-3, a*x]/x^4, x]", 6562);
	}

	// {6562}
	public void test0197() {
		check("Integrate[x^100*Gamma[1/2, a*x], x]", 6562);
	}

	// {6562}
	public void test0198() {
		check("Integrate[x^2*Gamma[1/2, a*x], x]", 6562);
	}

	// {6562}
	public void test0199() {
		check("Integrate[x*Gamma[1/2, a*x], x]", 6562);
	}

	// {6557}
	public void test0200() {
		check("Integrate[Gamma[1/2, a*x], x]", 6557);
	}

	// {6561}
	public void test0201() {
		check("Integrate[Gamma[1/2, a*x]/x, x]", 6561);
	}

	// {6562}
	public void test0202() {
		check("Integrate[Gamma[1/2, a*x]/x^2, x]", 6562);
	}

	// {6562}
	public void test0203() {
		check("Integrate[Gamma[1/2, a*x]/x^3, x]", 6562);
	}

	// {6562}
	public void test0204() {
		check("Integrate[Gamma[1/2, a*x]/x^4, x]", 6562);
	}

	// {6562}
	public void test0205() {
		check("Integrate[x^100*Gamma[3/2, a*x], x]", 6562);
	}

	// {6562}
	public void test0206() {
		check("Integrate[x^2*Gamma[3/2, a*x], x]", 6562);
	}

	// {6562}
	public void test0207() {
		check("Integrate[x*Gamma[3/2, a*x], x]", 6562);
	}

	// {6557}
	public void test0208() {
		check("Integrate[Gamma[3/2, a*x], x]", 6557);
	}

	// {6561}
	public void test0209() {
		check("Integrate[Gamma[3/2, a*x]/x, x]", 6561);
	}

	// {6562}
	public void test0210() {
		check("Integrate[Gamma[3/2, a*x]/x^2, x]", 6562);
	}

	// {6562}
	public void test0211() {
		check("Integrate[Gamma[3/2, a*x]/x^3, x]", 6562);
	}

	// {6562}
	public void test0212() {
		check("Integrate[Gamma[3/2, a*x]/x^4, x]", 6562);
	}

	// {6562}
	public void test0213() {
		check("Integrate[(d*x)^m*Gamma[3, b*x], x]", 6562);
	}

	// {6562}
	public void test0214() {
		check("Integrate[(d*x)^m*Gamma[2, b*x], x]", 6562);
	}

	// {2181}
	public void test0215() {
		check("Integrate[(d*x)^m/E^(b*x), x]", 2181);
	}

	// {6562}
	public void test0216() {
		check("Integrate[(d*x)^m*Gamma[0, b*x], x]", 6562);
	}

	// {6562}
	public void test0217() {
		check("Integrate[(d*x)^m*Gamma[-1, b*x], x]", 6562);
	}

	// {6562}
	public void test0218() {
		check("Integrate[(d*x)^m*Gamma[-2, b*x], x]", 6562);
	}

	// {6562}
	public void test0219() {
		check("Integrate[x^m*Gamma[n, x], x]", 6562);
	}

	// {6562}
	public void test0220() {
		check("Integrate[x^m*Gamma[n, b*x], x]", 6562);
	}

	// {6562}
	public void test0221() {
		check("Integrate[(d*x)^m*Gamma[n, x], x]", 6562);
	}

	// {6562}
	public void test0222() {
		check("Integrate[(d*x)^m*Gamma[n, b*x], x]", 6562);
	}

	// {6562}
	public void test0223() {
		check("Integrate[x^100*Gamma[n, a*x], x]", 6562);
	}

	// {6562}
	public void test0224() {
		check("Integrate[x^2*Gamma[n, a*x], x]", 6562);
	}

	// {6562}
	public void test0225() {
		check("Integrate[x*Gamma[n, a*x], x]", 6562);
	}

	// {6557}
	public void test0226() {
		check("Integrate[Gamma[n, a*x], x]", 6557);
	}

	// {6561}
	public void test0227() {
		check("Integrate[Gamma[n, a*x]/x, x]", 6561);
	}

	// {6562}
	public void test0228() {
		check("Integrate[Gamma[n, a*x]/x^2, x]", 6562);
	}

	// {6562}
	public void test0229() {
		check("Integrate[Gamma[n, a*x]/x^3, x]", 6562);
	}

	// {6562}
	public void test0230() {
		check("Integrate[Gamma[n, a*x]/x^4, x]", 6562);
	}

	// {6562}
	public void test0231() {
		check("Integrate[x^100*Gamma[n, 2*x], x]", 6562);
	}

	// {6562}
	public void test0232() {
		check("Integrate[x^2*Gamma[n, 2*x], x]", 6562);
	}

	// {6562}
	public void test0233() {
		check("Integrate[x*Gamma[n, 2*x], x]", 6562);
	}

	// {6557}
	public void test0234() {
		check("Integrate[Gamma[n, 2*x], x]", 6557);
	}

	// {6561}
	public void test0235() {
		check("Integrate[Gamma[n, 2*x]/x, x]", 6561);
	}

	// {6562}
	public void test0236() {
		check("Integrate[Gamma[n, 2*x]/x^2, x]", 6562);
	}

	// {6562}
	public void test0237() {
		check("Integrate[Gamma[n, 2*x]/x^3, x]", 6562);
	}

	// {6562}
	public void test0238() {
		check("Integrate[Gamma[n, 2*x]/x^4, x]", 6562);
	}

	// {6557}
	public void test0239() {
		check("Integrate[Gamma[0, a + b*x], x]", 6557);
	}

	// {2194}
	public void test0240() {
		check("Integrate[E^(-a - b*x), x]", 2194);
	}

	// {2178}
	public void test0241() {
		check("Integrate[E^(-a - b*x)/(c + d*x), x]", 2178);
	}

	// {6557}
	public void test0242() {
		check("Integrate[Gamma[2, a + b*x], x]", 6557);
	}

	// {6557}
	public void test0243() {
		check("Integrate[Gamma[3, a + b*x], x]", 6557);
	}

	// {6557}
	public void test0244() {
		check("Integrate[Gamma[-1, a + b*x], x]", 6557);
	}

	// {6557}
	public void test0245() {
		check("Integrate[Gamma[-2, a + b*x], x]", 6557);
	}

	// {6557}
	public void test0246() {
		check("Integrate[Gamma[-3, a + b*x], x]", 6557);
	}

	// {2181}
	public void test0247() {
		check("Integrate[E^(-a - b*x)*(c + d*x)^m, x]", 2181);
	}

	// {6557}
	public void test0248() {
		check("Integrate[Gamma[n, a + b*x], x]", 6557);
	}

	// {6567}
	public void test0249() {
		check("Integrate[LogGamma[a + b*x], x]", 6567);
	}

	// {6570}
	public void test0250() {
		check("Integrate[PolyGamma[n, a + b*x], x]", 6570);
	}

	// {6572}
	public void test0251() {
		check("Integrate[PolyGamma[n, a + b*x]/(c + d*x)^2, x]", 6572);
	}

	// {6571}
	public void test0252() {
		check("Integrate[Sqrt[c + d*x]*PolyGamma[n, a + b*x], x]", 6571);
	}

	// {6572}
	public void test0253() {
		check("Integrate[PolyGamma[n, a + b*x]/(c + d*x)^(3/2), x]", 6572);
	}

	// {6574}
	public void test0254() {
		check("Integrate[Gamma[a + b*x]^n*PolyGamma[0, a + b*x], x]", 6574);
	}

	// {6575}
	public void test0255() {
		check("Integrate[(a + b*x)!^n*PolyGamma[0, 1 + a + b*x], x]", 6575);
	}

	// {6583}
	public void test0256() {
		check("Integrate[Zeta[2, a + b*x]/x, x]", 6583);
	}

	// {6582}
	public void test0257() {
		check("Integrate[Zeta[s, a + b*x], x]", 6582);
	}

	// {6585}
	public void test0258() {
		check("Integrate[Zeta[s, a + b*x]/x^2, x]", 6585);
	}

	// {6589}
	public void test0259() {
		check("Integrate[PolyLog[2, a*x]/x, x]", 6589);
	}

	// {6589}
	public void test0260() {
		check("Integrate[PolyLog[3, a*x]/x, x]", 6589);
	}

	// {6589}
	public void test0261() {
		check("Integrate[PolyLog[2, a*x^2]/x, x]", 6589);
	}

	// {6589}
	public void test0262() {
		check("Integrate[PolyLog[3, a*x^2]/x, x]", 6589);
	}

	// {6589}
	public void test0263() {
		check("Integrate[PolyLog[2, a*x^q]/x, x]", 6589);
	}

	// {6589}
	public void test0264() {
		check("Integrate[PolyLog[3, a*x^q]/x, x]", 6589);
	}

	// {6586}
	public void test0265() {
		check("Integrate[PolyLog[1/2, a*x], x]", 6586);
	}

	// {6587}
	public void test0266() {
		check("Integrate[PolyLog[-3/2, a*x], x]", 6587);
	}

	// {6589}
	public void test0267() {
		check("Integrate[PolyLog[n, a*x]/x, x]", 6589);
	}

	// {6589}
	public void test0268() {
		check("Integrate[PolyLog[n, a*x^q]/x, x]", 6589);
	}

	// {6741}
	public void test0269() {
		check("Integrate[PolyLog[3, c*(a + b*x)]/x, x]", 6741);
	}

	// {6610}
	public void test0270() {
		check("Integrate[PolyLog[n, e*((a + b*x)/(c + d*x))^n]/((a + b*x)*(c + d*x)), x]", 6610);
	}

	// {6610}
	public void test0271() {
		check("Integrate[PolyLog[3, e*((a + b*x)/(c + d*x))^n]/((a + b*x)*(c + d*x)), x]", 6610);
	}

	// {6610}
	public void test0272() {
		check("Integrate[PolyLog[2, e*((a + b*x)/(c + d*x))^n]/((a + b*x)*(c + d*x)), x]", 6610);
	}

	// {2518}
	public void test0273() {
		check("Integrate[-(Log[1 - e*((a + b*x)/(c + d*x))^n]/((a + b*x)*(c + d*x))), x]", 2518);
	}

	// {6741}
	public void test0274() {
		check("Integrate[PolyLog[n, d*(F^(c*(a + b*x)))^p]/x, x]", 6741);
	}

	// {6601}
	public void test0275() {
		check("Integrate[(Log[1 - c*x]*PolyLog[2, c*x])/x, x]", 6601);
	}

	// {6720}
	public void test0276() {
		check("Integrate[1/(x*Sqrt[c*ProductLog[a + b*x]]), x]", 6720);
	}

	// {6720}
	public void test0277() {
		check("Integrate[1/(x^2*Sqrt[c*ProductLog[a + b*x]]), x]", 6720);
	}

	// {6720}
	public void test0278() {
		check("Integrate[1/(x*Sqrt[-(c*ProductLog[a + b*x])]), x]", 6720);
	}

	// {6720}
	public void test0279() {
		check("Integrate[1/(x^2*Sqrt[-(c*ProductLog[a + b*x])]), x]", 6720);
	}

	// {6720}
	public void test0280() {
		check("Integrate[Sqrt[c*ProductLog[a + b*x]]/x, x]", 6720);
	}

	// {6720}
	public void test0281() {
		check("Integrate[Sqrt[c*ProductLog[a + b*x]]/x^2, x]", 6720);
	}

	// {6622}
	public void test0282() {
		check("Integrate[(d + d*ProductLog[a + b*x])^(-1), x]", 6622);
	}

	// {6720}
	public void test0283() {
		check("Integrate[Sqrt[c*ProductLog[a*x^2]]/x^2, x]", 6720);
	}

	// {6720}
	public void test0284() {
		check("Integrate[Sqrt[c*ProductLog[a*x^2]]/x^4, x]", 6720);
	}

	// {6720}
	public void test0285() {
		check("Integrate[Sqrt[c*ProductLog[a*x^2]]/x^6, x]", 6720);
	}

	// {6720}
	public void test0286() {
		check("Integrate[1/Sqrt[c*ProductLog[a*x^2]], x]", 6720);
	}

	// {6720}
	public void test0287() {
		check("Integrate[1/(x^2*Sqrt[c*ProductLog[a*x^2]]), x]", 6720);
	}

	// {6720}
	public void test0288() {
		check("Integrate[1/(x^4*Sqrt[c*ProductLog[a*x^2]]), x]", 6720);
	}

	// {6720}
	public void test0289() {
		check("Integrate[1/(x^6*Sqrt[c*ProductLog[a*x^2]]), x]", 6720);
	}

	// {6720}
	public void test0290() {
		check("Integrate[x^2*(c*ProductLog[a*x^2])^p, x]", 6720);
	}

	// {6720}
	public void test0291() {
		check("Integrate[(c*ProductLog[a*x^2])^p/x^2, x]", 6720);
	}

	// {6622}
	public void test0292() {
		check("Integrate[(1 + ProductLog[a*x])^(-1), x]", 6622);
	}

	// {6641}
	public void test0293() {
		check("Integrate[1/(x*(1 + ProductLog[a*x])), x]", 6641);
	}

	// {6644}
	public void test0294() {
		check("Integrate[1/(x*(1 + ProductLog[a*x^2])), x]", 6644);
	}

	// {6644}
	public void test0295() {
		check("Integrate[1/(x*(1 + ProductLog[a/x])), x]", 6644);
	}

	// {6644}
	public void test0296() {
		check("Integrate[1/(x*(1 + ProductLog[a/x^2])), x]", 6644);
	}

	// {6645}
	public void test0297() {
		check("Integrate[x^4/(1 + ProductLog[a/x^2]), x]", 6645);
	}

	// {6645}
	public void test0298() {
		check("Integrate[x^2/(1 + ProductLog[a/x^2]), x]", 6645);
	}

	// {6632}
	public void test0299() {
		check("Integrate[(1 + ProductLog[a/x^2])^(-1), x]", 6632);
	}

	// {6645}
	public void test0300() {
		check("Integrate[1/(x^2*(1 + ProductLog[a/x^2])), x]", 6645);
	}

	// {6645}
	public void test0301() {
		check("Integrate[1/(x^4*(1 + ProductLog[a/x^2])), x]", 6645);
	}

	// {6643}
	public void test0302() {
		check("Integrate[x^m/(d + d*ProductLog[a*x]), x]", 6643);
	}

	// {6633}
	public void test0303() {
		check("Integrate[ProductLog[a/x^(1/4)]^5/(1 + ProductLog[a/x^(1/4)]), x]", 6633);
	}

	// {6633}
	public void test0304() {
		check("Integrate[ProductLog[a/x^(1/3)]^4/(1 + ProductLog[a/x^(1/3)]), x]", 6633);
	}

	// {6633}
	public void test0305() {
		check("Integrate[ProductLog[a/Sqrt[x]]^3/(1 + ProductLog[a/Sqrt[x]]), x]", 6633);
	}

	// {6633}
	public void test0306() {
		check("Integrate[ProductLog[a/x]^2/(1 + ProductLog[a/x]), x]", 6633);
	}

	// {6633}
	public void test0307() {
		check("Integrate[1/(ProductLog[a*Sqrt[x]]*(1 + ProductLog[a*Sqrt[x]])), x]", 6633);
	}

	// {6633}
	public void test0308() {
		check("Integrate[1/(ProductLog[a*x^(1/3)]^2*(1 + ProductLog[a*x^(1/3)])), x]", 6633);
	}

	// {6633}
	public void test0309() {
		check("Integrate[1/(ProductLog[a*x^(1/4)]^3*(1 + ProductLog[a*x^(1/4)])), x]", 6633);
	}

	// {6634}
	public void test0310() {
		check("Integrate[ProductLog[a/x^(1/4)]^4/(1 + ProductLog[a/x^(1/4)]), x]", 6634);
	}

	// {6634}
	public void test0311() {
		check("Integrate[ProductLog[a/x^(1/3)]^3/(1 + ProductLog[a/x^(1/3)]), x]", 6634);
	}

	// {6634}
	public void test0312() {
		check("Integrate[ProductLog[a/Sqrt[x]]^2/(1 + ProductLog[a/Sqrt[x]]), x]", 6634);
	}

	// {6634}
	public void test0313() {
		check("Integrate[ProductLog[a/x]/(1 + ProductLog[a/x]), x]", 6634);
	}

	// {6625}
	public void test0314() {
		check("Integrate[1/(ProductLog[a*x]*(1 + ProductLog[a*x])), x]", 6625);
	}

	// {6634}
	public void test0315() {
		check("Integrate[1/(ProductLog[a*Sqrt[x]]^2*(1 + ProductLog[a*Sqrt[x]])), x]", 6634);
	}

	// {6634}
	public void test0316() {
		check("Integrate[1/(ProductLog[a*x^(1/3)]^3*(1 + ProductLog[a*x^(1/3)])), x]", 6634);
	}

	// {6634}
	public void test0317() {
		check("Integrate[1/(ProductLog[a*x^(1/4)]^4*(1 + ProductLog[a*x^(1/4)])), x]", 6634);
	}

	// {6633}
	public void test0318() {
		check("Integrate[ProductLog[a*x^n]^(1 - n^(-1))/(1 + ProductLog[a*x^n]), x]", 6633);
	}

	// {6633}
	public void test0319() {
		check("Integrate[ProductLog[a*x^(1 - p)^(-1)]^p/(1 + ProductLog[a*x^(1 - p)^(-1)]), x]", 6633);
	}
}
