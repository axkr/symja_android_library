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
class IntRules245 { 
  public static IAST RULES = List( 
IIntegrate(4901,Integrate(u_,x_Symbol),
    Condition(With(list(Set(v,FunctionOfTrig(u,x))),Condition(With(list(Set(d,FreeFactors(Tan(v),x))),Simp(Dist(Times(d,Power(Coefficient(v,x,C1),CN1)),Subst(Integrate(SubstFor(Power(Plus(C1,Times(Sqr(d),Sqr(x))),CN1),Times(Tan(v),Power(d,CN1)),u,x),x),x,Times(Tan(v),Power(d,CN1))),x),x)),And(Not(FalseQ(v)),FunctionOfQ(NonfreeFactors(Tan(v),x),u,x)))),And(InverseFunctionFreeQ(u,x),Not(MatchQ(u,Condition(Times(v_DEFAULT,Power(Times(c_DEFAULT,Power($($s("§tan"),w_),n_DEFAULT),Power($($s("§tan"),z_),n_DEFAULT)),p_DEFAULT)),And(FreeQ(list(c,p),x),IntegerQ(n),LinearQ(w,x),EqQ(z,Times(C2,w))))))))),
IIntegrate(4902,Integrate(Times(u_,Power(Times(c_DEFAULT,$($s("§sin"),v_)),m_)),x_Symbol),
    Condition(With(list(Set(w,FunctionOfTrig(Times(u,Power(Sin(Times(C1D2,v)),Times(C2,m)),Power(Power(Times(c,Tan(Times(C1D2,v))),m),CN1)),x))),Condition(Simp(Dist(Times(Power(Times(c,Sin(v)),m),Power(Times(c,Tan(Times(C1D2,v))),m),Power(Power(Sin(Times(C1D2,v)),Times(C2,m)),CN1)),Integrate(Times(u,Power(Sin(Times(C1D2,v)),Times(C2,m)),Power(Power(Times(c,Tan(Times(C1D2,v))),m),CN1)),x),x),x),And(Not(FalseQ(w)),FunctionOfQ(NonfreeFactors(Tan(w),x),Times(u,Power(Sin(Times(C1D2,v)),Times(C2,m)),Power(Power(Times(c,Tan(Times(C1D2,v))),m),CN1)),x)))),And(FreeQ(c,x),LinearQ(v,x),IntegerQ(Plus(m,C1D2)),Not(SumQ(u)),InverseFunctionFreeQ(u,x)))),
IIntegrate(4903,Integrate(Times(u_DEFAULT,Power(Plus(Times(b_DEFAULT,Power($($s("§sec"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_DEFAULT)),Times(a_DEFAULT,Power($($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_DEFAULT))),p_)),x_Symbol),
    Condition(Integrate(Times(ActivateTrig(u),Power(Sec(Plus(c,Times(d,x))),Times(n,p)),Power(Plus(b,Times(a,Power(Sin(Plus(c,Times(d,x))),n))),p)),x),And(FreeQ(List(a,b,c,d),x),IntegersQ(n,p)))),
IIntegrate(4904,Integrate(Times(Power(Plus(Times(Power($($s("§cot"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_DEFAULT),a_DEFAULT),Times(Power($($s("§csc"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_DEFAULT),b_DEFAULT)),p_),u_DEFAULT),x_Symbol),
    Condition(Integrate(Times(ActivateTrig(u),Power(Csc(Plus(c,Times(d,x))),Times(n,p)),Power(Plus(b,Times(a,Power(Cos(Plus(c,Times(d,x))),n))),p)),x),And(FreeQ(List(a,b,c,d),x),IntegersQ(n,p)))),
IIntegrate(4905,Integrate(Times(u_,Power(Plus(Times(a_,Power($(F_,Plus(c_DEFAULT,Times(d_DEFAULT,x_))),p_DEFAULT)),Times(b_DEFAULT,Power($(F_,Plus(c_DEFAULT,Times(d_DEFAULT,x_))),q_DEFAULT))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(ActivateTrig(Times(u,Power(F(Plus(c,Times(d,x))),Times(n,p)),Power(Plus(a,Times(b,Power(F(Plus(c,Times(d,x))),Subtract(q,p)))),n))),x),And(FreeQ(List(a,b,c,d,p,q),x),InertTrigQ(FSymbol),IntegerQ(n),PosQ(Subtract(q,p))))),
IIntegrate(4906,Integrate(Times(u_,Power(Plus(Times(a_,Power($(F_,Plus(d_DEFAULT,Times(e_DEFAULT,x_))),p_DEFAULT)),Times(b_DEFAULT,Power($(F_,Plus(d_DEFAULT,Times(e_DEFAULT,x_))),q_DEFAULT)),Times(c_DEFAULT,Power($(F_,Plus(d_DEFAULT,Times(e_DEFAULT,x_))),r_DEFAULT))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(ActivateTrig(Times(u,Power(F(Plus(d,Times(e,x))),Times(n,p)),Power(Plus(a,Times(b,Power(F(Plus(d,Times(e,x))),Subtract(q,p))),Times(c,Power(F(Plus(d,Times(e,x))),Subtract(r,p)))),n))),x),And(FreeQ(List(a,b,c,d,e,p,q,r),x),InertTrigQ(FSymbol),IntegerQ(n),PosQ(Subtract(q,p)),PosQ(Subtract(r,p))))),
IIntegrate(4907,Integrate(Times(u_,Power(Plus(a_,Times(b_DEFAULT,Power($(F_,Plus(d_DEFAULT,Times(e_DEFAULT,x_))),p_DEFAULT)),Times(c_DEFAULT,Power($(F_,Plus(d_DEFAULT,Times(e_DEFAULT,x_))),q_DEFAULT))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(ActivateTrig(Times(u,Power(F(Plus(d,Times(e,x))),Times(n,p)),Power(Plus(b,Times(a,Power(Power(F(Plus(d,Times(e,x))),p),CN1)),Times(c,Power(F(Plus(d,Times(e,x))),Subtract(q,p)))),n))),x),And(FreeQ(List(a,b,c,d,e,p,q),x),InertTrigQ(FSymbol),IntegerQ(n),NegQ(p)))),
IIntegrate(4908,Integrate(Times(u_DEFAULT,Power(Plus(Times($($s("§cos"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),a_DEFAULT),Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(ActivateTrig(u),Power(Times(a,Power(Exp(Times(a,Power(b,CN1),Plus(c,Times(d,x)))),CN1)),n)),x),And(FreeQ(List(a,b,c,d,n),x),EqQ(Plus(Sqr(a),Sqr(b)),C0)))),
IIntegrate(4909,Integrate(u_,x_Symbol),
    Condition(Integrate(TrigSimplify(u),x),TrigSimplifyQ(u))),
IIntegrate(4910,Integrate(Times(u_DEFAULT,Power(Times(a_,v_),p_)),x_Symbol),
    Condition(With(list(Set($s("uu"),ActivateTrig(u)),Set($s("vv"),ActivateTrig(v))),Simp(Dist(Times(Power(a,IntPart(p)),Power(Times(a,$s("vv")),FracPart(p)),Power(Power($s("vv"),FracPart(p)),CN1)),Integrate(Times($s("uu"),Power($s("vv"),p)),x),x),x)),And(FreeQ(list(a,p),x),Not(IntegerQ(p)),Not(InertTrigFreeQ(v))))),
IIntegrate(4911,Integrate(Times(u_DEFAULT,Power(Power(v_,m_),p_)),x_Symbol),
    Condition(With(list(Set($s("uu"),ActivateTrig(u)),Set($s("vv"),ActivateTrig(v))),Simp(Dist(Times(Power(Power($s("vv"),m),FracPart(p)),Power(Power($s("vv"),Times(m,FracPart(p))),CN1)),Integrate(Times($s("uu"),Power($s("vv"),Times(m,p))),x),x),x)),And(FreeQ(list(m,p),x),Not(IntegerQ(p)),Not(InertTrigFreeQ(v))))),
IIntegrate(4912,Integrate(Times(u_DEFAULT,Power(Times(Power(v_,m_DEFAULT),Power(w_,n_DEFAULT)),p_)),x_Symbol),
    Condition(With(list(Set($s("uu"),ActivateTrig(u)),Set($s("vv"),ActivateTrig(v)),Set($s("ww"),ActivateTrig(w))),Simp(Dist(Times(Power(Times(Power($s("vv"),m),Power($s("ww"),n)),FracPart(p)),Power(Times(Power($s("vv"),Times(m,FracPart(p))),Power($s("ww"),Times(n,FracPart(p)))),CN1)),Integrate(Times($s("uu"),Power($s("vv"),Times(m,p)),Power($s("ww"),Times(n,p))),x),x),x)),And(FreeQ(list(m,n,p),x),Not(IntegerQ(p)),Or(Not(InertTrigFreeQ(v)),Not(InertTrigFreeQ(w)))))),
IIntegrate(4913,Integrate(u_,x_Symbol),
    Condition(With(list(Set(v,ExpandTrig(u,x))),Condition(Integrate(v,x),SumQ(v))),Not(InertTrigFreeQ(u)))),
IIntegrate(4914,Integrate(u_,x_Symbol),
    Condition(With(list(Set(w,Block(list(Set($s("§$showsteps"),False),Set($s("§$stepcounter"),Null)),Integrate(SubstFor(Power(Plus(C1,Times(Sqr(FreeFactors(Tan(Times(C1D2,FunctionOfTrig(u,x))),x)),Sqr(x))),CN1),Times(Tan(Times(C1D2,FunctionOfTrig(u,x))),Power(FreeFactors(Tan(Times(C1D2,FunctionOfTrig(u,x))),x),CN1)),u,x),x)))),Condition(Module(list(Set(v,FunctionOfTrig(u,x)),d),Simp(CompoundExpression(Set(d,FreeFactors(Tan(Times(C1D2,v)),x)),Dist(Times(C2,d,Power(Coefficient(v,x,C1),CN1)),Subst(Integrate(SubstFor(Power(Plus(C1,Times(Sqr(d),Sqr(x))),CN1),Times(Tan(Times(C1D2,v)),Power(d,CN1)),u,x),x),x,Times(Tan(Times(C1D2,v)),Power(d,CN1))),x)),x)),CalculusFreeQ(w,x))),And(InverseFunctionFreeQ(u,x),Not(FalseQ(FunctionOfTrig(u,x)))))),
IIntegrate(4915,Integrate(u_,x_Symbol),
    Condition(With(list(Set(v,ActivateTrig(u))),CannotIntegrate(v,x)),Not(InertTrigFreeQ(u)))),
IIntegrate(4916,Integrate(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),m),Power(Sin(Plus(a,Times(b,x))),Plus(n,C1)),Power(Times(b,Plus(n,C1)),CN1)),x),Simp(Dist(Times(d,m,Power(Times(b,Plus(n,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,CN1)),Power(Sin(Plus(a,Times(b,x))),Plus(n,C1))),x),x),x)),And(FreeQ(List(a,b,c,d,n),x),IGtQ(m,C0),NeQ(n,CN1)))),
IIntegrate(4917,Integrate(Times(Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(Plus(c,Times(d,x)),m),Power(Cos(Plus(a,Times(b,x))),Plus(n,C1)),Power(Times(b,Plus(n,C1)),CN1)),x),Simp(Dist(Times(d,m,Power(Times(b,Plus(n,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,CN1)),Power(Cos(Plus(a,Times(b,x))),Plus(n,C1))),x),x),x)),And(FreeQ(List(a,b,c,d,n),x),IGtQ(m,C0),NeQ(n,CN1)))),
IIntegrate(4918,Integrate(Times(Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(Plus(c,Times(d,x)),m),Times(Power(Sin(Plus(a,Times(b,x))),n),Power(Cos(Plus(a,Times(b,x))),p)),x),x),And(FreeQ(List(a,b,c,d,m),x),IGtQ(n,C0),IGtQ(p,C0)))),
IIntegrate(4919,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT)),x_Symbol),
    Condition(Plus(Negate(Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Sin(Plus(a,Times(b,x))),n),Power(Tan(Plus(a,Times(b,x))),Plus(p,CN2))),x)),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Sin(Plus(a,Times(b,x))),Plus(n,CN2)),Power(Tan(Plus(a,Times(b,x))),p)),x)),And(FreeQ(List(a,b,c,d,m),x),IGtQ(n,C0),IGtQ(p,C0)))),
IIntegrate(4920,Integrate(Times(Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Negate(Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Cos(Plus(a,Times(b,x))),n),Power(Cot(Plus(a,Times(b,x))),Plus(p,CN2))),x)),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Cos(Plus(a,Times(b,x))),Plus(n,CN2)),Power(Cot(Plus(a,Times(b,x))),p)),x)),And(FreeQ(List(a,b,c,d,m),x),IGtQ(n,C0),IGtQ(p,C0))))
  );
}
