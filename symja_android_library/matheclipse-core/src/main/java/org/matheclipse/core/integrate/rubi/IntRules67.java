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
class IntRules67 { 
  public static IAST RULES = List( 
IIntegrate(1341,Integrate(Power(Plus(a_,Times(c_DEFAULT,Power(x_,$p("n2",true))),Times(b_DEFAULT,Power(x_,n_))),p_),x_Symbol),
    Condition(With(list(Set(k,Denominator(n))),Dist(k,Subst(Integrate(Times(Power(x,Subtract(k,C1)),Power(Plus(a,Times(b,Power(x,Times(k,n))),Times(c,Power(x,Times(C2,k,n)))),p)),x),x,Power(x,Power(k,CN1))),x)),And(FreeQ(List(a,b,c,p),x),EqQ($s("n2"),Times(C2,n)),FractionQ(n)))),
IIntegrate(1342,Integrate(Power(Plus(a_,Times(c_DEFAULT,Power(x_,$p("n2",true))),Times(b_DEFAULT,Power(x_,n_))),p_),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Plus(a,Times(b,Power(Power(x,n),CN1)),Times(c,Power(Power(x,Times(C2,n)),CN1))),p),Power(x,CN2)),x),x,Power(x,CN1))),And(FreeQ(List(a,b,c,p),x),EqQ($s("n2"),Times(C2,n)),ILtQ(n,C0)))),
IIntegrate(1343,Integrate(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(c_DEFAULT,Power(x_,$p("n2",true)))),p_),x_Symbol),
    Condition(Dist(Times(Power(Plus(a,Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n)))),p),Power(Power(Plus(b,Times(C2,c,Power(x,n))),Times(C2,p)),CN1)),Integrate(Power(Plus(b,Times(C2,c,Power(x,n))),Times(C2,p)),x),x),And(FreeQ(List(a,b,c,n,p),x),EqQ($s("n2"),Times(C2,n)),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0)))),
IIntegrate(1344,Integrate(Power(Plus(a_,Times(c_DEFAULT,Power(x_,$p("n2",true))),Times(b_DEFAULT,Power(x_,n_))),p_),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(a,Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n)))),p),x),x),And(FreeQ(List(a,b,c,n),x),EqQ($s("n2"),Times(C2,n)),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),IGtQ(p,C0)))),
IIntegrate(1345,Integrate(Power(Plus(a_,Times(c_DEFAULT,Power(x_,$p("n2",true))),Times(b_DEFAULT,Power(x_,n_))),p_),x_Symbol),
    Condition(Plus(Negate(Simp(Times(x,Plus(Sqr(b),Times(CN1,C2,a,c),Times(b,c,Power(x,n))),Power(Plus(a,Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n)))),Plus(p,C1)),Power(Times(a,n,Plus(p,C1),Subtract(Sqr(b),Times(C4,a,c))),CN1)),x)),Dist(Power(Times(a,n,Plus(p,C1),Subtract(Sqr(b),Times(C4,a,c))),CN1),Integrate(Times(Plus(Sqr(b),Times(CN1,C2,a,c),Times(n,Plus(p,C1),Subtract(Sqr(b),Times(C4,a,c))),Times(b,c,Plus(Times(n,Plus(Times(C2,p),C3)),C1),Power(x,n))),Power(Plus(a,Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n)))),Plus(p,C1))),x),x)),And(FreeQ(List(a,b,c,n),x),EqQ($s("n2"),Times(C2,n)),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),ILtQ(p,CN1)))),
IIntegrate(1346,Integrate(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_)),Times(c_DEFAULT,Power(x_,$p("n2")))),CN1),x_Symbol),
    Condition(With(list(Set(q,Rt(Times(a,Power(c,CN1)),C2))),With(list(Set(r,Rt(Subtract(Times(C2,q),Times(b,Power(c,CN1))),C2))),Plus(Dist(Power(Times(C2,c,q,r),CN1),Integrate(Times(Subtract(r,Power(x,Times(C1D2,n))),Power(Plus(q,Times(CN1,r,Power(x,Times(C1D2,n))),Power(x,n)),CN1)),x),x),Dist(Power(Times(C2,c,q,r),CN1),Integrate(Times(Plus(r,Power(x,Times(C1D2,n))),Power(Plus(q,Times(r,Power(x,Times(C1D2,n))),Power(x,n)),CN1)),x),x)))),And(FreeQ(list(a,b,c),x),EqQ($s("n2"),Times(C2,n)),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),IGtQ(Times(C1D2,n),C0),NegQ(Subtract(Sqr(b),Times(C4,a,c)))))),
