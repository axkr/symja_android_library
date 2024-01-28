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
class IntRules280 { 
  public static IAST RULES = List( 
IIntegrate(5601,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),x_,Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcTan(Times(a,x)))),Power(Times(C2,d,Plus(p,C1)),CN1)),x),Simp(Star(Times(a,c,n,Power(Times(C2,d,Plus(p,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,Sqr(x))),p),Exp(Times(n,ArcTan(Times(a,x))))),x)),x)),And(FreeQ(List(a,c,d,n),x),EqQ(d,Times(Sqr(a),c)),LtQ(p,CN1),Not(IntegerQ(Times(CI,n))),IntegerQ(Times(C2,p))))),
IIntegrate(5602,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),Sqr(x_),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Times(CN1,Subtract(C1,Times(a,n,x)),Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcTan(Times(a,x)))),Power(Times(a,d,n,Plus(Sqr(n),C1)),CN1)),x),And(FreeQ(List(a,c,d,n),x),EqQ(d,Times(Sqr(a),c)),EqQ(Subtract(Sqr(n),Times(C2,Plus(p,C1))),C0),Not(IntegerQ(Times(CI,n)))))),
IIntegrate(5603,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),Sqr(x_),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Subtract(n,Times(C2,Plus(p,C1),a,x)),Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcTan(Times(a,x)))),Power(Times(a,d,Plus(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),CN1)),x),Simp(Star(Times(Subtract(Sqr(n),Times(C2,Plus(p,C1))),Power(Times(d,Plus(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),CN1)),Integrate(Times(Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcTan(Times(a,x))))),x)),x)),And(FreeQ(List(a,c,d,n),x),EqQ(d,Times(Sqr(a),c)),LtQ(p,CN1),Not(IntegerQ(Times(CI,n))),NeQ(Plus(Sqr(n),Times(C4,Sqr(Plus(p,C1)))),C0),IntegerQ(Times(C2,p))))),
IIntegrate(5604,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_)),Power(x_,m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(c,p),Integrate(Times(Power(x,m),Power(Plus(C1,Times(Sqr(a),Sqr(x))),Subtract(p,Times(CI,C1D2,n))),Power(Subtract(C1,Times(CI,a,x)),Times(CI,n))),x)),x),And(FreeQ(List(a,c,d,m,p),x),EqQ(d,Times(Sqr(a),c)),Or(IntegerQ(p),GtQ(c,C0)),IntegerQ(Times(C1D2,Plus(Times(CI,n),C1))),Not(IntegerQ(Subtract(p,Times(CI,C1D2,n))))))),
IIntegrate(5605,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(x_,m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(c,p),Integrate(Times(Power(x,m),Power(Subtract(C1,Times(CI,a,x)),Plus(p,Times(CI,C1D2,n))),Power(Plus(C1,Times(CI,a,x)),Subtract(p,Times(CI,C1D2,n)))),x)),x),And(FreeQ(List(a,c,d,m,n,p),x),EqQ(d,Times(Sqr(a),c)),Or(IntegerQ(p),GtQ(c,C0))))),
IIntegrate(5606,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_)),Power(x_,m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Star(Power(c,Times(CI,C1D2,n)),Integrate(Times(Power(x,m),Power(Plus(c,Times(d,Sqr(x))),Subtract(p,Times(CI,C1D2,n))),Power(Subtract(C1,Times(CI,a,x)),Times(CI,n))),x)),x),And(FreeQ(List(a,c,d,m,p),x),EqQ(d,Times(Sqr(a),c)),Not(Or(IntegerQ(p),GtQ(c,C0))),IGtQ(Times(CI,C1D2,n),C0)))),
IIntegrate(5607,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_)),Power(x_,m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Star(Power(Power(c,Times(CI,C1D2,n)),CN1),Integrate(Times(Power(x,m),Power(Plus(c,Times(d,Sqr(x))),Plus(p,Times(CI,C1D2,n))),Power(Power(Plus(C1,Times(CI,a,x)),Times(CI,n)),CN1)),x)),x),And(FreeQ(List(a,c,d,m,p),x),EqQ(d,Times(Sqr(a),c)),Not(Or(IntegerQ(p),GtQ(c,C0))),ILtQ(Times(CI,C1D2,n),C0)))),
IIntegrate(5608,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(x_,m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(c,IntPart(p)),Power(Plus(c,Times(d,Sqr(x))),FracPart(p)),Power(Power(Plus(C1,Times(Sqr(a),Sqr(x))),FracPart(p)),CN1)),Integrate(Times(Power(x,m),Power(Plus(C1,Times(Sqr(a),Sqr(x))),p),Exp(Times(n,ArcTan(Times(a,x))))),x)),x),And(FreeQ(List(a,c,d,m,n,p),x),EqQ(d,Times(Sqr(a),c)),Not(Or(IntegerQ(p),GtQ(c,C0)))))),
IIntegrate(5609,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),u_,Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(c,p),Integrate(Times(u,Power(Subtract(C1,Times(CI,a,x)),Plus(p,Times(CI,C1D2,n))),Power(Plus(C1,Times(CI,a,x)),Subtract(p,Times(CI,C1D2,n)))),x)),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(d,Times(Sqr(a),c)),Or(IntegerQ(p),GtQ(c,C0))))),
IIntegrate(5610,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_)),u_,Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(c,IntPart(p)),Power(Plus(c,Times(d,Sqr(x))),FracPart(p)),Power(Times(Power(Subtract(C1,Times(CI,a,x)),FracPart(p)),Power(Plus(C1,Times(CI,a,x)),FracPart(p))),CN1)),Integrate(Times(u,Power(Subtract(C1,Times(CI,a,x)),Plus(p,Times(CI,C1D2,n))),Power(Plus(C1,Times(CI,a,x)),Subtract(p,Times(CI,C1D2,n)))),x)),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(d,Times(Sqr(a),c)),Or(IntegerQ(p),GtQ(c,C0)),IntegerQ(Times(CI,C1D2,n))))),
IIntegrate(5611,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),u_,Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(c,IntPart(p)),Power(Plus(c,Times(d,Sqr(x))),FracPart(p)),Power(Power(Plus(C1,Times(Sqr(a),Sqr(x))),FracPart(p)),CN1)),Integrate(Times(u,Power(Plus(C1,Times(Sqr(a),Sqr(x))),p),Exp(Times(n,ArcTan(Times(a,x))))),x)),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(d,Times(Sqr(a),c)),Not(Or(IntegerQ(p),GtQ(c,C0))),Not(IntegerQ(Times(CI,C1D2,n)))))),
IIntegrate(5612,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN2))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(d,p),Integrate(Times(u,Power(Power(x,Times(C2,p)),CN1),Power(Plus(C1,Times(Sqr(a),Sqr(x))),p),Exp(Times(n,ArcTan(Times(a,x))))),x)),x),And(FreeQ(List(a,c,d,n),x),EqQ(Subtract(c,Times(Sqr(a),d)),C0),IntegerQ(p)))),
IIntegrate(5613,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN2))),p_)),x_Symbol),
    Condition(Simp(Star(Power(c,p),Integrate(Times(u,Power(Subtract(C1,Times(CI,Power(Times(a,x),CN1))),p),Power(Plus(C1,Times(CI,Power(Times(a,x),CN1))),p),Exp(Times(n,ArcTan(Times(a,x))))),x)),x),And(FreeQ(List(a,c,d,p),x),EqQ(Subtract(c,Times(Sqr(a),d)),C0),Not(IntegerQ(p)),IntegerQ(Times(CI,C1D2,n)),GtQ(c,C0)))),
