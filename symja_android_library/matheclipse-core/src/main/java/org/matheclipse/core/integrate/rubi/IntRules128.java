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
class IntRules128 { 
  public static IAST RULES = List( 
IIntegrate(2561,Integrate(Times(u_DEFAULT,Power(Plus(Times(a_DEFAULT,Power(x_,m_DEFAULT)),Times(Power(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),q_DEFAULT),b_DEFAULT,Power(x_,r_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(u,Power(x,Times(p,r)),Power(Plus(Times(a,Power(x,Subtract(m,r))),Times(b,Power(Log(Times(c,Power(x,n))),q))),p)),x),And(FreeQ(List(a,b,c,m,n,p,q,r),x),IntegerQ(p)))),
IIntegrate(2562,Integrate(u_,x_Symbol),
    Condition(Integrate(DeactivateTrig(u,x),x),FunctionOfTrigOfLinearQ(u,x))),
IIntegrate(2563,Integrate(Times(Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT),n_DEFAULT),Power(Times(a_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT)),x_Symbol),
    Condition(Simp(Times(Power(Times(a,Sin(Plus(e,Times(f,x)))),Plus(m,C1)),Power(Times(b,Cos(Plus(e,Times(f,x)))),Plus(n,C1)),Power(Times(a,b,f,Plus(m,C1)),CN1)),x),And(FreeQ(List(a,b,e,f,m,n),x),EqQ(Plus(m,n,C2),C0),NeQ(m,CN1)))),
IIntegrate(2564,Integrate(Times(Power($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),n_DEFAULT),Power(Times(a_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Times(a,f),CN1),Subst(Integrate(Times(Power(x,m),Power(Subtract(C1,Times(Sqr(x),Power(a,CN2))),Times(C1D2,Subtract(n,C1)))),x),x,Times(a,Sin(Plus(e,Times(f,x))))),x),And(FreeQ(List(a,e,f,m),x),IntegerQ(Times(C1D2,Subtract(n,C1))),Not(And(IntegerQ(Times(C1D2,Subtract(m,C1))),LtQ(C0,m,n)))))),
IIntegrate(2565,Integrate(Times(Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),a_DEFAULT),m_DEFAULT),Power($($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Negate(Dist(Power(Times(a,f),CN1),Subst(Integrate(Times(Power(x,m),Power(Subtract(C1,Times(Sqr(x),Power(a,CN2))),Times(C1D2,Subtract(n,C1)))),x),x,Times(a,Cos(Plus(e,Times(f,x))))),x)),And(FreeQ(List(a,e,f,m),x),IntegerQ(Times(C1D2,Subtract(n,C1))),Not(And(IntegerQ(Times(C1D2,Subtract(m,C1))),GtQ(m,C0),LeQ(m,n)))))),
IIntegrate(2566,Integrate(Times(Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT),n_),Power(Times(a_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(a,Power(Times(a,Sin(Plus(e,Times(f,x)))),Subtract(m,C1)),Power(Times(b,Cos(Plus(e,Times(f,x)))),Plus(n,C1)),Power(Times(b,f,Plus(n,C1)),CN1)),x)),Dist(Times(Sqr(a),Subtract(m,C1),Power(Times(Sqr(b),Plus(n,C1)),CN1)),Integrate(Times(Power(Times(a,Sin(Plus(e,Times(f,x)))),Subtract(m,C2)),Power(Times(b,Cos(Plus(e,Times(f,x)))),Plus(n,C2))),x),x)),And(FreeQ(List(a,b,e,f),x),GtQ(m,C1),LtQ(n,CN1),Or(IntegersQ(Times(C2,m),Times(C2,n)),EqQ(Plus(m,n),C0))))),
IIntegrate(2567,Integrate(Times(Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),a_DEFAULT),m_),Power(Times(b_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(Plus(Simp(Times(a,Power(Times(a,Cos(Plus(e,Times(f,x)))),Subtract(m,C1)),Power(Times(b,Sin(Plus(e,Times(f,x)))),Plus(n,C1)),Power(Times(b,f,Plus(n,C1)),CN1)),x),Dist(Times(Sqr(a),Subtract(m,C1),Power(Times(Sqr(b),Plus(n,C1)),CN1)),Integrate(Times(Power(Times(a,Cos(Plus(e,Times(f,x)))),Subtract(m,C2)),Power(Times(b,Sin(Plus(e,Times(f,x)))),Plus(n,C2))),x),x)),And(FreeQ(List(a,b,e,f),x),GtQ(m,C1),LtQ(n,CN1),Or(IntegersQ(Times(C2,m),Times(C2,n)),EqQ(Plus(m,n),C0))))),
IIntegrate(2568,Integrate(Times(Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT),n_),Power(Times(a_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(a,Power(Times(b,Cos(Plus(e,Times(f,x)))),Plus(n,C1)),Power(Times(a,Sin(Plus(e,Times(f,x)))),Subtract(m,C1)),Power(Times(b,f,Plus(m,n)),CN1)),x)),Dist(Times(Sqr(a),Subtract(m,C1),Power(Plus(m,n),CN1)),Integrate(Times(Power(Times(b,Cos(Plus(e,Times(f,x)))),n),Power(Times(a,Sin(Plus(e,Times(f,x)))),Subtract(m,C2))),x),x)),And(FreeQ(List(a,b,e,f,n),x),GtQ(m,C1),NeQ(Plus(m,n),C0),IntegersQ(Times(C2,m),Times(C2,n))))),
IIntegrate(2569,Integrate(Times(Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),a_DEFAULT),m_),Power(Times(b_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(Plus(Simp(Times(a,Power(Times(b,Sin(Plus(e,Times(f,x)))),Plus(n,C1)),Power(Times(a,Cos(Plus(e,Times(f,x)))),Subtract(m,C1)),Power(Times(b,f,Plus(m,n)),CN1)),x),Dist(Times(Sqr(a),Subtract(m,C1),Power(Plus(m,n),CN1)),Integrate(Times(Power(Times(b,Sin(Plus(e,Times(f,x)))),n),Power(Times(a,Cos(Plus(e,Times(f,x)))),Subtract(m,C2))),x),x)),And(FreeQ(List(a,b,e,f,n),x),GtQ(m,C1),NeQ(Plus(m,n),C0),IntegersQ(Times(C2,m),Times(C2,n))))),
IIntegrate(2570,Integrate(Times(Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT),n_),Power(Times(a_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(b,Cos(Plus(e,Times(f,x)))),Plus(n,C1)),Power(Times(a,Sin(Plus(e,Times(f,x)))),Plus(m,C1)),Power(Times(a,b,f,Plus(m,C1)),CN1)),x),Dist(Times(Plus(m,n,C2),Power(Times(Sqr(a),Plus(m,C1)),CN1)),Integrate(Times(Power(Times(b,Cos(Plus(e,Times(f,x)))),n),Power(Times(a,Sin(Plus(e,Times(f,x)))),Plus(m,C2))),x),x)),And(FreeQ(List(a,b,e,f,n),x),LtQ(m,CN1),IntegersQ(Times(C2,m),Times(C2,n))))),
IIntegrate(2571,Integrate(Times(Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),a_DEFAULT),m_),Power(Times(b_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Power(Times(b,Sin(Plus(e,Times(f,x)))),Plus(n,C1)),Power(Times(a,Cos(Plus(e,Times(f,x)))),Plus(m,C1)),Power(Times(a,b,f,Plus(m,C1)),CN1)),x)),Dist(Times(Plus(m,n,C2),Power(Times(Sqr(a),Plus(m,C1)),CN1)),Integrate(Times(Power(Times(b,Sin(Plus(e,Times(f,x)))),n),Power(Times(a,Cos(Plus(e,Times(f,x)))),Plus(m,C2))),x),x)),And(FreeQ(List(a,b,e,f,n),x),LtQ(m,CN1),IntegersQ(Times(C2,m),Times(C2,n))))),
IIntegrate(2572,Integrate(Times(Sqrt(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT)),Sqrt(Times(a_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))))),x_Symbol),
    Condition(Dist(Times(Sqrt(Times(a,Sin(Plus(e,Times(f,x))))),Sqrt(Times(b,Cos(Plus(e,Times(f,x))))),Power(Sin(Plus(Times(C2,e),Times(C2,f,x))),CN1D2)),Integrate(Sqrt(Sin(Plus(Times(C2,e),Times(C2,f,x)))),x),x),FreeQ(List(a,b,e,f),x))),
