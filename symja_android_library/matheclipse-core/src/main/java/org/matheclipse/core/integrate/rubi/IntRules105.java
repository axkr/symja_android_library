package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class IntRules105 { 
  public static IAST RULES = List( 
IIntegrate(2626,Int(Times(Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),a_DEFAULT),m_DEFAULT),Power(Times(b_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(Plus(Simp(Times(a,b,Power(Times(a,Csc(Plus(e,Times(f,x)))),Subtract(m,C1)),Power(Times(b,Sec(Plus(e,Times(f,x)))),Subtract(n,C1)),Power(Times(f,Subtract(n,C1)),CN1)),x),Dist(Times(Sqr(b),Subtract(Plus(m,n),C2),Power(Subtract(n,C1),CN1)),Int(Times(Power(Times(a,Csc(Plus(e,Times(f,x)))),m),Power(Times(b,Sec(Plus(e,Times(f,x)))),Subtract(n,C2))),x),x)),And(FreeQ(List(a,b,e,f,m),x),GtQ(n,C1),IntegersQ(Times(C2,m),Times(C2,n))))),
IIntegrate(2627,Int(Times(Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),a_DEFAULT),m_),Power(Times(b_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(b,Power(Times(a,Csc(Plus(e,Times(f,x)))),Plus(m,C1)),Power(Times(b,Sec(Plus(e,Times(f,x)))),Subtract(n,C1)),Power(Times(a,f,Plus(m,n)),CN1)),x),Dist(Times(Plus(m,C1),Power(Times(Sqr(a),Plus(m,n)),CN1)),Int(Times(Power(Times(a,Csc(Plus(e,Times(f,x)))),Plus(m,C2)),Power(Times(b,Sec(Plus(e,Times(f,x)))),n)),x),x)),And(FreeQ(List(a,b,e,f,n),x),LtQ(m,CN1),NeQ(Plus(m,n),C0),IntegersQ(Times(C2,m),Times(C2,n))))),
IIntegrate(2628,Int(Times(Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),a_DEFAULT),m_DEFAULT),Power(Times(b_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(a,Power(Times(a,Csc(Plus(e,Times(f,x)))),Subtract(m,C1)),Power(Times(b,Sec(Plus(e,Times(f,x)))),Plus(n,C1)),Power(Times(b,f,Plus(m,n)),CN1)),x)),Dist(Times(Plus(n,C1),Power(Times(Sqr(b),Plus(m,n)),CN1)),Int(Times(Power(Times(a,Csc(Plus(e,Times(f,x)))),m),Power(Times(b,Sec(Plus(e,Times(f,x)))),Plus(n,C2))),x),x)),And(FreeQ(List(a,b,e,f,m),x),LtQ(n,CN1),NeQ(Plus(m,n),C0),IntegersQ(Times(C2,m),Times(C2,n))))),
IIntegrate(2629,Int(Times(Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),a_DEFAULT),m_),Power(Times(b_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(Dist(Times(Power(Times(a,Csc(Plus(e,Times(f,x)))),m),Power(Times(b,Sec(Plus(e,Times(f,x)))),n),Power(Power(Tan(Plus(e,Times(f,x))),n),CN1)),Int(Power(Tan(Plus(e,Times(f,x))),n),x),x),And(FreeQ(List(a,b,e,f,m,n),x),Not(IntegerQ(n)),EqQ(Plus(m,n),C0)))),
IIntegrate(2630,Int(Times(Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),a_DEFAULT),m_),Power(Times(b_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(Dist(Times(Power(Times(a,Csc(Plus(e,Times(f,x)))),m),Power(Times(b,Sec(Plus(e,Times(f,x)))),n),Power(Times(a,Sin(Plus(e,Times(f,x)))),m),Power(Times(b,Cos(Plus(e,Times(f,x)))),n)),Int(Power(Times(Power(Times(a,Sin(Plus(e,Times(f,x)))),m),Power(Times(b,Cos(Plus(e,Times(f,x)))),n)),CN1),x),x),And(FreeQ(List(a,b,e,f,m,n),x),IntegerQ(Subtract(m,C1D2)),IntegerQ(Subtract(n,C1D2))))),
IIntegrate(2631,Int(Times(Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),a_DEFAULT),m_),Power(Times(b_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(Dist(Times(Sqr(a),Power(Times(a,Csc(Plus(e,Times(f,x)))),Subtract(m,C1)),Power(Times(b,Sec(Plus(e,Times(f,x)))),Plus(n,C1)),Power(Times(a,Sin(Plus(e,Times(f,x)))),Subtract(m,C1)),Power(Times(b,Cos(Plus(e,Times(f,x)))),Plus(n,C1)),Power(b,CN2)),Int(Power(Times(Power(Times(a,Sin(Plus(e,Times(f,x)))),m),Power(Times(b,Cos(Plus(e,Times(f,x)))),n)),CN1),x),x),And(FreeQ(List(a,b,e,f,m,n),x),Not(SimplerQ(Negate(m),Negate(n)))))),
IIntegrate(2632,Int(Times(Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT),n_),Power(Times(a_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_)),x_Symbol),
    Condition(Dist(Times(Sqr(a),Power(Times(a,Sec(Plus(e,Times(f,x)))),Subtract(m,C1)),Power(Times(b,Csc(Plus(e,Times(f,x)))),Plus(n,C1)),Power(Times(a,Cos(Plus(e,Times(f,x)))),Subtract(m,C1)),Power(Times(b,Sin(Plus(e,Times(f,x)))),Plus(n,C1)),Power(b,CN2)),Int(Power(Times(Power(Times(a,Cos(Plus(e,Times(f,x)))),m),Power(Times(b,Sin(Plus(e,Times(f,x)))),n)),CN1),x),x),FreeQ(List(a,b,e,f,m,n),x))),
IIntegrate(2633,Int(Power($($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_),x_Symbol),
    Condition(Negate(Dist(Power(d,CN1),Subst(Int(Expand(Power(Subtract(C1,Sqr(x)),Times(C1D2,Subtract(n,C1))),x),x),x,Cos(Plus(c,Times(d,x)))),x)),And(FreeQ(List(c,d),x),IGtQ(Times(C1D2,Subtract(n,C1)),C0)))),
IIntegrate(2634,Int(Sqr($($s("§sin"),Plus(c_DEFAULT,Times(C1D2,d_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(C1D2,x),x),Simp(Times(Sin(Plus(Times(C2,c),Times(d,x))),Power(Times(C2,d),CN1)),x)),FreeQ(List(c,d),x))),
IIntegrate(2635,Int(Power(Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Plus(Negate(Simp(Times(b,Cos(Plus(c,Times(d,x))),Power(Times(b,Sin(Plus(c,Times(d,x)))),Subtract(n,C1)),Power(Times(d,n),CN1)),x)),Dist(Times(Sqr(b),Subtract(n,C1),Power(n,CN1)),Int(Power(Times(b,Sin(Plus(c,Times(d,x)))),Subtract(n,C2)),x),x)),And(FreeQ(List(b,c,d),x),GtQ(n,C1),IntegerQ(Times(C2,n))))),
IIntegrate(2636,Int(Power(Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Plus(Simp(Times(Cos(Plus(c,Times(d,x))),Power(Times(b,Sin(Plus(c,Times(d,x)))),Plus(n,C1)),Power(Times(b,d,Plus(n,C1)),CN1)),x),Dist(Times(Plus(n,C2),Power(Times(Sqr(b),Plus(n,C1)),CN1)),Int(Power(Times(b,Sin(Plus(c,Times(d,x)))),Plus(n,C2)),x),x)),And(FreeQ(List(b,c,d),x),LtQ(n,CN1),IntegerQ(Times(C2,n))))),
IIntegrate(2637,Int($($s("§sin"),Plus(CPiHalf,c_DEFAULT,Times(d_DEFAULT,x_))),x_Symbol),
    Condition(Simp(Times(Sin(Plus(c,Times(d,x))),Power(d,CN1)),x),FreeQ(List(c,d),x))),
IIntegrate(2638,Int($($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),x_Symbol),
    Condition(Negate(Simp(Times(Cos(Plus(c,Times(d,x))),Power(d,CN1)),x)),FreeQ(List(c,d),x))),
IIntegrate(2639,Int(Sqrt($($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Simp(Times(C2,EllipticE(Times(C1D2,C1,Plus(c,Times(CN1,C1D2,Pi),Times(d,x))),C2),Power(d,CN1)),x),FreeQ(List(c,d),x))),
IIntegrate(2640,Int(Sqrt(Times(b_,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),x_Symbol),
    Condition(Dist(Times(Sqrt(Times(b,Sin(Plus(c,Times(d,x))))),Power(Sin(Plus(c,Times(d,x))),CN1D2)),Int(Sqrt(Sin(Plus(c,Times(d,x)))),x),x),FreeQ(List(b,c,d),x))),
IIntegrate(2641,Int(Power($($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),CN1D2),x_Symbol),
    Condition(Simp(Times(C2,EllipticF(Times(C1D2,C1,Plus(c,Times(CN1,C1D2,Pi),Times(d,x))),C2),Power(d,CN1)),x),FreeQ(List(c,d),x))),
IIntegrate(2642,Int(Power(Times(b_,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),CN1D2),x_Symbol),
    Condition(Dist(Times(Sqrt(Sin(Plus(c,Times(d,x)))),Power(Times(b,Sin(Plus(c,Times(d,x)))),CN1D2)),Int(Power(Sin(Plus(c,Times(d,x))),CN1D2),x),x),FreeQ(List(b,c,d),x))),
IIntegrate(2643,Int(Power(Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Simp(Times(Cos(Plus(c,Times(d,x))),Power(Times(b,Sin(Plus(c,Times(d,x)))),Plus(n,C1)),Hypergeometric2F1(C1D2,Times(C1D2,Plus(n,C1)),Times(C1D2,Plus(n,C3)),Sqr(Sin(Plus(c,Times(d,x))))),Power(Times(b,d,Plus(n,C1),Sqrt(Sqr(Cos(Plus(c,Times(d,x)))))),CN1)),x),And(FreeQ(List(b,c,d,n),x),Not(IntegerQ(Times(C2,n)))))),
IIntegrate(2644,Int(Sqr(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Simp(Times(C1D2,Plus(Times(C2,Sqr(a)),Sqr(b)),x),x),Negate(Simp(Times(C2,a,b,Cos(Plus(c,Times(d,x))),Power(d,CN1)),x)),Negate(Simp(Times(Sqr(b),Cos(Plus(c,Times(d,x))),Sin(Plus(c,Times(d,x))),Power(Times(C2,d),CN1)),x))),FreeQ(List(a,b,c,d),x))),
IIntegrate(2645,Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Int(ExpandTrig(Power(Plus(a,Times(b,$($s("§sin"),Plus(c,Times(d,x))))),n),x),x),And(FreeQ(List(a,b,c,d,n),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0),IGtQ(n,C0)))),
IIntegrate(2646,Int(Sqrt(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Simp(Times(CN2,b,Cos(Plus(c,Times(d,x))),Power(Times(d,Sqrt(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))))),CN1)),x),And(FreeQ(List(a,b,c,d),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0)))),
IIntegrate(2647,Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Negate(Simp(Times(b,Cos(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Subtract(n,C1)),Power(Times(d,n),CN1)),x)),Dist(Times(a,Subtract(Times(C2,n),C1),Power(n,CN1)),Int(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Subtract(n,C1)),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0),IGtQ(Subtract(n,C1D2),C0)))),
IIntegrate(2648,Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1),x_Symbol),
    Condition(Negate(Simp(Times(Cos(Plus(c,Times(d,x))),Power(Times(d,Plus(b,Times(a,Sin(Plus(c,Times(d,x)))))),CN1)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0)))),
IIntegrate(2649,Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1D2),x_Symbol),
    Condition(Dist(Times(CN2,Power(d,CN1)),Subst(Int(Power(Subtract(Times(C2,a),Sqr(x)),CN1),x),x,Times(b,Cos(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),CN1D2))),x),And(FreeQ(List(a,b,c,d),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0)))),
IIntegrate(2650,Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Simp(Times(b,Cos(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),n),Power(Times(a,d,Plus(Times(C2,n),C1)),CN1)),x),Dist(Times(Plus(n,C1),Power(Times(a,Plus(Times(C2,n),C1)),CN1)),Int(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Plus(n,C1)),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0),LtQ(n,CN1),IntegerQ(Times(C2,n)))))
  );
}
