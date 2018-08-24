package org.matheclipse.core.rubi;

public class RuleTestHyperbolicFunctions extends AbstractRubiTestCase {

	public RuleTestHyperbolicFunctions(String name) {
		super(name, false);
	}

	// {3314}
	public void test0001() {
		check("Integrate[Sinh[x]^(3/2)/x^3, x]", 3314);
	}

	// {5441}
	public void test0002() {
		check("Integrate[(e*x)^m*Csch[a + b*x^2], x]", 5441);
	}

	// {5441}
	public void test0003() {
		check("Integrate[(e*x)^m*Csch[a + b/x], x]", 5441);
	}

	// {5441}
	public void test0004() {
		check("Integrate[(e*x)^m*Csch[a + b/x^2], x]", 5441);
	}

	// {5322}
	public void test0005() {
		check("Integrate[(e*x)^(-1 + 2*n)*(b*Sinh[c + d*x^n])^p, x]", 5322);
	}

	// {5322}
	public void test0006() {
		check("Integrate[(e*x)^(-1 + 2*n)*(a + b*Sinh[c + d*x^n])^p, x]", 5322);
	}

	// {5441}
	public void test0007() {
		check("Integrate[(e*x)^m*Csch[a + b*x^n]^2, x]", 5441);
	}

	// {5364}
	public void test0008() {
		check("Integrate[Sinh[(a + b*x)^2]/x, x]", 5364);
	}

	// {2638}
	public void test0009() {
		check("Integrate[Sinh[a + b*x], x]", 2638);
	}

	// {2639}
	public void test0010() {
		check("Integrate[Sqrt[I*Sinh[c + d*x]], x]", 2639);
	}

	// {2641}
	public void test0011() {
		check("Integrate[1/Sqrt[I*Sinh[c + d*x]], x]", 2641);
	}

	// {2643}
	public void test0012() {
		check("Integrate[(b*Sinh[c + d*x])^(4/3), x]", 2643);
	}

	// {2643}
	public void test0013() {
		check("Integrate[(b*Sinh[c + d*x])^(2/3), x]", 2643);
	}

	// {2643}
	public void test0014() {
		check("Integrate[(b*Sinh[c + d*x])^(1/3), x]", 2643);
	}

	// {2643}
	public void test0015() {
		check("Integrate[(b*Sinh[c + d*x])^(-1/3), x]", 2643);
	}

	// {2643}
	public void test0016() {
		check("Integrate[(b*Sinh[c + d*x])^(-2/3), x]", 2643);
	}

	// {2643}
	public void test0017() {
		check("Integrate[(b*Sinh[c + d*x])^(-4/3), x]", 2643);
	}

	// {2643}
	public void test0018() {
		check("Integrate[(b*Sinh[c + d*x])^n, x]", 2643);
	}

	// {2643}
	public void test0019() {
		check("Integrate[(I*Sinh[c + d*x])^n, x]", 2643);
	}

	// {2643}
	public void test0020() {
		check("Integrate[((-I)*Sinh[c + d*x])^n, x]", 2643);
	}

	// {2648}
	public void test0021() {
		check("Integrate[(1 + I*Sinh[c + d*x])^(-1), x]", 2648);
	}

	// {2648}
	public void test0022() {
		check("Integrate[(1 - I*Sinh[c + d*x])^(-1), x]", 2648);
	}

	// {2646}
	public void test0023() {
		check("Integrate[Sqrt[a + I*a*Sinh[c + d*x]], x]", 2646);
	}

	// {2657}
	public void test0024() {
		check("Integrate[(5 + (3*I)*Sinh[c + d*x])^(-1), x]", 2657);
	}

	// {2644}
	public void test0025() {
		check("Integrate[(a + b*Sinh[c + d*x])^2, x]", 2644);
	}

	// {2671}
	public void test0026() {
		check("Integrate[Cosh[x]^2/(1 + I*Sinh[x])^3, x]", 2671);
	}

