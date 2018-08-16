package org.matheclipse.core.rubi;

public class RubiTests002 extends AbstractRubiTestCase {
	public RubiTests002(String name) {
		super(name);
	}

	@Override
	public void check(String evalString, String expectedResult) {
		System.out.println(getName());
		check(evalString, expectedResult, -1);
	}


	// {x/Sqrt(9+4*x^2), x, 1, Sqrt(9+4*x^2)/4}
	public void test00670() {
		check("Integrate(x/Sqrt(9+4*x^2), x)", "Sqrt(9+4*x^2)/4");

	}

	// {1/Sqrt(9+4*x^2), x, 1, ArcSinh((2*x)/3)/2}
	public void test00671() {
		check("Integrate(1/Sqrt(9+4*x^2), x)", "ArcSinh((2*x)/3)/2");

	}

	// {1/(x^2*Sqrt(9+4*x^2)), x, 1, -Sqrt(9+4*x^2)/(9*x)}
	public void test00672() {
		check("Integrate(1/(x^2*Sqrt(9+4*x^2)), x)", "-Sqrt(9+4*x^2)/(9*x)");

	}

	// {x/Sqrt(9-4*x^2), x, 1, -Sqrt(9-4*x^2)/4}
	public void test00673() {
		check("Integrate(x/Sqrt(9-4*x^2), x)", "-Sqrt(9-4*x^2)/4");

	}

	// {1/Sqrt(9-4*x^2), x, 1, ArcSin((2*x)/3)/2}
	public void test00674() {
		check("Integrate(1/Sqrt(9-4*x^2), x)", "ArcSin((2*x)/3)/2");

	}

	// {1/(x^2*Sqrt(9-4*x^2)), x, 1, -Sqrt(9-4*x^2)/(9*x)}
	public void test00675() {
		check("Integrate(1/(x^2*Sqrt(9-4*x^2)), x)", "-Sqrt(9-4*x^2)/(9*x)");

	}

	// {x/Sqrt(-9+4*x^2), x, 1, Sqrt(-9+4*x^2)/4}
	public void test00676() {
		check("Integrate(x/Sqrt(-9+4*x^2), x)", "Sqrt(-9+4*x^2)/4");

	}

	// {1/(x^2*Sqrt(-9+4*x^2)), x, 1, Sqrt(-9+4*x^2)/(9*x)}
	public void test00677() {
		check("Integrate(1/(x^2*Sqrt(-9+4*x^2)), x)", "Sqrt(-9+4*x^2)/(9*x)");

	}

	// {x/Sqrt(-9-4*x^2), x, 1, -Sqrt(-9-4*x^2)/4}
	public void test00678() {
		check("Integrate(x/Sqrt(-9-4*x^2), x)", "-Sqrt(-9-4*x^2)/4");

	}

	// {1/(x^2*Sqrt(-9-4*x^2)), x, 1, Sqrt(-9-4*x^2)/(9*x)}
	public void test00679() {
		check("Integrate(1/(x^2*Sqrt(-9-4*x^2)), x)", "Sqrt(-9-4*x^2)/(9*x)");

	}

	// {1/Sqrt(9+b*x^2), x, 1, ArcSinh((Sqrt(b)*x)/3)/Sqrt(b)}
	public void test00680() {
		check("Integrate(1/Sqrt(9+b*x^2), x)", "ArcSinh((Sqrt(b)*x)/3)/Sqrt(b)");

	}

	// {1/Sqrt(9-b*x^2), x, 1, ArcSin((Sqrt(b)*x)/3)/Sqrt(b)}
	public void test00681() {
		check("Integrate(1/Sqrt(9-b*x^2), x)", "ArcSin((Sqrt(b)*x)/3)/Sqrt(b)");

	}

	// {1/Sqrt(Pi+b*x^2), x, 1, ArcSinh((Sqrt(b)*x)/Sqrt(Pi))/Sqrt(b)}
	public void test00682() {
		check("Integrate(1/Sqrt(Pi+b*x^2), x)", "ArcSinh((Sqrt(b)*x)/Sqrt(Pi))/Sqrt(b)");

	}

	// {1/Sqrt(Pi-b*x^2), x, 1, ArcSin((Sqrt(b)*x)/Sqrt(Pi))/Sqrt(b)}
	public void test00683() {
		check("Integrate(1/Sqrt(Pi-b*x^2), x)", "ArcSin((Sqrt(b)*x)/Sqrt(Pi))/Sqrt(b)");

	}

	// {(x^(1+m)*(a*(2+m)+b*(3+m)*x^2))/Sqrt(a+b*x^2), x, 1, x^(2+m)*Sqrt(a+b*x^2)}
	public void test00684() {
		check("Integrate((x^(1+m)*(a*(2+m)+b*(3+m)*x^2))/Sqrt(a+b*x^2), x)", "x^(2+m)*Sqrt(a+b*x^2)");

	}

	// {(x^(-1+m)*(a*m+b*(-1+m)*x^2))/(a+b*x^2)^(3/2), x, 1, x^m/Sqrt(a+b*x^2)}
	public void test00685() {
		check("Integrate((x^(-1+m)*(a*m+b*(-1+m)*x^2))/(a+b*x^2)^(3/2), x)", "x^m/Sqrt(a+b*x^2)");

	}

