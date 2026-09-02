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
class IntRules198 { 
  public static IAST RULES = List( 
IIntegrate(3961,Integrate(Times(Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(Plus(d,Times(e,x)),m),Power(Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),x),And(FreeQ(List(a,b,c,d,e,m),x),IGtQ(n,C1)))),
IIntegrate(3962,Integrate(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT),Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(d,Times(e,x)),m),Power(Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),n)),x),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(3963,Integrate(Times(Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(d,Times(e,x)),m),Power(Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),n)),x),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(3964,Integrate(Times(Power(u_,m_DEFAULT),Power(Sin(v_),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),m),Power(Sin(ExpandToSum(v,x)),n)),x),And(FreeQ(m,x),IGtQ(n,C0),LinearQ(u,x),QuadraticQ(v,x),Not(And(LinearMatchQ(u,x),QuadraticMatchQ(v,x)))))),
IIntegrate(3965,Integrate(Times(Power(Cos(v_),n_DEFAULT),Power(u_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),m),Power(Cos(ExpandToSum(v,x)),n)),x),And(FreeQ(m,x),IGtQ(n,C0),LinearQ(u,x),QuadraticQ(v,x),Not(And(LinearMatchQ(u,x),QuadraticMatchQ(v,x)))))),
IIntegrate(3966,Integrate(Power(Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Subtract(Simp(Times(b,Power(Times(b,Tan(Plus(c,Times(d,x)))),Plus(n,CN1)),Power(Times(d,Plus(n,CN1)),CN1)),x),Simp(Dist(Sqr(b),Integrate(Power(Times(b,Tan(Plus(c,Times(d,x)))),Plus(n,CN2)),x),x),x)),And(FreeQ(list(b,c,d),x),GtQ(n,C1)))),
IIntegrate(3967,Integrate(Power(Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(b,Tan(Plus(c,Times(d,x)))),Plus(n,C1)),Power(Times(b,d,Plus(n,C1)),CN1)),x),Simp(Dist(Power(b,CN2),Integrate(Power(Times(b,Tan(Plus(c,Times(d,x)))),Plus(n,C2)),x),x),x)),And(FreeQ(list(b,c,d),x),LtQ(n,CN1)))),
IIntegrate(3968,Integrate($($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),x_Symbol),
    Condition(Simp(Times(CN1,Log(RemoveContent(Cos(Plus(c,Times(d,x))),x)),Power(d,CN1)),x),FreeQ(list(c,d),x))),
IIntegrate(3969,Integrate(Power(Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Simp(Dist(Times(b,Power(d,CN1)),Subst(Integrate(Times(Power(x,n),Power(Plus(Sqr(b),Sqr(x)),CN1)),x),x,Times(b,Tan(Plus(c,Times(d,x))))),x),x),And(FreeQ(List(b,c,d,n),x),Not(IntegerQ(n))))),
IIntegrate(3970,Integrate(Sqr(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Simp(Times(Subtract(Sqr(a),Sqr(b)),x),x),Simp(Times(Sqr(b),Tan(Plus(c,Times(d,x))),Power(d,CN1)),x),Simp(Dist(Times(C2,a,b),Integrate(Tan(Plus(c,Times(d,x))),x),x),x)),FreeQ(List(a,b,c,d),x))),
IIntegrate(3971,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Simp(Times(b,Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),Plus(n,CN1)),Power(Times(d,Plus(n,CN1)),CN1)),x),Simp(Dist(Times(C2,a),Integrate(Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),Plus(n,CN1)),x),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Sqr(a),Sqr(b)),C0),GtQ(n,C1)))),
IIntegrate(3972,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Simp(Times(a,Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),n),Power(Times(C2,b,d,n),CN1)),x),Simp(Dist(Power(Times(C2,a),CN1),Integrate(Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),Plus(n,C1)),x),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Sqr(a),Sqr(b)),C0),LtQ(n,C0)))),
IIntegrate(3973,Integrate(Sqrt(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Simp(Dist(Times(CN2,b,Power(d,CN1)),Subst(Integrate(Power(Subtract(Times(C2,a),Sqr(x)),CN1),x),x,Sqrt(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))))),x),x),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Sqr(a),Sqr(b)),C0)))),
IIntegrate(3974,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Simp(Dist(Times(CN1,b,Power(d,CN1)),Subst(Integrate(Times(Power(Plus(a,x),Plus(n,CN1)),Power(Subtract(a,x),CN1)),x),x,Times(b,Tan(Plus(c,Times(d,x))))),x),x),And(FreeQ(List(a,b,c,d,n),x),EqQ(Plus(Sqr(a),Sqr(b)),C0)))),
IIntegrate(3975,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Simp(Times(b,Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),Plus(n,CN1)),Power(Times(d,Plus(n,CN1)),CN1)),x),Integrate(Times(Plus(Sqr(a),Negate(Sqr(b)),Times(C2,a,b,Tan(Plus(c,Times(d,x))))),Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),Plus(n,CN2))),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Plus(Sqr(a),Sqr(b)),C0),GtQ(n,C1)))),
IIntegrate(3976,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Simp(Times(b,Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),Plus(n,C1)),Power(Times(d,Plus(n,C1),Plus(Sqr(a),Sqr(b))),CN1)),x),Simp(Dist(Power(Plus(Sqr(a),Sqr(b)),CN1),Integrate(Times(Subtract(a,Times(b,Tan(Plus(c,Times(d,x))))),Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),Plus(n,C1))),x),x),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Plus(Sqr(a),Sqr(b)),C0),LtQ(n,CN1)))),
IIntegrate(3977,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1),x_Symbol),
    Condition(Plus(Simp(Times(a,x,Power(Plus(Sqr(a),Sqr(b)),CN1)),x),Simp(Dist(Times(b,Power(Plus(Sqr(a),Sqr(b)),CN1)),Integrate(Times(Subtract(b,Times(a,Tan(Plus(c,Times(d,x))))),Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Plus(Sqr(a),Sqr(b)),C0)))),
IIntegrate(3978,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Simp(Dist(Times(b,Power(d,CN1)),Subst(Integrate(Times(Power(Plus(a,x),n),Power(Plus(Sqr(b),Sqr(x)),CN1)),x),x,Times(b,Tan(Plus(c,Times(d,x))))),x),x),And(FreeQ(List(a,b,c,d,n),x),NeQ(Plus(Sqr(a),Sqr(b)),C0)))),
IIntegrate(3979,Integrate(Times(Power(Times(d_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT),Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Simp(Times(b,Power(Times(d,Sec(Plus(e,Times(f,x)))),m),Power(Times(f,m),CN1)),x),Simp(Dist(a,Integrate(Power(Times(d,Sec(Plus(e,Times(f,x)))),m),x),x),x)),And(FreeQ(List(a,b,d,e,f,m),x),Or(IntegerQ(Times(C2,m)),NeQ(Plus(Sqr(a),Sqr(b)),C0))))),
IIntegrate(3980,Integrate(Times(Power($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),m_),Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),n_)),x_Symbol),
    Condition(Simp(Dist(Power(Times(Power(a,Plus(m,CN2)),b,f),CN1),Subst(Integrate(Times(Power(Subtract(a,x),Plus(Times(C1D2,m),CN1)),Power(Plus(a,x),Plus(n,Times(C1D2,m),CN1))),x),x,Times(b,Tan(Plus(e,Times(f,x))))),x),x),And(FreeQ(List(a,b,e,f,n),x),EqQ(Plus(Sqr(a),Sqr(b)),C0),IntegerQ(Times(C1D2,m)))))
  );
}
