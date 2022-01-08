package org.matheclipse.parser.test;

import java.util.List;
import org.matheclipse.parser.client.ParserConfig;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.ast.ASTNode;
import junit.framework.TestCase;

/** Tests parser function for SimpleParserFactory */
public class ParserTestCase extends TestCase {

  public ParserTestCase(String name) {
    super(name);
  }

  public void testParser() {
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("-a-b*c!!+d");
      assertEquals(
          obj.toString(), "Plus(Plus(Times(-1, a), Times(-1, Times(b, Factorial2(c)))), d)");
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
      if (!ParserConfig.EXPLICIT_TIMES_OPERATOR) {
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
      assertEquals(
          "Syntax error in line: 1 - Operator: := is no prefix operator.\n"
              + "a+%%%+%3*:=4!\n"
              + "          ^",
          e.getMessage());
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
      ASTNode obj =
          p.parse(
              "Integrate[Sin[a_.*x_]^n_IntegerQ, x_Symbol]:= -Sin[a*x]^(n-1)*Cos[a*x]/(n*a)+(n-1)/n*Integrate[Sin[a*x]^(n-2),x]/;Positive[n]&&FreeQ[a,x]");
      assertEquals(
          obj.toString(),
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

  public void testParser21a() {
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("B[[ ;;1;;-1]]");
      assertEquals(obj.toString(), "Part(B, Span(1, 1, -1))");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser21b() {
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("B[[3;;1;;-1]]");
      assertEquals(obj.toString(), "Part(B, Span(3, 1, -1))");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser21c() {
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("B[[;;;;-1]]");
      assertEquals(obj.toString(), "Part(B, Span(1, All, -1))");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParserFunction() {
    try {
      Parser p = new Parser(true);
      ASTNode obj = p.parse("#^2-3#-1&");
      assertEquals(
          obj.toString(),
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
      assertEquals(
          obj.toString(), "Equal(Derivative(1)[x][t], Times(10, Plus(y(t), Times(-1, x(t)))))");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser25() {
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("Int[f_'[x_]*g_[x_] + f_[x_]*g_'[x_],x_Symbol]");
      assertEquals(
          obj.toString(),
          "Int(Plus(Times(Derivative(1)[f_][x_], g_(x_)), Times(f_(x_), Derivative(1)[g_][x_])), x_Symbol)");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser26() {
    try {
      Parser p = new Parser(false, true);
      List<ASTNode> obj =
          p.parsePackage(
              "	SumSimplerAuxQ[u_,v_] :=\n"
                  + "			  v=!=0 && \n"
                  + "			  NonnumericFactors[u]===NonnumericFactors[v] &&\n"
                  + "			  (NumericFactor[u]/NumericFactor[v]<-1/2 || NumericFactor[u]/NumericFactor[v]==-1/2 && NumericFactor[u]<0)\n"
                  + "\n"
                  + "");
      assertEquals(
          obj.toString(),
          "[SetDelayed(SumSimplerAuxQ(u_, v_), And(UnsameQ(v, 0), SameQ(NonnumericFactors(u), NonnumericFactors(v)), Or(Less(Times(NumericFactor(u), Power(NumericFactor(v), -1)), -1/2), And(Equal(Times(NumericFactor(u), Power(NumericFactor(v), -1)), -1/2), Less(NumericFactor(u), 0)))))]");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser27() {
    try {
      Parser p = new Parser(false, true);
      List<ASTNode> obj =
          p.parsePackage(
              "\n"
                  + "TryTanhSubst[u_,x_Symbol] :=\n"
                  + "  FalseQ[FunctionOfLinear[u,x]] &&\n"
                  + "  Not[MatchQ[u,r_.*(s_+t_)^n_. /; IntegerQ[n] && n>0]] &&\n"
                  + "(*Not[MatchQ[u,Log[f_[x]^2] /; SinhCoshQ[f]]]  && *)\n"
                  + "  Not[MatchQ[u,Log[v_]]]  &&\n"
                  + "  Not[MatchQ[u,1/(a_+b_.*f_[x]^n_) /; SinhCoshQ[f] && IntegerQ[n] && n>2]] &&\n"
                  + "  Not[MatchQ[u,f_[m_.*x]*g_[n_.*x] /; IntegersQ[m,n] && SinhCoshQ[f] && SinhCoshQ[g]]] &&\n"
                  + "  Not[MatchQ[u,r_.*(a_.*s_^m_)^p_ /; FreeQ[{a,m,p},x] && Not[m===2 && (s===Sech[x] || s===Csch[x])]]] &&\n"
                  + "  u===ExpandIntegrand[u,x]\n"
                  + "\n"
                  + "TryPureTanhSubst[u_,x_Symbol] :=\n"
                  + "  Not[MatchQ[u,Log[v_]]]  &&\n"
                  + "  Not[MatchQ[u,ArcTanh[a_.*Tanh[v_]] /; FreeQ[a,x]]] &&\n"
                  + "  Not[MatchQ[u,ArcTanh[a_.*Coth[v_]] /; FreeQ[a,x]]] &&\n"
                  + "  Not[MatchQ[u,ArcCoth[a_.*Tanh[v_]] /; FreeQ[a,x]]] &&\n"
                  + "  Not[MatchQ[u,ArcCoth[a_.*Coth[v_]] /; FreeQ[a,x]]] &&\n"
                  + "  u===ExpandIntegrand[u,x]\n"
                  + "\n"
                  + "");
      assertEquals(
          obj.toString(),
          "[SetDelayed(TryTanhSubst(u_, x_Symbol), And(FalseQ(FunctionOfLinear(u, x)), Not(MatchQ(u, Condition(Times(r_., Power(Plus(s_, t_), n_.)), And(IntegerQ(n), Greater(n, 0))))), Not(MatchQ(u, Log(v_))), Not(MatchQ(u, Condition(Power(Plus(a_, Times(b_., Power(f_(x), n_))), -1), And(SinhCoshQ(f), IntegerQ(n), Greater(n, 2))))), Not(MatchQ(u, Condition(Times(f_(Times(m_., x)), g_(Times(n_., x))), And(IntegersQ(m, n), SinhCoshQ(f), SinhCoshQ(g))))), Not(MatchQ(u, Condition(Times(r_., Power(Times(a_., Power(s_, m_)), p_)), And(FreeQ(List(a, m, p), x), Not(And(SameQ(m, 2), Or(SameQ(s, Sech(x)), SameQ(s, Csch(x))))))))), SameQ(u, ExpandIntegrand(u, x)))), SetDelayed(TryPureTanhSubst(u_, x_Symbol), And(Not(MatchQ(u, Log(v_))), Not(MatchQ(u, Condition(ArcTanh(Times(a_., Tanh(v_))), FreeQ(a, x)))), Not(MatchQ(u, Condition(ArcTanh(Times(a_., Coth(v_))), FreeQ(a, x)))), Not(MatchQ(u, Condition(ArcCoth(Times(a_., Tanh(v_))), FreeQ(a, x)))), Not(MatchQ(u, Condition(ArcCoth(Times(a_., Coth(v_))), FreeQ(a, x)))), SameQ(u, ExpandIntegrand(u, x))))]");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser28() {
    try {
      Parser p = new Parser(false, true);
      List<ASTNode> list =
          p.parsePackage(
              "\n"
                  + "TrigSimplifyAux[u_] := u\n"
                  + "\n"
                  + "\n"
                  + "(* ::Section::Closed:: *)\n"
                  + "(*Factoring functions*)\n"
                  + "\n"
                  + "\n"
                  + "(* ::Subsection::Closed:: *)\n"
                  + "(*ContentFactor*)\n"
                  + "\n"
                  + "\n"
                  + "(* ContentFactor[expn] returns expn with the content of sum factors factored out. *)\n"
                  + "(* Basis: a*b+a*c == a*(b+c) *)\n"
                  + "ContentFactor[expn_] :=\n"
                  + "  TimeConstrained[ContentFactorAux[expn],2.0,expn];\n"
                  + "\n"
                  + "ContentFactorAux[expn_] :=\n"
                  + "  If[AtomQ[expn],\n"
                  + "    expn,\n"
                  + "  If[IntegerPowerQ[expn],\n"
                  + "    If[SumQ[expn[[1]]] && NumericFactor[expn[[1,1]]]<0,\n"
                  + "      (-1)^expn[[2]] * ContentFactorAux[-expn[[1]]]^expn[[2]],\n"
                  + "    ContentFactorAux[expn[[1]]]^expn[[2]]],\n"
                  + "  If[ProductQ[expn],\n"
                  + "    Module[{num=1,tmp},\n"
                  + "    tmp=Map[Function[If[SumQ[#] && NumericFactor[#[[1]]]<0, num=-num; ContentFactorAux[-#], ContentFactorAux[#]]], expn];\n"
                  + "    num*UnifyNegativeBaseFactors[tmp]],\n"
                  + "  If[SumQ[expn],\n"
                  + "    With[{lst=CommonFactors[Apply[List,expn]]},\n"
                  + "    If[lst[[1]]===1 || lst[[1]]===-1,\n"
                  + "      expn,\n"
                  + "    lst[[1]]*Apply[Plus,Rest[lst]]]],\n"
                  + "  expn]]]]\n"
                  + "\n"
                  + "");
      assertEquals(list.size(), 3);
      assertEquals(
          list.get(1).toString(),
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

  public void testParser33() {
    try {
      Parser p = new Parser();
      ASTNode obj =
          p.parse(
              "Int[(u_)*(y_)^(m_.), x_Symbol] := With[{q = DerivativeDivides[y, u, x]}, Simp[(q*y^(m + 1))/(m + 1), x] /;  !FalseQ[q]] /; FreeQ[m, x] && NeQ[m, -1]");
      assertEquals(
          obj.toString(),
          "SetDelayed(Int(Times(u_, Power(y_, m_.)), x_Symbol), Condition(With(List(Set(q, DerivativeDivides(y, u, x))), Condition(Simp(Times(Times(q, Power(y, Plus(m, 1))), Power(Plus(m, 1), -1)), x), Not(FalseQ(q)))), And(FreeQ(m, x), NeQ(m, -1))))");
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
      ASTNode obj =
          p.parse(
              "a = {1}; Do[ If[ a[ [ -1 ] ] - n > 0 && Position[ a, a[ [ -1 ] ] - n ] == {}, a = Append[ a, a[ [ -1 ] ] - n ], a = Append[ a, a[ [ -1 ] ] + n ] ], {n, 2, 70} ]; a");
      assertEquals(
          obj.toString(),
          "CompoundExpression(Set(a, List(1)), Do(If(And(Greater(Plus(Part(a, -1), Times(-1, n)), 0), Equal(Position(a, Plus(Part(a, -1), Times(-1, n))), List())), Set(a, Append(a, Plus(Part(a, -1), Times(-1, n)))), Set(a, Append(a, Plus(Part(a, -1), n)))), List(n, 2, 70)), a)");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParse35() {
    try {
      Parser p = new Parser();
      // http://oeis.org/A005132
      ASTNode obj =
          p.parse(
              "	If[!MatchQ[#,_\\[Rule]_],\n"
                  + "       Message[Caller::\"UnknownOption\",#];\n"
                  + "       (*else*),\n"
                  + "       pos=Position[FullOptions,{#[[1]],_,_}];\n"
                  + "       If[Length[pos]\\[Equal]0,\n"
                  + "         Message[Caller::\"UnknownOption\",#]\n"
                  + "         (*else*),\n"
                  + "         FullOptions[[pos[[1,1]],3]]=#[[2]]\n"
                  + "         ];\n"
                  + "       ];");
      assertEquals(
          obj.toString(),
          "CompoundExpression(If(Not(MatchQ(Slot(1), Rule(_, _))), CompoundExpression(Message(MessageName(Caller, UnknownOption), Slot(1)), Null), CompoundExpression(Set(pos, Position(FullOptions, List(Part(Slot(1), 1), _, _))), If(Equal(Length(pos), 0), Message(MessageName(Caller, UnknownOption), Slot(1)), Set(Part(FullOptions, Part(pos, 1, 1), 3), Part(Slot(1), 2))), Null)), Null)");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParse36() {
    try {
      // System.out.println(Character.isUnicodeIdentifierPart('\u221E'));
      Parser p = new Parser();
      ASTNode obj = p.parse("\u221E");
      assertEquals(obj.toString(), "Infinity");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParse37() {
    try {
      Parser p = new Parser();
      ASTNode obj =
          p.parse(
              "      Do[\n"
                  + "        serh=SeriesHead[ser,\\[Omega]];\n"
                  + "        \n"
                  + "        (* check for series term that run out of precision *)\n"
                  + "        If[FreeQ[serh,HoldPattern[SeriesData[_,_,{},_,_,_]]],\n"
                  + "          (* No: done. *)\n"
                  + "          Break[];\n"
                  + "          ];\n"
                  + "        ,{i,1,30}\n"
                  + "        ]");
      assertEquals(
          obj.toString(),
          "Do(CompoundExpression(Set(serh, SeriesHead(ser, ω)), If(FreeQ(serh, HoldPattern(SeriesData(_, _, List(), _, _, _))), CompoundExpression(Break(), Null)), Null), List(i, 1, 30))");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParse38() {
    try {
      Parser p = new Parser();
      ASTNode obj =
          p.parse(
              "MakeGraph[Range[26],\n"
                  + "                            Mod[#1-#2, 26] == 1 ||\n"
                  + "                                (-1)^#1Mod[#1-#2, 26] == 11 ||\n"
                  + "                                (-1)^#1Mod[#1-#2, 26] == 7&,\n"
                  + "                            Type -> Directed]");
      assertEquals(
          obj.toString(),
          "MakeGraph(Range(26), Function(Or(Equal(Mod(Plus(Slot(1), Times(-1, Slot(2))), 26), 1), Equal(Times(Power(-1, Slot(1)), Mod(Plus(Slot(1), Times(-1, Slot(2))), 26)), 11), Equal(Times(Power(-1, Slot(1)), Mod(Plus(Slot(1), Times(-1, Slot(2))), 26)), 7))), Rule(Type, Directed))");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParse39() {
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("While[i\\[LessEqual]Length[asympt] && asympt[[i,2]]\\[Equal]0,i++]");
      assertEquals(
          obj.toString(),
          "While(And(LessEqual(i, Length(asympt)), Equal(Part(asympt, i, 2), 0)), Increment(i))");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParse40() {
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("ff/: Power[ff, i_Integer] = {i}");
      assertEquals(obj.toString(), "TagSet(ff, Power(ff, i_Integer), List(i))");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParse41() {
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("ff/: Power[ff, i_Integer] := {i}");
      assertEquals(obj.toString(), "TagSetDelayed(ff, Power(ff, i_Integer), List(i))");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParse42() {
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("m_.k_+b_.");
      assertEquals(obj.toString(), "Plus(Times(m_., k_), b_.)");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParse43() {
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("MakeAssocList[u_,x_Symbol,alst_List:{}] ");
      assertEquals(obj.toString(), "MakeAssocList(u_, x_Symbol, Optional(alst_List, List()))");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser44() {
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("-1<0<=a<4<5<b<10<11");
      assertEquals(
          obj.toString(),
          "Inequality(-1, Less, 0, LessEqual, a, Less, 4, Less, 5, Less, b, Less, 10, Less, 11)");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser45() {
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("\"array\"[x] ");
      assertEquals(
          obj.toString(), //
          "array(x)");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser46() {
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("{a,b,c}[x] ");
      assertEquals(
          obj.toString(), //
          "List(a, b, c)[x]");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser47() {
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("123456789012345678901234567890");
      assertEquals(
          obj.toString(), //
          "123456789012345678901234567890");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser48a() {
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("FullForm[Hold[;;; ]]");
      assertEquals(
          obj.toString(), //
          "FullForm(Hold(CompoundExpression(Span(1, All), Null)))");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser48b() {
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("FullForm[Hold[abc;;; ]]");
      assertEquals(
          obj.toString(), //
          "FullForm(Hold(CompoundExpression(Span(abc, All), Null)))");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser48c() {
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("FullForm[Hold[abc;;;d ]]");
      assertEquals(
          obj.toString(), //
          "FullForm(Hold(CompoundExpression(Span(abc, All), d)))");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser49() {
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("FullForm[Hold[abc;;;;; ]]");
      assertEquals(
          obj.toString(), //
          "FullForm(Hold(CompoundExpression(Times(Span(abc, All), Span(1, All)), Null)))");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser50() {
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("FullForm[Hold[{a, b, c, d, e, f, g, h, i, j, k}[[3 ;; -3 ;; 2]]]]");
      assertEquals(
          obj.toString(), //
          "FullForm(Hold(Part(List(a, b, c, d, e, f, g, h, i, j, k), Span(3, -3, 2))))");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser51() {
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("FullForm[Hold[UT[n, ttt\\[Lambda]abc0]]]");
      assertEquals(
          obj.toString(), //
          "FullForm(Hold(UT(n, tttλabc0)))");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser52() {
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("FullForm[Hold[{1, 2, 3, 4, 5}[[;; ;; -1]]]]");
      assertEquals(
          obj.toString(), //
          "FullForm(Hold(Part(List(1, 2, 3, 4, 5), Span(1, All, -1))))");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser53() {
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("opts : OptionsPattern[]");
      assertEquals(
          obj.toString(), //
          "Pattern(opts, OptionsPattern())");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser54() {
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("f[x_, opts : OptionsPattern[]]");
      assertEquals(
          obj.toString(), //
          "f(x_, Pattern(opts, OptionsPattern()))");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser55() {
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("a:b:c:d");
      assertEquals(
          obj.toString(), //
          "Optional(Pattern(a, b), Pattern(c, d))");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser56() {
    // see https://github.com/halirutan/Wolfram-Language-IntelliJ-Plugin-Archive/issues/166
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("x y : z");
      assertEquals(
          obj.toString(), //
          "Times(x, Pattern(y, z))");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser57() {
    // see https://github.com/halirutan/Wolfram-Language-IntelliJ-Plugin-Archive/issues/166
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("x y_ : z");
      assertEquals(
          obj.toString(), //
          "Times(x, Optional(y_, z))");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser58() {
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("StringContainsQ[\"bcde\", \"c\" ~~ __ ~~ \"t\"]");
      assertEquals(
          obj.toString(), //
          "StringContainsQ(bcde, StringExpression(c, __, t))");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser59() {
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("f[t_] = Simplify[ r'[t] / Norm[ r'[t] ], t ∈ Reals];");
      assertEquals(
          obj.toString(), //
          "CompoundExpression(Set(f(t_), Simplify(Times(Derivative(1)[r][t], Power(Norm(Derivative(1)[r][t]), -1)), Element(t, Reals))), Null)");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser60() {
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("x[ [ ] ]");
      assertEquals(
          obj.toString(), //
          "Part(x)");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser61() {
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("(a+i*b^2+k*c^3+d)");
      assertEquals(
          obj.toString(), //
          "Plus(a, Times(i, Power(b, 2)), Times(k, Power(c, 3)), d)");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser62() {
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("\\[Theta] = 4");
      assertEquals(
          obj.toString(), //
          "Set(θ, 4)");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser63() {
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("\\[DifferentialD] = 4");
      fail();
    } catch (Exception e) {
      String s = System.getProperty("os.name");
      if (s.contains("Windows")) {
        e.printStackTrace();
        assertEquals(
            e.getMessage(),
            "Syntax error in line: 1 - unexpected (named unicode) character: '\\[DifferentialD]'\n"
                + " = 4\n"
                + "^");
      }
    }
  }

  public void testParser64() {
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("test = \" \\[Theta] is using {\\\"1\\\", \\\"2\\\"} instead.\"");
      assertEquals(
          obj.toString(), //
          "Set(test,  θ is using {\"1\", \"2\"} instead.)");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser65() {
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("2.2250738585072014`*^-308");
      assertEquals(
          obj.toString(), //
          "2.2250738585072014E-308");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser66() {
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("a~b~c");
      assertEquals(
          obj.toString(), //
          "b(a, c)");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser67() {
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("a~b~c~d~e");
      assertEquals(
          obj.toString(), //
          "d(b(a, c), e)");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser68() {
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("8 + 1*^-28");
      assertEquals(
          obj.toString(), //
          "Plus(8, Power(10000000000000000000000000000, -1))");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser69() {
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("2/3 + Pi + 5.5`30");
      assertEquals(
          obj.toString(), //
          "Plus(2/3, Pi, 5.5)");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser70() {
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("Plot3D[Sin[x*y],{x,-1.5`,1.5`},{y,-1.5`,1.5`}]");
      assertEquals(
          obj.toString(), //
          "Plot3D(Sin(Times(x, y)), List(x, -1.5, 1.5), List(y, -1.5, 1.5))");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser71() {
    try {
      Parser p = new Parser();
      ASTNode obj =
          p.parse(
              "  \n"
                  + "a = f[Rule, {#, RandomReal[1, Length[#]]}]&[\n"
                  + "Flatten[constants[[All,2]]]"
                  + "\n]");
      assertEquals(
          obj.toString(), //
          "Set(a, Function(f(Rule, List(Slot(1), RandomReal(1, Length(Slot(1))))))[Flatten(Part(constants, All, 2))])");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser72() {
    try {
      Parser p = new Parser();
      ASTNode obj =
          p.parse(
              "  \n"
                  + "	constantRules = f[Rule, {#, RandomReal[1, Length[#]]}]&[\n"
                  + "		Flatten[constants[[All,2]]]\n"
                  + "	]");
      assertEquals(
          obj.toString(), //
          "Set(constantRules, Function(f(Rule, List(Slot(1), RandomReal(1, Length(Slot(1))))))[Flatten(Part(constants, All, 2))])");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser73() {
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("y:r_^n_");
      // Pattern[y,Power[Pattern[r,Blank[]],Pattern[n,Blank[]]]]
      assertEquals(
          obj.toString(), //
          "Pattern(y, Power(r_, n_))");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser74() {
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("Cases[e, y:(a_. x + b_.)^n_ /; FreeQ[{a,b}, x] ]");
      // Cases[e,Condition[Pattern[y,Power[Plus[Times[Optional[Pattern[a,Blank[]]],x],Optional[Pattern[b,Blank[]]]],Pattern[n,Blank[]]]],FreeQ[List[a,b],x]]]
      assertEquals(
          obj.toString(), //
          "Cases(e, Condition(Pattern(y, Power(Plus(Times(a_., x), b_.), n_)), FreeQ(List(a, b), x)))");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser75() {
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("Cases[e, t:{__Integer} :> t^2]");
      assertEquals(
          obj.toString(), //
          "Cases(e, RuleDelayed(Pattern(t, List(__Integer)), Power(t, 2)))");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  // Hold[Cases[e, y:(a_. x + b_.)^n_ /;  FreeQ[{a,b}, x] && Head[n] == Rational :> {y, a x + b,
  // Numerator[n], Denominator[n], a, b}, {0, Infinity}]]//FullForm
  public void testParser76() {
    try {
      Parser p = new Parser();
      ASTNode obj =
          p.parse(
              "Cases[e, y:(a_. x + b_.)^n_ /;  FreeQ[{a,b}, x] && Head[n] == Rational :> {y, a x + b, Numerator[n], Denominator[n], a, b}, {0, Infinity}]");
      assertEquals(
          obj.toString(), //
          "Cases(e, RuleDelayed(Condition(Pattern(y, Power(Plus(Times(a_., x), b_.), n_)), And(FreeQ(List(a, b), x), Equal(Head(n), Rational))), List(y, Plus(Times(a, x), b), Numerator(n), Denominator(n), a, b)), List(0, Infinity))");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser77() {
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("a_+b:Power[c_,_Rational]");
      assertEquals(
          obj.toString(), //
          "Plus(a_, Pattern(b, Power(c_, _Rational)))");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }

  public void testParser78() {
    try {
      Parser p = new Parser();
      ASTNode obj = p.parse("WordBoundary ~~ x:DigitCharacter.. ~~ WordBoundary");
      assertEquals(
          obj.toString(), //
          "StringExpression(WordBoundary, Pattern(x, Repeated(DigitCharacter)), WordBoundary)");
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", e.getMessage());
    }
  }
}
