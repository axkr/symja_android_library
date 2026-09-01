package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IAST;
import com.google.common.base.Supplier;

/** 
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class IntRules242 { 
  public static IAST RULES = List( 
IIntegrate(4841,Integrate(Power(Plus(Times($($s("§cos"),Times(n_DEFAULT,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),b_DEFAULT),Times(a_DEFAULT,$($s("§sin"),Times(m_DEFAULT,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),p_),x_Symbol),
    Condition(Simp(Dist(Power(d,CN1),Subst(Integrate(Times(Power(Simplify(TrigExpand(Plus(Times(a,Sin(Times(m,ArcSin(x)))),Times(b,Cos(Times(n,ArcSin(x))))))),p),Power(Subtract(C1,Sqr(x)),CN1D2)),x),x,Sin(Plus(c,Times(d,x)))),x),x),And(FreeQ(List(a,b,c,d),x),ILtQ(Times(C1D2,Plus(p,CN1)),C0),IntegerQ(Times(C1D2,m)),IntegerQ(Times(C1D2,Plus(n,CN1)))))),
IIntegrate(4842,Integrate(Power(Plus(Times($($s("§cos"),Times(n_DEFAULT,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),b_DEFAULT),Times(a_DEFAULT,$($s("§sin"),Times(m_DEFAULT,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),p_),x_Symbol),
    Condition(Simp(Dist(Times(C2,Power(d,CN1)),Subst(Integrate(Times(Power(Simplify(TrigExpand(Plus(Times(a,Sin(Times(C2,m,ArcTan(x)))),Times(b,Cos(Times(C2,n,ArcTan(x))))))),p),Power(Plus(C1,Sqr(x)),CN1)),x),x,Tan(Times(C1D2,Plus(c,Times(d,x))))),x),x),And(FreeQ(List(a,b,c,d),x),ILtQ(p,C0),IntegerQ(m),IntegerQ(n)))),
IIntegrate(4843,Integrate(Power(Plus(Times(a_DEFAULT,$($s("§sin"),u_)),Times(b_DEFAULT,$($s("§sin"),v_))),p_),x_Symbol),
    Condition(With(list(Set(m,Denominator(Times(f,Power(d,CN1))))),Integrate(Power(Plus(Times(a,Sin(Times(m,Plus(Times(c,Power(m,CN1)),Times(d,x,Power(m,CN1)))))),Times(b,Sin(Times(m,f,Power(d,CN1),Plus(Times(c,Power(m,CN1)),Times(d,x,Power(m,CN1))))))),p),x)),And(FreeQ(list(a,b),x),LinearQ(list(u,v),x),ILtQ(p,C0),EqQ(Subtract(Times(d,e),Times(c,f)),C0),RationalQ(Times(f,Power(d,CN1)))))),
IIntegrate(4844,Integrate(Power(Times(a_DEFAULT,Power($(F_,Plus(c_DEFAULT,Times(d_DEFAULT,x_))),p_)),n_),x_Symbol),
    Condition(With(list(Set(v,ActivateTrig(F(Plus(c,Times(d,x)))))),Simp(Dist(Times(Power(a,IntPart(n)),Power(Times(v,Power(NonfreeFactors(v,x),CN1)),Times(p,IntPart(n))),Power(Times(a,Power(v,p)),FracPart(n)),Power(Power(NonfreeFactors(v,x),Times(p,FracPart(n))),CN1)),Integrate(Power(NonfreeFactors(v,x),Times(n,p)),x),x),x)),And(FreeQ(List(a,c,d,n,p),x),InertTrigQ(FSymbol),Not(IntegerQ(n)),IntegerQ(p)))),
IIntegrate(4845,Integrate(Power(Times(a_DEFAULT,Power(Times(b_DEFAULT,$(F_,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),p_)),n_DEFAULT),x_Symbol),
    Condition(With(list(Set(v,ActivateTrig(F(Plus(c,Times(d,x)))))),Simp(Dist(Times(Power(a,IntPart(n)),Power(Times(a,Power(Times(b,v),p)),FracPart(n)),Power(Power(Times(b,v),Times(p,FracPart(n))),CN1)),Integrate(Power(Times(b,v),Times(n,p)),x),x),x)),And(FreeQ(List(a,b,c,d,n,p),x),InertTrigQ(FSymbol),Not(IntegerQ(n)),Not(IntegerQ(p))))),
IIntegrate(4846,Integrate(Times(u_,$(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Sin(Times(c,Plus(a,Times(b,x)))),x))),Condition(Simp(Dist(Times(d,Power(Times(b,c),CN1)),Subst(Integrate(SubstFor(C1,Times(Sin(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Sin(Times(c,Plus(a,Times(b,x)))),Power(d,CN1))),x),x),FunctionOfQ(Times(Sin(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x,True))),And(FreeQ(list(a,b,c),x),Or(EqQ(FSymbol,Cos),EqQ(FSymbol,$s("§cos")))))),
IIntegrate(4847,Integrate(Times(u_,$(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Cos(Times(c,Plus(a,Times(b,x)))),x))),Condition(Simp(Dist(Times(CN1,d,Power(Times(b,c),CN1)),Subst(Integrate(SubstFor(C1,Times(Cos(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Cos(Times(c,Plus(a,Times(b,x)))),Power(d,CN1))),x),x),FunctionOfQ(Times(Cos(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x,True))),And(FreeQ(list(a,b,c),x),Or(EqQ(FSymbol,Sin),EqQ(FSymbol,$s("§sin")))))),
IIntegrate(4848,Integrate(Times(Cosh(Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),u_),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Sinh(Times(c,Plus(a,Times(b,x)))),x))),Condition(Simp(Dist(Times(d,Power(Times(b,c),CN1)),Subst(Integrate(SubstFor(C1,Times(Sinh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Sinh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1))),x),x),FunctionOfQ(Times(Sinh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x,True))),FreeQ(list(a,b,c),x))),
IIntegrate(4849,Integrate(Times(u_,Sinh(Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Cosh(Times(c,Plus(a,Times(b,x)))),x))),Condition(Simp(Dist(Times(d,Power(Times(b,c),CN1)),Subst(Integrate(SubstFor(C1,Times(Cosh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Cosh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1))),x),x),FunctionOfQ(Times(Cosh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x,True))),FreeQ(list(a,b,c),x))),
IIntegrate(4850,Integrate(Times(u_,$(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Sin(Times(c,Plus(a,Times(b,x)))),x))),Condition(Simp(Dist(Power(Times(b,c),CN1),Subst(Integrate(SubstFor(Power(x,CN1),Times(Sin(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Sin(Times(c,Plus(a,Times(b,x)))),Power(d,CN1))),x),x),FunctionOfQ(Times(Sin(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x,True))),And(FreeQ(list(a,b,c),x),Or(EqQ(FSymbol,Cot),EqQ(FSymbol,$s("§cot")))))),
IIntegrate(4851,Integrate(Times(u_,$(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Cos(Times(c,Plus(a,Times(b,x)))),x))),Condition(Simp(Dist(Negate(Power(Times(b,c),CN1)),Subst(Integrate(SubstFor(Power(x,CN1),Times(Cos(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Cos(Times(c,Plus(a,Times(b,x)))),Power(d,CN1))),x),x),FunctionOfQ(Times(Cos(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x,True))),And(FreeQ(list(a,b,c),x),Or(EqQ(FSymbol,Tan),EqQ(FSymbol,$s("§tan")))))),
IIntegrate(4852,Integrate(Times(Coth(Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),u_),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Sinh(Times(c,Plus(a,Times(b,x)))),x))),Condition(Simp(Dist(Power(Times(b,c),CN1),Subst(Integrate(SubstFor(Power(x,CN1),Times(Sinh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Sinh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1))),x),x),FunctionOfQ(Times(Sinh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x,True))),FreeQ(list(a,b,c),x))),
IIntegrate(4853,Integrate(Times(u_,Tanh(Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Cosh(Times(c,Plus(a,Times(b,x)))),x))),Condition(Simp(Dist(Power(Times(b,c),CN1),Subst(Integrate(SubstFor(Power(x,CN1),Times(Cosh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Cosh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1))),x),x),FunctionOfQ(Times(Cosh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x,True))),FreeQ(list(a,b,c),x))),
IIntegrate(4854,Integrate(Times(u_,Sqr($(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Tan(Times(c,Plus(a,Times(b,x)))),x))),Condition(Simp(Dist(Times(d,Power(Times(b,c),CN1)),Subst(Integrate(SubstFor(C1,Times(Tan(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Tan(Times(c,Plus(a,Times(b,x)))),Power(d,CN1))),x),x),FunctionOfQ(Times(Tan(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x,True))),And(FreeQ(list(a,b,c),x),NonsumQ(u),Or(EqQ(FSymbol,Sec),EqQ(FSymbol,$s("§sec")))))),
IIntegrate(4855,Integrate(Times(Power($($s("§cos"),Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),CN2),u_),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Tan(Times(c,Plus(a,Times(b,x)))),x))),Condition(Simp(Dist(Times(d,Power(Times(b,c),CN1)),Subst(Integrate(SubstFor(C1,Times(Tan(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Tan(Times(c,Plus(a,Times(b,x)))),Power(d,CN1))),x),x),FunctionOfQ(Times(Tan(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x,True))),And(FreeQ(list(a,b,c),x),NonsumQ(u)))),
IIntegrate(4856,Integrate(Times(u_,Sqr($(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Cot(Times(c,Plus(a,Times(b,x)))),x))),Condition(Simp(Dist(Times(CN1,d,Power(Times(b,c),CN1)),Subst(Integrate(SubstFor(C1,Times(Cot(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Cot(Times(c,Plus(a,Times(b,x)))),Power(d,CN1))),x),x),FunctionOfQ(Times(Cot(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x,True))),And(FreeQ(list(a,b,c),x),NonsumQ(u),Or(EqQ(FSymbol,Csc),EqQ(FSymbol,$s("§csc")))))),
IIntegrate(4857,Integrate(Times(u_,Power($($s("§sin"),Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),CN2)),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Cot(Times(c,Plus(a,Times(b,x)))),x))),Condition(Simp(Dist(Times(CN1,d,Power(Times(b,c),CN1)),Subst(Integrate(SubstFor(C1,Times(Cot(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Cot(Times(c,Plus(a,Times(b,x)))),Power(d,CN1))),x),x),FunctionOfQ(Times(Cot(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x,True))),And(FreeQ(list(a,b,c),x),NonsumQ(u)))),
IIntegrate(4858,Integrate(Times(u_,Sqr(Sech(Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Tanh(Times(c,Plus(a,Times(b,x)))),x))),Condition(Simp(Dist(Times(d,Power(Times(b,c),CN1)),Subst(Integrate(SubstFor(C1,Times(Tanh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Tanh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1))),x),x),FunctionOfQ(Times(Tanh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x,True))),And(FreeQ(list(a,b,c),x),NonsumQ(u)))),
IIntegrate(4859,Integrate(Times(Sqr(Csch(Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),u_),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Coth(Times(c,Plus(a,Times(b,x)))),x))),Condition(Simp(Dist(Times(CN1,d,Power(Times(b,c),CN1)),Subst(Integrate(SubstFor(C1,Times(Coth(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Coth(Times(c,Plus(a,Times(b,x)))),Power(d,CN1))),x),x),FunctionOfQ(Times(Coth(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x,True))),And(FreeQ(list(a,b,c),x),NonsumQ(u)))),
IIntegrate(4860,Integrate(Times(u_,Power($(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Tan(Times(c,Plus(a,Times(b,x)))),x))),Condition(Simp(Dist(Power(Times(b,c,Power(d,Plus(n,CN1))),CN1),Subst(Integrate(SubstFor(Power(Times(Power(x,n),Plus(C1,Times(Sqr(d),Sqr(x)))),CN1),Times(Tan(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Tan(Times(c,Plus(a,Times(b,x)))),Power(d,CN1))),x),x),And(FunctionOfQ(Times(Tan(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x,True),TryPureTanSubst(Times(ActivateTrig(u),Power(Cot(Times(c,Plus(a,Times(b,x)))),n)),x)))),And(FreeQ(list(a,b,c),x),IntegerQ(n),Or(EqQ(FSymbol,Cot),EqQ(FSymbol,$s("§cot"))))))
  );
}
