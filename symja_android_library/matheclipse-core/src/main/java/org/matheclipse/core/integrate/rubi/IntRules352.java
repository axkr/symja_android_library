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
class IntRules352 { 
  public static IAST RULES = List( 
IIntegrate(7041,Integrate(Times(ExpIntegralE(C1,Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Simp(Times(b,x,HypergeometricPFQ(list(C1,C1,C1),list(C2,C2,C2),Times(CN1,b,x))),x),Negate(Simp(Times(EulerGamma,Log(x)),x)),Negate(Simp(Times(C1D2,Sqr(Log(Times(b,x)))),x))),FreeQ(b,x))),
IIntegrate(7042,Integrate(Times(ExpIntegralE(n_,Times(b_DEFAULT,x_)),Power(x_,m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),ExpIntegralE(n,Times(b,x)),Power(Plus(m,C1),CN1)),x),Simp(Dist(Times(b,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C1)),ExpIntegralE(Plus(n,CN1),Times(b,x))),x),x),x)),And(FreeQ(b,x),EqQ(Plus(m,n),C0),ILtQ(m,CN1)))),
IIntegrate(7043,Integrate(Times(ExpIntegralE(n_,Times(b_DEFAULT,x_)),Power(Times(d_DEFAULT,x_),m_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),m),Gamma(Plus(m,C1)),Log(x),Power(Times(b,Power(Times(b,x),m)),CN1)),x),Simp(Times(Power(Times(d,x),Plus(m,C1)),HypergeometricPFQ(list(Plus(m,C1),Plus(m,C1)),list(Plus(m,C2),Plus(m,C2)),Times(CN1,b,x)),Power(Times(d,Sqr(Plus(m,C1))),CN1)),x)),And(FreeQ(List(b,d,m,n),x),EqQ(Plus(m,n),C0),Not(IntegerQ(m))))),
IIntegrate(7044,Integrate(Times(ExpIntegralE(n_,Times(b_DEFAULT,x_)),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),Plus(m,C1)),ExpIntegralE(n,Times(b,x)),Power(Times(d,Plus(m,n)),CN1)),x),Simp(Times(Power(Times(d,x),Plus(m,C1)),ExpIntegralE(Negate(m),Times(b,x)),Power(Times(d,Plus(m,n)),CN1)),x)),And(FreeQ(List(b,d,m,n),x),NeQ(Plus(m,n),C0)))),
IIntegrate(7045,Integrate(Times(ExpIntegralE(n_,Plus(a_,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(Plus(c,Times(d,x)),m),ExpIntegralE(Plus(n,C1),Plus(a,Times(b,x))),Power(b,CN1)),x),Simp(Dist(Times(d,m,Power(b,CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,CN1)),ExpIntegralE(Plus(n,C1),Plus(a,Times(b,x)))),x),x),x)),And(FreeQ(List(a,b,c,d,m,n),x),Or(IGtQ(m,C0),ILtQ(n,C0),And(GtQ(m,C0),LtQ(n,CN1)))))),
IIntegrate(7046,Integrate(Times(ExpIntegralE(n_,Plus(a_,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),ExpIntegralE(n,Plus(a,Times(b,x))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),ExpIntegralE(Plus(n,CN1),Plus(a,Times(b,x)))),x),x),x)),And(FreeQ(List(a,b,c,d,m,n),x),Or(IGtQ(n,C0),And(LtQ(m,CN1),GtQ(n,C0))),NeQ(m,CN1)))),
IIntegrate(7047,Integrate(Times(ExpIntegralE(n_,Plus(a_,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(c,Times(d,x)),m),ExpIntegralE(n,Plus(a,Times(b,x)))),x),FreeQ(List(a,b,c,d,m,n),x))),
IIntegrate(7048,Integrate(ExpIntegralEi(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),ExpIntegralEi(Plus(a,Times(b,x))),Power(b,CN1)),x),Simp(Times(Exp(Plus(a,Times(b,x))),Power(b,CN1)),x)),FreeQ(list(a,b),x))),
IIntegrate(7049,Integrate(Times(ExpIntegralEi(Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Log(x),Plus(ExpIntegralEi(Times(b,x)),ExpIntegralE(C1,Times(CN1,b,x)))),x),Integrate(Times(ExpIntegralE(C1,Times(CN1,b,x)),Power(x,CN1)),x)),FreeQ(b,x))),
IIntegrate(7050,Integrate(Times(ExpIntegralEi(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Unintegrable(Times(ExpIntegralEi(Plus(a,Times(b,x))),Power(Plus(c,Times(d,x)),CN1)),x),FreeQ(List(a,b,c,d),x))),
IIntegrate(7051,Integrate(Times(ExpIntegralEi(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),ExpIntegralEi(Plus(a,Times(b,x))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Exp(Plus(a,Times(b,x))),Power(Plus(a,Times(b,x)),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1)))),
IIntegrate(7052,Integrate(Sqr(ExpIntegralEi(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),Sqr(ExpIntegralEi(Plus(a,Times(b,x)))),Power(b,CN1)),x),Simp(Dist(C2,Integrate(Times(Exp(Plus(a,Times(b,x))),ExpIntegralEi(Plus(a,Times(b,x)))),x),x),x)),FreeQ(list(a,b),x))),
IIntegrate(7053,Integrate(Times(Sqr(ExpIntegralEi(Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Sqr(ExpIntegralEi(Times(b,x))),Power(Plus(m,C1),CN1)),x),Simp(Dist(Times(C2,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,m),Exp(Times(b,x)),ExpIntegralEi(Times(b,x))),x),x),x)),And(FreeQ(b,x),IGtQ(m,C0)))),
IIntegrate(7054,Integrate(Times(Sqr(ExpIntegralEi(Plus(a_,Times(b_DEFAULT,x_)))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Sqr(ExpIntegralEi(Plus(a,Times(b,x)))),Power(Plus(m,C1),CN1)),x),Simp(Times(a,Power(x,m),Sqr(ExpIntegralEi(Plus(a,Times(b,x)))),Power(Times(b,Plus(m,C1)),CN1)),x),Negate(Simp(Dist(Times(C2,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,m),Exp(Plus(a,Times(b,x))),ExpIntegralEi(Plus(a,Times(b,x)))),x),x),x)),Negate(Simp(Dist(Times(a,m,Power(Times(b,Plus(m,C1)),CN1)),Integrate(Times(Power(x,Plus(m,CN1)),Sqr(ExpIntegralEi(Plus(a,Times(b,x))))),x),x),x))),And(FreeQ(list(a,b),x),IGtQ(m,C0)))),
IIntegrate(7055,Integrate(Times(Exp(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),ExpIntegralEi(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Exp(Plus(a,Times(b,x))),ExpIntegralEi(Plus(c,Times(d,x))),Power(b,CN1)),x),Simp(Dist(Times(d,Power(b,CN1)),Integrate(Times(Exp(Plus(a,c,Times(Plus(b,d),x))),Power(Plus(c,Times(d,x)),CN1)),x),x),x)),FreeQ(List(a,b,c,d),x))),
IIntegrate(7056,Integrate(Times(Exp(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),ExpIntegralEi(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,m),Exp(Plus(a,Times(b,x))),ExpIntegralEi(Plus(c,Times(d,x))),Power(b,CN1)),x),Negate(Simp(Dist(Times(d,Power(b,CN1)),Integrate(Times(Power(x,m),Exp(Plus(a,c,Times(Plus(b,d),x))),Power(Plus(c,Times(d,x)),CN1)),x),x),x)),Negate(Simp(Dist(Times(m,Power(b,CN1)),Integrate(Times(Power(x,Plus(m,CN1)),Exp(Plus(a,Times(b,x))),ExpIntegralEi(Plus(c,Times(d,x)))),x),x),x))),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0)))),
IIntegrate(7057,Integrate(Times(Exp(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),ExpIntegralEi(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(x_,m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Exp(Plus(a,Times(b,x))),ExpIntegralEi(Plus(c,Times(d,x))),Power(Plus(m,C1),CN1)),x),Negate(Simp(Dist(Times(b,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Exp(Plus(a,Times(b,x))),ExpIntegralEi(Plus(c,Times(d,x)))),x),x),x)),Negate(Simp(Dist(Times(d,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Exp(Plus(a,c,Times(Plus(b,d),x))),Power(Plus(c,Times(d,x)),CN1)),x),x),x))),And(FreeQ(List(a,b,c,d),x),ILtQ(m,CN1)))),
IIntegrate(7058,Integrate(ExpIntegralEi(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(x,ExpIntegralEi(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),Simp(Dist(Times(b,n,Exp(Times(a,d))),Integrate(Times(Power(Times(c,Power(x,n)),Times(b,d)),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),CN1)),x),x),x)),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(7059,Integrate(Times(ExpIntegralEi(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),Power(x_,CN1)),x_Symbol),
    Condition(Simp(Dist(Power(n,CN1),Subst(ExpIntegralEi(Times(d,Plus(a,Times(b,x)))),x,Log(Times(c,Power(x,n)))),x),x),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(7060,Integrate(Times(ExpIntegralEi(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(e,x),Plus(m,C1)),ExpIntegralEi(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,n,Exp(Times(a,d)),Power(Times(c,Power(x,n)),Times(b,d)),Power(Times(Plus(m,C1),Power(Times(e,x),Times(b,d,n))),CN1)),Integrate(Times(Power(Times(e,x),Plus(m,Times(b,d,n))),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,m,n),x),NeQ(m,CN1))))
  );
}
