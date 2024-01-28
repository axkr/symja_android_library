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
class IntRules334 { 
  public static IAST RULES = List( 
IIntegrate(6681,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(d,p),Integrate(Times(u,Power(Plus(C1,Times(c,x,Power(d,CN1))),p),Exp(Times(n,ArcTanh(Times(a,x)))),Power(Power(x,p),CN1)),x)),x),And(FreeQ(List(a,c,d,n),x),EqQ(Subtract(Sqr(c),Times(Sqr(a),Sqr(d))),C0),IntegerQ(p)))),
IIntegrate(6682,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(CN1,Times(C1D2,n)),Power(c,p)),Integrate(Times(u,Power(Plus(C1,Times(d,Power(Times(c,x),CN1))),p),Power(Plus(C1,Power(Times(a,x),CN1)),Times(C1D2,n)),Power(Power(Subtract(C1,Power(Times(a,x),CN1)),Times(C1D2,n)),CN1)),x)),x),And(FreeQ(List(a,c,d,p),x),EqQ(Subtract(Sqr(c),Times(Sqr(a),Sqr(d))),C0),Not(IntegerQ(p)),IntegerQ(Times(C1D2,n)),GtQ(c,C0)))),
IIntegrate(6683,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_)),x_Symbol),
    Condition(Integrate(Times(u,Power(Plus(c,Times(d,Power(x,CN1))),p),Power(Plus(C1,Times(a,x)),Times(C1D2,n)),Power(Power(Subtract(C1,Times(a,x)),Times(C1D2,n)),CN1)),x),And(FreeQ(List(a,c,d,p),x),EqQ(Subtract(Sqr(c),Times(Sqr(a),Sqr(d))),C0),Not(IntegerQ(p)),IntegerQ(Times(C1D2,n)),Not(GtQ(c,C0))))),
IIntegrate(6684,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(x,p),Power(Plus(c,Times(d,Power(x,CN1))),p),Power(Power(Plus(C1,Times(c,x,Power(d,CN1))),p),CN1)),Integrate(Times(u,Power(Plus(C1,Times(c,x,Power(d,CN1))),p),Exp(Times(n,ArcTanh(Times(a,x)))),Power(Power(x,p),CN1)),x)),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Subtract(Sqr(c),Times(Sqr(a),Sqr(d))),C0),Not(IntegerQ(p))))),
IIntegrate(6685,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),QQ(-3L,2L))),x_Symbol),
    Condition(Simp(Times(Subtract(n,Times(a,x)),Exp(Times(n,ArcTanh(Times(a,x)))),Power(Times(a,c,Subtract(Sqr(n),C1),Sqrt(Plus(c,Times(d,Sqr(x))))),CN1)),x),And(FreeQ(List(a,c,d,n),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Not(IntegerQ(n))))),
IIntegrate(6686,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(n,Times(C2,a,Plus(p,C1),x)),Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcTanh(Times(a,x)))),Power(Times(a,c,Subtract(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),CN1)),x),Simp(Star(Times(C2,Plus(p,C1),Plus(Times(C2,p),C3),Power(Times(c,Subtract(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),CN1)),Integrate(Times(Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcTanh(Times(a,x))))),x)),x)),And(FreeQ(List(a,c,d,n),x),EqQ(Plus(Times(Sqr(a),c),d),C0),LtQ(p,CN1),Not(IntegerQ(n)),NeQ(Subtract(Sqr(n),Times(C4,Sqr(Plus(p,C1)))),C0),IntegerQ(Times(C2,p))))),
IIntegrate(6687,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Simp(Times(Exp(Times(n,ArcTanh(Times(a,x)))),Power(Times(a,c,n),CN1)),x),And(FreeQ(List(a,c,d,n),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Not(IntegerQ(Times(C1D2,n)))))),
IIntegrate(6688,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(c,p),Integrate(Times(Power(Subtract(C1,Times(Sqr(a),Sqr(x))),Subtract(p,Times(C1D2,n))),Power(Plus(C1,Times(a,x)),n)),x)),x),And(FreeQ(List(a,c,d,p),x),EqQ(Plus(Times(Sqr(a),c),d),C0),IntegerQ(p),IGtQ(Times(C1D2,Plus(n,C1)),C0),Not(IntegerQ(Subtract(p,Times(C1D2,n))))))),
IIntegrate(6689,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(c,p),Integrate(Times(Power(Subtract(C1,Times(Sqr(a),Sqr(x))),Plus(p,Times(C1D2,n))),Power(Power(Subtract(C1,Times(a,x)),n),CN1)),x)),x),And(FreeQ(List(a,c,d,p),x),EqQ(Plus(Times(Sqr(a),c),d),C0),IntegerQ(p),ILtQ(Times(C1D2,Subtract(n,C1)),C0),Not(IntegerQ(Subtract(p,Times(C1D2,n))))))),
IIntegrate(6690,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(c,p),Integrate(Times(Power(Subtract(C1,Times(a,x)),Subtract(p,Times(C1D2,n))),Power(Plus(C1,Times(a,x)),Plus(p,Times(C1D2,n)))),x)),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Or(IntegerQ(p),GtQ(c,C0))))),
IIntegrate(6691,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(c,Times(C1D2,n)),Integrate(Times(Power(Plus(c,Times(d,Sqr(x))),Subtract(p,Times(C1D2,n))),Power(Plus(C1,Times(a,x)),n)),x)),x),And(FreeQ(List(a,c,d,p),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Not(Or(IntegerQ(p),GtQ(c,C0))),IGtQ(Times(C1D2,n),C0)))),
IIntegrate(6692,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Power(c,Times(C1D2,n)),CN1),Integrate(Times(Power(Plus(c,Times(d,Sqr(x))),Plus(p,Times(C1D2,n))),Power(Power(Subtract(C1,Times(a,x)),n),CN1)),x)),x),And(FreeQ(List(a,c,d,p),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Not(Or(IntegerQ(p),GtQ(c,C0))),ILtQ(Times(C1D2,n),C0)))),
IIntegrate(6693,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(c,IntPart(p)),Power(Plus(c,Times(d,Sqr(x))),FracPart(p)),Power(Power(Subtract(C1,Times(Sqr(a),Sqr(x))),FracPart(p)),CN1)),Integrate(Times(Power(Subtract(C1,Times(Sqr(a),Sqr(x))),p),Exp(Times(n,ArcTanh(Times(a,x))))),x)),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Not(Or(IntegerQ(p),GtQ(c,C0)))))),
IIntegrate(6694,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),x_,Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),QQ(-3L,2L))),x_Symbol),
    Condition(Simp(Times(Subtract(C1,Times(a,n,x)),Exp(Times(n,ArcTanh(Times(a,x)))),Power(Times(d,Subtract(Sqr(n),C1),Sqrt(Plus(c,Times(d,Sqr(x))))),CN1)),x),And(FreeQ(List(a,c,d,n),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Not(IntegerQ(n))))),
