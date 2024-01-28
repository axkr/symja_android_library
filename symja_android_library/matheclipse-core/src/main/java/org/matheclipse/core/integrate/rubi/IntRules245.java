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
class IntRules245 { 
  public static IAST RULES = List( 
IIntegrate(4901,Integrate(u_,x_Symbol),
    Condition(With(list(Set(v,ExpandTrig(u,x))),Condition(Integrate(v,x),SumQ(v))),Not(InertTrigFreeQ(u)))),
IIntegrate(4902,Integrate(u_,x_Symbol),
    Condition(With(list(Set(w,Block(list(Set($s("§$showsteps"),False),Set($s("§$stepcounter"),Null)),Integrate(SubstFor(Power(Plus(C1,Times(Sqr(FreeFactors(Tan(Times(C1D2,FunctionOfTrig(u,x))),x)),Sqr(x))),CN1),Times(Tan(Times(C1D2,FunctionOfTrig(u,x))),Power(FreeFactors(Tan(Times(C1D2,FunctionOfTrig(u,x))),x),CN1)),u,x),x)))),Condition(Module(list(Set(v,FunctionOfTrig(u,x)),d),Simp(CompoundExpression(Set(d,FreeFactors(Tan(Times(C1D2,v)),x)),Star(Times(C2,d,Power(Coefficient(v,x,C1),CN1)),Subst(Integrate(SubstFor(Power(Plus(C1,Times(Sqr(d),Sqr(x))),CN1),Times(Tan(Times(C1D2,v)),Power(d,CN1)),u,x),x),x,Times(Tan(Times(C1D2,v)),Power(d,CN1))))),x)),CalculusFreeQ(w,x))),And(InverseFunctionFreeQ(u,x),Not(FalseQ(FunctionOfTrig(u,x)))))),
IIntegrate(4903,Integrate(u_,x_Symbol),
    Condition(With(list(Set(v,ActivateTrig(u))),CannotIntegrate(v,x)),Not(InertTrigFreeQ(u)))),
IIntegrate(4904,Integrate(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),m),Power(Sin(Plus(a,Times(b,x))),Plus(n,C1)),Power(Times(b,Plus(n,C1)),CN1)),x),Simp(Star(Times(d,m,Power(Times(b,Plus(n,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,C1)),Power(Sin(Plus(a,Times(b,x))),Plus(n,C1))),x)),x)),And(FreeQ(List(a,b,c,d,n),x),IGtQ(m,C0),NeQ(n,CN1)))),
IIntegrate(4905,Integrate(Times(Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(Plus(c,Times(d,x)),m),Power(Cos(Plus(a,Times(b,x))),Plus(n,C1)),Power(Times(b,Plus(n,C1)),CN1)),x),Simp(Star(Times(d,m,Power(Times(b,Plus(n,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,C1)),Power(Cos(Plus(a,Times(b,x))),Plus(n,C1))),x)),x)),And(FreeQ(List(a,b,c,d,n),x),IGtQ(m,C0),NeQ(n,CN1)))),
IIntegrate(4906,Integrate(Times(Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(Plus(c,Times(d,x)),m),Times(Power(Sin(Plus(a,Times(b,x))),n),Power(Cos(Plus(a,Times(b,x))),p)),x),x),And(FreeQ(List(a,b,c,d,m),x),IGtQ(n,C0),IGtQ(p,C0)))),
IIntegrate(4907,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT)),x_Symbol),
    Condition(Plus(Negate(Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Sin(Plus(a,Times(b,x))),n),Power(Tan(Plus(a,Times(b,x))),Subtract(p,C2))),x)),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Sin(Plus(a,Times(b,x))),Subtract(n,C2)),Power(Tan(Plus(a,Times(b,x))),p)),x)),And(FreeQ(List(a,b,c,d,m),x),IGtQ(n,C0),IGtQ(p,C0)))),
