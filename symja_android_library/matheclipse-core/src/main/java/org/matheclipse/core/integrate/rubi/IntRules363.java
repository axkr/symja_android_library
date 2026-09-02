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
class IntRules363 { 
  public static IAST RULES = List( 
IIntegrate(7261,Integrate(Times(u_DEFAULT,Power(v_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(y_,n_))),p_DEFAULT)),x_Symbol),
    Condition(Module(list(q,r),Condition(Simp(Dist(Times(q,r),Subst(Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Power(x,n))),p)),x),x,y),x),x),And(Not(FalseQ(Set(r,Divides(Power(y,m),Power(v,m),x)))),Not(FalseQ(Set(q,DerivativeDivides(y,u,x))))))),FreeQ(List(a,b,m,n,p),x))),
IIntegrate(7262,Integrate(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(c_DEFAULT,Power(v_,$p("n2",true))),Times(b_DEFAULT,Power(y_,n_))),p_)),x_Symbol),
    Condition(With(list(Set(q,DerivativeDivides(y,u,x))),Condition(Simp(Dist(q,Subst(Integrate(Power(Plus(a,Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n)))),p),x),x,y),x),x),Not(FalseQ(q)))),And(FreeQ(List(a,b,c,n,p),x),EqQ($s("n2"),Times(C2,n)),EqQ(v,y)))),
IIntegrate(7263,Integrate(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(v_,n_)),Times(c_DEFAULT,Power(w_,$p("n2",true)))),p_DEFAULT),Plus(A_,Times(B_DEFAULT,Power(y_,n_)))),x_Symbol),
    Condition(With(list(Set(q,DerivativeDivides(y,u,x))),Condition(Simp(Dist(q,Subst(Integrate(Times(Plus(ASymbol,Times(BSymbol,Power(x,n))),Power(Plus(a,Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n)))),p)),x),x,y),x),x),Not(FalseQ(q)))),And(FreeQ(List(a,b,c,ASymbol,BSymbol,n,p),x),EqQ($s("n2"),Times(C2,n)),EqQ(v,y),EqQ(w,y)))),
IIntegrate(7264,Integrate(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(c_DEFAULT,Power(w_,$p("n2",true)))),p_DEFAULT),Plus(A_,Times(B_DEFAULT,Power(y_,n_)))),x_Symbol),
    Condition(With(list(Set(q,DerivativeDivides(y,u,x))),Condition(Simp(Dist(q,Subst(Integrate(Times(Plus(ASymbol,Times(BSymbol,Power(x,n))),Power(Plus(a,Times(c,Power(x,Times(C2,n)))),p)),x),x,y),x),x),Not(FalseQ(q)))),And(FreeQ(List(a,c,ASymbol,BSymbol,n,p),x),EqQ($s("n2"),Times(C2,n)),EqQ(w,y)))),
IIntegrate(7265,Integrate(Times(u_DEFAULT,Power(v_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(c_DEFAULT,Power(w_,$p("n2",true))),Times(b_DEFAULT,Power(y_,n_))),p_DEFAULT)),x_Symbol),
    Condition(Module(list(q,r),Condition(Simp(Dist(Times(q,r),Subst(Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n)))),p)),x),x,y),x),x),And(Not(FalseQ(Set(r,Divides(Power(y,m),Power(v,m),x)))),Not(FalseQ(Set(q,DerivativeDivides(y,u,x))))))),And(FreeQ(List(a,b,c,m,n,p),x),EqQ($s("n2"),Times(C2,n)),EqQ(w,y)))),
IIntegrate(7266,Integrate(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(v_,n_)),Times(c_DEFAULT,Power(w_,$p("n2",true)))),p_DEFAULT),Plus(A_,Times(B_DEFAULT,Power(y_,n_))),Power(z_,m_DEFAULT)),x_Symbol),
    Condition(Module(list(q,r),Condition(Simp(Dist(Times(q,r),Subst(Integrate(Times(Power(x,m),Plus(ASymbol,Times(BSymbol,Power(x,n))),Power(Plus(a,Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n)))),p)),x),x,y),x),x),And(Not(FalseQ(Set(r,Divides(Power(y,m),Power(z,m),x)))),Not(FalseQ(Set(q,DerivativeDivides(y,u,x))))))),And(FreeQ(List(a,b,c,ASymbol,BSymbol,m,n,p),x),EqQ($s("n2"),Times(C2,n)),EqQ(v,y),EqQ(w,y)))),
