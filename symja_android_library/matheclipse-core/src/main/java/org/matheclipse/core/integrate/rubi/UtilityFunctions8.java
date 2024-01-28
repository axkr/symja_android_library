package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.AtomQ;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.C5;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Catch;
import static org.matheclipse.core.expression.F.CoefficientList;
import static org.matheclipse.core.expression.F.CompoundExpression;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Drop;
import static org.matheclipse.core.expression.F.Equal;
import static org.matheclipse.core.expression.F.EvenQ;
import static org.matheclipse.core.expression.F.First;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.Function;
import static org.matheclipse.core.expression.F.ISetDelayed;
import static org.matheclipse.core.expression.F.If;
import static org.matheclipse.core.expression.F.Last;
import static org.matheclipse.core.expression.F.Length;
import static org.matheclipse.core.expression.F.Less;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.ListQ;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Part;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.PolynomialQ;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Rest;
import static org.matheclipse.core.expression.F.Scan;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Slot1;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Throw;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.list;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.q_DEFAULT;
import static org.matheclipse.core.expression.F.r_DEFAULT;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.False;
import static org.matheclipse.core.expression.S.List;
import static org.matheclipse.core.expression.S.Null;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.r;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.BinomialParts;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Coeff;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Expon;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GeneralizedBinomialDegree;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GeneralizedBinomialParts;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GeneralizedTrinomialDegree;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GeneralizedTrinomialParts;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PolyQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PosQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PowerQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ProductQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SumQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.TrinomialParts;
import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class UtilityFunctions8 { 
  public static IAST RULES = List( 
ISetDelayed(71,TrinomialParts(u_,x_Symbol),
    If(PolynomialQ(u,x),With(list(Set($s("lst"),CoefficientList(u,x))),If(Or(Less(Length($s("lst")),C3),EvenQ(Length($s("lst"))),EqQ(Part($s("lst"),Times(C1D2,Plus(Length($s("lst")),C1))),C0)),False,Catch(CompoundExpression(Scan(Function(If(EqQ(Slot1,C0),Null,Throw(False))),Drop(Drop(Drop($s("lst"),list(Times(C1D2,Plus(Length($s("lst")),C1)))),C1),CN1)),List(First($s("lst")),Part($s("lst"),Times(C1D2,Plus(Length($s("lst")),C1))),Last($s("lst")),Times(C1D2,Subtract(Length($s("lst")),C1))))))),If(PowerQ(u),If(EqQ(Part(u,C2),C2),With(list(Set($s("lst"),BinomialParts(Part(u,C1),x))),If(Or(AtomQ($s("lst")),EqQ(Part($s("lst"),C1),C0)),False,List(Sqr(Part($s("lst"),C1)),Times(C2,Part($s("lst"),C1),Part($s("lst"),C2)),Sqr(Part($s("lst"),C2)),Part($s("lst"),C3)))),False),If(ProductQ(u),If(FreeQ(First(u),x),With(list(Set($s("lst2"),TrinomialParts(Rest(u),x))),If(AtomQ($s("lst2")),False,List(Times(First(u),Part($s("lst2"),C1)),Times(First(u),Part($s("lst2"),C2)),Times(First(u),Part($s("lst2"),C3)),Part($s("lst2"),C4)))),If(FreeQ(Rest(u),x),With(list(Set($s("lst1"),TrinomialParts(First(u),x))),If(AtomQ($s("lst1")),False,List(Times(Rest(u),Part($s("lst1"),C1)),Times(Rest(u),Part($s("lst1"),C2)),Times(Rest(u),Part($s("lst1"),C3)),Part($s("lst1"),C4)))),With(list(Set($s("lst1"),BinomialParts(First(u),x))),If(AtomQ($s("lst1")),False,With(list(Set($s("lst2"),BinomialParts(Rest(u),x))),If(AtomQ($s("lst2")),False,With(List(Set(a,Part($s("lst1"),C1)),Set(b,Part($s("lst1"),C2)),Set(m,Part($s("lst1"),C3)),Set(c,Part($s("lst2"),C1)),Set(d,Part($s("lst2"),C2)),Set(n,Part($s("lst2"),C3))),If(And(EqQ(m,n),NeQ(Plus(Times(a,d),Times(b,c)),C0)),List(Times(a,c),Plus(Times(a,d),Times(b,c)),Times(b,d),m),False)))))))),If(SumQ(u),If(FreeQ(First(u),x),With(list(Set($s("lst2"),TrinomialParts(Rest(u),x))),If(AtomQ($s("lst2")),False,List(Plus(First(u),Part($s("lst2"),C1)),Part($s("lst2"),C2),Part($s("lst2"),C3),Part($s("lst2"),C4)))),If(FreeQ(Rest(u),x),With(list(Set($s("lst1"),TrinomialParts(First(u),x))),If(AtomQ($s("lst1")),False,List(Plus(Rest(u),Part($s("lst1"),C1)),Part($s("lst1"),C2),Part($s("lst1"),C3),Part($s("lst1"),C4)))),With(list(Set($s("lst1"),TrinomialParts(First(u),x))),If(AtomQ($s("lst1")),With(list(Set($s("lst3"),BinomialParts(First(u),x))),If(AtomQ($s("lst3")),False,With(list(Set($s("lst2"),TrinomialParts(Rest(u),x))),If(AtomQ($s("lst2")),With(list(Set($s("lst4"),BinomialParts(Rest(u),x))),If(AtomQ($s("lst4")),False,If(EqQ(Part($s("lst3"),C3),Times(C2,Part($s("lst4"),C3))),List(Plus(Part($s("lst3"),C1),Part($s("lst4"),C1)),Part($s("lst4"),C2),Part($s("lst3"),C2),Part($s("lst4"),C3)),If(EqQ(Part($s("lst4"),C3),Times(C2,Part($s("lst3"),C3))),List(Plus(Part($s("lst3"),C1),Part($s("lst4"),C1)),Part($s("lst3"),C2),Part($s("lst4"),C2),Part($s("lst3"),C3)),False)))),If(And(EqQ(Part($s("lst3"),C3),Part($s("lst2"),C4)),NeQ(Plus(Part($s("lst3"),C2),Part($s("lst2"),C2)),C0)),List(Plus(Part($s("lst3"),C1),Part($s("lst2"),C1)),Plus(Part($s("lst3"),C2),Part($s("lst2"),C2)),Part($s("lst2"),C3),Part($s("lst2"),C4)),If(And(EqQ(Part($s("lst3"),C3),Times(C2,Part($s("lst2"),C4))),NeQ(Plus(Part($s("lst3"),C2),Part($s("lst2"),C3)),C0)),List(Plus(Part($s("lst3"),C1),Part($s("lst2"),C1)),Part($s("lst2"),C2),Plus(Part($s("lst3"),C2),Part($s("lst2"),C3)),Part($s("lst2"),C4)),False)))))),With(list(Set($s("lst2"),TrinomialParts(Rest(u),x))),If(AtomQ($s("lst2")),With(list(Set($s("lst4"),BinomialParts(Rest(u),x))),If(AtomQ($s("lst4")),False,If(And(EqQ(Part($s("lst4"),C3),Part($s("lst1"),C4)),NeQ(Plus(Part($s("lst1"),C2),Part($s("lst4"),C2)),C0)),List(Plus(Part($s("lst1"),C1),Part($s("lst4"),C1)),Plus(Part($s("lst1"),C2),Part($s("lst4"),C2)),Part($s("lst1"),C3),Part($s("lst1"),C4)),If(And(EqQ(Part($s("lst4"),C3),Times(C2,Part($s("lst1"),C4))),NeQ(Plus(Part($s("lst1"),C3),Part($s("lst4"),C2)),C0)),List(Plus(Part($s("lst1"),C1),Part($s("lst4"),C1)),Part($s("lst1"),C2),Plus(Part($s("lst1"),C3),Part($s("lst4"),C2)),Part($s("lst1"),C4)),False)))),If(And(EqQ(Part($s("lst1"),C4),Part($s("lst2"),C4)),NeQ(Plus(Part($s("lst1"),C2),Part($s("lst2"),C2)),C0),NeQ(Plus(Part($s("lst1"),C3),Part($s("lst2"),C3)),C0)),List(Plus(Part($s("lst1"),C1),Part($s("lst2"),C1)),Plus(Part($s("lst1"),C2),Part($s("lst2"),C2)),Plus(Part($s("lst1"),C3),Part($s("lst2"),C3)),Part($s("lst1"),C4)),False))))))),False))))),
ISetDelayed(72,GeneralizedBinomialDegree(u_,x_Symbol),
    $(Function(Subtract(Part(Slot1,C3),Part(Slot1,C4))),GeneralizedBinomialParts(u,x))),
ISetDelayed(73,GeneralizedBinomialParts(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT))),x_Symbol),
    Condition(List(a,b,n,q),And(FreeQ(List(a,b,n,q),x),PosQ(Subtract(n,q))))),