IIntegrate(1347,Integrate(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_)),Times(c_DEFAULT,Power(x_,$p("n2")))),CN1),x_Symbol),
    Condition(With(list(Set(q,Rt(Subtract(Sqr(b),Times(C4,a,c)),C2))),Subtract(Dist(Times(c,Power(q,CN1)),Integrate(Power(Plus(Times(C1D2,b),Times(CN1,C1D2,q),Times(c,Power(x,n))),CN1),x),x),Dist(Times(c,Power(q,CN1)),Integrate(Power(Plus(Times(C1D2,b),Times(C1D2,q),Times(c,Power(x,n))),CN1),x),x))),And(FreeQ(list(a,b,c),x),EqQ($s("n2"),Times(C2,n)),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0)))),
IIntegrate(1348,Integrate(Power(Plus(a_,Times(c_DEFAULT,Power(x_,$p("n2",true))),Times(b_DEFAULT,Power(x_,n_))),p_),x_Symbol),
    Condition(Dist(Times(Power(a,IntPart(p)),Power(Plus(a,Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n)))),FracPart(p)),Power(Times(Power(Plus(C1,Times(C2,c,Power(x,n),Power(Plus(b,Rt(Subtract(Sqr(b),Times(C4,a,c)),C2)),CN1))),FracPart(p)),Power(Plus(C1,Times(C2,c,Power(x,n),Power(Subtract(b,Rt(Subtract(Sqr(b),Times(C4,a,c)),C2)),CN1))),FracPart(p))),CN1)),Integrate(Times(Power(Plus(C1,Times(C2,c,Power(x,n),Power(Plus(b,Sqrt(Subtract(Sqr(b),Times(C4,a,c)))),CN1))),p),Power(Plus(C1,Times(C2,c,Power(x,n),Power(Subtract(b,Sqrt(Subtract(Sqr(b),Times(C4,a,c)))),CN1))),p)),x),x),And(FreeQ(List(a,b,c,n,p),x),EqQ($s("n2"),Times(C2,n)),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),Not(IntegerQ(p))))),
IIntegrate(1349,Integrate(Power(Plus(a_,Times(c_DEFAULT,Power(u_,$p("n2",true))),Times(b_DEFAULT,Power(u_,n_))),p_),x_Symbol),
    Condition(Dist(Power(Coefficient(u,x,C1),CN1),Subst(Integrate(Power(Plus(a,Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n)))),p),x),x,u),x),And(FreeQ(List(a,b,c,n,p),x),EqQ($s("n2"),Times(C2,n)),LinearQ(u,x),NeQ(u,x)))),
IIntegrate(1350,Integrate(Power(Plus(a_,Times(c_DEFAULT,Power(x_,n_DEFAULT)),Times(b_DEFAULT,Power(x_,$p("mn")))),p_DEFAULT),x_Symbol),
    Condition(Integrate(Times(Power(Plus(b,Times(a,Power(x,n)),Times(c,Power(x,Times(C2,n)))),p),Power(Power(x,Times(n,p)),CN1)),x),And(FreeQ(List(a,b,c,n),x),EqQ($s("mn"),Negate(n)),IntegerQ(p),PosQ(n)))),
IIntegrate(1351,Integrate(Power(Plus(a_,Times(c_DEFAULT,Power(x_,n_DEFAULT)),Times(b_DEFAULT,Power(x_,$p("mn")))),p_),x_Symbol),
    Condition(Dist(Times(Power(x,Times(n,FracPart(p))),Power(Plus(a,Times(b,Power(Power(x,n),CN1)),Times(c,Power(x,n))),FracPart(p)),Power(Power(Plus(b,Times(a,Power(x,n)),Times(c,Power(x,Times(C2,n)))),FracPart(p)),CN1)),Integrate(Times(Power(Plus(b,Times(a,Power(x,n)),Times(c,Power(x,Times(C2,n)))),p),Power(Power(x,Times(n,p)),CN1)),x),x),And(FreeQ(List(a,b,c,n,p),x),EqQ($s("mn"),Negate(n)),Not(IntegerQ(p)),PosQ(n)))),
