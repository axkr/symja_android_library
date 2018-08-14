package org.matheclipse.core.system;

import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.AtomQ;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.Coefficient;
import static org.matheclipse.core.expression.F.Complex;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Exponent;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.Head;
import static org.matheclipse.core.expression.F.I;
import static org.matheclipse.core.expression.F.If;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.MatchQ;
import static org.matheclipse.core.expression.F.MemberQ;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Null;
import static org.matheclipse.core.expression.F.Pi;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.UnsameQ;
import static org.matheclipse.core.expression.F.ZZ;
import static org.matheclipse.core.expression.F.a;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c;
import static org.matheclipse.core.expression.F.f;
import static org.matheclipse.core.expression.F.fraction;
import static org.matheclipse.core.expression.F.m;
import static org.matheclipse.core.expression.F.p;
import static org.matheclipse.core.expression.F.v;
import static org.matheclipse.core.expression.F.x;
import static org.matheclipse.core.integrate.rubi45.UtilityFunctionCtors.*;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;

/**
 * Tests for the Java port of the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi - rule-based integrator</a>.
 * 
 */
public class RubiIntegrationTest extends AbstractTestCase {
	public RubiIntegrationTest(String name) {
		super(name);
	}

	@Override
	public void check(String evalString, String expectedResult) {
		check(fScriptEngine, evalString, expectedResult, -1);
	}

	public void testRubi001() {
		IAST ast;
		ast = LinearQ(Times(2, x), x);
		check(ast, "True");

		// ???
		ast = LinearQ(C2, x);
		check(ast, "False");
	}

	public void testRubi002() {
		IAST ast;
		ast = ExpandToSum(Times(x, Plus(a, b)), x);
		check(ast, "(a+b)*x");
	}

	public void testRubi003() {
		IAST ast;

		ast = MemberQ(List($s("Sin"), $s("Cos"), $s("Tan"), $s("Cot"), $s("Sec"), $s("Csc")),
				If(AtomQ(Sin(Times(C2, x))), Sin(Times(C2, x)), Head(Sin(Times(C2, x)))));
		check(ast, "True");

		ast = UnsameQ(Coefficient(Times(2, x), x, C1), C0);
		check(ast, "True");

		ast = Exponent(Times(2, x), x);
		check(ast, "1");

		ast = PolyQ(Times(2, x), x, C1);
		check(ast, "True");

		ast = LinearQ(Times(2, x), x);
		check(ast, "True");

		ast = ActivateTrig(Sin(Times(2, x)));
		check(ast, "Sin[2*x]");

		ast = FunctionOfTrig(Sin(Times(2, x)), x);
		check(ast, "2*x");

		ast = FunctionOfTrigOfLinearQ(Sin(Times(2, x)), x);
		check(ast, "True");

		ast = MatchQ(Times(2, x), Condition(Plus(a_DEFAULT, Times(b_DEFAULT, x)), FreeQ(List(a, b), x)));
		check(ast, "True");

	}

	public void testRubi004() {
		IAST ast;

		// ast = RemoveContentAux(Plus(Times(x, b), Times(x, c)), x);
		// check(ast, "x");

		// ast = RemoveContent(Plus(Times(a, b), Times(a, c)), x);
		// check(ast, "1");

		ast = RemoveContent(Plus(Times(a, x), Times(a, c)), x);
		// ISymbol num=$s("num");
		// ISymbol tmp=$s("tmp");
		// IExpr expn=Plus(Times(a, x), Times(a, c)); //$s("expn");
		// ast=F.Module(
		// F.List(F.Set(num,C1),tmp),
		// F.CompoundExpression(F.Set(tmp,F.Map(F.Function(F.If(F.And(SumQ(F.Slot1),F.Less(NumericFactor(F.Part(F.Slot1,C1)),C0)),F.CompoundExpression(F.Set(num,F.Negate(num)),ContentFactorAux(F.Negate(F.Slot1))),ContentFactorAux(F.Slot1))),expn)),F.Times(num,UnifyNegativeBaseFactors(tmp)))
		// );
		check(F.eval(ast).toString(), "c+x");

		ast = RemoveContent(Plus(Times(x, b), Times(x, c)), x);
		check(ast, "b*x+c*x");

		ast = ContentFactor(Plus(Times(a, b), Times(a, c)));
		check(ast, "a*(b+c)");
		// (a_.*cos[v_]+b_.*sin[v_])^n_
		ast = TrigSimplifyAux(Power(Plus(Times(-1, Cos(v)), Times(CI, Sin(v))), -1));
		check(ast, "1/(-Cos[v]+I*Sin[v])");
	}

