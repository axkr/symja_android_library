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
class IntRules282 { 
  public static IAST RULES = List( 
IIntegrate(5641,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_)),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(d,p),Integrate(Times(u,Power(x,p),Power(Plus(C1,Times(c,Power(Times(d,x),CN1))),p),Exp(Times(n,ArcCot(Times(a,x))))),x),x),x),And(FreeQ(List(a,c,d,n),x),EqQ(Plus(Times(Sqr(a),Sqr(c)),Sqr(d)),C0),Not(IntegerQ(Times(CI,C1D2,n))),IntegerQ(p)))),
IIntegrate(5642,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_)),p_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Plus(c,Times(d,x)),p),Power(Times(Power(x,p),Power(Plus(C1,Times(c,Power(Times(d,x),CN1))),p)),CN1)),Integrate(Times(u,Power(x,p),Power(Plus(C1,Times(c,Power(Times(d,x),CN1))),p),Exp(Times(n,ArcCot(Times(a,x))))),x),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(Times(Sqr(a),Sqr(c)),Sqr(d)),C0),Not(IntegerQ(Times(CI,C1D2,n))),Not(IntegerQ(p))))),
IIntegrate(5643,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Negate(Power(c,p)),Subst(Integrate(Times(Power(Plus(C1,Times(d,x,Power(c,CN1))),p),Power(Subtract(C1,Times(CI,x,Power(a,CN1))),Times(CI,C1D2,n)),Power(Times(Sqr(x),Power(Plus(C1,Times(CI,x,Power(a,CN1))),Times(CI,C1D2,n))),CN1)),x),x,Power(x,CN1)),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(Sqr(c),Times(Sqr(a),Sqr(d))),C0),Not(IntegerQ(Times(CI,C1D2,n))),Or(IntegerQ(p),GtQ(c,C0))))),
IIntegrate(5644,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Negate(Power(c,p)),Subst(Integrate(Times(Power(Plus(C1,Times(d,x,Power(c,CN1))),p),Power(Subtract(C1,Times(CI,x,Power(a,CN1))),Times(CI,C1D2,n)),Power(Times(Power(x,Plus(m,C2)),Power(Plus(C1,Times(CI,x,Power(a,CN1))),Times(CI,C1D2,n))),CN1)),x),x,Power(x,CN1)),x),x),And(FreeQ(List(a,c,d,m,n,p),x),EqQ(Plus(Sqr(c),Times(Sqr(a),Sqr(d))),C0),Not(IntegerQ(Times(CI,C1D2,n))),Or(IntegerQ(p),GtQ(c,C0)),IntegerQ(m)))),
IIntegrate(5645,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Plus(c,Times(d,Power(x,CN1))),p),Power(Power(Plus(C1,Times(d,Power(Times(c,x),CN1))),p),CN1)),Integrate(Times(Power(Plus(C1,Times(d,Power(Times(c,x),CN1))),p),Exp(Times(n,ArcCot(Times(a,x))))),x),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(Sqr(c),Times(Sqr(a),Sqr(d))),C0),Not(IntegerQ(Times(CI,C1D2,n))),Not(Or(IntegerQ(p),GtQ(c,C0)))))),
IIntegrate(5646,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_DEFAULT),Power(x_,m_)),x_Symbol),
    Condition(Simp(Dist(Times(CN1,Power(c,p),Power(x,m),Power(Power(x,CN1),m)),Subst(Integrate(Times(Power(Plus(C1,Times(d,x,Power(c,CN1))),p),Power(Subtract(C1,Times(CI,x,Power(a,CN1))),Times(CI,C1D2,n)),Power(Times(Power(x,Plus(m,C2)),Power(Plus(C1,Times(CI,x,Power(a,CN1))),Times(CI,C1D2,n))),CN1)),x),x,Power(x,CN1)),x),x),And(FreeQ(List(a,c,d,m,n,p),x),EqQ(Plus(Sqr(c),Times(Sqr(a),Sqr(d))),C0),Not(IntegerQ(Times(CI,C1D2,n))),Or(IntegerQ(p),GtQ(c,C0)),Not(IntegerQ(m))))),
