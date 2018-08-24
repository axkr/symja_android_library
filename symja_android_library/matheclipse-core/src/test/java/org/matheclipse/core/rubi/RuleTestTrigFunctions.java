package org.matheclipse.core.rubi;

public class RuleTestTrigFunctions extends AbstractRubiTestCase {

	public RuleTestTrigFunctions(String name) {
		super(name, false);
	}

	// {2638}
	public void test0001() {
		check("Integrate[Sin[a + b*x], x]", 2638);
	}

	// {2639}
	public void test0002() {
		check("Integrate[Sqrt[Sin[b*x]], x]", 2639);
	}

	// {2641}
	public void test0003() {
		check("Integrate[1/Sqrt[Sin[b*x]], x]", 2641);
	}

	// {2639}
	public void test0004() {
		check("Integrate[Sqrt[Sin[a + b*x]], x]", 2639);
	}

	// {2641}
	public void test0005() {
		check("Integrate[1/Sqrt[Sin[a + b*x]], x]", 2641);
	}

	// {2643}
	public void test0006() {
		check("Integrate[(c*Sin[a + b*x])^(4/3), x]", 2643);
	}

	// {2643}
	public void test0007() {
		check("Integrate[(c*Sin[a + b*x])^(2/3), x]", 2643);
	}

	// {2643}
	public void test0008() {
		check("Integrate[(c*Sin[a + b*x])^(-4/3), x]", 2643);
	}

	// {2643}
	public void test0009() {
		check("Integrate[Sin[a + b*x]^n, x]", 2643);
	}

	// {2643}
	public void test0010() {
		check("Integrate[(c*Sin[a + b*x])^n, x]", 2643);
	}

	// {3475}
	public void test0011() {
		check("Integrate[Tan[a + b*x], x]", 3475);
	}

	// {3475}
	public void test0012() {
		check("Integrate[Cot[a + b*x], x]", 3475);
	}

	// {2563}
	public void test0013() {
		check("Integrate[Sqrt[c*Sin[a + b*x]]/(d*Cos[a + b*x])^(5/2), x]", 2563);
	}

	// {2563}
	public void test0014() {
		check("Integrate[(c*Sin[a + b*x])^(3/2)/(d*Cos[a + b*x])^(7/2), x]", 2563);
	}

	// {2563}
	public void test0015() {
		check("Integrate[(c*Sin[a + b*x])^(5/2)/(d*Cos[a + b*x])^(9/2), x]", 2563);
	}

	// {2563}
	public void test0016() {
		check("Integrate[Sin[x]^(3/2)/Cos[x]^(7/2), x]", 2563);
	}

	// {2563}
	public void test0017() {
		check("Integrate[1/((d*Cos[a + b*x])^(3/2)*Sqrt[c*Sin[a + b*x]]), x]", 2563);
	}

	// {2577}
	public void test0018() {
		check("Integrate[Cos[e + f*x]^4*(b*Sin[e + f*x])^(1/3), x]", 2577);
	}

	// {2577}
	public void test0019() {
		check("Integrate[Cos[e + f*x]^2*(b*Sin[e + f*x])^(1/3), x]", 2577);
	}

	// {2643}
	public void test0020() {
		check("Integrate[(b*Sin[e + f*x])^(1/3), x]", 2643);
	}

	// {2577}
	public void test0021() {
		check("Integrate[Sec[e + f*x]^2*(b*Sin[e + f*x])^(1/3), x]", 2577);
	}

	// {2577}
	public void test0022() {
		check("Integrate[Sec[e + f*x]^4*(b*Sin[e + f*x])^(1/3), x]", 2577);
	}

	// {2577}
	public void test0023() {
		check("Integrate[Cos[e + f*x]^4*(b*Sin[e + f*x])^(5/3), x]", 2577);
	}

	// {2577}
	public void test0024() {
		check("Integrate[Cos[e + f*x]^2*(b*Sin[e + f*x])^(5/3), x]", 2577);
	}

	// {2643}
	public void test0025() {
		check("Integrate[(b*Sin[e + f*x])^(5/3), x]", 2643);
	}

	// {2577}
	public void test0026() {
		check("Integrate[Sec[e + f*x]^2*(b*Sin[e + f*x])^(5/3), x]", 2577);
	}

	// {2577}
	public void test0027() {
		check("Integrate[Sec[e + f*x]^4*(b*Sin[e + f*x])^(5/3), x]", 2577);
	}

	// {2577}
	public void test0028() {
		check("Integrate[Cos[e + f*x]^4/(b*Sin[e + f*x])^(1/3), x]", 2577);
	}

	// {2577}
	public void test0029() {
		check("Integrate[Cos[e + f*x]^2/(b*Sin[e + f*x])^(1/3), x]", 2577);
	}

	// {2643}
	public void test0030() {
		check("Integrate[(b*Sin[e + f*x])^(-1/3), x]", 2643);
	}

	// {2577}
	public void test0031() {
		check("Integrate[Sec[e + f*x]^2/(b*Sin[e + f*x])^(1/3), x]", 2577);
	}

	// {2577}
	public void test0032() {
		check("Integrate[Sec[e + f*x]^4/(b*Sin[e + f*x])^(1/3), x]", 2577);
	}

	// {2577}
	public void test0033() {
		check("Integrate[Cos[e + f*x]^4/(b*Sin[e + f*x])^(5/3), x]", 2577);
	}

	// {2577}
	public void test0034() {
		check("Integrate[Cos[e + f*x]^2/(b*Sin[e + f*x])^(5/3), x]", 2577);
	}

	// {2643}
	public void test0035() {
		check("Integrate[(b*Sin[e + f*x])^(-5/3), x]", 2643);
	}

	// {2577}
	public void test0036() {
		check("Integrate[Sec[e + f*x]^2/(b*Sin[e + f*x])^(5/3), x]", 2577);
	}

	// {2577}
	public void test0037() {
		check("Integrate[Sec[e + f*x]^4/(b*Sin[e + f*x])^(5/3), x]", 2577);
	}

	// {2563}
	public void test0038() {
		check("Integrate[Cos[x]^(2/3)/Sin[x]^(8/3), x]", 2563);
	}

	// {2563}
	public void test0039() {
		check("Integrate[Sin[x]^(2/3)/Cos[x]^(8/3), x]", 2563);
	}

	// {2576}
	public void test0040() {
		check("Integrate[Cos[e + f*x]^n*Sin[e + f*x]^m, x]", 2576);
	}

	// {2576}
	public void test0041() {
		check("Integrate[(d*Cos[e + f*x])^n*Sin[e + f*x]^m, x]", 2576);
	}

	// {2576}
	public void test0042() {
		check("Integrate[Cos[e + f*x]^n*(b*Sin[e + f*x])^m, x]", 2576);
	}

	// {2576}
	public void test0043() {
		check("Integrate[(d*Cos[e + f*x])^n*(b*Sin[e + f*x])^m, x]", 2576);
	}

	// {2577}
	public void test0044() {
		check("Integrate[Cos[a + b*x]^4*(c*Sin[a + b*x])^m, x]", 2577);
	}

	// {2577}
	public void test0045() {
		check("Integrate[Cos[a + b*x]^2*(c*Sin[a + b*x])^m, x]", 2577);
	}

	// {2643}
	public void test0046() {
		check("Integrate[(c*Sin[a + b*x])^m, x]", 2643);
	}

	// {2577}
	public void test0047() {
		check("Integrate[Sec[a + b*x]^2*(c*Sin[a + b*x])^m, x]", 2577);
	}

	// {2577}
	public void test0048() {
		check("Integrate[Sec[a + b*x]^4*(c*Sin[a + b*x])^m, x]", 2577);
	}

	// {2577}
	public void test0049() {
		check("Integrate[(d*Cos[a + b*x])^(3/2)*(c*Sin[a + b*x])^m, x]", 2577);
	}

	// {2577}
	public void test0050() {
		check("Integrate[Sqrt[d*Cos[a + b*x]]*(c*Sin[a + b*x])^m, x]", 2577);
	}

	// {2577}
	public void test0051() {
		check("Integrate[(c*Sin[a + b*x])^m/Sqrt[d*Cos[a + b*x]], x]", 2577);
	}

	// {2577}
	public void test0052() {
		check("Integrate[(c*Sin[a + b*x])^m/(d*Cos[a + b*x])^(3/2), x]", 2577);
	}

	// {2577}
	public void test0053() {
		check("Integrate[(c*Sin[a + b*x])^m/(d*Cos[a + b*x])^(5/2), x]", 2577);
	}

	// {2576}
	public void test0054() {
		check("Integrate[(d*Cos[a + b*x])^n*Sin[a + b*x]^4, x]", 2576);
	}

	// {2576}
	public void test0055() {
		check("Integrate[(d*Cos[a + b*x])^n*Sin[a + b*x]^2, x]", 2576);
	}

	// {2643}
	public void test0056() {
		check("Integrate[(d*Cos[a + b*x])^n, x]", 2643);
	}

	// {2576}
	public void test0057() {
		check("Integrate[(d*Cos[a + b*x])^n*Csc[a + b*x]^2, x]", 2576);
	}

	// {2576}
	public void test0058() {
		check("Integrate[(d*Cos[a + b*x])^n*Csc[a + b*x]^4, x]", 2576);
	}

	// {2576}
	public void test0059() {
		check("Integrate[(d*Cos[a + b*x])^n*(c*Sin[a + b*x])^(5/2), x]", 2576);
	}

	// {2576}
	public void test0060() {
		check("Integrate[(d*Cos[a + b*x])^n*(c*Sin[a + b*x])^(3/2), x]", 2576);
	}

	// {2576}
	public void test0061() {
		check("Integrate[(d*Cos[a + b*x])^n*Sqrt[c*Sin[a + b*x]], x]", 2576);
	}

	// {2576}
	public void test0062() {
		check("Integrate[(d*Cos[a + b*x])^n/Sqrt[c*Sin[a + b*x]], x]", 2576);
	}

	// {2576}
	public void test0063() {
		check("Integrate[(d*Cos[a + b*x])^n/(c*Sin[a + b*x])^(3/2), x]", 2576);
	}

	// {2578}
	public void test0064() {
		check("Integrate[Sqrt[b*Sec[e + f*x]]/(a*Sin[e + f*x])^(3/2), x]", 2578);
	}

	// {2578}
	public void test0065() {
		check("Integrate[1/(Sqrt[b*Sec[e + f*x]]*Sin[e + f*x]^(5/2)), x]", 2578);
	}

	// {2578}
	public void test0066() {
		check("Integrate[1/((b*Sec[e + f*x])^(3/2)*(a*Sin[e + f*x])^(7/2)), x]", 2578);
	}

	// {2646}
	public void test0067() {
		check("Integrate[Sqrt[a + a*Sin[c + d*x]], x]", 2646);
	}

	// {2651}
	public void test0068() {
		check("Integrate[(2 + 2*Sin[c + d*x])^n, x]", 2651);
	}

	// {2651}
	public void test0069() {
		check("Integrate[(2 - 2*Sin[c + d*x])^n, x]", 2651);
	}

	// {2657}
	public void test0070() {
		check("Integrate[(5 + 3*Sin[c + d*x])^(-1), x]", 2657);
	}

	// {2657}
	public void test0071() {
		check("Integrate[(5 - 3*Sin[c + d*x])^(-1), x]", 2657);
	}

	// {2658}
	public void test0072() {
		check("Integrate[(-5 + 3*Sin[c + d*x])^(-1), x]", 2658);
	}

	// {2658}
	public void test0073() {
		check("Integrate[(-5 - 3*Sin[c + d*x])^(-1), x]", 2658);
	}

	// {3381}
	public void test0074() {
		check("Integrate[(e*x)^(-1 + 2*n)*(b*Sin[c + d*x^n])^p, x]", 3381);
	}

	// {3381}
	public void test0075() {
		check("Integrate[(e*x)^(-1 + 2*n)*(a + b*Sin[c + d*x^n])^p, x]", 3381);
	}

	// {3351}
	public void test0076() {
		check("Integrate[Sin[b*(c + d*x)^2], x]", 3351);
	}

	// {2671}
	public void test0077() {
		check("Integrate[Cos[c + d*x]^2/(a + a*Sin[c + d*x])^3, x]", 2671);
	}

	// {2673}
	public void test0078() {
		check("Integrate[Sec[c + d*x]^2*(a + a*Sin[c + d*x])^(3/2), x]", 2673);
	}

