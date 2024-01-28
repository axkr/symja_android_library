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
class IntRules355 { 
  public static IAST RULES = List( 
IIntegrate(7101,Integrate(Times(CoshIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Cosh(Plus(a,Times(b,x))),CoshIntegral(Plus(c,Times(d,x))),Power(b,CN1)),x),Simp(Star(Times(d,Power(b,CN1)),Integrate(Times(Cosh(Plus(a,Times(b,x))),Cosh(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),CN1)),x)),x)),FreeQ(List(a,b,c,d),x))),
IIntegrate(7102,Integrate(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),SinhIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),m),Sinh(Plus(a,Times(b,x))),SinhIntegral(Plus(c,Times(d,x))),Power(b,CN1)),x),Negate(Simp(Star(Times(d,Power(b,CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),m),Sinh(Plus(a,Times(b,x))),Sinh(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),CN1)),x)),x)),Negate(Simp(Star(Times(f,m,Power(b,CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Subtract(m,C1)),Sinh(Plus(a,Times(b,x))),SinhIntegral(Plus(c,Times(d,x)))),x)),x))),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0)))),
IIntegrate(7103,Integrate(Times(CoshIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),m),Cosh(Plus(a,Times(b,x))),CoshIntegral(Plus(c,Times(d,x))),Power(b,CN1)),x),Negate(Simp(Star(Times(d,Power(b,CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),m),Cosh(Plus(a,Times(b,x))),Cosh(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),CN1)),x)),x)),Negate(Simp(Star(Times(f,m,Power(b,CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Subtract(m,C1)),Cosh(Plus(a,Times(b,x))),CoshIntegral(Plus(c,Times(d,x)))),x)),x))),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0)))),
IIntegrate(7104,Integrate(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),SinhIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Cosh(Plus(a,Times(b,x))),SinhIntegral(Plus(c,Times(d,x))),Power(Times(f,Plus(m,C1)),CN1)),x),Negate(Simp(Star(Times(b,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Sinh(Plus(a,Times(b,x))),SinhIntegral(Plus(c,Times(d,x)))),x)),x)),Negate(Simp(Star(Times(d,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Cosh(Plus(a,Times(b,x))),Sinh(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),CN1)),x)),x))),And(FreeQ(List(a,b,c,d,e,f),x),ILtQ(m,CN1)))),
IIntegrate(7105,Integrate(Times(CoshIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Sinh(Plus(a,Times(b,x))),CoshIntegral(Plus(c,Times(d,x))),Power(Times(f,Plus(m,C1)),CN1)),x),Negate(Simp(Star(Times(b,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Cosh(Plus(a,Times(b,x))),CoshIntegral(Plus(c,Times(d,x)))),x)),x)),Negate(Simp(Star(Times(d,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Sinh(Plus(a,Times(b,x))),Cosh(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),CN1)),x)),x))),And(FreeQ(List(a,b,c,d,e,f),x),ILtQ(m,CN1)))),
IIntegrate(7106,Integrate(SinhIntegral(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(x,SinhIntegral(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),Simp(Star(Times(b,d,n),Integrate(Times(Sinh(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))),CN1)),x)),x)),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(7107,Integrate(CoshIntegral(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(x,CoshIntegral(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),Simp(Star(Times(b,d,n),Integrate(Times(Cosh(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))),CN1)),x)),x)),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(7108,Integrate(Times(Power(x_,CN1),$(F_,Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT))),x_Symbol),
    Condition(Simp(Star(Power(n,CN1),Subst(F(Times(d,Plus(a,Times(b,x)))),x,Log(Times(c,Power(x,n))))),x),And(FreeQ(List(a,b,c,d,n),x),MemberQ(list(SinhIntegral,CoshIntegral),x)))),
