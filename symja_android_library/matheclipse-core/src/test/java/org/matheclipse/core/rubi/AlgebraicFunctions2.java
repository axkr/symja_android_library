package org.matheclipse.core.rubi;

/**
 * JUnit tests which fail.
 *
 */
public class AlgebraicFunctions2 extends AbstractRubiTestCase {

	public AlgebraicFunctions2(String name) {
		super(name, false);
	}

	// {1877}
	public void test0870() {
		check(//
				"Integrate[(1 - Sqrt[3] + x)/Sqrt[1 + x^3], x]", //
				"(2*Sqrt[1 + x^3])/(1 + Sqrt[3] + x) - (3^(1/4)*Sqrt[2 - Sqrt[3]]*(1 + x)*Sqrt[(1 - x + x^2)/(1 + Sqrt[3] + x)^2]*EllipticE[ArcSin[(1 - Sqrt[3] + x)/(1 + Sqrt[3] + x)], -7 - 4*Sqrt[3]])/(Sqrt[(1 + x)/(1 + Sqrt[3] + x)^2]*Sqrt[1 + x^3])", //
				1877);
	}

	// {1877}
	public void test0871() {
		check(//
				"Integrate[(1 - Sqrt[3] - x)/Sqrt[1 - x^3], x]", //
				"(-2*Sqrt[1 - x^3])/(1 + Sqrt[3] - x) + (3^(1/4)*Sqrt[2 - Sqrt[3]]*(1 - x)*Sqrt[(1 + x + x^2)/(1 + Sqrt[3] - x)^2]*EllipticE[ArcSin[(1 - Sqrt[3] - x)/(1 + Sqrt[3] - x)], -7 - 4*Sqrt[3]])/(Sqrt[(1 - x)/(1 + Sqrt[3] - x)^2]*Sqrt[1 - x^3])", //
				1877);
	}

	// {1877}
	public void test0872() {
		check(//
				"Integrate[(-1 + Sqrt[3] - x)/Sqrt[1 + x^3], x]", //
				"(-2*Sqrt[1 + x^3])/(1 + Sqrt[3] + x) + (3^(1/4)*Sqrt[2 - Sqrt[3]]*(1 + x)*Sqrt[(1 - x + x^2)/(1 + Sqrt[3] + x)^2]*EllipticE[ArcSin[(1 - Sqrt[3] + x)/(1 + Sqrt[3] + x)], -7 - 4*Sqrt[3]])/(Sqrt[(1 + x)/(1 + Sqrt[3] + x)^2]*Sqrt[1 + x^3])", //
				1877);
	}

	// {1877}
	public void test0873() {
		check(//
				"Integrate[(-1 + Sqrt[3] + x)/Sqrt[1 - x^3], x]", //
				"(2*Sqrt[1 - x^3])/(1 + Sqrt[3] - x) - (3^(1/4)*Sqrt[2 - Sqrt[3]]*(1 - x)*Sqrt[(1 + x + x^2)/(1 + Sqrt[3] - x)^2]*EllipticE[ArcSin[(1 - Sqrt[3] - x)/(1 + Sqrt[3] - x)], -7 - 4*Sqrt[3]])/(Sqrt[(1 - x)/(1 + Sqrt[3] - x)^2]*Sqrt[1 - x^3])", //
				1877);
	}

	// {1877}
	public void test0874() {
		check(//
				"Integrate[((1 - Sqrt[3])*a^(1/3) + b^(1/3)*x)/Sqrt[a + b*x^3], x]", //
				"(2*Sqrt[a + b*x^3])/(b^(1/3)*((1 + Sqrt[3])*a^(1/3) + b^(1/3)*x)) - (3^(1/4)*Sqrt[2 - Sqrt[3]]*a^(1/3)*(a^(1/3) + b^(1/3)*x)*Sqrt[(a^(2/3) - a^(1/3)*b^(1/3)*x + b^(2/3)*x^2)/((1 + Sqrt[3])*a^(1/3) + b^(1/3)*x)^2]*EllipticE[ArcSin[((1 - Sqrt[3])*a^(1/3) + b^(1/3)*x)/((1 + Sqrt[3])*a^(1/3) + b^(1/3)*x)], -7 - 4*Sqrt[3]])/(b^(1/3)*Sqrt[(a^(1/3)*(a^(1/3) + b^(1/3)*x))/((1 + Sqrt[3])*a^(1/3) + b^(1/3)*x)^2]*Sqrt[a + b*x^3])", //
				1877);
	}

