package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class IntRules333 { 
  public static IAST RULES = List( 
IIntegrate(6661,Integrate(Times(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$($(Derivative(n_),f_),x_)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Cos(Plus(a,Times(b,x))),$($(Derivative(n),f),x),Power(b,CN1)),x)),Dist(Power(b,CN1),Integrate(Times(Cos(Plus(a,Times(b,x))),$($(Derivative(Plus(n,C1)),f),x)),x),x)),And(FreeQ(List(a,b,f),x),ILtQ(n,C0)))),
IIntegrate(6662,Integrate(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$($(Derivative(n_),f_),x_)),x_Symbol),
    Condition(Subtract(Simp(Times(Sin(Plus(a,Times(b,x))),$($(Derivative(n),f),x),Power(b,CN1)),x),Dist(Power(b,CN1),Integrate(Times(Sin(Plus(a,Times(b,x))),$($(Derivative(Plus(n,C1)),f),x)),x),x)),And(FreeQ(List(a,b,f),x),ILtQ(n,C0)))),
IIntegrate(6663,Integrate(Times(u_,$($(Derivative(n_),f_),x_)),x_Symbol),
    Condition(Subst(Integrate(SimplifyIntegrand(SubstFor($($(Derivative(Subtract(n,C1)),f),x),u,x),x),x),x,$($(Derivative(Subtract(n,C1)),f),x)),And(FreeQ(List(f,n),x),FunctionOfQ($($(Derivative(Subtract(n,C1)),f),x),u,x)))),
IIntegrate(6664,Integrate(Times(u_,Plus(Times(a_DEFAULT,$(g_,x_),$($(Derivative(C1),f_),x_)),Times(a_DEFAULT,$(f_,x_),$($(Derivative(C1),g_),x_)))),x_Symbol),
    Condition(Dist(a,Subst(Integrate(SimplifyIntegrand(SubstFor(Times($(f,x),$(g,x)),u,x),x),x),x,Times($(f,x),$(g,x))),x),And(FreeQ(List(a,f,g),x),FunctionOfQ(Times($(f,x),$(g,x)),u,x)))),
IIntegrate(6665,Integrate(Times(u_,Plus(Times(a_DEFAULT,$(g_,x_),$($(Derivative(m_),f_),x_)),Times(a_DEFAULT,$($(Derivative(C1),g_),x_),$($(Derivative($p("m1")),f_),x_)))),x_Symbol),
    Condition(Dist(a,Subst(Integrate(SimplifyIntegrand(SubstFor(Times($($(Derivative(Subtract(m,C1)),f),x),$(g,x)),u,x),x),x),x,Times($($(Derivative(Subtract(m,C1)),f),x),$(g,x))),x),And(FreeQ(List(a,f,g,m),x),EqQ($s("m1"),Subtract(m,C1)),FunctionOfQ(Times($($(Derivative(Subtract(m,C1)),f),x),$(g,x)),u,x)))),
IIntegrate(6666,Integrate(Times(u_,Plus(Times(a_DEFAULT,$($(Derivative($p("m1")),f_),x_),$($(Derivative(n_),g_),x_)),Times(a_DEFAULT,$($(Derivative(m_),f_),x_),$($(Derivative($p("n1")),g_),x_)))),x_Symbol),
    Condition(Dist(a,Subst(Integrate(SimplifyIntegrand(SubstFor(Times($($(Derivative(Subtract(m,C1)),f),x),$($(Derivative(Subtract(n,C1)),g),x)),u,x),x),x),x,Times($($(Derivative(Subtract(m,C1)),f),x),$($(Derivative(Subtract(n,C1)),g),x))),x),And(FreeQ(List(a,f,g,m,n),x),EqQ($s("m1"),Subtract(m,C1)),EqQ($s("n1"),Subtract(n,C1)),FunctionOfQ(Times($($(Derivative(Subtract(m,C1)),f),x),$($(Derivative(Subtract(n,C1)),g),x)),u,x)))),
