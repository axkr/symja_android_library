package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class IntRules220 { 
  public static IAST RULES = List( 
IIntegrate(4401,Integrate(u_,x_Symbol),
    Condition(With(List(Set(v,ExpandTrig(u,x))),Condition(Integrate(v,x),SumQ(v))),Not(InertTrigFreeQ(u)))),
IIntegrate(4402,Integrate(u_,x_Symbol),
    Condition(With(List(Set(w,Block(List(Set($s("§$showsteps"),False),Set($s("§$stepcounter"),Null)),Integrate(SubstFor(Power(Plus(C1,Times(Sqr(FreeFactors(Tan(Times(C1D2,FunctionOfTrig(u,x))),x)),Sqr(x))),CN1),Times(Tan(Times(C1D2,FunctionOfTrig(u,x))),Power(FreeFactors(Tan(Times(C1D2,FunctionOfTrig(u,x))),x),CN1)),u,x),x)))),Condition(Module(List(Set(v,FunctionOfTrig(u,x)),d),Simp(CompoundExpression(Set(d,FreeFactors(Tan(Times(C1D2,v)),x)),Dist(Times(C2,d,Power(Coefficient(v,x,C1),CN1)),Subst(Integrate(SubstFor(Power(Plus(C1,Times(Sqr(d),Sqr(x))),CN1),Times(Tan(Times(C1D2,v)),Power(d,CN1)),u,x),x),x,Times(Tan(Times(C1D2,v)),Power(d,CN1))),x)),x)),CalculusFreeQ(w,x))),And(InverseFunctionFreeQ(u,x),Not(FalseQ(FunctionOfTrig(u,x)))))),
IIntegrate(4403,Integrate(u_,x_Symbol),
    Condition(With(List(Set(v,ActivateTrig(u))),CannotIntegrate(v,x)),Not(InertTrigFreeQ(u)))),
IIntegrate(4404,Integrate(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),m),Power(Sin(Plus(a,Times(b,x))),Plus(n,C1)),Power(Times(b,Plus(n,C1)),CN1)),x),Dist(Times(d,m,Power(Times(b,Plus(n,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,C1)),Power(Sin(Plus(a,Times(b,x))),Plus(n,C1))),x),x)),And(FreeQ(List(a,b,c,d,n),x),IGtQ(m,C0),NeQ(n,CN1)))),
IIntegrate(4405,Integrate(Times(Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Power(Plus(c,Times(d,x)),m),Power(Cos(Plus(a,Times(b,x))),Plus(n,C1)),Power(Times(b,Plus(n,C1)),CN1)),x)),Dist(Times(d,m,Power(Times(b,Plus(n,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,C1)),Power(Cos(Plus(a,Times(b,x))),Plus(n,C1))),x),x)),And(FreeQ(List(a,b,c,d,n),x),IGtQ(m,C0),NeQ(n,CN1)))),
IIntegrate(4406,Integrate(Times(Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(Plus(c,Times(d,x)),m),Times(Power(Sin(Plus(a,Times(b,x))),n),Power(Cos(Plus(a,Times(b,x))),p)),x),x),And(FreeQ(List(a,b,c,d,m),x),IGtQ(n,C0),IGtQ(p,C0)))),
IIntegrate(4407,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT)),x_Symbol),
    Condition(Plus(Negate(Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Sin(Plus(a,Times(b,x))),n),Power(Tan(Plus(a,Times(b,x))),Subtract(p,C2))),x)),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Sin(Plus(a,Times(b,x))),Subtract(n,C2)),Power(Tan(Plus(a,Times(b,x))),p)),x)),And(FreeQ(List(a,b,c,d,m),x),IGtQ(n,C0),IGtQ(p,C0)))),
