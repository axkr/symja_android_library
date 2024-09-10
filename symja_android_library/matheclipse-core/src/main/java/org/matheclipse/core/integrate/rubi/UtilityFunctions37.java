package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$rubi;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.AppendTo;
import static org.matheclipse.core.expression.F.AtomQ;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Cancel;
import static org.matheclipse.core.expression.F.CompoundExpression;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.First;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.Function;
import static org.matheclipse.core.expression.F.Head;
import static org.matheclipse.core.expression.F.ISetDelayed;
import static org.matheclipse.core.expression.F.If;
import static org.matheclipse.core.expression.F.Im;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.ListQ;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Map;
import static org.matheclipse.core.expression.F.MemberQ;
import static org.matheclipse.core.expression.F.Module;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.OddQ;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Part;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Re;
import static org.matheclipse.core.expression.F.Rest;
import static org.matheclipse.core.expression.F.SameQ;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Slot1;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.list;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.Cos;
import static org.matheclipse.core.expression.S.False;
import static org.matheclipse.core.expression.S.Null;
import static org.matheclipse.core.expression.S.Sin;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.AllNegTermQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.AtomBaseQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Coeff;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ComplexNumberQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FreeFactors;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FreeTerms;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntSum;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntTerm;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NegQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NegSumBaseQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NonfreeFactors;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NonfreeTerms;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NthRoot;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PosQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PowerQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ProductQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RemoveContent;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RtAux;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RuleName;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SomeNegTermQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SplitProduct;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SplitSum;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SumBaseQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SumQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.TrigSquare;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.TrigSquareQ;
import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class UtilityFunctions37 { 
  public static IAST RULES = List( 
ISetDelayed(671,NthRoot(u_,n_),
    Power(u,Power(n,CN1))),
ISetDelayed(672,TrigSquare(u_),
    If(SumQ(u),With(list(Set($s("lst"),SplitSum(Function(SplitProduct($rubi("TrigSquareQ"),Slot1)),u))),If(And(Not(AtomQ($s("lst"))),EqQ(Plus(Part($s("lst"),C1,C2),Part($s("lst"),C2)),C0)),If(SameQ(Head(Part(Part($s("lst"),C1,C1),C1)),Sin),Times(Part($s("lst"),C2),Sqr(Cos(Part(Part($s("lst"),C1,C1),C1,C1)))),Times(Part($s("lst"),C2),Sqr(Sin(Part(Part($s("lst"),C1,C1),C1,C1))))),False)),False)),
ISetDelayed(673,RtAux(u_,n_),
    If(PowerQ(u),Power(Part(u,C1),Times(Part(u,C2),Power(n,CN1))),If(ProductQ(u),Module(list($s("lst")),CompoundExpression(Set($s("lst"),SplitProduct(Function(GtQ(Slot1,C0)),u)),If(ListQ($s("lst")),Times(RtAux(Part($s("lst"),C1),n),RtAux(Part($s("lst"),C2),n)),CompoundExpression(Set($s("lst"),SplitProduct(Function(LtQ(Slot1,C0)),u)),If(ListQ($s("lst")),If(EqQ(Part($s("lst"),C1),CN1),With(list(Set(v,Part($s("lst"),C2))),If(And(PowerQ(v),LtQ(Part(v,C2),C0)),Power(RtAux(Negate(Power(Part(v,C1),Negate(Part(v,C2)))),n),CN1),If(ProductQ(v),If(ListQ(SplitProduct($rubi("SumBaseQ"),v)),CompoundExpression(Set($s("lst"),SplitProduct($rubi("AllNegTermQ"),v)),If(ListQ($s("lst")),Times(RtAux(Negate(Part($s("lst"),C1)),n),RtAux(Part($s("lst"),C2),n)),CompoundExpression(Set($s("lst"),SplitProduct($rubi("NegSumBaseQ"),v)),If(ListQ($s("lst")),Times(RtAux(Negate(Part($s("lst"),C1)),n),RtAux(Part($s("lst"),C2),n)),CompoundExpression(Set($s("lst"),SplitProduct($rubi("SomeNegTermQ"),v)),If(ListQ($s("lst")),Times(RtAux(Negate(Part($s("lst"),C1)),n),RtAux(Part($s("lst"),C2),n)),CompoundExpression(Set($s("lst"),SplitProduct($rubi("SumBaseQ"),v)),Times(RtAux(Negate(Part($s("lst"),C1)),n),RtAux(Part($s("lst"),C2),n))))))))),CompoundExpression(Set($s("lst"),SplitProduct($rubi("AtomBaseQ"),v)),If(ListQ($s("lst")),Times(RtAux(Negate(Part($s("lst"),C1)),n),RtAux(Part($s("lst"),C2),n)),Times(RtAux(Negate(First(v)),n),RtAux(Rest(v),n))))),If(OddQ(n),Negate(RtAux(v,n)),NthRoot(u,n))))),Times(RtAux(Negate(Part($s("lst"),C1)),n),RtAux(Negate(Part($s("lst"),C2)),n))),CompoundExpression(Set($s("lst"),SplitProduct($rubi("AllNegTermQ"),u)),If(And(ListQ($s("lst")),ListQ(SplitProduct($rubi("SumBaseQ"),Part($s("lst"),C2)))),Times(RtAux(Negate(Part($s("lst"),C1)),n),RtAux(Negate(Part($s("lst"),C2)),n)),CompoundExpression(Set($s("lst"),SplitProduct($rubi("NegSumBaseQ"),u)),If(And(ListQ($s("lst")),ListQ(SplitProduct($rubi("NegSumBaseQ"),Part($s("lst"),C2)))),Times(RtAux(Negate(Part($s("lst"),C1)),n),RtAux(Negate(Part($s("lst"),C2)),n)),Map(Function(RtAux(Slot1,n)),u)))))))))),With(list(Set(v,TrigSquare(u))),If(Not(AtomQ(v)),RtAux(v,n),If(And(OddQ(n),LtQ(u,C0)),Negate(RtAux(Negate(u),n)),If(ComplexNumberQ(u),With(list(Set(a,Re(u)),Set(b,Im(u))),If(And(Not(And(IntegerQ(a),IntegerQ(b))),IntegerQ(Times(a,Power(Plus(Sqr(a),Sqr(b)),CN1))),IntegerQ(Times(b,Power(Plus(Sqr(a),Sqr(b)),CN1)))),Power(RtAux(Subtract(Times(a,Power(Plus(Sqr(a),Sqr(b)),CN1)),Times(b,Power(Plus(Sqr(a),Sqr(b)),CN1),CI)),n),CN1),NthRoot(u,n))),If(And(OddQ(n),NegQ(u),PosQ(Negate(u))),Negate(RtAux(Negate(u),n)),NthRoot(u,n))))))))),
ISetDelayed(674,AtomBaseQ(u_),
    Or(AtomQ(u),And(PowerQ(u),OddQ(Part(u,C2)),AtomBaseQ(Part(u,C1))))),
ISetDelayed(675,SumBaseQ(u_),
    Or(SumQ(u),And(PowerQ(u),OddQ(Part(u,C2)),SumBaseQ(Part(u,C1))))),
ISetDelayed(676,NegSumBaseQ(u_),
    Or(And(SumQ(u),NegQ(First(u))),And(PowerQ(u),OddQ(Part(u,C2)),NegSumBaseQ(Part(u,C1))))),
ISetDelayed(677,AllNegTermQ(u_),
    If(And(PowerQ(u),OddQ(Part(u,C2))),AllNegTermQ(Part(u,C1)),If(SumQ(u),And(NegQ(First(u)),AllNegTermQ(Rest(u))),NegQ(u)))),
ISetDelayed(678,SomeNegTermQ(u_),
    If(And(PowerQ(u),OddQ(Part(u,C2))),SomeNegTermQ(Part(u,C1)),If(SumQ(u),Or(NegQ(First(u)),SomeNegTermQ(Rest(u))),NegQ(u)))),
ISetDelayed(679,TrigSquareQ(u_),
    And(PowerQ(u),EqQ(Part(u,C2),C2),MemberQ(list(Sin,Cos),Head(Part(u,C1))))),
ISetDelayed(680,SplitProduct($p("func"),u_),
    If(ProductQ(u),If($($s("func"),First(u)),list(First(u),Rest(u)),With(list(Set($s("lst"),SplitProduct($s("func"),Rest(u)))),If(AtomQ($s("lst")),False,list(Part($s("lst"),C1),Times(First(u),Part($s("lst"),C2)))))),If($($s("func"),u),list(u,C1),False))),
ISetDelayed(681,SplitSum($p("func"),u_),
    If(SumQ(u),If(Not(AtomQ($($s("func"),First(u)))),list($($s("func"),First(u)),Rest(u)),With(list(Set($s("lst"),SplitSum($s("func"),Rest(u)))),If(AtomQ($s("lst")),False,list(Part($s("lst"),C1),Plus(First(u),Part($s("lst"),C2)))))),If(Not(AtomQ($($s("func"),u))),list($($s("func"),u),C0),False))),
ISetDelayed(682,IntSum(u_,x_Symbol),
    Plus(Simp(Times(FreeTerms(u,x),x),x),IntTerm(NonfreeTerms(u,x),x))),
ISetDelayed(683,IntTerm(u_,x_Symbol),
    Condition(Map(Function(IntTerm(Slot1,x)),u),SumQ(u))),
ISetDelayed(684,IntTerm(Times(c_DEFAULT,Power(v_,m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,Cancel(v))),If(EqQ(m,CN1),Simp(Times(c,Log(RemoveContent(u,x)),Power(Coeff(u,x,C1),CN1)),x),If(And(EqQ(m,C1),EqQ(c,C1),SumQ(u)),IntSum(u,x),Simp(Times(c,Power(u,Plus(m,C1)),Power(Times(Coeff(u,x,C1),Plus(m,C1)),CN1)),x)))),And(FreeQ(list(c,m),x),LinearQ(v,x)))),
ISetDelayed(685,IntTerm(u_,x_Symbol),
    Dist(FreeFactors(u,x),Integrate(NonfreeFactors(u,x),x),x)),
ISetDelayed(686,RuleName($p("name")),
          CompoundExpression(AppendTo($s("§$rulenamelist"), $s("name")), Null))
      // ISetDelayed(687,FixIntRules(),
      // CompoundExpression(Set(DownValues(Integrate),FixIntRules(DownValues(Integrate))),Null)),
//ISetDelayed(688,FixIntRules($p("rulelist")),
//    Block(List(Integrate,$rubi("Subst"),$rubi("Simp"),$rubi("Dist")),CompoundExpression(SetAttributes(List($rubi("Simp"),$rubi("Dist"),Integrate,$rubi("Subst")),HoldAll),Map(Function(FixIntRule(Slot1,Part(Slot1,C1,C1,C2,C1))),$s("rulelist")))))
  );
}