IIntegrate(6667,Integrate(Times(u_,Power($(f_,x_),p_DEFAULT),Plus(Times(a_DEFAULT,$(g_,x_),$($(Derivative(C1),f_),x_)),Times(b_DEFAULT,$(f_,x_),$($(Derivative(C1),g_),x_)))),x_Symbol),
    Condition(Dist(b,Subst(Integrate(SimplifyIntegrand(SubstFor(Times(Power($(f,x),Plus(p,C1)),$(g,x)),u,x),x),x),x,Times(Power($(f,x),Plus(p,C1)),$(g,x))),x),And(FreeQ(List(a,b,f,g,p),x),EqQ(a,Times(b,Plus(p,C1))),FunctionOfQ(Times(Power($(f,x),Plus(p,C1)),$(g,x)),u,x)))),
IIntegrate(6668,Integrate(Times(u_,Power($($(Derivative($p("m1")),f_),x_),p_DEFAULT),Plus(Times(a_DEFAULT,$(g_,x_),$($(Derivative(m_),f_),x_)),Times(b_DEFAULT,$($(Derivative(C1),g_),x_),$($(Derivative($p("m1")),f_),x_)))),x_Symbol),
    Condition(Dist(b,Subst(Integrate(SimplifyIntegrand(SubstFor(Times(Power($($(Derivative(Subtract(m,C1)),f),x),Plus(p,C1)),$(g,x)),u,x),x),x),x,Times(Power($($(Derivative(Subtract(m,C1)),f),x),Plus(p,C1)),$(g,x))),x),And(FreeQ(List(a,b,f,g,m,p),x),EqQ($s("m1"),Subtract(m,C1)),EqQ(a,Times(b,Plus(p,C1))),FunctionOfQ(Times(Power($($(Derivative(Subtract(m,C1)),f),x),Plus(p,C1)),$(g,x)),u,x)))),
IIntegrate(6669,Integrate(Times(u_,Power($(g_,x_),q_DEFAULT),Plus(Times(a_DEFAULT,$(g_,x_),$($(Derivative(m_),f_),x_)),Times(b_DEFAULT,$($(Derivative(C1),g_),x_),$($(Derivative($p("m1")),f_),x_)))),x_Symbol),
    Condition(Dist(a,Subst(Integrate(SimplifyIntegrand(SubstFor(Times($($(Derivative(Subtract(m,C1)),f),x),Power($(g,x),Plus(q,C1))),u,x),x),x),x,Times($($(Derivative(Subtract(m,C1)),f),x),Power($(g,x),Plus(q,C1)))),x),And(FreeQ(List(a,b,f,g,m,q),x),EqQ($s("m1"),Subtract(m,C1)),EqQ(Times(a,Plus(q,C1)),b),FunctionOfQ(Times($($(Derivative(Subtract(m,C1)),f),x),Power($(g,x),Plus(q,C1))),u,x)))),
IIntegrate(6670,Integrate(Times(u_,Power($($(Derivative($p("m1")),f_),x_),p_DEFAULT),Plus(Times(b_DEFAULT,$($(Derivative($p("m1")),f_),x_),$($(Derivative(n_),g_),x_)),Times(a_DEFAULT,$($(Derivative(m_),f_),x_),$($(Derivative($p("n1")),g_),x_)))),x_Symbol),
    Condition(Dist(b,Subst(Integrate(SimplifyIntegrand(SubstFor(Times(Power($($(Derivative(Subtract(m,C1)),f),x),Plus(p,C1)),$($(Derivative(Subtract(n,C1)),g),x)),u,x),x),x),x,Times(Power($($(Derivative(Subtract(m,C1)),f),x),Plus(p,C1)),$($(Derivative(Subtract(n,C1)),g),x))),x),And(FreeQ(List(a,b,f,g,m,n,p),x),EqQ($s("m1"),Subtract(m,C1)),EqQ($s("n1"),Subtract(n,C1)),EqQ(a,Times(b,Plus(p,C1))),FunctionOfQ(Times(Power($($(Derivative(Subtract(m,C1)),f),x),Plus(p,C1)),$($(Derivative(Subtract(n,C1)),g),x)),u,x)))),
