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
class IntRules336 { 
  public static IAST RULES = List( 
IIntegrate(6721,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Plus(C1,Times(x,Power(a,CN1))),Times(C1D2,n)),Power(Times(Power(x,Plus(m,C2)),Power(Subtract(C1,Times(x,Power(a,CN1))),Times(C1D2,n))),CN1)),x),x,Power(x,CN1))),And(FreeQ(list(a,n),x),Not(IntegerQ(n)),IntegerQ(m)))),
IIntegrate(6722,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Times(c_DEFAULT,x_),m_)),x_Symbol),
    Condition(Simp(Star(Times(CN1,Power(Times(c,x),m),Power(Power(x,CN1),m)),Subst(Integrate(Times(Power(Plus(C1,Times(x,Power(a,CN1))),Times(C1D2,Plus(n,C1))),Power(Times(Power(x,Plus(m,C2)),Power(Subtract(C1,Times(x,Power(a,CN1))),Times(C1D2,Subtract(n,C1))),Sqrt(Subtract(C1,Times(Sqr(x),Power(a,CN2))))),CN1)),x),x,Power(x,CN1))),x),And(FreeQ(list(a,c,m),x),IntegerQ(Times(C1D2,Subtract(n,C1))),Not(IntegerQ(m))))),
IIntegrate(6723,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_)),Power(Times(c_DEFAULT,x_),m_)),x_Symbol),
    Condition(Simp(Star(Times(CN1,Power(Times(c,x),m),Power(Power(x,CN1),m)),Subst(Integrate(Times(Power(Plus(C1,Times(x,Power(a,CN1))),Times(C1D2,n)),Power(Times(Power(x,Plus(m,C2)),Power(Subtract(C1,Times(x,Power(a,CN1))),Times(C1D2,n))),CN1)),x),x,Power(x,CN1))),x),And(FreeQ(List(a,c,m,n),x),Not(IntegerQ(n)),Not(IntegerQ(m))))),
IIntegrate(6724,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,x_)),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Negate(Power(d,n)),Subst(Integrate(Times(Power(Plus(d,Times(c,x)),Subtract(p,n)),Power(Subtract(C1,Times(Sqr(x),Power(a,CN2))),Times(C1D2,n)),Power(Power(x,Plus(p,C2)),CN1)),x),x,Power(x,CN1))),x),And(FreeQ(list(a,c,d),x),EqQ(Plus(Times(a,c),d),C0),IntegerQ(p),IntegerQ(n)))),
IIntegrate(6725,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,x_)),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Negate(Power(d,p)),Subst(Integrate(Times(Power(Plus(C1,Times(c,x,Power(d,CN1))),p),Power(Plus(C1,Times(x,Power(a,CN1))),Times(C1D2,n)),Power(Power(x,Plus(p,C2)),CN1),Power(Power(Subtract(C1,Times(x,Power(a,CN1))),Times(C1D2,n)),CN1)),x),x,Power(x,CN1))),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Subtract(Times(Sqr(a),Sqr(c)),Sqr(d)),C0),IntegerQ(p)))),
IIntegrate(6726,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,x_)),p_DEFAULT)),x_Symbol),
    Condition(Simp(Times(Plus(C1,Times(a,x)),Power(Plus(c,Times(d,x)),p),Exp(Times(n,ArcCoth(Times(a,x)))),Power(Times(a,Plus(p,C1)),CN1)),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(Times(a,c),d),C0),Not(IntegerQ(p)),EqQ(p,Times(C1D2,n))))),
IIntegrate(6727,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,x_)),p_)),x_Symbol),
    Condition(Simp(Star(Times(CN1,Power(Power(x,CN1),p),Power(Plus(c,Times(d,x)),p),Power(Power(Plus(C1,Times(c,Power(Times(d,x),CN1))),p),CN1)),Subst(Integrate(Times(Power(Plus(C1,Times(c,x,Power(d,CN1))),p),Power(Plus(C1,Times(x,Power(a,CN1))),Times(C1D2,n)),Power(Power(x,Plus(p,C2)),CN1),Power(Power(Subtract(C1,Times(x,Power(a,CN1))),Times(C1D2,n)),CN1)),x),x,Power(x,CN1))),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Subtract(Times(Sqr(a),Sqr(c)),Sqr(d)),C0),Not(IntegerQ(p))))),
