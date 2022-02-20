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
class IntRules116 { 
  public static IAST RULES = List( 
IIntegrate(2321,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(x,Power(Plus(d,Times(e,Sqr(x))),q),Plus(a,Times(b,Log(Times(c,Power(x,n))))),Power(Plus(Times(C2,q),C1),CN1)),x),Negate(Dist(Times(b,n,Power(Plus(Times(C2,q),C1),CN1)),Integrate(Power(Plus(d,Times(e,Sqr(x))),q),x),x)),Dist(Times(C2,d,q,Power(Plus(Times(C2,q),C1),CN1)),Integrate(Times(Power(Plus(d,Times(e,Sqr(x))),Subtract(q,C1)),Plus(a,Times(b,Log(Times(c,Power(x,n)))))),x),x)),And(FreeQ(List(a,b,c,d,e,n),x),GtQ(q,C0)))),
IIntegrate(2322,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),QQ(-3L,2L))),x_Symbol),
    Condition(Subtract(Simp(Times(x,Plus(a,Times(b,Log(Times(c,Power(x,n))))),Power(Times(d,Sqrt(Plus(d,Times(e,Sqr(x))))),CN1)),x),Dist(Times(b,n,Power(d,CN1)),Integrate(Power(Plus(d,Times(e,Sqr(x))),CN1D2),x),x)),FreeQ(List(a,b,c,d,e,n),x))),
IIntegrate(2323,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(x,Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),Plus(a,Times(b,Log(Times(c,Power(x,n))))),Power(Times(C2,d,Plus(q,C1)),CN1)),x)),Dist(Times(Plus(Times(C2,q),C3),Power(Times(C2,d,Plus(q,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),Plus(a,Times(b,Log(Times(c,Power(x,n)))))),x),x),Dist(Times(b,n,Power(Times(C2,d,Plus(q,C1)),CN1)),Integrate(Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),x),x)),And(FreeQ(List(a,b,c,d,e,n),x),LtQ(q,CN1)))),
IIntegrate(2324,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Power(Plus(d,Times(e,Sqr(x))),CN1),x))),Subtract(Simp(Times(u,Plus(a,Times(b,Log(Times(c,Power(x,n)))))),x),Dist(Times(b,n),Integrate(Times(u,Power(x,CN1)),x),x))),FreeQ(List(a,b,c,d,e,n),x))),
IIntegrate(2325,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Subtract(Simp(Times(ArcSinh(Times(Rt(e,C2),x,Power(d,CN1D2))),Plus(a,Times(b,Log(Times(c,Power(x,n))))),Power(Rt(e,C2),CN1)),x),Dist(Times(b,n,Power(Rt(e,C2),CN1)),Integrate(Times(ArcSinh(Times(Rt(e,C2),x,Power(d,CN1D2))),Power(x,CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,n),x),GtQ(d,C0),PosQ(e)))),
IIntegrate(2326,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Subtract(Simp(Times(ArcSin(Times(Rt(Negate(e),C2),x,Power(d,CN1D2))),Plus(a,Times(b,Log(Times(c,Power(x,n))))),Power(Rt(Negate(e),C2),CN1)),x),Dist(Times(b,n,Power(Rt(Negate(e),C2),CN1)),Integrate(Times(ArcSin(Times(Rt(Negate(e),C2),x,Power(d,CN1D2))),Power(x,CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,n),x),GtQ(d,C0),NegQ(e)))),
IIntegrate(2327,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Dist(Times(Sqrt(Plus(C1,Times(e,Sqr(x),Power(d,CN1)))),Power(Plus(d,Times(e,Sqr(x))),CN1D2)),Integrate(Times(Plus(a,Times(b,Log(Times(c,Power(x,n))))),Power(Plus(C1,Times(e,Sqr(x),Power(d,CN1))),CN1D2)),x),x),And(FreeQ(List(a,b,c,d,e,n),x),Not(GtQ(d,C0))))),
IIntegrate(2328,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),Power(Plus($p("d1"),Times($p("e1",true),x_)),CN1D2),Power(Plus($p("d2"),Times($p("e2",true),x_)),CN1D2)),x_Symbol),
    Condition(Dist(Times(Sqrt(Plus(C1,Times($s("e1"),$s("e2"),Sqr(x),Power(Times($s("d1"),$s("d2")),CN1)))),Power(Times(Sqrt(Plus($s("d1"),Times($s("e1"),x))),Sqrt(Plus($s("d2"),Times($s("e2"),x)))),CN1)),Integrate(Times(Plus(a,Times(b,Log(Times(c,Power(x,n))))),Power(Plus(C1,Times($s("e1"),$s("e2"),Sqr(x),Power(Times($s("d1"),$s("d2")),CN1))),CN1D2)),x),x),And(FreeQ(List(a,b,c,$s("d1"),$s("e1"),$s("d2"),$s("e2"),n),x),EqQ(Plus(Times($s("d2"),$s("e1")),Times($s("d1"),$s("e2"))),C0)))),