IIntegrate(6671,Integrate(Times(u_,Power($(f_,x_),p_DEFAULT),Power($(g_,x_),q_DEFAULT),Plus(Times(a_DEFAULT,$(g_,x_),$($(Derivative(C1),f_),x_)),Times(b_DEFAULT,$(f_,x_),$($(Derivative(C1),g_),x_)))),x_Symbol),
    Condition(Dist(Times(a,Power(Plus(p,C1),CN1)),Subst(Integrate(SimplifyIntegrand(SubstFor(Times(Power($(f,x),Plus(p,C1)),Power($(g,x),Plus(q,C1))),u,x),x),x),x,Times(Power($(f,x),Plus(p,C1)),Power($(g,x),Plus(q,C1)))),x),And(FreeQ(List(a,b,f,g,p,q),x),EqQ(Times(a,Plus(q,C1)),Times(b,Plus(p,C1))),FunctionOfQ(Times(Power($(f,x),Plus(p,C1)),Power($(g,x),Plus(q,C1))),u,x)))),
IIntegrate(6672,Integrate(Times(u_,Power($(g_,x_),q_DEFAULT),Power($($(Derivative($p("m1")),f_),x_),p_DEFAULT),Plus(Times(a_DEFAULT,$(g_,x_),$($(Derivative(m_),f_),x_)),Times(b_DEFAULT,$($(Derivative(C1),g_),x_),$($(Derivative($p("m1")),f_),x_)))),x_Symbol),
    Condition(Dist(Times(a,Power(Plus(p,C1),CN1)),Subst(Integrate(SimplifyIntegrand(SubstFor(Times(Power($($(Derivative(Subtract(m,C1)),f),x),Plus(p,C1)),Power($(g,x),Plus(q,C1))),u,x),x),x),x,Times(Power($($(Derivative(Subtract(m,C1)),f),x),Plus(p,C1)),Power($(g,x),Plus(q,C1)))),x),And(FreeQ(List(a,b,f,g,m,p,q),x),EqQ($s("m1"),Subtract(m,C1)),EqQ(Times(a,Plus(q,C1)),Times(b,Plus(p,C1))),FunctionOfQ(Times(Power($($(Derivative(Subtract(m,C1)),f),x),Plus(p,C1)),Power($(g,x),Plus(q,C1))),u,x)))),
IIntegrate(6673,Integrate(Times(u_,Power($($(Derivative($p("m1")),f_),x_),p_DEFAULT),Power($($(Derivative($p("n1")),g_),x_),q_DEFAULT),Plus(Times(b_DEFAULT,$($(Derivative($p("m1")),f_),x_),$($(Derivative(n_),g_),x_)),Times(a_DEFAULT,$($(Derivative(m_),f_),x_),$($(Derivative($p("n1")),g_),x_)))),x_Symbol),
    Condition(Dist(Times(a,Power(Plus(p,C1),CN1)),Subst(Integrate(SimplifyIntegrand(SubstFor(Times(Power($($(Derivative(Subtract(m,C1)),f),x),Plus(p,C1)),Power($($(Derivative(Subtract(n,C1)),g),x),Plus(q,C1))),u,x),x),x),x,Times(Power($($(Derivative(Subtract(m,C1)),f),x),Plus(p,C1)),Power($($(Derivative(Subtract(n,C1)),g),x),Plus(q,C1)))),x),And(FreeQ(List(a,b,f,g,m,n,p,q),x),EqQ($s("m1"),Subtract(m,C1)),EqQ($s("n1"),Subtract(n,C1)),EqQ(Times(a,Plus(q,C1)),Times(b,Plus(p,C1))),FunctionOfQ(Times(Power($($(Derivative(Subtract(m,C1)),f),x),Plus(p,C1)),Power($($(Derivative(Subtract(n,C1)),g),x),Plus(q,C1))),u,x)))),