	// {2671}
	public void test0027() {
		check("Integrate[Cosh[x]^2/(1 - I*Sinh[x])^3, x]", 2671);
	}

	// {6681}
	public void test0028() {
		check("Integrate[Csch[Sqrt[1 - a*x]/Sqrt[1 + a*x]]/(1 - a^2*x^2), x]", 6681);
	}

	// {6681}
	public void test0029() {
		check("Integrate[Csch[Sqrt[1 - a*x]/Sqrt[1 + a*x]]^2/(1 - a^2*x^2), x]", 6681);
	}

	// {5517}
	public void test0030() {
		check("Integrate[Sinh[a + b*Log[c*x^n]], x]", 5517);
	}

	// {5527}
	public void test0031() {
		check("Integrate[x^m*Sinh[a + b*Log[c*x^n]], x]", 5527);
	}

	// {5474}
	public void test0032() {
		check("Integrate[F^(c*(a + b*x))*Sinh[d + e*x], x]", 5474);
	}

	// {5493}
	public void test0033() {
		check("Integrate[F^(c*(a + b*x))*Csch[d + e*x], x]", 5493);
	}

	// {5493}
	public void test0034() {
		check("Integrate[F^(c*(a + b*x))*Csch[d + e*x]^2, x]", 5493);
	}

	// {5474}
	public void test0035() {
		check("Integrate[E^x*Sinh[a + b*x], x]", 5474);
	}

	// {3179}
	public void test0036() {
		check("Integrate[(a + b*Sinh[c + d*x]^2)^2, x]", 3179);
	}

	// {3177}
	public void test0037() {
		check("Integrate[Sqrt[1 - Sinh[x]^2], x]", 3177);
	}

	// {3182}
	public void test0038() {
		check("Integrate[1/Sqrt[1 - Sinh[x]^2], x]", 3182);
	}

	// {3314}
	public void test0039() {
		check("Integrate[Cosh[x]^(3/2)/x^3, x]", 3314);
	}

	// {5323}
	public void test0040() {
		check("Integrate[(e*x)^(-1 + 2*n)*(b*Cosh[c + d*x^n])^p, x]", 5323);
	}

	// {5323}
	public void test0041() {
		check("Integrate[(e*x)^(-1 + 2*n)*(a + b*Cosh[c + d*x^n])^p, x]", 5323);
	}

	// {5365}
	public void test0042() {
		check("Integrate[Cosh[(a + b*x)^2]/x, x]", 5365);
	}

	// {2637}
	public void test0043() {
		check("Integrate[Cosh[a + b*x], x]", 2637);
	}

	// {2639}
	public void test0044() {
		check("Integrate[Sqrt[Cosh[a + b*x]], x]", 2639);
	}

	// {2641}
	public void test0045() {
		check("Integrate[1/Sqrt[Cosh[a + b*x]], x]", 2641);
	}

	// {2643}
	public void test0046() {
		check("Integrate[(b*Cosh[c + d*x])^n, x]", 2643);
	}

	// {2648}
	public void test0047() {
		check("Integrate[(1 + Cosh[c + d*x])^(-1), x]", 2648);
	}

	// {2648}
	public void test0048() {
		check("Integrate[(1 - Cosh[c + d*x])^(-1), x]", 2648);
	}

	// {2646}
	public void test0049() {
		check("Integrate[Sqrt[a + a*Cosh[c + d*x]], x]", 2646);
	}

	// {2646}
	public void test0050() {
		check("Integrate[Sqrt[a - a*Cosh[c + d*x]], x]", 2646);
	}

	// {2644}
	public void test0051() {
		check("Integrate[(a + b*Cosh[c + d*x])^2, x]", 2644);
	}

	// {2657}
	public void test0052() {
		check("Integrate[(5 + 3*Cosh[c + d*x])^(-1), x]", 2657);
	}

	// {2671}
	public void test0053() {
		check("Integrate[Sinh[x]^2/(1 + Cosh[x])^3, x]", 2671);
	}

