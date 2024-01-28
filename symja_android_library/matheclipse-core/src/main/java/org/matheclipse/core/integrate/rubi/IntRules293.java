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
class IntRules293 { 
  public static IAST RULES = List( 
IIntegrate(5861,Integrate(Times(Power(Times(e_DEFAULT,x_),m_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(With(list(Set(k,Denominator(m))),Simp(Star(Times(k,Power(e,CN1)),Subst(Integrate(Times(Power(x,Subtract(Times(k,Plus(m,C1)),C1)),Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(x,Times(k,n)),Power(Power(e,n),CN1)))))),p)),x),x,Power(Times(e,x),Power(k,CN1)))),x)),And(FreeQ(List(a,b,c,d,e),x),IntegerQ(p),IGtQ(n,C0),FractionQ(m)))),
IIntegrate(5862,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_DEFAULT,x_),m_)),x_Symbol),
    Condition(With(list(Set(k,Denominator(m))),Simp(Star(Times(k,Power(e,CN1)),Subst(Integrate(Times(Power(x,Subtract(Times(k,Plus(m,C1)),C1)),Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(x,Times(k,n)),Power(Power(e,n),CN1)))))),p)),x),x,Power(Times(e,x),Power(k,CN1)))),x)),And(FreeQ(List(a,b,c,d,e),x),IntegerQ(p),IGtQ(n,C0),FractionQ(m)))),
IIntegrate(5863,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(Times(e,x),m),Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(x,n)))))),p),x),x),And(FreeQ(List(a,b,c,d,e,m),x),IGtQ(p,C1),IGtQ(n,C0)))),
IIntegrate(5864,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(Times(e,x),m),Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(x,n)))))),p),x),x),And(FreeQ(List(a,b,c,d,e,m),x),IGtQ(p,C1),IGtQ(n,C0)))),
IIntegrate(5865,Integrate(Times(Power(x_,m_DEFAULT),Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,n),Cosh(Plus(a,Times(b,Power(x,n)))),Power(Sinh(Plus(a,Times(b,Power(x,n)))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x),Negate(Simp(Times(n,Power(Sinh(Plus(a,Times(b,Power(x,n)))),Plus(p,C2)),Power(Times(Sqr(b),Sqr(n),Plus(p,C1),Plus(p,C2)),CN1)),x)),Negate(Simp(Star(Times(Plus(p,C2),Power(Plus(p,C1),CN1)),Integrate(Times(Power(x,m),Power(Sinh(Plus(a,Times(b,Power(x,n)))),Plus(p,C2))),x)),x))),And(FreeQ(List(a,b,m,n),x),EqQ(Plus(m,Times(CN1,C2,n),C1),C0),LtQ(p,CN1),NeQ(p,CN2)))),
IIntegrate(5866,Integrate(Times(Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(x,n),Sinh(Plus(a,Times(b,Power(x,n)))),Power(Cosh(Plus(a,Times(b,Power(x,n)))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x),Simp(Times(n,Power(Cosh(Plus(a,Times(b,Power(x,n)))),Plus(p,C2)),Power(Times(Sqr(b),Sqr(n),Plus(p,C1),Plus(p,C2)),CN1)),x),Simp(Star(Times(Plus(p,C2),Power(Plus(p,C1),CN1)),Integrate(Times(Power(x,m),Power(Cosh(Plus(a,Times(b,Power(x,n)))),Plus(p,C2))),x)),x)),And(FreeQ(List(a,b,m,n),x),EqQ(Plus(m,Times(CN1,C2,n),C1),C0),LtQ(p,CN1),NeQ(p,CN2)))),
IIntegrate(5867,Integrate(Times(Power(x_,m_DEFAULT),Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,Negate(n),C1)),Cosh(Plus(a,Times(b,Power(x,n)))),Power(Sinh(Plus(a,Times(b,Power(x,n)))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x),Negate(Simp(Times(Plus(m,Negate(n),C1),Power(x,Plus(m,Times(CN1,C2,n),C1)),Power(Sinh(Plus(a,Times(b,Power(x,n)))),Plus(p,C2)),Power(Times(Sqr(b),Sqr(n),Plus(p,C1),Plus(p,C2)),CN1)),x)),Negate(Simp(Star(Times(Plus(p,C2),Power(Plus(p,C1),CN1)),Integrate(Times(Power(x,m),Power(Sinh(Plus(a,Times(b,Power(x,n)))),Plus(p,C2))),x)),x)),Simp(Star(Times(Plus(m,Negate(n),C1),Plus(m,Times(CN1,C2,n),C1),Power(Times(Sqr(b),Sqr(n),Plus(p,C1),Plus(p,C2)),CN1)),Integrate(Times(Power(x,Subtract(m,Times(C2,n))),Power(Sinh(Plus(a,Times(b,Power(x,n)))),Plus(p,C2))),x)),x)),And(FreeQ(list(a,b),x),IntegersQ(m,n),LtQ(p,CN1),NeQ(p,CN2),LtQ(C0,Times(C2,n),Plus(m,C1))))),
IIntegrate(5868,Integrate(Times(Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(x,Plus(m,Negate(n),C1)),Sinh(Plus(a,Times(b,Power(x,n)))),Power(Cosh(Plus(a,Times(b,Power(x,n)))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x),Simp(Times(Plus(m,Negate(n),C1),Power(x,Plus(m,Times(CN1,C2,n),C1)),Power(Cosh(Plus(a,Times(b,Power(x,n)))),Plus(p,C2)),Power(Times(Sqr(b),Sqr(n),Plus(p,C1),Plus(p,C2)),CN1)),x),Simp(Star(Times(Plus(p,C2),Power(Plus(p,C1),CN1)),Integrate(Times(Power(x,m),Power(Cosh(Plus(a,Times(b,Power(x,n)))),Plus(p,C2))),x)),x),Negate(Simp(Star(Times(Plus(m,Negate(n),C1),Plus(m,Times(CN1,C2,n),C1),Power(Times(Sqr(b),Sqr(n),Plus(p,C1),Plus(p,C2)),CN1)),Integrate(Times(Power(x,Subtract(m,Times(C2,n))),Power(Cosh(Plus(a,Times(b,Power(x,n)))),Plus(p,C2))),x)),x))),And(FreeQ(list(a,b),x),IntegersQ(m,n),LtQ(p,CN1),NeQ(p,CN2),LtQ(C0,Times(C2,n),Plus(m,C1))))),
IIntegrate(5869,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(Power(x,n),CN1)))))),p),Power(Power(x,Plus(m,C2)),CN1)),x),x,Power(x,CN1))),And(FreeQ(List(a,b,c,d),x),IntegerQ(p),ILtQ(n,C0),IntegerQ(m)))),
IIntegrate(5870,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(Power(x,n),CN1)))))),p),Power(Power(x,Plus(m,C2)),CN1)),x),x,Power(x,CN1))),And(FreeQ(List(a,b,c,d),x),IntegerQ(p),ILtQ(n,C0),IntegerQ(m)))),
IIntegrate(5871,Integrate(Times(Power(Times(e_DEFAULT,x_),m_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(With(list(Set(k,Denominator(m))),Simp(Star(Times(CN1,k,Power(e,CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(Times(Power(e,n),Power(x,Times(k,n))),CN1)))))),p),Power(Power(x,Plus(Times(k,Plus(m,C1)),C1)),CN1)),x),x,Power(Power(Times(e,x),Power(k,CN1)),CN1))),x)),And(FreeQ(List(a,b,c,d,e),x),IntegerQ(p),ILtQ(n,C0),FractionQ(m)))),
IIntegrate(5872,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_DEFAULT,x_),m_)),x_Symbol),
    Condition(With(list(Set(k,Denominator(m))),Simp(Star(Times(CN1,k,Power(e,CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(Times(Power(e,n),Power(x,Times(k,n))),CN1)))))),p),Power(Power(x,Plus(Times(k,Plus(m,C1)),C1)),CN1)),x),x,Power(Power(Times(e,x),Power(k,CN1)),CN1))),x)),And(FreeQ(List(a,b,c,d,e),x),IntegerQ(p),ILtQ(n,C0),FractionQ(m)))),