	// {2673}
	public void test0079() {
		check("Integrate[Sec[c + d*x]^4*(a + a*Sin[c + d*x])^(5/2), x]", 2673);
	}

	// {2673}
	public void test0080() {
		check("Integrate[Sec[c + d*x]^6*(a + a*Sin[c + d*x])^(7/2), x]", 2673);
	}

	// {2673}
	public void test0081() {
		check("Integrate[Cos[c + d*x]^2/Sqrt[a + a*Sin[c + d*x]], x]", 2673);
	}

	// {2673}
	public void test0082() {
		check("Integrate[Cos[c + d*x]^4/(a + a*Sin[c + d*x])^(3/2), x]", 2673);
	}

	// {2673}
	public void test0083() {
		check("Integrate[Cos[c + d*x]^6/(a + a*Sin[c + d*x])^(5/2), x]", 2673);
	}

	// {2671}
	public void test0084() {
		check("Integrate[Sqrt[a + a*Sin[c + d*x]]/(e*Cos[c + d*x])^(3/2), x]", 2671);
	}

	// {2671}
	public void test0085() {
		check("Integrate[(a + a*Sin[c + d*x])^(3/2)/(e*Cos[c + d*x])^(5/2), x]", 2671);
	}

	// {2671}
	public void test0086() {
		check("Integrate[(a + a*Sin[c + d*x])^(5/2)/(e*Cos[c + d*x])^(7/2), x]", 2671);
	}

	// {2671}
	public void test0087() {
		check("Integrate[1/(Sqrt[e*Cos[c + d*x]]*Sqrt[a + a*Sin[c + d*x]]), x]", 2671);
	}

	// {2671}
	public void test0088() {
		check("Integrate[Sqrt[e*Cos[c + d*x]]/(a + a*Sin[c + d*x])^(3/2), x]", 2671);
	}

	// {2671}
	public void test0089() {
		check("Integrate[(e*Cos[c + d*x])^(3/2)/(a + a*Sin[c + d*x])^(5/2), x]", 2671);
	}

	// {2671}
	public void test0090() {
		check("Integrate[(e*Cos[c + d*x])^(-1 - m)*(a + a*Sin[c + d*x])^m, x]", 2671);
	}

	// {2673}
	public void test0091() {
		check("Integrate[(e*Cos[c + d*x])^(1 - 2*m)*(a + a*Sin[c + d*x])^m, x]", 2673);
	}

	// {2703}
	public void test0092() {
		check("Integrate[(e*Cos[c + d*x])^p/(a + b*Sin[c + d*x]), x]", 2703);
	}

	// {2703}
	public void test0093() {
		check("Integrate[(e*Cos[c + d*x])^p/(a + b*Sin[c + d*x])^2, x]", 2703);
	}

	// {2703}
	public void test0094() {
		check("Integrate[(e*Cos[c + d*x])^p/(a + b*Sin[c + d*x])^3, x]", 2703);
	}

	// {2703}
	public void test0095() {
		check("Integrate[(e*Cos[c + d*x])^p/(a + b*Sin[c + d*x])^8, x]", 2703);
	}

	// {2698}
	public void test0096() {
		check("Integrate[(e*Cos[c + d*x])^(-1 - m)*(a + b*Sin[c + d*x])^m, x]", 2698);
	}

	// {2644}
	public void test0097() {
		check("Integrate[(a + a*Sin[c + d*x])^2, x]", 2644);
	}

	// {2648}
	public void test0098() {
		check("Integrate[(a + a*Sin[c + d*x])^(-1), x]", 2648);
	}

	// {2648}
	public void test0099() {
		check("Integrate[(a + a*Sin[x])^(-1), x]", 2648);
	}

	// {2646}
	public void test0100() {
		check("Integrate[Sqrt[a + a*Sin[c + d*x]], x]", 2646);
	}

	// {2651}
	public void test0101() {
		check("Integrate[(1 + Sin[c + d*x])^n, x]", 2651);
	}

	// {2651}
	public void test0102() {
		check("Integrate[(1 - Sin[c + d*x])^n, x]", 2651);
	}

	// {2734}
	public void test0103() {
		check("Integrate[Sin[e + f*x]*(a + b*Sin[e + f*x]), x]", 2734);
	}

	// {2644}
	public void test0104() {
		check("Integrate[(a + b*Sin[e + f*x])^2, x]", 2644);
	}

	// {2816}
	public void test0105() {
		check("Integrate[1/(Sqrt[Sin[c + d*x]]*Sqrt[a + b*Sin[c + d*x]]), x]", 2816);
	}

	// {2734}
	public void test0106() {
		check("Integrate[(a + a*Sin[e + f*x])*(c - c*Sin[e + f*x]), x]", 2734);
	}

	// {2738}
	public void test0107() {
		check("Integrate[Sqrt[a + a*Sin[e + f*x]]*(c - c*Sin[e + f*x])^(7/2), x]", 2738);
	}

	// {2738}
	public void test0108() {
		check("Integrate[Sqrt[a + a*Sin[e + f*x]]*(c - c*Sin[e + f*x])^(5/2), x]", 2738);
	}

	// {2738}
	public void test0109() {
		check("Integrate[Sqrt[a + a*Sin[e + f*x]]*(c - c*Sin[e + f*x])^(3/2), x]", 2738);
	}

	// {2738}
	public void test0110() {
		check("Integrate[Sqrt[a + a*Sin[e + f*x]]*Sqrt[c - c*Sin[e + f*x]], x]", 2738);
	}

	// {2738}
	public void test0111() {
		check("Integrate[Sqrt[a + a*Sin[e + f*x]]/(c - c*Sin[e + f*x])^(3/2), x]", 2738);
	}

	// {2738}
	public void test0112() {
		check("Integrate[Sqrt[a + a*Sin[e + f*x]]/(c - c*Sin[e + f*x])^(5/2), x]", 2738);
	}

	// {2738}
	public void test0113() {
		check("Integrate[Sqrt[a + a*Sin[e + f*x]]/(c - c*Sin[e + f*x])^(7/2), x]", 2738);
	}

	// {2738}
	public void test0114() {
		check("Integrate[(a + a*Sin[e + f*x])^(3/2)*Sqrt[c - c*Sin[e + f*x]], x]", 2738);
	}

	// {2742}
	public void test0115() {
		check("Integrate[(a + a*Sin[e + f*x])^(3/2)/(c - c*Sin[e + f*x])^(5/2), x]", 2742);
	}

	// {2738}
	public void test0116() {
		check("Integrate[(a + a*Sin[e + f*x])^(5/2)*Sqrt[c - c*Sin[e + f*x]], x]", 2738);
	}

	// {2742}
	public void test0117() {
		check("Integrate[(a + a*Sin[e + f*x])^(5/2)/(c - c*Sin[e + f*x])^(7/2), x]", 2742);
	}

	// {2738}
	public void test0118() {
		check("Integrate[(a + a*Sin[e + f*x])^(7/2)*Sqrt[c - c*Sin[e + f*x]], x]", 2738);
	}

	// {2742}
	public void test0119() {
		check("Integrate[(a + a*Sin[e + f*x])^(7/2)/(c - c*Sin[e + f*x])^(9/2), x]", 2742);
	}

	// {2738}
	public void test0120() {
		check("Integrate[Sqrt[c - c*Sin[e + f*x]]/(a + a*Sin[e + f*x])^(3/2), x]", 2738);
	}

	// {2742}
	public void test0121() {
		check("Integrate[(c - c*Sin[e + f*x])^(3/2)/(a + a*Sin[e + f*x])^(5/2), x]", 2742);
	}

	// {2738}
	public void test0122() {
		check("Integrate[Sqrt[c - c*Sin[e + f*x]]/(a + a*Sin[e + f*x])^(5/2), x]", 2738);
	}

	// {2738}
	public void test0123() {
		check("Integrate[(a + a*Sin[e + f*x])^m*Sqrt[c - c*Sin[e + f*x]], x]", 2738);
	}

	// {2742}
	public void test0124() {
		check("Integrate[(a + a*Sin[e + f*x])^m*(c - c*Sin[e + f*x])^(-1 - m), x]", 2742);
	}

	// {2734}
	public void test0125() {
		check("Integrate[(a + a*Sin[e + f*x])*(c + d*Sin[e + f*x]), x]", 2734);
	}

	// {2644}
	public void test0126() {
		check("Integrate[(a + a*Sin[e + f*x])^2, x]", 2644);
	}

	// {2648}
	public void test0127() {
		check("Integrate[(a + a*Sin[e + f*x])^(-1), x]", 2648);
	}

	// {2646}
	public void test0128() {
		check("Integrate[Sqrt[a + a*Sin[e + f*x]], x]", 2646);
	}

	// {2771}
	public void test0129() {
		check("Integrate[Sqrt[a + a*Sin[e + f*x]]/(c + d*Sin[e + f*x])^(3/2), x]", 2771);
	}

	// {2742}
	public void test0130() {
		check("Integrate[(3 - 3*Sin[e + f*x])^(-1 - m)*(1 + Sin[e + f*x])^m, x]", 2742);
	}

	// {2742}
	public void test0131() {
		check("Integrate[(3 - 3*Sin[e + f*x])^(-1 - m)*(a + a*Sin[e + f*x])^m, x]", 2742);
	}

	// {2742}
	public void test0132() {
		check("Integrate[(-3 + 3*Sin[e + f*x])^(-1 - m)*(a + a*Sin[e + f*x])^m, x]", 2742);
	}

	// {2734}
	public void test0133() {
		check("Integrate[(a + b*Sin[e + f*x])*(c + d*Sin[e + f*x]), x]", 2734);
	}

	// {2644}
	public void test0134() {
		check("Integrate[(a + b*Sin[e + f*x])^2, x]", 2644);
	}

	// {2811}
	public void test0135() {
		check("Integrate[Sqrt[a + b*Sin[e + f*x]]/Sqrt[c + d*Sin[e + f*x]], x]", 2811);
	}

	// {2811}
	public void test0136() {
		check("Integrate[Sqrt[c + d*Sin[e + f*x]]/Sqrt[a + b*Sin[e + f*x]], x]", 2811);
	}

	// {2818}
	public void test0137() {
		check("Integrate[1/(Sqrt[a + b*Sin[e + f*x]]*Sqrt[c + d*Sin[e + f*x]]), x]", 2818);
	}

	// {2826}
	public void test0138() {
		check("Integrate[(c*(d*Sin[e + f*x])^p)^n*(a + b*Sin[e + f*x])^m, x]", 2826);
	}

	// {2844}
	public void test0139() {
		check("Integrate[(g*Cos[e + f*x])^(1 - 2*m)*(a + a*Sin[e + f*x])^m*(c - c*Sin[e + f*x])^n, x]", 2844);
	}

	// {2848}
	public void test0140() {
		check("Integrate[(g*Cos[e + f*x])^(-1 - m - n)*(a + a*Sin[e + f*x])^m*(c - c*Sin[e + f*x])^n, x]", 2848);
	}

	// {2854}
	public void test0141() {
		check("Integrate[(g*Cos[e + f*x])^p*(a + a*Sin[e + f*x])^m*(A*m - A*(1 + m + p)*Sin[e + f*x]), x]", 2854);
	}

	// {2854}
	public void test0142() {
		check("Integrate[(g*Cos[e + f*x])^p*(a - a*Sin[e + f*x])^m*(A*m + A*(1 + m + p)*Sin[e + f*x]), x]", 2854);
	}

	// {2932}
	public void test0143() {
		check("Integrate[Sqrt[a + b*Sin[e + f*x]]/(Sqrt[g*Sin[e + f*x]]*(c + c*Sin[e + f*x])), x]", 2932);
	}

	// {2937}
	public void test0144() {
		check("Integrate[Sqrt[g*Sin[e + f*x]]/(Sqrt[a + b*Sin[e + f*x]]*(c + d*Sin[e + f*x])), x]", 2937);
	}

	// {2937}
	public void test0145() {
		check("Integrate[Sqrt[g*Sin[e + f*x]]/((a + b*Sin[e + f*x])*Sqrt[c + d*Sin[e + f*x]]), x]", 2937);
	}

	// {2945}
	public void test0146() {
		check("Integrate[(Csc[e + f*x]*Sqrt[a + b*Sin[e + f*x]])/Sqrt[c + d*Sin[e + f*x]], x]", 2945);
	}

