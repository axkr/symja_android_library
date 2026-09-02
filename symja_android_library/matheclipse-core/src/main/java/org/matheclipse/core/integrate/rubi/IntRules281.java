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
class IntRules281 { 
  public static IAST RULES = List( 
IIntegrate(5621,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),u_,Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(c,p),Integrate(Times(u,Power(Subtract(C1,Times(CI,a,x)),Plus(p,Times(CI,C1D2,n))),Power(Plus(C1,Times(CI,a,x)),Subtract(p,Times(CI,C1D2,n)))),x),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(d,Times(Sqr(a),c)),Or(IntegerQ(p),GtQ(c,C0))))),
IIntegrate(5622,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_)),u_,Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(Power(c,IntPart(p)),Power(Plus(c,Times(d,Sqr(x))),FracPart(p)),Power(Times(Power(Subtract(C1,Times(CI,a,x)),FracPart(p)),Power(Plus(C1,Times(CI,a,x)),FracPart(p))),CN1)),Integrate(Times(u,Power(Subtract(C1,Times(CI,a,x)),Plus(p,Times(CI,C1D2,n))),Power(Plus(C1,Times(CI,a,x)),Subtract(p,Times(CI,C1D2,n)))),x),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(d,Times(Sqr(a),c)),Or(IntegerQ(p),GtQ(c,C0)),IntegerQ(Times(CI,C1D2,n))))),
IIntegrate(5623,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),u_,Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(c,IntPart(p)),Power(Plus(c,Times(d,Sqr(x))),FracPart(p)),Power(Power(Plus(C1,Times(Sqr(a),Sqr(x))),FracPart(p)),CN1)),Integrate(Times(u,Power(Plus(C1,Times(Sqr(a),Sqr(x))),p),Exp(Times(n,ArcTan(Times(a,x))))),x),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(d,Times(Sqr(a),c)),Not(Or(IntegerQ(p),GtQ(c,C0))),Not(IntegerQ(Times(CI,C1D2,n)))))),
IIntegrate(5624,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN2))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(d,p),Integrate(Times(u,Power(Power(x,Times(C2,p)),CN1),Power(Plus(C1,Times(Sqr(a),Sqr(x))),p),Exp(Times(n,ArcTan(Times(a,x))))),x),x),x),And(FreeQ(List(a,c,d,n),x),EqQ(Subtract(c,Times(Sqr(a),d)),C0),IntegerQ(p)))),
IIntegrate(5625,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN2))),p_)),x_Symbol),
    Condition(Simp(Dist(Power(c,p),Integrate(Times(u,Power(Subtract(C1,Times(CI,Power(Times(a,x),CN1))),p),Power(Plus(C1,Times(CI,Power(Times(a,x),CN1))),p),Exp(Times(n,ArcTan(Times(a,x))))),x),x),x),And(FreeQ(List(a,c,d,p),x),EqQ(Subtract(c,Times(Sqr(a),d)),C0),Not(IntegerQ(p)),IntegerQ(Times(CI,C1D2,n)),GtQ(c,C0)))),