	// {2671}
	public void test0054() {
		check("Integrate[Sinh[x]^2/(1 - Cosh[x])^3, x]", 2671);
	}

	// {6681}
	public void test0055() {
		check("Integrate[Sech[Sqrt[1 - a*x]/Sqrt[1 + a*x]]/(1 - a^2*x^2), x]", 6681);
	}

	// {6681}
	public void test0056() {
		check("Integrate[Sech[Sqrt[1 - a*x]/Sqrt[1 + a*x]]^2/(1 - a^2*x^2), x]", 6681);
	}

	// {5518}
	public void test0057() {
		check("Integrate[Cosh[a + b*Log[c*x^n]], x]", 5518);
	}

	// {5528}
	public void test0058() {
		check("Integrate[x^m*Cosh[a + b*Log[c*x^n]], x]", 5528);
	}

	// {5475}
	public void test0059() {
		check("Integrate[F^(c*(a + b*x))*Cosh[d + e*x], x]", 5475);
	}

	// {5492}
	public void test0060() {
		check("Integrate[F^(c*(a + b*x))*Sech[d + e*x], x]", 5492);
	}

	// {5492}
	public void test0061() {
		check("Integrate[F^(c*(a + b*x))*Sech[d + e*x]^2, x]", 5492);
	}

	// {5475}
	public void test0062() {
		check("Integrate[E^x*Cosh[a + b*x], x]", 5475);
	}

	// {3177}
	public void test0063() {
		check("Integrate[Sqrt[1 + Cosh[x]^2], x]", 3177);
	}

	// {3182}
	public void test0064() {
		check("Integrate[1/Sqrt[1 + Cosh[x]^2], x]", 3182);
	}

	// {3475}
	public void test0065() {
		check("Integrate[Tanh[a + b*x], x]", 3475);
	}

	// {3475}
	public void test0066() {
		check("Integrate[Coth[a + b*x], x]", 3475);
	}

	// {3488}
	public void test0067() {
		check("Integrate[Sech[x]/(1 + Tanh[x]), x]", 3488);
	}

	// {3488}
	public void test0068() {
		check("Integrate[Csch[x]/(1 + Coth[x]), x]", 3488);
	}

	// {5440}
	public void test0069() {
		check("Integrate[(e*x)^m*(a + b*Sech[c + d*x^n])^p, x]", 5440);
	}

	// {3770}
	public void test0070() {
		check("Integrate[Sech[a + b*x], x]", 3770);
	}

	// {3794}
	public void test0071() {
		check("Integrate[Sech[x]/(a + a*Sech[x]), x]", 3794);
	}

	// {3784}
	public void test0072() {
		check("Integrate[1/Sqrt[a + b*Sech[c + d*x]], x]", 3784);
	}

	// {3780}
	public void test0073() {
		check("Integrate[Sqrt[a + b*Sech[c + d*x]], x]", 3780);
	}

	// {3784}
	public void test0074() {
		check("Integrate[1/Sqrt[a + b*Sech[c + d*x]], x]", 3784);
	}

	// {5594}
	public void test0075() {
		check("Integrate[Cosh[c + d*x]/((e + f*x)*(a + b*Csch[c + d*x])), x]", 5594);
	}

	// {5441}
	public void test0076() {
		check("Integrate[(e*x)^m*(a + b*Csch[c + d*x^n])^p, x]", 5441);
	}

	// {3770}
	public void test0077() {
		check("Integrate[Csch[a + b*x], x]", 3770);
	}

	// {3794}
	public void test0078() {
		check("Integrate[Csch[x]/(I + Csch[x]), x]", 3794);
	}

	// {2563}
	public void test0079() {
		check("Integrate[Cosh[x]^(2/3)/Sinh[x]^(8/3), x]", 2563);
	}

	// {2563}
	public void test0080() {
		check("Integrate[Sinh[x]^(2/3)/Cosh[x]^(8/3), x]", 2563);
	}

