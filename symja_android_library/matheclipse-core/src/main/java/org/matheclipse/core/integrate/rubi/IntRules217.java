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
class IntRules217 { 
  public static IAST RULES = List( 
IIntegrate(4341,Integrate(Times(u_,Tanh(Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Cosh(Times(c,Plus(a,Times(b,x)))),x))),Condition(Dist(Power(Times(b,c),CN1),Subst(Integrate(SubstFor(Power(x,CN1),Times(Cosh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Cosh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1))),x),FunctionOfQ(Times(Cosh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x,True))),FreeQ(list(a,b,c),x))),
IIntegrate(4342,Integrate(Times(u_,Sqr($(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Tan(Times(c,Plus(a,Times(b,x)))),x))),Condition(Dist(Times(d,Power(Times(b,c),CN1)),Subst(Integrate(SubstFor(C1,Times(Tan(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Tan(Times(c,Plus(a,Times(b,x)))),Power(d,CN1))),x),FunctionOfQ(Times(Tan(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x,True))),And(FreeQ(list(a,b,c),x),NonsumQ(u),Or(EqQ(FSymbol,Sec),EqQ(FSymbol,$s("§sec")))))),
IIntegrate(4343,Integrate(Times(Power($($s("§cos"),Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),CN2),u_),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Tan(Times(c,Plus(a,Times(b,x)))),x))),Condition(Dist(Times(d,Power(Times(b,c),CN1)),Subst(Integrate(SubstFor(C1,Times(Tan(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Tan(Times(c,Plus(a,Times(b,x)))),Power(d,CN1))),x),FunctionOfQ(Times(Tan(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x,True))),And(FreeQ(list(a,b,c),x),NonsumQ(u)))),
IIntegrate(4344,Integrate(Times(u_,Sqr($(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Cot(Times(c,Plus(a,Times(b,x)))),x))),Condition(Negate(Dist(Times(d,Power(Times(b,c),CN1)),Subst(Integrate(SubstFor(C1,Times(Cot(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Cot(Times(c,Plus(a,Times(b,x)))),Power(d,CN1))),x)),FunctionOfQ(Times(Cot(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x,True))),And(FreeQ(list(a,b,c),x),NonsumQ(u),Or(EqQ(FSymbol,Csc),EqQ(FSymbol,$s("§csc")))))),
IIntegrate(4345,Integrate(Times(u_,Power($($s("§sin"),Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),CN2)),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Cot(Times(c,Plus(a,Times(b,x)))),x))),Condition(Negate(Dist(Times(d,Power(Times(b,c),CN1)),Subst(Integrate(SubstFor(C1,Times(Cot(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Cot(Times(c,Plus(a,Times(b,x)))),Power(d,CN1))),x)),FunctionOfQ(Times(Cot(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x,True))),And(FreeQ(list(a,b,c),x),NonsumQ(u)))),
IIntegrate(4346,Integrate(Times(u_,Sqr(Sech(Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Tanh(Times(c,Plus(a,Times(b,x)))),x))),Condition(Dist(Times(d,Power(Times(b,c),CN1)),Subst(Integrate(SubstFor(C1,Times(Tanh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Tanh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1))),x),FunctionOfQ(Times(Tanh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x,True))),And(FreeQ(list(a,b,c),x),NonsumQ(u)))),
IIntegrate(4347,Integrate(Times(Sqr(Csch(Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),u_),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Coth(Times(c,Plus(a,Times(b,x)))),x))),Condition(Negate(Dist(Times(d,Power(Times(b,c),CN1)),Subst(Integrate(SubstFor(C1,Times(Coth(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Coth(Times(c,Plus(a,Times(b,x)))),Power(d,CN1))),x)),FunctionOfQ(Times(Coth(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x,True))),And(FreeQ(list(a,b,c),x),NonsumQ(u)))),
IIntegrate(4348,Integrate(Times(u_,Power($(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Tan(Times(c,Plus(a,Times(b,x)))),x))),Condition(Dist(Power(Times(b,c,Power(d,Subtract(n,C1))),CN1),Subst(Integrate(SubstFor(Power(Times(Power(x,n),Plus(C1,Times(Sqr(d),Sqr(x)))),CN1),Times(Tan(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Tan(Times(c,Plus(a,Times(b,x)))),Power(d,CN1))),x),And(FunctionOfQ(Times(Tan(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x,True),TryPureTanSubst(Times(ActivateTrig(u),Power(Cot(Times(c,Plus(a,Times(b,x)))),n)),x)))),And(FreeQ(list(a,b,c),x),IntegerQ(n),Or(EqQ(FSymbol,Cot),EqQ(FSymbol,$s("§cot")))))),
IIntegrate(4349,Integrate(Times(u_,Power($(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Cot(Times(c,Plus(a,Times(b,x)))),x))),Condition(Negate(Dist(Power(Times(b,c,Power(d,Subtract(n,C1))),CN1),Subst(Integrate(SubstFor(Power(Times(Power(x,n),Plus(C1,Times(Sqr(d),Sqr(x)))),CN1),Times(Cot(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Cot(Times(c,Plus(a,Times(b,x)))),Power(d,CN1))),x)),And(FunctionOfQ(Times(Cot(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x,True),TryPureTanSubst(Times(ActivateTrig(u),Power(Tan(Times(c,Plus(a,Times(b,x)))),n)),x)))),And(FreeQ(list(a,b,c),x),IntegerQ(n),Or(EqQ(FSymbol,Tan),EqQ(FSymbol,$s("§tan")))))),
IIntegrate(4350,Integrate(Times(Power(Coth(Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT),u_),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Tanh(Times(c,Plus(a,Times(b,x)))),x))),Condition(Dist(Power(Times(b,c,Power(d,Subtract(n,C1))),CN1),Subst(Integrate(SubstFor(Power(Times(Power(x,n),Subtract(C1,Times(Sqr(d),Sqr(x)))),CN1),Times(Tanh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Tanh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1))),x),And(FunctionOfQ(Times(Tanh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x,True),TryPureTanSubst(Times(ActivateTrig(u),Power(Coth(Times(c,Plus(a,Times(b,x)))),n)),x)))),And(FreeQ(list(a,b,c),x),IntegerQ(n)))),
IIntegrate(4351,Integrate(Times(u_,Power(Tanh(Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Coth(Times(c,Plus(a,Times(b,x)))),x))),Condition(Dist(Power(Times(b,c,Power(d,Subtract(n,C1))),CN1),Subst(Integrate(SubstFor(Power(Times(Power(x,n),Subtract(C1,Times(Sqr(d),Sqr(x)))),CN1),Times(Coth(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Coth(Times(c,Plus(a,Times(b,x)))),Power(d,CN1))),x),And(FunctionOfQ(Times(Coth(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x,True),TryPureTanSubst(Times(ActivateTrig(u),Power(Tanh(Times(c,Plus(a,Times(b,x)))),n)),x)))),And(FreeQ(list(a,b,c),x),IntegerQ(n)))),
IIntegrate(4352,Integrate(u_,x_Symbol),
    With(list(Set(v,FunctionOfTrig(u,x))),Condition(Simp(With(list(Set(d,FreeFactors(Cot(v),x))),Dist(Times(CN1,d,Power(Coefficient(v,x,C1),CN1)),Subst(Integrate(SubstFor(Power(Plus(C1,Times(Sqr(d),Sqr(x))),CN1),Times(Cot(v),Power(d,CN1)),u,x),x),x,Times(Cot(v),Power(d,CN1))),x)),x),And(Not(FalseQ(v)),FunctionOfQ(NonfreeFactors(Cot(v),x),u,x,True),TryPureTanSubst(ActivateTrig(u),x))))),
IIntegrate(4353,Integrate(u_,x_Symbol),
    With(list(Set(v,FunctionOfTrig(u,x))),Condition(Simp(With(list(Set(d,FreeFactors(Tan(v),x))),Dist(Times(d,Power(Coefficient(v,x,C1),CN1)),Subst(Integrate(SubstFor(Power(Plus(C1,Times(Sqr(d),Sqr(x))),CN1),Times(Tan(v),Power(d,CN1)),u,x),x),x,Times(Tan(v),Power(d,CN1))),x)),x),And(Not(FalseQ(v)),FunctionOfQ(NonfreeFactors(Tan(v),x),u,x,True),TryPureTanSubst(ActivateTrig(u),x))))),
IIntegrate(4354,Integrate(Times(Power($(F_,Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT),Power($(G_,Plus(c_DEFAULT,Times(d_DEFAULT,x_))),q_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(ActivateTrig(Times(Power(F(Plus(a,Times(b,x))),p),Power(G(Plus(c,Times(d,x))),q))),x),x),And(FreeQ(List(a,b,c,d),x),Or(EqQ(FSymbol,$s("§sin")),EqQ(FSymbol,$s("§cos"))),Or(EqQ(GSymbol,$s("§sin")),EqQ(GSymbol,$s("§cos"))),IGtQ(p,C0),IGtQ(q,C0)))),
IIntegrate(4355,Integrate(Times(Power($(F_,Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT),Power($(G_,Plus(c_DEFAULT,Times(d_DEFAULT,x_))),q_DEFAULT),Power($($p("H"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),r_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(ActivateTrig(Times(Power(F(Plus(a,Times(b,x))),p),Power(G(Plus(c,Times(d,x))),q),Power(H(Plus(e,Times(f,x))),r))),x),x),And(FreeQ(List(a,b,c,d,e,f),x),Or(EqQ(FSymbol,$s("§sin")),EqQ(FSymbol,$s("§cos"))),Or(EqQ(GSymbol,$s("§sin")),EqQ(GSymbol,$s("§cos"))),Or(EqQ($s("H"),$s("§sin")),EqQ($s("H"),$s("§cos"))),IGtQ(p,C0),IGtQ(q,C0),IGtQ(r,C0)))),
IIntegrate(4356,Integrate(Times(u_,$(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Sin(Times(c,Plus(a,Times(b,x)))),x))),Condition(Dist(Times(d,Power(Times(b,c),CN1)),Subst(Integrate(SubstFor(C1,Times(Sin(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Sin(Times(c,Plus(a,Times(b,x)))),Power(d,CN1))),x),FunctionOfQ(Times(Sin(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x))),And(FreeQ(list(a,b,c),x),Or(EqQ(FSymbol,Cos),EqQ(FSymbol,$s("§cos")))))),
IIntegrate(4357,Integrate(Times(u_,$(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Cos(Times(c,Plus(a,Times(b,x)))),x))),Condition(Negate(Dist(Times(d,Power(Times(b,c),CN1)),Subst(Integrate(SubstFor(C1,Times(Cos(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Cos(Times(c,Plus(a,Times(b,x)))),Power(d,CN1))),x)),FunctionOfQ(Times(Cos(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x))),And(FreeQ(list(a,b,c),x),Or(EqQ(FSymbol,Sin),EqQ(FSymbol,$s("§sin")))))),
IIntegrate(4358,Integrate(Times(Cosh(Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),u_),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Sinh(Times(c,Plus(a,Times(b,x)))),x))),Condition(Dist(Times(d,Power(Times(b,c),CN1)),Subst(Integrate(SubstFor(C1,Times(Sinh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Sinh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1))),x),FunctionOfQ(Times(Sinh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x))),FreeQ(list(a,b,c),x))),
IIntegrate(4359,Integrate(Times(u_,Sinh(Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Cosh(Times(c,Plus(a,Times(b,x)))),x))),Condition(Dist(Times(d,Power(Times(b,c),CN1)),Subst(Integrate(SubstFor(C1,Times(Cosh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Cosh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1))),x),FunctionOfQ(Times(Cosh(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x))),FreeQ(list(a,b,c),x))),
IIntegrate(4360,Integrate(Times(u_,$(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(With(list(Set(d,FreeFactors(Sin(Times(c,Plus(a,Times(b,x)))),x))),Condition(Dist(Power(Times(b,c),CN1),Subst(Integrate(SubstFor(Power(x,CN1),Times(Sin(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x),x),x,Times(Sin(Times(c,Plus(a,Times(b,x)))),Power(d,CN1))),x),FunctionOfQ(Times(Sin(Times(c,Plus(a,Times(b,x)))),Power(d,CN1)),u,x))),And(FreeQ(list(a,b,c),x),Or(EqQ(FSymbol,Cot),EqQ(FSymbol,$s("§cot"))))))
  );
}
