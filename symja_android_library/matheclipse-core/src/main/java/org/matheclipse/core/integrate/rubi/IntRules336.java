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
class IntRules336 { 
  public static IAST RULES = List( 
IIntegrate(6721,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN2))),p_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(x,Times(C2,p)),Power(Plus(c,Times(d,Power(x,CN2))),p),Power(Power(Subtract(C1,Times(Sqr(a),Sqr(x))),p),CN1)),Integrate(Times(u,Power(Plus(C1,Times(a,x)),n),Power(Times(Power(x,Times(C2,p)),Power(Subtract(C1,Times(Sqr(a),Sqr(x))),Subtract(Times(C1D2,n),p))),CN1)),x),x),x),And(FreeQ(List(a,c,d,p),x),EqQ(Plus(c,Times(Sqr(a),d)),C0),Not(IntegerQ(p)),IntegerQ(Times(C1D2,n)),Not(GtQ(c,C0))))),
IIntegrate(6722,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN2))),p_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(x,Times(C2,p)),Power(Plus(c,Times(d,Power(x,CN2))),p),Power(Power(Subtract(C1,Times(Sqr(a),Sqr(x))),p),CN1)),Integrate(Times(u,Power(Power(x,Times(C2,p)),CN1),Power(Subtract(C1,Times(Sqr(a),Sqr(x))),p),Exp(Times(n,ArcTanh(Times(a,x))))),x),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(c,Times(Sqr(a),d)),C0),Not(IntegerQ(p)),Not(IntegerQ(Times(C1D2,n)))))),
IIntegrate(6723,Integrate(Exp(Times(ArcTanh(Times(c_DEFAULT,Plus(a_,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Plus(C1,Times(a,c),Times(b,c,x)),Times(C1D2,n)),Power(Power(Subtract(Subtract(C1,Times(a,c)),Times(b,c,x)),Times(C1D2,n)),CN1)),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(6724,Integrate(Times(Exp(Times(ArcTanh(Times(c_DEFAULT,Plus(a_,Times(b_DEFAULT,x_)))),n_)),Power(x_,m_)),x_Symbol),
    Condition(Simp(Dist(Times(C4,Power(Times(n,Power(b,Plus(m,C1)),Power(c,Plus(m,C1))),CN1)),Subst(Integrate(Times(Power(x,Times(C2,Power(n,CN1))),Power(Plus(CN1,Times(CN1,a,c),Times(Subtract(C1,Times(a,c)),Power(x,Times(C2,Power(n,CN1))))),m),Power(Power(Plus(C1,Power(x,Times(C2,Power(n,CN1)))),Plus(m,C2)),CN1)),x),x,Times(Power(Plus(C1,Times(c,Plus(a,Times(b,x)))),Times(C1D2,n)),Power(Power(Subtract(C1,Times(c,Plus(a,Times(b,x)))),Times(C1D2,n)),CN1))),x),x),And(FreeQ(list(a,b,c),x),ILtQ(m,C0),LtQ(CN1,n,C1)))),
IIntegrate(6725,Integrate(Times(Exp(Times(ArcTanh(Times(c_DEFAULT,Plus(a_,Times(b_DEFAULT,x_)))),n_DEFAULT)),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Plus(d,Times(e,x)),m),Power(Plus(C1,Times(a,c),Times(b,c,x)),Times(C1D2,n)),Power(Power(Subtract(Subtract(C1,Times(a,c)),Times(b,c,x)),Times(C1D2,n)),CN1)),x),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(6726,Integrate(Times(Exp(Times(ArcTanh(Plus(a_,Times(b_DEFAULT,x_))),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_),Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Times(c,Power(Subtract(C1,Sqr(a)),CN1)),p),Integrate(Times(u,Power(Subtract(Subtract(C1,a),Times(b,x)),Subtract(p,Times(C1D2,n))),Power(Plus(C1,a,Times(b,x)),Plus(p,Times(C1D2,n)))),x),x),x),And(FreeQ(List(a,b,c,d,e,n,p),x),EqQ(Subtract(Times(b,d),Times(C2,a,e)),C0),EqQ(Plus(Times(Sqr(b),c),Times(e,Subtract(C1,Sqr(a)))),C0),Or(IntegerQ(p),GtQ(Times(c,Power(Subtract(C1,Sqr(a)),CN1)),C0))))),
IIntegrate(6727,Integrate(Times(Exp(Times(ArcTanh(Plus(a_,Times(b_DEFAULT,x_))),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_),Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Plus(c,Times(d,x),Times(e,Sqr(x))),p),Power(Power(Subtract(Subtract(Subtract(C1,Sqr(a)),Times(C2,a,b,x)),Times(Sqr(b),Sqr(x))),p),CN1)),Integrate(Times(u,Power(Subtract(Subtract(Subtract(C1,Sqr(a)),Times(C2,a,b,x)),Times(Sqr(b),Sqr(x))),p),Exp(Times(n,ArcTanh(Times(a,x))))),x),x),x),And(FreeQ(List(a,b,c,d,e,n,p),x),EqQ(Subtract(Times(b,d),Times(C2,a,e)),C0),EqQ(Plus(Times(Sqr(b),c),Times(e,Subtract(C1,Sqr(a)))),C0),Not(Or(IntegerQ(p),GtQ(Times(c,Power(Subtract(C1,Sqr(a)),CN1)),C0)))))),
IIntegrate(6728,Integrate(Times(Exp(Times(ArcTanh(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),CN1))),n_DEFAULT)),u_DEFAULT),x_Symbol),
    Condition(Integrate(Times(u,Exp(Times(n,ArcCoth(Plus(Times(a,Power(c,CN1)),Times(b,x,Power(c,CN1))))))),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(6729,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_)),u_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Power(-1,Times(C1D2,n)),Integrate(Times(u,Exp(Times(n,ArcTanh(Times(a,x))))),x),x),x),And(FreeQ(a,x),IntegerQ(Times(C1D2,n))))),
