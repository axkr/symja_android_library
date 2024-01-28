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
class IntRules354 { 
  public static IAST RULES = List( 
IIntegrate(7081,Integrate(Times(CosIntegral(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(e,x),Plus(m,C1)),CosIntegral(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,d,n,Power(Plus(m,C1),CN1)),Integrate(Times(Power(Times(e,x),m),Cos(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,m,n),x),NeQ(m,CN1)))),
IIntegrate(7082,Integrate(SinhIntegral(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),SinhIntegral(Plus(a,Times(b,x))),Power(b,CN1)),x),Simp(Times(Cosh(Plus(a,Times(b,x))),Power(b,CN1)),x)),FreeQ(list(a,b),x))),
IIntegrate(7083,Integrate(CoshIntegral(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),CoshIntegral(Plus(a,Times(b,x))),Power(b,CN1)),x),Simp(Times(Sinh(Plus(a,Times(b,x))),Power(b,CN1)),x)),FreeQ(list(a,b),x))),
IIntegrate(7084,Integrate(Times(Power(x_,CN1),SinhIntegral(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Times(C1D2,b,x,HypergeometricPFQ(list(C1,C1,C1),list(C2,C2,C2),Times(CN1,b,x))),x),Simp(Times(C1D2,b,x,HypergeometricPFQ(list(C1,C1,C1),list(C2,C2,C2),Times(b,x))),x)),FreeQ(b,x))),
IIntegrate(7085,Integrate(Times(CoshIntegral(Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,C1D2,b,x,HypergeometricPFQ(list(C1,C1,C1),list(C2,C2,C2),Times(CN1,b,x))),x),Simp(Times(C1D2,b,x,HypergeometricPFQ(list(C1,C1,C1),list(C2,C2,C2),Times(b,x))),x),Simp(Times(EulerGamma,Log(x)),x),Simp(Times(C1D2,Sqr(Log(Times(b,x)))),x)),FreeQ(b,x))),
IIntegrate(7086,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),SinhIntegral(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),SinhIntegral(Plus(a,Times(b,x))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Sinh(Plus(a,Times(b,x))),Power(Plus(a,Times(b,x)),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1)))),
IIntegrate(7087,Integrate(Times(CoshIntegral(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),CoshIntegral(Plus(a,Times(b,x))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Cosh(Plus(a,Times(b,x))),Power(Plus(a,Times(b,x)),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1)))),
IIntegrate(7088,Integrate(Sqr(SinhIntegral(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),Sqr(SinhIntegral(Plus(a,Times(b,x)))),Power(b,CN1)),x),Simp(Star(C2,Integrate(Times(Sinh(Plus(a,Times(b,x))),SinhIntegral(Plus(a,Times(b,x)))),x)),x)),FreeQ(list(a,b),x))),
IIntegrate(7089,Integrate(Sqr(CoshIntegral(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),Sqr(CoshIntegral(Plus(a,Times(b,x)))),Power(b,CN1)),x),Simp(Star(C2,Integrate(Times(Cosh(Plus(a,Times(b,x))),CoshIntegral(Plus(a,Times(b,x)))),x)),x)),FreeQ(list(a,b),x))),
IIntegrate(7090,Integrate(Times(Power(x_,m_DEFAULT),Sqr(SinhIntegral(Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Sqr(SinhIntegral(Times(b,x))),Power(Plus(m,C1),CN1)),x),Simp(Star(Times(C2,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,m),Sinh(Times(b,x)),SinhIntegral(Times(b,x))),x)),x)),And(FreeQ(b,x),IGtQ(m,C0)))),
IIntegrate(7091,Integrate(Times(Sqr(CoshIntegral(Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Sqr(CoshIntegral(Times(b,x))),Power(Plus(m,C1),CN1)),x),Simp(Star(Times(C2,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,m),Cosh(Times(b,x)),CoshIntegral(Times(b,x))),x)),x)),And(FreeQ(b,x),IGtQ(m,C0)))),
IIntegrate(7092,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Sqr(SinhIntegral(Plus(a_,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(Plus(Simp(Times(Plus(a,Times(b,x)),Power(Plus(c,Times(d,x)),m),Sqr(SinhIntegral(Plus(a,Times(b,x)))),Power(Times(b,Plus(m,C1)),CN1)),x),Negate(Simp(Star(Times(C2,Power(Plus(m,C1),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),m),Sinh(Plus(a,Times(b,x))),SinhIntegral(Plus(a,Times(b,x)))),x)),x)),Simp(Star(Times(Subtract(Times(b,c),Times(a,d)),m,Power(Times(b,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,C1)),Sqr(SinhIntegral(Plus(a,Times(b,x))))),x)),x)),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0)))),
IIntegrate(7093,Integrate(Times(Sqr(CoshIntegral(Plus(a_,Times(b_DEFAULT,x_)))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Plus(a,Times(b,x)),Power(Plus(c,Times(d,x)),m),Sqr(CoshIntegral(Plus(a,Times(b,x)))),Power(Times(b,Plus(m,C1)),CN1)),x),Negate(Simp(Star(Times(C2,Power(Plus(m,C1),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),m),Cosh(Plus(a,Times(b,x))),CoshIntegral(Plus(a,Times(b,x)))),x)),x)),Simp(Star(Times(Subtract(Times(b,c),Times(a,d)),m,Power(Times(b,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,C1)),Sqr(CoshIntegral(Plus(a,Times(b,x))))),x)),x)),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0)))),
IIntegrate(7094,Integrate(Times(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),SinhIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Cosh(Plus(a,Times(b,x))),SinhIntegral(Plus(c,Times(d,x))),Power(b,CN1)),x),Simp(Star(Times(d,Power(b,CN1)),Integrate(Times(Cosh(Plus(a,Times(b,x))),Sinh(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),CN1)),x)),x)),FreeQ(List(a,b,c,d),x))),
IIntegrate(7095,Integrate(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),CoshIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Sinh(Plus(a,Times(b,x))),CoshIntegral(Plus(c,Times(d,x))),Power(b,CN1)),x),Simp(Star(Times(d,Power(b,CN1)),Integrate(Times(Sinh(Plus(a,Times(b,x))),Cosh(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),CN1)),x)),x)),FreeQ(List(a,b,c,d),x))),
IIntegrate(7096,Integrate(Times(Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),SinhIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),m),Cosh(Plus(a,Times(b,x))),SinhIntegral(Plus(c,Times(d,x))),Power(b,CN1)),x),Negate(Simp(Star(Times(d,Power(b,CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),m),Cosh(Plus(a,Times(b,x))),Sinh(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),CN1)),x)),x)),Negate(Simp(Star(Times(f,m,Power(b,CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Subtract(m,C1)),Cosh(Plus(a,Times(b,x))),SinhIntegral(Plus(c,Times(d,x)))),x)),x))),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0)))),
IIntegrate(7097,Integrate(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),CoshIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),m),Sinh(Plus(a,Times(b,x))),CoshIntegral(Plus(c,Times(d,x))),Power(b,CN1)),x),Negate(Simp(Star(Times(d,Power(b,CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),m),Sinh(Plus(a,Times(b,x))),Cosh(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),CN1)),x)),x)),Negate(Simp(Star(Times(f,m,Power(b,CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Subtract(m,C1)),Sinh(Plus(a,Times(b,x))),CoshIntegral(Plus(c,Times(d,x)))),x)),x))),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0)))),
IIntegrate(7098,Integrate(Times(Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),SinhIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Sinh(Plus(a,Times(b,x))),SinhIntegral(Plus(c,Times(d,x))),Power(Times(f,Plus(m,C1)),CN1)),x),Negate(Simp(Star(Times(b,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Cosh(Plus(a,Times(b,x))),SinhIntegral(Plus(c,Times(d,x)))),x)),x)),Negate(Simp(Star(Times(d,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Sinh(Plus(a,Times(b,x))),Sinh(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),CN1)),x)),x))),And(FreeQ(List(a,b,c,d,e,f),x),ILtQ(m,CN1)))),
IIntegrate(7099,Integrate(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),CoshIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Cosh(Plus(a,Times(b,x))),CoshIntegral(Plus(c,Times(d,x))),Power(Times(f,Plus(m,C1)),CN1)),x),Negate(Simp(Star(Times(b,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Sinh(Plus(a,Times(b,x))),CoshIntegral(Plus(c,Times(d,x)))),x)),x)),Negate(Simp(Star(Times(d,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Cosh(Plus(a,Times(b,x))),Cosh(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),CN1)),x)),x))),And(FreeQ(List(a,b,c,d,e,f),x),ILtQ(m,CN1)))),
IIntegrate(7100,Integrate(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),SinhIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Sinh(Plus(a,Times(b,x))),SinhIntegral(Plus(c,Times(d,x))),Power(b,CN1)),x),Simp(Star(Times(d,Power(b,CN1)),Integrate(Times(Sinh(Plus(a,Times(b,x))),Sinh(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),CN1)),x)),x)),FreeQ(List(a,b,c,d),x)))
  );
}
