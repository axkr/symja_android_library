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
class IntRules290 { 
  public static IAST RULES = List( 
IIntegrate(5801,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(Negate(n),C1)),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Sinh(Plus(c,Times(d,x))),Power(Times(b,n,Plus(p,C1)),CN1)),x),Negate(Simp(Star(Times(Plus(Negate(n),C1),Power(Times(b,n,Plus(p,C1)),CN1)),Integrate(Times(Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Sinh(Plus(c,Times(d,x))),Power(Power(x,n),CN1)),x)),x)),Negate(Simp(Star(Times(d,Power(Times(b,n,Plus(p,C1)),CN1)),Integrate(Times(Power(x,Plus(Negate(n),C1)),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Cosh(Plus(c,Times(d,x)))),x)),x))),And(FreeQ(List(a,b,c,d),x),IntegerQ(p),IGtQ(n,C0),LtQ(p,CN1),GtQ(n,C2)))),
IIntegrate(5802,Integrate(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(Negate(n),C1)),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Cosh(Plus(c,Times(d,x))),Power(Times(b,n,Plus(p,C1)),CN1)),x),Negate(Simp(Star(Times(Plus(Negate(n),C1),Power(Times(b,n,Plus(p,C1)),CN1)),Integrate(Times(Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Cosh(Plus(c,Times(d,x))),Power(Power(x,n),CN1)),x)),x)),Negate(Simp(Star(Times(d,Power(Times(b,n,Plus(p,C1)),CN1)),Integrate(Times(Power(x,Plus(Negate(n),C1)),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Sinh(Plus(c,Times(d,x)))),x)),x))),And(FreeQ(List(a,b,c,d),x),IntegerQ(p),IGtQ(n,C0),LtQ(p,CN1),GtQ(n,C2)))),
IIntegrate(5803,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Sinh(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Power(x,n))),p),x),x),And(FreeQ(List(a,b,c,d),x),ILtQ(p,C0),IGtQ(n,C0),Or(EqQ(n,C2),EqQ(p,CN1))))),
IIntegrate(5804,Integrate(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Cosh(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Power(x,n))),p),x),x),And(FreeQ(List(a,b,c,d),x),ILtQ(p,C0),IGtQ(n,C0),Or(EqQ(n,C2),EqQ(p,CN1))))),
IIntegrate(5805,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Integrate(Times(Power(x,Times(n,p)),Power(Plus(b,Times(a,Power(Power(x,n),CN1))),p),Sinh(Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d),x),ILtQ(p,C0),ILtQ(n,C0)))),
IIntegrate(5806,Integrate(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Integrate(Times(Power(x,Times(n,p)),Power(Plus(b,Times(a,Power(Power(x,n),CN1))),p),Cosh(Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d),x),ILtQ(p,C0),ILtQ(n,C0)))),
IIntegrate(5807,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(a,Times(b,Power(x,n))),p),Sinh(Plus(c,Times(d,x)))),x),FreeQ(List(a,b,c,d,n,p),x))),
IIntegrate(5808,Integrate(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(a,Times(b,Power(x,n))),p),Cosh(Plus(c,Times(d,x)))),x),FreeQ(List(a,b,c,d,n,p),x))),
IIntegrate(5809,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Sinh(Plus(c,Times(d,x))),Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Power(x,n))),p)),x),x),And(FreeQ(List(a,b,c,d,e,m,n),x),IGtQ(p,C0)))),
IIntegrate(5810,Integrate(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Cosh(Plus(c,Times(d,x))),Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Power(x,n))),p)),x),x),And(FreeQ(List(a,b,c,d,e,m,n),x),IGtQ(p,C0)))),
IIntegrate(5811,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(e,m),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Sinh(Plus(c,Times(d,x))),Power(Times(b,n,Plus(p,C1)),CN1)),x),Simp(Star(Times(d,Power(e,m),Power(Times(b,n,Plus(p,C1)),CN1)),Integrate(Times(Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Cosh(Plus(c,Times(d,x)))),x)),x)),And(FreeQ(List(a,b,c,d,e,m,n),x),IntegerQ(p),EqQ(Plus(m,Negate(n),C1),C0),LtQ(p,CN1),Or(IntegerQ(n),GtQ(e,C0))))),
IIntegrate(5812,Integrate(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(e,m),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Cosh(Plus(c,Times(d,x))),Power(Times(b,n,Plus(p,C1)),CN1)),x),Simp(Star(Times(d,Power(e,m),Power(Times(b,n,Plus(p,C1)),CN1)),Integrate(Times(Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Sinh(Plus(c,Times(d,x)))),x)),x)),And(FreeQ(List(a,b,c,d,e,m,n),x),IntegerQ(p),EqQ(Plus(m,Negate(n),C1),C0),LtQ(p,CN1),Or(IntegerQ(n),GtQ(e,C0))))),
IIntegrate(5813,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,Negate(n),C1)),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Sinh(Plus(c,Times(d,x))),Power(Times(b,n,Plus(p,C1)),CN1)),x),Negate(Simp(Star(Times(Plus(m,Negate(n),C1),Power(Times(b,n,Plus(p,C1)),CN1)),Integrate(Times(Power(x,Subtract(m,n)),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Sinh(Plus(c,Times(d,x)))),x)),x)),Negate(Simp(Star(Times(d,Power(Times(b,n,Plus(p,C1)),CN1)),Integrate(Times(Power(x,Plus(m,Negate(n),C1)),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Cosh(Plus(c,Times(d,x)))),x)),x))),And(FreeQ(List(a,b,c,d),x),ILtQ(p,CN1),IGtQ(n,C0),RationalQ(m),Or(GtQ(Plus(m,Negate(n),C1),C0),GtQ(n,C2))))),
IIntegrate(5814,Integrate(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,Negate(n),C1)),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Cosh(Plus(c,Times(d,x))),Power(Times(b,n,Plus(p,C1)),CN1)),x),Negate(Simp(Star(Times(Plus(m,Negate(n),C1),Power(Times(b,n,Plus(p,C1)),CN1)),Integrate(Times(Power(x,Subtract(m,n)),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Cosh(Plus(c,Times(d,x)))),x)),x)),Negate(Simp(Star(Times(d,Power(Times(b,n,Plus(p,C1)),CN1)),Integrate(Times(Power(x,Plus(m,Negate(n),C1)),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Sinh(Plus(c,Times(d,x)))),x)),x))),And(FreeQ(List(a,b,c,d),x),ILtQ(p,CN1),IGtQ(n,C0),RationalQ(m),Or(GtQ(Plus(m,Negate(n),C1),C0),GtQ(n,C2))))),
IIntegrate(5815,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Sinh(Plus(c,Times(d,x))),Times(Power(x,m),Power(Plus(a,Times(b,Power(x,n))),p)),x),x),And(FreeQ(List(a,b,c,d),x),ILtQ(p,C0),IntegerQ(m),IGtQ(n,C0),Or(EqQ(n,C2),EqQ(p,CN1))))),
IIntegrate(5816,Integrate(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Cosh(Plus(c,Times(d,x))),Times(Power(x,m),Power(Plus(a,Times(b,Power(x,n))),p)),x),x),And(FreeQ(List(a,b,c,d),x),ILtQ(p,C0),IntegerQ(m),IGtQ(n,C0),Or(EqQ(n,C2),EqQ(p,CN1))))),
IIntegrate(5817,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Integrate(Times(Power(x,Plus(m,Times(n,p))),Power(Plus(b,Times(a,Power(Power(x,n),CN1))),p),Sinh(Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d,m),x),ILtQ(p,C0),ILtQ(n,C0)))),
IIntegrate(5818,Integrate(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Integrate(Times(Power(x,Plus(m,Times(n,p))),Power(Plus(b,Times(a,Power(Power(x,n),CN1))),p),Cosh(Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d,m),x),ILtQ(p,C0),ILtQ(n,C0)))),
IIntegrate(5819,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Power(x,n))),p),Sinh(Plus(c,Times(d,x)))),x),FreeQ(List(a,b,c,d,e,m,n,p),x))),
IIntegrate(5820,Integrate(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Power(x,n))),p),Cosh(Plus(c,Times(d,x)))),x),FreeQ(List(a,b,c,d,e,m,n,p),x)))
  );
}