IIntegrate(6728,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(x_,m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,x_)),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Negate(Power(d,n)),Subst(Integrate(Times(Power(Plus(d,Times(c,x)),Subtract(p,n)),Power(Subtract(C1,Times(Sqr(x),Power(a,CN2))),Times(C1D2,n)),Power(Power(x,Plus(m,p,C2)),CN1)),x),x,Power(x,CN1))),x),And(FreeQ(list(a,c,d),x),EqQ(Plus(Times(a,c),d),C0),IntegerQ(p),IntegerQ(n),IntegerQ(m)))),
IIntegrate(6729,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,x_)),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(CN1,Power(d,p),Power(Times(e,x),m),Power(Power(x,CN1),m)),Subst(Integrate(Times(Power(Plus(C1,Times(c,x,Power(d,CN1))),p),Power(Plus(C1,Times(x,Power(a,CN1))),Times(C1D2,n)),Power(Power(x,Plus(m,p,C2)),CN1),Power(Power(Subtract(C1,Times(x,Power(a,CN1))),Times(C1D2,n)),CN1)),x),x,Power(x,CN1))),x),And(FreeQ(List(a,c,d,e,m,n),x),EqQ(Subtract(Times(Sqr(a),Sqr(c)),Sqr(d)),C0),IntegerQ(p)))),
IIntegrate(6730,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,x_)),p_)),x_Symbol),
    Condition(Simp(Star(Times(CN1,Power(Times(e,x),m),Power(Power(x,CN1),Plus(m,p)),Power(Plus(c,Times(d,x)),p),Power(Power(Plus(C1,Times(c,Power(Times(d,x),CN1))),p),CN1)),Subst(Integrate(Times(Power(Plus(C1,Times(c,x,Power(d,CN1))),p),Power(Plus(C1,Times(x,Power(a,CN1))),Times(C1D2,n)),Power(Power(x,Plus(m,p,C2)),CN1),Power(Power(Subtract(C1,Times(x,Power(a,CN1))),Times(C1D2,n)),CN1)),x),x,Power(x,CN1))),x),And(FreeQ(List(a,c,d,e,m,n,p),x),EqQ(Subtract(Times(Sqr(a),Sqr(c)),Sqr(d)),C0),Not(IntegerQ(p))))),
IIntegrate(6731,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Negate(Power(c,n)),Subst(Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(p,n)),Power(Subtract(C1,Times(Sqr(x),Power(a,CN2))),Times(C1D2,n)),Power(x,CN2)),x),x,Power(x,CN1))),x),And(FreeQ(List(a,c,d,p),x),EqQ(Plus(c,Times(a,d)),C0),IntegerQ(Times(C1D2,Subtract(n,C1))),IntegerQ(Times(C2,p))))),
IIntegrate(6732,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Negate(Power(c,p)),Subst(Integrate(Times(Power(Plus(C1,Times(d,x,Power(c,CN1))),p),Power(Plus(C1,Times(x,Power(a,CN1))),Times(C1D2,n)),Power(Times(Sqr(x),Power(Subtract(C1,Times(x,Power(a,CN1))),Times(C1D2,n))),CN1)),x),x,Power(x,CN1))),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Subtract(Sqr(c),Times(Sqr(a),Sqr(d))),C0),Not(IntegerQ(Times(C1D2,n))),Or(IntegerQ(p),GtQ(c,C0))))),
IIntegrate(6733,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Negate(Power(c,n)),Subst(Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(p,n)),Power(Subtract(C1,Times(Sqr(x),Power(a,CN2))),Times(C1D2,n)),Power(Power(x,Plus(m,C2)),CN1)),x),x,Power(x,CN1))),x),And(FreeQ(List(a,c,d,p),x),EqQ(Plus(c,Times(a,d)),C0),IntegerQ(Times(C1D2,Subtract(n,C1))),IntegerQ(m),IntegerQ(Times(C2,p))))),
IIntegrate(6734,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Negate(Power(c,p)),Subst(Integrate(Times(Power(Plus(C1,Times(d,x,Power(c,CN1))),p),Power(Plus(C1,Times(x,Power(a,CN1))),Times(C1D2,n)),Power(Times(Power(x,Plus(m,C2)),Power(Subtract(C1,Times(x,Power(a,CN1))),Times(C1D2,n))),CN1)),x),x,Power(x,CN1))),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Subtract(Sqr(c),Times(Sqr(a),Sqr(d))),C0),Not(IntegerQ(Times(C1D2,n))),Or(IntegerQ(p),GtQ(c,C0)),IntegerQ(m)))),
IIntegrate(6735,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_DEFAULT),Power(x_,m_)),x_Symbol),
    Condition(Simp(Star(Times(CN1,Power(c,p),Power(x,m),Power(Power(x,CN1),m)),Subst(Integrate(Times(Power(Plus(C1,Times(d,x,Power(c,CN1))),p),Power(Plus(C1,Times(x,Power(a,CN1))),Times(C1D2,n)),Power(Times(Power(x,Plus(m,C2)),Power(Subtract(C1,Times(x,Power(a,CN1))),Times(C1D2,n))),CN1)),x),x,Power(x,CN1))),x),And(FreeQ(List(a,c,d,m,n,p),x),EqQ(Subtract(Sqr(c),Times(Sqr(a),Sqr(d))),C0),Not(IntegerQ(Times(C1D2,n))),Or(IntegerQ(p),GtQ(c,C0)),Not(IntegerQ(m))))),
