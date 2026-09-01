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
class IntRules339 { 
  public static IAST RULES = List( 
IIntegrate(6781,Integrate(Times(Power(ArcTanh(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),CN1))),m_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Integrate(Times(u,Power(ArcCoth(Plus(Times(a,Power(c,CN1)),Times(b,Power(x,n),Power(c,CN1)))),m)),x),FreeQ(List(a,b,c,n,m),x))),
IIntegrate(6782,Integrate(Times(Power(ArcCoth(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),CN1))),m_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Integrate(Times(u,Power(ArcTanh(Plus(Times(a,Power(c,CN1)),Times(b,Power(x,n),Power(c,CN1)))),m)),x),FreeQ(List(a,b,c,n,m),x))),
IIntegrate(6783,Integrate(ArcTanh(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcTanh(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2)))),x),Simp(Dist(c,Integrate(Times(x,Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x),x),x)),And(FreeQ(list(a,b,c),x),EqQ(b,Sqr(c))))),
IIntegrate(6784,Integrate(ArcCoth(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcCoth(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2)))),x),Simp(Dist(c,Integrate(Times(x,Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x),x),x)),And(FreeQ(list(a,b,c),x),EqQ(b,Sqr(c))))),
IIntegrate(6785,Integrate(Times(ArcTanh(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(ArcTanh(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))),Log(x)),x),Simp(Dist(c,Integrate(Times(Log(x),Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x),x),x)),And(FreeQ(list(a,b,c),x),EqQ(b,Sqr(c))))),
IIntegrate(6786,Integrate(Times(ArcCoth(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(ArcCoth(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))),Log(x)),x),Simp(Dist(c,Integrate(Times(Log(x),Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x),x),x)),And(FreeQ(list(a,b,c),x),EqQ(b,Sqr(c))))),
IIntegrate(6787,Integrate(Times(ArcTanh(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),Plus(m,C1)),ArcTanh(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Dist(Times(c,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(d,x),Plus(m,C1)),Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x),x),x)),And(FreeQ(List(a,b,c,d,m),x),EqQ(b,Sqr(c)),NeQ(m,CN1)))),
IIntegrate(6788,Integrate(Times(ArcCoth(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),Plus(m,C1)),ArcCoth(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Dist(Times(c,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(d,x),Plus(m,C1)),Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x),x),x)),And(FreeQ(List(a,b,c,d,m),x),EqQ(b,Sqr(c)),NeQ(m,CN1)))),
IIntegrate(6789,Integrate(Times(Power(ArcTanh(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),CN1),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Times(Power(c,CN1),Log(ArcTanh(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))))),x),And(FreeQ(list(a,b,c),x),EqQ(b,Sqr(c))))),
IIntegrate(6790,Integrate(Times(Power(ArcCoth(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),CN1),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Times(CN1,Power(c,CN1),Log(ArcCoth(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))))),x),And(FreeQ(list(a,b,c),x),EqQ(b,Sqr(c))))),
IIntegrate(6791,Integrate(Times(Power(ArcTanh(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Times(Power(ArcTanh(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))),Plus(m,C1)),Power(Times(c,Plus(m,C1)),CN1)),x),And(FreeQ(List(a,b,c,m),x),EqQ(b,Sqr(c)),NeQ(m,CN1)))),
IIntegrate(6792,Integrate(Times(Power(ArcCoth(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Times(CN1,Power(ArcCoth(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))),Plus(m,C1)),Power(Times(c,Plus(m,C1)),CN1)),x),And(FreeQ(List(a,b,c,m),x),EqQ(b,Sqr(c)),NeQ(m,CN1)))),
IIntegrate(6793,Integrate(Times(Power(ArcTanh(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Dist(Times(Sqrt(Plus(a,Times(b,Sqr(x)))),Power(Plus(d,Times(e,Sqr(x))),CN1D2)),Integrate(Times(Power(ArcTanh(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))),m),Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x),x),x),And(FreeQ(List(a,b,c,d,e,m),x),EqQ(b,Sqr(c)),EqQ(Subtract(Times(b,d),Times(a,e)),C0)))),
IIntegrate(6794,Integrate(Times(Power(ArcCoth(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Dist(Times(Sqrt(Plus(a,Times(b,Sqr(x)))),Power(Plus(d,Times(e,Sqr(x))),CN1D2)),Integrate(Times(Power(ArcCoth(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))),m),Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x),x),x),And(FreeQ(List(a,b,c,d,e,m),x),EqQ(b,Sqr(c)),EqQ(Subtract(Times(b,d),Times(a,e)),C0)))),
IIntegrate(6795,Integrate(Times(u_,Power(v_,n_DEFAULT)),x_Symbol),
    Condition(With(list(Set($s("tmp"),InverseFunctionOfLinear(u,x))),Condition(Simp(Times(Power(Times(CN1,Discriminant(v,x),Power(Times(C4,Coefficient(v,x,C2)),CN1)),n),Power(Coefficient(Part($s("tmp"),C1),x,C1),CN1),Subst(Integrate(SimplifyIntegrand(Times(SubstForInverseFunction(u,$s("tmp"),x),Power(Sech(x),Times(C2,Plus(n,C1)))),x),x),x,$s("tmp"))),x),And(Not(FalseQ($s("tmp"))),EqQ(Head($s("tmp")),ArcTanh),EqQ(Subtract(Times(Discriminant(v,x),Sqr(Part($s("tmp"),C1))),Sqr(D(v,x))),C0)))),And(QuadraticQ(v,x),ILtQ(n,C0),PosQ(Discriminant(v,x)),MatchQ(u,Condition(Times(r_DEFAULT,Power(f_,w_)),FreeQ(f,x)))))),
IIntegrate(6796,Integrate(Times(u_,Power(v_,n_DEFAULT)),x_Symbol),
    Condition(With(list(Set($s("tmp"),InverseFunctionOfLinear(u,x))),Condition(Simp(Times(Power(Times(CN1,Discriminant(v,x),Power(Times(C4,Coefficient(v,x,C2)),CN1)),n),Power(Coefficient(Part($s("tmp"),C1),x,C1),CN1),Subst(Integrate(SimplifyIntegrand(Times(SubstForInverseFunction(u,$s("tmp"),x),Power(Negate(Sqr(Csch(x))),Plus(n,C1))),x),x),x,$s("tmp"))),x),And(Not(FalseQ($s("tmp"))),EqQ(Head($s("tmp")),ArcCoth),EqQ(Subtract(Times(Discriminant(v,x),Sqr(Part($s("tmp"),C1))),Sqr(D(v,x))),C0)))),And(QuadraticQ(v,x),ILtQ(n,C0),PosQ(Discriminant(v,x)),MatchQ(u,Condition(Times(r_DEFAULT,Power(f_,w_)),FreeQ(f,x)))))),
IIntegrate(6797,Integrate(ArcTanh(Plus(c_DEFAULT,Times(d_DEFAULT,Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcTanh(Plus(c,Times(d,Tanh(Plus(a,Times(b,x))))))),x),Simp(Dist(b,Integrate(Times(x,Power(Plus(c,Negate(d),Times(c,Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Sqr(Subtract(c,d)),C1)))),
IIntegrate(6798,Integrate(ArcCoth(Plus(c_DEFAULT,Times(d_DEFAULT,Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCoth(Plus(c,Times(d,Tanh(Plus(a,Times(b,x))))))),x),Simp(Dist(b,Integrate(Times(x,Power(Plus(c,Negate(d),Times(c,Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Sqr(Subtract(c,d)),C1)))),
IIntegrate(6799,Integrate(ArcTanh(Plus(c_DEFAULT,Times(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),d_DEFAULT))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcTanh(Plus(c,Times(d,Coth(Plus(a,Times(b,x))))))),x),Simp(Dist(b,Integrate(Times(x,Power(Subtract(Subtract(c,d),Times(c,Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Sqr(Subtract(c,d)),C1)))),
IIntegrate(6800,Integrate(ArcCoth(Plus(c_DEFAULT,Times(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),d_DEFAULT))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCoth(Plus(c,Times(d,Coth(Plus(a,Times(b,x))))))),x),Simp(Dist(b,Integrate(Times(x,Power(Subtract(Subtract(c,d),Times(c,Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Sqr(Subtract(c,d)),C1))))
  );
}
