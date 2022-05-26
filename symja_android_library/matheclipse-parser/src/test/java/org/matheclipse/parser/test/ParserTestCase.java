package org.matheclipse.parser.test;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.ParserConfig;
import org.matheclipse.parser.client.ast.ASTNode;

/** Tests parser function for SimpleParserFactory */
class ParserTestCase {

  @Test
  void testParser() {
    Parser p = new Parser();
    ASTNode obj = p.parse("-a-b*c!!+d");
    assertEquals("Plus(Plus(Times(-1, a), Times(-1, Times(b, Factorial2(c)))), d)", obj.toString());
  }

  @Test
  void testParser0() {
    Parser p = new Parser();
    ASTNode obj = p.parse("(#^3)&[x][y,z].{a,b,c}");
    assertEquals("Dot(Function(Power(Slot(1), 3))[x][y, z], List(a, b, c))", obj.toString());
  }

  @Test
  void testParser1() {
    Parser p = new Parser();
    ASTNode obj = p.parse("Integrate[Sin[x]^2+3*x^4, x]");
    assertEquals("Integrate(Plus(Power(Sin(x), 2), Times(3, Power(x, 4))), x)", obj.toString());
  }

  @Test
  void testParser2() {
    Parser p = new Parser();
    ASTNode obj = p.parse("a[][0][1]f[[x]]");
    assertEquals("Times(a()[0][1], Part(f, x))", obj.toString());
  }

  @Test
  void testParser3() {
    Parser p = new Parser();
    ASTNode obj = p.parse("f[y,z](a+b+c)");
    assertEquals("Times(f(y, z), Plus(a, b, c))", obj.toString());
  }

  @Test
  void testParser4() {
    Parser p = new Parser();
    ASTNode obj = p.parse("$a=2");
    assertEquals("Set($a, 2)", obj.toString());
  }

  @Test
  void testParser5() {
    Parser p = new Parser();
    if (!ParserConfig.EXPLICIT_TIMES_OPERATOR) {
      ASTNode obj = p.parse("4.7942553860420304E-1");
      assertEquals("Plus(Times(4.7942553860420304, E), Times(-1, 1))", obj.toString());

      obj = p.parse("4.7942553860420304 * E - 1");
      assertEquals("Plus(Times(4.7942553860420304, E), Times(-1, 1))", obj.toString());
    } else {
      ASTNode obj = p.parse("4.7942553860420304E-1");
      assertEquals("4.7942553860420304E-1", obj.toString());
    }
  }

  @Test
  void testParser6() {
    Parser p = new Parser();
    ASTNode obj = p.parse("a+%%%+%3*4!");
    assertEquals("Plus(a, Out(-3), Times(Out(3), Factorial(4)))", obj.toString());
  }

  @Test
  void testParser7() {
    Exception e = assertThrows(Exception.class, () -> {
      Parser p = new Parser();
      p.parse("a+%%%+%3*:=4!");
    });
    assertEquals("Syntax error in line: 1 - Operator: := is no prefix operator.\n"
        + "a+%%%+%3*:=4!\n" + "          ^", e.getMessage());
  }

  @Test
  void testParser8() {
    Parser p = new Parser();
    ASTNode obj = p.parse("-42424242424242424242");
    assertEquals("-42424242424242424242", obj.toString());
  }

  @Test
  void testParser9() {
    Parser p = new Parser();
    ASTNode obj = p.parse("-42424242424242424242.125");
    assertEquals("-42424242424242424242.125", obj.toString());
  }

  @Test
  void testParser10() {
    Parser p = new Parser();
    ASTNode obj = p.parse("-3/4");
    assertEquals("-3/4", obj.toString());
  }

  @Test
  void testParser11() {
    Parser p = new Parser();
    ASTNode obj = p.parse("-(3/4)");
    assertEquals("-3/4", obj.toString());
  }

  @Test
  void testParser12() {
    Parser p = new Parser();
    ASTNode obj = p.parse("-(Pi/4)");
    assertEquals("Times(-1, Times(1/4, Pi))", obj.toString());
  }

  @Test
  void testParser13() {
    Parser p = new Parser();
    ASTNode obj = p.parse("a*b*c*d");
    assertEquals("Times(a, b, c, d)", obj.toString());
  }

  @Test
  void testParser14() {
    Parser p = new Parser();
    ASTNode obj = p.parse("-a-b*c!!+d");
    assertTrue(obj.dependsOn("d"));
    assertFalse(obj.dependsOn("x"));
  }