ISetDelayed(74,GeneralizedBinomialParts(Times(a_,u_),x_Symbol),
    Condition(With(list(Set($s("lst"),GeneralizedBinomialParts(u,x))),Condition(List(Times(a,Part($s("lst"),C1)),Times(a,Part($s("lst"),C2)),Part($s("lst"),C3),Part($s("lst"),C4)),ListQ($s("lst")))),FreeQ(a,x))),
ISetDelayed(75,GeneralizedBinomialParts(Times(u_,Power(x_,m_DEFAULT)),x_Symbol),
    Condition(With(list(Set($s("lst"),GeneralizedBinomialParts(u,x))),Condition(List(Part($s("lst"),C1),Part($s("lst"),C2),Plus(m,Part($s("lst"),C3)),Plus(m,Part($s("lst"),C4))),And(ListQ($s("lst")),NeQ(Plus(m,Part($s("lst"),C3)),C0),NeQ(Plus(m,Part($s("lst"),C4)),C0)))),FreeQ(m,x))),
ISetDelayed(76,GeneralizedBinomialParts(Times(u_,Power(x_,m_DEFAULT)),x_Symbol),
    Condition(With(list(Set($s("lst"),BinomialParts(u,x))),Condition(List(Part($s("lst"),C1),Part($s("lst"),C2),Plus(m,Part($s("lst"),C3)),m),And(ListQ($s("lst")),NeQ(Plus(m,Part($s("lst"),C3)),C0)))),FreeQ(m,x))),
