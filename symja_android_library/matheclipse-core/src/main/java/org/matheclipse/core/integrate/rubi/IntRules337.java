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
class IntRules337 { 
  public static IAST RULES = List( 
IIntegrate(6741,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,x_)),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(CN1,Power(d,p),Power(Times(e,x),m),Power(Power(x,CN1),m)),Subst(Integrate(Times(Power(Plus(C1,Times(c,x,Power(d,CN1))),p),Power(Plus(C1,Times(x,Power(a,CN1))),Times(C1D2,n)),Power(Power(x,Plus(m,p,C2)),CN1),Power(Power(Subtract(C1,Times(x,Power(a,CN1))),Times(C1D2,n)),CN1)),x),x,Power(x,CN1)),x),x),And(FreeQ(List(a,c,d,e,m,n),x),EqQ(Subtract(Times(Sqr(a),Sqr(c)),Sqr(d)),C0),IntegerQ(p)))),
IIntegrate(6742,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,x_)),p_)),x_Symbol),
    Condition(Simp(Dist(Times(CN1,Power(Times(e,x),m),Power(Power(x,CN1),Plus(m,p)),Power(Plus(c,Times(d,x)),p),Power(Power(Plus(C1,Times(c,Power(Times(d,x),CN1))),p),CN1)),Subst(Integrate(Times(Power(Plus(C1,Times(c,x,Power(d,CN1))),p),Power(Plus(C1,Times(x,Power(a,CN1))),Times(C1D2,n)),Power(Power(x,Plus(m,p,C2)),CN1),Power(Power(Subtract(C1,Times(x,Power(a,CN1))),Times(C1D2,n)),CN1)),x),x,Power(x,CN1)),x),x),And(FreeQ(List(a,c,d,e,m,n,p),x),EqQ(Subtract(Times(Sqr(a),Sqr(c)),Sqr(d)),C0),Not(IntegerQ(p))))),
IIntegrate(6743,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Negate(Power(c,n)),Subst(Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(p,n)),Power(Subtract(C1,Times(Sqr(x),Power(a,CN2))),Times(C1D2,n)),Power(x,CN2)),x),x,Power(x,CN1)),x),x),And(FreeQ(List(a,c,d,p),x),EqQ(Plus(c,Times(a,d)),C0),IntegerQ(Times(C1D2,Plus(n,CN1))),IntegerQ(Times(C2,p))))),
IIntegrate(6744,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Negate(Power(c,p)),Subst(Integrate(Times(Power(Plus(C1,Times(d,x,Power(c,CN1))),p),Power(Plus(C1,Times(x,Power(a,CN1))),Times(C1D2,n)),Power(Times(Sqr(x),Power(Subtract(C1,Times(x,Power(a,CN1))),Times(C1D2,n))),CN1)),x),x,Power(x,CN1)),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Subtract(Sqr(c),Times(Sqr(a),Sqr(d))),C0),Not(IntegerQ(Times(C1D2,n))),Or(IntegerQ(p),GtQ(c,C0))))),
IIntegrate(6745,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Negate(Power(c,n)),Subst(Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(p,n)),Power(Subtract(C1,Times(Sqr(x),Power(a,CN2))),Times(C1D2,n)),Power(Power(x,Plus(m,C2)),CN1)),x),x,Power(x,CN1)),x),x),And(FreeQ(List(a,c,d,p),x),EqQ(Plus(c,Times(a,d)),C0),IntegerQ(Times(C1D2,Plus(n,CN1))),IntegerQ(m),IntegerQ(Times(C2,p))))),
IIntegrate(6746,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Negate(Power(c,p)),Subst(Integrate(Times(Power(Plus(C1,Times(d,x,Power(c,CN1))),p),Power(Plus(C1,Times(x,Power(a,CN1))),Times(C1D2,n)),Power(Times(Power(x,Plus(m,C2)),Power(Subtract(C1,Times(x,Power(a,CN1))),Times(C1D2,n))),CN1)),x),x,Power(x,CN1)),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Subtract(Sqr(c),Times(Sqr(a),Sqr(d))),C0),Not(IntegerQ(Times(C1D2,n))),Or(IntegerQ(p),GtQ(c,C0)),IntegerQ(m)))),
IIntegrate(6747,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_DEFAULT),Power(x_,m_)),x_Symbol),
    Condition(Simp(Dist(Times(CN1,Power(c,p),Power(x,m),Power(Power(x,CN1),m)),Subst(Integrate(Times(Power(Plus(C1,Times(d,x,Power(c,CN1))),p),Power(Plus(C1,Times(x,Power(a,CN1))),Times(C1D2,n)),Power(Times(Power(x,Plus(m,C2)),Power(Subtract(C1,Times(x,Power(a,CN1))),Times(C1D2,n))),CN1)),x),x,Power(x,CN1)),x),x),And(FreeQ(List(a,c,d,m,n,p),x),EqQ(Subtract(Sqr(c),Times(Sqr(a),Sqr(d))),C0),Not(IntegerQ(Times(C1D2,n))),Or(IntegerQ(p),GtQ(c,C0)),Not(IntegerQ(m))))),
