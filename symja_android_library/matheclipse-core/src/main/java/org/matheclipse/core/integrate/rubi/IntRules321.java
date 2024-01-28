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
class IntRules321 { 
  public static IAST RULES = List( 
IIntegrate(6421,Integrate(Power(Plus(a_DEFAULT,Times(ArcCosh(Plus(C1,Times(d_DEFAULT,Sqr(x_)))),b_DEFAULT)),QQ(-3L,2L)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Sqrt(Times(d,Sqr(x))),Sqrt(Plus(C2,Times(d,Sqr(x)))),Power(Times(b,d,x,Sqrt(Plus(a,Times(b,ArcCosh(Plus(C1,Times(d,Sqr(x)))))))),CN1)),x),Negate(Simp(Times(Sqrt(CPiHalf),Plus(Cosh(Times(a,Power(Times(C2,b),CN1))),Sinh(Times(a,Power(Times(C2,b),CN1)))),Sinh(Times(C1D2,ArcCosh(Plus(C1,Times(d,Sqr(x)))))),Erf(Times(Sqrt(Plus(a,Times(b,ArcCosh(Plus(C1,Times(d,Sqr(x))))))),Power(Times(C2,b),CN1D2))),Power(Times(Power(b,QQ(3L,2L)),d,x),CN1)),x)),Simp(Times(Sqrt(CPiHalf),Subtract(Cosh(Times(a,Power(Times(C2,b),CN1))),Sinh(Times(a,Power(Times(C2,b),CN1)))),Sinh(Times(C1D2,ArcCosh(Plus(C1,Times(d,Sqr(x)))))),Erfi(Times(Sqrt(Plus(a,Times(b,ArcCosh(Plus(C1,Times(d,Sqr(x))))))),Power(Times(C2,b),CN1D2))),Power(Times(Power(b,QQ(3L,2L)),d,x),CN1)),x)),FreeQ(list(a,b,d),x))),
IIntegrate(6422,Integrate(Power(Plus(a_DEFAULT,Times(ArcCosh(Plus(CN1,Times(d_DEFAULT,Sqr(x_)))),b_DEFAULT)),QQ(-3L,2L)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Sqrt(Times(d,Sqr(x))),Sqrt(Plus(CN2,Times(d,Sqr(x)))),Power(Times(b,d,x,Sqrt(Plus(a,Times(b,ArcCosh(Plus(CN1,Times(d,Sqr(x)))))))),CN1)),x),Simp(Times(Sqrt(CPiHalf),Plus(Cosh(Times(a,Power(Times(C2,b),CN1))),Sinh(Times(a,Power(Times(C2,b),CN1)))),Cosh(Times(C1D2,ArcCosh(Plus(CN1,Times(d,Sqr(x)))))),Erf(Times(Sqrt(Plus(a,Times(b,ArcCosh(Plus(CN1,Times(d,Sqr(x))))))),Power(Times(C2,b),CN1D2))),Power(Times(Power(b,QQ(3L,2L)),d,x),CN1)),x),Simp(Times(Sqrt(CPiHalf),Subtract(Cosh(Times(a,Power(Times(C2,b),CN1))),Sinh(Times(a,Power(Times(C2,b),CN1)))),Cosh(Times(C1D2,ArcCosh(Plus(CN1,Times(d,Sqr(x)))))),Erfi(Times(Sqrt(Plus(a,Times(b,ArcCosh(Plus(CN1,Times(d,Sqr(x))))))),Power(Times(C2,b),CN1D2))),Power(Times(Power(b,QQ(3L,2L)),d,x),CN1)),x)),FreeQ(list(a,b,d),x))),
IIntegrate(6423,Integrate(Power(Plus(a_DEFAULT,Times(ArcCosh(Plus(C1,Times(d_DEFAULT,Sqr(x_)))),b_DEFAULT)),CN2),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Sqrt(Times(d,Sqr(x))),Sqrt(Plus(C2,Times(d,Sqr(x)))),Power(Times(C2,b,d,x,Plus(a,Times(b,ArcCosh(Plus(C1,Times(d,Sqr(x))))))),CN1)),x),Negate(Simp(Times(x,Sinh(Times(a,Power(Times(C2,b),CN1))),CoshIntegral(Times(Plus(a,Times(b,ArcCosh(Plus(C1,Times(d,Sqr(x)))))),Power(Times(C2,b),CN1))),Power(Times(C2,CSqrt2,Sqr(b),Sqrt(Times(d,Sqr(x)))),CN1)),x)),Simp(Times(x,Cosh(Times(a,Power(Times(C2,b),CN1))),SinhIntegral(Times(Plus(a,Times(b,ArcCosh(Plus(C1,Times(d,Sqr(x)))))),Power(Times(C2,b),CN1))),Power(Times(C2,CSqrt2,Sqr(b),Sqrt(Times(d,Sqr(x)))),CN1)),x)),FreeQ(list(a,b,d),x))),
IIntegrate(6424,Integrate(Power(Plus(a_DEFAULT,Times(ArcCosh(Plus(CN1,Times(d_DEFAULT,Sqr(x_)))),b_DEFAULT)),CN2),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Sqrt(Times(d,Sqr(x))),Sqrt(Plus(CN2,Times(d,Sqr(x)))),Power(Times(C2,b,d,x,Plus(a,Times(b,ArcCosh(Plus(CN1,Times(d,Sqr(x))))))),CN1)),x),Simp(Times(x,Cosh(Times(a,Power(Times(C2,b),CN1))),CoshIntegral(Times(Plus(a,Times(b,ArcCosh(Plus(CN1,Times(d,Sqr(x)))))),Power(Times(C2,b),CN1))),Power(Times(C2,CSqrt2,Sqr(b),Sqrt(Times(d,Sqr(x)))),CN1)),x),Negate(Simp(Times(x,Sinh(Times(a,Power(Times(C2,b),CN1))),SinhIntegral(Times(Plus(a,Times(b,ArcCosh(Plus(CN1,Times(d,Sqr(x)))))),Power(Times(C2,b),CN1))),Power(Times(C2,CSqrt2,Sqr(b),Sqrt(Times(d,Sqr(x)))),CN1)),x))),FreeQ(list(a,b,d),x))),
IIntegrate(6425,Integrate(Power(Plus(a_DEFAULT,Times(ArcCosh(Plus(c_,Times(d_DEFAULT,Sqr(x_)))),b_DEFAULT)),n_),x_Symbol),
    Condition(Plus(Simp(Times(CN1,x,Power(Plus(a,Times(b,ArcCosh(Plus(c,Times(d,Sqr(x)))))),Plus(n,C2)),Power(Times(C4,Sqr(b),Plus(n,C1),Plus(n,C2)),CN1)),x),Simp(Times(Plus(Times(C2,c,Sqr(x)),Times(d,Power(x,C4))),Power(Plus(a,Times(b,ArcCosh(Plus(c,Times(d,Sqr(x)))))),Plus(n,C1)),Power(Times(C2,b,Plus(n,C1),x,Sqrt(Plus(CN1,c,Times(d,Sqr(x)))),Sqrt(Plus(C1,c,Times(d,Sqr(x))))),CN1)),x),Simp(Star(Power(Times(C4,Sqr(b),Plus(n,C1),Plus(n,C2)),CN1),Integrate(Power(Plus(a,Times(b,ArcCosh(Plus(c,Times(d,Sqr(x)))))),Plus(n,C2)),x)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Sqr(c),C1),LtQ(n,CN1),NeQ(n,CN2)))),
IIntegrate(6426,Integrate(Times(Power(ArcCosh(Times(a_DEFAULT,Power(x_,p_))),n_DEFAULT),Power(x_,CN1)),x_Symbol),
    Condition(Simp(Star(Power(p,CN1),Subst(Integrate(Times(Power(x,n),Tanh(x)),x),x,ArcCosh(Times(a,Power(x,p))))),x),And(FreeQ(list(a,p),x),IGtQ(n,C0)))),
IIntegrate(6427,Integrate(Times(Power(ArcCosh(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),CN1))),m_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Integrate(Times(u,Power(ArcSech(Plus(Times(a,Power(c,CN1)),Times(b,Power(x,n),Power(c,CN1)))),m)),x),FreeQ(List(a,b,c,n,m),x))),
IIntegrate(6428,Integrate(Times(Power(ArcCosh(Sqrt(Plus(C1,Times(b_DEFAULT,Sqr(x_))))),n_DEFAULT),Power(Plus(C1,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Star(Times(Sqrt(Plus(CN1,Sqrt(Plus(C1,Times(b,Sqr(x)))))),Sqrt(Plus(C1,Sqrt(Plus(C1,Times(b,Sqr(x)))))),Power(Times(b,x),CN1)),Subst(Integrate(Times(Power(ArcCosh(x),n),Power(Times(Sqrt(Plus(CN1,x)),Sqrt(Plus(C1,x))),CN1)),x),x,Sqrt(Plus(C1,Times(b,Sqr(x)))))),x),FreeQ(list(b,n),x))),
IIntegrate(6429,Integrate(Power(f_,Times(Power(ArcCosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),c_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(b,CN1),Subst(Integrate(Times(Power(f,Times(c,Power(x,n))),Sinh(x)),x),x,ArcCosh(Plus(a,Times(b,x))))),x),And(FreeQ(List(a,b,c,f),x),IGtQ(n,C0)))),
IIntegrate(6430,Integrate(Times(Power(f_,Times(Power(ArcCosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),c_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(b,CN1),Subst(Integrate(Times(Power(Plus(Times(CN1,a,Power(b,CN1)),Times(Cosh(x),Power(b,CN1))),m),Power(f,Times(c,Power(x,n))),Sinh(x)),x),x,ArcCosh(Plus(a,Times(b,x))))),x),And(FreeQ(List(a,b,c,f),x),IGtQ(m,C0),IGtQ(n,C0)))),
IIntegrate(6431,Integrate(ArcCosh(u_),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcCosh(u)),x),Integrate(SimplifyIntegrand(Times(x,D(u,x),Power(Times(Sqrt(Plus(CN1,u)),Sqrt(Plus(C1,u))),CN1)),x),x)),And(InverseFunctionFreeQ(u,x),Not(FunctionOfExponentialQ(u,x))))),
IIntegrate(6432,Integrate(Times(Plus(a_DEFAULT,Times(ArcCosh(u_),b_DEFAULT)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Plus(a,Times(b,ArcCosh(u))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Integrate(SimplifyIntegrand(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),D(u,x),Power(Times(Sqrt(Plus(CN1,u)),Sqrt(Plus(C1,u))),CN1)),x),x)),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1),InverseFunctionFreeQ(u,x),Not(FunctionOfQ(Power(Plus(c,Times(d,x)),Plus(m,C1)),u,x)),Not(FunctionOfExponentialQ(u,x))))),
IIntegrate(6433,Integrate(Times(Plus(a_DEFAULT,Times(ArcCosh(u_),b_DEFAULT)),v_),x_Symbol),
    Condition(With(list(Set(w,IntHide(v,x))),Condition(Subtract(Simp(Star(Plus(a,Times(b,ArcCosh(u))),w),x),Simp(Star(b,Integrate(SimplifyIntegrand(Times(w,D(u,x),Power(Times(Sqrt(Plus(CN1,u)),Sqrt(Plus(C1,u))),CN1)),x),x)),x)),InverseFunctionFreeQ(w,x))),And(FreeQ(list(a,b),x),InverseFunctionFreeQ(u,x),Not(MatchQ(v,Condition(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x)),m_DEFAULT),FreeQ(list(c,d,m),x))))))),
