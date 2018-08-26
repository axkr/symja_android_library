package org.matheclipse.core.rubi;

public class IndependentTestSuites extends AbstractRubiTestCase {

	public IndependentTestSuites(String name) {
		super(name, false);
	}

	// {32}
	public void test0001() {
		check(//
				"Integrate[Sqrt[1 + 2*x], x]", //
				"(1 + 2*x)^(3/2)/3", //
				32);
	}

	// {629}
	public void test0002() {
		check(//
				"Integrate[(1 + x)/(2 + 2*x + x^2)^3, x]", //
				"-1/(4*(2 + 2*x + x^2)^2)", //
				629);
	}

	// {261}
	public void test0003() {
		check(//
				"Integrate[x^5/Sqrt[1 - x^6], x]", //
				"-Sqrt[1 - x^6]/3", //
				261);
	}

	// {191}
	public void test0004() {
		check(//
				"Integrate[(1 + x^2)^(-3/2), x]", //
				"x/Sqrt[1 + x^2]", //
				191);
	}

	// {261}
	public void test0005() {
		check(//
				"Integrate[x^2*(27 + 8*x^3)^(2/3), x]", //
				"(27 + 8*x^3)^(5/3)/40", //
				261);
	}

	// {3145}
	public void test0006() {
		check(//
				"Integrate[(Cos[x] + Sin[x])/(-Cos[x] + Sin[x])^(1/3), x]", //
				"(3*(-Cos[x] + Sin[x])^(2/3))/2", //
				3145);
	}

	// {6686}
	public void test0007() {
		check(//
				"Integrate[x/(Sqrt[1 + x^2]*Sqrt[1 + Sqrt[1 + x^2]]), x]", //
				"2*Sqrt[1 + Sqrt[1 + x^2]]", //
				6686);
	}

	// {261}
	public void test0008() {
		check(//
				"Integrate[x*Sqrt[1 + x^2], x]", //
				"(1 + x^2)^(3/2)/3", //
				261);
	}

	// {261}
	public void test0009() {
		check(//
				"Integrate[x*(-1 + x^2)^9, x]", //
				"(1 - x^2)^10/20", //
				261);
	}

	// {37}
	public void test0010() {
		check(//
				"Integrate[(3 + 2*x)/(7 + 6*x)^3, x]", //
				"-(3 + 2*x)^2/(8*(7 + 6*x)^2)", //
				37);
	}

	// {261}
	public void test0011() {
		check(//
				"Integrate[x^4*(1 + x^5)^5, x]", //
				"(1 + x^5)^6/30", //
				261);
	}

	// {3441}
	public void test0012() {
		check(//
				"Integrate[x*Cos[x^2]*Sin[x^2], x]", //
				"Sin[x^2]^2/4", //
				3441);
	}

	// {31}
	public void test0013() {
		check(//
				"Integrate[(2 + 3*x)^(-1), x]", //
				"Log[2 + 3*x]/3", //
				31);
	}

	// {2304}
	public void test0014() {
		check(//
				"Integrate[x*Log[x], x]", //
				"-x^2/4 + (x^2*Log[x])/2", //
				2304);
	}

	// {31}
	public void test0015() {
		check(//
				"Integrate[(1 + t)^(-1), t]", //
				"Log[1 + t]", //
				31);
	}

	// {3475}
	public void test0016() {
		check(//
				"Integrate[Cot[x], x]", //
				"Log[Sin[x]]", //
				3475);
	}

	// {2304}
	public void test0017() {
		check(//
				"Integrate[x^n*Log[a*x], x]", //
				"-(x^(1 + n)/(1 + n)^2) + (x^(1 + n)*Log[a*x])/(1 + n)", //
				2304);
	}

	// {2209}
	public void test0018() {
		check(//
				"Integrate[E^x^3*x^2, x]", //
				"E^x^3/3", //
				2209);
	}

	// {2209}
	public void test0019() {
		check(//
				"Integrate[2^Sqrt[x]/Sqrt[x], x]", //
				"2^(1 + Sqrt[x])/Log[2]", //
				2209);
	}

	// {4432}
	public void test0020() {
		check(//
				"Integrate[E^x*Sin[x], x]", //
				"-(E^x*Cos[x])/2 + (E^x*Sin[x])/2", //
				4432);
	}

	// {4433}
	public void test0021() {
		check(//
				"Integrate[E^x*Cos[x], x]", //
				"(E^x*Cos[x])/2 + (E^x*Sin[x])/2", //
				4433);
	}

	// {4433}
	public void test0022() {
		check(//
				"Integrate[E^(a*x)*Cos[b*x], x]", //
				"(a*E^(a*x)*Cos[b*x])/(a^2 + b^2) + (b*E^(a*x)*Sin[b*x])/(a^2 + b^2)", //
				4433);
	}

	// {4432}
	public void test0023() {
		check(//
				"Integrate[E^(a*x)*Sin[b*x], x]", //
				"-((b*E^(a*x)*Cos[b*x])/(a^2 + b^2)) + (a*E^(a*x)*Sin[b*x])/(a^2 + b^2)", //
				4432);
	}

	// {203}
	public void test0024() {
		check(//
				"Integrate[(a^2 + x^2)^(-1), x]", //
				"ArcTan[x/a]/a", //
				203);
	}

	// {205}
	public void test0025() {
		check(//
				"Integrate[(a + b*x^2)^(-1), x]", //
				"ArcTan[(Sqrt[b]*x)/Sqrt[a]]/(Sqrt[a]*Sqrt[b])", //
				205);
	}

	// {6686}
	public void test0026() {
		check(//
				"Integrate[ArcTan[Sqrt[x]]/(Sqrt[x]*(1 + x)), x]", //
				"ArcTan[Sqrt[x]]^2", //
				6686);
	}

	// {5077}
	public void test0027() {
		check(//
				"Integrate[(E^ArcTan[x]*x)/(1 + x^2)^(3/2), x]", //
				"-(E^ArcTan[x]*(1 - x))/(2*Sqrt[1 + x^2])", //
				5077);
	}

	// {5069}
	public void test0028() {
		check(//
				"Integrate[E^ArcTan[x]/(1 + x^2)^(3/2), x]", //
				"(E^ArcTan[x]*(1 + x))/(2*Sqrt[1 + x^2])", //
				5069);
	}

	// {1588}
	public void test0029() {
		check(//
				"Integrate[(-1 + 4*x^5)/(1 + x + x^5)^2, x]", //
				"-(x/(1 + x + x^5))", //
				1588);
	}

	// {2657}
	public void test0030() {
		check(//
				"Integrate[(1 + Cos[x]/2)^(-1), x]", //
				"(2*x)/Sqrt[3] - (4*ArcTan[Sin[x]/(2 + Sqrt[3] + Cos[x])])/Sqrt[3]", //
				2657);
	}

	// {3075}
	public void test0031() {
		check(//
				"Integrate[(b*Cos[x] + a*Sin[x])^(-2), x]", //
				"Sin[x]/(b*(b*Cos[x] + a*Sin[x]))", //
				3075);
	}

	// {261}
	public void test0032() {
		check(//
				"Integrate[x/Sqrt[3 - x^2], x]", //
				"-Sqrt[3 - x^2]", //
				261);
	}

	// {2178}
	public void test0033() {
		check(//
				"Integrate[E^t/t, t]", //
				"ExpIntegralEi[t]", //
				2178);
	}

	// {2178}
	public void test0034() {
		check(//
				"Integrate[E^(a*t)/t, t]", //
				"ExpIntegralEi[a*t]", //
				2178);
	}

	// {2178}
	public void test0035() {
		check(//
				"Integrate[1/(E^t*(-1 - a + t)), t]", //
				"E^(-1 - a)*ExpIntegralEi[1 + a - t]", //
				2178);
	}

	// {3133}
	public void test0036() {
		check(//
				"Integrate[(b1*Cos[x] + a1*Sin[x])/(b*Cos[x] + a*Sin[x]), x]", //
				"((a*a1 + b*b1)*x)/(a^2 + b^2) - ((a1*b - a*b1)*Log[b*Cos[x] + a*Sin[x]])/(a^2 + b^2)", //
				3133);
	}

	// {2298}
	public void test0037() {
		check(//
				"Integrate[Log[t]^(-1), t]", //
				"LogIntegral[t]", //
				2298);
	}

	// {2178}
	public void test0038() {
		check(//
				"Integrate[E^(2*t)/(-1 + t), t]", //
				"E^2*ExpIntegralEi[-2*(1 - t)]", //
				2178);
	}