  @Test
  void testParser15() {
    Parser p = new Parser();
    ASTNode obj = p.parse(
        "Integrate[Sin[a_.*x_]^n_IntegerQ, x_Symbol]:= -Sin[a*x]^(n-1)*Cos[a*x]/(n*a)+(n-1)/n*Integrate[Sin[a*x]^(n-2),x]/;Positive[n]&&FreeQ[a,x]");
    assertEquals(
        "SetDelayed(Integrate(Power(Sin(Times(a_., x_)), n_IntegerQ), x_Symbol), Condition(Plus(Times(Times(-1, Power(Sin(Times(a, x)), Plus(n, Times(-1, 1)))), Times(Cos(Times(a, x)), Power(Times(n, a), -1))), Times(Times(Plus(n, Times(-1, 1)), Power(n, -1)), Integrate(Power(Sin(Times(a, x)), Plus(n, Times(-1, 2))), x))), And(Positive(n), FreeQ(a, x))))",
        obj.toString());
  }

  @Test
  void testParser16() {
    Parser p = new Parser();
    ASTNode obj = p.parse("f[[1,2]]");
    assertEquals("Part(f, 1, 2)", obj.toString());
    obj = p.parse("f[[1]][[2]]");
    assertEquals("Part(Part(f, 1), 2)", obj.toString());
    obj = p.parse("f[[1,2,f[x]]]");
    assertEquals("Part(f, 1, 2, f(x))", obj.toString());
    obj = p.parse("f[[1]][[2]][[f[x]]]");
    assertEquals("Part(Part(Part(f, 1), 2), f(x))", obj.toString());
  }

  @Test
  void testParser17() {
    Parser p = new Parser();
    ASTNode obj = p.parse("\\[Alpha]+\\[Alpha]*f[\\[CapitalSHacek]]");
    assertEquals("Plus(α, Times(α, f(Š)))", obj.toString());
  }

  @Test
  void testParser18() {
    Parser p = new Parser();
    ASTNode obj = p.parse("(a+b)[x]");
    assertEquals("Plus(a, b)[x]", obj.toString());
  }

  @Test
  void testParser19() {
    Parser p = new Parser();
    ASTNode obj = p.parse("B[[;; 2]]");
    assertEquals("Part(B, Span(1, 2))", obj.toString());
  }

  @Test
  void testParser20() {
    Parser p = new Parser();
    ASTNode obj = p.parse("B[[;;, 2]]");
    assertEquals("Part(B, Span(1, All), 2)", obj.toString());
  }

  @Test
  void testParser21a() {
    Parser p = new Parser();
    ASTNode obj = p.parse("B[[ ;;1;;-1]]");
    assertEquals("Part(B, Span(1, 1, -1))", obj.toString());
  }

  @Test
  void testParser21b() {
    Parser p = new Parser();
    ASTNode obj = p.parse("B[[3;;1;;-1]]");
    assertEquals("Part(B, Span(3, 1, -1))", obj.toString());
  }

  @Test
  void testParser21c() {
    Parser p = new Parser();
    ASTNode obj = p.parse("B[[;;;;-1]]");
    assertEquals("Part(B, Span(1, All, -1))", obj.toString());
  }

  @Test
  void testParserFunction() {
    Parser p = new Parser(true);
    ASTNode obj = p.parse("#^2-3#-1&");
    assertEquals(
        "Function(Plus(Plus(Power(Slot(1), 2), Times(-1, Times(3, Slot(1)))), Times(-1, 1)))",
        obj.toString());
  }

  @Test
  void testParser22() {
    Parser p = new Parser();
    ASTNode obj = p.parse("a+b;");
    assertEquals("CompoundExpression(Plus(a, b), Null)", obj.toString());
  }

  @Test
  void testParser23() {
    Parser p = new Parser();
    ASTNode obj = p.parse("f[x]*f''[x]");
    assertEquals("Times(f(x), Derivative(2)[f][x])", obj.toString());
  }

  @Test
  void testParser24() {
    Parser p = new Parser();
    ASTNode obj = p.parse("x'[t] == 10*(y[t] - x[t])");
    assertEquals("Equal(Derivative(1)[x][t], Times(10, Plus(y(t), Times(-1, x(t)))))",
        obj.toString());
  }

  @Test
  void testParser25() {
    Parser p = new Parser();
    ASTNode obj = p.parse("Int[f_'[x_]*g_[x_] + f_[x_]*g_'[x_],x_Symbol]");
    assertEquals(
        "Int(Plus(Times(Derivative(1)[f_][x_], g_(x_)), Times(f_(x_), Derivative(1)[g_][x_])), x_Symbol)",
        obj.toString());
  }