IIntegrate(6695,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),x_,Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcTanh(Times(a,x)))),Power(Times(C2,d,Plus(p,C1)),CN1)),x),Simp(Star(Times(a,c,n,Power(Times(C2,d,Plus(p,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,Sqr(x))),p),Exp(Times(n,ArcTanh(Times(a,x))))),x)),x)),And(FreeQ(List(a,c,d,n),x),EqQ(Plus(Times(Sqr(a),c),d),C0),LtQ(p,CN1),Not(IntegerQ(n)),IntegerQ(Times(C2,p))))),
IIntegrate(6696,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),Sqr(x_),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Times(Subtract(C1,Times(a,n,x)),Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcTanh(Times(a,x)))),Power(Times(a,d,n,Subtract(Sqr(n),C1)),CN1)),x),And(FreeQ(List(a,c,d,n),x),EqQ(Plus(Times(Sqr(a),c),d),C0),EqQ(Plus(Sqr(n),Times(C2,Plus(p,C1))),C0),Not(IntegerQ(n))))),
IIntegrate(6697,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),Sqr(x_),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Plus(n,Times(C2,Plus(p,C1),a,x)),Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcTanh(Times(a,x)))),Power(Times(a,d,Subtract(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),CN1)),x),Simp(Star(Times(Plus(Sqr(n),Times(C2,Plus(p,C1))),Power(Times(d,Subtract(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),CN1)),Integrate(Times(Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcTanh(Times(a,x))))),x)),x)),And(FreeQ(List(a,c,d,n),x),EqQ(Plus(Times(Sqr(a),c),d),C0),LtQ(p,CN1),Not(IntegerQ(n)),NeQ(Subtract(Sqr(n),Times(C4,Sqr(Plus(p,C1)))),C0),IntegerQ(Times(C2,p))))),
IIntegrate(6698,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(x_,m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(c,p),Integrate(Times(Power(x,m),Power(Subtract(C1,Times(Sqr(a),Sqr(x))),Subtract(p,Times(C1D2,n))),Power(Plus(C1,Times(a,x)),n)),x)),x),And(FreeQ(List(a,c,d,m,p),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Or(IntegerQ(p),GtQ(c,C0)),IGtQ(Times(C1D2,Plus(n,C1)),C0),Not(IntegerQ(Subtract(p,Times(C1D2,n))))))),
IIntegrate(6699,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),Power(x_,m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(c,p),Integrate(Times(Power(x,m),Power(Subtract(C1,Times(Sqr(a),Sqr(x))),Plus(p,Times(C1D2,n))),Power(Power(Subtract(C1,Times(a,x)),n),CN1)),x)),x),And(FreeQ(List(a,c,d,m,p),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Or(IntegerQ(p),GtQ(c,C0)),ILtQ(Times(C1D2,Subtract(n,C1)),C0),Not(IntegerQ(Subtract(p,Times(C1D2,n))))))),
IIntegrate(6700,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(x_,m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(c,p),Integrate(Times(Power(x,m),Power(Subtract(C1,Times(a,x)),Subtract(p,Times(C1D2,n))),Power(Plus(C1,Times(a,x)),Plus(p,Times(C1D2,n)))),x)),x),And(FreeQ(List(a,c,d,m,n,p),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Or(IntegerQ(p),GtQ(c,C0)))))
  );
}
