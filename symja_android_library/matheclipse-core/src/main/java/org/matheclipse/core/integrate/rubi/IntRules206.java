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
class IntRules206 { 
  public static IAST RULES = List( 
IIntegrate(4121,Integrate(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,Sqr($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))))),p_)),x_Symbol),
    Condition(Integrate(ActivateTrig(Times(u,Power(Times(b,Sqr($($s("§tan"),Plus(e,Times(f,x))))),p))),x),And(FreeQ(List(a,b,e,f,p),x),EqQ(Plus(a,b),C0)))),
IIntegrate(4122,Integrate(Power(Times(b_DEFAULT,Sqr($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),p_),x_Symbol),
    Condition(With(list(Set($s("ff"),FreeFactors(Tan(Plus(e,Times(f,x))),x))),Dist(Times(b,$s("ff"),Power(f,CN1)),Subst(Integrate(Power(Plus(b,Times(b,Sqr($s("ff")),Sqr(x))),Subtract(p,C1)),x),x,Times(Tan(Plus(e,Times(f,x))),Power($s("ff"),CN1))),x)),And(FreeQ(List(b,e,f,p),x),Not(IntegerQ(p))))),
IIntegrate(4123,Integrate(Power(Times(b_DEFAULT,Power(Times(c_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_)),p_),x_Symbol),
    Condition(Dist(Times(Power(b,IntPart(p)),Power(Times(b,Power(Times(c,Sec(Plus(e,Times(f,x)))),n)),FracPart(p)),Power(Power(Times(c,Sec(Plus(e,Times(f,x)))),Times(n,FracPart(p))),CN1)),Integrate(Power(Times(c,Sec(Plus(e,Times(f,x)))),Times(n,p)),x),x),And(FreeQ(List(b,c,e,f,n,p),x),Not(IntegerQ(p))))),
IIntegrate(4124,Integrate(Times(Power(Times(b_DEFAULT,Sqr($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),p_DEFAULT),Power($($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),m_DEFAULT)),x_Symbol),
    Condition(Dist(Times(b,Power(Times(C2,f),CN1)),Subst(Integrate(Times(Power(Plus(CN1,x),Times(C1D2,Subtract(m,C1))),Power(Times(b,x),Subtract(p,C1))),x),x,Sqr(Sec(Plus(e,Times(f,x))))),x),And(FreeQ(List(b,e,f,p),x),Not(IntegerQ(p)),IntegerQ(Times(C1D2,Subtract(m,C1)))))),
IIntegrate(4125,Integrate(Times(u_DEFAULT,Power(Times(b_DEFAULT,Power($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),n_)),p_)),x_Symbol),
    Condition(With(list(Set($s("ff"),FreeFactors(Sec(Plus(e,Times(f,x))),x))),Dist(Times(Power(Times(b,Power($s("ff"),n)),IntPart(p)),Power(Times(b,Power(Sec(Plus(e,Times(f,x))),n)),FracPart(p)),Power(Power(Times(Sec(Plus(e,Times(f,x))),Power($s("ff"),CN1)),Times(n,FracPart(p))),CN1)),Integrate(Times(ActivateTrig(u),Power(Times(Sec(Plus(e,Times(f,x))),Power($s("ff"),CN1)),Times(n,p))),x),x)),And(FreeQ(List(b,e,f,n,p),x),Not(IntegerQ(p)),IntegerQ(n),Or(EqQ(u,C1),MatchQ(u,Condition(Power(Times(d_DEFAULT,$($p("§trig"),Plus(e,Times(f,x)))),m_DEFAULT),And(FreeQ(list(d,m),x),MemberQ(List($s("§sin"),$s("§cos"),$s("§tan"),$s("§cot"),$s("§sec"),$s("§csc")),$s("§trig"))))))))),
IIntegrate(4126,Integrate(Times(u_DEFAULT,Power(Times(b_DEFAULT,Power(Times(c_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_)),p_)),x_Symbol),
    Condition(Dist(Times(Power(b,IntPart(p)),Power(Times(b,Power(Times(c,Sec(Plus(e,Times(f,x)))),n)),FracPart(p)),Power(Power(Times(c,Sec(Plus(e,Times(f,x)))),Times(n,FracPart(p))),CN1)),Integrate(Times(ActivateTrig(u),Power(Times(c,Sec(Plus(e,Times(f,x)))),Times(n,p))),x),x),And(FreeQ(List(b,c,e,f,n,p),x),Not(IntegerQ(p)),Not(IntegerQ(n)),Or(EqQ(u,C1),MatchQ(u,Condition(Power(Times(d_DEFAULT,$($p("§trig"),Plus(e,Times(f,x)))),m_DEFAULT),And(FreeQ(list(d,m),x),MemberQ(List($s("§sin"),$s("§cos"),$s("§tan"),$s("§cot"),$s("§sec"),$s("§csc")),$s("§trig"))))))))),
IIntegrate(4127,Integrate(Power(Plus(a_,Times(b_DEFAULT,Sqr($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))))),CN1),x_Symbol),
    Condition(Subtract(Simp(Times(x,Power(a,CN1)),x),Dist(Times(b,Power(a,CN1)),Integrate(Power(Plus(b,Times(a,Sqr(Cos(Plus(e,Times(f,x)))))),CN1),x),x)),And(FreeQ(List(a,b,e,f),x),NeQ(Plus(a,b),C0)))),
IIntegrate(4128,Integrate(Power(Plus(a_,Times(b_DEFAULT,Sqr($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))))),p_),x_Symbol),
    Condition(With(list(Set($s("ff"),FreeFactors(Tan(Plus(e,Times(f,x))),x))),Dist(Times($s("ff"),Power(f,CN1)),Subst(Integrate(Times(Power(Plus(a,b,Times(b,Sqr($s("ff")),Sqr(x))),p),Power(Plus(C1,Times(Sqr($s("ff")),Sqr(x))),CN1)),x),x,Times(Tan(Plus(e,Times(f,x))),Power($s("ff"),CN1))),x)),And(FreeQ(List(a,b,e,f,p),x),NeQ(Plus(a,b),C0),NeQ(p,CN1)))),
