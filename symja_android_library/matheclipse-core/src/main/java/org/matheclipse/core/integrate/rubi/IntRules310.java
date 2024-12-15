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
class IntRules310 { 
  public static IAST RULES = List( 
IIntegrate(6201,Integrate(Times(Exp(Times(ArcCoth(Times(c_DEFAULT,Plus(a_,Times(b_DEFAULT,x_)))),n_DEFAULT)),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(Times(c,Plus(a,Times(b,x))),Times(C1D2,n)),Power(Plus(C1,Power(Times(c,Plus(a,Times(b,x))),CN1)),Times(C1D2,n)),Power(Power(Plus(C1,Times(a,c),Times(b,c,x)),Times(C1D2,n)),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),m),Power(Plus(C1,Times(a,c),Times(b,c,x)),Times(C1D2,n)),Power(Power(Plus(CN1,Times(a,c),Times(b,c,x)),Times(C1D2,n)),CN1)),x),x),And(FreeQ(List(a,b,c,d,e,m,n),x),Not(IntegerQ(Times(C1D2,n)))))),
IIntegrate(6202,Integrate(Times(Exp(Times(ArcCoth(Plus(a_,Times(b_DEFAULT,x_))),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_),Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(Times(c,Power(Subtract(C1,Sqr(a)),CN1)),p),Power(Times(Plus(a,Times(b,x)),Power(Plus(C1,a,Times(b,x)),CN1)),Times(C1D2,n)),Power(Times(Plus(C1,a,Times(b,x)),Power(Plus(a,Times(b,x)),CN1)),Times(C1D2,n)),Power(Subtract(Subtract(C1,a),Times(b,x)),Times(C1D2,n)),Power(Power(Plus(CN1,a,Times(b,x)),Times(C1D2,n)),CN1)),Integrate(Times(u,Power(Subtract(Subtract(C1,a),Times(b,x)),Subtract(p,Times(C1D2,n))),Power(Plus(C1,a,Times(b,x)),Plus(p,Times(C1D2,n)))),x),x),And(FreeQ(List(a,b,c,d,e,n,p),x),Not(IntegerQ(Times(C1D2,n))),EqQ(Subtract(Times(b,d),Times(C2,a,e)),C0),EqQ(Plus(Times(Sqr(b),c),Times(e,Subtract(C1,Sqr(a)))),C0),Or(IntegerQ(p),GtQ(Times(c,Power(Subtract(C1,Sqr(a)),CN1)),C0))))),
IIntegrate(6203,Integrate(Times(Exp(Times(ArcCoth(Plus(a_,Times(b_DEFAULT,x_))),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_),Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(Plus(c,Times(d,x),Times(e,Sqr(x))),p),Power(Power(Subtract(Subtract(Subtract(C1,Sqr(a)),Times(C2,a,b,x)),Times(Sqr(b),Sqr(x))),p),CN1)),Integrate(Times(u,Power(Subtract(Subtract(Subtract(C1,Sqr(a)),Times(C2,a,b,x)),Times(Sqr(b),Sqr(x))),p),Exp(Times(n,ArcCoth(Times(a,x))))),x),x),And(FreeQ(List(a,b,c,d,e,n,p),x),Not(IntegerQ(Times(C1D2,n))),EqQ(Subtract(Times(b,d),Times(C2,a,e)),C0),EqQ(Plus(Times(Sqr(b),c),Times(e,Subtract(C1,Sqr(a)))),C0),Not(Or(IntegerQ(p),GtQ(Times(c,Power(Subtract(C1,Sqr(a)),CN1)),C0)))))),
IIntegrate(6204,Integrate(Times(Exp(Times(ArcCoth(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),CN1))),n_DEFAULT)),u_DEFAULT),x_Symbol),
    Condition(Integrate(Times(u,Exp(Times(n,ArcTanh(Plus(Times(a,Power(c,CN1)),Times(b,x,Power(c,CN1))))))),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(6205,Integrate(ArcTanh(Plus(a_,Times(b_DEFAULT,Power(x_,n_)))),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcTanh(Plus(a,Times(b,Power(x,n))))),x),Dist(Times(b,n),Integrate(Times(Power(x,n),Power(Subtract(Subtract(Subtract(C1,Sqr(a)),Times(C2,a,b,Power(x,n))),Times(Sqr(b),Power(x,Times(C2,n)))),CN1)),x),x)),FreeQ(list(a,b,n),x))),