	// {4282}
	public void test0081() {
		check("Integrate[Sinh[x]*Sinh[3*x], x]", 4282);
	}

	// {4282}
	public void test0082() {
		check("Integrate[Sinh[x]*Sinh[4*x], x]", 4282);
	}

	// {4284}
	public void test0083() {
		check("Integrate[Cosh[2*x]*Sinh[x], x]", 4284);
	}

	// {4284}
	public void test0084() {
		check("Integrate[Cosh[3*x]*Sinh[x], x]", 4284);
	}

	// {4284}
	public void test0085() {
		check("Integrate[Cosh[4*x]*Sinh[x], x]", 4284);
	}

	// {4284}
	public void test0086() {
		check("Integrate[Cosh[x]*Sinh[3*x], x]", 4284);
	}

	// {4284}
	public void test0087() {
		check("Integrate[Cosh[x]*Sinh[4*x], x]", 4284);
	}

	// {4283}
	public void test0088() {
		check("Integrate[Cosh[x]*Cosh[2*x], x]", 4283);
	}

	// {4283}
	public void test0089() {
		check("Integrate[Cosh[x]*Cosh[3*x], x]", 4283);
	}

	// {4283}
	public void test0090() {
		check("Integrate[Cosh[x]*Cosh[4*x], x]", 4283);
	}

	// {3475}
	public void test0091() {
		check("Integrate[Tanh[a + b*x], x]", 3475);
	}

	// {5455}
	public void test0092() {
		check("Integrate[x^m*Sech[a + b*x]*Tanh[a + b*x]^2, x]", 5455);
	}

	// {5455}
	public void test0093() {
		check("Integrate[(Sech[a + b*x]*Tanh[a + b*x]^2)/x, x]", 5455);
	}

	// {5455}
	public void test0094() {
		check("Integrate[(Sech[a + b*x]*Tanh[a + b*x]^2)/x^2, x]", 5455);
	}

	// {3475}
	public void test0095() {
		check("Integrate[Coth[a + b*x], x]", 3475);
	}

	// {5457}
	public void test0096() {
		check("Integrate[x^m*Coth[a + b*x]^2*Csch[a + b*x], x]", 5457);
	}

	// {5457}
	public void test0097() {
		check("Integrate[(Coth[a + b*x]^2*Csch[a + b*x])/x, x]", 5457);
	}

	// {5457}
	public void test0098() {
		check("Integrate[(Coth[a + b*x]^2*Csch[a + b*x])/x^2, x]", 5457);
	}

	// {5461}
	public void test0099() {
		check("Integrate[(Csch[a + b*x]*Sech[a + b*x])/x, x]", 5461);
	}

	// {5461}
	public void test0100() {
		check("Integrate[(Csch[a + b*x]*Sech[a + b*x])/x^2, x]", 5461);
	}

	// {5461}
	public void test0101() {
		check("Integrate[(Csch[a + b*x]^2*Sech[a + b*x]^2)/x, x]", 5461);
	}

	// {5461}
	public void test0102() {
		check("Integrate[(Csch[a + b*x]^2*Sech[a + b*x]^2)/x^2, x]", 5461);
	}

	// {5461}
	public void test0103() {
		check("Integrate[(Csch[a + b*x]^3*Sech[a + b*x]^3)/x, x]", 5461);
	}

	// {5461}
	public void test0104() {
		check("Integrate[(Csch[a + b*x]^3*Sech[a + b*x]^3)/x^2, x]", 5461);
	}

	// {3075}
	public void test0105() {
		check("Integrate[(a*Cosh[x] + b*Sinh[x])^(-2), x]", 3075);
	}

	// {3071}
	public void test0106() {
		check("Integrate[(a*Cosh[c + d*x] + a*Sinh[c + d*x])^2, x]", 3071);
	}

