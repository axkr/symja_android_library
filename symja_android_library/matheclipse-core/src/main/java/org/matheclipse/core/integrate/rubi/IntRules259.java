package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class IntRules259 { 
  public static IAST RULES = List( 
IIntegrate(5181,Int(ArcTan(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcTan(Coth(Plus(a,Times(b,x))))),x),Dist(b,Int(Times(x,Sech(Plus(Times(C2,a),Times(C2,b,x)))),x),x)),FreeQ(List(a,b),x))),
IIntegrate(5182,Int(ArcCot(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcCot(Coth(Plus(a,Times(b,x))))),x),Dist(b,Int(Times(x,Sech(Plus(Times(C2,a),Times(C2,b,x)))),x),x)),FreeQ(List(a,b),x))),
IIntegrate(5183,Int(Times(ArcTan(Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),ArcTan(Tanh(Plus(a,Times(b,x)))),Power(Times(f,Plus(m,C1)),CN1)),x),Dist(Times(b,Power(Times(f,Plus(m,C1)),CN1)),Int(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Sech(Plus(Times(C2,a),Times(C2,b,x)))),x),x)),And(FreeQ(List(a,b,e,f),x),IGtQ(m,C0)))),
IIntegrate(5184,Int(Times(ArcCot(Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),ArcCot(Tanh(Plus(a,Times(b,x)))),Power(Times(f,Plus(m,C1)),CN1)),x),Dist(Times(b,Power(Times(f,Plus(m,C1)),CN1)),Int(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Sech(Plus(Times(C2,a),Times(C2,b,x)))),x),x)),And(FreeQ(List(a,b,e,f),x),IGtQ(m,C0)))),
IIntegrate(5185,Int(Times(ArcTan(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),ArcTan(Coth(Plus(a,Times(b,x)))),Power(Times(f,Plus(m,C1)),CN1)),x),Dist(Times(b,Power(Times(f,Plus(m,C1)),CN1)),Int(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Sech(Plus(Times(C2,a),Times(C2,b,x)))),x),x)),And(FreeQ(List(a,b,e,f),x),IGtQ(m,C0)))),
IIntegrate(5186,Int(Times(ArcCot(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),ArcCot(Coth(Plus(a,Times(b,x)))),Power(Times(f,Plus(m,C1)),CN1)),x),Dist(Times(b,Power(Times(f,Plus(m,C1)),CN1)),Int(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Sech(Plus(Times(C2,a),Times(C2,b,x)))),x),x)),And(FreeQ(List(a,b,e,f),x),IGtQ(m,C0)))),
IIntegrate(5187,Int(ArcTan(Plus(c_DEFAULT,Times(d_DEFAULT,Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcTan(Plus(c,Times(d,Tanh(Plus(a,Times(b,x))))))),x),Dist(b,Int(Times(x,Power(Plus(c,Negate(d),Times(c,Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Sqr(Subtract(c,d)),CN1)))),
IIntegrate(5188,Int(ArcCot(Plus(c_DEFAULT,Times(d_DEFAULT,Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCot(Plus(c,Times(d,Tanh(Plus(a,Times(b,x))))))),x),Dist(b,Int(Times(x,Power(Plus(c,Negate(d),Times(c,Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Sqr(Subtract(c,d)),CN1)))),
IIntegrate(5189,Int(ArcTan(Plus(c_DEFAULT,Times(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),d_DEFAULT))),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcTan(Plus(c,Times(d,Coth(Plus(a,Times(b,x))))))),x),Dist(b,Int(Times(x,Power(Subtract(Subtract(c,d),Times(c,Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Sqr(Subtract(c,d)),CN1)))),
IIntegrate(5190,Int(ArcCot(Plus(c_DEFAULT,Times(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),d_DEFAULT))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCot(Plus(c,Times(d,Coth(Plus(a,Times(b,x))))))),x),Dist(b,Int(Times(x,Power(Subtract(Subtract(c,d),Times(c,Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Sqr(Subtract(c,d)),CN1)))),
IIntegrate(5191,Int(ArcTan(Plus(c_DEFAULT,Times(d_DEFAULT,Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcTan(Plus(c,Times(d,Tanh(Plus(a,Times(b,x))))))),x),Dist(Times(CI,b,Subtract(Subtract(CI,c),d)),Int(Times(x,Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Plus(CI,Negate(c),d,Times(Subtract(Subtract(CI,c),d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x),Negate(Dist(Times(CI,b,Plus(CI,c,d)),Int(Times(x,Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Plus(CI,c,Negate(d),Times(Plus(CI,c,d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x))),And(FreeQ(List(a,b,c,d),x),NeQ(Sqr(Subtract(c,d)),CN1)))),
IIntegrate(5192,Int(ArcCot(Plus(c_DEFAULT,Times(d_DEFAULT,Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCot(Plus(c,Times(d,Tanh(Plus(a,Times(b,x))))))),x),Negate(Dist(Times(CI,b,Subtract(Subtract(CI,c),d)),Int(Times(x,Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Plus(CI,Negate(c),d,Times(Subtract(Subtract(CI,c),d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x)),Dist(Times(CI,b,Plus(CI,c,d)),Int(Times(x,Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Plus(CI,c,Negate(d),Times(Plus(CI,c,d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Sqr(Subtract(c,d)),CN1)))),
IIntegrate(5193,Int(ArcTan(Plus(c_DEFAULT,Times(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),d_DEFAULT))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcTan(Plus(c,Times(d,Coth(Plus(a,Times(b,x))))))),x),Negate(Dist(Times(CI,b,Subtract(Subtract(CI,c),d)),Int(Times(x,Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Subtract(Plus(CI,Negate(c),d),Times(Subtract(Subtract(CI,c),d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x)),Dist(Times(CI,b,Plus(CI,c,d)),Int(Times(x,Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Subtract(Subtract(Plus(CI,c),d),Times(Plus(CI,c,d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Sqr(Subtract(c,d)),CN1)))),
IIntegrate(5194,Int(ArcCot(Plus(c_DEFAULT,Times(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),d_DEFAULT))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCot(Plus(c,Times(d,Coth(Plus(a,Times(b,x))))))),x),Dist(Times(CI,b,Subtract(Subtract(CI,c),d)),Int(Times(x,Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Subtract(Plus(CI,Negate(c),d),Times(Subtract(Subtract(CI,c),d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x),Negate(Dist(Times(CI,b,Plus(CI,c,d)),Int(Times(x,Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Subtract(Subtract(Plus(CI,c),d),Times(Plus(CI,c,d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x))),And(FreeQ(List(a,b,c,d),x),NeQ(Sqr(Subtract(c,d)),CN1)))),
IIntegrate(5195,Int(Times(ArcTan(Plus(c_DEFAULT,Times(d_DEFAULT,Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),ArcTan(Plus(c,Times(d,Tanh(Plus(a,Times(b,x)))))),Power(Times(f,Plus(m,C1)),CN1)),x),Dist(Times(b,Power(Times(f,Plus(m,C1)),CN1)),Int(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Power(Plus(c,Negate(d),Times(c,Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0),EqQ(Sqr(Subtract(c,d)),CN1)))),
IIntegrate(5196,Int(Times(ArcCot(Plus(c_DEFAULT,Times(d_DEFAULT,Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),ArcCot(Plus(c,Times(d,Tanh(Plus(a,Times(b,x)))))),Power(Times(f,Plus(m,C1)),CN1)),x),Dist(Times(b,Power(Times(f,Plus(m,C1)),CN1)),Int(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Power(Plus(c,Negate(d),Times(c,Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0),EqQ(Sqr(Subtract(c,d)),CN1)))),
IIntegrate(5197,Int(Times(ArcTan(Plus(c_DEFAULT,Times(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),d_DEFAULT))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),ArcTan(Plus(c,Times(d,Coth(Plus(a,Times(b,x)))))),Power(Times(f,Plus(m,C1)),CN1)),x),Dist(Times(b,Power(Times(f,Plus(m,C1)),CN1)),Int(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Power(Subtract(Subtract(c,d),Times(c,Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0),EqQ(Sqr(Subtract(c,d)),CN1)))),
IIntegrate(5198,Int(Times(ArcCot(Plus(c_DEFAULT,Times(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),d_DEFAULT))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),ArcCot(Plus(c,Times(d,Coth(Plus(a,Times(b,x)))))),Power(Times(f,Plus(m,C1)),CN1)),x),Dist(Times(b,Power(Times(f,Plus(m,C1)),CN1)),Int(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Power(Subtract(Subtract(c,d),Times(c,Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0),EqQ(Sqr(Subtract(c,d)),CN1)))),
IIntegrate(5199,Int(Times(ArcTan(Plus(c_DEFAULT,Times(d_DEFAULT,Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),ArcTan(Plus(c,Times(d,Tanh(Plus(a,Times(b,x)))))),Power(Times(f,Plus(m,C1)),CN1)),x),Dist(Times(CI,b,Subtract(Subtract(CI,c),d),Power(Times(f,Plus(m,C1)),CN1)),Int(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Plus(CI,Negate(c),d,Times(Subtract(Subtract(CI,c),d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x),Negate(Dist(Times(CI,b,Plus(CI,c,d),Power(Times(f,Plus(m,C1)),CN1)),Int(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Plus(CI,c,Negate(d),Times(Plus(CI,c,d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x))),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0),NeQ(Sqr(Subtract(c,d)),CN1)))),
IIntegrate(5200,Int(Times(ArcCot(Plus(c_DEFAULT,Times(d_DEFAULT,Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),ArcCot(Plus(c,Times(d,Tanh(Plus(a,Times(b,x)))))),Power(Times(f,Plus(m,C1)),CN1)),x),Negate(Dist(Times(CI,b,Subtract(Subtract(CI,c),d),Power(Times(f,Plus(m,C1)),CN1)),Int(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Plus(CI,Negate(c),d,Times(Subtract(Subtract(CI,c),d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x)),Dist(Times(CI,b,Plus(CI,c,d),Power(Times(f,Plus(m,C1)),CN1)),Int(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Plus(CI,c,Negate(d),Times(Plus(CI,c,d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0),NeQ(Sqr(Subtract(c,d)),CN1))))
  );
}