	// {x*(a+b*x^2)^(1/3), x, 1, (3*(a+b*x^2)^(4/3))/(8*b)}
	public void test00686() {
		check("Integrate(x*(a+b*x^2)^(1/3), x)", "(3*(a+b*x^2)^(4/3))/(8*b)");

	}

	// {x*(a+b*x^2)^(2/3), x, 1, (3*(a+b*x^2)^(5/3))/(10*b)}
	public void test00687() {
		check("Integrate(x*(a+b*x^2)^(2/3), x)", "(3*(a+b*x^2)^(5/3))/(10*b)");

	}

	// {x*(a+b*x^2)^(4/3), x, 1, (3*(a+b*x^2)^(7/3))/(14*b)}
	public void test00688() {
		check("Integrate(x*(a+b*x^2)^(4/3), x)", "(3*(a+b*x^2)^(7/3))/(14*b)");

	}

	// {x*(-1+x^2)^(7/3), x, 1, (3*(-1+x^2)^(10/3))/20}
	public void test00689() {
		check("Integrate(x*(-1+x^2)^(7/3), x)", "(3*(-1+x^2)^(10/3))/20");

	}

	// {x/(a+b*x^2)^(1/3), x, 1, (3*(a+b*x^2)^(2/3))/(4*b)}
	public void test00690() {
		check("Integrate(x/(a+b*x^2)^(1/3), x)", "(3*(a+b*x^2)^(2/3))/(4*b)");

	}

	// {x/(a+b*x^2)^(2/3), x, 1, (3*(a+b*x^2)^(1/3))/(2*b)}
	public void test00691() {
		check("Integrate(x/(a+b*x^2)^(2/3), x)", "(3*(a+b*x^2)^(1/3))/(2*b)");

	}

	// {x/(a+b*x^2)^(4/3), x, 1, -3/(2*b*(a+b*x^2)^(1/3))}
	public void test00692() {
		check("Integrate(x/(a+b*x^2)^(4/3), x)", "-3/(2*b*(a+b*x^2)^(1/3))");

	}

	// {(a+b*x^2)^(1/3)/(c*x)^(11/3), x, 1, (-3*(a+b*x^2)^(4/3))/(8*a*c*(c*x)^(8/3))}
	public void test00693() {
		check("Integrate((a+b*x^2)^(1/3)/(c*x)^(11/3), x)", "(-3*(a+b*x^2)^(4/3))/(8*a*c*(c*x)^(8/3))");

	}

	// {(a+b*x^2)^(4/3)/(c*x)^(17/3), x, 1, (-3*(a+b*x^2)^(7/3))/(14*a*c*(c*x)^(14/3))}
	public void test00694() {
		check("Integrate((a+b*x^2)^(4/3)/(c*x)^(17/3), x)", "(-3*(a+b*x^2)^(7/3))/(14*a*c*(c*x)^(14/3))");

	}

	// {1/((c*x)^(5/3)*(a+b*x^2)^(2/3)), x, 1, (-3*(a+b*x^2)^(1/3))/(2*a*c*(c*x)^(2/3))}
	public void test00695() {
		check("Integrate(1/((c*x)^(5/3)*(a+b*x^2)^(2/3)), x)", "(-3*(a+b*x^2)^(1/3))/(2*a*c*(c*x)^(2/3))");

	}

	// {(2-3*x^2)^(-1/4), x, 1, (2*2^(1/4)*EllipticE(ArcSin(Sqrt(3/2)*x)/2, 2))/Sqrt(3)}
	public void test00696() {
		check("Integrate((2-3*x^2)^(-1/4), x)", "(2*2^(1/4)*EllipticE(ArcSin(Sqrt(3/2)*x)/2, 2))/Sqrt(3)");

	}

	// {(2+3*x^2)^(-3/4), x, 1, (2^(3/4)*EllipticF(ArcTan(Sqrt(3/2)*x)/2, 2))/Sqrt(3)}
	public void test00697() {
		check("Integrate((2+3*x^2)^(-3/4), x)", "(2^(3/4)*EllipticF(ArcTan(Sqrt(3/2)*x)/2, 2))/Sqrt(3)");

	}

	// {(2-3*x^2)^(-3/4), x, 1, (2^(3/4)*EllipticF(ArcSin(Sqrt(3/2)*x)/2, 2))/Sqrt(3)}
	public void test00698() {
		check("Integrate((2-3*x^2)^(-3/4), x)", "(2^(3/4)*EllipticF(ArcSin(Sqrt(3/2)*x)/2, 2))/Sqrt(3)");

	}

	// {(a+b*x^2)^(1/4)/(c*x)^(7/2), x, 1, (-2*(a+b*x^2)^(5/4))/(5*a*c*(c*x)^(5/2))}
	public void test00699() {
		check("Integrate((a+b*x^2)^(1/4)/(c*x)^(7/2), x)", "(-2*(a+b*x^2)^(5/4))/(5*a*c*(c*x)^(5/2))");

	}
}