IIntegrate(4129,Integrate(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),C4))),p_),x_Symbol),
    Condition(With(list(Set($s("ff"),FreeFactors(Tan(Plus(e,Times(f,x))),x))),Dist(Times($s("ff"),Power(f,CN1)),Subst(Integrate(Times(Power(Plus(a,b,Times(C2,b,Sqr($s("ff")),Sqr(x)),Times(b,Power($s("ff"),C4),Power(x,C4))),p),Power(Plus(C1,Times(Sqr($s("ff")),Sqr(x))),CN1)),x),x,Times(Tan(Plus(e,Times(f,x))),Power($s("ff"),CN1))),x)),And(FreeQ(List(a,b,e,f,p),x),IntegerQ(Times(C2,p))))),
IIntegrate(4130,Integrate(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),n_))),p_),x_Symbol),
    Condition(With(list(Set($s("ff"),FreeFactors(Tan(Plus(e,Times(f,x))),x))),Dist(Times($s("ff"),Power(f,CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,Power(Plus(C1,Times(Sqr($s("ff")),Sqr(x))),Times(C1D2,n)))),p),Power(Plus(C1,Times(Sqr($s("ff")),Sqr(x))),CN1)),x),x,Times(Tan(Plus(e,Times(f,x))),Power($s("ff"),CN1))),x)),And(FreeQ(List(a,b,e,f,p),x),IntegerQ(Times(C1D2,n)),IGtQ(p,CN2)))),
IIntegrate(4131,Integrate(Power(Plus(a_,Times(b_DEFAULT,Power(Times(c_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_))),p_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,Power(Times(c,Sec(Plus(e,Times(f,x)))),n))),p),x),FreeQ(List(a,b,c,e,f,n,p),x))),
IIntegrate(4132,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),n_))),p_DEFAULT),Power($($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),m_)),x_Symbol),
    Condition(With(list(Set($s("ff"),FreeFactors(Tan(Plus(e,Times(f,x))),x))),Dist(Times(Power($s("ff"),Plus(m,C1)),Power(f,CN1)),Subst(Integrate(Times(Power(x,m),Power(ExpandToSum(Plus(a,Times(b,Power(Plus(C1,Times(Sqr($s("ff")),Sqr(x))),Times(C1D2,n)))),x),p),Power(Power(Plus(C1,Times(Sqr($s("ff")),Sqr(x))),Plus(Times(C1D2,m),C1)),CN1)),x),x,Times(Tan(Plus(e,Times(f,x))),Power($s("ff"),CN1))),x)),And(FreeQ(List(a,b,e,f,p),x),IntegerQ(Times(C1D2,m)),IntegerQ(Times(C1D2,n))))),
