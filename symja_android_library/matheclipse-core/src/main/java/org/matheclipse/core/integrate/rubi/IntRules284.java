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
class IntRules284 { 
  public static IAST RULES = List( 
IIntegrate(5681,Integrate(Times(Power(ArcCot(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),CN1))),m_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Integrate(Times(u,Power(ArcTan(Plus(Times(a,Power(c,CN1)),Times(b,Power(x,n),Power(c,CN1)))),m)),x),FreeQ(List(a,b,c,n,m),x))),
IIntegrate(5682,Integrate(ArcTan(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcTan(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2)))),x),Simp(Dist(c,Integrate(Times(x,Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x),x),x)),And(FreeQ(list(a,b,c),x),EqQ(Plus(b,Sqr(c)),C0)))),
IIntegrate(5683,Integrate(ArcCot(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCot(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2)))),x),Simp(Dist(c,Integrate(Times(x,Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x),x),x)),And(FreeQ(list(a,b,c),x),EqQ(Plus(b,Sqr(c)),C0)))),
IIntegrate(5684,Integrate(Times(ArcTan(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(ArcTan(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))),Log(x)),x),Simp(Dist(c,Integrate(Times(Log(x),Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x),x),x)),And(FreeQ(list(a,b,c),x),EqQ(Plus(b,Sqr(c)),C0)))),
IIntegrate(5685,Integrate(Times(ArcCot(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Simp(Times(ArcCot(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))),Log(x)),x),Simp(Dist(c,Integrate(Times(Log(x),Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x),x),x)),And(FreeQ(list(a,b,c),x),EqQ(Plus(b,Sqr(c)),C0)))),
IIntegrate(5686,Integrate(Times(ArcTan(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),Plus(m,C1)),ArcTan(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Dist(Times(c,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(d,x),Plus(m,C1)),Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x),x),x)),And(FreeQ(List(a,b,c,d,m),x),EqQ(Plus(b,Sqr(c)),C0),NeQ(m,CN1)))),
IIntegrate(5687,Integrate(Times(ArcCot(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(d,x),Plus(m,C1)),ArcCot(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Dist(Times(c,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(d,x),Plus(m,C1)),Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x),x),x)),And(FreeQ(List(a,b,c,d,m),x),EqQ(Plus(b,Sqr(c)),C0),NeQ(m,CN1)))),
IIntegrate(5688,Integrate(Times(Power(ArcTan(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),CN1),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Times(Power(c,CN1),Log(ArcTan(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))))),x),And(FreeQ(list(a,b,c),x),EqQ(Plus(b,Sqr(c)),C0)))),
IIntegrate(5689,Integrate(Times(Power(ArcCot(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),CN1),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Times(CN1,Power(c,CN1),Log(ArcCot(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))))),x),And(FreeQ(list(a,b,c),x),EqQ(Plus(b,Sqr(c)),C0)))),
IIntegrate(5690,Integrate(Times(Power(ArcTan(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Times(Power(ArcTan(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))),Plus(m,C1)),Power(Times(c,Plus(m,C1)),CN1)),x),And(FreeQ(List(a,b,c,m),x),EqQ(Plus(b,Sqr(c)),C0),NeQ(m,CN1)))),
IIntegrate(5691,Integrate(Times(Power(ArcCot(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Times(CN1,Power(ArcCot(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))),Plus(m,C1)),Power(Times(c,Plus(m,C1)),CN1)),x),And(FreeQ(List(a,b,c,m),x),EqQ(Plus(b,Sqr(c)),C0),NeQ(m,CN1)))),
IIntegrate(5692,Integrate(Times(Power(ArcTan(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Dist(Times(Sqrt(Plus(a,Times(b,Sqr(x)))),Power(Plus(d,Times(e,Sqr(x))),CN1D2)),Integrate(Times(Power(ArcTan(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))),m),Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x),x),x),And(FreeQ(List(a,b,c,d,e,m),x),EqQ(Plus(b,Sqr(c)),C0),EqQ(Subtract(Times(b,d),Times(a,e)),C0)))),
IIntegrate(5693,Integrate(Times(Power(ArcCot(Times(c_DEFAULT,x_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(x_))),CN1D2))),m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Dist(Times(Sqrt(Plus(a,Times(b,Sqr(x)))),Power(Plus(d,Times(e,Sqr(x))),CN1D2)),Integrate(Times(Power(ArcCot(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))),m),Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x),x),x),And(FreeQ(List(a,b,c,d,e,m),x),EqQ(Plus(b,Sqr(c)),C0),EqQ(Subtract(Times(b,d),Times(a,e)),C0)))),
IIntegrate(5694,Integrate(Times(ArcTan(Plus(v_,Times(s_DEFAULT,Sqrt(w_)))),u_DEFAULT),x_Symbol),
    Condition(Plus(Simp(Dist(Times(Pi,C1D4,s),Integrate(u,x),x),x),Simp(Dist(C1D2,Integrate(Times(u,ArcTan(v)),x),x),x)),And(EqQ(Sqr(s),C1),EqQ(w,Plus(Sqr(v),C1))))),