	// {218}
	public void test0039() {
		check(//
				"Integrate[1/Sqrt[1 + t^3], t]", //
				"(2*Sqrt[2 + Sqrt[3]]*(1 + t)*Sqrt[(1 - t + t^2)/(1 + Sqrt[3] + t)^2]*EllipticF[ArcSin[(1 - Sqrt[3] + t)/(1 + Sqrt[3] + t)], -7 - 4*Sqrt[3]])/(3^(1/4)*Sqrt[(1 + t)/(1 + Sqrt[3] + t)^2]*Sqrt[1 + t^3])", //
				218);
	}

	// {3114}
	public void test0040() {
		check(//
				"Integrate[(Sqrt[2] + Cos[z] + Sin[z])^(-1), z]", //
				"-((1 - Sqrt[2]*Sin[z])/(Cos[z] - Sin[z]))", //
				3114);
	}

	// {203}
	public void test0041() {
		check(//
				"Integrate[(1 + x^2)^(-1), x]", //
				"ArcTan[x]", //
				203);
	}

	// {3299}
	public void test0042() {
		check(//
				"Integrate[Sin[x]/x, x]", //
				"SinIntegral[x]", //
				3299);
	}

	// {2095}
	public void test0043() {
		check(//
				"Integrate[(6 - 3*x^2 + x^4)/(4 + 5*x^2 - 5*x^4 + x^6), x]", //
				"-ArcTan[Sqrt[3] - 2*x] + ArcTan[Sqrt[3] + 2*x] + ArcTan[(x*(1 - 3*x^2 + x^4))/2]", //
				2095);
	}

	// {29}
	public void test0045() {
		check(//
				"Integrate[x^(-1), x]", //
				"Log[x]", //
				29);
	}

	// {32}
	public void test0046() {
		check(//
				"Integrate[(a + b*x)^p, x]", //
				"(a + b*x)^(1 + p)/(b*(1 + p))", //
				32);
	}

	// {31}
	public void test0047() {
		check(//
				"Integrate[(a + b*x)^(-1), x]", //
				"Log[a + b*x]/b", //
				31);
	}

	// {32}
	public void test0048() {
		check(//
				"Integrate[(a + b*x)^(-2), x]", //
				"-(1/(b*(a + b*x)))", //
				32);
	}

	// {203}
	public void test0049() {
		check(//
				"Integrate[(c^2 + x^2)^(-1), x]", //
				"ArcTan[x/c]/c", //
				203);
	}

	// {206}
	public void test0050() {
		check(//
				"Integrate[(c^2 - x^2)^(-1), x]", //
				"ArcTanh[x/c]/c", //
				206);
	}

	// {2295}
	public void test0051() {
		check(//
				"Integrate[Log[x], x]", //
				"-x + x*Log[x]", //
				2295);
	}

	// {2304}
	public void test0052() {
		check(//
				"Integrate[x*Log[x], x]", //
				"-x^2/4 + (x^2*Log[x])/2", //
				2304);
	}

	// {2304}
	public void test0053() {
		check(//
				"Integrate[x^2*Log[x], x]", //
				"-x^3/9 + (x^3*Log[x])/3", //
				2304);
	}

	// {2304}
	public void test0054() {
		check(//
				"Integrate[x^p*Log[x], x]", //
				"-(x^(1 + p)/(1 + p)^2) + (x^(1 + p)*Log[x])/(1 + p)", //
				2304);
	}

	// {2298}
	public void test0055() {
		check(//
				"Integrate[Log[x]^(-1), x]", //
				"LogIntegral[x]", //
				2298);
	}

	// {2638}
	public void test0056() {
		check(//
				"Integrate[Sin[x], x]", //
				"-Cos[x]", //
				2638);
	}

	// {2637}
	public void test0057() {
		check(//
				"Integrate[Cos[x], x]", //
				"Sin[x]", //
				2637);
	}

	// {3475}
	public void test0058() {
		check(//
				"Integrate[Tan[x], x]", //
				"-Log[Cos[x]]", //
				3475);
	}

	// {3475}
	public void test0059() {
		check(//
				"Integrate[Cot[x], x]", //
				"Log[Sin[x]]", //
				3475);
	}

	// {3770}
	public void test0060() {
		check(//
				"Integrate[Sec[x], x]", //
				"ArcTanh[Sin[x]]", //
				3770);
	}

	// {3770}
	public void test0061() {
		check(//
				"Integrate[Csc[x], x]", //
				"-ArcTanh[Cos[x]]", //
				3770);
	}

	// {2643}
	public void test0062() {
		check(//
				"Integrate[Sin[x]^p, x]", //
				"(Cos[x]*Hypergeometric2F1[1/2, (1 + p)/2, (3 + p)/2, Sin[x]^2]*Sin[x]^(1 + p))/((1 + p)*Sqrt[Cos[x]^2])", //
				2643);
	}

	// {4282}
	public void test0063() {
		check(//
				"Integrate[Sin[x]*Sin[2*x], x]", //
				"Sin[x]/2 - Sin[3*x]/6", //
				4282);
	}

	// {3299}
	public void test0064() {
		check(//
				"Integrate[Sin[x]/x, x]", //
				"SinIntegral[x]", //
				3299);
	}

	// {3302}
	public void test0065() {
		check(//
				"Integrate[Cos[x]/x, x]", //
				"CosIntegral[x]", //
				3302);
	}

	// {2638}
	public void test0066() {
		check(//
				"Integrate[Sin[a + b*x], x]", //
				"-(Cos[a + b*x]/b)", //
				2638);
	}

	// {2637}
	public void test0067() {
		check(//
				"Integrate[Cos[a + b*x], x]", //
				"Sin[a + b*x]/b", //
				2637);
	}

	// {3475}
	public void test0068() {
		check(//
				"Integrate[Tan[a + b*x], x]", //
				"-(Log[Cos[a + b*x]]/b)", //
				3475);
	}

	// {3475}
	public void test0069() {
		check(//
				"Integrate[Cot[a + b*x], x]", //
				"Log[Sin[a + b*x]]/b", //
				3475);
	}

	// {3770}
	public void test0070() {
		check(//
				"Integrate[Csc[a + b*x], x]", //
				"-(ArcTanh[Cos[a + b*x]]/b)", //
				3770);
	}

	// {3770}
	public void test0071() {
		check(//
				"Integrate[Sec[a + b*x], x]", //
				"ArcTanh[Sin[a + b*x]]/b", //
				3770);
	}

	// {2648}
	public void test0072() {
		check(//
				"Integrate[(1 + Cos[x])^(-1), x]", //
				"Sin[x]/(1 + Cos[x])", //
				2648);
	}

	// {2648}
	public void test0073() {
		check(//
				"Integrate[(1 - Cos[x])^(-1), x]", //
				"-(Sin[x]/(1 - Cos[x]))", //
				2648);
	}

	// {2648}
	public void test0074() {
		check(//
				"Integrate[(1 + Sin[x])^(-1), x]", //
				"-(Cos[x]/(1 + Sin[x]))", //
				2648);
	}

	// {2648}
	public void test0075() {
		check(//
				"Integrate[(1 - Sin[x])^(-1), x]", //
				"Cos[x]/(1 - Sin[x])", //
				2648);
	}

	// {4283}
	public void test0076() {
		check(//
				"Integrate[Cos[x]*Cos[2*x], x]", //
				"Sin[x]/2 + Sin[3*x]/6", //
				4283);
	}

	// {4284}
	public void test0077() {
		check(//
				"Integrate[Cos[3*x]*Sin[2*x], x]", //
				"Cos[x]/2 - Cos[5*x]/10", //
				4284);
	}

	// {4432}
	public void test0078() {
		check(//
				"Integrate[d^x*Sin[x], x]", //
				"-((d^x*Cos[x])/(1 + Log[d]^2)) + (d^x*Log[d]*Sin[x])/(1 + Log[d]^2)", //
				4432);
	}

	// {4433}
	public void test0079() {
		check(//
				"Integrate[d^x*Cos[x], x]", //
				"(d^x*Cos[x]*Log[d])/(1 + Log[d]^2) + (d^x*Sin[x])/(1 + Log[d]^2)", //
				4433);
	}

	// {4475}
	public void test0080() {
		check(//
				"Integrate[Sin[Log[x]], x]", //
				"-(x*Cos[Log[x]])/2 + (x*Sin[Log[x]])/2", //
				4475);
	}