IIntegrate(2573,Integrate(Times(Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT),CN1D2),Power(Times(a_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),CN1D2)),x_Symbol),
    Condition(Dist(Times(Sqrt(Sin(Plus(Times(C2,e),Times(C2,f,x)))),Power(Times(Sqrt(Times(a,Sin(Plus(e,Times(f,x))))),Sqrt(Times(b,Cos(Plus(e,Times(f,x)))))),CN1)),Integrate(Power(Sin(Plus(Times(C2,e),Times(C2,f,x))),CN1D2),x),x),FreeQ(List(a,b,e,f),x))),
IIntegrate(2574,Integrate(Times(Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT),n_),Power(Times(a_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_)),x_Symbol),
    Condition(With(list(Set(k,Denominator(m))),Dist(Times(k,a,b,Power(f,CN1)),Subst(Integrate(Times(Power(x,Subtract(Times(k,Plus(m,C1)),C1)),Power(Plus(Sqr(a),Times(Sqr(b),Power(x,Times(C2,k)))),CN1)),x),x,Times(Power(Times(a,Sin(Plus(e,Times(f,x)))),Power(k,CN1)),Power(Power(Times(b,Cos(Plus(e,Times(f,x)))),Power(k,CN1)),CN1))),x)),And(FreeQ(List(a,b,e,f),x),EqQ(Plus(m,n),C0),GtQ(m,C0),LtQ(m,C1)))),
