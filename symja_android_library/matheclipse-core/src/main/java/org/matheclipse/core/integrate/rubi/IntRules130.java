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
class IntRules130 { 
  public static IAST RULES = List( 
IIntegrate(2601,Integrate(Times(Power(Times(a_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT),Power(Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(Dist(Times(Power(Cos(Plus(e,Times(f,x))),n),Power(Times(b,Tan(Plus(e,Times(f,x)))),n),Power(Power(Times(a,Sin(Plus(e,Times(f,x)))),n),CN1)),Integrate(Times(Power(Times(a,Sin(Plus(e,Times(f,x)))),Plus(m,n)),Power(Power(Cos(Plus(e,Times(f,x))),n),CN1)),x),x),And(FreeQ(List(a,b,e,f,m,n),x),Not(IntegerQ(n)),Or(ILtQ(m,C0),And(EqQ(m,C1),EqQ(n,Negate(C1D2))),IntegersQ(Subtract(m,C1D2),Subtract(n,C1D2)))))),
IIntegrate(2602,Integrate(Times(Power(Times(a_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT),Power(Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(Dist(Times(a,Power(Cos(Plus(e,Times(f,x))),Plus(n,C1)),Power(Times(b,Tan(Plus(e,Times(f,x)))),Plus(n,C1)),Power(Times(b,Power(Times(a,Sin(Plus(e,Times(f,x)))),Plus(n,C1))),CN1)),Integrate(Times(Power(Times(a,Sin(Plus(e,Times(f,x)))),Plus(m,n)),Power(Power(Cos(Plus(e,Times(f,x))),n),CN1)),x),x),And(FreeQ(List(a,b,e,f,m,n),x),Not(IntegerQ(n))))),
IIntegrate(2603,Integrate(Times(Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),a_DEFAULT),m_),Power(Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(Dist(Times(Power(Times(a,Cos(Plus(e,Times(f,x)))),FracPart(m)),Power(Times(Sec(Plus(e,Times(f,x))),Power(a,CN1)),FracPart(m))),Integrate(Times(Power(Times(b,Tan(Plus(e,Times(f,x)))),n),Power(Power(Times(Sec(Plus(e,Times(f,x))),Power(a,CN1)),m),CN1)),x),x),And(FreeQ(List(a,b,e,f,m,n),x),Not(IntegerQ(m)),Not(IntegerQ(n))))),
IIntegrate(2604,Integrate(Times(Power(Times($($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),a_DEFAULT),m_),Power(Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(Dist(Times(Power(Times(a,Cot(Plus(e,Times(f,x)))),m),Power(Times(b,Tan(Plus(e,Times(f,x)))),m)),Integrate(Power(Times(b,Tan(Plus(e,Times(f,x)))),Subtract(n,m)),x),x),And(FreeQ(List(a,b,e,f,m,n),x),Not(IntegerQ(m)),Not(IntegerQ(n))))),
IIntegrate(2605,Integrate(Times(Power(Times(a_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT),Power(Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Negate(Simp(Times(Power(Times(a,Sec(Plus(e,Times(f,x)))),m),Power(Times(b,Tan(Plus(e,Times(f,x)))),Plus(n,C1)),Power(Times(b,f,m),CN1)),x)),And(FreeQ(List(a,b,e,f,m,n),x),EqQ(Plus(m,n,C1),C0)))),
IIntegrate(2606,Integrate(Times(Power(Times(a_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT),Power(Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Dist(Times(a,Power(f,CN1)),Subst(Integrate(Times(Power(Times(a,x),Subtract(m,C1)),Power(Plus(CN1,Sqr(x)),Times(C1D2,Subtract(n,C1)))),x),x,Sec(Plus(e,Times(f,x)))),x),And(FreeQ(List(a,e,f,m),x),IntegerQ(Times(C1D2,Subtract(n,C1))),Not(And(IntegerQ(Times(C1D2,m)),LtQ(C0,m,Plus(n,C1))))))),
IIntegrate(2607,Integrate(Times(Power($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),m_),Power(Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Dist(Power(f,CN1),Subst(Integrate(Times(Power(Times(b,x),n),Power(Plus(C1,Sqr(x)),Subtract(Times(C1D2,m),C1))),x),x,Tan(Plus(e,Times(f,x)))),x),And(FreeQ(List(b,e,f,n),x),IntegerQ(Times(C1D2,m)),Not(And(IntegerQ(Times(C1D2,Subtract(n,C1))),LtQ(C0,n,Subtract(m,C1))))))),
IIntegrate(2608,Integrate(Times(Power(Times(a_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT),Power(Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(Subtract(Simp(Times(Sqr(a),Power(Times(a,Sec(Plus(e,Times(f,x)))),Subtract(m,C2)),Power(Times(b,Tan(Plus(e,Times(f,x)))),Plus(n,C1)),Power(Times(b,f,Plus(n,C1)),CN1)),x),Dist(Times(Sqr(a),Subtract(m,C2),Power(Times(Sqr(b),Plus(n,C1)),CN1)),Integrate(Times(Power(Times(a,Sec(Plus(e,Times(f,x)))),Subtract(m,C2)),Power(Times(b,Tan(Plus(e,Times(f,x)))),Plus(n,C2))),x),x)),And(FreeQ(List(a,b,e,f),x),LtQ(n,CN1),Or(GtQ(m,C1),And(EqQ(m,C1),EqQ(n,QQ(-3L,2L)))),IntegersQ(Times(C2,m),Times(C2,n))))),
IIntegrate(2609,Integrate(Times(Power(Times(a_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT),Power(Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(a,Sec(Plus(e,Times(f,x)))),m),Power(Times(b,Tan(Plus(e,Times(f,x)))),Plus(n,C1)),Power(Times(b,f,Plus(n,C1)),CN1)),x),Dist(Times(Plus(m,n,C1),Power(Times(Sqr(b),Plus(n,C1)),CN1)),Integrate(Times(Power(Times(a,Sec(Plus(e,Times(f,x)))),m),Power(Times(b,Tan(Plus(e,Times(f,x)))),Plus(n,C2))),x),x)),And(FreeQ(List(a,b,e,f,m),x),LtQ(n,CN1),IntegersQ(Times(C2,m),Times(C2,n))))),
IIntegrate(2610,Integrate(Times(Power(Times(a_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_),Power(Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(Subtract(Simp(Times(b,Power(Times(a,Sec(Plus(e,Times(f,x)))),m),Power(Times(b,Tan(Plus(e,Times(f,x)))),Subtract(n,C1)),Power(Times(f,m),CN1)),x),Dist(Times(Sqr(b),Subtract(n,C1),Power(Times(Sqr(a),m),CN1)),Integrate(Times(Power(Times(a,Sec(Plus(e,Times(f,x)))),Plus(m,C2)),Power(Times(b,Tan(Plus(e,Times(f,x)))),Subtract(n,C2))),x),x)),And(FreeQ(List(a,b,e,f),x),GtQ(n,C1),Or(LtQ(m,CN1),And(EqQ(m,CN1),EqQ(n,QQ(3L,2L)))),IntegersQ(Times(C2,m),Times(C2,n))))),
IIntegrate(2611,Integrate(Times(Power(Times(a_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT),Power(Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(Subtract(Simp(Times(b,Power(Times(a,Sec(Plus(e,Times(f,x)))),m),Power(Times(b,Tan(Plus(e,Times(f,x)))),Subtract(n,C1)),Power(Times(f,Subtract(Plus(m,n),C1)),CN1)),x),Dist(Times(Sqr(b),Subtract(n,C1),Power(Subtract(Plus(m,n),C1),CN1)),Integrate(Times(Power(Times(a,Sec(Plus(e,Times(f,x)))),m),Power(Times(b,Tan(Plus(e,Times(f,x)))),Subtract(n,C2))),x),x)),And(FreeQ(List(a,b,e,f,m),x),GtQ(n,C1),NeQ(Subtract(Plus(m,n),C1),C0),IntegersQ(Times(C2,m),Times(C2,n))))),
IIntegrate(2612,Integrate(Times(Power(Times(a_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_),Power(Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Power(Times(a,Sec(Plus(e,Times(f,x)))),m),Power(Times(b,Tan(Plus(e,Times(f,x)))),Plus(n,C1)),Power(Times(b,f,m),CN1)),x)),Dist(Times(Plus(m,n,C1),Power(Times(Sqr(a),m),CN1)),Integrate(Times(Power(Times(a,Sec(Plus(e,Times(f,x)))),Plus(m,C2)),Power(Times(b,Tan(Plus(e,Times(f,x)))),n)),x),x)),And(FreeQ(List(a,b,e,f,n),x),Or(LtQ(m,CN1),And(EqQ(m,CN1),EqQ(n,Negate(C1D2)))),IntegersQ(Times(C2,m),Times(C2,n))))),
IIntegrate(2613,Integrate(Times(Power(Times(a_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT),Power(Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(Plus(Simp(Times(Sqr(a),Power(Times(a,Sec(Plus(e,Times(f,x)))),Subtract(m,C2)),Power(Times(b,Tan(Plus(e,Times(f,x)))),Plus(n,C1)),Power(Times(b,f,Subtract(Plus(m,n),C1)),CN1)),x),Dist(Times(Sqr(a),Subtract(m,C2),Power(Subtract(Plus(m,n),C1),CN1)),Integrate(Times(Power(Times(a,Sec(Plus(e,Times(f,x)))),Subtract(m,C2)),Power(Times(b,Tan(Plus(e,Times(f,x)))),n)),x),x)),And(FreeQ(List(a,b,e,f,n),x),Or(GtQ(m,C1),And(EqQ(m,C1),EqQ(n,C1D2))),NeQ(Subtract(Plus(m,n),C1),C0),IntegersQ(Times(C2,m),Times(C2,n))))),
IIntegrate(2614,Integrate(Times($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),Power(Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),CN1D2)),x_Symbol),
    Condition(Dist(Times(Sqrt(Sin(Plus(e,Times(f,x)))),Power(Times(Sqrt(Cos(Plus(e,Times(f,x)))),Sqrt(Times(b,Tan(Plus(e,Times(f,x)))))),CN1)),Integrate(Power(Times(Sqrt(Cos(Plus(e,Times(f,x)))),Sqrt(Sin(Plus(e,Times(f,x))))),CN1),x),x),FreeQ(list(b,e,f),x))),
IIntegrate(2615,Integrate(Times(Power($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),CN1),Sqrt(Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))))),x_Symbol),
    Condition(Dist(Times(Sqrt(Cos(Plus(e,Times(f,x)))),Sqrt(Times(b,Tan(Plus(e,Times(f,x))))),Power(Sin(Plus(e,Times(f,x))),CN1D2)),Integrate(Times(Sqrt(Cos(Plus(e,Times(f,x)))),Sqrt(Sin(Plus(e,Times(f,x))))),x),x),FreeQ(list(b,e,f),x))),
IIntegrate(2616,Integrate(Times(Power(Times(a_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_),Power(Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(Dist(Times(Power(a,Plus(m,n)),Power(Times(b,Tan(Plus(e,Times(f,x)))),n),Power(Times(Power(Times(a,Sec(Plus(e,Times(f,x)))),n),Power(Times(b,Sin(Plus(e,Times(f,x)))),n)),CN1)),Integrate(Times(Power(Times(b,Sin(Plus(e,Times(f,x)))),n),Power(Power(Cos(Plus(e,Times(f,x))),Plus(m,n)),CN1)),x),x),And(FreeQ(List(a,b,e,f,m,n),x),IntegerQ(Plus(n,C1D2)),IntegerQ(Plus(m,C1D2))))),
IIntegrate(2617,Integrate(Times(Power(Times(a_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT),Power(Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(Simp(Times(Power(Times(a,Sec(Plus(e,Times(f,x)))),m),Power(Times(b,Tan(Plus(e,Times(f,x)))),Plus(n,C1)),Power(Sqr(Cos(Plus(e,Times(f,x)))),Times(C1D2,Plus(m,n,C1))),Hypergeometric2F1(Times(C1D2,Plus(n,C1)),Times(C1D2,Plus(m,n,C1)),Times(C1D2,Plus(n,C3)),Sqr(Sin(Plus(e,Times(f,x))))),Power(Times(b,f,Plus(n,C1)),CN1)),x),And(FreeQ(List(a,b,e,f,m,n),x),Not(IntegerQ(Times(C1D2,Subtract(n,C1)))),Not(IntegerQ(Times(C1D2,m)))))),
IIntegrate(2618,Integrate(Times(Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),a_DEFAULT),m_),Power(Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(Dist(Times(Power(Times(a,Csc(Plus(e,Times(f,x)))),FracPart(m)),Power(Times(Sin(Plus(e,Times(f,x))),Power(a,CN1)),FracPart(m))),Integrate(Times(Power(Times(b,Tan(Plus(e,Times(f,x)))),n),Power(Power(Times(Sin(Plus(e,Times(f,x))),Power(a,CN1)),m),CN1)),x),x),And(FreeQ(List(a,b,e,f,m,n),x),Not(IntegerQ(m)),Not(IntegerQ(n))))),
IIntegrate(2619,Integrate(Times(Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),a_DEFAULT),m_),Power(Times(b_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(Simp(Times(a,b,Power(Times(a,Csc(Plus(e,Times(f,x)))),Subtract(m,C1)),Power(Times(b,Sec(Plus(e,Times(f,x)))),Subtract(n,C1)),Power(Times(f,Subtract(n,C1)),CN1)),x),And(FreeQ(List(a,b,e,f,m,n),x),EqQ(Subtract(Plus(m,n),C2),C0),NeQ(n,C1)))),
IIntegrate(2620,Integrate(Times(Power($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),m_DEFAULT),Power($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Dist(Power(f,CN1),Subst(Integrate(Times(Power(Plus(C1,Sqr(x)),Subtract(Times(C1D2,Plus(m,n)),C1)),Power(Power(x,m),CN1)),x),x,Tan(Plus(e,Times(f,x)))),x),And(FreeQ(list(e,f),x),IntegersQ(m,n,Times(C1D2,Plus(m,n))))))
  );
}