IIntegrate(6748,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Plus(c,Times(d,Power(x,CN1))),p),Power(Power(Plus(C1,Times(d,Power(Times(c,x),CN1))),p),CN1)),Integrate(Times(u,Power(Plus(C1,Times(d,Power(Times(c,x),CN1))),p),Exp(Times(n,ArcCoth(Times(a,x))))),x),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Subtract(Sqr(c),Times(Sqr(a),Sqr(d))),C0),Not(IntegerQ(Times(C1D2,n))),Not(Or(IntegerQ(p),GtQ(c,C0)))))),
IIntegrate(6749,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Simp(Times(Exp(Times(n,ArcCoth(Times(a,x)))),Power(Times(a,c,n),CN1)),x),And(FreeQ(List(a,c,d,n),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Not(IntegerQ(Times(C1D2,n)))))),
IIntegrate(6750,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),QQ(-3L,2L))),x_Symbol),
    Condition(Simp(Times(Subtract(n,Times(a,x)),Exp(Times(n,ArcCoth(Times(a,x)))),Power(Times(a,c,Plus(Sqr(n),CN1),Sqrt(Plus(c,Times(d,Sqr(x))))),CN1)),x),And(FreeQ(List(a,c,d,n),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Not(IntegerQ(n))))),
IIntegrate(6751,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(n,Times(C2,a,Plus(p,C1),x)),Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcCoth(Times(a,x)))),Power(Times(a,c,Subtract(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),CN1)),x),Simp(Dist(Times(C2,Plus(p,C1),Plus(Times(C2,p),C3),Power(Times(c,Subtract(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),CN1)),Integrate(Times(Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcCoth(Times(a,x))))),x),x),x)),And(FreeQ(List(a,c,d,n),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Not(IntegerQ(Times(C1D2,n))),LtQ(p,CN1),NeQ(p,QQ(-3L,2L)),NeQ(Subtract(Sqr(n),Times(C4,Sqr(Plus(p,C1)))),C0),Or(IntegerQ(p),Not(IntegerQ(n)))))),
IIntegrate(6752,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_)),x_,Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),QQ(-3L,2L))),x_Symbol),
    Condition(Simp(Times(CN1,Subtract(C1,Times(a,n,x)),Exp(Times(n,ArcCoth(Times(a,x)))),Power(Times(Sqr(a),c,Plus(Sqr(n),CN1),Sqrt(Plus(c,Times(d,Sqr(x))))),CN1)),x),And(FreeQ(List(a,c,d,n),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Not(IntegerQ(n))))),