	// {2974}
	public void test0147() {
		check("Integrate[Sin[c + d*x]^n*(a + a*Sin[c + d*x])^(-2 - n)*(-1 - n - (-2 - n)*Sin[c + d*x]), x]", 2974);
	}

	// {2974}
	public void test0148() {
		check("Integrate[Sin[c + d*x]^(-2 - m)*(a + a*Sin[c + d*x])^m*(1 + m - m*Sin[c + d*x]), x]", 2974);
	}

	// {2970}
	public void test0149() {
		check("Integrate[(a + a*Sin[e + f*x])^m*(c - c*Sin[e + f*x])^n*(B*(m - n) - B*(1 + m + n)*Sin[e + f*x]), x]", 2970);
	}

	// {2970}
	public void test0150() {
		check("Integrate[(a - a*Sin[e + f*x])^m*(c + c*Sin[e + f*x])^n*(B*(m - n) + B*(1 + m + n)*Sin[e + f*x]), x]", 2970);
	}

	// {2734}
	public void test0151() {
		check("Integrate[(a + a*Sin[e + f*x])*(A + B*Sin[e + f*x]), x]", 2734);
	}

	// {2974}
	public void test0152() {
		check("Integrate[(a + a*Sin[e + f*x])^m*(c + d*Sin[e + f*x])^(-2 - m)*(d - (c - d)*m + (c + (c - d)*m)*Sin[e + f*x]), x]", 2974);
	}

	// {2974}
	public void test0153() {
		check("Integrate[(a - a*Sin[e + f*x])^m*(c + d*Sin[e + f*x])^(-2 - m)*(d + (c + d)*m + (c + (c + d)*m)*Sin[e + f*x]), x]", 2974);
	}

	// {3011}
	public void test0154() {
		check("Integrate[Sin[e + f*x]^m*(1 + m - (2 + m)*Sin[e + f*x]^2), x]", 3011);
	}

	// {3011}
	public void test0155() {
		check("Integrate[Sin[e + f*x]^5*(6 - 7*Sin[e + f*x]^2), x]", 3011);
	}

	// {3011}
	public void test0156() {
		check("Integrate[Sin[e + f*x]^4*(5 - 6*Sin[e + f*x]^2), x]", 3011);
	}

	// {3011}
	public void test0157() {
		check("Integrate[Sin[e + f*x]^3*(4 - 5*Sin[e + f*x]^2), x]", 3011);
	}

	// {3011}
	public void test0158() {
		check("Integrate[Sin[e + f*x]^2*(3 - 4*Sin[e + f*x]^2), x]", 3011);
	}

	// {3011}
	public void test0159() {
		check("Integrate[Sin[e + f*x]*(2 - 3*Sin[e + f*x]^2), x]", 3011);
	}

	// {2638}
	public void test0160() {
		check("Integrate[-Sin[e + f*x], x]", 2638);
	}

	// {3011}
	public void test0161() {
		check("Integrate[Csc[e + f*x]^3*(-2 + Sin[e + f*x]^2), x]", 3011);
	}

	// {3011}
	public void test0162() {
		check("Integrate[Csc[e + f*x]^4*(-3 + 2*Sin[e + f*x]^2), x]", 3011);
	}

	// {3011}
	public void test0163() {
		check("Integrate[Csc[e + f*x]^5*(-4 + 3*Sin[e + f*x]^2), x]", 3011);
	}

	// {3179}
	public void test0164() {
		check("Integrate[(a + b*Sin[x]^2)^2, x]", 3179);
	}

	// {3179}
	public void test0165() {
		check("Integrate[(a + b*Sin[e + f*x]^2)^2, x]", 3179);
	}

	// {2637}
	public void test0166() {
		check("Integrate[Cos[a + b*x], x]", 2637);
	}

	// {2639}
	public void test0167() {
		check("Integrate[Sqrt[Cos[a + b*x]], x]", 2639);
	}

	// {2641}
	public void test0168() {
		check("Integrate[1/Sqrt[Cos[a + b*x]], x]", 2641);
	}

	// {2643}
	public void test0169() {
		check("Integrate[Cos[a + b*x]^(4/3), x]", 2643);
	}

	// {2643}
	public void test0170() {
		check("Integrate[Cos[a + b*x]^(2/3), x]", 2643);
	}

	// {2643}
	public void test0171() {
		check("Integrate[Cos[a + b*x]^(1/3), x]", 2643);
	}

	// {2643}
	public void test0172() {
		check("Integrate[Cos[a + b*x]^(-1/3), x]", 2643);
	}

	// {2643}
	public void test0173() {
		check("Integrate[Cos[a + b*x]^(-2/3), x]", 2643);
	}

	// {2643}
	public void test0174() {
		check("Integrate[Cos[a + b*x]^(-4/3), x]", 2643);
	}

	// {2643}
	public void test0175() {
		check("Integrate[(c*Cos[a + b*x])^(4/3), x]", 2643);
	}

	// {2643}
	public void test0176() {
		check("Integrate[(c*Cos[a + b*x])^(2/3), x]", 2643);
	}

	// {2643}
	public void test0177() {
		check("Integrate[(c*Cos[a + b*x])^(1/3), x]", 2643);
	}

	// {2643}
	public void test0178() {
		check("Integrate[(c*Cos[a + b*x])^(-1/3), x]", 2643);
	}

	// {2643}
	public void test0179() {
		check("Integrate[(c*Cos[a + b*x])^(-2/3), x]", 2643);
	}

	// {2643}
	public void test0180() {
		check("Integrate[(c*Cos[a + b*x])^(-4/3), x]", 2643);
	}

	// {2643}
	public void test0181() {
		check("Integrate[Cos[a + b*x]^n, x]", 2643);
	}

	// {2643}
	public void test0182() {
		check("Integrate[(c*Cos[a + b*x])^n, x]", 2643);
	}

	// {2643}
	public void test0183() {
		check("Integrate[(b*Cos[c + d*x])^(1/3), x]", 2643);
	}

	// {2643}
	public void test0184() {
		check("Integrate[(b*Cos[c + d*x])^(2/3), x]", 2643);
	}

	// {2643}
	public void test0185() {
		check("Integrate[(b*Cos[c + d*x])^(4/3), x]", 2643);
	}

	// {2643}
	public void test0186() {
		check("Integrate[(b*Cos[c + d*x])^(-1/3), x]", 2643);
	}

	// {2643}
	public void test0187() {
		check("Integrate[(b*Cos[c + d*x])^(-2/3), x]", 2643);
	}

	// {2643}
	public void test0188() {
		check("Integrate[(b*Cos[c + d*x])^(-4/3), x]", 2643);
	}

	// {2643}
	public void test0189() {
		check("Integrate[(b*Cos[c + d*x])^n, x]", 2643);
	}

	// {2639}
	public void test0190() {
		check("Integrate[Sqrt[Cos[a + b*x]], x]", 2639);
	}

	// {3310}
	public void test0191() {
		check("Integrate[x*Cos[a + b*x]^(3/2), x]", 3310);
	}

	// {3314}
	public void test0192() {
		check("Integrate[Cos[x]^(3/2)/x^3, x]", 3314);
	}

	// {2641}
	public void test0193() {
		check("Integrate[1/Sqrt[Cos[a + b*x]], x]", 2641);
	}

	// {3315}
	public void test0194() {
		check("Integrate[x/Cos[a + b*x]^(3/2), x]", 3315);
	}

	// {2646}
	public void test0195() {
		check("Integrate[Sqrt[a + a*Cos[c + d*x]], x]", 2646);
	}

	// {2646}
	public void test0196() {
		check("Integrate[Sqrt[a + a*Cos[x]], x]", 2646);
	}

	// {2646}
	public void test0197() {
		check("Integrate[Sqrt[a - a*Cos[x]], x]", 2646);
	}

	// {2646}
	public void test0198() {
		check("Integrate[Sqrt[a + a*Cos[c + d*x]], x]", 2646);
	}

	// {2651}
	public void test0199() {
		check("Integrate[(2 + 2*Cos[c + d*x])^n, x]", 2651);
	}

	// {2651}
	public void test0200() {
		check("Integrate[(2 - 2*Cos[c + d*x])^n, x]", 2651);
	}

	// {2657}
	public void test0201() {
		check("Integrate[(5 + 3*Cos[c + d*x])^(-1), x]", 2657);
	}

	// {2657}
	public void test0202() {
		check("Integrate[(5 - 3*Cos[c + d*x])^(-1), x]", 2657);
	}

	// {2658}
	public void test0203() {
		check("Integrate[(-5 + 3*Cos[c + d*x])^(-1), x]", 2658);
	}

	// {2658}
	public void test0204() {
		check("Integrate[(-5 - 3*Cos[c + d*x])^(-1), x]", 2658);
	}

	// {3382}
	public void test0205() {
		check("Integrate[(e*x)^(-1 + 2*n)*(b*Cos[c + d*x^n])^p, x]", 3382);
	}

	// {3382}
	public void test0206() {
		check("Integrate[(e*x)^(-1 + 2*n)*(a + b*Cos[c + d*x^n])^p, x]", 3382);
	}

	// {3352}
	public void test0207() {
		check("Integrate[Cos[(a + b*x)^2], x]", 3352);
	}

	// {2648}
	public void test0208() {
		check("Integrate[(a + a*Cos[x])^(-1), x]", 2648);
	}

	// {2671}
	public void test0209() {
		check("Integrate[Sin[x]^2/(1 + Cos[x])^3, x]", 2671);
	}

	// {2671}
	public void test0210() {
		check("Integrate[Sin[x]^2/(1 - Cos[x])^3, x]", 2671);
	}

	// {2733}
	public void test0211() {
		check("Integrate[(a + b*Cos[e + f*x])^m*(g*Tan[e + f*x])^p, x]", 2733);
	}

	// {2734}
	public void test0212() {
		check("Integrate[Cos[c + d*x]*(a + a*Cos[c + d*x]), x]", 2734);
	}

	// {2644}
	public void test0213() {
		check("Integrate[(a + a*Cos[c + d*x])^2, x]", 2644);
	}

	// {2648}
	public void test0214() {
		check("Integrate[(a + a*Cos[c + d*x])^(-1), x]", 2648);
	}

	// {2646}
	public void test0215() {
		check("Integrate[Sqrt[a + a*Cos[c + d*x]], x]", 2646);
	}

	// {2771}
	public void test0216() {
		check("Integrate[Sqrt[a + a*Cos[c + d*x]]/Cos[c + d*x]^(3/2), x]", 2771);
	}

	// {2771}
	public void test0217() {
		check("Integrate[Sqrt[a - a*Cos[c + d*x]]/Cos[c + d*x]^(3/2), x]", 2771);
	}

	// {2771}
	public void test0218() {
		check("Integrate[Sqrt[1 - Cos[c + d*x]]/Cos[c + d*x]^(3/2), x]", 2771);
	}

	// {2734}
	public void test0219() {
		check("Integrate[Cos[c + d*x]*(a + b*Cos[c + d*x]), x]", 2734);
	}

	// {2644}
	public void test0220() {
		check("Integrate[(a + b*Cos[c + d*x])^2, x]", 2644);
	}

	// {2653}
	public void test0221() {
		check("Integrate[Sqrt[3 + 4*Cos[c + d*x]], x]", 2653);
	}

	// {2654}
	public void test0222() {
		check("Integrate[Sqrt[3 - 4*Cos[c + d*x]], x]", 2654);
	}

	// {2661}
	public void test0223() {
		check("Integrate[1/Sqrt[3 + 4*Cos[c + d*x]], x]", 2661);
	}

	// {2805}
	public void test0224() {
		check("Integrate[Sec[c + d*x]/Sqrt[3 + 4*Cos[c + d*x]], x]", 2805);
	}

	// {2662}
	public void test0225() {
		check("Integrate[1/Sqrt[3 - 4*Cos[c + d*x]], x]", 2662);
	}

	// {2806}
	public void test0226() {
		check("Integrate[Sec[c + d*x]/Sqrt[3 - 4*Cos[c + d*x]], x]", 2806);
	}

	// {2805}
	public void test0227() {
		check("Integrate[1/(Sqrt[Cos[c + d*x]]*(a + b*Cos[c + d*x])), x]", 2805);
	}

	// {2811}
	public void test0228() {
		check("Integrate[Sqrt[a + b*Cos[c + d*x]]/Sqrt[Cos[c + d*x]], x]", 2811);
	}

