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
class IntRules191 { 
  public static IAST RULES = List( 
IIntegrate(3821,Integrate(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Cos(Plus(c,Times(d,x))),Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Power(x,n))),p)),x),x),And(FreeQ(List(a,b,c,d,e,m,n),x),IGtQ(p,C0)))),
IIntegrate(3822,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(e,m),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Sin(Plus(c,Times(d,x))),Power(Times(b,n,Plus(p,C1)),CN1)),x),Simp(Star(Times(d,Power(e,m),Power(Times(b,n,Plus(p,C1)),CN1)),Integrate(Times(Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Cos(Plus(c,Times(d,x)))),x)),x)),And(FreeQ(List(a,b,c,d,e,m,n),x),ILtQ(p,CN1),EqQ(m,Subtract(n,C1)),Or(IntegerQ(n),GtQ(e,C0))))),
IIntegrate(3823,Integrate(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(e,m),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Cos(Plus(c,Times(d,x))),Power(Times(b,n,Plus(p,C1)),CN1)),x),Simp(Star(Times(d,Power(e,m),Power(Times(b,n,Plus(p,C1)),CN1)),Integrate(Times(Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Sin(Plus(c,Times(d,x)))),x)),x)),And(FreeQ(List(a,b,c,d,e,m,n),x),ILtQ(p,CN1),EqQ(m,Subtract(n,C1)),Or(IntegerQ(n),GtQ(e,C0))))),
IIntegrate(3824,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,Negate(n),C1)),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Sin(Plus(c,Times(d,x))),Power(Times(b,n,Plus(p,C1)),CN1)),x),Negate(Simp(Star(Times(Plus(m,Negate(n),C1),Power(Times(b,n,Plus(p,C1)),CN1)),Integrate(Times(Power(x,Subtract(m,n)),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Sin(Plus(c,Times(d,x)))),x)),x)),Negate(Simp(Star(Times(d,Power(Times(b,n,Plus(p,C1)),CN1)),Integrate(Times(Power(x,Plus(m,Negate(n),C1)),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Cos(Plus(c,Times(d,x)))),x)),x))),And(FreeQ(List(a,b,c,d,m),x),ILtQ(p,CN1),IGtQ(n,C0),Or(GtQ(Plus(m,Negate(n),C1),C0),GtQ(n,C2)),RationalQ(m)))),
IIntegrate(3825,Integrate(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,Negate(n),C1)),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Cos(Plus(c,Times(d,x))),Power(Times(b,n,Plus(p,C1)),CN1)),x),Negate(Simp(Star(Times(Plus(m,Negate(n),C1),Power(Times(b,n,Plus(p,C1)),CN1)),Integrate(Times(Power(x,Subtract(m,n)),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Cos(Plus(c,Times(d,x)))),x)),x)),Simp(Star(Times(d,Power(Times(b,n,Plus(p,C1)),CN1)),Integrate(Times(Power(x,Plus(m,Negate(n),C1)),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Sin(Plus(c,Times(d,x)))),x)),x)),And(FreeQ(List(a,b,c,d,m),x),ILtQ(p,CN1),IGtQ(n,C0),Or(GtQ(Plus(m,Negate(n),C1),C0),GtQ(n,C2)),RationalQ(m)))),
IIntegrate(3826,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Sin(Plus(c,Times(d,x))),Times(Power(x,m),Power(Plus(a,Times(b,Power(x,n))),p)),x),x),And(FreeQ(List(a,b,c,d,m),x),ILtQ(p,C0),IGtQ(n,C0),Or(EqQ(n,C2),EqQ(p,CN1)),IntegerQ(m)))),
IIntegrate(3827,Integrate(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Cos(Plus(c,Times(d,x))),Times(Power(x,m),Power(Plus(a,Times(b,Power(x,n))),p)),x),x),And(FreeQ(List(a,b,c,d,m),x),ILtQ(p,C0),IGtQ(n,C0),Or(EqQ(n,C2),EqQ(p,CN1)),IntegerQ(m)))),
IIntegrate(3828,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Integrate(Times(Power(x,Plus(m,Times(n,p))),Power(Plus(b,Times(a,Power(Power(x,n),CN1))),p),Sin(Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d,m),x),ILtQ(p,C0),ILtQ(n,C0)))),
IIntegrate(3829,Integrate(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Integrate(Times(Power(x,Plus(m,Times(n,p))),Power(Plus(b,Times(a,Power(Power(x,n),CN1))),p),Cos(Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d,m),x),ILtQ(p,C0),ILtQ(n,C0)))),
IIntegrate(3830,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Power(x,n))),p),Sin(Plus(c,Times(d,x)))),x),FreeQ(List(a,b,c,d,e,m,n,p),x))),
IIntegrate(3831,Integrate(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Power(x,n))),p),Cos(Plus(c,Times(d,x)))),x),FreeQ(List(a,b,c,d,e,m,n,p),x))),
IIntegrate(3832,Integrate(Sin(Times(d_DEFAULT,Sqr(Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),x_Symbol),
    Condition(Simp(Times(Sqrt(CPiHalf),Power(Times(f,Rt(d,C2)),CN1),FresnelS(Times(Sqrt(Times(C2,Power(Pi,CN1))),Rt(d,C2),Plus(e,Times(f,x))))),x),FreeQ(list(d,e,f),x))),
IIntegrate(3833,Integrate(Cos(Times(d_DEFAULT,Sqr(Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),x_Symbol),
    Condition(Simp(Times(Sqrt(CPiHalf),Power(Times(f,Rt(d,C2)),CN1),FresnelC(Times(Sqrt(Times(C2,Power(Pi,CN1))),Rt(d,C2),Plus(e,Times(f,x))))),x),FreeQ(list(d,e,f),x))),
IIntegrate(3834,Integrate(Sin(Plus(c_,Times(d_DEFAULT,Sqr(Plus(e_DEFAULT,Times(f_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Simp(Star(Sin(c),Integrate(Cos(Times(d,Sqr(Plus(e,Times(f,x))))),x)),x),Simp(Star(Cos(c),Integrate(Sin(Times(d,Sqr(Plus(e,Times(f,x))))),x)),x)),FreeQ(List(c,d,e,f),x))),
IIntegrate(3835,Integrate(Cos(Plus(c_,Times(d_DEFAULT,Sqr(Plus(e_DEFAULT,Times(f_DEFAULT,x_)))))),x_Symbol),
    Condition(Subtract(Simp(Star(Cos(c),Integrate(Cos(Times(d,Sqr(Plus(e,Times(f,x))))),x)),x),Simp(Star(Sin(c),Integrate(Sin(Times(d,Sqr(Plus(e,Times(f,x))))),x)),x)),FreeQ(List(c,d,e,f),x))),
IIntegrate(3836,Integrate(Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))),x_Symbol),
    Condition(Subtract(Simp(Star(Times(C1D2,CI),Integrate(Exp(Subtract(Times(CN1,c,CI),Times(d,CI,Power(Plus(e,Times(f,x)),n)))),x)),x),Simp(Star(Times(C1D2,CI),Integrate(Exp(Plus(Times(c,CI),Times(d,CI,Power(Plus(e,Times(f,x)),n)))),x)),x)),And(FreeQ(List(c,d,e,f),x),IGtQ(n,C2)))),
IIntegrate(3837,Integrate(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))),x_Symbol),
    Condition(Plus(Simp(Star(C1D2,Integrate(Exp(Subtract(Times(CN1,c,CI),Times(d,CI,Power(Plus(e,Times(f,x)),n)))),x)),x),Simp(Star(C1D2,Integrate(Exp(Plus(Times(c,CI),Times(d,CI,Power(Plus(e,Times(f,x)),n)))),x)),x)),And(FreeQ(List(c,d,e,f),x),IGtQ(n,C2)))),
IIntegrate(3838,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))))),p_),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(Plus(e,Times(f,x)),n)))))),p),x),x),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C1),IGtQ(n,C1)))),
IIntegrate(3839,Integrate(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))),b_DEFAULT)),p_),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(Plus(e,Times(f,x)),n)))))),p),x),x),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C1),IGtQ(n,C1)))),
IIntegrate(3840,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))))),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Negate(Power(f,CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(Power(x,n),CN1)))))),p),Power(x,CN2)),x),x,Power(Plus(e,Times(f,x)),CN1))),x),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),ILtQ(n,C0),EqQ(n,CN2))))
  );
}
