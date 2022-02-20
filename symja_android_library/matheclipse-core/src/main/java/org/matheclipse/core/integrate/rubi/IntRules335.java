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
class IntRules335 { 
  public static IAST RULES = List( 
IIntegrate(6700,Integrate(Times(u_DEFAULT,Power(v_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(c_DEFAULT,Power(w_,$p("n2",true))),Times(b_DEFAULT,Power(y_,n_))),p_DEFAULT)),x_Symbol),
    Condition(Module(list(q,r),Condition(Dist(Times(q,r),Subst(Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n)))),p)),x),x,y),x),And(Not(FalseQ(Set(r,Divides(Power(y,m),Power(v,m),x)))),Not(FalseQ(Set(q,DerivativeDivides(y,u,x))))))),And(FreeQ(List(a,b,c,m,n,p),x),EqQ($s("n2"),Times(C2,n)),EqQ(w,y)))),
IIntegrate(6701,Integrate(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(v_,n_)),Times(c_DEFAULT,Power(w_,$p("n2",true)))),p_DEFAULT),Plus(A_,Times(B_DEFAULT,Power(y_,n_))),Power(z_,m_DEFAULT)),x_Symbol),
    Condition(Module(list(q,r),Condition(Dist(Times(q,r),Subst(Integrate(Times(Power(x,m),Plus(ASymbol,Times(BSymbol,Power(x,n))),Power(Plus(a,Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n)))),p)),x),x,y),x),And(Not(FalseQ(Set(r,Divides(Power(y,m),Power(z,m),x)))),Not(FalseQ(Set(q,DerivativeDivides(y,u,x))))))),And(FreeQ(List(a,b,c,ASymbol,BSymbol,m,n,p),x),EqQ($s("n2"),Times(C2,n)),EqQ(v,y),EqQ(w,y)))),
IIntegrate(6702,Integrate(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(c_DEFAULT,Power(w_,$p("n2",true)))),p_DEFAULT),Plus(A_,Times(B_DEFAULT,Power(y_,n_))),Power(z_,m_DEFAULT)),x_Symbol),
    Condition(Module(list(q,r),Condition(Dist(Times(q,r),Subst(Integrate(Times(Power(x,m),Plus(ASymbol,Times(BSymbol,Power(x,n))),Power(Plus(a,Times(c,Power(x,Times(C2,n)))),p)),x),x,y),x),And(Not(FalseQ(Set(r,Divides(Power(y,m),Power(z,m),x)))),Not(FalseQ(Set(q,DerivativeDivides(y,u,x))))))),And(FreeQ(List(a,c,ASymbol,BSymbol,m,n,p),x),EqQ($s("n2"),Times(C2,n)),EqQ(w,y)))),
IIntegrate(6703,Integrate(Times(u_DEFAULT,Power(Plus(c_DEFAULT,Times(d_DEFAULT,Power(v_,n_))),p_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(y_,n_))),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(q,DerivativeDivides(y,u,x))),Condition(Dist(q,Subst(Integrate(Times(Power(Plus(a,Times(b,Power(x,n))),m),Power(Plus(c,Times(d,Power(x,n))),p)),x),x,y),x),Not(FalseQ(q)))),And(FreeQ(List(a,b,c,d,m,n,p),x),EqQ(v,y)))),
IIntegrate(6704,Integrate(Times(u_DEFAULT,Power(Plus(c_DEFAULT,Times(d_DEFAULT,Power(v_,n_))),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,Power(w_,n_))),q_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(y_,n_))),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(r,DerivativeDivides(y,u,x))),Condition(Dist(r,Subst(Integrate(Times(Power(Plus(a,Times(b,Power(x,n))),m),Power(Plus(c,Times(d,Power(x,n))),p),Power(Plus(e,Times(f,Power(x,n))),q)),x),x,y),x),Not(FalseQ(r)))),And(FreeQ(List(a,b,c,d,e,f,m,n,p,q),x),EqQ(v,y),EqQ(w,y)))),
IIntegrate(6705,Integrate(Times(Power(F_,v_),u_),x_Symbol),
    Condition(With(list(Set(q,DerivativeDivides(v,u,x))),Condition(Simp(Times(q,Power(FSymbol,v),Power(Log(FSymbol),CN1)),x),Not(FalseQ(q)))),FreeQ(FSymbol,x))),
