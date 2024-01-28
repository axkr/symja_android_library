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
class IntRules363 { 
  public static IAST RULES = List( 
IIntegrate(7260,Integrate(Times(u_,Power(v_,r_DEFAULT),Power(w_,s_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(v_,p_DEFAULT),Power(w_,q_DEFAULT))),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(c,Simplify(Times(u,Power(Plus(Times(p,w,D(v,x)),Times(q,v,D(w,x))),CN1))))),Condition(Simp(Star(Times(c,p,Power(Plus(r,C1),CN1)),Subst(Integrate(Power(Plus(a,Times(b,Power(x,Times(p,Power(Plus(r,C1),CN1))))),m),x),x,Times(Power(v,Plus(r,C1)),Power(w,Plus(s,C1))))),x),FreeQ(c,x))),And(FreeQ(List(a,b,m,p,q,r,s),x),EqQ(Times(p,Plus(s,C1)),Times(q,Plus(r,C1))),NeQ(r,CN1),IntegerQ(Times(p,Power(Plus(r,C1),CN1)))))),
IIntegrate(7261,Integrate(Times(u_,Power(Plus(Times(a_DEFAULT,Power(v_,p_DEFAULT)),Times(b_DEFAULT,Power(w_,q_DEFAULT))),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(c,Simplify(Times(u,Power(Subtract(Times(p,w,D(v,x)),Times(q,v,D(w,x))),CN1))))),Condition(Simp(Star(Times(c,p),Subst(Integrate(Power(Plus(b,Times(a,Power(x,p))),m),x),x,Times(v,Power(w,Plus(Times(m,q),C1))))),x),FreeQ(c,x))),And(FreeQ(List(a,b,m,p,q),x),EqQ(Plus(p,Times(q,Plus(Times(m,p),C1))),C0),IntegerQ(p),IntegerQ(m)))),
IIntegrate(7262,Integrate(Times(u_,Power(v_,r_DEFAULT),Power(Plus(Times(a_DEFAULT,Power(v_,p_DEFAULT)),Times(b_DEFAULT,Power(w_,q_DEFAULT))),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(c,Simplify(Times(u,Power(Subtract(Times(p,w,D(v,x)),Times(q,v,D(w,x))),CN1))))),Condition(Simp(Star(Times(CN1,c,q),Subst(Integrate(Power(Plus(a,Times(b,Power(x,q))),m),x),x,Times(Power(v,Plus(Times(m,p),r,C1)),w))),x),FreeQ(c,x))),And(FreeQ(List(a,b,m,p,q,r),x),EqQ(Plus(p,Times(q,Plus(Times(m,p),r,C1))),C0),IntegerQ(q),IntegerQ(m)))),
IIntegrate(7263,Integrate(Times(u_,Power(w_,s_DEFAULT),Power(Plus(Times(a_DEFAULT,Power(v_,p_DEFAULT)),Times(b_DEFAULT,Power(w_,q_DEFAULT))),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(c,Simplify(Times(u,Power(Subtract(Times(p,w,D(v,x)),Times(q,v,D(w,x))),CN1))))),Condition(Simp(Star(Times(CN1,c,q,Power(Plus(s,C1),CN1)),Subst(Integrate(Power(Plus(a,Times(b,Power(x,Times(q,Power(Plus(s,C1),CN1))))),m),x),x,Times(Power(v,Plus(Times(m,p),C1)),Power(w,Plus(s,C1))))),x),FreeQ(c,x))),And(FreeQ(List(a,b,m,p,q,s),x),EqQ(Plus(Times(p,Plus(s,C1)),Times(q,Plus(Times(m,p),C1))),C0),NeQ(s,CN1),IntegerQ(Times(q,Power(Plus(s,C1),CN1))),IntegerQ(m)))),
IIntegrate(7264,Integrate(Times(u_,Power(v_,r_DEFAULT),Power(w_,s_DEFAULT),Power(Plus(Times(a_DEFAULT,Power(v_,p_DEFAULT)),Times(b_DEFAULT,Power(w_,q_DEFAULT))),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(c,Simplify(Times(u,Power(Subtract(Times(p,w,D(v,x)),Times(q,v,D(w,x))),CN1))))),Condition(Simp(Star(Times(CN1,c,q,Power(Plus(s,C1),CN1)),Subst(Integrate(Power(Plus(a,Times(b,Power(x,Times(q,Power(Plus(s,C1),CN1))))),m),x),x,Times(Power(v,Plus(Times(m,p),r,C1)),Power(w,Plus(s,C1))))),x),FreeQ(c,x))),And(FreeQ(List(a,b,m,p,q,r,s),x),EqQ(Plus(Times(p,Plus(s,C1)),Times(q,Plus(Times(m,p),r,C1))),C0),NeQ(s,CN1),IntegerQ(Times(q,Power(Plus(s,C1),CN1))),IntegerQ(m)))),
IIntegrate(7265,Integrate(Times(u_,Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Plus(m,C1),CN1),Subst(Integrate(SubstFor(Power(x,Plus(m,C1)),u,x),x),x,Power(x,Plus(m,C1)))),x),And(FreeQ(m,x),NeQ(m,CN1),FunctionOfQ(Power(x,Plus(m,C1)),u,x)))),
IIntegrate(7266,Integrate(u_,x_Symbol),
    With(list(Set($s("lst"),SubstForFractionalPowerOfLinear(u,x))),Condition(Simp(Star(Times(Part($s("lst"),C2),Part($s("lst"),C4)),Subst(Integrate(Part($s("lst"),C1),x),x,Power(Part($s("lst"),C3),Power(Part($s("lst"),C2),CN1)))),x),And(Not(FalseQ($s("lst"))),SubstForFractionalPowerQ(u,Part($s("lst"),C3),x))))),
