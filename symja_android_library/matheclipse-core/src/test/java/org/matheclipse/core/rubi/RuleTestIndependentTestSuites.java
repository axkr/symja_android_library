package org.matheclipse.core.rubi;

public class RuleTestIndependentTestSuites extends AbstractRubiTestCase {

	public RuleTestIndependentTestSuites(String name) {
		super(name, false);
	}

	// {32}
	public void test0001() {
		check("Integrate[Sqrt[1 + 2*x], x]", 32);
	}

	// {629}
	public void test0002() {
		check("Integrate[(1 + x)/(2 + 2*x + x^2)^3, x]", 629);
	}

	// {261}
	public void test0003() {
		check("Integrate[x^5/Sqrt[1 - x^6], x]", 261);
	}

	// {191}
	public void test0004() {
		check("Integrate[(1 + x^2)^(-3/2), x]", 191);
	}

	// {261}
	public void test0005() {
		check("Integrate[x^2*(27 + 8*x^3)^(2/3), x]", 261);
	}

	// {3145}
	public void test0006() {
		check("Integrate[(Cos[x] + Sin[x])/(-Cos[x] + Sin[x])^(1/3), x]", 3145);
	}

	// {6686}
	public void test0007() {
		check("Integrate[x/(Sqrt[1 + x^2]*Sqrt[1 + Sqrt[1 + x^2]]), x]", 6686);
	}

	// {261}
	public void test0008() {
		check("Integrate[x*Sqrt[1 + x^2], x]", 261);
	}

	// {261}
	public void test0009() {
		check("Integrate[x*(-1 + x^2)^9, x]", 261);
	}

	// {37}
	public void test0010() {
		check("Integrate[(3 + 2*x)/(7 + 6*x)^3, x]", 37);
	}

	// {261}
	public void test0011() {
		check("Integrate[x^4*(1 + x^5)^5, x]", 261);
	}

	// {3441}
	public void test0012() {
		check("Integrate[x*Cos[x^2]*Sin[x^2], x]", 3441);
	}

	// {31}
	public void test0013() {
		check("Integrate[(2 + 3*x)^(-1), x]", 31);
	}

	// {2304}
	public void test0014() {
		check("Integrate[x*Log[x], x]", 2304);
	}

	// {31}
	public void test0015() {
		check("Integrate[(1 + t)^(-1), t]", 31);
	}

	// {3475}
	public void test0016() {
		check("Integrate[Cot[x], x]", 3475);
	}

	// {2304}
	public void test0017() {
		check("Integrate[x^n*Log[a*x], x]", 2304);
	}

	// {2209}
	public void test0018() {
		check("Integrate[E^x^3*x^2, x]", 2209);
	}

	// {2209}
	public void test0019() {
		check("Integrate[2^Sqrt[x]/Sqrt[x], x]", 2209);
	}

	// {4432}
	public void test0020() {
		check("Integrate[E^x*Sin[x], x]", 4432);
	}

	// {4433}
	public void test0021() {
		check("Integrate[E^x*Cos[x], x]", 4433);
	}

	// {4433}
	public void test0022() {
		check("Integrate[E^(a*x)*Cos[b*x], x]", 4433);
	}

	// {4432}
	public void test0023() {
		check("Integrate[E^(a*x)*Sin[b*x], x]", 4432);
	}

	// {203}
	public void test0024() {
		check("Integrate[(a^2 + x^2)^(-1), x]", 203);
	}

	// {205}
	public void test0025() {
		check("Integrate[(a + b*x^2)^(-1), x]", 205);
	}

	// {6686}
	public void test0026() {
		check("Integrate[ArcTan[Sqrt[x]]/(Sqrt[x]*(1 + x)), x]", 6686);
	}

	// {5077}
	public void test0027() {
		check("Integrate[(E^ArcTan[x]*x)/(1 + x^2)^(3/2), x]", 5077);
	}

	// {5069}
	public void test0028() {
		check("Integrate[E^ArcTan[x]/(1 + x^2)^(3/2), x]", 5069);
	}

	// {1588}
	public void test0029() {
		check("Integrate[(-1 + 4*x^5)/(1 + x + x^5)^2, x]", 1588);
	}

	// {2657}
	public void test0030() {
		check("Integrate[(1 + Cos[x]/2)^(-1), x]", 2657);
	}

	// {3075}
	public void test0031() {
		check("Integrate[(b*Cos[x] + a*Sin[x])^(-2), x]", 3075);
	}

	// {261}
	public void test0032() {
		check("Integrate[x/Sqrt[3 - x^2], x]", 261);
	}

	// {2178}
	public void test0033() {
		check("Integrate[E^t/t, t]", 2178);
	}

