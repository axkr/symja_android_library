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
class IntRules362 { 
  public static IAST RULES = List( 
IIntegrate(7241,Integrate(Times(u_,Power($($(Derivative($p("m1")),f_),x_),p_DEFAULT),Power($($(Derivative($p("n1")),g_),x_),q_DEFAULT),Plus(Times(b_DEFAULT,$($(Derivative($p("m1")),f_),x_),$($(Derivative(n_),g_),x_)),Times(a_DEFAULT,$($(Derivative(m_),f_),x_),$($(Derivative($p("n1")),g_),x_)))),x_Symbol),
    Condition(Simp(Dist(Times(a,Power(Plus(p,C1),CN1)),Subst(Integrate(SimplifyIntegrand(SubstFor(Times(Power($($(Derivative(Subtract(m,C1)),f),x),Plus(p,C1)),Power($($(Derivative(Subtract(n,C1)),g),x),Plus(q,C1))),u,x),x),x),x,Times(Power($($(Derivative(Subtract(m,C1)),f),x),Plus(p,C1)),Power($($(Derivative(Subtract(n,C1)),g),x),Plus(q,C1)))),x),x),And(FreeQ(List(a,b,f,g,m,n,p,q),x),EqQ($s("m1"),Plus(m,CN1)),EqQ($s("n1"),Plus(n,CN1)),EqQ(Times(a,Plus(q,C1)),Times(b,Plus(p,C1))),FunctionOfQ(Times(Power($($(Derivative(Subtract(m,C1)),f),x),Plus(p,C1)),Power($($(Derivative(Subtract(n,C1)),g),x),Plus(q,C1))),u,x)))),
IIntegrate(7243,Integrate(Times(Power($(g_,x_),CN2),Plus(Times($(g_,x_),$($(Derivative(C1),f_),x_)),Times(CN1,$(f_,x_),$($(Derivative(C1),g_),x_)))),x_Symbol),
    Condition(Simp(Times($(f,x),Power($(g,x),CN1)),x),FreeQ(list(f,g),x))),
IIntegrate(7244,Integrate(Times(Power($(f_,x_),CN1),Power($(g_,x_),CN1),Plus(Times($(g_,x_),$($(Derivative(C1),f_),x_)),Times(CN1,$(f_,x_),$($(Derivative(C1),g_),x_)))),x_Symbol),
    Condition(Simp(Log(Times($(f,x),Power($(g,x),CN1))),x),FreeQ(list(f,g),x))),
IIntegrate(7245,Integrate(Times(Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),CN1),Power(Plus(a_DEFAULT,Times(b_DEFAULT,$(F_,Times(c_DEFAULT,Sqrt(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),CN1D2))))),n_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(C2,e,g,Power(Times(CSymbol,Subtract(Times(e,f),Times(d,g))),CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,F(Times(c,x)))),n),Power(x,CN1)),x),x,Times(Sqrt(Plus(d,Times(e,x))),Power(Plus(f,Times(g,x)),CN1D2))),x),x),And(FreeQ(List(a,b,c,d,e,f,g,ASymbol,BSymbol,CSymbol,FSymbol),x),EqQ(Subtract(Times(CSymbol,d,f),Times(ASymbol,e,g)),C0),EqQ(Subtract(Times(BSymbol,e,g),Times(CSymbol,Plus(Times(e,f),Times(d,g)))),C0),IGtQ(n,C0)))),
IIntegrate(7246,Integrate(Times(Power(Plus(A_DEFAULT,Times(C_DEFAULT,Sqr(x_))),CN1),Power(Plus(a_DEFAULT,Times(b_DEFAULT,$(F_,Times(c_DEFAULT,Sqrt(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),CN1D2))))),n_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(C2,e,g,Power(Times(CSymbol,Subtract(Times(e,f),Times(d,g))),CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,F(Times(c,x)))),n),Power(x,CN1)),x),x,Times(Sqrt(Plus(d,Times(e,x))),Power(Plus(f,Times(g,x)),CN1D2))),x),x),And(FreeQ(List(a,b,c,d,e,f,g,ASymbol,CSymbol,FSymbol),x),EqQ(Subtract(Times(CSymbol,d,f),Times(ASymbol,e,g)),C0),EqQ(Plus(Times(e,f),Times(d,g)),C0),IGtQ(n,C0)))),
IIntegrate(7247,Integrate(Times(Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),CN1),Power(Plus(a_DEFAULT,Times(b_DEFAULT,$(F_,Times(c_DEFAULT,Sqrt(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),CN1D2))))),n_)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(a,Times(b,F(Times(c,Sqrt(Plus(d,Times(e,x))),Power(Plus(f,Times(g,x)),CN1D2))))),n),Power(Plus(ASymbol,Times(BSymbol,x),Times(CSymbol,Sqr(x))),CN1)),x),And(FreeQ(List(a,b,c,d,e,f,g,ASymbol,BSymbol,CSymbol,FSymbol,n),x),EqQ(Subtract(Times(CSymbol,d,f),Times(ASymbol,e,g)),C0),EqQ(Subtract(Times(BSymbol,e,g),Times(CSymbol,Plus(Times(e,f),Times(d,g)))),C0),Not(IGtQ(n,C0))))),
IIntegrate(7248,Integrate(Times(Power(Plus(A_,Times(C_DEFAULT,Sqr(x_))),CN1),Power(Plus(a_DEFAULT,Times(b_DEFAULT,$(F_,Times(c_DEFAULT,Sqrt(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),CN1D2))))),n_)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(a,Times(b,F(Times(c,Sqrt(Plus(d,Times(e,x))),Power(Plus(f,Times(g,x)),CN1D2))))),n),Power(Plus(ASymbol,Times(CSymbol,Sqr(x))),CN1)),x),And(FreeQ(List(a,b,c,d,e,f,g,ASymbol,CSymbol,FSymbol,n),x),EqQ(Subtract(Times(CSymbol,d,f),Times(ASymbol,e,g)),C0),EqQ(Plus(Times(e,f),Times(d,g)),C0),Not(IGtQ(n,C0))))),
IIntegrate(7249,Integrate(Times(u_,Power(y_,CN1)),x_Symbol),
    With(list(Set(q,DerivativeDivides(y,u,x))),Condition(Simp(Times(q,Log(RemoveContent(y,x))),x),Not(FalseQ(q))))),