IIntegrate(7267,Integrate(u_,x_Symbol),
    With(list(Set($s("lst"),SubstForFractionalPowerOfQuotientOfLinears(u,x))),Condition(Simp(Star(Times(Part($s("lst"),C2),Part($s("lst"),C4)),Subst(Integrate(Part($s("lst"),C1),x),x,Power(Part($s("lst"),C3),Power(Part($s("lst"),C2),CN1)))),x),Not(FalseQ($s("lst")))))),
IIntegrate(7268,Integrate(Times(u_DEFAULT,Power(Times(a_DEFAULT,Power(v_,m_DEFAULT),Power(w_,n_DEFAULT),Power(z_,q_DEFAULT)),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(a,IntPart(p)),Power(Times(a,Power(v,m),Power(w,n),Power(z,q)),FracPart(p)),Power(Times(Power(v,Times(m,FracPart(p))),Power(w,Times(n,FracPart(p))),Power(z,Times(q,FracPart(p)))),CN1)),Integrate(Times(u,Power(v,Times(m,p)),Power(w,Times(n,p)),Power(z,Times(p,q))),x)),x),And(FreeQ(List(a,m,n,p,q),x),Not(IntegerQ(p)),Not(FreeQ(v,x)),Not(FreeQ(w,x)),Not(FreeQ(z,x))))),
IIntegrate(7269,Integrate(Times(u_DEFAULT,Power(Times(a_DEFAULT,Power(v_,m_DEFAULT),Power(w_,n_DEFAULT)),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(a,IntPart(p)),Power(Times(a,Power(v,m),Power(w,n)),FracPart(p)),Power(Times(Power(v,Times(m,FracPart(p))),Power(w,Times(n,FracPart(p)))),CN1)),Integrate(Times(u,Power(v,Times(m,p)),Power(w,Times(n,p))),x)),x),And(FreeQ(List(a,m,n,p),x),Not(IntegerQ(p)),Not(FreeQ(v,x)),Not(FreeQ(w,x))))),
IIntegrate(7270,Integrate(Times(u_DEFAULT,Power(Times(a_DEFAULT,Power(v_,m_DEFAULT)),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(a,IntPart(p)),Power(Times(a,Power(v,m)),FracPart(p)),Power(Power(v,Times(m,FracPart(p))),CN1)),Integrate(Times(u,Power(v,Times(m,p))),x)),x),And(FreeQ(list(a,m,p),x),Not(IntegerQ(p)),Not(FreeQ(v,x)),Not(And(EqQ(a,C1),EqQ(m,C1))),Not(And(EqQ(v,x),EqQ(m,C1)))))),
IIntegrate(7271,Integrate(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(b,IntPart(p)),Power(Plus(a,Times(b,Power(x,n))),FracPart(p)),Power(Times(Power(x,Times(n,FracPart(p))),Power(Plus(C1,Times(a,Power(Times(Power(x,n),b),CN1))),FracPart(p))),CN1)),Integrate(Times(u,Power(x,Times(n,p)),Power(Plus(C1,Times(a,Power(Times(Power(x,n),b),CN1))),p)),x)),x),And(FreeQ(list(a,b,p),x),Not(IntegerQ(p)),ILtQ(n,C0),Not(RationalFunctionQ(u,x)),IntegerQ(Plus(p,C1D2))))),
IIntegrate(7272,Integrate(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(v_,n_))),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(Plus(a,Times(b,Power(v,n))),FracPart(p)),Power(Times(Power(v,Times(n,FracPart(p))),Power(Plus(b,Times(a,Power(Power(v,n),CN1))),FracPart(p))),CN1)),Integrate(Times(u,Power(v,Times(n,p)),Power(Plus(b,Times(a,Power(Power(v,n),CN1))),p)),x)),x),And(FreeQ(list(a,b,p),x),Not(IntegerQ(p)),ILtQ(n,C0),BinomialQ(v,x),Not(LinearQ(v,x))))),
IIntegrate(7273,Integrate(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(v_,n_),Power(x_,m_DEFAULT))),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(Plus(a,Times(b,Power(x,m),Power(v,n))),FracPart(p)),Power(Times(Power(v,Times(n,FracPart(p))),Power(Plus(Times(b,Power(x,m)),Times(a,Power(Power(v,n),CN1))),FracPart(p))),CN1)),Integrate(Times(u,Power(v,Times(n,p)),Power(Plus(Times(b,Power(x,m)),Times(a,Power(Power(v,n),CN1))),p)),x)),x),And(FreeQ(List(a,b,m,p),x),Not(IntegerQ(p)),ILtQ(n,C0),BinomialQ(v,x)))),
IIntegrate(7274,Integrate(Times(u_DEFAULT,Power(Plus(Times(a_DEFAULT,Power(x_,r_DEFAULT)),Times(b_DEFAULT,Power(x_,s_DEFAULT))),m_)),x_Symbol),
    Condition(With(list(Set(v,Times(Power(Plus(Times(a,Power(x,r)),Times(b,Power(x,s))),FracPart(m)),Power(Times(Power(x,Times(r,FracPart(m))),Power(Plus(a,Times(b,Power(x,Subtract(s,r)))),FracPart(m))),CN1)))),Condition(Simp(Star(v,Integrate(Times(u,Power(x,Times(m,r)),Power(Plus(a,Times(b,Power(x,Subtract(s,r)))),m)),x)),x),NeQ(Simplify(v),C1))),And(FreeQ(List(a,b,m,r,s),x),Not(IntegerQ(m)),PosQ(Subtract(s,r))))),