IIntegrate(4133,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),n_))),p_DEFAULT),Power($($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set($s("ff"),FreeFactors(Cos(Plus(e,Times(f,x))),x))),Negate(Dist(Times($s("ff"),Power(f,CN1)),Subst(Integrate(Times(Power(Subtract(C1,Times(Sqr($s("ff")),Sqr(x))),Times(C1D2,Subtract(m,C1))),Power(Plus(b,Times(a,Power(Times($s("ff"),x),n))),p),Power(Power(Times($s("ff"),x),Times(n,p)),CN1)),x),x,Times(Cos(Plus(e,Times(f,x))),Power($s("ff"),CN1))),x))),And(FreeQ(List(a,b,e,f),x),IntegerQ(Times(C1D2,Subtract(m,C1))),IntegerQ(n),IntegerQ(p)))),
IIntegrate(4134,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Power(Times(c_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_))),p_DEFAULT),Power($($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set($s("ff"),FreeFactors(Cos(Plus(e,Times(f,x))),x))),Dist(Power(Times(f,Power($s("ff"),m)),CN1),Subst(Integrate(Times(Power(Plus(CN1,Times(Sqr($s("ff")),Sqr(x))),Times(C1D2,Subtract(m,C1))),Power(Plus(a,Times(b,Power(Times(c,$s("ff"),x),n))),p),Power(Power(x,Plus(m,C1)),CN1)),x),x,Times(Sec(Plus(e,Times(f,x))),Power($s("ff"),CN1))),x)),And(FreeQ(List(a,b,c,e,f,n,p),x),IntegerQ(Times(C1D2,Subtract(m,C1))),Or(GtQ(m,C0),EqQ(n,C2),EqQ(n,C4))))),
IIntegrate(4135,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Power(Times(c_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_))),p_DEFAULT),Power(Times(d_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(a,Times(b,Power(Times(c,Sec(Plus(e,Times(f,x)))),n))),p),Power(Times(d,Sin(Plus(e,Times(f,x)))),m)),x),FreeQ(List(a,b,c,d,e,f,m,n,p),x))),
IIntegrate(4136,Integrate(Times(Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT),m_),Power(Plus(a_,Times(b_DEFAULT,Power($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),n_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(d,Times(n,p)),Integrate(Times(Power(Times(d,Cos(Plus(e,Times(f,x)))),Subtract(m,Times(n,p))),Power(Plus(b,Times(a,Power(Cos(Plus(e,Times(f,x))),n))),p)),x),x),And(FreeQ(List(a,b,d,e,f,m,n,p),x),Not(IntegerQ(m)),IntegersQ(n,p)))),
IIntegrate(4137,Integrate(Times(Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT),m_),Power(Plus(a_,Times(b_DEFAULT,Power(Times(c_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_))),p_)),x_Symbol),
    Condition(Dist(Times(Power(Times(d,Cos(Plus(e,Times(f,x)))),FracPart(m)),Power(Times(Sec(Plus(e,Times(f,x))),Power(d,CN1)),FracPart(m))),Integrate(Times(Power(Plus(a,Times(b,Power(Times(c,Sec(Plus(e,Times(f,x)))),n))),p),Power(Power(Times(Sec(Plus(e,Times(f,x))),Power(d,CN1)),m),CN1)),x),x),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),Not(IntegerQ(m))))),
IIntegrate(4138,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),n_))),p_DEFAULT),Power($($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),m_DEFAULT)),x_Symbol),
    Condition(Module(list(Set($s("ff"),FreeFactors(Cos(Plus(e,Times(f,x))),x))),Negate(Dist(Power(Times(f,Power($s("ff"),Subtract(Plus(m,Times(n,p)),C1))),CN1),Subst(Integrate(Times(Power(Subtract(C1,Times(Sqr($s("ff")),Sqr(x))),Times(C1D2,Subtract(m,C1))),Power(Plus(b,Times(a,Power(Times($s("ff"),x),n))),p),Power(Power(x,Plus(m,Times(n,p))),CN1)),x),x,Times(Cos(Plus(e,Times(f,x))),Power($s("ff"),CN1))),x))),And(FreeQ(List(a,b,e,f,n),x),IntegerQ(Times(C1D2,Subtract(m,C1))),IntegerQ(n),IntegerQ(p)))),
IIntegrate(4139,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Power(Times(c_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_))),p_DEFAULT),Power($($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set($s("ff"),FreeFactors(Sec(Plus(e,Times(f,x))),x))),Dist(Power(f,CN1),Subst(Integrate(Times(Power(Plus(CN1,Times(Sqr($s("ff")),Sqr(x))),Times(C1D2,Subtract(m,C1))),Power(Plus(a,Times(b,Power(Times(c,$s("ff"),x),n))),p),Power(x,CN1)),x),x,Times(Sec(Plus(e,Times(f,x))),Power($s("ff"),CN1))),x)),And(FreeQ(List(a,b,c,e,f,n,p),x),IntegerQ(Times(C1D2,Subtract(m,C1))),Or(GtQ(m,C0),EqQ(n,C2),EqQ(n,C4),IGtQ(p,C0),IntegersQ(Times(C2,n),p))))),
IIntegrate(4140,Integrate(Times(Power(Times(b_DEFAULT,Sqr($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),p_DEFAULT),Power(Times(d_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_)),x_Symbol),
    Condition(With(list(Set($s("ff"),FreeFactors(Tan(Plus(e,Times(f,x))),x))),Dist(Times(b,$s("ff"),Power(f,CN1)),Subst(Integrate(Times(Power(Times(d,$s("ff"),x),m),Power(Plus(b,Times(b,Sqr($s("ff")),Sqr(x))),Subtract(p,C1))),x),x,Times(Tan(Plus(e,Times(f,x))),Power($s("ff"),CN1))),x)),FreeQ(List(b,d,e,f,m,p),x)))
  );
}