	// {2809}
	public void test0229() {
		check("Integrate[Sqrt[Cos[c + d*x]]/Sqrt[a + b*Cos[c + d*x]], x]", 2809);
	}

	// {2816}
	public void test0230() {
		check("Integrate[1/(Sqrt[Cos[c + d*x]]*Sqrt[a + b*Cos[c + d*x]]), x]", 2816);
	}

	// {2813}
	public void test0231() {
		check("Integrate[1/(Sqrt[Cos[c + d*x]]*Sqrt[2 + 3*Cos[c + d*x]]), x]", 2813);
	}

	// {2813}
	public void test0232() {
		check("Integrate[1/(Sqrt[Cos[c + d*x]]*Sqrt[-2 + 3*Cos[c + d*x]]), x]", 2813);
	}

	// {2815}
	public void test0233() {
		check("Integrate[1/(Sqrt[Cos[c + d*x]]*Sqrt[3 + 2*Cos[c + d*x]]), x]", 2815);
	}

	// {2815}
	public void test0234() {
		check("Integrate[1/(Sqrt[3 - 2*Cos[c + d*x]]*Sqrt[Cos[c + d*x]]), x]", 2815);
	}

	// {2813}
	public void test0235() {
		check("Integrate[1/(Sqrt[2 - 3*Cos[c + d*x]]*Sqrt[-Cos[c + d*x]]), x]", 2813);
	}

	// {2813}
	public void test0236() {
		check("Integrate[1/(Sqrt[-2 - 3*Cos[c + d*x]]*Sqrt[-Cos[c + d*x]]), x]", 2813);
	}

	// {2815}
	public void test0237() {
		check("Integrate[1/(Sqrt[-Cos[c + d*x]]*Sqrt[-3 + 2*Cos[c + d*x]]), x]", 2815);
	}

	// {2815}
	public void test0238() {
		check("Integrate[1/(Sqrt[-3 - 2*Cos[c + d*x]]*Sqrt[-Cos[c + d*x]]), x]", 2815);
	}

	// {2809}
	public void test0239() {
		check("Integrate[Sqrt[Cos[c + d*x]]/Sqrt[2 + 3*Cos[c + d*x]], x]", 2809);
	}

	// {2809}
	public void test0240() {
		check("Integrate[Sqrt[Cos[c + d*x]]/Sqrt[-2 + 3*Cos[c + d*x]], x]", 2809);
	}

	// {2808}
	public void test0241() {
		check("Integrate[Sqrt[Cos[c + d*x]]/Sqrt[3 + 2*Cos[c + d*x]], x]", 2808);
	}

	// {2808}
	public void test0242() {
		check("Integrate[Sqrt[Cos[c + d*x]]/Sqrt[3 - 2*Cos[c + d*x]], x]", 2808);
	}

	// {2809}
	public void test0243() {
		check("Integrate[Sqrt[-Cos[c + d*x]]/Sqrt[2 - 3*Cos[c + d*x]], x]", 2809);
	}

	// {2809}
	public void test0244() {
		check("Integrate[Sqrt[-Cos[c + d*x]]/Sqrt[-2 - 3*Cos[c + d*x]], x]", 2809);
	}

	// {2808}
	public void test0245() {
		check("Integrate[Sqrt[-Cos[c + d*x]]/Sqrt[-3 + 2*Cos[c + d*x]], x]", 2808);
	}

	// {2808}
	public void test0246() {
		check("Integrate[Sqrt[-Cos[c + d*x]]/Sqrt[-3 - 2*Cos[c + d*x]], x]", 2808);
	}

	// {2734}
	public void test0247() {
		check("Integrate[(a + a*Cos[c + d*x])*(-B/2 + B*Cos[c + d*x]), x]", 2734);
	}

	// {2749}
	public void test0248() {
		check("Integrate[(a + a*Cos[c + d*x])^4*((-4*B)/5 + B*Cos[c + d*x]), x]", 2749);
	}

	// {2749}
	public void test0249() {
		check("Integrate[(a + a*Cos[c + d*x])^n*(-((B*n)/(1 + n)) + B*Cos[c + d*x]), x]", 2749);
	}

	// {2749}
	public void test0250() {
		check("Integrate[((-3*B)/2 + B*Cos[c + d*x])/(a + a*Cos[c + d*x])^3, x]", 2749);
	}

	// {2749}
	public void test0251() {
		check("Integrate[(a + a*Cos[c + d*x])^(3/2)*((-3*B)/5 + B*Cos[c + d*x]), x]", 2749);
	}

	// {2749}
	public void test0252() {
		check("Integrate[((-5*B)/3 + B*Cos[c + d*x])/(a + a*Cos[c + d*x])^(5/2), x]", 2749);
	}

	// {2734}
	public void test0253() {
		check("Integrate[(a + a*Cos[c + d*x])*(A + B*Cos[c + d*x]), x]", 2734);
	}

	// {2734}
	public void test0254() {
		check("Integrate[(a + b*Cos[c + d*x])*(A + B*Cos[c + d*x]), x]", 2734);
	}

	// {2994}
	public void test0255() {
		check("Integrate[(1 + Cos[c + d*x])/(Cos[c + d*x]^(3/2)*Sqrt[2 + 3*Cos[c + d*x]]), x]", 2994);
	}

	// {2994}
	public void test0256() {
		check("Integrate[(1 + Cos[c + d*x])/(Cos[c + d*x]^(3/2)*Sqrt[-2 + 3*Cos[c + d*x]]), x]", 2994);
	}

	// {2994}
	public void test0257() {
		check("Integrate[(1 + Cos[c + d*x])/(Cos[c + d*x]^(3/2)*Sqrt[3 + 2*Cos[c + d*x]]), x]", 2994);
	}

	// {2994}
	public void test0258() {
		check("Integrate[(1 + Cos[c + d*x])/(Sqrt[3 - 2*Cos[c + d*x]]*Cos[c + d*x]^(3/2)), x]", 2994);
	}

	// {2990}
	public void test0259() {
		check("Integrate[(c*Cos[e + f*x])^m*(a + b*Cos[e + f*x])^(3/2)*(A + B*Cos[e + f*x]), x]", 2990);
	}

	// {3000}
	public void test0260() {
		check("Integrate[((c*Cos[e + f*x])^m*(A + B*Cos[e + f*x]))/(a + b*Cos[e + f*x])^(3/2), x]", 3000);
	}

	// {2961}
	public void test0261() {
		check("Integrate[(a + b*Cos[e + f*x])^n*(A + B*Cos[e + f*x])*(c*Sec[e + f*x])^m, x]", 2961);
	}

	// {2961}
	public void test0262() {
		check("Integrate[Sqrt[a + b*Cos[e + f*x]]*(A + B*Cos[e + f*x])*(c*Sec[e + f*x])^m, x]", 2961);
	}

	// {2961}
	public void test0263() {
		check("Integrate[((A + B*Cos[e + f*x])*(c*Sec[e + f*x])^m)/Sqrt[a + b*Cos[e + f*x]], x]", 2961);
	}

	// {3011}
	public void test0264() {
		check("Integrate[Sqrt[Cos[c + d*x]]*(3 - 5*Cos[c + d*x]^2), x]", 3011);
	}

	// {3011}
	public void test0265() {
		check("Integrate[(1 - 3*Cos[c + d*x]^2)/Sqrt[Cos[c + d*x]], x]", 3011);
	}

	// {3011}
	public void test0266() {
		check("Integrate[(b*Cos[c + d*x])^m*(-((C*(1 + m))/(2 + m)) + C*Cos[c + d*x]^2), x]", 3011);
	}

	// {3011}
	public void test0267() {
		check("Integrate[(b*Cos[c + d*x])^m*(A - (A*(2 + m)*Cos[c + d*x]^2)/(1 + m)), x]", 3011);
	}

	// {3177}
	public void test0268() {
		check("Integrate[Sqrt[1 + Cos[x]^2], x]", 3177);
	}

	// {3182}
	public void test0269() {
		check("Integrate[1/Sqrt[1 + Cos[x]^2], x]", 3182);
	}

	// {3475}
	public void test0270() {
		check("Integrate[Tan[c + d*x], x]", 3475);
	}

	// {2589}
	public void test0271() {
		check("Integrate[Sqrt[a*Sin[e + f*x]]*Sqrt[b*Tan[e + f*x]], x]", 2589);
	}

	// {2589}
	public void test0272() {
		check("Integrate[(b*Tan[e + f*x])^(3/2)/Sqrt[a*Sin[e + f*x]], x]", 2589);
	}

	// {2589}
	public void test0273() {
		check("Integrate[(a*Sin[e + f*x])^(3/2)/Sqrt[b*Tan[e + f*x]], x]", 2589);
	}

	// {2589}
	public void test0274() {
		check("Integrate[(a*Sin[e + f*x])^(5/2)/(b*Tan[e + f*x])^(3/2), x]", 2589);
	}

	// {2617}
	public void test0275() {
		check("Integrate[(d*Sec[e + f*x])^(4/3)*Tan[e + f*x]^2, x]", 2617);
	}

	// {2617}
	public void test0276() {
		check("Integrate[(d*Sec[e + f*x])^(2/3)*Tan[e + f*x]^2, x]", 2617);
	}

	// {2617}
	public void test0277() {
		check("Integrate[(d*Sec[e + f*x])^(1/3)*Tan[e + f*x]^2, x]", 2617);
	}

	// {2617}
	public void test0278() {
		check("Integrate[Tan[e + f*x]^2/(d*Sec[e + f*x])^(1/3), x]", 2617);
	}

	// {2617}
	public void test0279() {
		check("Integrate[Tan[e + f*x]^2/(d*Sec[e + f*x])^(2/3), x]", 2617);
	}

	// {2617}
	public void test0280() {
		check("Integrate[(d*Sec[e + f*x])^(4/3)*Tan[e + f*x]^4, x]", 2617);
	}

	// {2617}
	public void test0281() {
		check("Integrate[(d*Sec[e + f*x])^(2/3)*Tan[e + f*x]^4, x]", 2617);
	}

	// {2617}
	public void test0282() {
		check("Integrate[(d*Sec[e + f*x])^(1/3)*Tan[e + f*x]^4, x]", 2617);
	}

	// {2617}
	public void test0283() {
		check("Integrate[Tan[e + f*x]^4/(d*Sec[e + f*x])^(1/3), x]", 2617);
	}

	// {2617}
	public void test0284() {
		check("Integrate[Tan[e + f*x]^4/(d*Sec[e + f*x])^(2/3), x]", 2617);
	}

	// {2605}
	public void test0285() {
		check("Integrate[Sqrt[b*Tan[e + f*x]]/(d*Sec[e + f*x])^(3/2), x]", 2605);
	}

	// {2605}
	public void test0286() {
		check("Integrate[(b*Tan[e + f*x])^(3/2)/(d*Sec[e + f*x])^(5/2), x]", 2605);
	}

	// {2605}
	public void test0287() {
		check("Integrate[(b*Tan[e + f*x])^(5/2)/(d*Sec[e + f*x])^(7/2), x]", 2605);
	}

	// {2605}
	public void test0288() {
		check("Integrate[1/(Sqrt[d*Sec[e + f*x]]*Sqrt[b*Tan[e + f*x]]), x]", 2605);
	}

	// {2605}
	public void test0289() {
		check("Integrate[Sqrt[d*Sec[e + f*x]]/(b*Tan[e + f*x])^(3/2), x]", 2605);
	}

	// {2605}
	public void test0290() {
		check("Integrate[(d*Sec[e + f*x])^(3/2)/(b*Tan[e + f*x])^(5/2), x]", 2605);
	}

	// {2617}
	public void test0291() {
		check("Integrate[(b*Sec[e + f*x])^(4/3)*Sqrt[d*Tan[e + f*x]], x]", 2617);
	}

	// {2617}
	public void test0292() {
		check("Integrate[(b*Sec[e + f*x])^(1/3)*Sqrt[d*Tan[e + f*x]], x]", 2617);
	}

	// {2617}
	public void test0293() {
		check("Integrate[Sqrt[d*Tan[e + f*x]]/(b*Sec[e + f*x])^(1/3), x]", 2617);
	}

	// {2617}
	public void test0294() {
		check("Integrate[Sqrt[d*Tan[e + f*x]]/(b*Sec[e + f*x])^(4/3), x]", 2617);
	}

