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
class IntRules31 { 
  public static IAST RULES = List( 
IIntegrate(621,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,x_)),CN1),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Subtract(Simp(Star(c,Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Sqr(x))),p),Power(Subtract(Sqr(c),Times(Sqr(d),Sqr(x))),CN1)),x)),x),Simp(Star(d,Integrate(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,Sqr(x))),p),Power(Subtract(Sqr(c),Times(Sqr(d),Sqr(x))),CN1)),x)),x)),FreeQ(List(a,b,c,d,m,p),x))),
IIntegrate(622,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,x_)),n_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times(Power(x,m),Power(Plus(a,Times(b,Sqr(x))),p)),Power(Subtract(Times(c,Power(Subtract(Sqr(c),Times(Sqr(d),Sqr(x))),CN1)),Times(d,x,Power(Subtract(Sqr(c),Times(Sqr(d),Sqr(x))),CN1))),Negate(n)),x),x),And(FreeQ(List(a,b,c,d,m,p),x),ILtQ(n,CN1)))),
IIntegrate(623,Integrate(Times(Power(Times(e_,x_),m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,x_)),n_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(e,x),m),Power(Power(x,m),CN1)),Integrate(Times(Power(x,m),Power(Plus(c,Times(d,x)),n),Power(Plus(a,Times(b,Sqr(x))),p)),x)),x),And(FreeQ(List(a,b,c,d,e,m,p),x),ILtQ(n,C0)))),
IIntegrate(624,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,x_)),n_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Subtract(Simp(Star(Power(d,CN1),Integrate(Times(Power(x,Subtract(m,C1)),Power(Plus(c,Times(d,x)),Plus(n,C1)),Power(Plus(a,Times(b,Sqr(x))),p)),x)),x),Simp(Star(Times(c,Power(d,CN1)),Integrate(Times(Power(x,Subtract(m,C1)),Power(Plus(c,Times(d,x)),n),Power(Plus(a,Times(b,Sqr(x))),p)),x)),x)),And(FreeQ(List(a,b,c,d,n,p),x),IGtQ(m,C0)))),
IIntegrate(625,Integrate(Times(Power(x_,CN1),Power(Plus(c_,Times(d_DEFAULT,x_)),CN1D2),Sqrt(Plus(a_,Times(b_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Simp(Star(CN2,Subst(Integrate(Times(Sqrt(Plus(Times(Plus(Times(b,Sqr(c)),Times(a,Sqr(d))),Power(d,CN2)),Times(CN1,C2,b,c,Sqr(x),Power(d,CN2)),Times(b,Power(x,C4),Power(d,CN2)))),Power(Subtract(c,Sqr(x)),CN1)),x),x,Sqrt(Plus(c,Times(d,x))))),x),And(FreeQ(List(a,b,c,d),x),PosQ(Times(b,Power(a,CN1)))))),
IIntegrate(626,Integrate(Times(Power(x_,CN1),Power(Plus(c_,Times(d_DEFAULT,x_)),n_),Sqrt(Plus(a_,Times(b_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Simp(Star(Times(a,Power(c,Plus(n,C1D2))),Integrate(Power(Times(x,Sqrt(Plus(c,Times(d,x))),Sqrt(Plus(a,Times(b,Sqr(x))))),CN1),x)),x),Integrate(Times(Power(Times(Sqrt(Plus(c,Times(d,x))),Sqrt(Plus(a,Times(b,Sqr(x))))),CN1),ExpandToSum(Times(Plus(Times(CN1,a,Power(c,Plus(n,C1D2))),Times(a,Power(Plus(c,Times(d,x)),Plus(n,C1D2))),Times(b,Sqr(x),Power(Plus(c,Times(d,x)),Plus(n,C1D2)))),Power(x,CN1)),x)),x)),And(FreeQ(List(a,b,c,d),x),IGtQ(Plus(n,QQ(3L,2L)),C0)))),
IIntegrate(627,Integrate(Times(Power(x_,CN1),Power(Plus(c_,Times(d_DEFAULT,x_)),n_),Sqrt(Plus(a_,Times(b_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Simp(Star(Times(a,Power(c,Plus(n,C1D2))),Integrate(Power(Times(x,Sqrt(Plus(c,Times(d,x))),Sqrt(Plus(a,Times(b,Sqr(x))))),CN1),x)),x),Integrate(Times(Power(Plus(c,Times(d,x)),n),Power(Plus(a,Times(b,Sqr(x))),CN1D2),ExpandToSum(Times(Subtract(Plus(a,Times(b,Sqr(x))),Times(a,Power(c,Plus(n,C1D2)),Power(Plus(c,Times(d,x)),Subtract(Negate(n),C1D2)))),Power(x,CN1)),x)),x)),And(FreeQ(List(a,b,c,d),x),ILtQ(Plus(n,C1D2),C0)))),
IIntegrate(628,Integrate(Times(Power(Times(e_DEFAULT,x_),m_),Power(Plus(c_,Times(d_DEFAULT,x_)),n_),Sqrt(Plus(a_,Times(b_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(c,Subtract(n,C1D2)),Power(Times(e,x),Plus(m,C1)),Sqrt(Plus(c,Times(d,x))),Sqrt(Plus(a,Times(b,Sqr(x)))),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Star(Power(Times(C2,e,Plus(m,C1)),CN1),Integrate(Times(Power(Times(e,x),Plus(m,C1)),Power(Times(Sqrt(Plus(c,Times(d,x))),Sqrt(Plus(a,Times(b,Sqr(x))))),CN1),ExpandToSum(Times(Subtract(Subtract(Plus(Times(C2,a,Power(c,Plus(n,C1D2)),Plus(m,C1)),Times(a,Power(c,Subtract(n,C1D2)),d,Plus(Times(C2,m),C3),x),Times(C2,b,Power(c,Plus(n,C1D2)),Plus(m,C2),Sqr(x)),Times(b,Power(c,Subtract(n,C1D2)),d,Plus(Times(C2,m),C5),Power(x,C3))),Times(C2,a,Plus(m,C1),Power(Plus(c,Times(d,x)),Plus(n,C1D2)))),Times(C2,b,Plus(m,C1),Sqr(x),Power(Plus(c,Times(d,x)),Plus(n,C1D2)))),Power(x,CN1)),x)),x)),x)),And(FreeQ(List(a,b,c,d,e),x),IGtQ(Plus(n,QQ(3L,2L)),C0),LtQ(m,CN1),IntegerQ(Times(C2,m))))),
IIntegrate(629,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Sqrt(Plus(c_,Times(d_DEFAULT,x_))),Sqrt(Plus(a_,Times(b_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Simp(Times(C2,Power(Times(e,x),Plus(m,C1)),Sqrt(Plus(c,Times(d,x))),Sqrt(Plus(a,Times(b,Sqr(x)))),Power(Times(e,Plus(Times(C2,m),C5)),CN1)),x),Simp(Star(Power(Plus(Times(C2,m),C5),CN1),Integrate(Times(Power(Times(e,x),m),Plus(Times(C3,a,c),Times(C2,a,d,x),Times(b,c,Sqr(x))),Power(Times(Sqrt(Plus(c,Times(d,x))),Sqrt(Plus(a,Times(b,Sqr(x))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,m),x),Not(LtQ(m,CN1)),IntegerQ(Times(C2,m))))),
IIntegrate(630,Integrate(Times(Power(x_,CN1),Sqrt(Plus(c_,Times(d_DEFAULT,x_))),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Star(CN2,Subst(Integrate(Times(Sqr(x),Power(Times(Subtract(c,Sqr(x)),Sqrt(Plus(Times(Plus(Times(b,Sqr(c)),Times(a,Sqr(d))),Power(d,CN2)),Times(CN1,C2,b,c,Sqr(x),Power(d,CN2)),Times(b,Power(x,C4),Power(d,CN2))))),CN1)),x),x,Sqrt(Plus(c,Times(d,x))))),x),And(FreeQ(List(a,b,c,d),x),PosQ(Times(b,Power(a,CN1)))))),
IIntegrate(631,Integrate(Times(Power(x_,CN1),Power(Plus(c_,Times(d_DEFAULT,x_)),CN1D2),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Star(CN2,Subst(Integrate(Power(Times(Subtract(c,Sqr(x)),Sqrt(Plus(Times(Plus(Times(b,Sqr(c)),Times(a,Sqr(d))),Power(d,CN2)),Times(CN1,C2,b,c,Sqr(x),Power(d,CN2)),Times(b,Power(x,C4),Power(d,CN2))))),CN1),x),x,Sqrt(Plus(c,Times(d,x))))),x),And(FreeQ(List(a,b,c,d),x),PosQ(Times(b,Power(a,CN1)))))),
IIntegrate(632,Integrate(Times(Power(x_,CN1),Power(Plus(c_,Times(d_DEFAULT,x_)),CN1D2),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(With(list(Set(q,Rt(Times(CN1,b,Power(a,CN1)),C2))),Simp(Star(Power(a,CN1D2),Integrate(Power(Times(x,Sqrt(Plus(c,Times(d,x))),Sqrt(Subtract(C1,Times(q,x))),Sqrt(Plus(C1,Times(q,x)))),CN1),x)),x)),And(FreeQ(List(a,b,c,d),x),NegQ(Times(b,Power(a,CN1))),GtQ(a,C0)))),
IIntegrate(633,Integrate(Times(Power(x_,CN1),Power(Plus(c_,Times(d_DEFAULT,x_)),CN1D2),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Star(Times(Sqrt(Plus(C1,Times(b,Sqr(x),Power(a,CN1)))),Power(Plus(a,Times(b,Sqr(x))),CN1D2)),Integrate(Power(Times(x,Sqrt(Plus(c,Times(d,x))),Sqrt(Plus(C1,Times(b,Sqr(x),Power(a,CN1))))),CN1),x)),x),And(FreeQ(List(a,b,c,d),x),NegQ(Times(b,Power(a,CN1))),Not(GtQ(a,C0))))),
IIntegrate(634,Integrate(Times(Power(x_,CN1),Power(Plus(c_,Times(d_DEFAULT,x_)),n_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Subtract(Simp(Star(Power(c,Plus(n,C1D2)),Integrate(Power(Times(x,Sqrt(Plus(c,Times(d,x))),Sqrt(Plus(a,Times(b,Sqr(x))))),CN1),x)),x),Integrate(Times(Power(Times(Sqrt(Plus(c,Times(d,x))),Sqrt(Plus(a,Times(b,Sqr(x))))),CN1),ExpandToSum(Times(Subtract(Power(c,Plus(n,C1D2)),Power(Plus(c,Times(d,x)),Plus(n,C1D2))),Power(x,CN1)),x)),x)),And(FreeQ(List(a,b,c,d),x),IGtQ(Subtract(n,C1D2),C0)))),
IIntegrate(635,Integrate(Times(Power(x_,CN1),Power(Plus(c_,Times(d_DEFAULT,x_)),n_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Plus(Simp(Star(Power(c,Plus(n,C1D2)),Integrate(Power(Times(x,Sqrt(Plus(c,Times(d,x))),Sqrt(Plus(a,Times(b,Sqr(x))))),CN1),x)),x),Integrate(Times(Power(Plus(c,Times(d,x)),n),Power(Plus(a,Times(b,Sqr(x))),CN1D2),ExpandToSum(Times(Subtract(C1,Times(Power(c,Plus(n,C1D2)),Power(Plus(c,Times(d,x)),Subtract(Negate(n),C1D2)))),Power(x,CN1)),x)),x)),And(FreeQ(List(a,b,c,d),x),ILtQ(Plus(n,C1D2),C0)))),
IIntegrate(636,Integrate(Times(Power(Times(e_DEFAULT,x_),m_),Power(Plus(c_,Times(d_DEFAULT,x_)),n_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(c,Subtract(n,C1D2)),Power(Times(e,x),Plus(m,C1)),Sqrt(Plus(c,Times(d,x))),Sqrt(Plus(a,Times(b,Sqr(x)))),Power(Times(a,e,Plus(m,C1)),CN1)),x),Simp(Star(Power(Times(C2,a,e,Plus(m,C1)),CN1),Integrate(Times(Power(Times(e,x),Plus(m,C1)),Power(Times(Sqrt(Plus(c,Times(d,x))),Sqrt(Plus(a,Times(b,Sqr(x))))),CN1),ExpandToSum(Times(Subtract(Plus(Times(C2,a,Power(c,Plus(n,C1D2)),Plus(m,C1)),Times(a,Power(c,Subtract(n,C1D2)),d,Plus(Times(C2,m),C3),x),Times(C2,b,Power(c,Plus(n,C1D2)),Plus(m,C2),Sqr(x)),Times(b,Power(c,Subtract(n,C1D2)),d,Plus(Times(C2,m),C5),Power(x,C3))),Times(C2,a,Plus(m,C1),Power(Plus(c,Times(d,x)),Plus(n,C1D2)))),Power(x,CN1)),x)),x)),x)),And(FreeQ(List(a,b,c,d,e),x),IGtQ(Plus(n,QQ(3L,2L)),C0),LtQ(m,CN1),IntegerQ(Times(C2,m))))),
IIntegrate(637,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,x_)),n_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times(Power(Plus(a,Times(b,Sqr(x))),p),Power(Plus(c,Times(d,x)),CN1D2)),Times(Power(x,m),Power(Plus(c,Times(d,x)),Plus(n,C1D2))),x),x),And(FreeQ(List(a,b,c,d,m),x),IntegerQ(Plus(p,C1D2)),IntegerQ(Plus(n,C1D2)),IntegerQ(m)))),
IIntegrate(638,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,x_)),n_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(e,x),m),Power(Plus(c,Times(d,x)),n),Power(Plus(a,Times(b,Sqr(x))),p)),x),FreeQ(List(a,b,c,d,e,m,n,p),x))),
IIntegrate(639,Integrate(Times(Power(Plus(c_,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Plus(e_,Times(f_DEFAULT,x_)),n_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,p)),Power(Plus(e,Times(f,x)),n),Power(Plus(Times(a,Power(c,CN1)),Times(b,Power(d,CN1),x)),p)),x),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),EqQ(Plus(Times(b,Sqr(c)),Times(a,Sqr(d))),C0),Or(IntegerQ(p),And(GtQ(a,C0),GtQ(c,C0),Not(IntegerQ(m))))))),
IIntegrate(640,Integrate(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),n_DEFAULT),Power(Plus(a_,Times(c_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Times(g,Power(e,CN1)),n),Integrate(Times(Power(Plus(d,Times(e,x)),Plus(m,n)),Power(Plus(a,Times(c,Sqr(x))),p)),x)),x),And(FreeQ(List(a,c,d,e,f,g,m,p),x),EqQ(Subtract(Times(e,f),Times(d,g)),C0),IntegerQ(n),Not(And(IntegerQ(m),SimplerQ(Plus(f,Times(g,x)),Plus(d,Times(e,x))))))))
  );
}
