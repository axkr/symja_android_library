package org.matheclipse.core.rubi;

public class AlgebraicFunctions extends AbstractRubiTestCase {

	public AlgebraicFunctions(String name) {
		super(name, false);
	}

	// {8}
	public void test0001() {
		check(//
				"Integrate[0, x]", //
				"0", //
				8);
	}

	// {8}
	public void test0002() {
		check(//
				"Integrate[1, x]", //
				"x", //
				8);
	}

	// {8}
	public void test0003() {
		check(//
				"Integrate[5, x]", //
				"5*x", //
				8);
	}

	// {8}
	public void test0004() {
		check(//
				"Integrate[-2, x]", //
				"-2*x", //
				8);
	}

	// {8}
	public void test0005() {
		check(//
				"Integrate[-3/2, x]", //
				"(-3*x)/2", //
				8);
	}

	// {8}
	public void test0006() {
		check(//
				"Integrate[Pi, x]", //
				"Pi*x", //
				8);
	}

	// {8}
	public void test0007() {
		check(//
				"Integrate[a, x]", //
				"a*x", //
				8);
	}

	// {8}
	public void test0008() {
		check(//
				"Integrate[3*a, x]", //
				"3*a*x", //
				8);
	}

	// {8}
	public void test0009() {
		check(//
				"Integrate[Pi/Sqrt[16 - E^2], x]", //
				"(Pi*x)/Sqrt[16 - E^2]", //
				8);
	}

	// {30}
	public void test0010() {
		check(//
				"Integrate[x^100, x]", //
				"x^101/101", //
				30);
	}

	// {30}
	public void test0011() {
		check(//
				"Integrate[x^3, x]", //
				"x^4/4", //
				30);
	}

	// {30}
	public void test0012() {
		check(//
				"Integrate[x^2, x]", //
				"x^3/3", //
				30);
	}

	// {30}
	public void test0013() {
		check(//
				"Integrate[x, x]", //
				"x^2/2", //
				30);
	}

	// {8}
	public void test0014() {
		check(//
				"Integrate[1, x]", //
				"x", //
				8);
	}

	// {29}
	public void test0015() {
		check(//
				"Integrate[x^(-1), x]", //
				"Log[x]", //
				29);
	}

	// {30}
	public void test0016() {
		check(//
				"Integrate[x^(-2), x]", //
				"-x^(-1)", //
				30);
	}

	// {30}
	public void test0017() {
		check(//
				"Integrate[x^(-3), x]", //
				"-1/(2*x^2)", //
				30);
	}

	// {30}
	public void test0018() {
		check(//
				"Integrate[x^(-4), x]", //
				"-1/(3*x^3)", //
				30);
	}

	// {30}
	public void test0019() {
		check(//
				"Integrate[x^(-100), x]", //
				"-1/(99*x^99)", //
				30);
	}

	// {30}
	public void test0020() {
		check(//
				"Integrate[x^(5/2), x]", //
				"(2*x^(7/2))/7", //
				30);
	}

	// {30}
	public void test0021() {
		check(//
				"Integrate[x^(3/2), x]", //
				"(2*x^(5/2))/5", //
				30);
	}

	// {30}
	public void test0022() {
		check(//
				"Integrate[Sqrt[x], x]", //
				"(2*x^(3/2))/3", //
				30);
	}

	// {30}
	public void test0023() {
		check(//
				"Integrate[1/Sqrt[x], x]", //
				"2*Sqrt[x]", //
				30);
	}

	// {30}
	public void test0024() {
		check(//
				"Integrate[x^(-3/2), x]", //
				"-2/Sqrt[x]", //
				30);
	}

	// {30}
	public void test0025() {
		check(//
				"Integrate[x^(-5/2), x]", //
				"-2/(3*x^(3/2))", //
				30);
	}

	// {30}
	public void test0026() {
		check(//
				"Integrate[x^(5/3), x]", //
				"(3*x^(8/3))/8", //
				30);
	}

	// {30}
	public void test0027() {
		check(//
				"Integrate[x^(4/3), x]", //
				"(3*x^(7/3))/7", //
				30);
	}

	// {30}
	public void test0028() {
		check(//
				"Integrate[x^(2/3), x]", //
				"(3*x^(5/3))/5", //
				30);
	}

	// {30}
	public void test0029() {
		check(//
				"Integrate[x^(1/3), x]", //
				"(3*x^(4/3))/4", //
				30);
	}

	// {30}
	public void test0030() {
		check(//
				"Integrate[x^(-1/3), x]", //
				"(3*x^(2/3))/2", //
				30);
	}

	// {30}
	public void test0031() {
		check(//
				"Integrate[x^(-2/3), x]", //
				"3*x^(1/3)", //
				30);
	}

	// {30}
	public void test0032() {
		check(//
				"Integrate[x^(-4/3), x]", //
				"-3/x^(1/3)", //
				30);
	}

	// {30}
	public void test0033() {
		check(//
				"Integrate[x^(-5/3), x]", //
				"-3/(2*x^(2/3))", //
				30);
	}

	// {30}
	public void test0034() {
		check(//
				"Integrate[x^n, x]", //
				"x^(1 + n)/(1 + n)", //
				30);
	}

	// {32}
	public void test0035() {
		check(//
				"Integrate[(b*x)^n, x]", //
				"(b*x)^(1 + n)/(b*(1 + n))", //
				32);
	}

	// {37}
	public void test0037() {
		check(//
				"Integrate[(a + b*x)/x^3, x]", //
				"-(a + b*x)^2/(2*a*x^2)", //
				37);
	}

	// {32}
	public void test0038() {
		check(//
				"Integrate[(a + b*x)^2, x]", //
				"(a + b*x)^3/(3*b)", //
				32);
	}

	// {37}
	public void test0039() {
		check(//
				"Integrate[(a + b*x)^2/x^4, x]", //
				"-(a + b*x)^3/(3*a*x^3)", //
				37);
	}

	// {32}
	public void test0040() {
		check(//
				"Integrate[(a + b*x)^3, x]", //
				"(a + b*x)^4/(4*b)", //
				32);
	}

	// {37}
	public void test0041() {
		check(//
				"Integrate[(a + b*x)^3/x^5, x]", //
				"-(a + b*x)^4/(4*a*x^4)", //
				37);
	}

	// {32}
	public void test0042() {
		check(//
				"Integrate[(a + b*x)^5, x]", //
				"(a + b*x)^6/(6*b)", //
				32);
	}

	// {37}
	public void test0043() {
		check(//
				"Integrate[(a + b*x)^5/x^7, x]", //
				"-(a + b*x)^6/(6*a*x^6)", //
				37);
	}

	// {32}
	public void test0044() {
		check(//
				"Integrate[(a + b*x)^7, x]", //
				"(a + b*x)^8/(8*b)", //
				32);
	}

	// {37}
	public void test0045() {
		check(//
				"Integrate[(a + b*x)^7/x^9, x]", //
				"-(a + b*x)^8/(8*a*x^8)", //
				37);
	}

	// {32}
	public void test0046() {
		check(//
				"Integrate[(a + b*x)^10, x]", //
				"(a + b*x)^11/(11*b)", //
				32);
	}

	// {37}
	public void test0047() {
		check(//
				"Integrate[(a + b*x)^10/x^12, x]", //
				"-(a + b*x)^11/(11*a*x^11)", //
				37);
	}

	// {9}
	public void test0048() {
		check(//
				"Integrate[c*(a + b*x), x]", //
				"(c*(a + b*x)^2)/(2*b)", //
				9);
	}

	// {9}
	public void test0049() {
		check(//
				"Integrate[((c + d)*(a + b*x))/e, x]", //
				"((c + d)*(a + b*x)^2)/(2*b*e)", //
				9);
	}

	// {31}
	public void test0050() {
		check(//
				"Integrate[(a + b*x)^(-1), x]", //
				"Log[a + b*x]/b", //
				31);
	}

	// {32}
	public void test0051() {
		check(//
				"Integrate[(a + b*x)^(-2), x]", //
				"-(1/(b*(a + b*x)))", //
				32);
	}

	// {37}
	public void test0052() {
		check(//
				"Integrate[x/(a + b*x)^3, x]", //
				"x^2/(2*a*(a + b*x)^2)", //
				37);
	}

	// {32}
	public void test0053() {
		check(//
				"Integrate[(a + b*x)^(-3), x]", //
				"-1/(2*b*(a + b*x)^2)", //
				32);
	}

	// {37}
	public void test0054() {
		check(//
				"Integrate[x^2/(a + b*x)^4, x]", //
				"x^3/(3*a*(a + b*x)^3)", //
				37);
	}

	// {32}
	public void test0055() {
		check(//
				"Integrate[(a + b*x)^(-4), x]", //
				"-1/(3*b*(a + b*x)^3)", //
				32);
	}

	// {37}
	public void test0056() {
		check(//
				"Integrate[x^5/(a + b*x)^7, x]", //
				"x^6/(6*a*(a + b*x)^6)", //
				37);
	}

	// {32}
	public void test0057() {
		check(//
				"Integrate[(a + b*x)^(-7), x]", //
				"-1/(6*b*(a + b*x)^6)", //
				32);
	}

	// {37}
	public void test0058() {
		check(//
				"Integrate[x^8/(a + b*x)^10, x]", //
				"x^9/(9*a*(a + b*x)^9)", //
				37);
	}

	// {32}
	public void test0059() {
		check(//
				"Integrate[(a + b*x)^(-10), x]", //
				"-1/(9*b*(a + b*x)^9)", //
				32);
	}

	// {37}
	public void test0060() {
		check(//
				"Integrate[(a + b*x)^8/x^10, x]", //
				"-(a + b*x)^9/(9*a*x^9)", //
				37);
	}

	// {30}
	public void test0061() {
		check(//
				"Integrate[x^(-10), x]", //
				"-1/(9*x^9)", //
				30);
	}

	// {31}
	public void test0062() {
		check(//
				"Integrate[(2 + 2*x)^(-1), x]", //
				"Log[1 + x]/2", //
				31);
	}

	// {31}
	public void test0063() {
		check(//
				"Integrate[(4 - 6*x)^(-1), x]", //
				"-Log[2 - 3*x]/6", //
				31);
	}

	// {31}
	public void test0064() {
		check(//
				"Integrate[(a + Sqrt[a]*x)^(-1), x]", //
				"Log[Sqrt[a] + x]/Sqrt[a]", //
				31);
	}

	// {31}
	public void test0065() {
		check(//
				"Integrate[(a + Sqrt[-a]*x)^(-1), x]", //
				"Log[a + Sqrt[-a]*x]/Sqrt[-a]", //
				31);
	}

	// {31}
	public void test0066() {
		check(//
				"Integrate[(a^2 + Sqrt[-a]*x)^(-1), x]", //
				"Log[a^2 + Sqrt[-a]*x]/Sqrt[-a]", //
				31);
	}

	// {31}
	public void test0067() {
		check(//
				"Integrate[(a^3 + Sqrt[-a]*x)^(-1), x]", //
				"Log[a^3 + Sqrt[-a]*x]/Sqrt[-a]", //
				31);
	}

	// {31}
	public void test0068() {
		check(//
				"Integrate[(a^(-1) + Sqrt[-a]*x)^(-1), x]", //
				"Log[1 - (-a)^(3/2)*x]/Sqrt[-a]", //
				31);
	}

	// {31}
	public void test0069() {
		check(//
				"Integrate[(a^(-2) + Sqrt[-a]*x)^(-1), x]", //
				"Log[1 + (-a)^(5/2)*x]/Sqrt[-a]", //
				31);
	}

	// {32}
	public void test0070() {
		check(//
				"Integrate[Sqrt[a + b*x], x]", //
				"(2*(a + b*x)^(3/2))/(3*b)", //
				32);
	}

	// {32}
	public void test0071() {
		check(//
				"Integrate[(a + b*x)^(3/2), x]", //
				"(2*(a + b*x)^(5/2))/(5*b)", //
				32);
	}

	// {32}
	public void test0072() {
		check(//
				"Integrate[(a + b*x)^(5/2), x]", //
				"(2*(a + b*x)^(7/2))/(7*b)", //
				32);
	}

	// {32}
	public void test0073() {
		check(//
				"Integrate[(a + b*x)^(9/2), x]", //
				"(2*(a + b*x)^(11/2))/(11*b)", //
				32);
	}

	// {32}
	public void test0074() {
		check(//
				"Integrate[1/Sqrt[a + b*x], x]", //
				"(2*Sqrt[a + b*x])/b", //
				32);
	}

	// {32}
	public void test0075() {
		check(//
				"Integrate[(a + b*x)^(-3/2), x]", //
				"-2/(b*Sqrt[a + b*x])", //
				32);
	}

	// {32}
	public void test0076() {
		check(//
				"Integrate[(a + b*x)^(-5/2), x]", //
				"-2/(3*b*(a + b*x)^(3/2))", //
				32);
	}

	// {32}
	public void test0077() {
		check(//
				"Integrate[(a + b*x)^(1/3), x]", //
				"(3*(a + b*x)^(4/3))/(4*b)", //
				32);
	}

	// {32}
	public void test0078() {
		check(//
				"Integrate[(a + b*x)^(2/3), x]", //
				"(3*(a + b*x)^(5/3))/(5*b)", //
				32);
	}

	// {32}
	public void test0079() {
		check(//
				"Integrate[(a + b*x)^(4/3), x]", //
				"(3*(a + b*x)^(7/3))/(7*b)", //
				32);
	}

	// {32}
	public void test0080() {
		check(//
				"Integrate[(a + b*x)^(-1/3), x]", //
				"(3*(a + b*x)^(2/3))/(2*b)", //
				32);
	}

	// {32}
	public void test0081() {
		check(//
				"Integrate[(-a + b*x)^(-1/3), x]", //
				"(3*(-a + b*x)^(2/3))/(2*b)", //
				32);
	}

	// {32}
	public void test0082() {
		check(//
				"Integrate[(a + b*x)^(-2/3), x]", //
				"(3*(a + b*x)^(1/3))/b", //
				32);
	}

	// {32}
	public void test0083() {
		check(//
				"Integrate[(a + b*x)^(-4/3), x]", //
				"-3/(b*(a + b*x)^(1/3))", //
				32);
	}

	// {37}
	public void test0084() {
		check(//
				"Integrate[Sqrt[a + b*x]/x^(5/2), x]", //
				"(-2*(a + b*x)^(3/2))/(3*a*x^(3/2))", //
				37);
	}

	// {37}
	public void test0085() {
		check(//
				"Integrate[Sqrt[a - b*x]/x^(5/2), x]", //
				"(-2*(a - b*x)^(3/2))/(3*a*x^(3/2))", //
				37);
	}

	// {37}
	public void test0086() {
		check(//
				"Integrate[Sqrt[2 + b*x]/x^(5/2), x]", //
				"-(2 + b*x)^(3/2)/(3*x^(3/2))", //
				37);
	}

	// {37}
	public void test0087() {
		check(//
				"Integrate[Sqrt[2 - b*x]/x^(5/2), x]", //
				"-(2 - b*x)^(3/2)/(3*x^(3/2))", //
				37);
	}

	// {37}
	public void test0088() {
		check(//
				"Integrate[1/(x^(3/2)*Sqrt[a + b*x]), x]", //
				"(-2*Sqrt[a + b*x])/(a*Sqrt[x])", //
				37);
	}

	// {37}
	public void test0089() {
		check(//
				"Integrate[1/(Sqrt[x]*(a + b*x)^(3/2)), x]", //
				"(2*Sqrt[x])/(a*Sqrt[a + b*x])", //
				37);
	}

	// {37}
	public void test0090() {
		check(//
				"Integrate[Sqrt[x]/(a + b*x)^(5/2), x]", //
				"(2*x^(3/2))/(3*a*(a + b*x)^(3/2))", //
				37);
	}

	// {37}
	public void test0091() {
		check(//
				"Integrate[1/(x^(3/2)*Sqrt[a - b*x]), x]", //
				"(-2*Sqrt[a - b*x])/(a*Sqrt[x])", //
				37);
	}

	// {37}
	public void test0092() {
		check(//
				"Integrate[1/(Sqrt[x]*(a - b*x)^(3/2)), x]", //
				"(2*Sqrt[x])/(a*Sqrt[a - b*x])", //
				37);
	}

	// {37}
	public void test0093() {
		check(//
				"Integrate[Sqrt[x]/(a - b*x)^(5/2), x]", //
				"(2*x^(3/2))/(3*a*(a - b*x)^(3/2))", //
				37);
	}

	// {37}
	public void test0094() {
		check(//
				"Integrate[1/(x^(3/2)*Sqrt[2 + b*x]), x]", //
				"-(Sqrt[2 + b*x]/Sqrt[x])", //
				37);
	}

	// {37}
	public void test0095() {
		check(//
				"Integrate[1/(Sqrt[x]*(2 + b*x)^(3/2)), x]", //
				"Sqrt[x]/Sqrt[2 + b*x]", //
				37);
	}

	// {37}
	public void test0096() {
		check(//
				"Integrate[Sqrt[x]/(2 + b*x)^(5/2), x]", //
				"x^(3/2)/(3*(2 + b*x)^(3/2))", //
				37);
	}

	// {37}
	public void test0097() {
		check(//
				"Integrate[1/(x^(3/2)*Sqrt[2 - b*x]), x]", //
				"-(Sqrt[2 - b*x]/Sqrt[x])", //
				37);
	}

	// {37}
	public void test0098() {
		check(//
				"Integrate[1/(Sqrt[x]*(2 - b*x)^(3/2)), x]", //
				"Sqrt[x]/Sqrt[2 - b*x]", //
				37);
	}

	// {37}
	public void test0099() {
		check(//
				"Integrate[Sqrt[x]/(2 - b*x)^(5/2), x]", //
				"x^(3/2)/(3*(2 - b*x)^(3/2))", //
				37);
	}

	// {64}
	public void test0100() {
		check(//
				"Integrate[x^m/(a + b*x), x]", //
				"(x^(1 + m)*Hypergeometric2F1[1, 1 + m, 2 + m, -((b*x)/a)])/(a*(1 + m))", //
				64);
	}

	// {64}
	public void test0101() {
		check(//
				"Integrate[x^m/(a + b*x)^2, x]", //
				"(x^(1 + m)*Hypergeometric2F1[2, 1 + m, 2 + m, -((b*x)/a)])/(a^2*(1 + m))", //
				64);
	}

	// {64}
	public void test0102() {
		check(//
				"Integrate[x^m/(a + b*x)^3, x]", //
				"(x^(1 + m)*Hypergeometric2F1[3, 1 + m, 2 + m, -((b*x)/a)])/(a^3*(1 + m))", //
				64);
	}

	// {64}
	public void test0103() {
		check(//
				"Integrate[x^m/Sqrt[2 + 3*x], x]", //
				"(x^(1 + m)*Hypergeometric2F1[1/2, 1 + m, 2 + m, (-3*x)/2])/(Sqrt[2]*(1 + m))", //
				64);
	}

	// {64}
	public void test0104() {
		check(//
				"Integrate[x^m/Sqrt[2 - 3*x], x]", //
				"(x^(1 + m)*Hypergeometric2F1[1/2, 1 + m, 2 + m, (3*x)/2])/(Sqrt[2]*(1 + m))", //
				64);
	}

	// {65}
	public void test0105() {
		check(//
				"Integrate[x^m/Sqrt[-2 + 3*x], x]", //
				"(3/2)^(-1 - m)*Sqrt[-2 + 3*x]*Hypergeometric2F1[1/2, -m, 3/2, 1 - (3*x)/2]", //
				65);
	}

	// {64}
	public void test0106() {
		check(//
				"Integrate[(-x)^m/Sqrt[2 + 3*x], x]", //
				"-(((-x)^(1 + m)*Hypergeometric2F1[1/2, 1 + m, 2 + m, (-3*x)/2])/(Sqrt[2]*(1 + m)))", //
				64);
	}

	// {64}
	public void test0107() {
		check(//
				"Integrate[(-x)^m/Sqrt[2 - 3*x], x]", //
				"-(((-x)^(1 + m)*Hypergeometric2F1[1/2, 1 + m, 2 + m, (3*x)/2])/(Sqrt[2]*(1 + m)))", //
				64);
	}

	// {65}
	public void test0108() {
		check(//
				"Integrate[(-x)^m/Sqrt[-2 - 3*x], x]", //
				"-((3/2)^(-1 - m)*Sqrt[-2 - 3*x]*Hypergeometric2F1[1/2, -m, 3/2, 1 + (3*x)/2])", //
				65);
	}

	// {65}
	public void test0109() {
		check(//
				"Integrate[x^n/Sqrt[1 - x], x]", //
				"-2*Sqrt[1 - x]*Hypergeometric2F1[1/2, -n, 3/2, 1 - x]", //
				65);
	}

	// {65}
	public void test0110() {
		check(//
				"Integrate[x^n/Sqrt[a - a*x], x]", //
				"(-2*Sqrt[a - a*x]*Hypergeometric2F1[1/2, -n, 3/2, 1 - x])/a", //
				65);
	}

	// {32}
	public void test0111() {
		check(//
				"Integrate[(a + b*x)^n, x]", //
				"(a + b*x)^(1 + n)/(b*(1 + n))", //
				32);
	}

	// {65}
	public void test0112() {
		check(//
				"Integrate[(a + b*x)^n/x, x]", //
				"-(((a + b*x)^(1 + n)*Hypergeometric2F1[1, 1 + n, 2 + n, 1 + (b*x)/a])/(a*(1 + n)))", //
				65);
	}

	// {65}
	public void test0113() {
		check(//
				"Integrate[(a + b*x)^n/x^2, x]", //
				"(b*(a + b*x)^(1 + n)*Hypergeometric2F1[2, 1 + n, 2 + n, 1 + (b*x)/a])/(a^2*(1 + n))", //
				65);
	}

	// {65}
	public void test0114() {
		check(//
				"Integrate[(a + b*x)^n/x^3, x]", //
				"-((b^2*(a + b*x)^(1 + n)*Hypergeometric2F1[3, 1 + n, 2 + n, 1 + (b*x)/a])/(a^3*(1 + n)))", //
				65);
	}

	// {37}
	public void test0115() {
		check(//
				"Integrate[x^(-2 + n)/(a + b*x)^n, x]", //
				"-((x^(-1 + n)*(a + b*x)^(1 - n))/(a*(1 - n)))", //
				37);
	}

	// {64}
	public void test0116() {
		check(//
				"Integrate[(b*x)^m*(2 + d*x)^n, x]", //
				"(2^n*(b*x)^(1 + m)*Hypergeometric2F1[1 + m, -n, 2 + m, -(d*x)/2])/(b*(1 + m))", //
				64);
	}

	// {65}
	public void test0117() {
		check(//
				"Integrate[(b*x)^m*(c - b*c*x)^n, x]", //
				"-(((c - b*c*x)^(1 + n)*Hypergeometric2F1[-m, 1 + n, 2 + n, 1 - b*x])/(b*c*(1 + n)))", //
				65);
	}

	// {37}
	public void test0118() {
		check(//
				"Integrate[x^(-1 + n)*(a + b*x)^(-1 - n), x]", //
				"x^n/(a*n*(a + b*x)^n)", //
				37);
	}

	// {34}
	public void test0120() {
		check(//
				"Integrate[(a + b*x)/(a*c - b*c*x)^3, x]", //
				"x/(c^3*(a - b*x)^2)", //
				34);
	}

	// {32}
	public void test0121() {
		check(//
				"Integrate[(a + b*x)^2, x]", //
				"(a + b*x)^3/(3*b)", //
				32);
	}

	// {37}
	public void test0122() {
		check(//
				"Integrate[(a + b*x)^2/(a*c - b*c*x)^4, x]", //
				"(a + b*x)^3/(6*a*b*c^4*(a - b*x)^3)", //
				37);
	}

	// {31}
	public void test0123() {
		check(//
				"Integrate[(a + b*x)^(-1), x]", //
				"Log[a + b*x]/b", //
				31);
	}

	// {32}
	public void test0124() {
		check(//
				"Integrate[(a + b*x)^(-2), x]", //
				"-(1/(b*(a + b*x)))", //
				32);
	}

	// {37}
	public void test0125() {
		check(//
				"Integrate[Sqrt[1 + x]/(1 - x)^(5/2), x]", //
				"(1 + x)^(3/2)/(3*(1 - x)^(3/2))", //
				37);
	}

	// {37}
	public void test0126() {
		check(//
				"Integrate[(1 + x)^(3/2)/(1 - x)^(7/2), x]", //
				"(1 + x)^(5/2)/(5*(1 - x)^(5/2))", //
				37);
	}

	// {37}
	public void test0127() {
		check(//
				"Integrate[(1 + x)^(5/2)/(1 - x)^(9/2), x]", //
				"(1 + x)^(7/2)/(7*(1 - x)^(7/2))", //
				37);
	}

	// {37}
	public void test0128() {
		check(//
				"Integrate[1/((1 - x)^(3/2)*Sqrt[1 + x]), x]", //
				"Sqrt[1 + x]/Sqrt[1 - x]", //
				37);
	}

	// {37}
	public void test0129() {
		check(//
				"Integrate[1/(Sqrt[1 - x]*(1 + x)^(3/2)), x]", //
				"-(Sqrt[1 - x]/Sqrt[1 + x])", //
				37);
	}

	// {39}
	public void test0130() {
		check(//
				"Integrate[1/((1 - x)^(3/2)*(1 + x)^(3/2)), x]", //
				"x/(Sqrt[1 - x]*Sqrt[1 + x])", //
				39);
	}

	// {37}
	public void test0131() {
		check(//
				"Integrate[Sqrt[1 - x]/(1 + x)^(5/2), x]", //
				"-(1 - x)^(3/2)/(3*(1 + x)^(3/2))", //
				37);
	}

	// {39}
	public void test0132() {
		check(//
				"Integrate[1/((a + a*x)^(3/2)*(c - c*x)^(3/2)), x]", //
				"x/(a*c*Sqrt[a + a*x]*Sqrt[c - c*x])", //
				39);
	}

	// {39}
	public void test0133() {
		check(//
				"Integrate[1/((a + b*x)^(3/2)*(a*c - b*c*x)^(3/2)), x]", //
				"x/(a^2*c*Sqrt[a + b*x]*Sqrt[a*c - b*c*x])", //
				39);
	}

	// {39}
	public void test0134() {
		check(//
				"Integrate[1/((3 - 6*x)^(3/2)*(2 + 4*x)^(3/2)), x]", //
				"x/(6*Sqrt[6]*Sqrt[1 - 2*x]*Sqrt[1 + 2*x])", //
				39);
	}

	// {39}
	public void test0135() {
		check(//
				"Integrate[1/((3 - x)^(3/2)*(3 + x)^(3/2)), x]", //
				"x/(9*Sqrt[3 - x]*Sqrt[3 + x])", //
				39);
	}

	// {39}
	public void test0136() {
		check(//
				"Integrate[1/((3 - b*x)^(3/2)*(3 + b*x)^(3/2)), x]", //
				"x/(9*Sqrt[3 - b*x]*Sqrt[3 + b*x])", //
				39);
	}

	// {39}
	public void test0137() {
		check(//
				"Integrate[1/((6 - 2*x)^(3/2)*(3 + x)^(3/2)), x]", //
				"x/(18*Sqrt[2]*Sqrt[3 - x]*Sqrt[3 + x])", //
				39);
	}

	// {39}
	public void test0138() {
		check(//
				"Integrate[1/((6 - 2*b*x)^(3/2)*(3 + b*x)^(3/2)), x]", //
				"x/(18*Sqrt[2]*Sqrt[3 - b*x]*Sqrt[3 + b*x])", //
				39);
	}

	// {37}
	public void test0139() {
		check(//
				"Integrate[1/((a - I*a*x)^(7/4)*(a + I*a*x)^(1/4)), x]", //
				"(((-2*I)/3)*(a + I*a*x)^(3/4))/(a^2*(a - I*a*x)^(3/4))", //
				37);
	}

	// {37}
	public void test0140() {
		check(//
				"Integrate[1/((a - I*a*x)^(5/4)*(a + I*a*x)^(3/4)), x]", //
				"((-2*I)*(a + I*a*x)^(1/4))/(a^2*(a - I*a*x)^(1/4))", //
				37);
	}

	// {37}
	public void test0141() {
		check(//
				"Integrate[1/((a - I*a*x)^(1/4)*(a + I*a*x)^(7/4)), x]", //
				"(((2*I)/3)*(a - I*a*x)^(3/4))/(a^2*(a + I*a*x)^(3/4))", //
				37);
	}

	// {37}
	public void test0142() {
		check(//
				"Integrate[1/((a - I*a*x)^(3/4)*(a + I*a*x)^(5/4)), x]", //
				"((2*I)*(a - I*a*x)^(1/4))/(a^2*(a + I*a*x)^(1/4))", //
				37);
	}

	// {37}
	public void test0143() {
		check(//
				"Integrate[(a - I*a*x)^(1/4)/(a + I*a*x)^(9/4), x]", //
				"(((2*I)/5)*(a - I*a*x)^(5/4))/(a^2*(a + I*a*x)^(5/4))", //
				37);
	}

	// {68}
	public void test0144() {
		check(//
				"Integrate[(a*c - b*c*x)^n/(a + b*x), x]", //
				"-((a*c - b*c*x)^(1 + n)*Hypergeometric2F1[1, 1 + n, 2 + n, (a - b*x)/(2*a)])/(2*a*b*c*(1 + n))", //
				68);
	}

	// {68}
	public void test0145() {
		check(//
				"Integrate[(a*c - b*c*x)^n/(a + b*x)^2, x]", //
				"-((a*c - b*c*x)^(1 + n)*Hypergeometric2F1[2, 1 + n, 2 + n, (a - b*x)/(2*a)])/(4*a^2*b*c*(1 + n))", //
				68);
	}

	// {37}
	public void test0147() {
		check(//
				"Integrate[(c + d*x)/(a + b*x)^3, x]", //
				"-(c + d*x)^2/(2*(b*c - a*d)*(a + b*x)^2)", //
				37);
	}

	// {32}
	public void test0148() {
		check(//
				"Integrate[(c + d*x)^2, x]", //
				"(c + d*x)^3/(3*d)", //
				32);
	}

	// {37}
	public void test0149() {
		check(//
				"Integrate[(c + d*x)^2/(a + b*x)^4, x]", //
				"-(c + d*x)^3/(3*(b*c - a*d)*(a + b*x)^3)", //
				37);
	}

	// {32}
	public void test0150() {
		check(//
				"Integrate[(c + d*x)^3, x]", //
				"(c + d*x)^4/(4*d)", //
				32);
	}

	// {37}
	public void test0151() {
		check(//
				"Integrate[(c + d*x)^3/(a + b*x)^5, x]", //
				"-(c + d*x)^4/(4*(b*c - a*d)*(a + b*x)^4)", //
				37);
	}

	// {32}
	public void test0152() {
		check(//
				"Integrate[(c + d*x)^7, x]", //
				"(c + d*x)^8/(8*d)", //
				32);
	}

	// {37}
	public void test0153() {
		check(//
				"Integrate[(c + d*x)^7/(a + b*x)^9, x]", //
				"-(c + d*x)^8/(8*(b*c - a*d)*(a + b*x)^8)", //
				37);
	}

	// {32}
	public void test0154() {
		check(//
				"Integrate[(c + d*x)^10, x]", //
				"(c + d*x)^11/(11*d)", //
				32);
	}

	// {37}
	public void test0155() {
		check(//
				"Integrate[(c + d*x)^10/(a + b*x)^12, x]", //
				"-(c + d*x)^11/(11*(b*c - a*d)*(a + b*x)^11)", //
				37);
	}

	// {31}
	public void test0156() {
		check(//
				"Integrate[(c + d*x)^(-1), x]", //
				"Log[c + d*x]/d", //
				31);
	}

	// {32}
	public void test0157() {
		check(//
				"Integrate[(c + d*x)^(-2), x]", //
				"-(1/(d*(c + d*x)))", //
				32);
	}

	// {37}
	public void test0158() {
		check(//
				"Integrate[(a + b*x)/(c + d*x)^3, x]", //
				"(a + b*x)^2/(2*(b*c - a*d)*(c + d*x)^2)", //
				37);
	}

	// {32}
	public void test0159() {
		check(//
				"Integrate[(c + d*x)^(-3), x]", //
				"-1/(2*d*(c + d*x)^2)", //
				32);
	}

	// {37}
	public void test0160() {
		check(//
				"Integrate[(a + b*x)^6/(c + d*x)^8, x]", //
				"(a + b*x)^7/(7*(b*c - a*d)*(c + d*x)^7)", //
				37);
	}

	// {32}
	public void test0161() {
		check(//
				"Integrate[(c + d*x)^(-8), x]", //
				"-1/(7*d*(c + d*x)^7)", //
				32);
	}

	// {32}
	public void test0162() {
		check(//
				"Integrate[Sqrt[c + d*x], x]", //
				"(2*(c + d*x)^(3/2))/(3*d)", //
				32);
	}

	// {32}
	public void test0163() {
		check(//
				"Integrate[(c + d*x)^(3/2), x]", //
				"(2*(c + d*x)^(5/2))/(5*d)", //
				32);
	}

	// {32}
	public void test0164() {
		check(//
				"Integrate[(c + d*x)^(5/2), x]", //
				"(2*(c + d*x)^(7/2))/(7*d)", //
				32);
	}

	// {32}
	public void test0165() {
		check(//
				"Integrate[1/Sqrt[c + d*x], x]", //
				"(2*Sqrt[c + d*x])/d", //
				32);
	}

	// {32}
	public void test0166() {
		check(//
				"Integrate[(c + d*x)^(-3/2), x]", //
				"-2/(d*Sqrt[c + d*x])", //
				32);
	}

	// {32}
	public void test0167() {
		check(//
				"Integrate[(c + d*x)^(-5/2), x]", //
				"-2/(3*d*(c + d*x)^(3/2))", //
				32);
	}

	// {37}
	public void test0168() {
		check(//
				"Integrate[Sqrt[c + d*x]/(a + b*x)^(5/2), x]", //
				"(-2*(c + d*x)^(3/2))/(3*(b*c - a*d)*(a + b*x)^(3/2))", //
				37);
	}

	// {37}
	public void test0169() {
		check(//
				"Integrate[(c + d*x)^(3/2)/(a + b*x)^(7/2), x]", //
				"(-2*(c + d*x)^(5/2))/(5*(b*c - a*d)*(a + b*x)^(5/2))", //
				37);
	}

	// {37}
	public void test0170() {
		check(//
				"Integrate[(c + d*x)^(5/2)/(a + b*x)^(9/2), x]", //
				"(-2*(c + d*x)^(7/2))/(7*(b*c - a*d)*(a + b*x)^(7/2))", //
				37);
	}

	// {37}
	public void test0171() {
		check(//
				"Integrate[1/((a + b*x)^(3/2)*Sqrt[c + d*x]), x]", //
				"(-2*Sqrt[c + d*x])/((b*c - a*d)*Sqrt[a + b*x])", //
				37);
	}

	// {37}
	public void test0172() {
		check(//
				"Integrate[1/(Sqrt[a + b*x]*(c + d*x)^(3/2)), x]", //
				"(2*Sqrt[a + b*x])/((b*c - a*d)*Sqrt[c + d*x])", //
				37);
	}

	// {37}
	public void test0173() {
		check(//
				"Integrate[Sqrt[a + b*x]/(c + d*x)^(5/2), x]", //
				"(2*(a + b*x)^(3/2))/(3*(b*c - a*d)*(c + d*x)^(3/2))", //
				37);
	}

	// {52}
	public void test0174() {
		check(//
				"Integrate[1/(Sqrt[-2 + b*x]*Sqrt[2 + b*x]), x]", //
				"ArcCosh[(b*x)/2]/b", //
				52);
	}

	// {31}
	public void test0175() {
		check(//
				"Integrate[(2 + b*x)^(-1), x]", //
				"Log[2 + b*x]/b", //
				31);
	}

	// {52}
	public void test0176() {
		check(//
				"Integrate[1/(Sqrt[-2 + b*x]*Sqrt[2 + b*x]), x]", //
				"ArcCosh[(b*x)/2]/b", //
				52);
	}

	// {31}
	public void test0177() {
		check(//
				"Integrate[(2 - b*x)^(-1), x]", //
				"-(Log[2 - b*x]/b)", //
				31);
	}

	// {52}
	public void test0178() {
		check(//
				"Integrate[1/(Sqrt[-2 - b*x]*Sqrt[2 - b*x]), x]", //
				"-(ArcCosh[-(b*x)/2]/b)", //
				52);
	}

	// {52}
	public void test0179() {
		check(//
				"Integrate[1/(Sqrt[-4 + b*x]*Sqrt[4 + b*x]), x]", //
				"ArcCosh[(b*x)/4]/b", //
				52);
	}

	// {37}
	public void test0180() {
		check(//
				"Integrate[(c + d*x)^(1/3)/(a + b*x)^(7/3), x]", //
				"(-3*(c + d*x)^(4/3))/(4*(b*c - a*d)*(a + b*x)^(4/3))", //
				37);
	}

	// {59}
	public void test0181() {
		check(//
				"Integrate[1/((a + b*x)^(2/3)*(c + d*x)^(1/3)), x]", //
				"-((Sqrt[3]*ArcTan[1/Sqrt[3] + (2*b^(1/3)*(c + d*x)^(1/3))/(Sqrt[3]*d^(1/3)*(a + b*x)^(1/3))])/(b^(2/3)*d^(1/3))) - Log[a + b*x]/(2*b^(2/3)*d^(1/3)) - (3*Log[-1 + (b^(1/3)*(c + d*x)^(1/3))/(d^(1/3)*(a + b*x)^(1/3))])/(2*b^(2/3)*d^(1/3))", //
				59);
	}

	// {37}
	public void test0182() {
		check(//
				"Integrate[1/((a + b*x)^(5/3)*(c + d*x)^(1/3)), x]", //
				"(-3*(c + d*x)^(2/3))/(2*(b*c - a*d)*(a + b*x)^(2/3))", //
				37);
	}

	// {59}
	public void test0183() {
		check(//
				"Integrate[1/((a + b*x)^(1/3)*(c + d*x)^(2/3)), x]", //
				"-((Sqrt[3]*ArcTan[1/Sqrt[3] + (2*d^(1/3)*(a + b*x)^(1/3))/(Sqrt[3]*b^(1/3)*(c + d*x)^(1/3))])/(b^(1/3)*d^(2/3))) - Log[c + d*x]/(2*b^(1/3)*d^(2/3)) - (3*Log[-1 + (d^(1/3)*(a + b*x)^(1/3))/(b^(1/3)*(c + d*x)^(1/3))])/(2*b^(1/3)*d^(2/3))", //
				59);
	}

	// {37}
	public void test0184() {
		check(//
				"Integrate[1/((a + b*x)^(4/3)*(c + d*x)^(2/3)), x]", //
				"(-3*(c + d*x)^(1/3))/((b*c - a*d)*(a + b*x)^(1/3))", //
				37);
	}

	// {37}
	public void test0185() {
		check(//
				"Integrate[1/((a + b*x)^(2/3)*(c + d*x)^(4/3)), x]", //
				"(3*(a + b*x)^(1/3))/((b*c - a*d)*(c + d*x)^(1/3))", //
				37);
	}

	// {37}
	public void test0186() {
		check(//
				"Integrate[(c + d*x)^(5/4)/(a + b*x)^(13/4), x]", //
				"(-4*(c + d*x)^(9/4))/(9*(b*c - a*d)*(a + b*x)^(9/4))", //
				37);
	}

	// {37}
	public void test0187() {
		check(//
				"Integrate[1/((a + b*x)^(7/4)*(c + d*x)^(1/4)), x]", //
				"(-4*(c + d*x)^(3/4))/(3*(b*c - a*d)*(a + b*x)^(3/4))", //
				37);
	}

	// {37}
	public void test0188() {
		check(//
				"Integrate[1/((a + b*x)^(5/4)*(c + d*x)^(3/4)), x]", //
				"(-4*(c + d*x)^(1/4))/((b*c - a*d)*(a + b*x)^(1/4))", //
				37);
	}

	// {37}
	public void test0189() {
		check(//
				"Integrate[1/((a + b*x)^(3/4)*(c + d*x)^(5/4)), x]", //
				"(4*(a + b*x)^(1/4))/((b*c - a*d)*(c + d*x)^(1/4))", //
				37);
	}

	// {37}
	public void test0190() {
		check(//
				"Integrate[(a + b*x)^(1/6)/(c + d*x)^(13/6), x]", //
				"(6*(a + b*x)^(7/6))/(7*(b*c - a*d)*(c + d*x)^(7/6))", //
				37);
	}

	// {37}
	public void test0191() {
		check(//
				"Integrate[(a + b*x)^(5/6)/(c + d*x)^(17/6), x]", //
				"(6*(a + b*x)^(11/6))/(11*(b*c - a*d)*(c + d*x)^(11/6))", //
				37);
	}

	// {37}
	public void test0192() {
		check(//
				"Integrate[(a + b*x)^(7/6)/(c + d*x)^(19/6), x]", //
				"(6*(a + b*x)^(13/6))/(13*(b*c - a*d)*(c + d*x)^(13/6))", //
				37);
	}

	// {37}
	public void test0193() {
		check(//
				"Integrate[1/((a + b*x)^(1/6)*(c + d*x)^(11/6)), x]", //
				"(6*(a + b*x)^(5/6))/(5*(b*c - a*d)*(c + d*x)^(5/6))", //
				37);
	}

	// {37}
	public void test0194() {
		check(//
				"Integrate[1/((a + b*x)^(5/6)*(c + d*x)^(7/6)), x]", //
				"(6*(a + b*x)^(1/6))/((b*c - a*d)*(c + d*x)^(1/6))", //
				37);
	}

	// {37}
	public void test0195() {
		check(//
				"Integrate[1/((a + b*x)^(7/6)*(c + d*x)^(5/6)), x]", //
				"(-6*(c + d*x)^(1/6))/((b*c - a*d)*(a + b*x)^(1/6))", //
				37);
	}

	// {68}
	public void test0196() {
		check(//
				"Integrate[(a + b*x)^m/(c + d*x), x]", //
				"((a + b*x)^(1 + m)*Hypergeometric2F1[1, 1 + m, 2 + m, -((d*(a + b*x))/(b*c - a*d))])/((b*c - a*d)*(1 + m))", //
				68);
	}

	// {68}
	public void test0197() {
		check(//
				"Integrate[(a + b*x)^m/(c + d*x)^2, x]", //
				"(b*(a + b*x)^(1 + m)*Hypergeometric2F1[2, 1 + m, 2 + m, -((d*(a + b*x))/(b*c - a*d))])/((b*c - a*d)^2*(1 + m))", //
				68);
	}

	// {68}
	public void test0198() {
		check(//
				"Integrate[(a + b*x)^m/(c + d*x)^3, x]", //
				"(b^2*(a + b*x)^(1 + m)*Hypergeometric2F1[3, 1 + m, 2 + m, -((d*(a + b*x))/(b*c - a*d))])/((b*c - a*d)^3*(1 + m))", //
				68);
	}

	// {32}
	public void test0199() {
		check(//
				"Integrate[(c + d*x)^n, x]", //
				"(c + d*x)^(1 + n)/(d*(1 + n))", //
				32);
	}

	// {68}
	public void test0200() {
		check(//
				"Integrate[(c + d*x)^n/(a + b*x), x]", //
				"-(((c + d*x)^(1 + n)*Hypergeometric2F1[1, 1 + n, 2 + n, (b*(c + d*x))/(b*c - a*d)])/((b*c - a*d)*(1 + n)))", //
				68);
	}

	// {68}
	public void test0201() {
		check(//
				"Integrate[(c + d*x)^n/(a + b*x)^2, x]", //
				"(d*(c + d*x)^(1 + n)*Hypergeometric2F1[2, 1 + n, 2 + n, (b*(c + d*x))/(b*c - a*d)])/((b*c - a*d)^2*(1 + n))", //
				68);
	}

	// {68}
	public void test0202() {
		check(//
				"Integrate[(c + d*x)^n/(a + b*x)^3, x]", //
				"-((d^2*(c + d*x)^(1 + n)*Hypergeometric2F1[3, 1 + n, 2 + n, (b*(c + d*x))/(b*c - a*d)])/((b*c - a*d)^3*(1 + n)))", //
				68);
	}

	// {37}
	public void test0203() {
		check(//
				"Integrate[(a + b*x)^(-2 + n)/(c + d*x)^n, x]", //
				"-(((a + b*x)^(-1 + n)*(c + d*x)^(1 - n))/((b*c - a*d)*(1 - n)))", //
				37);
	}

	// {37}
	public void test0204() {
		check(//
				"Integrate[(a + b*x)^(-2 - n)*(c + d*x)^n, x]", //
				"-(((a + b*x)^(-1 - n)*(c + d*x)^(1 + n))/((b*c - a*d)*(1 + n)))", //
				37);
	}

	// {37}
	public void test0205() {
		check(//
				"Integrate[(a + b*x)^n*(c + d*x)^(-2 - n), x]", //
				"((a + b*x)^(1 + n)*(c + d*x)^(-1 - n))/((b*c - a*d)*(1 + n))", //
				37);
	}

	// {69}
	public void test0206() {
		check(//
				"Integrate[(1 - x)^n/Sqrt[1 + x], x]", //
				"2^(1 + n)*Sqrt[1 + x]*Hypergeometric2F1[1/2, -n, 3/2, (1 + x)/2]", //
				69);
	}

	// {69}
	public void test0207() {
		check(//
				"Integrate[(1 + x)^n/Sqrt[1 - x], x]", //
				"-(2^(1 + n)*Sqrt[1 - x]*Hypergeometric2F1[1/2, -n, 3/2, (1 - x)/2])", //
				69);
	}

	// {69}
	public void test0208() {
		check(//
				"Integrate[(1 - x)^n*(1 + x)^(7/3), x]", //
				"(3*2^(-1 + n)*(1 + x)^(10/3)*Hypergeometric2F1[10/3, -n, 13/3, (1 + x)/2])/5", //
				69);
	}

	// {69}
	public void test0209() {
		check(//
				"Integrate[(1 - x)^(7/3)*(1 + x)^n, x]", //
				"(-3*2^(-1 + n)*(1 - x)^(10/3)*Hypergeometric2F1[10/3, -n, 13/3, (1 - x)/2])/5", //
				69);
	}

	// {69}
	public void test0210() {
		check(//
				"Integrate[(2 + 3*x)^m/(1 + 2*x)^m, x]", //
				"(2^(-1 - m)*(1 + 2*x)^(1 - m)*Hypergeometric2F1[1 - m, -m, 2 - m, -3*(1 + 2*x)])/(1 - m)", //
				69);
	}

	// {74}
	public void test0238() {
		check(//
				"Integrate[((a + b*x)*(a*c - b*c*x)^3)/x^3, x]", //
				"-(c^3*(a - b*x)^4)/(2*x^2)", //
				74);
	}

	// {74}
	public void test0239() {
		check(//
				"Integrate[((a + b*x)*(a*c - b*c*x)^5)/x^4, x]", //
				"-(c^5*(a - b*x)^6)/(3*x^3)", //
				74);
	}

	// {74}
	public void test0240() {
		check(//
				"Integrate[x^2*(2 + x)^5*(2 + 3*x), x]", //
				"(x^3*(2 + x)^6)/3", //
				74);
	}

	// {37}
	public void test0241() {
		check(//
				"Integrate[(A + B*x)/(a + b*x)^3, x]", //
				"-(A + B*x)^2/(2*(A*b - a*B)*(a + b*x)^2)", //
				37);
	}

	// {37}
	public void test0242() {
		check(//
				"Integrate[Sqrt[a + b*x]/(c + d*x)^(5/2), x]", //
				"(2*(a + b*x)^(3/2))/(3*(b*c - a*d)*(c + d*x)^(3/2))", //
				37);
	}

	// {37}
	public void test0243() {
		check(//
				"Integrate[1/(Sqrt[a + b*x]*(c + d*x)^(3/2)), x]", //
				"(2*Sqrt[a + b*x])/((b*c - a*d)*Sqrt[c + d*x])", //
				37);
	}

	// {37}
	public void test0244() {
		check(//
				"Integrate[Sqrt[1 + x]/(1 - x)^(5/2), x]", //
				"(1 + x)^(3/2)/(3*(1 - x)^(3/2))", //
				37);
	}

	// {37}
	public void test0245() {
		check(//
				"Integrate[Sqrt[1 + x]/(-1 + x)^(5/2), x]", //
				"-(1 + x)^(3/2)/(3*(-1 + x)^(3/2))", //
				37);
	}

	// {74}
	public void test0246() {
		check(//
				"Integrate[x/(Sqrt[-1 + x]*Sqrt[1 + x]), x]", //
				"Sqrt[-1 + x]*Sqrt[1 + x]", //
				74);
	}

	// {52}
	public void test0247() {
		check(//
				"Integrate[1/(Sqrt[-1 + x]*Sqrt[1 + x]), x]", //
				"ArcCosh[x]", //
				52);
	}

	// {95}
	public void test0248() {
		check(//
				"Integrate[1/(Sqrt[-1 + x]*x^2*Sqrt[1 + x]), x]", //
				"(Sqrt[-1 + x]*Sqrt[1 + x])/x", //
				95);
	}

	// {74}
	public void test0249() {
		check(//
				"Integrate[Sqrt[-1 + x]*x*Sqrt[1 + x], x]", //
				"((-1 + x)^(3/2)*(1 + x)^(3/2))/3", //
				74);
	}

	// {74}
	public void test0250() {
		check(//
				"Integrate[Sqrt[1 - x]*x*Sqrt[1 + x], x]", //
				"-((1 - x)^(3/2)*(1 + x)^(3/2))/3", //
				74);
	}

	// {116}
	public void test0251() {
		check(//
				"Integrate[1/(Sqrt[x]*Sqrt[2 - b*x]*Sqrt[2 + b*x]), x]", //
				"(Sqrt[2]*EllipticF[ArcSin[(Sqrt[b]*Sqrt[x])/Sqrt[2]], -1])/Sqrt[b]", //
				116);
	}

	// {116}
	public void test0252() {
		check(//
				"Integrate[1/(Sqrt[-x]*Sqrt[2 - b*x]*Sqrt[2 + b*x]), x]", //
				"-((Sqrt[2]*EllipticF[ArcSin[(Sqrt[b]*Sqrt[-x])/Sqrt[2]], -1])/Sqrt[b])", //
				116);
	}

	// {116}
	public void test0253() {
		check(//
				"Integrate[1/(Sqrt[e*x]*Sqrt[2 - b*x]*Sqrt[2 + b*x]), x]", //
				"(Sqrt[2]*EllipticF[ArcSin[(Sqrt[b]*Sqrt[e*x])/(Sqrt[2]*Sqrt[e])], -1])/(Sqrt[b]*Sqrt[e])", //
				116);
	}

	// {115}
	public void test0254() {
		check(//
				"Integrate[1/(Sqrt[2 - 3*x]*Sqrt[x]*Sqrt[2 + 3*x]), x]", //
				"Sqrt[2/3]*EllipticF[ArcSin[Sqrt[3/2]*Sqrt[x]], -1]", //
				115);
	}

	// {115}
	public void test0255() {
		check(//
				"Integrate[1/(Sqrt[2 - 3*x]*Sqrt[-x]*Sqrt[2 + 3*x]), x]", //
				"-(Sqrt[2/3]*EllipticF[ArcSin[Sqrt[3/2]*Sqrt[-x]], -1])", //
				115);
	}

	// {116}
	public void test0256() {
		check(//
				"Integrate[1/(Sqrt[2 - 3*x]*Sqrt[e*x]*Sqrt[2 + 3*x]), x]", //
				"(Sqrt[2/3]*EllipticF[ArcSin[(Sqrt[3/2]*Sqrt[e*x])/Sqrt[e]], -1])/Sqrt[e]", //
				116);
	}

	// {115}
	public void test0257() {
		check(//
				"Integrate[1/(Sqrt[1 - x]*Sqrt[x]*Sqrt[1 + x]), x]", //
				"2*EllipticF[ArcSin[Sqrt[x]], -1]", //
				115);
	}

	// {116}
	public void test0258() {
		check(//
				"Integrate[1/(Sqrt[b*x]*Sqrt[1 - c*x]*Sqrt[1 + c*x]), x]", //
				"(2*EllipticF[ArcSin[(Sqrt[c]*Sqrt[b*x])/Sqrt[b]], -1])/(Sqrt[b]*Sqrt[c])", //
				116);
	}

	// {116}
	public void test0259() {
		check(//
				"Integrate[1/(Sqrt[b*x]*Sqrt[1 - c*x]*Sqrt[1 + d*x]), x]", //
				"(2*EllipticF[ArcSin[(Sqrt[c]*Sqrt[b*x])/Sqrt[b]], -(d/c)])/(Sqrt[b]*Sqrt[c])", //
				116);
	}

	// {110}
	public void test0260() {
		check(//
				"Integrate[Sqrt[1 + x]/(Sqrt[1 - x]*Sqrt[x]), x]", //
				"2*EllipticE[ArcSin[Sqrt[x]], -1]", //
				110);
	}

	// {110}
	public void test0261() {
		check(//
				"Integrate[Sqrt[1 + c*x]/(Sqrt[b*x]*Sqrt[1 - c*x]), x]", //
				"(2*EllipticE[ArcSin[(Sqrt[c]*Sqrt[b*x])/Sqrt[b]], -1])/(Sqrt[b]*Sqrt[c])", //
				110);
	}

	// {110}
	public void test0262() {
		check(//
				"Integrate[Sqrt[1 + c*x]/(Sqrt[b*x]*Sqrt[1 - d*x]), x]", //
				"(2*EllipticE[ArcSin[(Sqrt[d]*Sqrt[b*x])/Sqrt[b]], -(c/d)])/(Sqrt[b]*Sqrt[d])", //
				110);
	}

	// {110}
	public void test0263() {
		check(//
				"Integrate[Sqrt[1 - c*x]/(Sqrt[b*x]*Sqrt[1 + c*x]), x]", //
				"(-2*EllipticE[ArcSin[(Sqrt[c]*Sqrt[b*x])/Sqrt[-b]], -1])/(Sqrt[-b]*Sqrt[c])", //
				110);
	}

	// {110}
	public void test0264() {
		check(//
				"Integrate[Sqrt[1 - c*x]/(Sqrt[b*x]*Sqrt[1 + d*x]), x]", //
				"(-2*EllipticE[ArcSin[(Sqrt[d]*Sqrt[b*x])/Sqrt[-b]], -(c/d)])/(Sqrt[-b]*Sqrt[d])", //
				110);
	}

	// {123}
	public void test0265() {
		check(//
				"Integrate[1/((1 - x)^(1/3)*(2 - x)^(1/3)*x), x]", //
				"-(Sqrt[3]*ArcTan[1/Sqrt[3] + (2^(1/3)*(2 - x)^(2/3))/(Sqrt[3]*(1 - x)^(1/3))])/(2*2^(1/3)) + (3*Log[-(1 - x)^(1/3) + (2 - x)^(2/3)/2^(2/3)])/(4*2^(1/3)) - Log[x]/(2*2^(1/3))", //
				123);
	}

	// {95}
	public void test0266() {
		check(//
				"Integrate[1/((1 - x)^(1/4)*(e*x)^(5/2)*(1 + x)^(1/4)), x]", //
				"(-2*(1 - x)^(3/4)*(1 + x)^(3/4))/(3*e*(e*x)^(3/2))", //
				95);
	}

	// {74}
	public void test0267() {
		check(//
				"Integrate[x^(1 + 2*n)*(a + b*x)^n*(2*a + 3*b*x), x]", //
				"(x^(2*(1 + n))*(a + b*x)^(1 + n))/(1 + n)", //
				74);
	}

	// {68}
	public void test0268() {
		check(//
				"Integrate[(a + b*x)^n/(c + d*x), x]", //
				"((a + b*x)^(1 + n)*Hypergeometric2F1[1, 1 + n, 2 + n, -((d*(a + b*x))/(b*c - a*d))])/((b*c - a*d)*(1 + n))", //
				68);
	}

	// {68}
	public void test0269() {
		check(//
				"Integrate[(a + b*x)^n/(c + d*x)^2, x]", //
				"(b*(a + b*x)^(1 + n)*Hypergeometric2F1[2, 1 + n, 2 + n, -((d*(a + b*x))/(b*c - a*d))])/((b*c - a*d)^2*(1 + n))", //
				68);
	}

	// {133}
	public void test0270() {
		check(//
				"Integrate[(b*x)^m*(Pi + d*x)^n*(E + f*x)^p, x]", //
				"(E^p*Pi^n*(b*x)^(1 + m)*AppellF1[1 + m, -n, -p, 2 + m, -((d*x)/Pi), -((f*x)/E)])/(b*(1 + m))", //
				133);
	}

	// {133}
	public void test0271() {
		check(//
				"Integrate[(b*x)^(5/2)*(Pi + d*x)^n*(E + f*x)^p, x]", //
				"(2*E^p*Pi^n*(b*x)^(7/2)*AppellF1[7/2, -n, -p, 9/2, -((d*x)/Pi), -((f*x)/E)])/(7*b)", //
				133);
	}

	// {131}
	public void test0272() {
		check(//
				"Integrate[(a + b*x)^n/(x^2*(c + d*x)^n), x]", //
				"((b*c - a*d)*(a + b*x)^(1 + n)*(c + d*x)^(-1 - n)*Hypergeometric2F1[2, 1 + n, 2 + n, (c*(a + b*x))/(a*(c + d*x))])/(a^2*(1 + n))", //
				131);
	}

	// {69}
	public void test0273() {
		check(//
				"Integrate[(1 - x)^n/(1 + x)^n, x]", //
				"-(((1 - x)^(1 + n)*Hypergeometric2F1[n, 1 + n, 2 + n, (1 - x)/2])/(2^n*(1 + n)))", //
				69);
	}

	// {131}
	public void test0274() {
		check(//
				"Integrate[(1 - x)^n/(x^2*(1 + x)^n), x]", //
				"(-2*(1 - x)^(1 + n)*(1 + x)^(-1 - n)*Hypergeometric2F1[2, 1 + n, 2 + n, (1 - x)/(1 + x)])/(1 + n)", //
				131);
	}

	// {132}
	public void test0275() {
		check(//
				"Integrate[((1 - x)^(-1/2 + p)*(1 + x)^(1/2 + p))/(c*x)^(2*(1 + p)), x]", //
				"-((4^(1 + p)*(1 - x)^(1/2 + p)*(x/(1 + x))^(2*(1 + p))*(1 + x)^(3/2 + p)*Hypergeometric2F1[1/2 + p, 2*(1 + p), 3/2 + p, (1 - x)/(1 + x)])/((1 + 2*p)*(c*x)^(2*(1 + p))))", //
				132);
	}

	// {131}
	public void test0276() {
		check(//
				"Integrate[(1 + x/a)^(n/2)/(x^2*(1 - x/a)^(n/2)), x]", //
				"(-4*(1 - x/a)^(1 - n/2)*(1 + x/a)^((-2 + n)/2)*Hypergeometric2F1[2, 1 - n/2, 2 - n/2, (a - x)/(a + x)])/(a*(2 - n))", //
				131);
	}

	// {81}
	public void test0277() {
		check(//
				"Integrate[x^2/((1 - a*x)^7*(1 + a*x)^4), x]", //
				"-(1 - 3*a*x)/(24*a^3*(1 - a*x)^6*(1 + a*x)^3)", //
				81);
	}

	// {81}
	public void test0278() {
		check(//
				"Integrate[x^2/((1 - a*x)^11*(1 + a*x)^7), x]", //
				"-(1 - 4*a*x)/(60*a^3*(1 - a*x)^10*(1 + a*x)^6)", //
				81);
	}

	// {81}
	public void test0279() {
		check(//
				"Integrate[x^2/((1 - a*x)^16*(1 + a*x)^11), x]", //
				"-(1 - 5*a*x)/(120*a^3*(1 - a*x)^15*(1 + a*x)^10)", //
				81);
	}

	// {81}
	public void test0280() {
		check(//
				"Integrate[x^2*(1 - a*x)^(-1 - (n*(1 + n))/2)*(1 + a*x)^(-1 - ((-1 + n)*n)/2), x]", //
				"((1 + a*x)^(((1 - n)*n)/2)*(1 - a*n*x))/(a^3*n*(1 - n^2)*(1 - a*x)^((n*(1 + n))/2))", //
				81);
	}

	// {37}
	public void test0281() {
		check(//
				"Integrate[(A + B*x)/(a + b*x)^3, x]", //
				"-(A + B*x)^2/(2*(A*b - a*B)*(a + b*x)^2)", //
				37);
	}

	// {74}
	public void test0282() {
		check(//
				"Integrate[(5 - 2*x)^6*(2 + 3*x)^3*(-16 + 33*x), x]", //
				"-((5 - 2*x)^7*(2 + 3*x)^4)/2", //
				74);
	}

	// {37}
	public void test0283() {
		check(//
				"Integrate[(1 - 2*x)/(3 + 5*x)^3, x]", //
				"-(1 - 2*x)^2/(22*(3 + 5*x)^2)", //
				37);
	}

	// {37}
	public void test0284() {
		check(//
				"Integrate[(3 + 5*x)/(1 - 2*x)^3, x]", //
				"(3 + 5*x)^2/(22*(1 - 2*x)^2)", //
				37);
	}

	// {37}
	public void test0285() {
		check(//
				"Integrate[Sqrt[1 - 2*x]/(3 + 5*x)^(5/2), x]", //
				"(-2*(1 - 2*x)^(3/2))/(33*(3 + 5*x)^(3/2))", //
				37);
	}

	// {37}
	public void test0286() {
		check(//
				"Integrate[1/(Sqrt[1 - 2*x]*(3 + 5*x)^(3/2)), x]", //
				"(-2*Sqrt[1 - 2*x])/(11*Sqrt[3 + 5*x])", //
				37);
	}

	// {37}
	public void test0287() {
		check(//
				"Integrate[1/((1 - 2*x)^(3/2)*Sqrt[3 + 5*x]), x]", //
				"(2*Sqrt[3 + 5*x])/(11*Sqrt[1 - 2*x])", //
				37);
	}

	// {37}
	public void test0288() {
		check(//
				"Integrate[Sqrt[3 + 5*x]/(1 - 2*x)^(5/2), x]", //
				"(2*(3 + 5*x)^(3/2))/(33*(1 - 2*x)^(3/2))", //
				37);
	}

	// {119}
	public void test0289() {
		check(//
				"Integrate[1/(Sqrt[a + b*x]*Sqrt[c + (b*(-1 + c)*x)/a]*Sqrt[e + (b*(-1 + e)*x)/a]), x]", //
				"(2*Sqrt[a]*EllipticF[ArcSin[(Sqrt[1 - c]*Sqrt[a + b*x])/Sqrt[a]], (1 - e)/(1 - c)])/(b*Sqrt[1 - c])", //
				119);
	}

	// {113}
	public void test0290() {
		check(//
				"Integrate[Sqrt[e + (b*(-1 + e)*x)/a]/(Sqrt[a + b*x]*Sqrt[c + (b*(-1 + c)*x)/a]), x]", //
				"(2*Sqrt[a]*EllipticE[ArcSin[(Sqrt[1 - c]*Sqrt[a + b*x])/Sqrt[a]], (1 - e)/(1 - c)])/(b*Sqrt[1 - c])", //
				113);
	}

	// {113}
	public void test0291() {
		check(//
				"Integrate[Sqrt[1 - 2*x]/(Sqrt[-3 - 5*x]*Sqrt[2 + 3*x]), x]", //
				"(2*Sqrt[7/5]*EllipticE[ArcSin[Sqrt[5]*Sqrt[2 + 3*x]], 2/35])/3", //
				113);
	}

	// {113}
	public void test0292() {
		check(//
				"Integrate[Sqrt[3 + 5*x]/(Sqrt[1 - 2*x]*Sqrt[2 + 3*x]), x]", //
				"-(Sqrt[11/3]*EllipticE[ArcSin[Sqrt[3/7]*Sqrt[1 - 2*x]], 35/33])", //
				113);
	}

	// {118}
	public void test0293() {
		check(//
				"Integrate[1/(Sqrt[1 + x]*Sqrt[2 + x]*Sqrt[3 + x]), x]", //
				"-2*EllipticF[ArcSin[1/Sqrt[3 + x]], 2]", //
				118);
	}

	// {119}
	public void test0294() {
		check(//
				"Integrate[1/(Sqrt[3 - x]*Sqrt[1 + x]*Sqrt[2 + x]), x]", //
				"2*EllipticF[ArcSin[Sqrt[1 + x]/2], -4]", //
				119);
	}

	// {119}
	public void test0295() {
		check(//
				"Integrate[1/(Sqrt[2 - x]*Sqrt[1 + x]*Sqrt[3 + x]), x]", //
				"Sqrt[2]*EllipticF[ArcSin[Sqrt[1 + x]/Sqrt[3]], -3/2]", //
				119);
	}

	// {119}
	public void test0296() {
		check(//
				"Integrate[1/(Sqrt[2 - x]*Sqrt[3 - x]*Sqrt[1 + x]), x]", //
				"EllipticF[ArcSin[Sqrt[1 + x]/Sqrt[3]], 3/4]", //
				119);
	}

	// {119}
	public void test0297() {
		check(//
				"Integrate[1/(Sqrt[1 - x]*Sqrt[2 + x]*Sqrt[3 + x]), x]", //
				"2*EllipticF[ArcSin[Sqrt[2 + x]/Sqrt[3]], -3]", //
				119);
	}

	// {119}
	public void test0298() {
		check(//
				"Integrate[1/(Sqrt[1 - x]*Sqrt[3 - x]*Sqrt[2 + x]), x]", //
				"(2*EllipticF[ArcSin[Sqrt[2 + x]/Sqrt[3]], 3/5])/Sqrt[5]", //
				119);
	}

	// {119}
	public void test0299() {
		check(//
				"Integrate[1/(Sqrt[1 - x]*Sqrt[2 - x]*Sqrt[3 + x]), x]", //
				"(2*EllipticF[ArcSin[Sqrt[3 + x]/2], 4/5])/Sqrt[5]", //
				119);
	}

	// {118}
	public void test0300() {
		check(//
				"Integrate[1/(Sqrt[1 - x]*Sqrt[2 - x]*Sqrt[3 - x]), x]", //
				"2*EllipticF[ArcSin[1/Sqrt[3 - x]], 2]", //
				118);
	}

	// {118}
	public void test0301() {
		check(//
				"Integrate[1/(Sqrt[-3 + x]*Sqrt[-2 + x]*Sqrt[-1 + x]), x]", //
				"-2*EllipticF[ArcSin[1/Sqrt[-1 + x]], 2]", //
				118);
	}

	// {118}
	public void test0302() {
		check(//
				"Integrate[1/(Sqrt[-3 - x]*Sqrt[-2 - x]*Sqrt[-1 - x]), x]", //
				"2*EllipticF[ArcSin[1/Sqrt[-1 - x]], 2]", //
				118);
	}

	// {113}
	public void test0303() {
		check(//
				"Integrate[Sqrt[2 + 3*x]/(Sqrt[1 - 2*x]*Sqrt[3 + 5*x]), x]", //
				"-(Sqrt[7/5]*EllipticE[ArcSin[Sqrt[5/11]*Sqrt[1 - 2*x]], 33/35])", //
				113);
	}

	// {119}
	public void test0304() {
		check(//
				"Integrate[1/(Sqrt[1 - 2*x]*Sqrt[2 + 3*x]*Sqrt[3 + 5*x]), x]", //
				"(-2*EllipticF[ArcSin[Sqrt[3/7]*Sqrt[1 - 2*x]], 35/33])/Sqrt[33]", //
				119);
	}

	// {119}
	public void test0305() {
		check(//
				"Integrate[1/(Sqrt[4 - x]*Sqrt[5 - x]*Sqrt[-3 + x]), x]", //
				"Sqrt[2]*EllipticF[ArcSin[Sqrt[-3 + x]], 1/2]", //
				119);
	}

	// {119}
	public void test0306() {
		check(//
				"Integrate[1/(Sqrt[6 - x]*Sqrt[-2 + x]*Sqrt[-1 + x]), x]", //
				"2*EllipticF[ArcSin[Sqrt[-2 + x]/2], -4]", //
				119);
	}

	// {59}
	public void test0307() {
		check(//
				"Integrate[1/((a + b*x)^(1/3)*(c + d*x)^(2/3)), x]", //
				"-((Sqrt[3]*ArcTan[1/Sqrt[3] + (2*d^(1/3)*(a + b*x)^(1/3))/(Sqrt[3]*b^(1/3)*(c + d*x)^(1/3))])/(b^(1/3)*d^(2/3))) - Log[c + d*x]/(2*b^(1/3)*d^(2/3)) - (3*Log[-1 + (d^(1/3)*(a + b*x)^(1/3))/(b^(1/3)*(c + d*x)^(1/3))])/(2*b^(1/3)*d^(2/3))", //
				59);
	}

	// {91}
	public void test0308() {
		check(//
				"Integrate[1/((a + b*x)^(1/3)*(c + d*x)^(2/3)*(e + f*x)), x]", //
				"-((Sqrt[3]*ArcTan[1/Sqrt[3] + (2*(d*e - c*f)^(1/3)*(a + b*x)^(1/3))/(Sqrt[3]*(b*e - a*f)^(1/3)*(c + d*x)^(1/3))])/((b*e - a*f)^(1/3)*(d*e - c*f)^(2/3))) + Log[e + f*x]/(2*(b*e - a*f)^(1/3)*(d*e - c*f)^(2/3)) - (3*Log[((d*e - c*f)^(1/3)*(a + b*x)^(1/3))/(b*e - a*f)^(1/3) - (c + d*x)^(1/3)])/(2*(b*e - a*f)^(1/3)*(d*e - c*f)^(2/3))", //
				91);
	}

	// {123}
	public void test0309() {
		check(//
				"Integrate[1/((a + b*x)*(c + d*x)^(1/3)*(b*c + a*d + 2*b*d*x)^(1/3)), x]", //
				"-(Sqrt[3]*ArcTan[1/Sqrt[3] + (2*b^(2/3)*(c + d*x)^(2/3))/(Sqrt[3]*(b*c - a*d)^(1/3)*(b*c + a*d + 2*b*d*x)^(1/3))])/(2*b^(2/3)*(b*c - a*d)^(2/3)) - Log[a + b*x]/(2*b^(2/3)*(b*c - a*d)^(2/3)) + (3*Log[(b^(2/3)*(c + d*x)^(2/3))/(b*c - a*d)^(1/3) - (b*c + a*d + 2*b*d*x)^(1/3)])/(4*b^(2/3)*(b*c - a*d)^(2/3))", //
				123);
	}

	// {74}
	public void test0310() {
		check(//
				"Integrate[(a + b*x)/((c + d*x)^(1/3)*(b*c + a*d + 2*b*d*x)^(4/3)), x]", //
				"(3*(c + d*x)^(2/3))/(2*d^2*(b*c + a*d + 2*b*d*x)^(1/3))", //
				74);
	}

	// {123}
	public void test0311() {
		check(//
				"Integrate[1/((d - 3*e*x)^(1/3)*(d + e*x)*(d + 3*e*x)^(1/3)), x]", //
				"(Sqrt[3]*ArcTan[1/Sqrt[3] - (d - 3*e*x)^(2/3)/(Sqrt[3]*d^(1/3)*(d + 3*e*x)^(1/3))])/(4*d^(2/3)*e) + Log[d + e*x]/(4*d^(2/3)*e) - (3*Log[-(d - 3*e*x)^(2/3)/(2*d^(1/3)) - (d + 3*e*x)^(1/3)])/(8*d^(2/3)*e)", //
				123);
	}

	// {131}
	public void test0312() {
		check(//
				"Integrate[(a + b*x)^m/((c + d*x)^m*(e + f*x)^2), x]", //
				"((b*c - a*d)*(a + b*x)^(1 + m)*(c + d*x)^(-1 - m)*Hypergeometric2F1[2, 1 + m, 2 + m, ((d*e - c*f)*(a + b*x))/((b*e - a*f)*(c + d*x))])/((b*e - a*f)^2*(1 + m))", //
				131);
	}

	// {131}
	public void test0313() {
		check(//
				"Integrate[((a + b*x)^m*(c + d*x)^(-1 - m))/(e + f*x), x]", //
				"((a + b*x)^(1 + m)*(c + d*x)^(-1 - m)*Hypergeometric2F1[1, 1 + m, 2 + m, ((d*e - c*f)*(a + b*x))/((b*e - a*f)*(c + d*x))])/((b*e - a*f)*(1 + m))", //
				131);
	}

	// {37}
	public void test0314() {
		check(//
				"Integrate[(a + b*x)^m*(c + d*x)^(-2 - m), x]", //
				"((a + b*x)^(1 + m)*(c + d*x)^(-1 - m))/((b*c - a*d)*(1 + m))", //
				37);
	}

	// {131}
	public void test0315() {
		check(//
				"Integrate[((a + b*x)^m*(c + d*x)^(1 - m))/(e + f*x)^3, x]", //
				"((b*c - a*d)^2*(a + b*x)^(1 + m)*(c + d*x)^(-1 - m)*Hypergeometric2F1[3, 1 + m, 2 + m, ((d*e - c*f)*(a + b*x))/((b*e - a*f)*(c + d*x))])/((b*e - a*f)^3*(1 + m))", //
				131);
	}

	// {131}
	public void test0316() {
		check(//
				"Integrate[((a + b*x)^m*(c + d*x)^(2 - m))/(e + f*x)^4, x]", //
				"((b*c - a*d)^3*(a + b*x)^(1 + m)*(c + d*x)^(-1 - m)*Hypergeometric2F1[4, 1 + m, 2 + m, ((d*e - c*f)*(a + b*x))/((b*e - a*f)*(c + d*x))])/((b*e - a*f)^4*(1 + m))", //
				131);
	}

	// {132}
	public void test0317() {
		check(//
				"Integrate[(a + b*x)^m*(c + d*x)^(-m - n)*(e + f*x)^(-2 + n), x]", //
				"((a + b*x)^(1 + m)*(c + d*x)^(-m - n)*(((b*e - a*f)*(c + d*x))/((b*c - a*d)*(e + f*x)))^(m + n)*(e + f*x)^(-1 + n)*Hypergeometric2F1[1 + m, m + n, 2 + m, -(((d*e - c*f)*(a + b*x))/((b*c - a*d)*(e + f*x)))])/((b*e - a*f)*(1 + m))", //
				132);
	}

	// {95}
	public void test0318() {
		check(//
				"Integrate[(a + b*x)^m*(c + d*x)^n*((b*c*f + a*d*f + a*d*f*m + b*c*f*n)/(b*d*(2 + m + n)) + f*x)^(-3 - m - n), x]", //
				"(b*d*(2 + m + n)*(a + b*x)^(1 + m)*(c + d*x)^(1 + n)*((f*(a*d*(1 + m) + b*c*(1 + n)))/(b*d*(2 + m + n)) + f*x)^(-2 - m - n))/((b*c - a*d)^2*f*(1 + m)*(1 + n))", //
				95);
	}

	// {95}
	public void test0319() {
		check(//
				"Integrate[(a + b*x)^m*(c + d*x)^(-1 - (d*(b*e - a*f)*(1 + m))/(b*(d*e - c*f)))*(e + f*x)^(-1 + ((b*c - a*d)*f*(1 + m))/(b*(d*e - c*f))), x]", //
				"(b*(a + b*x)^(1 + m)*(e + f*x)^(((b*c - a*d)*f*(1 + m))/(b*(d*e - c*f))))/((b*c - a*d)*(b*e - a*f)*(1 + m)*(c + d*x)^((d*(b*e - a*f)*(1 + m))/(b*(d*e - c*f))))", //
				95);
	}

	// {132}
	public void test0320() {
		check(//
				"Integrate[(a + b*x)^m*(c + d*x)^n*(e + f*x)^(-2 - m - n), x]", //
				"((a + b*x)^(1 + m)*(c + d*x)^n*(e + f*x)^(-1 - m - n)*Hypergeometric2F1[1 + m, -n, 2 + m, -(((d*e - c*f)*(a + b*x))/((b*c - a*d)*(e + f*x)))])/((b*e - a*f)*(1 + m)*(((b*e - a*f)*(c + d*x))/((b*c - a*d)*(e + f*x)))^n)", //
				132);
	}

	// {138}
	public void test0321() {
		check(//
				"Integrate[(3 + 4*x)^n/(Sqrt[1 - x]*Sqrt[1 + x]), x]", //
				"-(Sqrt[2]*7^n*Sqrt[1 - x]*AppellF1[1/2, 1/2, -n, 3/2, (1 - x)/2, (4*(1 - x))/7])", //
				138);
	}

	// {138}
	public void test0322() {
		check(//
				"Integrate[(3 - 4*x)^n/(Sqrt[1 - x]*Sqrt[1 + x]), x]", //
				"Sqrt[2]*7^n*Sqrt[1 + x]*AppellF1[1/2, -n, 1/2, 3/2, (4*(1 + x))/7, (1 + x)/2]", //
				138);
	}

	// {138}
	public void test0323() {
		check(//
				"Integrate[(-3 + 4*x)^n/(Sqrt[1 - x]*Sqrt[1 + x]), x]", //
				"-(Sqrt[2]*Sqrt[1 - x]*AppellF1[1/2, 1/2, -n, 3/2, (1 - x)/2, 4*(1 - x)])", //
				138);
	}

	// {138}
	public void test0324() {
		check(//
				"Integrate[(-3 - 4*x)^n/(Sqrt[1 - x]*Sqrt[1 + x]), x]", //
				"Sqrt[2]*Sqrt[1 + x]*AppellF1[1/2, -n, 1/2, 3/2, 4*(1 + x), (1 + x)/2]", //
				138);
	}

	// {68}
	public void test0325() {
		check(//
				"Integrate[(a + b*x)^m/(e + f*x)^2, x]", //
				"(b*(a + b*x)^(1 + m)*Hypergeometric2F1[2, 1 + m, 2 + m, -((f*(a + b*x))/(b*e - a*f))])/((b*e - a*f)^2*(1 + m))", //
				68);
	}

	// {261}
	public void test0327() {
		check(//
				"Integrate[x*(a + b*x^2)^2, x]", //
				"(a + b*x^2)^3/(6*b)", //
				261);
	}

	// {264}
	public void test0328() {
		check(//
				"Integrate[(a + b*x^2)^2/x^7, x]", //
				"-(a + b*x^2)^3/(6*a*x^6)", //
				264);
	}

	// {261}
	public void test0329() {
		check(//
				"Integrate[x*(a + b*x^2)^3, x]", //
				"(a + b*x^2)^4/(8*b)", //
				261);
	}

	// {264}
	public void test0330() {
		check(//
				"Integrate[(a + b*x^2)^3/x^9, x]", //
				"-(a + b*x^2)^4/(8*a*x^8)", //
				264);
	}

	// {261}
	public void test0331() {
		check(//
				"Integrate[x*(a + b*x^2)^5, x]", //
				"(a + b*x^2)^6/(12*b)", //
				261);
	}

	// {264}
	public void test0332() {
		check(//
				"Integrate[(a + b*x^2)^5/x^13, x]", //
				"-(a + b*x^2)^6/(12*a*x^12)", //
				264);
	}

	// {261}
	public void test0333() {
		check(//
				"Integrate[x*(a + b*x^2)^8, x]", //
				"(a + b*x^2)^9/(18*b)", //
				261);
	}

	// {264}
	public void test0334() {
		check(//
				"Integrate[(a + b*x^2)^8/x^19, x]", //
				"-(a + b*x^2)^9/(18*a*x^18)", //
				264);
	}

	// {260}
	public void test0335() {
		check(//
				"Integrate[x/(a + b*x^2), x]", //
				"Log[a + b*x^2]/(2*b)", //
				260);
	}

	// {205}
	public void test0336() {
		check(//
				"Integrate[(a + b*x^2)^(-1), x]", //
				"ArcTan[(Sqrt[b]*x)/Sqrt[a]]/(Sqrt[a]*Sqrt[b])", //
				205);
	}

	// {261}
	public void test0337() {
		check(//
				"Integrate[x/(a + b*x^2)^2, x]", //
				"-1/(2*b*(a + b*x^2))", //
				261);
	}

	// {264}
	public void test0338() {
		check(//
				"Integrate[x^3/(a + b*x^2)^3, x]", //
				"x^4/(4*a*(a + b*x^2)^2)", //
				264);
	}

	// {261}
	public void test0339() {
		check(//
				"Integrate[x/(a + b*x^2)^3, x]", //
				"-1/(4*b*(a + b*x^2)^2)", //
				261);
	}

	// {264}
	public void test0340() {
		check(//
				"Integrate[x^17/(a + b*x^2)^10, x]", //
				"x^18/(18*a*(a + b*x^2)^9)", //
				264);
	}

	// {261}
	public void test0341() {
		check(//
				"Integrate[x/(a + b*x^2)^10, x]", //
				"-1/(18*b*(a + b*x^2)^9)", //
				261);
	}

	// {260}
	public void test0342() {
		check(//
				"Integrate[x/(a - b*x^2), x]", //
				"-Log[a - b*x^2]/(2*b)", //
				260);
	}

	// {208}
	public void test0343() {
		check(//
				"Integrate[(a - b*x^2)^(-1), x]", //
				"ArcTanh[(Sqrt[b]*x)/Sqrt[a]]/(Sqrt[a]*Sqrt[b])", //
				208);
	}

	// {261}
	public void test0344() {
		check(//
				"Integrate[x/(a - b*x^2)^2, x]", //
				"1/(2*b*(a - b*x^2))", //
				261);
	}

	// {264}
	public void test0345() {
		check(//
				"Integrate[x^3/(a - b*x^2)^3, x]", //
				"x^4/(4*a*(a - b*x^2)^2)", //
				264);
	}

	// {261}
	public void test0346() {
		check(//
				"Integrate[x/(a - b*x^2)^3, x]", //
				"1/(4*b*(a - b*x^2)^2)", //
				261);
	}

	// {261}
	public void test0347() {
		check(//
				"Integrate[x/(a - b*x^2)^5, x]", //
				"1/(8*b*(a - b*x^2)^4)", //
				261);
	}

	// {208}
	public void test0348() {
		check(//
				"Integrate[(-1 + a + a*x^2)^(-1), x]", //
				"-(ArcTanh[(Sqrt[a]*x)/Sqrt[1 - a]]/Sqrt[(1 - a)*a])", //
				208);
	}

	// {208}
	public void test0349() {
		check(//
				"Integrate[(-c - d + (c - d)*x^2)^(-1), x]", //
				"-(ArcTanh[(Sqrt[c - d]*x)/Sqrt[c + d]]/(Sqrt[c - d]*Sqrt[c + d]))", //
				208);
	}

	// {205}
	public void test0350() {
		check(//
				"Integrate[(a + (b - a*c)*x^2)^(-1), x]", //
				"ArcTan[(Sqrt[b - a*c]*x)/Sqrt[a]]/(Sqrt[a]*Sqrt[b - a*c])", //
				205);
	}

	// {208}
	public void test0351() {
		check(//
				"Integrate[(a - (b - a*c)*x^2)^(-1), x]", //
				"ArcTanh[(Sqrt[b - a*c]*x)/Sqrt[a]]/(Sqrt[a]*Sqrt[b - a*c])", //
				208);
	}

	// {208}
	public void test0352() {
		check(//
				"Integrate[(c*(a - d) - (b - c)*x^2)^(-1), x]", //
				"ArcTanh[(Sqrt[b - c]*x)/(Sqrt[c]*Sqrt[a - d])]/(Sqrt[b - c]*Sqrt[c]*Sqrt[a - d])", //
				208);
	}

	// {364}
	public void test0353() {
		check(//
				"Integrate[x^m/(a + b*x^2), x]", //
				"(x^(1 + m)*Hypergeometric2F1[1, (1 + m)/2, (3 + m)/2, -((b*x^2)/a)])/(a*(1 + m))", //
				364);
	}

	// {364}
	public void test0354() {
		check(//
				"Integrate[x^m/(a + b*x^2)^2, x]", //
				"(x^(1 + m)*Hypergeometric2F1[2, (1 + m)/2, (3 + m)/2, -((b*x^2)/a)])/(a^2*(1 + m))", //
				364);
	}

	// {364}
	public void test0355() {
		check(//
				"Integrate[x^m/(a + b*x^2)^3, x]", //
				"(x^(1 + m)*Hypergeometric2F1[3, (1 + m)/2, (3 + m)/2, -((b*x^2)/a)])/(a^3*(1 + m))", //
				364);
	}

	// {364}
	public void test0356() {
		check(//
				"Integrate[(c*x)^(1 + m)/(a + b*x^2), x]", //
				"((c*x)^(2 + m)*Hypergeometric2F1[1, (2 + m)/2, (4 + m)/2, -((b*x^2)/a)])/(a*c*(2 + m))", //
				364);
	}

	// {364}
	public void test0357() {
		check(//
				"Integrate[(c*x)^m/(a + b*x^2), x]", //
				"((c*x)^(1 + m)*Hypergeometric2F1[1, (1 + m)/2, (3 + m)/2, -((b*x^2)/a)])/(a*c*(1 + m))", //
				364);
	}

	// {364}
	public void test0358() {
		check(//
				"Integrate[(c*x)^(-1 + m)/(a + b*x^2), x]", //
				"((c*x)^m*Hypergeometric2F1[1, m/2, (2 + m)/2, -((b*x^2)/a)])/(a*c*m)", //
				364);
	}

	// {364}
	public void test0359() {
		check(//
				"Integrate[(c*x)^(-2 + m)/(a + b*x^2), x]", //
				"-(((c*x)^(-1 + m)*Hypergeometric2F1[1, (-1 + m)/2, (1 + m)/2, -((b*x^2)/a)])/(a*c*(1 - m)))", //
				364);
	}

	// {364}
	public void test0360() {
		check(//
				"Integrate[(c*x)^(-3 + m)/(a + b*x^2), x]", //
				"-(((c*x)^(-2 + m)*Hypergeometric2F1[1, (-2 + m)/2, m/2, -((b*x^2)/a)])/(a*c*(2 - m)))", //
				364);
	}

	// {364}
	public void test0361() {
		check(//
				"Integrate[x^m/(1 + (a*x^2)/b)^2, x]", //
				"(x^(1 + m)*Hypergeometric2F1[2, (1 + m)/2, (3 + m)/2, -((a*x^2)/b)])/(1 + m)", //
				364);
	}

	// {261}
	public void test0362() {
		check(//
				"Integrate[x*Sqrt[a + b*x^2], x]", //
				"(a + b*x^2)^(3/2)/(3*b)", //
				261);
	}

	// {264}
	public void test0363() {
		check(//
				"Integrate[Sqrt[a + b*x^2]/x^4, x]", //
				"-(a + b*x^2)^(3/2)/(3*a*x^3)", //
				264);
	}

	// {261}
	public void test0364() {
		check(//
				"Integrate[x*(a + b*x^2)^(3/2), x]", //
				"(a + b*x^2)^(5/2)/(5*b)", //
				261);
	}

	// {264}
	public void test0365() {
		check(//
				"Integrate[(a + b*x^2)^(3/2)/x^6, x]", //
				"-(a + b*x^2)^(5/2)/(5*a*x^5)", //
				264);
	}

	// {261}
	public void test0366() {
		check(//
				"Integrate[x*(a + b*x^2)^(5/2), x]", //
				"(a + b*x^2)^(7/2)/(7*b)", //
				261);
	}

	// {264}
	public void test0367() {
		check(//
				"Integrate[(a + b*x^2)^(5/2)/x^8, x]", //
				"-(a + b*x^2)^(7/2)/(7*a*x^7)", //
				264);
	}

	// {261}
	public void test0368() {
		check(//
				"Integrate[x*(a + b*x^2)^(9/2), x]", //
				"(a + b*x^2)^(11/2)/(11*b)", //
				261);
	}

	// {264}
	public void test0369() {
		check(//
				"Integrate[(a + b*x^2)^(9/2)/x^12, x]", //
				"-(a + b*x^2)^(11/2)/(11*a*x^11)", //
				264);
	}

	// {261}
	public void test0370() {
		check(//
				"Integrate[x*Sqrt[9 + 4*x^2], x]", //
				"(9 + 4*x^2)^(3/2)/12", //
				261);
	}

	// {264}
	public void test0371() {
		check(//
				"Integrate[Sqrt[9 + 4*x^2]/x^4, x]", //
				"-(9 + 4*x^2)^(3/2)/(27*x^3)", //
				264);
	}

	// {261}
	public void test0372() {
		check(//
				"Integrate[x*Sqrt[9 - 4*x^2], x]", //
				"-(9 - 4*x^2)^(3/2)/12", //
				261);
	}

	// {264}
	public void test0373() {
		check(//
				"Integrate[Sqrt[9 - 4*x^2]/x^4, x]", //
				"-(9 - 4*x^2)^(3/2)/(27*x^3)", //
				264);
	}

	// {261}
	public void test0374() {
		check(//
				"Integrate[x*Sqrt[-9 + 4*x^2], x]", //
				"(-9 + 4*x^2)^(3/2)/12", //
				261);
	}

	// {264}
	public void test0375() {
		check(//
				"Integrate[Sqrt[-9 + 4*x^2]/x^4, x]", //
				"(-9 + 4*x^2)^(3/2)/(27*x^3)", //
				264);
	}

	// {261}
	public void test0376() {
		check(//
				"Integrate[x*Sqrt[-9 - 4*x^2], x]", //
				"-(-9 - 4*x^2)^(3/2)/12", //
				261);
	}

	// {264}
	public void test0377() {
		check(//
				"Integrate[Sqrt[-9 - 4*x^2]/x^4, x]", //
				"(-9 - 4*x^2)^(3/2)/(27*x^3)", //
				264);
	}

	// {261}
	public void test0378() {
		check(//
				"Integrate[x/Sqrt[a + b*x^2], x]", //
				"Sqrt[a + b*x^2]/b", //
				261);
	}

	// {264}
	public void test0379() {
		check(//
				"Integrate[1/(x^2*Sqrt[a + b*x^2]), x]", //
				"-(Sqrt[a + b*x^2]/(a*x))", //
				264);
	}

	// {261}
	public void test0380() {
		check(//
				"Integrate[x/(a + b*x^2)^(3/2), x]", //
				"-(1/(b*Sqrt[a + b*x^2]))", //
				261);
	}

	// {191}
	public void test0381() {
		check(//
				"Integrate[(a + b*x^2)^(-3/2), x]", //
				"x/(a*Sqrt[a + b*x^2])", //
				191);
	}

	// {264}
	public void test0382() {
		check(//
				"Integrate[x^2/(a + b*x^2)^(5/2), x]", //
				"x^3/(3*a*(a + b*x^2)^(3/2))", //
				264);
	}

	// {261}
	public void test0383() {
		check(//
				"Integrate[x/(a + b*x^2)^(5/2), x]", //
				"-1/(3*b*(a + b*x^2)^(3/2))", //
				261);
	}

	// {264}
	public void test0384() {
		check(//
				"Integrate[x^6/(a + b*x^2)^(9/2), x]", //
				"x^7/(7*a*(a + b*x^2)^(7/2))", //
				264);
	}

	// {261}
	public void test0385() {
		check(//
				"Integrate[x/(a + b*x^2)^(9/2), x]", //
				"-1/(7*b*(a + b*x^2)^(7/2))", //
				261);
	}

	// {261}
	public void test0386() {
		check(//
				"Integrate[x/Sqrt[9 + 4*x^2], x]", //
				"Sqrt[9 + 4*x^2]/4", //
				261);
	}

	// {215}
	public void test0387() {
		check(//
				"Integrate[1/Sqrt[9 + 4*x^2], x]", //
				"ArcSinh[(2*x)/3]/2", //
				215);
	}

	// {264}
	public void test0388() {
		check(//
				"Integrate[1/(x^2*Sqrt[9 + 4*x^2]), x]", //
				"-Sqrt[9 + 4*x^2]/(9*x)", //
				264);
	}

	// {261}
	public void test0389() {
		check(//
				"Integrate[x/Sqrt[9 - 4*x^2], x]", //
				"-Sqrt[9 - 4*x^2]/4", //
				261);
	}

	// {216}
	public void test0390() {
		check(//
				"Integrate[1/Sqrt[9 - 4*x^2], x]", //
				"ArcSin[(2*x)/3]/2", //
				216);
	}

	// {264}
	public void test0391() {
		check(//
				"Integrate[1/(x^2*Sqrt[9 - 4*x^2]), x]", //
				"-Sqrt[9 - 4*x^2]/(9*x)", //
				264);
	}

	// {261}
	public void test0392() {
		check(//
				"Integrate[x/Sqrt[-9 + 4*x^2], x]", //
				"Sqrt[-9 + 4*x^2]/4", //
				261);
	}

	// {264}
	public void test0393() {
		check(//
				"Integrate[1/(x^2*Sqrt[-9 + 4*x^2]), x]", //
				"Sqrt[-9 + 4*x^2]/(9*x)", //
				264);
	}

	// {261}
	public void test0394() {
		check(//
				"Integrate[x/Sqrt[-9 - 4*x^2], x]", //
				"-Sqrt[-9 - 4*x^2]/4", //
				261);
	}

	// {264}
	public void test0395() {
		check(//
				"Integrate[1/(x^2*Sqrt[-9 - 4*x^2]), x]", //
				"Sqrt[-9 - 4*x^2]/(9*x)", //
				264);
	}

	// {215}
	public void test0396() {
		check(//
				"Integrate[1/Sqrt[9 + b*x^2], x]", //
				"ArcSinh[(Sqrt[b]*x)/3]/Sqrt[b]", //
				215);
	}

	// {216}
	public void test0397() {
		check(//
				"Integrate[1/Sqrt[9 - b*x^2], x]", //
				"ArcSin[(Sqrt[b]*x)/3]/Sqrt[b]", //
				216);
	}

	// {215}
	public void test0398() {
		check(//
				"Integrate[1/Sqrt[Pi + b*x^2], x]", //
				"ArcSinh[(Sqrt[b]*x)/Sqrt[Pi]]/Sqrt[b]", //
				215);
	}

	// {216}
	public void test0399() {
		check(//
				"Integrate[1/Sqrt[Pi - b*x^2], x]", //
				"ArcSin[(Sqrt[b]*x)/Sqrt[Pi]]/Sqrt[b]", //
				216);
	}

	// {449}
	public void test0400() {
		check(//
				"Integrate[(x^(1 + m)*(a*(2 + m) + b*(3 + m)*x^2))/Sqrt[a + b*x^2], x]", //
				"x^(2 + m)*Sqrt[a + b*x^2]", //
				449);
	}

	// {449}
	public void test0401() {
		check(//
				"Integrate[(x^(-1 + m)*(a*m + b*(-1 + m)*x^2))/(a + b*x^2)^(3/2), x]", //
				"x^m/Sqrt[a + b*x^2]", //
				449);
	}

	// {261}
	public void test0402() {
		check(//
				"Integrate[x*(a + b*x^2)^(1/3), x]", //
				"(3*(a + b*x^2)^(4/3))/(8*b)", //
				261);
	}

	// {261}
	public void test0403() {
		check(//
				"Integrate[x*(a + b*x^2)^(2/3), x]", //
				"(3*(a + b*x^2)^(5/3))/(10*b)", //
				261);
	}

	// {261}
	public void test0404() {
		check(//
				"Integrate[x*(a + b*x^2)^(4/3), x]", //
				"(3*(a + b*x^2)^(7/3))/(14*b)", //
				261);
	}

	// {261}
	public void test0405() {
		check(//
				"Integrate[x*(-1 + x^2)^(7/3), x]", //
				"(3*(-1 + x^2)^(10/3))/20", //
				261);
	}

	// {261}
	public void test0406() {
		check(//
				"Integrate[x/(a + b*x^2)^(1/3), x]", //
				"(3*(a + b*x^2)^(2/3))/(4*b)", //
				261);
	}

	// {261}
	public void test0407() {
		check(//
				"Integrate[x/(a + b*x^2)^(2/3), x]", //
				"(3*(a + b*x^2)^(1/3))/(2*b)", //
				261);
	}

	// {261}
	public void test0408() {
		check(//
				"Integrate[x/(a + b*x^2)^(4/3), x]", //
				"-3/(2*b*(a + b*x^2)^(1/3))", //
				261);
	}

	// {264}
	public void test0409() {
		check(//
				"Integrate[(a + b*x^2)^(1/3)/(c*x)^(11/3), x]", //
				"(-3*(a + b*x^2)^(4/3))/(8*a*c*(c*x)^(8/3))", //
				264);
	}

	// {264}
	public void test0410() {
		check(//
				"Integrate[(a + b*x^2)^(4/3)/(c*x)^(17/3), x]", //
				"(-3*(a + b*x^2)^(7/3))/(14*a*c*(c*x)^(14/3))", //
				264);
	}

	// {264}
	public void test0411() {
		check(//
				"Integrate[1/((c*x)^(5/3)*(a + b*x^2)^(2/3)), x]", //
				"(-3*(a + b*x^2)^(1/3))/(2*a*c*(c*x)^(2/3))", //
				264);
	}

	// {228}
	public void test0412() {
		check(//
				"Integrate[(2 - 3*x^2)^(-1/4), x]", //
				"(2*2^(1/4)*EllipticE[ArcSin[Sqrt[3/2]*x]/2, 2])/Sqrt[3]", //
				228);
	}

	// {231}
	public void test0413() {
		check(//
				"Integrate[(2 + 3*x^2)^(-3/4), x]", //
				"(2^(3/4)*EllipticF[ArcTan[Sqrt[3/2]*x]/2, 2])/Sqrt[3]", //
				231);
	}

	// {232}
	public void test0414() {
		check(//
				"Integrate[(2 - 3*x^2)^(-3/4), x]", //
				"(2^(3/4)*EllipticF[ArcSin[Sqrt[3/2]*x]/2, 2])/Sqrt[3]", //
				232);
	}

	// {264}
	public void test0415() {
		check(//
				"Integrate[(a + b*x^2)^(1/4)/(c*x)^(7/2), x]", //
				"(-2*(a + b*x^2)^(5/4))/(5*a*c*(c*x)^(5/2))", //
				264);
	}

	// {264}
	public void test0416() {
		check(//
				"Integrate[(a - b*x^2)^(1/4)/(c*x)^(7/2), x]", //
				"(-2*(a - b*x^2)^(5/4))/(5*a*c*(c*x)^(5/2))", //
				264);
	}

	// {264}
	public void test0417() {
		check(//
				"Integrate[1/((c*x)^(5/2)*(a + b*x^2)^(1/4)), x]", //
				"(-2*(a + b*x^2)^(3/4))/(3*a*c*(c*x)^(3/2))", //
				264);
	}

	// {264}
	public void test0418() {
		check(//
				"Integrate[1/((c*x)^(5/2)*(a - b*x^2)^(1/4)), x]", //
				"(-2*(a - b*x^2)^(3/4))/(3*a*c*(c*x)^(3/2))", //
				264);
	}

	// {264}
	public void test0419() {
		check(//
				"Integrate[1/((c*x)^(3/2)*(a + b*x^2)^(3/4)), x]", //
				"(-2*(a + b*x^2)^(1/4))/(a*c*Sqrt[c*x])", //
				264);
	}

	// {264}
	public void test0420() {
		check(//
				"Integrate[1/((c*x)^(3/2)*(a - b*x^2)^(3/4)), x]", //
				"(-2*(a - b*x^2)^(1/4))/(a*c*Sqrt[c*x])", //
				264);
	}

	// {264}
	public void test0421() {
		check(//
				"Integrate[1/(Sqrt[c*x]*(a + b*x^2)^(5/4)), x]", //
				"(2*Sqrt[c*x])/(a*c*(a + b*x^2)^(1/4))", //
				264);
	}

	// {261}
	public void test0422() {
		check(//
				"Integrate[x*(a + b*x^2)^p, x]", //
				"(a + b*x^2)^(1 + p)/(2*b*(1 + p))", //
				261);
	}

	// {264}
	public void test0423() {
		check(//
				"Integrate[x^(-3 - 2*p)*(a + b*x^2)^p, x]", //
				"-(a + b*x^2)^(1 + p)/(2*a*(1 + p)*x^(2*(1 + p)))", //
				264);
	}

	// {191}
	public void test0424() {
		check(//
				"Integrate[(a + b*x^2)^(-3/2), x]", //
				"x/(a*Sqrt[a + b*x^2])", //
				191);
	}

	// {393}
	public void test0425() {
		check(//
				"Integrate[1/((a - b*x^2)^(1/3)*(3*a + b*x^2)), x]", //
				"ArcTan[(Sqrt[3]*Sqrt[a])/(Sqrt[b]*x)]/(2*2^(2/3)*Sqrt[3]*a^(5/6)*Sqrt[b]) + ArcTan[(Sqrt[3]*a^(1/6)*(a^(1/3) - 2^(1/3)*(a - b*x^2)^(1/3)))/(Sqrt[b]*x)]/(2*2^(2/3)*Sqrt[3]*a^(5/6)*Sqrt[b]) - ArcTanh[(Sqrt[b]*x)/Sqrt[a]]/(6*2^(2/3)*a^(5/6)*Sqrt[b]) + ArcTanh[(Sqrt[b]*x)/(a^(1/6)*(a^(1/3) + 2^(1/3)*(a - b*x^2)^(1/3)))]/(2*2^(2/3)*a^(5/6)*Sqrt[b])", //
				393);
	}

	// {393}
	public void test0426() {
		check(//
				"Integrate[1/((-3*a - b*x^2)*(-a + b*x^2)^(1/3)), x]", //
				"-ArcTan[(Sqrt[3]*Sqrt[a])/(Sqrt[b]*x)]/(2*2^(2/3)*Sqrt[3]*(-a)^(1/3)*Sqrt[a]*Sqrt[b]) - ArcTan[(Sqrt[3]*Sqrt[a]*((-a)^(1/3) - 2^(1/3)*(-a + b*x^2)^(1/3)))/((-a)^(1/3)*Sqrt[b]*x)]/(2*2^(2/3)*Sqrt[3]*(-a)^(1/3)*Sqrt[a]*Sqrt[b]) + ArcTanh[(Sqrt[b]*x)/Sqrt[a]]/(6*2^(2/3)*(-a)^(1/3)*Sqrt[a]*Sqrt[b]) - ArcTanh[((-a)^(1/3)*Sqrt[b]*x)/(Sqrt[a]*((-a)^(1/3) + 2^(1/3)*(-a + b*x^2)^(1/3)))]/(2*2^(2/3)*(-a)^(1/3)*Sqrt[a]*Sqrt[b])", //
				393);
	}

	// {392}
	public void test0427() {
		check(//
				"Integrate[1/((3*a - b*x^2)*(a + b*x^2)^(1/3)), x]", //
				"-ArcTan[(Sqrt[b]*x)/Sqrt[a]]/(6*2^(2/3)*a^(5/6)*Sqrt[b]) + ArcTan[(Sqrt[b]*x)/(a^(1/6)*(a^(1/3) + 2^(1/3)*(a + b*x^2)^(1/3)))]/(2*2^(2/3)*a^(5/6)*Sqrt[b]) - ArcTanh[(Sqrt[3]*Sqrt[a])/(Sqrt[b]*x)]/(2*2^(2/3)*Sqrt[3]*a^(5/6)*Sqrt[b]) - ArcTanh[(Sqrt[3]*a^(1/6)*(a^(1/3) - 2^(1/3)*(a + b*x^2)^(1/3)))/(Sqrt[b]*x)]/(2*2^(2/3)*Sqrt[3]*a^(5/6)*Sqrt[b])", //
				392);
	}

	// {392}
	public void test0428() {
		check(//
				"Integrate[1/((c - d*x^2)*(c + 3*d*x^2)^(1/3)), x]", //
				"-ArcTan[(Sqrt[3]*Sqrt[d]*x)/Sqrt[c]]/(2*2^(2/3)*Sqrt[3]*c^(5/6)*Sqrt[d]) + (Sqrt[3]*ArcTan[(Sqrt[3]*Sqrt[d]*x)/(c^(1/6)*(c^(1/3) + 2^(1/3)*(c + 3*d*x^2)^(1/3)))])/(2*2^(2/3)*c^(5/6)*Sqrt[d]) - ArcTanh[Sqrt[c]/(Sqrt[d]*x)]/(2*2^(2/3)*c^(5/6)*Sqrt[d]) - ArcTanh[(c^(1/6)*(c^(1/3) - 2^(1/3)*(c + 3*d*x^2)^(1/3)))/(Sqrt[d]*x)]/(2*2^(2/3)*c^(5/6)*Sqrt[d])", //
				392);
	}

	// {393}
	public void test0429() {
		check(//
				"Integrate[1/((a - b*x^2)^(1/3)*(3*a + b*x^2)), x]", //
				"ArcTan[(Sqrt[3]*Sqrt[a])/(Sqrt[b]*x)]/(2*2^(2/3)*Sqrt[3]*a^(5/6)*Sqrt[b]) + ArcTan[(Sqrt[3]*a^(1/6)*(a^(1/3) - 2^(1/3)*(a - b*x^2)^(1/3)))/(Sqrt[b]*x)]/(2*2^(2/3)*Sqrt[3]*a^(5/6)*Sqrt[b]) - ArcTanh[(Sqrt[b]*x)/Sqrt[a]]/(6*2^(2/3)*a^(5/6)*Sqrt[b]) + ArcTanh[(Sqrt[b]*x)/(a^(1/6)*(a^(1/3) + 2^(1/3)*(a - b*x^2)^(1/3)))]/(2*2^(2/3)*a^(5/6)*Sqrt[b])", //
				393);
	}

	// {393}
	public void test0430() {
		check(//
				"Integrate[1/((c - 3*d*x^2)^(1/3)*(c + d*x^2)), x]", //
				"ArcTan[Sqrt[c]/(Sqrt[d]*x)]/(2*2^(2/3)*c^(5/6)*Sqrt[d]) + ArcTan[(c^(1/6)*(c^(1/3) - 2^(1/3)*(c - 3*d*x^2)^(1/3)))/(Sqrt[d]*x)]/(2*2^(2/3)*c^(5/6)*Sqrt[d]) - ArcTanh[(Sqrt[3]*Sqrt[d]*x)/Sqrt[c]]/(2*2^(2/3)*Sqrt[3]*c^(5/6)*Sqrt[d]) + (Sqrt[3]*ArcTanh[(Sqrt[3]*Sqrt[d]*x)/(c^(1/6)*(c^(1/3) + 2^(1/3)*(c - 3*d*x^2)^(1/3)))])/(2*2^(2/3)*c^(5/6)*Sqrt[d])", //
				393);
	}

	// {393}
	public void test0431() {
		check(//
				"Integrate[1/((1 - x^2)^(1/3)*(3 + x^2)), x]", //
				"ArcTan[Sqrt[3]/x]/(2*2^(2/3)*Sqrt[3]) + ArcTan[(Sqrt[3]*(1 - 2^(1/3)*(1 - x^2)^(1/3)))/x]/(2*2^(2/3)*Sqrt[3]) - ArcTanh[x]/(6*2^(2/3)) + ArcTanh[x/(1 + 2^(1/3)*(1 - x^2)^(1/3))]/(2*2^(2/3))", //
				393);
	}

	// {392}
	public void test0432() {
		check(//
				"Integrate[1/((3 - x^2)*(1 + x^2)^(1/3)), x]", //
				"-ArcTan[x]/(6*2^(2/3)) + ArcTan[x/(1 + 2^(1/3)*(1 + x^2)^(1/3))]/(2*2^(2/3)) - ArcTanh[Sqrt[3]/x]/(2*2^(2/3)*Sqrt[3]) - ArcTanh[(Sqrt[3]*(1 - 2^(1/3)*(1 + x^2)^(1/3)))/x]/(2*2^(2/3)*Sqrt[3])", //
				392);
	}

	// {1008}
	public void test0433() {
		check(//
				"Integrate[(3 - x)/((1 - x^2)^(1/3)*(3 + x^2)), x]", //
				"-((Sqrt[3]*ArcTan[1/Sqrt[3] - (2^(2/3)*(1 + x)^(2/3))/(Sqrt[3]*(1 - x)^(1/3))])/2^(2/3)) - Log[3 + x^2]/(2*2^(2/3)) + (3*Log[2^(1/3)*(1 - x)^(1/3) + (1 + x)^(2/3)])/(2*2^(2/3))", //
				1008);
	}

	// {1008}
	public void test0434() {
		check(//
				"Integrate[(3 + x)/((1 - x^2)^(1/3)*(3 + x^2)), x]", //
				"(Sqrt[3]*ArcTan[1/Sqrt[3] - (2^(2/3)*(1 - x)^(2/3))/(Sqrt[3]*(1 + x)^(1/3))])/2^(2/3) + Log[3 + x^2]/(2*2^(2/3)) - (3*Log[(1 - x)^(2/3) + 2^(1/3)*(1 + x)^(1/3)])/(2*2^(2/3))", //
				1008);
	}

	// {394}
	public void test0435() {
		check(//
				"Integrate[1/((a + b*x^2)^(1/3)*((9*a*d)/b + d*x^2)), x]", //
				"(Sqrt[b]*ArcTan[(Sqrt[b]*x)/(3*Sqrt[a])])/(12*a^(5/6)*d) + (Sqrt[b]*ArcTan[(a^(1/3) - (a + b*x^2)^(1/3))^2/(3*a^(1/6)*Sqrt[b]*x)])/(12*a^(5/6)*d) - (Sqrt[b]*ArcTanh[(Sqrt[3]*a^(1/6)*(a^(1/3) - (a + b*x^2)^(1/3)))/(Sqrt[b]*x)])/(4*Sqrt[3]*a^(5/6)*d)", //
				394);
	}

	// {395}
	public void test0436() {
		check(//
				"Integrate[1/((a - b*x^2)^(1/3)*((-9*a*d)/b + d*x^2)), x]", //
				"-(Sqrt[b]*ArcTan[(Sqrt[3]*a^(1/6)*(a^(1/3) - (a - b*x^2)^(1/3)))/(Sqrt[b]*x)])/(4*Sqrt[3]*a^(5/6)*d) - (Sqrt[b]*ArcTanh[(Sqrt[b]*x)/(3*Sqrt[a])])/(12*a^(5/6)*d) + (Sqrt[b]*ArcTanh[(a^(1/3) - (a - b*x^2)^(1/3))^2/(3*a^(1/6)*Sqrt[b]*x)])/(12*a^(5/6)*d)", //
				395);
	}

	// {395}
	public void test0437() {
		check(//
				"Integrate[1/((-a + b*x^2)^(1/3)*((-9*a*d)/b + d*x^2)), x]", //
				"(Sqrt[b]*ArcTan[(Sqrt[3]*a^(1/6)*(a^(1/3) + (-a + b*x^2)^(1/3)))/(Sqrt[b]*x)])/(4*Sqrt[3]*a^(5/6)*d) + (Sqrt[b]*ArcTanh[(Sqrt[b]*x)/(3*Sqrt[a])])/(12*a^(5/6)*d) - (Sqrt[b]*ArcTanh[(a^(1/3) + (-a + b*x^2)^(1/3))^2/(3*a^(1/6)*Sqrt[b]*x)])/(12*a^(5/6)*d)", //
				395);
	}

	// {394}
	public void test0438() {
		check(//
				"Integrate[1/((-a - b*x^2)^(1/3)*((9*a*d)/b + d*x^2)), x]", //
				"-(Sqrt[b]*ArcTan[(Sqrt[b]*x)/(3*Sqrt[a])])/(12*a^(5/6)*d) - (Sqrt[b]*ArcTan[(a^(1/3) + (-a - b*x^2)^(1/3))^2/(3*a^(1/6)*Sqrt[b]*x)])/(12*a^(5/6)*d) + (Sqrt[b]*ArcTanh[(Sqrt[3]*a^(1/6)*(a^(1/3) + (-a - b*x^2)^(1/3)))/(Sqrt[b]*x)])/(4*Sqrt[3]*a^(5/6)*d)", //
				394);
	}

	// {394}
	public void test0439() {
		check(//
				"Integrate[1/((2 + b*x^2)^(1/3)*((18*d)/b + d*x^2)), x]", //
				"(Sqrt[b]*ArcTan[(Sqrt[b]*x)/(3*Sqrt[2])])/(12*2^(5/6)*d) + (Sqrt[b]*ArcTan[(2^(1/3) - (2 + b*x^2)^(1/3))^2/(3*2^(1/6)*Sqrt[b]*x)])/(12*2^(5/6)*d) - (Sqrt[b]*ArcTanh[(2^(1/6)*Sqrt[3]*(2^(1/3) - (2 + b*x^2)^(1/3)))/(Sqrt[b]*x)])/(4*2^(5/6)*Sqrt[3]*d)", //
				394);
	}

	// {395}
	public void test0440() {
		check(//
				"Integrate[1/((-2 + b*x^2)^(1/3)*((-18*d)/b + d*x^2)), x]", //
				"(Sqrt[b]*ArcTan[(2^(1/6)*Sqrt[3]*(2^(1/3) + (-2 + b*x^2)^(1/3)))/(Sqrt[b]*x)])/(4*2^(5/6)*Sqrt[3]*d) + (Sqrt[b]*ArcTanh[(Sqrt[b]*x)/(3*Sqrt[2])])/(12*2^(5/6)*d) - (Sqrt[b]*ArcTanh[(2^(1/3) + (-2 + b*x^2)^(1/3))^2/(3*2^(1/6)*Sqrt[b]*x)])/(12*2^(5/6)*d)", //
				395);
	}

	// {394}
	public void test0441() {
		check(//
				"Integrate[1/((2 + 3*x^2)^(1/3)*(6*d + d*x^2)), x]", //
				"ArcTan[x/Sqrt[6]]/(4*2^(5/6)*Sqrt[3]*d) + ArcTan[(2^(1/3) - (2 + 3*x^2)^(1/3))^2/(3*2^(1/6)*Sqrt[3]*x)]/(4*2^(5/6)*Sqrt[3]*d) - ArcTanh[(2^(1/6)*(2^(1/3) - (2 + 3*x^2)^(1/3)))/x]/(4*2^(5/6)*d)", //
				394);
	}

	// {395}
	public void test0442() {
		check(//
				"Integrate[1/((2 - 3*x^2)^(1/3)*(-6*d + d*x^2)), x]", //
				"-ArcTan[(2^(1/6)*(2^(1/3) - (2 - 3*x^2)^(1/3)))/x]/(4*2^(5/6)*d) - ArcTanh[x/Sqrt[6]]/(4*2^(5/6)*Sqrt[3]*d) + ArcTanh[(2^(1/3) - (2 - 3*x^2)^(1/3))^2/(3*2^(1/6)*Sqrt[3]*x)]/(4*2^(5/6)*Sqrt[3]*d)", //
				395);
	}

	// {395}
	public void test0443() {
		check(//
				"Integrate[1/((-2 + 3*x^2)^(1/3)*(-6*d + d*x^2)), x]", //
				"ArcTan[(2^(1/6)*(2^(1/3) + (-2 + 3*x^2)^(1/3)))/x]/(4*2^(5/6)*d) + ArcTanh[x/Sqrt[6]]/(4*2^(5/6)*Sqrt[3]*d) - ArcTanh[(2^(1/3) + (-2 + 3*x^2)^(1/3))^2/(3*2^(1/6)*Sqrt[3]*x)]/(4*2^(5/6)*Sqrt[3]*d)", //
				395);
	}

	// {394}
	public void test0444() {
		check(//
				"Integrate[1/((-2 - 3*x^2)^(1/3)*(6*d + d*x^2)), x]", //
				"-ArcTan[x/Sqrt[6]]/(4*2^(5/6)*Sqrt[3]*d) - ArcTan[(2^(1/3) + (-2 - 3*x^2)^(1/3))^2/(3*2^(1/6)*Sqrt[3]*x)]/(4*2^(5/6)*Sqrt[3]*d) + ArcTanh[(2^(1/6)*(2^(1/3) + (-2 - 3*x^2)^(1/3)))/x]/(4*2^(5/6)*d)", //
				394);
	}

	// {394}
	public void test0445() {
		check(//
				"Integrate[1/((1 + x^2)^(1/3)*(9 + x^2)), x]", //
				"ArcTan[x/3]/12 + ArcTan[(1 - (1 + x^2)^(1/3))^2/(3*x)]/12 - ArcTanh[(Sqrt[3]*(1 - (1 + x^2)^(1/3)))/x]/(4*Sqrt[3])", //
				394);
	}

	// {394}
	public void test0446() {
		check(//
				"Integrate[1/((1 + b*x^2)^(1/3)*(9 + b*x^2)), x]", //
				"ArcTan[(Sqrt[b]*x)/3]/(12*Sqrt[b]) + ArcTan[(1 - (1 + b*x^2)^(1/3))^2/(3*Sqrt[b]*x)]/(12*Sqrt[b]) - ArcTanh[(Sqrt[3]*(1 - (1 + b*x^2)^(1/3)))/(Sqrt[b]*x)]/(4*Sqrt[3]*Sqrt[b])", //
				394);
	}

	// {395}
	public void test0447() {
		check(//
				"Integrate[1/((1 - x^2)^(1/3)*(9 - x^2)), x]", //
				"ArcTan[(Sqrt[3]*(1 - (1 - x^2)^(1/3)))/x]/(4*Sqrt[3]) + ArcTanh[x/3]/12 - ArcTanh[(1 - (1 - x^2)^(1/3))^2/(3*x)]/12", //
				395);
	}

	// {411}
	public void test0448() {
		check(//
				"Integrate[Sqrt[c + d*x^2]/(a + b*x^2)^(3/2), x]", //
				"(Sqrt[c + d*x^2]*EllipticE[ArcTan[(Sqrt[b]*x)/Sqrt[a]], 1 - (a*d)/(b*c)])/(Sqrt[a]*Sqrt[b]*Sqrt[a + b*x^2]*Sqrt[(a*(c + d*x^2))/(c*(a + b*x^2))])", //
				411);
	}

	// {424}
	public void test0449() {
		check(//
				"Integrate[Sqrt[1 - x^2]/Sqrt[2 - 3*x^2], x]", //
				"EllipticE[ArcSin[Sqrt[3/2]*x], 2/3]/Sqrt[3]", //
				424);
	}

	// {424}
	public void test0450() {
		check(//
				"Integrate[Sqrt[4 - x^2]/Sqrt[2 - 3*x^2], x]", //
				"(2*EllipticE[ArcSin[Sqrt[3/2]*x], 1/6])/Sqrt[3]", //
				424);
	}

	// {424}
	public void test0451() {
		check(//
				"Integrate[Sqrt[1 - 4*x^2]/Sqrt[2 - 3*x^2], x]", //
				"EllipticE[ArcSin[Sqrt[3/2]*x], 8/3]/Sqrt[3]", //
				424);
	}

	// {424}
	public void test0452() {
		check(//
				"Integrate[Sqrt[1 + x^2]/Sqrt[1 - x^2], x]", //
				"EllipticE[ArcSin[x], -1]", //
				424);
	}

	// {424}
	public void test0453() {
		check(//
				"Integrate[Sqrt[1 + x^2]/Sqrt[2 - 3*x^2], x]", //
				"EllipticE[ArcSin[Sqrt[3/2]*x], -2/3]/Sqrt[3]", //
				424);
	}

	// {424}
	public void test0454() {
		check(//
				"Integrate[Sqrt[4 + x^2]/Sqrt[2 - 3*x^2], x]", //
				"(2*EllipticE[ArcSin[Sqrt[3/2]*x], -1/6])/Sqrt[3]", //
				424);
	}

	// {424}
	public void test0455() {
		check(//
				"Integrate[Sqrt[1 + 4*x^2]/Sqrt[2 - 3*x^2], x]", //
				"EllipticE[ArcSin[Sqrt[3/2]*x], -8/3]/Sqrt[3]", //
				424);
	}

	// {418}
	public void test0456() {
		check(//
				"Integrate[1/(Sqrt[a + b*x^2]*Sqrt[c + d*x^2]), x]", //
				"(Sqrt[c]*Sqrt[a + b*x^2]*EllipticF[ArcTan[(Sqrt[d]*x)/Sqrt[c]], 1 - (b*c)/(a*d)])/(a*Sqrt[d]*Sqrt[(c*(a + b*x^2))/(a*(c + d*x^2))]*Sqrt[c + d*x^2])", //
				418);
	}

	// {411}
	public void test0457() {
		check(//
				"Integrate[Sqrt[a + b*x^2]/(c + d*x^2)^(3/2), x]", //
				"(Sqrt[a + b*x^2]*EllipticE[ArcTan[(Sqrt[d]*x)/Sqrt[c]], 1 - (b*c)/(a*d)])/(Sqrt[c]*Sqrt[d]*Sqrt[(c*(a + b*x^2))/(a*(c + d*x^2))]*Sqrt[c + d*x^2])", //
				411);
	}

	// {418}
	public void test0458() {
		check(//
				"Integrate[1/(Sqrt[a + b*x^2]*Sqrt[c + d*x^2]), x]", //
				"(Sqrt[c]*Sqrt[a + b*x^2]*EllipticF[ArcTan[(Sqrt[d]*x)/Sqrt[c]], 1 - (b*c)/(a*d)])/(a*Sqrt[d]*Sqrt[(c*(a + b*x^2))/(a*(c + d*x^2))]*Sqrt[c + d*x^2])", //
				418);
	}

	// {419}
	public void test0459() {
		check(//
				"Integrate[1/(Sqrt[1 - x^2]*Sqrt[2 + 5*x^2]), x]", //
				"EllipticF[ArcSin[x], -5/2]/Sqrt[2]", //
				419);
	}

	// {419}
	public void test0460() {
		check(//
				"Integrate[1/(Sqrt[1 - x^2]*Sqrt[2 + 4*x^2]), x]", //
				"EllipticF[ArcSin[x], -2]/Sqrt[2]", //
				419);
	}

	// {419}
	public void test0461() {
		check(//
				"Integrate[1/(Sqrt[1 - x^2]*Sqrt[2 + 3*x^2]), x]", //
				"EllipticF[ArcSin[x], -3/2]/Sqrt[2]", //
				419);
	}

	// {419}
	public void test0462() {
		check(//
				"Integrate[1/(Sqrt[1 - x^2]*Sqrt[2 + x^2]), x]", //
				"EllipticF[ArcSin[x], -1/2]/Sqrt[2]", //
				419);
	}

	// {419}
	public void test0463() {
		check(//
				"Integrate[1/(Sqrt[1 - x^2]*Sqrt[2 - x^2]), x]", //
				"EllipticF[ArcSin[x], 1/2]/Sqrt[2]", //
				419);
	}

	// {419}
	public void test0464() {
		check(//
				"Integrate[1/(Sqrt[2 - 3*x^2]*Sqrt[1 - x^2]), x]", //
				"EllipticF[ArcSin[x], 3/2]/Sqrt[2]", //
				419);
	}

	// {419}
	public void test0465() {
		check(//
				"Integrate[1/(Sqrt[2 - 4*x^2]*Sqrt[1 - x^2]), x]", //
				"EllipticF[ArcSin[x], 2]/Sqrt[2]", //
				419);
	}

	// {419}
	public void test0466() {
		check(//
				"Integrate[1/(Sqrt[2 - 5*x^2]*Sqrt[1 - x^2]), x]", //
				"EllipticF[ArcSin[x], 5/2]/Sqrt[2]", //
				419);
	}

	// {418}
	public void test0467() {
		check(//
				"Integrate[1/(Sqrt[1 + x^2]*Sqrt[2 + 5*x^2]), x]", //
				"(Sqrt[2 + 5*x^2]*EllipticF[ArcTan[x], -3/2])/(Sqrt[2]*Sqrt[1 + x^2]*Sqrt[(2 + 5*x^2)/(1 + x^2)])", //
				418);
	}

	// {418}
	public void test0468() {
		check(//
				"Integrate[1/(Sqrt[1 + x^2]*Sqrt[2 + 4*x^2]), x]", //
				"(Sqrt[1 + 2*x^2]*EllipticF[ArcTan[x], -1])/(Sqrt[2]*Sqrt[1 + x^2]*Sqrt[(1 + 2*x^2)/(1 + x^2)])", //
				418);
	}

	// {418}
	public void test0469() {
		check(//
				"Integrate[1/(Sqrt[1 + x^2]*Sqrt[2 + 3*x^2]), x]", //
				"(Sqrt[2 + 3*x^2]*EllipticF[ArcTan[x], -1/2])/(Sqrt[2]*Sqrt[1 + x^2]*Sqrt[(2 + 3*x^2)/(1 + x^2)])", //
				418);
	}

	// {418}
	public void test0470() {
		check(//
				"Integrate[1/(Sqrt[1 + x^2]*Sqrt[2 + x^2]), x]", //
				"(Sqrt[2 + x^2]*EllipticF[ArcTan[x], 1/2])/(Sqrt[2]*Sqrt[1 + x^2]*Sqrt[(2 + x^2)/(1 + x^2)])", //
				418);
	}

	// {419}
	public void test0471() {
		check(//
				"Integrate[1/(Sqrt[2 - x^2]*Sqrt[1 + x^2]), x]", //
				"EllipticF[ArcSin[x/Sqrt[2]], -2]", //
				419);
	}

	// {419}
	public void test0472() {
		check(//
				"Integrate[1/(Sqrt[2 - 3*x^2]*Sqrt[1 + x^2]), x]", //
				"EllipticF[ArcSin[Sqrt[3/2]*x], -2/3]/Sqrt[3]", //
				419);
	}

	// {419}
	public void test0473() {
		check(//
				"Integrate[1/(Sqrt[2 - 4*x^2]*Sqrt[1 + x^2]), x]", //
				"EllipticF[ArcSin[Sqrt[2]*x], -1/2]/2", //
				419);
	}

	// {419}
	public void test0474() {
		check(//
				"Integrate[1/(Sqrt[2 - 5*x^2]*Sqrt[1 + x^2]), x]", //
				"EllipticF[ArcSin[Sqrt[5/2]*x], -2/5]/Sqrt[5]", //
				419);
	}

	// {420}
	public void test0475() {
		check(//
				"Integrate[1/(Sqrt[2 - x^2]*Sqrt[-1 + x^2]), x]", //
				"-EllipticF[ArcCos[x/Sqrt[2]], 2]", //
				420);
	}

	// {418}
	public void test0476() {
		check(//
				"Integrate[1/(Sqrt[-1 - x^2]*Sqrt[2 + 5*x^2]), x]", //
				"(Sqrt[2 + 5*x^2]*EllipticF[ArcTan[x], -3/2])/(Sqrt[2]*Sqrt[-1 - x^2]*Sqrt[(2 + 5*x^2)/(1 + x^2)])", //
				418);
	}

	// {418}
	public void test0477() {
		check(//
				"Integrate[1/(Sqrt[-1 - x^2]*Sqrt[2 + 4*x^2]), x]", //
				"(Sqrt[1 + 2*x^2]*EllipticF[ArcTan[x], -1])/(Sqrt[2]*Sqrt[-1 - x^2]*Sqrt[(1 + 2*x^2)/(1 + x^2)])", //
				418);
	}

	// {418}
	public void test0478() {
		check(//
				"Integrate[1/(Sqrt[-1 - x^2]*Sqrt[2 + 3*x^2]), x]", //
				"(Sqrt[2 + 3*x^2]*EllipticF[ArcTan[x], -1/2])/(Sqrt[2]*Sqrt[-1 - x^2]*Sqrt[(2 + 3*x^2)/(1 + x^2)])", //
				418);
	}

	// {418}
	public void test0479() {
		check(//
				"Integrate[1/(Sqrt[-1 - x^2]*Sqrt[2 + x^2]), x]", //
				"(Sqrt[2 + x^2]*EllipticF[ArcTan[x], 1/2])/(Sqrt[2]*Sqrt[-1 - x^2]*Sqrt[(2 + x^2)/(1 + x^2)])", //
				418);
	}

	// {418}
	public void test0480() {
		check(//
				"Integrate[1/(Sqrt[2 + b*x^2]*Sqrt[3 + d*x^2]), x]", //
				"(Sqrt[2 + b*x^2]*EllipticF[ArcTan[(Sqrt[d]*x)/Sqrt[3]], 1 - (3*b)/(2*d)])/(Sqrt[2]*Sqrt[d]*Sqrt[(2 + b*x^2)/(3 + d*x^2)]*Sqrt[3 + d*x^2])", //
				418);
	}

	// {418}
	public void test0481() {
		check(//
				"Integrate[1/(Sqrt[4 + x^2]*Sqrt[c + d*x^2]), x]", //
				"(Sqrt[c + d*x^2]*EllipticF[ArcTan[x/2], 1 - (4*d)/c])/(c*Sqrt[4 + x^2]*Sqrt[(c + d*x^2)/(c*(4 + x^2))])", //
				418);
	}

	// {420}
	public void test0482() {
		check(//
				"Integrate[1/(Sqrt[1 - x^2]*Sqrt[-1 + 2*x^2]), x]", //
				"-EllipticF[ArcCos[x], 2]", //
				420);
	}

	// {425}
	public void test0483() {
		check(//
				"Integrate[Sqrt[-1 + 3*x^2]/Sqrt[2 - 3*x^2], x]", //
				"-(EllipticE[ArcCos[Sqrt[3/2]*x], 2]/Sqrt[3])", //
				425);
	}

	// {424}
	public void test0484() {
		check(//
				"Integrate[Sqrt[1 + (2*c*x^2)/(b - Sqrt[b^2 - 4*a*c])]/Sqrt[1 - (2*c*x^2)/(b + Sqrt[b^2 - 4*a*c])], x]", //
				"(Sqrt[b + Sqrt[b^2 - 4*a*c]]*EllipticE[ArcSin[(Sqrt[2]*Sqrt[c]*x)/Sqrt[b + Sqrt[b^2 - 4*a*c]]], -((b + Sqrt[b^2 - 4*a*c])/(b - Sqrt[b^2 - 4*a*c]))])/(Sqrt[2]*Sqrt[c])", //
				424);
	}

	// {424}
	public void test0485() {
		check(//
				"Integrate[Sqrt[1 - (2*c*x^2)/(b - Sqrt[b^2 - 4*a*c])]/Sqrt[1 - (2*c*x^2)/(b + Sqrt[b^2 - 4*a*c])], x]", //
				"(Sqrt[b + Sqrt[b^2 - 4*a*c]]*EllipticE[ArcSin[(Sqrt[2]*Sqrt[c]*x)/Sqrt[b + Sqrt[b^2 - 4*a*c]]], (b + Sqrt[b^2 - 4*a*c])/(b - Sqrt[b^2 - 4*a*c])])/(Sqrt[2]*Sqrt[c])", //
				424);
	}

	// {420}
	public void test0486() {
		check(//
				"Integrate[1/(Sqrt[3 - 3*Sqrt[3] + 2*Sqrt[3]*x^2]*Sqrt[3 + (-3 + Sqrt[3])*x^2]), x]", //
				"-(Sqrt[3 + Sqrt[3]]*EllipticF[ArcCos[Sqrt[(3 - Sqrt[3])/3]*x], (1 + Sqrt[3])/2])/6", //
				420);
	}

	// {397}
	public void test0487() {
		check(//
				"Integrate[1/((2 + 3*x^2)^(1/4)*(4 + 3*x^2)), x]", //
				"-ArcTan[(2*2^(3/4) + 2*2^(1/4)*Sqrt[2 + 3*x^2])/(2*Sqrt[3]*x*(2 + 3*x^2)^(1/4))]/(2*2^(3/4)*Sqrt[3]) - ArcTanh[(2*2^(3/4) - 2*2^(1/4)*Sqrt[2 + 3*x^2])/(2*Sqrt[3]*x*(2 + 3*x^2)^(1/4))]/(2*2^(3/4)*Sqrt[3])", //
				397);
	}

	// {397}
	public void test0488() {
		check(//
				"Integrate[1/((2 - 3*x^2)^(1/4)*(4 - 3*x^2)), x]", //
				"ArcTan[(2 - Sqrt[2]*Sqrt[2 - 3*x^2])/(2^(1/4)*Sqrt[3]*x*(2 - 3*x^2)^(1/4))]/(2*2^(3/4)*Sqrt[3]) + ArcTanh[(2 + Sqrt[2]*Sqrt[2 - 3*x^2])/(2^(1/4)*Sqrt[3]*x*(2 - 3*x^2)^(1/4))]/(2*2^(3/4)*Sqrt[3])", //
				397);
	}

	// {397}
	public void test0489() {
		check(//
				"Integrate[1/((2 + b*x^2)^(1/4)*(4 + b*x^2)), x]", //
				"-ArcTan[(2*2^(3/4) + 2*2^(1/4)*Sqrt[2 + b*x^2])/(2*Sqrt[b]*x*(2 + b*x^2)^(1/4))]/(2*2^(3/4)*Sqrt[b]) - ArcTanh[(2*2^(3/4) - 2*2^(1/4)*Sqrt[2 + b*x^2])/(2*Sqrt[b]*x*(2 + b*x^2)^(1/4))]/(2*2^(3/4)*Sqrt[b])", //
				397);
	}

	// {397}
	public void test0490() {
		check(//
				"Integrate[1/((2 - b*x^2)^(1/4)*(4 - b*x^2)), x]", //
				"ArcTan[(2 - Sqrt[2]*Sqrt[2 - b*x^2])/(2^(1/4)*Sqrt[b]*x*(2 - b*x^2)^(1/4))]/(2*2^(3/4)*Sqrt[b]) + ArcTanh[(2 + Sqrt[2]*Sqrt[2 - b*x^2])/(2^(1/4)*Sqrt[b]*x*(2 - b*x^2)^(1/4))]/(2*2^(3/4)*Sqrt[b])", //
				397);
	}

	// {397}
	public void test0491() {
		check(//
				"Integrate[1/((a + 3*x^2)^(1/4)*(2*a + 3*x^2)), x]", //
				"-ArcTan[(a^(3/4)*(1 + Sqrt[a + 3*x^2]/Sqrt[a]))/(Sqrt[3]*x*(a + 3*x^2)^(1/4))]/(2*Sqrt[3]*a^(3/4)) - ArcTanh[(a^(3/4)*(1 - Sqrt[a + 3*x^2]/Sqrt[a]))/(Sqrt[3]*x*(a + 3*x^2)^(1/4))]/(2*Sqrt[3]*a^(3/4))", //
				397);
	}

	// {397}
	public void test0492() {
		check(//
				"Integrate[1/((a - 3*x^2)^(1/4)*(2*a - 3*x^2)), x]", //
				"ArcTan[(a^(3/4)*(1 - Sqrt[a - 3*x^2]/Sqrt[a]))/(Sqrt[3]*x*(a - 3*x^2)^(1/4))]/(2*Sqrt[3]*a^(3/4)) + ArcTanh[(a^(3/4)*(1 + Sqrt[a - 3*x^2]/Sqrt[a]))/(Sqrt[3]*x*(a - 3*x^2)^(1/4))]/(2*Sqrt[3]*a^(3/4))", //
				397);
	}

	// {397}
	public void test0493() {
		check(//
				"Integrate[1/((a + b*x^2)^(1/4)*(2*a + b*x^2)), x]", //
				"-ArcTan[(a^(3/4)*(1 + Sqrt[a + b*x^2]/Sqrt[a]))/(Sqrt[b]*x*(a + b*x^2)^(1/4))]/(2*a^(3/4)*Sqrt[b]) - ArcTanh[(a^(3/4)*(1 - Sqrt[a + b*x^2]/Sqrt[a]))/(Sqrt[b]*x*(a + b*x^2)^(1/4))]/(2*a^(3/4)*Sqrt[b])", //
				397);
	}

	// {397}
	public void test0494() {
		check(//
				"Integrate[1/((a - b*x^2)^(1/4)*(2*a - b*x^2)), x]", //
				"ArcTan[(a^(3/4)*(1 - Sqrt[a - b*x^2]/Sqrt[a]))/(Sqrt[b]*x*(a - b*x^2)^(1/4))]/(2*a^(3/4)*Sqrt[b]) + ArcTanh[(a^(3/4)*(1 + Sqrt[a - b*x^2]/Sqrt[a]))/(Sqrt[b]*x*(a - b*x^2)^(1/4))]/(2*a^(3/4)*Sqrt[b])", //
				397);
	}

	// {398}
	public void test0495() {
		check(//
				"Integrate[1/((-2 + 3*x^2)*(-1 + 3*x^2)^(1/4)), x]", //
				"-ArcTan[(Sqrt[3/2]*x)/(-1 + 3*x^2)^(1/4)]/(2*Sqrt[6]) - ArcTanh[(Sqrt[3/2]*x)/(-1 + 3*x^2)^(1/4)]/(2*Sqrt[6])", //
				398);
	}

	// {398}
	public void test0496() {
		check(//
				"Integrate[1/((-2 - 3*x^2)*(-1 - 3*x^2)^(1/4)), x]", //
				"-ArcTan[(Sqrt[3/2]*x)/(-1 - 3*x^2)^(1/4)]/(2*Sqrt[6]) - ArcTanh[(Sqrt[3/2]*x)/(-1 - 3*x^2)^(1/4)]/(2*Sqrt[6])", //
				398);
	}

	// {398}
	public void test0497() {
		check(//
				"Integrate[1/((-2 + b*x^2)*(-1 + b*x^2)^(1/4)), x]", //
				"-ArcTan[(Sqrt[b]*x)/(Sqrt[2]*(-1 + b*x^2)^(1/4))]/(2*Sqrt[2]*Sqrt[b]) - ArcTanh[(Sqrt[b]*x)/(Sqrt[2]*(-1 + b*x^2)^(1/4))]/(2*Sqrt[2]*Sqrt[b])", //
				398);
	}

	// {398}
	public void test0498() {
		check(//
				"Integrate[1/((-2 - b*x^2)*(-1 - b*x^2)^(1/4)), x]", //
				"-ArcTan[(Sqrt[b]*x)/(Sqrt[2]*(-1 - b*x^2)^(1/4))]/(2*Sqrt[2]*Sqrt[b]) - ArcTanh[(Sqrt[b]*x)/(Sqrt[2]*(-1 - b*x^2)^(1/4))]/(2*Sqrt[2]*Sqrt[b])", //
				398);
	}

	// {398}
	public void test0499() {
		check(//
				"Integrate[1/((-2*a + 3*x^2)*(-a + 3*x^2)^(1/4)), x]", //
				"-ArcTan[(Sqrt[3/2]*x)/(a^(1/4)*(-a + 3*x^2)^(1/4))]/(2*Sqrt[6]*a^(3/4)) - ArcTanh[(Sqrt[3/2]*x)/(a^(1/4)*(-a + 3*x^2)^(1/4))]/(2*Sqrt[6]*a^(3/4))", //
				398);
	}

	// {398}
	public void test0500() {
		check(//
				"Integrate[1/((-2*a - 3*x^2)*(-a - 3*x^2)^(1/4)), x]", //
				"-ArcTan[(Sqrt[3/2]*x)/(a^(1/4)*(-a - 3*x^2)^(1/4))]/(2*Sqrt[6]*a^(3/4)) - ArcTanh[(Sqrt[3/2]*x)/(a^(1/4)*(-a - 3*x^2)^(1/4))]/(2*Sqrt[6]*a^(3/4))", //
				398);
	}

	// {398}
	public void test0501() {
		check(//
				"Integrate[1/((-2*a + b*x^2)*(-a + b*x^2)^(1/4)), x]", //
				"-ArcTan[(Sqrt[b]*x)/(Sqrt[2]*a^(1/4)*(-a + b*x^2)^(1/4))]/(2*Sqrt[2]*a^(3/4)*Sqrt[b]) - ArcTanh[(Sqrt[b]*x)/(Sqrt[2]*a^(1/4)*(-a + b*x^2)^(1/4))]/(2*Sqrt[2]*a^(3/4)*Sqrt[b])", //
				398);
	}

	// {398}
	public void test0502() {
		check(//
				"Integrate[1/((-2*a - b*x^2)*(-a - b*x^2)^(1/4)), x]", //
				"-ArcTan[(Sqrt[b]*x)/(Sqrt[2]*a^(1/4)*(-a - b*x^2)^(1/4))]/(2*Sqrt[2]*a^(3/4)*Sqrt[b]) - ArcTanh[(Sqrt[b]*x)/(Sqrt[2]*a^(1/4)*(-a - b*x^2)^(1/4))]/(2*Sqrt[2]*a^(3/4)*Sqrt[b])", //
				398);
	}

	// {398}
	public void test0503() {
		check(//
				"Integrate[1/((2 - x^2)*(-1 + x^2)^(1/4)), x]", //
				"ArcTan[x/(Sqrt[2]*(-1 + x^2)^(1/4))]/(2*Sqrt[2]) + ArcTanh[x/(Sqrt[2]*(-1 + x^2)^(1/4))]/(2*Sqrt[2])", //
				398);
	}

	// {381}
	public void test0504() {
		check(//
				"Integrate[(a + b*x^2)^(-1 - (b*c)/(2*b*c - 2*a*d))*(c + d*x^2)^(-1 + (a*d)/(2*b*c - 2*a*d)), x]", //
				"(x*(c + d*x^2)^((a*d)/(2*b*c - 2*a*d)))/(a*c*(a + b*x^2)^((b*c)/(2*b*c - 2*a*d)))", //
				381);
	}

	// {383}
	public void test0505() {
		check(//
				"Integrate[(1 + x^2)/(-1 + x^2)^2, x]", //
				"x/(1 - x^2)", //
				383);
	}

	// {383}
	public void test0506() {
		check(//
				"Integrate[(1 - x^2)/(1 + x^2)^2, x]", //
				"x/(1 + x^2)", //
				383);
	}

	// {383}
	public void test0507() {
		check(//
				"Integrate[(a + b*x^2)/(-a + b*x^2)^2, x]", //
				"x/(a - b*x^2)", //
				383);
	}

	// {383}
	public void test0508() {
		check(//
				"Integrate[(a + b*x^2)/(a - b*x^2)^2, x]", //
				"x/(a - b*x^2)", //
				383);
	}

	// {446, 74}
	public void test0509() {
		check(//
				"Integrate[(1 + 2*x^2)/(x^5*(1 + x^2)^3), x]", //
				"-1/(4*x^4*(1 + x^2)^2)", //
				446, 74);
	}

	// {393}
	public void test0510() {
		check(//
				"Integrate[1/((1 - x^2)^(1/3)*(3 + x^2)), x]", //
				"ArcTan[Sqrt[3]/x]/(2*2^(2/3)*Sqrt[3]) + ArcTan[(Sqrt[3]*(1 - 2^(1/3)*(1 - x^2)^(1/3)))/x]/(2*2^(2/3)*Sqrt[3]) - ArcTanh[x]/(6*2^(2/3)) + ArcTanh[x/(1 + 2^(1/3)*(1 - x^2)^(1/3))]/(2*2^(2/3))", //
				393);
	}

	// {439}
	public void test0511() {
		check(//
				"Integrate[x/((2 - 3*x^2)^(1/4)*(4 - 3*x^2)), x]", //
				"ArcTan[(Sqrt[2] - Sqrt[2 - 3*x^2])/(2^(3/4)*(2 - 3*x^2)^(1/4))]/(3*2^(3/4)) + ArcTanh[(Sqrt[2] + Sqrt[2 - 3*x^2])/(2^(3/4)*(2 - 3*x^2)^(1/4))]/(3*2^(3/4))", //
				439);
	}

	// {397}
	public void test0512() {
		check(//
				"Integrate[1/((2 - 3*x^2)^(1/4)*(4 - 3*x^2)), x]", //
				"ArcTan[(2 - Sqrt[2]*Sqrt[2 - 3*x^2])/(2^(1/4)*Sqrt[3]*x*(2 - 3*x^2)^(1/4))]/(2*2^(3/4)*Sqrt[3]) + ArcTanh[(2 + Sqrt[2]*Sqrt[2 - 3*x^2])/(2^(1/4)*Sqrt[3]*x*(2 - 3*x^2)^(1/4))]/(2*2^(3/4)*Sqrt[3])", //
				397);
	}

	// {398}
	public void test0513() {
		check(//
				"Integrate[1/((-2 + 3*x^2)*(-1 + 3*x^2)^(1/4)), x]", //
				"-ArcTan[(Sqrt[3/2]*x)/(-1 + 3*x^2)^(1/4)]/(2*Sqrt[6]) - ArcTanh[(Sqrt[3/2]*x)/(-1 + 3*x^2)^(1/4)]/(2*Sqrt[6])", //
				398);
	}

	// {441}
	public void test0514() {
		check(//
				"Integrate[x^2/((2 + 3*x^2)^(3/4)*(4 + 3*x^2)), x]", //
				"-ArcTan[(2*2^(3/4) + 2*2^(1/4)*Sqrt[2 + 3*x^2])/(2*Sqrt[3]*x*(2 + 3*x^2)^(1/4))]/(3*2^(1/4)*Sqrt[3]) + ArcTanh[(2*2^(3/4) - 2*2^(1/4)*Sqrt[2 + 3*x^2])/(2*Sqrt[3]*x*(2 + 3*x^2)^(1/4))]/(3*2^(1/4)*Sqrt[3])", //
				441);
	}

	// {441}
	public void test0515() {
		check(//
				"Integrate[x^2/((2 - 3*x^2)^(3/4)*(4 - 3*x^2)), x]", //
				"ArcTan[(2 - Sqrt[2]*Sqrt[2 - 3*x^2])/(2^(1/4)*Sqrt[3]*x*(2 - 3*x^2)^(1/4))]/(3*2^(1/4)*Sqrt[3]) - ArcTanh[(2 + Sqrt[2]*Sqrt[2 - 3*x^2])/(2^(1/4)*Sqrt[3]*x*(2 - 3*x^2)^(1/4))]/(3*2^(1/4)*Sqrt[3])", //
				441);
	}

	// {441}
	public void test0516() {
		check(//
				"Integrate[x^2/((2 + b*x^2)^(3/4)*(4 + b*x^2)), x]", //
				"-(ArcTan[(2*2^(3/4) + 2*2^(1/4)*Sqrt[2 + b*x^2])/(2*Sqrt[b]*x*(2 + b*x^2)^(1/4))]/(2^(1/4)*b^(3/2))) + ArcTanh[(2*2^(3/4) - 2*2^(1/4)*Sqrt[2 + b*x^2])/(2*Sqrt[b]*x*(2 + b*x^2)^(1/4))]/(2^(1/4)*b^(3/2))", //
				441);
	}

	// {441}
	public void test0517() {
		check(//
				"Integrate[x^2/((2 - b*x^2)^(3/4)*(4 - b*x^2)), x]", //
				"ArcTan[(2 - Sqrt[2]*Sqrt[2 - b*x^2])/(2^(1/4)*Sqrt[b]*x*(2 - b*x^2)^(1/4))]/(2^(1/4)*b^(3/2)) - ArcTanh[(2 + Sqrt[2]*Sqrt[2 - b*x^2])/(2^(1/4)*Sqrt[b]*x*(2 - b*x^2)^(1/4))]/(2^(1/4)*b^(3/2))", //
				441);
	}

	// {441}
	public void test0518() {
		check(//
				"Integrate[x^2/((a + 3*x^2)^(3/4)*(2*a + 3*x^2)), x]", //
				"-ArcTan[(a^(3/4)*(1 + Sqrt[a + 3*x^2]/Sqrt[a]))/(Sqrt[3]*x*(a + 3*x^2)^(1/4))]/(3*Sqrt[3]*a^(1/4)) + ArcTanh[(a^(3/4)*(1 - Sqrt[a + 3*x^2]/Sqrt[a]))/(Sqrt[3]*x*(a + 3*x^2)^(1/4))]/(3*Sqrt[3]*a^(1/4))", //
				441);
	}

	// {441}
	public void test0519() {
		check(//
				"Integrate[x^2/((a - 3*x^2)^(3/4)*(2*a - 3*x^2)), x]", //
				"ArcTan[(a^(3/4)*(1 - Sqrt[a - 3*x^2]/Sqrt[a]))/(Sqrt[3]*x*(a - 3*x^2)^(1/4))]/(3*Sqrt[3]*a^(1/4)) - ArcTanh[(a^(3/4)*(1 + Sqrt[a - 3*x^2]/Sqrt[a]))/(Sqrt[3]*x*(a - 3*x^2)^(1/4))]/(3*Sqrt[3]*a^(1/4))", //
				441);
	}

	// {441}
	public void test0520() {
		check(//
				"Integrate[x^2/((a + b*x^2)^(3/4)*(2*a + b*x^2)), x]", //
				"-(ArcTan[(a^(3/4)*(1 + Sqrt[a + b*x^2]/Sqrt[a]))/(Sqrt[b]*x*(a + b*x^2)^(1/4))]/(a^(1/4)*b^(3/2))) + ArcTanh[(a^(3/4)*(1 - Sqrt[a + b*x^2]/Sqrt[a]))/(Sqrt[b]*x*(a + b*x^2)^(1/4))]/(a^(1/4)*b^(3/2))", //
				441);
	}

	// {441}
	public void test0521() {
		check(//
				"Integrate[x^2/((a - b*x^2)^(3/4)*(2*a - b*x^2)), x]", //
				"ArcTan[(a^(3/4)*(1 - Sqrt[a - b*x^2]/Sqrt[a]))/(Sqrt[b]*x*(a - b*x^2)^(1/4))]/(a^(1/4)*b^(3/2)) - ArcTanh[(a^(3/4)*(1 + Sqrt[a - b*x^2]/Sqrt[a]))/(Sqrt[b]*x*(a - b*x^2)^(1/4))]/(a^(1/4)*b^(3/2))", //
				441);
	}

	// {441}
	public void test0522() {
		check(//
				"Integrate[x^2/((2 - 3*x^2)^(3/4)*(4 - 3*x^2)), x]", //
				"ArcTan[(2 - Sqrt[2]*Sqrt[2 - 3*x^2])/(2^(1/4)*Sqrt[3]*x*(2 - 3*x^2)^(1/4))]/(3*2^(1/4)*Sqrt[3]) - ArcTanh[(2 + Sqrt[2]*Sqrt[2 - 3*x^2])/(2^(1/4)*Sqrt[3]*x*(2 - 3*x^2)^(1/4))]/(3*2^(1/4)*Sqrt[3])", //
				441);
	}

	// {442}
	public void test0523() {
		check(//
				"Integrate[x^2/((-2 + 3*x^2)*(-1 + 3*x^2)^(3/4)), x]", //
				"ArcTan[(Sqrt[3/2]*x)/(-1 + 3*x^2)^(1/4)]/(3*Sqrt[6]) - ArcTanh[(Sqrt[3/2]*x)/(-1 + 3*x^2)^(1/4)]/(3*Sqrt[6])", //
				442);
	}

	// {442}
	public void test0524() {
		check(//
				"Integrate[x^2/((-2 - 3*x^2)*(-1 - 3*x^2)^(3/4)), x]", //
				"ArcTan[(Sqrt[3/2]*x)/(-1 - 3*x^2)^(1/4)]/(3*Sqrt[6]) - ArcTanh[(Sqrt[3/2]*x)/(-1 - 3*x^2)^(1/4)]/(3*Sqrt[6])", //
				442);
	}

	// {442}
	public void test0525() {
		check(//
				"Integrate[x^2/((-2 + b*x^2)*(-1 + b*x^2)^(3/4)), x]", //
				"ArcTan[(Sqrt[b]*x)/(Sqrt[2]*(-1 + b*x^2)^(1/4))]/(Sqrt[2]*b^(3/2)) - ArcTanh[(Sqrt[b]*x)/(Sqrt[2]*(-1 + b*x^2)^(1/4))]/(Sqrt[2]*b^(3/2))", //
				442);
	}

	// {442}
	public void test0526() {
		check(//
				"Integrate[x^2/((-2 - b*x^2)*(-1 - b*x^2)^(3/4)), x]", //
				"ArcTan[(Sqrt[b]*x)/(Sqrt[2]*(-1 - b*x^2)^(1/4))]/(Sqrt[2]*b^(3/2)) - ArcTanh[(Sqrt[b]*x)/(Sqrt[2]*(-1 - b*x^2)^(1/4))]/(Sqrt[2]*b^(3/2))", //
				442);
	}

	// {442}
	public void test0527() {
		check(//
				"Integrate[x^2/((-2*a + 3*x^2)*(-a + 3*x^2)^(3/4)), x]", //
				"ArcTan[(Sqrt[3/2]*x)/(a^(1/4)*(-a + 3*x^2)^(1/4))]/(3*Sqrt[6]*a^(1/4)) - ArcTanh[(Sqrt[3/2]*x)/(a^(1/4)*(-a + 3*x^2)^(1/4))]/(3*Sqrt[6]*a^(1/4))", //
				442);
	}

	// {442}
	public void test0528() {
		check(//
				"Integrate[x^2/((-2*a - 3*x^2)*(-a - 3*x^2)^(3/4)), x]", //
				"ArcTan[(Sqrt[3/2]*x)/(a^(1/4)*(-a - 3*x^2)^(1/4))]/(3*Sqrt[6]*a^(1/4)) - ArcTanh[(Sqrt[3/2]*x)/(a^(1/4)*(-a - 3*x^2)^(1/4))]/(3*Sqrt[6]*a^(1/4))", //
				442);
	}

	// {442}
	public void test0529() {
		check(//
				"Integrate[x^2/((-2*a + b*x^2)*(-a + b*x^2)^(3/4)), x]", //
				"ArcTan[(Sqrt[b]*x)/(Sqrt[2]*a^(1/4)*(-a + b*x^2)^(1/4))]/(Sqrt[2]*a^(1/4)*b^(3/2)) - ArcTanh[(Sqrt[b]*x)/(Sqrt[2]*a^(1/4)*(-a + b*x^2)^(1/4))]/(Sqrt[2]*a^(1/4)*b^(3/2))", //
				442);
	}

	// {442}
	public void test0530() {
		check(//
				"Integrate[x^2/((-2*a - b*x^2)*(-a - b*x^2)^(3/4)), x]", //
				"ArcTan[(Sqrt[b]*x)/(Sqrt[2]*a^(1/4)*(-a - b*x^2)^(1/4))]/(Sqrt[2]*a^(1/4)*b^(3/2)) - ArcTanh[(Sqrt[b]*x)/(Sqrt[2]*a^(1/4)*(-a - b*x^2)^(1/4))]/(Sqrt[2]*a^(1/4)*b^(3/2))", //
				442);
	}

	// {442}
	public void test0531() {
		check(//
				"Integrate[x^2/((-2 + 3*x^2)*(-1 + 3*x^2)^(3/4)), x]", //
				"ArcTan[(Sqrt[3/2]*x)/(-1 + 3*x^2)^(1/4)]/(3*Sqrt[6]) - ArcTanh[(Sqrt[3/2]*x)/(-1 + 3*x^2)^(1/4)]/(3*Sqrt[6])", //
				442);
	}

	// {539}
	public void test0532() {
		check(//
				"Integrate[Sqrt[e + f*x^2]/((a + b*x^2)*Sqrt[c + d*x^2]), x]", //
				"(e^(3/2)*Sqrt[c + d*x^2]*EllipticPi[1 - (b*e)/(a*f), ArcTan[(Sqrt[f]*x)/Sqrt[e]], 1 - (d*e)/(c*f)])/(a*c*Sqrt[f]*Sqrt[(e*(c + d*x^2))/(c*(e + f*x^2))]*Sqrt[e + f*x^2])", //
				539);
	}

	// {539}
	public void test0533() {
		check(//
				"Integrate[Sqrt[c + d*x^2]/((a + b*x^2)*Sqrt[e + f*x^2]), x]", //
				"(c^(3/2)*Sqrt[e + f*x^2]*EllipticPi[1 - (b*c)/(a*d), ArcTan[(Sqrt[d]*x)/Sqrt[c]], 1 - (c*f)/(d*e)])/(a*Sqrt[d]*e*Sqrt[c + d*x^2]*Sqrt[(c*(e + f*x^2))/(e*(c + d*x^2))])", //
				539);
	}

	// {539}
	public void test0534() {
		check(//
				"Integrate[Sqrt[2 + x^2]/(Sqrt[1 + x^2]*(a + b*x^2)), x]", //
				"(2*Sqrt[1 + x^2]*EllipticPi[1 - (2*b)/a, ArcTan[x/Sqrt[2]], -1])/(a*Sqrt[(1 + x^2)/(2 + x^2)]*Sqrt[2 + x^2])", //
				539);
	}

	// {539}
	public void test0535() {
		check(//
				"Integrate[Sqrt[2 + d*x^2]/((a + b*x^2)*Sqrt[3 + f*x^2]), x]", //
				"(2*Sqrt[3 + f*x^2]*EllipticPi[1 - (2*b)/(a*d), ArcTan[(Sqrt[d]*x)/Sqrt[2]], 1 - (2*f)/(3*d)])/(Sqrt[3]*a*Sqrt[d]*Sqrt[2 + d*x^2]*Sqrt[(3 + f*x^2)/(2 + d*x^2)])", //
				539);
	}

	// {537}
	public void test0536() {
		check(//
				"Integrate[1/((a + b*x^2)*Sqrt[2 + d*x^2]*Sqrt[3 + f*x^2]), x]", //
				"EllipticPi[(2*b)/(a*d), ArcSin[(Sqrt[-d]*x)/Sqrt[2]], (2*f)/(3*d)]/(Sqrt[3]*a*Sqrt[-d])", //
				537);
	}

	// {637}
	public void test0537() {
		check(//
				"Integrate[(A + B*x)/(a + b*x^2)^(3/2), x]", //
				"-((a*B - A*b*x)/(a*b*Sqrt[a + b*x^2]))", //
				637);
	}

	// {32}
	public void test0538() {
		check(//
				"Integrate[Sqrt[b*x], x]", //
				"(2*(b*x)^(3/2))/(3*b)", //
				32);
	}

	// {32}
	public void test0539() {
		check(//
				"Integrate[(b*x)^(3/2), x]", //
				"(2*(b*x)^(5/2))/(5*b)", //
				32);
	}

	// {32}
	public void test0540() {
		check(//
				"Integrate[1/Sqrt[b*x], x]", //
				"(2*Sqrt[b*x])/b", //
				32);
	}

	// {32}
	public void test0541() {
		check(//
				"Integrate[(b*x)^(-3/2), x]", //
				"-2/(b*Sqrt[b*x])", //
				32);
	}

	// {32}
	public void test0542() {
		check(//
				"Integrate[(b*x)^(1/3), x]", //
				"(3*(b*x)^(4/3))/(4*b)", //
				32);
	}

	// {32}
	public void test0543() {
		check(//
				"Integrate[(b*x)^(2/3), x]", //
				"(3*(b*x)^(5/3))/(5*b)", //
				32);
	}

	// {32}
	public void test0544() {
		check(//
				"Integrate[(b*x)^(-1/3), x]", //
				"(3*(b*x)^(2/3))/(2*b)", //
				32);
	}

	// {32}
	public void test0545() {
		check(//
				"Integrate[(b*x)^(-2/3), x]", //
				"(3*(b*x)^(1/3))/b", //
				32);
	}

	// {261}
	public void test0547() {
		check(//
				"Integrate[x^2*(a + b*x^3)^2, x]", //
				"(a + b*x^3)^3/(9*b)", //
				261);
	}

	// {264}
	public void test0548() {
		check(//
				"Integrate[(a + b*x^3)^2/x^10, x]", //
				"-(a + b*x^3)^3/(9*a*x^9)", //
				264);
	}

	// {261}
	public void test0549() {
		check(//
				"Integrate[x^2*(a + b*x^3)^3, x]", //
				"(a + b*x^3)^4/(12*b)", //
				261);
	}

	// {264}
	public void test0550() {
		check(//
				"Integrate[(a + b*x^3)^3/x^13, x]", //
				"-(a + b*x^3)^4/(12*a*x^12)", //
				264);
	}

	// {261}
	public void test0551() {
		check(//
				"Integrate[x^2*(a + b*x^3)^5, x]", //
				"(a + b*x^3)^6/(18*b)", //
				261);
	}

	// {264}
	public void test0552() {
		check(//
				"Integrate[(a + b*x^3)^5/x^19, x]", //
				"-(a + b*x^3)^6/(18*a*x^18)", //
				264);
	}

	// {261}
	public void test0553() {
		check(//
				"Integrate[x^2*(a + b*x^3)^8, x]", //
				"(a + b*x^3)^9/(27*b)", //
				261);
	}

	// {264}
	public void test0554() {
		check(//
				"Integrate[(a + b*x^3)^8/x^28, x]", //
				"-(a + b*x^3)^9/(27*a*x^27)", //
				264);
	}

	// {260}
	public void test0555() {
		check(//
				"Integrate[x^2/(a + b*x^3), x]", //
				"Log[a + b*x^3]/(3*b)", //
				260);
	}

	// {261}
	public void test0556() {
		check(//
				"Integrate[x^2/(a + b*x^3)^2, x]", //
				"-1/(3*b*(a + b*x^3))", //
				261);
	}

	// {264}
	public void test0557() {
		check(//
				"Integrate[x^5/(a + b*x^3)^3, x]", //
				"x^6/(6*a*(a + b*x^3)^2)", //
				264);
	}

	// {261}
	public void test0558() {
		check(//
				"Integrate[x^2/(a + b*x^3)^3, x]", //
				"-1/(6*b*(a + b*x^3)^2)", //
				261);
	}

	// {260}
	public void test0559() {
		check(//
				"Integrate[x^2/(a - b*x^3), x]", //
				"-Log[a - b*x^3]/(3*b)", //
				260);
	}

	// {261}
	public void test0560() {
		check(//
				"Integrate[x^2*Sqrt[a + b*x^3], x]", //
				"(2*(a + b*x^3)^(3/2))/(9*b)", //
				261);
	}

	// {261}
	public void test0561() {
		check(//
				"Integrate[x^2*(a + b*x^3)^(3/2), x]", //
				"(2*(a + b*x^3)^(5/2))/(15*b)", //
				261);
	}

	// {261}
	public void test0562() {
		check(//
				"Integrate[x^2/Sqrt[a + b*x^3], x]", //
				"(2*Sqrt[a + b*x^3])/(3*b)", //
				261);
	}

	// {218}
	public void test0563() {
		check(//
				"Integrate[1/Sqrt[a + b*x^3], x]", //
				"(2*Sqrt[2 + Sqrt[3]]*(a^(1/3) + b^(1/3)*x)*Sqrt[(a^(2/3) - a^(1/3)*b^(1/3)*x + b^(2/3)*x^2)/((1 + Sqrt[3])*a^(1/3) + b^(1/3)*x)^2]*EllipticF[ArcSin[((1 - Sqrt[3])*a^(1/3) + b^(1/3)*x)/((1 + Sqrt[3])*a^(1/3) + b^(1/3)*x)], -7 - 4*Sqrt[3]])/(3^(1/4)*b^(1/3)*Sqrt[(a^(1/3)*(a^(1/3) + b^(1/3)*x))/((1 + Sqrt[3])*a^(1/3) + b^(1/3)*x)^2]*Sqrt[a + b*x^3])", //
				218);
	}

	// {261}
	public void test0564() {
		check(//
				"Integrate[x^2/(a + b*x^3)^(3/2), x]", //
				"-2/(3*b*Sqrt[a + b*x^3])", //
				261);
	}

	// {261}
	public void test0565() {
		check(//
				"Integrate[x^2/Sqrt[1 + x^3], x]", //
				"(2*Sqrt[1 + x^3])/3", //
				261);
	}

	// {218}
	public void test0566() {
		check(//
				"Integrate[1/Sqrt[1 + x^3], x]", //
				"(2*Sqrt[2 + Sqrt[3]]*(1 + x)*Sqrt[(1 - x + x^2)/(1 + Sqrt[3] + x)^2]*EllipticF[ArcSin[(1 - Sqrt[3] + x)/(1 + Sqrt[3] + x)], -7 - 4*Sqrt[3]])/(3^(1/4)*Sqrt[(1 + x)/(1 + Sqrt[3] + x)^2]*Sqrt[1 + x^3])", //
				218);
	}

	// {261}
	public void test0567() {
		check(//
				"Integrate[x^2/Sqrt[1 - x^3], x]", //
				"(-2*Sqrt[1 - x^3])/3", //
				261);
	}

	// {218}
	public void test0568() {
		check(//
				"Integrate[1/Sqrt[1 - x^3], x]", //
				"(-2*Sqrt[2 + Sqrt[3]]*(1 - x)*Sqrt[(1 + x + x^2)/(1 + Sqrt[3] - x)^2]*EllipticF[ArcSin[(1 - Sqrt[3] - x)/(1 + Sqrt[3] - x)], -7 - 4*Sqrt[3]])/(3^(1/4)*Sqrt[(1 - x)/(1 + Sqrt[3] - x)^2]*Sqrt[1 - x^3])", //
				218);
	}

	// {261}
	public void test0569() {
		check(//
				"Integrate[x^2/Sqrt[-1 + x^3], x]", //
				"(2*Sqrt[-1 + x^3])/3", //
				261);
	}

	// {219}
	public void test0570() {
		check(//
				"Integrate[1/Sqrt[-1 + x^3], x]", //
				"(-2*Sqrt[2 - Sqrt[3]]*(1 - x)*Sqrt[(1 + x + x^2)/(1 - Sqrt[3] - x)^2]*EllipticF[ArcSin[(1 + Sqrt[3] - x)/(1 - Sqrt[3] - x)], -7 + 4*Sqrt[3]])/(3^(1/4)*Sqrt[-((1 - x)/(1 - Sqrt[3] - x)^2)]*Sqrt[-1 + x^3])", //
				219);
	}

	// {261}
	public void test0571() {
		check(//
				"Integrate[x^2/Sqrt[-1 - x^3], x]", //
				"(-2*Sqrt[-1 - x^3])/3", //
				261);
	}

	// {219}
	public void test0572() {
		check(//
				"Integrate[1/Sqrt[-1 - x^3], x]", //
				"(2*Sqrt[2 - Sqrt[3]]*(1 + x)*Sqrt[(1 - x + x^2)/(1 - Sqrt[3] + x)^2]*EllipticF[ArcSin[(1 + Sqrt[3] + x)/(1 - Sqrt[3] + x)], -7 + 4*Sqrt[3]])/(3^(1/4)*Sqrt[-((1 + x)/(1 - Sqrt[3] + x)^2)]*Sqrt[-1 - x^3])", //
				219);
	}

	// {261}
	public void test0573() {
		check(//
				"Integrate[x^2*(a + b*x^3)^(1/3), x]", //
				"(a + b*x^3)^(4/3)/(4*b)", //
				261);
	}

	// {264}
	public void test0574() {
		check(//
				"Integrate[(a + b*x^3)^(1/3)/x^5, x]", //
				"-(a + b*x^3)^(4/3)/(4*a*x^4)", //
				264);
	}

	// {261}
	public void test0575() {
		check(//
				"Integrate[x^2*(a + b*x^3)^(2/3), x]", //
				"(a + b*x^3)^(5/3)/(5*b)", //
				261);
	}

	// {264}
	public void test0576() {
		check(//
				"Integrate[(a + b*x^3)^(2/3)/x^6, x]", //
				"-(a + b*x^3)^(5/3)/(5*a*x^5)", //
				264);
	}

	// {261}
	public void test0577() {
		check(//
				"Integrate[x^2/(a + b*x^3)^(1/3), x]", //
				"(a + b*x^3)^(2/3)/(2*b)", //
				261);
	}

	// {239}
	public void test0578() {
		check(//
				"Integrate[(a + b*x^3)^(-1/3), x]", //
				"ArcTan[(1 + (2*b^(1/3)*x)/(a + b*x^3)^(1/3))/Sqrt[3]]/(Sqrt[3]*b^(1/3)) - Log[-(b^(1/3)*x) + (a + b*x^3)^(1/3)]/(2*b^(1/3))", //
				239);
	}

	// {264}
	public void test0579() {
		check(//
				"Integrate[1/(x^3*(a + b*x^3)^(1/3)), x]", //
				"-(a + b*x^3)^(2/3)/(2*a*x^2)", //
				264);
	}

	// {261}
	public void test0580() {
		check(//
				"Integrate[x^2/(a + b*x^3)^(2/3), x]", //
				"(a + b*x^3)^(1/3)/b", //
				261);
	}

	// {264}
	public void test0581() {
		check(//
				"Integrate[1/(x^2*(a + b*x^3)^(2/3)), x]", //
				"-((a + b*x^3)^(1/3)/(a*x))", //
				264);
	}

	// {239}
	public void test0582() {
		check(//
				"Integrate[(a - b*x^3)^(-1/3), x]", //
				"-(ArcTan[(1 - (2*b^(1/3)*x)/(a - b*x^3)^(1/3))/Sqrt[3]]/(Sqrt[3]*b^(1/3))) + Log[b^(1/3)*x + (a - b*x^3)^(1/3)]/(2*b^(1/3))", //
				239);
	}

	// {239}
	public void test0583() {
		check(//
				"Integrate[(2 + x^3)^(-1/3), x]", //
				"ArcTan[(1 + (2*x)/(2 + x^3)^(1/3))/Sqrt[3]]/Sqrt[3] - Log[-x + (2 + x^3)^(1/3)]/2", //
				239);
	}

	// {261}
	public void test0584() {
		check(//
				"Integrate[x^2/(2 + x^3)^(1/4), x]", //
				"(4*(2 + x^3)^(3/4))/9", //
				261);
	}

	// {364}
	public void test0585() {
		check(//
				"Integrate[x^m/(a + b*x^3), x]", //
				"(x^(1 + m)*Hypergeometric2F1[1, (1 + m)/3, (4 + m)/3, -((b*x^3)/a)])/(a*(1 + m))", //
				364);
	}

	// {364}
	public void test0586() {
		check(//
				"Integrate[x^m/(a + b*x^3)^2, x]", //
				"(x^(1 + m)*Hypergeometric2F1[2, (1 + m)/3, (4 + m)/3, -((b*x^3)/a)])/(a^2*(1 + m))", //
				364);
	}

	// {364}
	public void test0587() {
		check(//
				"Integrate[x^m/(a + b*x^3)^3, x]", //
				"(x^(1 + m)*Hypergeometric2F1[3, (1 + m)/3, (4 + m)/3, -((b*x^3)/a)])/(a^3*(1 + m))", //
				364);
	}

	// {261}
	public void test0588() {
		check(//
				"Integrate[x^2*(a + b*x^3)^p, x]", //
				"(a + b*x^3)^(1 + p)/(3*b*(1 + p))", //
				261);
	}

	// {261}
	public void test0590() {
		check(//
				"Integrate[x^3*(a + b*x^4)^2, x]", //
				"(a + b*x^4)^3/(12*b)", //
				261);
	}

	// {261}
	public void test0591() {
		check(//
				"Integrate[x^3*(a + b*x^4)^3, x]", //
				"(a + b*x^4)^4/(16*b)", //
				261);
	}

	// {260}
	public void test0592() {
		check(//
				"Integrate[x^3/(a + c*x^4), x]", //
				"Log[a + c*x^4]/(4*c)", //
				260);
	}

	// {261}
	public void test0593() {
		check(//
				"Integrate[x^3/(a + c*x^4)^2, x]", //
				"-1/(4*c*(a + c*x^4))", //
				261);
	}

	// {264}
	public void test0594() {
		check(//
				"Integrate[x^7/(a + c*x^4)^3, x]", //
				"x^8/(8*a*(a + c*x^4)^2)", //
				264);
	}

	// {261}
	public void test0595() {
		check(//
				"Integrate[x^3/(a + c*x^4)^3, x]", //
				"-1/(8*c*(a + c*x^4)^2)", //
				261);
	}

	// {260}
	public void test0596() {
		check(//
				"Integrate[x^3/(2 + 3*x^4), x]", //
				"Log[2 + 3*x^4]/12", //
				260);
	}

	// {261}
	public void test0597() {
		check(//
				"Integrate[x^3/(2 + 3*x^4)^2, x]", //
				"-1/(12*(2 + 3*x^4))", //
				261);
	}

	// {260}
	public void test0598() {
		check(//
				"Integrate[x^3/(2*a + 2*b + x^4), x]", //
				"Log[2*(a + b) + x^4]/4", //
				260);
	}

	// {260}
	public void test0599() {
		check(//
				"Integrate[x^3/(2*(a + b) + x^4), x]", //
				"Log[2*(a + b) + x^4]/4", //
				260);
	}

	// {261}
	public void test0600() {
		check(//
				"Integrate[x^3*Sqrt[a + c*x^4], x]", //
				"(a + c*x^4)^(3/2)/(6*c)", //
				261);
	}

	// {264}
	public void test0601() {
		check(//
				"Integrate[Sqrt[a + c*x^4]/x^7, x]", //
				"-(a + c*x^4)^(3/2)/(6*a*x^6)", //
				264);
	}

	// {261}
	public void test0602() {
		check(//
				"Integrate[x^3*(a + c*x^4)^(3/2), x]", //
				"(a + c*x^4)^(5/2)/(10*c)", //
				261);
	}

	// {264}
	public void test0603() {
		check(//
				"Integrate[(a + c*x^4)^(3/2)/x^11, x]", //
				"-(a + c*x^4)^(5/2)/(10*a*x^10)", //
				264);
	}

	// {261}
	public void test0604() {
		check(//
				"Integrate[x^3*Sqrt[5 + x^4], x]", //
				"(5 + x^4)^(3/2)/6", //
				261);
	}

	// {261}
	public void test0605() {
		check(//
				"Integrate[x^3/Sqrt[a + b*x^4], x]", //
				"Sqrt[a + b*x^4]/(2*b)", //
				261);
	}

	// {264}
	public void test0606() {
		check(//
				"Integrate[1/(x^3*Sqrt[a + b*x^4]), x]", //
				"-Sqrt[a + b*x^4]/(2*a*x^2)", //
				264);
	}

	// {220}
	public void test0607() {
		check(//
				"Integrate[1/Sqrt[a + b*x^4], x]", //
				"((Sqrt[a] + Sqrt[b]*x^2)*Sqrt[(a + b*x^4)/(Sqrt[a] + Sqrt[b]*x^2)^2]*EllipticF[2*ArcTan[(b^(1/4)*x)/a^(1/4)], 1/2])/(2*a^(1/4)*b^(1/4)*Sqrt[a + b*x^4])", //
				220);
	}

	// {261}
	public void test0608() {
		check(//
				"Integrate[x^3/Sqrt[a - b*x^4], x]", //
				"-Sqrt[a - b*x^4]/(2*b)", //
				261);
	}

	// {264}
	public void test0609() {
		check(//
				"Integrate[1/(x^3*Sqrt[a - b*x^4]), x]", //
				"-Sqrt[a - b*x^4]/(2*a*x^2)", //
				264);
	}

	// {261}
	public void test0610() {
		check(//
				"Integrate[x^3/(a + b*x^4)^(3/2), x]", //
				"-1/(2*b*Sqrt[a + b*x^4])", //
				261);
	}

	// {264}
	public void test0611() {
		check(//
				"Integrate[x/(a + b*x^4)^(3/2), x]", //
				"x^2/(2*a*Sqrt[a + b*x^4])", //
				264);
	}

	// {261}
	public void test0612() {
		check(//
				"Integrate[x^3/Sqrt[1 - x^4], x]", //
				"-Sqrt[1 - x^4]/2", //
				261);
	}

	// {264}
	public void test0613() {
		check(//
				"Integrate[1/(x^3*Sqrt[1 - x^4]), x]", //
				"-Sqrt[1 - x^4]/(2*x^2)", //
				264);
	}

	// {221}
	public void test0614() {
		check(//
				"Integrate[1/Sqrt[1 - x^4], x]", //
				"EllipticF[ArcSin[x], -1]", //
				221);
	}

	// {261}
	public void test0615() {
		check(//
				"Integrate[x^3/(1 - x^4)^(3/2), x]", //
				"1/(2*Sqrt[1 - x^4])", //
				261);
	}

	// {264}
	public void test0616() {
		check(//
				"Integrate[x/(1 - x^4)^(3/2), x]", //
				"x^2/(2*Sqrt[1 - x^4])", //
				264);
	}

	// {261}
	public void test0617() {
		check(//
				"Integrate[x^3/Sqrt[1 + x^4], x]", //
				"Sqrt[1 + x^4]/2", //
				261);
	}

	// {264}
	public void test0618() {
		check(//
				"Integrate[1/(x^3*Sqrt[1 + x^4]), x]", //
				"-Sqrt[1 + x^4]/(2*x^2)", //
				264);
	}

	// {220}
	public void test0619() {
		check(//
				"Integrate[1/Sqrt[1 + x^4], x]", //
				"((1 + x^2)*Sqrt[(1 + x^4)/(1 + x^2)^2]*EllipticF[2*ArcTan[x], 1/2])/(2*Sqrt[1 + x^4])", //
				220);
	}

	// {261}
	public void test0620() {
		check(//
				"Integrate[x^3/(1 + x^4)^(3/2), x]", //
				"-1/(2*Sqrt[1 + x^4])", //
				261);
	}

	// {264}
	public void test0621() {
		check(//
				"Integrate[x/(1 + x^4)^(3/2), x]", //
				"x^2/(2*Sqrt[1 + x^4])", //
				264);
	}

	// {261}
	public void test0622() {
		check(//
				"Integrate[x^3/Sqrt[16 - x^4], x]", //
				"-Sqrt[16 - x^4]/2", //
				261);
	}

	// {264}
	public void test0623() {
		check(//
				"Integrate[1/(x^3*Sqrt[16 - x^4]), x]", //
				"-Sqrt[16 - x^4]/(32*x^2)", //
				264);
	}

	// {221}
	public void test0624() {
		check(//
				"Integrate[1/Sqrt[16 - x^4], x]", //
				"EllipticF[ArcSin[x/2], -1]/2", //
				221);
	}

	// {222}
	public void test0625() {
		check(//
				"Integrate[1/Sqrt[-1 + x^4], x]", //
				"(Sqrt[-1 + x^2]*Sqrt[1 + x^2]*EllipticF[ArcSin[(Sqrt[2]*x)/Sqrt[-1 + x^2]], 1/2])/(Sqrt[2]*Sqrt[-1 + x^4])", //
				222);
	}

	// {261}
	public void test0626() {
		check(//
				"Integrate[x^3/(1 + x^4)^(4/3), x]", //
				"-3/(4*(1 + x^4)^(1/3))", //
				261);
	}

	// {261}
	public void test0627() {
		check(//
				"Integrate[x^3/(1 + x^4)^(1/3), x]", //
				"(3*(1 + x^4)^(2/3))/8", //
				261);
	}

	// {261}
	public void test0628() {
		check(//
				"Integrate[x^3*(a + b*x^4)^(1/4), x]", //
				"(a + b*x^4)^(5/4)/(5*b)", //
				261);
	}

	// {264}
	public void test0629() {
		check(//
				"Integrate[(a + b*x^4)^(1/4)/x^6, x]", //
				"-(a + b*x^4)^(5/4)/(5*a*x^5)", //
				264);
	}

	// {261}
	public void test0630() {
		check(//
				"Integrate[x^3*(a + b*x^4)^(3/4), x]", //
				"(a + b*x^4)^(7/4)/(7*b)", //
				261);
	}

	// {264}
	public void test0631() {
		check(//
				"Integrate[(a + b*x^4)^(3/4)/x^8, x]", //
				"-(a + b*x^4)^(7/4)/(7*a*x^7)", //
				264);
	}

	// {261}
	public void test0632() {
		check(//
				"Integrate[x^3*(a + b*x^4)^(5/4), x]", //
				"(a + b*x^4)^(9/4)/(9*b)", //
				261);
	}

	// {264}
	public void test0633() {
		check(//
				"Integrate[(a + b*x^4)^(5/4)/x^10, x]", //
				"-(a + b*x^4)^(9/4)/(9*a*x^9)", //
				264);
	}

	// {261}
	public void test0634() {
		check(//
				"Integrate[x^3/(a + b*x^4)^(1/4), x]", //
				"(a + b*x^4)^(3/4)/(3*b)", //
				261);
	}

	// {264}
	public void test0635() {
		check(//
				"Integrate[1/(x^4*(a + b*x^4)^(1/4)), x]", //
				"-(a + b*x^4)^(3/4)/(3*a*x^3)", //
				264);
	}

	// {261}
	public void test0636() {
		check(//
				"Integrate[x^3/(a + b*x^4)^(3/4), x]", //
				"(a + b*x^4)^(1/4)/b", //
				261);
	}

	// {264}
	public void test0637() {
		check(//
				"Integrate[1/(x^2*(a + b*x^4)^(3/4)), x]", //
				"-((a + b*x^4)^(1/4)/(a*x))", //
				264);
	}

	// {261}
	public void test0638() {
		check(//
				"Integrate[x^3/(a + b*x^4)^(5/4), x]", //
				"-(1/(b*(a + b*x^4)^(1/4)))", //
				261);
	}

	// {191}
	public void test0639() {
		check(//
				"Integrate[(a + b*x^4)^(-5/4), x]", //
				"x/(a*(a + b*x^4)^(1/4))", //
				191);
	}

	// {261}
	public void test0640() {
		check(//
				"Integrate[x^3*(a - b*x^4)^(1/4), x]", //
				"-(a - b*x^4)^(5/4)/(5*b)", //
				261);
	}

	// {264}
	public void test0641() {
		check(//
				"Integrate[(a - b*x^4)^(1/4)/x^6, x]", //
				"-(a - b*x^4)^(5/4)/(5*a*x^5)", //
				264);
	}

	// {261}
	public void test0642() {
		check(//
				"Integrate[x^3/(a - b*x^4)^(1/4), x]", //
				"-(a - b*x^4)^(3/4)/(3*b)", //
				261);
	}

	// {264}
	public void test0643() {
		check(//
				"Integrate[1/(x^4*(a - b*x^4)^(1/4)), x]", //
				"-(a - b*x^4)^(3/4)/(3*a*x^3)", //
				264);
	}

	// {261}
	public void test0644() {
		check(//
				"Integrate[x^3/(a - b*x^4)^(3/4), x]", //
				"-((a - b*x^4)^(1/4)/b)", //
				261);
	}

	// {264}
	public void test0645() {
		check(//
				"Integrate[1/(x^2*(a - b*x^4)^(3/4)), x]", //
				"-((a - b*x^4)^(1/4)/(a*x))", //
				264);
	}

	// {261}
	public void test0646() {
		check(//
				"Integrate[x^3*(a + b*x^4)^p, x]", //
				"(a + b*x^4)^(1 + p)/(4*b*(1 + p))", //
				261);
	}

	// {260}
	public void test0647() {
		check(//
				"Integrate[x^4/(a + b*x^5), x]", //
				"Log[a + b*x^5]/(5*b)", //
				260);
	}

	// {261}
	public void test0648() {
		check(//
				"Integrate[x^4/(a + b*x^5)^2, x]", //
				"-1/(5*b*(a + b*x^5))", //
				261);
	}

	// {260}
	public void test0649() {
		check(//
				"Integrate[x^4/(2*b + b*x^5), x]", //
				"Log[2 + x^5]/(5*b)", //
				260);
	}

	// {260}
	public void test0650() {
		check(//
				"Integrate[x^4/(3 + b*x^5), x]", //
				"Log[3 + b*x^5]/(5*b)", //
				260);
	}

	// {260}
	public void test0651() {
		check(//
				"Integrate[x^4/(1 + x^5), x]", //
				"Log[1 + x^5]/5", //
				260);
	}

	// {264}
	public void test0652() {
		check(//
				"Integrate[1/(x^(7/2)*Sqrt[a + b*x^5]), x]", //
				"(-2*Sqrt[a + b*x^5])/(5*a*x^(5/2))", //
				264);
	}

	// {264}
	public void test0653() {
		check(//
				"Integrate[1/(x^(7/2)*Sqrt[1 + x^5]), x]", //
				"(-2*Sqrt[1 + x^5])/(5*x^(5/2))", //
				264);
	}

	// {260}
	public void test0654() {
		check(//
				"Integrate[x^5/(a + b*x^6), x]", //
				"Log[a + b*x^6]/(6*b)", //
				260);
	}

	// {261}
	public void test0655() {
		check(//
				"Integrate[x^5/(a + b*x^6)^2, x]", //
				"-1/(6*b*(a + b*x^6))", //
				261);
	}

	// {260}
	public void test0656() {
		check(//
				"Integrate[x^5/(1 - x^6), x]", //
				"-Log[1 - x^6]/6", //
				260);
	}

	// {260}
	public void test0657() {
		check(//
				"Integrate[x^5/(1 + x^6), x]", //
				"Log[1 + x^6]/6", //
				260);
	}

	// {261}
	public void test0658() {
		check(//
				"Integrate[x^5*Sqrt[a^6 - x^6], x]", //
				"-(a^6 - x^6)^(3/2)/9", //
				261);
	}

	// {261}
	public void test0659() {
		check(//
				"Integrate[x^5/Sqrt[2 + x^6], x]", //
				"Sqrt[2 + x^6]/3", //
				261);
	}

	// {264}
	public void test0660() {
		check(//
				"Integrate[1/(x^4*Sqrt[2 + x^6]), x]", //
				"-Sqrt[2 + x^6]/(6*x^3)", //
				264);
	}

	// {225}
	public void test0661() {
		check(//
				"Integrate[1/Sqrt[2 + x^6], x]", //
				"(x*(2^(1/3) + x^2)*Sqrt[(2^(2/3) - 2^(1/3)*x^2 + x^4)/(2^(1/3) + (1 + Sqrt[3])*x^2)^2]*EllipticF[ArcCos[(2^(1/3) + (1 - Sqrt[3])*x^2)/(2^(1/3) + (1 + Sqrt[3])*x^2)], (2 + Sqrt[3])/4])/(2*2^(1/3)*3^(1/4)*Sqrt[(x^2*(2^(1/3) + x^2))/(2^(1/3) + (1 + Sqrt[3])*x^2)^2]*Sqrt[2 + x^6])", //
				225);
	}

	// {261}
	public void test0662() {
		check(//
				"Integrate[x^5/(2 + x^6)^(3/2), x]", //
				"-1/(3*Sqrt[2 + x^6])", //
				261);
	}

	// {264}
	public void test0663() {
		check(//
				"Integrate[x^2/(2 + x^6)^(3/2), x]", //
				"x^3/(6*Sqrt[2 + x^6])", //
				264);
	}

	// {364}
	public void test0664() {
		check(//
				"Integrate[x^m/(a + b*x^7), x]", //
				"(x^(1 + m)*Hypergeometric2F1[1, (1 + m)/7, (8 + m)/7, -((b*x^7)/a)])/(a*(1 + m))", //
				364);
	}

	// {260}
	public void test0665() {
		check(//
				"Integrate[x^6/(a + b*x^7), x]", //
				"Log[a + b*x^7]/(7*b)", //
				260);
	}

	// {364}
	public void test0666() {
		check(//
				"Integrate[x^m/(a - b*x^7), x]", //
				"(x^(1 + m)*Hypergeometric2F1[1, (1 + m)/7, (8 + m)/7, (b*x^7)/a])/(a*(1 + m))", //
				364);
	}

	// {260}
	public void test0667() {
		check(//
				"Integrate[x^6/(a - b*x^7), x]", //
				"-Log[a - b*x^7]/(7*b)", //
				260);
	}

	// {260}
	public void test0668() {
		check(//
				"Integrate[x^7/(a + b*x^8), x]", //
				"Log[a + b*x^8]/(8*b)", //
				260);
	}

	// {260}
	public void test0669() {
		check(//
				"Integrate[x^7/(1 - x^8), x]", //
				"-Log[1 - x^8]/8", //
				260);
	}

	// {260}
	public void test0670() {
		check(//
				"Integrate[x^7/(1 + x^8), x]", //
				"Log[1 + x^8]/8", //
				260);
	}

	// {261}
	public void test0671() {
		check(//
				"Integrate[x^7/Sqrt[1 + x^8], x]", //
				"Sqrt[1 + x^8]/4", //
				261);
	}

	// {264}
	public void test0672() {
		check(//
				"Integrate[1/(x^5*Sqrt[1 + x^8]), x]", //
				"-Sqrt[1 + x^8]/(4*x^4)", //
				264);
	}

	// {364}
	public void test0673() {
		check(//
				"Integrate[x^6/Sqrt[1 + x^8], x]", //
				"(x^7*Hypergeometric2F1[1/2, 7/8, 15/8, -x^8])/7", //
				364);
	}

	// {364}
	public void test0674() {
		check(//
				"Integrate[x^4/Sqrt[1 + x^8], x]", //
				"(x^5*Hypergeometric2F1[1/2, 5/8, 13/8, -x^8])/5", //
				364);
	}

	// {261}
	public void test0676() {
		check(//
				"Integrate[(a + b/x)^2/x^2, x]", //
				"-(a + b/x)^3/(3*b)", //
				261);
	}

	// {261}
	public void test0677() {
		check(//
				"Integrate[(a + b/x)^3/x^2, x]", //
				"-(a + b/x)^4/(4*b)", //
				261);
	}

	// {261}
	public void test0678() {
		check(//
				"Integrate[(a + b/x)^8/x^2, x]", //
				"-(a + b/x)^9/(9*b)", //
				261);
	}

	// {260}
	public void test0679() {
		check(//
				"Integrate[1/((a + b/x)*x^2), x]", //
				"-(Log[a + b/x]/b)", //
				260);
	}

	// {261}
	public void test0680() {
		check(//
				"Integrate[1/((a + b/x)^2*x^2), x]", //
				"1/(b*(a + b/x))", //
				261);
	}

	// {261}
	public void test0681() {
		check(//
				"Integrate[1/((a + b/x)^3*x^2), x]", //
				"1/(2*b*(a + b/x)^2)", //
				261);
	}

	// {261}
	public void test0682() {
		check(//
				"Integrate[Sqrt[a + b/x]/x^2, x]", //
				"(-2*(a + b/x)^(3/2))/(3*b)", //
				261);
	}

	// {261}
	public void test0683() {
		check(//
				"Integrate[(a + b/x)^(3/2)/x^2, x]", //
				"(-2*(a + b/x)^(5/2))/(5*b)", //
				261);
	}

	// {261}
	public void test0684() {
		check(//
				"Integrate[(a + b/x)^(5/2)/x^2, x]", //
				"(-2*(a + b/x)^(7/2))/(7*b)", //
				261);
	}

	// {261}
	public void test0685() {
		check(//
				"Integrate[1/(Sqrt[a + b/x]*x^2), x]", //
				"(-2*Sqrt[a + b/x])/b", //
				261);
	}

	// {261}
	public void test0686() {
		check(//
				"Integrate[1/((a + b/x)^(3/2)*x^2), x]", //
				"2/(b*Sqrt[a + b/x])", //
				261);
	}

	// {261}
	public void test0687() {
		check(//
				"Integrate[1/((a + b/x)^(5/2)*x^2), x]", //
				"2/(3*b*(a + b/x)^(3/2))", //
				261);
	}

	// {264}
	public void test0688() {
		check(//
				"Integrate[Sqrt[a + b/x]*Sqrt[x], x]", //
				"(2*(a + b/x)^(3/2)*x^(3/2))/(3*a)", //
				264);
	}

	// {264}
	public void test0689() {
		check(//
				"Integrate[(a + b/x)^(3/2)*x^(3/2), x]", //
				"(2*(a + b/x)^(5/2)*x^(5/2))/(5*a)", //
				264);
	}

	// {264}
	public void test0690() {
		check(//
				"Integrate[(a + b/x)^(5/2)*x^(5/2), x]", //
				"(2*(a + b/x)^(7/2)*x^(7/2))/(7*a)", //
				264);
	}

	// {264}
	public void test0691() {
		check(//
				"Integrate[1/(Sqrt[a + b/x]*Sqrt[x]), x]", //
				"(2*Sqrt[a + b/x]*Sqrt[x])/a", //
				264);
	}

	// {264}
	public void test0692() {
		check(//
				"Integrate[1/((a + b/x)^(3/2)*x^(3/2)), x]", //
				"-2/(a*Sqrt[a + b/x]*Sqrt[x])", //
				264);
	}

	// {264}
	public void test0693() {
		check(//
				"Integrate[1/((a + b/x)^(5/2)*x^(5/2)), x]", //
				"-2/(3*a*(a + b/x)^(3/2)*x^(3/2))", //
				264);
	}

	// {261}
	public void test0695() {
		check(//
				"Integrate[(a + b/x^2)^2/x^3, x]", //
				"-(a + b/x^2)^3/(6*b)", //
				261);
	}

	// {261}
	public void test0696() {
		check(//
				"Integrate[(a + b/x^2)^3/x^3, x]", //
				"-(a + b/x^2)^4/(8*b)", //
				261);
	}

	// {260}
	public void test0697() {
		check(//
				"Integrate[1/((a + b/x^2)*x^3), x]", //
				"-Log[a + b/x^2]/(2*b)", //
				260);
	}

	// {261}
	public void test0698() {
		check(//
				"Integrate[1/((a + b/x^2)^2*x^3), x]", //
				"1/(2*b*(a + b/x^2))", //
				261);
	}

	// {261}
	public void test0699() {
		check(//
				"Integrate[1/((a + b/x^2)^3*x^3), x]", //
				"1/(4*b*(a + b/x^2)^2)", //
				261);
	}

	// {264}
	public void test0700() {
		check(//
				"Integrate[Sqrt[a + b/x^2]*x^2, x]", //
				"((a + b/x^2)^(3/2)*x^3)/(3*a)", //
				264);
	}

	// {261}
	public void test0701() {
		check(//
				"Integrate[Sqrt[a + b/x^2]/x^3, x]", //
				"-(a + b/x^2)^(3/2)/(3*b)", //
				261);
	}

	// {261}
	public void test0702() {
		check(//
				"Integrate[(a + b/x^2)^(3/2)/x^3, x]", //
				"-(a + b/x^2)^(5/2)/(5*b)", //
				261);
	}

	// {261}
	public void test0703() {
		check(//
				"Integrate[(a + b/x^2)^(5/2)/x^3, x]", //
				"-(a + b/x^2)^(7/2)/(7*b)", //
				261);
	}

	// {261}
	public void test0704() {
		check(//
				"Integrate[1/(Sqrt[a + b/x^2]*x^3), x]", //
				"-(Sqrt[a + b/x^2]/b)", //
				261);
	}

	// {191}
	public void test0705() {
		check(//
				"Integrate[1/Sqrt[a + b/x^2], x]", //
				"(Sqrt[a + b/x^2]*x)/a", //
				191);
	}

	// {261}
	public void test0706() {
		check(//
				"Integrate[1/((a + b/x^2)^(3/2)*x^3), x]", //
				"1/(b*Sqrt[a + b/x^2])", //
				261);
	}

	// {264}
	public void test0707() {
		check(//
				"Integrate[1/((a + b/x^2)^(3/2)*x^2), x]", //
				"-(1/(a*Sqrt[a + b/x^2]*x))", //
				264);
	}

	// {261}
	public void test0708() {
		check(//
				"Integrate[1/((a + b/x^2)^(5/2)*x^3), x]", //
				"1/(3*b*(a + b/x^2)^(3/2))", //
				261);
	}

	// {264}
	public void test0709() {
		check(//
				"Integrate[1/((a + b/x^2)^(5/2)*x^4), x]", //
				"-1/(3*a*(a + b/x^2)^(3/2)*x^3)", //
				264);
	}

	// {261}
	public void test0710() {
		check(//
				"Integrate[(1 + x^(-2))^(1/3)/x^3, x]", //
				"(-3*(1 + x^(-2))^(4/3))/8", //
				261);
	}

	// {261}
	public void test0711() {
		check(//
				"Integrate[(1 + x^(-2))^(5/3)/x^3, x]", //
				"(-3*(1 + x^(-2))^(8/3))/16", //
				261);
	}

	// {260}
	public void test0712() {
		check(//
				"Integrate[1/((a + b/x^3)*x^4), x]", //
				"-Log[a + b/x^3]/(3*b)", //
				260);
	}

	// {261}
	public void test0713() {
		check(//
				"Integrate[1/((a + b/x^3)^2*x^4), x]", //
				"1/(3*b*(a + b/x^3))", //
				261);
	}

	// {261}
	public void test0714() {
		check(//
				"Integrate[Sqrt[a + b/x^3]/x^4, x]", //
				"(-2*(a + b/x^3)^(3/2))/(9*b)", //
				261);
	}

	// {261}
	public void test0715() {
		check(//
				"Integrate[(a + b/x^3)^(3/2)/x^4, x]", //
				"(-2*(a + b/x^3)^(5/2))/(15*b)", //
				261);
	}

	// {261}
	public void test0716() {
		check(//
				"Integrate[1/(Sqrt[a + b/x^3]*x^4), x]", //
				"(-2*Sqrt[a + b/x^3])/(3*b)", //
				261);
	}

	// {261}
	public void test0717() {
		check(//
				"Integrate[1/((a + b/x^3)^(3/2)*x^4), x]", //
				"2/(3*b*Sqrt[a + b/x^3])", //
				261);
	}

	// {264}
	public void test0718() {
		check(//
				"Integrate[x/Sqrt[a + b/x^4], x]", //
				"(Sqrt[a + b/x^4]*x^2)/(2*a)", //
				264);
	}

	// {264}
	public void test0719() {
		check(//
				"Integrate[1/((a + b/x^4)^(3/2)*x^3), x]", //
				"-1/(2*a*Sqrt[a + b/x^4]*x^2)", //
				264);
	}

	// {264}
	public void test0721() {
		check(//
				"Integrate[(a + b*Sqrt[x])^3/x^3, x]", //
				"-(a + b*Sqrt[x])^4/(2*a*x^2)", //
				264);
	}

	// {264}
	public void test0722() {
		check(//
				"Integrate[(a + b*Sqrt[x])^5/x^4, x]", //
				"-(a + b*Sqrt[x])^6/(3*a*x^3)", //
				264);
	}

	// {264}
	public void test0723() {
		check(//
				"Integrate[(a + b*Sqrt[x])^15/x^9, x]", //
				"-(a + b*Sqrt[x])^16/(8*a*x^8)", //
				264);
	}

	// {264}
	public void test0724() {
		check(//
				"Integrate[x/(a + b*Sqrt[x])^5, x]", //
				"x^2/(2*a*(a + b*Sqrt[x])^4)", //
				264);
	}

	// {261}
	public void test0725() {
		check(//
				"Integrate[(a + b*Sqrt[x])^n/Sqrt[x], x]", //
				"(2*(a + b*Sqrt[x])^(1 + n))/(b*(1 + n))", //
				261);
	}

	// {261}
	public void test0726() {
		check(//
				"Integrate[(1 + Sqrt[x])^2/Sqrt[x], x]", //
				"(2*(1 + Sqrt[x])^3)/3", //
				261);
	}

	// {261}
	public void test0727() {
		check(//
				"Integrate[(1 + Sqrt[x])^3/Sqrt[x], x]", //
				"(1 + Sqrt[x])^4/2", //
				261);
	}

	// {260}
	public void test0728() {
		check(//
				"Integrate[1/((1 + Sqrt[x])*Sqrt[x]), x]", //
				"2*Log[1 + Sqrt[x]]", //
				260);
	}

	// {261}
	public void test0729() {
		check(//
				"Integrate[1/((1 + Sqrt[x])^2*Sqrt[x]), x]", //
				"-2/(1 + Sqrt[x])", //
				261);
	}

	// {261}
	public void test0730() {
		check(//
				"Integrate[1/((1 + Sqrt[x])^3*Sqrt[x]), x]", //
				"-(1 + Sqrt[x])^(-2)", //
				261);
	}

	// {261}
	public void test0731() {
		check(//
				"Integrate[Sqrt[1 + Sqrt[x]]/Sqrt[x], x]", //
				"(4*(1 + Sqrt[x])^(3/2))/3", //
				261);
	}

	// {260}
	public void test0732() {
		check(//
				"Integrate[Sqrt[x]/(1 + x^(3/2)), x]", //
				"(2*Log[1 + x^(3/2)])/3", //
				260);
	}

	// {264}
	public void test0734() {
		check(//
				"Integrate[(a + b*x^(1/3))^2/x^2, x]", //
				"-((a + b*x^(1/3))^3/(a*x))", //
				264);
	}

	// {264}
	public void test0735() {
		check(//
				"Integrate[(a + b*x^(1/3))^5/x^3, x]", //
				"-(a + b*x^(1/3))^6/(2*a*x^2)", //
				264);
	}

	// {260}
	public void test0736() {
		check(//
				"Integrate[1/((1 + x^(2/3))*x^(1/3)), x]", //
				"(3*Log[1 + x^(2/3)])/2", //
				260);
	}

	// {261}
	public void test0737() {
		check(//
				"Integrate[Sqrt[-1 + x^(2/3)]/x^(1/3), x]", //
				"(-1 + x^(2/3))^(3/2)", //
				261);
	}

	// {261}
	public void test0738() {
		check(//
				"Integrate[(1 + x^(2/3))^(3/2)/x^(1/3), x]", //
				"(3*(1 + x^(2/3))^(5/2))/5", //
				261);
	}

	// {261}
	public void test0740() {
		check(//
				"Integrate[x^(2/3)*(1 + x^(5/3))^(2/3), x]", //
				"(9*(1 + x^(5/3))^(5/3))/25", //
				261);
	}

	// {261}
	public void test0741() {
		check(//
				"Integrate[x^(7/3)*(a^(10/3) - x^(10/3))^(19/7), x]", //
				"(-21*(a^(10/3) - x^(10/3))^(26/7))/260", //
				261);
	}

	// {261}
	public void test0742() {
		check(//
				"Integrate[1/(Sqrt[1 + x^(4/5)]*x^(1/5)), x]", //
				"(5*Sqrt[1 + x^(4/5)])/2", //
				261);
	}

	// {191}
	public void test0743() {
		check(//
				"Integrate[(a + b/x^(3/5))^(2/3), x]", //
				"((a + b/x^(3/5))^(5/3)*x)/a", //
				191);
	}

	// {364}
	public void test0745() {
		check(//
				"Integrate[x/(a + b*x^n), x]", //
				"(x^2*Hypergeometric2F1[1, 2/n, (2 + n)/n, -((b*x^n)/a)])/(2*a)", //
				364);
	}

	// {245}
	public void test0746() {
		check(//
				"Integrate[(a + b*x^n)^(-1), x]", //
				"(x*Hypergeometric2F1[1, n^(-1), 1 + n^(-1), -((b*x^n)/a)])/a", //
				245);
	}

	// {364}
	public void test0747() {
		check(//
				"Integrate[1/(x^2*(a + b*x^n)), x]", //
				"-(Hypergeometric2F1[1, -n^(-1), -((1 - n)/n), -((b*x^n)/a)]/(a*x))", //
				364);
	}

	// {364}
	public void test0748() {
		check(//
				"Integrate[1/(x^3*(a + b*x^n)), x]", //
				"-Hypergeometric2F1[1, -2/n, -((2 - n)/n), -((b*x^n)/a)]/(2*a*x^2)", //
				364);
	}

	// {364}
	public void test0749() {
		check(//
				"Integrate[x/(a + b*x^n)^2, x]", //
				"(x^2*Hypergeometric2F1[2, 2/n, (2 + n)/n, -((b*x^n)/a)])/(2*a^2)", //
				364);
	}

	// {245}
	public void test0750() {
		check(//
				"Integrate[(a + b*x^n)^(-2), x]", //
				"(x*Hypergeometric2F1[2, n^(-1), 1 + n^(-1), -((b*x^n)/a)])/a^2", //
				245);
	}

	// {364}
	public void test0751() {
		check(//
				"Integrate[1/(x^2*(a + b*x^n)^2), x]", //
				"-(Hypergeometric2F1[2, -n^(-1), -((1 - n)/n), -((b*x^n)/a)]/(a^2*x))", //
				364);
	}

	// {364}
	public void test0752() {
		check(//
				"Integrate[1/(x^3*(a + b*x^n)^2), x]", //
				"-Hypergeometric2F1[2, -2/n, -((2 - n)/n), -((b*x^n)/a)]/(2*a^2*x^2)", //
				364);
	}

	// {364}
	public void test0753() {
		check(//
				"Integrate[x/(a + b*x^n)^3, x]", //
				"(x^2*Hypergeometric2F1[3, 2/n, (2 + n)/n, -((b*x^n)/a)])/(2*a^3)", //
				364);
	}

	// {245}
	public void test0754() {
		check(//
				"Integrate[(a + b*x^n)^(-3), x]", //
				"(x*Hypergeometric2F1[3, n^(-1), 1 + n^(-1), -((b*x^n)/a)])/a^3", //
				245);
	}

	// {364}
	public void test0755() {
		check(//
				"Integrate[1/(x^2*(a + b*x^n)^3), x]", //
				"-(Hypergeometric2F1[3, -n^(-1), -((1 - n)/n), -((b*x^n)/a)]/(a^3*x))", //
				364);
	}

	// {364}
	public void test0756() {
		check(//
				"Integrate[1/(x^3*(a + b*x^n)^3), x]", //
				"-Hypergeometric2F1[3, -2/n, -((2 - n)/n), -((b*x^n)/a)]/(2*a^3*x^2)", //
				364);
	}

	// {261}
	public void test0757() {
		check(//
				"Integrate[x^(-1 + n)*(a + b*x^n)^2, x]", //
				"(a + b*x^n)^3/(3*b*n)", //
				261);
	}

	// {264}
	public void test0758() {
		check(//
				"Integrate[x^(-1 - 3*n)*(a + b*x^n)^2, x]", //
				"-(a + b*x^n)^3/(3*a*n*x^(3*n))", //
				264);
	}

	// {261}
	public void test0759() {
		check(//
				"Integrate[x^(-1 + n)*(a + b*x^n)^3, x]", //
				"(a + b*x^n)^4/(4*b*n)", //
				261);
	}

	// {264}
	public void test0760() {
		check(//
				"Integrate[x^(-1 - 4*n)*(a + b*x^n)^3, x]", //
				"-(a + b*x^n)^4/(4*a*n*x^(4*n))", //
				264);
	}

	// {261}
	public void test0761() {
		check(//
				"Integrate[x^(-1 + n)*(a + b*x^n)^5, x]", //
				"(a + b*x^n)^6/(6*b*n)", //
				261);
	}

	// {264}
	public void test0762() {
		check(//
				"Integrate[x^(-1 - 6*n)*(a + b*x^n)^5, x]", //
				"-(a + b*x^n)^6/(6*a*n*x^(6*n))", //
				264);
	}

	// {261}
	public void test0763() {
		check(//
				"Integrate[x^(-1 + n)*(a + b*x^n)^8, x]", //
				"(a + b*x^n)^9/(9*b*n)", //
				261);
	}

	// {264}
	public void test0764() {
		check(//
				"Integrate[x^(-1 - 9*n)*(a + b*x^n)^8, x]", //
				"-(a + b*x^n)^9/(9*a*n*x^(9*n))", //
				264);
	}

	// {261}
	public void test0765() {
		check(//
				"Integrate[x^(-1 + n)*(a + b*x^n)^16, x]", //
				"(a + b*x^n)^17/(17*b*n)", //
				261);
	}

	// {261}
	public void test0766() {
		check(//
				"Integrate[x^12*(a + b*x^13)^12, x]", //
				"(a + b*x^13)^13/(169*b)", //
				261);
	}

	// {261}
	public void test0767() {
		check(//
				"Integrate[x^24*(a + b*x^25)^12, x]", //
				"(a + b*x^25)^13/(325*b)", //
				261);
	}

	// {261}
	public void test0768() {
		check(//
				"Integrate[x^36*(a + b*x^37)^12, x]", //
				"(a + b*x^37)^13/(481*b)", //
				261);
	}

	// {261}
	public void test0769() {
		check(//
				"Integrate[x^(12*m)*(a + b*x^(1 + 12*m))^12, x]", //
				"(a + b*x^(1 + 12*m))^13/(13*b*(1 + 12*m))", //
				261);
	}

	// {261}
	public void test0770() {
		check(//
				"Integrate[x^(12 + 12*(-1 + m))*(a + b*x^(1 + 12*m))^12, x]", //
				"(a + b*x^(1 + 12*m))^13/(13*b*(1 + 12*m))", //
				261);
	}

	// {260}
	public void test0771() {
		check(//
				"Integrate[x^(-1 + n)/(a + b*x^n), x]", //
				"Log[a + b*x^n]/(b*n)", //
				260);
	}

	// {260}
	public void test0772() {
		check(//
				"Integrate[x^(-1 + n)/(a + b*x^n), x]", //
				"Log[a + b*x^n]/(b*n)", //
				260);
	}

	// {260}
	public void test0773() {
		check(//
				"Integrate[x^(-1 + n)/(2 + b*x^n), x]", //
				"Log[2 + b*x^n]/(b*n)", //
				260);
	}

	// {261}
	public void test0774() {
		check(//
				"Integrate[x^(-1 + n)/(a + b*x^n)^2, x]", //
				"-(1/(b*n*(a + b*x^n)))", //
				261);
	}

	// {264}
	public void test0775() {
		check(//
				"Integrate[x^(-1 + 2*n)/(a + b*x^n)^3, x]", //
				"x^(2*n)/(2*a*n*(a + b*x^n)^2)", //
				264);
	}

	// {261}
	public void test0776() {
		check(//
				"Integrate[x^(-1 + n)/(a + b*x^n)^3, x]", //
				"-1/(2*b*n*(a + b*x^n)^2)", //
				261);
	}

	// {261}
	public void test0777() {
		check(//
				"Integrate[x^(-1 + n)*Sqrt[a + b*x^n], x]", //
				"(2*(a + b*x^n)^(3/2))/(3*b*n)", //
				261);
	}

	// {261}
	public void test0778() {
		check(//
				"Integrate[x^(-1 + n)/Sqrt[a + b*x^n], x]", //
				"(2*Sqrt[a + b*x^n])/(b*n)", //
				261);
	}

	// {364}
	public void test0779() {
		check(//
				"Integrate[x^m/(a + b*x^n), x]", //
				"(x^(1 + m)*Hypergeometric2F1[1, (1 + m)/n, (1 + m + n)/n, -((b*x^n)/a)])/(a*(1 + m))", //
				364);
	}

	// {364}
	public void test0780() {
		check(//
				"Integrate[x^m/(a + b*x^n)^2, x]", //
				"(x^(1 + m)*Hypergeometric2F1[2, (1 + m)/n, (1 + m + n)/n, -((b*x^n)/a)])/(a^2*(1 + m))", //
				364);
	}

	// {364}
	public void test0781() {
		check(//
				"Integrate[x^m/(a + b*x^n)^3, x]", //
				"(x^(1 + m)*Hypergeometric2F1[3, (1 + m)/n, (1 + m + n)/n, -((b*x^n)/a)])/(a^3*(1 + m))", //
				364);
	}

	// {264}
	public void test0782() {
		check(//
				"Integrate[x^(-1 - n/2)/Sqrt[a + b*x^n], x]", //
				"(-2*Sqrt[a + b*x^n])/(a*n*x^(n/2))", //
				264);
	}

	// {191}
	public void test0783() {
		check(//
				"Integrate[(a + b*x^n)^(-1 - n^(-1)), x]", //
				"x/(a*(a + b*x^n)^n^(-1))", //
				191);
	}

	// {261}
	public void test0784() {
		check(//
				"Integrate[x^(-1 + n)*(a + b*x^n)^p, x]", //
				"(a + b*x^n)^(1 + p)/(b*n*(1 + p))", //
				261);
	}

	// {264}
	public void test0785() {
		check(//
				"Integrate[x^(-1 - n - n*p)*(a + b*x^n)^p, x]", //
				"-((a + b*x^n)^(1 + p)/(a*n*(1 + p)*x^(n*(1 + p))))", //
				264);
	}

	// {264}
	public void test0786() {
		check(//
				"Integrate[x^(-1 - 9*n)*(a + b*x^n)^8, x]", //
				"-(a + b*x^n)^9/(9*a*n*x^(9*n))", //
				264);
	}

	// {264}
	public void test0787() {
		check(//
				"Integrate[x^(-4 - 3*p)*(a + b*x^3)^p, x]", //
				"-(a + b*x^3)^(1 + p)/(3*a*(1 + p)*x^(3*(1 + p)))", //
				264);
	}

	// {264}
	public void test0788() {
		check(//
				"Integrate[(a + b*x^3)^8/x^28, x]", //
				"-(a + b*x^3)^9/(27*a*x^27)", //
				264);
	}

	// {191}
	public void test0789() {
		check(//
				"Integrate[(a + b*x^n)^(-((1 + n)/n)), x]", //
				"x/(a*(a + b*x^n)^n^(-1))", //
				191);
	}

	// {260}
	public void test0790() {
		check(//
				"Integrate[x^m/(a + b*x^(1 + m)), x]", //
				"Log[a + b*x^(1 + m)]/(b*(1 + m))", //
				260);
	}

	// {261}
	public void test0791() {
		check(//
				"Integrate[x^m*(a + b*x^(1 + m))^n, x]", //
				"(a + b*x^(1 + m))^(1 + n)/(b*(1 + m)*(1 + n))", //
				261);
	}

	// {264}
	public void test0792() {
		check(//
				"Integrate[x^m/(a + b*x^(2 + 2*m))^(3/2), x]", //
				"x^(1 + m)/(a*(1 + m)*Sqrt[a + b*x^(2*(1 + m))])", //
				264);
	}

	// {261}
	public void test0793() {
		check(//
				"Integrate[x^n*Sqrt[1 + x^(1 + n)], x]", //
				"(2*(1 + x^(1 + n))^(3/2))/(3*(1 + n))", //
				261);
	}

	// {261}
	public void test0794() {
		check(//
				"Integrate[x^n*Sqrt[a^2 + x^(1 + n)], x]", //
				"(2*(a^2 + x^(1 + n))^(3/2))/(3*(1 + n))", //
				261);
	}

	// {364}
	public void test0795() {
		check(//
				"Integrate[(c*x)^(4 + n)/(a + b*x^n), x]", //
				"((c*x)^(5 + n)*Hypergeometric2F1[1, (5 + n)/n, 2 + 5/n, -((b*x^n)/a)])/(a*c*(5 + n))", //
				364);
	}

	// {364}
	public void test0796() {
		check(//
				"Integrate[(c*x)^(3 + n)/(a + b*x^n), x]", //
				"((c*x)^(4 + n)*Hypergeometric2F1[1, (4 + n)/n, 2*(1 + 2/n), -((b*x^n)/a)])/(a*c*(4 + n))", //
				364);
	}

	// {364}
	public void test0797() {
		check(//
				"Integrate[(c*x)^(2 + n)/(a + b*x^n), x]", //
				"((c*x)^(3 + n)*Hypergeometric2F1[1, (3 + n)/n, 2 + 3/n, -((b*x^n)/a)])/(a*c*(3 + n))", //
				364);
	}

	// {364}
	public void test0798() {
		check(//
				"Integrate[(c*x)^(1 + n)/(a + b*x^n), x]", //
				"((c*x)^(2 + n)*Hypergeometric2F1[1, (2 + n)/n, 2*(1 + n^(-1)), -((b*x^n)/a)])/(a*c*(2 + n))", //
				364);
	}

	// {364}
	public void test0799() {
		check(//
				"Integrate[(c*x)^n/(a + b*x^n), x]", //
				"((c*x)^(1 + n)*Hypergeometric2F1[1, 1 + n^(-1), 2 + n^(-1), -((b*x^n)/a)])/(a*c*(1 + n))", //
				364);
	}

	// {364}
	public void test0800() {
		check(//
				"Integrate[(c*x)^(-2 + n)/(a + b*x^n), x]", //
				"-(((c*x)^(-1 + n)*Hypergeometric2F1[1, -((1 - n)/n), 2 - n^(-1), -((b*x^n)/a)])/(a*c*(1 - n)))", //
				364);
	}

	// {364}
	public void test0801() {
		check(//
				"Integrate[(c*x)^(-3 + n)/(a + b*x^n), x]", //
				"-(((c*x)^(-2 + n)*Hypergeometric2F1[1, -((2 - n)/n), 2*(1 - n^(-1)), -((b*x^n)/a)])/(a*c*(2 - n)))", //
				364);
	}

	// {264}
	public void test0802() {
		check(//
				"Integrate[(c*x)^(-1 + n)/(a + b*x^n)^2, x]", //
				"(c*x)^n/(a*c*n*(a + b*x^n))", //
				264);
	}

	// {264}
	public void test0803() {
		check(//
				"Integrate[(c*x)^(-1 - n/2)/Sqrt[a + b*x^n], x]", //
				"(-2*Sqrt[a + b*x^n])/(a*c*n*(c*x)^(n/2))", //
				264);
	}

	// {264}
	public void test0804() {
		check(//
				"Integrate[(c*x)^(-1 - n - n*p)*(a + b*x^n)^p, x]", //
				"-((a + b*x^n)^(1 + p)/(a*c*n*(1 + p)*(c*x)^(n*(1 + p))))", //
				264);
	}

	// {372, 260}
	public void test0805() {
		check(//
				"Integrate[(2 + x)/(1 + (2 + x)^2), x]", //
				"Log[1 + (2 + x)^2]/2", //
				372, 260);
	}

	// {372, 261}
	public void test0806() {
		check(//
				"Integrate[(2 + x)/(1 + (2 + x)^2)^2, x]", //
				"-1/(2*(1 + (2 + x)^2))", //
				372, 261);
	}

	// {372, 261}
	public void test0807() {
		check(//
				"Integrate[(2 + x)/(1 + (2 + x)^2)^3, x]", //
				"-1/(4*(1 + (2 + x)^2)^2)", //
				372, 261);
	}

	// {372, 261}
	public void test0808() {
		check(//
				"Integrate[(c + d*x)*(a + b*(c + d*x)^2)^p, x]", //
				"(a + b*(c + d*x)^2)^(1 + p)/(2*b*d*(1 + p))", //
				372, 261);
	}

	// {372, 260}
	public void test0809() {
		check(//
				"Integrate[(c + d*x)^2/(a + b*(c + d*x)^3), x]", //
				"Log[a + b*(c + d*x)^3]/(3*b*d)", //
				372, 260);
	}

	// {372, 261}
	public void test0810() {
		check(//
				"Integrate[(c + d*x)^2/(a + b*(c + d*x)^3)^2, x]", //
				"-1/(3*b*d*(a + b*(c + d*x)^3))", //
				372, 261);
	}

	// {372, 261}
	public void test0811() {
		check(//
				"Integrate[(c + d*x)^2/(a + b*(c + d*x)^3)^3, x]", //
				"-1/(6*b*d*(a + b*(c + d*x)^3)^2)", //
				372, 261);
	}

	// {372, 260}
	public void test0812() {
		check(//
				"Integrate[(c*e + d*e*x)^2/(a + b*(c + d*x)^3), x]", //
				"(e^2*Log[a + b*(c + d*x)^3])/(3*b*d)", //
				372, 260);
	}

	// {372, 261}
	public void test0813() {
		check(//
				"Integrate[(c*e + d*e*x)^2/(a + b*(c + d*x)^3)^2, x]", //
				"-e^2/(3*b*d*(a + b*(c + d*x)^3))", //
				372, 261);
	}

	// {372, 261}
	public void test0814() {
		check(//
				"Integrate[(c*e + d*e*x)^2/(a + b*(c + d*x)^3)^3, x]", //
				"-e^2/(6*b*d*(a + b*(c + d*x)^3)^2)", //
				372, 261);
	}

	// {372, 261}
	public void test0815() {
		check(//
				"Integrate[(c + d*x)^3*(a + b*(c + d*x)^4)^p, x]", //
				"(a + b*(c + d*x)^4)^(1 + p)/(4*b*d*(1 + p))", //
				372, 261);
	}

	// {372, 14}
	public void test0816() {
		check(//
				"Integrate[(c + d*x)^3*(a + b*(c + d*x)^4), x]", //
				"(a*(c + d*x)^4)/(4*d) + (b*(c + d*x)^8)/(8*d)", //
				372, 14);
	}

	// {372, 261}
	public void test0817() {
		check(//
				"Integrate[(c + d*x)^3*(a + b*(c + d*x)^4)^2, x]", //
				"(a + b*(c + d*x)^4)^3/(12*b*d)", //
				372, 261);
	}

	// {372, 261}
	public void test0818() {
		check(//
				"Integrate[(c + d*x)^3*(a + b*(c + d*x)^4)^3, x]", //
				"(a + b*(c + d*x)^4)^4/(16*b*d)", //
				372, 261);
	}

	// {372, 260}
	public void test0819() {
		check(//
				"Integrate[(c + d*x)^3/(a + b*(c + d*x)^4), x]", //
				"Log[a + b*(c + d*x)^4]/(4*b*d)", //
				372, 260);
	}

	// {372, 261}
	public void test0820() {
		check(//
				"Integrate[(c + d*x)^3/(a + b*(c + d*x)^4)^2, x]", //
				"-1/(4*b*d*(a + b*(c + d*x)^4))", //
				372, 261);
	}

	// {372, 261}
	public void test0821() {
		check(//
				"Integrate[(c + d*x)^3/(a + b*(c + d*x)^4)^3, x]", //
				"-1/(8*b*d*(a + b*(c + d*x)^4)^2)", //
				372, 261);
	}

	// {191}
	public void test0822() {
		check(//
				"Integrate[(c + d*x^3)^(-4/3), x]", //
				"x/(c*(c + d*x^3)^(1/3))", //
				191);
	}

	// {381}
	public void test0823() {
		check(//
				"Integrate[(a + b*x^3)^(-1 - (b*c)/(3*b*c - 3*a*d))*(c + d*x^3)^(-1 + (a*d)/(3*b*c - 3*a*d)), x]", //
				"(x*(c + d*x^3)^((a*d)/(3*b*c - 3*a*d)))/(a*c*(a + b*x^3)^((b*c)/(3*b*c - 3*a*d)))", //
				381);
	}

	// {405}
	public void test0824() {
		check(//
				"Integrate[Sqrt[a - b*x^4]/(a*c + b*c*x^4), x]", //
				"ArcTan[(b^(1/4)*x*(Sqrt[a] + Sqrt[b]*x^2))/(a^(1/4)*Sqrt[a - b*x^4])]/(2*a^(1/4)*b^(1/4)*c) + ArcTanh[(b^(1/4)*x*(Sqrt[a] - Sqrt[b]*x^2))/(a^(1/4)*Sqrt[a - b*x^4])]/(2*a^(1/4)*b^(1/4)*c)", //
				405);
	}

	// {380}
	public void test0825() {
		check(//
				"Integrate[(a + b*x^n)^p*(c + d*x^n)^(-1 - n^(-1) - p), x]", //
				"(x*(a + b*x^n)^p*(c + d*x^n)^(-n^(-1) - p)*Hypergeometric2F1[n^(-1), -p, 1 + n^(-1), -(((b*c - a*d)*x^n)/(a*(c + d*x^n)))])/(c*((c*(a + b*x^n))/(a*(c + d*x^n)))^p)", //
				380);
	}

	// {191}
	public void test0826() {
		check(//
				"Integrate[(c + d*x^n)^(-1 - n^(-1)), x]", //
				"x/(c*(c + d*x^n)^n^(-1))", //
				191);
	}

	// {379}
	public void test0827() {
		check(//
				"Integrate[1/((a + b*x^n)*(c + d*x^n)^n^(-1)), x]", //
				"(x*Hypergeometric2F1[1, n^(-1), 1 + n^(-1), -(((b*c - a*d)*x^n)/(a*(c + d*x^n)))])/(a*(c + d*x^n)^n^(-1))", //
				379);
	}

	// {379}
	public void test0828() {
		check(//
				"Integrate[(c + d*x^n)^(1 - n^(-1))/(a + b*x^n)^2, x]", //
				"(c*x*Hypergeometric2F1[2, n^(-1), 1 + n^(-1), -(((b*c - a*d)*x^n)/(a*(c + d*x^n)))])/(a^2*(c + d*x^n)^n^(-1))", //
				379);
	}

	// {379}
	public void test0829() {
		check(//
				"Integrate[(c + d*x^n)^(2 - n^(-1))/(a + b*x^n)^3, x]", //
				"(c^2*x*Hypergeometric2F1[3, n^(-1), 1 + n^(-1), -(((b*c - a*d)*x^n)/(a*(c + d*x^n)))])/(a^3*(c + d*x^n)^n^(-1))", //
				379);
	}

	// {381}
	public void test0830() {
		check(//
				"Integrate[(a + b*x^n)^((a*d*n - b*c*(1 + n))/((b*c - a*d)*n))*(c + d*x^n)^((a*d - b*c*n + a*d*n)/(b*c*n - a*d*n)), x]", //
				"(x*(c + d*x^n)^((a*d)/((b*c - a*d)*n)))/(a*c*(a + b*x^n)^((b*c)/((b*c - a*d)*n)))", //
				381);
	}

	// {450}
	public void test0831() {
		check(//
				"Integrate[(c + d*x^2)/(x^((2*b^2*c + a^2*d)/(b^2*c + a^2*d))*Sqrt[-a + b*x]*Sqrt[a + b*x]), x]", //
				"((c/a^2 + d/b^2)*Sqrt[-a + b*x]*Sqrt[a + b*x])/x^((b^2*c)/(b^2*c + a^2*d))", //
				450);
	}

	// {484}
	public void test0832() {
		check(//
				"Integrate[x/(Sqrt[c + d*x^3]*(4*c + d*x^3)), x]", //
				"-ArcTan[(Sqrt[3]*c^(1/6)*(c^(1/3) + 2^(1/3)*d^(1/3)*x))/Sqrt[c + d*x^3]]/(3*2^(2/3)*Sqrt[3]*c^(5/6)*d^(2/3)) + ArcTan[Sqrt[c + d*x^3]/(Sqrt[3]*Sqrt[c])]/(3*2^(2/3)*Sqrt[3]*c^(5/6)*d^(2/3)) - ArcTanh[(c^(1/6)*(c^(1/3) - 2^(1/3)*d^(1/3)*x))/Sqrt[c + d*x^3]]/(3*2^(2/3)*c^(5/6)*d^(2/3)) + ArcTanh[Sqrt[c + d*x^3]/Sqrt[c]]/(9*2^(2/3)*c^(5/6)*d^(2/3))", //
				484);
	}

	// {484}
	public void test0833() {
		check(//
				"Integrate[x/(Sqrt[1 - x^3]*(4 - x^3)), x]", //
				"-ArcTan[(Sqrt[3]*(1 - 2^(1/3)*x))/Sqrt[1 - x^3]]/(3*2^(2/3)*Sqrt[3]) + ArcTan[Sqrt[1 - x^3]/Sqrt[3]]/(3*2^(2/3)*Sqrt[3]) - ArcTanh[(1 + 2^(1/3)*x)/Sqrt[1 - x^3]]/(3*2^(2/3)) + ArcTanh[Sqrt[1 - x^3]]/(9*2^(2/3))", //
				484);
	}

	// {487}
	public void test0834() {
		check(//
				"Integrate[x/(Sqrt[a + b*x^3]*(2*(5 + 3*Sqrt[3])*a + b*x^3)), x]", //
				"-((2 - Sqrt[3])*ArcTan[(3^(1/4)*(1 + Sqrt[3])*a^(1/6)*(a^(1/3) + b^(1/3)*x))/(Sqrt[2]*Sqrt[a + b*x^3])])/(2*Sqrt[2]*3^(3/4)*a^(5/6)*b^(2/3)) - ((2 - Sqrt[3])*ArcTan[((1 - Sqrt[3])*Sqrt[a + b*x^3])/(Sqrt[2]*3^(3/4)*Sqrt[a])])/(3*Sqrt[2]*3^(3/4)*a^(5/6)*b^(2/3)) - ((2 - Sqrt[3])*ArcTanh[(3^(1/4)*a^(1/6)*((1 + Sqrt[3])*a^(1/3) - 2*b^(1/3)*x))/(Sqrt[2]*Sqrt[a + b*x^3])])/(3*Sqrt[2]*3^(1/4)*a^(5/6)*b^(2/3)) - ((2 - Sqrt[3])*ArcTanh[(3^(1/4)*(1 - Sqrt[3])*a^(1/6)*(a^(1/3) + b^(1/3)*x))/(Sqrt[2]*Sqrt[a + b*x^3])])/(6*Sqrt[2]*3^(1/4)*a^(5/6)*b^(2/3))", //
				487);
	}

	// {487}
	public void test0835() {
		check(//
				"Integrate[x/(Sqrt[a - b*x^3]*(2*(5 + 3*Sqrt[3])*a - b*x^3)), x]", //
				"-((2 - Sqrt[3])*ArcTan[(3^(1/4)*(1 + Sqrt[3])*a^(1/6)*(a^(1/3) - b^(1/3)*x))/(Sqrt[2]*Sqrt[a - b*x^3])])/(2*Sqrt[2]*3^(3/4)*a^(5/6)*b^(2/3)) - ((2 - Sqrt[3])*ArcTan[((1 - Sqrt[3])*Sqrt[a - b*x^3])/(Sqrt[2]*3^(3/4)*Sqrt[a])])/(3*Sqrt[2]*3^(3/4)*a^(5/6)*b^(2/3)) - ((2 - Sqrt[3])*ArcTanh[(3^(1/4)*(1 - Sqrt[3])*a^(1/6)*(a^(1/3) - b^(1/3)*x))/(Sqrt[2]*Sqrt[a - b*x^3])])/(6*Sqrt[2]*3^(1/4)*a^(5/6)*b^(2/3)) - ((2 - Sqrt[3])*ArcTanh[(3^(1/4)*a^(1/6)*((1 + Sqrt[3])*a^(1/3) + 2*b^(1/3)*x))/(Sqrt[2]*Sqrt[a - b*x^3])])/(3*Sqrt[2]*3^(1/4)*a^(5/6)*b^(2/3))", //
				487);
	}

	// {488}
	public void test0836() {
		check(//
				"Integrate[x/(Sqrt[-a + b*x^3]*(-2*(5 + 3*Sqrt[3])*a + b*x^3)), x]", //
				"((2 - Sqrt[3])*ArcTan[(3^(1/4)*(1 - Sqrt[3])*a^(1/6)*(a^(1/3) - b^(1/3)*x))/(Sqrt[2]*Sqrt[-a + b*x^3])])/(6*Sqrt[2]*3^(1/4)*a^(5/6)*b^(2/3)) + ((2 - Sqrt[3])*ArcTan[(3^(1/4)*a^(1/6)*((1 + Sqrt[3])*a^(1/3) + 2*b^(1/3)*x))/(Sqrt[2]*Sqrt[-a + b*x^3])])/(3*Sqrt[2]*3^(1/4)*a^(5/6)*b^(2/3)) + ((2 - Sqrt[3])*ArcTanh[(3^(1/4)*(1 + Sqrt[3])*a^(1/6)*(a^(1/3) - b^(1/3)*x))/(Sqrt[2]*Sqrt[-a + b*x^3])])/(2*Sqrt[2]*3^(3/4)*a^(5/6)*b^(2/3)) - ((2 - Sqrt[3])*ArcTanh[((1 - Sqrt[3])*Sqrt[-a + b*x^3])/(Sqrt[2]*3^(3/4)*Sqrt[a])])/(3*Sqrt[2]*3^(3/4)*a^(5/6)*b^(2/3))", //
				488);
	}

	// {488}
	public void test0837() {
		check(//
				"Integrate[x/(Sqrt[-a - b*x^3]*(-2*(5 + 3*Sqrt[3])*a - b*x^3)), x]", //
				"((2 - Sqrt[3])*ArcTan[(3^(1/4)*a^(1/6)*((1 + Sqrt[3])*a^(1/3) - 2*b^(1/3)*x))/(Sqrt[2]*Sqrt[-a - b*x^3])])/(3*Sqrt[2]*3^(1/4)*a^(5/6)*b^(2/3)) + ((2 - Sqrt[3])*ArcTan[(3^(1/4)*(1 - Sqrt[3])*a^(1/6)*(a^(1/3) + b^(1/3)*x))/(Sqrt[2]*Sqrt[-a - b*x^3])])/(6*Sqrt[2]*3^(1/4)*a^(5/6)*b^(2/3)) + ((2 - Sqrt[3])*ArcTanh[(3^(1/4)*(1 + Sqrt[3])*a^(1/6)*(a^(1/3) + b^(1/3)*x))/(Sqrt[2]*Sqrt[-a - b*x^3])])/(2*Sqrt[2]*3^(3/4)*a^(5/6)*b^(2/3)) - ((2 - Sqrt[3])*ArcTanh[((1 - Sqrt[3])*Sqrt[-a - b*x^3])/(Sqrt[2]*3^(3/4)*Sqrt[a])])/(3*Sqrt[2]*3^(3/4)*a^(5/6)*b^(2/3))", //
				488);
	}

	// {487}
	public void test0838() {
		check(//
				"Integrate[x/(Sqrt[a + b*x^3]*(2*(5 - 3*Sqrt[3])*a + b*x^3)), x]", //
				"-((2 + Sqrt[3])*ArcTan[(3^(1/4)*a^(1/6)*((1 - Sqrt[3])*a^(1/3) - 2*b^(1/3)*x))/(Sqrt[2]*Sqrt[a + b*x^3])])/(3*Sqrt[2]*3^(1/4)*a^(5/6)*b^(2/3)) - ((2 + Sqrt[3])*ArcTan[(3^(1/4)*(1 + Sqrt[3])*a^(1/6)*(a^(1/3) + b^(1/3)*x))/(Sqrt[2]*Sqrt[a + b*x^3])])/(6*Sqrt[2]*3^(1/4)*a^(5/6)*b^(2/3)) + ((2 + Sqrt[3])*ArcTanh[(3^(1/4)*(1 - Sqrt[3])*a^(1/6)*(a^(1/3) + b^(1/3)*x))/(Sqrt[2]*Sqrt[a + b*x^3])])/(2*Sqrt[2]*3^(3/4)*a^(5/6)*b^(2/3)) + ((2 + Sqrt[3])*ArcTanh[((1 + Sqrt[3])*Sqrt[a + b*x^3])/(Sqrt[2]*3^(3/4)*Sqrt[a])])/(3*Sqrt[2]*3^(3/4)*a^(5/6)*b^(2/3))", //
				487);
	}

	// {487}
	public void test0839() {
		check(//
				"Integrate[x/(Sqrt[a - b*x^3]*(2*(5 - 3*Sqrt[3])*a - b*x^3)), x]", //
				"-((2 + Sqrt[3])*ArcTan[(3^(1/4)*(1 + Sqrt[3])*a^(1/6)*(a^(1/3) - b^(1/3)*x))/(Sqrt[2]*Sqrt[a - b*x^3])])/(6*Sqrt[2]*3^(1/4)*a^(5/6)*b^(2/3)) - ((2 + Sqrt[3])*ArcTan[(3^(1/4)*a^(1/6)*((1 - Sqrt[3])*a^(1/3) + 2*b^(1/3)*x))/(Sqrt[2]*Sqrt[a - b*x^3])])/(3*Sqrt[2]*3^(1/4)*a^(5/6)*b^(2/3)) + ((2 + Sqrt[3])*ArcTanh[(3^(1/4)*(1 - Sqrt[3])*a^(1/6)*(a^(1/3) - b^(1/3)*x))/(Sqrt[2]*Sqrt[a - b*x^3])])/(2*Sqrt[2]*3^(3/4)*a^(5/6)*b^(2/3)) + ((2 + Sqrt[3])*ArcTanh[((1 + Sqrt[3])*Sqrt[a - b*x^3])/(Sqrt[2]*3^(3/4)*Sqrt[a])])/(3*Sqrt[2]*3^(3/4)*a^(5/6)*b^(2/3))", //
				487);
	}

	// {488}
	public void test0840() {
		check(//
				"Integrate[x/((2*(5 - 3*Sqrt[3])*a - b*x^3)*Sqrt[-a + b*x^3]), x]", //
				"((2 + Sqrt[3])*ArcTan[(3^(1/4)*(1 - Sqrt[3])*a^(1/6)*(a^(1/3) - b^(1/3)*x))/(Sqrt[2]*Sqrt[-a + b*x^3])])/(2*Sqrt[2]*3^(3/4)*a^(5/6)*b^(2/3)) - ((2 + Sqrt[3])*ArcTan[((1 + Sqrt[3])*Sqrt[-a + b*x^3])/(Sqrt[2]*3^(3/4)*Sqrt[a])])/(3*Sqrt[2]*3^(3/4)*a^(5/6)*b^(2/3)) - ((2 + Sqrt[3])*ArcTanh[(3^(1/4)*(1 + Sqrt[3])*a^(1/6)*(a^(1/3) - b^(1/3)*x))/(Sqrt[2]*Sqrt[-a + b*x^3])])/(6*Sqrt[2]*3^(1/4)*a^(5/6)*b^(2/3)) - ((2 + Sqrt[3])*ArcTanh[(3^(1/4)*a^(1/6)*((1 - Sqrt[3])*a^(1/3) + 2*b^(1/3)*x))/(Sqrt[2]*Sqrt[-a + b*x^3])])/(3*Sqrt[2]*3^(1/4)*a^(5/6)*b^(2/3))", //
				488);
	}

	// {488}
	public void test0841() {
		check(//
				"Integrate[x/(Sqrt[-a - b*x^3]*(2*(5 - 3*Sqrt[3])*a + b*x^3)), x]", //
				"((2 + Sqrt[3])*ArcTan[(3^(1/4)*(1 - Sqrt[3])*a^(1/6)*(a^(1/3) + b^(1/3)*x))/(Sqrt[2]*Sqrt[-a - b*x^3])])/(2*Sqrt[2]*3^(3/4)*a^(5/6)*b^(2/3)) - ((2 + Sqrt[3])*ArcTan[((1 + Sqrt[3])*Sqrt[-a - b*x^3])/(Sqrt[2]*3^(3/4)*Sqrt[a])])/(3*Sqrt[2]*3^(3/4)*a^(5/6)*b^(2/3)) - ((2 + Sqrt[3])*ArcTanh[(3^(1/4)*a^(1/6)*((1 - Sqrt[3])*a^(1/3) - 2*b^(1/3)*x))/(Sqrt[2]*Sqrt[-a - b*x^3])])/(3*Sqrt[2]*3^(1/4)*a^(5/6)*b^(2/3)) - ((2 + Sqrt[3])*ArcTanh[(3^(1/4)*(1 + Sqrt[3])*a^(1/6)*(a^(1/3) + b^(1/3)*x))/(Sqrt[2]*Sqrt[-a - b*x^3])])/(6*Sqrt[2]*3^(1/4)*a^(5/6)*b^(2/3))", //
				488);
	}

	// {510}
	public void test0842() {
		check(//
				"Integrate[x^4/((1 - x^3)^(1/3)*(1 + x^3)), x]", //
				"(x^5*AppellF1[5/3, 1/3, 1, 8/3, x^3, -x^3])/5", //
				510);
	}

	// {510}
	public void test0843() {
		check(//
				"Integrate[x/((1 - x^3)^(1/3)*(1 + x^3)), x]", //
				"(x^2*AppellF1[2/3, 1/3, 1, 5/3, x^3, -x^3])/2", //
				510);
	}

	// {510}
	public void test0844() {
		check(//
				"Integrate[1/(x^2*(1 - x^3)^(1/3)*(1 + x^3)), x]", //
				"-(AppellF1[-1/3, 1/3, 1, 2/3, x^3, -x^3]/x)", //
				510);
	}

	// {510}
	public void test0845() {
		check(//
				"Integrate[1/(x^5*(1 - x^3)^(1/3)*(1 + x^3)), x]", //
				"-AppellF1[-4/3, 1/3, 1, -1/3, x^3, -x^3]/(4*x^4)", //
				510);
	}

	// {510}
	public void test0846() {
		check(//
				"Integrate[x^6/((1 - x^3)^(2/3)*(1 + x^3)), x]", //
				"(x^7*AppellF1[7/3, 2/3, 1, 10/3, x^3, -x^3])/7", //
				510);
	}

	// {510}
	public void test0847() {
		check(//
				"Integrate[x^3/((1 - x^3)^(2/3)*(1 + x^3)), x]", //
				"(x^4*AppellF1[4/3, 2/3, 1, 7/3, x^3, -x^3])/4", //
				510);
	}

	// {429}
	public void test0848() {
		check(//
				"Integrate[1/((1 - x^3)^(2/3)*(1 + x^3)), x]", //
				"x*AppellF1[1/3, 2/3, 1, 4/3, x^3, -x^3]", //
				429);
	}

	// {510}
	public void test0849() {
		check(//
				"Integrate[1/(x^3*(1 - x^3)^(2/3)*(1 + x^3)), x]", //
				"-AppellF1[-2/3, 2/3, 1, 1/3, x^3, -x^3]/(2*x^2)", //
				510);
	}

	// {265}
	public void test0850() {
		check(//
				"Integrate[(Sqrt[-1 + Sqrt[x]]*Sqrt[1 + Sqrt[x]])/x^(5/2), x]", //
				"(2*(-1 + Sqrt[x])^(3/2)*(1 + Sqrt[x])^(3/2))/(3*x^(3/2))", //
				265);
	}

	// {265}
	public void test0851() {
		check(//
				"Integrate[1/(Sqrt[-1 + Sqrt[x]]*Sqrt[1 + Sqrt[x]]*x^(3/2)), x]", //
				"(2*Sqrt[-1 + Sqrt[x]]*Sqrt[1 + Sqrt[x]])/Sqrt[x]", //
				265);
	}

	// {449}
	public void test0852() {
		check(//
				"Integrate[(e*x)^m*(a + b*x^n)^p*(a*(1 + m) + b*(1 + m + n + n*p)*x^n), x]", //
				"((e*x)^(1 + m)*(a + b*x^n)^(1 + p))/e", //
				449);
	}

	// {74}
	public void test0853() {
		check(//
				"Integrate[x^13*(b + c*x)^13*(b + 2*c*x), x]", //
				"(x^14*(b + c*x)^14)/14", //
				74);
	}

	// {446, 74}
	public void test0854() {
		check(//
				"Integrate[x^27*(b + c*x^2)^13*(b + 2*c*x^2), x]", //
				"(x^28*(b + c*x^2)^14)/28", //
				446, 74);
	}

	// {446, 74}
	public void test0855() {
		check(//
				"Integrate[x^41*(b + c*x^3)^13*(b + 2*c*x^3), x]", //
				"(x^42*(b + c*x^3)^14)/42", //
				446, 74);
	}

	// {449}
	public void test0856() {
		check(//
				"Integrate[x^(-1 + m)*(a + b*x^n)^(-1 + p)*(a*m + b*(m + n*p)*x^n), x]", //
				"x^m*(a + b*x^n)^p", //
				449);
	}

	// {74}
	public void test0857() {
		check(//
				"Integrate[(b + 2*c*x)/(x^8*(b + c*x)^8), x]", //
				"-1/(7*x^7*(b + c*x)^7)", //
				74);
	}

	// {446, 74}
	public void test0858() {
		check(//
				"Integrate[(b + 2*c*x^2)/(x^15*(b + c*x^2)^8), x]", //
				"-1/(14*x^14*(b + c*x^2)^7)", //
				446, 74);
	}

	// {446, 74}
	public void test0859() {
		check(//
				"Integrate[(b + 2*c*x^3)/(x^22*(b + c*x^3)^8), x]", //
				"-1/(21*x^21*(b + c*x^3)^7)", //
				446, 74);
	}

	// {74}
	public void test0860() {
		check(//
				"Integrate[x^p*(b + c*x)^p*(b + 2*c*x), x]", //
				"(x^(1 + p)*(b + c*x)^(1 + p))/(1 + p)", //
				74);
	}

	// {449}
	public void test0861() {
		check(//
				"Integrate[x^(-1 + 2*(1 + p))*(b + c*x^2)^p*(b + 2*c*x^2), x]", //
				"(x^(2*(1 + p))*(b + c*x^2)^(1 + p))/(2*(1 + p))", //
				449);
	}

	// {449}
	public void test0862() {
		check(//
				"Integrate[x^(-1 + 3*(1 + p))*(b + c*x^3)^p*(b + 2*c*x^3), x]", //
				"(x^(3*(1 + p))*(b + c*x^3)^(1 + p))/(3*(1 + p))", //
				449);
	}

	// {449}
	public void test0863() {
		check(//
				"Integrate[x^(-1 + n*(1 + p))*(b + c*x^n)^p*(b + 2*c*x^n), x]", //
				"(x^(n*(1 + p))*(b + c*x^n)^(1 + p))/(n*(1 + p))", //
				449);
	}

	// {1879}
	public void test0864() {
		check(//
				"Integrate[(1 + Sqrt[3] - x)/Sqrt[-1 + x^3], x]", //
				"(2*Sqrt[-1 + x^3])/(1 - Sqrt[3] - x) - (3^(1/4)*Sqrt[2 + Sqrt[3]]*(1 - x)*Sqrt[(1 + x + x^2)/(1 - Sqrt[3] - x)^2]*EllipticE[ArcSin[(1 + Sqrt[3] - x)/(1 - Sqrt[3] - x)], -7 + 4*Sqrt[3]])/(Sqrt[-((1 - x)/(1 - Sqrt[3] - x)^2)]*Sqrt[-1 + x^3])", //
				1879);
	}

	// {1879}
	public void test0865() {
		check(//
				"Integrate[(1 + Sqrt[3] + x)/Sqrt[-1 - x^3], x]", //
				"(-2*Sqrt[-1 - x^3])/(1 - Sqrt[3] + x) + (3^(1/4)*Sqrt[2 + Sqrt[3]]*(1 + x)*Sqrt[(1 - x + x^2)/(1 - Sqrt[3] + x)^2]*EllipticE[ArcSin[(1 + Sqrt[3] + x)/(1 - Sqrt[3] + x)], -7 + 4*Sqrt[3]])/(Sqrt[-((1 + x)/(1 - Sqrt[3] + x)^2)]*Sqrt[-1 - x^3])", //
				1879);
	}

	// {1879}
	public void test0866() {
		check(//
				"Integrate[((1 + Sqrt[3])*a^(1/3) - b^(1/3)*x)/Sqrt[-a + b*x^3], x]", //
				"(2*Sqrt[-a + b*x^3])/(b^(1/3)*((1 - Sqrt[3])*a^(1/3) - b^(1/3)*x)) - (3^(1/4)*Sqrt[2 + Sqrt[3]]*a^(1/3)*(a^(1/3) - b^(1/3)*x)*Sqrt[(a^(2/3) + a^(1/3)*b^(1/3)*x + b^(2/3)*x^2)/((1 - Sqrt[3])*a^(1/3) - b^(1/3)*x)^2]*EllipticE[ArcSin[((1 + Sqrt[3])*a^(1/3) - b^(1/3)*x)/((1 - Sqrt[3])*a^(1/3) - b^(1/3)*x)], -7 + 4*Sqrt[3]])/(b^(1/3)*Sqrt[-((a^(1/3)*(a^(1/3) - b^(1/3)*x))/((1 - Sqrt[3])*a^(1/3) - b^(1/3)*x)^2)]*Sqrt[-a + b*x^3])", //
				1879);
	}

	// {1879}
	public void test0867() {
		check(//
				"Integrate[((1 + Sqrt[3])*a^(1/3) + b^(1/3)*x)/Sqrt[-a - b*x^3], x]", //
				"(-2*Sqrt[-a - b*x^3])/(b^(1/3)*((1 - Sqrt[3])*a^(1/3) + b^(1/3)*x)) + (3^(1/4)*Sqrt[2 + Sqrt[3]]*a^(1/3)*(a^(1/3) + b^(1/3)*x)*Sqrt[(a^(2/3) - a^(1/3)*b^(1/3)*x + b^(2/3)*x^2)/((1 - Sqrt[3])*a^(1/3) + b^(1/3)*x)^2]*EllipticE[ArcSin[((1 + Sqrt[3])*a^(1/3) + b^(1/3)*x)/((1 - Sqrt[3])*a^(1/3) + b^(1/3)*x)], -7 + 4*Sqrt[3]])/(b^(1/3)*Sqrt[-((a^(1/3)*(a^(1/3) + b^(1/3)*x))/((1 - Sqrt[3])*a^(1/3) + b^(1/3)*x)^2)]*Sqrt[-a - b*x^3])", //
				1879);
	}

	// {1879}
	public void test0868() {
		check(//
				"Integrate[(1 + Sqrt[3] - (b/a)^(1/3)*x)/Sqrt[-a + b*x^3], x]", //
				"(2*(b/a)^(2/3)*Sqrt[-a + b*x^3])/(b*(1 - Sqrt[3] - (b/a)^(1/3)*x)) - (3^(1/4)*Sqrt[2 + Sqrt[3]]*(1 - (b/a)^(1/3)*x)*Sqrt[(1 + (b/a)^(1/3)*x + (b/a)^(2/3)*x^2)/(1 - Sqrt[3] - (b/a)^(1/3)*x)^2]*EllipticE[ArcSin[(1 + Sqrt[3] - (b/a)^(1/3)*x)/(1 - Sqrt[3] - (b/a)^(1/3)*x)], -7 + 4*Sqrt[3]])/((b/a)^(1/3)*Sqrt[-((1 - (b/a)^(1/3)*x)/(1 - Sqrt[3] - (b/a)^(1/3)*x)^2)]*Sqrt[-a + b*x^3])", //
				1879);
	}

	// {1879}
	public void test0869() {
		check(//
				"Integrate[(1 + Sqrt[3] + (b/a)^(1/3)*x)/Sqrt[-a - b*x^3], x]", //
				"(-2*(b/a)^(2/3)*Sqrt[-a - b*x^3])/(b*(1 - Sqrt[3] + (b/a)^(1/3)*x)) + (3^(1/4)*Sqrt[2 + Sqrt[3]]*(1 + (b/a)^(1/3)*x)*Sqrt[(1 - (b/a)^(1/3)*x + (b/a)^(2/3)*x^2)/(1 - Sqrt[3] + (b/a)^(1/3)*x)^2]*EllipticE[ArcSin[(1 + Sqrt[3] + (b/a)^(1/3)*x)/(1 - Sqrt[3] + (b/a)^(1/3)*x)], -7 + 4*Sqrt[3]])/((b/a)^(1/3)*Sqrt[-((1 + (b/a)^(1/3)*x)/(1 - Sqrt[3] + (b/a)^(1/3)*x)^2)]*Sqrt[-a - b*x^3])", //
				1879);
	}

//	// {1877}
//	public void test0870() {
//		check(//
//				"Integrate[(1 - Sqrt[3] + x)/Sqrt[1 + x^3], x]", //
//				"(2*Sqrt[1 + x^3])/(1 + Sqrt[3] + x) - (3^(1/4)*Sqrt[2 - Sqrt[3]]*(1 + x)*Sqrt[(1 - x + x^2)/(1 + Sqrt[3] + x)^2]*EllipticE[ArcSin[(1 - Sqrt[3] + x)/(1 + Sqrt[3] + x)], -7 - 4*Sqrt[3]])/(Sqrt[(1 + x)/(1 + Sqrt[3] + x)^2]*Sqrt[1 + x^3])", //
//				1877);
//	}
//
//	// {1877}
//	public void test0871() {
//		check(//
//				"Integrate[(1 - Sqrt[3] - x)/Sqrt[1 - x^3], x]", //
//				"(-2*Sqrt[1 - x^3])/(1 + Sqrt[3] - x) + (3^(1/4)*Sqrt[2 - Sqrt[3]]*(1 - x)*Sqrt[(1 + x + x^2)/(1 + Sqrt[3] - x)^2]*EllipticE[ArcSin[(1 - Sqrt[3] - x)/(1 + Sqrt[3] - x)], -7 - 4*Sqrt[3]])/(Sqrt[(1 - x)/(1 + Sqrt[3] - x)^2]*Sqrt[1 - x^3])", //
//				1877);
//	}
//
//	// {1877}
//	public void test0872() {
//		check(//
//				"Integrate[(-1 + Sqrt[3] - x)/Sqrt[1 + x^3], x]", //
//				"(-2*Sqrt[1 + x^3])/(1 + Sqrt[3] + x) + (3^(1/4)*Sqrt[2 - Sqrt[3]]*(1 + x)*Sqrt[(1 - x + x^2)/(1 + Sqrt[3] + x)^2]*EllipticE[ArcSin[(1 - Sqrt[3] + x)/(1 + Sqrt[3] + x)], -7 - 4*Sqrt[3]])/(Sqrt[(1 + x)/(1 + Sqrt[3] + x)^2]*Sqrt[1 + x^3])", //
//				1877);
//	}
//
//	// {1877}
//	public void test0873() {
//		check(//
//				"Integrate[(-1 + Sqrt[3] + x)/Sqrt[1 - x^3], x]", //
//				"(2*Sqrt[1 - x^3])/(1 + Sqrt[3] - x) - (3^(1/4)*Sqrt[2 - Sqrt[3]]*(1 - x)*Sqrt[(1 + x + x^2)/(1 + Sqrt[3] - x)^2]*EllipticE[ArcSin[(1 - Sqrt[3] - x)/(1 + Sqrt[3] - x)], -7 - 4*Sqrt[3]])/(Sqrt[(1 - x)/(1 + Sqrt[3] - x)^2]*Sqrt[1 - x^3])", //
//				1877);
//	}
//
//	// {1877}
//	public void test0874() {
//		check(//
//				"Integrate[((1 - Sqrt[3])*a^(1/3) + b^(1/3)*x)/Sqrt[a + b*x^3], x]", //
//				"(2*Sqrt[a + b*x^3])/(b^(1/3)*((1 + Sqrt[3])*a^(1/3) + b^(1/3)*x)) - (3^(1/4)*Sqrt[2 - Sqrt[3]]*a^(1/3)*(a^(1/3) + b^(1/3)*x)*Sqrt[(a^(2/3) - a^(1/3)*b^(1/3)*x + b^(2/3)*x^2)/((1 + Sqrt[3])*a^(1/3) + b^(1/3)*x)^2]*EllipticE[ArcSin[((1 - Sqrt[3])*a^(1/3) + b^(1/3)*x)/((1 + Sqrt[3])*a^(1/3) + b^(1/3)*x)], -7 - 4*Sqrt[3]])/(b^(1/3)*Sqrt[(a^(1/3)*(a^(1/3) + b^(1/3)*x))/((1 + Sqrt[3])*a^(1/3) + b^(1/3)*x)^2]*Sqrt[a + b*x^3])", //
//				1877);
//	}
//
//	// {1877}
//	public void test0875() {
//		check(//
//				"Integrate[((1 - Sqrt[3])*a^(1/3) - b^(1/3)*x)/Sqrt[a - b*x^3], x]", //
//				"(-2*Sqrt[a - b*x^3])/(b^(1/3)*((1 + Sqrt[3])*a^(1/3) - b^(1/3)*x)) + (3^(1/4)*Sqrt[2 - Sqrt[3]]*a^(1/3)*(a^(1/3) - b^(1/3)*x)*Sqrt[(a^(2/3) + a^(1/3)*b^(1/3)*x + b^(2/3)*x^2)/((1 + Sqrt[3])*a^(1/3) - b^(1/3)*x)^2]*EllipticE[ArcSin[((1 - Sqrt[3])*a^(1/3) - b^(1/3)*x)/((1 + Sqrt[3])*a^(1/3) - b^(1/3)*x)], -7 - 4*Sqrt[3]])/(b^(1/3)*Sqrt[(a^(1/3)*(a^(1/3) - b^(1/3)*x))/((1 + Sqrt[3])*a^(1/3) - b^(1/3)*x)^2]*Sqrt[a - b*x^3])", //
//				1877);
//	}
//
//	// {1877}
//	public void test0876() {
//		check(//
//				"Integrate[(1 - Sqrt[3] + (b/a)^(1/3)*x)/Sqrt[a + b*x^3], x]", //
//				"(2*(b/a)^(2/3)*Sqrt[a + b*x^3])/(b*(1 + Sqrt[3] + (b/a)^(1/3)*x)) - (3^(1/4)*Sqrt[2 - Sqrt[3]]*(1 + (b/a)^(1/3)*x)*Sqrt[(1 - (b/a)^(1/3)*x + (b/a)^(2/3)*x^2)/(1 + Sqrt[3] + (b/a)^(1/3)*x)^2]*EllipticE[ArcSin[(1 - Sqrt[3] + (b/a)^(1/3)*x)/(1 + Sqrt[3] + (b/a)^(1/3)*x)], -7 - 4*Sqrt[3]])/((b/a)^(1/3)*Sqrt[(1 + (b/a)^(1/3)*x)/(1 + Sqrt[3] + (b/a)^(1/3)*x)^2]*Sqrt[a + b*x^3])", //
//				1877);
//	}
//
//	// {1877}
//	public void test0877() {
//		check(//
//				"Integrate[(1 - Sqrt[3] - (b/a)^(1/3)*x)/Sqrt[a - b*x^3], x]", //
//				"(-2*(b/a)^(2/3)*Sqrt[a - b*x^3])/(b*(1 + Sqrt[3] - (b/a)^(1/3)*x)) + (3^(1/4)*Sqrt[2 - Sqrt[3]]*(1 - (b/a)^(1/3)*x)*Sqrt[(1 + (b/a)^(1/3)*x + (b/a)^(2/3)*x^2)/(1 + Sqrt[3] - (b/a)^(1/3)*x)^2]*EllipticE[ArcSin[(1 - Sqrt[3] - (b/a)^(1/3)*x)/(1 + Sqrt[3] - (b/a)^(1/3)*x)], -7 - 4*Sqrt[3]])/((b/a)^(1/3)*Sqrt[(1 - (b/a)^(1/3)*x)/(1 + Sqrt[3] - (b/a)^(1/3)*x)^2]*Sqrt[a - b*x^3])", //
//				1877);
//	}

	// {383}
	public void test0878() {
		check(//
				"Integrate[(a*g - b*g*x^4)/(a + b*x^4)^(3/2), x]", //
				"(g*x)/Sqrt[a + b*x^4]", //
				383);
	}

	// {1856}
	public void test0879() {
		check(//
				"Integrate[(a*g + e*x - b*g*x^4)/(a + b*x^4)^(3/2), x]", //
				"(2*a*g*x + e*x^2)/(2*a*Sqrt[a + b*x^4])", //
				1856);
	}

	// {1856}
	public void test0880() {
		check(//
				"Integrate[(a*g + f*x^3 - b*g*x^4)/(a + b*x^4)^(3/2), x]", //
				"-(f - 2*b*g*x)/(2*b*Sqrt[a + b*x^4])", //
				1856);
	}

	// {1856}
	public void test0881() {
		check(//
				"Integrate[(a*g + e*x + f*x^3 - b*g*x^4)/(a + b*x^4)^(3/2), x]", //
				"-(a*f - 2*a*b*g*x - b*e*x^2)/(2*a*b*Sqrt[a + b*x^4])", //
				1856);
	}

	// {383}
	public void test0882() {
		check(//
				"Integrate[(-1 + x^4)/(1 + x^4)^(3/2), x]", //
				"-(x/Sqrt[1 + x^4])", //
				383);
	}

	// {1590}
	public void test0884() {
		check(//
				"Integrate[(a*c + 2*(b*c + a*d)*x^2 + 3*b*d*x^4)/(Sqrt[a + b*x^2]*Sqrt[c + d*x^2]), x]", //
				"x*Sqrt[a + b*x^2]*Sqrt[c + d*x^2]", //
				1590);
	}

	// {1898}
	public void test0885() {
		check(//
				"Integrate[(a + b*x^n)^((-1 - n)/n)*(c + d*x^n)^((-1 - n)/n)*(a*c - b*d*x^(2*n)), x]", //
				"x/((a + b*x^n)^n^(-1)*(c + d*x^n)^n^(-1))", //
				1898);
	}

	// {1849}
	public void test0886() {
		check(//
				"Integrate[(h*x)^(-1 - n - n*p)*(a + b*x^n)^p*(c + d*x^n)^p*(a*c - b*d*x^(2*n)), x]", //
				"-(((a + b*x^n)^(1 + p)*(c + d*x^n)^(1 + p))/(h*n*(1 + p)*(h*x)^(n*(1 + p))))", //
				1849);
	}

	// {1897}
	public void test0887() {
		check(//
				"Integrate[(a + b*x^n)^p*(c + d*x^n)^p*(e + ((b*c + a*d)*e*(1 + n + n*p)*x^n)/(a*c) + (b*d*e*(1 + 2*n + 2*n*p)*x^(2*n))/(a*c)), x]", //
				"(e*x*(a + b*x^n)^(1 + p)*(c + d*x^n)^(1 + p))/(a*c)", //
				1897);
	}

	// {1848}
	public void test0888() {
		check(//
				"Integrate[(h*x)^m*(a + b*x^n)^p*(c + d*x^n)^p*(e + ((b*c + a*d)*e*(1 + m + n + n*p)*x^n)/(a*c*(1 + m)) + (b*d*e*(1 + m + 2*n + 2*n*p)*x^(2*n))/(a*c*(1 + m))), x]", //
				"(e*(h*x)^(1 + m)*(a + b*x^n)^(1 + p)*(c + d*x^n)^(1 + p))/(a*c*h*(1 + m))", //
				1848);
	}

	// {2014}
	public void test0890() {
		check(//
				"Integrate[x^(21/2)/(a*x + b*x^3)^(9/2), x]", //
				"x^(21/2)/(7*a*(a*x + b*x^3)^(7/2))", //
				2014);
	}

	// {2014}
	public void test0891() {
		check(//
				"Integrate[x^(11/2)/(a*x + b*x^3)^(9/2), x]", //
				"-x^(7/2)/(7*b*(a*x + b*x^3)^(7/2))", //
				2014);
	}

	// {2014}
	public void test0892() {
		check(//
				"Integrate[1/(x^2*Sqrt[a*x + b*x^4]), x]", //
				"(-2*Sqrt[a*x + b*x^4])/(3*a*x^2)", //
				2014);
	}

	// {2014}
	public void test0893() {
		check(//
				"Integrate[1/(x*Sqrt[b*Sqrt[x] + a*x]), x]", //
				"(-4*Sqrt[b*Sqrt[x] + a*x])/(b*Sqrt[x])", //
				2014);
	}

	// {2000}
	public void test0894() {
		check(//
				"Integrate[(b*Sqrt[x] + a*x)^(-3/2), x]", //
				"(4*Sqrt[x])/(b*Sqrt[b*Sqrt[x] + a*x])", //
				2000);
	}

	// {2014}
	public void test0895() {
		check(//
				"Integrate[Sqrt[b*x^(2/3) + a*x]/x, x]", //
				"(2*(b*x^(2/3) + a*x)^(3/2))/(a*x)", //
				2014);
	}

	// {1584, 32}
	public void test0897() {
		check(//
				"Integrate[x^4/(a*x^2 + b*x^3)^2, x]", //
				"-(1/(b*(a + b*x)))", //
				1584, 32);
	}

	// {2014}
	public void test0898() {
		check(//
				"Integrate[Sqrt[a*x^2 + b*x^3]/x, x]", //
				"(2*(a*x^2 + b*x^3)^(3/2))/(3*b*x^3)", //
				2014);
	}

	// {2014}
	public void test0899() {
		check(//
				"Integrate[(a*x^2 + b*x^3)^(3/2)/x^3, x]", //
				"(2*(a*x^2 + b*x^3)^(5/2))/(5*b*x^5)", //
				2014);
	}

	// {1588}
	public void test0900() {
		check(//
				"Integrate[x/Sqrt[a*x^2 + b*x^3], x]", //
				"(2*Sqrt[a*x^2 + b*x^3])/(b*x)", //
				1588);
	}

	// {1588}
	public void test0901() {
		check(//
				"Integrate[x^3/(a*x^2 + b*x^3)^(3/2), x]", //
				"(-2*x)/(b*Sqrt[a*x^2 + b*x^3])", //
				1588);
	}

	// {2014}
	public void test0902() {
		check(//
				"Integrate[1/(Sqrt[x]*Sqrt[a*x^2 + b*x^3]), x]", //
				"(-2*Sqrt[a*x^2 + b*x^3])/(a*x^(3/2))", //
				2014);
	}

	// {2014}
	public void test0903() {
		check(//
				"Integrate[x^(-2 - 3*n)*(a*x^2 + b*x^3)^n, x]", //
				"-((a*x^2 + b*x^3)^(1 + n)/(a*(1 + n)*x^(3*(1 + n))))", //
				2014);
	}

	// {1588}
	public void test0904() {
		check(//
				"Integrate[x^3/Sqrt[a*x^2 + b*x^5], x]", //
				"(2*Sqrt[a*x^2 + b*x^5])/(3*b*x)", //
				1588);
	}

	// {2014}
	public void test0905() {
		check(//
				"Integrate[1/(x^(3/2)*Sqrt[a*x^2 + b*x^5]), x]", //
				"(-2*Sqrt[a*x^2 + b*x^5])/(3*a*x^(5/2))", //
				2014);
	}

	// {2000}
	public void test0906() {
		check(//
				"Integrate[1/Sqrt[a*x^3 + b*x^4], x]", //
				"(-2*Sqrt[a*x^3 + b*x^4])/(a*x^2)", //
				2000);
	}

	// {1584, 261}
	public void test0907() {
		check(//
				"Integrate[x^12*(a*x + b*x^26)^12, x]", //
				"(a + b*x^25)^13/(325*b)", //
				1584, 261);
	}

	// {1584, 261}
	public void test0908() {
		check(//
				"Integrate[x^24*(a*x + b*x^38)^12, x]", //
				"(a + b*x^37)^13/(481*b)", //
				1584, 261);
	}

	// {261}
	public void test0909() {
		check(//
				"Integrate[x^12*(a + b*x^13)^12, x]", //
				"(a + b*x^13)^13/(169*b)", //
				261);
	}

	// {1584, 261}
	public void test0910() {
		check(//
				"Integrate[x^12*(a*x + b*x^26)^12, x]", //
				"(a + b*x^25)^13/(325*b)", //
				1584, 261);
	}

	// {1584, 261}
	public void test0911() {
		check(//
				"Integrate[x^12*(a*x^2 + b*x^39)^12, x]", //
				"(a + b*x^37)^13/(481*b)", //
				1584, 261);
	}

	// {261}
	public void test0912() {
		check(//
				"Integrate[x^24*(a + b*x^25)^12, x]", //
				"(a + b*x^25)^13/(325*b)", //
				261);
	}

	// {1584, 261}
	public void test0913() {
		check(//
				"Integrate[x^24*(a*x + b*x^38)^12, x]", //
				"(a + b*x^37)^13/(481*b)", //
				1584, 261);
	}

	// {261}
	public void test0914() {
		check(//
				"Integrate[x^36*(a + b*x^37)^12, x]", //
				"(a + b*x^37)^13/(481*b)", //
				261);
	}

	// {2000}
	public void test0915() {
		check(//
				"Integrate[Sqrt[x + x^(5/2)], x]", //
				"(4*(x + x^(5/2))^(3/2))/(9*x^(3/2))", //
				2000);
	}

	// {1588}
	public void test0916() {
		check(//
				"Integrate[x*Sqrt[x^2*(a + b*x^3)], x]", //
				"(2*(x^2*(a + b*x^3))^(3/2))/(9*b*x^3)", //
				1588);
	}

	// {1588}
	public void test0917() {
		check(//
				"Integrate[x*Sqrt[a*x^2 + b*x^5], x]", //
				"(2*(a*x^2 + b*x^5)^(3/2))/(9*b*x^3)", //
				1588);
	}

	// {2000}
	public void test0918() {
		check(//
				"Integrate[(a*x^m + b*x^(1 + m + m*p))^p, x]", //
				"(a*x^m + b*x^(1 + m + m*p))^(1 + p)/(b*(1 + p)*(1 + m*p)*x^(m*(1 + p)))", //
				2000);
	}

	// {2014}
	public void test0919() {
		check(//
				"Integrate[x^n*(a*x^m + b*x^(1 + m + n + m*p))^p, x]", //
				"(a*x^m + b*x^(1 + m + n + m*p))^(1 + p)/(b*(1 + p)*(1 + n + m*p)*x^(m*(1 + p)))", //
				2014);
	}

	// {2014}
	public void test0920() {
		check(//
				"Integrate[x^(-1 + n - p*(1 + q))*(a*x^n + b*x^p)^q, x]", //
				"(a*x^n + b*x^p)^(1 + q)/(a*(n - p)*(1 + q)*x^(p*(1 + q)))", //
				2014);
	}

	// {613}
	public void test0921() {
		check(//
				"Integrate[((3*I)*x + 4*x^2)^(-3/2), x]", //
				"(2*(3*I + 8*x))/(9*Sqrt[(3*I)*x + 4*x^2])", //
				613);
	}

	// {613}
	public void test0922() {
		check(//
				"Integrate[(3*x - 4*x^2)^(-3/2), x]", //
				"(-2*(3 - 8*x))/(9*Sqrt[3*x - 4*x^2])", //
				613);
	}

	// {624}
	public void test0923() {
		check(//
				"Integrate[(b*x + c*x^2)^p, x]", //
				"-(((-((c*x)/b))^(-1 - p)*(b*x + c*x^2)^(1 + p)*Hypergeometric2F1[-p, 1 + p, 2 + p, (b + c*x)/b])/(b*(1 + p)))", //
				624);
	}

	// {205}
	public void test0925() {
		check(//
				"Integrate[(a + c*x^2)^(-1), x]", //
				"ArcTan[(Sqrt[c]*x)/Sqrt[a]]/(Sqrt[a]*Sqrt[c])", //
				205);
	}

	// {191}
	public void test0926() {
		check(//
				"Integrate[(a + c*x^2)^(-3/2), x]", //
				"x/(a*Sqrt[a + c*x^2])", //
				191);
	}

	// {609}
	public void test0927() {
		check(//
				"Integrate[(4 + 12*x + 9*x^2)^(3/2), x]", //
				"((2 + 3*x)*(4 + 12*x + 9*x^2)^(3/2))/12", //
				609);
	}

	// {609}
	public void test0928() {
		check(//
				"Integrate[Sqrt[4 + 12*x + 9*x^2], x]", //
				"((2 + 3*x)*Sqrt[4 + 12*x + 9*x^2])/6", //
				609);
	}

	// {607}
	public void test0929() {
		check(//
				"Integrate[(4 + 12*x + 9*x^2)^(-3/2), x]", //
				"-1/(6*(2 + 3*x)*Sqrt[4 + 12*x + 9*x^2])", //
				607);
	}

	// {609}
	public void test0930() {
		check(//
				"Integrate[Sqrt[4 - 12*x + 9*x^2], x]", //
				"-((2 - 3*x)*Sqrt[4 - 12*x + 9*x^2])/6", //
				609);
	}

	// {609}
	public void test0931() {
		check(//
				"Integrate[Sqrt[-4 + 12*x - 9*x^2], x]", //
				"-((2 - 3*x)*Sqrt[-4 + 12*x - 9*x^2])/6", //
				609);
	}

	// {609}
	public void test0932() {
		check(//
				"Integrate[Sqrt[-4 - 12*x - 9*x^2], x]", //
				"((2 + 3*x)*Sqrt[-4 - 12*x - 9*x^2])/6", //
				609);
	}

	// {613}
	public void test0933() {
		check(//
				"Integrate[(2 + 3*x + x^2)^(-3/2), x]", //
				"(-2*(3 + 2*x))/Sqrt[2 + 3*x + x^2]", //
				613);
	}

	// {613}
	public void test0934() {
		check(//
				"Integrate[(27 - 24*x + 4*x^2)^(-3/2), x]", //
				"(3 - x)/(9*Sqrt[27 - 24*x + 4*x^2])", //
				613);
	}

	// {636}
	public void test0935() {
		check(//
				"Integrate[x/(5 - 4*x - x^2)^(3/2), x]", //
				"(5 - 2*x)/(9*Sqrt[5 - 4*x - x^2])", //
				636);
	}

	// {624}
	public void test0936() {
		check(//
				"Integrate[(a + b*x + c*x^2)^p, x]", //
				"-((2^(1 + p)*(-((b - Sqrt[b^2 - 4*a*c] + 2*c*x)/Sqrt[b^2 - 4*a*c]))^(-1 - p)*(a + b*x + c*x^2)^(1 + p)*Hypergeometric2F1[-p, 1 + p, 2 + p, (b + Sqrt[b^2 - 4*a*c] + 2*c*x)/(2*Sqrt[b^2 - 4*a*c])])/(Sqrt[b^2 - 4*a*c]*(1 + p)))", //
				624);
	}

	// {624}
	public void test0937() {
		check(//
				"Integrate[(3 + 4*x + x^2)^p, x]", //
				"-((2^(1 + 2*p)*(-2 - 2*x)^(-1 - p)*(3 + 4*x + x^2)^(1 + p)*Hypergeometric2F1[-p, 1 + p, 2 + p, (3 + x)/2])/(1 + p))", //
				624);
	}

	// {32}
	public void test0938() {
		check(//
				"Integrate[(3 + 4*x)^p, x]", //
				"(3 + 4*x)^(1 + p)/(4*(1 + p))", //
				32);
	}

	// {650}
	public void test0939() {
		check(//
				"Integrate[Sqrt[b*x + c*x^2]/x^3, x]", //
				"(-2*(b*x + c*x^2)^(3/2))/(3*b*x^3)", //
				650);
	}

	// {650}
	public void test0940() {
		check(//
				"Integrate[(b*x + c*x^2)^(3/2)/x^5, x]", //
				"(-2*(b*x + c*x^2)^(5/2))/(5*b*x^5)", //
				650);
	}

	// {650}
	public void test0941() {
		check(//
				"Integrate[(a*x + b*x^2)^(5/2)/x^7, x]", //
				"(-2*(a*x + b*x^2)^(7/2))/(7*a*x^7)", //
				650);
	}

	// {650}
	public void test0942() {
		check(//
				"Integrate[1/(x*Sqrt[b*x + c*x^2]), x]", //
				"(-2*Sqrt[b*x + c*x^2])/(b*x)", //
				650);
	}

	// {636}
	public void test0943() {
		check(//
				"Integrate[x/(b*x + c*x^2)^(3/2), x]", //
				"(2*x)/(b*Sqrt[b*x + c*x^2])", //
				636);
	}

	// {613}
	public void test0944() {
		check(//
				"Integrate[(b*x + c*x^2)^(-3/2), x]", //
				"(-2*(b + 2*c*x))/(b^2*Sqrt[b*x + c*x^2])", //
				613);
	}

	// {650}
	public void test0945() {
		check(//
				"Integrate[x^3/(a*x + b*x^2)^(5/2), x]", //
				"(2*x^3)/(3*a*(a*x + b*x^2)^(3/2))", //
				650);
	}

	// {648}
	public void test0946() {
		check(//
				"Integrate[Sqrt[b*x + c*x^2]/Sqrt[x], x]", //
				"(2*(b*x + c*x^2)^(3/2))/(3*c*x^(3/2))", //
				648);
	}

	// {648}
	public void test0947() {
		check(//
				"Integrate[(b*x + c*x^2)^(3/2)/x^(3/2), x]", //
				"(2*(b*x + c*x^2)^(5/2))/(5*c*x^(5/2))", //
				648);
	}

	// {648}
	public void test0948() {
		check(//
				"Integrate[Sqrt[x]/Sqrt[b*x + c*x^2], x]", //
				"(2*Sqrt[b*x + c*x^2])/(c*Sqrt[x])", //
				648);
	}

	// {648}
	public void test0949() {
		check(//
				"Integrate[x^(3/2)/(b*x + c*x^2)^(3/2), x]", //
				"(-2*Sqrt[x])/(c*Sqrt[b*x + c*x^2])", //
				648);
	}

	// {609}
	public void test0950() {
		check(//
				"Integrate[Sqrt[a^2 + 2*a*b*x + b^2*x^2], x]", //
				"((a + b*x)*Sqrt[a^2 + 2*a*b*x + b^2*x^2])/(2*b)", //
				609);
	}

	// {609}
	public void test0951() {
		check(//
				"Integrate[(a^2 + 2*a*b*x + b^2*x^2)^(3/2), x]", //
				"((a + b*x)*(a^2 + 2*a*b*x + b^2*x^2)^(3/2))/(4*b)", //
				609);
	}

	// {609}
	public void test0952() {
		check(//
				"Integrate[(a^2 + 2*a*b*x + b^2*x^2)^(5/2), x]", //
				"((a + b*x)*(a^2 + 2*a*b*x + b^2*x^2)^(5/2))/(6*b)", //
				609);
	}

	// {607}
	public void test0953() {
		check(//
				"Integrate[(a^2 + 2*a*b*x + b^2*x^2)^(-3/2), x]", //
				"-1/(2*b*(a + b*x)*Sqrt[a^2 + 2*a*b*x + b^2*x^2])", //
				607);
	}

	// {607}
	public void test0954() {
		check(//
				"Integrate[(a^2 + 2*a*b*x + b^2*x^2)^(-5/2), x]", //
				"-1/(4*b*(a + b*x)*(a^2 + 2*a*b*x + b^2*x^2)^(3/2))", //
				607);
	}

	// {628}
	public void test0955() {
		check(//
				"Integrate[(1 + x)/(2*x + x^2), x]", //
				"Log[2*x + x^2]/2", //
				628);
	}

	// {628}
	public void test0956() {
		check(//
				"Integrate[(a + 2*b*x)/(a*x + b*x^2), x]", //
				"Log[a*x + b*x^2]", //
				628);
	}

	// {636}
	public void test0958() {
		check(//
				"Integrate[(d + e*x)/(b*x + c*x^2)^(3/2), x]", //
				"(-2*(b*d + (2*c*d - b*e)*x))/(b^2*Sqrt[b*x + c*x^2])", //
				636);
	}

	// {613}
	public void test0959() {
		check(//
				"Integrate[(b*x + c*x^2)^(-3/2), x]", //
				"(-2*(b + 2*c*x))/(b^2*Sqrt[b*x + c*x^2])", //
				613);
	}

	// {650}
	public void test0960() {
		check(//
				"Integrate[1/((2 + x)*Sqrt[2*x + x^2]), x]", //
				"Sqrt[2*x + x^2]/(2 + x)", //
				650);
	}

	// {110}
	public void test0961() {
		check(//
				"Integrate[Sqrt[1 - x]/(Sqrt[-x]*Sqrt[1 + x]), x]", //
				"-2*EllipticE[ArcSin[Sqrt[-x]], -1]", //
				110);
	}

	// {32}
	public void test0962() {
		check(//
				"Integrate[(d + e*x)^m, x]", //
				"(d + e*x)^(1 + m)/(e*(1 + m))", //
				32);
	}

	// {637}
	public void test0963() {
		check(//
				"Integrate[(d + e*x)/(a + c*x^2)^(3/2), x]", //
				"-((a*e - c*d*x)/(a*c*Sqrt[a + c*x^2]))", //
				637);
	}

	// {637}
	public void test0964() {
		check(//
				"Integrate[(2 + 3*x)/(4 + x^2)^(3/2), x]", //
				"-(6 - x)/(2*Sqrt[4 + x^2])", //
				637);
	}

	// {751}
	public void test0965() {
		check(//
				"Integrate[1/((d + e*x)*(d^2 + 3*e^2*x^2)^(1/3)), x]", //
				"-(ArcTan[1/Sqrt[3] + (2^(2/3)*(d - e*x))/(Sqrt[3]*d^(1/3)*(d^2 + 3*e^2*x^2)^(1/3))]/(2^(2/3)*Sqrt[3]*d^(2/3)*e)) - Log[d + e*x]/(2*2^(2/3)*d^(2/3)*e) + Log[3*d*e^2 - 3*e^3*x - 3*2^(1/3)*d^(1/3)*e^2*(d^2 + 3*e^2*x^2)^(1/3)]/(2*2^(2/3)*d^(2/3)*e)", //
				751);
	}

	// {751}
	public void test0966() {
		check(//
				"Integrate[1/((2 + 3*x)*(4 + 27*x^2)^(1/3)), x]", //
				"-ArcTan[1/Sqrt[3] + (2^(1/3)*(2 - 3*x))/(Sqrt[3]*(4 + 27*x^2)^(1/3))]/(6*2^(1/3)*Sqrt[3]) - Log[2 + 3*x]/(12*2^(1/3)) + Log[54 - 81*x - 27*2^(2/3)*(4 + 27*x^2)^(1/3)]/(12*2^(1/3))", //
				751);
	}

	// {751}
	public void test0967() {
		check(//
				"Integrate[1/((2 + (3*I)*x)*(4 - 27*x^2)^(1/3)), x]", //
				"((I/6)*ArcTan[1/Sqrt[3] + (2^(1/3)*(2 - (3*I)*x))/(Sqrt[3]*(4 - 27*x^2)^(1/3))])/(2^(1/3)*Sqrt[3]) + ((I/12)*Log[2 + (3*I)*x])/2^(1/3) - ((I/12)*Log[-54 + (81*I)*x + 27*2^(2/3)*(4 - 27*x^2)^(1/3)])/2^(1/3)", //
				751);
	}

	// {751}
	public void test0968() {
		check(//
				"Integrate[1/((Sqrt[3] + x)*(1 + x^2)^(1/3)), x]", //
				"-(ArcTan[1/Sqrt[3] + (2^(2/3)*(Sqrt[3] - x))/(3*(1 + x^2)^(1/3))]/(2^(2/3)*Sqrt[3])) - Log[Sqrt[3] + x]/(2*2^(2/3)) + Log[Sqrt[3] - x - 2^(1/3)*Sqrt[3]*(1 + x^2)^(1/3)]/(2*2^(2/3))", //
				751);
	}

	// {751}
	public void test0969() {
		check(//
				"Integrate[1/((Sqrt[3] - x)*(1 + x^2)^(1/3)), x]", //
				"ArcTan[1/Sqrt[3] + (2^(2/3)*(Sqrt[3] + x))/(3*(1 + x^2)^(1/3))]/(2^(2/3)*Sqrt[3]) + Log[Sqrt[3] - x]/(2*2^(2/3)) - Log[Sqrt[3] + x - 2^(1/3)*Sqrt[3]*(1 + x^2)^(1/3)]/(2*2^(2/3))", //
				751);
	}

	// {727}
	public void test0970() {
		check(//
				"Integrate[1/((d + e*x)^(3/2)*(a + c*x^2)^(1/4)), x]", //
				"(-2*(Sqrt[-a] - Sqrt[c]*x)*(-(((Sqrt[c]*d + Sqrt[-a]*e)*(Sqrt[-a] + Sqrt[c]*x))/((Sqrt[c]*d - Sqrt[-a]*e)*(Sqrt[-a] - Sqrt[c]*x))))^(1/4)*Hypergeometric2F1[-1/2, 1/4, 1/2, (2*Sqrt[-a]*Sqrt[c]*(d + e*x))/((Sqrt[c]*d - Sqrt[-a]*e)*(Sqrt[-a] - Sqrt[c]*x))])/((Sqrt[c]*d + Sqrt[-a]*e)*Sqrt[d + e*x]*(a + c*x^2)^(1/4))", //
				727);
	}

	// {727}
	public void test0971() {
		check(//
				"Integrate[(d + e*x)^(-2 - 2*p)*(a + c*x^2)^p, x]", //
				"-(((Sqrt[-a] - Sqrt[c]*x)*(d + e*x)^(-1 - 2*p)*(a + c*x^2)^p*Hypergeometric2F1[-1 - 2*p, -p, -2*p, (2*Sqrt[-a]*Sqrt[c]*(d + e*x))/((Sqrt[c]*d - Sqrt[-a]*e)*(Sqrt[-a] - Sqrt[c]*x))])/((Sqrt[c]*d + Sqrt[-a]*e)*(1 + 2*p)*(-(((Sqrt[c]*d + Sqrt[-a]*e)*(Sqrt[-a] + Sqrt[c]*x))/((Sqrt[c]*d - Sqrt[-a]*e)*(Sqrt[-a] - Sqrt[c]*x))))^p))", //
				727);
	}

	// {651}
	public void test0972() {
		check(//
				"Integrate[Sqrt[a^2 - b^2*x^2]/(a + b*x)^3, x]", //
				"-(a^2 - b^2*x^2)^(3/2)/(3*a*b*(a + b*x)^3)", //
				651);
	}

	// {651}
	public void test0973() {
		check(//
				"Integrate[(a^2 - b^2*x^2)^(3/2)/(a + b*x)^5, x]", //
				"-(a^2 - b^2*x^2)^(5/2)/(5*a*b*(a + b*x)^5)", //
				651);
	}

	// {651}
	public void test0974() {
		check(//
				"Integrate[(d^2 - e^2*x^2)^(7/2)/(d + e*x)^9, x]", //
				"-(d^2 - e^2*x^2)^(9/2)/(9*d*e*(d + e*x)^9)", //
				651);
	}

	// {651}
	public void test0975() {
		check(//
				"Integrate[Sqrt[1 - x^2]/(1 - x)^3, x]", //
				"(1 - x^2)^(3/2)/(3*(1 - x)^3)", //
				651);
	}

	// {651}
	public void test0976() {
		check(//
				"Integrate[1/((d + e*x)*Sqrt[d^2 - e^2*x^2]), x]", //
				"-(Sqrt[d^2 - e^2*x^2]/(d*e*(d + e*x)))", //
				651);
	}

	// {651}
	public void test0977() {
		check(//
				"Integrate[(d + e*x)^3/(d^2 - e^2*x^2)^(5/2), x]", //
				"(d + e*x)^3/(3*d*e*(d^2 - e^2*x^2)^(3/2))", //
				651);
	}

	// {651}
	public void test0978() {
		check(//
				"Integrate[(d + e*x)^5/(d^2 - e^2*x^2)^(7/2), x]", //
				"(d + e*x)^5/(5*d*e*(d^2 - e^2*x^2)^(5/2))", //
				651);
	}

	// {649}
	public void test0979() {
		check(//
				"Integrate[Sqrt[c*d^2 - c*e^2*x^2]/Sqrt[d + e*x], x]", //
				"(-2*(c*d^2 - c*e^2*x^2)^(3/2))/(3*c*e*(d + e*x)^(3/2))", //
				649);
	}

	// {649}
	public void test0980() {
		check(//
				"Integrate[(c*d^2 - c*e^2*x^2)^(3/2)/(d + e*x)^(3/2), x]", //
				"(-2*(c*d^2 - c*e^2*x^2)^(5/2))/(5*c*e*(d + e*x)^(5/2))", //
				649);
	}

	// {649}
	public void test0981() {
		check(//
				"Integrate[Sqrt[d + e*x]/Sqrt[c*d^2 - c*e^2*x^2], x]", //
				"(-2*Sqrt[c*d^2 - c*e^2*x^2])/(c*e*Sqrt[d + e*x])", //
				649);
	}

	// {649}
	public void test0982() {
		check(//
				"Integrate[(d + e*x)^(3/2)/(c*d^2 - c*e^2*x^2)^(3/2), x]", //
				"(2*Sqrt[d + e*x])/(c*e*Sqrt[c*d^2 - c*e^2*x^2])", //
				649);
	}

	// {651}
	public void test0983() {
		check(//
				"Integrate[(12 - 3*e^2*x^2)^(1/4)/(2 + e*x)^(5/2), x]", //
				"-(3^(1/4)*(4 - e^2*x^2)^(5/4))/(5*e*(2 + e*x)^(5/2))", //
				651);
	}

	// {651}
	public void test0984() {
		check(//
				"Integrate[1/((2 + e*x)^(3/2)*(12 - 3*e^2*x^2)^(1/4)), x]", //
				"-(4 - e^2*x^2)^(3/4)/(3*3^(1/4)*e*(2 + e*x)^(3/2))", //
				651);
	}

	// {245}
	public void test0985() {
		check(//
				"Integrate[(1 - (e^2*x^2)/d^2)^p, x]", //
				"x*Hypergeometric2F1[1/2, -p, 3/2, (e^2*x^2)/d^2]", //
				245);
	}

	// {629}
	public void test0987() {
		check(//
				"Integrate[(d + e*x)*Sqrt[c*d^2 + 2*c*d*e*x + c*e^2*x^2], x]", //
				"(c*d^2 + 2*c*d*e*x + c*e^2*x^2)^(3/2)/(3*c*e)", //
				629);
	}

	// {609}
	public void test0988() {
		check(//
				"Integrate[Sqrt[c*d^2 + 2*c*d*e*x + c*e^2*x^2], x]", //
				"((d + e*x)*Sqrt[c*d^2 + 2*c*d*e*x + c*e^2*x^2])/(2*e)", //
				609);
	}

	// {629}
	public void test0989() {
		check(//
				"Integrate[(d + e*x)*(c*d^2 + 2*c*d*e*x + c*e^2*x^2)^(3/2), x]", //
				"(c*d^2 + 2*c*d*e*x + c*e^2*x^2)^(5/2)/(5*c*e)", //
				629);
	}

	// {609}
	public void test0990() {
		check(//
				"Integrate[(c*d^2 + 2*c*d*e*x + c*e^2*x^2)^(3/2), x]", //
				"((d + e*x)*(c*d^2 + 2*c*d*e*x + c*e^2*x^2)^(3/2))/(4*e)", //
				609);
	}

	// {629}
	public void test0991() {
		check(//
				"Integrate[(d + e*x)*(c*d^2 + 2*c*d*e*x + c*e^2*x^2)^(5/2), x]", //
				"(c*d^2 + 2*c*d*e*x + c*e^2*x^2)^(7/2)/(7*c*e)", //
				629);
	}

	// {609}
	public void test0992() {
		check(//
				"Integrate[(c*d^2 + 2*c*d*e*x + c*e^2*x^2)^(5/2), x]", //
				"((d + e*x)*(c*d^2 + 2*c*d*e*x + c*e^2*x^2)^(5/2))/(6*e)", //
				609);
	}

	// {629}
	public void test0993() {
		check(//
				"Integrate[(d + e*x)/Sqrt[c*d^2 + 2*c*d*e*x + c*e^2*x^2], x]", //
				"Sqrt[c*d^2 + 2*c*d*e*x + c*e^2*x^2]/(c*e)", //
				629);
	}

	// {629}
	public void test0994() {
		check(//
				"Integrate[(d + e*x)/(c*d^2 + 2*c*d*e*x + c*e^2*x^2)^(3/2), x]", //
				"-(1/(c*e*Sqrt[c*d^2 + 2*c*d*e*x + c*e^2*x^2]))", //
				629);
	}

	// {607}
	public void test0995() {
		check(//
				"Integrate[(c*d^2 + 2*c*d*e*x + c*e^2*x^2)^(-3/2), x]", //
				"-1/(2*c*e*(d + e*x)*Sqrt[c*d^2 + 2*c*d*e*x + c*e^2*x^2])", //
				607);
	}

	// {629}
	public void test0996() {
		check(//
				"Integrate[(d + e*x)/(c*d^2 + 2*c*d*e*x + c*e^2*x^2)^(5/2), x]", //
				"-1/(3*c*e*(c*d^2 + 2*c*d*e*x + c*e^2*x^2)^(3/2))", //
				629);
	}

	// {607}
	public void test0997() {
		check(//
				"Integrate[(c*d^2 + 2*c*d*e*x + c*e^2*x^2)^(-5/2), x]", //
				"-1/(4*c*e*(d + e*x)*(c*d^2 + 2*c*d*e*x + c*e^2*x^2)^(3/2))", //
				607);
	}

	// {629}
	public void test0998() {
		check(//
				"Integrate[(d + e*x)*(c*d^2 + 2*c*d*e*x + c*e^2*x^2)^p, x]", //
				"(c*d^2 + 2*c*d*e*x + c*e^2*x^2)^(1 + p)/(2*c*e*(1 + p))", //
				629);
	}

	// {609}
	public void test0999() {
		check(//
				"Integrate[(c*d^2 + 2*c*d*e*x + c*e^2*x^2)^p, x]", //
				"((d + e*x)*(c*d^2 + 2*c*d*e*x + c*e^2*x^2)^p)/(e*(1 + 2*p))", //
				609);
	}

	// {629}
	public void test1000() {
		check(//
				"Integrate[(b*d + 2*c*d*x)*(a + b*x + c*x^2), x]", //
				"(d*(a + b*x + c*x^2)^2)/2", //
				629);
	}

	// {682}
	public void test1001() {
		check(//
				"Integrate[(a + b*x + c*x^2)/(b*d + 2*c*d*x)^5, x]", //
				"(a + b*x + c*x^2)^2/(2*(b^2 - 4*a*c)*d^5*(b + 2*c*x)^4)", //
				682);
	}

	// {629}
	public void test1002() {
		check(//
				"Integrate[(b*d + 2*c*d*x)*(a + b*x + c*x^2)^2, x]", //
				"(d*(a + b*x + c*x^2)^3)/3", //
				629);
	}

	// {682}
	public void test1003() {
		check(//
				"Integrate[(a + b*x + c*x^2)^2/(b*d + 2*c*d*x)^7, x]", //
				"(a + b*x + c*x^2)^3/(3*(b^2 - 4*a*c)*d^7*(b + 2*c*x)^6)", //
				682);
	}

	// {629}
	public void test1004() {
		check(//
				"Integrate[(b*d + 2*c*d*x)*(a + b*x + c*x^2)^3, x]", //
				"(d*(a + b*x + c*x^2)^4)/4", //
				629);
	}

	// {682}
	public void test1005() {
		check(//
				"Integrate[(a + b*x + c*x^2)^3/(b*d + 2*c*d*x)^9, x]", //
				"(a + b*x + c*x^2)^4/(4*(b^2 - 4*a*c)*d^9*(b + 2*c*x)^8)", //
				682);
	}

	// {628}
	public void test1006() {
		check(//
				"Integrate[(b*d + 2*c*d*x)/(a + b*x + c*x^2), x]", //
				"d*Log[a + b*x + c*x^2]", //
				628);
	}

	// {629}
	public void test1007() {
		check(//
				"Integrate[(b*d + 2*c*d*x)/(a + b*x + c*x^2)^2, x]", //
				"-(d/(a + b*x + c*x^2))", //
				629);
	}

	// {682}
	public void test1008() {
		check(//
				"Integrate[(b*d + 2*c*d*x)^3/(a + b*x + c*x^2)^3, x]", //
				"-(d^3*(b + 2*c*x)^4)/(2*(b^2 - 4*a*c)*(a + b*x + c*x^2)^2)", //
				682);
	}

	// {629}
	public void test1009() {
		check(//
				"Integrate[(b*d + 2*c*d*x)/(a + b*x + c*x^2)^3, x]", //
				"-d/(2*(a + b*x + c*x^2)^2)", //
				629);
	}

	// {629}
	public void test1010() {
		check(//
				"Integrate[(b*d + 2*c*d*x)*Sqrt[a + b*x + c*x^2], x]", //
				"(2*d*(a + b*x + c*x^2)^(3/2))/3", //
				629);
	}

	// {682}
	public void test1011() {
		check(//
				"Integrate[Sqrt[a + b*x + c*x^2]/(b*d + 2*c*d*x)^4, x]", //
				"(2*(a + b*x + c*x^2)^(3/2))/(3*(b^2 - 4*a*c)*d^4*(b + 2*c*x)^3)", //
				682);
	}

	// {629}
	public void test1012() {
		check(//
				"Integrate[(b*d + 2*c*d*x)*(a + b*x + c*x^2)^(3/2), x]", //
				"(2*d*(a + b*x + c*x^2)^(5/2))/5", //
				629);
	}

	// {682}
	public void test1013() {
		check(//
				"Integrate[(a + b*x + c*x^2)^(3/2)/(b*d + 2*c*d*x)^6, x]", //
				"(2*(a + b*x + c*x^2)^(5/2))/(5*(b^2 - 4*a*c)*d^6*(b + 2*c*x)^5)", //
				682);
	}

	// {629}
	public void test1014() {
		check(//
				"Integrate[(b*d + 2*c*d*x)*(a + b*x + c*x^2)^(5/2), x]", //
				"(2*d*(a + b*x + c*x^2)^(7/2))/7", //
				629);
	}

	// {682}
	public void test1015() {
		check(//
				"Integrate[(a + b*x + c*x^2)^(5/2)/(b*d + 2*c*d*x)^8, x]", //
				"(2*(a + b*x + c*x^2)^(7/2))/(7*(b^2 - 4*a*c)*d^8*(b + 2*c*x)^7)", //
				682);
	}

	// {629}
	public void test1016() {
		check(//
				"Integrate[(b*d + 2*c*d*x)/Sqrt[a + b*x + c*x^2], x]", //
				"2*d*Sqrt[a + b*x + c*x^2]", //
				629);
	}

	// {682}
	public void test1017() {
		check(//
				"Integrate[1/((b*d + 2*c*d*x)^2*Sqrt[a + b*x + c*x^2]), x]", //
				"(2*Sqrt[a + b*x + c*x^2])/((b^2 - 4*a*c)*d^2*(b + 2*c*x))", //
				682);
	}

	// {629}
	public void test1018() {
		check(//
				"Integrate[(b*d + 2*c*d*x)/(a + b*x + c*x^2)^(3/2), x]", //
				"(-2*d)/Sqrt[a + b*x + c*x^2]", //
				629);
	}

	// {682}
	public void test1019() {
		check(//
				"Integrate[(b*d + 2*c*d*x)^2/(a + b*x + c*x^2)^(5/2), x]", //
				"(-2*d^2*(b + 2*c*x)^3)/(3*(b^2 - 4*a*c)*(a + b*x + c*x^2)^(3/2))", //
				682);
	}

	// {629}
	public void test1020() {
		check(//
				"Integrate[(b*d + 2*c*d*x)/(a + b*x + c*x^2)^(5/2), x]", //
				"(-2*d)/(3*(a + b*x + c*x^2)^(3/2))", //
				629);
	}

	// {682}
	public void test1021() {
		check(//
				"Integrate[(a + b*x + c*x^2)^(4/3)/(b*d + 2*c*d*x)^(17/3), x]", //
				"(3*(a + b*x + c*x^2)^(7/3))/(7*(b^2 - 4*a*c)*d*(b*d + 2*c*d*x)^(14/3))", //
				682);
	}

	// {629}
	public void test1022() {
		check(//
				"Integrate[(b*d + 2*c*d*x)*(a + b*x + c*x^2)^p, x]", //
				"(d*(a + b*x + c*x^2)^(1 + p))/(1 + p)", //
				629);
	}

	// {629}
	public void test1023() {
		check(//
				"Integrate[(1 + x)/(-3 + 2*x + x^2)^(2/3), x]", //
				"(3*(-3 + 2*x + x^2)^(1/3))/2", //
				629);
	}

	// {629}
	public void test1024() {
		check(//
				"Integrate[(b + c*x)/(a + 2*b*x + c*x^2)^(3/7), x]", //
				"(7*(a + 2*b*x + c*x^2)^(4/7))/8", //
				629);
	}

	// {609}
	public void test1026() {
		check(//
				"Integrate[Sqrt[a^2 + 2*a*b*x + b^2*x^2], x]", //
				"((a + b*x)*Sqrt[a^2 + 2*a*b*x + b^2*x^2])/(2*b)", //
				609);
	}

	// {609}
	public void test1027() {
		check(//
				"Integrate[(a^2 + 2*a*b*x + b^2*x^2)^(3/2), x]", //
				"((a + b*x)*(a^2 + 2*a*b*x + b^2*x^2)^(3/2))/(4*b)", //
				609);
	}

	// {609}
	public void test1028() {
		check(//
				"Integrate[(a^2 + 2*a*b*x + b^2*x^2)^(5/2), x]", //
				"((a + b*x)*(a^2 + 2*a*b*x + b^2*x^2)^(5/2))/(6*b)", //
				609);
	}

	// {607}
	public void test1029() {
		check(//
				"Integrate[(a^2 + 2*a*b*x + b^2*x^2)^(-3/2), x]", //
				"-1/(2*b*(a + b*x)*Sqrt[a^2 + 2*a*b*x + b^2*x^2])", //
				607);
	}

	// {607}
	public void test1030() {
		check(//
				"Integrate[(a^2 + 2*a*b*x + b^2*x^2)^(-5/2), x]", //
				"-1/(4*b*(a + b*x)*(a^2 + 2*a*b*x + b^2*x^2)^(3/2))", //
				607);
	}

	// {609}
	public void test1031() {
		check(//
				"Integrate[(a^2 + 2*a*b*x + b^2*x^2)^p, x]", //
				"((a + b*x)*(a^2 + 2*a*b*x + b^2*x^2)^p)/(b*(1 + 2*p))", //
				609);
	}

	// {650}
	public void test1034() {
		check(//
				"Integrate[Sqrt[a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2]/(d + e*x)^3, x]", //
				"(2*(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^(3/2))/(3*(c*d^2 - a*e^2)*(d + e*x)^3)", //
				650);
	}

	// {650}
	public void test1035() {
		check(//
				"Integrate[(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^(3/2)/(d + e*x)^5, x]", //
				"(2*(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^(5/2))/(5*(c*d^2 - a*e^2)*(d + e*x)^5)", //
				650);
	}

	// {650}
	public void test1036() {
		check(//
				"Integrate[(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^(5/2)/(d + e*x)^7, x]", //
				"(2*(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^(7/2))/(7*(c*d^2 - a*e^2)*(d + e*x)^7)", //
				650);
	}

	// {650}
	public void test1037() {
		check(//
				"Integrate[1/((d + e*x)*Sqrt[a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2]), x]", //
				"(2*Sqrt[a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2])/((c*d^2 - a*e^2)*(d + e*x))", //
				650);
	}

	// {636}
	public void test1038() {
		check(//
				"Integrate[(d + e*x)/(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^(3/2), x]", //
				"(-2*(d + e*x))/((c*d^2 - a*e^2)*Sqrt[a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2])", //
				636);
	}

	// {613}
	public void test1039() {
		check(//
				"Integrate[(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^(-3/2), x]", //
				"(-2*(c*d^2 + a*e^2 + 2*c*d*e*x))/((c*d^2 - a*e^2)^2*Sqrt[a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2])", //
				613);
	}

	// {650}
	public void test1040() {
		check(//
				"Integrate[(d + e*x)^3/(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^(5/2), x]", //
				"(-2*(d + e*x)^3)/(3*(c*d^2 - a*e^2)*(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^(3/2))", //
				650);
	}

	// {648}
	public void test1041() {
		check(//
				"Integrate[Sqrt[a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2]/Sqrt[d + e*x], x]", //
				"(2*(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^(3/2))/(3*c*d*(d + e*x)^(3/2))", //
				648);
	}

	// {648}
	public void test1042() {
		check(//
				"Integrate[(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^(3/2)/(d + e*x)^(3/2), x]", //
				"(2*(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^(5/2))/(5*c*d*(d + e*x)^(5/2))", //
				648);
	}

	// {648}
	public void test1043() {
		check(//
				"Integrate[(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^(5/2)/(d + e*x)^(5/2), x]", //
				"(2*(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^(7/2))/(7*c*d*(d + e*x)^(7/2))", //
				648);
	}

	// {648}
	public void test1044() {
		check(//
				"Integrate[Sqrt[d + e*x]/Sqrt[a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2], x]", //
				"(2*Sqrt[a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2])/(c*d*Sqrt[d + e*x])", //
				648);
	}

	// {648}
	public void test1045() {
		check(//
				"Integrate[(d + e*x)^(3/2)/(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^(3/2), x]", //
				"(-2*Sqrt[d + e*x])/(c*d*Sqrt[a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2])", //
				648);
	}

	// {648}
	public void test1046() {
		check(//
				"Integrate[(d + e*x)^(5/2)/(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^(5/2), x]", //
				"(-2*(d + e*x)^(3/2))/(3*c*d*(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^(3/2))", //
				648);
	}

	// {624}
	public void test1047() {
		check(//
				"Integrate[(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^p, x]", //
				"-(((-((e*(a*e + c*d*x))/(c*d^2 - a*e^2)))^(-1 - p)*(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^(1 + p)*Hypergeometric2F1[-p, 1 + p, 2 + p, (c*d*(d + e*x))/(c*d^2 - a*e^2)])/((c*d^2 - a*e^2)*(1 + p)))", //
				624);
	}

	// {650}
	public void test1048() {
		check(//
				"Integrate[(d + e*x)^(-2 - 2*p)*(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^p, x]", //
				"(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^(1 + p)/((c*d^2 - a*e^2)*(1 + p)*(d + e*x)^(2*(1 + p)))", //
				650);
	}

	// {648}
	public void test1049() {
		check(//
				"Integrate[(d + e*x)^m/(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^m, x]", //
				"((d + e*x)^(-1 + m)*(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^(1 - m))/(c*d*(1 - m))", //
				648);
	}

	// {648}
	public void test1050() {
		check(//
				"Integrate[(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^p/(d + e*x)^p, x]", //
				"((d + e*x)^(-1 - p)*(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^(1 + p))/(c*d*(1 + p))", //
				648);
	}

	// {636}
	public void test1052() {
		check(//
				"Integrate[(d + e*x)/(a + b*x + c*x^2)^(3/2), x]", //
				"(-2*(b*d - 2*a*e + (2*c*d - b*e)*x))/((b^2 - 4*a*c)*Sqrt[a + b*x + c*x^2])", //
				636);
	}

	// {613}
	public void test1053() {
		check(//
				"Integrate[(a + b*x + c*x^2)^(-3/2), x]", //
				"(-2*(b + 2*c*x))/((b^2 - 4*a*c)*Sqrt[a + b*x + c*x^2])", //
				613);
	}

	// {636}
	public void test1054() {
		check(//
				"Integrate[(1 + x)/(2 + 3*x + x^2)^(3/2), x]", //
				"(2*(1 + x))/Sqrt[2 + 3*x + x^2]", //
				636);
	}

	// {650}
	public void test1055() {
		check(//
				"Integrate[1/((d + e*x)*Sqrt[(-(c*d^2) + b*d*e)/e^2 + b*x + c*x^2]), x]", //
				"(2*e*Sqrt[-((d*(c*d - b*e))/e^2) + b*x + c*x^2])/((2*c*d - b*e)*(d + e*x))", //
				650);
	}

	// {750}
	public void test1056() {
		check(//
				"Integrate[1/((d + e*x)*(c^2*d^2 - b*c*d*e + b^2*e^2 + 3*b*c*e^2*x + 3*c^2*e^2*x^2)^(1/3)), x]", //
				"-(ArcTan[1/Sqrt[3] + (2*(c*d - b*e - c*e*x))/(Sqrt[3]*(2*c*d - b*e)^(1/3)*(c^2*d^2 - b*c*d*e + b^2*e^2 + 3*b*c*e^2*x + 3*c^2*e^2*x^2)^(1/3))]/(Sqrt[3]*e*(2*c*d - b*e)^(2/3))) - Log[d + e*x]/(2*e*(2*c*d - b*e)^(2/3)) + Log[3*c*e^2*(c*d - b*e) - 3*c^2*e^3*x - 3*c*e^2*(2*c*d - b*e)^(1/3)*(c^2*d^2 - b*c*d*e + b^2*e^2 + 3*b*c*e^2*x + 3*c^2*e^2*x^2)^(1/3)]/(2*e*(2*c*d - b*e)^(2/3))", //
				750);
	}

	// {750}
	public void test1057() {
		check(//
				"Integrate[1/((2 + 3*x)*(52 - 54*x + 27*x^2)^(1/3)), x]", //
				"-ArcTan[1/Sqrt[3] + (2^(2/3)*(8 - 3*x))/(Sqrt[3]*5^(1/3)*(52 - 54*x + 27*x^2)^(1/3))]/(3*Sqrt[3]*10^(2/3)) - Log[2 + 3*x]/(6*10^(2/3)) + Log[216 - 81*x - 27*10^(1/3)*(52 - 54*x + 27*x^2)^(1/3)]/(6*10^(2/3))", //
				750);
	}

	// {752}
	public void test1058() {
		check(//
				"Integrate[1/((2 + 3*x)*(28 + 54*x + 27*x^2)^(1/3)), x]", //
				"-ArcTan[1/Sqrt[3] + (2^(2/3)*(4 + 3*x))/(Sqrt[3]*(28 + 54*x + 27*x^2)^(1/3))]/(3*2^(2/3)*Sqrt[3]) - Log[2 + 3*x]/(6*2^(2/3)) + Log[-108 - 81*x + 27*2^(1/3)*(28 + 54*x + 27*x^2)^(1/3)]/(6*2^(2/3))", //
				752);
	}

	// {726}
	public void test1059() {
		check(//
				"Integrate[1/((d + e*x)^(3/2)*(a + b*x + c*x^2)^(1/4)), x]", //
				"(2*(b - Sqrt[b^2 - 4*a*c] + 2*c*x)*(((2*c*d - (b - Sqrt[b^2 - 4*a*c])*e)*(b + Sqrt[b^2 - 4*a*c] + 2*c*x))/((2*c*d - (b + Sqrt[b^2 - 4*a*c])*e)*(b - Sqrt[b^2 - 4*a*c] + 2*c*x)))^(1/4)*Hypergeometric2F1[-1/2, 1/4, 1/2, (-4*c*Sqrt[b^2 - 4*a*c]*(d + e*x))/((2*c*d - (b + Sqrt[b^2 - 4*a*c])*e)*(b - Sqrt[b^2 - 4*a*c] + 2*c*x))])/((2*c*d - b*e + Sqrt[b^2 - 4*a*c]*e)*Sqrt[d + e*x]*(a + b*x + c*x^2)^(1/4))", //
				726);
	}

	// {624}
	public void test1060() {
		check(//
				"Integrate[(a + b*x + c*x^2)^p, x]", //
				"-((2^(1 + p)*(-((b - Sqrt[b^2 - 4*a*c] + 2*c*x)/Sqrt[b^2 - 4*a*c]))^(-1 - p)*(a + b*x + c*x^2)^(1 + p)*Hypergeometric2F1[-p, 1 + p, 2 + p, (b + Sqrt[b^2 - 4*a*c] + 2*c*x)/(2*Sqrt[b^2 - 4*a*c])])/(Sqrt[b^2 - 4*a*c]*(1 + p)))", //
				624);
	}

	// {726}
	public void test1061() {
		check(//
				"Integrate[(d + e*x)^(-2 - 2*p)*(a + b*x + c*x^2)^p, x]", //
				"((b - Sqrt[b^2 - 4*a*c] + 2*c*x)*(d + e*x)^(-1 - 2*p)*(a + b*x + c*x^2)^p*Hypergeometric2F1[-1 - 2*p, -p, -2*p, (-4*c*Sqrt[b^2 - 4*a*c]*(d + e*x))/((2*c*d - (b + Sqrt[b^2 - 4*a*c])*e)*(b - Sqrt[b^2 - 4*a*c] + 2*c*x))])/((2*c*d - (b - Sqrt[b^2 - 4*a*c])*e)*(1 + 2*p)*(((2*c*d - (b - Sqrt[b^2 - 4*a*c])*e)*(b + Sqrt[b^2 - 4*a*c] + 2*c*x))/((2*c*d - (b + Sqrt[b^2 - 4*a*c])*e)*(b - Sqrt[b^2 - 4*a*c] + 2*c*x)))^p)", //
				726);
	}

	// {636}
	public void test1062() {
		check(//
				"Integrate[(A + B*x)/(b*x + c*x^2)^(3/2), x]", //
				"(-2*(A*b - (b*B - 2*A*c)*x))/(b^2*Sqrt[b*x + c*x^2])", //
				636);
	}

	// {763}
	public void test1063() {
		check(//
				"Integrate[x^(1 + p)*(2*b + 3*c*x)*(b*x + c*x^2)^p, x]", //
				"(x^(1 + p)*(b*x + c*x^2)^(1 + p))/(1 + p)", //
				763);
	}

	// {637}
	public void test1064() {
		check(//
				"Integrate[(A + B*x)/(a + c*x^2)^(3/2), x]", //
				"-((a*B - A*c*x)/(a*c*Sqrt[a + c*x^2]))", //
				637);
	}

	// {628}
	public void test1065() {
		check(//
				"Integrate[(5 + 2*x)/(4 + 5*x + x^2), x]", //
				"Log[4 + 5*x + x^2]", //
				628);
	}

	// {629}
	public void test1066() {
		check(//
				"Integrate[(3 + 2*x)/(13 + 12*x + 4*x^2)^2, x]", //
				"-1/(4*(13 + 12*x + 4*x^2))", //
				629);
	}

	// {31}
	public void test1067() {
		check(//
				"Integrate[(A + B*x)^(-1), x]", //
				"Log[A + B*x]/B", //
				31);
	}

	// {636}
	public void test1068() {
		check(//
				"Integrate[(A + B*x)/(a + b*x + c*x^2)^(3/2), x]", //
				"(-2*(A*b - 2*a*B - (b*B - 2*A*c)*x))/((b^2 - 4*a*c)*Sqrt[a + b*x + c*x^2])", //
				636);
	}

	// {636}
	public void test1069() {
		check(//
				"Integrate[(A + B*x)/(b*x + c*x^2)^(3/2), x]", //
				"(-2*(A*b - (b*B - 2*A*c)*x))/(b^2*Sqrt[b*x + c*x^2])", //
				636);
	}

	// {637}
	public void test1070() {
		check(//
				"Integrate[(5 - x)/(2 + 3*x^2)^(3/2), x]", //
				"(2 + 15*x)/(6*Sqrt[2 + 3*x^2])", //
				637);
	}

	// {803}
	public void test1071() {
		check(//
				"Integrate[(-(a*e) + c*d*x)*(d + e*x)^(-3 - 2*p)*(a + c*x^2)^p, x]", //
				"(a + c*x^2)^(1 + p)/(2*(1 + p)*(d + e*x)^(2*(1 + p)))", //
				803);
	}

	// {629}
	public void test1072() {
		check(//
				"Integrate[(b + 2*c*x)*(a + b*x + c*x^2), x]", //
				"(a + b*x + c*x^2)^2/2", //
				629);
	}

	// {629}
	public void test1073() {
		check(//
				"Integrate[(b + 2*c*x)*(a + b*x + c*x^2)^2, x]", //
				"(a + b*x + c*x^2)^3/3", //
				629);
	}

	// {629}
	public void test1074() {
		check(//
				"Integrate[(b + 2*c*x)*(a + b*x + c*x^2)^3, x]", //
				"(a + b*x + c*x^2)^4/4", //
				629);
	}

	// {628}
	public void test1075() {
		check(//
				"Integrate[(b + 2*c*x)/(a + b*x + c*x^2), x]", //
				"Log[a + b*x + c*x^2]", //
				628);
	}

	// {629}
	public void test1076() {
		check(//
				"Integrate[(b + 2*c*x)/(a + b*x + c*x^2)^2, x]", //
				"-(a + b*x + c*x^2)^(-1)", //
				629);
	}

	// {629}
	public void test1077() {
		check(//
				"Integrate[(b + 2*c*x)/(a + b*x + c*x^2)^3, x]", //
				"-1/(2*(a + b*x + c*x^2)^2)", //
				629);
	}

	// {629}
	public void test1078() {
		check(//
				"Integrate[(b + 2*c*x)*Sqrt[a + b*x + c*x^2], x]", //
				"(2*(a + b*x + c*x^2)^(3/2))/3", //
				629);
	}

	// {629}
	public void test1079() {
		check(//
				"Integrate[(b + 2*c*x)*(a + b*x + c*x^2)^(3/2), x]", //
				"(2*(a + b*x + c*x^2)^(5/2))/5", //
				629);
	}

	// {629}
	public void test1080() {
		check(//
				"Integrate[(b + 2*c*x)*(a + b*x + c*x^2)^(5/2), x]", //
				"(2*(a + b*x + c*x^2)^(7/2))/7", //
				629);
	}

	// {629}
	public void test1081() {
		check(//
				"Integrate[(b + 2*c*x)/Sqrt[a + b*x + c*x^2], x]", //
				"2*Sqrt[a + b*x + c*x^2]", //
				629);
	}

	// {629}
	public void test1082() {
		check(//
				"Integrate[(b + 2*c*x)/(a + b*x + c*x^2)^(3/2), x]", //
				"-2/Sqrt[a + b*x + c*x^2]", //
				629);
	}

	// {629}
	public void test1083() {
		check(//
				"Integrate[(b + 2*c*x)/(a + b*x + c*x^2)^(5/2), x]", //
				"-2/(3*(a + b*x + c*x^2)^(3/2))", //
				629);
	}

	// {629}
	public void test1084() {
		check(//
				"Integrate[(a + b*x)*Sqrt[a^2 + 2*a*b*x + b^2*x^2], x]", //
				"(a^2 + 2*a*b*x + b^2*x^2)^(3/2)/(3*b)", //
				629);
	}

	// {767}
	public void test1085() {
		check(//
				"Integrate[((a + b*x)*Sqrt[a^2 + 2*a*b*x + b^2*x^2])/(d + e*x)^4, x]", //
				"(a^2 + 2*a*b*x + b^2*x^2)^(3/2)/(3*(b*d - a*e)*(d + e*x)^3)", //
				767);
	}

	// {629}
	public void test1086() {
		check(//
				"Integrate[(a + b*x)*(a^2 + 2*a*b*x + b^2*x^2)^(3/2), x]", //
				"(a^2 + 2*a*b*x + b^2*x^2)^(5/2)/(5*b)", //
				629);
	}

	// {767}
	public void test1087() {
		check(//
				"Integrate[((a + b*x)*(a^2 + 2*a*b*x + b^2*x^2)^(3/2))/(d + e*x)^6, x]", //
				"(a^2 + 2*a*b*x + b^2*x^2)^(5/2)/(5*(b*d - a*e)*(d + e*x)^5)", //
				767);
	}

	// {629}
	public void test1088() {
		check(//
				"Integrate[(a + b*x)*(a^2 + 2*a*b*x + b^2*x^2)^(5/2), x]", //
				"(a^2 + 2*a*b*x + b^2*x^2)^(7/2)/(7*b)", //
				629);
	}

	// {767}
	public void test1089() {
		check(//
				"Integrate[((a + b*x)*(a^2 + 2*a*b*x + b^2*x^2)^(5/2))/(d + e*x)^8, x]", //
				"(a^2 + 2*a*b*x + b^2*x^2)^(7/2)/(7*(b*d - a*e)*(d + e*x)^7)", //
				767);
	}

	// {629}
	public void test1090() {
		check(//
				"Integrate[(a + b*x)/Sqrt[a^2 + 2*a*b*x + b^2*x^2], x]", //
				"Sqrt[a^2 + 2*a*b*x + b^2*x^2]/b", //
				629);
	}

	// {767}
	public void test1091() {
		check(//
				"Integrate[(a + b*x)/((d + e*x)^2*Sqrt[a^2 + 2*a*b*x + b^2*x^2]), x]", //
				"Sqrt[a^2 + 2*a*b*x + b^2*x^2]/((b*d - a*e)*(d + e*x))", //
				767);
	}

	// {629}
	public void test1092() {
		check(//
				"Integrate[(a + b*x)/(a^2 + 2*a*b*x + b^2*x^2)^(3/2), x]", //
				"-(1/(b*Sqrt[a^2 + 2*a*b*x + b^2*x^2]))", //
				629);
	}

	// {767}
	public void test1093() {
		check(//
				"Integrate[((a + b*x)*(d + e*x)^2)/(a^2 + 2*a*b*x + b^2*x^2)^(5/2), x]", //
				"-(d + e*x)^3/(3*(b*d - a*e)*(a^2 + 2*a*b*x + b^2*x^2)^(3/2))", //
				767);
	}

	// {629}
	public void test1094() {
		check(//
				"Integrate[(a + b*x)/(a^2 + 2*a*b*x + b^2*x^2)^(5/2), x]", //
				"-1/(3*b*(a^2 + 2*a*b*x + b^2*x^2)^(3/2))", //
				629);
	}

	// {767}
	public void test1095() {
		check(//
				"Integrate[(a*c + b*c*x)*(d + e*x)^(-3 - 2*p)*(a^2 + 2*a*b*x + b^2*x^2)^p, x]", //
				"(c*(a^2 + 2*a*b*x + b^2*x^2)^(1 + p))/(2*(b*d - a*e)*(1 + p)*(d + e*x)^(2*(1 + p)))", //
				767);
	}

	// {629}
	public void test1096() {
		check(//
				"Integrate[(a + b*x)*(a^2 + 2*a*b*x + b^2*x^2)^p, x]", //
				"(a^2 + 2*a*b*x + b^2*x^2)^(1 + p)/(2*b*(1 + p))", //
				629);
	}

	// {786}
	public void test1097() {
		check(//
				"Integrate[(d + e*x)^m*(c*d*m - b*e*(1 + m + p) - c*e*(2 + m + 2*p)*x)*(c*d^2 - b*d*e - b*e^2*x - c*e^2*x^2)^p, x]", //
				"((d + e*x)^m*(d*(c*d - b*e) - b*e^2*x - c*e^2*x^2)^(1 + p))/e", //
				786);
	}

	// {786}
	public void test1098() {
		check(//
				"Integrate[(d + e*x)^(-3 - 2*p)*(f + g*x)*(d*(e*f + d*g + d*g*p) + e*(e*f + 3*d*g + 2*d*g*p)*x + e^2*g*(2 + p)*x^2)^p, x]", //
				"-(((d + e*x)^(-3 - 2*p)*(d*(e*f + d*g*(1 + p)) + e*(e*f + d*g*(3 + 2*p))*x + e^2*g*(2 + p)*x^2)^(1 + p))/(e^2*(2 + p)))", //
				786);
	}

	// {636}
	public void test1099() {
		check(//
				"Integrate[(A + B*x)/(a + b*x + c*x^2)^(3/2), x]", //
				"(-2*(A*b - 2*a*B - (b*B - 2*A*c)*x))/((b^2 - 4*a*c)*Sqrt[a + b*x + c*x^2])", //
				636);
	}

	// {636}
	public void test1100() {
		check(//
				"Integrate[(5 - x)/(2 + 5*x + 3*x^2)^(3/2), x]", //
				"(-2*(29 + 35*x))/Sqrt[2 + 5*x + 3*x^2]", //
				636);
	}

	// {651}
	public void test1101() {
		check(//
				"Integrate[1/((d + e*x)*Sqrt[d^2 - e^2*x^2]), x]", //
				"-(Sqrt[d^2 - e^2*x^2]/(d*e*(d + e*x)))", //
				651);
	}

	// {651}
	public void test1102() {
		check(//
				"Integrate[1/((1 + a*x)*Sqrt[1 - a^2*x^2]), x]", //
				"-(Sqrt[1 - a^2*x^2]/(a*(1 + a*x)))", //
				651);
	}

	// {650}
	public void test1103() {
		check(//
				"Integrate[1/((d + e*x)*Sqrt[a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2]), x]", //
				"(2*Sqrt[a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2])/((c*d^2 - a*e^2)*(d + e*x))", //
				650);
	}

	// {913}
	public void test1104() {
		check(//
				"Integrate[x^2*Sqrt[1 + x]*Sqrt[1 - x + x^2], x]", //
				"(2*(1 + x)^(3/2)*(1 - x + x^2)^(3/2))/9", //
				913);
	}

	// {913}
	public void test1105() {
		check(//
				"Integrate[x^2*(1 + x)^(3/2)*(1 - x + x^2)^(3/2), x]", //
				"(2*(1 + x)^(5/2)*(1 - x + x^2)^(5/2))/15", //
				913);
	}

	// {913}
	public void test1106() {
		check(//
				"Integrate[x^2/(Sqrt[1 + x]*Sqrt[1 - x + x^2]), x]", //
				"(2*Sqrt[1 + x]*Sqrt[1 - x + x^2])/3", //
				913);
	}

	// {913}
	public void test1107() {
		check(//
				"Integrate[x^2/((1 + x)^(3/2)*(1 - x + x^2)^(3/2)), x]", //
				"-2/(3*Sqrt[1 + x]*Sqrt[1 - x + x^2])", //
				913);
	}

	// {913}
	public void test1108() {
		check(//
				"Integrate[x^2/((1 + x)^(5/2)*(1 - x + x^2)^(5/2)), x]", //
				"-2/(9*(1 + x)^(3/2)*(1 - x + x^2)^(3/2))", //
				913);
	}

	// {384}
	public void test1109() {
		check(//
				"Integrate[(-1 + 2*x^2)/(Sqrt[-1 + x]*Sqrt[1 + x]), x]", //
				"Sqrt[-1 + x]*x*Sqrt[1 + x]", //
				384);
	}

	// {648}
	public void test1110() {
		check(//
				"Integrate[Sqrt[d + e*x]/Sqrt[a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2], x]", //
				"(2*Sqrt[a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2])/(c*d*Sqrt[d + e*x])", //
				648);
	}

	// {648}
	public void test1111() {
		check(//
				"Integrate[(d + e*x)^(3/2)/(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^(3/2), x]", //
				"(-2*Sqrt[d + e*x])/(c*d*Sqrt[a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2])", //
				648);
	}

	// {648}
	public void test1112() {
		check(//
				"Integrate[(d + e*x)^(5/2)/(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^(5/2), x]", //
				"(-2*(d + e*x)^(3/2))/(3*c*d*(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^(3/2))", //
				648);
	}

	// {648}
	public void test1113() {
		check(//
				"Integrate[Sqrt[a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2]/Sqrt[d + e*x], x]", //
				"(2*(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^(3/2))/(3*c*d*(d + e*x)^(3/2))", //
				648);
	}

	// {648}
	public void test1114() {
		check(//
				"Integrate[(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^(3/2)/(d + e*x)^(3/2), x]", //
				"(2*(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^(5/2))/(5*c*d*(d + e*x)^(5/2))", //
				648);
	}

	// {648}
	public void test1115() {
		check(//
				"Integrate[(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^(5/2)/(d + e*x)^(5/2), x]", //
				"(2*(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^(7/2))/(7*c*d*(d + e*x)^(7/2))", //
				648);
	}

	// {860}
	public void test1116() {
		check(//
				"Integrate[Sqrt[d + e*x]/((f + g*x)^(3/2)*Sqrt[a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2]), x]", //
				"(2*Sqrt[a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2])/((c*d*f - a*e*g)*Sqrt[d + e*x]*Sqrt[f + g*x])", //
				860);
	}

	// {860}
	public void test1117() {
		check(//
				"Integrate[(d + e*x)^(3/2)/(Sqrt[f + g*x]*(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^(3/2)), x]", //
				"(-2*Sqrt[d + e*x]*Sqrt[f + g*x])/((c*d*f - a*e*g)*Sqrt[a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2])", //
				860);
	}

	// {860}
	public void test1118() {
		check(//
				"Integrate[((d + e*x)^(5/2)*Sqrt[f + g*x])/(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^(5/2), x]", //
				"(-2*(d + e*x)^(3/2)*(f + g*x)^(3/2))/(3*(c*d*f - a*e*g)*(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^(3/2))", //
				860);
	}

	// {860}
	public void test1119() {
		check(//
				"Integrate[Sqrt[a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2]/(Sqrt[d + e*x]*(f + g*x)^(5/2)), x]", //
				"(2*(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^(3/2))/(3*(c*d*f - a*e*g)*(d + e*x)^(3/2)*(f + g*x)^(3/2))", //
				860);
	}

	// {860}
	public void test1120() {
		check(//
				"Integrate[(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^(3/2)/((d + e*x)^(3/2)*(f + g*x)^(7/2)), x]", //
				"(2*(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^(5/2))/(5*(c*d*f - a*e*g)*(d + e*x)^(5/2)*(f + g*x)^(5/2))", //
				860);
	}

	// {860}
	public void test1121() {
		check(//
				"Integrate[(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^(5/2)/((d + e*x)^(5/2)*(f + g*x)^(9/2)), x]", //
				"(2*(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^(7/2))/(7*(c*d*f - a*e*g)*(d + e*x)^(7/2)*(f + g*x)^(7/2))", //
				860);
	}

	// {648}
	public void test1122() {
		check(//
				"Integrate[(d + e*x)^m/(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^m, x]", //
				"((d + e*x)^(-1 + m)*(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^(1 - m))/(c*d*(1 - m))", //
				648);
	}

	// {858}
	public void test1123() {
		check(//
				"Integrate[((a*e + c*d*x)^n*(d + e*x)^m)/(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^m, x]", //
				"((a*e + c*d*x)^n*(d + e*x)^(-1 + m)*(a*d*e + (c*d^2 + a*e^2)*x + c*d*e*x^2)^(1 - m))/(c*d*(1 - m + n))", //
				858);
	}

	// {926}
	public void test1124() {
		check(//
				"Integrate[Sqrt[d + e*x]/(Sqrt[f + g*x]*Sqrt[a + b*x + c*x^2]), x]", //
				"(Sqrt[2]*Sqrt[2*c*f - (b + Sqrt[b^2 - 4*a*c])*g]*Sqrt[b - Sqrt[b^2 - 4*a*c] + 2*c*x]*Sqrt[((e*f - d*g)*(b + Sqrt[b^2 - 4*a*c] + 2*c*x))/((2*c*f - (b + Sqrt[b^2 - 4*a*c])*g)*(d + e*x))]*Sqrt[((e*f - d*g)*(2*a + (b + Sqrt[b^2 - 4*a*c])*x))/((b*f + Sqrt[b^2 - 4*a*c]*f - 2*a*g)*(d + e*x))]*(d + e*x)*EllipticPi[(e*(2*c*f - (b + Sqrt[b^2 - 4*a*c])*g))/((2*c*d - (b + Sqrt[b^2 - 4*a*c])*e)*g), ArcSin[(Sqrt[2*c*d - (b + Sqrt[b^2 - 4*a*c])*e]*Sqrt[f + g*x])/(Sqrt[2*c*f - (b + Sqrt[b^2 - 4*a*c])*g]*Sqrt[d + e*x])], ((b*d + Sqrt[b^2 - 4*a*c]*d - 2*a*e)*(2*c*f - (b + Sqrt[b^2 - 4*a*c])*g))/((2*c*d - (b + Sqrt[b^2 - 4*a*c])*e)*(b*f + Sqrt[b^2 - 4*a*c]*f - 2*a*g))])/(Sqrt[2*c*d - (b + Sqrt[b^2 - 4*a*c])*e]*g*Sqrt[(2*a*c)/(b + Sqrt[b^2 - 4*a*c]) + c*x]*Sqrt[a + b*x + c*x^2])", //
				926);
	}

	// {629}
	public void test1125() {
		check(//
				"Integrate[(-3 + 2*x)*(-3*x + x^2)^(2/3), x]", //
				"(3*(-3*x + x^2)^(5/3))/5", //
				629);
	}

	// {1588}
	public void test1126() {
		check(//
				"Integrate[((-3 + x)*x)^(2/3)*(-3 + 2*x), x]", //
				"(3*(-((3 - x)*x))^(5/3))/5", //
				1588);
	}

	// {1590}
	public void test1127() {
		check(//
				"Integrate[((a + b*x^2)*(-(a*d) + 4*b*c*x + 3*b*d*x^2))/(c + d*x)^2, x]", //
				"(a + b*x^2)^2/(c + d*x)", //
				1590);
	}

	// {1590}
	public void test1128() {
		check(//
				"Integrate[((a + b*x^2)*(-(a*d) + b*x*(4*c + 3*d*x)))/(c + d*x)^2, x]", //
				"(a + b*x^2)^2/(c + d*x)", //
				1590);
	}

	// {1590}
	public void test1129() {
		check(//
				"Integrate[((a + b*x^2)^2*(-(a*d) + 6*b*c*x + 5*b*d*x^2))/(c + d*x)^2, x]", //
				"(a + b*x^2)^3/(c + d*x)", //
				1590);
	}

	// {1590}
	public void test1130() {
		check(//
				"Integrate[((a + b*x^2)^2*(-(a*d) + b*x*(6*c + 5*d*x)))/(c + d*x)^2, x]", //
				"(a + b*x^2)^3/(c + d*x)", //
				1590);
	}

	// {1588}
	public void test1131() {
		check(//
				"Integrate[(1 - x^2)/(1 + x + x^2)^2, x]", //
				"x/(1 + x + x^2)", //
				1588);
	}

	// {1588}
	public void test1132() {
		check(//
				"Integrate[(-1 + 2*x + 5*x^2)/(1 + x + x^2)^4, x]", //
				"-(x/(1 + x + x^2)^3)", //
				1588);
	}

	// {221}
	public void test1133() {
		check(//
				"Integrate[1/Sqrt[2 - 3*x^4], x]", //
				"EllipticF[ArcSin[(3/2)^(1/4)*x], -1]/6^(1/4)", //
				221);
	}

	// {221}
	public void test1134() {
		check(//
				"Integrate[1/Sqrt[3 - 2*x^4], x]", //
				"EllipticF[ArcSin[(2/3)^(1/4)*x], -1]/6^(1/4)", //
				221);
	}

	// {1097}
	public void test1135() {
		check(//
				"Integrate[1/Sqrt[-2 + 5*x^2 + 3*x^4], x]", //
				"(Sqrt[2 + x^2]*Sqrt[-1 + 3*x^2]*EllipticF[ArcSin[(Sqrt[7/2]*x)/Sqrt[-1 + 3*x^2]], 6/7])/(Sqrt[7]*Sqrt[-2 + 5*x^2 + 3*x^4])", //
				1097);
	}

	// {1098}
	public void test1136() {
		check(//
				"Integrate[1/Sqrt[-2 + 4*x^2 + 3*x^4], x]", //
				"(Sqrt[(2 - (2 - Sqrt[10])*x^2)/(2 - (2 + Sqrt[10])*x^2)]*Sqrt[-2 + (2 + Sqrt[10])*x^2]*EllipticF[ArcSin[(2^(3/4)*5^(1/4)*x)/Sqrt[-2 + (2 + Sqrt[10])*x^2]], (5 + Sqrt[10])/10])/(2*10^(1/4)*Sqrt[(2 - (2 + Sqrt[10])*x^2)^(-1)]*Sqrt[-2 + 4*x^2 + 3*x^4])", //
				1098);
	}

	// {1098}
	public void test1137() {
		check(//
				"Integrate[1/Sqrt[-2 + 3*x^2 + 3*x^4], x]", //
				"(Sqrt[(4 - (3 - Sqrt[33])*x^2)/(4 - (3 + Sqrt[33])*x^2)]*Sqrt[-4 + (3 + Sqrt[33])*x^2]*EllipticF[ArcSin[(Sqrt[2]*33^(1/4)*x)/Sqrt[-4 + (3 + Sqrt[33])*x^2]], (11 + Sqrt[33])/22])/(2*Sqrt[2]*33^(1/4)*Sqrt[(4 - (3 + Sqrt[33])*x^2)^(-1)]*Sqrt[-2 + 3*x^2 + 3*x^4])", //
				1098);
	}

	// {1098}
	public void test1138() {
		check(//
				"Integrate[1/Sqrt[-2 + 2*x^2 + 3*x^4], x]", //
				"(Sqrt[(2 - (1 - Sqrt[7])*x^2)/(2 - (1 + Sqrt[7])*x^2)]*Sqrt[-2 + (1 + Sqrt[7])*x^2]*EllipticF[ArcSin[(Sqrt[2]*7^(1/4)*x)/Sqrt[-2 + (1 + Sqrt[7])*x^2]], (7 + Sqrt[7])/14])/(2*7^(1/4)*Sqrt[(2 - (1 + Sqrt[7])*x^2)^(-1)]*Sqrt[-2 + 2*x^2 + 3*x^4])", //
				1098);
	}

	// {1097}
	public void test1139() {
		check(//
				"Integrate[1/Sqrt[-2 + x^2 + 3*x^4], x]", //
				"(Sqrt[1 + x^2]*Sqrt[-2 + 3*x^2]*EllipticF[ArcSin[(Sqrt[5]*x)/Sqrt[-2 + 3*x^2]], 3/5])/(Sqrt[5]*Sqrt[-2 + x^2 + 3*x^4])", //
				1097);
	}

	// {223}
	public void test1140() {
		check(//
				"Integrate[1/Sqrt[-2 + 3*x^4], x]", //
				"(Sqrt[-2 + Sqrt[6]*x^2]*Sqrt[(2 + Sqrt[6]*x^2)/(2 - Sqrt[6]*x^2)]*EllipticF[ArcSin[(2^(3/4)*3^(1/4)*x)/Sqrt[-2 + Sqrt[6]*x^2]], 1/2])/(2*6^(1/4)*Sqrt[(2 - Sqrt[6]*x^2)^(-1)]*Sqrt[-2 + 3*x^4])", //
				223);
	}

	// {1097}
	public void test1141() {
		check(//
				"Integrate[1/Sqrt[-2 - x^2 + 3*x^4], x]", //
				"(Sqrt[-1 + x^2]*Sqrt[2 + 3*x^2]*EllipticF[ArcSin[(Sqrt[5/2]*x)/Sqrt[-1 + x^2]], 2/5])/(Sqrt[5]*Sqrt[-2 - x^2 + 3*x^4])", //
				1097);
	}

	// {1098}
	public void test1142() {
		check(//
				"Integrate[1/Sqrt[-2 - 2*x^2 + 3*x^4], x]", //
				"(Sqrt[-2 - (1 - Sqrt[7])*x^2]*Sqrt[(2 + (1 + Sqrt[7])*x^2)/(2 + (1 - Sqrt[7])*x^2)]*EllipticF[ArcSin[(Sqrt[2]*7^(1/4)*x)/Sqrt[-2 - (1 - Sqrt[7])*x^2]], (7 - Sqrt[7])/14])/(2*7^(1/4)*Sqrt[(2 + (1 - Sqrt[7])*x^2)^(-1)]*Sqrt[-2 - 2*x^2 + 3*x^4])", //
				1098);
	}

	// {1098}
	public void test1143() {
		check(//
				"Integrate[1/Sqrt[-2 - 3*x^2 + 3*x^4], x]", //
				"(Sqrt[-4 - (3 - Sqrt[33])*x^2]*Sqrt[(4 + (3 + Sqrt[33])*x^2)/(4 + (3 - Sqrt[33])*x^2)]*EllipticF[ArcSin[(Sqrt[2]*33^(1/4)*x)/Sqrt[-4 - (3 - Sqrt[33])*x^2]], (11 - Sqrt[33])/22])/(2*Sqrt[2]*33^(1/4)*Sqrt[(4 + (3 - Sqrt[33])*x^2)^(-1)]*Sqrt[-2 - 3*x^2 + 3*x^4])", //
				1098);
	}

	// {1098}
	public void test1144() {
		check(//
				"Integrate[1/Sqrt[-2 - 4*x^2 + 3*x^4], x]", //
				"(Sqrt[-2 - (2 - Sqrt[10])*x^2]*Sqrt[(2 + (2 + Sqrt[10])*x^2)/(2 + (2 - Sqrt[10])*x^2)]*EllipticF[ArcSin[(2^(3/4)*5^(1/4)*x)/Sqrt[-2 - (2 - Sqrt[10])*x^2]], (5 - Sqrt[10])/10])/(2*10^(1/4)*Sqrt[(2 + (2 - Sqrt[10])*x^2)^(-1)]*Sqrt[-2 - 4*x^2 + 3*x^4])", //
				1098);
	}

	// {1097}
	public void test1145() {
		check(//
				"Integrate[1/Sqrt[-2 - 5*x^2 + 3*x^4], x]", //
				"(Sqrt[-2 + x^2]*Sqrt[1 + 3*x^2]*EllipticF[ArcSin[(Sqrt[7]*x)/Sqrt[-2 + x^2]], 1/7])/(Sqrt[7]*Sqrt[-2 - 5*x^2 + 3*x^4])", //
				1097);
	}

	// {1098}
	public void test1146() {
		check(//
				"Integrate[1/Sqrt[-3 + 7*x^2 + 2*x^4], x]", //
				"(Sqrt[(6 - (7 - Sqrt[73])*x^2)/(6 - (7 + Sqrt[73])*x^2)]*Sqrt[-6 + (7 + Sqrt[73])*x^2]*EllipticF[ArcSin[(Sqrt[2]*73^(1/4)*x)/Sqrt[-6 + (7 + Sqrt[73])*x^2]], (73 + 7*Sqrt[73])/146])/(2*Sqrt[3]*73^(1/4)*Sqrt[(6 - (7 + Sqrt[73])*x^2)^(-1)]*Sqrt[-3 + 7*x^2 + 2*x^4])", //
				1098);
	}

	// {1098}
	public void test1147() {
		check(//
				"Integrate[1/Sqrt[-3 + 6*x^2 + 2*x^4], x]", //
				"(Sqrt[(3 - (3 - Sqrt[15])*x^2)/(3 - (3 + Sqrt[15])*x^2)]*Sqrt[-3 + (3 + Sqrt[15])*x^2]*EllipticF[ArcSin[(Sqrt[2]*15^(1/4)*x)/Sqrt[-3 + (3 + Sqrt[15])*x^2]], (5 + Sqrt[15])/10])/(Sqrt[2]*3^(3/4)*5^(1/4)*Sqrt[(3 - (3 + Sqrt[15])*x^2)^(-1)]*Sqrt[-3 + 6*x^2 + 2*x^4])", //
				1098);
	}

	// {1097}
	public void test1148() {
		check(//
				"Integrate[1/Sqrt[-3 + 5*x^2 + 2*x^4], x]", //
				"(Sqrt[3 + x^2]*Sqrt[-1 + 2*x^2]*EllipticF[ArcSin[(Sqrt[7/3]*x)/Sqrt[-1 + 2*x^2]], 6/7])/(Sqrt[7]*Sqrt[-3 + 5*x^2 + 2*x^4])", //
				1097);
	}

	// {1098}
	public void test1149() {
		check(//
				"Integrate[1/Sqrt[-3 + 4*x^2 + 2*x^4], x]", //
				"(Sqrt[(3 - (2 - Sqrt[10])*x^2)/(3 - (2 + Sqrt[10])*x^2)]*Sqrt[-3 + (2 + Sqrt[10])*x^2]*EllipticF[ArcSin[(2^(3/4)*5^(1/4)*x)/Sqrt[-3 + (2 + Sqrt[10])*x^2]], (5 + Sqrt[10])/10])/(2^(3/4)*Sqrt[3]*5^(1/4)*Sqrt[(3 - (2 + Sqrt[10])*x^2)^(-1)]*Sqrt[-3 + 4*x^2 + 2*x^4])", //
				1098);
	}

	// {1098}
	public void test1150() {
		check(//
				"Integrate[1/Sqrt[-3 + 3*x^2 + 2*x^4], x]", //
				"(Sqrt[(6 - (3 - Sqrt[33])*x^2)/(6 - (3 + Sqrt[33])*x^2)]*Sqrt[-6 + (3 + Sqrt[33])*x^2]*EllipticF[ArcSin[(Sqrt[2]*33^(1/4)*x)/Sqrt[-6 + (3 + Sqrt[33])*x^2]], (11 + Sqrt[33])/22])/(2*3^(3/4)*11^(1/4)*Sqrt[(6 - (3 + Sqrt[33])*x^2)^(-1)]*Sqrt[-3 + 3*x^2 + 2*x^4])", //
				1098);
	}

	// {1098}
	public void test1151() {
		check(//
				"Integrate[1/Sqrt[-3 + 2*x^2 + 2*x^4], x]", //
				"(Sqrt[(3 - (1 - Sqrt[7])*x^2)/(3 - (1 + Sqrt[7])*x^2)]*Sqrt[-3 + (1 + Sqrt[7])*x^2]*EllipticF[ArcSin[(Sqrt[2]*7^(1/4)*x)/Sqrt[-3 + (1 + Sqrt[7])*x^2]], (7 + Sqrt[7])/14])/(Sqrt[6]*7^(1/4)*Sqrt[(3 - (1 + Sqrt[7])*x^2)^(-1)]*Sqrt[-3 + 2*x^2 + 2*x^4])", //
				1098);
	}

	// {1097}
	public void test1152() {
		check(//
				"Integrate[1/Sqrt[-3 + x^2 + 2*x^4], x]", //
				"(Sqrt[-1 + x^2]*Sqrt[3 + 2*x^2]*EllipticF[ArcSin[(Sqrt[5/3]*x)/Sqrt[-1 + x^2]], 3/5])/(Sqrt[5]*Sqrt[-3 + x^2 + 2*x^4])", //
				1097);
	}

	// {223}
	public void test1153() {
		check(//
				"Integrate[1/Sqrt[-3 + 2*x^4], x]", //
				"(Sqrt[-3 + Sqrt[6]*x^2]*Sqrt[(3 + Sqrt[6]*x^2)/(3 - Sqrt[6]*x^2)]*EllipticF[ArcSin[(2^(3/4)*3^(1/4)*x)/Sqrt[-3 + Sqrt[6]*x^2]], 1/2])/(6^(3/4)*Sqrt[(3 - Sqrt[6]*x^2)^(-1)]*Sqrt[-3 + 2*x^4])", //
				223);
	}

	// {1097}
	public void test1154() {
		check(//
				"Integrate[1/Sqrt[-3 - x^2 + 2*x^4], x]", //
				"(Sqrt[1 + x^2]*Sqrt[-3 + 2*x^2]*EllipticF[ArcSin[(Sqrt[5]*x)/Sqrt[-3 + 2*x^2]], 2/5])/(Sqrt[5]*Sqrt[-3 - x^2 + 2*x^4])", //
				1097);
	}

	// {1098}
	public void test1155() {
		check(//
				"Integrate[1/Sqrt[-3 - 2*x^2 + 2*x^4], x]", //
				"(Sqrt[-3 - (1 - Sqrt[7])*x^2]*Sqrt[(3 + (1 + Sqrt[7])*x^2)/(3 + (1 - Sqrt[7])*x^2)]*EllipticF[ArcSin[(Sqrt[2]*7^(1/4)*x)/Sqrt[-3 - (1 - Sqrt[7])*x^2]], (7 - Sqrt[7])/14])/(Sqrt[6]*7^(1/4)*Sqrt[(3 + (1 - Sqrt[7])*x^2)^(-1)]*Sqrt[-3 - 2*x^2 + 2*x^4])", //
				1098);
	}

	// {1098}
	public void test1156() {
		check(//
				"Integrate[1/Sqrt[-3 - 3*x^2 + 2*x^4], x]", //
				"(Sqrt[-6 - (3 - Sqrt[33])*x^2]*Sqrt[(6 + (3 + Sqrt[33])*x^2)/(6 + (3 - Sqrt[33])*x^2)]*EllipticF[ArcSin[(Sqrt[2]*33^(1/4)*x)/Sqrt[-6 - (3 - Sqrt[33])*x^2]], (11 - Sqrt[33])/22])/(2*3^(3/4)*11^(1/4)*Sqrt[(6 + (3 - Sqrt[33])*x^2)^(-1)]*Sqrt[-3 - 3*x^2 + 2*x^4])", //
				1098);
	}

	// {1098}
	public void test1157() {
		check(//
				"Integrate[1/Sqrt[-3 - 4*x^2 + 2*x^4], x]", //
				"(Sqrt[-3 - (2 - Sqrt[10])*x^2]*Sqrt[(3 + (2 + Sqrt[10])*x^2)/(3 + (2 - Sqrt[10])*x^2)]*EllipticF[ArcSin[(2^(3/4)*5^(1/4)*x)/Sqrt[-3 - (2 - Sqrt[10])*x^2]], (5 - Sqrt[10])/10])/(2^(3/4)*Sqrt[3]*5^(1/4)*Sqrt[(3 + (2 - Sqrt[10])*x^2)^(-1)]*Sqrt[-3 - 4*x^2 + 2*x^4])", //
				1098);
	}

	// {1097}
	public void test1158() {
		check(//
				"Integrate[1/Sqrt[-3 - 5*x^2 + 2*x^4], x]", //
				"(Sqrt[-3 + x^2]*Sqrt[1 + 2*x^2]*EllipticF[ArcSin[(Sqrt[7]*x)/Sqrt[-3 + x^2]], 1/7])/(Sqrt[7]*Sqrt[-3 - 5*x^2 + 2*x^4])", //
				1097);
	}

	// {1100}
	public void test1159() {
		check(//
				"Integrate[1/Sqrt[2 + 5*x^2 + 3*x^4], x]", //
				"((1 + x^2)*Sqrt[(2 + 3*x^2)/(1 + x^2)]*EllipticF[ArcTan[x], -1/2])/(Sqrt[2]*Sqrt[2 + 5*x^2 + 3*x^4])", //
				1100);
	}

//	// {1103}
//	public void test1160() {
//		check(//
//				"Integrate[1/Sqrt[2 + 4*x^2 + 3*x^4], x]", //
//				"((2 + Sqrt[6]*x^2)*Sqrt[(2 + 4*x^2 + 3*x^4)/(2 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(3/2)^(1/4)*x], 1/2 - 1/Sqrt[6]])/(2*6^(1/4)*Sqrt[2 + 4*x^2 + 3*x^4])", //
//				1103);
//	}
//
//	// {1103}
//	public void test1161() {
//		check(//
//				"Integrate[1/Sqrt[2 + 3*x^2 + 3*x^4], x]", //
//				"((2 + Sqrt[6]*x^2)*Sqrt[(2 + 3*x^2 + 3*x^4)/(2 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(3/2)^(1/4)*x], (4 - Sqrt[6])/8])/(2*6^(1/4)*Sqrt[2 + 3*x^2 + 3*x^4])", //
//				1103);
//	}
//
//	// {1103}
//	public void test1162() {
//		check(//
//				"Integrate[1/Sqrt[2 + 2*x^2 + 3*x^4], x]", //
//				"((2 + Sqrt[6]*x^2)*Sqrt[(2 + 2*x^2 + 3*x^4)/(2 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(3/2)^(1/4)*x], (6 - Sqrt[6])/12])/(2*6^(1/4)*Sqrt[2 + 2*x^2 + 3*x^4])", //
//				1103);
//	}
//
//	// {1103}
//	public void test1163() {
//		check(//
//				"Integrate[1/Sqrt[2 + x^2 + 3*x^4], x]", //
//				"((2 + Sqrt[6]*x^2)*Sqrt[(2 + x^2 + 3*x^4)/(2 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(3/2)^(1/4)*x], (12 - Sqrt[6])/24])/(2*6^(1/4)*Sqrt[2 + x^2 + 3*x^4])", //
//				1103);
//	}

	// {220}
	public void test1164() {
		check(//
				"Integrate[1/Sqrt[2 + 3*x^4], x]", //
				"((2 + Sqrt[6]*x^2)*Sqrt[(2 + 3*x^4)/(2 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(3/2)^(1/4)*x], 1/2])/(2*6^(1/4)*Sqrt[2 + 3*x^4])", //
				220);
	}

//	// {1103}
//	public void test1165() {
//		check(//
//				"Integrate[1/Sqrt[2 - x^2 + 3*x^4], x]", //
//				"((2 + Sqrt[6]*x^2)*Sqrt[(2 - x^2 + 3*x^4)/(2 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(3/2)^(1/4)*x], (12 + Sqrt[6])/24])/(2*6^(1/4)*Sqrt[2 - x^2 + 3*x^4])", //
//				1103);
//	}
//
//	// {1103}
//	public void test1166() {
//		check(//
//				"Integrate[1/Sqrt[2 - 2*x^2 + 3*x^4], x]", //
//				"((2 + Sqrt[6]*x^2)*Sqrt[(2 - 2*x^2 + 3*x^4)/(2 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(3/2)^(1/4)*x], (6 + Sqrt[6])/12])/(2*6^(1/4)*Sqrt[2 - 2*x^2 + 3*x^4])", //
//				1103);
//	}
//
//	// {1103}
//	public void test1167() {
//		check(//
//				"Integrate[1/Sqrt[2 - 3*x^2 + 3*x^4], x]", //
//				"((2 + Sqrt[6]*x^2)*Sqrt[(2 - 3*x^2 + 3*x^4)/(2 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(3/2)^(1/4)*x], (4 + Sqrt[6])/8])/(2*6^(1/4)*Sqrt[2 - 3*x^2 + 3*x^4])", //
//				1103);
//	}
//
//	// {1103}
//	public void test1168() {
//		check(//
//				"Integrate[1/Sqrt[2 - 4*x^2 + 3*x^4], x]", //
//				"((2 + Sqrt[6]*x^2)*Sqrt[(2 - 4*x^2 + 3*x^4)/(2 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(3/2)^(1/4)*x], 1/2 + 1/Sqrt[6]])/(2*6^(1/4)*Sqrt[2 - 4*x^2 + 3*x^4])", //
//				1103);
//	}
//
//	// {1096}
//	public void test1169() {
//		check(//
//				"Integrate[1/Sqrt[2 - 5*x^2 + 3*x^4], x]", //
//				"((2 + Sqrt[6]*x^2)*Sqrt[(2 - 5*x^2 + 3*x^4)/(2 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(3/2)^(1/4)*x], (12 + 5*Sqrt[6])/24])/(2*6^(1/4)*Sqrt[2 - 5*x^2 + 3*x^4])", //
//				1096);
//	}
//
//	// {1096}
//	public void test1170() {
//		check(//
//				"Integrate[1/Sqrt[2 - 6*x^2 + 3*x^4], x]", //
//				"((2 + Sqrt[6]*x^2)*Sqrt[(2 - 6*x^2 + 3*x^4)/(2 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(3/2)^(1/4)*x], (2 + Sqrt[6])/4])/(2*6^(1/4)*Sqrt[2 - 6*x^2 + 3*x^4])", //
//				1096);
//	}

	// {1099}
	public void test1171() {
		check(//
				"Integrate[1/Sqrt[3 + 9*x^2 + 2*x^4], x]", //
				"(Sqrt[(6 + (9 - Sqrt[57])*x^2)/(6 + (9 + Sqrt[57])*x^2)]*(6 + (9 + Sqrt[57])*x^2)*EllipticF[ArcTan[Sqrt[(9 + Sqrt[57])/6]*x], (-19 + 3*Sqrt[57])/4])/(Sqrt[6*(9 + Sqrt[57])]*Sqrt[3 + 9*x^2 + 2*x^4])", //
				1099);
	}

	// {1099}
	public void test1172() {
		check(//
				"Integrate[1/Sqrt[3 + 8*x^2 + 2*x^4], x]", //
				"(Sqrt[(3 + (4 - Sqrt[10])*x^2)/(3 + (4 + Sqrt[10])*x^2)]*(3 + (4 + Sqrt[10])*x^2)*EllipticF[ArcTan[Sqrt[(4 + Sqrt[10])/3]*x], (-2*(5 - 2*Sqrt[10]))/3])/(Sqrt[3*(4 + Sqrt[10])]*Sqrt[3 + 8*x^2 + 2*x^4])", //
				1099);
	}

	// {1099}
	public void test1173() {
		check(//
				"Integrate[1/Sqrt[3 + 7*x^2 + 2*x^4], x]", //
				"(Sqrt[(3 + x^2)/(1 + 2*x^2)]*(1 + 2*x^2)*EllipticF[ArcTan[Sqrt[2]*x], 5/6])/(Sqrt[6]*Sqrt[3 + 7*x^2 + 2*x^4])", //
				1099);
	}

	// {1099}
	public void test1174() {
		check(//
				"Integrate[1/Sqrt[3 + 6*x^2 + 2*x^4], x]", //
				"(Sqrt[(3 + (3 - Sqrt[3])*x^2)/(3 + (3 + Sqrt[3])*x^2)]*(3 + (3 + Sqrt[3])*x^2)*EllipticF[ArcTan[Sqrt[(3 + Sqrt[3])/3]*x], -1 + Sqrt[3]])/(Sqrt[3*(3 + Sqrt[3])]*Sqrt[3 + 6*x^2 + 2*x^4])", //
				1099);
	}

	// {1099}
	public void test1175() {
		check(//
				"Integrate[1/Sqrt[3 + 5*x^2 + 2*x^4], x]", //
				"((1 + x^2)*Sqrt[(3 + 2*x^2)/(1 + x^2)]*EllipticF[ArcTan[x], 1/3])/(Sqrt[3]*Sqrt[3 + 5*x^2 + 2*x^4])", //
				1099);
	}

//	// {1103}
//	public void test1176() {
//		check(//
//				"Integrate[1/Sqrt[3 + 4*x^2 + 2*x^4], x]", //
//				"((3 + Sqrt[6]*x^2)*Sqrt[(3 + 4*x^2 + 2*x^4)/(3 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(2/3)^(1/4)*x], 1/2 - 1/Sqrt[6]])/(2*6^(1/4)*Sqrt[3 + 4*x^2 + 2*x^4])", //
//				1103);
//	}
//
//	// {1103}
//	public void test1177() {
//		check(//
//				"Integrate[1/Sqrt[3 + 3*x^2 + 2*x^4], x]", //
//				"((3 + Sqrt[6]*x^2)*Sqrt[(3 + 3*x^2 + 2*x^4)/(3 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(2/3)^(1/4)*x], (4 - Sqrt[6])/8])/(2*6^(1/4)*Sqrt[3 + 3*x^2 + 2*x^4])", //
//				1103);
//	}
//
//	// {1103}
//	public void test1178() {
//		check(//
//				"Integrate[1/Sqrt[3 + 2*x^2 + 2*x^4], x]", //
//				"((3 + Sqrt[6]*x^2)*Sqrt[(3 + 2*x^2 + 2*x^4)/(3 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(2/3)^(1/4)*x], (6 - Sqrt[6])/12])/(2*6^(1/4)*Sqrt[3 + 2*x^2 + 2*x^4])", //
//				1103);
//	}
//
//	// {1103}
//	public void test1179() {
//		check(//
//				"Integrate[1/Sqrt[3 + x^2 + 2*x^4], x]", //
//				"((3 + Sqrt[6]*x^2)*Sqrt[(3 + x^2 + 2*x^4)/(3 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(2/3)^(1/4)*x], (12 - Sqrt[6])/24])/(2*6^(1/4)*Sqrt[3 + x^2 + 2*x^4])", //
//				1103);
//	}

	// {220}
	public void test1180() {
		check(//
				"Integrate[1/Sqrt[3 + 2*x^4], x]", //
				"((3 + Sqrt[6]*x^2)*Sqrt[(3 + 2*x^4)/(3 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(2/3)^(1/4)*x], 1/2])/(2*6^(1/4)*Sqrt[3 + 2*x^4])", //
				220);
	}

//	// {1103}
//	public void test1181() {
//		check(//
//				"Integrate[1/Sqrt[3 - x^2 + 2*x^4], x]", //
//				"((3 + Sqrt[6]*x^2)*Sqrt[(3 - x^2 + 2*x^4)/(3 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(2/3)^(1/4)*x], (12 + Sqrt[6])/24])/(2*6^(1/4)*Sqrt[3 - x^2 + 2*x^4])", //
//				1103);
//	}
//
//	// {1103}
//	public void test1182() {
//		check(//
//				"Integrate[1/Sqrt[3 - 2*x^2 + 2*x^4], x]", //
//				"((3 + Sqrt[6]*x^2)*Sqrt[(3 - 2*x^2 + 2*x^4)/(3 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(2/3)^(1/4)*x], (6 + Sqrt[6])/12])/(2*6^(1/4)*Sqrt[3 - 2*x^2 + 2*x^4])", //
//				1103);
//	}
//
//	// {1103}
//	public void test1183() {
//		check(//
//				"Integrate[1/Sqrt[3 - 3*x^2 + 2*x^4], x]", //
//				"((3 + Sqrt[6]*x^2)*Sqrt[(3 - 3*x^2 + 2*x^4)/(3 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(2/3)^(1/4)*x], (4 + Sqrt[6])/8])/(2*6^(1/4)*Sqrt[3 - 3*x^2 + 2*x^4])", //
//				1103);
//	}
//
//	// {1103}
//	public void test1184() {
//		check(//
//				"Integrate[1/Sqrt[3 - 4*x^2 + 2*x^4], x]", //
//				"((3 + Sqrt[6]*x^2)*Sqrt[(3 - 4*x^2 + 2*x^4)/(3 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(2/3)^(1/4)*x], 1/2 + 1/Sqrt[6]])/(2*6^(1/4)*Sqrt[3 - 4*x^2 + 2*x^4])", //
//				1103);
//	}
//
//	// {1096}
//	public void test1185() {
//		check(//
//				"Integrate[1/Sqrt[3 - 5*x^2 + 2*x^4], x]", //
//				"((3 + Sqrt[6]*x^2)*Sqrt[(3 - 5*x^2 + 2*x^4)/(3 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(2/3)^(1/4)*x], (12 + 5*Sqrt[6])/24])/(2*6^(1/4)*Sqrt[3 - 5*x^2 + 2*x^4])", //
//				1096);
//	}
//
//	// {1096}
//	public void test1186() {
//		check(//
//				"Integrate[1/Sqrt[3 - 6*x^2 + 2*x^4], x]", //
//				"((3 + Sqrt[6]*x^2)*Sqrt[(3 - 6*x^2 + 2*x^4)/(3 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(2/3)^(1/4)*x], (2 + Sqrt[6])/4])/(2*6^(1/4)*Sqrt[3 - 6*x^2 + 2*x^4])", //
//				1096);
//	}
//
//	// {1096}
//	public void test1187() {
//		check(//
//				"Integrate[1/Sqrt[3 - 7*x^2 + 2*x^4], x]", //
//				"((3 + Sqrt[6]*x^2)*Sqrt[(3 - 7*x^2 + 2*x^4)/(3 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(2/3)^(1/4)*x], (12 + 7*Sqrt[6])/24])/(2*6^(1/4)*Sqrt[3 - 7*x^2 + 2*x^4])", //
//				1096);
//	}
//
//	// {1103}
//	public void test1188() {
//		check(//
//				"Integrate[1/Sqrt[-3 + 4*x^2 - 2*x^4], x]", //
//				"((3 + Sqrt[6]*x^2)*Sqrt[(3 - 4*x^2 + 2*x^4)/(3 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(2/3)^(1/4)*x], 1/2 + 1/Sqrt[6]])/(2*6^(1/4)*Sqrt[-3 + 4*x^2 - 2*x^4])", //
//				1103);
//	}
//
//	// {1103}
//	public void test1189() {
//		check(//
//				"Integrate[1/Sqrt[-3 + 3*x^2 - 2*x^4], x]", //
//				"((3 + Sqrt[6]*x^2)*Sqrt[(3 - 3*x^2 + 2*x^4)/(3 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(2/3)^(1/4)*x], (4 + Sqrt[6])/8])/(2*6^(1/4)*Sqrt[-3 + 3*x^2 - 2*x^4])", //
//				1103);
//	}
//
//	// {1103}
//	public void test1190() {
//		check(//
//				"Integrate[1/Sqrt[-3 + 2*x^2 - 2*x^4], x]", //
//				"((3 + Sqrt[6]*x^2)*Sqrt[(3 - 2*x^2 + 2*x^4)/(3 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(2/3)^(1/4)*x], (6 + Sqrt[6])/12])/(2*6^(1/4)*Sqrt[-3 + 2*x^2 - 2*x^4])", //
//				1103);
//	}
//
//	// {1103}
//	public void test1191() {
//		check(//
//				"Integrate[1/Sqrt[-3 + x^2 - 2*x^4], x]", //
//				"((3 + Sqrt[6]*x^2)*Sqrt[(3 - x^2 + 2*x^4)/(3 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(2/3)^(1/4)*x], (12 + Sqrt[6])/24])/(2*6^(1/4)*Sqrt[-3 + x^2 - 2*x^4])", //
//				1103);
//	}
//
//	// {220}
//	public void test1192() {
//		check(//
//				"Integrate[1/Sqrt[-3 - 2*x^4], x]", //
//				"((3 + Sqrt[6]*x^2)*Sqrt[(3 + 2*x^4)/(3 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(2/3)^(1/4)*x], 1/2])/(2*6^(1/4)*Sqrt[-3 - 2*x^4])", //
//				220);
//	}
//
//	// {1103}
//	public void test1193() {
//		check(//
//				"Integrate[1/Sqrt[-3 - x^2 - 2*x^4], x]", //
//				"((3 + Sqrt[6]*x^2)*Sqrt[(3 + x^2 + 2*x^4)/(3 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(2/3)^(1/4)*x], (12 - Sqrt[6])/24])/(2*6^(1/4)*Sqrt[-3 - x^2 - 2*x^4])", //
//				1103);
//	}
//
//	// {1103}
//	public void test1194() {
//		check(//
//				"Integrate[1/Sqrt[-3 - 2*x^2 - 2*x^4], x]", //
//				"((3 + Sqrt[6]*x^2)*Sqrt[(3 + 2*x^2 + 2*x^4)/(3 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(2/3)^(1/4)*x], (6 - Sqrt[6])/12])/(2*6^(1/4)*Sqrt[-3 - 2*x^2 - 2*x^4])", //
//				1103);
//	}
//
//	// {1103}
//	public void test1195() {
//		check(//
//				"Integrate[1/Sqrt[-3 - 3*x^2 - 2*x^4], x]", //
//				"((3 + Sqrt[6]*x^2)*Sqrt[(3 + 3*x^2 + 2*x^4)/(3 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(2/3)^(1/4)*x], (4 - Sqrt[6])/8])/(2*6^(1/4)*Sqrt[-3 - 3*x^2 - 2*x^4])", //
//				1103);
//	}
//
//	// {1103}
//	public void test1196() {
//		check(//
//				"Integrate[1/Sqrt[-3 - 4*x^2 - 2*x^4], x]", //
//				"((3 + Sqrt[6]*x^2)*Sqrt[(3 + 4*x^2 + 2*x^4)/(3 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(2/3)^(1/4)*x], 1/2 - 1/Sqrt[6]])/(2*6^(1/4)*Sqrt[-3 - 4*x^2 - 2*x^4])", //
//				1103);
//	}
//
//	// {1103}
//	public void test1197() {
//		check(//
//				"Integrate[1/Sqrt[-2 + 4*x^2 - 3*x^4], x]", //
//				"((2 + Sqrt[6]*x^2)*Sqrt[(2 - 4*x^2 + 3*x^4)/(2 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(3/2)^(1/4)*x], 1/2 + 1/Sqrt[6]])/(2*6^(1/4)*Sqrt[-2 + 4*x^2 - 3*x^4])", //
//				1103);
//	}
//
//	// {1103}
//	public void test1198() {
//		check(//
//				"Integrate[1/Sqrt[-2 + 3*x^2 - 3*x^4], x]", //
//				"((2 + Sqrt[6]*x^2)*Sqrt[(2 - 3*x^2 + 3*x^4)/(2 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(3/2)^(1/4)*x], (4 + Sqrt[6])/8])/(2*6^(1/4)*Sqrt[-2 + 3*x^2 - 3*x^4])", //
//				1103);
//	}
//
//	// {1103}
//	public void test1199() {
//		check(//
//				"Integrate[1/Sqrt[-2 + 2*x^2 - 3*x^4], x]", //
//				"((2 + Sqrt[6]*x^2)*Sqrt[(2 - 2*x^2 + 3*x^4)/(2 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(3/2)^(1/4)*x], (6 + Sqrt[6])/12])/(2*6^(1/4)*Sqrt[-2 + 2*x^2 - 3*x^4])", //
//				1103);
//	}
//
//	// {1103}
//	public void test1200() {
//		check(//
//				"Integrate[1/Sqrt[-2 + x^2 - 3*x^4], x]", //
//				"((2 + Sqrt[6]*x^2)*Sqrt[(2 - x^2 + 3*x^4)/(2 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(3/2)^(1/4)*x], (12 + Sqrt[6])/24])/(2*6^(1/4)*Sqrt[-2 + x^2 - 3*x^4])", //
//				1103);
//	}
//
//	// {220}
//	public void test1201() {
//		check(//
//				"Integrate[1/Sqrt[-2 - 3*x^4], x]", //
//				"((2 + Sqrt[6]*x^2)*Sqrt[(2 + 3*x^4)/(2 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(3/2)^(1/4)*x], 1/2])/(2*6^(1/4)*Sqrt[-2 - 3*x^4])", //
//				220);
//	}
//
//	// {1103}
//	public void test1202() {
//		check(//
//				"Integrate[1/Sqrt[-2 - x^2 - 3*x^4], x]", //
//				"((2 + Sqrt[6]*x^2)*Sqrt[(2 + x^2 + 3*x^4)/(2 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(3/2)^(1/4)*x], (12 - Sqrt[6])/24])/(2*6^(1/4)*Sqrt[-2 - x^2 - 3*x^4])", //
//				1103);
//	}
//
//	// {1103}
//	public void test1203() {
//		check(//
//				"Integrate[1/Sqrt[-2 - 2*x^2 - 3*x^4], x]", //
//				"((2 + Sqrt[6]*x^2)*Sqrt[(2 + 2*x^2 + 3*x^4)/(2 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(3/2)^(1/4)*x], (6 - Sqrt[6])/12])/(2*6^(1/4)*Sqrt[-2 - 2*x^2 - 3*x^4])", //
//				1103);
//	}
//
//	// {1103}
//	public void test1204() {
//		check(//
//				"Integrate[1/Sqrt[-2 - 3*x^2 - 3*x^4], x]", //
//				"((2 + Sqrt[6]*x^2)*Sqrt[(2 + 3*x^2 + 3*x^4)/(2 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(3/2)^(1/4)*x], (4 - Sqrt[6])/8])/(2*6^(1/4)*Sqrt[-2 - 3*x^2 - 3*x^4])", //
//				1103);
//	}
//
//	// {1103}
//	public void test1205() {
//		check(//
//				"Integrate[1/Sqrt[-2 - 4*x^2 - 3*x^4], x]", //
//				"((2 + Sqrt[6]*x^2)*Sqrt[(2 + 4*x^2 + 3*x^4)/(2 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(3/2)^(1/4)*x], 1/2 - 1/Sqrt[6]])/(2*6^(1/4)*Sqrt[-2 - 4*x^2 - 3*x^4])", //
//				1103);
//	}
//
//	// {1103}
//	public void test1206() {
//		check(//
//				"Integrate[1/Sqrt[2 + 5*x^2 + 5*x^4], x]", //
//				"((2 + Sqrt[10]*x^2)*Sqrt[(2 + 5*x^2 + 5*x^4)/(2 + Sqrt[10]*x^2)^2]*EllipticF[2*ArcTan[(5/2)^(1/4)*x], (4 - Sqrt[10])/8])/(2*10^(1/4)*Sqrt[2 + 5*x^2 + 5*x^4])", //
//				1103);
//	}
//
//	// {1103}
//	public void test1207() {
//		check(//
//				"Integrate[1/Sqrt[2 + 5*x^2 + 4*x^4], x]", //
//				"((1 + Sqrt[2]*x^2)*Sqrt[(2 + 5*x^2 + 4*x^4)/(1 + Sqrt[2]*x^2)^2]*EllipticF[2*ArcTan[2^(1/4)*x], (8 - 5*Sqrt[2])/16])/(2*2^(3/4)*Sqrt[2 + 5*x^2 + 4*x^4])", //
//				1103);
//	}

	// {1100}
	public void test1208() {
		check(//
				"Integrate[1/Sqrt[2 + 5*x^2 + 3*x^4], x]", //
				"((1 + x^2)*Sqrt[(2 + 3*x^2)/(1 + x^2)]*EllipticF[ArcTan[x], -1/2])/(Sqrt[2]*Sqrt[2 + 5*x^2 + 3*x^4])", //
				1100);
	}

	// {1099}
	public void test1209() {
		check(//
				"Integrate[1/Sqrt[2 + 5*x^2 + 2*x^4], x]", //
				"(Sqrt[(2 + x^2)/(1 + 2*x^2)]*(1 + 2*x^2)*EllipticF[ArcTan[Sqrt[2]*x], 3/4])/(2*Sqrt[2 + 5*x^2 + 2*x^4])", //
				1099);
	}

	// {1099}
	public void test1210() {
		check(//
				"Integrate[1/Sqrt[2 + 5*x^2 + x^4], x]", //
				"(Sqrt[(4 + (5 - Sqrt[17])*x^2)/(4 + (5 + Sqrt[17])*x^2)]*(4 + (5 + Sqrt[17])*x^2)*EllipticF[ArcTan[(Sqrt[5 + Sqrt[17]]*x)/2], (-17 + 5*Sqrt[17])/4])/(2*Sqrt[5 + Sqrt[17]]*Sqrt[2 + 5*x^2 + x^4])", //
				1099);
	}

	// {1584, 261}
	public void test1212() {
		check(//
				"Integrate[x^5/(b*x^2 + c*x^4)^2, x]", //
				"-1/(2*c*(b + c*x^2))", //
				1584, 261);
	}

	// {1584, 261}
	public void test1213() {
		check(//
				"Integrate[x^7/(b*x^2 + c*x^4)^3, x]", //
				"-1/(4*c*(b + c*x^2)^2)", //
				1584, 261);
	}

	// {2014}
	public void test1214() {
		check(//
				"Integrate[Sqrt[b*x^2 + c*x^4]/x^5, x]", //
				"-(b*x^2 + c*x^4)^(3/2)/(3*b*x^6)", //
				2014);
	}

	// {2000}
	public void test1215() {
		check(//
				"Integrate[Sqrt[b*x^2 + c*x^4], x]", //
				"(b*x^2 + c*x^4)^(3/2)/(3*c*x^3)", //
				2000);
	}

	// {2014}
	public void test1216() {
		check(//
				"Integrate[(b*x^2 + c*x^4)^(3/2)/x^9, x]", //
				"-(b*x^2 + c*x^4)^(5/2)/(5*b*x^10)", //
				2014);
	}

	// {2014}
	public void test1217() {
		check(//
				"Integrate[(b*x^2 + c*x^4)^(3/2)/x^2, x]", //
				"(b*x^2 + c*x^4)^(5/2)/(5*c*x^5)", //
				2014);
	}

	// {2014}
	public void test1218() {
		check(//
				"Integrate[1/(x*Sqrt[b*x^2 + c*x^4]), x]", //
				"-(Sqrt[b*x^2 + c*x^4]/(b*x^2))", //
				2014);
	}

	// {1588}
	public void test1219() {
		check(//
				"Integrate[x^2/Sqrt[b*x^2 + c*x^4], x]", //
				"Sqrt[b*x^2 + c*x^4]/(c*x)", //
				1588);
	}

	// {2014}
	public void test1220() {
		check(//
				"Integrate[x^3/(b*x^2 + c*x^4)^(3/2), x]", //
				"x^2/(b*Sqrt[b*x^2 + c*x^4])", //
				2014);
	}

	// {1588}
	public void test1221() {
		check(//
				"Integrate[x^4/(b*x^2 + c*x^4)^(3/2), x]", //
				"-(x/(c*Sqrt[b*x^2 + c*x^4]))", //
				1588);
	}

	// {1110}
	public void test1223() {
		check(//
				"Integrate[Sqrt[a^2 + 2*a*b*x^2 + b^2*x^4]/x^7, x]", //
				"-((a + b*x^2)*Sqrt[a^2 + 2*a*b*x^2 + b^2*x^4])/(4*a*x^6) + (a^2 + 2*a*b*x^2 + b^2*x^4)^(3/2)/(12*a^2*x^6)", //
				1110);
	}

	// {1110}
	public void test1224() {
		check(//
				"Integrate[(a^2 + 2*a*b*x^2 + b^2*x^4)^(3/2)/x^11, x]", //
				"-((a + b*x^2)*(a^2 + 2*a*b*x^2 + b^2*x^4)^(3/2))/(8*a*x^10) + (a^2 + 2*a*b*x^2 + b^2*x^4)^(5/2)/(40*a^2*x^10)", //
				1110);
	}

	// {1110}
	public void test1225() {
		check(//
				"Integrate[(a^2 + 2*a*b*x^2 + b^2*x^4)^(5/2)/x^15, x]", //
				"-((a + b*x^2)*(a^2 + 2*a*b*x^2 + b^2*x^4)^(5/2))/(12*a*x^14) + (a^2 + 2*a*b*x^2 + b^2*x^4)^(7/2)/(84*a^2*x^14)", //
				1110);
	}

	// {1109}
	public void test1226() {
		check(//
				"Integrate[x^5/(a^2 + 2*a*b*x^2 + b^2*x^4)^(5/2), x]", //
				"x^6/(24*a^2*(a^2 + 2*a*b*x^2 + b^2*x^4)^(3/2)) + x^6/(8*a*(a + b*x^2)*(a^2 + 2*a*b*x^2 + b^2*x^4)^(3/2))", //
				1109);
	}

	// {1103}
	public void test1228() {
		check(//
				"Integrate[1/Sqrt[a + b*x^2 + c*x^4], x]", //
				"((Sqrt[a] + Sqrt[c]*x^2)*Sqrt[(a + b*x^2 + c*x^4)/(Sqrt[a] + Sqrt[c]*x^2)^2]*EllipticF[2*ArcTan[(c^(1/4)*x)/a^(1/4)], (2 - b/(Sqrt[a]*Sqrt[c]))/4])/(2*a^(1/4)*c^(1/4)*Sqrt[a + b*x^2 + c*x^4])", //
				1103);
	}

	// {1196}
	public void test1229() {
		check(//
				"Integrate[(1 - b*x^2)/Sqrt[1 + b^2*x^4], x]", //
				"-((x*Sqrt[1 + b^2*x^4])/(1 + b*x^2)) + ((1 + b*x^2)*Sqrt[(1 + b^2*x^4)/(1 + b*x^2)^2]*EllipticE[2*ArcTan[Sqrt[b]*x], 1/2])/(Sqrt[b]*Sqrt[1 + b^2*x^4])", //
				1196);
	}

	// {1196}
	public void test1230() {
		check(//
				"Integrate[(1 - b*x^2)/Sqrt[-1 - b^2*x^4], x]", //
				"(x*Sqrt[-1 - b^2*x^4])/(1 + b*x^2) + ((1 + b*x^2)*Sqrt[(1 + b^2*x^4)/(1 + b*x^2)^2]*EllipticE[2*ArcTan[Sqrt[b]*x], 1/2])/(Sqrt[b]*Sqrt[-1 - b^2*x^4])", //
				1196);
	}

	// {424}
	public void test1231() {
		check(//
				"Integrate[Sqrt[1 + c^2*x^2]/Sqrt[1 - c^2*x^2], x]", //
				"EllipticE[ArcSin[c*x], -1]/c", //
				424);
	}

	// {1218}
	public void test1232() {
		check(//
				"Integrate[1/((a + b*x^2)*Sqrt[4 - d*x^4]), x]", //
				"EllipticPi[(-2*b)/(a*Sqrt[d]), ArcSin[(d^(1/4)*x)/Sqrt[2]], -1]/(Sqrt[2]*a*d^(1/4))", //
				1218);
	}

	// {245}
	public void test1233() {
		check(//
				"Integrate[(1 + b*x^4)^p, x]", //
				"x*Hypergeometric2F1[1/4, -p, 5/4, -(b*x^4)]", //
				245);
	}

	// {1225}
	public void test1234() {
		check(//
				"Integrate[Sqrt[1 + x^2 + x^4]/(1 + x^2)^2, x]", //
				"((1 + x^2)*Sqrt[(1 + x^2 + x^4)/(1 + x^2)^2]*EllipticE[2*ArcTan[x], 1/4])/(2*Sqrt[1 + x^2 + x^4])", //
				1225);
	}

	// {1099}
	public void test1235() {
		check(//
				"Integrate[1/Sqrt[2 + 3*x^2 + x^4], x]", //
				"((1 + x^2)*Sqrt[(2 + x^2)/(1 + x^2)]*EllipticF[ArcTan[x], 1/2])/(Sqrt[2]*Sqrt[2 + 3*x^2 + x^4])", //
				1099);
	}

	// {1103}
	public void test1236() {
		check(//
				"Integrate[1/Sqrt[4 + 3*x^2 + x^4], x]", //
				"((2 + x^2)*Sqrt[(4 + 3*x^2 + x^4)/(2 + x^2)^2]*EllipticF[2*ArcTan[x/Sqrt[2]], 1/8])/(2*Sqrt[2]*Sqrt[4 + 3*x^2 + x^4])", //
				1103);
	}

	// {1247, 629}
	public void test1237() {
		check(//
				"Integrate[x*(a + b*x^2)*(a^2 + 2*a*b*x^2 + b^2*x^4)^p, x]", //
				"(a^2 + 2*a*b*x^2 + b^2*x^4)^(1 + p)/(4*b*(1 + p))", //
				1247, 629);
	}

	// {1588}
	public void test1238() {
		check(//
				"Integrate[(a*g - c*g*x^4)/(a + b*x^2 + c*x^4)^(3/2), x]", //
				"(g*x)/Sqrt[a + b*x^2 + c*x^4]", //
				1588);
	}

	// {1588}
	public void test1239() {
		check(//
				"Integrate[x^2*(a + b*x^2 + c*x^4)^p*(3*a + b*(5 + 2*p)*x^2 + c*(7 + 4*p)*x^4), x]", //
				"x^3*(a + b*x^2 + c*x^4)^(1 + p)", //
				1588);
	}

	// {2072}
	public void test1240() {
		check(//
				"Integrate[Sqrt[a + b*x^2 - c*x^4]/(a*d + c*d*x^4), x]", //
				"-(Sqrt[b + Sqrt[b^2 + 4*a*c]]*ArcTan[(Sqrt[b + Sqrt[b^2 + 4*a*c]]*x*(b - Sqrt[b^2 + 4*a*c] - 2*c*x^2))/(2*Sqrt[2]*Sqrt[a]*Sqrt[c]*Sqrt[a + b*x^2 - c*x^4])])/(2*Sqrt[2]*Sqrt[a]*Sqrt[c]*d) + (Sqrt[-b + Sqrt[b^2 + 4*a*c]]*ArcTanh[(Sqrt[-b + Sqrt[b^2 + 4*a*c]]*x*(b + Sqrt[b^2 + 4*a*c] - 2*c*x^2))/(2*Sqrt[2]*Sqrt[a]*Sqrt[c]*Sqrt[a + b*x^2 - c*x^4])])/(2*Sqrt[2]*Sqrt[a]*Sqrt[c]*d)", //
				2072);
	}

	// {1706}
	public void test1241() {
		check(//
				"Integrate[(Sqrt[a] + Sqrt[c]*x^2)/((d + e*x^2)*Sqrt[a + b*x^2 + c*x^4]), x]", //
				"-((Sqrt[c]*d - Sqrt[a]*e)*ArcTan[(Sqrt[c*d^2 - b*d*e + a*e^2]*x)/(Sqrt[d]*Sqrt[e]*Sqrt[a + b*x^2 + c*x^4])])/(2*Sqrt[d]*Sqrt[e]*Sqrt[c*d^2 - b*d*e + a*e^2]) + ((Sqrt[c]*d + Sqrt[a]*e)*(Sqrt[a] + Sqrt[c]*x^2)*Sqrt[(a + b*x^2 + c*x^4)/(Sqrt[a] + Sqrt[c]*x^2)^2]*EllipticPi[-(Sqrt[c]*d - Sqrt[a]*e)^2/(4*Sqrt[a]*Sqrt[c]*d*e), 2*ArcTan[(c^(1/4)*x)/a^(1/4)], (2 - b/(Sqrt[a]*Sqrt[c]))/4])/(4*a^(1/4)*c^(1/4)*d*e*Sqrt[a + b*x^2 + c*x^4])", //
				1706);
	}

	// {1706}
	public void test1242() {
		check(//
				"Integrate[(1 + Sqrt[c/a]*x^2)/((d + e*x^2)*Sqrt[a + b*x^2 + c*x^4]), x]", //
				"-((Sqrt[c/a]*d - e)*ArcTan[(Sqrt[c*d^2 - b*d*e + a*e^2]*x)/(Sqrt[d]*Sqrt[e]*Sqrt[a + b*x^2 + c*x^4])])/(2*Sqrt[d]*Sqrt[e]*Sqrt[c*d^2 - b*d*e + a*e^2]) + ((Sqrt[c/a]*d + e)*(1 + Sqrt[c/a]*x^2)*Sqrt[(a + b*x^2 + c*x^4)/(a*(1 + Sqrt[c/a]*x^2)^2)]*EllipticPi[-(Sqrt[c/a]*d - e)^2/(4*Sqrt[c/a]*d*e), 2*ArcTan[(c/a)^(1/4)*x], (2 - (b*Sqrt[c/a])/c)/4])/(4*(c/a)^(1/4)*d*e*Sqrt[a + b*x^2 + c*x^4])", //
				1706);
	}

	// {2000}
	public void test1243() {
		check(//
				"Integrate[(a*x^3 + b*x^6)^(2/3), x]", //
				"(a*x^3 + b*x^6)^(5/3)/(5*b*x^5)", //
				2000);
	}

	// {2000}
	public void test1244() {
		check(//
				"Integrate[(a*x^3 + b*x^6)^(-2/3), x]", //
				"-((a*x^3 + b*x^6)^(1/3)/(a*x^2))", //
				2000);
	}

	// {2014}
	public void test1245() {
		check(//
				"Integrate[x^(-1 - n*(-1 + p))*(b*x^n + c*x^(2*n))^p, x]", //
				"(b*x^n + c*x^(2*n))^(1 + p)/(c*n*(1 + p)*x^(n*(1 + p)))", //
				2014);
	}

	// {2014}
	public void test1246() {
		check(//
				"Integrate[x^(-1 - n*(1 + 2*p))*(b*x^n + c*x^(2*n))^p, x]", //
				"-((b*x^n + c*x^(2*n))^(1 + p)/(b*n*(1 + p)*x^(2*n*(1 + p))))", //
				2014);
	}

	// {629}
	public void test1247() {
		check(//
				"Integrate[(b + 2*c*x)*(a + b*x + c*x^2)^13, x]", //
				"(a + b*x + c*x^2)^14/14", //
				629);
	}

	// {1247, 629}
	public void test1248() {
		check(//
				"Integrate[x*(b + 2*c*x^2)*(a + b*x^2 + c*x^4)^13, x]", //
				"(a + b*x^2 + c*x^4)^14/28", //
				1247, 629);
	}

	// {1468, 629}
	public void test1249() {
		check(//
				"Integrate[x^2*(b + 2*c*x^3)*(a + b*x^3 + c*x^6)^13, x]", //
				"(a + b*x^3 + c*x^6)^14/42", //
				1468, 629);
	}

	// {629}
	public void test1250() {
		check(//
				"Integrate[(b + 2*c*x)*(-a + b*x + c*x^2)^13, x]", //
				"(a - b*x - c*x^2)^14/14", //
				629);
	}

	// {1247, 629}
	public void test1251() {
		check(//
				"Integrate[x*(b + 2*c*x^2)*(-a + b*x^2 + c*x^4)^13, x]", //
				"(a - b*x^2 - c*x^4)^14/28", //
				1247, 629);
	}

	// {1468, 629}
	public void test1252() {
		check(//
				"Integrate[x^2*(b + 2*c*x^3)*(-a + b*x^3 + c*x^6)^13, x]", //
				"(a - b*x^3 - c*x^6)^14/42", //
				1468, 629);
	}

	// {629}
	public void test1253() {
		check(//
				"Integrate[(b + 2*c*x)*(b*x + c*x^2)^13, x]", //
				"(b*x + c*x^2)^14/14", //
				629);
	}

	// {1584, 446, 74}
	public void test1254() {
		check(//
				"Integrate[x*(b + 2*c*x^2)*(b*x^2 + c*x^4)^13, x]", //
				"(x^28*(b + c*x^2)^14)/28", //
				1584, 446, 74);
	}

	// {1584, 446, 74}
	public void test1255() {
		check(//
				"Integrate[x^2*(b + 2*c*x^3)*(b*x^3 + c*x^6)^13, x]", //
				"(x^42*(b + c*x^3)^14)/42", //
				1584, 446, 74);
	}

	// {628}
	public void test1256() {
		check(//
				"Integrate[(b + 2*c*x)/(a + b*x + c*x^2), x]", //
				"Log[a + b*x + c*x^2]", //
				628);
	}

	// {1247, 628}
	public void test1257() {
		check(//
				"Integrate[(x*(b + 2*c*x^2))/(a + b*x^2 + c*x^4), x]", //
				"Log[a + b*x^2 + c*x^4]/2", //
				1247, 628);
	}

	// {1468, 628}
	public void test1258() {
		check(//
				"Integrate[(x^2*(b + 2*c*x^3))/(a + b*x^3 + c*x^6), x]", //
				"Log[a + b*x^3 + c*x^6]/3", //
				1468, 628);
	}

	// {629}
	public void test1259() {
		check(//
				"Integrate[(b + 2*c*x)/(a + b*x + c*x^2)^8, x]", //
				"-1/(7*(a + b*x + c*x^2)^7)", //
				629);
	}

	// {1247, 629}
	public void test1260() {
		check(//
				"Integrate[(x*(b + 2*c*x^2))/(a + b*x^2 + c*x^4)^8, x]", //
				"-1/(14*(a + b*x^2 + c*x^4)^7)", //
				1247, 629);
	}

	// {1468, 629}
	public void test1261() {
		check(//
				"Integrate[(x^2*(b + 2*c*x^3))/(a + b*x^3 + c*x^6)^8, x]", //
				"-1/(21*(a + b*x^3 + c*x^6)^7)", //
				1468, 629);
	}

	// {628}
	public void test1262() {
		check(//
				"Integrate[(b + 2*c*x)/(-a + b*x + c*x^2), x]", //
				"Log[a - b*x - c*x^2]", //
				628);
	}

	// {1247, 628}
	public void test1263() {
		check(//
				"Integrate[(x*(b + 2*c*x^2))/(-a + b*x^2 + c*x^4), x]", //
				"Log[a - b*x^2 - c*x^4]/2", //
				1247, 628);
	}

	// {1468, 628}
	public void test1264() {
		check(//
				"Integrate[(x^2*(b + 2*c*x^3))/(-a + b*x^3 + c*x^6), x]", //
				"Log[a - b*x^3 - c*x^6]/3", //
				1468, 628);
	}

	// {629}
	public void test1265() {
		check(//
				"Integrate[(b + 2*c*x)/(-a + b*x + c*x^2)^8, x]", //
				"1/(7*(a - b*x - c*x^2)^7)", //
				629);
	}

	// {1247, 629}
	public void test1266() {
		check(//
				"Integrate[(x*(b + 2*c*x^2))/(-a + b*x^2 + c*x^4)^8, x]", //
				"1/(14*(a - b*x^2 - c*x^4)^7)", //
				1247, 629);
	}

	// {1468, 629}
	public void test1267() {
		check(//
				"Integrate[(x^2*(b + 2*c*x^3))/(-a + b*x^3 + c*x^6)^8, x]", //
				"1/(21*(a - b*x^3 - c*x^6)^7)", //
				1468, 629);
	}

	// {628}
	public void test1268() {
		check(//
				"Integrate[(b + 2*c*x)/(b*x + c*x^2), x]", //
				"Log[b*x + c*x^2]", //
				628);
	}

	// {1584, 446, 72}
	public void test1269() {
		check(//
				"Integrate[(x*(b + 2*c*x^2))/(b*x^2 + c*x^4), x]", //
				"Log[x] + Log[b + c*x^2]/2", //
				1584, 446, 72);
	}

	// {1584, 446, 72}
	public void test1270() {
		check(//
				"Integrate[(x^2*(b + 2*c*x^3))/(b*x^3 + c*x^6), x]", //
				"Log[x] + Log[b + c*x^3]/3", //
				1584, 446, 72);
	}

	// {629}
	public void test1271() {
		check(//
				"Integrate[(b + 2*c*x)/(b*x + c*x^2)^8, x]", //
				"-1/(7*(b*x + c*x^2)^7)", //
				629);
	}

	// {1584, 446, 74}
	public void test1272() {
		check(//
				"Integrate[(x*(b + 2*c*x^2))/(b*x^2 + c*x^4)^8, x]", //
				"-1/(14*x^14*(b + c*x^2)^7)", //
				1584, 446, 74);
	}

	// {1584, 446, 74}
	public void test1273() {
		check(//
				"Integrate[(x^2*(b + 2*c*x^3))/(b*x^3 + c*x^6)^8, x]", //
				"-1/(21*x^21*(b + c*x^3)^7)", //
				1584, 446, 74);
	}

	// {629}
	public void test1274() {
		check(//
				"Integrate[(b + 2*c*x)*(a + b*x + c*x^2)^p, x]", //
				"(a + b*x + c*x^2)^(1 + p)/(1 + p)", //
				629);
	}

	// {1247, 629}
	public void test1275() {
		check(//
				"Integrate[x*(b + 2*c*x^2)*(a + b*x^2 + c*x^4)^p, x]", //
				"(a + b*x^2 + c*x^4)^(1 + p)/(2*(1 + p))", //
				1247, 629);
	}

	// {1468, 629}
	public void test1276() {
		check(//
				"Integrate[x^2*(b + 2*c*x^3)*(a + b*x^3 + c*x^6)^p, x]", //
				"(a + b*x^3 + c*x^6)^(1 + p)/(3*(1 + p))", //
				1468, 629);
	}

	// {629}
	public void test1277() {
		check(//
				"Integrate[(b + 2*c*x)*(-a + b*x + c*x^2)^p, x]", //
				"(-a + b*x + c*x^2)^(1 + p)/(1 + p)", //
				629);
	}

	// {1247, 629}
	public void test1278() {
		check(//
				"Integrate[x*(b + 2*c*x^2)*(-a + b*x^2 + c*x^4)^p, x]", //
				"(-a + b*x^2 + c*x^4)^(1 + p)/(2*(1 + p))", //
				1247, 629);
	}

	// {1468, 629}
	public void test1279() {
		check(//
				"Integrate[x^2*(b + 2*c*x^3)*(-a + b*x^3 + c*x^6)^p, x]", //
				"(-a + b*x^3 + c*x^6)^(1 + p)/(3*(1 + p))", //
				1468, 629);
	}

	// {629}
	public void test1280() {
		check(//
				"Integrate[(b + 2*c*x)*(b*x + c*x^2)^p, x]", //
				"(b*x + c*x^2)^(1 + p)/(1 + p)", //
				629);
	}

	// {1588}
	public void test1281() {
		check(//
				"Integrate[x*(b + 2*c*x^2)*(b*x^2 + c*x^4)^p, x]", //
				"(b*x^2 + c*x^4)^(1 + p)/(2*(1 + p))", //
				1588);
	}

	// {1588}
	public void test1282() {
		check(//
				"Integrate[x^2*(b + 2*c*x^3)*(b*x^3 + c*x^6)^p, x]", //
				"(b*x^3 + c*x^6)^(1 + p)/(3*(1 + p))", //
				1588);
	}

	// {1775}
	public void test1283() {
		check(//
				"Integrate[(a + b*x^n + c*x^(2*n))^p*(a + b*(1 + n + n*p)*x^n + c*(1 + 2*n*(1 + p))*x^(2*n)), x]", //
				"x*(a + b*x^n + c*x^(2*n))^(1 + p)", //
				1775);
	}

	// {1816}
	public void test1284() {
		check(//
				"Integrate[(x^(-1 + n/4)*(-(a*h) + c*f*x^(n/4) + c*g*x^((3*n)/4) + c*h*x^n))/(a + c*x^n)^(3/2), x]", //
				"(-2*(a*g + 2*a*h*x^(n/4) - c*f*x^(n/2)))/(a*n*Sqrt[a + c*x^n])", //
				1816);
	}

	// {1753}
	public void test1285() {
		check(//
				"Integrate[(x^(-1 + n/2)*(-(a*h) + c*f*x^(n/2) + c*g*x^((3*n)/2) + c*h*x^(2*n)))/(a + b*x^n + c*x^(2*n))^(3/2), x]", //
				"(-2*(c*(b*f - 2*a*g) + (b^2 - 4*a*c)*h*x^(n/2) + c*(2*c*f - b*g)*x^n))/((b^2 - 4*a*c)*n*Sqrt[a + b*x^n + c*x^(2*n)])", //
				1753);
	}

	// {1747}
	public void test1286() {
		check(//
				"Integrate[(g*x)^m*(a + b*x^n + c*x^(2*n))^p*(a*(1 + m) + b*(1 + m + n + n*p)*x^n + c*(1 + m + 2*n*(1 + p))*x^(2*n)), x]", //
				"((g*x)^(1 + m)*(a + b*x^n + c*x^(2*n))^(1 + p))/g", //
				1747);
	}

	// {1916}
	public void test1288() {
		check(//
				"Integrate[x^4/(a*x^2 + b*x^3 + c*x^4)^(3/2), x]", //
				"(2*x*(2*a + b*x))/((b^2 - 4*a*c)*Sqrt[a*x^2 + b*x^3 + c*x^4])", //
				1916);
	}

	// {1915}
	public void test1289() {
		check(//
				"Integrate[x^3/(a*x^2 + b*x^3 + c*x^4)^(3/2), x]", //
				"(-2*x*(b + 2*c*x))/((b^2 - 4*a*c)*Sqrt[a*x^2 + b*x^3 + c*x^4])", //
				1915);
	}

	// {1915}
	public void test1291() {
		check(//
				"Integrate[x^((3*(-1 + n))/2)/(a*x^(-1 + n) + b*x^n + c*x^(1 + n))^(3/2), x]", //
				"(-2*x^((-1 + n)/2)*(b + 2*c*x))/((b^2 - 4*a*c)*Sqrt[a*x^(-1 + n) + b*x^n + c*x^(1 + n)])", //
				1915);
	}

	// {629}
	public void test1302() {
		check(//
				"Integrate[(b + 2*c*x)*(b*x + c*x^2)^13, x]", //
				"(b*x + c*x^2)^14/14", //
				629);
	}

	// {1584, 446, 74}
	public void test1303() {
		check(//
				"Integrate[x^14*(b + 2*c*x^2)*(b*x + c*x^3)^13, x]", //
				"(x^28*(b + c*x^2)^14)/28", //
				1584, 446, 74);
	}

	// {1584, 446, 74}
	public void test1304() {
		check(//
				"Integrate[x^28*(b + 2*c*x^3)*(b*x + c*x^4)^13, x]", //
				"(x^42*(b + c*x^3)^14)/42", //
				1584, 446, 74);
	}

	// {628}
	public void test1305() {
		check(//
				"Integrate[(b + 2*c*x)/(b*x + c*x^2), x]", //
				"Log[b*x + c*x^2]", //
				628);
	}

	// {629}
	public void test1306() {
		check(//
				"Integrate[(b + 2*c*x)/(b*x + c*x^2)^8, x]", //
				"-1/(7*(b*x + c*x^2)^7)", //
				629);
	}

	// {1584, 446, 74}
	public void test1307() {
		check(//
				"Integrate[(b + 2*c*x^2)/(x^7*(b*x + c*x^3)^8), x]", //
				"-1/(14*x^14*(b + c*x^2)^7)", //
				1584, 446, 74);
	}

	// {1584, 446, 74}
	public void test1308() {
		check(//
				"Integrate[(b + 2*c*x^3)/(x^14*(b*x + c*x^4)^8), x]", //
				"-1/(21*x^21*(b + c*x^3)^7)", //
				1584, 446, 74);
	}

	// {629}
	public void test1309() {
		check(//
				"Integrate[(b + 2*c*x)*(b*x + c*x^2)^p, x]", //
				"(b*x + c*x^2)^(1 + p)/(1 + p)", //
				629);
	}

	// {1590}
	public void test1310() {
		check(//
				"Integrate[x^(1 + p)*(b + 2*c*x^2)*(b*x + c*x^3)^p, x]", //
				"(x^(1 + p)*(b*x + c*x^3)^(1 + p))/(2*(1 + p))", //
				1590);
	}

	// {1590}
	public void test1311() {
		check(//
				"Integrate[x^(2*(1 + p))*(b + 2*c*x^3)*(b*x + c*x^4)^p, x]", //
				"(x^(2*(1 + p))*(b*x + c*x^4)^(1 + p))/(3*(1 + p))", //
				1590);
	}

	// {2036}
	public void test1312() {
		check(//
				"Integrate[x^((-1 + n)*(1 + p))*(b + 2*c*x^n)*(b*x + c*x^(1 + n))^p, x]", //
				"(b*x + c*x^(1 + n))^(1 + p)/(n*(1 + p)*x^((1 - n)*(1 + p)))", //
				2036);
	}

	// {1588}
	public void test1313() {
		check(//
				"Integrate[(b + 2*c*x + 3*d*x^2)*(a + b*x + c*x^2 + d*x^3)^n, x]", //
				"(a + b*x + c*x^2 + d*x^3)^(1 + n)/(1 + n)", //
				1588);
	}

	// {1588}
	public void test1314() {
		check(//
				"Integrate[(b + 2*c*x + 3*d*x^2)*(b*x + c*x^2 + d*x^3)^n, x]", //
				"(b*x + c*x^2 + d*x^3)^(1 + n)/(1 + n)", //
				1588);
	}

	// {1590}
	public void test1315() {
		check(//
				"Integrate[x^n*(b + c*x + d*x^2)^n*(b + 2*c*x + 3*d*x^2), x]", //
				"(x^(1 + n)*(b + c*x + d*x^2)^(1 + n))/(1 + n)", //
				1590);
	}

	// {1588}
	public void test1316() {
		check(//
				"Integrate[(b + 3*d*x^2)*(a + b*x + d*x^3)^n, x]", //
				"(a + b*x + d*x^3)^(1 + n)/(1 + n)", //
				1588);
	}

	// {1588}
	public void test1317() {
		check(//
				"Integrate[(b + 3*d*x^2)*(b*x + d*x^3)^n, x]", //
				"(b*x + d*x^3)^(1 + n)/(1 + n)", //
				1588);
	}

	// {449}
	public void test1318() {
		check(//
				"Integrate[x^n*(b + d*x^2)^n*(b + 3*d*x^2), x]", //
				"(x^(1 + n)*(b + d*x^2)^(1 + n))/(1 + n)", //
				449);
	}

	// {1588}
	public void test1319() {
		check(//
				"Integrate[(2*c*x + 3*d*x^2)*(a + c*x^2 + d*x^3)^n, x]", //
				"(a + c*x^2 + d*x^3)^(1 + n)/(1 + n)", //
				1588);
	}

	// {1588}
	public void test1320() {
		check(//
				"Integrate[(2*c*x + 3*d*x^2)*(c*x^2 + d*x^3)^n, x]", //
				"(c*x^2 + d*x^3)^(1 + n)/(1 + n)", //
				1588);
	}

	// {1584, 763}
	public void test1321() {
		check(//
				"Integrate[x^n*(c*x + d*x^2)^n*(2*c*x + 3*d*x^2), x]", //
				"(x^(1 + n)*(c*x + d*x^2)^(1 + n))/(1 + n)", //
				1584, 763);
	}

	// {845}
	public void test1322() {
		check(//
				"Integrate[x^(2*n)*(c + d*x)^n*(2*c*x + 3*d*x^2), x]", //
				"(x^(2*(1 + n))*(c + d*x)^(1 + n))/(1 + n)", //
				845);
	}

	// {1588}
	public void test1323() {
		check(//
				"Integrate[x*(2*c + 3*d*x)*(a + c*x^2 + d*x^3)^n, x]", //
				"(a + c*x^2 + d*x^3)^(1 + n)/(1 + n)", //
				1588);
	}

	// {1588}
	public void test1324() {
		check(//
				"Integrate[x*(2*c + 3*d*x)*(c*x^2 + d*x^3)^n, x]", //
				"(c*x^2 + d*x^3)^(1 + n)/(1 + n)", //
				1588);
	}

	// {1588}
	public void test1325() {
		check(//
				"Integrate[(b + 2*c*x + 3*d*x^2)*(a + b*x + c*x^2 + d*x^3)^7, x]", //
				"(a + b*x + c*x^2 + d*x^3)^8/8", //
				1588);
	}

	// {1588}
	public void test1326() {
		check(//
				"Integrate[(b + 2*c*x + 3*d*x^2)*(b*x + c*x^2 + d*x^3)^7, x]", //
				"(b*x + c*x^2 + d*x^3)^8/8", //
				1588);
	}

	// {1588}
	public void test1327() {
		check(//
				"Integrate[x^7*(b + c*x + d*x^2)^7*(b + 2*c*x + 3*d*x^2), x]", //
				"(x^8*(b + c*x + d*x^2)^8)/8", //
				1588);
	}

	// {1588}
	public void test1328() {
		check(//
				"Integrate[(b + 3*d*x^2)*(a + b*x + d*x^3)^7, x]", //
				"(a + b*x + d*x^3)^8/8", //
				1588);
	}

	// {446, 74}
	public void test1329() {
		check(//
				"Integrate[x^7*(b + d*x^2)^7*(b + 3*d*x^2), x]", //
				"(x^8*(b + d*x^2)^8)/8", //
				446, 74);
	}

	// {1588}
	public void test1330() {
		check(//
				"Integrate[(b + 3*d*x^2)*(b*x + d*x^3)^7, x]", //
				"(b*x + d*x^3)^8/8", //
				1588);
	}

	// {1588}
	public void test1331() {
		check(//
				"Integrate[(2*c*x + 3*d*x^2)*(a + c*x^2 + d*x^3)^7, x]", //
				"(a + c*x^2 + d*x^3)^8/8", //
				1588);
	}

	// {1588}
	public void test1332() {
		check(//
				"Integrate[(2*c*x + 3*d*x^2)*(c*x^2 + d*x^3)^7, x]", //
				"(c*x^2 + d*x^3)^8/8", //
				1588);
	}

	// {1584, 845}
	public void test1333() {
		check(//
				"Integrate[x^7*(c*x + d*x^2)^7*(2*c*x + 3*d*x^2), x]", //
				"(x^16*(c + d*x)^8)/8", //
				1584, 845);
	}

	// {845}
	public void test1334() {
		check(//
				"Integrate[x^14*(c + d*x)^7*(2*c*x + 3*d*x^2), x]", //
				"(x^16*(c + d*x)^8)/8", //
				845);
	}

	// {1588}
	public void test1335() {
		check(//
				"Integrate[x*(2*c + 3*d*x)*(a + c*x^2 + d*x^3)^7, x]", //
				"(a + c*x^2 + d*x^3)^8/8", //
				1588);
	}

	// {1584, 74}
	public void test1336() {
		check(//
				"Integrate[x*(2*c + 3*d*x)*(c*x^2 + d*x^3)^7, x]", //
				"(x^16*(c + d*x)^8)/8", //
				1584, 74);
	}

	// {763}
	public void test1337() {
		check(//
				"Integrate[x^8*(2*c + 3*d*x)*(c*x + d*x^2)^7, x]", //
				"(x^8*(c*x + d*x^2)^8)/8", //
				763);
	}

	// {74}
	public void test1338() {
		check(//
				"Integrate[x^15*(c + d*x)^7*(2*c + 3*d*x), x]", //
				"(x^16*(c + d*x)^8)/8", //
				74);
	}

	// {1588}
	public void test1339() {
		check(//
				"Integrate[(-4 + 4*x + x^2)*(5 - 12*x + 6*x^2 + x^3), x]", //
				"(5 - 12*x + 6*x^2 + x^3)^2/6", //
				1588);
	}

	// {1588}
	public void test1340() {
		check(//
				"Integrate[(2*x + x^3)*(1 + 4*x^2 + x^4), x]", //
				"(1 + 4*x^2 + x^4)^2/8", //
				1588);
	}

	// {1588}
	public void test1341() {
		check(//
				"Integrate[(2 - x^2)/(1 - 6*x + x^3)^5, x]", //
				"1/(12*(1 - 6*x + x^3)^4)", //
				1588);
	}

	// {1587}
	public void test1342() {
		check(//
				"Integrate[(2*x + x^2)/(4 + 3*x^2 + x^3), x]", //
				"Log[4 + 3*x^2 + x^3]/3", //
				1587);
	}

	// {1587}
	public void test1343() {
		check(//
				"Integrate[(1 + x + x^3)/(4*x + 2*x^2 + x^4), x]", //
				"Log[4*x + 2*x^2 + x^4]/4", //
				1587);
	}

	// {1588}
	public void test1344() {
		check(//
				"Integrate[(-1 + 4*x^5)/(1 + x + x^5)^2, x]", //
				"-(x/(1 + x + x^5))", //
				1588);
	}

	// {1590}
	public void test1345() {
		check(//
				"Integrate[x^m*(a + b*x + c*x^2 + d*x^3)^p*(a*(1 + m) + x*(b*(2 + m + p) + x*(c*(3 + m + 2*p) + d*(4 + m + 3*p)*x))), x]", //
				"x^(1 + m)*(a + b*x + c*x^2 + d*x^3)^(1 + p)", //
				1590);
	}

	// {1588}
	public void test1346() {
		check(//
				"Integrate[x^2*(a + b*x + c*x^2 + d*x^3)^p*(3*a + b*(4 + p)*x + c*(5 + 2*p)*x^2 + d*(6 + 3*p)*x^3), x]", //
				"x^3*(a + b*x + c*x^2 + d*x^3)^(1 + p)", //
				1588);
	}

	// {1588}
	public void test1347() {
		check(//
				"Integrate[x*(a + b*x + c*x^2 + d*x^3)^p*(2*a + b*(3 + p)*x + c*(4 + 2*p)*x^2 + d*(5 + 3*p)*x^3), x]", //
				"x^2*(a + b*x + c*x^2 + d*x^3)^(1 + p)", //
				1588);
	}

	// {1588}
	public void test1348() {
		check(//
				"Integrate[(a + b*x + c*x^2 + d*x^3)^p*(a + b*(2 + p)*x + c*(3 + 2*p)*x^2 + d*(4 + 3*p)*x^3), x]", //
				"x*(a + b*x + c*x^2 + d*x^3)^(1 + p)", //
				1588);
	}

	// {1585, 1588}
	public void test1349() {
		check(//
				"Integrate[((a + b*x + c*x^2 + d*x^3)^p*(b*(1 + p)*x + c*(2 + 2*p)*x^2 + d*(3 + 3*p)*x^3))/x, x]", //
				"(a + b*x + c*x^2 + d*x^3)^(1 + p)", //
				1585, 1588);
	}

	// {1590}
	public void test1350() {
		check(//
				"Integrate[((a + b*x + c*x^2 + d*x^3)^p*(-a + b*p*x + c*(1 + 2*p)*x^2 + d*(2 + 3*p)*x^3))/x^2, x]", //
				"(a + b*x + c*x^2 + d*x^3)^(1 + p)/x", //
				1590);
	}

	// {1590}
	public void test1351() {
		check(//
				"Integrate[((a + b*x + c*x^2 + d*x^3)^p*(-2*a + b*(-1 + p)*x + 2*c*p*x^2 + d*(1 + 3*p)*x^3))/x^3, x]", //
				"(a + b*x + c*x^2 + d*x^3)^(1 + p)/x^2", //
				1590);
	}

	// {1590}
	public void test1352() {
		check(//
				"Integrate[((a + b*x + c*x^2 + d*x^3)^p*(-3*a + b*(-2 + p)*x + c*(-1 + 2*p)*x^2 + 3*d*p*x^3))/x^4, x]", //
				"(a + b*x + c*x^2 + d*x^3)^(1 + p)/x^3", //
				1590);
	}

	// {261}
	public void test1354() {
		check(//
				"Integrate[x/(1 - x^2)^5, x]", //
				"1/(8*(1 - x^2)^4)", //
				261);
	}

	// {1587}
	public void test1357() {
		check(//
				"Integrate[(1 + x^2)/(3*x + x^3), x]", //
				"Log[3*x + x^3]/3", //
				1587);
	}

	// {1587}
	public void test1358() {
		check(//
				"Integrate[(a + 3*b*x^2)/(a*x + b*x^3), x]", //
				"Log[a*x + b*x^3]", //
				1587);
	}

	// {1587}
	public void test1359() {
		check(//
				"Integrate[(-x + 2*x^3)/(1 - x^2 + x^4), x]", //
				"Log[1 - x^2 + x^4]/2", //
				1587);
	}

	// {1588}
	public void test1360() {
		check(//
				"Integrate[(x + 2*x^3)/(x^2 + x^4)^3, x]", //
				"-1/(4*(x^2 + x^4)^2)", //
				1588);
	}

	// {260}
	public void test1361() {
		check(//
				"Integrate[x/(-1 + x^2), x]", //
				"Log[1 - x^2]/2", //
				260);
	}

	// {31}
	public void test1362() {
		check(//
				"Integrate[(2 + 3*x)^(-1), x]", //
				"Log[2 + 3*x]/3", //
				31);
	}

	// {203}
	public void test1363() {
		check(//
				"Integrate[(a^2 + x^2)^(-1), x]", //
				"ArcTan[x/a]/a", //
				203);
	}

	// {205}
	public void test1364() {
		check(//
				"Integrate[(a + b*x^2)^(-1), x]", //
				"ArcTan[(Sqrt[b]*x)/Sqrt[a]]/(Sqrt[a]*Sqrt[b])", //
				205);
	}

	// {683}
	public void test1365() {
		check(//
				"Integrate[(2*x + x^2)/(1 + x)^2, x]", //
				"x + (1 + x)^(-1)", //
				683);
	}

	// {32}
	public void test1366() {
		check(//
				"Integrate[(-2 + 7*x)^3, x]", //
				"(2 - 7*x)^4/28", //
				32);
	}

	// {2148}
	public void test1367() {
		check(//
				"Integrate[1/((c + d*x)*(-c^3 + d^3*x^3)^(1/3)), x]", //
				"(Sqrt[3]*ArcTan[(1 - (2^(1/3)*(c - d*x))/(-c^3 + d^3*x^3)^(1/3))/Sqrt[3]])/(2*2^(1/3)*c*d) + Log[(c - d*x)*(c + d*x)^2]/(4*2^(1/3)*c*d) - (3*Log[d*(c - d*x) + 2^(2/3)*d*(-c^3 + d^3*x^3)^(1/3)])/(4*2^(1/3)*c*d)", //
				2148);
	}

	// {2151}
	public void test1368() {
		check(//
				"Integrate[(c - d*x)/((c + d*x)*(2*c^3 + d^3*x^3)^(1/3)), x]", //
				"-((Sqrt[3]*ArcTan[(1 + (2*(2*c + d*x))/(2*c^3 + d^3*x^3)^(1/3))/Sqrt[3]])/d) - Log[c + d*x]/d + (3*Log[d*(2*c + d*x) - d*(2*c^3 + d^3*x^3)^(1/3)])/(2*d)", //
				2151);
	}

	// {220}
	public void test1369() {
		check(//
				"Integrate[1/Sqrt[a + c*x^4], x]", //
				"((Sqrt[a] + Sqrt[c]*x^2)*Sqrt[(a + c*x^4)/(Sqrt[a] + Sqrt[c]*x^2)^2]*EllipticF[2*ArcTan[(c^(1/4)*x)/a^(1/4)], 1/2])/(2*a^(1/4)*c^(1/4)*Sqrt[a + c*x^4])", //
				220);
	}

	// {2129}
	public void test1370() {
		check(//
				"Integrate[Sqrt[1 + Sqrt[1 - x^2]], x]", //
				"(-2*x^3)/(3*(1 + Sqrt[1 - x^2])^(3/2)) + (2*x)/Sqrt[1 + Sqrt[1 - x^2]]", //
				2129);
	}

	// {2129}
	public void test1371() {
		check(//
				"Integrate[Sqrt[1 + Sqrt[1 + x^2]], x]", //
				"(2*x^3)/(3*(1 + Sqrt[1 + x^2])^(3/2)) + (2*x)/Sqrt[1 + Sqrt[1 + x^2]]", //
				2129);
	}

	// {2129}
	public void test1372() {
		check(//
				"Integrate[Sqrt[5 + Sqrt[25 + x^2]], x]", //
				"(2*x^3)/(3*(5 + Sqrt[25 + x^2])^(3/2)) + (10*x)/Sqrt[5 + Sqrt[25 + x^2]]", //
				2129);
	}

	// {2129}
	public void test1373() {
		check(//
				"Integrate[Sqrt[a + b*Sqrt[a^2/b^2 + c*x^2]], x]", //
				"(2*b^2*c*x^3)/(3*(a + b*Sqrt[a^2/b^2 + c*x^2])^(3/2)) + (2*a*x)/Sqrt[a + b*Sqrt[a^2/b^2 + c*x^2]]", //
				2129);
	}

	// {1588}
	public void test1374() {
		check(//
				"Integrate[(-1 + x^3)/(-4*x + x^4)^(2/3), x]", //
				"(3*(-4*x + x^4)^(1/3))/4", //
				1588);
	}

	// {1588}
	public void test1375() {
		check(//
				"Integrate[(2 - x^2)*(6*x - x^3)^(1/4), x]", //
				"(4*(6*x - x^3)^(5/4))/15", //
				1588);
	}

	// {1588}
	public void test1376() {
		check(//
				"Integrate[(1 + x^4)*Sqrt[5*x + x^5], x]", //
				"(2*(5*x + x^5)^(3/2))/15", //
				1588);
	}

	// {1588}
	public void test1377() {
		check(//
				"Integrate[(2 + 5*x^4)*Sqrt[2*x + x^5], x]", //
				"(2*(2*x + x^5)^(3/2))/3", //
				1588);
	}

	// {1588}
	public void test1378() {
		check(//
				"Integrate[(x + 3*x^2)/Sqrt[x^2 + 2*x^3], x]", //
				"Sqrt[x^2 + 2*x^3]", //
				1588);
	}

	// {1590}
	public void test1379() {
		check(//
				"Integrate[x*(a + b*x + c*x^2)^m*(d + e*x + f*x^2 + g*x^3)^n*(2*a*d + (3*b*d + 3*a*e + b*d*m + a*e*n)*x + (4*c*d + 4*b*e + 4*a*f + 2*c*d*m + b*e*m + b*e*n + 2*a*f*n)*x^2 + (5*c*e + 5*b*f + 5*a*g + 2*c*e*m + b*f*m + c*e*n + 2*b*f*n + 3*a*g*n)*x^3 + (6*c*f + 6*b*g + 2*c*f*m + b*g*m + 2*c*f*n + 3*b*g*n)*x^4 + c*g*(7 + 2*m + 3*n)*x^5), x]", //
				"x^2*(a + b*x + c*x^2)^(1 + m)*(d + e*x + f*x^2 + g*x^3)^(1 + n)", //
				1590);
	}

	// {1590}
	public void test1380() {
		check(//
				"Integrate[(a + b*x + c*x^2)^m*(d + e*x + f*x^2 + g*x^3)^n*(a*d + (2*b*d + 2*a*e + b*d*m + a*e*n)*x + (3*c*d + 3*b*e + 3*a*f + 2*c*d*m + b*e*m + b*e*n + 2*a*f*n)*x^2 + (4*c*e + 4*b*f + 4*a*g + 2*c*e*m + b*f*m + c*e*n + 2*b*f*n + 3*a*g*n)*x^3 + (5*c*f + 5*b*g + 2*c*f*m + b*g*m + 2*c*f*n + 3*b*g*n)*x^4 + c*g*(6 + 2*m + 3*n)*x^5), x]", //
				"x*(a + b*x + c*x^2)^(1 + m)*(d + e*x + f*x^2 + g*x^3)^(1 + n)", //
				1590);
	}

	// {1590}
	public void test1381() {
		check(//
				"Integrate[(a + b*x + c*x^2)^m*(d + e*x + f*x^2 + g*x^3)^n*(b*d + a*e + b*d*m + a*e*n + (2*c*d + 2*b*e + 2*a*f + 2*c*d*m + b*e*m + b*e*n + 2*a*f*n)*x + (3*c*e + 3*b*f + 3*a*g + 2*c*e*m + b*f*m + c*e*n + 2*b*f*n + 3*a*g*n)*x^2 + (4*c*f + 4*b*g + 2*c*f*m + b*g*m + 2*c*f*n + 3*b*g*n)*x^3 + c*g*(5 + 2*m + 3*n)*x^4), x]", //
				"(a + b*x + c*x^2)^(1 + m)*(d + e*x + f*x^2 + g*x^3)^(1 + n)", //
				1590);
	}

	// {216}
	public void test1382() {
		check(//
				"Integrate[1/Sqrt[4 - 9*x^2], x]", //
				"ArcSin[(3*x)/2]/3", //
				216);
	}

	// {32}
	public void test1384() {
		check(//
				"Integrate[1/Sqrt[1 - x], x]", //
				"-2*Sqrt[1 - x]", //
				32);
	}

	// {32}
	public void test1385() {
		check(//
				"Integrate[1/Sqrt[1 + x], x]", //
				"2*Sqrt[1 + x]", //
				32);
	}

	// {32}
	public void test1386() {
		check(//
				"Integrate[Sqrt[1 - x], x]", //
				"(-2*(1 - x)^(3/2))/3", //
				32);
	}

	// {32}
	public void test1387() {
		check(//
				"Integrate[Sqrt[1 + x], x]", //
				"(2*(1 + x)^(3/2))/3", //
				32);
	}

	// {216}
	public void test1388() {
		check(//
				"Integrate[1/Sqrt[1 - x^2], x]", //
				"ArcSin[x]", //
				216);
	}

	// {215}
	public void test1389() {
		check(//
				"Integrate[1/Sqrt[1 + x^2], x]", //
				"ArcSinh[x]", //
				215);
	}

	// {629}
	public void test1394() {
		check(//
				"Integrate[(3 + x)/(6*x + x^2)^(1/3), x]", //
				"(3*(6*x + x^2)^(2/3))/4", //
				629);
	}

	// {636}
	public void test1395() {
		check(//
				"Integrate[(4 + x)/(6*x - x^2)^(3/2), x]", //
				"-(12 - 7*x)/(9*Sqrt[6*x - x^2])", //
				636);
	}

	// {629}
	public void test1396() {
		check(//
				"Integrate[(-1 + x)/Sqrt[2*x - x^2], x]", //
				"-Sqrt[2*x - x^2]", //
				629);
	}

	// {651}
	public void test1397() {
		check(//
				"Integrate[1/((1 + x)^(2/3)*(-1 + x^2)^(2/3)), x]", //
				"(3*(-1 + x^2)^(1/3))/(2*(1 + x)^(2/3))", //
				651);
	}

	// {629}
	public void test1398() {
		check(//
				"Integrate[(1 + 2*x)/Sqrt[x + x^2], x]", //
				"2*Sqrt[x + x^2]", //
				629);
	}

	// {650}
	public void test1399() {
		check(//
				"Integrate[1/(x*Sqrt[6*x - x^2]), x]", //
				"-Sqrt[6*x - x^2]/(3*x)", //
				650);
	}

	// {2084}
	public void test1402() {
		check(//
				"Integrate[(e*f - e*f*x^2)/((a*d + b*d*x + a*d*x^2)*Sqrt[a + b*x + c*x^2 + b*x^3 + a*x^4]), x]", //
				"(e*f*ArcTan[(a*b + (4*a^2 + b^2 - 2*a*c)*x + a*b*x^2)/(2*a*Sqrt[2*a - c]*Sqrt[a + b*x + c*x^2 + b*x^3 + a*x^4])])/(a*Sqrt[2*a - c]*d)", //
				2084);
	}

	// {2085}
	public void test1403() {
		check(//
				"Integrate[(e*f - e*f*x^2)/((-(a*d) + b*d*x - a*d*x^2)*Sqrt[-a + b*x + c*x^2 + b*x^3 - a*x^4]), x]", //
				"(e*f*ArcTanh[(a*b - (4*a^2 + b^2 + 2*a*c)*x + a*b*x^2)/(2*a*Sqrt[2*a + c]*Sqrt[-a + b*x + c*x^2 + b*x^3 - a*x^4])])/(a*Sqrt[2*a + c]*d)", //
				2085);
	}
}