	// {2178}
	public void test0034() {
		check("Integrate[E^(a*t)/t, t]", 2178);
	}

	// {2178}
	public void test0035() {
		check("Integrate[1/(E^t*(-1 - a + t)), t]", 2178);
	}

	// {3133}
	public void test0036() {
		check("Integrate[(b1*Cos[x] + a1*Sin[x])/(b*Cos[x] + a*Sin[x]), x]", 3133);
	}

	// {2298}
	public void test0037() {
		check("Integrate[Log[t]^(-1), t]", 2298);
	}

	// {2178}
	public void test0038() {
		check("Integrate[E^(2*t)/(-1 + t), t]", 2178);
	}

	// {218}
	public void test0039() {
		check("Integrate[1/Sqrt[1 + t^3], t]", 218);
	}

	// {3114}
	public void test0040() {
		check("Integrate[(Sqrt[2] + Cos[z] + Sin[z])^(-1), z]", 3114);
	}

	// {203}
	public void test0041() {
		check("Integrate[(1 + x^2)^(-1), x]", 203);
	}

	// {3299}
	public void test0042() {
		check("Integrate[Sin[x]/x, x]", 3299);
	}

	// {2095}
	public void test0043() {
		check("Integrate[(6 - 3*x^2 + x^4)/(4 + 5*x^2 - 5*x^4 + x^6), x]", 2095);
	}

	// {-1}
	public void test0044() {
		check("Integrate[1 + x + x^2, x]", -1);
	}

	// {29}
	public void test0045() {
		check("Integrate[x^(-1), x]", 29);
	}

	// {32}
	public void test0046() {
		check("Integrate[(a + b*x)^p, x]", 32);
	}

	// {31}
	public void test0047() {
		check("Integrate[(a + b*x)^(-1), x]", 31);
	}

	// {32}
	public void test0048() {
		check("Integrate[(a + b*x)^(-2), x]", 32);
	}

	// {203}
	public void test0049() {
		check("Integrate[(c^2 + x^2)^(-1), x]", 203);
	}

	// {206}
	public void test0050() {
		check("Integrate[(c^2 - x^2)^(-1), x]", 206);
	}

	// {2295}
	public void test0051() {
		check("Integrate[Log[x], x]", 2295);
	}

	// {2304}
	public void test0052() {
		check("Integrate[x*Log[x], x]", 2304);
	}

	// {2304}
	public void test0053() {
		check("Integrate[x^2*Log[x], x]", 2304);
	}

	// {2304}
	public void test0054() {
		check("Integrate[x^p*Log[x], x]", 2304);
	}

	// {2298}
	public void test0055() {
		check("Integrate[Log[x]^(-1), x]", 2298);
	}

	// {2638}
	public void test0056() {
		check("Integrate[Sin[x], x]", 2638);
	}

	// {2637}
	public void test0057() {
		check("Integrate[Cos[x], x]", 2637);
	}

	// {3475}
	public void test0058() {
		check("Integrate[Tan[x], x]", 3475);
	}

	// {3475}
	public void test0059() {
		check("Integrate[Cot[x], x]", 3475);
	}

	// {3770}
	public void test0060() {
		check("Integrate[Sec[x], x]", 3770);
	}

	// {3770}
	public void test0061() {
		check("Integrate[Csc[x], x]", 3770);
	}

	// {2643}
	public void test0062() {
		check("Integrate[Sin[x]^p, x]", 2643);
	}

	// {4282}
	public void test0063() {
		check("Integrate[Sin[x]*Sin[2*x], x]", 4282);
	}

	// {3299}
	public void test0064() {
		check("Integrate[Sin[x]/x, x]", 3299);
	}

	// {3302}
	public void test0065() {
		check("Integrate[Cos[x]/x, x]", 3302);
	}

	// {2638}
	public void test0066() {
		check("Integrate[Sin[a + b*x], x]", 2638);
	}

	// {2637}
	public void test0067() {
		check("Integrate[Cos[a + b*x], x]", 2637);
	}

	// {3475}
	public void test0068() {
		check("Integrate[Tan[a + b*x], x]", 3475);
	}

	// {3475}
	public void test0069() {
		check("Integrate[Cot[a + b*x], x]", 3475);
	}

	// {3770}
	public void test0070() {
		check("Integrate[Csc[a + b*x], x]", 3770);
	}

	// {3770}
	public void test0071() {
		check("Integrate[Sec[a + b*x], x]", 3770);
	}

	// {2648}
	public void test0072() {
		check("Integrate[(1 + Cos[x])^(-1), x]", 2648);
	}

