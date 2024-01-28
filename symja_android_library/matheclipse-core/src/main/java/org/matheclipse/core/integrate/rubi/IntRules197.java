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
class IntRules197 { 
  public static IAST RULES = List( 
IIntegrate(3941,Integrate(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Star(Times(C2,c,Power(Times(Sqr(e),Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Plus(m,C2)),Sin(Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(Subtract(Times(C2,c,d),Times(b,e)),C0),LtQ(m,CN1)))),
IIntegrate(3942,Integrate(Times(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,e,Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),x),Simp(Star(Times(Subtract(Times(C2,c,d),Times(b,e)),Power(Times(C2,c),CN1)),Integrate(Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),x)),x)),And(FreeQ(List(a,b,c,d,e),x),NeQ(Subtract(Times(C2,c,d),Times(b,e)),C0)))),
IIntegrate(3943,Integrate(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Times(e,Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),x),Simp(Star(Times(Subtract(Times(C2,c,d),Times(b,e)),Power(Times(C2,c),CN1)),Integrate(Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),x)),x)),And(FreeQ(List(a,b,c,d,e),x),NeQ(Subtract(Times(C2,c,d),Times(b,e)),C0)))),
IIntegrate(3944,Integrate(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,e,Power(Plus(d,Times(e,x)),Subtract(m,C1)),Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),x),Negate(Simp(Star(Times(Subtract(Times(b,e),Times(C2,c,d)),Power(Times(C2,c),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Subtract(m,C1)),Sin(Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),x)),Simp(Star(Times(Sqr(e),Subtract(m,C1),Power(Times(C2,c),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Subtract(m,C2)),Cos(Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),x)),And(FreeQ(List(a,b,c,d,e),x),NeQ(Subtract(Times(b,e),Times(C2,c,d)),C0),GtQ(m,C1)))),
IIntegrate(3945,Integrate(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Plus(Simp(Times(e,Power(Plus(d,Times(e,x)),Subtract(m,C1)),Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),x),Negate(Simp(Star(Times(Subtract(Times(b,e),Times(C2,c,d)),Power(Times(C2,c),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Subtract(m,C1)),Cos(Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),x)),Negate(Simp(Star(Times(Sqr(e),Subtract(m,C1),Power(Times(C2,c),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Subtract(m,C2)),Sin(Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),x))),And(FreeQ(List(a,b,c,d,e),x),NeQ(Subtract(Times(b,e),Times(C2,c,d)),C0),GtQ(m,C1)))),
IIntegrate(3946,Integrate(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(e,Plus(m,C1)),CN1)),x),Negate(Simp(Star(Times(Subtract(Times(b,e),Times(C2,c,d)),Power(Times(Sqr(e),Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Cos(Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),x)),Negate(Simp(Star(Times(C2,c,Power(Times(Sqr(e),Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Plus(m,C2)),Cos(Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),x))),And(FreeQ(List(a,b,c,d,e),x),NeQ(Subtract(Times(b,e),Times(C2,c,d)),C0),LtQ(m,CN1)))),
IIntegrate(3947,Integrate(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Star(Times(Subtract(Times(b,e),Times(C2,c,d)),Power(Times(Sqr(e),Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Sin(Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),x),Simp(Star(Times(C2,c,Power(Times(Sqr(e),Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Plus(m,C2)),Sin(Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),x)),And(FreeQ(List(a,b,c,d,e),x),NeQ(Subtract(Times(b,e),Times(C2,c,d)),C0),LtQ(m,CN1)))),
IIntegrate(3948,Integrate(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT),Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(Plus(d,Times(e,x)),m),Power(Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),x),And(FreeQ(List(a,b,c,d,e,m),x),IGtQ(n,C1)))),
IIntegrate(3949,Integrate(Times(Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(Plus(d,Times(e,x)),m),Power(Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),x),And(FreeQ(List(a,b,c,d,e,m),x),IGtQ(n,C1)))),
IIntegrate(3950,Integrate(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT),Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(d,Times(e,x)),m),Power(Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),n)),x),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(3951,Integrate(Times(Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(d,Times(e,x)),m),Power(Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),n)),x),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(3952,Integrate(Times(Power(u_,m_DEFAULT),Power(Sin(v_),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),m),Power(Sin(ExpandToSum(v,x)),n)),x),And(FreeQ(m,x),IGtQ(n,C0),LinearQ(u,x),QuadraticQ(v,x),Not(And(LinearMatchQ(u,x),QuadraticMatchQ(v,x)))))),
IIntegrate(3953,Integrate(Times(Power(Cos(v_),n_DEFAULT),Power(u_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),m),Power(Cos(ExpandToSum(v,x)),n)),x),And(FreeQ(m,x),IGtQ(n,C0),LinearQ(u,x),QuadraticQ(v,x),Not(And(LinearMatchQ(u,x),QuadraticMatchQ(v,x)))))),
IIntegrate(3954,Integrate(Power(Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Subtract(Simp(Times(b,Power(Times(b,Tan(Plus(c,Times(d,x)))),Subtract(n,C1)),Power(Times(d,Subtract(n,C1)),CN1)),x),Simp(Star(Sqr(b),Integrate(Power(Times(b,Tan(Plus(c,Times(d,x)))),Subtract(n,C2)),x)),x)),And(FreeQ(list(b,c,d),x),GtQ(n,C1)))),
IIntegrate(3955,Integrate(Power(Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(b,Tan(Plus(c,Times(d,x)))),Plus(n,C1)),Power(Times(b,d,Plus(n,C1)),CN1)),x),Simp(Star(Power(b,CN2),Integrate(Power(Times(b,Tan(Plus(c,Times(d,x)))),Plus(n,C2)),x)),x)),And(FreeQ(list(b,c,d),x),LtQ(n,CN1)))),
IIntegrate(3956,Integrate($($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),x_Symbol),
    Condition(Simp(Times(CN1,Log(RemoveContent(Cos(Plus(c,Times(d,x))),x)),Power(d,CN1)),x),FreeQ(list(c,d),x))),
IIntegrate(3957,Integrate(Power(Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Simp(Star(Times(b,Power(d,CN1)),Subst(Integrate(Times(Power(x,n),Power(Plus(Sqr(b),Sqr(x)),CN1)),x),x,Times(b,Tan(Plus(c,Times(d,x)))))),x),And(FreeQ(List(b,c,d,n),x),Not(IntegerQ(n))))),
IIntegrate(3958,Integrate(Sqr(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Simp(Times(Subtract(Sqr(a),Sqr(b)),x),x),Simp(Times(Sqr(b),Tan(Plus(c,Times(d,x))),Power(d,CN1)),x),Simp(Star(Times(C2,a,b),Integrate(Tan(Plus(c,Times(d,x))),x)),x)),FreeQ(List(a,b,c,d),x))),
IIntegrate(3959,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Simp(Times(b,Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),Subtract(n,C1)),Power(Times(d,Subtract(n,C1)),CN1)),x),Simp(Star(Times(C2,a),Integrate(Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),Subtract(n,C1)),x)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Sqr(a),Sqr(b)),C0),GtQ(n,C1)))),
IIntegrate(3960,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Simp(Times(a,Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),n),Power(Times(C2,b,d,n),CN1)),x),Simp(Star(Power(Times(C2,a),CN1),Integrate(Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),Plus(n,C1)),x)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Sqr(a),Sqr(b)),C0),LtQ(n,C0))))
  );
}