	// {1877}
	public void test0875() {
		check(//
				"Integrate[((1 - Sqrt[3])*a^(1/3) - b^(1/3)*x)/Sqrt[a - b*x^3], x]", //
				"(-2*Sqrt[a - b*x^3])/(b^(1/3)*((1 + Sqrt[3])*a^(1/3) - b^(1/3)*x)) + (3^(1/4)*Sqrt[2 - Sqrt[3]]*a^(1/3)*(a^(1/3) - b^(1/3)*x)*Sqrt[(a^(2/3) + a^(1/3)*b^(1/3)*x + b^(2/3)*x^2)/((1 + Sqrt[3])*a^(1/3) - b^(1/3)*x)^2]*EllipticE[ArcSin[((1 - Sqrt[3])*a^(1/3) - b^(1/3)*x)/((1 + Sqrt[3])*a^(1/3) - b^(1/3)*x)], -7 - 4*Sqrt[3]])/(b^(1/3)*Sqrt[(a^(1/3)*(a^(1/3) - b^(1/3)*x))/((1 + Sqrt[3])*a^(1/3) - b^(1/3)*x)^2]*Sqrt[a - b*x^3])", //
				1877);
	}

	// {1877}
	public void test0876() {
		check(//
				"Integrate[(1 - Sqrt[3] + (b/a)^(1/3)*x)/Sqrt[a + b*x^3], x]", //
				"(2*(b/a)^(2/3)*Sqrt[a + b*x^3])/(b*(1 + Sqrt[3] + (b/a)^(1/3)*x)) - (3^(1/4)*Sqrt[2 - Sqrt[3]]*(1 + (b/a)^(1/3)*x)*Sqrt[(1 - (b/a)^(1/3)*x + (b/a)^(2/3)*x^2)/(1 + Sqrt[3] + (b/a)^(1/3)*x)^2]*EllipticE[ArcSin[(1 - Sqrt[3] + (b/a)^(1/3)*x)/(1 + Sqrt[3] + (b/a)^(1/3)*x)], -7 - 4*Sqrt[3]])/((b/a)^(1/3)*Sqrt[(1 + (b/a)^(1/3)*x)/(1 + Sqrt[3] + (b/a)^(1/3)*x)^2]*Sqrt[a + b*x^3])", //
				1877);
	}

	// {1877}
	public void test0877() {
		check(//
				"Integrate[(1 - Sqrt[3] - (b/a)^(1/3)*x)/Sqrt[a - b*x^3], x]", //
				"(-2*(b/a)^(2/3)*Sqrt[a - b*x^3])/(b*(1 + Sqrt[3] - (b/a)^(1/3)*x)) + (3^(1/4)*Sqrt[2 - Sqrt[3]]*(1 - (b/a)^(1/3)*x)*Sqrt[(1 + (b/a)^(1/3)*x + (b/a)^(2/3)*x^2)/(1 + Sqrt[3] - (b/a)^(1/3)*x)^2]*EllipticE[ArcSin[(1 - Sqrt[3] - (b/a)^(1/3)*x)/(1 + Sqrt[3] - (b/a)^(1/3)*x)], -7 - 4*Sqrt[3]])/((b/a)^(1/3)*Sqrt[(1 - (b/a)^(1/3)*x)/(1 + Sqrt[3] - (b/a)^(1/3)*x)^2]*Sqrt[a - b*x^3])", //
				1877);
	}

	// {1103}
	public void test1160() {
		check(//
				"Integrate[1/Sqrt[2 + 4*x^2 + 3*x^4], x]", //
				"((2 + Sqrt[6]*x^2)*Sqrt[(2 + 4*x^2 + 3*x^4)/(2 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(3/2)^(1/4)*x], 1/2 - 1/Sqrt[6]])/(2*6^(1/4)*Sqrt[2 + 4*x^2 + 3*x^4])", //
				1103);
	}

	public void test1161() {
		check(//
				"Integrate[1/Sqrt[2 + 3*x^2 + 3*x^4], x]", //
				"((2 + Sqrt[6]*x^2)*Sqrt[(2 + 3*x^2 + 3*x^4)/(2 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(3/2)^(1/4)*x], (4 - Sqrt[6])/8])/(2*6^(1/4)*Sqrt[2 + 3*x^2 + 3*x^4])", //
				1103);
	}