	// {2648}
	public void test0073() {
		check("Integrate[(1 - Cos[x])^(-1), x]", 2648);
	}

	// {2648}
	public void test0074() {
		check("Integrate[(1 + Sin[x])^(-1), x]", 2648);
	}

	// {2648}
	public void test0075() {
		check("Integrate[(1 - Sin[x])^(-1), x]", 2648);
	}

	// {4283}
	public void test0076() {
		check("Integrate[Cos[x]*Cos[2*x], x]", 4283);
	}

	// {4284}
	public void test0077() {
		check("Integrate[Cos[3*x]*Sin[2*x], x]", 4284);
	}

	// {4432}
	public void test0078() {
		check("Integrate[d^x*Sin[x], x]", 4432);
	}

	// {4433}
	public void test0079() {
		check("Integrate[d^x*Cos[x], x]", 4433);
	}

	// {4475}
	public void test0080() {
		check("Integrate[Sin[Log[x]], x]", 4475);
	}

	// {4476}
	public void test0081() {
		check("Integrate[Cos[Log[x]], x]", 4476);
	}

	// {2194}
	public void test0082() {
		check("Integrate[E^x, x]", 2194);
	}

	// {2194}
	public void test0083() {
		check("Integrate[a^x, x]", 2194);
	}

	// {2194}
	public void test0084() {
		check("Integrate[E^(a*x), x]", 2194);
	}

	// {2178}
	public void test0085() {
		check("Integrate[E^(a*x)/x, x]", 2178);
	}

	// {2197}
	public void test0086() {
		check("Integrate[(E^(a*x)*x)/(1 + a*x)^2, x]", 2197);
	}

	// {2209}
	public void test0087() {
		check("Integrate[k^x^2*x, x]", 2209);
	}

	// {2204}
	public void test0088() {
		check("Integrate[E^x^2, x]", 2204);
	}

	// {2209}
	public void test0089() {
		check("Integrate[E^x^2*x, x]", 2209);
	}

	// {2282}
	public void test0090() {
		check("Integrate[E^E^E^E^x, x]", 2282);
	}

	// {-1}
	public void test0091() {
		check("Integrate[2*x + Sqrt[2]*x^2, x]", -1);
	}

	// {32}
	public void test0092() {
		check("Integrate[Sqrt[a + b*x], x]", 32);
	}

	// {32}
	public void test0093() {
		check("Integrate[1/Sqrt[a + b*x], x]", 32);
	}

	// {32}
	public void test0094() {
		check("Integrate[(a + b*x)^(p/2), x]", 32);
	}

	// {261}
	public void test0095() {
		check("Integrate[x/(1 - x^2)^(9/8), x]", 261);
	}

	// {803}
	public void test0096() {
		check("Integrate[(1 + x)/((1 - x)^2*Sqrt[1 + x^2]), x]", 803);
	}

	// {215}
	public void test0097() {
		check("Integrate[1/Sqrt[1 + x^2], x]", 215);
	}

	// {261}
	public void test0098() {
		check("Integrate[r/Sqrt[-alpha^2 + 2*e*r^2], r]", 261);
	}

	// {261}
	public void test0099() {
		check("Integrate[r/Sqrt[-alpha^2 - epsilon^2 + 2*e*r^2], r]", 261);
	}

	// {2304}
	public void test0100() {
		check("Integrate[Log[x^2]/x^3, x]", 2304);
	}

	// {2201}
	public void test0101() {
		check("Integrate[(-1 + (1 - x)*Log[x])/(E^x*Log[x]^2), x]", 2201);
	}

	// {3475}
	public void test0102() {
		check("Integrate[Tanh[2*x], x]", 3475);
	}

	// {6684}
	public void test0103() {
		check("Integrate[(-1 + I*eps*Sinh[x])/(I*a - x + I*eps*Cosh[x]), x]", 6684);
	}

	// {2646}
	public void test0104() {
		check("Integrate[Sqrt[1 + Sin[x]], x]", 2646);
	}

	// {2646}
	public void test0105() {
		check("Integrate[Sqrt[1 - Sin[x]], x]", 2646);
	}

	// {2646}
	public void test0106() {
		check("Integrate[Sqrt[1 + Cos[x]], x]", 2646);
	}

	// {2646}
	public void test0107() {
		check("Integrate[Sqrt[1 - Cos[x]], x]", 2646);
	}

	// {3475}
	public void test0108() {
		check("Integrate[Cot[x], x]", 3475);
	}

	// {3475}
	public void test0109() {
		check("Integrate[Tanh[x], x]", 3475);
	}

	// {3475}
	public void test0110() {
		check("Integrate[Coth[x], x]", 3475);
	}