IIntegrate(2575,Integrate(Times(Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),a_DEFAULT),m_),Power(Times(b_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(With(list(Set(k,Denominator(m))),Negate(Dist(Times(k,a,b,Power(f,CN1)),Subst(Integrate(Times(Power(x,Subtract(Times(k,Plus(m,C1)),C1)),Power(Plus(Sqr(a),Times(Sqr(b),Power(x,Times(C2,k)))),CN1)),x),x,Times(Power(Times(a,Cos(Plus(e,Times(f,x)))),Power(k,CN1)),Power(Power(Times(b,Sin(Plus(e,Times(f,x)))),Power(k,CN1)),CN1))),x))),And(FreeQ(List(a,b,e,f),x),EqQ(Plus(m,n),C0),GtQ(m,C0),LtQ(m,C1)))),
IIntegrate(2576,Integrate(Times(Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),a_DEFAULT),m_),Power(Times(b_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(Negate(Simp(Times(Power(b,Plus(Times(C2,IntPart(Times(C1D2,Subtract(n,C1)))),C1)),Power(Times(b,Sin(Plus(e,Times(f,x)))),Times(C2,FracPart(Times(C1D2,Subtract(n,C1))))),Power(Times(a,Cos(Plus(e,Times(f,x)))),Plus(m,C1)),Hypergeometric2F1(Times(C1D2,Plus(C1,m)),Times(C1D2,Subtract(C1,n)),Times(C1D2,Plus(C3,m)),Sqr(Cos(Plus(e,Times(f,x))))),Power(Times(a,f,Plus(m,C1),Power(Sqr(Sin(Plus(e,Times(f,x)))),FracPart(Times(C1D2,Subtract(n,C1))))),CN1)),x)),And(FreeQ(List(a,b,e,f,m,n),x),SimplerQ(n,m)))),
IIntegrate(2577,Integrate(Times(Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT),n_),Power(Times(a_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_)),x_Symbol),
    Condition(Simp(Times(Power(b,Plus(Times(C2,IntPart(Times(C1D2,Subtract(n,C1)))),C1)),Power(Times(b,Cos(Plus(e,Times(f,x)))),Times(C2,FracPart(Times(C1D2,Subtract(n,C1))))),Power(Times(a,Sin(Plus(e,Times(f,x)))),Plus(m,C1)),Hypergeometric2F1(Times(C1D2,Plus(C1,m)),Times(C1D2,Subtract(C1,n)),Times(C1D2,Plus(C3,m)),Sqr(Sin(Plus(e,Times(f,x))))),Power(Times(a,f,Plus(m,C1),Power(Sqr(Cos(Plus(e,Times(f,x)))),FracPart(Times(C1D2,Subtract(n,C1))))),CN1)),x),FreeQ(List(a,b,e,f,m,n),x))),
IIntegrate(2578,Integrate(Times(Power(Times(b_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_DEFAULT),Power(Times(a_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT)),x_Symbol),
    Condition(Simp(Times(b,Power(Times(a,Sin(Plus(e,Times(f,x)))),Plus(m,C1)),Power(Times(b,Sec(Plus(e,Times(f,x)))),Subtract(n,C1)),Power(Times(a,f,Plus(m,C1)),CN1)),x),And(FreeQ(List(a,b,e,f,m,n),x),EqQ(Plus(m,Negate(n),C2),C0),NeQ(m,CN1)))),
IIntegrate(2579,Integrate(Times(Power(Times(b_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_),Power(Times(a_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_)),x_Symbol),
    Condition(Subtract(Simp(Times(a,b,Power(Times(a,Sin(Plus(e,Times(f,x)))),Subtract(m,C1)),Power(Times(b,Sec(Plus(e,Times(f,x)))),Subtract(n,C1)),Power(Times(f,Subtract(n,C1)),CN1)),x),Dist(Times(Sqr(a),Sqr(b),Subtract(m,C1),Power(Subtract(n,C1),CN1)),Integrate(Times(Power(Times(a,Sin(Plus(e,Times(f,x)))),Subtract(m,C2)),Power(Times(b,Sec(Plus(e,Times(f,x)))),Subtract(n,C2))),x),x)),And(FreeQ(List(a,b,e,f),x),GtQ(n,C1),GtQ(m,C1),IntegersQ(Times(C2,m),Times(C2,n))))),
IIntegrate(2580,Integrate(Times(Power(Times(b_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_),Power(Times(a_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(a,Sin(Plus(e,Times(f,x)))),Plus(m,C1)),Power(Times(b,Sec(Plus(e,Times(f,x)))),Plus(n,C1)),Power(Times(a,b,f,Subtract(m,n)),CN1)),x),Dist(Times(Plus(n,C1),Power(Times(Sqr(b),Subtract(m,n)),CN1)),Integrate(Times(Power(Times(a,Sin(Plus(e,Times(f,x)))),m),Power(Times(b,Sec(Plus(e,Times(f,x)))),Plus(n,C2))),x),x)),And(FreeQ(List(a,b,e,f,m),x),GtQ(n,C1),IntegersQ(Times(C2,m),Times(C2,n)))))
  );
}