IIntegrate(5626,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN2))),p_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(x,Times(C2,p)),Power(Plus(c,Times(d,Power(x,CN2))),p),Power(Power(Plus(C1,Times(Sqr(a),Sqr(x))),p),CN1)),Integrate(Times(u,Power(Plus(C1,Times(Sqr(a),Sqr(x))),Plus(Times(CI,C1D2,n),p)),Power(Times(Power(x,Times(C2,p)),Power(Plus(C1,Times(CI,a,x)),Times(CI,n))),CN1)),x),x),x),And(FreeQ(List(a,c,d,p),x),EqQ(Subtract(c,Times(Sqr(a),d)),C0),Not(IntegerQ(p)),IntegerQ(Times(CI,C1D2,n)),Not(GtQ(c,C0))))),
IIntegrate(5627,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN2))),p_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(x,Times(C2,p)),Power(Plus(c,Times(d,Power(x,CN2))),p),Power(Power(Plus(C1,Times(Sqr(a),Sqr(x))),p),CN1)),Integrate(Times(u,Power(Power(x,Times(C2,p)),CN1),Power(Plus(C1,Times(Sqr(a),Sqr(x))),p),Exp(Times(n,ArcTan(Times(a,x))))),x),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Subtract(c,Times(Sqr(a),d)),C0),Not(IntegerQ(p)),Not(IntegerQ(Times(CI,C1D2,n)))))),
IIntegrate(5628,Integrate(Exp(Times(ArcTan(Times(c_DEFAULT,Plus(a_,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Subtract(Subtract(C1,Times(CI,a,c)),Times(CI,b,c,x)),Times(CI,C1D2,n)),Power(Power(Plus(C1,Times(CI,a,c),Times(CI,b,c,x)),Times(CI,C1D2,n)),CN1)),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(5629,Integrate(Times(Exp(Times(ArcTan(Times(c_DEFAULT,Plus(a_,Times(b_DEFAULT,x_)))),n_)),Power(x_,m_)),x_Symbol),
    Condition(Simp(Dist(Times(C4,Power(Times(Power(CI,m),n,Power(b,Plus(m,C1)),Power(c,Plus(m,C1))),CN1)),Subst(Integrate(Times(Power(x,Times(C2,Power(Times(CI,n),CN1))),Power(Subtract(Subtract(C1,Times(CI,a,c)),Times(Plus(C1,Times(CI,a,c)),Power(x,Times(C2,Power(Times(CI,n),CN1))))),m),Power(Power(Plus(C1,Power(x,Times(C2,Power(Times(CI,n),CN1)))),Plus(m,C2)),CN1)),x),x,Times(Power(Subtract(C1,Times(CI,c,Plus(a,Times(b,x)))),Times(CI,C1D2,n)),Power(Power(Plus(C1,Times(CI,c,Plus(a,Times(b,x)))),Times(CI,C1D2,n)),CN1))),x),x),And(FreeQ(list(a,b,c),x),ILtQ(m,C0),LtQ(CN1,Times(CI,n),C1)))),
IIntegrate(5630,Integrate(Times(Exp(Times(ArcTan(Times(c_DEFAULT,Plus(a_,Times(b_DEFAULT,x_)))),n_DEFAULT)),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Plus(d,Times(e,x)),m),Power(Subtract(Subtract(C1,Times(CI,a,c)),Times(CI,b,c,x)),Times(CI,C1D2,n)),Power(Power(Plus(C1,Times(CI,a,c),Times(CI,b,c,x)),Times(CI,C1D2,n)),CN1)),x),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(5631,Integrate(Times(Exp(Times(ArcTan(Plus(a_,Times(b_DEFAULT,x_))),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_),Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Times(c,Power(Plus(C1,Sqr(a)),CN1)),p),Integrate(Times(u,Power(Subtract(Subtract(C1,Times(CI,a)),Times(CI,b,x)),Plus(p,Times(CI,C1D2,n))),Power(Plus(C1,Times(CI,a),Times(CI,b,x)),Subtract(p,Times(CI,C1D2,n)))),x),x),x),And(FreeQ(List(a,b,c,d,e,n,p),x),EqQ(Times(b,d),Times(C2,a,e)),EqQ(Subtract(Times(Sqr(b),c),Times(e,Plus(C1,Sqr(a)))),C0),Or(IntegerQ(p),GtQ(Times(c,Power(Plus(C1,Sqr(a)),CN1)),C0))))),
IIntegrate(5632,Integrate(Times(Exp(Times(ArcTan(Plus(a_,Times(b_DEFAULT,x_))),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_),Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Plus(c,Times(d,x),Times(e,Sqr(x))),p),Power(Power(Plus(C1,Sqr(a),Times(C2,a,b,x),Times(Sqr(b),Sqr(x))),p),CN1)),Integrate(Times(u,Power(Plus(C1,Sqr(a),Times(C2,a,b,x),Times(Sqr(b),Sqr(x))),p),Exp(Times(n,ArcTan(Times(a,x))))),x),x),x),And(FreeQ(List(a,b,c,d,e,n,p),x),EqQ(Times(b,d),Times(C2,a,e)),EqQ(Subtract(Times(Sqr(b),c),Times(e,Plus(C1,Sqr(a)))),C0),Not(Or(IntegerQ(p),GtQ(Times(c,Power(Plus(C1,Sqr(a)),CN1)),C0)))))),
IIntegrate(5633,Integrate(Times(Exp(Times(ArcTan(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),CN1))),n_DEFAULT)),u_DEFAULT),x_Symbol),
    Condition(Integrate(Times(u,Exp(Times(n,ArcCot(Plus(Times(a,Power(c,CN1)),Times(b,x,Power(c,CN1))))))),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(5634,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_)),u_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Power(-1,Times(CI,C1D2,n)),Integrate(Times(u,Power(Exp(Times(n,ArcTan(Times(a,x)))),CN1)),x),x),x),And(FreeQ(a,x),IntegerQ(Times(CI,C1D2,n))))),