IIntegrate(6674,Integrate(Times(Power($(g_,x_),CN2),Plus(Times($(g_,x_),$($(Derivative(C1),f_),x_)),Times(CN1,$(f_,x_),$($(Derivative(C1),g_),x_)))),x_Symbol),
    Condition(Simp(Times($(f,x),Power($(g,x),CN1)),x),FreeQ(List(f,g),x))),
IIntegrate(6675,Integrate(Times(Power($(f_,x_),CN1),Power($(g_,x_),CN1),Plus(Times($(g_,x_),$($(Derivative(C1),f_),x_)),Times(CN1,$(f_,x_),$($(Derivative(C1),g_),x_)))),x_Symbol),
    Condition(Simp(Log(Times($(f,x),Power($(g,x),CN1))),x),FreeQ(List(f,g),x))),
IIntegrate(6676,Integrate(Times(u_,Power(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),n_)),p_)),x_Symbol),
    Condition(Dist(Times(Power(c,IntPart(p)),Power(Times(c,Power(Plus(a,Times(b,x)),n)),FracPart(p)),Power(Power(Plus(a,Times(b,x)),Times(n,FracPart(p))),CN1)),Integrate(Times(u,Power(Plus(a,Times(b,x)),Times(n,p))),x),x),And(FreeQ(List(a,b,c,n,p),x),Not(IntegerQ(p)),Not(MatchQ(u,Condition(Times(Power(x,$p("n1",true)),v_DEFAULT),EqQ(n,Plus($s("n1"),C1)))))))),
IIntegrate(6677,Integrate(Times(u_DEFAULT,Power(Times(c_DEFAULT,Power(Times(d_,Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_)),q_)),x_Symbol),
    Condition(Dist(Times(Power(Times(c,Power(Times(d,Plus(a,Times(b,x))),p)),q),Power(Power(Plus(a,Times(b,x)),Times(p,q)),CN1)),Integrate(Times(u,Power(Plus(a,Times(b,x)),Times(p,q))),x),x),And(FreeQ(List(a,b,c,d,p,q),x),Not(IntegerQ(p)),Not(IntegerQ(q))))),
IIntegrate(6678,Integrate(Times(u_DEFAULT,Power(Times(c_DEFAULT,Power(Times(d_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),n_)),p_)),q_)),x_Symbol),
    Condition(Dist(Times(Power(Times(c,Power(Times(d,Power(Plus(a,Times(b,x)),n)),p)),q),Power(Power(Plus(a,Times(b,x)),Times(n,p,q)),CN1)),Integrate(Times(u,Power(Plus(a,Times(b,x)),Times(n,p,q))),x),x),And(FreeQ(List(a,b,c,d,n,p,q),x),Not(IntegerQ(p)),Not(IntegerQ(q))))),
IIntegrate(6679,Integrate(Times(Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),CN1),Power(Plus(a_DEFAULT,Times(b_DEFAULT,$(F_,Times(c_DEFAULT,Sqrt(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),CN1D2))))),n_DEFAULT)),x_Symbol),
    Condition(Dist(Times(C2,e,g,Power(Times(CSymbol,Subtract(Times(e,f),Times(d,g))),CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,F(Times(c,x)))),n),Power(x,CN1)),x),x,Times(Sqrt(Plus(d,Times(e,x))),Power(Plus(f,Times(g,x)),CN1D2))),x),And(FreeQ(List(a,b,c,d,e,f,g,ASymbol,BSymbol,CSymbol,FSymbol),x),EqQ(Subtract(Times(CSymbol,d,f),Times(ASymbol,e,g)),C0),EqQ(Subtract(Times(BSymbol,e,g),Times(CSymbol,Plus(Times(e,f),Times(d,g)))),C0),IGtQ(n,C0))))
  );
}