IIntegrate(6706,Integrate(Times(Power(F_,v_),u_,Power(w_,m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(q,DerivativeDivides(v,u,x))),Condition(Dist(q,Subst(Integrate(Times(Power(x,m),Power(FSymbol,x)),x),x,v),x),Not(FalseQ(q)))),And(FreeQ(list(FSymbol,m),x),EqQ(w,v)))),
IIntegrate(6707,Integrate(Times(u_,Power(Plus(a_,Times(b_DEFAULT,Power(v_,p_DEFAULT),Power(w_,p_DEFAULT))),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(c,Simplify(Times(u,Power(Plus(Times(w,D(v,x)),Times(v,D(w,x))),CN1))))),Condition(Dist(c,Subst(Integrate(Power(Plus(a,Times(b,Power(x,p))),m),x),x,Times(v,w)),x),FreeQ(c,x))),And(FreeQ(List(a,b,m,p),x),IntegerQ(p)))),
IIntegrate(6708,Integrate(Times(u_,Power(v_,r_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(v_,p_DEFAULT),Power(w_,q_DEFAULT))),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(c,Simplify(Times(u,Power(Plus(Times(p,w,D(v,x)),Times(q,v,D(w,x))),CN1))))),Condition(Dist(Times(c,p,Power(Plus(r,C1),CN1)),Subst(Integrate(Power(Plus(a,Times(b,Power(x,Times(p,Power(Plus(r,C1),CN1))))),m),x),x,Times(Power(v,Plus(r,C1)),w)),x),FreeQ(c,x))),And(FreeQ(List(a,b,m,p,q,r),x),EqQ(p,Times(q,Plus(r,C1))),NeQ(r,CN1),IntegerQ(Times(p,Power(Plus(r,C1),CN1)))))),
IIntegrate(6709,Integrate(Times(u_,Power(v_,r_DEFAULT),Power(w_,s_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(v_,p_DEFAULT),Power(w_,q_DEFAULT))),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(c,Simplify(Times(u,Power(Plus(Times(p,w,D(v,x)),Times(q,v,D(w,x))),CN1))))),Condition(Dist(Times(c,p,Power(Plus(r,C1),CN1)),Subst(Integrate(Power(Plus(a,Times(b,Power(x,Times(p,Power(Plus(r,C1),CN1))))),m),x),x,Times(Power(v,Plus(r,C1)),Power(w,Plus(s,C1)))),x),FreeQ(c,x))),And(FreeQ(List(a,b,m,p,q,r,s),x),EqQ(Times(p,Plus(s,C1)),Times(q,Plus(r,C1))),NeQ(r,CN1),IntegerQ(Times(p,Power(Plus(r,C1),CN1)))))),
IIntegrate(6710,Integrate(Times(u_,Power(Plus(Times(a_DEFAULT,Power(v_,p_DEFAULT)),Times(b_DEFAULT,Power(w_,q_DEFAULT))),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(c,Simplify(Times(u,Power(Subtract(Times(p,w,D(v,x)),Times(q,v,D(w,x))),CN1))))),Condition(Dist(Times(c,p),Subst(Integrate(Power(Plus(b,Times(a,Power(x,p))),m),x),x,Times(v,Power(w,Plus(Times(m,q),C1)))),x),FreeQ(c,x))),And(FreeQ(List(a,b,m,p,q),x),EqQ(Plus(p,Times(q,Plus(Times(m,p),C1))),C0),IntegerQ(p),IntegerQ(m)))),
IIntegrate(6711,Integrate(Times(u_,Power(v_,r_DEFAULT),Power(Plus(Times(a_DEFAULT,Power(v_,p_DEFAULT)),Times(b_DEFAULT,Power(w_,q_DEFAULT))),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(c,Simplify(Times(u,Power(Subtract(Times(p,w,D(v,x)),Times(q,v,D(w,x))),CN1))))),Condition(Negate(Dist(Times(c,q),Subst(Integrate(Power(Plus(a,Times(b,Power(x,q))),m),x),x,Times(Power(v,Plus(Times(m,p),r,C1)),w)),x)),FreeQ(c,x))),And(FreeQ(List(a,b,m,p,q,r),x),EqQ(Plus(p,Times(q,Plus(Times(m,p),r,C1))),C0),IntegerQ(q),IntegerQ(m)))),
IIntegrate(6712,Integrate(Times(u_,Power(w_,s_DEFAULT),Power(Plus(Times(a_DEFAULT,Power(v_,p_DEFAULT)),Times(b_DEFAULT,Power(w_,q_DEFAULT))),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(c,Simplify(Times(u,Power(Subtract(Times(p,w,D(v,x)),Times(q,v,D(w,x))),CN1))))),Condition(Negate(Dist(Times(c,q,Power(Plus(s,C1),CN1)),Subst(Integrate(Power(Plus(a,Times(b,Power(x,Times(q,Power(Plus(s,C1),CN1))))),m),x),x,Times(Power(v,Plus(Times(m,p),C1)),Power(w,Plus(s,C1)))),x)),FreeQ(c,x))),And(FreeQ(List(a,b,m,p,q,s),x),EqQ(Plus(Times(p,Plus(s,C1)),Times(q,Plus(Times(m,p),C1))),C0),NeQ(s,CN1),IntegerQ(Times(q,Power(Plus(s,C1),CN1))),IntegerQ(m)))),
IIntegrate(6713,Integrate(Times(u_,Power(v_,r_DEFAULT),Power(w_,s_DEFAULT),Power(Plus(Times(a_DEFAULT,Power(v_,p_DEFAULT)),Times(b_DEFAULT,Power(w_,q_DEFAULT))),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(c,Simplify(Times(u,Power(Subtract(Times(p,w,D(v,x)),Times(q,v,D(w,x))),CN1))))),Condition(Negate(Dist(Times(c,q,Power(Plus(s,C1),CN1)),Subst(Integrate(Power(Plus(a,Times(b,Power(x,Times(q,Power(Plus(s,C1),CN1))))),m),x),x,Times(Power(v,Plus(Times(m,p),r,C1)),Power(w,Plus(s,C1)))),x)),FreeQ(c,x))),And(FreeQ(List(a,b,m,p,q,r,s),x),EqQ(Plus(Times(p,Plus(s,C1)),Times(q,Plus(Times(m,p),r,C1))),C0),NeQ(s,CN1),IntegerQ(Times(q,Power(Plus(s,C1),CN1))),IntegerQ(m)))),
IIntegrate(6714,Integrate(Times(u_,Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Plus(m,C1),CN1),Subst(Integrate(SubstFor(Power(x,Plus(m,C1)),u,x),x),x,Power(x,Plus(m,C1))),x),And(FreeQ(m,x),NeQ(m,CN1),FunctionOfQ(Power(x,Plus(m,C1)),u,x)))),
IIntegrate(6715,Integrate(u_,x_Symbol),
    With(list(Set($s("lst"),SubstForFractionalPowerOfLinear(u,x))),Condition(Dist(Times(Part($s("lst"),C2),Part($s("lst"),C4)),Subst(Integrate(Part($s("lst"),C1),x),x,Power(Part($s("lst"),C3),Power(Part($s("lst"),C2),CN1))),x),And(Not(FalseQ($s("lst"))),SubstForFractionalPowerQ(u,Part($s("lst"),C3),x))))),
