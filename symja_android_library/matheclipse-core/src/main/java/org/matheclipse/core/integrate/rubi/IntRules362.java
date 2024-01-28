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
class IntRules362 { 
  public static IAST RULES = List( 
IIntegrate(7240,Integrate(Times(u_DEFAULT,Power(Plus(Times(e_DEFAULT,Sqrt(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))))),Times(f_DEFAULT,Sqrt(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_DEFAULT)))))),m_)),x_Symbol),
    Condition(Simp(Star(Power(Subtract(Times(b,Sqr(e)),Times(d,Sqr(f))),m),Integrate(ExpandIntegrand(Times(u,Power(x,Times(m,n)),Power(Power(Subtract(Times(e,Sqrt(Plus(a,Times(b,Power(x,n))))),Times(f,Sqrt(Plus(c,Times(d,Power(x,n)))))),m),CN1)),x),x)),x),And(FreeQ(List(a,b,c,d,e,f,n),x),ILtQ(m,C0),EqQ(Subtract(Times(a,Sqr(e)),Times(c,Sqr(f))),C0)))),
IIntegrate(7241,Integrate(Times(Power(u_,m_DEFAULT),Power(Plus(Times(a_DEFAULT,Power(u_,n_)),v_),p_DEFAULT),w_),x_Symbol),
    Condition(Integrate(Times(Power(u,Plus(m,Times(n,p))),Power(Plus(a,Times(v,Power(Power(u,n),CN1))),p),w),x),And(FreeQ(list(a,m,n),x),IntegerQ(p),Not(GtQ(n,C0)),Not(FreeQ(v,x))))),
IIntegrate(7242,Integrate(Times(u_,Power(Plus(c_DEFAULT,Times(d_DEFAULT,v_)),n_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,y_)),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(q,DerivativeDivides(y,u,x))),Condition(Simp(Star(q,Subst(Integrate(Times(Power(Plus(a,Times(b,x)),m),Power(Plus(c,Times(d,x)),n)),x),x,y)),x),Not(FalseQ(q)))),And(FreeQ(List(a,b,c,d,m,n),x),EqQ(v,y)))),
IIntegrate(7243,Integrate(Times(u_,Power(Plus(c_DEFAULT,Times(d_DEFAULT,v_)),n_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,w_)),p_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,y_)),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(q,DerivativeDivides(y,u,x))),Condition(Simp(Star(q,Subst(Integrate(Times(Power(Plus(a,Times(b,x)),m),Power(Plus(c,Times(d,x)),n),Power(Plus(e,Times(f,x)),p)),x),x,y)),x),Not(FalseQ(q)))),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),EqQ(v,y),EqQ(w,y)))),
IIntegrate(7244,Integrate(Times(u_,Power(Plus(c_DEFAULT,Times(d_DEFAULT,v_)),n_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,w_)),p_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,y_)),m_DEFAULT),Power(Plus(g_DEFAULT,Times(h_DEFAULT,z_)),q_DEFAULT)),x_Symbol),
    Condition(With(list(Set(r,DerivativeDivides(y,u,x))),Condition(Simp(Star(r,Subst(Integrate(Times(Power(Plus(a,Times(b,x)),m),Power(Plus(c,Times(d,x)),n),Power(Plus(e,Times(f,x)),p),Power(Plus(g,Times(h,x)),q)),x),x,y)),x),Not(FalseQ(r)))),And(FreeQ(List(a,b,c,d,e,f,g,h,m,n,p,q),x),EqQ(v,y),EqQ(w,y),EqQ(z,y)))),
IIntegrate(7245,Integrate(Times(u_DEFAULT,Plus(a_,Times(b_DEFAULT,Power(y_,n_)))),x_Symbol),
    Condition(With(list(Set(q,DerivativeDivides(y,u,x))),Condition(Plus(Simp(Star(a,Integrate(u,x)),x),Simp(Star(Times(b,q),Subst(Integrate(Power(x,n),x),x,y)),x)),Not(FalseQ(q)))),FreeQ(list(a,b,n),x))),
IIntegrate(7246,Integrate(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(y_,n_))),p_)),x_Symbol),
    Condition(With(list(Set(q,DerivativeDivides(y,u,x))),Condition(Simp(Star(q,Subst(Integrate(Power(Plus(a,Times(b,Power(x,n))),p),x),x,y)),x),Not(FalseQ(q)))),FreeQ(List(a,b,n,p),x))),