IIntegrate(4908,Integrate(Times(Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Negate(Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Cos(Plus(a,Times(b,x))),n),Power(Cot(Plus(a,Times(b,x))),Subtract(p,C2))),x)),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Cos(Plus(a,Times(b,x))),Subtract(n,C2)),Power(Cot(Plus(a,Times(b,x))),p)),x)),And(FreeQ(List(a,b,c,d,m),x),IGtQ(n,C0),IGtQ(p,C0)))),
IIntegrate(4909,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),m),Power(Sec(Plus(a,Times(b,x))),n),Power(Times(b,n),CN1)),x),Simp(Star(Times(d,m,Power(Times(b,n),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,C1)),Power(Sec(Plus(a,Times(b,x))),n)),x)),x)),And(FreeQ(List(a,b,c,d,n),x),EqQ(p,C1),GtQ(m,C0)))),
IIntegrate(4910,Integrate(Times(Power(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT),Power(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(Plus(c,Times(d,x)),m),Power(Csc(Plus(a,Times(b,x))),n),Power(Times(b,n),CN1)),x),Simp(Star(Times(d,m,Power(Times(b,n),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,C1)),Power(Csc(Plus(a,Times(b,x))),n)),x)),x)),And(FreeQ(List(a,b,c,d,n),x),EqQ(p,C1),GtQ(m,C0)))),
IIntegrate(4911,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Sqr(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),m),Power(Tan(Plus(a,Times(b,x))),Plus(n,C1)),Power(Times(b,Plus(n,C1)),CN1)),x),Simp(Star(Times(d,m,Power(Times(b,Plus(n,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,C1)),Power(Tan(Plus(a,Times(b,x))),Plus(n,C1))),x)),x)),And(FreeQ(List(a,b,c,d,n),x),IGtQ(m,C0),NeQ(n,CN1)))),
IIntegrate(4912,Integrate(Times(Power(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Sqr(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(Plus(c,Times(d,x)),m),Power(Cot(Plus(a,Times(b,x))),Plus(n,C1)),Power(Times(b,Plus(n,C1)),CN1)),x),Simp(Star(Times(d,m,Power(Times(b,Plus(n,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,C1)),Power(Cot(Plus(a,Times(b,x))),Plus(n,C1))),x)),x)),And(FreeQ(List(a,b,c,d,n),x),IGtQ(m,C0),NeQ(n,CN1)))),
IIntegrate(4913,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Sec(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_)),x_Symbol),
    Condition(Plus(Negate(Integrate(Times(Power(Plus(c,Times(d,x)),m),Sec(Plus(a,Times(b,x))),Power(Tan(Plus(a,Times(b,x))),Subtract(p,C2))),x)),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Sec(Plus(a,Times(b,x))),C3),Power(Tan(Plus(a,Times(b,x))),Subtract(p,C2))),x)),And(FreeQ(List(a,b,c,d,m),x),IGtQ(Times(C1D2,p),C0)))),
IIntegrate(4914,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_)),x_Symbol),
    Condition(Plus(Negate(Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Sec(Plus(a,Times(b,x))),n),Power(Tan(Plus(a,Times(b,x))),Subtract(p,C2))),x)),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Sec(Plus(a,Times(b,x))),Plus(n,C2)),Power(Tan(Plus(a,Times(b,x))),Subtract(p,C2))),x)),And(FreeQ(List(a,b,c,d,m,n),x),IGtQ(Times(C1D2,p),C0)))),
IIntegrate(4915,Integrate(Times(Power(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_),Csc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Negate(Integrate(Times(Power(Plus(c,Times(d,x)),m),Csc(Plus(a,Times(b,x))),Power(Cot(Plus(a,Times(b,x))),Subtract(p,C2))),x)),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Csc(Plus(a,Times(b,x))),C3),Power(Cot(Plus(a,Times(b,x))),Subtract(p,C2))),x)),And(FreeQ(List(a,b,c,d,m),x),IGtQ(Times(C1D2,p),C0)))),
IIntegrate(4916,Integrate(Times(Power(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_),Power(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Negate(Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Csc(Plus(a,Times(b,x))),n),Power(Cot(Plus(a,Times(b,x))),Subtract(p,C2))),x)),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Csc(Plus(a,Times(b,x))),Plus(n,C2)),Power(Cot(Plus(a,Times(b,x))),Subtract(p,C2))),x)),And(FreeQ(List(a,b,c,d,m,n),x),IGtQ(Times(C1D2,p),C0)))),
IIntegrate(4917,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT)),x_Symbol),
    Condition(Module(list(Set(u,IntHide(Times(Power(Sec(Plus(a,Times(b,x))),n),Power(Tan(Plus(a,Times(b,x))),p)),x))),Subtract(Simp(Star(Power(Plus(c,Times(d,x)),m),u),x),Simp(Star(Times(d,m),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,C1)),u),x)),x))),And(FreeQ(List(a,b,c,d,n,p),x),IGtQ(m,C0),Or(IntegerQ(Times(C1D2,n)),IntegerQ(Times(C1D2,Subtract(p,C1))))))),
IIntegrate(4918,Integrate(Times(Power(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT),Power(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Module(list(Set(u,IntHide(Times(Power(Csc(Plus(a,Times(b,x))),n),Power(Cot(Plus(a,Times(b,x))),p)),x))),Subtract(Simp(Star(Power(Plus(c,Times(d,x)),m),u),x),Simp(Star(Times(d,m),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,C1)),u),x)),x))),And(FreeQ(List(a,b,c,d,n,p),x),IGtQ(m,C0),Or(IntegerQ(Times(C1D2,n)),IntegerQ(Times(C1D2,Subtract(p,C1))))))),
IIntegrate(4919,Integrate(Times(Power(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(C2,n),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Csc(Plus(Times(C2,a),Times(C2,b,x))),n)),x)),x),And(FreeQ(List(a,b,c,d,m),x),IntegerQ(n),RationalQ(m)))),
IIntegrate(4920,Integrate(Times(Power(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT)),x_Symbol),
    Condition(Module(list(Set(u,IntHide(Times(Power(Csc(Plus(a,Times(b,x))),n),Power(Sec(Plus(a,Times(b,x))),p)),x))),Subtract(Simp(Star(Power(Plus(c,Times(d,x)),m),u),x),Simp(Star(Times(d,m),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,C1)),u),x)),x))),And(FreeQ(List(a,b,c,d),x),IntegersQ(n,p),GtQ(m,C0),NeQ(n,p))))
  );
}