IIntegrate(5647,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Plus(c,Times(d,Power(x,CN1))),p),Power(Power(Plus(C1,Times(d,Power(Times(c,x),CN1))),p),CN1)),Integrate(Times(u,Power(Plus(C1,Times(d,Power(Times(c,x),CN1))),p),Exp(Times(n,ArcCot(Times(a,x))))),x),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(Sqr(c),Times(Sqr(a),Sqr(d))),C0),Not(IntegerQ(Times(CI,C1D2,n))),Not(Or(IntegerQ(p),GtQ(c,C0)))))),
IIntegrate(5648,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Simp(Times(CN1,Exp(Times(n,ArcCot(Times(a,x)))),Power(Times(a,c,n),CN1)),x),And(FreeQ(List(a,c,d,n),x),EqQ(d,Times(Sqr(a),c))))),
IIntegrate(5649,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),QQ(-3L,2L))),x_Symbol),
    Condition(Simp(Times(CN1,Subtract(n,Times(a,x)),Exp(Times(n,ArcCot(Times(a,x)))),Power(Times(a,c,Plus(Sqr(n),C1),Sqrt(Plus(c,Times(d,Sqr(x))))),CN1)),x),And(FreeQ(List(a,c,d,n),x),EqQ(d,Times(Sqr(a),c)),Not(IntegerQ(Times(C1D2,Plus(Times(CI,n),CN1))))))),
IIntegrate(5650,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Plus(n,Times(C2,a,Plus(p,C1),x)),Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcCot(Times(a,x)))),Power(Times(a,c,Plus(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),CN1)),x),Simp(Dist(Times(C2,Plus(p,C1),Plus(Times(C2,p),C3),Power(Times(c,Plus(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),CN1)),Integrate(Times(Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcCot(Times(a,x))))),x),x),x)),And(FreeQ(List(a,c,d,n),x),EqQ(d,Times(Sqr(a),c)),LtQ(p,CN1),NeQ(p,QQ(-3L,2L)),NeQ(Plus(Sqr(n),Times(C4,Sqr(Plus(p,C1)))),C0),Not(And(IntegerQ(p),IntegerQ(Times(CI,C1D2,n)))),Not(And(Not(IntegerQ(p)),IntegerQ(Times(C1D2,Plus(Times(CI,n),CN1)))))))),
IIntegrate(5651,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),x_,Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),QQ(-3L,2L))),x_Symbol),
    Condition(Simp(Times(CN1,Plus(C1,Times(a,n,x)),Exp(Times(n,ArcCot(Times(a,x)))),Power(Times(Sqr(a),c,Plus(Sqr(n),C1),Sqrt(Plus(c,Times(d,Sqr(x))))),CN1)),x),And(FreeQ(List(a,c,d,n),x),EqQ(d,Times(Sqr(a),c)),Not(IntegerQ(Times(C1D2,Plus(Times(CI,n),CN1))))))),