	public void testRubi005() {
		IAST ast;
		// SimpFixFactor[(a_.*Complex[0,c_] + b_.*Complex[0,d_])^p_.,x_]
		ast = SimpFixFactor(Power(Plus(Times(a, Complex(C0, c)), Times(b, Complex(C0, f))), -1), x);
		check(ast, "-I/(a*c+b*f)");

		ast = SimpFixFactor(Power(Plus(Times(a, I), Times(b, Complex(C0, f))), -1), x);
		check(ast, "-I/(a+b*f)");
	}

	public void testRubi006() {
		IAST ast;
		ast = ExpandToSum(x, x);
		check(ast, "x");

		ast = ExpandToSum(Plus(Negate(Sqr(x)), Times(CN2, x)), x);
		check(ast, "-2*x-x^2");

	}

	public void testRubi007() {
		IAST ast;

		// -x^2-2*x+3+Pi+x^4
		ast = QuadraticMatchQ(Plus(C3, Pi, Power(x, C4), Negate(Sqr(x)), Times(CN2, x)), x);
		check(ast, "False");

		// -x^2-2*x
		ast = QuadraticMatchQ(Plus(Negate(Sqr(x)), Times(CN2, x)), x);
		check(ast, "True");

		// -x^2-2*x+3
		ast = QuadraticMatchQ(Plus(C3, Negate(Sqr(x)), Times(CN2, x)), x);
		check(ast, "True");

		// -x^2-2*x+3+Pi
		ast = QuadraticMatchQ(Plus(C3, Pi, Negate(Sqr(x)), Times(CN2, x)), x);
		check(ast, "True");

		// -x^2-2*x+3+Pi+x^4
		ast = QuadraticMatchQ(Plus(C3, Pi, Power(x, C4), Negate(Sqr(x)), Times(CN2, x)), x);
		check(ast, "False");

		// -x^2-2*x+3+Pi+Sin(x)
		ast = QuadraticMatchQ(Plus(C3, Pi, Sin(x), Negate(Sqr(x)), Times(CN2, x)), x);
		check(ast, "False");
	}

	// public void testRubi008() {
	// IAST ast;
	// ast = RealNumericQ(C1);
	// check(ast, "True");
	// }

	public void testRubi009() {
		IAST ast;
		ast = FixSimplify(fraction(5L, 3L));
		check(ast, "5/3");
	}

	public void testRubi010() {
		IAST ast;
		ast = AbortRubi(Integrate);
		// throws AbortException
		check(ast, "Abort evaluation.\n" + "");

	}

	public void testRubi011() {
		IAST ast;
		// DeactivateTrig(Sin(a+b*x)^3,x)
		ast = DeactivateTrig(Power(Sin(Plus(a, Times(b, x))), C3), x);
		check(ast, "§sin[a+b*x]^3");
	}

	public void testRubi012() {
		IAST ast;
		// 2*(a+b*x),x
		ast = ExpandToSum(Times(C2, Plus(a, Times(b, x))), x);

		// throws AbortException
		check(ast, "2*a+2*b*x");

	}

	// public void testRubi013() {
	// IAST ast;
	// //
	// ast = BinomialParts(F.unary(F.g, x), x);
	//
	// check(ast, "False");
	//
	// ast = BinomialParts(Times(F.unaryAST1(F.f, x), F.unaryAST1(F.g, x)), x);
	//
	// check(ast, "False");
	// }

	public void testRubi014() {
		IAST ast;
		//
		ast = EasyDQ(x, x);

		check(ast, "True");

	}

	public void testRubi015() {
		IAST ast;
		ast = ContentFactor(Plus(a, b));
		check(ast, "a+b");
	}