	// {2194}
	public void test0111() {
		check("Integrate[b^x, x]", 2194);
	}

	// {32}
	public void test0112() {
		check("Integrate[(-3 + x)^(-4), x]", 32);
	}

	// {215}
	public void test0113() {
		check("Integrate[1/Sqrt[1 + x^2], x]", 215);
	}

	// {215}
	public void test0114() {
		check("Integrate[1/Sqrt[9 + 4*x^2], x]", 215);
	}

	// {215}
	public void test0115() {
		check("Integrate[1/Sqrt[4 + x^2], x]", 215);
	}

	// {6349}
	public void test0116() {
		check("Integrate[Erf[x], x]", 6349);
	}

	// {6349}
	public void test0117() {
		check("Integrate[Erf[a + x], x]", 6349);
	}

	// {6684}
	public void test0118() {
		check("Integrate[(x*(-Sqrt[-4 + x^2] + x^2*Sqrt[-4 + x^2] - 4*Sqrt[-1 + x^2] + x^2*Sqrt[-1 + x^2]))/((4 - 5*x^2 + x^4)*(1 + Sqrt[-4 + x^2] + Sqrt[-1 + x^2])), x]", 6684);
	}

	// {-1}
	public void test0119() {
		check("Integrate[Sqrt[9 - 4*Sqrt[2]]*x - Sqrt[2]*Sqrt[1 + 4*x + 2*x^2 + x^4], x]", -1);
	}

	// {2288}
	public void test0120() {
		check("Integrate[(E^(1 + Log[x]^(-1))*(-1 + Log[x]^2))/Log[x]^2, x]", 2288);
	}

	// {2637}
	public void test0121() {
		check("Integrate[Cos[x], x]", 2637);
	}

	// {2209}
	public void test0122() {
		check("Integrate[E^x^2*x, x]", 2209);
	}

	// {261}
	public void test0123() {
		check("Integrate[x*Sqrt[1 + x^2], x]", 261);
	}

	// {4432}
	public void test0124() {
		check("Integrate[E^x*Sin[x], x]", 4432);
	}

	// {3299}
	public void test0125() {
		check("Integrate[Sin[y]/y, y]", 3299);
	}

	// {2209}
	public void test0126() {
		check("Integrate[E^x^2*x, x]", 2209);
	}

	// {261}
	public void test0127() {
		check("Integrate[x*Sqrt[1 + x^2], x]", 261);
	}

	// {30}
	public void test0128() {
		check("Integrate[x^(3/2), x]", 30);
	}

	// {2637}
	public void test0129() {
		check("Integrate[Cos[3 + 2*x], x]", 2637);
	}

	// {2194}
	public void test0130() {
		check("Integrate[(10*E)^x, x]", 2194);
	}

	// {2648}
	public void test0131() {
		check("Integrate[(1 + Cos[x])^(-1), x]", 2648);
	}

	// {2197}
	public void test0132() {
		check("Integrate[(E^x*x)/(1 + x)^2, x]", 2197);
	}

	// {2204}
	public void test0133() {
		check("Integrate[E^x^2, x]", 2204);
	}

	// {2178}
	public void test0134() {
		check("Integrate[E^x/x, x]", 2178);
	}

	// {208}
	public void test0135() {
		check("Integrate[(A^4 - A^2*B^2 + (-A^2 + B^2)*x^2)^(-1), x]", 208);
	}

	// {2304}
	public void test0136() {
		check("Integrate[x*Log[x], x]", 2304);
	}

	// {2298}
	public void test0137() {
		check("Integrate[Log[x]^(-1), x]", 2298);
	}

	// {2194}
	public void test0138() {
		check("Integrate[E^(-1 - x), x]", 2194);
	}

	// {4432}
	public void test0139() {
		check("Integrate[E^x*Sin[x], x]", 4432);
	}

	// {29}
	public void test0140() {
		check("Integrate[x^(-1), x]", 29);
	}

	// {2648}
	public void test0141() {
		check("Integrate[(1 - Cos[x])^(-1), x]", 2648);
	}

	// {2304}
	public void test0142() {
		check("Integrate[x*Log[x], x]", 2304);
	}

	// {8}
	public void test0143() {
		check("Integrate[1/(r*Sqrt[-a^2 + 2*H*r^2]), x]", 8);
	}

	// {8}
	public void test0144() {
		check("Integrate[1/(r*Sqrt[-a^2 - e^2 + 2*H*r^2]), x]", 8);
	}

	// {8}
	public void test0145() {
		check("Integrate[1/(r*Sqrt[-a^2 + 2*H*r^2 - 2*K*r^4]), x]", 8);
	}