IIntegrate(7250,Integrate(Times(u_,Power(w_,CN1),Power(y_,CN1)),x_Symbol),
    With(list(Set(q,DerivativeDivides(Times(y,w),u,x))),Condition(Simp(Times(q,Log(RemoveContent(Times(y,w),x))),x),Not(FalseQ(q))))),
IIntegrate(7251,Integrate(Times(u_,Power(y_,m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(q,DerivativeDivides(y,u,x))),Condition(Simp(Times(q,Power(y,Plus(m,C1)),Power(Plus(m,C1),CN1)),x),Not(FalseQ(q)))),And(FreeQ(m,x),NeQ(m,CN1)))),
IIntegrate(7252,Integrate(Times(u_,Power(y_,m_DEFAULT),Power(z_,n_DEFAULT)),x_Symbol),
    Condition(With(list(Set(q,DerivativeDivides(Times(y,z),Times(u,Power(z,Subtract(n,m))),x))),Condition(Simp(Times(q,Power(y,Plus(m,C1)),Power(z,Plus(m,C1)),Power(Plus(m,C1),CN1)),x),Not(FalseQ(q)))),And(FreeQ(list(m,n),x),NeQ(m,CN1)))),
IIntegrate(7253,Integrate(u_,x_Symbol),
    With(list(Set(v,SimplifyIntegrand(u,x))),Condition(Integrate(v,x),SimplerIntegrandQ(v,u,x)))),
IIntegrate(7254,Integrate(Times(u_DEFAULT,Power(Plus(Times(e_DEFAULT,Sqrt(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))))),Times(f_DEFAULT,Sqrt(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_DEFAULT)))))),m_)),x_Symbol),
    Condition(Simp(Dist(Power(Subtract(Times(a,Sqr(e)),Times(c,Sqr(f))),m),Integrate(ExpandIntegrand(Times(u,Power(Power(Subtract(Times(e,Sqrt(Plus(a,Times(b,Power(x,n))))),Times(f,Sqrt(Plus(c,Times(d,Power(x,n)))))),m),CN1)),x),x),x),x),And(FreeQ(List(a,b,c,d,e,f,n),x),ILtQ(m,C0),EqQ(Subtract(Times(b,Sqr(e)),Times(d,Sqr(f))),C0)))),
