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
class IntRules340 { 
  public static IAST RULES = List( 
IIntegrate(6801,Integrate(ArcTanh(Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcTanh(Tan(Plus(a,Times(b,x))))),x),Simp(Star(b,Integrate(Times(x,Sec(Plus(Times(C2,a),Times(C2,b,x)))),x)),x)),FreeQ(list(a,b),x))),
IIntegrate(6802,Integrate(ArcCoth(Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcCoth(Tan(Plus(a,Times(b,x))))),x),Simp(Star(b,Integrate(Times(x,Sec(Plus(Times(C2,a),Times(C2,b,x)))),x)),x)),FreeQ(list(a,b),x))),
IIntegrate(6803,Integrate(ArcTanh(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcTanh(Cot(Plus(a,Times(b,x))))),x),Simp(Star(b,Integrate(Times(x,Sec(Plus(Times(C2,a),Times(C2,b,x)))),x)),x)),FreeQ(list(a,b),x))),
IIntegrate(6804,Integrate(ArcCoth(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcCoth(Cot(Plus(a,Times(b,x))))),x),Simp(Star(b,Integrate(Times(x,Sec(Plus(Times(C2,a),Times(C2,b,x)))),x)),x)),FreeQ(list(a,b),x))),
IIntegrate(6805,Integrate(Times(ArcTanh(Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),ArcTanh(Tan(Plus(a,Times(b,x)))),Power(Times(f,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Sec(Plus(Times(C2,a),Times(C2,b,x)))),x)),x)),And(FreeQ(List(a,b,e,f),x),IGtQ(m,C0)))),
IIntegrate(6806,Integrate(Times(ArcCoth(Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),ArcCoth(Tan(Plus(a,Times(b,x)))),Power(Times(f,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Sec(Plus(Times(C2,a),Times(C2,b,x)))),x)),x)),And(FreeQ(List(a,b,e,f),x),IGtQ(m,C0)))),
IIntegrate(6807,Integrate(Times(ArcTanh(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),ArcTanh(Cot(Plus(a,Times(b,x)))),Power(Times(f,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Sec(Plus(Times(C2,a),Times(C2,b,x)))),x)),x)),And(FreeQ(List(a,b,e,f),x),IGtQ(m,C0)))),
IIntegrate(6808,Integrate(Times(ArcCoth(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),ArcCoth(Cot(Plus(a,Times(b,x)))),Power(Times(f,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Sec(Plus(Times(C2,a),Times(C2,b,x)))),x)),x)),And(FreeQ(List(a,b,e,f),x),IGtQ(m,C0)))),
IIntegrate(6809,Integrate(ArcTanh(Plus(c_DEFAULT,Times(d_DEFAULT,Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcTanh(Plus(c,Times(d,Tan(Plus(a,Times(b,x))))))),x),Simp(Star(Times(CI,b),Integrate(Times(x,Power(Plus(c,Times(CI,d),Times(c,Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Sqr(Plus(c,Times(CI,d))),C1)))),
IIntegrate(6810,Integrate(ArcCoth(Plus(c_DEFAULT,Times(d_DEFAULT,Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCoth(Plus(c,Times(d,Tan(Plus(a,Times(b,x))))))),x),Simp(Star(Times(CI,b),Integrate(Times(x,Power(Plus(c,Times(CI,d),Times(c,Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Sqr(Plus(c,Times(CI,d))),C1)))),
IIntegrate(6811,Integrate(ArcTanh(Plus(c_DEFAULT,Times(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),d_DEFAULT))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcTanh(Plus(c,Times(d,Cot(Plus(a,Times(b,x))))))),x),Simp(Star(Times(CI,b),Integrate(Times(x,Power(Subtract(Subtract(c,Times(CI,d)),Times(c,Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Sqr(Subtract(c,Times(CI,d))),C1)))),
IIntegrate(6812,Integrate(ArcCoth(Plus(c_DEFAULT,Times(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),d_DEFAULT))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCoth(Plus(c,Times(d,Cot(Plus(a,Times(b,x))))))),x),Simp(Star(Times(CI,b),Integrate(Times(x,Power(Subtract(Subtract(c,Times(CI,d)),Times(c,Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Sqr(Subtract(c,Times(CI,d))),C1)))),
IIntegrate(6813,Integrate(ArcTanh(Plus(c_DEFAULT,Times(d_DEFAULT,Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcTanh(Plus(c,Times(d,Tan(Plus(a,Times(b,x))))))),x),Negate(Simp(Star(Times(CI,b,Subtract(Plus(C1,c),Times(CI,d))),Integrate(Times(x,Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))),Power(Plus(C1,c,Times(CI,d),Times(Subtract(Plus(C1,c),Times(CI,d)),Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))))),CN1)),x)),x)),Simp(Star(Times(CI,b,Plus(C1,Negate(c),Times(CI,d))),Integrate(Times(x,Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))),Power(Plus(C1,Negate(c),Times(CN1,CI,d),Times(Plus(C1,Negate(c),Times(CI,d)),Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Sqr(Plus(c,Times(CI,d))),C1)))),
IIntegrate(6814,Integrate(ArcCoth(Plus(c_DEFAULT,Times(d_DEFAULT,Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCoth(Plus(c,Times(d,Tan(Plus(a,Times(b,x))))))),x),Negate(Simp(Star(Times(CI,b,Subtract(Plus(C1,c),Times(CI,d))),Integrate(Times(x,Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))),Power(Plus(C1,c,Times(CI,d),Times(Subtract(Plus(C1,c),Times(CI,d)),Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))))),CN1)),x)),x)),Simp(Star(Times(CI,b,Plus(C1,Negate(c),Times(CI,d))),Integrate(Times(x,Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))),Power(Plus(C1,Negate(c),Times(CN1,CI,d),Times(Plus(C1,Negate(c),Times(CI,d)),Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Sqr(Plus(c,Times(CI,d))),C1)))),
IIntegrate(6815,Integrate(ArcTanh(Plus(c_DEFAULT,Times(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),d_DEFAULT))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcTanh(Plus(c,Times(d,Cot(Plus(a,Times(b,x))))))),x),Negate(Simp(Star(Times(CI,b,Subtract(Subtract(C1,c),Times(CI,d))),Integrate(Times(x,Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))),Power(Subtract(Plus(C1,Negate(c),Times(CI,d)),Times(Subtract(Subtract(C1,c),Times(CI,d)),Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))))),CN1)),x)),x)),Simp(Star(Times(CI,b,Plus(C1,c,Times(CI,d))),Integrate(Times(x,Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))),Power(Subtract(Subtract(Plus(C1,c),Times(CI,d)),Times(Plus(C1,c,Times(CI,d)),Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Sqr(Subtract(c,Times(CI,d))),C1)))),
IIntegrate(6816,Integrate(ArcCoth(Plus(c_DEFAULT,Times(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),d_DEFAULT))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCoth(Plus(c,Times(d,Cot(Plus(a,Times(b,x))))))),x),Negate(Simp(Star(Times(CI,b,Subtract(Subtract(C1,c),Times(CI,d))),Integrate(Times(x,Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))),Power(Subtract(Plus(C1,Negate(c),Times(CI,d)),Times(Subtract(Subtract(C1,c),Times(CI,d)),Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))))),CN1)),x)),x)),Simp(Star(Times(CI,b,Plus(C1,c,Times(CI,d))),Integrate(Times(x,Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))),Power(Subtract(Subtract(Plus(C1,c),Times(CI,d)),Times(Plus(C1,c,Times(CI,d)),Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Sqr(Subtract(c,Times(CI,d))),C1)))),
IIntegrate(6817,Integrate(Times(ArcTanh(Plus(c_DEFAULT,Times(d_DEFAULT,Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),ArcTanh(Plus(c,Times(d,Tan(Plus(a,Times(b,x)))))),Power(Times(f,Plus(m,C1)),CN1)),x),Simp(Star(Times(CI,b,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Power(Plus(c,Times(CI,d),Times(c,Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0),EqQ(Sqr(Plus(c,Times(CI,d))),C1)))),
IIntegrate(6818,Integrate(Times(ArcCoth(Plus(c_DEFAULT,Times(d_DEFAULT,Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),ArcCoth(Plus(c,Times(d,Tan(Plus(a,Times(b,x)))))),Power(Times(f,Plus(m,C1)),CN1)),x),Simp(Star(Times(CI,b,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Power(Plus(c,Times(CI,d),Times(c,Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0),EqQ(Sqr(Plus(c,Times(CI,d))),C1)))),
IIntegrate(6819,Integrate(Times(ArcTanh(Plus(c_DEFAULT,Times(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),d_DEFAULT))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),ArcTanh(Plus(c,Times(d,Cot(Plus(a,Times(b,x)))))),Power(Times(f,Plus(m,C1)),CN1)),x),Simp(Star(Times(CI,b,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Power(Subtract(Subtract(c,Times(CI,d)),Times(c,Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0),EqQ(Sqr(Subtract(c,Times(CI,d))),C1)))),
IIntegrate(6820,Integrate(Times(ArcCoth(Plus(c_DEFAULT,Times(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),d_DEFAULT))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),ArcCoth(Plus(c,Times(d,Cot(Plus(a,Times(b,x)))))),Power(Times(f,Plus(m,C1)),CN1)),x),Simp(Star(Times(CI,b,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Power(Subtract(Subtract(c,Times(CI,d)),Times(c,Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0),EqQ(Sqr(Subtract(c,Times(CI,d))),C1))))
  );
}
