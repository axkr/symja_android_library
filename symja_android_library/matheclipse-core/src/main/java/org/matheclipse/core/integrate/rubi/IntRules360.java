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
class IntRules360 { 
  public static IAST RULES = List( 
IIntegrate(7201,Integrate(Times(Power(ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT))),p_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT))))),CN1)),x_Symbol),
    Condition(Simp(Times(Power(a,p),ExpIntegralEi(Times(CN1,p,ProductLog(Times(a,Power(x,n))))),Power(Times(d,n),CN1)),x),And(FreeQ(list(a,d),x),IntegerQ(p),EqQ(Times(n,p),CN1)))),
IIntegrate(7202,Integrate(Times(Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT)))),p_),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT))))),CN1)),x_Symbol),
    Condition(Simp(Times(Rt(Times(Pi,c,n),C2),Power(Times(d,n,Power(a,Power(n,CN1)),Power(c,Power(n,CN1))),CN1),Erfi(Times(Sqrt(Times(c,ProductLog(Times(a,Power(x,n))))),Power(Rt(Times(c,n),C2),CN1)))),x),And(FreeQ(list(a,c,d),x),IntegerQ(Power(n,CN1)),EqQ(p,Subtract(C1D2,Power(n,CN1))),PosQ(Times(c,n))))),
IIntegrate(7203,Integrate(Times(Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT)))),p_),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT))))),CN1)),x_Symbol),
    Condition(Simp(Times(Rt(Times(CN1,Pi,c,n),C2),Power(Times(d,n,Power(a,Power(n,CN1)),Power(c,Power(n,CN1))),CN1),Erf(Times(Sqrt(Times(c,ProductLog(Times(a,Power(x,n))))),Power(Rt(Times(CN1,c,n),C2),CN1)))),x),And(FreeQ(list(a,c,d),x),IntegerQ(Power(n,CN1)),EqQ(p,Subtract(C1D2,Power(n,CN1))),NegQ(Times(c,n))))),
IIntegrate(7204,Integrate(Times(Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT))))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(c,x,Power(Times(c,ProductLog(Times(a,Power(x,n)))),Plus(p,CN1)),Power(d,CN1)),x),Simp(Dist(Times(c,Plus(Times(n,Plus(p,CN1)),C1)),Integrate(Times(Power(Times(c,ProductLog(Times(a,Power(x,n)))),Plus(p,CN1)),Power(Plus(d,Times(d,ProductLog(Times(a,Power(x,n))))),CN1)),x),x),x)),And(FreeQ(list(a,c,d),x),GtQ(n,C0),GtQ(Plus(Times(n,Plus(p,CN1)),C1),C0)))),
IIntegrate(7205,Integrate(Times(Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT))))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(x,Power(Times(c,ProductLog(Times(a,Power(x,n)))),p),Power(Times(d,Plus(Times(n,p),C1)),CN1)),x),Simp(Dist(Power(Times(c,Plus(Times(n,p),C1)),CN1),Integrate(Times(Power(Times(c,ProductLog(Times(a,Power(x,n)))),Plus(p,C1)),Power(Plus(d,Times(d,ProductLog(Times(a,Power(x,n))))),CN1)),x),x),x)),And(FreeQ(list(a,c,d),x),GtQ(n,C0),LtQ(Plus(Times(n,p),C1),C0)))),
IIntegrate(7206,Integrate(Times(Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_)))),p_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_))))),CN1)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Times(c,ProductLog(Times(a,Power(Power(x,n),CN1)))),p),Power(Times(Sqr(x),Plus(d,Times(d,ProductLog(Times(a,Power(Power(x,n),CN1)))))),CN1)),x),x,Power(x,CN1))),And(FreeQ(List(a,c,d,p),x),ILtQ(n,C0)))),
IIntegrate(7207,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,x_)))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Power(Times(d,Plus(m,C1),ProductLog(Times(a,x))),CN1)),x),Simp(Dist(Times(m,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,m),Power(Times(ProductLog(Times(a,x)),Plus(d,Times(d,ProductLog(Times(a,x))))),CN1)),x),x),x)),And(FreeQ(list(a,d),x),GtQ(m,C0)))),
IIntegrate(7208,Integrate(Times(Power(x_,CN1),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,x_)))),CN1)),x_Symbol),
    Condition(Simp(Times(Log(ProductLog(Times(a,x))),Power(d,CN1)),x),FreeQ(list(a,d),x))),
IIntegrate(7209,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,x_)))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Power(Times(d,Plus(m,C1)),CN1)),x),Integrate(Times(Power(x,m),ProductLog(Times(a,x)),Power(Plus(d,Times(d,ProductLog(Times(a,x)))),CN1)),x)),And(FreeQ(list(a,d),x),LtQ(m,CN1)))),
IIntegrate(7210,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,x_)))),CN1)),x_Symbol),
    Condition(Simp(Times(Power(x,m),Gamma(Plus(m,C1),Times(CN1,Plus(m,C1),ProductLog(Times(a,x)))),Power(Times(a,d,Plus(m,C1),Exp(Times(m,ProductLog(Times(a,x)))),Power(Times(CN1,Plus(m,C1),ProductLog(Times(a,x))),m)),CN1)),x),And(FreeQ(list(a,d,m),x),Not(IntegerQ(m))))),
IIntegrate(7211,Integrate(Times(Power(x_,CN1),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT))))),CN1)),x_Symbol),
    Condition(Simp(Times(Log(ProductLog(Times(a,Power(x,n)))),Power(Times(d,n),CN1)),x),FreeQ(list(a,d,n),x))),