	public void testRubi016() {
		IAST ast;
		// NormalizeTrig[func_,m_.*(n_.*Pi+r_.)+s_.,b_,x_]
		ast = NormalizeTrig(Sin, F.Plus(Times(F.C1D2, F.Plus(Times(F.C1D2, Pi), F.r)), F.s), F.CN1, F.x);
		check(ast, "Cos[Pi/4-r/2-s+x]");

		ast = NormalizeTrig(F.ArcSin, F.Plus(Times(F.C1D3, F.Plus(Times(F.C1D2, Pi), F.r)), F.s), F.CN1, F.x);
		check(ast, "ArcSin[Pi/6+r/3+s-x]");
	}

	public void testSqrtSin() {
		check("Integrate(Sqrt(a*Sin(x)^2),x)", "-Cot(x)*Sqrt(a*Sin(x)^2)");
	}

	public void testArcSin() {

		check("Integrate(ArcSin(x),x)", "Sqrt(1-x^2)+x*ArcSin(x)");
		check("Integrate(ArcCos(x),x)", "-Sqrt(1-x^2)+x*ArcCos(x)");

		// check("(-x^2-2*x)^(1/2)+2*ArcTan(x*(-x^2-2*x)^(-1/2))+x*ArcSin(x+1)",
		// "(-2*x-x^2)^(1/2)+x*ArcSin(1+x)+2*ArcTan(x*(-2*x-x^2)^(-1/2))");
		check("Integrate(ArcSin(x+1),x)",
				"x*ArcSin(1+x)+(I*1/3*Sqrt(2)*x^2*Sqrt(2+x)*Hypergeometric2F1(1/2,3/2,5/2,-x/2))/Sqrt(\n" + "2*x+x^2)");

		check("Integrate(ArcCos(x+1),x)", //
				"x*ArcCos(1+x)+(-I*1/3*Sqrt(2)*x^2*Sqrt(2+x)*Hypergeometric2F1(1/2,3/2,5/2,-x/2))/Sqrt(\n"
						+ "2*x+x^2)");
	}

	public void testSystemIntegrate() {
		// check("Integrate(x*Sin(x),x)", "-x*Cos(x)+Sin(x)");
		check("Integrate(Sin(x*y),y)", "-Cos(x*y)/x");

		check("Integrate(x^(-1),x)", "Log(x)");
		check("Integrate(x^(-1),{x,0,1})", "Integrate(1/x,{x,0,1})");
		check("Integrate(x^a, x)", "x^(1+a)/(1+a)");
		check("Integrate(f(x), x)", "Integrate(f(x),x)");
		check("Integrate(x^(-1),{x, 0, 1})", "Integrate(1/x,{x,0,1})");
		check("Integrate(f(x)+g(x), {x, a, b})", "Integrate(f(x)+g(x),{x,a,b})");
		check("Integrate(Sin(x), {x, 3, 4})", "Cos(3)-Cos(4)");
		check("Integrate(Sin(x), {x, a, b})", "Cos(a)-Cos(b)");
		check("Integrate(Sin(x*y),y)", "-Cos(x*y)/x");
		check("D(-Cos(x*y)*x^(-1),y)", "Sin(x*y)");
		check("Integrate(Sin(x*y),{y, 0, x})", "1/x-Cos(x^2)/x");
		// check("Integrate(Sin(x*y), {x, 0, 1}, {y, 0, x})",
		// "-1/2*CosIntegral(1)+1/2*CosIntegral(0)+Integrate(x^(-1),{x,0,1})");
	}