IIntegrate(5614,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN2))),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(x,Times(C2,p)),Power(Plus(c,Times(d,Power(x,CN2))),p),Power(Power(Plus(C1,Times(Sqr(a),Sqr(x))),p),CN1)),Integrate(Times(u,Power(Plus(C1,Times(Sqr(a),Sqr(x))),Plus(Times(CI,C1D2,n),p)),Power(Times(Power(x,Times(C2,p)),Power(Plus(C1,Times(CI,a,x)),Times(CI,n))),CN1)),x)),x),And(FreeQ(List(a,c,d,p),x),EqQ(Subtract(c,Times(Sqr(a),d)),C0),Not(IntegerQ(p)),IntegerQ(Times(CI,C1D2,n)),Not(GtQ(c,C0))))),
IIntegrate(5615,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN2))),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(x,Times(C2,p)),Power(Plus(c,Times(d,Power(x,CN2))),p),Power(Power(Plus(C1,Times(Sqr(a),Sqr(x))),p),CN1)),Integrate(Times(u,Power(Power(x,Times(C2,p)),CN1),Power(Plus(C1,Times(Sqr(a),Sqr(x))),p),Exp(Times(n,ArcTan(Times(a,x))))),x)),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Subtract(c,Times(Sqr(a),d)),C0),Not(IntegerQ(p)),Not(IntegerQ(Times(CI,C1D2,n)))))),
IIntegrate(5616,Integrate(Exp(Times(ArcTan(Times(c_DEFAULT,Plus(a_,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Subtract(Subtract(C1,Times(CI,a,c)),Times(CI,b,c,x)),Times(CI,C1D2,n)),Power(Power(Plus(C1,Times(CI,a,c),Times(CI,b,c,x)),Times(CI,C1D2,n)),CN1)),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(5617,Integrate(Times(Exp(Times(ArcTan(Times(c_DEFAULT,Plus(a_,Times(b_DEFAULT,x_)))),n_)),Power(x_,m_)),x_Symbol),
    Condition(Simp(Star(Times(C4,Power(Times(Power(CI,m),n,Power(b,Plus(m,C1)),Power(c,Plus(m,C1))),CN1)),Subst(Integrate(Times(Power(x,Times(C2,Power(Times(CI,n),CN1))),Power(Subtract(Subtract(C1,Times(CI,a,c)),Times(Plus(C1,Times(CI,a,c)),Power(x,Times(C2,Power(Times(CI,n),CN1))))),m),Power(Power(Plus(C1,Power(x,Times(C2,Power(Times(CI,n),CN1)))),Plus(m,C2)),CN1)),x),x,Times(Power(Subtract(C1,Times(CI,c,Plus(a,Times(b,x)))),Times(CI,C1D2,n)),Power(Power(Plus(C1,Times(CI,c,Plus(a,Times(b,x)))),Times(CI,C1D2,n)),CN1)))),x),And(FreeQ(list(a,b,c),x),ILtQ(m,C0),LtQ(CN1,Times(CI,n),C1)))),
IIntegrate(5618,Integrate(Times(Exp(Times(ArcTan(Times(c_DEFAULT,Plus(a_,Times(b_DEFAULT,x_)))),n_DEFAULT)),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Plus(d,Times(e,x)),m),Power(Subtract(Subtract(C1,Times(CI,a,c)),Times(CI,b,c,x)),Times(CI,C1D2,n)),Power(Power(Plus(C1,Times(CI,a,c),Times(CI,b,c,x)),Times(CI,C1D2,n)),CN1)),x),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(5619,Integrate(Times(Exp(Times(ArcTan(Plus(a_,Times(b_DEFAULT,x_))),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_),Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Times(c,Power(Plus(C1,Sqr(a)),CN1)),p),Integrate(Times(u,Power(Subtract(Subtract(C1,Times(CI,a)),Times(CI,b,x)),Plus(p,Times(CI,C1D2,n))),Power(Plus(C1,Times(CI,a),Times(CI,b,x)),Subtract(p,Times(CI,C1D2,n)))),x)),x),And(FreeQ(List(a,b,c,d,e,n,p),x),EqQ(Times(b,d),Times(C2,a,e)),EqQ(Subtract(Times(Sqr(b),c),Times(e,Plus(C1,Sqr(a)))),C0),Or(IntegerQ(p),GtQ(Times(c,Power(Plus(C1,Sqr(a)),CN1)),C0))))),
IIntegrate(5620,Integrate(Times(Exp(Times(ArcTan(Plus(a_,Times(b_DEFAULT,x_))),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_),Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(Plus(c,Times(d,x),Times(e,Sqr(x))),p),Power(Power(Plus(C1,Sqr(a),Times(C2,a,b,x),Times(Sqr(b),Sqr(x))),p),CN1)),Integrate(Times(u,Power(Plus(C1,Sqr(a),Times(C2,a,b,x),Times(Sqr(b),Sqr(x))),p),Exp(Times(n,ArcTan(Times(a,x))))),x)),x),And(FreeQ(List(a,b,c,d,e,n,p),x),EqQ(Times(b,d),Times(C2,a,e)),EqQ(Subtract(Times(Sqr(b),c),Times(e,Plus(C1,Sqr(a)))),C0),Not(Or(IntegerQ(p),GtQ(Times(c,Power(Plus(C1,Sqr(a)),CN1)),C0))))))
  );
}
