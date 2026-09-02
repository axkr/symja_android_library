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
class IntRules315 { 
  public static IAST RULES = List( 
IIntegrate(6301,Integrate(ArcSinh(u_),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcSinh(u)),x),Integrate(SimplifyIntegrand(Times(x,D(u,x),Power(Plus(C1,Sqr(u)),CN1D2)),x),x)),And(InverseFunctionFreeQ(u,x),Not(FunctionOfExponentialQ(u,x))))),
IIntegrate(6302,Integrate(Times(Plus(a_DEFAULT,Times(ArcSinh(u_),b_DEFAULT)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Plus(a,Times(b,ArcSinh(u))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Integrate(SimplifyIntegrand(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),D(u,x),Power(Plus(C1,Sqr(u)),CN1D2)),x),x),x),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1),InverseFunctionFreeQ(u,x),Not(FunctionOfQ(Power(Plus(c,Times(d,x)),Plus(m,C1)),u,x)),Not(FunctionOfExponentialQ(u,x))))),
IIntegrate(6303,Integrate(Times(Plus(a_DEFAULT,Times(ArcSinh(u_),b_DEFAULT)),v_),x_Symbol),
    Condition(With(list(Set(w,IntHide(v,x))),Condition(Subtract(Simp(Dist(Plus(a,Times(b,ArcSinh(u))),w,x),x),Simp(Dist(b,Integrate(SimplifyIntegrand(Times(w,D(u,x),Power(Plus(C1,Sqr(u)),CN1D2)),x),x),x),x)),InverseFunctionFreeQ(w,x))),And(FreeQ(list(a,b),x),InverseFunctionFreeQ(u,x),Not(MatchQ(v,Condition(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x)),m_DEFAULT),FreeQ(list(c,d,m),x))))))),
IIntegrate(6304,Integrate(Exp(Times(ArcSinh(u_),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Power(Plus(u,Sqrt(Plus(C1,Sqr(u)))),n),x),And(IntegerQ(n),PolyQ(u,x)))),
IIntegrate(6305,Integrate(Times(Exp(Times(ArcSinh(u_),n_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(x,m),Power(Plus(u,Sqrt(Plus(C1,Sqr(u)))),n)),x),And(RationalQ(m),IntegerQ(n),PolyQ(u,x)))),
IIntegrate(6306,Integrate(Power(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),x_Symbol),
    Condition(Subtract(Simp(Times(x,Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),n)),x),Simp(Dist(Times(b,c,n),Integrate(Times(x,Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),Plus(n,CN1)),Power(Times(Sqrt(Plus(C1,Times(c,x))),Sqrt(Plus(CN1,Times(c,x)))),CN1)),x),x),x)),And(FreeQ(list(a,b,c),x),GtQ(n,C0)))),
IIntegrate(6307,Integrate(Power(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),x_Symbol),
    Condition(Subtract(Simp(Times(Sqrt(Plus(C1,Times(c,x))),Sqrt(Plus(CN1,Times(c,x))),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),Plus(n,C1)),Power(Times(b,c,Plus(n,C1)),CN1)),x),Simp(Dist(Times(c,Power(Times(b,Plus(n,C1)),CN1)),Integrate(Times(x,Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),Plus(n,C1)),Power(Times(Sqrt(Plus(C1,Times(c,x))),Sqrt(Plus(CN1,Times(c,x)))),CN1)),x),x),x)),And(FreeQ(list(a,b,c),x),LtQ(n,CN1)))),