	public void testTrig001() {
		check("Integrate(x*Sin(a+x),x)", //
				"-I*1/2*E^(I*a+I*x)*(1-I*x)+(I*1/2*(1+I*x))/E^(I*a+I*x)");

		// check("Sin(x)^2*sec(x)", "Sin(x)*Tan(x)");
		check("Sin(x)*tan(x)", "Sin(x)*Tan(x)");
		check("Cos(x)*tan(x)", "Sin(x)");
		check("csc(x)*tan(x)", "Sec(x)");
		check("int(Sin(x)*tan(x),x)", //
				"-Log(1-Sin(x))/2+Log(1+Sin(x))/2-Sin(x)");

		check("Integrate(Sin(a + b*x)^3,x)", //
				"-2/3*Cos(a+b*x)/b+(-Cos(a+b*x)*Sin(a+b*x)^2)/(3*b)");

		check("Integrate(Sin(a+x),x)", "-Cos(a+x)");
		check("Integrate(x*Sin(a+x),x)", //
				"-I*1/2*E^(I*a+I*x)*(1-I*x)+(I*1/2*(1+I*x))/E^(I*a+I*x)");
		check("Integrate(x*Sin(a + b*x),x)", //
				"(-I*1/2*E^(I*a+I*b*x)*(1-I*b*x))/b^2+(I*1/2*(1+I*b*x))/(b^2*E^(I*a+I*b*x))");
		check("$f(a_.+b_.*c_):={a,b,c};$f(x)", //
				"{0,1,x}");
		check("$h($g(a_.+b_.*c_)):={a,b,c};$h($g(x))", //
				"{0,1,x}");

		// for (int i = 0; i < 50; i++) {

		check("Integrate(Sin(a + b*x),x)", "-Cos(a+b*x)/b");
		// check("D(-1/4*b^(-1)*Sin(2*b*x+2*a)+1/2*x,x)",
		// "-1/2*Cos(2*b*x+2*a)+1/2");
		check("Integrate(Sin(a + b*x)^2,x)", "x/2+(-Cos(a+b*x)*Sin(a+b*x))/(2*b)");
		check("Integrate(Sin(a + b*x)^3,x)", "-(Cos(a+b*x)-Cos(a+b*x)^3/3)/b");
		check("Integrate(Sin(a + b*x)^4,x)",
				"3/4*(x/2+(-Cos(a+b*x)*Sin(a+b*x))/(2*b))+(-Cos(a+b*x)*Sin(a+b*x)^3)/(4*b)");
		check("Integrate(Sin(a + b*x)^5,x)", "-(Cos(a+b*x)-2/3*Cos(a+b*x)^3+Cos(a+b*x)^5/5)/b");

		check("Integrate(Sin(a + b*x)^(1/2),x)", "(-2*EllipticE(Pi/4-(a+b*x)/2,2))/b");
		check("Integrate(Sin(a + b*x)^(3/2),x)",
				"-2/3*EllipticF(Pi/4-(a+b*x)/2,2)/b-2/3*(Cos(a+b*x)*Sqrt(Sin(a+b*x)))/b");
		check("Integrate(Sin(a + b*x)^(5/2),x)",
				"-6/5*EllipticE(Pi/4-(a+b*x)/2,2)/b-2/5*(Cos(a+b*x)*Sin(a+b*x)^(3/2))/b");

		check("Integrate(x*Sin(a + b*x),x)", //
				"(-I*1/2*E^(I*a+I*b*x)*(1-I*b*x))/b^2+(I*1/2*(1+I*b*x))/(b^2*E^(I*a+I*b*x))");
		check("D(b^(-2)*Sin(b*x+a)-Cos(b*x+a)*b^(-1)*x,x)", "x*Sin(a+b*x)");
		check("Integrate(x*Sin(a + b*x)^2,x)", //
				"-((E^(I*2*a+I*2*b*x)*(1-I*2*b*x))/(8*b^2)+(1+I*2*b*x)/(8*b^2*E^(I*2*a+I*2*b*x)))/\n" + "2+x^2/4");
		check("Integrate(x*Sin(a + b*x)^3,x)", //
				"-((I*1/2*E^(I*a+I*b*x)*(1-I*b*x))/b^2+(-I*1/2*(1+I*b*x))/(b^2*E^(I*a+I*b*x)))/4+\n"
						+ "1/2*((-I*1/2*E^(I*a+I*b*x)*(1-I*b*x))/b^2+(I*1/2*(1+I*b*x))/(b^2*E^(I*a+I*b*x)))-((-\n"
						+ "I*1/18*E^(I*3*a+I*3*b*x)*(1-I*3*b*x))/b^2+(I*1/18*(1+I*3*b*x))/(b^2*E^(I*3*a+I*3*b*x)))/\n"
						+ "4");

		check("Integrate(x^2*Sin(a + b*x),x)",
				"(E^(I*a+I*b*x)*(1-I*b*x-1/2*b^2*x^2))/b^3+(1+I*b*x-1/2*b^2*x^2)/(b^3*E^(I*a+I*b*x))");
		check("Integrate(x^2*Sin(a + b*x)^2,x)",
				"-((I*1/8*E^(I*2*a+I*2*b*x)*(1-I*2*b*x-2*b^2*x^2))/b^3+(-I*1/8*(1+I*2*b*x-2*b^2*x^\n"
						+ "2))/(b^3*E^(I*2*a+I*2*b*x)))/2+x^3/6");

		check("Integrate((a + b*Sin(c + g*x)),x)", "a*x+(-b*Cos(c+g*x))/g");
		check("Integrate((a + b*Sin(c + g*x))^2,x)",
				"a^2*x+(-2*a*b*Cos(c+g*x))/g+b^2*(x/2+(-Cos(c+g*x)*Sin(c+g*x))/(2*g))");

		// check("Simplify(D(-1/3*b*Cos(g*x+c)*g^(-1)*(b*Sin(g*x+c)+a)^2+1/3*(-5/4*a*b^2*g^(-1)*Sin(2*g*x+2*c)+\n"
		// +
		// "9/2*a*b^2*x+3*a^3*x-2*Cos(g*x+c)*b^3*g^(-1)-8*a^2*b*Cos(g*x+c)*g^(-1)),x))",
		// "");
		// check("Simplify(D(-1/3*b*Cos(g*x+c)*g^(-1)*(b*Sin(g*x+c)+a)^2+1/3*(2*a*b^2*x+3*a^3*x-2*Cos(g*x+c)*b^\n"
		// +
		// "3*g^(-1)-8*Cos(g*x+c)*a^2*b*g^(-1)+5*a*(-1/2*Cos(g*x+c)*g^(-1)*Sin(g*x+c)+1/2*x)*b^\n"
		// +
		// "2),x))", "");
		check("Integrate((a + b*Sin(c + g*x))^3,x)",
				"a^3*x+(-3*a^2*b*Cos(c+g*x))/g+(-b^3*(Cos(c+g*x)-Cos(c+g*x)^3/3))/g+3*a*b^2*(x/2+(-Cos(c+g*x)*Sin(c+g*x))/(\n"
						+ "2*g))");

		check("Integrate((a + b*Sin(c + g*x))^4,x)",
				"a^4*x+(-4*a^3*b*Cos(c+g*x))/g+(-4*a*b^3*(Cos(c+g*x)-Cos(c+g*x)^3/3))/g+6*a^2*b^2*(x/\n"
						+ "2+(-Cos(c+g*x)*Sin(c+g*x))/(2*g))+b^4*(3/4*(x/2+(-Cos(c+g*x)*Sin(c+g*x))/(2*g))+(-Cos(c+g*x)*Sin(c+g*x)^\n"
						+ "3)/(4*g))");

		check("$f(a_.+b_.*c_):={a,b,c};$f(x)", "{0,1,x}");

		check("Integrate(Sqrt(a*Sin(x)^2),x)", "-Cot(x)*Sqrt(a*Sin(x)^2)");
		check("Integrate(Sqrt(a*Sin(x)),x)", "(-2*EllipticE(Pi/4-x/2,2)*Sqrt(a*Sin(x)))/Sqrt(Sin(x))");

		check("Integrate(Sin(b*x^2),x)", "(Sqrt(Pi)*FresnelS((Sqrt(2)*Sqrt(b)*x)/Sqrt(Pi)))/(Sqrt(2)*Sqrt(b))");

		check("Integrate(Sin(x)/Sqrt(x),x)", //
				"(2*Sqrt(Pi)*FresnelS((Sqrt(2)*Sqrt(x))/Sqrt(Pi)))/Sqrt(2)");
	}