	// {4476}
	public void test0081() {
		check(//
				"Integrate[Cos[Log[x]], x]", //
				"(x*Cos[Log[x]])/2 + (x*Sin[Log[x]])/2", //
				4476);
	}

	// {2194}
	public void test0082() {
		check(//
				"Integrate[E^x, x]", //
				"E^x", //
				2194);
	}

	// {2194}
	public void test0083() {
		check(//
				"Integrate[a^x, x]", //
				"a^x/Log[a]", //
				2194);
	}

	// {2194}
	public void test0084() {
		check(//
				"Integrate[E^(a*x), x]", //
				"E^(a*x)/a", //
				2194);
	}

	// {2178}
	public void test0085() {
		check(//
				"Integrate[E^(a*x)/x, x]", //
				"ExpIntegralEi[a*x]", //
				2178);
	}

	// {2197}
	public void test0086() {
		check(//
				"Integrate[(E^(a*x)*x)/(1 + a*x)^2, x]", //
				"E^(a*x)/(a^2*(1 + a*x))", //
				2197);
	}

	// {2209}
	public void test0087() {
		check(//
				"Integrate[k^x^2*x, x]", //
				"k^x^2/(2*Log[k])", //
				2209);
	}

	// {2204}
	public void test0088() {
		check(//
				"Integrate[E^x^2, x]", //
				"(Sqrt[Pi]*Erfi[x])/2", //
				2204);
	}

	// {2209}
	public void test0089() {
		check(//
				"Integrate[E^x^2*x, x]", //
				"E^x^2/2", //
				2209);
	}

	// {32}
	public void test0092() {
		check(//
				"Integrate[Sqrt[a + b*x], x]", //
				"(2*(a + b*x)^(3/2))/(3*b)", //
				32);
	}

	// {32}
	public void test0093() {
		check(//
				"Integrate[1/Sqrt[a + b*x], x]", //
				"(2*Sqrt[a + b*x])/b", //
				32);
	}

	// {32}
	public void test0094() {
		check(//
				"Integrate[(a + b*x)^(p/2), x]", //
				"(2*(a + b*x)^((2 + p)/2))/(b*(2 + p))", //
				32);
	}

	// {261}
	public void test0095() {
		check(//
				"Integrate[x/(1 - x^2)^(9/8), x]", //
				"4/(1 - x^2)^(1/8)", //
				261);
	}

	// {803}
	public void test0096() {
		check(//
				"Integrate[(1 + x)/((1 - x)^2*Sqrt[1 + x^2]), x]", //
				"Sqrt[1 + x^2]/(1 - x)", //
				803);
	}

	// {215}
	public void test0097() {
		check(//
				"Integrate[1/Sqrt[1 + x^2], x]", //
				"ArcSinh[x]", //
				215);
	}

	// {261}
	public void test0098() {
		check(//
				"Integrate[r/Sqrt[-alpha^2 + 2*e*r^2], r]", //
				"Sqrt[-alpha^2 + 2*e*r^2]/(2*e)", //
				261);
	}

	// {261}
	public void test0099() {
		check(//
				"Integrate[r/Sqrt[-alpha^2 - epsilon^2 + 2*e*r^2], r]", //
				"Sqrt[-alpha^2 - epsilon^2 + 2*e*r^2]/(2*e)", //
				261);
	}

	// {2304}
	public void test0100() {
		check(//
				"Integrate[Log[x^2]/x^3, x]", //
				"-1/(2*x^2) - Log[x^2]/(2*x^2)", //
				2304);
	}

	// {2201}
	public void test0101() {
		check(//
				"Integrate[(-1 + (1 - x)*Log[x])/(E^x*Log[x]^2), x]", //
				"x/(E^x*Log[x])", //
				2201);
	}

	// {3475}
	public void test0102() {
		check(//
				"Integrate[Tanh[2*x], x]", //
				"Log[Cosh[2*x]]/2", //
				3475);
	}

	// {6684}
	public void test0103() {
		check(//
				"Integrate[(-1 + I*eps*Sinh[x])/(I*a - x + I*eps*Cosh[x]), x]", //
				"Log[a + I*x + eps*Cosh[x]]", //
				6684);
	}

	// {2646}
	public void test0104() {
		check(//
				"Integrate[Sqrt[1 + Sin[x]], x]", //
				"(-2*Cos[x])/Sqrt[1 + Sin[x]]", //
				2646);
	}

	// {2646}
	public void test0105() {
		check(//
				"Integrate[Sqrt[1 - Sin[x]], x]", //
				"(2*Cos[x])/Sqrt[1 - Sin[x]]", //
				2646);
	}

	// {2646}
	public void test0106() {
		check(//
				"Integrate[Sqrt[1 + Cos[x]], x]", //
				"(2*Sin[x])/Sqrt[1 + Cos[x]]", //
				2646);
	}

	// {2646}
	public void test0107() {
		check(//
				"Integrate[Sqrt[1 - Cos[x]], x]", //
				"(-2*Sin[x])/Sqrt[1 - Cos[x]]", //
				2646);
	}

	// {3475}
	public void test0108() {
		check(//
				"Integrate[Cot[x], x]", //
				"Log[Sin[x]]", //
				3475);
	}

	// {3475}
	public void test0109() {
		check(//
				"Integrate[Tanh[x], x]", //
				"Log[Cosh[x]]", //
				3475);
	}

	// {3475}
	public void test0110() {
		check(//
				"Integrate[Coth[x], x]", //
				"Log[Sinh[x]]", //
				3475);
	}

	// {2194}
	public void test0111() {
		check(//
				"Integrate[b^x, x]", //
				"b^x/Log[b]", //
				2194);
	}

	// {32}
	public void test0112() {
		check(//
				"Integrate[(-3 + x)^(-4), x]", //
				"1/(3*(3 - x)^3)", //
				32);
	}

	// {215}
	public void test0113() {
		check(//
				"Integrate[1/Sqrt[1 + x^2], x]", //
				"ArcSinh[x]", //
				215);
	}

	// {215}
	public void test0114() {
		check(//
				"Integrate[1/Sqrt[9 + 4*x^2], x]", //
				"ArcSinh[(2*x)/3]/2", //
				215);
	}

	// {215}
	public void test0115() {
		check(//
				"Integrate[1/Sqrt[4 + x^2], x]", //
				"ArcSinh[x/2]", //
				215);
	}

	// {6349}
	public void test0116() {
		check(//
				"Integrate[Erf[x], x]", //
				"1/(E^x^2*Sqrt[Pi]) + x*Erf[x]", //
				6349);
	}

	// {6349}
	public void test0117() {
		check(//
				"Integrate[Erf[a + x], x]", //
				"1/(E^(a + x)^2*Sqrt[Pi]) + (a + x)*Erf[a + x]", //
				6349);
	}

	// {6684}
	public void test0118() {
		check(//
				"Integrate[(x*(-Sqrt[-4 + x^2] + x^2*Sqrt[-4 + x^2] - 4*Sqrt[-1 + x^2] + x^2*Sqrt[-1 + x^2]))/((4 - 5*x^2 + x^4)*(1 + Sqrt[-4 + x^2] + Sqrt[-1 + x^2])), x]", //
				"Log[1 + Sqrt[-4 + x^2] + Sqrt[-1 + x^2]]", //
				6684);
	}

	// {2288}
	public void test0120() {
		check(//
				"Integrate[(E^(1 + Log[x]^(-1))*(-1 + Log[x]^2))/Log[x]^2, x]", //
				"E^(1 + Log[x]^(-1))*x", //
				2288);
	}

	// {2637}
	public void test0121() {
		check(//
				"Integrate[Cos[x], x]", //
				"Sin[x]", //
				2637);
	}

	// {2209}
	public void test0122() {
		check(//
				"Integrate[E^x^2*x, x]", //
				"E^x^2/2", //
				2209);
	}

	// {261}
	public void test0123() {
		check(//
				"Integrate[x*Sqrt[1 + x^2], x]", //
				"(1 + x^2)^(3/2)/3", //
				261);
	}

	// {4432}
	public void test0124() {
		check(//
				"Integrate[E^x*Sin[x], x]", //
				"-(E^x*Cos[x])/2 + (E^x*Sin[x])/2", //
				4432);
	}

	// {3299}
	public void test0125() {
		check(//
				"Integrate[Sin[y]/y, y]", //
				"SinIntegral[y]", //
				3299);
	}