IIntegrate(5695,Integrate(Times(ArcCot(Plus(v_,Times(s_DEFAULT,Sqrt(w_)))),u_DEFAULT),x_Symbol),
    Condition(Subtract(Simp(Dist(Times(Pi,C1D4,s),Integrate(u,x),x),x),Simp(Dist(C1D2,Integrate(Times(u,ArcTan(v)),x),x),x)),And(EqQ(Sqr(s),C1),EqQ(w,Plus(Sqr(v),C1))))),
IIntegrate(5696,Integrate(Times(u_,Power(v_,n_DEFAULT)),x_Symbol),
    Condition(With(list(Set($s("tmp"),InverseFunctionOfLinear(u,x))),Condition(Simp(Times(Power(Times(CN1,Discriminant(v,x),Power(Times(C4,Coefficient(v,x,C2)),CN1)),n),Power(Coefficient(Part($s("tmp"),C1),x,C1),CN1),Subst(Integrate(SimplifyIntegrand(Times(SubstForInverseFunction(u,$s("tmp"),x),Power(Sec(x),Times(C2,Plus(n,C1)))),x),x),x,$s("tmp"))),x),And(Not(FalseQ($s("tmp"))),EqQ(Head($s("tmp")),ArcTan),EqQ(Plus(Times(Discriminant(v,x),Sqr(Part($s("tmp"),C1))),Sqr(D(v,x))),C0)))),And(QuadraticQ(v,x),ILtQ(n,C0),NegQ(Discriminant(v,x)),MatchQ(u,Condition(Times(r_DEFAULT,Power(f_,w_)),FreeQ(f,x)))))),
IIntegrate(5697,Integrate(Times(u_,Power(v_,n_DEFAULT)),x_Symbol),
    Condition(With(list(Set($s("tmp"),InverseFunctionOfLinear(u,x))),Condition(Simp(Times(CN1,Power(Times(CN1,Discriminant(v,x),Power(Times(C4,Coefficient(v,x,C2)),CN1)),n),Power(Coefficient(Part($s("tmp"),C1),x,C1),CN1),Subst(Integrate(SimplifyIntegrand(Times(SubstForInverseFunction(u,$s("tmp"),x),Power(Csc(x),Times(C2,Plus(n,C1)))),x),x),x,$s("tmp"))),x),And(Not(FalseQ($s("tmp"))),EqQ(Head($s("tmp")),ArcCot),EqQ(Plus(Times(Discriminant(v,x),Sqr(Part($s("tmp"),C1))),Sqr(D(v,x))),C0)))),And(QuadraticQ(v,x),ILtQ(n,C0),NegQ(Discriminant(v,x)),MatchQ(u,Condition(Times(r_DEFAULT,Power(f_,w_)),FreeQ(f,x)))))),
IIntegrate(5698,Integrate(ArcTan(Plus(c_DEFAULT,Times(d_DEFAULT,Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcTan(Plus(c,Times(d,Tan(Plus(a,Times(b,x))))))),x),Simp(Dist(Times(CI,b),Integrate(Times(x,Power(Plus(c,Times(CI,d),Times(c,Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Sqr(Plus(c,Times(CI,d))),CN1)))),
IIntegrate(5699,Integrate(ArcCot(Plus(c_DEFAULT,Times(d_DEFAULT,Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCot(Plus(c,Times(d,Tan(Plus(a,Times(b,x))))))),x),Simp(Dist(Times(CI,b),Integrate(Times(x,Power(Plus(c,Times(CI,d),Times(c,Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Sqr(Plus(c,Times(CI,d))),CN1)))),
IIntegrate(5700,Integrate(ArcTan(Plus(c_DEFAULT,Times(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),d_DEFAULT))),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcTan(Plus(c,Times(d,Cot(Plus(a,Times(b,x))))))),x),Simp(Dist(Times(CI,b),Integrate(Times(x,Power(Subtract(Subtract(c,Times(CI,d)),Times(c,Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Sqr(Subtract(c,Times(CI,d))),CN1))))
  );
}