	public void testRationalFunction001() {
		check("PolynomialQ(x^2*(a+b*x^3)^16,x)", "True");

		check("Integrate(x^2*(a+b*x^3)^16,x)", //
				"2/51*a^16*x^3+1/51*a^15*b*x^6+1/51*a^14*x^3*(a+b*x^3)^2+1/51*a^13*x^3*(a+b*x^3)^\n"
						+ "3+1/51*a^12*x^3*(a+b*x^3)^4+1/51*a^11*x^3*(a+b*x^3)^5+1/51*a^10*x^3*(a+b*x^3)^6+\n"
						+ "1/51*a^9*x^3*(a+b*x^3)^7+1/51*a^8*x^3*(a+b*x^3)^8+1/51*a^7*x^3*(a+b*x^3)^9+1/51*a^\n"
						+ "6*x^3*(a+b*x^3)^10+1/51*a^5*x^3*(a+b*x^3)^11+1/51*a^4*x^3*(a+b*x^3)^12+1/51*a^3*x^\n"
						+ "3*(a+b*x^3)^13+1/51*a^2*x^3*(a+b*x^3)^14+1/51*a*x^3*(a+b*x^3)^15+1/51*x^3*(a+b*x^\n"
						+ "3)^16");
		check("Integrate(x^(k-1)*(a+b*x^k)^m,x)", //
				"(x^k*(a+b*x^k)^m*Hypergeometric2F1(-m,1,2,(-b*x^k)/a))/(k*(1+(b*x^k)/a)^m)");
		check("Integrate(x^(k-1)*(a+b*x^k)^16,x)", //
				"2/17*(a^16*x^k)/k+(a^15*b*x^(2*k))/(17*k)+(a^14*x^k*(a+b*x^k)^2)/(17*k)+(a^13*x^k*(a+b*x^k)^\n"
						+ "3)/(17*k)+(a^12*x^k*(a+b*x^k)^4)/(17*k)+(a^11*x^k*(a+b*x^k)^5)/(17*k)+(a^10*x^k*(a+b*x^k)^\n"
						+ "6)/(17*k)+(a^9*x^k*(a+b*x^k)^7)/(17*k)+(a^8*x^k*(a+b*x^k)^8)/(17*k)+(a^7*x^k*(a+b*x^k)^\n"
						+ "9)/(17*k)+(a^6*x^k*(a+b*x^k)^10)/(17*k)+(a^5*x^k*(a+b*x^k)^11)/(17*k)+(a^4*x^k*(a+b*x^k)^\n"
						+ "12)/(17*k)+(a^3*x^k*(a+b*x^k)^13)/(17*k)+(a^2*x^k*(a+b*x^k)^14)/(17*k)+(a*x^k*(a+b*x^k)^\n"
						+ "15)/(17*k)+(x^k*(a+b*x^k)^16)/(17*k)");
	}

