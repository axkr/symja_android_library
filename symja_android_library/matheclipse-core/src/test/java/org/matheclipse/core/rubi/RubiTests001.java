package org.matheclipse.core.rubi;

/**
 * Tests for the Java port of the Rubi-rule-based integrator. 
 * Isn't used in Maven build process at the moment.
 * 
 */
public class RubiTests001 extends AbstractRubiTestCase {
	public RubiTests001(String name) {
		super(name);
	}

	@Override
	public void check(String evalString, String expectedResult) {
		System.out.println(getName());
		check(evalString, expectedResult, -1);
	}
 
	// {Sqrt(1+2*x), x, 1, (1+2*x)^(3/2)/3}
	public void test00001() {
		check("Integrate(Sqrt(1+2*x), x)", "(1+2*x)^(3/2)/3");

	}

	// {(1+x)/(2+2*x+x^2)^3, x, 1, -1/(4*(2+2*x+x^2)^2)}
	public void test00002() {
		check("Integrate((1+x)/(2+2*x+x^2)^3, x)", "-1/(4*(2+2*x+x^2)^2)");

	}

	// {x^5/Sqrt(1-x^6), x, 1, -Sqrt(1-x^6)/3}
	public void test00003() {
		check("Integrate(x^5/Sqrt(1-x^6), x)", "-Sqrt(1-x^6)/3");

	}

	// {(1+x^2)^(-3/2), x, 1, x/Sqrt(1+x^2)}
	public void test00004() {
		check("Integrate((1+x^2)^(-3/2), x)", "x/Sqrt(1+x^2)");

	}

	// {x^2*(27+8*x^3)^(2/3), x, 1, (27+8*x^3)^(5/3)/40}
	public void test00005() {
		check("Integrate(x^2*(27+8*x^3)^(2/3), x)", "(27+8*x^3)^(5/3)/40");

	}

	// {(Cos(x)+Sin(x))/(-Cos(x)+Sin(x))^(1/3), x, 1, (3*(-Cos(x)+Sin(x))^(2/3))/2}
	public void test00006() {
		check("Integrate((Cos(x)+Sin(x))/(-Cos(x)+Sin(x))^(1/3), x)", "(3*(-Cos(x)+Sin(x))^(2/3))/2");

	}

	// {x/(Sqrt(1+x^2)*Sqrt(1+Sqrt(1+x^2))), x, 1, 2*Sqrt(1+Sqrt(1+x^2))}
	public void test00007() {
		check("Integrate(x/(Sqrt(1+x^2)*Sqrt(1+Sqrt(1+x^2))), x)", "2*Sqrt(1+Sqrt(1+x^2))");

	}

	// {x*Sqrt(1+x^2), x, 1, (1+x^2)^(3/2)/3}
	public void test00008() {
		check("Integrate(x*Sqrt(1+x^2), x)", "(1+x^2)^(3/2)/3");

	}

	// {x*(-1+x^2)^9, x, 1, (1-x^2)^10/20}
	public void test00009() {
		check("Integrate(x*(-1+x^2)^9, x)", "(1-x^2)^10/20");

	}

	// {(3+2*x)/(7+6*x)^3, x, 1, -(3+2*x)^2/(8*(7+6*x)^2)}
	public void test00010() {
		check("Integrate((3+2*x)/(7+6*x)^3, x)", "-(3+2*x)^2/(8*(7+6*x)^2)");

	}

	// {x^4*(1+x^5)^5, x, 1, (1+x^5)^6/30}
	public void test00011() {
		check("Integrate(x^4*(1+x^5)^5, x)", "(1+x^5)^6/30");

	}

	// {x*Cos(x^2)*Sin(x^2), x, 1, Sin(x^2)^2/4}
	public void test00012() {
		check("Integrate(x*Cos(x^2)*Sin(x^2), x)", "Sin(x^2)^2/4");

	}

	// {(2+3*x)^(-1), x, 1, Log(2+3*x)/3}
	public void test00013() {
		check("Integrate((2+3*x)^(-1), x)", "Log(2+3*x)/3");

	}

	// {x*Log(x), x, 1, -x^2/4+(x^2*Log(x))/2}
	public void test00014() {
		check("Integrate(x*Log(x), x)", "-x^2/4+(x^2*Log(x))/2");
	}

	// {(1+t)^(-1), t, 1, Log(1+t)}
	public void test00015() {
		check("Integrate((1+t)^(-1), t)", "Log(1+t)");

	}

	// {Cot(x), x, 1, Log(Sin(x))}
	public void test00016() {
		check("Integrate(Cot(x), x)", "Log(Sin(x))");

	}

	// {x^n*Log(a*x), x, 1, -(x^(1+n)/(1+n)^2)+(x^(1+n)*Log(a*x))/(1+n)}
	public void test00017() {
		check("Integrate(x^n*Log(a*x), x)", "-(x^(1+n)/(1+n)^2)+(x^(1+n)*Log(a*x))/(1+n)");

	}

	// {E^x^3*x^2, x, 1, E^x^3/3}
	public void test00018() {
		check("Integrate(E^x^3*x^2, x)", "E^x^3/3");

	}

	// {2^Sqrt(x)/Sqrt(x), x, 1, 2^(1+Sqrt(x))/Log(2)}
	public void test00019() {
		check("Integrate(2^Sqrt(x)/Sqrt(x), x)", "2^(1+Sqrt(x))/Log(2)");

	}

	// {E^x*Sin(x), x, 1, -(E^x*Cos(x))/2+(E^x*Sin(x))/2}
	public void test00020() {
		check("Integrate(E^x*Sin(x), x)", "-(E^x*Cos(x))/2+(E^x*Sin(x))/2");

	}

	// {E^x*Cos(x), x, 1, (E^x*Cos(x))/2+(E^x*Sin(x))/2}
	public void test00021() {
		check("Integrate(E^x*Cos(x), x)", "(E^x*Cos(x))/2+(E^x*Sin(x))/2");

	}

	// {E^(a*x)*Cos(b*x), x, 1, (a*E^(a*x)*Cos(b*x))/(a^2+b^2)+(b*E^(a*x)*Sin(b*x))/(a^2+b^2)}
	public void test00022() {
		check("Integrate(E^(a*x)*Cos(b*x), x)", "(a*E^(a*x)*Cos(b*x))/(a^2+b^2)+(b*E^(a*x)*Sin(b*x))/(a^2+b^2)");

	}

	// {E^(a*x)*Sin(b*x), x, 1, -((b*E^(a*x)*Cos(b*x))/(a^2+b^2))+(a*E^(a*x)*Sin(b*x))/(a^2+b^2)}
	public void test00023() {
		check("Integrate(E^(a*x)*Sin(b*x), x)", "-((b*E^(a*x)*Cos(b*x))/(a^2+b^2))+(a*E^(a*x)*Sin(b*x))/(a^2+b^2)");

	}

	// {(a^2+x^2)^(-1), x, 1, ArcTan(x/a)/a}
	public void test00024() {
		check("Integrate((a^2+x^2)^(-1), x)", "ArcTan(x/a)/a");

	}

	// {(a+b*x^2)^(-1), x, 1, ArcTan((Sqrt(b)*x)/Sqrt(a))/(Sqrt(a)*Sqrt(b))}
	public void test00025() {
		check("Integrate((a+b*x^2)^(-1), x)", "ArcTan((Sqrt(b)*x)/Sqrt(a))/(Sqrt(a)*Sqrt(b))");

	}

	// {ArcTan(Sqrt(x))/(Sqrt(x)*(1+x)), x, 1, ArcTan(Sqrt(x))^2}
	public void test00026() {
		check("Integrate(ArcTan(Sqrt(x))/(Sqrt(x)*(1+x)), x)", "ArcTan(Sqrt(x))^2");

	}

	// {(E^ArcTan(x)*x)/(1+x^2)^(3/2), x, 1, -(E^ArcTan(x)*(1-x))/(2*Sqrt(1+x^2))}
	public void test00027() {
		check("Integrate((E^ArcTan(x)*x)/(1+x^2)^(3/2), x)", "-(E^ArcTan(x)*(1-x))/(2*Sqrt(1+x^2))");

	}

	// {E^ArcTan(x)/(1+x^2)^(3/2), x, 1, (E^ArcTan(x)*(1+x))/(2*Sqrt(1+x^2))}
	public void test00028() {
		check("Integrate(E^ArcTan(x)/(1+x^2)^(3/2), x)", "(E^ArcTan(x)*(1+x))/(2*Sqrt(1+x^2))");

	}

	// {(-1+4*x^5)/(1+x+x^5)^2, x, 1, -(x/(1+x+x^5))}
	public void test00029() {
		check("Integrate((-1+4*x^5)/(1+x+x^5)^2, x)", "-x/(1+x+x^5)");

	}

	// {(1+Cos(x)/2)^(-1), x, 1, (2*x)/Sqrt(3)-(4*ArcTan(Sin(x)/(2+Sqrt(3)+Cos(x))))/Sqrt(3)}
	public void test00030() {
		check("Integrate((1+Cos(x)/2)^(-1), x)", "(2*x)/Sqrt(3)-(4*ArcTan(Sin(x)/(2+Sqrt(3)+Cos(x))))/Sqrt(3)");

	}

	// {(b*Cos(x)+a*Sin(x))^(-2), x, 1, Sin(x)/(b*(b*Cos(x)+a*Sin(x)))}
	public void test00031() {
		check("Integrate((b*Cos(x)+a*Sin(x))^(-2), x)", "Sin(x)/(b*(b*Cos(x)+a*Sin(x)))");

	}

	// {x/Sqrt(3-x^2), x, 1, -Sqrt(3-x^2)}
	public void test00032() {
		check("Integrate(x/Sqrt(3-x^2), x)", "-Sqrt(3-x^2)");

	}

	// {E^t/t, t, 1, ExpIntegralEi(t)}
	public void test00033() {
		check("Integrate(E^t/t, t)", "ExpIntegralEi(t)");

	}

	// {E^(a*t)/t, t, 1, ExpIntegralEi(a*t)}
	public void test00034() {
		check("Integrate(E^(a*t)/t, t)", "ExpIntegralEi(a*t)");

	}

	// {1/(E^t*(-1-a+t)), t, 1, E^(-1-a)*ExpIntegralEi(1+a-t)}
	public void test00035() {
		check("Integrate(1/(E^t*(-1-a+t)), t)", "E^(-1-a)*ExpIntegralEi(1+a-t)");

	}

	// {(b1*Cos(x)+a1*Sin(x))/(b*Cos(x)+a*Sin(x)), x, 1, ((a*a1+b*b1)*x)/(a^2+b^2)-((a1*b-a*b1)*Log(b*Cos(x)
	// +a*Sin(x)))/(a^2+b^2)}
	public void test00036() {
		check("Integrate((b1*Cos(x)+a1*Sin(x))/(b*Cos(x)+a*Sin(x)), x)",
				"((a*a1+b*b1)*x)/(a^2+b^2)-((a1*b-a*b1)*Log(b*Cos(x)+a*Sin(x)))/(a^2+b^2)");

	}

	// {Log(t)^(-1), t, 1, LogIntegral(t)}
	public void test00037() {
		check("Integrate(Log(t)^(-1), t)", "LogIntegral(t)");

	}

	// {E^(2*t)/(-1+t), t, 1, E^2*ExpIntegralEi(-2*(1-t))}
	public void test00038() {
		check("Integrate(E^(2*t)/(-1+t), t)", "E^2*ExpIntegralEi(-2*(1-t))");

	}

	// {1/Sqrt(1+t^3), t, 1, (2*Sqrt(2+Sqrt(3))*(1+t)*Sqrt((1-t+t^2)/(1+Sqrt(3)+t)^2)*EllipticF(ArcSin((1
	// -Sqrt(3)+t)/(1+Sqrt(3)+t)), -7-4*Sqrt(3)))/(3^(1/4)*Sqrt((1+t)/(1+Sqrt(3)+t)^2)*Sqrt(1+t^3))}
	public void test00039() {
		check("Integrate(1/Sqrt(1+t^3), t)",
				"(2*Sqrt(2+Sqrt(3))*(1+t)*Sqrt((1-t+t^2)/(1+Sqrt(3)+t)^2)*EllipticF(ArcSin((1-Sqrt(3)+t)/(1+Sqrt(3)+t)), -7-4*Sqrt(3)))/(3^(1/4)*Sqrt((1+t)/(1+Sqrt(3)+t)^2)*Sqrt(1+t^3))");

	}

	// {(Sqrt(2)+Cos(z)+Sin(z))^(-1), z, 1, -((1-Sqrt(2)*Sin(z))/(Cos(z)-Sin(z)))}
	public void test00040() {
		check("Integrate((Sqrt(2)+Cos(z)+Sin(z))^(-1), z)", "-((1-Sqrt(2)*Sin(z))/(Cos(z)-Sin(z)))");

	}

	// {(1+x^2)^(-1), x, 1, ArcTan(x)}
	public void test00041() {
		check("Integrate((1+x^2)^(-1), x)", "ArcTan(x)");

	}

	// {Sin(x)/x, x, 1, SinIntegral(x)}
	public void test00042() {
		check("Integrate(Sin(x)/x, x)", "SinIntegral(x)");

	}

	// {(6-3*x^2+x^4)/(4+5*x^2-5*x^4+x^6), x, 1, -ArcTan(Sqrt(3)-2*x)+ArcTan(Sqrt(3)+2*x)+ArcTan((x*(1
	// -3*x^2+x^4))/2)}
	public void test00043() {
		check("Integrate((6-3*x^2+x^4)/(4+5*x^2-5*x^4+x^6), x)",
				"-ArcTan(Sqrt(3)-2*x)+ArcTan(Sqrt(3)+2*x)+ArcTan((x*(1-3*x^2+x^4))/2)");

	}

	// {1+x+x^2, x, 1, x+x^2/2+x^3/3}
	public void test00044() {
		check("Integrate(1+x+x^2, x)", "x+x^2/2+x^3/3");

	}

	// {x^(-1), x, 1, Log(x)}
	public void test00045() {
		check("Integrate(x^(-1), x)", "Log(x)");

	}

	// {(a+b*x)^p, x, 1, (a+b*x)^(1+p)/(b*(1+p))}
	public void test00046() {
		check("Integrate((a+b*x)^p, x)", "(a+b*x)^(1+p)/(b*(1+p))");

	}

	// {(a+b*x)^(-1), x, 1, Log(a+b*x)/b}
	public void test00047() {
		check("Integrate((a+b*x)^(-1), x)", "Log(a+b*x)/b");

	}

	// {(a+b*x)^(-2), x, 1, -(1/(b*(a+b*x)))}
	public void test00048() {
		check("Integrate((a+b*x)^(-2), x)", "-(1/(b*(a+b*x)))");

	}

	// {(c^2+x^2)^(-1), x, 1, ArcTan(x/c)/c}
	public void test00049() {
		check("Integrate((c^2+x^2)^(-1), x)", "ArcTan(x/c)/c");

	}

	// {(c^2-x^2)^(-1), x, 1, ArcTanh(x/c)/c}
	public void test00050() {
		check("Integrate((c^2-x^2)^(-1), x)", "ArcTanh(x/c)/c");

	}

	// {Log(x), x, 1, -x+x*Log(x)}
	public void test00051() {
		check("Integrate(Log(x), x)", "-x+x*Log(x)");

	}

	// {x*Log(x), x, 1, -x^2/4+(x^2*Log(x))/2}
	public void test00052() {
		check("Integrate(x*Log(x), x)", "-x^2/4+(x^2*Log(x))/2");

	}

	// {x^2*Log(x), x, 1, -x^3/9+(x^3*Log(x))/3}
	public void test00053() {
		check("Integrate(x^2*Log(x), x)", "-x^3/9+(x^3*Log(x))/3");

	}

	// {x^p*Log(x), x, 1, -(x^(1+p)/(1+p)^2)+(x^(1+p)*Log(x))/(1+p)}
	public void test00054() {
		check("Integrate(x^p*Log(x), x)", "-(x^(1+p)/(1+p)^2)+(x^(1+p)*Log(x))/(1+p)");

	}

	// {Log(x)^(-1), x, 1, LogIntegral(x)}
	public void test00055() {
		check("Integrate(Log(x)^(-1), x)", "LogIntegral(x)");

	}

	// {Sin(x), x, 1, -Cos(x)}
	public void test00056() {
		check("Integrate(Sin(x), x)", "-Cos(x)");

	}

	// {Cos(x), x, 1, Sin(x)}
	public void test00057() {
		check("Integrate(Cos(x), x)", "Sin(x)");

	}

	// {Tan(x), x, 1, -Log(Cos(x))}
	public void test00058() {
		check("Integrate(Tan(x), x)", "-Log(Cos(x))");

	}

	// {Cot(x), x, 1, Log(Sin(x))}
	public void test00059() {
		check("Integrate(Cot(x), x)", "Log(Sin(x))");

	}

	// {Sec(x), x, 1, ArcTanh(Sin(x))}
	public void test00060() {
		check("Integrate(Sec(x), x)", "ArcTanh(Sin(x))");

	}

	// {Csc(x), x, 1, -ArcTanh(Cos(x))}
	public void test00061() {
		check("Integrate(Csc(x), x)", "-ArcTanh(Cos(x))");

	}

	// {Sin(x)^p, x, 1, (Cos(x)*Hypergeometric2F1(1/2, (1+p)/2, (3+p)/2, Sin(x)^2)*Sin(x)^(1+p))/((1 +
	// p)*Sqrt(Cos(x)^2))}
	public void test00062() {
		check("Integrate(Sin(x)^p, x)",
				"(Cos(x)*Hypergeometric2F1(1/2, (1+p)/2, (3+p)/2, Sin(x)^2)*Sin(x)^(1+p))/((1+p)*Sqrt(Cos(x)^2))");

	}

	// {Sin(x)*Sin(2*x), x, 1, Sin(x)/2-Sin(3*x)/6}
	public void test00063() {
		check("Integrate(Sin(x)*Sin(2*x), x)", "Sin(x)/2-Sin(3*x)/6");

	}

	// {Sin(x)/x, x, 1, SinIntegral(x)}
	public void test00064() {
		check("Integrate(Sin(x)/x, x)", "SinIntegral(x)");

	}

	// {Cos(x)/x, x, 1, CosIntegral(x)}
	public void test00065() {
		check("Integrate(Cos(x)/x, x)", "CosIntegral(x)");

	}

	// {Sin(a+b*x), x, 1, -(Cos(a+b*x)/b)}
	public void test00066() {
		check("Integrate(Sin(a+b*x), x)", "-(Cos(a+b*x)/b)");

	}

	// {Cos(a+b*x), x, 1, Sin(a+b*x)/b}
	public void test00067() {
		check("Integrate(Cos(a+b*x), x)", "Sin(a+b*x)/b");

	}

	// {Tan(a+b*x), x, 1, -(Log(Cos(a+b*x))/b)}
	public void test00068() {
		check("Integrate(Tan(a+b*x), x)", "-(Log(Cos(a+b*x))/b)");

	}

	// {Cot(a+b*x), x, 1, Log(Sin(a+b*x))/b}
	public void test00069() {
		check("Integrate(Cot(a+b*x), x)", "Log(Sin(a+b*x))/b");

	}

	// {Csc(a+b*x), x, 1, -(ArcTanh(Cos(a+b*x))/b)}
	public void test00070() {
		check("Integrate(Csc(a+b*x), x)", "-(ArcTanh(Cos(a+b*x))/b)");

	}

	// {Sec(a+b*x), x, 1, ArcTanh(Sin(a+b*x))/b}
	public void test00071() {
		check("Integrate(Sec(a+b*x), x)", "ArcTanh(Sin(a+b*x))/b");

	}

	// {(1+Cos(x))^(-1), x, 1, Sin(x)/(1+Cos(x))}
	public void test00072() {
		check("Integrate((1+Cos(x))^(-1), x)", "Sin(x)/(1+Cos(x))");

	}

	// {(1-Cos(x))^(-1), x, 1, -(Sin(x)/(1-Cos(x)))}
	public void test00073() {
		check("Integrate((1-Cos(x))^(-1), x)", "-(Sin(x)/(1-Cos(x)))");

	}

	// {(1+Sin(x))^(-1), x, 1, -(Cos(x)/(1+Sin(x)))}
	public void test00074() {
		check("Integrate((1+Sin(x))^(-1), x)", "-(Cos(x)/(1+Sin(x)))");

	}

	// {(1-Sin(x))^(-1), x, 1, Cos(x)/(1-Sin(x))}
	public void test00075() {
		check("Integrate((1-Sin(x))^(-1), x)", "Cos(x)/(1-Sin(x))");

	}

	// {Cos(x)*Cos(2*x), x, 1, Sin(x)/2+Sin(3*x)/6}
	public void test00076() {
		check("Integrate(Cos(x)*Cos(2*x), x)", "Sin(x)/2+Sin(3*x)/6");

	}

	// {Cos(3*x)*Sin(2*x), x, 1, Cos(x)/2-Cos(5*x)/10}
	public void test00077() {
		check("Integrate(Cos(3*x)*Sin(2*x), x)", "Cos(x)/2-Cos(5*x)/10");

	}

	// {d^x*Sin(x), x, 1, -((d^x*Cos(x))/(1+Log(d)^2))+(d^x*Log(d)*Sin(x))/(1+Log(d)^2)}
	public void test00078() {
		check("Integrate(d^x*Sin(x), x)", "-((d^x*Cos(x))/(1+Log(d)^2))+(d^x*Log(d)*Sin(x))/(1+Log(d)^2)");

	}

	// {d^x*Cos(x), x, 1, (d^x*Cos(x)*Log(d))/(1+Log(d)^2)+(d^x*Sin(x))/(1+Log(d)^2)}
	public void test00079() {
		check("Integrate(d^x*Cos(x), x)", "(d^x*Cos(x)*Log(d))/(1+Log(d)^2)+(d^x*Sin(x))/(1+Log(d)^2)");

	}

	// {Sin(Log(x)), x, 1, -(x*Cos(Log(x)))/2+(x*Sin(Log(x)))/2}
	public void test00080() {
		check("Integrate(Sin(Log(x)), x)", "-(x*Cos(Log(x)))/2+(x*Sin(Log(x)))/2");

	}

	// {Cos(Log(x)), x, 1, (x*Cos(Log(x)))/2+(x*Sin(Log(x)))/2}
	public void test00081() {
		check("Integrate(Cos(Log(x)), x)", "(x*Cos(Log(x)))/2+(x*Sin(Log(x)))/2");

	}

	// {E^x, x, 1, E^x}
	public void test00082() {
		check("Integrate(E^x, x)", "E^x");

	}

	// {a^x, x, 1, a^x/Log(a)}
	public void test00083() {
		check("Integrate(a^x, x)", "a^x/Log(a)");

	}

	// {E^(a*x), x, 1, E^(a*x)/a}
	public void test00084() {
		check("Integrate(E^(a*x), x)", "E^(a*x)/a");

	}

	// {E^(a*x)/x, x, 1, ExpIntegralEi(a*x)}
	public void test00085() {
		check("Integrate(E^(a*x)/x, x)", "ExpIntegralEi(a*x)");

	}

	// {(E^(a*x)*x)/(1+a*x)^2, x, 1, E^(a*x)/(a^2*(1+a*x))}
	public void test00086() {
		check("Integrate((E^(a*x)*x)/(1+a*x)^2, x)", "E^(a*x)/(a^2*(1+a*x))");

	}

	// {k^x^2*x, x, 1, k^x^2/(2*Log(k))}
	public void test00087() {
		check("Integrate(k^x^2*x, x)", "k^x^2/(2*Log(k))");

	}

	// {E^x^2, x, 1, (Sqrt(Pi)*Erfi(x))/2}
	public void test00088() {
		check("Integrate(E^x^2, x)", "(Sqrt(Pi)*Erfi(x))/2");

	}

	// {E^x^2*x, x, 1, E^x^2/2}
	public void test00089() {
		check("Integrate(E^x^2*x, x)", "E^x^2/2");

	}

	// {E^E^E^E^x, x, 1, CannotIntegrate(E^E^E^E^x, x)}
	public void test00090() {
		check("Integrate(E^E^E^E^x, x)", "CannotIntegrate(E^E^E^E^x, x)");

	}

	// {2*x+Sqrt(2)*x^2, x, 1, x^2+(Sqrt(2)*x^3)/3}
	public void test00091() {
		check("Integrate(2*x+Sqrt(2)*x^2, x)", "x^2+(Sqrt(2)*x^3)/3");

	}

	// {Sqrt(a+b*x), x, 1, (2*(a+b*x)^(3/2))/(3*b)}
	public void test00092() {
		check("Integrate(Sqrt(a+b*x), x)", "(2*(a+b*x)^(3/2))/(3*b)");

	}

	// {1/Sqrt(a+b*x), x, 1, (2*Sqrt(a+b*x))/b}
	public void test00093() {
		check("Integrate(1/Sqrt(a+b*x), x)", "(2*Sqrt(a+b*x))/b");

	}

	// {(a+b*x)^(p/2), x, 1, (2*(a+b*x)^((2+p)/2))/(b*(2+p))}
	public void test00094() {
		check("Integrate((a+b*x)^(p/2), x)", "(2*(a+b*x)^((2+p)/2))/(b*(2+p))");

	}

	// {x/(1-x^2)^(9/8), x, 1, 4/(1-x^2)^(1/8)}
	public void test00095() {
		check("Integrate(x/(1-x^2)^(9/8), x)", "4/(1-x^2)^(1/8)");

	}

	// {(1+x)/((1-x)^2*Sqrt(1+x^2)), x, 1, Sqrt(1+x^2)/(1-x)}
	public void test00096() {
		check("Integrate((1+x)/((1-x)^2*Sqrt(1+x^2)), x)", "Sqrt(1+x^2)/(1-x)");

	}

	// {1/Sqrt(1+x^2), x, 1, ArcSinh(x)}
	public void test00097() {
		check("Integrate(1/Sqrt(1+x^2), x)", "ArcSinh(x)");

	}

	// {r/Sqrt(-alpha^2+2*e*r^2), r, 1, Sqrt(-alpha^2+2*e*r^2)/(2*e)}
	public void test00098() {
		check("Integrate(r/Sqrt(-alpha^2+2*e*r^2), r)", "Sqrt(-alpha^2+2*e*r^2)/(2*e)");

	}

	// {r/Sqrt(-alpha^2-epsilon^2+2*e*r^2), r, 1, Sqrt(-alpha^2-epsilon^2+2*e*r^2)/(2*e)}
	public void test00099() {
		check("Integrate(r/Sqrt(-alpha^2-epsilon^2+2*e*r^2), r)", "Sqrt(-alpha^2-epsilon^2+2*e*r^2)/(2*e)");

	}

	// {Log(x^2)/x^3, x, 1, -1/(2*x^2)-Log(x^2)/(2*x^2)}
	public void test00100() {
		check("Integrate(Log(x^2)/x^3, x)", "-1/(2*x^2)-Log(x^2)/(2*x^2)");

	}

	// {(-1+(1-x)*Log(x))/(E^x*Log(x)^2), x, 1, x/(E^x*Log(x))}
	public void test00101() {
		check("Integrate((-1+(1-x)*Log(x))/(E^x*Log(x)^2), x)", "x/(E^x*Log(x))");

	}

	// {Tanh(2*x), x, 1, Log(Cosh(2*x))/2}
	public void test00102() {
		check("Integrate(Tanh(2*x), x)", "Log(Cosh(2*x))/2");

	}

	// {(-1+I*eps*Sinh(x))/(I*a-x+I*eps*Cosh(x)), x, 1, Log(a+I*x+eps*Cosh(x))}
	public void test00103() {
		check("Integrate((-1+I*eps*Sinh(x))/(I*a-x+I*eps*Cosh(x)), x)", "Log(a+I*x+eps*Cosh(x))");

	}

	// {Sqrt(1+Sin(x)), x, 1, (-2*Cos(x))/Sqrt(1+Sin(x))}
	public void test00104() {
		check("Integrate(Sqrt(1+Sin(x)), x)", "(-2*Cos(x))/Sqrt(1+Sin(x))");

	}

	// {Sqrt(1-Sin(x)), x, 1, (2*Cos(x))/Sqrt(1-Sin(x))}
	public void test00105() {
		check("Integrate(Sqrt(1-Sin(x)), x)", "(2*Cos(x))/Sqrt(1-Sin(x))");

	}

	// {Sqrt(1+Cos(x)), x, 1, (2*Sin(x))/Sqrt(1+Cos(x))}
	public void test00106() {
		check("Integrate(Sqrt(1+Cos(x)), x)", "(2*Sin(x))/Sqrt(1+Cos(x))");

	}

	// {Sqrt(1-Cos(x)), x, 1, (-2*Sin(x))/Sqrt(1-Cos(x))}
	public void test00107() {
		check("Integrate(Sqrt(1-Cos(x)), x)", "(-2*Sin(x))/Sqrt(1-Cos(x))");

	}

	// {Cot(x), x, 1, Log(Sin(x))}
	public void test00108() {
		check("Integrate(Cot(x), x)", "Log(Sin(x))");

	}

	// {Tanh(x), x, 1, Log(Cosh(x))}
	public void test00109() {
		check("Integrate(Tanh(x), x)", "Log(Cosh(x))");

	}

	// {Coth(x), x, 1, Log(Sinh(x))}
	public void test00110() {
		check("Integrate(Coth(x), x)", "Log(Sinh(x))");

	}

	// {b^x, x, 1, b^x/Log(b)}
	public void test00111() {
		check("Integrate(b^x, x)", "b^x/Log(b)");

	}

	// {(-3+x)^(-4), x, 1, 1/(3*(3-x)^3)}
	public void test00112() {
		check("Integrate((-3+x)^(-4), x)", "1/(3*(3-x)^3)");

	}

	// {1/Sqrt(1+x^2), x, 1, ArcSinh(x)}
	public void test00113() {
		check("Integrate(1/Sqrt(1+x^2), x)", "ArcSinh(x)");

	}

	// {1/Sqrt(9+4*x^2), x, 1, ArcSinh((2*x)/3)/2}
	public void test00114() {
		check("Integrate(1/Sqrt(9+4*x^2), x)", "ArcSinh((2*x)/3)/2");

	}

	// {1/Sqrt(4+x^2), x, 1, ArcSinh(x/2)}
	public void test00115() {
		check("Integrate(1/Sqrt(4+x^2), x)", "ArcSinh(x/2)");

	}

	// {Erf(x), x, 1, 1/(E^x^2*Sqrt(Pi))+x*Erf(x)}
	public void test00116() {
		check("Integrate(Erf(x), x)", "1/(E^x^2*Sqrt(Pi))+x*Erf(x)");

	}

	// {Erf(a+x), x, 1, 1/(E^(a+x)^2*Sqrt(Pi))+(a+x)*Erf(a+x)}
	public void test00117() {
		check("Integrate(Erf(a+x), x)", "1/(E^(a+x)^2*Sqrt(Pi))+(a+x)*Erf(a+x)");

	}

	// {(x*(-Sqrt(-4+x^2)+x^2*Sqrt(-4+x^2)-4*Sqrt(-1+x^2)+x^2*Sqrt(-1+x^2)))/((4-5*x^2+x^4)*(1 +
	// Sqrt(-4+x^2)+Sqrt(-1+x^2))), x, 1, Log(1+Sqrt(-4+x^2)+Sqrt(-1+x^2))}
	public void test00118() {
		check("Integrate((x*(-Sqrt(-4+x^2)+x^2*Sqrt(-4+x^2)-4*Sqrt(-1+x^2)+x^2*Sqrt(-1+x^2)))/((4-5*x^2+x^4)*(1+Sqrt(-4+x^2)+Sqrt(-1+x^2))), x)",
				"Log(1+Sqrt(-4+x^2)+Sqrt(-1+x^2))");

	}

	// {Sqrt(9-4*Sqrt(2))*x-Sqrt(2)*Sqrt(1+4*x+2*x^2+x^4), x, 1, (Sqrt(9-4*Sqrt(2))*x^2)/2 -
	// Sqrt(2)*CannotIntegrate(Sqrt(1+4*x+2*x^2+x^4), x)}
	public void test00119() {
		check("Integrate(Sqrt(9-4*Sqrt(2))*x-Sqrt(2)*Sqrt(1+4*x+2*x^2+x^4), x)",
				"(Sqrt(9-4*Sqrt(2))*x^2)/2-Sqrt(2)*CannotIntegrate(Sqrt(1+4*x+2*x^2+x^4), x)");

	}

	// {(E^(1+Log(x)^(-1))*(-1+Log(x)^2))/Log(x)^2, x, 1, E^(1+Log(x)^(-1))*x}
	public void test00120() {
		check("Integrate((E^(1+Log(x)^(-1))*(-1+Log(x)^2))/Log(x)^2, x)", "E^(1+Log(x)^(-1))*x");

	}

	// {Cos(x), x, 1, Sin(x)}
	public void test00121() {
		check("Integrate(Cos(x), x)", "Sin(x)");

	}

	// {E^x^2*x, x, 1, E^x^2/2}
	public void test00122() {
		check("Integrate(E^x^2*x, x)", "E^x^2/2");

	}

	// {x*Sqrt(1+x^2), x, 1, (1+x^2)^(3/2)/3}
	public void test00123() {
		check("Integrate(x*Sqrt(1+x^2), x)", "(1+x^2)^(3/2)/3");

	}

	// {E^x*Sin(x), x, 1, -(E^x*Cos(x))/2+(E^x*Sin(x))/2}
	public void test00124() {
		check("Integrate(E^x*Sin(x), x)", "-(E^x*Cos(x))/2+(E^x*Sin(x))/2");

	}

	// {Sin(y)/y, y, 1, SinIntegral(y)}
	public void test00125() {
		check("Integrate(Sin(y)/y, y)", "SinIntegral(y)");

	}

	// {E^x^2*x, x, 1, E^x^2/2}
	public void test00126() {
		check("Integrate(E^x^2*x, x)", "E^x^2/2");

	}

	// {x*Sqrt(1+x^2), x, 1, (1+x^2)^(3/2)/3}
	public void test00127() {
		check("Integrate(x*Sqrt(1+x^2), x)", "(1+x^2)^(3/2)/3");

	}

	// {x^(3/2), x, 1, (2*x^(5/2))/5}
	public void test00128() {
		check("Integrate(x^(3/2), x)", "(2*x^(5/2))/5");

	}

	// {Cos(3+2*x), x, 1, Sin(3+2*x)/2}
	public void test00129() {
		check("Integrate(Cos(3+2*x), x)", "Sin(3+2*x)/2");

	}

	// {(10*E)^x, x, 1, (10*E)^x/(1+Log(10))}
	public void test00130() {
		check("Integrate((10*E)^x, x)", "(10*E)^x/(1+Log(10))");

	}

	// {(1+Cos(x))^(-1), x, 1, Sin(x)/(1+Cos(x))}
	public void test00131() {
		check("Integrate((1+Cos(x))^(-1), x)", "Sin(x)/(1+Cos(x))");

	}

	// {(E^x*x)/(1+x)^2, x, 1, E^x/(1+x)}
	public void test00132() {
		check("Integrate((E^x*x)/(1+x)^2, x)", "E^x/(1+x)");

	}

	// {E^x^2, x, 1, (Sqrt(Pi)*Erfi(x))/2}
	public void test00133() {
		check("Integrate(E^x^2, x)", "(Sqrt(Pi)*Erfi(x))/2");

	}

	// {E^x/x, x, 1, ExpIntegralEi(x)}
	public void test00134() {
		check("Integrate(E^x/x, x)", "ExpIntegralEi(x)");

	}

	// {(A^4-A^2*B^2+(-A^2+B^2)*x^2)^(-1), x, 1, ArcTanh(x/A)/(A*(A^2-B^2))}
	public void test00135() {
		check("Integrate((A^4-A^2*B^2+(-A^2+B^2)*x^2)^(-1), x)", "ArcTanh(x/A)/(A*(A^2-B^2))");

	}

	// {x*Log(x), x, 1, -x^2/4+(x^2*Log(x))/2}
	public void test00136() {
		check("Integrate(x*Log(x), x)", "-x^2/4+(x^2*Log(x))/2");

	}

	// {Log(x)^(-1), x, 1, LogIntegral(x)}
	public void test00137() {
		check("Integrate(Log(x)^(-1), x)", "LogIntegral(x)");

	}

	// {E^(-1-x), x, 1, -E^(-1-x)}
	public void test00138() {
		check("Integrate(E^(-1-x), x)", "-E^(-1-x)");

	}

	// {E^x*Sin(x), x, 1, -(E^x*Cos(x))/2+(E^x*Sin(x))/2}
	public void test00139() {
		check("Integrate(E^x*Sin(x), x)", "-(E^x*Cos(x))/2+(E^x*Sin(x))/2");

	}

	// {x^(-1), x, 1, Log(x)}
	public void test00140() {
		check("Integrate(x^(-1), x)", "Log(x)");

	}

	// {(1-Cos(x))^(-1), x, 1, -(Sin(x)/(1-Cos(x)))}
	public void test00141() {
		check("Integrate((1-Cos(x))^(-1), x)", "-(Sin(x)/(1-Cos(x)))");

	}

	// {x*Log(x), x, 1, -x^2/4+(x^2*Log(x))/2}
	public void test00142() {
		check("Integrate(x*Log(x), x)", "-x^2/4+(x^2*Log(x))/2");

	}

	// {1/(r*Sqrt(-a^2+2*H*r^2)), x, 1, x/(r*Sqrt(-a^2+2*H*r^2))}
	public void test00143() {
		check("Integrate(1/(r*Sqrt(-a^2+2*H*r^2)), x)", "x/(r*Sqrt(-a^2+2*H*r^2))");

	}

	// {1/(r*Sqrt(-a^2-e^2+2*H*r^2)), x, 1, x/(r*Sqrt(-a^2-e^2+2*H*r^2))}
	public void test00144() {
		check("Integrate(1/(r*Sqrt(-a^2-e^2+2*H*r^2)), x)", "x/(r*Sqrt(-a^2-e^2+2*H*r^2))");

	}

	// {1/(r*Sqrt(-a^2+2*H*r^2-2*K*r^4)), x, 1, x/(r*Sqrt(-a^2+2*H*r^2-2*K*r^4))}
	public void test00145() {
		check("Integrate(1/(r*Sqrt(-a^2+2*H*r^2-2*K*r^4)), x)", "x/(r*Sqrt(-a^2+2*H*r^2-2*K*r^4))");

	}

	// {1/(r*Sqrt(-a^2-e^2+2*H*r^2-2*K*r^4)), x, 1, x/(r*Sqrt(-a^2-e^2+2*H*r^2-2*K*r^4))}
	public void test00146() {
		check("Integrate(1/(r*Sqrt(-a^2-e^2+2*H*r^2-2*K*r^4)), x)", "x/(r*Sqrt(-a^2-e^2+2*H*r^2-2*K*r^4))");

	}

	// {1/(r*Sqrt(-a^2-2*K*r+2*H*r^2)), x, 1, x/(r*Sqrt(-a^2-2*r*(K-H*r)))}
	public void test00147() {
		check("Integrate(1/(r*Sqrt(-a^2-2*K*r+2*H*r^2)), x)", "x/(r*Sqrt(-a^2-2*r*(K-H*r)))");

	}

	// {1/(r*Sqrt(-a^2-e^2-2*K*r+2*H*r^2)), x, 1, x/(r*Sqrt(-a^2-e^2-2*r*(K-H*r)))}
	public void test00148() {
		check("Integrate(1/(r*Sqrt(-a^2-e^2-2*K*r+2*H*r^2)), x)", "x/(r*Sqrt(-a^2-e^2-2*r*(K-H*r)))");

	}

	// {r/Sqrt(-a^2+2*E*r^2), x, 1, (r*x)/Sqrt(-a^2+2*E*r^2)}
	public void test00149() {
		check("Integrate(r/Sqrt(-a^2+2*E*r^2), x)", "(r*x)/Sqrt(-a^2+2*E*r^2)");

	}

	// {r/Sqrt(-a^2-e^2+2*E*r^2), x, 1, (r*x)/Sqrt(-a^2-e^2+2*E*r^2)}
	public void test00150() {
		check("Integrate(r/Sqrt(-a^2-e^2+2*E*r^2), x)", "(r*x)/Sqrt(-a^2-e^2+2*E*r^2)");

	}

	// {r/Sqrt(-a^2+2*E*r^2-2*K*r^4), x, 1, (r*x)/Sqrt(-a^2+2*E*r^2-2*K*r^4)}
	public void test00151() {
		check("Integrate(r/Sqrt(-a^2+2*E*r^2-2*K*r^4), x)", "(r*x)/Sqrt(-a^2+2*E*r^2-2*K*r^4)");

	}

	// {r/Sqrt(-a^2-e^2+2*E*r^2-2*K*r^4), x, 1, (r*x)/Sqrt(-a^2-e^2+2*E*r^2-2*K*r^4)}
	public void test00152() {
		check("Integrate(r/Sqrt(-a^2-e^2+2*E*r^2-2*K*r^4), x)", "(r*x)/Sqrt(-a^2-e^2+2*E*r^2-2*K*r^4)");

	}

	// {r/Sqrt(-a^2-e^2-2*K*r+2*H*r^2), x, 1, (r*x)/Sqrt(-a^2-e^2-2*r*(K-H*r))}
	public void test00153() {
		check("Integrate(r/Sqrt(-a^2-e^2-2*K*r+2*H*r^2), x)", "(r*x)/Sqrt(-a^2-e^2-2*r*(K-H*r))");

	}

	// {x^n, x, 1, x^(1+n)/(1+n)}
	public void test00154() {
		check("Integrate(x^n, x)", "x^(1+n)/(1+n)");

	}

	// {E^x, x, 1, E^x}
	public void test00155() {
		check("Integrate(E^x, x)", "E^x");

	}

	// {x^(-1), x, 1, Log(x)}
	public void test00156() {
		check("Integrate(x^(-1), x)", "Log(x)");

	}

	// {a^x, x, 1, a^x/Log(a)}
	public void test00157() {
		check("Integrate(a^x, x)", "a^x/Log(a)");

	}

	// {Sin(x), x, 1, -Cos(x)}
	public void test00158() {
		check("Integrate(Sin(x), x)", "-Cos(x)");

	}

	// {Cos(x), x, 1, Sin(x)}
	public void test00159() {
		check("Integrate(Cos(x), x)", "Sin(x)");

	}

	// {Sinh(x), x, 1, Cosh(x)}
	public void test00160() {
		check("Integrate(Sinh(x), x)", "Cosh(x)");

	}

	// {Cosh(x), x, 1, Sinh(x)}
	public void test00161() {
		check("Integrate(Cosh(x), x)", "Sinh(x)");

	}

	// {Tan(x), x, 1, -Log(Cos(x))}
	public void test00162() {
		check("Integrate(Tan(x), x)", "-Log(Cos(x))");

	}

	// {Cot(x), x, 1, Log(Sin(x))}
	public void test00163() {
		check("Integrate(Cot(x), x)", "Log(Sin(x))");

	}

	// {Log(x), x, 1, -x+x*Log(x)}
	public void test00164() {
		check("Integrate(Log(x), x)", "-x+x*Log(x)");

	}

	// {E^x*Sin(x), x, 1, -(E^x*Cos(x))/2+(E^x*Sin(x))/2}
	public void test00165() {
		check("Integrate(E^x*Sin(x), x)", "-(E^x*Cos(x))/2+(E^x*Sin(x))/2");

	}

	// {x*Log(x), x, 1, -x^2/4+(x^2*Log(x))/2}
	public void test00166() {
		check("Integrate(x*Log(x), x)", "-x^2/4+(x^2*Log(x))/2");

	}

	// {t^2*Log(t), t, 1, -t^3/9+(t^3*Log(t))/3}
	public void test00167() {
		check("Integrate(t^2*Log(t), t)", "-t^3/9+(t^3*Log(t))/3");

	}

	// {E^(2*t)*Sin(3*t), t, 1, (-3*E^(2*t)*Cos(3*t))/13+(2*E^(2*t)*Sin(3*t))/13}
	public void test00168() {
		check("Integrate(E^(2*t)*Sin(3*t), t)", "(-3*E^(2*t)*Cos(3*t))/13+(2*E^(2*t)*Sin(3*t))/13");

	}

	// {Cos(3*t)/E^t, t, 1, -Cos(3*t)/(10*E^t)+(3*Sin(3*t))/(10*E^t)}
	public void test00169() {
		check("Integrate(Cos(3*t)/E^t, t)", "-Cos(3*t)/(10*E^t)+(3*Sin(3*t))/(10*E^t)");

	}

	// {Sqrt(t)*Log(t), t, 1, (-4*t^(3/2))/9+(2*t^(3/2)*Log(t))/3}
	public void test00170() {
		check("Integrate(Sqrt(t)*Log(t), t)", "(-4*t^(3/2))/9+(2*t^(3/2)*Log(t))/3");

	}

	// {Cos(5*x)*Sin(3*x), x, 1, Cos(2*x)/4-Cos(8*x)/16}
	public void test00171() {
		check("Integrate(Cos(5*x)*Sin(3*x), x)", "Cos(2*x)/4-Cos(8*x)/16");

	}

	// {Sin(2*x)*Sin(4*x), x, 1, Sin(2*x)/4-Sin(6*x)/12}
	public void test00172() {
		check("Integrate(Sin(2*x)*Sin(4*x), x)", "Sin(2*x)/4-Sin(6*x)/12");

	}

	// {Cos(Log(x)), x, 1, (x*Cos(Log(x)))/2+(x*Sin(Log(x)))/2}
	public void test00173() {
		check("Integrate(Cos(Log(x)), x)", "(x*Cos(Log(x)))/2+(x*Sin(Log(x)))/2");

	}

	// {Log(Sqrt(x)), x, 1, -x/2+x*Log(Sqrt(x))}
	public void test00174() {
		check("Integrate(Log(Sqrt(x)), x)", "-x/2+x*Log(Sqrt(x))");

	}

	// {Sin(Log(x)), x, 1, -(x*Cos(Log(x)))/2+(x*Sin(Log(x)))/2}
	public void test00175() {
		check("Integrate(Sin(Log(x)), x)", "-(x*Cos(Log(x)))/2+(x*Sin(Log(x)))/2");

	}

	// {Sqrt(x)*Log(x), x, 1, (-4*x^(3/2))/9+(2*x^(3/2)*Log(x))/3}
	public void test00176() {
		check("Integrate(Sqrt(x)*Log(x), x)", "(-4*x^(3/2))/9+(2*x^(3/2)*Log(x))/3");

	}

	// {(1-Sin(2*x))^2, x, 1, (3*x)/2+Cos(2*x)-(Cos(2*x)*Sin(2*x))/4}
	public void test00177() {
		check("Integrate((1-Sin(2*x))^2, x)", "(3*x)/2+Cos(2*x)-(Cos(2*x)*Sin(2*x))/4");

	}

	// {(1-Sin(x))^(-1), x, 1, Cos(x)/(1-Sin(x))}
	public void test00178() {
		check("Integrate((1-Sin(x))^(-1), x)", "Cos(x)/(1-Sin(x))");

	}

	// {Csc(x), x, 1, -ArcTanh(Cos(x))}
	public void test00179() {
		check("Integrate(Csc(x), x)", "-ArcTanh(Cos(x))");

	}

	// {Sin(2*x)*Sin(5*x), x, 1, Sin(3*x)/6-Sin(7*x)/14}
	public void test00180() {
		check("Integrate(Sin(2*x)*Sin(5*x), x)", "Sin(3*x)/6-Sin(7*x)/14");

	}

	// {Cos(x)*Sin(3*x), x, 1, -Cos(2*x)/4-Cos(4*x)/8}
	public void test00181() {
		check("Integrate(Cos(x)*Sin(3*x), x)", "-Cos(2*x)/4-Cos(4*x)/8");

	}

	// {Cos(3*x)*Cos(4*x), x, 1, Sin(x)/2+Sin(7*x)/14}
	public void test00182() {
		check("Integrate(Cos(3*x)*Cos(4*x), x)", "Sin(x)/2+Sin(7*x)/14");

	}

	// {Sin(3*x)*Sin(6*x), x, 1, Sin(3*x)/6-Sin(9*x)/18}
	public void test00183() {
		check("Integrate(Sin(3*x)*Sin(6*x), x)", "Sin(3*x)/6-Sin(9*x)/18");

	}

	// {1/(x^2*Sqrt(4+x^2)), x, 1, -Sqrt(4+x^2)/(4*x)}
	public void test00184() {
		check("Integrate(1/(x^2*Sqrt(4+x^2)), x)", "-Sqrt(4+x^2)/(4*x)");

	}

	// {x/Sqrt(4+x^2), x, 1, Sqrt(4+x^2)}
	public void test00185() {
		check("Integrate(x/Sqrt(4+x^2), x)", "Sqrt(4+x^2)");

	}

	// {1/(x^2*Sqrt(1-x^2)), x, 1, -(Sqrt(1-x^2)/x)}
	public void test00186() {
		check("Integrate(1/(x^2*Sqrt(1-x^2)), x)", "-(Sqrt(1-x^2)/x)");

	}

	// {x/Sqrt(1-x^2), x, 1, -Sqrt(1-x^2)}
	public void test00187() {
		check("Integrate(x/Sqrt(1-x^2), x)", "-Sqrt(1-x^2)");

	}

	// {x*Sqrt(4-x^2), x, 1, -(4-x^2)^(3/2)/3}
	public void test00188() {
		check("Integrate(x*Sqrt(4-x^2), x)", "-(4-x^2)^(3/2)/3");

	}

	// {1/Sqrt(9+x^2), x, 1, ArcSinh(x/3)}
	public void test00189() {
		check("Integrate(1/Sqrt(9+x^2), x)", "ArcSinh(x/3)");

	}

	// {Sqrt(-a^2+x^2)/x^4, x, 1, (-a^2+x^2)^(3/2)/(3*a^2*x^3)}
	public void test00190() {
		check("Integrate(Sqrt(-a^2+x^2)/x^4, x)", "(-a^2+x^2)^(3/2)/(3*a^2*x^3)");

	}

	// {1/(x^2*Sqrt(-9+16*x^2)), x, 1, Sqrt(-9+16*x^2)/(9*x)}
	public void test00191() {
		check("Integrate(1/(x^2*Sqrt(-9+16*x^2)), x)", "Sqrt(-9+16*x^2)/(9*x)");

	}

	// {x/(4+x^2)^(5/2), x, 1, -1/(3*(4+x^2)^(3/2))}
	public void test00192() {
		check("Integrate(x/(4+x^2)^(5/2), x)", "-1/(3*(4+x^2)^(3/2))");

	}

	// {(-25+4*x^2)^(-3/2), x, 1, -x/(25*Sqrt(-25+4*x^2))}
	public void test00193() {
		check("Integrate((-25+4*x^2)^(-3/2), x)", "-x/(25*Sqrt(-25+4*x^2))");

	}

	// {(2*x+x^2)/(4+3*x^2+x^3), x, 1, Log(4+3*x^2+x^3)/3}
	public void test00194() {
		check("Integrate((2*x+x^2)/(4+3*x^2+x^3), x)", "Log(4+3*x^2+x^3)/3");

	}

	// {(-x+2*x^3)/(1-x^2+x^4), x, 1, Log(1-x^2+x^4)/2}
	public void test00195() {
		check("Integrate((-x+2*x^3)/(1-x^2+x^4), x)", "Log(1-x^2+x^4)/2");

	}

	// {x/(-1+x^2), x, 1, Log(1-x^2)/2}
	public void test00196() {
		check("Integrate(x/(-1+x^2), x)", "Log(1-x^2)/2");
	}

	// {(1-Cos(x))^(-1), x, 1, -(Sin(x)/(1-Cos(x)))}
	public void test00197() {
		check("Integrate((1-Cos(x))^(-1), x)", "-(Sin(x)/(1-Cos(x)))");

	}

	// {(-Cos(x)+Sin(x))/(Cos(x)+Sin(x)), x, 1, -Log(Cos(x)+Sin(x))}
	public void test00198() {
		check("Integrate((-Cos(x)+Sin(x))/(Cos(x)+Sin(x)), x)", "-Log(Cos(x)+Sin(x))");

	}

	// {x/Sqrt(1-x^2), x, 1, -Sqrt(1-x^2)}
	public void test00199() {
		check("Integrate(x/Sqrt(1-x^2), x)", "-Sqrt(1-x^2)");

	}

	// {x^3*Log(x), x, 1, -x^4/16+(x^4*Log(x))/4}
	public void test00200() {
		check("Integrate(x^3*Log(x), x)", "-x^4/16+(x^4*Log(x))/4");

	}

	// {(1+x+x^3)/(4*x+2*x^2+x^4), x, 1, Log(4*x+2*x^2+x^4)/4}
	public void test00201() {
		check("Integrate((1+x+x^3)/(4*x+2*x^2+x^4), x)", "Log(4*x+2*x^2+x^4)/4");

	}

	// {Sin(Pi*x), x, 1, -(Cos(Pi*x)/Pi)}
	public void test00202() {
		check("Integrate(Sin(Pi*x), x)", "-(Cos(Pi*x)/Pi)");

	}

	// {E^(3*x)*Cos(5*x), x, 1, (3*E^(3*x)*Cos(5*x))/34+(5*E^(3*x)*Sin(5*x))/34}
	public void test00203() {
		check("Integrate(E^(3*x)*Cos(5*x), x)", "(3*E^(3*x)*Cos(5*x))/34+(5*E^(3*x)*Sin(5*x))/34");

	}

	// {Cos(3*x)*Cos(5*x), x, 1, Sin(2*x)/4+Sin(8*x)/16}
	public void test00204() {
		check("Integrate(Cos(3*x)*Cos(5*x), x)", "Sin(2*x)/4+Sin(8*x)/16");

	}

	// {Csc(x)*Log(Tan(x))*Sec(x), x, 1, Log(Tan(x))^2/2}
	public void test00205() {
		check("Integrate(Csc(x)*Log(Tan(x))*Sec(x), x)", "Log(Tan(x))^2/2");

	}

	// {-2*x+x^2+x^3, x, 1, -x^2+x^3/3+x^4/4}
	public void test00206() {
		check("Integrate(-2*x+x^2+x^3, x)", "-x^2+x^3/3+x^4/4");

	}

	// {1/Sqrt(16-x^2), x, 1, ArcSin(x/4)}
	public void test00207() {
		check("Integrate(1/Sqrt(16-x^2), x)", "ArcSin(x/4)");

	}

	// {E^ArcTan(x)/(1+x^2), x, 1, E^ArcTan(x)}
	public void test00208() {
		check("Integrate(E^ArcTan(x)/(1+x^2), x)", "E^ArcTan(x)");

	}

	// {Log(x/2), x, 1, -x+x*Log(x/2)}
	public void test00209() {
		check("Integrate(Log(x/2), x)", "-x+x*Log(x/2)");

	}

	// {x*(5+x^2)^8, x, 1, (5+x^2)^9/18}
	public void test00210() {
		check("Integrate(x*(5+x^2)^8, x)", "(5+x^2)^9/18");

	}

	// {Cos(4*x)/E^(3*x), x, 1, (-3*Cos(4*x))/(25*E^(3*x))+(4*Sin(4*x))/(25*E^(3*x))}
	public void test00211() {
		check("Integrate(Cos(4*x)/E^(3*x), x)", "(-3*Cos(4*x))/(25*E^(3*x))+(4*Sin(4*x))/(25*E^(3*x))");

	}

	// {E^x*Cos(4+3*x), x, 1, (E^x*Cos(4+3*x))/10+(3*E^x*Sin(4+3*x))/10}
	public void test00212() {
		check("Integrate(E^x*Cos(4+3*x), x)", "(E^x*Cos(4+3*x))/10+(3*E^x*Sin(4+3*x))/10");

	}

	// {x^2*(1+x^3)^4, x, 1, (1+x^3)^5/15}
	public void test00213() {
		check("Integrate(x^2*(1+x^3)^4, x)", "(1+x^3)^5/15");

	}

	// {(a^2-b^2*x^2)^(-1), x, 1, ArcTanh((b*x)/a)/(a*b)}
	public void test00214() {
		check("Integrate((a^2-b^2*x^2)^(-1), x)", "ArcTanh((b*x)/a)/(a*b)");

	}

	// {(a^2+b^2*x^2)^(-1), x, 1, ArcTan((b*x)/a)/(a*b)}
	public void test00215() {
		check("Integrate((a^2+b^2*x^2)^(-1), x)", "ArcTan((b*x)/a)/(a*b)");

	}

	// {Sec(2*a*x), x, 1, ArcTanh(Sin(2*a*x))/(2*a)}
	public void test00216() {
		check("Integrate(Sec(2*a*x), x)", "ArcTanh(Sin(2*a*x))/(2*a)");

	}

	// {-Sec(Pi/4+2*x), x, 1, -ArcTanh(Sin(Pi/4+2*x))/2}
	public void test00217() {
		check("Integrate(-Sec(Pi/4+2*x), x)", "-ArcTanh(Sin(Pi/4+2*x))/2");

	}

	// {(1+Cos(x))^(-1), x, 1, Sin(x)/(1+Cos(x))}
	public void test00218() {
		check("Integrate((1+Cos(x))^(-1), x)", "Sin(x)/(1+Cos(x))");

	}

	// {(1-Cos(x))^(-1), x, 1, -(Sin(x)/(1-Cos(x)))}
	public void test00219() {
		check("Integrate((1-Cos(x))^(-1), x)", "-(Sin(x)/(1-Cos(x)))");

	}

	// {Sin(x/4)*Sin(x), x, 1, (2*Sin((3*x)/4))/3-(2*Sin((5*x)/4))/5}
	public void test00220() {
		check("Integrate(Sin(x/4)*Sin(x), x)", "(2*Sin((3*x)/4))/3-(2*Sin((5*x)/4))/5");

	}

	// {Cos(3*x)*Cos(4*x), x, 1, Sin(x)/2+Sin(7*x)/14}
	public void test00221() {
		check("Integrate(Cos(3*x)*Cos(4*x), x)", "Sin(x)/2+Sin(7*x)/14");

	}

	// {ArcTan(x)^n/(1+x^2), x, 1, ArcTan(x)^(1+n)/(1+n)}
	public void test00222() {
		check("Integrate(ArcTan(x)^n/(1+x^2), x)", "ArcTan(x)^(1+n)/(1+n)");

	}

	// {1/(Sqrt(1-x^2)*ArcCos(x)^3), x, 1, 1/(2*ArcCos(x)^2)}
	public void test00223() {
		check("Integrate(1/(Sqrt(1-x^2)*ArcCos(x)^3), x)", "1/(2*ArcCos(x)^2)");

	}

	// {Log(x)/x^5, x, 1, -1/(16*x^4)-Log(x)/(4*x^4)}
	public void test00224() {
		check("Integrate(Log(x)/x^5, x)", "-1/(16*x^4)-Log(x)/(4*x^4)");

	}

	// {Sin(x)/E^x, x, 1, -Cos(x)/(2*E^x)-Sin(x)/(2*E^x)}
	public void test00225() {
		check("Integrate(Sin(x)/E^x, x)", "-Cos(x)/(2*E^x)-Sin(x)/(2*E^x)");

	}

	// {E^(2*x)*Sin(3*x), x, 1, (-3*E^(2*x)*Cos(3*x))/13+(2*E^(2*x)*Sin(3*x))/13}
	public void test00226() {
		check("Integrate(E^(2*x)*Sin(3*x), x)", "(-3*E^(2*x)*Cos(3*x))/13+(2*E^(2*x)*Sin(3*x))/13");

	}

	// {a^x*Cos(x), x, 1, (a^x*Cos(x)*Log(a))/(1+Log(a)^2)+(a^x*Sin(x))/(1+Log(a)^2)}
	public void test00227() {
		check("Integrate(a^x*Cos(x), x)", "(a^x*Cos(x)*Log(a))/(1+Log(a)^2)+(a^x*Sin(x))/(1+Log(a)^2)");

	}

	// {Cos(Log(x)), x, 1, (x*Cos(Log(x)))/2+(x*Sin(Log(x)))/2}
	public void test00228() {
		check("Integrate(Cos(Log(x)), x)", "(x*Cos(Log(x)))/2+(x*Sin(Log(x)))/2");

	}

	// {x^(-1+k)*(a+b*x^k)^n, x, 1, (a+b*x^k)^(1+n)/(b*k*(1+n))}
	public void test00229() {
		check("Integrate(x^(-1+k)*(a+b*x^k)^n, x)", "(a+b*x^k)^(1+n)/(b*k*(1+n))");

	}

	// {x^2/(a^3+x^3), x, 1, Log(a^3+x^3)/3}
	public void test00230() {
		check("Integrate(x^2/(a^3+x^3), x)", "Log(a^3+x^3)/3");

	}

	// {1/(x^m*(a^3+x^3)), x, 1, (x^(1-m)*Hypergeometric2F1(1, (1-m)/3, (4-m)/3, -(x^3/a^3)))/(a^3*(1-m))}
	public void test00231() {
		check("Integrate(1/(x^m*(a^3+x^3)), x)",
				"(x^(1-m)*Hypergeometric2F1(1, (1-m)/3, (4-m)/3, -(x^3/a^3)))/(a^3*(1-m))");

	}

	// {1/(x^m*(a^4-x^4)), x, 1, (x^(1-m)*Hypergeometric2F1(1, (1-m)/4, (5-m)/4, x^4/a^4))/(a^4*(1-m))}
	public void test00232() {
		check("Integrate(1/(x^m*(a^4-x^4)), x)",
				"(x^(1-m)*Hypergeometric2F1(1, (1-m)/4, (5-m)/4, x^4/a^4))/(a^4*(1-m))");

	}

	// {x^4/(a^5+x^5), x, 1, Log(a^5+x^5)/5}
	public void test00233() {
		check("Integrate(x^4/(a^5+x^5), x)", "Log(a^5+x^5)/5");

	}

	// {1/(x^m*(a^5+x^5)), x, 1, (x^(1-m)*Hypergeometric2F1(1, (1-m)/5, (6-m)/5, -(x^5/a^5)))/(a^5*(1-m))}
	public void test00234() {
		check("Integrate(1/(x^m*(a^5+x^5)), x)",
				"(x^(1-m)*Hypergeometric2F1(1, (1-m)/5, (6-m)/5, -(x^5/a^5)))/(a^5*(1-m))");

	}

	// {x^3/(a^4+x^4)^3, x, 1, -1/(8*(a^4+x^4)^2)}
	public void test00235() {
		check("Integrate(x^3/(a^4+x^4)^3, x)", "-1/(8*(a^4+x^4)^2)");

	}

	// {(1+x+x^2)^(-3/2), x, 1, (2*(1+2*x))/(3*Sqrt(1+x+x^2))}
	public void test00236() {
		check("Integrate((1+x+x^2)^(-3/2), x)", "(2*(1+2*x))/(3*Sqrt(1+x+x^2))");

	}

	// {x/(1+x+x^2)^(3/2), x, 1, (-2*(2+x))/(3*Sqrt(1+x+x^2))}
	public void test00237() {
		check("Integrate(x/(1+x+x^2)^(3/2), x)", "(-2*(2+x))/(3*Sqrt(1+x+x^2))");

	}

	// {1/((1-3/x)^(4/3)*x^2), x, 1, -(1-3/x)^(-1/3)}
	public void test00238() {
		check("Integrate(1/((1-3/x)^(4/3)*x^2), x)", "-(1-3/x)^(-1/3)");

	}

	// {x^6*(1+x^7)^(1/3), x, 1, (3*(1+x^7)^(4/3))/28}
	public void test00239() {
		check("Integrate(x^6*(1+x^7)^(1/3), x)", "(3*(1+x^7)^(4/3))/28");

	}

	// {x^6/(1+x^7)^(5/3), x, 1, -3/(14*(1+x^7)^(2/3))}
	public void test00240() {
		check("Integrate(x^6/(1+x^7)^(5/3), x)", "-3/(14*(1+x^7)^(2/3))");

	}

	// {(-3*x+2*x^3)*(-3*x^2+x^4)^(3/5), x, 1, (5*(-3*x^2+x^4)^(8/5))/16}
	public void test00241() {
		check("Integrate((-3*x+2*x^3)*(-3*x^2+x^4)^(3/5), x)", "(5*(-3*x^2+x^4)^(8/5))/16");

	}

	// {(-1+x^4)/(x^2*Sqrt(1+x^2+x^4)), x, 1, Sqrt(1+x^2+x^4)/x}
	public void test00242() {
		check("Integrate((-1+x^4)/(x^2*Sqrt(1+x^2+x^4)), x)", "Sqrt(1+x^2+x^4)/x");

	}

	// {(1-x^2)/((1+2*a*x+x^2)*Sqrt(1+2*a*x+2*b*x^2+2*a*x^3+x^4)), x, 1, ArcTan((a+2*(1+a^2-b)*x +
	// a*x^2)/(Sqrt(2)*Sqrt(1-b)*Sqrt(1+2*a*x+2*b*x^2+2*a*x^3+x^4)))/(Sqrt(2)*Sqrt(1-b))}
	public void test00243() {
		check("Integrate((1-x^2)/((1+2*a*x+x^2)*Sqrt(1+2*a*x+2*b*x^2+2*a*x^3+x^4)), x)",
				"ArcTan((a+2*(1+a^2-b)*x+a*x^2)/(Sqrt(2)*Sqrt(1-b)*Sqrt(1+2*a*x+2*b*x^2+2*a*x^3+x^4)))/(Sqrt(2)*Sqrt(1-b))");

	}

	// {Cos(x)^(2*m)*Sin(x)^(2*m), x, 1, (Cos(x)^(-1+2*m)*(Cos(x)^2)^(1/2-m)*Hypergeometric2F1((1-2*m)/2, (1 +
	// 2*m)/2, (3+2*m)/2, Sin(x)^2)*Sin(x)^(1+2*m))/(1+2*m)}
	public void test00244() {
		check("Integrate(Cos(x)^(2*m)*Sin(x)^(2*m), x)",
				"(Cos(x)^(-1+2*m)*(Cos(x)^2)^(1/2-m)*Hypergeometric2F1((1-2*m)/2, (1+2*m)/2, (3+2*m)/2, Sin(x)^2)*Sin(x)^(1+2*m))/(1+2*m)");

	}

	// {Cos(x)*Cos(4*x), x, 1, Sin(3*x)/6+Sin(5*x)/10}
	public void test00245() {
		check("Integrate(Cos(x)*Cos(4*x), x)", "Sin(3*x)/6+Sin(5*x)/10");

	}

	// {Sqrt(1+Sin(2*x)), x, 1, -(Cos(2*x)/Sqrt(1+Sin(2*x)))}
	public void test00246() {
		check("Integrate(Sqrt(1+Sin(2*x)), x)", "-(Cos(2*x)/Sqrt(1+Sin(2*x)))");

	}

	// {Sqrt(1-Sin(2*x)), x, 1, Cos(2*x)/Sqrt(1-Sin(2*x))}
	public void test00247() {
		check("Integrate(Sqrt(1-Sin(2*x)), x)", "Cos(2*x)/Sqrt(1-Sin(2*x))");

	}

	// {Sin(x)/Sqrt(Sin(2*x)), x, 1, -ArcSin(Cos(x)-Sin(x))/2-Log(Cos(x)+Sin(x)+Sqrt(Sin(2*x)))/2}
	public void test00248() {
		check("Integrate(Sin(x)/Sqrt(Sin(2*x)), x)", "-ArcSin(Cos(x)-Sin(x))/2-Log(Cos(x)+Sin(x)+Sqrt(Sin(2*x)))/2");

	}

	// {Cos(x)/Sqrt(Sin(2*x)), x, 1, -ArcSin(Cos(x)-Sin(x))/2+Log(Cos(x)+Sin(x)+Sqrt(Sin(2*x)))/2}
	public void test00249() {
		check("Integrate(Cos(x)/Sqrt(Sin(2*x)), x)", "-ArcSin(Cos(x)-Sin(x))/2+Log(Cos(x)+Sin(x)+Sqrt(Sin(2*x)))/2");

	}

	// {Csc(x)^5*Sin(2*x)^(3/2), x, 1, -(Csc(x)^5*Sin(2*x)^(5/2))/5}
	public void test00250() {
		check("Integrate(Csc(x)^5*Sin(2*x)^(3/2), x)", "-(Csc(x)^5*Sin(2*x)^(5/2))/5");

	}

	// {Sin(x)/Cos(2*x)^(5/2), x, 1, -Cos(3*x)/(3*Cos(2*x)^(3/2))}
	public void test00251() {
		check("Integrate(Sin(x)/Cos(2*x)^(5/2), x)", "-Cos(3*x)/(3*Cos(2*x)^(3/2))");

	}

	// {x^(1+2*n), x, 1, x^(2*(1+n))/(2*(1+n))}
	public void test00252() {
		check("Integrate(x^(1+2*n), x)", "x^(2*(1+n))/(2*(1+n))");

	}

	// {(E^x*(1-x-x^2))/Sqrt(1-x^2), x, 1, E^x*Sqrt(1-x^2)}
	public void test00253() {
		check("Integrate((E^x*(1-x-x^2))/Sqrt(1-x^2), x)", "E^x*Sqrt(1-x^2)");

	}

	// {Cos(2*x)/E^(3*x), x, 1, (-3*Cos(2*x))/(13*E^(3*x))+(2*Sin(2*x))/(13*E^(3*x))}
	public void test00254() {
		check("Integrate(Cos(2*x)/E^(3*x), x)", "(-3*Cos(2*x))/(13*E^(3*x))+(2*Sin(2*x))/(13*E^(3*x))");

	}

	// {E^(m*x)*Csc(x)^2, x, 1, (-4*E^((2*I+m)*x)*Hypergeometric2F1(2, 1-(I/2)*m, 2-(I/2)*m, E^((2*I)*x)))/(2*I +
	// m)}
	public void test00255() {
		check("Integrate(E^(m*x)*Csc(x)^2, x)",
				"(-4*E^((2*I+m)*x)*Hypergeometric2F1(2, 1-(I/2)*m, 2-(I/2)*m, E^((2*I)*x)))/(2*I+m)");

	}

	// {(E^x*(1-Sin(x)))/(1-Cos(x)), x, 1, -((E^x*Sin(x))/(1-Cos(x)))}
	public void test00256() {
		check("Integrate((E^x*(1-Sin(x)))/(1-Cos(x)), x)", "-((E^x*Sin(x))/(1-Cos(x)))");

	}

	// {(E^x*(1+Sin(x)))/(1+Cos(x)), x, 1, (E^x*Sin(x))/(1+Cos(x))}
	public void test00257() {
		check("Integrate((E^x*(1+Sin(x)))/(1+Cos(x)), x)", "(E^x*Sin(x))/(1+Cos(x))");

	}

	// {(E^x*(1+Cos(x)))/(1-Sin(x)), x, 1, (E^x*Cos(x))/(1-Sin(x))}
	public void test00258() {
		check("Integrate((E^x*(1+Cos(x)))/(1-Sin(x)), x)", "(E^x*Cos(x))/(1-Sin(x))");

	}

	// {(E^x*(1-Cos(x)))/(1+Sin(x)), x, 1, -((E^x*Cos(x))/(1+Sin(x)))}
	public void test00259() {
		check("Integrate((E^x*(1-Cos(x)))/(1+Sin(x)), x)", "-((E^x*Cos(x))/(1+Sin(x)))");

	}

	// {Cosh(x), x, 1, Sinh(x)}
	public void test00260() {
		check("Integrate(Cosh(x), x)", "Sinh(x)");

	}

	// {Sinh(x), x, 1, Cosh(x)}
	public void test00261() {
		check("Integrate(Sinh(x), x)", "Cosh(x)");

	}

	// {Tanh(x), x, 1, Log(Cosh(x))}
	public void test00262() {
		check("Integrate(Tanh(x), x)", "Log(Cosh(x))");

	}

	// {Coth(x), x, 1, Log(Sinh(x))}
	public void test00263() {
		check("Integrate(Coth(x), x)", "Log(Sinh(x))");

	}

	// {Sech(x), x, 1, ArcTan(Sinh(x))}
	public void test00264() {
		check("Integrate(Sech(x), x)", "ArcTan(Sinh(x))");

	}

	// {Csch(x), x, 1, -ArcTanh(Cosh(x))}
	public void test00265() {
		check("Integrate(Csch(x), x)", "-ArcTanh(Cosh(x))");

	}

	// {x^m*Log(x), x, 1, -(x^(1+m)/(1+m)^2)+(x^(1+m)*Log(x))/(1+m)}
	public void test00266() {
		check("Integrate(x^m*Log(x), x)", "-(x^(1+m)/(1+m)^2)+(x^(1+m)*Log(x))/(1+m)");

	}

	// {Log(Log(x))/x, x, 1, -Log(x)+Log(x)*Log(Log(x))}
	public void test00267() {
		check("Integrate(Log(Log(x))/x, x)", "-Log(x)+Log(x)*Log(Log(x))");

	}

	// {1/Sqrt(1-a*x), x, 1, (-2*Sqrt(1-a*x))/a}
	public void test00268() {
		check("Integrate(1/Sqrt(1-a*x), x)", "(-2*Sqrt(1-a*x))/a");

	}

	// {(1-x^3)^(-1/3), x, 1, -(ArcTan((1-(2*x)/(1-x^3)^(1/3))/Sqrt(3))/Sqrt(3))+Log(x+(1-x^3)^(1/3))/2}
	public void test00269() {
		check("Integrate((1-x^3)^(-1/3), x)",
				"-(ArcTan((1-(2*x)/(1-x^3)^(1/3))/Sqrt(3))/Sqrt(3))+Log(x+(1-x^3)^(1/3))/2");

	}

	// {(3-3*x+30*x^2+160*x^3)/(9+24*x-12*x^2+80*x^3+320*x^4), x, 1, Log(9+24*x-12*x^2+80*x^3 +
	// 320*x^4)/8}
	public void test00270() {
		check("Integrate((3-3*x+30*x^2+160*x^3)/(9+24*x-12*x^2+80*x^3+320*x^4), x)",
				"Log(9+24*x-12*x^2+80*x^3+320*x^4)/8");

	}

	// {(3+12*x+20*x^2)/(9+24*x-12*x^2+80*x^3+320*x^4), x, 1, -ArcTan((7-40*x)/(5*Sqrt(11)))/(2*Sqrt(11))
	// +ArcTan((57+30*x-40*x^2+800*x^3)/(6*Sqrt(11)))/(2*Sqrt(11))}
	public void test00271() {
		check("Integrate((3+12*x+20*x^2)/(9+24*x-12*x^2+80*x^3+320*x^4), x)",
				"-ArcTan((7-40*x)/(5*Sqrt(11)))/(2*Sqrt(11))+ArcTan((57+30*x-40*x^2+800*x^3)/(6*Sqrt(11)))/(2*Sqrt(11))");

	}

	// {Sqrt(1-x^4)/(1+x^4), x, 1, ArcTan((x*(1+x^2))/Sqrt(1-x^4))/2+ArcTanh((x*(1-x^2))/Sqrt(1-x^4))/2}
	public void test00272() {
		check("Integrate(Sqrt(1-x^4)/(1+x^4), x)",
				"ArcTan((x*(1+x^2))/Sqrt(1-x^4))/2+ArcTanh((x*(1-x^2))/Sqrt(1-x^4))/2");

	}

	// {Sqrt(1+p*x^2-x^4)/(1+x^4), x, 1, -(Sqrt(p+Sqrt(4+p^2))*ArcTan((Sqrt(p+Sqrt(4+p^2))*x*(p-Sqrt(4 +
	// p^2)-2*x^2))/(2*Sqrt(2)*Sqrt(1+p*x^2-x^4))))/(2*Sqrt(2))+(Sqrt(-p+Sqrt(4+p^2))*ArcTanh((Sqrt(-p +
	// Sqrt(4+p^2))*x*(p+Sqrt(4+p^2)-2*x^2))/(2*Sqrt(2)*Sqrt(1+p*x^2-x^4))))/(2*Sqrt(2))}
	public void test00273() {
		check("Integrate(Sqrt(1+p*x^2-x^4)/(1+x^4), x)",
				"-(Sqrt(p+Sqrt(4+p^2))*ArcTan((Sqrt(p+Sqrt(4+p^2))*x*(p-Sqrt(4+p^2)-2*x^2))/(2*Sqrt(2)*Sqrt(1+p*x^2-x^4))))/(2*Sqrt(2))+(Sqrt(-p+Sqrt(4+p^2))*ArcTanh((Sqrt(-p+Sqrt(4+p^2))*x*(p+Sqrt(4+p^2)-2*x^2))/(2*Sqrt(2)*Sqrt(1+p*x^2-x^4))))/(2*Sqrt(2))");

	}

	// {x/(Sqrt(1-x^3)*(4-x^3)), x, 1, -ArcTan((Sqrt(3)*(1-2^(1/3)*x))/Sqrt(1-x^3))/(3*2^(2/3)*Sqrt(3)) +
	// ArcTan(Sqrt(1-x^3)/Sqrt(3))/(3*2^(2/3)*Sqrt(3))-ArcTanh((1+2^(1/3)*x)/Sqrt(1-x^3))/(3*2^(2/3)) +
	// ArcTanh(Sqrt(1-x^3))/(9*2^(2/3))}
	public void test00274() {
		check("Integrate(x/(Sqrt(1-x^3)*(4-x^3)), x)",
				"-ArcTan((Sqrt(3)*(1-2^(1/3)*x))/Sqrt(1-x^3))/(3*2^(2/3)*Sqrt(3))+ArcTan(Sqrt(1-x^3)/Sqrt(3))/(3*2^(2/3)*Sqrt(3))-ArcTanh((1+2^(1/3)*x)/Sqrt(1-x^3))/(3*2^(2/3))+ArcTanh(Sqrt(1-x^3))/(9*2^(2/3))");

	}

	// {x/((4-d*x^3)*Sqrt(-1+d*x^3)), x, 1, -ArcTan((1+2^(1/3)*d^(1/3)*x)/Sqrt(-1+d*x^3))/(3*2^(2/3)*d^(2/3)) -
	// ArcTan(Sqrt(-1+d*x^3))/(9*2^(2/3)*d^(2/3))-ArcTanh((Sqrt(3)*(1-2^(1/3)*d^(1/3)*x))/Sqrt(-1 +
	// d*x^3))/(3*2^(2/3)*Sqrt(3)*d^(2/3))-ArcTanh(Sqrt(-1+d*x^3)/Sqrt(3))/(3*2^(2/3)*Sqrt(3)*d^(2/3))}
	public void test00275() {
		check("Integrate(x/((4-d*x^3)*Sqrt(-1+d*x^3)), x)",
				"-ArcTan((1+2^(1/3)*d^(1/3)*x)/Sqrt(-1+d*x^3))/(3*2^(2/3)*d^(2/3))-ArcTan(Sqrt(-1+d*x^3))/(9*2^(2/3)*d^(2/3))-ArcTanh((Sqrt(3)*(1-2^(1/3)*d^(1/3)*x))/Sqrt(-1+d*x^3))/(3*2^(2/3)*Sqrt(3)*d^(2/3))-ArcTanh(Sqrt(-1+d*x^3)/Sqrt(3))/(3*2^(2/3)*Sqrt(3)*d^(2/3))");

	}

	// {1/((1-3*x^2)^(1/3)*(3-x^2)), x, 1, ArcTan((1-(1-3*x^2)^(1/3))/x)/4+ArcTanh(x/Sqrt(3))/(4*Sqrt(3)) -
	// ArcTanh((1-(1-3*x^2)^(1/3))^2/(3*Sqrt(3)*x))/(4*Sqrt(3))}
	public void test00276() {
		check("Integrate(1/((1-3*x^2)^(1/3)*(3-x^2)), x)",
				"ArcTan((1-(1-3*x^2)^(1/3))/x)/4+ArcTanh(x/Sqrt(3))/(4*Sqrt(3))-ArcTanh((1-(1-3*x^2)^(1/3))^2/(3*Sqrt(3)*x))/(4*Sqrt(3))");

	}

	// {1/((3+x^2)*(1+3*x^2)^(1/3)), x, 1, ArcTan(x/Sqrt(3))/(4*Sqrt(3))+ArcTan((1-(1 +
	// 3*x^2)^(1/3))^2/(3*Sqrt(3)*x))/(4*Sqrt(3))-ArcTanh((1-(1+3*x^2)^(1/3))/x)/4}
	public void test00277() {
		check("Integrate(1/((3+x^2)*(1+3*x^2)^(1/3)), x)",
				"ArcTan(x/Sqrt(3))/(4*Sqrt(3))+ArcTan((1-(1+3*x^2)^(1/3))^2/(3*Sqrt(3)*x))/(4*Sqrt(3))-ArcTanh((1-(1+3*x^2)^(1/3))/x)/4");

	}

	// {1/((1-x^2)^(1/3)*(3+x^2)), x, 1, ArcTan(Sqrt(3)/x)/(2*2^(2/3)*Sqrt(3))+ArcTan((Sqrt(3)*(1-2^(1/3)*(1 -
	// x^2)^(1/3)))/x)/(2*2^(2/3)*Sqrt(3))-ArcTanh(x)/(6*2^(2/3))+ArcTanh(x/(1+2^(1/3)*(1 -
	// x^2)^(1/3)))/(2*2^(2/3))}
	public void test00278() {
		check("Integrate(1/((1-x^2)^(1/3)*(3+x^2)), x)",
				"ArcTan(Sqrt(3)/x)/(2*2^(2/3)*Sqrt(3))+ArcTan((Sqrt(3)*(1-2^(1/3)*(1-x^2)^(1/3)))/x)/(2*2^(2/3)*Sqrt(3))-ArcTanh(x)/(6*2^(2/3))+ArcTanh(x/(1+2^(1/3)*(1-x^2)^(1/3)))/(2*2^(2/3))");

	}

	// {1/((3-x^2)*(1+x^2)^(1/3)), x, 1, -ArcTan(x)/(6*2^(2/3))+ArcTan(x/(1+2^(1/3)*(1 +
	// x^2)^(1/3)))/(2*2^(2/3))-ArcTanh(Sqrt(3)/x)/(2*2^(2/3)*Sqrt(3))-ArcTanh((Sqrt(3)*(1-2^(1/3)*(1 +
	// x^2)^(1/3)))/x)/(2*2^(2/3)*Sqrt(3))}
	public void test00279() {
		check("Integrate(1/((3-x^2)*(1+x^2)^(1/3)), x)",
				"-ArcTan(x)/(6*2^(2/3))+ArcTan(x/(1+2^(1/3)*(1+x^2)^(1/3)))/(2*2^(2/3))-ArcTanh(Sqrt(3)/x)/(2*2^(2/3)*Sqrt(3))-ArcTanh((Sqrt(3)*(1-2^(1/3)*(1+x^2)^(1/3)))/x)/(2*2^(2/3)*Sqrt(3))");

	}

	// {x/(Sqrt(1+x^3)*(10+6*Sqrt(3)+x^3)), x, 1, -((2-Sqrt(3))*ArcTan((3^(1/4)*(1+Sqrt(3))*(1 +
	// x))/(Sqrt(2)*Sqrt(1+x^3))))/(2*Sqrt(2)*3^(3/4))-((2-Sqrt(3))*ArcTan(((1-Sqrt(3))*Sqrt(1 +
	// x^3))/(Sqrt(2)*3^(3/4))))/(3*Sqrt(2)*3^(3/4))-((2-Sqrt(3))*ArcTanh((3^(1/4)*(1+Sqrt(3) -
	// 2*x))/(Sqrt(2)*Sqrt(1+x^3))))/(3*Sqrt(2)*3^(1/4))-((2-Sqrt(3))*ArcTanh((3^(1/4)*(1-Sqrt(3))*(1 +
	// x))/(Sqrt(2)*Sqrt(1+x^3))))/(6*Sqrt(2)*3^(1/4))}
	public void test00280() {
		check("Integrate(x/(Sqrt(1+x^3)*(10+6*Sqrt(3)+x^3)), x)",
				"-((2-Sqrt(3))*ArcTan((3^(1/4)*(1+Sqrt(3))*(1+x))/(Sqrt(2)*Sqrt(1+x^3))))/(2*Sqrt(2)*3^(3/4))-((2-Sqrt(3))*ArcTan(((1-Sqrt(3))*Sqrt(1+x^3))/(Sqrt(2)*3^(3/4))))/(3*Sqrt(2)*3^(3/4))-((2-Sqrt(3))*ArcTanh((3^(1/4)*(1+Sqrt(3)-2*x))/(Sqrt(2)*Sqrt(1+x^3))))/(3*Sqrt(2)*3^(1/4))-((2-Sqrt(3))*ArcTanh((3^(1/4)*(1-Sqrt(3))*(1+x))/(Sqrt(2)*Sqrt(1+x^3))))/(6*Sqrt(2)*3^(1/4))");

	}

	// {x/(Sqrt(1+x^3)*(10-6*Sqrt(3)+x^3)), x, 1, -((2+Sqrt(3))*ArcTan((3^(1/4)*(1-Sqrt(3) -
	// 2*x))/(Sqrt(2)*Sqrt(1+x^3))))/(3*Sqrt(2)*3^(1/4))-((2+Sqrt(3))*ArcTan((3^(1/4)*(1+Sqrt(3))*(1 +
	// x))/(Sqrt(2)*Sqrt(1+x^3))))/(6*Sqrt(2)*3^(1/4))+((2+Sqrt(3))*ArcTanh((3^(1/4)*(1-Sqrt(3))*(1 +
	// x))/(Sqrt(2)*Sqrt(1+x^3))))/(2*Sqrt(2)*3^(3/4))+((2+Sqrt(3))*ArcTanh(((1+Sqrt(3))*Sqrt(1 +
	// x^3))/(Sqrt(2)*3^(3/4))))/(3*Sqrt(2)*3^(3/4))}
	public void test00281() {
		check("Integrate(x/(Sqrt(1+x^3)*(10-6*Sqrt(3)+x^3)), x)",
				"-((2+Sqrt(3))*ArcTan((3^(1/4)*(1-Sqrt(3)-2*x))/(Sqrt(2)*Sqrt(1+x^3))))/(3*Sqrt(2)*3^(1/4))-((2+Sqrt(3))*ArcTan((3^(1/4)*(1+Sqrt(3))*(1+x))/(Sqrt(2)*Sqrt(1+x^3))))/(6*Sqrt(2)*3^(1/4))+((2+Sqrt(3))*ArcTanh((3^(1/4)*(1-Sqrt(3))*(1+x))/(Sqrt(2)*Sqrt(1+x^3))))/(2*Sqrt(2)*3^(3/4))+((2+Sqrt(3))*ArcTanh(((1+Sqrt(3))*Sqrt(1+x^3))/(Sqrt(2)*3^(3/4))))/(3*Sqrt(2)*3^(3/4))");

	}

	// {x/(Sqrt(-1+x^3)*(-10-6*Sqrt(3)+x^3)), x, 1, ((2-Sqrt(3))*ArcTan((3^(1/4)*(1-Sqrt(3))*(1 -
	// x))/(Sqrt(2)*Sqrt(-1+x^3))))/(6*Sqrt(2)*3^(1/4))+((2-Sqrt(3))*ArcTan((3^(1/4)*(1+Sqrt(3) +
	// 2*x))/(Sqrt(2)*Sqrt(-1+x^3))))/(3*Sqrt(2)*3^(1/4))+((2-Sqrt(3))*ArcTanh((3^(1/4)*(1+Sqrt(3))*(1 -
	// x))/(Sqrt(2)*Sqrt(-1+x^3))))/(2*Sqrt(2)*3^(3/4))-((2-Sqrt(3))*ArcTanh(((1-Sqrt(3))*Sqrt(-1 +
	// x^3))/(Sqrt(2)*3^(3/4))))/(3*Sqrt(2)*3^(3/4))}
	public void test00282() {
		check("Integrate(x/(Sqrt(-1+x^3)*(-10-6*Sqrt(3)+x^3)), x)",
				"((2-Sqrt(3))*ArcTan((3^(1/4)*(1-Sqrt(3))*(1-x))/(Sqrt(2)*Sqrt(-1+x^3))))/(6*Sqrt(2)*3^(1/4))+((2-Sqrt(3))*ArcTan((3^(1/4)*(1+Sqrt(3)+2*x))/(Sqrt(2)*Sqrt(-1+x^3))))/(3*Sqrt(2)*3^(1/4))+((2-Sqrt(3))*ArcTanh((3^(1/4)*(1+Sqrt(3))*(1-x))/(Sqrt(2)*Sqrt(-1+x^3))))/(2*Sqrt(2)*3^(3/4))-((2-Sqrt(3))*ArcTanh(((1-Sqrt(3))*Sqrt(-1+x^3))/(Sqrt(2)*3^(3/4))))/(3*Sqrt(2)*3^(3/4))");

	}

	// {x/(Sqrt(-1+x^3)*(-10+6*Sqrt(3)+x^3)), x, 1, -((2+Sqrt(3))*ArcTan((3^(1/4)*(1-Sqrt(3))*(1 -
	// x))/(Sqrt(2)*Sqrt(-1+x^3))))/(2*Sqrt(2)*3^(3/4))+((2+Sqrt(3))*ArcTan(((1+Sqrt(3))*Sqrt(-1 +
	// x^3))/(Sqrt(2)*3^(3/4))))/(3*Sqrt(2)*3^(3/4))+((2+Sqrt(3))*ArcTanh((3^(1/4)*(1+Sqrt(3))*(1 -
	// x))/(Sqrt(2)*Sqrt(-1+x^3))))/(6*Sqrt(2)*3^(1/4))+((2+Sqrt(3))*ArcTanh((3^(1/4)*(1-Sqrt(3) +
	// 2*x))/(Sqrt(2)*Sqrt(-1+x^3))))/(3*Sqrt(2)*3^(1/4))}
	public void test00283() {
		check("Integrate(x/(Sqrt(-1+x^3)*(-10+6*Sqrt(3)+x^3)), x)",
				"-((2+Sqrt(3))*ArcTan((3^(1/4)*(1-Sqrt(3))*(1-x))/(Sqrt(2)*Sqrt(-1+x^3))))/(2*Sqrt(2)*3^(3/4))+((2+Sqrt(3))*ArcTan(((1+Sqrt(3))*Sqrt(-1+x^3))/(Sqrt(2)*3^(3/4))))/(3*Sqrt(2)*3^(3/4))+((2+Sqrt(3))*ArcTanh((3^(1/4)*(1+Sqrt(3))*(1-x))/(Sqrt(2)*Sqrt(-1+x^3))))/(6*Sqrt(2)*3^(1/4))+((2+Sqrt(3))*ArcTanh((3^(1/4)*(1-Sqrt(3)+2*x))/(Sqrt(2)*Sqrt(-1+x^3))))/(3*Sqrt(2)*3^(1/4))");

	}

	// {(-1+x)/((1+x)*(2+x^3)^(1/3)), x, 1, Sqrt(3)*ArcTan((1+(2*(2+x))/(2+x^3)^(1/3))/Sqrt(3))+Log(1+x)
	// -(3*Log(2+x-(2+x^3)^(1/3)))/2}
	public void test00284() {
		check("Integrate((-1+x)/((1+x)*(2+x^3)^(1/3)), x)",
				"Sqrt(3)*ArcTan((1+(2*(2+x))/(2+x^3)^(1/3))/Sqrt(3))+Log(1+x)-(3*Log(2+x-(2+x^3)^(1/3)))/2");

	}

	// {0, x, 1, 0}
	public void test00285() {
		check("Integrate(0, x)", "0");

	}

	// {1, x, 1, x}
	public void test00286() {
		check("Integrate(1, x)", "x");

	}

	// {5, x, 1, 5*x}
	public void test00287() {
		check("Integrate(5, x)", "5*x");

	}

	// {-2, x, 1, -2*x}
	public void test00288() {
		check("Integrate(-2, x)", "-2*x");

	}

	// {-3/2, x, 1, (-3*x)/2}
	public void test00289() {
		check("Integrate(-3/2, x)", "(-3*x)/2");

	}

	// {Pi, x, 1, Pi*x}
	public void test00290() {
		check("Integrate(Pi, x)", "Pi*x");

	}

	// {a, x, 1, a*x}
	public void test00291() {
		check("Integrate(a, x)", "a*x");

	}

	// {3*a, x, 1, 3*a*x}
	public void test00292() {
		check("Integrate(3*a, x)", "3*a*x");

	}

	// {Pi/Sqrt(16-E^2), x, 1, (Pi*x)/Sqrt(16-E^2)}
	public void test00293() {
		check("Integrate(Pi/Sqrt(16-E^2), x)", "(Pi*x)/Sqrt(16-E^2)");

	}

	// {x^100, x, 1, x^101/101}
	public void test00294() {
		check("Integrate(x^100, x)", "x^101/101");

	}

	// {x^3, x, 1, x^4/4}
	public void test00295() {
		check("Integrate(x^3, x)", "x^4/4");

	}

	// {x^2, x, 1, x^3/3}
	public void test00296() {
		check("Integrate(x^2, x)", "x^3/3");

	}

	// {x, x, 1, x^2/2}
	public void test00297() {
		check("Integrate(x, x)", "x^2/2");

	}

	// {1, x, 1, x}
	public void test00298() {
		check("Integrate(1, x)", "x");

	}

	// {x^(-1), x, 1, Log(x)}
	public void test00299() {
		check("Integrate(x^(-1), x)", "Log(x)");

	}

	// {x^(-2), x, 1, -x^(-1)}
	public void test00300() {
		check("Integrate(x^(-2), x)", "-x^(-1)");

	}

	// {x^(-3), x, 1, -1/(2*x^2)}
	public void test00301() {
		check("Integrate(x^(-3), x)", "-1/(2*x^2)");

	}

	// {x^(-4), x, 1, -1/(3*x^3)}
	public void test00302() {
		check("Integrate(x^(-4), x)", "-1/(3*x^3)");

	}

	// {x^(-100), x, 1, -1/(99*x^99)}
	public void test00303() {
		check("Integrate(x^(-100), x)", "-1/(99*x^99)");

	}

	// {x^(5/2), x, 1, (2*x^(7/2))/7}
	public void test00304() {
		check("Integrate(x^(5/2), x)", "(2*x^(7/2))/7");

	}

	// {x^(3/2), x, 1, (2*x^(5/2))/5}
	public void test00305() {
		check("Integrate(x^(3/2), x)", "(2*x^(5/2))/5");

	}

	// {Sqrt(x), x, 1, (2*x^(3/2))/3}
	public void test00306() {
		check("Integrate(Sqrt(x), x)", "(2*x^(3/2))/3");

	}

	// {1/Sqrt(x), x, 1, 2*Sqrt(x)}
	public void test00307() {
		check("Integrate(1/Sqrt(x), x)", "2*Sqrt(x)");

	}

	// {x^(-3/2), x, 1, -2/Sqrt(x)}
	public void test00308() {
		check("Integrate(x^(-3/2), x)", "-2/Sqrt(x)");

	}

	// {x^(-5/2), x, 1, -2/(3*x^(3/2))}
	public void test00309() {
		check("Integrate(x^(-5/2), x)", "-2/(3*x^(3/2))");

	}

	// {x^(5/3), x, 1, (3*x^(8/3))/8}
	public void test00310() {
		check("Integrate(x^(5/3), x)", "(3*x^(8/3))/8");

	}

	// {x^(4/3), x, 1, (3*x^(7/3))/7}
	public void test00311() {
		check("Integrate(x^(4/3), x)", "(3*x^(7/3))/7");

	}

	// {x^(2/3), x, 1, (3*x^(5/3))/5}
	public void test00312() {
		check("Integrate(x^(2/3), x)", "(3*x^(5/3))/5");

	}

	// {x^(1/3), x, 1, (3*x^(4/3))/4}
	public void test00313() {
		check("Integrate(x^(1/3), x)", "(3*x^(4/3))/4");

	}

	// {x^(-1/3), x, 1, (3*x^(2/3))/2}
	public void test00314() {
		check("Integrate(x^(-1/3), x)", "(3*x^(2/3))/2");

	}

	// {x^(-2/3), x, 1, 3*x^(1/3)}
	public void test00315() {
		check("Integrate(x^(-2/3), x)", "3*x^(1/3)");

	}

	// {x^(-4/3), x, 1, -3/x^(1/3)}
	public void test00316() {
		check("Integrate(x^(-4/3), x)", "-3/x^(1/3)");

	}

	// {x^(-5/3), x, 1, -3/(2*x^(2/3))}
	public void test00317() {
		check("Integrate(x^(-5/3), x)", "-3/(2*x^(2/3))");

	}

	// {x^n, x, 1, x^(1+n)/(1+n)}
	public void test00318() {
		check("Integrate(x^n, x)", "x^(1+n)/(1+n)");

	}

	// {(b*x)^n, x, 1, (b*x)^(1+n)/(b*(1+n))}
	public void test00319() {
		check("Integrate((b*x)^n, x)", "(b*x)^(1+n)/(b*(1+n))");

	}

	// {a+b*x, x, 1, a*x+(b*x^2)/2}
	public void test00320() {
		check("Integrate(a+b*x, x)", "a*x+(b*x^2)/2");

	}

	// {(a+b*x)/x^3, x, 1, -(a+b*x)^2/(2*a*x^2)}
	public void test00321() {
		check("Integrate((a+b*x)/x^3, x)", "-(a+b*x)^2/(2*a*x^2)");

	}

	// {(a+b*x)^2, x, 1, (a+b*x)^3/(3*b)}
	public void test00322() {
		check("Integrate((a+b*x)^2, x)", "(a+b*x)^3/(3*b)");

	}

	// {(a+b*x)^2/x^4, x, 1, -(a+b*x)^3/(3*a*x^3)}
	public void test00323() {
		check("Integrate((a+b*x)^2/x^4, x)", "-(a+b*x)^3/(3*a*x^3)");

	}

	// {(a+b*x)^3, x, 1, (a+b*x)^4/(4*b)}
	public void test00324() {
		check("Integrate((a+b*x)^3, x)", "(a+b*x)^4/(4*b)");

	}

	// {(a+b*x)^3/x^5, x, 1, -(a+b*x)^4/(4*a*x^4)}
	public void test00325() {
		check("Integrate((a+b*x)^3/x^5, x)", "-(a+b*x)^4/(4*a*x^4)");

	}

	// {(a+b*x)^5, x, 1, (a+b*x)^6/(6*b)}
	public void test00326() {
		check("Integrate((a+b*x)^5, x)", "(a+b*x)^6/(6*b)");

	}

	// {(a+b*x)^5/x^7, x, 1, -(a+b*x)^6/(6*a*x^6)}
	public void test00327() {
		check("Integrate((a+b*x)^5/x^7, x)", "-(a+b*x)^6/(6*a*x^6)");

	}

	// {(a+b*x)^7, x, 1, (a+b*x)^8/(8*b)}
	public void test00328() {
		check("Integrate((a+b*x)^7, x)", "(a+b*x)^8/(8*b)");

	}

	// {(a+b*x)^7/x^9, x, 1, -(a+b*x)^8/(8*a*x^8)}
	public void test00329() {
		check("Integrate((a+b*x)^7/x^9, x)", "-(a+b*x)^8/(8*a*x^8)");

	}

	// {(a+b*x)^10, x, 1, (a+b*x)^11/(11*b)}
	public void test00330() {
		check("Integrate((a+b*x)^10, x)", "(a+b*x)^11/(11*b)");

	}

	// {(a+b*x)^10/x^12, x, 1, -(a+b*x)^11/(11*a*x^11)}
	public void test00331() {
		check("Integrate((a+b*x)^10/x^12, x)", "-(a+b*x)^11/(11*a*x^11)");

	}

	// {c*(a+b*x), x, 1, (c*(a+b*x)^2)/(2*b)}
	public void test00332() {
		check("Integrate(c*(a+b*x), x)", "(c*(a+b*x)^2)/(2*b)");

	}

	// {((c+d)*(a+b*x))/e, x, 1, ((c+d)*(a+b*x)^2)/(2*b*e)}
	public void test00333() {
		check("Integrate(((c+d)*(a+b*x))/e, x)", "((c+d)*(a+b*x)^2)/(2*b*e)");

	}

	// {(a+b*x)^(-1), x, 1, Log(a+b*x)/b}
	public void test00334() {
		check("Integrate((a+b*x)^(-1), x)", "Log(a+b*x)/b");

	}

	// {(a+b*x)^(-2), x, 1, -(1/(b*(a+b*x)))}
	public void test00335() {
		check("Integrate((a+b*x)^(-2), x)", "-(1/(b*(a+b*x)))");

	}

	// {x/(a+b*x)^3, x, 1, x^2/(2*a*(a+b*x)^2)}
	public void test00336() {
		check("Integrate(x/(a+b*x)^3, x)", "x^2/(2*a*(a+b*x)^2)");

	}

	// {(a+b*x)^(-3), x, 1, -1/(2*b*(a+b*x)^2)}
	public void test00337() {
		check("Integrate((a+b*x)^(-3), x)", "-1/(2*b*(a+b*x)^2)");

	}

	// {x^2/(a+b*x)^4, x, 1, x^3/(3*a*(a+b*x)^3)}
	public void test00338() {
		check("Integrate(x^2/(a+b*x)^4, x)", "x^3/(3*a*(a+b*x)^3)");

	}

	// {(a+b*x)^(-4), x, 1, -1/(3*b*(a+b*x)^3)}
	public void test00339() {
		check("Integrate((a+b*x)^(-4), x)", "-1/(3*b*(a+b*x)^3)");

	}

	// {x^5/(a+b*x)^7, x, 1, x^6/(6*a*(a+b*x)^6)}
	public void test00340() {
		check("Integrate(x^5/(a+b*x)^7, x)", "x^6/(6*a*(a+b*x)^6)");

	}

	// {(a+b*x)^(-7), x, 1, -1/(6*b*(a+b*x)^6)}
	public void test00341() {
		check("Integrate((a+b*x)^(-7), x)", "-1/(6*b*(a+b*x)^6)");

	}

	// {x^8/(a+b*x)^10, x, 1, x^9/(9*a*(a+b*x)^9)}
	public void test00342() {
		check("Integrate(x^8/(a+b*x)^10, x)", "x^9/(9*a*(a+b*x)^9)");

	}

	// {(a+b*x)^(-10), x, 1, -1/(9*b*(a+b*x)^9)}
	public void test00343() {
		check("Integrate((a+b*x)^(-10), x)", "-1/(9*b*(a+b*x)^9)");

	}

	// {(a+b*x)^8/x^10, x, 1, -(a+b*x)^9/(9*a*x^9)}
	public void test00344() {
		check("Integrate((a+b*x)^8/x^10, x)", "-(a+b*x)^9/(9*a*x^9)");

	}

	// {x^(-10), x, 1, -1/(9*x^9)}
	public void test00345() {
		check("Integrate(x^(-10), x)", "-1/(9*x^9)");

	}

	// {(2+2*x)^(-1), x, 1, Log(1+x)/2}
	public void test00346() {
		check("Integrate((2+2*x)^(-1), x)", "Log(1+x)/2");

	}

	// {(4-6*x)^(-1), x, 1, -Log(2-3*x)/6}
	public void test00347() {
		check("Integrate((4-6*x)^(-1), x)", "-Log(2-3*x)/6");

	}

	// {(a+Sqrt(a)*x)^(-1), x, 1, Log(Sqrt(a)+x)/Sqrt(a)}
	public void test00348() {
		check("Integrate((a+Sqrt(a)*x)^(-1), x)", "Log(Sqrt(a)+x)/Sqrt(a)");

	}

	// {(a+Sqrt(-a)*x)^(-1), x, 1, Log(a+Sqrt(-a)*x)/Sqrt(-a)}
	public void test00349() {
		check("Integrate((a+Sqrt(-a)*x)^(-1), x)", "Log(a+Sqrt(-a)*x)/Sqrt(-a)");

	}

	// {(a^2+Sqrt(-a)*x)^(-1), x, 1, Log(a^2+Sqrt(-a)*x)/Sqrt(-a)}
	public void test00350() {
		check("Integrate((a^2+Sqrt(-a)*x)^(-1), x)", "Log(a^2+Sqrt(-a)*x)/Sqrt(-a)");

	}

	// {(a^3+Sqrt(-a)*x)^(-1), x, 1, Log(a^3+Sqrt(-a)*x)/Sqrt(-a)}
	public void test00351() {
		check("Integrate((a^3+Sqrt(-a)*x)^(-1), x)", "Log(a^3+Sqrt(-a)*x)/Sqrt(-a)");

	}

	// {(a^(-1)+Sqrt(-a)*x)^(-1), x, 1, Log(1-(-a)^(3/2)*x)/Sqrt(-a)}
	public void test00352() {
		check("Integrate((a^(-1)+Sqrt(-a)*x)^(-1), x)", "Log(1-(-a)^(3/2)*x)/Sqrt(-a)");

	}

	// {(a^(-2)+Sqrt(-a)*x)^(-1), x, 1, Log(1+(-a)^(5/2)*x)/Sqrt(-a)}
	public void test00353() {
		check("Integrate((a^(-2)+Sqrt(-a)*x)^(-1), x)", "Log(1+(-a)^(5/2)*x)/Sqrt(-a)");

	}

	// {Sqrt(a+b*x), x, 1, (2*(a+b*x)^(3/2))/(3*b)}
	public void test00354() {
		check("Integrate(Sqrt(a+b*x), x)", "(2*(a+b*x)^(3/2))/(3*b)");

	}

	// {(a+b*x)^(3/2), x, 1, (2*(a+b*x)^(5/2))/(5*b)}
	public void test00355() {
		check("Integrate((a+b*x)^(3/2), x)", "(2*(a+b*x)^(5/2))/(5*b)");

	}

	// {(a+b*x)^(5/2), x, 1, (2*(a+b*x)^(7/2))/(7*b)}
	public void test00356() {
		check("Integrate((a+b*x)^(5/2), x)", "(2*(a+b*x)^(7/2))/(7*b)");

	}

	// {(a+b*x)^(9/2), x, 1, (2*(a+b*x)^(11/2))/(11*b)}
	public void test00357() {
		check("Integrate((a+b*x)^(9/2), x)", "(2*(a+b*x)^(11/2))/(11*b)");

	}

	// {1/Sqrt(a+b*x), x, 1, (2*Sqrt(a+b*x))/b}
	public void test00358() {
		check("Integrate(1/Sqrt(a+b*x), x)", "(2*Sqrt(a+b*x))/b");

	}

	// {(a+b*x)^(-3/2), x, 1, -2/(b*Sqrt(a+b*x))}
	public void test00359() {
		check("Integrate((a+b*x)^(-3/2), x)", "-2/(b*Sqrt(a+b*x))");

	}

	// {(a+b*x)^(-5/2), x, 1, -2/(3*b*(a+b*x)^(3/2))}
	public void test00360() {
		check("Integrate((a+b*x)^(-5/2), x)", "-2/(3*b*(a+b*x)^(3/2))");

	}

	// {(a+b*x)^(1/3), x, 1, (3*(a+b*x)^(4/3))/(4*b)}
	public void test00361() {
		check("Integrate((a+b*x)^(1/3), x)", "(3*(a+b*x)^(4/3))/(4*b)");

	}

	// {(a+b*x)^(2/3), x, 1, (3*(a+b*x)^(5/3))/(5*b)}
	public void test00362() {
		check("Integrate((a+b*x)^(2/3), x)", "(3*(a+b*x)^(5/3))/(5*b)");

	}

	// {(a+b*x)^(4/3), x, 1, (3*(a+b*x)^(7/3))/(7*b)}
	public void test00363() {
		check("Integrate((a+b*x)^(4/3), x)", "(3*(a+b*x)^(7/3))/(7*b)");

	}

	// {(a+b*x)^(-1/3), x, 1, (3*(a+b*x)^(2/3))/(2*b)}
	public void test00364() {
		check("Integrate((a+b*x)^(-1/3), x)", "(3*(a+b*x)^(2/3))/(2*b)");

	}

	// {(-a+b*x)^(-1/3), x, 1, (3*(-a+b*x)^(2/3))/(2*b)}
	public void test00365() {
		check("Integrate((-a+b*x)^(-1/3), x)", "(3*(-a+b*x)^(2/3))/(2*b)");

	}

	// {(a+b*x)^(-2/3), x, 1, (3*(a+b*x)^(1/3))/b}
	public void test00366() {
		check("Integrate((a+b*x)^(-2/3), x)", "(3*(a+b*x)^(1/3))/b");

	}

	// {(a+b*x)^(-4/3), x, 1, -3/(b*(a+b*x)^(1/3))}
	public void test00367() {
		check("Integrate((a+b*x)^(-4/3), x)", "-3/(b*(a+b*x)^(1/3))");

	}

	// {Sqrt(a+b*x)/x^(5/2), x, 1, (-2*(a+b*x)^(3/2))/(3*a*x^(3/2))}
	public void test00368() {
		check("Integrate(Sqrt(a+b*x)/x^(5/2), x)", "(-2*(a+b*x)^(3/2))/(3*a*x^(3/2))");

	}

	// {Sqrt(a-b*x)/x^(5/2), x, 1, (-2*(a-b*x)^(3/2))/(3*a*x^(3/2))}
	public void test00369() {
		check("Integrate(Sqrt(a-b*x)/x^(5/2), x)", "(-2*(a-b*x)^(3/2))/(3*a*x^(3/2))");

	}

	// {Sqrt(2+b*x)/x^(5/2), x, 1, -(2+b*x)^(3/2)/(3*x^(3/2))}
	public void test00370() {
		check("Integrate(Sqrt(2+b*x)/x^(5/2), x)", "-(2+b*x)^(3/2)/(3*x^(3/2))");

	}

	// {Sqrt(2-b*x)/x^(5/2), x, 1, -(2-b*x)^(3/2)/(3*x^(3/2))}
	public void test00371() {
		check("Integrate(Sqrt(2-b*x)/x^(5/2), x)", "-(2-b*x)^(3/2)/(3*x^(3/2))");

	}

	// {1/(x^(3/2)*Sqrt(a+b*x)), x, 1, (-2*Sqrt(a+b*x))/(a*Sqrt(x))}
	public void test00372() {
		check("Integrate(1/(x^(3/2)*Sqrt(a+b*x)), x)", "(-2*Sqrt(a+b*x))/(a*Sqrt(x))");

	}

	// {1/(Sqrt(x)*(a+b*x)^(3/2)), x, 1, (2*Sqrt(x))/(a*Sqrt(a+b*x))}
	public void test00373() {
		check("Integrate(1/(Sqrt(x)*(a+b*x)^(3/2)), x)", "(2*Sqrt(x))/(a*Sqrt(a+b*x))");

	}

	// {Sqrt(x)/(a+b*x)^(5/2), x, 1, (2*x^(3/2))/(3*a*(a+b*x)^(3/2))}
	public void test00374() {
		check("Integrate(Sqrt(x)/(a+b*x)^(5/2), x)", "(2*x^(3/2))/(3*a*(a+b*x)^(3/2))");

	}

	// {1/(x^(3/2)*Sqrt(a-b*x)), x, 1, (-2*Sqrt(a-b*x))/(a*Sqrt(x))}
	public void test00375() {
		check("Integrate(1/(x^(3/2)*Sqrt(a-b*x)), x)", "(-2*Sqrt(a-b*x))/(a*Sqrt(x))");

	}

	// {1/(Sqrt(x)*(a-b*x)^(3/2)), x, 1, (2*Sqrt(x))/(a*Sqrt(a-b*x))}
	public void test00376() {
		check("Integrate(1/(Sqrt(x)*(a-b*x)^(3/2)), x)", "(2*Sqrt(x))/(a*Sqrt(a-b*x))");

	}

	// {Sqrt(x)/(a-b*x)^(5/2), x, 1, (2*x^(3/2))/(3*a*(a-b*x)^(3/2))}
	public void test00377() {
		check("Integrate(Sqrt(x)/(a-b*x)^(5/2), x)", "(2*x^(3/2))/(3*a*(a-b*x)^(3/2))");

	}

	// {1/(x^(3/2)*Sqrt(2+b*x)), x, 1, -(Sqrt(2+b*x)/Sqrt(x))}
	public void test00378() {
		check("Integrate(1/(x^(3/2)*Sqrt(2+b*x)), x)", "-(Sqrt(2+b*x)/Sqrt(x))");

	}

	// {1/(Sqrt(x)*(2+b*x)^(3/2)), x, 1, Sqrt(x)/Sqrt(2+b*x)}
	public void test00379() {
		check("Integrate(1/(Sqrt(x)*(2+b*x)^(3/2)), x)", "Sqrt(x)/Sqrt(2+b*x)");

	}

	// {Sqrt(x)/(2+b*x)^(5/2), x, 1, x^(3/2)/(3*(2+b*x)^(3/2))}
	public void test00380() {
		check("Integrate(Sqrt(x)/(2+b*x)^(5/2), x)", "x^(3/2)/(3*(2+b*x)^(3/2))");

	}

	// {1/(x^(3/2)*Sqrt(2-b*x)), x, 1, -(Sqrt(2-b*x)/Sqrt(x))}
	public void test00381() {
		check("Integrate(1/(x^(3/2)*Sqrt(2-b*x)), x)", "-(Sqrt(2-b*x)/Sqrt(x))");

	}

	// {1/(Sqrt(x)*(2-b*x)^(3/2)), x, 1, Sqrt(x)/Sqrt(2-b*x)}
	public void test00382() {
		check("Integrate(1/(Sqrt(x)*(2-b*x)^(3/2)), x)", "Sqrt(x)/Sqrt(2-b*x)");

	}

	// {Sqrt(x)/(2-b*x)^(5/2), x, 1, x^(3/2)/(3*(2-b*x)^(3/2))}
	public void test00383() {
		check("Integrate(Sqrt(x)/(2-b*x)^(5/2), x)", "x^(3/2)/(3*(2-b*x)^(3/2))");

	}

	// {x^m/(a+b*x), x, 1, (x^(1+m)*Hypergeometric2F1(1, 1+m, 2+m, -((b*x)/a)))/(a*(1+m))}
	public void test00384() {
		check("Integrate(x^m/(a+b*x), x)", "(x^(1+m)*Hypergeometric2F1(1, 1+m, 2+m, -((b*x)/a)))/(a*(1+m))");

	}

	// {x^m/(a+b*x)^2, x, 1, (x^(1+m)*Hypergeometric2F1(2, 1+m, 2+m, -((b*x)/a)))/(a^2*(1+m))}
	public void test00385() {
		check("Integrate(x^m/(a+b*x)^2, x)", "(x^(1+m)*Hypergeometric2F1(2, 1+m, 2+m, -((b*x)/a)))/(a^2*(1+m))");

	}

	// {x^m/(a+b*x)^3, x, 1, (x^(1+m)*Hypergeometric2F1(3, 1+m, 2+m, -((b*x)/a)))/(a^3*(1+m))}
	public void test00386() {
		check("Integrate(x^m/(a+b*x)^3, x)", "(x^(1+m)*Hypergeometric2F1(3, 1+m, 2+m, -((b*x)/a)))/(a^3*(1+m))");

	}

	// {x^m/Sqrt(2+3*x), x, 1, (x^(1+m)*Hypergeometric2F1(1/2, 1+m, 2+m, (-3*x)/2))/(Sqrt(2)*(1+m))}
	public void test00387() {
		check("Integrate(x^m/Sqrt(2+3*x), x)", "(x^(1+m)*Hypergeometric2F1(1/2, 1+m, 2+m, (-3*x)/2))/(Sqrt(2)*(1+m))");

	}

	// {x^m/Sqrt(2-3*x), x, 1, (x^(1+m)*Hypergeometric2F1(1/2, 1+m, 2+m, (3*x)/2))/(Sqrt(2)*(1+m))}
	public void test00388() {
		check("Integrate(x^m/Sqrt(2-3*x), x)", "(x^(1+m)*Hypergeometric2F1(1/2, 1+m, 2+m, (3*x)/2))/(Sqrt(2)*(1+m))");

	}

	// {x^m/Sqrt(-2+3*x), x, 1, (3/2)^(-1-m)*Sqrt(-2+3*x)*Hypergeometric2F1(1/2, -m, 3/2, 1-(3*x)/2)}
	public void test00389() {
		check("Integrate(x^m/Sqrt(-2+3*x), x)", "(3/2)^(-1-m)*Sqrt(-2+3*x)*Hypergeometric2F1(1/2, -m, 3/2, 1-(3*x)/2)");

	}

	// {(-x)^m/Sqrt(2+3*x), x, 1, -(((-x)^(1+m)*Hypergeometric2F1(1/2, 1+m, 2+m, (-3*x)/2))/(Sqrt(2)*(1+m)))}
	public void test00390() {
		check("Integrate((-x)^m/Sqrt(2+3*x), x)",
				"-(((-x)^(1+m)*Hypergeometric2F1(1/2, 1+m, 2+m, (-3*x)/2))/(Sqrt(2)*(1+m)))");

	}

	// {(-x)^m/Sqrt(2-3*x), x, 1, -(((-x)^(1+m)*Hypergeometric2F1(1/2, 1+m, 2+m, (3*x)/2))/(Sqrt(2)*(1+m)))}
	public void test00391() {
		check("Integrate((-x)^m/Sqrt(2-3*x), x)",
				"-(((-x)^(1+m)*Hypergeometric2F1(1/2, 1+m, 2+m, (3*x)/2))/(Sqrt(2)*(1+m)))");

	}

	// {(-x)^m/Sqrt(-2-3*x), x, 1, -((3/2)^(-1-m)*Sqrt(-2-3*x)*Hypergeometric2F1(1/2, -m, 3/2, 1+(3*x)/2))}
	public void test00392() {
		check("Integrate((-x)^m/Sqrt(-2-3*x), x)",
				"-((3/2)^(-1-m)*Sqrt(-2-3*x)*Hypergeometric2F1(1/2, -m, 3/2, 1+(3*x)/2))");

	}

	// {x^n/Sqrt(1-x), x, 1, -2*Sqrt(1-x)*Hypergeometric2F1(1/2, -n, 3/2, 1-x)}
	public void test00393() {
		check("Integrate(x^n/Sqrt(1-x), x)", "-2*Sqrt(1-x)*Hypergeometric2F1(1/2, -n, 3/2, 1-x)");

	}

	// {x^n/Sqrt(a-a*x), x, 1, (-2*Sqrt(a-a*x)*Hypergeometric2F1(1/2, -n, 3/2, 1-x))/a}
	public void test00394() {
		check("Integrate(x^n/Sqrt(a-a*x), x)", "(-2*Sqrt(a-a*x)*Hypergeometric2F1(1/2, -n, 3/2, 1-x))/a");

	}

	// {(a+b*x)^n, x, 1, (a+b*x)^(1+n)/(b*(1+n))}
	public void test00395() {
		check("Integrate((a+b*x)^n, x)", "(a+b*x)^(1+n)/(b*(1+n))");

	}

	// {(a+b*x)^n/x, x, 1, -(((a+b*x)^(1+n)*Hypergeometric2F1(1, 1+n, 2+n, 1+(b*x)/a))/(a*(1+n)))}
	public void test00396() {
		check("Integrate((a+b*x)^n/x, x)", "-(((a+b*x)^(1+n)*Hypergeometric2F1(1, 1+n, 2+n, 1+(b*x)/a))/(a*(1+n)))");

	}

	// {(a+b*x)^n/x^2, x, 1, (b*(a+b*x)^(1+n)*Hypergeometric2F1(2, 1+n, 2+n, 1+(b*x)/a))/(a^2*(1+n))}
	public void test00397() {
		check("Integrate((a+b*x)^n/x^2, x)", "(b*(a+b*x)^(1+n)*Hypergeometric2F1(2, 1+n, 2+n, 1+(b*x)/a))/(a^2*(1+n))");

	}

	// {(a+b*x)^n/x^3, x, 1, -((b^2*(a+b*x)^(1+n)*Hypergeometric2F1(3, 1+n, 2+n, 1+(b*x)/a))/(a^3*(1+n)))}
	public void test00398() {
		check("Integrate((a+b*x)^n/x^3, x)",
				"-((b^2*(a+b*x)^(1+n)*Hypergeometric2F1(3, 1+n, 2+n, 1+(b*x)/a))/(a^3*(1+n)))");

	}

	// {x^(-2+n)/(a+b*x)^n, x, 1, -((x^(-1+n)*(a+b*x)^(1-n))/(a*(1-n)))}
	public void test00399() {
		check("Integrate(x^(-2+n)/(a+b*x)^n, x)", "-((x^(-1+n)*(a+b*x)^(1-n))/(a*(1-n)))");

	}

	// {(b*x)^m*(2+d*x)^n, x, 1, (2^n*(b*x)^(1+m)*Hypergeometric2F1(1+m, -n, 2+m, -(d*x)/2))/(b*(1+m))}
	public void test00400() {
		check("Integrate((b*x)^m*(2+d*x)^n, x)",
				"(2^n*(b*x)^(1+m)*Hypergeometric2F1(1+m, -n, 2+m, -(d*x)/2))/(b*(1+m))");

	}

	// {(b*x)^m*(c-b*c*x)^n, x, 1, -(((c-b*c*x)^(1+n)*Hypergeometric2F1(-m, 1+n, 2+n, 1-b*x))/(b*c*(1 +
	// n)))}
	public void test00401() {
		check("Integrate((b*x)^m*(c-b*c*x)^n, x)",
				"-(((c-b*c*x)^(1+n)*Hypergeometric2F1(-m, 1+n, 2+n, 1-b*x))/(b*c*(1+n)))");

	}

	// {x^(-1+n)*(a+b*x)^(-1-n), x, 1, x^n/(a*n*(a+b*x)^n)}
	public void test00402() {
		check("Integrate(x^(-1+n)*(a+b*x)^(-1-n), x)", "x^n/(a*n*(a+b*x)^n)");

	}

	// {a+b*x, x, 1, a*x+(b*x^2)/2}
	public void test00403() {
		check("Integrate(a+b*x, x)", "a*x+(b*x^2)/2");

	}

	// {(a+b*x)/(a*c-b*c*x)^3, x, 1, x/(c^3*(a-b*x)^2)}
	public void test00404() {
		check("Integrate((a+b*x)/(a*c-b*c*x)^3, x)", "x/(c^3*(a-b*x)^2)");

	}

	// {(a+b*x)^2, x, 1, (a+b*x)^3/(3*b)}
	public void test00405() {
		check("Integrate((a+b*x)^2, x)", "(a+b*x)^3/(3*b)");

	}

	// {(a+b*x)^2/(a*c-b*c*x)^4, x, 1, (a+b*x)^3/(6*a*b*c^4*(a-b*x)^3)}
	public void test00406() {
		check("Integrate((a+b*x)^2/(a*c-b*c*x)^4, x)", "(a+b*x)^3/(6*a*b*c^4*(a-b*x)^3)");

	}

	// {(a+b*x)^(-1), x, 1, Log(a+b*x)/b}
	public void test00407() {
		check("Integrate((a+b*x)^(-1), x)", "Log(a+b*x)/b");

	}

	// {(a+b*x)^(-2), x, 1, -(1/(b*(a+b*x)))}
	public void test00408() {
		check("Integrate((a+b*x)^(-2), x)", "-(1/(b*(a+b*x)))");

	}

	// {Sqrt(1+x)/(1-x)^(5/2), x, 1, (1+x)^(3/2)/(3*(1-x)^(3/2))}
	public void test00409() {
		check("Integrate(Sqrt(1+x)/(1-x)^(5/2), x)", "(1+x)^(3/2)/(3*(1-x)^(3/2))");

	}

	// {(1+x)^(3/2)/(1-x)^(7/2), x, 1, (1+x)^(5/2)/(5*(1-x)^(5/2))}
	public void test00410() {
		check("Integrate((1+x)^(3/2)/(1-x)^(7/2), x)", "(1+x)^(5/2)/(5*(1-x)^(5/2))");

	}

	// {(1+x)^(5/2)/(1-x)^(9/2), x, 1, (1+x)^(7/2)/(7*(1-x)^(7/2))}
	public void test00411() {
		check("Integrate((1+x)^(5/2)/(1-x)^(9/2), x)", "(1+x)^(7/2)/(7*(1-x)^(7/2))");

	}

	// {1/((1-x)^(3/2)*Sqrt(1+x)), x, 1, Sqrt(1+x)/Sqrt(1-x)}
	public void test00412() {
		check("Integrate(1/((1-x)^(3/2)*Sqrt(1+x)), x)", "Sqrt(1+x)/Sqrt(1-x)");

	}

	// {1/(Sqrt(1-x)*(1+x)^(3/2)), x, 1, -(Sqrt(1-x)/Sqrt(1+x))}
	public void test00413() {
		check("Integrate(1/(Sqrt(1-x)*(1+x)^(3/2)), x)", "-(Sqrt(1-x)/Sqrt(1+x))");

	}

	// {1/((1-x)^(3/2)*(1+x)^(3/2)), x, 1, x/(Sqrt(1-x)*Sqrt(1+x))}
	public void test00414() {
		check("Integrate(1/((1-x)^(3/2)*(1+x)^(3/2)), x)", "x/(Sqrt(1-x)*Sqrt(1+x))");

	}

	// {Sqrt(1-x)/(1+x)^(5/2), x, 1, -(1-x)^(3/2)/(3*(1+x)^(3/2))}
	public void test00415() {
		check("Integrate(Sqrt(1-x)/(1+x)^(5/2), x)", "-(1-x)^(3/2)/(3*(1+x)^(3/2))");

	}

	// {1/((a+a*x)^(3/2)*(c-c*x)^(3/2)), x, 1, x/(a*c*Sqrt(a+a*x)*Sqrt(c-c*x))}
	public void test00416() {
		check("Integrate(1/((a+a*x)^(3/2)*(c-c*x)^(3/2)), x)", "x/(a*c*Sqrt(a+a*x)*Sqrt(c-c*x))");

	}

	// {1/((a+b*x)^(3/2)*(a*c-b*c*x)^(3/2)), x, 1, x/(a^2*c*Sqrt(a+b*x)*Sqrt(a*c-b*c*x))}
	public void test00417() {
		check("Integrate(1/((a+b*x)^(3/2)*(a*c-b*c*x)^(3/2)), x)", "x/(a^2*c*Sqrt(a+b*x)*Sqrt(a*c-b*c*x))");

	}

	// {1/((3-6*x)^(3/2)*(2+4*x)^(3/2)), x, 1, x/(6*Sqrt(6)*Sqrt(1-2*x)*Sqrt(1+2*x))}
	public void test00418() {
		check("Integrate(1/((3-6*x)^(3/2)*(2+4*x)^(3/2)), x)", "x/(6*Sqrt(6)*Sqrt(1-2*x)*Sqrt(1+2*x))");

	}

	// {1/((3-x)^(3/2)*(3+x)^(3/2)), x, 1, x/(9*Sqrt(3-x)*Sqrt(3+x))}
	public void test00419() {
		check("Integrate(1/((3-x)^(3/2)*(3+x)^(3/2)), x)", "x/(9*Sqrt(3-x)*Sqrt(3+x))");

	}

	// {1/((3-b*x)^(3/2)*(3+b*x)^(3/2)), x, 1, x/(9*Sqrt(3-b*x)*Sqrt(3+b*x))}
	public void test00420() {
		check("Integrate(1/((3-b*x)^(3/2)*(3+b*x)^(3/2)), x)", "x/(9*Sqrt(3-b*x)*Sqrt(3+b*x))");

	}

	// {1/((6-2*x)^(3/2)*(3+x)^(3/2)), x, 1, x/(18*Sqrt(2)*Sqrt(3-x)*Sqrt(3+x))}
	public void test00421() {
		check("Integrate(1/((6-2*x)^(3/2)*(3+x)^(3/2)), x)", "x/(18*Sqrt(2)*Sqrt(3-x)*Sqrt(3+x))");

	}

	// {1/((6-2*b*x)^(3/2)*(3+b*x)^(3/2)), x, 1, x/(18*Sqrt(2)*Sqrt(3-b*x)*Sqrt(3+b*x))}
	public void test00422() {
		check("Integrate(1/((6-2*b*x)^(3/2)*(3+b*x)^(3/2)), x)", "x/(18*Sqrt(2)*Sqrt(3-b*x)*Sqrt(3+b*x))");

	}

	// {1/((a-I*a*x)^(7/4)*(a+I*a*x)^(1/4)), x, 1, (((-2*I)/3)*(a+I*a*x)^(3/4))/(a^2*(a-I*a*x)^(3/4))}
	public void test00423() {
		check("Integrate(1/((a-I*a*x)^(7/4)*(a+I*a*x)^(1/4)), x)",
				"(((-2*I)/3)*(a+I*a*x)^(3/4))/(a^2*(a-I*a*x)^(3/4))");

	}

	// {1/((a-I*a*x)^(5/4)*(a+I*a*x)^(3/4)), x, 1, ((-2*I)*(a+I*a*x)^(1/4))/(a^2*(a-I*a*x)^(1/4))}
	public void test00424() {
		check("Integrate(1/((a-I*a*x)^(5/4)*(a+I*a*x)^(3/4)), x)", "((-2*I)*(a+I*a*x)^(1/4))/(a^2*(a-I*a*x)^(1/4))");

	}

	// {1/((a-I*a*x)^(1/4)*(a+I*a*x)^(7/4)), x, 1, (((2*I)/3)*(a-I*a*x)^(3/4))/(a^2*(a+I*a*x)^(3/4))}
	public void test00425() {
		check("Integrate(1/((a-I*a*x)^(1/4)*(a+I*a*x)^(7/4)), x)", "(((2*I)/3)*(a-I*a*x)^(3/4))/(a^2*(a+I*a*x)^(3/4))");

	}

	// {1/((a-I*a*x)^(3/4)*(a+I*a*x)^(5/4)), x, 1, ((2*I)*(a-I*a*x)^(1/4))/(a^2*(a+I*a*x)^(1/4))}
	public void test00426() {
		check("Integrate(1/((a-I*a*x)^(3/4)*(a+I*a*x)^(5/4)), x)", "((2*I)*(a-I*a*x)^(1/4))/(a^2*(a+I*a*x)^(1/4))");

	}

	// {(a-I*a*x)^(1/4)/(a+I*a*x)^(9/4), x, 1, (((2*I)/5)*(a-I*a*x)^(5/4))/(a^2*(a+I*a*x)^(5/4))}
	public void test00427() {
		check("Integrate((a-I*a*x)^(1/4)/(a+I*a*x)^(9/4), x)", "(((2*I)/5)*(a-I*a*x)^(5/4))/(a^2*(a+I*a*x)^(5/4))");

	}

	// {(a*c-b*c*x)^n/(a+b*x), x, 1, -((a*c-b*c*x)^(1+n)*Hypergeometric2F1(1, 1+n, 2+n, (a -
	// b*x)/(2*a)))/(2*a*b*c*(1+n))}
	public void test00428() {
		check("Integrate((a*c-b*c*x)^n/(a+b*x), x)",
				"-((a*c-b*c*x)^(1+n)*Hypergeometric2F1(1, 1+n, 2+n, (a-b*x)/(2*a)))/(2*a*b*c*(1+n))");

	}

	// {(a*c-b*c*x)^n/(a+b*x)^2, x, 1, -((a*c-b*c*x)^(1+n)*Hypergeometric2F1(2, 1+n, 2+n, (a -
	// b*x)/(2*a)))/(4*a^2*b*c*(1+n))}
	public void test00429() {
		check("Integrate((a*c-b*c*x)^n/(a+b*x)^2, x)",
				"-((a*c-b*c*x)^(1+n)*Hypergeometric2F1(2, 1+n, 2+n, (a-b*x)/(2*a)))/(4*a^2*b*c*(1+n))");

	}

	// {c+d*x, x, 1, c*x+(d*x^2)/2}
	public void test00430() {
		check("Integrate(c+d*x, x)", "c*x+(d*x^2)/2");

	}

	// {(c+d*x)/(a+b*x)^3, x, 1, -(c+d*x)^2/(2*(b*c-a*d)*(a+b*x)^2)}
	public void test00431() {
		check("Integrate((c+d*x)/(a+b*x)^3, x)", "-(c+d*x)^2/(2*(b*c-a*d)*(a+b*x)^2)");

	}

	// {(c+d*x)^2, x, 1, (c+d*x)^3/(3*d)}
	public void test00432() {
		check("Integrate((c+d*x)^2, x)", "(c+d*x)^3/(3*d)");

	}

	// {(c+d*x)^2/(a+b*x)^4, x, 1, -(c+d*x)^3/(3*(b*c-a*d)*(a+b*x)^3)}
	public void test00433() {
		check("Integrate((c+d*x)^2/(a+b*x)^4, x)", "-(c+d*x)^3/(3*(b*c-a*d)*(a+b*x)^3)");

	}

	// {(c+d*x)^3, x, 1, (c+d*x)^4/(4*d)}
	public void test00434() {
		check("Integrate((c+d*x)^3, x)", "(c+d*x)^4/(4*d)");

	}

	// {(c+d*x)^3/(a+b*x)^5, x, 1, -(c+d*x)^4/(4*(b*c-a*d)*(a+b*x)^4)}
	public void test00435() {
		check("Integrate((c+d*x)^3/(a+b*x)^5, x)", "-(c+d*x)^4/(4*(b*c-a*d)*(a+b*x)^4)");

	}

	// {(c+d*x)^7, x, 1, (c+d*x)^8/(8*d)}
	public void test00436() {
		check("Integrate((c+d*x)^7, x)", "(c+d*x)^8/(8*d)");

	}

	// {(c+d*x)^7/(a+b*x)^9, x, 1, -(c+d*x)^8/(8*(b*c-a*d)*(a+b*x)^8)}
	public void test00437() {
		check("Integrate((c+d*x)^7/(a+b*x)^9, x)", "-(c+d*x)^8/(8*(b*c-a*d)*(a+b*x)^8)");

	}

	// {(c+d*x)^10, x, 1, (c+d*x)^11/(11*d)}
	public void test00438() {
		check("Integrate((c+d*x)^10, x)", "(c+d*x)^11/(11*d)");

	}

	// {(c+d*x)^10/(a+b*x)^12, x, 1, -(c+d*x)^11/(11*(b*c-a*d)*(a+b*x)^11)}
	public void test00439() {
		check("Integrate((c+d*x)^10/(a+b*x)^12, x)", "-(c+d*x)^11/(11*(b*c-a*d)*(a+b*x)^11)");

	}

	// {(c+d*x)^(-1), x, 1, Log(c+d*x)/d}
	public void test00440() {
		check("Integrate((c+d*x)^(-1), x)", "Log(c+d*x)/d");

	}

	// {(c+d*x)^(-2), x, 1, -(1/(d*(c+d*x)))}
	public void test00441() {
		check("Integrate((c+d*x)^(-2), x)", "-(1/(d*(c+d*x)))");

	}

	// {(a+b*x)/(c+d*x)^3, x, 1, (a+b*x)^2/(2*(b*c-a*d)*(c+d*x)^2)}
	public void test00442() {
		check("Integrate((a+b*x)/(c+d*x)^3, x)", "(a+b*x)^2/(2*(b*c-a*d)*(c+d*x)^2)");

	}

	// {(c+d*x)^(-3), x, 1, -1/(2*d*(c+d*x)^2)}
	public void test00443() {
		check("Integrate((c+d*x)^(-3), x)", "-1/(2*d*(c+d*x)^2)");

	}

	// {(a+b*x)^6/(c+d*x)^8, x, 1, (a+b*x)^7/(7*(b*c-a*d)*(c+d*x)^7)}
	public void test00444() {
		check("Integrate((a+b*x)^6/(c+d*x)^8, x)", "(a+b*x)^7/(7*(b*c-a*d)*(c+d*x)^7)");

	}

	// {(c+d*x)^(-8), x, 1, -1/(7*d*(c+d*x)^7)}
	public void test00445() {
		check("Integrate((c+d*x)^(-8), x)", "-1/(7*d*(c+d*x)^7)");

	}

	// {Sqrt(c+d*x), x, 1, (2*(c+d*x)^(3/2))/(3*d)}
	public void test00446() {
		check("Integrate(Sqrt(c+d*x), x)", "(2*(c+d*x)^(3/2))/(3*d)");

	}

	// {(c+d*x)^(3/2), x, 1, (2*(c+d*x)^(5/2))/(5*d)}
	public void test00447() {
		check("Integrate((c+d*x)^(3/2), x)", "(2*(c+d*x)^(5/2))/(5*d)");

	}

	// {(c+d*x)^(5/2), x, 1, (2*(c+d*x)^(7/2))/(7*d)}
	public void test00448() {
		check("Integrate((c+d*x)^(5/2), x)", "(2*(c+d*x)^(7/2))/(7*d)");

	}

	// {1/Sqrt(c+d*x), x, 1, (2*Sqrt(c+d*x))/d}
	public void test00449() {
		check("Integrate(1/Sqrt(c+d*x), x)", "(2*Sqrt(c+d*x))/d");

	}

	// {(c+d*x)^(-3/2), x, 1, -2/(d*Sqrt(c+d*x))}
	public void test00450() {
		check("Integrate((c+d*x)^(-3/2), x)", "-2/(d*Sqrt(c+d*x))");

	}

	// {(c+d*x)^(-5/2), x, 1, -2/(3*d*(c+d*x)^(3/2))}
	public void test00451() {
		check("Integrate((c+d*x)^(-5/2), x)", "-2/(3*d*(c+d*x)^(3/2))");

	}

	// {Sqrt(c+d*x)/(a+b*x)^(5/2), x, 1, (-2*(c+d*x)^(3/2))/(3*(b*c-a*d)*(a+b*x)^(3/2))}
	public void test00452() {
		check("Integrate(Sqrt(c+d*x)/(a+b*x)^(5/2), x)", "(-2*(c+d*x)^(3/2))/(3*(b*c-a*d)*(a+b*x)^(3/2))");

	}

	// {(c+d*x)^(3/2)/(a+b*x)^(7/2), x, 1, (-2*(c+d*x)^(5/2))/(5*(b*c-a*d)*(a+b*x)^(5/2))}
	public void test00453() {
		check("Integrate((c+d*x)^(3/2)/(a+b*x)^(7/2), x)", "(-2*(c+d*x)^(5/2))/(5*(b*c-a*d)*(a+b*x)^(5/2))");

	}

	// {(c+d*x)^(5/2)/(a+b*x)^(9/2), x, 1, (-2*(c+d*x)^(7/2))/(7*(b*c-a*d)*(a+b*x)^(7/2))}
	public void test00454() {
		check("Integrate((c+d*x)^(5/2)/(a+b*x)^(9/2), x)", "(-2*(c+d*x)^(7/2))/(7*(b*c-a*d)*(a+b*x)^(7/2))");

	}

	// {1/((a+b*x)^(3/2)*Sqrt(c+d*x)), x, 1, (-2*Sqrt(c+d*x))/((b*c-a*d)*Sqrt(a+b*x))}
	public void test00455() {
		check("Integrate(1/((a+b*x)^(3/2)*Sqrt(c+d*x)), x)", "(-2*Sqrt(c+d*x))/((b*c-a*d)*Sqrt(a+b*x))");

	}

	// {1/(Sqrt(a+b*x)*(c+d*x)^(3/2)), x, 1, (2*Sqrt(a+b*x))/((b*c-a*d)*Sqrt(c+d*x))}
	public void test00456() {
		check("Integrate(1/(Sqrt(a+b*x)*(c+d*x)^(3/2)), x)", "(2*Sqrt(a+b*x))/((b*c-a*d)*Sqrt(c+d*x))");

	}

	// {Sqrt(a+b*x)/(c+d*x)^(5/2), x, 1, (2*(a+b*x)^(3/2))/(3*(b*c-a*d)*(c+d*x)^(3/2))}
	public void test00457() {
		check("Integrate(Sqrt(a+b*x)/(c+d*x)^(5/2), x)", "(2*(a+b*x)^(3/2))/(3*(b*c-a*d)*(c+d*x)^(3/2))");

	}

	// {1/(Sqrt(-2+b*x)*Sqrt(2+b*x)), x, 1, ArcCosh((b*x)/2)/b}
	public void test00458() {
		check("Integrate(1/(Sqrt(-2+b*x)*Sqrt(2+b*x)), x)", "ArcCosh((b*x)/2)/b");

	}

	// {(2+b*x)^(-1), x, 1, Log(2+b*x)/b}
	public void test00459() {
		check("Integrate((2+b*x)^(-1), x)", "Log(2+b*x)/b");

	}

	// {1/(Sqrt(-2+b*x)*Sqrt(2+b*x)), x, 1, ArcCosh((b*x)/2)/b}
	public void test00460() {
		check("Integrate(1/(Sqrt(-2+b*x)*Sqrt(2+b*x)), x)", "ArcCosh((b*x)/2)/b");

	}

	// {(2-b*x)^(-1), x, 1, -(Log(2-b*x)/b)}
	public void test00461() {
		check("Integrate((2-b*x)^(-1), x)", "-(Log(2-b*x)/b)");

	}

	// {1/(Sqrt(-2-b*x)*Sqrt(2-b*x)), x, 1, -(ArcCosh(-(b*x)/2)/b)}
	public void test00462() {
		check("Integrate(1/(Sqrt(-2-b*x)*Sqrt(2-b*x)), x)", "-(ArcCosh(-(b*x)/2)/b)");

	}

	// {1/(Sqrt(-4+b*x)*Sqrt(4+b*x)), x, 1, ArcCosh((b*x)/4)/b}
	public void test00463() {
		check("Integrate(1/(Sqrt(-4+b*x)*Sqrt(4+b*x)), x)", "ArcCosh((b*x)/4)/b");

	}

	// {(c+d*x)^(1/3)/(a+b*x)^(7/3), x, 1, (-3*(c+d*x)^(4/3))/(4*(b*c-a*d)*(a+b*x)^(4/3))}
	public void test00464() {
		check("Integrate((c+d*x)^(1/3)/(a+b*x)^(7/3), x)", "(-3*(c+d*x)^(4/3))/(4*(b*c-a*d)*(a+b*x)^(4/3))");

	}

	// {1/((a+b*x)^(2/3)*(c+d*x)^(1/3)), x, 1, -((Sqrt(3)*ArcTan(1/Sqrt(3)+(2*b^(1/3)*(c +
	// d*x)^(1/3))/(Sqrt(3)*d^(1/3)*(a+b*x)^(1/3))))/(b^(2/3)*d^(1/3)))-Log(a+b*x)/(2*b^(2/3)*d^(1/3))-(3*Log(-1
	// +(b^(1/3)*(c+d*x)^(1/3))/(d^(1/3)*(a+b*x)^(1/3))))/(2*b^(2/3)*d^(1/3))}
	public void test00465() {
		check("Integrate(1/((a+b*x)^(2/3)*(c+d*x)^(1/3)), x)",
				"-((Sqrt(3)*ArcTan(1/Sqrt(3)+(2*b^(1/3)*(c+d*x)^(1/3))/(Sqrt(3)*d^(1/3)*(a+b*x)^(1/3))))/(b^(2/3)*d^(1/3)))-Log(a+b*x)/(2*b^(2/3)*d^(1/3))-(3*Log(-1+(b^(1/3)*(c+d*x)^(1/3))/(d^(1/3)*(a+b*x)^(1/3))))/(2*b^(2/3)*d^(1/3))");

	}

	// {1/((a+b*x)^(5/3)*(c+d*x)^(1/3)), x, 1, (-3*(c+d*x)^(2/3))/(2*(b*c-a*d)*(a+b*x)^(2/3))}
	public void test00466() {
		check("Integrate(1/((a+b*x)^(5/3)*(c+d*x)^(1/3)), x)", "(-3*(c+d*x)^(2/3))/(2*(b*c-a*d)*(a+b*x)^(2/3))");

	}

	// {1/((a+b*x)^(1/3)*(c+d*x)^(2/3)), x, 1, -((Sqrt(3)*ArcTan(1/Sqrt(3)+(2*d^(1/3)*(a +
	// b*x)^(1/3))/(Sqrt(3)*b^(1/3)*(c+d*x)^(1/3))))/(b^(1/3)*d^(2/3)))-Log(c+d*x)/(2*b^(1/3)*d^(2/3))-(3*Log(-1
	// +(d^(1/3)*(a+b*x)^(1/3))/(b^(1/3)*(c+d*x)^(1/3))))/(2*b^(1/3)*d^(2/3))}
	public void test00467() {
		check("Integrate(1/((a+b*x)^(1/3)*(c+d*x)^(2/3)), x)",
				"-((Sqrt(3)*ArcTan(1/Sqrt(3)+(2*d^(1/3)*(a+b*x)^(1/3))/(Sqrt(3)*b^(1/3)*(c+d*x)^(1/3))))/(b^(1/3)*d^(2/3)))-Log(c+d*x)/(2*b^(1/3)*d^(2/3))-(3*Log(-1+(d^(1/3)*(a+b*x)^(1/3))/(b^(1/3)*(c+d*x)^(1/3))))/(2*b^(1/3)*d^(2/3))");

	}

	// {1/((a+b*x)^(4/3)*(c+d*x)^(2/3)), x, 1, (-3*(c+d*x)^(1/3))/((b*c-a*d)*(a+b*x)^(1/3))}
	public void test00468() {
		check("Integrate(1/((a+b*x)^(4/3)*(c+d*x)^(2/3)), x)", "(-3*(c+d*x)^(1/3))/((b*c-a*d)*(a+b*x)^(1/3))");

	}

	// {1/((a+b*x)^(2/3)*(c+d*x)^(4/3)), x, 1, (3*(a+b*x)^(1/3))/((b*c-a*d)*(c+d*x)^(1/3))}
	public void test00469() {
		check("Integrate(1/((a+b*x)^(2/3)*(c+d*x)^(4/3)), x)", "(3*(a+b*x)^(1/3))/((b*c-a*d)*(c+d*x)^(1/3))");

	}

	// {(c+d*x)^(5/4)/(a+b*x)^(13/4), x, 1, (-4*(c+d*x)^(9/4))/(9*(b*c-a*d)*(a+b*x)^(9/4))}
	public void test00470() {
		check("Integrate((c+d*x)^(5/4)/(a+b*x)^(13/4), x)", "(-4*(c+d*x)^(9/4))/(9*(b*c-a*d)*(a+b*x)^(9/4))");

	}

	// {1/((a+b*x)^(7/4)*(c+d*x)^(1/4)), x, 1, (-4*(c+d*x)^(3/4))/(3*(b*c-a*d)*(a+b*x)^(3/4))}
	public void test00471() {
		check("Integrate(1/((a+b*x)^(7/4)*(c+d*x)^(1/4)), x)", "(-4*(c+d*x)^(3/4))/(3*(b*c-a*d)*(a+b*x)^(3/4))");

	}

	// {1/((a+b*x)^(5/4)*(c+d*x)^(3/4)), x, 1, (-4*(c+d*x)^(1/4))/((b*c-a*d)*(a+b*x)^(1/4))}
	public void test00472() {
		check("Integrate(1/((a+b*x)^(5/4)*(c+d*x)^(3/4)), x)", "(-4*(c+d*x)^(1/4))/((b*c-a*d)*(a+b*x)^(1/4))");

	}

	// {1/((a+b*x)^(3/4)*(c+d*x)^(5/4)), x, 1, (4*(a+b*x)^(1/4))/((b*c-a*d)*(c+d*x)^(1/4))}
	public void test00473() {
		check("Integrate(1/((a+b*x)^(3/4)*(c+d*x)^(5/4)), x)", "(4*(a+b*x)^(1/4))/((b*c-a*d)*(c+d*x)^(1/4))");

	}

	// {(a+b*x)^(1/6)/(c+d*x)^(13/6), x, 1, (6*(a+b*x)^(7/6))/(7*(b*c-a*d)*(c+d*x)^(7/6))}
	public void test00474() {
		check("Integrate((a+b*x)^(1/6)/(c+d*x)^(13/6), x)", "(6*(a+b*x)^(7/6))/(7*(b*c-a*d)*(c+d*x)^(7/6))");

	}

	// {(a+b*x)^(5/6)/(c+d*x)^(17/6), x, 1, (6*(a+b*x)^(11/6))/(11*(b*c-a*d)*(c+d*x)^(11/6))}
	public void test00475() {
		check("Integrate((a+b*x)^(5/6)/(c+d*x)^(17/6), x)", "(6*(a+b*x)^(11/6))/(11*(b*c-a*d)*(c+d*x)^(11/6))");

	}

	// {(a+b*x)^(7/6)/(c+d*x)^(19/6), x, 1, (6*(a+b*x)^(13/6))/(13*(b*c-a*d)*(c+d*x)^(13/6))}
	public void test00476() {
		check("Integrate((a+b*x)^(7/6)/(c+d*x)^(19/6), x)", "(6*(a+b*x)^(13/6))/(13*(b*c-a*d)*(c+d*x)^(13/6))");

	}

	// {1/((a+b*x)^(1/6)*(c+d*x)^(11/6)), x, 1, (6*(a+b*x)^(5/6))/(5*(b*c-a*d)*(c+d*x)^(5/6))}
	public void test00477() {
		check("Integrate(1/((a+b*x)^(1/6)*(c+d*x)^(11/6)), x)", "(6*(a+b*x)^(5/6))/(5*(b*c-a*d)*(c+d*x)^(5/6))");

	}

	// {1/((a+b*x)^(5/6)*(c+d*x)^(7/6)), x, 1, (6*(a+b*x)^(1/6))/((b*c-a*d)*(c+d*x)^(1/6))}
	public void test00478() {
		check("Integrate(1/((a+b*x)^(5/6)*(c+d*x)^(7/6)), x)", "(6*(a+b*x)^(1/6))/((b*c-a*d)*(c+d*x)^(1/6))");

	}

	// {1/((a+b*x)^(7/6)*(c+d*x)^(5/6)), x, 1, (-6*(c+d*x)^(1/6))/((b*c-a*d)*(a+b*x)^(1/6))}
	public void test00479() {
		check("Integrate(1/((a+b*x)^(7/6)*(c+d*x)^(5/6)), x)", "(-6*(c+d*x)^(1/6))/((b*c-a*d)*(a+b*x)^(1/6))");

	}

	// {(a+b*x)^m/(c+d*x), x, 1, ((a+b*x)^(1+m)*Hypergeometric2F1(1, 1+m, 2+m, -((d*(a+b*x))/(b*c -
	// a*d))))/((b*c-a*d)*(1+m))}
	public void test00480() {
		check("Integrate((a+b*x)^m/(c+d*x), x)",
				"((a+b*x)^(1+m)*Hypergeometric2F1(1, 1+m, 2+m, -((d*(a+b*x))/(b*c-a*d))))/((b*c-a*d)*(1+m))");

	}

	// {(a+b*x)^m/(c+d*x)^2, x, 1, (b*(a+b*x)^(1+m)*Hypergeometric2F1(2, 1+m, 2+m, -((d*(a+b*x))/(b*c -
	// a*d))))/((b*c-a*d)^2*(1+m))}
	public void test00481() {
		check("Integrate((a+b*x)^m/(c+d*x)^2, x)",
				"(b*(a+b*x)^(1+m)*Hypergeometric2F1(2, 1+m, 2+m, -((d*(a+b*x))/(b*c-a*d))))/((b*c-a*d)^2*(1+m))");

	}

	// {(a+b*x)^m/(c+d*x)^3, x, 1, (b^2*(a+b*x)^(1+m)*Hypergeometric2F1(3, 1+m, 2+m, -((d*(a+b*x))/(b*c -
	// a*d))))/((b*c-a*d)^3*(1+m))}
	public void test00482() {
		check("Integrate((a+b*x)^m/(c+d*x)^3, x)",
				"(b^2*(a+b*x)^(1+m)*Hypergeometric2F1(3, 1+m, 2+m, -((d*(a+b*x))/(b*c-a*d))))/((b*c-a*d)^3*(1+m))");

	}

	// {(c+d*x)^n, x, 1, (c+d*x)^(1+n)/(d*(1+n))}
	public void test00483() {
		check("Integrate((c+d*x)^n, x)", "(c+d*x)^(1+n)/(d*(1+n))");

	}

	// {(c+d*x)^n/(a+b*x), x, 1, -(((c+d*x)^(1+n)*Hypergeometric2F1(1, 1+n, 2+n, (b*(c+d*x))/(b*c -
	// a*d)))/((b*c-a*d)*(1+n)))}
	public void test00484() {
		check("Integrate((c+d*x)^n/(a+b*x), x)",
				"-(((c+d*x)^(1+n)*Hypergeometric2F1(1, 1+n, 2+n, (b*(c+d*x))/(b*c-a*d)))/((b*c-a*d)*(1+n)))");

	}

	// {(c+d*x)^n/(a+b*x)^2, x, 1, (d*(c+d*x)^(1+n)*Hypergeometric2F1(2, 1+n, 2+n, (b*(c+d*x))/(b*c -
	// a*d)))/((b*c-a*d)^2*(1+n))}
	public void test00485() {
		check("Integrate((c+d*x)^n/(a+b*x)^2, x)",
				"(d*(c+d*x)^(1+n)*Hypergeometric2F1(2, 1+n, 2+n, (b*(c+d*x))/(b*c-a*d)))/((b*c-a*d)^2*(1+n))");

	}

	// {(c+d*x)^n/(a+b*x)^3, x, 1, -((d^2*(c+d*x)^(1+n)*Hypergeometric2F1(3, 1+n, 2+n, (b*(c+d*x))/(b*c -
	// a*d)))/((b*c-a*d)^3*(1+n)))}
	public void test00486() {
		check("Integrate((c+d*x)^n/(a+b*x)^3, x)",
				"-((d^2*(c+d*x)^(1+n)*Hypergeometric2F1(3, 1+n, 2+n, (b*(c+d*x))/(b*c-a*d)))/((b*c-a*d)^3*(1+n)))");

	}

	// {(a+b*x)^(-2+n)/(c+d*x)^n, x, 1, -(((a+b*x)^(-1+n)*(c+d*x)^(1-n))/((b*c-a*d)*(1-n)))}
	public void test00487() {
		check("Integrate((a+b*x)^(-2+n)/(c+d*x)^n, x)", "-(((a+b*x)^(-1+n)*(c+d*x)^(1-n))/((b*c-a*d)*(1-n)))");

	}

	// {(a+b*x)^(-2-n)*(c+d*x)^n, x, 1, -(((a+b*x)^(-1-n)*(c+d*x)^(1+n))/((b*c-a*d)*(1+n)))}
	public void test00488() {
		check("Integrate((a+b*x)^(-2-n)*(c+d*x)^n, x)", "-(((a+b*x)^(-1-n)*(c+d*x)^(1+n))/((b*c-a*d)*(1+n)))");

	}

	// {(a+b*x)^n*(c+d*x)^(-2-n), x, 1, ((a+b*x)^(1+n)*(c+d*x)^(-1-n))/((b*c-a*d)*(1+n))}
	public void test00489() {
		check("Integrate((a+b*x)^n*(c+d*x)^(-2-n), x)", "((a+b*x)^(1+n)*(c+d*x)^(-1-n))/((b*c-a*d)*(1+n))");

	}

	// {(1-x)^n/Sqrt(1+x), x, 1, 2^(1+n)*Sqrt(1+x)*Hypergeometric2F1(1/2, -n, 3/2, (1+x)/2)}
	public void test00490() {
		check("Integrate((1-x)^n/Sqrt(1+x), x)", "2^(1+n)*Sqrt(1+x)*Hypergeometric2F1(1/2, -n, 3/2, (1+x)/2)");

	}

	// {(1+x)^n/Sqrt(1-x), x, 1, -(2^(1+n)*Sqrt(1-x)*Hypergeometric2F1(1/2, -n, 3/2, (1-x)/2))}
	public void test00491() {
		check("Integrate((1+x)^n/Sqrt(1-x), x)", "-(2^(1+n)*Sqrt(1-x)*Hypergeometric2F1(1/2, -n, 3/2, (1-x)/2))");

	}

	// {(1-x)^n*(1+x)^(7/3), x, 1, (3*2^(-1+n)*(1+x)^(10/3)*Hypergeometric2F1(10/3, -n, 13/3, (1+x)/2))/5}
	public void test00492() {
		check("Integrate((1-x)^n*(1+x)^(7/3), x)",
				"(3*2^(-1+n)*(1+x)^(10/3)*Hypergeometric2F1(10/3, -n, 13/3, (1+x)/2))/5");

	}

	// {(1-x)^(7/3)*(1+x)^n, x, 1, (-3*2^(-1+n)*(1-x)^(10/3)*Hypergeometric2F1(10/3, -n, 13/3, (1-x)/2))/5}
	public void test00493() {
		check("Integrate((1-x)^(7/3)*(1+x)^n, x)",
				"(-3*2^(-1+n)*(1-x)^(10/3)*Hypergeometric2F1(10/3, -n, 13/3, (1-x)/2))/5");

	}

	// {(2+3*x)^m/(1+2*x)^m, x, 1, (2^(-1-m)*(1+2*x)^(1-m)*Hypergeometric2F1(1-m, -m, 2-m, -3*(1 +
	// 2*x)))/(1-m)}
	public void test00494() {
		check("Integrate((2+3*x)^m/(1+2*x)^m, x)",
				"(2^(-1-m)*(1+2*x)^(1-m)*Hypergeometric2F1(1-m, -m, 2-m, -3*(1+2*x)))/(1-m)");

	}

	// {a+b*x+c*x^2+d*x^3, x, 1, a*x+(b*x^2)/2+(c*x^3)/3+(d*x^4)/4}
	public void test00495() {
		check("Integrate(a+b*x+c*x^2+d*x^3, x)", "a*x+(b*x^2)/2+(c*x^3)/3+(d*x^4)/4");

	}

	// {-x^3+x^4, x, 1, -x^4/4+x^5/5}
	public void test00496() {
		check("Integrate(-x^3+x^4, x)", "-x^4/4+x^5/5");

	}

	// {-1+x^5, x, 1, -x+x^6/6}
	public void test00497() {
		check("Integrate(-1+x^5, x)", "-x+x^6/6");

	}

	// {7+4*x, x, 1, 7*x+2*x^2}
	public void test00498() {
		check("Integrate(7+4*x, x)", "7*x+2*x^2");

	}

	// {4*x+Pi*x^3, x, 1, 2*x^2+(Pi*x^4)/4}
	public void test00499() {
		check("Integrate(4*x+Pi*x^3, x)", "2*x^2+(Pi*x^4)/4");

	}

	// {2*x+5*x^2, x, 1, x^2+(5*x^3)/3}
	public void test00500() {
		check("Integrate(2*x+5*x^2, x)", "x^2+(5*x^3)/3");

	}

	// {x^2/2+x^3/3, x, 1, x^3/6+x^4/12}
	public void test00501() {
		check("Integrate(x^2/2+x^3/3, x)", "x^3/6+x^4/12");

	}

	// {3-5*x+2*x^2, x, 1, 3*x-(5*x^2)/2+(2*x^3)/3}
	public void test00502() {
		check("Integrate(3-5*x+2*x^2, x)", "3*x-(5*x^2)/2+(2*x^3)/3");

	}

	// {-2*x+x^2+x^3, x, 1, -x^2+x^3/3+x^4/4}
	public void test00503() {
		check("Integrate(-2*x+x^2+x^3, x)", "-x^2+x^3/3+x^4/4");

	}

	// {1-x^2-3*x^5, x, 1, x-x^3/3-x^6/2}
	public void test00504() {
		check("Integrate(1-x^2-3*x^5, x)", "x-x^3/3-x^6/2");

	}

	// {5+2*x+3*x^2+4*x^3, x, 1, 5*x+x^2+x^3+x^4}
	public void test00505() {
		check("Integrate(5+2*x+3*x^2+4*x^3, x)", "5*x+x^2+x^3+x^4");

	}

	// {a+d/x^3+c/x^2+b/x, x, 1, -d/(2*x^2)-c/x+a*x+b*Log(x)}
	public void test00506() {
		check("Integrate(a+d/x^3+c/x^2+b/x, x)", "-d/(2*x^2)-c/x+a*x+b*Log(x)");

	}

	// {x^(-5)+x+x^5, x, 1, -1/(4*x^4)+x^2/2+x^6/6}
	public void test00507() {
		check("Integrate(x^(-5)+x+x^5, x)", "-1/(4*x^4)+x^2/2+x^6/6");

	}

	// {x^(-3)+x^(-2)+x^(-1), x, 1, -1/(2*x^2)-x^(-1)+Log(x)}
	public void test00508() {
		check("Integrate(x^(-3)+x^(-2)+x^(-1), x)", "-1/(2*x^2)-x^(-1)+Log(x)");

	}

	// {-2/x^2+3/x, x, 1, 2/x+3*Log(x)}
	public void test00509() {
		check("Integrate(-2/x^2+3/x, x)", "2/x+3*Log(x)");

	}

	// {-1/(7*x^6)+x^6, x, 1, 1/(35*x^5)+x^7/7}
	public void test00510() {
		check("Integrate(-1/(7*x^6)+x^6, x)", "1/(35*x^5)+x^7/7");

	}

	// {1+x^(-1)+x, x, 1, x+x^2/2+Log(x)}
	public void test00511() {
		check("Integrate(1+x^(-1)+x, x)", "x+x^2/2+Log(x)");

	}

	// {-3/x^3+4/x^2, x, 1, 3/(2*x^2)-4/x}
	public void test00512() {
		check("Integrate(-3/x^3+4/x^2, x)", "3/(2*x^2)-4/x");

	}

	// {x^(-1)+2*x+x^2, x, 1, x^2+x^3/3+Log(x)}
	public void test00513() {
		check("Integrate(x^(-1)+2*x+x^2, x)", "x^2+x^3/3+Log(x)");

	}

	// {x^(5/6)-x^3, x, 1, (6*x^(11/6))/11-x^4/4}
	public void test00514() {
		check("Integrate(x^(5/6)-x^3, x)", "(6*x^(11/6))/11-x^4/4");

	}

	// {33+x^(1/33), x, 1, 33*x+(33*x^(34/33))/34}
	public void test00515() {
		check("Integrate(33+x^(1/33), x)", "33*x+(33*x^(34/33))/34");

	}

	// {1/(2*Sqrt(x))+2*Sqrt(x), x, 1, Sqrt(x)+(4*x^(3/2))/3}
	public void test00516() {
		check("Integrate(1/(2*Sqrt(x))+2*Sqrt(x), x)", "Sqrt(x)+(4*x^(3/2))/3");

	}

	// {-x^(-2)+10/x+6*Sqrt(x), x, 1, x^(-1)+4*x^(3/2)+10*Log(x)}
	public void test00517() {
		check("Integrate(-x^(-2)+10/x+6*Sqrt(x), x)", "x^(-1)+4*x^(3/2)+10*Log(x)");

	}

	// {x^(-3/2)+x^(3/2), x, 1, -2/Sqrt(x)+(2*x^(5/2))/5}
	public void test00518() {
		check("Integrate(x^(-3/2)+x^(3/2), x)", "-2/Sqrt(x)+(2*x^(5/2))/5");

	}

	// {-5*x^(3/2)+7*x^(5/2), x, 1, -2*x^(5/2)+2*x^(7/2)}
	public void test00519() {
		check("Integrate(-5*x^(3/2)+7*x^(5/2), x)", "-2*x^(5/2)+2*x^(7/2)");

	}

	// {2/Sqrt(x)+Sqrt(x)-x/2, x, 1, 4*Sqrt(x)+(2*x^(3/2))/3-x^2/4}
	public void test00520() {
		check("Integrate(2/Sqrt(x)+Sqrt(x)-x/2, x)", "4*Sqrt(x)+(2*x^(3/2))/3-x^2/4");

	}

	// {-2/x+Sqrt(x)/5+x^(3/2), x, 1, (2*x^(3/2))/15+(2*x^(5/2))/5-2*Log(x)}
	public void test00521() {
		check("Integrate(-2/x+Sqrt(x)/5+x^(3/2), x)", "(2*x^(3/2))/15+(2*x^(5/2))/5-2*Log(x)");

	}

	// {((a+b*x)*(a*c-b*c*x)^3)/x^3, x, 1, -(c^3*(a-b*x)^4)/(2*x^2)}
	public void test00522() {
		check("Integrate(((a+b*x)*(a*c-b*c*x)^3)/x^3, x)", "-(c^3*(a-b*x)^4)/(2*x^2)");

	}

	// {((a+b*x)*(a*c-b*c*x)^5)/x^4, x, 1, -(c^5*(a-b*x)^6)/(3*x^3)}
	public void test00523() {
		check("Integrate(((a+b*x)*(a*c-b*c*x)^5)/x^4, x)", "-(c^5*(a-b*x)^6)/(3*x^3)");

	}

	// {x^2*(2+x)^5*(2+3*x), x, 1, (x^3*(2+x)^6)/3}
	public void test00524() {
		check("Integrate(x^2*(2+x)^5*(2+3*x), x)", "(x^3*(2+x)^6)/3");

	}

	// {(A+B*x)/(a+b*x)^3, x, 1, -(A+B*x)^2/(2*(A*b-a*B)*(a+b*x)^2)}
	public void test00525() {
		check("Integrate((A+B*x)/(a+b*x)^3, x)", "-(A+B*x)^2/(2*(A*b-a*B)*(a+b*x)^2)");

	}

	// {Sqrt(a+b*x)/(c+d*x)^(5/2), x, 1, (2*(a+b*x)^(3/2))/(3*(b*c-a*d)*(c+d*x)^(3/2))}
	public void test00526() {
		check("Integrate(Sqrt(a+b*x)/(c+d*x)^(5/2), x)", "(2*(a+b*x)^(3/2))/(3*(b*c-a*d)*(c+d*x)^(3/2))");

	}

	// {1/(Sqrt(a+b*x)*(c+d*x)^(3/2)), x, 1, (2*Sqrt(a+b*x))/((b*c-a*d)*Sqrt(c+d*x))}
	public void test00527() {
		check("Integrate(1/(Sqrt(a+b*x)*(c+d*x)^(3/2)), x)", "(2*Sqrt(a+b*x))/((b*c-a*d)*Sqrt(c+d*x))");

	}

	// {Sqrt(1+x)/(1-x)^(5/2), x, 1, (1+x)^(3/2)/(3*(1-x)^(3/2))}
	public void test00528() {
		check("Integrate(Sqrt(1+x)/(1-x)^(5/2), x)", "(1+x)^(3/2)/(3*(1-x)^(3/2))");

	}

	// {Sqrt(1+x)/(-1+x)^(5/2), x, 1, -(1+x)^(3/2)/(3*(-1+x)^(3/2))}
	public void test00529() {
		check("Integrate(Sqrt(1+x)/(-1+x)^(5/2), x)", "-(1+x)^(3/2)/(3*(-1+x)^(3/2))");

	}

	// {x/(Sqrt(-1+x)*Sqrt(1+x)), x, 1, Sqrt(-1+x)*Sqrt(1+x)}
	public void test00530() {
		check("Integrate(x/(Sqrt(-1+x)*Sqrt(1+x)), x)", "Sqrt(-1+x)*Sqrt(1+x)");

	}

	// {1/(Sqrt(-1+x)*Sqrt(1+x)), x, 1, ArcCosh(x)}
	public void test00531() {
		check("Integrate(1/(Sqrt(-1+x)*Sqrt(1+x)), x)", "ArcCosh(x)");

	}

	// {1/(Sqrt(-1+x)*x^2*Sqrt(1+x)), x, 1, (Sqrt(-1+x)*Sqrt(1+x))/x}
	public void test00532() {
		check("Integrate(1/(Sqrt(-1+x)*x^2*Sqrt(1+x)), x)", "(Sqrt(-1+x)*Sqrt(1+x))/x");

	}

	// {Sqrt(-1+x)*x*Sqrt(1+x), x, 1, ((-1+x)^(3/2)*(1+x)^(3/2))/3}
	public void test00533() {
		check("Integrate(Sqrt(-1+x)*x*Sqrt(1+x), x)", "((-1+x)^(3/2)*(1+x)^(3/2))/3");

	}

	// {Sqrt(1-x)*x*Sqrt(1+x), x, 1, -((1-x)^(3/2)*(1+x)^(3/2))/3}
	public void test00534() {
		check("Integrate(Sqrt(1-x)*x*Sqrt(1+x), x)", "-((1-x)^(3/2)*(1+x)^(3/2))/3");

	}

	// {1/(Sqrt(x)*Sqrt(2-b*x)*Sqrt(2+b*x)), x, 1, (Sqrt(2)*EllipticF(ArcSin((Sqrt(b)*Sqrt(x))/Sqrt(2)),
	// -1))/Sqrt(b)}
	public void test00535() {
		check("Integrate(1/(Sqrt(x)*Sqrt(2-b*x)*Sqrt(2+b*x)), x)",
				"(Sqrt(2)*EllipticF(ArcSin((Sqrt(b)*Sqrt(x))/Sqrt(2)), -1))/Sqrt(b)");

	}

	// {1/(Sqrt(-x)*Sqrt(2-b*x)*Sqrt(2+b*x)), x, 1, -((Sqrt(2)*EllipticF(ArcSin((Sqrt(b)*Sqrt(-x))/Sqrt(2)),
	// -1))/Sqrt(b))}
	public void test00536() {
		check("Integrate(1/(Sqrt(-x)*Sqrt(2-b*x)*Sqrt(2+b*x)), x)",
				"-((Sqrt(2)*EllipticF(ArcSin((Sqrt(b)*Sqrt(-x))/Sqrt(2)), -1))/Sqrt(b))");

	}

	// {1/(Sqrt(e*x)*Sqrt(2-b*x)*Sqrt(2+b*x)), x, 1,
	// (Sqrt(2)*EllipticF(ArcSin((Sqrt(b)*Sqrt(e*x))/(Sqrt(2)*Sqrt(e))), -1))/(Sqrt(b)*Sqrt(e))}
	public void test00537() {
		check("Integrate(1/(Sqrt(e*x)*Sqrt(2-b*x)*Sqrt(2+b*x)), x)",
				"(Sqrt(2)*EllipticF(ArcSin((Sqrt(b)*Sqrt(e*x))/(Sqrt(2)*Sqrt(e))), -1))/(Sqrt(b)*Sqrt(e))");

	}

	// {1/(Sqrt(2-3*x)*Sqrt(x)*Sqrt(2+3*x)), x, 1, Sqrt(2/3)*EllipticF(ArcSin(Sqrt(3/2)*Sqrt(x)), -1)}
	public void test00538() {
		check("Integrate(1/(Sqrt(2-3*x)*Sqrt(x)*Sqrt(2+3*x)), x)",
				"Sqrt(2/3)*EllipticF(ArcSin(Sqrt(3/2)*Sqrt(x)), -1)");

	}

	// {1/(Sqrt(2-3*x)*Sqrt(-x)*Sqrt(2+3*x)), x, 1, -(Sqrt(2/3)*EllipticF(ArcSin(Sqrt(3/2)*Sqrt(-x)), -1))}
	public void test00539() {
		check("Integrate(1/(Sqrt(2-3*x)*Sqrt(-x)*Sqrt(2+3*x)), x)",
				"-(Sqrt(2/3)*EllipticF(ArcSin(Sqrt(3/2)*Sqrt(-x)), -1))");

	}

	// {1/(Sqrt(2-3*x)*Sqrt(e*x)*Sqrt(2+3*x)), x, 1, (Sqrt(2/3)*EllipticF(ArcSin((Sqrt(3/2)*Sqrt(e*x))/Sqrt(e)),
	// -1))/Sqrt(e)}
	public void test00540() {
		check("Integrate(1/(Sqrt(2-3*x)*Sqrt(e*x)*Sqrt(2+3*x)), x)",
				"(Sqrt(2/3)*EllipticF(ArcSin((Sqrt(3/2)*Sqrt(e*x))/Sqrt(e)), -1))/Sqrt(e)");

	}

	// {1/(Sqrt(1-x)*Sqrt(x)*Sqrt(1+x)), x, 1, 2*EllipticF(ArcSin(Sqrt(x)), -1)}
	public void test00541() {
		check("Integrate(1/(Sqrt(1-x)*Sqrt(x)*Sqrt(1+x)), x)", "2*EllipticF(ArcSin(Sqrt(x)), -1)");

	}

	// {1/(Sqrt(b*x)*Sqrt(1-c*x)*Sqrt(1+c*x)), x, 1, (2*EllipticF(ArcSin((Sqrt(c)*Sqrt(b*x))/Sqrt(b)),
	// -1))/(Sqrt(b)*Sqrt(c))}
	public void test00542() {
		check("Integrate(1/(Sqrt(b*x)*Sqrt(1-c*x)*Sqrt(1+c*x)), x)",
				"(2*EllipticF(ArcSin((Sqrt(c)*Sqrt(b*x))/Sqrt(b)), -1))/(Sqrt(b)*Sqrt(c))");

	}

	// {1/(Sqrt(b*x)*Sqrt(1-c*x)*Sqrt(1+d*x)), x, 1, (2*EllipticF(ArcSin((Sqrt(c)*Sqrt(b*x))/Sqrt(b)),
	// -(d/c)))/(Sqrt(b)*Sqrt(c))}
	public void test00543() {
		check("Integrate(1/(Sqrt(b*x)*Sqrt(1-c*x)*Sqrt(1+d*x)), x)",
				"(2*EllipticF(ArcSin((Sqrt(c)*Sqrt(b*x))/Sqrt(b)), -(d/c)))/(Sqrt(b)*Sqrt(c))");

	}

	// {Sqrt(1+x)/(Sqrt(1-x)*Sqrt(x)), x, 1, 2*EllipticE(ArcSin(Sqrt(x)), -1)}
	public void test00544() {
		check("Integrate(Sqrt(1+x)/(Sqrt(1-x)*Sqrt(x)), x)", "2*EllipticE(ArcSin(Sqrt(x)), -1)");

	}

	// {Sqrt(1+c*x)/(Sqrt(b*x)*Sqrt(1-c*x)), x, 1, (2*EllipticE(ArcSin((Sqrt(c)*Sqrt(b*x))/Sqrt(b)),
	// -1))/(Sqrt(b)*Sqrt(c))}
	public void test00545() {
		check("Integrate(Sqrt(1+c*x)/(Sqrt(b*x)*Sqrt(1-c*x)), x)",
				"(2*EllipticE(ArcSin((Sqrt(c)*Sqrt(b*x))/Sqrt(b)), -1))/(Sqrt(b)*Sqrt(c))");

	}

	// {Sqrt(1+c*x)/(Sqrt(b*x)*Sqrt(1-d*x)), x, 1, (2*EllipticE(ArcSin((Sqrt(d)*Sqrt(b*x))/Sqrt(b)),
	// -(c/d)))/(Sqrt(b)*Sqrt(d))}
	public void test00546() {
		check("Integrate(Sqrt(1+c*x)/(Sqrt(b*x)*Sqrt(1-d*x)), x)",
				"(2*EllipticE(ArcSin((Sqrt(d)*Sqrt(b*x))/Sqrt(b)), -(c/d)))/(Sqrt(b)*Sqrt(d))");

	}

	// {Sqrt(1-c*x)/(Sqrt(b*x)*Sqrt(1+c*x)), x, 1, (-2*EllipticE(ArcSin((Sqrt(c)*Sqrt(b*x))/Sqrt(-b)),
	// -1))/(Sqrt(-b)*Sqrt(c))}
	public void test00547() {
		check("Integrate(Sqrt(1-c*x)/(Sqrt(b*x)*Sqrt(1+c*x)), x)",
				"(-2*EllipticE(ArcSin((Sqrt(c)*Sqrt(b*x))/Sqrt(-b)), -1))/(Sqrt(-b)*Sqrt(c))");

	}

	// {Sqrt(1-c*x)/(Sqrt(b*x)*Sqrt(1+d*x)), x, 1, (-2*EllipticE(ArcSin((Sqrt(d)*Sqrt(b*x))/Sqrt(-b)),
	// -(c/d)))/(Sqrt(-b)*Sqrt(d))}
	public void test00548() {
		check("Integrate(Sqrt(1-c*x)/(Sqrt(b*x)*Sqrt(1+d*x)), x)",
				"(-2*EllipticE(ArcSin((Sqrt(d)*Sqrt(b*x))/Sqrt(-b)), -(c/d)))/(Sqrt(-b)*Sqrt(d))");

	}

	// {1/((1-x)^(1/3)*(2-x)^(1/3)*x), x, 1, -(Sqrt(3)*ArcTan(1/Sqrt(3)+(2^(1/3)*(2-x)^(2/3))/(Sqrt(3)*(1 -
	// x)^(1/3))))/(2*2^(1/3))+(3*Log(-(1-x)^(1/3)+(2-x)^(2/3)/2^(2/3)))/(4*2^(1/3))-Log(x)/(2*2^(1/3))}
	public void test00549() {
		check("Integrate(1/((1-x)^(1/3)*(2-x)^(1/3)*x), x)",
				"-(Sqrt(3)*ArcTan(1/Sqrt(3)+(2^(1/3)*(2-x)^(2/3))/(Sqrt(3)*(1-x)^(1/3))))/(2*2^(1/3))+(3*Log(-(1-x)^(1/3)+(2-x)^(2/3)/2^(2/3)))/(4*2^(1/3))-Log(x)/(2*2^(1/3))");

	}

	// {1/((1-x)^(1/4)*(e*x)^(5/2)*(1+x)^(1/4)), x, 1, (-2*(1-x)^(3/4)*(1+x)^(3/4))/(3*e*(e*x)^(3/2))}
	public void test00550() {
		check("Integrate(1/((1-x)^(1/4)*(e*x)^(5/2)*(1+x)^(1/4)), x)",
				"(-2*(1-x)^(3/4)*(1+x)^(3/4))/(3*e*(e*x)^(3/2))");

	}

	// {x^(1+2*n)*(a+b*x)^n*(2*a+3*b*x), x, 1, (x^(2*(1+n))*(a+b*x)^(1+n))/(1+n)}
	public void test00551() {
		check("Integrate(x^(1+2*n)*(a+b*x)^n*(2*a+3*b*x), x)", "(x^(2*(1+n))*(a+b*x)^(1+n))/(1+n)");

	}

	// {(a+b*x)^n/(c+d*x), x, 1, ((a+b*x)^(1+n)*Hypergeometric2F1(1, 1+n, 2+n, -((d*(a+b*x))/(b*c -
	// a*d))))/((b*c-a*d)*(1+n))}
	public void test00552() {
		check("Integrate((a+b*x)^n/(c+d*x), x)",
				"((a+b*x)^(1+n)*Hypergeometric2F1(1, 1+n, 2+n, -((d*(a+b*x))/(b*c-a*d))))/((b*c-a*d)*(1+n))");

	}

	// {(a+b*x)^n/(c+d*x)^2, x, 1, (b*(a+b*x)^(1+n)*Hypergeometric2F1(2, 1+n, 2+n, -((d*(a+b*x))/(b*c -
	// a*d))))/((b*c-a*d)^2*(1+n))}
	public void test00553() {
		check("Integrate((a+b*x)^n/(c+d*x)^2, x)",
				"(b*(a+b*x)^(1+n)*Hypergeometric2F1(2, 1+n, 2+n, -((d*(a+b*x))/(b*c-a*d))))/((b*c-a*d)^2*(1+n))");

	}

	// {(b*x)^m*(Pi+d*x)^n*(E+f*x)^p, x, 1, (E^p*Pi^n*(b*x)^(1+m)*AppellF1(1+m, -n, -p, 2+m, -((d*x)/Pi),
	// -((f*x)/E)))/(b*(1+m))}
	public void test00554() {
		check("Integrate((b*x)^m*(Pi+d*x)^n*(E+f*x)^p, x)",
				"(E^p*Pi^n*(b*x)^(1+m)*AppellF1(1+m, -n, -p, 2+m, -((d*x)/Pi), -((f*x)/E)))/(b*(1+m))");

	}

	// {(b*x)^(5/2)*(Pi+d*x)^n*(E+f*x)^p, x, 1, (2*E^p*Pi^n*(b*x)^(7/2)*AppellF1(7/2, -n, -p, 9/2, -((d*x)/Pi),
	// -((f*x)/E)))/(7*b)}
	public void test00555() {
		check("Integrate((b*x)^(5/2)*(Pi+d*x)^n*(E+f*x)^p, x)",
				"(2*E^p*Pi^n*(b*x)^(7/2)*AppellF1(7/2, -n, -p, 9/2, -((d*x)/Pi), -((f*x)/E)))/(7*b)");

	}

	// {(a+b*x)^n/(x^2*(c+d*x)^n), x, 1, ((b*c-a*d)*(a+b*x)^(1+n)*(c+d*x)^(-1-n)*Hypergeometric2F1(2, 1 +
	// n, 2+n, (c*(a+b*x))/(a*(c+d*x))))/(a^2*(1+n))}
	public void test00556() {
		check("Integrate((a+b*x)^n/(x^2*(c+d*x)^n), x)",
				"((b*c-a*d)*(a+b*x)^(1+n)*(c+d*x)^(-1-n)*Hypergeometric2F1(2, 1+n, 2+n, (c*(a+b*x))/(a*(c+d*x))))/(a^2*(1+n))");

	}

	// {(1-x)^n/(1+x)^n, x, 1, -(((1-x)^(1+n)*Hypergeometric2F1(n, 1+n, 2+n, (1-x)/2))/(2^n*(1+n)))}
	public void test00557() {
		check("Integrate((1-x)^n/(1+x)^n, x)", "-(((1-x)^(1+n)*Hypergeometric2F1(n, 1+n, 2+n, (1-x)/2))/(2^n*(1+n)))");

	}

	// {(1-x)^n/(x^2*(1+x)^n), x, 1, (-2*(1-x)^(1+n)*(1+x)^(-1-n)*Hypergeometric2F1(2, 1+n, 2+n, (1 -
	// x)/(1+x)))/(1+n)}
	public void test00558() {
		check("Integrate((1-x)^n/(x^2*(1+x)^n), x)",
				"(-2*(1-x)^(1+n)*(1+x)^(-1-n)*Hypergeometric2F1(2, 1+n, 2+n, (1-x)/(1+x)))/(1+n)");

	}

	// {((1-x)^(-1/2+p)*(1+x)^(1/2+p))/(c*x)^(2*(1+p)), x, 1, -((4^(1+p)*(1-x)^(1/2+p)*(x/(1+x))^(2*(1
	// +p))*(1+x)^(3/2+p)*Hypergeometric2F1(1/2+p, 2*(1+p), 3/2+p, (1-x)/(1+x)))/((1+2*p)*(c*x)^(2*(1 +
	// p))))}
	public void test00559() {
		check("Integrate(((1-x)^(-1/2+p)*(1+x)^(1/2+p))/(c*x)^(2*(1+p)), x)",
				"-((4^(1+p)*(1-x)^(1/2+p)*(x/(1+x))^(2*(1+p))*(1+x)^(3/2+p)*Hypergeometric2F1(1/2+p, 2*(1+p), 3/2+p, (1-x)/(1+x)))/((1+2*p)*(c*x)^(2*(1+p))))");

	}

	// {(1+x/a)^(n/2)/(x^2*(1-x/a)^(n/2)), x, 1, (-4*(1-x/a)^(1-n/2)*(1+x/a)^((-2+n)/2)*Hypergeometric2F1(2,
	// 1-n/2, 2-n/2, (a-x)/(a+x)))/(a*(2-n))}
	public void test00560() {
		check("Integrate((1+x/a)^(n/2)/(x^2*(1-x/a)^(n/2)), x)",
				"(-4*(1-x/a)^(1-n/2)*(1+x/a)^((-2+n)/2)*Hypergeometric2F1(2, 1-n/2, 2-n/2, (a-x)/(a+x)))/(a*(2-n))");

	}

	// {x^2/((1-a*x)^7*(1+a*x)^4), x, 1, -(1-3*a*x)/(24*a^3*(1-a*x)^6*(1+a*x)^3)}
	public void test00561() {
		check("Integrate(x^2/((1-a*x)^7*(1+a*x)^4), x)", "-(1-3*a*x)/(24*a^3*(1-a*x)^6*(1+a*x)^3)");

	}

	// {x^2/((1-a*x)^11*(1+a*x)^7), x, 1, -(1-4*a*x)/(60*a^3*(1-a*x)^10*(1+a*x)^6)}
	public void test00562() {
		check("Integrate(x^2/((1-a*x)^11*(1+a*x)^7), x)", "-(1-4*a*x)/(60*a^3*(1-a*x)^10*(1+a*x)^6)");

	}

	// {x^2/((1-a*x)^16*(1+a*x)^11), x, 1, -(1-5*a*x)/(120*a^3*(1-a*x)^15*(1+a*x)^10)}
	public void test00563() {
		check("Integrate(x^2/((1-a*x)^16*(1+a*x)^11), x)", "-(1-5*a*x)/(120*a^3*(1-a*x)^15*(1+a*x)^10)");

	}

	// {x^2*(1-a*x)^(-1-(n*(1+n))/2)*(1+a*x)^(-1-((-1+n)*n)/2), x, 1, ((1+a*x)^(((1-n)*n)/2)*(1 -
	// a*n*x))/(a^3*n*(1-n^2)*(1-a*x)^((n*(1+n))/2))}
	public void test00564() {
		check("Integrate(x^2*(1-a*x)^(-1-(n*(1+n))/2)*(1+a*x)^(-1-((-1+n)*n)/2), x)",
				"((1+a*x)^(((1-n)*n)/2)*(1-a*n*x))/(a^3*n*(1-n^2)*(1-a*x)^((n*(1+n))/2))");

	}

	// {(A+B*x)/(a+b*x)^3, x, 1, -(A+B*x)^2/(2*(A*b-a*B)*(a+b*x)^2)}
	public void test00565() {
		check("Integrate((A+B*x)/(a+b*x)^3, x)", "-(A+B*x)^2/(2*(A*b-a*B)*(a+b*x)^2)");

	}

	// {(5-2*x)^6*(2+3*x)^3*(-16+33*x), x, 1, -((5-2*x)^7*(2+3*x)^4)/2}
	public void test00566() {
		check("Integrate((5-2*x)^6*(2+3*x)^3*(-16+33*x), x)", "-((5-2*x)^7*(2+3*x)^4)/2");

	}

	// {(1-2*x)/(3+5*x)^3, x, 1, -(1-2*x)^2/(22*(3+5*x)^2)}
	public void test00567() {
		check("Integrate((1-2*x)/(3+5*x)^3, x)", "-(1-2*x)^2/(22*(3+5*x)^2)");

	}

	// {(3+5*x)/(1-2*x)^3, x, 1, (3+5*x)^2/(22*(1-2*x)^2)}
	public void test00568() {
		check("Integrate((3+5*x)/(1-2*x)^3, x)", "(3+5*x)^2/(22*(1-2*x)^2)");

	}

	// {Sqrt(1-2*x)/(3+5*x)^(5/2), x, 1, (-2*(1-2*x)^(3/2))/(33*(3+5*x)^(3/2))}
	public void test00569() {
		check("Integrate(Sqrt(1-2*x)/(3+5*x)^(5/2), x)", "(-2*(1-2*x)^(3/2))/(33*(3+5*x)^(3/2))");

	}

	// {1/(Sqrt(1-2*x)*(3+5*x)^(3/2)), x, 1, (-2*Sqrt(1-2*x))/(11*Sqrt(3+5*x))}
	public void test00570() {
		check("Integrate(1/(Sqrt(1-2*x)*(3+5*x)^(3/2)), x)", "(-2*Sqrt(1-2*x))/(11*Sqrt(3+5*x))");

	}

	// {1/((1-2*x)^(3/2)*Sqrt(3+5*x)), x, 1, (2*Sqrt(3+5*x))/(11*Sqrt(1-2*x))}
	public void test00571() {
		check("Integrate(1/((1-2*x)^(3/2)*Sqrt(3+5*x)), x)", "(2*Sqrt(3+5*x))/(11*Sqrt(1-2*x))");

	}

	// {Sqrt(3+5*x)/(1-2*x)^(5/2), x, 1, (2*(3+5*x)^(3/2))/(33*(1-2*x)^(3/2))}
	public void test00572() {
		check("Integrate(Sqrt(3+5*x)/(1-2*x)^(5/2), x)", "(2*(3+5*x)^(3/2))/(33*(1-2*x)^(3/2))");

	}

	// {1/(Sqrt(a+b*x)*Sqrt(c+(b*(-1+c)*x)/a)*Sqrt(e+(b*(-1+e)*x)/a)), x, 1,
	// (2*Sqrt(a)*EllipticF(ArcSin((Sqrt(1-c)*Sqrt(a+b*x))/Sqrt(a)), (1-e)/(1-c)))/(b*Sqrt(1-c))}
	public void test00573() {
		check("Integrate(1/(Sqrt(a+b*x)*Sqrt(c+(b*(-1+c)*x)/a)*Sqrt(e+(b*(-1+e)*x)/a)), x)",
				"(2*Sqrt(a)*EllipticF(ArcSin((Sqrt(1-c)*Sqrt(a+b*x))/Sqrt(a)), (1-e)/(1-c)))/(b*Sqrt(1-c))");

	}

	// {Sqrt(e+(b*(-1+e)*x)/a)/(Sqrt(a+b*x)*Sqrt(c+(b*(-1+c)*x)/a)), x, 1, (2*Sqrt(a)*EllipticE(ArcSin((Sqrt(1
	// -c)*Sqrt(a+b*x))/Sqrt(a)), (1-e)/(1-c)))/(b*Sqrt(1-c))}
	public void test00574() {
		check("Integrate(Sqrt(e+(b*(-1+e)*x)/a)/(Sqrt(a+b*x)*Sqrt(c+(b*(-1+c)*x)/a)), x)",
				"(2*Sqrt(a)*EllipticE(ArcSin((Sqrt(1-c)*Sqrt(a+b*x))/Sqrt(a)), (1-e)/(1-c)))/(b*Sqrt(1-c))");

	}

	// {Sqrt(1-2*x)/(Sqrt(-3-5*x)*Sqrt(2+3*x)), x, 1, (2*Sqrt(7/5)*EllipticE(ArcSin(Sqrt(5)*Sqrt(2+3*x)),
	// 2/35))/3}
	public void test00575() {
		check("Integrate(Sqrt(1-2*x)/(Sqrt(-3-5*x)*Sqrt(2+3*x)), x)",
				"(2*Sqrt(7/5)*EllipticE(ArcSin(Sqrt(5)*Sqrt(2+3*x)), 2/35))/3");

	}

	// {Sqrt(3+5*x)/(Sqrt(1-2*x)*Sqrt(2+3*x)), x, 1, -(Sqrt(11/3)*EllipticE(ArcSin(Sqrt(3/7)*Sqrt(1-2*x)),
	// 35/33))}
	public void test00576() {
		check("Integrate(Sqrt(3+5*x)/(Sqrt(1-2*x)*Sqrt(2+3*x)), x)",
				"-(Sqrt(11/3)*EllipticE(ArcSin(Sqrt(3/7)*Sqrt(1-2*x)), 35/33))");

	}

	// {1/(Sqrt(1+x)*Sqrt(2+x)*Sqrt(3+x)), x, 1, -2*EllipticF(ArcSin(1/Sqrt(3+x)), 2)}
	public void test00577() {
		check("Integrate(1/(Sqrt(1+x)*Sqrt(2+x)*Sqrt(3+x)), x)", "-2*EllipticF(ArcSin(1/Sqrt(3+x)), 2)");

	}

	// {1/(Sqrt(3-x)*Sqrt(1+x)*Sqrt(2+x)), x, 1, 2*EllipticF(ArcSin(Sqrt(1+x)/2), -4)}
	public void test00578() {
		check("Integrate(1/(Sqrt(3-x)*Sqrt(1+x)*Sqrt(2+x)), x)", "2*EllipticF(ArcSin(Sqrt(1+x)/2), -4)");

	}

	// {1/(Sqrt(2-x)*Sqrt(1+x)*Sqrt(3+x)), x, 1, Sqrt(2)*EllipticF(ArcSin(Sqrt(1+x)/Sqrt(3)), -3/2)}
	public void test00579() {
		check("Integrate(1/(Sqrt(2-x)*Sqrt(1+x)*Sqrt(3+x)), x)", "Sqrt(2)*EllipticF(ArcSin(Sqrt(1+x)/Sqrt(3)), -3/2)");

	}

	// {1/(Sqrt(2-x)*Sqrt(3-x)*Sqrt(1+x)), x, 1, EllipticF(ArcSin(Sqrt(1+x)/Sqrt(3)), 3/4)}
	public void test00580() {
		check("Integrate(1/(Sqrt(2-x)*Sqrt(3-x)*Sqrt(1+x)), x)", "EllipticF(ArcSin(Sqrt(1+x)/Sqrt(3)), 3/4)");

	}

	// {1/(Sqrt(1-x)*Sqrt(2+x)*Sqrt(3+x)), x, 1, 2*EllipticF(ArcSin(Sqrt(2+x)/Sqrt(3)), -3)}
	public void test00581() {
		check("Integrate(1/(Sqrt(1-x)*Sqrt(2+x)*Sqrt(3+x)), x)", "2*EllipticF(ArcSin(Sqrt(2+x)/Sqrt(3)), -3)");

	}

	// {1/(Sqrt(1-x)*Sqrt(3-x)*Sqrt(2+x)), x, 1, (2*EllipticF(ArcSin(Sqrt(2+x)/Sqrt(3)), 3/5))/Sqrt(5)}
	public void test00582() {
		check("Integrate(1/(Sqrt(1-x)*Sqrt(3-x)*Sqrt(2+x)), x)",
				"(2*EllipticF(ArcSin(Sqrt(2+x)/Sqrt(3)), 3/5))/Sqrt(5)");

	}

	// {1/(Sqrt(1-x)*Sqrt(2-x)*Sqrt(3+x)), x, 1, (2*EllipticF(ArcSin(Sqrt(3+x)/2), 4/5))/Sqrt(5)}
	public void test00583() {
		check("Integrate(1/(Sqrt(1-x)*Sqrt(2-x)*Sqrt(3+x)), x)", "(2*EllipticF(ArcSin(Sqrt(3+x)/2), 4/5))/Sqrt(5)");

	}

	// {1/(Sqrt(1-x)*Sqrt(2-x)*Sqrt(3-x)), x, 1, 2*EllipticF(ArcSin(1/Sqrt(3-x)), 2)}
	public void test00584() {
		check("Integrate(1/(Sqrt(1-x)*Sqrt(2-x)*Sqrt(3-x)), x)", "2*EllipticF(ArcSin(1/Sqrt(3-x)), 2)");

	}

	// {1/(Sqrt(-3+x)*Sqrt(-2+x)*Sqrt(-1+x)), x, 1, -2*EllipticF(ArcSin(1/Sqrt(-1+x)), 2)}
	public void test00585() {
		check("Integrate(1/(Sqrt(-3+x)*Sqrt(-2+x)*Sqrt(-1+x)), x)", "-2*EllipticF(ArcSin(1/Sqrt(-1+x)), 2)");

	}

	// {1/(Sqrt(-3-x)*Sqrt(-2-x)*Sqrt(-1-x)), x, 1, 2*EllipticF(ArcSin(1/Sqrt(-1-x)), 2)}
	public void test00586() {
		check("Integrate(1/(Sqrt(-3-x)*Sqrt(-2-x)*Sqrt(-1-x)), x)", "2*EllipticF(ArcSin(1/Sqrt(-1-x)), 2)");

	}

	// {Sqrt(2+3*x)/(Sqrt(1-2*x)*Sqrt(3+5*x)), x, 1, -(Sqrt(7/5)*EllipticE(ArcSin(Sqrt(5/11)*Sqrt(1-2*x)),
	// 33/35))}
	public void test00587() {
		check("Integrate(Sqrt(2+3*x)/(Sqrt(1-2*x)*Sqrt(3+5*x)), x)",
				"-(Sqrt(7/5)*EllipticE(ArcSin(Sqrt(5/11)*Sqrt(1-2*x)), 33/35))");

	}

	// {1/(Sqrt(1-2*x)*Sqrt(2+3*x)*Sqrt(3+5*x)), x, 1, (-2*EllipticF(ArcSin(Sqrt(3/7)*Sqrt(1-2*x)),
	// 35/33))/Sqrt(33)}
	public void test00588() {
		check("Integrate(1/(Sqrt(1-2*x)*Sqrt(2+3*x)*Sqrt(3+5*x)), x)",
				"(-2*EllipticF(ArcSin(Sqrt(3/7)*Sqrt(1-2*x)), 35/33))/Sqrt(33)");

	}

	// {1/(Sqrt(4-x)*Sqrt(5-x)*Sqrt(-3+x)), x, 1, Sqrt(2)*EllipticF(ArcSin(Sqrt(-3+x)), 1/2)}
	public void test00589() {
		check("Integrate(1/(Sqrt(4-x)*Sqrt(5-x)*Sqrt(-3+x)), x)", "Sqrt(2)*EllipticF(ArcSin(Sqrt(-3+x)), 1/2)");

	}

	// {1/(Sqrt(6-x)*Sqrt(-2+x)*Sqrt(-1+x)), x, 1, 2*EllipticF(ArcSin(Sqrt(-2+x)/2), -4)}
	public void test00590() {
		check("Integrate(1/(Sqrt(6-x)*Sqrt(-2+x)*Sqrt(-1+x)), x)", "2*EllipticF(ArcSin(Sqrt(-2+x)/2), -4)");

	}

	// {1/((a+b*x)^(1/3)*(c+d*x)^(2/3)), x, 1, -((Sqrt(3)*ArcTan(1/Sqrt(3)+(2*d^(1/3)*(a +
	// b*x)^(1/3))/(Sqrt(3)*b^(1/3)*(c+d*x)^(1/3))))/(b^(1/3)*d^(2/3)))-Log(c+d*x)/(2*b^(1/3)*d^(2/3))-(3*Log(-1
	// +(d^(1/3)*(a+b*x)^(1/3))/(b^(1/3)*(c+d*x)^(1/3))))/(2*b^(1/3)*d^(2/3))}
	public void test00591() {
		check("Integrate(1/((a+b*x)^(1/3)*(c+d*x)^(2/3)), x)",
				"-((Sqrt(3)*ArcTan(1/Sqrt(3)+(2*d^(1/3)*(a+b*x)^(1/3))/(Sqrt(3)*b^(1/3)*(c+d*x)^(1/3))))/(b^(1/3)*d^(2/3)))-Log(c+d*x)/(2*b^(1/3)*d^(2/3))-(3*Log(-1+(d^(1/3)*(a+b*x)^(1/3))/(b^(1/3)*(c+d*x)^(1/3))))/(2*b^(1/3)*d^(2/3))");

	}

	// {1/((a+b*x)^(1/3)*(c+d*x)^(2/3)*(e+f*x)), x, 1, -((Sqrt(3)*ArcTan(1/Sqrt(3)+(2*(d*e-c*f)^(1/3)*(a +
	// b*x)^(1/3))/(Sqrt(3)*(b*e-a*f)^(1/3)*(c+d*x)^(1/3))))/((b*e-a*f)^(1/3)*(d*e-c*f)^(2/3)))+Log(e +
	// f*x)/(2*(b*e-a*f)^(1/3)*(d*e-c*f)^(2/3))-(3*Log(((d*e-c*f)^(1/3)*(a+b*x)^(1/3))/(b*e-a*f)^(1/3)-(c
	// +d*x)^(1/3)))/(2*(b*e-a*f)^(1/3)*(d*e-c*f)^(2/3))}
	public void test00592() {
		check("Integrate(1/((a+b*x)^(1/3)*(c+d*x)^(2/3)*(e+f*x)), x)",
				"-((Sqrt(3)*ArcTan(1/Sqrt(3)+(2*(d*e-c*f)^(1/3)*(a+b*x)^(1/3))/(Sqrt(3)*(b*e-a*f)^(1/3)*(c+d*x)^(1/3))))/((b*e-a*f)^(1/3)*(d*e-c*f)^(2/3)))+Log(e+f*x)/(2*(b*e-a*f)^(1/3)*(d*e-c*f)^(2/3))-(3*Log(((d*e-c*f)^(1/3)*(a+b*x)^(1/3))/(b*e-a*f)^(1/3)-(c+d*x)^(1/3)))/(2*(b*e-a*f)^(1/3)*(d*e-c*f)^(2/3))");

	}

	// {1/((a+b*x)*(c+d*x)^(1/3)*(b*c+a*d+2*b*d*x)^(1/3)), x, 1, -(Sqrt(3)*ArcTan(1/Sqrt(3)+(2*b^(2/3)*(c +
	// d*x)^(2/3))/(Sqrt(3)*(b*c-a*d)^(1/3)*(b*c+a*d+2*b*d*x)^(1/3))))/(2*b^(2/3)*(b*c-a*d)^(2/3))-Log(a +
	// b*x)/(2*b^(2/3)*(b*c-a*d)^(2/3))+(3*Log((b^(2/3)*(c+d*x)^(2/3))/(b*c-a*d)^(1/3)-(b*c+a*d +
	// 2*b*d*x)^(1/3)))/(4*b^(2/3)*(b*c-a*d)^(2/3))}
	public void test00593() {
		check("Integrate(1/((a+b*x)*(c+d*x)^(1/3)*(b*c+a*d+2*b*d*x)^(1/3)), x)",
				"-(Sqrt(3)*ArcTan(1/Sqrt(3)+(2*b^(2/3)*(c+d*x)^(2/3))/(Sqrt(3)*(b*c-a*d)^(1/3)*(b*c+a*d+2*b*d*x)^(1/3))))/(2*b^(2/3)*(b*c-a*d)^(2/3))-Log(a+b*x)/(2*b^(2/3)*(b*c-a*d)^(2/3))+(3*Log((b^(2/3)*(c+d*x)^(2/3))/(b*c-a*d)^(1/3)-(b*c+a*d+2*b*d*x)^(1/3)))/(4*b^(2/3)*(b*c-a*d)^(2/3))");

	}

	// {(a+b*x)/((c+d*x)^(1/3)*(b*c+a*d+2*b*d*x)^(4/3)), x, 1, (3*(c+d*x)^(2/3))/(2*d^2*(b*c+a*d +
	// 2*b*d*x)^(1/3))}
	public void test00594() {
		check("Integrate((a+b*x)/((c+d*x)^(1/3)*(b*c+a*d+2*b*d*x)^(4/3)), x)",
				"(3*(c+d*x)^(2/3))/(2*d^2*(b*c+a*d+2*b*d*x)^(1/3))");

	}

	// {1/((d-3*e*x)^(1/3)*(d+e*x)*(d+3*e*x)^(1/3)), x, 1, (Sqrt(3)*ArcTan(1/Sqrt(3)-(d -
	// 3*e*x)^(2/3)/(Sqrt(3)*d^(1/3)*(d+3*e*x)^(1/3))))/(4*d^(2/3)*e)+Log(d+e*x)/(4*d^(2/3)*e)-(3*Log(-(d -
	// 3*e*x)^(2/3)/(2*d^(1/3))-(d+3*e*x)^(1/3)))/(8*d^(2/3)*e)}
	public void test00595() {
		check("Integrate(1/((d-3*e*x)^(1/3)*(d+e*x)*(d+3*e*x)^(1/3)), x)",
				"(Sqrt(3)*ArcTan(1/Sqrt(3)-(d-3*e*x)^(2/3)/(Sqrt(3)*d^(1/3)*(d+3*e*x)^(1/3))))/(4*d^(2/3)*e)+Log(d+e*x)/(4*d^(2/3)*e)-(3*Log(-(d-3*e*x)^(2/3)/(2*d^(1/3))-(d+3*e*x)^(1/3)))/(8*d^(2/3)*e)");

	}

	// {(a+b*x)^m/((c+d*x)^m*(e+f*x)^2), x, 1, ((b*c-a*d)*(a+b*x)^(1+m)*(c+d*x)^(-1 -
	// m)*Hypergeometric2F1(2, 1+m, 2+m, ((d*e-c*f)*(a+b*x))/((b*e-a*f)*(c+d*x))))/((b*e-a*f)^2*(1+m))}
	public void test00596() {
		check("Integrate((a+b*x)^m/((c+d*x)^m*(e+f*x)^2), x)",
				"((b*c-a*d)*(a+b*x)^(1+m)*(c+d*x)^(-1-m)*Hypergeometric2F1(2, 1+m, 2+m, ((d*e-c*f)*(a+b*x))/((b*e-a*f)*(c+d*x))))/((b*e-a*f)^2*(1+m))");

	}

	// {((a+b*x)^m*(c+d*x)^(-1-m))/(e+f*x), x, 1, ((a+b*x)^(1+m)*(c+d*x)^(-1-m)*Hypergeometric2F1(1, 1 +
	// m, 2+m, ((d*e-c*f)*(a+b*x))/((b*e-a*f)*(c+d*x))))/((b*e-a*f)*(1+m))}
	public void test00597() {
		check("Integrate(((a+b*x)^m*(c+d*x)^(-1-m))/(e+f*x), x)",
				"((a+b*x)^(1+m)*(c+d*x)^(-1-m)*Hypergeometric2F1(1, 1+m, 2+m, ((d*e-c*f)*(a+b*x))/((b*e-a*f)*(c+d*x))))/((b*e-a*f)*(1+m))");

	}

	// {(a+b*x)^m*(c+d*x)^(-2-m), x, 1, ((a+b*x)^(1+m)*(c+d*x)^(-1-m))/((b*c-a*d)*(1+m))}
	public void test00598() {
		check("Integrate((a+b*x)^m*(c+d*x)^(-2-m), x)", "((a+b*x)^(1+m)*(c+d*x)^(-1-m))/((b*c-a*d)*(1+m))");

	}

	// {((a+b*x)^m*(c+d*x)^(1-m))/(e+f*x)^3, x, 1, ((b*c-a*d)^2*(a+b*x)^(1+m)*(c+d*x)^(-1 -
	// m)*Hypergeometric2F1(3, 1+m, 2+m, ((d*e-c*f)*(a+b*x))/((b*e-a*f)*(c+d*x))))/((b*e-a*f)^3*(1+m))}
	public void test00599() {
		check("Integrate(((a+b*x)^m*(c+d*x)^(1-m))/(e+f*x)^3, x)",
				"((b*c-a*d)^2*(a+b*x)^(1+m)*(c+d*x)^(-1-m)*Hypergeometric2F1(3, 1+m, 2+m, ((d*e-c*f)*(a+b*x))/((b*e-a*f)*(c+d*x))))/((b*e-a*f)^3*(1+m))");

	}

	// {((a+b*x)^m*(c+d*x)^(2-m))/(e+f*x)^4, x, 1, ((b*c-a*d)^3*(a+b*x)^(1+m)*(c+d*x)^(-1 -
	// m)*Hypergeometric2F1(4, 1+m, 2+m, ((d*e-c*f)*(a+b*x))/((b*e-a*f)*(c+d*x))))/((b*e-a*f)^4*(1+m))}
	public void test00600() {
		check("Integrate(((a+b*x)^m*(c+d*x)^(2-m))/(e+f*x)^4, x)",
				"((b*c-a*d)^3*(a+b*x)^(1+m)*(c+d*x)^(-1-m)*Hypergeometric2F1(4, 1+m, 2+m, ((d*e-c*f)*(a+b*x))/((b*e-a*f)*(c+d*x))))/((b*e-a*f)^4*(1+m))");

	}

	// {(a+b*x)^m*(c+d*x)^(-m-n)*(e+f*x)^(-2+n), x, 1, ((a+b*x)^(1+m)*(c+d*x)^(-m-n)*(((b*e-a*f)*(c
	// +d*x))/((b*c-a*d)*(e+f*x)))^(m+n)*(e+f*x)^(-1+n)*Hypergeometric2F1(1+m, m+n, 2+m, -(((d*e -
	// c*f)*(a+b*x))/((b*c-a*d)*(e+f*x)))))/((b*e-a*f)*(1+m))}
	public void test00601() {
		check("Integrate((a+b*x)^m*(c+d*x)^(-m-n)*(e+f*x)^(-2+n), x)",
				"((a+b*x)^(1+m)*(c+d*x)^(-m-n)*(((b*e-a*f)*(c+d*x))/((b*c-a*d)*(e+f*x)))^(m+n)*(e+f*x)^(-1+n)*Hypergeometric2F1(1+m, m+n, 2+m, -(((d*e-c*f)*(a+b*x))/((b*c-a*d)*(e+f*x)))))/((b*e-a*f)*(1+m))");

	}

	// {(a+b*x)^m*(c+d*x)^n*((b*c*f+a*d*f+a*d*f*m+b*c*f*n)/(b*d*(2+m+n))+f*x)^(-3-m-n), x, 1,
	// (b*d*(2+m+n)*(a+b*x)^(1+m)*(c+d*x)^(1+n)*((f*(a*d*(1+m)+b*c*(1+n)))/(b*d*(2+m+n)) +
	// f*x)^(-2-m-n))/((b*c-a*d)^2*f*(1+m)*(1+n))}
	public void test00602() {
		check("Integrate((a+b*x)^m*(c+d*x)^n*((b*c*f+a*d*f+a*d*f*m+b*c*f*n)/(b*d*(2+m+n))+f*x)^(-3-m-n), x)",
				"(b*d*(2+m+n)*(a+b*x)^(1+m)*(c+d*x)^(1+n)*((f*(a*d*(1+m)+b*c*(1+n)))/(b*d*(2+m+n))+f*x)^(-2-m-n))/((b*c-a*d)^2*f*(1+m)*(1+n))");

	}

	// {(a+b*x)^m*(c+d*x)^(-1-(d*(b*e-a*f)*(1+m))/(b*(d*e-c*f)))*(e+f*x)^(-1+((b*c-a*d)*f*(1 +
	// m))/(b*(d*e-c*f))), x, 1, (b*(a+b*x)^(1+m)*(e+f*x)^(((b*c-a*d)*f*(1+m))/(b*(d*e-c*f))))/((b*c -
	// a*d)*(b*e-a*f)*(1+m)*(c+d*x)^((d*(b*e-a*f)*(1+m))/(b*(d*e-c*f))))}
	public void test00603() {
		check("Integrate((a+b*x)^m*(c+d*x)^(-1-(d*(b*e-a*f)*(1+m))/(b*(d*e-c*f)))*(e+f*x)^(-1+((b*c-a*d)*f*(1+m))/(b*(d*e-c*f))), x)",
				"(b*(a+b*x)^(1+m)*(e+f*x)^(((b*c-a*d)*f*(1+m))/(b*(d*e-c*f))))/((b*c-a*d)*(b*e-a*f)*(1+m)*(c+d*x)^((d*(b*e-a*f)*(1+m))/(b*(d*e-c*f))))");

	}

	// {(a+b*x)^m*(c+d*x)^n*(e+f*x)^(-2-m-n), x, 1, ((a+b*x)^(1+m)*(c+d*x)^n*(e+f*x)^(-1-m -
	// n)*Hypergeometric2F1(1+m, -n, 2+m, -(((d*e-c*f)*(a+b*x))/((b*c-a*d)*(e+f*x)))))/((b*e-a*f)*(1 +
	// m)*(((b*e-a*f)*(c+d*x))/((b*c-a*d)*(e+f*x)))^n)}
	public void test00604() {
		check("Integrate((a+b*x)^m*(c+d*x)^n*(e+f*x)^(-2-m-n), x)",
				"((a+b*x)^(1+m)*(c+d*x)^n*(e+f*x)^(-1-m-n)*Hypergeometric2F1(1+m, -n, 2+m, -(((d*e-c*f)*(a+b*x))/((b*c-a*d)*(e+f*x)))))/((b*e-a*f)*(1+m)*(((b*e-a*f)*(c+d*x))/((b*c-a*d)*(e+f*x)))^n)");

	}

	// {(3+4*x)^n/(Sqrt(1-x)*Sqrt(1+x)), x, 1, -(Sqrt(2)*7^n*Sqrt(1-x)*AppellF1(1/2, 1/2, -n, 3/2, (1-x)/2,
	// (4*(1-x))/7))}
	public void test00605() {
		check("Integrate((3+4*x)^n/(Sqrt(1-x)*Sqrt(1+x)), x)",
				"-(Sqrt(2)*7^n*Sqrt(1-x)*AppellF1(1/2, 1/2, -n, 3/2, (1-x)/2, (4*(1-x))/7))");

	}

	// {(3-4*x)^n/(Sqrt(1-x)*Sqrt(1+x)), x, 1, Sqrt(2)*7^n*Sqrt(1+x)*AppellF1(1/2, -n, 1/2, 3/2, (4*(1+x))/7,
	// (1+x)/2)}
	public void test00606() {
		check("Integrate((3-4*x)^n/(Sqrt(1-x)*Sqrt(1+x)), x)",
				"Sqrt(2)*7^n*Sqrt(1+x)*AppellF1(1/2, -n, 1/2, 3/2, (4*(1+x))/7, (1+x)/2)");

	}

	// {(-3+4*x)^n/(Sqrt(1-x)*Sqrt(1+x)), x, 1, -(Sqrt(2)*Sqrt(1-x)*AppellF1(1/2, 1/2, -n, 3/2, (1-x)/2, 4*(1
	// -x)))}
	public void test00607() {
		check("Integrate((-3+4*x)^n/(Sqrt(1-x)*Sqrt(1+x)), x)",
				"-(Sqrt(2)*Sqrt(1-x)*AppellF1(1/2, 1/2, -n, 3/2, (1-x)/2, 4*(1-x)))");

	}

	// {(-3-4*x)^n/(Sqrt(1-x)*Sqrt(1+x)), x, 1, Sqrt(2)*Sqrt(1+x)*AppellF1(1/2, -n, 1/2, 3/2, 4*(1+x), (1 +
	// x)/2)}
	public void test00608() {
		check("Integrate((-3-4*x)^n/(Sqrt(1-x)*Sqrt(1+x)), x)",
				"Sqrt(2)*Sqrt(1+x)*AppellF1(1/2, -n, 1/2, 3/2, 4*(1+x), (1+x)/2)");

	}

	// {(a+b*x)^m/(e+f*x)^2, x, 1, (b*(a+b*x)^(1+m)*Hypergeometric2F1(2, 1+m, 2+m, -((f*(a+b*x))/(b*e -
	// a*f))))/((b*e-a*f)^2*(1+m))}
	public void test00609() {
		check("Integrate((a+b*x)^m/(e+f*x)^2, x)",
				"(b*(a+b*x)^(1+m)*Hypergeometric2F1(2, 1+m, 2+m, -((f*(a+b*x))/(b*e-a*f))))/((b*e-a*f)^2*(1+m))");

	}

	// {a+b*x^2, x, 1, a*x+(b*x^3)/3}
	public void test00610() {
		check("Integrate(a+b*x^2, x)", "a*x+(b*x^3)/3");

	}

	// {x*(a+b*x^2)^2, x, 1, (a+b*x^2)^3/(6*b)}
	public void test00611() {
		check("Integrate(x*(a+b*x^2)^2, x)", "(a+b*x^2)^3/(6*b)");

	}

	// {(a+b*x^2)^2/x^7, x, 1, -(a+b*x^2)^3/(6*a*x^6)}
	public void test00612() {
		check("Integrate((a+b*x^2)^2/x^7, x)", "-(a+b*x^2)^3/(6*a*x^6)");

	}

	// {x*(a+b*x^2)^3, x, 1, (a+b*x^2)^4/(8*b)}
	public void test00613() {
		check("Integrate(x*(a+b*x^2)^3, x)", "(a+b*x^2)^4/(8*b)");

	}

	// {(a+b*x^2)^3/x^9, x, 1, -(a+b*x^2)^4/(8*a*x^8)}
	public void test00614() {
		check("Integrate((a+b*x^2)^3/x^9, x)", "-(a+b*x^2)^4/(8*a*x^8)");

	}

	// {x*(a+b*x^2)^5, x, 1, (a+b*x^2)^6/(12*b)}
	public void test00615() {
		check("Integrate(x*(a+b*x^2)^5, x)", "(a+b*x^2)^6/(12*b)");

	}

	// {(a+b*x^2)^5/x^13, x, 1, -(a+b*x^2)^6/(12*a*x^12)}
	public void test00616() {
		check("Integrate((a+b*x^2)^5/x^13, x)", "-(a+b*x^2)^6/(12*a*x^12)");

	}

	// {x*(a+b*x^2)^8, x, 1, (a+b*x^2)^9/(18*b)}
	public void test00617() {
		check("Integrate(x*(a+b*x^2)^8, x)", "(a+b*x^2)^9/(18*b)");

	}

	// {(a+b*x^2)^8/x^19, x, 1, -(a+b*x^2)^9/(18*a*x^18)}
	public void test00618() {
		check("Integrate((a+b*x^2)^8/x^19, x)", "-(a+b*x^2)^9/(18*a*x^18)");

	}

	// {x/(a+b*x^2), x, 1, Log(a+b*x^2)/(2*b)}
	public void test00619() {
		check("Integrate(x/(a+b*x^2), x)", "Log(a+b*x^2)/(2*b)");

	}

	// {(a+b*x^2)^(-1), x, 1, ArcTan((Sqrt(b)*x)/Sqrt(a))/(Sqrt(a)*Sqrt(b))}
	public void test00620() {
		check("Integrate((a+b*x^2)^(-1), x)", "ArcTan((Sqrt(b)*x)/Sqrt(a))/(Sqrt(a)*Sqrt(b))");

	}

	// {x/(a+b*x^2)^2, x, 1, -1/(2*b*(a+b*x^2))}
	public void test00621() {
		check("Integrate(x/(a+b*x^2)^2, x)", "-1/(2*b*(a+b*x^2))");

	}

	// {x^3/(a+b*x^2)^3, x, 1, x^4/(4*a*(a+b*x^2)^2)}
	public void test00622() {
		check("Integrate(x^3/(a+b*x^2)^3, x)", "x^4/(4*a*(a+b*x^2)^2)");

	}

	// {x/(a+b*x^2)^3, x, 1, -1/(4*b*(a+b*x^2)^2)}
	public void test00623() {
		check("Integrate(x/(a+b*x^2)^3, x)", "-1/(4*b*(a+b*x^2)^2)");

	}

	// {x^17/(a+b*x^2)^10, x, 1, x^18/(18*a*(a+b*x^2)^9)}
	public void test00624() {
		check("Integrate(x^17/(a+b*x^2)^10, x)", "x^18/(18*a*(a+b*x^2)^9)");

	}

	// {x/(a+b*x^2)^10, x, 1, -1/(18*b*(a+b*x^2)^9)}
	public void test00625() {
		check("Integrate(x/(a+b*x^2)^10, x)", "-1/(18*b*(a+b*x^2)^9)");

	}

	// {x/(a-b*x^2), x, 1, -Log(a-b*x^2)/(2*b)}
	public void test00626() {
		check("Integrate(x/(a-b*x^2), x)", "-Log(a-b*x^2)/(2*b)");

	}

	// {(a-b*x^2)^(-1), x, 1, ArcTanh((Sqrt(b)*x)/Sqrt(a))/(Sqrt(a)*Sqrt(b))}
	public void test00627() {
		check("Integrate((a-b*x^2)^(-1), x)", "ArcTanh((Sqrt(b)*x)/Sqrt(a))/(Sqrt(a)*Sqrt(b))");

	}

	// {x/(a-b*x^2)^2, x, 1, 1/(2*b*(a-b*x^2))}
	public void test00628() {
		check("Integrate(x/(a-b*x^2)^2, x)", "1/(2*b*(a-b*x^2))");

	}

	// {x^3/(a-b*x^2)^3, x, 1, x^4/(4*a*(a-b*x^2)^2)}
	public void test00629() {
		check("Integrate(x^3/(a-b*x^2)^3, x)", "x^4/(4*a*(a-b*x^2)^2)");

	}

	// {x/(a-b*x^2)^3, x, 1, 1/(4*b*(a-b*x^2)^2)}
	public void test00630() {
		check("Integrate(x/(a-b*x^2)^3, x)", "1/(4*b*(a-b*x^2)^2)");

	}

	// {x/(a-b*x^2)^5, x, 1, 1/(8*b*(a-b*x^2)^4)}
	public void test00631() {
		check("Integrate(x/(a-b*x^2)^5, x)", "1/(8*b*(a-b*x^2)^4)");

	}

	// {(-1+a+a*x^2)^(-1), x, 1, -(ArcTanh((Sqrt(a)*x)/Sqrt(1-a))/Sqrt((1-a)*a))}
	public void test00632() {
		check("Integrate((-1+a+a*x^2)^(-1), x)", "-(ArcTanh((Sqrt(a)*x)/Sqrt(1-a))/Sqrt((1-a)*a))");

	}

	// {(-c-d+(c-d)*x^2)^(-1), x, 1, -(ArcTanh((Sqrt(c-d)*x)/Sqrt(c+d))/(Sqrt(c-d)*Sqrt(c+d)))}
	public void test00633() {
		check("Integrate((-c-d+(c-d)*x^2)^(-1), x)", "-(ArcTanh((Sqrt(c-d)*x)/Sqrt(c+d))/(Sqrt(c-d)*Sqrt(c+d)))");

	}

	// {(a+(b-a*c)*x^2)^(-1), x, 1, ArcTan((Sqrt(b-a*c)*x)/Sqrt(a))/(Sqrt(a)*Sqrt(b-a*c))}
	public void test00634() {
		check("Integrate((a+(b-a*c)*x^2)^(-1), x)", "ArcTan((Sqrt(b-a*c)*x)/Sqrt(a))/(Sqrt(a)*Sqrt(b-a*c))");

	}

	// {(a-(b-a*c)*x^2)^(-1), x, 1, ArcTanh((Sqrt(b-a*c)*x)/Sqrt(a))/(Sqrt(a)*Sqrt(b-a*c))}
	public void test00635() {
		check("Integrate((a-(b-a*c)*x^2)^(-1), x)", "ArcTanh((Sqrt(b-a*c)*x)/Sqrt(a))/(Sqrt(a)*Sqrt(b-a*c))");

	}

	// {(c*(a-d)-(b-c)*x^2)^(-1), x, 1, ArcTanh((Sqrt(b-c)*x)/(Sqrt(c)*Sqrt(a-d)))/(Sqrt(b-c)*Sqrt(c)*Sqrt(a
	// -d))}
	public void test00636() {
		check("Integrate((c*(a-d)-(b-c)*x^2)^(-1), x)",
				"ArcTanh((Sqrt(b-c)*x)/(Sqrt(c)*Sqrt(a-d)))/(Sqrt(b-c)*Sqrt(c)*Sqrt(a-d))");

	}

	// {x^m/(a+b*x^2), x, 1, (x^(1+m)*Hypergeometric2F1(1, (1+m)/2, (3+m)/2, -((b*x^2)/a)))/(a*(1+m))}
	public void test00637() {
		check("Integrate(x^m/(a+b*x^2), x)",
				"(x^(1+m)*Hypergeometric2F1(1, (1+m)/2, (3+m)/2, -((b*x^2)/a)))/(a*(1+m))");

	}

	// {x^m/(a+b*x^2)^2, x, 1, (x^(1+m)*Hypergeometric2F1(2, (1+m)/2, (3+m)/2, -((b*x^2)/a)))/(a^2*(1+m))}
	public void test00638() {
		check("Integrate(x^m/(a+b*x^2)^2, x)",
				"(x^(1+m)*Hypergeometric2F1(2, (1+m)/2, (3+m)/2, -((b*x^2)/a)))/(a^2*(1+m))");

	}

	// {x^m/(a+b*x^2)^3, x, 1, (x^(1+m)*Hypergeometric2F1(3, (1+m)/2, (3+m)/2, -((b*x^2)/a)))/(a^3*(1+m))}
	public void test00639() {
		check("Integrate(x^m/(a+b*x^2)^3, x)",
				"(x^(1+m)*Hypergeometric2F1(3, (1+m)/2, (3+m)/2, -((b*x^2)/a)))/(a^3*(1+m))");

	}

	// {(c*x)^(1+m)/(a+b*x^2), x, 1, ((c*x)^(2+m)*Hypergeometric2F1(1, (2+m)/2, (4+m)/2,
	// -((b*x^2)/a)))/(a*c*(2+m))}
	public void test00640() {
		check("Integrate((c*x)^(1+m)/(a+b*x^2), x)",
				"((c*x)^(2+m)*Hypergeometric2F1(1, (2+m)/2, (4+m)/2, -((b*x^2)/a)))/(a*c*(2+m))");

	}

	// {(c*x)^m/(a+b*x^2), x, 1, ((c*x)^(1+m)*Hypergeometric2F1(1, (1+m)/2, (3+m)/2, -((b*x^2)/a)))/(a*c*(1 +
	// m))}
	public void test00641() {
		check("Integrate((c*x)^m/(a+b*x^2), x)",
				"((c*x)^(1+m)*Hypergeometric2F1(1, (1+m)/2, (3+m)/2, -((b*x^2)/a)))/(a*c*(1+m))");

	}

	// {(c*x)^(-1+m)/(a+b*x^2), x, 1, ((c*x)^m*Hypergeometric2F1(1, m/2, (2+m)/2, -((b*x^2)/a)))/(a*c*m)}
	public void test00642() {
		check("Integrate((c*x)^(-1+m)/(a+b*x^2), x)",
				"((c*x)^m*Hypergeometric2F1(1, m/2, (2+m)/2, -((b*x^2)/a)))/(a*c*m)");

	}

	// {(c*x)^(-2+m)/(a+b*x^2), x, 1, -(((c*x)^(-1+m)*Hypergeometric2F1(1, (-1+m)/2, (1+m)/2,
	// -((b*x^2)/a)))/(a*c*(1-m)))}
	public void test00643() {
		check("Integrate((c*x)^(-2+m)/(a+b*x^2), x)",
				"-(((c*x)^(-1+m)*Hypergeometric2F1(1, (-1+m)/2, (1+m)/2, -((b*x^2)/a)))/(a*c*(1-m)))");

	}

	// {(c*x)^(-3+m)/(a+b*x^2), x, 1, -(((c*x)^(-2+m)*Hypergeometric2F1(1, (-2+m)/2, m/2, -((b*x^2)/a)))/(a*c*(2
	// -m)))}
	public void test00644() {
		check("Integrate((c*x)^(-3+m)/(a+b*x^2), x)",
				"-(((c*x)^(-2+m)*Hypergeometric2F1(1, (-2+m)/2, m/2, -((b*x^2)/a)))/(a*c*(2-m)))");

	}

	// {x^m/(1+(a*x^2)/b)^2, x, 1, (x^(1+m)*Hypergeometric2F1(2, (1+m)/2, (3+m)/2, -((a*x^2)/b)))/(1+m)}
	public void test00645() {
		check("Integrate(x^m/(1+(a*x^2)/b)^2, x)",
				"(x^(1+m)*Hypergeometric2F1(2, (1+m)/2, (3+m)/2, -((a*x^2)/b)))/(1+m)");

	}

	// {x*Sqrt(a+b*x^2), x, 1, (a+b*x^2)^(3/2)/(3*b)}
	public void test00646() {
		check("Integrate(x*Sqrt(a+b*x^2), x)", "(a+b*x^2)^(3/2)/(3*b)");

	}

	// {Sqrt(a+b*x^2)/x^4, x, 1, -(a+b*x^2)^(3/2)/(3*a*x^3)}
	public void test00647() {
		check("Integrate(Sqrt(a+b*x^2)/x^4, x)", "-(a+b*x^2)^(3/2)/(3*a*x^3)");

	}

	// {x*(a+b*x^2)^(3/2), x, 1, (a+b*x^2)^(5/2)/(5*b)}
	public void test00648() {
		check("Integrate(x*(a+b*x^2)^(3/2), x)", "(a+b*x^2)^(5/2)/(5*b)");

	}

	// {(a+b*x^2)^(3/2)/x^6, x, 1, -(a+b*x^2)^(5/2)/(5*a*x^5)}
	public void test00649() {
		check("Integrate((a+b*x^2)^(3/2)/x^6, x)", "-(a+b*x^2)^(5/2)/(5*a*x^5)");

	}

	// {x*(a+b*x^2)^(5/2), x, 1, (a+b*x^2)^(7/2)/(7*b)}
	public void test00650() {
		check("Integrate(x*(a+b*x^2)^(5/2), x)", "(a+b*x^2)^(7/2)/(7*b)");

	}

	// {(a+b*x^2)^(5/2)/x^8, x, 1, -(a+b*x^2)^(7/2)/(7*a*x^7)}
	public void test00651() {
		check("Integrate((a+b*x^2)^(5/2)/x^8, x)", "-(a+b*x^2)^(7/2)/(7*a*x^7)");

	}

	// {x*(a+b*x^2)^(9/2), x, 1, (a+b*x^2)^(11/2)/(11*b)}
	public void test00652() {
		check("Integrate(x*(a+b*x^2)^(9/2), x)", "(a+b*x^2)^(11/2)/(11*b)");

	}

	// {(a+b*x^2)^(9/2)/x^12, x, 1, -(a+b*x^2)^(11/2)/(11*a*x^11)}
	public void test00653() {
		check("Integrate((a+b*x^2)^(9/2)/x^12, x)", "-(a+b*x^2)^(11/2)/(11*a*x^11)");

	}

	// {x*Sqrt(9+4*x^2), x, 1, (9+4*x^2)^(3/2)/12}
	public void test00654() {
		check("Integrate(x*Sqrt(9+4*x^2), x)", "(9+4*x^2)^(3/2)/12");

	}

	// {Sqrt(9+4*x^2)/x^4, x, 1, -(9+4*x^2)^(3/2)/(27*x^3)}
	public void test00655() {
		check("Integrate(Sqrt(9+4*x^2)/x^4, x)", "-(9+4*x^2)^(3/2)/(27*x^3)");

	}

	// {x*Sqrt(9-4*x^2), x, 1, -(9-4*x^2)^(3/2)/12}
	public void test00656() {
		check("Integrate(x*Sqrt(9-4*x^2), x)", "-(9-4*x^2)^(3/2)/12");

	}

	// {Sqrt(9-4*x^2)/x^4, x, 1, -(9-4*x^2)^(3/2)/(27*x^3)}
	public void test00657() {
		check("Integrate(Sqrt(9-4*x^2)/x^4, x)", "-(9-4*x^2)^(3/2)/(27*x^3)");

	}

	// {x*Sqrt(-9+4*x^2), x, 1, (-9+4*x^2)^(3/2)/12}
	public void test00658() {
		check("Integrate(x*Sqrt(-9+4*x^2), x)", "(-9+4*x^2)^(3/2)/12");

	}

	// {Sqrt(-9+4*x^2)/x^4, x, 1, (-9+4*x^2)^(3/2)/(27*x^3)}
	public void test00659() {
		check("Integrate(Sqrt(-9+4*x^2)/x^4, x)", "(-9+4*x^2)^(3/2)/(27*x^3)");

	}

	// {x*Sqrt(-9-4*x^2), x, 1, -(-9-4*x^2)^(3/2)/12}
	public void test00660() {
		check("Integrate(x*Sqrt(-9-4*x^2), x)", "-(-9-4*x^2)^(3/2)/12");

	}

	// {Sqrt(-9-4*x^2)/x^4, x, 1, (-9-4*x^2)^(3/2)/(27*x^3)}
	public void test00661() {
		check("Integrate(Sqrt(-9-4*x^2)/x^4, x)", "(-9-4*x^2)^(3/2)/(27*x^3)");

	}

	// {x/Sqrt(a+b*x^2), x, 1, Sqrt(a+b*x^2)/b}
	public void test00662() {
		check("Integrate(x/Sqrt(a+b*x^2), x)", "Sqrt(a+b*x^2)/b");

	}

	// {1/(x^2*Sqrt(a+b*x^2)), x, 1, -(Sqrt(a+b*x^2)/(a*x))}
	public void test00663() {
		check("Integrate(1/(x^2*Sqrt(a+b*x^2)), x)", "-(Sqrt(a+b*x^2)/(a*x))");

	}

	// {x/(a+b*x^2)^(3/2), x, 1, -(1/(b*Sqrt(a+b*x^2)))}
	public void test00664() {
		check("Integrate(x/(a+b*x^2)^(3/2), x)", "-(1/(b*Sqrt(a+b*x^2)))");

	}

	// {(a+b*x^2)^(-3/2), x, 1, x/(a*Sqrt(a+b*x^2))}
	public void test00665() {
		check("Integrate((a+b*x^2)^(-3/2), x)", "x/(a*Sqrt(a+b*x^2))");

	}

	// {x^2/(a+b*x^2)^(5/2), x, 1, x^3/(3*a*(a+b*x^2)^(3/2))}
	public void test00666() {
		check("Integrate(x^2/(a+b*x^2)^(5/2), x)", "x^3/(3*a*(a+b*x^2)^(3/2))");

	}

	// {x/(a+b*x^2)^(5/2), x, 1, -1/(3*b*(a+b*x^2)^(3/2))}
	public void test00667() {
		check("Integrate(x/(a+b*x^2)^(5/2), x)", "-1/(3*b*(a+b*x^2)^(3/2))");

	}

	// {x^6/(a+b*x^2)^(9/2), x, 1, x^7/(7*a*(a+b*x^2)^(7/2))}
	public void test00668() {
		check("Integrate(x^6/(a+b*x^2)^(9/2), x)", "x^7/(7*a*(a+b*x^2)^(7/2))");

	}

	// {x/(a+b*x^2)^(9/2), x, 1, -1/(7*b*(a+b*x^2)^(7/2))}
	public void test00669() {
		check("Integrate(x/(a+b*x^2)^(9/2), x)", "-1/(7*b*(a+b*x^2)^(7/2))");

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

	// {(a-b*x^2)^(1/4)/(c*x)^(7/2), x, 1, (-2*(a-b*x^2)^(5/4))/(5*a*c*(c*x)^(5/2))}
	public void test00700() {
		check("Integrate((a-b*x^2)^(1/4)/(c*x)^(7/2), x)", "(-2*(a-b*x^2)^(5/4))/(5*a*c*(c*x)^(5/2))");

	}

	// {1/((c*x)^(5/2)*(a+b*x^2)^(1/4)), x, 1, (-2*(a+b*x^2)^(3/4))/(3*a*c*(c*x)^(3/2))}
	public void test00701() {
		check("Integrate(1/((c*x)^(5/2)*(a+b*x^2)^(1/4)), x)", "(-2*(a+b*x^2)^(3/4))/(3*a*c*(c*x)^(3/2))");

	}

	// {1/((c*x)^(5/2)*(a-b*x^2)^(1/4)), x, 1, (-2*(a-b*x^2)^(3/4))/(3*a*c*(c*x)^(3/2))}
	public void test00702() {
		check("Integrate(1/((c*x)^(5/2)*(a-b*x^2)^(1/4)), x)", "(-2*(a-b*x^2)^(3/4))/(3*a*c*(c*x)^(3/2))");

	}

	// {1/((c*x)^(3/2)*(a+b*x^2)^(3/4)), x, 1, (-2*(a+b*x^2)^(1/4))/(a*c*Sqrt(c*x))}
	public void test00703() {
		check("Integrate(1/((c*x)^(3/2)*(a+b*x^2)^(3/4)), x)", "(-2*(a+b*x^2)^(1/4))/(a*c*Sqrt(c*x))");

	}

	// {1/((c*x)^(3/2)*(a-b*x^2)^(3/4)), x, 1, (-2*(a-b*x^2)^(1/4))/(a*c*Sqrt(c*x))}
	public void test00704() {
		check("Integrate(1/((c*x)^(3/2)*(a-b*x^2)^(3/4)), x)", "(-2*(a-b*x^2)^(1/4))/(a*c*Sqrt(c*x))");

	}

	// {1/(Sqrt(c*x)*(a+b*x^2)^(5/4)), x, 1, (2*Sqrt(c*x))/(a*c*(a+b*x^2)^(1/4))}
	public void test00705() {
		check("Integrate(1/(Sqrt(c*x)*(a+b*x^2)^(5/4)), x)", "(2*Sqrt(c*x))/(a*c*(a+b*x^2)^(1/4))");

	}

	// {x*(a+b*x^2)^p, x, 1, (a+b*x^2)^(1+p)/(2*b*(1+p))}
	public void test00706() {
		check("Integrate(x*(a+b*x^2)^p, x)", "(a+b*x^2)^(1+p)/(2*b*(1+p))");

	}

	// {x^(-3-2*p)*(a+b*x^2)^p, x, 1, -(a+b*x^2)^(1+p)/(2*a*(1+p)*x^(2*(1+p)))}
	public void test00707() {
		check("Integrate(x^(-3-2*p)*(a+b*x^2)^p, x)", "-(a+b*x^2)^(1+p)/(2*a*(1+p)*x^(2*(1+p)))");

	}

	// {(a+b*x^2)^(-3/2), x, 1, x/(a*Sqrt(a+b*x^2))}
	public void test00708() {
		check("Integrate((a+b*x^2)^(-3/2), x)", "x/(a*Sqrt(a+b*x^2))");

	}

	// {1/((a-b*x^2)^(1/3)*(3*a+b*x^2)), x, 1,
	// ArcTan((Sqrt(3)*Sqrt(a))/(Sqrt(b)*x))/(2*2^(2/3)*Sqrt(3)*a^(5/6)*Sqrt(b))+ArcTan((Sqrt(3)*a^(1/6)*(a^(1/3) -
	// 2^(1/3)*(a-b*x^2)^(1/3)))/(Sqrt(b)*x))/(2*2^(2/3)*Sqrt(3)*a^(5/6)*Sqrt(b)) -
	// ArcTanh((Sqrt(b)*x)/Sqrt(a))/(6*2^(2/3)*a^(5/6)*Sqrt(b))+ArcTanh((Sqrt(b)*x)/(a^(1/6)*(a^(1/3)+2^(1/3)*(a -
	// b*x^2)^(1/3))))/(2*2^(2/3)*a^(5/6)*Sqrt(b))}
	public void test00709() {
		check("Integrate(1/((a-b*x^2)^(1/3)*(3*a+b*x^2)), x)",
				"ArcTan((Sqrt(3)*Sqrt(a))/(Sqrt(b)*x))/(2*2^(2/3)*Sqrt(3)*a^(5/6)*Sqrt(b))+ArcTan((Sqrt(3)*a^(1/6)*(a^(1/3)-2^(1/3)*(a-b*x^2)^(1/3)))/(Sqrt(b)*x))/(2*2^(2/3)*Sqrt(3)*a^(5/6)*Sqrt(b))-ArcTanh((Sqrt(b)*x)/Sqrt(a))/(6*2^(2/3)*a^(5/6)*Sqrt(b))+ArcTanh((Sqrt(b)*x)/(a^(1/6)*(a^(1/3)+2^(1/3)*(a-b*x^2)^(1/3))))/(2*2^(2/3)*a^(5/6)*Sqrt(b))");

	}

	// {1/((-3*a-b*x^2)*(-a+b*x^2)^(1/3)), x, 1,
	// -ArcTan((Sqrt(3)*Sqrt(a))/(Sqrt(b)*x))/(2*2^(2/3)*Sqrt(3)*(-a)^(1/3)*Sqrt(a)*Sqrt(b)) -
	// ArcTan((Sqrt(3)*Sqrt(a)*((-a)^(1/3)-2^(1/3)*(-a +
	// b*x^2)^(1/3)))/((-a)^(1/3)*Sqrt(b)*x))/(2*2^(2/3)*Sqrt(3)*(-a)^(1/3)*Sqrt(a)*Sqrt(b)) +
	// ArcTanh((Sqrt(b)*x)/Sqrt(a))/(6*2^(2/3)*(-a)^(1/3)*Sqrt(a)*Sqrt(b)) -
	// ArcTanh(((-a)^(1/3)*Sqrt(b)*x)/(Sqrt(a)*((-a)^(1/3)+2^(1/3)*(-a +
	// b*x^2)^(1/3))))/(2*2^(2/3)*(-a)^(1/3)*Sqrt(a)*Sqrt(b))}
	public void test00710() {
		check("Integrate(1/((-3*a-b*x^2)*(-a+b*x^2)^(1/3)), x)",
				"-ArcTan((Sqrt(3)*Sqrt(a))/(Sqrt(b)*x))/(2*2^(2/3)*Sqrt(3)*(-a)^(1/3)*Sqrt(a)*Sqrt(b))-ArcTan((Sqrt(3)*Sqrt(a)*((-a)^(1/3)-2^(1/3)*(-a+b*x^2)^(1/3)))/((-a)^(1/3)*Sqrt(b)*x))/(2*2^(2/3)*Sqrt(3)*(-a)^(1/3)*Sqrt(a)*Sqrt(b))+ArcTanh((Sqrt(b)*x)/Sqrt(a))/(6*2^(2/3)*(-a)^(1/3)*Sqrt(a)*Sqrt(b))-ArcTanh(((-a)^(1/3)*Sqrt(b)*x)/(Sqrt(a)*((-a)^(1/3)+2^(1/3)*(-a+b*x^2)^(1/3))))/(2*2^(2/3)*(-a)^(1/3)*Sqrt(a)*Sqrt(b))");

	}

	// {1/((3*a-b*x^2)*(a+b*x^2)^(1/3)), x, 1, -ArcTan((Sqrt(b)*x)/Sqrt(a))/(6*2^(2/3)*a^(5/6)*Sqrt(b)) +
	// ArcTan((Sqrt(b)*x)/(a^(1/6)*(a^(1/3)+2^(1/3)*(a+b*x^2)^(1/3))))/(2*2^(2/3)*a^(5/6)*Sqrt(b)) -
	// ArcTanh((Sqrt(3)*Sqrt(a))/(Sqrt(b)*x))/(2*2^(2/3)*Sqrt(3)*a^(5/6)*Sqrt(b))-ArcTanh((Sqrt(3)*a^(1/6)*(a^(1/3) -
	// 2^(1/3)*(a+b*x^2)^(1/3)))/(Sqrt(b)*x))/(2*2^(2/3)*Sqrt(3)*a^(5/6)*Sqrt(b))}
	public void test00711() {
		check("Integrate(1/((3*a-b*x^2)*(a+b*x^2)^(1/3)), x)",
				"-ArcTan((Sqrt(b)*x)/Sqrt(a))/(6*2^(2/3)*a^(5/6)*Sqrt(b))+ArcTan((Sqrt(b)*x)/(a^(1/6)*(a^(1/3)+2^(1/3)*(a+b*x^2)^(1/3))))/(2*2^(2/3)*a^(5/6)*Sqrt(b))-ArcTanh((Sqrt(3)*Sqrt(a))/(Sqrt(b)*x))/(2*2^(2/3)*Sqrt(3)*a^(5/6)*Sqrt(b))-ArcTanh((Sqrt(3)*a^(1/6)*(a^(1/3)-2^(1/3)*(a+b*x^2)^(1/3)))/(Sqrt(b)*x))/(2*2^(2/3)*Sqrt(3)*a^(5/6)*Sqrt(b))");

	}

	// {1/((c-d*x^2)*(c+3*d*x^2)^(1/3)), x, 1,
	// -ArcTan((Sqrt(3)*Sqrt(d)*x)/Sqrt(c))/(2*2^(2/3)*Sqrt(3)*c^(5/6)*Sqrt(d)) +
	// (Sqrt(3)*ArcTan((Sqrt(3)*Sqrt(d)*x)/(c^(1/6)*(c^(1/3)+2^(1/3)*(c +
	// 3*d*x^2)^(1/3)))))/(2*2^(2/3)*c^(5/6)*Sqrt(d))-ArcTanh(Sqrt(c)/(Sqrt(d)*x))/(2*2^(2/3)*c^(5/6)*Sqrt(d)) -
	// ArcTanh((c^(1/6)*(c^(1/3)-2^(1/3)*(c+3*d*x^2)^(1/3)))/(Sqrt(d)*x))/(2*2^(2/3)*c^(5/6)*Sqrt(d))}
	public void test00712() {
		check("Integrate(1/((c-d*x^2)*(c+3*d*x^2)^(1/3)), x)",
				"-ArcTan((Sqrt(3)*Sqrt(d)*x)/Sqrt(c))/(2*2^(2/3)*Sqrt(3)*c^(5/6)*Sqrt(d))+(Sqrt(3)*ArcTan((Sqrt(3)*Sqrt(d)*x)/(c^(1/6)*(c^(1/3)+2^(1/3)*(c+3*d*x^2)^(1/3)))))/(2*2^(2/3)*c^(5/6)*Sqrt(d))-ArcTanh(Sqrt(c)/(Sqrt(d)*x))/(2*2^(2/3)*c^(5/6)*Sqrt(d))-ArcTanh((c^(1/6)*(c^(1/3)-2^(1/3)*(c+3*d*x^2)^(1/3)))/(Sqrt(d)*x))/(2*2^(2/3)*c^(5/6)*Sqrt(d))");

	}

	// {1/((a-b*x^2)^(1/3)*(3*a+b*x^2)), x, 1,
	// ArcTan((Sqrt(3)*Sqrt(a))/(Sqrt(b)*x))/(2*2^(2/3)*Sqrt(3)*a^(5/6)*Sqrt(b))+ArcTan((Sqrt(3)*a^(1/6)*(a^(1/3) -
	// 2^(1/3)*(a-b*x^2)^(1/3)))/(Sqrt(b)*x))/(2*2^(2/3)*Sqrt(3)*a^(5/6)*Sqrt(b)) -
	// ArcTanh((Sqrt(b)*x)/Sqrt(a))/(6*2^(2/3)*a^(5/6)*Sqrt(b))+ArcTanh((Sqrt(b)*x)/(a^(1/6)*(a^(1/3)+2^(1/3)*(a -
	// b*x^2)^(1/3))))/(2*2^(2/3)*a^(5/6)*Sqrt(b))}
	public void test00713() {
		check("Integrate(1/((a-b*x^2)^(1/3)*(3*a+b*x^2)), x)",
				"ArcTan((Sqrt(3)*Sqrt(a))/(Sqrt(b)*x))/(2*2^(2/3)*Sqrt(3)*a^(5/6)*Sqrt(b))+ArcTan((Sqrt(3)*a^(1/6)*(a^(1/3)-2^(1/3)*(a-b*x^2)^(1/3)))/(Sqrt(b)*x))/(2*2^(2/3)*Sqrt(3)*a^(5/6)*Sqrt(b))-ArcTanh((Sqrt(b)*x)/Sqrt(a))/(6*2^(2/3)*a^(5/6)*Sqrt(b))+ArcTanh((Sqrt(b)*x)/(a^(1/6)*(a^(1/3)+2^(1/3)*(a-b*x^2)^(1/3))))/(2*2^(2/3)*a^(5/6)*Sqrt(b))");

	}

	// {1/((c-3*d*x^2)^(1/3)*(c+d*x^2)), x, 1, ArcTan(Sqrt(c)/(Sqrt(d)*x))/(2*2^(2/3)*c^(5/6)*Sqrt(d)) +
	// ArcTan((c^(1/6)*(c^(1/3)-2^(1/3)*(c-3*d*x^2)^(1/3)))/(Sqrt(d)*x))/(2*2^(2/3)*c^(5/6)*Sqrt(d)) -
	// ArcTanh((Sqrt(3)*Sqrt(d)*x)/Sqrt(c))/(2*2^(2/3)*Sqrt(3)*c^(5/6)*Sqrt(d)) +
	// (Sqrt(3)*ArcTanh((Sqrt(3)*Sqrt(d)*x)/(c^(1/6)*(c^(1/3)+2^(1/3)*(c -
	// 3*d*x^2)^(1/3)))))/(2*2^(2/3)*c^(5/6)*Sqrt(d))}
	public void test00714() {
		check("Integrate(1/((c-3*d*x^2)^(1/3)*(c+d*x^2)), x)",
				"ArcTan(Sqrt(c)/(Sqrt(d)*x))/(2*2^(2/3)*c^(5/6)*Sqrt(d))+ArcTan((c^(1/6)*(c^(1/3)-2^(1/3)*(c-3*d*x^2)^(1/3)))/(Sqrt(d)*x))/(2*2^(2/3)*c^(5/6)*Sqrt(d))-ArcTanh((Sqrt(3)*Sqrt(d)*x)/Sqrt(c))/(2*2^(2/3)*Sqrt(3)*c^(5/6)*Sqrt(d))+(Sqrt(3)*ArcTanh((Sqrt(3)*Sqrt(d)*x)/(c^(1/6)*(c^(1/3)+2^(1/3)*(c-3*d*x^2)^(1/3)))))/(2*2^(2/3)*c^(5/6)*Sqrt(d))");

	}

	// {1/((1-x^2)^(1/3)*(3+x^2)), x, 1, ArcTan(Sqrt(3)/x)/(2*2^(2/3)*Sqrt(3))+ArcTan((Sqrt(3)*(1-2^(1/3)*(1 -
	// x^2)^(1/3)))/x)/(2*2^(2/3)*Sqrt(3))-ArcTanh(x)/(6*2^(2/3))+ArcTanh(x/(1+2^(1/3)*(1 -
	// x^2)^(1/3)))/(2*2^(2/3))}
	public void test00715() {
		check("Integrate(1/((1-x^2)^(1/3)*(3+x^2)), x)",
				"ArcTan(Sqrt(3)/x)/(2*2^(2/3)*Sqrt(3))+ArcTan((Sqrt(3)*(1-2^(1/3)*(1-x^2)^(1/3)))/x)/(2*2^(2/3)*Sqrt(3))-ArcTanh(x)/(6*2^(2/3))+ArcTanh(x/(1+2^(1/3)*(1-x^2)^(1/3)))/(2*2^(2/3))");

	}

	// {1/((3-x^2)*(1+x^2)^(1/3)), x, 1, -ArcTan(x)/(6*2^(2/3))+ArcTan(x/(1+2^(1/3)*(1 +
	// x^2)^(1/3)))/(2*2^(2/3))-ArcTanh(Sqrt(3)/x)/(2*2^(2/3)*Sqrt(3))-ArcTanh((Sqrt(3)*(1-2^(1/3)*(1 +
	// x^2)^(1/3)))/x)/(2*2^(2/3)*Sqrt(3))}
	public void test00716() {
		check("Integrate(1/((3-x^2)*(1+x^2)^(1/3)), x)",
				"-ArcTan(x)/(6*2^(2/3))+ArcTan(x/(1+2^(1/3)*(1+x^2)^(1/3)))/(2*2^(2/3))-ArcTanh(Sqrt(3)/x)/(2*2^(2/3)*Sqrt(3))-ArcTanh((Sqrt(3)*(1-2^(1/3)*(1+x^2)^(1/3)))/x)/(2*2^(2/3)*Sqrt(3))");

	}

	// {(3-x)/((1-x^2)^(1/3)*(3+x^2)), x, 1, -((Sqrt(3)*ArcTan(1/Sqrt(3)-(2^(2/3)*(1+x)^(2/3))/(Sqrt(3)*(1 -
	// x)^(1/3))))/2^(2/3))-Log(3+x^2)/(2*2^(2/3))+(3*Log(2^(1/3)*(1-x)^(1/3)+(1+x)^(2/3)))/(2*2^(2/3))}
	public void test00717() {
		check("Integrate((3-x)/((1-x^2)^(1/3)*(3+x^2)), x)",
				"-((Sqrt(3)*ArcTan(1/Sqrt(3)-(2^(2/3)*(1+x)^(2/3))/(Sqrt(3)*(1-x)^(1/3))))/2^(2/3))-Log(3+x^2)/(2*2^(2/3))+(3*Log(2^(1/3)*(1-x)^(1/3)+(1+x)^(2/3)))/(2*2^(2/3))");

	}

	// {(3+x)/((1-x^2)^(1/3)*(3+x^2)), x, 1, (Sqrt(3)*ArcTan(1/Sqrt(3)-(2^(2/3)*(1-x)^(2/3))/(Sqrt(3)*(1 +
	// x)^(1/3))))/2^(2/3)+Log(3+x^2)/(2*2^(2/3))-(3*Log((1-x)^(2/3)+2^(1/3)*(1+x)^(1/3)))/(2*2^(2/3))}
	public void test00718() {
		check("Integrate((3+x)/((1-x^2)^(1/3)*(3+x^2)), x)",
				"(Sqrt(3)*ArcTan(1/Sqrt(3)-(2^(2/3)*(1-x)^(2/3))/(Sqrt(3)*(1+x)^(1/3))))/2^(2/3)+Log(3+x^2)/(2*2^(2/3))-(3*Log((1-x)^(2/3)+2^(1/3)*(1+x)^(1/3)))/(2*2^(2/3))");

	}

	// {1/((a+b*x^2)^(1/3)*((9*a*d)/b+d*x^2)), x, 1, (Sqrt(b)*ArcTan((Sqrt(b)*x)/(3*Sqrt(a))))/(12*a^(5/6)*d) +
	// (Sqrt(b)*ArcTan((a^(1/3)-(a+b*x^2)^(1/3))^2/(3*a^(1/6)*Sqrt(b)*x)))/(12*a^(5/6)*d) -
	// (Sqrt(b)*ArcTanh((Sqrt(3)*a^(1/6)*(a^(1/3)-(a+b*x^2)^(1/3)))/(Sqrt(b)*x)))/(4*Sqrt(3)*a^(5/6)*d)}
	public void test00719() {
		check("Integrate(1/((a+b*x^2)^(1/3)*((9*a*d)/b+d*x^2)), x)",
				"(Sqrt(b)*ArcTan((Sqrt(b)*x)/(3*Sqrt(a))))/(12*a^(5/6)*d)+(Sqrt(b)*ArcTan((a^(1/3)-(a+b*x^2)^(1/3))^2/(3*a^(1/6)*Sqrt(b)*x)))/(12*a^(5/6)*d)-(Sqrt(b)*ArcTanh((Sqrt(3)*a^(1/6)*(a^(1/3)-(a+b*x^2)^(1/3)))/(Sqrt(b)*x)))/(4*Sqrt(3)*a^(5/6)*d)");

	}

	// {1/((a-b*x^2)^(1/3)*((-9*a*d)/b+d*x^2)), x, 1, -(Sqrt(b)*ArcTan((Sqrt(3)*a^(1/6)*(a^(1/3)-(a -
	// b*x^2)^(1/3)))/(Sqrt(b)*x)))/(4*Sqrt(3)*a^(5/6)*d)-(Sqrt(b)*ArcTanh((Sqrt(b)*x)/(3*Sqrt(a))))/(12*a^(5/6)*d) +
	// (Sqrt(b)*ArcTanh((a^(1/3)-(a-b*x^2)^(1/3))^2/(3*a^(1/6)*Sqrt(b)*x)))/(12*a^(5/6)*d)}
	public void test00720() {
		check("Integrate(1/((a-b*x^2)^(1/3)*((-9*a*d)/b+d*x^2)), x)",
				"-(Sqrt(b)*ArcTan((Sqrt(3)*a^(1/6)*(a^(1/3)-(a-b*x^2)^(1/3)))/(Sqrt(b)*x)))/(4*Sqrt(3)*a^(5/6)*d)-(Sqrt(b)*ArcTanh((Sqrt(b)*x)/(3*Sqrt(a))))/(12*a^(5/6)*d)+(Sqrt(b)*ArcTanh((a^(1/3)-(a-b*x^2)^(1/3))^2/(3*a^(1/6)*Sqrt(b)*x)))/(12*a^(5/6)*d)");

	}

	// {1/((-a+b*x^2)^(1/3)*((-9*a*d)/b+d*x^2)), x, 1, (Sqrt(b)*ArcTan((Sqrt(3)*a^(1/6)*(a^(1/3)+(-a +
	// b*x^2)^(1/3)))/(Sqrt(b)*x)))/(4*Sqrt(3)*a^(5/6)*d)+(Sqrt(b)*ArcTanh((Sqrt(b)*x)/(3*Sqrt(a))))/(12*a^(5/6)*d) -
	// (Sqrt(b)*ArcTanh((a^(1/3)+(-a+b*x^2)^(1/3))^2/(3*a^(1/6)*Sqrt(b)*x)))/(12*a^(5/6)*d)}
	public void test00721() {
		check("Integrate(1/((-a+b*x^2)^(1/3)*((-9*a*d)/b+d*x^2)), x)",
				"(Sqrt(b)*ArcTan((Sqrt(3)*a^(1/6)*(a^(1/3)+(-a+b*x^2)^(1/3)))/(Sqrt(b)*x)))/(4*Sqrt(3)*a^(5/6)*d)+(Sqrt(b)*ArcTanh((Sqrt(b)*x)/(3*Sqrt(a))))/(12*a^(5/6)*d)-(Sqrt(b)*ArcTanh((a^(1/3)+(-a+b*x^2)^(1/3))^2/(3*a^(1/6)*Sqrt(b)*x)))/(12*a^(5/6)*d)");

	}

	// {1/((-a-b*x^2)^(1/3)*((9*a*d)/b+d*x^2)), x, 1, -(Sqrt(b)*ArcTan((Sqrt(b)*x)/(3*Sqrt(a))))/(12*a^(5/6)*d) -
	// (Sqrt(b)*ArcTan((a^(1/3)+(-a-b*x^2)^(1/3))^2/(3*a^(1/6)*Sqrt(b)*x)))/(12*a^(5/6)*d) +
	// (Sqrt(b)*ArcTanh((Sqrt(3)*a^(1/6)*(a^(1/3)+(-a-b*x^2)^(1/3)))/(Sqrt(b)*x)))/(4*Sqrt(3)*a^(5/6)*d)}
	public void test00722() {
		check("Integrate(1/((-a-b*x^2)^(1/3)*((9*a*d)/b+d*x^2)), x)",
				"-(Sqrt(b)*ArcTan((Sqrt(b)*x)/(3*Sqrt(a))))/(12*a^(5/6)*d)-(Sqrt(b)*ArcTan((a^(1/3)+(-a-b*x^2)^(1/3))^2/(3*a^(1/6)*Sqrt(b)*x)))/(12*a^(5/6)*d)+(Sqrt(b)*ArcTanh((Sqrt(3)*a^(1/6)*(a^(1/3)+(-a-b*x^2)^(1/3)))/(Sqrt(b)*x)))/(4*Sqrt(3)*a^(5/6)*d)");

	}

	// {1/((2+b*x^2)^(1/3)*((18*d)/b+d*x^2)), x, 1, (Sqrt(b)*ArcTan((Sqrt(b)*x)/(3*Sqrt(2))))/(12*2^(5/6)*d) +
	// (Sqrt(b)*ArcTan((2^(1/3)-(2+b*x^2)^(1/3))^2/(3*2^(1/6)*Sqrt(b)*x)))/(12*2^(5/6)*d) -
	// (Sqrt(b)*ArcTanh((2^(1/6)*Sqrt(3)*(2^(1/3)-(2+b*x^2)^(1/3)))/(Sqrt(b)*x)))/(4*2^(5/6)*Sqrt(3)*d)}
	public void test00723() {
		check("Integrate(1/((2+b*x^2)^(1/3)*((18*d)/b+d*x^2)), x)",
				"(Sqrt(b)*ArcTan((Sqrt(b)*x)/(3*Sqrt(2))))/(12*2^(5/6)*d)+(Sqrt(b)*ArcTan((2^(1/3)-(2+b*x^2)^(1/3))^2/(3*2^(1/6)*Sqrt(b)*x)))/(12*2^(5/6)*d)-(Sqrt(b)*ArcTanh((2^(1/6)*Sqrt(3)*(2^(1/3)-(2+b*x^2)^(1/3)))/(Sqrt(b)*x)))/(4*2^(5/6)*Sqrt(3)*d)");

	}

	// {1/((-2+b*x^2)^(1/3)*((-18*d)/b+d*x^2)), x, 1, (Sqrt(b)*ArcTan((2^(1/6)*Sqrt(3)*(2^(1/3)+(-2 +
	// b*x^2)^(1/3)))/(Sqrt(b)*x)))/(4*2^(5/6)*Sqrt(3)*d)+(Sqrt(b)*ArcTanh((Sqrt(b)*x)/(3*Sqrt(2))))/(12*2^(5/6)*d) -
	// (Sqrt(b)*ArcTanh((2^(1/3)+(-2+b*x^2)^(1/3))^2/(3*2^(1/6)*Sqrt(b)*x)))/(12*2^(5/6)*d)}
	public void test00724() {
		check("Integrate(1/((-2+b*x^2)^(1/3)*((-18*d)/b+d*x^2)), x)",
				"(Sqrt(b)*ArcTan((2^(1/6)*Sqrt(3)*(2^(1/3)+(-2+b*x^2)^(1/3)))/(Sqrt(b)*x)))/(4*2^(5/6)*Sqrt(3)*d)+(Sqrt(b)*ArcTanh((Sqrt(b)*x)/(3*Sqrt(2))))/(12*2^(5/6)*d)-(Sqrt(b)*ArcTanh((2^(1/3)+(-2+b*x^2)^(1/3))^2/(3*2^(1/6)*Sqrt(b)*x)))/(12*2^(5/6)*d)");

	}

	// {1/((2+3*x^2)^(1/3)*(6*d+d*x^2)), x, 1, ArcTan(x/Sqrt(6))/(4*2^(5/6)*Sqrt(3)*d)+ArcTan((2^(1/3)-(2 +
	// 3*x^2)^(1/3))^2/(3*2^(1/6)*Sqrt(3)*x))/(4*2^(5/6)*Sqrt(3)*d)-ArcTanh((2^(1/6)*(2^(1/3)-(2 +
	// 3*x^2)^(1/3)))/x)/(4*2^(5/6)*d)}
	public void test00725() {
		check("Integrate(1/((2+3*x^2)^(1/3)*(6*d+d*x^2)), x)",
				"ArcTan(x/Sqrt(6))/(4*2^(5/6)*Sqrt(3)*d)+ArcTan((2^(1/3)-(2+3*x^2)^(1/3))^2/(3*2^(1/6)*Sqrt(3)*x))/(4*2^(5/6)*Sqrt(3)*d)-ArcTanh((2^(1/6)*(2^(1/3)-(2+3*x^2)^(1/3)))/x)/(4*2^(5/6)*d)");

	}

	// {1/((2-3*x^2)^(1/3)*(-6*d+d*x^2)), x, 1, -ArcTan((2^(1/6)*(2^(1/3)-(2-3*x^2)^(1/3)))/x)/(4*2^(5/6)*d) -
	// ArcTanh(x/Sqrt(6))/(4*2^(5/6)*Sqrt(3)*d)+ArcTanh((2^(1/3)-(2 -
	// 3*x^2)^(1/3))^2/(3*2^(1/6)*Sqrt(3)*x))/(4*2^(5/6)*Sqrt(3)*d)}
	public void test00726() {
		check("Integrate(1/((2-3*x^2)^(1/3)*(-6*d+d*x^2)), x)",
				"-ArcTan((2^(1/6)*(2^(1/3)-(2-3*x^2)^(1/3)))/x)/(4*2^(5/6)*d)-ArcTanh(x/Sqrt(6))/(4*2^(5/6)*Sqrt(3)*d)+ArcTanh((2^(1/3)-(2-3*x^2)^(1/3))^2/(3*2^(1/6)*Sqrt(3)*x))/(4*2^(5/6)*Sqrt(3)*d)");

	}

	// {1/((-2+3*x^2)^(1/3)*(-6*d+d*x^2)), x, 1, ArcTan((2^(1/6)*(2^(1/3)+(-2+3*x^2)^(1/3)))/x)/(4*2^(5/6)*d) +
	// ArcTanh(x/Sqrt(6))/(4*2^(5/6)*Sqrt(3)*d)-ArcTanh((2^(1/3)+(-2 +
	// 3*x^2)^(1/3))^2/(3*2^(1/6)*Sqrt(3)*x))/(4*2^(5/6)*Sqrt(3)*d)}
	public void test00727() {
		check("Integrate(1/((-2+3*x^2)^(1/3)*(-6*d+d*x^2)), x)",
				"ArcTan((2^(1/6)*(2^(1/3)+(-2+3*x^2)^(1/3)))/x)/(4*2^(5/6)*d)+ArcTanh(x/Sqrt(6))/(4*2^(5/6)*Sqrt(3)*d)-ArcTanh((2^(1/3)+(-2+3*x^2)^(1/3))^2/(3*2^(1/6)*Sqrt(3)*x))/(4*2^(5/6)*Sqrt(3)*d)");

	}

	// {1/((-2-3*x^2)^(1/3)*(6*d+d*x^2)), x, 1, -ArcTan(x/Sqrt(6))/(4*2^(5/6)*Sqrt(3)*d)-ArcTan((2^(1/3)+(-2 -
	// 3*x^2)^(1/3))^2/(3*2^(1/6)*Sqrt(3)*x))/(4*2^(5/6)*Sqrt(3)*d)+ArcTanh((2^(1/6)*(2^(1/3)+(-2 -
	// 3*x^2)^(1/3)))/x)/(4*2^(5/6)*d)}
	public void test00728() {
		check("Integrate(1/((-2-3*x^2)^(1/3)*(6*d+d*x^2)), x)",
				"-ArcTan(x/Sqrt(6))/(4*2^(5/6)*Sqrt(3)*d)-ArcTan((2^(1/3)+(-2-3*x^2)^(1/3))^2/(3*2^(1/6)*Sqrt(3)*x))/(4*2^(5/6)*Sqrt(3)*d)+ArcTanh((2^(1/6)*(2^(1/3)+(-2-3*x^2)^(1/3)))/x)/(4*2^(5/6)*d)");

	}

	// {1/((1+x^2)^(1/3)*(9+x^2)), x, 1, ArcTan(x/3)/12+ArcTan((1-(1+x^2)^(1/3))^2/(3*x))/12 -
	// ArcTanh((Sqrt(3)*(1-(1+x^2)^(1/3)))/x)/(4*Sqrt(3))}
	public void test00729() {
		check("Integrate(1/((1+x^2)^(1/3)*(9+x^2)), x)",
				"ArcTan(x/3)/12+ArcTan((1-(1+x^2)^(1/3))^2/(3*x))/12-ArcTanh((Sqrt(3)*(1-(1+x^2)^(1/3)))/x)/(4*Sqrt(3))");

	}

	// {1/((1+b*x^2)^(1/3)*(9+b*x^2)), x, 1, ArcTan((Sqrt(b)*x)/3)/(12*Sqrt(b))+ArcTan((1-(1 +
	// b*x^2)^(1/3))^2/(3*Sqrt(b)*x))/(12*Sqrt(b))-ArcTanh((Sqrt(3)*(1-(1 +
	// b*x^2)^(1/3)))/(Sqrt(b)*x))/(4*Sqrt(3)*Sqrt(b))}
	public void test00730() {
		check("Integrate(1/((1+b*x^2)^(1/3)*(9+b*x^2)), x)",
				"ArcTan((Sqrt(b)*x)/3)/(12*Sqrt(b))+ArcTan((1-(1+b*x^2)^(1/3))^2/(3*Sqrt(b)*x))/(12*Sqrt(b))-ArcTanh((Sqrt(3)*(1-(1+b*x^2)^(1/3)))/(Sqrt(b)*x))/(4*Sqrt(3)*Sqrt(b))");

	}

	// {1/((1-x^2)^(1/3)*(9-x^2)), x, 1, ArcTan((Sqrt(3)*(1-(1-x^2)^(1/3)))/x)/(4*Sqrt(3))+ArcTanh(x/3)/12 -
	// ArcTanh((1-(1-x^2)^(1/3))^2/(3*x))/12}
	public void test00731() {
		check("Integrate(1/((1-x^2)^(1/3)*(9-x^2)), x)",
				"ArcTan((Sqrt(3)*(1-(1-x^2)^(1/3)))/x)/(4*Sqrt(3))+ArcTanh(x/3)/12-ArcTanh((1-(1-x^2)^(1/3))^2/(3*x))/12");

	}

	// {Sqrt(c+d*x^2)/(a+b*x^2)^(3/2), x, 1, (Sqrt(c+d*x^2)*EllipticE(ArcTan((Sqrt(b)*x)/Sqrt(a)), 1 -
	// (a*d)/(b*c)))/(Sqrt(a)*Sqrt(b)*Sqrt(a+b*x^2)*Sqrt((a*(c+d*x^2))/(c*(a+b*x^2))))}
	public void test00732() {
		check("Integrate(Sqrt(c+d*x^2)/(a+b*x^2)^(3/2), x)",
				"(Sqrt(c+d*x^2)*EllipticE(ArcTan((Sqrt(b)*x)/Sqrt(a)), 1-(a*d)/(b*c)))/(Sqrt(a)*Sqrt(b)*Sqrt(a+b*x^2)*Sqrt((a*(c+d*x^2))/(c*(a+b*x^2))))");

	}

	// {Sqrt(1-x^2)/Sqrt(2-3*x^2), x, 1, EllipticE(ArcSin(Sqrt(3/2)*x), 2/3)/Sqrt(3)}
	public void test00733() {
		check("Integrate(Sqrt(1-x^2)/Sqrt(2-3*x^2), x)", "EllipticE(ArcSin(Sqrt(3/2)*x), 2/3)/Sqrt(3)");

	}

	// {Sqrt(4-x^2)/Sqrt(2-3*x^2), x, 1, (2*EllipticE(ArcSin(Sqrt(3/2)*x), 1/6))/Sqrt(3)}
	public void test00734() {
		check("Integrate(Sqrt(4-x^2)/Sqrt(2-3*x^2), x)", "(2*EllipticE(ArcSin(Sqrt(3/2)*x), 1/6))/Sqrt(3)");

	}

	// {Sqrt(1-4*x^2)/Sqrt(2-3*x^2), x, 1, EllipticE(ArcSin(Sqrt(3/2)*x), 8/3)/Sqrt(3)}
	public void test00735() {
		check("Integrate(Sqrt(1-4*x^2)/Sqrt(2-3*x^2), x)", "EllipticE(ArcSin(Sqrt(3/2)*x), 8/3)/Sqrt(3)");

	}

	// {Sqrt(1+x^2)/Sqrt(1-x^2), x, 1, EllipticE(ArcSin(x), -1)}
	public void test00736() {
		check("Integrate(Sqrt(1+x^2)/Sqrt(1-x^2), x)", "EllipticE(ArcSin(x), -1)");

	}

	// {Sqrt(1+x^2)/Sqrt(2-3*x^2), x, 1, EllipticE(ArcSin(Sqrt(3/2)*x), -2/3)/Sqrt(3)}
	public void test00737() {
		check("Integrate(Sqrt(1+x^2)/Sqrt(2-3*x^2), x)", "EllipticE(ArcSin(Sqrt(3/2)*x), -2/3)/Sqrt(3)");

	}

	// {Sqrt(4+x^2)/Sqrt(2-3*x^2), x, 1, (2*EllipticE(ArcSin(Sqrt(3/2)*x), -1/6))/Sqrt(3)}
	public void test00738() {
		check("Integrate(Sqrt(4+x^2)/Sqrt(2-3*x^2), x)", "(2*EllipticE(ArcSin(Sqrt(3/2)*x), -1/6))/Sqrt(3)");

	}

	// {Sqrt(1+4*x^2)/Sqrt(2-3*x^2), x, 1, EllipticE(ArcSin(Sqrt(3/2)*x), -8/3)/Sqrt(3)}
	public void test00739() {
		check("Integrate(Sqrt(1+4*x^2)/Sqrt(2-3*x^2), x)", "EllipticE(ArcSin(Sqrt(3/2)*x), -8/3)/Sqrt(3)");

	}

	// {1/(Sqrt(a+b*x^2)*Sqrt(c+d*x^2)), x, 1, (Sqrt(c)*Sqrt(a+b*x^2)*EllipticF(ArcTan((Sqrt(d)*x)/Sqrt(c)), 1 -
	// (b*c)/(a*d)))/(a*Sqrt(d)*Sqrt((c*(a+b*x^2))/(a*(c+d*x^2)))*Sqrt(c+d*x^2))}
	public void test00740() {
		check("Integrate(1/(Sqrt(a+b*x^2)*Sqrt(c+d*x^2)), x)",
				"(Sqrt(c)*Sqrt(a+b*x^2)*EllipticF(ArcTan((Sqrt(d)*x)/Sqrt(c)), 1-(b*c)/(a*d)))/(a*Sqrt(d)*Sqrt((c*(a+b*x^2))/(a*(c+d*x^2)))*Sqrt(c+d*x^2))");

	}

	// {Sqrt(a+b*x^2)/(c+d*x^2)^(3/2), x, 1, (Sqrt(a+b*x^2)*EllipticE(ArcTan((Sqrt(d)*x)/Sqrt(c)), 1 -
	// (b*c)/(a*d)))/(Sqrt(c)*Sqrt(d)*Sqrt((c*(a+b*x^2))/(a*(c+d*x^2)))*Sqrt(c+d*x^2))}
	public void test00741() {
		check("Integrate(Sqrt(a+b*x^2)/(c+d*x^2)^(3/2), x)",
				"(Sqrt(a+b*x^2)*EllipticE(ArcTan((Sqrt(d)*x)/Sqrt(c)), 1-(b*c)/(a*d)))/(Sqrt(c)*Sqrt(d)*Sqrt((c*(a+b*x^2))/(a*(c+d*x^2)))*Sqrt(c+d*x^2))");

	}

	// {1/(Sqrt(a+b*x^2)*Sqrt(c+d*x^2)), x, 1, (Sqrt(c)*Sqrt(a+b*x^2)*EllipticF(ArcTan((Sqrt(d)*x)/Sqrt(c)), 1 -
	// (b*c)/(a*d)))/(a*Sqrt(d)*Sqrt((c*(a+b*x^2))/(a*(c+d*x^2)))*Sqrt(c+d*x^2))}
	public void test00742() {
		check("Integrate(1/(Sqrt(a+b*x^2)*Sqrt(c+d*x^2)), x)",
				"(Sqrt(c)*Sqrt(a+b*x^2)*EllipticF(ArcTan((Sqrt(d)*x)/Sqrt(c)), 1-(b*c)/(a*d)))/(a*Sqrt(d)*Sqrt((c*(a+b*x^2))/(a*(c+d*x^2)))*Sqrt(c+d*x^2))");

	}

	// {1/(Sqrt(1-x^2)*Sqrt(2+5*x^2)), x, 1, EllipticF(ArcSin(x), -5/2)/Sqrt(2)}
	public void test00743() {
		check("Integrate(1/(Sqrt(1-x^2)*Sqrt(2+5*x^2)), x)", "EllipticF(ArcSin(x), -5/2)/Sqrt(2)");

	}

	// {1/(Sqrt(1-x^2)*Sqrt(2+4*x^2)), x, 1, EllipticF(ArcSin(x), -2)/Sqrt(2)}
	public void test00744() {
		check("Integrate(1/(Sqrt(1-x^2)*Sqrt(2+4*x^2)), x)", "EllipticF(ArcSin(x), -2)/Sqrt(2)");

	}

	// {1/(Sqrt(1-x^2)*Sqrt(2+3*x^2)), x, 1, EllipticF(ArcSin(x), -3/2)/Sqrt(2)}
	public void test00745() {
		check("Integrate(1/(Sqrt(1-x^2)*Sqrt(2+3*x^2)), x)", "EllipticF(ArcSin(x), -3/2)/Sqrt(2)");

	}

	// {1/(Sqrt(1-x^2)*Sqrt(2+x^2)), x, 1, EllipticF(ArcSin(x), -1/2)/Sqrt(2)}
	public void test00746() {
		check("Integrate(1/(Sqrt(1-x^2)*Sqrt(2+x^2)), x)", "EllipticF(ArcSin(x), -1/2)/Sqrt(2)");

	}

	// {1/(Sqrt(1-x^2)*Sqrt(2-x^2)), x, 1, EllipticF(ArcSin(x), 1/2)/Sqrt(2)}
	public void test00747() {
		check("Integrate(1/(Sqrt(1-x^2)*Sqrt(2-x^2)), x)", "EllipticF(ArcSin(x), 1/2)/Sqrt(2)");

	}

	// {1/(Sqrt(2-3*x^2)*Sqrt(1-x^2)), x, 1, EllipticF(ArcSin(x), 3/2)/Sqrt(2)}
	public void test00748() {
		check("Integrate(1/(Sqrt(2-3*x^2)*Sqrt(1-x^2)), x)", "EllipticF(ArcSin(x), 3/2)/Sqrt(2)");

	}

	// {1/(Sqrt(2-4*x^2)*Sqrt(1-x^2)), x, 1, EllipticF(ArcSin(x), 2)/Sqrt(2)}
	public void test00749() {
		check("Integrate(1/(Sqrt(2-4*x^2)*Sqrt(1-x^2)), x)", "EllipticF(ArcSin(x), 2)/Sqrt(2)");

	}

	// {1/(Sqrt(2-5*x^2)*Sqrt(1-x^2)), x, 1, EllipticF(ArcSin(x), 5/2)/Sqrt(2)}
	public void test00750() {
		check("Integrate(1/(Sqrt(2-5*x^2)*Sqrt(1-x^2)), x)", "EllipticF(ArcSin(x), 5/2)/Sqrt(2)");

	}

	// {1/(Sqrt(1+x^2)*Sqrt(2+5*x^2)), x, 1, (Sqrt(2+5*x^2)*EllipticF(ArcTan(x), -3/2))/(Sqrt(2)*Sqrt(1 +
	// x^2)*Sqrt((2+5*x^2)/(1+x^2)))}
	public void test00751() {
		check("Integrate(1/(Sqrt(1+x^2)*Sqrt(2+5*x^2)), x)",
				"(Sqrt(2+5*x^2)*EllipticF(ArcTan(x), -3/2))/(Sqrt(2)*Sqrt(1+x^2)*Sqrt((2+5*x^2)/(1+x^2)))");

	}

	// {1/(Sqrt(1+x^2)*Sqrt(2+4*x^2)), x, 1, (Sqrt(1+2*x^2)*EllipticF(ArcTan(x), -1))/(Sqrt(2)*Sqrt(1 +
	// x^2)*Sqrt((1+2*x^2)/(1+x^2)))}
	public void test00752() {
		check("Integrate(1/(Sqrt(1+x^2)*Sqrt(2+4*x^2)), x)",
				"(Sqrt(1+2*x^2)*EllipticF(ArcTan(x), -1))/(Sqrt(2)*Sqrt(1+x^2)*Sqrt((1+2*x^2)/(1+x^2)))");

	}

	// {1/(Sqrt(1+x^2)*Sqrt(2+3*x^2)), x, 1, (Sqrt(2+3*x^2)*EllipticF(ArcTan(x), -1/2))/(Sqrt(2)*Sqrt(1 +
	// x^2)*Sqrt((2+3*x^2)/(1+x^2)))}
	public void test00753() {
		check("Integrate(1/(Sqrt(1+x^2)*Sqrt(2+3*x^2)), x)",
				"(Sqrt(2+3*x^2)*EllipticF(ArcTan(x), -1/2))/(Sqrt(2)*Sqrt(1+x^2)*Sqrt((2+3*x^2)/(1+x^2)))");

	}

	// {1/(Sqrt(1+x^2)*Sqrt(2+x^2)), x, 1, (Sqrt(2+x^2)*EllipticF(ArcTan(x), 1/2))/(Sqrt(2)*Sqrt(1+x^2)*Sqrt((2
	// +x^2)/(1+x^2)))}
	public void test00754() {
		check("Integrate(1/(Sqrt(1+x^2)*Sqrt(2+x^2)), x)",
				"(Sqrt(2+x^2)*EllipticF(ArcTan(x), 1/2))/(Sqrt(2)*Sqrt(1+x^2)*Sqrt((2+x^2)/(1+x^2)))");

	}

	// {1/(Sqrt(2-x^2)*Sqrt(1+x^2)), x, 1, EllipticF(ArcSin(x/Sqrt(2)), -2)}
	public void test00755() {
		check("Integrate(1/(Sqrt(2-x^2)*Sqrt(1+x^2)), x)", "EllipticF(ArcSin(x/Sqrt(2)), -2)");

	}

	// {1/(Sqrt(2-3*x^2)*Sqrt(1+x^2)), x, 1, EllipticF(ArcSin(Sqrt(3/2)*x), -2/3)/Sqrt(3)}
	public void test00756() {
		check("Integrate(1/(Sqrt(2-3*x^2)*Sqrt(1+x^2)), x)", "EllipticF(ArcSin(Sqrt(3/2)*x), -2/3)/Sqrt(3)");

	}

	// {1/(Sqrt(2-4*x^2)*Sqrt(1+x^2)), x, 1, EllipticF(ArcSin(Sqrt(2)*x), -1/2)/2}
	public void test00757() {
		check("Integrate(1/(Sqrt(2-4*x^2)*Sqrt(1+x^2)), x)", "EllipticF(ArcSin(Sqrt(2)*x), -1/2)/2");

	}

	// {1/(Sqrt(2-5*x^2)*Sqrt(1+x^2)), x, 1, EllipticF(ArcSin(Sqrt(5/2)*x), -2/5)/Sqrt(5)}
	public void test00758() {
		check("Integrate(1/(Sqrt(2-5*x^2)*Sqrt(1+x^2)), x)", "EllipticF(ArcSin(Sqrt(5/2)*x), -2/5)/Sqrt(5)");

	}

	// {1/(Sqrt(2-x^2)*Sqrt(-1+x^2)), x, 1, -EllipticF(ArcCos(x/Sqrt(2)), 2)}
	public void test00759() {
		check("Integrate(1/(Sqrt(2-x^2)*Sqrt(-1+x^2)), x)", "-EllipticF(ArcCos(x/Sqrt(2)), 2)");

	}

	// {1/(Sqrt(-1-x^2)*Sqrt(2+5*x^2)), x, 1, (Sqrt(2+5*x^2)*EllipticF(ArcTan(x), -3/2))/(Sqrt(2)*Sqrt(-1 -
	// x^2)*Sqrt((2+5*x^2)/(1+x^2)))}
	public void test00760() {
		check("Integrate(1/(Sqrt(-1-x^2)*Sqrt(2+5*x^2)), x)",
				"(Sqrt(2+5*x^2)*EllipticF(ArcTan(x), -3/2))/(Sqrt(2)*Sqrt(-1-x^2)*Sqrt((2+5*x^2)/(1+x^2)))");

	}

	// {1/(Sqrt(-1-x^2)*Sqrt(2+4*x^2)), x, 1, (Sqrt(1+2*x^2)*EllipticF(ArcTan(x), -1))/(Sqrt(2)*Sqrt(-1 -
	// x^2)*Sqrt((1+2*x^2)/(1+x^2)))}
	public void test00761() {
		check("Integrate(1/(Sqrt(-1-x^2)*Sqrt(2+4*x^2)), x)",
				"(Sqrt(1+2*x^2)*EllipticF(ArcTan(x), -1))/(Sqrt(2)*Sqrt(-1-x^2)*Sqrt((1+2*x^2)/(1+x^2)))");

	}

	// {1/(Sqrt(-1-x^2)*Sqrt(2+3*x^2)), x, 1, (Sqrt(2+3*x^2)*EllipticF(ArcTan(x), -1/2))/(Sqrt(2)*Sqrt(-1 -
	// x^2)*Sqrt((2+3*x^2)/(1+x^2)))}
	public void test00762() {
		check("Integrate(1/(Sqrt(-1-x^2)*Sqrt(2+3*x^2)), x)",
				"(Sqrt(2+3*x^2)*EllipticF(ArcTan(x), -1/2))/(Sqrt(2)*Sqrt(-1-x^2)*Sqrt((2+3*x^2)/(1+x^2)))");

	}

	// {1/(Sqrt(-1-x^2)*Sqrt(2+x^2)), x, 1, (Sqrt(2+x^2)*EllipticF(ArcTan(x), 1/2))/(Sqrt(2)*Sqrt(-1 -
	// x^2)*Sqrt((2+x^2)/(1+x^2)))}
	public void test00763() {
		check("Integrate(1/(Sqrt(-1-x^2)*Sqrt(2+x^2)), x)",
				"(Sqrt(2+x^2)*EllipticF(ArcTan(x), 1/2))/(Sqrt(2)*Sqrt(-1-x^2)*Sqrt((2+x^2)/(1+x^2)))");

	}

	// {1/(Sqrt(2+b*x^2)*Sqrt(3+d*x^2)), x, 1, (Sqrt(2+b*x^2)*EllipticF(ArcTan((Sqrt(d)*x)/Sqrt(3)), 1 -
	// (3*b)/(2*d)))/(Sqrt(2)*Sqrt(d)*Sqrt((2+b*x^2)/(3+d*x^2))*Sqrt(3+d*x^2))}
	public void test00764() {
		check("Integrate(1/(Sqrt(2+b*x^2)*Sqrt(3+d*x^2)), x)",
				"(Sqrt(2+b*x^2)*EllipticF(ArcTan((Sqrt(d)*x)/Sqrt(3)), 1-(3*b)/(2*d)))/(Sqrt(2)*Sqrt(d)*Sqrt((2+b*x^2)/(3+d*x^2))*Sqrt(3+d*x^2))");

	}

	// {1/(Sqrt(4+x^2)*Sqrt(c+d*x^2)), x, 1, (Sqrt(c+d*x^2)*EllipticF(ArcTan(x/2), 1-(4*d)/c))/(c*Sqrt(4 +
	// x^2)*Sqrt((c+d*x^2)/(c*(4+x^2))))}
	public void test00765() {
		check("Integrate(1/(Sqrt(4+x^2)*Sqrt(c+d*x^2)), x)",
				"(Sqrt(c+d*x^2)*EllipticF(ArcTan(x/2), 1-(4*d)/c))/(c*Sqrt(4+x^2)*Sqrt((c+d*x^2)/(c*(4+x^2))))");

	}

	// {1/(Sqrt(1-x^2)*Sqrt(-1+2*x^2)), x, 1, -EllipticF(ArcCos(x), 2)}
	public void test00766() {
		check("Integrate(1/(Sqrt(1-x^2)*Sqrt(-1+2*x^2)), x)", "-EllipticF(ArcCos(x), 2)");

	}

	// {Sqrt(-1+3*x^2)/Sqrt(2-3*x^2), x, 1, -(EllipticE(ArcCos(Sqrt(3/2)*x), 2)/Sqrt(3))}
	public void test00767() {
		check("Integrate(Sqrt(-1+3*x^2)/Sqrt(2-3*x^2), x)", "-(EllipticE(ArcCos(Sqrt(3/2)*x), 2)/Sqrt(3))");

	}

	// {Sqrt(1+(2*c*x^2)/(b-Sqrt(b^2-4*a*c)))/Sqrt(1-(2*c*x^2)/(b+Sqrt(b^2-4*a*c))), x, 1, (Sqrt(b +
	// Sqrt(b^2-4*a*c))*EllipticE(ArcSin((Sqrt(2)*Sqrt(c)*x)/Sqrt(b+Sqrt(b^2-4*a*c))), -((b+Sqrt(b^2 -
	// 4*a*c))/(b-Sqrt(b^2-4*a*c)))))/(Sqrt(2)*Sqrt(c))}
	public void test00768() {
		check("Integrate(Sqrt(1+(2*c*x^2)/(b-Sqrt(b^2-4*a*c)))/Sqrt(1-(2*c*x^2)/(b+Sqrt(b^2-4*a*c))), x)",
				"(Sqrt(b+Sqrt(b^2-4*a*c))*EllipticE(ArcSin((Sqrt(2)*Sqrt(c)*x)/Sqrt(b+Sqrt(b^2-4*a*c))), -((b+Sqrt(b^2-4*a*c))/(b-Sqrt(b^2-4*a*c)))))/(Sqrt(2)*Sqrt(c))");

	}

	// {Sqrt(1-(2*c*x^2)/(b-Sqrt(b^2-4*a*c)))/Sqrt(1-(2*c*x^2)/(b+Sqrt(b^2-4*a*c))), x, 1, (Sqrt(b +
	// Sqrt(b^2-4*a*c))*EllipticE(ArcSin((Sqrt(2)*Sqrt(c)*x)/Sqrt(b+Sqrt(b^2-4*a*c))), (b+Sqrt(b^2-4*a*c))/(b
	// -Sqrt(b^2-4*a*c))))/(Sqrt(2)*Sqrt(c))}
	public void test00769() {
		check("Integrate(Sqrt(1-(2*c*x^2)/(b-Sqrt(b^2-4*a*c)))/Sqrt(1-(2*c*x^2)/(b+Sqrt(b^2-4*a*c))), x)",
				"(Sqrt(b+Sqrt(b^2-4*a*c))*EllipticE(ArcSin((Sqrt(2)*Sqrt(c)*x)/Sqrt(b+Sqrt(b^2-4*a*c))), (b+Sqrt(b^2-4*a*c))/(b-Sqrt(b^2-4*a*c))))/(Sqrt(2)*Sqrt(c))");

	}

	// {1/(Sqrt(3-3*Sqrt(3)+2*Sqrt(3)*x^2)*Sqrt(3+(-3+Sqrt(3))*x^2)), x, 1, -(Sqrt(3 +
	// Sqrt(3))*EllipticF(ArcCos(Sqrt((3-Sqrt(3))/3)*x), (1+Sqrt(3))/2))/6}
	public void test00770() {
		check("Integrate(1/(Sqrt(3-3*Sqrt(3)+2*Sqrt(3)*x^2)*Sqrt(3+(-3+Sqrt(3))*x^2)), x)",
				"-(Sqrt(3+Sqrt(3))*EllipticF(ArcCos(Sqrt((3-Sqrt(3))/3)*x), (1+Sqrt(3))/2))/6");

	}

	// {1/((2+3*x^2)^(1/4)*(4+3*x^2)), x, 1, -ArcTan((2*2^(3/4)+2*2^(1/4)*Sqrt(2+3*x^2))/(2*Sqrt(3)*x*(2 +
	// 3*x^2)^(1/4)))/(2*2^(3/4)*Sqrt(3))-ArcTanh((2*2^(3/4)-2*2^(1/4)*Sqrt(2+3*x^2))/(2*Sqrt(3)*x*(2 +
	// 3*x^2)^(1/4)))/(2*2^(3/4)*Sqrt(3))}
	public void test00771() {
		check("Integrate(1/((2+3*x^2)^(1/4)*(4+3*x^2)), x)",
				"-ArcTan((2*2^(3/4)+2*2^(1/4)*Sqrt(2+3*x^2))/(2*Sqrt(3)*x*(2+3*x^2)^(1/4)))/(2*2^(3/4)*Sqrt(3))-ArcTanh((2*2^(3/4)-2*2^(1/4)*Sqrt(2+3*x^2))/(2*Sqrt(3)*x*(2+3*x^2)^(1/4)))/(2*2^(3/4)*Sqrt(3))");

	}

	// {1/((2-3*x^2)^(1/4)*(4-3*x^2)), x, 1, ArcTan((2-Sqrt(2)*Sqrt(2-3*x^2))/(2^(1/4)*Sqrt(3)*x*(2 -
	// 3*x^2)^(1/4)))/(2*2^(3/4)*Sqrt(3))+ArcTanh((2+Sqrt(2)*Sqrt(2-3*x^2))/(2^(1/4)*Sqrt(3)*x*(2 -
	// 3*x^2)^(1/4)))/(2*2^(3/4)*Sqrt(3))}
	public void test00772() {
		check("Integrate(1/((2-3*x^2)^(1/4)*(4-3*x^2)), x)",
				"ArcTan((2-Sqrt(2)*Sqrt(2-3*x^2))/(2^(1/4)*Sqrt(3)*x*(2-3*x^2)^(1/4)))/(2*2^(3/4)*Sqrt(3))+ArcTanh((2+Sqrt(2)*Sqrt(2-3*x^2))/(2^(1/4)*Sqrt(3)*x*(2-3*x^2)^(1/4)))/(2*2^(3/4)*Sqrt(3))");

	}

	// {1/((2+b*x^2)^(1/4)*(4+b*x^2)), x, 1, -ArcTan((2*2^(3/4)+2*2^(1/4)*Sqrt(2+b*x^2))/(2*Sqrt(b)*x*(2 +
	// b*x^2)^(1/4)))/(2*2^(3/4)*Sqrt(b))-ArcTanh((2*2^(3/4)-2*2^(1/4)*Sqrt(2+b*x^2))/(2*Sqrt(b)*x*(2 +
	// b*x^2)^(1/4)))/(2*2^(3/4)*Sqrt(b))}
	public void test00773() {
		check("Integrate(1/((2+b*x^2)^(1/4)*(4+b*x^2)), x)",
				"-ArcTan((2*2^(3/4)+2*2^(1/4)*Sqrt(2+b*x^2))/(2*Sqrt(b)*x*(2+b*x^2)^(1/4)))/(2*2^(3/4)*Sqrt(b))-ArcTanh((2*2^(3/4)-2*2^(1/4)*Sqrt(2+b*x^2))/(2*Sqrt(b)*x*(2+b*x^2)^(1/4)))/(2*2^(3/4)*Sqrt(b))");

	}

	// {1/((2-b*x^2)^(1/4)*(4-b*x^2)), x, 1, ArcTan((2-Sqrt(2)*Sqrt(2-b*x^2))/(2^(1/4)*Sqrt(b)*x*(2 -
	// b*x^2)^(1/4)))/(2*2^(3/4)*Sqrt(b))+ArcTanh((2+Sqrt(2)*Sqrt(2-b*x^2))/(2^(1/4)*Sqrt(b)*x*(2 -
	// b*x^2)^(1/4)))/(2*2^(3/4)*Sqrt(b))}
	public void test00774() {
		check("Integrate(1/((2-b*x^2)^(1/4)*(4-b*x^2)), x)",
				"ArcTan((2-Sqrt(2)*Sqrt(2-b*x^2))/(2^(1/4)*Sqrt(b)*x*(2-b*x^2)^(1/4)))/(2*2^(3/4)*Sqrt(b))+ArcTanh((2+Sqrt(2)*Sqrt(2-b*x^2))/(2^(1/4)*Sqrt(b)*x*(2-b*x^2)^(1/4)))/(2*2^(3/4)*Sqrt(b))");

	}

	// {1/((a+3*x^2)^(1/4)*(2*a+3*x^2)), x, 1, -ArcTan((a^(3/4)*(1+Sqrt(a+3*x^2)/Sqrt(a)))/(Sqrt(3)*x*(a +
	// 3*x^2)^(1/4)))/(2*Sqrt(3)*a^(3/4))-ArcTanh((a^(3/4)*(1-Sqrt(a+3*x^2)/Sqrt(a)))/(Sqrt(3)*x*(a +
	// 3*x^2)^(1/4)))/(2*Sqrt(3)*a^(3/4))}
	public void test00775() {
		check("Integrate(1/((a+3*x^2)^(1/4)*(2*a+3*x^2)), x)",
				"-ArcTan((a^(3/4)*(1+Sqrt(a+3*x^2)/Sqrt(a)))/(Sqrt(3)*x*(a+3*x^2)^(1/4)))/(2*Sqrt(3)*a^(3/4))-ArcTanh((a^(3/4)*(1-Sqrt(a+3*x^2)/Sqrt(a)))/(Sqrt(3)*x*(a+3*x^2)^(1/4)))/(2*Sqrt(3)*a^(3/4))");

	}

	// {1/((a-3*x^2)^(1/4)*(2*a-3*x^2)), x, 1, ArcTan((a^(3/4)*(1-Sqrt(a-3*x^2)/Sqrt(a)))/(Sqrt(3)*x*(a -
	// 3*x^2)^(1/4)))/(2*Sqrt(3)*a^(3/4))+ArcTanh((a^(3/4)*(1+Sqrt(a-3*x^2)/Sqrt(a)))/(Sqrt(3)*x*(a -
	// 3*x^2)^(1/4)))/(2*Sqrt(3)*a^(3/4))}
	public void test00776() {
		check("Integrate(1/((a-3*x^2)^(1/4)*(2*a-3*x^2)), x)",
				"ArcTan((a^(3/4)*(1-Sqrt(a-3*x^2)/Sqrt(a)))/(Sqrt(3)*x*(a-3*x^2)^(1/4)))/(2*Sqrt(3)*a^(3/4))+ArcTanh((a^(3/4)*(1+Sqrt(a-3*x^2)/Sqrt(a)))/(Sqrt(3)*x*(a-3*x^2)^(1/4)))/(2*Sqrt(3)*a^(3/4))");

	}

	// {1/((a+b*x^2)^(1/4)*(2*a+b*x^2)), x, 1, -ArcTan((a^(3/4)*(1+Sqrt(a+b*x^2)/Sqrt(a)))/(Sqrt(b)*x*(a +
	// b*x^2)^(1/4)))/(2*a^(3/4)*Sqrt(b))-ArcTanh((a^(3/4)*(1-Sqrt(a+b*x^2)/Sqrt(a)))/(Sqrt(b)*x*(a +
	// b*x^2)^(1/4)))/(2*a^(3/4)*Sqrt(b))}
	public void test00777() {
		check("Integrate(1/((a+b*x^2)^(1/4)*(2*a+b*x^2)), x)",
				"-ArcTan((a^(3/4)*(1+Sqrt(a+b*x^2)/Sqrt(a)))/(Sqrt(b)*x*(a+b*x^2)^(1/4)))/(2*a^(3/4)*Sqrt(b))-ArcTanh((a^(3/4)*(1-Sqrt(a+b*x^2)/Sqrt(a)))/(Sqrt(b)*x*(a+b*x^2)^(1/4)))/(2*a^(3/4)*Sqrt(b))");

	}

	// {1/((a-b*x^2)^(1/4)*(2*a-b*x^2)), x, 1, ArcTan((a^(3/4)*(1-Sqrt(a-b*x^2)/Sqrt(a)))/(Sqrt(b)*x*(a -
	// b*x^2)^(1/4)))/(2*a^(3/4)*Sqrt(b))+ArcTanh((a^(3/4)*(1+Sqrt(a-b*x^2)/Sqrt(a)))/(Sqrt(b)*x*(a -
	// b*x^2)^(1/4)))/(2*a^(3/4)*Sqrt(b))}
	public void test00778() {
		check("Integrate(1/((a-b*x^2)^(1/4)*(2*a-b*x^2)), x)",
				"ArcTan((a^(3/4)*(1-Sqrt(a-b*x^2)/Sqrt(a)))/(Sqrt(b)*x*(a-b*x^2)^(1/4)))/(2*a^(3/4)*Sqrt(b))+ArcTanh((a^(3/4)*(1+Sqrt(a-b*x^2)/Sqrt(a)))/(Sqrt(b)*x*(a-b*x^2)^(1/4)))/(2*a^(3/4)*Sqrt(b))");

	}

	// {1/((-2+3*x^2)*(-1+3*x^2)^(1/4)), x, 1, -ArcTan((Sqrt(3/2)*x)/(-1+3*x^2)^(1/4))/(2*Sqrt(6)) -
	// ArcTanh((Sqrt(3/2)*x)/(-1+3*x^2)^(1/4))/(2*Sqrt(6))}
	public void test00779() {
		check("Integrate(1/((-2+3*x^2)*(-1+3*x^2)^(1/4)), x)",
				"-ArcTan((Sqrt(3/2)*x)/(-1+3*x^2)^(1/4))/(2*Sqrt(6))-ArcTanh((Sqrt(3/2)*x)/(-1+3*x^2)^(1/4))/(2*Sqrt(6))");

	}

	// {1/((-2-3*x^2)*(-1-3*x^2)^(1/4)), x, 1, -ArcTan((Sqrt(3/2)*x)/(-1-3*x^2)^(1/4))/(2*Sqrt(6)) -
	// ArcTanh((Sqrt(3/2)*x)/(-1-3*x^2)^(1/4))/(2*Sqrt(6))}
	public void test00780() {
		check("Integrate(1/((-2-3*x^2)*(-1-3*x^2)^(1/4)), x)",
				"-ArcTan((Sqrt(3/2)*x)/(-1-3*x^2)^(1/4))/(2*Sqrt(6))-ArcTanh((Sqrt(3/2)*x)/(-1-3*x^2)^(1/4))/(2*Sqrt(6))");

	}

	// {1/((-2+b*x^2)*(-1+b*x^2)^(1/4)), x, 1, -ArcTan((Sqrt(b)*x)/(Sqrt(2)*(-1+b*x^2)^(1/4)))/(2*Sqrt(2)*Sqrt(b))
	// -ArcTanh((Sqrt(b)*x)/(Sqrt(2)*(-1+b*x^2)^(1/4)))/(2*Sqrt(2)*Sqrt(b))}
	public void test00781() {
		check("Integrate(1/((-2+b*x^2)*(-1+b*x^2)^(1/4)), x)",
				"-ArcTan((Sqrt(b)*x)/(Sqrt(2)*(-1+b*x^2)^(1/4)))/(2*Sqrt(2)*Sqrt(b))-ArcTanh((Sqrt(b)*x)/(Sqrt(2)*(-1+b*x^2)^(1/4)))/(2*Sqrt(2)*Sqrt(b))");

	}

	// {1/((-2-b*x^2)*(-1-b*x^2)^(1/4)), x, 1, -ArcTan((Sqrt(b)*x)/(Sqrt(2)*(-1-b*x^2)^(1/4)))/(2*Sqrt(2)*Sqrt(b))
	// -ArcTanh((Sqrt(b)*x)/(Sqrt(2)*(-1-b*x^2)^(1/4)))/(2*Sqrt(2)*Sqrt(b))}
	public void test00782() {
		check("Integrate(1/((-2-b*x^2)*(-1-b*x^2)^(1/4)), x)",
				"-ArcTan((Sqrt(b)*x)/(Sqrt(2)*(-1-b*x^2)^(1/4)))/(2*Sqrt(2)*Sqrt(b))-ArcTanh((Sqrt(b)*x)/(Sqrt(2)*(-1-b*x^2)^(1/4)))/(2*Sqrt(2)*Sqrt(b))");

	}

	// {1/((-2*a+3*x^2)*(-a+3*x^2)^(1/4)), x, 1, -ArcTan((Sqrt(3/2)*x)/(a^(1/4)*(-a +
	// 3*x^2)^(1/4)))/(2*Sqrt(6)*a^(3/4))-ArcTanh((Sqrt(3/2)*x)/(a^(1/4)*(-a+3*x^2)^(1/4)))/(2*Sqrt(6)*a^(3/4))}
	public void test00783() {
		check("Integrate(1/((-2*a+3*x^2)*(-a+3*x^2)^(1/4)), x)",
				"-ArcTan((Sqrt(3/2)*x)/(a^(1/4)*(-a+3*x^2)^(1/4)))/(2*Sqrt(6)*a^(3/4))-ArcTanh((Sqrt(3/2)*x)/(a^(1/4)*(-a+3*x^2)^(1/4)))/(2*Sqrt(6)*a^(3/4))");

	}

	// {1/((-2*a-3*x^2)*(-a-3*x^2)^(1/4)), x, 1, -ArcTan((Sqrt(3/2)*x)/(a^(1/4)*(-a -
	// 3*x^2)^(1/4)))/(2*Sqrt(6)*a^(3/4))-ArcTanh((Sqrt(3/2)*x)/(a^(1/4)*(-a-3*x^2)^(1/4)))/(2*Sqrt(6)*a^(3/4))}
	public void test00784() {
		check("Integrate(1/((-2*a-3*x^2)*(-a-3*x^2)^(1/4)), x)",
				"-ArcTan((Sqrt(3/2)*x)/(a^(1/4)*(-a-3*x^2)^(1/4)))/(2*Sqrt(6)*a^(3/4))-ArcTanh((Sqrt(3/2)*x)/(a^(1/4)*(-a-3*x^2)^(1/4)))/(2*Sqrt(6)*a^(3/4))");

	}

	// {1/((-2*a+b*x^2)*(-a+b*x^2)^(1/4)), x, 1, -ArcTan((Sqrt(b)*x)/(Sqrt(2)*a^(1/4)*(-a +
	// b*x^2)^(1/4)))/(2*Sqrt(2)*a^(3/4)*Sqrt(b))-ArcTanh((Sqrt(b)*x)/(Sqrt(2)*a^(1/4)*(-a +
	// b*x^2)^(1/4)))/(2*Sqrt(2)*a^(3/4)*Sqrt(b))}
	public void test00785() {
		check("Integrate(1/((-2*a+b*x^2)*(-a+b*x^2)^(1/4)), x)",
				"-ArcTan((Sqrt(b)*x)/(Sqrt(2)*a^(1/4)*(-a+b*x^2)^(1/4)))/(2*Sqrt(2)*a^(3/4)*Sqrt(b))-ArcTanh((Sqrt(b)*x)/(Sqrt(2)*a^(1/4)*(-a+b*x^2)^(1/4)))/(2*Sqrt(2)*a^(3/4)*Sqrt(b))");

	}

	// {1/((-2*a-b*x^2)*(-a-b*x^2)^(1/4)), x, 1, -ArcTan((Sqrt(b)*x)/(Sqrt(2)*a^(1/4)*(-a -
	// b*x^2)^(1/4)))/(2*Sqrt(2)*a^(3/4)*Sqrt(b))-ArcTanh((Sqrt(b)*x)/(Sqrt(2)*a^(1/4)*(-a -
	// b*x^2)^(1/4)))/(2*Sqrt(2)*a^(3/4)*Sqrt(b))}
	public void test00786() {
		check("Integrate(1/((-2*a-b*x^2)*(-a-b*x^2)^(1/4)), x)",
				"-ArcTan((Sqrt(b)*x)/(Sqrt(2)*a^(1/4)*(-a-b*x^2)^(1/4)))/(2*Sqrt(2)*a^(3/4)*Sqrt(b))-ArcTanh((Sqrt(b)*x)/(Sqrt(2)*a^(1/4)*(-a-b*x^2)^(1/4)))/(2*Sqrt(2)*a^(3/4)*Sqrt(b))");

	}

	// {1/((2-x^2)*(-1+x^2)^(1/4)), x, 1, ArcTan(x/(Sqrt(2)*(-1+x^2)^(1/4)))/(2*Sqrt(2))+ArcTanh(x/(Sqrt(2)*(-1
	// +x^2)^(1/4)))/(2*Sqrt(2))}
	public void test00787() {
		check("Integrate(1/((2-x^2)*(-1+x^2)^(1/4)), x)",
				"ArcTan(x/(Sqrt(2)*(-1+x^2)^(1/4)))/(2*Sqrt(2))+ArcTanh(x/(Sqrt(2)*(-1+x^2)^(1/4)))/(2*Sqrt(2))");

	}

	// {(a+b*x^2)^(-1-(b*c)/(2*b*c-2*a*d))*(c+d*x^2)^(-1+(a*d)/(2*b*c-2*a*d)), x, 1, (x*(c +
	// d*x^2)^((a*d)/(2*b*c-2*a*d)))/(a*c*(a+b*x^2)^((b*c)/(2*b*c-2*a*d)))}
	public void test00788() {
		check("Integrate((a+b*x^2)^(-1-(b*c)/(2*b*c-2*a*d))*(c+d*x^2)^(-1+(a*d)/(2*b*c-2*a*d)), x)",
				"(x*(c+d*x^2)^((a*d)/(2*b*c-2*a*d)))/(a*c*(a+b*x^2)^((b*c)/(2*b*c-2*a*d)))");

	}

	// {(1+x^2)/(-1+x^2)^2, x, 1, x/(1-x^2)}
	public void test00789() {
		check("Integrate((1+x^2)/(-1+x^2)^2, x)", "x/(1-x^2)");

	}

	// {(1-x^2)/(1+x^2)^2, x, 1, x/(1+x^2)}
	public void test00790() {
		check("Integrate((1-x^2)/(1+x^2)^2, x)", "x/(1+x^2)");

	}

	// {(a+b*x^2)/(-a+b*x^2)^2, x, 1, x/(a-b*x^2)}
	public void test00791() {
		check("Integrate((a+b*x^2)/(-a+b*x^2)^2, x)", "x/(a-b*x^2)");

	}

	// {(a+b*x^2)/(a-b*x^2)^2, x, 1, x/(a-b*x^2)}
	public void test00792() {
		check("Integrate((a+b*x^2)/(a-b*x^2)^2, x)", "x/(a-b*x^2)");

	}

	// {(1+2*x^2)/(x^5*(1+x^2)^3), x, 1, -1/(4*x^4*(1+x^2)^2)}
	public void test00793() {
		check("Integrate((1+2*x^2)/(x^5*(1+x^2)^3), x)", "-1/(4*x^4*(1+x^2)^2)");

	}

	// {1/((1-x^2)^(1/3)*(3+x^2)), x, 1, ArcTan(Sqrt(3)/x)/(2*2^(2/3)*Sqrt(3))+ArcTan((Sqrt(3)*(1-2^(1/3)*(1 -
	// x^2)^(1/3)))/x)/(2*2^(2/3)*Sqrt(3))-ArcTanh(x)/(6*2^(2/3))+ArcTanh(x/(1+2^(1/3)*(1 -
	// x^2)^(1/3)))/(2*2^(2/3))}
	public void test00794() {
		check("Integrate(1/((1-x^2)^(1/3)*(3+x^2)), x)",
				"ArcTan(Sqrt(3)/x)/(2*2^(2/3)*Sqrt(3))+ArcTan((Sqrt(3)*(1-2^(1/3)*(1-x^2)^(1/3)))/x)/(2*2^(2/3)*Sqrt(3))-ArcTanh(x)/(6*2^(2/3))+ArcTanh(x/(1+2^(1/3)*(1-x^2)^(1/3)))/(2*2^(2/3))");

	}

	// {x/((2-3*x^2)^(1/4)*(4-3*x^2)), x, 1, ArcTan((Sqrt(2)-Sqrt(2-3*x^2))/(2^(3/4)*(2 -
	// 3*x^2)^(1/4)))/(3*2^(3/4))+ArcTanh((Sqrt(2)+Sqrt(2-3*x^2))/(2^(3/4)*(2-3*x^2)^(1/4)))/(3*2^(3/4))}
	public void test00795() {
		check("Integrate(x/((2-3*x^2)^(1/4)*(4-3*x^2)), x)",
				"ArcTan((Sqrt(2)-Sqrt(2-3*x^2))/(2^(3/4)*(2-3*x^2)^(1/4)))/(3*2^(3/4))+ArcTanh((Sqrt(2)+Sqrt(2-3*x^2))/(2^(3/4)*(2-3*x^2)^(1/4)))/(3*2^(3/4))");

	}

	// {1/((2-3*x^2)^(1/4)*(4-3*x^2)), x, 1, ArcTan((2-Sqrt(2)*Sqrt(2-3*x^2))/(2^(1/4)*Sqrt(3)*x*(2 -
	// 3*x^2)^(1/4)))/(2*2^(3/4)*Sqrt(3))+ArcTanh((2+Sqrt(2)*Sqrt(2-3*x^2))/(2^(1/4)*Sqrt(3)*x*(2 -
	// 3*x^2)^(1/4)))/(2*2^(3/4)*Sqrt(3))}
	public void test00796() {
		check("Integrate(1/((2-3*x^2)^(1/4)*(4-3*x^2)), x)",
				"ArcTan((2-Sqrt(2)*Sqrt(2-3*x^2))/(2^(1/4)*Sqrt(3)*x*(2-3*x^2)^(1/4)))/(2*2^(3/4)*Sqrt(3))+ArcTanh((2+Sqrt(2)*Sqrt(2-3*x^2))/(2^(1/4)*Sqrt(3)*x*(2-3*x^2)^(1/4)))/(2*2^(3/4)*Sqrt(3))");

	}

	// {1/((-2+3*x^2)*(-1+3*x^2)^(1/4)), x, 1, -ArcTan((Sqrt(3/2)*x)/(-1+3*x^2)^(1/4))/(2*Sqrt(6)) -
	// ArcTanh((Sqrt(3/2)*x)/(-1+3*x^2)^(1/4))/(2*Sqrt(6))}
	public void test00797() {
		check("Integrate(1/((-2+3*x^2)*(-1+3*x^2)^(1/4)), x)",
				"-ArcTan((Sqrt(3/2)*x)/(-1+3*x^2)^(1/4))/(2*Sqrt(6))-ArcTanh((Sqrt(3/2)*x)/(-1+3*x^2)^(1/4))/(2*Sqrt(6))");

	}

	// {x^2/((2+3*x^2)^(3/4)*(4+3*x^2)), x, 1, -ArcTan((2*2^(3/4)+2*2^(1/4)*Sqrt(2+3*x^2))/(2*Sqrt(3)*x*(2 +
	// 3*x^2)^(1/4)))/(3*2^(1/4)*Sqrt(3))+ArcTanh((2*2^(3/4)-2*2^(1/4)*Sqrt(2+3*x^2))/(2*Sqrt(3)*x*(2 +
	// 3*x^2)^(1/4)))/(3*2^(1/4)*Sqrt(3))}
	public void test00798() {
		check("Integrate(x^2/((2+3*x^2)^(3/4)*(4+3*x^2)), x)",
				"-ArcTan((2*2^(3/4)+2*2^(1/4)*Sqrt(2+3*x^2))/(2*Sqrt(3)*x*(2+3*x^2)^(1/4)))/(3*2^(1/4)*Sqrt(3))+ArcTanh((2*2^(3/4)-2*2^(1/4)*Sqrt(2+3*x^2))/(2*Sqrt(3)*x*(2+3*x^2)^(1/4)))/(3*2^(1/4)*Sqrt(3))");

	}

	// {x^2/((2-3*x^2)^(3/4)*(4-3*x^2)), x, 1, ArcTan((2-Sqrt(2)*Sqrt(2-3*x^2))/(2^(1/4)*Sqrt(3)*x*(2 -
	// 3*x^2)^(1/4)))/(3*2^(1/4)*Sqrt(3))-ArcTanh((2+Sqrt(2)*Sqrt(2-3*x^2))/(2^(1/4)*Sqrt(3)*x*(2 -
	// 3*x^2)^(1/4)))/(3*2^(1/4)*Sqrt(3))}
	public void test00799() {
		check("Integrate(x^2/((2-3*x^2)^(3/4)*(4-3*x^2)), x)",
				"ArcTan((2-Sqrt(2)*Sqrt(2-3*x^2))/(2^(1/4)*Sqrt(3)*x*(2-3*x^2)^(1/4)))/(3*2^(1/4)*Sqrt(3))-ArcTanh((2+Sqrt(2)*Sqrt(2-3*x^2))/(2^(1/4)*Sqrt(3)*x*(2-3*x^2)^(1/4)))/(3*2^(1/4)*Sqrt(3))");

	}

	// {x^2/((2+b*x^2)^(3/4)*(4+b*x^2)), x, 1, -(ArcTan((2*2^(3/4)+2*2^(1/4)*Sqrt(2+b*x^2))/(2*Sqrt(b)*x*(2 +
	// b*x^2)^(1/4)))/(2^(1/4)*b^(3/2)))+ArcTanh((2*2^(3/4)-2*2^(1/4)*Sqrt(2+b*x^2))/(2*Sqrt(b)*x*(2 +
	// b*x^2)^(1/4)))/(2^(1/4)*b^(3/2))}
	public void test00800() {
		check("Integrate(x^2/((2+b*x^2)^(3/4)*(4+b*x^2)), x)",
				"-(ArcTan((2*2^(3/4)+2*2^(1/4)*Sqrt(2+b*x^2))/(2*Sqrt(b)*x*(2+b*x^2)^(1/4)))/(2^(1/4)*b^(3/2)))+ArcTanh((2*2^(3/4)-2*2^(1/4)*Sqrt(2+b*x^2))/(2*Sqrt(b)*x*(2+b*x^2)^(1/4)))/(2^(1/4)*b^(3/2))");

	}

	// {x^2/((2-b*x^2)^(3/4)*(4-b*x^2)), x, 1, ArcTan((2-Sqrt(2)*Sqrt(2-b*x^2))/(2^(1/4)*Sqrt(b)*x*(2 -
	// b*x^2)^(1/4)))/(2^(1/4)*b^(3/2))-ArcTanh((2+Sqrt(2)*Sqrt(2-b*x^2))/(2^(1/4)*Sqrt(b)*x*(2 -
	// b*x^2)^(1/4)))/(2^(1/4)*b^(3/2))}
	public void test00801() {
		check("Integrate(x^2/((2-b*x^2)^(3/4)*(4-b*x^2)), x)",
				"ArcTan((2-Sqrt(2)*Sqrt(2-b*x^2))/(2^(1/4)*Sqrt(b)*x*(2-b*x^2)^(1/4)))/(2^(1/4)*b^(3/2))-ArcTanh((2+Sqrt(2)*Sqrt(2-b*x^2))/(2^(1/4)*Sqrt(b)*x*(2-b*x^2)^(1/4)))/(2^(1/4)*b^(3/2))");

	}

	// {x^2/((a+3*x^2)^(3/4)*(2*a+3*x^2)), x, 1, -ArcTan((a^(3/4)*(1+Sqrt(a+3*x^2)/Sqrt(a)))/(Sqrt(3)*x*(a +
	// 3*x^2)^(1/4)))/(3*Sqrt(3)*a^(1/4))+ArcTanh((a^(3/4)*(1-Sqrt(a+3*x^2)/Sqrt(a)))/(Sqrt(3)*x*(a +
	// 3*x^2)^(1/4)))/(3*Sqrt(3)*a^(1/4))}
	public void test00802() {
		check("Integrate(x^2/((a+3*x^2)^(3/4)*(2*a+3*x^2)), x)",
				"-ArcTan((a^(3/4)*(1+Sqrt(a+3*x^2)/Sqrt(a)))/(Sqrt(3)*x*(a+3*x^2)^(1/4)))/(3*Sqrt(3)*a^(1/4))+ArcTanh((a^(3/4)*(1-Sqrt(a+3*x^2)/Sqrt(a)))/(Sqrt(3)*x*(a+3*x^2)^(1/4)))/(3*Sqrt(3)*a^(1/4))");

	}

	// {x^2/((a-3*x^2)^(3/4)*(2*a-3*x^2)), x, 1, ArcTan((a^(3/4)*(1-Sqrt(a-3*x^2)/Sqrt(a)))/(Sqrt(3)*x*(a -
	// 3*x^2)^(1/4)))/(3*Sqrt(3)*a^(1/4))-ArcTanh((a^(3/4)*(1+Sqrt(a-3*x^2)/Sqrt(a)))/(Sqrt(3)*x*(a -
	// 3*x^2)^(1/4)))/(3*Sqrt(3)*a^(1/4))}
	public void test00803() {
		check("Integrate(x^2/((a-3*x^2)^(3/4)*(2*a-3*x^2)), x)",
				"ArcTan((a^(3/4)*(1-Sqrt(a-3*x^2)/Sqrt(a)))/(Sqrt(3)*x*(a-3*x^2)^(1/4)))/(3*Sqrt(3)*a^(1/4))-ArcTanh((a^(3/4)*(1+Sqrt(a-3*x^2)/Sqrt(a)))/(Sqrt(3)*x*(a-3*x^2)^(1/4)))/(3*Sqrt(3)*a^(1/4))");

	}

	// {x^2/((a+b*x^2)^(3/4)*(2*a+b*x^2)), x, 1, -(ArcTan((a^(3/4)*(1+Sqrt(a+b*x^2)/Sqrt(a)))/(Sqrt(b)*x*(a +
	// b*x^2)^(1/4)))/(a^(1/4)*b^(3/2)))+ArcTanh((a^(3/4)*(1-Sqrt(a+b*x^2)/Sqrt(a)))/(Sqrt(b)*x*(a +
	// b*x^2)^(1/4)))/(a^(1/4)*b^(3/2))}
	public void test00804() {
		check("Integrate(x^2/((a+b*x^2)^(3/4)*(2*a+b*x^2)), x)",
				"-(ArcTan((a^(3/4)*(1+Sqrt(a+b*x^2)/Sqrt(a)))/(Sqrt(b)*x*(a+b*x^2)^(1/4)))/(a^(1/4)*b^(3/2)))+ArcTanh((a^(3/4)*(1-Sqrt(a+b*x^2)/Sqrt(a)))/(Sqrt(b)*x*(a+b*x^2)^(1/4)))/(a^(1/4)*b^(3/2))");

	}

	// {x^2/((a-b*x^2)^(3/4)*(2*a-b*x^2)), x, 1, ArcTan((a^(3/4)*(1-Sqrt(a-b*x^2)/Sqrt(a)))/(Sqrt(b)*x*(a -
	// b*x^2)^(1/4)))/(a^(1/4)*b^(3/2))-ArcTanh((a^(3/4)*(1+Sqrt(a-b*x^2)/Sqrt(a)))/(Sqrt(b)*x*(a -
	// b*x^2)^(1/4)))/(a^(1/4)*b^(3/2))}
	public void test00805() {
		check("Integrate(x^2/((a-b*x^2)^(3/4)*(2*a-b*x^2)), x)",
				"ArcTan((a^(3/4)*(1-Sqrt(a-b*x^2)/Sqrt(a)))/(Sqrt(b)*x*(a-b*x^2)^(1/4)))/(a^(1/4)*b^(3/2))-ArcTanh((a^(3/4)*(1+Sqrt(a-b*x^2)/Sqrt(a)))/(Sqrt(b)*x*(a-b*x^2)^(1/4)))/(a^(1/4)*b^(3/2))");

	}

	// {x^2/((2-3*x^2)^(3/4)*(4-3*x^2)), x, 1, ArcTan((2-Sqrt(2)*Sqrt(2-3*x^2))/(2^(1/4)*Sqrt(3)*x*(2 -
	// 3*x^2)^(1/4)))/(3*2^(1/4)*Sqrt(3))-ArcTanh((2+Sqrt(2)*Sqrt(2-3*x^2))/(2^(1/4)*Sqrt(3)*x*(2 -
	// 3*x^2)^(1/4)))/(3*2^(1/4)*Sqrt(3))}
	public void test00806() {
		check("Integrate(x^2/((2-3*x^2)^(3/4)*(4-3*x^2)), x)",
				"ArcTan((2-Sqrt(2)*Sqrt(2-3*x^2))/(2^(1/4)*Sqrt(3)*x*(2-3*x^2)^(1/4)))/(3*2^(1/4)*Sqrt(3))-ArcTanh((2+Sqrt(2)*Sqrt(2-3*x^2))/(2^(1/4)*Sqrt(3)*x*(2-3*x^2)^(1/4)))/(3*2^(1/4)*Sqrt(3))");

	}

	// {x^2/((-2+3*x^2)*(-1+3*x^2)^(3/4)), x, 1, ArcTan((Sqrt(3/2)*x)/(-1+3*x^2)^(1/4))/(3*Sqrt(6)) -
	// ArcTanh((Sqrt(3/2)*x)/(-1+3*x^2)^(1/4))/(3*Sqrt(6))}
	public void test00807() {
		check("Integrate(x^2/((-2+3*x^2)*(-1+3*x^2)^(3/4)), x)",
				"ArcTan((Sqrt(3/2)*x)/(-1+3*x^2)^(1/4))/(3*Sqrt(6))-ArcTanh((Sqrt(3/2)*x)/(-1+3*x^2)^(1/4))/(3*Sqrt(6))");

	}

	// {x^2/((-2-3*x^2)*(-1-3*x^2)^(3/4)), x, 1, ArcTan((Sqrt(3/2)*x)/(-1-3*x^2)^(1/4))/(3*Sqrt(6)) -
	// ArcTanh((Sqrt(3/2)*x)/(-1-3*x^2)^(1/4))/(3*Sqrt(6))}
	public void test00808() {
		check("Integrate(x^2/((-2-3*x^2)*(-1-3*x^2)^(3/4)), x)",
				"ArcTan((Sqrt(3/2)*x)/(-1-3*x^2)^(1/4))/(3*Sqrt(6))-ArcTanh((Sqrt(3/2)*x)/(-1-3*x^2)^(1/4))/(3*Sqrt(6))");

	}

	// {x^2/((-2+b*x^2)*(-1+b*x^2)^(3/4)), x, 1, ArcTan((Sqrt(b)*x)/(Sqrt(2)*(-1+b*x^2)^(1/4)))/(Sqrt(2)*b^(3/2))
	// -ArcTanh((Sqrt(b)*x)/(Sqrt(2)*(-1+b*x^2)^(1/4)))/(Sqrt(2)*b^(3/2))}
	public void test00809() {
		check("Integrate(x^2/((-2+b*x^2)*(-1+b*x^2)^(3/4)), x)",
				"ArcTan((Sqrt(b)*x)/(Sqrt(2)*(-1+b*x^2)^(1/4)))/(Sqrt(2)*b^(3/2))-ArcTanh((Sqrt(b)*x)/(Sqrt(2)*(-1+b*x^2)^(1/4)))/(Sqrt(2)*b^(3/2))");

	}

	// {x^2/((-2-b*x^2)*(-1-b*x^2)^(3/4)), x, 1, ArcTan((Sqrt(b)*x)/(Sqrt(2)*(-1-b*x^2)^(1/4)))/(Sqrt(2)*b^(3/2))
	// -ArcTanh((Sqrt(b)*x)/(Sqrt(2)*(-1-b*x^2)^(1/4)))/(Sqrt(2)*b^(3/2))}
	public void test00810() {
		check("Integrate(x^2/((-2-b*x^2)*(-1-b*x^2)^(3/4)), x)",
				"ArcTan((Sqrt(b)*x)/(Sqrt(2)*(-1-b*x^2)^(1/4)))/(Sqrt(2)*b^(3/2))-ArcTanh((Sqrt(b)*x)/(Sqrt(2)*(-1-b*x^2)^(1/4)))/(Sqrt(2)*b^(3/2))");

	}

	// {x^2/((-2*a+3*x^2)*(-a+3*x^2)^(3/4)), x, 1, ArcTan((Sqrt(3/2)*x)/(a^(1/4)*(-a +
	// 3*x^2)^(1/4)))/(3*Sqrt(6)*a^(1/4))-ArcTanh((Sqrt(3/2)*x)/(a^(1/4)*(-a+3*x^2)^(1/4)))/(3*Sqrt(6)*a^(1/4))}
	public void test00811() {
		check("Integrate(x^2/((-2*a+3*x^2)*(-a+3*x^2)^(3/4)), x)",
				"ArcTan((Sqrt(3/2)*x)/(a^(1/4)*(-a+3*x^2)^(1/4)))/(3*Sqrt(6)*a^(1/4))-ArcTanh((Sqrt(3/2)*x)/(a^(1/4)*(-a+3*x^2)^(1/4)))/(3*Sqrt(6)*a^(1/4))");

	}

	// {x^2/((-2*a-3*x^2)*(-a-3*x^2)^(3/4)), x, 1, ArcTan((Sqrt(3/2)*x)/(a^(1/4)*(-a -
	// 3*x^2)^(1/4)))/(3*Sqrt(6)*a^(1/4))-ArcTanh((Sqrt(3/2)*x)/(a^(1/4)*(-a-3*x^2)^(1/4)))/(3*Sqrt(6)*a^(1/4))}
	public void test00812() {
		check("Integrate(x^2/((-2*a-3*x^2)*(-a-3*x^2)^(3/4)), x)",
				"ArcTan((Sqrt(3/2)*x)/(a^(1/4)*(-a-3*x^2)^(1/4)))/(3*Sqrt(6)*a^(1/4))-ArcTanh((Sqrt(3/2)*x)/(a^(1/4)*(-a-3*x^2)^(1/4)))/(3*Sqrt(6)*a^(1/4))");

	}

	// {x^2/((-2*a+b*x^2)*(-a+b*x^2)^(3/4)), x, 1, ArcTan((Sqrt(b)*x)/(Sqrt(2)*a^(1/4)*(-a +
	// b*x^2)^(1/4)))/(Sqrt(2)*a^(1/4)*b^(3/2))-ArcTanh((Sqrt(b)*x)/(Sqrt(2)*a^(1/4)*(-a +
	// b*x^2)^(1/4)))/(Sqrt(2)*a^(1/4)*b^(3/2))}
	public void test00813() {
		check("Integrate(x^2/((-2*a+b*x^2)*(-a+b*x^2)^(3/4)), x)",
				"ArcTan((Sqrt(b)*x)/(Sqrt(2)*a^(1/4)*(-a+b*x^2)^(1/4)))/(Sqrt(2)*a^(1/4)*b^(3/2))-ArcTanh((Sqrt(b)*x)/(Sqrt(2)*a^(1/4)*(-a+b*x^2)^(1/4)))/(Sqrt(2)*a^(1/4)*b^(3/2))");

	}

	// {x^2/((-2*a-b*x^2)*(-a-b*x^2)^(3/4)), x, 1, ArcTan((Sqrt(b)*x)/(Sqrt(2)*a^(1/4)*(-a -
	// b*x^2)^(1/4)))/(Sqrt(2)*a^(1/4)*b^(3/2))-ArcTanh((Sqrt(b)*x)/(Sqrt(2)*a^(1/4)*(-a -
	// b*x^2)^(1/4)))/(Sqrt(2)*a^(1/4)*b^(3/2))}
	public void test00814() {
		check("Integrate(x^2/((-2*a-b*x^2)*(-a-b*x^2)^(3/4)), x)",
				"ArcTan((Sqrt(b)*x)/(Sqrt(2)*a^(1/4)*(-a-b*x^2)^(1/4)))/(Sqrt(2)*a^(1/4)*b^(3/2))-ArcTanh((Sqrt(b)*x)/(Sqrt(2)*a^(1/4)*(-a-b*x^2)^(1/4)))/(Sqrt(2)*a^(1/4)*b^(3/2))");

	}

	// {x^2/((-2+3*x^2)*(-1+3*x^2)^(3/4)), x, 1, ArcTan((Sqrt(3/2)*x)/(-1+3*x^2)^(1/4))/(3*Sqrt(6)) -
	// ArcTanh((Sqrt(3/2)*x)/(-1+3*x^2)^(1/4))/(3*Sqrt(6))}
	public void test00815() {
		check("Integrate(x^2/((-2+3*x^2)*(-1+3*x^2)^(3/4)), x)",
				"ArcTan((Sqrt(3/2)*x)/(-1+3*x^2)^(1/4))/(3*Sqrt(6))-ArcTanh((Sqrt(3/2)*x)/(-1+3*x^2)^(1/4))/(3*Sqrt(6))");

	}

	// {Sqrt(e+f*x^2)/((a+b*x^2)*Sqrt(c+d*x^2)), x, 1, (e^(3/2)*Sqrt(c+d*x^2)*EllipticPi(1-(b*e)/(a*f),
	// ArcTan((Sqrt(f)*x)/Sqrt(e)), 1-(d*e)/(c*f)))/(a*c*Sqrt(f)*Sqrt((e*(c+d*x^2))/(c*(e+f*x^2)))*Sqrt(e +
	// f*x^2))}
	public void test00816() {
		check("Integrate(Sqrt(e+f*x^2)/((a+b*x^2)*Sqrt(c+d*x^2)), x)",
				"(e^(3/2)*Sqrt(c+d*x^2)*EllipticPi(1-(b*e)/(a*f), ArcTan((Sqrt(f)*x)/Sqrt(e)), 1-(d*e)/(c*f)))/(a*c*Sqrt(f)*Sqrt((e*(c+d*x^2))/(c*(e+f*x^2)))*Sqrt(e+f*x^2))");

	}

	// {Sqrt(c+d*x^2)/((a+b*x^2)*Sqrt(e+f*x^2)), x, 1, (c^(3/2)*Sqrt(e+f*x^2)*EllipticPi(1-(b*c)/(a*d),
	// ArcTan((Sqrt(d)*x)/Sqrt(c)), 1-(c*f)/(d*e)))/(a*Sqrt(d)*e*Sqrt(c+d*x^2)*Sqrt((c*(e+f*x^2))/(e*(c +
	// d*x^2))))}
	public void test00817() {
		check("Integrate(Sqrt(c+d*x^2)/((a+b*x^2)*Sqrt(e+f*x^2)), x)",
				"(c^(3/2)*Sqrt(e+f*x^2)*EllipticPi(1-(b*c)/(a*d), ArcTan((Sqrt(d)*x)/Sqrt(c)), 1-(c*f)/(d*e)))/(a*Sqrt(d)*e*Sqrt(c+d*x^2)*Sqrt((c*(e+f*x^2))/(e*(c+d*x^2))))");

	}

	// {Sqrt(2+x^2)/(Sqrt(1+x^2)*(a+b*x^2)), x, 1, (2*Sqrt(1+x^2)*EllipticPi(1-(2*b)/a, ArcTan(x/Sqrt(2)),
	// -1))/(a*Sqrt((1+x^2)/(2+x^2))*Sqrt(2+x^2))}
	public void test00818() {
		check("Integrate(Sqrt(2+x^2)/(Sqrt(1+x^2)*(a+b*x^2)), x)",
				"(2*Sqrt(1+x^2)*EllipticPi(1-(2*b)/a, ArcTan(x/Sqrt(2)), -1))/(a*Sqrt((1+x^2)/(2+x^2))*Sqrt(2+x^2))");

	}

	// {Sqrt(2+d*x^2)/((a+b*x^2)*Sqrt(3+f*x^2)), x, 1, (2*Sqrt(3+f*x^2)*EllipticPi(1-(2*b)/(a*d),
	// ArcTan((Sqrt(d)*x)/Sqrt(2)), 1-(2*f)/(3*d)))/(Sqrt(3)*a*Sqrt(d)*Sqrt(2+d*x^2)*Sqrt((3+f*x^2)/(2+d*x^2)))}
	public void test00819() {
		check("Integrate(Sqrt(2+d*x^2)/((a+b*x^2)*Sqrt(3+f*x^2)), x)",
				"(2*Sqrt(3+f*x^2)*EllipticPi(1-(2*b)/(a*d), ArcTan((Sqrt(d)*x)/Sqrt(2)), 1-(2*f)/(3*d)))/(Sqrt(3)*a*Sqrt(d)*Sqrt(2+d*x^2)*Sqrt((3+f*x^2)/(2+d*x^2)))");

	}

	// {1/((a+b*x^2)*Sqrt(2+d*x^2)*Sqrt(3+f*x^2)), x, 1, EllipticPi((2*b)/(a*d), ArcSin((Sqrt(-d)*x)/Sqrt(2)),
	// (2*f)/(3*d))/(Sqrt(3)*a*Sqrt(-d))}
	public void test00820() {
		check("Integrate(1/((a+b*x^2)*Sqrt(2+d*x^2)*Sqrt(3+f*x^2)), x)",
				"EllipticPi((2*b)/(a*d), ArcSin((Sqrt(-d)*x)/Sqrt(2)), (2*f)/(3*d))/(Sqrt(3)*a*Sqrt(-d))");

	}

	// {(A+B*x)/(a+b*x^2)^(3/2), x, 1, -((a*B-A*b*x)/(a*b*Sqrt(a+b*x^2)))}
	public void test00821() {
		check("Integrate((A+B*x)/(a+b*x^2)^(3/2), x)", "-((a*B-A*b*x)/(a*b*Sqrt(a+b*x^2)))");

	}

	// {Sqrt(b*x), x, 1, (2*(b*x)^(3/2))/(3*b)}
	public void test00822() {
		check("Integrate(Sqrt(b*x), x)", "(2*(b*x)^(3/2))/(3*b)");

	}

	// {(b*x)^(3/2), x, 1, (2*(b*x)^(5/2))/(5*b)}
	public void test00823() {
		check("Integrate((b*x)^(3/2), x)", "(2*(b*x)^(5/2))/(5*b)");

	}

	// {1/Sqrt(b*x), x, 1, (2*Sqrt(b*x))/b}
	public void test00824() {
		check("Integrate(1/Sqrt(b*x), x)", "(2*Sqrt(b*x))/b");

	}

	// {(b*x)^(-3/2), x, 1, -2/(b*Sqrt(b*x))}
	public void test00825() {
		check("Integrate((b*x)^(-3/2), x)", "-2/(b*Sqrt(b*x))");

	}

	// {(b*x)^(1/3), x, 1, (3*(b*x)^(4/3))/(4*b)}
	public void test00826() {
		check("Integrate((b*x)^(1/3), x)", "(3*(b*x)^(4/3))/(4*b)");

	}

	// {(b*x)^(2/3), x, 1, (3*(b*x)^(5/3))/(5*b)}
	public void test00827() {
		check("Integrate((b*x)^(2/3), x)", "(3*(b*x)^(5/3))/(5*b)");

	}

	// {(b*x)^(-1/3), x, 1, (3*(b*x)^(2/3))/(2*b)}
	public void test00828() {
		check("Integrate((b*x)^(-1/3), x)", "(3*(b*x)^(2/3))/(2*b)");

	}

	// {(b*x)^(-2/3), x, 1, (3*(b*x)^(1/3))/b}
	public void test00829() {
		check("Integrate((b*x)^(-2/3), x)", "(3*(b*x)^(1/3))/b");

	}

	// {a+b*x^3, x, 1, a*x+(b*x^4)/4}
	public void test00830() {
		check("Integrate(a+b*x^3, x)", "a*x+(b*x^4)/4");

	}

	// {x^2*(a+b*x^3)^2, x, 1, (a+b*x^3)^3/(9*b)}
	public void test00831() {
		check("Integrate(x^2*(a+b*x^3)^2, x)", "(a+b*x^3)^3/(9*b)");

	}

	// {(a+b*x^3)^2/x^10, x, 1, -(a+b*x^3)^3/(9*a*x^9)}
	public void test00832() {
		check("Integrate((a+b*x^3)^2/x^10, x)", "-(a+b*x^3)^3/(9*a*x^9)");

	}

	// {x^2*(a+b*x^3)^3, x, 1, (a+b*x^3)^4/(12*b)}
	public void test00833() {
		check("Integrate(x^2*(a+b*x^3)^3, x)", "(a+b*x^3)^4/(12*b)");

	}

	// {(a+b*x^3)^3/x^13, x, 1, -(a+b*x^3)^4/(12*a*x^12)}
	public void test00834() {
		check("Integrate((a+b*x^3)^3/x^13, x)", "-(a+b*x^3)^4/(12*a*x^12)");

	}

	// {x^2*(a+b*x^3)^5, x, 1, (a+b*x^3)^6/(18*b)}
	public void test00835() {
		check("Integrate(x^2*(a+b*x^3)^5, x)", "(a+b*x^3)^6/(18*b)");

	}

	// {(a+b*x^3)^5/x^19, x, 1, -(a+b*x^3)^6/(18*a*x^18)}
	public void test00836() {
		check("Integrate((a+b*x^3)^5/x^19, x)", "-(a+b*x^3)^6/(18*a*x^18)");

	}

	// {x^2*(a+b*x^3)^8, x, 1, (a+b*x^3)^9/(27*b)}
	public void test00837() {
		check("Integrate(x^2*(a+b*x^3)^8, x)", "(a+b*x^3)^9/(27*b)");

	}

	// {(a+b*x^3)^8/x^28, x, 1, -(a+b*x^3)^9/(27*a*x^27)}
	public void test00838() {
		check("Integrate((a+b*x^3)^8/x^28, x)", "-(a+b*x^3)^9/(27*a*x^27)");

	}

	// {x^2/(a+b*x^3), x, 1, Log(a+b*x^3)/(3*b)}
	public void test00839() {
		check("Integrate(x^2/(a+b*x^3), x)", "Log(a+b*x^3)/(3*b)");

	}

	// {x^2/(a+b*x^3)^2, x, 1, -1/(3*b*(a+b*x^3))}
	public void test00840() {
		check("Integrate(x^2/(a+b*x^3)^2, x)", "-1/(3*b*(a+b*x^3))");

	}

	// {x^5/(a+b*x^3)^3, x, 1, x^6/(6*a*(a+b*x^3)^2)}
	public void test00841() {
		check("Integrate(x^5/(a+b*x^3)^3, x)", "x^6/(6*a*(a+b*x^3)^2)");

	}

	// {x^2/(a+b*x^3)^3, x, 1, -1/(6*b*(a+b*x^3)^2)}
	public void test00842() {
		check("Integrate(x^2/(a+b*x^3)^3, x)", "-1/(6*b*(a+b*x^3)^2)");

	}

	// {x^2/(a-b*x^3), x, 1, -Log(a-b*x^3)/(3*b)}
	public void test00843() {
		check("Integrate(x^2/(a-b*x^3), x)", "-Log(a-b*x^3)/(3*b)");

	}

	// {x^2*Sqrt(a+b*x^3), x, 1, (2*(a+b*x^3)^(3/2))/(9*b)}
	public void test00844() {
		check("Integrate(x^2*Sqrt(a+b*x^3), x)", "(2*(a+b*x^3)^(3/2))/(9*b)");

	}

	// {x^2*(a+b*x^3)^(3/2), x, 1, (2*(a+b*x^3)^(5/2))/(15*b)}
	public void test00845() {
		check("Integrate(x^2*(a+b*x^3)^(3/2), x)", "(2*(a+b*x^3)^(5/2))/(15*b)");

	}

	// {x^2/Sqrt(a+b*x^3), x, 1, (2*Sqrt(a+b*x^3))/(3*b)}
	public void test00846() {
		check("Integrate(x^2/Sqrt(a+b*x^3), x)", "(2*Sqrt(a+b*x^3))/(3*b)");

	}

	// {1/Sqrt(a+b*x^3), x, 1, (2*Sqrt(2+Sqrt(3))*(a^(1/3)+b^(1/3)*x)*Sqrt((a^(2/3)-a^(1/3)*b^(1/3)*x +
	// b^(2/3)*x^2)/((1+Sqrt(3))*a^(1/3)+b^(1/3)*x)^2)*EllipticF(ArcSin(((1-Sqrt(3))*a^(1/3)+b^(1/3)*x)/((1 +
	// Sqrt(3))*a^(1/3)+b^(1/3)*x)), -7-4*Sqrt(3)))/(3^(1/4)*b^(1/3)*Sqrt((a^(1/3)*(a^(1/3)+b^(1/3)*x))/((1 +
	// Sqrt(3))*a^(1/3)+b^(1/3)*x)^2)*Sqrt(a+b*x^3))}
	public void test00847() {
		check("Integrate(1/Sqrt(a+b*x^3), x)",
				"(2*Sqrt(2+Sqrt(3))*(a^(1/3)+b^(1/3)*x)*Sqrt((a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/((1+Sqrt(3))*a^(1/3)+b^(1/3)*x)^2)*EllipticF(ArcSin(((1-Sqrt(3))*a^(1/3)+b^(1/3)*x)/((1+Sqrt(3))*a^(1/3)+b^(1/3)*x)), -7-4*Sqrt(3)))/(3^(1/4)*b^(1/3)*Sqrt((a^(1/3)*(a^(1/3)+b^(1/3)*x))/((1+Sqrt(3))*a^(1/3)+b^(1/3)*x)^2)*Sqrt(a+b*x^3))");

	}

	// {x^2/(a+b*x^3)^(3/2), x, 1, -2/(3*b*Sqrt(a+b*x^3))}
	public void test00848() {
		check("Integrate(x^2/(a+b*x^3)^(3/2), x)", "-2/(3*b*Sqrt(a+b*x^3))");

	}

	// {x^2/Sqrt(1+x^3), x, 1, (2*Sqrt(1+x^3))/3}
	public void test00849() {
		check("Integrate(x^2/Sqrt(1+x^3), x)", "(2*Sqrt(1+x^3))/3");

	}

	// {1/Sqrt(1+x^3), x, 1, (2*Sqrt(2+Sqrt(3))*(1+x)*Sqrt((1-x+x^2)/(1+Sqrt(3)+x)^2)*EllipticF(ArcSin((1
	// -Sqrt(3)+x)/(1+Sqrt(3)+x)), -7-4*Sqrt(3)))/(3^(1/4)*Sqrt((1+x)/(1+Sqrt(3)+x)^2)*Sqrt(1+x^3))}
	public void test00850() {
		check("Integrate(1/Sqrt(1+x^3), x)",
				"(2*Sqrt(2+Sqrt(3))*(1+x)*Sqrt((1-x+x^2)/(1+Sqrt(3)+x)^2)*EllipticF(ArcSin((1-Sqrt(3)+x)/(1+Sqrt(3)+x)), -7-4*Sqrt(3)))/(3^(1/4)*Sqrt((1+x)/(1+Sqrt(3)+x)^2)*Sqrt(1+x^3))");

	}

	// {x^2/Sqrt(1-x^3), x, 1, (-2*Sqrt(1-x^3))/3}
	public void test00851() {
		check("Integrate(x^2/Sqrt(1-x^3), x)", "(-2*Sqrt(1-x^3))/3");

	}

	// {1/Sqrt(1-x^3), x, 1, (-2*Sqrt(2+Sqrt(3))*(1-x)*Sqrt((1+x+x^2)/(1+Sqrt(3)-x)^2)*EllipticF(ArcSin((1
	// -Sqrt(3)-x)/(1+Sqrt(3)-x)), -7-4*Sqrt(3)))/(3^(1/4)*Sqrt((1-x)/(1+Sqrt(3)-x)^2)*Sqrt(1-x^3))}
	public void test00852() {
		check("Integrate(1/Sqrt(1-x^3), x)",
				"(-2*Sqrt(2+Sqrt(3))*(1-x)*Sqrt((1+x+x^2)/(1+Sqrt(3)-x)^2)*EllipticF(ArcSin((1-Sqrt(3)-x)/(1+Sqrt(3)-x)), -7-4*Sqrt(3)))/(3^(1/4)*Sqrt((1-x)/(1+Sqrt(3)-x)^2)*Sqrt(1-x^3))");

	}

	// {x^2/Sqrt(-1+x^3), x, 1, (2*Sqrt(-1+x^3))/3}
	public void test00853() {
		check("Integrate(x^2/Sqrt(-1+x^3), x)", "(2*Sqrt(-1+x^3))/3");

	}

	// {1/Sqrt(-1+x^3), x, 1, (-2*Sqrt(2-Sqrt(3))*(1-x)*Sqrt((1+x+x^2)/(1-Sqrt(3) -
	// x)^2)*EllipticF(ArcSin((1+Sqrt(3)-x)/(1-Sqrt(3)-x)), -7+4*Sqrt(3)))/(3^(1/4)*Sqrt(-((1-x)/(1 -
	// Sqrt(3)-x)^2))*Sqrt(-1+x^3))}
	public void test00854() {
		check("Integrate(1/Sqrt(-1+x^3), x)",
				"(-2*Sqrt(2-Sqrt(3))*(1-x)*Sqrt((1+x+x^2)/(1-Sqrt(3)-x)^2)*EllipticF(ArcSin((1+Sqrt(3)-x)/(1-Sqrt(3)-x)), -7+4*Sqrt(3)))/(3^(1/4)*Sqrt(-((1-x)/(1-Sqrt(3)-x)^2))*Sqrt(-1+x^3))");

	}

	// {x^2/Sqrt(-1-x^3), x, 1, (-2*Sqrt(-1-x^3))/3}
	public void test00855() {
		check("Integrate(x^2/Sqrt(-1-x^3), x)", "(-2*Sqrt(-1-x^3))/3");

	}

	// {1/Sqrt(-1-x^3), x, 1, (2*Sqrt(2-Sqrt(3))*(1+x)*Sqrt((1-x+x^2)/(1-Sqrt(3)+x)^2)*EllipticF(ArcSin((1
	// +Sqrt(3)+x)/(1-Sqrt(3)+x)), -7+4*Sqrt(3)))/(3^(1/4)*Sqrt(-((1+x)/(1-Sqrt(3)+x)^2))*Sqrt(-1 -
	// x^3))}
	public void test00856() {
		check("Integrate(1/Sqrt(-1-x^3), x)",
				"(2*Sqrt(2-Sqrt(3))*(1+x)*Sqrt((1-x+x^2)/(1-Sqrt(3)+x)^2)*EllipticF(ArcSin((1+Sqrt(3)+x)/(1-Sqrt(3)+x)), -7+4*Sqrt(3)))/(3^(1/4)*Sqrt(-((1+x)/(1-Sqrt(3)+x)^2))*Sqrt(-1-x^3))");

	}

	// {x^2*(a+b*x^3)^(1/3), x, 1, (a+b*x^3)^(4/3)/(4*b)}
	public void test00857() {
		check("Integrate(x^2*(a+b*x^3)^(1/3), x)", "(a+b*x^3)^(4/3)/(4*b)");

	}

	// {(a+b*x^3)^(1/3)/x^5, x, 1, -(a+b*x^3)^(4/3)/(4*a*x^4)}
	public void test00858() {
		check("Integrate((a+b*x^3)^(1/3)/x^5, x)", "-(a+b*x^3)^(4/3)/(4*a*x^4)");

	}

	// {x^2*(a+b*x^3)^(2/3), x, 1, (a+b*x^3)^(5/3)/(5*b)}
	public void test00859() {
		check("Integrate(x^2*(a+b*x^3)^(2/3), x)", "(a+b*x^3)^(5/3)/(5*b)");

	}

	// {(a+b*x^3)^(2/3)/x^6, x, 1, -(a+b*x^3)^(5/3)/(5*a*x^5)}
	public void test00860() {
		check("Integrate((a+b*x^3)^(2/3)/x^6, x)", "-(a+b*x^3)^(5/3)/(5*a*x^5)");

	}

	// {x^2/(a+b*x^3)^(1/3), x, 1, (a+b*x^3)^(2/3)/(2*b)}
	public void test00861() {
		check("Integrate(x^2/(a+b*x^3)^(1/3), x)", "(a+b*x^3)^(2/3)/(2*b)");

	}

	// {(a+b*x^3)^(-1/3), x, 1, ArcTan((1+(2*b^(1/3)*x)/(a+b*x^3)^(1/3))/Sqrt(3))/(Sqrt(3)*b^(1/3)) -
	// Log(-(b^(1/3)*x)+(a+b*x^3)^(1/3))/(2*b^(1/3))}
	public void test00862() {
		check("Integrate((a+b*x^3)^(-1/3), x)",
				"ArcTan((1+(2*b^(1/3)*x)/(a+b*x^3)^(1/3))/Sqrt(3))/(Sqrt(3)*b^(1/3))-Log(-(b^(1/3)*x)+(a+b*x^3)^(1/3))/(2*b^(1/3))");

	}

	// {1/(x^3*(a+b*x^3)^(1/3)), x, 1, -(a+b*x^3)^(2/3)/(2*a*x^2)}
	public void test00863() {
		check("Integrate(1/(x^3*(a+b*x^3)^(1/3)), x)", "-(a+b*x^3)^(2/3)/(2*a*x^2)");

	}

	// {x^2/(a+b*x^3)^(2/3), x, 1, (a+b*x^3)^(1/3)/b}
	public void test00864() {
		check("Integrate(x^2/(a+b*x^3)^(2/3), x)", "(a+b*x^3)^(1/3)/b");

	}

	// {1/(x^2*(a+b*x^3)^(2/3)), x, 1, -((a+b*x^3)^(1/3)/(a*x))}
	public void test00865() {
		check("Integrate(1/(x^2*(a+b*x^3)^(2/3)), x)", "-((a+b*x^3)^(1/3)/(a*x))");

	}

	// {(a-b*x^3)^(-1/3), x, 1, -(ArcTan((1-(2*b^(1/3)*x)/(a-b*x^3)^(1/3))/Sqrt(3))/(Sqrt(3)*b^(1/3))) +
	// Log(b^(1/3)*x+(a-b*x^3)^(1/3))/(2*b^(1/3))}
	public void test00866() {
		check("Integrate((a-b*x^3)^(-1/3), x)",
				"-(ArcTan((1-(2*b^(1/3)*x)/(a-b*x^3)^(1/3))/Sqrt(3))/(Sqrt(3)*b^(1/3)))+Log(b^(1/3)*x+(a-b*x^3)^(1/3))/(2*b^(1/3))");

	}

	// {(2+x^3)^(-1/3), x, 1, ArcTan((1+(2*x)/(2+x^3)^(1/3))/Sqrt(3))/Sqrt(3)-Log(-x+(2+x^3)^(1/3))/2}
	public void test00867() {
		check("Integrate((2+x^3)^(-1/3), x)",
				"ArcTan((1+(2*x)/(2+x^3)^(1/3))/Sqrt(3))/Sqrt(3)-Log(-x+(2+x^3)^(1/3))/2");

	}

	// {x^2/(2+x^3)^(1/4), x, 1, (4*(2+x^3)^(3/4))/9}
	public void test00868() {
		check("Integrate(x^2/(2+x^3)^(1/4), x)", "(4*(2+x^3)^(3/4))/9");

	}

	// {x^m/(a+b*x^3), x, 1, (x^(1+m)*Hypergeometric2F1(1, (1+m)/3, (4+m)/3, -((b*x^3)/a)))/(a*(1+m))}
	public void test00869() {
		check("Integrate(x^m/(a+b*x^3), x)",
				"(x^(1+m)*Hypergeometric2F1(1, (1+m)/3, (4+m)/3, -((b*x^3)/a)))/(a*(1+m))");

	}

	// {x^m/(a+b*x^3)^2, x, 1, (x^(1+m)*Hypergeometric2F1(2, (1+m)/3, (4+m)/3, -((b*x^3)/a)))/(a^2*(1+m))}
	public void test00870() {
		check("Integrate(x^m/(a+b*x^3)^2, x)",
				"(x^(1+m)*Hypergeometric2F1(2, (1+m)/3, (4+m)/3, -((b*x^3)/a)))/(a^2*(1+m))");

	}

	// {x^m/(a+b*x^3)^3, x, 1, (x^(1+m)*Hypergeometric2F1(3, (1+m)/3, (4+m)/3, -((b*x^3)/a)))/(a^3*(1+m))}
	public void test00871() {
		check("Integrate(x^m/(a+b*x^3)^3, x)",
				"(x^(1+m)*Hypergeometric2F1(3, (1+m)/3, (4+m)/3, -((b*x^3)/a)))/(a^3*(1+m))");

	}

	// {x^2*(a+b*x^3)^p, x, 1, (a+b*x^3)^(1+p)/(3*b*(1+p))}
	public void test00872() {
		check("Integrate(x^2*(a+b*x^3)^p, x)", "(a+b*x^3)^(1+p)/(3*b*(1+p))");

	}

	// {a+b*x^4, x, 1, a*x+(b*x^5)/5}
	public void test00873() {
		check("Integrate(a+b*x^4, x)", "a*x+(b*x^5)/5");

	}

	// {x^3*(a+b*x^4)^2, x, 1, (a+b*x^4)^3/(12*b)}
	public void test00874() {
		check("Integrate(x^3*(a+b*x^4)^2, x)", "(a+b*x^4)^3/(12*b)");

	}

	// {x^3*(a+b*x^4)^3, x, 1, (a+b*x^4)^4/(16*b)}
	public void test00875() {
		check("Integrate(x^3*(a+b*x^4)^3, x)", "(a+b*x^4)^4/(16*b)");

	}

	// {x^3/(a+c*x^4), x, 1, Log(a+c*x^4)/(4*c)}
	public void test00876() {
		check("Integrate(x^3/(a+c*x^4), x)", "Log(a+c*x^4)/(4*c)");

	}

	// {x^3/(a+c*x^4)^2, x, 1, -1/(4*c*(a+c*x^4))}
	public void test00877() {
		check("Integrate(x^3/(a+c*x^4)^2, x)", "-1/(4*c*(a+c*x^4))");

	}

	// {x^7/(a+c*x^4)^3, x, 1, x^8/(8*a*(a+c*x^4)^2)}
	public void test00878() {
		check("Integrate(x^7/(a+c*x^4)^3, x)", "x^8/(8*a*(a+c*x^4)^2)");

	}

	// {x^3/(a+c*x^4)^3, x, 1, -1/(8*c*(a+c*x^4)^2)}
	public void test00879() {
		check("Integrate(x^3/(a+c*x^4)^3, x)", "-1/(8*c*(a+c*x^4)^2)");

	}

	// {x^3/(2+3*x^4), x, 1, Log(2+3*x^4)/12}
	public void test00880() {
		check("Integrate(x^3/(2+3*x^4), x)", "Log(2+3*x^4)/12");

	}

	// {x^3/(2+3*x^4)^2, x, 1, -1/(12*(2+3*x^4))}
	public void test00881() {
		check("Integrate(x^3/(2+3*x^4)^2, x)", "-1/(12*(2+3*x^4))");

	}

	// {x^3/(2*a+2*b+x^4), x, 1, Log(2*(a+b)+x^4)/4}
	public void test00882() {
		check("Integrate(x^3/(2*a+2*b+x^4), x)", "Log(2*(a+b)+x^4)/4");

	}

	// {x^3/(2*(a+b)+x^4), x, 1, Log(2*(a+b)+x^4)/4}
	public void test00883() {
		check("Integrate(x^3/(2*(a+b)+x^4), x)", "Log(2*(a+b)+x^4)/4");

	}

	// {x^3*Sqrt(a+c*x^4), x, 1, (a+c*x^4)^(3/2)/(6*c)}
	public void test00884() {
		check("Integrate(x^3*Sqrt(a+c*x^4), x)", "(a+c*x^4)^(3/2)/(6*c)");

	}

	// {Sqrt(a+c*x^4)/x^7, x, 1, -(a+c*x^4)^(3/2)/(6*a*x^6)}
	public void test00885() {
		check("Integrate(Sqrt(a+c*x^4)/x^7, x)", "-(a+c*x^4)^(3/2)/(6*a*x^6)");

	}

	// {x^3*(a+c*x^4)^(3/2), x, 1, (a+c*x^4)^(5/2)/(10*c)}
	public void test00886() {
		check("Integrate(x^3*(a+c*x^4)^(3/2), x)", "(a+c*x^4)^(5/2)/(10*c)");

	}

	// {(a+c*x^4)^(3/2)/x^11, x, 1, -(a+c*x^4)^(5/2)/(10*a*x^10)}
	public void test00887() {
		check("Integrate((a+c*x^4)^(3/2)/x^11, x)", "-(a+c*x^4)^(5/2)/(10*a*x^10)");

	}

	// {x^3*Sqrt(5+x^4), x, 1, (5+x^4)^(3/2)/6}
	public void test00888() {
		check("Integrate(x^3*Sqrt(5+x^4), x)", "(5+x^4)^(3/2)/6");

	}

	// {x^3/Sqrt(a+b*x^4), x, 1, Sqrt(a+b*x^4)/(2*b)}
	public void test00889() {
		check("Integrate(x^3/Sqrt(a+b*x^4), x)", "Sqrt(a+b*x^4)/(2*b)");

	}

	// {1/(x^3*Sqrt(a+b*x^4)), x, 1, -Sqrt(a+b*x^4)/(2*a*x^2)}
	public void test00890() {
		check("Integrate(1/(x^3*Sqrt(a+b*x^4)), x)", "-Sqrt(a+b*x^4)/(2*a*x^2)");

	}

	// {1/Sqrt(a+b*x^4), x, 1, ((Sqrt(a)+Sqrt(b)*x^2)*Sqrt((a+b*x^4)/(Sqrt(a) +
	// Sqrt(b)*x^2)^2)*EllipticF(2*ArcTan((b^(1/4)*x)/a^(1/4)), 1/2))/(2*a^(1/4)*b^(1/4)*Sqrt(a+b*x^4))}
	public void test00891() {
		check("Integrate(1/Sqrt(a+b*x^4), x)",
				"((Sqrt(a)+Sqrt(b)*x^2)*Sqrt((a+b*x^4)/(Sqrt(a)+Sqrt(b)*x^2)^2)*EllipticF(2*ArcTan((b^(1/4)*x)/a^(1/4)), 1/2))/(2*a^(1/4)*b^(1/4)*Sqrt(a+b*x^4))");

	}

	// {x^3/Sqrt(a-b*x^4), x, 1, -Sqrt(a-b*x^4)/(2*b)}
	public void test00892() {
		check("Integrate(x^3/Sqrt(a-b*x^4), x)", "-Sqrt(a-b*x^4)/(2*b)");

	}

	// {1/(x^3*Sqrt(a-b*x^4)), x, 1, -Sqrt(a-b*x^4)/(2*a*x^2)}
	public void test00893() {
		check("Integrate(1/(x^3*Sqrt(a-b*x^4)), x)", "-Sqrt(a-b*x^4)/(2*a*x^2)");

	}

	// {x^3/(a+b*x^4)^(3/2), x, 1, -1/(2*b*Sqrt(a+b*x^4))}
	public void test00894() {
		check("Integrate(x^3/(a+b*x^4)^(3/2), x)", "-1/(2*b*Sqrt(a+b*x^4))");

	}

	// {x/(a+b*x^4)^(3/2), x, 1, x^2/(2*a*Sqrt(a+b*x^4))}
	public void test00895() {
		check("Integrate(x/(a+b*x^4)^(3/2), x)", "x^2/(2*a*Sqrt(a+b*x^4))");

	}

	// {x^3/Sqrt(1-x^4), x, 1, -Sqrt(1-x^4)/2}
	public void test00896() {
		check("Integrate(x^3/Sqrt(1-x^4), x)", "-Sqrt(1-x^4)/2");

	}

	// {1/(x^3*Sqrt(1-x^4)), x, 1, -Sqrt(1-x^4)/(2*x^2)}
	public void test00897() {
		check("Integrate(1/(x^3*Sqrt(1-x^4)), x)", "-Sqrt(1-x^4)/(2*x^2)");

	}

	// {1/Sqrt(1-x^4), x, 1, EllipticF(ArcSin(x), -1)}
	public void test00898() {
		check("Integrate(1/Sqrt(1-x^4), x)", "EllipticF(ArcSin(x), -1)");

	}

	// {x^3/(1-x^4)^(3/2), x, 1, 1/(2*Sqrt(1-x^4))}
	public void test00899() {
		check("Integrate(x^3/(1-x^4)^(3/2), x)", "1/(2*Sqrt(1-x^4))");

	}

	// {x/(1-x^4)^(3/2), x, 1, x^2/(2*Sqrt(1-x^4))}
	public void test00900() {
		check("Integrate(x/(1-x^4)^(3/2), x)", "x^2/(2*Sqrt(1-x^4))");

	}

	// {x^3/Sqrt(1+x^4), x, 1, Sqrt(1+x^4)/2}
	public void test00901() {
		check("Integrate(x^3/Sqrt(1+x^4), x)", "Sqrt(1+x^4)/2");

	}

	// {1/(x^3*Sqrt(1+x^4)), x, 1, -Sqrt(1+x^4)/(2*x^2)}
	public void test00902() {
		check("Integrate(1/(x^3*Sqrt(1+x^4)), x)", "-Sqrt(1+x^4)/(2*x^2)");

	}

	// {1/Sqrt(1+x^4), x, 1, ((1+x^2)*Sqrt((1+x^4)/(1+x^2)^2)*EllipticF(2*ArcTan(x), 1/2))/(2*Sqrt(1+x^4))}
	public void test00903() {
		check("Integrate(1/Sqrt(1+x^4), x)",
				"((1+x^2)*Sqrt((1+x^4)/(1+x^2)^2)*EllipticF(2*ArcTan(x), 1/2))/(2*Sqrt(1+x^4))");

	}

	// {x^3/(1+x^4)^(3/2), x, 1, -1/(2*Sqrt(1+x^4))}
	public void test00904() {
		check("Integrate(x^3/(1+x^4)^(3/2), x)", "-1/(2*Sqrt(1+x^4))");

	}

	// {x/(1+x^4)^(3/2), x, 1, x^2/(2*Sqrt(1+x^4))}
	public void test00905() {
		check("Integrate(x/(1+x^4)^(3/2), x)", "x^2/(2*Sqrt(1+x^4))");

	}

	// {x^3/Sqrt(16-x^4), x, 1, -Sqrt(16-x^4)/2}
	public void test00906() {
		check("Integrate(x^3/Sqrt(16-x^4), x)", "-Sqrt(16-x^4)/2");

	}

	// {1/(x^3*Sqrt(16-x^4)), x, 1, -Sqrt(16-x^4)/(32*x^2)}
	public void test00907() {
		check("Integrate(1/(x^3*Sqrt(16-x^4)), x)", "-Sqrt(16-x^4)/(32*x^2)");

	}

	// {1/Sqrt(16-x^4), x, 1, EllipticF(ArcSin(x/2), -1)/2}
	public void test00908() {
		check("Integrate(1/Sqrt(16-x^4), x)", "EllipticF(ArcSin(x/2), -1)/2");

	}

	// {1/Sqrt(-1+x^4), x, 1, (Sqrt(-1+x^2)*Sqrt(1+x^2)*EllipticF(ArcSin((Sqrt(2)*x)/Sqrt(-1+x^2)),
	// 1/2))/(Sqrt(2)*Sqrt(-1+x^4))}
	public void test00909() {
		check("Integrate(1/Sqrt(-1+x^4), x)",
				"(Sqrt(-1+x^2)*Sqrt(1+x^2)*EllipticF(ArcSin((Sqrt(2)*x)/Sqrt(-1+x^2)), 1/2))/(Sqrt(2)*Sqrt(-1+x^4))");

	}

	// {x^3/(1+x^4)^(4/3), x, 1, -3/(4*(1+x^4)^(1/3))}
	public void test00910() {
		check("Integrate(x^3/(1+x^4)^(4/3), x)", "-3/(4*(1+x^4)^(1/3))");

	}

	// {x^3/(1+x^4)^(1/3), x, 1, (3*(1+x^4)^(2/3))/8}
	public void test00911() {
		check("Integrate(x^3/(1+x^4)^(1/3), x)", "(3*(1+x^4)^(2/3))/8");

	}

	// {x^3*(a+b*x^4)^(1/4), x, 1, (a+b*x^4)^(5/4)/(5*b)}
	public void test00912() {
		check("Integrate(x^3*(a+b*x^4)^(1/4), x)", "(a+b*x^4)^(5/4)/(5*b)");

	}

	// {(a+b*x^4)^(1/4)/x^6, x, 1, -(a+b*x^4)^(5/4)/(5*a*x^5)}
	public void test00913() {
		check("Integrate((a+b*x^4)^(1/4)/x^6, x)", "-(a+b*x^4)^(5/4)/(5*a*x^5)");

	}

	// {x^3*(a+b*x^4)^(3/4), x, 1, (a+b*x^4)^(7/4)/(7*b)}
	public void test00914() {
		check("Integrate(x^3*(a+b*x^4)^(3/4), x)", "(a+b*x^4)^(7/4)/(7*b)");

	}

	// {(a+b*x^4)^(3/4)/x^8, x, 1, -(a+b*x^4)^(7/4)/(7*a*x^7)}
	public void test00915() {
		check("Integrate((a+b*x^4)^(3/4)/x^8, x)", "-(a+b*x^4)^(7/4)/(7*a*x^7)");

	}

	// {x^3*(a+b*x^4)^(5/4), x, 1, (a+b*x^4)^(9/4)/(9*b)}
	public void test00916() {
		check("Integrate(x^3*(a+b*x^4)^(5/4), x)", "(a+b*x^4)^(9/4)/(9*b)");

	}

	// {(a+b*x^4)^(5/4)/x^10, x, 1, -(a+b*x^4)^(9/4)/(9*a*x^9)}
	public void test00917() {
		check("Integrate((a+b*x^4)^(5/4)/x^10, x)", "-(a+b*x^4)^(9/4)/(9*a*x^9)");

	}

	// {x^3/(a+b*x^4)^(1/4), x, 1, (a+b*x^4)^(3/4)/(3*b)}
	public void test00918() {
		check("Integrate(x^3/(a+b*x^4)^(1/4), x)", "(a+b*x^4)^(3/4)/(3*b)");

	}

	// {1/(x^4*(a+b*x^4)^(1/4)), x, 1, -(a+b*x^4)^(3/4)/(3*a*x^3)}
	public void test00919() {
		check("Integrate(1/(x^4*(a+b*x^4)^(1/4)), x)", "-(a+b*x^4)^(3/4)/(3*a*x^3)");

	}

	// {x^3/(a+b*x^4)^(3/4), x, 1, (a+b*x^4)^(1/4)/b}
	public void test00920() {
		check("Integrate(x^3/(a+b*x^4)^(3/4), x)", "(a+b*x^4)^(1/4)/b");

	}

	// {1/(x^2*(a+b*x^4)^(3/4)), x, 1, -((a+b*x^4)^(1/4)/(a*x))}
	public void test00921() {
		check("Integrate(1/(x^2*(a+b*x^4)^(3/4)), x)", "-((a+b*x^4)^(1/4)/(a*x))");

	}

	// {x^3/(a+b*x^4)^(5/4), x, 1, -(1/(b*(a+b*x^4)^(1/4)))}
	public void test00922() {
		check("Integrate(x^3/(a+b*x^4)^(5/4), x)", "-(1/(b*(a+b*x^4)^(1/4)))");

	}

	// {(a+b*x^4)^(-5/4), x, 1, x/(a*(a+b*x^4)^(1/4))}
	public void test00923() {
		check("Integrate((a+b*x^4)^(-5/4), x)", "x/(a*(a+b*x^4)^(1/4))");

	}

	// {x^3*(a-b*x^4)^(1/4), x, 1, -(a-b*x^4)^(5/4)/(5*b)}
	public void test00924() {
		check("Integrate(x^3*(a-b*x^4)^(1/4), x)", "-(a-b*x^4)^(5/4)/(5*b)");

	}

	// {(a-b*x^4)^(1/4)/x^6, x, 1, -(a-b*x^4)^(5/4)/(5*a*x^5)}
	public void test00925() {
		check("Integrate((a-b*x^4)^(1/4)/x^6, x)", "-(a-b*x^4)^(5/4)/(5*a*x^5)");

	}

	// {x^3/(a-b*x^4)^(1/4), x, 1, -(a-b*x^4)^(3/4)/(3*b)}
	public void test00926() {
		check("Integrate(x^3/(a-b*x^4)^(1/4), x)", "-(a-b*x^4)^(3/4)/(3*b)");

	}

	// {1/(x^4*(a-b*x^4)^(1/4)), x, 1, -(a-b*x^4)^(3/4)/(3*a*x^3)}
	public void test00927() {
		check("Integrate(1/(x^4*(a-b*x^4)^(1/4)), x)", "-(a-b*x^4)^(3/4)/(3*a*x^3)");

	}

	// {x^3/(a-b*x^4)^(3/4), x, 1, -((a-b*x^4)^(1/4)/b)}
	public void test00928() {
		check("Integrate(x^3/(a-b*x^4)^(3/4), x)", "-((a-b*x^4)^(1/4)/b)");

	}

	// {1/(x^2*(a-b*x^4)^(3/4)), x, 1, -((a-b*x^4)^(1/4)/(a*x))}
	public void test00929() {
		check("Integrate(1/(x^2*(a-b*x^4)^(3/4)), x)", "-((a-b*x^4)^(1/4)/(a*x))");

	}

	// {x^3*(a+b*x^4)^p, x, 1, (a+b*x^4)^(1+p)/(4*b*(1+p))}
	public void test00930() {
		check("Integrate(x^3*(a+b*x^4)^p, x)", "(a+b*x^4)^(1+p)/(4*b*(1+p))");

	}

	// {x^4/(a+b*x^5), x, 1, Log(a+b*x^5)/(5*b)}
	public void test00931() {
		check("Integrate(x^4/(a+b*x^5), x)", "Log(a+b*x^5)/(5*b)");

	}

	// {x^4/(a+b*x^5)^2, x, 1, -1/(5*b*(a+b*x^5))}
	public void test00932() {
		check("Integrate(x^4/(a+b*x^5)^2, x)", "-1/(5*b*(a+b*x^5))");

	}

	// {x^4/(2*b+b*x^5), x, 1, Log(2+x^5)/(5*b)}
	public void test00933() {
		check("Integrate(x^4/(2*b+b*x^5), x)", "Log(2+x^5)/(5*b)");

	}

	// {x^4/(3+b*x^5), x, 1, Log(3+b*x^5)/(5*b)}
	public void test00934() {
		check("Integrate(x^4/(3+b*x^5), x)", "Log(3+b*x^5)/(5*b)");

	}

	// {x^4/(1+x^5), x, 1, Log(1+x^5)/5}
	public void test00935() {
		check("Integrate(x^4/(1+x^5), x)", "Log(1+x^5)/5");

	}

	// {1/(x^(7/2)*Sqrt(a+b*x^5)), x, 1, (-2*Sqrt(a+b*x^5))/(5*a*x^(5/2))}
	public void test00936() {
		check("Integrate(1/(x^(7/2)*Sqrt(a+b*x^5)), x)", "(-2*Sqrt(a+b*x^5))/(5*a*x^(5/2))");

	}

	// {1/(x^(7/2)*Sqrt(1+x^5)), x, 1, (-2*Sqrt(1+x^5))/(5*x^(5/2))}
	public void test00937() {
		check("Integrate(1/(x^(7/2)*Sqrt(1+x^5)), x)", "(-2*Sqrt(1+x^5))/(5*x^(5/2))");

	}

	// {x^5/(a+b*x^6), x, 1, Log(a+b*x^6)/(6*b)}
	public void test00938() {
		check("Integrate(x^5/(a+b*x^6), x)", "Log(a+b*x^6)/(6*b)");

	}

	// {x^5/(a+b*x^6)^2, x, 1, -1/(6*b*(a+b*x^6))}
	public void test00939() {
		check("Integrate(x^5/(a+b*x^6)^2, x)", "-1/(6*b*(a+b*x^6))");

	}

	// {x^5/(1-x^6), x, 1, -Log(1-x^6)/6}
	public void test00940() {
		check("Integrate(x^5/(1-x^6), x)", "-Log(1-x^6)/6");

	}

	// {x^5/(1+x^6), x, 1, Log(1+x^6)/6}
	public void test00941() {
		check("Integrate(x^5/(1+x^6), x)", "Log(1+x^6)/6");

	}

	// {x^5*Sqrt(a^6-x^6), x, 1, -(a^6-x^6)^(3/2)/9}
	public void test00942() {
		check("Integrate(x^5*Sqrt(a^6-x^6), x)", "-(a^6-x^6)^(3/2)/9");

	}

	// {x^5/Sqrt(2+x^6), x, 1, Sqrt(2+x^6)/3}
	public void test00943() {
		check("Integrate(x^5/Sqrt(2+x^6), x)", "Sqrt(2+x^6)/3");

	}

	// {1/(x^4*Sqrt(2+x^6)), x, 1, -Sqrt(2+x^6)/(6*x^3)}
	public void test00944() {
		check("Integrate(1/(x^4*Sqrt(2+x^6)), x)", "-Sqrt(2+x^6)/(6*x^3)");

	}

	// {1/Sqrt(2+x^6), x, 1, (x*(2^(1/3)+x^2)*Sqrt((2^(2/3)-2^(1/3)*x^2+x^4)/(2^(1/3)+(1 +
	// Sqrt(3))*x^2)^2)*EllipticF(ArcCos((2^(1/3)+(1-Sqrt(3))*x^2)/(2^(1/3)+(1+Sqrt(3))*x^2)), (2 +
	// Sqrt(3))/4))/(2*2^(1/3)*3^(1/4)*Sqrt((x^2*(2^(1/3)+x^2))/(2^(1/3)+(1+Sqrt(3))*x^2)^2)*Sqrt(2+x^6))}
	public void test00945() {
		check("Integrate(1/Sqrt(2+x^6), x)",
				"(x*(2^(1/3)+x^2)*Sqrt((2^(2/3)-2^(1/3)*x^2+x^4)/(2^(1/3)+(1+Sqrt(3))*x^2)^2)*EllipticF(ArcCos((2^(1/3)+(1-Sqrt(3))*x^2)/(2^(1/3)+(1+Sqrt(3))*x^2)), (2+Sqrt(3))/4))/(2*2^(1/3)*3^(1/4)*Sqrt((x^2*(2^(1/3)+x^2))/(2^(1/3)+(1+Sqrt(3))*x^2)^2)*Sqrt(2+x^6))");

	}

	// {x^5/(2+x^6)^(3/2), x, 1, -1/(3*Sqrt(2+x^6))}
	public void test00946() {
		check("Integrate(x^5/(2+x^6)^(3/2), x)", "-1/(3*Sqrt(2+x^6))");

	}

	// {x^2/(2+x^6)^(3/2), x, 1, x^3/(6*Sqrt(2+x^6))}
	public void test00947() {
		check("Integrate(x^2/(2+x^6)^(3/2), x)", "x^3/(6*Sqrt(2+x^6))");

	}

	// {x^m/(a+b*x^7), x, 1, (x^(1+m)*Hypergeometric2F1(1, (1+m)/7, (8+m)/7, -((b*x^7)/a)))/(a*(1+m))}
	public void test00948() {
		check("Integrate(x^m/(a+b*x^7), x)",
				"(x^(1+m)*Hypergeometric2F1(1, (1+m)/7, (8+m)/7, -((b*x^7)/a)))/(a*(1+m))");

	}

	// {x^6/(a+b*x^7), x, 1, Log(a+b*x^7)/(7*b)}
	public void test00949() {
		check("Integrate(x^6/(a+b*x^7), x)", "Log(a+b*x^7)/(7*b)");

	}

	// {x^m/(a-b*x^7), x, 1, (x^(1+m)*Hypergeometric2F1(1, (1+m)/7, (8+m)/7, (b*x^7)/a))/(a*(1+m))}
	public void test00950() {
		check("Integrate(x^m/(a-b*x^7), x)", "(x^(1+m)*Hypergeometric2F1(1, (1+m)/7, (8+m)/7, (b*x^7)/a))/(a*(1+m))");

	}

	// {x^6/(a-b*x^7), x, 1, -Log(a-b*x^7)/(7*b)}
	public void test00951() {
		check("Integrate(x^6/(a-b*x^7), x)", "-Log(a-b*x^7)/(7*b)");

	}

	// {x^7/(a+b*x^8), x, 1, Log(a+b*x^8)/(8*b)}
	public void test00952() {
		check("Integrate(x^7/(a+b*x^8), x)", "Log(a+b*x^8)/(8*b)");

	}

	// {x^7/(1-x^8), x, 1, -Log(1-x^8)/8}
	public void test00953() {
		check("Integrate(x^7/(1-x^8), x)", "-Log(1-x^8)/8");

	}

	// {x^7/(1+x^8), x, 1, Log(1+x^8)/8}
	public void test00954() {
		check("Integrate(x^7/(1+x^8), x)", "Log(1+x^8)/8");

	}

	// {x^7/Sqrt(1+x^8), x, 1, Sqrt(1+x^8)/4}
	public void test00955() {
		check("Integrate(x^7/Sqrt(1+x^8), x)", "Sqrt(1+x^8)/4");

	}

	// {1/(x^5*Sqrt(1+x^8)), x, 1, -Sqrt(1+x^8)/(4*x^4)}
	public void test00956() {
		check("Integrate(1/(x^5*Sqrt(1+x^8)), x)", "-Sqrt(1+x^8)/(4*x^4)");

	}

	// {x^6/Sqrt(1+x^8), x, 1, (x^7*Hypergeometric2F1(1/2, 7/8, 15/8, -x^8))/7}
	public void test00957() {
		check("Integrate(x^6/Sqrt(1+x^8), x)", "(x^7*Hypergeometric2F1(1/2, 7/8, 15/8, -x^8))/7");

	}

	// {x^4/Sqrt(1+x^8), x, 1, (x^5*Hypergeometric2F1(1/2, 5/8, 13/8, -x^8))/5}
	public void test00958() {
		check("Integrate(x^4/Sqrt(1+x^8), x)", "(x^5*Hypergeometric2F1(1/2, 5/8, 13/8, -x^8))/5");

	}

	// {a+b/x, x, 1, a*x+b*Log(x)}
	public void test00959() {
		check("Integrate(a+b/x, x)", "a*x+b*Log(x)");

	}

	// {(a+b/x)^2/x^2, x, 1, -(a+b/x)^3/(3*b)}
	public void test00960() {
		check("Integrate((a+b/x)^2/x^2, x)", "-(a+b/x)^3/(3*b)");

	}

	// {(a+b/x)^3/x^2, x, 1, -(a+b/x)^4/(4*b)}
	public void test00961() {
		check("Integrate((a+b/x)^3/x^2, x)", "-(a+b/x)^4/(4*b)");

	}

	// {(a+b/x)^8/x^2, x, 1, -(a+b/x)^9/(9*b)}
	public void test00962() {
		check("Integrate((a+b/x)^8/x^2, x)", "-(a+b/x)^9/(9*b)");

	}

	// {1/((a+b/x)*x^2), x, 1, -(Log(a+b/x)/b)}
	public void test00963() {
		check("Integrate(1/((a+b/x)*x^2), x)", "-(Log(a+b/x)/b)");

	}

	// {1/((a+b/x)^2*x^2), x, 1, 1/(b*(a+b/x))}
	public void test00964() {
		check("Integrate(1/((a+b/x)^2*x^2), x)", "1/(b*(a+b/x))");

	}

	// {1/((a+b/x)^3*x^2), x, 1, 1/(2*b*(a+b/x)^2)}
	public void test00965() {
		check("Integrate(1/((a+b/x)^3*x^2), x)", "1/(2*b*(a+b/x)^2)");

	}

	// {Sqrt(a+b/x)/x^2, x, 1, (-2*(a+b/x)^(3/2))/(3*b)}
	public void test00966() {
		check("Integrate(Sqrt(a+b/x)/x^2, x)", "(-2*(a+b/x)^(3/2))/(3*b)");

	}

	// {(a+b/x)^(3/2)/x^2, x, 1, (-2*(a+b/x)^(5/2))/(5*b)}
	public void test00967() {
		check("Integrate((a+b/x)^(3/2)/x^2, x)", "(-2*(a+b/x)^(5/2))/(5*b)");

	}

	// {(a+b/x)^(5/2)/x^2, x, 1, (-2*(a+b/x)^(7/2))/(7*b)}
	public void test00968() {
		check("Integrate((a+b/x)^(5/2)/x^2, x)", "(-2*(a+b/x)^(7/2))/(7*b)");

	}

	// {1/(Sqrt(a+b/x)*x^2), x, 1, (-2*Sqrt(a+b/x))/b}
	public void test00969() {
		check("Integrate(1/(Sqrt(a+b/x)*x^2), x)", "(-2*Sqrt(a+b/x))/b");

	}

	// {1/((a+b/x)^(3/2)*x^2), x, 1, 2/(b*Sqrt(a+b/x))}
	public void test00970() {
		check("Integrate(1/((a+b/x)^(3/2)*x^2), x)", "2/(b*Sqrt(a+b/x))");

	}

	// {1/((a+b/x)^(5/2)*x^2), x, 1, 2/(3*b*(a+b/x)^(3/2))}
	public void test00971() {
		check("Integrate(1/((a+b/x)^(5/2)*x^2), x)", "2/(3*b*(a+b/x)^(3/2))");

	}

	// {Sqrt(a+b/x)*Sqrt(x), x, 1, (2*(a+b/x)^(3/2)*x^(3/2))/(3*a)}
	public void test00972() {
		check("Integrate(Sqrt(a+b/x)*Sqrt(x), x)", "(2*(a+b/x)^(3/2)*x^(3/2))/(3*a)");

	}

	// {(a+b/x)^(3/2)*x^(3/2), x, 1, (2*(a+b/x)^(5/2)*x^(5/2))/(5*a)}
	public void test00973() {
		check("Integrate((a+b/x)^(3/2)*x^(3/2), x)", "(2*(a+b/x)^(5/2)*x^(5/2))/(5*a)");

	}

	// {(a+b/x)^(5/2)*x^(5/2), x, 1, (2*(a+b/x)^(7/2)*x^(7/2))/(7*a)}
	public void test00974() {
		check("Integrate((a+b/x)^(5/2)*x^(5/2), x)", "(2*(a+b/x)^(7/2)*x^(7/2))/(7*a)");

	}

	// {1/(Sqrt(a+b/x)*Sqrt(x)), x, 1, (2*Sqrt(a+b/x)*Sqrt(x))/a}
	public void test00975() {
		check("Integrate(1/(Sqrt(a+b/x)*Sqrt(x)), x)", "(2*Sqrt(a+b/x)*Sqrt(x))/a");

	}

	// {1/((a+b/x)^(3/2)*x^(3/2)), x, 1, -2/(a*Sqrt(a+b/x)*Sqrt(x))}
	public void test00976() {
		check("Integrate(1/((a+b/x)^(3/2)*x^(3/2)), x)", "-2/(a*Sqrt(a+b/x)*Sqrt(x))");

	}

	// {1/((a+b/x)^(5/2)*x^(5/2)), x, 1, -2/(3*a*(a+b/x)^(3/2)*x^(3/2))}
	public void test00977() {
		check("Integrate(1/((a+b/x)^(5/2)*x^(5/2)), x)", "-2/(3*a*(a+b/x)^(3/2)*x^(3/2))");

	}

	// {a+b/x^2, x, 1, -(b/x)+a*x}
	public void test00978() {
		check("Integrate(a+b/x^2, x)", "-(b/x)+a*x");

	}

	// {(a+b/x^2)^2/x^3, x, 1, -(a+b/x^2)^3/(6*b)}
	public void test00979() {
		check("Integrate((a+b/x^2)^2/x^3, x)", "-(a+b/x^2)^3/(6*b)");

	}

	// {(a+b/x^2)^3/x^3, x, 1, -(a+b/x^2)^4/(8*b)}
	public void test00980() {
		check("Integrate((a+b/x^2)^3/x^3, x)", "-(a+b/x^2)^4/(8*b)");

	}

	// {1/((a+b/x^2)*x^3), x, 1, -Log(a+b/x^2)/(2*b)}
	public void test00981() {
		check("Integrate(1/((a+b/x^2)*x^3), x)", "-Log(a+b/x^2)/(2*b)");

	}

	// {1/((a+b/x^2)^2*x^3), x, 1, 1/(2*b*(a+b/x^2))}
	public void test00982() {
		check("Integrate(1/((a+b/x^2)^2*x^3), x)", "1/(2*b*(a+b/x^2))");

	}

	// {1/((a+b/x^2)^3*x^3), x, 1, 1/(4*b*(a+b/x^2)^2)}
	public void test00983() {
		check("Integrate(1/((a+b/x^2)^3*x^3), x)", "1/(4*b*(a+b/x^2)^2)");

	}

	// {Sqrt(a+b/x^2)*x^2, x, 1, ((a+b/x^2)^(3/2)*x^3)/(3*a)}
	public void test00984() {
		check("Integrate(Sqrt(a+b/x^2)*x^2, x)", "((a+b/x^2)^(3/2)*x^3)/(3*a)");

	}

	// {Sqrt(a+b/x^2)/x^3, x, 1, -(a+b/x^2)^(3/2)/(3*b)}
	public void test00985() {
		check("Integrate(Sqrt(a+b/x^2)/x^3, x)", "-(a+b/x^2)^(3/2)/(3*b)");

	}

	// {(a+b/x^2)^(3/2)/x^3, x, 1, -(a+b/x^2)^(5/2)/(5*b)}
	public void test00986() {
		check("Integrate((a+b/x^2)^(3/2)/x^3, x)", "-(a+b/x^2)^(5/2)/(5*b)");

	}

	// {(a+b/x^2)^(5/2)/x^3, x, 1, -(a+b/x^2)^(7/2)/(7*b)}
	public void test00987() {
		check("Integrate((a+b/x^2)^(5/2)/x^3, x)", "-(a+b/x^2)^(7/2)/(7*b)");

	}

	// {1/(Sqrt(a+b/x^2)*x^3), x, 1, -(Sqrt(a+b/x^2)/b)}
	public void test00988() {
		check("Integrate(1/(Sqrt(a+b/x^2)*x^3), x)", "-(Sqrt(a+b/x^2)/b)");

	}

	// {1/Sqrt(a+b/x^2), x, 1, (Sqrt(a+b/x^2)*x)/a}
	public void test00989() {
		check("Integrate(1/Sqrt(a+b/x^2), x)", "(Sqrt(a+b/x^2)*x)/a");

	}

	// {1/((a+b/x^2)^(3/2)*x^3), x, 1, 1/(b*Sqrt(a+b/x^2))}
	public void test00990() {
		check("Integrate(1/((a+b/x^2)^(3/2)*x^3), x)", "1/(b*Sqrt(a+b/x^2))");

	}

	// {1/((a+b/x^2)^(3/2)*x^2), x, 1, -(1/(a*Sqrt(a+b/x^2)*x))}
	public void test00991() {
		check("Integrate(1/((a+b/x^2)^(3/2)*x^2), x)", "-(1/(a*Sqrt(a+b/x^2)*x))");

	}

	// {1/((a+b/x^2)^(5/2)*x^3), x, 1, 1/(3*b*(a+b/x^2)^(3/2))}
	public void test00992() {
		check("Integrate(1/((a+b/x^2)^(5/2)*x^3), x)", "1/(3*b*(a+b/x^2)^(3/2))");

	}

	// {1/((a+b/x^2)^(5/2)*x^4), x, 1, -1/(3*a*(a+b/x^2)^(3/2)*x^3)}
	public void test00993() {
		check("Integrate(1/((a+b/x^2)^(5/2)*x^4), x)", "-1/(3*a*(a+b/x^2)^(3/2)*x^3)");

	}

	// {(1+x^(-2))^(1/3)/x^3, x, 1, (-3*(1+x^(-2))^(4/3))/8}
	public void test00994() {
		check("Integrate((1+x^(-2))^(1/3)/x^3, x)", "(-3*(1+x^(-2))^(4/3))/8");

	}

	// {(1+x^(-2))^(5/3)/x^3, x, 1, (-3*(1+x^(-2))^(8/3))/16}
	public void test00995() {
		check("Integrate((1+x^(-2))^(5/3)/x^3, x)", "(-3*(1+x^(-2))^(8/3))/16");

	}

	// {1/((a+b/x^3)*x^4), x, 1, -Log(a+b/x^3)/(3*b)}
	public void test00996() {
		check("Integrate(1/((a+b/x^3)*x^4), x)", "-Log(a+b/x^3)/(3*b)");

	}

	// {1/((a+b/x^3)^2*x^4), x, 1, 1/(3*b*(a+b/x^3))}
	public void test00997() {
		check("Integrate(1/((a+b/x^3)^2*x^4), x)", "1/(3*b*(a+b/x^3))");

	}

	// {Sqrt(a+b/x^3)/x^4, x, 1, (-2*(a+b/x^3)^(3/2))/(9*b)}
	public void test00998() {
		check("Integrate(Sqrt(a+b/x^3)/x^4, x)", "(-2*(a+b/x^3)^(3/2))/(9*b)");

	}

	// {(a+b/x^3)^(3/2)/x^4, x, 1, (-2*(a+b/x^3)^(5/2))/(15*b)}
	public void test00999() {
		check("Integrate((a+b/x^3)^(3/2)/x^4, x)", "(-2*(a+b/x^3)^(5/2))/(15*b)");

	}

	// {1/(Sqrt(a+b/x^3)*x^4), x, 1, (-2*Sqrt(a+b/x^3))/(3*b)}
	public void test01000() {
		check("Integrate(1/(Sqrt(a+b/x^3)*x^4), x)", "(-2*Sqrt(a+b/x^3))/(3*b)");

	}

	// {1/((a+b/x^3)^(3/2)*x^4), x, 1, 2/(3*b*Sqrt(a+b/x^3))}
	public void test01001() {
		check("Integrate(1/((a+b/x^3)^(3/2)*x^4), x)", "2/(3*b*Sqrt(a+b/x^3))");

	}

	// {x/Sqrt(a+b/x^4), x, 1, (Sqrt(a+b/x^4)*x^2)/(2*a)}
	public void test01002() {
		check("Integrate(x/Sqrt(a+b/x^4), x)", "(Sqrt(a+b/x^4)*x^2)/(2*a)");

	}

	// {1/((a+b/x^4)^(3/2)*x^3), x, 1, -1/(2*a*Sqrt(a+b/x^4)*x^2)}
	public void test01003() {
		check("Integrate(1/((a+b/x^4)^(3/2)*x^3), x)", "-1/(2*a*Sqrt(a+b/x^4)*x^2)");

	}

	// {a+b*Sqrt(x), x, 1, a*x+(2*b*x^(3/2))/3}
	public void test01004() {
		check("Integrate(a+b*Sqrt(x), x)", "a*x+(2*b*x^(3/2))/3");

	}

	// {(a+b*Sqrt(x))^3/x^3, x, 1, -(a+b*Sqrt(x))^4/(2*a*x^2)}
	public void test01005() {
		check("Integrate((a+b*Sqrt(x))^3/x^3, x)", "-(a+b*Sqrt(x))^4/(2*a*x^2)");

	}

	// {(a+b*Sqrt(x))^5/x^4, x, 1, -(a+b*Sqrt(x))^6/(3*a*x^3)}
	public void test01006() {
		check("Integrate((a+b*Sqrt(x))^5/x^4, x)", "-(a+b*Sqrt(x))^6/(3*a*x^3)");

	}

	// {(a+b*Sqrt(x))^15/x^9, x, 1, -(a+b*Sqrt(x))^16/(8*a*x^8)}
	public void test01007() {
		check("Integrate((a+b*Sqrt(x))^15/x^9, x)", "-(a+b*Sqrt(x))^16/(8*a*x^8)");

	}

	// {x/(a+b*Sqrt(x))^5, x, 1, x^2/(2*a*(a+b*Sqrt(x))^4)}
	public void test01008() {
		check("Integrate(x/(a+b*Sqrt(x))^5, x)", "x^2/(2*a*(a+b*Sqrt(x))^4)");

	}

	// {(a+b*Sqrt(x))^n/Sqrt(x), x, 1, (2*(a+b*Sqrt(x))^(1+n))/(b*(1+n))}
	public void test01009() {
		check("Integrate((a+b*Sqrt(x))^n/Sqrt(x), x)", "(2*(a+b*Sqrt(x))^(1+n))/(b*(1+n))");

	}

	// {(1+Sqrt(x))^2/Sqrt(x), x, 1, (2*(1+Sqrt(x))^3)/3}
	public void test01010() {
		check("Integrate((1+Sqrt(x))^2/Sqrt(x), x)", "(2*(1+Sqrt(x))^3)/3");

	}

	// {(1+Sqrt(x))^3/Sqrt(x), x, 1, (1+Sqrt(x))^4/2}
	public void test01011() {
		check("Integrate((1+Sqrt(x))^3/Sqrt(x), x)", "(1+Sqrt(x))^4/2");

	}

	// {1/((1+Sqrt(x))*Sqrt(x)), x, 1, 2*Log(1+Sqrt(x))}
	public void test01012() {
		check("Integrate(1/((1+Sqrt(x))*Sqrt(x)), x)", "2*Log(1+Sqrt(x))");

	}

	// {1/((1+Sqrt(x))^2*Sqrt(x)), x, 1, -2/(1+Sqrt(x))}
	public void test01013() {
		check("Integrate(1/((1+Sqrt(x))^2*Sqrt(x)), x)", "-2/(1+Sqrt(x))");

	}

	// {1/((1+Sqrt(x))^3*Sqrt(x)), x, 1, -(1+Sqrt(x))^(-2)}
	public void test01014() {
		check("Integrate(1/((1+Sqrt(x))^3*Sqrt(x)), x)", "-(1+Sqrt(x))^(-2)");

	}

	// {Sqrt(1+Sqrt(x))/Sqrt(x), x, 1, (4*(1+Sqrt(x))^(3/2))/3}
	public void test01015() {
		check("Integrate(Sqrt(1+Sqrt(x))/Sqrt(x), x)", "(4*(1+Sqrt(x))^(3/2))/3");

	}

	// {Sqrt(x)/(1+x^(3/2)), x, 1, (2*Log(1+x^(3/2)))/3}
	public void test01016() {
		check("Integrate(Sqrt(x)/(1+x^(3/2)), x)", "(2*Log(1+x^(3/2)))/3");

	}

	// {a+b*x^(1/3), x, 1, a*x+(3*b*x^(4/3))/4}
	public void test01017() {
		check("Integrate(a+b*x^(1/3), x)", "a*x+(3*b*x^(4/3))/4");

	}

	// {(a+b*x^(1/3))^2/x^2, x, 1, -((a+b*x^(1/3))^3/(a*x))}
	public void test01018() {
		check("Integrate((a+b*x^(1/3))^2/x^2, x)", "-((a+b*x^(1/3))^3/(a*x))");

	}

	// {(a+b*x^(1/3))^5/x^3, x, 1, -(a+b*x^(1/3))^6/(2*a*x^2)}
	public void test01019() {
		check("Integrate((a+b*x^(1/3))^5/x^3, x)", "-(a+b*x^(1/3))^6/(2*a*x^2)");

	}

	// {1/((1+x^(2/3))*x^(1/3)), x, 1, (3*Log(1+x^(2/3)))/2}
	public void test01020() {
		check("Integrate(1/((1+x^(2/3))*x^(1/3)), x)", "(3*Log(1+x^(2/3)))/2");

	}

	// {Sqrt(-1+x^(2/3))/x^(1/3), x, 1, (-1+x^(2/3))^(3/2)}
	public void test01021() {
		check("Integrate(Sqrt(-1+x^(2/3))/x^(1/3), x)", "(-1+x^(2/3))^(3/2)");

	}

	// {(1+x^(2/3))^(3/2)/x^(1/3), x, 1, (3*(1+x^(2/3))^(5/2))/5}
	public void test01022() {
		check("Integrate((1+x^(2/3))^(3/2)/x^(1/3), x)", "(3*(1+x^(2/3))^(5/2))/5");

	}

	// {a+b/x^(1/3), x, 1, (3*b*x^(2/3))/2+a*x}
	public void test01023() {
		check("Integrate(a+b/x^(1/3), x)", "(3*b*x^(2/3))/2+a*x");

	}

	// {x^(2/3)*(1+x^(5/3))^(2/3), x, 1, (9*(1+x^(5/3))^(5/3))/25}
	public void test01024() {
		check("Integrate(x^(2/3)*(1+x^(5/3))^(2/3), x)", "(9*(1+x^(5/3))^(5/3))/25");

	}

	// {x^(7/3)*(a^(10/3)-x^(10/3))^(19/7), x, 1, (-21*(a^(10/3)-x^(10/3))^(26/7))/260}
	public void test01025() {
		check("Integrate(x^(7/3)*(a^(10/3)-x^(10/3))^(19/7), x)", "(-21*(a^(10/3)-x^(10/3))^(26/7))/260");

	}

	// {1/(Sqrt(1+x^(4/5))*x^(1/5)), x, 1, (5*Sqrt(1+x^(4/5)))/2}
	public void test01026() {
		check("Integrate(1/(Sqrt(1+x^(4/5))*x^(1/5)), x)", "(5*Sqrt(1+x^(4/5)))/2");

	}

	// {(a+b/x^(3/5))^(2/3), x, 1, ((a+b/x^(3/5))^(5/3)*x)/a}
	public void test01027() {
		check("Integrate((a+b/x^(3/5))^(2/3), x)", "((a+b/x^(3/5))^(5/3)*x)/a");

	}

	// {a+b*x^n, x, 1, a*x+(b*x^(1+n))/(1+n)}
	public void test01028() {
		check("Integrate(a+b*x^n, x)", "a*x+(b*x^(1+n))/(1+n)");

	}

	// {x/(a+b*x^n), x, 1, (x^2*Hypergeometric2F1(1, 2/n, (2+n)/n, -((b*x^n)/a)))/(2*a)}
	public void test01029() {
		check("Integrate(x/(a+b*x^n), x)", "(x^2*Hypergeometric2F1(1, 2/n, (2+n)/n, -((b*x^n)/a)))/(2*a)");

	}

	// {(a+b*x^n)^(-1), x, 1, (x*Hypergeometric2F1(1, n^(-1), 1+n^(-1), -((b*x^n)/a)))/a}
	public void test01030() {
		check("Integrate((a+b*x^n)^(-1), x)", "(x*Hypergeometric2F1(1, n^(-1), 1+n^(-1), -((b*x^n)/a)))/a");

	}

	// {1/(x^2*(a+b*x^n)), x, 1, -(Hypergeometric2F1(1, -n^(-1), -((1-n)/n), -((b*x^n)/a))/(a*x))}
	public void test01031() {
		check("Integrate(1/(x^2*(a+b*x^n)), x)", "-(Hypergeometric2F1(1, -n^(-1), -((1-n)/n), -((b*x^n)/a))/(a*x))");

	}

	// {1/(x^3*(a+b*x^n)), x, 1, -Hypergeometric2F1(1, -2/n, -((2-n)/n), -((b*x^n)/a))/(2*a*x^2)}
	public void test01032() {
		check("Integrate(1/(x^3*(a+b*x^n)), x)", "-Hypergeometric2F1(1, -2/n, -((2-n)/n), -((b*x^n)/a))/(2*a*x^2)");

	}

	// {x/(a+b*x^n)^2, x, 1, (x^2*Hypergeometric2F1(2, 2/n, (2+n)/n, -((b*x^n)/a)))/(2*a^2)}
	public void test01033() {
		check("Integrate(x/(a+b*x^n)^2, x)", "(x^2*Hypergeometric2F1(2, 2/n, (2+n)/n, -((b*x^n)/a)))/(2*a^2)");

	}

	// {(a+b*x^n)^(-2), x, 1, (x*Hypergeometric2F1(2, n^(-1), 1+n^(-1), -((b*x^n)/a)))/a^2}
	public void test01034() {
		check("Integrate((a+b*x^n)^(-2), x)", "(x*Hypergeometric2F1(2, n^(-1), 1+n^(-1), -((b*x^n)/a)))/a^2");

	}

	// {1/(x^2*(a+b*x^n)^2), x, 1, -(Hypergeometric2F1(2, -n^(-1), -((1-n)/n), -((b*x^n)/a))/(a^2*x))}
	public void test01035() {
		check("Integrate(1/(x^2*(a+b*x^n)^2), x)",
				"-(Hypergeometric2F1(2, -n^(-1), -((1-n)/n), -((b*x^n)/a))/(a^2*x))");

	}

	// {1/(x^3*(a+b*x^n)^2), x, 1, -Hypergeometric2F1(2, -2/n, -((2-n)/n), -((b*x^n)/a))/(2*a^2*x^2)}
	public void test01036() {
		check("Integrate(1/(x^3*(a+b*x^n)^2), x)", "-Hypergeometric2F1(2, -2/n, -((2-n)/n), -((b*x^n)/a))/(2*a^2*x^2)");

	}

	// {x/(a+b*x^n)^3, x, 1, (x^2*Hypergeometric2F1(3, 2/n, (2+n)/n, -((b*x^n)/a)))/(2*a^3)}
	public void test01037() {
		check("Integrate(x/(a+b*x^n)^3, x)", "(x^2*Hypergeometric2F1(3, 2/n, (2+n)/n, -((b*x^n)/a)))/(2*a^3)");

	}

	// {(a+b*x^n)^(-3), x, 1, (x*Hypergeometric2F1(3, n^(-1), 1+n^(-1), -((b*x^n)/a)))/a^3}
	public void test01038() {
		check("Integrate((a+b*x^n)^(-3), x)", "(x*Hypergeometric2F1(3, n^(-1), 1+n^(-1), -((b*x^n)/a)))/a^3");

	}

	// {1/(x^2*(a+b*x^n)^3), x, 1, -(Hypergeometric2F1(3, -n^(-1), -((1-n)/n), -((b*x^n)/a))/(a^3*x))}
	public void test01039() {
		check("Integrate(1/(x^2*(a+b*x^n)^3), x)",
				"-(Hypergeometric2F1(3, -n^(-1), -((1-n)/n), -((b*x^n)/a))/(a^3*x))");

	}

	// {1/(x^3*(a+b*x^n)^3), x, 1, -Hypergeometric2F1(3, -2/n, -((2-n)/n), -((b*x^n)/a))/(2*a^3*x^2)}
	public void test01040() {
		check("Integrate(1/(x^3*(a+b*x^n)^3), x)", "-Hypergeometric2F1(3, -2/n, -((2-n)/n), -((b*x^n)/a))/(2*a^3*x^2)");

	}

	// {x^(-1+n)*(a+b*x^n)^2, x, 1, (a+b*x^n)^3/(3*b*n)}
	public void test01041() {
		check("Integrate(x^(-1+n)*(a+b*x^n)^2, x)", "(a+b*x^n)^3/(3*b*n)");

	}

	// {x^(-1-3*n)*(a+b*x^n)^2, x, 1, -(a+b*x^n)^3/(3*a*n*x^(3*n))}
	public void test01042() {
		check("Integrate(x^(-1-3*n)*(a+b*x^n)^2, x)", "-(a+b*x^n)^3/(3*a*n*x^(3*n))");

	}

	// {x^(-1+n)*(a+b*x^n)^3, x, 1, (a+b*x^n)^4/(4*b*n)}
	public void test01043() {
		check("Integrate(x^(-1+n)*(a+b*x^n)^3, x)", "(a+b*x^n)^4/(4*b*n)");

	}

	// {x^(-1-4*n)*(a+b*x^n)^3, x, 1, -(a+b*x^n)^4/(4*a*n*x^(4*n))}
	public void test01044() {
		check("Integrate(x^(-1-4*n)*(a+b*x^n)^3, x)", "-(a+b*x^n)^4/(4*a*n*x^(4*n))");

	}

	// {x^(-1+n)*(a+b*x^n)^5, x, 1, (a+b*x^n)^6/(6*b*n)}
	public void test01045() {
		check("Integrate(x^(-1+n)*(a+b*x^n)^5, x)", "(a+b*x^n)^6/(6*b*n)");

	}

	// {x^(-1-6*n)*(a+b*x^n)^5, x, 1, -(a+b*x^n)^6/(6*a*n*x^(6*n))}
	public void test01046() {
		check("Integrate(x^(-1-6*n)*(a+b*x^n)^5, x)", "-(a+b*x^n)^6/(6*a*n*x^(6*n))");

	}

	// {x^(-1+n)*(a+b*x^n)^8, x, 1, (a+b*x^n)^9/(9*b*n)}
	public void test01047() {
		check("Integrate(x^(-1+n)*(a+b*x^n)^8, x)", "(a+b*x^n)^9/(9*b*n)");

	}

	// {x^(-1-9*n)*(a+b*x^n)^8, x, 1, -(a+b*x^n)^9/(9*a*n*x^(9*n))}
	public void test01048() {
		check("Integrate(x^(-1-9*n)*(a+b*x^n)^8, x)", "-(a+b*x^n)^9/(9*a*n*x^(9*n))");

	}

	// {x^(-1+n)*(a+b*x^n)^16, x, 1, (a+b*x^n)^17/(17*b*n)}
	public void test01049() {
		check("Integrate(x^(-1+n)*(a+b*x^n)^16, x)", "(a+b*x^n)^17/(17*b*n)");

	}

	// {x^12*(a+b*x^13)^12, x, 1, (a+b*x^13)^13/(169*b)}
	public void test01050() {
		check("Integrate(x^12*(a+b*x^13)^12, x)", "(a+b*x^13)^13/(169*b)");

	}

	// {x^24*(a+b*x^25)^12, x, 1, (a+b*x^25)^13/(325*b)}
	public void test01051() {
		check("Integrate(x^24*(a+b*x^25)^12, x)", "(a+b*x^25)^13/(325*b)");

	}

	// {x^36*(a+b*x^37)^12, x, 1, (a+b*x^37)^13/(481*b)}
	public void test01052() {
		check("Integrate(x^36*(a+b*x^37)^12, x)", "(a+b*x^37)^13/(481*b)");

	}

	// {x^(12*m)*(a+b*x^(1+12*m))^12, x, 1, (a+b*x^(1+12*m))^13/(13*b*(1+12*m))}
	public void test01053() {
		check("Integrate(x^(12*m)*(a+b*x^(1+12*m))^12, x)", "(a+b*x^(1+12*m))^13/(13*b*(1+12*m))");

	}

	// {x^(12+12*(-1+m))*(a+b*x^(1+12*m))^12, x, 1, (a+b*x^(1+12*m))^13/(13*b*(1+12*m))}
	public void test01054() {
		check("Integrate(x^(12+12*(-1+m))*(a+b*x^(1+12*m))^12, x)", "(a+b*x^(1+12*m))^13/(13*b*(1+12*m))");

	}

	// {x^(-1+n)/(a+b*x^n), x, 1, Log(a+b*x^n)/(b*n)}
	public void test01055() {
		check("Integrate(x^(-1+n)/(a+b*x^n), x)", "Log(a+b*x^n)/(b*n)");

	}

	// {x^(-1+n)/(a+b*x^n), x, 1, Log(a+b*x^n)/(b*n)}
	public void test01056() {
		check("Integrate(x^(-1+n)/(a+b*x^n), x)", "Log(a+b*x^n)/(b*n)");

	}

	// {x^(-1+n)/(2+b*x^n), x, 1, Log(2+b*x^n)/(b*n)}
	public void test01057() {
		check("Integrate(x^(-1+n)/(2+b*x^n), x)", "Log(2+b*x^n)/(b*n)");

	}

	// {x^(-1+n)/(a+b*x^n)^2, x, 1, -(1/(b*n*(a+b*x^n)))}
	public void test01058() {
		check("Integrate(x^(-1+n)/(a+b*x^n)^2, x)", "-(1/(b*n*(a+b*x^n)))");

	}

	// {x^(-1+2*n)/(a+b*x^n)^3, x, 1, x^(2*n)/(2*a*n*(a+b*x^n)^2)}
	public void test01059() {
		check("Integrate(x^(-1+2*n)/(a+b*x^n)^3, x)", "x^(2*n)/(2*a*n*(a+b*x^n)^2)");

	}

	// {x^(-1+n)/(a+b*x^n)^3, x, 1, -1/(2*b*n*(a+b*x^n)^2)}
	public void test01060() {
		check("Integrate(x^(-1+n)/(a+b*x^n)^3, x)", "-1/(2*b*n*(a+b*x^n)^2)");

	}

	// {x^(-1+n)*Sqrt(a+b*x^n), x, 1, (2*(a+b*x^n)^(3/2))/(3*b*n)}
	public void test01061() {
		check("Integrate(x^(-1+n)*Sqrt(a+b*x^n), x)", "(2*(a+b*x^n)^(3/2))/(3*b*n)");

	}

	// {x^(-1+n)/Sqrt(a+b*x^n), x, 1, (2*Sqrt(a+b*x^n))/(b*n)}
	public void test01062() {
		check("Integrate(x^(-1+n)/Sqrt(a+b*x^n), x)", "(2*Sqrt(a+b*x^n))/(b*n)");

	}

	// {x^m/(a+b*x^n), x, 1, (x^(1+m)*Hypergeometric2F1(1, (1+m)/n, (1+m+n)/n, -((b*x^n)/a)))/(a*(1+m))}
	public void test01063() {
		check("Integrate(x^m/(a+b*x^n), x)",
				"(x^(1+m)*Hypergeometric2F1(1, (1+m)/n, (1+m+n)/n, -((b*x^n)/a)))/(a*(1+m))");

	}

	// {x^m/(a+b*x^n)^2, x, 1, (x^(1+m)*Hypergeometric2F1(2, (1+m)/n, (1+m+n)/n, -((b*x^n)/a)))/(a^2*(1+m))}
	public void test01064() {
		check("Integrate(x^m/(a+b*x^n)^2, x)",
				"(x^(1+m)*Hypergeometric2F1(2, (1+m)/n, (1+m+n)/n, -((b*x^n)/a)))/(a^2*(1+m))");

	}

	// {x^m/(a+b*x^n)^3, x, 1, (x^(1+m)*Hypergeometric2F1(3, (1+m)/n, (1+m+n)/n, -((b*x^n)/a)))/(a^3*(1+m))}
	public void test01065() {
		check("Integrate(x^m/(a+b*x^n)^3, x)",
				"(x^(1+m)*Hypergeometric2F1(3, (1+m)/n, (1+m+n)/n, -((b*x^n)/a)))/(a^3*(1+m))");

	}

	// {x^(-1-n/2)/Sqrt(a+b*x^n), x, 1, (-2*Sqrt(a+b*x^n))/(a*n*x^(n/2))}
	public void test01066() {
		check("Integrate(x^(-1-n/2)/Sqrt(a+b*x^n), x)", "(-2*Sqrt(a+b*x^n))/(a*n*x^(n/2))");

	}

	// {(a+b*x^n)^(-1-n^(-1)), x, 1, x/(a*(a+b*x^n)^n^(-1))}
	public void test01067() {
		check("Integrate((a+b*x^n)^(-1-n^(-1)), x)", "x/(a*(a+b*x^n)^n^(-1))");

	}

	// {x^(-1+n)*(a+b*x^n)^p, x, 1, (a+b*x^n)^(1+p)/(b*n*(1+p))}
	public void test01068() {
		check("Integrate(x^(-1+n)*(a+b*x^n)^p, x)", "(a+b*x^n)^(1+p)/(b*n*(1+p))");

	}

	// {x^(-1-n-n*p)*(a+b*x^n)^p, x, 1, -((a+b*x^n)^(1+p)/(a*n*(1+p)*x^(n*(1+p))))}
	public void test01069() {
		check("Integrate(x^(-1-n-n*p)*(a+b*x^n)^p, x)", "-((a+b*x^n)^(1+p)/(a*n*(1+p)*x^(n*(1+p))))");

	}

	// {x^(-1-9*n)*(a+b*x^n)^8, x, 1, -(a+b*x^n)^9/(9*a*n*x^(9*n))}
	public void test01070() {
		check("Integrate(x^(-1-9*n)*(a+b*x^n)^8, x)", "-(a+b*x^n)^9/(9*a*n*x^(9*n))");

	}

	// {x^(-4-3*p)*(a+b*x^3)^p, x, 1, -(a+b*x^3)^(1+p)/(3*a*(1+p)*x^(3*(1+p)))}
	public void test01071() {
		check("Integrate(x^(-4-3*p)*(a+b*x^3)^p, x)", "-(a+b*x^3)^(1+p)/(3*a*(1+p)*x^(3*(1+p)))");

	}

	// {(a+b*x^3)^8/x^28, x, 1, -(a+b*x^3)^9/(27*a*x^27)}
	public void test01072() {
		check("Integrate((a+b*x^3)^8/x^28, x)", "-(a+b*x^3)^9/(27*a*x^27)");

	}

	// {(a+b*x^n)^(-((1+n)/n)), x, 1, x/(a*(a+b*x^n)^n^(-1))}
	public void test01073() {
		check("Integrate((a+b*x^n)^(-((1+n)/n)), x)", "x/(a*(a+b*x^n)^n^(-1))");

	}

	// {x^m/(a+b*x^(1+m)), x, 1, Log(a+b*x^(1+m))/(b*(1+m))}
	public void test01074() {
		check("Integrate(x^m/(a+b*x^(1+m)), x)", "Log(a+b*x^(1+m))/(b*(1+m))");

	}

	// {x^m*(a+b*x^(1+m))^n, x, 1, (a+b*x^(1+m))^(1+n)/(b*(1+m)*(1+n))}
	public void test01075() {
		check("Integrate(x^m*(a+b*x^(1+m))^n, x)", "(a+b*x^(1+m))^(1+n)/(b*(1+m)*(1+n))");

	}

	// {x^m/(a+b*x^(2+2*m))^(3/2), x, 1, x^(1+m)/(a*(1+m)*Sqrt(a+b*x^(2*(1+m))))}
	public void test01076() {
		check("Integrate(x^m/(a+b*x^(2+2*m))^(3/2), x)", "x^(1+m)/(a*(1+m)*Sqrt(a+b*x^(2*(1+m))))");

	}

	// {x^n*Sqrt(1+x^(1+n)), x, 1, (2*(1+x^(1+n))^(3/2))/(3*(1+n))}
	public void test01077() {
		check("Integrate(x^n*Sqrt(1+x^(1+n)), x)", "(2*(1+x^(1+n))^(3/2))/(3*(1+n))");

	}

	// {x^n*Sqrt(a^2+x^(1+n)), x, 1, (2*(a^2+x^(1+n))^(3/2))/(3*(1+n))}
	public void test01078() {
		check("Integrate(x^n*Sqrt(a^2+x^(1+n)), x)", "(2*(a^2+x^(1+n))^(3/2))/(3*(1+n))");

	}

	// {(c*x)^(4+n)/(a+b*x^n), x, 1, ((c*x)^(5+n)*Hypergeometric2F1(1, (5+n)/n, 2+5/n, -((b*x^n)/a)))/(a*c*(5
	// +n))}
	public void test01079() {
		check("Integrate((c*x)^(4+n)/(a+b*x^n), x)",
				"((c*x)^(5+n)*Hypergeometric2F1(1, (5+n)/n, 2+5/n, -((b*x^n)/a)))/(a*c*(5+n))");

	}

	// {(c*x)^(3+n)/(a+b*x^n), x, 1, ((c*x)^(4+n)*Hypergeometric2F1(1, (4+n)/n, 2*(1+2/n),
	// -((b*x^n)/a)))/(a*c*(4+n))}
	public void test01080() {
		check("Integrate((c*x)^(3+n)/(a+b*x^n), x)",
				"((c*x)^(4+n)*Hypergeometric2F1(1, (4+n)/n, 2*(1+2/n), -((b*x^n)/a)))/(a*c*(4+n))");

	}

	// {(c*x)^(2+n)/(a+b*x^n), x, 1, ((c*x)^(3+n)*Hypergeometric2F1(1, (3+n)/n, 2+3/n, -((b*x^n)/a)))/(a*c*(3
	// +n))}
	public void test01081() {
		check("Integrate((c*x)^(2+n)/(a+b*x^n), x)",
				"((c*x)^(3+n)*Hypergeometric2F1(1, (3+n)/n, 2+3/n, -((b*x^n)/a)))/(a*c*(3+n))");

	}

	// {(c*x)^(1+n)/(a+b*x^n), x, 1, ((c*x)^(2+n)*Hypergeometric2F1(1, (2+n)/n, 2*(1+n^(-1)),
	// -((b*x^n)/a)))/(a*c*(2+n))}
	public void test01082() {
		check("Integrate((c*x)^(1+n)/(a+b*x^n), x)",
				"((c*x)^(2+n)*Hypergeometric2F1(1, (2+n)/n, 2*(1+n^(-1)), -((b*x^n)/a)))/(a*c*(2+n))");

	}

	// {(c*x)^n/(a+b*x^n), x, 1, ((c*x)^(1+n)*Hypergeometric2F1(1, 1+n^(-1), 2+n^(-1), -((b*x^n)/a)))/(a*c*(1 +
	// n))}
	public void test01083() {
		check("Integrate((c*x)^n/(a+b*x^n), x)",
				"((c*x)^(1+n)*Hypergeometric2F1(1, 1+n^(-1), 2+n^(-1), -((b*x^n)/a)))/(a*c*(1+n))");

	}

	// {(c*x)^(-2+n)/(a+b*x^n), x, 1, -(((c*x)^(-1+n)*Hypergeometric2F1(1, -((1-n)/n), 2-n^(-1),
	// -((b*x^n)/a)))/(a*c*(1-n)))}
	public void test01084() {
		check("Integrate((c*x)^(-2+n)/(a+b*x^n), x)",
				"-(((c*x)^(-1+n)*Hypergeometric2F1(1, -((1-n)/n), 2-n^(-1), -((b*x^n)/a)))/(a*c*(1-n)))");

	}

	// {(c*x)^(-3+n)/(a+b*x^n), x, 1, -(((c*x)^(-2+n)*Hypergeometric2F1(1, -((2-n)/n), 2*(1-n^(-1)),
	// -((b*x^n)/a)))/(a*c*(2-n)))}
	public void test01085() {
		check("Integrate((c*x)^(-3+n)/(a+b*x^n), x)",
				"-(((c*x)^(-2+n)*Hypergeometric2F1(1, -((2-n)/n), 2*(1-n^(-1)), -((b*x^n)/a)))/(a*c*(2-n)))");

	}

	// {(c*x)^(-1+n)/(a+b*x^n)^2, x, 1, (c*x)^n/(a*c*n*(a+b*x^n))}
	public void test01086() {
		check("Integrate((c*x)^(-1+n)/(a+b*x^n)^2, x)", "(c*x)^n/(a*c*n*(a+b*x^n))");

	}

	// {(c*x)^(-1-n/2)/Sqrt(a+b*x^n), x, 1, (-2*Sqrt(a+b*x^n))/(a*c*n*(c*x)^(n/2))}
	public void test01087() {
		check("Integrate((c*x)^(-1-n/2)/Sqrt(a+b*x^n), x)", "(-2*Sqrt(a+b*x^n))/(a*c*n*(c*x)^(n/2))");

	}

	// {(c*x)^(-1-n-n*p)*(a+b*x^n)^p, x, 1, -((a+b*x^n)^(1+p)/(a*c*n*(1+p)*(c*x)^(n*(1+p))))}
	public void test01088() {
		check("Integrate((c*x)^(-1-n-n*p)*(a+b*x^n)^p, x)", "-((a+b*x^n)^(1+p)/(a*c*n*(1+p)*(c*x)^(n*(1+p))))");

	}

	// {(2+x)/(1+(2+x)^2), x, 1, Log(1+(2+x)^2)/2}
	public void test01089() {
		check("Integrate((2+x)/(1+(2+x)^2), x)", "Log(1+(2+x)^2)/2");

	}

	// {(2+x)/(1+(2+x)^2)^2, x, 1, -1/(2*(1+(2+x)^2))}
	public void test01090() {
		check("Integrate((2+x)/(1+(2+x)^2)^2, x)", "-1/(2*(1+(2+x)^2))");

	}

	// {(2+x)/(1+(2+x)^2)^3, x, 1, -1/(4*(1+(2+x)^2)^2)}
	public void test01091() {
		check("Integrate((2+x)/(1+(2+x)^2)^3, x)", "-1/(4*(1+(2+x)^2)^2)");

	}

	// {(c+d*x)*(a+b*(c+d*x)^2)^p, x, 1, (a+b*(c+d*x)^2)^(1+p)/(2*b*d*(1+p))}
	public void test01092() {
		check("Integrate((c+d*x)*(a+b*(c+d*x)^2)^p, x)", "(a+b*(c+d*x)^2)^(1+p)/(2*b*d*(1+p))");

	}

	// {(c+d*x)^2/(a+b*(c+d*x)^3), x, 1, Log(a+b*(c+d*x)^3)/(3*b*d)}
	public void test01093() {
		check("Integrate((c+d*x)^2/(a+b*(c+d*x)^3), x)", "Log(a+b*(c+d*x)^3)/(3*b*d)");

	}

	// {(c+d*x)^2/(a+b*(c+d*x)^3)^2, x, 1, -1/(3*b*d*(a+b*(c+d*x)^3))}
	public void test01094() {
		check("Integrate((c+d*x)^2/(a+b*(c+d*x)^3)^2, x)", "-1/(3*b*d*(a+b*(c+d*x)^3))");

	}

	// {(c+d*x)^2/(a+b*(c+d*x)^3)^3, x, 1, -1/(6*b*d*(a+b*(c+d*x)^3)^2)}
	public void test01095() {
		check("Integrate((c+d*x)^2/(a+b*(c+d*x)^3)^3, x)", "-1/(6*b*d*(a+b*(c+d*x)^3)^2)");

	}

	// {(c*e+d*e*x)^2/(a+b*(c+d*x)^3), x, 1, (e^2*Log(a+b*(c+d*x)^3))/(3*b*d)}
	public void test01096() {
		check("Integrate((c*e+d*e*x)^2/(a+b*(c+d*x)^3), x)", "(e^2*Log(a+b*(c+d*x)^3))/(3*b*d)");

	}

	// {(c*e+d*e*x)^2/(a+b*(c+d*x)^3)^2, x, 1, -e^2/(3*b*d*(a+b*(c+d*x)^3))}
	public void test01097() {
		check("Integrate((c*e+d*e*x)^2/(a+b*(c+d*x)^3)^2, x)", "-e^2/(3*b*d*(a+b*(c+d*x)^3))");

	}

	// {(c*e+d*e*x)^2/(a+b*(c+d*x)^3)^3, x, 1, -e^2/(6*b*d*(a+b*(c+d*x)^3)^2)}
	public void test01098() {
		check("Integrate((c*e+d*e*x)^2/(a+b*(c+d*x)^3)^3, x)", "-e^2/(6*b*d*(a+b*(c+d*x)^3)^2)");

	}

	// {(c+d*x)^3*(a+b*(c+d*x)^4)^p, x, 1, (a+b*(c+d*x)^4)^(1+p)/(4*b*d*(1+p))}
	public void test01099() {
		check("Integrate((c+d*x)^3*(a+b*(c+d*x)^4)^p, x)", "(a+b*(c+d*x)^4)^(1+p)/(4*b*d*(1+p))");

	}

	// {(c+d*x)^3*(a+b*(c+d*x)^4), x, 1, (a+b*(c+d*x)^4)^2/(8*b*d)}
	public void test01100() {
		check("Integrate((c+d*x)^3*(a+b*(c+d*x)^4), x)", "(a+b*(c+d*x)^4)^2/(8*b*d)");

	}

	// {(c+d*x)^3*(a+b*(c+d*x)^4)^2, x, 1, (a+b*(c+d*x)^4)^3/(12*b*d)}
	public void test01101() {
		check("Integrate((c+d*x)^3*(a+b*(c+d*x)^4)^2, x)", "(a+b*(c+d*x)^4)^3/(12*b*d)");

	}

	// {(c+d*x)^3*(a+b*(c+d*x)^4)^3, x, 1, (a+b*(c+d*x)^4)^4/(16*b*d)}
	public void test01102() {
		check("Integrate((c+d*x)^3*(a+b*(c+d*x)^4)^3, x)", "(a+b*(c+d*x)^4)^4/(16*b*d)");

	}

	// {(c+d*x)^3/(a+b*(c+d*x)^4), x, 1, Log(a+b*(c+d*x)^4)/(4*b*d)}
	public void test01103() {
		check("Integrate((c+d*x)^3/(a+b*(c+d*x)^4), x)", "Log(a+b*(c+d*x)^4)/(4*b*d)");

	}

	// {(c+d*x)^3/(a+b*(c+d*x)^4)^2, x, 1, -1/(4*b*d*(a+b*(c+d*x)^4))}
	public void test01104() {
		check("Integrate((c+d*x)^3/(a+b*(c+d*x)^4)^2, x)", "-1/(4*b*d*(a+b*(c+d*x)^4))");

	}

	// {(c+d*x)^3/(a+b*(c+d*x)^4)^3, x, 1, -1/(8*b*d*(a+b*(c+d*x)^4)^2)}
	public void test01105() {
		check("Integrate((c+d*x)^3/(a+b*(c+d*x)^4)^3, x)", "-1/(8*b*d*(a+b*(c+d*x)^4)^2)");

	}

	// {(c+d*x^3)^(-4/3), x, 1, x/(c*(c+d*x^3)^(1/3))}
	public void test01106() {
		check("Integrate((c+d*x^3)^(-4/3), x)", "x/(c*(c+d*x^3)^(1/3))");

	}

	// {(a+b*x^3)^(-1-(b*c)/(3*b*c-3*a*d))*(c+d*x^3)^(-1+(a*d)/(3*b*c-3*a*d)), x, 1, (x*(c +
	// d*x^3)^((a*d)/(3*b*c-3*a*d)))/(a*c*(a+b*x^3)^((b*c)/(3*b*c-3*a*d)))}
	public void test01107() {
		check("Integrate((a+b*x^3)^(-1-(b*c)/(3*b*c-3*a*d))*(c+d*x^3)^(-1+(a*d)/(3*b*c-3*a*d)), x)",
				"(x*(c+d*x^3)^((a*d)/(3*b*c-3*a*d)))/(a*c*(a+b*x^3)^((b*c)/(3*b*c-3*a*d)))");

	}

	// {Sqrt(a-b*x^4)/(a*c+b*c*x^4), x, 1, ArcTan((b^(1/4)*x*(Sqrt(a)+Sqrt(b)*x^2))/(a^(1/4)*Sqrt(a -
	// b*x^4)))/(2*a^(1/4)*b^(1/4)*c)+ArcTanh((b^(1/4)*x*(Sqrt(a)-Sqrt(b)*x^2))/(a^(1/4)*Sqrt(a -
	// b*x^4)))/(2*a^(1/4)*b^(1/4)*c)}
	public void test01108() {
		check("Integrate(Sqrt(a-b*x^4)/(a*c+b*c*x^4), x)",
				"ArcTan((b^(1/4)*x*(Sqrt(a)+Sqrt(b)*x^2))/(a^(1/4)*Sqrt(a-b*x^4)))/(2*a^(1/4)*b^(1/4)*c)+ArcTanh((b^(1/4)*x*(Sqrt(a)-Sqrt(b)*x^2))/(a^(1/4)*Sqrt(a-b*x^4)))/(2*a^(1/4)*b^(1/4)*c)");

	}

	// {(a+b*x^n)^p*(c+d*x^n)^(-1-n^(-1)-p), x, 1, (x*(a+b*x^n)^p*(c+d*x^n)^(-n^(-1) -
	// p)*Hypergeometric2F1(n^(-1), -p, 1+n^(-1), -(((b*c-a*d)*x^n)/(a*(c+d*x^n)))))/(c*((c*(a+b*x^n))/(a*(c +
	// d*x^n)))^p)}
	public void test01109() {
		check("Integrate((a+b*x^n)^p*(c+d*x^n)^(-1-n^(-1)-p), x)",
				"(x*(a+b*x^n)^p*(c+d*x^n)^(-n^(-1)-p)*Hypergeometric2F1(n^(-1), -p, 1+n^(-1), -(((b*c-a*d)*x^n)/(a*(c+d*x^n)))))/(c*((c*(a+b*x^n))/(a*(c+d*x^n)))^p)");

	}

	// {(c+d*x^n)^(-1-n^(-1)), x, 1, x/(c*(c+d*x^n)^n^(-1))}
	public void test01110() {
		check("Integrate((c+d*x^n)^(-1-n^(-1)), x)", "x/(c*(c+d*x^n)^n^(-1))");

	}

	// {1/((a+b*x^n)*(c+d*x^n)^n^(-1)), x, 1, (x*Hypergeometric2F1(1, n^(-1), 1+n^(-1), -(((b*c-a*d)*x^n)/(a*(c
	// +d*x^n)))))/(a*(c+d*x^n)^n^(-1))}
	public void test01111() {
		check("Integrate(1/((a+b*x^n)*(c+d*x^n)^n^(-1)), x)",
				"(x*Hypergeometric2F1(1, n^(-1), 1+n^(-1), -(((b*c-a*d)*x^n)/(a*(c+d*x^n)))))/(a*(c+d*x^n)^n^(-1))");

	}

	// {(c+d*x^n)^(1-n^(-1))/(a+b*x^n)^2, x, 1, (c*x*Hypergeometric2F1(2, n^(-1), 1+n^(-1), -(((b*c -
	// a*d)*x^n)/(a*(c+d*x^n)))))/(a^2*(c+d*x^n)^n^(-1))}
	public void test01112() {
		check("Integrate((c+d*x^n)^(1-n^(-1))/(a+b*x^n)^2, x)",
				"(c*x*Hypergeometric2F1(2, n^(-1), 1+n^(-1), -(((b*c-a*d)*x^n)/(a*(c+d*x^n)))))/(a^2*(c+d*x^n)^n^(-1))");

	}

	// {(c+d*x^n)^(2-n^(-1))/(a+b*x^n)^3, x, 1, (c^2*x*Hypergeometric2F1(3, n^(-1), 1+n^(-1), -(((b*c -
	// a*d)*x^n)/(a*(c+d*x^n)))))/(a^3*(c+d*x^n)^n^(-1))}
	public void test01113() {
		check("Integrate((c+d*x^n)^(2-n^(-1))/(a+b*x^n)^3, x)",
				"(c^2*x*Hypergeometric2F1(3, n^(-1), 1+n^(-1), -(((b*c-a*d)*x^n)/(a*(c+d*x^n)))))/(a^3*(c+d*x^n)^n^(-1))");

	}

	// {(a+b*x^n)^((a*d*n-b*c*(1+n))/((b*c-a*d)*n))*(c+d*x^n)^((a*d-b*c*n+a*d*n)/(b*c*n-a*d*n)), x, 1,
	// (x*(c+d*x^n)^((a*d)/((b*c-a*d)*n)))/(a*c*(a+b*x^n)^((b*c)/((b*c-a*d)*n)))}
	public void test01114() {
		check("Integrate((a+b*x^n)^((a*d*n-b*c*(1+n))/((b*c-a*d)*n))*(c+d*x^n)^((a*d-b*c*n+a*d*n)/(b*c*n-a*d*n)), x)",
				"(x*(c+d*x^n)^((a*d)/((b*c-a*d)*n)))/(a*c*(a+b*x^n)^((b*c)/((b*c-a*d)*n)))");

	}

	// {(c+d*x^2)/(x^((2*b^2*c+a^2*d)/(b^2*c+a^2*d))*Sqrt(-a+b*x)*Sqrt(a+b*x)), x, 1, ((c/a^2+d/b^2)*Sqrt(-a
	// +b*x)*Sqrt(a+b*x))/x^((b^2*c)/(b^2*c+a^2*d))}
	public void test01115() {
		check("Integrate((c+d*x^2)/(x^((2*b^2*c+a^2*d)/(b^2*c+a^2*d))*Sqrt(-a+b*x)*Sqrt(a+b*x)), x)",
				"((c/a^2+d/b^2)*Sqrt(-a+b*x)*Sqrt(a+b*x))/x^((b^2*c)/(b^2*c+a^2*d))");

	}

	// {x/(Sqrt(c+d*x^3)*(4*c+d*x^3)), x, 1, -ArcTan((Sqrt(3)*c^(1/6)*(c^(1/3)+2^(1/3)*d^(1/3)*x))/Sqrt(c +
	// d*x^3))/(3*2^(2/3)*Sqrt(3)*c^(5/6)*d^(2/3))+ArcTan(Sqrt(c +
	// d*x^3)/(Sqrt(3)*Sqrt(c)))/(3*2^(2/3)*Sqrt(3)*c^(5/6)*d^(2/3))-ArcTanh((c^(1/6)*(c^(1/3) -
	// 2^(1/3)*d^(1/3)*x))/Sqrt(c+d*x^3))/(3*2^(2/3)*c^(5/6)*d^(2/3))+ArcTanh(Sqrt(c +
	// d*x^3)/Sqrt(c))/(9*2^(2/3)*c^(5/6)*d^(2/3))}
	public void test01116() {
		check("Integrate(x/(Sqrt(c+d*x^3)*(4*c+d*x^3)), x)",
				"-ArcTan((Sqrt(3)*c^(1/6)*(c^(1/3)+2^(1/3)*d^(1/3)*x))/Sqrt(c+d*x^3))/(3*2^(2/3)*Sqrt(3)*c^(5/6)*d^(2/3))+ArcTan(Sqrt(c+d*x^3)/(Sqrt(3)*Sqrt(c)))/(3*2^(2/3)*Sqrt(3)*c^(5/6)*d^(2/3))-ArcTanh((c^(1/6)*(c^(1/3)-2^(1/3)*d^(1/3)*x))/Sqrt(c+d*x^3))/(3*2^(2/3)*c^(5/6)*d^(2/3))+ArcTanh(Sqrt(c+d*x^3)/Sqrt(c))/(9*2^(2/3)*c^(5/6)*d^(2/3))");

	}

	// {x/(Sqrt(1-x^3)*(4-x^3)), x, 1, -ArcTan((Sqrt(3)*(1-2^(1/3)*x))/Sqrt(1-x^3))/(3*2^(2/3)*Sqrt(3)) +
	// ArcTan(Sqrt(1-x^3)/Sqrt(3))/(3*2^(2/3)*Sqrt(3))-ArcTanh((1+2^(1/3)*x)/Sqrt(1-x^3))/(3*2^(2/3)) +
	// ArcTanh(Sqrt(1-x^3))/(9*2^(2/3))}
	public void test01117() {
		check("Integrate(x/(Sqrt(1-x^3)*(4-x^3)), x)",
				"-ArcTan((Sqrt(3)*(1-2^(1/3)*x))/Sqrt(1-x^3))/(3*2^(2/3)*Sqrt(3))+ArcTan(Sqrt(1-x^3)/Sqrt(3))/(3*2^(2/3)*Sqrt(3))-ArcTanh((1+2^(1/3)*x)/Sqrt(1-x^3))/(3*2^(2/3))+ArcTanh(Sqrt(1-x^3))/(9*2^(2/3))");

	}

	// {x/(Sqrt(a+b*x^3)*(2*(5+3*Sqrt(3))*a+b*x^3)), x, 1, -((2-Sqrt(3))*ArcTan((3^(1/4)*(1 +
	// Sqrt(3))*a^(1/6)*(a^(1/3)+b^(1/3)*x))/(Sqrt(2)*Sqrt(a+b*x^3))))/(2*Sqrt(2)*3^(3/4)*a^(5/6)*b^(2/3))-((2 -
	// Sqrt(3))*ArcTan(((1-Sqrt(3))*Sqrt(a+b*x^3))/(Sqrt(2)*3^(3/4)*Sqrt(a))))/(3*Sqrt(2)*3^(3/4)*a^(5/6)*b^(2/3)) -
	// ((2-Sqrt(3))*ArcTanh((3^(1/4)*a^(1/6)*((1+Sqrt(3))*a^(1/3)-2*b^(1/3)*x))/(Sqrt(2)*Sqrt(a +
	// b*x^3))))/(3*Sqrt(2)*3^(1/4)*a^(5/6)*b^(2/3))-((2-Sqrt(3))*ArcTanh((3^(1/4)*(1-Sqrt(3))*a^(1/6)*(a^(1/3) +
	// b^(1/3)*x))/(Sqrt(2)*Sqrt(a+b*x^3))))/(6*Sqrt(2)*3^(1/4)*a^(5/6)*b^(2/3))}
	public void test01118() {
		check("Integrate(x/(Sqrt(a+b*x^3)*(2*(5+3*Sqrt(3))*a+b*x^3)), x)",
				"-((2-Sqrt(3))*ArcTan((3^(1/4)*(1+Sqrt(3))*a^(1/6)*(a^(1/3)+b^(1/3)*x))/(Sqrt(2)*Sqrt(a+b*x^3))))/(2*Sqrt(2)*3^(3/4)*a^(5/6)*b^(2/3))-((2-Sqrt(3))*ArcTan(((1-Sqrt(3))*Sqrt(a+b*x^3))/(Sqrt(2)*3^(3/4)*Sqrt(a))))/(3*Sqrt(2)*3^(3/4)*a^(5/6)*b^(2/3))-((2-Sqrt(3))*ArcTanh((3^(1/4)*a^(1/6)*((1+Sqrt(3))*a^(1/3)-2*b^(1/3)*x))/(Sqrt(2)*Sqrt(a+b*x^3))))/(3*Sqrt(2)*3^(1/4)*a^(5/6)*b^(2/3))-((2-Sqrt(3))*ArcTanh((3^(1/4)*(1-Sqrt(3))*a^(1/6)*(a^(1/3)+b^(1/3)*x))/(Sqrt(2)*Sqrt(a+b*x^3))))/(6*Sqrt(2)*3^(1/4)*a^(5/6)*b^(2/3))");

	}

	// {x/(Sqrt(a-b*x^3)*(2*(5+3*Sqrt(3))*a-b*x^3)), x, 1, -((2-Sqrt(3))*ArcTan((3^(1/4)*(1 +
	// Sqrt(3))*a^(1/6)*(a^(1/3)-b^(1/3)*x))/(Sqrt(2)*Sqrt(a-b*x^3))))/(2*Sqrt(2)*3^(3/4)*a^(5/6)*b^(2/3))-((2 -
	// Sqrt(3))*ArcTan(((1-Sqrt(3))*Sqrt(a-b*x^3))/(Sqrt(2)*3^(3/4)*Sqrt(a))))/(3*Sqrt(2)*3^(3/4)*a^(5/6)*b^(2/3)) -
	// ((2-Sqrt(3))*ArcTanh((3^(1/4)*(1-Sqrt(3))*a^(1/6)*(a^(1/3)-b^(1/3)*x))/(Sqrt(2)*Sqrt(a -
	// b*x^3))))/(6*Sqrt(2)*3^(1/4)*a^(5/6)*b^(2/3))-((2-Sqrt(3))*ArcTanh((3^(1/4)*a^(1/6)*((1+Sqrt(3))*a^(1/3) +
	// 2*b^(1/3)*x))/(Sqrt(2)*Sqrt(a-b*x^3))))/(3*Sqrt(2)*3^(1/4)*a^(5/6)*b^(2/3))}
	public void test01119() {
		check("Integrate(x/(Sqrt(a-b*x^3)*(2*(5+3*Sqrt(3))*a-b*x^3)), x)",
				"-((2-Sqrt(3))*ArcTan((3^(1/4)*(1+Sqrt(3))*a^(1/6)*(a^(1/3)-b^(1/3)*x))/(Sqrt(2)*Sqrt(a-b*x^3))))/(2*Sqrt(2)*3^(3/4)*a^(5/6)*b^(2/3))-((2-Sqrt(3))*ArcTan(((1-Sqrt(3))*Sqrt(a-b*x^3))/(Sqrt(2)*3^(3/4)*Sqrt(a))))/(3*Sqrt(2)*3^(3/4)*a^(5/6)*b^(2/3))-((2-Sqrt(3))*ArcTanh((3^(1/4)*(1-Sqrt(3))*a^(1/6)*(a^(1/3)-b^(1/3)*x))/(Sqrt(2)*Sqrt(a-b*x^3))))/(6*Sqrt(2)*3^(1/4)*a^(5/6)*b^(2/3))-((2-Sqrt(3))*ArcTanh((3^(1/4)*a^(1/6)*((1+Sqrt(3))*a^(1/3)+2*b^(1/3)*x))/(Sqrt(2)*Sqrt(a-b*x^3))))/(3*Sqrt(2)*3^(1/4)*a^(5/6)*b^(2/3))");

	}

	// {x/(Sqrt(-a+b*x^3)*(-2*(5+3*Sqrt(3))*a+b*x^3)), x, 1, ((2-Sqrt(3))*ArcTan((3^(1/4)*(1 -
	// Sqrt(3))*a^(1/6)*(a^(1/3)-b^(1/3)*x))/(Sqrt(2)*Sqrt(-a+b*x^3))))/(6*Sqrt(2)*3^(1/4)*a^(5/6)*b^(2/3))+((2 -
	// Sqrt(3))*ArcTan((3^(1/4)*a^(1/6)*((1+Sqrt(3))*a^(1/3)+2*b^(1/3)*x))/(Sqrt(2)*Sqrt(-a +
	// b*x^3))))/(3*Sqrt(2)*3^(1/4)*a^(5/6)*b^(2/3))+((2-Sqrt(3))*ArcTanh((3^(1/4)*(1+Sqrt(3))*a^(1/6)*(a^(1/3) -
	// b^(1/3)*x))/(Sqrt(2)*Sqrt(-a+b*x^3))))/(2*Sqrt(2)*3^(3/4)*a^(5/6)*b^(2/3))-((2-Sqrt(3))*ArcTanh(((1 -
	// Sqrt(3))*Sqrt(-a+b*x^3))/(Sqrt(2)*3^(3/4)*Sqrt(a))))/(3*Sqrt(2)*3^(3/4)*a^(5/6)*b^(2/3))}
	public void test01120() {
		check("Integrate(x/(Sqrt(-a+b*x^3)*(-2*(5+3*Sqrt(3))*a+b*x^3)), x)",
				"((2-Sqrt(3))*ArcTan((3^(1/4)*(1-Sqrt(3))*a^(1/6)*(a^(1/3)-b^(1/3)*x))/(Sqrt(2)*Sqrt(-a+b*x^3))))/(6*Sqrt(2)*3^(1/4)*a^(5/6)*b^(2/3))+((2-Sqrt(3))*ArcTan((3^(1/4)*a^(1/6)*((1+Sqrt(3))*a^(1/3)+2*b^(1/3)*x))/(Sqrt(2)*Sqrt(-a+b*x^3))))/(3*Sqrt(2)*3^(1/4)*a^(5/6)*b^(2/3))+((2-Sqrt(3))*ArcTanh((3^(1/4)*(1+Sqrt(3))*a^(1/6)*(a^(1/3)-b^(1/3)*x))/(Sqrt(2)*Sqrt(-a+b*x^3))))/(2*Sqrt(2)*3^(3/4)*a^(5/6)*b^(2/3))-((2-Sqrt(3))*ArcTanh(((1-Sqrt(3))*Sqrt(-a+b*x^3))/(Sqrt(2)*3^(3/4)*Sqrt(a))))/(3*Sqrt(2)*3^(3/4)*a^(5/6)*b^(2/3))");

	}

	// {x/(Sqrt(-a-b*x^3)*(-2*(5+3*Sqrt(3))*a-b*x^3)), x, 1, ((2-Sqrt(3))*ArcTan((3^(1/4)*a^(1/6)*((1 +
	// Sqrt(3))*a^(1/3)-2*b^(1/3)*x))/(Sqrt(2)*Sqrt(-a-b*x^3))))/(3*Sqrt(2)*3^(1/4)*a^(5/6)*b^(2/3))+((2 -
	// Sqrt(3))*ArcTan((3^(1/4)*(1-Sqrt(3))*a^(1/6)*(a^(1/3)+b^(1/3)*x))/(Sqrt(2)*Sqrt(-a -
	// b*x^3))))/(6*Sqrt(2)*3^(1/4)*a^(5/6)*b^(2/3))+((2-Sqrt(3))*ArcTanh((3^(1/4)*(1+Sqrt(3))*a^(1/6)*(a^(1/3) +
	// b^(1/3)*x))/(Sqrt(2)*Sqrt(-a-b*x^3))))/(2*Sqrt(2)*3^(3/4)*a^(5/6)*b^(2/3))-((2-Sqrt(3))*ArcTanh(((1 -
	// Sqrt(3))*Sqrt(-a-b*x^3))/(Sqrt(2)*3^(3/4)*Sqrt(a))))/(3*Sqrt(2)*3^(3/4)*a^(5/6)*b^(2/3))}
	public void test01121() {
		check("Integrate(x/(Sqrt(-a-b*x^3)*(-2*(5+3*Sqrt(3))*a-b*x^3)), x)",
				"((2-Sqrt(3))*ArcTan((3^(1/4)*a^(1/6)*((1+Sqrt(3))*a^(1/3)-2*b^(1/3)*x))/(Sqrt(2)*Sqrt(-a-b*x^3))))/(3*Sqrt(2)*3^(1/4)*a^(5/6)*b^(2/3))+((2-Sqrt(3))*ArcTan((3^(1/4)*(1-Sqrt(3))*a^(1/6)*(a^(1/3)+b^(1/3)*x))/(Sqrt(2)*Sqrt(-a-b*x^3))))/(6*Sqrt(2)*3^(1/4)*a^(5/6)*b^(2/3))+((2-Sqrt(3))*ArcTanh((3^(1/4)*(1+Sqrt(3))*a^(1/6)*(a^(1/3)+b^(1/3)*x))/(Sqrt(2)*Sqrt(-a-b*x^3))))/(2*Sqrt(2)*3^(3/4)*a^(5/6)*b^(2/3))-((2-Sqrt(3))*ArcTanh(((1-Sqrt(3))*Sqrt(-a-b*x^3))/(Sqrt(2)*3^(3/4)*Sqrt(a))))/(3*Sqrt(2)*3^(3/4)*a^(5/6)*b^(2/3))");

	}

	// {x/(Sqrt(a+b*x^3)*(2*(5-3*Sqrt(3))*a+b*x^3)), x, 1, -((2+Sqrt(3))*ArcTan((3^(1/4)*a^(1/6)*((1 -
	// Sqrt(3))*a^(1/3)-2*b^(1/3)*x))/(Sqrt(2)*Sqrt(a+b*x^3))))/(3*Sqrt(2)*3^(1/4)*a^(5/6)*b^(2/3))-((2 +
	// Sqrt(3))*ArcTan((3^(1/4)*(1+Sqrt(3))*a^(1/6)*(a^(1/3)+b^(1/3)*x))/(Sqrt(2)*Sqrt(a +
	// b*x^3))))/(6*Sqrt(2)*3^(1/4)*a^(5/6)*b^(2/3))+((2+Sqrt(3))*ArcTanh((3^(1/4)*(1-Sqrt(3))*a^(1/6)*(a^(1/3) +
	// b^(1/3)*x))/(Sqrt(2)*Sqrt(a+b*x^3))))/(2*Sqrt(2)*3^(3/4)*a^(5/6)*b^(2/3))+((2+Sqrt(3))*ArcTanh(((1 +
	// Sqrt(3))*Sqrt(a+b*x^3))/(Sqrt(2)*3^(3/4)*Sqrt(a))))/(3*Sqrt(2)*3^(3/4)*a^(5/6)*b^(2/3))}
	public void test01122() {
		check("Integrate(x/(Sqrt(a+b*x^3)*(2*(5-3*Sqrt(3))*a+b*x^3)), x)",
				"-((2+Sqrt(3))*ArcTan((3^(1/4)*a^(1/6)*((1-Sqrt(3))*a^(1/3)-2*b^(1/3)*x))/(Sqrt(2)*Sqrt(a+b*x^3))))/(3*Sqrt(2)*3^(1/4)*a^(5/6)*b^(2/3))-((2+Sqrt(3))*ArcTan((3^(1/4)*(1+Sqrt(3))*a^(1/6)*(a^(1/3)+b^(1/3)*x))/(Sqrt(2)*Sqrt(a+b*x^3))))/(6*Sqrt(2)*3^(1/4)*a^(5/6)*b^(2/3))+((2+Sqrt(3))*ArcTanh((3^(1/4)*(1-Sqrt(3))*a^(1/6)*(a^(1/3)+b^(1/3)*x))/(Sqrt(2)*Sqrt(a+b*x^3))))/(2*Sqrt(2)*3^(3/4)*a^(5/6)*b^(2/3))+((2+Sqrt(3))*ArcTanh(((1+Sqrt(3))*Sqrt(a+b*x^3))/(Sqrt(2)*3^(3/4)*Sqrt(a))))/(3*Sqrt(2)*3^(3/4)*a^(5/6)*b^(2/3))");

	}

	// {x/(Sqrt(a-b*x^3)*(2*(5-3*Sqrt(3))*a-b*x^3)), x, 1, -((2+Sqrt(3))*ArcTan((3^(1/4)*(1 +
	// Sqrt(3))*a^(1/6)*(a^(1/3)-b^(1/3)*x))/(Sqrt(2)*Sqrt(a-b*x^3))))/(6*Sqrt(2)*3^(1/4)*a^(5/6)*b^(2/3))-((2 +
	// Sqrt(3))*ArcTan((3^(1/4)*a^(1/6)*((1-Sqrt(3))*a^(1/3)+2*b^(1/3)*x))/(Sqrt(2)*Sqrt(a -
	// b*x^3))))/(3*Sqrt(2)*3^(1/4)*a^(5/6)*b^(2/3))+((2+Sqrt(3))*ArcTanh((3^(1/4)*(1-Sqrt(3))*a^(1/6)*(a^(1/3) -
	// b^(1/3)*x))/(Sqrt(2)*Sqrt(a-b*x^3))))/(2*Sqrt(2)*3^(3/4)*a^(5/6)*b^(2/3))+((2+Sqrt(3))*ArcTanh(((1 +
	// Sqrt(3))*Sqrt(a-b*x^3))/(Sqrt(2)*3^(3/4)*Sqrt(a))))/(3*Sqrt(2)*3^(3/4)*a^(5/6)*b^(2/3))}
	public void test01123() {
		check("Integrate(x/(Sqrt(a-b*x^3)*(2*(5-3*Sqrt(3))*a-b*x^3)), x)",
				"-((2+Sqrt(3))*ArcTan((3^(1/4)*(1+Sqrt(3))*a^(1/6)*(a^(1/3)-b^(1/3)*x))/(Sqrt(2)*Sqrt(a-b*x^3))))/(6*Sqrt(2)*3^(1/4)*a^(5/6)*b^(2/3))-((2+Sqrt(3))*ArcTan((3^(1/4)*a^(1/6)*((1-Sqrt(3))*a^(1/3)+2*b^(1/3)*x))/(Sqrt(2)*Sqrt(a-b*x^3))))/(3*Sqrt(2)*3^(1/4)*a^(5/6)*b^(2/3))+((2+Sqrt(3))*ArcTanh((3^(1/4)*(1-Sqrt(3))*a^(1/6)*(a^(1/3)-b^(1/3)*x))/(Sqrt(2)*Sqrt(a-b*x^3))))/(2*Sqrt(2)*3^(3/4)*a^(5/6)*b^(2/3))+((2+Sqrt(3))*ArcTanh(((1+Sqrt(3))*Sqrt(a-b*x^3))/(Sqrt(2)*3^(3/4)*Sqrt(a))))/(3*Sqrt(2)*3^(3/4)*a^(5/6)*b^(2/3))");

	}

	// {x/((2*(5-3*Sqrt(3))*a-b*x^3)*Sqrt(-a+b*x^3)), x, 1, ((2+Sqrt(3))*ArcTan((3^(1/4)*(1 -
	// Sqrt(3))*a^(1/6)*(a^(1/3)-b^(1/3)*x))/(Sqrt(2)*Sqrt(-a+b*x^3))))/(2*Sqrt(2)*3^(3/4)*a^(5/6)*b^(2/3))-((2 +
	// Sqrt(3))*ArcTan(((1+Sqrt(3))*Sqrt(-a+b*x^3))/(Sqrt(2)*3^(3/4)*Sqrt(a))))/(3*Sqrt(2)*3^(3/4)*a^(5/6)*b^(2/3))
	// -((2+Sqrt(3))*ArcTanh((3^(1/4)*(1+Sqrt(3))*a^(1/6)*(a^(1/3)-b^(1/3)*x))/(Sqrt(2)*Sqrt(-a +
	// b*x^3))))/(6*Sqrt(2)*3^(1/4)*a^(5/6)*b^(2/3))-((2+Sqrt(3))*ArcTanh((3^(1/4)*a^(1/6)*((1-Sqrt(3))*a^(1/3) +
	// 2*b^(1/3)*x))/(Sqrt(2)*Sqrt(-a+b*x^3))))/(3*Sqrt(2)*3^(1/4)*a^(5/6)*b^(2/3))}
	public void test01124() {
		check("Integrate(x/((2*(5-3*Sqrt(3))*a-b*x^3)*Sqrt(-a+b*x^3)), x)",
				"((2+Sqrt(3))*ArcTan((3^(1/4)*(1-Sqrt(3))*a^(1/6)*(a^(1/3)-b^(1/3)*x))/(Sqrt(2)*Sqrt(-a+b*x^3))))/(2*Sqrt(2)*3^(3/4)*a^(5/6)*b^(2/3))-((2+Sqrt(3))*ArcTan(((1+Sqrt(3))*Sqrt(-a+b*x^3))/(Sqrt(2)*3^(3/4)*Sqrt(a))))/(3*Sqrt(2)*3^(3/4)*a^(5/6)*b^(2/3))-((2+Sqrt(3))*ArcTanh((3^(1/4)*(1+Sqrt(3))*a^(1/6)*(a^(1/3)-b^(1/3)*x))/(Sqrt(2)*Sqrt(-a+b*x^3))))/(6*Sqrt(2)*3^(1/4)*a^(5/6)*b^(2/3))-((2+Sqrt(3))*ArcTanh((3^(1/4)*a^(1/6)*((1-Sqrt(3))*a^(1/3)+2*b^(1/3)*x))/(Sqrt(2)*Sqrt(-a+b*x^3))))/(3*Sqrt(2)*3^(1/4)*a^(5/6)*b^(2/3))");

	}

	// {x/(Sqrt(-a-b*x^3)*(2*(5-3*Sqrt(3))*a+b*x^3)), x, 1, ((2+Sqrt(3))*ArcTan((3^(1/4)*(1 -
	// Sqrt(3))*a^(1/6)*(a^(1/3)+b^(1/3)*x))/(Sqrt(2)*Sqrt(-a-b*x^3))))/(2*Sqrt(2)*3^(3/4)*a^(5/6)*b^(2/3))-((2 +
	// Sqrt(3))*ArcTan(((1+Sqrt(3))*Sqrt(-a-b*x^3))/(Sqrt(2)*3^(3/4)*Sqrt(a))))/(3*Sqrt(2)*3^(3/4)*a^(5/6)*b^(2/3))
	// -((2+Sqrt(3))*ArcTanh((3^(1/4)*a^(1/6)*((1-Sqrt(3))*a^(1/3)-2*b^(1/3)*x))/(Sqrt(2)*Sqrt(-a -
	// b*x^3))))/(3*Sqrt(2)*3^(1/4)*a^(5/6)*b^(2/3))-((2+Sqrt(3))*ArcTanh((3^(1/4)*(1+Sqrt(3))*a^(1/6)*(a^(1/3) +
	// b^(1/3)*x))/(Sqrt(2)*Sqrt(-a-b*x^3))))/(6*Sqrt(2)*3^(1/4)*a^(5/6)*b^(2/3))}
	public void test01125() {
		check("Integrate(x/(Sqrt(-a-b*x^3)*(2*(5-3*Sqrt(3))*a+b*x^3)), x)",
				"((2+Sqrt(3))*ArcTan((3^(1/4)*(1-Sqrt(3))*a^(1/6)*(a^(1/3)+b^(1/3)*x))/(Sqrt(2)*Sqrt(-a-b*x^3))))/(2*Sqrt(2)*3^(3/4)*a^(5/6)*b^(2/3))-((2+Sqrt(3))*ArcTan(((1+Sqrt(3))*Sqrt(-a-b*x^3))/(Sqrt(2)*3^(3/4)*Sqrt(a))))/(3*Sqrt(2)*3^(3/4)*a^(5/6)*b^(2/3))-((2+Sqrt(3))*ArcTanh((3^(1/4)*a^(1/6)*((1-Sqrt(3))*a^(1/3)-2*b^(1/3)*x))/(Sqrt(2)*Sqrt(-a-b*x^3))))/(3*Sqrt(2)*3^(1/4)*a^(5/6)*b^(2/3))-((2+Sqrt(3))*ArcTanh((3^(1/4)*(1+Sqrt(3))*a^(1/6)*(a^(1/3)+b^(1/3)*x))/(Sqrt(2)*Sqrt(-a-b*x^3))))/(6*Sqrt(2)*3^(1/4)*a^(5/6)*b^(2/3))");

	}

	// {x^4/((1-x^3)^(1/3)*(1+x^3)), x, 1, (x^5*AppellF1(5/3, 1/3, 1, 8/3, x^3, -x^3))/5}
	public void test01126() {
		check("Integrate(x^4/((1-x^3)^(1/3)*(1+x^3)), x)", "(x^5*AppellF1(5/3, 1/3, 1, 8/3, x^3, -x^3))/5");

	}

	// {x/((1-x^3)^(1/3)*(1+x^3)), x, 1, (x^2*AppellF1(2/3, 1/3, 1, 5/3, x^3, -x^3))/2}
	public void test01127() {
		check("Integrate(x/((1-x^3)^(1/3)*(1+x^3)), x)", "(x^2*AppellF1(2/3, 1/3, 1, 5/3, x^3, -x^3))/2");

	}

	// {1/(x^2*(1-x^3)^(1/3)*(1+x^3)), x, 1, -(AppellF1(-1/3, 1/3, 1, 2/3, x^3, -x^3)/x)}
	public void test01128() {
		check("Integrate(1/(x^2*(1-x^3)^(1/3)*(1+x^3)), x)", "-(AppellF1(-1/3, 1/3, 1, 2/3, x^3, -x^3)/x)");

	}

	// {1/(x^5*(1-x^3)^(1/3)*(1+x^3)), x, 1, -AppellF1(-4/3, 1/3, 1, -1/3, x^3, -x^3)/(4*x^4)}
	public void test01129() {
		check("Integrate(1/(x^5*(1-x^3)^(1/3)*(1+x^3)), x)", "-AppellF1(-4/3, 1/3, 1, -1/3, x^3, -x^3)/(4*x^4)");

	}

	// {x^6/((1-x^3)^(2/3)*(1+x^3)), x, 1, (x^7*AppellF1(7/3, 2/3, 1, 10/3, x^3, -x^3))/7}
	public void test01130() {
		check("Integrate(x^6/((1-x^3)^(2/3)*(1+x^3)), x)", "(x^7*AppellF1(7/3, 2/3, 1, 10/3, x^3, -x^3))/7");

	}

	// {x^3/((1-x^3)^(2/3)*(1+x^3)), x, 1, (x^4*AppellF1(4/3, 2/3, 1, 7/3, x^3, -x^3))/4}
	public void test01131() {
		check("Integrate(x^3/((1-x^3)^(2/3)*(1+x^3)), x)", "(x^4*AppellF1(4/3, 2/3, 1, 7/3, x^3, -x^3))/4");

	}

	// {1/((1-x^3)^(2/3)*(1+x^3)), x, 1, x*AppellF1(1/3, 2/3, 1, 4/3, x^3, -x^3)}
	public void test01132() {
		check("Integrate(1/((1-x^3)^(2/3)*(1+x^3)), x)", "x*AppellF1(1/3, 2/3, 1, 4/3, x^3, -x^3)");

	}

	// {1/(x^3*(1-x^3)^(2/3)*(1+x^3)), x, 1, -AppellF1(-2/3, 2/3, 1, 1/3, x^3, -x^3)/(2*x^2)}
	public void test01133() {
		check("Integrate(1/(x^3*(1-x^3)^(2/3)*(1+x^3)), x)", "-AppellF1(-2/3, 2/3, 1, 1/3, x^3, -x^3)/(2*x^2)");

	}

	// {(Sqrt(-1+Sqrt(x))*Sqrt(1+Sqrt(x)))/x^(5/2), x, 1, (2*(-1+Sqrt(x))^(3/2)*(1+Sqrt(x))^(3/2))/(3*x^(3/2))}
	public void test01134() {
		check("Integrate((Sqrt(-1+Sqrt(x))*Sqrt(1+Sqrt(x)))/x^(5/2), x)",
				"(2*(-1+Sqrt(x))^(3/2)*(1+Sqrt(x))^(3/2))/(3*x^(3/2))");

	}

	// {1/(Sqrt(-1+Sqrt(x))*Sqrt(1+Sqrt(x))*x^(3/2)), x, 1, (2*Sqrt(-1+Sqrt(x))*Sqrt(1+Sqrt(x)))/Sqrt(x)}
	public void test01135() {
		check("Integrate(1/(Sqrt(-1+Sqrt(x))*Sqrt(1+Sqrt(x))*x^(3/2)), x)",
				"(2*Sqrt(-1+Sqrt(x))*Sqrt(1+Sqrt(x)))/Sqrt(x)");

	}

	// {(e*x)^m*(a+b*x^n)^p*(a*(1+m)+b*(1+m+n+n*p)*x^n), x, 1, ((e*x)^(1+m)*(a+b*x^n)^(1+p))/e}
	public void test01136() {
		check("Integrate((e*x)^m*(a+b*x^n)^p*(a*(1+m)+b*(1+m+n+n*p)*x^n), x)", "((e*x)^(1+m)*(a+b*x^n)^(1+p))/e");

	}

	// {x^13*(b+c*x)^13*(b+2*c*x), x, 1, (x^14*(b+c*x)^14)/14}
	public void test01137() {
		check("Integrate(x^13*(b+c*x)^13*(b+2*c*x), x)", "(x^14*(b+c*x)^14)/14");

	}

	// {x^27*(b+c*x^2)^13*(b+2*c*x^2), x, 1, (x^28*(b+c*x^2)^14)/28}
	public void test01138() {
		check("Integrate(x^27*(b+c*x^2)^13*(b+2*c*x^2), x)", "(x^28*(b+c*x^2)^14)/28");

	}

	// {x^41*(b+c*x^3)^13*(b+2*c*x^3), x, 1, (x^42*(b+c*x^3)^14)/42}
	public void test01139() {
		check("Integrate(x^41*(b+c*x^3)^13*(b+2*c*x^3), x)", "(x^42*(b+c*x^3)^14)/42");

	}

	// {x^(-1+m)*(a+b*x^n)^(-1+p)*(a*m+b*(m+n*p)*x^n), x, 1, x^m*(a+b*x^n)^p}
	public void test01140() {
		check("Integrate(x^(-1+m)*(a+b*x^n)^(-1+p)*(a*m+b*(m+n*p)*x^n), x)", "x^m*(a+b*x^n)^p");

	}

	// {(b+2*c*x)/(x^8*(b+c*x)^8), x, 1, -1/(7*x^7*(b+c*x)^7)}
	public void test01141() {
		check("Integrate((b+2*c*x)/(x^8*(b+c*x)^8), x)", "-1/(7*x^7*(b+c*x)^7)");

	}

	// {(b+2*c*x^2)/(x^15*(b+c*x^2)^8), x, 1, -1/(14*x^14*(b+c*x^2)^7)}
	public void test01142() {
		check("Integrate((b+2*c*x^2)/(x^15*(b+c*x^2)^8), x)", "-1/(14*x^14*(b+c*x^2)^7)");

	}

	// {(b+2*c*x^3)/(x^22*(b+c*x^3)^8), x, 1, -1/(21*x^21*(b+c*x^3)^7)}
	public void test01143() {
		check("Integrate((b+2*c*x^3)/(x^22*(b+c*x^3)^8), x)", "-1/(21*x^21*(b+c*x^3)^7)");

	}

	// {x^p*(b+c*x)^p*(b+2*c*x), x, 1, (x^(1+p)*(b+c*x)^(1+p))/(1+p)}
	public void test01144() {
		check("Integrate(x^p*(b+c*x)^p*(b+2*c*x), x)", "(x^(1+p)*(b+c*x)^(1+p))/(1+p)");

	}

	// {x^(-1+2*(1+p))*(b+c*x^2)^p*(b+2*c*x^2), x, 1, (x^(2*(1+p))*(b+c*x^2)^(1+p))/(2*(1+p))}
	public void test01145() {
		check("Integrate(x^(-1+2*(1+p))*(b+c*x^2)^p*(b+2*c*x^2), x)", "(x^(2*(1+p))*(b+c*x^2)^(1+p))/(2*(1+p))");

	}

	// {x^(-1+3*(1+p))*(b+c*x^3)^p*(b+2*c*x^3), x, 1, (x^(3*(1+p))*(b+c*x^3)^(1+p))/(3*(1+p))}
	public void test01146() {
		check("Integrate(x^(-1+3*(1+p))*(b+c*x^3)^p*(b+2*c*x^3), x)", "(x^(3*(1+p))*(b+c*x^3)^(1+p))/(3*(1+p))");

	}

	// {x^(-1+n*(1+p))*(b+c*x^n)^p*(b+2*c*x^n), x, 1, (x^(n*(1+p))*(b+c*x^n)^(1+p))/(n*(1+p))}
	public void test01147() {
		check("Integrate(x^(-1+n*(1+p))*(b+c*x^n)^p*(b+2*c*x^n), x)", "(x^(n*(1+p))*(b+c*x^n)^(1+p))/(n*(1+p))");

	}

	// {(1+Sqrt(3)-x)/Sqrt(-1+x^3), x, 1, (2*Sqrt(-1+x^3))/(1-Sqrt(3)-x)-(3^(1/4)*Sqrt(2+Sqrt(3))*(1 -
	// x)*Sqrt((1+x+x^2)/(1-Sqrt(3)-x)^2)*EllipticE(ArcSin((1+Sqrt(3)-x)/(1-Sqrt(3)-x)), -7 +
	// 4*Sqrt(3)))/(Sqrt(-((1-x)/(1-Sqrt(3)-x)^2))*Sqrt(-1+x^3))}
	public void test01148() {
		check("Integrate((1+Sqrt(3)-x)/Sqrt(-1+x^3), x)",
				"(2*Sqrt(-1+x^3))/(1-Sqrt(3)-x)-(3^(1/4)*Sqrt(2+Sqrt(3))*(1-x)*Sqrt((1+x+x^2)/(1-Sqrt(3)-x)^2)*EllipticE(ArcSin((1+Sqrt(3)-x)/(1-Sqrt(3)-x)), -7+4*Sqrt(3)))/(Sqrt(-((1-x)/(1-Sqrt(3)-x)^2))*Sqrt(-1+x^3))");

	}

	// {(1+Sqrt(3)+x)/Sqrt(-1-x^3), x, 1, (-2*Sqrt(-1-x^3))/(1-Sqrt(3)+x)+(3^(1/4)*Sqrt(2+Sqrt(3))*(1 +
	// x)*Sqrt((1-x+x^2)/(1-Sqrt(3)+x)^2)*EllipticE(ArcSin((1+Sqrt(3)+x)/(1-Sqrt(3)+x)), -7 +
	// 4*Sqrt(3)))/(Sqrt(-((1+x)/(1-Sqrt(3)+x)^2))*Sqrt(-1-x^3))}
	public void test01149() {
		check("Integrate((1+Sqrt(3)+x)/Sqrt(-1-x^3), x)",
				"(-2*Sqrt(-1-x^3))/(1-Sqrt(3)+x)+(3^(1/4)*Sqrt(2+Sqrt(3))*(1+x)*Sqrt((1-x+x^2)/(1-Sqrt(3)+x)^2)*EllipticE(ArcSin((1+Sqrt(3)+x)/(1-Sqrt(3)+x)), -7+4*Sqrt(3)))/(Sqrt(-((1+x)/(1-Sqrt(3)+x)^2))*Sqrt(-1-x^3))");

	}

	// {((1+Sqrt(3))*a^(1/3)-b^(1/3)*x)/Sqrt(-a+b*x^3), x, 1, (2*Sqrt(-a+b*x^3))/(b^(1/3)*((1-Sqrt(3))*a^(1/3)
	// -b^(1/3)*x))-(3^(1/4)*Sqrt(2+Sqrt(3))*a^(1/3)*(a^(1/3)-b^(1/3)*x)*Sqrt((a^(2/3)+a^(1/3)*b^(1/3)*x +
	// b^(2/3)*x^2)/((1-Sqrt(3))*a^(1/3)-b^(1/3)*x)^2)*EllipticE(ArcSin(((1+Sqrt(3))*a^(1/3)-b^(1/3)*x)/((1 -
	// Sqrt(3))*a^(1/3)-b^(1/3)*x)), -7+4*Sqrt(3)))/(b^(1/3)*Sqrt(-((a^(1/3)*(a^(1/3)-b^(1/3)*x))/((1 -
	// Sqrt(3))*a^(1/3)-b^(1/3)*x)^2))*Sqrt(-a+b*x^3))}
	public void test01150() {
		check("Integrate(((1+Sqrt(3))*a^(1/3)-b^(1/3)*x)/Sqrt(-a+b*x^3), x)",
				"(2*Sqrt(-a+b*x^3))/(b^(1/3)*((1-Sqrt(3))*a^(1/3)-b^(1/3)*x))-(3^(1/4)*Sqrt(2+Sqrt(3))*a^(1/3)*(a^(1/3)-b^(1/3)*x)*Sqrt((a^(2/3)+a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/((1-Sqrt(3))*a^(1/3)-b^(1/3)*x)^2)*EllipticE(ArcSin(((1+Sqrt(3))*a^(1/3)-b^(1/3)*x)/((1-Sqrt(3))*a^(1/3)-b^(1/3)*x)), -7+4*Sqrt(3)))/(b^(1/3)*Sqrt(-((a^(1/3)*(a^(1/3)-b^(1/3)*x))/((1-Sqrt(3))*a^(1/3)-b^(1/3)*x)^2))*Sqrt(-a+b*x^3))");

	}

	// {((1+Sqrt(3))*a^(1/3)+b^(1/3)*x)/Sqrt(-a-b*x^3), x, 1, (-2*Sqrt(-a-b*x^3))/(b^(1/3)*((1 -
	// Sqrt(3))*a^(1/3)+b^(1/3)*x))+(3^(1/4)*Sqrt(2+Sqrt(3))*a^(1/3)*(a^(1/3)+b^(1/3)*x)*Sqrt((a^(2/3) -
	// a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/((1-Sqrt(3))*a^(1/3)+b^(1/3)*x)^2)*EllipticE(ArcSin(((1+Sqrt(3))*a^(1/3) +
	// b^(1/3)*x)/((1-Sqrt(3))*a^(1/3)+b^(1/3)*x)), -7+4*Sqrt(3)))/(b^(1/3)*Sqrt(-((a^(1/3)*(a^(1/3) +
	// b^(1/3)*x))/((1-Sqrt(3))*a^(1/3)+b^(1/3)*x)^2))*Sqrt(-a-b*x^3))}
	public void test01151() {
		check("Integrate(((1+Sqrt(3))*a^(1/3)+b^(1/3)*x)/Sqrt(-a-b*x^3), x)",
				"(-2*Sqrt(-a-b*x^3))/(b^(1/3)*((1-Sqrt(3))*a^(1/3)+b^(1/3)*x))+(3^(1/4)*Sqrt(2+Sqrt(3))*a^(1/3)*(a^(1/3)+b^(1/3)*x)*Sqrt((a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/((1-Sqrt(3))*a^(1/3)+b^(1/3)*x)^2)*EllipticE(ArcSin(((1+Sqrt(3))*a^(1/3)+b^(1/3)*x)/((1-Sqrt(3))*a^(1/3)+b^(1/3)*x)), -7+4*Sqrt(3)))/(b^(1/3)*Sqrt(-((a^(1/3)*(a^(1/3)+b^(1/3)*x))/((1-Sqrt(3))*a^(1/3)+b^(1/3)*x)^2))*Sqrt(-a-b*x^3))");

	}

	// {(1+Sqrt(3)-(b/a)^(1/3)*x)/Sqrt(-a+b*x^3), x, 1, (2*(b/a)^(2/3)*Sqrt(-a+b*x^3))/(b*(1-Sqrt(3) -
	// (b/a)^(1/3)*x))-(3^(1/4)*Sqrt(2+Sqrt(3))*(1-(b/a)^(1/3)*x)*Sqrt((1+(b/a)^(1/3)*x+(b/a)^(2/3)*x^2)/(1 -
	// Sqrt(3)-(b/a)^(1/3)*x)^2)*EllipticE(ArcSin((1+Sqrt(3)-(b/a)^(1/3)*x)/(1-Sqrt(3)-(b/a)^(1/3)*x)), -7 +
	// 4*Sqrt(3)))/((b/a)^(1/3)*Sqrt(-((1-(b/a)^(1/3)*x)/(1-Sqrt(3)-(b/a)^(1/3)*x)^2))*Sqrt(-a+b*x^3))}
	public void test01152() {
		check("Integrate((1+Sqrt(3)-(b/a)^(1/3)*x)/Sqrt(-a+b*x^3), x)",
				"(2*(b/a)^(2/3)*Sqrt(-a+b*x^3))/(b*(1-Sqrt(3)-(b/a)^(1/3)*x))-(3^(1/4)*Sqrt(2+Sqrt(3))*(1-(b/a)^(1/3)*x)*Sqrt((1+(b/a)^(1/3)*x+(b/a)^(2/3)*x^2)/(1-Sqrt(3)-(b/a)^(1/3)*x)^2)*EllipticE(ArcSin((1+Sqrt(3)-(b/a)^(1/3)*x)/(1-Sqrt(3)-(b/a)^(1/3)*x)), -7+4*Sqrt(3)))/((b/a)^(1/3)*Sqrt(-((1-(b/a)^(1/3)*x)/(1-Sqrt(3)-(b/a)^(1/3)*x)^2))*Sqrt(-a+b*x^3))");

	}

	// {(1+Sqrt(3)+(b/a)^(1/3)*x)/Sqrt(-a-b*x^3), x, 1, (-2*(b/a)^(2/3)*Sqrt(-a-b*x^3))/(b*(1-Sqrt(3) +
	// (b/a)^(1/3)*x))+(3^(1/4)*Sqrt(2+Sqrt(3))*(1+(b/a)^(1/3)*x)*Sqrt((1-(b/a)^(1/3)*x+(b/a)^(2/3)*x^2)/(1 -
	// Sqrt(3)+(b/a)^(1/3)*x)^2)*EllipticE(ArcSin((1+Sqrt(3)+(b/a)^(1/3)*x)/(1-Sqrt(3)+(b/a)^(1/3)*x)), -7 +
	// 4*Sqrt(3)))/((b/a)^(1/3)*Sqrt(-((1+(b/a)^(1/3)*x)/(1-Sqrt(3)+(b/a)^(1/3)*x)^2))*Sqrt(-a-b*x^3))}
	public void test01153() {
		check("Integrate((1+Sqrt(3)+(b/a)^(1/3)*x)/Sqrt(-a-b*x^3), x)",
				"(-2*(b/a)^(2/3)*Sqrt(-a-b*x^3))/(b*(1-Sqrt(3)+(b/a)^(1/3)*x))+(3^(1/4)*Sqrt(2+Sqrt(3))*(1+(b/a)^(1/3)*x)*Sqrt((1-(b/a)^(1/3)*x+(b/a)^(2/3)*x^2)/(1-Sqrt(3)+(b/a)^(1/3)*x)^2)*EllipticE(ArcSin((1+Sqrt(3)+(b/a)^(1/3)*x)/(1-Sqrt(3)+(b/a)^(1/3)*x)), -7+4*Sqrt(3)))/((b/a)^(1/3)*Sqrt(-((1+(b/a)^(1/3)*x)/(1-Sqrt(3)+(b/a)^(1/3)*x)^2))*Sqrt(-a-b*x^3))");

	}

	// {(1-Sqrt(3)+x)/Sqrt(1+x^3), x, 1, (2*Sqrt(1+x^3))/(1+Sqrt(3)+x)-(3^(1/4)*Sqrt(2-Sqrt(3))*(1 +
	// x)*Sqrt((1-x+x^2)/(1+Sqrt(3)+x)^2)*EllipticE(ArcSin((1-Sqrt(3)+x)/(1+Sqrt(3)+x)), -7 -
	// 4*Sqrt(3)))/(Sqrt((1+x)/(1+Sqrt(3)+x)^2)*Sqrt(1+x^3))}
	public void test01154() {
		check("Integrate((1-Sqrt(3)+x)/Sqrt(1+x^3), x)",
				"(2*Sqrt(1+x^3))/(1+Sqrt(3)+x)-(3^(1/4)*Sqrt(2-Sqrt(3))*(1+x)*Sqrt((1-x+x^2)/(1+Sqrt(3)+x)^2)*EllipticE(ArcSin((1-Sqrt(3)+x)/(1+Sqrt(3)+x)), -7-4*Sqrt(3)))/(Sqrt((1+x)/(1+Sqrt(3)+x)^2)*Sqrt(1+x^3))");

	}

	// {(1-Sqrt(3)-x)/Sqrt(1-x^3), x, 1, (-2*Sqrt(1-x^3))/(1+Sqrt(3)-x)+(3^(1/4)*Sqrt(2-Sqrt(3))*(1 -
	// x)*Sqrt((1+x+x^2)/(1+Sqrt(3)-x)^2)*EllipticE(ArcSin((1-Sqrt(3)-x)/(1+Sqrt(3)-x)), -7 -
	// 4*Sqrt(3)))/(Sqrt((1-x)/(1+Sqrt(3)-x)^2)*Sqrt(1-x^3))}
	public void test01155() {
		check("Integrate((1-Sqrt(3)-x)/Sqrt(1-x^3), x)",
				"(-2*Sqrt(1-x^3))/(1+Sqrt(3)-x)+(3^(1/4)*Sqrt(2-Sqrt(3))*(1-x)*Sqrt((1+x+x^2)/(1+Sqrt(3)-x)^2)*EllipticE(ArcSin((1-Sqrt(3)-x)/(1+Sqrt(3)-x)), -7-4*Sqrt(3)))/(Sqrt((1-x)/(1+Sqrt(3)-x)^2)*Sqrt(1-x^3))");

	}

	// {(-1+Sqrt(3)-x)/Sqrt(1+x^3), x, 1, (-2*Sqrt(1+x^3))/(1+Sqrt(3)+x)+(3^(1/4)*Sqrt(2-Sqrt(3))*(1 +
	// x)*Sqrt((1-x+x^2)/(1+Sqrt(3)+x)^2)*EllipticE(ArcSin((1-Sqrt(3)+x)/(1+Sqrt(3)+x)), -7 -
	// 4*Sqrt(3)))/(Sqrt((1+x)/(1+Sqrt(3)+x)^2)*Sqrt(1+x^3))}
	public void test01156() {
		check("Integrate((-1+Sqrt(3)-x)/Sqrt(1+x^3), x)",
				"(-2*Sqrt(1+x^3))/(1+Sqrt(3)+x)+(3^(1/4)*Sqrt(2-Sqrt(3))*(1+x)*Sqrt((1-x+x^2)/(1+Sqrt(3)+x)^2)*EllipticE(ArcSin((1-Sqrt(3)+x)/(1+Sqrt(3)+x)), -7-4*Sqrt(3)))/(Sqrt((1+x)/(1+Sqrt(3)+x)^2)*Sqrt(1+x^3))");

	}

	// {(-1+Sqrt(3)+x)/Sqrt(1-x^3), x, 1, (2*Sqrt(1-x^3))/(1+Sqrt(3)-x)-(3^(1/4)*Sqrt(2-Sqrt(3))*(1 -
	// x)*Sqrt((1+x+x^2)/(1+Sqrt(3)-x)^2)*EllipticE(ArcSin((1-Sqrt(3)-x)/(1+Sqrt(3)-x)), -7 -
	// 4*Sqrt(3)))/(Sqrt((1-x)/(1+Sqrt(3)-x)^2)*Sqrt(1-x^3))}
	public void test01157() {
		check("Integrate((-1+Sqrt(3)+x)/Sqrt(1-x^3), x)",
				"(2*Sqrt(1-x^3))/(1+Sqrt(3)-x)-(3^(1/4)*Sqrt(2-Sqrt(3))*(1-x)*Sqrt((1+x+x^2)/(1+Sqrt(3)-x)^2)*EllipticE(ArcSin((1-Sqrt(3)-x)/(1+Sqrt(3)-x)), -7-4*Sqrt(3)))/(Sqrt((1-x)/(1+Sqrt(3)-x)^2)*Sqrt(1-x^3))");

	}

	// {((1-Sqrt(3))*a^(1/3)+b^(1/3)*x)/Sqrt(a+b*x^3), x, 1, (2*Sqrt(a+b*x^3))/(b^(1/3)*((1+Sqrt(3))*a^(1/3) +
	// b^(1/3)*x))-(3^(1/4)*Sqrt(2-Sqrt(3))*a^(1/3)*(a^(1/3)+b^(1/3)*x)*Sqrt((a^(2/3)-a^(1/3)*b^(1/3)*x +
	// b^(2/3)*x^2)/((1+Sqrt(3))*a^(1/3)+b^(1/3)*x)^2)*EllipticE(ArcSin(((1-Sqrt(3))*a^(1/3)+b^(1/3)*x)/((1 +
	// Sqrt(3))*a^(1/3)+b^(1/3)*x)), -7-4*Sqrt(3)))/(b^(1/3)*Sqrt((a^(1/3)*(a^(1/3)+b^(1/3)*x))/((1 +
	// Sqrt(3))*a^(1/3)+b^(1/3)*x)^2)*Sqrt(a+b*x^3))}
	public void test01158() {
		check("Integrate(((1-Sqrt(3))*a^(1/3)+b^(1/3)*x)/Sqrt(a+b*x^3), x)",
				"(2*Sqrt(a+b*x^3))/(b^(1/3)*((1+Sqrt(3))*a^(1/3)+b^(1/3)*x))-(3^(1/4)*Sqrt(2-Sqrt(3))*a^(1/3)*(a^(1/3)+b^(1/3)*x)*Sqrt((a^(2/3)-a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/((1+Sqrt(3))*a^(1/3)+b^(1/3)*x)^2)*EllipticE(ArcSin(((1-Sqrt(3))*a^(1/3)+b^(1/3)*x)/((1+Sqrt(3))*a^(1/3)+b^(1/3)*x)), -7-4*Sqrt(3)))/(b^(1/3)*Sqrt((a^(1/3)*(a^(1/3)+b^(1/3)*x))/((1+Sqrt(3))*a^(1/3)+b^(1/3)*x)^2)*Sqrt(a+b*x^3))");

	}

	// {((1-Sqrt(3))*a^(1/3)-b^(1/3)*x)/Sqrt(a-b*x^3), x, 1, (-2*Sqrt(a-b*x^3))/(b^(1/3)*((1+Sqrt(3))*a^(1/3)
	// -b^(1/3)*x))+(3^(1/4)*Sqrt(2-Sqrt(3))*a^(1/3)*(a^(1/3)-b^(1/3)*x)*Sqrt((a^(2/3)+a^(1/3)*b^(1/3)*x +
	// b^(2/3)*x^2)/((1+Sqrt(3))*a^(1/3)-b^(1/3)*x)^2)*EllipticE(ArcSin(((1-Sqrt(3))*a^(1/3)-b^(1/3)*x)/((1 +
	// Sqrt(3))*a^(1/3)-b^(1/3)*x)), -7-4*Sqrt(3)))/(b^(1/3)*Sqrt((a^(1/3)*(a^(1/3)-b^(1/3)*x))/((1 +
	// Sqrt(3))*a^(1/3)-b^(1/3)*x)^2)*Sqrt(a-b*x^3))}
	public void test01159() {
		check("Integrate(((1-Sqrt(3))*a^(1/3)-b^(1/3)*x)/Sqrt(a-b*x^3), x)",
				"(-2*Sqrt(a-b*x^3))/(b^(1/3)*((1+Sqrt(3))*a^(1/3)-b^(1/3)*x))+(3^(1/4)*Sqrt(2-Sqrt(3))*a^(1/3)*(a^(1/3)-b^(1/3)*x)*Sqrt((a^(2/3)+a^(1/3)*b^(1/3)*x+b^(2/3)*x^2)/((1+Sqrt(3))*a^(1/3)-b^(1/3)*x)^2)*EllipticE(ArcSin(((1-Sqrt(3))*a^(1/3)-b^(1/3)*x)/((1+Sqrt(3))*a^(1/3)-b^(1/3)*x)), -7-4*Sqrt(3)))/(b^(1/3)*Sqrt((a^(1/3)*(a^(1/3)-b^(1/3)*x))/((1+Sqrt(3))*a^(1/3)-b^(1/3)*x)^2)*Sqrt(a-b*x^3))");

	}

	// {(1-Sqrt(3)+(b/a)^(1/3)*x)/Sqrt(a+b*x^3), x, 1, (2*(b/a)^(2/3)*Sqrt(a+b*x^3))/(b*(1+Sqrt(3) +
	// (b/a)^(1/3)*x))-(3^(1/4)*Sqrt(2-Sqrt(3))*(1+(b/a)^(1/3)*x)*Sqrt((1-(b/a)^(1/3)*x+(b/a)^(2/3)*x^2)/(1 +
	// Sqrt(3)+(b/a)^(1/3)*x)^2)*EllipticE(ArcSin((1-Sqrt(3)+(b/a)^(1/3)*x)/(1+Sqrt(3)+(b/a)^(1/3)*x)), -7 -
	// 4*Sqrt(3)))/((b/a)^(1/3)*Sqrt((1+(b/a)^(1/3)*x)/(1+Sqrt(3)+(b/a)^(1/3)*x)^2)*Sqrt(a+b*x^3))}
	public void test01160() {
		check("Integrate((1-Sqrt(3)+(b/a)^(1/3)*x)/Sqrt(a+b*x^3), x)",
				"(2*(b/a)^(2/3)*Sqrt(a+b*x^3))/(b*(1+Sqrt(3)+(b/a)^(1/3)*x))-(3^(1/4)*Sqrt(2-Sqrt(3))*(1+(b/a)^(1/3)*x)*Sqrt((1-(b/a)^(1/3)*x+(b/a)^(2/3)*x^2)/(1+Sqrt(3)+(b/a)^(1/3)*x)^2)*EllipticE(ArcSin((1-Sqrt(3)+(b/a)^(1/3)*x)/(1+Sqrt(3)+(b/a)^(1/3)*x)), -7-4*Sqrt(3)))/((b/a)^(1/3)*Sqrt((1+(b/a)^(1/3)*x)/(1+Sqrt(3)+(b/a)^(1/3)*x)^2)*Sqrt(a+b*x^3))");

	}

	// {(1-Sqrt(3)-(b/a)^(1/3)*x)/Sqrt(a-b*x^3), x, 1, (-2*(b/a)^(2/3)*Sqrt(a-b*x^3))/(b*(1+Sqrt(3) -
	// (b/a)^(1/3)*x))+(3^(1/4)*Sqrt(2-Sqrt(3))*(1-(b/a)^(1/3)*x)*Sqrt((1+(b/a)^(1/3)*x+(b/a)^(2/3)*x^2)/(1 +
	// Sqrt(3)-(b/a)^(1/3)*x)^2)*EllipticE(ArcSin((1-Sqrt(3)-(b/a)^(1/3)*x)/(1+Sqrt(3)-(b/a)^(1/3)*x)), -7 -
	// 4*Sqrt(3)))/((b/a)^(1/3)*Sqrt((1-(b/a)^(1/3)*x)/(1+Sqrt(3)-(b/a)^(1/3)*x)^2)*Sqrt(a-b*x^3))}
	public void test01161() {
		check("Integrate((1-Sqrt(3)-(b/a)^(1/3)*x)/Sqrt(a-b*x^3), x)",
				"(-2*(b/a)^(2/3)*Sqrt(a-b*x^3))/(b*(1+Sqrt(3)-(b/a)^(1/3)*x))+(3^(1/4)*Sqrt(2-Sqrt(3))*(1-(b/a)^(1/3)*x)*Sqrt((1+(b/a)^(1/3)*x+(b/a)^(2/3)*x^2)/(1+Sqrt(3)-(b/a)^(1/3)*x)^2)*EllipticE(ArcSin((1-Sqrt(3)-(b/a)^(1/3)*x)/(1+Sqrt(3)-(b/a)^(1/3)*x)), -7-4*Sqrt(3)))/((b/a)^(1/3)*Sqrt((1-(b/a)^(1/3)*x)/(1+Sqrt(3)-(b/a)^(1/3)*x)^2)*Sqrt(a-b*x^3))");

	}

	// {(a*g-b*g*x^4)/(a+b*x^4)^(3/2), x, 1, (g*x)/Sqrt(a+b*x^4)}
	public void test01162() {
		check("Integrate((a*g-b*g*x^4)/(a+b*x^4)^(3/2), x)", "(g*x)/Sqrt(a+b*x^4)");

	}

	// {(a*g+e*x-b*g*x^4)/(a+b*x^4)^(3/2), x, 1, (2*a*g*x+e*x^2)/(2*a*Sqrt(a+b*x^4))}
	public void test01163() {
		check("Integrate((a*g+e*x-b*g*x^4)/(a+b*x^4)^(3/2), x)", "(2*a*g*x+e*x^2)/(2*a*Sqrt(a+b*x^4))");

	}

	// {(a*g+f*x^3-b*g*x^4)/(a+b*x^4)^(3/2), x, 1, -(f-2*b*g*x)/(2*b*Sqrt(a+b*x^4))}
	public void test01164() {
		check("Integrate((a*g+f*x^3-b*g*x^4)/(a+b*x^4)^(3/2), x)", "-(f-2*b*g*x)/(2*b*Sqrt(a+b*x^4))");

	}

	// {(a*g+e*x+f*x^3-b*g*x^4)/(a+b*x^4)^(3/2), x, 1, -(a*f-2*a*b*g*x-b*e*x^2)/(2*a*b*Sqrt(a+b*x^4))}
	public void test01165() {
		check("Integrate((a*g+e*x+f*x^3-b*g*x^4)/(a+b*x^4)^(3/2), x)",
				"-(a*f-2*a*b*g*x-b*e*x^2)/(2*a*b*Sqrt(a+b*x^4))");

	}

	// {(-1+x^4)/(1+x^4)^(3/2), x, 1, -(x/Sqrt(1+x^4))}
	public void test01166() {
		check("Integrate((-1+x^4)/(1+x^4)^(3/2), x)", "-(x/Sqrt(1+x^4))");

	}

	// {c+d*x^(-1+n), x, 1, c*x+(d*x^n)/n}
	public void test01167() {
		check("Integrate(c+d*x^(-1+n), x)", "c*x+(d*x^n)/n");

	}

	// {(a*c+2*(b*c+a*d)*x^2+3*b*d*x^4)/(Sqrt(a+b*x^2)*Sqrt(c+d*x^2)), x, 1, x*Sqrt(a+b*x^2)*Sqrt(c +
	// d*x^2)}
	public void test01168() {
		check("Integrate((a*c+2*(b*c+a*d)*x^2+3*b*d*x^4)/(Sqrt(a+b*x^2)*Sqrt(c+d*x^2)), x)",
				"x*Sqrt(a+b*x^2)*Sqrt(c+d*x^2)");

	}

	// {(a+b*x^n)^((-1-n)/n)*(c+d*x^n)^((-1-n)/n)*(a*c-b*d*x^(2*n)), x, 1, x/((a+b*x^n)^n^(-1)*(c +
	// d*x^n)^n^(-1))}
	public void test01169() {
		check("Integrate((a+b*x^n)^((-1-n)/n)*(c+d*x^n)^((-1-n)/n)*(a*c-b*d*x^(2*n)), x)",
				"x/((a+b*x^n)^n^(-1)*(c+d*x^n)^n^(-1))");

	}

	// {(h*x)^(-1-n-n*p)*(a+b*x^n)^p*(c+d*x^n)^p*(a*c-b*d*x^(2*n)), x, 1, -(((a+b*x^n)^(1+p)*(c +
	// d*x^n)^(1+p))/(h*n*(1+p)*(h*x)^(n*(1+p))))}
	public void test01170() {
		check("Integrate((h*x)^(-1-n-n*p)*(a+b*x^n)^p*(c+d*x^n)^p*(a*c-b*d*x^(2*n)), x)",
				"-(((a+b*x^n)^(1+p)*(c+d*x^n)^(1+p))/(h*n*(1+p)*(h*x)^(n*(1+p))))");

	}

	// {(a+b*x^n)^p*(c+d*x^n)^p*(e+((b*c+a*d)*e*(1+n+n*p)*x^n)/(a*c)+(b*d*e*(1+2*n +
	// 2*n*p)*x^(2*n))/(a*c)), x, 1, (e*x*(a+b*x^n)^(1+p)*(c+d*x^n)^(1+p))/(a*c)}
	public void test01171() {
		check("Integrate((a+b*x^n)^p*(c+d*x^n)^p*(e+((b*c+a*d)*e*(1+n+n*p)*x^n)/(a*c)+(b*d*e*(1+2*n+2*n*p)*x^(2*n))/(a*c)), x)",
				"(e*x*(a+b*x^n)^(1+p)*(c+d*x^n)^(1+p))/(a*c)");

	}

	// {(h*x)^m*(a+b*x^n)^p*(c+d*x^n)^p*(e+((b*c+a*d)*e*(1+m+n+n*p)*x^n)/(a*c*(1+m))+(b*d*e*(1+m +
	// 2*n+2*n*p)*x^(2*n))/(a*c*(1+m))), x, 1, (e*(h*x)^(1+m)*(a+b*x^n)^(1+p)*(c+d*x^n)^(1+p))/(a*c*h*(1 +
	// m))}
	public void test01172() {
		check("Integrate((h*x)^m*(a+b*x^n)^p*(c+d*x^n)^p*(e+((b*c+a*d)*e*(1+m+n+n*p)*x^n)/(a*c*(1+m))+(b*d*e*(1+m+2*n+2*n*p)*x^(2*n))/(a*c*(1+m))), x)",
				"(e*(h*x)^(1+m)*(a+b*x^n)^(1+p)*(c+d*x^n)^(1+p))/(a*c*h*(1+m))");

	}

	// {a*x+b*x^3, x, 1, (a*x^2)/2+(b*x^4)/4}
	public void test01173() {
		check("Integrate(a*x+b*x^3, x)", "(a*x^2)/2+(b*x^4)/4");

	}

	// {x^(21/2)/(a*x+b*x^3)^(9/2), x, 1, x^(21/2)/(7*a*(a*x+b*x^3)^(7/2))}
	public void test01174() {
		check("Integrate(x^(21/2)/(a*x+b*x^3)^(9/2), x)", "x^(21/2)/(7*a*(a*x+b*x^3)^(7/2))");

	}

	// {x^(11/2)/(a*x+b*x^3)^(9/2), x, 1, -x^(7/2)/(7*b*(a*x+b*x^3)^(7/2))}
	public void test01175() {
		check("Integrate(x^(11/2)/(a*x+b*x^3)^(9/2), x)", "-x^(7/2)/(7*b*(a*x+b*x^3)^(7/2))");

	}

	// {1/(x^2*Sqrt(a*x+b*x^4)), x, 1, (-2*Sqrt(a*x+b*x^4))/(3*a*x^2)}
	public void test01176() {
		check("Integrate(1/(x^2*Sqrt(a*x+b*x^4)), x)", "(-2*Sqrt(a*x+b*x^4))/(3*a*x^2)");

	}

	// {1/(x*Sqrt(b*Sqrt(x)+a*x)), x, 1, (-4*Sqrt(b*Sqrt(x)+a*x))/(b*Sqrt(x))}
	public void test01177() {
		check("Integrate(1/(x*Sqrt(b*Sqrt(x)+a*x)), x)", "(-4*Sqrt(b*Sqrt(x)+a*x))/(b*Sqrt(x))");

	}

	// {(b*Sqrt(x)+a*x)^(-3/2), x, 1, (4*Sqrt(x))/(b*Sqrt(b*Sqrt(x)+a*x))}
	public void test01178() {
		check("Integrate((b*Sqrt(x)+a*x)^(-3/2), x)", "(4*Sqrt(x))/(b*Sqrt(b*Sqrt(x)+a*x))");

	}

	// {Sqrt(b*x^(2/3)+a*x)/x, x, 1, (2*(b*x^(2/3)+a*x)^(3/2))/(a*x)}
	public void test01179() {
		check("Integrate(Sqrt(b*x^(2/3)+a*x)/x, x)", "(2*(b*x^(2/3)+a*x)^(3/2))/(a*x)");

	}

	// {a*x^2+b*x^3, x, 1, (a*x^3)/3+(b*x^4)/4}
	public void test01180() {
		check("Integrate(a*x^2+b*x^3, x)", "(a*x^3)/3+(b*x^4)/4");

	}

	// {x^4/(a*x^2+b*x^3)^2, x, 1, -(x^2/(b*(a*x^2+b*x^3)))}
	public void test01181() {
		check("Integrate(x^4/(a*x^2+b*x^3)^2, x)", "-(x^2/(b*(a*x^2+b*x^3)))");

	}

	// {Sqrt(a*x^2+b*x^3)/x, x, 1, (2*(a*x^2+b*x^3)^(3/2))/(3*b*x^3)}
	public void test01182() {
		check("Integrate(Sqrt(a*x^2+b*x^3)/x, x)", "(2*(a*x^2+b*x^3)^(3/2))/(3*b*x^3)");

	}

	// {(a*x^2+b*x^3)^(3/2)/x^3, x, 1, (2*(a*x^2+b*x^3)^(5/2))/(5*b*x^5)}
	public void test01183() {
		check("Integrate((a*x^2+b*x^3)^(3/2)/x^3, x)", "(2*(a*x^2+b*x^3)^(5/2))/(5*b*x^5)");

	}

	// {x/Sqrt(a*x^2+b*x^3), x, 1, (2*Sqrt(a*x^2+b*x^3))/(b*x)}
	public void test01184() {
		check("Integrate(x/Sqrt(a*x^2+b*x^3), x)", "(2*Sqrt(a*x^2+b*x^3))/(b*x)");

	}

	// {x^3/(a*x^2+b*x^3)^(3/2), x, 1, (-2*x)/(b*Sqrt(a*x^2+b*x^3))}
	public void test01185() {
		check("Integrate(x^3/(a*x^2+b*x^3)^(3/2), x)", "(-2*x)/(b*Sqrt(a*x^2+b*x^3))");

	}

	// {1/(Sqrt(x)*Sqrt(a*x^2+b*x^3)), x, 1, (-2*Sqrt(a*x^2+b*x^3))/(a*x^(3/2))}
	public void test01186() {
		check("Integrate(1/(Sqrt(x)*Sqrt(a*x^2+b*x^3)), x)", "(-2*Sqrt(a*x^2+b*x^3))/(a*x^(3/2))");

	}

	// {x^(-2-3*n)*(a*x^2+b*x^3)^n, x, 1, -((a*x^2+b*x^3)^(1+n)/(a*(1+n)*x^(3*(1+n))))}
	public void test01187() {
		check("Integrate(x^(-2-3*n)*(a*x^2+b*x^3)^n, x)", "-((a*x^2+b*x^3)^(1+n)/(a*(1+n)*x^(3*(1+n))))");

	}

	// {x^3/Sqrt(a*x^2+b*x^5), x, 1, (2*Sqrt(a*x^2+b*x^5))/(3*b*x)}
	public void test01188() {
		check("Integrate(x^3/Sqrt(a*x^2+b*x^5), x)", "(2*Sqrt(a*x^2+b*x^5))/(3*b*x)");

	}

	// {1/(x^(3/2)*Sqrt(a*x^2+b*x^5)), x, 1, (-2*Sqrt(a*x^2+b*x^5))/(3*a*x^(5/2))}
	public void test01189() {
		check("Integrate(1/(x^(3/2)*Sqrt(a*x^2+b*x^5)), x)", "(-2*Sqrt(a*x^2+b*x^5))/(3*a*x^(5/2))");

	}

	// {1/Sqrt(a*x^3+b*x^4), x, 1, (-2*Sqrt(a*x^3+b*x^4))/(a*x^2)}
	public void test01190() {
		check("Integrate(1/Sqrt(a*x^3+b*x^4), x)", "(-2*Sqrt(a*x^3+b*x^4))/(a*x^2)");

	}

	// {x^12*(a*x+b*x^26)^12, x, 1, (a*x+b*x^26)^13/(325*b*x^13)}
	public void test01191() {
		check("Integrate(x^12*(a*x+b*x^26)^12, x)", "(a*x+b*x^26)^13/(325*b*x^13)");

	}

	// {x^24*(a*x+b*x^38)^12, x, 1, (a*x+b*x^38)^13/(481*b*x^13)}
	public void test01192() {
		check("Integrate(x^24*(a*x+b*x^38)^12, x)", "(a*x+b*x^38)^13/(481*b*x^13)");

	}

	// {x^12*(a+b*x^13)^12, x, 1, (a+b*x^13)^13/(169*b)}
	public void test01193() {
		check("Integrate(x^12*(a+b*x^13)^12, x)", "(a+b*x^13)^13/(169*b)");

	}

	// {x^12*(a*x+b*x^26)^12, x, 1, (a*x+b*x^26)^13/(325*b*x^13)}
	public void test01194() {
		check("Integrate(x^12*(a*x+b*x^26)^12, x)", "(a*x+b*x^26)^13/(325*b*x^13)");

	}

	// {x^12*(a*x^2+b*x^39)^12, x, 1, (a*x^2+b*x^39)^13/(481*b*x^26)}
	public void test01195() {
		check("Integrate(x^12*(a*x^2+b*x^39)^12, x)", "(a*x^2+b*x^39)^13/(481*b*x^26)");

	}

	// {x^24*(a+b*x^25)^12, x, 1, (a+b*x^25)^13/(325*b)}
	public void test01196() {
		check("Integrate(x^24*(a+b*x^25)^12, x)", "(a+b*x^25)^13/(325*b)");

	}

	// {x^24*(a*x+b*x^38)^12, x, 1, (a*x+b*x^38)^13/(481*b*x^13)}
	public void test01197() {
		check("Integrate(x^24*(a*x+b*x^38)^12, x)", "(a*x+b*x^38)^13/(481*b*x^13)");

	}

	// {x^36*(a+b*x^37)^12, x, 1, (a+b*x^37)^13/(481*b)}
	public void test01198() {
		check("Integrate(x^36*(a+b*x^37)^12, x)", "(a+b*x^37)^13/(481*b)");

	}

	// {Sqrt(x+x^(5/2)), x, 1, (4*(x+x^(5/2))^(3/2))/(9*x^(3/2))}
	public void test01199() {
		check("Integrate(Sqrt(x+x^(5/2)), x)", "(4*(x+x^(5/2))^(3/2))/(9*x^(3/2))");

	}

	// {x*Sqrt(x^2*(a+b*x^3)), x, 1, (2*(x^2*(a+b*x^3))^(3/2))/(9*b*x^3)}
	public void test01200() {
		check("Integrate(x*Sqrt(x^2*(a+b*x^3)), x)", "(2*(x^2*(a+b*x^3))^(3/2))/(9*b*x^3)");

	}

	// {x*Sqrt(a*x^2+b*x^5), x, 1, (2*(a*x^2+b*x^5)^(3/2))/(9*b*x^3)}
	public void test01201() {
		check("Integrate(x*Sqrt(a*x^2+b*x^5), x)", "(2*(a*x^2+b*x^5)^(3/2))/(9*b*x^3)");

	}

	// {(a*x^m+b*x^(1+m+m*p))^p, x, 1, (a*x^m+b*x^(1+m+m*p))^(1+p)/(b*(1+p)*(1+m*p)*x^(m*(1+p)))}
	public void test01202() {
		check("Integrate((a*x^m+b*x^(1+m+m*p))^p, x)", "(a*x^m+b*x^(1+m+m*p))^(1+p)/(b*(1+p)*(1+m*p)*x^(m*(1+p)))");

	}

	// {x^n*(a*x^m+b*x^(1+m+n+m*p))^p, x, 1, (a*x^m+b*x^(1+m+n+m*p))^(1+p)/(b*(1+p)*(1+n +
	// m*p)*x^(m*(1+p)))}
	public void test01203() {
		check("Integrate(x^n*(a*x^m+b*x^(1+m+n+m*p))^p, x)",
				"(a*x^m+b*x^(1+m+n+m*p))^(1+p)/(b*(1+p)*(1+n+m*p)*x^(m*(1+p)))");

	}

	// {x^(-1+n-p*(1+q))*(a*x^n+b*x^p)^q, x, 1, (a*x^n+b*x^p)^(1+q)/(a*(n-p)*(1+q)*x^(p*(1+q)))}
	public void test01204() {
		check("Integrate(x^(-1+n-p*(1+q))*(a*x^n+b*x^p)^q, x)", "(a*x^n+b*x^p)^(1+q)/(a*(n-p)*(1+q)*x^(p*(1+q)))");

	}

	// {((3*I)*x+4*x^2)^(-3/2), x, 1, (2*(3*I+8*x))/(9*Sqrt((3*I)*x+4*x^2))}
	public void test01205() {
		check("Integrate(((3*I)*x+4*x^2)^(-3/2), x)", "(2*(3*I+8*x))/(9*Sqrt((3*I)*x+4*x^2))");

	}

	// {(3*x-4*x^2)^(-3/2), x, 1, (-2*(3-8*x))/(9*Sqrt(3*x-4*x^2))}
	public void test01206() {
		check("Integrate((3*x-4*x^2)^(-3/2), x)", "(-2*(3-8*x))/(9*Sqrt(3*x-4*x^2))");

	}

	// {(b*x+c*x^2)^p, x, 1, -(((-((c*x)/b))^(-1-p)*(b*x+c*x^2)^(1+p)*Hypergeometric2F1(-p, 1+p, 2+p, (b +
	// c*x)/b))/(b*(1+p)))}
	public void test01207() {
		check("Integrate((b*x+c*x^2)^p, x)",
				"-(((-((c*x)/b))^(-1-p)*(b*x+c*x^2)^(1+p)*Hypergeometric2F1(-p, 1+p, 2+p, (b+c*x)/b))/(b*(1+p)))");

	}

	// {a+c*x^2, x, 1, a*x+(c*x^3)/3}
	public void test01208() {
		check("Integrate(a+c*x^2, x)", "a*x+(c*x^3)/3");

	}

	// {(a+c*x^2)^(-1), x, 1, ArcTan((Sqrt(c)*x)/Sqrt(a))/(Sqrt(a)*Sqrt(c))}
	public void test01209() {
		check("Integrate((a+c*x^2)^(-1), x)", "ArcTan((Sqrt(c)*x)/Sqrt(a))/(Sqrt(a)*Sqrt(c))");

	}

	// {(a+c*x^2)^(-3/2), x, 1, x/(a*Sqrt(a+c*x^2))}
	public void test01210() {
		check("Integrate((a+c*x^2)^(-3/2), x)", "x/(a*Sqrt(a+c*x^2))");

	}

	// {(4+12*x+9*x^2)^(3/2), x, 1, ((2+3*x)*(4+12*x+9*x^2)^(3/2))/12}
	public void test01211() {
		check("Integrate((4+12*x+9*x^2)^(3/2), x)", "((2+3*x)*(4+12*x+9*x^2)^(3/2))/12");

	}

	// {Sqrt(4+12*x+9*x^2), x, 1, ((2+3*x)*Sqrt(4+12*x+9*x^2))/6}
	public void test01212() {
		check("Integrate(Sqrt(4+12*x+9*x^2), x)", "((2+3*x)*Sqrt(4+12*x+9*x^2))/6");

	}

	// {(4+12*x+9*x^2)^(-3/2), x, 1, -1/(6*(2+3*x)*Sqrt(4+12*x+9*x^2))}
	public void test01213() {
		check("Integrate((4+12*x+9*x^2)^(-3/2), x)", "-1/(6*(2+3*x)*Sqrt(4+12*x+9*x^2))");

	}

	// {Sqrt(4-12*x+9*x^2), x, 1, -((2-3*x)*Sqrt(4-12*x+9*x^2))/6}
	public void test01214() {
		check("Integrate(Sqrt(4-12*x+9*x^2), x)", "-((2-3*x)*Sqrt(4-12*x+9*x^2))/6");

	}

	// {Sqrt(-4+12*x-9*x^2), x, 1, -((2-3*x)*Sqrt(-4+12*x-9*x^2))/6}
	public void test01215() {
		check("Integrate(Sqrt(-4+12*x-9*x^2), x)", "-((2-3*x)*Sqrt(-4+12*x-9*x^2))/6");

	}

	// {Sqrt(-4-12*x-9*x^2), x, 1, ((2+3*x)*Sqrt(-4-12*x-9*x^2))/6}
	public void test01216() {
		check("Integrate(Sqrt(-4-12*x-9*x^2), x)", "((2+3*x)*Sqrt(-4-12*x-9*x^2))/6");

	}

	// {(2+3*x+x^2)^(-3/2), x, 1, (-2*(3+2*x))/Sqrt(2+3*x+x^2)}
	public void test01217() {
		check("Integrate((2+3*x+x^2)^(-3/2), x)", "(-2*(3+2*x))/Sqrt(2+3*x+x^2)");

	}

	// {(27-24*x+4*x^2)^(-3/2), x, 1, (3-x)/(9*Sqrt(27-24*x+4*x^2))}
	public void test01218() {
		check("Integrate((27-24*x+4*x^2)^(-3/2), x)", "(3-x)/(9*Sqrt(27-24*x+4*x^2))");

	}

	// {x/(5-4*x-x^2)^(3/2), x, 1, (5-2*x)/(9*Sqrt(5-4*x-x^2))}
	public void test01219() {
		check("Integrate(x/(5-4*x-x^2)^(3/2), x)", "(5-2*x)/(9*Sqrt(5-4*x-x^2))");

	}

	// {(a+b*x+c*x^2)^p, x, 1, -((2^(1+p)*(-((b-Sqrt(b^2-4*a*c)+2*c*x)/Sqrt(b^2-4*a*c)))^(-1-p)*(a+b*x
	// +c*x^2)^(1+p)*Hypergeometric2F1(-p, 1+p, 2+p, (b+Sqrt(b^2-4*a*c)+2*c*x)/(2*Sqrt(b^2 -
	// 4*a*c))))/(Sqrt(b^2-4*a*c)*(1+p)))}
	public void test01220() {
		check("Integrate((a+b*x+c*x^2)^p, x)",
				"-((2^(1+p)*(-((b-Sqrt(b^2-4*a*c)+2*c*x)/Sqrt(b^2-4*a*c)))^(-1-p)*(a+b*x+c*x^2)^(1+p)*Hypergeometric2F1(-p, 1+p, 2+p, (b+Sqrt(b^2-4*a*c)+2*c*x)/(2*Sqrt(b^2-4*a*c))))/(Sqrt(b^2-4*a*c)*(1+p)))");

	}

	// {(3+4*x+x^2)^p, x, 1, -((2^(1+2*p)*(-2-2*x)^(-1-p)*(3+4*x+x^2)^(1+p)*Hypergeometric2F1(-p, 1+p,
	// 2+p, (3+x)/2))/(1+p))}
	public void test01221() {
		check("Integrate((3+4*x+x^2)^p, x)",
				"-((2^(1+2*p)*(-2-2*x)^(-1-p)*(3+4*x+x^2)^(1+p)*Hypergeometric2F1(-p, 1+p, 2+p, (3+x)/2))/(1+p))");

	}

	// {(3+4*x)^p, x, 1, (3+4*x)^(1+p)/(4*(1+p))}
	public void test01222() {
		check("Integrate((3+4*x)^p, x)", "(3+4*x)^(1+p)/(4*(1+p))");

	}

	// {Sqrt(b*x+c*x^2)/x^3, x, 1, (-2*(b*x+c*x^2)^(3/2))/(3*b*x^3)}
	public void test01223() {
		check("Integrate(Sqrt(b*x+c*x^2)/x^3, x)", "(-2*(b*x+c*x^2)^(3/2))/(3*b*x^3)");

	}

	// {(b*x+c*x^2)^(3/2)/x^5, x, 1, (-2*(b*x+c*x^2)^(5/2))/(5*b*x^5)}
	public void test01224() {
		check("Integrate((b*x+c*x^2)^(3/2)/x^5, x)", "(-2*(b*x+c*x^2)^(5/2))/(5*b*x^5)");

	}

	// {(a*x+b*x^2)^(5/2)/x^7, x, 1, (-2*(a*x+b*x^2)^(7/2))/(7*a*x^7)}
	public void test01225() {
		check("Integrate((a*x+b*x^2)^(5/2)/x^7, x)", "(-2*(a*x+b*x^2)^(7/2))/(7*a*x^7)");

	}

	// {1/(x*Sqrt(b*x+c*x^2)), x, 1, (-2*Sqrt(b*x+c*x^2))/(b*x)}
	public void test01226() {
		check("Integrate(1/(x*Sqrt(b*x+c*x^2)), x)", "(-2*Sqrt(b*x+c*x^2))/(b*x)");

	}

	// {x/(b*x+c*x^2)^(3/2), x, 1, (2*x)/(b*Sqrt(b*x+c*x^2))}
	public void test01227() {
		check("Integrate(x/(b*x+c*x^2)^(3/2), x)", "(2*x)/(b*Sqrt(b*x+c*x^2))");

	}

	// {(b*x+c*x^2)^(-3/2), x, 1, (-2*(b+2*c*x))/(b^2*Sqrt(b*x+c*x^2))}
	public void test01228() {
		check("Integrate((b*x+c*x^2)^(-3/2), x)", "(-2*(b+2*c*x))/(b^2*Sqrt(b*x+c*x^2))");

	}

	// {x^3/(a*x+b*x^2)^(5/2), x, 1, (2*x^3)/(3*a*(a*x+b*x^2)^(3/2))}
	public void test01229() {
		check("Integrate(x^3/(a*x+b*x^2)^(5/2), x)", "(2*x^3)/(3*a*(a*x+b*x^2)^(3/2))");

	}

	// {Sqrt(b*x+c*x^2)/Sqrt(x), x, 1, (2*(b*x+c*x^2)^(3/2))/(3*c*x^(3/2))}
	public void test01230() {
		check("Integrate(Sqrt(b*x+c*x^2)/Sqrt(x), x)", "(2*(b*x+c*x^2)^(3/2))/(3*c*x^(3/2))");

	}

	// {(b*x+c*x^2)^(3/2)/x^(3/2), x, 1, (2*(b*x+c*x^2)^(5/2))/(5*c*x^(5/2))}
	public void test01231() {
		check("Integrate((b*x+c*x^2)^(3/2)/x^(3/2), x)", "(2*(b*x+c*x^2)^(5/2))/(5*c*x^(5/2))");

	}

	// {Sqrt(x)/Sqrt(b*x+c*x^2), x, 1, (2*Sqrt(b*x+c*x^2))/(c*Sqrt(x))}
	public void test01232() {
		check("Integrate(Sqrt(x)/Sqrt(b*x+c*x^2), x)", "(2*Sqrt(b*x+c*x^2))/(c*Sqrt(x))");

	}

	// {x^(3/2)/(b*x+c*x^2)^(3/2), x, 1, (-2*Sqrt(x))/(c*Sqrt(b*x+c*x^2))}
	public void test01233() {
		check("Integrate(x^(3/2)/(b*x+c*x^2)^(3/2), x)", "(-2*Sqrt(x))/(c*Sqrt(b*x+c*x^2))");

	}

	// {Sqrt(a^2+2*a*b*x+b^2*x^2), x, 1, ((a+b*x)*Sqrt(a^2+2*a*b*x+b^2*x^2))/(2*b)}
	public void test01234() {
		check("Integrate(Sqrt(a^2+2*a*b*x+b^2*x^2), x)", "((a+b*x)*Sqrt(a^2+2*a*b*x+b^2*x^2))/(2*b)");

	}

	// {(a^2+2*a*b*x+b^2*x^2)^(3/2), x, 1, ((a+b*x)*(a^2+2*a*b*x+b^2*x^2)^(3/2))/(4*b)}
	public void test01235() {
		check("Integrate((a^2+2*a*b*x+b^2*x^2)^(3/2), x)", "((a+b*x)*(a^2+2*a*b*x+b^2*x^2)^(3/2))/(4*b)");

	}

	// {(a^2+2*a*b*x+b^2*x^2)^(5/2), x, 1, ((a+b*x)*(a^2+2*a*b*x+b^2*x^2)^(5/2))/(6*b)}
	public void test01236() {
		check("Integrate((a^2+2*a*b*x+b^2*x^2)^(5/2), x)", "((a+b*x)*(a^2+2*a*b*x+b^2*x^2)^(5/2))/(6*b)");

	}

	// {(a^2+2*a*b*x+b^2*x^2)^(-3/2), x, 1, -1/(2*b*(a+b*x)*Sqrt(a^2+2*a*b*x+b^2*x^2))}
	public void test01237() {
		check("Integrate((a^2+2*a*b*x+b^2*x^2)^(-3/2), x)", "-1/(2*b*(a+b*x)*Sqrt(a^2+2*a*b*x+b^2*x^2))");

	}

	// {(a^2+2*a*b*x+b^2*x^2)^(-5/2), x, 1, -1/(4*b*(a+b*x)*(a^2+2*a*b*x+b^2*x^2)^(3/2))}
	public void test01238() {
		check("Integrate((a^2+2*a*b*x+b^2*x^2)^(-5/2), x)", "-1/(4*b*(a+b*x)*(a^2+2*a*b*x+b^2*x^2)^(3/2))");

	}

	// {(1+x)/(2*x+x^2), x, 1, Log(2*x+x^2)/2}
	public void test01239() {
		check("Integrate((1+x)/(2*x+x^2), x)", "Log(2*x+x^2)/2");

	}

	// {(a+2*b*x)/(a*x+b*x^2), x, 1, Log(a*x+b*x^2)}
	public void test01240() {
		check("Integrate((a+2*b*x)/(a*x+b*x^2), x)", "Log(a*x+b*x^2)");

	}

	// {b*x+c*x^2, x, 1, (b*x^2)/2+(c*x^3)/3}
	public void test01241() {
		check("Integrate(b*x+c*x^2, x)", "(b*x^2)/2+(c*x^3)/3");

	}

	// {(d+e*x)/(b*x+c*x^2)^(3/2), x, 1, (-2*(b*d+(2*c*d-b*e)*x))/(b^2*Sqrt(b*x+c*x^2))}
	public void test01242() {
		check("Integrate((d+e*x)/(b*x+c*x^2)^(3/2), x)", "(-2*(b*d+(2*c*d-b*e)*x))/(b^2*Sqrt(b*x+c*x^2))");

	}

	// {(b*x+c*x^2)^(-3/2), x, 1, (-2*(b+2*c*x))/(b^2*Sqrt(b*x+c*x^2))}
	public void test01243() {
		check("Integrate((b*x+c*x^2)^(-3/2), x)", "(-2*(b+2*c*x))/(b^2*Sqrt(b*x+c*x^2))");

	}

	// {1/((2+x)*Sqrt(2*x+x^2)), x, 1, Sqrt(2*x+x^2)/(2+x)}
	public void test01244() {
		check("Integrate(1/((2+x)*Sqrt(2*x+x^2)), x)", "Sqrt(2*x+x^2)/(2+x)");

	}

	// {Sqrt(1-x)/(Sqrt(-x)*Sqrt(1+x)), x, 1, -2*EllipticE(ArcSin(Sqrt(-x)), -1)}
	public void test01245() {
		check("Integrate(Sqrt(1-x)/(Sqrt(-x)*Sqrt(1+x)), x)", "-2*EllipticE(ArcSin(Sqrt(-x)), -1)");

	}

	// {(d+e*x)^m, x, 1, (d+e*x)^(1+m)/(e*(1+m))}
	public void test01246() {
		check("Integrate((d+e*x)^m, x)", "(d+e*x)^(1+m)/(e*(1+m))");

	}

	// {(d+e*x)/(a+c*x^2)^(3/2), x, 1, -((a*e-c*d*x)/(a*c*Sqrt(a+c*x^2)))}
	public void test01247() {
		check("Integrate((d+e*x)/(a+c*x^2)^(3/2), x)", "-((a*e-c*d*x)/(a*c*Sqrt(a+c*x^2)))");

	}

	// {(2+3*x)/(4+x^2)^(3/2), x, 1, -(6-x)/(2*Sqrt(4+x^2))}
	public void test01248() {
		check("Integrate((2+3*x)/(4+x^2)^(3/2), x)", "-(6-x)/(2*Sqrt(4+x^2))");

	}

	// {1/((d+e*x)*(d^2+3*e^2*x^2)^(1/3)), x, 1, -(ArcTan(1/Sqrt(3)+(2^(2/3)*(d-e*x))/(Sqrt(3)*d^(1/3)*(d^2 +
	// 3*e^2*x^2)^(1/3)))/(2^(2/3)*Sqrt(3)*d^(2/3)*e))-Log(d+e*x)/(2*2^(2/3)*d^(2/3)*e)+Log(3*d*e^2-3*e^3*x -
	// 3*2^(1/3)*d^(1/3)*e^2*(d^2+3*e^2*x^2)^(1/3))/(2*2^(2/3)*d^(2/3)*e)}
	public void test01249() {
		check("Integrate(1/((d+e*x)*(d^2+3*e^2*x^2)^(1/3)), x)",
				"-(ArcTan(1/Sqrt(3)+(2^(2/3)*(d-e*x))/(Sqrt(3)*d^(1/3)*(d^2+3*e^2*x^2)^(1/3)))/(2^(2/3)*Sqrt(3)*d^(2/3)*e))-Log(d+e*x)/(2*2^(2/3)*d^(2/3)*e)+Log(3*d*e^2-3*e^3*x-3*2^(1/3)*d^(1/3)*e^2*(d^2+3*e^2*x^2)^(1/3))/(2*2^(2/3)*d^(2/3)*e)");

	}

	// {1/((2+3*x)*(4+27*x^2)^(1/3)), x, 1, -ArcTan(1/Sqrt(3)+(2^(1/3)*(2-3*x))/(Sqrt(3)*(4 +
	// 27*x^2)^(1/3)))/(6*2^(1/3)*Sqrt(3))-Log(2+3*x)/(12*2^(1/3))+Log(54-81*x-27*2^(2/3)*(4 +
	// 27*x^2)^(1/3))/(12*2^(1/3))}
	public void test01250() {
		check("Integrate(1/((2+3*x)*(4+27*x^2)^(1/3)), x)",
				"-ArcTan(1/Sqrt(3)+(2^(1/3)*(2-3*x))/(Sqrt(3)*(4+27*x^2)^(1/3)))/(6*2^(1/3)*Sqrt(3))-Log(2+3*x)/(12*2^(1/3))+Log(54-81*x-27*2^(2/3)*(4+27*x^2)^(1/3))/(12*2^(1/3))");

	}

	// {1/((2+(3*I)*x)*(4-27*x^2)^(1/3)), x, 1, ((I/6)*ArcTan(1/Sqrt(3)+(2^(1/3)*(2-(3*I)*x))/(Sqrt(3)*(4 -
	// 27*x^2)^(1/3))))/(2^(1/3)*Sqrt(3))+((I/12)*Log(2+(3*I)*x))/2^(1/3)-((I/12)*Log(-54+(81*I)*x +
	// 27*2^(2/3)*(4-27*x^2)^(1/3)))/2^(1/3)}
	public void test01251() {
		check("Integrate(1/((2+(3*I)*x)*(4-27*x^2)^(1/3)), x)",
				"((I/6)*ArcTan(1/Sqrt(3)+(2^(1/3)*(2-(3*I)*x))/(Sqrt(3)*(4-27*x^2)^(1/3))))/(2^(1/3)*Sqrt(3))+((I/12)*Log(2+(3*I)*x))/2^(1/3)-((I/12)*Log(-54+(81*I)*x+27*2^(2/3)*(4-27*x^2)^(1/3)))/2^(1/3)");

	}

	// {1/((Sqrt(3)+x)*(1+x^2)^(1/3)), x, 1, -(ArcTan(1/Sqrt(3)+(2^(2/3)*(Sqrt(3)-x))/(3*(1 +
	// x^2)^(1/3)))/(2^(2/3)*Sqrt(3)))-Log(Sqrt(3)+x)/(2*2^(2/3))+Log(Sqrt(3)-x-2^(1/3)*Sqrt(3)*(1 +
	// x^2)^(1/3))/(2*2^(2/3))}
	public void test01252() {
		check("Integrate(1/((Sqrt(3)+x)*(1+x^2)^(1/3)), x)",
				"-(ArcTan(1/Sqrt(3)+(2^(2/3)*(Sqrt(3)-x))/(3*(1+x^2)^(1/3)))/(2^(2/3)*Sqrt(3)))-Log(Sqrt(3)+x)/(2*2^(2/3))+Log(Sqrt(3)-x-2^(1/3)*Sqrt(3)*(1+x^2)^(1/3))/(2*2^(2/3))");

	}

	// {1/((Sqrt(3)-x)*(1+x^2)^(1/3)), x, 1, ArcTan(1/Sqrt(3)+(2^(2/3)*(Sqrt(3)+x))/(3*(1 +
	// x^2)^(1/3)))/(2^(2/3)*Sqrt(3))+Log(Sqrt(3)-x)/(2*2^(2/3))-Log(Sqrt(3)+x-2^(1/3)*Sqrt(3)*(1 +
	// x^2)^(1/3))/(2*2^(2/3))}
	public void test01253() {
		check("Integrate(1/((Sqrt(3)-x)*(1+x^2)^(1/3)), x)",
				"ArcTan(1/Sqrt(3)+(2^(2/3)*(Sqrt(3)+x))/(3*(1+x^2)^(1/3)))/(2^(2/3)*Sqrt(3))+Log(Sqrt(3)-x)/(2*2^(2/3))-Log(Sqrt(3)+x-2^(1/3)*Sqrt(3)*(1+x^2)^(1/3))/(2*2^(2/3))");

	}

	// {1/((d+e*x)^(3/2)*(a+c*x^2)^(1/4)), x, 1, (-2*(Sqrt(-a)-Sqrt(c)*x)*(-(((Sqrt(c)*d+Sqrt(-a)*e)*(Sqrt(-a) +
	// Sqrt(c)*x))/((Sqrt(c)*d-Sqrt(-a)*e)*(Sqrt(-a)-Sqrt(c)*x))))^(1/4)*Hypergeometric2F1(-1/2, 1/4, 1/2,
	// (2*Sqrt(-a)*Sqrt(c)*(d+e*x))/((Sqrt(c)*d-Sqrt(-a)*e)*(Sqrt(-a)-Sqrt(c)*x))))/((Sqrt(c)*d +
	// Sqrt(-a)*e)*Sqrt(d+e*x)*(a+c*x^2)^(1/4))}
	public void test01254() {
		check("Integrate(1/((d+e*x)^(3/2)*(a+c*x^2)^(1/4)), x)",
				"(-2*(Sqrt(-a)-Sqrt(c)*x)*(-(((Sqrt(c)*d+Sqrt(-a)*e)*(Sqrt(-a)+Sqrt(c)*x))/((Sqrt(c)*d-Sqrt(-a)*e)*(Sqrt(-a)-Sqrt(c)*x))))^(1/4)*Hypergeometric2F1(-1/2, 1/4, 1/2, (2*Sqrt(-a)*Sqrt(c)*(d+e*x))/((Sqrt(c)*d-Sqrt(-a)*e)*(Sqrt(-a)-Sqrt(c)*x))))/((Sqrt(c)*d+Sqrt(-a)*e)*Sqrt(d+e*x)*(a+c*x^2)^(1/4))");

	}

	// {(d+e*x)^(-2-2*p)*(a+c*x^2)^p, x, 1, -(((Sqrt(-a)-Sqrt(c)*x)*(d+e*x)^(-1-2*p)*(a +
	// c*x^2)^p*Hypergeometric2F1(-1-2*p, -p, -2*p, (2*Sqrt(-a)*Sqrt(c)*(d+e*x))/((Sqrt(c)*d-Sqrt(-a)*e)*(Sqrt(-a)
	// -Sqrt(c)*x))))/((Sqrt(c)*d+Sqrt(-a)*e)*(1+2*p)*(-(((Sqrt(c)*d+Sqrt(-a)*e)*(Sqrt(-a) +
	// Sqrt(c)*x))/((Sqrt(c)*d-Sqrt(-a)*e)*(Sqrt(-a)-Sqrt(c)*x))))^p))}
	public void test01255() {
		check("Integrate((d+e*x)^(-2-2*p)*(a+c*x^2)^p, x)",
				"-(((Sqrt(-a)-Sqrt(c)*x)*(d+e*x)^(-1-2*p)*(a+c*x^2)^p*Hypergeometric2F1(-1-2*p, -p, -2*p, (2*Sqrt(-a)*Sqrt(c)*(d+e*x))/((Sqrt(c)*d-Sqrt(-a)*e)*(Sqrt(-a)-Sqrt(c)*x))))/((Sqrt(c)*d+Sqrt(-a)*e)*(1+2*p)*(-(((Sqrt(c)*d+Sqrt(-a)*e)*(Sqrt(-a)+Sqrt(c)*x))/((Sqrt(c)*d-Sqrt(-a)*e)*(Sqrt(-a)-Sqrt(c)*x))))^p))");

	}

	// {Sqrt(a^2-b^2*x^2)/(a+b*x)^3, x, 1, -(a^2-b^2*x^2)^(3/2)/(3*a*b*(a+b*x)^3)}
	public void test01256() {
		check("Integrate(Sqrt(a^2-b^2*x^2)/(a+b*x)^3, x)", "-(a^2-b^2*x^2)^(3/2)/(3*a*b*(a+b*x)^3)");

	}

	// {(a^2-b^2*x^2)^(3/2)/(a+b*x)^5, x, 1, -(a^2-b^2*x^2)^(5/2)/(5*a*b*(a+b*x)^5)}
	public void test01257() {
		check("Integrate((a^2-b^2*x^2)^(3/2)/(a+b*x)^5, x)", "-(a^2-b^2*x^2)^(5/2)/(5*a*b*(a+b*x)^5)");

	}

	// {(d^2-e^2*x^2)^(7/2)/(d+e*x)^9, x, 1, -(d^2-e^2*x^2)^(9/2)/(9*d*e*(d+e*x)^9)}
	public void test01258() {
		check("Integrate((d^2-e^2*x^2)^(7/2)/(d+e*x)^9, x)", "-(d^2-e^2*x^2)^(9/2)/(9*d*e*(d+e*x)^9)");

	}

	// {Sqrt(1-x^2)/(1-x)^3, x, 1, (1-x^2)^(3/2)/(3*(1-x)^3)}
	public void test01259() {
		check("Integrate(Sqrt(1-x^2)/(1-x)^3, x)", "(1-x^2)^(3/2)/(3*(1-x)^3)");

	}

	// {1/((d+e*x)*Sqrt(d^2-e^2*x^2)), x, 1, -(Sqrt(d^2-e^2*x^2)/(d*e*(d+e*x)))}
	public void test01260() {
		check("Integrate(1/((d+e*x)*Sqrt(d^2-e^2*x^2)), x)", "-(Sqrt(d^2-e^2*x^2)/(d*e*(d+e*x)))");

	}

	// {(d+e*x)^3/(d^2-e^2*x^2)^(5/2), x, 1, (d+e*x)^3/(3*d*e*(d^2-e^2*x^2)^(3/2))}
	public void test01261() {
		check("Integrate((d+e*x)^3/(d^2-e^2*x^2)^(5/2), x)", "(d+e*x)^3/(3*d*e*(d^2-e^2*x^2)^(3/2))");

	}

	// {(d+e*x)^5/(d^2-e^2*x^2)^(7/2), x, 1, (d+e*x)^5/(5*d*e*(d^2-e^2*x^2)^(5/2))}
	public void test01262() {
		check("Integrate((d+e*x)^5/(d^2-e^2*x^2)^(7/2), x)", "(d+e*x)^5/(5*d*e*(d^2-e^2*x^2)^(5/2))");

	}

	// {Sqrt(c*d^2-c*e^2*x^2)/Sqrt(d+e*x), x, 1, (-2*(c*d^2-c*e^2*x^2)^(3/2))/(3*c*e*(d+e*x)^(3/2))}
	public void test01263() {
		check("Integrate(Sqrt(c*d^2-c*e^2*x^2)/Sqrt(d+e*x), x)", "(-2*(c*d^2-c*e^2*x^2)^(3/2))/(3*c*e*(d+e*x)^(3/2))");

	}

	// {(c*d^2-c*e^2*x^2)^(3/2)/(d+e*x)^(3/2), x, 1, (-2*(c*d^2-c*e^2*x^2)^(5/2))/(5*c*e*(d+e*x)^(5/2))}
	public void test01264() {
		check("Integrate((c*d^2-c*e^2*x^2)^(3/2)/(d+e*x)^(3/2), x)",
				"(-2*(c*d^2-c*e^2*x^2)^(5/2))/(5*c*e*(d+e*x)^(5/2))");

	}

	// {Sqrt(d+e*x)/Sqrt(c*d^2-c*e^2*x^2), x, 1, (-2*Sqrt(c*d^2-c*e^2*x^2))/(c*e*Sqrt(d+e*x))}
	public void test01265() {
		check("Integrate(Sqrt(d+e*x)/Sqrt(c*d^2-c*e^2*x^2), x)", "(-2*Sqrt(c*d^2-c*e^2*x^2))/(c*e*Sqrt(d+e*x))");

	}

	// {(d+e*x)^(3/2)/(c*d^2-c*e^2*x^2)^(3/2), x, 1, (2*Sqrt(d+e*x))/(c*e*Sqrt(c*d^2-c*e^2*x^2))}
	public void test01266() {
		check("Integrate((d+e*x)^(3/2)/(c*d^2-c*e^2*x^2)^(3/2), x)", "(2*Sqrt(d+e*x))/(c*e*Sqrt(c*d^2-c*e^2*x^2))");

	}

	// {(12-3*e^2*x^2)^(1/4)/(2+e*x)^(5/2), x, 1, -(3^(1/4)*(4-e^2*x^2)^(5/4))/(5*e*(2+e*x)^(5/2))}
	public void test01267() {
		check("Integrate((12-3*e^2*x^2)^(1/4)/(2+e*x)^(5/2), x)", "-(3^(1/4)*(4-e^2*x^2)^(5/4))/(5*e*(2+e*x)^(5/2))");

	}

	// {1/((2+e*x)^(3/2)*(12-3*e^2*x^2)^(1/4)), x, 1, -(4-e^2*x^2)^(3/4)/(3*3^(1/4)*e*(2+e*x)^(3/2))}
	public void test01268() {
		check("Integrate(1/((2+e*x)^(3/2)*(12-3*e^2*x^2)^(1/4)), x)", "-(4-e^2*x^2)^(3/4)/(3*3^(1/4)*e*(2+e*x)^(3/2))");

	}

	// {(1-(e^2*x^2)/d^2)^p, x, 1, x*Hypergeometric2F1(1/2, -p, 3/2, (e^2*x^2)/d^2)}
	public void test01269() {
		check("Integrate((1-(e^2*x^2)/d^2)^p, x)", "x*Hypergeometric2F1(1/2, -p, 3/2, (e^2*x^2)/d^2)");

	}

	// {c*d^2+2*c*d*e*x+c*e^2*x^2, x, 1, c*d^2*x+c*d*e*x^2+(c*e^2*x^3)/3}
	public void test01270() {
		check("Integrate(c*d^2+2*c*d*e*x+c*e^2*x^2, x)", "c*d^2*x+c*d*e*x^2+(c*e^2*x^3)/3");

	}

	// {(d+e*x)*Sqrt(c*d^2+2*c*d*e*x+c*e^2*x^2), x, 1, (c*d^2+2*c*d*e*x+c*e^2*x^2)^(3/2)/(3*c*e)}
	public void test01271() {
		check("Integrate((d+e*x)*Sqrt(c*d^2+2*c*d*e*x+c*e^2*x^2), x)", "(c*d^2+2*c*d*e*x+c*e^2*x^2)^(3/2)/(3*c*e)");

	}

	// {Sqrt(c*d^2+2*c*d*e*x+c*e^2*x^2), x, 1, ((d+e*x)*Sqrt(c*d^2+2*c*d*e*x+c*e^2*x^2))/(2*e)}
	public void test01272() {
		check("Integrate(Sqrt(c*d^2+2*c*d*e*x+c*e^2*x^2), x)", "((d+e*x)*Sqrt(c*d^2+2*c*d*e*x+c*e^2*x^2))/(2*e)");

	}

	// {(d+e*x)*(c*d^2+2*c*d*e*x+c*e^2*x^2)^(3/2), x, 1, (c*d^2+2*c*d*e*x+c*e^2*x^2)^(5/2)/(5*c*e)}
	public void test01273() {
		check("Integrate((d+e*x)*(c*d^2+2*c*d*e*x+c*e^2*x^2)^(3/2), x)", "(c*d^2+2*c*d*e*x+c*e^2*x^2)^(5/2)/(5*c*e)");

	}

	// {(c*d^2+2*c*d*e*x+c*e^2*x^2)^(3/2), x, 1, ((d+e*x)*(c*d^2+2*c*d*e*x+c*e^2*x^2)^(3/2))/(4*e)}
	public void test01274() {
		check("Integrate((c*d^2+2*c*d*e*x+c*e^2*x^2)^(3/2), x)", "((d+e*x)*(c*d^2+2*c*d*e*x+c*e^2*x^2)^(3/2))/(4*e)");

	}

	// {(d+e*x)*(c*d^2+2*c*d*e*x+c*e^2*x^2)^(5/2), x, 1, (c*d^2+2*c*d*e*x+c*e^2*x^2)^(7/2)/(7*c*e)}
	public void test01275() {
		check("Integrate((d+e*x)*(c*d^2+2*c*d*e*x+c*e^2*x^2)^(5/2), x)", "(c*d^2+2*c*d*e*x+c*e^2*x^2)^(7/2)/(7*c*e)");

	}

	// {(c*d^2+2*c*d*e*x+c*e^2*x^2)^(5/2), x, 1, ((d+e*x)*(c*d^2+2*c*d*e*x+c*e^2*x^2)^(5/2))/(6*e)}
	public void test01276() {
		check("Integrate((c*d^2+2*c*d*e*x+c*e^2*x^2)^(5/2), x)", "((d+e*x)*(c*d^2+2*c*d*e*x+c*e^2*x^2)^(5/2))/(6*e)");

	}

	// {(d+e*x)/Sqrt(c*d^2+2*c*d*e*x+c*e^2*x^2), x, 1, Sqrt(c*d^2+2*c*d*e*x+c*e^2*x^2)/(c*e)}
	public void test01277() {
		check("Integrate((d+e*x)/Sqrt(c*d^2+2*c*d*e*x+c*e^2*x^2), x)", "Sqrt(c*d^2+2*c*d*e*x+c*e^2*x^2)/(c*e)");

	}

	// {(d+e*x)/(c*d^2+2*c*d*e*x+c*e^2*x^2)^(3/2), x, 1, -(1/(c*e*Sqrt(c*d^2+2*c*d*e*x+c*e^2*x^2)))}
	public void test01278() {
		check("Integrate((d+e*x)/(c*d^2+2*c*d*e*x+c*e^2*x^2)^(3/2), x)", "-(1/(c*e*Sqrt(c*d^2+2*c*d*e*x+c*e^2*x^2)))");

	}

	// {(c*d^2+2*c*d*e*x+c*e^2*x^2)^(-3/2), x, 1, -1/(2*c*e*(d+e*x)*Sqrt(c*d^2+2*c*d*e*x+c*e^2*x^2))}
	public void test01279() {
		check("Integrate((c*d^2+2*c*d*e*x+c*e^2*x^2)^(-3/2), x)", "-1/(2*c*e*(d+e*x)*Sqrt(c*d^2+2*c*d*e*x+c*e^2*x^2))");

	}

	// {(d+e*x)/(c*d^2+2*c*d*e*x+c*e^2*x^2)^(5/2), x, 1, -1/(3*c*e*(c*d^2+2*c*d*e*x+c*e^2*x^2)^(3/2))}
	public void test01280() {
		check("Integrate((d+e*x)/(c*d^2+2*c*d*e*x+c*e^2*x^2)^(5/2), x)",
				"-1/(3*c*e*(c*d^2+2*c*d*e*x+c*e^2*x^2)^(3/2))");

	}

	// {(c*d^2+2*c*d*e*x+c*e^2*x^2)^(-5/2), x, 1, -1/(4*c*e*(d+e*x)*(c*d^2+2*c*d*e*x+c*e^2*x^2)^(3/2))}
	public void test01281() {
		check("Integrate((c*d^2+2*c*d*e*x+c*e^2*x^2)^(-5/2), x)",
				"-1/(4*c*e*(d+e*x)*(c*d^2+2*c*d*e*x+c*e^2*x^2)^(3/2))");

	}

	// {(d+e*x)*(c*d^2+2*c*d*e*x+c*e^2*x^2)^p, x, 1, (c*d^2+2*c*d*e*x+c*e^2*x^2)^(1+p)/(2*c*e*(1+p))}
	public void test01282() {
		check("Integrate((d+e*x)*(c*d^2+2*c*d*e*x+c*e^2*x^2)^p, x)", "(c*d^2+2*c*d*e*x+c*e^2*x^2)^(1+p)/(2*c*e*(1+p))");

	}

	// {(c*d^2+2*c*d*e*x+c*e^2*x^2)^p, x, 1, ((d+e*x)*(c*d^2+2*c*d*e*x+c*e^2*x^2)^p)/(e*(1+2*p))}
	public void test01283() {
		check("Integrate((c*d^2+2*c*d*e*x+c*e^2*x^2)^p, x)", "((d+e*x)*(c*d^2+2*c*d*e*x+c*e^2*x^2)^p)/(e*(1+2*p))");

	}

	// {(b*d+2*c*d*x)*(a+b*x+c*x^2), x, 1, (d*(a+b*x+c*x^2)^2)/2}
	public void test01284() {
		check("Integrate((b*d+2*c*d*x)*(a+b*x+c*x^2), x)", "(d*(a+b*x+c*x^2)^2)/2");

	}

	// {(a+b*x+c*x^2)/(b*d+2*c*d*x)^5, x, 1, (a+b*x+c*x^2)^2/(2*(b^2-4*a*c)*d^5*(b+2*c*x)^4)}
	public void test01285() {
		check("Integrate((a+b*x+c*x^2)/(b*d+2*c*d*x)^5, x)", "(a+b*x+c*x^2)^2/(2*(b^2-4*a*c)*d^5*(b+2*c*x)^4)");

	}

	// {(b*d+2*c*d*x)*(a+b*x+c*x^2)^2, x, 1, (d*(a+b*x+c*x^2)^3)/3}
	public void test01286() {
		check("Integrate((b*d+2*c*d*x)*(a+b*x+c*x^2)^2, x)", "(d*(a+b*x+c*x^2)^3)/3");

	}

	// {(a+b*x+c*x^2)^2/(b*d+2*c*d*x)^7, x, 1, (a+b*x+c*x^2)^3/(3*(b^2-4*a*c)*d^7*(b+2*c*x)^6)}
	public void test01287() {
		check("Integrate((a+b*x+c*x^2)^2/(b*d+2*c*d*x)^7, x)", "(a+b*x+c*x^2)^3/(3*(b^2-4*a*c)*d^7*(b+2*c*x)^6)");

	}

	// {(b*d+2*c*d*x)*(a+b*x+c*x^2)^3, x, 1, (d*(a+b*x+c*x^2)^4)/4}
	public void test01288() {
		check("Integrate((b*d+2*c*d*x)*(a+b*x+c*x^2)^3, x)", "(d*(a+b*x+c*x^2)^4)/4");

	}

	// {(a+b*x+c*x^2)^3/(b*d+2*c*d*x)^9, x, 1, (a+b*x+c*x^2)^4/(4*(b^2-4*a*c)*d^9*(b+2*c*x)^8)}
	public void test01289() {
		check("Integrate((a+b*x+c*x^2)^3/(b*d+2*c*d*x)^9, x)", "(a+b*x+c*x^2)^4/(4*(b^2-4*a*c)*d^9*(b+2*c*x)^8)");

	}

	// {(b*d+2*c*d*x)/(a+b*x+c*x^2), x, 1, d*Log(a+b*x+c*x^2)}
	public void test01290() {
		check("Integrate((b*d+2*c*d*x)/(a+b*x+c*x^2), x)", "d*Log(a+b*x+c*x^2)");

	}

	// {(b*d+2*c*d*x)/(a+b*x+c*x^2)^2, x, 1, -(d/(a+b*x+c*x^2))}
	public void test01291() {
		check("Integrate((b*d+2*c*d*x)/(a+b*x+c*x^2)^2, x)", "-(d/(a+b*x+c*x^2))");

	}

	// {(b*d+2*c*d*x)^3/(a+b*x+c*x^2)^3, x, 1, -(d^3*(b+2*c*x)^4)/(2*(b^2-4*a*c)*(a+b*x+c*x^2)^2)}
	public void test01292() {
		check("Integrate((b*d+2*c*d*x)^3/(a+b*x+c*x^2)^3, x)", "-(d^3*(b+2*c*x)^4)/(2*(b^2-4*a*c)*(a+b*x+c*x^2)^2)");

	}

	// {(b*d+2*c*d*x)/(a+b*x+c*x^2)^3, x, 1, -d/(2*(a+b*x+c*x^2)^2)}
	public void test01293() {
		check("Integrate((b*d+2*c*d*x)/(a+b*x+c*x^2)^3, x)", "-d/(2*(a+b*x+c*x^2)^2)");

	}

	// {(b*d+2*c*d*x)*Sqrt(a+b*x+c*x^2), x, 1, (2*d*(a+b*x+c*x^2)^(3/2))/3}
	public void test01294() {
		check("Integrate((b*d+2*c*d*x)*Sqrt(a+b*x+c*x^2), x)", "(2*d*(a+b*x+c*x^2)^(3/2))/3");

	}

	// {Sqrt(a+b*x+c*x^2)/(b*d+2*c*d*x)^4, x, 1, (2*(a+b*x+c*x^2)^(3/2))/(3*(b^2-4*a*c)*d^4*(b+2*c*x)^3)}
	public void test01295() {
		check("Integrate(Sqrt(a+b*x+c*x^2)/(b*d+2*c*d*x)^4, x)",
				"(2*(a+b*x+c*x^2)^(3/2))/(3*(b^2-4*a*c)*d^4*(b+2*c*x)^3)");

	}

	// {(b*d+2*c*d*x)*(a+b*x+c*x^2)^(3/2), x, 1, (2*d*(a+b*x+c*x^2)^(5/2))/5}
	public void test01296() {
		check("Integrate((b*d+2*c*d*x)*(a+b*x+c*x^2)^(3/2), x)", "(2*d*(a+b*x+c*x^2)^(5/2))/5");

	}

	// {(a+b*x+c*x^2)^(3/2)/(b*d+2*c*d*x)^6, x, 1, (2*(a+b*x+c*x^2)^(5/2))/(5*(b^2-4*a*c)*d^6*(b +
	// 2*c*x)^5)}
	public void test01297() {
		check("Integrate((a+b*x+c*x^2)^(3/2)/(b*d+2*c*d*x)^6, x)",
				"(2*(a+b*x+c*x^2)^(5/2))/(5*(b^2-4*a*c)*d^6*(b+2*c*x)^5)");

	}

	// {(b*d+2*c*d*x)*(a+b*x+c*x^2)^(5/2), x, 1, (2*d*(a+b*x+c*x^2)^(7/2))/7}
	public void test01298() {
		check("Integrate((b*d+2*c*d*x)*(a+b*x+c*x^2)^(5/2), x)", "(2*d*(a+b*x+c*x^2)^(7/2))/7");

	}

	// {(a+b*x+c*x^2)^(5/2)/(b*d+2*c*d*x)^8, x, 1, (2*(a+b*x+c*x^2)^(7/2))/(7*(b^2-4*a*c)*d^8*(b +
	// 2*c*x)^7)}
	public void test01299() {
		check("Integrate((a+b*x+c*x^2)^(5/2)/(b*d+2*c*d*x)^8, x)",
				"(2*(a+b*x+c*x^2)^(7/2))/(7*(b^2-4*a*c)*d^8*(b+2*c*x)^7)");

	}

	// {(b*d+2*c*d*x)/Sqrt(a+b*x+c*x^2), x, 1, 2*d*Sqrt(a+b*x+c*x^2)}
	public void test01300() {
		check("Integrate((b*d+2*c*d*x)/Sqrt(a+b*x+c*x^2), x)", "2*d*Sqrt(a+b*x+c*x^2)");

	}

	// {1/((b*d+2*c*d*x)^2*Sqrt(a+b*x+c*x^2)), x, 1, (2*Sqrt(a+b*x+c*x^2))/((b^2-4*a*c)*d^2*(b+2*c*x))}
	public void test01301() {
		check("Integrate(1/((b*d+2*c*d*x)^2*Sqrt(a+b*x+c*x^2)), x)",
				"(2*Sqrt(a+b*x+c*x^2))/((b^2-4*a*c)*d^2*(b+2*c*x))");

	}

	// {(b*d+2*c*d*x)/(a+b*x+c*x^2)^(3/2), x, 1, (-2*d)/Sqrt(a+b*x+c*x^2)}
	public void test01302() {
		check("Integrate((b*d+2*c*d*x)/(a+b*x+c*x^2)^(3/2), x)", "(-2*d)/Sqrt(a+b*x+c*x^2)");

	}

	// {(b*d+2*c*d*x)^2/(a+b*x+c*x^2)^(5/2), x, 1, (-2*d^2*(b+2*c*x)^3)/(3*(b^2-4*a*c)*(a+b*x +
	// c*x^2)^(3/2))}
	public void test01303() {
		check("Integrate((b*d+2*c*d*x)^2/(a+b*x+c*x^2)^(5/2), x)",
				"(-2*d^2*(b+2*c*x)^3)/(3*(b^2-4*a*c)*(a+b*x+c*x^2)^(3/2))");

	}

	// {(b*d+2*c*d*x)/(a+b*x+c*x^2)^(5/2), x, 1, (-2*d)/(3*(a+b*x+c*x^2)^(3/2))}
	public void test01304() {
		check("Integrate((b*d+2*c*d*x)/(a+b*x+c*x^2)^(5/2), x)", "(-2*d)/(3*(a+b*x+c*x^2)^(3/2))");

	}

	// {(a+b*x+c*x^2)^(4/3)/(b*d+2*c*d*x)^(17/3), x, 1, (3*(a+b*x+c*x^2)^(7/3))/(7*(b^2-4*a*c)*d*(b*d +
	// 2*c*d*x)^(14/3))}
	public void test01305() {
		check("Integrate((a+b*x+c*x^2)^(4/3)/(b*d+2*c*d*x)^(17/3), x)",
				"(3*(a+b*x+c*x^2)^(7/3))/(7*(b^2-4*a*c)*d*(b*d+2*c*d*x)^(14/3))");

	}

	// {(b*d+2*c*d*x)*(a+b*x+c*x^2)^p, x, 1, (d*(a+b*x+c*x^2)^(1+p))/(1+p)}
	public void test01306() {
		check("Integrate((b*d+2*c*d*x)*(a+b*x+c*x^2)^p, x)", "(d*(a+b*x+c*x^2)^(1+p))/(1+p)");

	}

	// {(1+x)/(-3+2*x+x^2)^(2/3), x, 1, (3*(-3+2*x+x^2)^(1/3))/2}
	public void test01307() {
		check("Integrate((1+x)/(-3+2*x+x^2)^(2/3), x)", "(3*(-3+2*x+x^2)^(1/3))/2");

	}

	// {(b+c*x)/(a+2*b*x+c*x^2)^(3/7), x, 1, (7*(a+2*b*x+c*x^2)^(4/7))/8}
	public void test01308() {
		check("Integrate((b+c*x)/(a+2*b*x+c*x^2)^(3/7), x)", "(7*(a+2*b*x+c*x^2)^(4/7))/8");

	}

	// {a^2+2*a*b*x+b^2*x^2, x, 1, a^2*x+a*b*x^2+(b^2*x^3)/3}
	public void test01309() {
		check("Integrate(a^2+2*a*b*x+b^2*x^2, x)", "a^2*x+a*b*x^2+(b^2*x^3)/3");

	}

	// {Sqrt(a^2+2*a*b*x+b^2*x^2), x, 1, ((a+b*x)*Sqrt(a^2+2*a*b*x+b^2*x^2))/(2*b)}
	public void test01310() {
		check("Integrate(Sqrt(a^2+2*a*b*x+b^2*x^2), x)", "((a+b*x)*Sqrt(a^2+2*a*b*x+b^2*x^2))/(2*b)");

	}

	// {(a^2+2*a*b*x+b^2*x^2)^(3/2), x, 1, ((a+b*x)*(a^2+2*a*b*x+b^2*x^2)^(3/2))/(4*b)}
	public void test01311() {
		check("Integrate((a^2+2*a*b*x+b^2*x^2)^(3/2), x)", "((a+b*x)*(a^2+2*a*b*x+b^2*x^2)^(3/2))/(4*b)");

	}

	// {(a^2+2*a*b*x+b^2*x^2)^(5/2), x, 1, ((a+b*x)*(a^2+2*a*b*x+b^2*x^2)^(5/2))/(6*b)}
	public void test01312() {
		check("Integrate((a^2+2*a*b*x+b^2*x^2)^(5/2), x)", "((a+b*x)*(a^2+2*a*b*x+b^2*x^2)^(5/2))/(6*b)");

	}

	// {(a^2+2*a*b*x+b^2*x^2)^(-3/2), x, 1, -1/(2*b*(a+b*x)*Sqrt(a^2+2*a*b*x+b^2*x^2))}
	public void test01313() {
		check("Integrate((a^2+2*a*b*x+b^2*x^2)^(-3/2), x)", "-1/(2*b*(a+b*x)*Sqrt(a^2+2*a*b*x+b^2*x^2))");

	}

	// {(a^2+2*a*b*x+b^2*x^2)^(-5/2), x, 1, -1/(4*b*(a+b*x)*(a^2+2*a*b*x+b^2*x^2)^(3/2))}
	public void test01314() {
		check("Integrate((a^2+2*a*b*x+b^2*x^2)^(-5/2), x)", "-1/(4*b*(a+b*x)*(a^2+2*a*b*x+b^2*x^2)^(3/2))");

	}

	// {(a^2+2*a*b*x+b^2*x^2)^p, x, 1, ((a+b*x)*(a^2+2*a*b*x+b^2*x^2)^p)/(b*(1+2*p))}
	public void test01315() {
		check("Integrate((a^2+2*a*b*x+b^2*x^2)^p, x)", "((a+b*x)*(a^2+2*a*b*x+b^2*x^2)^p)/(b*(1+2*p))");

	}

	// {a*c+(b*c+a*d)*x+b*d*x^2, x, 1, a*c*x+((b*c+a*d)*x^2)/2+(b*d*x^3)/3}
	public void test01316() {
		check("Integrate(a*c+(b*c+a*d)*x+b*d*x^2, x)", "a*c*x+((b*c+a*d)*x^2)/2+(b*d*x^3)/3");

	}

	// {a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2, x, 1, a*d*e*x+((c*d^2+a*e^2)*x^2)/2+(c*d*e*x^3)/3}
	public void test01317() {
		check("Integrate(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2, x)", "a*d*e*x+((c*d^2+a*e^2)*x^2)/2+(c*d*e*x^3)/3");

	}

	// {Sqrt(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)/(d+e*x)^3, x, 1, (2*(a*d*e+(c*d^2+a*e^2)*x +
	// c*d*e*x^2)^(3/2))/(3*(c*d^2-a*e^2)*(d+e*x)^3)}
	public void test01318() {
		check("Integrate(Sqrt(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)/(d+e*x)^3, x)",
				"(2*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2))/(3*(c*d^2-a*e^2)*(d+e*x)^3)");

	}

	// {(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2)/(d+e*x)^5, x, 1, (2*(a*d*e+(c*d^2+a*e^2)*x +
	// c*d*e*x^2)^(5/2))/(5*(c*d^2-a*e^2)*(d+e*x)^5)}
	public void test01319() {
		check("Integrate((a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2)/(d+e*x)^5, x)",
				"(2*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(5/2))/(5*(c*d^2-a*e^2)*(d+e*x)^5)");

	}

	// {(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(5/2)/(d+e*x)^7, x, 1, (2*(a*d*e+(c*d^2+a*e^2)*x +
	// c*d*e*x^2)^(7/2))/(7*(c*d^2-a*e^2)*(d+e*x)^7)}
	public void test01320() {
		check("Integrate((a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(5/2)/(d+e*x)^7, x)",
				"(2*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(7/2))/(7*(c*d^2-a*e^2)*(d+e*x)^7)");

	}

	// {1/((d+e*x)*Sqrt(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)), x, 1, (2*Sqrt(a*d*e+(c*d^2+a*e^2)*x +
	// c*d*e*x^2))/((c*d^2-a*e^2)*(d+e*x))}
	public void test01321() {
		check("Integrate(1/((d+e*x)*Sqrt(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)), x)",
				"(2*Sqrt(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2))/((c*d^2-a*e^2)*(d+e*x))");

	}

	// {(d+e*x)/(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2), x, 1, (-2*(d+e*x))/((c*d^2-a*e^2)*Sqrt(a*d*e +
	// (c*d^2+a*e^2)*x+c*d*e*x^2))}
	public void test01322() {
		check("Integrate((d+e*x)/(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2), x)",
				"(-2*(d+e*x))/((c*d^2-a*e^2)*Sqrt(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2))");

	}

	// {(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(-3/2), x, 1, (-2*(c*d^2+a*e^2+2*c*d*e*x))/((c*d^2 -
	// a*e^2)^2*Sqrt(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2))}
	public void test01323() {
		check("Integrate((a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(-3/2), x)",
				"(-2*(c*d^2+a*e^2+2*c*d*e*x))/((c*d^2-a*e^2)^2*Sqrt(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2))");

	}

	// {(d+e*x)^3/(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(5/2), x, 1, (-2*(d+e*x)^3)/(3*(c*d^2-a*e^2)*(a*d*e +
	// (c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2))}
	public void test01324() {
		check("Integrate((d+e*x)^3/(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(5/2), x)",
				"(-2*(d+e*x)^3)/(3*(c*d^2-a*e^2)*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2))");

	}

	// {Sqrt(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)/Sqrt(d+e*x), x, 1, (2*(a*d*e+(c*d^2+a*e^2)*x +
	// c*d*e*x^2)^(3/2))/(3*c*d*(d+e*x)^(3/2))}
	public void test01325() {
		check("Integrate(Sqrt(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)/Sqrt(d+e*x), x)",
				"(2*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2))/(3*c*d*(d+e*x)^(3/2))");

	}

	// {(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2)/(d+e*x)^(3/2), x, 1, (2*(a*d*e+(c*d^2+a*e^2)*x +
	// c*d*e*x^2)^(5/2))/(5*c*d*(d+e*x)^(5/2))}
	public void test01326() {
		check("Integrate((a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2)/(d+e*x)^(3/2), x)",
				"(2*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(5/2))/(5*c*d*(d+e*x)^(5/2))");

	}

	// {(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(5/2)/(d+e*x)^(5/2), x, 1, (2*(a*d*e+(c*d^2+a*e^2)*x +
	// c*d*e*x^2)^(7/2))/(7*c*d*(d+e*x)^(7/2))}
	public void test01327() {
		check("Integrate((a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(5/2)/(d+e*x)^(5/2), x)",
				"(2*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(7/2))/(7*c*d*(d+e*x)^(7/2))");

	}

	// {Sqrt(d+e*x)/Sqrt(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2), x, 1, (2*Sqrt(a*d*e+(c*d^2+a*e^2)*x +
	// c*d*e*x^2))/(c*d*Sqrt(d+e*x))}
	public void test01328() {
		check("Integrate(Sqrt(d+e*x)/Sqrt(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2), x)",
				"(2*Sqrt(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2))/(c*d*Sqrt(d+e*x))");

	}

	// {(d+e*x)^(3/2)/(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2), x, 1, (-2*Sqrt(d+e*x))/(c*d*Sqrt(a*d*e+(c*d^2
	// +a*e^2)*x+c*d*e*x^2))}
	public void test01329() {
		check("Integrate((d+e*x)^(3/2)/(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2), x)",
				"(-2*Sqrt(d+e*x))/(c*d*Sqrt(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2))");

	}

	// {(d+e*x)^(5/2)/(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(5/2), x, 1, (-2*(d+e*x)^(3/2))/(3*c*d*(a*d*e+(c*d^2
	// +a*e^2)*x+c*d*e*x^2)^(3/2))}
	public void test01330() {
		check("Integrate((d+e*x)^(5/2)/(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(5/2), x)",
				"(-2*(d+e*x)^(3/2))/(3*c*d*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2))");

	}

	// {(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^p, x, 1, -(((-((e*(a*e+c*d*x))/(c*d^2-a*e^2)))^(-1-p)*(a*d*e +
	// (c*d^2+a*e^2)*x+c*d*e*x^2)^(1+p)*Hypergeometric2F1(-p, 1+p, 2+p, (c*d*(d+e*x))/(c*d^2 -
	// a*e^2)))/((c*d^2-a*e^2)*(1+p)))}
	public void test01331() {
		check("Integrate((a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^p, x)",
				"-(((-((e*(a*e+c*d*x))/(c*d^2-a*e^2)))^(-1-p)*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(1+p)*Hypergeometric2F1(-p, 1+p, 2+p, (c*d*(d+e*x))/(c*d^2-a*e^2)))/((c*d^2-a*e^2)*(1+p)))");

	}

	// {(d+e*x)^(-2-2*p)*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^p, x, 1, (a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(1
	// +p)/((c*d^2-a*e^2)*(1+p)*(d+e*x)^(2*(1+p)))}
	public void test01332() {
		check("Integrate((d+e*x)^(-2-2*p)*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^p, x)",
				"(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(1+p)/((c*d^2-a*e^2)*(1+p)*(d+e*x)^(2*(1+p)))");

	}

	// {(d+e*x)^m/(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^m, x, 1, ((d+e*x)^(-1+m)*(a*d*e+(c*d^2+a*e^2)*x +
	// c*d*e*x^2)^(1-m))/(c*d*(1-m))}
	public void test01333() {
		check("Integrate((d+e*x)^m/(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^m, x)",
				"((d+e*x)^(-1+m)*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(1-m))/(c*d*(1-m))");

	}

	// {(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^p/(d+e*x)^p, x, 1, ((d+e*x)^(-1-p)*(a*d*e+(c*d^2+a*e^2)*x +
	// c*d*e*x^2)^(1+p))/(c*d*(1+p))}
	public void test01334() {
		check("Integrate((a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^p/(d+e*x)^p, x)",
				"((d+e*x)^(-1-p)*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(1+p))/(c*d*(1+p))");

	}

	// {a+b*x+c*x^2, x, 1, a*x+(b*x^2)/2+(c*x^3)/3}
	public void test01335() {
		check("Integrate(a+b*x+c*x^2, x)", "a*x+(b*x^2)/2+(c*x^3)/3");

	}

	// {(d+e*x)/(a+b*x+c*x^2)^(3/2), x, 1, (-2*(b*d-2*a*e+(2*c*d-b*e)*x))/((b^2-4*a*c)*Sqrt(a+b*x +
	// c*x^2))}
	public void test01336() {
		check("Integrate((d+e*x)/(a+b*x+c*x^2)^(3/2), x)",
				"(-2*(b*d-2*a*e+(2*c*d-b*e)*x))/((b^2-4*a*c)*Sqrt(a+b*x+c*x^2))");

	}

	// {(a+b*x+c*x^2)^(-3/2), x, 1, (-2*(b+2*c*x))/((b^2-4*a*c)*Sqrt(a+b*x+c*x^2))}
	public void test01337() {
		check("Integrate((a+b*x+c*x^2)^(-3/2), x)", "(-2*(b+2*c*x))/((b^2-4*a*c)*Sqrt(a+b*x+c*x^2))");

	}

	// {(1+x)/(2+3*x+x^2)^(3/2), x, 1, (2*(1+x))/Sqrt(2+3*x+x^2)}
	public void test01338() {
		check("Integrate((1+x)/(2+3*x+x^2)^(3/2), x)", "(2*(1+x))/Sqrt(2+3*x+x^2)");

	}

	// {1/((d+e*x)*Sqrt((-(c*d^2)+b*d*e)/e^2+b*x+c*x^2)), x, 1, (2*e*Sqrt(-((d*(c*d-b*e))/e^2)+b*x +
	// c*x^2))/((2*c*d-b*e)*(d+e*x))}
	public void test01339() {
		check("Integrate(1/((d+e*x)*Sqrt((-(c*d^2)+b*d*e)/e^2+b*x+c*x^2)), x)",
				"(2*e*Sqrt(-((d*(c*d-b*e))/e^2)+b*x+c*x^2))/((2*c*d-b*e)*(d+e*x))");

	}

	// {1/((d+e*x)*(c^2*d^2-b*c*d*e+b^2*e^2+3*b*c*e^2*x+3*c^2*e^2*x^2)^(1/3)), x, 1, -(ArcTan(1/Sqrt(3) +
	// (2*(c*d-b*e-c*e*x))/(Sqrt(3)*(2*c*d-b*e)^(1/3)*(c^2*d^2-b*c*d*e+b^2*e^2+3*b*c*e^2*x +
	// 3*c^2*e^2*x^2)^(1/3)))/(Sqrt(3)*e*(2*c*d-b*e)^(2/3)))-Log(d+e*x)/(2*e*(2*c*d-b*e)^(2/3)) +
	// Log(3*c*e^2*(c*d-b*e)-3*c^2*e^3*x-3*c*e^2*(2*c*d-b*e)^(1/3)*(c^2*d^2-b*c*d*e+b^2*e^2+3*b*c*e^2*x +
	// 3*c^2*e^2*x^2)^(1/3))/(2*e*(2*c*d-b*e)^(2/3))}
	public void test01340() {
		check("Integrate(1/((d+e*x)*(c^2*d^2-b*c*d*e+b^2*e^2+3*b*c*e^2*x+3*c^2*e^2*x^2)^(1/3)), x)",
				"-(ArcTan(1/Sqrt(3)+(2*(c*d-b*e-c*e*x))/(Sqrt(3)*(2*c*d-b*e)^(1/3)*(c^2*d^2-b*c*d*e+b^2*e^2+3*b*c*e^2*x+3*c^2*e^2*x^2)^(1/3)))/(Sqrt(3)*e*(2*c*d-b*e)^(2/3)))-Log(d+e*x)/(2*e*(2*c*d-b*e)^(2/3))+Log(3*c*e^2*(c*d-b*e)-3*c^2*e^3*x-3*c*e^2*(2*c*d-b*e)^(1/3)*(c^2*d^2-b*c*d*e+b^2*e^2+3*b*c*e^2*x+3*c^2*e^2*x^2)^(1/3))/(2*e*(2*c*d-b*e)^(2/3))");

	}

	// {1/((2+3*x)*(52-54*x+27*x^2)^(1/3)), x, 1, -ArcTan(1/Sqrt(3)+(2^(2/3)*(8-3*x))/(Sqrt(3)*5^(1/3)*(52 -
	// 54*x+27*x^2)^(1/3)))/(3*Sqrt(3)*10^(2/3))-Log(2+3*x)/(6*10^(2/3))+Log(216-81*x-27*10^(1/3)*(52-54*x
	// +27*x^2)^(1/3))/(6*10^(2/3))}
	public void test01341() {
		check("Integrate(1/((2+3*x)*(52-54*x+27*x^2)^(1/3)), x)",
				"-ArcTan(1/Sqrt(3)+(2^(2/3)*(8-3*x))/(Sqrt(3)*5^(1/3)*(52-54*x+27*x^2)^(1/3)))/(3*Sqrt(3)*10^(2/3))-Log(2+3*x)/(6*10^(2/3))+Log(216-81*x-27*10^(1/3)*(52-54*x+27*x^2)^(1/3))/(6*10^(2/3))");

	}

	// {1/((2+3*x)*(28+54*x+27*x^2)^(1/3)), x, 1, -ArcTan(1/Sqrt(3)+(2^(2/3)*(4+3*x))/(Sqrt(3)*(28+54*x +
	// 27*x^2)^(1/3)))/(3*2^(2/3)*Sqrt(3))-Log(2+3*x)/(6*2^(2/3))+Log(-108-81*x+27*2^(1/3)*(28+54*x +
	// 27*x^2)^(1/3))/(6*2^(2/3))}
	public void test01342() {
		check("Integrate(1/((2+3*x)*(28+54*x+27*x^2)^(1/3)), x)",
				"-ArcTan(1/Sqrt(3)+(2^(2/3)*(4+3*x))/(Sqrt(3)*(28+54*x+27*x^2)^(1/3)))/(3*2^(2/3)*Sqrt(3))-Log(2+3*x)/(6*2^(2/3))+Log(-108-81*x+27*2^(1/3)*(28+54*x+27*x^2)^(1/3))/(6*2^(2/3))");

	}

	// {1/((d+e*x)^(3/2)*(a+b*x+c*x^2)^(1/4)), x, 1, (2*(b-Sqrt(b^2-4*a*c)+2*c*x)*(((2*c*d-(b-Sqrt(b^2 -
	// 4*a*c))*e)*(b+Sqrt(b^2-4*a*c)+2*c*x))/((2*c*d-(b+Sqrt(b^2-4*a*c))*e)*(b-Sqrt(b^2-4*a*c) +
	// 2*c*x)))^(1/4)*Hypergeometric2F1(-1/2, 1/4, 1/2, (-4*c*Sqrt(b^2-4*a*c)*(d+e*x))/((2*c*d-(b+Sqrt(b^2 -
	// 4*a*c))*e)*(b-Sqrt(b^2-4*a*c)+2*c*x))))/((2*c*d-b*e+Sqrt(b^2-4*a*c)*e)*Sqrt(d+e*x)*(a+b*x +
	// c*x^2)^(1/4))}
	public void test01343() {
		check("Integrate(1/((d+e*x)^(3/2)*(a+b*x+c*x^2)^(1/4)), x)",
				"(2*(b-Sqrt(b^2-4*a*c)+2*c*x)*(((2*c*d-(b-Sqrt(b^2-4*a*c))*e)*(b+Sqrt(b^2-4*a*c)+2*c*x))/((2*c*d-(b+Sqrt(b^2-4*a*c))*e)*(b-Sqrt(b^2-4*a*c)+2*c*x)))^(1/4)*Hypergeometric2F1(-1/2, 1/4, 1/2, (-4*c*Sqrt(b^2-4*a*c)*(d+e*x))/((2*c*d-(b+Sqrt(b^2-4*a*c))*e)*(b-Sqrt(b^2-4*a*c)+2*c*x))))/((2*c*d-b*e+Sqrt(b^2-4*a*c)*e)*Sqrt(d+e*x)*(a+b*x+c*x^2)^(1/4))");

	}

	// {(a+b*x+c*x^2)^p, x, 1, -((2^(1+p)*(-((b-Sqrt(b^2-4*a*c)+2*c*x)/Sqrt(b^2-4*a*c)))^(-1-p)*(a+b*x
	// +c*x^2)^(1+p)*Hypergeometric2F1(-p, 1+p, 2+p, (b+Sqrt(b^2-4*a*c)+2*c*x)/(2*Sqrt(b^2 -
	// 4*a*c))))/(Sqrt(b^2-4*a*c)*(1+p)))}
	public void test01344() {
		check("Integrate((a+b*x+c*x^2)^p, x)",
				"-((2^(1+p)*(-((b-Sqrt(b^2-4*a*c)+2*c*x)/Sqrt(b^2-4*a*c)))^(-1-p)*(a+b*x+c*x^2)^(1+p)*Hypergeometric2F1(-p, 1+p, 2+p, (b+Sqrt(b^2-4*a*c)+2*c*x)/(2*Sqrt(b^2-4*a*c))))/(Sqrt(b^2-4*a*c)*(1+p)))");

	}

	// {(d+e*x)^(-2-2*p)*(a+b*x+c*x^2)^p, x, 1, ((b-Sqrt(b^2-4*a*c)+2*c*x)*(d+e*x)^(-1-2*p)*(a+b*x +
	// c*x^2)^p*Hypergeometric2F1(-1-2*p, -p, -2*p, (-4*c*Sqrt(b^2-4*a*c)*(d+e*x))/((2*c*d-(b+Sqrt(b^2 -
	// 4*a*c))*e)*(b-Sqrt(b^2-4*a*c)+2*c*x))))/((2*c*d-(b-Sqrt(b^2-4*a*c))*e)*(1+2*p)*(((2*c*d-(b -
	// Sqrt(b^2-4*a*c))*e)*(b+Sqrt(b^2-4*a*c)+2*c*x))/((2*c*d-(b+Sqrt(b^2-4*a*c))*e)*(b-Sqrt(b^2 -
	// 4*a*c)+2*c*x)))^p)}
	public void test01345() {
		check("Integrate((d+e*x)^(-2-2*p)*(a+b*x+c*x^2)^p, x)",
				"((b-Sqrt(b^2-4*a*c)+2*c*x)*(d+e*x)^(-1-2*p)*(a+b*x+c*x^2)^p*Hypergeometric2F1(-1-2*p, -p, -2*p, (-4*c*Sqrt(b^2-4*a*c)*(d+e*x))/((2*c*d-(b+Sqrt(b^2-4*a*c))*e)*(b-Sqrt(b^2-4*a*c)+2*c*x))))/((2*c*d-(b-Sqrt(b^2-4*a*c))*e)*(1+2*p)*(((2*c*d-(b-Sqrt(b^2-4*a*c))*e)*(b+Sqrt(b^2-4*a*c)+2*c*x))/((2*c*d-(b+Sqrt(b^2-4*a*c))*e)*(b-Sqrt(b^2-4*a*c)+2*c*x)))^p)");

	}

	// {(A+B*x)/(b*x+c*x^2)^(3/2), x, 1, (-2*(A*b-(b*B-2*A*c)*x))/(b^2*Sqrt(b*x+c*x^2))}
	public void test01346() {
		check("Integrate((A+B*x)/(b*x+c*x^2)^(3/2), x)", "(-2*(A*b-(b*B-2*A*c)*x))/(b^2*Sqrt(b*x+c*x^2))");

	}

	// {x^(1+p)*(2*b+3*c*x)*(b*x+c*x^2)^p, x, 1, (x^(1+p)*(b*x+c*x^2)^(1+p))/(1+p)}
	public void test01347() {
		check("Integrate(x^(1+p)*(2*b+3*c*x)*(b*x+c*x^2)^p, x)", "(x^(1+p)*(b*x+c*x^2)^(1+p))/(1+p)");

	}

	// {(A+B*x)/(a+c*x^2)^(3/2), x, 1, -((a*B-A*c*x)/(a*c*Sqrt(a+c*x^2)))}
	public void test01348() {
		check("Integrate((A+B*x)/(a+c*x^2)^(3/2), x)", "-((a*B-A*c*x)/(a*c*Sqrt(a+c*x^2)))");

	}

	// {(5+2*x)/(4+5*x+x^2), x, 1, Log(4+5*x+x^2)}
	public void test01349() {
		check("Integrate((5+2*x)/(4+5*x+x^2), x)", "Log(4+5*x+x^2)");

	}

	// {(3+2*x)/(13+12*x+4*x^2)^2, x, 1, -1/(4*(13+12*x+4*x^2))}
	public void test01350() {
		check("Integrate((3+2*x)/(13+12*x+4*x^2)^2, x)", "-1/(4*(13+12*x+4*x^2))");

	}

	// {(A+B*x)^(-1), x, 1, Log(A+B*x)/B}
	public void test01351() {
		check("Integrate((A+B*x)^(-1), x)", "Log(A+B*x)/B");

	}

	// {(A+B*x)/(a+b*x+c*x^2)^(3/2), x, 1, (-2*(A*b-2*a*B-(b*B-2*A*c)*x))/((b^2-4*a*c)*Sqrt(a+b*x +
	// c*x^2))}
	public void test01352() {
		check("Integrate((A+B*x)/(a+b*x+c*x^2)^(3/2), x)",
				"(-2*(A*b-2*a*B-(b*B-2*A*c)*x))/((b^2-4*a*c)*Sqrt(a+b*x+c*x^2))");

	}

	// {(A+B*x)/(b*x+c*x^2)^(3/2), x, 1, (-2*(A*b-(b*B-2*A*c)*x))/(b^2*Sqrt(b*x+c*x^2))}
	public void test01353() {
		check("Integrate((A+B*x)/(b*x+c*x^2)^(3/2), x)", "(-2*(A*b-(b*B-2*A*c)*x))/(b^2*Sqrt(b*x+c*x^2))");

	}

	// {(5-x)/(2+3*x^2)^(3/2), x, 1, (2+15*x)/(6*Sqrt(2+3*x^2))}
	public void test01354() {
		check("Integrate((5-x)/(2+3*x^2)^(3/2), x)", "(2+15*x)/(6*Sqrt(2+3*x^2))");

	}

	// {(-(a*e)+c*d*x)*(d+e*x)^(-3-2*p)*(a+c*x^2)^p, x, 1, (a+c*x^2)^(1+p)/(2*(1+p)*(d+e*x)^(2*(1 +
	// p)))}
	public void test01355() {
		check("Integrate((-(a*e)+c*d*x)*(d+e*x)^(-3-2*p)*(a+c*x^2)^p, x)",
				"(a+c*x^2)^(1+p)/(2*(1+p)*(d+e*x)^(2*(1+p)))");

	}

	// {(b+2*c*x)*(a+b*x+c*x^2), x, 1, (a+b*x+c*x^2)^2/2}
	public void test01356() {
		check("Integrate((b+2*c*x)*(a+b*x+c*x^2), x)", "(a+b*x+c*x^2)^2/2");

	}

	// {(b+2*c*x)*(a+b*x+c*x^2)^2, x, 1, (a+b*x+c*x^2)^3/3}
	public void test01357() {
		check("Integrate((b+2*c*x)*(a+b*x+c*x^2)^2, x)", "(a+b*x+c*x^2)^3/3");

	}

	// {(b+2*c*x)*(a+b*x+c*x^2)^3, x, 1, (a+b*x+c*x^2)^4/4}
	public void test01358() {
		check("Integrate((b+2*c*x)*(a+b*x+c*x^2)^3, x)", "(a+b*x+c*x^2)^4/4");

	}

	// {(b+2*c*x)/(a+b*x+c*x^2), x, 1, Log(a+b*x+c*x^2)}
	public void test01359() {
		check("Integrate((b+2*c*x)/(a+b*x+c*x^2), x)", "Log(a+b*x+c*x^2)");

	}

	// {(b+2*c*x)/(a+b*x+c*x^2)^2, x, 1, -(a+b*x+c*x^2)^(-1)}
	public void test01360() {
		check("Integrate((b+2*c*x)/(a+b*x+c*x^2)^2, x)", "-(a+b*x+c*x^2)^(-1)");

	}

	// {(b+2*c*x)/(a+b*x+c*x^2)^3, x, 1, -1/(2*(a+b*x+c*x^2)^2)}
	public void test01361() {
		check("Integrate((b+2*c*x)/(a+b*x+c*x^2)^3, x)", "-1/(2*(a+b*x+c*x^2)^2)");

	}

	// {(b+2*c*x)*Sqrt(a+b*x+c*x^2), x, 1, (2*(a+b*x+c*x^2)^(3/2))/3}
	public void test01362() {
		check("Integrate((b+2*c*x)*Sqrt(a+b*x+c*x^2), x)", "(2*(a+b*x+c*x^2)^(3/2))/3");

	}

	// {(b+2*c*x)*(a+b*x+c*x^2)^(3/2), x, 1, (2*(a+b*x+c*x^2)^(5/2))/5}
	public void test01363() {
		check("Integrate((b+2*c*x)*(a+b*x+c*x^2)^(3/2), x)", "(2*(a+b*x+c*x^2)^(5/2))/5");

	}

	// {(b+2*c*x)*(a+b*x+c*x^2)^(5/2), x, 1, (2*(a+b*x+c*x^2)^(7/2))/7}
	public void test01364() {
		check("Integrate((b+2*c*x)*(a+b*x+c*x^2)^(5/2), x)", "(2*(a+b*x+c*x^2)^(7/2))/7");

	}

	// {(b+2*c*x)/Sqrt(a+b*x+c*x^2), x, 1, 2*Sqrt(a+b*x+c*x^2)}
	public void test01365() {
		check("Integrate((b+2*c*x)/Sqrt(a+b*x+c*x^2), x)", "2*Sqrt(a+b*x+c*x^2)");

	}

	// {(b+2*c*x)/(a+b*x+c*x^2)^(3/2), x, 1, -2/Sqrt(a+b*x+c*x^2)}
	public void test01366() {
		check("Integrate((b+2*c*x)/(a+b*x+c*x^2)^(3/2), x)", "-2/Sqrt(a+b*x+c*x^2)");

	}

	// {(b+2*c*x)/(a+b*x+c*x^2)^(5/2), x, 1, -2/(3*(a+b*x+c*x^2)^(3/2))}
	public void test01367() {
		check("Integrate((b+2*c*x)/(a+b*x+c*x^2)^(5/2), x)", "-2/(3*(a+b*x+c*x^2)^(3/2))");

	}

	// {(a+b*x)*Sqrt(a^2+2*a*b*x+b^2*x^2), x, 1, (a^2+2*a*b*x+b^2*x^2)^(3/2)/(3*b)}
	public void test01368() {
		check("Integrate((a+b*x)*Sqrt(a^2+2*a*b*x+b^2*x^2), x)", "(a^2+2*a*b*x+b^2*x^2)^(3/2)/(3*b)");

	}

	// {((a+b*x)*Sqrt(a^2+2*a*b*x+b^2*x^2))/(d+e*x)^4, x, 1, (a^2+2*a*b*x+b^2*x^2)^(3/2)/(3*(b*d-a*e)*(d +
	// e*x)^3)}
	public void test01369() {
		check("Integrate(((a+b*x)*Sqrt(a^2+2*a*b*x+b^2*x^2))/(d+e*x)^4, x)",
				"(a^2+2*a*b*x+b^2*x^2)^(3/2)/(3*(b*d-a*e)*(d+e*x)^3)");

	}

	// {(a+b*x)*(a^2+2*a*b*x+b^2*x^2)^(3/2), x, 1, (a^2+2*a*b*x+b^2*x^2)^(5/2)/(5*b)}
	public void test01370() {
		check("Integrate((a+b*x)*(a^2+2*a*b*x+b^2*x^2)^(3/2), x)", "(a^2+2*a*b*x+b^2*x^2)^(5/2)/(5*b)");

	}

	// {((a+b*x)*(a^2+2*a*b*x+b^2*x^2)^(3/2))/(d+e*x)^6, x, 1, (a^2+2*a*b*x+b^2*x^2)^(5/2)/(5*(b*d-a*e)*(d
	// +e*x)^5)}
	public void test01371() {
		check("Integrate(((a+b*x)*(a^2+2*a*b*x+b^2*x^2)^(3/2))/(d+e*x)^6, x)",
				"(a^2+2*a*b*x+b^2*x^2)^(5/2)/(5*(b*d-a*e)*(d+e*x)^5)");

	}

	// {(a+b*x)*(a^2+2*a*b*x+b^2*x^2)^(5/2), x, 1, (a^2+2*a*b*x+b^2*x^2)^(7/2)/(7*b)}
	public void test01372() {
		check("Integrate((a+b*x)*(a^2+2*a*b*x+b^2*x^2)^(5/2), x)", "(a^2+2*a*b*x+b^2*x^2)^(7/2)/(7*b)");

	}

	// {((a+b*x)*(a^2+2*a*b*x+b^2*x^2)^(5/2))/(d+e*x)^8, x, 1, (a^2+2*a*b*x+b^2*x^2)^(7/2)/(7*(b*d-a*e)*(d
	// +e*x)^7)}
	public void test01373() {
		check("Integrate(((a+b*x)*(a^2+2*a*b*x+b^2*x^2)^(5/2))/(d+e*x)^8, x)",
				"(a^2+2*a*b*x+b^2*x^2)^(7/2)/(7*(b*d-a*e)*(d+e*x)^7)");

	}

	// {(a+b*x)/Sqrt(a^2+2*a*b*x+b^2*x^2), x, 1, Sqrt(a^2+2*a*b*x+b^2*x^2)/b}
	public void test01374() {
		check("Integrate((a+b*x)/Sqrt(a^2+2*a*b*x+b^2*x^2), x)", "Sqrt(a^2+2*a*b*x+b^2*x^2)/b");

	}

	// {(a+b*x)/((d+e*x)^2*Sqrt(a^2+2*a*b*x+b^2*x^2)), x, 1, Sqrt(a^2+2*a*b*x+b^2*x^2)/((b*d-a*e)*(d +
	// e*x))}
	public void test01375() {
		check("Integrate((a+b*x)/((d+e*x)^2*Sqrt(a^2+2*a*b*x+b^2*x^2)), x)",
				"Sqrt(a^2+2*a*b*x+b^2*x^2)/((b*d-a*e)*(d+e*x))");

	}

	// {(a+b*x)/(a^2+2*a*b*x+b^2*x^2)^(3/2), x, 1, -(1/(b*Sqrt(a^2+2*a*b*x+b^2*x^2)))}
	public void test01376() {
		check("Integrate((a+b*x)/(a^2+2*a*b*x+b^2*x^2)^(3/2), x)", "-(1/(b*Sqrt(a^2+2*a*b*x+b^2*x^2)))");

	}

	// {((a+b*x)*(d+e*x)^2)/(a^2+2*a*b*x+b^2*x^2)^(5/2), x, 1, -(d+e*x)^3/(3*(b*d-a*e)*(a^2+2*a*b*x +
	// b^2*x^2)^(3/2))}
	public void test01377() {
		check("Integrate(((a+b*x)*(d+e*x)^2)/(a^2+2*a*b*x+b^2*x^2)^(5/2), x)",
				"-(d+e*x)^3/(3*(b*d-a*e)*(a^2+2*a*b*x+b^2*x^2)^(3/2))");

	}

	// {(a+b*x)/(a^2+2*a*b*x+b^2*x^2)^(5/2), x, 1, -1/(3*b*(a^2+2*a*b*x+b^2*x^2)^(3/2))}
	public void test01378() {
		check("Integrate((a+b*x)/(a^2+2*a*b*x+b^2*x^2)^(5/2), x)", "-1/(3*b*(a^2+2*a*b*x+b^2*x^2)^(3/2))");

	}

	// {(a*c+b*c*x)*(d+e*x)^(-3-2*p)*(a^2+2*a*b*x+b^2*x^2)^p, x, 1, (c*(a^2+2*a*b*x+b^2*x^2)^(1 +
	// p))/(2*(b*d-a*e)*(1+p)*(d+e*x)^(2*(1+p)))}
	public void test01379() {
		check("Integrate((a*c+b*c*x)*(d+e*x)^(-3-2*p)*(a^2+2*a*b*x+b^2*x^2)^p, x)",
				"(c*(a^2+2*a*b*x+b^2*x^2)^(1+p))/(2*(b*d-a*e)*(1+p)*(d+e*x)^(2*(1+p)))");

	}

	// {(a+b*x)*(a^2+2*a*b*x+b^2*x^2)^p, x, 1, (a^2+2*a*b*x+b^2*x^2)^(1+p)/(2*b*(1+p))}
	public void test01380() {
		check("Integrate((a+b*x)*(a^2+2*a*b*x+b^2*x^2)^p, x)", "(a^2+2*a*b*x+b^2*x^2)^(1+p)/(2*b*(1+p))");

	}

	// {(d+e*x)^m*(c*d*m-b*e*(1+m+p)-c*e*(2+m+2*p)*x)*(c*d^2-b*d*e-b*e^2*x-c*e^2*x^2)^p, x, 1, ((d +
	// e*x)^m*(d*(c*d-b*e)-b*e^2*x-c*e^2*x^2)^(1+p))/e}
	public void test01381() {
		check("Integrate((d+e*x)^m*(c*d*m-b*e*(1+m+p)-c*e*(2+m+2*p)*x)*(c*d^2-b*d*e-b*e^2*x-c*e^2*x^2)^p, x)",
				"((d+e*x)^m*(d*(c*d-b*e)-b*e^2*x-c*e^2*x^2)^(1+p))/e");

	}

	// {(d+e*x)^(-3-2*p)*(f+g*x)*(d*(e*f+d*g+d*g*p)+e*(e*f+3*d*g+2*d*g*p)*x+e^2*g*(2+p)*x^2)^p, x,
	// 1, -(((d+e*x)^(-3-2*p)*(d*(e*f+d*g*(1+p))+e*(e*f+d*g*(3+2*p))*x+e^2*g*(2+p)*x^2)^(1 +
	// p))/(e^2*(2+p)))}
	public void test01382() {
		check("Integrate((d+e*x)^(-3-2*p)*(f+g*x)*(d*(e*f+d*g+d*g*p)+e*(e*f+3*d*g+2*d*g*p)*x+e^2*g*(2+p)*x^2)^p, x)",
				"-(((d+e*x)^(-3-2*p)*(d*(e*f+d*g*(1+p))+e*(e*f+d*g*(3+2*p))*x+e^2*g*(2+p)*x^2)^(1+p))/(e^2*(2+p)))");

	}

	// {(A+B*x)/(a+b*x+c*x^2)^(3/2), x, 1, (-2*(A*b-2*a*B-(b*B-2*A*c)*x))/((b^2-4*a*c)*Sqrt(a+b*x +
	// c*x^2))}
	public void test01383() {
		check("Integrate((A+B*x)/(a+b*x+c*x^2)^(3/2), x)",
				"(-2*(A*b-2*a*B-(b*B-2*A*c)*x))/((b^2-4*a*c)*Sqrt(a+b*x+c*x^2))");

	}

	// {(5-x)/(2+5*x+3*x^2)^(3/2), x, 1, (-2*(29+35*x))/Sqrt(2+5*x+3*x^2)}
	public void test01384() {
		check("Integrate((5-x)/(2+5*x+3*x^2)^(3/2), x)", "(-2*(29+35*x))/Sqrt(2+5*x+3*x^2)");

	}

	// {1/((d+e*x)*Sqrt(d^2-e^2*x^2)), x, 1, -(Sqrt(d^2-e^2*x^2)/(d*e*(d+e*x)))}
	public void test01385() {
		check("Integrate(1/((d+e*x)*Sqrt(d^2-e^2*x^2)), x)", "-(Sqrt(d^2-e^2*x^2)/(d*e*(d+e*x)))");

	}

	// {1/((1+a*x)*Sqrt(1-a^2*x^2)), x, 1, -(Sqrt(1-a^2*x^2)/(a*(1+a*x)))}
	public void test01386() {
		check("Integrate(1/((1+a*x)*Sqrt(1-a^2*x^2)), x)", "-(Sqrt(1-a^2*x^2)/(a*(1+a*x)))");

	}

	// {1/((d+e*x)*Sqrt(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)), x, 1, (2*Sqrt(a*d*e+(c*d^2+a*e^2)*x +
	// c*d*e*x^2))/((c*d^2-a*e^2)*(d+e*x))}
	public void test01387() {
		check("Integrate(1/((d+e*x)*Sqrt(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)), x)",
				"(2*Sqrt(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2))/((c*d^2-a*e^2)*(d+e*x))");

	}

	// {x^2*Sqrt(1+x)*Sqrt(1-x+x^2), x, 1, (2*(1+x)^(3/2)*(1-x+x^2)^(3/2))/9}
	public void test01388() {
		check("Integrate(x^2*Sqrt(1+x)*Sqrt(1-x+x^2), x)", "(2*(1+x)^(3/2)*(1-x+x^2)^(3/2))/9");

	}

	// {x^2*(1+x)^(3/2)*(1-x+x^2)^(3/2), x, 1, (2*(1+x)^(5/2)*(1-x+x^2)^(5/2))/15}
	public void test01389() {
		check("Integrate(x^2*(1+x)^(3/2)*(1-x+x^2)^(3/2), x)", "(2*(1+x)^(5/2)*(1-x+x^2)^(5/2))/15");

	}

	// {x^2/(Sqrt(1+x)*Sqrt(1-x+x^2)), x, 1, (2*Sqrt(1+x)*Sqrt(1-x+x^2))/3}
	public void test01390() {
		check("Integrate(x^2/(Sqrt(1+x)*Sqrt(1-x+x^2)), x)", "(2*Sqrt(1+x)*Sqrt(1-x+x^2))/3");

	}

	// {x^2/((1+x)^(3/2)*(1-x+x^2)^(3/2)), x, 1, -2/(3*Sqrt(1+x)*Sqrt(1-x+x^2))}
	public void test01391() {
		check("Integrate(x^2/((1+x)^(3/2)*(1-x+x^2)^(3/2)), x)", "-2/(3*Sqrt(1+x)*Sqrt(1-x+x^2))");

	}

	// {x^2/((1+x)^(5/2)*(1-x+x^2)^(5/2)), x, 1, -2/(9*(1+x)^(3/2)*(1-x+x^2)^(3/2))}
	public void test01392() {
		check("Integrate(x^2/((1+x)^(5/2)*(1-x+x^2)^(5/2)), x)", "-2/(9*(1+x)^(3/2)*(1-x+x^2)^(3/2))");

	}

	// {(-1+2*x^2)/(Sqrt(-1+x)*Sqrt(1+x)), x, 1, Sqrt(-1+x)*x*Sqrt(1+x)}
	public void test01393() {
		check("Integrate((-1+2*x^2)/(Sqrt(-1+x)*Sqrt(1+x)), x)", "Sqrt(-1+x)*x*Sqrt(1+x)");

	}

	// {Sqrt(d+e*x)/Sqrt(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2), x, 1, (2*Sqrt(a*d*e+(c*d^2+a*e^2)*x +
	// c*d*e*x^2))/(c*d*Sqrt(d+e*x))}
	public void test01394() {
		check("Integrate(Sqrt(d+e*x)/Sqrt(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2), x)",
				"(2*Sqrt(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2))/(c*d*Sqrt(d+e*x))");

	}

	// {(d+e*x)^(3/2)/(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2), x, 1, (-2*Sqrt(d+e*x))/(c*d*Sqrt(a*d*e+(c*d^2
	// +a*e^2)*x+c*d*e*x^2))}
	public void test01395() {
		check("Integrate((d+e*x)^(3/2)/(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2), x)",
				"(-2*Sqrt(d+e*x))/(c*d*Sqrt(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2))");

	}

	// {(d+e*x)^(5/2)/(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(5/2), x, 1, (-2*(d+e*x)^(3/2))/(3*c*d*(a*d*e+(c*d^2
	// +a*e^2)*x+c*d*e*x^2)^(3/2))}
	public void test01396() {
		check("Integrate((d+e*x)^(5/2)/(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(5/2), x)",
				"(-2*(d+e*x)^(3/2))/(3*c*d*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2))");

	}

	// {Sqrt(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)/Sqrt(d+e*x), x, 1, (2*(a*d*e+(c*d^2+a*e^2)*x +
	// c*d*e*x^2)^(3/2))/(3*c*d*(d+e*x)^(3/2))}
	public void test01397() {
		check("Integrate(Sqrt(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)/Sqrt(d+e*x), x)",
				"(2*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2))/(3*c*d*(d+e*x)^(3/2))");

	}

	// {(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2)/(d+e*x)^(3/2), x, 1, (2*(a*d*e+(c*d^2+a*e^2)*x +
	// c*d*e*x^2)^(5/2))/(5*c*d*(d+e*x)^(5/2))}
	public void test01398() {
		check("Integrate((a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2)/(d+e*x)^(3/2), x)",
				"(2*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(5/2))/(5*c*d*(d+e*x)^(5/2))");

	}

	// {(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(5/2)/(d+e*x)^(5/2), x, 1, (2*(a*d*e+(c*d^2+a*e^2)*x +
	// c*d*e*x^2)^(7/2))/(7*c*d*(d+e*x)^(7/2))}
	public void test01399() {
		check("Integrate((a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(5/2)/(d+e*x)^(5/2), x)",
				"(2*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(7/2))/(7*c*d*(d+e*x)^(7/2))");

	}

	// {Sqrt(d+e*x)/((f+g*x)^(3/2)*Sqrt(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)), x, 1, (2*Sqrt(a*d*e+(c*d^2 +
	// a*e^2)*x+c*d*e*x^2))/((c*d*f-a*e*g)*Sqrt(d+e*x)*Sqrt(f+g*x))}
	public void test01400() {
		check("Integrate(Sqrt(d+e*x)/((f+g*x)^(3/2)*Sqrt(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)), x)",
				"(2*Sqrt(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2))/((c*d*f-a*e*g)*Sqrt(d+e*x)*Sqrt(f+g*x))");

	}

	// {(d+e*x)^(3/2)/(Sqrt(f+g*x)*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2)), x, 1, (-2*Sqrt(d+e*x)*Sqrt(f +
	// g*x))/((c*d*f-a*e*g)*Sqrt(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2))}
	public void test01401() {
		check("Integrate((d+e*x)^(3/2)/(Sqrt(f+g*x)*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2)), x)",
				"(-2*Sqrt(d+e*x)*Sqrt(f+g*x))/((c*d*f-a*e*g)*Sqrt(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2))");

	}

	// {((d+e*x)^(5/2)*Sqrt(f+g*x))/(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(5/2), x, 1, (-2*(d+e*x)^(3/2)*(f +
	// g*x)^(3/2))/(3*(c*d*f-a*e*g)*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2))}
	public void test01402() {
		check("Integrate(((d+e*x)^(5/2)*Sqrt(f+g*x))/(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(5/2), x)",
				"(-2*(d+e*x)^(3/2)*(f+g*x)^(3/2))/(3*(c*d*f-a*e*g)*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2))");

	}

	// {Sqrt(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)/(Sqrt(d+e*x)*(f+g*x)^(5/2)), x, 1, (2*(a*d*e+(c*d^2+a*e^2)*x
	// +c*d*e*x^2)^(3/2))/(3*(c*d*f-a*e*g)*(d+e*x)^(3/2)*(f+g*x)^(3/2))}
	public void test01403() {
		check("Integrate(Sqrt(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)/(Sqrt(d+e*x)*(f+g*x)^(5/2)), x)",
				"(2*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2))/(3*(c*d*f-a*e*g)*(d+e*x)^(3/2)*(f+g*x)^(3/2))");

	}

	// {(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2)/((d+e*x)^(3/2)*(f+g*x)^(7/2)), x, 1, (2*(a*d*e+(c*d^2 +
	// a*e^2)*x+c*d*e*x^2)^(5/2))/(5*(c*d*f-a*e*g)*(d+e*x)^(5/2)*(f+g*x)^(5/2))}
	public void test01404() {
		check("Integrate((a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2)/((d+e*x)^(3/2)*(f+g*x)^(7/2)), x)",
				"(2*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(5/2))/(5*(c*d*f-a*e*g)*(d+e*x)^(5/2)*(f+g*x)^(5/2))");

	}

	// {(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(5/2)/((d+e*x)^(5/2)*(f+g*x)^(9/2)), x, 1, (2*(a*d*e+(c*d^2 +
	// a*e^2)*x+c*d*e*x^2)^(7/2))/(7*(c*d*f-a*e*g)*(d+e*x)^(7/2)*(f+g*x)^(7/2))}
	public void test01405() {
		check("Integrate((a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(5/2)/((d+e*x)^(5/2)*(f+g*x)^(9/2)), x)",
				"(2*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(7/2))/(7*(c*d*f-a*e*g)*(d+e*x)^(7/2)*(f+g*x)^(7/2))");

	}

	// {(d+e*x)^m/(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^m, x, 1, ((d+e*x)^(-1+m)*(a*d*e+(c*d^2+a*e^2)*x +
	// c*d*e*x^2)^(1-m))/(c*d*(1-m))}
	public void test01406() {
		check("Integrate((d+e*x)^m/(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^m, x)",
				"((d+e*x)^(-1+m)*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(1-m))/(c*d*(1-m))");

	}

	// {((a*e+c*d*x)^n*(d+e*x)^m)/(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^m, x, 1, ((a*e+c*d*x)^n*(d+e*x)^(-1 +
	// m)*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(1-m))/(c*d*(1-m+n))}
	public void test01407() {
		check("Integrate(((a*e+c*d*x)^n*(d+e*x)^m)/(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^m, x)",
				"((a*e+c*d*x)^n*(d+e*x)^(-1+m)*(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(1-m))/(c*d*(1-m+n))");

	}

	// {Sqrt(d+e*x)/(Sqrt(f+g*x)*Sqrt(a+b*x+c*x^2)), x, 1, (Sqrt(2)*Sqrt(2*c*f-(b+Sqrt(b^2 -
	// 4*a*c))*g)*Sqrt(b-Sqrt(b^2-4*a*c)+2*c*x)*Sqrt(((e*f-d*g)*(b+Sqrt(b^2-4*a*c)+2*c*x))/((2*c*f-(b +
	// Sqrt(b^2-4*a*c))*g)*(d+e*x)))*Sqrt(((e*f-d*g)*(2*a+(b+Sqrt(b^2-4*a*c))*x))/((b*f+Sqrt(b^2 -
	// 4*a*c)*f-2*a*g)*(d+e*x)))*(d+e*x)*EllipticPi((e*(2*c*f-(b+Sqrt(b^2-4*a*c))*g))/((2*c*d-(b +
	// Sqrt(b^2-4*a*c))*e)*g), ArcSin((Sqrt(2*c*d-(b+Sqrt(b^2-4*a*c))*e)*Sqrt(f+g*x))/(Sqrt(2*c*f-(b +
	// Sqrt(b^2-4*a*c))*g)*Sqrt(d+e*x))), ((b*d+Sqrt(b^2-4*a*c)*d-2*a*e)*(2*c*f-(b+Sqrt(b^2 -
	// 4*a*c))*g))/((2*c*d-(b+Sqrt(b^2-4*a*c))*e)*(b*f+Sqrt(b^2-4*a*c)*f-2*a*g))))/(Sqrt(2*c*d-(b +
	// Sqrt(b^2-4*a*c))*e)*g*Sqrt((2*a*c)/(b+Sqrt(b^2-4*a*c))+c*x)*Sqrt(a+b*x+c*x^2))}
	public void test01408() {
		check("Integrate(Sqrt(d+e*x)/(Sqrt(f+g*x)*Sqrt(a+b*x+c*x^2)), x)",
				"(Sqrt(2)*Sqrt(2*c*f-(b+Sqrt(b^2-4*a*c))*g)*Sqrt(b-Sqrt(b^2-4*a*c)+2*c*x)*Sqrt(((e*f-d*g)*(b+Sqrt(b^2-4*a*c)+2*c*x))/((2*c*f-(b+Sqrt(b^2-4*a*c))*g)*(d+e*x)))*Sqrt(((e*f-d*g)*(2*a+(b+Sqrt(b^2-4*a*c))*x))/((b*f+Sqrt(b^2-4*a*c)*f-2*a*g)*(d+e*x)))*(d+e*x)*EllipticPi((e*(2*c*f-(b+Sqrt(b^2-4*a*c))*g))/((2*c*d-(b+Sqrt(b^2-4*a*c))*e)*g), ArcSin((Sqrt(2*c*d-(b+Sqrt(b^2-4*a*c))*e)*Sqrt(f+g*x))/(Sqrt(2*c*f-(b+Sqrt(b^2-4*a*c))*g)*Sqrt(d+e*x))), ((b*d+Sqrt(b^2-4*a*c)*d-2*a*e)*(2*c*f-(b+Sqrt(b^2-4*a*c))*g))/((2*c*d-(b+Sqrt(b^2-4*a*c))*e)*(b*f+Sqrt(b^2-4*a*c)*f-2*a*g))))/(Sqrt(2*c*d-(b+Sqrt(b^2-4*a*c))*e)*g*Sqrt((2*a*c)/(b+Sqrt(b^2-4*a*c))+c*x)*Sqrt(a+b*x+c*x^2))");

	}

	// {(-3+2*x)*(-3*x+x^2)^(2/3), x, 1, (3*(-3*x+x^2)^(5/3))/5}
	public void test01409() {
		check("Integrate((-3+2*x)*(-3*x+x^2)^(2/3), x)", "(3*(-3*x+x^2)^(5/3))/5");

	}

	// {((-3+x)*x)^(2/3)*(-3+2*x), x, 1, (3*(-((3-x)*x))^(5/3))/5}
	public void test01410() {
		check("Integrate(((-3+x)*x)^(2/3)*(-3+2*x), x)", "(3*(-((3-x)*x))^(5/3))/5");

	}

	// {((a+b*x^2)*(-(a*d)+4*b*c*x+3*b*d*x^2))/(c+d*x)^2, x, 1, (a+b*x^2)^2/(c+d*x)}
	public void test01411() {
		check("Integrate(((a+b*x^2)*(-(a*d)+4*b*c*x+3*b*d*x^2))/(c+d*x)^2, x)", "(a+b*x^2)^2/(c+d*x)");

	}

	// {((a+b*x^2)*(-(a*d)+b*x*(4*c+3*d*x)))/(c+d*x)^2, x, 1, (a+b*x^2)^2/(c+d*x)}
	public void test01412() {
		check("Integrate(((a+b*x^2)*(-(a*d)+b*x*(4*c+3*d*x)))/(c+d*x)^2, x)", "(a+b*x^2)^2/(c+d*x)");

	}

	// {((a+b*x^2)^2*(-(a*d)+6*b*c*x+5*b*d*x^2))/(c+d*x)^2, x, 1, (a+b*x^2)^3/(c+d*x)}
	public void test01413() {
		check("Integrate(((a+b*x^2)^2*(-(a*d)+6*b*c*x+5*b*d*x^2))/(c+d*x)^2, x)", "(a+b*x^2)^3/(c+d*x)");

	}

	// {((a+b*x^2)^2*(-(a*d)+b*x*(6*c+5*d*x)))/(c+d*x)^2, x, 1, (a+b*x^2)^3/(c+d*x)}
	public void test01414() {
		check("Integrate(((a+b*x^2)^2*(-(a*d)+b*x*(6*c+5*d*x)))/(c+d*x)^2, x)", "(a+b*x^2)^3/(c+d*x)");

	}

	// {(1-x^2)/(1+x+x^2)^2, x, 1, x/(1+x+x^2)}
	public void test01415() {
		check("Integrate((1-x^2)/(1+x+x^2)^2, x)", "x/(1+x+x^2)");

	}

	// {(-1+2*x+5*x^2)/(1+x+x^2)^4, x, 1, -(x/(1+x+x^2)^3)}
	public void test01416() {
		check("Integrate((-1+2*x+5*x^2)/(1+x+x^2)^4, x)", "-(x/(1+x+x^2)^3)");

	}

	// {1/Sqrt(2-3*x^4), x, 1, EllipticF(ArcSin((3/2)^(1/4)*x), -1)/6^(1/4)}
	public void test01417() {
		check("Integrate(1/Sqrt(2-3*x^4), x)", "EllipticF(ArcSin((3/2)^(1/4)*x), -1)/6^(1/4)");

	}

	// {1/Sqrt(3-2*x^4), x, 1, EllipticF(ArcSin((2/3)^(1/4)*x), -1)/6^(1/4)}
	public void test01418() {
		check("Integrate(1/Sqrt(3-2*x^4), x)", "EllipticF(ArcSin((2/3)^(1/4)*x), -1)/6^(1/4)");

	}

	// {1/Sqrt(-2+5*x^2+3*x^4), x, 1, (Sqrt(2+x^2)*Sqrt(-1+3*x^2)*EllipticF(ArcSin((Sqrt(7/2)*x)/Sqrt(-1 +
	// 3*x^2)), 6/7))/(Sqrt(7)*Sqrt(-2+5*x^2+3*x^4))}
	public void test01419() {
		check("Integrate(1/Sqrt(-2+5*x^2+3*x^4), x)",
				"(Sqrt(2+x^2)*Sqrt(-1+3*x^2)*EllipticF(ArcSin((Sqrt(7/2)*x)/Sqrt(-1+3*x^2)), 6/7))/(Sqrt(7)*Sqrt(-2+5*x^2+3*x^4))");

	}

	// {1/Sqrt(-2+4*x^2+3*x^4), x, 1, (Sqrt((2-(2-Sqrt(10))*x^2)/(2-(2+Sqrt(10))*x^2))*Sqrt(-2+(2 +
	// Sqrt(10))*x^2)*EllipticF(ArcSin((2^(3/4)*5^(1/4)*x)/Sqrt(-2+(2+Sqrt(10))*x^2)), (5 +
	// Sqrt(10))/10))/(2*10^(1/4)*Sqrt((2-(2+Sqrt(10))*x^2)^(-1))*Sqrt(-2+4*x^2+3*x^4))}
	public void test01420() {
		check("Integrate(1/Sqrt(-2+4*x^2+3*x^4), x)",
				"(Sqrt((2-(2-Sqrt(10))*x^2)/(2-(2+Sqrt(10))*x^2))*Sqrt(-2+(2+Sqrt(10))*x^2)*EllipticF(ArcSin((2^(3/4)*5^(1/4)*x)/Sqrt(-2+(2+Sqrt(10))*x^2)), (5+Sqrt(10))/10))/(2*10^(1/4)*Sqrt((2-(2+Sqrt(10))*x^2)^(-1))*Sqrt(-2+4*x^2+3*x^4))");

	}

	// {1/Sqrt(-2+3*x^2+3*x^4), x, 1, (Sqrt((4-(3-Sqrt(33))*x^2)/(4-(3+Sqrt(33))*x^2))*Sqrt(-4+(3 +
	// Sqrt(33))*x^2)*EllipticF(ArcSin((Sqrt(2)*33^(1/4)*x)/Sqrt(-4+(3+Sqrt(33))*x^2)), (11 +
	// Sqrt(33))/22))/(2*Sqrt(2)*33^(1/4)*Sqrt((4-(3+Sqrt(33))*x^2)^(-1))*Sqrt(-2+3*x^2+3*x^4))}
	public void test01421() {
		check("Integrate(1/Sqrt(-2+3*x^2+3*x^4), x)",
				"(Sqrt((4-(3-Sqrt(33))*x^2)/(4-(3+Sqrt(33))*x^2))*Sqrt(-4+(3+Sqrt(33))*x^2)*EllipticF(ArcSin((Sqrt(2)*33^(1/4)*x)/Sqrt(-4+(3+Sqrt(33))*x^2)), (11+Sqrt(33))/22))/(2*Sqrt(2)*33^(1/4)*Sqrt((4-(3+Sqrt(33))*x^2)^(-1))*Sqrt(-2+3*x^2+3*x^4))");

	}

	// {1/Sqrt(-2+2*x^2+3*x^4), x, 1, (Sqrt((2-(1-Sqrt(7))*x^2)/(2-(1+Sqrt(7))*x^2))*Sqrt(-2+(1 +
	// Sqrt(7))*x^2)*EllipticF(ArcSin((Sqrt(2)*7^(1/4)*x)/Sqrt(-2+(1+Sqrt(7))*x^2)), (7 +
	// Sqrt(7))/14))/(2*7^(1/4)*Sqrt((2-(1+Sqrt(7))*x^2)^(-1))*Sqrt(-2+2*x^2+3*x^4))}
	public void test01422() {
		check("Integrate(1/Sqrt(-2+2*x^2+3*x^4), x)",
				"(Sqrt((2-(1-Sqrt(7))*x^2)/(2-(1+Sqrt(7))*x^2))*Sqrt(-2+(1+Sqrt(7))*x^2)*EllipticF(ArcSin((Sqrt(2)*7^(1/4)*x)/Sqrt(-2+(1+Sqrt(7))*x^2)), (7+Sqrt(7))/14))/(2*7^(1/4)*Sqrt((2-(1+Sqrt(7))*x^2)^(-1))*Sqrt(-2+2*x^2+3*x^4))");

	}

	// {1/Sqrt(-2+x^2+3*x^4), x, 1, (Sqrt(1+x^2)*Sqrt(-2+3*x^2)*EllipticF(ArcSin((Sqrt(5)*x)/Sqrt(-2+3*x^2)),
	// 3/5))/(Sqrt(5)*Sqrt(-2+x^2+3*x^4))}
	public void test01423() {
		check("Integrate(1/Sqrt(-2+x^2+3*x^4), x)",
				"(Sqrt(1+x^2)*Sqrt(-2+3*x^2)*EllipticF(ArcSin((Sqrt(5)*x)/Sqrt(-2+3*x^2)), 3/5))/(Sqrt(5)*Sqrt(-2+x^2+3*x^4))");

	}

	// {1/Sqrt(-2+3*x^4), x, 1, (Sqrt(-2+Sqrt(6)*x^2)*Sqrt((2+Sqrt(6)*x^2)/(2 -
	// Sqrt(6)*x^2))*EllipticF(ArcSin((2^(3/4)*3^(1/4)*x)/Sqrt(-2+Sqrt(6)*x^2)), 1/2))/(2*6^(1/4)*Sqrt((2 -
	// Sqrt(6)*x^2)^(-1))*Sqrt(-2+3*x^4))}
	public void test01424() {
		check("Integrate(1/Sqrt(-2+3*x^4), x)",
				"(Sqrt(-2+Sqrt(6)*x^2)*Sqrt((2+Sqrt(6)*x^2)/(2-Sqrt(6)*x^2))*EllipticF(ArcSin((2^(3/4)*3^(1/4)*x)/Sqrt(-2+Sqrt(6)*x^2)), 1/2))/(2*6^(1/4)*Sqrt((2-Sqrt(6)*x^2)^(-1))*Sqrt(-2+3*x^4))");

	}

	// {1/Sqrt(-2-x^2+3*x^4), x, 1, (Sqrt(-1+x^2)*Sqrt(2+3*x^2)*EllipticF(ArcSin((Sqrt(5/2)*x)/Sqrt(-1+x^2)),
	// 2/5))/(Sqrt(5)*Sqrt(-2-x^2+3*x^4))}
	public void test01425() {
		check("Integrate(1/Sqrt(-2-x^2+3*x^4), x)",
				"(Sqrt(-1+x^2)*Sqrt(2+3*x^2)*EllipticF(ArcSin((Sqrt(5/2)*x)/Sqrt(-1+x^2)), 2/5))/(Sqrt(5)*Sqrt(-2-x^2+3*x^4))");

	}

	// {1/Sqrt(-2-2*x^2+3*x^4), x, 1, (Sqrt(-2-(1-Sqrt(7))*x^2)*Sqrt((2+(1+Sqrt(7))*x^2)/(2+(1 -
	// Sqrt(7))*x^2))*EllipticF(ArcSin((Sqrt(2)*7^(1/4)*x)/Sqrt(-2-(1-Sqrt(7))*x^2)), (7 -
	// Sqrt(7))/14))/(2*7^(1/4)*Sqrt((2+(1-Sqrt(7))*x^2)^(-1))*Sqrt(-2-2*x^2+3*x^4))}
	public void test01426() {
		check("Integrate(1/Sqrt(-2-2*x^2+3*x^4), x)",
				"(Sqrt(-2-(1-Sqrt(7))*x^2)*Sqrt((2+(1+Sqrt(7))*x^2)/(2+(1-Sqrt(7))*x^2))*EllipticF(ArcSin((Sqrt(2)*7^(1/4)*x)/Sqrt(-2-(1-Sqrt(7))*x^2)), (7-Sqrt(7))/14))/(2*7^(1/4)*Sqrt((2+(1-Sqrt(7))*x^2)^(-1))*Sqrt(-2-2*x^2+3*x^4))");

	}

	// {1/Sqrt(-2-3*x^2+3*x^4), x, 1, (Sqrt(-4-(3-Sqrt(33))*x^2)*Sqrt((4+(3+Sqrt(33))*x^2)/(4+(3 -
	// Sqrt(33))*x^2))*EllipticF(ArcSin((Sqrt(2)*33^(1/4)*x)/Sqrt(-4-(3-Sqrt(33))*x^2)), (11 -
	// Sqrt(33))/22))/(2*Sqrt(2)*33^(1/4)*Sqrt((4+(3-Sqrt(33))*x^2)^(-1))*Sqrt(-2-3*x^2+3*x^4))}
	public void test01427() {
		check("Integrate(1/Sqrt(-2-3*x^2+3*x^4), x)",
				"(Sqrt(-4-(3-Sqrt(33))*x^2)*Sqrt((4+(3+Sqrt(33))*x^2)/(4+(3-Sqrt(33))*x^2))*EllipticF(ArcSin((Sqrt(2)*33^(1/4)*x)/Sqrt(-4-(3-Sqrt(33))*x^2)), (11-Sqrt(33))/22))/(2*Sqrt(2)*33^(1/4)*Sqrt((4+(3-Sqrt(33))*x^2)^(-1))*Sqrt(-2-3*x^2+3*x^4))");

	}

	// {1/Sqrt(-2-4*x^2+3*x^4), x, 1, (Sqrt(-2-(2-Sqrt(10))*x^2)*Sqrt((2+(2+Sqrt(10))*x^2)/(2+(2 -
	// Sqrt(10))*x^2))*EllipticF(ArcSin((2^(3/4)*5^(1/4)*x)/Sqrt(-2-(2-Sqrt(10))*x^2)), (5 -
	// Sqrt(10))/10))/(2*10^(1/4)*Sqrt((2+(2-Sqrt(10))*x^2)^(-1))*Sqrt(-2-4*x^2+3*x^4))}
	public void test01428() {
		check("Integrate(1/Sqrt(-2-4*x^2+3*x^4), x)",
				"(Sqrt(-2-(2-Sqrt(10))*x^2)*Sqrt((2+(2+Sqrt(10))*x^2)/(2+(2-Sqrt(10))*x^2))*EllipticF(ArcSin((2^(3/4)*5^(1/4)*x)/Sqrt(-2-(2-Sqrt(10))*x^2)), (5-Sqrt(10))/10))/(2*10^(1/4)*Sqrt((2+(2-Sqrt(10))*x^2)^(-1))*Sqrt(-2-4*x^2+3*x^4))");

	}

	// {1/Sqrt(-2-5*x^2+3*x^4), x, 1, (Sqrt(-2+x^2)*Sqrt(1+3*x^2)*EllipticF(ArcSin((Sqrt(7)*x)/Sqrt(-2+x^2)),
	// 1/7))/(Sqrt(7)*Sqrt(-2-5*x^2+3*x^4))}
	public void test01429() {
		check("Integrate(1/Sqrt(-2-5*x^2+3*x^4), x)",
				"(Sqrt(-2+x^2)*Sqrt(1+3*x^2)*EllipticF(ArcSin((Sqrt(7)*x)/Sqrt(-2+x^2)), 1/7))/(Sqrt(7)*Sqrt(-2-5*x^2+3*x^4))");

	}

	// {1/Sqrt(-3+7*x^2+2*x^4), x, 1, (Sqrt((6-(7-Sqrt(73))*x^2)/(6-(7+Sqrt(73))*x^2))*Sqrt(-6+(7 +
	// Sqrt(73))*x^2)*EllipticF(ArcSin((Sqrt(2)*73^(1/4)*x)/Sqrt(-6+(7+Sqrt(73))*x^2)), (73 +
	// 7*Sqrt(73))/146))/(2*Sqrt(3)*73^(1/4)*Sqrt((6-(7+Sqrt(73))*x^2)^(-1))*Sqrt(-3+7*x^2+2*x^4))}
	public void test01430() {
		check("Integrate(1/Sqrt(-3+7*x^2+2*x^4), x)",
				"(Sqrt((6-(7-Sqrt(73))*x^2)/(6-(7+Sqrt(73))*x^2))*Sqrt(-6+(7+Sqrt(73))*x^2)*EllipticF(ArcSin((Sqrt(2)*73^(1/4)*x)/Sqrt(-6+(7+Sqrt(73))*x^2)), (73+7*Sqrt(73))/146))/(2*Sqrt(3)*73^(1/4)*Sqrt((6-(7+Sqrt(73))*x^2)^(-1))*Sqrt(-3+7*x^2+2*x^4))");

	}

	// {1/Sqrt(-3+6*x^2+2*x^4), x, 1, (Sqrt((3-(3-Sqrt(15))*x^2)/(3-(3+Sqrt(15))*x^2))*Sqrt(-3+(3 +
	// Sqrt(15))*x^2)*EllipticF(ArcSin((Sqrt(2)*15^(1/4)*x)/Sqrt(-3+(3+Sqrt(15))*x^2)), (5 +
	// Sqrt(15))/10))/(Sqrt(2)*3^(3/4)*5^(1/4)*Sqrt((3-(3+Sqrt(15))*x^2)^(-1))*Sqrt(-3+6*x^2+2*x^4))}
	public void test01431() {
		check("Integrate(1/Sqrt(-3+6*x^2+2*x^4), x)",
				"(Sqrt((3-(3-Sqrt(15))*x^2)/(3-(3+Sqrt(15))*x^2))*Sqrt(-3+(3+Sqrt(15))*x^2)*EllipticF(ArcSin((Sqrt(2)*15^(1/4)*x)/Sqrt(-3+(3+Sqrt(15))*x^2)), (5+Sqrt(15))/10))/(Sqrt(2)*3^(3/4)*5^(1/4)*Sqrt((3-(3+Sqrt(15))*x^2)^(-1))*Sqrt(-3+6*x^2+2*x^4))");

	}

	// {1/Sqrt(-3+5*x^2+2*x^4), x, 1, (Sqrt(3+x^2)*Sqrt(-1+2*x^2)*EllipticF(ArcSin((Sqrt(7/3)*x)/Sqrt(-1 +
	// 2*x^2)), 6/7))/(Sqrt(7)*Sqrt(-3+5*x^2+2*x^4))}
	public void test01432() {
		check("Integrate(1/Sqrt(-3+5*x^2+2*x^4), x)",
				"(Sqrt(3+x^2)*Sqrt(-1+2*x^2)*EllipticF(ArcSin((Sqrt(7/3)*x)/Sqrt(-1+2*x^2)), 6/7))/(Sqrt(7)*Sqrt(-3+5*x^2+2*x^4))");

	}

	// {1/Sqrt(-3+4*x^2+2*x^4), x, 1, (Sqrt((3-(2-Sqrt(10))*x^2)/(3-(2+Sqrt(10))*x^2))*Sqrt(-3+(2 +
	// Sqrt(10))*x^2)*EllipticF(ArcSin((2^(3/4)*5^(1/4)*x)/Sqrt(-3+(2+Sqrt(10))*x^2)), (5 +
	// Sqrt(10))/10))/(2^(3/4)*Sqrt(3)*5^(1/4)*Sqrt((3-(2+Sqrt(10))*x^2)^(-1))*Sqrt(-3+4*x^2+2*x^4))}
	public void test01433() {
		check("Integrate(1/Sqrt(-3+4*x^2+2*x^4), x)",
				"(Sqrt((3-(2-Sqrt(10))*x^2)/(3-(2+Sqrt(10))*x^2))*Sqrt(-3+(2+Sqrt(10))*x^2)*EllipticF(ArcSin((2^(3/4)*5^(1/4)*x)/Sqrt(-3+(2+Sqrt(10))*x^2)), (5+Sqrt(10))/10))/(2^(3/4)*Sqrt(3)*5^(1/4)*Sqrt((3-(2+Sqrt(10))*x^2)^(-1))*Sqrt(-3+4*x^2+2*x^4))");

	}

	// {1/Sqrt(-3+3*x^2+2*x^4), x, 1, (Sqrt((6-(3-Sqrt(33))*x^2)/(6-(3+Sqrt(33))*x^2))*Sqrt(-6+(3 +
	// Sqrt(33))*x^2)*EllipticF(ArcSin((Sqrt(2)*33^(1/4)*x)/Sqrt(-6+(3+Sqrt(33))*x^2)), (11 +
	// Sqrt(33))/22))/(2*3^(3/4)*11^(1/4)*Sqrt((6-(3+Sqrt(33))*x^2)^(-1))*Sqrt(-3+3*x^2+2*x^4))}
	public void test01434() {
		check("Integrate(1/Sqrt(-3+3*x^2+2*x^4), x)",
				"(Sqrt((6-(3-Sqrt(33))*x^2)/(6-(3+Sqrt(33))*x^2))*Sqrt(-6+(3+Sqrt(33))*x^2)*EllipticF(ArcSin((Sqrt(2)*33^(1/4)*x)/Sqrt(-6+(3+Sqrt(33))*x^2)), (11+Sqrt(33))/22))/(2*3^(3/4)*11^(1/4)*Sqrt((6-(3+Sqrt(33))*x^2)^(-1))*Sqrt(-3+3*x^2+2*x^4))");

	}

	// {1/Sqrt(-3+2*x^2+2*x^4), x, 1, (Sqrt((3-(1-Sqrt(7))*x^2)/(3-(1+Sqrt(7))*x^2))*Sqrt(-3+(1 +
	// Sqrt(7))*x^2)*EllipticF(ArcSin((Sqrt(2)*7^(1/4)*x)/Sqrt(-3+(1+Sqrt(7))*x^2)), (7 +
	// Sqrt(7))/14))/(Sqrt(6)*7^(1/4)*Sqrt((3-(1+Sqrt(7))*x^2)^(-1))*Sqrt(-3+2*x^2+2*x^4))}
	public void test01435() {
		check("Integrate(1/Sqrt(-3+2*x^2+2*x^4), x)",
				"(Sqrt((3-(1-Sqrt(7))*x^2)/(3-(1+Sqrt(7))*x^2))*Sqrt(-3+(1+Sqrt(7))*x^2)*EllipticF(ArcSin((Sqrt(2)*7^(1/4)*x)/Sqrt(-3+(1+Sqrt(7))*x^2)), (7+Sqrt(7))/14))/(Sqrt(6)*7^(1/4)*Sqrt((3-(1+Sqrt(7))*x^2)^(-1))*Sqrt(-3+2*x^2+2*x^4))");

	}

	// {1/Sqrt(-3+x^2+2*x^4), x, 1, (Sqrt(-1+x^2)*Sqrt(3+2*x^2)*EllipticF(ArcSin((Sqrt(5/3)*x)/Sqrt(-1+x^2)),
	// 3/5))/(Sqrt(5)*Sqrt(-3+x^2+2*x^4))}
	public void test01436() {
		check("Integrate(1/Sqrt(-3+x^2+2*x^4), x)",
				"(Sqrt(-1+x^2)*Sqrt(3+2*x^2)*EllipticF(ArcSin((Sqrt(5/3)*x)/Sqrt(-1+x^2)), 3/5))/(Sqrt(5)*Sqrt(-3+x^2+2*x^4))");

	}

	// {1/Sqrt(-3+2*x^4), x, 1, (Sqrt(-3+Sqrt(6)*x^2)*Sqrt((3+Sqrt(6)*x^2)/(3 -
	// Sqrt(6)*x^2))*EllipticF(ArcSin((2^(3/4)*3^(1/4)*x)/Sqrt(-3+Sqrt(6)*x^2)), 1/2))/(6^(3/4)*Sqrt((3 -
	// Sqrt(6)*x^2)^(-1))*Sqrt(-3+2*x^4))}
	public void test01437() {
		check("Integrate(1/Sqrt(-3+2*x^4), x)",
				"(Sqrt(-3+Sqrt(6)*x^2)*Sqrt((3+Sqrt(6)*x^2)/(3-Sqrt(6)*x^2))*EllipticF(ArcSin((2^(3/4)*3^(1/4)*x)/Sqrt(-3+Sqrt(6)*x^2)), 1/2))/(6^(3/4)*Sqrt((3-Sqrt(6)*x^2)^(-1))*Sqrt(-3+2*x^4))");

	}

	// {1/Sqrt(-3-x^2+2*x^4), x, 1, (Sqrt(1+x^2)*Sqrt(-3+2*x^2)*EllipticF(ArcSin((Sqrt(5)*x)/Sqrt(-3+2*x^2)),
	// 2/5))/(Sqrt(5)*Sqrt(-3-x^2+2*x^4))}
	public void test01438() {
		check("Integrate(1/Sqrt(-3-x^2+2*x^4), x)",
				"(Sqrt(1+x^2)*Sqrt(-3+2*x^2)*EllipticF(ArcSin((Sqrt(5)*x)/Sqrt(-3+2*x^2)), 2/5))/(Sqrt(5)*Sqrt(-3-x^2+2*x^4))");

	}

	// {1/Sqrt(-3-2*x^2+2*x^4), x, 1, (Sqrt(-3-(1-Sqrt(7))*x^2)*Sqrt((3+(1+Sqrt(7))*x^2)/(3+(1 -
	// Sqrt(7))*x^2))*EllipticF(ArcSin((Sqrt(2)*7^(1/4)*x)/Sqrt(-3-(1-Sqrt(7))*x^2)), (7 -
	// Sqrt(7))/14))/(Sqrt(6)*7^(1/4)*Sqrt((3+(1-Sqrt(7))*x^2)^(-1))*Sqrt(-3-2*x^2+2*x^4))}
	public void test01439() {
		check("Integrate(1/Sqrt(-3-2*x^2+2*x^4), x)",
				"(Sqrt(-3-(1-Sqrt(7))*x^2)*Sqrt((3+(1+Sqrt(7))*x^2)/(3+(1-Sqrt(7))*x^2))*EllipticF(ArcSin((Sqrt(2)*7^(1/4)*x)/Sqrt(-3-(1-Sqrt(7))*x^2)), (7-Sqrt(7))/14))/(Sqrt(6)*7^(1/4)*Sqrt((3+(1-Sqrt(7))*x^2)^(-1))*Sqrt(-3-2*x^2+2*x^4))");

	}

	// {1/Sqrt(-3-3*x^2+2*x^4), x, 1, (Sqrt(-6-(3-Sqrt(33))*x^2)*Sqrt((6+(3+Sqrt(33))*x^2)/(6+(3 -
	// Sqrt(33))*x^2))*EllipticF(ArcSin((Sqrt(2)*33^(1/4)*x)/Sqrt(-6-(3-Sqrt(33))*x^2)), (11 -
	// Sqrt(33))/22))/(2*3^(3/4)*11^(1/4)*Sqrt((6+(3-Sqrt(33))*x^2)^(-1))*Sqrt(-3-3*x^2+2*x^4))}
	public void test01440() {
		check("Integrate(1/Sqrt(-3-3*x^2+2*x^4), x)",
				"(Sqrt(-6-(3-Sqrt(33))*x^2)*Sqrt((6+(3+Sqrt(33))*x^2)/(6+(3-Sqrt(33))*x^2))*EllipticF(ArcSin((Sqrt(2)*33^(1/4)*x)/Sqrt(-6-(3-Sqrt(33))*x^2)), (11-Sqrt(33))/22))/(2*3^(3/4)*11^(1/4)*Sqrt((6+(3-Sqrt(33))*x^2)^(-1))*Sqrt(-3-3*x^2+2*x^4))");

	}

	// {1/Sqrt(-3-4*x^2+2*x^4), x, 1, (Sqrt(-3-(2-Sqrt(10))*x^2)*Sqrt((3+(2+Sqrt(10))*x^2)/(3+(2 -
	// Sqrt(10))*x^2))*EllipticF(ArcSin((2^(3/4)*5^(1/4)*x)/Sqrt(-3-(2-Sqrt(10))*x^2)), (5 -
	// Sqrt(10))/10))/(2^(3/4)*Sqrt(3)*5^(1/4)*Sqrt((3+(2-Sqrt(10))*x^2)^(-1))*Sqrt(-3-4*x^2+2*x^4))}
	public void test01441() {
		check("Integrate(1/Sqrt(-3-4*x^2+2*x^4), x)",
				"(Sqrt(-3-(2-Sqrt(10))*x^2)*Sqrt((3+(2+Sqrt(10))*x^2)/(3+(2-Sqrt(10))*x^2))*EllipticF(ArcSin((2^(3/4)*5^(1/4)*x)/Sqrt(-3-(2-Sqrt(10))*x^2)), (5-Sqrt(10))/10))/(2^(3/4)*Sqrt(3)*5^(1/4)*Sqrt((3+(2-Sqrt(10))*x^2)^(-1))*Sqrt(-3-4*x^2+2*x^4))");

	}

	// {1/Sqrt(-3-5*x^2+2*x^4), x, 1, (Sqrt(-3+x^2)*Sqrt(1+2*x^2)*EllipticF(ArcSin((Sqrt(7)*x)/Sqrt(-3+x^2)),
	// 1/7))/(Sqrt(7)*Sqrt(-3-5*x^2+2*x^4))}
	public void test01442() {
		check("Integrate(1/Sqrt(-3-5*x^2+2*x^4), x)",
				"(Sqrt(-3+x^2)*Sqrt(1+2*x^2)*EllipticF(ArcSin((Sqrt(7)*x)/Sqrt(-3+x^2)), 1/7))/(Sqrt(7)*Sqrt(-3-5*x^2+2*x^4))");

	}

	// {1/Sqrt(2+5*x^2+3*x^4), x, 1, ((1+x^2)*Sqrt((2+3*x^2)/(1+x^2))*EllipticF(ArcTan(x),
	// -1/2))/(Sqrt(2)*Sqrt(2+5*x^2+3*x^4))}
	public void test01443() {
		check("Integrate(1/Sqrt(2+5*x^2+3*x^4), x)",
				"((1+x^2)*Sqrt((2+3*x^2)/(1+x^2))*EllipticF(ArcTan(x), -1/2))/(Sqrt(2)*Sqrt(2+5*x^2+3*x^4))");

	}

	// {1/Sqrt(2+4*x^2+3*x^4), x, 1, ((2+Sqrt(6)*x^2)*Sqrt((2+4*x^2+3*x^4)/(2 +
	// Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((3/2)^(1/4)*x), 1/2-1/Sqrt(6)))/(2*6^(1/4)*Sqrt(2+4*x^2+3*x^4))}
	public void test01444() {
		check("Integrate(1/Sqrt(2+4*x^2+3*x^4), x)",
				"((2+Sqrt(6)*x^2)*Sqrt((2+4*x^2+3*x^4)/(2+Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((3/2)^(1/4)*x), 1/2-1/Sqrt(6)))/(2*6^(1/4)*Sqrt(2+4*x^2+3*x^4))");

	}

	// {1/Sqrt(2+3*x^2+3*x^4), x, 1, ((2+Sqrt(6)*x^2)*Sqrt((2+3*x^2+3*x^4)/(2 +
	// Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((3/2)^(1/4)*x), (4-Sqrt(6))/8))/(2*6^(1/4)*Sqrt(2+3*x^2+3*x^4))}
	public void test01445() {
		check("Integrate(1/Sqrt(2+3*x^2+3*x^4), x)",
				"((2+Sqrt(6)*x^2)*Sqrt((2+3*x^2+3*x^4)/(2+Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((3/2)^(1/4)*x), (4-Sqrt(6))/8))/(2*6^(1/4)*Sqrt(2+3*x^2+3*x^4))");

	}

	// {1/Sqrt(2+2*x^2+3*x^4), x, 1, ((2+Sqrt(6)*x^2)*Sqrt((2+2*x^2+3*x^4)/(2 +
	// Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((3/2)^(1/4)*x), (6-Sqrt(6))/12))/(2*6^(1/4)*Sqrt(2+2*x^2+3*x^4))}
	public void test01446() {
		check("Integrate(1/Sqrt(2+2*x^2+3*x^4), x)",
				"((2+Sqrt(6)*x^2)*Sqrt((2+2*x^2+3*x^4)/(2+Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((3/2)^(1/4)*x), (6-Sqrt(6))/12))/(2*6^(1/4)*Sqrt(2+2*x^2+3*x^4))");

	}

	// {1/Sqrt(2+x^2+3*x^4), x, 1, ((2+Sqrt(6)*x^2)*Sqrt((2+x^2+3*x^4)/(2 +
	// Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((3/2)^(1/4)*x), (12-Sqrt(6))/24))/(2*6^(1/4)*Sqrt(2+x^2+3*x^4))}
	public void test01447() {
		check("Integrate(1/Sqrt(2+x^2+3*x^4), x)",
				"((2+Sqrt(6)*x^2)*Sqrt((2+x^2+3*x^4)/(2+Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((3/2)^(1/4)*x), (12-Sqrt(6))/24))/(2*6^(1/4)*Sqrt(2+x^2+3*x^4))");

	}

	// {1/Sqrt(2+3*x^4), x, 1, ((2+Sqrt(6)*x^2)*Sqrt((2+3*x^4)/(2 +
	// Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((3/2)^(1/4)*x), 1/2))/(2*6^(1/4)*Sqrt(2+3*x^4))}
	public void test01448() {
		check("Integrate(1/Sqrt(2+3*x^4), x)",
				"((2+Sqrt(6)*x^2)*Sqrt((2+3*x^4)/(2+Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((3/2)^(1/4)*x), 1/2))/(2*6^(1/4)*Sqrt(2+3*x^4))");

	}

	// {1/Sqrt(2-x^2+3*x^4), x, 1, ((2+Sqrt(6)*x^2)*Sqrt((2-x^2+3*x^4)/(2 +
	// Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((3/2)^(1/4)*x), (12+Sqrt(6))/24))/(2*6^(1/4)*Sqrt(2-x^2+3*x^4))}
	public void test01449() {
		check("Integrate(1/Sqrt(2-x^2+3*x^4), x)",
				"((2+Sqrt(6)*x^2)*Sqrt((2-x^2+3*x^4)/(2+Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((3/2)^(1/4)*x), (12+Sqrt(6))/24))/(2*6^(1/4)*Sqrt(2-x^2+3*x^4))");

	}

	// {1/Sqrt(2-2*x^2+3*x^4), x, 1, ((2+Sqrt(6)*x^2)*Sqrt((2-2*x^2+3*x^4)/(2 +
	// Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((3/2)^(1/4)*x), (6+Sqrt(6))/12))/(2*6^(1/4)*Sqrt(2-2*x^2+3*x^4))}
	public void test01450() {
		check("Integrate(1/Sqrt(2-2*x^2+3*x^4), x)",
				"((2+Sqrt(6)*x^2)*Sqrt((2-2*x^2+3*x^4)/(2+Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((3/2)^(1/4)*x), (6+Sqrt(6))/12))/(2*6^(1/4)*Sqrt(2-2*x^2+3*x^4))");

	}

	// {1/Sqrt(2-3*x^2+3*x^4), x, 1, ((2+Sqrt(6)*x^2)*Sqrt((2-3*x^2+3*x^4)/(2 +
	// Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((3/2)^(1/4)*x), (4+Sqrt(6))/8))/(2*6^(1/4)*Sqrt(2-3*x^2+3*x^4))}
	public void test01451() {
		check("Integrate(1/Sqrt(2-3*x^2+3*x^4), x)",
				"((2+Sqrt(6)*x^2)*Sqrt((2-3*x^2+3*x^4)/(2+Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((3/2)^(1/4)*x), (4+Sqrt(6))/8))/(2*6^(1/4)*Sqrt(2-3*x^2+3*x^4))");

	}

	// {1/Sqrt(2-4*x^2+3*x^4), x, 1, ((2+Sqrt(6)*x^2)*Sqrt((2-4*x^2+3*x^4)/(2 +
	// Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((3/2)^(1/4)*x), 1/2+1/Sqrt(6)))/(2*6^(1/4)*Sqrt(2-4*x^2+3*x^4))}
	public void test01452() {
		check("Integrate(1/Sqrt(2-4*x^2+3*x^4), x)",
				"((2+Sqrt(6)*x^2)*Sqrt((2-4*x^2+3*x^4)/(2+Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((3/2)^(1/4)*x), 1/2+1/Sqrt(6)))/(2*6^(1/4)*Sqrt(2-4*x^2+3*x^4))");

	}

	// {1/Sqrt(2-5*x^2+3*x^4), x, 1, ((2+Sqrt(6)*x^2)*Sqrt((2-5*x^2+3*x^4)/(2 +
	// Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((3/2)^(1/4)*x), (12+5*Sqrt(6))/24))/(2*6^(1/4)*Sqrt(2-5*x^2+3*x^4))}
	public void test01453() {
		check("Integrate(1/Sqrt(2-5*x^2+3*x^4), x)",
				"((2+Sqrt(6)*x^2)*Sqrt((2-5*x^2+3*x^4)/(2+Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((3/2)^(1/4)*x), (12+5*Sqrt(6))/24))/(2*6^(1/4)*Sqrt(2-5*x^2+3*x^4))");

	}

	// {1/Sqrt(2-6*x^2+3*x^4), x, 1, ((2+Sqrt(6)*x^2)*Sqrt((2-6*x^2+3*x^4)/(2 +
	// Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((3/2)^(1/4)*x), (2+Sqrt(6))/4))/(2*6^(1/4)*Sqrt(2-6*x^2+3*x^4))}
	public void test01454() {
		check("Integrate(1/Sqrt(2-6*x^2+3*x^4), x)",
				"((2+Sqrt(6)*x^2)*Sqrt((2-6*x^2+3*x^4)/(2+Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((3/2)^(1/4)*x), (2+Sqrt(6))/4))/(2*6^(1/4)*Sqrt(2-6*x^2+3*x^4))");

	}

	// {1/Sqrt(3+9*x^2+2*x^4), x, 1, (Sqrt((6+(9-Sqrt(57))*x^2)/(6+(9+Sqrt(57))*x^2))*(6+(9 +
	// Sqrt(57))*x^2)*EllipticF(ArcTan(Sqrt((9+Sqrt(57))/6)*x), (-19+3*Sqrt(57))/4))/(Sqrt(6*(9+Sqrt(57)))*Sqrt(3
	// +9*x^2+2*x^4))}
	public void test01455() {
		check("Integrate(1/Sqrt(3+9*x^2+2*x^4), x)",
				"(Sqrt((6+(9-Sqrt(57))*x^2)/(6+(9+Sqrt(57))*x^2))*(6+(9+Sqrt(57))*x^2)*EllipticF(ArcTan(Sqrt((9+Sqrt(57))/6)*x), (-19+3*Sqrt(57))/4))/(Sqrt(6*(9+Sqrt(57)))*Sqrt(3+9*x^2+2*x^4))");

	}

	// {1/Sqrt(3+8*x^2+2*x^4), x, 1, (Sqrt((3+(4-Sqrt(10))*x^2)/(3+(4+Sqrt(10))*x^2))*(3+(4 +
	// Sqrt(10))*x^2)*EllipticF(ArcTan(Sqrt((4+Sqrt(10))/3)*x), (-2*(5-2*Sqrt(10)))/3))/(Sqrt(3*(4 +
	// Sqrt(10)))*Sqrt(3+8*x^2+2*x^4))}
	public void test01456() {
		check("Integrate(1/Sqrt(3+8*x^2+2*x^4), x)",
				"(Sqrt((3+(4-Sqrt(10))*x^2)/(3+(4+Sqrt(10))*x^2))*(3+(4+Sqrt(10))*x^2)*EllipticF(ArcTan(Sqrt((4+Sqrt(10))/3)*x), (-2*(5-2*Sqrt(10)))/3))/(Sqrt(3*(4+Sqrt(10)))*Sqrt(3+8*x^2+2*x^4))");

	}

	// {1/Sqrt(3+7*x^2+2*x^4), x, 1, (Sqrt((3+x^2)/(1+2*x^2))*(1+2*x^2)*EllipticF(ArcTan(Sqrt(2)*x),
	// 5/6))/(Sqrt(6)*Sqrt(3+7*x^2+2*x^4))}
	public void test01457() {
		check("Integrate(1/Sqrt(3+7*x^2+2*x^4), x)",
				"(Sqrt((3+x^2)/(1+2*x^2))*(1+2*x^2)*EllipticF(ArcTan(Sqrt(2)*x), 5/6))/(Sqrt(6)*Sqrt(3+7*x^2+2*x^4))");

	}

	// {1/Sqrt(3+6*x^2+2*x^4), x, 1, (Sqrt((3+(3-Sqrt(3))*x^2)/(3+(3+Sqrt(3))*x^2))*(3+(3 +
	// Sqrt(3))*x^2)*EllipticF(ArcTan(Sqrt((3+Sqrt(3))/3)*x), -1+Sqrt(3)))/(Sqrt(3*(3+Sqrt(3)))*Sqrt(3+6*x^2 +
	// 2*x^4))}
	public void test01458() {
		check("Integrate(1/Sqrt(3+6*x^2+2*x^4), x)",
				"(Sqrt((3+(3-Sqrt(3))*x^2)/(3+(3+Sqrt(3))*x^2))*(3+(3+Sqrt(3))*x^2)*EllipticF(ArcTan(Sqrt((3+Sqrt(3))/3)*x), -1+Sqrt(3)))/(Sqrt(3*(3+Sqrt(3)))*Sqrt(3+6*x^2+2*x^4))");

	}

	// {1/Sqrt(3+5*x^2+2*x^4), x, 1, ((1+x^2)*Sqrt((3+2*x^2)/(1+x^2))*EllipticF(ArcTan(x),
	// 1/3))/(Sqrt(3)*Sqrt(3+5*x^2+2*x^4))}
	public void test01459() {
		check("Integrate(1/Sqrt(3+5*x^2+2*x^4), x)",
				"((1+x^2)*Sqrt((3+2*x^2)/(1+x^2))*EllipticF(ArcTan(x), 1/3))/(Sqrt(3)*Sqrt(3+5*x^2+2*x^4))");

	}

	// {1/Sqrt(3+4*x^2+2*x^4), x, 1, ((3+Sqrt(6)*x^2)*Sqrt((3+4*x^2+2*x^4)/(3 +
	// Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((2/3)^(1/4)*x), 1/2-1/Sqrt(6)))/(2*6^(1/4)*Sqrt(3+4*x^2+2*x^4))}
	public void test01460() {
		check("Integrate(1/Sqrt(3+4*x^2+2*x^4), x)",
				"((3+Sqrt(6)*x^2)*Sqrt((3+4*x^2+2*x^4)/(3+Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((2/3)^(1/4)*x), 1/2-1/Sqrt(6)))/(2*6^(1/4)*Sqrt(3+4*x^2+2*x^4))");

	}

	// {1/Sqrt(3+3*x^2+2*x^4), x, 1, ((3+Sqrt(6)*x^2)*Sqrt((3+3*x^2+2*x^4)/(3 +
	// Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((2/3)^(1/4)*x), (4-Sqrt(6))/8))/(2*6^(1/4)*Sqrt(3+3*x^2+2*x^4))}
	public void test01461() {
		check("Integrate(1/Sqrt(3+3*x^2+2*x^4), x)",
				"((3+Sqrt(6)*x^2)*Sqrt((3+3*x^2+2*x^4)/(3+Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((2/3)^(1/4)*x), (4-Sqrt(6))/8))/(2*6^(1/4)*Sqrt(3+3*x^2+2*x^4))");

	}

	// {1/Sqrt(3+2*x^2+2*x^4), x, 1, ((3+Sqrt(6)*x^2)*Sqrt((3+2*x^2+2*x^4)/(3 +
	// Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((2/3)^(1/4)*x), (6-Sqrt(6))/12))/(2*6^(1/4)*Sqrt(3+2*x^2+2*x^4))}
	public void test01462() {
		check("Integrate(1/Sqrt(3+2*x^2+2*x^4), x)",
				"((3+Sqrt(6)*x^2)*Sqrt((3+2*x^2+2*x^4)/(3+Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((2/3)^(1/4)*x), (6-Sqrt(6))/12))/(2*6^(1/4)*Sqrt(3+2*x^2+2*x^4))");

	}

	// {1/Sqrt(3+x^2+2*x^4), x, 1, ((3+Sqrt(6)*x^2)*Sqrt((3+x^2+2*x^4)/(3 +
	// Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((2/3)^(1/4)*x), (12-Sqrt(6))/24))/(2*6^(1/4)*Sqrt(3+x^2+2*x^4))}
	public void test01463() {
		check("Integrate(1/Sqrt(3+x^2+2*x^4), x)",
				"((3+Sqrt(6)*x^2)*Sqrt((3+x^2+2*x^4)/(3+Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((2/3)^(1/4)*x), (12-Sqrt(6))/24))/(2*6^(1/4)*Sqrt(3+x^2+2*x^4))");

	}

	// {1/Sqrt(3+2*x^4), x, 1, ((3+Sqrt(6)*x^2)*Sqrt((3+2*x^4)/(3 +
	// Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((2/3)^(1/4)*x), 1/2))/(2*6^(1/4)*Sqrt(3+2*x^4))}
	public void test01464() {
		check("Integrate(1/Sqrt(3+2*x^4), x)",
				"((3+Sqrt(6)*x^2)*Sqrt((3+2*x^4)/(3+Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((2/3)^(1/4)*x), 1/2))/(2*6^(1/4)*Sqrt(3+2*x^4))");

	}

	// {1/Sqrt(3-x^2+2*x^4), x, 1, ((3+Sqrt(6)*x^2)*Sqrt((3-x^2+2*x^4)/(3 +
	// Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((2/3)^(1/4)*x), (12+Sqrt(6))/24))/(2*6^(1/4)*Sqrt(3-x^2+2*x^4))}
	public void test01465() {
		check("Integrate(1/Sqrt(3-x^2+2*x^4), x)",
				"((3+Sqrt(6)*x^2)*Sqrt((3-x^2+2*x^4)/(3+Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((2/3)^(1/4)*x), (12+Sqrt(6))/24))/(2*6^(1/4)*Sqrt(3-x^2+2*x^4))");

	}

	// {1/Sqrt(3-2*x^2+2*x^4), x, 1, ((3+Sqrt(6)*x^2)*Sqrt((3-2*x^2+2*x^4)/(3 +
	// Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((2/3)^(1/4)*x), (6+Sqrt(6))/12))/(2*6^(1/4)*Sqrt(3-2*x^2+2*x^4))}
	public void test01466() {
		check("Integrate(1/Sqrt(3-2*x^2+2*x^4), x)",
				"((3+Sqrt(6)*x^2)*Sqrt((3-2*x^2+2*x^4)/(3+Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((2/3)^(1/4)*x), (6+Sqrt(6))/12))/(2*6^(1/4)*Sqrt(3-2*x^2+2*x^4))");

	}

	// {1/Sqrt(3-3*x^2+2*x^4), x, 1, ((3+Sqrt(6)*x^2)*Sqrt((3-3*x^2+2*x^4)/(3 +
	// Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((2/3)^(1/4)*x), (4+Sqrt(6))/8))/(2*6^(1/4)*Sqrt(3-3*x^2+2*x^4))}
	public void test01467() {
		check("Integrate(1/Sqrt(3-3*x^2+2*x^4), x)",
				"((3+Sqrt(6)*x^2)*Sqrt((3-3*x^2+2*x^4)/(3+Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((2/3)^(1/4)*x), (4+Sqrt(6))/8))/(2*6^(1/4)*Sqrt(3-3*x^2+2*x^4))");

	}

	// {1/Sqrt(3-4*x^2+2*x^4), x, 1, ((3+Sqrt(6)*x^2)*Sqrt((3-4*x^2+2*x^4)/(3 +
	// Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((2/3)^(1/4)*x), 1/2+1/Sqrt(6)))/(2*6^(1/4)*Sqrt(3-4*x^2+2*x^4))}
	public void test01468() {
		check("Integrate(1/Sqrt(3-4*x^2+2*x^4), x)",
				"((3+Sqrt(6)*x^2)*Sqrt((3-4*x^2+2*x^4)/(3+Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((2/3)^(1/4)*x), 1/2+1/Sqrt(6)))/(2*6^(1/4)*Sqrt(3-4*x^2+2*x^4))");

	}

	// {1/Sqrt(3-5*x^2+2*x^4), x, 1, ((3+Sqrt(6)*x^2)*Sqrt((3-5*x^2+2*x^4)/(3 +
	// Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((2/3)^(1/4)*x), (12+5*Sqrt(6))/24))/(2*6^(1/4)*Sqrt(3-5*x^2+2*x^4))}
	public void test01469() {
		check("Integrate(1/Sqrt(3-5*x^2+2*x^4), x)",
				"((3+Sqrt(6)*x^2)*Sqrt((3-5*x^2+2*x^4)/(3+Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((2/3)^(1/4)*x), (12+5*Sqrt(6))/24))/(2*6^(1/4)*Sqrt(3-5*x^2+2*x^4))");

	}

	// {1/Sqrt(3-6*x^2+2*x^4), x, 1, ((3+Sqrt(6)*x^2)*Sqrt((3-6*x^2+2*x^4)/(3 +
	// Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((2/3)^(1/4)*x), (2+Sqrt(6))/4))/(2*6^(1/4)*Sqrt(3-6*x^2+2*x^4))}
	public void test01470() {
		check("Integrate(1/Sqrt(3-6*x^2+2*x^4), x)",
				"((3+Sqrt(6)*x^2)*Sqrt((3-6*x^2+2*x^4)/(3+Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((2/3)^(1/4)*x), (2+Sqrt(6))/4))/(2*6^(1/4)*Sqrt(3-6*x^2+2*x^4))");

	}

	// {1/Sqrt(3-7*x^2+2*x^4), x, 1, ((3+Sqrt(6)*x^2)*Sqrt((3-7*x^2+2*x^4)/(3 +
	// Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((2/3)^(1/4)*x), (12+7*Sqrt(6))/24))/(2*6^(1/4)*Sqrt(3-7*x^2+2*x^4))}
	public void test01471() {
		check("Integrate(1/Sqrt(3-7*x^2+2*x^4), x)",
				"((3+Sqrt(6)*x^2)*Sqrt((3-7*x^2+2*x^4)/(3+Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((2/3)^(1/4)*x), (12+7*Sqrt(6))/24))/(2*6^(1/4)*Sqrt(3-7*x^2+2*x^4))");

	}

	// {1/Sqrt(-3+4*x^2-2*x^4), x, 1, ((3+Sqrt(6)*x^2)*Sqrt((3-4*x^2+2*x^4)/(3 +
	// Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((2/3)^(1/4)*x), 1/2+1/Sqrt(6)))/(2*6^(1/4)*Sqrt(-3+4*x^2-2*x^4))}
	public void test01472() {
		check("Integrate(1/Sqrt(-3+4*x^2-2*x^4), x)",
				"((3+Sqrt(6)*x^2)*Sqrt((3-4*x^2+2*x^4)/(3+Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((2/3)^(1/4)*x), 1/2+1/Sqrt(6)))/(2*6^(1/4)*Sqrt(-3+4*x^2-2*x^4))");

	}

	// {1/Sqrt(-3+3*x^2-2*x^4), x, 1, ((3+Sqrt(6)*x^2)*Sqrt((3-3*x^2+2*x^4)/(3 +
	// Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((2/3)^(1/4)*x), (4+Sqrt(6))/8))/(2*6^(1/4)*Sqrt(-3+3*x^2-2*x^4))}
	public void test01473() {
		check("Integrate(1/Sqrt(-3+3*x^2-2*x^4), x)",
				"((3+Sqrt(6)*x^2)*Sqrt((3-3*x^2+2*x^4)/(3+Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((2/3)^(1/4)*x), (4+Sqrt(6))/8))/(2*6^(1/4)*Sqrt(-3+3*x^2-2*x^4))");

	}

	// {1/Sqrt(-3+2*x^2-2*x^4), x, 1, ((3+Sqrt(6)*x^2)*Sqrt((3-2*x^2+2*x^4)/(3 +
	// Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((2/3)^(1/4)*x), (6+Sqrt(6))/12))/(2*6^(1/4)*Sqrt(-3+2*x^2-2*x^4))}
	public void test01474() {
		check("Integrate(1/Sqrt(-3+2*x^2-2*x^4), x)",
				"((3+Sqrt(6)*x^2)*Sqrt((3-2*x^2+2*x^4)/(3+Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((2/3)^(1/4)*x), (6+Sqrt(6))/12))/(2*6^(1/4)*Sqrt(-3+2*x^2-2*x^4))");

	}

	// {1/Sqrt(-3+x^2-2*x^4), x, 1, ((3+Sqrt(6)*x^2)*Sqrt((3-x^2+2*x^4)/(3 +
	// Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((2/3)^(1/4)*x), (12+Sqrt(6))/24))/(2*6^(1/4)*Sqrt(-3+x^2-2*x^4))}
	public void test01475() {
		check("Integrate(1/Sqrt(-3+x^2-2*x^4), x)",
				"((3+Sqrt(6)*x^2)*Sqrt((3-x^2+2*x^4)/(3+Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((2/3)^(1/4)*x), (12+Sqrt(6))/24))/(2*6^(1/4)*Sqrt(-3+x^2-2*x^4))");

	}

	// {1/Sqrt(-3-2*x^4), x, 1, ((3+Sqrt(6)*x^2)*Sqrt((3+2*x^4)/(3 +
	// Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((2/3)^(1/4)*x), 1/2))/(2*6^(1/4)*Sqrt(-3-2*x^4))}
	public void test01476() {
		check("Integrate(1/Sqrt(-3-2*x^4), x)",
				"((3+Sqrt(6)*x^2)*Sqrt((3+2*x^4)/(3+Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((2/3)^(1/4)*x), 1/2))/(2*6^(1/4)*Sqrt(-3-2*x^4))");

	}

	// {1/Sqrt(-3-x^2-2*x^4), x, 1, ((3+Sqrt(6)*x^2)*Sqrt((3+x^2+2*x^4)/(3 +
	// Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((2/3)^(1/4)*x), (12-Sqrt(6))/24))/(2*6^(1/4)*Sqrt(-3-x^2-2*x^4))}
	public void test01477() {
		check("Integrate(1/Sqrt(-3-x^2-2*x^4), x)",
				"((3+Sqrt(6)*x^2)*Sqrt((3+x^2+2*x^4)/(3+Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((2/3)^(1/4)*x), (12-Sqrt(6))/24))/(2*6^(1/4)*Sqrt(-3-x^2-2*x^4))");

	}

	// {1/Sqrt(-3-2*x^2-2*x^4), x, 1, ((3+Sqrt(6)*x^2)*Sqrt((3+2*x^2+2*x^4)/(3 +
	// Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((2/3)^(1/4)*x), (6-Sqrt(6))/12))/(2*6^(1/4)*Sqrt(-3-2*x^2-2*x^4))}
	public void test01478() {
		check("Integrate(1/Sqrt(-3-2*x^2-2*x^4), x)",
				"((3+Sqrt(6)*x^2)*Sqrt((3+2*x^2+2*x^4)/(3+Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((2/3)^(1/4)*x), (6-Sqrt(6))/12))/(2*6^(1/4)*Sqrt(-3-2*x^2-2*x^4))");

	}

	// {1/Sqrt(-3-3*x^2-2*x^4), x, 1, ((3+Sqrt(6)*x^2)*Sqrt((3+3*x^2+2*x^4)/(3 +
	// Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((2/3)^(1/4)*x), (4-Sqrt(6))/8))/(2*6^(1/4)*Sqrt(-3-3*x^2-2*x^4))}
	public void test01479() {
		check("Integrate(1/Sqrt(-3-3*x^2-2*x^4), x)",
				"((3+Sqrt(6)*x^2)*Sqrt((3+3*x^2+2*x^4)/(3+Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((2/3)^(1/4)*x), (4-Sqrt(6))/8))/(2*6^(1/4)*Sqrt(-3-3*x^2-2*x^4))");

	}

	// {1/Sqrt(-3-4*x^2-2*x^4), x, 1, ((3+Sqrt(6)*x^2)*Sqrt((3+4*x^2+2*x^4)/(3 +
	// Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((2/3)^(1/4)*x), 1/2-1/Sqrt(6)))/(2*6^(1/4)*Sqrt(-3-4*x^2-2*x^4))}
	public void test01480() {
		check("Integrate(1/Sqrt(-3-4*x^2-2*x^4), x)",
				"((3+Sqrt(6)*x^2)*Sqrt((3+4*x^2+2*x^4)/(3+Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((2/3)^(1/4)*x), 1/2-1/Sqrt(6)))/(2*6^(1/4)*Sqrt(-3-4*x^2-2*x^4))");

	}

	// {1/Sqrt(-2+4*x^2-3*x^4), x, 1, ((2+Sqrt(6)*x^2)*Sqrt((2-4*x^2+3*x^4)/(2 +
	// Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((3/2)^(1/4)*x), 1/2+1/Sqrt(6)))/(2*6^(1/4)*Sqrt(-2+4*x^2-3*x^4))}
	public void test01481() {
		check("Integrate(1/Sqrt(-2+4*x^2-3*x^4), x)",
				"((2+Sqrt(6)*x^2)*Sqrt((2-4*x^2+3*x^4)/(2+Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((3/2)^(1/4)*x), 1/2+1/Sqrt(6)))/(2*6^(1/4)*Sqrt(-2+4*x^2-3*x^4))");

	}

	// {1/Sqrt(-2+3*x^2-3*x^4), x, 1, ((2+Sqrt(6)*x^2)*Sqrt((2-3*x^2+3*x^4)/(2 +
	// Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((3/2)^(1/4)*x), (4+Sqrt(6))/8))/(2*6^(1/4)*Sqrt(-2+3*x^2-3*x^4))}
	public void test01482() {
		check("Integrate(1/Sqrt(-2+3*x^2-3*x^4), x)",
				"((2+Sqrt(6)*x^2)*Sqrt((2-3*x^2+3*x^4)/(2+Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((3/2)^(1/4)*x), (4+Sqrt(6))/8))/(2*6^(1/4)*Sqrt(-2+3*x^2-3*x^4))");

	}

	// {1/Sqrt(-2+2*x^2-3*x^4), x, 1, ((2+Sqrt(6)*x^2)*Sqrt((2-2*x^2+3*x^4)/(2 +
	// Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((3/2)^(1/4)*x), (6+Sqrt(6))/12))/(2*6^(1/4)*Sqrt(-2+2*x^2-3*x^4))}
	public void test01483() {
		check("Integrate(1/Sqrt(-2+2*x^2-3*x^4), x)",
				"((2+Sqrt(6)*x^2)*Sqrt((2-2*x^2+3*x^4)/(2+Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((3/2)^(1/4)*x), (6+Sqrt(6))/12))/(2*6^(1/4)*Sqrt(-2+2*x^2-3*x^4))");

	}

	// {1/Sqrt(-2+x^2-3*x^4), x, 1, ((2+Sqrt(6)*x^2)*Sqrt((2-x^2+3*x^4)/(2 +
	// Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((3/2)^(1/4)*x), (12+Sqrt(6))/24))/(2*6^(1/4)*Sqrt(-2+x^2-3*x^4))}
	public void test01484() {
		check("Integrate(1/Sqrt(-2+x^2-3*x^4), x)",
				"((2+Sqrt(6)*x^2)*Sqrt((2-x^2+3*x^4)/(2+Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((3/2)^(1/4)*x), (12+Sqrt(6))/24))/(2*6^(1/4)*Sqrt(-2+x^2-3*x^4))");

	}

	// {1/Sqrt(-2-3*x^4), x, 1, ((2+Sqrt(6)*x^2)*Sqrt((2+3*x^4)/(2 +
	// Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((3/2)^(1/4)*x), 1/2))/(2*6^(1/4)*Sqrt(-2-3*x^4))}
	public void test01485() {
		check("Integrate(1/Sqrt(-2-3*x^4), x)",
				"((2+Sqrt(6)*x^2)*Sqrt((2+3*x^4)/(2+Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((3/2)^(1/4)*x), 1/2))/(2*6^(1/4)*Sqrt(-2-3*x^4))");

	}

	// {1/Sqrt(-2-x^2-3*x^4), x, 1, ((2+Sqrt(6)*x^2)*Sqrt((2+x^2+3*x^4)/(2 +
	// Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((3/2)^(1/4)*x), (12-Sqrt(6))/24))/(2*6^(1/4)*Sqrt(-2-x^2-3*x^4))}
	public void test01486() {
		check("Integrate(1/Sqrt(-2-x^2-3*x^4), x)",
				"((2+Sqrt(6)*x^2)*Sqrt((2+x^2+3*x^4)/(2+Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((3/2)^(1/4)*x), (12-Sqrt(6))/24))/(2*6^(1/4)*Sqrt(-2-x^2-3*x^4))");

	}

	// {1/Sqrt(-2-2*x^2-3*x^4), x, 1, ((2+Sqrt(6)*x^2)*Sqrt((2+2*x^2+3*x^4)/(2 +
	// Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((3/2)^(1/4)*x), (6-Sqrt(6))/12))/(2*6^(1/4)*Sqrt(-2-2*x^2-3*x^4))}
	public void test01487() {
		check("Integrate(1/Sqrt(-2-2*x^2-3*x^4), x)",
				"((2+Sqrt(6)*x^2)*Sqrt((2+2*x^2+3*x^4)/(2+Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((3/2)^(1/4)*x), (6-Sqrt(6))/12))/(2*6^(1/4)*Sqrt(-2-2*x^2-3*x^4))");

	}

	// {1/Sqrt(-2-3*x^2-3*x^4), x, 1, ((2+Sqrt(6)*x^2)*Sqrt((2+3*x^2+3*x^4)/(2 +
	// Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((3/2)^(1/4)*x), (4-Sqrt(6))/8))/(2*6^(1/4)*Sqrt(-2-3*x^2-3*x^4))}
	public void test01488() {
		check("Integrate(1/Sqrt(-2-3*x^2-3*x^4), x)",
				"((2+Sqrt(6)*x^2)*Sqrt((2+3*x^2+3*x^4)/(2+Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((3/2)^(1/4)*x), (4-Sqrt(6))/8))/(2*6^(1/4)*Sqrt(-2-3*x^2-3*x^4))");

	}

	// {1/Sqrt(-2-4*x^2-3*x^4), x, 1, ((2+Sqrt(6)*x^2)*Sqrt((2+4*x^2+3*x^4)/(2 +
	// Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((3/2)^(1/4)*x), 1/2-1/Sqrt(6)))/(2*6^(1/4)*Sqrt(-2-4*x^2-3*x^4))}
	public void test01489() {
		check("Integrate(1/Sqrt(-2-4*x^2-3*x^4), x)",
				"((2+Sqrt(6)*x^2)*Sqrt((2+4*x^2+3*x^4)/(2+Sqrt(6)*x^2)^2)*EllipticF(2*ArcTan((3/2)^(1/4)*x), 1/2-1/Sqrt(6)))/(2*6^(1/4)*Sqrt(-2-4*x^2-3*x^4))");

	}

	// {1/Sqrt(2+5*x^2+5*x^4), x, 1, ((2+Sqrt(10)*x^2)*Sqrt((2+5*x^2+5*x^4)/(2 +
	// Sqrt(10)*x^2)^2)*EllipticF(2*ArcTan((5/2)^(1/4)*x), (4-Sqrt(10))/8))/(2*10^(1/4)*Sqrt(2+5*x^2+5*x^4))}
	public void test01490() {
		check("Integrate(1/Sqrt(2+5*x^2+5*x^4), x)",
				"((2+Sqrt(10)*x^2)*Sqrt((2+5*x^2+5*x^4)/(2+Sqrt(10)*x^2)^2)*EllipticF(2*ArcTan((5/2)^(1/4)*x), (4-Sqrt(10))/8))/(2*10^(1/4)*Sqrt(2+5*x^2+5*x^4))");

	}

	// {1/Sqrt(2+5*x^2+4*x^4), x, 1, ((1+Sqrt(2)*x^2)*Sqrt((2+5*x^2+4*x^4)/(1 +
	// Sqrt(2)*x^2)^2)*EllipticF(2*ArcTan(2^(1/4)*x), (8-5*Sqrt(2))/16))/(2*2^(3/4)*Sqrt(2+5*x^2+4*x^4))}
	public void test01491() {
		check("Integrate(1/Sqrt(2+5*x^2+4*x^4), x)",
				"((1+Sqrt(2)*x^2)*Sqrt((2+5*x^2+4*x^4)/(1+Sqrt(2)*x^2)^2)*EllipticF(2*ArcTan(2^(1/4)*x), (8-5*Sqrt(2))/16))/(2*2^(3/4)*Sqrt(2+5*x^2+4*x^4))");

	}

	// {1/Sqrt(2+5*x^2+3*x^4), x, 1, ((1+x^2)*Sqrt((2+3*x^2)/(1+x^2))*EllipticF(ArcTan(x),
	// -1/2))/(Sqrt(2)*Sqrt(2+5*x^2+3*x^4))}
	public void test01492() {
		check("Integrate(1/Sqrt(2+5*x^2+3*x^4), x)",
				"((1+x^2)*Sqrt((2+3*x^2)/(1+x^2))*EllipticF(ArcTan(x), -1/2))/(Sqrt(2)*Sqrt(2+5*x^2+3*x^4))");

	}

	// {1/Sqrt(2+5*x^2+2*x^4), x, 1, (Sqrt((2+x^2)/(1+2*x^2))*(1+2*x^2)*EllipticF(ArcTan(Sqrt(2)*x),
	// 3/4))/(2*Sqrt(2+5*x^2+2*x^4))}
	public void test01493() {
		check("Integrate(1/Sqrt(2+5*x^2+2*x^4), x)",
				"(Sqrt((2+x^2)/(1+2*x^2))*(1+2*x^2)*EllipticF(ArcTan(Sqrt(2)*x), 3/4))/(2*Sqrt(2+5*x^2+2*x^4))");

	}

	// {1/Sqrt(2+5*x^2+x^4), x, 1, (Sqrt((4+(5-Sqrt(17))*x^2)/(4+(5+Sqrt(17))*x^2))*(4+(5 +
	// Sqrt(17))*x^2)*EllipticF(ArcTan((Sqrt(5+Sqrt(17))*x)/2), (-17+5*Sqrt(17))/4))/(2*Sqrt(5+Sqrt(17))*Sqrt(2 +
	// 5*x^2+x^4))}
	public void test01494() {
		check("Integrate(1/Sqrt(2+5*x^2+x^4), x)",
				"(Sqrt((4+(5-Sqrt(17))*x^2)/(4+(5+Sqrt(17))*x^2))*(4+(5+Sqrt(17))*x^2)*EllipticF(ArcTan((Sqrt(5+Sqrt(17))*x)/2), (-17+5*Sqrt(17))/4))/(2*Sqrt(5+Sqrt(17))*Sqrt(2+5*x^2+x^4))");

	}

	// {b*x^2+c*x^4, x, 1, (b*x^3)/3+(c*x^5)/5}
	public void test01495() {
		check("Integrate(b*x^2+c*x^4, x)", "(b*x^3)/3+(c*x^5)/5");

	}

	// {x^5/(b*x^2+c*x^4)^2, x, 1, -x^2/(2*c*(b*x^2+c*x^4))}
	public void test01496() {
		check("Integrate(x^5/(b*x^2+c*x^4)^2, x)", "-x^2/(2*c*(b*x^2+c*x^4))");

	}

	// {x^7/(b*x^2+c*x^4)^3, x, 1, -x^4/(4*c*(b*x^2+c*x^4)^2)}
	public void test01497() {
		check("Integrate(x^7/(b*x^2+c*x^4)^3, x)", "-x^4/(4*c*(b*x^2+c*x^4)^2)");

	}

	// {Sqrt(b*x^2+c*x^4)/x^5, x, 1, -(b*x^2+c*x^4)^(3/2)/(3*b*x^6)}
	public void test01498() {
		check("Integrate(Sqrt(b*x^2+c*x^4)/x^5, x)", "-(b*x^2+c*x^4)^(3/2)/(3*b*x^6)");

	}

	// {Sqrt(b*x^2+c*x^4), x, 1, (b*x^2+c*x^4)^(3/2)/(3*c*x^3)}
	public void test01499() {
		check("Integrate(Sqrt(b*x^2+c*x^4), x)", "(b*x^2+c*x^4)^(3/2)/(3*c*x^3)");

	}

	// {(b*x^2+c*x^4)^(3/2)/x^9, x, 1, -(b*x^2+c*x^4)^(5/2)/(5*b*x^10)}
	public void test01500() {
		check("Integrate((b*x^2+c*x^4)^(3/2)/x^9, x)", "-(b*x^2+c*x^4)^(5/2)/(5*b*x^10)");

	}

	// {(b*x^2+c*x^4)^(3/2)/x^2, x, 1, (b*x^2+c*x^4)^(5/2)/(5*c*x^5)}
	public void test01501() {
		check("Integrate((b*x^2+c*x^4)^(3/2)/x^2, x)", "(b*x^2+c*x^4)^(5/2)/(5*c*x^5)");

	}

	// {1/(x*Sqrt(b*x^2+c*x^4)), x, 1, -(Sqrt(b*x^2+c*x^4)/(b*x^2))}
	public void test01502() {
		check("Integrate(1/(x*Sqrt(b*x^2+c*x^4)), x)", "-(Sqrt(b*x^2+c*x^4)/(b*x^2))");

	}

	// {x^2/Sqrt(b*x^2+c*x^4), x, 1, Sqrt(b*x^2+c*x^4)/(c*x)}
	public void test01503() {
		check("Integrate(x^2/Sqrt(b*x^2+c*x^4), x)", "Sqrt(b*x^2+c*x^4)/(c*x)");

	}

	// {x^3/(b*x^2+c*x^4)^(3/2), x, 1, x^2/(b*Sqrt(b*x^2+c*x^4))}
	public void test01504() {
		check("Integrate(x^3/(b*x^2+c*x^4)^(3/2), x)", "x^2/(b*Sqrt(b*x^2+c*x^4))");

	}

	// {x^4/(b*x^2+c*x^4)^(3/2), x, 1, -(x/(c*Sqrt(b*x^2+c*x^4)))}
	public void test01505() {
		check("Integrate(x^4/(b*x^2+c*x^4)^(3/2), x)", "-(x/(c*Sqrt(b*x^2+c*x^4)))");

	}

	// {a^2+2*a*b*x^2+b^2*x^4, x, 1, a^2*x+(2*a*b*x^3)/3+(b^2*x^5)/5}
	public void test01506() {
		check("Integrate(a^2+2*a*b*x^2+b^2*x^4, x)", "a^2*x+(2*a*b*x^3)/3+(b^2*x^5)/5");

	}

	// {Sqrt(a^2+2*a*b*x^2+b^2*x^4)/x^7, x, 1, -((a+b*x^2)*Sqrt(a^2+2*a*b*x^2+b^2*x^4))/(4*a*x^6)+(a^2 +
	// 2*a*b*x^2+b^2*x^4)^(3/2)/(12*a^2*x^6)}
	public void test01507() {
		check("Integrate(Sqrt(a^2+2*a*b*x^2+b^2*x^4)/x^7, x)",
				"-((a+b*x^2)*Sqrt(a^2+2*a*b*x^2+b^2*x^4))/(4*a*x^6)+(a^2+2*a*b*x^2+b^2*x^4)^(3/2)/(12*a^2*x^6)");

	}

	// {(a^2+2*a*b*x^2+b^2*x^4)^(3/2)/x^11, x, 1, -((a+b*x^2)*(a^2+2*a*b*x^2+b^2*x^4)^(3/2))/(8*a*x^10)+(a^2
	// +2*a*b*x^2+b^2*x^4)^(5/2)/(40*a^2*x^10)}
	public void test01508() {
		check("Integrate((a^2+2*a*b*x^2+b^2*x^4)^(3/2)/x^11, x)",
				"-((a+b*x^2)*(a^2+2*a*b*x^2+b^2*x^4)^(3/2))/(8*a*x^10)+(a^2+2*a*b*x^2+b^2*x^4)^(5/2)/(40*a^2*x^10)");

	}

	// {(a^2+2*a*b*x^2+b^2*x^4)^(5/2)/x^15, x, 1, -((a+b*x^2)*(a^2+2*a*b*x^2+b^2*x^4)^(5/2))/(12*a*x^14) +
	// (a^2+2*a*b*x^2+b^2*x^4)^(7/2)/(84*a^2*x^14)}
	public void test01509() {
		check("Integrate((a^2+2*a*b*x^2+b^2*x^4)^(5/2)/x^15, x)",
				"-((a+b*x^2)*(a^2+2*a*b*x^2+b^2*x^4)^(5/2))/(12*a*x^14)+(a^2+2*a*b*x^2+b^2*x^4)^(7/2)/(84*a^2*x^14)");

	}

	// {x^5/(a^2+2*a*b*x^2+b^2*x^4)^(5/2), x, 1, x^6/(24*a^2*(a^2+2*a*b*x^2+b^2*x^4)^(3/2))+x^6/(8*a*(a +
	// b*x^2)*(a^2+2*a*b*x^2+b^2*x^4)^(3/2))}
	public void test01510() {
		check("Integrate(x^5/(a^2+2*a*b*x^2+b^2*x^4)^(5/2), x)",
				"x^6/(24*a^2*(a^2+2*a*b*x^2+b^2*x^4)^(3/2))+x^6/(8*a*(a+b*x^2)*(a^2+2*a*b*x^2+b^2*x^4)^(3/2))");

	}

	// {a+b*x^2+c*x^4, x, 1, a*x+(b*x^3)/3+(c*x^5)/5}
	public void test01511() {
		check("Integrate(a+b*x^2+c*x^4, x)", "a*x+(b*x^3)/3+(c*x^5)/5");

	}

	// {1/Sqrt(a+b*x^2+c*x^4), x, 1, ((Sqrt(a)+Sqrt(c)*x^2)*Sqrt((a+b*x^2+c*x^4)/(Sqrt(a) +
	// Sqrt(c)*x^2)^2)*EllipticF(2*ArcTan((c^(1/4)*x)/a^(1/4)), (2-b/(Sqrt(a)*Sqrt(c)))/4))/(2*a^(1/4)*c^(1/4)*Sqrt(a
	// +b*x^2+c*x^4))}
	public void test01512() {
		check("Integrate(1/Sqrt(a+b*x^2+c*x^4), x)",
				"((Sqrt(a)+Sqrt(c)*x^2)*Sqrt((a+b*x^2+c*x^4)/(Sqrt(a)+Sqrt(c)*x^2)^2)*EllipticF(2*ArcTan((c^(1/4)*x)/a^(1/4)), (2-b/(Sqrt(a)*Sqrt(c)))/4))/(2*a^(1/4)*c^(1/4)*Sqrt(a+b*x^2+c*x^4))");

	}

	// {(1-b*x^2)/Sqrt(1+b^2*x^4), x, 1, -((x*Sqrt(1+b^2*x^4))/(1+b*x^2))+((1+b*x^2)*Sqrt((1+b^2*x^4)/(1 +
	// b*x^2)^2)*EllipticE(2*ArcTan(Sqrt(b)*x), 1/2))/(Sqrt(b)*Sqrt(1+b^2*x^4))}
	public void test01513() {
		check("Integrate((1-b*x^2)/Sqrt(1+b^2*x^4), x)",
				"-((x*Sqrt(1+b^2*x^4))/(1+b*x^2))+((1+b*x^2)*Sqrt((1+b^2*x^4)/(1+b*x^2)^2)*EllipticE(2*ArcTan(Sqrt(b)*x), 1/2))/(Sqrt(b)*Sqrt(1+b^2*x^4))");

	}

	// {(1-b*x^2)/Sqrt(-1-b^2*x^4), x, 1, (x*Sqrt(-1-b^2*x^4))/(1+b*x^2)+((1+b*x^2)*Sqrt((1+b^2*x^4)/(1 +
	// b*x^2)^2)*EllipticE(2*ArcTan(Sqrt(b)*x), 1/2))/(Sqrt(b)*Sqrt(-1-b^2*x^4))}
	public void test01514() {
		check("Integrate((1-b*x^2)/Sqrt(-1-b^2*x^4), x)",
				"(x*Sqrt(-1-b^2*x^4))/(1+b*x^2)+((1+b*x^2)*Sqrt((1+b^2*x^4)/(1+b*x^2)^2)*EllipticE(2*ArcTan(Sqrt(b)*x), 1/2))/(Sqrt(b)*Sqrt(-1-b^2*x^4))");

	}

	// {Sqrt(1+c^2*x^2)/Sqrt(1-c^2*x^2), x, 1, EllipticE(ArcSin(c*x), -1)/c}
	public void test01515() {
		check("Integrate(Sqrt(1+c^2*x^2)/Sqrt(1-c^2*x^2), x)", "EllipticE(ArcSin(c*x), -1)/c");

	}

	// {1/((a+b*x^2)*Sqrt(4-d*x^4)), x, 1, EllipticPi((-2*b)/(a*Sqrt(d)), ArcSin((d^(1/4)*x)/Sqrt(2)),
	// -1)/(Sqrt(2)*a*d^(1/4))}
	public void test01516() {
		check("Integrate(1/((a+b*x^2)*Sqrt(4-d*x^4)), x)",
				"EllipticPi((-2*b)/(a*Sqrt(d)), ArcSin((d^(1/4)*x)/Sqrt(2)), -1)/(Sqrt(2)*a*d^(1/4))");

	}

	// {(1+b*x^4)^p, x, 1, x*Hypergeometric2F1(1/4, -p, 5/4, -(b*x^4))}
	public void test01517() {
		check("Integrate((1+b*x^4)^p, x)", "x*Hypergeometric2F1(1/4, -p, 5/4, -(b*x^4))");

	}

	// {Sqrt(1+x^2+x^4)/(1+x^2)^2, x, 1, ((1+x^2)*Sqrt((1+x^2+x^4)/(1+x^2)^2)*EllipticE(2*ArcTan(x),
	// 1/4))/(2*Sqrt(1+x^2+x^4))}
	public void test01518() {
		check("Integrate(Sqrt(1+x^2+x^4)/(1+x^2)^2, x)",
				"((1+x^2)*Sqrt((1+x^2+x^4)/(1+x^2)^2)*EllipticE(2*ArcTan(x), 1/4))/(2*Sqrt(1+x^2+x^4))");

	}

	// {1/Sqrt(2+3*x^2+x^4), x, 1, ((1+x^2)*Sqrt((2+x^2)/(1+x^2))*EllipticF(ArcTan(x), 1/2))/(Sqrt(2)*Sqrt(2 +
	// 3*x^2+x^4))}
	public void test01519() {
		check("Integrate(1/Sqrt(2+3*x^2+x^4), x)",
				"((1+x^2)*Sqrt((2+x^2)/(1+x^2))*EllipticF(ArcTan(x), 1/2))/(Sqrt(2)*Sqrt(2+3*x^2+x^4))");

	}

	// {1/Sqrt(4+3*x^2+x^4), x, 1, ((2+x^2)*Sqrt((4+3*x^2+x^4)/(2+x^2)^2)*EllipticF(2*ArcTan(x/Sqrt(2)),
	// 1/8))/(2*Sqrt(2)*Sqrt(4+3*x^2+x^4))}
	public void test01520() {
		check("Integrate(1/Sqrt(4+3*x^2+x^4), x)",
				"((2+x^2)*Sqrt((4+3*x^2+x^4)/(2+x^2)^2)*EllipticF(2*ArcTan(x/Sqrt(2)), 1/8))/(2*Sqrt(2)*Sqrt(4+3*x^2+x^4))");

	}

	// {x*(a+b*x^2)*(a^2+2*a*b*x^2+b^2*x^4)^p, x, 1, (a^2+2*a*b*x^2+b^2*x^4)^(1+p)/(4*b*(1+p))}
	public void test01521() {
		check("Integrate(x*(a+b*x^2)*(a^2+2*a*b*x^2+b^2*x^4)^p, x)", "(a^2+2*a*b*x^2+b^2*x^4)^(1+p)/(4*b*(1+p))");

	}

	// {(a*g-c*g*x^4)/(a+b*x^2+c*x^4)^(3/2), x, 1, (g*x)/Sqrt(a+b*x^2+c*x^4)}
	public void test01522() {
		check("Integrate((a*g-c*g*x^4)/(a+b*x^2+c*x^4)^(3/2), x)", "(g*x)/Sqrt(a+b*x^2+c*x^4)");

	}

	// {x^2*(a+b*x^2+c*x^4)^p*(3*a+b*(5+2*p)*x^2+c*(7+4*p)*x^4), x, 1, x^3*(a+b*x^2+c*x^4)^(1+p)}
	public void test01523() {
		check("Integrate(x^2*(a+b*x^2+c*x^4)^p*(3*a+b*(5+2*p)*x^2+c*(7+4*p)*x^4), x)", "x^3*(a+b*x^2+c*x^4)^(1+p)");

	}

	// {Sqrt(a+b*x^2-c*x^4)/(a*d+c*d*x^4), x, 1, -(Sqrt(b+Sqrt(b^2+4*a*c))*ArcTan((Sqrt(b+Sqrt(b^2 +
	// 4*a*c))*x*(b-Sqrt(b^2+4*a*c)-2*c*x^2))/(2*Sqrt(2)*Sqrt(a)*Sqrt(c)*Sqrt(a+b*x^2 -
	// c*x^4))))/(2*Sqrt(2)*Sqrt(a)*Sqrt(c)*d)+(Sqrt(-b+Sqrt(b^2+4*a*c))*ArcTanh((Sqrt(-b+Sqrt(b^2 +
	// 4*a*c))*x*(b+Sqrt(b^2+4*a*c)-2*c*x^2))/(2*Sqrt(2)*Sqrt(a)*Sqrt(c)*Sqrt(a+b*x^2 -
	// c*x^4))))/(2*Sqrt(2)*Sqrt(a)*Sqrt(c)*d)}
	public void test01524() {
		check("Integrate(Sqrt(a+b*x^2-c*x^4)/(a*d+c*d*x^4), x)",
				"-(Sqrt(b+Sqrt(b^2+4*a*c))*ArcTan((Sqrt(b+Sqrt(b^2+4*a*c))*x*(b-Sqrt(b^2+4*a*c)-2*c*x^2))/(2*Sqrt(2)*Sqrt(a)*Sqrt(c)*Sqrt(a+b*x^2-c*x^4))))/(2*Sqrt(2)*Sqrt(a)*Sqrt(c)*d)+(Sqrt(-b+Sqrt(b^2+4*a*c))*ArcTanh((Sqrt(-b+Sqrt(b^2+4*a*c))*x*(b+Sqrt(b^2+4*a*c)-2*c*x^2))/(2*Sqrt(2)*Sqrt(a)*Sqrt(c)*Sqrt(a+b*x^2-c*x^4))))/(2*Sqrt(2)*Sqrt(a)*Sqrt(c)*d)");

	}

	// {(Sqrt(a)+Sqrt(c)*x^2)/((d+e*x^2)*Sqrt(a+b*x^2+c*x^4)), x, 1, -((Sqrt(c)*d-Sqrt(a)*e)*ArcTan((Sqrt(-b +
	// (c*d)/e+(a*e)/d)*x)/Sqrt(a+b*x^2+c*x^4)))/(2*d*e*Sqrt(-b+(c*d)/e+(a*e)/d))+((Sqrt(c)*d +
	// Sqrt(a)*e)*(Sqrt(a)+Sqrt(c)*x^2)*Sqrt((a+b*x^2+c*x^4)/(Sqrt(a)+Sqrt(c)*x^2)^2)*EllipticPi(-(Sqrt(c)*d -
	// Sqrt(a)*e)^2/(4*Sqrt(a)*Sqrt(c)*d*e), 2*ArcTan((c^(1/4)*x)/a^(1/4)), (2 -
	// b/(Sqrt(a)*Sqrt(c)))/4))/(4*a^(1/4)*c^(1/4)*d*e*Sqrt(a+b*x^2+c*x^4))}
	public void test01525() {
		check("Integrate((Sqrt(a)+Sqrt(c)*x^2)/((d+e*x^2)*Sqrt(a+b*x^2+c*x^4)), x)",
				"-((Sqrt(c)*d-Sqrt(a)*e)*ArcTan((Sqrt(-b+(c*d)/e+(a*e)/d)*x)/Sqrt(a+b*x^2+c*x^4)))/(2*d*e*Sqrt(-b+(c*d)/e+(a*e)/d))+((Sqrt(c)*d+Sqrt(a)*e)*(Sqrt(a)+Sqrt(c)*x^2)*Sqrt((a+b*x^2+c*x^4)/(Sqrt(a)+Sqrt(c)*x^2)^2)*EllipticPi(-(Sqrt(c)*d-Sqrt(a)*e)^2/(4*Sqrt(a)*Sqrt(c)*d*e), 2*ArcTan((c^(1/4)*x)/a^(1/4)), (2-b/(Sqrt(a)*Sqrt(c)))/4))/(4*a^(1/4)*c^(1/4)*d*e*Sqrt(a+b*x^2+c*x^4))");

	}

	// {(1+Sqrt(c/a)*x^2)/((d+e*x^2)*Sqrt(a+b*x^2+c*x^4)), x, 1, -((Sqrt(c/a)*d-e)*ArcTan((Sqrt(-b+(c*d)/e +
	// (a*e)/d)*x)/Sqrt(a+b*x^2+c*x^4)))/(2*d*e*Sqrt(-b+(c*d)/e+(a*e)/d))+((Sqrt(c/a)*d+e)*(1 +
	// Sqrt(c/a)*x^2)*Sqrt((a+b*x^2+c*x^4)/(a*(1+Sqrt(c/a)*x^2)^2))*EllipticPi(-(Sqrt(c/a)*d -
	// e)^2/(4*Sqrt(c/a)*d*e), 2*ArcTan((c/a)^(1/4)*x), (2-(b*Sqrt(c/a))/c)/4))/(4*(c/a)^(1/4)*d*e*Sqrt(a+b*x^2 +
	// c*x^4))}
	public void test01526() {
		check("Integrate((1+Sqrt(c/a)*x^2)/((d+e*x^2)*Sqrt(a+b*x^2+c*x^4)), x)",
				"-((Sqrt(c/a)*d-e)*ArcTan((Sqrt(-b+(c*d)/e+(a*e)/d)*x)/Sqrt(a+b*x^2+c*x^4)))/(2*d*e*Sqrt(-b+(c*d)/e+(a*e)/d))+((Sqrt(c/a)*d+e)*(1+Sqrt(c/a)*x^2)*Sqrt((a+b*x^2+c*x^4)/(a*(1+Sqrt(c/a)*x^2)^2))*EllipticPi(-(Sqrt(c/a)*d-e)^2/(4*Sqrt(c/a)*d*e), 2*ArcTan((c/a)^(1/4)*x), (2-(b*Sqrt(c/a))/c)/4))/(4*(c/a)^(1/4)*d*e*Sqrt(a+b*x^2+c*x^4))");

	}

	// {(a*x^3+b*x^6)^(2/3), x, 1, (a*x^3+b*x^6)^(5/3)/(5*b*x^5)}
	public void test01527() {
		check("Integrate((a*x^3+b*x^6)^(2/3), x)", "(a*x^3+b*x^6)^(5/3)/(5*b*x^5)");

	}

	// {(a*x^3+b*x^6)^(-2/3), x, 1, -((a*x^3+b*x^6)^(1/3)/(a*x^2))}
	public void test01528() {
		check("Integrate((a*x^3+b*x^6)^(-2/3), x)", "-((a*x^3+b*x^6)^(1/3)/(a*x^2))");

	}

	// {x^(-1-n*(-1+p))*(b*x^n+c*x^(2*n))^p, x, 1, (b*x^n+c*x^(2*n))^(1+p)/(c*n*(1+p)*x^(n*(1+p)))}
	public void test01529() {
		check("Integrate(x^(-1-n*(-1+p))*(b*x^n+c*x^(2*n))^p, x)", "(b*x^n+c*x^(2*n))^(1+p)/(c*n*(1+p)*x^(n*(1+p)))");

	}

	// {x^(-1-n*(1+2*p))*(b*x^n+c*x^(2*n))^p, x, 1, -((b*x^n+c*x^(2*n))^(1+p)/(b*n*(1+p)*x^(2*n*(1+p))))}
	public void test01530() {
		check("Integrate(x^(-1-n*(1+2*p))*(b*x^n+c*x^(2*n))^p, x)",
				"-((b*x^n+c*x^(2*n))^(1+p)/(b*n*(1+p)*x^(2*n*(1+p))))");

	}

	// {(b+2*c*x)*(a+b*x+c*x^2)^13, x, 1, (a+b*x+c*x^2)^14/14}
	public void test01531() {
		check("Integrate((b+2*c*x)*(a+b*x+c*x^2)^13, x)", "(a+b*x+c*x^2)^14/14");

	}

	// {x*(b+2*c*x^2)*(a+b*x^2+c*x^4)^13, x, 1, (a+b*x^2+c*x^4)^14/28}
	public void test01532() {
		check("Integrate(x*(b+2*c*x^2)*(a+b*x^2+c*x^4)^13, x)", "(a+b*x^2+c*x^4)^14/28");

	}

	// {x^2*(b+2*c*x^3)*(a+b*x^3+c*x^6)^13, x, 1, (a+b*x^3+c*x^6)^14/42}
	public void test01533() {
		check("Integrate(x^2*(b+2*c*x^3)*(a+b*x^3+c*x^6)^13, x)", "(a+b*x^3+c*x^6)^14/42");

	}

	// {(b+2*c*x)*(-a+b*x+c*x^2)^13, x, 1, (a-b*x-c*x^2)^14/14}
	public void test01534() {
		check("Integrate((b+2*c*x)*(-a+b*x+c*x^2)^13, x)", "(a-b*x-c*x^2)^14/14");

	}

	// {x*(b+2*c*x^2)*(-a+b*x^2+c*x^4)^13, x, 1, (a-b*x^2-c*x^4)^14/28}
	public void test01535() {
		check("Integrate(x*(b+2*c*x^2)*(-a+b*x^2+c*x^4)^13, x)", "(a-b*x^2-c*x^4)^14/28");

	}

	// {x^2*(b+2*c*x^3)*(-a+b*x^3+c*x^6)^13, x, 1, (a-b*x^3-c*x^6)^14/42}
	public void test01536() {
		check("Integrate(x^2*(b+2*c*x^3)*(-a+b*x^3+c*x^6)^13, x)", "(a-b*x^3-c*x^6)^14/42");

	}

	// {(b+2*c*x)*(b*x+c*x^2)^13, x, 1, (b*x+c*x^2)^14/14}
	public void test01537() {
		check("Integrate((b+2*c*x)*(b*x+c*x^2)^13, x)", "(b*x+c*x^2)^14/14");

	}

	// {x*(b+2*c*x^2)*(b*x^2+c*x^4)^13, x, 1, (b*x^2+c*x^4)^14/28}
	public void test01538() {
		check("Integrate(x*(b+2*c*x^2)*(b*x^2+c*x^4)^13, x)", "(b*x^2+c*x^4)^14/28");

	}

	// {x^2*(b+2*c*x^3)*(b*x^3+c*x^6)^13, x, 1, (b*x^3+c*x^6)^14/42}
	public void test01539() {
		check("Integrate(x^2*(b+2*c*x^3)*(b*x^3+c*x^6)^13, x)", "(b*x^3+c*x^6)^14/42");

	}

	// {(b+2*c*x)/(a+b*x+c*x^2), x, 1, Log(a+b*x+c*x^2)}
	public void test01540() {
		check("Integrate((b+2*c*x)/(a+b*x+c*x^2), x)", "Log(a+b*x+c*x^2)");

	}

	// {(x*(b+2*c*x^2))/(a+b*x^2+c*x^4), x, 1, Log(a+b*x^2+c*x^4)/2}
	public void test01541() {
		check("Integrate((x*(b+2*c*x^2))/(a+b*x^2+c*x^4), x)", "Log(a+b*x^2+c*x^4)/2");

	}

	// {(x^2*(b+2*c*x^3))/(a+b*x^3+c*x^6), x, 1, Log(a+b*x^3+c*x^6)/3}
	public void test01542() {
		check("Integrate((x^2*(b+2*c*x^3))/(a+b*x^3+c*x^6), x)", "Log(a+b*x^3+c*x^6)/3");

	}

	// {(b+2*c*x)/(a+b*x+c*x^2)^8, x, 1, -1/(7*(a+b*x+c*x^2)^7)}
	public void test01543() {
		check("Integrate((b+2*c*x)/(a+b*x+c*x^2)^8, x)", "-1/(7*(a+b*x+c*x^2)^7)");

	}

	// {(x*(b+2*c*x^2))/(a+b*x^2+c*x^4)^8, x, 1, -1/(14*(a+b*x^2+c*x^4)^7)}
	public void test01544() {
		check("Integrate((x*(b+2*c*x^2))/(a+b*x^2+c*x^4)^8, x)", "-1/(14*(a+b*x^2+c*x^4)^7)");

	}

	// {(x^2*(b+2*c*x^3))/(a+b*x^3+c*x^6)^8, x, 1, -1/(21*(a+b*x^3+c*x^6)^7)}
	public void test01545() {
		check("Integrate((x^2*(b+2*c*x^3))/(a+b*x^3+c*x^6)^8, x)", "-1/(21*(a+b*x^3+c*x^6)^7)");

	}

	// {(b+2*c*x)/(-a+b*x+c*x^2), x, 1, Log(a-b*x-c*x^2)}
	public void test01546() {
		check("Integrate((b+2*c*x)/(-a+b*x+c*x^2), x)", "Log(a-b*x-c*x^2)");

	}

	// {(x*(b+2*c*x^2))/(-a+b*x^2+c*x^4), x, 1, Log(a-b*x^2-c*x^4)/2}
	public void test01547() {
		check("Integrate((x*(b+2*c*x^2))/(-a+b*x^2+c*x^4), x)", "Log(a-b*x^2-c*x^4)/2");

	}

	// {(x^2*(b+2*c*x^3))/(-a+b*x^3+c*x^6), x, 1, Log(a-b*x^3-c*x^6)/3}
	public void test01548() {
		check("Integrate((x^2*(b+2*c*x^3))/(-a+b*x^3+c*x^6), x)", "Log(a-b*x^3-c*x^6)/3");

	}

	// {(b+2*c*x)/(-a+b*x+c*x^2)^8, x, 1, 1/(7*(a-b*x-c*x^2)^7)}
	public void test01549() {
		check("Integrate((b+2*c*x)/(-a+b*x+c*x^2)^8, x)", "1/(7*(a-b*x-c*x^2)^7)");

	}

	// {(x*(b+2*c*x^2))/(-a+b*x^2+c*x^4)^8, x, 1, 1/(14*(a-b*x^2-c*x^4)^7)}
	public void test01550() {
		check("Integrate((x*(b+2*c*x^2))/(-a+b*x^2+c*x^4)^8, x)", "1/(14*(a-b*x^2-c*x^4)^7)");

	}

	// {(x^2*(b+2*c*x^3))/(-a+b*x^3+c*x^6)^8, x, 1, 1/(21*(a-b*x^3-c*x^6)^7)}
	public void test01551() {
		check("Integrate((x^2*(b+2*c*x^3))/(-a+b*x^3+c*x^6)^8, x)", "1/(21*(a-b*x^3-c*x^6)^7)");

	}

	// {(b+2*c*x)/(b*x+c*x^2), x, 1, Log(b*x+c*x^2)}
	public void test01552() {
		check("Integrate((b+2*c*x)/(b*x+c*x^2), x)", "Log(b*x+c*x^2)");

	}

	// {(x*(b+2*c*x^2))/(b*x^2+c*x^4), x, 1, Log(b*x^2+c*x^4)/2}
	public void test01553() {
		check("Integrate((x*(b+2*c*x^2))/(b*x^2+c*x^4), x)", "Log(b*x^2+c*x^4)/2");

	}

	// {(x^2*(b+2*c*x^3))/(b*x^3+c*x^6), x, 1, Log(b*x^3+c*x^6)/3}
	public void test01554() {
		check("Integrate((x^2*(b+2*c*x^3))/(b*x^3+c*x^6), x)", "Log(b*x^3+c*x^6)/3");

	}

	// {(b+2*c*x)/(b*x+c*x^2)^8, x, 1, -1/(7*(b*x+c*x^2)^7)}
	public void test01555() {
		check("Integrate((b+2*c*x)/(b*x+c*x^2)^8, x)", "-1/(7*(b*x+c*x^2)^7)");

	}

	// {(x*(b+2*c*x^2))/(b*x^2+c*x^4)^8, x, 1, -1/(14*(b*x^2+c*x^4)^7)}
	public void test01556() {
		check("Integrate((x*(b+2*c*x^2))/(b*x^2+c*x^4)^8, x)", "-1/(14*(b*x^2+c*x^4)^7)");

	}

	// {(x^2*(b+2*c*x^3))/(b*x^3+c*x^6)^8, x, 1, -1/(21*(b*x^3+c*x^6)^7)}
	public void test01557() {
		check("Integrate((x^2*(b+2*c*x^3))/(b*x^3+c*x^6)^8, x)", "-1/(21*(b*x^3+c*x^6)^7)");

	}

	// {(b+2*c*x)*(a+b*x+c*x^2)^p, x, 1, (a+b*x+c*x^2)^(1+p)/(1+p)}
	public void test01558() {
		check("Integrate((b+2*c*x)*(a+b*x+c*x^2)^p, x)", "(a+b*x+c*x^2)^(1+p)/(1+p)");

	}

	// {x*(b+2*c*x^2)*(a+b*x^2+c*x^4)^p, x, 1, (a+b*x^2+c*x^4)^(1+p)/(2*(1+p))}
	public void test01559() {
		check("Integrate(x*(b+2*c*x^2)*(a+b*x^2+c*x^4)^p, x)", "(a+b*x^2+c*x^4)^(1+p)/(2*(1+p))");

	}

	// {x^2*(b+2*c*x^3)*(a+b*x^3+c*x^6)^p, x, 1, (a+b*x^3+c*x^6)^(1+p)/(3*(1+p))}
	public void test01560() {
		check("Integrate(x^2*(b+2*c*x^3)*(a+b*x^3+c*x^6)^p, x)", "(a+b*x^3+c*x^6)^(1+p)/(3*(1+p))");

	}

	// {(b+2*c*x)*(-a+b*x+c*x^2)^p, x, 1, (-a+b*x+c*x^2)^(1+p)/(1+p)}
	public void test01561() {
		check("Integrate((b+2*c*x)*(-a+b*x+c*x^2)^p, x)", "(-a+b*x+c*x^2)^(1+p)/(1+p)");

	}

	// {x*(b+2*c*x^2)*(-a+b*x^2+c*x^4)^p, x, 1, (-a+b*x^2+c*x^4)^(1+p)/(2*(1+p))}
	public void test01562() {
		check("Integrate(x*(b+2*c*x^2)*(-a+b*x^2+c*x^4)^p, x)", "(-a+b*x^2+c*x^4)^(1+p)/(2*(1+p))");

	}

	// {x^2*(b+2*c*x^3)*(-a+b*x^3+c*x^6)^p, x, 1, (-a+b*x^3+c*x^6)^(1+p)/(3*(1+p))}
	public void test01563() {
		check("Integrate(x^2*(b+2*c*x^3)*(-a+b*x^3+c*x^6)^p, x)", "(-a+b*x^3+c*x^6)^(1+p)/(3*(1+p))");

	}

	// {(b+2*c*x)*(b*x+c*x^2)^p, x, 1, (b*x+c*x^2)^(1+p)/(1+p)}
	public void test01564() {
		check("Integrate((b+2*c*x)*(b*x+c*x^2)^p, x)", "(b*x+c*x^2)^(1+p)/(1+p)");

	}

	// {x*(b+2*c*x^2)*(b*x^2+c*x^4)^p, x, 1, (b*x^2+c*x^4)^(1+p)/(2*(1+p))}
	public void test01565() {
		check("Integrate(x*(b+2*c*x^2)*(b*x^2+c*x^4)^p, x)", "(b*x^2+c*x^4)^(1+p)/(2*(1+p))");

	}

	// {x^2*(b+2*c*x^3)*(b*x^3+c*x^6)^p, x, 1, (b*x^3+c*x^6)^(1+p)/(3*(1+p))}
	public void test01566() {
		check("Integrate(x^2*(b+2*c*x^3)*(b*x^3+c*x^6)^p, x)", "(b*x^3+c*x^6)^(1+p)/(3*(1+p))");

	}

	// {(a+b*x^n+c*x^(2*n))^p*(a+b*(1+n+n*p)*x^n+c*(1+2*n*(1+p))*x^(2*n)), x, 1, x*(a+b*x^n +
	// c*x^(2*n))^(1+p)}
	public void test01567() {
		check("Integrate((a+b*x^n+c*x^(2*n))^p*(a+b*(1+n+n*p)*x^n+c*(1+2*n*(1+p))*x^(2*n)), x)",
				"x*(a+b*x^n+c*x^(2*n))^(1+p)");

	}

	// {(x^(-1+n/4)*(-(a*h)+c*f*x^(n/4)+c*g*x^((3*n)/4)+c*h*x^n))/(a+c*x^n)^(3/2), x, 1, (-2*(a*g +
	// 2*a*h*x^(n/4)-c*f*x^(n/2)))/(a*n*Sqrt(a+c*x^n))}
	public void test01568() {
		check("Integrate((x^(-1+n/4)*(-(a*h)+c*f*x^(n/4)+c*g*x^((3*n)/4)+c*h*x^n))/(a+c*x^n)^(3/2), x)",
				"(-2*(a*g+2*a*h*x^(n/4)-c*f*x^(n/2)))/(a*n*Sqrt(a+c*x^n))");

	}

	// {(x^(-1+n/2)*(-(a*h)+c*f*x^(n/2)+c*g*x^((3*n)/2)+c*h*x^(2*n)))/(a+b*x^n+c*x^(2*n))^(3/2), x, 1,
	// (-2*(c*(b*f-2*a*g)+(b^2-4*a*c)*h*x^(n/2)+c*(2*c*f-b*g)*x^n))/((b^2-4*a*c)*n*Sqrt(a+b*x^n +
	// c*x^(2*n)))}
	public void test01569() {
		check("Integrate((x^(-1+n/2)*(-(a*h)+c*f*x^(n/2)+c*g*x^((3*n)/2)+c*h*x^(2*n)))/(a+b*x^n+c*x^(2*n))^(3/2), x)",
				"(-2*(c*(b*f-2*a*g)+(b^2-4*a*c)*h*x^(n/2)+c*(2*c*f-b*g)*x^n))/((b^2-4*a*c)*n*Sqrt(a+b*x^n+c*x^(2*n)))");

	}

	// {(g*x)^m*(a+b*x^n+c*x^(2*n))^p*(a*(1+m)+b*(1+m+n+n*p)*x^n+c*(1+m+2*n*(1+p))*x^(2*n)), x, 1,
	// ((g*x)^(1+m)*(a+b*x^n+c*x^(2*n))^(1+p))/g}
	public void test01570() {
		check("Integrate((g*x)^m*(a+b*x^n+c*x^(2*n))^p*(a*(1+m)+b*(1+m+n+n*p)*x^n+c*(1+m+2*n*(1+p))*x^(2*n)), x)",
				"((g*x)^(1+m)*(a+b*x^n+c*x^(2*n))^(1+p))/g");

	}

	// {a*x^2+b*x^3+c*x^4, x, 1, (a*x^3)/3+(b*x^4)/4+(c*x^5)/5}
	public void test01571() {
		check("Integrate(a*x^2+b*x^3+c*x^4, x)", "(a*x^3)/3+(b*x^4)/4+(c*x^5)/5");

	}

	// {x^4/(a*x^2+b*x^3+c*x^4)^(3/2), x, 1, (2*x*(2*a+b*x))/((b^2-4*a*c)*Sqrt(a*x^2+b*x^3+c*x^4))}
	public void test01572() {
		check("Integrate(x^4/(a*x^2+b*x^3+c*x^4)^(3/2), x)", "(2*x*(2*a+b*x))/((b^2-4*a*c)*Sqrt(a*x^2+b*x^3+c*x^4))");

	}

	// {x^3/(a*x^2+b*x^3+c*x^4)^(3/2), x, 1, (-2*x*(b+2*c*x))/((b^2-4*a*c)*Sqrt(a*x^2+b*x^3+c*x^4))}
	public void test01573() {
		check("Integrate(x^3/(a*x^2+b*x^3+c*x^4)^(3/2), x)", "(-2*x*(b+2*c*x))/((b^2-4*a*c)*Sqrt(a*x^2+b*x^3+c*x^4))");

	}

	// {a*x+b*x^3+c*x^5, x, 1, (a*x^2)/2+(b*x^4)/4+(c*x^6)/6}
	public void test01574() {
		check("Integrate(a*x+b*x^3+c*x^5, x)", "(a*x^2)/2+(b*x^4)/4+(c*x^6)/6");

	}

	// {x^((3*(-1+n))/2)/(a*x^(-1+n)+b*x^n+c*x^(1+n))^(3/2), x, 1, (-2*x^((-1+n)/2)*(b+2*c*x))/((b^2 -
	// 4*a*c)*Sqrt(a*x^(-1+n)+b*x^n+c*x^(1+n)))}
	public void test01575() {
		check("Integrate(x^((3*(-1+n))/2)/(a*x^(-1+n)+b*x^n+c*x^(1+n))^(3/2), x)",
				"(-2*x^((-1+n)/2)*(b+2*c*x))/((b^2-4*a*c)*Sqrt(a*x^(-1+n)+b*x^n+c*x^(1+n)))");

	}

	// {a^3+3*a^2*b*x+3*a*b^2*x^2+b^3*x^3, x, 1, a^3*x+(3*a^2*b*x^2)/2+a*b^2*x^3+(b^3*x^4)/4}
	public void test01576() {
		check("Integrate(a^3+3*a^2*b*x+3*a*b^2*x^2+b^3*x^3, x)", "a^3*x+(3*a^2*b*x^2)/2+a*b^2*x^3+(b^3*x^4)/4");

	}

	// {3*a*b+3*b^2*x+3*b*c*x^2+c^2*x^3, x, 1, 3*a*b*x+(3*b^2*x^2)/2+b*c*x^3+(c^2*x^4)/4}
	public void test01577() {
		check("Integrate(3*a*b+3*b^2*x+3*b*c*x^2+c^2*x^3, x)", "3*a*b*x+(3*b^2*x^2)/2+b*c*x^3+(c^2*x^4)/4");

	}

	// {a*c*e+(b*c*e+a*d*e+a*c*f)*x+(b*d*e+b*c*f+a*d*f)*x^2+b*d*f*x^3, x, 1, a*c*e*x+((b*c*e+a*d*e +
	// a*c*f)*x^2)/2+((b*d*e+b*c*f+a*d*f)*x^3)/3+(b*d*f*x^4)/4}
	public void test01578() {
		check("Integrate(a*c*e+(b*c*e+a*d*e+a*c*f)*x+(b*d*e+b*c*f+a*d*f)*x^2+b*d*f*x^3, x)",
				"a*c*e*x+((b*c*e+a*d*e+a*c*f)*x^2)/2+((b*d*e+b*c*f+a*d*f)*x^3)/3+(b*d*f*x^4)/4");

	}

	// {4*a*c+4*c^2*x^2+4*c*d*x^3+d^2*x^4, x, 1, 4*a*c*x+(4*c^2*x^3)/3+c*d*x^4+(d^2*x^5)/5}
	public void test01579() {
		check("Integrate(4*a*c+4*c^2*x^2+4*c*d*x^3+d^2*x^4, x)", "4*a*c*x+(4*c^2*x^3)/3+c*d*x^4+(d^2*x^5)/5");

	}

	// {8*a*e^2-d^3*x+8*d*e^2*x^3+8*e^3*x^4, x, 1, 8*a*e^2*x-(d^3*x^2)/2+2*d*e^2*x^4+(8*e^3*x^5)/5}
	public void test01580() {
		check("Integrate(8*a*e^2-d^3*x+8*d*e^2*x^3+8*e^3*x^4, x)", "8*a*e^2*x-(d^3*x^2)/2+2*d*e^2*x^4+(8*e^3*x^5)/5");

	}

	// {8+8*x-x^3+8*x^4, x, 1, 8*x+4*x^2-x^4/4+(8*x^5)/5}
	public void test01581() {
		check("Integrate(8+8*x-x^3+8*x^4, x)", "8*x+4*x^2-x^4/4+(8*x^5)/5");

	}

	// {1+4*x+4*x^2+4*x^4, x, 1, x+2*x^2+(4*x^3)/3+(4*x^5)/5}
	public void test01582() {
		check("Integrate(1+4*x+4*x^2+4*x^4, x)", "x+2*x^2+(4*x^3)/3+(4*x^5)/5");

	}

	// {8+24*x+8*x^2-15*x^3+8*x^4, x, 1, 8*x+12*x^2+(8*x^3)/3-(15*x^4)/4+(8*x^5)/5}
	public void test01583() {
		check("Integrate(8+24*x+8*x^2-15*x^3+8*x^4, x)", "8*x+12*x^2+(8*x^3)/3-(15*x^4)/4+(8*x^5)/5");

	}

	// {3-19*x^2+32*x^4-16*x^6, x, 1, 3*x-(19*x^3)/3+(32*x^5)/5-(16*x^7)/7}
	public void test01584() {
		check("Integrate(3-19*x^2+32*x^4-16*x^6, x)", "3*x-(19*x^3)/3+(32*x^5)/5-(16*x^7)/7");

	}

	// {a+8*x-8*x^2+4*x^3-x^4, x, 1, a*x+4*x^2-(8*x^3)/3+x^4-x^5/5}
	public void test01585() {
		check("Integrate(a+8*x-8*x^2+4*x^3-x^4, x)", "a*x+4*x^2-(8*x^3)/3+x^4-x^5/5");

	}

	// {(b+2*c*x)*(b*x+c*x^2)^13, x, 1, (b*x+c*x^2)^14/14}
	public void test01586() {
		check("Integrate((b+2*c*x)*(b*x+c*x^2)^13, x)", "(b*x+c*x^2)^14/14");

	}

	// {x^14*(b+2*c*x^2)*(b*x+c*x^3)^13, x, 1, (x^14*(b*x+c*x^3)^14)/28}
	public void test01587() {
		check("Integrate(x^14*(b+2*c*x^2)*(b*x+c*x^3)^13, x)", "(x^14*(b*x+c*x^3)^14)/28");

	}

	// {x^28*(b+2*c*x^3)*(b*x+c*x^4)^13, x, 1, (x^28*(b*x+c*x^4)^14)/42}
	public void test01588() {
		check("Integrate(x^28*(b+2*c*x^3)*(b*x+c*x^4)^13, x)", "(x^28*(b*x+c*x^4)^14)/42");

	}

	// {(b+2*c*x)/(b*x+c*x^2), x, 1, Log(b*x+c*x^2)}
	public void test01589() {
		check("Integrate((b+2*c*x)/(b*x+c*x^2), x)", "Log(b*x+c*x^2)");

	}

	// {(b+2*c*x)/(b*x+c*x^2)^8, x, 1, -1/(7*(b*x+c*x^2)^7)}
	public void test01590() {
		check("Integrate((b+2*c*x)/(b*x+c*x^2)^8, x)", "-1/(7*(b*x+c*x^2)^7)");

	}

	// {(b+2*c*x^2)/(x^7*(b*x+c*x^3)^8), x, 1, -1/(14*x^7*(b*x+c*x^3)^7)}
	public void test01591() {
		check("Integrate((b+2*c*x^2)/(x^7*(b*x+c*x^3)^8), x)", "-1/(14*x^7*(b*x+c*x^3)^7)");

	}

	// {(b+2*c*x^3)/(x^14*(b*x+c*x^4)^8), x, 1, -1/(21*x^14*(b*x+c*x^4)^7)}
	public void test01592() {
		check("Integrate((b+2*c*x^3)/(x^14*(b*x+c*x^4)^8), x)", "-1/(21*x^14*(b*x+c*x^4)^7)");

	}

	// {(b+2*c*x)*(b*x+c*x^2)^p, x, 1, (b*x+c*x^2)^(1+p)/(1+p)}
	public void test01593() {
		check("Integrate((b+2*c*x)*(b*x+c*x^2)^p, x)", "(b*x+c*x^2)^(1+p)/(1+p)");

	}

	// {x^(1+p)*(b+2*c*x^2)*(b*x+c*x^3)^p, x, 1, (x^(1+p)*(b*x+c*x^3)^(1+p))/(2*(1+p))}
	public void test01594() {
		check("Integrate(x^(1+p)*(b+2*c*x^2)*(b*x+c*x^3)^p, x)", "(x^(1+p)*(b*x+c*x^3)^(1+p))/(2*(1+p))");

	}

	// {x^(2*(1+p))*(b+2*c*x^3)*(b*x+c*x^4)^p, x, 1, (x^(2*(1+p))*(b*x+c*x^4)^(1+p))/(3*(1+p))}
	public void test01595() {
		check("Integrate(x^(2*(1+p))*(b+2*c*x^3)*(b*x+c*x^4)^p, x)", "(x^(2*(1+p))*(b*x+c*x^4)^(1+p))/(3*(1+p))");

	}

	// {x^((-1+n)*(1+p))*(b+2*c*x^n)*(b*x+c*x^(1+n))^p, x, 1, (b*x+c*x^(1+n))^(1+p)/(n*(1+p)*x^((1 -
	// n)*(1+p)))}
	public void test01596() {
		check("Integrate(x^((-1+n)*(1+p))*(b+2*c*x^n)*(b*x+c*x^(1+n))^p, x)",
				"(b*x+c*x^(1+n))^(1+p)/(n*(1+p)*x^((1-n)*(1+p)))");

	}

	// {(b+2*c*x+3*d*x^2)*(a+b*x+c*x^2+d*x^3)^n, x, 1, (a+b*x+c*x^2+d*x^3)^(1+n)/(1+n)}
	public void test01597() {
		check("Integrate((b+2*c*x+3*d*x^2)*(a+b*x+c*x^2+d*x^3)^n, x)", "(a+b*x+c*x^2+d*x^3)^(1+n)/(1+n)");

	}

	// {(b+2*c*x+3*d*x^2)*(b*x+c*x^2+d*x^3)^n, x, 1, (b*x+c*x^2+d*x^3)^(1+n)/(1+n)}
	public void test01598() {
		check("Integrate((b+2*c*x+3*d*x^2)*(b*x+c*x^2+d*x^3)^n, x)", "(b*x+c*x^2+d*x^3)^(1+n)/(1+n)");

	}

	// {x^n*(b+c*x+d*x^2)^n*(b+2*c*x+3*d*x^2), x, 1, (x^(1+n)*(b+c*x+d*x^2)^(1+n))/(1+n)}
	public void test01599() {
		check("Integrate(x^n*(b+c*x+d*x^2)^n*(b+2*c*x+3*d*x^2), x)", "(x^(1+n)*(b+c*x+d*x^2)^(1+n))/(1+n)");

	}

	// {(b+3*d*x^2)*(a+b*x+d*x^3)^n, x, 1, (a+b*x+d*x^3)^(1+n)/(1+n)}
	public void test01600() {
		check("Integrate((b+3*d*x^2)*(a+b*x+d*x^3)^n, x)", "(a+b*x+d*x^3)^(1+n)/(1+n)");

	}

	// {(b+3*d*x^2)*(b*x+d*x^3)^n, x, 1, (b*x+d*x^3)^(1+n)/(1+n)}
	public void test01601() {
		check("Integrate((b+3*d*x^2)*(b*x+d*x^3)^n, x)", "(b*x+d*x^3)^(1+n)/(1+n)");

	}

	// {x^n*(b+d*x^2)^n*(b+3*d*x^2), x, 1, (x^(1+n)*(b+d*x^2)^(1+n))/(1+n)}
	public void test01602() {
		check("Integrate(x^n*(b+d*x^2)^n*(b+3*d*x^2), x)", "(x^(1+n)*(b+d*x^2)^(1+n))/(1+n)");

	}

	// {(2*c*x+3*d*x^2)*(a+c*x^2+d*x^3)^n, x, 1, (a+c*x^2+d*x^3)^(1+n)/(1+n)}
	public void test01603() {
		check("Integrate((2*c*x+3*d*x^2)*(a+c*x^2+d*x^3)^n, x)", "(a+c*x^2+d*x^3)^(1+n)/(1+n)");

	}

	// {(2*c*x+3*d*x^2)*(c*x^2+d*x^3)^n, x, 1, (c*x^2+d*x^3)^(1+n)/(1+n)}
	public void test01604() {
		check("Integrate((2*c*x+3*d*x^2)*(c*x^2+d*x^3)^n, x)", "(c*x^2+d*x^3)^(1+n)/(1+n)");

	}

	// {x^n*(c*x+d*x^2)^n*(2*c*x+3*d*x^2), x, 1, (x^(1+n)*(c*x+d*x^2)^(1+n))/(1+n)}
	public void test01605() {
		check("Integrate(x^n*(c*x+d*x^2)^n*(2*c*x+3*d*x^2), x)", "(x^(1+n)*(c*x+d*x^2)^(1+n))/(1+n)");

	}

	// {x^(2*n)*(c+d*x)^n*(2*c*x+3*d*x^2), x, 1, (x^(2*(1+n))*(c+d*x)^(1+n))/(1+n)}
	public void test01606() {
		check("Integrate(x^(2*n)*(c+d*x)^n*(2*c*x+3*d*x^2), x)", "(x^(2*(1+n))*(c+d*x)^(1+n))/(1+n)");

	}

	// {x*(2*c+3*d*x)*(a+c*x^2+d*x^3)^n, x, 1, (a+c*x^2+d*x^3)^(1+n)/(1+n)}
	public void test01607() {
		check("Integrate(x*(2*c+3*d*x)*(a+c*x^2+d*x^3)^n, x)", "(a+c*x^2+d*x^3)^(1+n)/(1+n)");

	}

	// {x*(2*c+3*d*x)*(c*x^2+d*x^3)^n, x, 1, (c*x^2+d*x^3)^(1+n)/(1+n)}
	public void test01608() {
		check("Integrate(x*(2*c+3*d*x)*(c*x^2+d*x^3)^n, x)", "(c*x^2+d*x^3)^(1+n)/(1+n)");

	}

	// {(b+2*c*x+3*d*x^2)*(a+b*x+c*x^2+d*x^3)^7, x, 1, (a+b*x+c*x^2+d*x^3)^8/8}
	public void test01609() {
		check("Integrate((b+2*c*x+3*d*x^2)*(a+b*x+c*x^2+d*x^3)^7, x)", "(a+b*x+c*x^2+d*x^3)^8/8");

	}

	// {(b+2*c*x+3*d*x^2)*(b*x+c*x^2+d*x^3)^7, x, 1, (b*x+c*x^2+d*x^3)^8/8}
	public void test01610() {
		check("Integrate((b+2*c*x+3*d*x^2)*(b*x+c*x^2+d*x^3)^7, x)", "(b*x+c*x^2+d*x^3)^8/8");

	}

	// {x^7*(b+c*x+d*x^2)^7*(b+2*c*x+3*d*x^2), x, 1, (x^8*(b+c*x+d*x^2)^8)/8}
	public void test01611() {
		check("Integrate(x^7*(b+c*x+d*x^2)^7*(b+2*c*x+3*d*x^2), x)", "(x^8*(b+c*x+d*x^2)^8)/8");

	}

	// {(b+3*d*x^2)*(a+b*x+d*x^3)^7, x, 1, (a+b*x+d*x^3)^8/8}
	public void test01612() {
		check("Integrate((b+3*d*x^2)*(a+b*x+d*x^3)^7, x)", "(a+b*x+d*x^3)^8/8");

	}

	// {x^7*(b+d*x^2)^7*(b+3*d*x^2), x, 1, (x^8*(b+d*x^2)^8)/8}
	public void test01613() {
		check("Integrate(x^7*(b+d*x^2)^7*(b+3*d*x^2), x)", "(x^8*(b+d*x^2)^8)/8");

	}

	// {(b+3*d*x^2)*(b*x+d*x^3)^7, x, 1, (b*x+d*x^3)^8/8}
	public void test01614() {
		check("Integrate((b+3*d*x^2)*(b*x+d*x^3)^7, x)", "(b*x+d*x^3)^8/8");

	}

	// {(2*c*x+3*d*x^2)*(a+c*x^2+d*x^3)^7, x, 1, (a+c*x^2+d*x^3)^8/8}
	public void test01615() {
		check("Integrate((2*c*x+3*d*x^2)*(a+c*x^2+d*x^3)^7, x)", "(a+c*x^2+d*x^3)^8/8");

	}

	// {(2*c*x+3*d*x^2)*(c*x^2+d*x^3)^7, x, 1, (c*x^2+d*x^3)^8/8}
	public void test01616() {
		check("Integrate((2*c*x+3*d*x^2)*(c*x^2+d*x^3)^7, x)", "(c*x^2+d*x^3)^8/8");

	}

	// {x^7*(c*x+d*x^2)^7*(2*c*x+3*d*x^2), x, 1, (x^8*(c*x+d*x^2)^8)/8}
	public void test01617() {
		check("Integrate(x^7*(c*x+d*x^2)^7*(2*c*x+3*d*x^2), x)", "(x^8*(c*x+d*x^2)^8)/8");

	}

	// {x^14*(c+d*x)^7*(2*c*x+3*d*x^2), x, 1, (x^16*(c+d*x)^8)/8}
	public void test01618() {
		check("Integrate(x^14*(c+d*x)^7*(2*c*x+3*d*x^2), x)", "(x^16*(c+d*x)^8)/8");

	}

	// {x*(2*c+3*d*x)*(a+c*x^2+d*x^3)^7, x, 1, (a+c*x^2+d*x^3)^8/8}
	public void test01619() {
		check("Integrate(x*(2*c+3*d*x)*(a+c*x^2+d*x^3)^7, x)", "(a+c*x^2+d*x^3)^8/8");

	}

	// {x*(2*c+3*d*x)*(c*x^2+d*x^3)^7, x, 1, (c*x^2+d*x^3)^8/8}
	public void test01620() {
		check("Integrate(x*(2*c+3*d*x)*(c*x^2+d*x^3)^7, x)", "(c*x^2+d*x^3)^8/8");

	}

	// {x^8*(2*c+3*d*x)*(c*x+d*x^2)^7, x, 1, (x^8*(c*x+d*x^2)^8)/8}
	public void test01621() {
		check("Integrate(x^8*(2*c+3*d*x)*(c*x+d*x^2)^7, x)", "(x^8*(c*x+d*x^2)^8)/8");

	}

	// {x^15*(c+d*x)^7*(2*c+3*d*x), x, 1, (x^16*(c+d*x)^8)/8}
	public void test01622() {
		check("Integrate(x^15*(c+d*x)^7*(2*c+3*d*x), x)", "(x^16*(c+d*x)^8)/8");

	}

	// {(-4+4*x+x^2)*(5-12*x+6*x^2+x^3), x, 1, (5-12*x+6*x^2+x^3)^2/6}
	public void test01623() {
		check("Integrate((-4+4*x+x^2)*(5-12*x+6*x^2+x^3), x)", "(5-12*x+6*x^2+x^3)^2/6");

	}

	// {(2*x+x^3)*(1+4*x^2+x^4), x, 1, (1+4*x^2+x^4)^2/8}
	public void test01624() {
		check("Integrate((2*x+x^3)*(1+4*x^2+x^4), x)", "(1+4*x^2+x^4)^2/8");

	}

	// {(2-x^2)/(1-6*x+x^3)^5, x, 1, 1/(12*(1-6*x+x^3)^4)}
	public void test01625() {
		check("Integrate((2-x^2)/(1-6*x+x^3)^5, x)", "1/(12*(1-6*x+x^3)^4)");

	}

	// {(2*x+x^2)/(4+3*x^2+x^3), x, 1, Log(4+3*x^2+x^3)/3}
	public void test01626() {
		check("Integrate((2*x+x^2)/(4+3*x^2+x^3), x)", "Log(4+3*x^2+x^3)/3");

	}

	// {(1+x+x^3)/(4*x+2*x^2+x^4), x, 1, Log(4*x+2*x^2+x^4)/4}
	public void test01627() {
		check("Integrate((1+x+x^3)/(4*x+2*x^2+x^4), x)", "Log(4*x+2*x^2+x^4)/4");

	}

	// {(-1+4*x^5)/(1+x+x^5)^2, x, 1, -(x/(1+x+x^5))}
	public void test01628() {
		check("Integrate((-1+4*x^5)/(1+x+x^5)^2, x)", "-(x/(1+x+x^5))");

	}

	// {x^m*(a+b*x+c*x^2+d*x^3)^p*(a*(1+m)+x*(b*(2+m+p)+x*(c*(3+m+2*p)+d*(4+m+3*p)*x))), x, 1,
	// x^(1+m)*(a+b*x+c*x^2+d*x^3)^(1+p)}
	public void test01629() {
		check("Integrate(x^m*(a+b*x+c*x^2+d*x^3)^p*(a*(1+m)+x*(b*(2+m+p)+x*(c*(3+m+2*p)+d*(4+m+3*p)*x))), x)",
				"x^(1+m)*(a+b*x+c*x^2+d*x^3)^(1+p)");

	}

	// {x^2*(a+b*x+c*x^2+d*x^3)^p*(3*a+b*(4+p)*x+c*(5+2*p)*x^2+d*(6+3*p)*x^3), x, 1, x^3*(a+b*x +
	// c*x^2+d*x^3)^(1+p)}
	public void test01630() {
		check("Integrate(x^2*(a+b*x+c*x^2+d*x^3)^p*(3*a+b*(4+p)*x+c*(5+2*p)*x^2+d*(6+3*p)*x^3), x)",
				"x^3*(a+b*x+c*x^2+d*x^3)^(1+p)");

	}

	// {x*(a+b*x+c*x^2+d*x^3)^p*(2*a+b*(3+p)*x+c*(4+2*p)*x^2+d*(5+3*p)*x^3), x, 1, x^2*(a+b*x +
	// c*x^2+d*x^3)^(1+p)}
	public void test01631() {
		check("Integrate(x*(a+b*x+c*x^2+d*x^3)^p*(2*a+b*(3+p)*x+c*(4+2*p)*x^2+d*(5+3*p)*x^3), x)",
				"x^2*(a+b*x+c*x^2+d*x^3)^(1+p)");

	}

	// {(a+b*x+c*x^2+d*x^3)^p*(a+b*(2+p)*x+c*(3+2*p)*x^2+d*(4+3*p)*x^3), x, 1, x*(a+b*x+c*x^2 +
	// d*x^3)^(1+p)}
	public void test01632() {
		check("Integrate((a+b*x+c*x^2+d*x^3)^p*(a+b*(2+p)*x+c*(3+2*p)*x^2+d*(4+3*p)*x^3), x)",
				"x*(a+b*x+c*x^2+d*x^3)^(1+p)");

	}

	// {((a+b*x+c*x^2+d*x^3)^p*(b*(1+p)*x+c*(2+2*p)*x^2+d*(3+3*p)*x^3))/x, x, 1, (a+b*x+c*x^2 +
	// d*x^3)^(1+p)}
	public void test01633() {
		check("Integrate(((a+b*x+c*x^2+d*x^3)^p*(b*(1+p)*x+c*(2+2*p)*x^2+d*(3+3*p)*x^3))/x, x)",
				"(a+b*x+c*x^2+d*x^3)^(1+p)");

	}

	// {((a+b*x+c*x^2+d*x^3)^p*(-a+b*p*x+c*(1+2*p)*x^2+d*(2+3*p)*x^3))/x^2, x, 1, (a+b*x+c*x^2 +
	// d*x^3)^(1+p)/x}
	public void test01634() {
		check("Integrate(((a+b*x+c*x^2+d*x^3)^p*(-a+b*p*x+c*(1+2*p)*x^2+d*(2+3*p)*x^3))/x^2, x)",
				"(a+b*x+c*x^2+d*x^3)^(1+p)/x");

	}

	// {((a+b*x+c*x^2+d*x^3)^p*(-2*a+b*(-1+p)*x+2*c*p*x^2+d*(1+3*p)*x^3))/x^3, x, 1, (a+b*x+c*x^2 +
	// d*x^3)^(1+p)/x^2}
	public void test01635() {
		check("Integrate(((a+b*x+c*x^2+d*x^3)^p*(-2*a+b*(-1+p)*x+2*c*p*x^2+d*(1+3*p)*x^3))/x^3, x)",
				"(a+b*x+c*x^2+d*x^3)^(1+p)/x^2");

	}

	// {((a+b*x+c*x^2+d*x^3)^p*(-3*a+b*(-2+p)*x+c*(-1+2*p)*x^2+3*d*p*x^3))/x^4, x, 1, (a+b*x+c*x^2 +
	// d*x^3)^(1+p)/x^3}
	public void test01636() {
		check("Integrate(((a+b*x+c*x^2+d*x^3)^p*(-3*a+b*(-2+p)*x+c*(-1+2*p)*x^2+3*d*p*x^3))/x^4, x)",
				"(a+b*x+c*x^2+d*x^3)^(1+p)/x^3");

	}

	// {(-1+2*x)^(-1)-(1+2*x)^(-1), x, 1, Log(1-2*x)/2-Log(1+2*x)/2}
	public void test01637() {
		check("Integrate((-1+2*x)^(-1)-(1+2*x)^(-1), x)", "Log(1-2*x)/2-Log(1+2*x)/2");

	}

	// {x/(1-x^2)^5, x, 1, 1/(8*(1-x^2)^4)}
	public void test01638() {
		check("Integrate(x/(1-x^2)^5, x)", "1/(8*(1-x^2)^4)");

	}

	// {a*c+(b*c+d)*x, x, 1, a*c*x+((b*c+d)*x^2)/2}
	public void test01639() {
		check("Integrate(a*c+(b*c+d)*x, x)", "a*c*x+((b*c+d)*x^2)/2");

	}

	// {d*x+c*(a+b*x), x, 1, (d*x^2)/2+(c*(a+b*x)^2)/(2*b)}
	public void test01640() {
		check("Integrate(d*x+c*(a+b*x), x)", "(d*x^2)/2+(c*(a+b*x)^2)/(2*b)");

	}

	// {(1+x^2)/(3*x+x^3), x, 1, Log(3*x+x^3)/3}
	public void test01641() {
		check("Integrate((1+x^2)/(3*x+x^3), x)", "Log(3*x+x^3)/3");

	}

	// {(a+3*b*x^2)/(a*x+b*x^3), x, 1, Log(a*x+b*x^3)}
	public void test01642() {
		check("Integrate((a+3*b*x^2)/(a*x+b*x^3), x)", "Log(a*x+b*x^3)");

	}

	// {(-x+2*x^3)/(1-x^2+x^4), x, 1, Log(1-x^2+x^4)/2}
	public void test01643() {
		check("Integrate((-x+2*x^3)/(1-x^2+x^4), x)", "Log(1-x^2+x^4)/2");

	}

	// {(x+2*x^3)/(x^2+x^4)^3, x, 1, -1/(4*(x^2+x^4)^2)}
	public void test01644() {
		check("Integrate((x+2*x^3)/(x^2+x^4)^3, x)", "-1/(4*(x^2+x^4)^2)");

	}

	// {x/(-1+x^2), x, 1, Log(1-x^2)/2}
	public void test01645() {
		check("Integrate(x/(-1+x^2), x)", "Log(1-x^2)/2");

	}

	// {(2+3*x)^(-1), x, 1, Log(2+3*x)/3}
	public void test01646() {
		check("Integrate((2+3*x)^(-1), x)", "Log(2+3*x)/3");

	}

	// {(a^2+x^2)^(-1), x, 1, ArcTan(x/a)/a}
	public void test01647() {
		check("Integrate((a^2+x^2)^(-1), x)", "ArcTan(x/a)/a");

	}

	// {(a+b*x^2)^(-1), x, 1, ArcTan((Sqrt(b)*x)/Sqrt(a))/(Sqrt(a)*Sqrt(b))}
	public void test01648() {
		check("Integrate((a+b*x^2)^(-1), x)", "ArcTan((Sqrt(b)*x)/Sqrt(a))/(Sqrt(a)*Sqrt(b))");

	}

	// {(2*x+x^2)/(1+x)^2, x, 1, x^2/(1+x)}
	public void test01649() {
		check("Integrate((2*x+x^2)/(1+x)^2, x)", "x^2/(1+x)");

	}

	// {(-2+7*x)^3, x, 1, (2-7*x)^4/28}
	public void test01650() {
		check("Integrate((-2+7*x)^3, x)", "(2-7*x)^4/28");

	}

	// {1/((c+d*x)*(-c^3+d^3*x^3)^(1/3)), x, 1, (Sqrt(3)*ArcTan((1-(2^(1/3)*(c-d*x))/(-c^3 +
	// d^3*x^3)^(1/3))/Sqrt(3)))/(2*2^(1/3)*c*d)+Log((c-d*x)*(c+d*x)^2)/(4*2^(1/3)*c*d)-(3*Log(d*(c-d*x) +
	// 2^(2/3)*d*(-c^3+d^3*x^3)^(1/3)))/(4*2^(1/3)*c*d)}
	public void test01651() {
		check("Integrate(1/((c+d*x)*(-c^3+d^3*x^3)^(1/3)), x)",
				"(Sqrt(3)*ArcTan((1-(2^(1/3)*(c-d*x))/(-c^3+d^3*x^3)^(1/3))/Sqrt(3)))/(2*2^(1/3)*c*d)+Log((c-d*x)*(c+d*x)^2)/(4*2^(1/3)*c*d)-(3*Log(d*(c-d*x)+2^(2/3)*d*(-c^3+d^3*x^3)^(1/3)))/(4*2^(1/3)*c*d)");

	}

	// {(c-d*x)/((c+d*x)*(2*c^3+d^3*x^3)^(1/3)), x, 1, -((Sqrt(3)*ArcTan((1+(2*(2*c+d*x))/(2*c^3 +
	// d^3*x^3)^(1/3))/Sqrt(3)))/d)-Log(c+d*x)/d+(3*Log(d*(2*c+d*x)-d*(2*c^3+d^3*x^3)^(1/3)))/(2*d)}
	public void test01652() {
		check("Integrate((c-d*x)/((c+d*x)*(2*c^3+d^3*x^3)^(1/3)), x)",
				"-((Sqrt(3)*ArcTan((1+(2*(2*c+d*x))/(2*c^3+d^3*x^3)^(1/3))/Sqrt(3)))/d)-Log(c+d*x)/d+(3*Log(d*(2*c+d*x)-d*(2*c^3+d^3*x^3)^(1/3)))/(2*d)");

	}

	// {1/Sqrt(a+c*x^4), x, 1, ((Sqrt(a)+Sqrt(c)*x^2)*Sqrt((a+c*x^4)/(Sqrt(a) +
	// Sqrt(c)*x^2)^2)*EllipticF(2*ArcTan((c^(1/4)*x)/a^(1/4)), 1/2))/(2*a^(1/4)*c^(1/4)*Sqrt(a+c*x^4))}
	public void test01653() {
		check("Integrate(1/Sqrt(a+c*x^4), x)",
				"((Sqrt(a)+Sqrt(c)*x^2)*Sqrt((a+c*x^4)/(Sqrt(a)+Sqrt(c)*x^2)^2)*EllipticF(2*ArcTan((c^(1/4)*x)/a^(1/4)), 1/2))/(2*a^(1/4)*c^(1/4)*Sqrt(a+c*x^4))");

	}

	// {Sqrt(1+Sqrt(1-x^2)), x, 1, (-2*x^3)/(3*(1+Sqrt(1-x^2))^(3/2))+(2*x)/Sqrt(1+Sqrt(1-x^2))}
	public void test01654() {
		check("Integrate(Sqrt(1+Sqrt(1-x^2)), x)", "(-2*x^3)/(3*(1+Sqrt(1-x^2))^(3/2))+(2*x)/Sqrt(1+Sqrt(1-x^2))");

	}

	// {Sqrt(1+Sqrt(1+x^2)), x, 1, (2*x^3)/(3*(1+Sqrt(1+x^2))^(3/2))+(2*x)/Sqrt(1+Sqrt(1+x^2))}
	public void test01655() {
		check("Integrate(Sqrt(1+Sqrt(1+x^2)), x)", "(2*x^3)/(3*(1+Sqrt(1+x^2))^(3/2))+(2*x)/Sqrt(1+Sqrt(1+x^2))");

	}

	// {Sqrt(5+Sqrt(25+x^2)), x, 1, (2*x^3)/(3*(5+Sqrt(25+x^2))^(3/2))+(10*x)/Sqrt(5+Sqrt(25+x^2))}
	public void test01656() {
		check("Integrate(Sqrt(5+Sqrt(25+x^2)), x)", "(2*x^3)/(3*(5+Sqrt(25+x^2))^(3/2))+(10*x)/Sqrt(5+Sqrt(25+x^2))");

	}

	// {Sqrt(a+b*Sqrt(a^2/b^2+c*x^2)), x, 1, (2*b^2*c*x^3)/(3*(a+b*Sqrt(a^2/b^2+c*x^2))^(3/2))+(2*a*x)/Sqrt(a
	// +b*Sqrt(a^2/b^2+c*x^2))}
	public void test01657() {
		check("Integrate(Sqrt(a+b*Sqrt(a^2/b^2+c*x^2)), x)",
				"(2*b^2*c*x^3)/(3*(a+b*Sqrt(a^2/b^2+c*x^2))^(3/2))+(2*a*x)/Sqrt(a+b*Sqrt(a^2/b^2+c*x^2))");

	}

	// {(-1+x^3)/(-4*x+x^4)^(2/3), x, 1, (3*(-4*x+x^4)^(1/3))/4}
	public void test01658() {
		check("Integrate((-1+x^3)/(-4*x+x^4)^(2/3), x)", "(3*(-4*x+x^4)^(1/3))/4");

	}

	// {(2-x^2)*(6*x-x^3)^(1/4), x, 1, (4*(6*x-x^3)^(5/4))/15}
	public void test01659() {
		check("Integrate((2-x^2)*(6*x-x^3)^(1/4), x)", "(4*(6*x-x^3)^(5/4))/15");

	}

	// {(1+x^4)*Sqrt(5*x+x^5), x, 1, (2*(5*x+x^5)^(3/2))/15}
	public void test01660() {
		check("Integrate((1+x^4)*Sqrt(5*x+x^5), x)", "(2*(5*x+x^5)^(3/2))/15");

	}

	// {(2+5*x^4)*Sqrt(2*x+x^5), x, 1, (2*(2*x+x^5)^(3/2))/3}
	public void test01661() {
		check("Integrate((2+5*x^4)*Sqrt(2*x+x^5), x)", "(2*(2*x+x^5)^(3/2))/3");

	}

	// {(x+3*x^2)/Sqrt(x^2+2*x^3), x, 1, Sqrt(x^2+2*x^3)}
	public void test01662() {
		check("Integrate((x+3*x^2)/Sqrt(x^2+2*x^3), x)", "Sqrt(x^2+2*x^3)");

	}

	// {x*(a+b*x+c*x^2)^m*(d+e*x+f*x^2+g*x^3)^n*(2*a*d+(3*b*d+3*a*e+b*d*m+a*e*n)*x+(4*c*d+4*b*e +
	// 4*a*f+2*c*d*m+b*e*m+b*e*n+2*a*f*n)*x^2+(5*c*e+5*b*f+5*a*g+2*c*e*m+b*f*m+c*e*n+2*b*f*n +
	// 3*a*g*n)*x^3+(6*c*f+6*b*g+2*c*f*m+b*g*m+2*c*f*n+3*b*g*n)*x^4+c*g*(7+2*m+3*n)*x^5), x, 1, x^2*(a
	// +b*x+c*x^2)^(1+m)*(d+e*x+f*x^2+g*x^3)^(1+n)}
	public void test01663() {
		check("Integrate(x*(a+b*x+c*x^2)^m*(d+e*x+f*x^2+g*x^3)^n*(2*a*d+(3*b*d+3*a*e+b*d*m+a*e*n)*x+(4*c*d+4*b*e+4*a*f+2*c*d*m+b*e*m+b*e*n+2*a*f*n)*x^2+(5*c*e+5*b*f+5*a*g+2*c*e*m+b*f*m+c*e*n+2*b*f*n+3*a*g*n)*x^3+(6*c*f+6*b*g+2*c*f*m+b*g*m+2*c*f*n+3*b*g*n)*x^4+c*g*(7+2*m+3*n)*x^5), x)",
				"x^2*(a+b*x+c*x^2)^(1+m)*(d+e*x+f*x^2+g*x^3)^(1+n)");

	}

	// {(a+b*x+c*x^2)^m*(d+e*x+f*x^2+g*x^3)^n*(a*d+(2*b*d+2*a*e+b*d*m+a*e*n)*x+(3*c*d+3*b*e +
	// 3*a*f+2*c*d*m+b*e*m+b*e*n+2*a*f*n)*x^2+(4*c*e+4*b*f+4*a*g+2*c*e*m+b*f*m+c*e*n+2*b*f*n +
	// 3*a*g*n)*x^3+(5*c*f+5*b*g+2*c*f*m+b*g*m+2*c*f*n+3*b*g*n)*x^4+c*g*(6+2*m+3*n)*x^5), x, 1, x*(a +
	// b*x+c*x^2)^(1+m)*(d+e*x+f*x^2+g*x^3)^(1+n)}
	public void test01664() {
		check("Integrate((a+b*x+c*x^2)^m*(d+e*x+f*x^2+g*x^3)^n*(a*d+(2*b*d+2*a*e+b*d*m+a*e*n)*x+(3*c*d+3*b*e+3*a*f+2*c*d*m+b*e*m+b*e*n+2*a*f*n)*x^2+(4*c*e+4*b*f+4*a*g+2*c*e*m+b*f*m+c*e*n+2*b*f*n+3*a*g*n)*x^3+(5*c*f+5*b*g+2*c*f*m+b*g*m+2*c*f*n+3*b*g*n)*x^4+c*g*(6+2*m+3*n)*x^5), x)",
				"x*(a+b*x+c*x^2)^(1+m)*(d+e*x+f*x^2+g*x^3)^(1+n)");

	}

	// {(a+b*x+c*x^2)^m*(d+e*x+f*x^2+g*x^3)^n*(b*d+a*e+b*d*m+a*e*n+(2*c*d+2*b*e+2*a*f+2*c*d*m +
	// b*e*m+b*e*n+2*a*f*n)*x+(3*c*e+3*b*f+3*a*g+2*c*e*m+b*f*m+c*e*n+2*b*f*n+3*a*g*n)*x^2+(4*c*f +
	// 4*b*g+2*c*f*m+b*g*m+2*c*f*n+3*b*g*n)*x^3+c*g*(5+2*m+3*n)*x^4), x, 1, (a+b*x+c*x^2)^(1+m)*(d +
	// e*x+f*x^2+g*x^3)^(1+n)}
	public void test01665() {
		check("Integrate((a+b*x+c*x^2)^m*(d+e*x+f*x^2+g*x^3)^n*(b*d+a*e+b*d*m+a*e*n+(2*c*d+2*b*e+2*a*f+2*c*d*m+b*e*m+b*e*n+2*a*f*n)*x+(3*c*e+3*b*f+3*a*g+2*c*e*m+b*f*m+c*e*n+2*b*f*n+3*a*g*n)*x^2+(4*c*f+4*b*g+2*c*f*m+b*g*m+2*c*f*n+3*b*g*n)*x^3+c*g*(5+2*m+3*n)*x^4), x)",
				"(a+b*x+c*x^2)^(1+m)*(d+e*x+f*x^2+g*x^3)^(1+n)");

	}

	// {1/Sqrt(4-9*x^2), x, 1, ArcSin((3*x)/2)/3}
	public void test01666() {
		check("Integrate(1/Sqrt(4-9*x^2), x)", "ArcSin((3*x)/2)/3");

	}

	// {1-Sqrt(x), x, 1, x-(2*x^(3/2))/3}
	public void test01667() {
		check("Integrate(1-Sqrt(x), x)", "x-(2*x^(3/2))/3");

	}

	// {1/Sqrt(1-x), x, 1, -2*Sqrt(1-x)}
	public void test01668() {
		check("Integrate(1/Sqrt(1-x), x)", "-2*Sqrt(1-x)");

	}

	// {1/Sqrt(1+x), x, 1, 2*Sqrt(1+x)}
	public void test01669() {
		check("Integrate(1/Sqrt(1+x), x)", "2*Sqrt(1+x)");

	}

	// {Sqrt(1-x), x, 1, (-2*(1-x)^(3/2))/3}
	public void test01670() {
		check("Integrate(Sqrt(1-x), x)", "(-2*(1-x)^(3/2))/3");

	}

	// {Sqrt(1+x), x, 1, (2*(1+x)^(3/2))/3}
	public void test01671() {
		check("Integrate(Sqrt(1+x), x)", "(2*(1+x)^(3/2))/3");

	}

	// {1/Sqrt(1-x^2), x, 1, ArcSin(x)}
	public void test01672() {
		check("Integrate(1/Sqrt(1-x^2), x)", "ArcSin(x)");

	}

	// {1/Sqrt(1+x^2), x, 1, ArcSinh(x)}
	public void test01673() {
		check("Integrate(1/Sqrt(1+x^2), x)", "ArcSinh(x)");

	}

	// {Sqrt(1-x)*Sqrt(x)*F(x), x, 1, CannotIntegrate(Sqrt(x-x^2)*F(x), x)}
	public void test01674() {
		check("Integrate(Sqrt(1-x)*Sqrt(x)*F(x), x)", "CannotIntegrate(Sqrt(x-x^2)*F(x), x)");

	}

	// {F(x)/(Sqrt(1-x)*Sqrt(x)), x, 1, CannotIntegrate(F(x)/Sqrt(x-x^2), x)}
	public void test01675() {
		check("Integrate(F(x)/(Sqrt(1-x)*Sqrt(x)), x)", "CannotIntegrate(F(x)/Sqrt(x-x^2), x)");

	}

	// {f((a+b*x)/x), x, 1, CannotIntegrate(f(b+a/x), x)}
	public void test01676() {
		check("Integrate(f((a+b*x)/x), x)", "CannotIntegrate(f(b+a/x), x)");

	}

	// {f((a+b*x^2)/x^2), x, 1, CannotIntegrate(f(b+a/x^2), x)}
	public void test01677() {
		check("Integrate(f((a+b*x^2)/x^2), x)", "CannotIntegrate(f(b+a/x^2), x)");

	}

	// {(3+x)/(6*x+x^2)^(1/3), x, 1, (3*(6*x+x^2)^(2/3))/4}
	public void test01678() {
		check("Integrate((3+x)/(6*x+x^2)^(1/3), x)", "(3*(6*x+x^2)^(2/3))/4");

	}

	// {(4+x)/(6*x-x^2)^(3/2), x, 1, -(12-7*x)/(9*Sqrt(6*x-x^2))}
	public void test01679() {
		check("Integrate((4+x)/(6*x-x^2)^(3/2), x)", "-(12-7*x)/(9*Sqrt(6*x-x^2))");

	}

	// {(-1+x)/Sqrt(2*x-x^2), x, 1, -Sqrt(2*x-x^2)}
	public void test01680() {
		check("Integrate((-1+x)/Sqrt(2*x-x^2), x)", "-Sqrt(2*x-x^2)");

	}

	// {1/((1+x)^(2/3)*(-1+x^2)^(2/3)), x, 1, (3*(-1+x^2)^(1/3))/(2*(1+x)^(2/3))}
	public void test01681() {
		check("Integrate(1/((1+x)^(2/3)*(-1+x^2)^(2/3)), x)", "(3*(-1+x^2)^(1/3))/(2*(1+x)^(2/3))");

	}

	// {(1+2*x)/Sqrt(x+x^2), x, 1, 2*Sqrt(x+x^2)}
	public void test01682() {
		check("Integrate((1+2*x)/Sqrt(x+x^2), x)", "2*Sqrt(x+x^2)");

	}

	// {1/(x*Sqrt(6*x-x^2)), x, 1, -Sqrt(6*x-x^2)/(3*x)}
	public void test01683() {
		check("Integrate(1/(x*Sqrt(6*x-x^2)), x)", "-Sqrt(6*x-x^2)/(3*x)");

	}

	// {1-Sqrt(x), x, 1, x-(2*x^(3/2))/3}
	public void test01684() {
		check("Integrate(1-Sqrt(x), x)", "x-(2*x^(3/2))/3");

	}

	// {1-x^(1/4), x, 1, x-(4*x^(5/4))/5}
	public void test01685() {
		check("Integrate(1-x^(1/4), x)", "x-(4*x^(5/4))/5");

	}

	// {(e*f-e*f*x^2)/((a*d+b*d*x+a*d*x^2)*Sqrt(a+b*x+c*x^2+b*x^3+a*x^4)), x, 1, (e*f*ArcTan((a*b+(4*a^2
	// +b^2-2*a*c)*x+a*b*x^2)/(2*a*Sqrt(2*a-c)*Sqrt(a+b*x+c*x^2+b*x^3+a*x^4))))/(a*Sqrt(2*a-c)*d)}
	public void test01686() {
		check("Integrate((e*f-e*f*x^2)/((a*d+b*d*x+a*d*x^2)*Sqrt(a+b*x+c*x^2+b*x^3+a*x^4)), x)",
				"(e*f*ArcTan((a*b+(4*a^2+b^2-2*a*c)*x+a*b*x^2)/(2*a*Sqrt(2*a-c)*Sqrt(a+b*x+c*x^2+b*x^3+a*x^4))))/(a*Sqrt(2*a-c)*d)");

	}

	// {(e*f-e*f*x^2)/((-(a*d)+b*d*x-a*d*x^2)*Sqrt(-a+b*x+c*x^2+b*x^3-a*x^4)), x, 1, (e*f*ArcTanh((a*b -
	// (4*a^2+b^2+2*a*c)*x+a*b*x^2)/(2*a*Sqrt(2*a+c)*Sqrt(-a+b*x+c*x^2+b*x^3-a*x^4))))/(a*Sqrt(2*a +
	// c)*d)}
	public void test01687() {
		check("Integrate((e*f-e*f*x^2)/((-(a*d)+b*d*x-a*d*x^2)*Sqrt(-a+b*x+c*x^2+b*x^3-a*x^4)), x)",
				"(e*f*ArcTanh((a*b-(4*a^2+b^2+2*a*c)*x+a*b*x^2)/(2*a*Sqrt(2*a+c)*Sqrt(-a+b*x+c*x^2+b*x^3-a*x^4))))/(a*Sqrt(2*a+c)*d)");

	}

	// {x+(1-x^2)/(1+x), x, 1, x}
	public void test01688() {
		check("Integrate(x+(1-x^2)/(1+x), x)", "x");

	}

	// {F^(c*(a+b*x))*(d+e*x)^m, x, 1, (F^(c*(a-(b*d)/e))*(d+e*x)^m*Gamma(1+m, -((b*c*(d +
	// e*x)*Log(F))/e)))/(b*c*Log(F)*(-((b*c*(d+e*x)*Log(F))/e))^m)}
	public void test01689() {
		check("Integrate(F^(c*(a+b*x))*(d+e*x)^m, x)",
				"(F^(c*(a-(b*d)/e))*(d+e*x)^m*Gamma(1+m, -((b*c*(d+e*x)*Log(F))/e)))/(b*c*Log(F)*(-((b*c*(d+e*x)*Log(F))/e))^m)");

	}

	// {F^(c*(a+b*x)), x, 1, F^(c*(a+b*x))/(b*c*Log(F))}
	public void test01690() {
		check("Integrate(F^(c*(a+b*x)), x)", "F^(c*(a+b*x))/(b*c*Log(F))");

	}

	// {F^(c*(a+b*x))/(d+e*x), x, 1, (F^(c*(a-(b*d)/e))*ExpIntegralEi((b*c*(d+e*x)*Log(F))/e))/e}
	public void test01691() {
		check("Integrate(F^(c*(a+b*x))/(d+e*x), x)", "(F^(c*(a-(b*d)/e))*ExpIntegralEi((b*c*(d+e*x)*Log(F))/e))/e");

	}

	// {F^(c*(a+b*x))*(d+e*x)^m, x, 1, (F^(c*(a-(b*d)/e))*(d+e*x)^m*Gamma(1+m, -((b*c*(d +
	// e*x)*Log(F))/e)))/(b*c*Log(F)*(-((b*c*(d+e*x)*Log(F))/e))^m)}
	public void test01692() {
		check("Integrate(F^(c*(a+b*x))*(d+e*x)^m, x)",
				"(F^(c*(a-(b*d)/e))*(d+e*x)^m*Gamma(1+m, -((b*c*(d+e*x)*Log(F))/e)))/(b*c*Log(F)*(-((b*c*(d+e*x)*Log(F))/e))^m)");

	}

	// {F^(c*(a+b*x))/(d+e*x)^m, x, 1, (F^(c*(a-(b*d)/e))*Gamma(1-m, -((b*c*(d+e*x)*Log(F))/e))*(-((b*c*(d +
	// e*x)*Log(F))/e))^m)/(b*c*(d+e*x)^m*Log(F))}
	public void test01693() {
		check("Integrate(F^(c*(a+b*x))/(d+e*x)^m, x)",
				"(F^(c*(a-(b*d)/e))*Gamma(1-m, -((b*c*(d+e*x)*Log(F))/e))*(-((b*c*(d+e*x)*Log(F))/e))^m)/(b*c*(d+e*x)^m*Log(F))");

	}

	// {F^(2+5*x), x, 1, F^(2+5*x)/(5*Log(F))}
	public void test01694() {
		check("Integrate(F^(2+5*x), x)", "F^(2+5*x)/(5*Log(F))");

	}

	// {F^(a+b*x), x, 1, F^(a+b*x)/(b*Log(F))}
	public void test01695() {
		check("Integrate(F^(a+b*x), x)", "F^(a+b*x)/(b*Log(F))");

	}

	// {10^(2+5*x), x, 1, (2^(2+5*x)*5^(1+5*x))/Log(10)}
	public void test01696() {
		check("Integrate(10^(2+5*x), x)", "(2^(2+5*x)*5^(1+5*x))/Log(10)");

	}

	// {F^(c*(a+b*x))*(d+e*x)^(4/3), x, 1, -((e*F^(c*(a-(b*d)/e))*(d+e*x)^(1/3)*Gamma(7/3, -((b*c*(d +
	// e*x)*Log(F))/e)))/(b^2*c^2*Log(F)^2*(-((b*c*(d+e*x)*Log(F))/e))^(1/3)))}
	public void test01697() {
		check("Integrate(F^(c*(a+b*x))*(d+e*x)^(4/3), x)",
				"-((e*F^(c*(a-(b*d)/e))*(d+e*x)^(1/3)*Gamma(7/3, -((b*c*(d+e*x)*Log(F))/e)))/(b^2*c^2*Log(F)^2*(-((b*c*(d+e*x)*Log(F))/e))^(1/3)))");

	}

	// {F^(c*(a+b*x))*x^m*Log(d*x)^n*(e+e*n+e*(1+m+b*c*x*Log(F))*Log(d*x)), x, 1, e*F^(c*(a+b*x))*x^(1 +
	// m)*Log(d*x)^(1+n)}
	public void test01698() {
		check("Integrate(F^(c*(a+b*x))*x^m*Log(d*x)^n*(e+e*n+e*(1+m+b*c*x*Log(F))*Log(d*x)), x)",
				"e*F^(c*(a+b*x))*x^(1+m)*Log(d*x)^(1+n)");

	}

	// {F^(c*(a+b*x))*x^2*Log(d*x)^n*(e+e*n+e*(3+b*c*x*Log(F))*Log(d*x)), x, 1, e*F^(c*(a +
	// b*x))*x^3*Log(d*x)^(1+n)}
	public void test01699() {
		check("Integrate(F^(c*(a+b*x))*x^2*Log(d*x)^n*(e+e*n+e*(3+b*c*x*Log(F))*Log(d*x)), x)",
				"e*F^(c*(a+b*x))*x^3*Log(d*x)^(1+n)");

	}

	// {F^(c*(a+b*x))*x*Log(d*x)^n*(e+e*n+e*(2+b*c*x*Log(F))*Log(d*x)), x, 1, e*F^(c*(a+b*x))*x^2*Log(d*x)^(1
	// +n)}
	public void test01700() {
		check("Integrate(F^(c*(a+b*x))*x*Log(d*x)^n*(e+e*n+e*(2+b*c*x*Log(F))*Log(d*x)), x)",
				"e*F^(c*(a+b*x))*x^2*Log(d*x)^(1+n)");

	}

	// {F^(c*(a+b*x))*Log(d*x)^n*(e+e*n+e*(1+b*c*x*Log(F))*Log(d*x)), x, 1, e*F^(c*(a+b*x))*x*Log(d*x)^(1 +
	// n)}
	public void test01701() {
		check("Integrate(F^(c*(a+b*x))*Log(d*x)^n*(e+e*n+e*(1+b*c*x*Log(F))*Log(d*x)), x)",
				"e*F^(c*(a+b*x))*x*Log(d*x)^(1+n)");

	}

	// {(F^(c*(a+b*x))*Log(d*x)^n*(e+e*n+b*c*e*x*Log(F)*Log(d*x)))/x, x, 1, e*F^(c*(a+b*x))*Log(d*x)^(1+n)}
	public void test01702() {
		check("Integrate((F^(c*(a+b*x))*Log(d*x)^n*(e+e*n+b*c*e*x*Log(F)*Log(d*x)))/x, x)",
				"e*F^(c*(a+b*x))*Log(d*x)^(1+n)");

	}

	// {(F^(c*(a+b*x))*Log(d*x)^n*(e+e*n+e*(-1+b*c*x*Log(F))*Log(d*x)))/x^2, x, 1, (e*F^(c*(a +
	// b*x))*Log(d*x)^(1+n))/x}
	public void test01703() {
		check("Integrate((F^(c*(a+b*x))*Log(d*x)^n*(e+e*n+e*(-1+b*c*x*Log(F))*Log(d*x)))/x^2, x)",
				"(e*F^(c*(a+b*x))*Log(d*x)^(1+n))/x");

	}

	// {(F^(c*(a+b*x))*Log(d*x)^n*(e+e*n+e*(-2+b*c*x*Log(F))*Log(d*x)))/x^3, x, 1, (e*F^(c*(a +
	// b*x))*Log(d*x)^(1+n))/x^2}
	public void test01704() {
		check("Integrate((F^(c*(a+b*x))*Log(d*x)^n*(e+e*n+e*(-2+b*c*x*Log(F))*Log(d*x)))/x^3, x)",
				"(e*F^(c*(a+b*x))*Log(d*x)^(1+n))/x^2");

	}

	// {Sqrt(E^(a+b*x)), x, 1, (2*Sqrt(E^(a+b*x)))/b}
	public void test01705() {
		check("Integrate(Sqrt(E^(a+b*x)), x)", "(2*Sqrt(E^(a+b*x)))/b");

	}

	// {1/((a+b*(F^(g*(e+f*x)))^n)*(c+d*x)), x, 1, Unintegrable(1/((a+b*(F^(e*g+f*g*x))^n)*(c+d*x)), x)}
	public void test01706() {
		check("Integrate(1/((a+b*(F^(g*(e+f*x)))^n)*(c+d*x)), x)",
				"Unintegrable(1/((a+b*(F^(e*g+f*g*x))^n)*(c+d*x)), x)");

	}

	// {1/((a+b*(F^(g*(e+f*x)))^n)*(c+d*x)^2), x, 1, Unintegrable(1/((a+b*(F^(e*g+f*g*x))^n)*(c+d*x)^2), x)}
	public void test01707() {
		check("Integrate(1/((a+b*(F^(g*(e+f*x)))^n)*(c+d*x)^2), x)",
				"Unintegrable(1/((a+b*(F^(e*g+f*g*x))^n)*(c+d*x)^2), x)");

	}

	// {1/((a+b*(F^(g*(e+f*x)))^n)^2*(c+d*x)), x, 1, Unintegrable(1/((a+b*(F^(e*g+f*g*x))^n)^2*(c+d*x)), x)}
	public void test01708() {
		check("Integrate(1/((a+b*(F^(g*(e+f*x)))^n)^2*(c+d*x)), x)",
				"Unintegrable(1/((a+b*(F^(e*g+f*g*x))^n)^2*(c+d*x)), x)");

	}

	// {1/((a+b*(F^(g*(e+f*x)))^n)^2*(c+d*x)^2), x, 1, Unintegrable(1/((a+b*(F^(e*g+f*g*x))^n)^2*(c+d*x)^2),
	// x)}
	public void test01709() {
		check("Integrate(1/((a+b*(F^(g*(e+f*x)))^n)^2*(c+d*x)^2), x)",
				"Unintegrable(1/((a+b*(F^(e*g+f*g*x))^n)^2*(c+d*x)^2), x)");

	}

	// {1/((a+b*(F^(g*(e+f*x)))^n)^3*(c+d*x)), x, 1, Unintegrable(1/((a+b*(F^(e*g+f*g*x))^n)^3*(c+d*x)), x)}
	public void test01710() {
		check("Integrate(1/((a+b*(F^(g*(e+f*x)))^n)^3*(c+d*x)), x)",
				"Unintegrable(1/((a+b*(F^(e*g+f*g*x))^n)^3*(c+d*x)), x)");

	}

	// {1/((a+b*(F^(g*(e+f*x)))^n)^3*(c+d*x)^2), x, 1, Unintegrable(1/((a+b*(F^(e*g+f*g*x))^n)^3*(c+d*x)^2),
	// x)}
	public void test01711() {
		check("Integrate(1/((a+b*(F^(g*(e+f*x)))^n)^3*(c+d*x)^2), x)",
				"Unintegrable(1/((a+b*(F^(e*g+f*g*x))^n)^3*(c+d*x)^2), x)");

	}

	// {(c+d*x)^m/(a+b*(F^(g*(e+f*x)))^n), x, 1, Unintegrable((c+d*x)^m/(a+b*(F^(e*g+f*g*x))^n), x)}
	public void test01712() {
		check("Integrate((c+d*x)^m/(a+b*(F^(g*(e+f*x)))^n), x)", "Unintegrable((c+d*x)^m/(a+b*(F^(e*g+f*g*x))^n), x)");

	}

	// {(c+d*x)^m/(a+b*(F^(g*(e+f*x)))^n)^2, x, 1, Unintegrable((c+d*x)^m/(a+b*(F^(e*g+f*g*x))^n)^2, x)}
	public void test01713() {
		check("Integrate((c+d*x)^m/(a+b*(F^(g*(e+f*x)))^n)^2, x)",
				"Unintegrable((c+d*x)^m/(a+b*(F^(e*g+f*g*x))^n)^2, x)");

	}

	// {(a+b*(F^(g*(e+f*x)))^n)^p*(c+d*x)^m, x, 1, Unintegrable((a+b*(F^(e*g+f*g*x))^n)^p*(c+d*x)^m, x)}
	public void test01714() {
		check("Integrate((a+b*(F^(g*(e+f*x)))^n)^p*(c+d*x)^m, x)",
				"Unintegrable((a+b*(F^(e*g+f*g*x))^n)^p*(c+d*x)^m, x)");

	}

	// {F^(c+d*x)/((a+b*F^(c+d*x))^2*x), x, 1, -(1/(b*d*(a+b*F^(c+d*x))*x*Log(F)))-Unintegrable(1/((a +
	// b*F^(c+d*x))*x^2), x)/(b*d*Log(F))}
	public void test01715() {
		check("Integrate(F^(c+d*x)/((a+b*F^(c+d*x))^2*x), x)",
				"-(1/(b*d*(a+b*F^(c+d*x))*x*Log(F)))-Unintegrable(1/((a+b*F^(c+d*x))*x^2), x)/(b*d*Log(F))");

	}

	// {F^(c+d*x)/((a+b*F^(c+d*x))^2*x^2), x, 1, -(1/(b*d*(a+b*F^(c+d*x))*x^2*Log(F)))-(2*Unintegrable(1/((a
	// +b*F^(c+d*x))*x^3), x))/(b*d*Log(F))}
	public void test01716() {
		check("Integrate(F^(c+d*x)/((a+b*F^(c+d*x))^2*x^2), x)",
				"-(1/(b*d*(a+b*F^(c+d*x))*x^2*Log(F)))-(2*Unintegrable(1/((a+b*F^(c+d*x))*x^3), x))/(b*d*Log(F))");

	}

	// {F^(c+d*x)/((a+b*F^(c+d*x))^3*x), x, 1, -1/(2*b*d*(a+b*F^(c+d*x))^2*x*Log(F))-Unintegrable(1/((a +
	// b*F^(c+d*x))^2*x^2), x)/(2*b*d*Log(F))}
	public void test01717() {
		check("Integrate(F^(c+d*x)/((a+b*F^(c+d*x))^3*x), x)",
				"-1/(2*b*d*(a+b*F^(c+d*x))^2*x*Log(F))-Unintegrable(1/((a+b*F^(c+d*x))^2*x^2), x)/(2*b*d*Log(F))");

	}

	// {F^(c+d*x)/((a+b*F^(c+d*x))^3*x^2), x, 1, -1/(2*b*d*(a+b*F^(c+d*x))^2*x^2*Log(F))-Unintegrable(1/((a
	// +b*F^(c+d*x))^2*x^3), x)/(b*d*Log(F))}
	public void test01718() {
		check("Integrate(F^(c+d*x)/((a+b*F^(c+d*x))^3*x^2), x)",
				"-1/(2*b*d*(a+b*F^(c+d*x))^2*x^2*Log(F))-Unintegrable(1/((a+b*F^(c+d*x))^2*x^3), x)/(b*d*Log(F))");

	}

	// {f^(a+b*x^2)*x^m, x, 1, -(f^a*x^(1+m)*Gamma((1+m)/2, -(b*x^2*Log(f)))*(-(b*x^2*Log(f)))^((-1-m)/2))/2}
	public void test01719() {
		check("Integrate(f^(a+b*x^2)*x^m, x)",
				"-(f^a*x^(1+m)*Gamma((1+m)/2, -(b*x^2*Log(f)))*(-(b*x^2*Log(f)))^((-1-m)/2))/2");

	}

	// {f^(a+b*x^2)*x^11, x, 1, -(f^a*Gamma(6, -(b*x^2*Log(f))))/(2*b^6*Log(f)^6)}
	public void test01720() {
		check("Integrate(f^(a+b*x^2)*x^11, x)", "-(f^a*Gamma(6, -(b*x^2*Log(f))))/(2*b^6*Log(f)^6)");

	}

	// {f^(a+b*x^2)*x^9, x, 1, (f^a*Gamma(5, -(b*x^2*Log(f))))/(2*b^5*Log(f)^5)}
	public void test01721() {
		check("Integrate(f^(a+b*x^2)*x^9, x)", "(f^a*Gamma(5, -(b*x^2*Log(f))))/(2*b^5*Log(f)^5)");

	}

	// {f^(a+b*x^2)*x, x, 1, f^(a+b*x^2)/(2*b*Log(f))}
	public void test01722() {
		check("Integrate(f^(a+b*x^2)*x, x)", "f^(a+b*x^2)/(2*b*Log(f))");

	}

	// {f^(a+b*x^2)/x, x, 1, (f^a*ExpIntegralEi(b*x^2*Log(f)))/2}
	public void test01723() {
		check("Integrate(f^(a+b*x^2)/x, x)", "(f^a*ExpIntegralEi(b*x^2*Log(f)))/2");

	}

	// {f^(a+b*x^2)/x^9, x, 1, -(b^4*f^a*Gamma(-4, -(b*x^2*Log(f)))*Log(f)^4)/2}
	public void test01724() {
		check("Integrate(f^(a+b*x^2)/x^9, x)", "-(b^4*f^a*Gamma(-4, -(b*x^2*Log(f)))*Log(f)^4)/2");

	}

	// {f^(a+b*x^2)/x^11, x, 1, (b^5*f^a*Gamma(-5, -(b*x^2*Log(f)))*Log(f)^5)/2}
	public void test01725() {
		check("Integrate(f^(a+b*x^2)/x^11, x)", "(b^5*f^a*Gamma(-5, -(b*x^2*Log(f)))*Log(f)^5)/2");

	}

	// {f^(a+b*x^2)*x^12, x, 1, -(f^a*x^13*Gamma(13/2, -(b*x^2*Log(f))))/(2*(-(b*x^2*Log(f)))^(13/2))}
	public void test01726() {
		check("Integrate(f^(a+b*x^2)*x^12, x)",
				"-(f^a*x^13*Gamma(13/2, -(b*x^2*Log(f))))/(2*(-(b*x^2*Log(f)))^(13/2))");

	}

	// {f^(a+b*x^2)*x^10, x, 1, -(f^a*x^11*Gamma(11/2, -(b*x^2*Log(f))))/(2*(-(b*x^2*Log(f)))^(11/2))}
	public void test01727() {
		check("Integrate(f^(a+b*x^2)*x^10, x)",
				"-(f^a*x^11*Gamma(11/2, -(b*x^2*Log(f))))/(2*(-(b*x^2*Log(f)))^(11/2))");

	}

	// {f^(a+b*x^2), x, 1, (f^a*Sqrt(Pi)*Erfi(Sqrt(b)*x*Sqrt(Log(f))))/(2*Sqrt(b)*Sqrt(Log(f)))}
	public void test01728() {
		check("Integrate(f^(a+b*x^2), x)", "(f^a*Sqrt(Pi)*Erfi(Sqrt(b)*x*Sqrt(Log(f))))/(2*Sqrt(b)*Sqrt(Log(f)))");

	}

	// {f^(a+b*x^2)/x^10, x, 1, -(f^a*Gamma(-9/2, -(b*x^2*Log(f)))*(-(b*x^2*Log(f)))^(9/2))/(2*x^9)}
	public void test01729() {
		check("Integrate(f^(a+b*x^2)/x^10, x)", "-(f^a*Gamma(-9/2, -(b*x^2*Log(f)))*(-(b*x^2*Log(f)))^(9/2))/(2*x^9)");

	}

	// {f^(a+b*x^2)/x^12, x, 1, -(f^a*Gamma(-11/2, -(b*x^2*Log(f)))*(-(b*x^2*Log(f)))^(11/2))/(2*x^11)}
	public void test01730() {
		check("Integrate(f^(a+b*x^2)/x^12, x)",
				"-(f^a*Gamma(-11/2, -(b*x^2*Log(f)))*(-(b*x^2*Log(f)))^(11/2))/(2*x^11)");

	}

	// {f^(a+b*x^3)*x^m, x, 1, -(f^a*x^(1+m)*Gamma((1+m)/3, -(b*x^3*Log(f)))*(-(b*x^3*Log(f)))^((-1-m)/3))/3}
	public void test01731() {
		check("Integrate(f^(a+b*x^3)*x^m, x)",
				"-(f^a*x^(1+m)*Gamma((1+m)/3, -(b*x^3*Log(f)))*(-(b*x^3*Log(f)))^((-1-m)/3))/3");

	}

	// {f^(a+b*x^3)*x^17, x, 1, -(f^a*Gamma(6, -(b*x^3*Log(f))))/(3*b^6*Log(f)^6)}
	public void test01732() {
		check("Integrate(f^(a+b*x^3)*x^17, x)", "-(f^a*Gamma(6, -(b*x^3*Log(f))))/(3*b^6*Log(f)^6)");

	}

	// {f^(a+b*x^3)*x^14, x, 1, (f^a*Gamma(5, -(b*x^3*Log(f))))/(3*b^5*Log(f)^5)}
	public void test01733() {
		check("Integrate(f^(a+b*x^3)*x^14, x)", "(f^a*Gamma(5, -(b*x^3*Log(f))))/(3*b^5*Log(f)^5)");

	}

	// {f^(a+b*x^3)*x^2, x, 1, f^(a+b*x^3)/(3*b*Log(f))}
	public void test01734() {
		check("Integrate(f^(a+b*x^3)*x^2, x)", "f^(a+b*x^3)/(3*b*Log(f))");

	}

	// {f^(a+b*x^3)/x, x, 1, (f^a*ExpIntegralEi(b*x^3*Log(f)))/3}
	public void test01735() {
		check("Integrate(f^(a+b*x^3)/x, x)", "(f^a*ExpIntegralEi(b*x^3*Log(f)))/3");

	}

	// {f^(a+b*x^3)/x^13, x, 1, -(b^4*f^a*Gamma(-4, -(b*x^3*Log(f)))*Log(f)^4)/3}
	public void test01736() {
		check("Integrate(f^(a+b*x^3)/x^13, x)", "-(b^4*f^a*Gamma(-4, -(b*x^3*Log(f)))*Log(f)^4)/3");

	}

	// {f^(a+b*x^3)/x^16, x, 1, (b^5*f^a*Gamma(-5, -(b*x^3*Log(f)))*Log(f)^5)/3}
	public void test01737() {
		check("Integrate(f^(a+b*x^3)/x^16, x)", "(b^5*f^a*Gamma(-5, -(b*x^3*Log(f)))*Log(f)^5)/3");

	}

	// {f^(a+b*x^3)*x^4, x, 1, -(f^a*x^5*Gamma(5/3, -(b*x^3*Log(f))))/(3*(-(b*x^3*Log(f)))^(5/3))}
	public void test01738() {
		check("Integrate(f^(a+b*x^3)*x^4, x)", "-(f^a*x^5*Gamma(5/3, -(b*x^3*Log(f))))/(3*(-(b*x^3*Log(f)))^(5/3))");

	}

	// {f^(a+b*x^3)*x^3, x, 1, -(f^a*x^4*Gamma(4/3, -(b*x^3*Log(f))))/(3*(-(b*x^3*Log(f)))^(4/3))}
	public void test01739() {
		check("Integrate(f^(a+b*x^3)*x^3, x)", "-(f^a*x^4*Gamma(4/3, -(b*x^3*Log(f))))/(3*(-(b*x^3*Log(f)))^(4/3))");

	}

	// {f^(a+b*x^3)*x, x, 1, -(f^a*x^2*Gamma(2/3, -(b*x^3*Log(f))))/(3*(-(b*x^3*Log(f)))^(2/3))}
	public void test01740() {
		check("Integrate(f^(a+b*x^3)*x, x)", "-(f^a*x^2*Gamma(2/3, -(b*x^3*Log(f))))/(3*(-(b*x^3*Log(f)))^(2/3))");

	}

	// {f^(a+b*x^3), x, 1, -(f^a*x*Gamma(1/3, -(b*x^3*Log(f))))/(3*(-(b*x^3*Log(f)))^(1/3))}
	public void test01741() {
		check("Integrate(f^(a+b*x^3), x)", "-(f^a*x*Gamma(1/3, -(b*x^3*Log(f))))/(3*(-(b*x^3*Log(f)))^(1/3))");

	}

	// {f^(a+b*x^3)/x^2, x, 1, -(f^a*Gamma(-1/3, -(b*x^3*Log(f)))*(-(b*x^3*Log(f)))^(1/3))/(3*x)}
	public void test01742() {
		check("Integrate(f^(a+b*x^3)/x^2, x)", "-(f^a*Gamma(-1/3, -(b*x^3*Log(f)))*(-(b*x^3*Log(f)))^(1/3))/(3*x)");

	}

	// {f^(a+b*x^3)/x^3, x, 1, -(f^a*Gamma(-2/3, -(b*x^3*Log(f)))*(-(b*x^3*Log(f)))^(2/3))/(3*x^2)}
	public void test01743() {
		check("Integrate(f^(a+b*x^3)/x^3, x)", "-(f^a*Gamma(-2/3, -(b*x^3*Log(f)))*(-(b*x^3*Log(f)))^(2/3))/(3*x^2)");

	}

	// {E^(4*x^3)*x^2, x, 1, E^(4*x^3)/12}
	public void test01744() {
		check("Integrate(E^(4*x^3)*x^2, x)", "E^(4*x^3)/12");

	}

	// {f^(a+b/x)*x^m, x, 1, f^a*x^(1+m)*Gamma(-1-m, -((b*Log(f))/x))*(-((b*Log(f))/x))^(1+m)}
	public void test01745() {
		check("Integrate(f^(a+b/x)*x^m, x)", "f^a*x^(1+m)*Gamma(-1-m, -((b*Log(f))/x))*(-((b*Log(f))/x))^(1+m)");

	}

	// {f^(a+b/x)*x^4, x, 1, -(b^5*f^a*Gamma(-5, -((b*Log(f))/x))*Log(f)^5)}
	public void test01746() {
		check("Integrate(f^(a+b/x)*x^4, x)", "-(b^5*f^a*Gamma(-5, -((b*Log(f))/x))*Log(f)^5)");

	}

	// {f^(a+b/x)*x^3, x, 1, b^4*f^a*Gamma(-4, -((b*Log(f))/x))*Log(f)^4}
	public void test01747() {
		check("Integrate(f^(a+b/x)*x^3, x)", "b^4*f^a*Gamma(-4, -((b*Log(f))/x))*Log(f)^4");

	}

	// {f^(a+b/x)/x, x, 1, -(f^a*ExpIntegralEi((b*Log(f))/x))}
	public void test01748() {
		check("Integrate(f^(a+b/x)/x, x)", "-(f^a*ExpIntegralEi((b*Log(f))/x))");

	}

	// {f^(a+b/x)/x^2, x, 1, -(f^(a+b/x)/(b*Log(f)))}
	public void test01749() {
		check("Integrate(f^(a+b/x)/x^2, x)", "-(f^(a+b/x)/(b*Log(f)))");

	}

	// {f^(a+b/x)/x^6, x, 1, -((f^a*Gamma(5, -((b*Log(f))/x)))/(b^5*Log(f)^5))}
	public void test01750() {
		check("Integrate(f^(a+b/x)/x^6, x)", "-((f^a*Gamma(5, -((b*Log(f))/x)))/(b^5*Log(f)^5))");

	}

	// {f^(a+b/x)/x^7, x, 1, (f^a*Gamma(6, -((b*Log(f))/x)))/(b^6*Log(f)^6)}
	public void test01751() {
		check("Integrate(f^(a+b/x)/x^7, x)", "(f^a*Gamma(6, -((b*Log(f))/x)))/(b^6*Log(f)^6)");

	}

	// {f^(a+b/x^2)*x^m, x, 1, (f^a*x^(1+m)*Gamma((-1-m)/2, -((b*Log(f))/x^2))*(-((b*Log(f))/x^2))^((1+m)/2))/2}
	public void test01752() {
		check("Integrate(f^(a+b/x^2)*x^m, x)",
				"(f^a*x^(1+m)*Gamma((-1-m)/2, -((b*Log(f))/x^2))*(-((b*Log(f))/x^2))^((1+m)/2))/2");

	}

	// {f^(a+b/x^2)*x^9, x, 1, -(b^5*f^a*Gamma(-5, -((b*Log(f))/x^2))*Log(f)^5)/2}
	public void test01753() {
		check("Integrate(f^(a+b/x^2)*x^9, x)", "-(b^5*f^a*Gamma(-5, -((b*Log(f))/x^2))*Log(f)^5)/2");

	}

	// {f^(a+b/x^2)*x^7, x, 1, (b^4*f^a*Gamma(-4, -((b*Log(f))/x^2))*Log(f)^4)/2}
	public void test01754() {
		check("Integrate(f^(a+b/x^2)*x^7, x)", "(b^4*f^a*Gamma(-4, -((b*Log(f))/x^2))*Log(f)^4)/2");

	}

	// {f^(a+b/x^2)/x, x, 1, -(f^a*ExpIntegralEi((b*Log(f))/x^2))/2}
	public void test01755() {
		check("Integrate(f^(a+b/x^2)/x, x)", "-(f^a*ExpIntegralEi((b*Log(f))/x^2))/2");

	}

	// {f^(a+b/x^2)/x^3, x, 1, -f^(a+b/x^2)/(2*b*Log(f))}
	public void test01756() {
		check("Integrate(f^(a+b/x^2)/x^3, x)", "-f^(a+b/x^2)/(2*b*Log(f))");

	}

	// {f^(a+b/x^2)/x^11, x, 1, -(f^a*Gamma(5, -((b*Log(f))/x^2)))/(2*b^5*Log(f)^5)}
	public void test01757() {
		check("Integrate(f^(a+b/x^2)/x^11, x)", "-(f^a*Gamma(5, -((b*Log(f))/x^2)))/(2*b^5*Log(f)^5)");

	}

	// {f^(a+b/x^2)/x^13, x, 1, (f^a*Gamma(6, -((b*Log(f))/x^2)))/(2*b^6*Log(f)^6)}
	public void test01758() {
		check("Integrate(f^(a+b/x^2)/x^13, x)", "(f^a*Gamma(6, -((b*Log(f))/x^2)))/(2*b^6*Log(f)^6)");

	}

	// {f^(a+b/x^2)*x^10, x, 1, (f^a*x^11*Gamma(-11/2, -((b*Log(f))/x^2))*(-((b*Log(f))/x^2))^(11/2))/2}
	public void test01759() {
		check("Integrate(f^(a+b/x^2)*x^10, x)",
				"(f^a*x^11*Gamma(-11/2, -((b*Log(f))/x^2))*(-((b*Log(f))/x^2))^(11/2))/2");

	}

	// {f^(a+b/x^2)*x^8, x, 1, (f^a*x^9*Gamma(-9/2, -((b*Log(f))/x^2))*(-((b*Log(f))/x^2))^(9/2))/2}
	public void test01760() {
		check("Integrate(f^(a+b/x^2)*x^8, x)", "(f^a*x^9*Gamma(-9/2, -((b*Log(f))/x^2))*(-((b*Log(f))/x^2))^(9/2))/2");

	}

	// {f^(a+b/x^2)/x^12, x, 1, (f^a*Gamma(11/2, -((b*Log(f))/x^2)))/(2*x^11*(-((b*Log(f))/x^2))^(11/2))}
	public void test01761() {
		check("Integrate(f^(a+b/x^2)/x^12, x)",
				"(f^a*Gamma(11/2, -((b*Log(f))/x^2)))/(2*x^11*(-((b*Log(f))/x^2))^(11/2))");

	}

	// {f^(a+b/x^2)/x^14, x, 1, (f^a*Gamma(13/2, -((b*Log(f))/x^2)))/(2*x^13*(-((b*Log(f))/x^2))^(13/2))}
	public void test01762() {
		check("Integrate(f^(a+b/x^2)/x^14, x)",
				"(f^a*Gamma(13/2, -((b*Log(f))/x^2)))/(2*x^13*(-((b*Log(f))/x^2))^(13/2))");

	}

	// {f^(a+b/x^3)*x^m, x, 1, (f^a*x^(1+m)*Gamma((-1-m)/3, -((b*Log(f))/x^3))*(-((b*Log(f))/x^3))^((1+m)/3))/3}
	public void test01763() {
		check("Integrate(f^(a+b/x^3)*x^m, x)",
				"(f^a*x^(1+m)*Gamma((-1-m)/3, -((b*Log(f))/x^3))*(-((b*Log(f))/x^3))^((1+m)/3))/3");

	}

	// {f^(a+b/x^3)*x^14, x, 1, -(b^5*f^a*Gamma(-5, -((b*Log(f))/x^3))*Log(f)^5)/3}
	public void test01764() {
		check("Integrate(f^(a+b/x^3)*x^14, x)", "-(b^5*f^a*Gamma(-5, -((b*Log(f))/x^3))*Log(f)^5)/3");

	}

	// {f^(a+b/x^3)*x^11, x, 1, (b^4*f^a*Gamma(-4, -((b*Log(f))/x^3))*Log(f)^4)/3}
	public void test01765() {
		check("Integrate(f^(a+b/x^3)*x^11, x)", "(b^4*f^a*Gamma(-4, -((b*Log(f))/x^3))*Log(f)^4)/3");

	}

	// {f^(a+b/x^3)/x, x, 1, -(f^a*ExpIntegralEi((b*Log(f))/x^3))/3}
	public void test01766() {
		check("Integrate(f^(a+b/x^3)/x, x)", "-(f^a*ExpIntegralEi((b*Log(f))/x^3))/3");

	}

	// {f^(a+b/x^3)/x^4, x, 1, -f^(a+b/x^3)/(3*b*Log(f))}
	public void test01767() {
		check("Integrate(f^(a+b/x^3)/x^4, x)", "-f^(a+b/x^3)/(3*b*Log(f))");

	}

	// {f^(a+b/x^3)/x^16, x, 1, -(f^a*Gamma(5, -((b*Log(f))/x^3)))/(3*b^5*Log(f)^5)}
	public void test01768() {
		check("Integrate(f^(a+b/x^3)/x^16, x)", "-(f^a*Gamma(5, -((b*Log(f))/x^3)))/(3*b^5*Log(f)^5)");

	}

	// {f^(a+b/x^3)/x^19, x, 1, (f^a*Gamma(6, -((b*Log(f))/x^3)))/(3*b^6*Log(f)^6)}
	public void test01769() {
		check("Integrate(f^(a+b/x^3)/x^19, x)", "(f^a*Gamma(6, -((b*Log(f))/x^3)))/(3*b^6*Log(f)^6)");

	}

	// {f^(a+b/x^3)*x^4, x, 1, (f^a*x^5*Gamma(-5/3, -((b*Log(f))/x^3))*(-((b*Log(f))/x^3))^(5/3))/3}
	public void test01770() {
		check("Integrate(f^(a+b/x^3)*x^4, x)", "(f^a*x^5*Gamma(-5/3, -((b*Log(f))/x^3))*(-((b*Log(f))/x^3))^(5/3))/3");

	}

	// {f^(a+b/x^3)*x^3, x, 1, (f^a*x^4*Gamma(-4/3, -((b*Log(f))/x^3))*(-((b*Log(f))/x^3))^(4/3))/3}
	public void test01771() {
		check("Integrate(f^(a+b/x^3)*x^3, x)", "(f^a*x^4*Gamma(-4/3, -((b*Log(f))/x^3))*(-((b*Log(f))/x^3))^(4/3))/3");

	}

	// {f^(a+b/x^3)*x, x, 1, (f^a*x^2*Gamma(-2/3, -((b*Log(f))/x^3))*(-((b*Log(f))/x^3))^(2/3))/3}
	public void test01772() {
		check("Integrate(f^(a+b/x^3)*x, x)", "(f^a*x^2*Gamma(-2/3, -((b*Log(f))/x^3))*(-((b*Log(f))/x^3))^(2/3))/3");

	}

	// {f^(a+b/x^3), x, 1, (f^a*x*Gamma(-1/3, -((b*Log(f))/x^3))*(-((b*Log(f))/x^3))^(1/3))/3}
	public void test01773() {
		check("Integrate(f^(a+b/x^3), x)", "(f^a*x*Gamma(-1/3, -((b*Log(f))/x^3))*(-((b*Log(f))/x^3))^(1/3))/3");

	}

	// {f^(a+b/x^3)/x^2, x, 1, (f^a*Gamma(1/3, -((b*Log(f))/x^3)))/(3*x*(-((b*Log(f))/x^3))^(1/3))}
	public void test01774() {
		check("Integrate(f^(a+b/x^3)/x^2, x)", "(f^a*Gamma(1/3, -((b*Log(f))/x^3)))/(3*x*(-((b*Log(f))/x^3))^(1/3))");

	}

	// {f^(a+b/x^3)/x^3, x, 1, (f^a*Gamma(2/3, -((b*Log(f))/x^3)))/(3*x^2*(-((b*Log(f))/x^3))^(2/3))}
	public void test01775() {
		check("Integrate(f^(a+b/x^3)/x^3, x)", "(f^a*Gamma(2/3, -((b*Log(f))/x^3)))/(3*x^2*(-((b*Log(f))/x^3))^(2/3))");

	}

	// {f^(a+b/x^3)/x^5, x, 1, (f^a*Gamma(4/3, -((b*Log(f))/x^3)))/(3*x^4*(-((b*Log(f))/x^3))^(4/3))}
	public void test01776() {
		check("Integrate(f^(a+b/x^3)/x^5, x)", "(f^a*Gamma(4/3, -((b*Log(f))/x^3)))/(3*x^4*(-((b*Log(f))/x^3))^(4/3))");

	}

	// {f^(a+b*x^n)*x^m, x, 1, -((f^a*x^(1+m)*Gamma((1+m)/n, -(b*x^n*Log(f))))/(n*(-(b*x^n*Log(f)))^((1+m)/n)))}
	public void test01777() {
		check("Integrate(f^(a+b*x^n)*x^m, x)",
				"-((f^a*x^(1+m)*Gamma((1+m)/n, -(b*x^n*Log(f))))/(n*(-(b*x^n*Log(f)))^((1+m)/n)))");

	}

	// {f^(a+b*x^n)*x^3, x, 1, -((f^a*x^4*Gamma(4/n, -(b*x^n*Log(f))))/(n*(-(b*x^n*Log(f)))^(4/n)))}
	public void test01778() {
		check("Integrate(f^(a+b*x^n)*x^3, x)", "-((f^a*x^4*Gamma(4/n, -(b*x^n*Log(f))))/(n*(-(b*x^n*Log(f)))^(4/n)))");

	}

	// {f^(a+b*x^n)*x^2, x, 1, -((f^a*x^3*Gamma(3/n, -(b*x^n*Log(f))))/(n*(-(b*x^n*Log(f)))^(3/n)))}
	public void test01779() {
		check("Integrate(f^(a+b*x^n)*x^2, x)", "-((f^a*x^3*Gamma(3/n, -(b*x^n*Log(f))))/(n*(-(b*x^n*Log(f)))^(3/n)))");

	}

	// {f^(a+b*x^n)*x, x, 1, -((f^a*x^2*Gamma(2/n, -(b*x^n*Log(f))))/(n*(-(b*x^n*Log(f)))^(2/n)))}
	public void test01780() {
		check("Integrate(f^(a+b*x^n)*x, x)", "-((f^a*x^2*Gamma(2/n, -(b*x^n*Log(f))))/(n*(-(b*x^n*Log(f)))^(2/n)))");

	}

	// {f^(a+b*x^n), x, 1, -((f^a*x*Gamma(n^(-1), -(b*x^n*Log(f))))/(n*(-(b*x^n*Log(f)))^n^(-1)))}
	public void test01781() {
		check("Integrate(f^(a+b*x^n), x)", "-((f^a*x*Gamma(n^(-1), -(b*x^n*Log(f))))/(n*(-(b*x^n*Log(f)))^n^(-1)))");

	}

	// {f^(a+b*x^n)/x, x, 1, (f^a*ExpIntegralEi(b*x^n*Log(f)))/n}
	public void test01782() {
		check("Integrate(f^(a+b*x^n)/x, x)", "(f^a*ExpIntegralEi(b*x^n*Log(f)))/n");

	}

	// {f^(a+b*x^n)/x^2, x, 1, -((f^a*Gamma(-n^(-1), -(b*x^n*Log(f)))*(-(b*x^n*Log(f)))^n^(-1))/(n*x))}
	public void test01783() {
		check("Integrate(f^(a+b*x^n)/x^2, x)",
				"-((f^a*Gamma(-n^(-1), -(b*x^n*Log(f)))*(-(b*x^n*Log(f)))^n^(-1))/(n*x))");

	}

	// {f^(a+b*x^n)/x^3, x, 1, -((f^a*Gamma(-2/n, -(b*x^n*Log(f)))*(-(b*x^n*Log(f)))^(2/n))/(n*x^2))}
	public void test01784() {
		check("Integrate(f^(a+b*x^n)/x^3, x)", "-((f^a*Gamma(-2/n, -(b*x^n*Log(f)))*(-(b*x^n*Log(f)))^(2/n))/(n*x^2))");

	}

	// {f^(a+b*x^n)/x^4, x, 1, -((f^a*Gamma(-3/n, -(b*x^n*Log(f)))*(-(b*x^n*Log(f)))^(3/n))/(n*x^3))}
	public void test01785() {
		check("Integrate(f^(a+b*x^n)/x^4, x)", "-((f^a*Gamma(-3/n, -(b*x^n*Log(f)))*(-(b*x^n*Log(f)))^(3/n))/(n*x^3))");

	}

	// {f^(a+b*x^n)*x^(-1+n), x, 1, f^(a+b*x^n)/(b*n*Log(f))}
	public void test01786() {
		check("Integrate(f^(a+b*x^n)*x^(-1+n), x)", "f^(a+b*x^n)/(b*n*Log(f))");

	}

	// {f^(a+b*x^n)/x, x, 1, (f^a*ExpIntegralEi(b*x^n*Log(f)))/n}
	public void test01787() {
		check("Integrate(f^(a+b*x^n)/x, x)", "(f^a*ExpIntegralEi(b*x^n*Log(f)))/n");

	}

	// {f^(c*(a+b*x)^2), x, 1, (Sqrt(Pi)*Erfi(Sqrt(c)*(a+b*x)*Sqrt(Log(f))))/(2*b*Sqrt(c)*Sqrt(Log(f)))}
	public void test01788() {
		check("Integrate(f^(c*(a+b*x)^2), x)",
				"(Sqrt(Pi)*Erfi(Sqrt(c)*(a+b*x)*Sqrt(Log(f))))/(2*b*Sqrt(c)*Sqrt(Log(f)))");

	}

	// {f^(c*(a+b*x)^3), x, 1, -((a+b*x)*Gamma(1/3, -(c*(a+b*x)^3*Log(f))))/(3*b*(-(c*(a+b*x)^3*Log(f)))^(1/3))}
	public void test01789() {
		check("Integrate(f^(c*(a+b*x)^3), x)",
				"-((a+b*x)*Gamma(1/3, -(c*(a+b*x)^3*Log(f))))/(3*b*(-(c*(a+b*x)^3*Log(f)))^(1/3))");

	}

	// {f^(c/(a+b*x)^3), x, 1, ((a+b*x)*Gamma(-1/3, -((c*Log(f))/(a+b*x)^3))*(-((c*Log(f))/(a +
	// b*x)^3))^(1/3))/(3*b)}
	public void test01790() {
		check("Integrate(f^(c/(a+b*x)^3), x)",
				"((a+b*x)*Gamma(-1/3, -((c*Log(f))/(a+b*x)^3))*(-((c*Log(f))/(a+b*x)^3))^(1/3))/(3*b)");

	}

	// {f^(c*(a+b*x)^2)*x^m, x, 1, Unintegrable(f^(a^2*c+2*a*b*c*x+b^2*c*x^2)*x^m, x)}
	public void test01791() {
		check("Integrate(f^(c*(a+b*x)^2)*x^m, x)", "Unintegrable(f^(a^2*c+2*a*b*c*x+b^2*c*x^2)*x^m, x)");

	}

	// {f^(c*(a+b*x))*x^m, x, 1, (f^(a*c)*x^m*Gamma(1+m, -(b*c*x*Log(f))))/(b*c*Log(f)*(-(b*c*x*Log(f)))^m)}
	public void test01792() {
		check("Integrate(f^(c*(a+b*x))*x^m, x)",
				"(f^(a*c)*x^m*Gamma(1+m, -(b*c*x*Log(f))))/(b*c*Log(f)*(-(b*c*x*Log(f)))^m)");

	}

	// {f^(c*(a+b*x)^n), x, 1, -(((a+b*x)*Gamma(n^(-1), -(c*(a+b*x)^n*Log(f))))/(b*n*(-(c*(a +
	// b*x)^n*Log(f)))^n^(-1)))}
	public void test01793() {
		check("Integrate(f^(c*(a+b*x)^n), x)",
				"-(((a+b*x)*Gamma(n^(-1), -(c*(a+b*x)^n*Log(f))))/(b*n*(-(c*(a+b*x)^n*Log(f)))^n^(-1)))");

	}

	// {F^(a+b*(c+d*x)^2)*(c+d*x)^m, x, 1, -(F^a*(c+d*x)^(1+m)*Gamma((1+m)/2, -(b*(c +
	// d*x)^2*Log(F)))*(-(b*(c+d*x)^2*Log(F)))^((-1-m)/2))/(2*d)}
	public void test01794() {
		check("Integrate(F^(a+b*(c+d*x)^2)*(c+d*x)^m, x)",
				"-(F^a*(c+d*x)^(1+m)*Gamma((1+m)/2, -(b*(c+d*x)^2*Log(F)))*(-(b*(c+d*x)^2*Log(F)))^((-1-m)/2))/(2*d)");

	}

	// {F^(a+b*(c+d*x)^2)*(c+d*x)^11, x, 1, -(F^a*Gamma(6, -(b*(c+d*x)^2*Log(F))))/(2*b^6*d*Log(F)^6)}
	public void test01795() {
		check("Integrate(F^(a+b*(c+d*x)^2)*(c+d*x)^11, x)",
				"-(F^a*Gamma(6, -(b*(c+d*x)^2*Log(F))))/(2*b^6*d*Log(F)^6)");

	}

	// {F^(a+b*(c+d*x)^2)*(c+d*x)^9, x, 1, (F^a*Gamma(5, -(b*(c+d*x)^2*Log(F))))/(2*b^5*d*Log(F)^5)}
	public void test01796() {
		check("Integrate(F^(a+b*(c+d*x)^2)*(c+d*x)^9, x)", "(F^a*Gamma(5, -(b*(c+d*x)^2*Log(F))))/(2*b^5*d*Log(F)^5)");

	}

	// {F^(a+b*(c+d*x)^2)*(c+d*x), x, 1, F^(a+b*(c+d*x)^2)/(2*b*d*Log(F))}
	public void test01797() {
		check("Integrate(F^(a+b*(c+d*x)^2)*(c+d*x), x)", "F^(a+b*(c+d*x)^2)/(2*b*d*Log(F))");

	}

	// {F^(a+b*(c+d*x)^2)/(c+d*x), x, 1, (F^a*ExpIntegralEi(b*(c+d*x)^2*Log(F)))/(2*d)}
	public void test01798() {
		check("Integrate(F^(a+b*(c+d*x)^2)/(c+d*x), x)", "(F^a*ExpIntegralEi(b*(c+d*x)^2*Log(F)))/(2*d)");

	}

	// {F^(a+b*(c+d*x)^2)/(c+d*x)^9, x, 1, -(b^4*F^a*Gamma(-4, -(b*(c+d*x)^2*Log(F)))*Log(F)^4)/(2*d)}
	public void test01799() {
		check("Integrate(F^(a+b*(c+d*x)^2)/(c+d*x)^9, x)",
				"-(b^4*F^a*Gamma(-4, -(b*(c+d*x)^2*Log(F)))*Log(F)^4)/(2*d)");

	}

	// {F^(a+b*(c+d*x)^2)/(c+d*x)^11, x, 1, (b^5*F^a*Gamma(-5, -(b*(c+d*x)^2*Log(F)))*Log(F)^5)/(2*d)}
	public void test01800() {
		check("Integrate(F^(a+b*(c+d*x)^2)/(c+d*x)^11, x)",
				"(b^5*F^a*Gamma(-5, -(b*(c+d*x)^2*Log(F)))*Log(F)^5)/(2*d)");

	}

	// {F^(a+b*(c+d*x)^2)*(c+d*x)^12, x, 1, -(F^a*(c+d*x)^13*Gamma(13/2, -(b*(c+d*x)^2*Log(F))))/(2*d*(-(b*(c
	// +d*x)^2*Log(F)))^(13/2))}
	public void test01801() {
		check("Integrate(F^(a+b*(c+d*x)^2)*(c+d*x)^12, x)",
				"-(F^a*(c+d*x)^13*Gamma(13/2, -(b*(c+d*x)^2*Log(F))))/(2*d*(-(b*(c+d*x)^2*Log(F)))^(13/2))");

	}

	// {F^(a+b*(c+d*x)^2)*(c+d*x)^10, x, 1, -(F^a*(c+d*x)^11*Gamma(11/2, -(b*(c+d*x)^2*Log(F))))/(2*d*(-(b*(c
	// +d*x)^2*Log(F)))^(11/2))}
	public void test01802() {
		check("Integrate(F^(a+b*(c+d*x)^2)*(c+d*x)^10, x)",
				"-(F^a*(c+d*x)^11*Gamma(11/2, -(b*(c+d*x)^2*Log(F))))/(2*d*(-(b*(c+d*x)^2*Log(F)))^(11/2))");

	}

	// {F^(a+b*(c+d*x)^2), x, 1, (F^a*Sqrt(Pi)*Erfi(Sqrt(b)*(c+d*x)*Sqrt(Log(F))))/(2*Sqrt(b)*d*Sqrt(Log(F)))}
	public void test01803() {
		check("Integrate(F^(a+b*(c+d*x)^2), x)",
				"(F^a*Sqrt(Pi)*Erfi(Sqrt(b)*(c+d*x)*Sqrt(Log(F))))/(2*Sqrt(b)*d*Sqrt(Log(F)))");

	}

	// {F^(a+b*(c+d*x)^2)/(c+d*x)^10, x, 1, -(F^a*Gamma(-9/2, -(b*(c+d*x)^2*Log(F)))*(-(b*(c +
	// d*x)^2*Log(F)))^(9/2))/(2*d*(c+d*x)^9)}
	public void test01804() {
		check("Integrate(F^(a+b*(c+d*x)^2)/(c+d*x)^10, x)",
				"-(F^a*Gamma(-9/2, -(b*(c+d*x)^2*Log(F)))*(-(b*(c+d*x)^2*Log(F)))^(9/2))/(2*d*(c+d*x)^9)");

	}

	// {F^(a+b*(c+d*x)^2)/(c+d*x)^12, x, 1, -(F^a*Gamma(-11/2, -(b*(c+d*x)^2*Log(F)))*(-(b*(c +
	// d*x)^2*Log(F)))^(11/2))/(2*d*(c+d*x)^11)}
	public void test01805() {
		check("Integrate(F^(a+b*(c+d*x)^2)/(c+d*x)^12, x)",
				"-(F^a*Gamma(-11/2, -(b*(c+d*x)^2*Log(F)))*(-(b*(c+d*x)^2*Log(F)))^(11/2))/(2*d*(c+d*x)^11)");

	}

	// {F^(a+b*(c+d*x)^3)*(c+d*x)^m, x, 1, -(F^a*(c+d*x)^(1+m)*Gamma((1+m)/3, -(b*(c +
	// d*x)^3*Log(F)))*(-(b*(c+d*x)^3*Log(F)))^((-1-m)/3))/(3*d)}
	public void test01806() {
		check("Integrate(F^(a+b*(c+d*x)^3)*(c+d*x)^m, x)",
				"-(F^a*(c+d*x)^(1+m)*Gamma((1+m)/3, -(b*(c+d*x)^3*Log(F)))*(-(b*(c+d*x)^3*Log(F)))^((-1-m)/3))/(3*d)");

	}

	// {F^(a+b*(c+d*x)^3)*(c+d*x)^17, x, 1, -(F^a*Gamma(6, -(b*(c+d*x)^3*Log(F))))/(3*b^6*d*Log(F)^6)}
	public void test01807() {
		check("Integrate(F^(a+b*(c+d*x)^3)*(c+d*x)^17, x)",
				"-(F^a*Gamma(6, -(b*(c+d*x)^3*Log(F))))/(3*b^6*d*Log(F)^6)");

	}

	// {F^(a+b*(c+d*x)^3)*(c+d*x)^14, x, 1, (F^a*Gamma(5, -(b*(c+d*x)^3*Log(F))))/(3*b^5*d*Log(F)^5)}
	public void test01808() {
		check("Integrate(F^(a+b*(c+d*x)^3)*(c+d*x)^14, x)", "(F^a*Gamma(5, -(b*(c+d*x)^3*Log(F))))/(3*b^5*d*Log(F)^5)");

	}

	// {F^(a+b*(c+d*x)^3)*(c+d*x)^2, x, 1, F^(a+b*(c+d*x)^3)/(3*b*d*Log(F))}
	public void test01809() {
		check("Integrate(F^(a+b*(c+d*x)^3)*(c+d*x)^2, x)", "F^(a+b*(c+d*x)^3)/(3*b*d*Log(F))");

	}

	// {F^(a+b*(c+d*x)^3)/(c+d*x), x, 1, (F^a*ExpIntegralEi(b*(c+d*x)^3*Log(F)))/(3*d)}
	public void test01810() {
		check("Integrate(F^(a+b*(c+d*x)^3)/(c+d*x), x)", "(F^a*ExpIntegralEi(b*(c+d*x)^3*Log(F)))/(3*d)");

	}

	// {F^(a+b*(c+d*x)^3)/(c+d*x)^13, x, 1, -(b^4*F^a*Gamma(-4, -(b*(c+d*x)^3*Log(F)))*Log(F)^4)/(3*d)}
	public void test01811() {
		check("Integrate(F^(a+b*(c+d*x)^3)/(c+d*x)^13, x)",
				"-(b^4*F^a*Gamma(-4, -(b*(c+d*x)^3*Log(F)))*Log(F)^4)/(3*d)");

	}

	// {F^(a+b*(c+d*x)^3)/(c+d*x)^16, x, 1, (b^5*F^a*Gamma(-5, -(b*(c+d*x)^3*Log(F)))*Log(F)^5)/(3*d)}
	public void test01812() {
		check("Integrate(F^(a+b*(c+d*x)^3)/(c+d*x)^16, x)",
				"(b^5*F^a*Gamma(-5, -(b*(c+d*x)^3*Log(F)))*Log(F)^5)/(3*d)");

	}

	// {F^(a+b*(c+d*x)^3)*(c+d*x)^3, x, 1, -(F^a*(c+d*x)^4*Gamma(4/3, -(b*(c+d*x)^3*Log(F))))/(3*d*(-(b*(c +
	// d*x)^3*Log(F)))^(4/3))}
	public void test01813() {
		check("Integrate(F^(a+b*(c+d*x)^3)*(c+d*x)^3, x)",
				"-(F^a*(c+d*x)^4*Gamma(4/3, -(b*(c+d*x)^3*Log(F))))/(3*d*(-(b*(c+d*x)^3*Log(F)))^(4/3))");

	}

	// {F^(a+b*(c+d*x)^3)*(c+d*x), x, 1, -(F^a*(c+d*x)^2*Gamma(2/3, -(b*(c+d*x)^3*Log(F))))/(3*d*(-(b*(c +
	// d*x)^3*Log(F)))^(2/3))}
	public void test01814() {
		check("Integrate(F^(a+b*(c+d*x)^3)*(c+d*x), x)",
				"-(F^a*(c+d*x)^2*Gamma(2/3, -(b*(c+d*x)^3*Log(F))))/(3*d*(-(b*(c+d*x)^3*Log(F)))^(2/3))");

	}

	// {F^(a+b*(c+d*x)^3), x, 1, -(F^a*(c+d*x)*Gamma(1/3, -(b*(c+d*x)^3*Log(F))))/(3*d*(-(b*(c +
	// d*x)^3*Log(F)))^(1/3))}
	public void test01815() {
		check("Integrate(F^(a+b*(c+d*x)^3), x)",
				"-(F^a*(c+d*x)*Gamma(1/3, -(b*(c+d*x)^3*Log(F))))/(3*d*(-(b*(c+d*x)^3*Log(F)))^(1/3))");

	}

	// {F^(a+b*(c+d*x)^3)/(c+d*x)^2, x, 1, -(F^a*Gamma(-1/3, -(b*(c+d*x)^3*Log(F)))*(-(b*(c +
	// d*x)^3*Log(F)))^(1/3))/(3*d*(c+d*x))}
	public void test01816() {
		check("Integrate(F^(a+b*(c+d*x)^3)/(c+d*x)^2, x)",
				"-(F^a*Gamma(-1/3, -(b*(c+d*x)^3*Log(F)))*(-(b*(c+d*x)^3*Log(F)))^(1/3))/(3*d*(c+d*x))");

	}

	// {F^(a+b*(c+d*x)^3)/(c+d*x)^3, x, 1, -(F^a*Gamma(-2/3, -(b*(c+d*x)^3*Log(F)))*(-(b*(c +
	// d*x)^3*Log(F)))^(2/3))/(3*d*(c+d*x)^2)}
	public void test01817() {
		check("Integrate(F^(a+b*(c+d*x)^3)/(c+d*x)^3, x)",
				"-(F^a*Gamma(-2/3, -(b*(c+d*x)^3*Log(F)))*(-(b*(c+d*x)^3*Log(F)))^(2/3))/(3*d*(c+d*x)^2)");

	}

	// {F^(a+b*(c+d*x)^3)/(c+d*x)^5, x, 1, -(F^a*Gamma(-4/3, -(b*(c+d*x)^3*Log(F)))*(-(b*(c +
	// d*x)^3*Log(F)))^(4/3))/(3*d*(c+d*x)^4)}
	public void test01818() {
		check("Integrate(F^(a+b*(c+d*x)^3)/(c+d*x)^5, x)",
				"-(F^a*Gamma(-4/3, -(b*(c+d*x)^3*Log(F)))*(-(b*(c+d*x)^3*Log(F)))^(4/3))/(3*d*(c+d*x)^4)");

	}

	// {F^(a+b/(c+d*x))*(c+d*x)^m, x, 1, (F^a*(c+d*x)^(1+m)*Gamma(-1-m, -((b*Log(F))/(c +
	// d*x)))*(-((b*Log(F))/(c+d*x)))^(1+m))/d}
	public void test01819() {
		check("Integrate(F^(a+b/(c+d*x))*(c+d*x)^m, x)",
				"(F^a*(c+d*x)^(1+m)*Gamma(-1-m, -((b*Log(F))/(c+d*x)))*(-((b*Log(F))/(c+d*x)))^(1+m))/d");

	}

	// {F^(a+b/(c+d*x))*(c+d*x)^4, x, 1, -((b^5*F^a*Gamma(-5, -((b*Log(F))/(c+d*x)))*Log(F)^5)/d)}
	public void test01820() {
		check("Integrate(F^(a+b/(c+d*x))*(c+d*x)^4, x)", "-((b^5*F^a*Gamma(-5, -((b*Log(F))/(c+d*x)))*Log(F)^5)/d)");

	}

	// {F^(a+b/(c+d*x))*(c+d*x)^3, x, 1, (b^4*F^a*Gamma(-4, -((b*Log(F))/(c+d*x)))*Log(F)^4)/d}
	public void test01821() {
		check("Integrate(F^(a+b/(c+d*x))*(c+d*x)^3, x)", "(b^4*F^a*Gamma(-4, -((b*Log(F))/(c+d*x)))*Log(F)^4)/d");

	}

	// {F^(a+b/(c+d*x))/(c+d*x), x, 1, -((F^a*ExpIntegralEi((b*Log(F))/(c+d*x)))/d)}
	public void test01822() {
		check("Integrate(F^(a+b/(c+d*x))/(c+d*x), x)", "-((F^a*ExpIntegralEi((b*Log(F))/(c+d*x)))/d)");

	}

	// {F^(a+b/(c+d*x))/(c+d*x)^2, x, 1, -(F^(a+b/(c+d*x))/(b*d*Log(F)))}
	public void test01823() {
		check("Integrate(F^(a+b/(c+d*x))/(c+d*x)^2, x)", "-(F^(a+b/(c+d*x))/(b*d*Log(F)))");

	}

	// {F^(a+b/(c+d*x))/(c+d*x)^6, x, 1, -((F^a*Gamma(5, -((b*Log(F))/(c+d*x))))/(b^5*d*Log(F)^5))}
	public void test01824() {
		check("Integrate(F^(a+b/(c+d*x))/(c+d*x)^6, x)", "-((F^a*Gamma(5, -((b*Log(F))/(c+d*x))))/(b^5*d*Log(F)^5))");

	}

	// {F^(a+b/(c+d*x))/(c+d*x)^7, x, 1, (F^a*Gamma(6, -((b*Log(F))/(c+d*x))))/(b^6*d*Log(F)^6)}
	public void test01825() {
		check("Integrate(F^(a+b/(c+d*x))/(c+d*x)^7, x)", "(F^a*Gamma(6, -((b*Log(F))/(c+d*x))))/(b^6*d*Log(F)^6)");

	}

	// {F^(a+b/(c+d*x)^2)*(c+d*x)^m, x, 1, (F^a*(c+d*x)^(1+m)*Gamma((-1-m)/2, -((b*Log(F))/(c +
	// d*x)^2))*(-((b*Log(F))/(c+d*x)^2))^((1+m)/2))/(2*d)}
	public void test01826() {
		check("Integrate(F^(a+b/(c+d*x)^2)*(c+d*x)^m, x)",
				"(F^a*(c+d*x)^(1+m)*Gamma((-1-m)/2, -((b*Log(F))/(c+d*x)^2))*(-((b*Log(F))/(c+d*x)^2))^((1+m)/2))/(2*d)");

	}

	// {F^(a+b/(c+d*x)^2)*(c+d*x)^9, x, 1, -(b^5*F^a*Gamma(-5, -((b*Log(F))/(c+d*x)^2))*Log(F)^5)/(2*d)}
	public void test01827() {
		check("Integrate(F^(a+b/(c+d*x)^2)*(c+d*x)^9, x)",
				"-(b^5*F^a*Gamma(-5, -((b*Log(F))/(c+d*x)^2))*Log(F)^5)/(2*d)");

	}

	// {F^(a+b/(c+d*x)^2)*(c+d*x)^7, x, 1, (b^4*F^a*Gamma(-4, -((b*Log(F))/(c+d*x)^2))*Log(F)^4)/(2*d)}
	public void test01828() {
		check("Integrate(F^(a+b/(c+d*x)^2)*(c+d*x)^7, x)",
				"(b^4*F^a*Gamma(-4, -((b*Log(F))/(c+d*x)^2))*Log(F)^4)/(2*d)");

	}

	// {F^(a+b/(c+d*x)^2)/(c+d*x), x, 1, -(F^a*ExpIntegralEi((b*Log(F))/(c+d*x)^2))/(2*d)}
	public void test01829() {
		check("Integrate(F^(a+b/(c+d*x)^2)/(c+d*x), x)", "-(F^a*ExpIntegralEi((b*Log(F))/(c+d*x)^2))/(2*d)");

	}

	// {F^(a+b/(c+d*x)^2)/(c+d*x)^3, x, 1, -F^(a+b/(c+d*x)^2)/(2*b*d*Log(F))}
	public void test01830() {
		check("Integrate(F^(a+b/(c+d*x)^2)/(c+d*x)^3, x)", "-F^(a+b/(c+d*x)^2)/(2*b*d*Log(F))");

	}

	// {F^(a+b/(c+d*x)^2)/(c+d*x)^11, x, 1, -(F^a*Gamma(5, -((b*Log(F))/(c+d*x)^2)))/(2*b^5*d*Log(F)^5)}
	public void test01831() {
		check("Integrate(F^(a+b/(c+d*x)^2)/(c+d*x)^11, x)",
				"-(F^a*Gamma(5, -((b*Log(F))/(c+d*x)^2)))/(2*b^5*d*Log(F)^5)");

	}

	// {F^(a+b/(c+d*x)^2)/(c+d*x)^13, x, 1, (F^a*Gamma(6, -((b*Log(F))/(c+d*x)^2)))/(2*b^6*d*Log(F)^6)}
	public void test01832() {
		check("Integrate(F^(a+b/(c+d*x)^2)/(c+d*x)^13, x)",
				"(F^a*Gamma(6, -((b*Log(F))/(c+d*x)^2)))/(2*b^6*d*Log(F)^6)");

	}

	// {F^(a+b/(c+d*x)^2)*(c+d*x)^10, x, 1, (F^a*(c+d*x)^11*Gamma(-11/2, -((b*Log(F))/(c +
	// d*x)^2))*(-((b*Log(F))/(c+d*x)^2))^(11/2))/(2*d)}
	public void test01833() {
		check("Integrate(F^(a+b/(c+d*x)^2)*(c+d*x)^10, x)",
				"(F^a*(c+d*x)^11*Gamma(-11/2, -((b*Log(F))/(c+d*x)^2))*(-((b*Log(F))/(c+d*x)^2))^(11/2))/(2*d)");

	}

	// {F^(a+b/(c+d*x)^2)*(c+d*x)^8, x, 1, (F^a*(c+d*x)^9*Gamma(-9/2, -((b*Log(F))/(c +
	// d*x)^2))*(-((b*Log(F))/(c+d*x)^2))^(9/2))/(2*d)}
	public void test01834() {
		check("Integrate(F^(a+b/(c+d*x)^2)*(c+d*x)^8, x)",
				"(F^a*(c+d*x)^9*Gamma(-9/2, -((b*Log(F))/(c+d*x)^2))*(-((b*Log(F))/(c+d*x)^2))^(9/2))/(2*d)");

	}

	// {F^(a+b/(c+d*x)^2)/(c+d*x)^12, x, 1, (F^a*Gamma(11/2, -((b*Log(F))/(c+d*x)^2)))/(2*d*(c +
	// d*x)^11*(-((b*Log(F))/(c+d*x)^2))^(11/2))}
	public void test01835() {
		check("Integrate(F^(a+b/(c+d*x)^2)/(c+d*x)^12, x)",
				"(F^a*Gamma(11/2, -((b*Log(F))/(c+d*x)^2)))/(2*d*(c+d*x)^11*(-((b*Log(F))/(c+d*x)^2))^(11/2))");

	}

	// {F^(a+b/(c+d*x)^2)/(c+d*x)^14, x, 1, (F^a*Gamma(13/2, -((b*Log(F))/(c+d*x)^2)))/(2*d*(c +
	// d*x)^13*(-((b*Log(F))/(c+d*x)^2))^(13/2))}
	public void test01836() {
		check("Integrate(F^(a+b/(c+d*x)^2)/(c+d*x)^14, x)",
				"(F^a*Gamma(13/2, -((b*Log(F))/(c+d*x)^2)))/(2*d*(c+d*x)^13*(-((b*Log(F))/(c+d*x)^2))^(13/2))");

	}

	// {F^(a+b/(c+d*x)^3)*(c+d*x)^m, x, 1, (F^a*(c+d*x)^(1+m)*Gamma((-1-m)/3, -((b*Log(F))/(c +
	// d*x)^3))*(-((b*Log(F))/(c+d*x)^3))^((1+m)/3))/(3*d)}
	public void test01837() {
		check("Integrate(F^(a+b/(c+d*x)^3)*(c+d*x)^m, x)",
				"(F^a*(c+d*x)^(1+m)*Gamma((-1-m)/3, -((b*Log(F))/(c+d*x)^3))*(-((b*Log(F))/(c+d*x)^3))^((1+m)/3))/(3*d)");

	}

	// {F^(a+b/(c+d*x)^3)*(c+d*x)^14, x, 1, -(b^5*F^a*Gamma(-5, -((b*Log(F))/(c+d*x)^3))*Log(F)^5)/(3*d)}
	public void test01838() {
		check("Integrate(F^(a+b/(c+d*x)^3)*(c+d*x)^14, x)",
				"-(b^5*F^a*Gamma(-5, -((b*Log(F))/(c+d*x)^3))*Log(F)^5)/(3*d)");

	}

	// {F^(a+b/(c+d*x)^3)*(c+d*x)^11, x, 1, (b^4*F^a*Gamma(-4, -((b*Log(F))/(c+d*x)^3))*Log(F)^4)/(3*d)}
	public void test01839() {
		check("Integrate(F^(a+b/(c+d*x)^3)*(c+d*x)^11, x)",
				"(b^4*F^a*Gamma(-4, -((b*Log(F))/(c+d*x)^3))*Log(F)^4)/(3*d)");

	}

	// {F^(a+b/(c+d*x)^3)/(c+d*x), x, 1, -(F^a*ExpIntegralEi((b*Log(F))/(c+d*x)^3))/(3*d)}
	public void test01840() {
		check("Integrate(F^(a+b/(c+d*x)^3)/(c+d*x), x)", "-(F^a*ExpIntegralEi((b*Log(F))/(c+d*x)^3))/(3*d)");

	}

	// {F^(a+b/(c+d*x)^3)/(c+d*x)^4, x, 1, -F^(a+b/(c+d*x)^3)/(3*b*d*Log(F))}
	public void test01841() {
		check("Integrate(F^(a+b/(c+d*x)^3)/(c+d*x)^4, x)", "-F^(a+b/(c+d*x)^3)/(3*b*d*Log(F))");

	}

	// {F^(a+b/(c+d*x)^3)/(c+d*x)^16, x, 1, -(F^a*Gamma(5, -((b*Log(F))/(c+d*x)^3)))/(3*b^5*d*Log(F)^5)}
	public void test01842() {
		check("Integrate(F^(a+b/(c+d*x)^3)/(c+d*x)^16, x)",
				"-(F^a*Gamma(5, -((b*Log(F))/(c+d*x)^3)))/(3*b^5*d*Log(F)^5)");

	}

	// {F^(a+b/(c+d*x)^3)/(c+d*x)^19, x, 1, (F^a*Gamma(6, -((b*Log(F))/(c+d*x)^3)))/(3*b^6*d*Log(F)^6)}
	public void test01843() {
		check("Integrate(F^(a+b/(c+d*x)^3)/(c+d*x)^19, x)",
				"(F^a*Gamma(6, -((b*Log(F))/(c+d*x)^3)))/(3*b^6*d*Log(F)^6)");

	}

	// {F^(a+b/(c+d*x)^3)*(c+d*x)^3, x, 1, (F^a*(c+d*x)^4*Gamma(-4/3, -((b*Log(F))/(c +
	// d*x)^3))*(-((b*Log(F))/(c+d*x)^3))^(4/3))/(3*d)}
	public void test01844() {
		check("Integrate(F^(a+b/(c+d*x)^3)*(c+d*x)^3, x)",
				"(F^a*(c+d*x)^4*Gamma(-4/3, -((b*Log(F))/(c+d*x)^3))*(-((b*Log(F))/(c+d*x)^3))^(4/3))/(3*d)");

	}

	// {F^(a+b/(c+d*x)^3)*(c+d*x), x, 1, (F^a*(c+d*x)^2*Gamma(-2/3, -((b*Log(F))/(c+d*x)^3))*(-((b*Log(F))/(c
	// +d*x)^3))^(2/3))/(3*d)}
	public void test01845() {
		check("Integrate(F^(a+b/(c+d*x)^3)*(c+d*x), x)",
				"(F^a*(c+d*x)^2*Gamma(-2/3, -((b*Log(F))/(c+d*x)^3))*(-((b*Log(F))/(c+d*x)^3))^(2/3))/(3*d)");

	}

	// {F^(a+b/(c+d*x)^3), x, 1, (F^a*(c+d*x)*Gamma(-1/3, -((b*Log(F))/(c+d*x)^3))*(-((b*Log(F))/(c +
	// d*x)^3))^(1/3))/(3*d)}
	public void test01846() {
		check("Integrate(F^(a+b/(c+d*x)^3), x)",
				"(F^a*(c+d*x)*Gamma(-1/3, -((b*Log(F))/(c+d*x)^3))*(-((b*Log(F))/(c+d*x)^3))^(1/3))/(3*d)");

	}

	// {F^(a+b/(c+d*x)^3)/(c+d*x)^2, x, 1, (F^a*Gamma(1/3, -((b*Log(F))/(c+d*x)^3)))/(3*d*(c +
	// d*x)*(-((b*Log(F))/(c+d*x)^3))^(1/3))}
	public void test01847() {
		check("Integrate(F^(a+b/(c+d*x)^3)/(c+d*x)^2, x)",
				"(F^a*Gamma(1/3, -((b*Log(F))/(c+d*x)^3)))/(3*d*(c+d*x)*(-((b*Log(F))/(c+d*x)^3))^(1/3))");

	}

	// {F^(a+b/(c+d*x)^3)/(c+d*x)^3, x, 1, (F^a*Gamma(2/3, -((b*Log(F))/(c+d*x)^3)))/(3*d*(c +
	// d*x)^2*(-((b*Log(F))/(c+d*x)^3))^(2/3))}
	public void test01848() {
		check("Integrate(F^(a+b/(c+d*x)^3)/(c+d*x)^3, x)",
				"(F^a*Gamma(2/3, -((b*Log(F))/(c+d*x)^3)))/(3*d*(c+d*x)^2*(-((b*Log(F))/(c+d*x)^3))^(2/3))");

	}

	// {F^(a+b/(c+d*x)^3)/(c+d*x)^5, x, 1, (F^a*Gamma(4/3, -((b*Log(F))/(c+d*x)^3)))/(3*d*(c +
	// d*x)^4*(-((b*Log(F))/(c+d*x)^3))^(4/3))}
	public void test01849() {
		check("Integrate(F^(a+b/(c+d*x)^3)/(c+d*x)^5, x)",
				"(F^a*Gamma(4/3, -((b*Log(F))/(c+d*x)^3)))/(3*d*(c+d*x)^4*(-((b*Log(F))/(c+d*x)^3))^(4/3))");

	}

	// {F^(a+b*(c+d*x)^n)*(c+d*x)^m, x, 1, -((F^a*(c+d*x)^(1+m)*Gamma((1+m)/n, -(b*(c +
	// d*x)^n*Log(F))))/(d*n*(-(b*(c+d*x)^n*Log(F)))^((1+m)/n)))}
	public void test01850() {
		check("Integrate(F^(a+b*(c+d*x)^n)*(c+d*x)^m, x)",
				"-((F^a*(c+d*x)^(1+m)*Gamma((1+m)/n, -(b*(c+d*x)^n*Log(F))))/(d*n*(-(b*(c+d*x)^n*Log(F)))^((1+m)/n)))");

	}

	// {F^(a+b*(c+d*x)^n)*(c+d*x)^3, x, 1, -((F^a*(c+d*x)^4*Gamma(4/n, -(b*(c+d*x)^n*Log(F))))/(d*n*(-(b*(c +
	// d*x)^n*Log(F)))^(4/n)))}
	public void test01851() {
		check("Integrate(F^(a+b*(c+d*x)^n)*(c+d*x)^3, x)",
				"-((F^a*(c+d*x)^4*Gamma(4/n, -(b*(c+d*x)^n*Log(F))))/(d*n*(-(b*(c+d*x)^n*Log(F)))^(4/n)))");

	}

	// {F^(a+b*(c+d*x)^n)*(c+d*x)^2, x, 1, -((F^a*(c+d*x)^3*Gamma(3/n, -(b*(c+d*x)^n*Log(F))))/(d*n*(-(b*(c +
	// d*x)^n*Log(F)))^(3/n)))}
	public void test01852() {
		check("Integrate(F^(a+b*(c+d*x)^n)*(c+d*x)^2, x)",
				"-((F^a*(c+d*x)^3*Gamma(3/n, -(b*(c+d*x)^n*Log(F))))/(d*n*(-(b*(c+d*x)^n*Log(F)))^(3/n)))");

	}

	// {F^(a+b*(c+d*x)^n)*(c+d*x), x, 1, -((F^a*(c+d*x)^2*Gamma(2/n, -(b*(c+d*x)^n*Log(F))))/(d*n*(-(b*(c +
	// d*x)^n*Log(F)))^(2/n)))}
	public void test01853() {
		check("Integrate(F^(a+b*(c+d*x)^n)*(c+d*x), x)",
				"-((F^a*(c+d*x)^2*Gamma(2/n, -(b*(c+d*x)^n*Log(F))))/(d*n*(-(b*(c+d*x)^n*Log(F)))^(2/n)))");

	}

	// {F^(a+b*(c+d*x)^n), x, 1, -((F^a*(c+d*x)*Gamma(n^(-1), -(b*(c+d*x)^n*Log(F))))/(d*n*(-(b*(c +
	// d*x)^n*Log(F)))^n^(-1)))}
	public void test01854() {
		check("Integrate(F^(a+b*(c+d*x)^n), x)",
				"-((F^a*(c+d*x)*Gamma(n^(-1), -(b*(c+d*x)^n*Log(F))))/(d*n*(-(b*(c+d*x)^n*Log(F)))^n^(-1)))");

	}

	// {F^(a+b*(c+d*x)^n)/(c+d*x), x, 1, (F^a*ExpIntegralEi(b*(c+d*x)^n*Log(F)))/(d*n)}
	public void test01855() {
		check("Integrate(F^(a+b*(c+d*x)^n)/(c+d*x), x)", "(F^a*ExpIntegralEi(b*(c+d*x)^n*Log(F)))/(d*n)");

	}

	// {F^(a+b*(c+d*x)^n)/(c+d*x)^2, x, 1, -((F^a*Gamma(-n^(-1), -(b*(c+d*x)^n*Log(F)))*(-(b*(c +
	// d*x)^n*Log(F)))^n^(-1))/(d*n*(c+d*x)))}
	public void test01856() {
		check("Integrate(F^(a+b*(c+d*x)^n)/(c+d*x)^2, x)",
				"-((F^a*Gamma(-n^(-1), -(b*(c+d*x)^n*Log(F)))*(-(b*(c+d*x)^n*Log(F)))^n^(-1))/(d*n*(c+d*x)))");

	}

	// {F^(a+b*(c+d*x)^n)/(c+d*x)^3, x, 1, -((F^a*Gamma(-2/n, -(b*(c+d*x)^n*Log(F)))*(-(b*(c +
	// d*x)^n*Log(F)))^(2/n))/(d*n*(c+d*x)^2))}
	public void test01857() {
		check("Integrate(F^(a+b*(c+d*x)^n)/(c+d*x)^3, x)",
				"-((F^a*Gamma(-2/n, -(b*(c+d*x)^n*Log(F)))*(-(b*(c+d*x)^n*Log(F)))^(2/n))/(d*n*(c+d*x)^2))");

	}

	// {F^(a+b*(c+d*x)^n)/(c+d*x)^4, x, 1, -((F^a*Gamma(-3/n, -(b*(c+d*x)^n*Log(F)))*(-(b*(c +
	// d*x)^n*Log(F)))^(3/n))/(d*n*(c+d*x)^3))}
	public void test01858() {
		check("Integrate(F^(a+b*(c+d*x)^n)/(c+d*x)^4, x)",
				"-((F^a*Gamma(-3/n, -(b*(c+d*x)^n*Log(F)))*(-(b*(c+d*x)^n*Log(F)))^(3/n))/(d*n*(c+d*x)^3))");

	}

	// {F^(a+b*(c+d*x)^n)*(c+d*x)^(-1+6*n), x, 1, -((F^a*Gamma(6, -(b*(c+d*x)^n*Log(F))))/(b^6*d*n*Log(F)^6))}
	public void test01859() {
		check("Integrate(F^(a+b*(c+d*x)^n)*(c+d*x)^(-1+6*n), x)",
				"-((F^a*Gamma(6, -(b*(c+d*x)^n*Log(F))))/(b^6*d*n*Log(F)^6))");

	}

	// {F^(a+b*(c+d*x)^n)*(c+d*x)^(-1+5*n), x, 1, (F^a*Gamma(5, -(b*(c+d*x)^n*Log(F))))/(b^5*d*n*Log(F)^5)}
	public void test01860() {
		check("Integrate(F^(a+b*(c+d*x)^n)*(c+d*x)^(-1+5*n), x)",
				"(F^a*Gamma(5, -(b*(c+d*x)^n*Log(F))))/(b^5*d*n*Log(F)^5)");

	}

	// {F^(a+b*(c+d*x)^n)*(c+d*x)^(-1+n), x, 1, F^(a+b*(c+d*x)^n)/(b*d*n*Log(F))}
	public void test01861() {
		check("Integrate(F^(a+b*(c+d*x)^n)*(c+d*x)^(-1+n), x)", "F^(a+b*(c+d*x)^n)/(b*d*n*Log(F))");

	}

	// {F^(a+b*(c+d*x)^n)/(c+d*x), x, 1, (F^a*ExpIntegralEi(b*(c+d*x)^n*Log(F)))/(d*n)}
	public void test01862() {
		check("Integrate(F^(a+b*(c+d*x)^n)/(c+d*x), x)", "(F^a*ExpIntegralEi(b*(c+d*x)^n*Log(F)))/(d*n)");

	}

	// {F^(a+b*(c+d*x)^n)*(c+d*x)^(-1-4*n), x, 1, -((b^4*F^a*Gamma(-4, -(b*(c +
	// d*x)^n*Log(F)))*Log(F)^4)/(d*n))}
	public void test01863() {
		check("Integrate(F^(a+b*(c+d*x)^n)*(c+d*x)^(-1-4*n), x)",
				"-((b^4*F^a*Gamma(-4, -(b*(c+d*x)^n*Log(F)))*Log(F)^4)/(d*n))");

	}

	// {F^(a+b*(c+d*x)^n)*(c+d*x)^(-1-5*n), x, 1, (b^5*F^a*Gamma(-5, -(b*(c+d*x)^n*Log(F)))*Log(F)^5)/(d*n)}
	public void test01864() {
		check("Integrate(F^(a+b*(c+d*x)^n)*(c+d*x)^(-1-5*n), x)",
				"(b^5*F^a*Gamma(-5, -(b*(c+d*x)^n*Log(F)))*Log(F)^5)/(d*n)");

	}

	// {F^(a+b*(c+d*x)^2), x, 1, (F^a*Sqrt(Pi)*Erfi(Sqrt(b)*(c+d*x)*Sqrt(Log(F))))/(2*Sqrt(b)*d*Sqrt(Log(F)))}
	public void test01865() {
		check("Integrate(F^(a+b*(c+d*x)^2), x)",
				"(F^a*Sqrt(Pi)*Erfi(Sqrt(b)*(c+d*x)*Sqrt(Log(F))))/(2*Sqrt(b)*d*Sqrt(Log(F)))");

	}

	// {E^(e*(c+d*x)^3), x, 1, -((c+d*x)*Gamma(1/3, -(e*(c+d*x)^3)))/(3*d*(-(e*(c+d*x)^3))^(1/3))}
	public void test01866() {
		check("Integrate(E^(e*(c+d*x)^3), x)", "-((c+d*x)*Gamma(1/3, -(e*(c+d*x)^3)))/(3*d*(-(e*(c+d*x)^3))^(1/3))");

	}

	// {E^(e/(c+d*x)^3), x, 1, ((-(e/(c+d*x)^3))^(1/3)*(c+d*x)*Gamma(-1/3, -(e/(c+d*x)^3)))/(3*d)}
	public void test01867() {
		check("Integrate(E^(e/(c+d*x)^3), x)", "((-(e/(c+d*x)^3))^(1/3)*(c+d*x)*Gamma(-1/3, -(e/(c+d*x)^3)))/(3*d)");

	}

	// {E^((a+b*x)*(c+d*x))/x, x, 1, Unintegrable(E^(a*c+(b*c+a*d)*x+b*d*x^2)/x, x)}
	public void test01868() {
		check("Integrate(E^((a+b*x)*(c+d*x))/x, x)", "Unintegrable(E^(a*c+(b*c+a*d)*x+b*d*x^2)/x, x)");

	}

	// {f^(a+b*x+c*x^2)*(b+2*c*x), x, 1, f^(a+b*x+c*x^2)/Log(f)}
	public void test01869() {
		check("Integrate(f^(a+b*x+c*x^2)*(b+2*c*x), x)", "f^(a+b*x+c*x^2)/Log(f)");

	}

	// {f^(a+b*x+c*x^2)/(b+2*c*x), x, 1, (f^(a-b^2/(4*c))*ExpIntegralEi(((b+2*c*x)^2*Log(f))/(4*c)))/(4*c)}
	public void test01870() {
		check("Integrate(f^(a+b*x+c*x^2)/(b+2*c*x), x)",
				"(f^(a-b^2/(4*c))*ExpIntegralEi(((b+2*c*x)^2*Log(f))/(4*c)))/(4*c)");

	}

	// {f^(b*x+c*x^2)*(b+2*c*x), x, 1, f^(b*x+c*x^2)/Log(f)}
	public void test01871() {
		check("Integrate(f^(b*x+c*x^2)*(b+2*c*x), x)", "f^(b*x+c*x^2)/Log(f)");

	}

	// {f^(b*x+c*x^2)/(b+2*c*x), x, 1, ExpIntegralEi(((b+2*c*x)^2*Log(f))/(4*c))/(4*c*f^(b^2/(4*c)))}
	public void test01872() {
		check("Integrate(f^(b*x+c*x^2)/(b+2*c*x), x)", "ExpIntegralEi(((b+2*c*x)^2*Log(f))/(4*c))/(4*c*f^(b^2/(4*c)))");

	}

	// {(d^2-e^2*x^2)^(-1), x, 1, ArcTanh((e*x)/d)/(d*e)}
	public void test01873() {
		check("Integrate((d^2-e^2*x^2)^(-1), x)", "ArcTanh((e*x)/d)/(d*e)");

	}

	// {F^(a+b*x+c*x^3)*(b+3*c*x^2), x, 1, F^(a+b*x+c*x^3)/Log(F)}
	public void test01874() {
		check("Integrate(F^(a+b*x+c*x^3)*(b+3*c*x^2), x)", "F^(a+b*x+c*x^3)/Log(F)");

	}

	// {(F^(a+b*x+c*x^2)^(-1)*(b+2*c*x))/(a+b*x+c*x^2)^2, x, 1, -(F^(a+b*x+c*x^2)^(-1)/Log(F))}
	public void test01875() {
		check("Integrate((F^(a+b*x+c*x^2)^(-1)*(b+2*c*x))/(a+b*x+c*x^2)^2, x)", "-(F^(a+b*x+c*x^2)^(-1)/Log(F))");

	}

	// {E^(a+b*x+c*x^2)*(b+2*c*x), x, 1, E^(a+b*x+c*x^2)}
	public void test01876() {
		check("Integrate(E^(a+b*x+c*x^2)*(b+2*c*x), x)", "E^(a+b*x+c*x^2)");

	}

	// {E^(2-x^2)*x, x, 1, -E^(2-x^2)/2}
	public void test01877() {
		check("Integrate(E^(2-x^2)*x, x)", "-E^(2-x^2)/2");

	}

	// {E^Sqrt(4+x)/Sqrt(4+x), x, 1, 2*E^Sqrt(4+x)}
	public void test01878() {
		check("Integrate(E^Sqrt(4+x)/Sqrt(4+x), x)", "2*E^Sqrt(4+x)");

	}

	// {E^(1+x^2)*x, x, 1, E^(1+x^2)/2}
	public void test01879() {
		check("Integrate(E^(1+x^2)*x, x)", "E^(1+x^2)/2");

	}

	// {E^(1+x^3)*x^2, x, 1, E^(1+x^3)/3}
	public void test01880() {
		check("Integrate(E^(1+x^3)*x^2, x)", "E^(1+x^3)/3");

	}

	// {E^Sqrt(x)/Sqrt(x), x, 1, 2*E^Sqrt(x)}
	public void test01881() {
		check("Integrate(E^Sqrt(x)/Sqrt(x), x)", "2*E^Sqrt(x)");

	}

	// {E^x^(1/3)/x^(2/3), x, 1, 3*E^x^(1/3)}
	public void test01882() {
		check("Integrate(E^x^(1/3)/x^(2/3), x)", "3*E^x^(1/3)");

	}

	// {Cos(3*x)/E^x, x, 1, -Cos(3*x)/(10*E^x)+(3*Sin(3*x))/(10*E^x)}
	public void test01883() {
		check("Integrate(Cos(3*x)/E^x, x)", "-Cos(3*x)/(10*E^x)+(3*Sin(3*x))/(10*E^x)");

	}

	// {E^(3*x)*Cos(5*x), x, 1, (3*E^(3*x)*Cos(5*x))/34+(5*E^(3*x)*Sin(5*x))/34}
	public void test01884() {
		check("Integrate(E^(3*x)*Cos(5*x), x)", "(3*E^(3*x)*Cos(5*x))/34+(5*E^(3*x)*Sin(5*x))/34");

	}

	// {E^x*Cos(4+3*x), x, 1, (E^x*Cos(4+3*x))/10+(3*E^x*Sin(4+3*x))/10}
	public void test01885() {
		check("Integrate(E^x*Cos(4+3*x), x)", "(E^x*Cos(4+3*x))/10+(3*E^x*Sin(4+3*x))/10");

	}

	// {E^(6*x)*Sin(3*x), x, 1, -(E^(6*x)*Cos(3*x))/15+(2*E^(6*x)*Sin(3*x))/15}
	public void test01886() {
		check("Integrate(E^(6*x)*Sin(3*x), x)", "-(E^(6*x)*Cos(3*x))/15+(2*E^(6*x)*Sin(3*x))/15");

	}

	// {(E^x^2*x^3)/(1+x^2)^2, x, 1, E^x^2/(2*(1+x^2))}
	public void test01887() {
		check("Integrate((E^x^2*x^3)/(1+x^2)^2, x)", "E^x^2/(2*(1+x^2))");

	}

	// {(1+E^x)/Sqrt(E^x+x), x, 1, 2*Sqrt(E^x+x)}
	public void test01888() {
		check("Integrate((1+E^x)/Sqrt(E^x+x), x)", "2*Sqrt(E^x+x)");

	}

	// {(1+E^x)/(E^x+x), x, 1, Log(E^x+x)}
	public void test01889() {
		check("Integrate((1+E^x)/(E^x+x), x)", "Log(E^x+x)");

	}

	// {3^(1+x^2)*x, x, 1, 3^(1+x^2)/(2*Log(3))}
	public void test01890() {
		check("Integrate(3^(1+x^2)*x, x)", "3^(1+x^2)/(2*Log(3))");

	}

	// {2^Sqrt(x)/Sqrt(x), x, 1, 2^(1+Sqrt(x))/Log(2)}
	public void test01891() {
		check("Integrate(2^Sqrt(x)/Sqrt(x), x)", "2^(1+Sqrt(x))/Log(2)");

	}

	// {2^x^(-1)/x^2, x, 1, -(2^x^(-1)/Log(2))}
	public void test01892() {
		check("Integrate(2^x^(-1)/x^2, x)", "-(2^x^(-1)/Log(2))");

	}

	// {10^Sqrt(x)/Sqrt(x), x, 1, (2^(1+Sqrt(x))*5^Sqrt(x))/Log(10)}
	public void test01893() {
		check("Integrate(10^Sqrt(x)/Sqrt(x), x)", "(2^(1+Sqrt(x))*5^Sqrt(x))/Log(10)");

	}

	// {(E^x*x^2)/Sqrt(5*E^x+x^3), x, 1, (2*x^2*Sqrt(5*E^x+x^3))/5-(3*CannotIntegrate(x^4/Sqrt(5*E^x+x^3), x))/5
	// -(4*CannotIntegrate(x*Sqrt(5*E^x+x^3), x))/5}
	public void test01894() {
		check("Integrate((E^x*x^2)/Sqrt(5*E^x+x^3), x)",
				"(2*x^2*Sqrt(5*E^x+x^3))/5-(3*CannotIntegrate(x^4/Sqrt(5*E^x+x^3), x))/5-(4*CannotIntegrate(x*Sqrt(5*E^x+x^3), x))/5");

	}

	// {-((1+E^x)/(E^x+x)^(1/3)), x, 1, (-3*(E^x+x)^(2/3))/2}
	public void test01895() {
		check("Integrate(-((1+E^x)/(E^x+x)^(1/3)), x)", "(-3*(E^x+x)^(2/3))/2");

	}

	// {x/(E^x+x)^(1/3), x, 1, (-3*(E^x+x)^(2/3))/2+CannotIntegrate((E^x+x)^(-1/3), x)+CannotIntegrate((E^x +
	// x)^(2/3), x)}
	public void test01896() {
		check("Integrate(x/(E^x+x)^(1/3), x)",
				"(-3*(E^x+x)^(2/3))/2+CannotIntegrate((E^x+x)^(-1/3), x)+CannotIntegrate((E^x+x)^(2/3), x)");

	}

	// {E^x^n*x^m, x, 1, -((x^(1+m)*Gamma((1+m)/n, -x^n))/(n*(-x^n)^((1+m)/n)))}
	public void test01897() {
		check("Integrate(E^x^n*x^m, x)", "-((x^(1+m)*Gamma((1+m)/n, -x^n))/(n*(-x^n)^((1+m)/n)))");

	}

	// {f^x^n*x^m, x, 1, -((x^(1+m)*Gamma((1+m)/n, -(x^n*Log(f))))/(n*(-(x^n*Log(f)))^((1+m)/n)))}
	public void test01898() {
		check("Integrate(f^x^n*x^m, x)", "-((x^(1+m)*Gamma((1+m)/n, -(x^n*Log(f))))/(n*(-(x^n*Log(f)))^((1+m)/n)))");

	}

	// {E^(a+b*x)^n*(a+b*x)^m, x, 1, -(((a+b*x)^(1+m)*Gamma((1+m)/n, -(a+b*x)^n))/(b*n*(-(a+b*x)^n)^((1 +
	// m)/n)))}
	public void test01899() {
		check("Integrate(E^(a+b*x)^n*(a+b*x)^m, x)",
				"-(((a+b*x)^(1+m)*Gamma((1+m)/n, -(a+b*x)^n))/(b*n*(-(a+b*x)^n)^((1+m)/n)))");

	}

	// {f^(a+b*x)^n*(a+b*x)^m, x, 1, -(((a+b*x)^(1+m)*Gamma((1+m)/n, -((a+b*x)^n*Log(f))))/(b*n*(-((a +
	// b*x)^n*Log(f)))^((1+m)/n)))}
	public void test01900() {
		check("Integrate(f^(a+b*x)^n*(a+b*x)^m, x)",
				"-(((a+b*x)^(1+m)*Gamma((1+m)/n, -((a+b*x)^n*Log(f))))/(b*n*(-((a+b*x)^n*Log(f)))^((1+m)/n)))");

	}

	// {Log(c*x)/(1-c*x), x, 1, PolyLog(2, 1-c*x)/c}
	public void test01901() {
		check("Integrate(Log(c*x)/(1-c*x), x)", "PolyLog(2, 1-c*x)/c");

	}

	// {Log(x/c)/(c-x), x, 1, PolyLog(2, 1-x/c)}
	public void test01902() {
		check("Integrate(Log(x/c)/(c-x), x)", "PolyLog(2, 1-x/c)");

	}

	// {Sqrt(a+b*Log(c*x^n))/(d+e*x)^2, x, 1, (x*Sqrt(a+b*Log(c*x^n)))/(d*(d+e*x))-(b*n*Unintegrable(1/((d +
	// e*x)*Sqrt(a+b*Log(c*x^n))), x))/(2*d)}
	public void test01903() {
		check("Integrate(Sqrt(a+b*Log(c*x^n))/(d+e*x)^2, x)",
				"(x*Sqrt(a+b*Log(c*x^n)))/(d*(d+e*x))-(b*n*Unintegrable(1/((d+e*x)*Sqrt(a+b*Log(c*x^n))), x))/(2*d)");

	}

	// {Sqrt(a+b*Log(c*x^n))/(d+e*x)^3, x, 1, -Sqrt(a+b*Log(c*x^n))/(2*e*(d+e*x)^2)+(b*n*Unintegrable(1/(x*(d
	// +e*x)^2*Sqrt(a+b*Log(c*x^n))), x))/(4*e)}
	public void test01904() {
		check("Integrate(Sqrt(a+b*Log(c*x^n))/(d+e*x)^3, x)",
				"-Sqrt(a+b*Log(c*x^n))/(2*e*(d+e*x)^2)+(b*n*Unintegrable(1/(x*(d+e*x)^2*Sqrt(a+b*Log(c*x^n))), x))/(4*e)");

	}

	// {(f*x)^m*(a+b*Log(c*x^n)), x, 1, -((b*n*(f*x)^(1+m))/(f*(1+m)^2))+((f*x)^(1+m)*(a +
	// b*Log(c*x^n)))/(f*(1+m))}
	public void test01905() {
		check("Integrate((f*x)^m*(a+b*Log(c*x^n)), x)",
				"-((b*n*(f*x)^(1+m))/(f*(1+m)^2))+((f*x)^(1+m)*(a+b*Log(c*x^n)))/(f*(1+m))");

	}

	// {(f*x)^m*(a+b*Log(c*x^n)), x, 1, -((b*n*(f*x)^(1+m))/(f*(1+m)^2))+((f*x)^(1+m)*(a +
	// b*Log(c*x^n)))/(f*(1+m))}
	public void test01906() {
		check("Integrate((f*x)^m*(a+b*Log(c*x^n)), x)",
				"-((b*n*(f*x)^(1+m))/(f*(1+m)^2))+((f*x)^(1+m)*(a+b*Log(c*x^n)))/(f*(1+m))");

	}

	// {(f*x)^(-1+m)*(a+b*Log(c*x^n)), x, 1, -((b*n*(f*x)^m)/(f*m^2))+((f*x)^m*(a+b*Log(c*x^n)))/(f*m)}
	public void test01907() {
		check("Integrate((f*x)^(-1+m)*(a+b*Log(c*x^n)), x)",
				"-((b*n*(f*x)^m)/(f*m^2))+((f*x)^m*(a+b*Log(c*x^n)))/(f*m)");

	}

	// {(f*x)^m*(a+b*Log(c*x^n)), x, 1, -((b*n*(f*x)^(1+m))/(f*(1+m)^2))+((f*x)^(1+m)*(a +
	// b*Log(c*x^n)))/(f*(1+m))}
	public void test01908() {
		check("Integrate((f*x)^m*(a+b*Log(c*x^n)), x)",
				"-((b*n*(f*x)^(1+m))/(f*(1+m)^2))+((f*x)^(1+m)*(a+b*Log(c*x^n)))/(f*(1+m))");

	}

	// {x^3*Log(c*x), x, 1, -x^4/16+(x^4*Log(c*x))/4}
	public void test01909() {
		check("Integrate(x^3*Log(c*x), x)", "-x^4/16+(x^4*Log(c*x))/4");

	}

	// {x^2*Log(c*x), x, 1, -x^3/9+(x^3*Log(c*x))/3}
	public void test01910() {
		check("Integrate(x^2*Log(c*x), x)", "-x^3/9+(x^3*Log(c*x))/3");

	}

	// {x*Log(c*x), x, 1, -x^2/4+(x^2*Log(c*x))/2}
	public void test01911() {
		check("Integrate(x*Log(c*x), x)", "-x^2/4+(x^2*Log(c*x))/2");

	}

	// {Log(c*x), x, 1, -x+x*Log(c*x)}
	public void test01912() {
		check("Integrate(Log(c*x), x)", "-x+x*Log(c*x)");

	}

	// {Log(c*x)/x, x, 1, Log(c*x)^2/2}
	public void test01913() {
		check("Integrate(Log(c*x)/x, x)", "Log(c*x)^2/2");

	}

	// {Log(c*x)/x^2, x, 1, -x^(-1)-Log(c*x)/x}
	public void test01914() {
		check("Integrate(Log(c*x)/x^2, x)", "-x^(-1)-Log(c*x)/x");

	}

	// {Log(c*x)/x^3, x, 1, -1/(4*x^2)-Log(c*x)/(2*x^2)}
	public void test01915() {
		check("Integrate(Log(c*x)/x^3, x)", "-1/(4*x^2)-Log(c*x)/(2*x^2)");

	}

	// {Log(c*x)^(-1), x, 1, LogIntegral(c*x)/c}
	public void test01916() {
		check("Integrate(Log(c*x)^(-1), x)", "LogIntegral(c*x)/c");

	}

	// {x^3*(a+b*Log(c*x^n)), x, 1, -(b*n*x^4)/16+(x^4*(a+b*Log(c*x^n)))/4}
	public void test01917() {
		check("Integrate(x^3*(a+b*Log(c*x^n)), x)", "-(b*n*x^4)/16+(x^4*(a+b*Log(c*x^n)))/4");

	}

	// {x^2*(a+b*Log(c*x^n)), x, 1, -(b*n*x^3)/9+(x^3*(a+b*Log(c*x^n)))/3}
	public void test01918() {
		check("Integrate(x^2*(a+b*Log(c*x^n)), x)", "-(b*n*x^3)/9+(x^3*(a+b*Log(c*x^n)))/3");

	}

	// {x*(a+b*Log(c*x^n)), x, 1, -(b*n*x^2)/4+(x^2*(a+b*Log(c*x^n)))/2}
	public void test01919() {
		check("Integrate(x*(a+b*Log(c*x^n)), x)", "-(b*n*x^2)/4+(x^2*(a+b*Log(c*x^n)))/2");

	}

	// {(a+b*Log(c*x^n))/x, x, 1, (a+b*Log(c*x^n))^2/(2*b*n)}
	public void test01920() {
		check("Integrate((a+b*Log(c*x^n))/x, x)", "(a+b*Log(c*x^n))^2/(2*b*n)");

	}

	// {(a+b*Log(c*x^n))/x^2, x, 1, -((b*n)/x)-(a+b*Log(c*x^n))/x}
	public void test01921() {
		check("Integrate((a+b*Log(c*x^n))/x^2, x)", "-((b*n)/x)-(a+b*Log(c*x^n))/x");

	}

	// {(a+b*Log(c*x^n))/x^3, x, 1, -(b*n)/(4*x^2)-(a+b*Log(c*x^n))/(2*x^2)}
	public void test01922() {
		check("Integrate((a+b*Log(c*x^n))/x^3, x)", "-(b*n)/(4*x^2)-(a+b*Log(c*x^n))/(2*x^2)");

	}

	// {(d*x)^(5/2)*(a+b*Log(c*x^n)), x, 1, (-4*b*n*(d*x)^(7/2))/(49*d)+(2*(d*x)^(7/2)*(a+b*Log(c*x^n)))/(7*d)}
	public void test01923() {
		check("Integrate((d*x)^(5/2)*(a+b*Log(c*x^n)), x)",
				"(-4*b*n*(d*x)^(7/2))/(49*d)+(2*(d*x)^(7/2)*(a+b*Log(c*x^n)))/(7*d)");

	}

	// {(d*x)^(3/2)*(a+b*Log(c*x^n)), x, 1, (-4*b*n*(d*x)^(5/2))/(25*d)+(2*(d*x)^(5/2)*(a+b*Log(c*x^n)))/(5*d)}
	public void test01924() {
		check("Integrate((d*x)^(3/2)*(a+b*Log(c*x^n)), x)",
				"(-4*b*n*(d*x)^(5/2))/(25*d)+(2*(d*x)^(5/2)*(a+b*Log(c*x^n)))/(5*d)");

	}

	// {Sqrt(d*x)*(a+b*Log(c*x^n)), x, 1, (-4*b*n*(d*x)^(3/2))/(9*d)+(2*(d*x)^(3/2)*(a+b*Log(c*x^n)))/(3*d)}
	public void test01925() {
		check("Integrate(Sqrt(d*x)*(a+b*Log(c*x^n)), x)",
				"(-4*b*n*(d*x)^(3/2))/(9*d)+(2*(d*x)^(3/2)*(a+b*Log(c*x^n)))/(3*d)");

	}

	// {(a+b*Log(c*x^n))/Sqrt(d*x), x, 1, (-4*b*n*Sqrt(d*x))/d+(2*Sqrt(d*x)*(a+b*Log(c*x^n)))/d}
	public void test01926() {
		check("Integrate((a+b*Log(c*x^n))/Sqrt(d*x), x)", "(-4*b*n*Sqrt(d*x))/d+(2*Sqrt(d*x)*(a+b*Log(c*x^n)))/d");

	}

	// {(a+b*Log(c*x^n))/(d*x)^(3/2), x, 1, (-4*b*n)/(d*Sqrt(d*x))-(2*(a+b*Log(c*x^n)))/(d*Sqrt(d*x))}
	public void test01927() {
		check("Integrate((a+b*Log(c*x^n))/(d*x)^(3/2), x)",
				"(-4*b*n)/(d*Sqrt(d*x))-(2*(a+b*Log(c*x^n)))/(d*Sqrt(d*x))");

	}

	// {(a+b*Log(c*x^n))/(d*x)^(5/2), x, 1, (-4*b*n)/(9*d*(d*x)^(3/2))-(2*(a+b*Log(c*x^n)))/(3*d*(d*x)^(3/2))}
	public void test01928() {
		check("Integrate((a+b*Log(c*x^n))/(d*x)^(5/2), x)",
				"(-4*b*n)/(9*d*(d*x)^(3/2))-(2*(a+b*Log(c*x^n)))/(3*d*(d*x)^(3/2))");

	}

	// {(d*x)^m*(a+(a*(1+m)*Log(c*x^n))/n), x, 1, (a*(d*x)^(1+m)*Log(c*x^n))/(d*n)}
	public void test01929() {
		check("Integrate((d*x)^m*(a+(a*(1+m)*Log(c*x^n))/n), x)", "(a*(d*x)^(1+m)*Log(c*x^n))/(d*n)");

	}

	// {(d*x)^m*(a+b*Log(c*x^n)), x, 1, -((b*n*(d*x)^(1+m))/(d*(1+m)^2))+((d*x)^(1+m)*(a +
	// b*Log(c*x^n)))/(d*(1+m))}
	public void test01930() {
		check("Integrate((d*x)^m*(a+b*Log(c*x^n)), x)",
				"-((b*n*(d*x)^(1+m))/(d*(1+m)^2))+((d*x)^(1+m)*(a+b*Log(c*x^n)))/(d*(1+m))");

	}

	// {(d*x)^(-1+n)*Log(c*x^n), x, 1, -((d*x)^n/(d*n))+((d*x)^n*Log(c*x^n))/(d*n)}
	public void test01931() {
		check("Integrate((d*x)^(-1+n)*Log(c*x^n), x)", "-((d*x)^n/(d*n))+((d*x)^n*Log(c*x^n))/(d*n)");

	}

	// {PolyLog(k, e*x^q)/(x*(a+b*Log(c*x^n))^2), x, 1, -(PolyLog(k, e*x^q)/(b*n*(a+b*Log(c*x^n)))) +
	// (q*Unintegrable(PolyLog(-1+k, e*x^q)/(x*(a+b*Log(c*x^n))), x))/(b*n)}
	public void test01932() {
		check("Integrate(PolyLog(k, e*x^q)/(x*(a+b*Log(c*x^n))^2), x)",
				"-(PolyLog(k, e*x^q)/(b*n*(a+b*Log(c*x^n))))+(q*Unintegrable(PolyLog(-1+k, e*x^q)/(x*(a+b*Log(c*x^n))), x))/(b*n)");

	}

	// {Log(1+e*x)/x, x, 1, -PolyLog(2, -(e*x))}
	public void test01933() {
		check("Integrate(Log(1+e*x)/x, x)", "-PolyLog(2, -(e*x))");

	}

	// {Log(e*x)/x, x, 1, Log(e*x)^2/2}
	public void test01934() {
		check("Integrate(Log(e*x)/x, x)", "Log(e*x)^2/2");

	}

	// {(a+b*Log(e*x))/x, x, 1, (a+b*Log(e*x))^2/(2*b)}
	public void test01935() {
		check("Integrate((a+b*Log(e*x))/x, x)", "(a+b*Log(e*x))^2/(2*b)");

	}

	// {Sqrt(a+b*Log(c*(d+e*x)^n))/(f+g*x)^2, x, 1, ((d+e*x)*Sqrt(a+b*Log(c*(d+e*x)^n)))/((e*f-d*g)*(f +
	// g*x))-(b*e*n*Unintegrable(1/((f+g*x)*Sqrt(a+b*Log(c*(d+e*x)^n))), x))/(2*(e*f-d*g))}
	public void test01936() {
		check("Integrate(Sqrt(a+b*Log(c*(d+e*x)^n))/(f+g*x)^2, x)",
				"((d+e*x)*Sqrt(a+b*Log(c*(d+e*x)^n)))/((e*f-d*g)*(f+g*x))-(b*e*n*Unintegrable(1/((f+g*x)*Sqrt(a+b*Log(c*(d+e*x)^n))), x))/(2*(e*f-d*g))");

	}

	// {Sqrt(a+b*Log(c*(d+e*x)^n))/(f+g*x)^3, x, 1, -Sqrt(a+b*Log(c*(d+e*x)^n))/(2*g*(f+g*x)^2) +
	// (b*e*n*Unintegrable(1/((d+e*x)*(f+g*x)^2*Sqrt(a+b*Log(c*(d+e*x)^n))), x))/(4*g)}
	public void test01937() {
		check("Integrate(Sqrt(a+b*Log(c*(d+e*x)^n))/(f+g*x)^3, x)",
				"-Sqrt(a+b*Log(c*(d+e*x)^n))/(2*g*(f+g*x)^2)+(b*e*n*Unintegrable(1/((d+e*x)*(f+g*x)^2*Sqrt(a+b*Log(c*(d+e*x)^n))), x))/(4*g)");

	}

	// {(a+b*Log(c*(d+e*x)^n))^(3/2)/(f+g*x)^2, x, 1, ((d+e*x)*(a+b*Log(c*(d+e*x)^n))^(3/2))/((e*f-d*g)*(f
	// +g*x))-(3*b*e*n*Unintegrable(Sqrt(a+b*Log(c*(d+e*x)^n))/(f+g*x), x))/(2*(e*f-d*g))}
	public void test01938() {
		check("Integrate((a+b*Log(c*(d+e*x)^n))^(3/2)/(f+g*x)^2, x)",
				"((d+e*x)*(a+b*Log(c*(d+e*x)^n))^(3/2))/((e*f-d*g)*(f+g*x))-(3*b*e*n*Unintegrable(Sqrt(a+b*Log(c*(d+e*x)^n))/(f+g*x), x))/(2*(e*f-d*g))");

	}

	// {(a+b*Log(c*(d+e*x)^n))^(3/2)/(f+g*x)^3, x, 1, -(a+b*Log(c*(d+e*x)^n))^(3/2)/(2*g*(f+g*x)^2) +
	// (3*b*e*n*Unintegrable(Sqrt(a+b*Log(c*(d+e*x)^n))/((d+e*x)*(f+g*x)^2), x))/(4*g)}
	public void test01939() {
		check("Integrate((a+b*Log(c*(d+e*x)^n))^(3/2)/(f+g*x)^3, x)",
				"-(a+b*Log(c*(d+e*x)^n))^(3/2)/(2*g*(f+g*x)^2)+(3*b*e*n*Unintegrable(Sqrt(a+b*Log(c*(d+e*x)^n))/((d+e*x)*(f+g*x)^2), x))/(4*g)");

	}

	// {(a+b*Log(c*(d+e*x)^n))^(5/2)/(f+g*x)^2, x, 1, ((d+e*x)*(a+b*Log(c*(d+e*x)^n))^(5/2))/((e*f-d*g)*(f
	// +g*x))-(5*b*e*n*Unintegrable((a+b*Log(c*(d+e*x)^n))^(3/2)/(f+g*x), x))/(2*(e*f-d*g))}
	public void test01940() {
		check("Integrate((a+b*Log(c*(d+e*x)^n))^(5/2)/(f+g*x)^2, x)",
				"((d+e*x)*(a+b*Log(c*(d+e*x)^n))^(5/2))/((e*f-d*g)*(f+g*x))-(5*b*e*n*Unintegrable((a+b*Log(c*(d+e*x)^n))^(3/2)/(f+g*x), x))/(2*(e*f-d*g))");

	}

	// {(a+b*Log(c*(d+e*x)^n))^(5/2)/(f+g*x)^3, x, 1, -(a+b*Log(c*(d+e*x)^n))^(5/2)/(2*g*(f+g*x)^2) +
	// (5*b*e*n*Unintegrable((a+b*Log(c*(d+e*x)^n))^(3/2)/((d+e*x)*(f+g*x)^2), x))/(4*g)}
	public void test01941() {
		check("Integrate((a+b*Log(c*(d+e*x)^n))^(5/2)/(f+g*x)^3, x)",
				"-(a+b*Log(c*(d+e*x)^n))^(5/2)/(2*g*(f+g*x)^2)+(5*b*e*n*Unintegrable((a+b*Log(c*(d+e*x)^n))^(3/2)/((d+e*x)*(f+g*x)^2), x))/(4*g)");

	}

	// {Sqrt(f+g*x)*Sqrt(a+b*Log(c*(d+e*x)^n)), x, 1, (2*(f+g*x)^(3/2)*Sqrt(a+b*Log(c*(d+e*x)^n)))/(3*g) -
	// (b*e*n*Unintegrable((f+g*x)^(3/2)/((d+e*x)*Sqrt(a+b*Log(c*(d+e*x)^n))), x))/(3*g)}
	public void test01942() {
		check("Integrate(Sqrt(f+g*x)*Sqrt(a+b*Log(c*(d+e*x)^n)), x)",
				"(2*(f+g*x)^(3/2)*Sqrt(a+b*Log(c*(d+e*x)^n)))/(3*g)-(b*e*n*Unintegrable((f+g*x)^(3/2)/((d+e*x)*Sqrt(a+b*Log(c*(d+e*x)^n))), x))/(3*g)");

	}

	// {Sqrt(a+b*Log(c*(d+e*x)^n))/Sqrt(f+g*x), x, 1, (2*Sqrt(f+g*x)*Sqrt(a+b*Log(c*(d+e*x)^n)))/g -
	// (b*e*n*Unintegrable(Sqrt(f+g*x)/((d+e*x)*Sqrt(a+b*Log(c*(d+e*x)^n))), x))/g}
	public void test01943() {
		check("Integrate(Sqrt(a+b*Log(c*(d+e*x)^n))/Sqrt(f+g*x), x)",
				"(2*Sqrt(f+g*x)*Sqrt(a+b*Log(c*(d+e*x)^n)))/g-(b*e*n*Unintegrable(Sqrt(f+g*x)/((d+e*x)*Sqrt(a+b*Log(c*(d+e*x)^n))), x))/g");

	}

	// {Sqrt(a+b*Log(c*(d+e*x)^n))/(f+g*x)^(3/2), x, 1, (-2*Sqrt(a+b*Log(c*(d+e*x)^n)))/(g*Sqrt(f+g*x)) +
	// (b*e*n*Unintegrable(1/((d+e*x)*Sqrt(f+g*x)*Sqrt(a+b*Log(c*(d+e*x)^n))), x))/g}
	public void test01944() {
		check("Integrate(Sqrt(a+b*Log(c*(d+e*x)^n))/(f+g*x)^(3/2), x)",
				"(-2*Sqrt(a+b*Log(c*(d+e*x)^n)))/(g*Sqrt(f+g*x))+(b*e*n*Unintegrable(1/((d+e*x)*Sqrt(f+g*x)*Sqrt(a+b*Log(c*(d+e*x)^n))), x))/g");

	}

	// {Log((a*(1-c)+b*(1+c)*x)/(a+b*x))/(a^2-b^2*x^2), x, 1, PolyLog(2, 1-(a*(1-c)+b*(1+c)*x)/(a +
	// b*x))/(2*a*b)}
	public void test01945() {
		check("Integrate(Log((a*(1-c)+b*(1+c)*x)/(a+b*x))/(a^2-b^2*x^2), x)",
				"PolyLog(2, 1-(a*(1-c)+b*(1+c)*x)/(a+b*x))/(2*a*b)");

	}

	// {Log(1-(c*(a-b*x))/(a+b*x))/(a^2-b^2*x^2), x, 1, PolyLog(2, (c*(a-b*x))/(a+b*x))/(2*a*b)}
	public void test01946() {
		check("Integrate(Log(1-(c*(a-b*x))/(a+b*x))/(a^2-b^2*x^2), x)", "PolyLog(2, (c*(a-b*x))/(a+b*x))/(2*a*b)");

	}

	// {(Log(a+b*x)*Log(c+d*x))/x, x, 1, Log(-((b*x)/a))*Log(a+b*x)*Log(c+d*x)+((Log(-((b*x)/a))+Log((b*c -
	// a*d)/(b*(c+d*x)))-Log(-(((b*c-a*d)*x)/(a*(c+d*x)))))*Log((a*(c+d*x))/(c*(a+b*x)))^2)/2 -
	// ((Log(-((b*x)/a))-Log(-((d*x)/c)))*(Log(a+b*x)+Log((a*(c+d*x))/(c*(a+b*x))))^2)/2+(Log(c+d*x) -
	// Log((a*(c+d*x))/(c*(a+b*x))))*PolyLog(2, 1+(b*x)/a)+Log((a*(c+d*x))/(c*(a+b*x)))*PolyLog(2, (c*(a +
	// b*x))/(a*(c+d*x)))-Log((a*(c+d*x))/(c*(a+b*x)))*PolyLog(2, (d*(a+b*x))/(b*(c+d*x)))+(Log(a+b*x) +
	// Log((a*(c+d*x))/(c*(a+b*x))))*PolyLog(2, 1+(d*x)/c)-PolyLog(3, 1+(b*x)/a)+PolyLog(3, (c*(a +
	// b*x))/(a*(c+d*x)))-PolyLog(3, (d*(a+b*x))/(b*(c+d*x)))-PolyLog(3, 1+(d*x)/c)}
	public void test01947() {
		check("Integrate((Log(a+b*x)*Log(c+d*x))/x, x)",
				"Log(-((b*x)/a))*Log(a+b*x)*Log(c+d*x)+((Log(-((b*x)/a))+Log((b*c-a*d)/(b*(c+d*x)))-Log(-(((b*c-a*d)*x)/(a*(c+d*x)))))*Log((a*(c+d*x))/(c*(a+b*x)))^2)/2-((Log(-((b*x)/a))-Log(-((d*x)/c)))*(Log(a+b*x)+Log((a*(c+d*x))/(c*(a+b*x))))^2)/2+(Log(c+d*x)-Log((a*(c+d*x))/(c*(a+b*x))))*PolyLog(2, 1+(b*x)/a)+Log((a*(c+d*x))/(c*(a+b*x)))*PolyLog(2, (c*(a+b*x))/(a*(c+d*x)))-Log((a*(c+d*x))/(c*(a+b*x)))*PolyLog(2, (d*(a+b*x))/(b*(c+d*x)))+(Log(a+b*x)+Log((a*(c+d*x))/(c*(a+b*x))))*PolyLog(2, 1+(d*x)/c)-PolyLog(3, 1+(b*x)/a)+PolyLog(3, (c*(a+b*x))/(a*(c+d*x)))-PolyLog(3, (d*(a+b*x))/(b*(c+d*x)))-PolyLog(3, 1+(d*x)/c)");

	}

	// {Log(1+b/x)/x, x, 1, PolyLog(2, -(b/x))}
	public void test01948() {
		check("Integrate(Log(1+b/x)/x, x)", "PolyLog(2, -(b/x))");

	}

	// {Log(c*(a+b*x^2)^p)^3/x^2, x, 1, -(Log(c*(a+b*x^2)^p)^3/x)+6*b*p*Unintegrable(Log(c*(a+b*x^2)^p)^2/(a +
	// b*x^2), x)}
	public void test01949() {
		check("Integrate(Log(c*(a+b*x^2)^p)^3/x^2, x)",
				"-(Log(c*(a+b*x^2)^p)^3/x)+6*b*p*Unintegrable(Log(c*(a+b*x^2)^p)^2/(a+b*x^2), x)");

	}

	// {(f*x)^m*Log(c*(d+e*x^2)^p)^3, x, 1, ((f*x)^(1+m)*Log(c*(d+e*x^2)^p)^3)/(f*(1+m)) -
	// (6*e*p*Unintegrable(((f*x)^(2+m)*Log(c*(d+e*x^2)^p)^2)/(d+e*x^2), x))/(f^2*(1+m))}
	public void test01950() {
		check("Integrate((f*x)^m*Log(c*(d+e*x^2)^p)^3, x)",
				"((f*x)^(1+m)*Log(c*(d+e*x^2)^p)^3)/(f*(1+m))-(6*e*p*Unintegrable(((f*x)^(2+m)*Log(c*(d+e*x^2)^p)^2)/(d+e*x^2), x))/(f^2*(1+m))");

	}

	// {(f*x)^m*Log(c*(d+e*x^2)^p)^2, x, 1, ((f*x)^(1+m)*Log(c*(d+e*x^2)^p)^2)/(f*(1+m)) -
	// (4*e*p*Unintegrable(((f*x)^(2+m)*Log(c*(d+e*x^2)^p))/(d+e*x^2), x))/(f^2*(1+m))}
	public void test01951() {
		check("Integrate((f*x)^m*Log(c*(d+e*x^2)^p)^2, x)",
				"((f*x)^(1+m)*Log(c*(d+e*x^2)^p)^2)/(f*(1+m))-(4*e*p*Unintegrable(((f*x)^(2+m)*Log(c*(d+e*x^2)^p))/(d+e*x^2), x))/(f^2*(1+m))");

	}

	// {Log(1+e*x^n)/x, x, 1, -(PolyLog(2, -(e*x^n))/n)}
	public void test01952() {
		check("Integrate(Log(1+e*x^n)/x, x)", "-(PolyLog(2, -(e*x^n))/n)");

	}

	// {Log((a+b*x^n)/x^n)/(c+d*x), x, 1, Unintegrable(Log(b+a/x^n)/(c+d*x), x)}
	public void test01953() {
		check("Integrate(Log((a+b*x^n)/x^n)/(c+d*x), x)", "Unintegrable(Log(b+a/x^n)/(c+d*x), x)");

	}

	// {(a+b*Log(c*(d+e*Sqrt(x))))^p/x, x, 1, Unintegrable((a+b*Log(c*(d+e*Sqrt(x))))^p/x, x)}
	public void test01954() {
		check("Integrate((a+b*Log(c*(d+e*Sqrt(x))))^p/x, x)", "Unintegrable((a+b*Log(c*(d+e*Sqrt(x))))^p/x, x)");

	}

	// {(a+b*Log(c*(d+e*Sqrt(x))))^p/x^2, x, 1, Unintegrable((a+b*Log(c*(d+e*Sqrt(x))))^p/x^2, x)}
	public void test01955() {
		check("Integrate((a+b*Log(c*(d+e*Sqrt(x))))^p/x^2, x)", "Unintegrable((a+b*Log(c*(d+e*Sqrt(x))))^p/x^2, x)");

	}

	// {(a+b*Log(c*(d+e*Sqrt(x))^2))^p/x, x, 1, Unintegrable((a+b*Log(c*(d+e*Sqrt(x))^2))^p/x, x)}
	public void test01956() {
		check("Integrate((a+b*Log(c*(d+e*Sqrt(x))^2))^p/x, x)", "Unintegrable((a+b*Log(c*(d+e*Sqrt(x))^2))^p/x, x)");

	}

	// {(a+b*Log(c*(d+e*Sqrt(x))^2))^p/x^2, x, 1, Unintegrable((a+b*Log(c*(d+e*Sqrt(x))^2))^p/x^2, x)}
	public void test01957() {
		check("Integrate((a+b*Log(c*(d+e*Sqrt(x))^2))^p/x^2, x)",
				"Unintegrable((a+b*Log(c*(d+e*Sqrt(x))^2))^p/x^2, x)");

	}

	// {x*(a+b*Log(c*(d+e/Sqrt(x))))^p, x, 1, Unintegrable(x*(a+b*Log(c*(d+e/Sqrt(x))))^p, x)}
	public void test01958() {
		check("Integrate(x*(a+b*Log(c*(d+e/Sqrt(x))))^p, x)", "Unintegrable(x*(a+b*Log(c*(d+e/Sqrt(x))))^p, x)");

	}

	// {(a+b*Log(c*(d+e/Sqrt(x))))^p, x, 1, Unintegrable((a+b*Log(c*(d+e/Sqrt(x))))^p, x)}
	public void test01959() {
		check("Integrate((a+b*Log(c*(d+e/Sqrt(x))))^p, x)", "Unintegrable((a+b*Log(c*(d+e/Sqrt(x))))^p, x)");

	}

	// {(a+b*Log(c*(d+e/Sqrt(x))))^p/x, x, 1, Unintegrable((a+b*Log(c*(d+e/Sqrt(x))))^p/x, x)}
	public void test01960() {
		check("Integrate((a+b*Log(c*(d+e/Sqrt(x))))^p/x, x)", "Unintegrable((a+b*Log(c*(d+e/Sqrt(x))))^p/x, x)");

	}

	// {x*(a+b*Log(c*(d+e/Sqrt(x))^2))^p, x, 1, Unintegrable(x*(a+b*Log(c*(d+e/Sqrt(x))^2))^p, x)}
	public void test01961() {
		check("Integrate(x*(a+b*Log(c*(d+e/Sqrt(x))^2))^p, x)", "Unintegrable(x*(a+b*Log(c*(d+e/Sqrt(x))^2))^p, x)");

	}

	// {(a+b*Log(c*(d+e/Sqrt(x))^2))^p, x, 1, Unintegrable((a+b*Log(c*(d+e/Sqrt(x))^2))^p, x)}
	public void test01962() {
		check("Integrate((a+b*Log(c*(d+e/Sqrt(x))^2))^p, x)", "Unintegrable((a+b*Log(c*(d+e/Sqrt(x))^2))^p, x)");

	}

	// {(a+b*Log(c*(d+e/Sqrt(x))^2))^p/x, x, 1, Unintegrable((a+b*Log(c*(d+e/Sqrt(x))^2))^p/x, x)}
	public void test01963() {
		check("Integrate((a+b*Log(c*(d+e/Sqrt(x))^2))^p/x, x)", "Unintegrable((a+b*Log(c*(d+e/Sqrt(x))^2))^p/x, x)");

	}

	// {(a+b*Log(c*(d+e*x^(1/3))))^p/x, x, 1, Unintegrable((a+b*Log(c*(d+e*x^(1/3))))^p/x, x)}
	public void test01964() {
		check("Integrate((a+b*Log(c*(d+e*x^(1/3))))^p/x, x)", "Unintegrable((a+b*Log(c*(d+e*x^(1/3))))^p/x, x)");

	}

	// {(a+b*Log(c*(d+e*x^(1/3))))^p/x^2, x, 1, Unintegrable((a+b*Log(c*(d+e*x^(1/3))))^p/x^2, x)}
	public void test01965() {
		check("Integrate((a+b*Log(c*(d+e*x^(1/3))))^p/x^2, x)", "Unintegrable((a+b*Log(c*(d+e*x^(1/3))))^p/x^2, x)");

	}

	// {(a+b*Log(c*(d+e*x^(1/3))^2))^p/x, x, 1, Unintegrable((a+b*Log(c*(d+e*x^(1/3))^2))^p/x, x)}
	public void test01966() {
		check("Integrate((a+b*Log(c*(d+e*x^(1/3))^2))^p/x, x)", "Unintegrable((a+b*Log(c*(d+e*x^(1/3))^2))^p/x, x)");

	}

	// {(a+b*Log(c*(d+e*x^(1/3))^2))^p/x^2, x, 1, Unintegrable((a+b*Log(c*(d+e*x^(1/3))^2))^p/x^2, x)}
	public void test01967() {
		check("Integrate((a+b*Log(c*(d+e*x^(1/3))^2))^p/x^2, x)",
				"Unintegrable((a+b*Log(c*(d+e*x^(1/3))^2))^p/x^2, x)");

	}

	// {(a+b*Log(c*(d+e*x^(2/3))))^p/x, x, 1, Unintegrable((a+b*Log(c*(d+e*x^(2/3))))^p/x, x)}
	public void test01968() {
		check("Integrate((a+b*Log(c*(d+e*x^(2/3))))^p/x, x)", "Unintegrable((a+b*Log(c*(d+e*x^(2/3))))^p/x, x)");

	}

	// {(a+b*Log(c*(d+e*x^(2/3))))^p/x^3, x, 1, Unintegrable((a+b*Log(c*(d+e*x^(2/3))))^p/x^3, x)}
	public void test01969() {
		check("Integrate((a+b*Log(c*(d+e*x^(2/3))))^p/x^3, x)", "Unintegrable((a+b*Log(c*(d+e*x^(2/3))))^p/x^3, x)");

	}

	// {x^2*(a+b*Log(c*(d+e*x^(2/3))))^p, x, 1, Unintegrable(x^2*(a+b*Log(c*(d+e*x^(2/3))))^p, x)}
	public void test01970() {
		check("Integrate(x^2*(a+b*Log(c*(d+e*x^(2/3))))^p, x)", "Unintegrable(x^2*(a+b*Log(c*(d+e*x^(2/3))))^p, x)");

	}

	// {(a+b*Log(c*(d+e*x^(2/3))))^p, x, 1, Unintegrable((a+b*Log(c*(d+e*x^(2/3))))^p, x)}
	public void test01971() {
		check("Integrate((a+b*Log(c*(d+e*x^(2/3))))^p, x)", "Unintegrable((a+b*Log(c*(d+e*x^(2/3))))^p, x)");

	}

	// {(a+b*Log(c*(d+e*x^(2/3))))^p/x^2, x, 1, Unintegrable((a+b*Log(c*(d+e*x^(2/3))))^p/x^2, x)}
	public void test01972() {
		check("Integrate((a+b*Log(c*(d+e*x^(2/3))))^p/x^2, x)", "Unintegrable((a+b*Log(c*(d+e*x^(2/3))))^p/x^2, x)");

	}

	// {(a+b*Log(c*(d+e*x^(2/3))^2))^p/x, x, 1, Unintegrable((a+b*Log(c*(d+e*x^(2/3))^2))^p/x, x)}
	public void test01973() {
		check("Integrate((a+b*Log(c*(d+e*x^(2/3))^2))^p/x, x)", "Unintegrable((a+b*Log(c*(d+e*x^(2/3))^2))^p/x, x)");

	}

	// {(a+b*Log(c*(d+e*x^(2/3))^2))^p/x^3, x, 1, Unintegrable((a+b*Log(c*(d+e*x^(2/3))^2))^p/x^3, x)}
	public void test01974() {
		check("Integrate((a+b*Log(c*(d+e*x^(2/3))^2))^p/x^3, x)",
				"Unintegrable((a+b*Log(c*(d+e*x^(2/3))^2))^p/x^3, x)");

	}

	// {x^2*(a+b*Log(c*(d+e*x^(2/3))^2))^p, x, 1, Unintegrable(x^2*(a+b*Log(c*(d+e*x^(2/3))^2))^p, x)}
	public void test01975() {
		check("Integrate(x^2*(a+b*Log(c*(d+e*x^(2/3))^2))^p, x)",
				"Unintegrable(x^2*(a+b*Log(c*(d+e*x^(2/3))^2))^p, x)");

	}

	// {(a+b*Log(c*(d+e*x^(2/3))^2))^p, x, 1, Unintegrable((a+b*Log(c*(d+e*x^(2/3))^2))^p, x)}
	public void test01976() {
		check("Integrate((a+b*Log(c*(d+e*x^(2/3))^2))^p, x)", "Unintegrable((a+b*Log(c*(d+e*x^(2/3))^2))^p, x)");

	}

	// {(a+b*Log(c*(d+e*x^(2/3))^2))^p/x^2, x, 1, Unintegrable((a+b*Log(c*(d+e*x^(2/3))^2))^p/x^2, x)}
	public void test01977() {
		check("Integrate((a+b*Log(c*(d+e*x^(2/3))^2))^p/x^2, x)",
				"Unintegrable((a+b*Log(c*(d+e*x^(2/3))^2))^p/x^2, x)");

	}

	// {x*(a+b*Log(c*(d+e/x^(1/3))))^p, x, 1, Unintegrable(x*(a+b*Log(c*(d+e/x^(1/3))))^p, x)}
	public void test01978() {
		check("Integrate(x*(a+b*Log(c*(d+e/x^(1/3))))^p, x)", "Unintegrable(x*(a+b*Log(c*(d+e/x^(1/3))))^p, x)");

	}

	// {(a+b*Log(c*(d+e/x^(1/3))))^p, x, 1, Unintegrable((a+b*Log(c*(d+e/x^(1/3))))^p, x)}
	public void test01979() {
		check("Integrate((a+b*Log(c*(d+e/x^(1/3))))^p, x)", "Unintegrable((a+b*Log(c*(d+e/x^(1/3))))^p, x)");

	}

	// {(a+b*Log(c*(d+e/x^(1/3))))^p/x, x, 1, Unintegrable((a+b*Log(c*(d+e/x^(1/3))))^p/x, x)}
	public void test01980() {
		check("Integrate((a+b*Log(c*(d+e/x^(1/3))))^p/x, x)", "Unintegrable((a+b*Log(c*(d+e/x^(1/3))))^p/x, x)");

	}

	// {x*(a+b*Log(c*(d+e/x^(1/3))^2))^p, x, 1, Unintegrable(x*(a+b*Log(c*(d+e/x^(1/3))^2))^p, x)}
	public void test01981() {
		check("Integrate(x*(a+b*Log(c*(d+e/x^(1/3))^2))^p, x)", "Unintegrable(x*(a+b*Log(c*(d+e/x^(1/3))^2))^p, x)");

	}

	// {(a+b*Log(c*(d+e/x^(1/3))^2))^p, x, 1, Unintegrable((a+b*Log(c*(d+e/x^(1/3))^2))^p, x)}
	public void test01982() {
		check("Integrate((a+b*Log(c*(d+e/x^(1/3))^2))^p, x)", "Unintegrable((a+b*Log(c*(d+e/x^(1/3))^2))^p, x)");

	}

	// {(a+b*Log(c*(d+e/x^(1/3))^2))^p/x, x, 1, Unintegrable((a+b*Log(c*(d+e/x^(1/3))^2))^p/x, x)}
	public void test01983() {
		check("Integrate((a+b*Log(c*(d+e/x^(1/3))^2))^p/x, x)", "Unintegrable((a+b*Log(c*(d+e/x^(1/3))^2))^p/x, x)");

	}

	// {x^3*(a+b*Log(c*(d+e/x^(2/3))))^p, x, 1, Unintegrable(x^3*(a+b*Log(c*(d+e/x^(2/3))))^p, x)}
	public void test01984() {
		check("Integrate(x^3*(a+b*Log(c*(d+e/x^(2/3))))^p, x)", "Unintegrable(x^3*(a+b*Log(c*(d+e/x^(2/3))))^p, x)");

	}

	// {x^2*(a+b*Log(c*(d+e/x^(2/3))))^p, x, 1, Unintegrable(x^2*(a+b*Log(c*(d+e/x^(2/3))))^p, x)}
	public void test01985() {
		check("Integrate(x^2*(a+b*Log(c*(d+e/x^(2/3))))^p, x)", "Unintegrable(x^2*(a+b*Log(c*(d+e/x^(2/3))))^p, x)");

	}

	// {x*(a+b*Log(c*(d+e/x^(2/3))))^p, x, 1, Unintegrable(x*(a+b*Log(c*(d+e/x^(2/3))))^p, x)}
	public void test01986() {
		check("Integrate(x*(a+b*Log(c*(d+e/x^(2/3))))^p, x)", "Unintegrable(x*(a+b*Log(c*(d+e/x^(2/3))))^p, x)");

	}

	// {(a+b*Log(c*(d+e/x^(2/3))))^p, x, 1, Unintegrable((a+b*Log(c*(d+e/x^(2/3))))^p, x)}
	public void test01987() {
		check("Integrate((a+b*Log(c*(d+e/x^(2/3))))^p, x)", "Unintegrable((a+b*Log(c*(d+e/x^(2/3))))^p, x)");

	}

	// {(a+b*Log(c*(d+e/x^(2/3))))^p/x, x, 1, Unintegrable((a+b*Log(c*(d+e/x^(2/3))))^p/x, x)}
	public void test01988() {
		check("Integrate((a+b*Log(c*(d+e/x^(2/3))))^p/x, x)", "Unintegrable((a+b*Log(c*(d+e/x^(2/3))))^p/x, x)");

	}

	// {(a+b*Log(c*(d+e/x^(2/3))))^p/x^2, x, 1, Unintegrable((a+b*Log(c*(d+e/x^(2/3))))^p/x^2, x)}
	public void test01989() {
		check("Integrate((a+b*Log(c*(d+e/x^(2/3))))^p/x^2, x)", "Unintegrable((a+b*Log(c*(d+e/x^(2/3))))^p/x^2, x)");

	}

	// {x^3*(a+b*Log(c*(d+e/x^(2/3))^2))^p, x, 1, Unintegrable(x^3*(a+b*Log(c*(d+e/x^(2/3))^2))^p, x)}
	public void test01990() {
		check("Integrate(x^3*(a+b*Log(c*(d+e/x^(2/3))^2))^p, x)",
				"Unintegrable(x^3*(a+b*Log(c*(d+e/x^(2/3))^2))^p, x)");

	}

	// {x^2*(a+b*Log(c*(d+e/x^(2/3))^2))^p, x, 1, Unintegrable(x^2*(a+b*Log(c*(d+e/x^(2/3))^2))^p, x)}
	public void test01991() {
		check("Integrate(x^2*(a+b*Log(c*(d+e/x^(2/3))^2))^p, x)",
				"Unintegrable(x^2*(a+b*Log(c*(d+e/x^(2/3))^2))^p, x)");

	}

	// {x*(a+b*Log(c*(d+e/x^(2/3))^2))^p, x, 1, Unintegrable(x*(a+b*Log(c*(d+e/x^(2/3))^2))^p, x)}
	public void test01992() {
		check("Integrate(x*(a+b*Log(c*(d+e/x^(2/3))^2))^p, x)", "Unintegrable(x*(a+b*Log(c*(d+e/x^(2/3))^2))^p, x)");

	}

	// {(a+b*Log(c*(d+e/x^(2/3))^2))^p, x, 1, Unintegrable((a+b*Log(c*(d+e/x^(2/3))^2))^p, x)}
	public void test01993() {
		check("Integrate((a+b*Log(c*(d+e/x^(2/3))^2))^p, x)", "Unintegrable((a+b*Log(c*(d+e/x^(2/3))^2))^p, x)");

	}

	// {(a+b*Log(c*(d+e/x^(2/3))^2))^p/x, x, 1, Unintegrable((a+b*Log(c*(d+e/x^(2/3))^2))^p/x, x)}
	public void test01994() {
		check("Integrate((a+b*Log(c*(d+e/x^(2/3))^2))^p/x, x)", "Unintegrable((a+b*Log(c*(d+e/x^(2/3))^2))^p/x, x)");

	}

	// {(a+b*Log(c*(d+e/x^(2/3))^2))^p/x^2, x, 1, Unintegrable((a+b*Log(c*(d+e/x^(2/3))^2))^p/x^2, x)}
	public void test01995() {
		check("Integrate((a+b*Log(c*(d+e/x^(2/3))^2))^p/x^2, x)",
				"Unintegrable((a+b*Log(c*(d+e/x^(2/3))^2))^p/x^2, x)");

	}

	// {(a+b*Log(c*(d+e*x^m)^n))/(x*Log(f*x^p)^2), x, 1, -((a+b*Log(c*(d+e*x^m)^n))/(p*Log(f*x^p))) +
	// (b*e*m*n*Unintegrable(x^(-1+m)/((d+e*x^m)*Log(f*x^p)), x))/p}
	public void test01996() {
		check("Integrate((a+b*Log(c*(d+e*x^m)^n))/(x*Log(f*x^p)^2), x)",
				"-((a+b*Log(c*(d+e*x^m)^n))/(p*Log(f*x^p)))+(b*e*m*n*Unintegrable(x^(-1+m)/((d+e*x^m)*Log(f*x^p)), x))/p");

	}

	// {(a+b*Log(c*(d+e*x^m)^n))/(x*Log(f*x^p)^3), x, 1, -(a+b*Log(c*(d+e*x^m)^n))/(2*p*Log(f*x^p)^2) +
	// (b*e*m*n*Unintegrable(x^(-1+m)/((d+e*x^m)*Log(f*x^p)^2), x))/(2*p)}
	public void test01997() {
		check("Integrate((a+b*Log(c*(d+e*x^m)^n))/(x*Log(f*x^p)^3), x)",
				"-(a+b*Log(c*(d+e*x^m)^n))/(2*p*Log(f*x^p)^2)+(b*e*m*n*Unintegrable(x^(-1+m)/((d+e*x^m)*Log(f*x^p)^2), x))/(2*p)");

	}

	// {Log(1+(a+b*x)^(-1))/(a+b*x), x, 1, PolyLog(2, -(a+b*x)^(-1))/b}
	public void test01998() {
		check("Integrate(Log(1+(a+b*x)^(-1))/(a+b*x), x)", "PolyLog(2, -(a+b*x)^(-1))/b");

	}

	// {Log(1-(a+b*x)^(-1))/(a+b*x), x, 1, PolyLog(2, (a+b*x)^(-1))/b}
	public void test01999() {
		check("Integrate(Log(1-(a+b*x)^(-1))/(a+b*x), x)", "PolyLog(2, (a+b*x)^(-1))/b");

	}

	// {Log(e*((f*(a+b*x))/(c+d*x))^r)/((a+b*x)*(c+d*x)), x, 1, Log(e*((f*(a+b*x))/(c+d*x))^r)^2/(2*(b*c -
	// a*d)*r)}
	public void test02000() {
		check("Integrate(Log(e*((f*(a+b*x))/(c+d*x))^r)/((a+b*x)*(c+d*x)), x)",
				"Log(e*((f*(a+b*x))/(c+d*x))^r)^2/(2*(b*c-a*d)*r)");

	}

	// {Log(e*((a+b*x)/(c+d*x))^n)^p/((a+b*x)*(c+d*x)), x, 1, Log(e*((a+b*x)/(c+d*x))^n)^(1+p)/((b*c -
	// a*d)*n*(1+p))}
	public void test02001() {
		check("Integrate(Log(e*((a+b*x)/(c+d*x))^n)^p/((a+b*x)*(c+d*x)), x)",
				"Log(e*((a+b*x)/(c+d*x))^n)^(1+p)/((b*c-a*d)*n*(1+p))");

	}

	// {Log(e*((a+b*x)/(c+d*x))^n)^p/(a*c+(b*c+a*d)*x+b*d*x^2), x, 1, Log(e*((a+b*x)/(c+d*x))^n)^(1 +
	// p)/((b*c-a*d)*n*(1+p))}
	public void test02002() {
		check("Integrate(Log(e*((a+b*x)/(c+d*x))^n)^p/(a*c+(b*c+a*d)*x+b*d*x^2), x)",
				"Log(e*((a+b*x)/(c+d*x))^n)^(1+p)/((b*c-a*d)*n*(1+p))");

	}

	// {Log(e*((a+b*x)^n1/(c+d*x)^n1)^n)^3/((a+b*x)*(c+d*x)), x, 1, Log(e*((a+b*x)^n1/(c +
	// d*x)^n1)^n)^4/(4*(b*c-a*d)*n*n1)}
	public void test02003() {
		check("Integrate(Log(e*((a+b*x)^n1/(c+d*x)^n1)^n)^3/((a+b*x)*(c+d*x)), x)",
				"Log(e*((a+b*x)^n1/(c+d*x)^n1)^n)^4/(4*(b*c-a*d)*n*n1)");

	}

	// {Log(e*((a+b*x)^n1/(c+d*x)^n1)^n)^2/((a+b*x)*(c+d*x)), x, 1, Log(e*((a+b*x)^n1/(c +
	// d*x)^n1)^n)^3/(3*(b*c-a*d)*n*n1)}
	public void test02004() {
		check("Integrate(Log(e*((a+b*x)^n1/(c+d*x)^n1)^n)^2/((a+b*x)*(c+d*x)), x)",
				"Log(e*((a+b*x)^n1/(c+d*x)^n1)^n)^3/(3*(b*c-a*d)*n*n1)");

	}

	// {Log(e*((a+b*x)^n1/(c+d*x)^n1)^n)/((a+b*x)*(c+d*x)), x, 1, Log(e*((a+b*x)^n1/(c+d*x)^n1)^n)^2/(2*(b*c
	// -a*d)*n*n1)}
	public void test02005() {
		check("Integrate(Log(e*((a+b*x)^n1/(c+d*x)^n1)^n)/((a+b*x)*(c+d*x)), x)",
				"Log(e*((a+b*x)^n1/(c+d*x)^n1)^n)^2/(2*(b*c-a*d)*n*n1)");

	}

	// {1/((a+b*x)*(c+d*x)*Log(e*((a+b*x)^n1/(c+d*x)^n1)^n)), x, 1, Log(Log(e*((a+b*x)^n1/(c +
	// d*x)^n1)^n))/((b*c-a*d)*n*n1)}
	public void test02006() {
		check("Integrate(1/((a+b*x)*(c+d*x)*Log(e*((a+b*x)^n1/(c+d*x)^n1)^n)), x)",
				"Log(Log(e*((a+b*x)^n1/(c+d*x)^n1)^n))/((b*c-a*d)*n*n1)");

	}

	// {1/((a+b*x)*(c+d*x)*Log(e*((a+b*x)^n1/(c+d*x)^n1)^n)^2), x, 1, -(1/((b*c-a*d)*n*n1*Log(e*((a +
	// b*x)^n1/(c+d*x)^n1)^n)))}
	public void test02007() {
		check("Integrate(1/((a+b*x)*(c+d*x)*Log(e*((a+b*x)^n1/(c+d*x)^n1)^n)^2), x)",
				"-(1/((b*c-a*d)*n*n1*Log(e*((a+b*x)^n1/(c+d*x)^n1)^n)))");

	}

	// {1/((a+b*x)*(c+d*x)*Log(e*((a+b*x)^n1/(c+d*x)^n1)^n)^3), x, 1, -1/(2*(b*c-a*d)*n*n1*Log(e*((a +
	// b*x)^n1/(c+d*x)^n1)^n)^2)}
	public void test02008() {
		check("Integrate(1/((a+b*x)*(c+d*x)*Log(e*((a+b*x)^n1/(c+d*x)^n1)^n)^3), x)",
				"-1/(2*(b*c-a*d)*n*n1*Log(e*((a+b*x)^n1/(c+d*x)^n1)^n)^2)");

	}

	// {Log(e*((a+b*x)^n1/(c+d*x)^n1)^n)^p/((a+b*x)*(c+d*x)), x, 1, Log(e*((a+b*x)^n1/(c+d*x)^n1)^n)^(1 +
	// p)/((b*c-a*d)*n*n1*(1+p))}
	public void test02009() {
		check("Integrate(Log(e*((a+b*x)^n1/(c+d*x)^n1)^n)^p/((a+b*x)*(c+d*x)), x)",
				"Log(e*((a+b*x)^n1/(c+d*x)^n1)^n)^(1+p)/((b*c-a*d)*n*n1*(1+p))");

	}

	// {Log(e*((a+b*x)^n1/(c+d*x)^n1)^n)^p/((a*f+b*f*x)*(c*g+d*g*x)), x, 1, Log(e*((a+b*x)^n1/(c +
	// d*x)^n1)^n)^(1+p)/((b*c-a*d)*f*g*n*n1*(1+p))}
	public void test02010() {
		check("Integrate(Log(e*((a+b*x)^n1/(c+d*x)^n1)^n)^p/((a*f+b*f*x)*(c*g+d*g*x)), x)",
				"Log(e*((a+b*x)^n1/(c+d*x)^n1)^n)^(1+p)/((b*c-a*d)*f*g*n*n1*(1+p))");

	}

	// {Log(e*((a+b*x)^n1/(c+d*x)^n1)^n)^p/(a*c*f+(b*c+a*d)*f*x+b*d*f*x^2), x, 1, Log(e*((a+b*x)^n1/(c +
	// d*x)^n1)^n)^(1+p)/((b*c-a*d)*f*n*n1*(1+p))}
	public void test02011() {
		check("Integrate(Log(e*((a+b*x)^n1/(c+d*x)^n1)^n)^p/(a*c*f+(b*c+a*d)*f*x+b*d*f*x^2), x)",
				"Log(e*((a+b*x)^n1/(c+d*x)^n1)^n)^(1+p)/((b*c-a*d)*f*n*n1*(1+p))");

	}

	// {1/((a+b*x)*(c+d*x)*Log(e*((a+b*x)^n1/(c+d*x)^n1)^n)), x, 1, Log(Log(e*((a+b*x)^n1/(c +
	// d*x)^n1)^n))/((b*c-a*d)*n*n1)}
	public void test02012() {
		check("Integrate(1/((a+b*x)*(c+d*x)*Log(e*((a+b*x)^n1/(c+d*x)^n1)^n)), x)",
				"Log(Log(e*((a+b*x)^n1/(c+d*x)^n1)^n))/((b*c-a*d)*n*n1)");

	}

	// {1/((a*f+b*f*x)*(c*g+d*g*x)*Log(e*((a+b*x)^n1/(c+d*x)^n1)^n)), x, 1, Log(Log(e*((a+b*x)^n1/(c +
	// d*x)^n1)^n))/((b*c-a*d)*f*g*n*n1)}
	public void test02013() {
		check("Integrate(1/((a*f+b*f*x)*(c*g+d*g*x)*Log(e*((a+b*x)^n1/(c+d*x)^n1)^n)), x)",
				"Log(Log(e*((a+b*x)^n1/(c+d*x)^n1)^n))/((b*c-a*d)*f*g*n*n1)");

	}

	// {1/((a*c*f+(b*c+a*d)*f*x+b*d*f*x^2)*Log(e*((a+b*x)^n1/(c+d*x)^n1)^n)), x, 1, Log(Log(e*((a+b*x)^n1/(c
	// +d*x)^n1)^n))/((b*c-a*d)*f*n*n1)}
	public void test02014() {
		check("Integrate(1/((a*c*f+(b*c+a*d)*f*x+b*d*f*x^2)*Log(e*((a+b*x)^n1/(c+d*x)^n1)^n)), x)",
				"Log(Log(e*((a+b*x)^n1/(c+d*x)^n1)^n))/((b*c-a*d)*f*n*n1)");

	}

	// {Log(h*(f+g*x)^m)/((a+b*x)*(c+d*x)*Log(e*((a+b*x)/(c+d*x))^n)^2), x, 1, -(Log(h*(f+g*x)^m)/((b*c -
	// a*d)*n*Log(e*((a+b*x)/(c+d*x))^n)))+(g*m*Unintegrable(1/((f+g*x)*Log(e*((a+b*x)/(c+d*x))^n)),
	// x))/((b*c-a*d)*n)}
	public void test02015() {
		check("Integrate(Log(h*(f+g*x)^m)/((a+b*x)*(c+d*x)*Log(e*((a+b*x)/(c+d*x))^n)^2), x)",
				"-(Log(h*(f+g*x)^m)/((b*c-a*d)*n*Log(e*((a+b*x)/(c+d*x))^n)))+(g*m*Unintegrable(1/((f+g*x)*Log(e*((a+b*x)/(c+d*x))^n)), x))/((b*c-a*d)*n)");

	}

	// {((a+b*x)^m*(c+d*x)^(-2-m))/Log(e*((f*(a+b*x)^p)/(c+d*x)^p)^r), x, 1, ((a+b*x)^(1+m)*(c+d*x)^(-1
	// -m)*ExpIntegralEi(((1+m)*Log(e*((f*(a+b*x)^p)/(c+d*x)^p)^r))/(p*r)))/((b*c-a*d)*p*r*(e*((f*(a +
	// b*x)^p)/(c+d*x)^p)^r)^((1+m)/(p*r)))}
	public void test02016() {
		check("Integrate(((a+b*x)^m*(c+d*x)^(-2-m))/Log(e*((f*(a+b*x)^p)/(c+d*x)^p)^r), x)",
				"((a+b*x)^(1+m)*(c+d*x)^(-1-m)*ExpIntegralEi(((1+m)*Log(e*((f*(a+b*x)^p)/(c+d*x)^p)^r))/(p*r)))/((b*c-a*d)*p*r*(e*((f*(a+b*x)^p)/(c+d*x)^p)^r)^((1+m)/(p*r)))");

	}

	// {1/((a*h+b*h*x)^2*Log(e*((f*(a+b*x)^p)/(c+d*x)^p)^r)), x, 1, ((c+d*x)*(e*((f*(a+b*x)^p)/(c +
	// d*x)^p)^r)^(1/(p*r))*ExpIntegralEi(-(Log(e*((f*(a+b*x)^p)/(c+d*x)^p)^r)/(p*r))))/((b*c-a*d)*h^2*p*r*(a +
	// b*x))}
	public void test02017() {
		check("Integrate(1/((a*h+b*h*x)^2*Log(e*((f*(a+b*x)^p)/(c+d*x)^p)^r)), x)",
				"((c+d*x)*(e*((f*(a+b*x)^p)/(c+d*x)^p)^r)^(1/(p*r))*ExpIntegralEi(-(Log(e*((f*(a+b*x)^p)/(c+d*x)^p)^r)/(p*r))))/((b*c-a*d)*h^2*p*r*(a+b*x))");

	}

	// {(a+b*x)^3/((c+d*x)^5*Log(e*((a+b*x)/(c+d*x))^n)), x, 1, ((a+b*x)^4*ExpIntegralEi((4*Log(e*((a +
	// b*x)/(c+d*x))^n))/n))/((b*c-a*d)*n*(e*((a+b*x)/(c+d*x))^n)^(4/n)*(c+d*x)^4)}
	public void test02018() {
		check("Integrate((a+b*x)^3/((c+d*x)^5*Log(e*((a+b*x)/(c+d*x))^n)), x)",
				"((a+b*x)^4*ExpIntegralEi((4*Log(e*((a+b*x)/(c+d*x))^n))/n))/((b*c-a*d)*n*(e*((a+b*x)/(c+d*x))^n)^(4/n)*(c+d*x)^4)");

	}

	// {(a+b*x)^2/((c+d*x)^4*Log(e*((a+b*x)/(c+d*x))^n)), x, 1, ((a+b*x)^3*ExpIntegralEi((3*Log(e*((a +
	// b*x)/(c+d*x))^n))/n))/((b*c-a*d)*n*(e*((a+b*x)/(c+d*x))^n)^(3/n)*(c+d*x)^3)}
	public void test02019() {
		check("Integrate((a+b*x)^2/((c+d*x)^4*Log(e*((a+b*x)/(c+d*x))^n)), x)",
				"((a+b*x)^3*ExpIntegralEi((3*Log(e*((a+b*x)/(c+d*x))^n))/n))/((b*c-a*d)*n*(e*((a+b*x)/(c+d*x))^n)^(3/n)*(c+d*x)^3)");

	}

	// {(a+b*x)/((c+d*x)^3*Log(e*((a+b*x)/(c+d*x))^n)), x, 1, ((a+b*x)^2*ExpIntegralEi((2*Log(e*((a+b*x)/(c
	// +d*x))^n))/n))/((b*c-a*d)*n*(e*((a+b*x)/(c+d*x))^n)^(2/n)*(c+d*x)^2)}
	public void test02020() {
		check("Integrate((a+b*x)/((c+d*x)^3*Log(e*((a+b*x)/(c+d*x))^n)), x)",
				"((a+b*x)^2*ExpIntegralEi((2*Log(e*((a+b*x)/(c+d*x))^n))/n))/((b*c-a*d)*n*(e*((a+b*x)/(c+d*x))^n)^(2/n)*(c+d*x)^2)");

	}

	// {1/((c+d*x)^2*Log(e*((a+b*x)/(c+d*x))^n)), x, 1, ((a+b*x)*ExpIntegralEi(Log(e*((a+b*x)/(c +
	// d*x))^n)/n))/((b*c-a*d)*n*(e*((a+b*x)/(c+d*x))^n)^n^(-1)*(c+d*x))}
	public void test02021() {
		check("Integrate(1/((c+d*x)^2*Log(e*((a+b*x)/(c+d*x))^n)), x)",
				"((a+b*x)*ExpIntegralEi(Log(e*((a+b*x)/(c+d*x))^n)/n))/((b*c-a*d)*n*(e*((a+b*x)/(c+d*x))^n)^n^(-1)*(c+d*x))");

	}

	// {1/((a+b*x)*(c+d*x)*Log(e*((a+b*x)/(c+d*x))^n)), x, 1, Log(Log(e*((a+b*x)/(c+d*x))^n))/((b*c -
	// a*d)*n)}
	public void test02022() {
		check("Integrate(1/((a+b*x)*(c+d*x)*Log(e*((a+b*x)/(c+d*x))^n)), x)",
				"Log(Log(e*((a+b*x)/(c+d*x))^n))/((b*c-a*d)*n)");

	}

	// {1/((a+b*x)^2*Log(e*((a+b*x)/(c+d*x))^n)), x, 1, ((e*((a+b*x)/(c+d*x))^n)^n^(-1)*(c +
	// d*x)*ExpIntegralEi(-(Log(e*((a+b*x)/(c+d*x))^n)/n)))/((b*c-a*d)*n*(a+b*x))}
	public void test02023() {
		check("Integrate(1/((a+b*x)^2*Log(e*((a+b*x)/(c+d*x))^n)), x)",
				"((e*((a+b*x)/(c+d*x))^n)^n^(-1)*(c+d*x)*ExpIntegralEi(-(Log(e*((a+b*x)/(c+d*x))^n)/n)))/((b*c-a*d)*n*(a+b*x))");

	}

	// {(c+d*x)/((a+b*x)^3*Log(e*((a+b*x)/(c+d*x))^n)), x, 1, ((e*((a+b*x)/(c+d*x))^n)^(2/n)*(c +
	// d*x)^2*ExpIntegralEi((-2*Log(e*((a+b*x)/(c+d*x))^n))/n))/((b*c-a*d)*n*(a+b*x)^2)}
	public void test02024() {
		check("Integrate((c+d*x)/((a+b*x)^3*Log(e*((a+b*x)/(c+d*x))^n)), x)",
				"((e*((a+b*x)/(c+d*x))^n)^(2/n)*(c+d*x)^2*ExpIntegralEi((-2*Log(e*((a+b*x)/(c+d*x))^n))/n))/((b*c-a*d)*n*(a+b*x)^2)");

	}

	// {(c+d*x)^2/((a+b*x)^4*Log(e*((a+b*x)/(c+d*x))^n)), x, 1, ((e*((a+b*x)/(c+d*x))^n)^(3/n)*(c +
	// d*x)^3*ExpIntegralEi((-3*Log(e*((a+b*x)/(c+d*x))^n))/n))/((b*c-a*d)*n*(a+b*x)^3)}
	public void test02025() {
		check("Integrate((c+d*x)^2/((a+b*x)^4*Log(e*((a+b*x)/(c+d*x))^n)), x)",
				"((e*((a+b*x)/(c+d*x))^n)^(3/n)*(c+d*x)^3*ExpIntegralEi((-3*Log(e*((a+b*x)/(c+d*x))^n))/n))/((b*c-a*d)*n*(a+b*x)^3)");

	}

	// {Log((c*x)/(a+b*x))^2/(x*(a+b*x)), x, 1, Log((c*x)/(a+b*x))^3/(3*a)}
	public void test02026() {
		check("Integrate(Log((c*x)/(a+b*x))^2/(x*(a+b*x)), x)", "Log((c*x)/(a+b*x))^3/(3*a)");

	}

	// {PolyLog(2, 1+(b*c-a*d)/(d*(a+b*x)))/((a+b*x)*(c+d*x)), x, 1, -(PolyLog(3, 1+(b*c-a*d)/(d*(a +
	// b*x)))/(b*c-a*d))}
	public void test02027() {
		check("Integrate(PolyLog(2, 1+(b*c-a*d)/(d*(a+b*x)))/((a+b*x)*(c+d*x)), x)",
				"-(PolyLog(3, 1+(b*c-a*d)/(d*(a+b*x)))/(b*c-a*d))");

	}

	// {Log((2*x*(d*Sqrt(-(e/d))+e*x))/(d+e*x^2))/(d+e*x^2), x, 1, -(Sqrt(-(e/d))*PolyLog(2, 1 -
	// (2*x*(d*Sqrt(-(e/d))+e*x))/(d+e*x^2)))/(2*e)}
	public void test02028() {
		check("Integrate(Log((2*x*(d*Sqrt(-(e/d))+e*x))/(d+e*x^2))/(d+e*x^2), x)",
				"-(Sqrt(-(e/d))*PolyLog(2, 1-(2*x*(d*Sqrt(-(e/d))+e*x))/(d+e*x^2)))/(2*e)");

	}

	// {Log((-2*x*(d*Sqrt(-(e/d))-e*x))/(d+e*x^2))/(d+e*x^2), x, 1, (Sqrt(-(e/d))*PolyLog(2, 1 +
	// (2*x*(d*Sqrt(-(e/d))-e*x))/(d+e*x^2)))/(2*e)}
	public void test02029() {
		check("Integrate(Log((-2*x*(d*Sqrt(-(e/d))-e*x))/(d+e*x^2))/(d+e*x^2), x)",
				"(Sqrt(-(e/d))*PolyLog(2, 1+(2*x*(d*Sqrt(-(e/d))-e*x))/(d+e*x^2)))/(2*e)");

	}

	// {Log((2*x*((d*Sqrt(e))/Sqrt(-d)+e*x))/(d+e*x^2))/(d+e*x^2), x, 1, -PolyLog(2, 1+(2*Sqrt(e)*x*(Sqrt(-d) -
	// Sqrt(e)*x))/(d+e*x^2))/(2*Sqrt(-d)*Sqrt(e))}
	public void test02030() {
		check("Integrate(Log((2*x*((d*Sqrt(e))/Sqrt(-d)+e*x))/(d+e*x^2))/(d+e*x^2), x)",
				"-PolyLog(2, 1+(2*Sqrt(e)*x*(Sqrt(-d)-Sqrt(e)*x))/(d+e*x^2))/(2*Sqrt(-d)*Sqrt(e))");

	}

	// {Log((-2*x*((d*Sqrt(e))/Sqrt(-d)-e*x))/(d+e*x^2))/(d+e*x^2), x, 1, PolyLog(2, 1-(2*Sqrt(e)*x*(Sqrt(-d) +
	// Sqrt(e)*x))/(d+e*x^2))/(2*Sqrt(-d)*Sqrt(e))}
	public void test02031() {
		check("Integrate(Log((-2*x*((d*Sqrt(e))/Sqrt(-d)-e*x))/(d+e*x^2))/(d+e*x^2), x)",
				"PolyLog(2, 1-(2*Sqrt(e)*x*(Sqrt(-d)+Sqrt(e)*x))/(d+e*x^2))/(2*Sqrt(-d)*Sqrt(e))");

	}

	// {Log((2*x*(Sqrt(d)*Sqrt(-e)+e*x))/(d+e*x^2))/(d+e*x^2), x, 1, PolyLog(2, 1-(2*x*(Sqrt(d)*Sqrt(-e) +
	// e*x))/(d+e*x^2))/(2*Sqrt(d)*Sqrt(-e))}
	public void test02032() {
		check("Integrate(Log((2*x*(Sqrt(d)*Sqrt(-e)+e*x))/(d+e*x^2))/(d+e*x^2), x)",
				"PolyLog(2, 1-(2*x*(Sqrt(d)*Sqrt(-e)+e*x))/(d+e*x^2))/(2*Sqrt(d)*Sqrt(-e))");

	}

	// {Log((-2*x*(Sqrt(d)*Sqrt(-e)-e*x))/(d+e*x^2))/(d+e*x^2), x, 1, -PolyLog(2, 1+(2*x*(Sqrt(d)*Sqrt(-e) -
	// e*x))/(d+e*x^2))/(2*Sqrt(d)*Sqrt(-e))}
	public void test02033() {
		check("Integrate(Log((-2*x*(Sqrt(d)*Sqrt(-e)-e*x))/(d+e*x^2))/(d+e*x^2), x)",
				"-PolyLog(2, 1+(2*x*(Sqrt(d)*Sqrt(-e)-e*x))/(d+e*x^2))/(2*Sqrt(d)*Sqrt(-e))");

	}

	// {(a+b*Log(c*Log(d*x^n)^p))/x, x, 1, -(b*p*Log(x))+(Log(d*x^n)*(a+b*Log(c*Log(d*x^n)^p)))/n}
	public void test02034() {
		check("Integrate((a+b*Log(c*Log(d*x^n)^p))/x, x)", "-(b*p*Log(x))+(Log(d*x^n)*(a+b*Log(c*Log(d*x^n)^p)))/n");

	}

	// {Log(c*Log(d*x)^p)/x, x, 1, -(p*Log(x))+Log(d*x)*Log(c*Log(d*x)^p)}
	public void test02035() {
		check("Integrate(Log(c*Log(d*x)^p)/x, x)", "-(p*Log(x))+Log(d*x)*Log(c*Log(d*x)^p)");

	}

	// {Log(c*Log(d*x^n)^p)/x, x, 1, -(p*Log(x))+(Log(d*x^n)*Log(c*Log(d*x^n)^p))/n}
	public void test02036() {
		check("Integrate(Log(c*Log(d*x^n)^p)/x, x)", "-(p*Log(x))+(Log(d*x^n)*Log(c*Log(d*x^n)^p))/n");

	}

	// {Log(-1+4*x+4*Sqrt((-1+x)*x))/x, x, 1, CannotIntegrate(Log(-1+4*x+4*Sqrt(-x+x^2))/x, x)}
	public void test02037() {
		check("Integrate(Log(-1+4*x+4*Sqrt((-1+x)*x))/x, x)", "CannotIntegrate(Log(-1+4*x+4*Sqrt(-x+x^2))/x, x)");

	}

	// {Log(Sqrt(1-a*x)/Sqrt(1+a*x))/(1-a^2*x^2), x, 1, -Log(Sqrt(1-a*x)/Sqrt(1+a*x))^2/(2*a)}
	public void test02038() {
		check("Integrate(Log(Sqrt(1-a*x)/Sqrt(1+a*x))/(1-a^2*x^2), x)", "-Log(Sqrt(1-a*x)/Sqrt(1+a*x))^2/(2*a)");

	}

	// {Log(x)/Sqrt(x), x, 1, -4*Sqrt(x)+2*Sqrt(x)*Log(x)}
	public void test02039() {
		check("Integrate(Log(x)/Sqrt(x), x)", "-4*Sqrt(x)+2*Sqrt(x)*Log(x)");

	}

	// {x^(1/3)*Log(x), x, 1, (-9*x^(4/3))/16+(3*x^(4/3)*Log(x))/4}
	public void test02040() {
		check("Integrate(x^(1/3)*Log(x), x)", "(-9*x^(4/3))/16+(3*x^(4/3)*Log(x))/4");

	}

	// {(1-Log(x))/x^2, x, 1, Log(x)/x}
	public void test02041() {
		check("Integrate((1-Log(x))/x^2, x)", "Log(x)/x");

	}

	// {Log(x)/(-1+x), x, 1, -PolyLog(2, 1-x)}
	public void test02042() {
		check("Integrate(Log(x)/(-1+x), x)", "-PolyLog(2, 1-x)");

	}

	// {Log(1+(I*Sqrt(1-a*x))/Sqrt(1+a*x))/(1-a^2*x^2), x, 1, PolyLog(2, ((-I)*Sqrt(1-a*x))/Sqrt(1+a*x))/a}
	public void test02043() {
		check("Integrate(Log(1+(I*Sqrt(1-a*x))/Sqrt(1+a*x))/(1-a^2*x^2), x)",
				"PolyLog(2, ((-I)*Sqrt(1-a*x))/Sqrt(1+a*x))/a");

	}

	// {Log(1-(I*Sqrt(1-a*x))/Sqrt(1+a*x))/(1-a^2*x^2), x, 1, PolyLog(2, (I*Sqrt(1-a*x))/Sqrt(1+a*x))/a}
	public void test02044() {
		check("Integrate(Log(1-(I*Sqrt(1-a*x))/Sqrt(1+a*x))/(1-a^2*x^2), x)",
				"PolyLog(2, (I*Sqrt(1-a*x))/Sqrt(1+a*x))/a");

	}

	// {Sin(a+b*x), x, 1, -(Cos(a+b*x)/b)}
	public void test02045() {
		check("Integrate(Sin(a+b*x), x)", "-(Cos(a+b*x)/b)");

	}

	// {Sqrt(Sin(b*x)), x, 1, (-2*EllipticE(Pi/4-(b*x)/2, 2))/b}
	public void test02046() {
		check("Integrate(Sqrt(Sin(b*x)), x)", "(-2*EllipticE(Pi/4-(b*x)/2, 2))/b");

	}

	// {1/Sqrt(Sin(b*x)), x, 1, (-2*EllipticF(Pi/4-(b*x)/2, 2))/b}
	public void test02047() {
		check("Integrate(1/Sqrt(Sin(b*x)), x)", "(-2*EllipticF(Pi/4-(b*x)/2, 2))/b");

	}

	// {Sqrt(Sin(a+b*x)), x, 1, (2*EllipticE((a-Pi/2+b*x)/2, 2))/b}
	public void test02048() {
		check("Integrate(Sqrt(Sin(a+b*x)), x)", "(2*EllipticE((a-Pi/2+b*x)/2, 2))/b");

	}

	// {1/Sqrt(Sin(a+b*x)), x, 1, (2*EllipticF((a-Pi/2+b*x)/2, 2))/b}
	public void test02049() {
		check("Integrate(1/Sqrt(Sin(a+b*x)), x)", "(2*EllipticF((a-Pi/2+b*x)/2, 2))/b");

	}

	// {(c*Sin(a+b*x))^(4/3), x, 1, (3*Cos(a+b*x)*Hypergeometric2F1(1/2, 7/6, 13/6, Sin(a+b*x)^2)*(c*Sin(a +
	// b*x))^(7/3))/(7*b*c*Sqrt(Cos(a+b*x)^2))}
	public void test02050() {
		check("Integrate((c*Sin(a+b*x))^(4/3), x)",
				"(3*Cos(a+b*x)*Hypergeometric2F1(1/2, 7/6, 13/6, Sin(a+b*x)^2)*(c*Sin(a+b*x))^(7/3))/(7*b*c*Sqrt(Cos(a+b*x)^2))");

	}

	// {(c*Sin(a+b*x))^(2/3), x, 1, (3*Cos(a+b*x)*Hypergeometric2F1(1/2, 5/6, 11/6, Sin(a+b*x)^2)*(c*Sin(a +
	// b*x))^(5/3))/(5*b*c*Sqrt(Cos(a+b*x)^2))}
	public void test02051() {
		check("Integrate((c*Sin(a+b*x))^(2/3), x)",
				"(3*Cos(a+b*x)*Hypergeometric2F1(1/2, 5/6, 11/6, Sin(a+b*x)^2)*(c*Sin(a+b*x))^(5/3))/(5*b*c*Sqrt(Cos(a+b*x)^2))");

	}

	// {(c*Sin(a+b*x))^(-4/3), x, 1, (-3*Cos(a+b*x)*Hypergeometric2F1(-1/6, 1/2, 5/6, Sin(a +
	// b*x)^2))/(b*c*Sqrt(Cos(a+b*x)^2)*(c*Sin(a+b*x))^(1/3))}
	public void test02052() {
		check("Integrate((c*Sin(a+b*x))^(-4/3), x)",
				"(-3*Cos(a+b*x)*Hypergeometric2F1(-1/6, 1/2, 5/6, Sin(a+b*x)^2))/(b*c*Sqrt(Cos(a+b*x)^2)*(c*Sin(a+b*x))^(1/3))");

	}

	// {Sin(a+b*x)^n, x, 1, (Cos(a+b*x)*Hypergeometric2F1(1/2, (1+n)/2, (3+n)/2, Sin(a+b*x)^2)*Sin(a+b*x)^(1
	// +n))/(b*(1+n)*Sqrt(Cos(a+b*x)^2))}
	public void test02053() {
		check("Integrate(Sin(a+b*x)^n, x)",
				"(Cos(a+b*x)*Hypergeometric2F1(1/2, (1+n)/2, (3+n)/2, Sin(a+b*x)^2)*Sin(a+b*x)^(1+n))/(b*(1+n)*Sqrt(Cos(a+b*x)^2))");

	}

	// {(c*Sin(a+b*x))^n, x, 1, (Cos(a+b*x)*Hypergeometric2F1(1/2, (1+n)/2, (3+n)/2, Sin(a+b*x)^2)*(c*Sin(a +
	// b*x))^(1+n))/(b*c*(1+n)*Sqrt(Cos(a+b*x)^2))}
	public void test02054() {
		check("Integrate((c*Sin(a+b*x))^n, x)",
				"(Cos(a+b*x)*Hypergeometric2F1(1/2, (1+n)/2, (3+n)/2, Sin(a+b*x)^2)*(c*Sin(a+b*x))^(1+n))/(b*c*(1+n)*Sqrt(Cos(a+b*x)^2))");

	}

	// {Tan(a+b*x), x, 1, -(Log(Cos(a+b*x))/b)}
	public void test02055() {
		check("Integrate(Tan(a+b*x), x)", "-(Log(Cos(a+b*x))/b)");

	}

	// {Cot(a+b*x), x, 1, Log(Sin(a+b*x))/b}
	public void test02056() {
		check("Integrate(Cot(a+b*x), x)", "Log(Sin(a+b*x))/b");

	}

	// {Sqrt(c*Sin(a+b*x))/(d*Cos(a+b*x))^(5/2), x, 1, (2*(c*Sin(a+b*x))^(3/2))/(3*b*c*d*(d*Cos(a+b*x))^(3/2))}
	public void test02057() {
		check("Integrate(Sqrt(c*Sin(a+b*x))/(d*Cos(a+b*x))^(5/2), x)",
				"(2*(c*Sin(a+b*x))^(3/2))/(3*b*c*d*(d*Cos(a+b*x))^(3/2))");

	}

	// {(c*Sin(a+b*x))^(3/2)/(d*Cos(a+b*x))^(7/2), x, 1, (2*(c*Sin(a+b*x))^(5/2))/(5*b*c*d*(d*Cos(a +
	// b*x))^(5/2))}
	public void test02058() {
		check("Integrate((c*Sin(a+b*x))^(3/2)/(d*Cos(a+b*x))^(7/2), x)",
				"(2*(c*Sin(a+b*x))^(5/2))/(5*b*c*d*(d*Cos(a+b*x))^(5/2))");

	}

	// {(c*Sin(a+b*x))^(5/2)/(d*Cos(a+b*x))^(9/2), x, 1, (2*(c*Sin(a+b*x))^(7/2))/(7*b*c*d*(d*Cos(a +
	// b*x))^(7/2))}
	public void test02059() {
		check("Integrate((c*Sin(a+b*x))^(5/2)/(d*Cos(a+b*x))^(9/2), x)",
				"(2*(c*Sin(a+b*x))^(7/2))/(7*b*c*d*(d*Cos(a+b*x))^(7/2))");

	}

	// {Sin(x)^(3/2)/Cos(x)^(7/2), x, 1, (2*Sin(x)^(5/2))/(5*Cos(x)^(5/2))}
	public void test02060() {
		check("Integrate(Sin(x)^(3/2)/Cos(x)^(7/2), x)", "(2*Sin(x)^(5/2))/(5*Cos(x)^(5/2))");

	}

	// {1/((d*Cos(a+b*x))^(3/2)*Sqrt(c*Sin(a+b*x))), x, 1, (2*Sqrt(c*Sin(a+b*x)))/(b*c*d*Sqrt(d*Cos(a+b*x)))}
	public void test02061() {
		check("Integrate(1/((d*Cos(a+b*x))^(3/2)*Sqrt(c*Sin(a+b*x))), x)",
				"(2*Sqrt(c*Sin(a+b*x)))/(b*c*d*Sqrt(d*Cos(a+b*x)))");

	}

	// {Cos(e+f*x)^4*(b*Sin(e+f*x))^(1/3), x, 1, (3*Cos(e+f*x)*Hypergeometric2F1(-3/2, 2/3, 5/3, Sin(e +
	// f*x)^2)*(b*Sin(e+f*x))^(4/3))/(4*b*f*Sqrt(Cos(e+f*x)^2))}
	public void test02062() {
		check("Integrate(Cos(e+f*x)^4*(b*Sin(e+f*x))^(1/3), x)",
				"(3*Cos(e+f*x)*Hypergeometric2F1(-3/2, 2/3, 5/3, Sin(e+f*x)^2)*(b*Sin(e+f*x))^(4/3))/(4*b*f*Sqrt(Cos(e+f*x)^2))");

	}

	// {Cos(e+f*x)^2*(b*Sin(e+f*x))^(1/3), x, 1, (3*Cos(e+f*x)*Hypergeometric2F1(-1/2, 2/3, 5/3, Sin(e +
	// f*x)^2)*(b*Sin(e+f*x))^(4/3))/(4*b*f*Sqrt(Cos(e+f*x)^2))}
	public void test02063() {
		check("Integrate(Cos(e+f*x)^2*(b*Sin(e+f*x))^(1/3), x)",
				"(3*Cos(e+f*x)*Hypergeometric2F1(-1/2, 2/3, 5/3, Sin(e+f*x)^2)*(b*Sin(e+f*x))^(4/3))/(4*b*f*Sqrt(Cos(e+f*x)^2))");

	}

	// {(b*Sin(e+f*x))^(1/3), x, 1, (3*Cos(e+f*x)*Hypergeometric2F1(1/2, 2/3, 5/3, Sin(e+f*x)^2)*(b*Sin(e +
	// f*x))^(4/3))/(4*b*f*Sqrt(Cos(e+f*x)^2))}
	public void test02064() {
		check("Integrate((b*Sin(e+f*x))^(1/3), x)",
				"(3*Cos(e+f*x)*Hypergeometric2F1(1/2, 2/3, 5/3, Sin(e+f*x)^2)*(b*Sin(e+f*x))^(4/3))/(4*b*f*Sqrt(Cos(e+f*x)^2))");

	}

	// {Sec(e+f*x)^2*(b*Sin(e+f*x))^(1/3), x, 1, (3*Sqrt(Cos(e+f*x)^2)*Hypergeometric2F1(2/3, 3/2, 5/3, Sin(e +
	// f*x)^2)*Sec(e+f*x)*(b*Sin(e+f*x))^(4/3))/(4*b*f)}
	public void test02065() {
		check("Integrate(Sec(e+f*x)^2*(b*Sin(e+f*x))^(1/3), x)",
				"(3*Sqrt(Cos(e+f*x)^2)*Hypergeometric2F1(2/3, 3/2, 5/3, Sin(e+f*x)^2)*Sec(e+f*x)*(b*Sin(e+f*x))^(4/3))/(4*b*f)");

	}

	// {Sec(e+f*x)^4*(b*Sin(e+f*x))^(1/3), x, 1, (3*Sqrt(Cos(e+f*x)^2)*Hypergeometric2F1(2/3, 5/2, 5/3, Sin(e +
	// f*x)^2)*Sec(e+f*x)*(b*Sin(e+f*x))^(4/3))/(4*b*f)}
	public void test02066() {
		check("Integrate(Sec(e+f*x)^4*(b*Sin(e+f*x))^(1/3), x)",
				"(3*Sqrt(Cos(e+f*x)^2)*Hypergeometric2F1(2/3, 5/2, 5/3, Sin(e+f*x)^2)*Sec(e+f*x)*(b*Sin(e+f*x))^(4/3))/(4*b*f)");

	}

	// {Cos(e+f*x)^4*(b*Sin(e+f*x))^(5/3), x, 1, (3*Cos(e+f*x)*Hypergeometric2F1(-3/2, 4/3, 7/3, Sin(e +
	// f*x)^2)*(b*Sin(e+f*x))^(8/3))/(8*b*f*Sqrt(Cos(e+f*x)^2))}
	public void test02067() {
		check("Integrate(Cos(e+f*x)^4*(b*Sin(e+f*x))^(5/3), x)",
				"(3*Cos(e+f*x)*Hypergeometric2F1(-3/2, 4/3, 7/3, Sin(e+f*x)^2)*(b*Sin(e+f*x))^(8/3))/(8*b*f*Sqrt(Cos(e+f*x)^2))");

	}

	// {Cos(e+f*x)^2*(b*Sin(e+f*x))^(5/3), x, 1, (3*Cos(e+f*x)*Hypergeometric2F1(-1/2, 4/3, 7/3, Sin(e +
	// f*x)^2)*(b*Sin(e+f*x))^(8/3))/(8*b*f*Sqrt(Cos(e+f*x)^2))}
	public void test02068() {
		check("Integrate(Cos(e+f*x)^2*(b*Sin(e+f*x))^(5/3), x)",
				"(3*Cos(e+f*x)*Hypergeometric2F1(-1/2, 4/3, 7/3, Sin(e+f*x)^2)*(b*Sin(e+f*x))^(8/3))/(8*b*f*Sqrt(Cos(e+f*x)^2))");

	}

	// {(b*Sin(e+f*x))^(5/3), x, 1, (3*Cos(e+f*x)*Hypergeometric2F1(1/2, 4/3, 7/3, Sin(e+f*x)^2)*(b*Sin(e +
	// f*x))^(8/3))/(8*b*f*Sqrt(Cos(e+f*x)^2))}
	public void test02069() {
		check("Integrate((b*Sin(e+f*x))^(5/3), x)",
				"(3*Cos(e+f*x)*Hypergeometric2F1(1/2, 4/3, 7/3, Sin(e+f*x)^2)*(b*Sin(e+f*x))^(8/3))/(8*b*f*Sqrt(Cos(e+f*x)^2))");

	}

	// {Sec(e+f*x)^2*(b*Sin(e+f*x))^(5/3), x, 1, (3*Sqrt(Cos(e+f*x)^2)*Hypergeometric2F1(4/3, 3/2, 7/3, Sin(e +
	// f*x)^2)*Sec(e+f*x)*(b*Sin(e+f*x))^(8/3))/(8*b*f)}
	public void test02070() {
		check("Integrate(Sec(e+f*x)^2*(b*Sin(e+f*x))^(5/3), x)",
				"(3*Sqrt(Cos(e+f*x)^2)*Hypergeometric2F1(4/3, 3/2, 7/3, Sin(e+f*x)^2)*Sec(e+f*x)*(b*Sin(e+f*x))^(8/3))/(8*b*f)");

	}

	// {Sec(e+f*x)^4*(b*Sin(e+f*x))^(5/3), x, 1, (3*Sqrt(Cos(e+f*x)^2)*Hypergeometric2F1(4/3, 5/2, 7/3, Sin(e +
	// f*x)^2)*Sec(e+f*x)*(b*Sin(e+f*x))^(8/3))/(8*b*f)}
	public void test02071() {
		check("Integrate(Sec(e+f*x)^4*(b*Sin(e+f*x))^(5/3), x)",
				"(3*Sqrt(Cos(e+f*x)^2)*Hypergeometric2F1(4/3, 5/2, 7/3, Sin(e+f*x)^2)*Sec(e+f*x)*(b*Sin(e+f*x))^(8/3))/(8*b*f)");

	}

	// {Cos(e+f*x)^4/(b*Sin(e+f*x))^(1/3), x, 1, (3*Cos(e+f*x)*Hypergeometric2F1(-3/2, 1/3, 4/3, Sin(e +
	// f*x)^2)*(b*Sin(e+f*x))^(2/3))/(2*b*f*Sqrt(Cos(e+f*x)^2))}
	public void test02072() {
		check("Integrate(Cos(e+f*x)^4/(b*Sin(e+f*x))^(1/3), x)",
				"(3*Cos(e+f*x)*Hypergeometric2F1(-3/2, 1/3, 4/3, Sin(e+f*x)^2)*(b*Sin(e+f*x))^(2/3))/(2*b*f*Sqrt(Cos(e+f*x)^2))");

	}

	// {Cos(e+f*x)^2/(b*Sin(e+f*x))^(1/3), x, 1, (3*Cos(e+f*x)*Hypergeometric2F1(-1/2, 1/3, 4/3, Sin(e +
	// f*x)^2)*(b*Sin(e+f*x))^(2/3))/(2*b*f*Sqrt(Cos(e+f*x)^2))}
	public void test02073() {
		check("Integrate(Cos(e+f*x)^2/(b*Sin(e+f*x))^(1/3), x)",
				"(3*Cos(e+f*x)*Hypergeometric2F1(-1/2, 1/3, 4/3, Sin(e+f*x)^2)*(b*Sin(e+f*x))^(2/3))/(2*b*f*Sqrt(Cos(e+f*x)^2))");

	}

	// {(b*Sin(e+f*x))^(-1/3), x, 1, (3*Cos(e+f*x)*Hypergeometric2F1(1/3, 1/2, 4/3, Sin(e+f*x)^2)*(b*Sin(e +
	// f*x))^(2/3))/(2*b*f*Sqrt(Cos(e+f*x)^2))}
	public void test02074() {
		check("Integrate((b*Sin(e+f*x))^(-1/3), x)",
				"(3*Cos(e+f*x)*Hypergeometric2F1(1/3, 1/2, 4/3, Sin(e+f*x)^2)*(b*Sin(e+f*x))^(2/3))/(2*b*f*Sqrt(Cos(e+f*x)^2))");

	}

	// {Sec(e+f*x)^2/(b*Sin(e+f*x))^(1/3), x, 1, (3*Sqrt(Cos(e+f*x)^2)*Hypergeometric2F1(1/3, 3/2, 4/3, Sin(e +
	// f*x)^2)*Sec(e+f*x)*(b*Sin(e+f*x))^(2/3))/(2*b*f)}
	public void test02075() {
		check("Integrate(Sec(e+f*x)^2/(b*Sin(e+f*x))^(1/3), x)",
				"(3*Sqrt(Cos(e+f*x)^2)*Hypergeometric2F1(1/3, 3/2, 4/3, Sin(e+f*x)^2)*Sec(e+f*x)*(b*Sin(e+f*x))^(2/3))/(2*b*f)");

	}

	// {Sec(e+f*x)^4/(b*Sin(e+f*x))^(1/3), x, 1, (3*Sqrt(Cos(e+f*x)^2)*Hypergeometric2F1(1/3, 5/2, 4/3, Sin(e +
	// f*x)^2)*Sec(e+f*x)*(b*Sin(e+f*x))^(2/3))/(2*b*f)}
	public void test02076() {
		check("Integrate(Sec(e+f*x)^4/(b*Sin(e+f*x))^(1/3), x)",
				"(3*Sqrt(Cos(e+f*x)^2)*Hypergeometric2F1(1/3, 5/2, 4/3, Sin(e+f*x)^2)*Sec(e+f*x)*(b*Sin(e+f*x))^(2/3))/(2*b*f)");

	}

	// {Cos(e+f*x)^4/(b*Sin(e+f*x))^(5/3), x, 1, (-3*Cos(e+f*x)*Hypergeometric2F1(-3/2, -1/3, 2/3, Sin(e +
	// f*x)^2))/(2*b*f*Sqrt(Cos(e+f*x)^2)*(b*Sin(e+f*x))^(2/3))}
	public void test02077() {
		check("Integrate(Cos(e+f*x)^4/(b*Sin(e+f*x))^(5/3), x)",
				"(-3*Cos(e+f*x)*Hypergeometric2F1(-3/2, -1/3, 2/3, Sin(e+f*x)^2))/(2*b*f*Sqrt(Cos(e+f*x)^2)*(b*Sin(e+f*x))^(2/3))");

	}

	// {Cos(e+f*x)^2/(b*Sin(e+f*x))^(5/3), x, 1, (-3*Cos(e+f*x)*Hypergeometric2F1(-1/2, -1/3, 2/3, Sin(e +
	// f*x)^2))/(2*b*f*Sqrt(Cos(e+f*x)^2)*(b*Sin(e+f*x))^(2/3))}
	public void test02078() {
		check("Integrate(Cos(e+f*x)^2/(b*Sin(e+f*x))^(5/3), x)",
				"(-3*Cos(e+f*x)*Hypergeometric2F1(-1/2, -1/3, 2/3, Sin(e+f*x)^2))/(2*b*f*Sqrt(Cos(e+f*x)^2)*(b*Sin(e+f*x))^(2/3))");

	}

	// {(b*Sin(e+f*x))^(-5/3), x, 1, (-3*Cos(e+f*x)*Hypergeometric2F1(-1/3, 1/2, 2/3, Sin(e +
	// f*x)^2))/(2*b*f*Sqrt(Cos(e+f*x)^2)*(b*Sin(e+f*x))^(2/3))}
	public void test02079() {
		check("Integrate((b*Sin(e+f*x))^(-5/3), x)",
				"(-3*Cos(e+f*x)*Hypergeometric2F1(-1/3, 1/2, 2/3, Sin(e+f*x)^2))/(2*b*f*Sqrt(Cos(e+f*x)^2)*(b*Sin(e+f*x))^(2/3))");

	}

	// {Sec(e+f*x)^2/(b*Sin(e+f*x))^(5/3), x, 1, (-3*Sqrt(Cos(e+f*x)^2)*Hypergeometric2F1(-1/3, 3/2, 2/3, Sin(e +
	// f*x)^2)*Sec(e+f*x))/(2*b*f*(b*Sin(e+f*x))^(2/3))}
	public void test02080() {
		check("Integrate(Sec(e+f*x)^2/(b*Sin(e+f*x))^(5/3), x)",
				"(-3*Sqrt(Cos(e+f*x)^2)*Hypergeometric2F1(-1/3, 3/2, 2/3, Sin(e+f*x)^2)*Sec(e+f*x))/(2*b*f*(b*Sin(e+f*x))^(2/3))");

	}

	// {Sec(e+f*x)^4/(b*Sin(e+f*x))^(5/3), x, 1, (-3*Sqrt(Cos(e+f*x)^2)*Hypergeometric2F1(-1/3, 5/2, 2/3, Sin(e +
	// f*x)^2)*Sec(e+f*x))/(2*b*f*(b*Sin(e+f*x))^(2/3))}
	public void test02081() {
		check("Integrate(Sec(e+f*x)^4/(b*Sin(e+f*x))^(5/3), x)",
				"(-3*Sqrt(Cos(e+f*x)^2)*Hypergeometric2F1(-1/3, 5/2, 2/3, Sin(e+f*x)^2)*Sec(e+f*x))/(2*b*f*(b*Sin(e+f*x))^(2/3))");

	}

	// {Cos(x)^(2/3)/Sin(x)^(8/3), x, 1, (-3*Cos(x)^(5/3))/(5*Sin(x)^(5/3))}
	public void test02082() {
		check("Integrate(Cos(x)^(2/3)/Sin(x)^(8/3), x)", "(-3*Cos(x)^(5/3))/(5*Sin(x)^(5/3))");

	}

	// {Sin(x)^(2/3)/Cos(x)^(8/3), x, 1, (3*Sin(x)^(5/3))/(5*Cos(x)^(5/3))}
	public void test02083() {
		check("Integrate(Sin(x)^(2/3)/Cos(x)^(8/3), x)", "(3*Sin(x)^(5/3))/(5*Cos(x)^(5/3))");

	}

	// {Cos(e+f*x)^n*Sin(e+f*x)^m, x, 1, -((Cos(e+f*x)^(1+n)*Hypergeometric2F1((1-m)/2, (1+n)/2, (3+n)/2,
	// Cos(e+f*x)^2)*Sin(e+f*x)^(-1+m)*(Sin(e+f*x)^2)^((1-m)/2))/(f*(1+n)))}
	public void test02084() {
		check("Integrate(Cos(e+f*x)^n*Sin(e+f*x)^m, x)",
				"-((Cos(e+f*x)^(1+n)*Hypergeometric2F1((1-m)/2, (1+n)/2, (3+n)/2, Cos(e+f*x)^2)*Sin(e+f*x)^(-1+m)*(Sin(e+f*x)^2)^((1-m)/2))/(f*(1+n)))");

	}

	// {(d*Cos(e+f*x))^n*Sin(e+f*x)^m, x, 1, -(((d*Cos(e+f*x))^(1+n)*Hypergeometric2F1((1-m)/2, (1+n)/2, (3
	// +n)/2, Cos(e+f*x)^2)*Sin(e+f*x)^(-1+m)*(Sin(e+f*x)^2)^((1-m)/2))/(d*f*(1+n)))}
	public void test02085() {
		check("Integrate((d*Cos(e+f*x))^n*Sin(e+f*x)^m, x)",
				"-(((d*Cos(e+f*x))^(1+n)*Hypergeometric2F1((1-m)/2, (1+n)/2, (3+n)/2, Cos(e+f*x)^2)*Sin(e+f*x)^(-1+m)*(Sin(e+f*x)^2)^((1-m)/2))/(d*f*(1+n)))");

	}

	// {Cos(e+f*x)^n*(b*Sin(e+f*x))^m, x, 1, -((b*Cos(e+f*x)^(1+n)*Hypergeometric2F1((1-m)/2, (1+n)/2, (3 +
	// n)/2, Cos(e+f*x)^2)*(b*Sin(e+f*x))^(-1+m)*(Sin(e+f*x)^2)^((1-m)/2))/(f*(1+n)))}
	public void test02086() {
		check("Integrate(Cos(e+f*x)^n*(b*Sin(e+f*x))^m, x)",
				"-((b*Cos(e+f*x)^(1+n)*Hypergeometric2F1((1-m)/2, (1+n)/2, (3+n)/2, Cos(e+f*x)^2)*(b*Sin(e+f*x))^(-1+m)*(Sin(e+f*x)^2)^((1-m)/2))/(f*(1+n)))");

	}

	// {(d*Cos(e+f*x))^n*(b*Sin(e+f*x))^m, x, 1, -((b*(d*Cos(e+f*x))^(1+n)*Hypergeometric2F1((1-m)/2, (1 +
	// n)/2, (3+n)/2, Cos(e+f*x)^2)*(b*Sin(e+f*x))^(-1+m)*(Sin(e+f*x)^2)^((1-m)/2))/(d*f*(1+n)))}
	public void test02087() {
		check("Integrate((d*Cos(e+f*x))^n*(b*Sin(e+f*x))^m, x)",
				"-((b*(d*Cos(e+f*x))^(1+n)*Hypergeometric2F1((1-m)/2, (1+n)/2, (3+n)/2, Cos(e+f*x)^2)*(b*Sin(e+f*x))^(-1+m)*(Sin(e+f*x)^2)^((1-m)/2))/(d*f*(1+n)))");

	}

	// {Cos(a+b*x)^4*(c*Sin(a+b*x))^m, x, 1, (Cos(a+b*x)*Hypergeometric2F1(-3/2, (1+m)/2, (3+m)/2, Sin(a +
	// b*x)^2)*(c*Sin(a+b*x))^(1+m))/(b*c*(1+m)*Sqrt(Cos(a+b*x)^2))}
	public void test02088() {
		check("Integrate(Cos(a+b*x)^4*(c*Sin(a+b*x))^m, x)",
				"(Cos(a+b*x)*Hypergeometric2F1(-3/2, (1+m)/2, (3+m)/2, Sin(a+b*x)^2)*(c*Sin(a+b*x))^(1+m))/(b*c*(1+m)*Sqrt(Cos(a+b*x)^2))");

	}

	// {Cos(a+b*x)^2*(c*Sin(a+b*x))^m, x, 1, (Cos(a+b*x)*Hypergeometric2F1(-1/2, (1+m)/2, (3+m)/2, Sin(a +
	// b*x)^2)*(c*Sin(a+b*x))^(1+m))/(b*c*(1+m)*Sqrt(Cos(a+b*x)^2))}
	public void test02089() {
		check("Integrate(Cos(a+b*x)^2*(c*Sin(a+b*x))^m, x)",
				"(Cos(a+b*x)*Hypergeometric2F1(-1/2, (1+m)/2, (3+m)/2, Sin(a+b*x)^2)*(c*Sin(a+b*x))^(1+m))/(b*c*(1+m)*Sqrt(Cos(a+b*x)^2))");

	}

	// {(c*Sin(a+b*x))^m, x, 1, (Cos(a+b*x)*Hypergeometric2F1(1/2, (1+m)/2, (3+m)/2, Sin(a+b*x)^2)*(c*Sin(a +
	// b*x))^(1+m))/(b*c*(1+m)*Sqrt(Cos(a+b*x)^2))}
	public void test02090() {
		check("Integrate((c*Sin(a+b*x))^m, x)",
				"(Cos(a+b*x)*Hypergeometric2F1(1/2, (1+m)/2, (3+m)/2, Sin(a+b*x)^2)*(c*Sin(a+b*x))^(1+m))/(b*c*(1+m)*Sqrt(Cos(a+b*x)^2))");

	}

	// {Sec(a+b*x)^2*(c*Sin(a+b*x))^m, x, 1, (Sqrt(Cos(a+b*x)^2)*Hypergeometric2F1(3/2, (1+m)/2, (3+m)/2,
	// Sin(a+b*x)^2)*Sec(a+b*x)*(c*Sin(a+b*x))^(1+m))/(b*c*(1+m))}
	public void test02091() {
		check("Integrate(Sec(a+b*x)^2*(c*Sin(a+b*x))^m, x)",
				"(Sqrt(Cos(a+b*x)^2)*Hypergeometric2F1(3/2, (1+m)/2, (3+m)/2, Sin(a+b*x)^2)*Sec(a+b*x)*(c*Sin(a+b*x))^(1+m))/(b*c*(1+m))");

	}

	// {Sec(a+b*x)^4*(c*Sin(a+b*x))^m, x, 1, (Sqrt(Cos(a+b*x)^2)*Hypergeometric2F1(5/2, (1+m)/2, (3+m)/2,
	// Sin(a+b*x)^2)*Sec(a+b*x)*(c*Sin(a+b*x))^(1+m))/(b*c*(1+m))}
	public void test02092() {
		check("Integrate(Sec(a+b*x)^4*(c*Sin(a+b*x))^m, x)",
				"(Sqrt(Cos(a+b*x)^2)*Hypergeometric2F1(5/2, (1+m)/2, (3+m)/2, Sin(a+b*x)^2)*Sec(a+b*x)*(c*Sin(a+b*x))^(1+m))/(b*c*(1+m))");

	}

	// {(d*Cos(a+b*x))^(3/2)*(c*Sin(a+b*x))^m, x, 1, (d*Sqrt(d*Cos(a+b*x))*Hypergeometric2F1(-1/4, (1+m)/2, (3 +
	// m)/2, Sin(a+b*x)^2)*(c*Sin(a+b*x))^(1+m))/(b*c*(1+m)*(Cos(a+b*x)^2)^(1/4))}
	public void test02093() {
		check("Integrate((d*Cos(a+b*x))^(3/2)*(c*Sin(a+b*x))^m, x)",
				"(d*Sqrt(d*Cos(a+b*x))*Hypergeometric2F1(-1/4, (1+m)/2, (3+m)/2, Sin(a+b*x)^2)*(c*Sin(a+b*x))^(1+m))/(b*c*(1+m)*(Cos(a+b*x)^2)^(1/4))");

	}

	// {Sqrt(d*Cos(a+b*x))*(c*Sin(a+b*x))^m, x, 1, (d*(Cos(a+b*x)^2)^(1/4)*Hypergeometric2F1(1/4, (1+m)/2, (3 +
	// m)/2, Sin(a+b*x)^2)*(c*Sin(a+b*x))^(1+m))/(b*c*(1+m)*Sqrt(d*Cos(a+b*x)))}
	public void test02094() {
		check("Integrate(Sqrt(d*Cos(a+b*x))*(c*Sin(a+b*x))^m, x)",
				"(d*(Cos(a+b*x)^2)^(1/4)*Hypergeometric2F1(1/4, (1+m)/2, (3+m)/2, Sin(a+b*x)^2)*(c*Sin(a+b*x))^(1+m))/(b*c*(1+m)*Sqrt(d*Cos(a+b*x)))");

	}

	// {(c*Sin(a+b*x))^m/Sqrt(d*Cos(a+b*x)), x, 1, (d*(Cos(a+b*x)^2)^(3/4)*Hypergeometric2F1(3/4, (1+m)/2, (3 +
	// m)/2, Sin(a+b*x)^2)*(c*Sin(a+b*x))^(1+m))/(b*c*(1+m)*(d*Cos(a+b*x))^(3/2))}
	public void test02095() {
		check("Integrate((c*Sin(a+b*x))^m/Sqrt(d*Cos(a+b*x)), x)",
				"(d*(Cos(a+b*x)^2)^(3/4)*Hypergeometric2F1(3/4, (1+m)/2, (3+m)/2, Sin(a+b*x)^2)*(c*Sin(a+b*x))^(1+m))/(b*c*(1+m)*(d*Cos(a+b*x))^(3/2))");

	}

	// {(c*Sin(a+b*x))^m/(d*Cos(a+b*x))^(3/2), x, 1, ((Cos(a+b*x)^2)^(1/4)*Hypergeometric2F1(5/4, (1+m)/2, (3 +
	// m)/2, Sin(a+b*x)^2)*(c*Sin(a+b*x))^(1+m))/(b*c*d*(1+m)*Sqrt(d*Cos(a+b*x)))}
	public void test02096() {
		check("Integrate((c*Sin(a+b*x))^m/(d*Cos(a+b*x))^(3/2), x)",
				"((Cos(a+b*x)^2)^(1/4)*Hypergeometric2F1(5/4, (1+m)/2, (3+m)/2, Sin(a+b*x)^2)*(c*Sin(a+b*x))^(1+m))/(b*c*d*(1+m)*Sqrt(d*Cos(a+b*x)))");

	}

	// {(c*Sin(a+b*x))^m/(d*Cos(a+b*x))^(5/2), x, 1, ((Cos(a+b*x)^2)^(3/4)*Hypergeometric2F1(7/4, (1+m)/2, (3 +
	// m)/2, Sin(a+b*x)^2)*(c*Sin(a+b*x))^(1+m))/(b*c*d*(1+m)*(d*Cos(a+b*x))^(3/2))}
	public void test02097() {
		check("Integrate((c*Sin(a+b*x))^m/(d*Cos(a+b*x))^(5/2), x)",
				"((Cos(a+b*x)^2)^(3/4)*Hypergeometric2F1(7/4, (1+m)/2, (3+m)/2, Sin(a+b*x)^2)*(c*Sin(a+b*x))^(1+m))/(b*c*d*(1+m)*(d*Cos(a+b*x))^(3/2))");

	}

	// {(d*Cos(a+b*x))^n*Sin(a+b*x)^4, x, 1, -(((d*Cos(a+b*x))^(1+n)*Hypergeometric2F1(-3/2, (1+n)/2, (3 +
	// n)/2, Cos(a+b*x)^2)*Sin(a+b*x))/(b*d*(1+n)*Sqrt(Sin(a+b*x)^2)))}
	public void test02098() {
		check("Integrate((d*Cos(a+b*x))^n*Sin(a+b*x)^4, x)",
				"-(((d*Cos(a+b*x))^(1+n)*Hypergeometric2F1(-3/2, (1+n)/2, (3+n)/2, Cos(a+b*x)^2)*Sin(a+b*x))/(b*d*(1+n)*Sqrt(Sin(a+b*x)^2)))");

	}

	// {(d*Cos(a+b*x))^n*Sin(a+b*x)^2, x, 1, -(((d*Cos(a+b*x))^(1+n)*Hypergeometric2F1(-1/2, (1+n)/2, (3 +
	// n)/2, Cos(a+b*x)^2)*Sin(a+b*x))/(b*d*(1+n)*Sqrt(Sin(a+b*x)^2)))}
	public void test02099() {
		check("Integrate((d*Cos(a+b*x))^n*Sin(a+b*x)^2, x)",
				"-(((d*Cos(a+b*x))^(1+n)*Hypergeometric2F1(-1/2, (1+n)/2, (3+n)/2, Cos(a+b*x)^2)*Sin(a+b*x))/(b*d*(1+n)*Sqrt(Sin(a+b*x)^2)))");

	}

	// {(d*Cos(a+b*x))^n, x, 1, -(((d*Cos(a+b*x))^(1+n)*Hypergeometric2F1(1/2, (1+n)/2, (3+n)/2, Cos(a +
	// b*x)^2)*Sin(a+b*x))/(b*d*(1+n)*Sqrt(Sin(a+b*x)^2)))}
	public void test02100() {
		check("Integrate((d*Cos(a+b*x))^n, x)",
				"-(((d*Cos(a+b*x))^(1+n)*Hypergeometric2F1(1/2, (1+n)/2, (3+n)/2, Cos(a+b*x)^2)*Sin(a+b*x))/(b*d*(1+n)*Sqrt(Sin(a+b*x)^2)))");

	}

	// {(d*Cos(a+b*x))^n*Csc(a+b*x)^2, x, 1, -(((d*Cos(a+b*x))^(1+n)*Csc(a+b*x)*Hypergeometric2F1(3/2, (1 +
	// n)/2, (3+n)/2, Cos(a+b*x)^2)*Sqrt(Sin(a+b*x)^2))/(b*d*(1+n)))}
	public void test02101() {
		check("Integrate((d*Cos(a+b*x))^n*Csc(a+b*x)^2, x)",
				"-(((d*Cos(a+b*x))^(1+n)*Csc(a+b*x)*Hypergeometric2F1(3/2, (1+n)/2, (3+n)/2, Cos(a+b*x)^2)*Sqrt(Sin(a+b*x)^2))/(b*d*(1+n)))");

	}

	// {(d*Cos(a+b*x))^n*Csc(a+b*x)^4, x, 1, -(((d*Cos(a+b*x))^(1+n)*Csc(a+b*x)*Hypergeometric2F1(5/2, (1 +
	// n)/2, (3+n)/2, Cos(a+b*x)^2)*Sqrt(Sin(a+b*x)^2))/(b*d*(1+n)))}
	public void test02102() {
		check("Integrate((d*Cos(a+b*x))^n*Csc(a+b*x)^4, x)",
				"-(((d*Cos(a+b*x))^(1+n)*Csc(a+b*x)*Hypergeometric2F1(5/2, (1+n)/2, (3+n)/2, Cos(a+b*x)^2)*Sqrt(Sin(a+b*x)^2))/(b*d*(1+n)))");

	}

	// {(d*Cos(a+b*x))^n*(c*Sin(a+b*x))^(5/2), x, 1, -((c*(d*Cos(a+b*x))^(1+n)*Hypergeometric2F1(-3/4, (1 +
	// n)/2, (3+n)/2, Cos(a+b*x)^2)*(c*Sin(a+b*x))^(3/2))/(b*d*(1+n)*(Sin(a+b*x)^2)^(3/4)))}
	public void test02103() {
		check("Integrate((d*Cos(a+b*x))^n*(c*Sin(a+b*x))^(5/2), x)",
				"-((c*(d*Cos(a+b*x))^(1+n)*Hypergeometric2F1(-3/4, (1+n)/2, (3+n)/2, Cos(a+b*x)^2)*(c*Sin(a+b*x))^(3/2))/(b*d*(1+n)*(Sin(a+b*x)^2)^(3/4)))");

	}

	// {(d*Cos(a+b*x))^n*(c*Sin(a+b*x))^(3/2), x, 1, -((c*(d*Cos(a+b*x))^(1+n)*Hypergeometric2F1(-1/4, (1 +
	// n)/2, (3+n)/2, Cos(a+b*x)^2)*Sqrt(c*Sin(a+b*x)))/(b*d*(1+n)*(Sin(a+b*x)^2)^(1/4)))}
	public void test02104() {
		check("Integrate((d*Cos(a+b*x))^n*(c*Sin(a+b*x))^(3/2), x)",
				"-((c*(d*Cos(a+b*x))^(1+n)*Hypergeometric2F1(-1/4, (1+n)/2, (3+n)/2, Cos(a+b*x)^2)*Sqrt(c*Sin(a+b*x)))/(b*d*(1+n)*(Sin(a+b*x)^2)^(1/4)))");

	}

	// {(d*Cos(a+b*x))^n*Sqrt(c*Sin(a+b*x)), x, 1, -((c*(d*Cos(a+b*x))^(1+n)*Hypergeometric2F1(1/4, (1+n)/2,
	// (3+n)/2, Cos(a+b*x)^2)*(Sin(a+b*x)^2)^(1/4))/(b*d*(1+n)*Sqrt(c*Sin(a+b*x))))}
	public void test02105() {
		check("Integrate((d*Cos(a+b*x))^n*Sqrt(c*Sin(a+b*x)), x)",
				"-((c*(d*Cos(a+b*x))^(1+n)*Hypergeometric2F1(1/4, (1+n)/2, (3+n)/2, Cos(a+b*x)^2)*(Sin(a+b*x)^2)^(1/4))/(b*d*(1+n)*Sqrt(c*Sin(a+b*x))))");

	}

	// {(d*Cos(a+b*x))^n/Sqrt(c*Sin(a+b*x)), x, 1, -((c*(d*Cos(a+b*x))^(1+n)*Hypergeometric2F1(3/4, (1+n)/2,
	// (3+n)/2, Cos(a+b*x)^2)*(Sin(a+b*x)^2)^(3/4))/(b*d*(1+n)*(c*Sin(a+b*x))^(3/2)))}
	public void test02106() {
		check("Integrate((d*Cos(a+b*x))^n/Sqrt(c*Sin(a+b*x)), x)",
				"-((c*(d*Cos(a+b*x))^(1+n)*Hypergeometric2F1(3/4, (1+n)/2, (3+n)/2, Cos(a+b*x)^2)*(Sin(a+b*x)^2)^(3/4))/(b*d*(1+n)*(c*Sin(a+b*x))^(3/2)))");

	}

	// {(d*Cos(a+b*x))^n/(c*Sin(a+b*x))^(3/2), x, 1, -(((d*Cos(a+b*x))^(1+n)*Hypergeometric2F1(5/4, (1+n)/2,
	// (3+n)/2, Cos(a+b*x)^2)*(Sin(a+b*x)^2)^(1/4))/(b*c*d*(1+n)*Sqrt(c*Sin(a+b*x))))}
	public void test02107() {
		check("Integrate((d*Cos(a+b*x))^n/(c*Sin(a+b*x))^(3/2), x)",
				"-(((d*Cos(a+b*x))^(1+n)*Hypergeometric2F1(5/4, (1+n)/2, (3+n)/2, Cos(a+b*x)^2)*(Sin(a+b*x)^2)^(1/4))/(b*c*d*(1+n)*Sqrt(c*Sin(a+b*x))))");

	}

	// {Sqrt(b*Sec(e+f*x))/(a*Sin(e+f*x))^(3/2), x, 1, (-2*b)/(a*f*Sqrt(b*Sec(e+f*x))*Sqrt(a*Sin(e+f*x)))}
	public void test02108() {
		check("Integrate(Sqrt(b*Sec(e+f*x))/(a*Sin(e+f*x))^(3/2), x)",
				"(-2*b)/(a*f*Sqrt(b*Sec(e+f*x))*Sqrt(a*Sin(e+f*x)))");

	}

	// {1/(Sqrt(b*Sec(e+f*x))*Sin(e+f*x)^(5/2)), x, 1, (-2*b)/(3*f*(b*Sec(e+f*x))^(3/2)*Sin(e+f*x)^(3/2))}
	public void test02109() {
		check("Integrate(1/(Sqrt(b*Sec(e+f*x))*Sin(e+f*x)^(5/2)), x)",
				"(-2*b)/(3*f*(b*Sec(e+f*x))^(3/2)*Sin(e+f*x)^(3/2))");

	}

	// {1/((b*Sec(e+f*x))^(3/2)*(a*Sin(e+f*x))^(7/2)), x, 1, (-2*b)/(5*a*f*(b*Sec(e+f*x))^(5/2)*(a*Sin(e +
	// f*x))^(5/2))}
	public void test02110() {
		check("Integrate(1/((b*Sec(e+f*x))^(3/2)*(a*Sin(e+f*x))^(7/2)), x)",
				"(-2*b)/(5*a*f*(b*Sec(e+f*x))^(5/2)*(a*Sin(e+f*x))^(5/2))");

	}

	// {Sqrt(a+a*Sin(c+d*x)), x, 1, (-2*a*Cos(c+d*x))/(d*Sqrt(a+a*Sin(c+d*x)))}
	public void test02111() {
		check("Integrate(Sqrt(a+a*Sin(c+d*x)), x)", "(-2*a*Cos(c+d*x))/(d*Sqrt(a+a*Sin(c+d*x)))");

	}

	// {(2+2*Sin(c+d*x))^n, x, 1, -((2^(1/2+2*n)*Cos(c+d*x)*Hypergeometric2F1(1/2, 1/2-n, 3/2, (1-Sin(c +
	// d*x))/2))/(d*Sqrt(1+Sin(c+d*x))))}
	public void test02112() {
		check("Integrate((2+2*Sin(c+d*x))^n, x)",
				"-((2^(1/2+2*n)*Cos(c+d*x)*Hypergeometric2F1(1/2, 1/2-n, 3/2, (1-Sin(c+d*x))/2))/(d*Sqrt(1+Sin(c+d*x))))");

	}

	// {(2-2*Sin(c+d*x))^n, x, 1, (2^(1/2+2*n)*Cos(c+d*x)*Hypergeometric2F1(1/2, 1/2-n, 3/2, (1+Sin(c +
	// d*x))/2))/(d*Sqrt(1-Sin(c+d*x)))}
	public void test02113() {
		check("Integrate((2-2*Sin(c+d*x))^n, x)",
				"(2^(1/2+2*n)*Cos(c+d*x)*Hypergeometric2F1(1/2, 1/2-n, 3/2, (1+Sin(c+d*x))/2))/(d*Sqrt(1-Sin(c+d*x)))");

	}

	// {(5+3*Sin(c+d*x))^(-1), x, 1, x/4+ArcTan(Cos(c+d*x)/(3+Sin(c+d*x)))/(2*d)}
	public void test02114() {
		check("Integrate((5+3*Sin(c+d*x))^(-1), x)", "x/4+ArcTan(Cos(c+d*x)/(3+Sin(c+d*x)))/(2*d)");

	}

	// {(5-3*Sin(c+d*x))^(-1), x, 1, x/4-ArcTan(Cos(c+d*x)/(3-Sin(c+d*x)))/(2*d)}
	public void test02115() {
		check("Integrate((5-3*Sin(c+d*x))^(-1), x)", "x/4-ArcTan(Cos(c+d*x)/(3-Sin(c+d*x)))/(2*d)");

	}

	// {(-5+3*Sin(c+d*x))^(-1), x, 1, -x/4+ArcTan(Cos(c+d*x)/(3-Sin(c+d*x)))/(2*d)}
	public void test02116() {
		check("Integrate((-5+3*Sin(c+d*x))^(-1), x)", "-x/4+ArcTan(Cos(c+d*x)/(3-Sin(c+d*x)))/(2*d)");

	}

	// {(-5-3*Sin(c+d*x))^(-1), x, 1, -x/4-ArcTan(Cos(c+d*x)/(3+Sin(c+d*x)))/(2*d)}
	public void test02117() {
		check("Integrate((-5-3*Sin(c+d*x))^(-1), x)", "-x/4-ArcTan(Cos(c+d*x)/(3+Sin(c+d*x)))/(2*d)");

	}

	// {(e*x)^(-1+2*n)*(b*Sin(c+d*x^n))^p, x, 1, ((e*x)^(2*n)*Unintegrable(x^(-1+2*n)*(b*Sin(c+d*x^n))^p,
	// x))/(e*x^(2*n))}
	public void test02118() {
		check("Integrate((e*x)^(-1+2*n)*(b*Sin(c+d*x^n))^p, x)",
				"((e*x)^(2*n)*Unintegrable(x^(-1+2*n)*(b*Sin(c+d*x^n))^p, x))/(e*x^(2*n))");

	}

	// {(e*x)^(-1+2*n)*(a+b*Sin(c+d*x^n))^p, x, 1, ((e*x)^(2*n)*Unintegrable(x^(-1+2*n)*(a+b*Sin(c +
	// d*x^n))^p, x))/(e*x^(2*n))}
	public void test02119() {
		check("Integrate((e*x)^(-1+2*n)*(a+b*Sin(c+d*x^n))^p, x)",
				"((e*x)^(2*n)*Unintegrable(x^(-1+2*n)*(a+b*Sin(c+d*x^n))^p, x))/(e*x^(2*n))");

	}

	// {Sin(b*(c+d*x)^2), x, 1, (Sqrt(Pi/2)*FresnelS(Sqrt(b)*Sqrt(2/Pi)*(c+d*x)))/(Sqrt(b)*d)}
	public void test02120() {
		check("Integrate(Sin(b*(c+d*x)^2), x)", "(Sqrt(Pi/2)*FresnelS(Sqrt(b)*Sqrt(2/Pi)*(c+d*x)))/(Sqrt(b)*d)");

	}

	// {Cos(c+d*x)^2/(a+a*Sin(c+d*x))^3, x, 1, -Cos(c+d*x)^3/(3*d*(a+a*Sin(c+d*x))^3)}
	public void test02121() {
		check("Integrate(Cos(c+d*x)^2/(a+a*Sin(c+d*x))^3, x)", "-Cos(c+d*x)^3/(3*d*(a+a*Sin(c+d*x))^3)");

	}

	// {Sec(c+d*x)^2*(a+a*Sin(c+d*x))^(3/2), x, 1, (2*a*Sec(c+d*x)*Sqrt(a+a*Sin(c+d*x)))/d}
	public void test02122() {
		check("Integrate(Sec(c+d*x)^2*(a+a*Sin(c+d*x))^(3/2), x)", "(2*a*Sec(c+d*x)*Sqrt(a+a*Sin(c+d*x)))/d");

	}

	// {Sec(c+d*x)^4*(a+a*Sin(c+d*x))^(5/2), x, 1, (2*a*Sec(c+d*x)^3*(a+a*Sin(c+d*x))^(3/2))/(3*d)}
	public void test02123() {
		check("Integrate(Sec(c+d*x)^4*(a+a*Sin(c+d*x))^(5/2), x)", "(2*a*Sec(c+d*x)^3*(a+a*Sin(c+d*x))^(3/2))/(3*d)");

	}

	// {Sec(c+d*x)^6*(a+a*Sin(c+d*x))^(7/2), x, 1, (2*a*Sec(c+d*x)^5*(a+a*Sin(c+d*x))^(5/2))/(5*d)}
	public void test02124() {
		check("Integrate(Sec(c+d*x)^6*(a+a*Sin(c+d*x))^(7/2), x)", "(2*a*Sec(c+d*x)^5*(a+a*Sin(c+d*x))^(5/2))/(5*d)");

	}

	// {Cos(c+d*x)^2/Sqrt(a+a*Sin(c+d*x)), x, 1, (-2*a*Cos(c+d*x)^3)/(3*d*(a+a*Sin(c+d*x))^(3/2))}
	public void test02125() {
		check("Integrate(Cos(c+d*x)^2/Sqrt(a+a*Sin(c+d*x)), x)", "(-2*a*Cos(c+d*x)^3)/(3*d*(a+a*Sin(c+d*x))^(3/2))");

	}

	// {Cos(c+d*x)^4/(a+a*Sin(c+d*x))^(3/2), x, 1, (-2*a*Cos(c+d*x)^5)/(5*d*(a+a*Sin(c+d*x))^(5/2))}
	public void test02126() {
		check("Integrate(Cos(c+d*x)^4/(a+a*Sin(c+d*x))^(3/2), x)", "(-2*a*Cos(c+d*x)^5)/(5*d*(a+a*Sin(c+d*x))^(5/2))");

	}

	// {Cos(c+d*x)^6/(a+a*Sin(c+d*x))^(5/2), x, 1, (-2*a*Cos(c+d*x)^7)/(7*d*(a+a*Sin(c+d*x))^(7/2))}
	public void test02127() {
		check("Integrate(Cos(c+d*x)^6/(a+a*Sin(c+d*x))^(5/2), x)", "(-2*a*Cos(c+d*x)^7)/(7*d*(a+a*Sin(c+d*x))^(7/2))");

	}

	// {Sqrt(a+a*Sin(c+d*x))/(e*Cos(c+d*x))^(3/2), x, 1, (2*Sqrt(a+a*Sin(c+d*x)))/(d*e*Sqrt(e*Cos(c+d*x)))}
	public void test02128() {
		check("Integrate(Sqrt(a+a*Sin(c+d*x))/(e*Cos(c+d*x))^(3/2), x)",
				"(2*Sqrt(a+a*Sin(c+d*x)))/(d*e*Sqrt(e*Cos(c+d*x)))");

	}

	// {(a+a*Sin(c+d*x))^(3/2)/(e*Cos(c+d*x))^(5/2), x, 1, (2*(a+a*Sin(c+d*x))^(3/2))/(3*d*e*(e*Cos(c +
	// d*x))^(3/2))}
	public void test02129() {
		check("Integrate((a+a*Sin(c+d*x))^(3/2)/(e*Cos(c+d*x))^(5/2), x)",
				"(2*(a+a*Sin(c+d*x))^(3/2))/(3*d*e*(e*Cos(c+d*x))^(3/2))");

	}

	// {(a+a*Sin(c+d*x))^(5/2)/(e*Cos(c+d*x))^(7/2), x, 1, (2*(a+a*Sin(c+d*x))^(5/2))/(5*d*e*(e*Cos(c +
	// d*x))^(5/2))}
	public void test02130() {
		check("Integrate((a+a*Sin(c+d*x))^(5/2)/(e*Cos(c+d*x))^(7/2), x)",
				"(2*(a+a*Sin(c+d*x))^(5/2))/(5*d*e*(e*Cos(c+d*x))^(5/2))");

	}

	// {1/(Sqrt(e*Cos(c+d*x))*Sqrt(a+a*Sin(c+d*x))), x, 1, (-2*Sqrt(e*Cos(c+d*x)))/(d*e*Sqrt(a+a*Sin(c +
	// d*x)))}
	public void test02131() {
		check("Integrate(1/(Sqrt(e*Cos(c+d*x))*Sqrt(a+a*Sin(c+d*x))), x)",
				"(-2*Sqrt(e*Cos(c+d*x)))/(d*e*Sqrt(a+a*Sin(c+d*x)))");

	}

	// {Sqrt(e*Cos(c+d*x))/(a+a*Sin(c+d*x))^(3/2), x, 1, (-2*(e*Cos(c+d*x))^(3/2))/(3*d*e*(a+a*Sin(c +
	// d*x))^(3/2))}
	public void test02132() {
		check("Integrate(Sqrt(e*Cos(c+d*x))/(a+a*Sin(c+d*x))^(3/2), x)",
				"(-2*(e*Cos(c+d*x))^(3/2))/(3*d*e*(a+a*Sin(c+d*x))^(3/2))");

	}

	// {(e*Cos(c+d*x))^(3/2)/(a+a*Sin(c+d*x))^(5/2), x, 1, (-2*(e*Cos(c+d*x))^(5/2))/(5*d*e*(a+a*Sin(c +
	// d*x))^(5/2))}
	public void test02133() {
		check("Integrate((e*Cos(c+d*x))^(3/2)/(a+a*Sin(c+d*x))^(5/2), x)",
				"(-2*(e*Cos(c+d*x))^(5/2))/(5*d*e*(a+a*Sin(c+d*x))^(5/2))");

	}

	// {(e*Cos(c+d*x))^(-1-m)*(a+a*Sin(c+d*x))^m, x, 1, (a+a*Sin(c+d*x))^m/(d*e*m*(e*Cos(c+d*x))^m)}
	public void test02134() {
		check("Integrate((e*Cos(c+d*x))^(-1-m)*(a+a*Sin(c+d*x))^m, x)", "(a+a*Sin(c+d*x))^m/(d*e*m*(e*Cos(c+d*x))^m)");

	}

	// {(e*Cos(c+d*x))^(1-2*m)*(a+a*Sin(c+d*x))^m, x, 1, -((a*(e*Cos(c+d*x))^(2-2*m)*(a+a*Sin(c +
	// d*x))^(-1+m))/(d*e*(1-m)))}
	public void test02135() {
		check("Integrate((e*Cos(c+d*x))^(1-2*m)*(a+a*Sin(c+d*x))^m, x)",
				"-((a*(e*Cos(c+d*x))^(2-2*m)*(a+a*Sin(c+d*x))^(-1+m))/(d*e*(1-m)))");

	}

	// {(e*Cos(c+d*x))^p/(a+b*Sin(c+d*x)), x, 1, -((e*AppellF1(1-p, (1-p)/2, (1-p)/2, 2-p, (a+b)/(a +
	// b*Sin(c+d*x)), (a-b)/(a+b*Sin(c+d*x)))*(e*Cos(c+d*x))^(-1+p)*(-((b*(1-Sin(c+d*x)))/(a+b*Sin(c +
	// d*x))))^((1-p)/2)*((b*(1+Sin(c+d*x)))/(a+b*Sin(c+d*x)))^((1-p)/2))/(b*d*(1-p)))}
	public void test02136() {
		check("Integrate((e*Cos(c+d*x))^p/(a+b*Sin(c+d*x)), x)",
				"-((e*AppellF1(1-p, (1-p)/2, (1-p)/2, 2-p, (a+b)/(a+b*Sin(c+d*x)), (a-b)/(a+b*Sin(c+d*x)))*(e*Cos(c+d*x))^(-1+p)*(-((b*(1-Sin(c+d*x)))/(a+b*Sin(c+d*x))))^((1-p)/2)*((b*(1+Sin(c+d*x)))/(a+b*Sin(c+d*x)))^((1-p)/2))/(b*d*(1-p)))");

	}

	// {(e*Cos(c+d*x))^p/(a+b*Sin(c+d*x))^2, x, 1, -((e*AppellF1(2-p, (1-p)/2, (1-p)/2, 3-p, (a+b)/(a +
	// b*Sin(c+d*x)), (a-b)/(a+b*Sin(c+d*x)))*(e*Cos(c+d*x))^(-1+p)*(-((b*(1-Sin(c+d*x)))/(a+b*Sin(c +
	// d*x))))^((1-p)/2)*((b*(1+Sin(c+d*x)))/(a+b*Sin(c+d*x)))^((1-p)/2))/(b*d*(2-p)*(a+b*Sin(c +
	// d*x))))}
	public void test02137() {
		check("Integrate((e*Cos(c+d*x))^p/(a+b*Sin(c+d*x))^2, x)",
				"-((e*AppellF1(2-p, (1-p)/2, (1-p)/2, 3-p, (a+b)/(a+b*Sin(c+d*x)), (a-b)/(a+b*Sin(c+d*x)))*(e*Cos(c+d*x))^(-1+p)*(-((b*(1-Sin(c+d*x)))/(a+b*Sin(c+d*x))))^((1-p)/2)*((b*(1+Sin(c+d*x)))/(a+b*Sin(c+d*x)))^((1-p)/2))/(b*d*(2-p)*(a+b*Sin(c+d*x))))");

	}

	// {(e*Cos(c+d*x))^p/(a+b*Sin(c+d*x))^3, x, 1, -((e*AppellF1(3-p, (1-p)/2, (1-p)/2, 4-p, (a+b)/(a +
	// b*Sin(c+d*x)), (a-b)/(a+b*Sin(c+d*x)))*(e*Cos(c+d*x))^(-1+p)*(-((b*(1-Sin(c+d*x)))/(a+b*Sin(c +
	// d*x))))^((1-p)/2)*((b*(1+Sin(c+d*x)))/(a+b*Sin(c+d*x)))^((1-p)/2))/(b*d*(3-p)*(a+b*Sin(c +
	// d*x))^2))}
	public void test02138() {
		check("Integrate((e*Cos(c+d*x))^p/(a+b*Sin(c+d*x))^3, x)",
				"-((e*AppellF1(3-p, (1-p)/2, (1-p)/2, 4-p, (a+b)/(a+b*Sin(c+d*x)), (a-b)/(a+b*Sin(c+d*x)))*(e*Cos(c+d*x))^(-1+p)*(-((b*(1-Sin(c+d*x)))/(a+b*Sin(c+d*x))))^((1-p)/2)*((b*(1+Sin(c+d*x)))/(a+b*Sin(c+d*x)))^((1-p)/2))/(b*d*(3-p)*(a+b*Sin(c+d*x))^2))");

	}

	// {(e*Cos(c+d*x))^p/(a+b*Sin(c+d*x))^8, x, 1, -((e*AppellF1(8-p, (1-p)/2, (1-p)/2, 9-p, (a+b)/(a +
	// b*Sin(c+d*x)), (a-b)/(a+b*Sin(c+d*x)))*(e*Cos(c+d*x))^(-1+p)*(-((b*(1-Sin(c+d*x)))/(a+b*Sin(c +
	// d*x))))^((1-p)/2)*((b*(1+Sin(c+d*x)))/(a+b*Sin(c+d*x)))^((1-p)/2))/(b*d*(8-p)*(a+b*Sin(c +
	// d*x))^7))}
	public void test02139() {
		check("Integrate((e*Cos(c+d*x))^p/(a+b*Sin(c+d*x))^8, x)",
				"-((e*AppellF1(8-p, (1-p)/2, (1-p)/2, 9-p, (a+b)/(a+b*Sin(c+d*x)), (a-b)/(a+b*Sin(c+d*x)))*(e*Cos(c+d*x))^(-1+p)*(-((b*(1-Sin(c+d*x)))/(a+b*Sin(c+d*x))))^((1-p)/2)*((b*(1+Sin(c+d*x)))/(a+b*Sin(c+d*x)))^((1-p)/2))/(b*d*(8-p)*(a+b*Sin(c+d*x))^7))");

	}

	// {(e*Cos(c+d*x))^(-1-m)*(a+b*Sin(c+d*x))^m, x, 1, (e*(e*Cos(c+d*x))^(-2-m)*Hypergeometric2F1(1+m, (2
	// +m)/2, 2+m, (2*(a+b*Sin(c+d*x)))/((a+b)*(1+Sin(c+d*x))))*(1-Sin(c+d*x))*(-(((a-b)*(1-Sin(c +
	// d*x)))/((a+b)*(1+Sin(c+d*x)))))^(m/2)*(a+b*Sin(c+d*x))^(1+m))/((a+b)*d*(1+m))}
	public void test02140() {
		check("Integrate((e*Cos(c+d*x))^(-1-m)*(a+b*Sin(c+d*x))^m, x)",
				"(e*(e*Cos(c+d*x))^(-2-m)*Hypergeometric2F1(1+m, (2+m)/2, 2+m, (2*(a+b*Sin(c+d*x)))/((a+b)*(1+Sin(c+d*x))))*(1-Sin(c+d*x))*(-(((a-b)*(1-Sin(c+d*x)))/((a+b)*(1+Sin(c+d*x)))))^(m/2)*(a+b*Sin(c+d*x))^(1+m))/((a+b)*d*(1+m))");

	}

	// {(a+a*Sin(c+d*x))^2, x, 1, (3*a^2*x)/2-(2*a^2*Cos(c+d*x))/d-(a^2*Cos(c+d*x)*Sin(c+d*x))/(2*d)}
	public void test02141() {
		check("Integrate((a+a*Sin(c+d*x))^2, x)", "(3*a^2*x)/2-(2*a^2*Cos(c+d*x))/d-(a^2*Cos(c+d*x)*Sin(c+d*x))/(2*d)");

	}

	// {(a+a*Sin(c+d*x))^(-1), x, 1, -(Cos(c+d*x)/(d*(a+a*Sin(c+d*x))))}
	public void test02142() {
		check("Integrate((a+a*Sin(c+d*x))^(-1), x)", "-(Cos(c+d*x)/(d*(a+a*Sin(c+d*x))))");

	}

	// {(a+a*Sin(x))^(-1), x, 1, -(Cos(x)/(a+a*Sin(x)))}
	public void test02143() {
		check("Integrate((a+a*Sin(x))^(-1), x)", "-(Cos(x)/(a+a*Sin(x)))");

	}

	// {Sqrt(a+a*Sin(c+d*x)), x, 1, (-2*a*Cos(c+d*x))/(d*Sqrt(a+a*Sin(c+d*x)))}
	public void test02144() {
		check("Integrate(Sqrt(a+a*Sin(c+d*x)), x)", "(-2*a*Cos(c+d*x))/(d*Sqrt(a+a*Sin(c+d*x)))");

	}

	// {(1+Sin(c+d*x))^n, x, 1, -((2^(1/2+n)*Cos(c+d*x)*Hypergeometric2F1(1/2, 1/2-n, 3/2, (1-Sin(c +
	// d*x))/2))/(d*Sqrt(1+Sin(c+d*x))))}
	public void test02145() {
		check("Integrate((1+Sin(c+d*x))^n, x)",
				"-((2^(1/2+n)*Cos(c+d*x)*Hypergeometric2F1(1/2, 1/2-n, 3/2, (1-Sin(c+d*x))/2))/(d*Sqrt(1+Sin(c+d*x))))");

	}

	// {(1-Sin(c+d*x))^n, x, 1, (2^(1/2+n)*Cos(c+d*x)*Hypergeometric2F1(1/2, 1/2-n, 3/2, (1+Sin(c +
	// d*x))/2))/(d*Sqrt(1-Sin(c+d*x)))}
	public void test02146() {
		check("Integrate((1-Sin(c+d*x))^n, x)",
				"(2^(1/2+n)*Cos(c+d*x)*Hypergeometric2F1(1/2, 1/2-n, 3/2, (1+Sin(c+d*x))/2))/(d*Sqrt(1-Sin(c+d*x)))");

	}

	// {Sin(e+f*x)*(a+b*Sin(e+f*x)), x, 1, (b*x)/2-(a*Cos(e+f*x))/f-(b*Cos(e+f*x)*Sin(e+f*x))/(2*f)}
	public void test02147() {
		check("Integrate(Sin(e+f*x)*(a+b*Sin(e+f*x)), x)", "(b*x)/2-(a*Cos(e+f*x))/f-(b*Cos(e+f*x)*Sin(e+f*x))/(2*f)");

	}

	// {(a+b*Sin(e+f*x))^2, x, 1, ((2*a^2+b^2)*x)/2-(2*a*b*Cos(e+f*x))/f-(b^2*Cos(e+f*x)*Sin(e +
	// f*x))/(2*f)}
	public void test02148() {
		check("Integrate((a+b*Sin(e+f*x))^2, x)",
				"((2*a^2+b^2)*x)/2-(2*a*b*Cos(e+f*x))/f-(b^2*Cos(e+f*x)*Sin(e+f*x))/(2*f)");

	}

	// {1/(Sqrt(Sin(c+d*x))*Sqrt(a+b*Sin(c+d*x))), x, 1, (-2*Sqrt(a+b)*Sqrt((a*(1-Csc(c+d*x)))/(a +
	// b))*Sqrt((a*(1+Csc(c+d*x)))/(a-b))*EllipticF(ArcSin(Sqrt(a+b*Sin(c+d*x))/(Sqrt(a+b)*Sqrt(Sin(c +
	// d*x)))), -((a+b)/(a-b)))*Tan(c+d*x))/(a*d)}
	public void test02149() {
		check("Integrate(1/(Sqrt(Sin(c+d*x))*Sqrt(a+b*Sin(c+d*x))), x)",
				"(-2*Sqrt(a+b)*Sqrt((a*(1-Csc(c+d*x)))/(a+b))*Sqrt((a*(1+Csc(c+d*x)))/(a-b))*EllipticF(ArcSin(Sqrt(a+b*Sin(c+d*x))/(Sqrt(a+b)*Sqrt(Sin(c+d*x)))), -((a+b)/(a-b)))*Tan(c+d*x))/(a*d)");

	}

	// {(a+a*Sin(e+f*x))*(c-c*Sin(e+f*x)), x, 1, (a*c*x)/2+(a*c*Cos(e+f*x)*Sin(e+f*x))/(2*f)}
	public void test02150() {
		check("Integrate((a+a*Sin(e+f*x))*(c-c*Sin(e+f*x)), x)", "(a*c*x)/2+(a*c*Cos(e+f*x)*Sin(e+f*x))/(2*f)");

	}

	// {Sqrt(a+a*Sin(e+f*x))*(c-c*Sin(e+f*x))^(7/2), x, 1, -(a*Cos(e+f*x)*(c-c*Sin(e +
	// f*x))^(7/2))/(4*f*Sqrt(a+a*Sin(e+f*x)))}
	public void test02151() {
		check("Integrate(Sqrt(a+a*Sin(e+f*x))*(c-c*Sin(e+f*x))^(7/2), x)",
				"-(a*Cos(e+f*x)*(c-c*Sin(e+f*x))^(7/2))/(4*f*Sqrt(a+a*Sin(e+f*x)))");

	}

	// {Sqrt(a+a*Sin(e+f*x))*(c-c*Sin(e+f*x))^(5/2), x, 1, -(a*Cos(e+f*x)*(c-c*Sin(e +
	// f*x))^(5/2))/(3*f*Sqrt(a+a*Sin(e+f*x)))}
	public void test02152() {
		check("Integrate(Sqrt(a+a*Sin(e+f*x))*(c-c*Sin(e+f*x))^(5/2), x)",
				"-(a*Cos(e+f*x)*(c-c*Sin(e+f*x))^(5/2))/(3*f*Sqrt(a+a*Sin(e+f*x)))");

	}

	// {Sqrt(a+a*Sin(e+f*x))*(c-c*Sin(e+f*x))^(3/2), x, 1, -(a*Cos(e+f*x)*(c-c*Sin(e +
	// f*x))^(3/2))/(2*f*Sqrt(a+a*Sin(e+f*x)))}
	public void test02153() {
		check("Integrate(Sqrt(a+a*Sin(e+f*x))*(c-c*Sin(e+f*x))^(3/2), x)",
				"-(a*Cos(e+f*x)*(c-c*Sin(e+f*x))^(3/2))/(2*f*Sqrt(a+a*Sin(e+f*x)))");

	}

	// {Sqrt(a+a*Sin(e+f*x))*Sqrt(c-c*Sin(e+f*x)), x, 1, -((a*Cos(e+f*x)*Sqrt(c-c*Sin(e+f*x)))/(f*Sqrt(a +
	// a*Sin(e+f*x))))}
	public void test02154() {
		check("Integrate(Sqrt(a+a*Sin(e+f*x))*Sqrt(c-c*Sin(e+f*x)), x)",
				"-((a*Cos(e+f*x)*Sqrt(c-c*Sin(e+f*x)))/(f*Sqrt(a+a*Sin(e+f*x))))");

	}

	// {Sqrt(a+a*Sin(e+f*x))/(c-c*Sin(e+f*x))^(3/2), x, 1, (a*Cos(e+f*x))/(f*Sqrt(a+a*Sin(e+f*x))*(c -
	// c*Sin(e+f*x))^(3/2))}
	public void test02155() {
		check("Integrate(Sqrt(a+a*Sin(e+f*x))/(c-c*Sin(e+f*x))^(3/2), x)",
				"(a*Cos(e+f*x))/(f*Sqrt(a+a*Sin(e+f*x))*(c-c*Sin(e+f*x))^(3/2))");

	}

	// {Sqrt(a+a*Sin(e+f*x))/(c-c*Sin(e+f*x))^(5/2), x, 1, (a*Cos(e+f*x))/(2*f*Sqrt(a+a*Sin(e+f*x))*(c -
	// c*Sin(e+f*x))^(5/2))}
	public void test02156() {
		check("Integrate(Sqrt(a+a*Sin(e+f*x))/(c-c*Sin(e+f*x))^(5/2), x)",
				"(a*Cos(e+f*x))/(2*f*Sqrt(a+a*Sin(e+f*x))*(c-c*Sin(e+f*x))^(5/2))");

	}

	// {Sqrt(a+a*Sin(e+f*x))/(c-c*Sin(e+f*x))^(7/2), x, 1, (a*Cos(e+f*x))/(3*f*Sqrt(a+a*Sin(e+f*x))*(c -
	// c*Sin(e+f*x))^(7/2))}
	public void test02157() {
		check("Integrate(Sqrt(a+a*Sin(e+f*x))/(c-c*Sin(e+f*x))^(7/2), x)",
				"(a*Cos(e+f*x))/(3*f*Sqrt(a+a*Sin(e+f*x))*(c-c*Sin(e+f*x))^(7/2))");

	}

	// {(a+a*Sin(e+f*x))^(3/2)*Sqrt(c-c*Sin(e+f*x)), x, 1, (c*Cos(e+f*x)*(a+a*Sin(e +
	// f*x))^(3/2))/(2*f*Sqrt(c-c*Sin(e+f*x)))}
	public void test02158() {
		check("Integrate((a+a*Sin(e+f*x))^(3/2)*Sqrt(c-c*Sin(e+f*x)), x)",
				"(c*Cos(e+f*x)*(a+a*Sin(e+f*x))^(3/2))/(2*f*Sqrt(c-c*Sin(e+f*x)))");

	}

	// {(a+a*Sin(e+f*x))^(3/2)/(c-c*Sin(e+f*x))^(5/2), x, 1, (Cos(e+f*x)*(a+a*Sin(e+f*x))^(3/2))/(4*f*(c -
	// c*Sin(e+f*x))^(5/2))}
	public void test02159() {
		check("Integrate((a+a*Sin(e+f*x))^(3/2)/(c-c*Sin(e+f*x))^(5/2), x)",
				"(Cos(e+f*x)*(a+a*Sin(e+f*x))^(3/2))/(4*f*(c-c*Sin(e+f*x))^(5/2))");

	}

	// {(a+a*Sin(e+f*x))^(5/2)*Sqrt(c-c*Sin(e+f*x)), x, 1, (c*Cos(e+f*x)*(a+a*Sin(e +
	// f*x))^(5/2))/(3*f*Sqrt(c-c*Sin(e+f*x)))}
	public void test02160() {
		check("Integrate((a+a*Sin(e+f*x))^(5/2)*Sqrt(c-c*Sin(e+f*x)), x)",
				"(c*Cos(e+f*x)*(a+a*Sin(e+f*x))^(5/2))/(3*f*Sqrt(c-c*Sin(e+f*x)))");

	}

	// {(a+a*Sin(e+f*x))^(5/2)/(c-c*Sin(e+f*x))^(7/2), x, 1, (Cos(e+f*x)*(a+a*Sin(e+f*x))^(5/2))/(6*f*(c -
	// c*Sin(e+f*x))^(7/2))}
	public void test02161() {
		check("Integrate((a+a*Sin(e+f*x))^(5/2)/(c-c*Sin(e+f*x))^(7/2), x)",
				"(Cos(e+f*x)*(a+a*Sin(e+f*x))^(5/2))/(6*f*(c-c*Sin(e+f*x))^(7/2))");

	}

	// {(a+a*Sin(e+f*x))^(7/2)*Sqrt(c-c*Sin(e+f*x)), x, 1, (c*Cos(e+f*x)*(a+a*Sin(e +
	// f*x))^(7/2))/(4*f*Sqrt(c-c*Sin(e+f*x)))}
	public void test02162() {
		check("Integrate((a+a*Sin(e+f*x))^(7/2)*Sqrt(c-c*Sin(e+f*x)), x)",
				"(c*Cos(e+f*x)*(a+a*Sin(e+f*x))^(7/2))/(4*f*Sqrt(c-c*Sin(e+f*x)))");

	}

	// {(a+a*Sin(e+f*x))^(7/2)/(c-c*Sin(e+f*x))^(9/2), x, 1, (Cos(e+f*x)*(a+a*Sin(e+f*x))^(7/2))/(8*f*(c -
	// c*Sin(e+f*x))^(9/2))}
	public void test02163() {
		check("Integrate((a+a*Sin(e+f*x))^(7/2)/(c-c*Sin(e+f*x))^(9/2), x)",
				"(Cos(e+f*x)*(a+a*Sin(e+f*x))^(7/2))/(8*f*(c-c*Sin(e+f*x))^(9/2))");

	}

	// {Sqrt(c-c*Sin(e+f*x))/(a+a*Sin(e+f*x))^(3/2), x, 1, -((c*Cos(e+f*x))/(f*(a+a*Sin(e +
	// f*x))^(3/2)*Sqrt(c-c*Sin(e+f*x))))}
	public void test02164() {
		check("Integrate(Sqrt(c-c*Sin(e+f*x))/(a+a*Sin(e+f*x))^(3/2), x)",
				"-((c*Cos(e+f*x))/(f*(a+a*Sin(e+f*x))^(3/2)*Sqrt(c-c*Sin(e+f*x))))");

	}

	// {(c-c*Sin(e+f*x))^(3/2)/(a+a*Sin(e+f*x))^(5/2), x, 1, -(Cos(e+f*x)*(c-c*Sin(e+f*x))^(3/2))/(4*f*(a
	// +a*Sin(e+f*x))^(5/2))}
	public void test02165() {
		check("Integrate((c-c*Sin(e+f*x))^(3/2)/(a+a*Sin(e+f*x))^(5/2), x)",
				"-(Cos(e+f*x)*(c-c*Sin(e+f*x))^(3/2))/(4*f*(a+a*Sin(e+f*x))^(5/2))");

	}

	// {Sqrt(c-c*Sin(e+f*x))/(a+a*Sin(e+f*x))^(5/2), x, 1, -(c*Cos(e+f*x))/(2*f*(a+a*Sin(e +
	// f*x))^(5/2)*Sqrt(c-c*Sin(e+f*x)))}
	public void test02166() {
		check("Integrate(Sqrt(c-c*Sin(e+f*x))/(a+a*Sin(e+f*x))^(5/2), x)",
				"-(c*Cos(e+f*x))/(2*f*(a+a*Sin(e+f*x))^(5/2)*Sqrt(c-c*Sin(e+f*x)))");

	}

	// {(a+a*Sin(e+f*x))^m*Sqrt(c-c*Sin(e+f*x)), x, 1, (2*c*Cos(e+f*x)*(a+a*Sin(e+f*x))^m)/(f*(1 +
	// 2*m)*Sqrt(c-c*Sin(e+f*x)))}
	public void test02167() {
		check("Integrate((a+a*Sin(e+f*x))^m*Sqrt(c-c*Sin(e+f*x)), x)",
				"(2*c*Cos(e+f*x)*(a+a*Sin(e+f*x))^m)/(f*(1+2*m)*Sqrt(c-c*Sin(e+f*x)))");

	}

	// {(a+a*Sin(e+f*x))^m*(c-c*Sin(e+f*x))^(-1-m), x, 1, (Cos(e+f*x)*(a+a*Sin(e+f*x))^m*(c-c*Sin(e +
	// f*x))^(-1-m))/(f*(1+2*m))}
	public void test02168() {
		check("Integrate((a+a*Sin(e+f*x))^m*(c-c*Sin(e+f*x))^(-1-m), x)",
				"(Cos(e+f*x)*(a+a*Sin(e+f*x))^m*(c-c*Sin(e+f*x))^(-1-m))/(f*(1+2*m))");

	}

	// {(a+a*Sin(e+f*x))*(c+d*Sin(e+f*x)), x, 1, (a*(2*c+d)*x)/2-(a*(c+d)*Cos(e+f*x))/f-(a*d*Cos(e +
	// f*x)*Sin(e+f*x))/(2*f)}
	public void test02169() {
		check("Integrate((a+a*Sin(e+f*x))*(c+d*Sin(e+f*x)), x)",
				"(a*(2*c+d)*x)/2-(a*(c+d)*Cos(e+f*x))/f-(a*d*Cos(e+f*x)*Sin(e+f*x))/(2*f)");

	}

	// {(a+a*Sin(e+f*x))^2, x, 1, (3*a^2*x)/2-(2*a^2*Cos(e+f*x))/f-(a^2*Cos(e+f*x)*Sin(e+f*x))/(2*f)}
	public void test02170() {
		check("Integrate((a+a*Sin(e+f*x))^2, x)", "(3*a^2*x)/2-(2*a^2*Cos(e+f*x))/f-(a^2*Cos(e+f*x)*Sin(e+f*x))/(2*f)");

	}

	// {(a+a*Sin(e+f*x))^(-1), x, 1, -(Cos(e+f*x)/(f*(a+a*Sin(e+f*x))))}
	public void test02171() {
		check("Integrate((a+a*Sin(e+f*x))^(-1), x)", "-(Cos(e+f*x)/(f*(a+a*Sin(e+f*x))))");

	}

	// {Sqrt(a+a*Sin(e+f*x)), x, 1, (-2*a*Cos(e+f*x))/(f*Sqrt(a+a*Sin(e+f*x)))}
	public void test02172() {
		check("Integrate(Sqrt(a+a*Sin(e+f*x)), x)", "(-2*a*Cos(e+f*x))/(f*Sqrt(a+a*Sin(e+f*x)))");

	}

	// {Sqrt(a+a*Sin(e+f*x))/(c+d*Sin(e+f*x))^(3/2), x, 1, (-2*a*Cos(e+f*x))/((c+d)*f*Sqrt(a+a*Sin(e +
	// f*x))*Sqrt(c+d*Sin(e+f*x)))}
	public void test02173() {
		check("Integrate(Sqrt(a+a*Sin(e+f*x))/(c+d*Sin(e+f*x))^(3/2), x)",
				"(-2*a*Cos(e+f*x))/((c+d)*f*Sqrt(a+a*Sin(e+f*x))*Sqrt(c+d*Sin(e+f*x)))");

	}

	// {(3-3*Sin(e+f*x))^(-1-m)*(1+Sin(e+f*x))^m, x, 1, (Cos(e+f*x)*(3-3*Sin(e+f*x))^(-1-m)*(1+Sin(e
	// +f*x))^m)/(f*(1+2*m))}
	public void test02174() {
		check("Integrate((3-3*Sin(e+f*x))^(-1-m)*(1+Sin(e+f*x))^m, x)",
				"(Cos(e+f*x)*(3-3*Sin(e+f*x))^(-1-m)*(1+Sin(e+f*x))^m)/(f*(1+2*m))");

	}

	// {(3-3*Sin(e+f*x))^(-1-m)*(a+a*Sin(e+f*x))^m, x, 1, (Cos(e+f*x)*(3-3*Sin(e+f*x))^(-1-m)*(a +
	// a*Sin(e+f*x))^m)/(f*(1+2*m))}
	public void test02175() {
		check("Integrate((3-3*Sin(e+f*x))^(-1-m)*(a+a*Sin(e+f*x))^m, x)",
				"(Cos(e+f*x)*(3-3*Sin(e+f*x))^(-1-m)*(a+a*Sin(e+f*x))^m)/(f*(1+2*m))");

	}

	// {(-3+3*Sin(e+f*x))^(-1-m)*(a+a*Sin(e+f*x))^m, x, 1, (Cos(e+f*x)*(-3+3*Sin(e+f*x))^(-1-m)*(a +
	// a*Sin(e+f*x))^m)/(f*(1+2*m))}
	public void test02176() {
		check("Integrate((-3+3*Sin(e+f*x))^(-1-m)*(a+a*Sin(e+f*x))^m, x)",
				"(Cos(e+f*x)*(-3+3*Sin(e+f*x))^(-1-m)*(a+a*Sin(e+f*x))^m)/(f*(1+2*m))");

	}

	// {(a+b*Sin(e+f*x))*(c+d*Sin(e+f*x)), x, 1, ((2*a*c+b*d)*x)/2-((b*c+a*d)*Cos(e+f*x))/f-(b*d*Cos(e
	// +f*x)*Sin(e+f*x))/(2*f)}
	public void test02177() {
		check("Integrate((a+b*Sin(e+f*x))*(c+d*Sin(e+f*x)), x)",
				"((2*a*c+b*d)*x)/2-((b*c+a*d)*Cos(e+f*x))/f-(b*d*Cos(e+f*x)*Sin(e+f*x))/(2*f)");

	}

	// {(a+b*Sin(e+f*x))^2, x, 1, ((2*a^2+b^2)*x)/2-(2*a*b*Cos(e+f*x))/f-(b^2*Cos(e+f*x)*Sin(e +
	// f*x))/(2*f)}
	public void test02178() {
		check("Integrate((a+b*Sin(e+f*x))^2, x)",
				"((2*a^2+b^2)*x)/2-(2*a*b*Cos(e+f*x))/f-(b^2*Cos(e+f*x)*Sin(e+f*x))/(2*f)");

	}

	// {Sqrt(a+b*Sin(e+f*x))/Sqrt(c+d*Sin(e+f*x)), x, 1, (2*Sqrt(c+d)*EllipticPi((b*(c+d))/((a+b)*d),
	// ArcSin((Sqrt(a+b)*Sqrt(c+d*Sin(e+f*x)))/(Sqrt(c+d)*Sqrt(a+b*Sin(e+f*x)))), ((a-b)*(c+d))/((a +
	// b)*(c-d)))*Sec(e+f*x)*Sqrt(-(((b*c-a*d)*(1-Sin(e+f*x)))/((c+d)*(a+b*Sin(e+f*x)))))*Sqrt(((b*c -
	// a*d)*(1+Sin(e+f*x)))/((c-d)*(a+b*Sin(e+f*x))))*(a+b*Sin(e+f*x)))/(Sqrt(a+b)*d*f)}
	public void test02179() {
		check("Integrate(Sqrt(a+b*Sin(e+f*x))/Sqrt(c+d*Sin(e+f*x)), x)",
				"(2*Sqrt(c+d)*EllipticPi((b*(c+d))/((a+b)*d), ArcSin((Sqrt(a+b)*Sqrt(c+d*Sin(e+f*x)))/(Sqrt(c+d)*Sqrt(a+b*Sin(e+f*x)))), ((a-b)*(c+d))/((a+b)*(c-d)))*Sec(e+f*x)*Sqrt(-(((b*c-a*d)*(1-Sin(e+f*x)))/((c+d)*(a+b*Sin(e+f*x)))))*Sqrt(((b*c-a*d)*(1+Sin(e+f*x)))/((c-d)*(a+b*Sin(e+f*x))))*(a+b*Sin(e+f*x)))/(Sqrt(a+b)*d*f)");

	}

	// {Sqrt(c+d*Sin(e+f*x))/Sqrt(a+b*Sin(e+f*x)), x, 1, (2*Sqrt(a+b)*EllipticPi(((a+b)*d)/(b*(c+d)),
	// ArcSin((Sqrt(c+d)*Sqrt(a+b*Sin(e+f*x)))/(Sqrt(a+b)*Sqrt(c+d*Sin(e+f*x)))), ((a+b)*(c-d))/((a -
	// b)*(c+d)))*Sec(e+f*x)*Sqrt(((b*c-a*d)*(1-Sin(e+f*x)))/((a+b)*(c+d*Sin(e+f*x))))*Sqrt(-(((b*c -
	// a*d)*(1+Sin(e+f*x)))/((a-b)*(c+d*Sin(e+f*x)))))*(c+d*Sin(e+f*x)))/(b*Sqrt(c+d)*f)}
	public void test02180() {
		check("Integrate(Sqrt(c+d*Sin(e+f*x))/Sqrt(a+b*Sin(e+f*x)), x)",
				"(2*Sqrt(a+b)*EllipticPi(((a+b)*d)/(b*(c+d)), ArcSin((Sqrt(c+d)*Sqrt(a+b*Sin(e+f*x)))/(Sqrt(a+b)*Sqrt(c+d*Sin(e+f*x)))), ((a+b)*(c-d))/((a-b)*(c+d)))*Sec(e+f*x)*Sqrt(((b*c-a*d)*(1-Sin(e+f*x)))/((a+b)*(c+d*Sin(e+f*x))))*Sqrt(-(((b*c-a*d)*(1+Sin(e+f*x)))/((a-b)*(c+d*Sin(e+f*x)))))*(c+d*Sin(e+f*x)))/(b*Sqrt(c+d)*f)");

	}

	// {1/(Sqrt(a+b*Sin(e+f*x))*Sqrt(c+d*Sin(e+f*x))), x, 1, (2*Sqrt(a+b)*EllipticF(ArcSin((Sqrt(c+d)*Sqrt(a
	// +b*Sin(e+f*x)))/(Sqrt(a+b)*Sqrt(c+d*Sin(e+f*x)))), ((a+b)*(c-d))/((a-b)*(c+d)))*Sec(e +
	// f*x)*Sqrt(((b*c-a*d)*(1-Sin(e+f*x)))/((a+b)*(c+d*Sin(e+f*x))))*Sqrt(-(((b*c-a*d)*(1+Sin(e +
	// f*x)))/((a-b)*(c+d*Sin(e+f*x)))))*(c+d*Sin(e+f*x)))/(Sqrt(c+d)*(b*c-a*d)*f)}
	public void test02181() {
		check("Integrate(1/(Sqrt(a+b*Sin(e+f*x))*Sqrt(c+d*Sin(e+f*x))), x)",
				"(2*Sqrt(a+b)*EllipticF(ArcSin((Sqrt(c+d)*Sqrt(a+b*Sin(e+f*x)))/(Sqrt(a+b)*Sqrt(c+d*Sin(e+f*x)))), ((a+b)*(c-d))/((a-b)*(c+d)))*Sec(e+f*x)*Sqrt(((b*c-a*d)*(1-Sin(e+f*x)))/((a+b)*(c+d*Sin(e+f*x))))*Sqrt(-(((b*c-a*d)*(1+Sin(e+f*x)))/((a-b)*(c+d*Sin(e+f*x)))))*(c+d*Sin(e+f*x)))/(Sqrt(c+d)*(b*c-a*d)*f)");

	}

	// {(c*(d*Sin(e+f*x))^p)^n*(a+b*Sin(e+f*x))^m, x, 1, ((c*(d*Sin(e+f*x))^p)^n*Unintegrable((d*Sin(e +
	// f*x))^(n*p)*(a+b*Sin(e+f*x))^m, x))/(d*Sin(e+f*x))^(n*p)}
	public void test02182() {
		check("Integrate((c*(d*Sin(e+f*x))^p)^n*(a+b*Sin(e+f*x))^m, x)",
				"((c*(d*Sin(e+f*x))^p)^n*Unintegrable((d*Sin(e+f*x))^(n*p)*(a+b*Sin(e+f*x))^m, x))/(d*Sin(e+f*x))^(n*p)");

	}

	// {(g*Cos(e+f*x))^(1-2*m)*(a+a*Sin(e+f*x))^m*(c-c*Sin(e+f*x))^n, x, 1, -((a*(g*Cos(e+f*x))^(2 -
	// 2*m)*(a+a*Sin(e+f*x))^(-1+m)*(c-c*Sin(e+f*x))^n)/(f*g*(1-m+n)))}
	public void test02183() {
		check("Integrate((g*Cos(e+f*x))^(1-2*m)*(a+a*Sin(e+f*x))^m*(c-c*Sin(e+f*x))^n, x)",
				"-((a*(g*Cos(e+f*x))^(2-2*m)*(a+a*Sin(e+f*x))^(-1+m)*(c-c*Sin(e+f*x))^n)/(f*g*(1-m+n)))");

	}

	// {(g*Cos(e+f*x))^(-1-m-n)*(a+a*Sin(e+f*x))^m*(c-c*Sin(e+f*x))^n, x, 1, ((g*Cos(e+f*x))^(-m-n)*(a
	// +a*Sin(e+f*x))^m*(c-c*Sin(e+f*x))^n)/(f*g*(m-n))}
	public void test02184() {
		check("Integrate((g*Cos(e+f*x))^(-1-m-n)*(a+a*Sin(e+f*x))^m*(c-c*Sin(e+f*x))^n, x)",
				"((g*Cos(e+f*x))^(-m-n)*(a+a*Sin(e+f*x))^m*(c-c*Sin(e+f*x))^n)/(f*g*(m-n))");

	}

	// {(g*Cos(e+f*x))^p*(a+a*Sin(e+f*x))^m*(A*m-A*(1+m+p)*Sin(e+f*x)), x, 1, (A*(g*Cos(e+f*x))^(1 +
	// p)*(a+a*Sin(e+f*x))^m)/(f*g)}
	public void test02185() {
		check("Integrate((g*Cos(e+f*x))^p*(a+a*Sin(e+f*x))^m*(A*m-A*(1+m+p)*Sin(e+f*x)), x)",
				"(A*(g*Cos(e+f*x))^(1+p)*(a+a*Sin(e+f*x))^m)/(f*g)");

	}

	// {(g*Cos(e+f*x))^p*(a-a*Sin(e+f*x))^m*(A*m+A*(1+m+p)*Sin(e+f*x)), x, 1, -((A*(g*Cos(e+f*x))^(1 +
	// p)*(a-a*Sin(e+f*x))^m)/(f*g))}
	public void test02186() {
		check("Integrate((g*Cos(e+f*x))^p*(a-a*Sin(e+f*x))^m*(A*m+A*(1+m+p)*Sin(e+f*x)), x)",
				"-((A*(g*Cos(e+f*x))^(1+p)*(a-a*Sin(e+f*x))^m)/(f*g))");

	}

	// {Sqrt(a+b*Sin(e+f*x))/(Sqrt(g*Sin(e+f*x))*(c+c*Sin(e+f*x))), x, 1, -((EllipticE(ArcSin(Cos(e+f*x)/(1
	// +Sin(e+f*x))), -((a-b)/(a+b)))*Sqrt(Sin(e+f*x)/(1+Sin(e+f*x)))*Sqrt(a+b*Sin(e +
	// f*x)))/(c*f*Sqrt(g*Sin(e+f*x))*Sqrt((a+b*Sin(e+f*x))/((a+b)*(1+Sin(e+f*x))))))}
	public void test02187() {
		check("Integrate(Sqrt(a+b*Sin(e+f*x))/(Sqrt(g*Sin(e+f*x))*(c+c*Sin(e+f*x))), x)",
				"-((EllipticE(ArcSin(Cos(e+f*x)/(1+Sin(e+f*x))), -((a-b)/(a+b)))*Sqrt(Sin(e+f*x)/(1+Sin(e+f*x)))*Sqrt(a+b*Sin(e+f*x)))/(c*f*Sqrt(g*Sin(e+f*x))*Sqrt((a+b*Sin(e+f*x))/((a+b)*(1+Sin(e+f*x))))))");

	}

	// {Sqrt(g*Sin(e+f*x))/(Sqrt(a+b*Sin(e+f*x))*(c+d*Sin(e+f*x))), x, 1, (2*Sqrt(-Cot(e+f*x)^2)*Sqrt((b +
	// a*Csc(e+f*x))/(a+b))*EllipticPi((2*c)/(c+d), ArcSin(Sqrt(1-Csc(e+f*x))/Sqrt(2)), (2*a)/(a +
	// b))*Sqrt(g*Sin(e+f*x))*Tan(e+f*x))/((c+d)*f*Sqrt(a+b*Sin(e+f*x)))}
	public void test02188() {
		check("Integrate(Sqrt(g*Sin(e+f*x))/(Sqrt(a+b*Sin(e+f*x))*(c+d*Sin(e+f*x))), x)",
				"(2*Sqrt(-Cot(e+f*x)^2)*Sqrt((b+a*Csc(e+f*x))/(a+b))*EllipticPi((2*c)/(c+d), ArcSin(Sqrt(1-Csc(e+f*x))/Sqrt(2)), (2*a)/(a+b))*Sqrt(g*Sin(e+f*x))*Tan(e+f*x))/((c+d)*f*Sqrt(a+b*Sin(e+f*x)))");

	}

	// {Sqrt(g*Sin(e+f*x))/((a+b*Sin(e+f*x))*Sqrt(c+d*Sin(e+f*x))), x, 1, (2*Sqrt(-Cot(e+f*x)^2)*Sqrt((d +
	// c*Csc(e+f*x))/(c+d))*EllipticPi((2*a)/(a+b), ArcSin(Sqrt(1-Csc(e+f*x))/Sqrt(2)), (2*c)/(c +
	// d))*Sqrt(g*Sin(e+f*x))*Tan(e+f*x))/((a+b)*f*Sqrt(c+d*Sin(e+f*x)))}
	public void test02189() {
		check("Integrate(Sqrt(g*Sin(e+f*x))/((a+b*Sin(e+f*x))*Sqrt(c+d*Sin(e+f*x))), x)",
				"(2*Sqrt(-Cot(e+f*x)^2)*Sqrt((d+c*Csc(e+f*x))/(c+d))*EllipticPi((2*a)/(a+b), ArcSin(Sqrt(1-Csc(e+f*x))/Sqrt(2)), (2*c)/(c+d))*Sqrt(g*Sin(e+f*x))*Tan(e+f*x))/((a+b)*f*Sqrt(c+d*Sin(e+f*x)))");

	}

	// {(Csc(e+f*x)*Sqrt(a+b*Sin(e+f*x)))/Sqrt(c+d*Sin(e+f*x)), x, 1, (-2*Sqrt(c+d)*EllipticPi((a*(c +
	// d))/((a+b)*c), ArcSin((Sqrt(a+b)*Sqrt(c+d*Sin(e+f*x)))/(Sqrt(c+d)*Sqrt(a+b*Sin(e+f*x)))), ((a -
	// b)*(c+d))/((a+b)*(c-d)))*Sec(e+f*x)*Sqrt(-(((b*c-a*d)*(1-Sin(e+f*x)))/((c+d)*(a+b*Sin(e +
	// f*x)))))*Sqrt(((b*c-a*d)*(1+Sin(e+f*x)))/((c-d)*(a+b*Sin(e+f*x))))*(a+b*Sin(e+f*x)))/(Sqrt(a +
	// b)*c*f)}
	public void test02190() {
		check("Integrate((Csc(e+f*x)*Sqrt(a+b*Sin(e+f*x)))/Sqrt(c+d*Sin(e+f*x)), x)",
				"(-2*Sqrt(c+d)*EllipticPi((a*(c+d))/((a+b)*c), ArcSin((Sqrt(a+b)*Sqrt(c+d*Sin(e+f*x)))/(Sqrt(c+d)*Sqrt(a+b*Sin(e+f*x)))), ((a-b)*(c+d))/((a+b)*(c-d)))*Sec(e+f*x)*Sqrt(-(((b*c-a*d)*(1-Sin(e+f*x)))/((c+d)*(a+b*Sin(e+f*x)))))*Sqrt(((b*c-a*d)*(1+Sin(e+f*x)))/((c-d)*(a+b*Sin(e+f*x))))*(a+b*Sin(e+f*x)))/(Sqrt(a+b)*c*f)");

	}

	// {Sin(c+d*x)^n*(a+a*Sin(c+d*x))^(-2-n)*(-1-n-(-2-n)*Sin(c+d*x)), x, 1, -((Cos(c+d*x)*Sin(c +
	// d*x)^(1+n)*(a+a*Sin(c+d*x))^(-2-n))/d)}
	public void test02191() {
		check("Integrate(Sin(c+d*x)^n*(a+a*Sin(c+d*x))^(-2-n)*(-1-n-(-2-n)*Sin(c+d*x)), x)",
				"-((Cos(c+d*x)*Sin(c+d*x)^(1+n)*(a+a*Sin(c+d*x))^(-2-n))/d)");

	}

	// {Sin(c+d*x)^(-2-m)*(a+a*Sin(c+d*x))^m*(1+m-m*Sin(c+d*x)), x, 1, -((Cos(c+d*x)*Sin(c+d*x)^(-1 -
	// m)*(a+a*Sin(c+d*x))^m)/d)}
	public void test02192() {
		check("Integrate(Sin(c+d*x)^(-2-m)*(a+a*Sin(c+d*x))^m*(1+m-m*Sin(c+d*x)), x)",
				"-((Cos(c+d*x)*Sin(c+d*x)^(-1-m)*(a+a*Sin(c+d*x))^m)/d)");

	}

	// {(a+a*Sin(e+f*x))^m*(c-c*Sin(e+f*x))^n*(B*(m-n)-B*(1+m+n)*Sin(e+f*x)), x, 1, (B*Cos(e+f*x)*(a
	// +a*Sin(e+f*x))^m*(c-c*Sin(e+f*x))^n)/f}
	public void test02193() {
		check("Integrate((a+a*Sin(e+f*x))^m*(c-c*Sin(e+f*x))^n*(B*(m-n)-B*(1+m+n)*Sin(e+f*x)), x)",
				"(B*Cos(e+f*x)*(a+a*Sin(e+f*x))^m*(c-c*Sin(e+f*x))^n)/f");

	}

	// {(a-a*Sin(e+f*x))^m*(c+c*Sin(e+f*x))^n*(B*(m-n)+B*(1+m+n)*Sin(e+f*x)), x, 1, -((B*Cos(e +
	// f*x)*(a-a*Sin(e+f*x))^m*(c+c*Sin(e+f*x))^n)/f)}
	public void test02194() {
		check("Integrate((a-a*Sin(e+f*x))^m*(c+c*Sin(e+f*x))^n*(B*(m-n)+B*(1+m+n)*Sin(e+f*x)), x)",
				"-((B*Cos(e+f*x)*(a-a*Sin(e+f*x))^m*(c+c*Sin(e+f*x))^n)/f)");

	}

	// {(a+a*Sin(e+f*x))*(A+B*Sin(e+f*x)), x, 1, (a*(2*A+B)*x)/2-(a*(A+B)*Cos(e+f*x))/f-(a*B*Cos(e +
	// f*x)*Sin(e+f*x))/(2*f)}
	public void test02195() {
		check("Integrate((a+a*Sin(e+f*x))*(A+B*Sin(e+f*x)), x)",
				"(a*(2*A+B)*x)/2-(a*(A+B)*Cos(e+f*x))/f-(a*B*Cos(e+f*x)*Sin(e+f*x))/(2*f)");

	}

	// {(a+a*Sin(e+f*x))^m*(c+d*Sin(e+f*x))^(-2-m)*(d-(c-d)*m+(c+(c-d)*m)*Sin(e+f*x)), x, 1,
	// -((Cos(e+f*x)*(a+a*Sin(e+f*x))^m*(c+d*Sin(e+f*x))^(-1-m))/f)}
	public void test02196() {
		check("Integrate((a+a*Sin(e+f*x))^m*(c+d*Sin(e+f*x))^(-2-m)*(d-(c-d)*m+(c+(c-d)*m)*Sin(e+f*x)), x)",
				"-((Cos(e+f*x)*(a+a*Sin(e+f*x))^m*(c+d*Sin(e+f*x))^(-1-m))/f)");

	}

	// {(a-a*Sin(e+f*x))^m*(c+d*Sin(e+f*x))^(-2-m)*(d+(c+d)*m+(c+(c+d)*m)*Sin(e+f*x)), x, 1,
	// -((Cos(e+f*x)*(a-a*Sin(e+f*x))^m*(c+d*Sin(e+f*x))^(-1-m))/f)}
	public void test02197() {
		check("Integrate((a-a*Sin(e+f*x))^m*(c+d*Sin(e+f*x))^(-2-m)*(d+(c+d)*m+(c+(c+d)*m)*Sin(e+f*x)), x)",
				"-((Cos(e+f*x)*(a-a*Sin(e+f*x))^m*(c+d*Sin(e+f*x))^(-1-m))/f)");

	}

	// {Sin(e+f*x)^m*(1+m-(2+m)*Sin(e+f*x)^2), x, 1, (Cos(e+f*x)*Sin(e+f*x)^(1+m))/f}
	public void test02198() {
		check("Integrate(Sin(e+f*x)^m*(1+m-(2+m)*Sin(e+f*x)^2), x)", "(Cos(e+f*x)*Sin(e+f*x)^(1+m))/f");

	}

	// {Sin(e+f*x)^5*(6-7*Sin(e+f*x)^2), x, 1, (Cos(e+f*x)*Sin(e+f*x)^6)/f}
	public void test02199() {
		check("Integrate(Sin(e+f*x)^5*(6-7*Sin(e+f*x)^2), x)", "(Cos(e+f*x)*Sin(e+f*x)^6)/f");

	}

	// {Sin(e+f*x)^4*(5-6*Sin(e+f*x)^2), x, 1, (Cos(e+f*x)*Sin(e+f*x)^5)/f}
	public void test02200() {
		check("Integrate(Sin(e+f*x)^4*(5-6*Sin(e+f*x)^2), x)", "(Cos(e+f*x)*Sin(e+f*x)^5)/f");

	}

	// {Sin(e+f*x)^3*(4-5*Sin(e+f*x)^2), x, 1, (Cos(e+f*x)*Sin(e+f*x)^4)/f}
	public void test02201() {
		check("Integrate(Sin(e+f*x)^3*(4-5*Sin(e+f*x)^2), x)", "(Cos(e+f*x)*Sin(e+f*x)^4)/f");

	}

	// {Sin(e+f*x)^2*(3-4*Sin(e+f*x)^2), x, 1, (Cos(e+f*x)*Sin(e+f*x)^3)/f}
	public void test02202() {
		check("Integrate(Sin(e+f*x)^2*(3-4*Sin(e+f*x)^2), x)", "(Cos(e+f*x)*Sin(e+f*x)^3)/f");

	}

	// {Sin(e+f*x)*(2-3*Sin(e+f*x)^2), x, 1, (Cos(e+f*x)*Sin(e+f*x)^2)/f}
	public void test02203() {
		check("Integrate(Sin(e+f*x)*(2-3*Sin(e+f*x)^2), x)", "(Cos(e+f*x)*Sin(e+f*x)^2)/f");

	}

	// {-Sin(e+f*x), x, 1, Cos(e+f*x)/f}
	public void test02204() {
		check("Integrate(-Sin(e+f*x), x)", "Cos(e+f*x)/f");

	}

	// {Csc(e+f*x)^3*(-2+Sin(e+f*x)^2), x, 1, (Cot(e+f*x)*Csc(e+f*x))/f}
	public void test02205() {
		check("Integrate(Csc(e+f*x)^3*(-2+Sin(e+f*x)^2), x)", "(Cot(e+f*x)*Csc(e+f*x))/f");

	}

	// {Csc(e+f*x)^4*(-3+2*Sin(e+f*x)^2), x, 1, (Cot(e+f*x)*Csc(e+f*x)^2)/f}
	public void test02206() {
		check("Integrate(Csc(e+f*x)^4*(-3+2*Sin(e+f*x)^2), x)", "(Cot(e+f*x)*Csc(e+f*x)^2)/f");

	}

	// {Csc(e+f*x)^5*(-4+3*Sin(e+f*x)^2), x, 1, (Cot(e+f*x)*Csc(e+f*x)^3)/f}
	public void test02207() {
		check("Integrate(Csc(e+f*x)^5*(-4+3*Sin(e+f*x)^2), x)", "(Cot(e+f*x)*Csc(e+f*x)^3)/f");

	}

	// {(a+b*Sin(x)^2)^2, x, 1, ((8*a^2+8*a*b+3*b^2)*x)/8-(b*(8*a+3*b)*Cos(x)*Sin(x))/8 -
	// (b^2*Cos(x)*Sin(x)^3)/4}
	public void test02208() {
		check("Integrate((a+b*Sin(x)^2)^2, x)",
				"((8*a^2+8*a*b+3*b^2)*x)/8-(b*(8*a+3*b)*Cos(x)*Sin(x))/8-(b^2*Cos(x)*Sin(x)^3)/4");

	}

	// {(a+b*Sin(e+f*x)^2)^2, x, 1, ((8*a^2+8*a*b+3*b^2)*x)/8-(b*(8*a+3*b)*Cos(e+f*x)*Sin(e+f*x))/(8*f)
	// -(b^2*Cos(e+f*x)*Sin(e+f*x)^3)/(4*f)}
	public void test02209() {
		check("Integrate((a+b*Sin(e+f*x)^2)^2, x)",
				"((8*a^2+8*a*b+3*b^2)*x)/8-(b*(8*a+3*b)*Cos(e+f*x)*Sin(e+f*x))/(8*f)-(b^2*Cos(e+f*x)*Sin(e+f*x)^3)/(4*f)");

	}

	// {Cos(a+b*x), x, 1, Sin(a+b*x)/b}
	public void test02210() {
		check("Integrate(Cos(a+b*x), x)", "Sin(a+b*x)/b");

	}

	// {Sqrt(Cos(a+b*x)), x, 1, (2*EllipticE((a+b*x)/2, 2))/b}
	public void test02211() {
		check("Integrate(Sqrt(Cos(a+b*x)), x)", "(2*EllipticE((a+b*x)/2, 2))/b");

	}

	// {1/Sqrt(Cos(a+b*x)), x, 1, (2*EllipticF((a+b*x)/2, 2))/b}
	public void test02212() {
		check("Integrate(1/Sqrt(Cos(a+b*x)), x)", "(2*EllipticF((a+b*x)/2, 2))/b");

	}

	// {Cos(a+b*x)^(4/3), x, 1, (-3*Cos(a+b*x)^(7/3)*Hypergeometric2F1(1/2, 7/6, 13/6, Cos(a+b*x)^2)*Sin(a +
	// b*x))/(7*b*Sqrt(Sin(a+b*x)^2))}
	public void test02213() {
		check("Integrate(Cos(a+b*x)^(4/3), x)",
				"(-3*Cos(a+b*x)^(7/3)*Hypergeometric2F1(1/2, 7/6, 13/6, Cos(a+b*x)^2)*Sin(a+b*x))/(7*b*Sqrt(Sin(a+b*x)^2))");

	}

	// {Cos(a+b*x)^(2/3), x, 1, (-3*Cos(a+b*x)^(5/3)*Hypergeometric2F1(1/2, 5/6, 11/6, Cos(a+b*x)^2)*Sin(a +
	// b*x))/(5*b*Sqrt(Sin(a+b*x)^2))}
	public void test02214() {
		check("Integrate(Cos(a+b*x)^(2/3), x)",
				"(-3*Cos(a+b*x)^(5/3)*Hypergeometric2F1(1/2, 5/6, 11/6, Cos(a+b*x)^2)*Sin(a+b*x))/(5*b*Sqrt(Sin(a+b*x)^2))");

	}

	// {Cos(a+b*x)^(1/3), x, 1, (-3*Cos(a+b*x)^(4/3)*Hypergeometric2F1(1/2, 2/3, 5/3, Cos(a+b*x)^2)*Sin(a +
	// b*x))/(4*b*Sqrt(Sin(a+b*x)^2))}
	public void test02215() {
		check("Integrate(Cos(a+b*x)^(1/3), x)",
				"(-3*Cos(a+b*x)^(4/3)*Hypergeometric2F1(1/2, 2/3, 5/3, Cos(a+b*x)^2)*Sin(a+b*x))/(4*b*Sqrt(Sin(a+b*x)^2))");

	}

	// {Cos(a+b*x)^(-1/3), x, 1, (-3*Cos(a+b*x)^(2/3)*Hypergeometric2F1(1/3, 1/2, 4/3, Cos(a+b*x)^2)*Sin(a +
	// b*x))/(2*b*Sqrt(Sin(a+b*x)^2))}
	public void test02216() {
		check("Integrate(Cos(a+b*x)^(-1/3), x)",
				"(-3*Cos(a+b*x)^(2/3)*Hypergeometric2F1(1/3, 1/2, 4/3, Cos(a+b*x)^2)*Sin(a+b*x))/(2*b*Sqrt(Sin(a+b*x)^2))");

	}

	// {Cos(a+b*x)^(-2/3), x, 1, (-3*Cos(a+b*x)^(1/3)*Hypergeometric2F1(1/6, 1/2, 7/6, Cos(a+b*x)^2)*Sin(a +
	// b*x))/(b*Sqrt(Sin(a+b*x)^2))}
	public void test02217() {
		check("Integrate(Cos(a+b*x)^(-2/3), x)",
				"(-3*Cos(a+b*x)^(1/3)*Hypergeometric2F1(1/6, 1/2, 7/6, Cos(a+b*x)^2)*Sin(a+b*x))/(b*Sqrt(Sin(a+b*x)^2))");

	}

	// {Cos(a+b*x)^(-4/3), x, 1, (3*Hypergeometric2F1(-1/6, 1/2, 5/6, Cos(a+b*x)^2)*Sin(a+b*x))/(b*Cos(a +
	// b*x)^(1/3)*Sqrt(Sin(a+b*x)^2))}
	public void test02218() {
		check("Integrate(Cos(a+b*x)^(-4/3), x)",
				"(3*Hypergeometric2F1(-1/6, 1/2, 5/6, Cos(a+b*x)^2)*Sin(a+b*x))/(b*Cos(a+b*x)^(1/3)*Sqrt(Sin(a+b*x)^2))");

	}

	// {(c*Cos(a+b*x))^(4/3), x, 1, (-3*(c*Cos(a+b*x))^(7/3)*Hypergeometric2F1(1/2, 7/6, 13/6, Cos(a+b*x)^2)*Sin(a
	// +b*x))/(7*b*c*Sqrt(Sin(a+b*x)^2))}
	public void test02219() {
		check("Integrate((c*Cos(a+b*x))^(4/3), x)",
				"(-3*(c*Cos(a+b*x))^(7/3)*Hypergeometric2F1(1/2, 7/6, 13/6, Cos(a+b*x)^2)*Sin(a+b*x))/(7*b*c*Sqrt(Sin(a+b*x)^2))");

	}

	// {(c*Cos(a+b*x))^(2/3), x, 1, (-3*(c*Cos(a+b*x))^(5/3)*Hypergeometric2F1(1/2, 5/6, 11/6, Cos(a+b*x)^2)*Sin(a
	// +b*x))/(5*b*c*Sqrt(Sin(a+b*x)^2))}
	public void test02220() {
		check("Integrate((c*Cos(a+b*x))^(2/3), x)",
				"(-3*(c*Cos(a+b*x))^(5/3)*Hypergeometric2F1(1/2, 5/6, 11/6, Cos(a+b*x)^2)*Sin(a+b*x))/(5*b*c*Sqrt(Sin(a+b*x)^2))");

	}

	// {(c*Cos(a+b*x))^(1/3), x, 1, (-3*(c*Cos(a+b*x))^(4/3)*Hypergeometric2F1(1/2, 2/3, 5/3, Cos(a+b*x)^2)*Sin(a
	// +b*x))/(4*b*c*Sqrt(Sin(a+b*x)^2))}
	public void test02221() {
		check("Integrate((c*Cos(a+b*x))^(1/3), x)",
				"(-3*(c*Cos(a+b*x))^(4/3)*Hypergeometric2F1(1/2, 2/3, 5/3, Cos(a+b*x)^2)*Sin(a+b*x))/(4*b*c*Sqrt(Sin(a+b*x)^2))");

	}

	// {(c*Cos(a+b*x))^(-1/3), x, 1, (-3*(c*Cos(a+b*x))^(2/3)*Hypergeometric2F1(1/3, 1/2, 4/3, Cos(a+b*x)^2)*Sin(a
	// +b*x))/(2*b*c*Sqrt(Sin(a+b*x)^2))}
	public void test02222() {
		check("Integrate((c*Cos(a+b*x))^(-1/3), x)",
				"(-3*(c*Cos(a+b*x))^(2/3)*Hypergeometric2F1(1/3, 1/2, 4/3, Cos(a+b*x)^2)*Sin(a+b*x))/(2*b*c*Sqrt(Sin(a+b*x)^2))");

	}

	// {(c*Cos(a+b*x))^(-2/3), x, 1, (-3*(c*Cos(a+b*x))^(1/3)*Hypergeometric2F1(1/6, 1/2, 7/6, Cos(a+b*x)^2)*Sin(a
	// +b*x))/(b*c*Sqrt(Sin(a+b*x)^2))}
	public void test02223() {
		check("Integrate((c*Cos(a+b*x))^(-2/3), x)",
				"(-3*(c*Cos(a+b*x))^(1/3)*Hypergeometric2F1(1/6, 1/2, 7/6, Cos(a+b*x)^2)*Sin(a+b*x))/(b*c*Sqrt(Sin(a+b*x)^2))");

	}

	// {(c*Cos(a+b*x))^(-4/3), x, 1, (3*Hypergeometric2F1(-1/6, 1/2, 5/6, Cos(a+b*x)^2)*Sin(a+b*x))/(b*c*(c*Cos(a
	// +b*x))^(1/3)*Sqrt(Sin(a+b*x)^2))}
	public void test02224() {
		check("Integrate((c*Cos(a+b*x))^(-4/3), x)",
				"(3*Hypergeometric2F1(-1/6, 1/2, 5/6, Cos(a+b*x)^2)*Sin(a+b*x))/(b*c*(c*Cos(a+b*x))^(1/3)*Sqrt(Sin(a+b*x)^2))");

	}

	// {Cos(a+b*x)^n, x, 1, -((Cos(a+b*x)^(1+n)*Hypergeometric2F1(1/2, (1+n)/2, (3+n)/2, Cos(a+b*x)^2)*Sin(a
	// +b*x))/(b*(1+n)*Sqrt(Sin(a+b*x)^2)))}
	public void test02225() {
		check("Integrate(Cos(a+b*x)^n, x)",
				"-((Cos(a+b*x)^(1+n)*Hypergeometric2F1(1/2, (1+n)/2, (3+n)/2, Cos(a+b*x)^2)*Sin(a+b*x))/(b*(1+n)*Sqrt(Sin(a+b*x)^2)))");

	}

	// {(c*Cos(a+b*x))^n, x, 1, -(((c*Cos(a+b*x))^(1+n)*Hypergeometric2F1(1/2, (1+n)/2, (3+n)/2, Cos(a +
	// b*x)^2)*Sin(a+b*x))/(b*c*(1+n)*Sqrt(Sin(a+b*x)^2)))}
	public void test02226() {
		check("Integrate((c*Cos(a+b*x))^n, x)",
				"-(((c*Cos(a+b*x))^(1+n)*Hypergeometric2F1(1/2, (1+n)/2, (3+n)/2, Cos(a+b*x)^2)*Sin(a+b*x))/(b*c*(1+n)*Sqrt(Sin(a+b*x)^2)))");

	}

	// {(b*Cos(c+d*x))^(1/3), x, 1, (-3*(b*Cos(c+d*x))^(4/3)*Hypergeometric2F1(1/2, 2/3, 5/3, Cos(c+d*x)^2)*Sin(c
	// +d*x))/(4*b*d*Sqrt(Sin(c+d*x)^2))}
	public void test02227() {
		check("Integrate((b*Cos(c+d*x))^(1/3), x)",
				"(-3*(b*Cos(c+d*x))^(4/3)*Hypergeometric2F1(1/2, 2/3, 5/3, Cos(c+d*x)^2)*Sin(c+d*x))/(4*b*d*Sqrt(Sin(c+d*x)^2))");

	}

	// {(b*Cos(c+d*x))^(2/3), x, 1, (-3*(b*Cos(c+d*x))^(5/3)*Hypergeometric2F1(1/2, 5/6, 11/6, Cos(c+d*x)^2)*Sin(c
	// +d*x))/(5*b*d*Sqrt(Sin(c+d*x)^2))}
	public void test02228() {
		check("Integrate((b*Cos(c+d*x))^(2/3), x)",
				"(-3*(b*Cos(c+d*x))^(5/3)*Hypergeometric2F1(1/2, 5/6, 11/6, Cos(c+d*x)^2)*Sin(c+d*x))/(5*b*d*Sqrt(Sin(c+d*x)^2))");

	}

	// {(b*Cos(c+d*x))^(4/3), x, 1, (-3*(b*Cos(c+d*x))^(7/3)*Hypergeometric2F1(1/2, 7/6, 13/6, Cos(c+d*x)^2)*Sin(c
	// +d*x))/(7*b*d*Sqrt(Sin(c+d*x)^2))}
	public void test02229() {
		check("Integrate((b*Cos(c+d*x))^(4/3), x)",
				"(-3*(b*Cos(c+d*x))^(7/3)*Hypergeometric2F1(1/2, 7/6, 13/6, Cos(c+d*x)^2)*Sin(c+d*x))/(7*b*d*Sqrt(Sin(c+d*x)^2))");

	}

	// {(b*Cos(c+d*x))^(-1/3), x, 1, (-3*(b*Cos(c+d*x))^(2/3)*Hypergeometric2F1(1/3, 1/2, 4/3, Cos(c+d*x)^2)*Sin(c
	// +d*x))/(2*b*d*Sqrt(Sin(c+d*x)^2))}
	public void test02230() {
		check("Integrate((b*Cos(c+d*x))^(-1/3), x)",
				"(-3*(b*Cos(c+d*x))^(2/3)*Hypergeometric2F1(1/3, 1/2, 4/3, Cos(c+d*x)^2)*Sin(c+d*x))/(2*b*d*Sqrt(Sin(c+d*x)^2))");

	}

	// {(b*Cos(c+d*x))^(-2/3), x, 1, (-3*(b*Cos(c+d*x))^(1/3)*Hypergeometric2F1(1/6, 1/2, 7/6, Cos(c+d*x)^2)*Sin(c
	// +d*x))/(b*d*Sqrt(Sin(c+d*x)^2))}
	public void test02231() {
		check("Integrate((b*Cos(c+d*x))^(-2/3), x)",
				"(-3*(b*Cos(c+d*x))^(1/3)*Hypergeometric2F1(1/6, 1/2, 7/6, Cos(c+d*x)^2)*Sin(c+d*x))/(b*d*Sqrt(Sin(c+d*x)^2))");

	}

	// {(b*Cos(c+d*x))^(-4/3), x, 1, (3*Hypergeometric2F1(-1/6, 1/2, 5/6, Cos(c+d*x)^2)*Sin(c+d*x))/(b*d*(b*Cos(c
	// +d*x))^(1/3)*Sqrt(Sin(c+d*x)^2))}
	public void test02232() {
		check("Integrate((b*Cos(c+d*x))^(-4/3), x)",
				"(3*Hypergeometric2F1(-1/6, 1/2, 5/6, Cos(c+d*x)^2)*Sin(c+d*x))/(b*d*(b*Cos(c+d*x))^(1/3)*Sqrt(Sin(c+d*x)^2))");

	}

	// {(b*Cos(c+d*x))^n, x, 1, -(((b*Cos(c+d*x))^(1+n)*Hypergeometric2F1(1/2, (1+n)/2, (3+n)/2, Cos(c +
	// d*x)^2)*Sin(c+d*x))/(b*d*(1+n)*Sqrt(Sin(c+d*x)^2)))}
	public void test02233() {
		check("Integrate((b*Cos(c+d*x))^n, x)",
				"-(((b*Cos(c+d*x))^(1+n)*Hypergeometric2F1(1/2, (1+n)/2, (3+n)/2, Cos(c+d*x)^2)*Sin(c+d*x))/(b*d*(1+n)*Sqrt(Sin(c+d*x)^2)))");

	}

	// {Sqrt(Cos(a+b*x)), x, 1, (2*EllipticE((a+b*x)/2, 2))/b}
	public void test02234() {
		check("Integrate(Sqrt(Cos(a+b*x)), x)", "(2*EllipticE((a+b*x)/2, 2))/b");

	}

	// {x*Cos(a+b*x)^(3/2), x, 1, (4*Cos(a+b*x)^(3/2))/(9*b^2)+(2*x*Sqrt(Cos(a+b*x))*Sin(a+b*x))/(3*b) +
	// Unintegrable(x/Sqrt(Cos(a+b*x)), x)/3}
	public void test02235() {
		check("Integrate(x*Cos(a+b*x)^(3/2), x)",
				"(4*Cos(a+b*x)^(3/2))/(9*b^2)+(2*x*Sqrt(Cos(a+b*x))*Sin(a+b*x))/(3*b)+Unintegrable(x/Sqrt(Cos(a+b*x)), x)/3");

	}

	// {Cos(x)^(3/2)/x^3, x, 1, -Cos(x)^(3/2)/(2*x^2)+(3*Sqrt(Cos(x))*Sin(x))/(4*x) +
	// (3*Unintegrable(1/(x*Sqrt(Cos(x))), x))/8-(9*Unintegrable(Cos(x)^(3/2)/x, x))/8}
	public void test02236() {
		check("Integrate(Cos(x)^(3/2)/x^3, x)",
				"-Cos(x)^(3/2)/(2*x^2)+(3*Sqrt(Cos(x))*Sin(x))/(4*x)+(3*Unintegrable(1/(x*Sqrt(Cos(x))), x))/8-(9*Unintegrable(Cos(x)^(3/2)/x, x))/8");

	}

	// {1/Sqrt(Cos(a+b*x)), x, 1, (2*EllipticF((a+b*x)/2, 2))/b}
	public void test02237() {
		check("Integrate(1/Sqrt(Cos(a+b*x)), x)", "(2*EllipticF((a+b*x)/2, 2))/b");

	}

	// {x/Cos(a+b*x)^(3/2), x, 1, (4*Sqrt(Cos(a+b*x)))/b^2+(2*x*Sin(a+b*x))/(b*Sqrt(Cos(a+b*x))) -
	// Unintegrable(x*Sqrt(Cos(a+b*x)), x)}
	public void test02238() {
		check("Integrate(x/Cos(a+b*x)^(3/2), x)",
				"(4*Sqrt(Cos(a+b*x)))/b^2+(2*x*Sin(a+b*x))/(b*Sqrt(Cos(a+b*x)))-Unintegrable(x*Sqrt(Cos(a+b*x)), x)");

	}

	// {Sqrt(a+a*Cos(c+d*x)), x, 1, (2*a*Sin(c+d*x))/(d*Sqrt(a+a*Cos(c+d*x)))}
	public void test02239() {
		check("Integrate(Sqrt(a+a*Cos(c+d*x)), x)", "(2*a*Sin(c+d*x))/(d*Sqrt(a+a*Cos(c+d*x)))");

	}

	// {Sqrt(a+a*Cos(x)), x, 1, (2*a*Sin(x))/Sqrt(a+a*Cos(x))}
	public void test02240() {
		check("Integrate(Sqrt(a+a*Cos(x)), x)", "(2*a*Sin(x))/Sqrt(a+a*Cos(x))");

	}

	// {Sqrt(a-a*Cos(x)), x, 1, (-2*a*Sin(x))/Sqrt(a-a*Cos(x))}
	public void test02241() {
		check("Integrate(Sqrt(a-a*Cos(x)), x)", "(-2*a*Sin(x))/Sqrt(a-a*Cos(x))");

	}

	// {Sqrt(a+a*Cos(c+d*x)), x, 1, (2*a*Sin(c+d*x))/(d*Sqrt(a+a*Cos(c+d*x)))}
	public void test02242() {
		check("Integrate(Sqrt(a+a*Cos(c+d*x)), x)", "(2*a*Sin(c+d*x))/(d*Sqrt(a+a*Cos(c+d*x)))");

	}

	// {(2+2*Cos(c+d*x))^n, x, 1, (2^(1/2+2*n)*Hypergeometric2F1(1/2, 1/2-n, 3/2, (1-Cos(c+d*x))/2)*Sin(c +
	// d*x))/(d*Sqrt(1+Cos(c+d*x)))}
	public void test02243() {
		check("Integrate((2+2*Cos(c+d*x))^n, x)",
				"(2^(1/2+2*n)*Hypergeometric2F1(1/2, 1/2-n, 3/2, (1-Cos(c+d*x))/2)*Sin(c+d*x))/(d*Sqrt(1+Cos(c+d*x)))");

	}

	// {(2-2*Cos(c+d*x))^n, x, 1, -((2^(1/2+2*n)*Hypergeometric2F1(1/2, 1/2-n, 3/2, (1+Cos(c+d*x))/2)*Sin(c
	// +d*x))/(d*Sqrt(1-Cos(c+d*x))))}
	public void test02244() {
		check("Integrate((2-2*Cos(c+d*x))^n, x)",
				"-((2^(1/2+2*n)*Hypergeometric2F1(1/2, 1/2-n, 3/2, (1+Cos(c+d*x))/2)*Sin(c+d*x))/(d*Sqrt(1-Cos(c+d*x))))");

	}

	// {(5+3*Cos(c+d*x))^(-1), x, 1, x/4-ArcTan(Sin(c+d*x)/(3+Cos(c+d*x)))/(2*d)}
	public void test02245() {
		check("Integrate((5+3*Cos(c+d*x))^(-1), x)", "x/4-ArcTan(Sin(c+d*x)/(3+Cos(c+d*x)))/(2*d)");

	}

	// {(5-3*Cos(c+d*x))^(-1), x, 1, x/4+ArcTan(Sin(c+d*x)/(3-Cos(c+d*x)))/(2*d)}
	public void test02246() {
		check("Integrate((5-3*Cos(c+d*x))^(-1), x)", "x/4+ArcTan(Sin(c+d*x)/(3-Cos(c+d*x)))/(2*d)");

	}

	// {(-5+3*Cos(c+d*x))^(-1), x, 1, -x/4-ArcTan(Sin(c+d*x)/(3-Cos(c+d*x)))/(2*d)}
	public void test02247() {
		check("Integrate((-5+3*Cos(c+d*x))^(-1), x)", "-x/4-ArcTan(Sin(c+d*x)/(3-Cos(c+d*x)))/(2*d)");

	}

	// {(-5-3*Cos(c+d*x))^(-1), x, 1, -x/4+ArcTan(Sin(c+d*x)/(3+Cos(c+d*x)))/(2*d)}
	public void test02248() {
		check("Integrate((-5-3*Cos(c+d*x))^(-1), x)", "-x/4+ArcTan(Sin(c+d*x)/(3+Cos(c+d*x)))/(2*d)");

	}

	// {(e*x)^(-1+2*n)*(b*Cos(c+d*x^n))^p, x, 1, ((e*x)^(2*n)*Unintegrable(x^(-1+2*n)*(b*Cos(c+d*x^n))^p,
	// x))/(e*x^(2*n))}
	public void test02249() {
		check("Integrate((e*x)^(-1+2*n)*(b*Cos(c+d*x^n))^p, x)",
				"((e*x)^(2*n)*Unintegrable(x^(-1+2*n)*(b*Cos(c+d*x^n))^p, x))/(e*x^(2*n))");

	}

	// {(e*x)^(-1+2*n)*(a+b*Cos(c+d*x^n))^p, x, 1, ((e*x)^(2*n)*Unintegrable(x^(-1+2*n)*(a+b*Cos(c +
	// d*x^n))^p, x))/(e*x^(2*n))}
	public void test02250() {
		check("Integrate((e*x)^(-1+2*n)*(a+b*Cos(c+d*x^n))^p, x)",
				"((e*x)^(2*n)*Unintegrable(x^(-1+2*n)*(a+b*Cos(c+d*x^n))^p, x))/(e*x^(2*n))");

	}

	// {Cos((a+b*x)^2), x, 1, (Sqrt(Pi/2)*FresnelC(Sqrt(2/Pi)*(a+b*x)))/b}
	public void test02251() {
		check("Integrate(Cos((a+b*x)^2), x)", "(Sqrt(Pi/2)*FresnelC(Sqrt(2/Pi)*(a+b*x)))/b");

	}

	// {(a+a*Cos(x))^(-1), x, 1, Sin(x)/(a+a*Cos(x))}
	public void test02252() {
		check("Integrate((a+a*Cos(x))^(-1), x)", "Sin(x)/(a+a*Cos(x))");

	}

	// {Sin(x)^2/(1+Cos(x))^3, x, 1, Sin(x)^3/(3*(1+Cos(x))^3)}
	public void test02253() {
		check("Integrate(Sin(x)^2/(1+Cos(x))^3, x)", "Sin(x)^3/(3*(1+Cos(x))^3)");

	}

	// {Sin(x)^2/(1-Cos(x))^3, x, 1, -Sin(x)^3/(3*(1-Cos(x))^3)}
	public void test02254() {
		check("Integrate(Sin(x)^2/(1-Cos(x))^3, x)", "-Sin(x)^3/(3*(1-Cos(x))^3)");

	}

	// {(a+b*Cos(e+f*x))^m*(g*Tan(e+f*x))^p, x, 1, (g*Cot(e+f*x))^p*(g*Tan(e+f*x))^p*Unintegrable((a+b*Cos(e
	// +f*x))^m/(g*Cot(e+f*x))^p, x)}
	public void test02255() {
		check("Integrate((a+b*Cos(e+f*x))^m*(g*Tan(e+f*x))^p, x)",
				"(g*Cot(e+f*x))^p*(g*Tan(e+f*x))^p*Unintegrable((a+b*Cos(e+f*x))^m/(g*Cot(e+f*x))^p, x)");

	}

	// {Cos(c+d*x)*(a+a*Cos(c+d*x)), x, 1, (a*x)/2+(a*Sin(c+d*x))/d+(a*Cos(c+d*x)*Sin(c+d*x))/(2*d)}
	public void test02256() {
		check("Integrate(Cos(c+d*x)*(a+a*Cos(c+d*x)), x)", "(a*x)/2+(a*Sin(c+d*x))/d+(a*Cos(c+d*x)*Sin(c+d*x))/(2*d)");

	}

	// {(a+a*Cos(c+d*x))^2, x, 1, (3*a^2*x)/2+(2*a^2*Sin(c+d*x))/d+(a^2*Cos(c+d*x)*Sin(c+d*x))/(2*d)}
	public void test02257() {
		check("Integrate((a+a*Cos(c+d*x))^2, x)", "(3*a^2*x)/2+(2*a^2*Sin(c+d*x))/d+(a^2*Cos(c+d*x)*Sin(c+d*x))/(2*d)");

	}

	// {(a+a*Cos(c+d*x))^(-1), x, 1, Sin(c+d*x)/(d*(a+a*Cos(c+d*x)))}
	public void test02258() {
		check("Integrate((a+a*Cos(c+d*x))^(-1), x)", "Sin(c+d*x)/(d*(a+a*Cos(c+d*x)))");

	}

	// {Sqrt(a+a*Cos(c+d*x)), x, 1, (2*a*Sin(c+d*x))/(d*Sqrt(a+a*Cos(c+d*x)))}
	public void test02259() {
		check("Integrate(Sqrt(a+a*Cos(c+d*x)), x)", "(2*a*Sin(c+d*x))/(d*Sqrt(a+a*Cos(c+d*x)))");

	}

	// {Sqrt(a+a*Cos(c+d*x))/Cos(c+d*x)^(3/2), x, 1, (2*a*Sin(c+d*x))/(d*Sqrt(Cos(c+d*x))*Sqrt(a+a*Cos(c +
	// d*x)))}
	public void test02260() {
		check("Integrate(Sqrt(a+a*Cos(c+d*x))/Cos(c+d*x)^(3/2), x)",
				"(2*a*Sin(c+d*x))/(d*Sqrt(Cos(c+d*x))*Sqrt(a+a*Cos(c+d*x)))");

	}

	// {Sqrt(a-a*Cos(c+d*x))/Cos(c+d*x)^(3/2), x, 1, (2*a*Sin(c+d*x))/(d*Sqrt(Cos(c+d*x))*Sqrt(a-a*Cos(c +
	// d*x)))}
	public void test02261() {
		check("Integrate(Sqrt(a-a*Cos(c+d*x))/Cos(c+d*x)^(3/2), x)",
				"(2*a*Sin(c+d*x))/(d*Sqrt(Cos(c+d*x))*Sqrt(a-a*Cos(c+d*x)))");

	}

	// {Sqrt(1-Cos(c+d*x))/Cos(c+d*x)^(3/2), x, 1, (2*Sin(c+d*x))/(d*Sqrt(1-Cos(c+d*x))*Sqrt(Cos(c+d*x)))}
	public void test02262() {
		check("Integrate(Sqrt(1-Cos(c+d*x))/Cos(c+d*x)^(3/2), x)",
				"(2*Sin(c+d*x))/(d*Sqrt(1-Cos(c+d*x))*Sqrt(Cos(c+d*x)))");

	}

	// {Cos(c+d*x)*(a+b*Cos(c+d*x)), x, 1, (b*x)/2+(a*Sin(c+d*x))/d+(b*Cos(c+d*x)*Sin(c+d*x))/(2*d)}
	public void test02263() {
		check("Integrate(Cos(c+d*x)*(a+b*Cos(c+d*x)), x)", "(b*x)/2+(a*Sin(c+d*x))/d+(b*Cos(c+d*x)*Sin(c+d*x))/(2*d)");

	}

	// {(a+b*Cos(c+d*x))^2, x, 1, ((2*a^2+b^2)*x)/2+(2*a*b*Sin(c+d*x))/d+(b^2*Cos(c+d*x)*Sin(c +
	// d*x))/(2*d)}
	public void test02264() {
		check("Integrate((a+b*Cos(c+d*x))^2, x)",
				"((2*a^2+b^2)*x)/2+(2*a*b*Sin(c+d*x))/d+(b^2*Cos(c+d*x)*Sin(c+d*x))/(2*d)");

	}

	// {Sqrt(3+4*Cos(c+d*x)), x, 1, (2*Sqrt(7)*EllipticE((c+d*x)/2, 8/7))/d}
	public void test02265() {
		check("Integrate(Sqrt(3+4*Cos(c+d*x)), x)", "(2*Sqrt(7)*EllipticE((c+d*x)/2, 8/7))/d");

	}

	// {Sqrt(3-4*Cos(c+d*x)), x, 1, (2*Sqrt(7)*EllipticE((c+Pi+d*x)/2, 8/7))/d}
	public void test02266() {
		check("Integrate(Sqrt(3-4*Cos(c+d*x)), x)", "(2*Sqrt(7)*EllipticE((c+Pi+d*x)/2, 8/7))/d");

	}

	// {1/Sqrt(3+4*Cos(c+d*x)), x, 1, (2*EllipticF((c+d*x)/2, 8/7))/(Sqrt(7)*d)}
	public void test02267() {
		check("Integrate(1/Sqrt(3+4*Cos(c+d*x)), x)", "(2*EllipticF((c+d*x)/2, 8/7))/(Sqrt(7)*d)");

	}

	// {Sec(c+d*x)/Sqrt(3+4*Cos(c+d*x)), x, 1, (2*EllipticPi(2, (c+d*x)/2, 8/7))/(Sqrt(7)*d)}
	public void test02268() {
		check("Integrate(Sec(c+d*x)/Sqrt(3+4*Cos(c+d*x)), x)", "(2*EllipticPi(2, (c+d*x)/2, 8/7))/(Sqrt(7)*d)");

	}

	// {1/Sqrt(3-4*Cos(c+d*x)), x, 1, (2*EllipticF((c+Pi+d*x)/2, 8/7))/(Sqrt(7)*d)}
	public void test02269() {
		check("Integrate(1/Sqrt(3-4*Cos(c+d*x)), x)", "(2*EllipticF((c+Pi+d*x)/2, 8/7))/(Sqrt(7)*d)");

	}

	// {Sec(c+d*x)/Sqrt(3-4*Cos(c+d*x)), x, 1, (-2*EllipticPi(2, (c+Pi+d*x)/2, 8/7))/(Sqrt(7)*d)}
	public void test02270() {
		check("Integrate(Sec(c+d*x)/Sqrt(3-4*Cos(c+d*x)), x)", "(-2*EllipticPi(2, (c+Pi+d*x)/2, 8/7))/(Sqrt(7)*d)");

	}

	// {1/(Sqrt(Cos(c+d*x))*(a+b*Cos(c+d*x))), x, 1, (2*EllipticPi((2*b)/(a+b), (c+d*x)/2, 2))/((a+b)*d)}
	public void test02271() {
		check("Integrate(1/(Sqrt(Cos(c+d*x))*(a+b*Cos(c+d*x))), x)",
				"(2*EllipticPi((2*b)/(a+b), (c+d*x)/2, 2))/((a+b)*d)");

	}

	// {Sqrt(a+b*Cos(c+d*x))/Sqrt(Cos(c+d*x)), x, 1, (-2*Sqrt((a*(1-Cos(c+d*x)))/(a+b*Cos(c +
	// d*x)))*Sqrt((a*(1+Cos(c+d*x)))/(a+b*Cos(c+d*x)))*(a+b*Cos(c+d*x))*Csc(c+d*x)*EllipticPi(b/(a+b),
	// ArcSin((Sqrt(a+b)*Sqrt(Cos(c+d*x)))/Sqrt(a+b*Cos(c+d*x))), -((a-b)/(a+b))))/(Sqrt(a+b)*d)}
	public void test02272() {
		check("Integrate(Sqrt(a+b*Cos(c+d*x))/Sqrt(Cos(c+d*x)), x)",
				"(-2*Sqrt((a*(1-Cos(c+d*x)))/(a+b*Cos(c+d*x)))*Sqrt((a*(1+Cos(c+d*x)))/(a+b*Cos(c+d*x)))*(a+b*Cos(c+d*x))*Csc(c+d*x)*EllipticPi(b/(a+b), ArcSin((Sqrt(a+b)*Sqrt(Cos(c+d*x)))/Sqrt(a+b*Cos(c+d*x))), -((a-b)/(a+b))))/(Sqrt(a+b)*d)");

	}

	// {Sqrt(Cos(c+d*x))/Sqrt(a+b*Cos(c+d*x)), x, 1, (-2*Sqrt(a+b)*Cot(c+d*x)*EllipticPi((a+b)/b,
	// ArcSin(Sqrt(a+b*Cos(c+d*x))/(Sqrt(a+b)*Sqrt(Cos(c+d*x)))), -((a+b)/(a-b)))*Sqrt((a*(1-Sec(c +
	// d*x)))/(a+b))*Sqrt((a*(1+Sec(c+d*x)))/(a-b)))/(b*d)}
	public void test02273() {
		check("Integrate(Sqrt(Cos(c+d*x))/Sqrt(a+b*Cos(c+d*x)), x)",
				"(-2*Sqrt(a+b)*Cot(c+d*x)*EllipticPi((a+b)/b, ArcSin(Sqrt(a+b*Cos(c+d*x))/(Sqrt(a+b)*Sqrt(Cos(c+d*x)))), -((a+b)/(a-b)))*Sqrt((a*(1-Sec(c+d*x)))/(a+b))*Sqrt((a*(1+Sec(c+d*x)))/(a-b)))/(b*d)");

	}

	// {1/(Sqrt(Cos(c+d*x))*Sqrt(a+b*Cos(c+d*x))), x, 1, (2*Sqrt(a+b)*Cot(c+d*x)*EllipticF(ArcSin(Sqrt(a +
	// b*Cos(c+d*x))/(Sqrt(a+b)*Sqrt(Cos(c+d*x)))), -((a+b)/(a-b)))*Sqrt((a*(1-Sec(c+d*x)))/(a +
	// b))*Sqrt((a*(1+Sec(c+d*x)))/(a-b)))/(a*d)}
	public void test02274() {
		check("Integrate(1/(Sqrt(Cos(c+d*x))*Sqrt(a+b*Cos(c+d*x))), x)",
				"(2*Sqrt(a+b)*Cot(c+d*x)*EllipticF(ArcSin(Sqrt(a+b*Cos(c+d*x))/(Sqrt(a+b)*Sqrt(Cos(c+d*x)))), -((a+b)/(a-b)))*Sqrt((a*(1-Sec(c+d*x)))/(a+b))*Sqrt((a*(1+Sec(c+d*x)))/(a-b)))/(a*d)");

	}

	// {1/(Sqrt(Cos(c+d*x))*Sqrt(2+3*Cos(c+d*x))), x, 1, (2*EllipticF(ArcSin(Sin(c+d*x)/(1+Cos(c+d*x))),
	// 1/5))/(Sqrt(5)*d)}
	public void test02275() {
		check("Integrate(1/(Sqrt(Cos(c+d*x))*Sqrt(2+3*Cos(c+d*x))), x)",
				"(2*EllipticF(ArcSin(Sin(c+d*x)/(1+Cos(c+d*x))), 1/5))/(Sqrt(5)*d)");

	}

	// {1/(Sqrt(Cos(c+d*x))*Sqrt(-2+3*Cos(c+d*x))), x, 1, (2*EllipticF(ArcSin(Sin(c+d*x)/(1+Cos(c+d*x))),
	// 5))/d}
	public void test02276() {
		check("Integrate(1/(Sqrt(Cos(c+d*x))*Sqrt(-2+3*Cos(c+d*x))), x)",
				"(2*EllipticF(ArcSin(Sin(c+d*x)/(1+Cos(c+d*x))), 5))/d");

	}

	// {1/(Sqrt(Cos(c+d*x))*Sqrt(3+2*Cos(c+d*x))), x, 1, (2*Cot(c+d*x)*EllipticF(ArcSin(Sqrt(3+2*Cos(c +
	// d*x))/(Sqrt(5)*Sqrt(Cos(c+d*x)))), -5)*Sqrt(-Tan(c+d*x)^2))/d}
	public void test02277() {
		check("Integrate(1/(Sqrt(Cos(c+d*x))*Sqrt(3+2*Cos(c+d*x))), x)",
				"(2*Cot(c+d*x)*EllipticF(ArcSin(Sqrt(3+2*Cos(c+d*x))/(Sqrt(5)*Sqrt(Cos(c+d*x)))), -5)*Sqrt(-Tan(c+d*x)^2))/d");

	}

	// {1/(Sqrt(3-2*Cos(c+d*x))*Sqrt(Cos(c+d*x))), x, 1, (2*Cot(c+d*x)*EllipticF(ArcSin(Sqrt(3-2*Cos(c +
	// d*x))/Sqrt(Cos(c+d*x))), -1/5)*Sqrt(-Tan(c+d*x)^2))/(Sqrt(5)*d)}
	public void test02278() {
		check("Integrate(1/(Sqrt(3-2*Cos(c+d*x))*Sqrt(Cos(c+d*x))), x)",
				"(2*Cot(c+d*x)*EllipticF(ArcSin(Sqrt(3-2*Cos(c+d*x))/Sqrt(Cos(c+d*x))), -1/5)*Sqrt(-Tan(c+d*x)^2))/(Sqrt(5)*d)");

	}

	// {1/(Sqrt(2-3*Cos(c+d*x))*Sqrt(-Cos(c+d*x))), x, 1, (-2*EllipticF(ArcSin(Sin(c+d*x)/(1-Cos(c+d*x))),
	// 1/5))/(Sqrt(5)*d)}
	public void test02279() {
		check("Integrate(1/(Sqrt(2-3*Cos(c+d*x))*Sqrt(-Cos(c+d*x))), x)",
				"(-2*EllipticF(ArcSin(Sin(c+d*x)/(1-Cos(c+d*x))), 1/5))/(Sqrt(5)*d)");

	}

	// {1/(Sqrt(-2-3*Cos(c+d*x))*Sqrt(-Cos(c+d*x))), x, 1, (-2*EllipticF(ArcSin(Sin(c+d*x)/(1-Cos(c+d*x))),
	// 5))/d}
	public void test02280() {
		check("Integrate(1/(Sqrt(-2-3*Cos(c+d*x))*Sqrt(-Cos(c+d*x))), x)",
				"(-2*EllipticF(ArcSin(Sin(c+d*x)/(1-Cos(c+d*x))), 5))/d");

	}

	// {1/(Sqrt(-Cos(c+d*x))*Sqrt(-3+2*Cos(c+d*x))), x, 1, (-2*Cot(c+d*x)*EllipticF(ArcSin(Sqrt(-3+2*Cos(c +
	// d*x))/Sqrt(-Cos(c+d*x))), -1/5)*Sqrt(-Tan(c+d*x)^2))/(Sqrt(5)*d)}
	public void test02281() {
		check("Integrate(1/(Sqrt(-Cos(c+d*x))*Sqrt(-3+2*Cos(c+d*x))), x)",
				"(-2*Cot(c+d*x)*EllipticF(ArcSin(Sqrt(-3+2*Cos(c+d*x))/Sqrt(-Cos(c+d*x))), -1/5)*Sqrt(-Tan(c+d*x)^2))/(Sqrt(5)*d)");

	}

	// {1/(Sqrt(-3-2*Cos(c+d*x))*Sqrt(-Cos(c+d*x))), x, 1, (-2*Cot(c+d*x)*EllipticF(ArcSin(Sqrt(-3-2*Cos(c +
	// d*x))/(Sqrt(5)*Sqrt(-Cos(c+d*x)))), -5)*Sqrt(-Tan(c+d*x)^2))/d}
	public void test02282() {
		check("Integrate(1/(Sqrt(-3-2*Cos(c+d*x))*Sqrt(-Cos(c+d*x))), x)",
				"(-2*Cot(c+d*x)*EllipticF(ArcSin(Sqrt(-3-2*Cos(c+d*x))/(Sqrt(5)*Sqrt(-Cos(c+d*x)))), -5)*Sqrt(-Tan(c+d*x)^2))/d");

	}

	// {Sqrt(Cos(c+d*x))/Sqrt(2+3*Cos(c+d*x)), x, 1, (-4*Cot(c+d*x)*EllipticPi(5/3, ArcSin(Sqrt(2+3*Cos(c +
	// d*x))/(Sqrt(5)*Sqrt(Cos(c+d*x)))), 5)*Sqrt(-1-Sec(c+d*x))*Sqrt(1-Sec(c+d*x)))/(3*d)}
	public void test02283() {
		check("Integrate(Sqrt(Cos(c+d*x))/Sqrt(2+3*Cos(c+d*x)), x)",
				"(-4*Cot(c+d*x)*EllipticPi(5/3, ArcSin(Sqrt(2+3*Cos(c+d*x))/(Sqrt(5)*Sqrt(Cos(c+d*x)))), 5)*Sqrt(-1-Sec(c+d*x))*Sqrt(1-Sec(c+d*x)))/(3*d)");

	}

	// {Sqrt(Cos(c+d*x))/Sqrt(-2+3*Cos(c+d*x)), x, 1, (-4*Cot(c+d*x)*EllipticPi(1/3, ArcSin(Sqrt(-2+3*Cos(c +
	// d*x))/Sqrt(Cos(c+d*x))), 1/5)*Sqrt(-1+Sec(c+d*x))*Sqrt(1+Sec(c+d*x)))/(3*Sqrt(5)*d)}
	public void test02284() {
		check("Integrate(Sqrt(Cos(c+d*x))/Sqrt(-2+3*Cos(c+d*x)), x)",
				"(-4*Cot(c+d*x)*EllipticPi(1/3, ArcSin(Sqrt(-2+3*Cos(c+d*x))/Sqrt(Cos(c+d*x))), 1/5)*Sqrt(-1+Sec(c+d*x))*Sqrt(1+Sec(c+d*x)))/(3*Sqrt(5)*d)");

	}

	// {Sqrt(Cos(c+d*x))/Sqrt(3+2*Cos(c+d*x)), x, 1, (-3*Cot(c+d*x)*EllipticPi(5/2, ArcSin(Sqrt(3+2*Cos(c +
	// d*x))/(Sqrt(5)*Sqrt(Cos(c+d*x)))), -5)*Sqrt(1-Sec(c+d*x))*Sqrt(1+Sec(c+d*x)))/d}
	public void test02285() {
		check("Integrate(Sqrt(Cos(c+d*x))/Sqrt(3+2*Cos(c+d*x)), x)",
				"(-3*Cot(c+d*x)*EllipticPi(5/2, ArcSin(Sqrt(3+2*Cos(c+d*x))/(Sqrt(5)*Sqrt(Cos(c+d*x)))), -5)*Sqrt(1-Sec(c+d*x))*Sqrt(1+Sec(c+d*x)))/d");

	}

	// {Sqrt(Cos(c+d*x))/Sqrt(3-2*Cos(c+d*x)), x, 1, (3*Cot(c+d*x)*EllipticPi(-1/2, ArcSin(Sqrt(3-2*Cos(c +
	// d*x))/Sqrt(Cos(c+d*x))), -1/5)*Sqrt(1-Sec(c+d*x))*Sqrt(1+Sec(c+d*x)))/(Sqrt(5)*d)}
	public void test02286() {
		check("Integrate(Sqrt(Cos(c+d*x))/Sqrt(3-2*Cos(c+d*x)), x)",
				"(3*Cot(c+d*x)*EllipticPi(-1/2, ArcSin(Sqrt(3-2*Cos(c+d*x))/Sqrt(Cos(c+d*x))), -1/5)*Sqrt(1-Sec(c+d*x))*Sqrt(1+Sec(c+d*x)))/(Sqrt(5)*d)");

	}

	// {Sqrt(-Cos(c+d*x))/Sqrt(2-3*Cos(c+d*x)), x, 1, (-4*Cot(c+d*x)*EllipticPi(1/3, ArcSin(Sqrt(2-3*Cos(c +
	// d*x))/Sqrt(-Cos(c+d*x))), 1/5)*Sqrt(-1+Sec(c+d*x))*Sqrt(1+Sec(c+d*x)))/(3*Sqrt(5)*d)}
	public void test02287() {
		check("Integrate(Sqrt(-Cos(c+d*x))/Sqrt(2-3*Cos(c+d*x)), x)",
				"(-4*Cot(c+d*x)*EllipticPi(1/3, ArcSin(Sqrt(2-3*Cos(c+d*x))/Sqrt(-Cos(c+d*x))), 1/5)*Sqrt(-1+Sec(c+d*x))*Sqrt(1+Sec(c+d*x)))/(3*Sqrt(5)*d)");

	}

	// {Sqrt(-Cos(c+d*x))/Sqrt(-2-3*Cos(c+d*x)), x, 1, (-4*Cot(c+d*x)*EllipticPi(5/3, ArcSin(Sqrt(-2-3*Cos(c +
	// d*x))/(Sqrt(5)*Sqrt(-Cos(c+d*x)))), 5)*Sqrt(-1-Sec(c+d*x))*Sqrt(1-Sec(c+d*x)))/(3*d)}
	public void test02288() {
		check("Integrate(Sqrt(-Cos(c+d*x))/Sqrt(-2-3*Cos(c+d*x)), x)",
				"(-4*Cot(c+d*x)*EllipticPi(5/3, ArcSin(Sqrt(-2-3*Cos(c+d*x))/(Sqrt(5)*Sqrt(-Cos(c+d*x)))), 5)*Sqrt(-1-Sec(c+d*x))*Sqrt(1-Sec(c+d*x)))/(3*d)");

	}

	// {Sqrt(-Cos(c+d*x))/Sqrt(-3+2*Cos(c+d*x)), x, 1, (3*Cot(c+d*x)*EllipticPi(-1/2, ArcSin(Sqrt(-3+2*Cos(c +
	// d*x))/Sqrt(-Cos(c+d*x))), -1/5)*Sqrt(1-Sec(c+d*x))*Sqrt(1+Sec(c+d*x)))/(Sqrt(5)*d)}
	public void test02289() {
		check("Integrate(Sqrt(-Cos(c+d*x))/Sqrt(-3+2*Cos(c+d*x)), x)",
				"(3*Cot(c+d*x)*EllipticPi(-1/2, ArcSin(Sqrt(-3+2*Cos(c+d*x))/Sqrt(-Cos(c+d*x))), -1/5)*Sqrt(1-Sec(c+d*x))*Sqrt(1+Sec(c+d*x)))/(Sqrt(5)*d)");

	}

	// {Sqrt(-Cos(c+d*x))/Sqrt(-3-2*Cos(c+d*x)), x, 1, (-3*Cot(c+d*x)*EllipticPi(5/2, ArcSin(Sqrt(-3-2*Cos(c +
	// d*x))/(Sqrt(5)*Sqrt(-Cos(c+d*x)))), -5)*Sqrt(1-Sec(c+d*x))*Sqrt(1+Sec(c+d*x)))/d}
	public void test02290() {
		check("Integrate(Sqrt(-Cos(c+d*x))/Sqrt(-3-2*Cos(c+d*x)), x)",
				"(-3*Cot(c+d*x)*EllipticPi(5/2, ArcSin(Sqrt(-3-2*Cos(c+d*x))/(Sqrt(5)*Sqrt(-Cos(c+d*x)))), -5)*Sqrt(1-Sec(c+d*x))*Sqrt(1+Sec(c+d*x)))/d");

	}

	// {(a+a*Cos(c+d*x))*(-B/2+B*Cos(c+d*x)), x, 1, (a*B*Sin(c+d*x))/(2*d)+(a*B*Cos(c+d*x)*Sin(c +
	// d*x))/(2*d)}
	public void test02291() {
		check("Integrate((a+a*Cos(c+d*x))*(-B/2+B*Cos(c+d*x)), x)",
				"(a*B*Sin(c+d*x))/(2*d)+(a*B*Cos(c+d*x)*Sin(c+d*x))/(2*d)");

	}

	// {(a+a*Cos(c+d*x))^4*((-4*B)/5+B*Cos(c+d*x)), x, 1, (B*(a+a*Cos(c+d*x))^4*Sin(c+d*x))/(5*d)}
	public void test02292() {
		check("Integrate((a+a*Cos(c+d*x))^4*((-4*B)/5+B*Cos(c+d*x)), x)", "(B*(a+a*Cos(c+d*x))^4*Sin(c+d*x))/(5*d)");

	}

	// {(a+a*Cos(c+d*x))^n*(-((B*n)/(1+n))+B*Cos(c+d*x)), x, 1, (B*(a+a*Cos(c+d*x))^n*Sin(c+d*x))/(d*(1
	// +n))}
	public void test02293() {
		check("Integrate((a+a*Cos(c+d*x))^n*(-((B*n)/(1+n))+B*Cos(c+d*x)), x)",
				"(B*(a+a*Cos(c+d*x))^n*Sin(c+d*x))/(d*(1+n))");

	}

	// {((-3*B)/2+B*Cos(c+d*x))/(a+a*Cos(c+d*x))^3, x, 1, -(B*Sin(c+d*x))/(2*d*(a+a*Cos(c+d*x))^3)}
	public void test02294() {
		check("Integrate(((-3*B)/2+B*Cos(c+d*x))/(a+a*Cos(c+d*x))^3, x)", "-(B*Sin(c+d*x))/(2*d*(a+a*Cos(c+d*x))^3)");

	}

	// {(a+a*Cos(c+d*x))^(3/2)*((-3*B)/5+B*Cos(c+d*x)), x, 1, (2*B*(a+a*Cos(c+d*x))^(3/2)*Sin(c +
	// d*x))/(5*d)}
	public void test02295() {
		check("Integrate((a+a*Cos(c+d*x))^(3/2)*((-3*B)/5+B*Cos(c+d*x)), x)",
				"(2*B*(a+a*Cos(c+d*x))^(3/2)*Sin(c+d*x))/(5*d)");

	}

	// {((-5*B)/3+B*Cos(c+d*x))/(a+a*Cos(c+d*x))^(5/2), x, 1, (-2*B*Sin(c+d*x))/(3*d*(a+a*Cos(c +
	// d*x))^(5/2))}
	public void test02296() {
		check("Integrate(((-5*B)/3+B*Cos(c+d*x))/(a+a*Cos(c+d*x))^(5/2), x)",
				"(-2*B*Sin(c+d*x))/(3*d*(a+a*Cos(c+d*x))^(5/2))");

	}

	// {(a+a*Cos(c+d*x))*(A+B*Cos(c+d*x)), x, 1, (a*(2*A+B)*x)/2+(a*(A+B)*Sin(c+d*x))/d+(a*B*Cos(c +
	// d*x)*Sin(c+d*x))/(2*d)}
	public void test02297() {
		check("Integrate((a+a*Cos(c+d*x))*(A+B*Cos(c+d*x)), x)",
				"(a*(2*A+B)*x)/2+(a*(A+B)*Sin(c+d*x))/d+(a*B*Cos(c+d*x)*Sin(c+d*x))/(2*d)");

	}

	// {(a+b*Cos(c+d*x))*(A+B*Cos(c+d*x)), x, 1, ((2*a*A+b*B)*x)/2+((A*b+a*B)*Sin(c+d*x))/d+(b*B*Cos(c
	// +d*x)*Sin(c+d*x))/(2*d)}
	public void test02298() {
		check("Integrate((a+b*Cos(c+d*x))*(A+B*Cos(c+d*x)), x)",
				"((2*a*A+b*B)*x)/2+((A*b+a*B)*Sin(c+d*x))/d+(b*B*Cos(c+d*x)*Sin(c+d*x))/(2*d)");

	}

	// {(1+Cos(c+d*x))/(Cos(c+d*x)^(3/2)*Sqrt(2+3*Cos(c+d*x))), x, 1, -((Cot(c+d*x)*EllipticE(ArcSin(Sqrt(2
	// +3*Cos(c+d*x))/(Sqrt(5)*Sqrt(Cos(c+d*x)))), 5)*Sqrt(-1-Sec(c+d*x))*Sqrt(1-Sec(c+d*x)))/d)}
	public void test02299() {
		check("Integrate((1+Cos(c+d*x))/(Cos(c+d*x)^(3/2)*Sqrt(2+3*Cos(c+d*x))), x)",
				"-((Cot(c+d*x)*EllipticE(ArcSin(Sqrt(2+3*Cos(c+d*x))/(Sqrt(5)*Sqrt(Cos(c+d*x)))), 5)*Sqrt(-1-Sec(c+d*x))*Sqrt(1-Sec(c+d*x)))/d)");

	}

	// {(1+Cos(c+d*x))/(Cos(c+d*x)^(3/2)*Sqrt(-2+3*Cos(c+d*x))), x, 1, -((Sqrt(5)*Cot(c +
	// d*x)*EllipticE(ArcSin(Sqrt(-2+3*Cos(c+d*x))/Sqrt(Cos(c+d*x))), 1/5)*Sqrt(-1+Sec(c+d*x))*Sqrt(1+Sec(c
	// +d*x)))/d)}
	public void test02300() {
		check("Integrate((1+Cos(c+d*x))/(Cos(c+d*x)^(3/2)*Sqrt(-2+3*Cos(c+d*x))), x)",
				"-((Sqrt(5)*Cot(c+d*x)*EllipticE(ArcSin(Sqrt(-2+3*Cos(c+d*x))/Sqrt(Cos(c+d*x))), 1/5)*Sqrt(-1+Sec(c+d*x))*Sqrt(1+Sec(c+d*x)))/d)");

	}

	// {(1+Cos(c+d*x))/(Cos(c+d*x)^(3/2)*Sqrt(3+2*Cos(c+d*x))), x, 1, (2*Cot(c+d*x)*EllipticE(ArcSin(Sqrt(3
	// +2*Cos(c+d*x))/(Sqrt(5)*Sqrt(Cos(c+d*x)))), -5)*Sqrt(1-Sec(c+d*x))*Sqrt(1+Sec(c+d*x)))/(3*d)}
	public void test02301() {
		check("Integrate((1+Cos(c+d*x))/(Cos(c+d*x)^(3/2)*Sqrt(3+2*Cos(c+d*x))), x)",
				"(2*Cot(c+d*x)*EllipticE(ArcSin(Sqrt(3+2*Cos(c+d*x))/(Sqrt(5)*Sqrt(Cos(c+d*x)))), -5)*Sqrt(1-Sec(c+d*x))*Sqrt(1+Sec(c+d*x)))/(3*d)");

	}

	// {(1+Cos(c+d*x))/(Sqrt(3-2*Cos(c+d*x))*Cos(c+d*x)^(3/2)), x, 1, (2*Sqrt(5)*Cot(c +
	// d*x)*EllipticE(ArcSin(Sqrt(3-2*Cos(c+d*x))/Sqrt(Cos(c+d*x))), -1/5)*Sqrt(1-Sec(c+d*x))*Sqrt(1+Sec(c +
	// d*x)))/(3*d)}
	public void test02302() {
		check("Integrate((1+Cos(c+d*x))/(Sqrt(3-2*Cos(c+d*x))*Cos(c+d*x)^(3/2)), x)",
				"(2*Sqrt(5)*Cot(c+d*x)*EllipticE(ArcSin(Sqrt(3-2*Cos(c+d*x))/Sqrt(Cos(c+d*x))), -1/5)*Sqrt(1-Sec(c+d*x))*Sqrt(1+Sec(c+d*x)))/(3*d)");

	}

	// {(c*Cos(e+f*x))^m*(a+b*Cos(e+f*x))^(3/2)*(A+B*Cos(e+f*x)), x, 1, (2*b*B*(c*Cos(e+f*x))^(1+m)*Sqrt(a
	// +b*Cos(e+f*x))*Sin(e+f*x))/(c*f*(5+2*m))+(2*Unintegrable(((c*Cos(e+f*x))^m*((a*c*(2*b*B*(1+m) +
	// 2*a*A*(5/2+m)))/2+(c*(b^2*B*(3+2*m)+a*(2*A*b+a*B)*(5+2*m))*Cos(e+f*x))/2+(b*c*(2*a*B*(3+m) +
	// A*b*(5+2*m))*Cos(e+f*x)^2)/2))/Sqrt(a+b*Cos(e+f*x)), x))/(c*(5+2*m))}
	public void test02303() {
		check("Integrate((c*Cos(e+f*x))^m*(a+b*Cos(e+f*x))^(3/2)*(A+B*Cos(e+f*x)), x)",
				"(2*b*B*(c*Cos(e+f*x))^(1+m)*Sqrt(a+b*Cos(e+f*x))*Sin(e+f*x))/(c*f*(5+2*m))+(2*Unintegrable(((c*Cos(e+f*x))^m*((a*c*(2*b*B*(1+m)+2*a*A*(5/2+m)))/2+(c*(b^2*B*(3+2*m)+a*(2*A*b+a*B)*(5+2*m))*Cos(e+f*x))/2+(b*c*(2*a*B*(3+m)+A*b*(5+2*m))*Cos(e+f*x)^2)/2))/Sqrt(a+b*Cos(e+f*x)), x))/(c*(5+2*m))");

	}

	// {((c*Cos(e+f*x))^m*(A+B*Cos(e+f*x)))/(a+b*Cos(e+f*x))^(3/2), x, 1, (2*b*(A*b-a*B)*(c*Cos(e+f*x))^(1
	// +m)*Sin(e+f*x))/(a*(a^2-b^2)*c*f*Sqrt(a+b*Cos(e+f*x)))+(2*Unintegrable(((c*Cos(e+f*x))^m*((c*(a*(a*A
	// -b*B)+2*b*(A*b-a*B)*(1/2+m)))/2-(a*(A*b-a*B)*c*Cos(e+f*x))/2-(b*(A*b-a*B)*c*(3+2*m)*Cos(e +
	// f*x)^2)/2))/Sqrt(a+b*Cos(e+f*x)), x))/(a*(a^2-b^2)*c)}
	public void test02304() {
		check("Integrate(((c*Cos(e+f*x))^m*(A+B*Cos(e+f*x)))/(a+b*Cos(e+f*x))^(3/2), x)",
				"(2*b*(A*b-a*B)*(c*Cos(e+f*x))^(1+m)*Sin(e+f*x))/(a*(a^2-b^2)*c*f*Sqrt(a+b*Cos(e+f*x)))+(2*Unintegrable(((c*Cos(e+f*x))^m*((c*(a*(a*A-b*B)+2*b*(A*b-a*B)*(1/2+m)))/2-(a*(A*b-a*B)*c*Cos(e+f*x))/2-(b*(A*b-a*B)*c*(3+2*m)*Cos(e+f*x)^2)/2))/Sqrt(a+b*Cos(e+f*x)), x))/(a*(a^2-b^2)*c)");

	}

	// {(a+b*Cos(e+f*x))^n*(A+B*Cos(e+f*x))*(c*Sec(e+f*x))^m, x, 1, (c*Cos(e+f*x))^m*(c*Sec(e +
	// f*x))^m*Unintegrable(((a+b*Cos(e+f*x))^n*(A+B*Cos(e+f*x)))/(c*Cos(e+f*x))^m, x)}
	public void test02305() {
		check("Integrate((a+b*Cos(e+f*x))^n*(A+B*Cos(e+f*x))*(c*Sec(e+f*x))^m, x)",
				"(c*Cos(e+f*x))^m*(c*Sec(e+f*x))^m*Unintegrable(((a+b*Cos(e+f*x))^n*(A+B*Cos(e+f*x)))/(c*Cos(e+f*x))^m, x)");

	}

	// {Sqrt(a+b*Cos(e+f*x))*(A+B*Cos(e+f*x))*(c*Sec(e+f*x))^m, x, 1, (c*Cos(e+f*x))^m*(c*Sec(e +
	// f*x))^m*Unintegrable((Sqrt(a+b*Cos(e+f*x))*(A+B*Cos(e+f*x)))/(c*Cos(e+f*x))^m, x)}
	public void test02306() {
		check("Integrate(Sqrt(a+b*Cos(e+f*x))*(A+B*Cos(e+f*x))*(c*Sec(e+f*x))^m, x)",
				"(c*Cos(e+f*x))^m*(c*Sec(e+f*x))^m*Unintegrable((Sqrt(a+b*Cos(e+f*x))*(A+B*Cos(e+f*x)))/(c*Cos(e+f*x))^m, x)");

	}

	// {((A+B*Cos(e+f*x))*(c*Sec(e+f*x))^m)/Sqrt(a+b*Cos(e+f*x)), x, 1, (c*Cos(e+f*x))^m*(c*Sec(e +
	// f*x))^m*Unintegrable((A+B*Cos(e+f*x))/((c*Cos(e+f*x))^m*Sqrt(a+b*Cos(e+f*x))), x)}
	public void test02307() {
		check("Integrate(((A+B*Cos(e+f*x))*(c*Sec(e+f*x))^m)/Sqrt(a+b*Cos(e+f*x)), x)",
				"(c*Cos(e+f*x))^m*(c*Sec(e+f*x))^m*Unintegrable((A+B*Cos(e+f*x))/((c*Cos(e+f*x))^m*Sqrt(a+b*Cos(e+f*x))), x)");

	}

	// {Sqrt(Cos(c+d*x))*(3-5*Cos(c+d*x)^2), x, 1, (-2*Cos(c+d*x)^(3/2)*Sin(c+d*x))/d}
	public void test02308() {
		check("Integrate(Sqrt(Cos(c+d*x))*(3-5*Cos(c+d*x)^2), x)", "(-2*Cos(c+d*x)^(3/2)*Sin(c+d*x))/d");

	}

	// {(1-3*Cos(c+d*x)^2)/Sqrt(Cos(c+d*x)), x, 1, (-2*Sqrt(Cos(c+d*x))*Sin(c+d*x))/d}
	public void test02309() {
		check("Integrate((1-3*Cos(c+d*x)^2)/Sqrt(Cos(c+d*x)), x)", "(-2*Sqrt(Cos(c+d*x))*Sin(c+d*x))/d");

	}

	// {(b*Cos(c+d*x))^m*(-((C*(1+m))/(2+m))+C*Cos(c+d*x)^2), x, 1, (C*(b*Cos(c+d*x))^(1+m)*Sin(c +
	// d*x))/(b*d*(2+m))}
	public void test02310() {
		check("Integrate((b*Cos(c+d*x))^m*(-((C*(1+m))/(2+m))+C*Cos(c+d*x)^2), x)",
				"(C*(b*Cos(c+d*x))^(1+m)*Sin(c+d*x))/(b*d*(2+m))");

	}

	// {(b*Cos(c+d*x))^m*(A-(A*(2+m)*Cos(c+d*x)^2)/(1+m)), x, 1, -((A*(b*Cos(c+d*x))^(1+m)*Sin(c +
	// d*x))/(b*d*(1+m)))}
	public void test02311() {
		check("Integrate((b*Cos(c+d*x))^m*(A-(A*(2+m)*Cos(c+d*x)^2)/(1+m)), x)",
				"-((A*(b*Cos(c+d*x))^(1+m)*Sin(c+d*x))/(b*d*(1+m)))");

	}

	// {Sqrt(1+Cos(x)^2), x, 1, EllipticE(Pi/2+x, -1)}
	public void test02312() {
		check("Integrate(Sqrt(1+Cos(x)^2), x)", "EllipticE(Pi/2+x, -1)");

	}

	// {1/Sqrt(1+Cos(x)^2), x, 1, EllipticF(Pi/2+x, -1)}
	public void test02313() {
		check("Integrate(1/Sqrt(1+Cos(x)^2), x)", "EllipticF(Pi/2+x, -1)");

	}

	// {Tan(c+d*x), x, 1, -(Log(Cos(c+d*x))/d)}
	public void test02314() {
		check("Integrate(Tan(c+d*x), x)", "-(Log(Cos(c+d*x))/d)");

	}

	// {Sqrt(a*Sin(e+f*x))*Sqrt(b*Tan(e+f*x)), x, 1, (-2*b*Sqrt(a*Sin(e+f*x)))/(f*Sqrt(b*Tan(e+f*x)))}
	public void test02315() {
		check("Integrate(Sqrt(a*Sin(e+f*x))*Sqrt(b*Tan(e+f*x)), x)",
				"(-2*b*Sqrt(a*Sin(e+f*x)))/(f*Sqrt(b*Tan(e+f*x)))");

	}

	// {(b*Tan(e+f*x))^(3/2)/Sqrt(a*Sin(e+f*x)), x, 1, (2*b*Sqrt(b*Tan(e+f*x)))/(f*Sqrt(a*Sin(e+f*x)))}
	public void test02316() {
		check("Integrate((b*Tan(e+f*x))^(3/2)/Sqrt(a*Sin(e+f*x)), x)",
				"(2*b*Sqrt(b*Tan(e+f*x)))/(f*Sqrt(a*Sin(e+f*x)))");

	}

	// {(a*Sin(e+f*x))^(3/2)/Sqrt(b*Tan(e+f*x)), x, 1, (-2*b*(a*Sin(e+f*x))^(3/2))/(3*f*(b*Tan(e+f*x))^(3/2))}
	public void test02317() {
		check("Integrate((a*Sin(e+f*x))^(3/2)/Sqrt(b*Tan(e+f*x)), x)",
				"(-2*b*(a*Sin(e+f*x))^(3/2))/(3*f*(b*Tan(e+f*x))^(3/2))");

	}

	// {(a*Sin(e+f*x))^(5/2)/(b*Tan(e+f*x))^(3/2), x, 1, (-2*b*(a*Sin(e+f*x))^(5/2))/(5*f*(b*Tan(e+f*x))^(5/2))}
	public void test02318() {
		check("Integrate((a*Sin(e+f*x))^(5/2)/(b*Tan(e+f*x))^(3/2), x)",
				"(-2*b*(a*Sin(e+f*x))^(5/2))/(5*f*(b*Tan(e+f*x))^(5/2))");

	}

	// {(d*Sec(e+f*x))^(4/3)*Tan(e+f*x)^2, x, 1, ((Cos(e+f*x)^2)^(13/6)*Hypergeometric2F1(3/2, 13/6, 5/2, Sin(e +
	// f*x)^2)*(d*Sec(e+f*x))^(4/3)*Tan(e+f*x)^3)/(3*f)}
	public void test02319() {
		check("Integrate((d*Sec(e+f*x))^(4/3)*Tan(e+f*x)^2, x)",
				"((Cos(e+f*x)^2)^(13/6)*Hypergeometric2F1(3/2, 13/6, 5/2, Sin(e+f*x)^2)*(d*Sec(e+f*x))^(4/3)*Tan(e+f*x)^3)/(3*f)");

	}

	// {(d*Sec(e+f*x))^(2/3)*Tan(e+f*x)^2, x, 1, ((Cos(e+f*x)^2)^(11/6)*Hypergeometric2F1(3/2, 11/6, 5/2, Sin(e +
	// f*x)^2)*(d*Sec(e+f*x))^(2/3)*Tan(e+f*x)^3)/(3*f)}
	public void test02320() {
		check("Integrate((d*Sec(e+f*x))^(2/3)*Tan(e+f*x)^2, x)",
				"((Cos(e+f*x)^2)^(11/6)*Hypergeometric2F1(3/2, 11/6, 5/2, Sin(e+f*x)^2)*(d*Sec(e+f*x))^(2/3)*Tan(e+f*x)^3)/(3*f)");

	}

	// {(d*Sec(e+f*x))^(1/3)*Tan(e+f*x)^2, x, 1, ((Cos(e+f*x)^2)^(5/3)*Hypergeometric2F1(3/2, 5/3, 5/2, Sin(e +
	// f*x)^2)*(d*Sec(e+f*x))^(1/3)*Tan(e+f*x)^3)/(3*f)}
	public void test02321() {
		check("Integrate((d*Sec(e+f*x))^(1/3)*Tan(e+f*x)^2, x)",
				"((Cos(e+f*x)^2)^(5/3)*Hypergeometric2F1(3/2, 5/3, 5/2, Sin(e+f*x)^2)*(d*Sec(e+f*x))^(1/3)*Tan(e+f*x)^3)/(3*f)");

	}

	// {Tan(e+f*x)^2/(d*Sec(e+f*x))^(1/3), x, 1, ((Cos(e+f*x)^2)^(4/3)*Hypergeometric2F1(4/3, 3/2, 5/2, Sin(e +
	// f*x)^2)*Tan(e+f*x)^3)/(3*f*(d*Sec(e+f*x))^(1/3))}
	public void test02322() {
		check("Integrate(Tan(e+f*x)^2/(d*Sec(e+f*x))^(1/3), x)",
				"((Cos(e+f*x)^2)^(4/3)*Hypergeometric2F1(4/3, 3/2, 5/2, Sin(e+f*x)^2)*Tan(e+f*x)^3)/(3*f*(d*Sec(e+f*x))^(1/3))");

	}

	// {Tan(e+f*x)^2/(d*Sec(e+f*x))^(2/3), x, 1, ((Cos(e+f*x)^2)^(7/6)*Hypergeometric2F1(7/6, 3/2, 5/2, Sin(e +
	// f*x)^2)*Tan(e+f*x)^3)/(3*f*(d*Sec(e+f*x))^(2/3))}
	public void test02323() {
		check("Integrate(Tan(e+f*x)^2/(d*Sec(e+f*x))^(2/3), x)",
				"((Cos(e+f*x)^2)^(7/6)*Hypergeometric2F1(7/6, 3/2, 5/2, Sin(e+f*x)^2)*Tan(e+f*x)^3)/(3*f*(d*Sec(e+f*x))^(2/3))");

	}

	// {(d*Sec(e+f*x))^(4/3)*Tan(e+f*x)^4, x, 1, ((Cos(e+f*x)^2)^(19/6)*Hypergeometric2F1(5/2, 19/6, 7/2, Sin(e +
	// f*x)^2)*(d*Sec(e+f*x))^(4/3)*Tan(e+f*x)^5)/(5*f)}
	public void test02324() {
		check("Integrate((d*Sec(e+f*x))^(4/3)*Tan(e+f*x)^4, x)",
				"((Cos(e+f*x)^2)^(19/6)*Hypergeometric2F1(5/2, 19/6, 7/2, Sin(e+f*x)^2)*(d*Sec(e+f*x))^(4/3)*Tan(e+f*x)^5)/(5*f)");

	}

	// {(d*Sec(e+f*x))^(2/3)*Tan(e+f*x)^4, x, 1, ((Cos(e+f*x)^2)^(17/6)*Hypergeometric2F1(5/2, 17/6, 7/2, Sin(e +
	// f*x)^2)*(d*Sec(e+f*x))^(2/3)*Tan(e+f*x)^5)/(5*f)}
	public void test02325() {
		check("Integrate((d*Sec(e+f*x))^(2/3)*Tan(e+f*x)^4, x)",
				"((Cos(e+f*x)^2)^(17/6)*Hypergeometric2F1(5/2, 17/6, 7/2, Sin(e+f*x)^2)*(d*Sec(e+f*x))^(2/3)*Tan(e+f*x)^5)/(5*f)");

	}

	// {(d*Sec(e+f*x))^(1/3)*Tan(e+f*x)^4, x, 1, ((Cos(e+f*x)^2)^(8/3)*Hypergeometric2F1(5/2, 8/3, 7/2, Sin(e +
	// f*x)^2)*(d*Sec(e+f*x))^(1/3)*Tan(e+f*x)^5)/(5*f)}
	public void test02326() {
		check("Integrate((d*Sec(e+f*x))^(1/3)*Tan(e+f*x)^4, x)",
				"((Cos(e+f*x)^2)^(8/3)*Hypergeometric2F1(5/2, 8/3, 7/2, Sin(e+f*x)^2)*(d*Sec(e+f*x))^(1/3)*Tan(e+f*x)^5)/(5*f)");

	}

	// {Tan(e+f*x)^4/(d*Sec(e+f*x))^(1/3), x, 1, ((Cos(e+f*x)^2)^(7/3)*Hypergeometric2F1(7/3, 5/2, 7/2, Sin(e +
	// f*x)^2)*Tan(e+f*x)^5)/(5*f*(d*Sec(e+f*x))^(1/3))}
	public void test02327() {
		check("Integrate(Tan(e+f*x)^4/(d*Sec(e+f*x))^(1/3), x)",
				"((Cos(e+f*x)^2)^(7/3)*Hypergeometric2F1(7/3, 5/2, 7/2, Sin(e+f*x)^2)*Tan(e+f*x)^5)/(5*f*(d*Sec(e+f*x))^(1/3))");

	}

	// {Tan(e+f*x)^4/(d*Sec(e+f*x))^(2/3), x, 1, ((Cos(e+f*x)^2)^(13/6)*Hypergeometric2F1(13/6, 5/2, 7/2, Sin(e +
	// f*x)^2)*Tan(e+f*x)^5)/(5*f*(d*Sec(e+f*x))^(2/3))}
	public void test02328() {
		check("Integrate(Tan(e+f*x)^4/(d*Sec(e+f*x))^(2/3), x)",
				"((Cos(e+f*x)^2)^(13/6)*Hypergeometric2F1(13/6, 5/2, 7/2, Sin(e+f*x)^2)*Tan(e+f*x)^5)/(5*f*(d*Sec(e+f*x))^(2/3))");

	}

	// {Sqrt(b*Tan(e+f*x))/(d*Sec(e+f*x))^(3/2), x, 1, (2*(b*Tan(e+f*x))^(3/2))/(3*b*f*(d*Sec(e+f*x))^(3/2))}
	public void test02329() {
		check("Integrate(Sqrt(b*Tan(e+f*x))/(d*Sec(e+f*x))^(3/2), x)",
				"(2*(b*Tan(e+f*x))^(3/2))/(3*b*f*(d*Sec(e+f*x))^(3/2))");

	}

	// {(b*Tan(e+f*x))^(3/2)/(d*Sec(e+f*x))^(5/2), x, 1, (2*(b*Tan(e+f*x))^(5/2))/(5*b*f*(d*Sec(e+f*x))^(5/2))}
	public void test02330() {
		check("Integrate((b*Tan(e+f*x))^(3/2)/(d*Sec(e+f*x))^(5/2), x)",
				"(2*(b*Tan(e+f*x))^(5/2))/(5*b*f*(d*Sec(e+f*x))^(5/2))");

	}

	// {(b*Tan(e+f*x))^(5/2)/(d*Sec(e+f*x))^(7/2), x, 1, (2*(b*Tan(e+f*x))^(7/2))/(7*b*f*(d*Sec(e+f*x))^(7/2))}
	public void test02331() {
		check("Integrate((b*Tan(e+f*x))^(5/2)/(d*Sec(e+f*x))^(7/2), x)",
				"(2*(b*Tan(e+f*x))^(7/2))/(7*b*f*(d*Sec(e+f*x))^(7/2))");

	}

	// {1/(Sqrt(d*Sec(e+f*x))*Sqrt(b*Tan(e+f*x))), x, 1, (2*Sqrt(b*Tan(e+f*x)))/(b*f*Sqrt(d*Sec(e+f*x)))}
	public void test02332() {
		check("Integrate(1/(Sqrt(d*Sec(e+f*x))*Sqrt(b*Tan(e+f*x))), x)",
				"(2*Sqrt(b*Tan(e+f*x)))/(b*f*Sqrt(d*Sec(e+f*x)))");

	}

	// {Sqrt(d*Sec(e+f*x))/(b*Tan(e+f*x))^(3/2), x, 1, (-2*Sqrt(d*Sec(e+f*x)))/(b*f*Sqrt(b*Tan(e+f*x)))}
	public void test02333() {
		check("Integrate(Sqrt(d*Sec(e+f*x))/(b*Tan(e+f*x))^(3/2), x)",
				"(-2*Sqrt(d*Sec(e+f*x)))/(b*f*Sqrt(b*Tan(e+f*x)))");

	}

	// {(d*Sec(e+f*x))^(3/2)/(b*Tan(e+f*x))^(5/2), x, 1, (-2*(d*Sec(e+f*x))^(3/2))/(3*b*f*(b*Tan(e+f*x))^(3/2))}
	public void test02334() {
		check("Integrate((d*Sec(e+f*x))^(3/2)/(b*Tan(e+f*x))^(5/2), x)",
				"(-2*(d*Sec(e+f*x))^(3/2))/(3*b*f*(b*Tan(e+f*x))^(3/2))");

	}

	// {(b*Sec(e+f*x))^(4/3)*Sqrt(d*Tan(e+f*x)), x, 1, (2*(Cos(e+f*x)^2)^(17/12)*Hypergeometric2F1(3/4, 17/12,
	// 7/4, Sin(e+f*x)^2)*(b*Sec(e+f*x))^(4/3)*(d*Tan(e+f*x))^(3/2))/(3*d*f)}
	public void test02335() {
		check("Integrate((b*Sec(e+f*x))^(4/3)*Sqrt(d*Tan(e+f*x)), x)",
				"(2*(Cos(e+f*x)^2)^(17/12)*Hypergeometric2F1(3/4, 17/12, 7/4, Sin(e+f*x)^2)*(b*Sec(e+f*x))^(4/3)*(d*Tan(e+f*x))^(3/2))/(3*d*f)");

	}

	// {(b*Sec(e+f*x))^(1/3)*Sqrt(d*Tan(e+f*x)), x, 1, (2*(Cos(e+f*x)^2)^(11/12)*Hypergeometric2F1(3/4, 11/12,
	// 7/4, Sin(e+f*x)^2)*(b*Sec(e+f*x))^(1/3)*(d*Tan(e+f*x))^(3/2))/(3*d*f)}
	public void test02336() {
		check("Integrate((b*Sec(e+f*x))^(1/3)*Sqrt(d*Tan(e+f*x)), x)",
				"(2*(Cos(e+f*x)^2)^(11/12)*Hypergeometric2F1(3/4, 11/12, 7/4, Sin(e+f*x)^2)*(b*Sec(e+f*x))^(1/3)*(d*Tan(e+f*x))^(3/2))/(3*d*f)");

	}

	// {Sqrt(d*Tan(e+f*x))/(b*Sec(e+f*x))^(1/3), x, 1, (2*(Cos(e+f*x)^2)^(7/12)*Hypergeometric2F1(7/12, 3/4, 7/4,
	// Sin(e+f*x)^2)*(d*Tan(e+f*x))^(3/2))/(3*d*f*(b*Sec(e+f*x))^(1/3))}
	public void test02337() {
		check("Integrate(Sqrt(d*Tan(e+f*x))/(b*Sec(e+f*x))^(1/3), x)",
				"(2*(Cos(e+f*x)^2)^(7/12)*Hypergeometric2F1(7/12, 3/4, 7/4, Sin(e+f*x)^2)*(d*Tan(e+f*x))^(3/2))/(3*d*f*(b*Sec(e+f*x))^(1/3))");

	}

	// {Sqrt(d*Tan(e+f*x))/(b*Sec(e+f*x))^(4/3), x, 1, (2*(Cos(e+f*x)^2)^(1/12)*Hypergeometric2F1(1/12, 3/4, 7/4,
	// Sin(e+f*x)^2)*(d*Tan(e+f*x))^(3/2))/(3*d*f*(b*Sec(e+f*x))^(4/3))}
	public void test02338() {
		check("Integrate(Sqrt(d*Tan(e+f*x))/(b*Sec(e+f*x))^(4/3), x)",
				"(2*(Cos(e+f*x)^2)^(1/12)*Hypergeometric2F1(1/12, 3/4, 7/4, Sin(e+f*x)^2)*(d*Tan(e+f*x))^(3/2))/(3*d*f*(b*Sec(e+f*x))^(4/3))");

	}

	// {(b*Sec(e+f*x))^(4/3)*(d*Tan(e+f*x))^(3/2), x, 1, (2*(Cos(e+f*x)^2)^(23/12)*Hypergeometric2F1(5/4, 23/12,
	// 9/4, Sin(e+f*x)^2)*(b*Sec(e+f*x))^(4/3)*(d*Tan(e+f*x))^(5/2))/(5*d*f)}
	public void test02339() {
		check("Integrate((b*Sec(e+f*x))^(4/3)*(d*Tan(e+f*x))^(3/2), x)",
				"(2*(Cos(e+f*x)^2)^(23/12)*Hypergeometric2F1(5/4, 23/12, 9/4, Sin(e+f*x)^2)*(b*Sec(e+f*x))^(4/3)*(d*Tan(e+f*x))^(5/2))/(5*d*f)");

	}

	// {(b*Sec(e+f*x))^(1/3)*(d*Tan(e+f*x))^(3/2), x, 1, (2*(Cos(e+f*x)^2)^(17/12)*Hypergeometric2F1(5/4, 17/12,
	// 9/4, Sin(e+f*x)^2)*(b*Sec(e+f*x))^(1/3)*(d*Tan(e+f*x))^(5/2))/(5*d*f)}
	public void test02340() {
		check("Integrate((b*Sec(e+f*x))^(1/3)*(d*Tan(e+f*x))^(3/2), x)",
				"(2*(Cos(e+f*x)^2)^(17/12)*Hypergeometric2F1(5/4, 17/12, 9/4, Sin(e+f*x)^2)*(b*Sec(e+f*x))^(1/3)*(d*Tan(e+f*x))^(5/2))/(5*d*f)");

	}

	// {(d*Tan(e+f*x))^(3/2)/(b*Sec(e+f*x))^(1/3), x, 1, (2*(Cos(e+f*x)^2)^(13/12)*Hypergeometric2F1(13/12, 5/4,
	// 9/4, Sin(e+f*x)^2)*(d*Tan(e+f*x))^(5/2))/(5*d*f*(b*Sec(e+f*x))^(1/3))}
	public void test02341() {
		check("Integrate((d*Tan(e+f*x))^(3/2)/(b*Sec(e+f*x))^(1/3), x)",
				"(2*(Cos(e+f*x)^2)^(13/12)*Hypergeometric2F1(13/12, 5/4, 9/4, Sin(e+f*x)^2)*(d*Tan(e+f*x))^(5/2))/(5*d*f*(b*Sec(e+f*x))^(1/3))");

	}

	// {(d*Tan(e+f*x))^(3/2)/(b*Sec(e+f*x))^(4/3), x, 1, (2*(Cos(e+f*x)^2)^(7/12)*Hypergeometric2F1(7/12, 5/4,
	// 9/4, Sin(e+f*x)^2)*(d*Tan(e+f*x))^(5/2))/(5*d*f*(b*Sec(e+f*x))^(4/3))}
	public void test02342() {
		check("Integrate((d*Tan(e+f*x))^(3/2)/(b*Sec(e+f*x))^(4/3), x)",
				"(2*(Cos(e+f*x)^2)^(7/12)*Hypergeometric2F1(7/12, 5/4, 9/4, Sin(e+f*x)^2)*(d*Tan(e+f*x))^(5/2))/(5*d*f*(b*Sec(e+f*x))^(4/3))");

	}

	// {Sqrt(b*Sec(e+f*x))*(d*Tan(e+f*x))^(4/3), x, 1, (3*(Cos(e+f*x)^2)^(17/12)*Hypergeometric2F1(7/6, 17/12,
	// 13/6, Sin(e+f*x)^2)*Sqrt(b*Sec(e+f*x))*(d*Tan(e+f*x))^(7/3))/(7*d*f)}
	public void test02343() {
		check("Integrate(Sqrt(b*Sec(e+f*x))*(d*Tan(e+f*x))^(4/3), x)",
				"(3*(Cos(e+f*x)^2)^(17/12)*Hypergeometric2F1(7/6, 17/12, 13/6, Sin(e+f*x)^2)*Sqrt(b*Sec(e+f*x))*(d*Tan(e+f*x))^(7/3))/(7*d*f)");

	}

	// {Sqrt(b*Sec(e+f*x))*(d*Tan(e+f*x))^(1/3), x, 1, (3*(Cos(e+f*x)^2)^(11/12)*Hypergeometric2F1(2/3, 11/12,
	// 5/3, Sin(e+f*x)^2)*Sqrt(b*Sec(e+f*x))*(d*Tan(e+f*x))^(4/3))/(4*d*f)}
	public void test02344() {
		check("Integrate(Sqrt(b*Sec(e+f*x))*(d*Tan(e+f*x))^(1/3), x)",
				"(3*(Cos(e+f*x)^2)^(11/12)*Hypergeometric2F1(2/3, 11/12, 5/3, Sin(e+f*x)^2)*Sqrt(b*Sec(e+f*x))*(d*Tan(e+f*x))^(4/3))/(4*d*f)");

	}

	// {Sqrt(b*Sec(e+f*x))/(d*Tan(e+f*x))^(1/3), x, 1, (3*(Cos(e+f*x)^2)^(7/12)*Hypergeometric2F1(1/3, 7/12, 4/3,
	// Sin(e+f*x)^2)*Sqrt(b*Sec(e+f*x))*(d*Tan(e+f*x))^(2/3))/(2*d*f)}
	public void test02345() {
		check("Integrate(Sqrt(b*Sec(e+f*x))/(d*Tan(e+f*x))^(1/3), x)",
				"(3*(Cos(e+f*x)^2)^(7/12)*Hypergeometric2F1(1/3, 7/12, 4/3, Sin(e+f*x)^2)*Sqrt(b*Sec(e+f*x))*(d*Tan(e+f*x))^(2/3))/(2*d*f)");

	}

	// {Sqrt(b*Sec(e+f*x))/(d*Tan(e+f*x))^(4/3), x, 1, (-3*(Cos(e+f*x)^2)^(1/12)*Hypergeometric2F1(-1/6, 1/12,
	// 5/6, Sin(e+f*x)^2)*Sqrt(b*Sec(e+f*x)))/(d*f*(d*Tan(e+f*x))^(1/3))}
	public void test02346() {
		check("Integrate(Sqrt(b*Sec(e+f*x))/(d*Tan(e+f*x))^(4/3), x)",
				"(-3*(Cos(e+f*x)^2)^(1/12)*Hypergeometric2F1(-1/6, 1/12, 5/6, Sin(e+f*x)^2)*Sqrt(b*Sec(e+f*x)))/(d*f*(d*Tan(e+f*x))^(1/3))");

	}

	// {(b*Sec(e+f*x))^(3/2)*(d*Tan(e+f*x))^(4/3), x, 1, (3*(Cos(e+f*x)^2)^(23/12)*Hypergeometric2F1(7/6, 23/12,
	// 13/6, Sin(e+f*x)^2)*(b*Sec(e+f*x))^(3/2)*(d*Tan(e+f*x))^(7/3))/(7*d*f)}
	public void test02347() {
		check("Integrate((b*Sec(e+f*x))^(3/2)*(d*Tan(e+f*x))^(4/3), x)",
				"(3*(Cos(e+f*x)^2)^(23/12)*Hypergeometric2F1(7/6, 23/12, 13/6, Sin(e+f*x)^2)*(b*Sec(e+f*x))^(3/2)*(d*Tan(e+f*x))^(7/3))/(7*d*f)");

	}

	// {(b*Sec(e+f*x))^(3/2)*(d*Tan(e+f*x))^(1/3), x, 1, (3*(Cos(e+f*x)^2)^(17/12)*Hypergeometric2F1(2/3, 17/12,
	// 5/3, Sin(e+f*x)^2)*(b*Sec(e+f*x))^(3/2)*(d*Tan(e+f*x))^(4/3))/(4*d*f)}
	public void test02348() {
		check("Integrate((b*Sec(e+f*x))^(3/2)*(d*Tan(e+f*x))^(1/3), x)",
				"(3*(Cos(e+f*x)^2)^(17/12)*Hypergeometric2F1(2/3, 17/12, 5/3, Sin(e+f*x)^2)*(b*Sec(e+f*x))^(3/2)*(d*Tan(e+f*x))^(4/3))/(4*d*f)");

	}

	// {(b*Sec(e+f*x))^(3/2)/(d*Tan(e+f*x))^(1/3), x, 1, (3*(Cos(e+f*x)^2)^(13/12)*Hypergeometric2F1(1/3, 13/12,
	// 4/3, Sin(e+f*x)^2)*(b*Sec(e+f*x))^(3/2)*(d*Tan(e+f*x))^(2/3))/(2*d*f)}
	public void test02349() {
		check("Integrate((b*Sec(e+f*x))^(3/2)/(d*Tan(e+f*x))^(1/3), x)",
				"(3*(Cos(e+f*x)^2)^(13/12)*Hypergeometric2F1(1/3, 13/12, 4/3, Sin(e+f*x)^2)*(b*Sec(e+f*x))^(3/2)*(d*Tan(e+f*x))^(2/3))/(2*d*f)");

	}

	// {(b*Sec(e+f*x))^(3/2)/(d*Tan(e+f*x))^(4/3), x, 1, (-3*(Cos(e+f*x)^2)^(7/12)*Hypergeometric2F1(-1/6, 7/12,
	// 5/6, Sin(e+f*x)^2)*(b*Sec(e+f*x))^(3/2))/(d*f*(d*Tan(e+f*x))^(1/3))}
	public void test02350() {
		check("Integrate((b*Sec(e+f*x))^(3/2)/(d*Tan(e+f*x))^(4/3), x)",
				"(-3*(Cos(e+f*x)^2)^(7/12)*Hypergeometric2F1(-1/6, 7/12, 5/6, Sin(e+f*x)^2)*(b*Sec(e+f*x))^(3/2))/(d*f*(d*Tan(e+f*x))^(1/3))");

	}

	// {(b*Sec(e+f*x))^m*Tan(e+f*x)^4, x, 1, ((Cos(e+f*x)^2)^((5+m)/2)*Hypergeometric2F1(5/2, (5+m)/2, 7/2,
	// Sin(e+f*x)^2)*(b*Sec(e+f*x))^m*Tan(e+f*x)^5)/(5*f)}
	public void test02351() {
		check("Integrate((b*Sec(e+f*x))^m*Tan(e+f*x)^4, x)",
				"((Cos(e+f*x)^2)^((5+m)/2)*Hypergeometric2F1(5/2, (5+m)/2, 7/2, Sin(e+f*x)^2)*(b*Sec(e+f*x))^m*Tan(e+f*x)^5)/(5*f)");

	}

	// {(b*Sec(e+f*x))^m*Tan(e+f*x)^2, x, 1, ((Cos(e+f*x)^2)^((3+m)/2)*Hypergeometric2F1(3/2, (3+m)/2, 5/2,
	// Sin(e+f*x)^2)*(b*Sec(e+f*x))^m*Tan(e+f*x)^3)/(3*f)}
	public void test02352() {
		check("Integrate((b*Sec(e+f*x))^m*Tan(e+f*x)^2, x)",
				"((Cos(e+f*x)^2)^((3+m)/2)*Hypergeometric2F1(3/2, (3+m)/2, 5/2, Sin(e+f*x)^2)*(b*Sec(e+f*x))^m*Tan(e+f*x)^3)/(3*f)");

	}

	// {Cot(e+f*x)^2*(b*Sec(e+f*x))^m, x, 1, -(((Cos(e+f*x)^2)^((-1+m)/2)*Cot(e+f*x)*Hypergeometric2F1(-1/2,
	// (-1+m)/2, 1/2, Sin(e+f*x)^2)*(b*Sec(e+f*x))^m)/f)}
	public void test02353() {
		check("Integrate(Cot(e+f*x)^2*(b*Sec(e+f*x))^m, x)",
				"-(((Cos(e+f*x)^2)^((-1+m)/2)*Cot(e+f*x)*Hypergeometric2F1(-1/2, (-1+m)/2, 1/2, Sin(e+f*x)^2)*(b*Sec(e+f*x))^m)/f)");

	}

	// {Cot(e+f*x)^4*(b*Sec(e+f*x))^m, x, 1, -((Cos(e+f*x)^2)^((-3+m)/2)*Cot(e+f*x)^3*Hypergeometric2F1(-3/2,
	// (-3+m)/2, -1/2, Sin(e+f*x)^2)*(b*Sec(e+f*x))^m)/(3*f)}
	public void test02354() {
		check("Integrate(Cot(e+f*x)^4*(b*Sec(e+f*x))^m, x)",
				"-((Cos(e+f*x)^2)^((-3+m)/2)*Cot(e+f*x)^3*Hypergeometric2F1(-3/2, (-3+m)/2, -1/2, Sin(e+f*x)^2)*(b*Sec(e+f*x))^m)/(3*f)");

	}

	// {Cot(e+f*x)^6*(b*Sec(e+f*x))^m, x, 1, -((Cos(e+f*x)^2)^((-5+m)/2)*Cot(e+f*x)^5*Hypergeometric2F1(-5/2,
	// (-5+m)/2, -3/2, Sin(e+f*x)^2)*(b*Sec(e+f*x))^m)/(5*f)}
	public void test02355() {
		check("Integrate(Cot(e+f*x)^6*(b*Sec(e+f*x))^m, x)",
				"-((Cos(e+f*x)^2)^((-5+m)/2)*Cot(e+f*x)^5*Hypergeometric2F1(-5/2, (-5+m)/2, -3/2, Sin(e+f*x)^2)*(b*Sec(e+f*x))^m)/(5*f)");

	}

	// {(a*Sec(e+f*x))^m*(b*Tan(e+f*x))^n, x, 1, ((Cos(e+f*x)^2)^((1+m+n)/2)*Hypergeometric2F1((1+n)/2, (1 +
	// m+n)/2, (3+n)/2, Sin(e+f*x)^2)*(a*Sec(e+f*x))^m*(b*Tan(e+f*x))^(1+n))/(b*f*(1+n))}
	public void test02356() {
		check("Integrate((a*Sec(e+f*x))^m*(b*Tan(e+f*x))^n, x)",
				"((Cos(e+f*x)^2)^((1+m+n)/2)*Hypergeometric2F1((1+n)/2, (1+m+n)/2, (3+n)/2, Sin(e+f*x)^2)*(a*Sec(e+f*x))^m*(b*Tan(e+f*x))^(1+n))/(b*f*(1+n))");

	}

	// {Sec(a+b*x)^5*(d*Tan(a+b*x))^n, x, 1, ((Cos(a+b*x)^2)^((6+n)/2)*Hypergeometric2F1((1+n)/2, (6+n)/2,
	// (3+n)/2, Sin(a+b*x)^2)*Sec(a+b*x)^5*(d*Tan(a+b*x))^(1+n))/(b*d*(1+n))}
	public void test02357() {
		check("Integrate(Sec(a+b*x)^5*(d*Tan(a+b*x))^n, x)",
				"((Cos(a+b*x)^2)^((6+n)/2)*Hypergeometric2F1((1+n)/2, (6+n)/2, (3+n)/2, Sin(a+b*x)^2)*Sec(a+b*x)^5*(d*Tan(a+b*x))^(1+n))/(b*d*(1+n))");

	}

	// {Sec(a+b*x)^3*(d*Tan(a+b*x))^n, x, 1, ((Cos(a+b*x)^2)^((4+n)/2)*Hypergeometric2F1((1+n)/2, (4+n)/2,
	// (3+n)/2, Sin(a+b*x)^2)*Sec(a+b*x)^3*(d*Tan(a+b*x))^(1+n))/(b*d*(1+n))}
	public void test02358() {
		check("Integrate(Sec(a+b*x)^3*(d*Tan(a+b*x))^n, x)",
				"((Cos(a+b*x)^2)^((4+n)/2)*Hypergeometric2F1((1+n)/2, (4+n)/2, (3+n)/2, Sin(a+b*x)^2)*Sec(a+b*x)^3*(d*Tan(a+b*x))^(1+n))/(b*d*(1+n))");

	}

	// {Sec(a+b*x)*(d*Tan(a+b*x))^n, x, 1, ((Cos(a+b*x)^2)^((2+n)/2)*Hypergeometric2F1((1+n)/2, (2+n)/2, (3
	// +n)/2, Sin(a+b*x)^2)*Sec(a+b*x)*(d*Tan(a+b*x))^(1+n))/(b*d*(1+n))}
	public void test02359() {
		check("Integrate(Sec(a+b*x)*(d*Tan(a+b*x))^n, x)",
				"((Cos(a+b*x)^2)^((2+n)/2)*Hypergeometric2F1((1+n)/2, (2+n)/2, (3+n)/2, Sin(a+b*x)^2)*Sec(a+b*x)*(d*Tan(a+b*x))^(1+n))/(b*d*(1+n))");

	}

	// {Cos(a+b*x)*(d*Tan(a+b*x))^n, x, 1, (Cos(a+b*x)*(Cos(a+b*x)^2)^(n/2)*Hypergeometric2F1(n/2, (1+n)/2, (3
	// +n)/2, Sin(a+b*x)^2)*(d*Tan(a+b*x))^(1+n))/(b*d*(1+n))}
	public void test02360() {
		check("Integrate(Cos(a+b*x)*(d*Tan(a+b*x))^n, x)",
				"(Cos(a+b*x)*(Cos(a+b*x)^2)^(n/2)*Hypergeometric2F1(n/2, (1+n)/2, (3+n)/2, Sin(a+b*x)^2)*(d*Tan(a+b*x))^(1+n))/(b*d*(1+n))");

	}

	// {Cos(a+b*x)^3*(d*Tan(a+b*x))^n, x, 1, (Cos(a+b*x)^3*(Cos(a+b*x)^2)^((-2+n)/2)*Hypergeometric2F1((-2 +
	// n)/2, (1+n)/2, (3+n)/2, Sin(a+b*x)^2)*(d*Tan(a+b*x))^(1+n))/(b*d*(1+n))}
	public void test02361() {
		check("Integrate(Cos(a+b*x)^3*(d*Tan(a+b*x))^n, x)",
				"(Cos(a+b*x)^3*(Cos(a+b*x)^2)^((-2+n)/2)*Hypergeometric2F1((-2+n)/2, (1+n)/2, (3+n)/2, Sin(a+b*x)^2)*(d*Tan(a+b*x))^(1+n))/(b*d*(1+n))");

	}

	// {(b*Csc(e+f*x))^m*Tan(e+f*x)^4, x, 1, ((b*Csc(e+f*x))^m*Hypergeometric2F1(-3/2, (-3+m)/2, -1/2, Cos(e +
	// f*x)^2)*(Sin(e+f*x)^2)^((-3+m)/2)*Tan(e+f*x)^3)/(3*f)}
	public void test02362() {
		check("Integrate((b*Csc(e+f*x))^m*Tan(e+f*x)^4, x)",
				"((b*Csc(e+f*x))^m*Hypergeometric2F1(-3/2, (-3+m)/2, -1/2, Cos(e+f*x)^2)*(Sin(e+f*x)^2)^((-3+m)/2)*Tan(e+f*x)^3)/(3*f)");

	}

	// {(b*Csc(e+f*x))^m*Tan(e+f*x)^2, x, 1, ((b*Csc(e+f*x))^m*Hypergeometric2F1(-1/2, (-1+m)/2, 1/2, Cos(e +
	// f*x)^2)*(Sin(e+f*x)^2)^((-1+m)/2)*Tan(e+f*x))/f}
	public void test02363() {
		check("Integrate((b*Csc(e+f*x))^m*Tan(e+f*x)^2, x)",
				"((b*Csc(e+f*x))^m*Hypergeometric2F1(-1/2, (-1+m)/2, 1/2, Cos(e+f*x)^2)*(Sin(e+f*x)^2)^((-1+m)/2)*Tan(e+f*x))/f");

	}

	// {Cot(e+f*x)^2*(b*Csc(e+f*x))^m, x, 1, -(Cot(e+f*x)^3*(b*Csc(e+f*x))^m*Hypergeometric2F1(3/2, (3+m)/2,
	// 5/2, Cos(e+f*x)^2)*(Sin(e+f*x)^2)^((3+m)/2))/(3*f)}
	public void test02364() {
		check("Integrate(Cot(e+f*x)^2*(b*Csc(e+f*x))^m, x)",
				"-(Cot(e+f*x)^3*(b*Csc(e+f*x))^m*Hypergeometric2F1(3/2, (3+m)/2, 5/2, Cos(e+f*x)^2)*(Sin(e+f*x)^2)^((3+m)/2))/(3*f)");

	}

	// {Cot(e+f*x)^4*(b*Csc(e+f*x))^m, x, 1, -(Cot(e+f*x)^5*(b*Csc(e+f*x))^m*Hypergeometric2F1(5/2, (5+m)/2,
	// 7/2, Cos(e+f*x)^2)*(Sin(e+f*x)^2)^((5+m)/2))/(5*f)}
	public void test02365() {
		check("Integrate(Cot(e+f*x)^4*(b*Csc(e+f*x))^m, x)",
				"-(Cot(e+f*x)^5*(b*Csc(e+f*x))^m*Hypergeometric2F1(5/2, (5+m)/2, 7/2, Cos(e+f*x)^2)*(Sin(e+f*x)^2)^((5+m)/2))/(5*f)");

	}

	// {a+b*Tan(c+d*x^2), x, 1, a*x+b*Unintegrable(Tan(c+d*x^2), x)}
	public void test02366() {
		check("Integrate(a+b*Tan(c+d*x^2), x)", "a*x+b*Unintegrable(Tan(c+d*x^2), x)");

	}

	// {Cos(c+d*x)^3*(a+I*a*Tan(c+d*x))^3, x, 1, ((-I/3)*Cos(c+d*x)^3*(a+I*a*Tan(c+d*x))^3)/d}
	public void test02367() {
		check("Integrate(Cos(c+d*x)^3*(a+I*a*Tan(c+d*x))^3, x)", "((-I/3)*Cos(c+d*x)^3*(a+I*a*Tan(c+d*x))^3)/d");

	}

	// {Cos(c+d*x)^5*(a+I*a*Tan(c+d*x))^5, x, 1, ((-I/5)*Cos(c+d*x)^5*(a+I*a*Tan(c+d*x))^5)/d}
	public void test02368() {
		check("Integrate(Cos(c+d*x)^5*(a+I*a*Tan(c+d*x))^5, x)", "((-I/5)*Cos(c+d*x)^5*(a+I*a*Tan(c+d*x))^5)/d");

	}

	// {Sec(c+d*x)/(a+I*a*Tan(c+d*x)), x, 1, (I*Sec(c+d*x))/(d*(a+I*a*Tan(c+d*x)))}
	public void test02369() {
		check("Integrate(Sec(c+d*x)/(a+I*a*Tan(c+d*x)), x)", "(I*Sec(c+d*x))/(d*(a+I*a*Tan(c+d*x)))");

	}

	// {Sec(c+d*x)^3/(a+I*a*Tan(c+d*x))^3, x, 1, ((I/3)*Sec(c+d*x)^3)/(d*(a+I*a*Tan(c+d*x))^3)}
	public void test02370() {
		check("Integrate(Sec(c+d*x)^3/(a+I*a*Tan(c+d*x))^3, x)", "((I/3)*Sec(c+d*x)^3)/(d*(a+I*a*Tan(c+d*x))^3)");

	}

	// {Sec(c+d*x)*Sqrt(a+I*a*Tan(c+d*x)), x, 1, ((2*I)*a*Sec(c+d*x))/(d*Sqrt(a+I*a*Tan(c+d*x)))}
	public void test02371() {
		check("Integrate(Sec(c+d*x)*Sqrt(a+I*a*Tan(c+d*x)), x)", "((2*I)*a*Sec(c+d*x))/(d*Sqrt(a+I*a*Tan(c+d*x)))");

	}

	// {Cos(c+d*x)*(a+I*a*Tan(c+d*x))^(3/2), x, 1, ((-2*I)*a*Cos(c+d*x)*Sqrt(a+I*a*Tan(c+d*x)))/d}
	public void test02372() {
		check("Integrate(Cos(c+d*x)*(a+I*a*Tan(c+d*x))^(3/2), x)", "((-2*I)*a*Cos(c+d*x)*Sqrt(a+I*a*Tan(c+d*x)))/d");

	}

	// {Cos(c+d*x)^3*(a+I*a*Tan(c+d*x))^(5/2), x, 1, (((-2*I)/3)*a*Cos(c+d*x)^3*(a+I*a*Tan(c+d*x))^(3/2))/d}
	public void test02373() {
		check("Integrate(Cos(c+d*x)^3*(a+I*a*Tan(c+d*x))^(5/2), x)",
				"(((-2*I)/3)*a*Cos(c+d*x)^3*(a+I*a*Tan(c+d*x))^(3/2))/d");

	}

	// {Cos(c+d*x)^5*(a+I*a*Tan(c+d*x))^(7/2), x, 1, (((-2*I)/5)*a*Cos(c+d*x)^5*(a+I*a*Tan(c+d*x))^(5/2))/d}
	public void test02374() {
		check("Integrate(Cos(c+d*x)^5*(a+I*a*Tan(c+d*x))^(7/2), x)",
				"(((-2*I)/5)*a*Cos(c+d*x)^5*(a+I*a*Tan(c+d*x))^(5/2))/d");

	}

	// {Sec(c+d*x)^3/Sqrt(a+I*a*Tan(c+d*x)), x, 1, (((2*I)/3)*a*Sec(c+d*x)^3)/(d*(a+I*a*Tan(c+d*x))^(3/2))}
	public void test02375() {
		check("Integrate(Sec(c+d*x)^3/Sqrt(a+I*a*Tan(c+d*x)), x)",
				"(((2*I)/3)*a*Sec(c+d*x)^3)/(d*(a+I*a*Tan(c+d*x))^(3/2))");

	}

	// {Sec(c+d*x)^5/(a+I*a*Tan(c+d*x))^(3/2), x, 1, (((2*I)/5)*a*Sec(c+d*x)^5)/(d*(a+I*a*Tan(c +
	// d*x))^(5/2))}
	public void test02376() {
		check("Integrate(Sec(c+d*x)^5/(a+I*a*Tan(c+d*x))^(3/2), x)",
				"(((2*I)/5)*a*Sec(c+d*x)^5)/(d*(a+I*a*Tan(c+d*x))^(5/2))");

	}

	// {Sec(c+d*x)^7/(a+I*a*Tan(c+d*x))^(5/2), x, 1, (((2*I)/7)*a*Sec(c+d*x)^7)/(d*(a+I*a*Tan(c +
	// d*x))^(7/2))}
	public void test02377() {
		check("Integrate(Sec(c+d*x)^7/(a+I*a*Tan(c+d*x))^(5/2), x)",
				"(((2*I)/7)*a*Sec(c+d*x)^7)/(d*(a+I*a*Tan(c+d*x))^(7/2))");

	}

	// {Sec(c+d*x)^9/(a+I*a*Tan(c+d*x))^(7/2), x, 1, (((2*I)/9)*a*Sec(c+d*x)^9)/(d*(a+I*a*Tan(c +
	// d*x))^(9/2))}
	public void test02378() {
		check("Integrate(Sec(c+d*x)^9/(a+I*a*Tan(c+d*x))^(7/2), x)",
				"(((2*I)/9)*a*Sec(c+d*x)^9)/(d*(a+I*a*Tan(c+d*x))^(9/2))");

	}

	// {Sqrt(a+I*a*Tan(c+d*x))/Sqrt(e*Sec(c+d*x)), x, 1, ((-2*I)*Sqrt(a+I*a*Tan(c+d*x)))/(d*Sqrt(e*Sec(c +
	// d*x)))}
	public void test02379() {
		check("Integrate(Sqrt(a+I*a*Tan(c+d*x))/Sqrt(e*Sec(c+d*x)), x)",
				"((-2*I)*Sqrt(a+I*a*Tan(c+d*x)))/(d*Sqrt(e*Sec(c+d*x)))");

	}

	// {(a+I*a*Tan(c+d*x))^(3/2)/(e*Sec(c+d*x))^(3/2), x, 1, (((-2*I)/3)*(a+I*a*Tan(c+d*x))^(3/2))/(d*(e*Sec(c
	// +d*x))^(3/2))}
	public void test02380() {
		check("Integrate((a+I*a*Tan(c+d*x))^(3/2)/(e*Sec(c+d*x))^(3/2), x)",
				"(((-2*I)/3)*(a+I*a*Tan(c+d*x))^(3/2))/(d*(e*Sec(c+d*x))^(3/2))");

	}

	// {(a+I*a*Tan(c+d*x))^(5/2)/(e*Sec(c+d*x))^(5/2), x, 1, (((-2*I)/5)*(a+I*a*Tan(c+d*x))^(5/2))/(d*(e*Sec(c
	// +d*x))^(5/2))}
	public void test02381() {
		check("Integrate((a+I*a*Tan(c+d*x))^(5/2)/(e*Sec(c+d*x))^(5/2), x)",
				"(((-2*I)/5)*(a+I*a*Tan(c+d*x))^(5/2))/(d*(e*Sec(c+d*x))^(5/2))");

	}

	// {Sqrt(e*Sec(c+d*x))/Sqrt(a+I*a*Tan(c+d*x)), x, 1, ((2*I)*Sqrt(e*Sec(c+d*x)))/(d*Sqrt(a+I*a*Tan(c +
	// d*x)))}
	public void test02382() {
		check("Integrate(Sqrt(e*Sec(c+d*x))/Sqrt(a+I*a*Tan(c+d*x)), x)",
				"((2*I)*Sqrt(e*Sec(c+d*x)))/(d*Sqrt(a+I*a*Tan(c+d*x)))");

	}

	// {(e*Sec(c+d*x))^(3/2)/(a+I*a*Tan(c+d*x))^(3/2), x, 1, (((2*I)/3)*(e*Sec(c+d*x))^(3/2))/(d*(a+I*a*Tan(c
	// +d*x))^(3/2))}
	public void test02383() {
		check("Integrate((e*Sec(c+d*x))^(3/2)/(a+I*a*Tan(c+d*x))^(3/2), x)",
				"(((2*I)/3)*(e*Sec(c+d*x))^(3/2))/(d*(a+I*a*Tan(c+d*x))^(3/2))");

	}

	// {(e*Sec(c+d*x))^(5/2)/(a+I*a*Tan(c+d*x))^(5/2), x, 1, (((2*I)/5)*(e*Sec(c+d*x))^(5/2))/(d*(a+I*a*Tan(c
	// +d*x))^(5/2))}
	public void test02384() {
		check("Integrate((e*Sec(c+d*x))^(5/2)/(a+I*a*Tan(c+d*x))^(5/2), x)",
				"(((2*I)/5)*(e*Sec(c+d*x))^(5/2))/(d*(a+I*a*Tan(c+d*x))^(5/2))");

	}

	// {(d*Sec(e+f*x))^(2/3)*(a+I*a*Tan(e+f*x))^(2/3), x, 1, ((3*I)*a*(d*Sec(e+f*x))^(2/3))/(f*(a+I*a*Tan(e +
	// f*x))^(1/3))}
	public void test02385() {
		check("Integrate((d*Sec(e+f*x))^(2/3)*(a+I*a*Tan(e+f*x))^(2/3), x)",
				"((3*I)*a*(d*Sec(e+f*x))^(2/3))/(f*(a+I*a*Tan(e+f*x))^(1/3))");

	}

	// {(a+I*a*Tan(c+d*x))^n/(e*Sec(c+d*x))^n, x, 1, ((-I)*(a+I*a*Tan(c+d*x))^n)/(d*n*(e*Sec(c+d*x))^n)}
	public void test02386() {
		check("Integrate((a+I*a*Tan(c+d*x))^n/(e*Sec(c+d*x))^n, x)",
				"((-I)*(a+I*a*Tan(c+d*x))^n)/(d*n*(e*Sec(c+d*x))^n)");

	}

	// {(e*Sec(c+d*x))^(2-2*n)*(a+I*a*Tan(c+d*x))^n, x, 1, (I*a*(e*Sec(c+d*x))^(2-2*n)*(a+I*a*Tan(c +
	// d*x))^(-1+n))/(d*(1-n))}
	public void test02387() {
		check("Integrate((e*Sec(c+d*x))^(2-2*n)*(a+I*a*Tan(c+d*x))^n, x)",
				"(I*a*(e*Sec(c+d*x))^(2-2*n)*(a+I*a*Tan(c+d*x))^(-1+n))/(d*(1-n))");

	}

	// {(d*Sec(e+f*x))^(2*n)*(a+I*a*Tan(e+f*x))^(1-n), x, 1, (I*a*(d*Sec(e+f*x))^(2*n))/(f*n*(a+I*a*Tan(e +
	// f*x))^n)}
	public void test02388() {
		check("Integrate((d*Sec(e+f*x))^(2*n)*(a+I*a*Tan(e+f*x))^(1-n), x)",
				"(I*a*(d*Sec(e+f*x))^(2*n))/(f*n*(a+I*a*Tan(e+f*x))^n)");

	}

	// {Cos(c+d*x)*(a+b*Tan(c+d*x))^2, x, 1, (b^2*ArcTanh(Sin(c+d*x)))/d-(2*a*b*Cos(c+d*x))/d+((a^2 -
	// b^2)*Sin(c+d*x))/d}
	public void test02389() {
		check("Integrate(Cos(c+d*x)*(a+b*Tan(c+d*x))^2, x)",
				"(b^2*ArcTanh(Sin(c+d*x)))/d-(2*a*b*Cos(c+d*x))/d+((a^2-b^2)*Sin(c+d*x))/d");

	}

	// {(d*Cos(e+f*x))^m*(a+b*(c*Tan(e+f*x))^n)^p, x, 1, (d*Cos(e+f*x))^m*(Sec(e+f*x)/d)^m*Unintegrable((a +
	// b*(c*Tan(e+f*x))^n)^p/(Sec(e+f*x)/d)^m, x)}
	public void test02390() {
		check("Integrate((d*Cos(e+f*x))^m*(a+b*(c*Tan(e+f*x))^n)^p, x)",
				"(d*Cos(e+f*x))^m*(Sec(e+f*x)/d)^m*Unintegrable((a+b*(c*Tan(e+f*x))^n)^p/(Sec(e+f*x)/d)^m, x)");

	}

	// {(d*Cot(e+f*x))^m*(a+b*(c*Tan(e+f*x))^n)^p, x, 1, (d*Cot(e+f*x))^m*(Tan(e+f*x)/d)^m*Unintegrable((a +
	// b*(c*Tan(e+f*x))^n)^p/(Tan(e+f*x)/d)^m, x)}
	public void test02391() {
		check("Integrate((d*Cot(e+f*x))^m*(a+b*(c*Tan(e+f*x))^n)^p, x)",
				"(d*Cot(e+f*x))^m*(Tan(e+f*x)/d)^m*Unintegrable((a+b*(c*Tan(e+f*x))^n)^p/(Tan(e+f*x)/d)^m, x)");

	}

	// {(d*Csc(e+f*x))^m*(a+b*(c*Tan(e+f*x))^n)^p, x, 1, (d*Csc(e+f*x))^m*(Sin(e+f*x)/d)^m*Unintegrable((a +
	// b*(c*Tan(e+f*x))^n)^p/(Sin(e+f*x)/d)^m, x)}
	public void test02392() {
		check("Integrate((d*Csc(e+f*x))^m*(a+b*(c*Tan(e+f*x))^n)^p, x)",
				"(d*Csc(e+f*x))^m*(Sin(e+f*x)/d)^m*Unintegrable((a+b*(c*Tan(e+f*x))^n)^p/(Sin(e+f*x)/d)^m, x)");

	}

	// {Cot(a+b*x), x, 1, Log(Sin(a+b*x))/b}
	public void test02393() {
		check("Integrate(Cot(a+b*x), x)", "Log(Sin(a+b*x))/b");

	}

	// {(d*Cot(e+f*x))^n*Csc(e+f*x)^3, x, 1, -(((d*Cot(e+f*x))^(1+n)*Csc(e+f*x)^3*Hypergeometric2F1((1+n)/2,
	// (4+n)/2, (3+n)/2, Cos(e+f*x)^2)*(Sin(e+f*x)^2)^((4+n)/2))/(d*f*(1+n)))}
	public void test02394() {
		check("Integrate((d*Cot(e+f*x))^n*Csc(e+f*x)^3, x)",
				"-(((d*Cot(e+f*x))^(1+n)*Csc(e+f*x)^3*Hypergeometric2F1((1+n)/2, (4+n)/2, (3+n)/2, Cos(e+f*x)^2)*(Sin(e+f*x)^2)^((4+n)/2))/(d*f*(1+n)))");

	}

	// {(d*Cot(e+f*x))^n*Csc(e+f*x), x, 1, -(((d*Cot(e+f*x))^(1+n)*Csc(e+f*x)*Hypergeometric2F1((1+n)/2, (2
	// +n)/2, (3+n)/2, Cos(e+f*x)^2)*(Sin(e+f*x)^2)^((2+n)/2))/(d*f*(1+n)))}
	public void test02395() {
		check("Integrate((d*Cot(e+f*x))^n*Csc(e+f*x), x)",
				"-(((d*Cot(e+f*x))^(1+n)*Csc(e+f*x)*Hypergeometric2F1((1+n)/2, (2+n)/2, (3+n)/2, Cos(e+f*x)^2)*(Sin(e+f*x)^2)^((2+n)/2))/(d*f*(1+n)))");

	}

	// {(d*Cot(e+f*x))^n*Sin(e+f*x), x, 1, -(((d*Cot(e+f*x))^(1+n)*Hypergeometric2F1(n/2, (1+n)/2, (3+n)/2,
	// Cos(e+f*x)^2)*Sin(e+f*x)*(Sin(e+f*x)^2)^(n/2))/(d*f*(1+n)))}
	public void test02396() {
		check("Integrate((d*Cot(e+f*x))^n*Sin(e+f*x), x)",
				"-(((d*Cot(e+f*x))^(1+n)*Hypergeometric2F1(n/2, (1+n)/2, (3+n)/2, Cos(e+f*x)^2)*Sin(e+f*x)*(Sin(e+f*x)^2)^(n/2))/(d*f*(1+n)))");

	}

	// {(d*Cot(e+f*x))^n*Sin(e+f*x)^3, x, 1, -(((d*Cot(e+f*x))^(1+n)*Hypergeometric2F1((-2+n)/2, (1+n)/2, (3
	// +n)/2, Cos(e+f*x)^2)*Sin(e+f*x)^3*(Sin(e+f*x)^2)^((-2+n)/2))/(d*f*(1+n)))}
	public void test02397() {
		check("Integrate((d*Cot(e+f*x))^n*Sin(e+f*x)^3, x)",
				"-(((d*Cot(e+f*x))^(1+n)*Hypergeometric2F1((-2+n)/2, (1+n)/2, (3+n)/2, Cos(e+f*x)^2)*Sin(e+f*x)^3*(Sin(e+f*x)^2)^((-2+n)/2))/(d*f*(1+n)))");

	}

	// {(b*Cot(e+f*x))^n*(a*Csc(e+f*x))^m, x, 1, -(((b*Cot(e+f*x))^(1+n)*(a*Csc(e+f*x))^m*Hypergeometric2F1((1
	// +n)/2, (1+m+n)/2, (3+n)/2, Cos(e+f*x)^2)*(Sin(e+f*x)^2)^((1+m+n)/2))/(b*f*(1+n)))}
	public void test02398() {
		check("Integrate((b*Cot(e+f*x))^n*(a*Csc(e+f*x))^m, x)",
				"-(((b*Cot(e+f*x))^(1+n)*(a*Csc(e+f*x))^m*Hypergeometric2F1((1+n)/2, (1+m+n)/2, (3+n)/2, Cos(e+f*x)^2)*(Sin(e+f*x)^2)^((1+m+n)/2))/(b*f*(1+n)))");

	}

	// {Csc(x)/(I+Cot(x)), x, 1, (I*Csc(x))/(I+Cot(x))}
	public void test02399() {
		check("Integrate(Csc(x)/(I+Cot(x)), x)", "(I*Csc(x))/(I+Cot(x))");

	}

	// {Sec(a+b*x), x, 1, ArcTanh(Sin(a+b*x))/b}
	public void test02400() {
		check("Integrate(Sec(a+b*x), x)", "ArcTanh(Sin(a+b*x))/b");

	}

	// {(d*Csc(a+b*x))^(3/2)*Sqrt(c*Sec(a+b*x)), x, 1, (-2*c*d*Sqrt(d*Csc(a+b*x)))/(b*Sqrt(c*Sec(a+b*x)))}
	public void test02401() {
		check("Integrate((d*Csc(a+b*x))^(3/2)*Sqrt(c*Sec(a+b*x)), x)",
				"(-2*c*d*Sqrt(d*Csc(a+b*x)))/(b*Sqrt(c*Sec(a+b*x)))");

	}

	// {Sqrt(d*Csc(a+b*x))*(c*Sec(a+b*x))^(3/2), x, 1, (2*c*d*Sqrt(c*Sec(a+b*x)))/(b*Sqrt(d*Csc(a+b*x)))}
	public void test02402() {
		check("Integrate(Sqrt(d*Csc(a+b*x))*(c*Sec(a+b*x))^(3/2), x)",
				"(2*c*d*Sqrt(c*Sec(a+b*x)))/(b*Sqrt(d*Csc(a+b*x)))");

	}

	// {(c*Sec(a+b*x))^(5/2)/Sqrt(d*Csc(a+b*x)), x, 1, (2*c*d*(c*Sec(a+b*x))^(3/2))/(3*b*(d*Csc(a+b*x))^(3/2))}
	public void test02403() {
		check("Integrate((c*Sec(a+b*x))^(5/2)/Sqrt(d*Csc(a+b*x)), x)",
				"(2*c*d*(c*Sec(a+b*x))^(3/2))/(3*b*(d*Csc(a+b*x))^(3/2))");

	}

	// {(d*Csc(a+b*x))^(5/2)/Sqrt(c*Sec(a+b*x)), x, 1, (-2*c*d*(d*Csc(a+b*x))^(3/2))/(3*b*(c*Sec(a+b*x))^(3/2))}
	public void test02404() {
		check("Integrate((d*Csc(a+b*x))^(5/2)/Sqrt(c*Sec(a+b*x)), x)",
				"(-2*c*d*(d*Csc(a+b*x))^(3/2))/(3*b*(c*Sec(a+b*x))^(3/2))");

	}

	// {(d*Csc(a+b*x))^(7/2)/(c*Sec(a+b*x))^(3/2), x, 1, (-2*c*d*(d*Csc(a+b*x))^(5/2))/(5*b*(c*Sec(a +
	// b*x))^(5/2))}
	public void test02405() {
		check("Integrate((d*Csc(a+b*x))^(7/2)/(c*Sec(a+b*x))^(3/2), x)",
				"(-2*c*d*(d*Csc(a+b*x))^(5/2))/(5*b*(c*Sec(a+b*x))^(5/2))");

	}

	// {(d*Csc(a+b*x))^(9/2)/(c*Sec(a+b*x))^(5/2), x, 1, (-2*c*d*(d*Csc(a+b*x))^(7/2))/(7*b*(c*Sec(a +
	// b*x))^(7/2))}
	public void test02406() {
		check("Integrate((d*Csc(a+b*x))^(9/2)/(c*Sec(a+b*x))^(5/2), x)",
				"(-2*c*d*(d*Csc(a+b*x))^(7/2))/(7*b*(c*Sec(a+b*x))^(7/2))");

	}

	// {(e*x)^m*(a+b*Sec(c+d*x^n))^p, x, 1, ((e*x)^m*Unintegrable(x^m*(a+b*Sec(c+d*x^n))^p, x))/x^m}
	public void test02407() {
		check("Integrate((e*x)^m*(a+b*Sec(c+d*x^n))^p, x)", "((e*x)^m*Unintegrable(x^m*(a+b*Sec(c+d*x^n))^p, x))/x^m");

	}

	// {Sec(c+d*x)/(a+a*Sec(c+d*x)), x, 1, Tan(c+d*x)/(d*(a+a*Sec(c+d*x)))}
	public void test02408() {
		check("Integrate(Sec(c+d*x)/(a+a*Sec(c+d*x)), x)", "Tan(c+d*x)/(d*(a+a*Sec(c+d*x)))");

	}

	// {Sec(c+d*x)*Sqrt(a+a*Sec(c+d*x)), x, 1, (2*a*Tan(c+d*x))/(d*Sqrt(a+a*Sec(c+d*x)))}
	public void test02409() {
		check("Integrate(Sec(c+d*x)*Sqrt(a+a*Sec(c+d*x)), x)", "(2*a*Tan(c+d*x))/(d*Sqrt(a+a*Sec(c+d*x)))");

	}

	// {Sec(c+d*x)*Sqrt(a-a*Sec(c+d*x)), x, 1, (-2*a*Tan(c+d*x))/(d*Sqrt(a-a*Sec(c+d*x)))}
	public void test02410() {
		check("Integrate(Sec(c+d*x)*Sqrt(a-a*Sec(c+d*x)), x)", "(-2*a*Tan(c+d*x))/(d*Sqrt(a-a*Sec(c+d*x)))");

	}

	// {Sqrt(a+a*Sec(c+d*x))/Sqrt(Sec(c+d*x)), x, 1, (2*a*Sqrt(Sec(c+d*x))*Sin(c+d*x))/(d*Sqrt(a+a*Sec(c +
	// d*x)))}
	public void test02411() {
		check("Integrate(Sqrt(a+a*Sec(c+d*x))/Sqrt(Sec(c+d*x)), x)",
				"(2*a*Sqrt(Sec(c+d*x))*Sin(c+d*x))/(d*Sqrt(a+a*Sec(c+d*x)))");

	}

	// {Sqrt(a+b*Sec(c+d*x)), x, 1, (-2*Cot(c+d*x)*EllipticPi(a/(a+b), ArcSin(Sqrt(a+b)/Sqrt(a+b*Sec(c +
	// d*x))), (a-b)/(a+b))*Sqrt(-((b*(1-Sec(c+d*x)))/(a+b*Sec(c+d*x))))*Sqrt((b*(1+Sec(c+d*x)))/(a +
	// b*Sec(c+d*x)))*(a+b*Sec(c+d*x)))/(Sqrt(a+b)*d)}
	public void test02412() {
		check("Integrate(Sqrt(a+b*Sec(c+d*x)), x)",
				"(-2*Cot(c+d*x)*EllipticPi(a/(a+b), ArcSin(Sqrt(a+b)/Sqrt(a+b*Sec(c+d*x))), (a-b)/(a+b))*Sqrt(-((b*(1-Sec(c+d*x)))/(a+b*Sec(c+d*x))))*Sqrt((b*(1+Sec(c+d*x)))/(a+b*Sec(c+d*x)))*(a+b*Sec(c+d*x)))/(Sqrt(a+b)*d)");

	}

	// {Sec(c+d*x)/Sqrt(a+b*Sec(c+d*x)), x, 1, (2*Sqrt(a+b)*Cot(c+d*x)*EllipticF(ArcSin(Sqrt(a+b*Sec(c +
	// d*x))/Sqrt(a+b)), (a+b)/(a-b))*Sqrt((b*(1-Sec(c+d*x)))/(a+b))*Sqrt(-((b*(1+Sec(c+d*x)))/(a -
	// b))))/(b*d)}
	public void test02413() {
		check("Integrate(Sec(c+d*x)/Sqrt(a+b*Sec(c+d*x)), x)",
				"(2*Sqrt(a+b)*Cot(c+d*x)*EllipticF(ArcSin(Sqrt(a+b*Sec(c+d*x))/Sqrt(a+b)), (a+b)/(a-b))*Sqrt((b*(1-Sec(c+d*x)))/(a+b))*Sqrt(-((b*(1+Sec(c+d*x)))/(a-b))))/(b*d)");

	}

	// {1/Sqrt(a+b*Sec(c+d*x)), x, 1, (-2*Sqrt(a+b)*Cot(c+d*x)*EllipticPi((a+b)/a, ArcSin(Sqrt(a+b*Sec(c +
	// d*x))/Sqrt(a+b)), (a+b)/(a-b))*Sqrt((b*(1-Sec(c+d*x)))/(a+b))*Sqrt(-((b*(1+Sec(c+d*x)))/(a -
	// b))))/(a*d)}
	public void test02414() {
		check("Integrate(1/Sqrt(a+b*Sec(c+d*x)), x)",
				"(-2*Sqrt(a+b)*Cot(c+d*x)*EllipticPi((a+b)/a, ArcSin(Sqrt(a+b*Sec(c+d*x))/Sqrt(a+b)), (a+b)/(a-b))*Sqrt((b*(1-Sec(c+d*x)))/(a+b))*Sqrt(-((b*(1+Sec(c+d*x)))/(a-b))))/(a*d)");

	}

	// {Sqrt(a+b*Sec(e+f*x)), x, 1, (-2*Cot(e+f*x)*EllipticPi(a/(a+b), ArcSin(Sqrt(a+b)/Sqrt(a+b*Sec(e +
	// f*x))), (a-b)/(a+b))*Sqrt(-((b*(1-Sec(e+f*x)))/(a+b*Sec(e+f*x))))*Sqrt((b*(1+Sec(e+f*x)))/(a +
	// b*Sec(e+f*x)))*(a+b*Sec(e+f*x)))/(Sqrt(a+b)*f)}
	public void test02415() {
		check("Integrate(Sqrt(a+b*Sec(e+f*x)), x)",
				"(-2*Cot(e+f*x)*EllipticPi(a/(a+b), ArcSin(Sqrt(a+b)/Sqrt(a+b*Sec(e+f*x))), (a-b)/(a+b))*Sqrt(-((b*(1-Sec(e+f*x)))/(a+b*Sec(e+f*x))))*Sqrt((b*(1+Sec(e+f*x)))/(a+b*Sec(e+f*x)))*(a+b*Sec(e+f*x)))/(Sqrt(a+b)*f)");

	}

	// {1/Sqrt(a+b*Sec(e+f*x)), x, 1, (-2*Sqrt(a+b)*Cot(e+f*x)*EllipticPi((a+b)/a, ArcSin(Sqrt(a+b*Sec(e +
	// f*x))/Sqrt(a+b)), (a+b)/(a-b))*Sqrt((b*(1-Sec(e+f*x)))/(a+b))*Sqrt(-((b*(1+Sec(e+f*x)))/(a -
	// b))))/(a*f)}
	public void test02416() {
		check("Integrate(1/Sqrt(a+b*Sec(e+f*x)), x)",
				"(-2*Sqrt(a+b)*Cot(e+f*x)*EllipticPi((a+b)/a, ArcSin(Sqrt(a+b*Sec(e+f*x))/Sqrt(a+b)), (a+b)/(a-b))*Sqrt((b*(1-Sec(e+f*x)))/(a+b))*Sqrt(-((b*(1+Sec(e+f*x)))/(a-b))))/(a*f)");

	}

	// {(a+a*Sec(c+d*x))^n*(e*Tan(c+d*x))^m, x, 1, (2^(1+m+n)*AppellF1((1+m)/2, m+n, 1, (3+m)/2, -((a -
	// a*Sec(c+d*x))/(a+a*Sec(c+d*x))), (a-a*Sec(c+d*x))/(a+a*Sec(c+d*x)))*((1+Sec(c+d*x))^(-1))^(1 +
	// m+n)*(a+a*Sec(c+d*x))^n*(e*Tan(c+d*x))^(1+m))/(d*e*(1+m))}
	public void test02417() {
		check("Integrate((a+a*Sec(c+d*x))^n*(e*Tan(c+d*x))^m, x)",
				"(2^(1+m+n)*AppellF1((1+m)/2, m+n, 1, (3+m)/2, -((a-a*Sec(c+d*x))/(a+a*Sec(c+d*x))), (a-a*Sec(c+d*x))/(a+a*Sec(c+d*x)))*((1+Sec(c+d*x))^(-1))^(1+m+n)*(a+a*Sec(c+d*x))^n*(e*Tan(c+d*x))^(1+m))/(d*e*(1+m))");

	}

	// {(a+a*Sec(c+d*x))^(3/2)*(e*Tan(c+d*x))^m, x, 1, (2^(5/2+m)*AppellF1((1+m)/2, 3/2+m, 1, (3+m)/2,
	// -((a-a*Sec(c+d*x))/(a+a*Sec(c+d*x))), (a-a*Sec(c+d*x))/(a+a*Sec(c+d*x)))*((1+Sec(c +
	// d*x))^(-1))^(5/2+m)*(a+a*Sec(c+d*x))^(3/2)*(e*Tan(c+d*x))^(1+m))/(d*e*(1+m))}
	public void test02418() {
		check("Integrate((a+a*Sec(c+d*x))^(3/2)*(e*Tan(c+d*x))^m, x)",
				"(2^(5/2+m)*AppellF1((1+m)/2, 3/2+m, 1, (3+m)/2, -((a-a*Sec(c+d*x))/(a+a*Sec(c+d*x))), (a-a*Sec(c+d*x))/(a+a*Sec(c+d*x)))*((1+Sec(c+d*x))^(-1))^(5/2+m)*(a+a*Sec(c+d*x))^(3/2)*(e*Tan(c+d*x))^(1+m))/(d*e*(1+m))");

	}

	// {Sqrt(a+a*Sec(c+d*x))*(e*Tan(c+d*x))^m, x, 1, (2^(3/2+m)*AppellF1((1+m)/2, 1/2+m, 1, (3+m)/2, -((a
	// -a*Sec(c+d*x))/(a+a*Sec(c+d*x))), (a-a*Sec(c+d*x))/(a+a*Sec(c+d*x)))*((1+Sec(c +
	// d*x))^(-1))^(3/2+m)*Sqrt(a+a*Sec(c+d*x))*(e*Tan(c+d*x))^(1+m))/(d*e*(1+m))}
	public void test02419() {
		check("Integrate(Sqrt(a+a*Sec(c+d*x))*(e*Tan(c+d*x))^m, x)",
				"(2^(3/2+m)*AppellF1((1+m)/2, 1/2+m, 1, (3+m)/2, -((a-a*Sec(c+d*x))/(a+a*Sec(c+d*x))), (a-a*Sec(c+d*x))/(a+a*Sec(c+d*x)))*((1+Sec(c+d*x))^(-1))^(3/2+m)*Sqrt(a+a*Sec(c+d*x))*(e*Tan(c+d*x))^(1+m))/(d*e*(1+m))");

	}

	// {(e*Tan(c+d*x))^m/Sqrt(a+a*Sec(c+d*x)), x, 1, (2^(1/2+m)*AppellF1((1+m)/2, -1/2+m, 1, (3+m)/2, -((a
	// -a*Sec(c+d*x))/(a+a*Sec(c+d*x))), (a-a*Sec(c+d*x))/(a+a*Sec(c+d*x)))*((1+Sec(c +
	// d*x))^(-1))^(1/2+m)*(e*Tan(c+d*x))^(1+m))/(d*e*(1+m)*Sqrt(a+a*Sec(c+d*x)))}
	public void test02420() {
		check("Integrate((e*Tan(c+d*x))^m/Sqrt(a+a*Sec(c+d*x)), x)",
				"(2^(1/2+m)*AppellF1((1+m)/2, -1/2+m, 1, (3+m)/2, -((a-a*Sec(c+d*x))/(a+a*Sec(c+d*x))), (a-a*Sec(c+d*x))/(a+a*Sec(c+d*x)))*((1+Sec(c+d*x))^(-1))^(1/2+m)*(e*Tan(c+d*x))^(1+m))/(d*e*(1+m)*Sqrt(a+a*Sec(c+d*x)))");

	}

	// {(e*Tan(c+d*x))^m/(a+a*Sec(c+d*x))^(3/2), x, 1, (2^(-1/2+m)*AppellF1((1+m)/2, -3/2+m, 1, (3+m)/2,
	// -((a-a*Sec(c+d*x))/(a+a*Sec(c+d*x))), (a-a*Sec(c+d*x))/(a+a*Sec(c+d*x)))*((1+Sec(c +
	// d*x))^(-1))^(-1/2+m)*(e*Tan(c+d*x))^(1+m))/(d*e*(1+m)*(a+a*Sec(c+d*x))^(3/2))}
	public void test02421() {
		check("Integrate((e*Tan(c+d*x))^m/(a+a*Sec(c+d*x))^(3/2), x)",
				"(2^(-1/2+m)*AppellF1((1+m)/2, -3/2+m, 1, (3+m)/2, -((a-a*Sec(c+d*x))/(a+a*Sec(c+d*x))), (a-a*Sec(c+d*x))/(a+a*Sec(c+d*x)))*((1+Sec(c+d*x))^(-1))^(-1/2+m)*(e*Tan(c+d*x))^(1+m))/(d*e*(1+m)*(a+a*Sec(c+d*x))^(3/2))");

	}

	// {(a+a*Sec(c+d*x))^n*Tan(c+d*x)^4, x, 1, (2^(5+n)*AppellF1(5/2, 4+n, 1, 7/2, -((a-a*Sec(c+d*x))/(a +
	// a*Sec(c+d*x))), (a-a*Sec(c+d*x))/(a+a*Sec(c+d*x)))*((1+Sec(c+d*x))^(-1))^(5+n)*(a+a*Sec(c +
	// d*x))^n*Tan(c+d*x)^5)/(5*d)}
	public void test02422() {
		check("Integrate((a+a*Sec(c+d*x))^n*Tan(c+d*x)^4, x)",
				"(2^(5+n)*AppellF1(5/2, 4+n, 1, 7/2, -((a-a*Sec(c+d*x))/(a+a*Sec(c+d*x))), (a-a*Sec(c+d*x))/(a+a*Sec(c+d*x)))*((1+Sec(c+d*x))^(-1))^(5+n)*(a+a*Sec(c+d*x))^n*Tan(c+d*x)^5)/(5*d)");

	}

	// {(a+a*Sec(c+d*x))^n*Tan(c+d*x)^2, x, 1, (2^(3+n)*AppellF1(3/2, 2+n, 1, 5/2, -((a-a*Sec(c+d*x))/(a +
	// a*Sec(c+d*x))), (a-a*Sec(c+d*x))/(a+a*Sec(c+d*x)))*((1+Sec(c+d*x))^(-1))^(3+n)*(a+a*Sec(c +
	// d*x))^n*Tan(c+d*x)^3)/(3*d)}
	public void test02423() {
		check("Integrate((a+a*Sec(c+d*x))^n*Tan(c+d*x)^2, x)",
				"(2^(3+n)*AppellF1(3/2, 2+n, 1, 5/2, -((a-a*Sec(c+d*x))/(a+a*Sec(c+d*x))), (a-a*Sec(c+d*x))/(a+a*Sec(c+d*x)))*((1+Sec(c+d*x))^(-1))^(3+n)*(a+a*Sec(c+d*x))^n*Tan(c+d*x)^3)/(3*d)");

	}

	// {Cot(c+d*x)^2*(a+a*Sec(c+d*x))^n, x, 1, -((2^(-1+n)*AppellF1(-1/2, -2+n, 1, 1/2, -((a-a*Sec(c +
	// d*x))/(a+a*Sec(c+d*x))), (a-a*Sec(c+d*x))/(a+a*Sec(c+d*x)))*Cot(c+d*x)*((1+Sec(c +
	// d*x))^(-1))^(-1+n)*(a+a*Sec(c+d*x))^n)/d)}
	public void test02424() {
		check("Integrate(Cot(c+d*x)^2*(a+a*Sec(c+d*x))^n, x)",
				"-((2^(-1+n)*AppellF1(-1/2, -2+n, 1, 1/2, -((a-a*Sec(c+d*x))/(a+a*Sec(c+d*x))), (a-a*Sec(c+d*x))/(a+a*Sec(c+d*x)))*Cot(c+d*x)*((1+Sec(c+d*x))^(-1))^(-1+n)*(a+a*Sec(c+d*x))^n)/d)");

	}

	// {Cot(c+d*x)^4*(a+a*Sec(c+d*x))^n, x, 1, -(2^(-3+n)*AppellF1(-3/2, -4+n, 1, -1/2, -((a-a*Sec(c +
	// d*x))/(a+a*Sec(c+d*x))), (a-a*Sec(c+d*x))/(a+a*Sec(c+d*x)))*Cot(c+d*x)^3*((1+Sec(c +
	// d*x))^(-1))^(-3+n)*(a+a*Sec(c+d*x))^n)/(3*d)}
	public void test02425() {
		check("Integrate(Cot(c+d*x)^4*(a+a*Sec(c+d*x))^n, x)",
				"-(2^(-3+n)*AppellF1(-3/2, -4+n, 1, -1/2, -((a-a*Sec(c+d*x))/(a+a*Sec(c+d*x))), (a-a*Sec(c+d*x))/(a+a*Sec(c+d*x)))*Cot(c+d*x)^3*((1+Sec(c+d*x))^(-1))^(-3+n)*(a+a*Sec(c+d*x))^n)/(3*d)");

	}

	// {(a+a*Sec(c+d*x))^n*Tan(c+d*x)^(3/2), x, 1, (2^(7/2+n)*AppellF1(5/4, 3/2+n, 1, 9/4, -((a-a*Sec(c +
	// d*x))/(a+a*Sec(c+d*x))), (a-a*Sec(c+d*x))/(a+a*Sec(c+d*x)))*((1+Sec(c+d*x))^(-1))^(5/2+n)*(a +
	// a*Sec(c+d*x))^n*Tan(c+d*x)^(5/2))/(5*d)}
	public void test02426() {
		check("Integrate((a+a*Sec(c+d*x))^n*Tan(c+d*x)^(3/2), x)",
				"(2^(7/2+n)*AppellF1(5/4, 3/2+n, 1, 9/4, -((a-a*Sec(c+d*x))/(a+a*Sec(c+d*x))), (a-a*Sec(c+d*x))/(a+a*Sec(c+d*x)))*((1+Sec(c+d*x))^(-1))^(5/2+n)*(a+a*Sec(c+d*x))^n*Tan(c+d*x)^(5/2))/(5*d)");

	}

	// {(a+a*Sec(c+d*x))^n*Sqrt(Tan(c+d*x)), x, 1, (2^(5/2+n)*AppellF1(3/4, 1/2+n, 1, 7/4, -((a-a*Sec(c +
	// d*x))/(a+a*Sec(c+d*x))), (a-a*Sec(c+d*x))/(a+a*Sec(c+d*x)))*((1+Sec(c+d*x))^(-1))^(3/2+n)*(a +
	// a*Sec(c+d*x))^n*Tan(c+d*x)^(3/2))/(3*d)}
	public void test02427() {
		check("Integrate((a+a*Sec(c+d*x))^n*Sqrt(Tan(c+d*x)), x)",
				"(2^(5/2+n)*AppellF1(3/4, 1/2+n, 1, 7/4, -((a-a*Sec(c+d*x))/(a+a*Sec(c+d*x))), (a-a*Sec(c+d*x))/(a+a*Sec(c+d*x)))*((1+Sec(c+d*x))^(-1))^(3/2+n)*(a+a*Sec(c+d*x))^n*Tan(c+d*x)^(3/2))/(3*d)");

	}

	// {(a+a*Sec(c+d*x))^n/Sqrt(Tan(c+d*x)), x, 1, (2^(3/2+n)*AppellF1(1/4, -1/2+n, 1, 5/4, -((a-a*Sec(c +
	// d*x))/(a+a*Sec(c+d*x))), (a-a*Sec(c+d*x))/(a+a*Sec(c+d*x)))*((1+Sec(c+d*x))^(-1))^(1/2+n)*(a +
	// a*Sec(c+d*x))^n*Sqrt(Tan(c+d*x)))/d}
	public void test02428() {
		check("Integrate((a+a*Sec(c+d*x))^n/Sqrt(Tan(c+d*x)), x)",
				"(2^(3/2+n)*AppellF1(1/4, -1/2+n, 1, 5/4, -((a-a*Sec(c+d*x))/(a+a*Sec(c+d*x))), (a-a*Sec(c+d*x))/(a+a*Sec(c+d*x)))*((1+Sec(c+d*x))^(-1))^(1/2+n)*(a+a*Sec(c+d*x))^n*Sqrt(Tan(c+d*x)))/d");

	}

	// {(a+a*Sec(c+d*x))^n/Tan(c+d*x)^(3/2), x, 1, -((2^(1/2+n)*AppellF1(-1/4, -3/2+n, 1, 3/4, -((a-a*Sec(c
	// +d*x))/(a+a*Sec(c+d*x))), (a-a*Sec(c+d*x))/(a+a*Sec(c+d*x)))*((1+Sec(c+d*x))^(-1))^(-1/2+n)*(a
	// +a*Sec(c+d*x))^n)/(d*Sqrt(Tan(c+d*x))))}
	public void test02429() {
		check("Integrate((a+a*Sec(c+d*x))^n/Tan(c+d*x)^(3/2), x)",
				"-((2^(1/2+n)*AppellF1(-1/4, -3/2+n, 1, 3/4, -((a-a*Sec(c+d*x))/(a+a*Sec(c+d*x))), (a-a*Sec(c+d*x))/(a+a*Sec(c+d*x)))*((1+Sec(c+d*x))^(-1))^(-1/2+n)*(a+a*Sec(c+d*x))^n)/(d*Sqrt(Tan(c+d*x))))");

	}

	// {Sqrt(a+b*Sec(c+d*x)), x, 1, (-2*Cot(c+d*x)*EllipticPi(a/(a+b), ArcSin(Sqrt(a+b)/Sqrt(a+b*Sec(c +
	// d*x))), (a-b)/(a+b))*Sqrt(-((b*(1-Sec(c+d*x)))/(a+b*Sec(c+d*x))))*Sqrt((b*(1+Sec(c+d*x)))/(a +
	// b*Sec(c+d*x)))*(a+b*Sec(c+d*x)))/(Sqrt(a+b)*d)}
	public void test02430() {
		check("Integrate(Sqrt(a+b*Sec(c+d*x)), x)",
				"(-2*Cot(c+d*x)*EllipticPi(a/(a+b), ArcSin(Sqrt(a+b)/Sqrt(a+b*Sec(c+d*x))), (a-b)/(a+b))*Sqrt(-((b*(1-Sec(c+d*x)))/(a+b*Sec(c+d*x))))*Sqrt((b*(1+Sec(c+d*x)))/(a+b*Sec(c+d*x)))*(a+b*Sec(c+d*x)))/(Sqrt(a+b)*d)");

	}

	// {1/Sqrt(a+b*Sec(c+d*x)), x, 1, (-2*Sqrt(a+b)*Cot(c+d*x)*EllipticPi((a+b)/a, ArcSin(Sqrt(a+b*Sec(c +
	// d*x))/Sqrt(a+b)), (a+b)/(a-b))*Sqrt((b*(1-Sec(c+d*x)))/(a+b))*Sqrt(-((b*(1+Sec(c+d*x)))/(a -
	// b))))/(a*d)}
	public void test02431() {
		check("Integrate(1/Sqrt(a+b*Sec(c+d*x)), x)",
				"(-2*Sqrt(a+b)*Cot(c+d*x)*EllipticPi((a+b)/a, ArcSin(Sqrt(a+b*Sec(c+d*x))/Sqrt(a+b)), (a+b)/(a-b))*Sqrt((b*(1-Sec(c+d*x)))/(a+b))*Sqrt(-((b*(1+Sec(c+d*x)))/(a-b))))/(a*d)");

	}

	// {Sqrt(a+b*Sec(e+f*x))/Sqrt(c+d*Sec(e+f*x)), x, 1, (-2*Sqrt(c+d)*Cot(e+f*x)*EllipticPi((a*(c+d))/((a
	// +b)*c), ArcSin((Sqrt(a+b)*Sqrt(c+d*Sec(e+f*x)))/(Sqrt(c+d)*Sqrt(a+b*Sec(e+f*x)))), ((a-b)*(c +
	// d))/((a+b)*(c-d)))*Sqrt(-(((b*c-a*d)*(1-Sec(e+f*x)))/((c+d)*(a+b*Sec(e+f*x)))))*Sqrt(((b*c -
	// a*d)*(1+Sec(e+f*x)))/((c-d)*(a+b*Sec(e+f*x))))*(a+b*Sec(e+f*x)))/(Sqrt(a+b)*c*f)}
	public void test02432() {
		check("Integrate(Sqrt(a+b*Sec(e+f*x))/Sqrt(c+d*Sec(e+f*x)), x)",
				"(-2*Sqrt(c+d)*Cot(e+f*x)*EllipticPi((a*(c+d))/((a+b)*c), ArcSin((Sqrt(a+b)*Sqrt(c+d*Sec(e+f*x)))/(Sqrt(c+d)*Sqrt(a+b*Sec(e+f*x)))), ((a-b)*(c+d))/((a+b)*(c-d)))*Sqrt(-(((b*c-a*d)*(1-Sec(e+f*x)))/((c+d)*(a+b*Sec(e+f*x)))))*Sqrt(((b*c-a*d)*(1+Sec(e+f*x)))/((c-d)*(a+b*Sec(e+f*x))))*(a+b*Sec(e+f*x)))/(Sqrt(a+b)*c*f)");

	}

	// {Sqrt(c+d*Sec(e+f*x))/Sqrt(a+b*Sec(e+f*x)), x, 1, (-2*Sqrt(a+b)*Cot(e+f*x)*EllipticPi(((a +
	// b)*c)/(a*(c+d)), ArcSin((Sqrt(c+d)*Sqrt(a+b*Sec(e+f*x)))/(Sqrt(a+b)*Sqrt(c+d*Sec(e+f*x)))), ((a +
	// b)*(c-d))/((a-b)*(c+d)))*Sqrt(((b*c-a*d)*(1-Sec(e+f*x)))/((a+b)*(c+d*Sec(e+f*x))))*Sqrt(-(((b*c
	// -a*d)*(1+Sec(e+f*x)))/((a-b)*(c+d*Sec(e+f*x)))))*(c+d*Sec(e+f*x)))/(a*Sqrt(c+d)*f)}
	public void test02433() {
		check("Integrate(Sqrt(c+d*Sec(e+f*x))/Sqrt(a+b*Sec(e+f*x)), x)",
				"(-2*Sqrt(a+b)*Cot(e+f*x)*EllipticPi(((a+b)*c)/(a*(c+d)), ArcSin((Sqrt(c+d)*Sqrt(a+b*Sec(e+f*x)))/(Sqrt(a+b)*Sqrt(c+d*Sec(e+f*x)))), ((a+b)*(c-d))/((a-b)*(c+d)))*Sqrt(((b*c-a*d)*(1-Sec(e+f*x)))/((a+b)*(c+d*Sec(e+f*x))))*Sqrt(-(((b*c-a*d)*(1+Sec(e+f*x)))/((a-b)*(c+d*Sec(e+f*x)))))*(c+d*Sec(e+f*x)))/(a*Sqrt(c+d)*f)");

	}

	// {(a+b*Sec(e+f*x))^(1/3)/(c+d*Sec(e+f*x))^(1/3), x, 1, ((d+c*Cos(e+f*x))^(1/3)*(a+b*Sec(e +
	// f*x))^(1/3)*Unintegrable((b+a*Cos(e+f*x))^(1/3)/(d+c*Cos(e+f*x))^(1/3), x))/((b+a*Cos(e +
	// f*x))^(1/3)*(c+d*Sec(e+f*x))^(1/3))}
	public void test02434() {
		check("Integrate((a+b*Sec(e+f*x))^(1/3)/(c+d*Sec(e+f*x))^(1/3), x)",
				"((d+c*Cos(e+f*x))^(1/3)*(a+b*Sec(e+f*x))^(1/3)*Unintegrable((b+a*Cos(e+f*x))^(1/3)/(d+c*Cos(e+f*x))^(1/3), x))/((b+a*Cos(e+f*x))^(1/3)*(c+d*Sec(e+f*x))^(1/3))");

	}

	// {(a+b*Sec(e+f*x))^(2/3)/(c+d*Sec(e+f*x))^(2/3), x, 1, ((d+c*Cos(e+f*x))^(2/3)*(a+b*Sec(e +
	// f*x))^(2/3)*Unintegrable((b+a*Cos(e+f*x))^(2/3)/(d+c*Cos(e+f*x))^(2/3), x))/((b+a*Cos(e +
	// f*x))^(2/3)*(c+d*Sec(e+f*x))^(2/3))}
	public void test02435() {
		check("Integrate((a+b*Sec(e+f*x))^(2/3)/(c+d*Sec(e+f*x))^(2/3), x)",
				"((d+c*Cos(e+f*x))^(2/3)*(a+b*Sec(e+f*x))^(2/3)*Unintegrable((b+a*Cos(e+f*x))^(2/3)/(d+c*Cos(e+f*x))^(2/3), x))/((b+a*Cos(e+f*x))^(2/3)*(c+d*Sec(e+f*x))^(2/3))");

	}

	// {(a+b*Sec(e+f*x))^(4/3)/(c+d*Sec(e+f*x))^(4/3), x, 1, ((d+c*Cos(e+f*x))^(4/3)*(a+b*Sec(e +
	// f*x))^(4/3)*Unintegrable((b+a*Cos(e+f*x))^(4/3)/(d+c*Cos(e+f*x))^(4/3), x))/((b+a*Cos(e +
	// f*x))^(4/3)*(c+d*Sec(e+f*x))^(4/3))}
	public void test02436() {
		check("Integrate((a+b*Sec(e+f*x))^(4/3)/(c+d*Sec(e+f*x))^(4/3), x)",
				"((d+c*Cos(e+f*x))^(4/3)*(a+b*Sec(e+f*x))^(4/3)*Unintegrable((b+a*Cos(e+f*x))^(4/3)/(d+c*Cos(e+f*x))^(4/3), x))/((b+a*Cos(e+f*x))^(4/3)*(c+d*Sec(e+f*x))^(4/3))");

	}

	// {(c*(d*Sec(e+f*x))^p)^n*(a+b*Sec(e+f*x))^m, x, 1, ((c*(d*Sec(e+f*x))^p)^n*Unintegrable((d*Sec(e +
	// f*x))^(n*p)*(a+b*Sec(e+f*x))^m, x))/(d*Sec(e+f*x))^(n*p)}
	public void test02437() {
		check("Integrate((c*(d*Sec(e+f*x))^p)^n*(a+b*Sec(e+f*x))^m, x)",
				"((c*(d*Sec(e+f*x))^p)^n*Unintegrable((d*Sec(e+f*x))^(n*p)*(a+b*Sec(e+f*x))^m, x))/(d*Sec(e+f*x))^(n*p)");

	}

	// {(Sec(e+f*x)*(a+a*Sec(e+f*x)))/(c-c*Sec(e+f*x))^2, x, 1, -((a+a*Sec(e+f*x))*Tan(e+f*x))/(3*f*(c -
	// c*Sec(e+f*x))^2)}
	public void test02438() {
		check("Integrate((Sec(e+f*x)*(a+a*Sec(e+f*x)))/(c-c*Sec(e+f*x))^2, x)",
				"-((a+a*Sec(e+f*x))*Tan(e+f*x))/(3*f*(c-c*Sec(e+f*x))^2)");

	}

	// {(Sec(e+f*x)*(a+a*Sec(e+f*x))^2)/(c-c*Sec(e+f*x))^3, x, 1, -((a+a*Sec(e+f*x))^2*Tan(e +
	// f*x))/(5*f*(c-c*Sec(e+f*x))^3)}
	public void test02439() {
		check("Integrate((Sec(e+f*x)*(a+a*Sec(e+f*x))^2)/(c-c*Sec(e+f*x))^3, x)",
				"-((a+a*Sec(e+f*x))^2*Tan(e+f*x))/(5*f*(c-c*Sec(e+f*x))^3)");

	}

	// {(Sec(e+f*x)*(a+a*Sec(e+f*x))^3)/(c-c*Sec(e+f*x))^4, x, 1, -((a+a*Sec(e+f*x))^3*Tan(e +
	// f*x))/(7*f*(c-c*Sec(e+f*x))^4)}
	public void test02440() {
		check("Integrate((Sec(e+f*x)*(a+a*Sec(e+f*x))^3)/(c-c*Sec(e+f*x))^4, x)",
				"-((a+a*Sec(e+f*x))^3*Tan(e+f*x))/(7*f*(c-c*Sec(e+f*x))^4)");

	}

	// {(Sec(e+f*x)*(c-c*Sec(e+f*x)))/(a+a*Sec(e+f*x))^2, x, 1, ((c-c*Sec(e+f*x))*Tan(e+f*x))/(3*f*(a +
	// a*Sec(e+f*x))^2)}
	public void test02441() {
		check("Integrate((Sec(e+f*x)*(c-c*Sec(e+f*x)))/(a+a*Sec(e+f*x))^2, x)",
				"((c-c*Sec(e+f*x))*Tan(e+f*x))/(3*f*(a+a*Sec(e+f*x))^2)");

	}

	// {(Sec(e+f*x)*(c-c*Sec(e+f*x))^2)/(a+a*Sec(e+f*x))^3, x, 1, ((c-c*Sec(e+f*x))^2*Tan(e +
	// f*x))/(5*f*(a+a*Sec(e+f*x))^3)}
	public void test02442() {
		check("Integrate((Sec(e+f*x)*(c-c*Sec(e+f*x))^2)/(a+a*Sec(e+f*x))^3, x)",
				"((c-c*Sec(e+f*x))^2*Tan(e+f*x))/(5*f*(a+a*Sec(e+f*x))^3)");

	}

	// {Sec(e+f*x)*(a+a*Sec(e+f*x))*Sqrt(c-c*Sec(e+f*x)), x, 1, (-2*c*(a+a*Sec(e+f*x))*Tan(e +
	// f*x))/(3*f*Sqrt(c-c*Sec(e+f*x)))}
	public void test02443() {
		check("Integrate(Sec(e+f*x)*(a+a*Sec(e+f*x))*Sqrt(c-c*Sec(e+f*x)), x)",
				"(-2*c*(a+a*Sec(e+f*x))*Tan(e+f*x))/(3*f*Sqrt(c-c*Sec(e+f*x)))");

	}

	// {Sec(e+f*x)*(a+a*Sec(e+f*x))^2*Sqrt(c-c*Sec(e+f*x)), x, 1, (-2*c*(a+a*Sec(e+f*x))^2*Tan(e +
	// f*x))/(5*f*Sqrt(c-c*Sec(e+f*x)))}
	public void test02444() {
		check("Integrate(Sec(e+f*x)*(a+a*Sec(e+f*x))^2*Sqrt(c-c*Sec(e+f*x)), x)",
				"(-2*c*(a+a*Sec(e+f*x))^2*Tan(e+f*x))/(5*f*Sqrt(c-c*Sec(e+f*x)))");

	}

	// {Sec(e+f*x)*(a+a*Sec(e+f*x))^3*Sqrt(c-c*Sec(e+f*x)), x, 1, (-2*c*(a+a*Sec(e+f*x))^3*Tan(e +
	// f*x))/(7*f*Sqrt(c-c*Sec(e+f*x)))}
	public void test02445() {
		check("Integrate(Sec(e+f*x)*(a+a*Sec(e+f*x))^3*Sqrt(c-c*Sec(e+f*x)), x)",
				"(-2*c*(a+a*Sec(e+f*x))^3*Tan(e+f*x))/(7*f*Sqrt(c-c*Sec(e+f*x)))");

	}

	// {(Sec(e+f*x)*Sqrt(c-c*Sec(e+f*x)))/(a+a*Sec(e+f*x)), x, 1, (2*c*Tan(e+f*x))/(f*(a+a*Sec(e +
	// f*x))*Sqrt(c-c*Sec(e+f*x)))}
	public void test02446() {
		check("Integrate((Sec(e+f*x)*Sqrt(c-c*Sec(e+f*x)))/(a+a*Sec(e+f*x)), x)",
				"(2*c*Tan(e+f*x))/(f*(a+a*Sec(e+f*x))*Sqrt(c-c*Sec(e+f*x)))");

	}

	// {(Sec(e+f*x)*Sqrt(c-c*Sec(e+f*x)))/(a+a*Sec(e+f*x))^2, x, 1, (2*c*Tan(e+f*x))/(3*f*(a+a*Sec(e +
	// f*x))^2*Sqrt(c-c*Sec(e+f*x)))}
	public void test02447() {
		check("Integrate((Sec(e+f*x)*Sqrt(c-c*Sec(e+f*x)))/(a+a*Sec(e+f*x))^2, x)",
				"(2*c*Tan(e+f*x))/(3*f*(a+a*Sec(e+f*x))^2*Sqrt(c-c*Sec(e+f*x)))");

	}

	// {(Sec(e+f*x)*Sqrt(c-c*Sec(e+f*x)))/(a+a*Sec(e+f*x))^3, x, 1, (2*c*Tan(e+f*x))/(5*f*(a+a*Sec(e +
	// f*x))^3*Sqrt(c-c*Sec(e+f*x)))}
	public void test02448() {
		check("Integrate((Sec(e+f*x)*Sqrt(c-c*Sec(e+f*x)))/(a+a*Sec(e+f*x))^3, x)",
				"(2*c*Tan(e+f*x))/(5*f*(a+a*Sec(e+f*x))^3*Sqrt(c-c*Sec(e+f*x)))");

	}

	// {Sec(e+f*x)*Sqrt(a+a*Sec(e+f*x))*(c-c*Sec(e+f*x))^(5/2), x, 1, (a*(c-c*Sec(e+f*x))^(5/2)*Tan(e +
	// f*x))/(3*f*Sqrt(a+a*Sec(e+f*x)))}
	public void test02449() {
		check("Integrate(Sec(e+f*x)*Sqrt(a+a*Sec(e+f*x))*(c-c*Sec(e+f*x))^(5/2), x)",
				"(a*(c-c*Sec(e+f*x))^(5/2)*Tan(e+f*x))/(3*f*Sqrt(a+a*Sec(e+f*x)))");

	}

	// {Sec(e+f*x)*Sqrt(a+a*Sec(e+f*x))*(c-c*Sec(e+f*x))^(3/2), x, 1, (a*(c-c*Sec(e+f*x))^(3/2)*Tan(e +
	// f*x))/(2*f*Sqrt(a+a*Sec(e+f*x)))}
	public void test02450() {
		check("Integrate(Sec(e+f*x)*Sqrt(a+a*Sec(e+f*x))*(c-c*Sec(e+f*x))^(3/2), x)",
				"(a*(c-c*Sec(e+f*x))^(3/2)*Tan(e+f*x))/(2*f*Sqrt(a+a*Sec(e+f*x)))");

	}

	// {Sec(e+f*x)*Sqrt(a+a*Sec(e+f*x))*Sqrt(c-c*Sec(e+f*x)), x, 1, -((c*Sqrt(a+a*Sec(e+f*x))*Tan(e +
	// f*x))/(f*Sqrt(c-c*Sec(e+f*x))))}
	public void test02451() {
		check("Integrate(Sec(e+f*x)*Sqrt(a+a*Sec(e+f*x))*Sqrt(c-c*Sec(e+f*x)), x)",
				"-((c*Sqrt(a+a*Sec(e+f*x))*Tan(e+f*x))/(f*Sqrt(c-c*Sec(e+f*x))))");

	}

	// {(Sec(e+f*x)*Sqrt(a+a*Sec(e+f*x)))/Sqrt(c-c*Sec(e+f*x)), x, 1, (a*Log(1-Sec(e+f*x))*Tan(e +
	// f*x))/(f*Sqrt(a+a*Sec(e+f*x))*Sqrt(c-c*Sec(e+f*x)))}
	public void test02452() {
		check("Integrate((Sec(e+f*x)*Sqrt(a+a*Sec(e+f*x)))/Sqrt(c-c*Sec(e+f*x)), x)",
				"(a*Log(1-Sec(e+f*x))*Tan(e+f*x))/(f*Sqrt(a+a*Sec(e+f*x))*Sqrt(c-c*Sec(e+f*x)))");

	}

	// {(Sec(e+f*x)*Sqrt(a+a*Sec(e+f*x)))/(c-c*Sec(e+f*x))^(3/2), x, 1, -(Sqrt(a+a*Sec(e+f*x))*Tan(e +
	// f*x))/(2*f*(c-c*Sec(e+f*x))^(3/2))}
	public void test02453() {
		check("Integrate((Sec(e+f*x)*Sqrt(a+a*Sec(e+f*x)))/(c-c*Sec(e+f*x))^(3/2), x)",
				"-(Sqrt(a+a*Sec(e+f*x))*Tan(e+f*x))/(2*f*(c-c*Sec(e+f*x))^(3/2))");

	}

	// {(Sec(e+f*x)*Sqrt(a+a*Sec(e+f*x)))/(c-c*Sec(e+f*x))^(5/2), x, 1, -(a*Tan(e+f*x))/(2*f*Sqrt(a +
	// a*Sec(e+f*x))*(c-c*Sec(e+f*x))^(5/2))}
	public void test02454() {
		check("Integrate((Sec(e+f*x)*Sqrt(a+a*Sec(e+f*x)))/(c-c*Sec(e+f*x))^(5/2), x)",
				"-(a*Tan(e+f*x))/(2*f*Sqrt(a+a*Sec(e+f*x))*(c-c*Sec(e+f*x))^(5/2))");

	}

	// {Sec(e+f*x)*(a+a*Sec(e+f*x))^(3/2)*Sqrt(c-c*Sec(e+f*x)), x, 1, -(c*(a+a*Sec(e+f*x))^(3/2)*Tan(e +
	// f*x))/(2*f*Sqrt(c-c*Sec(e+f*x)))}
	public void test02455() {
		check("Integrate(Sec(e+f*x)*(a+a*Sec(e+f*x))^(3/2)*Sqrt(c-c*Sec(e+f*x)), x)",
				"-(c*(a+a*Sec(e+f*x))^(3/2)*Tan(e+f*x))/(2*f*Sqrt(c-c*Sec(e+f*x)))");

	}

	// {(Sec(e+f*x)*(a+a*Sec(e+f*x))^(3/2))/(c-c*Sec(e+f*x))^(5/2), x, 1, -((a+a*Sec(e+f*x))^(3/2)*Tan(e +
	// f*x))/(4*f*(c-c*Sec(e+f*x))^(5/2))}
	public void test02456() {
		check("Integrate((Sec(e+f*x)*(a+a*Sec(e+f*x))^(3/2))/(c-c*Sec(e+f*x))^(5/2), x)",
				"-((a+a*Sec(e+f*x))^(3/2)*Tan(e+f*x))/(4*f*(c-c*Sec(e+f*x))^(5/2))");

	}

	// {Sec(e+f*x)*(a+a*Sec(e+f*x))^(5/2)*Sqrt(c-c*Sec(e+f*x)), x, 1, -(c*(a+a*Sec(e+f*x))^(5/2)*Tan(e +
	// f*x))/(3*f*Sqrt(c-c*Sec(e+f*x)))}
	public void test02457() {
		check("Integrate(Sec(e+f*x)*(a+a*Sec(e+f*x))^(5/2)*Sqrt(c-c*Sec(e+f*x)), x)",
				"-(c*(a+a*Sec(e+f*x))^(5/2)*Tan(e+f*x))/(3*f*Sqrt(c-c*Sec(e+f*x)))");

	}

	// {(Sec(e+f*x)*(a+a*Sec(e+f*x))^(5/2))/(c-c*Sec(e+f*x))^(7/2), x, 1, -((a+a*Sec(e+f*x))^(5/2)*Tan(e +
	// f*x))/(6*f*(c-c*Sec(e+f*x))^(7/2))}
	public void test02458() {
		check("Integrate((Sec(e+f*x)*(a+a*Sec(e+f*x))^(5/2))/(c-c*Sec(e+f*x))^(7/2), x)",
				"-((a+a*Sec(e+f*x))^(5/2)*Tan(e+f*x))/(6*f*(c-c*Sec(e+f*x))^(7/2))");

	}

	// {(Sec(e+f*x)*Sqrt(c-c*Sec(e+f*x)))/Sqrt(a+a*Sec(e+f*x)), x, 1, -((c*Log(1+Sec(e+f*x))*Tan(e +
	// f*x))/(f*Sqrt(a+a*Sec(e+f*x))*Sqrt(c-c*Sec(e+f*x))))}
	public void test02459() {
		check("Integrate((Sec(e+f*x)*Sqrt(c-c*Sec(e+f*x)))/Sqrt(a+a*Sec(e+f*x)), x)",
				"-((c*Log(1+Sec(e+f*x))*Tan(e+f*x))/(f*Sqrt(a+a*Sec(e+f*x))*Sqrt(c-c*Sec(e+f*x))))");

	}

	// {(Sec(e+f*x)*Sqrt(c-c*Sec(e+f*x)))/(a+a*Sec(e+f*x))^(3/2), x, 1, (Sqrt(c-c*Sec(e+f*x))*Tan(e +
	// f*x))/(2*f*(a+a*Sec(e+f*x))^(3/2))}
	public void test02460() {
		check("Integrate((Sec(e+f*x)*Sqrt(c-c*Sec(e+f*x)))/(a+a*Sec(e+f*x))^(3/2), x)",
				"(Sqrt(c-c*Sec(e+f*x))*Tan(e+f*x))/(2*f*(a+a*Sec(e+f*x))^(3/2))");

	}

	// {(Sec(e+f*x)*(c-c*Sec(e+f*x))^(3/2))/(a+a*Sec(e+f*x))^(5/2), x, 1, ((c-c*Sec(e+f*x))^(3/2)*Tan(e +
	// f*x))/(4*f*(a+a*Sec(e+f*x))^(5/2))}
	public void test02461() {
		check("Integrate((Sec(e+f*x)*(c-c*Sec(e+f*x))^(3/2))/(a+a*Sec(e+f*x))^(5/2), x)",
				"((c-c*Sec(e+f*x))^(3/2)*Tan(e+f*x))/(4*f*(a+a*Sec(e+f*x))^(5/2))");

	}

	// {(Sec(e+f*x)*Sqrt(c-c*Sec(e+f*x)))/(a+a*Sec(e+f*x))^(5/2), x, 1, (c*Tan(e+f*x))/(2*f*(a+a*Sec(e +
	// f*x))^(5/2)*Sqrt(c-c*Sec(e+f*x)))}
	public void test02462() {
		check("Integrate((Sec(e+f*x)*Sqrt(c-c*Sec(e+f*x)))/(a+a*Sec(e+f*x))^(5/2), x)",
				"(c*Tan(e+f*x))/(2*f*(a+a*Sec(e+f*x))^(5/2)*Sqrt(c-c*Sec(e+f*x)))");

	}

	// {Sec(e+f*x)*(a+a*Sec(e+f*x))^m*Sqrt(c-c*Sec(e+f*x)), x, 1, (-2*c*(a+a*Sec(e+f*x))^m*Tan(e +
	// f*x))/(f*(1+2*m)*Sqrt(c-c*Sec(e+f*x)))}
	public void test02463() {
		check("Integrate(Sec(e+f*x)*(a+a*Sec(e+f*x))^m*Sqrt(c-c*Sec(e+f*x)), x)",
				"(-2*c*(a+a*Sec(e+f*x))^m*Tan(e+f*x))/(f*(1+2*m)*Sqrt(c-c*Sec(e+f*x)))");

	}

	// {Sec(e+f*x)*(a+a*Sec(e+f*x))^m*(c-c*Sec(e+f*x))^(-1-m), x, 1, -(((a+a*Sec(e+f*x))^m*(c-c*Sec(e
	// +f*x))^(-1-m)*Tan(e+f*x))/(f*(1+2*m)))}
	public void test02464() {
		check("Integrate(Sec(e+f*x)*(a+a*Sec(e+f*x))^m*(c-c*Sec(e+f*x))^(-1-m), x)",
				"-(((a+a*Sec(e+f*x))^m*(c-c*Sec(e+f*x))^(-1-m)*Tan(e+f*x))/(f*(1+2*m)))");

	}

	// {(Sec(e+f*x)*Sqrt(a+b*Sec(e+f*x)))/Sqrt(c+d*Sec(e+f*x)), x, 1, (2*Cot(e+f*x)*EllipticPi((b*(c +
	// d))/((a+b)*d), ArcSin((Sqrt((a+b)/(c+d))*Sqrt(c+d*Sec(e+f*x)))/Sqrt(a+b*Sec(e+f*x))), ((a-b)*(c +
	// d))/((a+b)*(c-d)))*Sqrt(-(((b*c-a*d)*(1-Sec(e+f*x)))/((c+d)*(a+b*Sec(e+f*x)))))*Sqrt(((b*c -
	// a*d)*(1+Sec(e+f*x)))/((c-d)*(a+b*Sec(e+f*x))))*(a+b*Sec(e+f*x)))/(d*Sqrt((a+b)/(c+d))*f)}
	public void test02465() {
		check("Integrate((Sec(e+f*x)*Sqrt(a+b*Sec(e+f*x)))/Sqrt(c+d*Sec(e+f*x)), x)",
				"(2*Cot(e+f*x)*EllipticPi((b*(c+d))/((a+b)*d), ArcSin((Sqrt((a+b)/(c+d))*Sqrt(c+d*Sec(e+f*x)))/Sqrt(a+b*Sec(e+f*x))), ((a-b)*(c+d))/((a+b)*(c-d)))*Sqrt(-(((b*c-a*d)*(1-Sec(e+f*x)))/((c+d)*(a+b*Sec(e+f*x)))))*Sqrt(((b*c-a*d)*(1+Sec(e+f*x)))/((c-d)*(a+b*Sec(e+f*x))))*(a+b*Sec(e+f*x)))/(d*Sqrt((a+b)/(c+d))*f)");

	}

	// {Sec(e+f*x)/(Sqrt(a+b*Sec(e+f*x))*Sqrt(c+d*Sec(e+f*x))), x, 1, (2*Sqrt(a+b)*Cot(e +
	// f*x)*EllipticF(ArcSin((Sqrt(c+d)*Sqrt(a+b*Sec(e+f*x)))/(Sqrt(a+b)*Sqrt(c+d*Sec(e+f*x)))), ((a+b)*(c
	// -d))/((a-b)*(c+d)))*Sqrt(((b*c-a*d)*(1-Sec(e+f*x)))/((a+b)*(c+d*Sec(e+f*x))))*Sqrt(-(((b*c -
	// a*d)*(1+Sec(e+f*x)))/((a-b)*(c+d*Sec(e+f*x)))))*(c+d*Sec(e+f*x)))/(Sqrt(c+d)*(b*c-a*d)*f)}
	public void test02466() {
		check("Integrate(Sec(e+f*x)/(Sqrt(a+b*Sec(e+f*x))*Sqrt(c+d*Sec(e+f*x))), x)",
				"(2*Sqrt(a+b)*Cot(e+f*x)*EllipticF(ArcSin((Sqrt(c+d)*Sqrt(a+b*Sec(e+f*x)))/(Sqrt(a+b)*Sqrt(c+d*Sec(e+f*x)))), ((a+b)*(c-d))/((a-b)*(c+d)))*Sqrt(((b*c-a*d)*(1-Sec(e+f*x)))/((a+b)*(c+d*Sec(e+f*x))))*Sqrt(-(((b*c-a*d)*(1+Sec(e+f*x)))/((a-b)*(c+d*Sec(e+f*x)))))*(c+d*Sec(e+f*x)))/(Sqrt(c+d)*(b*c-a*d)*f)");

	}

	// {Sec(e+f*x)/(Sqrt(2+3*Sec(e+f*x))*Sqrt(-4+5*Sec(e+f*x))), x, 1, (2*Cot(e+f*x)*EllipticF(ArcSin(Sqrt(2
	// +3*Sec(e+f*x))/(Sqrt(5)*Sqrt(-4+5*Sec(e+f*x)))), 45)*(4-5*Sec(e+f*x))*Sqrt((1-Sec(e+f*x))/(4 -
	// 5*Sec(e+f*x)))*Sqrt((1+Sec(e+f*x))/(4-5*Sec(e+f*x))))/f}
	public void test02467() {
		check("Integrate(Sec(e+f*x)/(Sqrt(2+3*Sec(e+f*x))*Sqrt(-4+5*Sec(e+f*x))), x)",
				"(2*Cot(e+f*x)*EllipticF(ArcSin(Sqrt(2+3*Sec(e+f*x))/(Sqrt(5)*Sqrt(-4+5*Sec(e+f*x)))), 45)*(4-5*Sec(e+f*x))*Sqrt((1-Sec(e+f*x))/(4-5*Sec(e+f*x)))*Sqrt((1+Sec(e+f*x))/(4-5*Sec(e+f*x))))/f");

	}

	// {Sec(e+f*x)/(Sqrt(4-5*Sec(e+f*x))*Sqrt(2+3*Sec(e+f*x))), x, 1, (((2*I)/3)*Cot(e +
	// f*x)*EllipticF(I*ArcSinh((Sqrt(5)*Sqrt(4-5*Sec(e+f*x)))/Sqrt(2+3*Sec(e+f*x))), 1/45)*Sqrt((1-Sec(e +
	// f*x))/(2+3*Sec(e+f*x)))*Sqrt((1+Sec(e+f*x))/(2+3*Sec(e+f*x)))*(2+3*Sec(e+f*x)))/(Sqrt(5)*f)}
	public void test02468() {
		check("Integrate(Sec(e+f*x)/(Sqrt(4-5*Sec(e+f*x))*Sqrt(2+3*Sec(e+f*x))), x)",
				"(((2*I)/3)*Cot(e+f*x)*EllipticF(I*ArcSinh((Sqrt(5)*Sqrt(4-5*Sec(e+f*x)))/Sqrt(2+3*Sec(e+f*x))), 1/45)*Sqrt((1-Sec(e+f*x))/(2+3*Sec(e+f*x)))*Sqrt((1+Sec(e+f*x))/(2+3*Sec(e+f*x)))*(2+3*Sec(e+f*x)))/(Sqrt(5)*f)");

	}

	// {(Sec(e+f*x)*Sqrt(a+b*Sec(e+f*x)))/(c+c*Sec(e+f*x)), x, 1, (EllipticE(ArcSin(Tan(e+f*x)/(1+Sec(e +
	// f*x))), (a-b)/(a+b))*Sqrt((1+Sec(e+f*x))^(-1))*Sqrt(a+b*Sec(e+f*x)))/(c*f*Sqrt((a+b*Sec(e +
	// f*x))/((a+b)*(1+Sec(e+f*x)))))}
	public void test02469() {
		check("Integrate((Sec(e+f*x)*Sqrt(a+b*Sec(e+f*x)))/(c+c*Sec(e+f*x)), x)",
				"(EllipticE(ArcSin(Tan(e+f*x)/(1+Sec(e+f*x))), (a-b)/(a+b))*Sqrt((1+Sec(e+f*x))^(-1))*Sqrt(a+b*Sec(e+f*x)))/(c*f*Sqrt((a+b*Sec(e+f*x))/((a+b)*(1+Sec(e+f*x)))))");

	}

	// {Sec(e+f*x)/(Sqrt(a+b*Sec(e+f*x))*(c+d*Sec(e+f*x))), x, 1, (2*EllipticPi((2*d)/(c+d), ArcSin(Sqrt(1 -
	// Sec(e+f*x))/Sqrt(2)), (2*b)/(a+b))*Sqrt((a+b*Sec(e+f*x))/(a+b))*Tan(e+f*x))/((c+d)*f*Sqrt(a +
	// b*Sec(e+f*x))*Sqrt(-Tan(e+f*x)^2))}
	public void test02470() {
		check("Integrate(Sec(e+f*x)/(Sqrt(a+b*Sec(e+f*x))*(c+d*Sec(e+f*x))), x)",
				"(2*EllipticPi((2*d)/(c+d), ArcSin(Sqrt(1-Sec(e+f*x))/Sqrt(2)), (2*b)/(a+b))*Sqrt((a+b*Sec(e+f*x))/(a+b))*Tan(e+f*x))/((c+d)*f*Sqrt(a+b*Sec(e+f*x))*Sqrt(-Tan(e+f*x)^2))");

	}

	// {(Sec(e+f*x)*(A+A*Sec(e+f*x)))/Sqrt(a+b*Sec(e+f*x)), x, 1, (-2*A*(a-b)*Sqrt(a+b)*Cot(e +
	// f*x)*EllipticE(ArcSin(Sqrt(a+b*Sec(e+f*x))/Sqrt(a+b)), (a+b)/(a-b))*Sqrt((b*(1-Sec(e+f*x)))/(a +
	// b))*Sqrt(-((b*(1+Sec(e+f*x)))/(a-b))))/(b^2*f)}
	public void test02471() {
		check("Integrate((Sec(e+f*x)*(A+A*Sec(e+f*x)))/Sqrt(a+b*Sec(e+f*x)), x)",
				"(-2*A*(a-b)*Sqrt(a+b)*Cot(e+f*x)*EllipticE(ArcSin(Sqrt(a+b*Sec(e+f*x))/Sqrt(a+b)), (a+b)/(a-b))*Sqrt((b*(1-Sec(e+f*x)))/(a+b))*Sqrt(-((b*(1+Sec(e+f*x)))/(a-b))))/(b^2*f)");

	}

	// {(Sec(e+f*x)*(A-A*Sec(e+f*x)))/Sqrt(a+b*Sec(e+f*x)), x, 1, (2*A*Sqrt(a-b)*(a+b)*Cot(e +
	// f*x)*EllipticE(ArcSin(Sqrt(a+b*Sec(e+f*x))/Sqrt(a-b)), (a-b)/(a+b))*Sqrt((b*(1-Sec(e+f*x)))/(a +
	// b))*Sqrt(-((b*(1+Sec(e+f*x)))/(a-b))))/(b^2*f)}
	public void test02472() {
		check("Integrate((Sec(e+f*x)*(A-A*Sec(e+f*x)))/Sqrt(a+b*Sec(e+f*x)), x)",
				"(2*A*Sqrt(a-b)*(a+b)*Cot(e+f*x)*EllipticE(ArcSin(Sqrt(a+b*Sec(e+f*x))/Sqrt(a-b)), (a-b)/(a+b))*Sqrt((b*(1-Sec(e+f*x)))/(a+b))*Sqrt(-((b*(1+Sec(e+f*x)))/(a-b))))/(b^2*f)");

	}

	// {Sec(c+d*x)^m*(-((C*m)/(1+m))+C*Sec(c+d*x)^2), x, 1, (C*Sec(c+d*x)^(1+m)*Sin(c+d*x))/(d*(1+m))}
	public void test02473() {
		check("Integrate(Sec(c+d*x)^m*(-((C*m)/(1+m))+C*Sec(c+d*x)^2), x)",
				"(C*Sec(c+d*x)^(1+m)*Sin(c+d*x))/(d*(1+m))");

	}

	// {Sec(c+d*x)^m*(A-(A*(1+m)*Sec(c+d*x)^2)/m), x, 1, -((A*Sec(c+d*x)^(1+m)*Sin(c+d*x))/(d*m))}
	public void test02474() {
		check("Integrate(Sec(c+d*x)^m*(A-(A*(1+m)*Sec(c+d*x)^2)/m), x)", "-((A*Sec(c+d*x)^(1+m)*Sin(c+d*x))/(d*m))");

	}

	// {(3+3*Sec(c+d*x)^2)/Sqrt(Sec(c+d*x)), x, 1, (6*Sqrt(Sec(c+d*x))*Sin(c+d*x))/d}
	public void test02475() {
		check("Integrate((3+3*Sec(c+d*x)^2)/Sqrt(Sec(c+d*x)), x)", "(6*Sqrt(Sec(c+d*x))*Sin(c+d*x))/d");

	}

	// {Sec(e+f*x)^m*(m-(1+m)*Sec(e+f*x)^2), x, 1, -((Sec(e+f*x)^(1+m)*Sin(e+f*x))/f)}
	public void test02476() {
		check("Integrate(Sec(e+f*x)^m*(m-(1+m)*Sec(e+f*x)^2), x)", "-((Sec(e+f*x)^(1+m)*Sin(e+f*x))/f)");

	}

	// {Sec(e+f*x)^5*(5-6*Sec(e+f*x)^2), x, 1, -((Sec(e+f*x)^5*Tan(e+f*x))/f)}
	public void test02477() {
		check("Integrate(Sec(e+f*x)^5*(5-6*Sec(e+f*x)^2), x)", "-((Sec(e+f*x)^5*Tan(e+f*x))/f)");

	}

	// {Sec(e+f*x)^4*(4-5*Sec(e+f*x)^2), x, 1, -((Sec(e+f*x)^4*Tan(e+f*x))/f)}
	public void test02478() {
		check("Integrate(Sec(e+f*x)^4*(4-5*Sec(e+f*x)^2), x)", "-((Sec(e+f*x)^4*Tan(e+f*x))/f)");

	}

	// {Sec(e+f*x)^3*(3-4*Sec(e+f*x)^2), x, 1, -((Sec(e+f*x)^3*Tan(e+f*x))/f)}
	public void test02479() {
		check("Integrate(Sec(e+f*x)^3*(3-4*Sec(e+f*x)^2), x)", "-((Sec(e+f*x)^3*Tan(e+f*x))/f)");

	}

	// {Sec(e+f*x)^2*(2-3*Sec(e+f*x)^2), x, 1, -((Sec(e+f*x)^2*Tan(e+f*x))/f)}
	public void test02480() {
		check("Integrate(Sec(e+f*x)^2*(2-3*Sec(e+f*x)^2), x)", "-((Sec(e+f*x)^2*Tan(e+f*x))/f)");

	}

	// {Sec(e+f*x)*(1-2*Sec(e+f*x)^2), x, 1, -((Sec(e+f*x)*Tan(e+f*x))/f)}
	public void test02481() {
		check("Integrate(Sec(e+f*x)*(1-2*Sec(e+f*x)^2), x)", "-((Sec(e+f*x)*Tan(e+f*x))/f)");

	}

	// {-Cos(e+f*x), x, 1, -(Sin(e+f*x)/f)}
	public void test02482() {
		check("Integrate(-Cos(e+f*x), x)", "-(Sin(e+f*x)/f)");

	}

	// {Cos(e+f*x)^2*(-2+Sec(e+f*x)^2), x, 1, -((Cos(e+f*x)*Sin(e+f*x))/f)}
	public void test02483() {
		check("Integrate(Cos(e+f*x)^2*(-2+Sec(e+f*x)^2), x)", "-((Cos(e+f*x)*Sin(e+f*x))/f)");

	}

	// {Cos(e+f*x)^3*(-3+2*Sec(e+f*x)^2), x, 1, -((Cos(e+f*x)^2*Sin(e+f*x))/f)}
	public void test02484() {
		check("Integrate(Cos(e+f*x)^3*(-3+2*Sec(e+f*x)^2), x)", "-((Cos(e+f*x)^2*Sin(e+f*x))/f)");

	}

	// {Cos(e+f*x)^4*(-4+3*Sec(e+f*x)^2), x, 1, -((Cos(e+f*x)^3*Sin(e+f*x))/f)}
	public void test02485() {
		check("Integrate(Cos(e+f*x)^4*(-4+3*Sec(e+f*x)^2), x)", "-((Cos(e+f*x)^3*Sin(e+f*x))/f)");

	}

	// {Cos(e+f*x)^5*(-5+4*Sec(e+f*x)^2), x, 1, -((Cos(e+f*x)^4*Sin(e+f*x))/f)}
	public void test02486() {
		check("Integrate(Cos(e+f*x)^5*(-5+4*Sec(e+f*x)^2), x)", "-((Cos(e+f*x)^4*Sin(e+f*x))/f)");

	}

	// {Csc(a+b*x), x, 1, -(ArcTanh(Cos(a+b*x))/b)}
	public void test02487() {
		check("Integrate(Csc(a+b*x), x)", "-(ArcTanh(Cos(a+b*x))/b)");

	}

	// {(e*x)^m*(a+b*Csc(c+d*x^n))^p, x, 1, ((e*x)^m*Unintegrable(x^m*(a+b*Csc(c+d*x^n))^p, x))/x^m}
	public void test02488() {
		check("Integrate((e*x)^m*(a+b*Csc(c+d*x^n))^p, x)", "((e*x)^m*Unintegrable(x^m*(a+b*Csc(c+d*x^n))^p, x))/x^m");

	}

	// {Csc(x)/(a+a*Csc(x)), x, 1, -(Cot(x)/(a+a*Csc(x)))}
	public void test02489() {
		check("Integrate(Csc(x)/(a+a*Csc(x)), x)", "-(Cot(x)/(a+a*Csc(x)))");

	}

	// {Sin(a+b*x)*Sin(2*a+2*b*x), x, 1, Sin(a+b*x)/(2*b)-Sin(3*a+3*b*x)/(6*b)}
	public void test02490() {
		check("Integrate(Sin(a+b*x)*Sin(2*a+2*b*x), x)", "Sin(a+b*x)/(2*b)-Sin(3*a+3*b*x)/(6*b)");

	}

	// {Sin(a+b*x)/Sqrt(Sin(2*a+2*b*x)), x, 1, -ArcSin(Cos(a+b*x)-Sin(a+b*x))/(2*b)-Log(Cos(a+b*x)+Sin(a
	// +b*x)+Sqrt(Sin(2*a+2*b*x)))/(2*b)}
	public void test02491() {
		check("Integrate(Sin(a+b*x)/Sqrt(Sin(2*a+2*b*x)), x)",
				"-ArcSin(Cos(a+b*x)-Sin(a+b*x))/(2*b)-Log(Cos(a+b*x)+Sin(a+b*x)+Sqrt(Sin(2*a+2*b*x)))/(2*b)");

	}

	// {Sin(a+b*x)/Sin(2*a+2*b*x)^(3/2), x, 1, Sin(a+b*x)/(b*Sqrt(Sin(2*a+2*b*x)))}
	public void test02492() {
		check("Integrate(Sin(a+b*x)/Sin(2*a+2*b*x)^(3/2), x)", "Sin(a+b*x)/(b*Sqrt(Sin(2*a+2*b*x)))");

	}

	// {Sin(a+b*x)^3/Sin(2*a+2*b*x)^(5/2), x, 1, Sin(a+b*x)^3/(3*b*Sin(2*a+2*b*x)^(3/2))}
	public void test02493() {
		check("Integrate(Sin(a+b*x)^3/Sin(2*a+2*b*x)^(5/2), x)", "Sin(a+b*x)^3/(3*b*Sin(2*a+2*b*x)^(3/2))");

	}

	// {Csc(a+b*x)/Sqrt(Sin(2*a+2*b*x)), x, 1, -((Csc(a+b*x)*Sqrt(Sin(2*a+2*b*x)))/b)}
	public void test02494() {
		check("Integrate(Csc(a+b*x)/Sqrt(Sin(2*a+2*b*x)), x)", "-((Csc(a+b*x)*Sqrt(Sin(2*a+2*b*x)))/b)");

	}

	// {Csc(a+b*x)^3*Sqrt(Sin(2*a+2*b*x)), x, 1, -(Csc(a+b*x)^3*Sin(2*a+2*b*x)^(3/2))/(3*b)}
	public void test02495() {
		check("Integrate(Csc(a+b*x)^3*Sqrt(Sin(2*a+2*b*x)), x)", "-(Csc(a+b*x)^3*Sin(2*a+2*b*x)^(3/2))/(3*b)");

	}

	// {Cos(a+b*x)*Sin(2*a+2*b*x), x, 1, -Cos(a+b*x)/(2*b)-Cos(3*a+3*b*x)/(6*b)}
	public void test02496() {
		check("Integrate(Cos(a+b*x)*Sin(2*a+2*b*x), x)", "-Cos(a+b*x)/(2*b)-Cos(3*a+3*b*x)/(6*b)");

	}

	// {Cos(a+b*x)/Sqrt(Sin(2*a+2*b*x)), x, 1, -ArcSin(Cos(a+b*x)-Sin(a+b*x))/(2*b)+Log(Cos(a+b*x)+Sin(a
	// +b*x)+Sqrt(Sin(2*a+2*b*x)))/(2*b)}
	public void test02497() {
		check("Integrate(Cos(a+b*x)/Sqrt(Sin(2*a+2*b*x)), x)",
				"-ArcSin(Cos(a+b*x)-Sin(a+b*x))/(2*b)+Log(Cos(a+b*x)+Sin(a+b*x)+Sqrt(Sin(2*a+2*b*x)))/(2*b)");

	}

	// {Cos(a+b*x)/Sin(2*a+2*b*x)^(3/2), x, 1, -(Cos(a+b*x)/(b*Sqrt(Sin(2*a+2*b*x))))}
	public void test02498() {
		check("Integrate(Cos(a+b*x)/Sin(2*a+2*b*x)^(3/2), x)", "-(Cos(a+b*x)/(b*Sqrt(Sin(2*a+2*b*x))))");

	}

	// {Cos(a+b*x)^3/Sin(2*a+2*b*x)^(5/2), x, 1, -Cos(a+b*x)^3/(3*b*Sin(2*a+2*b*x)^(3/2))}
	public void test02499() {
		check("Integrate(Cos(a+b*x)^3/Sin(2*a+2*b*x)^(5/2), x)", "-Cos(a+b*x)^3/(3*b*Sin(2*a+2*b*x)^(3/2))");

	}

	// {Cos(x)/Sqrt(Sin(2*x)), x, 1, -ArcSin(Cos(x)-Sin(x))/2+Log(Cos(x)+Sin(x)+Sqrt(Sin(2*x)))/2}
	public void test02500() {
		check("Integrate(Cos(x)/Sqrt(Sin(2*x)), x)", "-ArcSin(Cos(x)-Sin(x))/2+Log(Cos(x)+Sin(x)+Sqrt(Sin(2*x)))/2");

	}

	// {(a*Cos(x)+b*Sin(x))^(-2), x, 1, Sin(x)/(a*(a*Cos(x)+b*Sin(x)))}
	public void test02501() {
		check("Integrate((a*Cos(x)+b*Sin(x))^(-2), x)", "Sin(x)/(a*(a*Cos(x)+b*Sin(x)))");

	}

	// {(a*Cos(c+d*x)+I*a*Sin(c+d*x))^n/Sin(c+d*x)^n, x, 1, ((-I/2)*Hypergeometric2F1(1, n, 1+n, (-I/2)*(I +
	// Cot(c+d*x)))*(a*Cos(c+d*x)+I*a*Sin(c+d*x))^n)/(d*n*Sin(c+d*x)^n)}
	public void test02502() {
		check("Integrate((a*Cos(c+d*x)+I*a*Sin(c+d*x))^n/Sin(c+d*x)^n, x)",
				"((-I/2)*Hypergeometric2F1(1, n, 1+n, (-I/2)*(I+Cot(c+d*x)))*(a*Cos(c+d*x)+I*a*Sin(c+d*x))^n)/(d*n*Sin(c+d*x)^n)");

	}

	// {(a*Cos(c+d*x)+b*Sin(c+d*x))^(-2), x, 1, Sin(c+d*x)/(a*d*(a*Cos(c+d*x)+b*Sin(c+d*x)))}
	public void test02503() {
		check("Integrate((a*Cos(c+d*x)+b*Sin(c+d*x))^(-2), x)", "Sin(c+d*x)/(a*d*(a*Cos(c+d*x)+b*Sin(c+d*x)))");

	}

	// {(a*Cos(c+d*x)+I*a*Sin(c+d*x))^(-1), x, 1, I/(d*(a*Cos(c+d*x)+I*a*Sin(c+d*x)))}
	public void test02504() {
		check("Integrate((a*Cos(c+d*x)+I*a*Sin(c+d*x))^(-1), x)", "I/(d*(a*Cos(c+d*x)+I*a*Sin(c+d*x)))");

	}

	// {(a*Cos(c+d*x)+I*a*Sin(c+d*x))^(-2), x, 1, (I/2)/(d*(a*Cos(c+d*x)+I*a*Sin(c+d*x))^2)}
	public void test02505() {
		check("Integrate((a*Cos(c+d*x)+I*a*Sin(c+d*x))^(-2), x)", "(I/2)/(d*(a*Cos(c+d*x)+I*a*Sin(c+d*x))^2)");

	}

	// {(a*Cos(c+d*x)+I*a*Sin(c+d*x))^(-3), x, 1, (I/3)/(d*(a*Cos(c+d*x)+I*a*Sin(c+d*x))^3)}
	public void test02506() {
		check("Integrate((a*Cos(c+d*x)+I*a*Sin(c+d*x))^(-3), x)", "(I/3)/(d*(a*Cos(c+d*x)+I*a*Sin(c+d*x))^3)");

	}

	// {(a*Cos(c+d*x)+I*a*Sin(c+d*x))^n/Cos(c+d*x)^n, x, 1, ((-I/2)*Hypergeometric2F1(1, n, 1+n, (1+I*Tan(c
	// +d*x))/2)*(a*Cos(c+d*x)+I*a*Sin(c+d*x))^n)/(d*n*Cos(c+d*x)^n)}
	public void test02507() {
		check("Integrate((a*Cos(c+d*x)+I*a*Sin(c+d*x))^n/Cos(c+d*x)^n, x)",
				"((-I/2)*Hypergeometric2F1(1, n, 1+n, (1+I*Tan(c+d*x))/2)*(a*Cos(c+d*x)+I*a*Sin(c+d*x))^n)/(d*n*Cos(c+d*x)^n)");

	}

	// {(c+d*x)^m*Cot(a+b*x)^2*Csc(a+b*x), x, 1, -Unintegrable((c+d*x)^m*Csc(a+b*x), x)+Unintegrable((c +
	// d*x)^m*Csc(a+b*x)^3, x)}
	public void test02508() {
		check("Integrate((c+d*x)^m*Cot(a+b*x)^2*Csc(a+b*x), x)",
				"-Unintegrable((c+d*x)^m*Csc(a+b*x), x)+Unintegrable((c+d*x)^m*Csc(a+b*x)^3, x)");

	}

	// {(Cot(a+b*x)^2*Csc(a+b*x))/(c+d*x), x, 1, -Unintegrable(Csc(a+b*x)/(c+d*x), x)+Unintegrable(Csc(a +
	// b*x)^3/(c+d*x), x)}
	public void test02509() {
		check("Integrate((Cot(a+b*x)^2*Csc(a+b*x))/(c+d*x), x)",
				"-Unintegrable(Csc(a+b*x)/(c+d*x), x)+Unintegrable(Csc(a+b*x)^3/(c+d*x), x)");

	}

	// {(Cot(a+b*x)^2*Csc(a+b*x))/(c+d*x)^2, x, 1, -Unintegrable(Csc(a+b*x)/(c+d*x)^2, x)+Unintegrable(Csc(a
	// +b*x)^3/(c+d*x)^2, x)}
	public void test02510() {
		check("Integrate((Cot(a+b*x)^2*Csc(a+b*x))/(c+d*x)^2, x)",
				"-Unintegrable(Csc(a+b*x)/(c+d*x)^2, x)+Unintegrable(Csc(a+b*x)^3/(c+d*x)^2, x)");

	}

	// {(Csc(a+b*x)*Sec(a+b*x))/(c+d*x), x, 1, 2*Unintegrable(Csc(2*a+2*b*x)/(c+d*x), x)}
	public void test02511() {
		check("Integrate((Csc(a+b*x)*Sec(a+b*x))/(c+d*x), x)", "2*Unintegrable(Csc(2*a+2*b*x)/(c+d*x), x)");

	}

	// {(Csc(a+b*x)*Sec(a+b*x))/(c+d*x)^2, x, 1, 2*Unintegrable(Csc(2*a+2*b*x)/(c+d*x)^2, x)}
	public void test02512() {
		check("Integrate((Csc(a+b*x)*Sec(a+b*x))/(c+d*x)^2, x)", "2*Unintegrable(Csc(2*a+2*b*x)/(c+d*x)^2, x)");

	}

	// {(Csc(a+b*x)^2*Sec(a+b*x)^2)/(c+d*x), x, 1, 4*Unintegrable(Csc(2*a+2*b*x)^2/(c+d*x), x)}
	public void test02513() {
		check("Integrate((Csc(a+b*x)^2*Sec(a+b*x)^2)/(c+d*x), x)", "4*Unintegrable(Csc(2*a+2*b*x)^2/(c+d*x), x)");

	}

	// {(Csc(a+b*x)^2*Sec(a+b*x)^2)/(c+d*x)^2, x, 1, 4*Unintegrable(Csc(2*a+2*b*x)^2/(c+d*x)^2, x)}
	public void test02514() {
		check("Integrate((Csc(a+b*x)^2*Sec(a+b*x)^2)/(c+d*x)^2, x)", "4*Unintegrable(Csc(2*a+2*b*x)^2/(c+d*x)^2, x)");

	}

	// {(c+d*x)^m*Sec(a+b*x)*Tan(a+b*x)^2, x, 1, -Unintegrable((c+d*x)^m*Sec(a+b*x), x)+Unintegrable((c +
	// d*x)^m*Sec(a+b*x)^3, x)}
	public void test02515() {
		check("Integrate((c+d*x)^m*Sec(a+b*x)*Tan(a+b*x)^2, x)",
				"-Unintegrable((c+d*x)^m*Sec(a+b*x), x)+Unintegrable((c+d*x)^m*Sec(a+b*x)^3, x)");

	}

	// {(Sec(a+b*x)*Tan(a+b*x)^2)/(c+d*x), x, 1, -Unintegrable(Sec(a+b*x)/(c+d*x), x)+Unintegrable(Sec(a +
	// b*x)^3/(c+d*x), x)}
	public void test02516() {
		check("Integrate((Sec(a+b*x)*Tan(a+b*x)^2)/(c+d*x), x)",
				"-Unintegrable(Sec(a+b*x)/(c+d*x), x)+Unintegrable(Sec(a+b*x)^3/(c+d*x), x)");

	}

	// {(Sec(a+b*x)*Tan(a+b*x)^2)/(c+d*x)^2, x, 1, -Unintegrable(Sec(a+b*x)/(c+d*x)^2, x)+Unintegrable(Sec(a
	// +b*x)^3/(c+d*x)^2, x)}
	public void test02517() {
		check("Integrate((Sec(a+b*x)*Tan(a+b*x)^2)/(c+d*x)^2, x)",
				"-Unintegrable(Sec(a+b*x)/(c+d*x)^2, x)+Unintegrable(Sec(a+b*x)^3/(c+d*x)^2, x)");

	}

	// {(Csc(a+b*x)^3*Sec(a+b*x)^3)/(c+d*x), x, 1, 8*Unintegrable(Csc(2*a+2*b*x)^3/(c+d*x), x)}
	public void test02518() {
		check("Integrate((Csc(a+b*x)^3*Sec(a+b*x)^3)/(c+d*x), x)", "8*Unintegrable(Csc(2*a+2*b*x)^3/(c+d*x), x)");

	}

	// {(Csc(a+b*x)^3*Sec(a+b*x)^3)/(c+d*x)^2, x, 1, 8*Unintegrable(Csc(2*a+2*b*x)^3/(c+d*x)^2, x)}
	public void test02519() {
		check("Integrate((Csc(a+b*x)^3*Sec(a+b*x)^3)/(c+d*x)^2, x)", "8*Unintegrable(Csc(2*a+2*b*x)^3/(c+d*x)^2, x)");

	}

	// {x^2*Sin(a+b*Log(c*x^n)), x, 1, -((b*n*x^3*Cos(a+b*Log(c*x^n)))/(9+b^2*n^2))+(3*x^3*Sin(a +
	// b*Log(c*x^n)))/(9+b^2*n^2)}
	public void test02520() {
		check("Integrate(x^2*Sin(a+b*Log(c*x^n)), x)",
				"-((b*n*x^3*Cos(a+b*Log(c*x^n)))/(9+b^2*n^2))+(3*x^3*Sin(a+b*Log(c*x^n)))/(9+b^2*n^2)");

	}

	// {x*Sin(a+b*Log(c*x^n)), x, 1, -((b*n*x^2*Cos(a+b*Log(c*x^n)))/(4+b^2*n^2))+(2*x^2*Sin(a +
	// b*Log(c*x^n)))/(4+b^2*n^2)}
	public void test02521() {
		check("Integrate(x*Sin(a+b*Log(c*x^n)), x)",
				"-((b*n*x^2*Cos(a+b*Log(c*x^n)))/(4+b^2*n^2))+(2*x^2*Sin(a+b*Log(c*x^n)))/(4+b^2*n^2)");

	}

	// {Sin(a+b*Log(c*x^n)), x, 1, -((b*n*x*Cos(a+b*Log(c*x^n)))/(1+b^2*n^2))+(x*Sin(a+b*Log(c*x^n)))/(1 +
	// b^2*n^2)}
	public void test02522() {
		check("Integrate(Sin(a+b*Log(c*x^n)), x)",
				"-((b*n*x*Cos(a+b*Log(c*x^n)))/(1+b^2*n^2))+(x*Sin(a+b*Log(c*x^n)))/(1+b^2*n^2)");

	}

	// {Sin(a+b*Log(c*x^n))/x^2, x, 1, -((b*n*Cos(a+b*Log(c*x^n)))/((1+b^2*n^2)*x))-Sin(a+b*Log(c*x^n))/((1 +
	// b^2*n^2)*x)}
	public void test02523() {
		check("Integrate(Sin(a+b*Log(c*x^n))/x^2, x)",
				"-((b*n*Cos(a+b*Log(c*x^n)))/((1+b^2*n^2)*x))-Sin(a+b*Log(c*x^n))/((1+b^2*n^2)*x)");

	}

	// {Sin(a+b*Log(c*x^n))/x^3, x, 1, -((b*n*Cos(a+b*Log(c*x^n)))/((4+b^2*n^2)*x^2))-(2*Sin(a +
	// b*Log(c*x^n)))/((4+b^2*n^2)*x^2)}
	public void test02524() {
		check("Integrate(Sin(a+b*Log(c*x^n))/x^3, x)",
				"-((b*n*Cos(a+b*Log(c*x^n)))/((4+b^2*n^2)*x^2))-(2*Sin(a+b*Log(c*x^n)))/((4+b^2*n^2)*x^2)");

	}

	// {(e*x)^m*Sin(d*(a+b*Log(c*x^n))), x, 1, -((b*d*n*(e*x)^(1+m)*Cos(d*(a+b*Log(c*x^n))))/(e*((1+m)^2 +
	// b^2*d^2*n^2)))+((1+m)*(e*x)^(1+m)*Sin(d*(a+b*Log(c*x^n))))/(e*((1+m)^2+b^2*d^2*n^2))}
	public void test02525() {
		check("Integrate((e*x)^m*Sin(d*(a+b*Log(c*x^n))), x)",
				"-((b*d*n*(e*x)^(1+m)*Cos(d*(a+b*Log(c*x^n))))/(e*((1+m)^2+b^2*d^2*n^2)))+((1+m)*(e*x)^(1+m)*Sin(d*(a+b*Log(c*x^n))))/(e*((1+m)^2+b^2*d^2*n^2))");

	}

	// {x^2*Cos(a+b*Log(c*x^n)), x, 1, (3*x^3*Cos(a+b*Log(c*x^n)))/(9+b^2*n^2)+(b*n*x^3*Sin(a +
	// b*Log(c*x^n)))/(9+b^2*n^2)}
	public void test02526() {
		check("Integrate(x^2*Cos(a+b*Log(c*x^n)), x)",
				"(3*x^3*Cos(a+b*Log(c*x^n)))/(9+b^2*n^2)+(b*n*x^3*Sin(a+b*Log(c*x^n)))/(9+b^2*n^2)");

	}

	// {x*Cos(a+b*Log(c*x^n)), x, 1, (2*x^2*Cos(a+b*Log(c*x^n)))/(4+b^2*n^2)+(b*n*x^2*Sin(a+b*Log(c*x^n)))/(4
	// +b^2*n^2)}
	public void test02527() {
		check("Integrate(x*Cos(a+b*Log(c*x^n)), x)",
				"(2*x^2*Cos(a+b*Log(c*x^n)))/(4+b^2*n^2)+(b*n*x^2*Sin(a+b*Log(c*x^n)))/(4+b^2*n^2)");

	}

	// {Cos(a+b*Log(c*x^n)), x, 1, (x*Cos(a+b*Log(c*x^n)))/(1+b^2*n^2)+(b*n*x*Sin(a+b*Log(c*x^n)))/(1 +
	// b^2*n^2)}
	public void test02528() {
		check("Integrate(Cos(a+b*Log(c*x^n)), x)",
				"(x*Cos(a+b*Log(c*x^n)))/(1+b^2*n^2)+(b*n*x*Sin(a+b*Log(c*x^n)))/(1+b^2*n^2)");

	}

	// {Cos(a+b*Log(c*x^n))/x^2, x, 1, -(Cos(a+b*Log(c*x^n))/((1+b^2*n^2)*x))+(b*n*Sin(a+b*Log(c*x^n)))/((1 +
	// b^2*n^2)*x)}
	public void test02529() {
		check("Integrate(Cos(a+b*Log(c*x^n))/x^2, x)",
				"-(Cos(a+b*Log(c*x^n))/((1+b^2*n^2)*x))+(b*n*Sin(a+b*Log(c*x^n)))/((1+b^2*n^2)*x)");

	}

	// {x^m*Cos(a+b*Log(c*x^n)), x, 1, ((1+m)*x^(1+m)*Cos(a+b*Log(c*x^n)))/((1+m)^2+b^2*n^2)+(b*n*x^(1 +
	// m)*Sin(a+b*Log(c*x^n)))/((1+m)^2+b^2*n^2)}
	public void test02530() {
		check("Integrate(x^m*Cos(a+b*Log(c*x^n)), x)",
				"((1+m)*x^(1+m)*Cos(a+b*Log(c*x^n)))/((1+m)^2+b^2*n^2)+(b*n*x^(1+m)*Sin(a+b*Log(c*x^n)))/((1+m)^2+b^2*n^2)");

	}

	// {F^(c*(a+b*x))*Sin(d+e*x), x, 1, -((e*F^(c*(a+b*x))*Cos(d+e*x))/(e^2+b^2*c^2*Log(F)^2))+(b*c*F^(c*(a
	// +b*x))*Log(F)*Sin(d+e*x))/(e^2+b^2*c^2*Log(F)^2)}
	public void test02531() {
		check("Integrate(F^(c*(a+b*x))*Sin(d+e*x), x)",
				"-((e*F^(c*(a+b*x))*Cos(d+e*x))/(e^2+b^2*c^2*Log(F)^2))+(b*c*F^(c*(a+b*x))*Log(F)*Sin(d+e*x))/(e^2+b^2*c^2*Log(F)^2)");

	}

	// {F^(c*(a+b*x))*Csc(d+e*x), x, 1, (-2*E^(I*(d+e*x))*F^(c*(a+b*x))*Hypergeometric2F1(1, (e -
	// I*b*c*Log(F))/(2*e), (3-(I*b*c*Log(F))/e)/2, E^((2*I)*(d+e*x))))/(e-I*b*c*Log(F))}
	public void test02532() {
		check("Integrate(F^(c*(a+b*x))*Csc(d+e*x), x)",
				"(-2*E^(I*(d+e*x))*F^(c*(a+b*x))*Hypergeometric2F1(1, (e-I*b*c*Log(F))/(2*e), (3-(I*b*c*Log(F))/e)/2, E^((2*I)*(d+e*x))))/(e-I*b*c*Log(F))");

	}

	// {F^(c*(a+b*x))*Csc(d+e*x)^2, x, 1, (-4*E^((2*I)*(d+e*x))*F^(c*(a+b*x))*Hypergeometric2F1(2, 1 -
	// ((I/2)*b*c*Log(F))/e, 2-((I/2)*b*c*Log(F))/e, E^((2*I)*(d+e*x))))/((2*I)*e+b*c*Log(F))}
	public void test02533() {
		check("Integrate(F^(c*(a+b*x))*Csc(d+e*x)^2, x)",
				"(-4*E^((2*I)*(d+e*x))*F^(c*(a+b*x))*Hypergeometric2F1(2, 1-((I/2)*b*c*Log(F))/e, 2-((I/2)*b*c*Log(F))/e, E^((2*I)*(d+e*x))))/((2*I)*e+b*c*Log(F))");

	}

	// {F^(c*(a+b*x))*Cos(d+e*x), x, 1, (b*c*F^(c*(a+b*x))*Cos(d+e*x)*Log(F))/(e^2+b^2*c^2*Log(F)^2) +
	// (e*F^(c*(a+b*x))*Sin(d+e*x))/(e^2+b^2*c^2*Log(F)^2)}
	public void test02534() {
		check("Integrate(F^(c*(a+b*x))*Cos(d+e*x), x)",
				"(b*c*F^(c*(a+b*x))*Cos(d+e*x)*Log(F))/(e^2+b^2*c^2*Log(F)^2)+(e*F^(c*(a+b*x))*Sin(d+e*x))/(e^2+b^2*c^2*Log(F)^2)");

	}

	// {F^(c*(a+b*x))*Sec(d+e*x), x, 1, (2*E^(I*(d+e*x))*F^(c*(a+b*x))*Hypergeometric2F1(1, (e -
	// I*b*c*Log(F))/(2*e), (3-(I*b*c*Log(F))/e)/2, -E^((2*I)*(d+e*x))))/(I*e+b*c*Log(F))}
	public void test02535() {
		check("Integrate(F^(c*(a+b*x))*Sec(d+e*x), x)",
				"(2*E^(I*(d+e*x))*F^(c*(a+b*x))*Hypergeometric2F1(1, (e-I*b*c*Log(F))/(2*e), (3-(I*b*c*Log(F))/e)/2, -E^((2*I)*(d+e*x))))/(I*e+b*c*Log(F))");

	}

	// {F^(c*(a+b*x))*Sec(d+e*x)^2, x, 1, (4*E^((2*I)*(d+e*x))*F^(c*(a+b*x))*Hypergeometric2F1(2, 1 -
	// ((I/2)*b*c*Log(F))/e, 2-((I/2)*b*c*Log(F))/e, -E^((2*I)*(d+e*x))))/((2*I)*e+b*c*Log(F))}
	public void test02536() {
		check("Integrate(F^(c*(a+b*x))*Sec(d+e*x)^2, x)",
				"(4*E^((2*I)*(d+e*x))*F^(c*(a+b*x))*Hypergeometric2F1(2, 1-((I/2)*b*c*Log(F))/e, 2-((I/2)*b*c*Log(F))/e, -E^((2*I)*(d+e*x))))/((2*I)*e+b*c*Log(F))");

	}

	// {F^(c*(a+b*x))*(f*x)^m*Csc(d+e*x), x, 1, CannotIntegrate(F^(a*c+b*c*x)*(f*x)^m*Csc(d+e*x), x)}
	public void test02537() {
		check("Integrate(F^(c*(a+b*x))*(f*x)^m*Csc(d+e*x), x)", "CannotIntegrate(F^(a*c+b*c*x)*(f*x)^m*Csc(d+e*x), x)");

	}

	// {F^(c*(a+b*x))*(f*x)^m*Csc(d+e*x)^2, x, 1, CannotIntegrate(F^(a*c+b*c*x)*(f*x)^m*Csc(d+e*x)^2, x)}
	public void test02538() {
		check("Integrate(F^(c*(a+b*x))*(f*x)^m*Csc(d+e*x)^2, x)",
				"CannotIntegrate(F^(a*c+b*c*x)*(f*x)^m*Csc(d+e*x)^2, x)");

	}

	// {F^(c*(a+b*x))*(e*Cos(d+e*x)+b*c*Log(F)*Sin(d+e*x)), x, 1, F^(c*(a+b*x))*Sin(d+e*x)}
	public void test02539() {
		check("Integrate(F^(c*(a+b*x))*(e*Cos(d+e*x)+b*c*Log(F)*Sin(d+e*x)), x)", "F^(c*(a+b*x))*Sin(d+e*x)");

	}

	// {E^x*Sin(a+b*x), x, 1, -((b*E^x*Cos(a+b*x))/(1+b^2))+(E^x*Sin(a+b*x))/(1+b^2)}
	public void test02540() {
		check("Integrate(E^x*Sin(a+b*x), x)", "-((b*E^x*Cos(a+b*x))/(1+b^2))+(E^x*Sin(a+b*x))/(1+b^2)");

	}

	// {E^x*Cos(a+b*x), x, 1, (E^x*Cos(a+b*x))/(1+b^2)+(b*E^x*Sin(a+b*x))/(1+b^2)}
	public void test02541() {
		check("Integrate(E^x*Cos(a+b*x), x)", "(E^x*Cos(a+b*x))/(1+b^2)+(b*E^x*Sin(a+b*x))/(1+b^2)");

	}

	// {Csc(Sqrt(1-a*x)/Sqrt(1+a*x))/(1-a^2*x^2), x, 1, Unintegrable(Csc(Sqrt(1-a*x)/Sqrt(1+a*x))/((1 -
	// a*x)*(1+a*x)), x)}
	public void test02542() {
		check("Integrate(Csc(Sqrt(1-a*x)/Sqrt(1+a*x))/(1-a^2*x^2), x)",
				"Unintegrable(Csc(Sqrt(1-a*x)/Sqrt(1+a*x))/((1-a*x)*(1+a*x)), x)");

	}

	// {Csc(Sqrt(1-a*x)/Sqrt(1+a*x))^2/(1-a^2*x^2), x, 1, Unintegrable(Csc(Sqrt(1-a*x)/Sqrt(1+a*x))^2/((1 -
	// a*x)*(1+a*x)), x)}
	public void test02543() {
		check("Integrate(Csc(Sqrt(1-a*x)/Sqrt(1+a*x))^2/(1-a^2*x^2), x)",
				"Unintegrable(Csc(Sqrt(1-a*x)/Sqrt(1+a*x))^2/((1-a*x)*(1+a*x)), x)");

	}

	// {Sec(Sqrt(1-a*x)/Sqrt(1+a*x))/(1-a^2*x^2), x, 1, Unintegrable(Sec(Sqrt(1-a*x)/Sqrt(1+a*x))/((1 -
	// a*x)*(1+a*x)), x)}
	public void test02544() {
		check("Integrate(Sec(Sqrt(1-a*x)/Sqrt(1+a*x))/(1-a^2*x^2), x)",
				"Unintegrable(Sec(Sqrt(1-a*x)/Sqrt(1+a*x))/((1-a*x)*(1+a*x)), x)");

	}

	// {Sec(Sqrt(1-a*x)/Sqrt(1+a*x))^2/(1-a^2*x^2), x, 1, Unintegrable(Sec(Sqrt(1-a*x)/Sqrt(1+a*x))^2/((1 -
	// a*x)*(1+a*x)), x)}
	public void test02545() {
		check("Integrate(Sec(Sqrt(1-a*x)/Sqrt(1+a*x))^2/(1-a^2*x^2), x)",
				"Unintegrable(Sec(Sqrt(1-a*x)/Sqrt(1+a*x))^2/((1-a*x)*(1+a*x)), x)");

	}

	// {Sin(x)*Sin(2*x), x, 1, Sin(x)/2-Sin(3*x)/6}
	public void test02546() {
		check("Integrate(Sin(x)*Sin(2*x), x)", "Sin(x)/2-Sin(3*x)/6");

	}

	// {Sin(x)*Sin(3*x), x, 1, Sin(2*x)/4-Sin(4*x)/8}
	public void test02547() {
		check("Integrate(Sin(x)*Sin(3*x), x)", "Sin(2*x)/4-Sin(4*x)/8");

	}

	// {Sin(x)*Sin(4*x), x, 1, Sin(3*x)/6-Sin(5*x)/10}
	public void test02548() {
		check("Integrate(Sin(x)*Sin(4*x), x)", "Sin(3*x)/6-Sin(5*x)/10");

	}

	// {Cos(2*x)*Sin(x), x, 1, Cos(x)/2-Cos(3*x)/6}
	public void test02549() {
		check("Integrate(Cos(2*x)*Sin(x), x)", "Cos(x)/2-Cos(3*x)/6");

	}

	// {Cos(3*x)*Sin(x), x, 1, Cos(2*x)/4-Cos(4*x)/8}
	public void test02550() {
		check("Integrate(Cos(3*x)*Sin(x), x)", "Cos(2*x)/4-Cos(4*x)/8");

	}

	// {Cos(4*x)*Sin(x), x, 1, Cos(3*x)/6-Cos(5*x)/10}
	public void test02551() {
		check("Integrate(Cos(4*x)*Sin(x), x)", "Cos(3*x)/6-Cos(5*x)/10");

	}

	// {Cos(x)*Sin(2*x), x, 1, -Cos(x)/2-Cos(3*x)/6}
	public void test02552() {
		check("Integrate(Cos(x)*Sin(2*x), x)", "-Cos(x)/2-Cos(3*x)/6");

	}

	// {Cos(x)*Sin(3*x), x, 1, -Cos(2*x)/4-Cos(4*x)/8}
	public void test02553() {
		check("Integrate(Cos(x)*Sin(3*x), x)", "-Cos(2*x)/4-Cos(4*x)/8");

	}

	// {Cos(x)*Sin(4*x), x, 1, -Cos(3*x)/6-Cos(5*x)/10}
	public void test02554() {
		check("Integrate(Cos(x)*Sin(4*x), x)", "-Cos(3*x)/6-Cos(5*x)/10");

	}

	// {Cos(x)*Cos(2*x), x, 1, Sin(x)/2+Sin(3*x)/6}
	public void test02555() {
		check("Integrate(Cos(x)*Cos(2*x), x)", "Sin(x)/2+Sin(3*x)/6");

	}

	// {Cos(x)*Cos(3*x), x, 1, Sin(2*x)/4+Sin(4*x)/8}
	public void test02556() {
		check("Integrate(Cos(x)*Cos(3*x), x)", "Sin(2*x)/4+Sin(4*x)/8");

	}

	// {Cos(x)*Cos(4*x), x, 1, Sin(3*x)/6+Sin(5*x)/10}
	public void test02557() {
		check("Integrate(Cos(x)*Cos(4*x), x)", "Sin(3*x)/6+Sin(5*x)/10");

	}

	// {(a*Cos(c+d*x)+b*Sin(c+d*x))^(-2), x, 1, Sin(c+d*x)/(a*d*(a*Cos(c+d*x)+b*Sin(c+d*x)))}
	public void test02558() {
		check("Integrate((a*Cos(c+d*x)+b*Sin(c+d*x))^(-2), x)", "Sin(c+d*x)/(a*d*(a*Cos(c+d*x)+b*Sin(c+d*x)))");

	}

	// {(a*Cos(c+d*x)+I*a*Sin(c+d*x))^n, x, 1, ((-I)*(a*Cos(c+d*x)+I*a*Sin(c+d*x))^n)/(d*n)}
	public void test02559() {
		check("Integrate((a*Cos(c+d*x)+I*a*Sin(c+d*x))^n, x)", "((-I)*(a*Cos(c+d*x)+I*a*Sin(c+d*x))^n)/(d*n)");

	}

	// {(a*Cos(c+d*x)+I*a*Sin(c+d*x))^4, x, 1, ((-I/4)*(a*Cos(c+d*x)+I*a*Sin(c+d*x))^4)/d}
	public void test02560() {
		check("Integrate((a*Cos(c+d*x)+I*a*Sin(c+d*x))^4, x)", "((-I/4)*(a*Cos(c+d*x)+I*a*Sin(c+d*x))^4)/d");

	}

	// {(a*Cos(c+d*x)+I*a*Sin(c+d*x))^3, x, 1, ((-I/3)*(a*Cos(c+d*x)+I*a*Sin(c+d*x))^3)/d}
	public void test02561() {
		check("Integrate((a*Cos(c+d*x)+I*a*Sin(c+d*x))^3, x)", "((-I/3)*(a*Cos(c+d*x)+I*a*Sin(c+d*x))^3)/d");

	}

	// {(a*Cos(c+d*x)+I*a*Sin(c+d*x))^2, x, 1, ((-I/2)*(a*Cos(c+d*x)+I*a*Sin(c+d*x))^2)/d}
	public void test02562() {
		check("Integrate((a*Cos(c+d*x)+I*a*Sin(c+d*x))^2, x)", "((-I/2)*(a*Cos(c+d*x)+I*a*Sin(c+d*x))^2)/d");

	}

	// {(a*Cos(c+d*x)+I*a*Sin(c+d*x))^(-1), x, 1, I/(d*(a*Cos(c+d*x)+I*a*Sin(c+d*x)))}
	public void test02563() {
		check("Integrate((a*Cos(c+d*x)+I*a*Sin(c+d*x))^(-1), x)", "I/(d*(a*Cos(c+d*x)+I*a*Sin(c+d*x)))");

	}

	// {(a*Cos(c+d*x)+I*a*Sin(c+d*x))^(-2), x, 1, (I/2)/(d*(a*Cos(c+d*x)+I*a*Sin(c+d*x))^2)}
	public void test02564() {
		check("Integrate((a*Cos(c+d*x)+I*a*Sin(c+d*x))^(-2), x)", "(I/2)/(d*(a*Cos(c+d*x)+I*a*Sin(c+d*x))^2)");

	}

	// {(a*Cos(c+d*x)+I*a*Sin(c+d*x))^(-3), x, 1, (I/3)/(d*(a*Cos(c+d*x)+I*a*Sin(c+d*x))^3)}
	public void test02565() {
		check("Integrate((a*Cos(c+d*x)+I*a*Sin(c+d*x))^(-3), x)", "(I/3)/(d*(a*Cos(c+d*x)+I*a*Sin(c+d*x))^3)");

	}

	// {(a*Cos(c+d*x)+I*a*Sin(c+d*x))^(-4), x, 1, (I/4)/(d*(a*Cos(c+d*x)+I*a*Sin(c+d*x))^4)}
	public void test02566() {
		check("Integrate((a*Cos(c+d*x)+I*a*Sin(c+d*x))^(-4), x)", "(I/4)/(d*(a*Cos(c+d*x)+I*a*Sin(c+d*x))^4)");

	}

	// {(a*Cos(c+d*x)+I*a*Sin(c+d*x))^(5/2), x, 1, (((-2*I)/5)*(a*Cos(c+d*x)+I*a*Sin(c+d*x))^(5/2))/d}
	public void test02567() {
		check("Integrate((a*Cos(c+d*x)+I*a*Sin(c+d*x))^(5/2), x)",
				"(((-2*I)/5)*(a*Cos(c+d*x)+I*a*Sin(c+d*x))^(5/2))/d");

	}

	// {(a*Cos(c+d*x)+I*a*Sin(c+d*x))^(3/2), x, 1, (((-2*I)/3)*(a*Cos(c+d*x)+I*a*Sin(c+d*x))^(3/2))/d}
	public void test02568() {
		check("Integrate((a*Cos(c+d*x)+I*a*Sin(c+d*x))^(3/2), x)",
				"(((-2*I)/3)*(a*Cos(c+d*x)+I*a*Sin(c+d*x))^(3/2))/d");

	}

	// {Sqrt(a*Cos(c+d*x)+I*a*Sin(c+d*x)), x, 1, ((-2*I)*Sqrt(a*Cos(c+d*x)+I*a*Sin(c+d*x)))/d}
	public void test02569() {
		check("Integrate(Sqrt(a*Cos(c+d*x)+I*a*Sin(c+d*x)), x)", "((-2*I)*Sqrt(a*Cos(c+d*x)+I*a*Sin(c+d*x)))/d");

	}

	// {1/Sqrt(a*Cos(c+d*x)+I*a*Sin(c+d*x)), x, 1, (2*I)/(d*Sqrt(a*Cos(c+d*x)+I*a*Sin(c+d*x)))}
	public void test02570() {
		check("Integrate(1/Sqrt(a*Cos(c+d*x)+I*a*Sin(c+d*x)), x)", "(2*I)/(d*Sqrt(a*Cos(c+d*x)+I*a*Sin(c+d*x)))");

	}

	// {(a*Cos(c+d*x)+I*a*Sin(c+d*x))^(-3/2), x, 1, ((2*I)/3)/(d*(a*Cos(c+d*x)+I*a*Sin(c+d*x))^(3/2))}
	public void test02571() {
		check("Integrate((a*Cos(c+d*x)+I*a*Sin(c+d*x))^(-3/2), x)",
				"((2*I)/3)/(d*(a*Cos(c+d*x)+I*a*Sin(c+d*x))^(3/2))");

	}

	// {(a*Cos(c+d*x)+I*a*Sin(c+d*x))^(-5/2), x, 1, ((2*I)/5)/(d*(a*Cos(c+d*x)+I*a*Sin(c+d*x))^(5/2))}
	public void test02572() {
		check("Integrate((a*Cos(c+d*x)+I*a*Sin(c+d*x))^(-5/2), x)",
				"((2*I)/5)/(d*(a*Cos(c+d*x)+I*a*Sin(c+d*x))^(5/2))");

	}

	// {(Sqrt(b^2+c^2)+b*Cos(d+e*x)+c*Sin(d+e*x))^(-1), x, 1, -((c-Sqrt(b^2+c^2)*Sin(d +
	// e*x))/(c*e*(c*Cos(d+e*x)-b*Sin(d+e*x))))}
	public void test02573() {
		check("Integrate((Sqrt(b^2+c^2)+b*Cos(d+e*x)+c*Sin(d+e*x))^(-1), x)",
				"-((c-Sqrt(b^2+c^2)*Sin(d+e*x))/(c*e*(c*Cos(d+e*x)-b*Sin(d+e*x))))");

	}

	// {Sqrt(5+4*Cos(d+e*x)+3*Sin(d+e*x)), x, 1, (-2*(3*Cos(d+e*x)-4*Sin(d+e*x)))/(e*Sqrt(5+4*Cos(d +
	// e*x)+3*Sin(d+e*x)))}
	public void test02574() {
		check("Integrate(Sqrt(5+4*Cos(d+e*x)+3*Sin(d+e*x)), x)",
				"(-2*(3*Cos(d+e*x)-4*Sin(d+e*x)))/(e*Sqrt(5+4*Cos(d+e*x)+3*Sin(d+e*x)))");

	}

	// {Sqrt(-5+4*Cos(d+e*x)+3*Sin(d+e*x)), x, 1, (-2*(3*Cos(d+e*x)-4*Sin(d+e*x)))/(e*Sqrt(-5+4*Cos(d +
	// e*x)+3*Sin(d+e*x)))}
	public void test02575() {
		check("Integrate(Sqrt(-5+4*Cos(d+e*x)+3*Sin(d+e*x)), x)",
				"(-2*(3*Cos(d+e*x)-4*Sin(d+e*x)))/(e*Sqrt(-5+4*Cos(d+e*x)+3*Sin(d+e*x)))");

	}

	// {Sqrt(Sqrt(b^2+c^2)+b*Cos(d+e*x)+c*Sin(d+e*x)), x, 1, (-2*(c*Cos(d+e*x)-b*Sin(d +
	// e*x)))/(e*Sqrt(Sqrt(b^2+c^2)+b*Cos(d+e*x)+c*Sin(d+e*x)))}
	public void test02576() {
		check("Integrate(Sqrt(Sqrt(b^2+c^2)+b*Cos(d+e*x)+c*Sin(d+e*x)), x)",
				"(-2*(c*Cos(d+e*x)-b*Sin(d+e*x)))/(e*Sqrt(Sqrt(b^2+c^2)+b*Cos(d+e*x)+c*Sin(d+e*x)))");

	}

	// {Sqrt(-Sqrt(b^2+c^2)+b*Cos(d+e*x)+c*Sin(d+e*x)), x, 1, (-2*(c*Cos(d+e*x)-b*Sin(d +
	// e*x)))/(e*Sqrt(-Sqrt(b^2+c^2)+b*Cos(d+e*x)+c*Sin(d+e*x)))}
	public void test02577() {
		check("Integrate(Sqrt(-Sqrt(b^2+c^2)+b*Cos(d+e*x)+c*Sin(d+e*x)), x)",
				"(-2*(c*Cos(d+e*x)-b*Sin(d+e*x)))/(e*Sqrt(-Sqrt(b^2+c^2)+b*Cos(d+e*x)+c*Sin(d+e*x)))");

	}

	// {(Cos(x)-I*Sin(x))/(Cos(x)+I*Sin(x)), x, 1, (I/2)*(Cos(x)-I*Sin(x))^2}
	public void test02578() {
		check("Integrate((Cos(x)-I*Sin(x))/(Cos(x)+I*Sin(x)), x)", "(I/2)*(Cos(x)-I*Sin(x))^2");

	}

	// {(Cos(x)+I*Sin(x))/(Cos(x)-I*Sin(x)), x, 1, (-I/2)/(Cos(x)-I*Sin(x))^2}
	public void test02579() {
		check("Integrate((Cos(x)+I*Sin(x))/(Cos(x)-I*Sin(x)), x)", "(-I/2)/(Cos(x)-I*Sin(x))^2");

	}

	// {(Cos(x)-Sin(x))/(Cos(x)+Sin(x)), x, 1, Log(Cos(x)+Sin(x))}
	public void test02580() {
		check("Integrate((Cos(x)-Sin(x))/(Cos(x)+Sin(x)), x)", "Log(Cos(x)+Sin(x))");

	}

	// {(B*Cos(x)+C*Sin(x))/(b*Cos(x)+c*Sin(x)), x, 1, ((b*B+c*C)*x)/(b^2+c^2)+((B*c-b*C)*Log(b*Cos(x) +
	// c*Sin(x)))/(b^2+c^2)}
	public void test02581() {
		check("Integrate((B*Cos(x)+C*Sin(x))/(b*Cos(x)+c*Sin(x)), x)",
				"((b*B+c*C)*x)/(b^2+c^2)+((B*c-b*C)*Log(b*Cos(x)+c*Sin(x)))/(b^2+c^2)");

	}

	// {(A+B*Cos(x))/(a+b*Cos(x)+I*b*Sin(x)), x, 1, ((2*a*A-b*B)*x)/(2*a^2)+((I/2)*B*Cos(x))/a +
	// ((I/2)*(2*a*A*b-a^2*B-b^2*B)*Log(a+b*Cos(x)+I*b*Sin(x)))/(a^2*b)+(B*Sin(x))/(2*a)}
	public void test02582() {
		check("Integrate((A+B*Cos(x))/(a+b*Cos(x)+I*b*Sin(x)), x)",
				"((2*a*A-b*B)*x)/(2*a^2)+((I/2)*B*Cos(x))/a+((I/2)*(2*a*A*b-a^2*B-b^2*B)*Log(a+b*Cos(x)+I*b*Sin(x)))/(a^2*b)+(B*Sin(x))/(2*a)");

	}

	// {(A+B*Cos(x))/(a+b*Cos(x)-I*b*Sin(x)), x, 1, ((2*a*A-b*B)*x)/(2*a^2)-((I/2)*B*Cos(x))/a -
	// ((I/2)*(2*a*A*b-a^2*B-b^2*B)*Log(a+b*Cos(x)-I*b*Sin(x)))/(a^2*b)+(B*Sin(x))/(2*a)}
	public void test02583() {
		check("Integrate((A+B*Cos(x))/(a+b*Cos(x)-I*b*Sin(x)), x)",
				"((2*a*A-b*B)*x)/(2*a^2)-((I/2)*B*Cos(x))/a-((I/2)*(2*a*A*b-a^2*B-b^2*B)*Log(a+b*Cos(x)-I*b*Sin(x)))/(a^2*b)+(B*Sin(x))/(2*a)");

	}

	// {(A+C*Sin(x))/(a+b*Cos(x)+I*b*Sin(x)), x, 1, ((2*a*A-I*b*C)*x)/(2*a^2)-(C*Cos(x))/(2*a)+(((2*I)*a*A*b
	// -a^2*C+b^2*C)*Log(a+b*Cos(x)+I*b*Sin(x)))/(2*a^2*b)+((I/2)*C*Sin(x))/a}
	public void test02584() {
		check("Integrate((A+C*Sin(x))/(a+b*Cos(x)+I*b*Sin(x)), x)",
				"((2*a*A-I*b*C)*x)/(2*a^2)-(C*Cos(x))/(2*a)+(((2*I)*a*A*b-a^2*C+b^2*C)*Log(a+b*Cos(x)+I*b*Sin(x)))/(2*a^2*b)+((I/2)*C*Sin(x))/a");

	}

	// {(A+C*Sin(x))/(a+b*Cos(x)-I*b*Sin(x)), x, 1, ((2*a*A+I*b*C)*x)/(2*a^2)-(C*Cos(x))/(2*a)-(((2*I)*a*A*b
	// +a^2*C-b^2*C)*Log(a+b*Cos(x)-I*b*Sin(x)))/(2*a^2*b)-((I/2)*C*Sin(x))/a}
	public void test02585() {
		check("Integrate((A+C*Sin(x))/(a+b*Cos(x)-I*b*Sin(x)), x)",
				"((2*a*A+I*b*C)*x)/(2*a^2)-(C*Cos(x))/(2*a)-(((2*I)*a*A*b+a^2*C-b^2*C)*Log(a+b*Cos(x)-I*b*Sin(x)))/(2*a^2*b)-((I/2)*C*Sin(x))/a");

	}

	// {(B*Cos(x)+C*Sin(x))/(a+b*Cos(x)+I*b*Sin(x)), x, 1, -(b*(B+I*C)*x)/(2*a^2)-((I*b^2*(B+I*C)+a^2*(I*B
	// +C))*Log(a+b*Cos(x)+I*b*Sin(x)))/(2*a^2*b)+((I*B-C)*(Cos(x)-I*Sin(x)))/(2*a)}
	public void test02586() {
		check("Integrate((B*Cos(x)+C*Sin(x))/(a+b*Cos(x)+I*b*Sin(x)), x)",
				"-(b*(B+I*C)*x)/(2*a^2)-((I*b^2*(B+I*C)+a^2*(I*B+C))*Log(a+b*Cos(x)+I*b*Sin(x)))/(2*a^2*b)+((I*B-C)*(Cos(x)-I*Sin(x)))/(2*a)");

	}

	// {(B*Cos(x)+C*Sin(x))/(a+b*Cos(x)-I*b*Sin(x)), x, 1, -(b*(B-I*C)*x)/(2*a^2)+((I*a^2*(B+I*C)+b^2*(I*B
	// +C))*Log(a+b*Cos(x)-I*b*Sin(x)))/(2*a^2*b)-((I*B+C)*(Cos(x)+I*Sin(x)))/(2*a)}
	public void test02587() {
		check("Integrate((B*Cos(x)+C*Sin(x))/(a+b*Cos(x)-I*b*Sin(x)), x)",
				"-(b*(B-I*C)*x)/(2*a^2)+((I*a^2*(B+I*C)+b^2*(I*B+C))*Log(a+b*Cos(x)-I*b*Sin(x)))/(2*a^2*b)-((I*B+C)*(Cos(x)+I*Sin(x)))/(2*a)");

	}

	// {(A+B*Cos(x)+C*Sin(x))/(a+b*Cos(x)+I*b*Sin(x)), x, 1, ((2*a*A-b*(B+I*C))*x)/(2*a^2)+((I/2)*(2*a*A*b
	// -a^2*(B-I*C)-b^2*(B+I*C))*Log(a+b*Cos(x)+I*b*Sin(x)))/(a^2*b)+((I*B-C)*(Cos(x)-I*Sin(x)))/(2*a)}
	public void test02588() {
		check("Integrate((A+B*Cos(x)+C*Sin(x))/(a+b*Cos(x)+I*b*Sin(x)), x)",
				"((2*a*A-b*(B+I*C))*x)/(2*a^2)+((I/2)*(2*a*A*b-a^2*(B-I*C)-b^2*(B+I*C))*Log(a+b*Cos(x)+I*b*Sin(x)))/(a^2*b)+((I*B-C)*(Cos(x)-I*Sin(x)))/(2*a)");

	}

	// {(A+B*Cos(x)+C*Sin(x))/(a+b*Cos(x)-I*b*Sin(x)), x, 1, ((2*a*A-b*B+I*b*C)*x)/(2*a^2)-((I/2)*(2*a*A*b
	// -b^2*(B-I*C)-a^2*(B+I*C))*Log(a+b*Cos(x)-I*b*Sin(x)))/(a^2*b)-((I*B+C)*(Cos(x)+I*Sin(x)))/(2*a)}
	public void test02589() {
		check("Integrate((A+B*Cos(x)+C*Sin(x))/(a+b*Cos(x)-I*b*Sin(x)), x)",
				"((2*a*A-b*B+I*b*C)*x)/(2*a^2)-((I/2)*(2*a*A*b-b^2*(B-I*C)-a^2*(B+I*C))*Log(a+b*Cos(x)-I*b*Sin(x)))/(a^2*b)-((I*B+C)*(Cos(x)+I*Sin(x)))/(2*a)");

	}

	// {1/(x*(a+b*Cos(x)*Sin(x))), x, 1, Unintegrable(1/(x*(a+(b*Sin(2*x))/2)), x)}
	public void test02590() {
		check("Integrate(1/(x*(a+b*Cos(x)*Sin(x))), x)", "Unintegrable(1/(x*(a+(b*Sin(2*x))/2)), x)");

	}

	// {((b*x)^(2-n)*Sin(a*x)^n)/(a*c*x*Cos(a*x)-c*Sin(a*x))^2, x, 1, (b*(b*x)^(1-n)*Sin(a*x)^(-1 +
	// n))/(a^2*(a*c^2*x*Cos(a*x)-c^2*Sin(a*x)))+(b^2*(1-n)*Unintegrable(Sin(a*x)^(-2+n)/(b*x)^n, x))/(a^2*c^2)}
	public void test02591() {
		check("Integrate(((b*x)^(2-n)*Sin(a*x)^n)/(a*c*x*Cos(a*x)-c*Sin(a*x))^2, x)",
				"(b*(b*x)^(1-n)*Sin(a*x)^(-1+n))/(a^2*(a*c^2*x*Cos(a*x)-c^2*Sin(a*x)))+(b^2*(1-n)*Unintegrable(Sin(a*x)^(-2+n)/(b*x)^n, x))/(a^2*c^2)");

	}

	// {((b*x)^(2-n)*Cos(a*x)^n)/(c*Cos(a*x)+a*c*x*Sin(a*x))^2, x, 1, -((b*(b*x)^(1-n)*Cos(a*x)^(-1 +
	// n))/(a^2*(c^2*Cos(a*x)+a*c^2*x*Sin(a*x))))+(b^2*(1-n)*Unintegrable(Cos(a*x)^(-2+n)/(b*x)^n,
	// x))/(a^2*c^2)}
	public void test02592() {
		check("Integrate(((b*x)^(2-n)*Cos(a*x)^n)/(c*Cos(a*x)+a*c*x*Sin(a*x))^2, x)",
				"-((b*(b*x)^(1-n)*Cos(a*x)^(-1+n))/(a^2*(c^2*Cos(a*x)+a*c^2*x*Sin(a*x))))+(b^2*(1-n)*Unintegrable(Cos(a*x)^(-2+n)/(b*x)^n, x))/(a^2*c^2)");

	}

	// {Sin(a*x)^2/(a*x*Cos(a*x)-Sin(a*x))^2, x, 1, 1/(a^2*x)+Sin(a*x)/(a^2*x*(a*x*Cos(a*x)-Sin(a*x)))}
	public void test02593() {
		check("Integrate(Sin(a*x)^2/(a*x*Cos(a*x)-Sin(a*x))^2, x)",
				"1/(a^2*x)+Sin(a*x)/(a^2*x*(a*x*Cos(a*x)-Sin(a*x)))");

	}

	// {(x*Sin(a*x))/(a*x*Cos(a*x)-Sin(a*x))^2, x, 1, 1/(a^2*(a*x*Cos(a*x)-Sin(a*x)))}
	public void test02594() {
		check("Integrate((x*Sin(a*x))/(a*x*Cos(a*x)-Sin(a*x))^2, x)", "1/(a^2*(a*x*Cos(a*x)-Sin(a*x)))");

	}

	// {Cos(a*x)^2/(Cos(a*x)+a*x*Sin(a*x))^2, x, 1, 1/(a^2*x)-Cos(a*x)/(a^2*x*(Cos(a*x)+a*x*Sin(a*x)))}
	public void test02595() {
		check("Integrate(Cos(a*x)^2/(Cos(a*x)+a*x*Sin(a*x))^2, x)",
				"1/(a^2*x)-Cos(a*x)/(a^2*x*(Cos(a*x)+a*x*Sin(a*x)))");

	}

	// {(x*Cos(a*x))/(Cos(a*x)+a*x*Sin(a*x))^2, x, 1, -(1/(a^2*(Cos(a*x)+a*x*Sin(a*x))))}
	public void test02596() {
		check("Integrate((x*Cos(a*x))/(Cos(a*x)+a*x*Sin(a*x))^2, x)", "-(1/(a^2*(Cos(a*x)+a*x*Sin(a*x))))");

	}

	// {(b*Sec(c+d*x)+a*Sin(c+d*x))^n*(a*Cos(c+d*x)+b*Sec(c+d*x)*Tan(c+d*x)), x, 1, (b*Sec(c+d*x) +
	// a*Sin(c+d*x))^(1+n)/(d*(1+n))}
	public void test02597() {
		check("Integrate((b*Sec(c+d*x)+a*Sin(c+d*x))^n*(a*Cos(c+d*x)+b*Sec(c+d*x)*Tan(c+d*x)), x)",
				"(b*Sec(c+d*x)+a*Sin(c+d*x))^(1+n)/(d*(1+n))");

	}

	// {(b*Sec(c+d*x)+a*Sin(c+d*x))^3*(a*Cos(c+d*x)+b*Sec(c+d*x)*Tan(c+d*x)), x, 1, (b*Sec(c+d*x) +
	// a*Sin(c+d*x))^4/(4*d)}
	public void test02598() {
		check("Integrate((b*Sec(c+d*x)+a*Sin(c+d*x))^3*(a*Cos(c+d*x)+b*Sec(c+d*x)*Tan(c+d*x)), x)",
				"(b*Sec(c+d*x)+a*Sin(c+d*x))^4/(4*d)");

	}

	// {(b*Sec(c+d*x)+a*Sin(c+d*x))^2*(a*Cos(c+d*x)+b*Sec(c+d*x)*Tan(c+d*x)), x, 1, (b*Sec(c+d*x) +
	// a*Sin(c+d*x))^3/(3*d)}
	public void test02599() {
		check("Integrate((b*Sec(c+d*x)+a*Sin(c+d*x))^2*(a*Cos(c+d*x)+b*Sec(c+d*x)*Tan(c+d*x)), x)",
				"(b*Sec(c+d*x)+a*Sin(c+d*x))^3/(3*d)");

	}

	// {(b*Sec(c+d*x)+a*Sin(c+d*x))*(a*Cos(c+d*x)+b*Sec(c+d*x)*Tan(c+d*x)), x, 1, (b*Sec(c+d*x) +
	// a*Sin(c+d*x))^2/(2*d)}
	public void test02600() {
		check("Integrate((b*Sec(c+d*x)+a*Sin(c+d*x))*(a*Cos(c+d*x)+b*Sec(c+d*x)*Tan(c+d*x)), x)",
				"(b*Sec(c+d*x)+a*Sin(c+d*x))^2/(2*d)");

	}

	// {(a*Cos(c+d*x)+b*Sec(c+d*x)*Tan(c+d*x))/(b*Sec(c+d*x)+a*Sin(c+d*x)), x, 1, Log(b*Sec(c+d*x) +
	// a*Sin(c+d*x))/d}
	public void test02601() {
		check("Integrate((a*Cos(c+d*x)+b*Sec(c+d*x)*Tan(c+d*x))/(b*Sec(c+d*x)+a*Sin(c+d*x)), x)",
				"Log(b*Sec(c+d*x)+a*Sin(c+d*x))/d");

	}

	// {(a*Cos(c+d*x)+b*Sec(c+d*x)*Tan(c+d*x))/(b*Sec(c+d*x)+a*Sin(c+d*x))^2, x, 1, -(1/(d*(b*Sec(c+d*x)
	// +a*Sin(c+d*x))))}
	public void test02602() {
		check("Integrate((a*Cos(c+d*x)+b*Sec(c+d*x)*Tan(c+d*x))/(b*Sec(c+d*x)+a*Sin(c+d*x))^2, x)",
				"-(1/(d*(b*Sec(c+d*x)+a*Sin(c+d*x))))");

	}

	// {(a*Cos(c+d*x)+b*Sec(c+d*x)*Tan(c+d*x))/(b*Sec(c+d*x)+a*Sin(c+d*x))^3, x, 1, -1/(2*d*(b*Sec(c +
	// d*x)+a*Sin(c+d*x))^2)}
	public void test02603() {
		check("Integrate((a*Cos(c+d*x)+b*Sec(c+d*x)*Tan(c+d*x))/(b*Sec(c+d*x)+a*Sin(c+d*x))^3, x)",
				"-1/(2*d*(b*Sec(c+d*x)+a*Sin(c+d*x))^2)");

	}

	// {f(c, d, Cos(a+b*x), r, s)*Sin(a+b*x), x, 1, CannotIntegrate(f(c, d, Cos(a+b*x), r, s)*Sin(a+b*x), x)}
	public void test02604() {
		check("Integrate(f(c, d, Cos(a+b*x), r, s)*Sin(a+b*x), x)",
				"CannotIntegrate(f(c, d, Cos(a+b*x), r, s)*Sin(a+b*x), x)");

	}

	// {Cos(a+b*x)*f(c, d, Sin(a+b*x), r, s), x, 1, CannotIntegrate(Cos(a+b*x)*f(c, d, Sin(a+b*x), r, s), x)}
	public void test02605() {
		check("Integrate(Cos(a+b*x)*f(c, d, Sin(a+b*x), r, s), x)",
				"CannotIntegrate(Cos(a+b*x)*f(c, d, Sin(a+b*x), r, s), x)");

	}

	// {f(c, d, Tan(a+b*x), r, s)*Sec(a+b*x)^2, x, 1, CannotIntegrate(f(c, d, Tan(a+b*x), r, s)*Sec(a+b*x)^2,
	// x)}
	public void test02606() {
		check("Integrate(f(c, d, Tan(a+b*x), r, s)*Sec(a+b*x)^2, x)",
				"CannotIntegrate(f(c, d, Tan(a+b*x), r, s)*Sec(a+b*x)^2, x)");

	}

	// {Csc(a+b*x)^2*f(c, d, Cot(a+b*x), r, s), x, 1, CannotIntegrate(Csc(a+b*x)^2*f(c, d, Cot(a+b*x), r, s),
	// x)}
	public void test02607() {
		check("Integrate(Csc(a+b*x)^2*f(c, d, Cot(a+b*x), r, s), x)",
				"CannotIntegrate(Csc(a+b*x)^2*f(c, d, Cot(a+b*x), r, s), x)");

	}

	// {Csc(x)*Log(Tan(x))*Sec(x), x, 1, Log(Tan(x))^2/2}
	public void test02608() {
		check("Integrate(Csc(x)*Log(Tan(x))*Sec(x), x)", "Log(Tan(x))^2/2");

	}

	// {Csc(2*x)*Log(Tan(x)), x, 1, Log(Tan(x))^2/4}
	public void test02609() {
		check("Integrate(Csc(2*x)*Log(Tan(x)), x)", "Log(Tan(x))^2/4");

	}

	// {Cos(2*Pi*x)/E^(2*Pi*x), x, 1, -Cos(2*Pi*x)/(4*E^(2*Pi*x)*Pi)+Sin(2*Pi*x)/(4*E^(2*Pi*x)*Pi)}
	public void test02610() {
		check("Integrate(Cos(2*Pi*x)/E^(2*Pi*x), x)", "-Cos(2*Pi*x)/(4*E^(2*Pi*x)*Pi)+Sin(2*Pi*x)/(4*E^(2*Pi*x)*Pi)");

	}

	// {Sin(Pi*(1+2*x)), x, 1, Cos(2*Pi*x)/(2*Pi)}
	public void test02611() {
		check("Integrate(Sin(Pi*(1+2*x)), x)", "Cos(2*Pi*x)/(2*Pi)");

	}

	// {Cos(x)/E^(3*x), x, 1, (-3*Cos(x))/(10*E^(3*x))+Sin(x)/(10*E^(3*x))}
	public void test02612() {
		check("Integrate(Cos(x)/E^(3*x), x)", "(-3*Cos(x))/(10*E^(3*x))+Sin(x)/(10*E^(3*x))");

	}

	// {(1+Cos(x))*(x+Sin(x))^3, x, 1, (x+Sin(x))^4/4}
	public void test02613() {
		check("Integrate((1+Cos(x))*(x+Sin(x))^3, x)", "(x+Sin(x))^4/4");

	}

	// {Cos(4*x)/E^(3*x), x, 1, (-3*Cos(4*x))/(25*E^(3*x))+(4*Sin(4*x))/(25*E^(3*x))}
	public void test02614() {
		check("Integrate(Cos(4*x)/E^(3*x), x)", "(-3*Cos(4*x))/(25*E^(3*x))+(4*Sin(4*x))/(25*E^(3*x))");

	}

	// {(Cos(x^(-1))*Sin(x^(-1)))/x^2, x, 1, -Sin(x^(-1))^2/2}
	public void test02615() {
		check("Integrate((Cos(x^(-1))*Sin(x^(-1)))/x^2, x)", "-Sin(x^(-1))^2/2");

	}

	// {(Cos(Sqrt(x))*Sin(Sqrt(x)))/Sqrt(x), x, 1, Sin(Sqrt(x))^2}
	public void test02616() {
		check("Integrate((Cos(Sqrt(x))*Sin(Sqrt(x)))/Sqrt(x), x)", "Sin(Sqrt(x))^2");

	}

	// {(x*Cos(x^2))/Sqrt(Sin(x^2)), x, 1, Sqrt(Sin(x^2))}
	public void test02617() {
		check("Integrate((x*Cos(x^2))/Sqrt(Sin(x^2)), x)", "Sqrt(Sin(x^2))");

	}

	// {(-Cos(x)+Sin(x))*(Cos(x)+Sin(x))^5, x, 1, -(Cos(x)+Sin(x))^6/6}
	public void test02618() {
		check("Integrate((-Cos(x)+Sin(x))*(Cos(x)+Sin(x))^5, x)", "-(Cos(x)+Sin(x))^6/6");

	}

	// {Cos(a+b*x)^n*Sin(a+b*x)^(-2-n), x, 1, -((Cos(a+b*x)^(1+n)*Sin(a+b*x)^(-1-n))/(b*(1+n)))}
	public void test02619() {
		check("Integrate(Cos(a+b*x)^n*Sin(a+b*x)^(-2-n), x)", "-((Cos(a+b*x)^(1+n)*Sin(a+b*x)^(-1-n))/(b*(1+n)))");

	}

	// {Sin(x^5)/x, x, 1, SinIntegral(x^5)/5}
	public void test02620() {
		check("Integrate(Sin(x^5)/x, x)", "SinIntegral(x^5)/5");

	}

	// {x*Cos(2*x^2)*Sin(2*x^2)^(3/4), x, 1, Sin(2*x^2)^(7/4)/7}
	public void test02621() {
		check("Integrate(x*Cos(2*x^2)*Sin(2*x^2)^(3/4), x)", "Sin(2*x^2)^(7/4)/7");

	}

	// {x*Sec(x^2)^2*Tan(x^2)^2, x, 1, Tan(x^2)^3/6}
	public void test02622() {
		check("Integrate(x*Sec(x^2)^2*Tan(x^2)^2, x)", "Tan(x^2)^3/6");

	}

	// {x^2*Cos(a+b*x^3)^7*Sin(a+b*x^3), x, 1, -Cos(a+b*x^3)^8/(24*b)}
	public void test02623() {
		check("Integrate(x^2*Cos(a+b*x^3)^7*Sin(a+b*x^3), x)", "-Cos(a+b*x^3)^8/(24*b)");

	}

	// {(Cos(a+b*x)-Sin(a+b*x))/(Cos(a+b*x)+Sin(a+b*x)), x, 1, Log(Cos(a+b*x)+Sin(a+b*x))/b}
	public void test02624() {
		check("Integrate((Cos(a+b*x)-Sin(a+b*x))/(Cos(a+b*x)+Sin(a+b*x)), x)", "Log(Cos(a+b*x)+Sin(a+b*x))/b");

	}

	// {(b*x)^m*ArcSin(a*x)^4, x, 1, ((b*x)^(1+m)*ArcSin(a*x)^4)/(b*(1+m))-(4*a*Unintegrable(((b*x)^(1 +
	// m)*ArcSin(a*x)^3)/Sqrt(1-a^2*x^2), x))/(b*(1+m))}
	public void test02625() {
		check("Integrate((b*x)^m*ArcSin(a*x)^4, x)",
				"((b*x)^(1+m)*ArcSin(a*x)^4)/(b*(1+m))-(4*a*Unintegrable(((b*x)^(1+m)*ArcSin(a*x)^3)/Sqrt(1-a^2*x^2), x))/(b*(1+m))");

	}

	// {(b*x)^m*ArcSin(a*x)^3, x, 1, ((b*x)^(1+m)*ArcSin(a*x)^3)/(b*(1+m))-(3*a*Unintegrable(((b*x)^(1 +
	// m)*ArcSin(a*x)^2)/Sqrt(1-a^2*x^2), x))/(b*(1+m))}
	public void test02626() {
		check("Integrate((b*x)^m*ArcSin(a*x)^3, x)",
				"((b*x)^(1+m)*ArcSin(a*x)^3)/(b*(1+m))-(3*a*Unintegrable(((b*x)^(1+m)*ArcSin(a*x)^2)/Sqrt(1-a^2*x^2), x))/(b*(1+m))");

	}

	// {(d*x)^(3/2)*(a+b*ArcSin(c*x))^3, x, 1, (2*(d*x)^(5/2)*(a+b*ArcSin(c*x))^3)/(5*d) -
	// (6*b*c*Unintegrable(((d*x)^(5/2)*(a+b*ArcSin(c*x))^2)/Sqrt(1-c^2*x^2), x))/(5*d)}
	public void test02627() {
		check("Integrate((d*x)^(3/2)*(a+b*ArcSin(c*x))^3, x)",
				"(2*(d*x)^(5/2)*(a+b*ArcSin(c*x))^3)/(5*d)-(6*b*c*Unintegrable(((d*x)^(5/2)*(a+b*ArcSin(c*x))^2)/Sqrt(1-c^2*x^2), x))/(5*d)");

	}

	// {Sqrt(d*x)*(a+b*ArcSin(c*x))^3, x, 1, (2*(d*x)^(3/2)*(a+b*ArcSin(c*x))^3)/(3*d) -
	// (2*b*c*Unintegrable(((d*x)^(3/2)*(a+b*ArcSin(c*x))^2)/Sqrt(1-c^2*x^2), x))/d}
	public void test02628() {
		check("Integrate(Sqrt(d*x)*(a+b*ArcSin(c*x))^3, x)",
				"(2*(d*x)^(3/2)*(a+b*ArcSin(c*x))^3)/(3*d)-(2*b*c*Unintegrable(((d*x)^(3/2)*(a+b*ArcSin(c*x))^2)/Sqrt(1-c^2*x^2), x))/d");

	}

	// {(a+b*ArcSin(c*x))^3/Sqrt(d*x), x, 1, (2*Sqrt(d*x)*(a+b*ArcSin(c*x))^3)/d-(6*b*c*Unintegrable((Sqrt(d*x)*(a
	// +b*ArcSin(c*x))^2)/Sqrt(1-c^2*x^2), x))/d}
	public void test02629() {
		check("Integrate((a+b*ArcSin(c*x))^3/Sqrt(d*x), x)",
				"(2*Sqrt(d*x)*(a+b*ArcSin(c*x))^3)/d-(6*b*c*Unintegrable((Sqrt(d*x)*(a+b*ArcSin(c*x))^2)/Sqrt(1-c^2*x^2), x))/d");

	}

	// {(a+b*ArcSin(c*x))^3/(d*x)^(3/2), x, 1, (-2*(a+b*ArcSin(c*x))^3)/(d*Sqrt(d*x))+(6*b*c*Unintegrable((a +
	// b*ArcSin(c*x))^2/(Sqrt(d*x)*Sqrt(1-c^2*x^2)), x))/d}
	public void test02630() {
		check("Integrate((a+b*ArcSin(c*x))^3/(d*x)^(3/2), x)",
				"(-2*(a+b*ArcSin(c*x))^3)/(d*Sqrt(d*x))+(6*b*c*Unintegrable((a+b*ArcSin(c*x))^2/(Sqrt(d*x)*Sqrt(1-c^2*x^2)), x))/d");

	}

	// {(a+b*ArcSin(c*x))^3/(d*x)^(5/2), x, 1, (-2*(a+b*ArcSin(c*x))^3)/(3*d*(d*x)^(3/2))+(2*b*c*Unintegrable((a +
	// b*ArcSin(c*x))^2/((d*x)^(3/2)*Sqrt(1-c^2*x^2)), x))/d}
	public void test02631() {
		check("Integrate((a+b*ArcSin(c*x))^3/(d*x)^(5/2), x)",
				"(-2*(a+b*ArcSin(c*x))^3)/(3*d*(d*x)^(3/2))+(2*b*c*Unintegrable((a+b*ArcSin(c*x))^2/((d*x)^(3/2)*Sqrt(1-c^2*x^2)), x))/d");

	}

	// {ArcSin(a*x)/Sqrt(1-a^2*x^2), x, 1, ArcSin(a*x)^2/(2*a)}
	public void test02632() {
		check("Integrate(ArcSin(a*x)/Sqrt(1-a^2*x^2), x)", "ArcSin(a*x)^2/(2*a)");

	}

	// {((f*x)^(3/2)*(a+b*ArcSin(c*x)))/Sqrt(1-c^2*x^2), x, 1, (2*(f*x)^(5/2)*(a +
	// b*ArcSin(c*x))*Hypergeometric2F1(1/2, 5/4, 9/4, c^2*x^2))/(5*f)-(4*b*c*(f*x)^(7/2)*HypergeometricPFQ({1, 7/4,
	// 7/4}, {9/4, 11/4}, c^2*x^2))/(35*f^2)}
	public void test02633() {
		check("Integrate(((f*x)^(3/2)*(a+b*ArcSin(c*x)))/Sqrt(1-c^2*x^2), x)",
				"(2*(f*x)^(5/2)*(a+b*ArcSin(c*x))*Hypergeometric2F1(1/2, 5/4, 9/4, c^2*x^2))/(5*f)-(4*b*c*(f*x)^(7/2)*HypergeometricPFQ({1, 7/4, 7/4}, {9/4, 11/4}, c^2*x^2))/(35*f^2)");

	}

	// {(x^m*ArcSin(a*x))/Sqrt(1-a^2*x^2), x, 1, (x^(1+m)*ArcSin(a*x)*Hypergeometric2F1(1/2, (1+m)/2, (3+m)/2,
	// a^2*x^2))/(1+m)-(a*x^(2+m)*HypergeometricPFQ({1, 1+m/2, 1+m/2}, {3/2+m/2, 2+m/2}, a^2*x^2))/(2 +
	// 3*m+m^2)}
	public void test02634() {
		check("Integrate((x^m*ArcSin(a*x))/Sqrt(1-a^2*x^2), x)",
				"(x^(1+m)*ArcSin(a*x)*Hypergeometric2F1(1/2, (1+m)/2, (3+m)/2, a^2*x^2))/(1+m)-(a*x^(2+m)*HypergeometricPFQ({1, 1+m/2, 1+m/2}, {3/2+m/2, 2+m/2}, a^2*x^2))/(2+3*m+m^2)");

	}

	// {ArcSin(a*x)^2/Sqrt(1-a^2*x^2), x, 1, ArcSin(a*x)^3/(3*a)}
	public void test02635() {
		check("Integrate(ArcSin(a*x)^2/Sqrt(1-a^2*x^2), x)", "ArcSin(a*x)^3/(3*a)");

	}

	// {ArcSin(a*x)^3/Sqrt(1-a^2*x^2), x, 1, ArcSin(a*x)^4/(4*a)}
	public void test02636() {
		check("Integrate(ArcSin(a*x)^3/Sqrt(1-a^2*x^2), x)", "ArcSin(a*x)^4/(4*a)");

	}

	// {1/(Sqrt(1-a^2*x^2)*ArcSin(a*x)), x, 1, Log(ArcSin(a*x))/a}
	public void test02637() {
		check("Integrate(1/(Sqrt(1-a^2*x^2)*ArcSin(a*x)), x)", "Log(ArcSin(a*x))/a");

	}

	// {1/(Sqrt(1-c^2*x^2)*(a+b*ArcSin(c*x))), x, 1, Log(a+b*ArcSin(c*x))/(b*c)}
	public void test02638() {
		check("Integrate(1/(Sqrt(1-c^2*x^2)*(a+b*ArcSin(c*x))), x)", "Log(a+b*ArcSin(c*x))/(b*c)");

	}

	// {1/((c-a^2*c*x^2)*ArcSin(a*x)^2), x, 1, -(1/(a*c*Sqrt(1-a^2*x^2)*ArcSin(a*x)))+(a*Unintegrable(x/((1 -
	// a^2*x^2)^(3/2)*ArcSin(a*x)), x))/c}
	public void test02639() {
		check("Integrate(1/((c-a^2*c*x^2)*ArcSin(a*x)^2), x)",
				"-(1/(a*c*Sqrt(1-a^2*x^2)*ArcSin(a*x)))+(a*Unintegrable(x/((1-a^2*x^2)^(3/2)*ArcSin(a*x)), x))/c");

	}

	// {1/((c-a^2*c*x^2)^2*ArcSin(a*x)^2), x, 1, -(1/(a*c^2*(1-a^2*x^2)^(3/2)*ArcSin(a*x))) +
	// (3*a*Unintegrable(x/((1-a^2*x^2)^(5/2)*ArcSin(a*x)), x))/c^2}
	public void test02640() {
		check("Integrate(1/((c-a^2*c*x^2)^2*ArcSin(a*x)^2), x)",
				"-(1/(a*c^2*(1-a^2*x^2)^(3/2)*ArcSin(a*x)))+(3*a*Unintegrable(x/((1-a^2*x^2)^(5/2)*ArcSin(a*x)), x))/c^2");

	}

	// {Sqrt(1-c^2*x^2)/(x^2*(a+b*ArcSin(c*x))^2), x, 1, -((1-c^2*x^2)/(b*c*x^2*(a+b*ArcSin(c*x)))) -
	// (2*Unintegrable(1/(x^3*(a+b*ArcSin(c*x))), x))/(b*c)}
	public void test02641() {
		check("Integrate(Sqrt(1-c^2*x^2)/(x^2*(a+b*ArcSin(c*x))^2), x)",
				"-((1-c^2*x^2)/(b*c*x^2*(a+b*ArcSin(c*x))))-(2*Unintegrable(1/(x^3*(a+b*ArcSin(c*x))), x))/(b*c)");

	}

	// {(1-c^2*x^2)^(3/2)/(x^2*(a+b*ArcSin(c*x))^2), x, 1, -((1-c^2*x^2)^2/(b*c*x^2*(a+b*ArcSin(c*x)))) -
	// (2*Unintegrable((1-c^2*x^2)/(x^3*(a+b*ArcSin(c*x))), x))/(b*c)-(2*c*Unintegrable((1-c^2*x^2)/(x*(a +
	// b*ArcSin(c*x))), x))/b}
	public void test02642() {
		check("Integrate((1-c^2*x^2)^(3/2)/(x^2*(a+b*ArcSin(c*x))^2), x)",
				"-((1-c^2*x^2)^2/(b*c*x^2*(a+b*ArcSin(c*x))))-(2*Unintegrable((1-c^2*x^2)/(x^3*(a+b*ArcSin(c*x))), x))/(b*c)-(2*c*Unintegrable((1-c^2*x^2)/(x*(a+b*ArcSin(c*x))), x))/b");

	}

	// {(1-c^2*x^2)^(3/2)/(x^4*(a+b*ArcSin(c*x))^2), x, 1, -((1-c^2*x^2)^2/(b*c*x^4*(a+b*ArcSin(c*x)))) -
	// (4*Unintegrable((1-c^2*x^2)/(x^5*(a+b*ArcSin(c*x))), x))/(b*c)}
	public void test02643() {
		check("Integrate((1-c^2*x^2)^(3/2)/(x^4*(a+b*ArcSin(c*x))^2), x)",
				"-((1-c^2*x^2)^2/(b*c*x^4*(a+b*ArcSin(c*x))))-(4*Unintegrable((1-c^2*x^2)/(x^5*(a+b*ArcSin(c*x))), x))/(b*c)");

	}

	// {(1-c^2*x^2)^(5/2)/(x^2*(a+b*ArcSin(c*x))^2), x, 1, -((1-c^2*x^2)^3/(b*c*x^2*(a+b*ArcSin(c*x)))) -
	// (2*Unintegrable((1-c^2*x^2)^2/(x^3*(a+b*ArcSin(c*x))), x))/(b*c)-(4*c*Unintegrable((1-c^2*x^2)^2/(x*(a +
	// b*ArcSin(c*x))), x))/b}
	public void test02644() {
		check("Integrate((1-c^2*x^2)^(5/2)/(x^2*(a+b*ArcSin(c*x))^2), x)",
				"-((1-c^2*x^2)^3/(b*c*x^2*(a+b*ArcSin(c*x))))-(2*Unintegrable((1-c^2*x^2)^2/(x^3*(a+b*ArcSin(c*x))), x))/(b*c)-(4*c*Unintegrable((1-c^2*x^2)^2/(x*(a+b*ArcSin(c*x))), x))/b");

	}

	// {x^m/(Sqrt(1-c^2*x^2)*(a+b*ArcSin(c*x))^2), x, 1, -(x^m/(b*c*(a+b*ArcSin(c*x))))+(m*Unintegrable(x^(-1 +
	// m)/(a+b*ArcSin(c*x)), x))/(b*c)}
	public void test02645() {
		check("Integrate(x^m/(Sqrt(1-c^2*x^2)*(a+b*ArcSin(c*x))^2), x)",
				"-(x^m/(b*c*(a+b*ArcSin(c*x))))+(m*Unintegrable(x^(-1+m)/(a+b*ArcSin(c*x)), x))/(b*c)");

	}

	// {1/(Sqrt(1-c^2*x^2)*(a+b*ArcSin(c*x))^2), x, 1, -(1/(b*c*(a+b*ArcSin(c*x))))}
	public void test02646() {
		check("Integrate(1/(Sqrt(1-c^2*x^2)*(a+b*ArcSin(c*x))^2), x)", "-(1/(b*c*(a+b*ArcSin(c*x))))");

	}

	// {1/(x*Sqrt(1-c^2*x^2)*(a+b*ArcSin(c*x))^2), x, 1, -(1/(b*c*x*(a+b*ArcSin(c*x))))-Unintegrable(1/(x^2*(a +
	// b*ArcSin(c*x))), x)/(b*c)}
	public void test02647() {
		check("Integrate(1/(x*Sqrt(1-c^2*x^2)*(a+b*ArcSin(c*x))^2), x)",
				"-(1/(b*c*x*(a+b*ArcSin(c*x))))-Unintegrable(1/(x^2*(a+b*ArcSin(c*x))), x)/(b*c)");

	}

	// {1/(x^2*Sqrt(1-c^2*x^2)*(a+b*ArcSin(c*x))^2), x, 1, -(1/(b*c*x^2*(a+b*ArcSin(c*x)))) -
	// (2*Unintegrable(1/(x^3*(a+b*ArcSin(c*x))), x))/(b*c)}
	public void test02648() {
		check("Integrate(1/(x^2*Sqrt(1-c^2*x^2)*(a+b*ArcSin(c*x))^2), x)",
				"-(1/(b*c*x^2*(a+b*ArcSin(c*x))))-(2*Unintegrable(1/(x^3*(a+b*ArcSin(c*x))), x))/(b*c)");

	}

	// {x^2/((1-c^2*x^2)^(3/2)*(a+b*ArcSin(c*x))^2), x, 1, -(x^2/(b*c*(1-c^2*x^2)*(a+b*ArcSin(c*x)))) +
	// (2*Unintegrable(x/((1-c^2*x^2)^2*(a+b*ArcSin(c*x))), x))/(b*c)}
	public void test02649() {
		check("Integrate(x^2/((1-c^2*x^2)^(3/2)*(a+b*ArcSin(c*x))^2), x)",
				"-(x^2/(b*c*(1-c^2*x^2)*(a+b*ArcSin(c*x))))+(2*Unintegrable(x/((1-c^2*x^2)^2*(a+b*ArcSin(c*x))), x))/(b*c)");

	}

	// {1/((1-c^2*x^2)^(3/2)*(a+b*ArcSin(c*x))^2), x, 1, -(1/(b*c*(1-c^2*x^2)*(a+b*ArcSin(c*x)))) +
	// (2*c*Unintegrable(x/((1-c^2*x^2)^2*(a+b*ArcSin(c*x))), x))/b}
	public void test02650() {
		check("Integrate(1/((1-c^2*x^2)^(3/2)*(a+b*ArcSin(c*x))^2), x)",
				"-(1/(b*c*(1-c^2*x^2)*(a+b*ArcSin(c*x))))+(2*c*Unintegrable(x/((1-c^2*x^2)^2*(a+b*ArcSin(c*x))), x))/b");

	}

	// {1/((1-c^2*x^2)^(5/2)*(a+b*ArcSin(c*x))^2), x, 1, -(1/(b*c*(1-c^2*x^2)^2*(a+b*ArcSin(c*x)))) +
	// (4*c*Unintegrable(x/((1-c^2*x^2)^3*(a+b*ArcSin(c*x))), x))/b}
	public void test02651() {
		check("Integrate(1/((1-c^2*x^2)^(5/2)*(a+b*ArcSin(c*x))^2), x)",
				"-(1/(b*c*(1-c^2*x^2)^2*(a+b*ArcSin(c*x))))+(4*c*Unintegrable(x/((1-c^2*x^2)^3*(a+b*ArcSin(c*x))), x))/b");

	}

	// {1/(Sqrt(1-a^2*x^2)*ArcSin(a*x)^3), x, 1, -1/(2*a*ArcSin(a*x)^2)}
	public void test02652() {
		check("Integrate(1/(Sqrt(1-a^2*x^2)*ArcSin(a*x)^3), x)", "-1/(2*a*ArcSin(a*x)^2)");

	}

	// {Sqrt(ArcSin(a*x))/(c-a^2*c*x^2)^(3/2), x, 1, (x*Sqrt(ArcSin(a*x)))/(c*Sqrt(c-a^2*c*x^2))-(a*Sqrt(1 -
	// a^2*x^2)*Unintegrable(x/((1-a^2*x^2)*Sqrt(ArcSin(a*x))), x))/(2*c*Sqrt(c-a^2*c*x^2))}
	public void test02653() {
		check("Integrate(Sqrt(ArcSin(a*x))/(c-a^2*c*x^2)^(3/2), x)",
				"(x*Sqrt(ArcSin(a*x)))/(c*Sqrt(c-a^2*c*x^2))-(a*Sqrt(1-a^2*x^2)*Unintegrable(x/((1-a^2*x^2)*Sqrt(ArcSin(a*x))), x))/(2*c*Sqrt(c-a^2*c*x^2))");

	}

	// {ArcSin(a*x)^(3/2)/(c-a^2*c*x^2)^(3/2), x, 1, (x*ArcSin(a*x)^(3/2))/(c*Sqrt(c-a^2*c*x^2))-(3*a*Sqrt(1 -
	// a^2*x^2)*Unintegrable((x*Sqrt(ArcSin(a*x)))/(1-a^2*x^2), x))/(2*c*Sqrt(c-a^2*c*x^2))}
	public void test02654() {
		check("Integrate(ArcSin(a*x)^(3/2)/(c-a^2*c*x^2)^(3/2), x)",
				"(x*ArcSin(a*x)^(3/2))/(c*Sqrt(c-a^2*c*x^2))-(3*a*Sqrt(1-a^2*x^2)*Unintegrable((x*Sqrt(ArcSin(a*x)))/(1-a^2*x^2), x))/(2*c*Sqrt(c-a^2*c*x^2))");

	}

	// {ArcSin(a*x)^(5/2)/(c-a^2*c*x^2)^(3/2), x, 1, (x*ArcSin(a*x)^(5/2))/(c*Sqrt(c-a^2*c*x^2))-(5*a*Sqrt(1 -
	// a^2*x^2)*Unintegrable((x*ArcSin(a*x)^(3/2))/(1-a^2*x^2), x))/(2*c*Sqrt(c-a^2*c*x^2))}
	public void test02655() {
		check("Integrate(ArcSin(a*x)^(5/2)/(c-a^2*c*x^2)^(3/2), x)",
				"(x*ArcSin(a*x)^(5/2))/(c*Sqrt(c-a^2*c*x^2))-(5*a*Sqrt(1-a^2*x^2)*Unintegrable((x*ArcSin(a*x)^(3/2))/(1-a^2*x^2), x))/(2*c*Sqrt(c-a^2*c*x^2))");

	}

	// {Sqrt(ArcSin(x/a))/(a^2-x^2)^(3/2), x, 1, (x*Sqrt(ArcSin(x/a)))/(a^2*Sqrt(a^2-x^2))-(Sqrt(1 -
	// x^2/a^2)*Unintegrable(x/((1-x^2/a^2)*Sqrt(ArcSin(x/a))), x))/(2*a^3*Sqrt(a^2-x^2))}
	public void test02656() {
		check("Integrate(Sqrt(ArcSin(x/a))/(a^2-x^2)^(3/2), x)",
				"(x*Sqrt(ArcSin(x/a)))/(a^2*Sqrt(a^2-x^2))-(Sqrt(1-x^2/a^2)*Unintegrable(x/((1-x^2/a^2)*Sqrt(ArcSin(x/a))), x))/(2*a^3*Sqrt(a^2-x^2))");

	}

	// {ArcSin(x/a)^(3/2)/(a^2-x^2)^(3/2), x, 1, (x*ArcSin(x/a)^(3/2))/(a^2*Sqrt(a^2-x^2))-(3*Sqrt(1 -
	// x^2/a^2)*Unintegrable((x*Sqrt(ArcSin(x/a)))/(1-x^2/a^2), x))/(2*a^3*Sqrt(a^2-x^2))}
	public void test02657() {
		check("Integrate(ArcSin(x/a)^(3/2)/(a^2-x^2)^(3/2), x)",
				"(x*ArcSin(x/a)^(3/2))/(a^2*Sqrt(a^2-x^2))-(3*Sqrt(1-x^2/a^2)*Unintegrable((x*Sqrt(ArcSin(x/a)))/(1-x^2/a^2), x))/(2*a^3*Sqrt(a^2-x^2))");

	}

	// {1/((c-a^2*c*x^2)^(3/2)*ArcSin(a*x)^(3/2)), x, 1, (-2*Sqrt(1-a^2*x^2))/(a*(c -
	// a^2*c*x^2)^(3/2)*Sqrt(ArcSin(a*x)))+(4*a*Sqrt(1-a^2*x^2)*Unintegrable(x/((1-a^2*x^2)^2*Sqrt(ArcSin(a*x))),
	// x))/(c*Sqrt(c-a^2*c*x^2))}
	public void test02658() {
		check("Integrate(1/((c-a^2*c*x^2)^(3/2)*ArcSin(a*x)^(3/2)), x)",
				"(-2*Sqrt(1-a^2*x^2))/(a*(c-a^2*c*x^2)^(3/2)*Sqrt(ArcSin(a*x)))+(4*a*Sqrt(1-a^2*x^2)*Unintegrable(x/((1-a^2*x^2)^2*Sqrt(ArcSin(a*x))), x))/(c*Sqrt(c-a^2*c*x^2))");

	}

	// {1/((c-a^2*c*x^2)^(5/2)*ArcSin(a*x)^(3/2)), x, 1, (-2*Sqrt(1-a^2*x^2))/(a*(c -
	// a^2*c*x^2)^(5/2)*Sqrt(ArcSin(a*x)))+(8*a*Sqrt(1-a^2*x^2)*Unintegrable(x/((1-a^2*x^2)^3*Sqrt(ArcSin(a*x))),
	// x))/(c^2*Sqrt(c-a^2*c*x^2))}
	public void test02659() {
		check("Integrate(1/((c-a^2*c*x^2)^(5/2)*ArcSin(a*x)^(3/2)), x)",
				"(-2*Sqrt(1-a^2*x^2))/(a*(c-a^2*c*x^2)^(5/2)*Sqrt(ArcSin(a*x)))+(8*a*Sqrt(1-a^2*x^2)*Unintegrable(x/((1-a^2*x^2)^3*Sqrt(ArcSin(a*x))), x))/(c^2*Sqrt(c-a^2*c*x^2))");

	}

	// {1/((c-a^2*c*x^2)^(3/2)*ArcSin(a*x)^(5/2)), x, 1, (-2*Sqrt(1-a^2*x^2))/(3*a*(c -
	// a^2*c*x^2)^(3/2)*ArcSin(a*x)^(3/2))+(4*a*Sqrt(1-a^2*x^2)*Unintegrable(x/((1-a^2*x^2)^2*ArcSin(a*x)^(3/2)),
	// x))/(3*c*Sqrt(c-a^2*c*x^2))}
	public void test02660() {
		check("Integrate(1/((c-a^2*c*x^2)^(3/2)*ArcSin(a*x)^(5/2)), x)",
				"(-2*Sqrt(1-a^2*x^2))/(3*a*(c-a^2*c*x^2)^(3/2)*ArcSin(a*x)^(3/2))+(4*a*Sqrt(1-a^2*x^2)*Unintegrable(x/((1-a^2*x^2)^2*ArcSin(a*x)^(3/2)), x))/(3*c*Sqrt(c-a^2*c*x^2))");

	}

	// {1/((c-a^2*c*x^2)^(5/2)*ArcSin(a*x)^(5/2)), x, 1, (-2*Sqrt(1-a^2*x^2))/(3*a*(c -
	// a^2*c*x^2)^(5/2)*ArcSin(a*x)^(3/2))+(8*a*Sqrt(1-a^2*x^2)*Unintegrable(x/((1-a^2*x^2)^3*ArcSin(a*x)^(3/2)),
	// x))/(3*c^2*Sqrt(c-a^2*c*x^2))}
	public void test02661() {
		check("Integrate(1/((c-a^2*c*x^2)^(5/2)*ArcSin(a*x)^(5/2)), x)",
				"(-2*Sqrt(1-a^2*x^2))/(3*a*(c-a^2*c*x^2)^(5/2)*ArcSin(a*x)^(3/2))+(8*a*Sqrt(1-a^2*x^2)*Unintegrable(x/((1-a^2*x^2)^3*ArcSin(a*x)^(3/2)), x))/(3*c^2*Sqrt(c-a^2*c*x^2))");

	}

	// {ArcSin(a*x)^n/Sqrt(1-a^2*x^2), x, 1, ArcSin(a*x)^(1+n)/(a*(1+n))}
	public void test02662() {
		check("Integrate(ArcSin(a*x)^n/Sqrt(1-a^2*x^2), x)", "ArcSin(a*x)^(1+n)/(a*(1+n))");

	}

	// {(d+e*x)^m*(a+b*ArcSin(c*x))^2, x, 1, ((d+e*x)^(1+m)*(a+b*ArcSin(c*x))^2)/(e*(1+m)) -
	// (2*b*c*Unintegrable(((d+e*x)^(1+m)*(a+b*ArcSin(c*x)))/Sqrt(1-c^2*x^2), x))/(e*(1+m))}
	public void test02663() {
		check("Integrate((d+e*x)^m*(a+b*ArcSin(c*x))^2, x)",
				"((d+e*x)^(1+m)*(a+b*ArcSin(c*x))^2)/(e*(1+m))-(2*b*c*Unintegrable(((d+e*x)^(1+m)*(a+b*ArcSin(c*x)))/Sqrt(1-c^2*x^2), x))/(e*(1+m))");

	}

	// {1/(x*ArcSin(a+b*x)), x, 1, Unintegrable(1/(x*ArcSin(a+b*x)), x)}
	public void test02664() {
		check("Integrate(1/(x*ArcSin(a+b*x)), x)", "Unintegrable(1/(x*ArcSin(a+b*x)), x)");

	}

	// {1/(x*ArcSin(a+b*x)^2), x, 1, Unintegrable(1/(x*ArcSin(a+b*x)^2), x)}
	public void test02665() {
		check("Integrate(1/(x*ArcSin(a+b*x)^2), x)", "Unintegrable(1/(x*ArcSin(a+b*x)^2), x)");

	}

	// {1/(x*ArcSin(a+b*x)^3), x, 1, Unintegrable(1/(x*ArcSin(a+b*x)^3), x)}
	public void test02666() {
		check("Integrate(1/(x*ArcSin(a+b*x)^3), x)", "Unintegrable(1/(x*ArcSin(a+b*x)^3), x)");

	}

	// {x^m*(a+b*ArcSin(c+d*x))^n, x, 1, Unintegrable(x^m*(a+b*ArcSin(c+d*x))^n, x)}
	public void test02667() {
		check("Integrate(x^m*(a+b*ArcSin(c+d*x))^n, x)", "Unintegrable(x^m*(a+b*ArcSin(c+d*x))^n, x)");

	}

	// {(a+b*ArcSin(c+d*x))^n/x, x, 1, Unintegrable((a+b*ArcSin(c+d*x))^n/x, x)}
	public void test02668() {
		check("Integrate((a+b*ArcSin(c+d*x))^n/x, x)", "Unintegrable((a+b*ArcSin(c+d*x))^n/x, x)");

	}

	// {(c*e+d*e*x)^m/(a+b*ArcSin(c+d*x)), x, 1, Unintegrable((e*(c+d*x))^m/(a+b*ArcSin(c+d*x)), x)}
	public void test02669() {
		check("Integrate((c*e+d*e*x)^m/(a+b*ArcSin(c+d*x)), x)", "Unintegrable((e*(c+d*x))^m/(a+b*ArcSin(c+d*x)), x)");

	}

	// {1/((1-a^2-2*a*b*x-b^2*x^2)^(3/2)*ArcSin(a+b*x)), x, 1, Unintegrable(1/((1-(a+b*x)^2)^(3/2)*ArcSin(a
	// +b*x)), x)}
	public void test02670() {
		check("Integrate(1/((1-a^2-2*a*b*x-b^2*x^2)^(3/2)*ArcSin(a+b*x)), x)",
				"Unintegrable(1/((1-(a+b*x)^2)^(3/2)*ArcSin(a+b*x)), x)");

	}

	// {(a+b*ArcSin(1+d*x^2))^(-1), x, 1, -(x*CosIntegral((a+b*ArcSin(1+d*x^2))/(2*b))*(Cos(a/(2*b)) -
	// Sin(a/(2*b))))/(2*b*(Cos(ArcSin(1+d*x^2)/2)-Sin(ArcSin(1+d*x^2)/2)))-(x*(Cos(a/(2*b)) +
	// Sin(a/(2*b)))*SinIntegral((a+b*ArcSin(1+d*x^2))/(2*b)))/(2*b*(Cos(ArcSin(1+d*x^2)/2)-Sin(ArcSin(1 +
	// d*x^2)/2)))}
	public void test02671() {
		check("Integrate((a+b*ArcSin(1+d*x^2))^(-1), x)",
				"-(x*CosIntegral((a+b*ArcSin(1+d*x^2))/(2*b))*(Cos(a/(2*b))-Sin(a/(2*b))))/(2*b*(Cos(ArcSin(1+d*x^2)/2)-Sin(ArcSin(1+d*x^2)/2)))-(x*(Cos(a/(2*b))+Sin(a/(2*b)))*SinIntegral((a+b*ArcSin(1+d*x^2))/(2*b)))/(2*b*(Cos(ArcSin(1+d*x^2)/2)-Sin(ArcSin(1+d*x^2)/2)))");

	}

	// {(a+b*ArcSin(1+d*x^2))^(-2), x, 1, -Sqrt(-2*d*x^2-d^2*x^4)/(2*b*d*x*(a+b*ArcSin(1+d*x^2))) -
	// (x*CosIntegral((a+b*ArcSin(1+d*x^2))/(2*b))*(Cos(a/(2*b))+Sin(a/(2*b))))/(4*b^2*(Cos(ArcSin(1+d*x^2)/2) -
	// Sin(ArcSin(1+d*x^2)/2)))+(x*(Cos(a/(2*b))-Sin(a/(2*b)))*SinIntegral((a+b*ArcSin(1 +
	// d*x^2))/(2*b)))/(4*b^2*(Cos(ArcSin(1+d*x^2)/2)-Sin(ArcSin(1+d*x^2)/2)))}
	public void test02672() {
		check("Integrate((a+b*ArcSin(1+d*x^2))^(-2), x)",
				"-Sqrt(-2*d*x^2-d^2*x^4)/(2*b*d*x*(a+b*ArcSin(1+d*x^2)))-(x*CosIntegral((a+b*ArcSin(1+d*x^2))/(2*b))*(Cos(a/(2*b))+Sin(a/(2*b))))/(4*b^2*(Cos(ArcSin(1+d*x^2)/2)-Sin(ArcSin(1+d*x^2)/2)))+(x*(Cos(a/(2*b))-Sin(a/(2*b)))*SinIntegral((a+b*ArcSin(1+d*x^2))/(2*b)))/(4*b^2*(Cos(ArcSin(1+d*x^2)/2)-Sin(ArcSin(1+d*x^2)/2)))");

	}

	// {(a-b*ArcSin(1-d*x^2))^(-1), x, 1, (x*CosIntegral(-(a-b*ArcSin(1-d*x^2))/(2*b))*(Cos(a/(2*b)) +
	// Sin(a/(2*b))))/(2*b*(Cos(ArcSin(1-d*x^2)/2)-Sin(ArcSin(1-d*x^2)/2)))-(x*(Cos(a/(2*b)) -
	// Sin(a/(2*b)))*SinIntegral(a/(2*b)-ArcSin(1-d*x^2)/2))/(2*b*(Cos(ArcSin(1-d*x^2)/2)-Sin(ArcSin(1 -
	// d*x^2)/2)))}
	public void test02673() {
		check("Integrate((a-b*ArcSin(1-d*x^2))^(-1), x)",
				"(x*CosIntegral(-(a-b*ArcSin(1-d*x^2))/(2*b))*(Cos(a/(2*b))+Sin(a/(2*b))))/(2*b*(Cos(ArcSin(1-d*x^2)/2)-Sin(ArcSin(1-d*x^2)/2)))-(x*(Cos(a/(2*b))-Sin(a/(2*b)))*SinIntegral(a/(2*b)-ArcSin(1-d*x^2)/2))/(2*b*(Cos(ArcSin(1-d*x^2)/2)-Sin(ArcSin(1-d*x^2)/2)))");

	}

	// {(a-b*ArcSin(1-d*x^2))^(-2), x, 1, -Sqrt(2*d*x^2-d^2*x^4)/(2*b*d*x*(a-b*ArcSin(1-d*x^2))) -
	// (x*CosIntegral(-(a-b*ArcSin(1-d*x^2))/(2*b))*(Cos(a/(2*b))-Sin(a/(2*b))))/(4*b^2*(Cos(ArcSin(1-d*x^2)/2)
	// -Sin(ArcSin(1-d*x^2)/2)))-(x*(Cos(a/(2*b))+Sin(a/(2*b)))*SinIntegral(a/(2*b)-ArcSin(1 -
	// d*x^2)/2))/(4*b^2*(Cos(ArcSin(1-d*x^2)/2)-Sin(ArcSin(1-d*x^2)/2)))}
	public void test02674() {
		check("Integrate((a-b*ArcSin(1-d*x^2))^(-2), x)",
				"-Sqrt(2*d*x^2-d^2*x^4)/(2*b*d*x*(a-b*ArcSin(1-d*x^2)))-(x*CosIntegral(-(a-b*ArcSin(1-d*x^2))/(2*b))*(Cos(a/(2*b))-Sin(a/(2*b))))/(4*b^2*(Cos(ArcSin(1-d*x^2)/2)-Sin(ArcSin(1-d*x^2)/2)))-(x*(Cos(a/(2*b))+Sin(a/(2*b)))*SinIntegral(a/(2*b)-ArcSin(1-d*x^2)/2))/(4*b^2*(Cos(ArcSin(1-d*x^2)/2)-Sin(ArcSin(1-d*x^2)/2)))");

	}

	// {Sqrt(a+b*ArcSin(1+d*x^2)), x, 1, x*Sqrt(a+b*ArcSin(1+d*x^2))+(Sqrt(Pi)*x*FresnelS((Sqrt(b^(-1))*Sqrt(a
	// +b*ArcSin(1+d*x^2)))/Sqrt(Pi))*(Cos(a/(2*b))-Sin(a/(2*b))))/(Sqrt(b^(-1))*(Cos(ArcSin(1+d*x^2)/2) -
	// Sin(ArcSin(1+d*x^2)/2)))-(Sqrt(Pi)*x*FresnelC((Sqrt(b^(-1))*Sqrt(a+b*ArcSin(1 +
	// d*x^2)))/Sqrt(Pi))*(Cos(a/(2*b))+Sin(a/(2*b))))/(Sqrt(b^(-1))*(Cos(ArcSin(1+d*x^2)/2)-Sin(ArcSin(1 +
	// d*x^2)/2)))}
	public void test02675() {
		check("Integrate(Sqrt(a+b*ArcSin(1+d*x^2)), x)",
				"x*Sqrt(a+b*ArcSin(1+d*x^2))+(Sqrt(Pi)*x*FresnelS((Sqrt(b^(-1))*Sqrt(a+b*ArcSin(1+d*x^2)))/Sqrt(Pi))*(Cos(a/(2*b))-Sin(a/(2*b))))/(Sqrt(b^(-1))*(Cos(ArcSin(1+d*x^2)/2)-Sin(ArcSin(1+d*x^2)/2)))-(Sqrt(Pi)*x*FresnelC((Sqrt(b^(-1))*Sqrt(a+b*ArcSin(1+d*x^2)))/Sqrt(Pi))*(Cos(a/(2*b))+Sin(a/(2*b))))/(Sqrt(b^(-1))*(Cos(ArcSin(1+d*x^2)/2)-Sin(ArcSin(1+d*x^2)/2)))");

	}

	// {1/Sqrt(a+b*ArcSin(1+d*x^2)), x, 1, -((Sqrt(Pi)*x*FresnelC(Sqrt(a+b*ArcSin(1 +
	// d*x^2))/(Sqrt(b)*Sqrt(Pi)))*(Cos(a/(2*b))-Sin(a/(2*b))))/(Sqrt(b)*(Cos(ArcSin(1+d*x^2)/2)-Sin(ArcSin(1 +
	// d*x^2)/2))))-(Sqrt(Pi)*x*FresnelS(Sqrt(a+b*ArcSin(1+d*x^2))/(Sqrt(b)*Sqrt(Pi)))*(Cos(a/(2*b)) +
	// Sin(a/(2*b))))/(Sqrt(b)*(Cos(ArcSin(1+d*x^2)/2)-Sin(ArcSin(1+d*x^2)/2)))}
	public void test02676() {
		check("Integrate(1/Sqrt(a+b*ArcSin(1+d*x^2)), x)",
				"-((Sqrt(Pi)*x*FresnelC(Sqrt(a+b*ArcSin(1+d*x^2))/(Sqrt(b)*Sqrt(Pi)))*(Cos(a/(2*b))-Sin(a/(2*b))))/(Sqrt(b)*(Cos(ArcSin(1+d*x^2)/2)-Sin(ArcSin(1+d*x^2)/2))))-(Sqrt(Pi)*x*FresnelS(Sqrt(a+b*ArcSin(1+d*x^2))/(Sqrt(b)*Sqrt(Pi)))*(Cos(a/(2*b))+Sin(a/(2*b))))/(Sqrt(b)*(Cos(ArcSin(1+d*x^2)/2)-Sin(ArcSin(1+d*x^2)/2)))");

	}

	// {(a+b*ArcSin(1+d*x^2))^(-3/2), x, 1, -(Sqrt(-2*d*x^2-d^2*x^4)/(b*d*x*Sqrt(a+b*ArcSin(1+d*x^2)))) +
	// ((b^(-1))^(3/2)*Sqrt(Pi)*x*FresnelS((Sqrt(b^(-1))*Sqrt(a+b*ArcSin(1+d*x^2)))/Sqrt(Pi))*(Cos(a/(2*b)) -
	// Sin(a/(2*b))))/(Cos(ArcSin(1+d*x^2)/2)-Sin(ArcSin(1+d*x^2)/2)) -
	// ((b^(-1))^(3/2)*Sqrt(Pi)*x*FresnelC((Sqrt(b^(-1))*Sqrt(a+b*ArcSin(1+d*x^2)))/Sqrt(Pi))*(Cos(a/(2*b)) +
	// Sin(a/(2*b))))/(Cos(ArcSin(1+d*x^2)/2)-Sin(ArcSin(1+d*x^2)/2))}
	public void test02677() {
		check("Integrate((a+b*ArcSin(1+d*x^2))^(-3/2), x)",
				"-(Sqrt(-2*d*x^2-d^2*x^4)/(b*d*x*Sqrt(a+b*ArcSin(1+d*x^2))))+((b^(-1))^(3/2)*Sqrt(Pi)*x*FresnelS((Sqrt(b^(-1))*Sqrt(a+b*ArcSin(1+d*x^2)))/Sqrt(Pi))*(Cos(a/(2*b))-Sin(a/(2*b))))/(Cos(ArcSin(1+d*x^2)/2)-Sin(ArcSin(1+d*x^2)/2))-((b^(-1))^(3/2)*Sqrt(Pi)*x*FresnelC((Sqrt(b^(-1))*Sqrt(a+b*ArcSin(1+d*x^2)))/Sqrt(Pi))*(Cos(a/(2*b))+Sin(a/(2*b))))/(Cos(ArcSin(1+d*x^2)/2)-Sin(ArcSin(1+d*x^2)/2))");

	}

	// {Sqrt(a-b*ArcSin(1-d*x^2)), x, 1, x*Sqrt(a-b*ArcSin(1-d*x^2)) -
	// (Sqrt(Pi)*x*FresnelC((Sqrt(-b^(-1))*Sqrt(a-b*ArcSin(1-d*x^2)))/Sqrt(Pi))*(Cos(a/(2*b)) -
	// Sin(a/(2*b))))/(Sqrt(-b^(-1))*(Cos(ArcSin(1-d*x^2)/2)-Sin(ArcSin(1-d*x^2)/2))) +
	// (Sqrt(Pi)*x*FresnelS((Sqrt(-b^(-1))*Sqrt(a-b*ArcSin(1-d*x^2)))/Sqrt(Pi))*(Cos(a/(2*b)) +
	// Sin(a/(2*b))))/(Sqrt(-b^(-1))*(Cos(ArcSin(1-d*x^2)/2)-Sin(ArcSin(1-d*x^2)/2)))}
	public void test02678() {
		check("Integrate(Sqrt(a-b*ArcSin(1-d*x^2)), x)",
				"x*Sqrt(a-b*ArcSin(1-d*x^2))-(Sqrt(Pi)*x*FresnelC((Sqrt(-b^(-1))*Sqrt(a-b*ArcSin(1-d*x^2)))/Sqrt(Pi))*(Cos(a/(2*b))-Sin(a/(2*b))))/(Sqrt(-b^(-1))*(Cos(ArcSin(1-d*x^2)/2)-Sin(ArcSin(1-d*x^2)/2)))+(Sqrt(Pi)*x*FresnelS((Sqrt(-b^(-1))*Sqrt(a-b*ArcSin(1-d*x^2)))/Sqrt(Pi))*(Cos(a/(2*b))+Sin(a/(2*b))))/(Sqrt(-b^(-1))*(Cos(ArcSin(1-d*x^2)/2)-Sin(ArcSin(1-d*x^2)/2)))");

	}

	// {1/Sqrt(a-b*ArcSin(1-d*x^2)), x, 1, -((Sqrt(Pi)*x*FresnelS(Sqrt(a-b*ArcSin(1 -
	// d*x^2))/(Sqrt(-b)*Sqrt(Pi)))*(Cos(a/(2*b))-Sin(a/(2*b))))/(Sqrt(-b)*(Cos(ArcSin(1-d*x^2)/2)-Sin(ArcSin(1 -
	// d*x^2)/2))))-(Sqrt(Pi)*x*FresnelC(Sqrt(a-b*ArcSin(1-d*x^2))/(Sqrt(-b)*Sqrt(Pi)))*(Cos(a/(2*b)) +
	// Sin(a/(2*b))))/(Sqrt(-b)*(Cos(ArcSin(1-d*x^2)/2)-Sin(ArcSin(1-d*x^2)/2)))}
	public void test02679() {
		check("Integrate(1/Sqrt(a-b*ArcSin(1-d*x^2)), x)",
				"-((Sqrt(Pi)*x*FresnelS(Sqrt(a-b*ArcSin(1-d*x^2))/(Sqrt(-b)*Sqrt(Pi)))*(Cos(a/(2*b))-Sin(a/(2*b))))/(Sqrt(-b)*(Cos(ArcSin(1-d*x^2)/2)-Sin(ArcSin(1-d*x^2)/2))))-(Sqrt(Pi)*x*FresnelC(Sqrt(a-b*ArcSin(1-d*x^2))/(Sqrt(-b)*Sqrt(Pi)))*(Cos(a/(2*b))+Sin(a/(2*b))))/(Sqrt(-b)*(Cos(ArcSin(1-d*x^2)/2)-Sin(ArcSin(1-d*x^2)/2)))");

	}

	// {(a-b*ArcSin(1-d*x^2))^(-3/2), x, 1, -(Sqrt(2*d*x^2-d^2*x^4)/(b*d*x*Sqrt(a-b*ArcSin(1-d*x^2)))) -
	// ((-b^(-1))^(3/2)*Sqrt(Pi)*x*FresnelC((Sqrt(-b^(-1))*Sqrt(a-b*ArcSin(1-d*x^2)))/Sqrt(Pi))*(Cos(a/(2*b)) -
	// Sin(a/(2*b))))/(Cos(ArcSin(1-d*x^2)/2)-Sin(ArcSin(1-d*x^2)/2)) +
	// ((-b^(-1))^(3/2)*Sqrt(Pi)*x*FresnelS((Sqrt(-b^(-1))*Sqrt(a-b*ArcSin(1-d*x^2)))/Sqrt(Pi))*(Cos(a/(2*b)) +
	// Sin(a/(2*b))))/(Cos(ArcSin(1-d*x^2)/2)-Sin(ArcSin(1-d*x^2)/2))}
	public void test02680() {
		check("Integrate((a-b*ArcSin(1-d*x^2))^(-3/2), x)",
				"-(Sqrt(2*d*x^2-d^2*x^4)/(b*d*x*Sqrt(a-b*ArcSin(1-d*x^2))))-((-b^(-1))^(3/2)*Sqrt(Pi)*x*FresnelC((Sqrt(-b^(-1))*Sqrt(a-b*ArcSin(1-d*x^2)))/Sqrt(Pi))*(Cos(a/(2*b))-Sin(a/(2*b))))/(Cos(ArcSin(1-d*x^2)/2)-Sin(ArcSin(1-d*x^2)/2))+((-b^(-1))^(3/2)*Sqrt(Pi)*x*FresnelS((Sqrt(-b^(-1))*Sqrt(a-b*ArcSin(1-d*x^2)))/Sqrt(Pi))*(Cos(a/(2*b))+Sin(a/(2*b))))/(Cos(ArcSin(1-d*x^2)/2)-Sin(ArcSin(1-d*x^2)/2))");

	}

	// {(b*x)^m*ArcCos(a*x)^4, x, 1, ((b*x)^(1+m)*ArcCos(a*x)^4)/(b*(1+m))+(4*a*Unintegrable(((b*x)^(1 +
	// m)*ArcCos(a*x)^3)/Sqrt(1-a^2*x^2), x))/(b*(1+m))}
	public void test02681() {
		check("Integrate((b*x)^m*ArcCos(a*x)^4, x)",
				"((b*x)^(1+m)*ArcCos(a*x)^4)/(b*(1+m))+(4*a*Unintegrable(((b*x)^(1+m)*ArcCos(a*x)^3)/Sqrt(1-a^2*x^2), x))/(b*(1+m))");

	}

	// {(b*x)^m*ArcCos(a*x)^3, x, 1, ((b*x)^(1+m)*ArcCos(a*x)^3)/(b*(1+m))+(3*a*Unintegrable(((b*x)^(1 +
	// m)*ArcCos(a*x)^2)/Sqrt(1-a^2*x^2), x))/(b*(1+m))}
	public void test02682() {
		check("Integrate((b*x)^m*ArcCos(a*x)^3, x)",
				"((b*x)^(1+m)*ArcCos(a*x)^3)/(b*(1+m))+(3*a*Unintegrable(((b*x)^(1+m)*ArcCos(a*x)^2)/Sqrt(1-a^2*x^2), x))/(b*(1+m))");

	}

	// {(d*x)^(3/2)*(a+b*ArcCos(c*x))^3, x, 1, (2*(d*x)^(5/2)*(a+b*ArcCos(c*x))^3)/(5*d) +
	// (6*b*c*Unintegrable(((d*x)^(5/2)*(a+b*ArcCos(c*x))^2)/Sqrt(1-c^2*x^2), x))/(5*d)}
	public void test02683() {
		check("Integrate((d*x)^(3/2)*(a+b*ArcCos(c*x))^3, x)",
				"(2*(d*x)^(5/2)*(a+b*ArcCos(c*x))^3)/(5*d)+(6*b*c*Unintegrable(((d*x)^(5/2)*(a+b*ArcCos(c*x))^2)/Sqrt(1-c^2*x^2), x))/(5*d)");

	}

	// {Sqrt(d*x)*(a+b*ArcCos(c*x))^3, x, 1, (2*(d*x)^(3/2)*(a+b*ArcCos(c*x))^3)/(3*d) +
	// (2*b*c*Unintegrable(((d*x)^(3/2)*(a+b*ArcCos(c*x))^2)/Sqrt(1-c^2*x^2), x))/d}
	public void test02684() {
		check("Integrate(Sqrt(d*x)*(a+b*ArcCos(c*x))^3, x)",
				"(2*(d*x)^(3/2)*(a+b*ArcCos(c*x))^3)/(3*d)+(2*b*c*Unintegrable(((d*x)^(3/2)*(a+b*ArcCos(c*x))^2)/Sqrt(1-c^2*x^2), x))/d");

	}

	// {(a+b*ArcCos(c*x))^3/Sqrt(d*x), x, 1, (2*Sqrt(d*x)*(a+b*ArcCos(c*x))^3)/d+(6*b*c*Unintegrable((Sqrt(d*x)*(a
	// +b*ArcCos(c*x))^2)/Sqrt(1-c^2*x^2), x))/d}
	public void test02685() {
		check("Integrate((a+b*ArcCos(c*x))^3/Sqrt(d*x), x)",
				"(2*Sqrt(d*x)*(a+b*ArcCos(c*x))^3)/d+(6*b*c*Unintegrable((Sqrt(d*x)*(a+b*ArcCos(c*x))^2)/Sqrt(1-c^2*x^2), x))/d");

	}

	// {(a+b*ArcCos(c*x))^3/(d*x)^(3/2), x, 1, (-2*(a+b*ArcCos(c*x))^3)/(d*Sqrt(d*x))-(6*b*c*Unintegrable((a +
	// b*ArcCos(c*x))^2/(Sqrt(d*x)*Sqrt(1-c^2*x^2)), x))/d}
	public void test02686() {
		check("Integrate((a+b*ArcCos(c*x))^3/(d*x)^(3/2), x)",
				"(-2*(a+b*ArcCos(c*x))^3)/(d*Sqrt(d*x))-(6*b*c*Unintegrable((a+b*ArcCos(c*x))^2/(Sqrt(d*x)*Sqrt(1-c^2*x^2)), x))/d");

	}

	// {(a+b*ArcCos(c*x))^3/(d*x)^(5/2), x, 1, (-2*(a+b*ArcCos(c*x))^3)/(3*d*(d*x)^(3/2))-(2*b*c*Unintegrable((a +
	// b*ArcCos(c*x))^2/((d*x)^(3/2)*Sqrt(1-c^2*x^2)), x))/d}
	public void test02687() {
		check("Integrate((a+b*ArcCos(c*x))^3/(d*x)^(5/2), x)",
				"(-2*(a+b*ArcCos(c*x))^3)/(3*d*(d*x)^(3/2))-(2*b*c*Unintegrable((a+b*ArcCos(c*x))^2/((d*x)^(3/2)*Sqrt(1-c^2*x^2)), x))/d");

	}

	// {(a+b*ArcCos(1+d*x^2))^(-1), x, 1, (x*Cos(a/(2*b))*CosIntegral((a+b*ArcCos(1 +
	// d*x^2))/(2*b)))/(Sqrt(2)*b*Sqrt(-(d*x^2)))+(x*Sin(a/(2*b))*SinIntegral((a+b*ArcCos(1 +
	// d*x^2))/(2*b)))/(Sqrt(2)*b*Sqrt(-(d*x^2)))}
	public void test02688() {
		check("Integrate((a+b*ArcCos(1+d*x^2))^(-1), x)",
				"(x*Cos(a/(2*b))*CosIntegral((a+b*ArcCos(1+d*x^2))/(2*b)))/(Sqrt(2)*b*Sqrt(-(d*x^2)))+(x*Sin(a/(2*b))*SinIntegral((a+b*ArcCos(1+d*x^2))/(2*b)))/(Sqrt(2)*b*Sqrt(-(d*x^2)))");

	}

	// {(a+b*ArcCos(1+d*x^2))^(-2), x, 1, Sqrt(-2*d*x^2-d^2*x^4)/(2*b*d*x*(a+b*ArcCos(1+d*x^2))) +
	// (x*CosIntegral((a+b*ArcCos(1+d*x^2))/(2*b))*Sin(a/(2*b)))/(2*Sqrt(2)*b^2*Sqrt(-(d*x^2))) -
	// (x*Cos(a/(2*b))*SinIntegral((a+b*ArcCos(1+d*x^2))/(2*b)))/(2*Sqrt(2)*b^2*Sqrt(-(d*x^2)))}
	public void test02689() {
		check("Integrate((a+b*ArcCos(1+d*x^2))^(-2), x)",
				"Sqrt(-2*d*x^2-d^2*x^4)/(2*b*d*x*(a+b*ArcCos(1+d*x^2)))+(x*CosIntegral((a+b*ArcCos(1+d*x^2))/(2*b))*Sin(a/(2*b)))/(2*Sqrt(2)*b^2*Sqrt(-(d*x^2)))-(x*Cos(a/(2*b))*SinIntegral((a+b*ArcCos(1+d*x^2))/(2*b)))/(2*Sqrt(2)*b^2*Sqrt(-(d*x^2)))");

	}

	// {(a+b*ArcCos(-1+d*x^2))^(-1), x, 1, (x*CosIntegral((a+b*ArcCos(-1 +
	// d*x^2))/(2*b))*Sin(a/(2*b)))/(Sqrt(2)*b*Sqrt(d*x^2))-(x*Cos(a/(2*b))*SinIntegral((a+b*ArcCos(-1 +
	// d*x^2))/(2*b)))/(Sqrt(2)*b*Sqrt(d*x^2))}
	public void test02690() {
		check("Integrate((a+b*ArcCos(-1+d*x^2))^(-1), x)",
				"(x*CosIntegral((a+b*ArcCos(-1+d*x^2))/(2*b))*Sin(a/(2*b)))/(Sqrt(2)*b*Sqrt(d*x^2))-(x*Cos(a/(2*b))*SinIntegral((a+b*ArcCos(-1+d*x^2))/(2*b)))/(Sqrt(2)*b*Sqrt(d*x^2))");

	}

	// {(a+b*ArcCos(-1+d*x^2))^(-2), x, 1, Sqrt(2*d*x^2-d^2*x^4)/(2*b*d*x*(a+b*ArcCos(-1+d*x^2))) -
	// (x*Cos(a/(2*b))*CosIntegral((a+b*ArcCos(-1+d*x^2))/(2*b)))/(2*Sqrt(2)*b^2*Sqrt(d*x^2)) -
	// (x*Sin(a/(2*b))*SinIntegral((a+b*ArcCos(-1+d*x^2))/(2*b)))/(2*Sqrt(2)*b^2*Sqrt(d*x^2))}
	public void test02691() {
		check("Integrate((a+b*ArcCos(-1+d*x^2))^(-2), x)",
				"Sqrt(2*d*x^2-d^2*x^4)/(2*b*d*x*(a+b*ArcCos(-1+d*x^2)))-(x*Cos(a/(2*b))*CosIntegral((a+b*ArcCos(-1+d*x^2))/(2*b)))/(2*Sqrt(2)*b^2*Sqrt(d*x^2))-(x*Sin(a/(2*b))*SinIntegral((a+b*ArcCos(-1+d*x^2))/(2*b)))/(2*Sqrt(2)*b^2*Sqrt(d*x^2))");

	}

	// {Sqrt(a+b*ArcCos(1+d*x^2)), x, 1, (2*Sqrt(Pi)*Cos(a/(2*b))*FresnelS((Sqrt(b^(-1))*Sqrt(a+b*ArcCos(1 +
	// d*x^2)))/Sqrt(Pi))*Sin(ArcCos(1+d*x^2)/2))/(Sqrt(b^(-1))*d*x)-(2*Sqrt(Pi)*FresnelC((Sqrt(b^(-1))*Sqrt(a +
	// b*ArcCos(1+d*x^2)))/Sqrt(Pi))*Sin(a/(2*b))*Sin(ArcCos(1+d*x^2)/2))/(Sqrt(b^(-1))*d*x)-(2*Sqrt(a +
	// b*ArcCos(1+d*x^2))*Sin(ArcCos(1+d*x^2)/2)^2)/(d*x)}
	public void test02692() {
		check("Integrate(Sqrt(a+b*ArcCos(1+d*x^2)), x)",
				"(2*Sqrt(Pi)*Cos(a/(2*b))*FresnelS((Sqrt(b^(-1))*Sqrt(a+b*ArcCos(1+d*x^2)))/Sqrt(Pi))*Sin(ArcCos(1+d*x^2)/2))/(Sqrt(b^(-1))*d*x)-(2*Sqrt(Pi)*FresnelC((Sqrt(b^(-1))*Sqrt(a+b*ArcCos(1+d*x^2)))/Sqrt(Pi))*Sin(a/(2*b))*Sin(ArcCos(1+d*x^2)/2))/(Sqrt(b^(-1))*d*x)-(2*Sqrt(a+b*ArcCos(1+d*x^2))*Sin(ArcCos(1+d*x^2)/2)^2)/(d*x)");

	}

	// {1/Sqrt(a+b*ArcCos(1+d*x^2)), x, 1, (-2*Sqrt(b^(-1))*Sqrt(Pi)*Cos(a/(2*b))*FresnelC((Sqrt(b^(-1))*Sqrt(a +
	// b*ArcCos(1+d*x^2)))/Sqrt(Pi))*Sin(ArcCos(1+d*x^2)/2))/(d*x) -
	// (2*Sqrt(b^(-1))*Sqrt(Pi)*FresnelS((Sqrt(b^(-1))*Sqrt(a+b*ArcCos(1 +
	// d*x^2)))/Sqrt(Pi))*Sin(a/(2*b))*Sin(ArcCos(1+d*x^2)/2))/(d*x)}
	public void test02693() {
		check("Integrate(1/Sqrt(a+b*ArcCos(1+d*x^2)), x)",
				"(-2*Sqrt(b^(-1))*Sqrt(Pi)*Cos(a/(2*b))*FresnelC((Sqrt(b^(-1))*Sqrt(a+b*ArcCos(1+d*x^2)))/Sqrt(Pi))*Sin(ArcCos(1+d*x^2)/2))/(d*x)-(2*Sqrt(b^(-1))*Sqrt(Pi)*FresnelS((Sqrt(b^(-1))*Sqrt(a+b*ArcCos(1+d*x^2)))/Sqrt(Pi))*Sin(a/(2*b))*Sin(ArcCos(1+d*x^2)/2))/(d*x)");

	}

	// {(a+b*ArcCos(1+d*x^2))^(-3/2), x, 1, Sqrt(-2*d*x^2-d^2*x^4)/(b*d*x*Sqrt(a+b*ArcCos(1+d*x^2))) +
	// (2*(b^(-1))^(3/2)*Sqrt(Pi)*Cos(a/(2*b))*FresnelS((Sqrt(b^(-1))*Sqrt(a+b*ArcCos(1 +
	// d*x^2)))/Sqrt(Pi))*Sin(ArcCos(1+d*x^2)/2))/(d*x)-(2*(b^(-1))^(3/2)*Sqrt(Pi)*FresnelC((Sqrt(b^(-1))*Sqrt(a +
	// b*ArcCos(1+d*x^2)))/Sqrt(Pi))*Sin(a/(2*b))*Sin(ArcCos(1+d*x^2)/2))/(d*x)}
	public void test02694() {
		check("Integrate((a+b*ArcCos(1+d*x^2))^(-3/2), x)",
				"Sqrt(-2*d*x^2-d^2*x^4)/(b*d*x*Sqrt(a+b*ArcCos(1+d*x^2)))+(2*(b^(-1))^(3/2)*Sqrt(Pi)*Cos(a/(2*b))*FresnelS((Sqrt(b^(-1))*Sqrt(a+b*ArcCos(1+d*x^2)))/Sqrt(Pi))*Sin(ArcCos(1+d*x^2)/2))/(d*x)-(2*(b^(-1))^(3/2)*Sqrt(Pi)*FresnelC((Sqrt(b^(-1))*Sqrt(a+b*ArcCos(1+d*x^2)))/Sqrt(Pi))*Sin(a/(2*b))*Sin(ArcCos(1+d*x^2)/2))/(d*x)");

	}

	// {Sqrt(a+b*ArcCos(-1+d*x^2)), x, 1, (2*Sqrt(a+b*ArcCos(-1+d*x^2))*Cos(ArcCos(-1+d*x^2)/2)^2)/(d*x) -
	// (2*Sqrt(Pi)*Cos(a/(2*b))*Cos(ArcCos(-1+d*x^2)/2)*FresnelC((Sqrt(b^(-1))*Sqrt(a+b*ArcCos(-1 +
	// d*x^2)))/Sqrt(Pi)))/(Sqrt(b^(-1))*d*x)-(2*Sqrt(Pi)*Cos(ArcCos(-1+d*x^2)/2)*FresnelS((Sqrt(b^(-1))*Sqrt(a +
	// b*ArcCos(-1+d*x^2)))/Sqrt(Pi))*Sin(a/(2*b)))/(Sqrt(b^(-1))*d*x)}
	public void test02695() {
		check("Integrate(Sqrt(a+b*ArcCos(-1+d*x^2)), x)",
				"(2*Sqrt(a+b*ArcCos(-1+d*x^2))*Cos(ArcCos(-1+d*x^2)/2)^2)/(d*x)-(2*Sqrt(Pi)*Cos(a/(2*b))*Cos(ArcCos(-1+d*x^2)/2)*FresnelC((Sqrt(b^(-1))*Sqrt(a+b*ArcCos(-1+d*x^2)))/Sqrt(Pi)))/(Sqrt(b^(-1))*d*x)-(2*Sqrt(Pi)*Cos(ArcCos(-1+d*x^2)/2)*FresnelS((Sqrt(b^(-1))*Sqrt(a+b*ArcCos(-1+d*x^2)))/Sqrt(Pi))*Sin(a/(2*b)))/(Sqrt(b^(-1))*d*x)");

	}

	// {1/Sqrt(a+b*ArcCos(-1+d*x^2)), x, 1, (-2*Sqrt(b^(-1))*Sqrt(Pi)*Cos(a/(2*b))*Cos(ArcCos(-1 +
	// d*x^2)/2)*FresnelS((Sqrt(b^(-1))*Sqrt(a+b*ArcCos(-1+d*x^2)))/Sqrt(Pi)))/(d*x) +
	// (2*Sqrt(b^(-1))*Sqrt(Pi)*Cos(ArcCos(-1+d*x^2)/2)*FresnelC((Sqrt(b^(-1))*Sqrt(a+b*ArcCos(-1 +
	// d*x^2)))/Sqrt(Pi))*Sin(a/(2*b)))/(d*x)}
	public void test02696() {
		check("Integrate(1/Sqrt(a+b*ArcCos(-1+d*x^2)), x)",
				"(-2*Sqrt(b^(-1))*Sqrt(Pi)*Cos(a/(2*b))*Cos(ArcCos(-1+d*x^2)/2)*FresnelS((Sqrt(b^(-1))*Sqrt(a+b*ArcCos(-1+d*x^2)))/Sqrt(Pi)))/(d*x)+(2*Sqrt(b^(-1))*Sqrt(Pi)*Cos(ArcCos(-1+d*x^2)/2)*FresnelC((Sqrt(b^(-1))*Sqrt(a+b*ArcCos(-1+d*x^2)))/Sqrt(Pi))*Sin(a/(2*b)))/(d*x)");

	}

	// {(a+b*ArcCos(-1+d*x^2))^(-3/2), x, 1, Sqrt(2*d*x^2-d^2*x^4)/(b*d*x*Sqrt(a+b*ArcCos(-1+d*x^2))) -
	// (2*(b^(-1))^(3/2)*Sqrt(Pi)*Cos(a/(2*b))*Cos(ArcCos(-1+d*x^2)/2)*FresnelC((Sqrt(b^(-1))*Sqrt(a+b*ArcCos(-1 +
	// d*x^2)))/Sqrt(Pi)))/(d*x)-(2*(b^(-1))^(3/2)*Sqrt(Pi)*Cos(ArcCos(-1+d*x^2)/2)*FresnelS((Sqrt(b^(-1))*Sqrt(a +
	// b*ArcCos(-1+d*x^2)))/Sqrt(Pi))*Sin(a/(2*b)))/(d*x)}
	public void test02697() {
		check("Integrate((a+b*ArcCos(-1+d*x^2))^(-3/2), x)",
				"Sqrt(2*d*x^2-d^2*x^4)/(b*d*x*Sqrt(a+b*ArcCos(-1+d*x^2)))-(2*(b^(-1))^(3/2)*Sqrt(Pi)*Cos(a/(2*b))*Cos(ArcCos(-1+d*x^2)/2)*FresnelC((Sqrt(b^(-1))*Sqrt(a+b*ArcCos(-1+d*x^2)))/Sqrt(Pi)))/(d*x)-(2*(b^(-1))^(3/2)*Sqrt(Pi)*Cos(ArcCos(-1+d*x^2)/2)*FresnelS((Sqrt(b^(-1))*Sqrt(a+b*ArcCos(-1+d*x^2)))/Sqrt(Pi))*Sin(a/(2*b)))/(d*x)");

	}

	// {(a+b*ArcTan(c*x))^2/(d+e*x), x, 1, -(((a+b*ArcTan(c*x))^2*Log(2/(1-I*c*x)))/e)+((a +
	// b*ArcTan(c*x))^2*Log((2*c*(d+e*x))/((c*d+I*e)*(1-I*c*x))))/e+(I*b*(a+b*ArcTan(c*x))*PolyLog(2, 1-2/(1
	// -I*c*x)))/e-(I*b*(a+b*ArcTan(c*x))*PolyLog(2, 1-(2*c*(d+e*x))/((c*d+I*e)*(1-I*c*x))))/e -
	// (b^2*PolyLog(3, 1-2/(1-I*c*x)))/(2*e)+(b^2*PolyLog(3, 1-(2*c*(d+e*x))/((c*d+I*e)*(1 -
	// I*c*x))))/(2*e)}
	public void test02698() {
		check("Integrate((a+b*ArcTan(c*x))^2/(d+e*x), x)",
				"-(((a+b*ArcTan(c*x))^2*Log(2/(1-I*c*x)))/e)+((a+b*ArcTan(c*x))^2*Log((2*c*(d+e*x))/((c*d+I*e)*(1-I*c*x))))/e+(I*b*(a+b*ArcTan(c*x))*PolyLog(2, 1-2/(1-I*c*x)))/e-(I*b*(a+b*ArcTan(c*x))*PolyLog(2, 1-(2*c*(d+e*x))/((c*d+I*e)*(1-I*c*x))))/e-(b^2*PolyLog(3, 1-2/(1-I*c*x)))/(2*e)+(b^2*PolyLog(3, 1-(2*c*(d+e*x))/((c*d+I*e)*(1-I*c*x))))/(2*e)");

	}

	// {(a+b*ArcTan(c*x))^3/(d+e*x), x, 1, -(((a+b*ArcTan(c*x))^3*Log(2/(1-I*c*x)))/e)+((a +
	// b*ArcTan(c*x))^3*Log((2*c*(d+e*x))/((c*d+I*e)*(1-I*c*x))))/e+(((3*I)/2)*b*(a +
	// b*ArcTan(c*x))^2*PolyLog(2, 1-2/(1-I*c*x)))/e-(((3*I)/2)*b*(a+b*ArcTan(c*x))^2*PolyLog(2, 1-(2*c*(d +
	// e*x))/((c*d+I*e)*(1-I*c*x))))/e-(3*b^2*(a+b*ArcTan(c*x))*PolyLog(3, 1-2/(1-I*c*x)))/(2*e)+(3*b^2*(a
	// +b*ArcTan(c*x))*PolyLog(3, 1-(2*c*(d+e*x))/((c*d+I*e)*(1-I*c*x))))/(2*e)-(((3*I)/4)*b^3*PolyLog(4, 1 -
	// 2/(1-I*c*x)))/e+(((3*I)/4)*b^3*PolyLog(4, 1-(2*c*(d+e*x))/((c*d+I*e)*(1-I*c*x))))/e}
	public void test02699() {
		check("Integrate((a+b*ArcTan(c*x))^3/(d+e*x), x)",
				"-(((a+b*ArcTan(c*x))^3*Log(2/(1-I*c*x)))/e)+((a+b*ArcTan(c*x))^3*Log((2*c*(d+e*x))/((c*d+I*e)*(1-I*c*x))))/e+(((3*I)/2)*b*(a+b*ArcTan(c*x))^2*PolyLog(2, 1-2/(1-I*c*x)))/e-(((3*I)/2)*b*(a+b*ArcTan(c*x))^2*PolyLog(2, 1-(2*c*(d+e*x))/((c*d+I*e)*(1-I*c*x))))/e-(3*b^2*(a+b*ArcTan(c*x))*PolyLog(3, 1-2/(1-I*c*x)))/(2*e)+(3*b^2*(a+b*ArcTan(c*x))*PolyLog(3, 1-(2*c*(d+e*x))/((c*d+I*e)*(1-I*c*x))))/(2*e)-(((3*I)/4)*b^3*PolyLog(4, 1-2/(1-I*c*x)))/e+(((3*I)/4)*b^3*PolyLog(4, 1-(2*c*(d+e*x))/((c*d+I*e)*(1-I*c*x))))/e");

	}

	// {(a+b*ArcTan(c*x))^2/(d+e*x), x, 1, -(((a+b*ArcTan(c*x))^2*Log(2/(1-I*c*x)))/e)+((a +
	// b*ArcTan(c*x))^2*Log((2*c*(d+e*x))/((c*d+I*e)*(1-I*c*x))))/e+(I*b*(a+b*ArcTan(c*x))*PolyLog(2, 1-2/(1
	// -I*c*x)))/e-(I*b*(a+b*ArcTan(c*x))*PolyLog(2, 1-(2*c*(d+e*x))/((c*d+I*e)*(1-I*c*x))))/e -
	// (b^2*PolyLog(3, 1-2/(1-I*c*x)))/(2*e)+(b^2*PolyLog(3, 1-(2*c*(d+e*x))/((c*d+I*e)*(1 -
	// I*c*x))))/(2*e)}
	public void test02700() {
		check("Integrate((a+b*ArcTan(c*x))^2/(d+e*x), x)",
				"-(((a+b*ArcTan(c*x))^2*Log(2/(1-I*c*x)))/e)+((a+b*ArcTan(c*x))^2*Log((2*c*(d+e*x))/((c*d+I*e)*(1-I*c*x))))/e+(I*b*(a+b*ArcTan(c*x))*PolyLog(2, 1-2/(1-I*c*x)))/e-(I*b*(a+b*ArcTan(c*x))*PolyLog(2, 1-(2*c*(d+e*x))/((c*d+I*e)*(1-I*c*x))))/e-(b^2*PolyLog(3, 1-2/(1-I*c*x)))/(2*e)+(b^2*PolyLog(3, 1-(2*c*(d+e*x))/((c*d+I*e)*(1-I*c*x))))/(2*e)");

	}

	// {ArcTan(a*x)/(c+a^2*c*x^2), x, 1, ArcTan(a*x)^2/(2*a*c)}
	public void test02701() {
		check("Integrate(ArcTan(a*x)/(c+a^2*c*x^2), x)", "ArcTan(a*x)^2/(2*a*c)");

	}

	// {ArcTan(a*x)/(c+a^2*c*x^2)^(3/2), x, 1, 1/(a*c*Sqrt(c+a^2*c*x^2))+(x*ArcTan(a*x))/(c*Sqrt(c+a^2*c*x^2))}
	public void test02702() {
		check("Integrate(ArcTan(a*x)/(c+a^2*c*x^2)^(3/2), x)",
				"1/(a*c*Sqrt(c+a^2*c*x^2))+(x*ArcTan(a*x))/(c*Sqrt(c+a^2*c*x^2))");

	}

	// {ArcTan(a*x)^2/(c+a^2*c*x^2), x, 1, ArcTan(a*x)^3/(3*a*c)}
	public void test02703() {
		check("Integrate(ArcTan(a*x)^2/(c+a^2*c*x^2), x)", "ArcTan(a*x)^3/(3*a*c)");

	}

	// {ArcTan(a*x)^3/(c+a^2*c*x^2), x, 1, ArcTan(a*x)^4/(4*a*c)}
	public void test02704() {
		check("Integrate(ArcTan(a*x)^3/(c+a^2*c*x^2), x)", "ArcTan(a*x)^4/(4*a*c)");

	}

	// {1/((c+a^2*c*x^2)*ArcTan(a*x)), x, 1, Log(ArcTan(a*x))/(a*c)}
	public void test02705() {
		check("Integrate(1/((c+a^2*c*x^2)*ArcTan(a*x)), x)", "Log(ArcTan(a*x))/(a*c)");

	}

	// {x^3/((c+a^2*c*x^2)*ArcTan(a*x)^2), x, 1, -(x^3/(a*c*ArcTan(a*x)))+(3*Unintegrable(x^2/ArcTan(a*x),
	// x))/(a*c)}
	public void test02706() {
		check("Integrate(x^3/((c+a^2*c*x^2)*ArcTan(a*x)^2), x)",
				"-(x^3/(a*c*ArcTan(a*x)))+(3*Unintegrable(x^2/ArcTan(a*x), x))/(a*c)");

	}

	// {x^2/((c+a^2*c*x^2)*ArcTan(a*x)^2), x, 1, -(x^2/(a*c*ArcTan(a*x)))+(2*Unintegrable(x/ArcTan(a*x), x))/(a*c)}
	public void test02707() {
		check("Integrate(x^2/((c+a^2*c*x^2)*ArcTan(a*x)^2), x)",
				"-(x^2/(a*c*ArcTan(a*x)))+(2*Unintegrable(x/ArcTan(a*x), x))/(a*c)");

	}

	// {x/((c+a^2*c*x^2)*ArcTan(a*x)^2), x, 1, -(x/(a*c*ArcTan(a*x)))+Unintegrable(ArcTan(a*x)^(-1), x)/(a*c)}
	public void test02708() {
		check("Integrate(x/((c+a^2*c*x^2)*ArcTan(a*x)^2), x)",
				"-(x/(a*c*ArcTan(a*x)))+Unintegrable(ArcTan(a*x)^(-1), x)/(a*c)");

	}

	// {1/((c+a^2*c*x^2)*ArcTan(a*x)^2), x, 1, -(1/(a*c*ArcTan(a*x)))}
	public void test02709() {
		check("Integrate(1/((c+a^2*c*x^2)*ArcTan(a*x)^2), x)", "-(1/(a*c*ArcTan(a*x)))");

	}

	// {1/(x*(c+a^2*c*x^2)*ArcTan(a*x)^2), x, 1, -(1/(a*c*x*ArcTan(a*x)))-Unintegrable(1/(x^2*ArcTan(a*x)),
	// x)/(a*c)}
	public void test02710() {
		check("Integrate(1/(x*(c+a^2*c*x^2)*ArcTan(a*x)^2), x)",
				"-(1/(a*c*x*ArcTan(a*x)))-Unintegrable(1/(x^2*ArcTan(a*x)), x)/(a*c)");

	}

	// {1/(x^2*(c+a^2*c*x^2)*ArcTan(a*x)^2), x, 1, -(1/(a*c*x^2*ArcTan(a*x)))-(2*Unintegrable(1/(x^3*ArcTan(a*x)),
	// x))/(a*c)}
	public void test02711() {
		check("Integrate(1/(x^2*(c+a^2*c*x^2)*ArcTan(a*x)^2), x)",
				"-(1/(a*c*x^2*ArcTan(a*x)))-(2*Unintegrable(1/(x^3*ArcTan(a*x)), x))/(a*c)");

	}

	// {1/(x^3*(c+a^2*c*x^2)*ArcTan(a*x)^2), x, 1, -(1/(a*c*x^3*ArcTan(a*x)))-(3*Unintegrable(1/(x^4*ArcTan(a*x)),
	// x))/(a*c)}
	public void test02712() {
		check("Integrate(1/(x^3*(c+a^2*c*x^2)*ArcTan(a*x)^2), x)",
				"-(1/(a*c*x^3*ArcTan(a*x)))-(3*Unintegrable(1/(x^4*ArcTan(a*x)), x))/(a*c)");

	}

	// {1/(x^4*(c+a^2*c*x^2)*ArcTan(a*x)^2), x, 1, -(1/(a*c*x^4*ArcTan(a*x)))-(4*Unintegrable(1/(x^5*ArcTan(a*x)),
	// x))/(a*c)}
	public void test02713() {
		check("Integrate(1/(x^4*(c+a^2*c*x^2)*ArcTan(a*x)^2), x)",
				"-(1/(a*c*x^4*ArcTan(a*x)))-(4*Unintegrable(1/(x^5*ArcTan(a*x)), x))/(a*c)");

	}

	// {1/(x*Sqrt(c+a^2*c*x^2)*ArcTan(a*x)^2), x, 1, -(Sqrt(c+a^2*c*x^2)/(a*c*x*ArcTan(a*x))) -
	// Unintegrable(1/(x^2*Sqrt(c+a^2*c*x^2)*ArcTan(a*x)), x)/a}
	public void test02714() {
		check("Integrate(1/(x*Sqrt(c+a^2*c*x^2)*ArcTan(a*x)^2), x)",
				"-(Sqrt(c+a^2*c*x^2)/(a*c*x*ArcTan(a*x)))-Unintegrable(1/(x^2*Sqrt(c+a^2*c*x^2)*ArcTan(a*x)), x)/a");

	}

	// {x^m/((c+a^2*c*x^2)*ArcTan(a*x)^2), x, 1, -(x^m/(a*c*ArcTan(a*x)))+(m*Unintegrable(x^(-1+m)/ArcTan(a*x),
	// x))/(a*c)}
	public void test02715() {
		check("Integrate(x^m/((c+a^2*c*x^2)*ArcTan(a*x)^2), x)",
				"-(x^m/(a*c*ArcTan(a*x)))+(m*Unintegrable(x^(-1+m)/ArcTan(a*x), x))/(a*c)");

	}

	// {x^3/((c+a^2*c*x^2)*ArcTan(a*x)^3), x, 1, -x^3/(2*a*c*ArcTan(a*x)^2)+(3*Unintegrable(x^2/ArcTan(a*x)^2,
	// x))/(2*a*c)}
	public void test02716() {
		check("Integrate(x^3/((c+a^2*c*x^2)*ArcTan(a*x)^3), x)",
				"-x^3/(2*a*c*ArcTan(a*x)^2)+(3*Unintegrable(x^2/ArcTan(a*x)^2, x))/(2*a*c)");

	}

	// {x^2/((c+a^2*c*x^2)*ArcTan(a*x)^3), x, 1, -x^2/(2*a*c*ArcTan(a*x)^2)+Unintegrable(x/ArcTan(a*x)^2, x)/(a*c)}
	public void test02717() {
		check("Integrate(x^2/((c+a^2*c*x^2)*ArcTan(a*x)^3), x)",
				"-x^2/(2*a*c*ArcTan(a*x)^2)+Unintegrable(x/ArcTan(a*x)^2, x)/(a*c)");

	}

	// {x/((c+a^2*c*x^2)*ArcTan(a*x)^3), x, 1, -x/(2*a*c*ArcTan(a*x)^2)+Unintegrable(ArcTan(a*x)^(-2), x)/(2*a*c)}
	public void test02718() {
		check("Integrate(x/((c+a^2*c*x^2)*ArcTan(a*x)^3), x)",
				"-x/(2*a*c*ArcTan(a*x)^2)+Unintegrable(ArcTan(a*x)^(-2), x)/(2*a*c)");

	}

	// {1/((c+a^2*c*x^2)*ArcTan(a*x)^3), x, 1, -1/(2*a*c*ArcTan(a*x)^2)}
	public void test02719() {
		check("Integrate(1/((c+a^2*c*x^2)*ArcTan(a*x)^3), x)", "-1/(2*a*c*ArcTan(a*x)^2)");

	}

	// {1/(x*(c+a^2*c*x^2)*ArcTan(a*x)^3), x, 1, -1/(2*a*c*x*ArcTan(a*x)^2)-Unintegrable(1/(x^2*ArcTan(a*x)^2),
	// x)/(2*a*c)}
	public void test02720() {
		check("Integrate(1/(x*(c+a^2*c*x^2)*ArcTan(a*x)^3), x)",
				"-1/(2*a*c*x*ArcTan(a*x)^2)-Unintegrable(1/(x^2*ArcTan(a*x)^2), x)/(2*a*c)");

	}

	// {1/(x^2*(c+a^2*c*x^2)*ArcTan(a*x)^3), x, 1, -1/(2*a*c*x^2*ArcTan(a*x)^2)-Unintegrable(1/(x^3*ArcTan(a*x)^2),
	// x)/(a*c)}
	public void test02721() {
		check("Integrate(1/(x^2*(c+a^2*c*x^2)*ArcTan(a*x)^3), x)",
				"-1/(2*a*c*x^2*ArcTan(a*x)^2)-Unintegrable(1/(x^3*ArcTan(a*x)^2), x)/(a*c)");

	}

	// {1/(x^3*(c+a^2*c*x^2)*ArcTan(a*x)^3), x, 1, -1/(2*a*c*x^3*ArcTan(a*x)^2) -
	// (3*Unintegrable(1/(x^4*ArcTan(a*x)^2), x))/(2*a*c)}
	public void test02722() {
		check("Integrate(1/(x^3*(c+a^2*c*x^2)*ArcTan(a*x)^3), x)",
				"-1/(2*a*c*x^3*ArcTan(a*x)^2)-(3*Unintegrable(1/(x^4*ArcTan(a*x)^2), x))/(2*a*c)");

	}

	// {1/(x^4*(c+a^2*c*x^2)*ArcTan(a*x)^3), x, 1, -1/(2*a*c*x^4*ArcTan(a*x)^2) -
	// (2*Unintegrable(1/(x^5*ArcTan(a*x)^2), x))/(a*c)}
	public void test02723() {
		check("Integrate(1/(x^4*(c+a^2*c*x^2)*ArcTan(a*x)^3), x)",
				"-1/(2*a*c*x^4*ArcTan(a*x)^2)-(2*Unintegrable(1/(x^5*ArcTan(a*x)^2), x))/(a*c)");

	}

	// {1/(x*Sqrt(c+a^2*c*x^2)*ArcTan(a*x)^3), x, 1, -Sqrt(c+a^2*c*x^2)/(2*a*c*x*ArcTan(a*x)^2) -
	// Unintegrable(1/(x^2*Sqrt(c+a^2*c*x^2)*ArcTan(a*x)^2), x)/(2*a)}
	public void test02724() {
		check("Integrate(1/(x*Sqrt(c+a^2*c*x^2)*ArcTan(a*x)^3), x)",
				"-Sqrt(c+a^2*c*x^2)/(2*a*c*x*ArcTan(a*x)^2)-Unintegrable(1/(x^2*Sqrt(c+a^2*c*x^2)*ArcTan(a*x)^2), x)/(2*a)");

	}

	// {x^m/((c+a^2*c*x^2)*ArcTan(a*x)^3), x, 1, -x^m/(2*a*c*ArcTan(a*x)^2)+(m*Unintegrable(x^(-1 +
	// m)/ArcTan(a*x)^2, x))/(2*a*c)}
	public void test02725() {
		check("Integrate(x^m/((c+a^2*c*x^2)*ArcTan(a*x)^3), x)",
				"-x^m/(2*a*c*ArcTan(a*x)^2)+(m*Unintegrable(x^(-1+m)/ArcTan(a*x)^2, x))/(2*a*c)");

	}

	// {x*(c+a^2*c*x^2)*Sqrt(ArcTan(a*x)), x, 1, (c*(1+a^2*x^2)^2*Sqrt(ArcTan(a*x)))/(4*a^2)-Unintegrable((c +
	// a^2*c*x^2)/Sqrt(ArcTan(a*x)), x)/(8*a)}
	public void test02726() {
		check("Integrate(x*(c+a^2*c*x^2)*Sqrt(ArcTan(a*x)), x)",
				"(c*(1+a^2*x^2)^2*Sqrt(ArcTan(a*x)))/(4*a^2)-Unintegrable((c+a^2*c*x^2)/Sqrt(ArcTan(a*x)), x)/(8*a)");

	}

	// {x*(c+a^2*c*x^2)^2*Sqrt(ArcTan(a*x)), x, 1, (c^2*(1+a^2*x^2)^3*Sqrt(ArcTan(a*x)))/(6*a^2)-Unintegrable((c +
	// a^2*c*x^2)^2/Sqrt(ArcTan(a*x)), x)/(12*a)}
	public void test02727() {
		check("Integrate(x*(c+a^2*c*x^2)^2*Sqrt(ArcTan(a*x)), x)",
				"(c^2*(1+a^2*x^2)^3*Sqrt(ArcTan(a*x)))/(6*a^2)-Unintegrable((c+a^2*c*x^2)^2/Sqrt(ArcTan(a*x)), x)/(12*a)");

	}

	// {x*(c+a^2*c*x^2)^3*Sqrt(ArcTan(a*x)), x, 1, (c^3*(1+a^2*x^2)^4*Sqrt(ArcTan(a*x)))/(8*a^2)-Unintegrable((c +
	// a^2*c*x^2)^3/Sqrt(ArcTan(a*x)), x)/(16*a)}
	public void test02728() {
		check("Integrate(x*(c+a^2*c*x^2)^3*Sqrt(ArcTan(a*x)), x)",
				"(c^3*(1+a^2*x^2)^4*Sqrt(ArcTan(a*x)))/(8*a^2)-Unintegrable((c+a^2*c*x^2)^3/Sqrt(ArcTan(a*x)), x)/(16*a)");

	}

	// {(x*Sqrt(ArcTan(a*x)))/(c+a^2*c*x^2), x, 1, (2*x*ArcTan(a*x)^(3/2))/(3*a*c) -
	// (2*Unintegrable(ArcTan(a*x)^(3/2), x))/(3*a*c)}
	public void test02729() {
		check("Integrate((x*Sqrt(ArcTan(a*x)))/(c+a^2*c*x^2), x)",
				"(2*x*ArcTan(a*x)^(3/2))/(3*a*c)-(2*Unintegrable(ArcTan(a*x)^(3/2), x))/(3*a*c)");

	}

	// {Sqrt(ArcTan(a*x))/(c+a^2*c*x^2), x, 1, (2*ArcTan(a*x)^(3/2))/(3*a*c)}
	public void test02730() {
		check("Integrate(Sqrt(ArcTan(a*x))/(c+a^2*c*x^2), x)", "(2*ArcTan(a*x)^(3/2))/(3*a*c)");

	}

	// {Sqrt(ArcTan(a*x))/(x*(c+a^2*c*x^2)), x, 1, (((-2*I)/3)*ArcTan(a*x)^(3/2))/c +
	// (I*Unintegrable(Sqrt(ArcTan(a*x))/(x*(I+a*x)), x))/c}
	public void test02731() {
		check("Integrate(Sqrt(ArcTan(a*x))/(x*(c+a^2*c*x^2)), x)",
				"(((-2*I)/3)*ArcTan(a*x)^(3/2))/c+(I*Unintegrable(Sqrt(ArcTan(a*x))/(x*(I+a*x)), x))/c");

	}

	// {x*Sqrt(c+a^2*c*x^2)*Sqrt(ArcTan(a*x)), x, 1, ((c+a^2*c*x^2)^(3/2)*Sqrt(ArcTan(a*x)))/(3*a^2*c) -
	// Unintegrable(Sqrt(c+a^2*c*x^2)/Sqrt(ArcTan(a*x)), x)/(6*a)}
	public void test02732() {
		check("Integrate(x*Sqrt(c+a^2*c*x^2)*Sqrt(ArcTan(a*x)), x)",
				"((c+a^2*c*x^2)^(3/2)*Sqrt(ArcTan(a*x)))/(3*a^2*c)-Unintegrable(Sqrt(c+a^2*c*x^2)/Sqrt(ArcTan(a*x)), x)/(6*a)");

	}

	// {x*(c+a^2*c*x^2)^(3/2)*Sqrt(ArcTan(a*x)), x, 1, ((c+a^2*c*x^2)^(5/2)*Sqrt(ArcTan(a*x)))/(5*a^2*c) -
	// Unintegrable((c+a^2*c*x^2)^(3/2)/Sqrt(ArcTan(a*x)), x)/(10*a)}
	public void test02733() {
		check("Integrate(x*(c+a^2*c*x^2)^(3/2)*Sqrt(ArcTan(a*x)), x)",
				"((c+a^2*c*x^2)^(5/2)*Sqrt(ArcTan(a*x)))/(5*a^2*c)-Unintegrable((c+a^2*c*x^2)^(3/2)/Sqrt(ArcTan(a*x)), x)/(10*a)");

	}

	// {x*(c+a^2*c*x^2)^(5/2)*Sqrt(ArcTan(a*x)), x, 1, ((c+a^2*c*x^2)^(7/2)*Sqrt(ArcTan(a*x)))/(7*a^2*c) -
	// Unintegrable((c+a^2*c*x^2)^(5/2)/Sqrt(ArcTan(a*x)), x)/(14*a)}
	public void test02734() {
		check("Integrate(x*(c+a^2*c*x^2)^(5/2)*Sqrt(ArcTan(a*x)), x)",
				"((c+a^2*c*x^2)^(7/2)*Sqrt(ArcTan(a*x)))/(7*a^2*c)-Unintegrable((c+a^2*c*x^2)^(5/2)/Sqrt(ArcTan(a*x)), x)/(14*a)");

	}

	// {(x^2*Sqrt(ArcTan(a*x)))/Sqrt(c+a^2*c*x^2), x, 1, (x*Sqrt(c+a^2*c*x^2)*Sqrt(ArcTan(a*x)))/(2*a^2*c) -
	// Unintegrable(x/(Sqrt(c+a^2*c*x^2)*Sqrt(ArcTan(a*x))), x)/(4*a)-Unintegrable(Sqrt(ArcTan(a*x))/Sqrt(c +
	// a^2*c*x^2), x)/(2*a^2)}
	public void test02735() {
		check("Integrate((x^2*Sqrt(ArcTan(a*x)))/Sqrt(c+a^2*c*x^2), x)",
				"(x*Sqrt(c+a^2*c*x^2)*Sqrt(ArcTan(a*x)))/(2*a^2*c)-Unintegrable(x/(Sqrt(c+a^2*c*x^2)*Sqrt(ArcTan(a*x))), x)/(4*a)-Unintegrable(Sqrt(ArcTan(a*x))/Sqrt(c+a^2*c*x^2), x)/(2*a^2)");

	}

	// {(x*Sqrt(ArcTan(a*x)))/Sqrt(c+a^2*c*x^2), x, 1, (Sqrt(c+a^2*c*x^2)*Sqrt(ArcTan(a*x)))/(a^2*c) -
	// Unintegrable(1/(Sqrt(c+a^2*c*x^2)*Sqrt(ArcTan(a*x))), x)/(2*a)}
	public void test02736() {
		check("Integrate((x*Sqrt(ArcTan(a*x)))/Sqrt(c+a^2*c*x^2), x)",
				"(Sqrt(c+a^2*c*x^2)*Sqrt(ArcTan(a*x)))/(a^2*c)-Unintegrable(1/(Sqrt(c+a^2*c*x^2)*Sqrt(ArcTan(a*x))), x)/(2*a)");

	}

	// {Sqrt(ArcTan(a*x))/(x^2*Sqrt(c+a^2*c*x^2)), x, 1, -((Sqrt(c+a^2*c*x^2)*Sqrt(ArcTan(a*x)))/(c*x)) +
	// (a*Unintegrable(1/(x*Sqrt(c+a^2*c*x^2)*Sqrt(ArcTan(a*x))), x))/2}
	public void test02737() {
		check("Integrate(Sqrt(ArcTan(a*x))/(x^2*Sqrt(c+a^2*c*x^2)), x)",
				"-((Sqrt(c+a^2*c*x^2)*Sqrt(ArcTan(a*x)))/(c*x))+(a*Unintegrable(1/(x*Sqrt(c+a^2*c*x^2)*Sqrt(ArcTan(a*x))), x))/2");

	}

	// {Sqrt(ArcTan(a*x))/(x^3*Sqrt(c+a^2*c*x^2)), x, 1, -(Sqrt(c+a^2*c*x^2)*Sqrt(ArcTan(a*x)))/(2*c*x^2) +
	// (a*Unintegrable(1/(x^2*Sqrt(c+a^2*c*x^2)*Sqrt(ArcTan(a*x))), x))/4 -
	// (a^2*Unintegrable(Sqrt(ArcTan(a*x))/(x*Sqrt(c+a^2*c*x^2)), x))/2}
	public void test02738() {
		check("Integrate(Sqrt(ArcTan(a*x))/(x^3*Sqrt(c+a^2*c*x^2)), x)",
				"-(Sqrt(c+a^2*c*x^2)*Sqrt(ArcTan(a*x)))/(2*c*x^2)+(a*Unintegrable(1/(x^2*Sqrt(c+a^2*c*x^2)*Sqrt(ArcTan(a*x))), x))/4-(a^2*Unintegrable(Sqrt(ArcTan(a*x))/(x*Sqrt(c+a^2*c*x^2)), x))/2");

	}

	// {x*(c+a^2*c*x^2)*ArcTan(a*x)^(3/2), x, 1, (c*(1+a^2*x^2)^2*ArcTan(a*x)^(3/2))/(4*a^2)-(3*Unintegrable((c +
	// a^2*c*x^2)*Sqrt(ArcTan(a*x)), x))/(8*a)}
	public void test02739() {
		check("Integrate(x*(c+a^2*c*x^2)*ArcTan(a*x)^(3/2), x)",
				"(c*(1+a^2*x^2)^2*ArcTan(a*x)^(3/2))/(4*a^2)-(3*Unintegrable((c+a^2*c*x^2)*Sqrt(ArcTan(a*x)), x))/(8*a)");

	}

	// {(c+a^2*c*x^2)*ArcTan(a*x)^(3/2), x, 1, -(c*(1+a^2*x^2)*Sqrt(ArcTan(a*x)))/(4*a)+(c*x*(1 +
	// a^2*x^2)*ArcTan(a*x)^(3/2))/3+(c*Unintegrable(1/Sqrt(ArcTan(a*x)), x))/8+(2*c*Unintegrable(ArcTan(a*x)^(3/2),
	// x))/3}
	public void test02740() {
		check("Integrate((c+a^2*c*x^2)*ArcTan(a*x)^(3/2), x)",
				"-(c*(1+a^2*x^2)*Sqrt(ArcTan(a*x)))/(4*a)+(c*x*(1+a^2*x^2)*ArcTan(a*x)^(3/2))/3+(c*Unintegrable(1/Sqrt(ArcTan(a*x)), x))/8+(2*c*Unintegrable(ArcTan(a*x)^(3/2), x))/3");

	}

	// {x*(c+a^2*c*x^2)^2*ArcTan(a*x)^(3/2), x, 1, (c^2*(1+a^2*x^2)^3*ArcTan(a*x)^(3/2))/(6*a^2)-Unintegrable((c +
	// a^2*c*x^2)^2*Sqrt(ArcTan(a*x)), x)/(4*a)}
	public void test02741() {
		check("Integrate(x*(c+a^2*c*x^2)^2*ArcTan(a*x)^(3/2), x)",
				"(c^2*(1+a^2*x^2)^3*ArcTan(a*x)^(3/2))/(6*a^2)-Unintegrable((c+a^2*c*x^2)^2*Sqrt(ArcTan(a*x)), x)/(4*a)");

	}

	// {x*(c+a^2*c*x^2)^3*ArcTan(a*x)^(3/2), x, 1, (c^3*(1+a^2*x^2)^4*ArcTan(a*x)^(3/2))/(8*a^2) -
	// (3*Unintegrable((c+a^2*c*x^2)^3*Sqrt(ArcTan(a*x)), x))/(16*a)}
	public void test02742() {
		check("Integrate(x*(c+a^2*c*x^2)^3*ArcTan(a*x)^(3/2), x)",
				"(c^3*(1+a^2*x^2)^4*ArcTan(a*x)^(3/2))/(8*a^2)-(3*Unintegrable((c+a^2*c*x^2)^3*Sqrt(ArcTan(a*x)), x))/(16*a)");

	}

	// {(x*ArcTan(a*x)^(3/2))/(c+a^2*c*x^2), x, 1, (2*x*ArcTan(a*x)^(5/2))/(5*a*c) -
	// (2*Unintegrable(ArcTan(a*x)^(5/2), x))/(5*a*c)}
	public void test02743() {
		check("Integrate((x*ArcTan(a*x)^(3/2))/(c+a^2*c*x^2), x)",
				"(2*x*ArcTan(a*x)^(5/2))/(5*a*c)-(2*Unintegrable(ArcTan(a*x)^(5/2), x))/(5*a*c)");

	}

	// {ArcTan(a*x)^(3/2)/(c+a^2*c*x^2), x, 1, (2*ArcTan(a*x)^(5/2))/(5*a*c)}
	public void test02744() {
		check("Integrate(ArcTan(a*x)^(3/2)/(c+a^2*c*x^2), x)", "(2*ArcTan(a*x)^(5/2))/(5*a*c)");

	}

	// {ArcTan(a*x)^(3/2)/(x*(c+a^2*c*x^2)), x, 1, (((-2*I)/5)*ArcTan(a*x)^(5/2))/c +
	// (I*Unintegrable(ArcTan(a*x)^(3/2)/(x*(I+a*x)), x))/c}
	public void test02745() {
		check("Integrate(ArcTan(a*x)^(3/2)/(x*(c+a^2*c*x^2)), x)",
				"(((-2*I)/5)*ArcTan(a*x)^(5/2))/c+(I*Unintegrable(ArcTan(a*x)^(3/2)/(x*(I+a*x)), x))/c");

	}

	// {x*Sqrt(c+a^2*c*x^2)*ArcTan(a*x)^(3/2), x, 1, ((c+a^2*c*x^2)^(3/2)*ArcTan(a*x)^(3/2))/(3*a^2*c) -
	// Unintegrable(Sqrt(c+a^2*c*x^2)*Sqrt(ArcTan(a*x)), x)/(2*a)}
	public void test02746() {
		check("Integrate(x*Sqrt(c+a^2*c*x^2)*ArcTan(a*x)^(3/2), x)",
				"((c+a^2*c*x^2)^(3/2)*ArcTan(a*x)^(3/2))/(3*a^2*c)-Unintegrable(Sqrt(c+a^2*c*x^2)*Sqrt(ArcTan(a*x)), x)/(2*a)");

	}

	// {Sqrt(c+a^2*c*x^2)*ArcTan(a*x)^(3/2), x, 1, (-3*Sqrt(c+a^2*c*x^2)*Sqrt(ArcTan(a*x)))/(4*a)+(x*Sqrt(c +
	// a^2*c*x^2)*ArcTan(a*x)^(3/2))/2+(3*c*Unintegrable(1/(Sqrt(c+a^2*c*x^2)*Sqrt(ArcTan(a*x))), x))/8 +
	// (c*Unintegrable(ArcTan(a*x)^(3/2)/Sqrt(c+a^2*c*x^2), x))/2}
	public void test02747() {
		check("Integrate(Sqrt(c+a^2*c*x^2)*ArcTan(a*x)^(3/2), x)",
				"(-3*Sqrt(c+a^2*c*x^2)*Sqrt(ArcTan(a*x)))/(4*a)+(x*Sqrt(c+a^2*c*x^2)*ArcTan(a*x)^(3/2))/2+(3*c*Unintegrable(1/(Sqrt(c+a^2*c*x^2)*Sqrt(ArcTan(a*x))), x))/8+(c*Unintegrable(ArcTan(a*x)^(3/2)/Sqrt(c+a^2*c*x^2), x))/2");

	}

	// {x*(c+a^2*c*x^2)^(3/2)*ArcTan(a*x)^(3/2), x, 1, ((c+a^2*c*x^2)^(5/2)*ArcTan(a*x)^(3/2))/(5*a^2*c) -
	// (3*Unintegrable((c+a^2*c*x^2)^(3/2)*Sqrt(ArcTan(a*x)), x))/(10*a)}
	public void test02748() {
		check("Integrate(x*(c+a^2*c*x^2)^(3/2)*ArcTan(a*x)^(3/2), x)",
				"((c+a^2*c*x^2)^(5/2)*ArcTan(a*x)^(3/2))/(5*a^2*c)-(3*Unintegrable((c+a^2*c*x^2)^(3/2)*Sqrt(ArcTan(a*x)), x))/(10*a)");

	}

	// {x*(c+a^2*c*x^2)^(5/2)*ArcTan(a*x)^(3/2), x, 1, ((c+a^2*c*x^2)^(7/2)*ArcTan(a*x)^(3/2))/(7*a^2*c) -
	// (3*Unintegrable((c+a^2*c*x^2)^(5/2)*Sqrt(ArcTan(a*x)), x))/(14*a)}
	public void test02749() {
		check("Integrate(x*(c+a^2*c*x^2)^(5/2)*ArcTan(a*x)^(3/2), x)",
				"((c+a^2*c*x^2)^(7/2)*ArcTan(a*x)^(3/2))/(7*a^2*c)-(3*Unintegrable((c+a^2*c*x^2)^(5/2)*Sqrt(ArcTan(a*x)), x))/(14*a)");

	}

	// {(x*ArcTan(a*x)^(3/2))/Sqrt(c+a^2*c*x^2), x, 1, (Sqrt(c+a^2*c*x^2)*ArcTan(a*x)^(3/2))/(a^2*c) -
	// (3*Unintegrable(Sqrt(ArcTan(a*x))/Sqrt(c+a^2*c*x^2), x))/(2*a)}
	public void test02750() {
		check("Integrate((x*ArcTan(a*x)^(3/2))/Sqrt(c+a^2*c*x^2), x)",
				"(Sqrt(c+a^2*c*x^2)*ArcTan(a*x)^(3/2))/(a^2*c)-(3*Unintegrable(Sqrt(ArcTan(a*x))/Sqrt(c+a^2*c*x^2), x))/(2*a)");

	}

	// {ArcTan(a*x)^(3/2)/(x^2*Sqrt(c+a^2*c*x^2)), x, 1, -((Sqrt(c+a^2*c*x^2)*ArcTan(a*x)^(3/2))/(c*x)) +
	// (3*a*Unintegrable(Sqrt(ArcTan(a*x))/(x*Sqrt(c+a^2*c*x^2)), x))/2}
	public void test02751() {
		check("Integrate(ArcTan(a*x)^(3/2)/(x^2*Sqrt(c+a^2*c*x^2)), x)",
				"-((Sqrt(c+a^2*c*x^2)*ArcTan(a*x)^(3/2))/(c*x))+(3*a*Unintegrable(Sqrt(ArcTan(a*x))/(x*Sqrt(c+a^2*c*x^2)), x))/2");

	}

	// {(c+a^2*c*x^2)*ArcTan(a*x)^(5/2), x, 1, (-5*c*(1+a^2*x^2)*ArcTan(a*x)^(3/2))/(12*a)+(c*x*(1 +
	// a^2*x^2)*ArcTan(a*x)^(5/2))/3+(5*c*Unintegrable(Sqrt(ArcTan(a*x)), x))/8+(2*c*Unintegrable(ArcTan(a*x)^(5/2),
	// x))/3}
	public void test02752() {
		check("Integrate((c+a^2*c*x^2)*ArcTan(a*x)^(5/2), x)",
				"(-5*c*(1+a^2*x^2)*ArcTan(a*x)^(3/2))/(12*a)+(c*x*(1+a^2*x^2)*ArcTan(a*x)^(5/2))/3+(5*c*Unintegrable(Sqrt(ArcTan(a*x)), x))/8+(2*c*Unintegrable(ArcTan(a*x)^(5/2), x))/3");

	}

	// {(x*ArcTan(a*x)^(5/2))/(c+a^2*c*x^2), x, 1, (2*x*ArcTan(a*x)^(7/2))/(7*a*c) -
	// (2*Unintegrable(ArcTan(a*x)^(7/2), x))/(7*a*c)}
	public void test02753() {
		check("Integrate((x*ArcTan(a*x)^(5/2))/(c+a^2*c*x^2), x)",
				"(2*x*ArcTan(a*x)^(7/2))/(7*a*c)-(2*Unintegrable(ArcTan(a*x)^(7/2), x))/(7*a*c)");

	}

	// {ArcTan(a*x)^(5/2)/(c+a^2*c*x^2), x, 1, (2*ArcTan(a*x)^(7/2))/(7*a*c)}
	public void test02754() {
		check("Integrate(ArcTan(a*x)^(5/2)/(c+a^2*c*x^2), x)", "(2*ArcTan(a*x)^(7/2))/(7*a*c)");

	}

	// {ArcTan(a*x)^(5/2)/(x*(c+a^2*c*x^2)), x, 1, (((-2*I)/7)*ArcTan(a*x)^(7/2))/c +
	// (I*Unintegrable(ArcTan(a*x)^(5/2)/(x*(I+a*x)), x))/c}
	public void test02755() {
		check("Integrate(ArcTan(a*x)^(5/2)/(x*(c+a^2*c*x^2)), x)",
				"(((-2*I)/7)*ArcTan(a*x)^(7/2))/c+(I*Unintegrable(ArcTan(a*x)^(5/2)/(x*(I+a*x)), x))/c");

	}

	// {Sqrt(c+a^2*c*x^2)*ArcTan(a*x)^(5/2), x, 1, (-5*Sqrt(c+a^2*c*x^2)*ArcTan(a*x)^(3/2))/(4*a)+(x*Sqrt(c +
	// a^2*c*x^2)*ArcTan(a*x)^(5/2))/2+(15*c*Unintegrable(Sqrt(ArcTan(a*x))/Sqrt(c+a^2*c*x^2), x))/8 +
	// (c*Unintegrable(ArcTan(a*x)^(5/2)/Sqrt(c+a^2*c*x^2), x))/2}
	public void test02756() {
		check("Integrate(Sqrt(c+a^2*c*x^2)*ArcTan(a*x)^(5/2), x)",
				"(-5*Sqrt(c+a^2*c*x^2)*ArcTan(a*x)^(3/2))/(4*a)+(x*Sqrt(c+a^2*c*x^2)*ArcTan(a*x)^(5/2))/2+(15*c*Unintegrable(Sqrt(ArcTan(a*x))/Sqrt(c+a^2*c*x^2), x))/8+(c*Unintegrable(ArcTan(a*x)^(5/2)/Sqrt(c+a^2*c*x^2), x))/2");

	}

	// {(x*ArcTan(a*x)^(5/2))/Sqrt(c+a^2*c*x^2), x, 1, (Sqrt(c+a^2*c*x^2)*ArcTan(a*x)^(5/2))/(a^2*c) -
	// (5*Unintegrable(ArcTan(a*x)^(3/2)/Sqrt(c+a^2*c*x^2), x))/(2*a)}
	public void test02757() {
		check("Integrate((x*ArcTan(a*x)^(5/2))/Sqrt(c+a^2*c*x^2), x)",
				"(Sqrt(c+a^2*c*x^2)*ArcTan(a*x)^(5/2))/(a^2*c)-(5*Unintegrable(ArcTan(a*x)^(3/2)/Sqrt(c+a^2*c*x^2), x))/(2*a)");

	}

	// {ArcTan(a*x)^(5/2)/(x^2*Sqrt(c+a^2*c*x^2)), x, 1, -((Sqrt(c+a^2*c*x^2)*ArcTan(a*x)^(5/2))/(c*x)) +
	// (5*a*Unintegrable(ArcTan(a*x)^(3/2)/(x*Sqrt(c+a^2*c*x^2)), x))/2}
	public void test02758() {
		check("Integrate(ArcTan(a*x)^(5/2)/(x^2*Sqrt(c+a^2*c*x^2)), x)",
				"-((Sqrt(c+a^2*c*x^2)*ArcTan(a*x)^(5/2))/(c*x))+(5*a*Unintegrable(ArcTan(a*x)^(3/2)/(x*Sqrt(c+a^2*c*x^2)), x))/2");

	}

	// {x/((c+a^2*c*x^2)*Sqrt(ArcTan(a*x))), x, 1, (2*x*Sqrt(ArcTan(a*x)))/(a*c)-(2*Unintegrable(Sqrt(ArcTan(a*x)),
	// x))/(a*c)}
	public void test02759() {
		check("Integrate(x/((c+a^2*c*x^2)*Sqrt(ArcTan(a*x))), x)",
				"(2*x*Sqrt(ArcTan(a*x)))/(a*c)-(2*Unintegrable(Sqrt(ArcTan(a*x)), x))/(a*c)");

	}

	// {1/((c+a^2*c*x^2)*Sqrt(ArcTan(a*x))), x, 1, (2*Sqrt(ArcTan(a*x)))/(a*c)}
	public void test02760() {
		check("Integrate(1/((c+a^2*c*x^2)*Sqrt(ArcTan(a*x))), x)", "(2*Sqrt(ArcTan(a*x)))/(a*c)");

	}

	// {x^m/((c+a^2*c*x^2)*ArcTan(a*x)^(3/2)), x, 1, (-2*x^m)/(a*c*Sqrt(ArcTan(a*x)))+(2*m*Unintegrable(x^(-1 +
	// m)/Sqrt(ArcTan(a*x)), x))/(a*c)}
	public void test02761() {
		check("Integrate(x^m/((c+a^2*c*x^2)*ArcTan(a*x)^(3/2)), x)",
				"(-2*x^m)/(a*c*Sqrt(ArcTan(a*x)))+(2*m*Unintegrable(x^(-1+m)/Sqrt(ArcTan(a*x)), x))/(a*c)");

	}

	// {x/((c+a^2*c*x^2)*ArcTan(a*x)^(3/2)), x, 1, (-2*x)/(a*c*Sqrt(ArcTan(a*x))) +
	// (2*Unintegrable(1/Sqrt(ArcTan(a*x)), x))/(a*c)}
	public void test02762() {
		check("Integrate(x/((c+a^2*c*x^2)*ArcTan(a*x)^(3/2)), x)",
				"(-2*x)/(a*c*Sqrt(ArcTan(a*x)))+(2*Unintegrable(1/Sqrt(ArcTan(a*x)), x))/(a*c)");

	}

	// {1/((c+a^2*c*x^2)*ArcTan(a*x)^(3/2)), x, 1, -2/(a*c*Sqrt(ArcTan(a*x)))}
	public void test02763() {
		check("Integrate(1/((c+a^2*c*x^2)*ArcTan(a*x)^(3/2)), x)", "-2/(a*c*Sqrt(ArcTan(a*x)))");

	}

	// {1/(x*(c+a^2*c*x^2)*ArcTan(a*x)^(3/2)), x, 1, -2/(a*c*x*Sqrt(ArcTan(a*x))) -
	// (2*Unintegrable(1/(x^2*Sqrt(ArcTan(a*x))), x))/(a*c)}
	public void test02764() {
		check("Integrate(1/(x*(c+a^2*c*x^2)*ArcTan(a*x)^(3/2)), x)",
				"-2/(a*c*x*Sqrt(ArcTan(a*x)))-(2*Unintegrable(1/(x^2*Sqrt(ArcTan(a*x))), x))/(a*c)");

	}

	// {x^4/((c+a^2*c*x^2)^2*ArcTan(a*x)^(3/2)), x, 1, (-2*x^4)/(a*c^2*(1+a^2*x^2)*Sqrt(ArcTan(a*x))) +
	// (8*Unintegrable(x^3/((c+a^2*c*x^2)^2*Sqrt(ArcTan(a*x))), x))/a+4*a*Unintegrable(x^5/((c +
	// a^2*c*x^2)^2*Sqrt(ArcTan(a*x))), x)}
	public void test02765() {
		check("Integrate(x^4/((c+a^2*c*x^2)^2*ArcTan(a*x)^(3/2)), x)",
				"(-2*x^4)/(a*c^2*(1+a^2*x^2)*Sqrt(ArcTan(a*x)))+(8*Unintegrable(x^3/((c+a^2*c*x^2)^2*Sqrt(ArcTan(a*x))), x))/a+4*a*Unintegrable(x^5/((c+a^2*c*x^2)^2*Sqrt(ArcTan(a*x))), x)");

	}

	// {1/(x^2*(c+a^2*c*x^2)^2*ArcTan(a*x)^(3/2)), x, 1, -2/(a*c^2*x^2*(1+a^2*x^2)*Sqrt(ArcTan(a*x))) -
	// (4*Unintegrable(1/(x^3*(c+a^2*c*x^2)^2*Sqrt(ArcTan(a*x))), x))/a-8*a*Unintegrable(1/(x*(c +
	// a^2*c*x^2)^2*Sqrt(ArcTan(a*x))), x)}
	public void test02766() {
		check("Integrate(1/(x^2*(c+a^2*c*x^2)^2*ArcTan(a*x)^(3/2)), x)",
				"-2/(a*c^2*x^2*(1+a^2*x^2)*Sqrt(ArcTan(a*x)))-(4*Unintegrable(1/(x^3*(c+a^2*c*x^2)^2*Sqrt(ArcTan(a*x))), x))/a-8*a*Unintegrable(1/(x*(c+a^2*c*x^2)^2*Sqrt(ArcTan(a*x))), x)");

	}

	// {1/(x^3*(c+a^2*c*x^2)^2*ArcTan(a*x)^(3/2)), x, 1, -2/(a*c^2*x^3*(1+a^2*x^2)*Sqrt(ArcTan(a*x))) -
	// (6*Unintegrable(1/(x^4*(c+a^2*c*x^2)^2*Sqrt(ArcTan(a*x))), x))/a-10*a*Unintegrable(1/(x^2*(c +
	// a^2*c*x^2)^2*Sqrt(ArcTan(a*x))), x)}
	public void test02767() {
		check("Integrate(1/(x^3*(c+a^2*c*x^2)^2*ArcTan(a*x)^(3/2)), x)",
				"-2/(a*c^2*x^3*(1+a^2*x^2)*Sqrt(ArcTan(a*x)))-(6*Unintegrable(1/(x^4*(c+a^2*c*x^2)^2*Sqrt(ArcTan(a*x))), x))/a-10*a*Unintegrable(1/(x^2*(c+a^2*c*x^2)^2*Sqrt(ArcTan(a*x))), x)");

	}

	// {1/(x^4*(c+a^2*c*x^2)^2*ArcTan(a*x)^(3/2)), x, 1, -2/(a*c^2*x^4*(1+a^2*x^2)*Sqrt(ArcTan(a*x))) -
	// (8*Unintegrable(1/(x^5*(c+a^2*c*x^2)^2*Sqrt(ArcTan(a*x))), x))/a-12*a*Unintegrable(1/(x^3*(c +
	// a^2*c*x^2)^2*Sqrt(ArcTan(a*x))), x)}
	public void test02768() {
		check("Integrate(1/(x^4*(c+a^2*c*x^2)^2*ArcTan(a*x)^(3/2)), x)",
				"-2/(a*c^2*x^4*(1+a^2*x^2)*Sqrt(ArcTan(a*x)))-(8*Unintegrable(1/(x^5*(c+a^2*c*x^2)^2*Sqrt(ArcTan(a*x))), x))/a-12*a*Unintegrable(1/(x^3*(c+a^2*c*x^2)^2*Sqrt(ArcTan(a*x))), x)");

	}

	// {1/(x^2*(c+a^2*c*x^2)^3*ArcTan(a*x)^(3/2)), x, 1, -2/(a*c^3*x^2*(1+a^2*x^2)^2*Sqrt(ArcTan(a*x))) -
	// (4*Unintegrable(1/(x^3*(c+a^2*c*x^2)^3*Sqrt(ArcTan(a*x))), x))/a-12*a*Unintegrable(1/(x*(c +
	// a^2*c*x^2)^3*Sqrt(ArcTan(a*x))), x)}
	public void test02769() {
		check("Integrate(1/(x^2*(c+a^2*c*x^2)^3*ArcTan(a*x)^(3/2)), x)",
				"-2/(a*c^3*x^2*(1+a^2*x^2)^2*Sqrt(ArcTan(a*x)))-(4*Unintegrable(1/(x^3*(c+a^2*c*x^2)^3*Sqrt(ArcTan(a*x))), x))/a-12*a*Unintegrable(1/(x*(c+a^2*c*x^2)^3*Sqrt(ArcTan(a*x))), x)");

	}

	// {1/(x^3*(c+a^2*c*x^2)^3*ArcTan(a*x)^(3/2)), x, 1, -2/(a*c^3*x^3*(1+a^2*x^2)^2*Sqrt(ArcTan(a*x))) -
	// (6*Unintegrable(1/(x^4*(c+a^2*c*x^2)^3*Sqrt(ArcTan(a*x))), x))/a-14*a*Unintegrable(1/(x^2*(c +
	// a^2*c*x^2)^3*Sqrt(ArcTan(a*x))), x)}
	public void test02770() {
		check("Integrate(1/(x^3*(c+a^2*c*x^2)^3*ArcTan(a*x)^(3/2)), x)",
				"-2/(a*c^3*x^3*(1+a^2*x^2)^2*Sqrt(ArcTan(a*x)))-(6*Unintegrable(1/(x^4*(c+a^2*c*x^2)^3*Sqrt(ArcTan(a*x))), x))/a-14*a*Unintegrable(1/(x^2*(c+a^2*c*x^2)^3*Sqrt(ArcTan(a*x))), x)");

	}

	// {1/(x^4*(c+a^2*c*x^2)^3*ArcTan(a*x)^(3/2)), x, 1, -2/(a*c^3*x^4*(1+a^2*x^2)^2*Sqrt(ArcTan(a*x))) -
	// (8*Unintegrable(1/(x^5*(c+a^2*c*x^2)^3*Sqrt(ArcTan(a*x))), x))/a-16*a*Unintegrable(1/(x^3*(c +
	// a^2*c*x^2)^3*Sqrt(ArcTan(a*x))), x)}
	public void test02771() {
		check("Integrate(1/(x^4*(c+a^2*c*x^2)^3*ArcTan(a*x)^(3/2)), x)",
				"-2/(a*c^3*x^4*(1+a^2*x^2)^2*Sqrt(ArcTan(a*x)))-(8*Unintegrable(1/(x^5*(c+a^2*c*x^2)^3*Sqrt(ArcTan(a*x))), x))/a-16*a*Unintegrable(1/(x^3*(c+a^2*c*x^2)^3*Sqrt(ArcTan(a*x))), x)");

	}

	// {1/(x*Sqrt(c+a^2*c*x^2)*ArcTan(a*x)^(3/2)), x, 1, (-2*Sqrt(c+a^2*c*x^2))/(a*c*x*Sqrt(ArcTan(a*x))) -
	// (2*Unintegrable(1/(x^2*Sqrt(c+a^2*c*x^2)*Sqrt(ArcTan(a*x))), x))/a}
	public void test02772() {
		check("Integrate(1/(x*Sqrt(c+a^2*c*x^2)*ArcTan(a*x)^(3/2)), x)",
				"(-2*Sqrt(c+a^2*c*x^2))/(a*c*x*Sqrt(ArcTan(a*x)))-(2*Unintegrable(1/(x^2*Sqrt(c+a^2*c*x^2)*Sqrt(ArcTan(a*x))), x))/a");

	}

	// {x^3/((c+a^2*c*x^2)^(3/2)*ArcTan(a*x)^(3/2)), x, 1, (-2*x^3)/(a*c*Sqrt(c+a^2*c*x^2)*Sqrt(ArcTan(a*x))) +
	// (6*Unintegrable(x^2/((c+a^2*c*x^2)^(3/2)*Sqrt(ArcTan(a*x))), x))/a+4*a*Unintegrable(x^4/((c +
	// a^2*c*x^2)^(3/2)*Sqrt(ArcTan(a*x))), x)}
	public void test02773() {
		check("Integrate(x^3/((c+a^2*c*x^2)^(3/2)*ArcTan(a*x)^(3/2)), x)",
				"(-2*x^3)/(a*c*Sqrt(c+a^2*c*x^2)*Sqrt(ArcTan(a*x)))+(6*Unintegrable(x^2/((c+a^2*c*x^2)^(3/2)*Sqrt(ArcTan(a*x))), x))/a+4*a*Unintegrable(x^4/((c+a^2*c*x^2)^(3/2)*Sqrt(ArcTan(a*x))), x)");

	}

	// {1/(x^2*(c+a^2*c*x^2)^(3/2)*ArcTan(a*x)^(3/2)), x, 1, -2/(a*c*x^2*Sqrt(c+a^2*c*x^2)*Sqrt(ArcTan(a*x))) -
	// (4*Unintegrable(1/(x^3*(c+a^2*c*x^2)^(3/2)*Sqrt(ArcTan(a*x))), x))/a-6*a*Unintegrable(1/(x*(c +
	// a^2*c*x^2)^(3/2)*Sqrt(ArcTan(a*x))), x)}
	public void test02774() {
		check("Integrate(1/(x^2*(c+a^2*c*x^2)^(3/2)*ArcTan(a*x)^(3/2)), x)",
				"-2/(a*c*x^2*Sqrt(c+a^2*c*x^2)*Sqrt(ArcTan(a*x)))-(4*Unintegrable(1/(x^3*(c+a^2*c*x^2)^(3/2)*Sqrt(ArcTan(a*x))), x))/a-6*a*Unintegrable(1/(x*(c+a^2*c*x^2)^(3/2)*Sqrt(ArcTan(a*x))), x)");

	}

	// {1/(x^3*(c+a^2*c*x^2)^(3/2)*ArcTan(a*x)^(3/2)), x, 1, -2/(a*c*x^3*Sqrt(c+a^2*c*x^2)*Sqrt(ArcTan(a*x))) -
	// (6*Unintegrable(1/(x^4*(c+a^2*c*x^2)^(3/2)*Sqrt(ArcTan(a*x))), x))/a-8*a*Unintegrable(1/(x^2*(c +
	// a^2*c*x^2)^(3/2)*Sqrt(ArcTan(a*x))), x)}
	public void test02775() {
		check("Integrate(1/(x^3*(c+a^2*c*x^2)^(3/2)*ArcTan(a*x)^(3/2)), x)",
				"-2/(a*c*x^3*Sqrt(c+a^2*c*x^2)*Sqrt(ArcTan(a*x)))-(6*Unintegrable(1/(x^4*(c+a^2*c*x^2)^(3/2)*Sqrt(ArcTan(a*x))), x))/a-8*a*Unintegrable(1/(x^2*(c+a^2*c*x^2)^(3/2)*Sqrt(ArcTan(a*x))), x)");

	}

	// {1/(x^4*(c+a^2*c*x^2)^(3/2)*ArcTan(a*x)^(3/2)), x, 1, -2/(a*c*x^4*Sqrt(c+a^2*c*x^2)*Sqrt(ArcTan(a*x))) -
	// (8*Unintegrable(1/(x^5*(c+a^2*c*x^2)^(3/2)*Sqrt(ArcTan(a*x))), x))/a-10*a*Unintegrable(1/(x^3*(c +
	// a^2*c*x^2)^(3/2)*Sqrt(ArcTan(a*x))), x)}
	public void test02776() {
		check("Integrate(1/(x^4*(c+a^2*c*x^2)^(3/2)*ArcTan(a*x)^(3/2)), x)",
				"-2/(a*c*x^4*Sqrt(c+a^2*c*x^2)*Sqrt(ArcTan(a*x)))-(8*Unintegrable(1/(x^5*(c+a^2*c*x^2)^(3/2)*Sqrt(ArcTan(a*x))), x))/a-10*a*Unintegrable(1/(x^3*(c+a^2*c*x^2)^(3/2)*Sqrt(ArcTan(a*x))), x)");

	}

	// {1/(x^2*(c+a^2*c*x^2)^(5/2)*ArcTan(a*x)^(3/2)), x, 1, -2/(a*c*x^2*(c+a^2*c*x^2)^(3/2)*Sqrt(ArcTan(a*x))) -
	// (4*Unintegrable(1/(x^3*(c+a^2*c*x^2)^(5/2)*Sqrt(ArcTan(a*x))), x))/a-10*a*Unintegrable(1/(x*(c +
	// a^2*c*x^2)^(5/2)*Sqrt(ArcTan(a*x))), x)}
	public void test02777() {
		check("Integrate(1/(x^2*(c+a^2*c*x^2)^(5/2)*ArcTan(a*x)^(3/2)), x)",
				"-2/(a*c*x^2*(c+a^2*c*x^2)^(3/2)*Sqrt(ArcTan(a*x)))-(4*Unintegrable(1/(x^3*(c+a^2*c*x^2)^(5/2)*Sqrt(ArcTan(a*x))), x))/a-10*a*Unintegrable(1/(x*(c+a^2*c*x^2)^(5/2)*Sqrt(ArcTan(a*x))), x)");

	}

	// {1/(x^3*(c+a^2*c*x^2)^(5/2)*ArcTan(a*x)^(3/2)), x, 1, -2/(a*c*x^3*(c+a^2*c*x^2)^(3/2)*Sqrt(ArcTan(a*x))) -
	// (6*Unintegrable(1/(x^4*(c+a^2*c*x^2)^(5/2)*Sqrt(ArcTan(a*x))), x))/a-12*a*Unintegrable(1/(x^2*(c +
	// a^2*c*x^2)^(5/2)*Sqrt(ArcTan(a*x))), x)}
	public void test02778() {
		check("Integrate(1/(x^3*(c+a^2*c*x^2)^(5/2)*ArcTan(a*x)^(3/2)), x)",
				"-2/(a*c*x^3*(c+a^2*c*x^2)^(3/2)*Sqrt(ArcTan(a*x)))-(6*Unintegrable(1/(x^4*(c+a^2*c*x^2)^(5/2)*Sqrt(ArcTan(a*x))), x))/a-12*a*Unintegrable(1/(x^2*(c+a^2*c*x^2)^(5/2)*Sqrt(ArcTan(a*x))), x)");

	}

	// {1/(x^4*(c+a^2*c*x^2)^(5/2)*ArcTan(a*x)^(3/2)), x, 1, -2/(a*c*x^4*(c+a^2*c*x^2)^(3/2)*Sqrt(ArcTan(a*x))) -
	// (8*Unintegrable(1/(x^5*(c+a^2*c*x^2)^(5/2)*Sqrt(ArcTan(a*x))), x))/a-14*a*Unintegrable(1/(x^3*(c +
	// a^2*c*x^2)^(5/2)*Sqrt(ArcTan(a*x))), x)}
	public void test02779() {
		check("Integrate(1/(x^4*(c+a^2*c*x^2)^(5/2)*ArcTan(a*x)^(3/2)), x)",
				"-2/(a*c*x^4*(c+a^2*c*x^2)^(3/2)*Sqrt(ArcTan(a*x)))-(8*Unintegrable(1/(x^5*(c+a^2*c*x^2)^(5/2)*Sqrt(ArcTan(a*x))), x))/a-14*a*Unintegrable(1/(x^3*(c+a^2*c*x^2)^(5/2)*Sqrt(ArcTan(a*x))), x)");

	}

	// {x^m/((c+a^2*c*x^2)*ArcTan(a*x)^(5/2)), x, 1, (-2*x^m)/(3*a*c*ArcTan(a*x)^(3/2))+(2*m*Unintegrable(x^(-1 +
	// m)/ArcTan(a*x)^(3/2), x))/(3*a*c)}
	public void test02780() {
		check("Integrate(x^m/((c+a^2*c*x^2)*ArcTan(a*x)^(5/2)), x)",
				"(-2*x^m)/(3*a*c*ArcTan(a*x)^(3/2))+(2*m*Unintegrable(x^(-1+m)/ArcTan(a*x)^(3/2), x))/(3*a*c)");

	}

	// {x/((c+a^2*c*x^2)*ArcTan(a*x)^(5/2)), x, 1, (-2*x)/(3*a*c*ArcTan(a*x)^(3/2)) +
	// (2*Unintegrable(ArcTan(a*x)^(-3/2), x))/(3*a*c)}
	public void test02781() {
		check("Integrate(x/((c+a^2*c*x^2)*ArcTan(a*x)^(5/2)), x)",
				"(-2*x)/(3*a*c*ArcTan(a*x)^(3/2))+(2*Unintegrable(ArcTan(a*x)^(-3/2), x))/(3*a*c)");

	}

	// {1/((c+a^2*c*x^2)*ArcTan(a*x)^(5/2)), x, 1, -2/(3*a*c*ArcTan(a*x)^(3/2))}
	public void test02782() {
		check("Integrate(1/((c+a^2*c*x^2)*ArcTan(a*x)^(5/2)), x)", "-2/(3*a*c*ArcTan(a*x)^(3/2))");

	}

	// {1/(x*(c+a^2*c*x^2)*ArcTan(a*x)^(5/2)), x, 1, -2/(3*a*c*x*ArcTan(a*x)^(3/2)) -
	// (2*Unintegrable(1/(x^2*ArcTan(a*x)^(3/2)), x))/(3*a*c)}
	public void test02783() {
		check("Integrate(1/(x*(c+a^2*c*x^2)*ArcTan(a*x)^(5/2)), x)",
				"-2/(3*a*c*x*ArcTan(a*x)^(3/2))-(2*Unintegrable(1/(x^2*ArcTan(a*x)^(3/2)), x))/(3*a*c)");

	}

	// {1/(x*Sqrt(c+a^2*c*x^2)*ArcTan(a*x)^(5/2)), x, 1, (-2*Sqrt(c+a^2*c*x^2))/(3*a*c*x*ArcTan(a*x)^(3/2)) -
	// (2*Unintegrable(1/(x^2*Sqrt(c+a^2*c*x^2)*ArcTan(a*x)^(3/2)), x))/(3*a)}
	public void test02784() {
		check("Integrate(1/(x*Sqrt(c+a^2*c*x^2)*ArcTan(a*x)^(5/2)), x)",
				"(-2*Sqrt(c+a^2*c*x^2))/(3*a*c*x*ArcTan(a*x)^(3/2))-(2*Unintegrable(1/(x^2*Sqrt(c+a^2*c*x^2)*ArcTan(a*x)^(3/2)), x))/(3*a)");

	}

	// {(x*ArcTan(a*x)^n)/(c+a^2*c*x^2), x, 1, (x*ArcTan(a*x)^(1+n))/(a*c*(1+n))-Unintegrable(ArcTan(a*x)^(1 +
	// n), x)/(a*c*(1+n))}
	public void test02785() {
		check("Integrate((x*ArcTan(a*x)^n)/(c+a^2*c*x^2), x)",
				"(x*ArcTan(a*x)^(1+n))/(a*c*(1+n))-Unintegrable(ArcTan(a*x)^(1+n), x)/(a*c*(1+n))");

	}

	// {ArcTan(a*x)^n/(c+a^2*c*x^2), x, 1, ArcTan(a*x)^(1+n)/(a*c*(1+n))}
	public void test02786() {
		check("Integrate(ArcTan(a*x)^n/(c+a^2*c*x^2), x)", "ArcTan(a*x)^(1+n)/(a*c*(1+n))");

	}

	// {(e+f*x)^m*(a+b*ArcTan(c+d*x))^2, x, 1, Unintegrable((e+f*x)^m*(a+b*ArcTan(c+d*x))^2, x)}
	public void test02787() {
		check("Integrate((e+f*x)^m*(a+b*ArcTan(c+d*x))^2, x)", "Unintegrable((e+f*x)^m*(a+b*ArcTan(c+d*x))^2, x)");

	}

	// {(e+f*x)^m*(a+b*ArcTan(c+d*x))^3, x, 1, Unintegrable((e+f*x)^m*(a+b*ArcTan(c+d*x))^3, x)}
	public void test02788() {
		check("Integrate((e+f*x)^m*(a+b*ArcTan(c+d*x))^3, x)", "Unintegrable((e+f*x)^m*(a+b*ArcTan(c+d*x))^3, x)");

	}

	// {ArcTan(a+b*x)/(1+a^2+2*a*b*x+b^2*x^2)^(1/3), x, 1, Unintegrable(ArcTan(a+b*x)/(1+(a+b*x)^2)^(1/3),
	// x)}
	public void test02789() {
		check("Integrate(ArcTan(a+b*x)/(1+a^2+2*a*b*x+b^2*x^2)^(1/3), x)",
				"Unintegrable(ArcTan(a+b*x)/(1+(a+b*x)^2)^(1/3), x)");

	}

	// {ArcTan(a+b*x)/((1+a^2)*c+2*a*b*c*x+b^2*c*x^2)^(1/3), x, 1, Unintegrable(ArcTan(a+b*x)/(c+c*(a +
	// b*x)^2)^(1/3), x)}
	public void test02790() {
		check("Integrate(ArcTan(a+b*x)/((1+a^2)*c+2*a*b*c*x+b^2*c*x^2)^(1/3), x)",
				"Unintegrable(ArcTan(a+b*x)/(c+c*(a+b*x)^2)^(1/3), x)");

	}

	// {((a+b*x)^2*ArcTan(a+b*x))/(1+a^2+2*a*b*x+b^2*x^2)^(1/3), x, 1, Unintegrable(((a+b*x)^2*ArcTan(a +
	// b*x))/(1+(a+b*x)^2)^(1/3), x)}
	public void test02791() {
		check("Integrate(((a+b*x)^2*ArcTan(a+b*x))/(1+a^2+2*a*b*x+b^2*x^2)^(1/3), x)",
				"Unintegrable(((a+b*x)^2*ArcTan(a+b*x))/(1+(a+b*x)^2)^(1/3), x)");

	}

	// {((a+b*x)^2*ArcTan(a+b*x))/((1+a^2)*c+2*a*b*c*x+b^2*c*x^2)^(1/3), x, 1, Unintegrable(((a +
	// b*x)^2*ArcTan(a+b*x))/(c+c*(a+b*x)^2)^(1/3), x)}
	public void test02792() {
		check("Integrate(((a+b*x)^2*ArcTan(a+b*x))/((1+a^2)*c+2*a*b*c*x+b^2*c*x^2)^(1/3), x)",
				"Unintegrable(((a+b*x)^2*ArcTan(a+b*x))/(c+c*(a+b*x)^2)^(1/3), x)");

	}

	// {1/((1+x^2)*(2+ArcTan(x))), x, 1, Log(2+ArcTan(x))}
	public void test02793() {
		check("Integrate(1/((1+x^2)*(2+ArcTan(x))), x)", "Log(2+ArcTan(x))");

	}

	// {1/((a+a*x^2)*(b-2*b*ArcTan(x))), x, 1, -Log(1-2*ArcTan(x))/(2*a*b)}
	public void test02794() {
		check("Integrate(1/((a+a*x^2)*(b-2*b*ArcTan(x))), x)", "-Log(1-2*ArcTan(x))/(2*a*b)");

	}

	// {E^ArcTan(a*x)/(c+a^2*c*x^2), x, 1, E^ArcTan(a*x)/(a*c)}
	public void test02795() {
		check("Integrate(E^ArcTan(a*x)/(c+a^2*c*x^2), x)", "E^ArcTan(a*x)/(a*c)");

	}

	// {E^ArcTan(a*x)/(c+a^2*c*x^2)^(3/2), x, 1, (E^ArcTan(a*x)*(1+a*x))/(2*a*c*Sqrt(c+a^2*c*x^2))}
	public void test02796() {
		check("Integrate(E^ArcTan(a*x)/(c+a^2*c*x^2)^(3/2), x)", "(E^ArcTan(a*x)*(1+a*x))/(2*a*c*Sqrt(c+a^2*c*x^2))");

	}

	// {E^(2*ArcTan(a*x))/(c+a^2*c*x^2), x, 1, E^(2*ArcTan(a*x))/(2*a*c)}
	public void test02797() {
		check("Integrate(E^(2*ArcTan(a*x))/(c+a^2*c*x^2), x)", "E^(2*ArcTan(a*x))/(2*a*c)");

	}

	// {E^(2*ArcTan(a*x))/(c+a^2*c*x^2)^(3/2), x, 1, (E^(2*ArcTan(a*x))*(2+a*x))/(5*a*c*Sqrt(c+a^2*c*x^2))}
	public void test02798() {
		check("Integrate(E^(2*ArcTan(a*x))/(c+a^2*c*x^2)^(3/2), x)",
				"(E^(2*ArcTan(a*x))*(2+a*x))/(5*a*c*Sqrt(c+a^2*c*x^2))");

	}

	// {1/(E^ArcTan(a*x)*(c+a^2*c*x^2)), x, 1, -(1/(a*c*E^ArcTan(a*x)))}
	public void test02799() {
		check("Integrate(1/(E^ArcTan(a*x)*(c+a^2*c*x^2)), x)", "-(1/(a*c*E^ArcTan(a*x)))");

	}

	// {1/(E^ArcTan(a*x)*(c+a^2*c*x^2)^(3/2)), x, 1, -(1-a*x)/(2*a*c*E^ArcTan(a*x)*Sqrt(c+a^2*c*x^2))}
	public void test02800() {
		check("Integrate(1/(E^ArcTan(a*x)*(c+a^2*c*x^2)^(3/2)), x)",
				"-(1-a*x)/(2*a*c*E^ArcTan(a*x)*Sqrt(c+a^2*c*x^2))");

	}

	// {1/(E^(2*ArcTan(a*x))*(c+a^2*c*x^2)), x, 1, -1/(2*a*c*E^(2*ArcTan(a*x)))}
	public void test02801() {
		check("Integrate(1/(E^(2*ArcTan(a*x))*(c+a^2*c*x^2)), x)", "-1/(2*a*c*E^(2*ArcTan(a*x)))");

	}

	// {1/(E^(2*ArcTan(a*x))*(c+a^2*c*x^2)^(3/2)), x, 1, -(2-a*x)/(5*a*c*E^(2*ArcTan(a*x))*Sqrt(c+a^2*c*x^2))}
	public void test02802() {
		check("Integrate(1/(E^(2*ArcTan(a*x))*(c+a^2*c*x^2)^(3/2)), x)",
				"-(2-a*x)/(5*a*c*E^(2*ArcTan(a*x))*Sqrt(c+a^2*c*x^2))");

	}

	// {E^(n*ArcTan(a*x))/(c+a^2*c*x^2), x, 1, E^(n*ArcTan(a*x))/(a*c*n)}
	public void test02803() {
		check("Integrate(E^(n*ArcTan(a*x))/(c+a^2*c*x^2), x)", "E^(n*ArcTan(a*x))/(a*c*n)");

	}

	// {E^(I*n*ArcTan(a*x))*x^2*(c+a^2*c*x^2)^(-1-n^2/2), x, 1, (I*E^(I*n*ArcTan(a*x))*(1-I*a*n*x))/(a^3*c*n*(1 -
	// n^2)*(c+a^2*c*x^2)^(n^2/2))}
	public void test02804() {
		check("Integrate(E^(I*n*ArcTan(a*x))*x^2*(c+a^2*c*x^2)^(-1-n^2/2), x)",
				"(I*E^(I*n*ArcTan(a*x))*(1-I*a*n*x))/(a^3*c*n*(1-n^2)*(c+a^2*c*x^2)^(n^2/2))");

	}

	// {ArcCot(x)/(1+x^2), x, 1, -ArcCot(x)^2/2}
	public void test02805() {
		check("Integrate(ArcCot(x)/(1+x^2), x)", "-ArcCot(x)^2/2");

	}

	// {1/((1+x^2)*ArcCot(x)), x, 1, -Log(ArcCot(x))}
	public void test02806() {
		check("Integrate(1/((1+x^2)*ArcCot(x)), x)", "-Log(ArcCot(x))");

	}

	// {ArcCot(x)^n/(1+x^2), x, 1, -(ArcCot(x)^(1+n)/(1+n))}
	public void test02807() {
		check("Integrate(ArcCot(x)^n/(1+x^2), x)", "-(ArcCot(x)^(1+n)/(1+n))");

	}

	// {ArcCot(x)/(a+a*x^2)^(3/2), x, 1, -(1/(a*Sqrt(a+a*x^2)))+(x*ArcCot(x))/(a*Sqrt(a+a*x^2))}
	public void test02808() {
		check("Integrate(ArcCot(x)/(a+a*x^2)^(3/2), x)", "-(1/(a*Sqrt(a+a*x^2)))+(x*ArcCot(x))/(a*Sqrt(a+a*x^2))");

	}

	// {ArcCot(a+b*x)/(1+a^2+2*a*b*x+b^2*x^2)^(1/3), x, 1, Unintegrable(ArcCot(a+b*x)/(1+(a+b*x)^2)^(1/3),
	// x)}
	public void test02809() {
		check("Integrate(ArcCot(a+b*x)/(1+a^2+2*a*b*x+b^2*x^2)^(1/3), x)",
				"Unintegrable(ArcCot(a+b*x)/(1+(a+b*x)^2)^(1/3), x)");

	}

	// {ArcCot(a+b*x)/((1+a^2)*c+2*a*b*c*x+b^2*c*x^2)^(1/3), x, 1, Unintegrable(ArcCot(a+b*x)/(c+c*(a +
	// b*x)^2)^(1/3), x)}
	public void test02810() {
		check("Integrate(ArcCot(a+b*x)/((1+a^2)*c+2*a*b*c*x+b^2*c*x^2)^(1/3), x)",
				"Unintegrable(ArcCot(a+b*x)/(c+c*(a+b*x)^2)^(1/3), x)");

	}

	// {((a+b*x)^2*ArcCot(a+b*x))/(1+a^2+2*a*b*x+b^2*x^2)^(1/3), x, 1, Unintegrable(((a+b*x)^2*ArcCot(a +
	// b*x))/(1+(a+b*x)^2)^(1/3), x)}
	public void test02811() {
		check("Integrate(((a+b*x)^2*ArcCot(a+b*x))/(1+a^2+2*a*b*x+b^2*x^2)^(1/3), x)",
				"Unintegrable(((a+b*x)^2*ArcCot(a+b*x))/(1+(a+b*x)^2)^(1/3), x)");

	}

	// {((a+b*x)^2*ArcCot(a+b*x))/((1+a^2)*c+2*a*b*c*x+b^2*c*x^2)^(1/3), x, 1, Unintegrable(((a +
	// b*x)^2*ArcCot(a+b*x))/(c+c*(a+b*x)^2)^(1/3), x)}
	public void test02812() {
		check("Integrate(((a+b*x)^2*ArcCot(a+b*x))/((1+a^2)*c+2*a*b*c*x+b^2*c*x^2)^(1/3), x)",
				"Unintegrable(((a+b*x)^2*ArcCot(a+b*x))/(c+c*(a+b*x)^2)^(1/3), x)");

	}

	// {(e+f*x)^m*(a+b*ArcCot(c+d*x))^2, x, 1, Unintegrable((e+f*x)^m*(a+b*ArcCot(c+d*x))^2, x)}
	public void test02813() {
		check("Integrate((e+f*x)^m*(a+b*ArcCot(c+d*x))^2, x)", "Unintegrable((e+f*x)^m*(a+b*ArcCot(c+d*x))^2, x)");

	}

	// {(e+f*x)^m*(a+b*ArcCot(c+d*x))^3, x, 1, Unintegrable((e+f*x)^m*(a+b*ArcCot(c+d*x))^3, x)}
	public void test02814() {
		check("Integrate((e+f*x)^m*(a+b*ArcCot(c+d*x))^3, x)", "Unintegrable((e+f*x)^m*(a+b*ArcCot(c+d*x))^3, x)");

	}

	// {1/((a+a*x^2)*(b-2*b*ArcCot(x))), x, 1, Log(1-2*ArcCot(x))/(2*a*b)}
	public void test02815() {
		check("Integrate(1/((a+a*x^2)*(b-2*b*ArcCot(x))), x)", "Log(1-2*ArcCot(x))/(2*a*b)");

	}

	// {E^ArcCot(x)/(a+a*x^2), x, 1, -(E^ArcCot(x)/a)}
	public void test02816() {
		check("Integrate(E^ArcCot(x)/(a+a*x^2), x)", "-(E^ArcCot(x)/a)");

	}

	// {E^ArcCot(x)/(a+a*x^2)^(3/2), x, 1, -(E^ArcCot(x)*(1-x))/(2*a*Sqrt(a+a*x^2))}
	public void test02817() {
		check("Integrate(E^ArcCot(x)/(a+a*x^2)^(3/2), x)", "-(E^ArcCot(x)*(1-x))/(2*a*Sqrt(a+a*x^2))");

	}

	// {Sinh(x)^(3/2)/x^3, x, 1, (-3*Cosh(x)*Sqrt(Sinh(x)))/(4*x)-Sinh(x)^(3/2)/(2*x^2) +
	// (3*Unintegrable(1/(x*Sqrt(Sinh(x))), x))/8+(9*Unintegrable(Sinh(x)^(3/2)/x, x))/8}
	public void test02818() {
		check("Integrate(Sinh(x)^(3/2)/x^3, x)",
				"(-3*Cosh(x)*Sqrt(Sinh(x)))/(4*x)-Sinh(x)^(3/2)/(2*x^2)+(3*Unintegrable(1/(x*Sqrt(Sinh(x))), x))/8+(9*Unintegrable(Sinh(x)^(3/2)/x, x))/8");

	}

	// {(e*x)^m*Csch(a+b*x^2), x, 1, ((e*x)^m*Unintegrable(x^m*Csch(a+b*x^2), x))/x^m}
	public void test02819() {
		check("Integrate((e*x)^m*Csch(a+b*x^2), x)", "((e*x)^m*Unintegrable(x^m*Csch(a+b*x^2), x))/x^m");

	}

	// {(e*x)^m*Csch(a+b/x), x, 1, ((e*x)^m*Unintegrable(x^m*Csch(a+b/x), x))/x^m}
	public void test02820() {
		check("Integrate((e*x)^m*Csch(a+b/x), x)", "((e*x)^m*Unintegrable(x^m*Csch(a+b/x), x))/x^m");

	}

	// {(e*x)^m*Csch(a+b/x^2), x, 1, ((e*x)^m*Unintegrable(x^m*Csch(a+b/x^2), x))/x^m}
	public void test02821() {
		check("Integrate((e*x)^m*Csch(a+b/x^2), x)", "((e*x)^m*Unintegrable(x^m*Csch(a+b/x^2), x))/x^m");

	}

	// {(e*x)^(-1+2*n)*(b*Sinh(c+d*x^n))^p, x, 1, ((e*x)^(2*n)*Unintegrable(x^(-1+2*n)*(b*Sinh(c+d*x^n))^p,
	// x))/(e*x^(2*n))}
	public void test02822() {
		check("Integrate((e*x)^(-1+2*n)*(b*Sinh(c+d*x^n))^p, x)",
				"((e*x)^(2*n)*Unintegrable(x^(-1+2*n)*(b*Sinh(c+d*x^n))^p, x))/(e*x^(2*n))");

	}

	// {(e*x)^(-1+2*n)*(a+b*Sinh(c+d*x^n))^p, x, 1, ((e*x)^(2*n)*Unintegrable(x^(-1+2*n)*(a+b*Sinh(c +
	// d*x^n))^p, x))/(e*x^(2*n))}
	public void test02823() {
		check("Integrate((e*x)^(-1+2*n)*(a+b*Sinh(c+d*x^n))^p, x)",
				"((e*x)^(2*n)*Unintegrable(x^(-1+2*n)*(a+b*Sinh(c+d*x^n))^p, x))/(e*x^(2*n))");

	}

	// {(e*x)^m*Csch(a+b*x^n)^2, x, 1, ((e*x)^m*Unintegrable(x^m*Csch(a+b*x^n)^2, x))/x^m}
	public void test02824() {
		check("Integrate((e*x)^m*Csch(a+b*x^n)^2, x)", "((e*x)^m*Unintegrable(x^m*Csch(a+b*x^n)^2, x))/x^m");

	}

	// {Sinh((a+b*x)^2)/x, x, 1, b*CannotIntegrate(Sinh((a+b*x)^2)/(b*x), x)}
	public void test02825() {
		check("Integrate(Sinh((a+b*x)^2)/x, x)", "b*CannotIntegrate(Sinh((a+b*x)^2)/(b*x), x)");

	}

	// {Sinh(a+b*x), x, 1, Cosh(a+b*x)/b}
	public void test02826() {
		check("Integrate(Sinh(a+b*x), x)", "Cosh(a+b*x)/b");

	}

	// {Sqrt(I*Sinh(c+d*x)), x, 1, ((-2*I)*EllipticE((I*c-Pi/2+I*d*x)/2, 2))/d}
	public void test02827() {
		check("Integrate(Sqrt(I*Sinh(c+d*x)), x)", "((-2*I)*EllipticE((I*c-Pi/2+I*d*x)/2, 2))/d");

	}

	// {1/Sqrt(I*Sinh(c+d*x)), x, 1, ((-2*I)*EllipticF((I*c-Pi/2+I*d*x)/2, 2))/d}
	public void test02828() {
		check("Integrate(1/Sqrt(I*Sinh(c+d*x)), x)", "((-2*I)*EllipticF((I*c-Pi/2+I*d*x)/2, 2))/d");

	}

	// {(b*Sinh(c+d*x))^(4/3), x, 1, (3*Cosh(c+d*x)*Hypergeometric2F1(1/2, 7/6, 13/6, -Sinh(c+d*x)^2)*(b*Sinh(c +
	// d*x))^(7/3))/(7*b*d*Sqrt(Cosh(c+d*x)^2))}
	public void test02829() {
		check("Integrate((b*Sinh(c+d*x))^(4/3), x)",
				"(3*Cosh(c+d*x)*Hypergeometric2F1(1/2, 7/6, 13/6, -Sinh(c+d*x)^2)*(b*Sinh(c+d*x))^(7/3))/(7*b*d*Sqrt(Cosh(c+d*x)^2))");

	}

	// {(b*Sinh(c+d*x))^(2/3), x, 1, (3*Cosh(c+d*x)*Hypergeometric2F1(1/2, 5/6, 11/6, -Sinh(c+d*x)^2)*(b*Sinh(c +
	// d*x))^(5/3))/(5*b*d*Sqrt(Cosh(c+d*x)^2))}
	public void test02830() {
		check("Integrate((b*Sinh(c+d*x))^(2/3), x)",
				"(3*Cosh(c+d*x)*Hypergeometric2F1(1/2, 5/6, 11/6, -Sinh(c+d*x)^2)*(b*Sinh(c+d*x))^(5/3))/(5*b*d*Sqrt(Cosh(c+d*x)^2))");

	}

	// {(b*Sinh(c+d*x))^(1/3), x, 1, (3*Cosh(c+d*x)*Hypergeometric2F1(1/2, 2/3, 5/3, -Sinh(c+d*x)^2)*(b*Sinh(c +
	// d*x))^(4/3))/(4*b*d*Sqrt(Cosh(c+d*x)^2))}
	public void test02831() {
		check("Integrate((b*Sinh(c+d*x))^(1/3), x)",
				"(3*Cosh(c+d*x)*Hypergeometric2F1(1/2, 2/3, 5/3, -Sinh(c+d*x)^2)*(b*Sinh(c+d*x))^(4/3))/(4*b*d*Sqrt(Cosh(c+d*x)^2))");

	}

	// {(b*Sinh(c+d*x))^(-1/3), x, 1, (3*Cosh(c+d*x)*Hypergeometric2F1(1/3, 1/2, 4/3, -Sinh(c+d*x)^2)*(b*Sinh(c +
	// d*x))^(2/3))/(2*b*d*Sqrt(Cosh(c+d*x)^2))}
	public void test02832() {
		check("Integrate((b*Sinh(c+d*x))^(-1/3), x)",
				"(3*Cosh(c+d*x)*Hypergeometric2F1(1/3, 1/2, 4/3, -Sinh(c+d*x)^2)*(b*Sinh(c+d*x))^(2/3))/(2*b*d*Sqrt(Cosh(c+d*x)^2))");

	}

	// {(b*Sinh(c+d*x))^(-2/3), x, 1, (3*Cosh(c+d*x)*Hypergeometric2F1(1/6, 1/2, 7/6, -Sinh(c+d*x)^2)*(b*Sinh(c +
	// d*x))^(1/3))/(b*d*Sqrt(Cosh(c+d*x)^2))}
	public void test02833() {
		check("Integrate((b*Sinh(c+d*x))^(-2/3), x)",
				"(3*Cosh(c+d*x)*Hypergeometric2F1(1/6, 1/2, 7/6, -Sinh(c+d*x)^2)*(b*Sinh(c+d*x))^(1/3))/(b*d*Sqrt(Cosh(c+d*x)^2))");

	}

	// {(b*Sinh(c+d*x))^(-4/3), x, 1, (-3*Cosh(c+d*x)*Hypergeometric2F1(-1/6, 1/2, 5/6, -Sinh(c +
	// d*x)^2))/(b*d*Sqrt(Cosh(c+d*x)^2)*(b*Sinh(c+d*x))^(1/3))}
	public void test02834() {
		check("Integrate((b*Sinh(c+d*x))^(-4/3), x)",
				"(-3*Cosh(c+d*x)*Hypergeometric2F1(-1/6, 1/2, 5/6, -Sinh(c+d*x)^2))/(b*d*Sqrt(Cosh(c+d*x)^2)*(b*Sinh(c+d*x))^(1/3))");

	}

	// {(b*Sinh(c+d*x))^n, x, 1, (Cosh(c+d*x)*Hypergeometric2F1(1/2, (1+n)/2, (3+n)/2, -Sinh(c +
	// d*x)^2)*(b*Sinh(c+d*x))^(1+n))/(b*d*(1+n)*Sqrt(Cosh(c+d*x)^2))}
	public void test02835() {
		check("Integrate((b*Sinh(c+d*x))^n, x)",
				"(Cosh(c+d*x)*Hypergeometric2F1(1/2, (1+n)/2, (3+n)/2, -Sinh(c+d*x)^2)*(b*Sinh(c+d*x))^(1+n))/(b*d*(1+n)*Sqrt(Cosh(c+d*x)^2))");

	}

	// {(I*Sinh(c+d*x))^n, x, 1, ((-I)*Cosh(c+d*x)*Hypergeometric2F1(1/2, (1+n)/2, (3+n)/2, -Sinh(c +
	// d*x)^2)*(I*Sinh(c+d*x))^(1+n))/(d*(1+n)*Sqrt(Cosh(c+d*x)^2))}
	public void test02836() {
		check("Integrate((I*Sinh(c+d*x))^n, x)",
				"((-I)*Cosh(c+d*x)*Hypergeometric2F1(1/2, (1+n)/2, (3+n)/2, -Sinh(c+d*x)^2)*(I*Sinh(c+d*x))^(1+n))/(d*(1+n)*Sqrt(Cosh(c+d*x)^2))");

	}

	// {((-I)*Sinh(c+d*x))^n, x, 1, (I*Cosh(c+d*x)*Hypergeometric2F1(1/2, (1+n)/2, (3+n)/2, -Sinh(c +
	// d*x)^2)*((-I)*Sinh(c+d*x))^(1+n))/(d*(1+n)*Sqrt(Cosh(c+d*x)^2))}
	public void test02837() {
		check("Integrate(((-I)*Sinh(c+d*x))^n, x)",
				"(I*Cosh(c+d*x)*Hypergeometric2F1(1/2, (1+n)/2, (3+n)/2, -Sinh(c+d*x)^2)*((-I)*Sinh(c+d*x))^(1+n))/(d*(1+n)*Sqrt(Cosh(c+d*x)^2))");

	}

	// {(1+I*Sinh(c+d*x))^(-1), x, 1, (I*Cosh(c+d*x))/(d*(1+I*Sinh(c+d*x)))}
	public void test02838() {
		check("Integrate((1+I*Sinh(c+d*x))^(-1), x)", "(I*Cosh(c+d*x))/(d*(1+I*Sinh(c+d*x)))");

	}

	// {(1-I*Sinh(c+d*x))^(-1), x, 1, ((-I)*Cosh(c+d*x))/(d*(1-I*Sinh(c+d*x)))}
	public void test02839() {
		check("Integrate((1-I*Sinh(c+d*x))^(-1), x)", "((-I)*Cosh(c+d*x))/(d*(1-I*Sinh(c+d*x)))");

	}

	// {Sqrt(a+I*a*Sinh(c+d*x)), x, 1, ((2*I)*a*Cosh(c+d*x))/(d*Sqrt(a+I*a*Sinh(c+d*x)))}
	public void test02840() {
		check("Integrate(Sqrt(a+I*a*Sinh(c+d*x)), x)", "((2*I)*a*Cosh(c+d*x))/(d*Sqrt(a+I*a*Sinh(c+d*x)))");

	}

	// {(5+(3*I)*Sinh(c+d*x))^(-1), x, 1, x/4-((I/2)*ArcTan(Cosh(c+d*x)/(3+I*Sinh(c+d*x))))/d}
	public void test02841() {
		check("Integrate((5+(3*I)*Sinh(c+d*x))^(-1), x)", "x/4-((I/2)*ArcTan(Cosh(c+d*x)/(3+I*Sinh(c+d*x))))/d");

	}

	// {(a+b*Sinh(c+d*x))^2, x, 1, ((2*a^2-b^2)*x)/2+(2*a*b*Cosh(c+d*x))/d+(b^2*Cosh(c+d*x)*Sinh(c +
	// d*x))/(2*d)}
	public void test02842() {
		check("Integrate((a+b*Sinh(c+d*x))^2, x)",
				"((2*a^2-b^2)*x)/2+(2*a*b*Cosh(c+d*x))/d+(b^2*Cosh(c+d*x)*Sinh(c+d*x))/(2*d)");

	}

	// {Cosh(x)^2/(1+I*Sinh(x))^3, x, 1, ((I/3)*Cosh(x)^3)/(1+I*Sinh(x))^3}
	public void test02843() {
		check("Integrate(Cosh(x)^2/(1+I*Sinh(x))^3, x)", "((I/3)*Cosh(x)^3)/(1+I*Sinh(x))^3");

	}

	// {Cosh(x)^2/(1-I*Sinh(x))^3, x, 1, ((-I/3)*Cosh(x)^3)/(1-I*Sinh(x))^3}
	public void test02844() {
		check("Integrate(Cosh(x)^2/(1-I*Sinh(x))^3, x)", "((-I/3)*Cosh(x)^3)/(1-I*Sinh(x))^3");

	}

	// {Csch(Sqrt(1-a*x)/Sqrt(1+a*x))/(1-a^2*x^2), x, 1, Unintegrable(Csch(Sqrt(1-a*x)/Sqrt(1+a*x))/((1 -
	// a*x)*(1+a*x)), x)}
	public void test02845() {
		check("Integrate(Csch(Sqrt(1-a*x)/Sqrt(1+a*x))/(1-a^2*x^2), x)",
				"Unintegrable(Csch(Sqrt(1-a*x)/Sqrt(1+a*x))/((1-a*x)*(1+a*x)), x)");

	}

	// {Csch(Sqrt(1-a*x)/Sqrt(1+a*x))^2/(1-a^2*x^2), x, 1, Unintegrable(Csch(Sqrt(1-a*x)/Sqrt(1+a*x))^2/((1 -
	// a*x)*(1+a*x)), x)}
	public void test02846() {
		check("Integrate(Csch(Sqrt(1-a*x)/Sqrt(1+a*x))^2/(1-a^2*x^2), x)",
				"Unintegrable(Csch(Sqrt(1-a*x)/Sqrt(1+a*x))^2/((1-a*x)*(1+a*x)), x)");

	}

	// {Sinh(a+b*Log(c*x^n)), x, 1, -((b*n*x*Cosh(a+b*Log(c*x^n)))/(1-b^2*n^2))+(x*Sinh(a+b*Log(c*x^n)))/(1 -
	// b^2*n^2)}
	public void test02847() {
		check("Integrate(Sinh(a+b*Log(c*x^n)), x)",
				"-((b*n*x*Cosh(a+b*Log(c*x^n)))/(1-b^2*n^2))+(x*Sinh(a+b*Log(c*x^n)))/(1-b^2*n^2)");

	}

	// {x^m*Sinh(a+b*Log(c*x^n)), x, 1, -((b*n*x^(1+m)*Cosh(a+b*Log(c*x^n)))/((1+m)^2-b^2*n^2))+((1 +
	// m)*x^(1+m)*Sinh(a+b*Log(c*x^n)))/((1+m)^2-b^2*n^2)}
	public void test02848() {
		check("Integrate(x^m*Sinh(a+b*Log(c*x^n)), x)",
				"-((b*n*x^(1+m)*Cosh(a+b*Log(c*x^n)))/((1+m)^2-b^2*n^2))+((1+m)*x^(1+m)*Sinh(a+b*Log(c*x^n)))/((1+m)^2-b^2*n^2)");

	}

	// {F^(c*(a+b*x))*Sinh(d+e*x), x, 1, (e*F^(c*(a+b*x))*Cosh(d+e*x))/(e^2-b^2*c^2*Log(F)^2)-(b*c*F^(c*(a +
	// b*x))*Log(F)*Sinh(d+e*x))/(e^2-b^2*c^2*Log(F)^2)}
	public void test02849() {
		check("Integrate(F^(c*(a+b*x))*Sinh(d+e*x), x)",
				"(e*F^(c*(a+b*x))*Cosh(d+e*x))/(e^2-b^2*c^2*Log(F)^2)-(b*c*F^(c*(a+b*x))*Log(F)*Sinh(d+e*x))/(e^2-b^2*c^2*Log(F)^2)");

	}

	// {F^(c*(a+b*x))*Csch(d+e*x), x, 1, (-2*E^(d+e*x)*F^(c*(a+b*x))*Hypergeometric2F1(1, (e +
	// b*c*Log(F))/(2*e), (3+(b*c*Log(F))/e)/2, E^(2*(d+e*x))))/(e+b*c*Log(F))}
	public void test02850() {
		check("Integrate(F^(c*(a+b*x))*Csch(d+e*x), x)",
				"(-2*E^(d+e*x)*F^(c*(a+b*x))*Hypergeometric2F1(1, (e+b*c*Log(F))/(2*e), (3+(b*c*Log(F))/e)/2, E^(2*(d+e*x))))/(e+b*c*Log(F))");

	}

	// {F^(c*(a+b*x))*Csch(d+e*x)^2, x, 1, (4*E^(2*(d+e*x))*F^(c*(a+b*x))*Hypergeometric2F1(2, 1 +
	// (b*c*Log(F))/(2*e), 2+(b*c*Log(F))/(2*e), E^(2*(d+e*x))))/(2*e+b*c*Log(F))}
	public void test02851() {
		check("Integrate(F^(c*(a+b*x))*Csch(d+e*x)^2, x)",
				"(4*E^(2*(d+e*x))*F^(c*(a+b*x))*Hypergeometric2F1(2, 1+(b*c*Log(F))/(2*e), 2+(b*c*Log(F))/(2*e), E^(2*(d+e*x))))/(2*e+b*c*Log(F))");

	}

	// {E^x*Sinh(a+b*x), x, 1, -((b*E^x*Cosh(a+b*x))/(1-b^2))+(E^x*Sinh(a+b*x))/(1-b^2)}
	public void test02852() {
		check("Integrate(E^x*Sinh(a+b*x), x)", "-((b*E^x*Cosh(a+b*x))/(1-b^2))+(E^x*Sinh(a+b*x))/(1-b^2)");

	}

	// {(a+b*Sinh(c+d*x)^2)^2, x, 1, ((8*a^2-8*a*b+3*b^2)*x)/8+((8*a-3*b)*b*Cosh(c+d*x)*Sinh(c +
	// d*x))/(8*d)+(b^2*Cosh(c+d*x)*Sinh(c+d*x)^3)/(4*d)}
	public void test02853() {
		check("Integrate((a+b*Sinh(c+d*x)^2)^2, x)",
				"((8*a^2-8*a*b+3*b^2)*x)/8+((8*a-3*b)*b*Cosh(c+d*x)*Sinh(c+d*x))/(8*d)+(b^2*Cosh(c+d*x)*Sinh(c+d*x)^3)/(4*d)");

	}

	// {Sqrt(1-Sinh(x)^2), x, 1, (-I)*EllipticE(I*x, -1)}
	public void test02854() {
		check("Integrate(Sqrt(1-Sinh(x)^2), x)", "(-I)*EllipticE(I*x, -1)");

	}

	// {1/Sqrt(1-Sinh(x)^2), x, 1, (-I)*EllipticF(I*x, -1)}
	public void test02855() {
		check("Integrate(1/Sqrt(1-Sinh(x)^2), x)", "(-I)*EllipticF(I*x, -1)");

	}

	// {Cosh(x)^(3/2)/x^3, x, 1, -Cosh(x)^(3/2)/(2*x^2)-(3*Sqrt(Cosh(x))*Sinh(x))/(4*x) -
	// (3*Unintegrable(1/(x*Sqrt(Cosh(x))), x))/8+(9*Unintegrable(Cosh(x)^(3/2)/x, x))/8}
	public void test02856() {
		check("Integrate(Cosh(x)^(3/2)/x^3, x)",
				"-Cosh(x)^(3/2)/(2*x^2)-(3*Sqrt(Cosh(x))*Sinh(x))/(4*x)-(3*Unintegrable(1/(x*Sqrt(Cosh(x))), x))/8+(9*Unintegrable(Cosh(x)^(3/2)/x, x))/8");

	}

	// {(e*x)^(-1+2*n)*(b*Cosh(c+d*x^n))^p, x, 1, ((e*x)^(2*n)*Unintegrable(x^(-1+2*n)*(b*Cosh(c+d*x^n))^p,
	// x))/(e*x^(2*n))}
	public void test02857() {
		check("Integrate((e*x)^(-1+2*n)*(b*Cosh(c+d*x^n))^p, x)",
				"((e*x)^(2*n)*Unintegrable(x^(-1+2*n)*(b*Cosh(c+d*x^n))^p, x))/(e*x^(2*n))");

	}

	// {(e*x)^(-1+2*n)*(a+b*Cosh(c+d*x^n))^p, x, 1, ((e*x)^(2*n)*Unintegrable(x^(-1+2*n)*(a+b*Cosh(c +
	// d*x^n))^p, x))/(e*x^(2*n))}
	public void test02858() {
		check("Integrate((e*x)^(-1+2*n)*(a+b*Cosh(c+d*x^n))^p, x)",
				"((e*x)^(2*n)*Unintegrable(x^(-1+2*n)*(a+b*Cosh(c+d*x^n))^p, x))/(e*x^(2*n))");

	}

	// {Cosh((a+b*x)^2)/x, x, 1, b*CannotIntegrate(Cosh((a+b*x)^2)/(b*x), x)}
	public void test02859() {
		check("Integrate(Cosh((a+b*x)^2)/x, x)", "b*CannotIntegrate(Cosh((a+b*x)^2)/(b*x), x)");

	}

	// {Cosh(a+b*x), x, 1, Sinh(a+b*x)/b}
	public void test02860() {
		check("Integrate(Cosh(a+b*x), x)", "Sinh(a+b*x)/b");

	}

	// {Sqrt(Cosh(a+b*x)), x, 1, ((-2*I)*EllipticE((I/2)*(a+b*x), 2))/b}
	public void test02861() {
		check("Integrate(Sqrt(Cosh(a+b*x)), x)", "((-2*I)*EllipticE((I/2)*(a+b*x), 2))/b");

	}

	// {1/Sqrt(Cosh(a+b*x)), x, 1, ((-2*I)*EllipticF((I/2)*(a+b*x), 2))/b}
	public void test02862() {
		check("Integrate(1/Sqrt(Cosh(a+b*x)), x)", "((-2*I)*EllipticF((I/2)*(a+b*x), 2))/b");

	}

	// {(b*Cosh(c+d*x))^n, x, 1, -(((b*Cosh(c+d*x))^(1+n)*Hypergeometric2F1(1/2, (1+n)/2, (3+n)/2, Cosh(c +
	// d*x)^2)*Sinh(c+d*x))/(b*d*(1+n)*Sqrt(-Sinh(c+d*x)^2)))}
	public void test02863() {
		check("Integrate((b*Cosh(c+d*x))^n, x)",
				"-(((b*Cosh(c+d*x))^(1+n)*Hypergeometric2F1(1/2, (1+n)/2, (3+n)/2, Cosh(c+d*x)^2)*Sinh(c+d*x))/(b*d*(1+n)*Sqrt(-Sinh(c+d*x)^2)))");

	}

	// {(1+Cosh(c+d*x))^(-1), x, 1, Sinh(c+d*x)/(d*(1+Cosh(c+d*x)))}
	public void test02864() {
		check("Integrate((1+Cosh(c+d*x))^(-1), x)", "Sinh(c+d*x)/(d*(1+Cosh(c+d*x)))");

	}

	// {(1-Cosh(c+d*x))^(-1), x, 1, -(Sinh(c+d*x)/(d*(1-Cosh(c+d*x))))}
	public void test02865() {
		check("Integrate((1-Cosh(c+d*x))^(-1), x)", "-(Sinh(c+d*x)/(d*(1-Cosh(c+d*x))))");

	}

	// {Sqrt(a+a*Cosh(c+d*x)), x, 1, (2*a*Sinh(c+d*x))/(d*Sqrt(a+a*Cosh(c+d*x)))}
	public void test02866() {
		check("Integrate(Sqrt(a+a*Cosh(c+d*x)), x)", "(2*a*Sinh(c+d*x))/(d*Sqrt(a+a*Cosh(c+d*x)))");

	}

	// {Sqrt(a-a*Cosh(c+d*x)), x, 1, (-2*a*Sinh(c+d*x))/(d*Sqrt(a-a*Cosh(c+d*x)))}
	public void test02867() {
		check("Integrate(Sqrt(a-a*Cosh(c+d*x)), x)", "(-2*a*Sinh(c+d*x))/(d*Sqrt(a-a*Cosh(c+d*x)))");

	}

	// {(a+b*Cosh(c+d*x))^2, x, 1, ((2*a^2+b^2)*x)/2+(2*a*b*Sinh(c+d*x))/d+(b^2*Cosh(c+d*x)*Sinh(c +
	// d*x))/(2*d)}
	public void test02868() {
		check("Integrate((a+b*Cosh(c+d*x))^2, x)",
				"((2*a^2+b^2)*x)/2+(2*a*b*Sinh(c+d*x))/d+(b^2*Cosh(c+d*x)*Sinh(c+d*x))/(2*d)");

	}

	// {(5+3*Cosh(c+d*x))^(-1), x, 1, x/4-ArcTanh(Sinh(c+d*x)/(3+Cosh(c+d*x)))/(2*d)}
	public void test02869() {
		check("Integrate((5+3*Cosh(c+d*x))^(-1), x)", "x/4-ArcTanh(Sinh(c+d*x)/(3+Cosh(c+d*x)))/(2*d)");

	}

	// {Sinh(x)^2/(1+Cosh(x))^3, x, 1, Sinh(x)^3/(3*(1+Cosh(x))^3)}
	public void test02870() {
		check("Integrate(Sinh(x)^2/(1+Cosh(x))^3, x)", "Sinh(x)^3/(3*(1+Cosh(x))^3)");

	}

	// {Sinh(x)^2/(1-Cosh(x))^3, x, 1, -Sinh(x)^3/(3*(1-Cosh(x))^3)}
	public void test02871() {
		check("Integrate(Sinh(x)^2/(1-Cosh(x))^3, x)", "-Sinh(x)^3/(3*(1-Cosh(x))^3)");

	}

	// {Sech(Sqrt(1-a*x)/Sqrt(1+a*x))/(1-a^2*x^2), x, 1, Unintegrable(Sech(Sqrt(1-a*x)/Sqrt(1+a*x))/((1 -
	// a*x)*(1+a*x)), x)}
	public void test02872() {
		check("Integrate(Sech(Sqrt(1-a*x)/Sqrt(1+a*x))/(1-a^2*x^2), x)",
				"Unintegrable(Sech(Sqrt(1-a*x)/Sqrt(1+a*x))/((1-a*x)*(1+a*x)), x)");

	}

	// {Sech(Sqrt(1-a*x)/Sqrt(1+a*x))^2/(1-a^2*x^2), x, 1, Unintegrable(Sech(Sqrt(1-a*x)/Sqrt(1+a*x))^2/((1 -
	// a*x)*(1+a*x)), x)}
	public void test02873() {
		check("Integrate(Sech(Sqrt(1-a*x)/Sqrt(1+a*x))^2/(1-a^2*x^2), x)",
				"Unintegrable(Sech(Sqrt(1-a*x)/Sqrt(1+a*x))^2/((1-a*x)*(1+a*x)), x)");

	}

	// {Cosh(a+b*Log(c*x^n)), x, 1, (x*Cosh(a+b*Log(c*x^n)))/(1-b^2*n^2)-(b*n*x*Sinh(a+b*Log(c*x^n)))/(1 -
	// b^2*n^2)}
	public void test02874() {
		check("Integrate(Cosh(a+b*Log(c*x^n)), x)",
				"(x*Cosh(a+b*Log(c*x^n)))/(1-b^2*n^2)-(b*n*x*Sinh(a+b*Log(c*x^n)))/(1-b^2*n^2)");

	}

	// {x^m*Cosh(a+b*Log(c*x^n)), x, 1, ((1+m)*x^(1+m)*Cosh(a+b*Log(c*x^n)))/((1+m)^2-b^2*n^2)-(b*n*x^(1 +
	// m)*Sinh(a+b*Log(c*x^n)))/((1+m)^2-b^2*n^2)}
	public void test02875() {
		check("Integrate(x^m*Cosh(a+b*Log(c*x^n)), x)",
				"((1+m)*x^(1+m)*Cosh(a+b*Log(c*x^n)))/((1+m)^2-b^2*n^2)-(b*n*x^(1+m)*Sinh(a+b*Log(c*x^n)))/((1+m)^2-b^2*n^2)");

	}

	// {F^(c*(a+b*x))*Cosh(d+e*x), x, 1, -((b*c*F^(c*(a+b*x))*Cosh(d+e*x)*Log(F))/(e^2-b^2*c^2*Log(F)^2)) +
	// (e*F^(c*(a+b*x))*Sinh(d+e*x))/(e^2-b^2*c^2*Log(F)^2)}
	public void test02876() {
		check("Integrate(F^(c*(a+b*x))*Cosh(d+e*x), x)",
				"-((b*c*F^(c*(a+b*x))*Cosh(d+e*x)*Log(F))/(e^2-b^2*c^2*Log(F)^2))+(e*F^(c*(a+b*x))*Sinh(d+e*x))/(e^2-b^2*c^2*Log(F)^2)");

	}

	// {F^(c*(a+b*x))*Sech(d+e*x), x, 1, (2*E^(d+e*x)*F^(c*(a+b*x))*Hypergeometric2F1(1, (e+b*c*Log(F))/(2*e),
	// (3+(b*c*Log(F))/e)/2, -E^(2*(d+e*x))))/(e+b*c*Log(F))}
	public void test02877() {
		check("Integrate(F^(c*(a+b*x))*Sech(d+e*x), x)",
				"(2*E^(d+e*x)*F^(c*(a+b*x))*Hypergeometric2F1(1, (e+b*c*Log(F))/(2*e), (3+(b*c*Log(F))/e)/2, -E^(2*(d+e*x))))/(e+b*c*Log(F))");

	}

	// {F^(c*(a+b*x))*Sech(d+e*x)^2, x, 1, (4*E^(2*(d+e*x))*F^(c*(a+b*x))*Hypergeometric2F1(2, 1 +
	// (b*c*Log(F))/(2*e), 2+(b*c*Log(F))/(2*e), -E^(2*(d+e*x))))/(2*e+b*c*Log(F))}
	public void test02878() {
		check("Integrate(F^(c*(a+b*x))*Sech(d+e*x)^2, x)",
				"(4*E^(2*(d+e*x))*F^(c*(a+b*x))*Hypergeometric2F1(2, 1+(b*c*Log(F))/(2*e), 2+(b*c*Log(F))/(2*e), -E^(2*(d+e*x))))/(2*e+b*c*Log(F))");

	}

	// {E^x*Cosh(a+b*x), x, 1, (E^x*Cosh(a+b*x))/(1-b^2)-(b*E^x*Sinh(a+b*x))/(1-b^2)}
	public void test02879() {
		check("Integrate(E^x*Cosh(a+b*x), x)", "(E^x*Cosh(a+b*x))/(1-b^2)-(b*E^x*Sinh(a+b*x))/(1-b^2)");

	}

	// {Sqrt(1+Cosh(x)^2), x, 1, (-I)*EllipticE(Pi/2+I*x, -1)}
	public void test02880() {
		check("Integrate(Sqrt(1+Cosh(x)^2), x)", "(-I)*EllipticE(Pi/2+I*x, -1)");

	}

	// {1/Sqrt(1+Cosh(x)^2), x, 1, (-I)*EllipticF(Pi/2+I*x, -1)}
	public void test02881() {
		check("Integrate(1/Sqrt(1+Cosh(x)^2), x)", "(-I)*EllipticF(Pi/2+I*x, -1)");

	}

	// {Tanh(a+b*x), x, 1, Log(Cosh(a+b*x))/b}
	public void test02882() {
		check("Integrate(Tanh(a+b*x), x)", "Log(Cosh(a+b*x))/b");

	}

	// {Coth(a+b*x), x, 1, Log(Sinh(a+b*x))/b}
	public void test02883() {
		check("Integrate(Coth(a+b*x), x)", "Log(Sinh(a+b*x))/b");

	}

	// {Sech(x)/(1+Tanh(x)), x, 1, -(Sech(x)/(1+Tanh(x)))}
	public void test02884() {
		check("Integrate(Sech(x)/(1+Tanh(x)), x)", "-(Sech(x)/(1+Tanh(x)))");

	}

	// {Csch(x)/(1+Coth(x)), x, 1, -(Csch(x)/(1+Coth(x)))}
	public void test02885() {
		check("Integrate(Csch(x)/(1+Coth(x)), x)", "-(Csch(x)/(1+Coth(x)))");

	}

	// {(e*x)^m*(a+b*Sech(c+d*x^n))^p, x, 1, ((e*x)^m*Unintegrable(x^m*(a+b*Sech(c+d*x^n))^p, x))/x^m}
	public void test02886() {
		check("Integrate((e*x)^m*(a+b*Sech(c+d*x^n))^p, x)",
				"((e*x)^m*Unintegrable(x^m*(a+b*Sech(c+d*x^n))^p, x))/x^m");

	}

	// {Sech(a+b*x), x, 1, ArcTan(Sinh(a+b*x))/b}
	public void test02887() {
		check("Integrate(Sech(a+b*x), x)", "ArcTan(Sinh(a+b*x))/b");

	}

	// {Sech(x)/(a+a*Sech(x)), x, 1, Tanh(x)/(a+a*Sech(x))}
	public void test02888() {
		check("Integrate(Sech(x)/(a+a*Sech(x)), x)", "Tanh(x)/(a+a*Sech(x))");

	}

	// {1/Sqrt(a+b*Sech(c+d*x)), x, 1, (2*Sqrt(a+b)*Coth(c+d*x)*EllipticPi((a+b)/a, ArcSin(Sqrt(a+b*Sech(c +
	// d*x))/Sqrt(a+b)), (a+b)/(a-b))*Sqrt((b*(1-Sech(c+d*x)))/(a+b))*Sqrt(-((b*(1+Sech(c+d*x)))/(a -
	// b))))/(a*d)}
	public void test02889() {
		check("Integrate(1/Sqrt(a+b*Sech(c+d*x)), x)",
				"(2*Sqrt(a+b)*Coth(c+d*x)*EllipticPi((a+b)/a, ArcSin(Sqrt(a+b*Sech(c+d*x))/Sqrt(a+b)), (a+b)/(a-b))*Sqrt((b*(1-Sech(c+d*x)))/(a+b))*Sqrt(-((b*(1+Sech(c+d*x)))/(a-b))))/(a*d)");

	}

	// {Sqrt(a+b*Sech(c+d*x)), x, 1, (2*Coth(c+d*x)*EllipticPi(a/(a+b), ArcSin(Sqrt(a+b)/Sqrt(a+b*Sech(c +
	// d*x))), (a-b)/(a+b))*Sqrt(-((b*(1-Sech(c+d*x)))/(a+b*Sech(c+d*x))))*Sqrt((b*(1+Sech(c+d*x)))/(a +
	// b*Sech(c+d*x)))*(a+b*Sech(c+d*x)))/(Sqrt(a+b)*d)}
	public void test02890() {
		check("Integrate(Sqrt(a+b*Sech(c+d*x)), x)",
				"(2*Coth(c+d*x)*EllipticPi(a/(a+b), ArcSin(Sqrt(a+b)/Sqrt(a+b*Sech(c+d*x))), (a-b)/(a+b))*Sqrt(-((b*(1-Sech(c+d*x)))/(a+b*Sech(c+d*x))))*Sqrt((b*(1+Sech(c+d*x)))/(a+b*Sech(c+d*x)))*(a+b*Sech(c+d*x)))/(Sqrt(a+b)*d)");

	}

	// {1/Sqrt(a+b*Sech(c+d*x)), x, 1, (2*Sqrt(a+b)*Coth(c+d*x)*EllipticPi((a+b)/a, ArcSin(Sqrt(a+b*Sech(c +
	// d*x))/Sqrt(a+b)), (a+b)/(a-b))*Sqrt((b*(1-Sech(c+d*x)))/(a+b))*Sqrt(-((b*(1+Sech(c+d*x)))/(a -
	// b))))/(a*d)}
	public void test02891() {
		check("Integrate(1/Sqrt(a+b*Sech(c+d*x)), x)",
				"(2*Sqrt(a+b)*Coth(c+d*x)*EllipticPi((a+b)/a, ArcSin(Sqrt(a+b*Sech(c+d*x))/Sqrt(a+b)), (a+b)/(a-b))*Sqrt((b*(1-Sech(c+d*x)))/(a+b))*Sqrt(-((b*(1+Sech(c+d*x)))/(a-b))))/(a*d)");

	}

	// {Cosh(c+d*x)/((e+f*x)*(a+b*Csch(c+d*x))), x, 1, Unintegrable((Cosh(c+d*x)*Sinh(c+d*x))/((e+f*x)*(b
	// +a*Sinh(c+d*x))), x)}
	public void test02892() {
		check("Integrate(Cosh(c+d*x)/((e+f*x)*(a+b*Csch(c+d*x))), x)",
				"Unintegrable((Cosh(c+d*x)*Sinh(c+d*x))/((e+f*x)*(b+a*Sinh(c+d*x))), x)");

	}

	// {(e*x)^m*(a+b*Csch(c+d*x^n))^p, x, 1, ((e*x)^m*Unintegrable(x^m*(a+b*Csch(c+d*x^n))^p, x))/x^m}
	public void test02893() {
		check("Integrate((e*x)^m*(a+b*Csch(c+d*x^n))^p, x)",
				"((e*x)^m*Unintegrable(x^m*(a+b*Csch(c+d*x^n))^p, x))/x^m");

	}

	// {Csch(a+b*x), x, 1, -(ArcTanh(Cosh(a+b*x))/b)}
	public void test02894() {
		check("Integrate(Csch(a+b*x), x)", "-(ArcTanh(Cosh(a+b*x))/b)");

	}

	// {Csch(x)/(I+Csch(x)), x, 1, (I*Coth(x))/(I+Csch(x))}
	public void test02895() {
		check("Integrate(Csch(x)/(I+Csch(x)), x)", "(I*Coth(x))/(I+Csch(x))");

	}

	// {Cosh(x)^(2/3)/Sinh(x)^(8/3), x, 1, (-3*Cosh(x)^(5/3))/(5*Sinh(x)^(5/3))}
	public void test02896() {
		check("Integrate(Cosh(x)^(2/3)/Sinh(x)^(8/3), x)", "(-3*Cosh(x)^(5/3))/(5*Sinh(x)^(5/3))");

	}

	// {Sinh(x)^(2/3)/Cosh(x)^(8/3), x, 1, (3*Sinh(x)^(5/3))/(5*Cosh(x)^(5/3))}
	public void test02897() {
		check("Integrate(Sinh(x)^(2/3)/Cosh(x)^(8/3), x)", "(3*Sinh(x)^(5/3))/(5*Cosh(x)^(5/3))");

	}

	// {Sinh(x)*Sinh(3*x), x, 1, -Sinh(2*x)/4+Sinh(4*x)/8}
	public void test02898() {
		check("Integrate(Sinh(x)*Sinh(3*x), x)", "-Sinh(2*x)/4+Sinh(4*x)/8");

	}

	// {Sinh(x)*Sinh(4*x), x, 1, -Sinh(3*x)/6+Sinh(5*x)/10}
	public void test02899() {
		check("Integrate(Sinh(x)*Sinh(4*x), x)", "-Sinh(3*x)/6+Sinh(5*x)/10");

	}

	// {Cosh(2*x)*Sinh(x), x, 1, -Cosh(x)/2+Cosh(3*x)/6}
	public void test02900() {
		check("Integrate(Cosh(2*x)*Sinh(x), x)", "-Cosh(x)/2+Cosh(3*x)/6");

	}

	// {Cosh(3*x)*Sinh(x), x, 1, -Cosh(2*x)/4+Cosh(4*x)/8}
	public void test02901() {
		check("Integrate(Cosh(3*x)*Sinh(x), x)", "-Cosh(2*x)/4+Cosh(4*x)/8");

	}

	// {Cosh(4*x)*Sinh(x), x, 1, -Cosh(3*x)/6+Cosh(5*x)/10}
	public void test02902() {
		check("Integrate(Cosh(4*x)*Sinh(x), x)", "-Cosh(3*x)/6+Cosh(5*x)/10");

	}

	// {Cosh(x)*Sinh(3*x), x, 1, Cosh(2*x)/4+Cosh(4*x)/8}
	public void test02903() {
		check("Integrate(Cosh(x)*Sinh(3*x), x)", "Cosh(2*x)/4+Cosh(4*x)/8");

	}

	// {Cosh(x)*Sinh(4*x), x, 1, Cosh(3*x)/6+Cosh(5*x)/10}
	public void test02904() {
		check("Integrate(Cosh(x)*Sinh(4*x), x)", "Cosh(3*x)/6+Cosh(5*x)/10");

	}

	// {Cosh(x)*Cosh(2*x), x, 1, Sinh(x)/2+Sinh(3*x)/6}
	public void test02905() {
		check("Integrate(Cosh(x)*Cosh(2*x), x)", "Sinh(x)/2+Sinh(3*x)/6");

	}

	// {Cosh(x)*Cosh(3*x), x, 1, Sinh(2*x)/4+Sinh(4*x)/8}
	public void test02906() {
		check("Integrate(Cosh(x)*Cosh(3*x), x)", "Sinh(2*x)/4+Sinh(4*x)/8");

	}

	// {Cosh(x)*Cosh(4*x), x, 1, Sinh(3*x)/6+Sinh(5*x)/10}
	public void test02907() {
		check("Integrate(Cosh(x)*Cosh(4*x), x)", "Sinh(3*x)/6+Sinh(5*x)/10");

	}

	// {Tanh(a+b*x), x, 1, Log(Cosh(a+b*x))/b}
	public void test02908() {
		check("Integrate(Tanh(a+b*x), x)", "Log(Cosh(a+b*x))/b");

	}

	// {x^m*Sech(a+b*x)*Tanh(a+b*x)^2, x, 1, Unintegrable(x^m*Sech(a+b*x), x)-Unintegrable(x^m*Sech(a+b*x)^3,
	// x)}
	public void test02909() {
		check("Integrate(x^m*Sech(a+b*x)*Tanh(a+b*x)^2, x)",
				"Unintegrable(x^m*Sech(a+b*x), x)-Unintegrable(x^m*Sech(a+b*x)^3, x)");

	}

	// {(Sech(a+b*x)*Tanh(a+b*x)^2)/x, x, 1, Unintegrable(Sech(a+b*x)/x, x)-Unintegrable(Sech(a+b*x)^3/x, x)}
	public void test02910() {
		check("Integrate((Sech(a+b*x)*Tanh(a+b*x)^2)/x, x)",
				"Unintegrable(Sech(a+b*x)/x, x)-Unintegrable(Sech(a+b*x)^3/x, x)");

	}

	// {(Sech(a+b*x)*Tanh(a+b*x)^2)/x^2, x, 1, Unintegrable(Sech(a+b*x)/x^2, x)-Unintegrable(Sech(a +
	// b*x)^3/x^2, x)}
	public void test02911() {
		check("Integrate((Sech(a+b*x)*Tanh(a+b*x)^2)/x^2, x)",
				"Unintegrable(Sech(a+b*x)/x^2, x)-Unintegrable(Sech(a+b*x)^3/x^2, x)");

	}

	// {Coth(a+b*x), x, 1, Log(Sinh(a+b*x))/b}
	public void test02912() {
		check("Integrate(Coth(a+b*x), x)", "Log(Sinh(a+b*x))/b");

	}

	// {x^m*Coth(a+b*x)^2*Csch(a+b*x), x, 1, Unintegrable(x^m*Csch(a+b*x), x)+Unintegrable(x^m*Csch(a+b*x)^3,
	// x)}
	public void test02913() {
		check("Integrate(x^m*Coth(a+b*x)^2*Csch(a+b*x), x)",
				"Unintegrable(x^m*Csch(a+b*x), x)+Unintegrable(x^m*Csch(a+b*x)^3, x)");

	}

	// {(Coth(a+b*x)^2*Csch(a+b*x))/x, x, 1, Unintegrable(Csch(a+b*x)/x, x)+Unintegrable(Csch(a+b*x)^3/x, x)}
	public void test02914() {
		check("Integrate((Coth(a+b*x)^2*Csch(a+b*x))/x, x)",
				"Unintegrable(Csch(a+b*x)/x, x)+Unintegrable(Csch(a+b*x)^3/x, x)");

	}

	// {(Coth(a+b*x)^2*Csch(a+b*x))/x^2, x, 1, Unintegrable(Csch(a+b*x)/x^2, x)+Unintegrable(Csch(a +
	// b*x)^3/x^2, x)}
	public void test02915() {
		check("Integrate((Coth(a+b*x)^2*Csch(a+b*x))/x^2, x)",
				"Unintegrable(Csch(a+b*x)/x^2, x)+Unintegrable(Csch(a+b*x)^3/x^2, x)");

	}

	// {(Csch(a+b*x)*Sech(a+b*x))/x, x, 1, 2*Unintegrable(Csch(2*a+2*b*x)/x, x)}
	public void test02916() {
		check("Integrate((Csch(a+b*x)*Sech(a+b*x))/x, x)", "2*Unintegrable(Csch(2*a+2*b*x)/x, x)");

	}

	// {(Csch(a+b*x)*Sech(a+b*x))/x^2, x, 1, 2*Unintegrable(Csch(2*a+2*b*x)/x^2, x)}
	public void test02917() {
		check("Integrate((Csch(a+b*x)*Sech(a+b*x))/x^2, x)", "2*Unintegrable(Csch(2*a+2*b*x)/x^2, x)");

	}

	// {(Csch(a+b*x)^2*Sech(a+b*x)^2)/x, x, 1, 4*Unintegrable(Csch(2*a+2*b*x)^2/x, x)}
	public void test02918() {
		check("Integrate((Csch(a+b*x)^2*Sech(a+b*x)^2)/x, x)", "4*Unintegrable(Csch(2*a+2*b*x)^2/x, x)");

	}

	// {(Csch(a+b*x)^2*Sech(a+b*x)^2)/x^2, x, 1, 4*Unintegrable(Csch(2*a+2*b*x)^2/x^2, x)}
	public void test02919() {
		check("Integrate((Csch(a+b*x)^2*Sech(a+b*x)^2)/x^2, x)", "4*Unintegrable(Csch(2*a+2*b*x)^2/x^2, x)");

	}

	// {(Csch(a+b*x)^3*Sech(a+b*x)^3)/x, x, 1, 8*Unintegrable(Csch(2*a+2*b*x)^3/x, x)}
	public void test02920() {
		check("Integrate((Csch(a+b*x)^3*Sech(a+b*x)^3)/x, x)", "8*Unintegrable(Csch(2*a+2*b*x)^3/x, x)");

	}

	// {(Csch(a+b*x)^3*Sech(a+b*x)^3)/x^2, x, 1, 8*Unintegrable(Csch(2*a+2*b*x)^3/x^2, x)}
	public void test02921() {
		check("Integrate((Csch(a+b*x)^3*Sech(a+b*x)^3)/x^2, x)", "8*Unintegrable(Csch(2*a+2*b*x)^3/x^2, x)");

	}

	// {(a*Cosh(x)+b*Sinh(x))^(-2), x, 1, Sinh(x)/(a*(a*Cosh(x)+b*Sinh(x)))}
	public void test02922() {
		check("Integrate((a*Cosh(x)+b*Sinh(x))^(-2), x)", "Sinh(x)/(a*(a*Cosh(x)+b*Sinh(x)))");

	}

	// {(a*Cosh(c+d*x)+a*Sinh(c+d*x))^2, x, 1, (a*Cosh(c+d*x)+a*Sinh(c+d*x))^2/(2*d)}
	public void test02923() {
		check("Integrate((a*Cosh(c+d*x)+a*Sinh(c+d*x))^2, x)", "(a*Cosh(c+d*x)+a*Sinh(c+d*x))^2/(2*d)");

	}

	// {(a*Cosh(c+d*x)+a*Sinh(c+d*x))^3, x, 1, (a*Cosh(c+d*x)+a*Sinh(c+d*x))^3/(3*d)}
	public void test02924() {
		check("Integrate((a*Cosh(c+d*x)+a*Sinh(c+d*x))^3, x)", "(a*Cosh(c+d*x)+a*Sinh(c+d*x))^3/(3*d)");

	}

	// {(a*Cosh(c+d*x)+a*Sinh(c+d*x))^n, x, 1, (a*Cosh(c+d*x)+a*Sinh(c+d*x))^n/(d*n)}
	public void test02925() {
		check("Integrate((a*Cosh(c+d*x)+a*Sinh(c+d*x))^n, x)", "(a*Cosh(c+d*x)+a*Sinh(c+d*x))^n/(d*n)");

	}

	// {(a*Cosh(c+d*x)+a*Sinh(c+d*x))^(-1), x, 1, -(1/(d*(a*Cosh(c+d*x)+a*Sinh(c+d*x))))}
	public void test02926() {
		check("Integrate((a*Cosh(c+d*x)+a*Sinh(c+d*x))^(-1), x)", "-(1/(d*(a*Cosh(c+d*x)+a*Sinh(c+d*x))))");

	}

	// {(a*Cosh(c+d*x)+a*Sinh(c+d*x))^(-2), x, 1, -1/(2*d*(a*Cosh(c+d*x)+a*Sinh(c+d*x))^2)}
	public void test02927() {
		check("Integrate((a*Cosh(c+d*x)+a*Sinh(c+d*x))^(-2), x)", "-1/(2*d*(a*Cosh(c+d*x)+a*Sinh(c+d*x))^2)");

	}

	// {(a*Cosh(c+d*x)+a*Sinh(c+d*x))^(-3), x, 1, -1/(3*d*(a*Cosh(c+d*x)+a*Sinh(c+d*x))^3)}
	public void test02928() {
		check("Integrate((a*Cosh(c+d*x)+a*Sinh(c+d*x))^(-3), x)", "-1/(3*d*(a*Cosh(c+d*x)+a*Sinh(c+d*x))^3)");

	}

	// {Sqrt(a*Cosh(c+d*x)+a*Sinh(c+d*x)), x, 1, (2*Sqrt(a*Cosh(c+d*x)+a*Sinh(c+d*x)))/d}
	public void test02929() {
		check("Integrate(Sqrt(a*Cosh(c+d*x)+a*Sinh(c+d*x)), x)", "(2*Sqrt(a*Cosh(c+d*x)+a*Sinh(c+d*x)))/d");

	}

	// {1/Sqrt(a*Cosh(c+d*x)+a*Sinh(c+d*x)), x, 1, -2/(d*Sqrt(a*Cosh(c+d*x)+a*Sinh(c+d*x)))}
	public void test02930() {
		check("Integrate(1/Sqrt(a*Cosh(c+d*x)+a*Sinh(c+d*x)), x)", "-2/(d*Sqrt(a*Cosh(c+d*x)+a*Sinh(c+d*x)))");

	}

	// {(a*Cosh(c+d*x)-a*Sinh(c+d*x))^2, x, 1, -(a*Cosh(c+d*x)-a*Sinh(c+d*x))^2/(2*d)}
	public void test02931() {
		check("Integrate((a*Cosh(c+d*x)-a*Sinh(c+d*x))^2, x)", "-(a*Cosh(c+d*x)-a*Sinh(c+d*x))^2/(2*d)");

	}

	// {(a*Cosh(c+d*x)-a*Sinh(c+d*x))^3, x, 1, -(a*Cosh(c+d*x)-a*Sinh(c+d*x))^3/(3*d)}
	public void test02932() {
		check("Integrate((a*Cosh(c+d*x)-a*Sinh(c+d*x))^3, x)", "-(a*Cosh(c+d*x)-a*Sinh(c+d*x))^3/(3*d)");

	}

	// {(a*Cosh(c+d*x)-a*Sinh(c+d*x))^n, x, 1, -((a*Cosh(c+d*x)-a*Sinh(c+d*x))^n/(d*n))}
	public void test02933() {
		check("Integrate((a*Cosh(c+d*x)-a*Sinh(c+d*x))^n, x)", "-((a*Cosh(c+d*x)-a*Sinh(c+d*x))^n/(d*n))");

	}

	// {(a*Cosh(c+d*x)-a*Sinh(c+d*x))^(-1), x, 1, 1/(d*(a*Cosh(c+d*x)-a*Sinh(c+d*x)))}
	public void test02934() {
		check("Integrate((a*Cosh(c+d*x)-a*Sinh(c+d*x))^(-1), x)", "1/(d*(a*Cosh(c+d*x)-a*Sinh(c+d*x)))");

	}

	// {(a*Cosh(c+d*x)-a*Sinh(c+d*x))^(-2), x, 1, 1/(2*d*(a*Cosh(c+d*x)-a*Sinh(c+d*x))^2)}
	public void test02935() {
		check("Integrate((a*Cosh(c+d*x)-a*Sinh(c+d*x))^(-2), x)", "1/(2*d*(a*Cosh(c+d*x)-a*Sinh(c+d*x))^2)");

	}

	// {(a*Cosh(c+d*x)-a*Sinh(c+d*x))^(-3), x, 1, 1/(3*d*(a*Cosh(c+d*x)-a*Sinh(c+d*x))^3)}
	public void test02936() {
		check("Integrate((a*Cosh(c+d*x)-a*Sinh(c+d*x))^(-3), x)", "1/(3*d*(a*Cosh(c+d*x)-a*Sinh(c+d*x))^3)");

	}

	// {Sqrt(a*Cosh(c+d*x)-a*Sinh(c+d*x)), x, 1, (-2*Sqrt(a*Cosh(c+d*x)-a*Sinh(c+d*x)))/d}
	public void test02937() {
		check("Integrate(Sqrt(a*Cosh(c+d*x)-a*Sinh(c+d*x)), x)", "(-2*Sqrt(a*Cosh(c+d*x)-a*Sinh(c+d*x)))/d");

	}

	// {1/Sqrt(a*Cosh(c+d*x)-a*Sinh(c+d*x)), x, 1, 2/(d*Sqrt(a*Cosh(c+d*x)-a*Sinh(c+d*x)))}
	public void test02938() {
		check("Integrate(1/Sqrt(a*Cosh(c+d*x)-a*Sinh(c+d*x)), x)", "2/(d*Sqrt(a*Cosh(c+d*x)-a*Sinh(c+d*x)))");

	}

	// {(Cosh(x)+Sinh(x))/(Cosh(x)-Sinh(x)), x, 1, (Cosh(x)+Sinh(x))^2/2}
	public void test02939() {
		check("Integrate((Cosh(x)+Sinh(x))/(Cosh(x)-Sinh(x)), x)", "(Cosh(x)+Sinh(x))^2/2");

	}

	// {(Cosh(x)-Sinh(x))/(Cosh(x)+Sinh(x)), x, 1, -1/(2*(Cosh(x)+Sinh(x))^2)}
	public void test02940() {
		check("Integrate((Cosh(x)-Sinh(x))/(Cosh(x)+Sinh(x)), x)", "-1/(2*(Cosh(x)+Sinh(x))^2)");

	}

	// {(Cosh(x)-I*Sinh(x))/(Cosh(x)+I*Sinh(x)), x, 1, (-I)*Log(Cosh(x)+I*Sinh(x))}
	public void test02941() {
		check("Integrate((Cosh(x)-I*Sinh(x))/(Cosh(x)+I*Sinh(x)), x)", "(-I)*Log(Cosh(x)+I*Sinh(x))");

	}

	// {(B*Cosh(x)+C*Sinh(x))/(b*Cosh(x)+c*Sinh(x)), x, 1, ((b*B-c*C)*x)/(b^2-c^2)-((B*c-b*C)*Log(b*Cosh(x)
	// +c*Sinh(x)))/(b^2-c^2)}
	public void test02942() {
		check("Integrate((B*Cosh(x)+C*Sinh(x))/(b*Cosh(x)+c*Sinh(x)), x)",
				"((b*B-c*C)*x)/(b^2-c^2)-((B*c-b*C)*Log(b*Cosh(x)+c*Sinh(x)))/(b^2-c^2)");

	}

	// {(Sqrt(b^2-c^2)+b*Cosh(x)+c*Sinh(x))^(-1), x, 1, -((c+Sqrt(b^2-c^2)*Sinh(x))/(c*(c*Cosh(x) +
	// b*Sinh(x))))}
	public void test02943() {
		check("Integrate((Sqrt(b^2-c^2)+b*Cosh(x)+c*Sinh(x))^(-1), x)",
				"-((c+Sqrt(b^2-c^2)*Sinh(x))/(c*(c*Cosh(x)+b*Sinh(x))))");

	}

	// {Sqrt(Sqrt(b^2-c^2)+b*Cosh(x)+c*Sinh(x)), x, 1, (2*(c*Cosh(x)+b*Sinh(x)))/Sqrt(Sqrt(b^2-c^2) +
	// b*Cosh(x)+c*Sinh(x))}
	public void test02944() {
		check("Integrate(Sqrt(Sqrt(b^2-c^2)+b*Cosh(x)+c*Sinh(x)), x)",
				"(2*(c*Cosh(x)+b*Sinh(x)))/Sqrt(Sqrt(b^2-c^2)+b*Cosh(x)+c*Sinh(x))");

	}

	// {Sqrt(-Sqrt(b^2-c^2)+b*Cosh(x)+c*Sinh(x)), x, 1, (2*(c*Cosh(x)+b*Sinh(x)))/Sqrt(-Sqrt(b^2-c^2) +
	// b*Cosh(x)+c*Sinh(x))}
	public void test02945() {
		check("Integrate(Sqrt(-Sqrt(b^2-c^2)+b*Cosh(x)+c*Sinh(x)), x)",
				"(2*(c*Cosh(x)+b*Sinh(x)))/Sqrt(-Sqrt(b^2-c^2)+b*Cosh(x)+c*Sinh(x))");

	}

	// {Sinh(x)/(1+Cosh(x)+Sinh(x)), x, 1, x/2+Cosh(x)/2-Sinh(x)/2}
	public void test02946() {
		check("Integrate(Sinh(x)/(1+Cosh(x)+Sinh(x)), x)", "x/2+Cosh(x)/2-Sinh(x)/2");

	}

	// {(b^2-c^2+a*b*Cosh(x)+a*c*Sinh(x))/(a+b*Cosh(x)+c*Sinh(x))^2, x, 1, (c*Cosh(x)+b*Sinh(x))/(a +
	// b*Cosh(x)+c*Sinh(x))}
	public void test02947() {
		check("Integrate((b^2-c^2+a*b*Cosh(x)+a*c*Sinh(x))/(a+b*Cosh(x)+c*Sinh(x))^2, x)",
				"(c*Cosh(x)+b*Sinh(x))/(a+b*Cosh(x)+c*Sinh(x))");

	}

	// {(A+C*Sinh(x))/(a+b*Cosh(x)+b*Sinh(x)), x, 1, ((2*a*A+b*C)*x)/(2*a^2)+(C*Cosh(x))/(2*a)-(((2*A)/a -
	// C/b+(b*C)/a^2)*Log(a+b*Cosh(x)+b*Sinh(x)))/2-(C*Sinh(x))/(2*a)}
	public void test02948() {
		check("Integrate((A+C*Sinh(x))/(a+b*Cosh(x)+b*Sinh(x)), x)",
				"((2*a*A+b*C)*x)/(2*a^2)+(C*Cosh(x))/(2*a)-(((2*A)/a-C/b+(b*C)/a^2)*Log(a+b*Cosh(x)+b*Sinh(x)))/2-(C*Sinh(x))/(2*a)");

	}

	// {(A+B*Cosh(x))/(a+b*Cosh(x)+b*Sinh(x)), x, 1, ((2*a*A-b*B)*x)/(2*a^2)-(B*Cosh(x))/(2*a)-((2*a*A*b -
	// a^2*B-b^2*B)*Log(a+b*Cosh(x)+b*Sinh(x)))/(2*a^2*b)+(B*Sinh(x))/(2*a)}
	public void test02949() {
		check("Integrate((A+B*Cosh(x))/(a+b*Cosh(x)+b*Sinh(x)), x)",
				"((2*a*A-b*B)*x)/(2*a^2)-(B*Cosh(x))/(2*a)-((2*a*A*b-a^2*B-b^2*B)*Log(a+b*Cosh(x)+b*Sinh(x)))/(2*a^2*b)+(B*Sinh(x))/(2*a)");

	}

	// {(A+B*Cosh(x)+C*Sinh(x))/(a+b*Cosh(x)+b*Sinh(x)), x, 1, ((2*a*A-b*(B-C))*x)/(2*a^2)-((2*a*A*b -
	// b^2*(B-C)-a^2*(B+C))*Log(a+b*Cosh(x)+b*Sinh(x)))/(2*a^2*b)-((B-C)*(Cosh(x)-Sinh(x)))/(2*a)}
	public void test02950() {
		check("Integrate((A+B*Cosh(x)+C*Sinh(x))/(a+b*Cosh(x)+b*Sinh(x)), x)",
				"((2*a*A-b*(B-C))*x)/(2*a^2)-((2*a*A*b-b^2*(B-C)-a^2*(B+C))*Log(a+b*Cosh(x)+b*Sinh(x)))/(2*a^2*b)-((B-C)*(Cosh(x)-Sinh(x)))/(2*a)");

	}

	// {(A+C*Sinh(x))/(a+b*Cosh(x)-b*Sinh(x)), x, 1, ((2*a*A-b*C)*x)/(2*a^2)+(C*Cosh(x))/(2*a)+((2*a*A*b +
	// a^2*C-b^2*C)*Log(a+b*Cosh(x)-b*Sinh(x)))/(2*a^2*b)+(C*Sinh(x))/(2*a)}
	public void test02951() {
		check("Integrate((A+C*Sinh(x))/(a+b*Cosh(x)-b*Sinh(x)), x)",
				"((2*a*A-b*C)*x)/(2*a^2)+(C*Cosh(x))/(2*a)+((2*a*A*b+a^2*C-b^2*C)*Log(a+b*Cosh(x)-b*Sinh(x)))/(2*a^2*b)+(C*Sinh(x))/(2*a)");

	}

	// {(A+B*Cosh(x))/(a+b*Cosh(x)-b*Sinh(x)), x, 1, ((2*a*A-b*B)*x)/(2*a^2)+(B*Cosh(x))/(2*a)+((2*a*A*b -
	// a^2*B-b^2*B)*Log(a+b*Cosh(x)-b*Sinh(x)))/(2*a^2*b)+(B*Sinh(x))/(2*a)}
	public void test02952() {
		check("Integrate((A+B*Cosh(x))/(a+b*Cosh(x)-b*Sinh(x)), x)",
				"((2*a*A-b*B)*x)/(2*a^2)+(B*Cosh(x))/(2*a)+((2*a*A*b-a^2*B-b^2*B)*Log(a+b*Cosh(x)-b*Sinh(x)))/(2*a^2*b)+(B*Sinh(x))/(2*a)");

	}

	// {(A+B*Cosh(x)+C*Sinh(x))/(a+b*Cosh(x)-b*Sinh(x)), x, 1, ((2*a*A-b*(B+C))*x)/(2*a^2)+((2*a*A*b -
	// a^2*(B-C)-b^2*(B+C))*Log(a+b*Cosh(x)-b*Sinh(x)))/(2*a^2*b)+((B+C)*(Cosh(x)+Sinh(x)))/(2*a)}
	public void test02953() {
		check("Integrate((A+B*Cosh(x)+C*Sinh(x))/(a+b*Cosh(x)-b*Sinh(x)), x)",
				"((2*a*A-b*(B+C))*x)/(2*a^2)+((2*a*A*b-a^2*(B-C)-b^2*(B+C))*Log(a+b*Cosh(x)-b*Sinh(x)))/(2*a^2*b)+((B+C)*(Cosh(x)+Sinh(x)))/(2*a)");

	}

	// {1/(x*(a+b*Cosh(x)*Sinh(x))), x, 1, Unintegrable(1/(x*(a+(b*Sinh(2*x))/2)), x)}
	public void test02954() {
		check("Integrate(1/(x*(a+b*Cosh(x)*Sinh(x))), x)", "Unintegrable(1/(x*(a+(b*Sinh(2*x))/2)), x)");

	}

	// {E^(a+b*x)*Sinh(c+d*x), x, 1, -((d*E^(a+b*x)*Cosh(c+d*x))/(b^2-d^2))+(b*E^(a+b*x)*Sinh(c +
	// d*x))/(b^2-d^2)}
	public void test02955() {
		check("Integrate(E^(a+b*x)*Sinh(c+d*x), x)",
				"-((d*E^(a+b*x)*Cosh(c+d*x))/(b^2-d^2))+(b*E^(a+b*x)*Sinh(c+d*x))/(b^2-d^2)");

	}

	// {E^(a+b*x)*Csch(c+d*x), x, 1, (-2*E^(a+c+b*x+d*x)*Hypergeometric2F1(1, (b+d)/(2*d), (3+b/d)/2,
	// E^(2*(c+d*x))))/(b+d)}
	public void test02956() {
		check("Integrate(E^(a+b*x)*Csch(c+d*x), x)",
				"(-2*E^(a+c+b*x+d*x)*Hypergeometric2F1(1, (b+d)/(2*d), (3+b/d)/2, E^(2*(c+d*x))))/(b+d)");

	}

	// {E^(c+d*x)*Csch(a+b*x)^2, x, 1, (4*E^(c+d*x+2*(a+b*x))*Hypergeometric2F1(2, 1+d/(2*b), 2+d/(2*b),
	// E^(2*(a+b*x))))/(2*b+d)}
	public void test02957() {
		check("Integrate(E^(c+d*x)*Csch(a+b*x)^2, x)",
				"(4*E^(c+d*x+2*(a+b*x))*Hypergeometric2F1(2, 1+d/(2*b), 2+d/(2*b), E^(2*(a+b*x))))/(2*b+d)");

	}

	// {E^(a+b*x)*Cosh(c+d*x), x, 1, (b*E^(a+b*x)*Cosh(c+d*x))/(b^2-d^2)-(d*E^(a+b*x)*Sinh(c+d*x))/(b^2
	// -d^2)}
	public void test02958() {
		check("Integrate(E^(a+b*x)*Cosh(c+d*x), x)",
				"(b*E^(a+b*x)*Cosh(c+d*x))/(b^2-d^2)-(d*E^(a+b*x)*Sinh(c+d*x))/(b^2-d^2)");

	}

	// {E^(a+b*x)*Sech(c+d*x), x, 1, (2*E^(a+c+b*x+d*x)*Hypergeometric2F1(1, (b+d)/(2*d), (3+b/d)/2,
	// -E^(2*(c+d*x))))/(b+d)}
	public void test02959() {
		check("Integrate(E^(a+b*x)*Sech(c+d*x), x)",
				"(2*E^(a+c+b*x+d*x)*Hypergeometric2F1(1, (b+d)/(2*d), (3+b/d)/2, -E^(2*(c+d*x))))/(b+d)");

	}

	// {E^(a+b*x)*Sech(c+d*x)^2, x, 1, (4*E^(a+b*x+2*(c+d*x))*Hypergeometric2F1(2, 1+b/(2*d), 2+b/(2*d),
	// -E^(2*(c+d*x))))/(b+2*d)}
	public void test02960() {
		check("Integrate(E^(a+b*x)*Sech(c+d*x)^2, x)",
				"(4*E^(a+b*x+2*(c+d*x))*Hypergeometric2F1(2, 1+b/(2*d), 2+b/(2*d), -E^(2*(c+d*x))))/(b+2*d)");

	}

	// {E^(c+d*x)*Cosh(a+b*x), x, 1, -((d*E^(c+d*x)*Cosh(a+b*x))/(b^2-d^2))+(b*E^(c+d*x)*Sinh(a +
	// b*x))/(b^2-d^2)}
	public void test02961() {
		check("Integrate(E^(c+d*x)*Cosh(a+b*x), x)",
				"-((d*E^(c+d*x)*Cosh(a+b*x))/(b^2-d^2))+(b*E^(c+d*x)*Sinh(a+b*x))/(b^2-d^2)");

	}

	// {Csch(x)*Log(Tanh(x))*Sech(x), x, 1, Log(Tanh(x))^2/2}
	public void test02962() {
		check("Integrate(Csch(x)*Log(Tanh(x))*Sech(x), x)", "Log(Tanh(x))^2/2");

	}

	// {Csch(2*x)*Log(Tanh(x)), x, 1, Log(Tanh(x))^2/4}
	public void test02963() {
		check("Integrate(Csch(2*x)*Log(Tanh(x)), x)", "Log(Tanh(x))^2/4");

	}

	// {Cosh(a+b*x)*F(c, d, Sinh(a+b*x), r, s), x, 1, CannotIntegrate(Cosh(a+b*x)*F(c, d, Sinh(a+b*x), r, s),
	// x)}
	public void test02964() {
		check("Integrate(Cosh(a+b*x)*F(c, d, Sinh(a+b*x), r, s), x)",
				"CannotIntegrate(Cosh(a+b*x)*F(c, d, Sinh(a+b*x), r, s), x)");

	}

	// {F(c, d, Cosh(a+b*x), r, s)*Sinh(a+b*x), x, 1, CannotIntegrate(F(c, d, Cosh(a+b*x), r, s)*Sinh(a+b*x),
	// x)}
	public void test02965() {
		check("Integrate(F(c, d, Cosh(a+b*x), r, s)*Sinh(a+b*x), x)",
				"CannotIntegrate(F(c, d, Cosh(a+b*x), r, s)*Sinh(a+b*x), x)");

	}

	// {F(c, d, Tanh(a+b*x), r, s)*Sech(a+b*x)^2, x, 1, CannotIntegrate(F(c, d, Tanh(a+b*x), r, s)*Sech(a +
	// b*x)^2, x)}
	public void test02966() {
		check("Integrate(F(c, d, Tanh(a+b*x), r, s)*Sech(a+b*x)^2, x)",
				"CannotIntegrate(F(c, d, Tanh(a+b*x), r, s)*Sech(a+b*x)^2, x)");

	}

	// {Csch(a+b*x)^2*F(c, d, Coth(a+b*x), r, s), x, 1, CannotIntegrate(Csch(a+b*x)^2*F(c, d, Coth(a+b*x), r,
	// s), x)}
	public void test02967() {
		check("Integrate(Csch(a+b*x)^2*F(c, d, Coth(a+b*x), r, s), x)",
				"CannotIntegrate(Csch(a+b*x)^2*F(c, d, Coth(a+b*x), r, s), x)");

	}

	// {(Cosh(Sqrt(x))*Sinh(Sqrt(x)))/Sqrt(x), x, 1, Sinh(Sqrt(x))^2}
	public void test02968() {
		check("Integrate((Cosh(Sqrt(x))*Sinh(Sqrt(x)))/Sqrt(x), x)", "Sinh(Sqrt(x))^2");

	}

	// {(Cosh(a+b*x)-Sinh(a+b*x))/(Cosh(a+b*x)+Sinh(a+b*x)), x, 1, -1/(2*b*(Cosh(a+b*x)+Sinh(a +
	// b*x))^2)}
	public void test02969() {
		check("Integrate((Cosh(a+b*x)-Sinh(a+b*x))/(Cosh(a+b*x)+Sinh(a+b*x)), x)",
				"-1/(2*b*(Cosh(a+b*x)+Sinh(a+b*x))^2)");

	}

	// {x^m*ArcSinh(a*x)^4, x, 1, (x^(1+m)*ArcSinh(a*x)^4)/(1+m)-(4*a*Unintegrable((x^(1 +
	// m)*ArcSinh(a*x)^3)/Sqrt(1+a^2*x^2), x))/(1+m)}
	public void test02970() {
		check("Integrate(x^m*ArcSinh(a*x)^4, x)",
				"(x^(1+m)*ArcSinh(a*x)^4)/(1+m)-(4*a*Unintegrable((x^(1+m)*ArcSinh(a*x)^3)/Sqrt(1+a^2*x^2), x))/(1+m)");

	}

	// {x^m*ArcSinh(a*x)^3, x, 1, (x^(1+m)*ArcSinh(a*x)^3)/(1+m)-(3*a*Unintegrable((x^(1 +
	// m)*ArcSinh(a*x)^2)/Sqrt(1+a^2*x^2), x))/(1+m)}
	public void test02971() {
		check("Integrate(x^m*ArcSinh(a*x)^3, x)",
				"(x^(1+m)*ArcSinh(a*x)^3)/(1+m)-(3*a*Unintegrable((x^(1+m)*ArcSinh(a*x)^2)/Sqrt(1+a^2*x^2), x))/(1+m)");

	}

	// {ArcSinh(a*x)/Sqrt(1+a^2*x^2), x, 1, ArcSinh(a*x)^2/(2*a)}
	public void test02972() {
		check("Integrate(ArcSinh(a*x)/Sqrt(1+a^2*x^2), x)", "ArcSinh(a*x)^2/(2*a)");

	}

	// {(x^m*ArcSinh(a*x))/Sqrt(1+a^2*x^2), x, 1, (x^(1+m)*ArcSinh(a*x)*Hypergeometric2F1(1/2, (1+m)/2, (3+m)/2,
	// -(a^2*x^2)))/(1+m)-(a*x^(2+m)*HypergeometricPFQ({1, 1+m/2, 1+m/2}, {3/2+m/2, 2+m/2},
	// -(a^2*x^2)))/(2+3*m+m^2)}
	public void test02973() {
		check("Integrate((x^m*ArcSinh(a*x))/Sqrt(1+a^2*x^2), x)",
				"(x^(1+m)*ArcSinh(a*x)*Hypergeometric2F1(1/2, (1+m)/2, (3+m)/2, -(a^2*x^2)))/(1+m)-(a*x^(2+m)*HypergeometricPFQ({1, 1+m/2, 1+m/2}, {3/2+m/2, 2+m/2}, -(a^2*x^2)))/(2+3*m+m^2)");

	}

	// {ArcSinh(a*x)^2/Sqrt(1+a^2*x^2), x, 1, ArcSinh(a*x)^3/(3*a)}
	public void test02974() {
		check("Integrate(ArcSinh(a*x)^2/Sqrt(1+a^2*x^2), x)", "ArcSinh(a*x)^3/(3*a)");

	}

	// {ArcSinh(a*x)^3/Sqrt(1+a^2*x^2), x, 1, ArcSinh(a*x)^4/(4*a)}
	public void test02975() {
		check("Integrate(ArcSinh(a*x)^3/Sqrt(1+a^2*x^2), x)", "ArcSinh(a*x)^4/(4*a)");

	}

	// {1/(Sqrt(1+a^2*x^2)*ArcSinh(a*x)), x, 1, Log(ArcSinh(a*x))/a}
	public void test02976() {
		check("Integrate(1/(Sqrt(1+a^2*x^2)*ArcSinh(a*x)), x)", "Log(ArcSinh(a*x))/a");

	}

	// {1/(Sqrt(1+c^2*x^2)*(a+b*ArcSinh(c*x))), x, 1, Log(a+b*ArcSinh(c*x))/(b*c)}
	public void test02977() {
		check("Integrate(1/(Sqrt(1+c^2*x^2)*(a+b*ArcSinh(c*x))), x)", "Log(a+b*ArcSinh(c*x))/(b*c)");

	}

	// {1/((c+a^2*c*x^2)*ArcSinh(a*x)^2), x, 1, -(1/(a*c*Sqrt(1+a^2*x^2)*ArcSinh(a*x)))-(a*Unintegrable(x/((1 +
	// a^2*x^2)^(3/2)*ArcSinh(a*x)), x))/c}
	public void test02978() {
		check("Integrate(1/((c+a^2*c*x^2)*ArcSinh(a*x)^2), x)",
				"-(1/(a*c*Sqrt(1+a^2*x^2)*ArcSinh(a*x)))-(a*Unintegrable(x/((1+a^2*x^2)^(3/2)*ArcSinh(a*x)), x))/c");

	}

	// {1/((c+a^2*c*x^2)^2*ArcSinh(a*x)^2), x, 1, -(1/(a*c^2*(1+a^2*x^2)^(3/2)*ArcSinh(a*x))) -
	// (3*a*Unintegrable(x/((1+a^2*x^2)^(5/2)*ArcSinh(a*x)), x))/c^2}
	public void test02979() {
		check("Integrate(1/((c+a^2*c*x^2)^2*ArcSinh(a*x)^2), x)",
				"-(1/(a*c^2*(1+a^2*x^2)^(3/2)*ArcSinh(a*x)))-(3*a*Unintegrable(x/((1+a^2*x^2)^(5/2)*ArcSinh(a*x)), x))/c^2");

	}

	// {Sqrt(1+c^2*x^2)/(x^2*(a+b*ArcSinh(c*x))^2), x, 1, -((1+c^2*x^2)/(b*c*x^2*(a+b*ArcSinh(c*x)))) -
	// (2*Unintegrable(1/(x^3*(a+b*ArcSinh(c*x))), x))/(b*c)}
	public void test02980() {
		check("Integrate(Sqrt(1+c^2*x^2)/(x^2*(a+b*ArcSinh(c*x))^2), x)",
				"-((1+c^2*x^2)/(b*c*x^2*(a+b*ArcSinh(c*x))))-(2*Unintegrable(1/(x^3*(a+b*ArcSinh(c*x))), x))/(b*c)");

	}

	// {(1+c^2*x^2)^(3/2)/(x^2*(a+b*ArcSinh(c*x))^2), x, 1, -((1+c^2*x^2)^2/(b*c*x^2*(a+b*ArcSinh(c*x)))) -
	// (2*Unintegrable((1+c^2*x^2)/(x^3*(a+b*ArcSinh(c*x))), x))/(b*c)+(2*c*Unintegrable((1+c^2*x^2)/(x*(a +
	// b*ArcSinh(c*x))), x))/b}
	public void test02981() {
		check("Integrate((1+c^2*x^2)^(3/2)/(x^2*(a+b*ArcSinh(c*x))^2), x)",
				"-((1+c^2*x^2)^2/(b*c*x^2*(a+b*ArcSinh(c*x))))-(2*Unintegrable((1+c^2*x^2)/(x^3*(a+b*ArcSinh(c*x))), x))/(b*c)+(2*c*Unintegrable((1+c^2*x^2)/(x*(a+b*ArcSinh(c*x))), x))/b");

	}

	// {(1+c^2*x^2)^(3/2)/(x^4*(a+b*ArcSinh(c*x))^2), x, 1, -((1+c^2*x^2)^2/(b*c*x^4*(a+b*ArcSinh(c*x)))) -
	// (4*Unintegrable((1+c^2*x^2)/(x^5*(a+b*ArcSinh(c*x))), x))/(b*c)}
	public void test02982() {
		check("Integrate((1+c^2*x^2)^(3/2)/(x^4*(a+b*ArcSinh(c*x))^2), x)",
				"-((1+c^2*x^2)^2/(b*c*x^4*(a+b*ArcSinh(c*x))))-(4*Unintegrable((1+c^2*x^2)/(x^5*(a+b*ArcSinh(c*x))), x))/(b*c)");

	}

	// {(1+c^2*x^2)^(5/2)/(x^2*(a+b*ArcSinh(c*x))^2), x, 1, -((1+c^2*x^2)^3/(b*c*x^2*(a+b*ArcSinh(c*x)))) -
	// (2*Unintegrable((1+c^2*x^2)^2/(x^3*(a+b*ArcSinh(c*x))), x))/(b*c)+(4*c*Unintegrable((1+c^2*x^2)^2/(x*(a +
	// b*ArcSinh(c*x))), x))/b}
	public void test02983() {
		check("Integrate((1+c^2*x^2)^(5/2)/(x^2*(a+b*ArcSinh(c*x))^2), x)",
				"-((1+c^2*x^2)^3/(b*c*x^2*(a+b*ArcSinh(c*x))))-(2*Unintegrable((1+c^2*x^2)^2/(x^3*(a+b*ArcSinh(c*x))), x))/(b*c)+(4*c*Unintegrable((1+c^2*x^2)^2/(x*(a+b*ArcSinh(c*x))), x))/b");

	}

	// {x^m/(Sqrt(1+c^2*x^2)*(a+b*ArcSinh(c*x))^2), x, 1, -(x^m/(b*c*(a+b*ArcSinh(c*x))))+(m*Unintegrable(x^(-1
	// +m)/(a+b*ArcSinh(c*x)), x))/(b*c)}
	public void test02984() {
		check("Integrate(x^m/(Sqrt(1+c^2*x^2)*(a+b*ArcSinh(c*x))^2), x)",
				"-(x^m/(b*c*(a+b*ArcSinh(c*x))))+(m*Unintegrable(x^(-1+m)/(a+b*ArcSinh(c*x)), x))/(b*c)");

	}

	// {1/(Sqrt(1+c^2*x^2)*(a+b*ArcSinh(c*x))^2), x, 1, -(1/(b*c*(a+b*ArcSinh(c*x))))}
	public void test02985() {
		check("Integrate(1/(Sqrt(1+c^2*x^2)*(a+b*ArcSinh(c*x))^2), x)", "-(1/(b*c*(a+b*ArcSinh(c*x))))");

	}

	// {1/(x*Sqrt(1+c^2*x^2)*(a+b*ArcSinh(c*x))^2), x, 1, -(1/(b*c*x*(a+b*ArcSinh(c*x))))-Unintegrable(1/(x^2*(a
	// +b*ArcSinh(c*x))), x)/(b*c)}
	public void test02986() {
		check("Integrate(1/(x*Sqrt(1+c^2*x^2)*(a+b*ArcSinh(c*x))^2), x)",
				"-(1/(b*c*x*(a+b*ArcSinh(c*x))))-Unintegrable(1/(x^2*(a+b*ArcSinh(c*x))), x)/(b*c)");

	}

	// {1/(x^2*Sqrt(1+c^2*x^2)*(a+b*ArcSinh(c*x))^2), x, 1, -(1/(b*c*x^2*(a+b*ArcSinh(c*x)))) -
	// (2*Unintegrable(1/(x^3*(a+b*ArcSinh(c*x))), x))/(b*c)}
	public void test02987() {
		check("Integrate(1/(x^2*Sqrt(1+c^2*x^2)*(a+b*ArcSinh(c*x))^2), x)",
				"-(1/(b*c*x^2*(a+b*ArcSinh(c*x))))-(2*Unintegrable(1/(x^3*(a+b*ArcSinh(c*x))), x))/(b*c)");

	}

	// {x^2/((1+c^2*x^2)^(3/2)*(a+b*ArcSinh(c*x))^2), x, 1, -(x^2/(b*c*(1+c^2*x^2)*(a+b*ArcSinh(c*x)))) +
	// (2*Unintegrable(x/((1+c^2*x^2)^2*(a+b*ArcSinh(c*x))), x))/(b*c)}
	public void test02988() {
		check("Integrate(x^2/((1+c^2*x^2)^(3/2)*(a+b*ArcSinh(c*x))^2), x)",
				"-(x^2/(b*c*(1+c^2*x^2)*(a+b*ArcSinh(c*x))))+(2*Unintegrable(x/((1+c^2*x^2)^2*(a+b*ArcSinh(c*x))), x))/(b*c)");

	}

	// {1/((1+c^2*x^2)^(3/2)*(a+b*ArcSinh(c*x))^2), x, 1, -(1/(b*c*(1+c^2*x^2)*(a+b*ArcSinh(c*x)))) -
	// (2*c*Unintegrable(x/((1+c^2*x^2)^2*(a+b*ArcSinh(c*x))), x))/b}
	public void test02989() {
		check("Integrate(1/((1+c^2*x^2)^(3/2)*(a+b*ArcSinh(c*x))^2), x)",
				"-(1/(b*c*(1+c^2*x^2)*(a+b*ArcSinh(c*x))))-(2*c*Unintegrable(x/((1+c^2*x^2)^2*(a+b*ArcSinh(c*x))), x))/b");

	}

	// {1/((1+c^2*x^2)^(5/2)*(a+b*ArcSinh(c*x))^2), x, 1, -(1/(b*c*(1+c^2*x^2)^2*(a+b*ArcSinh(c*x)))) -
	// (4*c*Unintegrable(x/((1+c^2*x^2)^3*(a+b*ArcSinh(c*x))), x))/b}
	public void test02990() {
		check("Integrate(1/((1+c^2*x^2)^(5/2)*(a+b*ArcSinh(c*x))^2), x)",
				"-(1/(b*c*(1+c^2*x^2)^2*(a+b*ArcSinh(c*x))))-(4*c*Unintegrable(x/((1+c^2*x^2)^3*(a+b*ArcSinh(c*x))), x))/b");

	}

	// {1/(Sqrt(1+a^2*x^2)*ArcSinh(a*x)^3), x, 1, -1/(2*a*ArcSinh(a*x)^2)}
	public void test02991() {
		check("Integrate(1/(Sqrt(1+a^2*x^2)*ArcSinh(a*x)^3), x)", "-1/(2*a*ArcSinh(a*x)^2)");

	}

	// {Sqrt(ArcSinh(a*x))/(c+a^2*c*x^2)^(3/2), x, 1, (x*Sqrt(ArcSinh(a*x)))/(c*Sqrt(c+a^2*c*x^2))-(a*Sqrt(1 +
	// a^2*x^2)*Unintegrable(x/((1+a^2*x^2)*Sqrt(ArcSinh(a*x))), x))/(2*c*Sqrt(c+a^2*c*x^2))}
	public void test02992() {
		check("Integrate(Sqrt(ArcSinh(a*x))/(c+a^2*c*x^2)^(3/2), x)",
				"(x*Sqrt(ArcSinh(a*x)))/(c*Sqrt(c+a^2*c*x^2))-(a*Sqrt(1+a^2*x^2)*Unintegrable(x/((1+a^2*x^2)*Sqrt(ArcSinh(a*x))), x))/(2*c*Sqrt(c+a^2*c*x^2))");

	}

	// {ArcSinh(a*x)^(3/2)/(c+a^2*c*x^2)^(3/2), x, 1, (x*ArcSinh(a*x)^(3/2))/(c*Sqrt(c+a^2*c*x^2))-(3*a*Sqrt(1 +
	// a^2*x^2)*Unintegrable((x*Sqrt(ArcSinh(a*x)))/(1+a^2*x^2), x))/(2*c*Sqrt(c+a^2*c*x^2))}
	public void test02993() {
		check("Integrate(ArcSinh(a*x)^(3/2)/(c+a^2*c*x^2)^(3/2), x)",
				"(x*ArcSinh(a*x)^(3/2))/(c*Sqrt(c+a^2*c*x^2))-(3*a*Sqrt(1+a^2*x^2)*Unintegrable((x*Sqrt(ArcSinh(a*x)))/(1+a^2*x^2), x))/(2*c*Sqrt(c+a^2*c*x^2))");

	}

	// {ArcSinh(a*x)^(5/2)/(c+a^2*c*x^2)^(3/2), x, 1, (x*ArcSinh(a*x)^(5/2))/(c*Sqrt(c+a^2*c*x^2))-(5*a*Sqrt(1 +
	// a^2*x^2)*Unintegrable((x*ArcSinh(a*x)^(3/2))/(1+a^2*x^2), x))/(2*c*Sqrt(c+a^2*c*x^2))}
	public void test02994() {
		check("Integrate(ArcSinh(a*x)^(5/2)/(c+a^2*c*x^2)^(3/2), x)",
				"(x*ArcSinh(a*x)^(5/2))/(c*Sqrt(c+a^2*c*x^2))-(5*a*Sqrt(1+a^2*x^2)*Unintegrable((x*ArcSinh(a*x)^(3/2))/(1+a^2*x^2), x))/(2*c*Sqrt(c+a^2*c*x^2))");

	}

	// {Sqrt(ArcSinh(x/a))/(a^2+x^2)^(3/2), x, 1, (x*Sqrt(ArcSinh(x/a)))/(a^2*Sqrt(a^2+x^2))-(Sqrt(1 +
	// x^2/a^2)*Unintegrable(x/((1+x^2/a^2)*Sqrt(ArcSinh(x/a))), x))/(2*a^3*Sqrt(a^2+x^2))}
	public void test02995() {
		check("Integrate(Sqrt(ArcSinh(x/a))/(a^2+x^2)^(3/2), x)",
				"(x*Sqrt(ArcSinh(x/a)))/(a^2*Sqrt(a^2+x^2))-(Sqrt(1+x^2/a^2)*Unintegrable(x/((1+x^2/a^2)*Sqrt(ArcSinh(x/a))), x))/(2*a^3*Sqrt(a^2+x^2))");

	}

	// {ArcSinh(x/a)^(3/2)/(a^2+x^2)^(3/2), x, 1, (x*ArcSinh(x/a)^(3/2))/(a^2*Sqrt(a^2+x^2))-(3*Sqrt(1 +
	// x^2/a^2)*Unintegrable((x*Sqrt(ArcSinh(x/a)))/(1+x^2/a^2), x))/(2*a^3*Sqrt(a^2+x^2))}
	public void test02996() {
		check("Integrate(ArcSinh(x/a)^(3/2)/(a^2+x^2)^(3/2), x)",
				"(x*ArcSinh(x/a)^(3/2))/(a^2*Sqrt(a^2+x^2))-(3*Sqrt(1+x^2/a^2)*Unintegrable((x*Sqrt(ArcSinh(x/a)))/(1+x^2/a^2), x))/(2*a^3*Sqrt(a^2+x^2))");

	}

	// {1/((c+a^2*c*x^2)^(3/2)*ArcSinh(a*x)^(3/2)), x, 1, (-2*Sqrt(1+a^2*x^2))/(a*(c +
	// a^2*c*x^2)^(3/2)*Sqrt(ArcSinh(a*x)))-(4*a*Sqrt(1+a^2*x^2)*Unintegrable(x/((1 +
	// a^2*x^2)^2*Sqrt(ArcSinh(a*x))), x))/(c*Sqrt(c+a^2*c*x^2))}
	public void test02997() {
		check("Integrate(1/((c+a^2*c*x^2)^(3/2)*ArcSinh(a*x)^(3/2)), x)",
				"(-2*Sqrt(1+a^2*x^2))/(a*(c+a^2*c*x^2)^(3/2)*Sqrt(ArcSinh(a*x)))-(4*a*Sqrt(1+a^2*x^2)*Unintegrable(x/((1+a^2*x^2)^2*Sqrt(ArcSinh(a*x))), x))/(c*Sqrt(c+a^2*c*x^2))");

	}

	// {1/((c+a^2*c*x^2)^(5/2)*ArcSinh(a*x)^(3/2)), x, 1, (-2*Sqrt(1+a^2*x^2))/(a*(c +
	// a^2*c*x^2)^(5/2)*Sqrt(ArcSinh(a*x)))-(8*a*Sqrt(1+a^2*x^2)*Unintegrable(x/((1 +
	// a^2*x^2)^3*Sqrt(ArcSinh(a*x))), x))/(c^2*Sqrt(c+a^2*c*x^2))}
	public void test02998() {
		check("Integrate(1/((c+a^2*c*x^2)^(5/2)*ArcSinh(a*x)^(3/2)), x)",
				"(-2*Sqrt(1+a^2*x^2))/(a*(c+a^2*c*x^2)^(5/2)*Sqrt(ArcSinh(a*x)))-(8*a*Sqrt(1+a^2*x^2)*Unintegrable(x/((1+a^2*x^2)^3*Sqrt(ArcSinh(a*x))), x))/(c^2*Sqrt(c+a^2*c*x^2))");

	}

	// {1/((c+a^2*c*x^2)^(3/2)*ArcSinh(a*x)^(5/2)), x, 1, (-2*Sqrt(1+a^2*x^2))/(3*a*(c +
	// a^2*c*x^2)^(3/2)*ArcSinh(a*x)^(3/2))-(4*a*Sqrt(1+a^2*x^2)*Unintegrable(x/((1 +
	// a^2*x^2)^2*ArcSinh(a*x)^(3/2)), x))/(3*c*Sqrt(c+a^2*c*x^2))}
	public void test02999() {
		check("Integrate(1/((c+a^2*c*x^2)^(3/2)*ArcSinh(a*x)^(5/2)), x)",
				"(-2*Sqrt(1+a^2*x^2))/(3*a*(c+a^2*c*x^2)^(3/2)*ArcSinh(a*x)^(3/2))-(4*a*Sqrt(1+a^2*x^2)*Unintegrable(x/((1+a^2*x^2)^2*ArcSinh(a*x)^(3/2)), x))/(3*c*Sqrt(c+a^2*c*x^2))");

	}

	// {1/((c+a^2*c*x^2)^(5/2)*ArcSinh(a*x)^(5/2)), x, 1, (-2*Sqrt(1+a^2*x^2))/(3*a*(c +
	// a^2*c*x^2)^(5/2)*ArcSinh(a*x)^(3/2))-(8*a*Sqrt(1+a^2*x^2)*Unintegrable(x/((1 +
	// a^2*x^2)^3*ArcSinh(a*x)^(3/2)), x))/(3*c^2*Sqrt(c+a^2*c*x^2))}
	public void test03000() {
		check("Integrate(1/((c+a^2*c*x^2)^(5/2)*ArcSinh(a*x)^(5/2)), x)",
				"(-2*Sqrt(1+a^2*x^2))/(3*a*(c+a^2*c*x^2)^(5/2)*ArcSinh(a*x)^(3/2))-(8*a*Sqrt(1+a^2*x^2)*Unintegrable(x/((1+a^2*x^2)^3*ArcSinh(a*x)^(3/2)), x))/(3*c^2*Sqrt(c+a^2*c*x^2))");

	}

	// {ArcSinh(a*x)^n/Sqrt(1+a^2*x^2), x, 1, ArcSinh(a*x)^(1+n)/(a*(1+n))}
	public void test03001() {
		check("Integrate(ArcSinh(a*x)^n/Sqrt(1+a^2*x^2), x)", "ArcSinh(a*x)^(1+n)/(a*(1+n))");

	}

	// {(d+e*x)^m*(a+b*ArcSinh(c*x))^2, x, 1, ((d+e*x)^(1+m)*(a+b*ArcSinh(c*x))^2)/(e*(1+m)) -
	// (2*b*c*Unintegrable(((d+e*x)^(1+m)*(a+b*ArcSinh(c*x)))/Sqrt(1+c^2*x^2), x))/(e*(1+m))}
	public void test03002() {
		check("Integrate((d+e*x)^m*(a+b*ArcSinh(c*x))^2, x)",
				"((d+e*x)^(1+m)*(a+b*ArcSinh(c*x))^2)/(e*(1+m))-(2*b*c*Unintegrable(((d+e*x)^(1+m)*(a+b*ArcSinh(c*x)))/Sqrt(1+c^2*x^2), x))/(e*(1+m))");

	}

	// {1/(x*ArcSinh(a+b*x)), x, 1, Unintegrable(1/(x*ArcSinh(a+b*x)), x)}
	public void test03003() {
		check("Integrate(1/(x*ArcSinh(a+b*x)), x)", "Unintegrable(1/(x*ArcSinh(a+b*x)), x)");

	}

	// {1/(x*ArcSinh(a+b*x)^2), x, 1, Unintegrable(1/(x*ArcSinh(a+b*x)^2), x)}
	public void test03004() {
		check("Integrate(1/(x*ArcSinh(a+b*x)^2), x)", "Unintegrable(1/(x*ArcSinh(a+b*x)^2), x)");

	}

	// {1/(x*ArcSinh(a+b*x)^3), x, 1, Unintegrable(1/(x*ArcSinh(a+b*x)^3), x)}
	public void test03005() {
		check("Integrate(1/(x*ArcSinh(a+b*x)^3), x)", "Unintegrable(1/(x*ArcSinh(a+b*x)^3), x)");

	}

	// {x^m*(a+b*ArcSinh(c+d*x))^n, x, 1, Unintegrable(x^m*(a+b*ArcSinh(c+d*x))^n, x)}
	public void test03006() {
		check("Integrate(x^m*(a+b*ArcSinh(c+d*x))^n, x)", "Unintegrable(x^m*(a+b*ArcSinh(c+d*x))^n, x)");

	}

	// {(a+b*ArcSinh(c+d*x))^n/x, x, 1, Unintegrable((a+b*ArcSinh(c+d*x))^n/x, x)}
	public void test03007() {
		check("Integrate((a+b*ArcSinh(c+d*x))^n/x, x)", "Unintegrable((a+b*ArcSinh(c+d*x))^n/x, x)");

	}

	// {(c*e+d*e*x)^m/(a+b*ArcSinh(c+d*x)), x, 1, Unintegrable((e*(c+d*x))^m/(a+b*ArcSinh(c+d*x)), x)}
	public void test03008() {
		check("Integrate((c*e+d*e*x)^m/(a+b*ArcSinh(c+d*x)), x)",
				"Unintegrable((e*(c+d*x))^m/(a+b*ArcSinh(c+d*x)), x)");

	}

	// {1/((1+a^2+2*a*b*x+b^2*x^2)^(3/2)*ArcSinh(a+b*x)), x, 1, Unintegrable(1/((1+(a +
	// b*x)^2)^(3/2)*ArcSinh(a+b*x)), x)}
	public void test03009() {
		check("Integrate(1/((1+a^2+2*a*b*x+b^2*x^2)^(3/2)*ArcSinh(a+b*x)), x)",
				"Unintegrable(1/((1+(a+b*x)^2)^(3/2)*ArcSinh(a+b*x)), x)");

	}

	// {(a+I*b*ArcSin(1-I*d*x^2))^(-1), x, 1, (x*CosIntegral(((-I/2)*(a+I*b*ArcSin(1 -
	// I*d*x^2)))/b)*(I*Cosh(a/(2*b))-Sinh(a/(2*b))))/(2*b*(Cos(ArcSin(1-I*d*x^2)/2)-Sin(ArcSin(1-I*d*x^2)/2)))
	// -(x*(I*Cosh(a/(2*b))+Sinh(a/(2*b)))*SinIntegral(((I/2)*a)/b-ArcSin(1-I*d*x^2)/2))/(2*b*(Cos(ArcSin(1 -
	// I*d*x^2)/2)-Sin(ArcSin(1-I*d*x^2)/2)))}
	public void test03010() {
		check("Integrate((a+I*b*ArcSin(1-I*d*x^2))^(-1), x)",
				"(x*CosIntegral(((-I/2)*(a+I*b*ArcSin(1-I*d*x^2)))/b)*(I*Cosh(a/(2*b))-Sinh(a/(2*b))))/(2*b*(Cos(ArcSin(1-I*d*x^2)/2)-Sin(ArcSin(1-I*d*x^2)/2)))-(x*(I*Cosh(a/(2*b))+Sinh(a/(2*b)))*SinIntegral(((I/2)*a)/b-ArcSin(1-I*d*x^2)/2))/(2*b*(Cos(ArcSin(1-I*d*x^2)/2)-Sin(ArcSin(1-I*d*x^2)/2)))");

	}

	// {(a+I*b*ArcSin(1-I*d*x^2))^(-2), x, 1, -Sqrt((2*I)*d*x^2+d^2*x^4)/(2*b*d*x*(a+I*b*ArcSin(1-I*d*x^2))) +
	// (x*CosIntegral(((-I/2)*(a+I*b*ArcSin(1-I*d*x^2)))/b)*(Cosh(a/(2*b))-I*Sinh(a/(2*b))))/(4*b^2*(Cos(ArcSin(1
	// -I*d*x^2)/2)-Sin(ArcSin(1-I*d*x^2)/2)))+(x*(Cosh(a/(2*b))+I*Sinh(a/(2*b)))*SinIntegral(((I/2)*a)/b -
	// ArcSin(1-I*d*x^2)/2))/(4*b^2*(Cos(ArcSin(1-I*d*x^2)/2)-Sin(ArcSin(1-I*d*x^2)/2)))}
	public void test03011() {
		check("Integrate((a+I*b*ArcSin(1-I*d*x^2))^(-2), x)",
				"-Sqrt((2*I)*d*x^2+d^2*x^4)/(2*b*d*x*(a+I*b*ArcSin(1-I*d*x^2)))+(x*CosIntegral(((-I/2)*(a+I*b*ArcSin(1-I*d*x^2)))/b)*(Cosh(a/(2*b))-I*Sinh(a/(2*b))))/(4*b^2*(Cos(ArcSin(1-I*d*x^2)/2)-Sin(ArcSin(1-I*d*x^2)/2)))+(x*(Cosh(a/(2*b))+I*Sinh(a/(2*b)))*SinIntegral(((I/2)*a)/b-ArcSin(1-I*d*x^2)/2))/(4*b^2*(Cos(ArcSin(1-I*d*x^2)/2)-Sin(ArcSin(1-I*d*x^2)/2)))");

	}

	// {(a-I*b*ArcSin(1+I*d*x^2))^(-1), x, 1, -(x*CosIntegral(((I/2)*(a-I*b*ArcSin(1 +
	// I*d*x^2)))/b)*(I*Cosh(a/(2*b))+Sinh(a/(2*b))))/(2*b*(Cos(ArcSin(1+I*d*x^2)/2)-Sin(ArcSin(1+I*d*x^2)/2)))
	// +(x*(Cosh(a/(2*b))+I*Sinh(a/(2*b)))*SinhIntegral((a-I*b*ArcSin(1+I*d*x^2))/(2*b)))/(2*b*(Cos(ArcSin(1 +
	// I*d*x^2)/2)-Sin(ArcSin(1+I*d*x^2)/2)))}
	public void test03012() {
		check("Integrate((a-I*b*ArcSin(1+I*d*x^2))^(-1), x)",
				"-(x*CosIntegral(((I/2)*(a-I*b*ArcSin(1+I*d*x^2)))/b)*(I*Cosh(a/(2*b))+Sinh(a/(2*b))))/(2*b*(Cos(ArcSin(1+I*d*x^2)/2)-Sin(ArcSin(1+I*d*x^2)/2)))+(x*(Cosh(a/(2*b))+I*Sinh(a/(2*b)))*SinhIntegral((a-I*b*ArcSin(1+I*d*x^2))/(2*b)))/(2*b*(Cos(ArcSin(1+I*d*x^2)/2)-Sin(ArcSin(1+I*d*x^2)/2)))");

	}

	// {(a-I*b*ArcSin(1+I*d*x^2))^(-2), x, 1, -Sqrt((-2*I)*d*x^2+d^2*x^4)/(2*b*d*x*(a-I*b*ArcSin(1+I*d*x^2)))
	// +(x*CosIntegral(((I/2)*(a-I*b*ArcSin(1+I*d*x^2)))/b)*(Cosh(a/(2*b))+I*Sinh(a/(2*b))))/(4*b^2*(Cos(ArcSin(1
	// +I*d*x^2)/2)-Sin(ArcSin(1+I*d*x^2)/2)))-(x*(I*Cosh(a/(2*b))+Sinh(a/(2*b)))*SinhIntegral((a -
	// I*b*ArcSin(1+I*d*x^2))/(2*b)))/(4*b^2*(Cos(ArcSin(1+I*d*x^2)/2)-Sin(ArcSin(1+I*d*x^2)/2)))}
	public void test03013() {
		check("Integrate((a-I*b*ArcSin(1+I*d*x^2))^(-2), x)",
				"-Sqrt((-2*I)*d*x^2+d^2*x^4)/(2*b*d*x*(a-I*b*ArcSin(1+I*d*x^2)))+(x*CosIntegral(((I/2)*(a-I*b*ArcSin(1+I*d*x^2)))/b)*(Cosh(a/(2*b))+I*Sinh(a/(2*b))))/(4*b^2*(Cos(ArcSin(1+I*d*x^2)/2)-Sin(ArcSin(1+I*d*x^2)/2)))-(x*(I*Cosh(a/(2*b))+Sinh(a/(2*b)))*SinhIntegral((a-I*b*ArcSin(1+I*d*x^2))/(2*b)))/(4*b^2*(Cos(ArcSin(1+I*d*x^2)/2)-Sin(ArcSin(1+I*d*x^2)/2)))");

	}

	// {Sqrt(a+I*b*ArcSin(1-I*d*x^2)), x, 1, x*Sqrt(a+I*b*ArcSin(1-I*d*x^2)) +
	// (Sqrt(Pi)*x*FresnelS((Sqrt((-I)/b)*Sqrt(a+I*b*ArcSin(1-I*d*x^2)))/Sqrt(Pi))*(Cosh(a/(2*b)) +
	// I*Sinh(a/(2*b))))/(Sqrt((-I)/b)*(Cos(ArcSin(1-I*d*x^2)/2)-Sin(ArcSin(1-I*d*x^2)/2))) -
	// (Sqrt((-I)/b)*b*Sqrt(Pi)*x*FresnelC((Sqrt((-I)/b)*Sqrt(a+I*b*ArcSin(1-I*d*x^2)))/Sqrt(Pi))*(I*Cosh(a/(2*b)) +
	// Sinh(a/(2*b))))/(Cos(ArcSin(1-I*d*x^2)/2)-Sin(ArcSin(1-I*d*x^2)/2))}
	public void test03014() {
		check("Integrate(Sqrt(a+I*b*ArcSin(1-I*d*x^2)), x)",
				"x*Sqrt(a+I*b*ArcSin(1-I*d*x^2))+(Sqrt(Pi)*x*FresnelS((Sqrt((-I)/b)*Sqrt(a+I*b*ArcSin(1-I*d*x^2)))/Sqrt(Pi))*(Cosh(a/(2*b))+I*Sinh(a/(2*b))))/(Sqrt((-I)/b)*(Cos(ArcSin(1-I*d*x^2)/2)-Sin(ArcSin(1-I*d*x^2)/2)))-(Sqrt((-I)/b)*b*Sqrt(Pi)*x*FresnelC((Sqrt((-I)/b)*Sqrt(a+I*b*ArcSin(1-I*d*x^2)))/Sqrt(Pi))*(I*Cosh(a/(2*b))+Sinh(a/(2*b))))/(Cos(ArcSin(1-I*d*x^2)/2)-Sin(ArcSin(1-I*d*x^2)/2))");

	}

	// {1/Sqrt(a+I*b*ArcSin(1-I*d*x^2)), x, 1, -((Sqrt(Pi)*x*FresnelS(Sqrt(a+I*b*ArcSin(1 -
	// I*d*x^2))/(Sqrt(I*b)*Sqrt(Pi)))*(Cosh(a/(2*b))-I*Sinh(a/(2*b))))/(Sqrt(I*b)*(Cos(ArcSin(1-I*d*x^2)/2) -
	// Sin(ArcSin(1-I*d*x^2)/2))))-(Sqrt(Pi)*x*FresnelC(Sqrt(a+I*b*ArcSin(1 -
	// I*d*x^2))/(Sqrt(I*b)*Sqrt(Pi)))*(Cosh(a/(2*b))+I*Sinh(a/(2*b))))/(Sqrt(I*b)*(Cos(ArcSin(1-I*d*x^2)/2) -
	// Sin(ArcSin(1-I*d*x^2)/2)))}
	public void test03015() {
		check("Integrate(1/Sqrt(a+I*b*ArcSin(1-I*d*x^2)), x)",
				"-((Sqrt(Pi)*x*FresnelS(Sqrt(a+I*b*ArcSin(1-I*d*x^2))/(Sqrt(I*b)*Sqrt(Pi)))*(Cosh(a/(2*b))-I*Sinh(a/(2*b))))/(Sqrt(I*b)*(Cos(ArcSin(1-I*d*x^2)/2)-Sin(ArcSin(1-I*d*x^2)/2))))-(Sqrt(Pi)*x*FresnelC(Sqrt(a+I*b*ArcSin(1-I*d*x^2))/(Sqrt(I*b)*Sqrt(Pi)))*(Cosh(a/(2*b))+I*Sinh(a/(2*b))))/(Sqrt(I*b)*(Cos(ArcSin(1-I*d*x^2)/2)-Sin(ArcSin(1-I*d*x^2)/2)))");

	}

	// {(a+I*b*ArcSin(1-I*d*x^2))^(-3/2), x, 1, -(Sqrt((2*I)*d*x^2+d^2*x^4)/(b*d*x*Sqrt(a+I*b*ArcSin(1 -
	// I*d*x^2))))-(((-I)/b)^(3/2)*Sqrt(Pi)*x*FresnelC((Sqrt((-I)/b)*Sqrt(a+I*b*ArcSin(1 -
	// I*d*x^2)))/Sqrt(Pi))*(Cosh(a/(2*b))-I*Sinh(a/(2*b))))/(Cos(ArcSin(1-I*d*x^2)/2)-Sin(ArcSin(1-I*d*x^2)/2))
	// +(((-I)/b)^(3/2)*Sqrt(Pi)*x*FresnelS((Sqrt((-I)/b)*Sqrt(a+I*b*ArcSin(1-I*d*x^2)))/Sqrt(Pi))*(Cosh(a/(2*b)) +
	// I*Sinh(a/(2*b))))/(Cos(ArcSin(1-I*d*x^2)/2)-Sin(ArcSin(1-I*d*x^2)/2))}
	public void test03016() {
		check("Integrate((a+I*b*ArcSin(1-I*d*x^2))^(-3/2), x)",
				"-(Sqrt((2*I)*d*x^2+d^2*x^4)/(b*d*x*Sqrt(a+I*b*ArcSin(1-I*d*x^2))))-(((-I)/b)^(3/2)*Sqrt(Pi)*x*FresnelC((Sqrt((-I)/b)*Sqrt(a+I*b*ArcSin(1-I*d*x^2)))/Sqrt(Pi))*(Cosh(a/(2*b))-I*Sinh(a/(2*b))))/(Cos(ArcSin(1-I*d*x^2)/2)-Sin(ArcSin(1-I*d*x^2)/2))+(((-I)/b)^(3/2)*Sqrt(Pi)*x*FresnelS((Sqrt((-I)/b)*Sqrt(a+I*b*ArcSin(1-I*d*x^2)))/Sqrt(Pi))*(Cosh(a/(2*b))+I*Sinh(a/(2*b))))/(Cos(ArcSin(1-I*d*x^2)/2)-Sin(ArcSin(1-I*d*x^2)/2))");

	}

	// {Sqrt(a-I*b*ArcSin(1+I*d*x^2)), x, 1, x*Sqrt(a-I*b*ArcSin(1+I*d*x^2)) +
	// (Sqrt(Pi)*x*FresnelS((Sqrt(I/b)*Sqrt(a-I*b*ArcSin(1+I*d*x^2)))/Sqrt(Pi))*(Cosh(a/(2*b)) -
	// I*Sinh(a/(2*b))))/(Sqrt(I/b)*(Cos(ArcSin(1+I*d*x^2)/2)-Sin(ArcSin(1+I*d*x^2)/2))) -
	// (Sqrt(Pi)*x*FresnelC((Sqrt(I/b)*Sqrt(a-I*b*ArcSin(1+I*d*x^2)))/Sqrt(Pi))*(Cosh(a/(2*b)) +
	// I*Sinh(a/(2*b))))/(Sqrt(I/b)*(Cos(ArcSin(1+I*d*x^2)/2)-Sin(ArcSin(1+I*d*x^2)/2)))}
	public void test03017() {
		check("Integrate(Sqrt(a-I*b*ArcSin(1+I*d*x^2)), x)",
				"x*Sqrt(a-I*b*ArcSin(1+I*d*x^2))+(Sqrt(Pi)*x*FresnelS((Sqrt(I/b)*Sqrt(a-I*b*ArcSin(1+I*d*x^2)))/Sqrt(Pi))*(Cosh(a/(2*b))-I*Sinh(a/(2*b))))/(Sqrt(I/b)*(Cos(ArcSin(1+I*d*x^2)/2)-Sin(ArcSin(1+I*d*x^2)/2)))-(Sqrt(Pi)*x*FresnelC((Sqrt(I/b)*Sqrt(a-I*b*ArcSin(1+I*d*x^2)))/Sqrt(Pi))*(Cosh(a/(2*b))+I*Sinh(a/(2*b))))/(Sqrt(I/b)*(Cos(ArcSin(1+I*d*x^2)/2)-Sin(ArcSin(1+I*d*x^2)/2)))");

	}

	// {1/Sqrt(a-I*b*ArcSin(1+I*d*x^2)), x, 1, -((Sqrt(Pi)*x*FresnelC(Sqrt(a-I*b*ArcSin(1 +
	// I*d*x^2))/(Sqrt((-I)*b)*Sqrt(Pi)))*(Cosh(a/(2*b))-I*Sinh(a/(2*b))))/(Sqrt((-I)*b)*(Cos(ArcSin(1+I*d*x^2)/2) -
	// Sin(ArcSin(1+I*d*x^2)/2))))-(Sqrt(Pi)*x*FresnelS(Sqrt(a-I*b*ArcSin(1 +
	// I*d*x^2))/(Sqrt((-I)*b)*Sqrt(Pi)))*(Cosh(a/(2*b))+I*Sinh(a/(2*b))))/(Sqrt((-I)*b)*(Cos(ArcSin(1+I*d*x^2)/2) -
	// Sin(ArcSin(1+I*d*x^2)/2)))}
	public void test03018() {
		check("Integrate(1/Sqrt(a-I*b*ArcSin(1+I*d*x^2)), x)",
				"-((Sqrt(Pi)*x*FresnelC(Sqrt(a-I*b*ArcSin(1+I*d*x^2))/(Sqrt((-I)*b)*Sqrt(Pi)))*(Cosh(a/(2*b))-I*Sinh(a/(2*b))))/(Sqrt((-I)*b)*(Cos(ArcSin(1+I*d*x^2)/2)-Sin(ArcSin(1+I*d*x^2)/2))))-(Sqrt(Pi)*x*FresnelS(Sqrt(a-I*b*ArcSin(1+I*d*x^2))/(Sqrt((-I)*b)*Sqrt(Pi)))*(Cosh(a/(2*b))+I*Sinh(a/(2*b))))/(Sqrt((-I)*b)*(Cos(ArcSin(1+I*d*x^2)/2)-Sin(ArcSin(1+I*d*x^2)/2)))");

	}

	// {(a-I*b*ArcSin(1+I*d*x^2))^(-3/2), x, 1, -(Sqrt((-2*I)*d*x^2+d^2*x^4)/(b*d*x*Sqrt(a-I*b*ArcSin(1 +
	// I*d*x^2))))+((I/b)^(3/2)*Sqrt(Pi)*x*FresnelS((Sqrt(I/b)*Sqrt(a-I*b*ArcSin(1 +
	// I*d*x^2)))/Sqrt(Pi))*(Cosh(a/(2*b))-I*Sinh(a/(2*b))))/(Cos(ArcSin(1+I*d*x^2)/2)-Sin(ArcSin(1+I*d*x^2)/2))
	// -((I/b)^(3/2)*Sqrt(Pi)*x*FresnelC((Sqrt(I/b)*Sqrt(a-I*b*ArcSin(1+I*d*x^2)))/Sqrt(Pi))*(Cosh(a/(2*b)) +
	// I*Sinh(a/(2*b))))/(Cos(ArcSin(1+I*d*x^2)/2)-Sin(ArcSin(1+I*d*x^2)/2))}
	public void test03019() {
		check("Integrate((a-I*b*ArcSin(1+I*d*x^2))^(-3/2), x)",
				"-(Sqrt((-2*I)*d*x^2+d^2*x^4)/(b*d*x*Sqrt(a-I*b*ArcSin(1+I*d*x^2))))+((I/b)^(3/2)*Sqrt(Pi)*x*FresnelS((Sqrt(I/b)*Sqrt(a-I*b*ArcSin(1+I*d*x^2)))/Sqrt(Pi))*(Cosh(a/(2*b))-I*Sinh(a/(2*b))))/(Cos(ArcSin(1+I*d*x^2)/2)-Sin(ArcSin(1+I*d*x^2)/2))-((I/b)^(3/2)*Sqrt(Pi)*x*FresnelC((Sqrt(I/b)*Sqrt(a-I*b*ArcSin(1+I*d*x^2)))/Sqrt(Pi))*(Cosh(a/(2*b))+I*Sinh(a/(2*b))))/(Cos(ArcSin(1+I*d*x^2)/2)-Sin(ArcSin(1+I*d*x^2)/2))");

	}

	// {x^m*ArcCosh(a*x)^4, x, 1, (x^(1+m)*ArcCosh(a*x)^4)/(1+m)-(4*a*Unintegrable((x^(1 +
	// m)*ArcCosh(a*x)^3)/(Sqrt(-1+a*x)*Sqrt(1+a*x)), x))/(1+m)}
	public void test03020() {
		check("Integrate(x^m*ArcCosh(a*x)^4, x)",
				"(x^(1+m)*ArcCosh(a*x)^4)/(1+m)-(4*a*Unintegrable((x^(1+m)*ArcCosh(a*x)^3)/(Sqrt(-1+a*x)*Sqrt(1+a*x)), x))/(1+m)");

	}

	// {x^m*ArcCosh(a*x)^3, x, 1, (x^(1+m)*ArcCosh(a*x)^3)/(1+m)-(3*a*Unintegrable((x^(1 +
	// m)*ArcCosh(a*x)^2)/(Sqrt(-1+a*x)*Sqrt(1+a*x)), x))/(1+m)}
	public void test03021() {
		check("Integrate(x^m*ArcCosh(a*x)^3, x)",
				"(x^(1+m)*ArcCosh(a*x)^3)/(1+m)-(3*a*Unintegrable((x^(1+m)*ArcCosh(a*x)^2)/(Sqrt(-1+a*x)*Sqrt(1+a*x)), x))/(1+m)");

	}

	// {(f*x)^m*(d-c^2*d*x^2)^(5/2)*(a+b*ArcCosh(c*x))^2, x, 1, (d^2*Sqrt(d-c^2*d*x^2)*Unintegrable((f*x)^m*(-1 +
	// c*x)^(5/2)*(1+c*x)^(5/2)*(a+b*ArcCosh(c*x))^2, x))/(Sqrt(-1+c*x)*Sqrt(1+c*x))}
	public void test03022() {
		check("Integrate((f*x)^m*(d-c^2*d*x^2)^(5/2)*(a+b*ArcCosh(c*x))^2, x)",
				"(d^2*Sqrt(d-c^2*d*x^2)*Unintegrable((f*x)^m*(-1+c*x)^(5/2)*(1+c*x)^(5/2)*(a+b*ArcCosh(c*x))^2, x))/(Sqrt(-1+c*x)*Sqrt(1+c*x))");

	}

	// {(f*x)^m*(d-c^2*d*x^2)^(3/2)*(a+b*ArcCosh(c*x))^2, x, 1, -((d*Sqrt(d-c^2*d*x^2)*Unintegrable((f*x)^m*(-1 +
	// c*x)^(3/2)*(1+c*x)^(3/2)*(a+b*ArcCosh(c*x))^2, x))/(Sqrt(-1+c*x)*Sqrt(1+c*x)))}
	public void test03023() {
		check("Integrate((f*x)^m*(d-c^2*d*x^2)^(3/2)*(a+b*ArcCosh(c*x))^2, x)",
				"-((d*Sqrt(d-c^2*d*x^2)*Unintegrable((f*x)^m*(-1+c*x)^(3/2)*(1+c*x)^(3/2)*(a+b*ArcCosh(c*x))^2, x))/(Sqrt(-1+c*x)*Sqrt(1+c*x)))");

	}

	// {(f*x)^m*Sqrt(d-c^2*d*x^2)*(a+b*ArcCosh(c*x))^2, x, 1, (Sqrt(d-c^2*d*x^2)*Unintegrable((f*x)^m*Sqrt(-1 +
	// c*x)*Sqrt(1+c*x)*(a+b*ArcCosh(c*x))^2, x))/(Sqrt(-1+c*x)*Sqrt(1+c*x))}
	public void test03024() {
		check("Integrate((f*x)^m*Sqrt(d-c^2*d*x^2)*(a+b*ArcCosh(c*x))^2, x)",
				"(Sqrt(d-c^2*d*x^2)*Unintegrable((f*x)^m*Sqrt(-1+c*x)*Sqrt(1+c*x)*(a+b*ArcCosh(c*x))^2, x))/(Sqrt(-1+c*x)*Sqrt(1+c*x))");

	}

	// {((f*x)^m*(a+b*ArcCosh(c*x))^2)/Sqrt(d-c^2*d*x^2), x, 1, (Sqrt(-1+c*x)*Sqrt(1 +
	// c*x)*Unintegrable(((f*x)^m*(a+b*ArcCosh(c*x))^2)/(Sqrt(-1+c*x)*Sqrt(1+c*x)), x))/Sqrt(d-c^2*d*x^2)}
	public void test03025() {
		check("Integrate(((f*x)^m*(a+b*ArcCosh(c*x))^2)/Sqrt(d-c^2*d*x^2), x)",
				"(Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(((f*x)^m*(a+b*ArcCosh(c*x))^2)/(Sqrt(-1+c*x)*Sqrt(1+c*x)), x))/Sqrt(d-c^2*d*x^2)");

	}

	// {((f*x)^m*(a+b*ArcCosh(c*x))^2)/(d-c^2*d*x^2)^(3/2), x, 1, -((Sqrt(-1+c*x)*Sqrt(1 +
	// c*x)*Unintegrable(((f*x)^m*(a+b*ArcCosh(c*x))^2)/((-1+c*x)^(3/2)*(1+c*x)^(3/2)), x))/(d*Sqrt(d -
	// c^2*d*x^2)))}
	public void test03026() {
		check("Integrate(((f*x)^m*(a+b*ArcCosh(c*x))^2)/(d-c^2*d*x^2)^(3/2), x)",
				"-((Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(((f*x)^m*(a+b*ArcCosh(c*x))^2)/((-1+c*x)^(3/2)*(1+c*x)^(3/2)), x))/(d*Sqrt(d-c^2*d*x^2)))");

	}

	// {((f*x)^m*(a+b*ArcCosh(c*x))^2)/(d-c^2*d*x^2)^(5/2), x, 1, (Sqrt(-1+c*x)*Sqrt(1 +
	// c*x)*Unintegrable(((f*x)^m*(a+b*ArcCosh(c*x))^2)/((-1+c*x)^(5/2)*(1+c*x)^(5/2)), x))/(d^2*Sqrt(d -
	// c^2*d*x^2))}
	public void test03027() {
		check("Integrate(((f*x)^m*(a+b*ArcCosh(c*x))^2)/(d-c^2*d*x^2)^(5/2), x)",
				"(Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(((f*x)^m*(a+b*ArcCosh(c*x))^2)/((-1+c*x)^(5/2)*(1+c*x)^(5/2)), x))/(d^2*Sqrt(d-c^2*d*x^2))");

	}

	// {((f*x)^m*ArcCosh(c*x)^2)/Sqrt(1-c^2*x^2), x, 1, (Sqrt(-1+c*x)*Sqrt(1 +
	// c*x)*Unintegrable(((f*x)^m*ArcCosh(c*x)^2)/(Sqrt(-1+c*x)*Sqrt(1+c*x)), x))/Sqrt(1-c^2*x^2)}
	public void test03028() {
		check("Integrate(((f*x)^m*ArcCosh(c*x)^2)/Sqrt(1-c^2*x^2), x)",
				"(Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(((f*x)^m*ArcCosh(c*x)^2)/(Sqrt(-1+c*x)*Sqrt(1+c*x)), x))/Sqrt(1-c^2*x^2)");

	}

	// {((f*x)^m*(a+b*ArcCosh(c*x))^3)/Sqrt(1-c^2*x^2), x, 1, (Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(((f*x)^m*(a
	// +b*ArcCosh(c*x))^3)/(Sqrt(-1+c*x)*Sqrt(1+c*x)), x))/Sqrt(1-c^2*x^2)}
	public void test03029() {
		check("Integrate(((f*x)^m*(a+b*ArcCosh(c*x))^3)/Sqrt(1-c^2*x^2), x)",
				"(Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(((f*x)^m*(a+b*ArcCosh(c*x))^3)/(Sqrt(-1+c*x)*Sqrt(1+c*x)), x))/Sqrt(1-c^2*x^2)");

	}

	// {Sqrt(1-c^2*x^2)/(x^3*(a+b*ArcCosh(c*x))), x, 1, (Sqrt(1-c^2*x^2)*Unintegrable((Sqrt(-1+c*x)*Sqrt(1 +
	// c*x))/(x^3*(a+b*ArcCosh(c*x))), x))/(Sqrt(-1+c*x)*Sqrt(1+c*x))}
	public void test03030() {
		check("Integrate(Sqrt(1-c^2*x^2)/(x^3*(a+b*ArcCosh(c*x))), x)",
				"(Sqrt(1-c^2*x^2)*Unintegrable((Sqrt(-1+c*x)*Sqrt(1+c*x))/(x^3*(a+b*ArcCosh(c*x))), x))/(Sqrt(-1+c*x)*Sqrt(1+c*x))");

	}

	// {Sqrt(1-c^2*x^2)/(x^4*(a+b*ArcCosh(c*x))), x, 1, (Sqrt(1-c^2*x^2)*Unintegrable((Sqrt(-1+c*x)*Sqrt(1 +
	// c*x))/(x^4*(a+b*ArcCosh(c*x))), x))/(Sqrt(-1+c*x)*Sqrt(1+c*x))}
	public void test03031() {
		check("Integrate(Sqrt(1-c^2*x^2)/(x^4*(a+b*ArcCosh(c*x))), x)",
				"(Sqrt(1-c^2*x^2)*Unintegrable((Sqrt(-1+c*x)*Sqrt(1+c*x))/(x^4*(a+b*ArcCosh(c*x))), x))/(Sqrt(-1+c*x)*Sqrt(1+c*x))");

	}

	// {(1-c^2*x^2)^(3/2)/(x^3*(a+b*ArcCosh(c*x))), x, 1, -((Sqrt(1-c^2*x^2)*Unintegrable(((-1+c*x)^(3/2)*(1 +
	// c*x)^(3/2))/(x^3*(a+b*ArcCosh(c*x))), x))/(Sqrt(-1+c*x)*Sqrt(1+c*x)))}
	public void test03032() {
		check("Integrate((1-c^2*x^2)^(3/2)/(x^3*(a+b*ArcCosh(c*x))), x)",
				"-((Sqrt(1-c^2*x^2)*Unintegrable(((-1+c*x)^(3/2)*(1+c*x)^(3/2))/(x^3*(a+b*ArcCosh(c*x))), x))/(Sqrt(-1+c*x)*Sqrt(1+c*x)))");

	}

	// {(1-c^2*x^2)^(3/2)/(x^4*(a+b*ArcCosh(c*x))), x, 1, -((Sqrt(1-c^2*x^2)*Unintegrable(((-1+c*x)^(3/2)*(1 +
	// c*x)^(3/2))/(x^4*(a+b*ArcCosh(c*x))), x))/(Sqrt(-1+c*x)*Sqrt(1+c*x)))}
	public void test03033() {
		check("Integrate((1-c^2*x^2)^(3/2)/(x^4*(a+b*ArcCosh(c*x))), x)",
				"-((Sqrt(1-c^2*x^2)*Unintegrable(((-1+c*x)^(3/2)*(1+c*x)^(3/2))/(x^4*(a+b*ArcCosh(c*x))), x))/(Sqrt(-1+c*x)*Sqrt(1+c*x)))");

	}

	// {(1-c^2*x^2)^(5/2)/(x^3*(a+b*ArcCosh(c*x))), x, 1, (Sqrt(1-c^2*x^2)*Unintegrable(((-1+c*x)^(5/2)*(1 +
	// c*x)^(5/2))/(x^3*(a+b*ArcCosh(c*x))), x))/(Sqrt(-1+c*x)*Sqrt(1+c*x))}
	public void test03034() {
		check("Integrate((1-c^2*x^2)^(5/2)/(x^3*(a+b*ArcCosh(c*x))), x)",
				"(Sqrt(1-c^2*x^2)*Unintegrable(((-1+c*x)^(5/2)*(1+c*x)^(5/2))/(x^3*(a+b*ArcCosh(c*x))), x))/(Sqrt(-1+c*x)*Sqrt(1+c*x))");

	}

	// {(1-c^2*x^2)^(5/2)/(x^4*(a+b*ArcCosh(c*x))), x, 1, (Sqrt(1-c^2*x^2)*Unintegrable(((-1+c*x)^(5/2)*(1 +
	// c*x)^(5/2))/(x^4*(a+b*ArcCosh(c*x))), x))/(Sqrt(-1+c*x)*Sqrt(1+c*x))}
	public void test03035() {
		check("Integrate((1-c^2*x^2)^(5/2)/(x^4*(a+b*ArcCosh(c*x))), x)",
				"(Sqrt(1-c^2*x^2)*Unintegrable(((-1+c*x)^(5/2)*(1+c*x)^(5/2))/(x^4*(a+b*ArcCosh(c*x))), x))/(Sqrt(-1+c*x)*Sqrt(1+c*x))");

	}

	// {1/(x*Sqrt(1-a^2*x^2)*ArcCosh(a*x)), x, 1, (Sqrt(-1+a*x)*Sqrt(1+a*x)*Unintegrable(1/(x*Sqrt(-1 +
	// a*x)*Sqrt(1+a*x)*ArcCosh(a*x)), x))/Sqrt(1-a^2*x^2)}
	public void test03036() {
		check("Integrate(1/(x*Sqrt(1-a^2*x^2)*ArcCosh(a*x)), x)",
				"(Sqrt(-1+a*x)*Sqrt(1+a*x)*Unintegrable(1/(x*Sqrt(-1+a*x)*Sqrt(1+a*x)*ArcCosh(a*x)), x))/Sqrt(1-a^2*x^2)");

	}

	// {1/(x^2*Sqrt(1-a^2*x^2)*ArcCosh(a*x)), x, 1, (Sqrt(-1+a*x)*Sqrt(1+a*x)*Unintegrable(1/(x^2*Sqrt(-1 +
	// a*x)*Sqrt(1+a*x)*ArcCosh(a*x)), x))/Sqrt(1-a^2*x^2)}
	public void test03037() {
		check("Integrate(1/(x^2*Sqrt(1-a^2*x^2)*ArcCosh(a*x)), x)",
				"(Sqrt(-1+a*x)*Sqrt(1+a*x)*Unintegrable(1/(x^2*Sqrt(-1+a*x)*Sqrt(1+a*x)*ArcCosh(a*x)), x))/Sqrt(1-a^2*x^2)");

	}

	// {1/(x*Sqrt(1-c^2*x^2)*(a+b*ArcCosh(c*x))), x, 1, (Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(1/(x*Sqrt(-1 +
	// c*x)*Sqrt(1+c*x)*(a+b*ArcCosh(c*x))), x))/Sqrt(1-c^2*x^2)}
	public void test03038() {
		check("Integrate(1/(x*Sqrt(1-c^2*x^2)*(a+b*ArcCosh(c*x))), x)",
				"(Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(1/(x*Sqrt(-1+c*x)*Sqrt(1+c*x)*(a+b*ArcCosh(c*x))), x))/Sqrt(1-c^2*x^2)");

	}

	// {1/(x^2*Sqrt(1-c^2*x^2)*(a+b*ArcCosh(c*x))), x, 1, (Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(1/(x^2*Sqrt(-1
	// +c*x)*Sqrt(1+c*x)*(a+b*ArcCosh(c*x))), x))/Sqrt(1-c^2*x^2)}
	public void test03039() {
		check("Integrate(1/(x^2*Sqrt(1-c^2*x^2)*(a+b*ArcCosh(c*x))), x)",
				"(Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(1/(x^2*Sqrt(-1+c*x)*Sqrt(1+c*x)*(a+b*ArcCosh(c*x))), x))/Sqrt(1-c^2*x^2)");

	}

	// {x^2/((1-c^2*x^2)^(3/2)*(a+b*ArcCosh(c*x))), x, 1, -((Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(x^2/((-1 +
	// c*x)^(3/2)*(1+c*x)^(3/2)*(a+b*ArcCosh(c*x))), x))/Sqrt(1-c^2*x^2))}
	public void test03040() {
		check("Integrate(x^2/((1-c^2*x^2)^(3/2)*(a+b*ArcCosh(c*x))), x)",
				"-((Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(x^2/((-1+c*x)^(3/2)*(1+c*x)^(3/2)*(a+b*ArcCosh(c*x))), x))/Sqrt(1-c^2*x^2))");

	}

	// {x/((1-c^2*x^2)^(3/2)*(a+b*ArcCosh(c*x))), x, 1, -((Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(x/((-1 +
	// c*x)^(3/2)*(1+c*x)^(3/2)*(a+b*ArcCosh(c*x))), x))/Sqrt(1-c^2*x^2))}
	public void test03041() {
		check("Integrate(x/((1-c^2*x^2)^(3/2)*(a+b*ArcCosh(c*x))), x)",
				"-((Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(x/((-1+c*x)^(3/2)*(1+c*x)^(3/2)*(a+b*ArcCosh(c*x))), x))/Sqrt(1-c^2*x^2))");

	}

	// {1/((1-c^2*x^2)^(3/2)*(a+b*ArcCosh(c*x))), x, 1, -((Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(1/((-1 +
	// c*x)^(3/2)*(1+c*x)^(3/2)*(a+b*ArcCosh(c*x))), x))/Sqrt(1-c^2*x^2))}
	public void test03042() {
		check("Integrate(1/((1-c^2*x^2)^(3/2)*(a+b*ArcCosh(c*x))), x)",
				"-((Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(1/((-1+c*x)^(3/2)*(1+c*x)^(3/2)*(a+b*ArcCosh(c*x))), x))/Sqrt(1-c^2*x^2))");

	}

	// {1/(x*(1-c^2*x^2)^(3/2)*(a+b*ArcCosh(c*x))), x, 1, -((Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(1/(x*(-1 +
	// c*x)^(3/2)*(1+c*x)^(3/2)*(a+b*ArcCosh(c*x))), x))/Sqrt(1-c^2*x^2))}
	public void test03043() {
		check("Integrate(1/(x*(1-c^2*x^2)^(3/2)*(a+b*ArcCosh(c*x))), x)",
				"-((Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(1/(x*(-1+c*x)^(3/2)*(1+c*x)^(3/2)*(a+b*ArcCosh(c*x))), x))/Sqrt(1-c^2*x^2))");

	}

	// {1/(x^2*(1-c^2*x^2)^(3/2)*(a+b*ArcCosh(c*x))), x, 1, -((Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(1/(x^2*(-1
	// +c*x)^(3/2)*(1+c*x)^(3/2)*(a+b*ArcCosh(c*x))), x))/Sqrt(1-c^2*x^2))}
	public void test03044() {
		check("Integrate(1/(x^2*(1-c^2*x^2)^(3/2)*(a+b*ArcCosh(c*x))), x)",
				"-((Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(1/(x^2*(-1+c*x)^(3/2)*(1+c*x)^(3/2)*(a+b*ArcCosh(c*x))), x))/Sqrt(1-c^2*x^2))");

	}

	// {x^2/((1-c^2*x^2)^(5/2)*(a+b*ArcCosh(c*x))), x, 1, (Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(x^2/((-1 +
	// c*x)^(5/2)*(1+c*x)^(5/2)*(a+b*ArcCosh(c*x))), x))/Sqrt(1-c^2*x^2)}
	public void test03045() {
		check("Integrate(x^2/((1-c^2*x^2)^(5/2)*(a+b*ArcCosh(c*x))), x)",
				"(Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(x^2/((-1+c*x)^(5/2)*(1+c*x)^(5/2)*(a+b*ArcCosh(c*x))), x))/Sqrt(1-c^2*x^2)");

	}

	// {x/((1-c^2*x^2)^(5/2)*(a+b*ArcCosh(c*x))), x, 1, (Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(x/((-1 +
	// c*x)^(5/2)*(1+c*x)^(5/2)*(a+b*ArcCosh(c*x))), x))/Sqrt(1-c^2*x^2)}
	public void test03046() {
		check("Integrate(x/((1-c^2*x^2)^(5/2)*(a+b*ArcCosh(c*x))), x)",
				"(Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(x/((-1+c*x)^(5/2)*(1+c*x)^(5/2)*(a+b*ArcCosh(c*x))), x))/Sqrt(1-c^2*x^2)");

	}

	// {1/((1-c^2*x^2)^(5/2)*(a+b*ArcCosh(c*x))), x, 1, (Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(1/((-1 +
	// c*x)^(5/2)*(1+c*x)^(5/2)*(a+b*ArcCosh(c*x))), x))/Sqrt(1-c^2*x^2)}
	public void test03047() {
		check("Integrate(1/((1-c^2*x^2)^(5/2)*(a+b*ArcCosh(c*x))), x)",
				"(Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(1/((-1+c*x)^(5/2)*(1+c*x)^(5/2)*(a+b*ArcCosh(c*x))), x))/Sqrt(1-c^2*x^2)");

	}

	// {1/(x*(1-c^2*x^2)^(5/2)*(a+b*ArcCosh(c*x))), x, 1, (Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(1/(x*(-1 +
	// c*x)^(5/2)*(1+c*x)^(5/2)*(a+b*ArcCosh(c*x))), x))/Sqrt(1-c^2*x^2)}
	public void test03048() {
		check("Integrate(1/(x*(1-c^2*x^2)^(5/2)*(a+b*ArcCosh(c*x))), x)",
				"(Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(1/(x*(-1+c*x)^(5/2)*(1+c*x)^(5/2)*(a+b*ArcCosh(c*x))), x))/Sqrt(1-c^2*x^2)");

	}

	// {1/(x^2*(1-c^2*x^2)^(5/2)*(a+b*ArcCosh(c*x))), x, 1, (Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(1/(x^2*(-1 +
	// c*x)^(5/2)*(1+c*x)^(5/2)*(a+b*ArcCosh(c*x))), x))/Sqrt(1-c^2*x^2)}
	public void test03049() {
		check("Integrate(1/(x^2*(1-c^2*x^2)^(5/2)*(a+b*ArcCosh(c*x))), x)",
				"(Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(1/(x^2*(-1+c*x)^(5/2)*(1+c*x)^(5/2)*(a+b*ArcCosh(c*x))), x))/Sqrt(1-c^2*x^2)");

	}

	// {(x^m*(1-c^2*x^2)^(5/2))/(a+b*ArcCosh(c*x)), x, 1, (Sqrt(1-c^2*x^2)*Unintegrable((x^m*(-1+c*x)^(5/2)*(1 +
	// c*x)^(5/2))/(a+b*ArcCosh(c*x)), x))/(Sqrt(-1+c*x)*Sqrt(1+c*x))}
	public void test03050() {
		check("Integrate((x^m*(1-c^2*x^2)^(5/2))/(a+b*ArcCosh(c*x)), x)",
				"(Sqrt(1-c^2*x^2)*Unintegrable((x^m*(-1+c*x)^(5/2)*(1+c*x)^(5/2))/(a+b*ArcCosh(c*x)), x))/(Sqrt(-1+c*x)*Sqrt(1+c*x))");

	}

	// {(x^m*(1-c^2*x^2)^(3/2))/(a+b*ArcCosh(c*x)), x, 1, -((Sqrt(1-c^2*x^2)*Unintegrable((x^m*(-1+c*x)^(3/2)*(1
	// +c*x)^(3/2))/(a+b*ArcCosh(c*x)), x))/(Sqrt(-1+c*x)*Sqrt(1+c*x)))}
	public void test03051() {
		check("Integrate((x^m*(1-c^2*x^2)^(3/2))/(a+b*ArcCosh(c*x)), x)",
				"-((Sqrt(1-c^2*x^2)*Unintegrable((x^m*(-1+c*x)^(3/2)*(1+c*x)^(3/2))/(a+b*ArcCosh(c*x)), x))/(Sqrt(-1+c*x)*Sqrt(1+c*x)))");

	}

	// {(x^m*Sqrt(1-c^2*x^2))/(a+b*ArcCosh(c*x)), x, 1, (Sqrt(1-c^2*x^2)*Unintegrable((x^m*Sqrt(-1+c*x)*Sqrt(1 +
	// c*x))/(a+b*ArcCosh(c*x)), x))/(Sqrt(-1+c*x)*Sqrt(1+c*x))}
	public void test03052() {
		check("Integrate((x^m*Sqrt(1-c^2*x^2))/(a+b*ArcCosh(c*x)), x)",
				"(Sqrt(1-c^2*x^2)*Unintegrable((x^m*Sqrt(-1+c*x)*Sqrt(1+c*x))/(a+b*ArcCosh(c*x)), x))/(Sqrt(-1+c*x)*Sqrt(1+c*x))");

	}

	// {x^m/(Sqrt(1-a^2*x^2)*ArcCosh(a*x)), x, 1, (Sqrt(-1+a*x)*Sqrt(1+a*x)*Unintegrable(x^m/(Sqrt(-1 +
	// a*x)*Sqrt(1+a*x)*ArcCosh(a*x)), x))/Sqrt(1-a^2*x^2)}
	public void test03053() {
		check("Integrate(x^m/(Sqrt(1-a^2*x^2)*ArcCosh(a*x)), x)",
				"(Sqrt(-1+a*x)*Sqrt(1+a*x)*Unintegrable(x^m/(Sqrt(-1+a*x)*Sqrt(1+a*x)*ArcCosh(a*x)), x))/Sqrt(1-a^2*x^2)");

	}

	// {x^m/(Sqrt(1-c^2*x^2)*(a+b*ArcCosh(c*x))), x, 1, (Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(x^m/(Sqrt(-1 +
	// c*x)*Sqrt(1+c*x)*(a+b*ArcCosh(c*x))), x))/Sqrt(1-c^2*x^2)}
	public void test03054() {
		check("Integrate(x^m/(Sqrt(1-c^2*x^2)*(a+b*ArcCosh(c*x))), x)",
				"(Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(x^m/(Sqrt(-1+c*x)*Sqrt(1+c*x)*(a+b*ArcCosh(c*x))), x))/Sqrt(1-c^2*x^2)");

	}

	// {x^m/((1-c^2*x^2)^(3/2)*(a+b*ArcCosh(c*x))), x, 1, -((Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(x^m/((-1 +
	// c*x)^(3/2)*(1+c*x)^(3/2)*(a+b*ArcCosh(c*x))), x))/Sqrt(1-c^2*x^2))}
	public void test03055() {
		check("Integrate(x^m/((1-c^2*x^2)^(3/2)*(a+b*ArcCosh(c*x))), x)",
				"-((Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(x^m/((-1+c*x)^(3/2)*(1+c*x)^(3/2)*(a+b*ArcCosh(c*x))), x))/Sqrt(1-c^2*x^2))");

	}

	// {x^m/((1-c^2*x^2)^(5/2)*(a+b*ArcCosh(c*x))), x, 1, (Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(x^m/((-1 +
	// c*x)^(5/2)*(1+c*x)^(5/2)*(a+b*ArcCosh(c*x))), x))/Sqrt(1-c^2*x^2)}
	public void test03056() {
		check("Integrate(x^m/((1-c^2*x^2)^(5/2)*(a+b*ArcCosh(c*x))), x)",
				"(Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(x^m/((-1+c*x)^(5/2)*(1+c*x)^(5/2)*(a+b*ArcCosh(c*x))), x))/Sqrt(1-c^2*x^2)");

	}

	// {1/((c-a^2*c*x^2)*ArcCosh(a*x)^2), x, 1, 1/(a*c*Sqrt(-1+a*x)*Sqrt(1+a*x)*ArcCosh(a*x)) +
	// (a*Unintegrable(x/((-1+a*x)^(3/2)*(1+a*x)^(3/2)*ArcCosh(a*x)), x))/c}
	public void test03057() {
		check("Integrate(1/((c-a^2*c*x^2)*ArcCosh(a*x)^2), x)",
				"1/(a*c*Sqrt(-1+a*x)*Sqrt(1+a*x)*ArcCosh(a*x))+(a*Unintegrable(x/((-1+a*x)^(3/2)*(1+a*x)^(3/2)*ArcCosh(a*x)), x))/c");

	}

	// {1/((c-a^2*c*x^2)^2*ArcCosh(a*x)^2), x, 1, -(1/(a*c^2*(-1+a*x)^(3/2)*(1+a*x)^(3/2)*ArcCosh(a*x))) -
	// (3*a*Unintegrable(x/((-1+a*x)^(5/2)*(1+a*x)^(5/2)*ArcCosh(a*x)), x))/c^2}
	public void test03058() {
		check("Integrate(1/((c-a^2*c*x^2)^2*ArcCosh(a*x)^2), x)",
				"-(1/(a*c^2*(-1+a*x)^(3/2)*(1+a*x)^(3/2)*ArcCosh(a*x)))-(3*a*Unintegrable(x/((-1+a*x)^(5/2)*(1+a*x)^(5/2)*ArcCosh(a*x)), x))/c^2");

	}

	// {Sqrt(1-c^2*x^2)/(x^3*(a+b*ArcCosh(c*x))^2), x, 1, (Sqrt(1-c^2*x^2)*Unintegrable((Sqrt(-1+c*x)*Sqrt(1 +
	// c*x))/(x^3*(a+b*ArcCosh(c*x))^2), x))/(Sqrt(-1+c*x)*Sqrt(1+c*x))}
	public void test03059() {
		check("Integrate(Sqrt(1-c^2*x^2)/(x^3*(a+b*ArcCosh(c*x))^2), x)",
				"(Sqrt(1-c^2*x^2)*Unintegrable((Sqrt(-1+c*x)*Sqrt(1+c*x))/(x^3*(a+b*ArcCosh(c*x))^2), x))/(Sqrt(-1+c*x)*Sqrt(1+c*x))");

	}

	// {Sqrt(1-c^2*x^2)/(x^4*(a+b*ArcCosh(c*x))^2), x, 1, (Sqrt(1-c^2*x^2)*Unintegrable((Sqrt(-1+c*x)*Sqrt(1 +
	// c*x))/(x^4*(a+b*ArcCosh(c*x))^2), x))/(Sqrt(-1+c*x)*Sqrt(1+c*x))}
	public void test03060() {
		check("Integrate(Sqrt(1-c^2*x^2)/(x^4*(a+b*ArcCosh(c*x))^2), x)",
				"(Sqrt(1-c^2*x^2)*Unintegrable((Sqrt(-1+c*x)*Sqrt(1+c*x))/(x^4*(a+b*ArcCosh(c*x))^2), x))/(Sqrt(-1+c*x)*Sqrt(1+c*x))");

	}

	// {(1-c^2*x^2)^(3/2)/(x^3*(a+b*ArcCosh(c*x))^2), x, 1, -((Sqrt(1-c^2*x^2)*Unintegrable(((-1+c*x)^(3/2)*(1 +
	// c*x)^(3/2))/(x^3*(a+b*ArcCosh(c*x))^2), x))/(Sqrt(-1+c*x)*Sqrt(1+c*x)))}
	public void test03061() {
		check("Integrate((1-c^2*x^2)^(3/2)/(x^3*(a+b*ArcCosh(c*x))^2), x)",
				"-((Sqrt(1-c^2*x^2)*Unintegrable(((-1+c*x)^(3/2)*(1+c*x)^(3/2))/(x^3*(a+b*ArcCosh(c*x))^2), x))/(Sqrt(-1+c*x)*Sqrt(1+c*x)))");

	}

	// {(1-c^2*x^2)^(3/2)/(x^5*(a+b*ArcCosh(c*x))^2), x, 1, -((Sqrt(1-c^2*x^2)*Unintegrable(((-1+c*x)^(3/2)*(1 +
	// c*x)^(3/2))/(x^5*(a+b*ArcCosh(c*x))^2), x))/(Sqrt(-1+c*x)*Sqrt(1+c*x)))}
	public void test03062() {
		check("Integrate((1-c^2*x^2)^(3/2)/(x^5*(a+b*ArcCosh(c*x))^2), x)",
				"-((Sqrt(1-c^2*x^2)*Unintegrable(((-1+c*x)^(3/2)*(1+c*x)^(3/2))/(x^5*(a+b*ArcCosh(c*x))^2), x))/(Sqrt(-1+c*x)*Sqrt(1+c*x)))");

	}

	// {(1-c^2*x^2)^(5/2)/(x^3*(a+b*ArcCosh(c*x))^2), x, 1, (Sqrt(1-c^2*x^2)*Unintegrable(((-1+c*x)^(5/2)*(1 +
	// c*x)^(5/2))/(x^3*(a+b*ArcCosh(c*x))^2), x))/(Sqrt(-1+c*x)*Sqrt(1+c*x))}
	public void test03063() {
		check("Integrate((1-c^2*x^2)^(5/2)/(x^3*(a+b*ArcCosh(c*x))^2), x)",
				"(Sqrt(1-c^2*x^2)*Unintegrable(((-1+c*x)^(5/2)*(1+c*x)^(5/2))/(x^3*(a+b*ArcCosh(c*x))^2), x))/(Sqrt(-1+c*x)*Sqrt(1+c*x))");

	}

	// {(1-c^2*x^2)^(5/2)/(x^4*(a+b*ArcCosh(c*x))^2), x, 1, (Sqrt(1-c^2*x^2)*Unintegrable(((-1+c*x)^(5/2)*(1 +
	// c*x)^(5/2))/(x^4*(a+b*ArcCosh(c*x))^2), x))/(Sqrt(-1+c*x)*Sqrt(1+c*x))}
	public void test03064() {
		check("Integrate((1-c^2*x^2)^(5/2)/(x^4*(a+b*ArcCosh(c*x))^2), x)",
				"(Sqrt(1-c^2*x^2)*Unintegrable(((-1+c*x)^(5/2)*(1+c*x)^(5/2))/(x^4*(a+b*ArcCosh(c*x))^2), x))/(Sqrt(-1+c*x)*Sqrt(1+c*x))");

	}

	// {x^3/((1-c^2*x^2)^(3/2)*(a+b*ArcCosh(c*x))^2), x, 1, -((Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(x^3/((-1 +
	// c*x)^(3/2)*(1+c*x)^(3/2)*(a+b*ArcCosh(c*x))^2), x))/Sqrt(1-c^2*x^2))}
	public void test03065() {
		check("Integrate(x^3/((1-c^2*x^2)^(3/2)*(a+b*ArcCosh(c*x))^2), x)",
				"-((Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(x^3/((-1+c*x)^(3/2)*(1+c*x)^(3/2)*(a+b*ArcCosh(c*x))^2), x))/Sqrt(1-c^2*x^2))");

	}

	// {x/((1-c^2*x^2)^(3/2)*(a+b*ArcCosh(c*x))^2), x, 1, -((Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(x/((-1 +
	// c*x)^(3/2)*(1+c*x)^(3/2)*(a+b*ArcCosh(c*x))^2), x))/Sqrt(1-c^2*x^2))}
	public void test03066() {
		check("Integrate(x/((1-c^2*x^2)^(3/2)*(a+b*ArcCosh(c*x))^2), x)",
				"-((Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(x/((-1+c*x)^(3/2)*(1+c*x)^(3/2)*(a+b*ArcCosh(c*x))^2), x))/Sqrt(1-c^2*x^2))");

	}

	// {1/(x*(1-c^2*x^2)^(3/2)*(a+b*ArcCosh(c*x))^2), x, 1, -((Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(1/(x*(-1 +
	// c*x)^(3/2)*(1+c*x)^(3/2)*(a+b*ArcCosh(c*x))^2), x))/Sqrt(1-c^2*x^2))}
	public void test03067() {
		check("Integrate(1/(x*(1-c^2*x^2)^(3/2)*(a+b*ArcCosh(c*x))^2), x)",
				"-((Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(1/(x*(-1+c*x)^(3/2)*(1+c*x)^(3/2)*(a+b*ArcCosh(c*x))^2), x))/Sqrt(1-c^2*x^2))");

	}

	// {1/(x^2*(1-c^2*x^2)^(3/2)*(a+b*ArcCosh(c*x))^2), x, 1, -((Sqrt(-1+c*x)*Sqrt(1 +
	// c*x)*Unintegrable(1/(x^2*(-1+c*x)^(3/2)*(1+c*x)^(3/2)*(a+b*ArcCosh(c*x))^2), x))/Sqrt(1-c^2*x^2))}
	public void test03068() {
		check("Integrate(1/(x^2*(1-c^2*x^2)^(3/2)*(a+b*ArcCosh(c*x))^2), x)",
				"-((Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(1/(x^2*(-1+c*x)^(3/2)*(1+c*x)^(3/2)*(a+b*ArcCosh(c*x))^2), x))/Sqrt(1-c^2*x^2))");

	}

	// {x^3/((1-c^2*x^2)^(5/2)*(a+b*ArcCosh(c*x))^2), x, 1, (Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(x^3/((-1 +
	// c*x)^(5/2)*(1+c*x)^(5/2)*(a+b*ArcCosh(c*x))^2), x))/Sqrt(1-c^2*x^2)}
	public void test03069() {
		check("Integrate(x^3/((1-c^2*x^2)^(5/2)*(a+b*ArcCosh(c*x))^2), x)",
				"(Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(x^3/((-1+c*x)^(5/2)*(1+c*x)^(5/2)*(a+b*ArcCosh(c*x))^2), x))/Sqrt(1-c^2*x^2)");

	}

	// {x^2/((1-c^2*x^2)^(5/2)*(a+b*ArcCosh(c*x))^2), x, 1, (Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(x^2/((-1 +
	// c*x)^(5/2)*(1+c*x)^(5/2)*(a+b*ArcCosh(c*x))^2), x))/Sqrt(1-c^2*x^2)}
	public void test03070() {
		check("Integrate(x^2/((1-c^2*x^2)^(5/2)*(a+b*ArcCosh(c*x))^2), x)",
				"(Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(x^2/((-1+c*x)^(5/2)*(1+c*x)^(5/2)*(a+b*ArcCosh(c*x))^2), x))/Sqrt(1-c^2*x^2)");

	}

	// {x/((1-c^2*x^2)^(5/2)*(a+b*ArcCosh(c*x))^2), x, 1, (Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(x/((-1 +
	// c*x)^(5/2)*(1+c*x)^(5/2)*(a+b*ArcCosh(c*x))^2), x))/Sqrt(1-c^2*x^2)}
	public void test03071() {
		check("Integrate(x/((1-c^2*x^2)^(5/2)*(a+b*ArcCosh(c*x))^2), x)",
				"(Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(x/((-1+c*x)^(5/2)*(1+c*x)^(5/2)*(a+b*ArcCosh(c*x))^2), x))/Sqrt(1-c^2*x^2)");

	}

	// {1/(x*(1-c^2*x^2)^(5/2)*(a+b*ArcCosh(c*x))^2), x, 1, (Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(1/(x*(-1 +
	// c*x)^(5/2)*(1+c*x)^(5/2)*(a+b*ArcCosh(c*x))^2), x))/Sqrt(1-c^2*x^2)}
	public void test03072() {
		check("Integrate(1/(x*(1-c^2*x^2)^(5/2)*(a+b*ArcCosh(c*x))^2), x)",
				"(Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(1/(x*(-1+c*x)^(5/2)*(1+c*x)^(5/2)*(a+b*ArcCosh(c*x))^2), x))/Sqrt(1-c^2*x^2)");

	}

	// {1/(x^2*(1-c^2*x^2)^(5/2)*(a+b*ArcCosh(c*x))^2), x, 1, (Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(1/(x^2*(-1
	// +c*x)^(5/2)*(1+c*x)^(5/2)*(a+b*ArcCosh(c*x))^2), x))/Sqrt(1-c^2*x^2)}
	public void test03073() {
		check("Integrate(1/(x^2*(1-c^2*x^2)^(5/2)*(a+b*ArcCosh(c*x))^2), x)",
				"(Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(1/(x^2*(-1+c*x)^(5/2)*(1+c*x)^(5/2)*(a+b*ArcCosh(c*x))^2), x))/Sqrt(1-c^2*x^2)");

	}

	// {(x^m*(1-c^2*x^2)^(5/2))/(a+b*ArcCosh(c*x))^2, x, 1, (Sqrt(1-c^2*x^2)*Unintegrable((x^m*(-1+c*x)^(5/2)*(1
	// +c*x)^(5/2))/(a+b*ArcCosh(c*x))^2, x))/(Sqrt(-1+c*x)*Sqrt(1+c*x))}
	public void test03074() {
		check("Integrate((x^m*(1-c^2*x^2)^(5/2))/(a+b*ArcCosh(c*x))^2, x)",
				"(Sqrt(1-c^2*x^2)*Unintegrable((x^m*(-1+c*x)^(5/2)*(1+c*x)^(5/2))/(a+b*ArcCosh(c*x))^2, x))/(Sqrt(-1+c*x)*Sqrt(1+c*x))");

	}

	// {(x^m*(1-c^2*x^2)^(3/2))/(a+b*ArcCosh(c*x))^2, x, 1, -((Sqrt(1-c^2*x^2)*Unintegrable((x^m*(-1 +
	// c*x)^(3/2)*(1+c*x)^(3/2))/(a+b*ArcCosh(c*x))^2, x))/(Sqrt(-1+c*x)*Sqrt(1+c*x)))}
	public void test03075() {
		check("Integrate((x^m*(1-c^2*x^2)^(3/2))/(a+b*ArcCosh(c*x))^2, x)",
				"-((Sqrt(1-c^2*x^2)*Unintegrable((x^m*(-1+c*x)^(3/2)*(1+c*x)^(3/2))/(a+b*ArcCosh(c*x))^2, x))/(Sqrt(-1+c*x)*Sqrt(1+c*x)))");

	}

	// {(x^m*Sqrt(1-c^2*x^2))/(a+b*ArcCosh(c*x))^2, x, 1, (Sqrt(1-c^2*x^2)*Unintegrable((x^m*Sqrt(-1+c*x)*Sqrt(1
	// +c*x))/(a+b*ArcCosh(c*x))^2, x))/(Sqrt(-1+c*x)*Sqrt(1+c*x))}
	public void test03076() {
		check("Integrate((x^m*Sqrt(1-c^2*x^2))/(a+b*ArcCosh(c*x))^2, x)",
				"(Sqrt(1-c^2*x^2)*Unintegrable((x^m*Sqrt(-1+c*x)*Sqrt(1+c*x))/(a+b*ArcCosh(c*x))^2, x))/(Sqrt(-1+c*x)*Sqrt(1+c*x))");

	}

	// {x^m/((1-c^2*x^2)^(3/2)*(a+b*ArcCosh(c*x))^2), x, 1, -((Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(x^m/((-1 +
	// c*x)^(3/2)*(1+c*x)^(3/2)*(a+b*ArcCosh(c*x))^2), x))/Sqrt(1-c^2*x^2))}
	public void test03077() {
		check("Integrate(x^m/((1-c^2*x^2)^(3/2)*(a+b*ArcCosh(c*x))^2), x)",
				"-((Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(x^m/((-1+c*x)^(3/2)*(1+c*x)^(3/2)*(a+b*ArcCosh(c*x))^2), x))/Sqrt(1-c^2*x^2))");

	}

	// {x^m/((1-c^2*x^2)^(5/2)*(a+b*ArcCosh(c*x))^2), x, 1, (Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(x^m/((-1 +
	// c*x)^(5/2)*(1+c*x)^(5/2)*(a+b*ArcCosh(c*x))^2), x))/Sqrt(1-c^2*x^2)}
	public void test03078() {
		check("Integrate(x^m/((1-c^2*x^2)^(5/2)*(a+b*ArcCosh(c*x))^2), x)",
				"(Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(x^m/((-1+c*x)^(5/2)*(1+c*x)^(5/2)*(a+b*ArcCosh(c*x))^2), x))/Sqrt(1-c^2*x^2)");

	}

	// {(x^m*ArcCosh(a*x)^n)/Sqrt(1-a^2*x^2), x, 1, (Sqrt(-1+a*x)*Sqrt(1 +
	// a*x)*Unintegrable((x^m*ArcCosh(a*x)^n)/(Sqrt(-1+a*x)*Sqrt(1+a*x)), x))/Sqrt(1-a^2*x^2)}
	public void test03079() {
		check("Integrate((x^m*ArcCosh(a*x)^n)/Sqrt(1-a^2*x^2), x)",
				"(Sqrt(-1+a*x)*Sqrt(1+a*x)*Unintegrable((x^m*ArcCosh(a*x)^n)/(Sqrt(-1+a*x)*Sqrt(1+a*x)), x))/Sqrt(1-a^2*x^2)");

	}

	// {ArcCosh(a*x)^n/(x*Sqrt(1-a^2*x^2)), x, 1, (Sqrt(-1+a*x)*Sqrt(1+a*x)*Unintegrable(ArcCosh(a*x)^n/(x*Sqrt(-1
	// +a*x)*Sqrt(1+a*x)), x))/Sqrt(1-a^2*x^2)}
	public void test03080() {
		check("Integrate(ArcCosh(a*x)^n/(x*Sqrt(1-a^2*x^2)), x)",
				"(Sqrt(-1+a*x)*Sqrt(1+a*x)*Unintegrable(ArcCosh(a*x)^n/(x*Sqrt(-1+a*x)*Sqrt(1+a*x)), x))/Sqrt(1-a^2*x^2)");

	}

	// {ArcCosh(a*x)^n/(x^2*Sqrt(1-a^2*x^2)), x, 1, (Sqrt(-1+a*x)*Sqrt(1 +
	// a*x)*Unintegrable(ArcCosh(a*x)^n/(x^2*Sqrt(-1+a*x)*Sqrt(1+a*x)), x))/Sqrt(1-a^2*x^2)}
	public void test03081() {
		check("Integrate(ArcCosh(a*x)^n/(x^2*Sqrt(1-a^2*x^2)), x)",
				"(Sqrt(-1+a*x)*Sqrt(1+a*x)*Unintegrable(ArcCosh(a*x)^n/(x^2*Sqrt(-1+a*x)*Sqrt(1+a*x)), x))/Sqrt(1-a^2*x^2)");

	}

	// {1/((c-a^2*c*x^2)^(3/2)*Sqrt(ArcCosh(a*x))), x, 1, -((Sqrt(-1+a*x)*Sqrt(1+a*x)*Unintegrable(1/((-1 +
	// a*x)^(3/2)*(1+a*x)^(3/2)*Sqrt(ArcCosh(a*x))), x))/(c*Sqrt(c-a^2*c*x^2)))}
	public void test03082() {
		check("Integrate(1/((c-a^2*c*x^2)^(3/2)*Sqrt(ArcCosh(a*x))), x)",
				"-((Sqrt(-1+a*x)*Sqrt(1+a*x)*Unintegrable(1/((-1+a*x)^(3/2)*(1+a*x)^(3/2)*Sqrt(ArcCosh(a*x))), x))/(c*Sqrt(c-a^2*c*x^2)))");

	}

	// {1/((c-a^2*c*x^2)^(5/2)*Sqrt(ArcCosh(a*x))), x, 1, (Sqrt(-1+a*x)*Sqrt(1+a*x)*Unintegrable(1/((-1 +
	// a*x)^(5/2)*(1+a*x)^(5/2)*Sqrt(ArcCosh(a*x))), x))/(c^2*Sqrt(c-a^2*c*x^2))}
	public void test03083() {
		check("Integrate(1/((c-a^2*c*x^2)^(5/2)*Sqrt(ArcCosh(a*x))), x)",
				"(Sqrt(-1+a*x)*Sqrt(1+a*x)*Unintegrable(1/((-1+a*x)^(5/2)*(1+a*x)^(5/2)*Sqrt(ArcCosh(a*x))), x))/(c^2*Sqrt(c-a^2*c*x^2))");

	}

	// {(d+e*x)^m*(a+b*ArcCosh(c*x))^3, x, 1, ((d+e*x)^(1+m)*(a+b*ArcCosh(c*x))^3)/(e*(1+m)) -
	// (3*b*c*Unintegrable(((d+e*x)^(1+m)*(a+b*ArcCosh(c*x))^2)/(Sqrt(-1+c*x)*Sqrt(1+c*x)), x))/(e*(1+m))}
	public void test03084() {
		check("Integrate((d+e*x)^m*(a+b*ArcCosh(c*x))^3, x)",
				"((d+e*x)^(1+m)*(a+b*ArcCosh(c*x))^3)/(e*(1+m))-(3*b*c*Unintegrable(((d+e*x)^(1+m)*(a+b*ArcCosh(c*x))^2)/(Sqrt(-1+c*x)*Sqrt(1+c*x)), x))/(e*(1+m))");

	}

	// {(d+e*x)^m*(a+b*ArcCosh(c*x))^2, x, 1, ((d+e*x)^(1+m)*(a+b*ArcCosh(c*x))^2)/(e*(1+m)) -
	// (2*b*c*Unintegrable(((d+e*x)^(1+m)*(a+b*ArcCosh(c*x)))/(Sqrt(-1+c*x)*Sqrt(1+c*x)), x))/(e*(1+m))}
	public void test03085() {
		check("Integrate((d+e*x)^m*(a+b*ArcCosh(c*x))^2, x)",
				"((d+e*x)^(1+m)*(a+b*ArcCosh(c*x))^2)/(e*(1+m))-(2*b*c*Unintegrable(((d+e*x)^(1+m)*(a+b*ArcCosh(c*x)))/(Sqrt(-1+c*x)*Sqrt(1+c*x)), x))/(e*(1+m))");

	}

	// {((a+b*ArcCosh(c*x))^n*Log(h*(f+g*x)^m))/Sqrt(1-c^2*x^2), x, 1, (Sqrt(-1+c*x)*Sqrt(1 +
	// c*x)*Unintegrable(((a+b*ArcCosh(c*x))^n*Log(h*(f+g*x)^m))/(Sqrt(-1+c*x)*Sqrt(1+c*x)), x))/Sqrt(1 -
	// c^2*x^2)}
	public void test03086() {
		check("Integrate(((a+b*ArcCosh(c*x))^n*Log(h*(f+g*x)^m))/Sqrt(1-c^2*x^2), x)",
				"(Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(((a+b*ArcCosh(c*x))^n*Log(h*(f+g*x)^m))/(Sqrt(-1+c*x)*Sqrt(1+c*x)), x))/Sqrt(1-c^2*x^2)");

	}

	// {Log(h*(f+g*x)^m)/(Sqrt(1-c^2*x^2)*(a+b*ArcCosh(c*x))), x, 1, (Sqrt(-1+c*x)*Sqrt(1 +
	// c*x)*Unintegrable(Log(h*(f+g*x)^m)/(Sqrt(-1+c*x)*Sqrt(1+c*x)*(a+b*ArcCosh(c*x))), x))/Sqrt(1-c^2*x^2)}
	public void test03087() {
		check("Integrate(Log(h*(f+g*x)^m)/(Sqrt(1-c^2*x^2)*(a+b*ArcCosh(c*x))), x)",
				"(Sqrt(-1+c*x)*Sqrt(1+c*x)*Unintegrable(Log(h*(f+g*x)^m)/(Sqrt(-1+c*x)*Sqrt(1+c*x)*(a+b*ArcCosh(c*x))), x))/Sqrt(1-c^2*x^2)");

	}

	// {(c*e+d*e*x)^m/(a+b*ArcCosh(c+d*x)), x, 1, Unintegrable((e*(c+d*x))^m/(a+b*ArcCosh(c+d*x)), x)}
	public void test03088() {
		check("Integrate((c*e+d*e*x)^m/(a+b*ArcCosh(c+d*x)), x)",
				"Unintegrable((e*(c+d*x))^m/(a+b*ArcCosh(c+d*x)), x)");

	}

	// {(a+b*ArcCosh(1+d*x^2))^(-1), x, 1, (x*Cosh(a/(2*b))*CoshIntegral((a+b*ArcCosh(1 +
	// d*x^2))/(2*b)))/(Sqrt(2)*b*Sqrt(d*x^2))-(x*Sinh(a/(2*b))*SinhIntegral((a+b*ArcCosh(1 +
	// d*x^2))/(2*b)))/(Sqrt(2)*b*Sqrt(d*x^2))}
	public void test03089() {
		check("Integrate((a+b*ArcCosh(1+d*x^2))^(-1), x)",
				"(x*Cosh(a/(2*b))*CoshIntegral((a+b*ArcCosh(1+d*x^2))/(2*b)))/(Sqrt(2)*b*Sqrt(d*x^2))-(x*Sinh(a/(2*b))*SinhIntegral((a+b*ArcCosh(1+d*x^2))/(2*b)))/(Sqrt(2)*b*Sqrt(d*x^2))");

	}

	// {(a+b*ArcCosh(1+d*x^2))^(-2), x, 1, -(Sqrt(d*x^2)*Sqrt(2+d*x^2))/(2*b*d*x*(a+b*ArcCosh(1+d*x^2))) -
	// (x*CoshIntegral((a+b*ArcCosh(1+d*x^2))/(2*b))*Sinh(a/(2*b)))/(2*Sqrt(2)*b^2*Sqrt(d*x^2)) +
	// (x*Cosh(a/(2*b))*SinhIntegral((a+b*ArcCosh(1+d*x^2))/(2*b)))/(2*Sqrt(2)*b^2*Sqrt(d*x^2))}
	public void test03090() {
		check("Integrate((a+b*ArcCosh(1+d*x^2))^(-2), x)",
				"-(Sqrt(d*x^2)*Sqrt(2+d*x^2))/(2*b*d*x*(a+b*ArcCosh(1+d*x^2)))-(x*CoshIntegral((a+b*ArcCosh(1+d*x^2))/(2*b))*Sinh(a/(2*b)))/(2*Sqrt(2)*b^2*Sqrt(d*x^2))+(x*Cosh(a/(2*b))*SinhIntegral((a+b*ArcCosh(1+d*x^2))/(2*b)))/(2*Sqrt(2)*b^2*Sqrt(d*x^2))");

	}

	// {(a+b*ArcCosh(-1+d*x^2))^(-1), x, 1, -((x*CoshIntegral((a+b*ArcCosh(-1 +
	// d*x^2))/(2*b))*Sinh(a/(2*b)))/(Sqrt(2)*b*Sqrt(d*x^2)))+(x*Cosh(a/(2*b))*SinhIntegral((a+b*ArcCosh(-1 +
	// d*x^2))/(2*b)))/(Sqrt(2)*b*Sqrt(d*x^2))}
	public void test03091() {
		check("Integrate((a+b*ArcCosh(-1+d*x^2))^(-1), x)",
				"-((x*CoshIntegral((a+b*ArcCosh(-1+d*x^2))/(2*b))*Sinh(a/(2*b)))/(Sqrt(2)*b*Sqrt(d*x^2)))+(x*Cosh(a/(2*b))*SinhIntegral((a+b*ArcCosh(-1+d*x^2))/(2*b)))/(Sqrt(2)*b*Sqrt(d*x^2))");

	}

	// {(a+b*ArcCosh(-1+d*x^2))^(-2), x, 1, -(Sqrt(d*x^2)*Sqrt(-2+d*x^2))/(2*b*d*x*(a+b*ArcCosh(-1+d*x^2))) +
	// (x*Cosh(a/(2*b))*CoshIntegral((a+b*ArcCosh(-1+d*x^2))/(2*b)))/(2*Sqrt(2)*b^2*Sqrt(d*x^2)) -
	// (x*Sinh(a/(2*b))*SinhIntegral((a+b*ArcCosh(-1+d*x^2))/(2*b)))/(2*Sqrt(2)*b^2*Sqrt(d*x^2))}
	public void test03092() {
		check("Integrate((a+b*ArcCosh(-1+d*x^2))^(-2), x)",
				"-(Sqrt(d*x^2)*Sqrt(-2+d*x^2))/(2*b*d*x*(a+b*ArcCosh(-1+d*x^2)))+(x*Cosh(a/(2*b))*CoshIntegral((a+b*ArcCosh(-1+d*x^2))/(2*b)))/(2*Sqrt(2)*b^2*Sqrt(d*x^2))-(x*Sinh(a/(2*b))*SinhIntegral((a+b*ArcCosh(-1+d*x^2))/(2*b)))/(2*Sqrt(2)*b^2*Sqrt(d*x^2))");

	}

	// {Sqrt(a+b*ArcCosh(1+d*x^2)), x, 1, -((Sqrt(b)*Sqrt(Pi/2)*Erfi(Sqrt(a+b*ArcCosh(1 +
	// d*x^2))/(Sqrt(2)*Sqrt(b)))*(Cosh(a/(2*b))-Sinh(a/(2*b)))*Sinh(ArcCosh(1+d*x^2)/2))/(d*x)) +
	// (Sqrt(b)*Sqrt(Pi/2)*Erf(Sqrt(a+b*ArcCosh(1+d*x^2))/(Sqrt(2)*Sqrt(b)))*(Cosh(a/(2*b)) +
	// Sinh(a/(2*b)))*Sinh(ArcCosh(1+d*x^2)/2))/(d*x)+(2*Sqrt(a+b*ArcCosh(1+d*x^2))*Sinh(ArcCosh(1 +
	// d*x^2)/2)^2)/(d*x)}
	public void test03093() {
		check("Integrate(Sqrt(a+b*ArcCosh(1+d*x^2)), x)",
				"-((Sqrt(b)*Sqrt(Pi/2)*Erfi(Sqrt(a+b*ArcCosh(1+d*x^2))/(Sqrt(2)*Sqrt(b)))*(Cosh(a/(2*b))-Sinh(a/(2*b)))*Sinh(ArcCosh(1+d*x^2)/2))/(d*x))+(Sqrt(b)*Sqrt(Pi/2)*Erf(Sqrt(a+b*ArcCosh(1+d*x^2))/(Sqrt(2)*Sqrt(b)))*(Cosh(a/(2*b))+Sinh(a/(2*b)))*Sinh(ArcCosh(1+d*x^2)/2))/(d*x)+(2*Sqrt(a+b*ArcCosh(1+d*x^2))*Sinh(ArcCosh(1+d*x^2)/2)^2)/(d*x)");

	}

	// {1/Sqrt(a+b*ArcCosh(1+d*x^2)), x, 1, (Sqrt(Pi/2)*Erfi(Sqrt(a+b*ArcCosh(1 +
	// d*x^2))/(Sqrt(2)*Sqrt(b)))*(Cosh(a/(2*b))-Sinh(a/(2*b)))*Sinh(ArcCosh(1+d*x^2)/2))/(Sqrt(b)*d*x) +
	// (Sqrt(Pi/2)*Erf(Sqrt(a+b*ArcCosh(1+d*x^2))/(Sqrt(2)*Sqrt(b)))*(Cosh(a/(2*b))+Sinh(a/(2*b)))*Sinh(ArcCosh(1
	// +d*x^2)/2))/(Sqrt(b)*d*x)}
	public void test03094() {
		check("Integrate(1/Sqrt(a+b*ArcCosh(1+d*x^2)), x)",
				"(Sqrt(Pi/2)*Erfi(Sqrt(a+b*ArcCosh(1+d*x^2))/(Sqrt(2)*Sqrt(b)))*(Cosh(a/(2*b))-Sinh(a/(2*b)))*Sinh(ArcCosh(1+d*x^2)/2))/(Sqrt(b)*d*x)+(Sqrt(Pi/2)*Erf(Sqrt(a+b*ArcCosh(1+d*x^2))/(Sqrt(2)*Sqrt(b)))*(Cosh(a/(2*b))+Sinh(a/(2*b)))*Sinh(ArcCosh(1+d*x^2)/2))/(Sqrt(b)*d*x)");

	}

	// {(a+b*ArcCosh(1+d*x^2))^(-3/2), x, 1, -((Sqrt(d*x^2)*Sqrt(2+d*x^2))/(b*d*x*Sqrt(a+b*ArcCosh(1+d*x^2))))
	// +(Sqrt(Pi/2)*Erfi(Sqrt(a+b*ArcCosh(1+d*x^2))/(Sqrt(2)*Sqrt(b)))*(Cosh(a/(2*b)) -
	// Sinh(a/(2*b)))*Sinh(ArcCosh(1+d*x^2)/2))/(b^(3/2)*d*x)-(Sqrt(Pi/2)*Erf(Sqrt(a+b*ArcCosh(1 +
	// d*x^2))/(Sqrt(2)*Sqrt(b)))*(Cosh(a/(2*b))+Sinh(a/(2*b)))*Sinh(ArcCosh(1+d*x^2)/2))/(b^(3/2)*d*x)}
	public void test03095() {
		check("Integrate((a+b*ArcCosh(1+d*x^2))^(-3/2), x)",
				"-((Sqrt(d*x^2)*Sqrt(2+d*x^2))/(b*d*x*Sqrt(a+b*ArcCosh(1+d*x^2))))+(Sqrt(Pi/2)*Erfi(Sqrt(a+b*ArcCosh(1+d*x^2))/(Sqrt(2)*Sqrt(b)))*(Cosh(a/(2*b))-Sinh(a/(2*b)))*Sinh(ArcCosh(1+d*x^2)/2))/(b^(3/2)*d*x)-(Sqrt(Pi/2)*Erf(Sqrt(a+b*ArcCosh(1+d*x^2))/(Sqrt(2)*Sqrt(b)))*(Cosh(a/(2*b))+Sinh(a/(2*b)))*Sinh(ArcCosh(1+d*x^2)/2))/(b^(3/2)*d*x)");

	}

	// {Sqrt(a+b*ArcCosh(-1+d*x^2)), x, 1, (2*Sqrt(a+b*ArcCosh(-1+d*x^2))*Cosh(ArcCosh(-1+d*x^2)/2)^2)/(d*x) -
	// (Sqrt(b)*Sqrt(Pi/2)*Cosh(ArcCosh(-1+d*x^2)/2)*Erfi(Sqrt(a+b*ArcCosh(-1 +
	// d*x^2))/(Sqrt(2)*Sqrt(b)))*(Cosh(a/(2*b))-Sinh(a/(2*b))))/(d*x)-(Sqrt(b)*Sqrt(Pi/2)*Cosh(ArcCosh(-1 +
	// d*x^2)/2)*Erf(Sqrt(a+b*ArcCosh(-1+d*x^2))/(Sqrt(2)*Sqrt(b)))*(Cosh(a/(2*b))+Sinh(a/(2*b))))/(d*x)}
	public void test03096() {
		check("Integrate(Sqrt(a+b*ArcCosh(-1+d*x^2)), x)",
				"(2*Sqrt(a+b*ArcCosh(-1+d*x^2))*Cosh(ArcCosh(-1+d*x^2)/2)^2)/(d*x)-(Sqrt(b)*Sqrt(Pi/2)*Cosh(ArcCosh(-1+d*x^2)/2)*Erfi(Sqrt(a+b*ArcCosh(-1+d*x^2))/(Sqrt(2)*Sqrt(b)))*(Cosh(a/(2*b))-Sinh(a/(2*b))))/(d*x)-(Sqrt(b)*Sqrt(Pi/2)*Cosh(ArcCosh(-1+d*x^2)/2)*Erf(Sqrt(a+b*ArcCosh(-1+d*x^2))/(Sqrt(2)*Sqrt(b)))*(Cosh(a/(2*b))+Sinh(a/(2*b))))/(d*x)");

	}

	// {1/Sqrt(a+b*ArcCosh(-1+d*x^2)), x, 1, (Sqrt(Pi/2)*Cosh(ArcCosh(-1+d*x^2)/2)*Erfi(Sqrt(a+b*ArcCosh(-1 +
	// d*x^2))/(Sqrt(2)*Sqrt(b)))*(Cosh(a/(2*b))-Sinh(a/(2*b))))/(Sqrt(b)*d*x)-(Sqrt(Pi/2)*Cosh(ArcCosh(-1 +
	// d*x^2)/2)*Erf(Sqrt(a+b*ArcCosh(-1+d*x^2))/(Sqrt(2)*Sqrt(b)))*(Cosh(a/(2*b))+Sinh(a/(2*b))))/(Sqrt(b)*d*x)}
	public void test03097() {
		check("Integrate(1/Sqrt(a+b*ArcCosh(-1+d*x^2)), x)",
				"(Sqrt(Pi/2)*Cosh(ArcCosh(-1+d*x^2)/2)*Erfi(Sqrt(a+b*ArcCosh(-1+d*x^2))/(Sqrt(2)*Sqrt(b)))*(Cosh(a/(2*b))-Sinh(a/(2*b))))/(Sqrt(b)*d*x)-(Sqrt(Pi/2)*Cosh(ArcCosh(-1+d*x^2)/2)*Erf(Sqrt(a+b*ArcCosh(-1+d*x^2))/(Sqrt(2)*Sqrt(b)))*(Cosh(a/(2*b))+Sinh(a/(2*b))))/(Sqrt(b)*d*x)");

	}

	// {(a+b*ArcCosh(-1+d*x^2))^(-3/2), x, 1, -((Sqrt(d*x^2)*Sqrt(-2+d*x^2))/(b*d*x*Sqrt(a+b*ArcCosh(-1 +
	// d*x^2))))+(Sqrt(Pi/2)*Cosh(ArcCosh(-1+d*x^2)/2)*Erfi(Sqrt(a+b*ArcCosh(-1 +
	// d*x^2))/(Sqrt(2)*Sqrt(b)))*(Cosh(a/(2*b))-Sinh(a/(2*b))))/(b^(3/2)*d*x)+(Sqrt(Pi/2)*Cosh(ArcCosh(-1 +
	// d*x^2)/2)*Erf(Sqrt(a+b*ArcCosh(-1+d*x^2))/(Sqrt(2)*Sqrt(b)))*(Cosh(a/(2*b))+Sinh(a/(2*b))))/(b^(3/2)*d*x)}
	public void test03098() {
		check("Integrate((a+b*ArcCosh(-1+d*x^2))^(-3/2), x)",
				"-((Sqrt(d*x^2)*Sqrt(-2+d*x^2))/(b*d*x*Sqrt(a+b*ArcCosh(-1+d*x^2))))+(Sqrt(Pi/2)*Cosh(ArcCosh(-1+d*x^2)/2)*Erfi(Sqrt(a+b*ArcCosh(-1+d*x^2))/(Sqrt(2)*Sqrt(b)))*(Cosh(a/(2*b))-Sinh(a/(2*b))))/(b^(3/2)*d*x)+(Sqrt(Pi/2)*Cosh(ArcCosh(-1+d*x^2)/2)*Erf(Sqrt(a+b*ArcCosh(-1+d*x^2))/(Sqrt(2)*Sqrt(b)))*(Cosh(a/(2*b))+Sinh(a/(2*b))))/(b^(3/2)*d*x)");

	}

	// {(a+b*ArcTanh(c*x))/x, x, 1, a*Log(x)-(b*PolyLog(2, -(c*x)))/2+(b*PolyLog(2, c*x))/2}
	public void test03099() {
		check("Integrate((a+b*ArcTanh(c*x))/x, x)", "a*Log(x)-(b*PolyLog(2, -(c*x)))/2+(b*PolyLog(2, c*x))/2");

	}

	// {(a+b*ArcTanh(c*x))^2/(d+e*x), x, 1, -(((a+b*ArcTanh(c*x))^2*Log(2/(1+c*x)))/e)+((a +
	// b*ArcTanh(c*x))^2*Log((2*c*(d+e*x))/((c*d+e)*(1+c*x))))/e+(b*(a+b*ArcTanh(c*x))*PolyLog(2, 1-2/(1 +
	// c*x)))/e-(b*(a+b*ArcTanh(c*x))*PolyLog(2, 1-(2*c*(d+e*x))/((c*d+e)*(1+c*x))))/e+(b^2*PolyLog(3, 1 -
	// 2/(1+c*x)))/(2*e)-(b^2*PolyLog(3, 1-(2*c*(d+e*x))/((c*d+e)*(1+c*x))))/(2*e)}
	public void test03100() {
		check("Integrate((a+b*ArcTanh(c*x))^2/(d+e*x), x)",
				"-(((a+b*ArcTanh(c*x))^2*Log(2/(1+c*x)))/e)+((a+b*ArcTanh(c*x))^2*Log((2*c*(d+e*x))/((c*d+e)*(1+c*x))))/e+(b*(a+b*ArcTanh(c*x))*PolyLog(2, 1-2/(1+c*x)))/e-(b*(a+b*ArcTanh(c*x))*PolyLog(2, 1-(2*c*(d+e*x))/((c*d+e)*(1+c*x))))/e+(b^2*PolyLog(3, 1-2/(1+c*x)))/(2*e)-(b^2*PolyLog(3, 1-(2*c*(d+e*x))/((c*d+e)*(1+c*x))))/(2*e)");

	}

	// {(a+b*ArcTanh(c*x))^3/(d+e*x), x, 1, -(((a+b*ArcTanh(c*x))^3*Log(2/(1+c*x)))/e)+((a +
	// b*ArcTanh(c*x))^3*Log((2*c*(d+e*x))/((c*d+e)*(1+c*x))))/e+(3*b*(a+b*ArcTanh(c*x))^2*PolyLog(2, 1-2/(1
	// +c*x)))/(2*e)-(3*b*(a+b*ArcTanh(c*x))^2*PolyLog(2, 1-(2*c*(d+e*x))/((c*d+e)*(1+c*x))))/(2*e) +
	// (3*b^2*(a+b*ArcTanh(c*x))*PolyLog(3, 1-2/(1+c*x)))/(2*e)-(3*b^2*(a+b*ArcTanh(c*x))*PolyLog(3, 1 -
	// (2*c*(d+e*x))/((c*d+e)*(1+c*x))))/(2*e)+(3*b^3*PolyLog(4, 1-2/(1+c*x)))/(4*e)-(3*b^3*PolyLog(4, 1 -
	// (2*c*(d+e*x))/((c*d+e)*(1+c*x))))/(4*e)}
	public void test03101() {
		check("Integrate((a+b*ArcTanh(c*x))^3/(d+e*x), x)",
				"-(((a+b*ArcTanh(c*x))^3*Log(2/(1+c*x)))/e)+((a+b*ArcTanh(c*x))^3*Log((2*c*(d+e*x))/((c*d+e)*(1+c*x))))/e+(3*b*(a+b*ArcTanh(c*x))^2*PolyLog(2, 1-2/(1+c*x)))/(2*e)-(3*b*(a+b*ArcTanh(c*x))^2*PolyLog(2, 1-(2*c*(d+e*x))/((c*d+e)*(1+c*x))))/(2*e)+(3*b^2*(a+b*ArcTanh(c*x))*PolyLog(3, 1-2/(1+c*x)))/(2*e)-(3*b^2*(a+b*ArcTanh(c*x))*PolyLog(3, 1-(2*c*(d+e*x))/((c*d+e)*(1+c*x))))/(2*e)+(3*b^3*PolyLog(4, 1-2/(1+c*x)))/(4*e)-(3*b^3*PolyLog(4, 1-(2*c*(d+e*x))/((c*d+e)*(1+c*x))))/(4*e)");

	}

	// {(a+b*ArcTanh(c*x))^2/(d+e*x), x, 1, -(((a+b*ArcTanh(c*x))^2*Log(2/(1+c*x)))/e)+((a +
	// b*ArcTanh(c*x))^2*Log((2*c*(d+e*x))/((c*d+e)*(1+c*x))))/e+(b*(a+b*ArcTanh(c*x))*PolyLog(2, 1-2/(1 +
	// c*x)))/e-(b*(a+b*ArcTanh(c*x))*PolyLog(2, 1-(2*c*(d+e*x))/((c*d+e)*(1+c*x))))/e+(b^2*PolyLog(3, 1 -
	// 2/(1+c*x)))/(2*e)-(b^2*PolyLog(3, 1-(2*c*(d+e*x))/((c*d+e)*(1+c*x))))/(2*e)}
	public void test03102() {
		check("Integrate((a+b*ArcTanh(c*x))^2/(d+e*x), x)",
				"-(((a+b*ArcTanh(c*x))^2*Log(2/(1+c*x)))/e)+((a+b*ArcTanh(c*x))^2*Log((2*c*(d+e*x))/((c*d+e)*(1+c*x))))/e+(b*(a+b*ArcTanh(c*x))*PolyLog(2, 1-2/(1+c*x)))/e-(b*(a+b*ArcTanh(c*x))*PolyLog(2, 1-(2*c*(d+e*x))/((c*d+e)*(1+c*x))))/e+(b^2*PolyLog(3, 1-2/(1+c*x)))/(2*e)-(b^2*PolyLog(3, 1-(2*c*(d+e*x))/((c*d+e)*(1+c*x))))/(2*e)");

	}

	// {ArcTanh(a*x)/(1-a^2*x^2), x, 1, ArcTanh(a*x)^2/(2*a)}
	public void test03103() {
		check("Integrate(ArcTanh(a*x)/(1-a^2*x^2), x)", "ArcTanh(a*x)^2/(2*a)");

	}

	// {ArcTanh(a*x)^2/(1-a^2*x^2), x, 1, ArcTanh(a*x)^3/(3*a)}
	public void test03104() {
		check("Integrate(ArcTanh(a*x)^2/(1-a^2*x^2), x)", "ArcTanh(a*x)^3/(3*a)");

	}

	// {ArcTanh(a*x)^3/(1-a^2*x^2), x, 1, ArcTanh(a*x)^4/(4*a)}
	public void test03105() {
		check("Integrate(ArcTanh(a*x)^3/(1-a^2*x^2), x)", "ArcTanh(a*x)^4/(4*a)");

	}

	// {Sqrt(ArcTanh(a*x))/(1-a^2*x^2), x, 1, (2*ArcTanh(a*x)^(3/2))/(3*a)}
	public void test03106() {
		check("Integrate(Sqrt(ArcTanh(a*x))/(1-a^2*x^2), x)", "(2*ArcTanh(a*x)^(3/2))/(3*a)");

	}

	// {1/((1-a^2*x^2)*ArcTanh(a*x)), x, 1, Log(ArcTanh(a*x))/a}
	public void test03107() {
		check("Integrate(1/((1-a^2*x^2)*ArcTanh(a*x)), x)", "Log(ArcTanh(a*x))/a");

	}

	// {x/((1-a^2*x^2)*ArcTanh(a*x)^2), x, 1, -(x/(a*ArcTanh(a*x)))+Unintegrable(ArcTanh(a*x)^(-1), x)/a}
	public void test03108() {
		check("Integrate(x/((1-a^2*x^2)*ArcTanh(a*x)^2), x)",
				"-(x/(a*ArcTanh(a*x)))+Unintegrable(ArcTanh(a*x)^(-1), x)/a");

	}

	// {1/((1-a^2*x^2)*ArcTanh(a*x)^2), x, 1, -(1/(a*ArcTanh(a*x)))}
	public void test03109() {
		check("Integrate(1/((1-a^2*x^2)*ArcTanh(a*x)^2), x)", "-(1/(a*ArcTanh(a*x)))");

	}

	// {1/(x*(1-a^2*x^2)*ArcTanh(a*x)^2), x, 1, -(1/(a*x*ArcTanh(a*x)))-Unintegrable(1/(x^2*ArcTanh(a*x)), x)/a}
	public void test03110() {
		check("Integrate(1/(x*(1-a^2*x^2)*ArcTanh(a*x)^2), x)",
				"-(1/(a*x*ArcTanh(a*x)))-Unintegrable(1/(x^2*ArcTanh(a*x)), x)/a");

	}

	// {x/((1-a^2*x^2)*ArcTanh(a*x)^3), x, 1, -x/(2*a*ArcTanh(a*x)^2)+Unintegrable(ArcTanh(a*x)^(-2), x)/(2*a)}
	public void test03111() {
		check("Integrate(x/((1-a^2*x^2)*ArcTanh(a*x)^3), x)",
				"-x/(2*a*ArcTanh(a*x)^2)+Unintegrable(ArcTanh(a*x)^(-2), x)/(2*a)");

	}

	// {1/((1-a^2*x^2)*ArcTanh(a*x)^3), x, 1, -1/(2*a*ArcTanh(a*x)^2)}
	public void test03112() {
		check("Integrate(1/((1-a^2*x^2)*ArcTanh(a*x)^3), x)", "-1/(2*a*ArcTanh(a*x)^2)");

	}

	// {1/(x*(1-a^2*x^2)*ArcTanh(a*x)^3), x, 1, -1/(2*a*x*ArcTanh(a*x)^2)-Unintegrable(1/(x^2*ArcTanh(a*x)^2),
	// x)/(2*a)}
	public void test03113() {
		check("Integrate(1/(x*(1-a^2*x^2)*ArcTanh(a*x)^3), x)",
				"-1/(2*a*x*ArcTanh(a*x)^2)-Unintegrable(1/(x^2*ArcTanh(a*x)^2), x)/(2*a)");

	}

	// {ArcTanh(a*x)^p/(1-a^2*x^2), x, 1, ArcTanh(a*x)^(1+p)/(a*(1+p))}
	public void test03114() {
		check("Integrate(ArcTanh(a*x)^p/(1-a^2*x^2), x)", "ArcTanh(a*x)^(1+p)/(a*(1+p))");

	}

	// {ArcTanh(a*x)/Sqrt(1-a^2*x^2), x, 1, (-2*ArcTan(Sqrt(1-a*x)/Sqrt(1+a*x))*ArcTanh(a*x))/a-(I*PolyLog(2,
	// ((-I)*Sqrt(1-a*x))/Sqrt(1+a*x)))/a+(I*PolyLog(2, (I*Sqrt(1-a*x))/Sqrt(1+a*x)))/a}
	public void test03115() {
		check("Integrate(ArcTanh(a*x)/Sqrt(1-a^2*x^2), x)",
				"(-2*ArcTan(Sqrt(1-a*x)/Sqrt(1+a*x))*ArcTanh(a*x))/a-(I*PolyLog(2, ((-I)*Sqrt(1-a*x))/Sqrt(1+a*x)))/a+(I*PolyLog(2, (I*Sqrt(1-a*x))/Sqrt(1+a*x)))/a");

	}

	// {ArcTanh(a*x)/(x*Sqrt(1-a^2*x^2)), x, 1, -2*ArcTanh(a*x)*ArcTanh(Sqrt(1-a*x)/Sqrt(1+a*x))+PolyLog(2,
	// -(Sqrt(1-a*x)/Sqrt(1+a*x)))-PolyLog(2, Sqrt(1-a*x)/Sqrt(1+a*x))}
	public void test03116() {
		check("Integrate(ArcTanh(a*x)/(x*Sqrt(1-a^2*x^2)), x)",
				"-2*ArcTanh(a*x)*ArcTanh(Sqrt(1-a*x)/Sqrt(1+a*x))+PolyLog(2, -(Sqrt(1-a*x)/Sqrt(1+a*x)))-PolyLog(2, Sqrt(1-a*x)/Sqrt(1+a*x))");

	}

	// {ArcTanh(a*x)/(1-a^2*x^2)^(3/2), x, 1, -(1/(a*Sqrt(1-a^2*x^2)))+(x*ArcTanh(a*x))/Sqrt(1-a^2*x^2)}
	public void test03117() {
		check("Integrate(ArcTanh(a*x)/(1-a^2*x^2)^(3/2), x)",
				"-(1/(a*Sqrt(1-a^2*x^2)))+(x*ArcTanh(a*x))/Sqrt(1-a^2*x^2)");

	}

	// {ArcTanh(a*x)/(c-a^2*c*x^2)^(3/2), x, 1, -(1/(a*c*Sqrt(c-a^2*c*x^2)))+(x*ArcTanh(a*x))/(c*Sqrt(c -
	// a^2*c*x^2))}
	public void test03118() {
		check("Integrate(ArcTanh(a*x)/(c-a^2*c*x^2)^(3/2), x)",
				"-(1/(a*c*Sqrt(c-a^2*c*x^2)))+(x*ArcTanh(a*x))/(c*Sqrt(c-a^2*c*x^2))");

	}

	// {1/((a-a*x^2)*(b-2*b*ArcTanh(x))), x, 1, -Log(1-2*ArcTanh(x))/(2*a*b)}
	public void test03119() {
		check("Integrate(1/((a-a*x^2)*(b-2*b*ArcTanh(x))), x)", "-Log(1-2*ArcTanh(x))/(2*a*b)");

	}

	// {ArcTanh(x)/(a-a*x^2)^(3/2), x, 1, -(1/(a*Sqrt(a-a*x^2)))+(x*ArcTanh(x))/(a*Sqrt(a-a*x^2))}
	public void test03120() {
		check("Integrate(ArcTanh(x)/(a-a*x^2)^(3/2), x)", "-(1/(a*Sqrt(a-a*x^2)))+(x*ArcTanh(x))/(a*Sqrt(a-a*x^2))");

	}

	// {(e+f*x)^m*(a+b*ArcTanh(c+d*x))^3, x, 1, Unintegrable((e+f*x)^m*(a+b*ArcTanh(c+d*x))^3, x)}
	public void test03121() {
		check("Integrate((e+f*x)^m*(a+b*ArcTanh(c+d*x))^3, x)", "Unintegrable((e+f*x)^m*(a+b*ArcTanh(c+d*x))^3, x)");

	}

	// {(e+f*x)^m*(a+b*ArcTanh(c+d*x))^2, x, 1, Unintegrable((e+f*x)^m*(a+b*ArcTanh(c+d*x))^2, x)}
	public void test03122() {
		check("Integrate((e+f*x)^m*(a+b*ArcTanh(c+d*x))^2, x)", "Unintegrable((e+f*x)^m*(a+b*ArcTanh(c+d*x))^2, x)");

	}

	// {ArcTanh(Tanh(a+b*x))^2/x^4, x, 1, ArcTanh(Tanh(a+b*x))^3/(3*x^3*(b*x-ArcTanh(Tanh(a+b*x))))}
	public void test03123() {
		check("Integrate(ArcTanh(Tanh(a+b*x))^2/x^4, x)", "ArcTanh(Tanh(a+b*x))^3/(3*x^3*(b*x-ArcTanh(Tanh(a+b*x))))");

	}

	// {ArcTanh(Tanh(a+b*x))^3/x^5, x, 1, ArcTanh(Tanh(a+b*x))^4/(4*x^4*(b*x-ArcTanh(Tanh(a+b*x))))}
	public void test03124() {
		check("Integrate(ArcTanh(Tanh(a+b*x))^3/x^5, x)", "ArcTanh(Tanh(a+b*x))^4/(4*x^4*(b*x-ArcTanh(Tanh(a+b*x))))");

	}

	// {ArcTanh(Tanh(a+b*x))^4/x^6, x, 1, ArcTanh(Tanh(a+b*x))^5/(5*x^5*(b*x-ArcTanh(Tanh(a+b*x))))}
	public void test03125() {
		check("Integrate(ArcTanh(Tanh(a+b*x))^4/x^6, x)", "ArcTanh(Tanh(a+b*x))^5/(5*x^5*(b*x-ArcTanh(Tanh(a+b*x))))");

	}

	// {x^m/ArcTanh(Tanh(a+b*x)), x, 1, -((x^(1+m)*Hypergeometric2F1(1, 1+m, 2+m, (b*x)/(b*x-ArcTanh(Tanh(a +
	// b*x)))))/((1+m)*(b*x-ArcTanh(Tanh(a+b*x)))))}
	public void test03126() {
		check("Integrate(x^m/ArcTanh(Tanh(a+b*x)), x)",
				"-((x^(1+m)*Hypergeometric2F1(1, 1+m, 2+m, (b*x)/(b*x-ArcTanh(Tanh(a+b*x)))))/((1+m)*(b*x-ArcTanh(Tanh(a+b*x)))))");

	}

	// {1/(x*Sqrt(ArcTanh(Tanh(a+b*x)))), x, 1, (2*ArcTan(Sqrt(ArcTanh(Tanh(a+b*x)))/Sqrt(b*x-ArcTanh(Tanh(a +
	// b*x)))))/Sqrt(b*x-ArcTanh(Tanh(a+b*x)))}
	public void test03127() {
		check("Integrate(1/(x*Sqrt(ArcTanh(Tanh(a+b*x)))), x)",
				"(2*ArcTan(Sqrt(ArcTanh(Tanh(a+b*x)))/Sqrt(b*x-ArcTanh(Tanh(a+b*x)))))/Sqrt(b*x-ArcTanh(Tanh(a+b*x)))");

	}

	// {1/(Sqrt(x)*ArcTanh(Tanh(a+b*x))), x, 1, (-2*ArcTanh((Sqrt(b)*Sqrt(x))/Sqrt(b*x-ArcTanh(Tanh(a +
	// b*x)))))/(Sqrt(b)*Sqrt(b*x-ArcTanh(Tanh(a+b*x))))}
	public void test03128() {
		check("Integrate(1/(Sqrt(x)*ArcTanh(Tanh(a+b*x))), x)",
				"(-2*ArcTanh((Sqrt(b)*Sqrt(x))/Sqrt(b*x-ArcTanh(Tanh(a+b*x)))))/(Sqrt(b)*Sqrt(b*x-ArcTanh(Tanh(a+b*x))))");

	}

	// {Sqrt(ArcTanh(Tanh(a+b*x)))/x^(5/2), x, 1, (2*ArcTanh(Tanh(a+b*x))^(3/2))/(3*x^(3/2)*(b*x-ArcTanh(Tanh(a +
	// b*x))))}
	public void test03129() {
		check("Integrate(Sqrt(ArcTanh(Tanh(a+b*x)))/x^(5/2), x)",
				"(2*ArcTanh(Tanh(a+b*x))^(3/2))/(3*x^(3/2)*(b*x-ArcTanh(Tanh(a+b*x))))");

	}

	// {ArcTanh(Tanh(a+b*x))^(3/2)/x^(7/2), x, 1, (2*ArcTanh(Tanh(a+b*x))^(5/2))/(5*x^(5/2)*(b*x-ArcTanh(Tanh(a +
	// b*x))))}
	public void test03130() {
		check("Integrate(ArcTanh(Tanh(a+b*x))^(3/2)/x^(7/2), x)",
				"(2*ArcTanh(Tanh(a+b*x))^(5/2))/(5*x^(5/2)*(b*x-ArcTanh(Tanh(a+b*x))))");

	}

	// {ArcTanh(Tanh(a+b*x))^(5/2)/x^(9/2), x, 1, (2*ArcTanh(Tanh(a+b*x))^(7/2))/(7*x^(7/2)*(b*x-ArcTanh(Tanh(a +
	// b*x))))}
	public void test03131() {
		check("Integrate(ArcTanh(Tanh(a+b*x))^(5/2)/x^(9/2), x)",
				"(2*ArcTanh(Tanh(a+b*x))^(7/2))/(7*x^(7/2)*(b*x-ArcTanh(Tanh(a+b*x))))");

	}

	// {1/(Sqrt(x)*Sqrt(ArcTanh(Tanh(a+b*x)))), x, 1, (2*ArcTanh((Sqrt(b)*Sqrt(x))/Sqrt(ArcTanh(Tanh(a +
	// b*x)))))/Sqrt(b)}
	public void test03132() {
		check("Integrate(1/(Sqrt(x)*Sqrt(ArcTanh(Tanh(a+b*x)))), x)",
				"(2*ArcTanh((Sqrt(b)*Sqrt(x))/Sqrt(ArcTanh(Tanh(a+b*x)))))/Sqrt(b)");

	}

	// {1/(x^(3/2)*Sqrt(ArcTanh(Tanh(a+b*x)))), x, 1, (2*Sqrt(ArcTanh(Tanh(a+b*x))))/(Sqrt(x)*(b*x-ArcTanh(Tanh(a
	// +b*x))))}
	public void test03133() {
		check("Integrate(1/(x^(3/2)*Sqrt(ArcTanh(Tanh(a+b*x)))), x)",
				"(2*Sqrt(ArcTanh(Tanh(a+b*x))))/(Sqrt(x)*(b*x-ArcTanh(Tanh(a+b*x))))");

	}

	// {1/(Sqrt(x)*ArcTanh(Tanh(a+b*x))^(3/2)), x, 1, (-2*Sqrt(x))/((b*x-ArcTanh(Tanh(a+b*x)))*Sqrt(ArcTanh(Tanh(a
	// +b*x))))}
	public void test03134() {
		check("Integrate(1/(Sqrt(x)*ArcTanh(Tanh(a+b*x))^(3/2)), x)",
				"(-2*Sqrt(x))/((b*x-ArcTanh(Tanh(a+b*x)))*Sqrt(ArcTanh(Tanh(a+b*x))))");

	}

	// {Sqrt(x)/ArcTanh(Tanh(a+b*x))^(5/2), x, 1, (-2*x^(3/2))/(3*(b*x-ArcTanh(Tanh(a+b*x)))*ArcTanh(Tanh(a +
	// b*x))^(3/2))}
	public void test03135() {
		check("Integrate(Sqrt(x)/ArcTanh(Tanh(a+b*x))^(5/2), x)",
				"(-2*x^(3/2))/(3*(b*x-ArcTanh(Tanh(a+b*x)))*ArcTanh(Tanh(a+b*x))^(3/2))");

	}

	// {x^m*ArcTanh(Tanh(a+b*x))^n, x, 1, (x^m*ArcTanh(Tanh(a+b*x))^(1+n)*Hypergeometric2F1(-m, 1+n, 2+n,
	// -(ArcTanh(Tanh(a+b*x))/(b*x-ArcTanh(Tanh(a+b*x))))))/(b*(1+n)*((b*x)/(b*x-ArcTanh(Tanh(a+b*x))))^m)}
	public void test03136() {
		check("Integrate(x^m*ArcTanh(Tanh(a+b*x))^n, x)",
				"(x^m*ArcTanh(Tanh(a+b*x))^(1+n)*Hypergeometric2F1(-m, 1+n, 2+n, -(ArcTanh(Tanh(a+b*x))/(b*x-ArcTanh(Tanh(a+b*x))))))/(b*(1+n)*((b*x)/(b*x-ArcTanh(Tanh(a+b*x))))^m)");

	}

	// {ArcTanh(Tanh(a+b*x))^n/x, x, 1, (ArcTanh(Tanh(a+b*x))^(1+n)*Hypergeometric2F1(1, 1+n, 2+n,
	// -(ArcTanh(Tanh(a+b*x))/(b*x-ArcTanh(Tanh(a+b*x))))))/((1+n)*(b*x-ArcTanh(Tanh(a+b*x))))}
	public void test03137() {
		check("Integrate(ArcTanh(Tanh(a+b*x))^n/x, x)",
				"(ArcTanh(Tanh(a+b*x))^(1+n)*Hypergeometric2F1(1, 1+n, 2+n, -(ArcTanh(Tanh(a+b*x))/(b*x-ArcTanh(Tanh(a+b*x))))))/((1+n)*(b*x-ArcTanh(Tanh(a+b*x))))");

	}

	// {E^ArcTanh(a*x)/(c-a^2*c*x^2), x, 1, E^ArcTanh(a*x)/(a*c)}
	public void test03138() {
		check("Integrate(E^ArcTanh(a*x)/(c-a^2*c*x^2), x)", "E^ArcTanh(a*x)/(a*c)");

	}

	// {E^(3*ArcTanh(a*x))/(c-a^2*c*x^2), x, 1, E^(3*ArcTanh(a*x))/(3*a*c)}
	public void test03139() {
		check("Integrate(E^(3*ArcTanh(a*x))/(c-a^2*c*x^2), x)", "E^(3*ArcTanh(a*x))/(3*a*c)");

	}

	// {1/(E^ArcTanh(a*x)*(c-a^2*c*x^2)), x, 1, -(1/(a*c*E^ArcTanh(a*x)))}
	public void test03140() {
		check("Integrate(1/(E^ArcTanh(a*x)*(c-a^2*c*x^2)), x)", "-(1/(a*c*E^ArcTanh(a*x)))");

	}

	// {1/(E^(3*ArcTanh(a*x))*(c-a^2*c*x^2)), x, 1, -1/(3*a*c*E^(3*ArcTanh(a*x)))}
	public void test03141() {
		check("Integrate(1/(E^(3*ArcTanh(a*x))*(c-a^2*c*x^2)), x)", "-1/(3*a*c*E^(3*ArcTanh(a*x)))");

	}

	// {E^(ArcTanh(a*x)/2)/(1-a^2*x^2)^(3/2), x, 1, (-2*E^(ArcTanh(a*x)/2)*(1-2*a*x))/(3*a*Sqrt(1-a^2*x^2))}
	public void test03142() {
		check("Integrate(E^(ArcTanh(a*x)/2)/(1-a^2*x^2)^(3/2), x)",
				"(-2*E^(ArcTanh(a*x)/2)*(1-2*a*x))/(3*a*Sqrt(1-a^2*x^2))");

	}

	// {E^(ArcTanh(a*x)/2)/(c-a^2*c*x^2)^(3/2), x, 1, (-2*E^(ArcTanh(a*x)/2)*(1-2*a*x))/(3*a*c*Sqrt(c-a^2*c*x^2))}
	public void test03143() {
		check("Integrate(E^(ArcTanh(a*x)/2)/(c-a^2*c*x^2)^(3/2), x)",
				"(-2*E^(ArcTanh(a*x)/2)*(1-2*a*x))/(3*a*c*Sqrt(c-a^2*c*x^2))");

	}

	// {(E^(ArcTanh(a*x)/2)*x^2)/(c-a^2*c*x^2)^(9/8), x, 1, (4*E^(ArcTanh(a*x)/2)*(2-a*x))/(3*a^3*c*(c -
	// a^2*c*x^2)^(1/8))}
	public void test03144() {
		check("Integrate((E^(ArcTanh(a*x)/2)*x^2)/(c-a^2*c*x^2)^(9/8), x)",
				"(4*E^(ArcTanh(a*x)/2)*(2-a*x))/(3*a^3*c*(c-a^2*c*x^2)^(1/8))");

	}

	// {E^(n*ArcTanh(a*x))/(c-a^2*c*x^2), x, 1, E^(n*ArcTanh(a*x))/(a*c*n)}
	public void test03145() {
		check("Integrate(E^(n*ArcTanh(a*x))/(c-a^2*c*x^2), x)", "E^(n*ArcTanh(a*x))/(a*c*n)");

	}

	// {(E^(n*ArcTanh(a*x))*x)/(c-a^2*c*x^2)^(3/2), x, 1, (E^(n*ArcTanh(a*x))*(1-a*n*x))/(a^2*c*(1-n^2)*Sqrt(c -
	// a^2*c*x^2))}
	public void test03146() {
		check("Integrate((E^(n*ArcTanh(a*x))*x)/(c-a^2*c*x^2)^(3/2), x)",
				"(E^(n*ArcTanh(a*x))*(1-a*n*x))/(a^2*c*(1-n^2)*Sqrt(c-a^2*c*x^2))");

	}

	// {E^(n*ArcTanh(a*x))/(c-a^2*c*x^2)^(3/2), x, 1, -((E^(n*ArcTanh(a*x))*(n-a*x))/(a*c*(1-n^2)*Sqrt(c -
	// a^2*c*x^2)))}
	public void test03147() {
		check("Integrate(E^(n*ArcTanh(a*x))/(c-a^2*c*x^2)^(3/2), x)",
				"-((E^(n*ArcTanh(a*x))*(n-a*x))/(a*c*(1-n^2)*Sqrt(c-a^2*c*x^2)))");

	}

	// {E^(n*ArcTanh(a*x))*x^2*(c-a^2*c*x^2)^(-1-n^2/2), x, 1, (E^(n*ArcTanh(a*x))*(1-a*n*x))/(a^3*c*n*(1 -
	// n^2)*(c-a^2*c*x^2)^(n^2/2))}
	public void test03148() {
		check("Integrate(E^(n*ArcTanh(a*x))*x^2*(c-a^2*c*x^2)^(-1-n^2/2), x)",
				"(E^(n*ArcTanh(a*x))*(1-a*n*x))/(a^3*c*n*(1-n^2)*(c-a^2*c*x^2)^(n^2/2))");

	}

	// {ArcCoth(a*x)/x, x, 1, PolyLog(2, -(1/(a*x)))/2-PolyLog(2, 1/(a*x))/2}
	public void test03149() {
		check("Integrate(ArcCoth(a*x)/x, x)", "PolyLog(2, -(1/(a*x)))/2-PolyLog(2, 1/(a*x))/2");

	}

	// {ArcCoth(c*x)^2/(d+e*x), x, 1, -((ArcCoth(c*x)^2*Log(2/(1+c*x)))/e)+(ArcCoth(c*x)^2*Log((2*c*(d +
	// e*x))/((c*d+e)*(1+c*x))))/e+(ArcCoth(c*x)*PolyLog(2, 1-2/(1+c*x)))/e-(ArcCoth(c*x)*PolyLog(2, 1 -
	// (2*c*(d+e*x))/((c*d+e)*(1+c*x))))/e+PolyLog(3, 1-2/(1+c*x))/(2*e)-PolyLog(3, 1-(2*c*(d +
	// e*x))/((c*d+e)*(1+c*x)))/(2*e)}
	public void test03150() {
		check("Integrate(ArcCoth(c*x)^2/(d+e*x), x)",
				"-((ArcCoth(c*x)^2*Log(2/(1+c*x)))/e)+(ArcCoth(c*x)^2*Log((2*c*(d+e*x))/((c*d+e)*(1+c*x))))/e+(ArcCoth(c*x)*PolyLog(2, 1-2/(1+c*x)))/e-(ArcCoth(c*x)*PolyLog(2, 1-(2*c*(d+e*x))/((c*d+e)*(1+c*x))))/e+PolyLog(3, 1-2/(1+c*x))/(2*e)-PolyLog(3, 1-(2*c*(d+e*x))/((c*d+e)*(1+c*x)))/(2*e)");

	}

	// {ArcCoth(x)/(a-a*x^2)^(3/2), x, 1, -(1/(a*Sqrt(a-a*x^2)))+(x*ArcCoth(x))/(a*Sqrt(a-a*x^2))}
	public void test03151() {
		check("Integrate(ArcCoth(x)/(a-a*x^2)^(3/2), x)", "-(1/(a*Sqrt(a-a*x^2)))+(x*ArcCoth(x))/(a*Sqrt(a-a*x^2))");

	}

	// {1/((1-x^2)*ArcCoth(x)), x, 1, Log(ArcCoth(x))}
	public void test03152() {
		check("Integrate(1/((1-x^2)*ArcCoth(x)), x)", "Log(ArcCoth(x))");

	}

	// {ArcCoth(x)^n/(1-x^2), x, 1, ArcCoth(x)^(1+n)/(1+n)}
	public void test03153() {
		check("Integrate(ArcCoth(x)^n/(1-x^2), x)", "ArcCoth(x)^(1+n)/(1+n)");

	}

	// {ArcCoth(x)/(1-x^2), x, 1, ArcCoth(x)^2/2}
	public void test03154() {
		check("Integrate(ArcCoth(x)/(1-x^2), x)", "ArcCoth(x)^2/2");

	}

	// {(e+f*x)^m*(a+b*ArcCoth(c+d*x))^2, x, 1, Unintegrable((e+f*x)^m*(a+b*ArcCoth(c+d*x))^2, x)}
	public void test03155() {
		check("Integrate((e+f*x)^m*(a+b*ArcCoth(c+d*x))^2, x)", "Unintegrable((e+f*x)^m*(a+b*ArcCoth(c+d*x))^2, x)");

	}

	// {(e+f*x)^m*(a+b*ArcCoth(c+d*x))^3, x, 1, Unintegrable((e+f*x)^m*(a+b*ArcCoth(c+d*x))^3, x)}
	public void test03156() {
		check("Integrate((e+f*x)^m*(a+b*ArcCoth(c+d*x))^3, x)", "Unintegrable((e+f*x)^m*(a+b*ArcCoth(c+d*x))^3, x)");

	}

	// {ArcCoth(Tanh(a+b*x))^2/x^4, x, 1, ArcCoth(Tanh(a+b*x))^3/(3*x^3*(b*x-ArcCoth(Tanh(a+b*x))))}
	public void test03157() {
		check("Integrate(ArcCoth(Tanh(a+b*x))^2/x^4, x)", "ArcCoth(Tanh(a+b*x))^3/(3*x^3*(b*x-ArcCoth(Tanh(a+b*x))))");

	}

	// {ArcCoth(Tanh(a+b*x))^3/x^5, x, 1, ArcCoth(Tanh(a+b*x))^4/(4*x^4*(b*x-ArcCoth(Tanh(a+b*x))))}
	public void test03158() {
		check("Integrate(ArcCoth(Tanh(a+b*x))^3/x^5, x)", "ArcCoth(Tanh(a+b*x))^4/(4*x^4*(b*x-ArcCoth(Tanh(a+b*x))))");

	}

	// {x^m/ArcCoth(Tanh(a+b*x)), x, 1, -((x^(1+m)*Hypergeometric2F1(1, 1+m, 2+m, (b*x)/(b*x-ArcCoth(Tanh(a +
	// b*x)))))/((1+m)*(b*x-ArcCoth(Tanh(a+b*x)))))}
	public void test03159() {
		check("Integrate(x^m/ArcCoth(Tanh(a+b*x)), x)",
				"-((x^(1+m)*Hypergeometric2F1(1, 1+m, 2+m, (b*x)/(b*x-ArcCoth(Tanh(a+b*x)))))/((1+m)*(b*x-ArcCoth(Tanh(a+b*x)))))");

	}

	// {x^m*ArcCoth(Tanh(a+b*x))^n, x, 1, (x^m*ArcCoth(Tanh(a+b*x))^(1+n)*Hypergeometric2F1(-m, 1+n, 2+n,
	// -(ArcCoth(Tanh(a+b*x))/(b*x-ArcCoth(Tanh(a+b*x))))))/(b*(1+n)*((b*x)/(b*x-ArcCoth(Tanh(a+b*x))))^m)}
	public void test03160() {
		check("Integrate(x^m*ArcCoth(Tanh(a+b*x))^n, x)",
				"(x^m*ArcCoth(Tanh(a+b*x))^(1+n)*Hypergeometric2F1(-m, 1+n, 2+n, -(ArcCoth(Tanh(a+b*x))/(b*x-ArcCoth(Tanh(a+b*x))))))/(b*(1+n)*((b*x)/(b*x-ArcCoth(Tanh(a+b*x))))^m)");

	}

	// {ArcCoth(Tanh(a+b*x))^n/x, x, 1, (ArcCoth(Tanh(a+b*x))^(1+n)*Hypergeometric2F1(1, 1+n, 2+n,
	// -(ArcCoth(Tanh(a+b*x))/(b*x-ArcCoth(Tanh(a+b*x))))))/((1+n)*(b*x-ArcCoth(Tanh(a+b*x))))}
	public void test03161() {
		check("Integrate(ArcCoth(Tanh(a+b*x))^n/x, x)",
				"(ArcCoth(Tanh(a+b*x))^(1+n)*Hypergeometric2F1(1, 1+n, 2+n, -(ArcCoth(Tanh(a+b*x))/(b*x-ArcCoth(Tanh(a+b*x))))))/((1+n)*(b*x-ArcCoth(Tanh(a+b*x))))");

	}

	// {1/((a-a*x^2)*(b-2*b*ArcCoth(x))), x, 1, -Log(1-2*ArcCoth(x))/(2*a*b)}
	public void test03162() {
		check("Integrate(1/((a-a*x^2)*(b-2*b*ArcCoth(x))), x)", "-Log(1-2*ArcCoth(x))/(2*a*b)");

	}

	// {E^ArcCoth(a*x)*Sqrt(c-a*c*x), x, 1, (2*E^ArcCoth(a*x)*(1+a*x)*Sqrt(c-a*c*x))/(3*a)}
	public void test03163() {
		check("Integrate(E^ArcCoth(a*x)*Sqrt(c-a*c*x), x)", "(2*E^ArcCoth(a*x)*(1+a*x)*Sqrt(c-a*c*x))/(3*a)");

	}

	// {E^(3*ArcCoth(a*x))*(c-a*c*x)^(3/2), x, 1, (2*E^(3*ArcCoth(a*x))*(1+a*x)*(c-a*c*x)^(3/2))/(5*a)}
	public void test03164() {
		check("Integrate(E^(3*ArcCoth(a*x))*(c-a*c*x)^(3/2), x)",
				"(2*E^(3*ArcCoth(a*x))*(1+a*x)*(c-a*c*x)^(3/2))/(5*a)");

	}

	// {1/(E^ArcCoth(a*x)*Sqrt(c-a*c*x)), x, 1, (2*(1+a*x))/(a*E^ArcCoth(a*x)*Sqrt(c-a*c*x))}
	public void test03165() {
		check("Integrate(1/(E^ArcCoth(a*x)*Sqrt(c-a*c*x)), x)", "(2*(1+a*x))/(a*E^ArcCoth(a*x)*Sqrt(c-a*c*x))");

	}

	// {1/(E^(3*ArcCoth(a*x))*(c-a*c*x)^(3/2)), x, 1, (-2*(1+a*x))/(a*E^(3*ArcCoth(a*x))*(c-a*c*x)^(3/2))}
	public void test03166() {
		check("Integrate(1/(E^(3*ArcCoth(a*x))*(c-a*c*x)^(3/2)), x)",
				"(-2*(1+a*x))/(a*E^(3*ArcCoth(a*x))*(c-a*c*x)^(3/2))");

	}

	// {E^ArcCoth(a*x)*Sqrt(c-a*c*x), x, 1, (2*E^ArcCoth(a*x)*(1+a*x)*Sqrt(c-a*c*x))/(3*a)}
	public void test03167() {
		check("Integrate(E^ArcCoth(a*x)*Sqrt(c-a*c*x), x)", "(2*E^ArcCoth(a*x)*(1+a*x)*Sqrt(c-a*c*x))/(3*a)");

	}

	// {E^ArcCoth(x)*Sqrt(1-x), x, 1, (2*E^ArcCoth(x)*Sqrt(1-x)*(1+x))/3}
	public void test03168() {
		check("Integrate(E^ArcCoth(x)*Sqrt(1-x), x)", "(2*E^ArcCoth(x)*Sqrt(1-x)*(1+x))/3");

	}

	// {E^(n*ArcCoth(a*x))*(c-a*c*x)^(n/2), x, 1, (2*E^(n*ArcCoth(a*x))*(1+a*x)*(c-a*c*x)^(n/2))/(a*(2+n))}
	public void test03169() {
		check("Integrate(E^(n*ArcCoth(a*x))*(c-a*c*x)^(n/2), x)",
				"(2*E^(n*ArcCoth(a*x))*(1+a*x)*(c-a*c*x)^(n/2))/(a*(2+n))");

	}

	// {E^ArcCoth(a*x)/(c-a^2*c*x^2), x, 1, E^ArcCoth(a*x)/(a*c)}
	public void test03170() {
		check("Integrate(E^ArcCoth(a*x)/(c-a^2*c*x^2), x)", "E^ArcCoth(a*x)/(a*c)");

	}

	// {E^(3*ArcCoth(a*x))/(c-a^2*c*x^2), x, 1, E^(3*ArcCoth(a*x))/(3*a*c)}
	public void test03171() {
		check("Integrate(E^(3*ArcCoth(a*x))/(c-a^2*c*x^2), x)", "E^(3*ArcCoth(a*x))/(3*a*c)");

	}

	// {1/(E^ArcCoth(a*x)*(c-a^2*c*x^2)), x, 1, -(1/(a*c*E^ArcCoth(a*x)))}
	public void test03172() {
		check("Integrate(1/(E^ArcCoth(a*x)*(c-a^2*c*x^2)), x)", "-(1/(a*c*E^ArcCoth(a*x)))");

	}

	// {1/(E^(3*ArcCoth(a*x))*(c-a^2*c*x^2)), x, 1, -1/(3*a*c*E^(3*ArcCoth(a*x)))}
	public void test03173() {
		check("Integrate(1/(E^(3*ArcCoth(a*x))*(c-a^2*c*x^2)), x)", "-1/(3*a*c*E^(3*ArcCoth(a*x)))");

	}

	// {E^(n*ArcCoth(a*x))/(c-a^2*c*x^2), x, 1, E^(n*ArcCoth(a*x))/(a*c*n)}
	public void test03174() {
		check("Integrate(E^(n*ArcCoth(a*x))/(c-a^2*c*x^2), x)", "E^(n*ArcCoth(a*x))/(a*c*n)");

	}

	// {E^(n*ArcCoth(a*x))/(c-a^2*c*x^2)^(3/2), x, 1, -((E^(n*ArcCoth(a*x))*(n-a*x))/(a*c*(1-n^2)*Sqrt(c -
	// a^2*c*x^2)))}
	public void test03175() {
		check("Integrate(E^(n*ArcCoth(a*x))/(c-a^2*c*x^2)^(3/2), x)",
				"-((E^(n*ArcCoth(a*x))*(n-a*x))/(a*c*(1-n^2)*Sqrt(c-a^2*c*x^2)))");

	}

	// {(E^(n*ArcCoth(a*x))*x)/(c-a^2*c*x^2)^(3/2), x, 1, (E^(n*ArcCoth(a*x))*(1-a*n*x))/(a^2*c*(1-n^2)*Sqrt(c -
	// a^2*c*x^2))}
	public void test03176() {
		check("Integrate((E^(n*ArcCoth(a*x))*x)/(c-a^2*c*x^2)^(3/2), x)",
				"(E^(n*ArcCoth(a*x))*(1-a*n*x))/(a^2*c*(1-n^2)*Sqrt(c-a^2*c*x^2))");

	}

	// {E^(n*ArcCoth(a*x))/(c-a^2*c*x^2)^(3/2), x, 1, -((E^(n*ArcCoth(a*x))*(n-a*x))/(a*c*(1-n^2)*Sqrt(c -
	// a^2*c*x^2)))}
	public void test03177() {
		check("Integrate(E^(n*ArcCoth(a*x))/(c-a^2*c*x^2)^(3/2), x)",
				"-((E^(n*ArcCoth(a*x))*(n-a*x))/(a*c*(1-n^2)*Sqrt(c-a^2*c*x^2)))");

	}

	// {(d+e*x)^m*(a+b*ArcSech(c*x)), x, 1, ((d+e*x)^(1+m)*(a+b*ArcSech(c*x)))/(e*(1+m))+(b*Sqrt((1 +
	// c*x)^(-1))*Sqrt(1+c*x)*Unintegrable((d+e*x)^(1+m)/(x*Sqrt(1-c^2*x^2)), x))/(e*(1+m))}
	public void test03178() {
		check("Integrate((d+e*x)^m*(a+b*ArcSech(c*x)), x)",
				"((d+e*x)^(1+m)*(a+b*ArcSech(c*x)))/(e*(1+m))+(b*Sqrt((1+c*x)^(-1))*Sqrt(1+c*x)*Unintegrable((d+e*x)^(1+m)/(x*Sqrt(1-c^2*x^2)), x))/(e*(1+m))");

	}

	// {Derivative(1)(f)(x), x, 1, f(x)}
	public void test03179() {
		check("Integrate(Derivative(1)(f)(x), x)", "f(x)");

	}

	// {Derivative(2)(f)(x), x, 1, Derivative(1)(f)(x)}
	public void test03180() {
		check("Integrate(Derivative(2)(f)(x), x)", "Derivative(1)(f)(x)");

	}

	// {Derivative(3)(f)(x), x, 1, Derivative(2)(f)(x)}
	public void test03181() {
		check("Integrate(Derivative(3)(f)(x), x)", "Derivative(2)(f)(x)");

	}

	// {Derivative(n)(f)(x), x, 1, Derivative(-1+n)(f)(x)}
	public void test03182() {
		check("Integrate(Derivative(n)(f)(x), x)", "Derivative(-1+n)(f)(x)");

	}

	// {f(g(x))*Derivative(1)(g)(x), x, 1, CannotIntegrate(f(g(x))*Derivative(1)(g)(x), x)}
	public void test03183() {
		check("Integrate(f(g(x))*Derivative(1)(g)(x), x)", "CannotIntegrate(f(g(x))*Derivative(1)(g)(x), x)");

	}

	// {f(Derivative(1)(g)(x))*Derivative(2)(g)(x), x, 1, CannotIntegrate(f(Derivative(1)(g)(x))*Derivative(2)(g)(x),
	// x)}
	public void test03184() {
		check("Integrate(f(Derivative(1)(g)(x))*Derivative(2)(g)(x), x)",
				"CannotIntegrate(f(Derivative(1)(g)(x))*Derivative(2)(g)(x), x)");

	}

	// {(g(x)*Derivative(1)(f)(x)-f(x)*Derivative(1)(g)(x))/g(x)^2, x, 1, f(x)/g(x)}
	public void test03185() {
		check("Integrate((g(x)*Derivative(1)(f)(x)-f(x)*Derivative(1)(g)(x))/g(x)^2, x)", "f(x)/g(x)");

	}

	// {(g(x)*Derivative(1)(f)(x)-f(x)*Derivative(1)(g)(x))/(f(x)*g(x)), x, 1, Log(f(x)/g(x))}
	public void test03186() {
		check("Integrate((g(x)*Derivative(1)(f)(x)-f(x)*Derivative(1)(g)(x))/(f(x)*g(x)), x)", "Log(f(x)/g(x))");

	}

	// {F^(a+b*x)*Derivative(1)(f)(x), x, 1, F^(a+b*x)*f(x)-b*CannotIntegrate(F^(a+b*x)*f(x), x)*Log(F)}
	public void test03187() {
		check("Integrate(F^(a+b*x)*Derivative(1)(f)(x), x)",
				"F^(a+b*x)*f(x)-b*CannotIntegrate(F^(a+b*x)*f(x), x)*Log(F)");

	}

	// {F^(a+b*x)*Derivative(-1)(f)(x), x, 1, -(CannotIntegrate(F^(a+b*x)*f(x), x)/(b*Log(F)))+(F^(a +
	// b*x)*Derivative(-1)(f)(x))/(b*Log(F))}
	public void test03188() {
		check("Integrate(F^(a+b*x)*Derivative(-1)(f)(x), x)",
				"-(CannotIntegrate(F^(a+b*x)*f(x), x)/(b*Log(F)))+(F^(a+b*x)*Derivative(-1)(f)(x))/(b*Log(F))");

	}

	// {Sin(a+b*x)*Derivative(1)(f)(x), x, 1, -(b*CannotIntegrate(Cos(a+b*x)*f(x), x))+f(x)*Sin(a+b*x)}
	public void test03189() {
		check("Integrate(Sin(a+b*x)*Derivative(1)(f)(x), x)",
				"-(b*CannotIntegrate(Cos(a+b*x)*f(x), x))+f(x)*Sin(a+b*x)");

	}

	// {Sin(a+b*x)*Derivative(-1)(f)(x), x, 1, CannotIntegrate(Cos(a+b*x)*f(x), x)/b-(Cos(a +
	// b*x)*Derivative(-1)(f)(x))/b}
	public void test03190() {
		check("Integrate(Sin(a+b*x)*Derivative(-1)(f)(x), x)",
				"CannotIntegrate(Cos(a+b*x)*f(x), x)/b-(Cos(a+b*x)*Derivative(-1)(f)(x))/b");

	}

	// {Cos(a+b*x)*Derivative(1)(f)(x), x, 1, b*CannotIntegrate(f(x)*Sin(a+b*x), x)+Cos(a+b*x)*f(x)}
	public void test03191() {
		check("Integrate(Cos(a+b*x)*Derivative(1)(f)(x), x)", "b*CannotIntegrate(f(x)*Sin(a+b*x), x)+Cos(a+b*x)*f(x)");

	}

	// {Cos(a+b*x)*Derivative(-1)(f)(x), x, 1, -(CannotIntegrate(f(x)*Sin(a+b*x), x)/b)+(Sin(a +
	// b*x)*Derivative(-1)(f)(x))/b}
	public void test03192() {
		check("Integrate(Cos(a+b*x)*Derivative(-1)(f)(x), x)",
				"-(CannotIntegrate(f(x)*Sin(a+b*x), x)/b)+(Sin(a+b*x)*Derivative(-1)(f)(x))/b");

	}

	// {Erf(b*x)/x, x, 1, (2*b*x*HypergeometricPFQ({1/2, 1/2}, {3/2, 3/2}, -(b^2*x^2)))/Sqrt(Pi)}
	public void test03193() {
		check("Integrate(Erf(b*x)/x, x)", "(2*b*x*HypergeometricPFQ({1/2, 1/2}, {3/2, 3/2}, -(b^2*x^2)))/Sqrt(Pi)");

	}

	// {Erf(b*x), x, 1, 1/(b*E^(b^2*x^2)*Sqrt(Pi))+x*Erf(b*x)}
	public void test03194() {
		check("Integrate(Erf(b*x), x)", "1/(b*E^(b^2*x^2)*Sqrt(Pi))+x*Erf(b*x)");

	}

	// {Erf(a+b*x), x, 1, 1/(b*E^(a+b*x)^2*Sqrt(Pi))+((a+b*x)*Erf(a+b*x))/b}
	public void test03195() {
		check("Integrate(Erf(a+b*x), x)", "1/(b*E^(a+b*x)^2*Sqrt(Pi))+((a+b*x)*Erf(a+b*x))/b");

	}

	// {Erf(a+b*x)/(c+d*x)^2, x, 1, -(Erf(a+b*x)/(d*(c+d*x)))+(2*b*Unintegrable(1/(E^(a+b*x)^2*(c+d*x)),
	// x))/(d*Sqrt(Pi))}
	public void test03196() {
		check("Integrate(Erf(a+b*x)/(c+d*x)^2, x)",
				"-(Erf(a+b*x)/(d*(c+d*x)))+(2*b*Unintegrable(1/(E^(a+b*x)^2*(c+d*x)), x))/(d*Sqrt(Pi))");

	}

	// {(E^(c+b^2*x^2)*Erf(b*x))/x, x, 1, (2*b*E^c*x*HypergeometricPFQ({1/2, 1}, {3/2, 3/2}, b^2*x^2))/Sqrt(Pi)}
	public void test03197() {
		check("Integrate((E^(c+b^2*x^2)*Erf(b*x))/x, x)",
				"(2*b*E^c*x*HypergeometricPFQ({1/2, 1}, {3/2, 3/2}, b^2*x^2))/Sqrt(Pi)");

	}

	// {E^(c+b^2*x^2)*Erf(b*x), x, 1, (b*E^c*x^2*HypergeometricPFQ({1, 1}, {3/2, 2}, b^2*x^2))/Sqrt(Pi)}
	public void test03198() {
		check("Integrate(E^(c+b^2*x^2)*Erf(b*x), x)",
				"(b*E^c*x^2*HypergeometricPFQ({1, 1}, {3/2, 2}, b^2*x^2))/Sqrt(Pi)");

	}

	// {(E^(c+d*x^2)*Erf(a+b*x))/x^2, x, 1, -((E^(c+d*x^2)*Erf(a+b*x))/x)+(2*b*Unintegrable(E^(-a^2+c -
	// 2*a*b*x+(-b^2+d)*x^2)/x, x))/Sqrt(Pi)+2*d*Unintegrable(E^(c+d*x^2)*Erf(a+b*x), x)}
	public void test03199() {
		check("Integrate((E^(c+d*x^2)*Erf(a+b*x))/x^2, x)",
				"-((E^(c+d*x^2)*Erf(a+b*x))/x)+(2*b*Unintegrable(E^(-a^2+c-2*a*b*x+(-b^2+d)*x^2)/x, x))/Sqrt(Pi)+2*d*Unintegrable(E^(c+d*x^2)*Erf(a+b*x), x)");

	}

	// {Erfc(b*x), x, 1, -(1/(b*E^(b^2*x^2)*Sqrt(Pi)))+x*Erfc(b*x)}
	public void test03200() {
		check("Integrate(Erfc(b*x), x)", "-(1/(b*E^(b^2*x^2)*Sqrt(Pi)))+x*Erfc(b*x)");

	}

	// {Erfc(a+b*x), x, 1, -(1/(b*E^(a+b*x)^2*Sqrt(Pi)))+((a+b*x)*Erfc(a+b*x))/b}
	public void test03201() {
		check("Integrate(Erfc(a+b*x), x)", "-(1/(b*E^(a+b*x)^2*Sqrt(Pi)))+((a+b*x)*Erfc(a+b*x))/b");

	}

	// {Erfc(a+b*x)/(c+d*x)^2, x, 1, -(Erfc(a+b*x)/(d*(c+d*x)))-(2*b*Unintegrable(1/(E^(a+b*x)^2*(c+d*x)),
	// x))/(d*Sqrt(Pi))}
	public void test03202() {
		check("Integrate(Erfc(a+b*x)/(c+d*x)^2, x)",
				"-(Erfc(a+b*x)/(d*(c+d*x)))-(2*b*Unintegrable(1/(E^(a+b*x)^2*(c+d*x)), x))/(d*Sqrt(Pi))");

	}

	// {(E^(c+d*x^2)*Erfc(a+b*x))/x^2, x, 1, -((E^(c+d*x^2)*Erfc(a+b*x))/x)-(2*b*Unintegrable(E^(-a^2+c -
	// 2*a*b*x+(-b^2+d)*x^2)/x, x))/Sqrt(Pi)+2*d*Unintegrable(E^(c+d*x^2)*Erfc(a+b*x), x)}
	public void test03203() {
		check("Integrate((E^(c+d*x^2)*Erfc(a+b*x))/x^2, x)",
				"-((E^(c+d*x^2)*Erfc(a+b*x))/x)-(2*b*Unintegrable(E^(-a^2+c-2*a*b*x+(-b^2+d)*x^2)/x, x))/Sqrt(Pi)+2*d*Unintegrable(E^(c+d*x^2)*Erfc(a+b*x), x)");

	}

	// {Erfi(b*x)/x, x, 1, (2*b*x*HypergeometricPFQ({1/2, 1/2}, {3/2, 3/2}, b^2*x^2))/Sqrt(Pi)}
	public void test03204() {
		check("Integrate(Erfi(b*x)/x, x)", "(2*b*x*HypergeometricPFQ({1/2, 1/2}, {3/2, 3/2}, b^2*x^2))/Sqrt(Pi)");

	}

	// {Erfi(b*x), x, 1, -(E^(b^2*x^2)/(b*Sqrt(Pi)))+x*Erfi(b*x)}
	public void test03205() {
		check("Integrate(Erfi(b*x), x)", "-(E^(b^2*x^2)/(b*Sqrt(Pi)))+x*Erfi(b*x)");

	}

	// {Erfi(a+b*x), x, 1, -(E^(a+b*x)^2/(b*Sqrt(Pi)))+((a+b*x)*Erfi(a+b*x))/b}
	public void test03206() {
		check("Integrate(Erfi(a+b*x), x)", "-(E^(a+b*x)^2/(b*Sqrt(Pi)))+((a+b*x)*Erfi(a+b*x))/b");

	}

	// {Erfi(a+b*x)/(c+d*x)^2, x, 1, -(Erfi(a+b*x)/(d*(c+d*x)))+(2*b*Unintegrable(E^(a+b*x)^2/(c+d*x),
	// x))/(d*Sqrt(Pi))}
	public void test03207() {
		check("Integrate(Erfi(a+b*x)/(c+d*x)^2, x)",
				"-(Erfi(a+b*x)/(d*(c+d*x)))+(2*b*Unintegrable(E^(a+b*x)^2/(c+d*x), x))/(d*Sqrt(Pi))");

	}

	// {Erfi(b*x)/(E^(b^2*x^2)*x), x, 1, (2*b*x*HypergeometricPFQ({1/2, 1}, {3/2, 3/2}, -(b^2*x^2)))/Sqrt(Pi)}
	public void test03208() {
		check("Integrate(Erfi(b*x)/(E^(b^2*x^2)*x), x)",
				"(2*b*x*HypergeometricPFQ({1/2, 1}, {3/2, 3/2}, -(b^2*x^2)))/Sqrt(Pi)");

	}

	// {Erfi(b*x)/E^(b^2*x^2), x, 1, (b*x^2*HypergeometricPFQ({1, 1}, {3/2, 2}, -(b^2*x^2)))/Sqrt(Pi)}
	public void test03209() {
		check("Integrate(Erfi(b*x)/E^(b^2*x^2), x)",
				"(b*x^2*HypergeometricPFQ({1, 1}, {3/2, 2}, -(b^2*x^2)))/Sqrt(Pi)");

	}

	// {(E^(c+d*x^2)*Erfi(a+b*x))/x^2, x, 1, -((E^(c+d*x^2)*Erfi(a+b*x))/x)+(2*b*Unintegrable(E^(a^2+c +
	// 2*a*b*x+(b^2+d)*x^2)/x, x))/Sqrt(Pi)+2*d*Unintegrable(E^(c+d*x^2)*Erfi(a+b*x), x)}
	public void test03210() {
		check("Integrate((E^(c+d*x^2)*Erfi(a+b*x))/x^2, x)",
				"-((E^(c+d*x^2)*Erfi(a+b*x))/x)+(2*b*Unintegrable(E^(a^2+c+2*a*b*x+(b^2+d)*x^2)/x, x))/Sqrt(Pi)+2*d*Unintegrable(E^(c+d*x^2)*Erfi(a+b*x), x)");

	}

	// {FresnelS(b*x), x, 1, Cos((b^2*Pi*x^2)/2)/(b*Pi)+x*FresnelS(b*x)}
	public void test03211() {
		check("Integrate(FresnelS(b*x), x)", "Cos((b^2*Pi*x^2)/2)/(b*Pi)+x*FresnelS(b*x)");

	}

	// {FresnelS(a+b*x), x, 1, Cos((Pi*(a+b*x)^2)/2)/(b*Pi)+((a+b*x)*FresnelS(a+b*x))/b}
	public void test03212() {
		check("Integrate(FresnelS(a+b*x), x)", "Cos((Pi*(a+b*x)^2)/2)/(b*Pi)+((a+b*x)*FresnelS(a+b*x))/b");

	}

	// {FresnelS(a+b*x), x, 1, Cos((Pi*(a+b*x)^2)/2)/(b*Pi)+((a+b*x)*FresnelS(a+b*x))/b}
	public void test03213() {
		check("Integrate(FresnelS(a+b*x), x)", "Cos((Pi*(a+b*x)^2)/2)/(b*Pi)+((a+b*x)*FresnelS(a+b*x))/b");

	}

	// {FresnelS(b*x)^2/x^2, x, 1, -(FresnelS(b*x)^2/x)+2*b*Unintegrable((FresnelS(b*x)*Sin((b^2*Pi*x^2)/2))/x, x)}
	public void test03214() {
		check("Integrate(FresnelS(b*x)^2/x^2, x)",
				"-(FresnelS(b*x)^2/x)+2*b*Unintegrable((FresnelS(b*x)*Sin((b^2*Pi*x^2)/2))/x, x)");

	}

	// {FresnelS(b*x)^2/x^3, x, 1, -FresnelS(b*x)^2/(2*x^2)+b*Unintegrable((FresnelS(b*x)*Sin((b^2*Pi*x^2)/2))/x^2,
	// x)}
	public void test03215() {
		check("Integrate(FresnelS(b*x)^2/x^3, x)",
				"-FresnelS(b*x)^2/(2*x^2)+b*Unintegrable((FresnelS(b*x)*Sin((b^2*Pi*x^2)/2))/x^2, x)");

	}

	// {Cos((b^2*Pi*x^2)/2)*FresnelS(b*x), x, 1, (FresnelC(b*x)*FresnelS(b*x))/(2*b)-(I/8)*b*x^2*HypergeometricPFQ({1,
	// 1}, {3/2, 2}, (-I/2)*b^2*Pi*x^2)+(I/8)*b*x^2*HypergeometricPFQ({1, 1}, {3/2, 2}, (I/2)*b^2*Pi*x^2)}
	public void test03216() {
		check("Integrate(Cos((b^2*Pi*x^2)/2)*FresnelS(b*x), x)",
				"(FresnelC(b*x)*FresnelS(b*x))/(2*b)-(I/8)*b*x^2*HypergeometricPFQ({1, 1}, {3/2, 2}, (-I/2)*b^2*Pi*x^2)+(I/8)*b*x^2*HypergeometricPFQ({1, 1}, {3/2, 2}, (I/2)*b^2*Pi*x^2)");

	}

	// {FresnelC(b*x), x, 1, x*FresnelC(b*x)-Sin((b^2*Pi*x^2)/2)/(b*Pi)}
	public void test03217() {
		check("Integrate(FresnelC(b*x), x)", "x*FresnelC(b*x)-Sin((b^2*Pi*x^2)/2)/(b*Pi)");

	}

	// {FresnelC(a+b*x), x, 1, ((a+b*x)*FresnelC(a+b*x))/b-Sin((Pi*(a+b*x)^2)/2)/(b*Pi)}
	public void test03218() {
		check("Integrate(FresnelC(a+b*x), x)", "((a+b*x)*FresnelC(a+b*x))/b-Sin((Pi*(a+b*x)^2)/2)/(b*Pi)");

	}

	// {FresnelC(a+b*x), x, 1, ((a+b*x)*FresnelC(a+b*x))/b-Sin((Pi*(a+b*x)^2)/2)/(b*Pi)}
	public void test03219() {
		check("Integrate(FresnelC(a+b*x), x)", "((a+b*x)*FresnelC(a+b*x))/b-Sin((Pi*(a+b*x)^2)/2)/(b*Pi)");

	}

	// {FresnelC(b*x)^2/x^2, x, 1, -(FresnelC(b*x)^2/x)+2*b*Unintegrable((Cos((b^2*Pi*x^2)/2)*FresnelC(b*x))/x, x)}
	public void test03220() {
		check("Integrate(FresnelC(b*x)^2/x^2, x)",
				"-(FresnelC(b*x)^2/x)+2*b*Unintegrable((Cos((b^2*Pi*x^2)/2)*FresnelC(b*x))/x, x)");

	}

	// {FresnelC(b*x)^2/x^3, x, 1, -FresnelC(b*x)^2/(2*x^2)+b*Unintegrable((Cos((b^2*Pi*x^2)/2)*FresnelC(b*x))/x^2,
	// x)}
	public void test03221() {
		check("Integrate(FresnelC(b*x)^2/x^3, x)",
				"-FresnelC(b*x)^2/(2*x^2)+b*Unintegrable((Cos((b^2*Pi*x^2)/2)*FresnelC(b*x))/x^2, x)");

	}

	// {FresnelC(b*x)*Sin((b^2*Pi*x^2)/2), x, 1, (FresnelC(b*x)*FresnelS(b*x))/(2*b)+(I/8)*b*x^2*HypergeometricPFQ({1,
	// 1}, {3/2, 2}, (-I/2)*b^2*Pi*x^2)-(I/8)*b*x^2*HypergeometricPFQ({1, 1}, {3/2, 2}, (I/2)*b^2*Pi*x^2)}
	public void test03222() {
		check("Integrate(FresnelC(b*x)*Sin((b^2*Pi*x^2)/2), x)",
				"(FresnelC(b*x)*FresnelS(b*x))/(2*b)+(I/8)*b*x^2*HypergeometricPFQ({1, 1}, {3/2, 2}, (-I/2)*b^2*Pi*x^2)-(I/8)*b*x^2*HypergeometricPFQ({1, 1}, {3/2, 2}, (I/2)*b^2*Pi*x^2)");

	}

	// {x^2*ExpIntegralE(1, b*x), x, 1, -(x^3*ExpIntegralE(-2, b*x))/3+(x^3*ExpIntegralE(1, b*x))/3}
	public void test03223() {
		check("Integrate(x^2*ExpIntegralE(1, b*x), x)", "-(x^3*ExpIntegralE(-2, b*x))/3+(x^3*ExpIntegralE(1, b*x))/3");

	}

	// {x*ExpIntegralE(1, b*x), x, 1, -(x^2*ExpIntegralE(-1, b*x))/2+(x^2*ExpIntegralE(1, b*x))/2}
	public void test03224() {
		check("Integrate(x*ExpIntegralE(1, b*x), x)", "-(x^2*ExpIntegralE(-1, b*x))/2+(x^2*ExpIntegralE(1, b*x))/2");

	}

	// {ExpIntegralE(1, b*x), x, 1, -(ExpIntegralE(2, b*x)/b)}
	public void test03225() {
		check("Integrate(ExpIntegralE(1, b*x), x)", "-(ExpIntegralE(2, b*x)/b)");

	}

	// {ExpIntegralE(1, b*x)/x, x, 1, b*x*HypergeometricPFQ({1, 1, 1}, {2, 2, 2}, -(b*x))-EulerGamma*Log(x) -
	// Log(b*x)^2/2}
	public void test03226() {
		check("Integrate(ExpIntegralE(1, b*x)/x, x)",
				"b*x*HypergeometricPFQ({1, 1, 1}, {2, 2, 2}, -(b*x))-EulerGamma*Log(x)-Log(b*x)^2/2");

	}

	// {ExpIntegralE(1, b*x)/x^2, x, 1, -(ExpIntegralE(1, b*x)/x)+ExpIntegralE(2, b*x)/x}
	public void test03227() {
		check("Integrate(ExpIntegralE(1, b*x)/x^2, x)", "-(ExpIntegralE(1, b*x)/x)+ExpIntegralE(2, b*x)/x");

	}

	// {ExpIntegralE(1, b*x)/x^3, x, 1, -ExpIntegralE(1, b*x)/(2*x^2)+ExpIntegralE(3, b*x)/(2*x^2)}
	public void test03228() {
		check("Integrate(ExpIntegralE(1, b*x)/x^3, x)", "-ExpIntegralE(1, b*x)/(2*x^2)+ExpIntegralE(3, b*x)/(2*x^2)");

	}

	// {ExpIntegralE(1, b*x)/x^4, x, 1, -ExpIntegralE(1, b*x)/(3*x^3)+ExpIntegralE(4, b*x)/(3*x^3)}
	public void test03229() {
		check("Integrate(ExpIntegralE(1, b*x)/x^4, x)", "-ExpIntegralE(1, b*x)/(3*x^3)+ExpIntegralE(4, b*x)/(3*x^3)");

	}

	// {x^2*ExpIntegralE(2, b*x), x, 1, -(x^3*ExpIntegralE(-2, b*x))/4+(x^3*ExpIntegralE(2, b*x))/4}
	public void test03230() {
		check("Integrate(x^2*ExpIntegralE(2, b*x), x)", "-(x^3*ExpIntegralE(-2, b*x))/4+(x^3*ExpIntegralE(2, b*x))/4");

	}

	// {x*ExpIntegralE(2, b*x), x, 1, -(x^2*ExpIntegralE(-1, b*x))/3+(x^2*ExpIntegralE(2, b*x))/3}
	public void test03231() {
		check("Integrate(x*ExpIntegralE(2, b*x), x)", "-(x^2*ExpIntegralE(-1, b*x))/3+(x^2*ExpIntegralE(2, b*x))/3");

	}

	// {ExpIntegralE(2, b*x), x, 1, -(ExpIntegralE(3, b*x)/b)}
	public void test03232() {
		check("Integrate(ExpIntegralE(2, b*x), x)", "-(ExpIntegralE(3, b*x)/b)");

	}

	// {ExpIntegralE(2, b*x)/x, x, 1, -ExpIntegralE(1, b*x)+ExpIntegralE(2, b*x)}
	public void test03233() {
		check("Integrate(ExpIntegralE(2, b*x)/x, x)", "-ExpIntegralE(1, b*x)+ExpIntegralE(2, b*x)");

	}

	// {ExpIntegralE(2, b*x)/x^3, x, 1, -(ExpIntegralE(2, b*x)/x^2)+ExpIntegralE(3, b*x)/x^2}
	public void test03234() {
		check("Integrate(ExpIntegralE(2, b*x)/x^3, x)", "-(ExpIntegralE(2, b*x)/x^2)+ExpIntegralE(3, b*x)/x^2");

	}

	// {ExpIntegralE(2, b*x)/x^4, x, 1, -ExpIntegralE(2, b*x)/(2*x^3)+ExpIntegralE(4, b*x)/(2*x^3)}
	public void test03235() {
		check("Integrate(ExpIntegralE(2, b*x)/x^4, x)", "-ExpIntegralE(2, b*x)/(2*x^3)+ExpIntegralE(4, b*x)/(2*x^3)");

	}

	// {ExpIntegralE(2, b*x)/x^5, x, 1, -ExpIntegralE(2, b*x)/(3*x^4)+ExpIntegralE(5, b*x)/(3*x^4)}
	public void test03236() {
		check("Integrate(ExpIntegralE(2, b*x)/x^5, x)", "-ExpIntegralE(2, b*x)/(3*x^4)+ExpIntegralE(5, b*x)/(3*x^4)");

	}

	// {x^2*ExpIntegralE(3, b*x), x, 1, -(x^3*ExpIntegralE(-2, b*x))/5+(x^3*ExpIntegralE(3, b*x))/5}
	public void test03237() {
		check("Integrate(x^2*ExpIntegralE(3, b*x), x)", "-(x^3*ExpIntegralE(-2, b*x))/5+(x^3*ExpIntegralE(3, b*x))/5");

	}

	// {x*ExpIntegralE(3, b*x), x, 1, -(x^2*ExpIntegralE(-1, b*x))/4+(x^2*ExpIntegralE(3, b*x))/4}
	public void test03238() {
		check("Integrate(x*ExpIntegralE(3, b*x), x)", "-(x^2*ExpIntegralE(-1, b*x))/4+(x^2*ExpIntegralE(3, b*x))/4");

	}

	// {ExpIntegralE(3, b*x), x, 1, -(ExpIntegralE(4, b*x)/b)}
	public void test03239() {
		check("Integrate(ExpIntegralE(3, b*x), x)", "-(ExpIntegralE(4, b*x)/b)");

	}

	// {ExpIntegralE(3, b*x)/x, x, 1, -ExpIntegralE(1, b*x)/2+ExpIntegralE(3, b*x)/2}
	public void test03240() {
		check("Integrate(ExpIntegralE(3, b*x)/x, x)", "-ExpIntegralE(1, b*x)/2+ExpIntegralE(3, b*x)/2");

	}

	// {ExpIntegralE(3, b*x)/x^2, x, 1, -(ExpIntegralE(2, b*x)/x)+ExpIntegralE(3, b*x)/x}
	public void test03241() {
		check("Integrate(ExpIntegralE(3, b*x)/x^2, x)", "-(ExpIntegralE(2, b*x)/x)+ExpIntegralE(3, b*x)/x");

	}

	// {ExpIntegralE(3, b*x)/x^4, x, 1, -(ExpIntegralE(3, b*x)/x^3)+ExpIntegralE(4, b*x)/x^3}
	public void test03242() {
		check("Integrate(ExpIntegralE(3, b*x)/x^4, x)", "-(ExpIntegralE(3, b*x)/x^3)+ExpIntegralE(4, b*x)/x^3");

	}

	// {ExpIntegralE(3, b*x)/x^5, x, 1, -ExpIntegralE(3, b*x)/(2*x^4)+ExpIntegralE(5, b*x)/(2*x^4)}
	public void test03243() {
		check("Integrate(ExpIntegralE(3, b*x)/x^5, x)", "-ExpIntegralE(3, b*x)/(2*x^4)+ExpIntegralE(5, b*x)/(2*x^4)");

	}

	// {ExpIntegralE(3, b*x)/x^6, x, 1, -ExpIntegralE(3, b*x)/(3*x^5)+ExpIntegralE(6, b*x)/(3*x^5)}
	public void test03244() {
		check("Integrate(ExpIntegralE(3, b*x)/x^6, x)", "-ExpIntegralE(3, b*x)/(3*x^5)+ExpIntegralE(6, b*x)/(3*x^5)");

	}

	// {x^3*ExpIntegralE(-1, b*x), x, 1, -(x^4*ExpIntegralE(-3, b*x))/2+(x^4*ExpIntegralE(-1, b*x))/2}
	public void test03245() {
		check("Integrate(x^3*ExpIntegralE(-1, b*x), x)",
				"-(x^4*ExpIntegralE(-3, b*x))/2+(x^4*ExpIntegralE(-1, b*x))/2");

	}

	// {x^2*ExpIntegralE(-1, b*x), x, 1, -(x^3*ExpIntegralE(-2, b*x))+x^3*ExpIntegralE(-1, b*x)}
	public void test03246() {
		check("Integrate(x^2*ExpIntegralE(-1, b*x), x)", "-(x^3*ExpIntegralE(-2, b*x))+x^3*ExpIntegralE(-1, b*x)");

	}

	// {ExpIntegralE(-1, b*x), x, 1, -(1/(b^2*E^(b*x)*x))}
	public void test03247() {
		check("Integrate(ExpIntegralE(-1, b*x), x)", "-(1/(b^2*E^(b*x)*x))");

	}

	// {ExpIntegralE(-1, b*x)/x, x, 1, -ExpIntegralE(-1, b*x)/2+ExpIntegralE(1, b*x)/2}
	public void test03248() {
		check("Integrate(ExpIntegralE(-1, b*x)/x, x)", "-ExpIntegralE(-1, b*x)/2+ExpIntegralE(1, b*x)/2");

	}

	// {ExpIntegralE(-1, b*x)/x^2, x, 1, -ExpIntegralE(-1, b*x)/(3*x)+ExpIntegralE(2, b*x)/(3*x)}
	public void test03249() {
		check("Integrate(ExpIntegralE(-1, b*x)/x^2, x)", "-ExpIntegralE(-1, b*x)/(3*x)+ExpIntegralE(2, b*x)/(3*x)");

	}

	// {ExpIntegralE(-1, b*x)/x^3, x, 1, -ExpIntegralE(-1, b*x)/(4*x^2)+ExpIntegralE(3, b*x)/(4*x^2)}
	public void test03250() {
		check("Integrate(ExpIntegralE(-1, b*x)/x^3, x)", "-ExpIntegralE(-1, b*x)/(4*x^2)+ExpIntegralE(3, b*x)/(4*x^2)");

	}

	// {x^4*ExpIntegralE(-2, b*x), x, 1, -(x^5*ExpIntegralE(-4, b*x))/2+(x^5*ExpIntegralE(-2, b*x))/2}
	public void test03251() {
		check("Integrate(x^4*ExpIntegralE(-2, b*x), x)",
				"-(x^5*ExpIntegralE(-4, b*x))/2+(x^5*ExpIntegralE(-2, b*x))/2");

	}

	// {x^3*ExpIntegralE(-2, b*x), x, 1, -(x^4*ExpIntegralE(-3, b*x))+x^4*ExpIntegralE(-2, b*x)}
	public void test03252() {
		check("Integrate(x^3*ExpIntegralE(-2, b*x), x)", "-(x^4*ExpIntegralE(-3, b*x))+x^4*ExpIntegralE(-2, b*x)");

	}

	// {x*ExpIntegralE(-2, b*x), x, 1, -(x^2*ExpIntegralE(-2, b*x))+x^2*ExpIntegralE(-1, b*x)}
	public void test03253() {
		check("Integrate(x*ExpIntegralE(-2, b*x), x)", "-(x^2*ExpIntegralE(-2, b*x))+x^2*ExpIntegralE(-1, b*x)");

	}

	// {ExpIntegralE(-1, b*x), x, 1, -(1/(b^2*E^(b*x)*x))}
	public void test03254() {
		check("Integrate(ExpIntegralE(-1, b*x), x)", "-(1/(b^2*E^(b*x)*x))");

	}

	// {ExpIntegralE(-2, b*x)/x, x, 1, -ExpIntegralE(-2, b*x)/3+ExpIntegralE(1, b*x)/3}
	public void test03255() {
		check("Integrate(ExpIntegralE(-2, b*x)/x, x)", "-ExpIntegralE(-2, b*x)/3+ExpIntegralE(1, b*x)/3");

	}

	// {ExpIntegralE(-2, b*x)/x^2, x, 1, -ExpIntegralE(-2, b*x)/(4*x)+ExpIntegralE(2, b*x)/(4*x)}
	public void test03256() {
		check("Integrate(ExpIntegralE(-2, b*x)/x^2, x)", "-ExpIntegralE(-2, b*x)/(4*x)+ExpIntegralE(2, b*x)/(4*x)");

	}

	// {ExpIntegralE(-2, b*x)/x^3, x, 1, -ExpIntegralE(-2, b*x)/(5*x^2)+ExpIntegralE(3, b*x)/(5*x^2)}
	public void test03257() {
		check("Integrate(ExpIntegralE(-2, b*x)/x^3, x)", "-ExpIntegralE(-2, b*x)/(5*x^2)+ExpIntegralE(3, b*x)/(5*x^2)");

	}

	// {x^5*ExpIntegralE(-3, b*x), x, 1, -(x^6*ExpIntegralE(-5, b*x))/2+(x^6*ExpIntegralE(-3, b*x))/2}
	public void test03258() {
		check("Integrate(x^5*ExpIntegralE(-3, b*x), x)",
				"-(x^6*ExpIntegralE(-5, b*x))/2+(x^6*ExpIntegralE(-3, b*x))/2");

	}

	// {x^4*ExpIntegralE(-3, b*x), x, 1, -(x^5*ExpIntegralE(-4, b*x))+x^5*ExpIntegralE(-3, b*x)}
	public void test03259() {
		check("Integrate(x^4*ExpIntegralE(-3, b*x), x)", "-(x^5*ExpIntegralE(-4, b*x))+x^5*ExpIntegralE(-3, b*x)");

	}

	// {x^2*ExpIntegralE(-3, b*x), x, 1, -(x^3*ExpIntegralE(-3, b*x))+x^3*ExpIntegralE(-2, b*x)}
	public void test03260() {
		check("Integrate(x^2*ExpIntegralE(-3, b*x), x)", "-(x^3*ExpIntegralE(-3, b*x))+x^3*ExpIntegralE(-2, b*x)");

	}

	// {x*ExpIntegralE(-3, b*x), x, 1, -(x^2*ExpIntegralE(-3, b*x))/2+(x^2*ExpIntegralE(-1, b*x))/2}
	public void test03261() {
		check("Integrate(x*ExpIntegralE(-3, b*x), x)", "-(x^2*ExpIntegralE(-3, b*x))/2+(x^2*ExpIntegralE(-1, b*x))/2");

	}

	// {ExpIntegralE(-1, b*x), x, 1, -(1/(b^2*E^(b*x)*x))}
	public void test03262() {
		check("Integrate(ExpIntegralE(-1, b*x), x)", "-(1/(b^2*E^(b*x)*x))");

	}

	// {ExpIntegralE(-3, b*x)/x, x, 1, -ExpIntegralE(-3, b*x)/4+ExpIntegralE(1, b*x)/4}
	public void test03263() {
		check("Integrate(ExpIntegralE(-3, b*x)/x, x)", "-ExpIntegralE(-3, b*x)/4+ExpIntegralE(1, b*x)/4");

	}

	// {ExpIntegralE(-3, b*x)/x^2, x, 1, -ExpIntegralE(-3, b*x)/(5*x)+ExpIntegralE(2, b*x)/(5*x)}
	public void test03264() {
		check("Integrate(ExpIntegralE(-3, b*x)/x^2, x)", "-ExpIntegralE(-3, b*x)/(5*x)+ExpIntegralE(2, b*x)/(5*x)");

	}

	// {ExpIntegralE(-3, b*x)/x^3, x, 1, -ExpIntegralE(-3, b*x)/(6*x^2)+ExpIntegralE(3, b*x)/(6*x^2)}
	public void test03265() {
		check("Integrate(ExpIntegralE(-3, b*x)/x^3, x)", "-ExpIntegralE(-3, b*x)/(6*x^2)+ExpIntegralE(3, b*x)/(6*x^2)");

	}

	// {ExpIntegralE(1, b*x)/x, x, 1, b*x*HypergeometricPFQ({1, 1, 1}, {2, 2, 2}, -(b*x))-EulerGamma*Log(x) -
	// Log(b*x)^2/2}
	public void test03266() {
		check("Integrate(ExpIntegralE(1, b*x)/x, x)",
				"b*x*HypergeometricPFQ({1, 1, 1}, {2, 2, 2}, -(b*x))-EulerGamma*Log(x)-Log(b*x)^2/2");

	}

	// {(d*x)^(3/2)*ExpIntegralE(-3/2, b*x), x, 1, (-4*(d*x)^(5/2)*HypergeometricPFQ({5/2, 5/2}, {7/2, 7/2},
	// -(b*x)))/(25*d)+(3*Sqrt(Pi)*(d*x)^(3/2)*Log(x))/(4*b*(b*x)^(3/2))}
	public void test03267() {
		check("Integrate((d*x)^(3/2)*ExpIntegralE(-3/2, b*x), x)",
				"(-4*(d*x)^(5/2)*HypergeometricPFQ({5/2, 5/2}, {7/2, 7/2}, -(b*x)))/(25*d)+(3*Sqrt(Pi)*(d*x)^(3/2)*Log(x))/(4*b*(b*x)^(3/2))");

	}

	// {Sqrt(d*x)*ExpIntegralE(-1/2, b*x), x, 1, (-4*(d*x)^(3/2)*HypergeometricPFQ({3/2, 3/2}, {5/2, 5/2},
	// -(b*x)))/(9*d)+(Sqrt(Pi)*Sqrt(d*x)*Log(x))/(2*b*Sqrt(b*x))}
	public void test03268() {
		check("Integrate(Sqrt(d*x)*ExpIntegralE(-1/2, b*x), x)",
				"(-4*(d*x)^(3/2)*HypergeometricPFQ({3/2, 3/2}, {5/2, 5/2}, -(b*x)))/(9*d)+(Sqrt(Pi)*Sqrt(d*x)*Log(x))/(2*b*Sqrt(b*x))");

	}

	// {ExpIntegralE(1/2, b*x)/Sqrt(d*x), x, 1, (-4*Sqrt(d*x)*HypergeometricPFQ({1/2, 1/2}, {3/2, 3/2}, -(b*x)))/d +
	// (Sqrt(Pi)*Sqrt(b*x)*Log(x))/(b*Sqrt(d*x))}
	public void test03269() {
		check("Integrate(ExpIntegralE(1/2, b*x)/Sqrt(d*x), x)",
				"(-4*Sqrt(d*x)*HypergeometricPFQ({1/2, 1/2}, {3/2, 3/2}, -(b*x)))/d+(Sqrt(Pi)*Sqrt(b*x)*Log(x))/(b*Sqrt(d*x))");

	}

	// {ExpIntegralE(3/2, b*x)/(d*x)^(3/2), x, 1, (-4*HypergeometricPFQ({-1/2, -1/2}, {1/2, 1/2}, -(b*x)))/(d*Sqrt(d*x))
	// -(2*Sqrt(Pi)*(b*x)^(3/2)*Log(x))/(b*(d*x)^(3/2))}
	public void test03270() {
		check("Integrate(ExpIntegralE(3/2, b*x)/(d*x)^(3/2), x)",
				"(-4*HypergeometricPFQ({-1/2, -1/2}, {1/2, 1/2}, -(b*x)))/(d*Sqrt(d*x))-(2*Sqrt(Pi)*(b*x)^(3/2)*Log(x))/(b*(d*x)^(3/2))");

	}

	// {ExpIntegralE(5/2, b*x)/(d*x)^(5/2), x, 1, (-4*HypergeometricPFQ({-3/2, -3/2}, {-1/2, -1/2},
	// -(b*x)))/(9*d*(d*x)^(3/2))+(4*Sqrt(Pi)*(b*x)^(5/2)*Log(x))/(3*b*(d*x)^(5/2))}
	public void test03271() {
		check("Integrate(ExpIntegralE(5/2, b*x)/(d*x)^(5/2), x)",
				"(-4*HypergeometricPFQ({-3/2, -3/2}, {-1/2, -1/2}, -(b*x)))/(9*d*(d*x)^(3/2))+(4*Sqrt(Pi)*(b*x)^(5/2)*Log(x))/(3*b*(d*x)^(5/2))");

	}

	// {x^m*ExpIntegralE(n, x), x, 1, -((x^(1+m)*ExpIntegralE(-m, x))/(m+n))+(x^(1+m)*ExpIntegralE(n, x))/(m +
	// n)}
	public void test03272() {
		check("Integrate(x^m*ExpIntegralE(n, x), x)",
				"-((x^(1+m)*ExpIntegralE(-m, x))/(m+n))+(x^(1+m)*ExpIntegralE(n, x))/(m+n)");

	}

	// {x^m*ExpIntegralE(n, b*x), x, 1, -((x^(1+m)*ExpIntegralE(-m, b*x))/(m+n))+(x^(1+m)*ExpIntegralE(n,
	// b*x))/(m+n)}
	public void test03273() {
		check("Integrate(x^m*ExpIntegralE(n, b*x), x)",
				"-((x^(1+m)*ExpIntegralE(-m, b*x))/(m+n))+(x^(1+m)*ExpIntegralE(n, b*x))/(m+n)");

	}

	// {(d*x)^m*ExpIntegralE(n, x), x, 1, -(((d*x)^(1+m)*ExpIntegralE(-m, x))/(d*(m+n)))+((d*x)^(1 +
	// m)*ExpIntegralE(n, x))/(d*(m+n))}
	public void test03274() {
		check("Integrate((d*x)^m*ExpIntegralE(n, x), x)",
				"-(((d*x)^(1+m)*ExpIntegralE(-m, x))/(d*(m+n)))+((d*x)^(1+m)*ExpIntegralE(n, x))/(d*(m+n))");

	}

	// {(d*x)^m*ExpIntegralE(n, b*x), x, 1, -(((d*x)^(1+m)*ExpIntegralE(-m, b*x))/(d*(m+n)))+((d*x)^(1 +
	// m)*ExpIntegralE(n, b*x))/(d*(m+n))}
	public void test03275() {
		check("Integrate((d*x)^m*ExpIntegralE(n, b*x), x)",
				"-(((d*x)^(1+m)*ExpIntegralE(-m, b*x))/(d*(m+n)))+((d*x)^(1+m)*ExpIntegralE(n, b*x))/(d*(m+n))");

	}

	// {ExpIntegralE(n, x)/x^n, x, 1, -((x^(1-n)*HypergeometricPFQ({1-n, 1-n}, {2-n, 2-n}, -x))/(1-n)^2) +
	// Gamma(1-n)*Log(x)}
	public void test03276() {
		check("Integrate(ExpIntegralE(n, x)/x^n, x)",
				"-((x^(1-n)*HypergeometricPFQ({1-n, 1-n}, {2-n, 2-n}, -x))/(1-n)^2)+Gamma(1-n)*Log(x)");

	}

	// {ExpIntegralE(n, b*x)/x^n, x, 1, -((x^(1-n)*HypergeometricPFQ({1-n, 1-n}, {2-n, 2-n}, -(b*x)))/(1 -
	// n)^2)+((b*x)^n*Gamma(1-n)*Log(x))/(b*x^n)}
	public void test03277() {
		check("Integrate(ExpIntegralE(n, b*x)/x^n, x)",
				"-((x^(1-n)*HypergeometricPFQ({1-n, 1-n}, {2-n, 2-n}, -(b*x)))/(1-n)^2)+((b*x)^n*Gamma(1-n)*Log(x))/(b*x^n)");

	}

	// {ExpIntegralE(n, x)/(d*x)^n, x, 1, -(((d*x)^(1-n)*HypergeometricPFQ({1-n, 1-n}, {2-n, 2-n}, -x))/(d*(1
	// -n)^2))+(x^n*Gamma(1-n)*Log(x))/(d*x)^n}
	public void test03278() {
		check("Integrate(ExpIntegralE(n, x)/(d*x)^n, x)",
				"-(((d*x)^(1-n)*HypergeometricPFQ({1-n, 1-n}, {2-n, 2-n}, -x))/(d*(1-n)^2))+(x^n*Gamma(1-n)*Log(x))/(d*x)^n");

	}

	// {ExpIntegralE(n, b*x)/(d*x)^n, x, 1, -(((d*x)^(1-n)*HypergeometricPFQ({1-n, 1-n}, {2-n, 2-n},
	// -(b*x)))/(d*(1-n)^2))+((b*x)^n*Gamma(1-n)*Log(x))/(b*(d*x)^n)}
	public void test03279() {
		check("Integrate(ExpIntegralE(n, b*x)/(d*x)^n, x)",
				"-(((d*x)^(1-n)*HypergeometricPFQ({1-n, 1-n}, {2-n, 2-n}, -(b*x)))/(d*(1-n)^2))+((b*x)^n*Gamma(1-n)*Log(x))/(b*(d*x)^n)");

	}

	// {x^2*ExpIntegralE(n, b*x), x, 1, -((x^3*ExpIntegralE(-2, b*x))/(2+n))+(x^3*ExpIntegralE(n, b*x))/(2+n)}
	public void test03280() {
		check("Integrate(x^2*ExpIntegralE(n, b*x), x)",
				"-((x^3*ExpIntegralE(-2, b*x))/(2+n))+(x^3*ExpIntegralE(n, b*x))/(2+n)");

	}

	// {x*ExpIntegralE(n, b*x), x, 1, -((x^2*ExpIntegralE(-1, b*x))/(1+n))+(x^2*ExpIntegralE(n, b*x))/(1+n)}
	public void test03281() {
		check("Integrate(x*ExpIntegralE(n, b*x), x)",
				"-((x^2*ExpIntegralE(-1, b*x))/(1+n))+(x^2*ExpIntegralE(n, b*x))/(1+n)");

	}

	// {ExpIntegralE(n, b*x), x, 1, -(ExpIntegralE(1+n, b*x)/b)}
	public void test03282() {
		check("Integrate(ExpIntegralE(n, b*x), x)", "-(ExpIntegralE(1+n, b*x)/b)");

	}

	// {ExpIntegralE(n, b*x)/x, x, 1, ExpIntegralE(1, b*x)/(1-n)-ExpIntegralE(n, b*x)/(1-n)}
	public void test03283() {
		check("Integrate(ExpIntegralE(n, b*x)/x, x)", "ExpIntegralE(1, b*x)/(1-n)-ExpIntegralE(n, b*x)/(1-n)");

	}

	// {ExpIntegralE(n, b*x)/x^2, x, 1, ExpIntegralE(2, b*x)/((2-n)*x)-ExpIntegralE(n, b*x)/((2-n)*x)}
	public void test03284() {
		check("Integrate(ExpIntegralE(n, b*x)/x^2, x)",
				"ExpIntegralE(2, b*x)/((2-n)*x)-ExpIntegralE(n, b*x)/((2-n)*x)");

	}

	// {ExpIntegralE(n, b*x)/x^3, x, 1, ExpIntegralE(3, b*x)/((3-n)*x^2)-ExpIntegralE(n, b*x)/((3-n)*x^2)}
	public void test03285() {
		check("Integrate(ExpIntegralE(n, b*x)/x^3, x)",
				"ExpIntegralE(3, b*x)/((3-n)*x^2)-ExpIntegralE(n, b*x)/((3-n)*x^2)");

	}

	// {ExpIntegralE(1, a+b*x), x, 1, -(ExpIntegralE(2, a+b*x)/b)}
	public void test03286() {
		check("Integrate(ExpIntegralE(1, a+b*x), x)", "-(ExpIntegralE(2, a+b*x)/b)");

	}

	// {ExpIntegralE(2, a+b*x), x, 1, -(ExpIntegralE(3, a+b*x)/b)}
	public void test03287() {
		check("Integrate(ExpIntegralE(2, a+b*x), x)", "-(ExpIntegralE(3, a+b*x)/b)");

	}

	// {ExpIntegralE(2, a+b*x)/(c+d*x)^2, x, 1, -(ExpIntegralE(2, a+b*x)/(d*(c+d*x))) -
	// (b*Unintegrable(ExpIntegralE(1, a+b*x)/(c+d*x), x))/d}
	public void test03288() {
		check("Integrate(ExpIntegralE(2, a+b*x)/(c+d*x)^2, x)",
				"-(ExpIntegralE(2, a+b*x)/(d*(c+d*x)))-(b*Unintegrable(ExpIntegralE(1, a+b*x)/(c+d*x), x))/d");

	}

	// {ExpIntegralE(3, a+b*x), x, 1, -(ExpIntegralE(4, a+b*x)/b)}
	public void test03289() {
		check("Integrate(ExpIntegralE(3, a+b*x), x)", "-(ExpIntegralE(4, a+b*x)/b)");

	}

	// {ExpIntegralE(3, a+b*x)/(c+d*x)^2, x, 1, -(ExpIntegralE(3, a+b*x)/(d*(c+d*x))) -
	// (b*Unintegrable(ExpIntegralE(2, a+b*x)/(c+d*x), x))/d}
	public void test03290() {
		check("Integrate(ExpIntegralE(3, a+b*x)/(c+d*x)^2, x)",
				"-(ExpIntegralE(3, a+b*x)/(d*(c+d*x)))-(b*Unintegrable(ExpIntegralE(2, a+b*x)/(c+d*x), x))/d");

	}

	// {ExpIntegralE(-1, a+b*x), x, 1, -(E^(-a-b*x)/(b*(a+b*x)))}
	public void test03291() {
		check("Integrate(ExpIntegralE(-1, a+b*x), x)", "-(E^(-a-b*x)/(b*(a+b*x)))");

	}

	// {ExpIntegralE(-2, a+b*x), x, 1, -(ExpIntegralE(-1, a+b*x)/b)}
	public void test03292() {
		check("Integrate(ExpIntegralE(-2, a+b*x), x)", "-(ExpIntegralE(-1, a+b*x)/b)");

	}

	// {ExpIntegralE(-3, a+b*x), x, 1, -(ExpIntegralE(-2, a+b*x)/b)}
	public void test03293() {
		check("Integrate(ExpIntegralE(-3, a+b*x), x)", "-(ExpIntegralE(-2, a+b*x)/b)");

	}

	// {(c+d*x)^m*ExpIntegralE(1, a+b*x), x, 1, (b*CannotIntegrate((E^(-a-b*x)*(c+d*x)^(1+m))/(a+b*x),
	// x))/(d*(1+m))+((c+d*x)^(1+m)*ExpIntegralE(1, a+b*x))/(d*(1+m))}
	public void test03294() {
		check("Integrate((c+d*x)^m*ExpIntegralE(1, a+b*x), x)",
				"(b*CannotIntegrate((E^(-a-b*x)*(c+d*x)^(1+m))/(a+b*x), x))/(d*(1+m))+((c+d*x)^(1+m)*ExpIntegralE(1, a+b*x))/(d*(1+m))");

	}

	// {(c+d*x)^m*ExpIntegralE(-1, a+b*x), x, 1, -((E^(-a-b*x)*(c+d*x)^m)/(b*(a+b*x))) +
	// (d*m*CannotIntegrate((E^(-a-b*x)*(c+d*x)^(-1+m))/(a+b*x), x))/b}
	public void test03295() {
		check("Integrate((c+d*x)^m*ExpIntegralE(-1, a+b*x), x)",
				"-((E^(-a-b*x)*(c+d*x)^m)/(b*(a+b*x)))+(d*m*CannotIntegrate((E^(-a-b*x)*(c+d*x)^(-1+m))/(a+b*x), x))/b");

	}

	// {ExpIntegralE(n, a+b*x), x, 1, -(ExpIntegralE(1+n, a+b*x)/b)}
	public void test03296() {
		check("Integrate(ExpIntegralE(n, a+b*x), x)", "-(ExpIntegralE(1+n, a+b*x)/b)");

	}

	// {ExpIntegralEi(b*x), x, 1, -(E^(b*x)/b)+x*ExpIntegralEi(b*x)}
	public void test03297() {
		check("Integrate(ExpIntegralEi(b*x), x)", "-(E^(b*x)/b)+x*ExpIntegralEi(b*x)");

	}

	// {ExpIntegralEi(a+b*x), x, 1, -(E^(a+b*x)/b)+((a+b*x)*ExpIntegralEi(a+b*x))/b}
	public void test03298() {
		check("Integrate(ExpIntegralEi(a+b*x), x)", "-(E^(a+b*x)/b)+((a+b*x)*ExpIntegralEi(a+b*x))/b");

	}

	// {ExpIntegralEi(a+b*x)^3, x, 1, CannotIntegrate(ExpIntegralEi(a+b*x)^3, x)}
	public void test03299() {
		check("Integrate(ExpIntegralEi(a+b*x)^3, x)", "CannotIntegrate(ExpIntegralEi(a+b*x)^3, x)");

	}

	// {(c+d*x)^m*ExpIntegralEi(a+b*x), x, 1, -((b*CannotIntegrate((E^(a+b*x)*(c+d*x)^(1+m))/(a+b*x),
	// x))/(d*(1+m)))+((c+d*x)^(1+m)*ExpIntegralEi(a+b*x))/(d*(1+m))}
	public void test03300() {
		check("Integrate((c+d*x)^m*ExpIntegralEi(a+b*x), x)",
				"-((b*CannotIntegrate((E^(a+b*x)*(c+d*x)^(1+m))/(a+b*x), x))/(d*(1+m)))+((c+d*x)^(1+m)*ExpIntegralEi(a+b*x))/(d*(1+m))");

	}

	// {(E^(b*x)*ExpIntegralEi(b*x))/x, x, 1, ExpIntegralEi(b*x)^2/2}
	public void test03301() {
		check("Integrate((E^(b*x)*ExpIntegralEi(b*x))/x, x)", "ExpIntegralEi(b*x)^2/2");

	}

	// {LogIntegral(b*x), x, 1, -(ExpIntegralEi(2*Log(b*x))/b)+x*LogIntegral(b*x)}
	public void test03302() {
		check("Integrate(LogIntegral(b*x), x)", "-(ExpIntegralEi(2*Log(b*x))/b)+x*LogIntegral(b*x)");

	}

	// {LogIntegral(b*x)/x, x, 1, -(b*x)+Log(b*x)*LogIntegral(b*x)}
	public void test03303() {
		check("Integrate(LogIntegral(b*x)/x, x)", "-(b*x)+Log(b*x)*LogIntegral(b*x)");

	}

	// {LogIntegral(a+b*x), x, 1, -(ExpIntegralEi(2*Log(a+b*x))/b)+((a+b*x)*LogIntegral(a+b*x))/b}
	public void test03304() {
		check("Integrate(LogIntegral(a+b*x), x)", "-(ExpIntegralEi(2*Log(a+b*x))/b)+((a+b*x)*LogIntegral(a+b*x))/b");

	}

	// {LogIntegral(a+b*x)/x^2, x, 1, -(LogIntegral(a+b*x)/x)+b*Unintegrable(1/(x*Log(a+b*x)), x)}
	public void test03305() {
		check("Integrate(LogIntegral(a+b*x)/x^2, x)", "-(LogIntegral(a+b*x)/x)+b*Unintegrable(1/(x*Log(a+b*x)), x)");

	}

	// {(d*x)^m*LogIntegral(a+b*x), x, 1, ((d*x)^(1+m)*LogIntegral(a+b*x))/(d*(1+m))-(b*Unintegrable((d*x)^(1
	// +m)/Log(a+b*x), x))/(d*(1+m))}
	public void test03306() {
		check("Integrate((d*x)^m*LogIntegral(a+b*x), x)",
				"((d*x)^(1+m)*LogIntegral(a+b*x))/(d*(1+m))-(b*Unintegrable((d*x)^(1+m)/Log(a+b*x), x))/(d*(1+m))");

	}

	// {SinIntegral(b*x), x, 1, Cos(b*x)/b+x*SinIntegral(b*x)}
	public void test03307() {
		check("Integrate(SinIntegral(b*x), x)", "Cos(b*x)/b+x*SinIntegral(b*x)");

	}

	// {SinIntegral(b*x)/x, x, 1, (b*x*HypergeometricPFQ({1, 1, 1}, {2, 2, 2}, (-I)*b*x))/2+(b*x*HypergeometricPFQ({1,
	// 1, 1}, {2, 2, 2}, I*b*x))/2}
	public void test03308() {
		check("Integrate(SinIntegral(b*x)/x, x)",
				"(b*x*HypergeometricPFQ({1, 1, 1}, {2, 2, 2}, (-I)*b*x))/2+(b*x*HypergeometricPFQ({1, 1, 1}, {2, 2, 2}, I*b*x))/2");

	}

	// {x^m*SinIntegral(a+b*x), x, 1, -((b*CannotIntegrate((x^(1+m)*Sin(a+b*x))/(a+b*x), x))/(1+m))+(x^(1 +
	// m)*SinIntegral(a+b*x))/(1+m)}
	public void test03309() {
		check("Integrate(x^m*SinIntegral(a+b*x), x)",
				"-((b*CannotIntegrate((x^(1+m)*Sin(a+b*x))/(a+b*x), x))/(1+m))+(x^(1+m)*SinIntegral(a+b*x))/(1+m)");

	}

	// {SinIntegral(a+b*x), x, 1, Cos(a+b*x)/b+((a+b*x)*SinIntegral(a+b*x))/b}
	public void test03310() {
		check("Integrate(SinIntegral(a+b*x), x)", "Cos(a+b*x)/b+((a+b*x)*SinIntegral(a+b*x))/b");

	}

	// {(Sin(b*x)*SinIntegral(b*x))/x, x, 1, SinIntegral(b*x)^2/2}
	public void test03311() {
		check("Integrate((Sin(b*x)*SinIntegral(b*x))/x, x)", "SinIntegral(b*x)^2/2");

	}

	// {CosIntegral(b*x), x, 1, x*CosIntegral(b*x)-Sin(b*x)/b}
	public void test03312() {
		check("Integrate(CosIntegral(b*x), x)", "x*CosIntegral(b*x)-Sin(b*x)/b");

	}

	// {CosIntegral(b*x)/x, x, 1, (-I/2)*b*x*HypergeometricPFQ({1, 1, 1}, {2, 2, 2}, (-I)*b*x) +
	// (I/2)*b*x*HypergeometricPFQ({1, 1, 1}, {2, 2, 2}, I*b*x)+EulerGamma*Log(x)+Log(b*x)^2/2}
	public void test03313() {
		check("Integrate(CosIntegral(b*x)/x, x)",
				"(-I/2)*b*x*HypergeometricPFQ({1, 1, 1}, {2, 2, 2}, (-I)*b*x)+(I/2)*b*x*HypergeometricPFQ({1, 1, 1}, {2, 2, 2}, I*b*x)+EulerGamma*Log(x)+Log(b*x)^2/2");

	}

	// {x^m*CosIntegral(a+b*x), x, 1, -((b*CannotIntegrate((x^(1+m)*Cos(a+b*x))/(a+b*x), x))/(1+m))+(x^(1 +
	// m)*CosIntegral(a+b*x))/(1+m)}
	public void test03314() {
		check("Integrate(x^m*CosIntegral(a+b*x), x)",
				"-((b*CannotIntegrate((x^(1+m)*Cos(a+b*x))/(a+b*x), x))/(1+m))+(x^(1+m)*CosIntegral(a+b*x))/(1+m)");

	}

	// {CosIntegral(a+b*x), x, 1, ((a+b*x)*CosIntegral(a+b*x))/b-Sin(a+b*x)/b}
	public void test03315() {
		check("Integrate(CosIntegral(a+b*x), x)", "((a+b*x)*CosIntegral(a+b*x))/b-Sin(a+b*x)/b");

	}

	// {(Cos(b*x)*CosIntegral(b*x))/x, x, 1, CosIntegral(b*x)^2/2}
	public void test03316() {
		check("Integrate((Cos(b*x)*CosIntegral(b*x))/x, x)", "CosIntegral(b*x)^2/2");

	}

	// {SinhIntegral(b*x), x, 1, -(Cosh(b*x)/b)+x*SinhIntegral(b*x)}
	public void test03317() {
		check("Integrate(SinhIntegral(b*x), x)", "-(Cosh(b*x)/b)+x*SinhIntegral(b*x)");

	}

	// {SinhIntegral(b*x)/x, x, 1, (b*x*HypergeometricPFQ({1, 1, 1}, {2, 2, 2}, -(b*x)))/2+(b*x*HypergeometricPFQ({1,
	// 1, 1}, {2, 2, 2}, b*x))/2}
	public void test03318() {
		check("Integrate(SinhIntegral(b*x)/x, x)",
				"(b*x*HypergeometricPFQ({1, 1, 1}, {2, 2, 2}, -(b*x)))/2+(b*x*HypergeometricPFQ({1, 1, 1}, {2, 2, 2}, b*x))/2");

	}

	// {x^m*SinhIntegral(a+b*x), x, 1, -((b*CannotIntegrate((x^(1+m)*Sinh(a+b*x))/(a+b*x), x))/(1+m))+(x^(1
	// +m)*SinhIntegral(a+b*x))/(1+m)}
	public void test03319() {
		check("Integrate(x^m*SinhIntegral(a+b*x), x)",
				"-((b*CannotIntegrate((x^(1+m)*Sinh(a+b*x))/(a+b*x), x))/(1+m))+(x^(1+m)*SinhIntegral(a+b*x))/(1+m)");

	}

	// {SinhIntegral(a+b*x), x, 1, -(Cosh(a+b*x)/b)+((a+b*x)*SinhIntegral(a+b*x))/b}
	public void test03320() {
		check("Integrate(SinhIntegral(a+b*x), x)", "-(Cosh(a+b*x)/b)+((a+b*x)*SinhIntegral(a+b*x))/b");

	}

	// {(Sinh(b*x)*SinhIntegral(b*x))/x, x, 1, SinhIntegral(b*x)^2/2}
	public void test03321() {
		check("Integrate((Sinh(b*x)*SinhIntegral(b*x))/x, x)", "SinhIntegral(b*x)^2/2");

	}

	// {CoshIntegral(b*x), x, 1, x*CoshIntegral(b*x)-Sinh(b*x)/b}
	public void test03322() {
		check("Integrate(CoshIntegral(b*x), x)", "x*CoshIntegral(b*x)-Sinh(b*x)/b");

	}

	// {CoshIntegral(b*x)/x, x, 1, -(b*x*HypergeometricPFQ({1, 1, 1}, {2, 2, 2}, -(b*x)))/2+(b*x*HypergeometricPFQ({1,
	// 1, 1}, {2, 2, 2}, b*x))/2+EulerGamma*Log(x)+Log(b*x)^2/2}
	public void test03323() {
		check("Integrate(CoshIntegral(b*x)/x, x)",
				"-(b*x*HypergeometricPFQ({1, 1, 1}, {2, 2, 2}, -(b*x)))/2+(b*x*HypergeometricPFQ({1, 1, 1}, {2, 2, 2}, b*x))/2+EulerGamma*Log(x)+Log(b*x)^2/2");

	}

	// {x^m*CoshIntegral(a+b*x), x, 1, -((b*CannotIntegrate((x^(1+m)*Cosh(a+b*x))/(a+b*x), x))/(1+m))+(x^(1
	// +m)*CoshIntegral(a+b*x))/(1+m)}
	public void test03324() {
		check("Integrate(x^m*CoshIntegral(a+b*x), x)",
				"-((b*CannotIntegrate((x^(1+m)*Cosh(a+b*x))/(a+b*x), x))/(1+m))+(x^(1+m)*CoshIntegral(a+b*x))/(1+m)");

	}

	// {CoshIntegral(a+b*x), x, 1, ((a+b*x)*CoshIntegral(a+b*x))/b-Sinh(a+b*x)/b}
	public void test03325() {
		check("Integrate(CoshIntegral(a+b*x), x)", "((a+b*x)*CoshIntegral(a+b*x))/b-Sinh(a+b*x)/b");

	}

	// {(Cosh(b*x)*CoshIntegral(b*x))/x, x, 1, CoshIntegral(b*x)^2/2}
	public void test03326() {
		check("Integrate((Cosh(b*x)*CoshIntegral(b*x))/x, x)", "CoshIntegral(b*x)^2/2");

	}

	// {x^100*Gamma(0, a*x), x, 1, (x^101*Gamma(0, a*x))/101-Gamma(101, a*x)/(101*a^101)}
	public void test03327() {
		check("Integrate(x^100*Gamma(0, a*x), x)", "(x^101*Gamma(0, a*x))/101-Gamma(101, a*x)/(101*a^101)");

	}

	// {x^2*Gamma(0, a*x), x, 1, (x^3*Gamma(0, a*x))/3-Gamma(3, a*x)/(3*a^3)}
	public void test03328() {
		check("Integrate(x^2*Gamma(0, a*x), x)", "(x^3*Gamma(0, a*x))/3-Gamma(3, a*x)/(3*a^3)");

	}

	// {x*Gamma(0, a*x), x, 1, (x^2*Gamma(0, a*x))/2-Gamma(2, a*x)/(2*a^2)}
	public void test03329() {
		check("Integrate(x*Gamma(0, a*x), x)", "(x^2*Gamma(0, a*x))/2-Gamma(2, a*x)/(2*a^2)");

	}

	// {Gamma(0, a*x), x, 1, -(1/(a*E^(a*x)))+x*Gamma(0, a*x)}
	public void test03330() {
		check("Integrate(Gamma(0, a*x), x)", "-(1/(a*E^(a*x)))+x*Gamma(0, a*x)");

	}

	// {Gamma(0, a*x)/x, x, 1, a*x*HypergeometricPFQ({1, 1, 1}, {2, 2, 2}, -(a*x))-EulerGamma*Log(x)-Log(a*x)^2/2}
	public void test03331() {
		check("Integrate(Gamma(0, a*x)/x, x)",
				"a*x*HypergeometricPFQ({1, 1, 1}, {2, 2, 2}, -(a*x))-EulerGamma*Log(x)-Log(a*x)^2/2");

	}

	// {Gamma(0, a*x)/x^2, x, 1, a*Gamma(-1, a*x)-Gamma(0, a*x)/x}
	public void test03332() {
		check("Integrate(Gamma(0, a*x)/x^2, x)", "a*Gamma(-1, a*x)-Gamma(0, a*x)/x");

	}

	// {Gamma(0, a*x)/x^3, x, 1, (a^2*Gamma(-2, a*x))/2-Gamma(0, a*x)/(2*x^2)}
	public void test03333() {
		check("Integrate(Gamma(0, a*x)/x^3, x)", "(a^2*Gamma(-2, a*x))/2-Gamma(0, a*x)/(2*x^2)");

	}

	// {Gamma(0, a*x)/x^4, x, 1, (a^3*Gamma(-3, a*x))/3-Gamma(0, a*x)/(3*x^3)}
	public void test03334() {
		check("Integrate(Gamma(0, a*x)/x^4, x)", "(a^3*Gamma(-3, a*x))/3-Gamma(0, a*x)/(3*x^3)");

	}

	// {E^(-(a*x)), x, 1, -(1/(a*E^(a*x)))}
	public void test03335() {
		check("Integrate(E^(-(a*x)), x)", "-(1/(a*E^(a*x)))");

	}

	// {1/(E^(a*x)*x), x, 1, ExpIntegralEi(-(a*x))}
	public void test03336() {
		check("Integrate(1/(E^(a*x)*x), x)", "ExpIntegralEi(-(a*x))");

	}

	// {x^100*Gamma(2, a*x), x, 1, (x^101*Gamma(2, a*x))/101-Gamma(103, a*x)/(101*a^101)}
	public void test03337() {
		check("Integrate(x^100*Gamma(2, a*x), x)", "(x^101*Gamma(2, a*x))/101-Gamma(103, a*x)/(101*a^101)");

	}

	// {x^2*Gamma(2, a*x), x, 1, (x^3*Gamma(2, a*x))/3-Gamma(5, a*x)/(3*a^3)}
	public void test03338() {
		check("Integrate(x^2*Gamma(2, a*x), x)", "(x^3*Gamma(2, a*x))/3-Gamma(5, a*x)/(3*a^3)");

	}

	// {x*Gamma(2, a*x), x, 1, (x^2*Gamma(2, a*x))/2-Gamma(4, a*x)/(2*a^2)}
	public void test03339() {
		check("Integrate(x*Gamma(2, a*x), x)", "(x^2*Gamma(2, a*x))/2-Gamma(4, a*x)/(2*a^2)");

	}

	// {Gamma(2, a*x), x, 1, x*Gamma(2, a*x)-Gamma(3, a*x)/a}
	public void test03340() {
		check("Integrate(Gamma(2, a*x), x)", "x*Gamma(2, a*x)-Gamma(3, a*x)/a");

	}

	// {Gamma(2, a*x)/x^2, x, 1, a/E^(a*x)-Gamma(2, a*x)/x}
	public void test03341() {
		check("Integrate(Gamma(2, a*x)/x^2, x)", "a/E^(a*x)-Gamma(2, a*x)/x");

	}

	// {Gamma(2, a*x)/x^3, x, 1, (a^2*Gamma(0, a*x))/2-Gamma(2, a*x)/(2*x^2)}
	public void test03342() {
		check("Integrate(Gamma(2, a*x)/x^3, x)", "(a^2*Gamma(0, a*x))/2-Gamma(2, a*x)/(2*x^2)");

	}

	// {Gamma(2, a*x)/x^4, x, 1, (a^3*Gamma(-1, a*x))/3-Gamma(2, a*x)/(3*x^3)}
	public void test03343() {
		check("Integrate(Gamma(2, a*x)/x^4, x)", "(a^3*Gamma(-1, a*x))/3-Gamma(2, a*x)/(3*x^3)");

	}

	// {x^100*Gamma(3, a*x), x, 1, (x^101*Gamma(3, a*x))/101-Gamma(104, a*x)/(101*a^101)}
	public void test03344() {
		check("Integrate(x^100*Gamma(3, a*x), x)", "(x^101*Gamma(3, a*x))/101-Gamma(104, a*x)/(101*a^101)");

	}

	// {x^2*Gamma(3, a*x), x, 1, (x^3*Gamma(3, a*x))/3-Gamma(6, a*x)/(3*a^3)}
	public void test03345() {
		check("Integrate(x^2*Gamma(3, a*x), x)", "(x^3*Gamma(3, a*x))/3-Gamma(6, a*x)/(3*a^3)");

	}

	// {x*Gamma(3, a*x), x, 1, (x^2*Gamma(3, a*x))/2-Gamma(5, a*x)/(2*a^2)}
	public void test03346() {
		check("Integrate(x*Gamma(3, a*x), x)", "(x^2*Gamma(3, a*x))/2-Gamma(5, a*x)/(2*a^2)");

	}

	// {Gamma(3, a*x), x, 1, x*Gamma(3, a*x)-Gamma(4, a*x)/a}
	public void test03347() {
		check("Integrate(Gamma(3, a*x), x)", "x*Gamma(3, a*x)-Gamma(4, a*x)/a");

	}

	// {Gamma(3, a*x)/x^2, x, 1, a*Gamma(2, a*x)-Gamma(3, a*x)/x}
	public void test03348() {
		check("Integrate(Gamma(3, a*x)/x^2, x)", "a*Gamma(2, a*x)-Gamma(3, a*x)/x");

	}

	// {Gamma(3, a*x)/x^3, x, 1, a^2/(2*E^(a*x))-Gamma(3, a*x)/(2*x^2)}
	public void test03349() {
		check("Integrate(Gamma(3, a*x)/x^3, x)", "a^2/(2*E^(a*x))-Gamma(3, a*x)/(2*x^2)");

	}

	// {Gamma(3, a*x)/x^4, x, 1, (a^3*Gamma(0, a*x))/3-Gamma(3, a*x)/(3*x^3)}
	public void test03350() {
		check("Integrate(Gamma(3, a*x)/x^4, x)", "(a^3*Gamma(0, a*x))/3-Gamma(3, a*x)/(3*x^3)");

	}

	// {x^100*Gamma(-1, a*x), x, 1, (x^101*Gamma(-1, a*x))/101-Gamma(100, a*x)/(101*a^101)}
	public void test03351() {
		check("Integrate(x^100*Gamma(-1, a*x), x)", "(x^101*Gamma(-1, a*x))/101-Gamma(100, a*x)/(101*a^101)");

	}

	// {x^3*Gamma(-1, a*x), x, 1, (x^4*Gamma(-1, a*x))/4-Gamma(3, a*x)/(4*a^4)}
	public void test03352() {
		check("Integrate(x^3*Gamma(-1, a*x), x)", "(x^4*Gamma(-1, a*x))/4-Gamma(3, a*x)/(4*a^4)");

	}

	// {x^2*Gamma(-1, a*x), x, 1, (x^3*Gamma(-1, a*x))/3-Gamma(2, a*x)/(3*a^3)}
	public void test03353() {
		check("Integrate(x^2*Gamma(-1, a*x), x)", "(x^3*Gamma(-1, a*x))/3-Gamma(2, a*x)/(3*a^3)");

	}

	// {x*Gamma(-1, a*x), x, 1, -1/(2*a^2*E^(a*x))+(x^2*Gamma(-1, a*x))/2}
	public void test03354() {
		check("Integrate(x*Gamma(-1, a*x), x)", "-1/(2*a^2*E^(a*x))+(x^2*Gamma(-1, a*x))/2");

	}

	// {Gamma(-1, a*x), x, 1, x*Gamma(-1, a*x)-Gamma(0, a*x)/a}
	public void test03355() {
		check("Integrate(Gamma(-1, a*x), x)", "x*Gamma(-1, a*x)-Gamma(0, a*x)/a");

	}

	// {Gamma(-1, a*x)/x^2, x, 1, a*Gamma(-2, a*x)-Gamma(-1, a*x)/x}
	public void test03356() {
		check("Integrate(Gamma(-1, a*x)/x^2, x)", "a*Gamma(-2, a*x)-Gamma(-1, a*x)/x");

	}

	// {Gamma(-1, a*x)/x^3, x, 1, (a^2*Gamma(-3, a*x))/2-Gamma(-1, a*x)/(2*x^2)}
	public void test03357() {
		check("Integrate(Gamma(-1, a*x)/x^3, x)", "(a^2*Gamma(-3, a*x))/2-Gamma(-1, a*x)/(2*x^2)");

	}

	// {Gamma(-1, a*x)/x^4, x, 1, (a^3*Gamma(-4, a*x))/3-Gamma(-1, a*x)/(3*x^3)}
	public void test03358() {
		check("Integrate(Gamma(-1, a*x)/x^4, x)", "(a^3*Gamma(-4, a*x))/3-Gamma(-1, a*x)/(3*x^3)");

	}

	// {x^100*Gamma(-2, a*x), x, 1, (x^101*Gamma(-2, a*x))/101-Gamma(99, a*x)/(101*a^101)}
	public void test03359() {
		check("Integrate(x^100*Gamma(-2, a*x), x)", "(x^101*Gamma(-2, a*x))/101-Gamma(99, a*x)/(101*a^101)");

	}

	// {x^3*Gamma(-2, a*x), x, 1, (x^4*Gamma(-2, a*x))/4-Gamma(2, a*x)/(4*a^4)}
	public void test03360() {
		check("Integrate(x^3*Gamma(-2, a*x), x)", "(x^4*Gamma(-2, a*x))/4-Gamma(2, a*x)/(4*a^4)");

	}

	// {x^2*Gamma(-2, a*x), x, 1, -1/(3*a^3*E^(a*x))+(x^3*Gamma(-2, a*x))/3}
	public void test03361() {
		check("Integrate(x^2*Gamma(-2, a*x), x)", "-1/(3*a^3*E^(a*x))+(x^3*Gamma(-2, a*x))/3");

	}

	// {x*Gamma(-2, a*x), x, 1, (x^2*Gamma(-2, a*x))/2-Gamma(0, a*x)/(2*a^2)}
	public void test03362() {
		check("Integrate(x*Gamma(-2, a*x), x)", "(x^2*Gamma(-2, a*x))/2-Gamma(0, a*x)/(2*a^2)");

	}

	// {Gamma(-2, a*x), x, 1, x*Gamma(-2, a*x)-Gamma(-1, a*x)/a}
	public void test03363() {
		check("Integrate(Gamma(-2, a*x), x)", "x*Gamma(-2, a*x)-Gamma(-1, a*x)/a");

	}

	// {Gamma(-2, a*x)/x^2, x, 1, a*Gamma(-3, a*x)-Gamma(-2, a*x)/x}
	public void test03364() {
		check("Integrate(Gamma(-2, a*x)/x^2, x)", "a*Gamma(-3, a*x)-Gamma(-2, a*x)/x");

	}

	// {Gamma(-2, a*x)/x^3, x, 1, (a^2*Gamma(-4, a*x))/2-Gamma(-2, a*x)/(2*x^2)}
	public void test03365() {
		check("Integrate(Gamma(-2, a*x)/x^3, x)", "(a^2*Gamma(-4, a*x))/2-Gamma(-2, a*x)/(2*x^2)");

	}

	// {Gamma(-2, a*x)/x^4, x, 1, (a^3*Gamma(-5, a*x))/3-Gamma(-2, a*x)/(3*x^3)}
	public void test03366() {
		check("Integrate(Gamma(-2, a*x)/x^4, x)", "(a^3*Gamma(-5, a*x))/3-Gamma(-2, a*x)/(3*x^3)");

	}

	// {x^100*Gamma(-3, a*x), x, 1, (x^101*Gamma(-3, a*x))/101-Gamma(98, a*x)/(101*a^101)}
	public void test03367() {
		check("Integrate(x^100*Gamma(-3, a*x), x)", "(x^101*Gamma(-3, a*x))/101-Gamma(98, a*x)/(101*a^101)");

	}

	// {x^3*Gamma(-3, a*x), x, 1, -1/(4*a^4*E^(a*x))+(x^4*Gamma(-3, a*x))/4}
	public void test03368() {
		check("Integrate(x^3*Gamma(-3, a*x), x)", "-1/(4*a^4*E^(a*x))+(x^4*Gamma(-3, a*x))/4");

	}

	// {x^2*Gamma(-3, a*x), x, 1, (x^3*Gamma(-3, a*x))/3-Gamma(0, a*x)/(3*a^3)}
	public void test03369() {
		check("Integrate(x^2*Gamma(-3, a*x), x)", "(x^3*Gamma(-3, a*x))/3-Gamma(0, a*x)/(3*a^3)");

	}

	// {x*Gamma(-3, a*x), x, 1, (x^2*Gamma(-3, a*x))/2-Gamma(-1, a*x)/(2*a^2)}
	public void test03370() {
		check("Integrate(x*Gamma(-3, a*x), x)", "(x^2*Gamma(-3, a*x))/2-Gamma(-1, a*x)/(2*a^2)");

	}

	// {Gamma(-3, a*x), x, 1, x*Gamma(-3, a*x)-Gamma(-2, a*x)/a}
	public void test03371() {
		check("Integrate(Gamma(-3, a*x), x)", "x*Gamma(-3, a*x)-Gamma(-2, a*x)/a");

	}

	// {Gamma(-3, a*x)/x^2, x, 1, a*Gamma(-4, a*x)-Gamma(-3, a*x)/x}
	public void test03372() {
		check("Integrate(Gamma(-3, a*x)/x^2, x)", "a*Gamma(-4, a*x)-Gamma(-3, a*x)/x");

	}

	// {Gamma(-3, a*x)/x^3, x, 1, (a^2*Gamma(-5, a*x))/2-Gamma(-3, a*x)/(2*x^2)}
	public void test03373() {
		check("Integrate(Gamma(-3, a*x)/x^3, x)", "(a^2*Gamma(-5, a*x))/2-Gamma(-3, a*x)/(2*x^2)");

	}

	// {Gamma(-3, a*x)/x^4, x, 1, (a^3*Gamma(-6, a*x))/3-Gamma(-3, a*x)/(3*x^3)}
	public void test03374() {
		check("Integrate(Gamma(-3, a*x)/x^4, x)", "(a^3*Gamma(-6, a*x))/3-Gamma(-3, a*x)/(3*x^3)");

	}

	// {x^100*Gamma(1/2, a*x), x, 1, (x^101*Gamma(1/2, a*x))/101-Gamma(203/2, a*x)/(101*a^101)}
	public void test03375() {
		check("Integrate(x^100*Gamma(1/2, a*x), x)", "(x^101*Gamma(1/2, a*x))/101-Gamma(203/2, a*x)/(101*a^101)");

	}

	// {x^2*Gamma(1/2, a*x), x, 1, (x^3*Gamma(1/2, a*x))/3-Gamma(7/2, a*x)/(3*a^3)}
	public void test03376() {
		check("Integrate(x^2*Gamma(1/2, a*x), x)", "(x^3*Gamma(1/2, a*x))/3-Gamma(7/2, a*x)/(3*a^3)");

	}

	// {x*Gamma(1/2, a*x), x, 1, (x^2*Gamma(1/2, a*x))/2-Gamma(5/2, a*x)/(2*a^2)}
	public void test03377() {
		check("Integrate(x*Gamma(1/2, a*x), x)", "(x^2*Gamma(1/2, a*x))/2-Gamma(5/2, a*x)/(2*a^2)");

	}

	// {Gamma(1/2, a*x), x, 1, x*Gamma(1/2, a*x)-Gamma(3/2, a*x)/a}
	public void test03378() {
		check("Integrate(Gamma(1/2, a*x), x)", "x*Gamma(1/2, a*x)-Gamma(3/2, a*x)/a");

	}

	// {Gamma(1/2, a*x)/x, x, 1, -4*Sqrt(a*x)*HypergeometricPFQ({1/2, 1/2}, {3/2, 3/2}, -(a*x))+Sqrt(Pi)*Log(x)}
	public void test03379() {
		check("Integrate(Gamma(1/2, a*x)/x, x)",
				"-4*Sqrt(a*x)*HypergeometricPFQ({1/2, 1/2}, {3/2, 3/2}, -(a*x))+Sqrt(Pi)*Log(x)");

	}

	// {Gamma(1/2, a*x)/x^2, x, 1, a*Gamma(-1/2, a*x)-Gamma(1/2, a*x)/x}
	public void test03380() {
		check("Integrate(Gamma(1/2, a*x)/x^2, x)", "a*Gamma(-1/2, a*x)-Gamma(1/2, a*x)/x");

	}

	// {Gamma(1/2, a*x)/x^3, x, 1, (a^2*Gamma(-3/2, a*x))/2-Gamma(1/2, a*x)/(2*x^2)}
	public void test03381() {
		check("Integrate(Gamma(1/2, a*x)/x^3, x)", "(a^2*Gamma(-3/2, a*x))/2-Gamma(1/2, a*x)/(2*x^2)");

	}

	// {Gamma(1/2, a*x)/x^4, x, 1, (a^3*Gamma(-5/2, a*x))/3-Gamma(1/2, a*x)/(3*x^3)}
	public void test03382() {
		check("Integrate(Gamma(1/2, a*x)/x^4, x)", "(a^3*Gamma(-5/2, a*x))/3-Gamma(1/2, a*x)/(3*x^3)");

	}

	// {x^100*Gamma(3/2, a*x), x, 1, (x^101*Gamma(3/2, a*x))/101-Gamma(205/2, a*x)/(101*a^101)}
	public void test03383() {
		check("Integrate(x^100*Gamma(3/2, a*x), x)", "(x^101*Gamma(3/2, a*x))/101-Gamma(205/2, a*x)/(101*a^101)");

	}

	// {x^2*Gamma(3/2, a*x), x, 1, (x^3*Gamma(3/2, a*x))/3-Gamma(9/2, a*x)/(3*a^3)}
	public void test03384() {
		check("Integrate(x^2*Gamma(3/2, a*x), x)", "(x^3*Gamma(3/2, a*x))/3-Gamma(9/2, a*x)/(3*a^3)");

	}

	// {x*Gamma(3/2, a*x), x, 1, (x^2*Gamma(3/2, a*x))/2-Gamma(7/2, a*x)/(2*a^2)}
	public void test03385() {
		check("Integrate(x*Gamma(3/2, a*x), x)", "(x^2*Gamma(3/2, a*x))/2-Gamma(7/2, a*x)/(2*a^2)");

	}

	// {Gamma(3/2, a*x), x, 1, x*Gamma(3/2, a*x)-Gamma(5/2, a*x)/a}
	public void test03386() {
		check("Integrate(Gamma(3/2, a*x), x)", "x*Gamma(3/2, a*x)-Gamma(5/2, a*x)/a");

	}

	// {Gamma(3/2, a*x)/x, x, 1, (-4*(a*x)^(3/2)*HypergeometricPFQ({3/2, 3/2}, {5/2, 5/2}, -(a*x)))/9 +
	// (Sqrt(Pi)*Log(x))/2}
	public void test03387() {
		check("Integrate(Gamma(3/2, a*x)/x, x)",
				"(-4*(a*x)^(3/2)*HypergeometricPFQ({3/2, 3/2}, {5/2, 5/2}, -(a*x)))/9+(Sqrt(Pi)*Log(x))/2");

	}

	// {Gamma(3/2, a*x)/x^2, x, 1, a*Gamma(1/2, a*x)-Gamma(3/2, a*x)/x}
	public void test03388() {
		check("Integrate(Gamma(3/2, a*x)/x^2, x)", "a*Gamma(1/2, a*x)-Gamma(3/2, a*x)/x");

	}

	// {Gamma(3/2, a*x)/x^3, x, 1, (a^2*Gamma(-1/2, a*x))/2-Gamma(3/2, a*x)/(2*x^2)}
	public void test03389() {
		check("Integrate(Gamma(3/2, a*x)/x^3, x)", "(a^2*Gamma(-1/2, a*x))/2-Gamma(3/2, a*x)/(2*x^2)");

	}

	// {Gamma(3/2, a*x)/x^4, x, 1, (a^3*Gamma(-3/2, a*x))/3-Gamma(3/2, a*x)/(3*x^3)}
	public void test03390() {
		check("Integrate(Gamma(3/2, a*x)/x^4, x)", "(a^3*Gamma(-3/2, a*x))/3-Gamma(3/2, a*x)/(3*x^3)");

	}

	// {(d*x)^m*Gamma(3, b*x), x, 1, ((d*x)^(1+m)*Gamma(3, b*x))/(d*(1+m))-((d*x)^m*Gamma(4+m, b*x))/(b*(1 +
	// m)*(b*x)^m)}
	public void test03391() {
		check("Integrate((d*x)^m*Gamma(3, b*x), x)",
				"((d*x)^(1+m)*Gamma(3, b*x))/(d*(1+m))-((d*x)^m*Gamma(4+m, b*x))/(b*(1+m)*(b*x)^m)");

	}

	// {(d*x)^m*Gamma(2, b*x), x, 1, ((d*x)^(1+m)*Gamma(2, b*x))/(d*(1+m))-((d*x)^m*Gamma(3+m, b*x))/(b*(1 +
	// m)*(b*x)^m)}
	public void test03392() {
		check("Integrate((d*x)^m*Gamma(2, b*x), x)",
				"((d*x)^(1+m)*Gamma(2, b*x))/(d*(1+m))-((d*x)^m*Gamma(3+m, b*x))/(b*(1+m)*(b*x)^m)");

	}

	// {(d*x)^m/E^(b*x), x, 1, -(((d*x)^m*Gamma(1+m, b*x))/(b*(b*x)^m))}
	public void test03393() {
		check("Integrate((d*x)^m/E^(b*x), x)", "-(((d*x)^m*Gamma(1+m, b*x))/(b*(b*x)^m))");

	}

	// {(d*x)^m*Gamma(0, b*x), x, 1, ((d*x)^(1+m)*Gamma(0, b*x))/(d*(1+m))-((d*x)^m*Gamma(1+m, b*x))/(b*(1 +
	// m)*(b*x)^m)}
	public void test03394() {
		check("Integrate((d*x)^m*Gamma(0, b*x), x)",
				"((d*x)^(1+m)*Gamma(0, b*x))/(d*(1+m))-((d*x)^m*Gamma(1+m, b*x))/(b*(1+m)*(b*x)^m)");

	}

	// {(d*x)^m*Gamma(-1, b*x), x, 1, ((d*x)^(1+m)*Gamma(-1, b*x))/(d*(1+m))-((d*x)^m*Gamma(m, b*x))/(b*(1 +
	// m)*(b*x)^m)}
	public void test03395() {
		check("Integrate((d*x)^m*Gamma(-1, b*x), x)",
				"((d*x)^(1+m)*Gamma(-1, b*x))/(d*(1+m))-((d*x)^m*Gamma(m, b*x))/(b*(1+m)*(b*x)^m)");

	}

	// {(d*x)^m*Gamma(-2, b*x), x, 1, ((d*x)^(1+m)*Gamma(-2, b*x))/(d*(1+m))-((d*x)^m*Gamma(-1+m, b*x))/(b*(1 +
	// m)*(b*x)^m)}
	public void test03396() {
		check("Integrate((d*x)^m*Gamma(-2, b*x), x)",
				"((d*x)^(1+m)*Gamma(-2, b*x))/(d*(1+m))-((d*x)^m*Gamma(-1+m, b*x))/(b*(1+m)*(b*x)^m)");

	}

	// {x^m*Gamma(n, x), x, 1, (x^(1+m)*Gamma(n, x))/(1+m)-Gamma(1+m+n, x)/(1+m)}
	public void test03397() {
		check("Integrate(x^m*Gamma(n, x), x)", "(x^(1+m)*Gamma(n, x))/(1+m)-Gamma(1+m+n, x)/(1+m)");

	}

	// {x^m*Gamma(n, b*x), x, 1, (x^(1+m)*Gamma(n, b*x))/(1+m)-(x^m*Gamma(1+m+n, b*x))/(b*(1+m)*(b*x)^m)}
	public void test03398() {
		check("Integrate(x^m*Gamma(n, b*x), x)",
				"(x^(1+m)*Gamma(n, b*x))/(1+m)-(x^m*Gamma(1+m+n, b*x))/(b*(1+m)*(b*x)^m)");

	}

	// {(d*x)^m*Gamma(n, x), x, 1, ((d*x)^(1+m)*Gamma(n, x))/(d*(1+m))-((d*x)^m*Gamma(1+m+n, x))/((1 +
	// m)*x^m)}
	public void test03399() {
		check("Integrate((d*x)^m*Gamma(n, x), x)",
				"((d*x)^(1+m)*Gamma(n, x))/(d*(1+m))-((d*x)^m*Gamma(1+m+n, x))/((1+m)*x^m)");

	}

	// {(d*x)^m*Gamma(n, b*x), x, 1, ((d*x)^(1+m)*Gamma(n, b*x))/(d*(1+m))-((d*x)^m*Gamma(1+m+n, b*x))/(b*(1 +
	// m)*(b*x)^m)}
	public void test03400() {
		check("Integrate((d*x)^m*Gamma(n, b*x), x)",
				"((d*x)^(1+m)*Gamma(n, b*x))/(d*(1+m))-((d*x)^m*Gamma(1+m+n, b*x))/(b*(1+m)*(b*x)^m)");

	}

	// {x^100*Gamma(n, a*x), x, 1, (x^101*Gamma(n, a*x))/101-Gamma(101+n, a*x)/(101*a^101)}
	public void test03401() {
		check("Integrate(x^100*Gamma(n, a*x), x)", "(x^101*Gamma(n, a*x))/101-Gamma(101+n, a*x)/(101*a^101)");

	}

	// {x^2*Gamma(n, a*x), x, 1, (x^3*Gamma(n, a*x))/3-Gamma(3+n, a*x)/(3*a^3)}
	public void test03402() {
		check("Integrate(x^2*Gamma(n, a*x), x)", "(x^3*Gamma(n, a*x))/3-Gamma(3+n, a*x)/(3*a^3)");

	}

	// {x*Gamma(n, a*x), x, 1, (x^2*Gamma(n, a*x))/2-Gamma(2+n, a*x)/(2*a^2)}
	public void test03403() {
		check("Integrate(x*Gamma(n, a*x), x)", "(x^2*Gamma(n, a*x))/2-Gamma(2+n, a*x)/(2*a^2)");

	}

	// {Gamma(n, a*x), x, 1, x*Gamma(n, a*x)-Gamma(1+n, a*x)/a}
	public void test03404() {
		check("Integrate(Gamma(n, a*x), x)", "x*Gamma(n, a*x)-Gamma(1+n, a*x)/a");

	}

	// {Gamma(n, a*x)/x, x, 1, -(((a*x)^n*HypergeometricPFQ({n, n}, {1+n, 1+n}, -(a*x)))/n^2)+Gamma(n)*Log(x)}
	public void test03405() {
		check("Integrate(Gamma(n, a*x)/x, x)",
				"-(((a*x)^n*HypergeometricPFQ({n, n}, {1+n, 1+n}, -(a*x)))/n^2)+Gamma(n)*Log(x)");

	}

	// {Gamma(n, a*x)/x^2, x, 1, a*Gamma(-1+n, a*x)-Gamma(n, a*x)/x}
	public void test03406() {
		check("Integrate(Gamma(n, a*x)/x^2, x)", "a*Gamma(-1+n, a*x)-Gamma(n, a*x)/x");

	}

	// {Gamma(n, a*x)/x^3, x, 1, (a^2*Gamma(-2+n, a*x))/2-Gamma(n, a*x)/(2*x^2)}
	public void test03407() {
		check("Integrate(Gamma(n, a*x)/x^3, x)", "(a^2*Gamma(-2+n, a*x))/2-Gamma(n, a*x)/(2*x^2)");

	}

	// {Gamma(n, a*x)/x^4, x, 1, (a^3*Gamma(-3+n, a*x))/3-Gamma(n, a*x)/(3*x^3)}
	public void test03408() {
		check("Integrate(Gamma(n, a*x)/x^4, x)", "(a^3*Gamma(-3+n, a*x))/3-Gamma(n, a*x)/(3*x^3)");

	}

	// {x^100*Gamma(n, 2*x), x, 1, (x^101*Gamma(n, 2*x))/101-Gamma(101+n, 2*x)/256065421246102339102334047485952}
	public void test03409() {
		check("Integrate(x^100*Gamma(n, 2*x), x)",
				"(x^101*Gamma(n, 2*x))/101-Gamma(101+n, 2*x)/256065421246102339102334047485952");

	}

	// {x^2*Gamma(n, 2*x), x, 1, (x^3*Gamma(n, 2*x))/3-Gamma(3+n, 2*x)/24}
	public void test03410() {
		check("Integrate(x^2*Gamma(n, 2*x), x)", "(x^3*Gamma(n, 2*x))/3-Gamma(3+n, 2*x)/24");

	}

	// {x*Gamma(n, 2*x), x, 1, (x^2*Gamma(n, 2*x))/2-Gamma(2+n, 2*x)/8}
	public void test03411() {
		check("Integrate(x*Gamma(n, 2*x), x)", "(x^2*Gamma(n, 2*x))/2-Gamma(2+n, 2*x)/8");

	}

	// {Gamma(n, 2*x), x, 1, x*Gamma(n, 2*x)-Gamma(1+n, 2*x)/2}
	public void test03412() {
		check("Integrate(Gamma(n, 2*x), x)", "x*Gamma(n, 2*x)-Gamma(1+n, 2*x)/2");

	}

	// {Gamma(n, 2*x)/x, x, 1, -((2^n*x^n*HypergeometricPFQ({n, n}, {1+n, 1+n}, -2*x))/n^2)+Gamma(n)*Log(x)}
	public void test03413() {
		check("Integrate(Gamma(n, 2*x)/x, x)",
				"-((2^n*x^n*HypergeometricPFQ({n, n}, {1+n, 1+n}, -2*x))/n^2)+Gamma(n)*Log(x)");

	}

	// {Gamma(n, 2*x)/x^2, x, 1, 2*Gamma(-1+n, 2*x)-Gamma(n, 2*x)/x}
	public void test03414() {
		check("Integrate(Gamma(n, 2*x)/x^2, x)", "2*Gamma(-1+n, 2*x)-Gamma(n, 2*x)/x");

	}

	// {Gamma(n, 2*x)/x^3, x, 1, 2*Gamma(-2+n, 2*x)-Gamma(n, 2*x)/(2*x^2)}
	public void test03415() {
		check("Integrate(Gamma(n, 2*x)/x^3, x)", "2*Gamma(-2+n, 2*x)-Gamma(n, 2*x)/(2*x^2)");

	}

	// {Gamma(n, 2*x)/x^4, x, 1, (8*Gamma(-3+n, 2*x))/3-Gamma(n, 2*x)/(3*x^3)}
	public void test03416() {
		check("Integrate(Gamma(n, 2*x)/x^4, x)", "(8*Gamma(-3+n, 2*x))/3-Gamma(n, 2*x)/(3*x^3)");

	}

	// {Gamma(0, a+b*x), x, 1, -(E^(-a-b*x)/b)+((a+b*x)*Gamma(0, a+b*x))/b}
	public void test03417() {
		check("Integrate(Gamma(0, a+b*x), x)", "-(E^(-a-b*x)/b)+((a+b*x)*Gamma(0, a+b*x))/b");

	}

	// {E^(-a-b*x), x, 1, -(E^(-a-b*x)/b)}
	public void test03418() {
		check("Integrate(E^(-a-b*x), x)", "-(E^(-a-b*x)/b)");

	}

	// {E^(-a-b*x)/(c+d*x), x, 1, (E^(-a+(b*c)/d)*ExpIntegralEi(-((b*(c+d*x))/d)))/d}
	public void test03419() {
		check("Integrate(E^(-a-b*x)/(c+d*x), x)", "(E^(-a+(b*c)/d)*ExpIntegralEi(-((b*(c+d*x))/d)))/d");

	}

	// {Gamma(2, a+b*x), x, 1, ((a+b*x)*Gamma(2, a+b*x))/b-Gamma(3, a+b*x)/b}
	public void test03420() {
		check("Integrate(Gamma(2, a+b*x), x)", "((a+b*x)*Gamma(2, a+b*x))/b-Gamma(3, a+b*x)/b");

	}

	// {Gamma(3, a+b*x), x, 1, ((a+b*x)*Gamma(3, a+b*x))/b-Gamma(4, a+b*x)/b}
	public void test03421() {
		check("Integrate(Gamma(3, a+b*x), x)", "((a+b*x)*Gamma(3, a+b*x))/b-Gamma(4, a+b*x)/b");

	}

	// {Gamma(-1, a+b*x), x, 1, ((a+b*x)*Gamma(-1, a+b*x))/b-Gamma(0, a+b*x)/b}
	public void test03422() {
		check("Integrate(Gamma(-1, a+b*x), x)", "((a+b*x)*Gamma(-1, a+b*x))/b-Gamma(0, a+b*x)/b");

	}

	// {Gamma(-2, a+b*x), x, 1, ((a+b*x)*Gamma(-2, a+b*x))/b-Gamma(-1, a+b*x)/b}
	public void test03423() {
		check("Integrate(Gamma(-2, a+b*x), x)", "((a+b*x)*Gamma(-2, a+b*x))/b-Gamma(-1, a+b*x)/b");

	}

	// {Gamma(-3, a+b*x), x, 1, ((a+b*x)*Gamma(-3, a+b*x))/b-Gamma(-2, a+b*x)/b}
	public void test03424() {
		check("Integrate(Gamma(-3, a+b*x), x)", "((a+b*x)*Gamma(-3, a+b*x))/b-Gamma(-2, a+b*x)/b");

	}

	// {E^(-a-b*x)*(c+d*x)^m, x, 1, -((E^(-a+(b*c)/d)*(c+d*x)^m*Gamma(1+m, (b*(c+d*x))/d))/(b*((b*(c +
	// d*x))/d)^m))}
	public void test03425() {
		check("Integrate(E^(-a-b*x)*(c+d*x)^m, x)",
				"-((E^(-a+(b*c)/d)*(c+d*x)^m*Gamma(1+m, (b*(c+d*x))/d))/(b*((b*(c+d*x))/d)^m))");

	}

	// {Gamma(n, a+b*x), x, 1, ((a+b*x)*Gamma(n, a+b*x))/b-Gamma(1+n, a+b*x)/b}
	public void test03426() {
		check("Integrate(Gamma(n, a+b*x), x)", "((a+b*x)*Gamma(n, a+b*x))/b-Gamma(1+n, a+b*x)/b");

	}

	// {LogGamma(a+b*x), x, 1, PolyGamma(-2, a+b*x)/b}
	public void test03427() {
		check("Integrate(LogGamma(a+b*x), x)", "PolyGamma(-2, a+b*x)/b");

	}

	// {PolyGamma(n, a+b*x), x, 1, PolyGamma(-1+n, a+b*x)/b}
	public void test03428() {
		check("Integrate(PolyGamma(n, a+b*x), x)", "PolyGamma(-1+n, a+b*x)/b");

	}

	// {PolyGamma(n, a+b*x)/(c+d*x)^2, x, 1, -(PolyGamma(n, a+b*x)/(d*(c+d*x)))+(b*Unintegrable(PolyGamma(1 +
	// n, a+b*x)/(c+d*x), x))/d}
	public void test03429() {
		check("Integrate(PolyGamma(n, a+b*x)/(c+d*x)^2, x)",
				"-(PolyGamma(n, a+b*x)/(d*(c+d*x)))+(b*Unintegrable(PolyGamma(1+n, a+b*x)/(c+d*x), x))/d");

	}

	// {Sqrt(c+d*x)*PolyGamma(n, a+b*x), x, 1, (Sqrt(c+d*x)*PolyGamma(-1+n, a+b*x))/b -
	// (d*Unintegrable(PolyGamma(-1+n, a+b*x)/Sqrt(c+d*x), x))/(2*b)}
	public void test03430() {
		check("Integrate(Sqrt(c+d*x)*PolyGamma(n, a+b*x), x)",
				"(Sqrt(c+d*x)*PolyGamma(-1+n, a+b*x))/b-(d*Unintegrable(PolyGamma(-1+n, a+b*x)/Sqrt(c+d*x), x))/(2*b)");

	}

	// {PolyGamma(n, a+b*x)/(c+d*x)^(3/2), x, 1, (-2*PolyGamma(n, a+b*x))/(d*Sqrt(c+d*x)) +
	// (2*b*Unintegrable(PolyGamma(1+n, a+b*x)/Sqrt(c+d*x), x))/d}
	public void test03431() {
		check("Integrate(PolyGamma(n, a+b*x)/(c+d*x)^(3/2), x)",
				"(-2*PolyGamma(n, a+b*x))/(d*Sqrt(c+d*x))+(2*b*Unintegrable(PolyGamma(1+n, a+b*x)/Sqrt(c+d*x), x))/d");

	}

	// {Gamma(a+b*x)^n*PolyGamma(0, a+b*x), x, 1, Gamma(a+b*x)^n/(b*n)}
	public void test03432() {
		check("Integrate(Gamma(a+b*x)^n*PolyGamma(0, a+b*x), x)", "Gamma(a+b*x)^n/(b*n)");

	}

	// {(a+b*x)!^n*PolyGamma(0, 1+a+b*x), x, 1, (a+b*x)!^n/(b*n)}
	public void test03433() {
		check("Integrate((a+b*x)!^n*PolyGamma(0, 1+a+b*x), x)", "(a+b*x)!^n/(b*n)");

	}

	// {Zeta(2, a+b*x)/x, x, 1, Unintegrable(PolyGamma(1, a+b*x)/x, x)}
	public void test03434() {
		check("Integrate(Zeta(2, a+b*x)/x, x)", "Unintegrable(PolyGamma(1, a+b*x)/x, x)");

	}

	// {Zeta(s, a+b*x), x, 1, Zeta(-1+s, a+b*x)/(b*(1-s))}
	public void test03435() {
		check("Integrate(Zeta(s, a+b*x), x)", "Zeta(-1+s, a+b*x)/(b*(1-s))");

	}

	// {Zeta(s, a+b*x)/x^2, x, 1, -(b*s*CannotIntegrate(Zeta(1+s, a+b*x)/x, x))-Zeta(s, a+b*x)/x}
	public void test03436() {
		check("Integrate(Zeta(s, a+b*x)/x^2, x)", "-(b*s*CannotIntegrate(Zeta(1+s, a+b*x)/x, x))-Zeta(s, a+b*x)/x");

	}

	// {PolyLog(2, a*x)/x, x, 1, PolyLog(3, a*x)}
	public void test03437() {
		check("Integrate(PolyLog(2, a*x)/x, x)", "PolyLog(3, a*x)");

	}

	// {PolyLog(3, a*x)/x, x, 1, PolyLog(4, a*x)}
	public void test03438() {
		check("Integrate(PolyLog(3, a*x)/x, x)", "PolyLog(4, a*x)");

	}

	// {PolyLog(2, a*x^2)/x, x, 1, PolyLog(3, a*x^2)/2}
	public void test03439() {
		check("Integrate(PolyLog(2, a*x^2)/x, x)", "PolyLog(3, a*x^2)/2");

	}

	// {PolyLog(3, a*x^2)/x, x, 1, PolyLog(4, a*x^2)/2}
	public void test03440() {
		check("Integrate(PolyLog(3, a*x^2)/x, x)", "PolyLog(4, a*x^2)/2");

	}

	// {PolyLog(2, a*x^q)/x, x, 1, PolyLog(3, a*x^q)/q}
	public void test03441() {
		check("Integrate(PolyLog(2, a*x^q)/x, x)", "PolyLog(3, a*x^q)/q");

	}

	// {PolyLog(3, a*x^q)/x, x, 1, PolyLog(4, a*x^q)/q}
	public void test03442() {
		check("Integrate(PolyLog(3, a*x^q)/x, x)", "PolyLog(4, a*x^q)/q");

	}

	// {PolyLog(1/2, a*x), x, 1, x*PolyLog(1/2, a*x)-Unintegrable(PolyLog(-1/2, a*x), x)}
	public void test03443() {
		check("Integrate(PolyLog(1/2, a*x), x)", "x*PolyLog(1/2, a*x)-Unintegrable(PolyLog(-1/2, a*x), x)");

	}

	// {PolyLog(-3/2, a*x), x, 1, x*PolyLog(-1/2, a*x)-Unintegrable(PolyLog(-1/2, a*x), x)}
	public void test03444() {
		check("Integrate(PolyLog(-3/2, a*x), x)", "x*PolyLog(-1/2, a*x)-Unintegrable(PolyLog(-1/2, a*x), x)");

	}

	// {PolyLog(n, a*x)/x, x, 1, PolyLog(1+n, a*x)}
	public void test03445() {
		check("Integrate(PolyLog(n, a*x)/x, x)", "PolyLog(1+n, a*x)");

	}

	// {PolyLog(n, a*x^q)/x, x, 1, PolyLog(1+n, a*x^q)/q}
	public void test03446() {
		check("Integrate(PolyLog(n, a*x^q)/x, x)", "PolyLog(1+n, a*x^q)/q");

	}

	// {PolyLog(3, c*(a+b*x))/x, x, 1, Int(PolyLog(3, a*c+b*c*x)/x, x)}
	public void test03447() {
		check("Integrate(PolyLog(3, c*(a+b*x))/x, x)", "Int(PolyLog(3, a*c+b*c*x)/x, x)");

	}

	// {PolyLog(n, e*((a+b*x)/(c+d*x))^n)/((a+b*x)*(c+d*x)), x, 1, PolyLog(1+n, e*((a+b*x)/(c +
	// d*x))^n)/((b*c-a*d)*n)}
	public void test03448() {
		check("Integrate(PolyLog(n, e*((a+b*x)/(c+d*x))^n)/((a+b*x)*(c+d*x)), x)",
				"PolyLog(1+n, e*((a+b*x)/(c+d*x))^n)/((b*c-a*d)*n)");

	}

	// {PolyLog(3, e*((a+b*x)/(c+d*x))^n)/((a+b*x)*(c+d*x)), x, 1, PolyLog(4, e*((a+b*x)/(c+d*x))^n)/((b*c -
	// a*d)*n)}
	public void test03449() {
		check("Integrate(PolyLog(3, e*((a+b*x)/(c+d*x))^n)/((a+b*x)*(c+d*x)), x)",
				"PolyLog(4, e*((a+b*x)/(c+d*x))^n)/((b*c-a*d)*n)");

	}

	// {PolyLog(2, e*((a+b*x)/(c+d*x))^n)/((a+b*x)*(c+d*x)), x, 1, PolyLog(3, e*((a+b*x)/(c+d*x))^n)/((b*c -
	// a*d)*n)}
	public void test03450() {
		check("Integrate(PolyLog(2, e*((a+b*x)/(c+d*x))^n)/((a+b*x)*(c+d*x)), x)",
				"PolyLog(3, e*((a+b*x)/(c+d*x))^n)/((b*c-a*d)*n)");

	}

	// {-(Log(1-e*((a+b*x)/(c+d*x))^n)/((a+b*x)*(c+d*x))), x, 1, PolyLog(2, e*((a+b*x)/(c+d*x))^n)/((b*c -
	// a*d)*n)}
	public void test03451() {
		check("Integrate(-(Log(1-e*((a+b*x)/(c+d*x))^n)/((a+b*x)*(c+d*x))), x)",
				"PolyLog(2, e*((a+b*x)/(c+d*x))^n)/((b*c-a*d)*n)");

	}

	// {PolyLog(n, d*(F^(c*(a+b*x)))^p)/x, x, 1, CannotIntegrate(PolyLog(n, d*(F^(a*c+b*c*x))^p)/x, x)}
	public void test03452() {
		check("Integrate(PolyLog(n, d*(F^(c*(a+b*x)))^p)/x, x)",
				"CannotIntegrate(PolyLog(n, d*(F^(a*c+b*c*x))^p)/x, x)");

	}

	// {(Log(1-c*x)*PolyLog(2, c*x))/x, x, 1, -PolyLog(2, c*x)^2/2}
	public void test03453() {
		check("Integrate((Log(1-c*x)*PolyLog(2, c*x))/x, x)", "-PolyLog(2, c*x)^2/2");

	}

	// {1/(x*Sqrt(c*ProductLog(a+b*x))), x, 1, (CannotIntegrate(1/(x*Sqrt(ProductLog(a+b*x))), x)*Sqrt(ProductLog(a
	// +b*x)))/Sqrt(c*ProductLog(a+b*x))}
	public void test03454() {
		check("Integrate(1/(x*Sqrt(c*ProductLog(a+b*x))), x)",
				"(CannotIntegrate(1/(x*Sqrt(ProductLog(a+b*x))), x)*Sqrt(ProductLog(a+b*x)))/Sqrt(c*ProductLog(a+b*x))");

	}

	// {1/(x^2*Sqrt(c*ProductLog(a+b*x))), x, 1, (CannotIntegrate(1/(x^2*Sqrt(ProductLog(a+b*x))),
	// x)*Sqrt(ProductLog(a+b*x)))/Sqrt(c*ProductLog(a+b*x))}
	public void test03455() {
		check("Integrate(1/(x^2*Sqrt(c*ProductLog(a+b*x))), x)",
				"(CannotIntegrate(1/(x^2*Sqrt(ProductLog(a+b*x))), x)*Sqrt(ProductLog(a+b*x)))/Sqrt(c*ProductLog(a+b*x))");

	}

	// {1/(x*Sqrt(-(c*ProductLog(a+b*x)))), x, 1, (CannotIntegrate(1/(x*Sqrt(ProductLog(a+b*x))),
	// x)*Sqrt(ProductLog(a+b*x)))/Sqrt(-(c*ProductLog(a+b*x)))}
	public void test03456() {
		check("Integrate(1/(x*Sqrt(-(c*ProductLog(a+b*x)))), x)",
				"(CannotIntegrate(1/(x*Sqrt(ProductLog(a+b*x))), x)*Sqrt(ProductLog(a+b*x)))/Sqrt(-(c*ProductLog(a+b*x)))");

	}

	// {1/(x^2*Sqrt(-(c*ProductLog(a+b*x)))), x, 1, (CannotIntegrate(1/(x^2*Sqrt(ProductLog(a+b*x))),
	// x)*Sqrt(ProductLog(a+b*x)))/Sqrt(-(c*ProductLog(a+b*x)))}
	public void test03457() {
		check("Integrate(1/(x^2*Sqrt(-(c*ProductLog(a+b*x)))), x)",
				"(CannotIntegrate(1/(x^2*Sqrt(ProductLog(a+b*x))), x)*Sqrt(ProductLog(a+b*x)))/Sqrt(-(c*ProductLog(a+b*x)))");

	}

	// {Sqrt(c*ProductLog(a+b*x))/x, x, 1, (CannotIntegrate(Sqrt(ProductLog(a+b*x))/x, x)*Sqrt(c*ProductLog(a +
	// b*x)))/Sqrt(ProductLog(a+b*x))}
	public void test03458() {
		check("Integrate(Sqrt(c*ProductLog(a+b*x))/x, x)",
				"(CannotIntegrate(Sqrt(ProductLog(a+b*x))/x, x)*Sqrt(c*ProductLog(a+b*x)))/Sqrt(ProductLog(a+b*x))");

	}

	// {Sqrt(c*ProductLog(a+b*x))/x^2, x, 1, (CannotIntegrate(Sqrt(ProductLog(a+b*x))/x^2, x)*Sqrt(c*ProductLog(a +
	// b*x)))/Sqrt(ProductLog(a+b*x))}
	public void test03459() {
		check("Integrate(Sqrt(c*ProductLog(a+b*x))/x^2, x)",
				"(CannotIntegrate(Sqrt(ProductLog(a+b*x))/x^2, x)*Sqrt(c*ProductLog(a+b*x)))/Sqrt(ProductLog(a+b*x))");

	}

	// {(d+d*ProductLog(a+b*x))^(-1), x, 1, (a+b*x)/(b*d*ProductLog(a+b*x))}
	public void test03460() {
		check("Integrate((d+d*ProductLog(a+b*x))^(-1), x)", "(a+b*x)/(b*d*ProductLog(a+b*x))");

	}

	// {Sqrt(c*ProductLog(a*x^2))/x^2, x, 1, (CannotIntegrate(Sqrt(ProductLog(a*x^2))/x^2,
	// x)*Sqrt(c*ProductLog(a*x^2)))/Sqrt(ProductLog(a*x^2))}
	public void test03461() {
		check("Integrate(Sqrt(c*ProductLog(a*x^2))/x^2, x)",
				"(CannotIntegrate(Sqrt(ProductLog(a*x^2))/x^2, x)*Sqrt(c*ProductLog(a*x^2)))/Sqrt(ProductLog(a*x^2))");

	}

	// {Sqrt(c*ProductLog(a*x^2))/x^4, x, 1, (CannotIntegrate(Sqrt(ProductLog(a*x^2))/x^4,
	// x)*Sqrt(c*ProductLog(a*x^2)))/Sqrt(ProductLog(a*x^2))}
	public void test03462() {
		check("Integrate(Sqrt(c*ProductLog(a*x^2))/x^4, x)",
				"(CannotIntegrate(Sqrt(ProductLog(a*x^2))/x^4, x)*Sqrt(c*ProductLog(a*x^2)))/Sqrt(ProductLog(a*x^2))");

	}

	// {Sqrt(c*ProductLog(a*x^2))/x^6, x, 1, (CannotIntegrate(Sqrt(ProductLog(a*x^2))/x^6,
	// x)*Sqrt(c*ProductLog(a*x^2)))/Sqrt(ProductLog(a*x^2))}
	public void test03463() {
		check("Integrate(Sqrt(c*ProductLog(a*x^2))/x^6, x)",
				"(CannotIntegrate(Sqrt(ProductLog(a*x^2))/x^6, x)*Sqrt(c*ProductLog(a*x^2)))/Sqrt(ProductLog(a*x^2))");

	}

	// {1/Sqrt(c*ProductLog(a*x^2)), x, 1, (CannotIntegrate(1/Sqrt(ProductLog(a*x^2)),
	// x)*Sqrt(ProductLog(a*x^2)))/Sqrt(c*ProductLog(a*x^2))}
	public void test03464() {
		check("Integrate(1/Sqrt(c*ProductLog(a*x^2)), x)",
				"(CannotIntegrate(1/Sqrt(ProductLog(a*x^2)), x)*Sqrt(ProductLog(a*x^2)))/Sqrt(c*ProductLog(a*x^2))");

	}

	// {1/(x^2*Sqrt(c*ProductLog(a*x^2))), x, 1, (CannotIntegrate(1/(x^2*Sqrt(ProductLog(a*x^2))),
	// x)*Sqrt(ProductLog(a*x^2)))/Sqrt(c*ProductLog(a*x^2))}
	public void test03465() {
		check("Integrate(1/(x^2*Sqrt(c*ProductLog(a*x^2))), x)",
				"(CannotIntegrate(1/(x^2*Sqrt(ProductLog(a*x^2))), x)*Sqrt(ProductLog(a*x^2)))/Sqrt(c*ProductLog(a*x^2))");

	}

	// {1/(x^4*Sqrt(c*ProductLog(a*x^2))), x, 1, (CannotIntegrate(1/(x^4*Sqrt(ProductLog(a*x^2))),
	// x)*Sqrt(ProductLog(a*x^2)))/Sqrt(c*ProductLog(a*x^2))}
	public void test03466() {
		check("Integrate(1/(x^4*Sqrt(c*ProductLog(a*x^2))), x)",
				"(CannotIntegrate(1/(x^4*Sqrt(ProductLog(a*x^2))), x)*Sqrt(ProductLog(a*x^2)))/Sqrt(c*ProductLog(a*x^2))");

	}

	// {1/(x^6*Sqrt(c*ProductLog(a*x^2))), x, 1, (CannotIntegrate(1/(x^6*Sqrt(ProductLog(a*x^2))),
	// x)*Sqrt(ProductLog(a*x^2)))/Sqrt(c*ProductLog(a*x^2))}
	public void test03467() {
		check("Integrate(1/(x^6*Sqrt(c*ProductLog(a*x^2))), x)",
				"(CannotIntegrate(1/(x^6*Sqrt(ProductLog(a*x^2))), x)*Sqrt(ProductLog(a*x^2)))/Sqrt(c*ProductLog(a*x^2))");

	}

	// {x^2*(c*ProductLog(a*x^2))^p, x, 1, (CannotIntegrate(x^2*ProductLog(a*x^2)^p,
	// x)*(c*ProductLog(a*x^2))^p)/ProductLog(a*x^2)^p}
	public void test03468() {
		check("Integrate(x^2*(c*ProductLog(a*x^2))^p, x)",
				"(CannotIntegrate(x^2*ProductLog(a*x^2)^p, x)*(c*ProductLog(a*x^2))^p)/ProductLog(a*x^2)^p");

	}

	// {(c*ProductLog(a*x^2))^p/x^2, x, 1, (CannotIntegrate(ProductLog(a*x^2)^p/x^2,
	// x)*(c*ProductLog(a*x^2))^p)/ProductLog(a*x^2)^p}
	public void test03469() {
		check("Integrate((c*ProductLog(a*x^2))^p/x^2, x)",
				"(CannotIntegrate(ProductLog(a*x^2)^p/x^2, x)*(c*ProductLog(a*x^2))^p)/ProductLog(a*x^2)^p");

	}

	// {(1+ProductLog(a*x))^(-1), x, 1, x/ProductLog(a*x)}
	public void test03470() {
		check("Integrate((1+ProductLog(a*x))^(-1), x)", "x/ProductLog(a*x)");

	}

	// {1/(x*(1+ProductLog(a*x))), x, 1, Log(ProductLog(a*x))}
	public void test03471() {
		check("Integrate(1/(x*(1+ProductLog(a*x))), x)", "Log(ProductLog(a*x))");

	}

	// {1/(x*(1+ProductLog(a*x^2))), x, 1, Log(ProductLog(a*x^2))/2}
	public void test03472() {
		check("Integrate(1/(x*(1+ProductLog(a*x^2))), x)", "Log(ProductLog(a*x^2))/2");

	}

	// {1/(x*(1+ProductLog(a/x))), x, 1, -Log(ProductLog(a/x))}
	public void test03473() {
		check("Integrate(1/(x*(1+ProductLog(a/x))), x)", "-Log(ProductLog(a/x))");

	}

	// {1/(x*(1+ProductLog(a/x^2))), x, 1, -Log(ProductLog(a/x^2))/2}
	public void test03474() {
		check("Integrate(1/(x*(1+ProductLog(a/x^2))), x)", "-Log(ProductLog(a/x^2))/2");

	}

	// {x^4/(1+ProductLog(a/x^2)), x, 1, CannotIntegrate(x^4/(1+ProductLog(a/x^2)), x)}
	public void test03475() {
		check("Integrate(x^4/(1+ProductLog(a/x^2)), x)", "CannotIntegrate(x^4/(1+ProductLog(a/x^2)), x)");

	}

	// {x^2/(1+ProductLog(a/x^2)), x, 1, CannotIntegrate(x^2/(1+ProductLog(a/x^2)), x)}
	public void test03476() {
		check("Integrate(x^2/(1+ProductLog(a/x^2)), x)", "CannotIntegrate(x^2/(1+ProductLog(a/x^2)), x)");

	}

	// {(1+ProductLog(a/x^2))^(-1), x, 1, CannotIntegrate((1+ProductLog(a/x^2))^(-1), x)}
	public void test03477() {
		check("Integrate((1+ProductLog(a/x^2))^(-1), x)", "CannotIntegrate((1+ProductLog(a/x^2))^(-1), x)");

	}

	// {1/(x^2*(1+ProductLog(a/x^2))), x, 1, CannotIntegrate(1/(x^2*(1+ProductLog(a/x^2))), x)}
	public void test03478() {
		check("Integrate(1/(x^2*(1+ProductLog(a/x^2))), x)", "CannotIntegrate(1/(x^2*(1+ProductLog(a/x^2))), x)");

	}

	// {1/(x^4*(1+ProductLog(a/x^2))), x, 1, CannotIntegrate(1/(x^4*(1+ProductLog(a/x^2))), x)}
	public void test03479() {
		check("Integrate(1/(x^4*(1+ProductLog(a/x^2))), x)", "CannotIntegrate(1/(x^4*(1+ProductLog(a/x^2))), x)");

	}

	// {x^m/(d+d*ProductLog(a*x)), x, 1, (x^m*Gamma(1+m, -((1+m)*ProductLog(a*x))))/(a*d*E^(m*ProductLog(a*x))*(1
	// +m)*(-((1+m)*ProductLog(a*x)))^m)}
	public void test03480() {
		check("Integrate(x^m/(d+d*ProductLog(a*x)), x)",
				"(x^m*Gamma(1+m, -((1+m)*ProductLog(a*x))))/(a*d*E^(m*ProductLog(a*x))*(1+m)*(-((1+m)*ProductLog(a*x)))^m)");

	}

	// {ProductLog(a/x^(1/4))^5/(1+ProductLog(a/x^(1/4))), x, 1, x*ProductLog(a/x^(1/4))^4}
	public void test03481() {
		check("Integrate(ProductLog(a/x^(1/4))^5/(1+ProductLog(a/x^(1/4))), x)", "x*ProductLog(a/x^(1/4))^4");

	}

	// {ProductLog(a/x^(1/3))^4/(1+ProductLog(a/x^(1/3))), x, 1, x*ProductLog(a/x^(1/3))^3}
	public void test03482() {
		check("Integrate(ProductLog(a/x^(1/3))^4/(1+ProductLog(a/x^(1/3))), x)", "x*ProductLog(a/x^(1/3))^3");

	}

	// {ProductLog(a/Sqrt(x))^3/(1+ProductLog(a/Sqrt(x))), x, 1, x*ProductLog(a/Sqrt(x))^2}
	public void test03483() {
		check("Integrate(ProductLog(a/Sqrt(x))^3/(1+ProductLog(a/Sqrt(x))), x)", "x*ProductLog(a/Sqrt(x))^2");

	}

	// {ProductLog(a/x)^2/(1+ProductLog(a/x)), x, 1, x*ProductLog(a/x)}
	public void test03484() {
		check("Integrate(ProductLog(a/x)^2/(1+ProductLog(a/x)), x)", "x*ProductLog(a/x)");

	}

	// {1/(ProductLog(a*Sqrt(x))*(1+ProductLog(a*Sqrt(x)))), x, 1, x/ProductLog(a*Sqrt(x))^2}
	public void test03485() {
		check("Integrate(1/(ProductLog(a*Sqrt(x))*(1+ProductLog(a*Sqrt(x)))), x)", "x/ProductLog(a*Sqrt(x))^2");

	}

	// {1/(ProductLog(a*x^(1/3))^2*(1+ProductLog(a*x^(1/3)))), x, 1, x/ProductLog(a*x^(1/3))^3}
	public void test03486() {
		check("Integrate(1/(ProductLog(a*x^(1/3))^2*(1+ProductLog(a*x^(1/3)))), x)", "x/ProductLog(a*x^(1/3))^3");

	}

	// {1/(ProductLog(a*x^(1/4))^3*(1+ProductLog(a*x^(1/4)))), x, 1, x/ProductLog(a*x^(1/4))^4}
	public void test03487() {
		check("Integrate(1/(ProductLog(a*x^(1/4))^3*(1+ProductLog(a*x^(1/4)))), x)", "x/ProductLog(a*x^(1/4))^4");

	}

	// {ProductLog(a/x^(1/4))^4/(1+ProductLog(a/x^(1/4))), x, 1, -4*a^4*ExpIntegralEi(-4*ProductLog(a/x^(1/4)))}
	public void test03488() {
		check("Integrate(ProductLog(a/x^(1/4))^4/(1+ProductLog(a/x^(1/4))), x)",
				"-4*a^4*ExpIntegralEi(-4*ProductLog(a/x^(1/4)))");

	}

	// {ProductLog(a/x^(1/3))^3/(1+ProductLog(a/x^(1/3))), x, 1, -3*a^3*ExpIntegralEi(-3*ProductLog(a/x^(1/3)))}
	public void test03489() {
		check("Integrate(ProductLog(a/x^(1/3))^3/(1+ProductLog(a/x^(1/3))), x)",
				"-3*a^3*ExpIntegralEi(-3*ProductLog(a/x^(1/3)))");

	}

	// {ProductLog(a/Sqrt(x))^2/(1+ProductLog(a/Sqrt(x))), x, 1, -2*a^2*ExpIntegralEi(-2*ProductLog(a/Sqrt(x)))}
	public void test03490() {
		check("Integrate(ProductLog(a/Sqrt(x))^2/(1+ProductLog(a/Sqrt(x))), x)",
				"-2*a^2*ExpIntegralEi(-2*ProductLog(a/Sqrt(x)))");

	}

	// {ProductLog(a/x)/(1+ProductLog(a/x)), x, 1, -(a*ExpIntegralEi(-ProductLog(a/x)))}
	public void test03491() {
		check("Integrate(ProductLog(a/x)/(1+ProductLog(a/x)), x)", "-(a*ExpIntegralEi(-ProductLog(a/x)))");

	}

	// {1/(ProductLog(a*x)*(1+ProductLog(a*x))), x, 1, ExpIntegralEi(ProductLog(a*x))/a}
	public void test03492() {
		check("Integrate(1/(ProductLog(a*x)*(1+ProductLog(a*x))), x)", "ExpIntegralEi(ProductLog(a*x))/a");

	}

	// {1/(ProductLog(a*Sqrt(x))^2*(1+ProductLog(a*Sqrt(x)))), x, 1, (2*ExpIntegralEi(2*ProductLog(a*Sqrt(x))))/a^2}
	public void test03493() {
		check("Integrate(1/(ProductLog(a*Sqrt(x))^2*(1+ProductLog(a*Sqrt(x)))), x)",
				"(2*ExpIntegralEi(2*ProductLog(a*Sqrt(x))))/a^2");

	}

	// {1/(ProductLog(a*x^(1/3))^3*(1+ProductLog(a*x^(1/3)))), x, 1, (3*ExpIntegralEi(3*ProductLog(a*x^(1/3))))/a^3}
	public void test03494() {
		check("Integrate(1/(ProductLog(a*x^(1/3))^3*(1+ProductLog(a*x^(1/3)))), x)",
				"(3*ExpIntegralEi(3*ProductLog(a*x^(1/3))))/a^3");

	}

	// {1/(ProductLog(a*x^(1/4))^4*(1+ProductLog(a*x^(1/4)))), x, 1, (4*ExpIntegralEi(4*ProductLog(a*x^(1/4))))/a^4}
	public void test03495() {
		check("Integrate(1/(ProductLog(a*x^(1/4))^4*(1+ProductLog(a*x^(1/4)))), x)",
				"(4*ExpIntegralEi(4*ProductLog(a*x^(1/4))))/a^4");

	}

	// {ProductLog(a*x^n)^(1-n^(-1))/(1+ProductLog(a*x^n)), x, 1, x/ProductLog(a*x^n)^n^(-1)}
	public void test03496() {
		check("Integrate(ProductLog(a*x^n)^(1-n^(-1))/(1+ProductLog(a*x^n)), x)", "x/ProductLog(a*x^n)^n^(-1)");

	}

	// {ProductLog(a*x^(1-p)^(-1))^p/(1+ProductLog(a*x^(1-p)^(-1))), x, 1, x*ProductLog(a*x^(1-p)^(-1))^(-1 +
	// p)}
	public void test03497() {
		check("Integrate(ProductLog(a*x^(1-p)^(-1))^p/(1+ProductLog(a*x^(1-p)^(-1))), x)",
				"x*ProductLog(a*x^(1-p)^(-1))^(-1+p)");

	}

}