IIntegrate(7247,Integrate(Times(u_DEFAULT,Power(v_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(y_,n_))),p_DEFAULT)),x_Symbol),
    Condition(Module(list(q,r),Condition(Simp(Star(Times(q,r),Subst(Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Power(x,n))),p)),x),x,y)),x),And(Not(FalseQ(Set(r,Divides(Power(y,m),Power(v,m),x)))),Not(FalseQ(Set(q,DerivativeDivides(y,u,x))))))),FreeQ(List(a,b,m,n,p),x))),
IIntegrate(7248,Integrate(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(c_DEFAULT,Power(v_,$p("n2",true))),Times(b_DEFAULT,Power(y_,n_))),p_)),x_Symbol),
    Condition(With(list(Set(q,DerivativeDivides(y,u,x))),Condition(Simp(Star(q,Subst(Integrate(Power(Plus(a,Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n)))),p),x),x,y)),x),Not(FalseQ(q)))),And(FreeQ(List(a,b,c,n,p),x),EqQ($s("n2"),Times(C2,n)),EqQ(v,y)))),
IIntegrate(7249,Integrate(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(v_,n_)),Times(c_DEFAULT,Power(w_,$p("n2",true)))),p_DEFAULT),Plus(A_,Times(B_DEFAULT,Power(y_,n_)))),x_Symbol),
    Condition(With(list(Set(q,DerivativeDivides(y,u,x))),Condition(Simp(Star(q,Subst(Integrate(Times(Plus(ASymbol,Times(BSymbol,Power(x,n))),Power(Plus(a,Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n)))),p)),x),x,y)),x),Not(FalseQ(q)))),And(FreeQ(List(a,b,c,ASymbol,BSymbol,n,p),x),EqQ($s("n2"),Times(C2,n)),EqQ(v,y),EqQ(w,y)))),
IIntegrate(7250,Integrate(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(c_DEFAULT,Power(w_,$p("n2",true)))),p_DEFAULT),Plus(A_,Times(B_DEFAULT,Power(y_,n_)))),x_Symbol),
    Condition(With(list(Set(q,DerivativeDivides(y,u,x))),Condition(Simp(Star(q,Subst(Integrate(Times(Plus(ASymbol,Times(BSymbol,Power(x,n))),Power(Plus(a,Times(c,Power(x,Times(C2,n)))),p)),x),x,y)),x),Not(FalseQ(q)))),And(FreeQ(List(a,c,ASymbol,BSymbol,n,p),x),EqQ($s("n2"),Times(C2,n)),EqQ(w,y)))),
IIntegrate(7251,Integrate(Times(u_DEFAULT,Power(v_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(c_DEFAULT,Power(w_,$p("n2",true))),Times(b_DEFAULT,Power(y_,n_))),p_DEFAULT)),x_Symbol),
    Condition(Module(list(q,r),Condition(Simp(Star(Times(q,r),Subst(Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n)))),p)),x),x,y)),x),And(Not(FalseQ(Set(r,Divides(Power(y,m),Power(v,m),x)))),Not(FalseQ(Set(q,DerivativeDivides(y,u,x))))))),And(FreeQ(List(a,b,c,m,n,p),x),EqQ($s("n2"),Times(C2,n)),EqQ(w,y)))),
IIntegrate(7252,Integrate(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(v_,n_)),Times(c_DEFAULT,Power(w_,$p("n2",true)))),p_DEFAULT),Plus(A_,Times(B_DEFAULT,Power(y_,n_))),Power(z_,m_DEFAULT)),x_Symbol),
    Condition(Module(list(q,r),Condition(Simp(Star(Times(q,r),Subst(Integrate(Times(Power(x,m),Plus(ASymbol,Times(BSymbol,Power(x,n))),Power(Plus(a,Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n)))),p)),x),x,y)),x),And(Not(FalseQ(Set(r,Divides(Power(y,m),Power(z,m),x)))),Not(FalseQ(Set(q,DerivativeDivides(y,u,x))))))),And(FreeQ(List(a,b,c,ASymbol,BSymbol,m,n,p),x),EqQ($s("n2"),Times(C2,n)),EqQ(v,y),EqQ(w,y)))),