IIntegrate(5652,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),x_,Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(Subtract(Times(C2,Plus(p,C1)),Times(a,n,x)),Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcCot(Times(a,x)))),Power(Times(Sqr(a),c,Plus(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),CN1)),x),Simp(Dist(Times(n,Plus(Times(C2,p),C3),Power(Times(a,c,Plus(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),CN1)),Integrate(Times(Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcCot(Times(a,x))))),x),x),x)),And(FreeQ(List(a,c,d,n),x),EqQ(d,Times(Sqr(a),c)),LeQ(p,CN1),NeQ(p,QQ(-3L,2L)),NeQ(Plus(Sqr(n),Times(C4,Sqr(Plus(p,C1)))),C0),Not(And(IntegerQ(p),IntegerQ(Times(CI,C1D2,n)))),Not(And(Not(IntegerQ(p)),IntegerQ(Times(C1D2,Plus(Times(CI,n),CN1)))))))),
IIntegrate(5653,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),Sqr(x_),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Times(Plus(n,Times(C2,Plus(p,C1),a,x)),Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcCot(Times(a,x)))),Power(Times(Power(a,C3),c,Sqr(n),Plus(Sqr(n),C1)),CN1)),x),And(FreeQ(List(a,c,d,n),x),EqQ(d,Times(Sqr(a),c)),EqQ(Subtract(Sqr(n),Times(C2,Plus(p,C1))),C0),NeQ(Plus(Sqr(n),C1),C0)))),
IIntegrate(5654,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),Sqr(x_),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(Plus(n,Times(C2,Plus(p,C1),a,x)),Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcCot(Times(a,x)))),Power(Times(Power(a,C3),c,Plus(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),CN1)),x),Simp(Dist(Times(Subtract(Sqr(n),Times(C2,Plus(p,C1))),Power(Times(Sqr(a),c,Plus(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),CN1)),Integrate(Times(Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcCot(Times(a,x))))),x),x),x)),And(FreeQ(List(a,c,d,n),x),EqQ(d,Times(Sqr(a),c)),LeQ(p,CN1),NeQ(Subtract(Sqr(n),Times(C2,Plus(p,C1))),C0),NeQ(Plus(Sqr(n),Times(C4,Sqr(Plus(p,C1)))),C0),Not(And(IntegerQ(p),IntegerQ(Times(CI,C1D2,n)))),Not(And(Not(IntegerQ(p)),IntegerQ(Times(C1D2,Plus(Times(CI,n),CN1)))))))),
IIntegrate(5655,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(x_,m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Dist(Times(CN1,Power(c,p),Power(Power(a,Plus(m,C1)),CN1)),Subst(Integrate(Times(Exp(Times(n,x)),Power(Cot(x),Plus(m,Times(C2,Plus(p,C1)))),Power(Power(Cos(x),Times(C2,Plus(p,C1))),CN1)),x),x,ArcCot(Times(a,x))),x),x),And(FreeQ(List(a,c,d,n),x),EqQ(d,Times(Sqr(a),c)),IntegerQ(m),LeQ(C3,m,Times(CN2,Plus(p,C1))),IntegerQ(p)))),
IIntegrate(5656,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(d,p),Integrate(Times(u,Power(x,Times(C2,p)),Power(Plus(C1,Power(Times(Sqr(a),Sqr(x)),CN1)),p),Exp(Times(n,ArcCot(Times(a,x))))),x),x),x),And(FreeQ(List(a,c,d,n),x),EqQ(d,Times(Sqr(a),c)),Not(IntegerQ(Times(CI,C1D2,n))),IntegerQ(p)))),
IIntegrate(5657,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Plus(c,Times(d,Sqr(x))),p),Power(Times(Power(x,Times(C2,p)),Power(Plus(C1,Power(Times(Sqr(a),Sqr(x)),CN1)),p)),CN1)),Integrate(Times(u,Power(x,Times(C2,p)),Power(Plus(C1,Power(Times(Sqr(a),Sqr(x)),CN1)),p),Exp(Times(n,ArcCot(Times(a,x))))),x),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(d,Times(Sqr(a),c)),Not(IntegerQ(Times(CI,C1D2,n))),Not(IntegerQ(p))))),
IIntegrate(5658,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN2))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(Power(c,p),Power(Power(Times(CI,a),Times(C2,p)),CN1)),Integrate(Times(u,Power(Power(x,Times(C2,p)),CN1),Power(Plus(CN1,Times(CI,a,x)),Subtract(p,Times(CI,C1D2,n))),Power(Plus(C1,Times(CI,a,x)),Plus(p,Times(CI,C1D2,n)))),x),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(c,Times(Sqr(a),d)),Not(IntegerQ(Times(CI,C1D2,n))),Or(IntegerQ(p),GtQ(c,C0)),IntegersQ(Times(C2,p),Plus(p,Times(CI,C1D2,n)))))),
IIntegrate(5659,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN2))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Negate(Power(c,p)),Subst(Integrate(Times(Power(Subtract(C1,Times(CI,x,Power(a,CN1))),Plus(p,Times(CI,C1D2,n))),Power(Plus(C1,Times(CI,x,Power(a,CN1))),Subtract(p,Times(CI,C1D2,n))),Power(x,CN2)),x),x,Power(x,CN1)),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(c,Times(Sqr(a),d)),Not(IntegerQ(Times(CI,C1D2,n))),Or(IntegerQ(p),GtQ(c,C0)),Not(And(IntegerQ(Times(C2,p)),IntegerQ(Plus(p,Times(CI,C1D2,n)))))))),
IIntegrate(5660,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN2))),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Negate(Power(c,p)),Subst(Integrate(Times(Power(Subtract(C1,Times(CI,x,Power(a,CN1))),Plus(p,Times(CI,C1D2,n))),Power(Plus(C1,Times(CI,x,Power(a,CN1))),Subtract(p,Times(CI,C1D2,n))),Power(Power(x,Plus(m,C2)),CN1)),x),x,Power(x,CN1)),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(c,Times(Sqr(a),d)),Not(IntegerQ(Times(CI,C1D2,n))),Or(IntegerQ(p),GtQ(c,C0)),Not(And(IntegerQ(Times(C2,p)),IntegerQ(Plus(p,Times(CI,C1D2,n))))),IntegerQ(m))))
  );
}
