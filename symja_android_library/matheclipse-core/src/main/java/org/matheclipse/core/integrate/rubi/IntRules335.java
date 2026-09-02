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
class IntRules335 { 
  public static IAST RULES = List( 
IIntegrate(6701,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(c,p),Integrate(Times(Power(Subtract(C1,Times(Sqr(a),Sqr(x))),Plus(p,Times(C1D2,n))),Power(Power(Subtract(C1,Times(a,x)),n),CN1)),x),x),x),And(FreeQ(List(a,c,d,p),x),EqQ(Plus(Times(Sqr(a),c),d),C0),IntegerQ(p),ILtQ(Times(C1D2,Plus(n,CN1)),C0),Not(IntegerQ(Subtract(p,Times(C1D2,n))))))),
IIntegrate(6702,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(c,p),Integrate(Times(Power(Subtract(C1,Times(a,x)),Subtract(p,Times(C1D2,n))),Power(Plus(C1,Times(a,x)),Plus(p,Times(C1D2,n)))),x),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Or(IntegerQ(p),GtQ(c,C0))))),
IIntegrate(6703,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(c,Times(C1D2,n)),Integrate(Times(Power(Plus(c,Times(d,Sqr(x))),Subtract(p,Times(C1D2,n))),Power(Plus(C1,Times(a,x)),n)),x),x),x),And(FreeQ(List(a,c,d,p),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Not(Or(IntegerQ(p),GtQ(c,C0))),IGtQ(Times(C1D2,n),C0)))),
IIntegrate(6704,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Power(c,Times(C1D2,n)),CN1),Integrate(Times(Power(Plus(c,Times(d,Sqr(x))),Plus(p,Times(C1D2,n))),Power(Power(Subtract(C1,Times(a,x)),n),CN1)),x),x),x),And(FreeQ(List(a,c,d,p),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Not(Or(IntegerQ(p),GtQ(c,C0))),ILtQ(Times(C1D2,n),C0)))),
IIntegrate(6705,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(c,IntPart(p)),Power(Plus(c,Times(d,Sqr(x))),FracPart(p)),Power(Power(Subtract(C1,Times(Sqr(a),Sqr(x))),FracPart(p)),CN1)),Integrate(Times(Power(Subtract(C1,Times(Sqr(a),Sqr(x))),p),Exp(Times(n,ArcTanh(Times(a,x))))),x),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Not(Or(IntegerQ(p),GtQ(c,C0)))))),
IIntegrate(6706,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),x_,Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),QQ(-3L,2L))),x_Symbol),
    Condition(Simp(Times(Subtract(C1,Times(a,n,x)),Exp(Times(n,ArcTanh(Times(a,x)))),Power(Times(d,Plus(Sqr(n),CN1),Sqrt(Plus(c,Times(d,Sqr(x))))),CN1)),x),And(FreeQ(List(a,c,d,n),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Not(IntegerQ(n))))),
