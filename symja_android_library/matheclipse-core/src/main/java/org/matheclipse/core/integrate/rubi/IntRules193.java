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
class IntRules193 { 
  public static IAST RULES = List( 
IIntegrate(3861,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(n,CN1),Subst(Integrate(Times(Power(x,Subtract(Simplify(Times(Plus(m,C1),Power(n,CN1))),C1)),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,x))))),p)),x),x,Power(x,n))),x),And(FreeQ(List(a,b,c,d,m,n,p),x),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1)))),Or(EqQ(p,C1),EqQ(m,Subtract(n,C1)),And(IntegerQ(p),GtQ(Simplify(Times(Plus(m,C1),Power(n,CN1))),C0)))))),
IIntegrate(3862,Integrate(Times(Power(Times(e_,x_),m_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(x,n)))))),p)),x)),x),And(FreeQ(List(a,b,c,d,e,m,n,p),x),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1))))))),
IIntegrate(3863,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_,x_),m_)),x_Symbol),
    Condition(Simp(Star(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(x,n)))))),p)),x)),x),And(FreeQ(List(a,b,c,d,e,m,n,p),x),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1))))))),
IIntegrate(3864,Integrate(Times(Power(x_,m_DEFAULT),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_))))),x_Symbol),
    Condition(Simp(Star(Times(C2,Power(n,CN1)),Subst(Integrate(Sin(Plus(a,Times(b,Sqr(x)))),x),x,Power(x,Times(C1D2,n)))),x),And(FreeQ(List(a,b,m,n),x),EqQ(m,Subtract(Times(C1D2,n),C1))))),
IIntegrate(3865,Integrate(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(C2,Power(n,CN1)),Subst(Integrate(Cos(Plus(a,Times(b,Sqr(x)))),x),x,Power(x,Times(C1D2,n)))),x),And(FreeQ(List(a,b,m,n),x),EqQ(m,Subtract(Times(C1D2,n),C1))))),
IIntegrate(3866,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_))))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(e,Subtract(n,C1)),Power(Times(e,x),Plus(m,Negate(n),C1)),Cos(Plus(c,Times(d,Power(x,n)))),Power(Times(d,n),CN1)),x),Simp(Star(Times(Power(e,n),Plus(m,Negate(n),C1),Power(Times(d,n),CN1)),Integrate(Times(Power(Times(e,x),Subtract(m,n)),Cos(Plus(c,Times(d,Power(x,n))))),x)),x)),And(FreeQ(list(c,d,e),x),IGtQ(n,C0),LtQ(n,Plus(m,C1))))),
IIntegrate(3867,Integrate(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(e,Subtract(n,C1)),Power(Times(e,x),Plus(m,Negate(n),C1)),Sin(Plus(c,Times(d,Power(x,n)))),Power(Times(d,n),CN1)),x),Simp(Star(Times(Power(e,n),Plus(m,Negate(n),C1),Power(Times(d,n),CN1)),Integrate(Times(Power(Times(e,x),Subtract(m,n)),Sin(Plus(c,Times(d,Power(x,n))))),x)),x)),And(FreeQ(list(c,d,e),x),IGtQ(n,C0),LtQ(n,Plus(m,C1))))),
IIntegrate(3868,Integrate(Times(Power(Times(e_DEFAULT,x_),m_),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_))))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(e,x),Plus(m,C1)),Sin(Plus(c,Times(d,Power(x,n)))),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Star(Times(d,n,Power(Times(Power(e,n),Plus(m,C1)),CN1)),Integrate(Times(Power(Times(e,x),Plus(m,n)),Cos(Plus(c,Times(d,Power(x,n))))),x)),x)),And(FreeQ(list(c,d,e),x),IGtQ(n,C0),LtQ(m,CN1)))),
IIntegrate(3869,Integrate(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),Power(Times(e_DEFAULT,x_),m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(e,x),Plus(m,C1)),Cos(Plus(c,Times(d,Power(x,n)))),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Star(Times(d,n,Power(Times(Power(e,n),Plus(m,C1)),CN1)),Integrate(Times(Power(Times(e,x),Plus(m,n)),Sin(Plus(c,Times(d,Power(x,n))))),x)),x)),And(FreeQ(list(c,d,e),x),IGtQ(n,C0),LtQ(m,CN1)))),
IIntegrate(3870,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_))))),x_Symbol),
    Condition(Subtract(Simp(Star(Times(C1D2,CI),Integrate(Times(Power(Times(e,x),m),Exp(Subtract(Times(CN1,c,CI),Times(d,CI,Power(x,n))))),x)),x),Simp(Star(Times(C1D2,CI),Integrate(Times(Power(Times(e,x),m),Exp(Plus(Times(c,CI),Times(d,CI,Power(x,n))))),x)),x)),And(FreeQ(List(c,d,e,m),x),IGtQ(n,C0)))),
