package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.$str;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.Apart;
import static org.matheclipse.core.expression.F.Append;
import static org.matheclipse.core.expression.F.Apply;
import static org.matheclipse.core.expression.F.AtomQ;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.CoprimeQ;
import static org.matheclipse.core.expression.F.Denominator;
import static org.matheclipse.core.expression.F.Equal;
import static org.matheclipse.core.expression.F.Expand;
import static org.matheclipse.core.expression.F.First;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.Function;
import static org.matheclipse.core.expression.F.GCD;
import static org.matheclipse.core.expression.F.ISetDelayed;
import static org.matheclipse.core.expression.F.If;
import static org.matheclipse.core.expression.F.Less;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Map;
import static org.matheclipse.core.expression.F.Module;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Numerator;
import static org.matheclipse.core.expression.F.OddQ;
import static org.matheclipse.core.expression.F.Optional;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Part;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Rest;
import static org.matheclipse.core.expression.F.SameQ;
import static org.matheclipse.core.expression.F.Select;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Slot1;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Sum;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.Together;
import static org.matheclipse.core.expression.F.Unique;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.b_;
import static org.matheclipse.core.expression.F.list;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.v_DEFAULT;
import static org.matheclipse.core.expression.F.w_DEFAULT;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.Indeterminate;
import static org.matheclipse.core.expression.S.Integer;
import static org.matheclipse.core.expression.S.List;
import static org.matheclipse.core.expression.S.Plus;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.g;
import static org.matheclipse.core.expression.S.k;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.r;
import static org.matheclipse.core.expression.S.s;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.w;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandAlgebraicFunction;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandBinomial;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Expon;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GensymSubst;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegerPowerQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.KernelSubst;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.MakeAssocList;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PolyQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PosQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ProductQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Rt;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SimplifyTerm;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SmartApart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SumQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.UnifySum;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.UnifyTerms;
import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class UtilityFunctions27 { 
  public static IAST RULES = List( 
ISetDelayed(362,ExpandBinomial(a_,b_,m_,n_,u_,x_Symbol),
    If(And(OddQ(Times(n,Power(GCD(m,n),CN1))),PosQ(Times(a,Power(b,CN1)))),With(list(Set(g,GCD(m,n)),Set(r,Numerator(Rt(Times(a,Power(b,CN1)),Times(n,Power(GCD(m,n),CN1))))),Set(s,Denominator(Rt(Times(a,Power(b,CN1)),Times(n,Power(GCD(m,n),CN1)))))),Module(list(k),If(CoprimeQ(Plus(m,g),n),Sum(Times(r,Power(Times(CN1,r,Power(s,CN1)),Times(m,Power(g,CN1))),Power(CN1,Times(CN2,k,m,Power(n,CN1))),Power(Times(a,n,Plus(r,Times(Power(CN1,Times(C2,k,g,Power(n,CN1))),s,Power(u,g)))),CN1)),list(k,C1,Times(n,Power(g,CN1)))),Sum(Times(r,Power(Times(CN1,r,Power(s,CN1)),Times(m,Power(g,CN1))),Power(CN1,Times(C2,k,Plus(m,g),Power(n,CN1))),Power(Times(a,n,Plus(Times(Power(CN1,Times(C2,k,g,Power(n,CN1))),r),Times(s,Power(u,g)))),CN1)),list(k,C1,Times(n,Power(g,CN1))))))),With(list(Set(g,GCD(m,n)),Set(r,Numerator(Rt(Times(CN1,a,Power(b,CN1)),Times(n,Power(GCD(m,n),CN1))))),Set(s,Denominator(Rt(Times(CN1,a,Power(b,CN1)),Times(n,Power(GCD(m,n),CN1)))))),If(Equal(Times(n,Power(g,CN1)),C2),Subtract(Times(s,Power(Times(C2,b,Plus(r,Times(s,Power(u,g)))),CN1)),Times(s,Power(Times(C2,b,Subtract(r,Times(s,Power(u,g)))),CN1))),Module(list(k),If(CoprimeQ(Plus(m,g),n),Sum(Times(r,Power(Times(r,Power(s,CN1)),Times(m,Power(g,CN1))),Power(CN1,Times(CN2,k,m,Power(n,CN1))),Power(Times(a,n,Subtract(r,Times(Power(CN1,Times(C2,k,g,Power(n,CN1))),s,Power(u,g)))),CN1)),list(k,C1,Times(n,Power(g,CN1)))),Sum(Times(r,Power(Times(r,Power(s,CN1)),Times(m,Power(g,CN1))),Power(CN1,Times(C2,k,Plus(m,g),Power(n,CN1))),Power(Times(a,n,Subtract(Times(Power(CN1,Times(C2,k,g,Power(n,CN1))),r),Times(s,Power(u,g)))),CN1)),list(k,C1,Times(n,Power(g,CN1)))))))))),
ISetDelayed(363,SmartApart(u_,x_Symbol),
    With(list(Set($s("alst"),MakeAssocList(u,x))),With(list(Set($s("tmp"),KernelSubst(Apart(GensymSubst(u,x,$s("alst"))),x,$s("alst")))),If(SameQ($s("tmp"),Indeterminate),u,$s("tmp"))))),
ISetDelayed(364,SmartApart(u_,v_,x_Symbol),
    With(list(Set($s("alst"),MakeAssocList(u,x))),With(list(Set($s("tmp"),KernelSubst(Apart(GensymSubst(u,x,$s("alst")),v),x,$s("alst")))),If(SameQ($s("tmp"),Indeterminate),u,$s("tmp"))))),
ISetDelayed(365,MakeAssocList(u_,x_Symbol,Optional($p("alst",List),List())),
    If(AtomQ(u),$s("alst"),If(IntegerPowerQ(u),MakeAssocList(Part(u,C1),x,$s("alst")),If(Or(ProductQ(u),SumQ(u)),MakeAssocList(Rest(u),x,MakeAssocList(First(u),x,$s("alst"))),If(FreeQ(u,x),With(list(Set($s("tmp"),Select($s("alst"),Function(SameQ(Part(Slot1,C2),u)),C1))),If(SameQ($s("tmp"),List()),Append($s("alst"),list(Unique($str("Rubi")),u)),$s("alst"))),$s("alst")))))),
ISetDelayed(366,GensymSubst(u_,x_Symbol,$p("alst",List)),
    If(AtomQ(u),u,If(IntegerPowerQ(u),Power(GensymSubst(Part(u,C1),x,$s("alst")),Part(u,C2)),If(Or(ProductQ(u),SumQ(u)),Map(Function(GensymSubst(Slot1,x,$s("alst"))),u),If(FreeQ(u,x),With(list(Set($s("tmp"),Select($s("alst"),Function(SameQ(Part(Slot1,C2),u)),C1))),If(SameQ($s("tmp"),List()),u,Part($s("tmp"),C1,C1))),u))))),
ISetDelayed(367,KernelSubst(u_,x_Symbol,$p("alst",List)),
    If(AtomQ(u),With(list(Set($s("tmp"),Select($s("alst"),Function(SameQ(Part(Slot1,C1),u)),C1))),If(SameQ($s("tmp"),List()),u,Part($s("tmp"),C1,C2))),If(IntegerPowerQ(u),With(list(Set($s("tmp"),KernelSubst(Part(u,C1),x,$s("alst")))),If(And(Less(Part(u,C2),C0),EqQ($s("tmp"),C0)),Indeterminate,Power($s("tmp"),Part(u,C2)))),If(Or(ProductQ(u),SumQ(u)),Map(Function(KernelSubst(Slot1,x,$s("alst"))),u),u)))),
ISetDelayed(368,$($s("§incrementalexpand"),u_,v_,x_Symbol),
    $($s("§distributeoverterms"),u,$($s("§incrementalexpand"),v,x),x)),
ISetDelayed(369,$($s("§incrementalexpand"),u_,x_Symbol),
    $($s("§apartcollect"),SmartApart(u,x),x)),
ISetDelayed(370,$($s("§apartcollect"),Plus(u_,Times(Power($p("§px"),$p(m, Integer)),v_DEFAULT),Times(Power($p("§px"),$p(n, Integer)),w_DEFAULT)),x_Symbol),
    Condition($($s("§apartcollect"),Plus(u,Together(Plus(Times(v,Power($s("§px"),m)),Times(w,Power($s("§px"),n))))),x),And(PolyQ($s("§px"),x),GtQ(Expon($s("§px"),x),C0),Less(m,C0),Less(n,C0)))),
ISetDelayed(371,$($s("§apartcollect"),u_,x_Symbol),
    u),
ISetDelayed(372,ExpandAlgebraicFunction(Times($p(u,Plus),v_),x_Symbol),
    Condition(Map(Function(Times(Slot1,v)),u),Not(FreeQ(u,x)))),
ISetDelayed(373,ExpandAlgebraicFunction(Times(Power($p(u,Plus),n_),v_DEFAULT),x_Symbol),
    Condition(With(list(Set(w,Expand(Power(u,n),x))),Condition(Map(Function(Times(Slot1,v)),w),SumQ(w))),And(IGtQ(n,C0),Not(FreeQ(u,x))))),
ISetDelayed(374,UnifySum(u_,x_Symbol),
          If(SumQ(u), Apply(Plus, UnifyTerms(Apply(List, u), x)), SimplifyTerm(u, x)))
  );
}
