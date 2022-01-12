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
class IntRules189 { 
  public static IAST RULES = List( 
IIntegrate(3781,Integrate(Power(Plus(Times($($s("§csc"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),QQ(3L,2L)),x_Symbol),
    Condition(Plus(Integrate(Times(Plus(Sqr(a),Times(b,Subtract(Times(C2,a),b),Csc(Plus(c,Times(d,x))))),Power(Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),CN1D2)),x),Dist(Sqr(b),Integrate(Times(Csc(Plus(c,Times(d,x))),Plus(C1,Csc(Plus(c,Times(d,x)))),Power(Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),CN1D2)),x),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0)))),
IIntegrate(3782,Integrate(Power(Plus(Times($($s("§csc"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),n_),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Sqr(b),Cot(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),Subtract(n,C2)),Power(Times(d,Subtract(n,C1)),CN1)),x)),Dist(Power(Subtract(n,C1),CN1),Integrate(Times(Power(Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),Subtract(n,C3)),Simp(Plus(Times(Power(a,C3),Subtract(n,C1)),Times(b,Plus(Times(Sqr(b),Subtract(n,C2)),Times(C3,Sqr(a),Subtract(n,C1))),Csc(Plus(c,Times(d,x)))),Times(a,Sqr(b),Subtract(Times(C3,n),C4),Sqr(Csc(Plus(c,Times(d,x)))))),x)),x),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0),GtQ(n,C2),IntegerQ(Times(C2,n))))),
IIntegrate(3783,Integrate(Power(Plus(Times($($s("§csc"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),CN1),x_Symbol),
    Condition(Subtract(Simp(Times(x,Power(a,CN1)),x),Dist(Power(a,CN1),Integrate(Power(Plus(C1,Times(a,Sin(Plus(c,Times(d,x))),Power(b,CN1))),CN1),x),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0)))),
IIntegrate(3784,Integrate(Power(Plus(Times($($s("§csc"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),CN1D2),x_Symbol),
    Condition(Simp(Times(C2,Rt(Plus(a,b),C2),Sqrt(Times(b,Subtract(C1,Csc(Plus(c,Times(d,x)))),Power(Plus(a,b),CN1))),Sqrt(Times(CN1,b,Plus(C1,Csc(Plus(c,Times(d,x)))),Power(Subtract(a,b),CN1))),EllipticPi(Times(Plus(a,b),Power(a,CN1)),ArcSin(Times(Sqrt(Plus(a,Times(b,Csc(Plus(c,Times(d,x)))))),Power(Rt(Plus(a,b),C2),CN1))),Times(Plus(a,b),Power(Subtract(a,b),CN1))),Power(Times(a,d,Cot(Plus(c,Times(d,x)))),CN1)),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0)))),
IIntegrate(3785,Integrate(Power(Plus(Times($($s("§csc"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),n_),x_Symbol),
    Condition(Plus(Simp(Times(Sqr(b),Cot(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),Plus(n,C1)),Power(Times(a,d,Plus(n,C1),Subtract(Sqr(a),Sqr(b))),CN1)),x),Dist(Power(Times(a,Plus(n,C1),Subtract(Sqr(a),Sqr(b))),CN1),Integrate(Times(Power(Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),Plus(n,C1)),Simp(Plus(Times(Subtract(Sqr(a),Sqr(b)),Plus(n,C1)),Times(CN1,a,b,Plus(n,C1),Csc(Plus(c,Times(d,x)))),Times(Sqr(b),Plus(n,C2),Sqr(Csc(Plus(c,Times(d,x)))))),x)),x),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0),LtQ(n,CN1),IntegerQ(Times(C2,n))))),
IIntegrate(3786,Integrate(Power(Plus(Times($($s("§csc"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),n_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),n),x),And(FreeQ(List(a,b,c,d,n),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0),Not(IntegerQ(Times(C2,n)))))),
IIntegrate(3787,Integrate(Times(Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT),n_DEFAULT),Plus(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT),a_)),x_Symbol),
    Condition(Plus(Dist(a,Integrate(Power(Times(d,Csc(Plus(e,Times(f,x)))),n),x),x),Dist(Times(b,Power(d,CN1)),Integrate(Power(Times(d,Csc(Plus(e,Times(f,x)))),Plus(n,C1)),x),x)),FreeQ(List(a,b,d,e,f,n),x))),
IIntegrate(3788,Integrate(Times(Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT),n_DEFAULT),Sqr(Plus(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT),a_))),x_Symbol),
    Condition(Plus(Dist(Times(C2,a,b,Power(d,CN1)),Integrate(Power(Times(d,Csc(Plus(e,Times(f,x)))),Plus(n,C1)),x),x),Integrate(Times(Power(Times(d,Csc(Plus(e,Times(f,x)))),n),Plus(Sqr(a),Times(Sqr(b),Sqr(Csc(Plus(e,Times(f,x))))))),x)),FreeQ(List(a,b,d,e,f,n),x))),
IIntegrate(3789,Integrate(Times(Sqr($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),Power(Plus(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT),a_),CN1)),x_Symbol),
    Condition(Subtract(Dist(Power(b,CN1),Integrate(Csc(Plus(e,Times(f,x))),x),x),Dist(Times(a,Power(b,CN1)),Integrate(Times(Csc(Plus(e,Times(f,x))),Power(Plus(a,Times(b,Csc(Plus(e,Times(f,x))))),CN1)),x),x)),FreeQ(List(a,b,e,f),x))),