IIntegrate(4408,Integrate(Times(Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Negate(Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Cos(Plus(a,Times(b,x))),n),Power(Cot(Plus(a,Times(b,x))),Subtract(p,C2))),x)),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Cos(Plus(a,Times(b,x))),Subtract(n,C2)),Power(Cot(Plus(a,Times(b,x))),p)),x)),And(FreeQ(List(a,b,c,d,m),x),IGtQ(n,C0),IGtQ(p,C0)))),
IIntegrate(4409,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),m),Power(Sec(Plus(a,Times(b,x))),n),Power(Times(b,n),CN1)),x),Dist(Times(d,m,Power(Times(b,n),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,C1)),Power(Sec(Plus(a,Times(b,x))),n)),x),x)),And(FreeQ(List(a,b,c,d,n),x),EqQ(p,C1),GtQ(m,C0)))),
IIntegrate(4410,Integrate(Times(Power(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT),Power(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Power(Plus(c,Times(d,x)),m),Power(Csc(Plus(a,Times(b,x))),n),Power(Times(b,n),CN1)),x)),Dist(Times(d,m,Power(Times(b,n),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,C1)),Power(Csc(Plus(a,Times(b,x))),n)),x),x)),And(FreeQ(List(a,b,c,d,n),x),EqQ(p,C1),GtQ(m,C0)))),
IIntegrate(4411,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Sqr(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),m),Power(Tan(Plus(a,Times(b,x))),Plus(n,C1)),Power(Times(b,Plus(n,C1)),CN1)),x),Dist(Times(d,m,Power(Times(b,Plus(n,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,C1)),Power(Tan(Plus(a,Times(b,x))),Plus(n,C1))),x),x)),And(FreeQ(List(a,b,c,d,n),x),IGtQ(m,C0),NeQ(n,CN1)))),
IIntegrate(4412,Integrate(Times(Power(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Sqr(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Power(Plus(c,Times(d,x)),m),Power(Cot(Plus(a,Times(b,x))),Plus(n,C1)),Power(Times(b,Plus(n,C1)),CN1)),x)),Dist(Times(d,m,Power(Times(b,Plus(n,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,C1)),Power(Cot(Plus(a,Times(b,x))),Plus(n,C1))),x),x)),And(FreeQ(List(a,b,c,d,n),x),IGtQ(m,C0),NeQ(n,CN1)))),
IIntegrate(4413,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Sec(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_)),x_Symbol),
    Condition(Plus(Negate(Integrate(Times(Power(Plus(c,Times(d,x)),m),Sec(Plus(a,Times(b,x))),Power(Tan(Plus(a,Times(b,x))),Subtract(p,C2))),x)),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Sec(Plus(a,Times(b,x))),C3),Power(Tan(Plus(a,Times(b,x))),Subtract(p,C2))),x)),And(FreeQ(List(a,b,c,d,m),x),IGtQ(Times(C1D2,p),C0)))),
IIntegrate(4414,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_)),x_Symbol),
    Condition(Plus(Negate(Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Sec(Plus(a,Times(b,x))),n),Power(Tan(Plus(a,Times(b,x))),Subtract(p,C2))),x)),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Sec(Plus(a,Times(b,x))),Plus(n,C2)),Power(Tan(Plus(a,Times(b,x))),Subtract(p,C2))),x)),And(FreeQ(List(a,b,c,d,m,n),x),IGtQ(Times(C1D2,p),C0)))),
IIntegrate(4415,Integrate(Times(Power(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_),Csc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Negate(Integrate(Times(Power(Plus(c,Times(d,x)),m),Csc(Plus(a,Times(b,x))),Power(Cot(Plus(a,Times(b,x))),Subtract(p,C2))),x)),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Csc(Plus(a,Times(b,x))),C3),Power(Cot(Plus(a,Times(b,x))),Subtract(p,C2))),x)),And(FreeQ(List(a,b,c,d,m),x),IGtQ(Times(C1D2,p),C0)))),
IIntegrate(4416,Integrate(Times(Power(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_),Power(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Negate(Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Csc(Plus(a,Times(b,x))),n),Power(Cot(Plus(a,Times(b,x))),Subtract(p,C2))),x)),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Csc(Plus(a,Times(b,x))),Plus(n,C2)),Power(Cot(Plus(a,Times(b,x))),Subtract(p,C2))),x)),And(FreeQ(List(a,b,c,d,m,n),x),IGtQ(Times(C1D2,p),C0)))),
IIntegrate(4417,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(u,IntHide(Times(Power(Sec(Plus(a,Times(b,x))),n),Power(Tan(Plus(a,Times(b,x))),p)),x))),Subtract(Dist(Power(Plus(c,Times(d,x)),m),u,x),Dist(Times(d,m),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,C1)),u),x),x))),And(FreeQ(List(a,b,c,d,n,p),x),IGtQ(m,C0),Or(IntegerQ(Times(C1D2,n)),IntegerQ(Times(C1D2,Subtract(p,C1))))))),
IIntegrate(4418,Integrate(Times(Power(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT),Power(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(u,IntHide(Times(Power(Csc(Plus(a,Times(b,x))),n),Power(Cot(Plus(a,Times(b,x))),p)),x))),Subtract(Dist(Power(Plus(c,Times(d,x)),m),u,x),Dist(Times(d,m),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,C1)),u),x),x))),And(FreeQ(List(a,b,c,d,n,p),x),IGtQ(m,C0),Or(IntegerQ(Times(C1D2,n)),IntegerQ(Times(C1D2,Subtract(p,C1))))))),
IIntegrate(4419,Integrate(Times(Power(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Dist(Power(C2,n),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Csc(Plus(Times(C2,a),Times(C2,b,x))),n)),x),x),And(FreeQ(List(a,b,c,d,m),x),IntegerQ(n),RationalQ(m)))),
IIntegrate(4420,Integrate(Times(Power(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(u,IntHide(Times(Power(Csc(Plus(a,Times(b,x))),n),Power(Sec(Plus(a,Times(b,x))),p)),x))),Subtract(Dist(Power(Plus(c,Times(d,x)),m),u,x),Dist(Times(d,m),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,C1)),u),x),x))),And(FreeQ(List(a,b,c,d),x),IntegersQ(n,p),GtQ(m,C0),NeQ(n,p))))
  );
}