IIntegrate(6730,Integrate(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Plus(C1,Times(x,Power(a,CN1))),Times(C1D2,Plus(n,C1))),Power(Times(Sqr(x),Power(Subtract(C1,Times(x,Power(a,CN1))),Times(C1D2,Plus(n,CN1))),Sqrt(Subtract(C1,Times(Sqr(x),Power(a,CN2))))),CN1)),x),x,Power(x,CN1))),And(FreeQ(a,x),IntegerQ(Times(C1D2,Plus(n,CN1)))))),
IIntegrate(6731,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Plus(C1,Times(x,Power(a,CN1))),Times(C1D2,Plus(n,C1))),Power(Times(Power(x,Plus(m,C2)),Power(Subtract(C1,Times(x,Power(a,CN1))),Times(C1D2,Plus(n,CN1))),Sqrt(Subtract(C1,Times(Sqr(x),Power(a,CN2))))),CN1)),x),x,Power(x,CN1))),And(FreeQ(a,x),IntegerQ(Times(C1D2,Plus(n,CN1))),IntegerQ(m)))),
IIntegrate(6732,Integrate(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Plus(C1,Times(x,Power(a,CN1))),Times(C1D2,n)),Power(Times(Sqr(x),Power(Subtract(C1,Times(x,Power(a,CN1))),Times(C1D2,n))),CN1)),x),x,Power(x,CN1))),And(FreeQ(list(a,n),x),Not(IntegerQ(n))))),
IIntegrate(6733,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Plus(C1,Times(x,Power(a,CN1))),Times(C1D2,n)),Power(Times(Power(x,Plus(m,C2)),Power(Subtract(C1,Times(x,Power(a,CN1))),Times(C1D2,n))),CN1)),x),x,Power(x,CN1))),And(FreeQ(list(a,n),x),Not(IntegerQ(n)),IntegerQ(m)))),
IIntegrate(6734,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Times(c_DEFAULT,x_),m_)),x_Symbol),
    Condition(Simp(Dist(Times(CN1,Power(Times(c,x),m),Power(Power(x,CN1),m)),Subst(Integrate(Times(Power(Plus(C1,Times(x,Power(a,CN1))),Times(C1D2,Plus(n,C1))),Power(Times(Power(x,Plus(m,C2)),Power(Subtract(C1,Times(x,Power(a,CN1))),Times(C1D2,Plus(n,CN1))),Sqrt(Subtract(C1,Times(Sqr(x),Power(a,CN2))))),CN1)),x),x,Power(x,CN1)),x),x),And(FreeQ(list(a,c,m),x),IntegerQ(Times(C1D2,Plus(n,CN1))),Not(IntegerQ(m))))),
