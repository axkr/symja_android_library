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
class IntRules253 { 
  public static IAST RULES = List( 
IIntegrate(5061,Integrate(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Subtract(C1,Times(CI,a,x)),Times(C1D2,CI,n)),Power(Power(Plus(C1,Times(CI,a,x)),Times(C1D2,CI,n)),CN1)),x),And(FreeQ(list(a,n),x),Not(IntegerQ(Times(C1D2,Subtract(Times(CI,n),C1))))))),
IIntegrate(5062,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(x,m),Power(Subtract(C1,Times(CI,a,x)),Times(C1D2,CI,n)),Power(Power(Plus(C1,Times(CI,a,x)),Times(C1D2,CI,n)),CN1)),x),And(FreeQ(list(a,m,n),x),Not(IntegerQ(Times(C1D2,Subtract(Times(CI,n),C1))))))),
IIntegrate(5063,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_)),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(c,p),Integrate(Times(u,Power(Plus(C1,Times(d,x,Power(c,CN1))),p),Power(Subtract(C1,Times(CI,a,x)),Times(C1D2,CI,n)),Power(Power(Plus(C1,Times(CI,a,x)),Times(C1D2,CI,n)),CN1)),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(Times(Sqr(a),Sqr(c)),Sqr(d)),C0),Or(IntegerQ(p),GtQ(c,C0))))),
IIntegrate(5064,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_)),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(u,Power(Plus(c,Times(d,x)),p),Power(Subtract(C1,Times(CI,a,x)),Times(C1D2,CI,n)),Power(Power(Plus(C1,Times(CI,a,x)),Times(C1D2,CI,n)),CN1)),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(Times(Sqr(a),Sqr(c)),Sqr(d)),C0),Not(Or(IntegerQ(p),GtQ(c,C0)))))),
IIntegrate(5065,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(d,p),Integrate(Times(u,Power(Plus(C1,Times(c,x,Power(d,CN1))),p),Exp(Times(n,ArcTan(Times(a,x)))),Power(Power(x,p),CN1)),x),x),And(FreeQ(List(a,c,d,n),x),EqQ(Plus(Sqr(c),Times(Sqr(a),Sqr(d))),C0),IntegerQ(p)))),
IIntegrate(5066,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_)),x_Symbol),
    Condition(Dist(Times(Power(CN1,Times(C1D2,n)),Power(c,p)),Integrate(Times(u,Power(Plus(C1,Times(d,Power(Times(c,x),CN1))),p),Power(Subtract(C1,Power(Times(CI,a,x),CN1)),Times(C1D2,CI,n)),Power(Power(Plus(C1,Power(Times(CI,a,x),CN1)),Times(C1D2,CI,n)),CN1)),x),x),And(FreeQ(List(a,c,d,p),x),EqQ(Plus(Sqr(c),Times(Sqr(a),Sqr(d))),C0),Not(IntegerQ(p)),IntegerQ(Times(C1D2,CI,n)),GtQ(c,C0)))),
IIntegrate(5067,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_)),x_Symbol),
    Condition(Integrate(Times(u,Power(Plus(c,Times(d,Power(x,CN1))),p),Power(Subtract(C1,Times(CI,a,x)),Times(C1D2,CI,n)),Power(Power(Plus(C1,Times(CI,a,x)),Times(C1D2,CI,n)),CN1)),x),And(FreeQ(List(a,c,d,p),x),EqQ(Plus(Sqr(c),Times(Sqr(a),Sqr(d))),C0),Not(IntegerQ(p)),IntegerQ(Times(C1D2,CI,n)),Not(GtQ(c,C0))))),
IIntegrate(5068,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_)),x_Symbol),
    Condition(Dist(Times(Power(x,p),Power(Plus(c,Times(d,Power(x,CN1))),p),Power(Power(Plus(C1,Times(c,x,Power(d,CN1))),p),CN1)),Integrate(Times(u,Power(Plus(C1,Times(c,x,Power(d,CN1))),p),Exp(Times(n,ArcTan(Times(a,x)))),Power(Power(x,p),CN1)),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(Sqr(c),Times(Sqr(a),Sqr(d))),C0),Not(IntegerQ(p))))),
IIntegrate(5069,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),QQ(-3L,2L))),x_Symbol),
    Condition(Simp(Times(Plus(n,Times(a,x)),Exp(Times(n,ArcTan(Times(a,x)))),Power(Times(a,c,Plus(Sqr(n),C1),Sqrt(Plus(c,Times(d,Sqr(x))))),CN1)),x),And(FreeQ(List(a,c,d,n),x),EqQ(d,Times(Sqr(a),c)),Not(IntegerQ(Times(CI,n)))))),
