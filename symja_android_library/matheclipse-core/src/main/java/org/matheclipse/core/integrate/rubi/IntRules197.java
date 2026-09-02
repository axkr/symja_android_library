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
class IntRules197 { 
  public static IAST RULES = List( 
IIntegrate(3941,Integrate(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Plus(Simp(Dist(Cos(Times(Subtract(Sqr(b),Times(C4,a,c)),Power(Times(C4,c),CN1))),Integrate(Cos(Times(Sqr(Plus(b,Times(C2,c,x))),Power(Times(C4,c),CN1))),x),x),x),Simp(Dist(Sin(Times(Subtract(Sqr(b),Times(C4,a,c)),Power(Times(C4,c),CN1))),Integrate(Sin(Times(Sqr(Plus(b,Times(C2,c,x))),Power(Times(C4,c),CN1))),x),x),x)),And(FreeQ(list(a,b,c),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0)))),
IIntegrate(3942,Integrate(Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),x),And(FreeQ(list(a,b,c),x),IGtQ(n,C1)))),
IIntegrate(3943,Integrate(Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),x),And(FreeQ(list(a,b,c),x),IGtQ(n,C1)))),
IIntegrate(3944,Integrate(Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_DEFAULT),x_Symbol),
    Condition(Unintegrable(Power(Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(3945,Integrate(Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_DEFAULT),x_Symbol),
    Condition(Unintegrable(Power(Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(3946,Integrate(Power(Sin(v_),n_DEFAULT),x_Symbol),
    Condition(Integrate(Power(Sin(ExpandToSum(v,x)),n),x),And(IGtQ(n,C0),QuadraticQ(v,x),Not(QuadraticMatchQ(v,x))))),
IIntegrate(3947,Integrate(Power(Cos(v_),n_DEFAULT),x_Symbol),
    Condition(Integrate(Power(Cos(ExpandToSum(v,x)),n),x),And(IGtQ(n,C0),QuadraticQ(v,x),Not(QuadraticMatchQ(v,x))))),
IIntegrate(3948,Integrate(Times(Plus(d_,Times(e_DEFAULT,x_)),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Simp(Times(CN1,e,Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(Subtract(Times(C2,c,d),Times(b,e)),C0)))),
IIntegrate(3949,Integrate(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Plus(d_,Times(e_DEFAULT,x_))),x_Symbol),
    Condition(Simp(Times(e,Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(Subtract(Times(C2,c,d),Times(b,e)),C0)))),
IIntegrate(3950,Integrate(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,e,Power(Plus(d,Times(e,x)),Plus(m,CN1)),Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),x),Simp(Dist(Times(Sqr(e),Plus(m,CN1),Power(Times(C2,c),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Plus(m,CN2)),Cos(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),x),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(Subtract(Times(C2,c,d),Times(b,e)),C0),GtQ(m,C1)))),
IIntegrate(3951,Integrate(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Subtract(Simp(Times(e,Power(Plus(d,Times(e,x)),Plus(m,CN1)),Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),x),Simp(Dist(Times(Sqr(e),Plus(m,CN1),Power(Times(C2,c),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Plus(m,CN2)),Sin(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),x),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(Subtract(Times(C2,c,d),Times(b,e)),C0),GtQ(m,C1)))),
IIntegrate(3952,Integrate(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Dist(Times(C2,c,Power(Times(Sqr(e),Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Plus(m,C2)),Cos(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),x),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(Subtract(Times(C2,c,d),Times(b,e)),C0),LtQ(m,CN1)))),
IIntegrate(3953,Integrate(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Dist(Times(C2,c,Power(Times(Sqr(e),Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Plus(m,C2)),Sin(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),x),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(Subtract(Times(C2,c,d),Times(b,e)),C0),LtQ(m,CN1)))),
IIntegrate(3954,Integrate(Times(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,e,Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),x),Simp(Dist(Times(Subtract(Times(C2,c,d),Times(b,e)),Power(Times(C2,c),CN1)),Integrate(Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),x),x),x)),And(FreeQ(List(a,b,c,d,e),x),NeQ(Subtract(Times(C2,c,d),Times(b,e)),C0)))),
IIntegrate(3955,Integrate(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Times(e,Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),x),Simp(Dist(Times(Subtract(Times(C2,c,d),Times(b,e)),Power(Times(C2,c),CN1)),Integrate(Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),x),x),x)),And(FreeQ(List(a,b,c,d,e),x),NeQ(Subtract(Times(C2,c,d),Times(b,e)),C0)))),
IIntegrate(3956,Integrate(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,e,Power(Plus(d,Times(e,x)),Plus(m,CN1)),Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),x),Negate(Simp(Dist(Times(Subtract(Times(b,e),Times(C2,c,d)),Power(Times(C2,c),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Plus(m,CN1)),Sin(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),x),x)),Simp(Dist(Times(Sqr(e),Plus(m,CN1),Power(Times(C2,c),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Plus(m,CN2)),Cos(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),x),x)),And(FreeQ(List(a,b,c,d,e),x),NeQ(Subtract(Times(b,e),Times(C2,c,d)),C0),GtQ(m,C1)))),
IIntegrate(3957,Integrate(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Plus(Simp(Times(e,Power(Plus(d,Times(e,x)),Plus(m,CN1)),Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),x),Negate(Simp(Dist(Times(Subtract(Times(b,e),Times(C2,c,d)),Power(Times(C2,c),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Plus(m,CN1)),Cos(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),x),x)),Negate(Simp(Dist(Times(Sqr(e),Plus(m,CN1),Power(Times(C2,c),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Plus(m,CN2)),Sin(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),x),x))),And(FreeQ(List(a,b,c,d,e),x),NeQ(Subtract(Times(b,e),Times(C2,c,d)),C0),GtQ(m,C1)))),
IIntegrate(3958,Integrate(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(e,Plus(m,C1)),CN1)),x),Negate(Simp(Dist(Times(Subtract(Times(b,e),Times(C2,c,d)),Power(Times(Sqr(e),Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Cos(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),x),x)),Negate(Simp(Dist(Times(C2,c,Power(Times(Sqr(e),Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Plus(m,C2)),Cos(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),x),x))),And(FreeQ(List(a,b,c,d,e),x),NeQ(Subtract(Times(b,e),Times(C2,c,d)),C0),LtQ(m,CN1)))),
IIntegrate(3959,Integrate(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Dist(Times(Subtract(Times(b,e),Times(C2,c,d)),Power(Times(Sqr(e),Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Sin(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),x),x),Simp(Dist(Times(C2,c,Power(Times(Sqr(e),Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Plus(m,C2)),Sin(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),x),x)),And(FreeQ(List(a,b,c,d,e),x),NeQ(Subtract(Times(b,e),Times(C2,c,d)),C0),LtQ(m,CN1)))),
IIntegrate(3960,Integrate(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT),Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(Plus(d,Times(e,x)),m),Power(Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),x),And(FreeQ(List(a,b,c,d,e,m),x),IGtQ(n,C1))))
  );
}