	// {1103}
	public void test1162() {
		check(//
				"Integrate[1/Sqrt[2 + 2*x^2 + 3*x^4], x]", //
				"((2 + Sqrt[6]*x^2)*Sqrt[(2 + 2*x^2 + 3*x^4)/(2 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(3/2)^(1/4)*x], (6 - Sqrt[6])/12])/(2*6^(1/4)*Sqrt[2 + 2*x^2 + 3*x^4])", //
				1103);
	}

	// {1103}
	public void test1163() {
		check(//
				"Integrate[1/Sqrt[2 + x^2 + 3*x^4], x]", //
				"((2 + Sqrt[6]*x^2)*Sqrt[(2 + x^2 + 3*x^4)/(2 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(3/2)^(1/4)*x], (12 - Sqrt[6])/24])/(2*6^(1/4)*Sqrt[2 + x^2 + 3*x^4])", //
				1103);
	}

	// {1103}
	public void test1165() {
		check(//
				"Integrate[1/Sqrt[2 - x^2 + 3*x^4], x]", //
				"((2 + Sqrt[6]*x^2)*Sqrt[(2 - x^2 + 3*x^4)/(2 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(3/2)^(1/4)*x], (12 + Sqrt[6])/24])/(2*6^(1/4)*Sqrt[2 - x^2 + 3*x^4])", //
				1103);
	}

	// {1103}
	public void test1166() {
		check(//
				"Integrate[1/Sqrt[2 - 2*x^2 + 3*x^4], x]", //
				"((2 + Sqrt[6]*x^2)*Sqrt[(2 - 2*x^2 + 3*x^4)/(2 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(3/2)^(1/4)*x], (6 + Sqrt[6])/12])/(2*6^(1/4)*Sqrt[2 - 2*x^2 + 3*x^4])", //
				1103);
	}

	// {1103}
	public void test1167() {
		check(//
				"Integrate[1/Sqrt[2 - 3*x^2 + 3*x^4], x]", //
				"((2 + Sqrt[6]*x^2)*Sqrt[(2 - 3*x^2 + 3*x^4)/(2 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(3/2)^(1/4)*x], (4 + Sqrt[6])/8])/(2*6^(1/4)*Sqrt[2 - 3*x^2 + 3*x^4])", //
				1103);
	}

	// {1103}
	public void test1168() {
		check(//
				"Integrate[1/Sqrt[2 - 4*x^2 + 3*x^4], x]", //
				"((2 + Sqrt[6]*x^2)*Sqrt[(2 - 4*x^2 + 3*x^4)/(2 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(3/2)^(1/4)*x], 1/2 + 1/Sqrt[6]])/(2*6^(1/4)*Sqrt[2 - 4*x^2 + 3*x^4])", //
				1103);
	}


	// {1096}
	public void test1169() {
		check(//
				"Integrate[1/Sqrt[2 - 5*x^2 + 3*x^4], x]", //
				"((2 + Sqrt[6]*x^2)*Sqrt[(2 - 5*x^2 + 3*x^4)/(2 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(3/2)^(1/4)*x], (12 + 5*Sqrt[6])/24])/(2*6^(1/4)*Sqrt[2 - 5*x^2 + 3*x^4])", //
				1096);
	}

	// {1096}
	public void test1170() {
		check(//
				"Integrate[1/Sqrt[2 - 6*x^2 + 3*x^4], x]", //
				"((2 + Sqrt[6]*x^2)*Sqrt[(2 - 6*x^2 + 3*x^4)/(2 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(3/2)^(1/4)*x], (2 + Sqrt[6])/4])/(2*6^(1/4)*Sqrt[2 - 6*x^2 + 3*x^4])", //
				1096);
	}
	
	// {1103}
	public void test1176() {
		check(//
				"Integrate[1/Sqrt[3 + 4*x^2 + 2*x^4], x]", //
				"((3 + Sqrt[6]*x^2)*Sqrt[(3 + 4*x^2 + 2*x^4)/(3 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(2/3)^(1/4)*x], 1/2 - 1/Sqrt[6]])/(2*6^(1/4)*Sqrt[3 + 4*x^2 + 2*x^4])", //
				1103);
	}

	// {1103}
	public void test1177() {
		check(//
				"Integrate[1/Sqrt[3 + 3*x^2 + 2*x^4], x]", //
				"((3 + Sqrt[6]*x^2)*Sqrt[(3 + 3*x^2 + 2*x^4)/(3 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(2/3)^(1/4)*x], (4 - Sqrt[6])/8])/(2*6^(1/4)*Sqrt[3 + 3*x^2 + 2*x^4])", //
				1103);
	}