IIntegrate(7212,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_))))),CN1)),x_Symbol),
    Condition(Negate(Subst(Integrate(Power(Times(Power(x,Plus(m,C2)),Plus(d,Times(d,ProductLog(Times(a,Power(Power(x,n),CN1)))))),CN1),x),x,Power(x,CN1))),And(FreeQ(list(a,d),x),IntegerQ(m),ILtQ(n,C0),NeQ(m,CN1)))),
IIntegrate(7213,Integrate(Times(Power(x_,CN1),Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT))))),CN1)),x_Symbol),
    Condition(Simp(Times(Power(Times(c,ProductLog(Times(a,Power(x,n)))),p),Power(Times(d,n,p),CN1)),x),FreeQ(List(a,c,d,n,p),x))),
IIntegrate(7214,Integrate(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT))))),CN1)),x_Symbol),
    Condition(Simp(Times(c,Power(x,Plus(m,C1)),Power(Times(c,ProductLog(Times(a,Power(x,n)))),Plus(p,CN1)),Power(Times(d,Plus(m,C1)),CN1)),x),And(FreeQ(List(a,c,d,m,n,p),x),NeQ(m,CN1),EqQ(Plus(m,Times(n,Plus(p,CN1))),CN1)))),
IIntegrate(7215,Integrate(Times(Power(x_,m_DEFAULT),Power(ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT))),p_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT))))),CN1)),x_Symbol),
    Condition(Simp(Times(Power(a,p),ExpIntegralEi(Times(CN1,p,ProductLog(Times(a,Power(x,n))))),Power(Times(d,n),CN1)),x),And(FreeQ(List(a,d,m,n),x),IntegerQ(p),EqQ(Plus(m,Times(n,p)),CN1)))),
IIntegrate(7216,Integrate(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT)))),p_),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT))))),CN1)),x_Symbol),
    Condition(Simp(Times(Power(a,Plus(p,CN1D2)),Power(c,Plus(p,CN1D2)),Rt(Times(Pi,c,Power(Plus(p,CN1D2),CN1)),C2),Erf(Times(Sqrt(Times(c,ProductLog(Times(a,Power(x,n))))),Power(Rt(Times(c,Power(Plus(p,CN1D2),CN1)),C2),CN1))),Power(Times(d,n),CN1)),x),And(FreeQ(List(a,c,d,m,n),x),NeQ(m,CN1),IntegerQ(Plus(p,CN1D2)),EqQ(Plus(m,Times(n,Plus(p,CN1D2))),CN1),PosQ(Times(c,Power(Plus(p,CN1D2),CN1)))))),
IIntegrate(7217,Integrate(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT)))),p_),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT))))),CN1)),x_Symbol),
    Condition(Simp(Times(Power(a,Plus(p,CN1D2)),Power(c,Plus(p,CN1D2)),Rt(Times(CN1,Pi,c,Power(Plus(p,CN1D2),CN1)),C2),Erfi(Times(Sqrt(Times(c,ProductLog(Times(a,Power(x,n))))),Power(Rt(Times(CN1,c,Power(Plus(p,CN1D2),CN1)),C2),CN1))),Power(Times(d,n),CN1)),x),And(FreeQ(List(a,c,d,m,n),x),NeQ(m,CN1),IntegerQ(Plus(p,CN1D2)),EqQ(Plus(m,Times(n,Plus(p,CN1D2))),CN1),NegQ(Times(c,Power(Plus(p,CN1D2),CN1)))))),
IIntegrate(7218,Integrate(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT))))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(c,Power(x,Plus(m,C1)),Power(Times(c,ProductLog(Times(a,Power(x,n)))),Plus(p,CN1)),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Dist(Times(c,Plus(m,Times(n,Plus(p,CN1)),C1),Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,m),Power(Times(c,ProductLog(Times(a,Power(x,n)))),Plus(p,CN1)),Power(Plus(d,Times(d,ProductLog(Times(a,Power(x,n))))),CN1)),x),x),x)),And(FreeQ(List(a,c,d,m,n,p),x),NeQ(m,CN1),GtQ(Simplify(Plus(p,Times(Plus(m,C1),Power(n,CN1)))),C1)))),
IIntegrate(7219,Integrate(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT))))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Power(Times(c,ProductLog(Times(a,Power(x,n)))),p),Power(Times(d,Plus(m,Times(n,p),C1)),CN1)),x),Simp(Dist(Times(Plus(m,C1),Power(Times(c,Plus(m,Times(n,p),C1)),CN1)),Integrate(Times(Power(x,m),Power(Times(c,ProductLog(Times(a,Power(x,n)))),Plus(p,C1)),Power(Plus(d,Times(d,ProductLog(Times(a,Power(x,n))))),CN1)),x),x),x)),And(FreeQ(List(a,c,d,m,n,p),x),NeQ(m,CN1),LtQ(Simplify(Plus(p,Times(Plus(m,C1),Power(n,CN1)))),C0)))),
IIntegrate(7220,Integrate(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,x_))),p_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,x_)))),CN1)),x_Symbol),
    Condition(Simp(Times(Power(x,m),Gamma(Plus(m,p,C1),Times(CN1,Plus(m,C1),ProductLog(Times(a,x)))),Power(Times(c,ProductLog(Times(a,x))),p),Power(Times(a,d,Plus(m,C1),Exp(Times(m,ProductLog(Times(a,x)))),Power(Times(CN1,Plus(m,C1),ProductLog(Times(a,x))),Plus(m,p))),CN1)),x),And(FreeQ(List(a,c,d,m,p),x),NeQ(m,CN1))))
  );
}
