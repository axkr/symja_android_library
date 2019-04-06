package org.matheclipse.core.rubi.step02;

public class SpecialFunctions extends AbstractRubiTestCase {

	static boolean init = true;

	public SpecialFunctions(String name) {
		super(name, false);
	}

	@Override
	protected void setUp() {
		try {
			super.setUp();
			if (init) {
				System.out.println("SpecialFunctions");
				init = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// {6798, 30}
	public void test0001() {
		check(//
				"Integrate[Derivative[1][u][x]*Derivative[2][u][x], x]", //
				"Derivative[1][u][x]^2/2", //
				6798, 30);
	}

	// {6798, 29}
	public void test0002() {
		check(//
				"Integrate[Derivative[1][f][x]/f[x], x]", //
				"Log[f[x]]", //
				6798, 29);
	}

	// {6798, 31}
	public void test0003() {
		check(//
				"Integrate[Derivative[1][f][x]/(a + b*f[x]), x]", //
				"Log[a + b*f[x]]/b", //
				6798, 31);
	}

	// {6798, 30}
	public void test0004() {
		check(//
				"Integrate[f[x]*Derivative[1][f][x], x]", //
				"f[x]^2/2", //
				6798, 30);
	}

	// {6798}
	public void test0005() {
		check(//
				"Integrate[(a + b*f[x])*Derivative[1][f][x], x]", //
				"a*f[x] + (b*f[x]^2)/2", //
				6798);
	}

	// {6798, 30}
	public void test0006() {
		check(//
				"Integrate[Derivative[1][f][x]/Sqrt[f[x]], x]", //
				"2*Sqrt[f[x]]", //
				6798, 30);
	}

	// {6798, 32}
	public void test0007() {
		check(//
				"Integrate[Derivative[1][f][x]/Sqrt[a + b*f[x]], x]", //
				"(2*Sqrt[a + b*f[x]])/b", //
				6798, 32);
	}

	// {6798, 30}
	public void test0008() {
		check(//
				"Integrate[f[x]^n*Derivative[1][f][x], x]", //
				"f[x]^(1 + n)/(1 + n)", //
				6798, 30);
	}

	// {6798, 32}
	public void test0009() {
		check(//
				"Integrate[(a + b*f[x])^n*Derivative[1][f][x], x]", //
				"(a + b*f[x])^(1 + n)/(b*(1 + n))", //
				6798, 32);
	}

	// {6798, 29}
	public void test0010() {
		check(//
				"Integrate[Derivative[2][f][x]/Derivative[1][f][x], x]", //
				"Log[Derivative[1][f][x]]", //
				6798, 29);
	}

	// {6798, 31}
	public void test0011() {
		check(//
				"Integrate[Derivative[2][f][x]/(a + b*Derivative[1][f][x]), x]", //
				"Log[a + b*Derivative[1][f][x]]/b", //
				6798, 31);
	}

	// {6798, 30}
	public void test0012() {
		check(//
				"Integrate[Derivative[1][f][x]*Derivative[2][f][x], x]", //
				"Derivative[1][f][x]^2/2", //
				6798, 30);
	}

	// {6798}
	public void test0013() {
		check(//
				"Integrate[(a + b*Derivative[1][f][x])*Derivative[2][f][x], x]", //
				"a*Derivative[1][f][x] + (b*Derivative[1][f][x]^2)/2", //
				6798);
	}

	// {6798, 30}
	public void test0014() {
		check(//
				"Integrate[Derivative[2][f][x]/Sqrt[Derivative[1][f][x]], x]", //
				"2*Sqrt[Derivative[1][f][x]]", //
				6798, 30);
	}

	// {6798, 32}
	public void test0015() {
		check(//
				"Integrate[Derivative[2][f][x]/Sqrt[a + b*Derivative[1][f][x]], x]", //
				"(2*Sqrt[a + b*Derivative[1][f][x]])/b", //
				6798, 32);
	}

	// {6798, 30}
	public void test0016() {
		check(//
				"Integrate[Derivative[1][f][x]^n*Derivative[2][f][x], x]", //
				"Derivative[1][f][x]^(1 + n)/(1 + n)", //
				6798, 30);
	}

	// {6798, 32}
	public void test0017() {
		check(//
				"Integrate[(a + b*Derivative[1][f][x])^n*Derivative[2][f][x], x]", //
				"(a + b*Derivative[1][f][x])^(1 + n)/(b*(1 + n))", //
				6798, 32);
	}

	// {6799, 209}
	public void test0018() {
		check(//
				"Integrate[(g[x]*Derivative[1][f][x] + f[x]*Derivative[1][g][x])/(1 + f[x]^2*g[x]^2), x]", //
				"ArcTan[f[x]*g[x]]", //
				6799, 209);
	}

	// {6843, 209}
	public void test0019() {
		check(//
				"Integrate[(g[x]*Derivative[1][f][x] - f[x]*Derivative[1][g][x])/(f[x]^2 + g[x]^2), x]", //
				"ArcTan[f[x]/g[x]]", //
				6843, 209);
	}

	// {6799, 209}
	public void test0020() {
		check(//
				"Integrate[-((g[x]*Derivative[1][f][x] + f[x]*Derivative[1][g][x])/(1 + f[x]^2*g[x]^2)), x]", //
				"-ArcTan[f[x]*g[x]]", //
				6799, 209);
	}

	// {6799, 212}
	public void test0021() {
		check(//
				"Integrate[(g[x]*Derivative[1][f][x] + f[x]*Derivative[1][g][x])/(1 - f[x]^2*g[x]^2), x]", //
				"ArcTanh[f[x]*g[x]]", //
				6799, 212);
	}

	// {6843, 213}
	public void test0022() {
		check(//
				"Integrate[(-(g[x]*Derivative[1][f][x]) + f[x]*Derivative[1][g][x])/(f[x]^2 - g[x]^2), x]", //
				"ArcTanh[f[x]/g[x]]", //
				6843, 213);
	}

	// {6806, 212}
	public void test0023() {
		check(//
				"Integrate[(f[x]^(-1 + m)*g[x]^(-1 + n)*(m*g[x]*Derivative[1][f][x] + n*f[x]*Derivative[1][g][x]))/(1 - f[x]^(2*m)*g[x]^(2*n)), x]", //
				"ArcTanh[f[x]^m*g[x]^n]", //
				6806, 212);
	}

	// {6799, 31}
	public void test0024() {
		check(//
				"Integrate[(g[x]*Derivative[1][f][x] + f[x]*Derivative[1][g][x])/(a + b*f[x]*g[x]), x]", //
				"Log[a + b*f[x]*g[x]]/b", //
				6799, 31);
	}

	// {6799, 211}
	public void test0025() {
		check(//
				"Integrate[(g[x]*Derivative[1][f][x] + f[x]*Derivative[1][g][x])/(a + b*f[x]^2*g[x]^2), x]", //
				"ArcTan[(Sqrt[b]*f[x]*g[x])/Sqrt[a]]/(Sqrt[a]*Sqrt[b])", //
				6799, 211);
	}

	// {6799, 251}
	public void test0026() {
		check(//
				"Integrate[(g[x]*Derivative[1][f][x] + f[x]*Derivative[1][g][x])/(a + b*(f[x]*g[x])^n), x]", //
				"(f[x]*g[x]*Hypergeometric2F1[1, n^(-1), 1 + n^(-1), -((b*(f[x]*g[x])^n)/a)])/a", //
				6799, 251);
	}

	// {6799, 2717}
	public void test0034() {
		check(//
				"Integrate[Cos[f[x]*g[x]]*(g[x]*Derivative[1][f][x] + f[x]*Derivative[1][g][x]), x]", //
				"Sin[f[x]*g[x]]", //
				6799, 2717);
	}

	// {6800, 2717}
	public void test0035() {
		check(//
				"Integrate[Cos[g[x]*Derivative[m][f][x]]*(Derivative[1][g][x]*Derivative[m][f][x] + g[x]*Derivative[1 + m][f][x]), x]", //
				"Sin[g[x]*Derivative[m][f][x]]", //
				6800, 2717);
	}

	// {6801, 2717}
	public void test0036() {
		check(//
				"Integrate[Cos[Derivative[-1 + m][f][x]*Derivative[-1 + n][g][x]]*(Derivative[m][f][x]*Derivative[-1 + n][g][x] + Derivative[-1 + m][f][x]*Derivative[n][g][x]), x]", //
				"Sin[Derivative[-1 + m][f][x]*Derivative[-1 + n][g][x]]", //
				6801, 2717);
	}

	// {6799, 211}
	public void test0037() {
		check(//
				"Integrate[(g[x]*Derivative[1][f][x] + f[x]*Derivative[1][g][x])/(a + b*f[x]^2*g[x]^2), x]", //
				"ArcTan[(Sqrt[b]*f[x]*g[x])/Sqrt[a]]/(Sqrt[a]*Sqrt[b])", //
				6799, 211);
	}

	// {6800, 211}
	public void test0038() {
		check(//
				"Integrate[(Derivative[1][g][x]*Derivative[m][f][x] + g[x]*Derivative[1 + m][f][x])/(a + b*g[x]^2*Derivative[m][f][x]^2), x]", //
				"ArcTan[(Sqrt[b]*g[x]*Derivative[m][f][x])/Sqrt[a]]/(Sqrt[a]*Sqrt[b])", //
				6800, 211);
	}

	// {6801, 211}
	public void test0039() {
		check(//
				"Integrate[(Derivative[1 + m][f][x]*Derivative[n][g][x] + Derivative[m][f][x]*Derivative[1 + n][g][x])/(a + b*Derivative[m][f][x]^2*Derivative[n][g][x]^2), x]", //
				"ArcTan[(Sqrt[b]*Derivative[m][f][x]*Derivative[n][g][x])/Sqrt[a]]/(Sqrt[a]*Sqrt[b])", //
				6801, 211);
	}

	// {6799, 6791}
	public void test0040() {
		check(//
				"Integrate[Derivative[1][F][f[x]*g[x]]*(g[x]*Derivative[1][f][x] + f[x]*Derivative[1][g][x]), x]", //
				"F[f[x]*g[x]]", //
				6799, 6791);
	}

	// {6800, 6791}
	public void test0041() {
		check(//
				"Integrate[Derivative[1][F][g[x]*Derivative[m][f][x]]*(Derivative[1][g][x]*Derivative[m][f][x] + g[x]*Derivative[1 + m][f][x]), x]", //
				"F[g[x]*Derivative[m][f][x]]", //
				6800, 6791);
	}

	// {6801, 6791}
	public void test0042() {
		check(//
				"Integrate[Derivative[1][F][Derivative[-1 + m][f][x]*Derivative[-1 + n][g][x]]*(Derivative[m][f][x]*Derivative[-1 + n][g][x] + Derivative[-1 + m][f][x]*Derivative[n][g][x]), x]", //
				"F[Derivative[-1 + m][f][x]*Derivative[-1 + n][g][x]]", //
				6801, 6791);
	}

	// {6802, 2717}
	public void test0043() {
		check(//
				"Integrate[Cos[f[x]^2*g[x]]*f[x]*(2*g[x]*Derivative[1][f][x] + f[x]*Derivative[1][g][x]), x]", //
				"Sin[f[x]^2*g[x]]", //
				6802, 2717);
	}

	// {6804, 2717}
	public void test0044() {
		check(//
				"Integrate[Cos[g[x]^2*Derivative[m][f][x]]*g[x]*(2*Derivative[1][g][x]*Derivative[m][f][x] + g[x]*Derivative[1 + m][f][x]), x]", //
				"Sin[g[x]^2*Derivative[m][f][x]]", //
				6804, 2717);
	}

	// {6803, 2717}
	public void test0045() {
		check(//
				"Integrate[Cos[g[x]*Derivative[m][f][x]^2]*Derivative[m][f][x]*(Derivative[1][g][x]*Derivative[m][f][x] + 2*g[x]*Derivative[1 + m][f][x]), x]", //
				"Sin[g[x]*Derivative[m][f][x]^2]", //
				6803, 2717);
	}

	// {6805, 2717}
	public void test0046() {
		check(//
				"Integrate[Cos[Derivative[-1 + m][f][x]^2*Derivative[-1 + n][g][x]]*Derivative[-1 + m][f][x]*(2*Derivative[m][f][x]*Derivative[-1 + n][g][x] + Derivative[-1 + m][f][x]*Derivative[n][g][x]), x]", //
				"Sin[Derivative[-1 + m][f][x]^2*Derivative[-1 + n][g][x]]", //
				6805, 2717);
	}

	// {6802, 211}
	public void test0047() {
		check(//
				"Integrate[(f[x]*(2*g[x]*Derivative[1][f][x] + f[x]*Derivative[1][g][x]))/(a + b*f[x]^4*g[x]^2), x]", //
				"ArcTan[(Sqrt[b]*f[x]^2*g[x])/Sqrt[a]]/(Sqrt[a]*Sqrt[b])", //
				6802, 211);
	}

	// {6804, 211}
	public void test0048() {
		check(//
				"Integrate[(g[x]*(2*Derivative[1][g][x]*Derivative[m][f][x] + g[x]*Derivative[1 + m][f][x]))/(a + b*g[x]^4*Derivative[m][f][x]^2), x]", //
				"ArcTan[(Sqrt[b]*g[x]^2*Derivative[m][f][x])/Sqrt[a]]/(Sqrt[a]*Sqrt[b])", //
				6804, 211);
	}

	// {6803, 211}
	public void test0049() {
		check(//
				"Integrate[(Derivative[m][f][x]*(Derivative[1][g][x]*Derivative[m][f][x] + 2*g[x]*Derivative[1 + m][f][x]))/(a + b*g[x]^2*Derivative[m][f][x]^4), x]", //
				"ArcTan[(Sqrt[b]*g[x]*Derivative[m][f][x]^2)/Sqrt[a]]/(Sqrt[a]*Sqrt[b])", //
				6803, 211);
	}

	// {6805, 211}
	public void test0050() {
		check(//
				"Integrate[(Derivative[m][f][x]*(2*Derivative[1 + m][f][x]*Derivative[n][g][x] + Derivative[m][f][x]*Derivative[1 + n][g][x]))/(a + b*Derivative[m][f][x]^4*Derivative[n][g][x]^2), x]", //
				"ArcTan[(Sqrt[b]*Derivative[m][f][x]^2*Derivative[n][g][x])/Sqrt[a]]/(Sqrt[a]*Sqrt[b])", //
				6805, 211);
	}

	// {6802, 6791}
	public void test0051() {
		check(//
				"Integrate[f[x]*Derivative[1][F][f[x]^2*g[x]]*(2*g[x]*Derivative[1][f][x] + f[x]*Derivative[1][g][x]), x]", //
				"F[f[x]^2*g[x]]", //
				6802, 6791);
	}

	// {6804, 6791}
	public void test0052() {
		check(//
				"Integrate[g[x]*Derivative[1][F][g[x]^2*Derivative[m][f][x]]*(2*Derivative[1][g][x]*Derivative[m][f][x] + g[x]*Derivative[1 + m][f][x]), x]", //
				"F[g[x]^2*Derivative[m][f][x]]", //
				6804, 6791);
	}

	// {6803, 6791}
	public void test0053() {
		check(//
				"Integrate[Derivative[1][F][g[x]*Derivative[m][f][x]^2]*Derivative[m][f][x]*(Derivative[1][g][x]*Derivative[m][f][x] + 2*g[x]*Derivative[1 + m][f][x]), x]", //
				"F[g[x]*Derivative[m][f][x]^2]", //
				6803, 6791);
	}

	// {6805, 6791}
	public void test0054() {
		check(//
				"Integrate[Derivative[1][F][Derivative[-1 + m][f][x]^2*Derivative[-1 + n][g][x]]*Derivative[-1 + m][f][x]*(2*Derivative[m][f][x]*Derivative[-1 + n][g][x] + Derivative[-1 + m][f][x]*Derivative[n][g][x]), x]", //
				"F[Derivative[-1 + m][f][x]^2*Derivative[-1 + n][g][x]]", //
				6805, 6791);
	}

	// {6806, 2717}
	public void test0055() {
		check(//
				"Integrate[Cos[f[x]^2*g[x]^3]*f[x]*g[x]^2*(2*g[x]*Derivative[1][f][x] + 3*f[x]*Derivative[1][g][x]), x]", //
				"Sin[f[x]^2*g[x]^3]", //
				6806, 2717);
	}

	// {6807, 2717}
	public void test0056() {
		check(//
				"Integrate[Cos[g[x]^3*Derivative[m][f][x]^2]*g[x]^2*Derivative[m][f][x]*(3*Derivative[1][g][x]*Derivative[m][f][x] + 2*g[x]*Derivative[1 + m][f][x]), x]", //
				"Sin[g[x]^3*Derivative[m][f][x]^2]", //
				6807, 2717);
	}

	// {6808, 2717}
	public void test0057() {
		check(//
				"Integrate[Cos[Derivative[m][f][x]^2*Derivative[n][g][x]^3]*Derivative[m][f][x]*Derivative[n][g][x]^2*(2*Derivative[1 + m][f][x]*Derivative[n][g][x] + 3*Derivative[m][f][x]*Derivative[1 + n][g][x]), x]", //
				"Sin[Derivative[m][f][x]^2*Derivative[n][g][x]^3]", //
				6808, 2717);
	}

	// {6806, 211}
	public void test0058() {
		check(//
				"Integrate[(f[x]*g[x]^2*(2*g[x]*Derivative[1][f][x] + 3*f[x]*Derivative[1][g][x]))/(a + b*f[x]^4*g[x]^6), x]", //
				"ArcTan[(Sqrt[b]*f[x]^2*g[x]^3)/Sqrt[a]]/(Sqrt[a]*Sqrt[b])", //
				6806, 211);
	}

	// {6807, 211}
	public void test0059() {
		check(//
				"Integrate[(g[x]^2*Derivative[m][f][x]*(3*Derivative[1][g][x]*Derivative[m][f][x] + 2*g[x]*Derivative[1 + m][f][x]))/(a + b*g[x]^6*Derivative[m][f][x]^4), x]", //
				"ArcTan[(Sqrt[b]*g[x]^3*Derivative[m][f][x]^2)/Sqrt[a]]/(Sqrt[a]*Sqrt[b])", //
				6807, 211);
	}

	// {6808, 211}
	public void test0060() {
		check(//
				"Integrate[(Derivative[m][f][x]*Derivative[n][g][x]^2*(2*Derivative[1 + m][f][x]*Derivative[n][g][x] + 3*Derivative[m][f][x]*Derivative[1 + n][g][x]))/(a + b*Derivative[m][f][x]^4*Derivative[n][g][x]^6), x]", //
				"ArcTan[(Sqrt[b]*Derivative[m][f][x]^2*Derivative[n][g][x]^3)/Sqrt[a]]/(Sqrt[a]*Sqrt[b])", //
				6808, 211);
	}

	// {6806, 6791}
	public void test0061() {
		check(//
				"Integrate[f[x]*g[x]^2*Derivative[1][F][f[x]^2*g[x]^3]*(2*g[x]*Derivative[1][f][x] + 3*f[x]*Derivative[1][g][x]), x]", //
				"F[f[x]^2*g[x]^3]", //
				6806, 6791);
	}

	// {6807, 6791}
	public void test0062() {
		check(//
				"Integrate[g[x]^2*Derivative[1][F][g[x]^3*Derivative[m][f][x]^2]*Derivative[m][f][x]*(3*Derivative[1][g][x]*Derivative[m][f][x] + 2*g[x]*Derivative[1 + m][f][x]), x]", //
				"F[g[x]^3*Derivative[m][f][x]^2]", //
				6807, 6791);
	}

	// {6808, 6791}
	public void test0063() {
		check(//
				"Integrate[Derivative[1][F][Derivative[m][f][x]^2*Derivative[n][g][x]^3]*Derivative[m][f][x]*Derivative[n][g][x]^2*(2*Derivative[1 + m][f][x]*Derivative[n][g][x] + 3*Derivative[m][f][x]*Derivative[1 + n][g][x]), x]", //
				"F[Derivative[m][f][x]^2*Derivative[n][g][x]^3]", //
				6808, 6791);
	}

	// {6496, 2241}
	public void test0064() {
		check(//
				"Integrate[Erf[b*x]/x^2, x]", //
				"-(Erf[b*x]/x) + (b*ExpIntegralEi[-(b^2*x^2)])/Sqrt[Pi]", //
				6496, 2241);
	}

	// {6508, 30}
	public void test0065() {
		check(//
				"Integrate[E^(c - b^2*x^2)*Erf[b*x]^2, x]", //
				"(E^c*Sqrt[Pi]*Erf[b*x]^3)/(6*b)", //
				6508, 30);
	}

	// {6508, 30}
	public void test0066() {
		check(//
				"Integrate[E^(c - b^2*x^2)*Erf[b*x], x]", //
				"(E^c*Sqrt[Pi]*Erf[b*x]^2)/(4*b)", //
				6508, 30);
	}

	// {6508, 29}
	public void test0067() {
		check(//
				"Integrate[E^(c - b^2*x^2)/Erf[b*x], x]", //
				"(E^c*Sqrt[Pi]*Log[Erf[b*x]])/(2*b)", //
				6508, 29);
	}

	// {6508, 30}
	public void test0068() {
		check(//
				"Integrate[E^(c - b^2*x^2)/Erf[b*x]^2, x]", //
				"-(E^c*Sqrt[Pi])/(2*b*Erf[b*x])", //
				6508, 30);
	}

	// {6508, 30}
	public void test0069() {
		check(//
				"Integrate[E^(c - b^2*x^2)/Erf[b*x]^3, x]", //
				"-(E^c*Sqrt[Pi])/(4*b*Erf[b*x]^2)", //
				6508, 30);
	}

	// {6508, 30}
	public void test0070() {
		check(//
				"Integrate[E^(c - b^2*x^2)*Erf[b*x]^n, x]", //
				"(E^c*Sqrt[Pi]*Erf[b*x]^(1 + n))/(2*b*(1 + n))", //
				6508, 30);
	}

	// {6517, 2236}
	public void test0071() {
		check(//
				"Integrate[E^(c + d*x^2)*x*Erf[b*x], x]", //
				"(E^(c + d*x^2)*Erf[b*x])/(2*d) - (b*E^c*Erf[Sqrt[b^2 - d]*x])/(2*Sqrt[b^2 - d]*d)", //
				6517, 2236);
	}

	// {6517, 8}
	public void test0074() {
		check(//
				"Integrate[E^(c + b^2*x^2)*x*Erf[b*x], x]", //
				"-((E^c*x)/(b*Sqrt[Pi])) + (E^(c + b^2*x^2)*Erf[b*x])/(2*b^2)", //
				6517, 8);
	}

	// {6517, 2236}
	public void test0075() {
		check(//
				"Integrate[(x*Erf[b*x])/E^(b^2*x^2), x]", //
				"-Erf[b*x]/(2*b^2*E^(b^2*x^2)) + Erf[Sqrt[2]*b*x]/(2*Sqrt[2]*b^2)", //
				6517, 2236);
	}

	// {6508, 30}
	public void test0076() {
		check(//
				"Integrate[Erf[b*x]/E^(b^2*x^2), x]", //
				"(Sqrt[Pi]*Erf[b*x]^2)/(4*b)", //
				6508, 30);
	}

	// {6494, 6493}
	public void test0077() {
		check(//
				"Integrate[Erfc[b*x]/x, x]", //
				"(-2*b*x*HypergeometricPFQ[{1/2, 1/2}, {3/2, 3/2}, -(b^2*x^2)])/Sqrt[Pi] + Log[x]", //
				6494, 6493);
	}

	// {6497, 2241}
	public void test0078() {
		check(//
				"Integrate[Erfc[b*x]/x^2, x]", //
				"-(Erfc[b*x]/x) - (b*ExpIntegralEi[-(b^2*x^2)])/Sqrt[Pi]", //
				6497, 2241);
	}

	// {6509, 30}
	public void test0079() {
		check(//
				"Integrate[E^(c - b^2*x^2)*Erfc[b*x]^2, x]", //
				"-(E^c*Sqrt[Pi]*Erfc[b*x]^3)/(6*b)", //
				6509, 30);
	}

	// {6509, 30}
	public void test0080() {
		check(//
				"Integrate[E^(c - b^2*x^2)*Erfc[b*x], x]", //
				"-(E^c*Sqrt[Pi]*Erfc[b*x]^2)/(4*b)", //
				6509, 30);
	}

	// {6509, 29}
	public void test0081() {
		check(//
				"Integrate[E^(c - b^2*x^2)/Erfc[b*x], x]", //
				"-(E^c*Sqrt[Pi]*Log[Erfc[b*x]])/(2*b)", //
				6509, 29);
	}

	// {6509, 30}
	public void test0082() {
		check(//
				"Integrate[E^(c - b^2*x^2)/Erfc[b*x]^2, x]", //
				"(E^c*Sqrt[Pi])/(2*b*Erfc[b*x])", //
				6509, 30);
	}

	// {6509, 30}
	public void test0083() {
		check(//
				"Integrate[E^(c - b^2*x^2)/Erfc[b*x]^3, x]", //
				"(E^c*Sqrt[Pi])/(4*b*Erfc[b*x]^2)", //
				6509, 30);
	}

	// {6509, 30}
	public void test0084() {
		check(//
				"Integrate[E^(c - b^2*x^2)*Erfc[b*x]^n, x]", //
				"-(E^c*Sqrt[Pi]*Erfc[b*x]^(1 + n))/(2*b*(1 + n))", //
				6509, 30);
	}

	// {6518, 2236}
	public void test0085() {
		check(//
				"Integrate[E^(c + d*x^2)*x*Erfc[b*x], x]", //
				"(b*E^c*Erf[Sqrt[b^2 - d]*x])/(2*Sqrt[b^2 - d]*d) + (E^(c + d*x^2)*Erfc[b*x])/(2*d)", //
				6518, 2236);
	}

	// {6518, 8}
	public void test0088() {
		check(//
				"Integrate[E^(c + b^2*x^2)*x*Erfc[b*x], x]", //
				"(E^c*x)/(b*Sqrt[Pi]) + (E^(c + b^2*x^2)*Erfc[b*x])/(2*b^2)", //
				6518, 8);
	}

	// {6518, 2236}
	public void test0089() {
		check(//
				"Integrate[(x*Erfc[b*x])/E^(b^2*x^2), x]", //
				"-Erf[Sqrt[2]*b*x]/(2*Sqrt[2]*b^2) - Erfc[b*x]/(2*b^2*E^(b^2*x^2))", //
				6518, 2236);
	}

	// {6509, 30}
	public void test0090() {
		check(//
				"Integrate[Erfc[b*x]/E^(b^2*x^2), x]", //
				"-(Sqrt[Pi]*Erfc[b*x]^2)/(4*b)", //
				6509, 30);
	}

	// {6498, 2241}
	public void test0091() {
		check(//
				"Integrate[Erfi[b*x]/x^2, x]", //
				"-(Erfi[b*x]/x) + (b*ExpIntegralEi[b^2*x^2])/Sqrt[Pi]", //
				6498, 2241);
	}

	// {6510, 30}
	public void test0092() {
		check(//
				"Integrate[E^(c + b^2*x^2)*Erfi[b*x]^2, x]", //
				"(E^c*Sqrt[Pi]*Erfi[b*x]^3)/(6*b)", //
				6510, 30);
	}

	// {6510, 30}
	public void test0093() {
		check(//
				"Integrate[E^(c + b^2*x^2)*Erfi[b*x], x]", //
				"(E^c*Sqrt[Pi]*Erfi[b*x]^2)/(4*b)", //
				6510, 30);
	}

	// {6510, 29}
	public void test0094() {
		check(//
				"Integrate[E^(c + b^2*x^2)/Erfi[b*x], x]", //
				"(E^c*Sqrt[Pi]*Log[Erfi[b*x]])/(2*b)", //
				6510, 29);
	}

	// {6510, 30}
	public void test0095() {
		check(//
				"Integrate[E^(c + b^2*x^2)/Erfi[b*x]^2, x]", //
				"-(E^c*Sqrt[Pi])/(2*b*Erfi[b*x])", //
				6510, 30);
	}

	// {6510, 30}
	public void test0096() {
		check(//
				"Integrate[E^(c + b^2*x^2)/Erfi[b*x]^3, x]", //
				"-(E^c*Sqrt[Pi])/(4*b*Erfi[b*x]^2)", //
				6510, 30);
	}

	// {6510, 30}
	public void test0097() {
		check(//
				"Integrate[E^(c + b^2*x^2)*Erfi[b*x]^n, x]", //
				"(E^c*Sqrt[Pi]*Erfi[b*x]^(1 + n))/(2*b*(1 + n))", //
				6510, 30);
	}

	// {6519, 2235}
	public void test0098() {
		check(//
				"Integrate[E^(c + d*x^2)*x*Erfi[b*x], x]", //
				"(E^(c + d*x^2)*Erfi[b*x])/(2*d) - (b*E^c*Erfi[Sqrt[b^2 + d]*x])/(2*d*Sqrt[b^2 + d])", //
				6519, 2235);
	}

	// {6519, 8}
	public void test0101() {
		check(//
				"Integrate[(x*Erfi[b*x])/E^(b^2*x^2), x]", //
				"x/(b*Sqrt[Pi]) - Erfi[b*x]/(2*b^2*E^(b^2*x^2))", //
				6519, 8);
	}

	// {6519, 2235}
	public void test0102() {
		check(//
				"Integrate[E^(c + b^2*x^2)*x*Erfi[b*x], x]", //
				"(E^(c + b^2*x^2)*Erfi[b*x])/(2*b^2) - (E^c*Erfi[Sqrt[2]*b*x])/(2*Sqrt[2]*b^2)", //
				6519, 2235);
	}

	// {6510, 30}
	public void test0103() {
		check(//
				"Integrate[E^(c + b^2*x^2)*Erfi[b*x], x]", //
				"(E^c*Sqrt[Pi]*Erfi[b*x]^2)/(4*b)", //
				6510, 30);
	}

	// {6561, 3456}
	public void test0104() {
		check(//
				"Integrate[FresnelS[b*x]/x^2, x]", //
				"-(FresnelS[b*x]/x) + (b*SinIntegral[(b^2*Pi*x^2)/2])/2", //
				6561, 3456);
	}

	// {6575, 30}
	public void test0105() {
		check(//
				"Integrate[FresnelS[b*x]^2*Sin[(b^2*Pi*x^2)/2], x]", //
				"FresnelS[b*x]^3/(3*b)", //
				6575, 30);
	}

	// {6575, 30}
	public void test0106() {
		check(//
				"Integrate[FresnelS[b*x]*Sin[(b^2*Pi*x^2)/2], x]", //
				"FresnelS[b*x]^2/(2*b)", //
				6575, 30);
	}

	// {6575, 29}
	public void test0107() {
		check(//
				"Integrate[Sin[(b^2*Pi*x^2)/2]/FresnelS[b*x], x]", //
				"Log[FresnelS[b*x]]/b", //
				6575, 29);
	}

	// {6575, 30}
	public void test0108() {
		check(//
				"Integrate[Sin[(b^2*Pi*x^2)/2]/FresnelS[b*x]^2, x]", //
				"-(1/(b*FresnelS[b*x]))", //
				6575, 30);
	}

	// {6575, 30}
	public void test0109() {
		check(//
				"Integrate[Sin[(b^2*Pi*x^2)/2]/FresnelS[b*x]^3, x]", //
				"-1/(2*b*FresnelS[b*x]^2)", //
				6575, 30);
	}

	// {6575, 30}
	public void test0110() {
		check(//
				"Integrate[FresnelS[b*x]^n*Sin[(b^2*Pi*x^2)/2], x]", //
				"FresnelS[b*x]^(1 + n)/(b*(1 + n))", //
				6575, 30);
	}

	// {6587, 3432}
	public void test0111() {
		check(//
				"Integrate[x*FresnelS[b*x]*Sin[(b^2*Pi*x^2)/2], x]", //
				"-((Cos[(b^2*Pi*x^2)/2]*FresnelS[b*x])/(b^2*Pi)) + FresnelS[Sqrt[2]*b*x]/(2*Sqrt[2]*b^2*Pi)", //
				6587, 3432);
	}

	// {6575, 30}
	public void test0112() {
		check(//
				"Integrate[FresnelS[b*x]*Sin[(b^2*Pi*x^2)/2], x]", //
				"FresnelS[b*x]^2/(2*b)", //
				6575, 30);
	}

	// {6562, 3457}
	public void test0113() {
		check(//
				"Integrate[FresnelC[b*x]/x^2, x]", //
				"(b*CosIntegral[(b^2*Pi*x^2)/2])/2 - FresnelC[b*x]/x", //
				6562, 3457);
	}

	// {6576, 30}
	public void test0114() {
		check(//
				"Integrate[Cos[(b^2*Pi*x^2)/2]*FresnelC[b*x]^2, x]", //
				"FresnelC[b*x]^3/(3*b)", //
				6576, 30);
	}

	// {6576, 30}
	public void test0115() {
		check(//
				"Integrate[Cos[(b^2*Pi*x^2)/2]*FresnelC[b*x], x]", //
				"FresnelC[b*x]^2/(2*b)", //
				6576, 30);
	}

	// {6576, 29}
	public void test0116() {
		check(//
				"Integrate[Cos[(b^2*Pi*x^2)/2]/FresnelC[b*x], x]", //
				"Log[FresnelC[b*x]]/b", //
				6576, 29);
	}

	// {6576, 30}
	public void test0117() {
		check(//
				"Integrate[Cos[(b^2*Pi*x^2)/2]/FresnelC[b*x]^2, x]", //
				"-(1/(b*FresnelC[b*x]))", //
				6576, 30);
	}

	// {6576, 30}
	public void test0118() {
		check(//
				"Integrate[Cos[(b^2*Pi*x^2)/2]/FresnelC[b*x]^3, x]", //
				"-1/(2*b*FresnelC[b*x]^2)", //
				6576, 30);
	}

	// {6576, 30}
	public void test0119() {
		check(//
				"Integrate[Cos[(b^2*Pi*x^2)/2]*FresnelC[b*x]^n, x]", //
				"FresnelC[b*x]^(1 + n)/(b*(1 + n))", //
				6576, 30);
	}

	// {6588, 3432}
	public void test0120() {
		check(//
				"Integrate[x*Cos[(b^2*Pi*x^2)/2]*FresnelC[b*x], x]", //
				"-FresnelS[Sqrt[2]*b*x]/(2*Sqrt[2]*b^2*Pi) + (FresnelC[b*x]*Sin[(b^2*Pi*x^2)/2])/(b^2*Pi)", //
				6588, 3432);
	}

	// {6576, 30}
	public void test0121() {
		check(//
				"Integrate[Cos[(b^2*Pi*x^2)/2]*FresnelC[b*x], x]", //
				"FresnelC[b*x]^2/(2*b)", //
				6576, 30);
	}

	// {6611, 6610}
	public void test0122() {
		check(//
				"Integrate[ExpIntegralE[2, b*x]/x^2, x]", //
				"-(ExpIntegralE[2, b*x]/x) - b^2*x*HypergeometricPFQ[{1, 1, 1}, {2, 2, 2}, -(b*x)] + b*EulerGamma*Log[x] + (b*Log[b*x]^2)/2", //
				6611, 6610);
	}

	// {12, 2209}
	public void test0123() {
		check(//
				"Integrate[1/(b*E^(b*x)*x), x]", //
				"ExpIntegralEi[-(b*x)]/b", //
				12, 2209);
	}

	// {6611, 6610}
	public void test0124() {
		check(//
				"Integrate[ExpIntegralE[2, b*x]/x^2, x]", //
				"-(ExpIntegralE[2, b*x]/x) - b^2*x*HypergeometricPFQ[{1, 1, 1}, {2, 2, 2}, -(b*x)] + b*EulerGamma*Log[x] + (b*Log[b*x]^2)/2", //
				6611, 6610);
	}

	// {6614, 6608}
	public void test0125() {
		check(//
				"Integrate[(c + d*x)*ExpIntegralE[1, a + b*x], x]", //
				"-(((c + d*x)*ExpIntegralE[2, a + b*x])/b) - (d*ExpIntegralE[3, a + b*x])/b^2", //
				6614, 6608);
	}

	// {6614, 6608}
	public void test0126() {
		check(//
				"Integrate[(c + d*x)*ExpIntegralE[2, a + b*x], x]", //
				"-(((c + d*x)*ExpIntegralE[3, a + b*x])/b) - (d*ExpIntegralE[4, a + b*x])/b^2", //
				6614, 6608);
	}

	// {6614, 6608}
	public void test0127() {
		check(//
				"Integrate[(c + d*x)*ExpIntegralE[3, a + b*x], x]", //
				"-(((c + d*x)*ExpIntegralE[4, a + b*x])/b) - (d*ExpIntegralE[5, a + b*x])/b^2", //
				6614, 6608);
	}

	// {6614, 2209}
	public void test0129() {
		check(//
				"Integrate[(c + d*x)*ExpIntegralE[-1, a + b*x], x]", //
				"-((E^(-a - b*x)*(c + d*x))/(b*(a + b*x))) + (d*ExpIntegralEi[-a - b*x])/b^2", //
				6614, 2209);
	}

	// {6614, 6608}
	public void test0130() {
		check(//
				"Integrate[(c + d*x)*ExpIntegralE[-2, a + b*x], x]", //
				"-((d*E^(-a - b*x))/(b^2*(a + b*x))) - ((c + d*x)*ExpIntegralE[-1, a + b*x])/b", //
				6614, 6608);
	}

	// {6614, 6608}
	public void test0131() {
		check(//
				"Integrate[(c + d*x)*ExpIntegralE[-3, a + b*x], x]", //
				"-(((c + d*x)*ExpIntegralE[-2, a + b*x])/b) - (d*ExpIntegralE[-1, a + b*x])/b^2", //
				6614, 6608);
	}

	// {6614, 6608}
	public void test0134() {
		check(//
				"Integrate[(c + d*x)*ExpIntegralE[n, a + b*x], x]", //
				"-(((c + d*x)*ExpIntegralE[1 + n, a + b*x])/b) - (d*ExpIntegralE[2 + n, a + b*x])/b^2", //
				6614, 6608);
	}

	// {6618, 6610}
	public void test0135() {
		check(//
				"Integrate[ExpIntegralEi[b*x]/x, x]", //
				"b*x*HypergeometricPFQ[{1, 1, 1}, {2, 2, 2}, b*x] + EulerGamma*Log[x] + (ExpIntegralE[1, -(b*x)] + ExpIntegralEi[b*x])*Log[x] + Log[-(b*x)]^2/2", //
				6618, 6610);
	}

	// {6624, 2209}
	public void test0136() {
		check(//
				"Integrate[E^(a + b*x)*ExpIntegralEi[c + d*x], x]", //
				"(E^(a + b*x)*ExpIntegralEi[c + d*x])/b - (E^(a - (b*c)/d)*ExpIntegralEi[((b + d)*(c + d*x))/d])/b", //
				6624, 2209);
	}

	// {2207, 2225}
	public void test0137() {
		check(//
				"Integrate[x/E^(a*x), x]", //
				"-(1/(a^2*E^(a*x))) - x/(a*E^(a*x))", //
				2207, 2225);
	}

	// {2208, 2209}
	public void test0138() {
		check(//
				"Integrate[1/(E^(a*x)*x^2), x]", //
				"-(1/(E^(a*x)*x)) - a*ExpIntegralEi[-(a*x)]", //
				2208, 2209);
	}

	// {6694, 2209}
	public void test0139() {
		check(//
				"Integrate[Gamma[2, a*x]/x, x]", //
				"-E^(-(a*x)) + ExpIntegralEi[-(a*x)]", //
				6694, 2209);
	}

	// {6695, 6693}
	public void test0140() {
		check(//
				"Integrate[Gamma[-1, a*x]/x, x]", //
				"-Gamma[-1, a*x] - a*x*HypergeometricPFQ[{1, 1, 1}, {2, 2, 2}, -(a*x)] + EulerGamma*Log[x] + Log[a*x]^2/2", //
				6695, 6693);
	}

	// {2207, 2225}
	public void test0141() {
		check(//
				"Integrate[E^(-a - b*x)*(c + d*x), x]", //
				"-((d*E^(-a - b*x))/b^2) - (E^(-a - b*x)*(c + d*x))/b", //
				2207, 2225);
	}

	// {2208, 2209}
	public void test0142() {
		check(//
				"Integrate[E^(-a - b*x)/(c + d*x)^2, x]", //
				"-(E^(-a - b*x)/(d*(c + d*x))) - (b*E^(-a + (b*c)/d)*ExpIntegralEi[-((b*(c + d*x))/d)])/d^2", //
				2208, 2209);
	}

	// {6703, 6705}
	public void test0143() {
		check(//
				"Integrate[(c + d*x)*LogGamma[a + b*x], x]", //
				"-((d*PolyGamma[-3, a + b*x])/b^2) + ((c + d*x)*PolyGamma[-2, a + b*x])/b", //
				6703, 6705);
	}

	// {6706, 6705}
	public void test0146() {
		check(//
				"Integrate[(c + d*x)*PolyGamma[n, a + b*x], x]", //
				"-((d*PolyGamma[-2 + n, a + b*x])/b^2) + ((c + d*x)*PolyGamma[-1 + n, a + b*x])/b", //
				6706, 6705);
	}

	// {6707}
	public void test0149() {
		check(//
				"Integrate[PolyGamma[1, a + b*x]/x^2 - (b*PolyGamma[2, a + b*x])/x, x]", //
				"-(PolyGamma[1, a + b*x]/x)", //
				6707);
	}

	// {6707}
	public void test0150() {
		check(//
				"Integrate[PolyGamma[n, a + b*x]/x^2 - (b*PolyGamma[1 + n, a + b*x])/x, x]", //
				"-(PolyGamma[n, a + b*x]/x)", //
				6707);
	}

	// {6716, 6705}
	public void test0151() {
		check(//
				"Integrate[Zeta[2, a + b*x], x]", //
				"PolyGamma[0, a + b*x]/b", //
				6716, 6705);
	}

	// {6719, 6717}
	public void test0153() {
		check(//
				"Integrate[x*Zeta[s, a + b*x], x]", //
				"-(Zeta[-2 + s, a + b*x]/(b^2*(1 - s)*(2 - s))) + (x*Zeta[-1 + s, a + b*x])/(b*(1 - s))", //
				6719, 6717);
	}

	// {6720}
	public void test0155() {
		check(//
				"Integrate[Zeta[s, a + b*x]/x^2 + (b*s*Zeta[1 + s, a + b*x])/x, x]", //
				"-(Zeta[s, a + b*x]/x)", //
				6720);
	}

	// {6722}
	public void test0158() {
		check(//
				"Integrate[PolyLog[-3/2, a*x] + PolyLog[-1/2, a*x], x]", //
				"x*PolyLog[-1/2, a*x]", //
				6722);
	}

	// {12, 6816}
	public void test0159() {
		check(//
				"Integrate[(e*((a + b*x)/(c + d*x))^n)/((a + b*x)*(c + d*x)*(1 - e*((a + b*x)/(c + d*x))^n)), x]", //
				"-(Log[1 - e*((a + b*x)/(c + d*x))^n]/((b*c - a*d)*n))", //
				12, 6816);
	}

	// {12, 6818}
	public void test0160() {
		check(//
				"Integrate[(e*((a + b*x)/(c + d*x))^n)/((a + b*x)*(c + d*x)*(1 - e*((a + b*x)/(c + d*x))^n)^2), x]", //
				"1/((b*c - a*d)*n*(1 - e*((a + b*x)/(c + d*x))^n))", //
				12, 6818);
	}

	// {2320, 6724}
	public void test0161() {
		check(//
				"Integrate[PolyLog[n, d*(F^(c*(a + b*x)))^p], x]", //
				"PolyLog[1 + n, d*(F^(c*(a + b*x)))^p]/(b*c*p*Log[F])", //
				2320, 6724);
	}

	// {6748, 6760}
	public void test0162() {
		check(//
				"Integrate[ProductLog[a + b*x]^(-1), x]", //
				"ExpIntegralEi[ProductLog[a + b*x]]/b + (a + b*x)/(b*ProductLog[a + b*x])", //
				6748, 6760);
	}

	// {6747, 6760}
	public void test0163() {
		check(//
				"Integrate[ProductLog[a + b*x]^(-2), x]", //
				"(2*ExpIntegralEi[ProductLog[a + b*x]])/b - (a + b*x)/(b*ProductLog[a + b*x]^2)", //
				6747, 6760);
	}

	// {6748, 6761}
	public void test0164() {
		check(//
				"Integrate[1/Sqrt[c*ProductLog[a + b*x]], x]", //
				"(Sqrt[Pi]*Erfi[Sqrt[c*ProductLog[a + b*x]]/Sqrt[c]])/(2*b*Sqrt[c]) + (a + b*x)/(b*Sqrt[c*ProductLog[a + b*x]])", //
				6748, 6761);
	}

	// {6747, 6761}
	public void test0165() {
		check(//
				"Integrate[(c*ProductLog[a + b*x])^(-3/2), x]", //
				"(3*Sqrt[Pi]*Erfi[Sqrt[c*ProductLog[a + b*x]]/Sqrt[c]])/(b*c^(3/2)) - (2*(a + b*x))/(b*(c*ProductLog[a + b*x])^(3/2))", //
				6747, 6761);
	}

	// {6748, 6762}
	public void test0166() {
		check(//
				"Integrate[1/Sqrt[-(c*ProductLog[a + b*x])], x]", //
				"-(Sqrt[Pi]*Erf[Sqrt[-(c*ProductLog[a + b*x])]/Sqrt[c]])/(2*b*Sqrt[c]) + (a + b*x)/(b*Sqrt[-(c*ProductLog[a + b*x])])", //
				6748, 6762);
	}

	// {6747, 6762}
	public void test0167() {
		check(//
				"Integrate[(-(c*ProductLog[a + b*x]))^(-3/2), x]", //
				"(3*Sqrt[Pi]*Erf[Sqrt[-(c*ProductLog[a + b*x])]/Sqrt[c]])/(b*c^(3/2)) - (2*(a + b*x))/(b*(-(c*ProductLog[a + b*x]))^(3/2))", //
				6747, 6762);
	}

	// {6748, 6764}
	public void test0168() {
		check(//
				"Integrate[(c*ProductLog[a + b*x])^n, x]", //
				"((a + b*x)*(c*ProductLog[a + b*x])^n)/b - (n*Gamma[1 + n, -ProductLog[a + b*x]]*(c*ProductLog[a + b*x])^n)/(b*(-ProductLog[a + b*x])^n)", //
				6748, 6764);
	}

	// {6748, 6761}
	public void test0169() {
		check(//
				"Integrate[1/Sqrt[c*ProductLog[a + b*x]], x]", //
				"(Sqrt[Pi]*Erfi[Sqrt[c*ProductLog[a + b*x]]/Sqrt[c]])/(2*b*Sqrt[c]) + (a + b*x)/(b*Sqrt[c*ProductLog[a + b*x]])", //
				6748, 6761);
	}

	// {6748, 6762}
	public void test0170() {
		check(//
				"Integrate[1/Sqrt[-(c*ProductLog[a + b*x])], x]", //
				"-(Sqrt[Pi]*Erf[Sqrt[-(c*ProductLog[a + b*x])]/Sqrt[c]])/(2*b*Sqrt[c]) + (a + b*x)/(b*Sqrt[-(c*ProductLog[a + b*x])])", //
				6748, 6762);
	}

	// {6754, 6781}
	public void test0173() {
		check(//
				"Integrate[ProductLog[a*x]/x, x]", //
				"ProductLog[a*x] + ProductLog[a*x]^2/2", //
				6754, 6781);
	}

	// {6753, 6783}
	public void test0174() {
		check(//
				"Integrate[ProductLog[a*x]/x^2, x]", //
				"a*ExpIntegralEi[-ProductLog[a*x]] - ProductLog[a*x]/x", //
				6753, 6783);
	}

	// {6754, 6783}
	public void test0175() {
		check(//
				"Integrate[ProductLog[a*x]/x^3, x]", //
				"-(a^2*ExpIntegralEi[-2*ProductLog[a*x]]) - ProductLog[a*x]/x^2", //
				6754, 6783);
	}

	// {6754, 6781}
	public void test0176() {
		check(//
				"Integrate[ProductLog[a*x]^2/x, x]", //
				"ProductLog[a*x]^2/2 + ProductLog[a*x]^3/3", //
				6754, 6781);
	}

	// {6753, 6782}
	public void test0177() {
		check(//
				"Integrate[ProductLog[a*x]^2/x^2, x]", //
				"(-2*ProductLog[a*x])/x - ProductLog[a*x]^2/x", //
				6753, 6782);
	}

	// {6753, 6783}
	public void test0178() {
		check(//
				"Integrate[ProductLog[a*x]^2/x^3, x]", //
				"a^2*ExpIntegralEi[-2*ProductLog[a*x]] - ProductLog[a*x]^2/(2*x^2)", //
				6753, 6783);
	}

	// {6754, 6783}
	public void test0179() {
		check(//
				"Integrate[ProductLog[a*x]^2/x^4, x]", //
				"-2*a^3*ExpIntegralEi[-3*ProductLog[a*x]] - ProductLog[a*x]^2/x^3", //
				6754, 6783);
	}

	// {6754, 6781}
	public void test0180() {
		check(//
				"Integrate[ProductLog[a*x]^3/x, x]", //
				"ProductLog[a*x]^3/3 + ProductLog[a*x]^4/4", //
				6754, 6781);
	}

	// {6753, 6782}
	public void test0181() {
		check(//
				"Integrate[ProductLog[a*x]^3/x^3, x]", //
				"(-3*ProductLog[a*x]^2)/(4*x^2) - ProductLog[a*x]^3/(2*x^2)", //
				6753, 6782);
	}

	// {6753, 6783}
	public void test0182() {
		check(//
				"Integrate[ProductLog[a*x]^3/x^4, x]", //
				"a^3*ExpIntegralEi[-3*ProductLog[a*x]] - ProductLog[a*x]^3/(3*x^3)", //
				6753, 6783);
	}

	// {6754, 6783}
	public void test0183() {
		check(//
				"Integrate[ProductLog[a*x]^3/x^5, x]", //
				"-3*a^4*ExpIntegralEi[-4*ProductLog[a*x]] - ProductLog[a*x]^3/x^4", //
				6754, 6783);
	}

	// {6753, 6782}
	public void test0184() {
		check(//
				"Integrate[x/ProductLog[a*x], x]", //
				"x^2/(4*ProductLog[a*x]^2) + x^2/(2*ProductLog[a*x])", //
				6753, 6782);
	}

	// {6748, 6760}
	public void test0185() {
		check(//
				"Integrate[ProductLog[a*x]^(-1), x]", //
				"ExpIntegralEi[ProductLog[a*x]]/a + x/ProductLog[a*x]", //
				6748, 6760);
	}

	// {6754, 6776}
	public void test0186() {
		check(//
				"Integrate[1/(x*ProductLog[a*x]), x]", //
				"Log[ProductLog[a*x]] - ProductLog[a*x]^(-1)", //
				6754, 6776);
	}

	// {6753, 6782}
	public void test0187() {
		check(//
				"Integrate[x^2/ProductLog[a*x]^2, x]", //
				"(2*x^3)/(9*ProductLog[a*x]^3) + x^3/(3*ProductLog[a*x]^2)", //
				6753, 6782);
	}

	// {6753, 6783}
	public void test0188() {
		check(//
				"Integrate[x/ProductLog[a*x]^2, x]", //
				"ExpIntegralEi[2*ProductLog[a*x]]/a^2 + x^2/(2*ProductLog[a*x]^2)", //
				6753, 6783);
	}

	// {6747, 6760}
	public void test0189() {
		check(//
				"Integrate[ProductLog[a*x]^(-2), x]", //
				"(2*ExpIntegralEi[ProductLog[a*x]])/a - x/ProductLog[a*x]^2", //
				6747, 6760);
	}

	// {6754, 6781}
	public void test0190() {
		check(//
				"Integrate[1/(x*ProductLog[a*x]^2), x]", //
				"-1/(2*ProductLog[a*x]^2) - ProductLog[a*x]^(-1)", //
				6754, 6781);
	}

	// {6753, 6782}
	public void test0191() {
		check(//
				"Integrate[x^3/ProductLog[a*x]^3, x]", //
				"(3*x^4)/(16*ProductLog[a*x]^4) + x^4/(4*ProductLog[a*x]^3)", //
				6753, 6782);
	}

	// {6753, 6783}
	public void test0192() {
		check(//
				"Integrate[x^2/ProductLog[a*x]^3, x]", //
				"ExpIntegralEi[3*ProductLog[a*x]]/a^3 + x^3/(3*ProductLog[a*x]^3)", //
				6753, 6783);
	}

	// {6754, 6783}
	public void test0193() {
		check(//
				"Integrate[x/ProductLog[a*x]^3, x]", //
				"(3*ExpIntegralEi[2*ProductLog[a*x]])/a^2 - x^2/ProductLog[a*x]^3", //
				6754, 6783);
	}

	// {6754, 6781}
	public void test0194() {
		check(//
				"Integrate[1/(x*ProductLog[a*x]^3), x]", //
				"-1/(3*ProductLog[a*x]^3) - 1/(2*ProductLog[a*x]^2)", //
				6754, 6781);
	}

	// {6754, 6781}
	public void test0195() {
		check(//
				"Integrate[Sqrt[c*ProductLog[a*x]]/x, x]", //
				"2*Sqrt[c*ProductLog[a*x]] + (2*(c*ProductLog[a*x])^(3/2))/(3*c)", //
				6754, 6781);
	}

	// {6754, 6784}
	public void test0196() {
		check(//
				"Integrate[Sqrt[c*ProductLog[a*x]]/x^2, x]", //
				"-(a*Sqrt[c]*Sqrt[Pi]*Erf[Sqrt[c*ProductLog[a*x]]/Sqrt[c]]) - (2*Sqrt[c*ProductLog[a*x]])/x", //
				6754, 6784);
	}

	// {6748, 6761}
	public void test0197() {
		check(//
				"Integrate[1/Sqrt[c*ProductLog[a*x]], x]", //
				"(Sqrt[Pi]*Erfi[Sqrt[c*ProductLog[a*x]]/Sqrt[c]])/(2*a*Sqrt[c]) + x/Sqrt[c*ProductLog[a*x]]", //
				6748, 6761);
	}

	// {6754, 6781}
	public void test0198() {
		check(//
				"Integrate[1/(x*Sqrt[c*ProductLog[a*x]]), x]", //
				"-2/Sqrt[c*ProductLog[a*x]] + (2*Sqrt[c*ProductLog[a*x]])/c", //
				6754, 6781);
	}

	// {6754, 6781}
	public void test0199() {
		check(//
				"Integrate[(c*ProductLog[a*x])^p/x, x]", //
				"(c*ProductLog[a*x])^p/p + (c*ProductLog[a*x])^(1 + p)/(c*(1 + p))", //
				6754, 6781);
	}

	// {6754, 6781}
	public void test0200() {
		check(//
				"Integrate[ProductLog[a*x^2]/x, x]", //
				"ProductLog[a*x^2]/2 + ProductLog[a*x^2]^2/4", //
				6754, 6781);
	}

	// {6753, 6783}
	public void test0201() {
		check(//
				"Integrate[ProductLog[a*x^2]/x^3, x]", //
				"(a*ExpIntegralEi[-ProductLog[a*x^2]])/2 - ProductLog[a*x^2]/(2*x^2)", //
				6753, 6783);
	}

	// {6754, 6783}
	public void test0202() {
		check(//
				"Integrate[ProductLog[a*x^2]/x^5, x]", //
				"-(a^2*ExpIntegralEi[-2*ProductLog[a*x^2]])/2 - ProductLog[a*x^2]/(2*x^4)", //
				6754, 6783);
	}

	// {6754, 6781}
	public void test0203() {
		check(//
				"Integrate[ProductLog[a*x^2]^2/x, x]", //
				"ProductLog[a*x^2]^2/4 + ProductLog[a*x^2]^3/6", //
				6754, 6781);
	}

	// {6753, 6782}
	public void test0204() {
		check(//
				"Integrate[ProductLog[a*x^2]^2/x^3, x]", //
				"-(ProductLog[a*x^2]/x^2) - ProductLog[a*x^2]^2/(2*x^2)", //
				6753, 6782);
	}

	// {6753, 6783}
	public void test0205() {
		check(//
				"Integrate[ProductLog[a*x^2]^2/x^5, x]", //
				"(a^2*ExpIntegralEi[-2*ProductLog[a*x^2]])/2 - ProductLog[a*x^2]^2/(4*x^4)", //
				6753, 6783);
	}

	// {6754, 6783}
	public void test0206() {
		check(//
				"Integrate[ProductLog[a*x^2]^2/x^7, x]", //
				"-(a^3*ExpIntegralEi[-3*ProductLog[a*x^2]]) - ProductLog[a*x^2]^2/(2*x^6)", //
				6754, 6783);
	}

	// {6754, 6781}
	public void test0207() {
		check(//
				"Integrate[ProductLog[a*x^2]^3/x, x]", //
				"ProductLog[a*x^2]^3/6 + ProductLog[a*x^2]^4/8", //
				6754, 6781);
	}

	// {6753, 6782}
	public void test0208() {
		check(//
				"Integrate[ProductLog[a*x^2]^3/x^5, x]", //
				"(-3*ProductLog[a*x^2]^2)/(8*x^4) - ProductLog[a*x^2]^3/(4*x^4)", //
				6753, 6782);
	}

	// {6753, 6783}
	public void test0209() {
		check(//
				"Integrate[ProductLog[a*x^2]^3/x^7, x]", //
				"(a^3*ExpIntegralEi[-3*ProductLog[a*x^2]])/2 - ProductLog[a*x^2]^3/(6*x^6)", //
				6753, 6783);
	}

	// {6754, 6783}
	public void test0210() {
		check(//
				"Integrate[ProductLog[a*x^2]^3/x^9, x]", //
				"(-3*a^4*ExpIntegralEi[-4*ProductLog[a*x^2]])/2 - ProductLog[a*x^2]^3/(2*x^8)", //
				6754, 6783);
	}

	// {6753, 6782}
	public void test0211() {
		check(//
				"Integrate[x^3/ProductLog[a*x^2], x]", //
				"x^4/(8*ProductLog[a*x^2]^2) + x^4/(4*ProductLog[a*x^2])", //
				6753, 6782);
	}

	// {6753, 6783}
	public void test0212() {
		check(//
				"Integrate[x/ProductLog[a*x^2], x]", //
				"ExpIntegralEi[ProductLog[a*x^2]]/(2*a) + x^2/(2*ProductLog[a*x^2])", //
				6753, 6783);
	}

	// {6754, 6779}
	public void test0213() {
		check(//
				"Integrate[1/(x*ProductLog[a*x^2]), x]", //
				"Log[ProductLog[a*x^2]]/2 - 1/(2*ProductLog[a*x^2])", //
				6754, 6779);
	}

	// {6753, 6782}
	public void test0214() {
		check(//
				"Integrate[x^5/ProductLog[a*x^2]^2, x]", //
				"x^6/(9*ProductLog[a*x^2]^3) + x^6/(6*ProductLog[a*x^2]^2)", //
				6753, 6782);
	}

	// {6753, 6783}
	public void test0215() {
		check(//
				"Integrate[x^3/ProductLog[a*x^2]^2, x]", //
				"ExpIntegralEi[2*ProductLog[a*x^2]]/(2*a^2) + x^4/(4*ProductLog[a*x^2]^2)", //
				6753, 6783);
	}

	// {6754, 6783}
	public void test0216() {
		check(//
				"Integrate[x/ProductLog[a*x^2]^2, x]", //
				"ExpIntegralEi[ProductLog[a*x^2]]/a - x^2/(2*ProductLog[a*x^2]^2)", //
				6754, 6783);
	}

	// {6754, 6781}
	public void test0217() {
		check(//
				"Integrate[1/(x*ProductLog[a*x^2]^2), x]", //
				"-1/(4*ProductLog[a*x^2]^2) - 1/(2*ProductLog[a*x^2])", //
				6754, 6781);
	}

	// {6750, 6768}
	public void test0218() {
		check(//
				"Integrate[Sqrt[c*ProductLog[a*x^2]], x]", //
				"-((c*x)/Sqrt[c*ProductLog[a*x^2]]) + x*Sqrt[c*ProductLog[a*x^2]]", //
				6750, 6768);
	}

	// {6754, 6781}
	public void test0219() {
		check(//
				"Integrate[Sqrt[c*ProductLog[a*x^2]]/x, x]", //
				"Sqrt[c*ProductLog[a*x^2]] + (c*ProductLog[a*x^2])^(3/2)/(3*c)", //
				6754, 6781);
	}

	// {6754, 6784}
	public void test0220() {
		check(//
				"Integrate[Sqrt[c*ProductLog[a*x^2]]/x^3, x]", //
				"-(a*Sqrt[c]*Sqrt[Pi]*Erf[Sqrt[c*ProductLog[a*x^2]]/Sqrt[c]])/2 - Sqrt[c*ProductLog[a*x^2]]/x^2", //
				6754, 6784);
	}

	// {6753, 6782}
	public void test0221() {
		check(//
				"Integrate[x^2/Sqrt[c*ProductLog[a*x^2]], x]", //
				"(c*x^3)/(9*(c*ProductLog[a*x^2])^(3/2)) + x^3/(3*Sqrt[c*ProductLog[a*x^2]])", //
				6753, 6782);
	}

	// {6753, 6785}
	public void test0222() {
		check(//
				"Integrate[x/Sqrt[c*ProductLog[a*x^2]], x]", //
				"(Sqrt[Pi]*Erfi[Sqrt[c*ProductLog[a*x^2]]/Sqrt[c]])/(4*a*Sqrt[c]) + x^2/(2*Sqrt[c*ProductLog[a*x^2]])", //
				6753, 6785);
	}

	// {6754, 6781}
	public void test0223() {
		check(//
				"Integrate[1/(x*Sqrt[c*ProductLog[a*x^2]]), x]", //
				"-(1/Sqrt[c*ProductLog[a*x^2]]) + Sqrt[c*ProductLog[a*x^2]]/c", //
				6754, 6781);
	}

	// {6754, 6781}
	public void test0224() {
		check(//
				"Integrate[(c*ProductLog[a*x^2])^p/x, x]", //
				"(c*ProductLog[a*x^2])^p/(2*p) + (c*ProductLog[a*x^2])^(1 + p)/(2*c*(1 + p))", //
				6754, 6781);
	}

	// {6754, 6783}
	public void test0225() {
		check(//
				"Integrate[x*ProductLog[a/x], x]", //
				"a^2*ExpIntegralEi[-2*ProductLog[a/x]] + x^2*ProductLog[a/x]", //
				6754, 6783);
	}

	// {6754, 6781}
	public void test0226() {
		check(//
				"Integrate[ProductLog[a/x]/x, x]", //
				"-ProductLog[a/x] - ProductLog[a/x]^2/2", //
				6754, 6781);
	}

	// {6754, 6783}
	public void test0227() {
		check(//
				"Integrate[x^2*ProductLog[a/x]^2, x]", //
				"2*a^3*ExpIntegralEi[-3*ProductLog[a/x]] + x^3*ProductLog[a/x]^2", //
				6754, 6783);
	}

	// {6753, 6783}
	public void test0228() {
		check(//
				"Integrate[x*ProductLog[a/x]^2, x]", //
				"-(a^2*ExpIntegralEi[-2*ProductLog[a/x]]) + (x^2*ProductLog[a/x]^2)/2", //
				6753, 6783);
	}

	// {6750, 6768}
	public void test0229() {
		check(//
				"Integrate[ProductLog[a/x]^2, x]", //
				"2*x*ProductLog[a/x] + x*ProductLog[a/x]^2", //
				6750, 6768);
	}

	// {6754, 6781}
	public void test0230() {
		check(//
				"Integrate[ProductLog[a/x]^2/x, x]", //
				"-ProductLog[a/x]^2/2 - ProductLog[a/x]^3/3", //
				6754, 6781);
	}

	// {6751, 6771}
	public void test0231() {
		check(//
				"Integrate[Sqrt[ProductLog[a/x]], x]", //
				"a*Sqrt[Pi]*Erf[Sqrt[ProductLog[a/x]]] + 2*x*Sqrt[ProductLog[a/x]]", //
				6751, 6771);
	}

	// {6754, 6781}
	public void test0232() {
		check(//
				"Integrate[Sqrt[ProductLog[a/x]]/x, x]", //
				"-2*Sqrt[ProductLog[a/x]] - (2*ProductLog[a/x]^(3/2))/3", //
				6754, 6781);
	}

	// {6754, 6781}
	public void test0233() {
		check(//
				"Integrate[1/(x*Sqrt[ProductLog[a/x]]), x]", //
				"2/Sqrt[ProductLog[a/x]] - 2*Sqrt[ProductLog[a/x]]", //
				6754, 6781);
	}

	// {6753, 6785}
	public void test0234() {
		check(//
				"Integrate[1/(x^2*Sqrt[ProductLog[a/x]]), x]", //
				"-(Sqrt[Pi]*Erfi[Sqrt[ProductLog[a/x]]])/(2*a) - 1/(x*Sqrt[ProductLog[a/x]])", //
				6753, 6785);
	}

	// {6754, 6781}
	public void test0235() {
		check(//
				"Integrate[(c*ProductLog[a/x])^p/x, x]", //
				"-((c*ProductLog[a/x])^p/p) - (c*ProductLog[a/x])^(1 + p)/(c*(1 + p))", //
				6754, 6781);
	}

	// {6750, 6768}
	public void test0236() {
		check(//
				"Integrate[ProductLog[a/x^(1/4)]^5, x]", //
				"(5*x*ProductLog[a/x^(1/4)]^4)/4 + x*ProductLog[a/x^(1/4)]^5", //
				6750, 6768);
	}

	// {6750, 6768}
	public void test0237() {
		check(//
				"Integrate[ProductLog[a/x^(1/3)]^4, x]", //
				"(4*x*ProductLog[a/x^(1/3)]^3)/3 + x*ProductLog[a/x^(1/3)]^4", //
				6750, 6768);
	}

	// {6750, 6768}
	public void test0238() {
		check(//
				"Integrate[ProductLog[a/Sqrt[x]]^3, x]", //
				"(3*x*ProductLog[a/Sqrt[x]]^2)/2 + x*ProductLog[a/Sqrt[x]]^3", //
				6750, 6768);
	}

	// {6750, 6768}
	public void test0239() {
		check(//
				"Integrate[ProductLog[a/x]^2, x]", //
				"2*x*ProductLog[a/x] + x*ProductLog[a/x]^2", //
				6750, 6768);
	}

	// {6750, 6768}
	public void test0240() {
		check(//
				"Integrate[ProductLog[a*Sqrt[x]]^(-1), x]", //
				"x/(2*ProductLog[a*Sqrt[x]]^2) + x/ProductLog[a*Sqrt[x]]", //
				6750, 6768);
	}

	// {6750, 6768}
	public void test0241() {
		check(//
				"Integrate[ProductLog[a*x^(1/3)]^(-2), x]", //
				"(2*x)/(3*ProductLog[a*x^(1/3)]^3) + x/ProductLog[a*x^(1/3)]^2", //
				6750, 6768);
	}

	// {6750, 6768}
	public void test0242() {
		check(//
				"Integrate[ProductLog[a*x^(1/4)]^(-3), x]", //
				"(3*x)/(4*ProductLog[a*x^(1/4)]^4) + x/ProductLog[a*x^(1/4)]^3", //
				6750, 6768);
	}

	// {6751, 6769}
	public void test0243() {
		check(//
				"Integrate[ProductLog[a/x^(1/5)]^4, x]", //
				"20*a^5*ExpIntegralEi[-5*ProductLog[a/x^(1/5)]] + 5*x*ProductLog[a/x^(1/5)]^4", //
				6751, 6769);
	}

	// {6751, 6769}
	public void test0244() {
		check(//
				"Integrate[ProductLog[a/x^(1/4)]^3, x]", //
				"12*a^4*ExpIntegralEi[-4*ProductLog[a/x^(1/4)]] + 4*x*ProductLog[a/x^(1/4)]^3", //
				6751, 6769);
	}

	// {6751, 6769}
	public void test0245() {
		check(//
				"Integrate[ProductLog[a/x^(1/3)]^2, x]", //
				"6*a^3*ExpIntegralEi[-3*ProductLog[a/x^(1/3)]] + 3*x*ProductLog[a/x^(1/3)]^2", //
				6751, 6769);
	}

	// {6751, 6769}
	public void test0246() {
		check(//
				"Integrate[ProductLog[a/Sqrt[x]], x]", //
				"2*a^2*ExpIntegralEi[-2*ProductLog[a/Sqrt[x]]] + 2*x*ProductLog[a/Sqrt[x]]", //
				6751, 6769);
	}

	// {6747, 6760}
	public void test0247() {
		check(//
				"Integrate[ProductLog[a*x]^(-2), x]", //
				"(2*ExpIntegralEi[ProductLog[a*x]])/a - x/ProductLog[a*x]^2", //
				6747, 6760);
	}

	// {6751, 6769}
	public void test0248() {
		check(//
				"Integrate[ProductLog[a*Sqrt[x]]^(-3), x]", //
				"(6*ExpIntegralEi[2*ProductLog[a*Sqrt[x]]])/a^2 - (2*x)/ProductLog[a*Sqrt[x]]^3", //
				6751, 6769);
	}

	// {6751, 6769}
	public void test0249() {
		check(//
				"Integrate[ProductLog[a*x^(1/3)]^(-4), x]", //
				"(12*ExpIntegralEi[3*ProductLog[a*x^(1/3)]])/a^3 - (3*x)/ProductLog[a*x^(1/3)]^4", //
				6751, 6769);
	}

	// {6751, 6769}
	public void test0250() {
		check(//
				"Integrate[ProductLog[a*x^(1/4)]^(-5), x]", //
				"(20*ExpIntegralEi[4*ProductLog[a*x^(1/4)]])/a^4 - (4*x)/ProductLog[a*x^(1/4)]^5", //
				6751, 6769);
	}

	// {6750, 6768}
	public void test0251() {
		check(//
				"Integrate[ProductLog[a*x^n]^((-1 + n)/n), x]", //
				"((1 - n)*x)/ProductLog[a*x^n]^n^(-1) + x/ProductLog[a*x^n]^((1 - n)/n)", //
				6750, 6768);
	}

	// {6750, 6768}
	public void test0252() {
		check(//
				"Integrate[ProductLog[a*x^(1 - p)^(-1)]^p, x]", //
				"-((p*x*ProductLog[a*x^(1 - p)^(-1)]^(-1 + p))/(1 - p)) + x*ProductLog[a*x^(1 - p)^(-1)]^p", //
				6750, 6768);
	}

	// {6753, 6784}
	public void test0253() {
		check(//
				"Integrate[x^(-1 - n)*(c*ProductLog[a*x^n])^(3/2), x]", //
				"(3*a*c^(3/2)*Sqrt[Pi]*Erf[Sqrt[c*ProductLog[a*x^n]]/Sqrt[c]])/(2*n) - (c*ProductLog[a*x^n])^(3/2)/(n*x^n)", //
				6753, 6784);
	}

	// {6754, 6784}
	public void test0254() {
		check(//
				"Integrate[x^(-1 - n)*Sqrt[c*ProductLog[a*x^n]], x]", //
				"-((a*Sqrt[c]*Sqrt[Pi]*Erf[Sqrt[c*ProductLog[a*x^n]]/Sqrt[c]])/n) - (2*Sqrt[c*ProductLog[a*x^n]])/(n*x^n)", //
				6754, 6784);
	}

	// {6753, 6784}
	public void test0255() {
		check(//
				"Integrate[x^(-1 - 2*n)*(c*ProductLog[a*x^n])^(5/2), x]", //
				"(5*a^2*c^(5/2)*Sqrt[Pi/2]*Erf[(Sqrt[2]*Sqrt[c*ProductLog[a*x^n]])/Sqrt[c]])/(4*n) - (c*ProductLog[a*x^n])^(5/2)/(2*n*x^(2*n))", //
				6753, 6784);
	}

	// {6754, 6784}
	public void test0256() {
		check(//
				"Integrate[x^(-1 - 2*n)*(c*ProductLog[a*x^n])^(3/2), x]", //
				"(-3*a^2*c^(3/2)*Sqrt[Pi/2]*Erf[(Sqrt[2]*Sqrt[c*ProductLog[a*x^n]])/Sqrt[c]])/n - (2*(c*ProductLog[a*x^n])^(3/2))/(n*x^(2*n))", //
				6754, 6784);
	}

	// {6753, 6785}
	public void test0257() {
		check(//
				"Integrate[x^(-1 + n)/Sqrt[c*ProductLog[a*x^n]], x]", //
				"(Sqrt[Pi]*Erfi[Sqrt[c*ProductLog[a*x^n]]/Sqrt[c]])/(2*a*Sqrt[c]*n) + x^n/(n*Sqrt[c*ProductLog[a*x^n]])", //
				6753, 6785);
	}

	// {6754, 6785}
	public void test0258() {
		check(//
				"Integrate[x^(-1 + n)/(c*ProductLog[a*x^n])^(3/2), x]", //
				"(3*Sqrt[Pi]*Erfi[Sqrt[c*ProductLog[a*x^n]]/Sqrt[c]])/(a*c^(3/2)*n) - (2*x^n)/(n*(c*ProductLog[a*x^n])^(3/2))", //
				6754, 6785);
	}

	// {6753, 6785}
	public void test0259() {
		check(//
				"Integrate[x^(-1 + 2*n)/(c*ProductLog[a*x^n])^(3/2), x]", //
				"(3*Sqrt[Pi/2]*Erfi[(Sqrt[2]*Sqrt[c*ProductLog[a*x^n]])/Sqrt[c]])/(4*a^2*c^(3/2)*n) + x^(2*n)/(2*n*(c*ProductLog[a*x^n])^(3/2))", //
				6753, 6785);
	}

	// {6754, 6785}
	public void test0260() {
		check(//
				"Integrate[x^(-1 + 2*n)/(c*ProductLog[a*x^n])^(5/2), x]", //
				"(5*Sqrt[Pi/2]*Erfi[(Sqrt[2]*Sqrt[c*ProductLog[a*x^n]])/Sqrt[c]])/(a^2*c^(5/2)*n) - (2*x^(2*n))/(n*(c*ProductLog[a*x^n])^(5/2))", //
				6754, 6785);
	}

	// {6753, 6782}
	public void test0261() {
		check(//
				"Integrate[x^(-1 - 3*n)*ProductLog[a*x^n]^4, x]", //
				"(-4*ProductLog[a*x^n]^3)/(9*n*x^(3*n)) - ProductLog[a*x^n]^4/(3*n*x^(3*n))", //
				6753, 6782);
	}

	// {6753, 6782}
	public void test0262() {
		check(//
				"Integrate[x^(-1 - 2*n)*ProductLog[a*x^n]^3, x]", //
				"(-3*ProductLog[a*x^n]^2)/(4*n*x^(2*n)) - ProductLog[a*x^n]^3/(2*n*x^(2*n))", //
				6753, 6782);
	}

	// {6753, 6782}
	public void test0263() {
		check(//
				"Integrate[x^(-1 - n)*ProductLog[a*x^n]^2, x]", //
				"(-2*ProductLog[a*x^n])/(n*x^n) - ProductLog[a*x^n]^2/(n*x^n)", //
				6753, 6782);
	}

	// {6753, 6782}
	public void test0264() {
		check(//
				"Integrate[x^(-1 + 2*n)/ProductLog[a*x^n], x]", //
				"x^(2*n)/(4*n*ProductLog[a*x^n]^2) + x^(2*n)/(2*n*ProductLog[a*x^n])", //
				6753, 6782);
	}

	// {6753, 6782}
	public void test0265() {
		check(//
				"Integrate[x^(-1 + 3*n)/ProductLog[a*x^n]^2, x]", //
				"(2*x^(3*n))/(9*n*ProductLog[a*x^n]^3) + x^(3*n)/(3*n*ProductLog[a*x^n]^2)", //
				6753, 6782);
	}

	// {6753, 6782}
	public void test0266() {
		check(//
				"Integrate[x^(-1 + 4*n)/ProductLog[a*x^n]^3, x]", //
				"(3*x^(4*n))/(16*n*ProductLog[a*x^n]^4) + x^(4*n)/(4*n*ProductLog[a*x^n]^3)", //
				6753, 6782);
	}

	// {6753, 6782}
	public void test0269() {
		check(//
				"Integrate[x^(-1 + n*(1 - p))*(c*ProductLog[a*x^n])^p, x]", //
				"-((c*p*x^(n*(1 - p))*(c*ProductLog[a*x^n])^(-1 + p))/(n*(1 - p)^2)) + (x^(n*(1 - p))*(c*ProductLog[a*x^n])^p)/(n*(1 - p))", //
				6753, 6782);
	}

	// {6775, 6782}
	public void test0270() {
		check(//
				"Integrate[x/(1 + ProductLog[a*x]), x]", //
				"-x^2/(4*ProductLog[a*x]^2) + x^2/(2*ProductLog[a*x])", //
				6775, 6782);
	}

	// {6777, 6783}
	public void test0271() {
		check(//
				"Integrate[1/(x^2*(1 + ProductLog[a*x])), x]", //
				"-x^(-1) - a*ExpIntegralEi[-ProductLog[a*x]]", //
				6777, 6783);
	}

	// {6847, 6757}
	public void test0272() {
		check(//
				"Integrate[x/(1 + ProductLog[a*x^2]), x]", //
				"x^2/(2*ProductLog[a*x^2])", //
				6847, 6757);
	}

	// {6780, 6757}
	public void test0273() {
		check(//
				"Integrate[1/(x^2*(1 + ProductLog[a/x])), x]", //
				"-(1/(x*ProductLog[a/x]))", //
				6780, 6757);
	}

}