  @Test
  void testParser26() {
    Parser p = new Parser(false, true);
    List<ASTNode> obj = p.parsePackage("	SumSimplerAuxQ[u_,v_] :=\n" + "			  v=!=0 && \n"
        + "			  NonnumericFactors[u]===NonnumericFactors[v] &&\n"
        + "			  (NumericFactor[u]/NumericFactor[v]<-1/2 || NumericFactor[u]/NumericFactor[v]==-1/2 && NumericFactor[u]<0)\n"
        + "\n" + "");
    assertEquals(
        "[SetDelayed(SumSimplerAuxQ(u_, v_), And(UnsameQ(v, 0), SameQ(NonnumericFactors(u), NonnumericFactors(v)), Or(Less(Times(NumericFactor(u), Power(NumericFactor(v), -1)), -1/2), And(Equal(Times(NumericFactor(u), Power(NumericFactor(v), -1)), -1/2), Less(NumericFactor(u), 0)))))]",
        obj.toString());
  }

  @Test
  void testParser27() {
    Parser p = new Parser(false, true);
    List<ASTNode> obj = p.parsePackage("\n" + "TryTanhSubst[u_,x_Symbol] :=\n"
        + "  FalseQ[FunctionOfLinear[u,x]] &&\n"
        + "  Not[MatchQ[u,r_.*(s_+t_)^n_. /; IntegerQ[n] && n>0]] &&\n"
        + "(*Not[MatchQ[u,Log[f_[x]^2] /; SinhCoshQ[f]]]  && *)\n"
        + "  Not[MatchQ[u,Log[v_]]]  &&\n"
        + "  Not[MatchQ[u,1/(a_+b_.*f_[x]^n_) /; SinhCoshQ[f] && IntegerQ[n] && n>2]] &&\n"
        + "  Not[MatchQ[u,f_[m_.*x]*g_[n_.*x] /; IntegersQ[m,n] && SinhCoshQ[f] && SinhCoshQ[g]]] &&\n"
        + "  Not[MatchQ[u,r_.*(a_.*s_^m_)^p_ /; FreeQ[{a,m,p},x] && Not[m===2 && (s===Sech[x] || s===Csch[x])]]] &&\n"
        + "  u===ExpandIntegrand[u,x]\n" + "\n" + "TryPureTanhSubst[u_,x_Symbol] :=\n"
        + "  Not[MatchQ[u,Log[v_]]]  &&\n"
        + "  Not[MatchQ[u,ArcTanh[a_.*Tanh[v_]] /; FreeQ[a,x]]] &&\n"
        + "  Not[MatchQ[u,ArcTanh[a_.*Coth[v_]] /; FreeQ[a,x]]] &&\n"
        + "  Not[MatchQ[u,ArcCoth[a_.*Tanh[v_]] /; FreeQ[a,x]]] &&\n"
        + "  Not[MatchQ[u,ArcCoth[a_.*Coth[v_]] /; FreeQ[a,x]]] &&\n"
        + "  u===ExpandIntegrand[u,x]\n" + "\n" + "");
    assertEquals(
        "[SetDelayed(TryTanhSubst(u_, x_Symbol), And(FalseQ(FunctionOfLinear(u, x)), Not(MatchQ(u, Condition(Times(r_., Power(Plus(s_, t_), n_.)), And(IntegerQ(n), Greater(n, 0))))), Not(MatchQ(u, Log(v_))), Not(MatchQ(u, Condition(Power(Plus(a_, Times(b_., Power(f_(x), n_))), -1), And(SinhCoshQ(f), IntegerQ(n), Greater(n, 2))))), Not(MatchQ(u, Condition(Times(f_(Times(m_., x)), g_(Times(n_., x))), And(IntegersQ(m, n), SinhCoshQ(f), SinhCoshQ(g))))), Not(MatchQ(u, Condition(Times(r_., Power(Times(a_., Power(s_, m_)), p_)), And(FreeQ(List(a, m, p), x), Not(And(SameQ(m, 2), Or(SameQ(s, Sech(x)), SameQ(s, Csch(x))))))))), SameQ(u, ExpandIntegrand(u, x)))), SetDelayed(TryPureTanhSubst(u_, x_Symbol), And(Not(MatchQ(u, Log(v_))), Not(MatchQ(u, Condition(ArcTanh(Times(a_., Tanh(v_))), FreeQ(a, x)))), Not(MatchQ(u, Condition(ArcTanh(Times(a_., Coth(v_))), FreeQ(a, x)))), Not(MatchQ(u, Condition(ArcCoth(Times(a_., Tanh(v_))), FreeQ(a, x)))), Not(MatchQ(u, Condition(ArcCoth(Times(a_., Coth(v_))), FreeQ(a, x)))), SameQ(u, ExpandIntegrand(u, x))))]",
        obj.toString());
  }