IIntegrate(7109,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),SinhIntegral(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(e,x),Plus(m,C1)),SinhIntegral(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,d,n,Power(Plus(m,C1),CN1)),Integrate(Times(Power(Times(e,x),m),Sinh(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,m,n),x),NeQ(m,CN1)))),
IIntegrate(7110,Integrate(Times(CoshIntegral(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(e,x),Plus(m,C1)),CoshIntegral(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,d,n,Power(Plus(m,C1),CN1)),Integrate(Times(Power(Times(e,x),m),Cosh(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,m,n),x),NeQ(m,CN1)))),
IIntegrate(7111,Integrate(Gamma(n_,Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),Gamma(n,Plus(a,Times(b,x))),Power(b,CN1)),x),Simp(Times(Gamma(Plus(n,C1),Plus(a,Times(b,x))),Power(b,CN1)),x)),FreeQ(list(a,b,n),x))),
IIntegrate(7112,Integrate(Times(Gamma(C0,Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Simp(Times(b,x,HypergeometricPFQ(list(C1,C1,C1),list(C2,C2,C2),Times(CN1,b,x))),x),Negate(Simp(Times(EulerGamma,Log(x)),x)),Negate(Simp(Times(C1D2,Sqr(Log(Times(b,x)))),x))),FreeQ(b,x))),
IIntegrate(7113,Integrate(Times(Gamma(n_,Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Negate(Simp(Gamma(Subtract(n,C1),Times(b,x)),x)),Simp(Star(Subtract(n,C1),Integrate(Times(Gamma(Subtract(n,C1),Times(b,x)),Power(x,CN1)),x)),x)),And(FreeQ(b,x),IGtQ(n,C1)))),
IIntegrate(7114,Integrate(Times(Gamma(n_,Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Simp(Times(Gamma(n,Times(b,x)),Power(n,CN1)),x),Simp(Star(Power(n,CN1),Integrate(Times(Gamma(Plus(n,C1),Times(b,x)),Power(x,CN1)),x)),x)),And(FreeQ(b,x),ILtQ(n,C0)))),
IIntegrate(7115,Integrate(Times(Gamma(n_,Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Gamma(n),Log(x)),x),Simp(Times(Power(Times(b,x),n),Power(n,CN2),HypergeometricPFQ(list(n,n),list(Plus(C1,n),Plus(C1,n)),Times(CN1,b,x))),x)),And(FreeQ(list(b,n),x),Not(IntegerQ(n))))),
IIntegrate(7116,Integrate(Times(Gamma(n_,Times(b_DEFAULT,x_)),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),Plus(m,C1)),Gamma(n,Times(b,x)),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Times(Power(Times(d,x),m),Gamma(Plus(m,n,C1),Times(b,x)),Power(Times(b,Plus(m,C1),Power(Times(b,x),m)),CN1)),x)),And(FreeQ(List(b,d,m,n),x),NeQ(m,CN1)))),
IIntegrate(7117,Integrate(Times(Gamma(n_,Plus(a_,Times(b_DEFAULT,x_))),Power(Plus(c_,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(b,CN1),Subst(Integrate(Times(Power(Times(d,x,Power(b,CN1)),m),Gamma(n,x)),x),x,Plus(a,Times(b,x)))),x),And(FreeQ(List(a,b,c,d,m,n),x),EqQ(Subtract(Times(b,c),Times(a,d)),C0)))),
IIntegrate(7118,Integrate(Times(Gamma(n_,Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Plus(Integrate(Times(Power(Plus(a,Times(b,x)),Subtract(n,C1)),Power(Times(Plus(c,Times(d,x)),Exp(Plus(a,Times(b,x)))),CN1)),x),Simp(Star(Subtract(n,C1),Integrate(Times(Gamma(Subtract(n,C1),Plus(a,Times(b,x))),Power(Plus(c,Times(d,x)),CN1)),x)),x)),And(FreeQ(List(a,b,c,d),x),IGtQ(n,C1)))),
IIntegrate(7119,Integrate(Times(Gamma(n_,Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Block(list(Set(False,True)),Plus(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Gamma(n,Plus(a,Times(b,x))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Power(Plus(a,Times(b,x)),Subtract(n,C1)),Power(Exp(Plus(a,Times(b,x))),CN1)),x)),x))),And(FreeQ(List(a,b,c,d,m,n),x),Or(IGtQ(m,C0),IGtQ(n,C0),IntegersQ(m,n)),NeQ(m,CN1)))),
IIntegrate(7120,Integrate(Times(Gamma(n_,Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(c,Times(d,x)),m),Gamma(n,Plus(a,Times(b,x)))),x),FreeQ(List(a,b,c,d,m,n),x)))
  );
}
