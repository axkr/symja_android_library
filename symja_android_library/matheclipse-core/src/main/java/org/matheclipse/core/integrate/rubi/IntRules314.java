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
class IntRules314 { 
  public static IAST RULES = List( 
IIntegrate(6281,Integrate(Power(Plus(a_DEFAULT,Times(ArcSinh(Plus(c_,Times(d_DEFAULT,Sqr(x_)))),b_DEFAULT)),QQ(-3L,2L)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Sqrt(Plus(Times(C2,c,d,Sqr(x)),Times(Sqr(d),Power(x,C4)))),Power(Times(b,d,x,Sqrt(Plus(a,Times(b,ArcSinh(Plus(c,Times(d,Sqr(x)))))))),CN1)),x),Negate(Simp(Times(Power(Times(CN1,c,Power(b,CN1)),QQ(3L,2L)),CSqrtPi,x,Subtract(Cosh(Times(a,Power(Times(C2,b),CN1))),Times(c,Sinh(Times(a,Power(Times(C2,b),CN1))))),FresnelC(Times(Sqrt(Times(CN1,c,Power(Times(Pi,b),CN1))),Sqrt(Plus(a,Times(b,ArcSinh(Plus(c,Times(d,Sqr(x))))))))),Power(Plus(Cosh(Times(C1D2,ArcSinh(Plus(c,Times(d,Sqr(x)))))),Times(c,Sinh(Times(C1D2,ArcSinh(Plus(c,Times(d,Sqr(x)))))))),CN1)),x)),Simp(Times(Power(Times(CN1,c,Power(b,CN1)),QQ(3L,2L)),CSqrtPi,x,Plus(Cosh(Times(a,Power(Times(C2,b),CN1))),Times(c,Sinh(Times(a,Power(Times(C2,b),CN1))))),FresnelS(Times(Sqrt(Times(CN1,c,Power(Times(Pi,b),CN1))),Sqrt(Plus(a,Times(b,ArcSinh(Plus(c,Times(d,Sqr(x))))))))),Power(Plus(Cosh(Times(C1D2,ArcSinh(Plus(c,Times(d,Sqr(x)))))),Times(c,Sinh(Times(C1D2,ArcSinh(Plus(c,Times(d,Sqr(x)))))))),CN1)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Sqr(c),CN1)))),
IIntegrate(6282,Integrate(Power(Plus(a_DEFAULT,Times(ArcSinh(Plus(c_,Times(d_DEFAULT,Sqr(x_)))),b_DEFAULT)),CN2),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Sqrt(Plus(Times(C2,c,d,Sqr(x)),Times(Sqr(d),Power(x,C4)))),Power(Times(C2,b,d,x,Plus(a,Times(b,ArcSinh(Plus(c,Times(d,Sqr(x))))))),CN1)),x),Simp(Times(x,Subtract(Cosh(Times(a,Power(Times(C2,b),CN1))),Times(c,Sinh(Times(a,Power(Times(C2,b),CN1))))),CoshIntegral(Times(Plus(a,Times(b,ArcSinh(Plus(c,Times(d,Sqr(x)))))),Power(Times(C2,b),CN1))),Power(Times(C4,Sqr(b),Plus(Cosh(Times(C1D2,ArcSinh(Plus(c,Times(d,Sqr(x)))))),Times(c,Sinh(Times(C1D2,ArcSinh(Plus(c,Times(d,Sqr(x))))))))),CN1)),x),Simp(Times(x,Subtract(Times(c,Cosh(Times(a,Power(Times(C2,b),CN1)))),Sinh(Times(a,Power(Times(C2,b),CN1)))),SinhIntegral(Times(Plus(a,Times(b,ArcSinh(Plus(c,Times(d,Sqr(x)))))),Power(Times(C2,b),CN1))),Power(Times(C4,Sqr(b),Plus(Cosh(Times(C1D2,ArcSinh(Plus(c,Times(d,Sqr(x)))))),Times(c,Sinh(Times(C1D2,ArcSinh(Plus(c,Times(d,Sqr(x))))))))),CN1)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Sqr(c),CN1)))),
IIntegrate(6283,Integrate(Power(Plus(a_DEFAULT,Times(ArcSinh(Plus(c_,Times(d_DEFAULT,Sqr(x_)))),b_DEFAULT)),n_),x_Symbol),
    Condition(Plus(Simp(Times(CN1,x,Power(Plus(a,Times(b,ArcSinh(Plus(c,Times(d,Sqr(x)))))),Plus(n,C2)),Power(Times(C4,Sqr(b),Plus(n,C1),Plus(n,C2)),CN1)),x),Simp(Times(Sqrt(Plus(Times(C2,c,d,Sqr(x)),Times(Sqr(d),Power(x,C4)))),Power(Plus(a,Times(b,ArcSinh(Plus(c,Times(d,Sqr(x)))))),Plus(n,C1)),Power(Times(C2,b,d,Plus(n,C1),x),CN1)),x),Simp(Star(Power(Times(C4,Sqr(b),Plus(n,C1),Plus(n,C2)),CN1),Integrate(Power(Plus(a,Times(b,ArcSinh(Plus(c,Times(d,Sqr(x)))))),Plus(n,C2)),x)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Sqr(c),CN1),LtQ(n,CN1),NeQ(n,CN2)))),
IIntegrate(6284,Integrate(Times(Power(ArcSinh(Times(a_DEFAULT,Power(x_,p_))),n_DEFAULT),Power(x_,CN1)),x_Symbol),
    Condition(Simp(Star(Power(p,CN1),Subst(Integrate(Times(Power(x,n),Coth(x)),x),x,ArcSinh(Times(a,Power(x,p))))),x),And(FreeQ(list(a,p),x),IGtQ(n,C0)))),
IIntegrate(6285,Integrate(Times(Power(ArcSinh(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),CN1))),m_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Integrate(Times(u,Power(ArcCsch(Plus(Times(a,Power(c,CN1)),Times(b,Power(x,n),Power(c,CN1)))),m)),x),FreeQ(List(a,b,c,n,m),x))),
IIntegrate(6286,Integrate(Times(Power(ArcSinh(Sqrt(Plus(CN1,Times(b_DEFAULT,Sqr(x_))))),n_DEFAULT),Power(Plus(CN1,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Star(Times(Sqrt(Times(b,Sqr(x))),Power(Times(b,x),CN1)),Subst(Integrate(Times(Power(ArcSinh(x),n),Power(Plus(C1,Sqr(x)),CN1D2)),x),x,Sqrt(Plus(CN1,Times(b,Sqr(x)))))),x),FreeQ(list(b,n),x))),
IIntegrate(6287,Integrate(Power(f_,Times(Power(ArcSinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),c_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(b,CN1),Subst(Integrate(Times(Power(f,Times(c,Power(x,n))),Cosh(x)),x),x,ArcSinh(Plus(a,Times(b,x))))),x),And(FreeQ(List(a,b,c,f),x),IGtQ(n,C0)))),
IIntegrate(6288,Integrate(Times(Power(f_,Times(Power(ArcSinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),c_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(b,CN1),Subst(Integrate(Times(Power(Plus(Times(CN1,a,Power(b,CN1)),Times(Sinh(x),Power(b,CN1))),m),Power(f,Times(c,Power(x,n))),Cosh(x)),x),x,ArcSinh(Plus(a,Times(b,x))))),x),And(FreeQ(List(a,b,c,f),x),IGtQ(m,C0),IGtQ(n,C0)))),
IIntegrate(6289,Integrate(ArcSinh(u_),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcSinh(u)),x),Integrate(SimplifyIntegrand(Times(x,D(u,x),Power(Plus(C1,Sqr(u)),CN1D2)),x),x)),And(InverseFunctionFreeQ(u,x),Not(FunctionOfExponentialQ(u,x))))),
IIntegrate(6290,Integrate(Times(Plus(a_DEFAULT,Times(ArcSinh(u_),b_DEFAULT)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Plus(a,Times(b,ArcSinh(u))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Integrate(SimplifyIntegrand(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),D(u,x),Power(Plus(C1,Sqr(u)),CN1D2)),x),x)),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1),InverseFunctionFreeQ(u,x),Not(FunctionOfQ(Power(Plus(c,Times(d,x)),Plus(m,C1)),u,x)),Not(FunctionOfExponentialQ(u,x))))),
IIntegrate(6291,Integrate(Times(Plus(a_DEFAULT,Times(ArcSinh(u_),b_DEFAULT)),v_),x_Symbol),
    Condition(With(list(Set(w,IntHide(v,x))),Condition(Subtract(Simp(Star(Plus(a,Times(b,ArcSinh(u))),w),x),Simp(Star(b,Integrate(SimplifyIntegrand(Times(w,D(u,x),Power(Plus(C1,Sqr(u)),CN1D2)),x),x)),x)),InverseFunctionFreeQ(w,x))),And(FreeQ(list(a,b),x),InverseFunctionFreeQ(u,x),Not(MatchQ(v,Condition(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x)),m_DEFAULT),FreeQ(list(c,d,m),x))))))),