IIntegrate(6735,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_)),Power(Times(c_DEFAULT,x_),m_)),x_Symbol),
    Condition(Simp(Dist(Times(CN1,Power(Times(c,x),m),Power(Power(x,CN1),m)),Subst(Integrate(Times(Power(Plus(C1,Times(x,Power(a,CN1))),Times(C1D2,n)),Power(Times(Power(x,Plus(m,C2)),Power(Subtract(C1,Times(x,Power(a,CN1))),Times(C1D2,n))),CN1)),x),x,Power(x,CN1)),x),x),And(FreeQ(List(a,c,m,n),x),Not(IntegerQ(n)),Not(IntegerQ(m))))),
IIntegrate(6736,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,x_)),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Negate(Power(d,n)),Subst(Integrate(Times(Power(Plus(d,Times(c,x)),Subtract(p,n)),Power(Subtract(C1,Times(Sqr(x),Power(a,CN2))),Times(C1D2,n)),Power(Power(x,Plus(p,C2)),CN1)),x),x,Power(x,CN1)),x),x),And(FreeQ(list(a,c,d),x),EqQ(Plus(Times(a,c),d),C0),IntegerQ(p),IntegerQ(n)))),
IIntegrate(6737,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,x_)),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Negate(Power(d,p)),Subst(Integrate(Times(Power(Plus(C1,Times(c,x,Power(d,CN1))),p),Power(Plus(C1,Times(x,Power(a,CN1))),Times(C1D2,n)),Power(Power(x,Plus(p,C2)),CN1),Power(Power(Subtract(C1,Times(x,Power(a,CN1))),Times(C1D2,n)),CN1)),x),x,Power(x,CN1)),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Subtract(Times(Sqr(a),Sqr(c)),Sqr(d)),C0),IntegerQ(p)))),
IIntegrate(6738,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,x_)),p_DEFAULT)),x_Symbol),
    Condition(Simp(Times(Plus(C1,Times(a,x)),Power(Plus(c,Times(d,x)),p),Exp(Times(n,ArcCoth(Times(a,x)))),Power(Times(a,Plus(p,C1)),CN1)),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(Times(a,c),d),C0),Not(IntegerQ(p)),EqQ(p,Times(C1D2,n))))),
IIntegrate(6739,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,x_)),p_)),x_Symbol),
    Condition(Simp(Dist(Times(CN1,Power(Power(x,CN1),p),Power(Plus(c,Times(d,x)),p),Power(Power(Plus(C1,Times(c,Power(Times(d,x),CN1))),p),CN1)),Subst(Integrate(Times(Power(Plus(C1,Times(c,x,Power(d,CN1))),p),Power(Plus(C1,Times(x,Power(a,CN1))),Times(C1D2,n)),Power(Power(x,Plus(p,C2)),CN1),Power(Power(Subtract(C1,Times(x,Power(a,CN1))),Times(C1D2,n)),CN1)),x),x,Power(x,CN1)),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Subtract(Times(Sqr(a),Sqr(c)),Sqr(d)),C0),Not(IntegerQ(p))))),
IIntegrate(6740,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(x_,m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,x_)),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Negate(Power(d,n)),Subst(Integrate(Times(Power(Plus(d,Times(c,x)),Subtract(p,n)),Power(Subtract(C1,Times(Sqr(x),Power(a,CN2))),Times(C1D2,n)),Power(Power(x,Plus(m,p,C2)),CN1)),x),x,Power(x,CN1)),x),x),And(FreeQ(list(a,c,d),x),EqQ(Plus(Times(a,c),d),C0),IntegerQ(p),IntegerQ(n),IntegerQ(m))))
  );
}
