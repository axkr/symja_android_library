package org.matheclipse.parser.test;

import junit.framework.TestCase;

import java.util.List;

import org.matheclipse.core.basic.Config;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.ast.ASTNode;

/**
 * Tests parser function for SimpleParserFactory
 */
public class ParserTestCase extends TestCase {

	public ParserTestCase(String name) {
		super(name);
	}

	public void testParser() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("-a-b*c!!+d");
			assertEquals(obj.toString(), "Plus(Plus(Times(-1, a), Times(-1, Times(b, Factorial2(c)))), d)");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser0() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("(#^3)&[x][y,z].{a,b,c}");
			assertEquals(obj.toString(), "Dot(Function(Power(Slot(1), 3))[x][y, z], List(a, b, c))");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser1() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("Integrate[Sin[x]^2+3*x^4, x]");
			assertEquals(obj.toString(), "Integrate(Plus(Power(Sin(x), 2), Times(3, Power(x, 4))), x)");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser2() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("a[][0][1]f[[x]]");
			assertEquals(obj.toString(), "Times(a()[0][1], Part(f, x))");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser3() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("f[y,z](a+b+c)");
			assertEquals(obj.toString(), "Times(f(y, z), Plus(a, b, c))");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser4() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("$a=2");
			assertEquals(obj.toString(), "Set($a, 2)");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser5() {
		try {
			Parser p = new Parser();
			if (!Config.EXPLICIT_TIMES_OPERATOR) {
				ASTNode obj = p.parse("4.7942553860420304E-1");
				assertEquals(obj.toString(), "Plus(Times(4.7942553860420304, E), Times(-1, 1))");

				obj = p.parse("4.7942553860420304 * E - 1");
				assertEquals(obj.toString(), "Plus(Times(4.7942553860420304, E), Times(-1, 1))");
			} else {
				ASTNode obj = p.parse("4.7942553860420304E-1");
				assertEquals(obj.toString(), "4.7942553860420304E-1");
			}
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser6() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("a+%%%+%3*4!");
			assertEquals(obj.toString(), "Plus(a, Out(-3), Times(Out(3), Factorial(4)))");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser7() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("a+%%%+%3*:=4!");
			fail("A SyntaxError exception should occur here");
		} catch (Exception e) {
			assertEquals("Syntax error in line: 1 - Operator: := is no prefix operator.\n" + "a+%%%+%3*:=4!\n"
					+ "          ^", e.getMessage());
		}
	}