IIntegrate(2329,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,Power(x_,r_DEFAULT))),q_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Power(Plus(d,Times(e,Power(x,r))),q),x))),Condition(Subtract(Dist(Plus(a,Times(b,Log(Times(c,Power(x,n))))),u,x),Dist(Times(b,n),Integrate(SimplifyIntegrand(Times(u,Power(x,CN1)),x),x),x)),Or(And(EqQ(r,C1),IntegerQ(Subtract(q,C1D2))),And(EqQ(r,C2),EqQ(q,CN1)),InverseFunctionFreeQ(u,x)))),And(FreeQ(List(a,b,c,d,e,n,q,r),x),IntegerQ(Times(C2,q)),IntegerQ(r)))),
IIntegrate(2330,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Power(x_,r_DEFAULT))),q_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,ExpandIntegrand(Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p),Power(Plus(d,Times(e,Power(x,r))),q),x))),Condition(Integrate(u,x),SumQ(u))),And(FreeQ(List(a,b,c,d,e,n,p,q,r),x),IntegerQ(q),Or(GtQ(q,C0),And(IGtQ(p,C0),IntegerQ(r)))))),
IIntegrate(2331,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Power(x_,r_DEFAULT))),q_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(d,Times(e,Power(x,r))),q),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p)),x),FreeQ(List(a,b,c,d,e,n,p,q,r),x))),
IIntegrate(2332,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(u_,q_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),q),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p)),x),And(FreeQ(List(a,b,c,n,p,q),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x))))),
IIntegrate(2333,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Power(x_,CN1))),q_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Plus(e,Times(d,x)),q),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p)),x),And(FreeQ(List(a,b,c,d,e,m,n,p),x),EqQ(m,q),IntegerQ(q)))),
IIntegrate(2334,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),Power(x_,m_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Power(x_,r_DEFAULT))),q_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(x,m),Power(Plus(d,Times(e,Power(x,r))),q)),x))),Subtract(Simp(Times(u,Plus(a,Times(b,Log(Times(c,Power(x,n)))))),x),Dist(Times(b,n),Integrate(SimplifyIntegrand(Times(u,Power(x,CN1)),x),x),x))),And(FreeQ(List(a,b,c,d,e,n,r),x),IGtQ(q,C0),IntegerQ(m),Not(And(EqQ(q,C1),EqQ(m,CN1)))))),
IIntegrate(2335,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Power(x_,r_DEFAULT))),q_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(f,x),Plus(m,C1)),Power(Plus(d,Times(e,Power(x,r))),Plus(q,C1)),Plus(a,Times(b,Log(Times(c,Power(x,n))))),Power(Times(d,f,Plus(m,C1)),CN1)),x),Dist(Times(b,n,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(f,x),m),Power(Plus(d,Times(e,Power(x,r))),Plus(q,C1))),x),x)),And(FreeQ(List(a,b,c,d,e,f,m,n,q,r),x),EqQ(Plus(m,Times(r,Plus(q,C1)),C1),C0),NeQ(m,CN1)))),
IIntegrate(2336,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_DEFAULT),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Power(x_,r_))),q_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(f,m),Power(n,CN1)),Subst(Integrate(Times(Power(Plus(d,Times(e,x)),q),Power(Plus(a,Times(b,Log(Times(c,x)))),p)),x),x,Power(x,n)),x),And(FreeQ(List(a,b,c,d,e,f,m,n,q,r),x),EqQ(m,Subtract(r,C1)),IGtQ(p,C0),Or(IntegerQ(m),GtQ(f,C0)),EqQ(r,n)))),
IIntegrate(2337,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Power(x_,r_))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(f,m),Log(Plus(C1,Times(e,Power(x,r),Power(d,CN1)))),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p),Power(Times(e,r),CN1)),x),Dist(Times(b,Power(f,m),n,p,Power(Times(e,r),CN1)),Integrate(Times(Log(Plus(C1,Times(e,Power(x,r),Power(d,CN1)))),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),Subtract(p,C1)),Power(x,CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,f,m,n,r),x),EqQ(m,Subtract(r,C1)),IGtQ(p,C0),Or(IntegerQ(m),GtQ(f,C0)),NeQ(r,n)))),
IIntegrate(2338,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Power(x_,r_))),q_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(f,m),Power(Plus(d,Times(e,Power(x,r))),Plus(q,C1)),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p),Power(Times(e,r,Plus(q,C1)),CN1)),x),Dist(Times(b,Power(f,m),n,p,Power(Times(e,r,Plus(q,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,Power(x,r))),Plus(q,C1)),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),Subtract(p,C1)),Power(x,CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,f,m,n,q,r),x),EqQ(m,Subtract(r,C1)),IGtQ(p,C0),Or(IntegerQ(m),GtQ(f,C0)),NeQ(r,n),NeQ(q,CN1)))),
IIntegrate(2339,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(Times(f_,x_),m_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Power(x_,r_))),q_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(Times(f,x),m),Power(Power(x,m),CN1)),Integrate(Times(Power(x,m),Power(Plus(d,Times(e,Power(x,r))),q),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p)),x),x),And(FreeQ(List(a,b,c,d,e,f,m,n,q,r),x),EqQ(m,Subtract(r,C1)),IGtQ(p,C0),Not(Or(IntegerQ(m),GtQ(f,C0)))))),
IIntegrate(2340,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Power(Times(f,x),Plus(m,C1)),Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),Plus(a,Times(b,Log(Times(c,Power(x,n))))),Power(Times(C2,d,f,Plus(q,C1)),CN1)),x)),Dist(Power(Times(C2,d,Plus(q,C1)),CN1),Integrate(Times(Power(Times(f,x),m),Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),Plus(Times(a,Plus(m,Times(C2,q),C3)),Times(b,n),Times(b,Plus(m,Times(C2,q),C3),Log(Times(c,Power(x,n)))))),x),x)),And(FreeQ(List(a,b,c,d,e,f,m,n),x),ILtQ(q,CN1),ILtQ(m,C0))))
  );
}
