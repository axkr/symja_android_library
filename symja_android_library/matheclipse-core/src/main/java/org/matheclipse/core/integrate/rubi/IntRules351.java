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
class IntRules351 { 
  public static IAST RULES = List( 
IIntegrate(7021,Integrate(Times(Power(FresnelC(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(e,x),m),Power(FresnelC(Plus(a,Times(b,x))),n),Sin(Plus(c,Times(d,Sqr(x))))),x),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(7022,Integrate(FresnelS(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(x,FresnelS(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),Simp(Star(Times(b,d,n),Integrate(Sin(Times(C1D2,Pi,Sqr(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))))),x)),x)),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(7023,Integrate(FresnelC(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(x,FresnelC(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),Simp(Star(Times(b,d,n),Integrate(Cos(Times(C1D2,Pi,Sqr(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))))),x)),x)),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(7024,Integrate(Times(Power(x_,CN1),$(F_,Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT))),x_Symbol),
    Condition(Simp(Star(Power(n,CN1),Subst(F(Times(d,Plus(a,Times(b,x)))),x,Log(Times(c,Power(x,n))))),x),And(FreeQ(List(a,b,c,d,n),x),MemberQ(list(FresnelS,FresnelC),FSymbol)))),
IIntegrate(7025,Integrate(Times(FresnelS(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(e,x),Plus(m,C1)),FresnelS(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,d,n,Power(Plus(m,C1),CN1)),Integrate(Times(Power(Times(e,x),m),Sin(Times(C1D2,Pi,Sqr(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))))),x)),x)),And(FreeQ(List(a,b,c,d,e,m,n),x),NeQ(m,CN1)))),
IIntegrate(7026,Integrate(Times(FresnelC(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(e,x),Plus(m,C1)),FresnelC(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,d,n,Power(Plus(m,C1),CN1)),Integrate(Times(Power(Times(e,x),m),Cos(Times(C1D2,Pi,Sqr(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))))),x)),x)),And(FreeQ(List(a,b,c,d,e,m,n),x),NeQ(m,CN1)))),
IIntegrate(7027,Integrate(ExpIntegralE(n_,Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Simp(Times(CN1,ExpIntegralE(Plus(n,C1),Plus(a,Times(b,x))),Power(b,CN1)),x),FreeQ(list(a,b,n),x))),
IIntegrate(7028,Integrate(Times(ExpIntegralE(n_,Times(b_DEFAULT,x_)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(x,m),ExpIntegralE(Plus(n,C1),Times(b,x)),Power(b,CN1)),x),Simp(Star(Times(m,Power(b,CN1)),Integrate(Times(Power(x,Subtract(m,C1)),ExpIntegralE(Plus(n,C1),Times(b,x))),x)),x)),And(FreeQ(b,x),EqQ(Plus(m,n),C0),IGtQ(m,C0)))),
IIntegrate(7029,Integrate(Times(ExpIntegralE(C1,Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Simp(Times(b,x,HypergeometricPFQ(list(C1,C1,C1),list(C2,C2,C2),Times(CN1,b,x))),x),Negate(Simp(Times(EulerGamma,Log(x)),x)),Negate(Simp(Times(C1D2,Sqr(Log(Times(b,x)))),x))),FreeQ(b,x))),
IIntegrate(7030,Integrate(Times(ExpIntegralE(n_,Times(b_DEFAULT,x_)),Power(x_,m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),ExpIntegralE(n,Times(b,x)),Power(Plus(m,C1),CN1)),x),Simp(Star(Times(b,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C1)),ExpIntegralE(Subtract(n,C1),Times(b,x))),x)),x)),And(FreeQ(b,x),EqQ(Plus(m,n),C0),ILtQ(m,CN1)))),
IIntegrate(7031,Integrate(Times(ExpIntegralE(n_,Times(b_DEFAULT,x_)),Power(Times(d_DEFAULT,x_),m_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),m),Gamma(Plus(m,C1)),Log(x),Power(Times(b,Power(Times(b,x),m)),CN1)),x),Simp(Times(Power(Times(d,x),Plus(m,C1)),HypergeometricPFQ(list(Plus(m,C1),Plus(m,C1)),list(Plus(m,C2),Plus(m,C2)),Times(CN1,b,x)),Power(Times(d,Sqr(Plus(m,C1))),CN1)),x)),And(FreeQ(List(b,d,m,n),x),EqQ(Plus(m,n),C0),Not(IntegerQ(m))))),
IIntegrate(7032,Integrate(Times(ExpIntegralE(n_,Times(b_DEFAULT,x_)),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),Plus(m,C1)),ExpIntegralE(n,Times(b,x)),Power(Times(d,Plus(m,n)),CN1)),x),Simp(Times(Power(Times(d,x),Plus(m,C1)),ExpIntegralE(Negate(m),Times(b,x)),Power(Times(d,Plus(m,n)),CN1)),x)),And(FreeQ(List(b,d,m,n),x),NeQ(Plus(m,n),C0)))),
IIntegrate(7033,Integrate(Times(ExpIntegralE(n_,Plus(a_,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(Plus(c,Times(d,x)),m),ExpIntegralE(Plus(n,C1),Plus(a,Times(b,x))),Power(b,CN1)),x),Simp(Star(Times(d,m,Power(b,CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,C1)),ExpIntegralE(Plus(n,C1),Plus(a,Times(b,x)))),x)),x)),And(FreeQ(List(a,b,c,d,m,n),x),Or(IGtQ(m,C0),ILtQ(n,C0),And(GtQ(m,C0),LtQ(n,CN1)))))),
IIntegrate(7034,Integrate(Times(ExpIntegralE(n_,Plus(a_,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),ExpIntegralE(n,Plus(a,Times(b,x))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),ExpIntegralE(Subtract(n,C1),Plus(a,Times(b,x)))),x)),x)),And(FreeQ(List(a,b,c,d,m,n),x),Or(IGtQ(n,C0),And(LtQ(m,CN1),GtQ(n,C0))),NeQ(m,CN1)))),
IIntegrate(7035,Integrate(Times(ExpIntegralE(n_,Plus(a_,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(c,Times(d,x)),m),ExpIntegralE(n,Plus(a,Times(b,x)))),x),FreeQ(List(a,b,c,d,m,n),x))),
IIntegrate(7036,Integrate(ExpIntegralEi(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),ExpIntegralEi(Plus(a,Times(b,x))),Power(b,CN1)),x),Simp(Times(Exp(Plus(a,Times(b,x))),Power(b,CN1)),x)),FreeQ(list(a,b),x))),
IIntegrate(7037,Integrate(Times(ExpIntegralEi(Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Log(x),Plus(ExpIntegralEi(Times(b,x)),ExpIntegralE(C1,Times(CN1,b,x)))),x),Integrate(Times(ExpIntegralE(C1,Times(CN1,b,x)),Power(x,CN1)),x)),FreeQ(b,x))),
IIntegrate(7038,Integrate(Times(ExpIntegralEi(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Unintegrable(Times(ExpIntegralEi(Plus(a,Times(b,x))),Power(Plus(c,Times(d,x)),CN1)),x),FreeQ(List(a,b,c,d),x))),
IIntegrate(7039,Integrate(Times(ExpIntegralEi(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),ExpIntegralEi(Plus(a,Times(b,x))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Exp(Plus(a,Times(b,x))),Power(Plus(a,Times(b,x)),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1)))),
IIntegrate(7040,Integrate(Sqr(ExpIntegralEi(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),Sqr(ExpIntegralEi(Plus(a,Times(b,x)))),Power(b,CN1)),x),Simp(Star(C2,Integrate(Times(Exp(Plus(a,Times(b,x))),ExpIntegralEi(Plus(a,Times(b,x)))),x)),x)),FreeQ(list(a,b),x)))
  );
}