IIntegrate(6753,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),x_,Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(Times(C2,Plus(p,C1)),Times(a,n,x)),Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcCoth(Times(a,x)))),Power(Times(Sqr(a),c,Subtract(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),CN1)),x),Simp(Dist(Times(n,Plus(Times(C2,p),C3),Power(Times(a,c,Subtract(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),CN1)),Integrate(Times(Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcCoth(Times(a,x))))),x),x),x)),And(FreeQ(List(a,c,d,n),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Not(IntegerQ(Times(C1D2,n))),LeQ(p,CN1),NeQ(p,QQ(-3L,2L)),NeQ(Subtract(Sqr(n),Times(C4,Sqr(Plus(p,C1)))),C0),Or(IntegerQ(p),Not(IntegerQ(n)))))),
IIntegrate(6754,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),Sqr(x_),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Times(CN1,Plus(n,Times(C2,Plus(p,C1),a,x)),Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcCoth(Times(a,x)))),Power(Times(Power(a,C3),c,Sqr(n),Plus(Sqr(n),CN1)),CN1)),x),And(FreeQ(List(a,c,d,n),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Not(IntegerQ(Times(C1D2,n))),EqQ(Plus(Sqr(n),Times(C2,Plus(p,C1))),C0),NeQ(Sqr(n),C1)))),
IIntegrate(6755,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),Sqr(x_),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(n,Times(C2,Plus(p,C1),a,x)),Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcCoth(Times(a,x)))),Power(Times(Power(a,C3),c,Subtract(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),CN1)),x),Simp(Dist(Times(Plus(Sqr(n),Times(C2,Plus(p,C1))),Power(Times(Sqr(a),c,Subtract(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),CN1)),Integrate(Times(Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcCoth(Times(a,x))))),x),x),x)),And(FreeQ(List(a,c,d,n),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Not(IntegerQ(Times(C1D2,n))),LeQ(p,CN1),NeQ(Plus(Sqr(n),Times(C2,Plus(p,C1))),C0),NeQ(Subtract(Sqr(n),Times(C4,Sqr(Plus(p,C1)))),C0),Or(IntegerQ(p),Not(IntegerQ(n)))))),
IIntegrate(6756,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(x_,m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Dist(Times(CN1,Power(Negate(c),p),Power(Power(a,Plus(m,C1)),CN1)),Subst(Integrate(Times(Exp(Times(n,x)),Power(Coth(x),Plus(m,Times(C2,Plus(p,C1)))),Power(Power(Cosh(x),Times(C2,Plus(p,C1))),CN1)),x),x,ArcCoth(Times(a,x))),x),x),And(FreeQ(List(a,c,d,n),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Not(IntegerQ(Times(C1D2,n))),IntegerQ(m),LeQ(C3,m,Times(CN2,Plus(p,C1))),IntegerQ(p)))),
IIntegrate(6757,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(d,p),Integrate(Times(u,Power(x,Times(C2,p)),Power(Subtract(C1,Power(Times(Sqr(a),Sqr(x)),CN1)),p),Exp(Times(n,ArcCoth(Times(a,x))))),x),x),x),And(FreeQ(List(a,c,d,n),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Not(IntegerQ(Times(C1D2,n))),IntegerQ(p)))),
IIntegrate(6758,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Plus(c,Times(d,Sqr(x))),p),Power(Times(Power(x,Times(C2,p)),Power(Subtract(C1,Power(Times(Sqr(a),Sqr(x)),CN1)),p)),CN1)),Integrate(Times(u,Power(x,Times(C2,p)),Power(Subtract(C1,Power(Times(Sqr(a),Sqr(x)),CN1)),p),Exp(Times(n,ArcCoth(Times(a,x))))),x),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Not(IntegerQ(Times(C1D2,n))),Not(IntegerQ(p))))),
IIntegrate(6759,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN2))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(Power(c,p),Power(Power(a,Times(C2,p)),CN1)),Integrate(Times(u,Power(Power(x,Times(C2,p)),CN1),Power(Plus(CN1,Times(a,x)),Subtract(p,Times(C1D2,n))),Power(Plus(C1,Times(a,x)),Plus(p,Times(C1D2,n)))),x),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(c,Times(Sqr(a),d)),C0),Not(IntegerQ(Times(C1D2,n))),Or(IntegerQ(p),GtQ(c,C0)),IntegersQ(Times(C2,p),Plus(p,Times(C1D2,n)))))),
IIntegrate(6760,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN2))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Negate(Power(c,p)),Subst(Integrate(Times(Power(Subtract(C1,Times(x,Power(a,CN1))),Subtract(p,Times(C1D2,n))),Power(Plus(C1,Times(x,Power(a,CN1))),Plus(p,Times(C1D2,n))),Power(x,CN2)),x),x,Power(x,CN1)),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(c,Times(Sqr(a),d)),C0),Not(IntegerQ(Times(C1D2,n))),Or(IntegerQ(p),GtQ(c,C0)),Not(IntegersQ(Times(C2,p),Plus(p,Times(C1D2,n)))))))
  );
}
