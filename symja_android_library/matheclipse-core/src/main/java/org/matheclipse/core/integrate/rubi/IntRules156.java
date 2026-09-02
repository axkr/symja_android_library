package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IAST;
import com.google.common.base.Supplier;

/** 
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class IntRules156 { 
  public static IAST RULES = List( 
IIntegrate(3121,Integrate(Times(Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),a_DEFAULT),m_),Power(Times(b_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Times(a,Csc(Plus(e,Times(f,x)))),m),Power(Times(b,Sec(Plus(e,Times(f,x)))),n),Power(Power(Tan(Plus(e,Times(f,x))),n),CN1)),Integrate(Power(Tan(Plus(e,Times(f,x))),n),x),x),x),And(FreeQ(List(a,b,e,f,m,n),x),Not(IntegerQ(n)),EqQ(Plus(m,n),C0)))),
IIntegrate(3122,Integrate(Times(Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),a_DEFAULT),m_),Power(Times(b_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Times(a,Csc(Plus(e,Times(f,x)))),m),Power(Times(b,Sec(Plus(e,Times(f,x)))),n),Power(Times(a,Sin(Plus(e,Times(f,x)))),m),Power(Times(b,Cos(Plus(e,Times(f,x)))),n)),Integrate(Power(Times(Power(Times(a,Sin(Plus(e,Times(f,x)))),m),Power(Times(b,Cos(Plus(e,Times(f,x)))),n)),CN1),x),x),x),And(FreeQ(List(a,b,e,f,m,n),x),IntegerQ(Plus(m,CN1D2)),IntegerQ(Plus(n,CN1D2))))),
IIntegrate(3123,Integrate(Times(Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),a_DEFAULT),m_),Power(Times(b_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(Simp(Dist(Times(Sqr(a),Power(b,CN2),Power(Times(a,Csc(Plus(e,Times(f,x)))),Plus(m,CN1)),Power(Times(b,Sec(Plus(e,Times(f,x)))),Plus(n,C1)),Power(Times(a,Sin(Plus(e,Times(f,x)))),Plus(m,CN1)),Power(Times(b,Cos(Plus(e,Times(f,x)))),Plus(n,C1))),Integrate(Power(Times(Power(Times(a,Sin(Plus(e,Times(f,x)))),m),Power(Times(b,Cos(Plus(e,Times(f,x)))),n)),CN1),x),x),x),And(FreeQ(List(a,b,e,f,m,n),x),Not(SimplerQ(Negate(m),Negate(n)))))),
IIntegrate(3124,Integrate(Times(Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT),n_),Power(Times(a_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_)),x_Symbol),
    Condition(Simp(Dist(Times(Sqr(a),Power(b,CN2),Power(Times(a,Sec(Plus(e,Times(f,x)))),Plus(m,CN1)),Power(Times(b,Csc(Plus(e,Times(f,x)))),Plus(n,C1)),Power(Times(a,Cos(Plus(e,Times(f,x)))),Plus(m,CN1)),Power(Times(b,Sin(Plus(e,Times(f,x)))),Plus(n,C1))),Integrate(Power(Times(Power(Times(a,Cos(Plus(e,Times(f,x)))),m),Power(Times(b,Sin(Plus(e,Times(f,x)))),n)),CN1),x),x),x),FreeQ(List(a,b,e,f,m,n),x))),
IIntegrate(3125,Integrate(Power($($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_),x_Symbol),
    Condition(Simp(Dist(Negate(Power(d,CN1)),Subst(Integrate(Expand(Power(Subtract(C1,Sqr(x)),Times(C1D2,Plus(n,CN1))),x),x),x,Cos(Plus(c,Times(d,x)))),x),x),And(FreeQ(list(c,d),x),IGtQ(Times(C1D2,Plus(n,CN1)),C0)))),
IIntegrate(3126,Integrate(Sqr($($s("§sin"),Plus(c_DEFAULT,Times(C1D2,d_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(C1D2,x),x),Simp(Times(Sin(Plus(Times(C2,c),Times(d,x))),Power(Times(C2,d),CN1)),x)),FreeQ(list(c,d),x))),
IIntegrate(3127,Integrate(Power(Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Plus(Simp(Times(CN1,b,Cos(Plus(c,Times(d,x))),Power(Times(b,Sin(Plus(c,Times(d,x)))),Plus(n,CN1)),Power(Times(d,n),CN1)),x),Simp(Dist(Times(Sqr(b),Plus(n,CN1),Power(n,CN1)),Integrate(Power(Times(b,Sin(Plus(c,Times(d,x)))),Plus(n,CN2)),x),x),x)),And(FreeQ(list(b,c,d),x),GtQ(n,C1),IntegerQ(Times(C2,n))))),
IIntegrate(3128,Integrate(Power(Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Plus(Simp(Times(Cos(Plus(c,Times(d,x))),Power(Times(b,Sin(Plus(c,Times(d,x)))),Plus(n,C1)),Power(Times(b,d,Plus(n,C1)),CN1)),x),Simp(Dist(Times(Plus(n,C2),Power(Times(Sqr(b),Plus(n,C1)),CN1)),Integrate(Power(Times(b,Sin(Plus(c,Times(d,x)))),Plus(n,C2)),x),x),x)),And(FreeQ(list(b,c,d),x),LtQ(n,CN1),IntegerQ(Times(C2,n))))),
IIntegrate(3129,Integrate($($s("§sin"),Plus(CPiHalf,c_DEFAULT,Times(d_DEFAULT,x_))),x_Symbol),
    Condition(Simp(Times(Sin(Plus(c,Times(d,x))),Power(d,CN1)),x),FreeQ(list(c,d),x))),
IIntegrate(3130,Integrate($($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),x_Symbol),
    Condition(Simp(Times(CN1,Cos(Plus(c,Times(d,x))),Power(d,CN1)),x),FreeQ(list(c,d),x))),
IIntegrate(3131,Integrate(Sqrt($($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Simp(Times(C2,Power(d,CN1),EllipticE(Times(C1D2,Plus(c,Times(CN1,C1D2,Pi),Times(d,x))),C2)),x),FreeQ(list(c,d),x))),
IIntegrate(3132,Integrate(Power($($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),CN1D2),x_Symbol),
    Condition(Simp(Times(C2,Power(d,CN1),EllipticF(Times(C1D2,Plus(c,Times(CN1,C1D2,Pi),Times(d,x))),C2)),x),FreeQ(list(c,d),x))),
IIntegrate(3133,Integrate(Power(Times(b_,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Simp(Dist(Times(Power(Times(b,Sin(Plus(c,Times(d,x)))),n),Power(Power(Sin(Plus(c,Times(d,x))),n),CN1)),Integrate(Power(Sin(Plus(c,Times(d,x))),n),x),x),x),And(FreeQ(list(b,c,d),x),LtQ(CN1,n,C1),IntegerQ(Times(C2,n))))),
IIntegrate(3134,Integrate(Power(Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Simp(Times(Cos(Plus(c,Times(d,x))),Power(Times(b,Sin(Plus(c,Times(d,x)))),Plus(n,C1)),Power(Times(b,d,Plus(n,C1),Sqrt(Sqr(Cos(Plus(c,Times(d,x)))))),CN1),Hypergeometric2F1(C1D2,Times(C1D2,Plus(n,C1)),Times(C1D2,Plus(n,C3)),Sqr(Sin(Plus(c,Times(d,x)))))),x),And(FreeQ(List(b,c,d,n),x),Not(IntegerQ(Times(C2,n)))))),
IIntegrate(3135,Integrate(Sqr(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Simp(Times(Plus(Times(C2,Sqr(a)),Sqr(b)),C1D2,x),x),Negate(Simp(Times(C2,a,b,Cos(Plus(c,Times(d,x))),Power(d,CN1)),x)),Negate(Simp(Times(Sqr(b),Cos(Plus(c,Times(d,x))),Sin(Plus(c,Times(d,x))),Power(Times(C2,d),CN1)),x))),FreeQ(List(a,b,c,d),x))),
IIntegrate(3136,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Integrate(ExpandTrig(Power(Plus(a,Times(b,$($s("§sin"),Plus(c,Times(d,x))))),n),x),x),And(FreeQ(List(a,b,c,d,n),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0),IGtQ(n,C0)))),
IIntegrate(3137,Integrate(Sqrt(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Simp(Times(CN2,b,Cos(Plus(c,Times(d,x))),Power(Times(d,Sqrt(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))))),CN1)),x),And(FreeQ(List(a,b,c,d),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0)))),
IIntegrate(3138,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Simp(Times(CN1,b,Cos(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Plus(n,CN1)),Power(Times(d,n),CN1)),x),Simp(Dist(Times(a,Plus(Times(C2,n),CN1),Power(n,CN1)),Integrate(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Plus(n,CN1)),x),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0),IGtQ(Plus(n,CN1D2),C0)))),
IIntegrate(3139,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1),x_Symbol),
    Condition(Simp(Times(CN1,Cos(Plus(c,Times(d,x))),Power(Times(d,Plus(b,Times(a,Sin(Plus(c,Times(d,x)))))),CN1)),x),And(FreeQ(List(a,b,c,d),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0)))),
IIntegrate(3140,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1D2),x_Symbol),
    Condition(Simp(Dist(Times(CN2,Power(d,CN1)),Subst(Integrate(Power(Subtract(Times(C2,a),Sqr(x)),CN1),x),x,Times(b,Cos(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),CN1D2))),x),x),And(FreeQ(List(a,b,c,d),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0))))
  );
}