	public void testParser8() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("-42424242424242424242");
			assertEquals(obj.toString(), "-42424242424242424242");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser9() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("-42424242424242424242.125");
			assertEquals(obj.toString(), "-42424242424242424242.125");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser10() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("-3/4");
			assertEquals(obj.toString(), "-3/4");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser11() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("-(3/4)");
			assertEquals(obj.toString(), "-3/4");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser12() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("-(Pi/4)");
			assertEquals(obj.toString(), "Times(-1, Times(1/4, Pi))");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser13() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("a*b*c*d");
			assertEquals(obj.toString(), "Times(a, b, c, d)");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser14() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("-a-b*c!!+d");
			assertEquals(obj.dependsOn("d"), true);
			assertEquals(obj.dependsOn("x"), false);
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser15() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse(
					"Integrate[Sin[a_.*x_]^n_IntegerQ, x_Symbol]:= -Sin[a*x]^(n-1)*Cos[a*x]/(n*a)+(n-1)/n*Integrate[Sin[a*x]^(n-2),x]/;Positive[n]&&FreeQ[a,x]");
			assertEquals(obj.toString(),
					"SetDelayed(Integrate(Power(Sin(Times(a_., x_)), n_IntegerQ), x_Symbol), Condition(Plus(Times(Times(-1, Power(Sin(Times(a, x)), Plus(n, Times(-1, 1)))), Times(Cos(Times(a, x)), Power(Times(n, a), -1))), Times(Times(Plus(n, Times(-1, 1)), Power(n, -1)), Integrate(Power(Sin(Times(a, x)), Plus(n, Times(-1, 2))), x))), And(Positive(n), FreeQ(a, x))))");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser16() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("f[[1,2]]");
			assertEquals(obj.toString(), "Part(f, 1, 2)");
			obj = p.parse("f[[1]][[2]]");
			assertEquals(obj.toString(), "Part(Part(f, 1), 2)");
			obj = p.parse("f[[1,2,f[x]]]");
			assertEquals(obj.toString(), "Part(f, 1, 2, f(x))");
			obj = p.parse("f[[1]][[2]][[f[x]]]");
			assertEquals(obj.toString(), "Part(Part(Part(f, 1), 2), f(x))");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser17() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("\\[Alpha]+\\[Alpha]*f[\\[CapitalSHacek]]");
			assertEquals(obj.toString(), "Plus(α, Times(α, f(Š)))");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser18() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("(a+b)[x]");
			assertEquals(obj.toString(), "Plus(a, b)[x]");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser19() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("B[[;; 2]]");
			assertEquals(obj.toString(), "Part(B, Span(1, 2))");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser20() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("B[[;;, 2]]");
			assertEquals(obj.toString(), "Part(B, Span(1, All), 2)");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser21() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("B[[3;;1;;-1]]");
			assertEquals(obj.toString(), "Part(B, Span(3, 1, -1))");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParserFunction() {
		try {
			Parser p = new Parser(true);
			ASTNode obj = p.parse("#^2-3#-1&");
			assertEquals(obj.toString(),
					"Function(Plus(Plus(Power(Slot(1), 2), Times(-1, Times(3, Slot(1)))), Times(-1, 1)))");
		} catch (RuntimeException e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser22() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("a+b;");
			assertEquals(obj.toString(), "CompoundExpression(Plus(a, b), Null)");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser23() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("f[x]*f''[x]");
			assertEquals(obj.toString(), "Times(f(x), Derivative(2)[f][x])");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser24() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("x'[t] == 10*(y[t] - x[t])");
			assertEquals(obj.toString(), "Equal(Derivative(1)[x][t], Times(10, Plus(y(t), Times(-1, x(t)))))");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser25() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("Int[f_'[x_]*g_[x_] + f_[x_]*g_'[x_],x_Symbol]");
			assertEquals(obj.toString(),
					"Int(Plus(Times(Derivative(1)[f_][x_], g_(x_)), Times(f_(x_), Derivative(1)[g_][x_])), x_Symbol)");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser26() {
		try {
			Parser p = new Parser(false, true);
			List<ASTNode> obj = p.parsePackage("	SumSimplerAuxQ[u_,v_] :=\n" + "			  v=!=0 && \n"
					+ "			  NonnumericFactors[u]===NonnumericFactors[v] &&\n"
					+ "			  (NumericFactor[u]/NumericFactor[v]<-1/2 || NumericFactor[u]/NumericFactor[v]==-1/2 && NumericFactor[u]<0)\n"
					+ "\n" + "");
			assertEquals(obj.toString(),
					"[SetDelayed(SumSimplerAuxQ(u_, v_), And(UnsameQ(v, 0), SameQ(NonnumericFactors(u), NonnumericFactors(v)), Or(Less(Times(NumericFactor(u), Power(NumericFactor(v), -1)), -1/2), And(Equal(Times(NumericFactor(u), Power(NumericFactor(v), -1)), -1/2), Less(NumericFactor(u), 0)))))]");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser27() {
		try {
			Parser p = new Parser(false, true);
			List<ASTNode> obj = p.parsePackage("\n" + "TryTanhSubst[u_,x_Symbol] :=\n"
					+ "  FalseQ[FunctionOfLinear[u,x]] &&\n"
					+ "  Not[MatchQ[u,r_.*(s_+t_)^n_. /; IntegerQ[n] && n>0]] &&\n"
					+ "(*Not[MatchQ[u,Log[f_[x]^2] /; SinhCoshQ[f]]]  && *)\n" + "  Not[MatchQ[u,Log[v_]]]  &&\n"
					+ "  Not[MatchQ[u,1/(a_+b_.*f_[x]^n_) /; SinhCoshQ[f] && IntegerQ[n] && n>2]] &&\n"
					+ "  Not[MatchQ[u,f_[m_.*x]*g_[n_.*x] /; IntegersQ[m,n] && SinhCoshQ[f] && SinhCoshQ[g]]] &&\n"
					+ "  Not[MatchQ[u,r_.*(a_.*s_^m_)^p_ /; FreeQ[{a,m,p},x] && Not[m===2 && (s===Sech[x] || s===Csch[x])]]] &&\n"
					+ "  u===ExpandIntegrand[u,x]\n" + "\n" + "TryPureTanhSubst[u_,x_Symbol] :=\n"
					+ "  Not[MatchQ[u,Log[v_]]]  &&\n" + "  Not[MatchQ[u,ArcTanh[a_.*Tanh[v_]] /; FreeQ[a,x]]] &&\n"
					+ "  Not[MatchQ[u,ArcTanh[a_.*Coth[v_]] /; FreeQ[a,x]]] &&\n"
					+ "  Not[MatchQ[u,ArcCoth[a_.*Tanh[v_]] /; FreeQ[a,x]]] &&\n"
					+ "  Not[MatchQ[u,ArcCoth[a_.*Coth[v_]] /; FreeQ[a,x]]] &&\n" + "  u===ExpandIntegrand[u,x]\n"
					+ "\n" + "");
			assertEquals(obj.toString(),
					"[SetDelayed(TryTanhSubst(u_, x_Symbol), And(FalseQ(FunctionOfLinear(u, x)), Not(MatchQ(u, Condition(Times(r_., Power(Plus(s_, t_), n_.)), And(IntegerQ(n), Greater(n, 0))))), Not(MatchQ(u, Log(v_))), Not(MatchQ(u, Condition(Power(Plus(a_, Times(b_., Power(f_(x), n_))), -1), And(SinhCoshQ(f), IntegerQ(n), Greater(n, 2))))), Not(MatchQ(u, Condition(Times(f_(Times(m_., x)), g_(Times(n_., x))), And(IntegersQ(m, n), SinhCoshQ(f), SinhCoshQ(g))))), Not(MatchQ(u, Condition(Times(r_., Power(Times(a_., Power(s_, m_)), p_)), And(FreeQ(List(a, m, p), x), Not(And(SameQ(m, 2), Or(SameQ(s, Sech(x)), SameQ(s, Csch(x))))))))), SameQ(u, ExpandIntegrand(u, x)))), SetDelayed(TryPureTanhSubst(u_, x_Symbol), And(Not(MatchQ(u, Log(v_))), Not(MatchQ(u, Condition(ArcTanh(Times(a_., Tanh(v_))), FreeQ(a, x)))), Not(MatchQ(u, Condition(ArcTanh(Times(a_., Coth(v_))), FreeQ(a, x)))), Not(MatchQ(u, Condition(ArcCoth(Times(a_., Tanh(v_))), FreeQ(a, x)))), Not(MatchQ(u, Condition(ArcCoth(Times(a_., Coth(v_))), FreeQ(a, x)))), SameQ(u, ExpandIntegrand(u, x))))]");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser28() {
		try {
			Parser p = new Parser(false, true);
			List<ASTNode> list = p.parsePackage("\n" + "TrigSimplifyAux[u_] := u\n" + "\n" + "\n"
					+ "(* ::Section::Closed:: *)\n" + "(*Factoring functions*)\n" + "\n" + "\n"
					+ "(* ::Subsection::Closed:: *)\n" + "(*ContentFactor*)\n" + "\n" + "\n"
					+ "(* ContentFactor[expn] returns expn with the content of sum factors factored out. *)\n"
					+ "(* Basis: a*b+a*c == a*(b+c) *)\n" + "ContentFactor[expn_] :=\n"
					+ "  TimeConstrained[ContentFactorAux[expn],2.0,expn];\n" + "\n" + "ContentFactorAux[expn_] :=\n"
					+ "  If[AtomQ[expn],\n" + "    expn,\n" + "  If[IntegerPowerQ[expn],\n"
					+ "    If[SumQ[expn[[1]]] && NumericFactor[expn[[1,1]]]<0,\n"
					+ "      (-1)^expn[[2]] * ContentFactorAux[-expn[[1]]]^expn[[2]],\n"
					+ "    ContentFactorAux[expn[[1]]]^expn[[2]]],\n" + "  If[ProductQ[expn],\n"
					+ "    Module[{num=1,tmp},\n"
					+ "    tmp=Map[Function[If[SumQ[#] && NumericFactor[#[[1]]]<0, num=-num; ContentFactorAux[-#], ContentFactorAux[#]]], expn];\n"
					+ "    num*UnifyNegativeBaseFactors[tmp]],\n" + "  If[SumQ[expn],\n"
					+ "    With[{lst=CommonFactors[Apply[List,expn]]},\n" + "    If[lst[[1]]===1 || lst[[1]]===-1,\n"
					+ "      expn,\n" + "    lst[[1]]*Apply[Plus,Rest[lst]]]],\n" + "  expn]]]]\n" + "\n" + "");
			assertEquals(list.size(), 3);
			assertEquals(list.get(1).toString(),
					"CompoundExpression(SetDelayed(ContentFactor(expn_), TimeConstrained(ContentFactorAux(expn), 2.0, expn)), Null)");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser29() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("FracPart[m_*u_,n_:1]");
			assertEquals(obj.toString(), "FracPart(Times(m_, u_), Optional(n_, 1))");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParse30() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("f[x,y,]");
			assertEquals(obj.toString(), "f(x, y, Null)");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser31() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("#1.#123");
			assertEquals(obj.toString(), "Dot(Slot(1), Slot(123))");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParser32() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("(-1)^(a) (b)");
			assertEquals(obj.toString(), "Times(Power(-1, a), b)");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParse33() {
		try {
			Parser p = new Parser();
			ASTNode obj = p.parse("f@ g@ h");
			assertEquals(obj.toString(), "f(g(h))");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testParse34() {
		try {
			Parser p = new Parser();
			// http://oeis.org/A005132
			ASTNode obj = p.parse(
					"a = {1}; Do[ If[ a[ [ -1 ] ] - n > 0 && Position[ a, a[ [ -1 ] ] - n ] == {}, a = Append[ a, a[ [ -1 ] ] - n ], a = Append[ a, a[ [ -1 ] ] + n ] ], {n, 2, 70} ]; a");
			assertEquals(obj.toString(),
					"CompoundExpression(Set(a, List(1)), Do(If(And(Greater(Plus(Part(a, -1), Times(-1, n)), 0), Equal(Position(a, Plus(Part(a, -1), Times(-1, n))), List())), Set(a, Append(a, Plus(Part(a, -1), Times(-1, n)))), Set(a, Append(a, Plus(Part(a, -1), n)))), List(n, 2, 70)), a)");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

}