	// {3071}
	public void test0107() {
		check("Integrate[(a*Cosh[c + d*x] + a*Sinh[c + d*x])^3, x]", 3071);
	}

	// {3071}
	public void test0108() {
		check("Integrate[(a*Cosh[c + d*x] + a*Sinh[c + d*x])^n, x]", 3071);
	}

	// {3071}
	public void test0109() {
		check("Integrate[(a*Cosh[c + d*x] + a*Sinh[c + d*x])^(-1), x]", 3071);
	}

	// {3071}
	public void test0110() {
		check("Integrate[(a*Cosh[c + d*x] + a*Sinh[c + d*x])^(-2), x]", 3071);
	}

	// {3071}
	public void test0111() {
		check("Integrate[(a*Cosh[c + d*x] + a*Sinh[c + d*x])^(-3), x]", 3071);
	}

	// {3071}
	public void test0112() {
		check("Integrate[Sqrt[a*Cosh[c + d*x] + a*Sinh[c + d*x]], x]", 3071);
	}

	// {3071}
	public void test0113() {
		check("Integrate[1/Sqrt[a*Cosh[c + d*x] + a*Sinh[c + d*x]], x]", 3071);
	}

	// {3071}
	public void test0114() {
		check("Integrate[(a*Cosh[c + d*x] - a*Sinh[c + d*x])^2, x]", 3071);
	}

	// {3071}
	public void test0115() {
		check("Integrate[(a*Cosh[c + d*x] - a*Sinh[c + d*x])^3, x]", 3071);
	}

	// {3071}
	public void test0116() {
		check("Integrate[(a*Cosh[c + d*x] - a*Sinh[c + d*x])^n, x]", 3071);
	}

	// {3071}
	public void test0117() {
		check("Integrate[(a*Cosh[c + d*x] - a*Sinh[c + d*x])^(-1), x]", 3071);
	}

	// {3071}
	public void test0118() {
		check("Integrate[(a*Cosh[c + d*x] - a*Sinh[c + d*x])^(-2), x]", 3071);
	}

	// {3071}
	public void test0119() {
		check("Integrate[(a*Cosh[c + d*x] - a*Sinh[c + d*x])^(-3), x]", 3071);
	}

	// {3071}
	public void test0120() {
		check("Integrate[Sqrt[a*Cosh[c + d*x] - a*Sinh[c + d*x]], x]", 3071);
	}

	// {3071}
	public void test0121() {
		check("Integrate[1/Sqrt[a*Cosh[c + d*x] - a*Sinh[c + d*x]], x]", 3071);
	}

	// {4385}
	public void test0122() {
		check("Integrate[(Cosh[x] + Sinh[x])/(Cosh[x] - Sinh[x]), x]", 4385);
	}

	// {4385}
	public void test0123() {
		check("Integrate[(Cosh[x] - Sinh[x])/(Cosh[x] + Sinh[x]), x]", 4385);
	}

	// {3133}
	public void test0124() {
		check("Integrate[(Cosh[x] - I*Sinh[x])/(Cosh[x] + I*Sinh[x]), x]", 3133);
	}

	// {3133}
	public void test0125() {
		check("Integrate[(B*Cosh[x] + C*Sinh[x])/(b*Cosh[x] + c*Sinh[x]), x]", 3133);
	}

	// {3114}
	public void test0126() {
		check("Integrate[(Sqrt[b^2 - c^2] + b*Cosh[x] + c*Sinh[x])^(-1), x]", 3114);
	}

	// {3112}
	public void test0127() {
		check("Integrate[Sqrt[Sqrt[b^2 - c^2] + b*Cosh[x] + c*Sinh[x]], x]", 3112);
	}

	// {3112}
	public void test0128() {
		check("Integrate[Sqrt[-Sqrt[b^2 - c^2] + b*Cosh[x] + c*Sinh[x]], x]", 3112);
	}

	// {3131}
	public void test0129() {
		check("Integrate[Sinh[x]/(1 + Cosh[x] + Sinh[x]), x]", 3131);
	}