  @Test
  void testParser28() {
    Parser p = new Parser(false, true);
    List<ASTNode> list = p.parsePackage("\n" + "TrigSimplifyAux[u_] := u\n" + "\n" + "\n"
        + "(* ::Section::Closed:: *)\n" + "(*Factoring functions*)\n" + "\n" + "\n"
        + "(* ::Subsection::Closed:: *)\n" + "(*ContentFactor*)\n" + "\n" + "\n"
        + "(* ContentFactor[expn] returns expn with the content of sum factors factored out. *)\n"
        + "(* Basis: a*b+a*c == a*(b+c) *)\n" + "ContentFactor[expn_] :=\n"
        + "  TimeConstrained[ContentFactorAux[expn],2.0,expn];\n" + "\n"
        + "ContentFactorAux[expn_] :=\n" + "  If[AtomQ[expn],\n" + "    expn,\n"
        + "  If[IntegerPowerQ[expn],\n"
        + "    If[SumQ[expn[[1]]] && NumericFactor[expn[[1,1]]]<0,\n"
        + "      (-1)^expn[[2]] * ContentFactorAux[-expn[[1]]]^expn[[2]],\n"
        + "    ContentFactorAux[expn[[1]]]^expn[[2]]],\n" + "  If[ProductQ[expn],\n"
        + "    Module[{num=1,tmp},\n"
        + "    tmp=Map[Function[If[SumQ[#] && NumericFactor[#[[1]]]<0, num=-num; ContentFactorAux[-#], ContentFactorAux[#]]], expn];\n"
        + "    num*UnifyNegativeBaseFactors[tmp]],\n" + "  If[SumQ[expn],\n"
        + "    With[{lst=CommonFactors[Apply[List,expn]]},\n"
        + "    If[lst[[1]]===1 || lst[[1]]===-1,\n" + "      expn,\n"
        + "    lst[[1]]*Apply[Plus,Rest[lst]]]],\n" + "  expn]]]]\n" + "\n" + "");
    assertEquals(3, list.size());
    assertEquals(
        "CompoundExpression(SetDelayed(ContentFactor(expn_), TimeConstrained(ContentFactorAux(expn), 2.0, expn)), Null)",
        list.get(1).toString());
  }

  @Test
  void testParser29() {
    Parser p = new Parser();
    ASTNode obj = p.parse("FracPart[m_*u_,n_:1]");
    assertEquals("FracPart(Times(m_, u_), Optional(n_, 1))", obj.toString());
  }

  @Test
  void testParse30() {
    Parser p = new Parser();
    ASTNode obj = p.parse("f[x,y,]");
    assertEquals("f(x, y, Null)", obj.toString());
  }

  @Test
  void testParser31() {
    Parser p = new Parser();
    ASTNode obj = p.parse("#1.#123");
    assertEquals("Dot(Slot(1), Slot(123))", obj.toString());
  }

  @Test
  void testParser32() {
    Parser p = new Parser();
    ASTNode obj = p.parse("(-1)^(a) (b)");
    assertEquals("Times(Power(-1, a), b)", obj.toString());
  }

  @Test
  void testParser33() {
    Parser p = new Parser();
    ASTNode obj = p.parse(
        "Int[(u_)*(y_)^(m_.), x_Symbol] := With[{q = DerivativeDivides[y, u, x]}, Simp[(q*y^(m + 1))/(m + 1), x] /;  !FalseQ[q]] /; FreeQ[m, x] && NeQ[m, -1]");
    assertEquals(
        "SetDelayed(Int(Times(u_, Power(y_, m_.)), x_Symbol), Condition(With(List(Set(q, DerivativeDivides(y, u, x))), Condition(Simp(Times(Times(q, Power(y, Plus(m, 1))), Power(Plus(m, 1), -1)), x), Not(FalseQ(q)))), And(FreeQ(m, x), NeQ(m, -1))))",
        obj.toString());
  }

  @Test
  void testParse33() {
    Parser p = new Parser();
    ASTNode obj = p.parse("f@ g@ h");
    assertEquals("f(g(h))", obj.toString());
  }

