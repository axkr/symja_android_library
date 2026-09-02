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
class IntRules356 { 
  public static IAST RULES = List( 
IIntegrate(7121,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),SinhIntegral(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(e,x),Plus(m,C1)),SinhIntegral(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,d,n,Power(Plus(m,C1),CN1)),Integrate(Times(Power(Times(e,x),m),Sinh(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,m,n),x),NeQ(m,CN1)))),
IIntegrate(7122,Integrate(Times(CoshIntegral(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(e,x),Plus(m,C1)),CoshIntegral(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,d,n,Power(Plus(m,C1),CN1)),Integrate(Times(Power(Times(e,x),m),Cosh(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,m,n),x),NeQ(m,CN1)))),
IIntegrate(7123,Integrate(Gamma(n_,Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),Gamma(n,Plus(a,Times(b,x))),Power(b,CN1)),x),Simp(Times(Gamma(Plus(n,C1),Plus(a,Times(b,x))),Power(b,CN1)),x)),FreeQ(list(a,b,n),x))),
IIntegrate(7124,Integrate(Times(Gamma(C0,Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Simp(Times(b,x,HypergeometricPFQ(list(C1,C1,C1),list(C2,C2,C2),Times(CN1,b,x))),x),Negate(Simp(Times(EulerGamma,Log(x)),x)),Negate(Simp(Times(C1D2,Sqr(Log(Times(b,x)))),x))),FreeQ(b,x))),
IIntegrate(7125,Integrate(Times(Gamma(n_,Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Negate(Simp(Gamma(Plus(n,CN1),Times(b,x)),x)),Simp(Dist(Plus(n,CN1),Integrate(Times(Gamma(Plus(n,CN1),Times(b,x)),Power(x,CN1)),x),x),x)),And(FreeQ(b,x),IGtQ(n,C1)))),
IIntegrate(7126,Integrate(Times(Gamma(n_,Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Simp(Times(Gamma(n,Times(b,x)),Power(n,CN1)),x),Simp(Dist(Power(n,CN1),Integrate(Times(Gamma(Plus(n,C1),Times(b,x)),Power(x,CN1)),x),x),x)),And(FreeQ(b,x),ILtQ(n,C0)))),
IIntegrate(7127,Integrate(Times(Gamma(n_,Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Gamma(n),Log(x)),x),Simp(Times(Power(Times(b,x),n),Power(n,CN2),HypergeometricPFQ(list(n,n),list(Plus(C1,n),Plus(C1,n)),Times(CN1,b,x))),x)),And(FreeQ(list(b,n),x),Not(IntegerQ(n))))),
IIntegrate(7128,Integrate(Times(Gamma(n_,Times(b_DEFAULT,x_)),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),Plus(m,C1)),Gamma(n,Times(b,x)),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Times(Power(Times(d,x),m),Gamma(Plus(m,n,C1),Times(b,x)),Power(Times(b,Plus(m,C1),Power(Times(b,x),m)),CN1)),x)),And(FreeQ(List(b,d,m,n),x),NeQ(m,CN1)))),
IIntegrate(7129,Integrate(Times(Gamma(n_,Plus(a_,Times(b_DEFAULT,x_))),Power(Plus(c_,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(b,CN1),Subst(Integrate(Times(Power(Times(d,x,Power(b,CN1)),m),Gamma(n,x)),x),x,Plus(a,Times(b,x))),x),x),And(FreeQ(List(a,b,c,d,m,n),x),EqQ(Subtract(Times(b,c),Times(a,d)),C0)))),
IIntegrate(7130,Integrate(Times(Gamma(n_,Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Plus(Integrate(Times(Power(Plus(a,Times(b,x)),Plus(n,CN1)),Power(Times(Plus(c,Times(d,x)),Exp(Plus(a,Times(b,x)))),CN1)),x),Simp(Dist(Plus(n,CN1),Integrate(Times(Gamma(Plus(n,CN1),Plus(a,Times(b,x))),Power(Plus(c,Times(d,x)),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d),x),IGtQ(n,C1)))),
IIntegrate(7131,Integrate(Times(Gamma(n_,Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Block(list(Set(False,True)),Plus(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Gamma(n,Plus(a,Times(b,x))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Power(Plus(a,Times(b,x)),Plus(n,CN1)),Power(Exp(Plus(a,Times(b,x))),CN1)),x),x),x))),And(FreeQ(List(a,b,c,d,m,n),x),Or(IGtQ(m,C0),IGtQ(n,C0),IntegersQ(m,n)),NeQ(m,CN1)))),
IIntegrate(7132,Integrate(Times(Gamma(n_,Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(c,Times(d,x)),m),Gamma(n,Plus(a,Times(b,x)))),x),FreeQ(List(a,b,c,d,m,n),x))),
IIntegrate(7133,Integrate(LogGamma(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Simp(Times(PolyGamma(CN2,Plus(a,Times(b,x))),Power(b,CN1)),x),FreeQ(list(a,b),x))),
IIntegrate(7134,Integrate(Times(LogGamma(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),m),PolyGamma(CN2,Plus(a,Times(b,x))),Power(b,CN1)),x),Simp(Dist(Times(d,m,Power(b,CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,CN1)),PolyGamma(CN2,Plus(a,Times(b,x)))),x),x),x)),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0)))),
IIntegrate(7135,Integrate(Times(LogGamma(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(c,Times(d,x)),m),LogGamma(Plus(a,Times(b,x)))),x),FreeQ(List(a,b,c,d,m),x))),
IIntegrate(7136,Integrate(PolyGamma(n_,Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Simp(Times(PolyGamma(Plus(n,CN1),Plus(a,Times(b,x))),Power(b,CN1)),x),FreeQ(list(a,b,n),x))),
IIntegrate(7137,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),PolyGamma(n_,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),m),PolyGamma(Plus(n,CN1),Plus(a,Times(b,x))),Power(b,CN1)),x),Simp(Dist(Times(d,m,Power(b,CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,CN1)),PolyGamma(Plus(n,CN1),Plus(a,Times(b,x)))),x),x),x)),And(FreeQ(List(a,b,c,d,n),x),GtQ(m,C0)))),
IIntegrate(7138,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),PolyGamma(n_,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),PolyGamma(n,Plus(a,Times(b,x))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),PolyGamma(Plus(n,C1),Plus(a,Times(b,x)))),x),x),x)),And(FreeQ(List(a,b,c,d,n),x),LtQ(m,CN1)))),
IIntegrate(7139,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),PolyGamma(n_,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(c,Times(d,x)),m),PolyGamma(n,Plus(a,Times(b,x)))),x),FreeQ(List(a,b,c,d,m,n),x))),
IIntegrate(7140,Integrate(Times(Power(Gamma(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),PolyGamma(C0,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Simp(Times(Power(Gamma(Plus(a,Times(b,x))),n),Power(Times(b,n),CN1)),x),FreeQ(list(a,b,n),x)))
  );
}