	public void testRationalFunction002() {
		IAST ast;

		ast = SubstForFractionalPowerOfLinear(Times(Power(x, Times(ZZ(12L), m)), Power(x, p)), x);
		check(ast, "False");

		check("Integrate(x^(12*m)*x^p,x)", "x^(1+12*m+p)/(1+12*m+p)");
		// check("Integrate(x^p*(a*x^n+b*x^(12*n+n+p+1))^12,x)", "");

		check("D(b^(-1)*x^(-m*n-n)*(m+1)^(-1)*(p+m*n+1)^(-1)*(b*x^(p+m*n+n+1)+a*x^n)^(m+1),x)",
				"(((a*n)/x^(1-n)+b*(1+n+m*n+p)*x^(n+m*n+p))*(a*x^n+b*x^(1+n+m*n+p))^m)/(b*(1+m*n+p)*x^(n+m*n))+((-n-m*n)*(a*x^n+b*x^(\n"
						+ "1+n+m*n+p))^(1+m))/(b*(1+m)*(1+m*n+p)*x^(1+n+m*n))");

		// check("Simplify((-m*n-n)*b^(-1)*x^(-m*n-n-1)*(m+1)^(-1)*(p+m*n+1)^(-1)*(b*x^(p+m*n+n+1)+a*x^n)^(m+\n"
		// +
		// "1)+(b*(p+m*n+n+1)*x^(p+m*n+n)+a*n*x^(n-1))*b^(-1)*x^(-m*n-n)*(p+m*n+1)^(-1)*(b*x^(p+m*n+n+\n"
		// +
		// "1)+a*x^n)^m)","x^p*(a*x^n+b*x^(m*n+n+p+1))^m");
		check("Integrate(x^24*(a*x+b*x^38)^12,x)", //
				"(a*x+b*x^38)^13/(481*b*x^13)");
		check("Integrate(x^p*(a*x^n+b*x^(m*n+n+p+1))^m,x)",
				"(x^(1+p)*(a*x^n+b*x^(1+n+m*n+p))^m*Hypergeometric2F1(-m,1,2,(-b*x^(1+m*n+p))/a))/((\n"
						+ "1+m*n+p)*(1+(b*x^(1+m*n+p))/a)^m)");

	}