	// {1103}
	public void test1178() {
		check(//
				"Integrate[1/Sqrt[3 + 2*x^2 + 2*x^4], x]", //
				"((3 + Sqrt[6]*x^2)*Sqrt[(3 + 2*x^2 + 2*x^4)/(3 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(2/3)^(1/4)*x], (6 - Sqrt[6])/12])/(2*6^(1/4)*Sqrt[3 + 2*x^2 + 2*x^4])", //
				1103);
	}

	// {1103}
	public void test1179() {
		check(//
				"Integrate[1/Sqrt[3 + x^2 + 2*x^4], x]", //
				"((3 + Sqrt[6]*x^2)*Sqrt[(3 + x^2 + 2*x^4)/(3 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(2/3)^(1/4)*x], (12 - Sqrt[6])/24])/(2*6^(1/4)*Sqrt[3 + x^2 + 2*x^4])", //
				1103);
	}

	// {1103}
	public void test1181() {
		check(//
				"Integrate[1/Sqrt[3 - x^2 + 2*x^4], x]", //
				"((3 + Sqrt[6]*x^2)*Sqrt[(3 - x^2 + 2*x^4)/(3 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(2/3)^(1/4)*x], (12 + Sqrt[6])/24])/(2*6^(1/4)*Sqrt[3 - x^2 + 2*x^4])", //
				1103);
	}

	// {1103}
	public void test1182() {
		check(//
				"Integrate[1/Sqrt[3 - 2*x^2 + 2*x^4], x]", //
				"((3 + Sqrt[6]*x^2)*Sqrt[(3 - 2*x^2 + 2*x^4)/(3 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(2/3)^(1/4)*x], (6 + Sqrt[6])/12])/(2*6^(1/4)*Sqrt[3 - 2*x^2 + 2*x^4])", //
				1103);
	}

	// {1103}
	public void test1183() {
		check(//
				"Integrate[1/Sqrt[3 - 3*x^2 + 2*x^4], x]", //
				"((3 + Sqrt[6]*x^2)*Sqrt[(3 - 3*x^2 + 2*x^4)/(3 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(2/3)^(1/4)*x], (4 + Sqrt[6])/8])/(2*6^(1/4)*Sqrt[3 - 3*x^2 + 2*x^4])", //
				1103);
	}

	// {1103}
	public void test1184() {
		check(//
				"Integrate[1/Sqrt[3 - 4*x^2 + 2*x^4], x]", //
				"((3 + Sqrt[6]*x^2)*Sqrt[(3 - 4*x^2 + 2*x^4)/(3 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(2/3)^(1/4)*x], 1/2 + 1/Sqrt[6]])/(2*6^(1/4)*Sqrt[3 - 4*x^2 + 2*x^4])", //
				1103);
	}
	

	// {1096}
	public void test1185() {
		check(//
				"Integrate[1/Sqrt[3 - 5*x^2 + 2*x^4], x]", //
				"((3 + Sqrt[6]*x^2)*Sqrt[(3 - 5*x^2 + 2*x^4)/(3 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(2/3)^(1/4)*x], (12 + 5*Sqrt[6])/24])/(2*6^(1/4)*Sqrt[3 - 5*x^2 + 2*x^4])", //
				1096);
	}

	// {1096}
	public void test1186() {
		check(//
				"Integrate[1/Sqrt[3 - 6*x^2 + 2*x^4], x]", //
				"((3 + Sqrt[6]*x^2)*Sqrt[(3 - 6*x^2 + 2*x^4)/(3 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(2/3)^(1/4)*x], (2 + Sqrt[6])/4])/(2*6^(1/4)*Sqrt[3 - 6*x^2 + 2*x^4])", //
				1096);
	}

	// {1096}
	public void test1187() {
		check(//
				"Integrate[1/Sqrt[3 - 7*x^2 + 2*x^4], x]", //
				"((3 + Sqrt[6]*x^2)*Sqrt[(3 - 7*x^2 + 2*x^4)/(3 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(2/3)^(1/4)*x], (12 + 7*Sqrt[6])/24])/(2*6^(1/4)*Sqrt[3 - 7*x^2 + 2*x^4])", //
				1096);
	}

	// {1103}
	public void test1188() {
		check(//
				"Integrate[1/Sqrt[-3 + 4*x^2 - 2*x^4], x]", //
				"((3 + Sqrt[6]*x^2)*Sqrt[(3 - 4*x^2 + 2*x^4)/(3 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(2/3)^(1/4)*x], 1/2 + 1/Sqrt[6]])/(2*6^(1/4)*Sqrt[-3 + 4*x^2 - 2*x^4])", //
				1103);
	}