IIntegrate(1352,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(c_DEFAULT,Power(x_,$p("n2",true))),Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(n,CN1),Subst(Integrate(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),p),x),x,Power(x,n)),x),And(FreeQ(List(a,b,c,m,n,p),x),EqQ($s("n2"),Times(C2,n)),EqQ(Simplify(Plus(m,Negate(n),C1)),C0)))),
IIntegrate(1353,Integrate(Times(Power(Times(d_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(c_DEFAULT,Power(x_,$p("n2",true))),Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times(Power(Times(d,x),m),Power(Plus(a,Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n)))),p)),x),x),And(FreeQ(List(a,b,c,d,m,n),x),EqQ($s("n2"),Times(C2,n)),IGtQ(p,C0),Not(IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1)))))))),
IIntegrate(1354,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(c_DEFAULT,Power(x_,$p("n2",true))),Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Integrate(Times(Power(x,Plus(m,Times(C2,n,p))),Power(Plus(c,Times(b,Power(Power(x,n),CN1)),Times(a,Power(Power(x,Times(C2,n)),CN1))),p)),x),And(FreeQ(List(a,b,c,m,n),x),EqQ($s("n2"),Times(C2,n)),ILtQ(p,C0),NegQ(n)))),
IIntegrate(1355,Integrate(Times(Power(Times(d_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(c_DEFAULT,Power(x_,$p("n2",true)))),p_)),x_Symbol),
    Condition(Dist(Times(Power(Plus(a,Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n)))),FracPart(p)),Power(Times(Power(c,IntPart(p)),Power(Plus(Times(C1D2,b),Times(c,Power(x,n))),Times(C2,FracPart(p)))),CN1)),Integrate(Times(Power(Times(d,x),m),Power(Plus(Times(C1D2,b),Times(c,Power(x,n))),Times(C2,p))),x),x),And(FreeQ(List(a,b,c,d,m,n,p),x),EqQ($s("n2"),Times(C2,n)),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0),IntegerQ(Subtract(p,C1D2))))),
IIntegrate(1356,Integrate(Times(Power(Times(d_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(c_DEFAULT,Power(x_,$p("n2",true)))),p_)),x_Symbol),
    Condition(Dist(Times(Power(a,IntPart(p)),Power(Plus(a,Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n)))),FracPart(p)),Power(Power(Plus(C1,Times(C2,c,Power(x,n),Power(b,CN1))),Times(C2,FracPart(p))),CN1)),Integrate(Times(Power(Times(d,x),m),Power(Plus(C1,Times(C2,c,Power(x,n),Power(b,CN1))),Times(C2,p))),x),x),And(FreeQ(List(a,b,c,d,m,n,p),x),EqQ($s("n2"),Times(C2,n)),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0),Not(IntegerQ(Times(C2,p)))))),
IIntegrate(1357,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(c_DEFAULT,Power(x_,$p("n2",true))),Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(n,CN1),Subst(Integrate(Times(Power(x,Subtract(Simplify(Times(Plus(m,C1),Power(n,CN1))),C1)),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),p)),x),x,Power(x,n)),x),And(FreeQ(List(a,b,c,m,n,p),x),EqQ($s("n2"),Times(C2,n)),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1))))))),
IIntegrate(1358,Integrate(Times(Power(Times(d_,x_),m_DEFAULT),Power(Plus(a_,Times(c_DEFAULT,Power(x_,$p("n2",true))),Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(d,IntPart(m)),Power(Times(d,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n)))),p)),x),x),And(FreeQ(List(a,b,c,d,m,n,p),x),EqQ($s("n2"),Times(C2,n)),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1))))))),
IIntegrate(1359,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(c_DEFAULT,Power(x_,$p("n2",true))),Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(With(list(Set(k,GCD(Plus(m,C1),n))),Condition(Dist(Power(k,CN1),Subst(Integrate(Times(Power(x,Subtract(Times(Plus(m,C1),Power(k,CN1)),C1)),Power(Plus(a,Times(b,Power(x,Times(n,Power(k,CN1)))),Times(c,Power(x,Times(C2,n,Power(k,CN1))))),p)),x),x,Power(x,k)),x),Unequal(k,C1))),And(FreeQ(List(a,b,c,p),x),EqQ($s("n2"),Times(C2,n)),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),IGtQ(n,C0),IntegerQ(m)))),
IIntegrate(1360,Integrate(Times(Power(Times(d_DEFAULT,x_),m_),Power(Plus(a_,Times(c_DEFAULT,Power(x_,$p("n2",true))),Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(With(list(Set(k,Denominator(m))),Dist(Times(k,Power(d,CN1)),Subst(Integrate(Times(Power(x,Subtract(Times(k,Plus(m,C1)),C1)),Power(Plus(a,Times(b,Power(x,Times(k,n)),Power(Power(d,n),CN1)),Times(c,Power(x,Times(C2,k,n)),Power(Power(d,Times(C2,n)),CN1))),p)),x),x,Power(Times(d,x),Power(k,CN1))),x)),And(FreeQ(List(a,b,c,d,p),x),EqQ($s("n2"),Times(C2,n)),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),IGtQ(n,C0),FractionQ(m),IntegerQ(p))))
  );
}
