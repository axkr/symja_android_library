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
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.AbortRubi;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ActivateTrig;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ContentFactor;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.DeactivateTrig;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EasyDQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandToSum;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FixSimplify;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FunctionOfTrig;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FunctionOfTrigOfLinearQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NormalizeTrig;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PolyQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.QuadraticMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RemoveContent;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SimpFixFactor;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SubstAux;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SubstForFractionalPowerOfLinear;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.TrigSimplifyAux;

import javax.script.ScriptException;

import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

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

	public void testRubiRule001() {
		// check("Int[(F_)^((a_.) + (b_.)*((c_.) + (d_.)*(x_))^(n_))*((e_.) + (f_.)*(x_))^(m_.), x_Symbol] :=
		// {F,a,b,c,d,x,n,m}", //
		// "Null");
		// check("Int[F^(a+b/(c+d*x)^3)*(c+d*x),x]", //
		// "");
		try {
			fScriptEngine.put("RETURN_OBJECT", Boolean.TRUE);
			IExpr expr = (IExpr) fScriptEngine.eval("myfunction((e_.+f_.*x_)^m_., x_) := {e,f,m,x}");
			IExpr lhsEval = (IExpr) fScriptEngine.eval("myfunction(c+d*x ,x)");
			assertEquals(lhsEval.toString(), "{c,d,1,x}");
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}

	public void testRubiRule002() {
		// check("Int[(F_)^((a_.) + (b_.)*((c_.) + (d_.)*(x_))^(n_))*((e_.) + (f_.)*(x_))^(m_.), x_Symbol] :=
		// {F,a,b,c,d,x,n,m}", //
		// "Null");
		// check("Int[F^(a+b/(c+d*x)^3)*(c+d*x),x]", //
		// "");
		try {
			fScriptEngine.put("RETURN_OBJECT", Boolean.TRUE);
			IExpr expr = (IExpr) fScriptEngine.eval(
					"myfunction((F_)^((a_.) + (b_.)*((c_.) + (d_.)*(x_))^(n_))*((e_.) + (f_.)*(x_))^(m_.), x_Symbol) := {F,a,b,c,d,x,n,m}");
			IExpr lhsEval = (IExpr) fScriptEngine.eval("myfunction(F^(a+b/(c+d*x)^3)*(c+d*x),x)");
			assertEquals(lhsEval.toString(), "{F,a,b,c,d,x,-3,1}");
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}

	public void testRubiRule003() {
		try {
			fScriptEngine.put("RETURN_OBJECT", Boolean.TRUE);
			// IExpr expr = (IExpr) fScriptEngine.eval(
			// "myfunction[Sqrt[(e_) + (f_.)*(x_)]/(Sqrt[(b_.)*(x_)]*Sqrt[(c_) + (d_.)*(x_)]), x_Symbol] :=
			// {e,f,x,b,c,d}");
			// IExpr lhsEval = (IExpr) fScriptEngine.eval("myfunction[Sqrt[1-x]/(Sqrt[-x]*Sqrt[1+x]), x]");
			IExpr expr = (IExpr) fScriptEngine.eval("myfunction(Sqrt((e_) + (f_.)*(x_)), x_Symbol) := {e,f,x,b,c,d}");
			IExpr lhsEval = (IExpr) fScriptEngine.eval("myfunction(Sqrt(1-x), x)");
			assertEquals(lhsEval.toString(), "{1,-1,x,b,c,d}");
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}

	public void testRubiRule004() {
		try {
			fScriptEngine.put("RETURN_OBJECT", Boolean.TRUE);
			// IExpr expr = (IExpr) fScriptEngine.eval(
			// "myfunction[Sqrt[(e_) + (f_.)*(x_)]/(Sqrt[(b_.)*(x_)]*Sqrt[(c_) + (d_.)*(x_)]), x_Symbol] :=
			// {e,f,x,b,c,d}");
			// IExpr lhsEval = (IExpr) fScriptEngine.eval("myfunction[Sqrt[1-x]/(Sqrt[-x]*Sqrt[1+x]), x]");
			IExpr expr = (IExpr) fScriptEngine
					.eval("myfunction(Sqrt((e_) + (f_.)*(x_))/Sqrt((b_.)*(x_)), x_Symbol) := {e,f,x,b,c,d}");
			IExpr lhsEval = (IExpr) fScriptEngine.eval("myfunction(Sqrt(1-x)/Sqrt(-x), x)");
			assertEquals(lhsEval.toString(), "{1,-1,x,-1,c,d}");
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}

	public void testRubiRule005() {
		try {
			fScriptEngine.put("RETURN_OBJECT", Boolean.TRUE);
			IExpr lhsEval = (IExpr) fScriptEngine.eval("Integrate(F^(a+b/(c+d*x)^3)*(c+d*x),x)");
			assertEquals(lhsEval.toString(),
					"(F^a*(c+d*x)^2*Gamma(-2/3,(-b*Log(F))/(c+d*x)^3)*((-b*Log(F))/(c+d*x)^3)^(2/3))/(\n" + "3*d)");
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}

	public void testRubiRule006() {
		try {
			fScriptEngine.put("RETURN_OBJECT", Boolean.TRUE);
			IExpr lhsEval = (IExpr) fScriptEngine.eval("Integrate(Sqrt(1-x)/(Sqrt(-x)*Sqrt(1+x)), x)");
			assertEquals(lhsEval.toString(), "-2*EllipticE(ArcSin(Sqrt(-x)),-1)");
		} catch (ScriptException e) {
			e.printStackTrace();
		}
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

	public void testRubi017() {
		IAST ast;
		ast = F.Times(F.z, Dist(F.C0, F.v, F.w));
		check(ast, "0");
	}

	public void testRubi018() {
		IASTAppendable ast = F.quaternary(Plus, F.z, Dist(F.C3, F.v, F.w), Dist(F.C0, F.v, F.w), F.g);
		EvalAttributes.sort(ast);
		check(ast, "g+3*v+z");
	}

	public void testRubi019() {
		IASTAppendable ast = F.quaternary(Plus, Dist(F.C3, F.v, F.w), F.a, F.Times(F.CN1, Dist(F.C3, F.v, F.w)), F.g);
		EvalAttributes.sort(ast);
		check(ast, "a+g");
	}

	// Integrate(1/(a_.+b_.*x_+c_.*x_^2),x_Symbol) :=
	// Integrate::Dist(-2,integrate::subst(Integrate(1/integrate::simp(b^2+(-1)*4*a*c-x^
	// 2,x),x),x,b+2*c*x),x)/;FreeQ({a,b,c},x)&&integrate::neq(b^2+(-1)*4*a*c,0)
	// >>> Integrate(1/(3+2*x+x^2),x) >>>> 0
	public void testRubi020() {
		IAST ast;
		// IExpr e=F.eval(" -(ArcTan[(1+x)/Sqrt[2]]/(2* Sqrt[2]))");
		// System.out.println(e.toString());
		// check(e.toString(), "False");

		// Integrate((x^2+2*x+3)^(-1),x)
		ast = F.Integrate(F.Power(F.Plus(F.Power(F.x, 2), F.Times(F.C2, F.x), F.C3), -1), F.x);
		check(ast, "ArcTan[(1+x)/Sqrt[2]]/Sqrt[2]");

		ast = Simp(F.Plus(F.Power(F.C2, 2), F.Times(F.CN4, F.C3, F.C1), F.Times(F.CN1, F.Power(F.x, 2))), F.x);
		check(ast, "-8-x^2");

		IAST integral = F.Integrate(F.Power(ast, -1), F.x);
		check(integral, "-ArcTan[x/(2*Sqrt[2])]/(2*Sqrt[2])");

		ast = PolyQ(integral, F.x);
		check(ast, "False");

		ast = F.Times(F.x, F.Power(F.Times(F.C2, F.Sqrt(2)), -1));
		ast = PolyQ(ast, F.x, F.C1);
		System.out.println(ast.toString());
		check(ast, "True");

		ast = F.Times(F.Plus(F.C2, F.Times(2, F.x)), F.Power(F.Times(F.C2, F.Sqrt(2)), -1));
		ast = PolyQ(ast, F.x);
		System.out.println(ast.toString());
		check(ast, "True");

		ast = F.Plus(F.C2, F.Times(2, F.x));
		ast = PolyQ(ast, F.x, F.C1);
		System.out.println(ast.toString());
		check(ast, "True");

		ast = SubstAux(integral, F.x, F.Plus(F.C2, F.Times(F.C2, F.C1, F.x)), F.True);

		check(ast, "-ArcTan[(1+x)/Sqrt[2]]/(2*Sqrt[2])");
		System.out.println(ast.toString());

		// ast = Simp(F.Plus(F.C4,F.ZZ(-12),F.Power(F.x, 2)), F.x);
		//
		// System.out.println(ast.toString());
		// check(ast, "-8+x^2");

		// check("Integrate(1/(-8+x^2),x)", //
		// "-ArcTanh(x/(2*Sqrt(2)))/(2*Sqrt(2))");

		// integral = F.Integrate(F.Power(F.Plus(F.CN8,F.Power(F.x, 2)), -1), F.x);
		// ast = SubstAux(integral, F.x, F.Plus(F.C2, F.Times(F.C2, F.C1, F.x)), F.True);
		//
		// System.out.println(ast.toString());
		// check(ast, "-ArcTanh[1/Sqrt[2]]/(2*Sqrt[2])");
		//
		// ast = Dist(F.CN2, integral, F.x);
		// System.out.println(ast.toString());
		// check(ast, "ArcTanh[x/(2*Sqrt[2])]/Sqrt[2]");
	}

	public void testSqrtSin() {
		check("Integrate(Sqrt(a*Sin(x)^2),x)", "-Cot(x)*Sqrt(a*Sin(x)^2)");
	}

	public void testArcSin() {

		check("Integrate(ArcSin(x),x)", "Sqrt(1-x^2)+x*ArcSin(x)");
		check("Integrate(ArcCos(x),x)", "-Sqrt(1-x^2)+x*ArcCos(x)");

		check("Integrate(ArcSin(x+1),x)", //
				"Sqrt(-2*x-x^2)+ArcSin(1+x)+x*ArcSin(1+x)");

		check("Integrate(ArcCos(x+1),x)", //
				"-Sqrt(-2*x-x^2)+x*ArcCos(1+x)-ArcSin(1+x)");
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
				"-x*Cos(a+x)+Sin(a+x)");

		// check("Sin(x)^2*sec(x)", "Sin(x)*Tan(x)");
		check("Sin(x)*tan(x)", "Sin(x)*Tan(x)");
		check("Cos(x)*tan(x)", "Sin(x)");
		check("csc(x)*tan(x)", "Sec(x)");
		check("int(Sin(x)*tan(x),x)", //
				"ArcTanh(Sin(x))-Sin(x)");

		check("Integrate(Sin(a + b*x)^3,x)", //
				"-Cos(a+b*x)/b+Cos(a+b*x)^3/(3*b)");

		check("Integrate(Sin(a+x),x)", "-Cos(a+x)");
		check("Integrate(x*Sin(a+x),x)", //
				"-x*Cos(a+x)+Sin(a+x)");
		check("Integrate(x*Sin(a + b*x),x)", //
				"(-x*Cos(a+b*x))/b+Sin(a+b*x)/b^2");
		check("$f(a_.+b_.*c_):={a,b,c};$f(x)", //
				"{0,1,x}");
		check("$h($g(a_.+b_.*c_)):={a,b,c};$h($g(x))", //
				"{0,1,x}");

		// for (int i = 0; i < 50; i++) {

		check("Integrate(Sin(a + b*x),x)", "-Cos(a+b*x)/b");
		// check("D(-1/4*b^(-1)*Sin(2*b*x+2*a)+1/2*x,x)",
		// "-1/2*Cos(2*b*x+2*a)+1/2");
		check("Integrate(Sin(a + b*x)^2,x)", "x/2+(-Cos(a+b*x)*Sin(a+b*x))/(2*b)");
		check("Integrate(Sin(a + b*x)^3,x)", //
				"-Cos(a+b*x)/b+Cos(a+b*x)^3/(3*b)");
		check("Integrate(Sin(a + b*x)^4,x)", "3/8*x-3/8*(Cos(a+b*x)*Sin(a+b*x))/b+(-Cos(a+b*x)*Sin(a+b*x)^3)/(4*b)");
		check("Integrate(Sin(a + b*x)^5,x)", //
				"-Cos(a+b*x)/b+2/3*Cos(a+b*x)^3/b-Cos(a+b*x)^5/(5*b)");

		check("Integrate(Sin(a + b*x)^(1/2),x)", //
				"(2*EllipticE(1/2*(a-Pi/2+b*x),2))/b");
		check("Integrate(Sin(a + b*x)^(3/2),x)",
				"2/3*EllipticF(1/2*(a-Pi/2+b*x),2)/b-2/3*(Cos(a+b*x)*Sqrt(Sin(a+b*x)))/b");
		check("Integrate(Sin(a + b*x)^(5/2),x)",
				"6/5*EllipticE(1/2*(a-Pi/2+b*x),2)/b-2/5*(Cos(a+b*x)*Sin(a+b*x)^(3/2))/b");

		check("Integrate(x*Sin(a + b*x),x)", //
				"(-x*Cos(a+b*x))/b+Sin(a+b*x)/b^2");
		check("D(b^(-2)*Sin(b*x+a)-Cos(b*x+a)*b^(-1)*x,x)", "x*Sin(a+b*x)");
		check("Integrate(x*Sin(a + b*x)^2,x)", //
				"x^2/4+(-x*Cos(a+b*x)*Sin(a+b*x))/(2*b)+Sin(a+b*x)^2/(4*b^2)");
		check("Integrate(x*Sin(a + b*x)^3,x)", //
				"-2/3*(x*Cos(a+b*x))/b+2/3*Sin(a+b*x)/b^2+(-x*Cos(a+b*x)*Sin(a+b*x)^2)/(3*b)+Sin(a+b*x)^\n"
						+ "3/(9*b^2)");

		check("Integrate(x^2*Sin(a + b*x),x)", //
				"(2*Cos(a+b*x))/b^3+(-x^2*Cos(a+b*x))/b+(2*x*Sin(a+b*x))/b^2");

	}

	public void testTrig002() {

		check("Integrate(x^2*Sin(a + b*x)^2,x)", //
				"-x/(4*b^2)+x^3/6+(Cos(a+b*x)*Sin(a+b*x))/(4*b^3)+(-x^2*Cos(a+b*x)*Sin(a+b*x))/(2*b)+(x*Sin(a+b*x)^\n" + //
						"2)/(2*b^2)");

		check("Integrate((a + b*Sin(c + g*x)),x)", //
				"a*x+(-b*Cos(c+g*x))/g");
		check("Integrate((a + b*Sin(c + g*x))^2,x)",
				"1/2*(2*a^2+b^2)*x+(-2*a*b*Cos(c+g*x))/g+(-b^2*Cos(c+g*x)*Sin(c+g*x))/(2*g)");

		// check("Simplify(D(-1/3*b*Cos(g*x+c)*g^(-1)*(b*Sin(g*x+c)+a)^2+1/3*(-5/4*a*b^2*g^(-1)*Sin(2*g*x+2*c)+\n"
		// +
		// "9/2*a*b^2*x+3*a^3*x-2*Cos(g*x+c)*b^3*g^(-1)-8*a^2*b*Cos(g*x+c)*g^(-1)),x))",
		// "");
		// check("Simplify(D(-1/3*b*Cos(g*x+c)*g^(-1)*(b*Sin(g*x+c)+a)^2+1/3*(2*a*b^2*x+3*a^3*x-2*Cos(g*x+c)*b^\n"
		// +
		// "3*g^(-1)-8*Cos(g*x+c)*a^2*b*g^(-1)+5*a*(-1/2*Cos(g*x+c)*g^(-1)*Sin(g*x+c)+1/2*x)*b^\n"
		// +
		// "2),x))", "");
		check("Integrate((a + b*Sin(c + g*x))^3,x)", //
				"1/2*a*(2*a^2+3*b^2)*x-2/3*(b*(4*a^2+b^2)*Cos(c+g*x))/g-5/6*(a*b^2*Cos(c+g*x)*Sin(c+g*x))/g+(-b*Cos(c+g*x)*(a+b*Sin(c+g*x))^\n"
						+ "2)/(3*g)");

		check("Integrate((a + b*Sin(c + g*x))^4,x)", //
				"1/8*(8*a^4+24*a^2*b^2+3*b^4)*x+(-a*b*(19*a^2+16*b^2)*Cos(c+g*x))/(6*g)+(-b^2*(26*a^\n"
						+ "2+9*b^2)*Cos(c+g*x)*Sin(c+g*x))/(24*g)-7/12*(a*b*Cos(c+g*x)*(a+b*Sin(c+g*x))^2)/g+(-b*Cos(c+g*x)*(a+b*Sin(c+g*x))^\n"
						+ "3)/(4*g)");

		check("$f(a_.+b_.*c_):={a,b,c};$f(x)", //
				"{0,1,x}");

		check("Integrate(Sqrt(a*Sin(x)^2),x)", //
				"-Cot(x)*Sqrt(a*Sin(x)^2)");
		check("Integrate(Sqrt(a*Sin(x)),x)", //
				"(2*EllipticE(-Pi/4+x/2,2)*Sqrt(a*Sin(x)))/Sqrt(Sin(x))");

		check("Integrate(Sin(b*x^2),x)", //
				"(Sqrt(Pi/2)*FresnelS(Sqrt(b)*Sqrt(2/Pi)*x))/Sqrt(b)");

		check("Integrate(Sin(x)/Sqrt(x),x)", //
				"2*Sqrt(Pi/2)*FresnelS(Sqrt(2/Pi)*Sqrt(x))");
	}

	public void testRationalFunction001() {
		check("PolynomialQ(x^2*(a+b*x^3)^16,x)", "True");

		check("Integrate(x^2*(a+b*x^3)^16,x)", //
				"(a+b*x^3)^17/(51*b)");
		check("Integrate(x^(k-1)*(a+b*x^k)^m,x)", //
				"(a+b*x^k)^(1+m)/(b*k*(1+m))");
		check("Integrate(x^(k-1)*(a+b*x^k)^16,x)", //
				"(a+b*x^k)^17/(17*b*k)");
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
				"((a+b*x^(1+m*n+p))*(a*x^n+b*x^(1+n+m*n+p))^m)/(b*(1+m)*(1+m*n+p)*x^(m*n))");

	}

	public void testAnonymousFunction003() {
		check("Integrate(f(x+1),x)", //
				"Integrate(f(1+x),x)");
	}

	public void testRationalFunction003() {
		check("Integrate((b+2*c*x)*(a+b*x+c*x^2)^m, x)", "(a+b*x+c*x^2)^(1+m)/(1+m)");
		check("D((a+b*x+c*x^2)^13/13,x)", //
				"(b+2*c*x)*(a+b*x+c*x^2)^12");
		check("Integrate((b+2*c*x)*(a+b*x+c*x^2)^12, x)", //
				"(a+b*x+c*x^2)^13/13");
	}

	public void testTrigFunction001() {
		// check("Integrate(1/(1+sec(a+b*x)), x)", "");

		check("Integrate(Sin(x)*Cos(x),x)", //
				"-Cos(x)^2/2");
		// try {
		// Thread.wait(1000000000);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		check("Integrate(Sin(x)*Cos(x)*Pi,x)", //
				"-1/2*Pi*Cos(x)^2");
		check("D((-1/2)*Pi*Cos(x)^2,x)", //
				"Pi*Cos(x)*Sin(x)");

		check("Integrate(1/(1+sin(a+b*x)), x)", //
				"-Cos(a+b*x)/(b*(1+Sin(a+b*x)))");
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
		check("Integrate((x^2+1)/(x^3+3*x),x)", //
				"Log(3*x+x^3)/3");
		check("Together(D(1/3*Log(3*x^2+9)+1/3*Log(x),x))", "(1+x^2)/(3*x+x^3)");
		check("Together(D(1/3*Log(x^2+3)+1/3*Log(x),x))", "(1+x^2)/(3*x+x^3)");
	}

	public void testIssue82() {
		check("Integrate(Sin(a^4)*a,a)", //
				"1/2*Sqrt(Pi/2)*FresnelS(a^2*Sqrt(2/Pi))");
		check("Integrate(Sin(a^4)*a,{a,0,2.0})", "0.37357");
	}

	public void testIssue83() {
		// IAST ast = Simp(F.SinIntegral(a),a);
		// check(ast, "SinIntegral[a]");
		check("Integrate(Sin(a) / a,a)", //
				"SinIntegral(a)");

		check("test(Sin((e_.) + (f_.)*(x_))/((c_.) + (d_.)*(x_)), x_Symbol) := SinIntegral(e + f*x)/d " //
				+ "/; FreeQ({c, d, e, f}, x) && (d*e - c*f== 0)", //
				"");
		// check("Definition(test)", //
		// "");
		check("test(Sin(a)/a, a)", //
				"SinIntegral(a)");
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
		check("Integrate(1/(x-1), x)", //
				"Log(1-x)");
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
				"-5*x+x^(3/2)/3-4/3*Log(1-3*x)");
		check("Simplify(D(-5*x+x^(3/2)/3-4/3*Log(1/3-x),x))", //
				"-5+4/(1-3*x)+Sqrt(x)/2");
		check("PowerExpand(ln(3*(1/3-x)))", //
				"Log(3)+Log(1/3-x)");
	}

	public void testGithub22() {
		check("Int(Sin(x)^3*Cos(x),x)", //
				"Sin(x)^4/4");
		check("D(Sin(x)^4/4,x)", //
				"Cos(x)*Sin(x)^3");

		// unfortunately Simplify is not sophisticated enough to show this:
		// check("Simplify(Sin(2*x)/4-Sin(4*x)/8)", //
		// "Sin(x)^3*Cos(x)");

		check("Expand(TrigToExp(Sin(x)^3*Cos(x)))", //
				"(-I*1/16)/E^(I*4*x)+(I*1/8)/E^(I*2*x)-I*1/8*E^(I*2*x)+I*1/16*E^(I*4*x)");
		check("Expand(TrigToExp(Sin(2*x)/4-Sin(4*x)/8))", //
				"(-I*1/16)/E^(I*4*x)+(I*1/8)/E^(I*2*x)-I*1/8*E^(I*2*x)+I*1/16*E^(I*4*x)");
	}

	// {(a+b*x)^m/(c+d*x), x, 1, ((a+b*x)^(1+m)*Hypergeometric2F1(1, 1+m, 2+m, -((d*(a+b*x))/(b*c -
	// a*d))))/((b*c-a*d)*(1+m))}
	public void test00480() {
		check("Integrate((a+b*x)^m/(c+d*x), x)", //
				"((a+b*x)^(1+m)*Hypergeometric2F1(1,1+m,2+m,(-d*(a+b*x))/(b*c-a*d)))/((b*c-a*d)*(\n" + "1+m))");

	}

	// {(a+b*x)^m/(c+d*x)^2, x, 1, (b*(a+b*x)^(1+m)*Hypergeometric2F1(2, 1+m, 2+m, -((d*(a+b*x))/(b*c -
	// a*d))))/((b*c-a*d)^2*(1+m))}
	public void test00481() {
		check("Integrate((a+b*x)^m/(c+d*x)^2, x)", //
				"(b*(a+b*x)^(1+m)*Hypergeometric2F1(2,1+m,2+m,(-d*(a+b*x))/(b*c-a*d)))/((b*c-a*d)^\n" + "2*(1+m))");

	}

	// {Sqrt(d+e*x)/((f+g*x)^(3/2)*Sqrt(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)), x, 1, (2*Sqrt(a*d*e+(c*d^2 +
	// a*e^2)*x+c*d*e*x^2))/((c*d*f-a*e*g)*Sqrt(d+e*x)*Sqrt(f+g*x))}
	public void test01400() {
		// TODO sometimes works sometimes don't - seems to be a JAS random seed problem???
		// check("Integrate(Sqrt(d+e*x)/((f+g*x)^(3/2)*Sqrt(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)), x)", //
		// "(2*Sqrt(a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2))/((c*d*f-a*e*g)*Sqrt(d+e*x)*Sqrt(f+g*x))");
	}
}