IIntegrate(7253,Integrate(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(c_DEFAULT,Power(w_,$p("n2",true)))),p_DEFAULT),Plus(A_,Times(B_DEFAULT,Power(y_,n_))),Power(z_,m_DEFAULT)),x_Symbol),
    Condition(Module(list(q,r),Condition(Simp(Star(Times(q,r),Subst(Integrate(Times(Power(x,m),Plus(ASymbol,Times(BSymbol,Power(x,n))),Power(Plus(a,Times(c,Power(x,Times(C2,n)))),p)),x),x,y)),x),And(Not(FalseQ(Set(r,Divides(Power(y,m),Power(z,m),x)))),Not(FalseQ(Set(q,DerivativeDivides(y,u,x))))))),And(FreeQ(List(a,c,ASymbol,BSymbol,m,n,p),x),EqQ($s("n2"),Times(C2,n)),EqQ(w,y)))),
IIntegrate(7254,Integrate(Times(u_DEFAULT,Power(Plus(c_DEFAULT,Times(d_DEFAULT,Power(v_,n_))),p_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(y_,n_))),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(q,DerivativeDivides(y,u,x))),Condition(Simp(Star(q,Subst(Integrate(Times(Power(Plus(a,Times(b,Power(x,n))),m),Power(Plus(c,Times(d,Power(x,n))),p)),x),x,y)),x),Not(FalseQ(q)))),And(FreeQ(List(a,b,c,d,m,n,p),x),EqQ(v,y)))),
IIntegrate(7255,Integrate(Times(u_DEFAULT,Power(Plus(c_DEFAULT,Times(d_DEFAULT,Power(v_,n_))),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,Power(w_,n_))),q_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(y_,n_))),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(r,DerivativeDivides(y,u,x))),Condition(Simp(Star(r,Subst(Integrate(Times(Power(Plus(a,Times(b,Power(x,n))),m),Power(Plus(c,Times(d,Power(x,n))),p),Power(Plus(e,Times(f,Power(x,n))),q)),x),x,y)),x),Not(FalseQ(r)))),And(FreeQ(List(a,b,c,d,e,f,m,n,p,q),x),EqQ(v,y),EqQ(w,y)))),
IIntegrate(7256,Integrate(Times(Power(F_,v_),u_),x_Symbol),
    Condition(With(list(Set(q,DerivativeDivides(v,u,x))),Condition(Simp(Times(q,Power(FSymbol,v),Power(Log(FSymbol),CN1)),x),Not(FalseQ(q)))),FreeQ(FSymbol,x))),
IIntegrate(7257,Integrate(Times(Power(F_,v_),u_,Power(w_,m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(q,DerivativeDivides(v,u,x))),Condition(Simp(Star(q,Subst(Integrate(Times(Power(x,m),Power(FSymbol,x)),x),x,v)),x),Not(FalseQ(q)))),And(FreeQ(list(FSymbol,m),x),EqQ(w,v)))),
IIntegrate(7258,Integrate(Times(u_,Power(Plus(a_,Times(b_DEFAULT,Power(v_,p_DEFAULT),Power(w_,p_DEFAULT))),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(c,Simplify(Times(u,Power(Plus(Times(w,D(v,x)),Times(v,D(w,x))),CN1))))),Condition(Simp(Star(c,Subst(Integrate(Power(Plus(a,Times(b,Power(x,p))),m),x),x,Times(v,w))),x),FreeQ(c,x))),And(FreeQ(List(a,b,m,p),x),IntegerQ(p)))),
IIntegrate(7259,Integrate(Times(u_,Power(v_,r_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(v_,p_DEFAULT),Power(w_,q_DEFAULT))),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(c,Simplify(Times(u,Power(Plus(Times(p,w,D(v,x)),Times(q,v,D(w,x))),CN1))))),Condition(Simp(Star(Times(c,p,Power(Plus(r,C1),CN1)),Subst(Integrate(Power(Plus(a,Times(b,Power(x,Times(p,Power(Plus(r,C1),CN1))))),m),x),x,Times(Power(v,Plus(r,C1)),w))),x),FreeQ(c,x))),And(FreeQ(List(a,b,m,p,q,r),x),EqQ(p,Times(q,Plus(r,C1))),NeQ(r,CN1),IntegerQ(Times(p,Power(Plus(r,C1),CN1))))))
  );
}