IIntegrate(6206,Integrate(ArcCoth(Plus(a_,Times(b_DEFAULT,Power(x_,n_)))),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcCoth(Plus(a,Times(b,Power(x,n))))),x),Dist(Times(b,n),Integrate(Times(Power(x,n),Power(Subtract(Subtract(Subtract(C1,Sqr(a)),Times(C2,a,b,Power(x,n))),Times(Sqr(b),Power(x,Times(C2,n)))),CN1)),x),x)),FreeQ(list(a,b,n),x))),
IIntegrate(6207,Integrate(Times(ArcTanh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Dist(C1D2,Integrate(Times(Log(Plus(C1,a,Times(b,Power(x,n)))),Power(x,CN1)),x),x),Dist(C1D2,Integrate(Times(Log(Subtract(Subtract(C1,a),Times(b,Power(x,n)))),Power(x,CN1)),x),x)),FreeQ(list(a,b,n),x))),
IIntegrate(6208,Integrate(Times(ArcCoth(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Dist(C1D2,Integrate(Times(Log(Plus(C1,Power(Plus(a,Times(b,Power(x,n))),CN1))),Power(x,CN1)),x),x),Dist(C1D2,Integrate(Times(Log(Subtract(C1,Power(Plus(a,Times(b,Power(x,n))),CN1))),Power(x,CN1)),x),x)),FreeQ(list(a,b,n),x))),
IIntegrate(6209,Integrate(Times(ArcTanh(Plus(a_,Times(b_DEFAULT,Power(x_,n_)))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),ArcTanh(Plus(a,Times(b,Power(x,n)))),Power(Plus(m,C1),CN1)),x),Dist(Times(b,n,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,n)),Power(Subtract(Subtract(Subtract(C1,Sqr(a)),Times(C2,a,b,Power(x,n))),Times(Sqr(b),Power(x,Times(C2,n)))),CN1)),x),x)),And(FreeQ(list(a,b),x),RationalQ(m,n),NeQ(m,CN1),NeQ(Plus(m,C1),n)))),
IIntegrate(6210,Integrate(Times(ArcCoth(Plus(a_,Times(b_DEFAULT,Power(x_,n_)))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),ArcCoth(Plus(a,Times(b,Power(x,n)))),Power(Plus(m,C1),CN1)),x),Dist(Times(b,n,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,n)),Power(Subtract(Subtract(Subtract(C1,Sqr(a)),Times(C2,a,b,Power(x,n))),Times(Sqr(b),Power(x,Times(C2,n)))),CN1)),x),x)),And(FreeQ(list(a,b),x),RationalQ(m,n),NeQ(m,CN1),NeQ(Plus(m,C1),n)))),
IIntegrate(6211,Integrate(ArcTanh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(f_,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Subtract(Dist(C1D2,Integrate(Log(Plus(C1,a,Times(b,Power(f,Plus(c,Times(d,x)))))),x),x),Dist(C1D2,Integrate(Log(Subtract(Subtract(C1,a),Times(b,Power(f,Plus(c,Times(d,x)))))),x),x)),FreeQ(List(a,b,c,d,f),x))),
IIntegrate(6212,Integrate(ArcCoth(Plus(a_DEFAULT,Times(b_DEFAULT,Power(f_,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Subtract(Dist(C1D2,Integrate(Log(Plus(C1,Power(Plus(a,Times(b,Power(f,Plus(c,Times(d,x))))),CN1))),x),x),Dist(C1D2,Integrate(Log(Subtract(C1,Power(Plus(a,Times(b,Power(f,Plus(c,Times(d,x))))),CN1))),x),x)),FreeQ(List(a,b,c,d,f),x))),
IIntegrate(6213,Integrate(Times(ArcTanh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(f_,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Dist(C1D2,Integrate(Times(Power(x,m),Log(Plus(C1,a,Times(b,Power(f,Plus(c,Times(d,x))))))),x),x),Dist(C1D2,Integrate(Times(Power(x,m),Log(Subtract(Subtract(C1,a),Times(b,Power(f,Plus(c,Times(d,x))))))),x),x)),And(FreeQ(List(a,b,c,d,f),x),IGtQ(m,C0)))),
IIntegrate(6214,Integrate(Times(ArcCoth(Plus(a_DEFAULT,Times(b_DEFAULT,Power(f_,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Dist(C1D2,Integrate(Times(Power(x,m),Log(Plus(C1,Power(Plus(a,Times(b,Power(f,Plus(c,Times(d,x))))),CN1)))),x),x),Dist(C1D2,Integrate(Times(Power(x,m),Log(Subtract(C1,Power(Plus(a,Times(b,Power(f,Plus(c,Times(d,x))))),CN1)))),x),x)),And(FreeQ(List(a,b,c,d,f),x),IGtQ(m,C0)))),
IIntegrate(6215,Integrate(Times(Power(ArcTanh(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),CN1))),m_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Integrate(Times(u,Power(ArcCoth(Plus(Times(a,Power(c,CN1)),Times(b,Power(x,n),Power(c,CN1)))),m)),x),FreeQ(List(a,b,c,n,m),x))),
IIntegrate(6216,Integrate(Times(Power(ArcCoth(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),CN1))),m_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Integrate(Times(u,Power(ArcTanh(Plus(Times(a,Power(c,CN1)),Times(b,Power(x,n),Power(c,CN1)))),m)),x),FreeQ(List(a,b,c,n,m),x))),
IIntegrate(6217,Integrate(ArcTanh(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcTanh(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2)))),x),Dist(c,Integrate(Times(x,Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x),x)),And(FreeQ(list(a,b,c),x),EqQ(b,Sqr(c))))),
IIntegrate(6218,Integrate(ArcCoth(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcCoth(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2)))),x),Dist(c,Integrate(Times(x,Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x),x)),And(FreeQ(list(a,b,c),x),EqQ(b,Sqr(c))))),
IIntegrate(6219,Integrate(Times(ArcTanh(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(ArcTanh(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))),Log(x)),x),Dist(c,Integrate(Times(Log(x),Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x),x)),And(FreeQ(list(a,b,c),x),EqQ(b,Sqr(c))))),
IIntegrate(6220,Integrate(Times(ArcCoth(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(ArcCoth(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))),Log(x)),x),Dist(c,Integrate(Times(Log(x),Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x),x)),And(FreeQ(list(a,b,c),x),EqQ(b,Sqr(c)))))
  );
}