	// {2209}
	public void test0126() {
		check(//
				"Integrate[E^x^2*x, x]", //
				"E^x^2/2", //
				2209);
	}

	// {261}
	public void test0127() {
		check(//
				"Integrate[x*Sqrt[1 + x^2], x]", //
				"(1 + x^2)^(3/2)/3", //
				261);
	}

	// {30}
	public void test0128() {
		check(//
				"Integrate[x^(3/2), x]", //
				"(2*x^(5/2))/5", //
				30);
	}

	// {2637}
	public void test0129() {
		check(//
				"Integrate[Cos[3 + 2*x], x]", //
				"Sin[3 + 2*x]/2", //
				2637);
	}

	// {2194}
	public void test0130() {
		check(//
				"Integrate[(10*E)^x, x]", //
				"(10*E)^x/(1 + Log[10])", //
				2194);
	}

	// {2648}
	public void test0131() {
		check(//
				"Integrate[(1 + Cos[x])^(-1), x]", //
				"Sin[x]/(1 + Cos[x])", //
				2648);
	}

	// {2197}
	public void test0132() {
		check(//
				"Integrate[(E^x*x)/(1 + x)^2, x]", //
				"E^x/(1 + x)", //
				2197);
	}

	// {2204}
	public void test0133() {
		check(//
				"Integrate[E^x^2, x]", //
				"(Sqrt[Pi]*Erfi[x])/2", //
				2204);
	}

	// {2178}
	public void test0134() {
		check(//
				"Integrate[E^x/x, x]", //
				"ExpIntegralEi[x]", //
				2178);
	}

	// {208}
	public void test0135() {
		check(//
				"Integrate[(A^4 - A^2*B^2 + (-A^2 + B^2)*x^2)^(-1), x]", //
				"ArcTanh[x/A]/(A*(A^2 - B^2))", //
				208);
	}

	// {2304}
	public void test0136() {
		check(//
				"Integrate[x*Log[x], x]", //
				"-x^2/4 + (x^2*Log[x])/2", //
				2304);
	}

	// {2298}
	public void test0137() {
		check(//
				"Integrate[Log[x]^(-1), x]", //
				"LogIntegral[x]", //
				2298);
	}

	// {2194}
	public void test0138() {
		check(//
				"Integrate[E^(-1 - x), x]", //
				"-E^(-1 - x)", //
				2194);
	}

	// {4432}
	public void test0139() {
		check(//
				"Integrate[E^x*Sin[x], x]", //
				"-(E^x*Cos[x])/2 + (E^x*Sin[x])/2", //
				4432);
	}

	// {29}
	public void test0140() {
		check(//
				"Integrate[x^(-1), x]", //
				"Log[x]", //
				29);
	}

	// {2648}
	public void test0141() {
		check(//
				"Integrate[(1 - Cos[x])^(-1), x]", //
				"-(Sin[x]/(1 - Cos[x]))", //
				2648);
	}

	// {2304}
	public void test0142() {
		check(//
				"Integrate[x*Log[x], x]", //
				"-x^2/4 + (x^2*Log[x])/2", //
				2304);
	}

	// {8}
	public void test0143() {
		check(//
				"Integrate[1/(r*Sqrt[-a^2 + 2*H*r^2]), x]", //
				"x/(r*Sqrt[-a^2 + 2*H*r^2])", //
				8);
	}

	// {8}
	public void test0144() {
		check(//
				"Integrate[1/(r*Sqrt[-a^2 - e^2 + 2*H*r^2]), x]", //
				"x/(r*Sqrt[-a^2 - e^2 + 2*H*r^2])", //
				8);
	}

	// {8}
	public void test0145() {
		check(//
				"Integrate[1/(r*Sqrt[-a^2 + 2*H*r^2 - 2*K*r^4]), x]", //
				"x/(r*Sqrt[-a^2 + 2*H*r^2 - 2*K*r^4])", //
				8);
	}

	// {8}
	public void test0146() {
		check(//
				"Integrate[1/(r*Sqrt[-a^2 - e^2 + 2*H*r^2 - 2*K*r^4]), x]", //
				"x/(r*Sqrt[-a^2 - e^2 + 2*H*r^2 - 2*K*r^4])", //
				8);
	}

	// {8}
	public void test0147() {
		check(//
				"Integrate[1/(r*Sqrt[-a^2 - 2*K*r + 2*H*r^2]), x]", //
				"x/(r*Sqrt[-a^2 - 2*r*(K - H*r)])", //
				8);
	}

	// {8}
	public void test0148() {
		check(//
				"Integrate[1/(r*Sqrt[-a^2 - e^2 - 2*K*r + 2*H*r^2]), x]", //
				"x/(r*Sqrt[-a^2 - e^2 - 2*r*(K - H*r)])", //
				8);
	}

	// {8}
	public void test0149() {
		check(//
				"Integrate[r/Sqrt[-a^2 + 2*E*r^2], x]", //
				"(r*x)/Sqrt[-a^2 + 2*E*r^2]", //
				8);
	}

	// {8}
	public void test0150() {
		check(//
				"Integrate[r/Sqrt[-a^2 - e^2 + 2*E*r^2], x]", //
				"(r*x)/Sqrt[-a^2 - e^2 + 2*E*r^2]", //
				8);
	}

	// {8}
	public void test0151() {
		check(//
				"Integrate[r/Sqrt[-a^2 + 2*E*r^2 - 2*K*r^4], x]", //
				"(r*x)/Sqrt[-a^2 + 2*E*r^2 - 2*K*r^4]", //
				8);
	}

	// {8}
	public void test0152() {
		check(//
				"Integrate[r/Sqrt[-a^2 - e^2 + 2*E*r^2 - 2*K*r^4], x]", //
				"(r*x)/Sqrt[-a^2 - e^2 + 2*E*r^2 - 2*K*r^4]", //
				8);
	}

	// {8}
	public void test0153() {
		check(//
				"Integrate[r/Sqrt[-a^2 - e^2 - 2*K*r + 2*H*r^2], x]", //
				"(r*x)/Sqrt[-a^2 - e^2 - 2*r*(K - H*r)]", //
				8);
	}

	// {30}
	public void test0154() {
		check(//
				"Integrate[x^n, x]", //
				"x^(1 + n)/(1 + n)", //
				30);
	}

	// {2194}
	public void test0155() {
		check(//
				"Integrate[E^x, x]", //
				"E^x", //
				2194);
	}

	// {29}
	public void test0156() {
		check(//
				"Integrate[x^(-1), x]", //
				"Log[x]", //
				29);
	}

	// {2194}
	public void test0157() {
		check(//
				"Integrate[a^x, x]", //
				"a^x/Log[a]", //
				2194);
	}

	// {2638}
	public void test0158() {
		check(//
				"Integrate[Sin[x], x]", //
				"-Cos[x]", //
				2638);
	}

	// {2637}
	public void test0159() {
		check(//
				"Integrate[Cos[x], x]", //
				"Sin[x]", //
				2637);
	}

	// {2638}
	public void test0160() {
		check(//
				"Integrate[Sinh[x], x]", //
				"Cosh[x]", //
				2638);
	}

	// {2637}
	public void test0161() {
		check(//
				"Integrate[Cosh[x], x]", //
				"Sinh[x]", //
				2637);
	}

	// {3475}
	public void test0162() {
		check(//
				"Integrate[Tan[x], x]", //
				"-Log[Cos[x]]", //
				3475);
	}

	// {3475}
	public void test0163() {
		check(//
				"Integrate[Cot[x], x]", //
				"Log[Sin[x]]", //
				3475);
	}

	// {2295}
	public void test0164() {
		check(//
				"Integrate[Log[x], x]", //
				"-x + x*Log[x]", //
				2295);
	}

	// {4432}
	public void test0165() {
		check(//
				"Integrate[E^x*Sin[x], x]", //
				"-(E^x*Cos[x])/2 + (E^x*Sin[x])/2", //
				4432);
	}

	// {2304}
	public void test0166() {
		check(//
				"Integrate[x*Log[x], x]", //
				"-x^2/4 + (x^2*Log[x])/2", //
				2304);
	}

	// {2304}
	public void test0167() {
		check(//
				"Integrate[t^2*Log[t], t]", //
				"-t^3/9 + (t^3*Log[t])/3", //
				2304);
	}