IIntegrate(6292,Integrate(Exp(Times(ArcSinh(u_),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Power(Plus(u,Sqrt(Plus(C1,Sqr(u)))),n),x),And(IntegerQ(n),PolyQ(u,x)))),
IIntegrate(6293,Integrate(Times(Exp(Times(ArcSinh(u_),n_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(x,m),Power(Plus(u,Sqrt(Plus(C1,Sqr(u)))),n)),x),And(RationalQ(m),IntegerQ(n),PolyQ(u,x)))),
IIntegrate(6294,Integrate(Power(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),x_Symbol),
    Condition(Subtract(Simp(Times(x,Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),n)),x),Simp(Star(Times(b,c,n),Integrate(Times(x,Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),Subtract(n,C1)),Power(Times(Sqrt(Plus(C1,Times(c,x))),Sqrt(Plus(CN1,Times(c,x)))),CN1)),x)),x)),And(FreeQ(list(a,b,c),x),GtQ(n,C0)))),
IIntegrate(6295,Integrate(Power(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),x_Symbol),
    Condition(Subtract(Simp(Times(Sqrt(Plus(C1,Times(c,x))),Sqrt(Plus(CN1,Times(c,x))),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),Plus(n,C1)),Power(Times(b,c,Plus(n,C1)),CN1)),x),Simp(Star(Times(c,Power(Times(b,Plus(n,C1)),CN1)),Integrate(Times(x,Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),Plus(n,C1)),Power(Times(Sqrt(Plus(C1,Times(c,x))),Sqrt(Plus(CN1,Times(c,x)))),CN1)),x)),x)),And(FreeQ(list(a,b,c),x),LtQ(n,CN1)))),
