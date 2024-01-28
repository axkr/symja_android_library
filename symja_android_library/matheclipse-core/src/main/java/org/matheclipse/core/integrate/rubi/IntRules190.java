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
class IntRules190 { 
  public static IAST RULES = List( 
IIntegrate(3801,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(Pi,k_DEFAULT),Times(Complex(C0,$p("fz")),f_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Simp(Star(C2,Integrate(Times(Power(Plus(c,Times(d,x)),m),Exp(Plus(Times(CN1,CI,e),Times(f,$s("fz"),x))),Power(Subtract(Plus(b,Times(C2,a,Exp(Plus(Times(CN1,CI,e),Times(f,$s("fz"),x))),Power(Exp(Times(CI,Pi,Subtract(k,C1D2))),CN1))),Times(b,Exp(Times(C2,Plus(Times(CN1,CI,e),Times(f,$s("fz"),x)))),Power(Exp(Times(C2,CI,k,Pi)),CN1))),CN1),Power(Exp(Times(CI,Pi,Subtract(k,C1D2))),CN1)),x)),x),And(FreeQ(List(a,b,c,d,e,f,$s("fz")),x),IntegerQ(Times(C2,k)),NeQ(Subtract(Sqr(a),Sqr(b)),C0),IGtQ(m,C0)))),
IIntegrate(3802,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(Pi,k_DEFAULT),Times(f_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Simp(Star(C2,Integrate(Times(Power(Plus(c,Times(d,x)),m),Exp(Times(CI,Pi,Subtract(k,C1D2))),Exp(Times(CI,Plus(e,Times(f,x)))),Power(Subtract(Plus(b,Times(C2,a,Exp(Times(CI,Pi,Subtract(k,C1D2))),Exp(Times(CI,Plus(e,Times(f,x)))))),Times(b,Exp(Times(C2,CI,k,Pi)),Exp(Times(C2,CI,Plus(e,Times(f,x)))))),CN1)),x)),x),And(FreeQ(List(a,b,c,d,e,f),x),IntegerQ(Times(C2,k)),NeQ(Subtract(Sqr(a),Sqr(b)),C0),IGtQ(m,C0)))),
IIntegrate(3803,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(Complex(C0,$p("fz")),f_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Simp(Star(C2,Integrate(Times(Power(Plus(c,Times(d,x)),m),Exp(Plus(Times(CN1,CI,e),Times(f,$s("fz"),x))),Power(Plus(Times(CN1,CI,b),Times(C2,a,Exp(Plus(Times(CN1,CI,e),Times(f,$s("fz"),x)))),Times(CI,b,Exp(Times(C2,Plus(Times(CN1,CI,e),Times(f,$s("fz"),x)))))),CN1)),x)),x),And(FreeQ(List(a,b,c,d,e,f,$s("fz")),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0),IGtQ(m,C0)))),
IIntegrate(3804,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Simp(Star(C2,Integrate(Times(Power(Plus(c,Times(d,x)),m),Exp(Times(CI,Plus(e,Times(f,x)))),Power(Subtract(Plus(Times(CI,b),Times(C2,a,Exp(Times(CI,Plus(e,Times(f,x)))))),Times(CI,b,Exp(Times(C2,CI,Plus(e,Times(f,x)))))),CN1)),x)),x),And(FreeQ(List(a,b,c,d,e,f),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0),IGtQ(m,C0)))),
IIntegrate(3805,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),CN2)),x_Symbol),
    Condition(Plus(Simp(Times(b,Power(Plus(c,Times(d,x)),m),Cos(Plus(e,Times(f,x))),Power(Times(f,Subtract(Sqr(a),Sqr(b)),Plus(a,Times(b,Sin(Plus(e,Times(f,x)))))),CN1)),x),Simp(Star(Times(a,Power(Subtract(Sqr(a),Sqr(b)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Plus(a,Times(b,Sin(Plus(e,Times(f,x))))),CN1)),x)),x),Negate(Simp(Star(Times(b,d,m,Power(Times(f,Subtract(Sqr(a),Sqr(b))),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,C1)),Cos(Plus(e,Times(f,x))),Power(Plus(a,Times(b,Sin(Plus(e,Times(f,x))))),CN1)),x)),x))),And(FreeQ(List(a,b,c,d,e,f),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0),IGtQ(m,C0)))),