IIntegrate(6707,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),x_,Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcTanh(Times(a,x)))),Power(Times(C2,d,Plus(p,C1)),CN1)),x),Simp(Dist(Times(a,c,n,Power(Times(C2,d,Plus(p,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,Sqr(x))),p),Exp(Times(n,ArcTanh(Times(a,x))))),x),x),x)),And(FreeQ(List(a,c,d,n),x),EqQ(Plus(Times(Sqr(a),c),d),C0),LtQ(p,CN1),Not(IntegerQ(n)),IntegerQ(Times(C2,p))))),
IIntegrate(6708,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),Sqr(x_),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Times(Subtract(C1,Times(a,n,x)),Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcTanh(Times(a,x)))),Power(Times(a,d,n,Plus(Sqr(n),CN1)),CN1)),x),And(FreeQ(List(a,c,d,n),x),EqQ(Plus(Times(Sqr(a),c),d),C0),EqQ(Plus(Sqr(n),Times(C2,Plus(p,C1))),C0),Not(IntegerQ(n))))),
IIntegrate(6709,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),Sqr(x_),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Plus(n,Times(C2,Plus(p,C1),a,x)),Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcTanh(Times(a,x)))),Power(Times(a,d,Subtract(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),CN1)),x),Simp(Dist(Times(Plus(Sqr(n),Times(C2,Plus(p,C1))),Power(Times(d,Subtract(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),CN1)),Integrate(Times(Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcTanh(Times(a,x))))),x),x),x)),And(FreeQ(List(a,c,d,n),x),EqQ(Plus(Times(Sqr(a),c),d),C0),LtQ(p,CN1),Not(IntegerQ(n)),NeQ(Subtract(Sqr(n),Times(C4,Sqr(Plus(p,C1)))),C0),IntegerQ(Times(C2,p))))),
IIntegrate(6710,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(x_,m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(c,p),Integrate(Times(Power(x,m),Power(Subtract(C1,Times(Sqr(a),Sqr(x))),Subtract(p,Times(C1D2,n))),Power(Plus(C1,Times(a,x)),n)),x),x),x),And(FreeQ(List(a,c,d,m,p),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Or(IntegerQ(p),GtQ(c,C0)),IGtQ(Times(C1D2,Plus(n,C1)),C0),Not(IntegerQ(Subtract(p,Times(C1D2,n))))))),
IIntegrate(6711,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),Power(x_,m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(c,p),Integrate(Times(Power(x,m),Power(Subtract(C1,Times(Sqr(a),Sqr(x))),Plus(p,Times(C1D2,n))),Power(Power(Subtract(C1,Times(a,x)),n),CN1)),x),x),x),And(FreeQ(List(a,c,d,m,p),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Or(IntegerQ(p),GtQ(c,C0)),ILtQ(Times(C1D2,Plus(n,CN1)),C0),Not(IntegerQ(Subtract(p,Times(C1D2,n))))))),
IIntegrate(6712,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(x_,m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(c,p),Integrate(Times(Power(x,m),Power(Subtract(C1,Times(a,x)),Subtract(p,Times(C1D2,n))),Power(Plus(C1,Times(a,x)),Plus(p,Times(C1D2,n)))),x),x),x),And(FreeQ(List(a,c,d,m,n,p),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Or(IntegerQ(p),GtQ(c,C0))))),
IIntegrate(6713,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),Power(x_,m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(c,Times(C1D2,n)),Integrate(Times(Power(x,m),Power(Plus(c,Times(d,Sqr(x))),Subtract(p,Times(C1D2,n))),Power(Plus(C1,Times(a,x)),n)),x),x),x),And(FreeQ(List(a,c,d,m,p),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Not(Or(IntegerQ(p),GtQ(c,C0))),IGtQ(Times(C1D2,n),C0)))),
IIntegrate(6714,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),Power(x_,m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Power(c,Times(C1D2,n)),CN1),Integrate(Times(Power(x,m),Power(Plus(c,Times(d,Sqr(x))),Plus(p,Times(C1D2,n))),Power(Power(Subtract(C1,Times(a,x)),n),CN1)),x),x),x),And(FreeQ(List(a,c,d,m,p),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Not(Or(IntegerQ(p),GtQ(c,C0))),ILtQ(Times(C1D2,n),C0)))),
IIntegrate(6715,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(x_,m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(c,IntPart(p)),Power(Plus(c,Times(d,Sqr(x))),FracPart(p)),Power(Power(Subtract(C1,Times(Sqr(a),Sqr(x))),FracPart(p)),CN1)),Integrate(Times(Power(x,m),Power(Subtract(C1,Times(Sqr(a),Sqr(x))),p),Exp(Times(n,ArcTanh(Times(a,x))))),x),x),x),And(FreeQ(List(a,c,d,m,n,p),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Not(Or(IntegerQ(p),GtQ(c,C0))),Not(IntegerQ(Times(C1D2,n)))))),
IIntegrate(6716,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),u_,Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(c,p),Integrate(Times(u,Power(Subtract(C1,Times(a,x)),Subtract(p,Times(C1D2,n))),Power(Plus(C1,Times(a,x)),Plus(p,Times(C1D2,n)))),x),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Or(IntegerQ(p),GtQ(c,C0))))),
IIntegrate(6717,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),u_,Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(c,IntPart(p)),Power(Plus(c,Times(d,Sqr(x))),FracPart(p)),Power(Times(Power(Subtract(C1,Times(a,x)),FracPart(p)),Power(Plus(C1,Times(a,x)),FracPart(p))),CN1)),Integrate(Times(u,Power(Subtract(C1,Times(a,x)),Subtract(p,Times(C1D2,n))),Power(Plus(C1,Times(a,x)),Plus(p,Times(C1D2,n)))),x),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Not(Or(IntegerQ(p),GtQ(c,C0))),IntegerQ(Times(C1D2,n))))),
IIntegrate(6718,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),u_,Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(c,IntPart(p)),Power(Plus(c,Times(d,Sqr(x))),FracPart(p)),Power(Power(Subtract(C1,Times(Sqr(a),Sqr(x))),FracPart(p)),CN1)),Integrate(Times(u,Power(Subtract(C1,Times(Sqr(a),Sqr(x))),p),Exp(Times(n,ArcTanh(Times(a,x))))),x),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Not(Or(IntegerQ(p),GtQ(c,C0))),Not(IntegerQ(Times(C1D2,n)))))),
IIntegrate(6719,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN2))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(d,p),Integrate(Times(u,Power(Power(x,Times(C2,p)),CN1),Power(Subtract(C1,Times(Sqr(a),Sqr(x))),p),Exp(Times(n,ArcTanh(Times(a,x))))),x),x),x),And(FreeQ(List(a,c,d,n),x),EqQ(Plus(c,Times(Sqr(a),d)),C0),IntegerQ(p)))),
IIntegrate(6720,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN2))),p_)),x_Symbol),
    Condition(Simp(Dist(Power(c,p),Integrate(Times(u,Power(Subtract(C1,Power(Times(a,x),CN1)),p),Power(Plus(C1,Power(Times(a,x),CN1)),p),Exp(Times(n,ArcTanh(Times(a,x))))),x),x),x),And(FreeQ(List(a,c,d,p),x),EqQ(Plus(c,Times(Sqr(a),d)),C0),Not(IntegerQ(p)),IntegerQ(Times(C1D2,n)),GtQ(c,C0))))
  );
}