	// {8}
	public void test0146() {
		check("Integrate[1/(r*Sqrt[-a^2 - e^2 + 2*H*r^2 - 2*K*r^4]), x]", 8);
	}

	// {8}
	public void test0147() {
		check("Integrate[1/(r*Sqrt[-a^2 - 2*K*r + 2*H*r^2]), x]", 8);
	}

	// {8}
	public void test0148() {
		check("Integrate[1/(r*Sqrt[-a^2 - e^2 - 2*K*r + 2*H*r^2]), x]", 8);
	}

	// {8}
	public void test0149() {
		check("Integrate[r/Sqrt[-a^2 + 2*E*r^2], x]", 8);
	}

	// {8}
	public void test0150() {
		check("Integrate[r/Sqrt[-a^2 - e^2 + 2*E*r^2], x]", 8);
	}

	// {8}
	public void test0151() {
		check("Integrate[r/Sqrt[-a^2 + 2*E*r^2 - 2*K*r^4], x]", 8);
	}

	// {8}
	public void test0152() {
		check("Integrate[r/Sqrt[-a^2 - e^2 + 2*E*r^2 - 2*K*r^4], x]", 8);
	}

	// {8}
	public void test0153() {
		check("Integrate[r/Sqrt[-a^2 - e^2 - 2*K*r + 2*H*r^2], x]", 8);
	}

	// {30}
	public void test0154() {
		check("Integrate[x^n, x]", 30);
	}

	// {2194}
	public void test0155() {
		check("Integrate[E^x, x]", 2194);
	}

	// {29}
	public void test0156() {
		check("Integrate[x^(-1), x]", 29);
	}

	// {2194}
	public void test0157() {
		check("Integrate[a^x, x]", 2194);
	}

	// {2638}
	public void test0158() {
		check("Integrate[Sin[x], x]", 2638);
	}

	// {2637}
	public void test0159() {
		check("Integrate[Cos[x], x]", 2637);
	}

	// {2638}
	public void test0160() {
		check("Integrate[Sinh[x], x]", 2638);
	}

	// {2637}
	public void test0161() {
		check("Integrate[Cosh[x], x]", 2637);
	}

	// {3475}
	public void test0162() {
		check("Integrate[Tan[x], x]", 3475);
	}

	// {3475}
	public void test0163() {
		check("Integrate[Cot[x], x]", 3475);
	}

	// {2295}
	public void test0164() {
		check("Integrate[Log[x], x]", 2295);
	}

	// {4432}
	public void test0165() {
		check("Integrate[E^x*Sin[x], x]", 4432);
	}

	// {2304}
	public void test0166() {
		check("Integrate[x*Log[x], x]", 2304);
	}

	// {2304}
	public void test0167() {
		check("Integrate[t^2*Log[t], t]", 2304);
	}

	// {4432}
	public void test0168() {
		check("Integrate[E^(2*t)*Sin[3*t], t]", 4432);
	}

	// {4433}
	public void test0169() {
		check("Integrate[Cos[3*t]/E^t, t]", 4433);
	}

	// {2304}
	public void test0170() {
		check("Integrate[Sqrt[t]*Log[t], t]", 2304);
	}

	// {4284}
	public void test0171() {
		check("Integrate[Cos[5*x]*Sin[3*x], x]", 4284);
	}

	// {4282}
	public void test0172() {
		check("Integrate[Sin[2*x]*Sin[4*x], x]", 4282);
	}

	// {4476}
	public void test0173() {
		check("Integrate[Cos[Log[x]], x]", 4476);
	}

	// {2295}
	public void test0174() {
		check("Integrate[Log[Sqrt[x]], x]", 2295);
	}

	// {4475}
	public void test0175() {
		check("Integrate[Sin[Log[x]], x]", 4475);
	}

	// {2304}
	public void test0176() {
		check("Integrate[Sqrt[x]*Log[x], x]", 2304);
	}

	// {2644}
	public void test0177() {
		check("Integrate[(1 - Sin[2*x])^2, x]", 2644);
	}

	// {2648}
	public void test0178() {
		check("Integrate[(1 - Sin[x])^(-1), x]", 2648);
	}

	// {3770}
	public void test0179() {
		check("Integrate[Csc[x], x]", 3770);
	}

	// {4282}
	public void test0180() {
		check("Integrate[Sin[2*x]*Sin[5*x], x]", 4282);
	}

	// {4284}
	public void test0181() {
		check("Integrate[Cos[x]*Sin[3*x], x]", 4284);
	}

	// {4283}
	public void test0182() {
		check("Integrate[Cos[3*x]*Cos[4*x], x]", 4283);
	}