	// {1103}
	public void test1189() {
		check(//
				"Integrate[1/Sqrt[-3 + 3*x^2 - 2*x^4], x]", //
				"((3 + Sqrt[6]*x^2)*Sqrt[(3 - 3*x^2 + 2*x^4)/(3 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(2/3)^(1/4)*x], (4 + Sqrt[6])/8])/(2*6^(1/4)*Sqrt[-3 + 3*x^2 - 2*x^4])", //
				1103);
	}

	// {1103}
	public void test1190() {
		check(//
				"Integrate[1/Sqrt[-3 + 2*x^2 - 2*x^4], x]", //
				"((3 + Sqrt[6]*x^2)*Sqrt[(3 - 2*x^2 + 2*x^4)/(3 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(2/3)^(1/4)*x], (6 + Sqrt[6])/12])/(2*6^(1/4)*Sqrt[-3 + 2*x^2 - 2*x^4])", //
				1103);
	}

	// {1103}
	public void test1191() {
		check(//
				"Integrate[1/Sqrt[-3 + x^2 - 2*x^4], x]", //
				"((3 + Sqrt[6]*x^2)*Sqrt[(3 - x^2 + 2*x^4)/(3 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(2/3)^(1/4)*x], (12 + Sqrt[6])/24])/(2*6^(1/4)*Sqrt[-3 + x^2 - 2*x^4])", //
				1103);
	}

	// {220}
	public void test1192() {
		check(//
				"Integrate[1/Sqrt[-3 - 2*x^4], x]", //
				"((3 + Sqrt[6]*x^2)*Sqrt[(3 + 2*x^4)/(3 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(2/3)^(1/4)*x], 1/2])/(2*6^(1/4)*Sqrt[-3 - 2*x^4])", //
				220);
	}

	// {1103}
	public void test1193() {
		check(//
				"Integrate[1/Sqrt[-3 - x^2 - 2*x^4], x]", //
				"((3 + Sqrt[6]*x^2)*Sqrt[(3 + x^2 + 2*x^4)/(3 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(2/3)^(1/4)*x], (12 - Sqrt[6])/24])/(2*6^(1/4)*Sqrt[-3 - x^2 - 2*x^4])", //
				1103);
	}

	// {1103}
	public void test1194() {
		check(//
				"Integrate[1/Sqrt[-3 - 2*x^2 - 2*x^4], x]", //
				"((3 + Sqrt[6]*x^2)*Sqrt[(3 + 2*x^2 + 2*x^4)/(3 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(2/3)^(1/4)*x], (6 - Sqrt[6])/12])/(2*6^(1/4)*Sqrt[-3 - 2*x^2 - 2*x^4])", //
				1103);
	}

	// {1103}
	public void test1195() {
		check(//
				"Integrate[1/Sqrt[-3 - 3*x^2 - 2*x^4], x]", //
				"((3 + Sqrt[6]*x^2)*Sqrt[(3 + 3*x^2 + 2*x^4)/(3 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(2/3)^(1/4)*x], (4 - Sqrt[6])/8])/(2*6^(1/4)*Sqrt[-3 - 3*x^2 - 2*x^4])", //
				1103);
	}

	// {1103}
	public void test1196() {
		check(//
				"Integrate[1/Sqrt[-3 - 4*x^2 - 2*x^4], x]", //
				"((3 + Sqrt[6]*x^2)*Sqrt[(3 + 4*x^2 + 2*x^4)/(3 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(2/3)^(1/4)*x], 1/2 - 1/Sqrt[6]])/(2*6^(1/4)*Sqrt[-3 - 4*x^2 - 2*x^4])", //
				1103);
	}

	// {1103}
	public void test1197() {
		check(//
				"Integrate[1/Sqrt[-2 + 4*x^2 - 3*x^4], x]", //
				"((2 + Sqrt[6]*x^2)*Sqrt[(2 - 4*x^2 + 3*x^4)/(2 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(3/2)^(1/4)*x], 1/2 + 1/Sqrt[6]])/(2*6^(1/4)*Sqrt[-2 + 4*x^2 - 3*x^4])", //
				1103);
	}

	// {1103}
	public void test1198() {
		check(//
				"Integrate[1/Sqrt[-2 + 3*x^2 - 3*x^4], x]", //
				"((2 + Sqrt[6]*x^2)*Sqrt[(2 - 3*x^2 + 3*x^4)/(2 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(3/2)^(1/4)*x], (4 + Sqrt[6])/8])/(2*6^(1/4)*Sqrt[-2 + 3*x^2 - 3*x^4])", //
				1103);
	}