IIntegrate(6434,Integrate(Exp(Times(ArcCosh(u_),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Power(Plus(u,Times(Sqrt(Plus(CN1,u)),Sqrt(Plus(C1,u)))),n),x),And(IntegerQ(n),PolyQ(u,x)))),
IIntegrate(6435,Integrate(Times(Exp(Times(ArcCosh(u_),n_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(x,m),Power(Plus(u,Times(Sqrt(Plus(CN1,u)),Sqrt(Plus(C1,u)))),n)),x),And(RationalQ(m),IntegerQ(n),PolyQ(u,x)))),
IIntegrate(6436,Integrate(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Subtract(Simp(Times(x,Power(Plus(a,Times(b,ArcTanh(Times(c,Power(x,n))))),p)),x),Simp(Star(Times(b,c,n,p),Integrate(Times(Power(x,n),Power(Plus(a,Times(b,ArcTanh(Times(c,Power(x,n))))),Subtract(p,C1)),Power(Subtract(C1,Times(Sqr(c),Power(x,Times(C2,n)))),CN1)),x)),x)),And(FreeQ(List(a,b,c,n),x),IGtQ(p,C0),Or(EqQ(n,C1),EqQ(p,C1))))),
IIntegrate(6437,Integrate(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Subtract(Simp(Times(x,Power(Plus(a,Times(b,ArcCoth(Times(c,Power(x,n))))),p)),x),Simp(Star(Times(b,c,n,p),Integrate(Times(Power(x,n),Power(Plus(a,Times(b,ArcCoth(Times(c,Power(x,n))))),Subtract(p,C1)),Power(Subtract(C1,Times(Sqr(c),Power(x,Times(C2,n)))),CN1)),x)),x)),And(FreeQ(List(a,b,c,n),x),IGtQ(p,C0),Or(EqQ(n,C1),EqQ(p,C1))))),
IIntegrate(6438,Integrate(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Subtract(Plus(a,Times(b,C1D2,Log(Plus(C1,Times(c,Power(x,n)))))),Times(b,C1D2,Log(Subtract(C1,Times(c,Power(x,n)))))),p),x),x),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),IGtQ(n,C0)))),
IIntegrate(6439,Integrate(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Subtract(Plus(a,Times(b,C1D2,Log(Plus(C1,Power(Times(Power(x,n),c),CN1))))),Times(b,C1D2,Log(Subtract(C1,Power(Times(Power(x,n),c),CN1))))),p),x),x),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),IGtQ(n,C0)))),
IIntegrate(6440,Integrate(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),x_Symbol),
    Condition(Integrate(Power(Plus(a,Times(b,ArcCoth(Power(Times(Power(x,n),c),CN1)))),p),x),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),ILtQ(n,C0))))
  );
}
