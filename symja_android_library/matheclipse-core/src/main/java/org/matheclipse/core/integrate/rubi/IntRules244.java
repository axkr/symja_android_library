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
class IntRules244 { 
  public static IAST RULES = List( 
IIntegrate(4881,Integrate(Times(u_,Power(Sech(Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Sinh(Times(c,Plus(a,Times(b,x)))),x))),Condition(Simp(Dist(Times(d,Power(Times(b,c),CN1)),Subst(Integrate(SubstFor(Power(Plus(C1,Times(Sqr(d),Sqr(x))),Times(C1D2,Plus(Negate(n),CN1))),Times(Sinh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Sinh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1))),x),x),FunctionOfQ(Times(Sinh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x))),And(FreeQ(list(a,b,c),x),IntegerQ(Times(C1D2,Plus(n,CN1))),NonsumQ(u)))),
IIntegrate(4882,Integrate(Times(u_,Power(Sinh(Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Cosh(Times(c,Plus(a,Times(b,x)))),x))),Condition(Simp(Dist(Times(d,Power(Times(b,c),CN1)),Subst(Integrate(SubstFor(Power(Plus(CN1,Times(Sqr(d),Sqr(x))),Times(C1D2,Plus(n,CN1))),Times(Cosh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Cosh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1))),x),x),FunctionOfQ(Times(Cosh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x))),And(FreeQ(list(a,b,c),x),IntegerQ(Times(C1D2,Plus(n,CN1))),NonsumQ(u)))),
IIntegrate(4883,Integrate(Times(Power(Csch(Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_),u_),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Cosh(Times(c,Plus(a,Times(b,x)))),x))),Condition(Simp(Dist(Times(d,Power(Times(b,c),CN1)),Subst(Integrate(SubstFor(Power(Plus(CN1,Times(Sqr(d),Sqr(x))),Times(C1D2,Plus(Negate(n),CN1))),Times(Cosh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Cosh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1))),x),x),FunctionOfQ(Times(Cosh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x))),And(FreeQ(list(a,b,c),x),IntegerQ(Times(C1D2,Plus(n,CN1))),NonsumQ(u)))),
IIntegrate(4884,Integrate(Times(u_,Power($(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Sin(Times(c,Plus(a,Times(b,x)))),x))),Condition(Simp(Dist(Power(Times(b,c,Power(d,Plus(n,CN1))),CN1),Subst(Integrate(SubstFor(Times(Power(Subtract(C1,Times(Sqr(d),Sqr(x))),Times(C1D2,Plus(n,CN1))),Power(Power(x,n),CN1)),Times(Sin(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Sin(Times(c,Plus(a,Times(b,x)))),Power(d,CN1))),x),x),FunctionOfQ(Times(Sin(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x))),And(FreeQ(list(a,b,c),x),IntegerQ(Times(C1D2,Plus(n,CN1))),NonsumQ(u),Or(EqQ(FSymbol,Cot),EqQ(FSymbol,$s("§cot")))))),
IIntegrate(4885,Integrate(Times(u_,Power($(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Cos(Times(c,Plus(a,Times(b,x)))),x))),Condition(Simp(Dist(Negate(Power(Times(b,c,Power(d,Plus(n,CN1))),CN1)),Subst(Integrate(SubstFor(Times(Power(Subtract(C1,Times(Sqr(d),Sqr(x))),Times(C1D2,Plus(n,CN1))),Power(Power(x,n),CN1)),Times(Cos(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Cos(Times(c,Plus(a,Times(b,x)))),Power(d,CN1))),x),x),FunctionOfQ(Times(Cos(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x))),And(FreeQ(list(a,b,c),x),IntegerQ(Times(C1D2,Plus(n,CN1))),NonsumQ(u),Or(EqQ(FSymbol,Tan),EqQ(FSymbol,$s("§tan")))))),
IIntegrate(4886,Integrate(Times(Power(Coth(Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_),u_),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Sinh(Times(c,Plus(a,Times(b,x)))),x))),Condition(Simp(Dist(Power(Times(b,c,Power(d,Plus(n,CN1))),CN1),Subst(Integrate(SubstFor(Times(Power(Plus(C1,Times(Sqr(d),Sqr(x))),Times(C1D2,Plus(n,CN1))),Power(Power(x,n),CN1)),Times(Sinh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Sinh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1))),x),x),FunctionOfQ(Times(Sinh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x))),And(FreeQ(list(a,b,c),x),IntegerQ(Times(C1D2,Plus(n,CN1))),NonsumQ(u)))),
IIntegrate(4887,Integrate(Times(u_,Power(Tanh(Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Cosh(Times(c,Plus(a,Times(b,x)))),x))),Condition(Simp(Dist(Power(Times(b,c,Power(d,Plus(n,CN1))),CN1),Subst(Integrate(SubstFor(Times(Power(Plus(CN1,Times(Sqr(d),Sqr(x))),Times(C1D2,Plus(n,CN1))),Power(Power(x,n),CN1)),Times(Cosh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Cosh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1))),x),x),FunctionOfQ(Times(Cosh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x))),And(FreeQ(list(a,b,c),x),IntegerQ(Times(C1D2,Plus(n,CN1))),NonsumQ(u)))),
IIntegrate(4888,Integrate(Times(u_,Plus(v_,Times(d_DEFAULT,Power($(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)))),x_Symbol),
    Condition(With(list(Set(e,FreeFactors(Sin(Times(c,Plus(a,Times(b,x)))),x))),Condition(Plus(Integrate(ActivateTrig(Times(u,v)),x),Simp(Dist(d,Integrate(Times(ActivateTrig(u),Power(Cos(Times(c,Plus(a,Times(b,x)))),n)),x),x),x)),FunctionOfQ(Times(Sin(Times(c,Plus(a,Times(b,x)))),Power(e,CN1)),u,x))),And(FreeQ(List(a,b,c,d),x),Not(FreeQ(v,x)),IntegerQ(Times(C1D2,Plus(n,CN1))),NonsumQ(u),Or(EqQ(FSymbol,Cos),EqQ(FSymbol,$s("§cos")))))),
IIntegrate(4889,Integrate(Times(u_,Plus(v_,Times(d_DEFAULT,Power($(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)))),x_Symbol),
    Condition(With(list(Set(e,FreeFactors(Cos(Times(c,Plus(a,Times(b,x)))),x))),Condition(Plus(Integrate(ActivateTrig(Times(u,v)),x),Simp(Dist(d,Integrate(Times(ActivateTrig(u),Power(Sin(Times(c,Plus(a,Times(b,x)))),n)),x),x),x)),FunctionOfQ(Times(Cos(Times(c,Plus(a,Times(b,x)))),Power(e,CN1)),u,x))),And(FreeQ(List(a,b,c,d),x),Not(FreeQ(v,x)),IntegerQ(Times(C1D2,Plus(n,CN1))),NonsumQ(u),Or(EqQ(FSymbol,Sin),EqQ(FSymbol,$s("§sin")))))),
IIntegrate(4890,Integrate(u_,x_Symbol),
    With(list(Set(v,FunctionOfTrig(u,x))),Condition(Simp(With(list(Set(d,FreeFactors(Sin(v),x))),Dist(Times(d,Power(Coefficient(v,x,C1),CN1)),Subst(Integrate(SubstFor(C1,Times(Sin(v),Power(d,CN1)),Times(u,Power(Cos(v),CN1)),x),x),x,Times(Sin(v),Power(d,CN1))),x)),x),And(Not(FalseQ(v)),FunctionOfQ(NonfreeFactors(Sin(v),x),Times(u,Power(Cos(v),CN1)),x))))),
IIntegrate(4891,Integrate(u_,x_Symbol),
    With(list(Set(v,FunctionOfTrig(u,x))),Condition(Simp(With(list(Set(d,FreeFactors(Cos(v),x))),Dist(Times(CN1,d,Power(Coefficient(v,x,C1),CN1)),Subst(Integrate(SubstFor(C1,Times(Cos(v),Power(d,CN1)),Times(u,Power(Sin(v),CN1)),x),x),x,Times(Cos(v),Power(d,CN1))),x)),x),And(Not(FalseQ(v)),FunctionOfQ(NonfreeFactors(Cos(v),x),Times(u,Power(Sin(v),CN1)),x))))),
IIntegrate(4892,Integrate(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(Sqr($($s("§cos"),Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),b_DEFAULT),Times(c_DEFAULT,Sqr($($s("§sin"),Plus(d_DEFAULT,Times(e_DEFAULT,x_)))))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Plus(a,c),p),Integrate(ActivateTrig(u),x),x),x),And(FreeQ(List(a,b,c,d,e,p),x),EqQ(Subtract(b,c),C0)))),
IIntegrate(4893,Integrate(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(c_DEFAULT,Sqr($($s("§sec"),Plus(d_DEFAULT,Times(e_DEFAULT,x_))))),Times(b_DEFAULT,Sqr($($s("§tan"),Plus(d_DEFAULT,Times(e_DEFAULT,x_)))))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Plus(a,c),p),Integrate(ActivateTrig(u),x),x),x),And(FreeQ(List(a,b,c,d,e,p),x),EqQ(Plus(b,c),C0)))),
IIntegrate(4894,Integrate(Times(Power(Plus(a_DEFAULT,Times(Sqr($($s("§cot"),Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),b_DEFAULT),Times(Sqr($($s("§csc"),Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),c_DEFAULT)),p_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Power(Plus(a,c),p),Integrate(ActivateTrig(u),x),x),x),And(FreeQ(List(a,b,c,d,e,p),x),EqQ(Plus(b,c),C0)))),
IIntegrate(4895,Integrate(Times(u_,Power(y_,CN1)),x_Symbol),
    Condition(With(list(Set(q,DerivativeDivides(ActivateTrig(y),ActivateTrig(u),x))),Condition(Simp(Times(q,Log(RemoveContent(ActivateTrig(y),x))),x),Not(FalseQ(q)))),Not(InertTrigFreeQ(u)))),
IIntegrate(4896,Integrate(Times(u_,Power(w_,CN1),Power(y_,CN1)),x_Symbol),
    Condition(With(list(Set(q,DerivativeDivides(ActivateTrig(Times(y,w)),ActivateTrig(u),x))),Condition(Simp(Times(q,Log(RemoveContent(ActivateTrig(Times(y,w)),x))),x),Not(FalseQ(q)))),Not(InertTrigFreeQ(u)))),
IIntegrate(4897,Integrate(Times(u_,Power(y_,m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(q,DerivativeDivides(ActivateTrig(y),ActivateTrig(u),x))),Condition(Simp(Times(q,ActivateTrig(Power(y,Plus(m,C1))),Power(Plus(m,C1),CN1)),x),Not(FalseQ(q)))),And(FreeQ(m,x),NeQ(m,CN1),Not(InertTrigFreeQ(u))))),
IIntegrate(4898,Integrate(Times(u_,Power(y_,m_DEFAULT),Power(z_,n_DEFAULT)),x_Symbol),
    Condition(With(list(Set(q,DerivativeDivides(ActivateTrig(Times(y,z)),ActivateTrig(Times(u,Power(z,Subtract(n,m)))),x))),Condition(Simp(Times(q,ActivateTrig(Times(Power(y,Plus(m,C1)),Power(z,Plus(m,C1)))),Power(Plus(m,C1),CN1)),x),Not(FalseQ(q)))),And(FreeQ(list(m,n),x),NeQ(m,CN1),Not(InertTrigFreeQ(u))))),
IIntegrate(4899,Integrate(Times(u_DEFAULT,Power(Times(a_DEFAULT,Power($(F_,Plus(c_DEFAULT,Times(d_DEFAULT,x_))),p_)),n_)),x_Symbol),
    Condition(With(list(Set(v,ActivateTrig(F(Plus(c,Times(d,x)))))),Simp(Dist(Times(Power(a,IntPart(n)),Power(Times(v,Power(NonfreeFactors(v,x),CN1)),Times(p,IntPart(n))),Power(Times(a,Power(v,p)),FracPart(n)),Power(Power(NonfreeFactors(v,x),Times(p,FracPart(n))),CN1)),Integrate(Times(ActivateTrig(u),Power(NonfreeFactors(v,x),Times(n,p))),x),x),x)),And(FreeQ(List(a,c,d,n,p),x),InertTrigQ(FSymbol),Not(IntegerQ(n)),IntegerQ(p)))),
IIntegrate(4900,Integrate(Times(u_DEFAULT,Power(Times(a_DEFAULT,Power(Times(b_DEFAULT,$(F_,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),p_)),n_DEFAULT)),x_Symbol),
    Condition(With(list(Set(v,ActivateTrig(F(Plus(c,Times(d,x)))))),Simp(Dist(Times(Power(a,IntPart(n)),Power(Times(a,Power(Times(b,v),p)),FracPart(n)),Power(Power(Times(b,v),Times(p,FracPart(n))),CN1)),Integrate(Times(ActivateTrig(u),Power(Times(b,v),Times(n,p))),x),x),x)),And(FreeQ(List(a,b,c,d,n,p),x),InertTrigQ(FSymbol),Not(IntegerQ(n)),Not(IntegerQ(p)))))
  );
}