	// {2617}
	public void test0295() {
		check("Integrate[(b*Sec[e + f*x])^(4/3)*(d*Tan[e + f*x])^(3/2), x]", 2617);
	}

	// {2617}
	public void test0296() {
		check("Integrate[(b*Sec[e + f*x])^(1/3)*(d*Tan[e + f*x])^(3/2), x]", 2617);
	}

	// {2617}
	public void test0297() {
		check("Integrate[(d*Tan[e + f*x])^(3/2)/(b*Sec[e + f*x])^(1/3), x]", 2617);
	}

	// {2617}
	public void test0298() {
		check("Integrate[(d*Tan[e + f*x])^(3/2)/(b*Sec[e + f*x])^(4/3), x]", 2617);
	}

	// {2617}
	public void test0299() {
		check("Integrate[Sqrt[b*Sec[e + f*x]]*(d*Tan[e + f*x])^(4/3), x]", 2617);
	}

	// {2617}
	public void test0300() {
		check("Integrate[Sqrt[b*Sec[e + f*x]]*(d*Tan[e + f*x])^(1/3), x]", 2617);
	}

	// {2617}
	public void test0301() {
		check("Integrate[Sqrt[b*Sec[e + f*x]]/(d*Tan[e + f*x])^(1/3), x]", 2617);
	}

	// {2617}
	public void test0302() {
		check("Integrate[Sqrt[b*Sec[e + f*x]]/(d*Tan[e + f*x])^(4/3), x]", 2617);
	}

	// {2617}
	public void test0303() {
		check("Integrate[(b*Sec[e + f*x])^(3/2)*(d*Tan[e + f*x])^(4/3), x]", 2617);
	}

	// {2617}
	public void test0304() {
		check("Integrate[(b*Sec[e + f*x])^(3/2)*(d*Tan[e + f*x])^(1/3), x]", 2617);
	}

	// {2617}
	public void test0305() {
		check("Integrate[(b*Sec[e + f*x])^(3/2)/(d*Tan[e + f*x])^(1/3), x]", 2617);
	}

	// {2617}
	public void test0306() {
		check("Integrate[(b*Sec[e + f*x])^(3/2)/(d*Tan[e + f*x])^(4/3), x]", 2617);
	}

	// {2617}
	public void test0307() {
		check("Integrate[(b*Sec[e + f*x])^m*Tan[e + f*x]^4, x]", 2617);
	}

	// {2617}
	public void test0308() {
		check("Integrate[(b*Sec[e + f*x])^m*Tan[e + f*x]^2, x]", 2617);
	}

	// {2617}
	public void test0309() {
		check("Integrate[Cot[e + f*x]^2*(b*Sec[e + f*x])^m, x]", 2617);
	}

	// {2617}
	public void test0310() {
		check("Integrate[Cot[e + f*x]^4*(b*Sec[e + f*x])^m, x]", 2617);
	}

	// {2617}
	public void test0311() {
		check("Integrate[Cot[e + f*x]^6*(b*Sec[e + f*x])^m, x]", 2617);
	}

	// {2617}
	public void test0312() {
		check("Integrate[(a*Sec[e + f*x])^m*(b*Tan[e + f*x])^n, x]", 2617);
	}

	// {2617}
	public void test0313() {
		check("Integrate[Sec[a + b*x]^5*(d*Tan[a + b*x])^n, x]", 2617);
	}

	// {2617}
	public void test0314() {
		check("Integrate[Sec[a + b*x]^3*(d*Tan[a + b*x])^n, x]", 2617);
	}

	// {2617}
	public void test0315() {
		check("Integrate[Sec[a + b*x]*(d*Tan[a + b*x])^n, x]", 2617);
	}

	// {2617}
	public void test0316() {
		check("Integrate[Cos[a + b*x]*(d*Tan[a + b*x])^n, x]", 2617);
	}

	// {2617}
	public void test0317() {
		check("Integrate[Cos[a + b*x]^3*(d*Tan[a + b*x])^n, x]", 2617);
	}

	// {2617}
	public void test0318() {
		check("Integrate[(b*Csc[e + f*x])^m*Tan[e + f*x]^4, x]", 2617);
	}

	// {2617}
	public void test0319() {
		check("Integrate[(b*Csc[e + f*x])^m*Tan[e + f*x]^2, x]", 2617);
	}

	// {2617}
	public void test0320() {
		check("Integrate[Cot[e + f*x]^2*(b*Csc[e + f*x])^m, x]", 2617);
	}

	// {2617}
	public void test0321() {
		check("Integrate[Cot[e + f*x]^4*(b*Csc[e + f*x])^m, x]", 2617);
	}

	// {-1}
	public void test0322() {
		check("Integrate[a + b*Tan[c + d*x^2], x]", -1);
	}

	// {3488}
	public void test0323() {
		check("Integrate[Cos[c + d*x]^3*(a + I*a*Tan[c + d*x])^3, x]", 3488);
	}

	// {3488}
	public void test0324() {
		check("Integrate[Cos[c + d*x]^5*(a + I*a*Tan[c + d*x])^5, x]", 3488);
	}

	// {3488}
	public void test0325() {
		check("Integrate[Sec[c + d*x]/(a + I*a*Tan[c + d*x]), x]", 3488);
	}

	// {3488}
	public void test0326() {
		check("Integrate[Sec[c + d*x]^3/(a + I*a*Tan[c + d*x])^3, x]", 3488);
	}

	// {3493}
	public void test0327() {
		check("Integrate[Sec[c + d*x]*Sqrt[a + I*a*Tan[c + d*x]], x]", 3493);
	}

	// {3493}
	public void test0328() {
		check("Integrate[Cos[c + d*x]*(a + I*a*Tan[c + d*x])^(3/2), x]", 3493);
	}

	// {3493}
	public void test0329() {
		check("Integrate[Cos[c + d*x]^3*(a + I*a*Tan[c + d*x])^(5/2), x]", 3493);
	}

	// {3493}
	public void test0330() {
		check("Integrate[Cos[c + d*x]^5*(a + I*a*Tan[c + d*x])^(7/2), x]", 3493);
	}

	// {3493}
	public void test0331() {
		check("Integrate[Sec[c + d*x]^3/Sqrt[a + I*a*Tan[c + d*x]], x]", 3493);
	}

	// {3493}
	public void test0332() {
		check("Integrate[Sec[c + d*x]^5/(a + I*a*Tan[c + d*x])^(3/2), x]", 3493);
	}

	// {3493}
	public void test0333() {
		check("Integrate[Sec[c + d*x]^7/(a + I*a*Tan[c + d*x])^(5/2), x]", 3493);
	}

	// {3493}
	public void test0334() {
		check("Integrate[Sec[c + d*x]^9/(a + I*a*Tan[c + d*x])^(7/2), x]", 3493);
	}

	// {3488}
	public void test0335() {
		check("Integrate[Sqrt[a + I*a*Tan[c + d*x]]/Sqrt[e*Sec[c + d*x]], x]", 3488);
	}

	// {3488}
	public void test0336() {
		check("Integrate[(a + I*a*Tan[c + d*x])^(3/2)/(e*Sec[c + d*x])^(3/2), x]", 3488);
	}

	// {3488}
	public void test0337() {
		check("Integrate[(a + I*a*Tan[c + d*x])^(5/2)/(e*Sec[c + d*x])^(5/2), x]", 3488);
	}

	// {3488}
	public void test0338() {
		check("Integrate[Sqrt[e*Sec[c + d*x]]/Sqrt[a + I*a*Tan[c + d*x]], x]", 3488);
	}

	// {3488}
	public void test0339() {
		check("Integrate[(e*Sec[c + d*x])^(3/2)/(a + I*a*Tan[c + d*x])^(3/2), x]", 3488);
	}

	// {3488}
	public void test0340() {
		check("Integrate[(e*Sec[c + d*x])^(5/2)/(a + I*a*Tan[c + d*x])^(5/2), x]", 3488);
	}

	// {3493}
	public void test0341() {
		check("Integrate[(d*Sec[e + f*x])^(2/3)*(a + I*a*Tan[e + f*x])^(2/3), x]", 3493);
	}

	// {3488}
	public void test0342() {
		check("Integrate[(a + I*a*Tan[c + d*x])^n/(e*Sec[c + d*x])^n, x]", 3488);
	}

	// {3493}
	public void test0343() {
		check("Integrate[(e*Sec[c + d*x])^(2 - 2*n)*(a + I*a*Tan[c + d*x])^n, x]", 3493);
	}

	// {3493}
	public void test0344() {
		check("Integrate[(d*Sec[e + f*x])^(2*n)*(a + I*a*Tan[e + f*x])^(1 - n), x]", 3493);
	}

	// {3507}
	public void test0345() {
		check("Integrate[Cos[c + d*x]*(a + b*Tan[c + d*x])^2, x]", 3507);
	}

	// {3669}
	public void test0346() {
		check("Integrate[(d*Cos[e + f*x])^m*(a + b*(c*Tan[e + f*x])^n)^p, x]", 3669);
	}

	// {3674}
	public void test0347() {
		check("Integrate[(d*Cot[e + f*x])^m*(a + b*(c*Tan[e + f*x])^n)^p, x]", 3674);
	}

	// {3681}
	public void test0348() {
		check("Integrate[(d*Csc[e + f*x])^m*(a + b*(c*Tan[e + f*x])^n)^p, x]", 3681);
	}

	// {3475}
	public void test0349() {
		check("Integrate[Cot[a + b*x], x]", 3475);
	}

	// {2617}
	public void test0350() {
		check("Integrate[(d*Cot[e + f*x])^n*Csc[e + f*x]^3, x]", 2617);
	}

	// {2617}
	public void test0351() {
		check("Integrate[(d*Cot[e + f*x])^n*Csc[e + f*x], x]", 2617);
	}

	// {2617}
	public void test0352() {
		check("Integrate[(d*Cot[e + f*x])^n*Sin[e + f*x], x]", 2617);
	}

	// {2617}
	public void test0353() {
		check("Integrate[(d*Cot[e + f*x])^n*Sin[e + f*x]^3, x]", 2617);
	}

	// {2617}
	public void test0354() {
		check("Integrate[(b*Cot[e + f*x])^n*(a*Csc[e + f*x])^m, x]", 2617);
	}

	// {3488}
	public void test0355() {
		check("Integrate[Csc[x]/(I + Cot[x]), x]", 3488);
	}

	// {3770}
	public void test0356() {
		check("Integrate[Sec[a + b*x], x]", 3770);
	}

	// {2619}
	public void test0357() {
		check("Integrate[(d*Csc[a + b*x])^(3/2)*Sqrt[c*Sec[a + b*x]], x]", 2619);
	}

	// {2619}
	public void test0358() {
		check("Integrate[Sqrt[d*Csc[a + b*x]]*(c*Sec[a + b*x])^(3/2), x]", 2619);
	}

	// {2619}
	public void test0359() {
		check("Integrate[(c*Sec[a + b*x])^(5/2)/Sqrt[d*Csc[a + b*x]], x]", 2619);
	}

	// {2619}
	public void test0360() {
		check("Integrate[(d*Csc[a + b*x])^(5/2)/Sqrt[c*Sec[a + b*x]], x]", 2619);
	}

	// {2619}
	public void test0361() {
		check("Integrate[(d*Csc[a + b*x])^(7/2)/(c*Sec[a + b*x])^(3/2), x]", 2619);
	}

	// {2619}
	public void test0362() {
		check("Integrate[(d*Csc[a + b*x])^(9/2)/(c*Sec[a + b*x])^(5/2), x]", 2619);
	}

	// {4208}
	public void test0363() {
		check("Integrate[(e*x)^m*(a + b*Sec[c + d*x^n])^p, x]", 4208);
	}

	// {3794}
	public void test0364() {
		check("Integrate[Sec[c + d*x]/(a + a*Sec[c + d*x]), x]", 3794);
	}

	// {3792}
	public void test0365() {
		check("Integrate[Sec[c + d*x]*Sqrt[a + a*Sec[c + d*x]], x]", 3792);
	}

	// {3792}
	public void test0366() {
		check("Integrate[Sec[c + d*x]*Sqrt[a - a*Sec[c + d*x]], x]", 3792);
	}

	// {3804}
	public void test0367() {
		check("Integrate[Sqrt[a + a*Sec[c + d*x]]/Sqrt[Sec[c + d*x]], x]", 3804);
	}

