package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class IntRules311 { 
  public static IAST RULES = List( 
IIntegrate(6221,Integrate(Times(ArcTanh(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),Plus(m,C1)),ArcTanh(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))),Power(Times(d,Plus(m,C1)),CN1)),x),Dist(Times(c,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(d,x),Plus(m,C1)),Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x),x)),And(FreeQ(List(a,b,c,d,m),x),EqQ(b,Sqr(c)),NeQ(m,CN1)))),
IIntegrate(6222,Integrate(Times(ArcCoth(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),Plus(m,C1)),ArcCoth(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))),Power(Times(d,Plus(m,C1)),CN1)),x),Dist(Times(c,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(d,x),Plus(m,C1)),Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x),x)),And(FreeQ(List(a,b,c,d,m),x),EqQ(b,Sqr(c)),NeQ(m,CN1)))),
IIntegrate(6223,Integrate(Times(Power(ArcTanh(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),CN1),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Times(C1,Log(ArcTanh(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2)))),Power(c,CN1)),x),And(FreeQ(List(a,b,c),x),EqQ(b,Sqr(c))))),
IIntegrate(6224,Integrate(Times(Power(ArcCoth(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),CN1),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Negate(Simp(Times(Log(ArcCoth(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2)))),Power(c,CN1)),x)),And(FreeQ(List(a,b,c),x),EqQ(b,Sqr(c))))),
IIntegrate(6225,Integrate(Times(Power(ArcTanh(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Times(Power(ArcTanh(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))),Plus(m,C1)),Power(Times(c,Plus(m,C1)),CN1)),x),And(FreeQ(List(a,b,c,m),x),EqQ(b,Sqr(c)),NeQ(m,CN1)))),
IIntegrate(6226,Integrate(Times(Power(ArcCoth(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Negate(Simp(Times(Power(ArcCoth(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))),Plus(m,C1)),Power(Times(c,Plus(m,C1)),CN1)),x)),And(FreeQ(List(a,b,c,m),x),EqQ(b,Sqr(c)),NeQ(m,CN1)))),
IIntegrate(6227,Integrate(Times(Power(ArcTanh(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Dist(Times(Sqrt(Plus(a,Times(b,Sqr(x)))),Power(Plus(d,Times(e,Sqr(x))),CN1D2)),Integrate(Times(Power(ArcTanh(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))),m),Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x),x),And(FreeQ(List(a,b,c,d,e,m),x),EqQ(b,Sqr(c)),EqQ(Subtract(Times(b,d),Times(a,e)),C0)))),
IIntegrate(6228,Integrate(Times(Power(ArcCoth(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Dist(Times(Sqrt(Plus(a,Times(b,Sqr(x)))),Power(Plus(d,Times(e,Sqr(x))),CN1D2)),Integrate(Times(Power(ArcCoth(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))),m),Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x),x),And(FreeQ(List(a,b,c,d,e,m),x),EqQ(b,Sqr(c)),EqQ(Subtract(Times(b,d),Times(a,e)),C0)))),
IIntegrate(6229,Integrate(Times(u_,Power(v_,n_DEFAULT)),x_Symbol),
    Condition(With(List(Set($s("tmp"),InverseFunctionOfLinear(u,x))),Condition(Dist(Times(Power(Times(CN1,Discriminant(v,x),Power(Times(C4,Coefficient(v,x,C2)),CN1)),n),Power(Coefficient(Part($s("tmp"),C1),x,C1),CN1)),Subst(Integrate(SimplifyIntegrand(Times(SubstForInverseFunction(u,$s("tmp"),x),Power(Sech(x),Times(C2,Plus(n,C1)))),x),x),x,$s("tmp")),x),And(Not(FalseQ($s("tmp"))),EqQ(Head($s("tmp")),ArcTanh),EqQ(Subtract(Times(Discriminant(v,x),Sqr(Part($s("tmp"),C1))),Sqr(D(v,x))),C0)))),And(QuadraticQ(v,x),ILtQ(n,C0),PosQ(Discriminant(v,x)),MatchQ(u,Condition(Times(r_DEFAULT,Power(f_,w_)),FreeQ(f,x)))))),
IIntegrate(6230,Integrate(Times(u_,Power(v_,n_DEFAULT)),x_Symbol),
    Condition(With(List(Set($s("tmp"),InverseFunctionOfLinear(u,x))),Condition(Dist(Times(Power(Times(CN1,Discriminant(v,x),Power(Times(C4,Coefficient(v,x,C2)),CN1)),n),Power(Coefficient(Part($s("tmp"),C1),x,C1),CN1)),Subst(Integrate(SimplifyIntegrand(Times(SubstForInverseFunction(u,$s("tmp"),x),Power(Negate(Sqr(Csch(x))),Plus(n,C1))),x),x),x,$s("tmp")),x),And(Not(FalseQ($s("tmp"))),EqQ(Head($s("tmp")),ArcCoth),EqQ(Subtract(Times(Discriminant(v,x),Sqr(Part($s("tmp"),C1))),Sqr(D(v,x))),C0)))),And(QuadraticQ(v,x),ILtQ(n,C0),PosQ(Discriminant(v,x)),MatchQ(u,Condition(Times(r_DEFAULT,Power(f_,w_)),FreeQ(f,x)))))),
IIntegrate(6231,Integrate(ArcTanh(Plus(c_DEFAULT,Times(d_DEFAULT,Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcTanh(Plus(c,Times(d,Tanh(Plus(a,Times(b,x))))))),x),Dist(b,Integrate(Times(x,Power(Plus(c,Negate(d),Times(c,Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Sqr(Subtract(c,d)),C1)))),
IIntegrate(6232,Integrate(ArcCoth(Plus(c_DEFAULT,Times(d_DEFAULT,Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCoth(Plus(c,Times(d,Tanh(Plus(a,Times(b,x))))))),x),Dist(b,Integrate(Times(x,Power(Plus(c,Negate(d),Times(c,Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Sqr(Subtract(c,d)),C1)))),
IIntegrate(6233,Integrate(ArcTanh(Plus(c_DEFAULT,Times(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),d_DEFAULT))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcTanh(Plus(c,Times(d,Coth(Plus(a,Times(b,x))))))),x),Dist(b,Integrate(Times(x,Power(Subtract(Subtract(c,d),Times(c,Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Sqr(Subtract(c,d)),C1)))),
IIntegrate(6234,Integrate(ArcCoth(Plus(c_DEFAULT,Times(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),d_DEFAULT))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCoth(Plus(c,Times(d,Coth(Plus(a,Times(b,x))))))),x),Dist(b,Integrate(Times(x,Power(Subtract(Subtract(c,d),Times(c,Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Sqr(Subtract(c,d)),C1)))),
IIntegrate(6235,Integrate(ArcTanh(Plus(c_DEFAULT,Times(d_DEFAULT,Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcTanh(Plus(c,Times(d,Tanh(Plus(a,Times(b,x))))))),x),Dist(Times(b,Subtract(Subtract(C1,c),d)),Integrate(Times(x,Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Plus(C1,Negate(c),d,Times(Subtract(Subtract(C1,c),d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x),Negate(Dist(Times(b,Plus(C1,c,d)),Integrate(Times(x,Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Plus(C1,c,Negate(d),Times(Plus(C1,c,d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x))),And(FreeQ(List(a,b,c,d),x),NeQ(Sqr(Subtract(c,d)),C1)))),
IIntegrate(6236,Integrate(ArcCoth(Plus(c_DEFAULT,Times(d_DEFAULT,Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCoth(Plus(c,Times(d,Tanh(Plus(a,Times(b,x))))))),x),Dist(Times(b,Subtract(Subtract(C1,c),d)),Integrate(Times(x,Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Plus(C1,Negate(c),d,Times(Subtract(Subtract(C1,c),d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x),Negate(Dist(Times(b,Plus(C1,c,d)),Integrate(Times(x,Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Plus(C1,c,Negate(d),Times(Plus(C1,c,d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x))),And(FreeQ(List(a,b,c,d),x),NeQ(Sqr(Subtract(c,d)),C1)))),
IIntegrate(6237,Integrate(ArcTanh(Plus(c_DEFAULT,Times(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),d_DEFAULT))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcTanh(Plus(c,Times(d,Coth(Plus(a,Times(b,x))))))),x),Negate(Dist(Times(b,Subtract(Subtract(C1,c),d)),Integrate(Times(x,Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Subtract(Plus(C1,Negate(c),d),Times(Subtract(Subtract(C1,c),d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x)),Dist(Times(b,Plus(C1,c,d)),Integrate(Times(x,Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Subtract(Subtract(Plus(C1,c),d),Times(Plus(C1,c,d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Sqr(Subtract(c,d)),C1)))),
IIntegrate(6238,Integrate(ArcCoth(Plus(c_DEFAULT,Times(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),d_DEFAULT))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCoth(Plus(c,Times(d,Coth(Plus(a,Times(b,x))))))),x),Negate(Dist(Times(b,Subtract(Subtract(C1,c),d)),Integrate(Times(x,Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Subtract(Plus(C1,Negate(c),d),Times(Subtract(Subtract(C1,c),d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x)),Dist(Times(b,Plus(C1,c,d)),Integrate(Times(x,Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Subtract(Subtract(Plus(C1,c),d),Times(Plus(C1,c,d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Sqr(Subtract(c,d)),C1)))),
IIntegrate(6239,Integrate(Times(ArcTanh(Plus(c_DEFAULT,Times(d_DEFAULT,Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),ArcTanh(Plus(c,Times(d,Tanh(Plus(a,Times(b,x)))))),Power(Times(f,Plus(m,C1)),CN1)),x),Dist(Times(b,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Power(Plus(c,Negate(d),Times(c,Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0),EqQ(Sqr(Subtract(c,d)),C1)))),
IIntegrate(6240,Integrate(Times(ArcCoth(Plus(c_DEFAULT,Times(d_DEFAULT,Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),ArcCoth(Plus(c,Times(d,Tanh(Plus(a,Times(b,x)))))),Power(Times(f,Plus(m,C1)),CN1)),x),Dist(Times(b,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Power(Plus(c,Negate(d),Times(c,Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0),EqQ(Sqr(Subtract(c,d)),C1))))
  );
}
