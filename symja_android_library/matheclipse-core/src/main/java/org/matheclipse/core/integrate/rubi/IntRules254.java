package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class IntRules254 { 
  public static IAST RULES = List( 
IIntegrate(5081,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_)),Power(x_,m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(c,p),Integrate(Times(Power(x,m),Power(Plus(C1,Times(Sqr(a),Sqr(x))),Subtract(p,Times(C1D2,CI,n))),Power(Subtract(C1,Times(CI,a,x)),Times(CI,n))),x),x),And(FreeQ(List(a,c,d,m,p),x),EqQ(d,Times(Sqr(a),c)),Or(IntegerQ(p),GtQ(c,C0)),IntegerQ(Times(C1D2,Plus(Times(CI,n),C1))),Not(IntegerQ(Subtract(p,Times(C1D2,CI,n))))))),
IIntegrate(5082,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(x_,m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(c,p),Integrate(Times(Power(x,m),Power(Subtract(C1,Times(CI,a,x)),Plus(p,Times(C1D2,CI,n))),Power(Plus(C1,Times(CI,a,x)),Subtract(p,Times(C1D2,CI,n)))),x),x),And(FreeQ(List(a,c,d,m,n,p),x),EqQ(d,Times(Sqr(a),c)),Or(IntegerQ(p),GtQ(c,C0))))),
IIntegrate(5083,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_)),Power(x_,m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Dist(Power(c,Times(C1D2,CI,n)),Integrate(Times(Power(x,m),Power(Plus(c,Times(d,Sqr(x))),Subtract(p,Times(C1D2,CI,n))),Power(Subtract(C1,Times(CI,a,x)),Times(CI,n))),x),x),And(FreeQ(List(a,c,d,m,p),x),EqQ(d,Times(Sqr(a),c)),Not(Or(IntegerQ(p),GtQ(c,C0))),IGtQ(Times(C1D2,CI,n),C0)))),
IIntegrate(5084,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_)),Power(x_,m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Dist(Power(Power(c,Times(C1D2,CI,n)),CN1),Integrate(Times(Power(x,m),Power(Plus(c,Times(d,Sqr(x))),Plus(p,Times(C1D2,CI,n))),Power(Power(Plus(C1,Times(CI,a,x)),Times(CI,n)),CN1)),x),x),And(FreeQ(List(a,c,d,m,p),x),EqQ(d,Times(Sqr(a),c)),Not(Or(IntegerQ(p),GtQ(c,C0))),ILtQ(Times(C1D2,CI,n),C0)))),
IIntegrate(5085,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(x_,m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Dist(Times(Power(c,IntPart(p)),Power(Plus(c,Times(d,Sqr(x))),FracPart(p)),Power(Power(Plus(C1,Times(Sqr(a),Sqr(x))),FracPart(p)),CN1)),Integrate(Times(Power(x,m),Power(Plus(C1,Times(Sqr(a),Sqr(x))),p),Exp(Times(n,ArcTan(Times(a,x))))),x),x),And(FreeQ(List(a,c,d,m,n,p),x),EqQ(d,Times(Sqr(a),c)),Not(Or(IntegerQ(p),GtQ(c,C0)))))),
IIntegrate(5086,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),u_,Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(c,p),Integrate(Times(u,Power(Subtract(C1,Times(CI,a,x)),Plus(p,Times(C1D2,CI,n))),Power(Plus(C1,Times(CI,a,x)),Subtract(p,Times(C1D2,CI,n)))),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(d,Times(Sqr(a),c)),Or(IntegerQ(p),GtQ(c,C0))))),
IIntegrate(5087,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_)),u_,Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(c,IntPart(p)),Power(Plus(c,Times(d,Sqr(x))),FracPart(p)),Power(Times(Power(Subtract(C1,Times(CI,a,x)),FracPart(p)),Power(Plus(C1,Times(CI,a,x)),FracPart(p))),CN1)),Integrate(Times(u,Power(Subtract(C1,Times(CI,a,x)),Plus(p,Times(C1D2,CI,n))),Power(Plus(C1,Times(CI,a,x)),Subtract(p,Times(C1D2,CI,n)))),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(d,Times(Sqr(a),c)),Or(IntegerQ(p),GtQ(c,C0)),IntegerQ(Times(C1D2,CI,n))))),
IIntegrate(5088,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),u_,Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Dist(Times(Power(c,IntPart(p)),Power(Plus(c,Times(d,Sqr(x))),FracPart(p)),Power(Power(Plus(C1,Times(Sqr(a),Sqr(x))),FracPart(p)),CN1)),Integrate(Times(u,Power(Plus(C1,Times(Sqr(a),Sqr(x))),p),Exp(Times(n,ArcTan(Times(a,x))))),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(d,Times(Sqr(a),c)),Not(Or(IntegerQ(p),GtQ(c,C0))),Not(IntegerQ(Times(C1D2,CI,n)))))),
IIntegrate(5089,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN2))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(d,p),Integrate(Times(u,Power(Plus(C1,Times(Sqr(a),Sqr(x))),p),Exp(Times(n,ArcTan(Times(a,x)))),Power(Power(x,Times(C2,p)),CN1)),x),x),And(FreeQ(List(a,c,d,n),x),EqQ(Subtract(c,Times(Sqr(a),d)),C0),IntegerQ(p)))),
IIntegrate(5090,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN2))),p_)),x_Symbol),
    Condition(Dist(Power(c,p),Integrate(Times(u,Power(Subtract(C1,Times(CI,Power(Times(a,x),CN1))),p),Power(Plus(C1,Times(CI,Power(Times(a,x),CN1))),p),Exp(Times(n,ArcTan(Times(a,x))))),x),x),And(FreeQ(List(a,c,d,p),x),EqQ(Subtract(c,Times(Sqr(a),d)),C0),Not(IntegerQ(p)),IntegerQ(Times(C1D2,CI,n)),GtQ(c,C0)))),