	// {3780}
	public void test0368() {
		check("Integrate[Sqrt[a + b*Sec[c + d*x]], x]", 3780);
	}

	// {3832}
	public void test0369() {
		check("Integrate[Sec[c + d*x]/Sqrt[a + b*Sec[c + d*x]], x]", 3832);
	}

	// {3784}
	public void test0370() {
		check("Integrate[1/Sqrt[a + b*Sec[c + d*x]], x]", 3784);
	}

	// {3780}
	public void test0371() {
		check("Integrate[Sqrt[a + b*Sec[e + f*x]], x]", 3780);
	}

	// {3784}
	public void test0372() {
		check("Integrate[1/Sqrt[a + b*Sec[e + f*x]], x]", 3784);
	}

	// {3889}
	public void test0373() {
		check("Integrate[(a + a*Sec[c + d*x])^n*(e*Tan[c + d*x])^m, x]", 3889);
	}

	// {3889}
	public void test0374() {
		check("Integrate[(a + a*Sec[c + d*x])^(3/2)*(e*Tan[c + d*x])^m, x]", 3889);
	}

	// {3889}
	public void test0375() {
		check("Integrate[Sqrt[a + a*Sec[c + d*x]]*(e*Tan[c + d*x])^m, x]", 3889);
	}

	// {3889}
	public void test0376() {
		check("Integrate[(e*Tan[c + d*x])^m/Sqrt[a + a*Sec[c + d*x]], x]", 3889);
	}

	// {3889}
	public void test0377() {
		check("Integrate[(e*Tan[c + d*x])^m/(a + a*Sec[c + d*x])^(3/2), x]", 3889);
	}

	// {3889}
	public void test0378() {
		check("Integrate[(a + a*Sec[c + d*x])^n*Tan[c + d*x]^4, x]", 3889);
	}

	// {3889}
	public void test0379() {
		check("Integrate[(a + a*Sec[c + d*x])^n*Tan[c + d*x]^2, x]", 3889);
	}

	// {3889}
	public void test0380() {
		check("Integrate[Cot[c + d*x]^2*(a + a*Sec[c + d*x])^n, x]", 3889);
	}

	// {3889}
	public void test0381() {
		check("Integrate[Cot[c + d*x]^4*(a + a*Sec[c + d*x])^n, x]", 3889);
	}

	// {3889}
	public void test0382() {
		check("Integrate[(a + a*Sec[c + d*x])^n*Tan[c + d*x]^(3/2), x]", 3889);
	}

	// {3889}
	public void test0383() {
		check("Integrate[(a + a*Sec[c + d*x])^n*Sqrt[Tan[c + d*x]], x]", 3889);
	}

	// {3889}
	public void test0384() {
		check("Integrate[(a + a*Sec[c + d*x])^n/Sqrt[Tan[c + d*x]], x]", 3889);
	}

	// {3889}
	public void test0385() {
		check("Integrate[(a + a*Sec[c + d*x])^n/Tan[c + d*x]^(3/2), x]", 3889);
	}

	// {3780}
	public void test0386() {
		check("Integrate[Sqrt[a + b*Sec[c + d*x]], x]", 3780);
	}

	// {3784}
	public void test0387() {
		check("Integrate[1/Sqrt[a + b*Sec[c + d*x]], x]", 3784);
	}

	// {3936}
	public void test0388() {
		check("Integrate[Sqrt[a + b*Sec[e + f*x]]/Sqrt[c + d*Sec[e + f*x]], x]", 3936);
	}

	// {3936}
	public void test0389() {
		check("Integrate[Sqrt[c + d*Sec[e + f*x]]/Sqrt[a + b*Sec[e + f*x]], x]", 3936);
	}

	// {3943}
	public void test0390() {
		check("Integrate[(a + b*Sec[e + f*x])^(1/3)/(c + d*Sec[e + f*x])^(1/3), x]", 3943);
	}

	// {3943}
	public void test0391() {
		check("Integrate[(a + b*Sec[e + f*x])^(2/3)/(c + d*Sec[e + f*x])^(2/3), x]", 3943);
	}

	// {3943}
	public void test0392() {
		check("Integrate[(a + b*Sec[e + f*x])^(4/3)/(c + d*Sec[e + f*x])^(4/3), x]", 3943);
	}

	// {3948}
	public void test0393() {
		check("Integrate[(c*(d*Sec[e + f*x])^p)^n*(a + b*Sec[e + f*x])^m, x]", 3948);
	}

	// {3950}
	public void test0394() {
		check("Integrate[(Sec[e + f*x]*(a + a*Sec[e + f*x]))/(c - c*Sec[e + f*x])^2, x]", 3950);
	}

	// {3950}
	public void test0395() {
		check("Integrate[(Sec[e + f*x]*(a + a*Sec[e + f*x])^2)/(c - c*Sec[e + f*x])^3, x]", 3950);
	}

	// {3950}
	public void test0396() {
		check("Integrate[(Sec[e + f*x]*(a + a*Sec[e + f*x])^3)/(c - c*Sec[e + f*x])^4, x]", 3950);
	}

	// {3950}
	public void test0397() {
		check("Integrate[(Sec[e + f*x]*(c - c*Sec[e + f*x]))/(a + a*Sec[e + f*x])^2, x]", 3950);
	}

	// {3950}
	public void test0398() {
		check("Integrate[(Sec[e + f*x]*(c - c*Sec[e + f*x])^2)/(a + a*Sec[e + f*x])^3, x]", 3950);
	}

	// {3953}
	public void test0399() {
		check("Integrate[Sec[e + f*x]*(a + a*Sec[e + f*x])*Sqrt[c - c*Sec[e + f*x]], x]", 3953);
	}

	// {3953}
	public void test0400() {
		check("Integrate[Sec[e + f*x]*(a + a*Sec[e + f*x])^2*Sqrt[c - c*Sec[e + f*x]], x]", 3953);
	}

	// {3953}
	public void test0401() {
		check("Integrate[Sec[e + f*x]*(a + a*Sec[e + f*x])^3*Sqrt[c - c*Sec[e + f*x]], x]", 3953);
	}

	// {3953}
	public void test0402() {
		check("Integrate[(Sec[e + f*x]*Sqrt[c - c*Sec[e + f*x]])/(a + a*Sec[e + f*x]), x]", 3953);
	}

	// {3953}
	public void test0403() {
		check("Integrate[(Sec[e + f*x]*Sqrt[c - c*Sec[e + f*x]])/(a + a*Sec[e + f*x])^2, x]", 3953);
	}

	// {3953}
	public void test0404() {
		check("Integrate[(Sec[e + f*x]*Sqrt[c - c*Sec[e + f*x]])/(a + a*Sec[e + f*x])^3, x]", 3953);
	}

	// {3953}
	public void test0405() {
		check("Integrate[Sec[e + f*x]*Sqrt[a + a*Sec[e + f*x]]*(c - c*Sec[e + f*x])^(5/2), x]", 3953);
	}

	// {3953}
	public void test0406() {
		check("Integrate[Sec[e + f*x]*Sqrt[a + a*Sec[e + f*x]]*(c - c*Sec[e + f*x])^(3/2), x]", 3953);
	}

	// {3953}
	public void test0407() {
		check("Integrate[Sec[e + f*x]*Sqrt[a + a*Sec[e + f*x]]*Sqrt[c - c*Sec[e + f*x]], x]", 3953);
	}

	// {3952}
	public void test0408() {
		check("Integrate[(Sec[e + f*x]*Sqrt[a + a*Sec[e + f*x]])/Sqrt[c - c*Sec[e + f*x]], x]", 3952);
	}

	// {3950}
	public void test0409() {
		check("Integrate[(Sec[e + f*x]*Sqrt[a + a*Sec[e + f*x]])/(c - c*Sec[e + f*x])^(3/2), x]", 3950);
	}

	// {3953}
	public void test0410() {
		check("Integrate[(Sec[e + f*x]*Sqrt[a + a*Sec[e + f*x]])/(c - c*Sec[e + f*x])^(5/2), x]", 3953);
	}

	// {3953}
	public void test0411() {
		check("Integrate[Sec[e + f*x]*(a + a*Sec[e + f*x])^(3/2)*Sqrt[c - c*Sec[e + f*x]], x]", 3953);
	}

	// {3950}
	public void test0412() {
		check("Integrate[(Sec[e + f*x]*(a + a*Sec[e + f*x])^(3/2))/(c - c*Sec[e + f*x])^(5/2), x]", 3950);
	}

	// {3953}
	public void test0413() {
		check("Integrate[Sec[e + f*x]*(a + a*Sec[e + f*x])^(5/2)*Sqrt[c - c*Sec[e + f*x]], x]", 3953);
	}

	// {3950}
	public void test0414() {
		check("Integrate[(Sec[e + f*x]*(a + a*Sec[e + f*x])^(5/2))/(c - c*Sec[e + f*x])^(7/2), x]", 3950);
	}

	// {3952}
	public void test0415() {
		check("Integrate[(Sec[e + f*x]*Sqrt[c - c*Sec[e + f*x]])/Sqrt[a + a*Sec[e + f*x]], x]", 3952);
	}

	// {3950}
	public void test0416() {
		check("Integrate[(Sec[e + f*x]*Sqrt[c - c*Sec[e + f*x]])/(a + a*Sec[e + f*x])^(3/2), x]", 3950);
	}

	// {3950}
	public void test0417() {
		check("Integrate[(Sec[e + f*x]*(c - c*Sec[e + f*x])^(3/2))/(a + a*Sec[e + f*x])^(5/2), x]", 3950);
	}

	// {3953}
	public void test0418() {
		check("Integrate[(Sec[e + f*x]*Sqrt[c - c*Sec[e + f*x]])/(a + a*Sec[e + f*x])^(5/2), x]", 3953);
	}

	// {3953}
	public void test0419() {
		check("Integrate[Sec[e + f*x]*(a + a*Sec[e + f*x])^m*Sqrt[c - c*Sec[e + f*x]], x]", 3953);
	}

	// {3950}
	public void test0420() {
		check("Integrate[Sec[e + f*x]*(a + a*Sec[e + f*x])^m*(c - c*Sec[e + f*x])^(-1 - m), x]", 3950);
	}

	// {3982}
	public void test0421() {
		check("Integrate[(Sec[e + f*x]*Sqrt[a + b*Sec[e + f*x]])/Sqrt[c + d*Sec[e + f*x]], x]", 3982);
	}

	// {3984}
	public void test0422() {
		check("Integrate[Sec[e + f*x]/(Sqrt[a + b*Sec[e + f*x]]*Sqrt[c + d*Sec[e + f*x]]), x]", 3984);
	}

	// {3984}
	public void test0423() {
		check("Integrate[Sec[e + f*x]/(Sqrt[2 + 3*Sec[e + f*x]]*Sqrt[-4 + 5*Sec[e + f*x]]), x]", 3984);
	}

	// {3984}
	public void test0424() {
		check("Integrate[Sec[e + f*x]/(Sqrt[4 - 5*Sec[e + f*x]]*Sqrt[2 + 3*Sec[e + f*x]]), x]", 3984);
	}

	// {3968}
	public void test0425() {
		check("Integrate[(Sec[e + f*x]*Sqrt[a + b*Sec[e + f*x]])/(c + c*Sec[e + f*x]), x]", 3968);
	}

	// {3973}
	public void test0426() {
		check("Integrate[Sec[e + f*x]/(Sqrt[a + b*Sec[e + f*x]]*(c + d*Sec[e + f*x])), x]", 3973);
	}

	// {4004}
	public void test0427() {
		check("Integrate[(Sec[e + f*x]*(A + A*Sec[e + f*x]))/Sqrt[a + b*Sec[e + f*x]], x]", 4004);
	}

	// {4004}
	public void test0428() {
		check("Integrate[(Sec[e + f*x]*(A - A*Sec[e + f*x]))/Sqrt[a + b*Sec[e + f*x]], x]", 4004);
	}

	// {4043}
	public void test0429() {
		check("Integrate[Sec[c + d*x]^m*(-((C*m)/(1 + m)) + C*Sec[c + d*x]^2), x]", 4043);
	}

	// {4043}
	public void test0430() {
		check("Integrate[Sec[c + d*x]^m*(A - (A*(1 + m)*Sec[c + d*x]^2)/m), x]", 4043);
	}