IIntegrate(7267,Integrate(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(c_DEFAULT,Power(w_,$p("n2",true)))),p_DEFAULT),Plus(A_,Times(B_DEFAULT,Power(y_,n_))),Power(z_,m_DEFAULT)),x_Symbol),
    Condition(Module(list(q,r),Condition(Simp(Dist(Times(q,r),Subst(Integrate(Times(Power(x,m),Plus(ASymbol,Times(BSymbol,Power(x,n))),Power(Plus(a,Times(c,Power(x,Times(C2,n)))),p)),x),x,y),x),x),And(Not(FalseQ(Set(r,Divides(Power(y,m),Power(z,m),x)))),Not(FalseQ(Set(q,DerivativeDivides(y,u,x))))))),And(FreeQ(List(a,c,ASymbol,BSymbol,m,n,p),x),EqQ($s("n2"),Times(C2,n)),EqQ(w,y)))),
IIntegrate(7268,Integrate(Times(u_DEFAULT,Power(Plus(c_DEFAULT,Times(d_DEFAULT,Power(v_,n_))),p_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(y_,n_))),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(q,DerivativeDivides(y,u,x))),Condition(Simp(Dist(q,Subst(Integrate(Times(Power(Plus(a,Times(b,Power(x,n))),m),Power(Plus(c,Times(d,Power(x,n))),p)),x),x,y),x),x),Not(FalseQ(q)))),And(FreeQ(List(a,b,c,d,m,n,p),x),EqQ(v,y)))),
IIntegrate(7269,Integrate(Times(u_DEFAULT,Power(Plus(c_DEFAULT,Times(d_DEFAULT,Power(v_,n_))),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,Power(w_,n_))),q_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(y_,n_))),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(r,DerivativeDivides(y,u,x))),Condition(Simp(Dist(r,Subst(Integrate(Times(Power(Plus(a,Times(b,Power(x,n))),m),Power(Plus(c,Times(d,Power(x,n))),p),Power(Plus(e,Times(f,Power(x,n))),q)),x),x,y),x),x),Not(FalseQ(r)))),And(FreeQ(List(a,b,c,d,e,f,m,n,p,q),x),EqQ(v,y),EqQ(w,y)))),
IIntegrate(7270,Integrate(Times(Power(F_,v_),u_),x_Symbol),
    Condition(With(list(Set(q,DerivativeDivides(v,u,x))),Condition(Simp(Times(q,Power(FSymbol,v),Power(Log(FSymbol),CN1)),x),Not(FalseQ(q)))),FreeQ(FSymbol,x))),