IIntegrate(5091,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN2))),p_)),x_Symbol),
    Condition(Dist(Times(Power(x,Times(C2,p)),Power(Plus(c,Times(d,Power(x,CN2))),p),Power(Times(Power(Subtract(C1,Times(CI,a,x)),p),Power(Plus(C1,Times(CI,a,x)),p)),CN1)),Integrate(Times(u,Power(Subtract(C1,Times(CI,a,x)),p),Power(Plus(C1,Times(CI,a,x)),p),Exp(Times(n,ArcTan(Times(a,x)))),Power(Power(x,Times(C2,p)),CN1)),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Subtract(c,Times(Sqr(a),d)),C0),Not(IntegerQ(p)),IntegerQ(Times(C1D2,CI,n)),Not(GtQ(c,C0))))),
IIntegrate(5092,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN2))),p_)),x_Symbol),
    Condition(Dist(Times(Power(x,Times(C2,p)),Power(Plus(c,Times(d,Power(x,CN2))),p),Power(Power(Plus(C1,Times(Sqr(a),Sqr(x))),p),CN1)),Integrate(Times(u,Power(Plus(C1,Times(Sqr(a),Sqr(x))),p),Exp(Times(n,ArcTan(Times(a,x)))),Power(Power(x,Times(C2,p)),CN1)),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Subtract(c,Times(Sqr(a),d)),C0),Not(IntegerQ(p)),Not(IntegerQ(Times(C1D2,CI,n)))))),