IIntegrate(5635,Integrate(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Subtract(C1,Times(CI,x,Power(a,CN1))),Times(C1D2,Plus(Times(CI,n),C1))),Power(Times(Sqr(x),Power(Plus(C1,Times(CI,x,Power(a,CN1))),Times(C1D2,Plus(Times(CI,n),CN1))),Sqrt(Plus(C1,Times(Sqr(x),Power(a,CN2))))),CN1)),x),x,Power(x,CN1))),And(FreeQ(a,x),IntegerQ(Times(C1D2,Plus(Times(CI,n),CN1)))))),
IIntegrate(5636,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Subtract(C1,Times(CI,x,Power(a,CN1))),Times(C1D2,Plus(Times(CI,n),C1))),Power(Times(Power(x,Plus(m,C2)),Power(Plus(C1,Times(CI,x,Power(a,CN1))),Times(C1D2,Plus(Times(CI,n),CN1))),Sqrt(Plus(C1,Times(Sqr(x),Power(a,CN2))))),CN1)),x),x,Power(x,CN1))),And(FreeQ(a,x),IntegerQ(Times(C1D2,Plus(Times(CI,n),CN1))),IntegerQ(m)))),
IIntegrate(5637,Integrate(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Subtract(C1,Times(CI,x,Power(a,CN1))),Times(CI,C1D2,n)),Power(Times(Sqr(x),Power(Plus(C1,Times(CI,x,Power(a,CN1))),Times(CI,C1D2,n))),CN1)),x),x,Power(x,CN1))),And(FreeQ(list(a,n),x),Not(IntegerQ(Times(CI,n)))))),
IIntegrate(5638,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Subtract(C1,Times(CI,x,Power(a,CN1))),Times(C1D2,n)),Power(Times(Power(x,Plus(m,C2)),Power(Plus(C1,Times(CI,x,Power(a,CN1))),Times(C1D2,n))),CN1)),x),x,Power(x,CN1))),And(FreeQ(list(a,n),x),Not(IntegerQ(Times(CI,n))),IntegerQ(m)))),
IIntegrate(5639,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_)),Power(x_,m_)),x_Symbol),
    Condition(Simp(Dist(Times(CN1,Power(x,m),Power(Power(x,CN1),m)),Subst(Integrate(Times(Power(Subtract(C1,Times(CI,x,Power(a,CN1))),Times(C1D2,Plus(Times(CI,n),C1))),Power(Times(Power(x,Plus(m,C2)),Power(Plus(C1,Times(CI,x,Power(a,CN1))),Times(C1D2,Plus(Times(CI,n),CN1))),Sqrt(Plus(C1,Times(Sqr(x),Power(a,CN2))))),CN1)),x),x,Power(x,CN1)),x),x),And(FreeQ(list(a,m),x),IntegerQ(Times(C1D2,Plus(Times(CI,n),CN1))),Not(IntegerQ(m))))),
IIntegrate(5640,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(x_,m_)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Subtract(C1,Times(CI,x,Power(a,CN1))),Times(C1D2,n)),Power(Times(Power(x,Plus(m,C2)),Power(Plus(C1,Times(CI,x,Power(a,CN1))),Times(C1D2,n))),CN1)),x),x,Power(x,CN1))),And(FreeQ(list(a,m,n),x),Not(IntegerQ(Times(CI,C1D2,n))),Not(IntegerQ(m)))))
  );
}