	// {4043}
	public void test0431() {
		check("Integrate[(3 + 3*Sec[c + d*x]^2)/Sqrt[Sec[c + d*x]], x]", 4043);
	}

	// {4043}
	public void test0432() {
		check("Integrate[Sec[e + f*x]^m*(m - (1 + m)*Sec[e + f*x]^2), x]", 4043);
	}

	// {4043}
	public void test0433() {
		check("Integrate[Sec[e + f*x]^5*(5 - 6*Sec[e + f*x]^2), x]", 4043);
	}

	// {4043}
	public void test0434() {
		check("Integrate[Sec[e + f*x]^4*(4 - 5*Sec[e + f*x]^2), x]", 4043);
	}

	// {4043}
	public void test0435() {
		check("Integrate[Sec[e + f*x]^3*(3 - 4*Sec[e + f*x]^2), x]", 4043);
	}

	// {4043}
	public void test0436() {
		check("Integrate[Sec[e + f*x]^2*(2 - 3*Sec[e + f*x]^2), x]", 4043);
	}

	// {4043}
	public void test0437() {
		check("Integrate[Sec[e + f*x]*(1 - 2*Sec[e + f*x]^2), x]", 4043);
	}

	// {2637}
	public void test0438() {
		check("Integrate[-Cos[e + f*x], x]", 2637);
	}

	// {4043}
	public void test0439() {
		check("Integrate[Cos[e + f*x]^2*(-2 + Sec[e + f*x]^2), x]", 4043);
	}

	// {4043}
	public void test0440() {
		check("Integrate[Cos[e + f*x]^3*(-3 + 2*Sec[e + f*x]^2), x]", 4043);
	}

	// {4043}
	public void test0441() {
		check("Integrate[Cos[e + f*x]^4*(-4 + 3*Sec[e + f*x]^2), x]", 4043);
	}

	// {4043}
	public void test0442() {
		check("Integrate[Cos[e + f*x]^5*(-5 + 4*Sec[e + f*x]^2), x]", 4043);
	}

	// {3770}
	public void test0443() {
		check("Integrate[Csc[a + b*x], x]", 3770);
	}

	// {4209}
	public void test0444() {
		check("Integrate[(e*x)^m*(a + b*Csc[c + d*x^n])^p, x]", 4209);
	}

	// {3794}
	public void test0445() {
		check("Integrate[Csc[x]/(a + a*Csc[x]), x]", 3794);
	}

	// {4282}
	public void test0446() {
		check("Integrate[Sin[a + b*x]*Sin[2*a + 2*b*x], x]", 4282);
	}

	// {4306}
	public void test0447() {
		check("Integrate[Sin[a + b*x]/Sqrt[Sin[2*a + 2*b*x]], x]", 4306);
	}

	// {4292}
	public void test0448() {
		check("Integrate[Sin[a + b*x]/Sin[2*a + 2*b*x]^(3/2), x]", 4292);
	}

	// {4292}
	public void test0449() {
		check("Integrate[Sin[a + b*x]^3/Sin[2*a + 2*b*x]^(5/2), x]", 4292);
	}

	// {4292}
	public void test0450() {
		check("Integrate[Csc[a + b*x]/Sqrt[Sin[2*a + 2*b*x]], x]", 4292);
	}

	// {4292}
	public void test0451() {
		check("Integrate[Csc[a + b*x]^3*Sqrt[Sin[2*a + 2*b*x]], x]", 4292);
	}

	// {4284}
	public void test0452() {
		check("Integrate[Cos[a + b*x]*Sin[2*a + 2*b*x], x]", 4284);
	}

	// {4305}
	public void test0453() {
		check("Integrate[Cos[a + b*x]/Sqrt[Sin[2*a + 2*b*x]], x]", 4305);
	}

	// {4291}
	public void test0454() {
		check("Integrate[Cos[a + b*x]/Sin[2*a + 2*b*x]^(3/2), x]", 4291);
	}

	// {4291}
	public void test0455() {
		check("Integrate[Cos[a + b*x]^3/Sin[2*a + 2*b*x]^(5/2), x]", 4291);
	}

	// {4305}
	public void test0456() {
		check("Integrate[Cos[x]/Sqrt[Sin[2*x]], x]", 4305);
	}

	// {3075}
	public void test0457() {
		check("Integrate[(a*Cos[x] + b*Sin[x])^(-2), x]", 3075);
	}

	// {3083}
	public void test0458() {
		check("Integrate[(a*Cos[c + d*x] + I*a*Sin[c + d*x])^n/Sin[c + d*x]^n, x]", 3083);
	}

	// {3075}
	public void test0459() {
		check("Integrate[(a*Cos[c + d*x] + b*Sin[c + d*x])^(-2), x]", 3075);
	}

	// {3071}
	public void test0460() {
		check("Integrate[(a*Cos[c + d*x] + I*a*Sin[c + d*x])^(-1), x]", 3071);
	}

	// {3071}
	public void test0461() {
		check("Integrate[(a*Cos[c + d*x] + I*a*Sin[c + d*x])^(-2), x]", 3071);
	}

	// {3071}
	public void test0462() {
		check("Integrate[(a*Cos[c + d*x] + I*a*Sin[c + d*x])^(-3), x]", 3071);
	}

	// {3084}
	public void test0463() {
		check("Integrate[(a*Cos[c + d*x] + I*a*Sin[c + d*x])^n/Cos[c + d*x]^n, x]", 3084);
	}

	// {4415}
	public void test0464() {
		check("Integrate[(c + d*x)^m*Cot[a + b*x]^2*Csc[a + b*x], x]", 4415);
	}

	// {4415}
	public void test0465() {
		check("Integrate[(Cot[a + b*x]^2*Csc[a + b*x])/(c + d*x), x]", 4415);
	}

	// {4415}
	public void test0466() {
		check("Integrate[(Cot[a + b*x]^2*Csc[a + b*x])/(c + d*x)^2, x]", 4415);
	}

	// {4419}
	public void test0467() {
		check("Integrate[(Csc[a + b*x]*Sec[a + b*x])/(c + d*x), x]", 4419);
	}

	// {4419}
	public void test0468() {
		check("Integrate[(Csc[a + b*x]*Sec[a + b*x])/(c + d*x)^2, x]", 4419);
	}

	// {4419}
	public void test0469() {
		check("Integrate[(Csc[a + b*x]^2*Sec[a + b*x]^2)/(c + d*x), x]", 4419);
	}

	// {4419}
	public void test0470() {
		check("Integrate[(Csc[a + b*x]^2*Sec[a + b*x]^2)/(c + d*x)^2, x]", 4419);
	}

	// {4413}
	public void test0471() {
		check("Integrate[(c + d*x)^m*Sec[a + b*x]*Tan[a + b*x]^2, x]", 4413);
	}

	// {4413}
	public void test0472() {
		check("Integrate[(Sec[a + b*x]*Tan[a + b*x]^2)/(c + d*x), x]", 4413);
	}

	// {4413}
	public void test0473() {
		check("Integrate[(Sec[a + b*x]*Tan[a + b*x]^2)/(c + d*x)^2, x]", 4413);
	}

	// {4419}
	public void test0474() {
		check("Integrate[(Csc[a + b*x]^3*Sec[a + b*x]^3)/(c + d*x), x]", 4419);
	}

	// {4419}
	public void test0475() {
		check("Integrate[(Csc[a + b*x]^3*Sec[a + b*x]^3)/(c + d*x)^2, x]", 4419);
	}

	// {4485}
	public void test0476() {
		check("Integrate[x^2*Sin[a + b*Log[c*x^n]], x]", 4485);
	}

	// {4485}
	public void test0477() {
		check("Integrate[x*Sin[a + b*Log[c*x^n]], x]", 4485);
	}

	// {4475}
	public void test0478() {
		check("Integrate[Sin[a + b*Log[c*x^n]], x]", 4475);
	}

	// {4485}
	public void test0479() {
		check("Integrate[Sin[a + b*Log[c*x^n]]/x^2, x]", 4485);
	}

	// {4485}
	public void test0480() {
		check("Integrate[Sin[a + b*Log[c*x^n]]/x^3, x]", 4485);
	}

	// {4485}
	public void test0481() {
		check("Integrate[(e*x)^m*Sin[d*(a + b*Log[c*x^n])], x]", 4485);
	}

	// {4486}
	public void test0482() {
		check("Integrate[x^2*Cos[a + b*Log[c*x^n]], x]", 4486);
	}

	// {4486}
	public void test0483() {
		check("Integrate[x*Cos[a + b*Log[c*x^n]], x]", 4486);
	}

	// {4476}
	public void test0484() {
		check("Integrate[Cos[a + b*Log[c*x^n]], x]", 4476);
	}

	// {4486}
	public void test0485() {
		check("Integrate[Cos[a + b*Log[c*x^n]]/x^2, x]", 4486);
	}

	// {4486}
	public void test0486() {
		check("Integrate[x^m*Cos[a + b*Log[c*x^n]], x]", 4486);
	}

	// {4432}
	public void test0487() {
		check("Integrate[F^(c*(a + b*x))*Sin[d + e*x], x]", 4432);
	}

	// {4453}
	public void test0488() {
		check("Integrate[F^(c*(a + b*x))*Csc[d + e*x], x]", 4453);
	}

	// {4453}
	public void test0489() {
		check("Integrate[F^(c*(a + b*x))*Csc[d + e*x]^2, x]", 4453);
	}

	// {4433}
	public void test0490() {
		check("Integrate[F^(c*(a + b*x))*Cos[d + e*x], x]", 4433);
	}

	// {4451}
	public void test0491() {
		check("Integrate[F^(c*(a + b*x))*Sec[d + e*x], x]", 4451);
	}

	// {4451}
	public void test0492() {
		check("Integrate[F^(c*(a + b*x))*Sec[d + e*x]^2, x]", 4451);
	}

	// {6741}
	public void test0493() {
		check("Integrate[F^(c*(a + b*x))*(f*x)^m*Csc[d + e*x], x]", 6741);
	}

	// {6741}
	public void test0494() {
		check("Integrate[F^(c*(a + b*x))*(f*x)^m*Csc[d + e*x]^2, x]", 6741);
	}

	// {2288}
	public void test0495() {
		check("Integrate[F^(c*(a + b*x))*(e*Cos[d + e*x] + b*c*Log[F]*Sin[d + e*x]), x]", 2288);
	}

	// {4432}
	public void test0496() {
		check("Integrate[E^x*Sin[a + b*x], x]", 4432);
	}

	// {4433}
	public void test0497() {
		check("Integrate[E^x*Cos[a + b*x], x]", 4433);
	}

	// {6681}
	public void test0498() {
		check("Integrate[Csc[Sqrt[1 - a*x]/Sqrt[1 + a*x]]/(1 - a^2*x^2), x]", 6681);
	}

	// {6681}
	public void test0499() {
		check("Integrate[Csc[Sqrt[1 - a*x]/Sqrt[1 + a*x]]^2/(1 - a^2*x^2), x]", 6681);
	}

	// {6681}
	public void test0500() {
		check("Integrate[Sec[Sqrt[1 - a*x]/Sqrt[1 + a*x]]/(1 - a^2*x^2), x]", 6681);
	}

	// {6681}
	public void test0501() {
		check("Integrate[Sec[Sqrt[1 - a*x]/Sqrt[1 + a*x]]^2/(1 - a^2*x^2), x]", 6681);
	}

	// {4282}
	public void test0502() {
		check("Integrate[Sin[x]*Sin[2*x], x]", 4282);
	}

	// {4282}
	public void test0503() {
		check("Integrate[Sin[x]*Sin[3*x], x]", 4282);
	}

	// {4282}
	public void test0504() {
		check("Integrate[Sin[x]*Sin[4*x], x]", 4282);
	}

	// {4284}
	public void test0505() {
		check("Integrate[Cos[2*x]*Sin[x], x]", 4284);
	}

	// {4284}
	public void test0506() {
		check("Integrate[Cos[3*x]*Sin[x], x]", 4284);
	}

	// {4284}
	public void test0507() {
		check("Integrate[Cos[4*x]*Sin[x], x]", 4284);
	}

	// {4284}
	public void test0508() {
		check("Integrate[Cos[x]*Sin[2*x], x]", 4284);
	}

	// {4284}
	public void test0509() {
		check("Integrate[Cos[x]*Sin[3*x], x]", 4284);
	}

