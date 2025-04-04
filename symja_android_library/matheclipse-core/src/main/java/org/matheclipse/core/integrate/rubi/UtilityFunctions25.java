package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class UtilityFunctions25 { 
  public static IAST RULES = List( 
ISetDelayed(436,SquareRootOfQuadraticSubst(u_,$p("vv"),$p("xx"),x_Symbol),
    If(Or(AtomQ(u),FreeQ(u,x)),If(SameQ(u,x),$s("xx"),u),If(And(PowerQ(u),FreeQ(Part(u,C2),x)),If(And(FractionQ(Part(u,C2)),Equal(Denominator(Part(u,C2)),C2),PolynomialQ(Part(u,C1),x),Equal(Exponent(Part(u,C1),x),C2)),Power($s("vv"),Numerator(Part(u,C2))),Power(SquareRootOfQuadraticSubst(Part(u,C1),$s("vv"),$s("xx"),x),Part(u,C2))),Map(Function(SquareRootOfQuadraticSubst(Slot1,$s("vv"),$s("xx"),x)),u)))),
ISetDelayed(437,Subst(u_,x_Symbol,v_),
    If(And(PowerQ(v),Not(IntegerQ(Part(v,C2))),MatchQ(Part(v,C1),Condition(Plus(a_,Times(b_DEFAULT,x),Times(c_DEFAULT,Sqr(x))),And(FreeQ(list(a,b,c),x),Not(AtomQ(b))))),Less(LeafCount(Simplify(Part(v,C1))),Times(QQ(2L,3L),LeafCount(Part(v,C1))))),Subst(u,x,Power(Simplify(Part(v,C1)),Part(v,C2))),If(SumQ(u),If(BinomialQ(v,x),SimplifyAntiderivative(Map(Function(SubstAux(Slot1,x,v,True)),u),x),SimplifyAntiderivative(Map(Function(SubstAux(Slot1,x,v,False)),u),x)),SimplifyAntiderivative(SubstAux(u,x,v,BinomialQ(v,x)),x)))),
ISetDelayed(438,Subst(u_,Rule(x_Symbol,v_)),
    Subst(u,x,v)),
ISetDelayed(439,Subst(u_,Power(Times(a_DEFAULT,x_),n_),v_),
    Condition(If(AtomQ(u),u,If(And(RationalQ(n),Unequal(Numerator(n),C1)),Subst(u,Power(Times(a,x),Power(Denominator(n),CN1)),Times(v,Power(Power(Times(a,x),Subtract(n,Power(Denominator(n),CN1))),CN1))),If(And(PowerQ(u),FreeQ(Part(u,C2),x),SameQ(Part(u,C1),Times(a,x))),If(IntegerQ(Times(Part(u,C2),Power(n,CN1))),Simplify(Power(v,Times(Part(u,C2),Power(n,CN1)))),If(SumQ(Part(u,C2)),Apply(Times,Map(Function(Subst(Power(Part(u,C1),Slot1),Power(Times(a,x),n),v)),Apply(List,Part(u,C2)))),With(list(Set(w,Expand(Part(u,C2)))),If(SumQ(w),Apply(Times,Map(Function(Subst(Power(Part(u,C1),Slot1),Power(Times(a,x),n),v)),Apply(List,w))),With(list(Set(m,NumericFactor(Part(u,C2)))),If(Unequal(Numerator(m),C1),Power(Subst(Power(Part(u,C1),Times(m,Power(Numerator(m),CN1),NonnumericFactors(Part(u,C2)))),Power(Times(a,x),n),v),Numerator(m)),Power(Subst(Part(u,C1),Power(Times(a,x),n),v),Part(u,C2)))))))),If(Or(And(CalculusQ(u),Not(FreeQ(x,Part(u,C2)))),And(HeldFormQ(u),UnsameQ(Head(u),Defer(AppellF1)))),$(Defer($rubi("Subst")),u,Power(Times(a,x),n),v),Map(Function(Subst(Slot1,Power(Times(a,x),n),v)),u))))),FreeQ(list(a,n),x))),
ISetDelayed(440,Subst(u_,v_,w_),
    If(SameQ(u,v),w,If(AtomQ(u),u,If(Or(And(CalculusQ(u),Not(FreeQ(v,Part(u,C2)))),And(HeldFormQ(u),UnsameQ(Head(u),Defer(AppellF1)))),$(Defer($rubi("Subst")),u,v,w),Map(Function(Subst(Slot1,v,w)),u))))),
ISetDelayed(441,SubstAux(Plus(a_,Times(b_DEFAULT,x_)),x_,Times(Sqr($(F_,z_)),c_DEFAULT),False),
    Condition(Times(a,Simplify(Subtract(C1,Sqr(F(z))))),And(FreeQ(list(a,b,c),x),MemberQ(List(Sin,Cos,Sec,Csc,Cosh,Tanh,Coth,Sech),FSymbol),EqQ(Plus(a,Times(b,c)),C0)))),
ISetDelayed(442,SubstAux(Plus(a_,Times(b_DEFAULT,x_)),x_,Times(Sqr($(F_,z_)),c_DEFAULT),False),
    Condition(Times(a,Simplify(Plus(C1,Sqr(F(z))))),And(FreeQ(list(a,b,c),x),MemberQ(List(Tan,Cot,Sinh,Csch),FSymbol),EqQ(Subtract(a,Times(b,c)),C0)))),
ISetDelayed(443,SubstAux(Plus(a_,Times(b_DEFAULT,Sqr(x_))),x_,Times($(F_,z_),c_DEFAULT),False),
    Condition(Times(a,Simplify(Subtract(C1,Sqr(F(z))))),And(FreeQ(list(a,b,c),x),MemberQ(List(Sin,Cos,Sec,Csc,Cosh,Tanh,Coth,Sech),FSymbol),EqQ(Plus(a,Times(b,Sqr(c))),C0)))),
ISetDelayed(444,SubstAux(Plus(a_,Times(b_DEFAULT,Sqr(x_))),x_,Times($(F_,z_),c_DEFAULT),False),
    Condition(Times(a,Simplify(Plus(C1,Sqr(F(z))))),And(FreeQ(list(a,b,c),x),MemberQ(List(Tan,Cot,Sinh,Csch),FSymbol),EqQ(Subtract(a,Times(b,Sqr(c))),C0)))),
ISetDelayed(445,SubstAux($(F_,Times(a_DEFAULT,Power(x_,m_DEFAULT))),x_,Times(b_DEFAULT,Power(x_,n_)),$p("flag")),
    Condition($(Switch(FSymbol,ArcSin,ArcCsc,ArcCos,ArcSec,ArcTan,ArcCot,ArcCot,ArcTan,ArcSec,ArcCos,ArcCsc,ArcSin,ArcSinh,ArcCsch,ArcCosh,ArcSech,ArcTanh,ArcCoth,ArcCoth,ArcTanh,ArcSech,ArcCosh,ArcCsch,ArcSinh),Times(Power(x,Times(CN1,m,n)),Power(Times(a,Power(b,m)),CN1))),And(FreeQ(list(a,b),x),IGtQ(m,C0),ILtQ(n,C0),MemberQ(List(ArcSin,ArcCos,ArcTan,ArcCot,ArcSec,ArcCsc,ArcSinh,ArcCosh,ArcTanh,ArcCoth,ArcSech,ArcCsch),FSymbol)))),
ISetDelayed(446,SubstAux(u_,x_,v_,$p("flag")),
    If(AtomQ(u),If(SameQ(u,x),v,u),If(FreeQ(u,x),u,If(And($s("flag"),PowerQ(u)),If(And(Not(IntegerQ(Part(u,C2))),LinearQ(Part(u,C1),x)),Power(Simplify(SubstAux(Part(u,C1),x,v,$s("flag"))),SubstAux(Part(u,C2),x,v,$s("flag"))),Power(SubstAux(Part(u,C1),x,v,$s("flag")),SubstAux(Part(u,C2),x,v,$s("flag")))),If(SameQ(Head(u),Defer($rubi("Subst"))),If(Or(SameQ(Part(u,C2),x),FreeQ(Part(u,C1),x)),SubstAux(Part(u,C1),Part(u,C2),SubstAux(Part(u,C3),x,v,$s("flag")),$s("flag")),$(Defer($rubi("Subst")),u,x,v)),If(SameQ(Head(u),Defer($rubi("Dist"))),$(Defer($rubi("Dist")),SubstAux(Part(u,C1),x,v,$s("flag")),SubstAux(Part(u,C2),x,v,$s("flag")),Part(u,C3)),If(And($s("§simplifyflag"),MemberQ(list($rubi("Unintegrable"),$rubi("CannotIntegrate")),Head(u)),SameQ(Part(u,C2),x)),With(list(Set(w,Simplify(D(v,x)))),Times(FreeFactors(w,x),$(Head(u),Times(Subst(Part(u,C1),x,v),NonfreeFactors(w,x)),x))),If(Or(And(CalculusQ(u),Not(FreeQ(x,Part(u,C2)))),And(HeldFormQ(u),UnsameQ(Head(u),Defer(AppellF1)))),$(Defer($rubi("Subst")),u,x,v),If(And($s("flag"),Equal(Length(u),C1),LinearQ(Part(u,C1),x)),$(Head(u),Simplify(SubstAux(Part(u,C1),x,v,$s("flag")))),If(And($s("flag"),SameQ(Head(u),PolyLog),Equal(Length(u),C2),LinearQ(Part(u,C2),x)),PolyLog(SubstAux(Part(u,C1),x,v,$s("flag")),Simplify(SubstAux(Part(u,C2),x,v,$s("flag")))),With(list(Set(w,Map(Function(SubstAux(Slot1,x,v,$s("flag"))),u))),If(PolyQ(w,x),With(list(Set(z,If(Or(LinearQ(v,x),MonomialQ(v,x)),ExpandToSum(w,x),Simplify(w)))),If(LessEqual(LeafCount(z),Times(If(LinearQ(u,x),QQ(3L,4L),QQ(9L,10L)),LeafCount(w))),If(EqQ(Sqr(NumericFactor(z)),C1),z,Times(NumericFactor(z),NonnumericFactors(z))),If(EqQ(Sqr(NumericFactor(w)),C1),w,Times(NumericFactor(w),NonnumericFactors(w))))),With(list(Set($s("ulst"),BinomialParts(u,x))),If(And(Not(FalseQ($s("ulst"))),IGtQ(Part($s("ulst"),C3),C0),NeQ(Part($s("ulst"),C1),C0),Or(NeQ(Part($s("ulst"),C3),C1),AlgebraicFunctionQ(v,x))),With(list(Set(z,Simplify(w))),If(Less(LeafCount(z),Times(QQ(9L,10L),LeafCount(w))),z,w)),w)))))))))))))),
ISetDelayed(447,SimplifyAntiderivative(Times(c_,u_),x_Symbol),
    Condition(With(list(Set(v,SimplifyAntiderivative(u,x))),If(And(SumQ(v),NonsumQ(u)),Map(Function(Times(c,Slot1)),v),Times(c,v))),FreeQ(c,x))),
ISetDelayed(448,SimplifyAntiderivative(Log(Times(c_,u_)),x_Symbol),
    Condition(SimplifyAntiderivative(Log(u),x),FreeQ(c,x))),
ISetDelayed(449,SimplifyAntiderivative(Log(Power(u_,n_)),x_Symbol),
    Condition(Times(n,SimplifyAntiderivative(Log(u),x)),FreeQ(n,x))),
ISetDelayed(450,SimplifyAntiderivative($(F_,$(G_,u_)),x_Symbol),
    Condition(Negate(SimplifyAntiderivative(F(Power(G(u),CN1)),x)),And(MemberQ(list(Log,ArcTan,ArcCot),FSymbol),MemberQ(List(Cot,Sec,Csc,Coth,Sech,Csch),GSymbol)))),
ISetDelayed(451,SimplifyAntiderivative($(F_,$(G_,u_)),x_Symbol),
    Condition(SimplifyAntiderivative(F(Power(G(u),CN1)),x),And(MemberQ(list(ArcTanh,ArcCoth),FSymbol),MemberQ(List(Cot,Sec,Csc,Coth,Sech,Csch),GSymbol)))),
ISetDelayed(452,SimplifyAntiderivative(Log($(F_,u_)),x_Symbol),
    Condition(Negate(SimplifyAntiderivative(Log(Power(F(u),CN1)),x)),MemberQ(List(Cot,Sec,Csc,Coth,Sech,Csch),FSymbol))),
ISetDelayed(453,SimplifyAntiderivative(Log(Power(f_,u_)),x_Symbol),
    Condition(Times(Log(f),SimplifyAntiderivative(u,x)),FreeQ(f,x))),
ISetDelayed(454,SimplifyAntiderivative(Log(Plus(a_,Times(Tan(u_),b_DEFAULT))),x_Symbol),
    Condition(Subtract(Times(b,Power(a,CN1),SimplifyAntiderivative(u,x)),SimplifyAntiderivative(Log(Cos(u)),x)),And(FreeQ(list(a,b),x),EqQ(Plus(Sqr(a),Sqr(b)),C0))))
  );
}