IIntegrate(7271,Integrate(Times(Power(F_,v_),u_,Power(w_,m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(q,DerivativeDivides(v,u,x))),Condition(Simp(Dist(q,Subst(Integrate(Times(Power(x,m),Power(FSymbol,x)),x),x,v),x),x),Not(FalseQ(q)))),And(FreeQ(list(FSymbol,m),x),EqQ(w,v)))),
IIntegrate(7272,Integrate(Times(u_,Power(Plus(a_,Times(b_DEFAULT,Power(v_,p_DEFAULT),Power(w_,p_DEFAULT))),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(c,Simplify(Times(u,Power(Plus(Times(w,D(v,x)),Times(v,D(w,x))),CN1))))),Condition(Simp(Dist(c,Subst(Integrate(Power(Plus(a,Times(b,Power(x,p))),m),x),x,Times(v,w)),x),x),FreeQ(c,x))),And(FreeQ(List(a,b,m,p),x),IntegerQ(p)))),
IIntegrate(7273,Integrate(Times(u_,Power(v_,r_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(v_,p_DEFAULT),Power(w_,q_DEFAULT))),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(c,Simplify(Times(u,Power(Plus(Times(p,w,D(v,x)),Times(q,v,D(w,x))),CN1))))),Condition(Simp(Dist(Times(c,p,Power(Plus(r,C1),CN1)),Subst(Integrate(Power(Plus(a,Times(b,Power(x,Times(p,Power(Plus(r,C1),CN1))))),m),x),x,Times(Power(v,Plus(r,C1)),w)),x),x),FreeQ(c,x))),And(FreeQ(List(a,b,m,p,q,r),x),EqQ(p,Times(q,Plus(r,C1))),NeQ(r,CN1),IntegerQ(Times(p,Power(Plus(r,C1),CN1)))))),
IIntegrate(7274,Integrate(Times(u_,Power(v_,r_DEFAULT),Power(w_,s_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(v_,p_DEFAULT),Power(w_,q_DEFAULT))),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(c,Simplify(Times(u,Power(Plus(Times(p,w,D(v,x)),Times(q,v,D(w,x))),CN1))))),Condition(Simp(Dist(Times(c,p,Power(Plus(r,C1),CN1)),Subst(Integrate(Power(Plus(a,Times(b,Power(x,Times(p,Power(Plus(r,C1),CN1))))),m),x),x,Times(Power(v,Plus(r,C1)),Power(w,Plus(s,C1)))),x),x),FreeQ(c,x))),And(FreeQ(List(a,b,m,p,q,r,s),x),EqQ(Times(p,Plus(s,C1)),Times(q,Plus(r,C1))),NeQ(r,CN1),IntegerQ(Times(p,Power(Plus(r,C1),CN1)))))),
IIntegrate(7275,Integrate(Times(u_,Power(Plus(Times(a_DEFAULT,Power(v_,p_DEFAULT)),Times(b_DEFAULT,Power(w_,q_DEFAULT))),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(c,Simplify(Times(u,Power(Subtract(Times(p,w,D(v,x)),Times(q,v,D(w,x))),CN1))))),Condition(Simp(Dist(Times(c,p),Subst(Integrate(Power(Plus(b,Times(a,Power(x,p))),m),x),x,Times(v,Power(w,Plus(Times(m,q),C1)))),x),x),FreeQ(c,x))),And(FreeQ(List(a,b,m,p,q),x),EqQ(Plus(p,Times(q,Plus(Times(m,p),C1))),C0),IntegerQ(p),IntegerQ(m)))),
IIntegrate(7276,Integrate(Times(u_,Power(v_,r_DEFAULT),Power(Plus(Times(a_DEFAULT,Power(v_,p_DEFAULT)),Times(b_DEFAULT,Power(w_,q_DEFAULT))),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(c,Simplify(Times(u,Power(Subtract(Times(p,w,D(v,x)),Times(q,v,D(w,x))),CN1))))),Condition(Simp(Dist(Times(CN1,c,q),Subst(Integrate(Power(Plus(a,Times(b,Power(x,q))),m),x),x,Times(Power(v,Plus(Times(m,p),r,C1)),w)),x),x),FreeQ(c,x))),And(FreeQ(List(a,b,m,p,q,r),x),EqQ(Plus(p,Times(q,Plus(Times(m,p),r,C1))),C0),IntegerQ(q),IntegerQ(m)))),
IIntegrate(7277,Integrate(Times(u_,Power(w_,s_DEFAULT),Power(Plus(Times(a_DEFAULT,Power(v_,p_DEFAULT)),Times(b_DEFAULT,Power(w_,q_DEFAULT))),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(c,Simplify(Times(u,Power(Subtract(Times(p,w,D(v,x)),Times(q,v,D(w,x))),CN1))))),Condition(Simp(Dist(Times(CN1,c,q,Power(Plus(s,C1),CN1)),Subst(Integrate(Power(Plus(a,Times(b,Power(x,Times(q,Power(Plus(s,C1),CN1))))),m),x),x,Times(Power(v,Plus(Times(m,p),C1)),Power(w,Plus(s,C1)))),x),x),FreeQ(c,x))),And(FreeQ(List(a,b,m,p,q,s),x),EqQ(Plus(Times(p,Plus(s,C1)),Times(q,Plus(Times(m,p),C1))),C0),NeQ(s,CN1),IntegerQ(Times(q,Power(Plus(s,C1),CN1))),IntegerQ(m)))),
IIntegrate(7278,Integrate(Times(u_,Power(v_,r_DEFAULT),Power(w_,s_DEFAULT),Power(Plus(Times(a_DEFAULT,Power(v_,p_DEFAULT)),Times(b_DEFAULT,Power(w_,q_DEFAULT))),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(c,Simplify(Times(u,Power(Subtract(Times(p,w,D(v,x)),Times(q,v,D(w,x))),CN1))))),Condition(Simp(Dist(Times(CN1,c,q,Power(Plus(s,C1),CN1)),Subst(Integrate(Power(Plus(a,Times(b,Power(x,Times(q,Power(Plus(s,C1),CN1))))),m),x),x,Times(Power(v,Plus(Times(m,p),r,C1)),Power(w,Plus(s,C1)))),x),x),FreeQ(c,x))),And(FreeQ(List(a,b,m,p,q,r,s),x),EqQ(Plus(Times(p,Plus(s,C1)),Times(q,Plus(Times(m,p),r,C1))),C0),NeQ(s,CN1),IntegerQ(Times(q,Power(Plus(s,C1),CN1))),IntegerQ(m)))),
IIntegrate(7279,Integrate(Times(u_,Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Plus(m,C1),CN1),Subst(Integrate(SubstFor(Power(x,Plus(m,C1)),u,x),x),x,Power(x,Plus(m,C1))),x),x),And(FreeQ(m,x),NeQ(m,CN1),FunctionOfQ(Power(x,Plus(m,C1)),u,x)))),
IIntegrate(7280,Integrate(u_,x_Symbol),
    With(list(Set($s("lst"),SubstForFractionalPowerOfLinear(u,x))),Condition(Simp(Dist(Times(Part($s("lst"),C2),Part($s("lst"),C4)),Subst(Integrate(Part($s("lst"),C1),x),x,Power(Part($s("lst"),C3),Power(Part($s("lst"),C2),CN1))),x),x),And(Not(FalseQ($s("lst"))),SubstForFractionalPowerQ(u,Part($s("lst"),C3),x)))))
  );
}