	// {4282}
	public void test0183() {
		check("Integrate[Sin[3*x]*Sin[6*x], x]", 4282);
	}

	// {264}
	public void test0184() {
		check("Integrate[1/(x^2*Sqrt[4 + x^2]), x]", 264);
	}

	// {261}
	public void test0185() {
		check("Integrate[x/Sqrt[4 + x^2], x]", 261);
	}

	// {264}
	public void test0186() {
		check("Integrate[1/(x^2*Sqrt[1 - x^2]), x]", 264);
	}

	// {261}
	public void test0187() {
		check("Integrate[x/Sqrt[1 - x^2], x]", 261);
	}

	// {261}
	public void test0188() {
		check("Integrate[x*Sqrt[4 - x^2], x]", 261);
	}

	// {215}
	public void test0189() {
		check("Integrate[1/Sqrt[9 + x^2], x]", 215);
	}

	// {264}
	public void test0190() {
		check("Integrate[Sqrt[-a^2 + x^2]/x^4, x]", 264);
	}

	// {264}
	public void test0191() {
		check("Integrate[1/(x^2*Sqrt[-9 + 16*x^2]), x]", 264);
	}

	// {261}
	public void test0192() {
		check("Integrate[x/(4 + x^2)^(5/2), x]", 261);
	}

	// {191}
	public void test0193() {
		check("Integrate[(-25 + 4*x^2)^(-3/2), x]", 191);
	}

	// {1587}
	public void test0194() {
		check("Integrate[(2*x + x^2)/(4 + 3*x^2 + x^3), x]", 1587);
	}

	// {1587}
	public void test0195() {
		check("Integrate[(-x + 2*x^3)/(1 - x^2 + x^4), x]", 1587);
	}

	// {260}
	public void test0196() {
		check("Integrate[x/(-1 + x^2), x]", 260);
	}

	// {2648}
	public void test0197() {
		check("Integrate[(1 - Cos[x])^(-1), x]", 2648);
	}

	// {3133}
	public void test0198() {
		check("Integrate[(-Cos[x] + Sin[x])/(Cos[x] + Sin[x]), x]", 3133);
	}

	// {261}
	public void test0199() {
		check("Integrate[x/Sqrt[1 - x^2], x]", 261);
	}

	// {2304}
	public void test0200() {
		check("Integrate[x^3*Log[x], x]", 2304);
	}

	// {1587}
	public void test0201() {
		check("Integrate[(1 + x + x^3)/(4*x + 2*x^2 + x^4), x]", 1587);
	}

	// {2638}
	public void test0202() {
		check("Integrate[Sin[Pi*x], x]", 2638);
	}

	// {4433}
	public void test0203() {
		check("Integrate[E^(3*x)*Cos[5*x], x]", 4433);
	}

	// {4283}
	public void test0204() {
		check("Integrate[Cos[3*x]*Cos[5*x], x]", 4283);
	}

	// {6686}
	public void test0205() {
		check("Integrate[Csc[x]*Log[Tan[x]]*Sec[x], x]", 6686);
	}

	// {-1}
	public void test0206() {
		check("Integrate[-2*x + x^2 + x^3, x]", -1);
	}

	// {216}
	public void test0207() {
		check("Integrate[1/Sqrt[16 - x^2], x]", 216);
	}

	// {5071}
	public void test0208() {
		check("Integrate[E^ArcTan[x]/(1 + x^2), x]", 5071);
	}

	// {2295}
	public void test0209() {
		check("Integrate[Log[x/2], x]", 2295);
	}

	// {261}
	public void test0210() {
		check("Integrate[x*(5 + x^2)^8, x]", 261);
	}

	// {4433}
	public void test0211() {
		check("Integrate[Cos[4*x]/E^(3*x), x]", 4433);
	}

	// {4433}
	public void test0212() {
		check("Integrate[E^x*Cos[4 + 3*x], x]", 4433);
	}

	// {261}
	public void test0213() {
		check("Integrate[x^2*(1 + x^3)^4, x]", 261);
	}

	// {208}
	public void test0214() {
		check("Integrate[(a^2 - b^2*x^2)^(-1), x]", 208);
	}

	// {205}
	public void test0215() {
		check("Integrate[(a^2 + b^2*x^2)^(-1), x]", 205);
	}

	// {3770}
	public void test0216() {
		check("Integrate[Sec[2*a*x], x]", 3770);
	}

	// {3770}
	public void test0217() {
		check("Integrate[-Sec[Pi/4 + 2*x], x]", 3770);
	}

	// {2648}
	public void test0218() {
		check("Integrate[(1 + Cos[x])^(-1), x]", 2648);
	}

