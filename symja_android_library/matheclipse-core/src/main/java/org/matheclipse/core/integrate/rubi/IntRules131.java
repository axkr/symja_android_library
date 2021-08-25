package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class IntRules131 { 
  public static IAST RULES = List( 
IIntegrate(2621,Integrate(Times(Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),a_DEFAULT),m_),Power($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Negate(Dist(Power(Times(f,Power(a,n)),CN1),Subst(Integrate(Times(Power(x,Subtract(Plus(m,n),C1)),Power(Power(Plus(CN1,Times(Sqr(x),Power(a,CN2))),Times(C1D2,Plus(n,C1))),CN1)),x),x,Times(a,Csc(Plus(e,Times(f,x))))),x)),And(FreeQ(List(a,e,f,m),x),IntegerQ(Times(C1D2,Plus(n,C1))),Not(And(IntegerQ(Times(C1D2,Plus(m,C1))),LtQ(C0,m,n)))))),
IIntegrate(2622,Integrate(Times(Power($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),n_DEFAULT),Power(Times(a_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_)),x_Symbol),
    Condition(Dist(Power(Times(f,Power(a,n)),CN1),Subst(Integrate(Times(Power(x,Subtract(Plus(m,n),C1)),Power(Power(Plus(CN1,Times(Sqr(x),Power(a,CN2))),Times(C1D2,Plus(n,C1))),CN1)),x),x,Times(a,Sec(Plus(e,Times(f,x))))),x),And(FreeQ(List(a,e,f,m),x),IntegerQ(Times(C1D2,Plus(n,C1))),Not(And(IntegerQ(Times(C1D2,Plus(m,C1))),LtQ(C0,m,n)))))),
IIntegrate(2623,Integrate(Times(Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),a_DEFAULT),m_),Power(Times(b_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(a,Power(Times(a,Csc(Plus(e,Times(f,x)))),Subtract(m,C1)),Power(Times(b,Sec(Plus(e,Times(f,x)))),Plus(n,C1)),Power(Times(f,b,Subtract(m,C1)),CN1)),x)),Dist(Times(Sqr(a),Plus(n,C1),Power(Times(Sqr(b),Subtract(m,C1)),CN1)),Integrate(Times(Power(Times(a,Csc(Plus(e,Times(f,x)))),Subtract(m,C2)),Power(Times(b,Sec(Plus(e,Times(f,x)))),Plus(n,C2))),x),x)),And(FreeQ(List(a,b,e,f),x),GtQ(m,C1),LtQ(n,CN1),IntegersQ(Times(C2,m),Times(C2,n))))),
IIntegrate(2624,Integrate(Times(Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),a_DEFAULT),m_),Power(Times(b_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(Plus(Simp(Times(b,Power(Times(a,Csc(Plus(e,Times(f,x)))),Plus(m,C1)),Power(Times(b,Sec(Plus(e,Times(f,x)))),Subtract(n,C1)),Power(Times(f,a,Subtract(n,C1)),CN1)),x),Dist(Times(Sqr(b),Plus(m,C1),Power(Times(Sqr(a),Subtract(n,C1)),CN1)),Integrate(Times(Power(Times(a,Csc(Plus(e,Times(f,x)))),Plus(m,C2)),Power(Times(b,Sec(Plus(e,Times(f,x)))),Subtract(n,C2))),x),x)),And(FreeQ(List(a,b,e,f),x),GtQ(n,C1),LtQ(m,CN1),IntegersQ(Times(C2,m),Times(C2,n))))),
IIntegrate(2625,Integrate(Times(Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),a_DEFAULT),m_),Power(Times(b_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(a,b,Power(Times(a,Csc(Plus(e,Times(f,x)))),Subtract(m,C1)),Power(Times(b,Sec(Plus(e,Times(f,x)))),Subtract(n,C1)),Power(Times(f,Subtract(m,C1)),CN1)),x)),Dist(Times(Sqr(a),Subtract(Plus(m,n),C2),Power(Subtract(m,C1),CN1)),Integrate(Times(Power(Times(a,Csc(Plus(e,Times(f,x)))),Subtract(m,C2)),Power(Times(b,Sec(Plus(e,Times(f,x)))),n)),x),x)),And(FreeQ(List(a,b,e,f,n),x),GtQ(m,C1),IntegersQ(Times(C2,m),Times(C2,n)),Not(GtQ(n,m))))),
IIntegrate(2626,Integrate(Times(Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),a_DEFAULT),m_DEFAULT),Power(Times(b_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(Plus(Simp(Times(a,b,Power(Times(a,Csc(Plus(e,Times(f,x)))),Subtract(m,C1)),Power(Times(b,Sec(Plus(e,Times(f,x)))),Subtract(n,C1)),Power(Times(f,Subtract(n,C1)),CN1)),x),Dist(Times(Sqr(b),Subtract(Plus(m,n),C2),Power(Subtract(n,C1),CN1)),Integrate(Times(Power(Times(a,Csc(Plus(e,Times(f,x)))),m),Power(Times(b,Sec(Plus(e,Times(f,x)))),Subtract(n,C2))),x),x)),And(FreeQ(List(a,b,e,f,m),x),GtQ(n,C1),IntegersQ(Times(C2,m),Times(C2,n))))),
IIntegrate(2627,Integrate(Times(Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),a_DEFAULT),m_),Power(Times(b_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(b,Power(Times(a,Csc(Plus(e,Times(f,x)))),Plus(m,C1)),Power(Times(b,Sec(Plus(e,Times(f,x)))),Subtract(n,C1)),Power(Times(a,f,Plus(m,n)),CN1)),x),Dist(Times(Plus(m,C1),Power(Times(Sqr(a),Plus(m,n)),CN1)),Integrate(Times(Power(Times(a,Csc(Plus(e,Times(f,x)))),Plus(m,C2)),Power(Times(b,Sec(Plus(e,Times(f,x)))),n)),x),x)),And(FreeQ(List(a,b,e,f,n),x),LtQ(m,CN1),NeQ(Plus(m,n),C0),IntegersQ(Times(C2,m),Times(C2,n))))),
IIntegrate(2628,Integrate(Times(Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),a_DEFAULT),m_DEFAULT),Power(Times(b_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(a,Power(Times(a,Csc(Plus(e,Times(f,x)))),Subtract(m,C1)),Power(Times(b,Sec(Plus(e,Times(f,x)))),Plus(n,C1)),Power(Times(b,f,Plus(m,n)),CN1)),x)),Dist(Times(Plus(n,C1),Power(Times(Sqr(b),Plus(m,n)),CN1)),Integrate(Times(Power(Times(a,Csc(Plus(e,Times(f,x)))),m),Power(Times(b,Sec(Plus(e,Times(f,x)))),Plus(n,C2))),x),x)),And(FreeQ(List(a,b,e,f,m),x),LtQ(n,CN1),NeQ(Plus(m,n),C0),IntegersQ(Times(C2,m),Times(C2,n))))),
IIntegrate(2629,Integrate(Times(Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),a_DEFAULT),m_),Power(Times(b_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(Dist(Times(Power(Times(a,Csc(Plus(e,Times(f,x)))),m),Power(Times(b,Sec(Plus(e,Times(f,x)))),n),Power(Power(Tan(Plus(e,Times(f,x))),n),CN1)),Integrate(Power(Tan(Plus(e,Times(f,x))),n),x),x),And(FreeQ(List(a,b,e,f,m,n),x),Not(IntegerQ(n)),EqQ(Plus(m,n),C0)))),
IIntegrate(2630,Integrate(Times(Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),a_DEFAULT),m_),Power(Times(b_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(Dist(Times(Power(Times(a,Csc(Plus(e,Times(f,x)))),m),Power(Times(b,Sec(Plus(e,Times(f,x)))),n),Power(Times(a,Sin(Plus(e,Times(f,x)))),m),Power(Times(b,Cos(Plus(e,Times(f,x)))),n)),Integrate(Power(Times(Power(Times(a,Sin(Plus(e,Times(f,x)))),m),Power(Times(b,Cos(Plus(e,Times(f,x)))),n)),CN1),x),x),And(FreeQ(List(a,b,e,f,m,n),x),IntegerQ(Subtract(m,C1D2)),IntegerQ(Subtract(n,C1D2))))),
IIntegrate(2631,Integrate(Times(Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),a_DEFAULT),m_),Power(Times(b_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(Dist(Times(Sqr(a),Power(Times(a,Csc(Plus(e,Times(f,x)))),Subtract(m,C1)),Power(Times(b,Sec(Plus(e,Times(f,x)))),Plus(n,C1)),Power(Times(a,Sin(Plus(e,Times(f,x)))),Subtract(m,C1)),Power(Times(b,Cos(Plus(e,Times(f,x)))),Plus(n,C1)),Power(b,CN2)),Integrate(Power(Times(Power(Times(a,Sin(Plus(e,Times(f,x)))),m),Power(Times(b,Cos(Plus(e,Times(f,x)))),n)),CN1),x),x),And(FreeQ(List(a,b,e,f,m,n),x),Not(SimplerQ(Negate(m),Negate(n)))))),
IIntegrate(2632,Integrate(Times(Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT),n_),Power(Times(a_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_)),x_Symbol),
    Condition(Dist(Times(Sqr(a),Power(Times(a,Sec(Plus(e,Times(f,x)))),Subtract(m,C1)),Power(Times(b,Csc(Plus(e,Times(f,x)))),Plus(n,C1)),Power(Times(a,Cos(Plus(e,Times(f,x)))),Subtract(m,C1)),Power(Times(b,Sin(Plus(e,Times(f,x)))),Plus(n,C1)),Power(b,CN2)),Integrate(Power(Times(Power(Times(a,Cos(Plus(e,Times(f,x)))),m),Power(Times(b,Sin(Plus(e,Times(f,x)))),n)),CN1),x),x),FreeQ(List(a,b,e,f,m,n),x))),
IIntegrate(2633,Integrate(Power($($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_),x_Symbol),
    Condition(Negate(Dist(Power(d,CN1),Subst(Integrate(Expand(Power(Subtract(C1,Sqr(x)),Times(C1D2,Subtract(n,C1))),x),x),x,Cos(Plus(c,Times(d,x)))),x)),And(FreeQ(List(c,d),x),IGtQ(Times(C1D2,Subtract(n,C1)),C0)))),
IIntegrate(2634,Integrate(Sqr($($s("§sin"),Plus(c_DEFAULT,Times(C1D2,d_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(C1D2,x),x),Simp(Times(Sin(Plus(Times(C2,c),Times(d,x))),Power(Times(C2,d),CN1)),x)),FreeQ(List(c,d),x))),
IIntegrate(2635,Integrate(Power(Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Plus(Negate(Simp(Times(b,Cos(Plus(c,Times(d,x))),Power(Times(b,Sin(Plus(c,Times(d,x)))),Subtract(n,C1)),Power(Times(d,n),CN1)),x)),Dist(Times(Sqr(b),Subtract(n,C1),Power(n,CN1)),Integrate(Power(Times(b,Sin(Plus(c,Times(d,x)))),Subtract(n,C2)),x),x)),And(FreeQ(List(b,c,d),x),GtQ(n,C1),IntegerQ(Times(C2,n))))),
IIntegrate(2636,Integrate(Power(Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Plus(Simp(Times(Cos(Plus(c,Times(d,x))),Power(Times(b,Sin(Plus(c,Times(d,x)))),Plus(n,C1)),Power(Times(b,d,Plus(n,C1)),CN1)),x),Dist(Times(Plus(n,C2),Power(Times(Sqr(b),Plus(n,C1)),CN1)),Integrate(Power(Times(b,Sin(Plus(c,Times(d,x)))),Plus(n,C2)),x),x)),And(FreeQ(List(b,c,d),x),LtQ(n,CN1),IntegerQ(Times(C2,n))))),
IIntegrate(2637,Integrate($($s("§sin"),Plus(CPiHalf,c_DEFAULT,Times(d_DEFAULT,x_))),x_Symbol),
    Condition(Simp(Times(Sin(Plus(c,Times(d,x))),Power(d,CN1)),x),FreeQ(List(c,d),x))),
IIntegrate(2638,Integrate($($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),x_Symbol),
    Condition(Negate(Simp(Times(Cos(Plus(c,Times(d,x))),Power(d,CN1)),x)),FreeQ(List(c,d),x))),
IIntegrate(2639,Integrate(Sqrt($($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Simp(Times(C2,EllipticE(Times(C1D2,C1,Plus(c,Times(CN1,C1D2,Pi),Times(d,x))),C2),Power(d,CN1)),x),FreeQ(List(c,d),x))),
IIntegrate(2640,Integrate(Sqrt(Times(b_,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),x_Symbol),
    Condition(Dist(Times(Sqrt(Times(b,Sin(Plus(c,Times(d,x))))),Power(Sin(Plus(c,Times(d,x))),CN1D2)),Integrate(Sqrt(Sin(Plus(c,Times(d,x)))),x),x),FreeQ(List(b,c,d),x)))
  );
}
