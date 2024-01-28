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
class IntRules283 { 
  public static IAST RULES = List( 
IIntegrate(5661,Integrate(Times(ArcCot(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Simp(Star(Times(C1D2,CI),Integrate(Times(Log(Subtract(C1,Times(CI,Power(Plus(a,Times(b,Power(x,n))),CN1)))),Power(x,CN1)),x)),x),Simp(Star(Times(C1D2,CI),Integrate(Times(Log(Plus(C1,Times(CI,Power(Plus(a,Times(b,Power(x,n))),CN1)))),Power(x,CN1)),x)),x)),FreeQ(list(a,b,n),x))),
IIntegrate(5662,Integrate(Times(ArcTan(Plus(a_,Times(b_DEFAULT,Power(x_,n_)))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),ArcTan(Plus(a,Times(b,Power(x,n)))),Power(Plus(m,C1),CN1)),x),Simp(Star(Times(b,n,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,n)),Power(Plus(C1,Sqr(a),Times(C2,a,b,Power(x,n)),Times(Sqr(b),Power(x,Times(C2,n)))),CN1)),x)),x)),And(FreeQ(list(a,b),x),RationalQ(m,n),Unequal(Plus(m,C1),C0),Unequal(Plus(m,C1),n)))),
IIntegrate(5663,Integrate(Times(ArcCot(Plus(a_,Times(b_DEFAULT,Power(x_,n_)))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),ArcCot(Plus(a,Times(b,Power(x,n)))),Power(Plus(m,C1),CN1)),x),Simp(Star(Times(b,n,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,n)),Power(Plus(C1,Sqr(a),Times(C2,a,b,Power(x,n)),Times(Sqr(b),Power(x,Times(C2,n)))),CN1)),x)),x)),And(FreeQ(list(a,b),x),RationalQ(m,n),Unequal(Plus(m,C1),C0),Unequal(Plus(m,C1),n)))),
IIntegrate(5664,Integrate(ArcTan(Plus(a_DEFAULT,Times(b_DEFAULT,Power(f_,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Subtract(Simp(Star(Times(C1D2,CI),Integrate(Log(Subtract(Subtract(C1,Times(CI,a)),Times(CI,b,Power(f,Plus(c,Times(d,x)))))),x)),x),Simp(Star(Times(C1D2,CI),Integrate(Log(Plus(C1,Times(CI,a),Times(CI,b,Power(f,Plus(c,Times(d,x)))))),x)),x)),FreeQ(List(a,b,c,d,f),x))),
IIntegrate(5665,Integrate(ArcCot(Plus(a_DEFAULT,Times(b_DEFAULT,Power(f_,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Subtract(Simp(Star(Times(C1D2,CI),Integrate(Log(Subtract(C1,Times(CI,Power(Plus(a,Times(b,Power(f,Plus(c,Times(d,x))))),CN1)))),x)),x),Simp(Star(Times(C1D2,CI),Integrate(Log(Plus(C1,Times(CI,Power(Plus(a,Times(b,Power(f,Plus(c,Times(d,x))))),CN1)))),x)),x)),FreeQ(List(a,b,c,d,f),x))),
IIntegrate(5666,Integrate(Times(ArcTan(Plus(a_DEFAULT,Times(b_DEFAULT,Power(f_,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Star(Times(C1D2,CI),Integrate(Times(Power(x,m),Log(Subtract(Subtract(C1,Times(CI,a)),Times(CI,b,Power(f,Plus(c,Times(d,x))))))),x)),x),Simp(Star(Times(C1D2,CI),Integrate(Times(Power(x,m),Log(Plus(C1,Times(CI,a),Times(CI,b,Power(f,Plus(c,Times(d,x))))))),x)),x)),And(FreeQ(List(a,b,c,d,f),x),IntegerQ(m),Greater(m,C0)))),
IIntegrate(5667,Integrate(Times(ArcCot(Plus(a_DEFAULT,Times(b_DEFAULT,Power(f_,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Star(Times(C1D2,CI),Integrate(Times(Power(x,m),Log(Subtract(C1,Times(CI,Power(Plus(a,Times(b,Power(f,Plus(c,Times(d,x))))),CN1))))),x)),x),Simp(Star(Times(C1D2,CI),Integrate(Times(Power(x,m),Log(Plus(C1,Times(CI,Power(Plus(a,Times(b,Power(f,Plus(c,Times(d,x))))),CN1))))),x)),x)),And(FreeQ(List(a,b,c,d,f),x),IntegerQ(m),Greater(m,C0)))),
IIntegrate(5668,Integrate(Times(Power(ArcTan(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),CN1))),m_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Integrate(Times(u,Power(ArcCot(Plus(Times(a,Power(c,CN1)),Times(b,Power(x,n),Power(c,CN1)))),m)),x),FreeQ(List(a,b,c,n,m),x))),
IIntegrate(5669,Integrate(Times(Power(ArcCot(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),CN1))),m_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Integrate(Times(u,Power(ArcTan(Plus(Times(a,Power(c,CN1)),Times(b,Power(x,n),Power(c,CN1)))),m)),x),FreeQ(List(a,b,c,n,m),x))),
IIntegrate(5670,Integrate(ArcTan(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcTan(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2)))),x),Simp(Star(c,Integrate(Times(x,Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x)),x)),And(FreeQ(list(a,b,c),x),EqQ(Plus(b,Sqr(c)),C0)))),
IIntegrate(5671,Integrate(ArcCot(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCot(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2)))),x),Simp(Star(c,Integrate(Times(x,Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x)),x)),And(FreeQ(list(a,b,c),x),EqQ(Plus(b,Sqr(c)),C0)))),
IIntegrate(5672,Integrate(Times(ArcTan(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(ArcTan(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))),Log(x)),x),Simp(Star(c,Integrate(Times(Log(x),Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x)),x)),And(FreeQ(list(a,b,c),x),EqQ(Plus(b,Sqr(c)),C0)))),
IIntegrate(5673,Integrate(Times(ArcCot(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Simp(Times(ArcCot(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))),Log(x)),x),Simp(Star(c,Integrate(Times(Log(x),Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x)),x)),And(FreeQ(list(a,b,c),x),EqQ(Plus(b,Sqr(c)),C0)))),
IIntegrate(5674,Integrate(Times(ArcTan(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),Plus(m,C1)),ArcTan(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Star(Times(c,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(d,x),Plus(m,C1)),Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x)),x)),And(FreeQ(List(a,b,c,d,m),x),EqQ(Plus(b,Sqr(c)),C0),NeQ(m,CN1)))),
IIntegrate(5675,Integrate(Times(ArcCot(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(d,x),Plus(m,C1)),ArcCot(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Star(Times(c,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(d,x),Plus(m,C1)),Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x)),x)),And(FreeQ(List(a,b,c,d,m),x),EqQ(Plus(b,Sqr(c)),C0),NeQ(m,CN1)))),
IIntegrate(5676,Integrate(Times(Power(ArcTan(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),CN1),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Times(Power(c,CN1),Log(ArcTan(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))))),x),And(FreeQ(list(a,b,c),x),EqQ(Plus(b,Sqr(c)),C0)))),
IIntegrate(5677,Integrate(Times(Power(ArcCot(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),CN1),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Times(CN1,Power(c,CN1),Log(ArcCot(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))))),x),And(FreeQ(list(a,b,c),x),EqQ(Plus(b,Sqr(c)),C0)))),
IIntegrate(5678,Integrate(Times(Power(ArcTan(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Times(Power(ArcTan(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))),Plus(m,C1)),Power(Times(c,Plus(m,C1)),CN1)),x),And(FreeQ(List(a,b,c,m),x),EqQ(Plus(b,Sqr(c)),C0),NeQ(m,CN1)))),
IIntegrate(5679,Integrate(Times(Power(ArcCot(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Times(CN1,Power(ArcCot(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))),Plus(m,C1)),Power(Times(c,Plus(m,C1)),CN1)),x),And(FreeQ(List(a,b,c,m),x),EqQ(Plus(b,Sqr(c)),C0),NeQ(m,CN1)))),
IIntegrate(5680,Integrate(Times(Power(ArcTan(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Star(Times(Sqrt(Plus(a,Times(b,Sqr(x)))),Power(Plus(d,Times(e,Sqr(x))),CN1D2)),Integrate(Times(Power(ArcTan(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))),m),Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x)),x),And(FreeQ(List(a,b,c,d,e,m),x),EqQ(Plus(b,Sqr(c)),C0),EqQ(Subtract(Times(b,d),Times(a,e)),C0))))
  );
}