	// {2648}
	public void test0219() {
		check("Integrate[(1 - Cos[x])^(-1), x]", 2648);
	}

	// {4282}
	public void test0220() {
		check("Integrate[Sin[x/4]*Sin[x], x]", 4282);
	}

	// {4283}
	public void test0221() {
		check("Integrate[Cos[3*x]*Cos[4*x], x]", 4283);
	}

	// {4884}
	public void test0222() {
		check("Integrate[ArcTan[x]^n/(1 + x^2), x]", 4884);
	}

	// {4642}
	public void test0223() {
		check("Integrate[1/(Sqrt[1 - x^2]*ArcCos[x]^3), x]", 4642);
	}

	// {2304}
	public void test0224() {
		check("Integrate[Log[x]/x^5, x]", 2304);
	}

	// {4432}
	public void test0225() {
		check("Integrate[Sin[x]/E^x, x]", 4432);
	}

	// {4432}
	public void test0226() {
		check("Integrate[E^(2*x)*Sin[3*x], x]", 4432);
	}

	// {4433}
	public void test0227() {
		check("Integrate[a^x*Cos[x], x]", 4433);
	}

	// {4476}
	public void test0228() {
		check("Integrate[Cos[Log[x]], x]", 4476);
	}

	// {261}
	public void test0229() {
		check("Integrate[x^(-1 + k)*(a + b*x^k)^n, x]", 261);
	}

	// {260}
	public void test0230() {
		check("Integrate[x^2/(a^3 + x^3), x]", 260);
	}

	// {364}
	public void test0231() {
		check("Integrate[1/(x^m*(a^3 + x^3)), x]", 364);
	}

	// {364}
	public void test0232() {
		check("Integrate[1/(x^m*(a^4 - x^4)), x]", 364);
	}

	// {260}
	public void test0233() {
		check("Integrate[x^4/(a^5 + x^5), x]", 260);
	}

	// {364}
	public void test0234() {
		check("Integrate[1/(x^m*(a^5 + x^5)), x]", 364);
	}

	// {261}
	public void test0235() {
		check("Integrate[x^3/(a^4 + x^4)^3, x]", 261);
	}

	// {613}
	public void test0236() {
		check("Integrate[(1 + x + x^2)^(-3/2), x]", 613);
	}

	// {636}
	public void test0237() {
		check("Integrate[x/(1 + x + x^2)^(3/2), x]", 636);
	}

	// {261}
	public void test0238() {
		check("Integrate[1/((1 - 3/x)^(4/3)*x^2), x]", 261);
	}

	// {261}
	public void test0239() {
		check("Integrate[x^6*(1 + x^7)^(1/3), x]", 261);
	}

	// {261}
	public void test0240() {
		check("Integrate[x^6/(1 + x^7)^(5/3), x]", 261);
	}

	// {1588}
	public void test0241() {
		check("Integrate[(-3*x + 2*x^3)*(-3*x^2 + x^4)^(3/5), x]", 1588);
	}

	// {1590}
	public void test0242() {
		check("Integrate[(-1 + x^4)/(x^2*Sqrt[1 + x^2 + x^4]), x]", 1590);
	}

	// {2084}
	public void test0243() {
		check("Integrate[(1 - x^2)/((1 + 2*a*x + x^2)*Sqrt[1 + 2*a*x + 2*b*x^2 + 2*a*x^3 + x^4]), x]", 2084);
	}

	// {2577}
	public void test0244() {
		check("Integrate[Cos[x]^(2*m)*Sin[x]^(2*m), x]", 2577);
	}

	// {4283}
	public void test0245() {
		check("Integrate[Cos[x]*Cos[4*x], x]", 4283);
	}

	// {2646}
	public void test0246() {
		check("Integrate[Sqrt[1 + Sin[2*x]], x]", 2646);
	}

	// {2646}
	public void test0247() {
		check("Integrate[Sqrt[1 - Sin[2*x]], x]", 2646);
	}

	// {4306}
	public void test0248() {
		check("Integrate[Sin[x]/Sqrt[Sin[2*x]], x]", 4306);
	}

	// {4305}
	public void test0249() {
		check("Integrate[Cos[x]/Sqrt[Sin[2*x]], x]", 4305);
	}

	// {4292}
	public void test0250() {
		check("Integrate[Csc[x]^5*Sin[2*x]^(3/2), x]", 4292);
	}

	// {4331}
	public void test0251() {
		check("Integrate[Sin[x]/Cos[2*x]^(5/2), x]", 4331);
	}

	// {30}
	public void test0252() {
		check("Integrate[x^(1 + 2*n), x]", 30);
	}

