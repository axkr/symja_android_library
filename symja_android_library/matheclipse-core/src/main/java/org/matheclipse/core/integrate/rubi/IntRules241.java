package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class IntRules241 { 
  public static IAST RULES = List( 
IIntegrate(4821,Integrate(Power(Plus(Times($($s("§cos"),Times(m_DEFAULT,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),a_DEFAULT),Times($($s("§cos"),Times(n_DEFAULT,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),b_DEFAULT)),p_),x_Symbol),
    Condition(Simp(Star(Negate(Power(d,CN1)),Subst(Integrate(Times(Power(Simplify(TrigExpand(Plus(Times(a,Cos(Times(m,ArcCot(x)))),Times(b,Cos(Times(n,ArcCot(x))))))),p),Power(Plus(C1,Sqr(x)),CN1)),x),x,Cot(Plus(c,Times(d,x))))),x),And(FreeQ(List(a,b,c,d),x),ILtQ(p,C0),IntegerQ(Times(C1D2,m)),IntegerQ(Times(C1D2,n))))),
IIntegrate(4822,Integrate(Power(Plus(Times(a_DEFAULT,$($s("§sin"),Times(m_DEFAULT,Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),Times(b_DEFAULT,$($s("§sin"),Times(n_DEFAULT,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),p_),x_Symbol),
    Condition(Simp(Star(Power(d,CN1),Subst(Integrate(Times(Power(Simplify(TrigExpand(Plus(Times(a,Sin(Times(m,ArcTan(x)))),Times(b,Sin(Times(n,ArcTan(x))))))),p),Power(Plus(C1,Sqr(x)),CN1)),x),x,Tan(Plus(c,Times(d,x))))),x),And(FreeQ(List(a,b,c,d),x),ILtQ(Times(C1D2,p),C0),IntegerQ(Times(C1D2,Subtract(m,C1))),IntegerQ(Times(C1D2,Subtract(n,C1)))))),
IIntegrate(4823,Integrate(Power(Plus(Times($($s("§cos"),Times(m_DEFAULT,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),a_DEFAULT),Times($($s("§cos"),Times(n_DEFAULT,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),b_DEFAULT)),p_),x_Symbol),
    Condition(Simp(Star(Power(d,CN1),Subst(Integrate(Times(Power(Simplify(TrigExpand(Plus(Times(a,Cos(Times(m,ArcTan(x)))),Times(b,Cos(Times(n,ArcTan(x))))))),p),Power(Plus(C1,Sqr(x)),CN1)),x),x,Tan(Plus(c,Times(d,x))))),x),And(FreeQ(List(a,b,c,d),x),ILtQ(Times(C1D2,p),C0),IntegerQ(Times(C1D2,Subtract(m,C1))),IntegerQ(Times(C1D2,Subtract(n,C1)))))),
IIntegrate(4824,Integrate(Power(Plus(Times(a_DEFAULT,$($s("§sin"),Times(m_DEFAULT,Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),Times(b_DEFAULT,$($s("§sin"),Times(n_DEFAULT,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),p_),x_Symbol),
    Condition(Simp(Star(Negate(Power(d,CN1)),Subst(Integrate(Times(Power(Simplify(TrigExpand(Plus(Times(a,Sin(Times(m,ArcCos(x)))),Times(b,Sin(Times(n,ArcCos(x))))))),p),Power(Subtract(C1,Sqr(x)),CN1D2)),x),x,Cos(Plus(c,Times(d,x))))),x),And(FreeQ(List(a,b,c,d),x),ILtQ(Times(C1D2,Subtract(p,C1)),C0),IntegerQ(Times(C1D2,Subtract(m,C1))),IntegerQ(Times(C1D2,Subtract(n,C1)))))),
IIntegrate(4825,Integrate(Power(Plus(Times($($s("§cos"),Times(m_DEFAULT,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),a_DEFAULT),Times($($s("§cos"),Times(n_DEFAULT,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),b_DEFAULT)),p_),x_Symbol),
    Condition(Simp(Star(Power(d,CN1),Subst(Integrate(Times(Power(Simplify(TrigExpand(Plus(Times(a,Cos(Times(m,ArcSin(x)))),Times(b,Cos(Times(n,ArcSin(x))))))),p),Power(Subtract(C1,Sqr(x)),CN1D2)),x),x,Sin(Plus(c,Times(d,x))))),x),And(FreeQ(List(a,b,c,d),x),ILtQ(Times(C1D2,Subtract(p,C1)),C0),IntegerQ(Times(C1D2,Subtract(m,C1))),IntegerQ(Times(C1D2,Subtract(n,C1)))))),
IIntegrate(4826,Integrate(Power(Plus(Times(a_DEFAULT,$($s("§sin"),Times(m_DEFAULT,Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),Times(b_DEFAULT,$($s("§sin"),Times(n_DEFAULT,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),p_),x_Symbol),
    Condition(Simp(Star(Times(C2,Power(d,CN1)),Subst(Integrate(Times(Power(Simplify(TrigExpand(Plus(Times(a,Sin(Times(C2,m,ArcTan(x)))),Times(b,Sin(Times(C2,n,ArcTan(x))))))),p),Power(Plus(C1,Sqr(x)),CN1)),x),x,Tan(Times(C1D2,Plus(c,Times(d,x)))))),x),And(FreeQ(List(a,b,c,d),x),ILtQ(p,C0),IntegerQ(Times(C1D2,m)),IntegerQ(Times(C1D2,Subtract(n,C1)))))),
IIntegrate(4827,Integrate(Power(Plus(Times($($s("§cos"),Times(m_DEFAULT,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),a_DEFAULT),Times($($s("§cos"),Times(n_DEFAULT,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),b_DEFAULT)),p_),x_Symbol),
    Condition(Simp(Star(Times(CN2,Power(d,CN1)),Subst(Integrate(Times(Power(Simplify(TrigExpand(Plus(Times(a,Cos(Times(C2,m,ArcCot(x)))),Times(b,Cos(Times(C2,n,ArcCot(x))))))),p),Power(Plus(C1,Sqr(x)),CN1)),x),x,Cot(Times(C1D2,Plus(c,Times(d,x)))))),x),And(FreeQ(List(a,b,c,d),x),ILtQ(p,C0),IntegerQ(Times(C1D2,m)),IntegerQ(Times(C1D2,Subtract(n,C1)))))),
IIntegrate(4828,Integrate(Power(Plus(Times($($s("§cos"),Times(n_DEFAULT,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),b_DEFAULT),Times(a_DEFAULT,$($s("§sin"),Times(m_DEFAULT,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),p_),x_Symbol),
    Condition(Simp(Star(Power(d,CN1),Subst(Integrate(Times(Power(Simplify(TrigExpand(Plus(Times(a,Sin(Times(m,ArcTan(x)))),Times(b,Cos(Times(n,ArcTan(x))))))),p),Power(Plus(C1,Sqr(x)),CN1)),x),x,Tan(Plus(c,Times(d,x))))),x),And(FreeQ(List(a,b,c,d),x),ILtQ(p,C0),IntegerQ(Times(C1D2,m)),IntegerQ(Times(C1D2,n))))),
IIntegrate(4829,Integrate(Power(Plus(Times($($s("§cos"),Times(n_DEFAULT,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),b_DEFAULT),Times(a_DEFAULT,$($s("§sin"),Times(m_DEFAULT,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),p_),x_Symbol),
    Condition(Simp(Star(Power(d,CN1),Subst(Integrate(Times(Power(Simplify(TrigExpand(Plus(Times(a,Sin(Times(m,ArcSin(x)))),Times(b,Cos(Times(n,ArcSin(x))))))),p),Power(Subtract(C1,Sqr(x)),CN1D2)),x),x,Sin(Plus(c,Times(d,x))))),x),And(FreeQ(List(a,b,c,d),x),ILtQ(Times(C1D2,Subtract(p,C1)),C0),IntegerQ(Times(C1D2,m)),IntegerQ(Times(C1D2,Subtract(n,C1)))))),
IIntegrate(4830,Integrate(Power(Plus(Times($($s("§cos"),Times(n_DEFAULT,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),b_DEFAULT),Times(a_DEFAULT,$($s("§sin"),Times(m_DEFAULT,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),p_),x_Symbol),
    Condition(Simp(Star(Times(C2,Power(d,CN1)),Subst(Integrate(Times(Power(Simplify(TrigExpand(Plus(Times(a,Sin(Times(C2,m,ArcTan(x)))),Times(b,Cos(Times(C2,n,ArcTan(x))))))),p),Power(Plus(C1,Sqr(x)),CN1)),x),x,Tan(Times(C1D2,Plus(c,Times(d,x)))))),x),And(FreeQ(List(a,b,c,d),x),ILtQ(p,C0),IntegerQ(m),IntegerQ(n)))),
IIntegrate(4831,Integrate(Power(Plus(Times(a_DEFAULT,$($s("§sin"),u_)),Times(b_DEFAULT,$($s("§sin"),v_))),p_),x_Symbol),
    Condition(With(list(Set(m,Denominator(Times(f,Power(d,CN1))))),Integrate(Power(Plus(Times(a,Sin(Times(m,Plus(Times(c,Power(m,CN1)),Times(d,x,Power(m,CN1)))))),Times(b,Sin(Times(m,f,Power(d,CN1),Plus(Times(c,Power(m,CN1)),Times(d,x,Power(m,CN1))))))),p),x)),And(FreeQ(list(a,b),x),LinearQ(list(u,v),x),ILtQ(p,C0),EqQ(Subtract(Times(d,e),Times(c,f)),C0),RationalQ(Times(f,Power(d,CN1)))))),
IIntegrate(4832,Integrate(Power(Times(a_DEFAULT,Power($(F_,Plus(c_DEFAULT,Times(d_DEFAULT,x_))),p_)),n_),x_Symbol),
    Condition(With(list(Set(v,ActivateTrig(F(Plus(c,Times(d,x)))))),Simp(Star(Times(Power(a,IntPart(n)),Power(Times(v,Power(NonfreeFactors(v,x),CN1)),Times(p,IntPart(n))),Power(Times(a,Power(v,p)),FracPart(n)),Power(Power(NonfreeFactors(v,x),Times(p,FracPart(n))),CN1)),Integrate(Power(NonfreeFactors(v,x),Times(n,p)),x)),x)),And(FreeQ(List(a,c,d,n,p),x),InertTrigQ(FSymbol),Not(IntegerQ(n)),IntegerQ(p)))),
IIntegrate(4833,Integrate(Power(Times(a_DEFAULT,Power(Times(b_DEFAULT,$(F_,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),p_)),n_DEFAULT),x_Symbol),
    Condition(With(list(Set(v,ActivateTrig(F(Plus(c,Times(d,x)))))),Simp(Star(Times(Power(a,IntPart(n)),Power(Times(a,Power(Times(b,v),p)),FracPart(n)),Power(Power(Times(b,v),Times(p,FracPart(n))),CN1)),Integrate(Power(Times(b,v),Times(n,p)),x)),x)),And(FreeQ(List(a,b,c,d,n,p),x),InertTrigQ(FSymbol),Not(IntegerQ(n)),Not(IntegerQ(p))))),
IIntegrate(4834,Integrate(Times(u_,$(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Sin(Times(c,Plus(a,Times(b,x)))),x))),Condition(Simp(Star(Times(d,Power(Times(b,c),CN1)),Subst(Integrate(SubstFor(C1,Times(Sin(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Sin(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)))),x),FunctionOfQ(Times(Sin(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x,True))),And(FreeQ(list(a,b,c),x),Or(EqQ(FSymbol,Cos),EqQ(FSymbol,$s("§cos")))))),
IIntegrate(4835,Integrate(Times(u_,$(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Cos(Times(c,Plus(a,Times(b,x)))),x))),Condition(Simp(Star(Times(CN1,d,Power(Times(b,c),CN1)),Subst(Integrate(SubstFor(C1,Times(Cos(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Cos(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)))),x),FunctionOfQ(Times(Cos(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x,True))),And(FreeQ(list(a,b,c),x),Or(EqQ(FSymbol,Sin),EqQ(FSymbol,$s("§sin")))))),
IIntegrate(4836,Integrate(Times(Cosh(Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),u_),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Sinh(Times(c,Plus(a,Times(b,x)))),x))),Condition(Simp(Star(Times(d,Power(Times(b,c),CN1)),Subst(Integrate(SubstFor(C1,Times(Sinh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Sinh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)))),x),FunctionOfQ(Times(Sinh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x,True))),FreeQ(list(a,b,c),x))),
IIntegrate(4837,Integrate(Times(u_,Sinh(Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Cosh(Times(c,Plus(a,Times(b,x)))),x))),Condition(Simp(Star(Times(d,Power(Times(b,c),CN1)),Subst(Integrate(SubstFor(C1,Times(Cosh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Cosh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)))),x),FunctionOfQ(Times(Cosh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x,True))),FreeQ(list(a,b,c),x))),
IIntegrate(4838,Integrate(Times(u_,$(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Sin(Times(c,Plus(a,Times(b,x)))),x))),Condition(Simp(Star(Power(Times(b,c),CN1),Subst(Integrate(SubstFor(Power(x,CN1),Times(Sin(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Sin(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)))),x),FunctionOfQ(Times(Sin(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x,True))),And(FreeQ(list(a,b,c),x),Or(EqQ(FSymbol,Cot),EqQ(FSymbol,$s("§cot")))))),
IIntegrate(4839,Integrate(Times(u_,$(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Cos(Times(c,Plus(a,Times(b,x)))),x))),Condition(Simp(Star(Negate(Power(Times(b,c),CN1)),Subst(Integrate(SubstFor(Power(x,CN1),Times(Cos(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Cos(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)))),x),FunctionOfQ(Times(Cos(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x,True))),And(FreeQ(list(a,b,c),x),Or(EqQ(FSymbol,Tan),EqQ(FSymbol,$s("§tan")))))),
IIntegrate(4840,Integrate(Times(Coth(Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),u_),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Sinh(Times(c,Plus(a,Times(b,x)))),x))),Condition(Simp(Star(Power(Times(b,c),CN1),Subst(Integrate(SubstFor(Power(x,CN1),Times(Sinh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Sinh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)))),x),FunctionOfQ(Times(Sinh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x,True))),FreeQ(list(a,b,c),x)))
  );
}
