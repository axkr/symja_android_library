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
class IntRules361 { 
  public static IAST RULES = List( 
IIntegrate(7221,Integrate(Times(u_,Power($(f_,x_),p_DEFAULT),Plus(Times(a_DEFAULT,$(g_,x_),$($(Derivative(C1),f_),x_)),Times(b_DEFAULT,$(f_,x_),$($(Derivative(C1),g_),x_)))),x_Symbol),
    Condition(Simp(Star(b,Subst(Integrate(SimplifyIntegrand(SubstFor(Times(Power($(f,x),Plus(p,C1)),$(g,x)),u,x),x),x),x,Times(Power($(f,x),Plus(p,C1)),$(g,x)))),x),And(FreeQ(List(a,b,f,g,p),x),EqQ(a,Times(b,Plus(p,C1))),FunctionOfQ(Times(Power($(f,x),Plus(p,C1)),$(g,x)),u,x)))),
IIntegrate(7222,Integrate(Times(u_,Power($($(Derivative($p("m1")),f_),x_),p_DEFAULT),Plus(Times(a_DEFAULT,$(g_,x_),$($(Derivative(m_),f_),x_)),Times(b_DEFAULT,$($(Derivative(C1),g_),x_),$($(Derivative($p("m1")),f_),x_)))),x_Symbol),
    Condition(Simp(Star(b,Subst(Integrate(SimplifyIntegrand(SubstFor(Times(Power($($(Derivative(Subtract(m,C1)),f),x),Plus(p,C1)),$(g,x)),u,x),x),x),x,Times(Power($($(Derivative(Subtract(m,C1)),f),x),Plus(p,C1)),$(g,x)))),x),And(FreeQ(List(a,b,f,g,m,p),x),EqQ($s("m1"),Subtract(m,C1)),EqQ(a,Times(b,Plus(p,C1))),FunctionOfQ(Times(Power($($(Derivative(Subtract(m,C1)),f),x),Plus(p,C1)),$(g,x)),u,x)))),
IIntegrate(7223,Integrate(Times(u_,Power($(g_,x_),q_DEFAULT),Plus(Times(a_DEFAULT,$(g_,x_),$($(Derivative(m_),f_),x_)),Times(b_DEFAULT,$($(Derivative(C1),g_),x_),$($(Derivative($p("m1")),f_),x_)))),x_Symbol),
    Condition(Simp(Star(a,Subst(Integrate(SimplifyIntegrand(SubstFor(Times($($(Derivative(Subtract(m,C1)),f),x),Power($(g,x),Plus(q,C1))),u,x),x),x),x,Times($($(Derivative(Subtract(m,C1)),f),x),Power($(g,x),Plus(q,C1))))),x),And(FreeQ(List(a,b,f,g,m,q),x),EqQ($s("m1"),Subtract(m,C1)),EqQ(Times(a,Plus(q,C1)),b),FunctionOfQ(Times($($(Derivative(Subtract(m,C1)),f),x),Power($(g,x),Plus(q,C1))),u,x)))),
IIntegrate(7224,Integrate(Times(u_,Power($($(Derivative($p("m1")),f_),x_),p_DEFAULT),Plus(Times(b_DEFAULT,$($(Derivative($p("m1")),f_),x_),$($(Derivative(n_),g_),x_)),Times(a_DEFAULT,$($(Derivative(m_),f_),x_),$($(Derivative($p("n1")),g_),x_)))),x_Symbol),
    Condition(Simp(Star(b,Subst(Integrate(SimplifyIntegrand(SubstFor(Times(Power($($(Derivative(Subtract(m,C1)),f),x),Plus(p,C1)),$($(Derivative(Subtract(n,C1)),g),x)),u,x),x),x),x,Times(Power($($(Derivative(Subtract(m,C1)),f),x),Plus(p,C1)),$($(Derivative(Subtract(n,C1)),g),x)))),x),And(FreeQ(List(a,b,f,g,m,n,p),x),EqQ($s("m1"),Subtract(m,C1)),EqQ($s("n1"),Subtract(n,C1)),EqQ(a,Times(b,Plus(p,C1))),FunctionOfQ(Times(Power($($(Derivative(Subtract(m,C1)),f),x),Plus(p,C1)),$($(Derivative(Subtract(n,C1)),g),x)),u,x)))),
IIntegrate(7225,Integrate(Times(u_,Power($(f_,x_),p_DEFAULT),Power($(g_,x_),q_DEFAULT),Plus(Times(a_DEFAULT,$(g_,x_),$($(Derivative(C1),f_),x_)),Times(b_DEFAULT,$(f_,x_),$($(Derivative(C1),g_),x_)))),x_Symbol),
    Condition(Simp(Star(Times(a,Power(Plus(p,C1),CN1)),Subst(Integrate(SimplifyIntegrand(SubstFor(Times(Power($(f,x),Plus(p,C1)),Power($(g,x),Plus(q,C1))),u,x),x),x),x,Times(Power($(f,x),Plus(p,C1)),Power($(g,x),Plus(q,C1))))),x),And(FreeQ(List(a,b,f,g,p,q),x),EqQ(Times(a,Plus(q,C1)),Times(b,Plus(p,C1))),FunctionOfQ(Times(Power($(f,x),Plus(p,C1)),Power($(g,x),Plus(q,C1))),u,x)))),
IIntegrate(7226,Integrate(Times(u_,Power($(g_,x_),q_DEFAULT),Power($($(Derivative($p("m1")),f_),x_),p_DEFAULT),Plus(Times(a_DEFAULT,$(g_,x_),$($(Derivative(m_),f_),x_)),Times(b_DEFAULT,$($(Derivative(C1),g_),x_),$($(Derivative($p("m1")),f_),x_)))),x_Symbol),
    Condition(Simp(Star(Times(a,Power(Plus(p,C1),CN1)),Subst(Integrate(SimplifyIntegrand(SubstFor(Times(Power($($(Derivative(Subtract(m,C1)),f),x),Plus(p,C1)),Power($(g,x),Plus(q,C1))),u,x),x),x),x,Times(Power($($(Derivative(Subtract(m,C1)),f),x),Plus(p,C1)),Power($(g,x),Plus(q,C1))))),x),And(FreeQ(List(a,b,f,g,m,p,q),x),EqQ($s("m1"),Subtract(m,C1)),EqQ(Times(a,Plus(q,C1)),Times(b,Plus(p,C1))),FunctionOfQ(Times(Power($($(Derivative(Subtract(m,C1)),f),x),Plus(p,C1)),Power($(g,x),Plus(q,C1))),u,x)))),
IIntegrate(7227,Integrate(Times(u_,Power($($(Derivative($p("m1")),f_),x_),p_DEFAULT),Power($($(Derivative($p("n1")),g_),x_),q_DEFAULT),Plus(Times(b_DEFAULT,$($(Derivative($p("m1")),f_),x_),$($(Derivative(n_),g_),x_)),Times(a_DEFAULT,$($(Derivative(m_),f_),x_),$($(Derivative($p("n1")),g_),x_)))),x_Symbol),
    Condition(Simp(Star(Times(a,Power(Plus(p,C1),CN1)),Subst(Integrate(SimplifyIntegrand(SubstFor(Times(Power($($(Derivative(Subtract(m,C1)),f),x),Plus(p,C1)),Power($($(Derivative(Subtract(n,C1)),g),x),Plus(q,C1))),u,x),x),x),x,Times(Power($($(Derivative(Subtract(m,C1)),f),x),Plus(p,C1)),Power($($(Derivative(Subtract(n,C1)),g),x),Plus(q,C1))))),x),And(FreeQ(List(a,b,f,g,m,n,p,q),x),EqQ($s("m1"),Subtract(m,C1)),EqQ($s("n1"),Subtract(n,C1)),EqQ(Times(a,Plus(q,C1)),Times(b,Plus(p,C1))),FunctionOfQ(Times(Power($($(Derivative(Subtract(m,C1)),f),x),Plus(p,C1)),Power($($(Derivative(Subtract(n,C1)),g),x),Plus(q,C1))),u,x)))),
IIntegrate(7228,Integrate(Times(Power($(g_,x_),CN2),Plus(Times($(g_,x_),$($(Derivative(C1),f_),x_)),Times(CN1,$(f_,x_),$($(Derivative(C1),g_),x_)))),x_Symbol),
    Condition(Simp(Times($(f,x),Power($(g,x),CN1)),x),FreeQ(list(f,g),x))),
IIntegrate(7229,Integrate(Times(Power($(f_,x_),CN1),Power($(g_,x_),CN1),Plus(Times($(g_,x_),$($(Derivative(C1),f_),x_)),Times(CN1,$(f_,x_),$($(Derivative(C1),g_),x_)))),x_Symbol),
    Condition(Simp(Log(Times($(f,x),Power($(g,x),CN1))),x),FreeQ(list(f,g),x))),
IIntegrate(7230,Integrate(Times(Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),CN1),Power(Plus(a_DEFAULT,Times(b_DEFAULT,$(F_,Times(c_DEFAULT,Sqrt(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),CN1D2))))),n_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(C2,e,g,Power(Times(CSymbol,Subtract(Times(e,f),Times(d,g))),CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,F(Times(c,x)))),n),Power(x,CN1)),x),x,Times(Sqrt(Plus(d,Times(e,x))),Power(Plus(f,Times(g,x)),CN1D2)))),x),And(FreeQ(List(a,b,c,d,e,f,g,ASymbol,BSymbol,CSymbol,FSymbol),x),EqQ(Subtract(Times(CSymbol,d,f),Times(ASymbol,e,g)),C0),EqQ(Subtract(Times(BSymbol,e,g),Times(CSymbol,Plus(Times(e,f),Times(d,g)))),C0),IGtQ(n,C0)))),
IIntegrate(7231,Integrate(Times(Power(Plus(A_DEFAULT,Times(C_DEFAULT,Sqr(x_))),CN1),Power(Plus(a_DEFAULT,Times(b_DEFAULT,$(F_,Times(c_DEFAULT,Sqrt(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),CN1D2))))),n_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(C2,e,g,Power(Times(CSymbol,Subtract(Times(e,f),Times(d,g))),CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,F(Times(c,x)))),n),Power(x,CN1)),x),x,Times(Sqrt(Plus(d,Times(e,x))),Power(Plus(f,Times(g,x)),CN1D2)))),x),And(FreeQ(List(a,b,c,d,e,f,g,ASymbol,CSymbol,FSymbol),x),EqQ(Subtract(Times(CSymbol,d,f),Times(ASymbol,e,g)),C0),EqQ(Plus(Times(e,f),Times(d,g)),C0),IGtQ(n,C0)))),
IIntegrate(7232,Integrate(Times(Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),CN1),Power(Plus(a_DEFAULT,Times(b_DEFAULT,$(F_,Times(c_DEFAULT,Sqrt(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),CN1D2))))),n_)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(a,Times(b,F(Times(c,Sqrt(Plus(d,Times(e,x))),Power(Plus(f,Times(g,x)),CN1D2))))),n),Power(Plus(ASymbol,Times(BSymbol,x),Times(CSymbol,Sqr(x))),CN1)),x),And(FreeQ(List(a,b,c,d,e,f,g,ASymbol,BSymbol,CSymbol,FSymbol,n),x),EqQ(Subtract(Times(CSymbol,d,f),Times(ASymbol,e,g)),C0),EqQ(Subtract(Times(BSymbol,e,g),Times(CSymbol,Plus(Times(e,f),Times(d,g)))),C0),Not(IGtQ(n,C0))))),
IIntegrate(7233,Integrate(Times(Power(Plus(A_,Times(C_DEFAULT,Sqr(x_))),CN1),Power(Plus(a_DEFAULT,Times(b_DEFAULT,$(F_,Times(c_DEFAULT,Sqrt(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),CN1D2))))),n_)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(a,Times(b,F(Times(c,Sqrt(Plus(d,Times(e,x))),Power(Plus(f,Times(g,x)),CN1D2))))),n),Power(Plus(ASymbol,Times(CSymbol,Sqr(x))),CN1)),x),And(FreeQ(List(a,b,c,d,e,f,g,ASymbol,CSymbol,FSymbol,n),x),EqQ(Subtract(Times(CSymbol,d,f),Times(ASymbol,e,g)),C0),EqQ(Plus(Times(e,f),Times(d,g)),C0),Not(IGtQ(n,C0))))),
IIntegrate(7234,Integrate(Times(u_,Power(y_,CN1)),x_Symbol),
    With(list(Set(q,DerivativeDivides(y,u,x))),Condition(Simp(Times(q,Log(RemoveContent(y,x))),x),Not(FalseQ(q))))),
