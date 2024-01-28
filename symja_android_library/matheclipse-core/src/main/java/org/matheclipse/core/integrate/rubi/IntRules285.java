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
class IntRules285 { 
  public static IAST RULES = List( 
IIntegrate(5701,Integrate(Times(ArcCot(Plus(c_DEFAULT,Times(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),d_DEFAULT))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),ArcCot(Plus(c,Times(d,Cot(Plus(a,Times(b,x)))))),Power(Times(f,Plus(m,C1)),CN1)),x),Negate(Simp(Star(Times(b,Subtract(Plus(C1,Times(CI,c)),d),Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))),Power(Subtract(Plus(C1,Times(CI,c),d),Times(Subtract(Plus(C1,Times(CI,c)),d),Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))))),CN1)),x)),x)),Simp(Star(Times(b,Plus(C1,Times(CN1,CI,c),d),Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))),Power(Subtract(Subtract(Subtract(C1,Times(CI,c)),d),Times(Plus(C1,Times(CN1,CI,c),d),Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0),NeQ(Sqr(Subtract(c,Times(CI,d))),CN1)))),
IIntegrate(5702,Integrate(ArcTan(Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcTan(Tanh(Plus(a,Times(b,x))))),x),Simp(Star(b,Integrate(Times(x,Sech(Plus(Times(C2,a),Times(C2,b,x)))),x)),x)),FreeQ(list(a,b),x))),
IIntegrate(5703,Integrate(ArcCot(Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCot(Tanh(Plus(a,Times(b,x))))),x),Simp(Star(b,Integrate(Times(x,Sech(Plus(Times(C2,a),Times(C2,b,x)))),x)),x)),FreeQ(list(a,b),x))),
IIntegrate(5704,Integrate(ArcTan(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcTan(Coth(Plus(a,Times(b,x))))),x),Simp(Star(b,Integrate(Times(x,Sech(Plus(Times(C2,a),Times(C2,b,x)))),x)),x)),FreeQ(list(a,b),x))),
IIntegrate(5705,Integrate(ArcCot(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcCot(Coth(Plus(a,Times(b,x))))),x),Simp(Star(b,Integrate(Times(x,Sech(Plus(Times(C2,a),Times(C2,b,x)))),x)),x)),FreeQ(list(a,b),x))),
IIntegrate(5706,Integrate(Times(ArcTan(Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),ArcTan(Tanh(Plus(a,Times(b,x)))),Power(Times(f,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Sech(Plus(Times(C2,a),Times(C2,b,x)))),x)),x)),And(FreeQ(List(a,b,e,f),x),IGtQ(m,C0)))),
IIntegrate(5707,Integrate(Times(ArcCot(Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),ArcCot(Tanh(Plus(a,Times(b,x)))),Power(Times(f,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Sech(Plus(Times(C2,a),Times(C2,b,x)))),x)),x)),And(FreeQ(List(a,b,e,f),x),IGtQ(m,C0)))),
IIntegrate(5708,Integrate(Times(ArcTan(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),ArcTan(Coth(Plus(a,Times(b,x)))),Power(Times(f,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Sech(Plus(Times(C2,a),Times(C2,b,x)))),x)),x)),And(FreeQ(List(a,b,e,f),x),IGtQ(m,C0)))),
IIntegrate(5709,Integrate(Times(ArcCot(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),ArcCot(Coth(Plus(a,Times(b,x)))),Power(Times(f,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Sech(Plus(Times(C2,a),Times(C2,b,x)))),x)),x)),And(FreeQ(List(a,b,e,f),x),IGtQ(m,C0)))),
IIntegrate(5710,Integrate(ArcTan(Plus(c_DEFAULT,Times(d_DEFAULT,Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcTan(Plus(c,Times(d,Tanh(Plus(a,Times(b,x))))))),x),Simp(Star(b,Integrate(Times(x,Power(Plus(c,Negate(d),Times(c,Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Sqr(Subtract(c,d)),CN1)))),
IIntegrate(5711,Integrate(ArcCot(Plus(c_DEFAULT,Times(d_DEFAULT,Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCot(Plus(c,Times(d,Tanh(Plus(a,Times(b,x))))))),x),Simp(Star(b,Integrate(Times(x,Power(Plus(c,Negate(d),Times(c,Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Sqr(Subtract(c,d)),CN1)))),
IIntegrate(5712,Integrate(ArcTan(Plus(c_DEFAULT,Times(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),d_DEFAULT))),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcTan(Plus(c,Times(d,Coth(Plus(a,Times(b,x))))))),x),Simp(Star(b,Integrate(Times(x,Power(Subtract(Subtract(c,d),Times(c,Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Sqr(Subtract(c,d)),CN1)))),
IIntegrate(5713,Integrate(ArcCot(Plus(c_DEFAULT,Times(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),d_DEFAULT))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCot(Plus(c,Times(d,Coth(Plus(a,Times(b,x))))))),x),Simp(Star(b,Integrate(Times(x,Power(Subtract(Subtract(c,d),Times(c,Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Sqr(Subtract(c,d)),CN1)))),
IIntegrate(5714,Integrate(ArcTan(Plus(c_DEFAULT,Times(d_DEFAULT,Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcTan(Plus(c,Times(d,Tanh(Plus(a,Times(b,x))))))),x),Simp(Star(Times(CI,b,Subtract(Subtract(CI,c),d)),Integrate(Times(x,Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Plus(CI,Negate(c),d,Times(Subtract(Subtract(CI,c),d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x)),x),Negate(Simp(Star(Times(CI,b,Plus(CI,c,d)),Integrate(Times(x,Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Plus(CI,c,Negate(d),Times(Plus(CI,c,d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x)),x))),And(FreeQ(List(a,b,c,d),x),NeQ(Sqr(Subtract(c,d)),CN1)))),
IIntegrate(5715,Integrate(ArcCot(Plus(c_DEFAULT,Times(d_DEFAULT,Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCot(Plus(c,Times(d,Tanh(Plus(a,Times(b,x))))))),x),Negate(Simp(Star(Times(CI,b,Subtract(Subtract(CI,c),d)),Integrate(Times(x,Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Plus(CI,Negate(c),d,Times(Subtract(Subtract(CI,c),d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x)),x)),Simp(Star(Times(CI,b,Plus(CI,c,d)),Integrate(Times(x,Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Plus(CI,c,Negate(d),Times(Plus(CI,c,d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Sqr(Subtract(c,d)),CN1)))),
IIntegrate(5716,Integrate(ArcTan(Plus(c_DEFAULT,Times(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),d_DEFAULT))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcTan(Plus(c,Times(d,Coth(Plus(a,Times(b,x))))))),x),Negate(Simp(Star(Times(CI,b,Subtract(Subtract(CI,c),d)),Integrate(Times(x,Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Subtract(Plus(CI,Negate(c),d),Times(Subtract(Subtract(CI,c),d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x)),x)),Simp(Star(Times(CI,b,Plus(CI,c,d)),Integrate(Times(x,Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Subtract(Subtract(Plus(CI,c),d),Times(Plus(CI,c,d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Sqr(Subtract(c,d)),CN1)))),
IIntegrate(5717,Integrate(ArcCot(Plus(c_DEFAULT,Times(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),d_DEFAULT))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCot(Plus(c,Times(d,Coth(Plus(a,Times(b,x))))))),x),Simp(Star(Times(CI,b,Subtract(Subtract(CI,c),d)),Integrate(Times(x,Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Subtract(Plus(CI,Negate(c),d),Times(Subtract(Subtract(CI,c),d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x)),x),Negate(Simp(Star(Times(CI,b,Plus(CI,c,d)),Integrate(Times(x,Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Subtract(Subtract(Plus(CI,c),d),Times(Plus(CI,c,d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x)),x))),And(FreeQ(List(a,b,c,d),x),NeQ(Sqr(Subtract(c,d)),CN1)))),
IIntegrate(5718,Integrate(Times(ArcTan(Plus(c_DEFAULT,Times(d_DEFAULT,Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),ArcTan(Plus(c,Times(d,Tanh(Plus(a,Times(b,x)))))),Power(Times(f,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Power(Plus(c,Negate(d),Times(c,Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0),EqQ(Sqr(Subtract(c,d)),CN1)))),
IIntegrate(5719,Integrate(Times(ArcCot(Plus(c_DEFAULT,Times(d_DEFAULT,Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),ArcCot(Plus(c,Times(d,Tanh(Plus(a,Times(b,x)))))),Power(Times(f,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Power(Plus(c,Negate(d),Times(c,Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0),EqQ(Sqr(Subtract(c,d)),CN1)))),
IIntegrate(5720,Integrate(Times(ArcTan(Plus(c_DEFAULT,Times(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),d_DEFAULT))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),ArcTan(Plus(c,Times(d,Coth(Plus(a,Times(b,x)))))),Power(Times(f,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Power(Subtract(Subtract(c,d),Times(c,Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0),EqQ(Sqr(Subtract(c,d)),CN1))))
  );
}