IIntegrate(3790,Integrate(Times(Power($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),C3),Power(Plus(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT),a_),CN1)),x_Symbol),
    Condition(Subtract(Negate(Simp(Times(Cot(Plus(e,Times(f,x))),Power(Times(b,f),CN1)),x)),Dist(Times(a,Power(b,CN1)),Integrate(Times(Sqr(Csc(Plus(e,Times(f,x)))),Power(Plus(a,Times(b,Csc(Plus(e,Times(f,x))))),CN1)),x),x)),FreeQ(List(a,b,e,f),x))),
IIntegrate(3791,Integrate(Times(Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT),n_DEFAULT),Power(Plus(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT),a_),m_)),x_Symbol),
    Condition(Integrate(ExpandTrig(Times(Power(Plus(a,Times(b,$($s("§csc"),Plus(e,Times(f,x))))),m),Power(Times(d,$($s("§csc"),Plus(e,Times(f,x)))),n)),x),x),And(FreeQ(List(a,b,d,e,f,m,n),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0),IGtQ(m,C0),RationalQ(n)))),
IIntegrate(3792,Integrate(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),Sqrt(Plus(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT),a_))),x_Symbol),
    Condition(Simp(Times(CN2,b,Cot(Plus(e,Times(f,x))),Power(Times(f,Sqrt(Plus(a,Times(b,Csc(Plus(e,Times(f,x))))))),CN1)),x),And(FreeQ(List(a,b,e,f),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0)))),
IIntegrate(3793,Integrate(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),Power(Plus(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT),a_),m_)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(b,Cot(Plus(e,Times(f,x))),Power(Plus(a,Times(b,Csc(Plus(e,Times(f,x))))),Subtract(m,C1)),Power(Times(f,m),CN1)),x)),Dist(Times(a,Subtract(Times(C2,m),C1),Power(m,CN1)),Integrate(Times(Csc(Plus(e,Times(f,x))),Power(Plus(a,Times(b,Csc(Plus(e,Times(f,x))))),Subtract(m,C1))),x),x)),And(FreeQ(List(a,b,e,f),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0),GtQ(m,C1D2),IntegerQ(Times(C2,m))))),
IIntegrate(3794,Integrate(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),Power(Plus(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT),a_),CN1)),x_Symbol),
    Condition(Negate(Simp(Times(Cot(Plus(e,Times(f,x))),Power(Times(f,Plus(b,Times(a,Csc(Plus(e,Times(f,x)))))),CN1)),x)),And(FreeQ(List(a,b,e,f),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0)))),
IIntegrate(3795,Integrate(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),Power(Plus(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT),a_),CN1D2)),x_Symbol),
    Condition(Dist(Times(CN2,Power(f,CN1)),Subst(Integrate(Power(Plus(Times(C2,a),Sqr(x)),CN1),x),x,Times(b,Cot(Plus(e,Times(f,x))),Power(Plus(a,Times(b,Csc(Plus(e,Times(f,x))))),CN1D2))),x),And(FreeQ(List(a,b,e,f),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0)))),