	// {4432}
	public void test0168() {
		check(//
				"Integrate[E^(2*t)*Sin[3*t], t]", //
				"(-3*E^(2*t)*Cos[3*t])/13 + (2*E^(2*t)*Sin[3*t])/13", //
				4432);
	}

	// {4433}
	public void test0169() {
		check(//
				"Integrate[Cos[3*t]/E^t, t]", //
				"-Cos[3*t]/(10*E^t) + (3*Sin[3*t])/(10*E^t)", //
				4433);
	}

	// {2304}
	public void test0170() {
		check(//
				"Integrate[Sqrt[t]*Log[t], t]", //
				"(-4*t^(3/2))/9 + (2*t^(3/2)*Log[t])/3", //
				2304);
	}

	// {4284}
	public void test0171() {
		check(//
				"Integrate[Cos[5*x]*Sin[3*x], x]", //
				"Cos[2*x]/4 - Cos[8*x]/16", //
				4284);
	}

	// {4282}
	public void test0172() {
		check(//
				"Integrate[Sin[2*x]*Sin[4*x], x]", //
				"Sin[2*x]/4 - Sin[6*x]/12", //
				4282);
	}

	// {4476}
	public void test0173() {
		check(//
				"Integrate[Cos[Log[x]], x]", //
				"(x*Cos[Log[x]])/2 + (x*Sin[Log[x]])/2", //
				4476);
	}

	// {2295}
	public void test0174() {
		check(//
				"Integrate[Log[Sqrt[x]], x]", //
				"-x/2 + x*Log[Sqrt[x]]", //
				2295);
	}

	// {4475}
	public void test0175() {
		check(//
				"Integrate[Sin[Log[x]], x]", //
				"-(x*Cos[Log[x]])/2 + (x*Sin[Log[x]])/2", //
				4475);
	}

	// {2304}
	public void test0176() {
		check(//
				"Integrate[Sqrt[x]*Log[x], x]", //
				"(-4*x^(3/2))/9 + (2*x^(3/2)*Log[x])/3", //
				2304);
	}

	// {2644}
	public void test0177() {
		check(//
				"Integrate[(1 - Sin[2*x])^2, x]", //
				"(3*x)/2 + Cos[2*x] - (Cos[2*x]*Sin[2*x])/4", //
				2644);
	}

	// {2648}
	public void test0178() {
		check(//
				"Integrate[(1 - Sin[x])^(-1), x]", //
				"Cos[x]/(1 - Sin[x])", //
				2648);
	}

	// {3770}
	public void test0179() {
		check(//
				"Integrate[Csc[x], x]", //
				"-ArcTanh[Cos[x]]", //
				3770);
	}

	// {4282}
	public void test0180() {
		check(//
				"Integrate[Sin[2*x]*Sin[5*x], x]", //
				"Sin[3*x]/6 - Sin[7*x]/14", //
				4282);
	}

	// {4284}
	public void test0181() {
		check(//
				"Integrate[Cos[x]*Sin[3*x], x]", //
				"-Cos[2*x]/4 - Cos[4*x]/8", //
				4284);
	}

	// {4283}
	public void test0182() {
		check(//
				"Integrate[Cos[3*x]*Cos[4*x], x]", //
				"Sin[x]/2 + Sin[7*x]/14", //
				4283);
	}

	// {4282}
	public void test0183() {
		check(//
				"Integrate[Sin[3*x]*Sin[6*x], x]", //
				"Sin[3*x]/6 - Sin[9*x]/18", //
				4282);
	}

	// {264}
	public void test0184() {
		check(//
				"Integrate[1/(x^2*Sqrt[4 + x^2]), x]", //
				"-Sqrt[4 + x^2]/(4*x)", //
				264);
	}

	// {261}
	public void test0185() {
		check(//
				"Integrate[x/Sqrt[4 + x^2], x]", //
				"Sqrt[4 + x^2]", //
				261);
	}

	// {264}
	public void test0186() {
		check(//
				"Integrate[1/(x^2*Sqrt[1 - x^2]), x]", //
				"-(Sqrt[1 - x^2]/x)", //
				264);
	}

	// {261}
	public void test0187() {
		check(//
				"Integrate[x/Sqrt[1 - x^2], x]", //
				"-Sqrt[1 - x^2]", //
				261);
	}

	// {261}
	public void test0188() {
		check(//
				"Integrate[x*Sqrt[4 - x^2], x]", //
				"-(4 - x^2)^(3/2)/3", //
				261);
	}

	// {215}
	public void test0189() {
		check(//
				"Integrate[1/Sqrt[9 + x^2], x]", //
				"ArcSinh[x/3]", //
				215);
	}

	// {264}
	public void test0190() {
		check(//
				"Integrate[Sqrt[-a^2 + x^2]/x^4, x]", //
				"(-a^2 + x^2)^(3/2)/(3*a^2*x^3)", //
				264);
	}

	// {264}
	public void test0191() {
		check(//
				"Integrate[1/(x^2*Sqrt[-9 + 16*x^2]), x]", //
				"Sqrt[-9 + 16*x^2]/(9*x)", //
				264);
	}

	// {261}
	public void test0192() {
		check(//
				"Integrate[x/(4 + x^2)^(5/2), x]", //
				"-1/(3*(4 + x^2)^(3/2))", //
				261);
	}

	// {191}
	public void test0193() {
		check(//
				"Integrate[(-25 + 4*x^2)^(-3/2), x]", //
				"-x/(25*Sqrt[-25 + 4*x^2])", //
				191);
	}

	// {1587}
	public void test0194() {
		check(//
				"Integrate[(2*x + x^2)/(4 + 3*x^2 + x^3), x]", //
				"Log[4 + 3*x^2 + x^3]/3", //
				1587);
	}

	// {1587}
	public void test0195() {
		check(//
				"Integrate[(-x + 2*x^3)/(1 - x^2 + x^4), x]", //
				"Log[1 - x^2 + x^4]/2", //
				1587);
	}

	// {260}
	public void test0196() {
		check(//
				"Integrate[x/(-1 + x^2), x]", //
				"Log[1 - x^2]/2", //
				260);
	}

	// {2648}
	public void test0197() {
		check(//
				"Integrate[(1 - Cos[x])^(-1), x]", //
				"-(Sin[x]/(1 - Cos[x]))", //
				2648);
	}

	// {3133}
	public void test0198() {
		check(//
				"Integrate[(-Cos[x] + Sin[x])/(Cos[x] + Sin[x]), x]", //
				"-Log[Cos[x] + Sin[x]]", //
				3133);
	}

	// {261}
	public void test0199() {
		check(//
				"Integrate[x/Sqrt[1 - x^2], x]", //
				"-Sqrt[1 - x^2]", //
				261);
	}

	// {2304}
	public void test0200() {
		check(//
				"Integrate[x^3*Log[x], x]", //
				"-x^4/16 + (x^4*Log[x])/4", //
				2304);
	}

	// {1587}
	public void test0201() {
		check(//
				"Integrate[(1 + x + x^3)/(4*x + 2*x^2 + x^4), x]", //
				"Log[4*x + 2*x^2 + x^4]/4", //
				1587);
	}

	// {2638}
	public void test0202() {
		check(//
				"Integrate[Sin[Pi*x], x]", //
				"-(Cos[Pi*x]/Pi)", //
				2638);
	}

	// {4433}
	public void test0203() {
		check(//
				"Integrate[E^(3*x)*Cos[5*x], x]", //
				"(3*E^(3*x)*Cos[5*x])/34 + (5*E^(3*x)*Sin[5*x])/34", //
				4433);
	}

	// {4283}
	public void test0204() {
		check(//
				"Integrate[Cos[3*x]*Cos[5*x], x]", //
				"Sin[2*x]/4 + Sin[8*x]/16", //
				4283);
	}

	// {6686}
	public void test0205() {
		// Int[(u_)*(y_)^(m_.), x_Symbol] :=
		// With[{q = DerivativeDivides[y, u, x]}, Simp[(q*y^(m + 1))/(m + 1), x]
		// /; !FalseQ[q]] /; FreeQ[m, x] && NeQ[m, -1]
		
		
		// check("Integrate::DerivativeDivides[Log[Tan[x]]*Sec[x],Csc[x]*Sec[x]^((-1)*1+1),x]", //
		// "");
		check(//
				"Integrate[Csc[x]*Log[Tan[x]]*Sec[x], x]", //
				"Log[Tan[x]]^2/2", //
				6686);
	}