IIntegrate(6308,Integrate(Power(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),x_Symbol),
    Condition(Simp(Dist(Power(Times(b,c),CN1),Subst(Integrate(Times(Power(x,n),Sinh(Plus(Times(CN1,a,Power(b,CN1)),Times(x,Power(b,CN1))))),x),x,Plus(a,Times(b,ArcCosh(Times(c,x))))),x),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(6309,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(x_,CN1)),x_Symbol),
    Condition(Simp(Dist(Power(b,CN1),Subst(Integrate(Times(Power(x,n),Tanh(Plus(Times(CN1,a,Power(b,CN1)),Times(x,Power(b,CN1))))),x),x,Plus(a,Times(b,ArcCosh(Times(c,x))))),x),x),And(FreeQ(list(a,b,c),x),IGtQ(n,C0)))),
IIntegrate(6310,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),Plus(m,C1)),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),n),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,c,n,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(d,x),Plus(m,C1)),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),Plus(n,CN1)),Power(Times(Sqrt(Plus(C1,Times(c,x))),Sqrt(Plus(CN1,Times(c,x)))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,m),x),IGtQ(n,C0),NeQ(m,CN1)))),
IIntegrate(6311,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),n),Power(Plus(m,C1),CN1)),x),Simp(Dist(Times(b,c,n,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),Plus(n,CN1)),Power(Times(Sqrt(Plus(C1,Times(c,x))),Sqrt(Plus(CN1,Times(c,x)))),CN1)),x),x),x)),And(FreeQ(list(a,b,c),x),IGtQ(m,C0),GtQ(n,C0)))),
IIntegrate(6312,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,m),Sqrt(Plus(C1,Times(c,x))),Sqrt(Plus(CN1,Times(c,x))),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),Plus(n,C1)),Power(Times(b,c,Plus(n,C1)),CN1)),x),Simp(Dist(Power(Times(Sqr(b),Power(c,Plus(m,C1)),Plus(n,C1)),CN1),Subst(Integrate(ExpandTrigReduce(Power(x,Plus(n,C1)),Times(Power(Cosh(Plus(Times(CN1,a,Power(b,CN1)),Times(x,Power(b,CN1)))),Plus(m,CN1)),Subtract(m,Times(Plus(m,C1),Sqr(Cosh(Plus(Times(CN1,a,Power(b,CN1)),Times(x,Power(b,CN1)))))))),x),x),x,Plus(a,Times(b,ArcCosh(Times(c,x))))),x),x)),And(FreeQ(list(a,b,c),x),IGtQ(m,C0),GeQ(n,CN2),LtQ(n,CN1)))),
IIntegrate(6313,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,m),Sqrt(Plus(C1,Times(c,x))),Sqrt(Plus(CN1,Times(c,x))),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),Plus(n,C1)),Power(Times(b,c,Plus(n,C1)),CN1)),x),Negate(Simp(Dist(Times(c,Plus(m,C1),Power(Times(b,Plus(n,C1)),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),Plus(n,C1)),Power(Times(Sqrt(Plus(C1,Times(c,x))),Sqrt(Plus(CN1,Times(c,x)))),CN1)),x),x),x)),Simp(Dist(Times(m,Power(Times(b,c,Plus(n,C1)),CN1)),Integrate(Times(Power(x,Plus(m,CN1)),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),Plus(n,C1)),Power(Times(Sqrt(Plus(C1,Times(c,x))),Sqrt(Plus(CN1,Times(c,x)))),CN1)),x),x),x)),And(FreeQ(list(a,b,c),x),IGtQ(m,C0),LtQ(n,CN2)))),
IIntegrate(6314,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Times(b,Power(c,Plus(m,C1))),CN1),Subst(Integrate(Times(Power(x,n),Power(Cosh(Plus(Times(CN1,a,Power(b,CN1)),Times(x,Power(b,CN1)))),m),Sinh(Plus(Times(CN1,a,Power(b,CN1)),Times(x,Power(b,CN1))))),x),x,Plus(a,Times(b,ArcCosh(Times(c,x))))),x),x),And(FreeQ(List(a,b,c,n),x),IGtQ(m,C0)))),
IIntegrate(6315,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(d,x),m),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),n)),x),FreeQ(List(a,b,c,d,m,n),x))),
IIntegrate(6316,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus($p("d1"),Times($p("e1",true),x_)),p_DEFAULT),Power(Plus($p("d2"),Times($p("e2",true),x_)),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Plus(Times($s("d1"),$s("d2")),Times($s("e1"),$s("e2"),Sqr(x))),p),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),n)),x),And(FreeQ(List(a,b,c,$s("d1"),$s("e1"),$s("d2"),$s("e2"),n),x),EqQ(Plus(Times($s("d2"),$s("e1")),Times($s("d1"),$s("e2"))),C0),IntegerQ(p)))),
IIntegrate(6317,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),CN1),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Times(Power(Times(b,c),CN1),Simp(Times(Sqrt(Plus(C1,Times(c,x))),Sqrt(Plus(CN1,Times(c,x))),Power(Plus(d,Times(e,Sqr(x))),CN1D2)),x),Log(Plus(a,Times(b,ArcCosh(Times(c,x)))))),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0)))),
IIntegrate(6318,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),CN1),Power(Plus($p("d1"),Times($p("e1",true),x_)),CN1D2),Power(Plus($p("d2"),Times($p("e2",true),x_)),CN1D2)),x_Symbol),
    Condition(Simp(Times(Power(Times(b,c),CN1),Simp(Times(Sqrt(Plus(C1,Times(c,x))),Power(Plus($s("d1"),Times($s("e1"),x)),CN1D2)),x),Simp(Times(Sqrt(Plus(CN1,Times(c,x))),Power(Plus($s("d2"),Times($s("e2"),x)),CN1D2)),x),Log(Plus(a,Times(b,ArcCosh(Times(c,x)))))),x),And(FreeQ(List(a,b,c,$s("d1"),$s("e1"),$s("d2"),$s("e2")),x),EqQ($s("e1"),Times(c,$s("d1"))),EqQ($s("e2"),Times(CN1,c,$s("d2")))))),
IIntegrate(6319,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Times(Power(Times(b,c,Plus(n,C1)),CN1),Simp(Times(Sqrt(Plus(C1,Times(c,x))),Sqrt(Plus(CN1,Times(c,x))),Power(Plus(d,Times(e,Sqr(x))),CN1D2)),x),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),Plus(n,C1))),x),And(FreeQ(List(a,b,c,d,e,n),x),EqQ(Plus(Times(Sqr(c),d),e),C0),NeQ(n,CN1)))),
IIntegrate(6320,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus($p("d1"),Times($p("e1",true),x_)),CN1D2),Power(Plus($p("d2"),Times($p("e2",true),x_)),CN1D2)),x_Symbol),
    Condition(Simp(Times(Power(Times(b,c,Plus(n,C1)),CN1),Simp(Times(Sqrt(Plus(C1,Times(c,x))),Power(Plus($s("d1"),Times($s("e1"),x)),CN1D2)),x),Simp(Times(Sqrt(Plus(CN1,Times(c,x))),Power(Plus($s("d2"),Times($s("e2"),x)),CN1D2)),x),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),Plus(n,C1))),x),And(FreeQ(List(a,b,c,$s("d1"),$s("e1"),$s("d2"),$s("e2"),n),x),EqQ($s("e1"),Times(c,$s("d1"))),EqQ($s("e2"),Times(CN1,c,$s("d2"))),NeQ(n,CN1))))
  );
}