IIntegrate(3871,Integrate(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Star(C1D2,Integrate(Times(Power(Times(e,x),m),Exp(Subtract(Times(CN1,c,CI),Times(d,CI,Power(x,n))))),x)),x),Simp(Star(C1D2,Integrate(Times(Power(Times(e,x),m),Exp(Plus(Times(c,CI),Times(d,CI,Power(x,n))))),x)),x)),And(FreeQ(List(c,d,e,m),x),IGtQ(n,C0)))),
IIntegrate(3872,Integrate(Times(Power(x_,m_DEFAULT),Sqr(Sin(Plus(a_DEFAULT,Times(C1D2,b_DEFAULT,Power(x_,n_)))))),x_Symbol),
    Condition(Subtract(Simp(Star(C1D2,Integrate(Power(x,m),x)),x),Simp(Star(C1D2,Integrate(Times(Power(x,m),Cos(Plus(Times(C2,a),Times(b,Power(x,n))))),x)),x)),FreeQ(List(a,b,m,n),x))),
IIntegrate(3873,Integrate(Times(Sqr(Cos(Plus(a_DEFAULT,Times(C1D2,b_DEFAULT,Power(x_,n_))))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Star(C1D2,Integrate(Power(x,m),x)),x),Simp(Star(C1D2,Integrate(Times(Power(x,m),Cos(Plus(Times(C2,a),Times(b,Power(x,n))))),x)),x)),FreeQ(List(a,b,m,n),x))),
IIntegrate(3874,Integrate(Times(Power(x_,m_DEFAULT),Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Power(Sin(Plus(a,Times(b,Power(x,n)))),p),Power(Plus(m,C1),CN1)),x),Simp(Star(Times(b,n,p,Power(Plus(m,C1),CN1)),Integrate(Times(Power(Sin(Plus(a,Times(b,Power(x,n)))),Subtract(p,C1)),Cos(Plus(a,Times(b,Power(x,n))))),x)),x)),And(FreeQ(list(a,b),x),IGtQ(p,C1),EqQ(Plus(m,n),C0),NeQ(n,C1),IntegerQ(n)))),
IIntegrate(3875,Integrate(Times(Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Power(Cos(Plus(a,Times(b,Power(x,n)))),p),Power(Plus(m,C1),CN1)),x),Simp(Star(Times(b,n,p,Power(Plus(m,C1),CN1)),Integrate(Times(Power(Cos(Plus(a,Times(b,Power(x,n)))),Subtract(p,C1)),Sin(Plus(a,Times(b,Power(x,n))))),x)),x)),And(FreeQ(list(a,b),x),IGtQ(p,C1),EqQ(Plus(m,n),C0),NeQ(n,C1),IntegerQ(n)))),
IIntegrate(3876,Integrate(Times(Power(x_,m_DEFAULT),Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(n,Power(Sin(Plus(a,Times(b,Power(x,n)))),p),Power(Times(Sqr(b),Sqr(n),Sqr(p)),CN1)),x),Negate(Simp(Times(Power(x,n),Cos(Plus(a,Times(b,Power(x,n)))),Power(Sin(Plus(a,Times(b,Power(x,n)))),Subtract(p,C1)),Power(Times(b,n,p),CN1)),x)),Simp(Star(Times(Subtract(p,C1),Power(p,CN1)),Integrate(Times(Power(x,m),Power(Sin(Plus(a,Times(b,Power(x,n)))),Subtract(p,C2))),x)),x)),And(FreeQ(List(a,b,m,n),x),EqQ(Plus(m,Times(CN1,C2,n),C1),C0),GtQ(p,C1)))),
IIntegrate(3877,Integrate(Times(Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(n,Power(Cos(Plus(a,Times(b,Power(x,n)))),p),Power(Times(Sqr(b),Sqr(n),Sqr(p)),CN1)),x),Simp(Times(Power(x,n),Sin(Plus(a,Times(b,Power(x,n)))),Power(Cos(Plus(a,Times(b,Power(x,n)))),Subtract(p,C1)),Power(Times(b,n,p),CN1)),x),Simp(Star(Times(Subtract(p,C1),Power(p,CN1)),Integrate(Times(Power(x,m),Power(Cos(Plus(a,Times(b,Power(x,n)))),Subtract(p,C2))),x)),x)),And(FreeQ(List(a,b,m,n),x),EqQ(Plus(m,Times(CN1,C2,n),C1),C0),GtQ(p,C1)))),
IIntegrate(3878,Integrate(Times(Power(x_,m_DEFAULT),Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(Plus(m,Negate(n),C1),Power(x,Plus(m,Times(CN1,C2,n),C1)),Power(Sin(Plus(a,Times(b,Power(x,n)))),p),Power(Times(Sqr(b),Sqr(n),Sqr(p)),CN1)),x),Negate(Simp(Times(Power(x,Plus(m,Negate(n),C1)),Cos(Plus(a,Times(b,Power(x,n)))),Power(Sin(Plus(a,Times(b,Power(x,n)))),Subtract(p,C1)),Power(Times(b,n,p),CN1)),x)),Simp(Star(Times(Subtract(p,C1),Power(p,CN1)),Integrate(Times(Power(x,m),Power(Sin(Plus(a,Times(b,Power(x,n)))),Subtract(p,C2))),x)),x),Negate(Simp(Star(Times(Plus(m,Negate(n),C1),Plus(m,Times(CN1,C2,n),C1),Power(Times(Sqr(b),Sqr(n),Sqr(p)),CN1)),Integrate(Times(Power(x,Subtract(m,Times(C2,n))),Power(Sin(Plus(a,Times(b,Power(x,n)))),p)),x)),x))),And(FreeQ(list(a,b),x),GtQ(p,C1),IGtQ(n,C0),IGtQ(m,Subtract(Times(C2,n),C1))))),
IIntegrate(3879,Integrate(Times(Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Plus(m,Negate(n),C1),Power(x,Plus(m,Times(CN1,C2,n),C1)),Power(Cos(Plus(a,Times(b,Power(x,n)))),p),Power(Times(Sqr(b),Sqr(n),Sqr(p)),CN1)),x),Simp(Times(Power(x,Plus(m,Negate(n),C1)),Sin(Plus(a,Times(b,Power(x,n)))),Power(Cos(Plus(a,Times(b,Power(x,n)))),Subtract(p,C1)),Power(Times(b,n,p),CN1)),x),Simp(Star(Times(Subtract(p,C1),Power(p,CN1)),Integrate(Times(Power(x,m),Power(Cos(Plus(a,Times(b,Power(x,n)))),Subtract(p,C2))),x)),x),Negate(Simp(Star(Times(Plus(m,Negate(n),C1),Plus(m,Times(CN1,C2,n),C1),Power(Times(Sqr(b),Sqr(n),Sqr(p)),CN1)),Integrate(Times(Power(x,Subtract(m,Times(C2,n))),Power(Cos(Plus(a,Times(b,Power(x,n)))),p)),x)),x))),And(FreeQ(list(a,b),x),GtQ(p,C1),IGtQ(n,C0),IGtQ(m,Subtract(Times(C2,n),C1))))),
IIntegrate(3880,Integrate(Times(Power(x_,m_DEFAULT),Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Power(Sin(Plus(a,Times(b,Power(x,n)))),p),Power(Plus(m,C1),CN1)),x),Negate(Simp(Times(b,n,p,Power(x,Plus(m,n,C1)),Cos(Plus(a,Times(b,Power(x,n)))),Power(Sin(Plus(a,Times(b,Power(x,n)))),Subtract(p,C1)),Power(Times(Plus(m,C1),Plus(m,n,C1)),CN1)),x)),Simp(Star(Times(Sqr(b),Sqr(n),p,Subtract(p,C1),Power(Times(Plus(m,C1),Plus(m,n,C1)),CN1)),Integrate(Times(Power(x,Plus(m,Times(C2,n))),Power(Sin(Plus(a,Times(b,Power(x,n)))),Subtract(p,C2))),x)),x),Negate(Simp(Star(Times(Sqr(b),Sqr(n),Sqr(p),Power(Times(Plus(m,C1),Plus(m,n,C1)),CN1)),Integrate(Times(Power(x,Plus(m,Times(C2,n))),Power(Sin(Plus(a,Times(b,Power(x,n)))),p)),x)),x))),And(FreeQ(list(a,b),x),GtQ(p,C1),IGtQ(n,C0),ILtQ(m,Plus(Times(CN2,n),C1)),NeQ(Plus(m,n,C1),C0))))
  );
}