	// {216}
	public void test0207() {
		check(//
				"Integrate[1/Sqrt[16 - x^2], x]", //
				"ArcSin[x/4]", //
				216);
	}

	// {5071}
	public void test0208() {
		check(//
				"Integrate[E^ArcTan[x]/(1 + x^2), x]", //
				"E^ArcTan[x]", //
				5071);
	}

	// {2295}
	public void test0209() {
		check(//
				"Integrate[Log[x/2], x]", //
				"-x + x*Log[x/2]", //
				2295);
	}

	// {261}
	public void test0210() {
		check(//
				"Integrate[x*(5 + x^2)^8, x]", //
				"(5 + x^2)^9/18", //
				261);
	}

	// {4433}
	public void test0211() {
		check(//
				"Integrate[Cos[4*x]/E^(3*x), x]", //
				"(-3*Cos[4*x])/(25*E^(3*x)) + (4*Sin[4*x])/(25*E^(3*x))", //
				4433);
	}

	// {4433}
	public void test0212() {
		check(//
				"Integrate[E^x*Cos[4 + 3*x], x]", //
				"(E^x*Cos[4 + 3*x])/10 + (3*E^x*Sin[4 + 3*x])/10", //
				4433);
	}

	// {261}
	public void test0213() {
		check(//
				"Integrate[x^2*(1 + x^3)^4, x]", //
				"(1 + x^3)^5/15", //
				261);
	}

	// {208}
	public void test0214() {
		check(//
				"Integrate[(a^2 - b^2*x^2)^(-1), x]", //
				"ArcTanh[(b*x)/a]/(a*b)", //
				208);
	}

	// {205}
	public void test0215() {
		check(//
				"Integrate[(a^2 + b^2*x^2)^(-1), x]", //
				"ArcTan[(b*x)/a]/(a*b)", //
				205);
	}

	// {3770}
	public void test0216() {
		check(//
				"Integrate[Sec[2*a*x], x]", //
				"ArcTanh[Sin[2*a*x]]/(2*a)", //
				3770);
	}

	// {3770}
	public void test0217() {
		check(//
				"Integrate[-Sec[Pi/4 + 2*x], x]", //
				"-ArcTanh[Sin[Pi/4 + 2*x]]/2", //
				3770);
	}

	// {2648}
	public void test0218() {
		check(//
				"Integrate[(1 + Cos[x])^(-1), x]", //
				"Sin[x]/(1 + Cos[x])", //
				2648);
	}

	// {2648}
	public void test0219() {
		check(//
				"Integrate[(1 - Cos[x])^(-1), x]", //
				"-(Sin[x]/(1 - Cos[x]))", //
				2648);
	}

	// {4282}
	public void test0220() {
		check(//
				"Integrate[Sin[x/4]*Sin[x], x]", //
				"(2*Sin[(3*x)/4])/3 - (2*Sin[(5*x)/4])/5", //
				4282);
	}

	// {4283}
	public void test0221() {
		check(//
				"Integrate[Cos[3*x]*Cos[4*x], x]", //
				"Sin[x]/2 + Sin[7*x]/14", //
				4283);
	}

	// {4884}
	public void test0222() {
		check(//
				"Integrate[ArcTan[x]^n/(1 + x^2), x]", //
				"ArcTan[x]^(1 + n)/(1 + n)", //
				4884);
	}

	// {4642}
	public void test0223() {
		check(//
				"Integrate[1/(Sqrt[1 - x^2]*ArcCos[x]^3), x]", //
				"1/(2*ArcCos[x]^2)", //
				4642);
	}

	// {2304}
	public void test0224() {
		check(//
				"Integrate[Log[x]/x^5, x]", //
				"-1/(16*x^4) - Log[x]/(4*x^4)", //
				2304);
	}

	// {4432}
	public void test0225() {
		check(//
				"Integrate[Sin[x]/E^x, x]", //
				"-Cos[x]/(2*E^x) - Sin[x]/(2*E^x)", //
				4432);
	}

	// {4432}
	public void test0226() {
		check(//
				"Integrate[E^(2*x)*Sin[3*x], x]", //
				"(-3*E^(2*x)*Cos[3*x])/13 + (2*E^(2*x)*Sin[3*x])/13", //
				4432);
	}

	// {4433}
	public void test0227() {
		check(//
				"Integrate[a^x*Cos[x], x]", //
				"(a^x*Cos[x]*Log[a])/(1 + Log[a]^2) + (a^x*Sin[x])/(1 + Log[a]^2)", //
				4433);
	}

	// {4476}
	public void test0228() {
		check(//
				"Integrate[Cos[Log[x]], x]", //
				"(x*Cos[Log[x]])/2 + (x*Sin[Log[x]])/2", //
				4476);
	}

	// {261}
	public void test0229() {
		check(//
				"Integrate[x^(-1 + k)*(a + b*x^k)^n, x]", //
				"(a + b*x^k)^(1 + n)/(b*k*(1 + n))", //
				261);
	}

	// {260}
	public void test0230() {
		check(//
				"Integrate[x^2/(a^3 + x^3), x]", //
				"Log[a^3 + x^3]/3", //
				260);
	}

	// {364}
	public void test0231() {
		check(//
				"Integrate[1/(x^m*(a^3 + x^3)), x]", //
				"(x^(1 - m)*Hypergeometric2F1[1, (1 - m)/3, (4 - m)/3, -(x^3/a^3)])/(a^3*(1 - m))", //
				364);
	}

	// {364}
	public void test0232() {
		check(//
				"Integrate[1/(x^m*(a^4 - x^4)), x]", //
				"(x^(1 - m)*Hypergeometric2F1[1, (1 - m)/4, (5 - m)/4, x^4/a^4])/(a^4*(1 - m))", //
				364);
	}

	// {260}
	public void test0233() {
		check(//
				"Integrate[x^4/(a^5 + x^5), x]", //
				"Log[a^5 + x^5]/5", //
				260);
	}

	// {364}
	public void test0234() {
		check(//
				"Integrate[1/(x^m*(a^5 + x^5)), x]", //
				"(x^(1 - m)*Hypergeometric2F1[1, (1 - m)/5, (6 - m)/5, -(x^5/a^5)])/(a^5*(1 - m))", //
				364);
	}

	// {261}
	public void test0235() {
		check(//
				"Integrate[x^3/(a^4 + x^4)^3, x]", //
				"-1/(8*(a^4 + x^4)^2)", //
				261);
	}

	// {613}
	public void test0236() {
		check(//
				"Integrate[(1 + x + x^2)^(-3/2), x]", //
				"(2*(1 + 2*x))/(3*Sqrt[1 + x + x^2])", //
				613);
	}

	// {636}
	public void test0237() {
		check(//
				"Integrate[x/(1 + x + x^2)^(3/2), x]", //
				"(-2*(2 + x))/(3*Sqrt[1 + x + x^2])", //
				636);
	}

	// {261}
	public void test0238() {
		check(//
				"Integrate[1/((1 - 3/x)^(4/3)*x^2), x]", //
				"-(1 - 3/x)^(-1/3)", //
				261);
	}

	// {261}
	public void test0239() {
		check(//
				"Integrate[x^6*(1 + x^7)^(1/3), x]", //
				"(3*(1 + x^7)^(4/3))/28", //
				261);
	}

	// {261}
	public void test0240() {
		check(//
				"Integrate[x^6/(1 + x^7)^(5/3), x]", //
				"-3/(14*(1 + x^7)^(2/3))", //
				261);
	}

	// {1588}
	public void test0241() {
		check(//
				"Integrate[(-3*x + 2*x^3)*(-3*x^2 + x^4)^(3/5), x]", //
				"(5*(-3*x^2 + x^4)^(8/5))/16", //
				1588);
	}

	// {1590}
	public void test0242() {
		check(//
				"Integrate[(-1 + x^4)/(x^2*Sqrt[1 + x^2 + x^4]), x]", //
				"Sqrt[1 + x^2 + x^4]/x", //
				1590);
	}

	// {2084}
	public void test0243() {
		check(//
				"Integrate[(1 - x^2)/((1 + 2*a*x + x^2)*Sqrt[1 + 2*a*x + 2*b*x^2 + 2*a*x^3 + x^4]), x]", //
				"ArcTan[(a + 2*(1 + a^2 - b)*x + a*x^2)/(Sqrt[2]*Sqrt[1 - b]*Sqrt[1 + 2*a*x + 2*b*x^2 + 2*a*x^3 + x^4])]/(Sqrt[2]*Sqrt[1 - b])", //
				2084);
	}