IIntegrate(6296,Integrate(Power(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),x_Symbol),
    Condition(Simp(Star(Power(Times(b,c),CN1),Subst(Integrate(Times(Power(x,n),Sinh(Plus(Times(CN1,a,Power(b,CN1)),Times(x,Power(b,CN1))))),x),x,Plus(a,Times(b,ArcCosh(Times(c,x)))))),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(6297,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(x_,CN1)),x_Symbol),
    Condition(Simp(Star(Power(b,CN1),Subst(Integrate(Times(Power(x,n),Tanh(Plus(Times(CN1,a,Power(b,CN1)),Times(x,Power(b,CN1))))),x),x,Plus(a,Times(b,ArcCosh(Times(c,x)))))),x),And(FreeQ(list(a,b,c),x),IGtQ(n,C0)))),
IIntegrate(6298,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),Plus(m,C1)),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),n),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,c,n,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(d,x),Plus(m,C1)),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),Subtract(n,C1)),Power(Times(Sqrt(Plus(C1,Times(c,x))),Sqrt(Plus(CN1,Times(c,x)))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,m),x),IGtQ(n,C0),NeQ(m,CN1)))),
IIntegrate(6299,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),n),Power(Plus(m,C1),CN1)),x),Simp(Star(Times(b,c,n,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),Subtract(n,C1)),Power(Times(Sqrt(Plus(C1,Times(c,x))),Sqrt(Plus(CN1,Times(c,x)))),CN1)),x)),x)),And(FreeQ(list(a,b,c),x),IGtQ(m,C0),GtQ(n,C0)))),
IIntegrate(6300,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,m),Sqrt(Plus(C1,Times(c,x))),Sqrt(Plus(CN1,Times(c,x))),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),Plus(n,C1)),Power(Times(b,c,Plus(n,C1)),CN1)),x),Simp(Star(Power(Times(Sqr(b),Power(c,Plus(m,C1)),Plus(n,C1)),CN1),Subst(Integrate(ExpandTrigReduce(Power(x,Plus(n,C1)),Times(Power(Cosh(Plus(Times(CN1,a,Power(b,CN1)),Times(x,Power(b,CN1)))),Subtract(m,C1)),Subtract(m,Times(Plus(m,C1),Sqr(Cosh(Plus(Times(CN1,a,Power(b,CN1)),Times(x,Power(b,CN1)))))))),x),x),x,Plus(a,Times(b,ArcCosh(Times(c,x)))))),x)),And(FreeQ(list(a,b,c),x),IGtQ(m,C0),GeQ(n,CN2),LtQ(n,CN1))))
  );
}