	// {3150}
	public void test0130() {
		check("Integrate[(b^2 - c^2 + a*b*Cosh[x] + a*c*Sinh[x])/(a + b*Cosh[x] + c*Sinh[x])^2, x]", 3150);
	}

	// {3131}
	public void test0131() {
		check("Integrate[(A + C*Sinh[x])/(a + b*Cosh[x] + b*Sinh[x]), x]", 3131);
	}

	// {3132}
	public void test0132() {
		check("Integrate[(A + B*Cosh[x])/(a + b*Cosh[x] + b*Sinh[x]), x]", 3132);
	}

	// {3130}
	public void test0133() {
		check("Integrate[(A + B*Cosh[x] + C*Sinh[x])/(a + b*Cosh[x] + b*Sinh[x]), x]", 3130);
	}

	// {3131}
	public void test0134() {
		check("Integrate[(A + C*Sinh[x])/(a + b*Cosh[x] - b*Sinh[x]), x]", 3131);
	}

	// {3132}
	public void test0135() {
		check("Integrate[(A + B*Cosh[x])/(a + b*Cosh[x] - b*Sinh[x]), x]", 3132);
	}

	// {3130}
	public void test0136() {
		check("Integrate[(A + B*Cosh[x] + C*Sinh[x])/(a + b*Cosh[x] - b*Sinh[x]), x]", 3130);
	}

	// {5628}
	public void test0137() {
		check("Integrate[1/(x*(a + b*Cosh[x]*Sinh[x])), x]", 5628);
	}

	// {5474}
	public void test0138() {
		check("Integrate[E^(a + b*x)*Sinh[c + d*x], x]", 5474);
	}

	// {5493}
	public void test0139() {
		check("Integrate[E^(a + b*x)*Csch[c + d*x], x]", 5493);
	}

	// {5493}
	public void test0140() {
		check("Integrate[E^(c + d*x)*Csch[a + b*x]^2, x]", 5493);
	}

	// {5475}
	public void test0141() {
		check("Integrate[E^(a + b*x)*Cosh[c + d*x], x]", 5475);
	}

	// {5492}
	public void test0142() {
		check("Integrate[E^(a + b*x)*Sech[c + d*x], x]", 5492);
	}

	// {5492}
	public void test0143() {
		check("Integrate[E^(a + b*x)*Sech[c + d*x]^2, x]", 5492);
	}

	// {5475}
	public void test0144() {
		check("Integrate[E^(c + d*x)*Cosh[a + b*x], x]", 5475);
	}

	// {6686}
	public void test0145() {
		check("Integrate[Csch[x]*Log[Tanh[x]]*Sech[x], x]", 6686);
	}

	// {6686}
	public void test0146() {
		check("Integrate[Csch[2*x]*Log[Tanh[x]], x]", 6686);
	}

	// {4336}
	public void test0147() {
		check("Integrate[Cosh[a + b*x]*F[c, d, Sinh[a + b*x], r, s], x]", 4336);
	}

	// {4337}
	public void test0148() {
		check("Integrate[F[c, d, Cosh[a + b*x], r, s]*Sinh[a + b*x], x]", 4337);
	}

	// {4346}
	public void test0149() {
		check("Integrate[F[c, d, Tanh[a + b*x], r, s]*Sech[a + b*x]^2, x]", 4346);
	}

	// {4347}
	public void test0150() {
		check("Integrate[Csch[a + b*x]^2*F[c, d, Coth[a + b*x], r, s], x]", 4347);
	}

	// {5370}
	public void test0151() {
		check("Integrate[(Cosh[Sqrt[x]]*Sinh[Sqrt[x]])/Sqrt[x], x]", 5370);
	}

	// {4385}
	public void test0152() {
		check("Integrate[(Cosh[a + b*x] - Sinh[a + b*x])/(Cosh[a + b*x] + Sinh[a + b*x]), x]", 4385);
	}
}