	// {2577}
	public void test0244() {
		check(//
				"Integrate[Cos[x]^(2*m)*Sin[x]^(2*m), x]", //
				"(Cos[x]^(-1 + 2*m)*(Cos[x]^2)^(1/2 - m)*Hypergeometric2F1[(1 - 2*m)/2, (1 + 2*m)/2, (3 + 2*m)/2, Sin[x]^2]*Sin[x]^(1 + 2*m))/(1 + 2*m)", //
				2577);
	}

	// {4283}
	public void test0245() {
		check(//
				"Integrate[Cos[x]*Cos[4*x], x]", //
				"Sin[3*x]/6 + Sin[5*x]/10", //
				4283);
	}

	// {2646}
	public void test0246() {
		check(//
				"Integrate[Sqrt[1 + Sin[2*x]], x]", //
				"-(Cos[2*x]/Sqrt[1 + Sin[2*x]])", //
				2646);
	}

	// {2646}
	public void test0247() {
		check(//
				"Integrate[Sqrt[1 - Sin[2*x]], x]", //
				"Cos[2*x]/Sqrt[1 - Sin[2*x]]", //
				2646);
	}

	// {4306}
	public void test0248() {
		check(//
				"Integrate[Sin[x]/Sqrt[Sin[2*x]], x]", //
				"-ArcSin[Cos[x] - Sin[x]]/2 - Log[Cos[x] + Sin[x] + Sqrt[Sin[2*x]]]/2", //
				4306);
	}

	// {4305}
	public void test0249() {
		check(//
				"Integrate[Cos[x]/Sqrt[Sin[2*x]], x]", //
				"-ArcSin[Cos[x] - Sin[x]]/2 + Log[Cos[x] + Sin[x] + Sqrt[Sin[2*x]]]/2", //
				4305);
	}

	// {4292}
	public void test0250() {
		check(//
				"Integrate[Csc[x]^5*Sin[2*x]^(3/2), x]", //
				"-(Csc[x]^5*Sin[2*x]^(5/2))/5", //
				4292);
	}

	// {4331}
	public void test0251() {
		check(//
				"Integrate[Sin[x]/Cos[2*x]^(5/2), x]", //
				"-Cos[3*x]/(3*Cos[2*x]^(3/2))", //
				4331);
	}

	// {30}
	public void test0252() {
		check(//
				"Integrate[x^(1 + 2*n), x]", //
				"x^(2*(1 + n))/(2*(1 + n))", //
				30);
	}

	// {2288}
	public void test0253() {
		check(//
				"Integrate[(E^x*(1 - x - x^2))/Sqrt[1 - x^2], x]", //
				"E^x*Sqrt[1 - x^2]", //
				2288);
	}

	// {4433}
	public void test0254() {
		check(//
				"Integrate[Cos[2*x]/E^(3*x), x]", //
				"(-3*Cos[2*x])/(13*E^(3*x)) + (2*Sin[2*x])/(13*E^(3*x))", //
				4433);
	}

	// {4453}
	public void test0255() {
		check(//
				"Integrate[E^(m*x)*Csc[x]^2, x]", //
				"(-4*E^((2*I + m)*x)*Hypergeometric2F1[2, 1 - (I/2)*m, 2 - (I/2)*m, E^((2*I)*x)])/(2*I + m)", //
				4453);
	}

	// {2288}
	public void test0256() {
		check(//
				"Integrate[(E^x*(1 - Sin[x]))/(1 - Cos[x]), x]", //
				"-((E^x*Sin[x])/(1 - Cos[x]))", //
				2288);
	}

	// {2288}
	public void test0257() {
		check(//
				"Integrate[(E^x*(1 + Sin[x]))/(1 + Cos[x]), x]", //
				"(E^x*Sin[x])/(1 + Cos[x])", //
				2288);
	}

	// {2288}
	public void test0258() {
		check(//
				"Integrate[(E^x*(1 + Cos[x]))/(1 - Sin[x]), x]", //
				"(E^x*Cos[x])/(1 - Sin[x])", //
				2288);
	}

	// {2288}
	public void test0259() {
		check(//
				"Integrate[(E^x*(1 - Cos[x]))/(1 + Sin[x]), x]", //
				"-((E^x*Cos[x])/(1 + Sin[x]))", //
				2288);
	}

	// {2637}
	public void test0260() {
		check(//
				"Integrate[Cosh[x], x]", //
				"Sinh[x]", //
				2637);
	}

	// {2638}
	public void test0261() {
		check(//
				"Integrate[Sinh[x], x]", //
				"Cosh[x]", //
				2638);
	}

	// {3475}
	public void test0262() {
		check(//
				"Integrate[Tanh[x], x]", //
				"Log[Cosh[x]]", //
				3475);
	}

	// {3475}
	public void test0263() {
		check(//
				"Integrate[Coth[x], x]", //
				"Log[Sinh[x]]", //
				3475);
	}

	// {3770}
	public void test0264() {
		check(//
				"Integrate[Sech[x], x]", //
				"ArcTan[Sinh[x]]", //
				3770);
	}

	// {3770}
	public void test0265() {
		check(//
				"Integrate[Csch[x], x]", //
				"-ArcTanh[Cosh[x]]", //
				3770);
	}

	// {2304}
	public void test0266() {
		check(//
				"Integrate[x^m*Log[x], x]", //
				"-(x^(1 + m)/(1 + m)^2) + (x^(1 + m)*Log[x])/(1 + m)", //
				2304);
	}

	// {2521}
	public void test0267() {
		check(//
				"Integrate[Log[Log[x]]/x, x]", //
				"-Log[x] + Log[x]*Log[Log[x]]", //
				2521);
	}

	// {32}
	public void test0268() {
		check(//
				"Integrate[1/Sqrt[1 - a*x], x]", //
				"(-2*Sqrt[1 - a*x])/a", //
				32);
	}

	// {239}
	public void test0269() {
		check(//
				"Integrate[(1 - x^3)^(-1/3), x]", //
				"-(ArcTan[(1 - (2*x)/(1 - x^3)^(1/3))/Sqrt[3]]/Sqrt[3]) + Log[x + (1 - x^3)^(1/3)]/2", //
				239);
	}

	// {1587}
	public void test0270() {
		check(//
				"Integrate[(3 - 3*x + 30*x^2 + 160*x^3)/(9 + 24*x - 12*x^2 + 80*x^3 + 320*x^4), x]", //
				"Log[9 + 24*x - 12*x^2 + 80*x^3 + 320*x^4]/8", //
				1587);
	}

	// {2090}
	public void test0271() {
		check(//
				"Integrate[(3 + 12*x + 20*x^2)/(9 + 24*x - 12*x^2 + 80*x^3 + 320*x^4), x]", //
				"-ArcTan[(7 - 40*x)/(5*Sqrt[11])]/(2*Sqrt[11]) + ArcTan[(57 + 30*x - 40*x^2 + 800*x^3)/(6*Sqrt[11])]/(2*Sqrt[11])", //
				2090);
	}

	// {405}
	public void test0272() {
		check(//
				"Integrate[Sqrt[1 - x^4]/(1 + x^4), x]", //
				"ArcTan[(x*(1 + x^2))/Sqrt[1 - x^4]]/2 + ArcTanh[(x*(1 - x^2))/Sqrt[1 - x^4]]/2", //
				405);
	}

	// {2072}
	public void test0273() {
		check(//
				"Integrate[Sqrt[1 + p*x^2 - x^4]/(1 + x^4), x]", //
				"-(Sqrt[p + Sqrt[4 + p^2]]*ArcTan[(Sqrt[p + Sqrt[4 + p^2]]*x*(p - Sqrt[4 + p^2] - 2*x^2))/(2*Sqrt[2]*Sqrt[1 + p*x^2 - x^4])])/(2*Sqrt[2]) + (Sqrt[-p + Sqrt[4 + p^2]]*ArcTanh[(Sqrt[-p + Sqrt[4 + p^2]]*x*(p + Sqrt[4 + p^2] - 2*x^2))/(2*Sqrt[2]*Sqrt[1 + p*x^2 - x^4])])/(2*Sqrt[2])", //
				2072);
	}