IIntegrate(7275,Integrate(Times(u_,Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),CN1)),x_Symbol),
    Condition(With(list(Set(v,RationalFunctionExpand(Times(u,Power(Plus(a,Times(b,Power(x,n))),CN1)),x))),Condition(Integrate(v,x),SumQ(v))),And(FreeQ(list(a,b),x),IGtQ(n,C0)))),
IIntegrate(7276,Integrate(Times(u_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(c_DEFAULT,Power(x_,$p("n2",true)))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Times(Power(C4,p),Power(c,p)),CN1),Integrate(Times(u,Power(Plus(b,Times(C2,c,Power(x,n))),Times(C2,p))),x)),x),And(FreeQ(List(a,b,c,n),x),EqQ($s("n2"),Times(C2,n)),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0),IntegerQ(p),Not(AlgebraicFunctionQ(u,x))))),
IIntegrate(7277,Integrate(Times(u_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(c_DEFAULT,Power(x_,$p("n2",true)))),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(Plus(a,Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n)))),p),Power(Power(Plus(b,Times(C2,c,Power(x,n))),Times(C2,p)),CN1)),Integrate(Times(u,Power(Plus(b,Times(C2,c,Power(x,n))),Times(C2,p))),x)),x),And(FreeQ(List(a,b,c,n,p),x),EqQ($s("n2"),Times(C2,n)),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0),Not(IntegerQ(p)),Not(AlgebraicFunctionQ(u,x))))),
IIntegrate(7278,Integrate(Times(u_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(c_DEFAULT,Power(x_,$p("n2",true)))),CN1)),x_Symbol),
    Condition(With(list(Set(v,RationalFunctionExpand(Times(u,Power(Plus(a,Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n)))),CN1)),x))),Condition(Integrate(v,x),SumQ(v))),And(FreeQ(list(a,b,c),x),EqQ($s("n2"),Times(C2,n)),IGtQ(n,C0)))),
IIntegrate(7279,Integrate(Times(u_DEFAULT,Power(Plus(Times(a_DEFAULT,Power(x_,m_DEFAULT)),Times(b_DEFAULT,Sqrt(Times(c_DEFAULT,Power(x_,n_))))),CN1)),x_Symbol),
    Condition(Integrate(Times(u,Subtract(Times(a,Power(x,m)),Times(b,Sqrt(Times(c,Power(x,n))))),Power(Subtract(Times(Sqr(a),Power(x,Times(C2,m))),Times(Sqr(b),c,Power(x,n))),CN1)),x),FreeQ(List(a,b,c,m,n),x)))
  );
}