IIntegrate(5070,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(Subtract(n,Times(C2,a,Plus(p,C1),x)),Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcTan(Times(a,x)))),Power(Times(a,c,Plus(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),CN1)),x),Dist(Times(C2,Plus(p,C1),Plus(Times(C2,p),C3),Power(Times(c,Plus(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),CN1)),Integrate(Times(Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcTan(Times(a,x))))),x),x)),And(FreeQ(List(a,c,d,n),x),EqQ(d,Times(Sqr(a),c)),LtQ(p,CN1),Not(IntegerQ(Times(CI,n))),NeQ(Plus(Sqr(n),Times(C4,Sqr(Plus(p,C1)))),C0),IntegerQ(Times(C2,p))))),
IIntegrate(5071,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Simp(Times(Exp(Times(n,ArcTan(Times(a,x)))),Power(Times(a,c,n),CN1)),x),And(FreeQ(List(a,c,d,n),x),EqQ(d,Times(Sqr(a),c))))),
IIntegrate(5072,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(c,p),Integrate(Times(Power(Plus(C1,Times(Sqr(a),Sqr(x))),Subtract(p,Times(C1D2,CI,n))),Power(Subtract(C1,Times(CI,a,x)),Times(CI,n))),x),x),And(FreeQ(List(a,c,d,p),x),EqQ(d,Times(Sqr(a),c)),IntegerQ(p),IntegerQ(Times(C1D2,Plus(Times(CI,n),C1))),Not(IntegerQ(Subtract(p,Times(C1D2,CI,n))))))),
IIntegrate(5073,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(c,p),Integrate(Times(Power(Subtract(C1,Times(CI,a,x)),Plus(p,Times(C1D2,CI,n))),Power(Plus(C1,Times(CI,a,x)),Subtract(p,Times(C1D2,CI,n)))),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(d,Times(Sqr(a),c)),Or(IntegerQ(p),GtQ(c,C0))))),
IIntegrate(5074,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Dist(Power(c,Times(C1D2,CI,n)),Integrate(Times(Power(Plus(c,Times(d,Sqr(x))),Subtract(p,Times(C1D2,CI,n))),Power(Subtract(C1,Times(CI,a,x)),Times(CI,n))),x),x),And(FreeQ(List(a,c,d,p),x),EqQ(d,Times(Sqr(a),c)),Not(Or(IntegerQ(p),GtQ(c,C0))),IGtQ(Times(C1D2,CI,n),C0)))),
IIntegrate(5075,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Dist(Power(Power(c,Times(C1D2,CI,n)),CN1),Integrate(Times(Power(Plus(c,Times(d,Sqr(x))),Plus(p,Times(C1D2,CI,n))),Power(Power(Plus(C1,Times(CI,a,x)),Times(CI,n)),CN1)),x),x),And(FreeQ(List(a,c,d,p),x),EqQ(d,Times(Sqr(a),c)),Not(Or(IntegerQ(p),GtQ(c,C0))),ILtQ(Times(C1D2,CI,n),C0)))),
IIntegrate(5076,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Dist(Times(Power(c,IntPart(p)),Power(Plus(c,Times(d,Sqr(x))),FracPart(p)),Power(Power(Plus(C1,Times(Sqr(a),Sqr(x))),FracPart(p)),CN1)),Integrate(Times(Power(Plus(C1,Times(Sqr(a),Sqr(x))),p),Exp(Times(n,ArcTan(Times(a,x))))),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(d,Times(Sqr(a),c)),Not(Or(IntegerQ(p),GtQ(c,C0)))))),
IIntegrate(5077,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),x_,Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),QQ(-3L,2L))),x_Symbol),
    Condition(Negate(Simp(Times(Subtract(C1,Times(a,n,x)),Exp(Times(n,ArcTan(Times(a,x)))),Power(Times(d,Plus(Sqr(n),C1),Sqrt(Plus(c,Times(d,Sqr(x))))),CN1)),x)),And(FreeQ(List(a,c,d,n),x),EqQ(d,Times(Sqr(a),c)),Not(IntegerQ(Times(CI,n)))))),
IIntegrate(5078,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),x_,Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcTan(Times(a,x)))),Power(Times(C2,d,Plus(p,C1)),CN1)),x),Dist(Times(a,c,n,Power(Times(C2,d,Plus(p,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,Sqr(x))),p),Exp(Times(n,ArcTan(Times(a,x))))),x),x)),And(FreeQ(List(a,c,d,n),x),EqQ(d,Times(Sqr(a),c)),LtQ(p,CN1),Not(IntegerQ(Times(CI,n))),IntegerQ(Times(C2,p))))),
IIntegrate(5079,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),Sqr(x_),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Negate(Simp(Times(Subtract(C1,Times(a,n,x)),Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcTan(Times(a,x)))),Power(Times(a,d,n,Plus(Sqr(n),C1)),CN1)),x)),And(FreeQ(List(a,c,d,n),x),EqQ(d,Times(Sqr(a),c)),EqQ(Subtract(Sqr(n),Times(C2,Plus(p,C1))),C0),Not(IntegerQ(Times(CI,n)))))),
IIntegrate(5080,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),Sqr(x_),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Subtract(n,Times(C2,Plus(p,C1),a,x)),Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcTan(Times(a,x)))),Power(Times(a,d,Plus(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),CN1)),x)),Dist(Times(Subtract(Sqr(n),Times(C2,Plus(p,C1))),Power(Times(d,Plus(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),CN1)),Integrate(Times(Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcTan(Times(a,x))))),x),x)),And(FreeQ(List(a,c,d,n),x),EqQ(d,Times(Sqr(a),c)),LtQ(p,CN1),Not(IntegerQ(Times(CI,n))),NeQ(Plus(Sqr(n),Times(C4,Sqr(Plus(p,C1)))),C0),IntegerQ(Times(C2,p)))))
  );
}