	// {1103}
	public void test1199() {
		check(//
				"Integrate[1/Sqrt[-2 + 2*x^2 - 3*x^4], x]", //
				"((2 + Sqrt[6]*x^2)*Sqrt[(2 - 2*x^2 + 3*x^4)/(2 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(3/2)^(1/4)*x], (6 + Sqrt[6])/12])/(2*6^(1/4)*Sqrt[-2 + 2*x^2 - 3*x^4])", //
				1103);
	}

	// {1103}
	public void test1200() {
		check(//
				"Integrate[1/Sqrt[-2 + x^2 - 3*x^4], x]", //
				"((2 + Sqrt[6]*x^2)*Sqrt[(2 - x^2 + 3*x^4)/(2 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(3/2)^(1/4)*x], (12 + Sqrt[6])/24])/(2*6^(1/4)*Sqrt[-2 + x^2 - 3*x^4])", //
				1103);
	}

	// {220}
	public void test1201() {
		check(//
				"Integrate[1/Sqrt[-2 - 3*x^4], x]", //
				"((2 + Sqrt[6]*x^2)*Sqrt[(2 + 3*x^4)/(2 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(3/2)^(1/4)*x], 1/2])/(2*6^(1/4)*Sqrt[-2 - 3*x^4])", //
				220);
	}

	// {1103}
	public void test1202() {
		check(//
				"Integrate[1/Sqrt[-2 - x^2 - 3*x^4], x]", //
				"((2 + Sqrt[6]*x^2)*Sqrt[(2 + x^2 + 3*x^4)/(2 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(3/2)^(1/4)*x], (12 - Sqrt[6])/24])/(2*6^(1/4)*Sqrt[-2 - x^2 - 3*x^4])", //
				1103);
	}

	// {1103}
	public void test1203() {
		check(//
				"Integrate[1/Sqrt[-2 - 2*x^2 - 3*x^4], x]", //
				"((2 + Sqrt[6]*x^2)*Sqrt[(2 + 2*x^2 + 3*x^4)/(2 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(3/2)^(1/4)*x], (6 - Sqrt[6])/12])/(2*6^(1/4)*Sqrt[-2 - 2*x^2 - 3*x^4])", //
				1103);
	}

	// {1103}
	public void test1204() {
		check(//
				"Integrate[1/Sqrt[-2 - 3*x^2 - 3*x^4], x]", //
				"((2 + Sqrt[6]*x^2)*Sqrt[(2 + 3*x^2 + 3*x^4)/(2 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(3/2)^(1/4)*x], (4 - Sqrt[6])/8])/(2*6^(1/4)*Sqrt[-2 - 3*x^2 - 3*x^4])", //
				1103);
	}

	// {1103}
	public void test1205() {
		check(//
				"Integrate[1/Sqrt[-2 - 4*x^2 - 3*x^4], x]", //
				"((2 + Sqrt[6]*x^2)*Sqrt[(2 + 4*x^2 + 3*x^4)/(2 + Sqrt[6]*x^2)^2]*EllipticF[2*ArcTan[(3/2)^(1/4)*x], 1/2 - 1/Sqrt[6]])/(2*6^(1/4)*Sqrt[-2 - 4*x^2 - 3*x^4])", //
				1103);
	}

	// {1103}
	public void test1206() {
		check(//
				"Integrate[1/Sqrt[2 + 5*x^2 + 5*x^4], x]", //
				"((2 + Sqrt[10]*x^2)*Sqrt[(2 + 5*x^2 + 5*x^4)/(2 + Sqrt[10]*x^2)^2]*EllipticF[2*ArcTan[(5/2)^(1/4)*x], (4 - Sqrt[10])/8])/(2*10^(1/4)*Sqrt[2 + 5*x^2 + 5*x^4])", //
				1103);
	}

	// {1103}
	public void test1207() {
		check(//
				"Integrate[1/Sqrt[2 + 5*x^2 + 4*x^4], x]", //
				"((1 + Sqrt[2]*x^2)*Sqrt[(2 + 5*x^2 + 4*x^4)/(1 + Sqrt[2]*x^2)^2]*EllipticF[2*ArcTan[2^(1/4)*x], (8 - 5*Sqrt[2])/16])/(2*2^(3/4)*Sqrt[2 + 5*x^2 + 4*x^4])", //
				1103);
	}
}