IIntegrate(3796,Integrate(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),Power(Plus(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT),a_),m_)),x_Symbol),
    Condition(Plus(Simp(Times(b,Cot(Plus(e,Times(f,x))),Power(Plus(a,Times(b,Csc(Plus(e,Times(f,x))))),m),Power(Times(a,f,Plus(Times(C2,m),C1)),CN1)),x),Dist(Times(Plus(m,C1),Power(Times(a,Plus(Times(C2,m),C1)),CN1)),Integrate(Times(Csc(Plus(e,Times(f,x))),Power(Plus(a,Times(b,Csc(Plus(e,Times(f,x))))),Plus(m,C1))),x),x)),And(FreeQ(List(a,b,e,f),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0),LtQ(m,Negate(C1D2)),IntegerQ(Times(C2,m))))),
IIntegrate(3797,Integrate(Times(Sqr($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),Power(Plus(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT),a_),m_)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Cot(Plus(e,Times(f,x))),Power(Plus(a,Times(b,Csc(Plus(e,Times(f,x))))),m),Power(Times(f,Plus(Times(C2,m),C1)),CN1)),x)),Dist(Times(m,Power(Times(b,Plus(Times(C2,m),C1)),CN1)),Integrate(Times(Csc(Plus(e,Times(f,x))),Power(Plus(a,Times(b,Csc(Plus(e,Times(f,x))))),Plus(m,C1))),x),x)),And(FreeQ(List(a,b,e,f),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0),LtQ(m,Negate(C1D2))))),
IIntegrate(3798,Integrate(Times(Sqr($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),Power(Plus(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT),a_),m_)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Cot(Plus(e,Times(f,x))),Power(Plus(a,Times(b,Csc(Plus(e,Times(f,x))))),m),Power(Times(f,Plus(m,C1)),CN1)),x)),Dist(Times(a,m,Power(Times(b,Plus(m,C1)),CN1)),Integrate(Times(Csc(Plus(e,Times(f,x))),Power(Plus(a,Times(b,Csc(Plus(e,Times(f,x))))),m)),x),x)),And(FreeQ(List(a,b,e,f,m),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0),Not(LtQ(m,Negate(C1D2)))))),
IIntegrate(3799,Integrate(Times(Power($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),C3),Power(Plus(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT),a_),m_)),x_Symbol),
    Condition(Subtract(Simp(Times(b,Cot(Plus(e,Times(f,x))),Power(Plus(a,Times(b,Csc(Plus(e,Times(f,x))))),m),Power(Times(a,f,Plus(Times(C2,m),C1)),CN1)),x),Dist(Power(Times(Sqr(a),Plus(Times(C2,m),C1)),CN1),Integrate(Times(Csc(Plus(e,Times(f,x))),Power(Plus(a,Times(b,Csc(Plus(e,Times(f,x))))),Plus(m,C1)),Subtract(Times(a,m),Times(b,Plus(Times(C2,m),C1),Csc(Plus(e,Times(f,x)))))),x),x)),And(FreeQ(List(a,b,e,f),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0),LtQ(m,Negate(C1D2))))),
IIntegrate(3800,Integrate(Times(Power($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),C3),Power(Plus(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT),a_),m_)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Cot(Plus(e,Times(f,x))),Power(Plus(a,Times(b,Csc(Plus(e,Times(f,x))))),Plus(m,C1)),Power(Times(b,f,Plus(m,C2)),CN1)),x)),Dist(Power(Times(b,Plus(m,C2)),CN1),Integrate(Times(Csc(Plus(e,Times(f,x))),Power(Plus(a,Times(b,Csc(Plus(e,Times(f,x))))),m),Subtract(Times(b,Plus(m,C1)),Times(a,Csc(Plus(e,Times(f,x)))))),x),x)),And(FreeQ(List(a,b,e,f,m),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0),Not(LtQ(m,Negate(C1D2))))))
  );
}