IIntegrate(3806,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),n_)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,b,Power(Plus(c,Times(d,x)),m),Cos(Plus(e,Times(f,x))),Power(Plus(a,Times(b,Sin(Plus(e,Times(f,x))))),Plus(n,C1)),Power(Times(f,Plus(n,C1),Subtract(Sqr(a),Sqr(b))),CN1)),x),Simp(Star(Times(a,Power(Subtract(Sqr(a),Sqr(b)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Plus(a,Times(b,Sin(Plus(e,Times(f,x))))),Plus(n,C1))),x)),x),Negate(Simp(Star(Times(b,Plus(n,C2),Power(Times(Plus(n,C1),Subtract(Sqr(a),Sqr(b))),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),m),Sin(Plus(e,Times(f,x))),Power(Plus(a,Times(b,Sin(Plus(e,Times(f,x))))),Plus(n,C1))),x)),x)),Simp(Star(Times(b,d,m,Power(Times(f,Plus(n,C1),Subtract(Sqr(a),Sqr(b))),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,C1)),Cos(Plus(e,Times(f,x))),Power(Plus(a,Times(b,Sin(Plus(e,Times(f,x))))),Plus(n,C1))),x)),x)),And(FreeQ(List(a,b,c,d,e,f),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0),ILtQ(n,CN2),IGtQ(m,C0)))),
IIntegrate(3807,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),n_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(c,Times(d,x)),m),Power(Plus(a,Times(b,Sin(Plus(e,Times(f,x))))),n)),x),FreeQ(List(a,b,c,d,e,f,m,n),x))),
IIntegrate(3808,Integrate(Times(Power(u_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(v_))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),m),Power(Plus(a,Times(b,Sin(ExpandToSum(v,x)))),n)),x),And(FreeQ(List(a,b,m,n),x),LinearQ(list(u,v),x),Not(LinearMatchQ(list(u,v),x))))),
IIntegrate(3809,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cos(v_),b_DEFAULT)),n_DEFAULT),Power(u_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),m),Power(Plus(a,Times(b,Cos(ExpandToSum(v,x)))),n)),x),And(FreeQ(List(a,b,m,n),x),LinearQ(list(u,v),x),Not(LinearMatchQ(list(u,v),x))))),
IIntegrate(3810,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Sin(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Power(x,n))),p),x),x),And(FreeQ(List(a,b,c,d,n),x),IGtQ(p,C0)))),
IIntegrate(3811,Integrate(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Cos(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Power(x,n))),p),x),x),And(FreeQ(List(a,b,c,d,n),x),IGtQ(p,C0)))),
IIntegrate(3812,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(Negate(n),C1)),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Sin(Plus(c,Times(d,x))),Power(Times(b,n,Plus(p,C1)),CN1)),x),Negate(Simp(Star(Times(Plus(Negate(n),C1),Power(Times(b,n,Plus(p,C1)),CN1)),Integrate(Times(Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Sin(Plus(c,Times(d,x))),Power(Power(x,n),CN1)),x)),x)),Negate(Simp(Star(Times(d,Power(Times(b,n,Plus(p,C1)),CN1)),Integrate(Times(Power(x,Plus(Negate(n),C1)),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Cos(Plus(c,Times(d,x)))),x)),x))),And(FreeQ(List(a,b,c,d),x),ILtQ(p,CN1),IGtQ(n,C2)))),
IIntegrate(3813,Integrate(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(Negate(n),C1)),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Cos(Plus(c,Times(d,x))),Power(Times(b,n,Plus(p,C1)),CN1)),x),Negate(Simp(Star(Times(Plus(Negate(n),C1),Power(Times(b,n,Plus(p,C1)),CN1)),Integrate(Times(Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Cos(Plus(c,Times(d,x))),Power(Power(x,n),CN1)),x)),x)),Simp(Star(Times(d,Power(Times(b,n,Plus(p,C1)),CN1)),Integrate(Times(Power(x,Plus(Negate(n),C1)),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Sin(Plus(c,Times(d,x)))),x)),x)),And(FreeQ(List(a,b,c,d),x),ILtQ(p,CN1),IGtQ(n,C2)))),
IIntegrate(3814,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Sin(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Power(x,n))),p),x),x),And(FreeQ(List(a,b,c,d),x),ILtQ(p,C0),IGtQ(n,C0),Or(EqQ(n,C2),EqQ(p,CN1))))),
IIntegrate(3815,Integrate(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Cos(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Power(x,n))),p),x),x),And(FreeQ(List(a,b,c,d),x),ILtQ(p,C0),IGtQ(n,C0),Or(EqQ(n,C2),EqQ(p,CN1))))),
IIntegrate(3816,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Integrate(Times(Power(x,Times(n,p)),Power(Plus(b,Times(a,Power(Power(x,n),CN1))),p),Sin(Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d),x),ILtQ(p,C0),ILtQ(n,C0)))),
IIntegrate(3817,Integrate(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Integrate(Times(Power(x,Times(n,p)),Power(Plus(b,Times(a,Power(Power(x,n),CN1))),p),Cos(Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d),x),ILtQ(p,C0),ILtQ(n,C0)))),
IIntegrate(3818,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(a,Times(b,Power(x,n))),p),Sin(Plus(c,Times(d,x)))),x),FreeQ(List(a,b,c,d,n,p),x))),
IIntegrate(3819,Integrate(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(a,Times(b,Power(x,n))),p),Cos(Plus(c,Times(d,x)))),x),FreeQ(List(a,b,c,d,n,p),x))),
IIntegrate(3820,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Sin(Plus(c,Times(d,x))),Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Power(x,n))),p)),x),x),And(FreeQ(List(a,b,c,d,e,m,n),x),IGtQ(p,C0))))
  );
}