  @Test
  void testParse34() {
    Parser p = new Parser();
    // http://oeis.org/A005132
    ASTNode obj = p.parse(
        "a = {1}; Do[ If[ a[ [ -1 ] ] - n > 0 && Position[ a, a[ [ -1 ] ] - n ] == {}, a = Append[ a, a[ [ -1 ] ] - n ], a = Append[ a, a[ [ -1 ] ] + n ] ], {n, 2, 70} ]; a");
    assertEquals(
        "CompoundExpression(Set(a, List(1)), Do(If(And(Greater(Plus(Part(a, -1), Times(-1, n)), 0), Equal(Position(a, Plus(Part(a, -1), Times(-1, n))), List())), Set(a, Append(a, Plus(Part(a, -1), Times(-1, n)))), Set(a, Append(a, Plus(Part(a, -1), n)))), List(n, 2, 70)), a)",
        obj.toString());
  }

  @Test
  void testParse35() {
    Parser p = new Parser();
    // http://oeis.org/A005132
    ASTNode obj = p.parse("	If[!MatchQ[#,_\\[Rule]_],\n"
        + "       Message[Caller::\"UnknownOption\",#];\n" + "       (*else*),\n"
        + "       pos=Position[FullOptions,{#[[1]],_,_}];\n" + "       If[Length[pos]\\[Equal]0,\n"
        + "         Message[Caller::\"UnknownOption\",#]\n" + "         (*else*),\n"
        + "         FullOptions[[pos[[1,1]],3]]=#[[2]]\n" + "         ];\n" + "       ];");
    assertEquals(
        "CompoundExpression(If(Not(MatchQ(Slot(1), Rule(_, _))), CompoundExpression(Message(MessageName(Caller, UnknownOption), Slot(1)), Null), CompoundExpression(Set(pos, Position(FullOptions, List(Part(Slot(1), 1), _, _))), If(Equal(Length(pos), 0), Message(MessageName(Caller, UnknownOption), Slot(1)), Set(Part(FullOptions, Part(pos, 1, 1), 3), Part(Slot(1), 2))), Null)), Null)",
        obj.toString());
  }

  @Test
  void testParse36() {
    // System.out.println(Character.isUnicodeIdentifierPart('\u221E'));
    Parser p = new Parser();
    ASTNode obj = p.parse("\u221E");
    assertEquals("Infinity", obj.toString());
  }

  @Test
  void testParse37() {
    Parser p = new Parser();
    ASTNode obj = p.parse("      Do[\n" + "        serh=SeriesHead[ser,\\[Omega]];\n" + "        \n"
        + "        (* check for series term that run out of precision *)\n"
        + "        If[FreeQ[serh,HoldPattern[SeriesData[_,_,{},_,_,_]]],\n"
        + "          (* No: done. *)\n" + "          Break[];\n" + "          ];\n"
        + "        ,{i,1,30}\n" + "        ]");
    assertEquals(
        "Do(CompoundExpression(Set(serh, SeriesHead(ser, ω)), If(FreeQ(serh, HoldPattern(SeriesData(_, _, List(), _, _, _))), CompoundExpression(Break(), Null)), Null), List(i, 1, 30))",
        obj.toString());
  }

  @Test
  void testParse38() {
    Parser p = new Parser();
    ASTNode obj =
        p.parse("MakeGraph[Range[26],\n" + "                            Mod[#1-#2, 26] == 1 ||\n"
            + "                                (-1)^#1Mod[#1-#2, 26] == 11 ||\n"
            + "                                (-1)^#1Mod[#1-#2, 26] == 7&,\n"
            + "                            Type -> Directed]");
    assertEquals(
        "MakeGraph(Range(26), Function(Or(Equal(Mod(Plus(Slot(1), Times(-1, Slot(2))), 26), 1), Equal(Times(Power(-1, Slot(1)), Mod(Plus(Slot(1), Times(-1, Slot(2))), 26)), 11), Equal(Times(Power(-1, Slot(1)), Mod(Plus(Slot(1), Times(-1, Slot(2))), 26)), 7))), Rule(Type, Directed))",
        obj.toString());
  }

  @Test
  void testParse39() {
    Parser p = new Parser();
    ASTNode obj = p.parse("While[i\\[LessEqual]Length[asympt] && asympt[[i,2]]\\[Equal]0,i++]");
    assertEquals(
        "While(And(LessEqual(i, Length(asympt)), Equal(Part(asympt, i, 2), 0)), Increment(i))",
        obj.toString());
  }

  @Test
  void testParse40() {
    Parser p = new Parser();
    ASTNode obj = p.parse("ff/: Power[ff, i_Integer] = {i}");
    assertEquals("TagSet(ff, Power(ff, i_Integer), List(i))", obj.toString());
  }