IIntegrate(6736,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(Plus(c,Times(d,Power(x,CN1))),p),Power(Power(Plus(C1,Times(d,Power(Times(c,x),CN1))),p),CN1)),Integrate(Times(u,Power(Plus(C1,Times(d,Power(Times(c,x),CN1))),p),Exp(Times(n,ArcCoth(Times(a,x))))),x)),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Subtract(Sqr(c),Times(Sqr(a),Sqr(d))),C0),Not(IntegerQ(Times(C1D2,n))),Not(Or(IntegerQ(p),GtQ(c,C0)))))),
IIntegrate(6737,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Simp(Times(Exp(Times(n,ArcCoth(Times(a,x)))),Power(Times(a,c,n),CN1)),x),And(FreeQ(List(a,c,d,n),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Not(IntegerQ(Times(C1D2,n)))))),
IIntegrate(6738,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),QQ(-3L,2L))),x_Symbol),
    Condition(Simp(Times(Subtract(n,Times(a,x)),Exp(Times(n,ArcCoth(Times(a,x)))),Power(Times(a,c,Subtract(Sqr(n),C1),Sqrt(Plus(c,Times(d,Sqr(x))))),CN1)),x),And(FreeQ(List(a,c,d,n),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Not(IntegerQ(n))))),
IIntegrate(6739,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(n,Times(C2,a,Plus(p,C1),x)),Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcCoth(Times(a,x)))),Power(Times(a,c,Subtract(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),CN1)),x),Simp(Star(Times(C2,Plus(p,C1),Plus(Times(C2,p),C3),Power(Times(c,Subtract(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),CN1)),Integrate(Times(Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcCoth(Times(a,x))))),x)),x)),And(FreeQ(List(a,c,d,n),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Not(IntegerQ(Times(C1D2,n))),LtQ(p,CN1),NeQ(p,QQ(-3L,2L)),NeQ(Subtract(Sqr(n),Times(C4,Sqr(Plus(p,C1)))),C0),Or(IntegerQ(p),Not(IntegerQ(n)))))),
IIntegrate(6740,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_)),x_,Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),QQ(-3L,2L))),x_Symbol),
    Condition(Simp(Times(CN1,Subtract(C1,Times(a,n,x)),Exp(Times(n,ArcCoth(Times(a,x)))),Power(Times(Sqr(a),c,Subtract(Sqr(n),C1),Sqrt(Plus(c,Times(d,Sqr(x))))),CN1)),x),And(FreeQ(List(a,c,d,n),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Not(IntegerQ(n)))))
  );
}