IIntegrate(7235,Integrate(Times(u_,Power(w_,CN1),Power(y_,CN1)),x_Symbol),
    With(list(Set(q,DerivativeDivides(Times(y,w),u,x))),Condition(Simp(Times(q,Log(RemoveContent(Times(y,w),x))),x),Not(FalseQ(q))))),
IIntegrate(7236,Integrate(Times(u_,Power(y_,m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(q,DerivativeDivides(y,u,x))),Condition(Simp(Times(q,Power(y,Plus(m,C1)),Power(Plus(m,C1),CN1)),x),Not(FalseQ(q)))),And(FreeQ(m,x),NeQ(m,CN1)))),
IIntegrate(7237,Integrate(Times(u_,Power(y_,m_DEFAULT),Power(z_,n_DEFAULT)),x_Symbol),
    Condition(With(list(Set(q,DerivativeDivides(Times(y,z),Times(u,Power(z,Subtract(n,m))),x))),Condition(Simp(Times(q,Power(y,Plus(m,C1)),Power(z,Plus(m,C1)),Power(Plus(m,C1),CN1)),x),Not(FalseQ(q)))),And(FreeQ(list(m,n),x),NeQ(m,CN1)))),
IIntegrate(7238,Integrate(u_,x_Symbol),
    With(list(Set(v,SimplifyIntegrand(u,x))),Condition(Integrate(v,x),SimplerIntegrandQ(v,u,x)))),
IIntegrate(7239,Integrate(Times(u_DEFAULT,Power(Plus(Times(e_DEFAULT,Sqrt(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))))),Times(f_DEFAULT,Sqrt(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_DEFAULT)))))),m_)),x_Symbol),
    Condition(Simp(Star(Power(Subtract(Times(a,Sqr(e)),Times(c,Sqr(f))),m),Integrate(ExpandIntegrand(Times(u,Power(Power(Subtract(Times(e,Sqrt(Plus(a,Times(b,Power(x,n))))),Times(f,Sqrt(Plus(c,Times(d,Power(x,n)))))),m),CN1)),x),x)),x),And(FreeQ(List(a,b,c,d,e,f,n),x),ILtQ(m,C0),EqQ(Subtract(Times(b,Sqr(e)),Times(d,Sqr(f))),C0))))
  );
}