IIntegrate(5093,Integrate(Exp(Times(ArcTan(Times(c_DEFAULT,Plus(a_,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Subtract(Subtract(C1,Times(CI,a,c)),Times(CI,b,c,x)),Times(C1D2,CI,n)),Power(Power(Plus(C1,Times(CI,a,c),Times(CI,b,c,x)),Times(C1D2,CI,n)),CN1)),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(5094,Integrate(Times(Exp(Times(ArcTan(Times(c_DEFAULT,Plus(a_,Times(b_DEFAULT,x_)))),n_)),Power(x_,m_)),x_Symbol),
    Condition(Dist(Times(C4,Power(Times(Power(CI,m),n,Power(b,Plus(m,C1)),Power(c,Plus(m,C1))),CN1)),Subst(Integrate(Times(Power(x,Times(C2,Power(Times(CI,n),CN1))),Power(Subtract(Subtract(C1,Times(CI,a,c)),Times(Plus(C1,Times(CI,a,c)),Power(x,Times(C2,Power(Times(CI,n),CN1))))),m),Power(Power(Plus(C1,Power(x,Times(C2,Power(Times(CI,n),CN1)))),Plus(m,C2)),CN1)),x),x,Times(Power(Subtract(C1,Times(CI,c,Plus(a,Times(b,x)))),Times(C1D2,CI,n)),Power(Power(Plus(C1,Times(CI,c,Plus(a,Times(b,x)))),Times(C1D2,CI,n)),CN1))),x),And(FreeQ(List(a,b,c),x),ILtQ(m,C0),LtQ(CN1,Times(CI,n),C1)))),
IIntegrate(5095,Integrate(Times(Exp(Times(ArcTan(Times(c_DEFAULT,Plus(a_,Times(b_DEFAULT,x_)))),n_DEFAULT)),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Plus(d,Times(e,x)),m),Power(Subtract(Subtract(C1,Times(CI,a,c)),Times(CI,b,c,x)),Times(C1D2,CI,n)),Power(Power(Plus(C1,Times(CI,a,c),Times(CI,b,c,x)),Times(C1D2,CI,n)),CN1)),x),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(5096,Integrate(Times(Exp(Times(ArcTan(Plus(a_,Times(b_DEFAULT,x_))),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_),Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Times(c,Power(Plus(C1,Sqr(a)),CN1)),p),Integrate(Times(u,Power(Subtract(Subtract(C1,Times(CI,a)),Times(CI,b,x)),Plus(p,Times(C1D2,CI,n))),Power(Plus(C1,Times(CI,a),Times(CI,b,x)),Subtract(p,Times(C1D2,CI,n)))),x),x),And(FreeQ(List(a,b,c,d,e,n,p),x),EqQ(Times(b,d),Times(C2,a,e)),EqQ(Subtract(Times(Sqr(b),c),Times(e,Plus(C1,Sqr(a)))),C0),Or(IntegerQ(p),GtQ(Times(c,Power(Plus(C1,Sqr(a)),CN1)),C0))))),
IIntegrate(5097,Integrate(Times(Exp(Times(ArcTan(Plus(a_,Times(b_DEFAULT,x_))),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_),Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(Plus(c,Times(d,x),Times(e,Sqr(x))),p),Power(Power(Plus(C1,Sqr(a),Times(C2,a,b,x),Times(Sqr(b),Sqr(x))),p),CN1)),Integrate(Times(u,Power(Plus(C1,Sqr(a),Times(C2,a,b,x),Times(Sqr(b),Sqr(x))),p),Exp(Times(n,ArcTan(Times(a,x))))),x),x),And(FreeQ(List(a,b,c,d,e,n,p),x),EqQ(Times(b,d),Times(C2,a,e)),EqQ(Subtract(Times(Sqr(b),c),Times(e,Plus(C1,Sqr(a)))),C0),Not(Or(IntegerQ(p),GtQ(Times(c,Power(Plus(C1,Sqr(a)),CN1)),C0)))))),
IIntegrate(5098,Integrate(Times(Exp(Times(ArcTan(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),CN1))),n_DEFAULT)),u_DEFAULT),x_Symbol),
    Condition(Integrate(Times(u,Exp(Times(n,ArcCot(Plus(Times(a,Power(c,CN1)),Times(b,x,Power(c,CN1))))))),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(5099,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_)),u_DEFAULT),x_Symbol),
    Condition(Dist(Power(CN1,Times(C1D2,CI,n)),Integrate(Times(u,Power(Exp(Times(n,ArcTan(Times(a,x)))),CN1)),x),x),And(FreeQ(a,x),IntegerQ(Times(C1D2,CI,n))))),
IIntegrate(5100,Integrate(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Subtract(C1,Times(CI,x,Power(a,CN1))),Times(C1D2,Plus(Times(CI,n),C1))),Power(Times(Sqr(x),Power(Plus(C1,Times(CI,x,Power(a,CN1))),Times(C1D2,Subtract(Times(CI,n),C1))),Sqrt(Plus(C1,Times(Sqr(x),Power(a,CN2))))),CN1)),x),x,Power(x,CN1))),And(FreeQ(a,x),IntegerQ(Times(C1D2,Subtract(Times(CI,n),C1))))))
  );
}