IIntegrate(7255,Integrate(Times(u_DEFAULT,Power(Plus(Times(e_DEFAULT,Sqrt(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))))),Times(f_DEFAULT,Sqrt(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_DEFAULT)))))),m_)),x_Symbol),
    Condition(Simp(Dist(Power(Subtract(Times(b,Sqr(e)),Times(d,Sqr(f))),m),Integrate(ExpandIntegrand(Times(u,Power(x,Times(m,n)),Power(Power(Subtract(Times(e,Sqrt(Plus(a,Times(b,Power(x,n))))),Times(f,Sqrt(Plus(c,Times(d,Power(x,n)))))),m),CN1)),x),x),x),x),And(FreeQ(List(a,b,c,d,e,f,n),x),ILtQ(m,C0),EqQ(Subtract(Times(a,Sqr(e)),Times(c,Sqr(f))),C0)))),
IIntegrate(7256,Integrate(Times(Power(u_,m_DEFAULT),Power(Plus(Times(a_DEFAULT,Power(u_,n_)),v_),p_DEFAULT),w_),x_Symbol),
    Condition(Integrate(Times(Power(u,Plus(m,Times(n,p))),Power(Plus(a,Times(v,Power(Power(u,n),CN1))),p),w),x),And(FreeQ(list(a,m,n),x),IntegerQ(p),Not(GtQ(n,C0)),Not(FreeQ(v,x))))),
IIntegrate(7257,Integrate(Times(u_,Power(Plus(c_DEFAULT,Times(d_DEFAULT,v_)),n_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,y_)),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(q,DerivativeDivides(y,u,x))),Condition(Simp(Dist(q,Subst(Integrate(Times(Power(Plus(a,Times(b,x)),m),Power(Plus(c,Times(d,x)),n)),x),x,y),x),x),Not(FalseQ(q)))),And(FreeQ(List(a,b,c,d,m,n),x),EqQ(v,y)))),
IIntegrate(7258,Integrate(Times(u_,Power(Plus(c_DEFAULT,Times(d_DEFAULT,v_)),n_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,w_)),p_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,y_)),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(q,DerivativeDivides(y,u,x))),Condition(Simp(Dist(q,Subst(Integrate(Times(Power(Plus(a,Times(b,x)),m),Power(Plus(c,Times(d,x)),n),Power(Plus(e,Times(f,x)),p)),x),x,y),x),x),Not(FalseQ(q)))),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),EqQ(v,y),EqQ(w,y)))),
IIntegrate(7259,Integrate(Times(u_,Power(Plus(c_DEFAULT,Times(d_DEFAULT,v_)),n_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,w_)),p_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,y_)),m_DEFAULT),Power(Plus(g_DEFAULT,Times(h_DEFAULT,z_)),q_DEFAULT)),x_Symbol),
    Condition(With(list(Set(r,DerivativeDivides(y,u,x))),Condition(Simp(Dist(r,Subst(Integrate(Times(Power(Plus(a,Times(b,x)),m),Power(Plus(c,Times(d,x)),n),Power(Plus(e,Times(f,x)),p),Power(Plus(g,Times(h,x)),q)),x),x,y),x),x),Not(FalseQ(r)))),And(FreeQ(List(a,b,c,d,e,f,g,h,m,n,p,q),x),EqQ(v,y),EqQ(w,y),EqQ(z,y)))),
IIntegrate(7260,Integrate(Times(u_DEFAULT,Plus(a_,Times(b_DEFAULT,Power(y_,n_)))),x_Symbol),
    Condition(With(list(Set(q,DerivativeDivides(y,u,x))),Condition(Plus(Simp(Dist(a,Integrate(u,x),x),x),Simp(Dist(Times(b,q),Subst(Integrate(Power(x,n),x),x,y),x),x)),Not(FalseQ(q)))),FreeQ(list(a,b,n),x)))
  );
}