	public void testAnonymousFunction003() {
		check("Integrate(f(x+1),x)", "Integrate(f(1+x),x)");
	}

	public void testRationalFunction003() {
		check("Integrate((b+2*c*x)*(a+b*x+c*x^2)^m, x)",
				"(-b*(a+b*x+c*x^2)^(1+m)*Hypergeometric2F1(-m,1+m,2+m,(b+Sqrt(b^2-4*a*c)+2*c*x)/(\n"
						+ "2*Sqrt(b^2-4*a*c))))/(Sqrt(b^2-4*a*c)*(1+m)*(-(b-Sqrt(b^2-4*a*c)+2*c*x)/(2*Sqrt(b^\n"
						+ "2-4*a*c)))^(1+m))+2*c*((a+b*x+c*x^2)^(1+m)/(2*c*(1+m))+(b*(a+b*x+c*x^2)^(1+m)*Hypergeometric2F1(-m,\n"
						+ "1+m,2+m,(b+Sqrt(b^2-4*a*c)+2*c*x)/(2*Sqrt(b^2-4*a*c))))/(2*c*Sqrt(b^2-4*a*c)*(1+m)*(-(b-Sqrt(b^\n"
						+ "2-4*a*c)+2*c*x)/(2*Sqrt(b^2-4*a*c)))^(1+m)))");
		check("D((a+b*x+c*x^2)^13/13,x)", //
				"(b+2*c*x)*(a+b*x+c*x^2)^12");
		check("Integrate((b+2*c*x)*(a+b*x+c*x^2)^12, x)", //
				"(a+b*x+c*x^2)^13/13");
	}

	public void testTrigFunction001() {
		// check("Integrate(1/(1+sec(a+b*x)), x)", "");

		check("Integrate(Sin(x)*Cos(x),x)", //
				"-Cos(2*x)/4");
		// try {
		// Thread.wait(1000000000);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		check("Integrate(Sin(x)*Cos(x)*Pi,x)", //
				"-1/4*Pi*Cos(2*x)");
		check("D((-1/2)*Pi*Cos(x)^2,x)", "Pi*Cos(x)*Sin(x)");

		check("Integrate(1/(1+sin(a+b*x)), x)", "-Cos(a+b*x)/(b*(1+Sin(a+b*x)))");
		check("Integrate(1/(1-sin(a+b*x)), x)", //
				"Cos(a+b*x)/(b*(1-Sin(a+b*x)))");