	// {2288}
	public void test0253() {
		check("Integrate[(E^x*(1 - x - x^2))/Sqrt[1 - x^2], x]", 2288);
	}

	// {4433}
	public void test0254() {
		check("Integrate[Cos[2*x]/E^(3*x), x]", 4433);
	}

	// {4453}
	public void test0255() {
		check("Integrate[E^(m*x)*Csc[x]^2, x]", 4453);
	}

	// {2288}
	public void test0256() {
		check("Integrate[(E^x*(1 - Sin[x]))/(1 - Cos[x]), x]", 2288);
	}

	// {2288}
	public void test0257() {
		check("Integrate[(E^x*(1 + Sin[x]))/(1 + Cos[x]), x]", 2288);
	}

	// {2288}
	public void test0258() {
		check("Integrate[(E^x*(1 + Cos[x]))/(1 - Sin[x]), x]", 2288);
	}

	// {2288}
	public void test0259() {
		check("Integrate[(E^x*(1 - Cos[x]))/(1 + Sin[x]), x]", 2288);
	}

	// {2637}
	public void test0260() {
		check("Integrate[Cosh[x], x]", 2637);
	}

	// {2638}
	public void test0261() {
		check("Integrate[Sinh[x], x]", 2638);
	}

	// {3475}
	public void test0262() {
		check("Integrate[Tanh[x], x]", 3475);
	}

	// {3475}
	public void test0263() {
		check("Integrate[Coth[x], x]", 3475);
	}

	// {3770}
	public void test0264() {
		check("Integrate[Sech[x], x]", 3770);
	}

	// {3770}
	public void test0265() {
		check("Integrate[Csch[x], x]", 3770);
	}

	// {2304}
	public void test0266() {
		check("Integrate[x^m*Log[x], x]", 2304);
	}

	// {2521}
	public void test0267() {
		check("Integrate[Log[Log[x]]/x, x]", 2521);
	}

	// {32}
	public void test0268() {
		check("Integrate[1/Sqrt[1 - a*x], x]", 32);
	}

	// {239}
	public void test0269() {
		check("Integrate[(1 - x^3)^(-1/3), x]", 239);
	}

	// {1587}
	public void test0270() {
		check("Integrate[(3 - 3*x + 30*x^2 + 160*x^3)/(9 + 24*x - 12*x^2 + 80*x^3 + 320*x^4), x]", 1587);
	}

	// {2090}
	public void test0271() {
		check("Integrate[(3 + 12*x + 20*x^2)/(9 + 24*x - 12*x^2 + 80*x^3 + 320*x^4), x]", 2090);
	}

	// {405}
	public void test0272() {
		check("Integrate[Sqrt[1 - x^4]/(1 + x^4), x]", 405);
	}

	// {2072}
	public void test0273() {
		check("Integrate[Sqrt[1 + p*x^2 - x^4]/(1 + x^4), x]", 2072);
	}

	// {484}
	public void test0274() {
		check("Integrate[x/(Sqrt[1 - x^3]*(4 - x^3)), x]", 484);
	}

	// {485}
	public void test0275() {
		check("Integrate[x/((4 - d*x^3)*Sqrt[-1 + d*x^3]), x]", 485);
	}

	// {395}
	public void test0276() {
		check("Integrate[1/((1 - 3*x^2)^(1/3)*(3 - x^2)), x]", 395);
	}

	// {394}
	public void test0277() {
		check("Integrate[1/((3 + x^2)*(1 + 3*x^2)^(1/3)), x]", 394);
	}

	// {393}
	public void test0278() {
		check("Integrate[1/((1 - x^2)^(1/3)*(3 + x^2)), x]", 393);
	}

	// {392}
	public void test0279() {
		check("Integrate[1/((3 - x^2)*(1 + x^2)^(1/3)), x]", 392);
	}

	// {487}
	public void test0280() {
		check("Integrate[x/(Sqrt[1 + x^3]*(10 + 6*Sqrt[3] + x^3)), x]", 487);
	}

	// {487}
	public void test0281() {
		check("Integrate[x/(Sqrt[1 + x^3]*(10 - 6*Sqrt[3] + x^3)), x]", 487);
	}

	// {488}
	public void test0282() {
		check("Integrate[x/(Sqrt[-1 + x^3]*(-10 - 6*Sqrt[3] + x^3)), x]", 488);
	}

	// {488}
	public void test0283() {
		check("Integrate[x/(Sqrt[-1 + x^3]*(-10 + 6*Sqrt[3] + x^3)), x]", 488);
	}

	// {2151}
	public void test0284() {
		check("Integrate[(-1 + x)/((1 + x)*(2 + x^3)^(1/3)), x]", 2151);
	}
}