  @Test
  void testParse41() {
    Parser p = new Parser();
    ASTNode obj = p.parse("ff/: Power[ff, i_Integer] := {i}");
    assertEquals("TagSetDelayed(ff, Power(ff, i_Integer), List(i))", obj.toString());
  }

  @Test
  void testParse42() {
    Parser p = new Parser();
    ASTNode obj = p.parse("m_.k_+b_.");
    assertEquals("Plus(Times(m_., k_), b_.)", obj.toString());
  }

  @Test
  void testParse43() {
    Parser p = new Parser();
    ASTNode obj = p.parse("MakeAssocList[u_,x_Symbol,alst_List:{}] ");
    assertEquals("MakeAssocList(u_, x_Symbol, Optional(alst_List, List()))", obj.toString());
  }

  @Test
  void testParser44() {
    Parser p = new Parser();
    ASTNode obj = p.parse("-1<0<=a<4<5<b<10<11");
    assertEquals(
        "Inequality(-1, Less, 0, LessEqual, a, Less, 4, Less, 5, Less, b, Less, 10, Less, 11)",
        obj.toString());
  }

  @Test
  void testParser45() {
    Parser p = new Parser();
    ASTNode obj = p.parse("\"array\"[x] ");
    assertEquals("array(x)", obj.toString());
  }

  @Test
  void testParser46() {
    Parser p = new Parser();
    ASTNode obj = p.parse("{a,b,c}[x] ");
    assertEquals("List(a, b, c)[x]", obj.toString());
  }

  @Test
  void testParser47() {
    Parser p = new Parser();
    ASTNode obj = p.parse("123456789012345678901234567890");
    assertEquals("123456789012345678901234567890", obj.toString());
  }

  @Test
  void testParser48a() {
    Parser p = new Parser();
    ASTNode obj = p.parse("FullForm[Hold[;;; ]]");
    assertEquals("FullForm(Hold(CompoundExpression(Span(1, All), Null)))", obj.toString());
  }

  @Test
  void testParser48b() {
    Parser p = new Parser();
    ASTNode obj = p.parse("FullForm[Hold[abc;;; ]]");
    assertEquals("FullForm(Hold(CompoundExpression(Span(abc, All), Null)))", obj.toString());
  }

  @Test
  void testParser48c() {
    Parser p = new Parser();
    ASTNode obj = p.parse("FullForm[Hold[abc;;;d ]]");
    assertEquals("FullForm(Hold(CompoundExpression(Span(abc, All), d)))", obj.toString());
  }

  @Test
  void testParser49() {
    Parser p = new Parser();
    ASTNode obj = p.parse("FullForm[Hold[abc;;;;; ]]");
    assertEquals("FullForm(Hold(CompoundExpression(Times(Span(abc, All), Span(1, All)), Null)))",
        obj.toString());
  }

  @Test
  void testParser50() {
    Parser p = new Parser();
    ASTNode obj = p.parse("FullForm[Hold[{a, b, c, d, e, f, g, h, i, j, k}[[3 ;; -3 ;; 2]]]]");
    assertEquals("FullForm(Hold(Part(List(a, b, c, d, e, f, g, h, i, j, k), Span(3, -3, 2))))",
        obj.toString());
  }

  @Test
  void testParser51() {
    Parser p = new Parser();
    ASTNode obj = p.parse("FullForm[Hold[UT[n, ttt\\[Lambda]abc0]]]");
    assertEquals("FullForm(Hold(UT(n, tttλabc0)))", obj.toString());
  }

  @Test
  void testParser52() {
    Parser p = new Parser();
    ASTNode obj = p.parse("FullForm[Hold[{1, 2, 3, 4, 5}[[;; ;; -1]]]]");
    assertEquals("FullForm(Hold(Part(List(1, 2, 3, 4, 5), Span(1, All, -1))))", obj.toString());
  }

  @Test
  void testParser53() {
    Parser p = new Parser();
    ASTNode obj = p.parse("opts : OptionsPattern[]");
    assertEquals("Pattern(opts, OptionsPattern())", obj.toString());
  }

  @Test
  void testParser54() {
    Parser p = new Parser();
    ASTNode obj = p.parse("f[x_, opts : OptionsPattern[]]");
    assertEquals("f(x_, Pattern(opts, OptionsPattern()))", obj.toString());
  }

  @Test
  void testParser55() {
    Parser p = new Parser();
    ASTNode obj = p.parse("a:b:c:d");
    assertEquals("Optional(Pattern(a, b), Pattern(c, d))", obj.toString());
  }