		check("Integrate(1/sin(a+b*x), x)", "-ArcTanh(Cos(a+b*x))/b");
		check("Integrate(1/sin(a-b*x), x)", "ArcTanh(Cos(a-b*x))/b");

	}

	public void testSqrtLn() {
		check("integrate(Sqrt(x)*Log(x), x)", //
				"-4/9*x^(3/2)+2/3*x^(3/2)*Log(x)");
		check("D(2/3*Log(x)*x^(3/2)-4/9*x^(3/2),x)", //
				"Sqrt(x)*Log(x)");
	}

	public void testTimesFx() {
		check("integrate(g(x)*f(x), x)", "Integrate(f(x)*g(x),x)");
		// check("integrate(sqrt(x)*f(x), x)", "");
	}

	public void testFraction001() {
		check("ExpandAll(-(2-x)^(-1)*x^(-2))", //
				"1/(-2*x^2+x^3)");
		check("integrate(1/(-2*x^2+x^3),x)", //
				"1/(2*x)-Log(x)/4+Log(-8+4*x)/4");
		check("integrate(1/(x^2*(x-2)),x)", //
				"1/(2*x)-Log(x)/4+Log(-8+4*x)/4");
		check("Expand(-(2-x)^(-1)*x^(-2))", //
				"-1/((2-x)*x^2)");
	}

	public void testIssue64() {
		// check("Integrate(e^(x^2+2x+3)(2x+2),x)", "");
		check("Integrate((x^2+1)/(x^3+3*x),x)", "Log(3*x)/3+Log(3+x^2)/3");
		check("Together(D(1/3*Log(3*x^2+9)+1/3*Log(x),x))", "(1+x^2)/(3*x+x^3)");
		check("Together(D(1/3*Log(x^2+3)+1/3*Log(x),x))", "(1+x^2)/(3*x+x^3)");
	}

	public void testIssue82() {
		check("Integrate(Sin(a^4)*a,a)",
				"(I*1/8*Sqrt(Pi)*Erf((-1)^(1/4)*a^2))/(-1)^(1/4)+(-I*1/8*Sqrt(Pi)*Erfi((-1)^(1/4)*a^\n"
						+ "2))/(-1)^(1/4)");
		check("Integrate(Sin(a^4)*a,{a,0,2.0})",
				"(0.15666+I*(-0.15666))*Erf(-2.82843+I*2.82843)+(0.15666+I*0.15666)*Erf(2.82843+I*2.82843)");
	}

	public void testIssue83() {
		check("Integrate(Sin(x) / x,x)", //
				"SinIntegral(x)");
		check("Integrate(Sin(x) / x, {x,0,0.5})", //
				"0.49311");
		check("Integrate(Cos(x) / x,x)", //
				"CosIntegral(x)");
		check("Integrate(Cos(x) / x, {x,0.25,0.5})", //
				"0.64688");
	}

	public void testIssue84() {
		// check("Simplify(D(1/2*(-1/2*Log(x^2-x*2^(1/2)+1)*2^(-1/2)+1/2*Log(x^2+x*2^(1/2)+1)*2^(-1/2))+1/2*(ArcTan(\n"
		// + "2*x*2^(-1/2)+1)*2^(-1/2)-ArcTan(-2*x*2^(-1/2)+1)*2^(-1/2)),x))",
		// "");
		check("Integrate(1/(1+x^4),x)", //
				"ArcTan(1+Sqrt(2)*x)/(2*Sqrt(2))-ArcTan(1-Sqrt(2)*x)/(2*Sqrt(2))+Log(1+Sqrt(2)*x+x^\n"
						+ "2)/(4*Sqrt(2))-Log(1-Sqrt(2)*x+x^2)/(4*Sqrt(2))");
	}

	public void testIssue110() {

		check("D(-Log(-1+x),x)", "1/(1-x)");
		check("Together(-1/(-1+x))", "1/(1-x)");
		check("Integrate(1/(x-1), x)", "Log(-1+x)");
		check("Integrate(1/(1-x), x)", "-Log(1-x)");
		check("Integrate(1/(42-x), x)", "-Log(42-x)");

		check("Integrate(1/(I-x), x)", //
				"-Log(I-x)");
		check("Integrate(1/(x-I), x)", //
				"Log(I-x)");

		check("D(-Log(-1+x),x)", "1/(1-x)");
	}

	public void testGithub21() {
		check("Integrate(4/(1-3*(x)) + 1/2*Sqrt((x)) - 5,x)", //
				"-5*x+x^(3/2)/3-4/3*Log(1/3-x)");
		check("Simplify(D(-5*x+x^(3/2)/3-4/3*Log(1/3-x),x))", //
				"-5+4/(1-3*x)+Sqrt(x)/2");
		check("PowerExpand(ln(3*(1/3-x)))", //
				"Log(3)+Log(1/3-x)");
	}

	public void testGithub22() {
		check("Int(Sin(x)^3*Cos(x),x)", //
				"-Cos(2*x)/8+Cos(4*x)/32");
		check("D(-Cos(2*x)/8+Cos(4*x)/32,x)", //
				"Sin(2*x)/4-Sin(4*x)/8");

		// unfortunately Simplify is not sophisticated enough to show this:
		// check("Simplify(Sin(2*x)/4-Sin(4*x)/8)", //
		// "Sin(x)^3*Cos(x)");

		check("Expand(TrigToExp(Sin(x)^3*Cos(x)))", //
				"(-I*1/16)/E^(I*4*x)+(I*1/8)/E^(I*2*x)-I*1/8*E^(I*2*x)+I*1/16*E^(I*4*x)");
		check("Expand(TrigToExp(Sin(2*x)/4-Sin(4*x)/8))", //
				"(-I*1/16)/E^(I*4*x)+(I*1/8)/E^(I*2*x)-I*1/8*E^(I*2*x)+I*1/16*E^(I*4*x)");
	}
}