IIntegrate(5873,Integrate(Times(Power(Times(e_DEFAULT,x_),m_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(CN1,Power(Times(e,x),m),Power(Power(x,CN1),m)),Subst(Integrate(Times(Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(Power(x,n),CN1)))))),p),Power(Power(x,Plus(m,C2)),CN1)),x),x,Power(x,CN1))),x),And(FreeQ(List(a,b,c,d,e,m),x),IntegerQ(p),ILtQ(n,C0),Not(RationalQ(m))))),
IIntegrate(5874,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_DEFAULT,x_),m_)),x_Symbol),
    Condition(Simp(Star(Times(CN1,Power(Times(e,x),m),Power(Power(x,CN1),m)),Subst(Integrate(Times(Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(Power(x,n),CN1)))))),p),Power(Power(x,Plus(m,C2)),CN1)),x),x,Power(x,CN1))),x),And(FreeQ(List(a,b,c,d,e,m),x),IntegerQ(p),ILtQ(n,C0),Not(RationalQ(m))))),
IIntegrate(5875,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Module(list(Set(k,Denominator(n))),Simp(Star(k,Subst(Integrate(Times(Power(x,Subtract(Times(k,Plus(m,C1)),C1)),Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(x,Times(k,n))))))),p)),x),x,Power(x,Power(k,CN1)))),x)),And(FreeQ(List(a,b,c,d,m),x),IntegerQ(p),FractionQ(n)))),
IIntegrate(5876,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Module(list(Set(k,Denominator(n))),Simp(Star(k,Subst(Integrate(Times(Power(x,Subtract(Times(k,Plus(m,C1)),C1)),Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(x,Times(k,n))))))),p)),x),x,Power(x,Power(k,CN1)))),x)),And(FreeQ(List(a,b,c,d,m),x),IntegerQ(p),FractionQ(n)))),
IIntegrate(5877,Integrate(Times(Power(Times(e_,x_),m_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(x,n)))))),p)),x)),x),And(FreeQ(List(a,b,c,d,e,m),x),IntegerQ(p),FractionQ(n)))),
IIntegrate(5878,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_,x_),m_)),x_Symbol),
    Condition(Simp(Star(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(x,n)))))),p)),x)),x),And(FreeQ(List(a,b,c,d,e,m),x),IntegerQ(p),FractionQ(n)))),
IIntegrate(5879,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Plus(m,C1),CN1),Subst(Integrate(Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(x,Simplify(Times(n,Power(Plus(m,C1),CN1))))))))),p),x),x,Power(x,Plus(m,C1)))),x),And(FreeQ(List(a,b,c,d,m,n),x),IntegerQ(p),NeQ(m,CN1),IGtQ(Simplify(Times(n,Power(Plus(m,C1),CN1))),C0),Not(IntegerQ(n))))),
IIntegrate(5880,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Plus(m,C1),CN1),Subst(Integrate(Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(x,Simplify(Times(n,Power(Plus(m,C1),CN1))))))))),p),x),x,Power(x,Plus(m,C1)))),x),And(FreeQ(List(a,b,c,d,m,n),x),IntegerQ(p),NeQ(m,CN1),IGtQ(Simplify(Times(n,Power(Plus(m,C1),CN1))),C0),Not(IntegerQ(n)))))
  );
}