  @Test
  void testParser56() {
    // see https://github.com/halirutan/Wolfram-Language-IntelliJ-Plugin-Archive/issues/166
    Parser p = new Parser();
    ASTNode obj = p.parse("x y : z");
    assertEquals("Times(x, Pattern(y, z))", obj.toString());
  }

  @Test
  void testParser57() {
    // see https://github.com/halirutan/Wolfram-Language-IntelliJ-Plugin-Archive/issues/166
    Parser p = new Parser();
    ASTNode obj = p.parse("x y_ : z");
    assertEquals("Times(x, Optional(y_, z))", obj.toString());
  }

  @Test
  void testParser58() {
    Parser p = new Parser();
    ASTNode obj = p.parse("StringContainsQ[\"bcde\", \"c\" ~~ __ ~~ \"t\"]");
    assertEquals("StringContainsQ(bcde, StringExpression(c, __, t))", obj.toString());
  }

  @Test
  void testParser59() {
    Parser p = new Parser();
    ASTNode obj = p.parse("f[t_] = Simplify[ r'[t] / Norm[ r'[t] ], t ∈ Reals];");
    assertEquals(
        "CompoundExpression(Set(f(t_), Simplify(Times(Derivative(1)[r][t], Power(Norm(Derivative(1)[r][t]), -1)), Element(t, Reals))), Null)",
        obj.toString());
  }

  @Test
  void testParser60() {
    Parser p = new Parser();
    ASTNode obj = p.parse("x[ [ ] ]");
    assertEquals("Part(x)", obj.toString());
  }

  @Test
  void testParser61() {
    Parser p = new Parser();
    ASTNode obj = p.parse("(a+i*b^2+k*c^3+d)");
    assertEquals("Plus(a, Times(i, Power(b, 2)), Times(k, Power(c, 3)), d)", obj.toString());
  }

  @Test
  void testParser62() {
    Parser p = new Parser();
    ASTNode obj = p.parse("\\[Theta] = 4");
    assertEquals("Set(θ, 4)", obj.toString());
  }

  @Test
  void testParser63() {
    assumeTrue(System.getProperty("os.name").contains("Windows"));
    Exception e = assertThrows(Exception.class, () -> {
      Parser p = new Parser();
      p.parse("\\[DifferentialD] = 4");
    });
    assertEquals(e.getMessage(),
        "Syntax error in line: 1 - unexpected (named unicode) character: '\\[DifferentialD]'\n"
            + " = 4\n" + "^");
  }

  @Test
  void testParser64() {
    Parser p = new Parser();
    ASTNode obj = p.parse("test = \" \\[Theta] is using {\\\"1\\\", \\\"2\\\"} instead.\"");
    assertEquals("Set(test,  θ is using {\"1\", \"2\"} instead.)", obj.toString());
  }

  @Test
  void testParser65() {
    Parser p = new Parser();
    ASTNode obj = p.parse("2.2250738585072014`*^-308");
    assertEquals("2.2250738585072014E-308", obj.toString());
  }

  @Test
  void testParser66() {
    Parser p = new Parser();
    ASTNode obj = p.parse("a~b~c");
    assertEquals("b(a, c)", obj.toString());
  }

  @Test
  void testParser67() {
    Parser p = new Parser();
    ASTNode obj = p.parse("a~b~c~d~e");
    assertEquals("d(b(a, c), e)", obj.toString());
  }

  @Test
  void testParser68() {
    Parser p = new Parser();
    ASTNode obj = p.parse("8 + 1*^-28");
    assertEquals("Plus(8, Times(1, Power(10000000000000000000000000000, -1)))", obj.toString());

    // 6
    obj = p.parse("6*^0");
    assertEquals("Times(6, 1)", obj.toString());

    // 3/5
    obj = p.parse("6*^-1");
    assertEquals("Times(6, Power(10, -1))", obj.toString());

    // 6000
    obj = p.parse("6*^3");
    assertEquals("Times(6, 1000)", obj.toString());
  }

  @Test
  void testParser69() {
    Parser p = new Parser();
    ASTNode obj = p.parse("2/3 + Pi + 5.5`30");
    assertEquals("Plus(2/3, Pi, 5.5)", obj.toString());
  }

  @Test
  void testParser70() {
    Parser p = new Parser();
    ASTNode obj = p.parse("Plot3D[Sin[x*y],{x,-1.5`,1.5`},{y,-1.5`,1.5`}]");
    assertEquals("Plot3D(Sin(Times(x, y)), List(x, -1.5, 1.5), List(y, -1.5, 1.5))",
        obj.toString());
  }