	// {484}
	public void test0274() {
		check(//
				"Integrate[x/(Sqrt[1 - x^3]*(4 - x^3)), x]", //
				"-ArcTan[(Sqrt[3]*(1 - 2^(1/3)*x))/Sqrt[1 - x^3]]/(3*2^(2/3)*Sqrt[3]) + ArcTan[Sqrt[1 - x^3]/Sqrt[3]]/(3*2^(2/3)*Sqrt[3]) - ArcTanh[(1 + 2^(1/3)*x)/Sqrt[1 - x^3]]/(3*2^(2/3)) + ArcTanh[Sqrt[1 - x^3]]/(9*2^(2/3))", //
				484);
	}

	// {485}
	public void test0275() {
		check(//
				"Integrate[x/((4 - d*x^3)*Sqrt[-1 + d*x^3]), x]", //
				"-ArcTan[(1 + 2^(1/3)*d^(1/3)*x)/Sqrt[-1 + d*x^3]]/(3*2^(2/3)*d^(2/3)) - ArcTan[Sqrt[-1 + d*x^3]]/(9*2^(2/3)*d^(2/3)) - ArcTanh[(Sqrt[3]*(1 - 2^(1/3)*d^(1/3)*x))/Sqrt[-1 + d*x^3]]/(3*2^(2/3)*Sqrt[3]*d^(2/3)) - ArcTanh[Sqrt[-1 + d*x^3]/Sqrt[3]]/(3*2^(2/3)*Sqrt[3]*d^(2/3))", //
				485);
	}

	// {395}
	public void test0276() {
		check(//
				"Integrate[1/((1 - 3*x^2)^(1/3)*(3 - x^2)), x]", //
				"ArcTan[(1 - (1 - 3*x^2)^(1/3))/x]/4 + ArcTanh[x/Sqrt[3]]/(4*Sqrt[3]) - ArcTanh[(1 - (1 - 3*x^2)^(1/3))^2/(3*Sqrt[3]*x)]/(4*Sqrt[3])", //
				395);
	}

	// {394}
	public void test0277() {
		check(//
				"Integrate[1/((3 + x^2)*(1 + 3*x^2)^(1/3)), x]", //
				"ArcTan[x/Sqrt[3]]/(4*Sqrt[3]) + ArcTan[(1 - (1 + 3*x^2)^(1/3))^2/(3*Sqrt[3]*x)]/(4*Sqrt[3]) - ArcTanh[(1 - (1 + 3*x^2)^(1/3))/x]/4", //
				394);
	}

	// {393}
	public void test0278() {
		check(//
				"Integrate[1/((1 - x^2)^(1/3)*(3 + x^2)), x]", //
				"ArcTan[Sqrt[3]/x]/(2*2^(2/3)*Sqrt[3]) + ArcTan[(Sqrt[3]*(1 - 2^(1/3)*(1 - x^2)^(1/3)))/x]/(2*2^(2/3)*Sqrt[3]) - ArcTanh[x]/(6*2^(2/3)) + ArcTanh[x/(1 + 2^(1/3)*(1 - x^2)^(1/3))]/(2*2^(2/3))", //
				393);
	}

	// {392}
	public void test0279() {
		check(//
				"Integrate[1/((3 - x^2)*(1 + x^2)^(1/3)), x]", //
				"-ArcTan[x]/(6*2^(2/3)) + ArcTan[x/(1 + 2^(1/3)*(1 + x^2)^(1/3))]/(2*2^(2/3)) - ArcTanh[Sqrt[3]/x]/(2*2^(2/3)*Sqrt[3]) - ArcTanh[(Sqrt[3]*(1 - 2^(1/3)*(1 + x^2)^(1/3)))/x]/(2*2^(2/3)*Sqrt[3])", //
				392);
	}

	// {487}
	public void test0280() {
		check(//
				"Integrate[x/(Sqrt[1 + x^3]*(10 + 6*Sqrt[3] + x^3)), x]", //
				"-((2 - Sqrt[3])*ArcTan[(3^(1/4)*(1 + Sqrt[3])*(1 + x))/(Sqrt[2]*Sqrt[1 + x^3])])/(2*Sqrt[2]*3^(3/4)) - ((2 - Sqrt[3])*ArcTan[((1 - Sqrt[3])*Sqrt[1 + x^3])/(Sqrt[2]*3^(3/4))])/(3*Sqrt[2]*3^(3/4)) - ((2 - Sqrt[3])*ArcTanh[(3^(1/4)*(1 + Sqrt[3] - 2*x))/(Sqrt[2]*Sqrt[1 + x^3])])/(3*Sqrt[2]*3^(1/4)) - ((2 - Sqrt[3])*ArcTanh[(3^(1/4)*(1 - Sqrt[3])*(1 + x))/(Sqrt[2]*Sqrt[1 + x^3])])/(6*Sqrt[2]*3^(1/4))", //
				487);
	}

	// {487}
	public void test0281() {
		check(//
				"Integrate[x/(Sqrt[1 + x^3]*(10 - 6*Sqrt[3] + x^3)), x]", //
				"-((2 + Sqrt[3])*ArcTan[(3^(1/4)*(1 - Sqrt[3] - 2*x))/(Sqrt[2]*Sqrt[1 + x^3])])/(3*Sqrt[2]*3^(1/4)) - ((2 + Sqrt[3])*ArcTan[(3^(1/4)*(1 + Sqrt[3])*(1 + x))/(Sqrt[2]*Sqrt[1 + x^3])])/(6*Sqrt[2]*3^(1/4)) + ((2 + Sqrt[3])*ArcTanh[(3^(1/4)*(1 - Sqrt[3])*(1 + x))/(Sqrt[2]*Sqrt[1 + x^3])])/(2*Sqrt[2]*3^(3/4)) + ((2 + Sqrt[3])*ArcTanh[((1 + Sqrt[3])*Sqrt[1 + x^3])/(Sqrt[2]*3^(3/4))])/(3*Sqrt[2]*3^(3/4))", //
				487);
	}

	// {488}
	public void test0282() {
		check(//
				"Integrate[x/(Sqrt[-1 + x^3]*(-10 - 6*Sqrt[3] + x^3)), x]", //
				"((2 - Sqrt[3])*ArcTan[(3^(1/4)*(1 - Sqrt[3])*(1 - x))/(Sqrt[2]*Sqrt[-1 + x^3])])/(6*Sqrt[2]*3^(1/4)) + ((2 - Sqrt[3])*ArcTan[(3^(1/4)*(1 + Sqrt[3] + 2*x))/(Sqrt[2]*Sqrt[-1 + x^3])])/(3*Sqrt[2]*3^(1/4)) + ((2 - Sqrt[3])*ArcTanh[(3^(1/4)*(1 + Sqrt[3])*(1 - x))/(Sqrt[2]*Sqrt[-1 + x^3])])/(2*Sqrt[2]*3^(3/4)) - ((2 - Sqrt[3])*ArcTanh[((1 - Sqrt[3])*Sqrt[-1 + x^3])/(Sqrt[2]*3^(3/4))])/(3*Sqrt[2]*3^(3/4))", //
				488);
	}

	// {488}
	public void test0283() {
		check(//
				"Integrate[x/(Sqrt[-1 + x^3]*(-10 + 6*Sqrt[3] + x^3)), x]", //
				"-((2 + Sqrt[3])*ArcTan[(3^(1/4)*(1 - Sqrt[3])*(1 - x))/(Sqrt[2]*Sqrt[-1 + x^3])])/(2*Sqrt[2]*3^(3/4)) + ((2 + Sqrt[3])*ArcTan[((1 + Sqrt[3])*Sqrt[-1 + x^3])/(Sqrt[2]*3^(3/4))])/(3*Sqrt[2]*3^(3/4)) + ((2 + Sqrt[3])*ArcTanh[(3^(1/4)*(1 + Sqrt[3])*(1 - x))/(Sqrt[2]*Sqrt[-1 + x^3])])/(6*Sqrt[2]*3^(1/4)) + ((2 + Sqrt[3])*ArcTanh[(3^(1/4)*(1 - Sqrt[3] + 2*x))/(Sqrt[2]*Sqrt[-1 + x^3])])/(3*Sqrt[2]*3^(1/4))", //
				488);
	}

	// {2151}
	public void test0284() {
		check(//
				"Integrate[(-1 + x)/((1 + x)*(2 + x^3)^(1/3)), x]", //
				"Sqrt[3]*ArcTan[(1 + (2*(2 + x))/(2 + x^3)^(1/3))/Sqrt[3]] + Log[1 + x] - (3*Log[2 + x - (2 + x^3)^(1/3)])/2", //
				2151);
	}
}