ISetDelayed(77,GeneralizedBinomialParts(u_,x_Symbol),
    False),
ISetDelayed(78,GeneralizedTrinomialDegree(u_,x_Symbol),
    $(Function(Subtract(Part(Slot1,C4),Part(Slot1,C5))),GeneralizedTrinomialParts(u,x))),
ISetDelayed(79,GeneralizedTrinomialParts(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(c_DEFAULT,Power(x_,r_DEFAULT))),x_Symbol),
    Condition(List(a,b,c,n,q),And(FreeQ(List(a,b,c,n,q),x),EqQ(r,Subtract(Times(C2,n),q))))),
ISetDelayed(80,GeneralizedTrinomialParts(Times(a_,u_),x_Symbol),
    Condition(With(list(Set($s("lst"),GeneralizedTrinomialParts(u,x))),Condition(List(Times(a,Part($s("lst"),C1)),Times(a,Part($s("lst"),C2)),Times(a,Part($s("lst"),C3)),Part($s("lst"),C4),Part($s("lst"),C5)),ListQ($s("lst")))),FreeQ(a,x))),
ISetDelayed(81,GeneralizedTrinomialParts(u_,x_Symbol),
    Condition(With(list(Set($s("lst"),Expon(u,x,List))),If(And(Equal(Length($s("lst")),C3),NeQ(Part($s("lst"),C1),C0),EqQ(Part($s("lst"),C3),Subtract(Times(C2,Part($s("lst"),C2)),Part($s("lst"),C1)))),List(Coeff(u,x,Part($s("lst"),C1)),Coeff(u,x,Part($s("lst"),C2)),Coeff(u,x,Part($s("lst"),C3)),Part($s("lst"),C2),Part($s("lst"),C1)),False)),PolyQ(u,x))),
ISetDelayed(82,GeneralizedTrinomialParts(Times(u_,Power(x_,m_DEFAULT)),x_Symbol),
    Condition(With(list(Set($s("lst"),GeneralizedTrinomialParts(u,x))),Condition(List(Part($s("lst"),C1),Part($s("lst"),C2),Part($s("lst"),C3),Plus(m,Part($s("lst"),C4)),Plus(m,Part($s("lst"),C5))),And(ListQ($s("lst")),NeQ(Plus(m,Part($s("lst"),C4)),C0),NeQ(Plus(m,Part($s("lst"),C5)),C0)))),FreeQ(m,x))),
ISetDelayed(83,GeneralizedTrinomialParts(Times(u_,Power(x_,m_DEFAULT)),x_Symbol),
    Condition(With(list(Set($s("lst"),TrinomialParts(u,x))),Condition(List(Part($s("lst"),C1),Part($s("lst"),C2),Part($s("lst"),C3),Plus(m,Part($s("lst"),C4)),m),And(ListQ($s("lst")),NeQ(Plus(m,Part($s("lst"),C4)),C0)))),FreeQ(m,x))),
ISetDelayed(84,GeneralizedTrinomialParts(u_,x_Symbol),
          False)
  );
}