  @Test
  void testParser71() {
    Parser p = new Parser();
    ASTNode obj = p.parse("  \n" + "a = f[Rule, {#, RandomReal[1, Length[#]]}]&[\n"
        + "Flatten[constants[[All,2]]]" + "\n]");
    assertEquals(
        "Set(a, Function(f(Rule, List(Slot(1), RandomReal(1, Length(Slot(1))))))[Flatten(Part(constants, All, 2))])",
        obj.toString());
  }

  @Test
  void testParser72() {
    Parser p = new Parser();
    ASTNode obj = p.parse("  \n" + "	constantRules = f[Rule, {#, RandomReal[1, Length[#]]}]&[\n"
        + "		Flatten[constants[[All,2]]]\n" + "	]");
    assertEquals(
        "Set(constantRules, Function(f(Rule, List(Slot(1), RandomReal(1, Length(Slot(1))))))[Flatten(Part(constants, All, 2))])",
        obj.toString());
  }

  @Test
  void testParser73() {
    Parser p = new Parser();
    ASTNode obj = p.parse("y:r_^n_");
    // Pattern[y,Power[Pattern[r,Blank[]],Pattern[n,Blank[]]]]
    assertEquals("Pattern(y, Power(r_, n_))", obj.toString());
  }

  @Test
  void testParser74() {
    Parser p = new Parser();
    ASTNode obj = p.parse("Cases[e, y:(a_. x + b_.)^n_ /; FreeQ[{a,b}, x] ]");
    // Cases[e,Condition[Pattern[y,Power[Plus[Times[Optional[Pattern[a,Blank[]]],x],Optional[Pattern[b,Blank[]]]],Pattern[n,Blank[]]]],FreeQ[List[a,b],x]]]
    assertEquals(
        "Cases(e, Condition(Pattern(y, Power(Plus(Times(a_., x), b_.), n_)), FreeQ(List(a, b), x)))",
        obj.toString());
  }

  @Test
  void testParser75() {
    Parser p = new Parser();
    ASTNode obj = p.parse("Cases[e, t:{__Integer} :> t^2]");
    assertEquals("Cases(e, RuleDelayed(Pattern(t, List(__Integer)), Power(t, 2)))", obj.toString());
  }

  // Hold[Cases[e, y:(a_. x + b_.)^n_ /; FreeQ[{a,b}, x] && Head[n] == Rational :> {y, a x + b,
  // Numerator[n], Denominator[n], a, b}, {0, Infinity}]]//FullForm
  @Test
  void testParser76() {
    Parser p = new Parser();
    ASTNode obj = p.parse(
        "Cases[e, y:(a_. x + b_.)^n_ /;  FreeQ[{a,b}, x] && Head[n] == Rational :> {y, a x + b, Numerator[n], Denominator[n], a, b}, {0, Infinity}]");
    assertEquals(
        "Cases(e, RuleDelayed(Condition(Pattern(y, Power(Plus(Times(a_., x), b_.), n_)), And(FreeQ(List(a, b), x), Equal(Head(n), Rational))), List(y, Plus(Times(a, x), b), Numerator(n), Denominator(n), a, b)), List(0, Infinity))",
        obj.toString());
  }

  @Test
  void testParser77() {
    Parser p = new Parser();
    ASTNode obj = p.parse("a_+b:Power[c_,_Rational]");
    assertEquals("Plus(a_, Pattern(b, Power(c_, _Rational)))", obj.toString());
  }

  @Test
  void testParser78() {
    Parser p = new Parser();
    ASTNode obj = p.parse("WordBoundary ~~ x:DigitCharacter.. ~~ WordBoundary");
    assertEquals(
        "StringExpression(WordBoundary, Pattern(x, Repeated(DigitCharacter)), WordBoundary)",
        obj.toString());
  }

  @Test
  void testParser79() {
    Parser p = new Parser();
    ASTNode obj = p.parse(" (* a test *) ");
    assertEquals("Null", obj.toString());
  }

  @Test
  void testParser80() {
    Parser p = new Parser();
    ASTNode obj = p.parse(" 2 *(* a test *) 4");
    assertEquals("Times(2, 4)", obj.toString());
  }

  @Test
  void testParser81() {
    Parser p = new Parser();
    ASTNode obj = p.parse("x|->(x^3)");
    assertEquals("Function(x, Power(x, 3))", obj.toString());
  }

  @Test
  void testParser82() {
    Parser p = new Parser();
    ASTNode obj = p.parse("x \\[Function] (x^3)");
    assertEquals("Function(x, Power(x, 3))", obj.toString());
  }
}