	// {4284}
	public void test0510() {
		check("Integrate[Cos[x]*Sin[4*x], x]", 4284);
	}

	// {4283}
	public void test0511() {
		check("Integrate[Cos[x]*Cos[2*x], x]", 4283);
	}

	// {4283}
	public void test0512() {
		check("Integrate[Cos[x]*Cos[3*x], x]", 4283);
	}

	// {4283}
	public void test0513() {
		check("Integrate[Cos[x]*Cos[4*x], x]", 4283);
	}

	// {3075}
	public void test0514() {
		check("Integrate[(a*Cos[c + d*x] + b*Sin[c + d*x])^(-2), x]", 3075);
	}

	// {3071}
	public void test0515() {
		check("Integrate[(a*Cos[c + d*x] + I*a*Sin[c + d*x])^n, x]", 3071);
	}

	// {3071}
	public void test0516() {
		check("Integrate[(a*Cos[c + d*x] + I*a*Sin[c + d*x])^4, x]", 3071);
	}

	// {3071}
	public void test0517() {
		check("Integrate[(a*Cos[c + d*x] + I*a*Sin[c + d*x])^3, x]", 3071);
	}

	// {3071}
	public void test0518() {
		check("Integrate[(a*Cos[c + d*x] + I*a*Sin[c + d*x])^2, x]", 3071);
	}

	// {3071}
	public void test0519() {
		check("Integrate[(a*Cos[c + d*x] + I*a*Sin[c + d*x])^(-1), x]", 3071);
	}

	// {3071}
	public void test0520() {
		check("Integrate[(a*Cos[c + d*x] + I*a*Sin[c + d*x])^(-2), x]", 3071);
	}

	// {3071}
	public void test0521() {
		check("Integrate[(a*Cos[c + d*x] + I*a*Sin[c + d*x])^(-3), x]", 3071);
	}

	// {3071}
	public void test0522() {
		check("Integrate[(a*Cos[c + d*x] + I*a*Sin[c + d*x])^(-4), x]", 3071);
	}

	// {3071}
	public void test0523() {
		check("Integrate[(a*Cos[c + d*x] + I*a*Sin[c + d*x])^(5/2), x]", 3071);
	}

	// {3071}
	public void test0524() {
		check("Integrate[(a*Cos[c + d*x] + I*a*Sin[c + d*x])^(3/2), x]", 3071);
	}

	// {3071}
	public void test0525() {
		check("Integrate[Sqrt[a*Cos[c + d*x] + I*a*Sin[c + d*x]], x]", 3071);
	}

	// {3071}
	public void test0526() {
		check("Integrate[1/Sqrt[a*Cos[c + d*x] + I*a*Sin[c + d*x]], x]", 3071);
	}

	// {3071}
	public void test0527() {
		check("Integrate[(a*Cos[c + d*x] + I*a*Sin[c + d*x])^(-3/2), x]", 3071);
	}

	// {3071}
	public void test0528() {
		check("Integrate[(a*Cos[c + d*x] + I*a*Sin[c + d*x])^(-5/2), x]", 3071);
	}

	// {3114}
	public void test0529() {
		check("Integrate[(Sqrt[b^2 + c^2] + b*Cos[d + e*x] + c*Sin[d + e*x])^(-1), x]", 3114);
	}

	// {3112}
	public void test0530() {
		check("Integrate[Sqrt[5 + 4*Cos[d + e*x] + 3*Sin[d + e*x]], x]", 3112);
	}

	// {3112}
	public void test0531() {
		check("Integrate[Sqrt[-5 + 4*Cos[d + e*x] + 3*Sin[d + e*x]], x]", 3112);
	}

	// {3112}
	public void test0532() {
		check("Integrate[Sqrt[Sqrt[b^2 + c^2] + b*Cos[d + e*x] + c*Sin[d + e*x]], x]", 3112);
	}

	// {3112}
	public void test0533() {
		check("Integrate[Sqrt[-Sqrt[b^2 + c^2] + b*Cos[d + e*x] + c*Sin[d + e*x]], x]", 3112);
	}

	// {4385}
	public void test0534() {
		check("Integrate[(Cos[x] - I*Sin[x])/(Cos[x] + I*Sin[x]), x]", 4385);
	}

	// {4385}
	public void test0535() {
		check("Integrate[(Cos[x] + I*Sin[x])/(Cos[x] - I*Sin[x]), x]", 4385);
	}

	// {3133}
	public void test0536() {
		check("Integrate[(Cos[x] - Sin[x])/(Cos[x] + Sin[x]), x]", 3133);
	}

	// {3133}
	public void test0537() {
		check("Integrate[(B*Cos[x] + C*Sin[x])/(b*Cos[x] + c*Sin[x]), x]", 3133);
	}

	// {3132}
	public void test0538() {
		check("Integrate[(A + B*Cos[x])/(a + b*Cos[x] + I*b*Sin[x]), x]", 3132);
	}

	// {3132}
	public void test0539() {
		check("Integrate[(A + B*Cos[x])/(a + b*Cos[x] - I*b*Sin[x]), x]", 3132);
	}

	// {3131}
	public void test0540() {
		check("Integrate[(A + C*Sin[x])/(a + b*Cos[x] + I*b*Sin[x]), x]", 3131);
	}

	// {3131}
	public void test0541() {
		check("Integrate[(A + C*Sin[x])/(a + b*Cos[x] - I*b*Sin[x]), x]", 3131);
	}

	// {3130}
	public void test0542() {
		check("Integrate[(B*Cos[x] + C*Sin[x])/(a + b*Cos[x] + I*b*Sin[x]), x]", 3130);
	}

	// {3130}
	public void test0543() {
		check("Integrate[(B*Cos[x] + C*Sin[x])/(a + b*Cos[x] - I*b*Sin[x]), x]", 3130);
	}

	// {3130}
	public void test0544() {
		check("Integrate[(A + B*Cos[x] + C*Sin[x])/(a + b*Cos[x] + I*b*Sin[x]), x]", 3130);
	}

	// {3130}
	public void test0545() {
		check("Integrate[(A + B*Cos[x] + C*Sin[x])/(a + b*Cos[x] - I*b*Sin[x]), x]", 3130);
	}

	// {4584}
	public void test0546() {
		check("Integrate[1/(x*(a + b*Cos[x]*Sin[x])), x]", 4584);
	}

	// {4598}
	public void test0547() {
		check("Integrate[((b*x)^(2 - n)*Sin[a*x]^n)/(a*c*x*Cos[a*x] - c*Sin[a*x])^2, x]", 4598);
	}

	// {4599}
	public void test0548() {
		check("Integrate[((b*x)^(2 - n)*Cos[a*x]^n)/(c*Cos[a*x] + a*c*x*Sin[a*x])^2, x]", 4599);
	}

	// {4596}
	public void test0549() {
		check("Integrate[Sin[a*x]^2/(a*x*Cos[a*x] - Sin[a*x])^2, x]", 4596);
	}

	// {6686}
	public void test0550() {
		check("Integrate[(x*Sin[a*x])/(a*x*Cos[a*x] - Sin[a*x])^2, x]", 6686);
	}

	// {4597}
	public void test0551() {
		check("Integrate[Cos[a*x]^2/(Cos[a*x] + a*x*Sin[a*x])^2, x]", 4597);
	}

	// {6686}
	public void test0552() {
		check("Integrate[(x*Cos[a*x])/(Cos[a*x] + a*x*Sin[a*x])^2, x]", 6686);
	}

	// {4385}
	public void test0553() {
		check("Integrate[(b*Sec[c + d*x] + a*Sin[c + d*x])^n*(a*Cos[c + d*x] + b*Sec[c + d*x]*Tan[c + d*x]), x]", 4385);
	}

	// {4385}
	public void test0554() {
		check("Integrate[(b*Sec[c + d*x] + a*Sin[c + d*x])^3*(a*Cos[c + d*x] + b*Sec[c + d*x]*Tan[c + d*x]), x]", 4385);
	}

	// {4385}
	public void test0555() {
		check("Integrate[(b*Sec[c + d*x] + a*Sin[c + d*x])^2*(a*Cos[c + d*x] + b*Sec[c + d*x]*Tan[c + d*x]), x]", 4385);
	}

	// {4385}
	public void test0556() {
		check("Integrate[(b*Sec[c + d*x] + a*Sin[c + d*x])*(a*Cos[c + d*x] + b*Sec[c + d*x]*Tan[c + d*x]), x]", 4385);
	}

	// {4383}
	public void test0557() {
		check("Integrate[(a*Cos[c + d*x] + b*Sec[c + d*x]*Tan[c + d*x])/(b*Sec[c + d*x] + a*Sin[c + d*x]), x]", 4383);
	}

	// {4385}
	public void test0558() {
		check("Integrate[(a*Cos[c + d*x] + b*Sec[c + d*x]*Tan[c + d*x])/(b*Sec[c + d*x] + a*Sin[c + d*x])^2, x]", 4385);
	}

	// {4385}
	public void test0559() {
		check("Integrate[(a*Cos[c + d*x] + b*Sec[c + d*x]*Tan[c + d*x])/(b*Sec[c + d*x] + a*Sin[c + d*x])^3, x]", 4385);
	}

	// {4335}
	public void test0560() {
		check("Integrate[f[c, d, Cos[a + b*x], r, s]*Sin[a + b*x], x]", 4335);
	}

	// {4334}
	public void test0561() {
		check("Integrate[Cos[a + b*x]*f[c, d, Sin[a + b*x], r, s], x]", 4334);
	}

	// {4342}
	public void test0562() {
		check("Integrate[f[c, d, Tan[a + b*x], r, s]*Sec[a + b*x]^2, x]", 4342);
	}

	// {4344}
	public void test0563() {
		check("Integrate[Csc[a + b*x]^2*f[c, d, Cot[a + b*x], r, s], x]", 4344);
	}

	// {6686}
	public void test0564() {
		check("Integrate[Csc[x]*Log[Tan[x]]*Sec[x], x]", 6686);
	}

	// {6686}
	public void test0565() {
		check("Integrate[Csc[2*x]*Log[Tan[x]], x]", 6686);
	}

	// {4433}
	public void test0566() {
		check("Integrate[Cos[2*Pi*x]/E^(2*Pi*x), x]", 4433);
	}

	// {2638}
	public void test0567() {
		check("Integrate[Sin[Pi*(1 + 2*x)], x]", 2638);
	}

	// {4433}
	public void test0568() {
		check("Integrate[Cos[x]/E^(3*x), x]", 4433);
	}

	// {6686}
	public void test0569() {
		check("Integrate[(1 + Cos[x])*(x + Sin[x])^3, x]", 6686);
	}

	// {4433}
	public void test0570() {
		check("Integrate[Cos[4*x]/E^(3*x), x]", 4433);
	}

	// {3441}
	public void test0571() {
		check("Integrate[(Cos[x^(-1)]*Sin[x^(-1)])/x^2, x]", 3441);
	}

	// {3441}
	public void test0572() {
		check("Integrate[(Cos[Sqrt[x]]*Sin[Sqrt[x]])/Sqrt[x], x]", 3441);
	}

	// {3441}
	public void test0573() {
		check("Integrate[(x*Cos[x^2])/Sqrt[Sin[x^2]], x]", 3441);
	}

	// {3145}
	public void test0574() {
		check("Integrate[(-Cos[x] + Sin[x])*(Cos[x] + Sin[x])^5, x]", 3145);
	}

	// {2563}
	public void test0575() {
		check("Integrate[Cos[a + b*x]^n*Sin[a + b*x]^(-2 - n), x]", 2563);
	}

	// {3375}
	public void test0576() {
		check("Integrate[Sin[x^5]/x, x]", 3375);
	}

	// {3441}
	public void test0577() {
		check("Integrate[x*Cos[2*x^2]*Sin[2*x^2]^(3/4), x]", 3441);
	}

	// {6686}
	public void test0578() {
		check("Integrate[x*Sec[x^2]^2*Tan[x^2]^2, x]", 6686);
	}

	// {3442}
	public void test0579() {
		check("Integrate[x^2*Cos[a + b*x^3]^7*Sin[a + b*x^3], x]", 3442);
	}

	// {3133}
	public void test0580() {
		check("Integrate[(Cos[a + b*x] - Sin[a + b*x])/(Cos[a + b*x] + Sin[a + b*x]), x]", 3133);
	}
}
