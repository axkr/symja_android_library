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
class IntRules338 { 
  public static IAST RULES = List( 
IIntegrate(6761,Integrate(Times(ArcTanh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Simp(Star(C1D2,Integrate(Times(Log(Plus(C1,a,Times(b,Power(x,n)))),Power(x,CN1)),x)),x),Simp(Star(C1D2,Integrate(Times(Log(Subtract(Subtract(C1,a),Times(b,Power(x,n)))),Power(x,CN1)),x)),x)),FreeQ(list(a,b,n),x))),
IIntegrate(6762,Integrate(Times(ArcCoth(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Simp(Star(C1D2,Integrate(Times(Log(Plus(C1,Power(Plus(a,Times(b,Power(x,n))),CN1))),Power(x,CN1)),x)),x),Simp(Star(C1D2,Integrate(Times(Log(Subtract(C1,Power(Plus(a,Times(b,Power(x,n))),CN1))),Power(x,CN1)),x)),x)),FreeQ(list(a,b,n),x))),
IIntegrate(6763,Integrate(Times(ArcTanh(Plus(a_,Times(b_DEFAULT,Power(x_,n_)))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),ArcTanh(Plus(a,Times(b,Power(x,n)))),Power(Plus(m,C1),CN1)),x),Simp(Star(Times(b,n,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,n)),Power(Subtract(Subtract(Subtract(C1,Sqr(a)),Times(C2,a,b,Power(x,n))),Times(Sqr(b),Power(x,Times(C2,n)))),CN1)),x)),x)),And(FreeQ(list(a,b),x),RationalQ(m,n),NeQ(m,CN1),NeQ(Plus(m,C1),n)))),
IIntegrate(6764,Integrate(Times(ArcCoth(Plus(a_,Times(b_DEFAULT,Power(x_,n_)))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),ArcCoth(Plus(a,Times(b,Power(x,n)))),Power(Plus(m,C1),CN1)),x),Simp(Star(Times(b,n,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,n)),Power(Subtract(Subtract(Subtract(C1,Sqr(a)),Times(C2,a,b,Power(x,n))),Times(Sqr(b),Power(x,Times(C2,n)))),CN1)),x)),x)),And(FreeQ(list(a,b),x),RationalQ(m,n),NeQ(m,CN1),NeQ(Plus(m,C1),n)))),
IIntegrate(6765,Integrate(ArcTanh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(f_,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Subtract(Simp(Star(C1D2,Integrate(Log(Plus(C1,a,Times(b,Power(f,Plus(c,Times(d,x)))))),x)),x),Simp(Star(C1D2,Integrate(Log(Subtract(Subtract(C1,a),Times(b,Power(f,Plus(c,Times(d,x)))))),x)),x)),FreeQ(List(a,b,c,d,f),x))),
IIntegrate(6766,Integrate(ArcCoth(Plus(a_DEFAULT,Times(b_DEFAULT,Power(f_,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Subtract(Simp(Star(C1D2,Integrate(Log(Plus(C1,Power(Plus(a,Times(b,Power(f,Plus(c,Times(d,x))))),CN1))),x)),x),Simp(Star(C1D2,Integrate(Log(Subtract(C1,Power(Plus(a,Times(b,Power(f,Plus(c,Times(d,x))))),CN1))),x)),x)),FreeQ(List(a,b,c,d,f),x))),
IIntegrate(6767,Integrate(Times(ArcTanh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(f_,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Star(C1D2,Integrate(Times(Power(x,m),Log(Plus(C1,a,Times(b,Power(f,Plus(c,Times(d,x))))))),x)),x),Simp(Star(C1D2,Integrate(Times(Power(x,m),Log(Subtract(Subtract(C1,a),Times(b,Power(f,Plus(c,Times(d,x))))))),x)),x)),And(FreeQ(List(a,b,c,d,f),x),IGtQ(m,C0)))),
IIntegrate(6768,Integrate(Times(ArcCoth(Plus(a_DEFAULT,Times(b_DEFAULT,Power(f_,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Star(C1D2,Integrate(Times(Power(x,m),Log(Plus(C1,Power(Plus(a,Times(b,Power(f,Plus(c,Times(d,x))))),CN1)))),x)),x),Simp(Star(C1D2,Integrate(Times(Power(x,m),Log(Subtract(C1,Power(Plus(a,Times(b,Power(f,Plus(c,Times(d,x))))),CN1)))),x)),x)),And(FreeQ(List(a,b,c,d,f),x),IGtQ(m,C0)))),
IIntegrate(6769,Integrate(Times(Power(ArcTanh(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),CN1))),m_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Integrate(Times(u,Power(ArcCoth(Plus(Times(a,Power(c,CN1)),Times(b,Power(x,n),Power(c,CN1)))),m)),x),FreeQ(List(a,b,c,n,m),x))),
IIntegrate(6770,Integrate(Times(Power(ArcCoth(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),CN1))),m_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Integrate(Times(u,Power(ArcTanh(Plus(Times(a,Power(c,CN1)),Times(b,Power(x,n),Power(c,CN1)))),m)),x),FreeQ(List(a,b,c,n,m),x))),
IIntegrate(6771,Integrate(ArcTanh(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcTanh(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2)))),x),Simp(Star(c,Integrate(Times(x,Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x)),x)),And(FreeQ(list(a,b,c),x),EqQ(b,Sqr(c))))),
IIntegrate(6772,Integrate(ArcCoth(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcCoth(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2)))),x),Simp(Star(c,Integrate(Times(x,Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x)),x)),And(FreeQ(list(a,b,c),x),EqQ(b,Sqr(c))))),
IIntegrate(6773,Integrate(Times(ArcTanh(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(ArcTanh(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))),Log(x)),x),Simp(Star(c,Integrate(Times(Log(x),Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x)),x)),And(FreeQ(list(a,b,c),x),EqQ(b,Sqr(c))))),
IIntegrate(6774,Integrate(Times(ArcCoth(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(ArcCoth(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))),Log(x)),x),Simp(Star(c,Integrate(Times(Log(x),Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x)),x)),And(FreeQ(list(a,b,c),x),EqQ(b,Sqr(c))))),
IIntegrate(6775,Integrate(Times(ArcTanh(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),Plus(m,C1)),ArcTanh(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Star(Times(c,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(d,x),Plus(m,C1)),Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x)),x)),And(FreeQ(List(a,b,c,d,m),x),EqQ(b,Sqr(c)),NeQ(m,CN1)))),
IIntegrate(6776,Integrate(Times(ArcCoth(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),Plus(m,C1)),ArcCoth(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Star(Times(c,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(d,x),Plus(m,C1)),Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x)),x)),And(FreeQ(List(a,b,c,d,m),x),EqQ(b,Sqr(c)),NeQ(m,CN1)))),
IIntegrate(6777,Integrate(Times(Power(ArcTanh(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),CN1),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Times(Power(c,CN1),Log(ArcTanh(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))))),x),And(FreeQ(list(a,b,c),x),EqQ(b,Sqr(c))))),
IIntegrate(6778,Integrate(Times(Power(ArcCoth(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),CN1),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Times(CN1,Power(c,CN1),Log(ArcCoth(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))))),x),And(FreeQ(list(a,b,c),x),EqQ(b,Sqr(c))))),
IIntegrate(6779,Integrate(Times(Power(ArcTanh(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Times(Power(ArcTanh(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))),Plus(m,C1)),Power(Times(c,Plus(m,C1)),CN1)),x),And(FreeQ(List(a,b,c,m),x),EqQ(b,Sqr(c)),NeQ(m,CN1)))),
IIntegrate(6780,Integrate(Times(Power(ArcCoth(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Times(CN1,Power(ArcCoth(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))),Plus(m,C1)),Power(Times(c,Plus(m,C1)),CN1)),x),And(FreeQ(List(a,b,c,m),x),EqQ(b,Sqr(c)),NeQ(m,CN1))))
  );
}