IIntegrate(6716,Integrate(u_,x_Symbol),
    With(list(Set($s("lst"),SubstForFractionalPowerOfQuotientOfLinears(u,x))),Condition(Dist(Times(Part($s("lst"),C2),Part($s("lst"),C4)),Subst(Integrate(Part($s("lst"),C1),x),x,Power(Part($s("lst"),C3),Power(Part($s("lst"),C2),CN1))),x),Not(FalseQ($s("lst")))))),
IIntegrate(6717,Integrate(Times(u_DEFAULT,Power(Times(a_DEFAULT,Power(v_,m_DEFAULT),Power(w_,n_DEFAULT),Power(z_,q_DEFAULT)),p_)),x_Symbol),
    Condition(Dist(Times(Power(a,IntPart(p)),Power(Times(a,Power(v,m),Power(w,n),Power(z,q)),FracPart(p)),Power(Times(Power(v,Times(m,FracPart(p))),Power(w,Times(n,FracPart(p))),Power(z,Times(q,FracPart(p)))),CN1)),Integrate(Times(u,Power(v,Times(m,p)),Power(w,Times(n,p)),Power(z,Times(p,q))),x),x),And(FreeQ(List(a,m,n,p,q),x),Not(IntegerQ(p)),Not(FreeQ(v,x)),Not(FreeQ(w,x)),Not(FreeQ(z,x))))),
IIntegrate(6718,Integrate(Times(u_DEFAULT,Power(Times(a_DEFAULT,Power(v_,m_DEFAULT),Power(w_,n_DEFAULT)),p_)),x_Symbol),
    Condition(Dist(Times(Power(a,IntPart(p)),Power(Times(a,Power(v,m),Power(w,n)),FracPart(p)),Power(Times(Power(v,Times(m,FracPart(p))),Power(w,Times(n,FracPart(p)))),CN1)),Integrate(Times(u,Power(v,Times(m,p)),Power(w,Times(n,p))),x),x),And(FreeQ(List(a,m,n,p),x),Not(IntegerQ(p)),Not(FreeQ(v,x)),Not(FreeQ(w,x))))),
IIntegrate(6719,Integrate(Times(u_DEFAULT,Power(Times(a_DEFAULT,Power(v_,m_DEFAULT)),p_)),x_Symbol),
    Condition(Dist(Times(Power(a,IntPart(p)),Power(Times(a,Power(v,m)),FracPart(p)),Power(Power(v,Times(m,FracPart(p))),CN1)),Integrate(Times(u,Power(v,Times(m,p))),x),x),And(FreeQ(list(a,m,p),x),Not(IntegerQ(p)),Not(FreeQ(v,x)),Not(And(EqQ(a,C1),EqQ(m,C1))),Not(And(EqQ(v,x),EqQ(m,C1))))))
  );
}
