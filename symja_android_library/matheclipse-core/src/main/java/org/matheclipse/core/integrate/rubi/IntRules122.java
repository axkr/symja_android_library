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
class IntRules122 { 
  public static IAST RULES = List( 
IIntegrate(2441,Integrate(Times($p("§pq"),Power(x_,m_DEFAULT),Power(Plus(Times(a_DEFAULT,Power(x_,j_DEFAULT)),Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Simp(Star(Power(n,CN1),Subst(Integrate(Times(Power(x,Subtract(Simplify(Times(Plus(m,C1),Power(n,CN1))),C1)),SubstFor(Power(x,n),$s("§pq"),x),Power(Plus(Times(a,Power(x,Simplify(Times(j,Power(n,CN1))))),Times(b,x)),p)),x),x,Power(x,n))),x),And(FreeQ(List(a,b,j,m,n,p),x),PolyQ($s("§pq"),Power(x,n)),Not(IntegerQ(p)),NeQ(n,j),IntegerQ(Simplify(Times(j,Power(n,CN1)))),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1))))))),
IIntegrate(2442,Integrate(Times($p("§pq"),Power(Times(c_,x_),m_DEFAULT),Power(Plus(Times(a_DEFAULT,Power(x_,j_DEFAULT)),Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(c,Times(Sign(m),Quotient(m,Sign(m)))),Power(Times(c,x),Mod(m,Sign(m))),Power(Power(x,Mod(m,Sign(m))),CN1)),Integrate(Times(Power(x,m),$s("§pq"),Power(Plus(Times(a,Power(x,j)),Times(b,Power(x,n))),p)),x)),x),And(FreeQ(List(a,b,c,j,n,p),x),PolyQ($s("§pq"),Power(x,n)),Not(IntegerQ(p)),NeQ(n,j),IntegerQ(Simplify(Times(j,Power(n,CN1)))),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1)))),RationalQ(m),GtQ(Sqr(m),C1)))),
IIntegrate(2443,Integrate(Times($p("§pq"),Power(Times(c_,x_),m_DEFAULT),Power(Plus(Times(a_DEFAULT,Power(x_,j_DEFAULT)),Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(c,x),m),Power(Power(x,m),CN1)),Integrate(Times(Power(x,m),$s("§pq"),Power(Plus(Times(a,Power(x,j)),Times(b,Power(x,n))),p)),x)),x),And(FreeQ(List(a,b,c,j,m,n,p),x),PolyQ($s("§pq"),Power(x,n)),Not(IntegerQ(p)),NeQ(n,j),IntegerQ(Simplify(Times(j,Power(n,CN1)))),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1))))))),
IIntegrate(2444,Integrate(Times($p("§pq"),Power(x_,m_DEFAULT),Power(Plus(Times(a_DEFAULT,Power(x_,j_DEFAULT)),Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(With(list(Set(g,GCD(Plus(m,C1),n))),Condition(Simp(Star(Power(g,CN1),Subst(Integrate(Times(Power(x,Subtract(Times(Plus(m,C1),Power(g,CN1)),C1)),ReplaceAll($s("§pq"),Rule(x,Power(x,Power(g,CN1)))),Power(Plus(Times(a,Power(x,Times(j,Power(g,CN1)))),Times(b,Power(x,Times(n,Power(g,CN1))))),p)),x),x,Power(x,g))),x),NeQ(g,C1))),And(FreeQ(list(a,b,p),x),PolyQ($s("§pq"),Power(x,n)),Not(IntegerQ(p)),IGtQ(j,C0),IGtQ(n,C0),IGtQ(Times(j,Power(n,CN1)),C0),IntegerQ(m)))),
IIntegrate(2445,Integrate(Times($p("§pq"),Power(Times(c_DEFAULT,x_),m_DEFAULT),Power(Plus(Times(a_DEFAULT,Power(x_,j_DEFAULT)),Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(With(list(Set(q,Expon($s("§pq"),x))),Condition(With(list(Set($s("§pqq"),Coeff($s("§pq"),x,q))),Plus(Integrate(Times(Power(Times(c,x),m),ExpandToSum(Subtract(Subtract($s("§pq"),Times($s("§pqq"),Power(x,q))),Times(a,$s("§pqq"),Plus(m,q,Negate(n),C1),Power(x,Subtract(q,n)),Power(Times(b,Plus(m,q,Times(n,p),C1)),CN1))),x),Power(Plus(Times(a,Power(x,j)),Times(b,Power(x,n))),p)),x),Simp(Times($s("§pqq"),Power(Times(c,x),Plus(m,q,Negate(n),C1)),Power(Plus(Times(a,Power(x,j)),Times(b,Power(x,n))),Plus(p,C1)),Power(Times(b,Power(c,Plus(q,Negate(n),C1)),Plus(m,q,Times(n,p),C1)),CN1)),x))),And(GtQ(q,Subtract(n,C1)),NeQ(Plus(m,q,Times(n,p),C1),C0),Or(IntegerQ(Times(C2,p)),IntegerQ(Plus(p,Times(Plus(q,C1),Power(Times(C2,n),CN1)))))))),And(FreeQ(List(a,b,c,m,p),x),PolyQ($s("§pq"),x),Not(IntegerQ(p)),IGtQ(j,C0),IGtQ(n,C0),LtQ(j,n)))),
IIntegrate(2446,Integrate(Times($p("§pq"),Power(x_,m_DEFAULT),Power(Plus(Times(a_DEFAULT,Power(x_,j_DEFAULT)),Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Simp(Star(Power(Plus(m,C1),CN1),Subst(Integrate(Times(ReplaceAll(SubstFor(Power(x,n),$s("§pq"),x),Rule(x,Power(x,Simplify(Times(n,Power(Plus(m,C1),CN1)))))),Power(Plus(Times(a,Power(x,Simplify(Times(j,Power(Plus(m,C1),CN1))))),Times(b,Power(x,Simplify(Times(n,Power(Plus(m,C1),CN1)))))),p)),x),x,Power(x,Plus(m,C1)))),x),And(FreeQ(List(a,b,j,m,n,p),x),PolyQ($s("§pq"),Power(x,n)),Not(IntegerQ(p)),NeQ(n,j),IntegerQ(Simplify(Times(j,Power(n,CN1)))),IntegerQ(Simplify(Times(n,Power(Plus(m,C1),CN1)))),Not(IntegerQ(n))))),
IIntegrate(2447,Integrate(Times($p("§pq"),Power(Times(c_,x_),m_),Power(Plus(Times(a_DEFAULT,Power(x_,j_DEFAULT)),Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(c,Times(Sign(m),Quotient(m,Sign(m)))),Power(Times(c,x),Mod(m,Sign(m))),Power(Power(x,Mod(m,Sign(m))),CN1)),Integrate(Times(Power(x,m),$s("§pq"),Power(Plus(Times(a,Power(x,j)),Times(b,Power(x,n))),p)),x)),x),And(FreeQ(List(a,b,c,j,n,p),x),PolyQ($s("§pq"),Power(x,n)),Not(IntegerQ(p)),NeQ(n,j),IntegerQ(Simplify(Times(j,Power(n,CN1)))),IntegerQ(Simplify(Times(n,Power(Plus(m,C1),CN1)))),Not(IntegerQ(n)),GtQ(Sqr(m),C1)))),
IIntegrate(2448,Integrate(Times($p("§pq"),Power(Times(c_,x_),m_),Power(Plus(Times(a_DEFAULT,Power(x_,j_DEFAULT)),Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(c,x),m),Power(Power(x,m),CN1)),Integrate(Times(Power(x,m),$s("§pq"),Power(Plus(Times(a,Power(x,j)),Times(b,Power(x,n))),p)),x)),x),And(FreeQ(List(a,b,c,j,m,n,p),x),PolyQ($s("§pq"),Power(x,n)),Not(IntegerQ(p)),NeQ(n,j),IntegerQ(Simplify(Times(j,Power(n,CN1)))),IntegerQ(Simplify(Times(n,Power(Plus(m,C1),CN1)))),Not(IntegerQ(n))))),
IIntegrate(2449,Integrate(Times($p("§pq"),Power(Times(c_DEFAULT,x_),m_DEFAULT),Power(Plus(Times(a_DEFAULT,Power(x_,j_DEFAULT)),Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times(Power(Times(c,x),m),$s("§pq"),Power(Plus(Times(a,Power(x,j)),Times(b,Power(x,n))),p)),x),x),And(FreeQ(List(a,b,c,j,m,n,p),x),Or(PolyQ($s("§pq"),x),PolyQ($s("§pq"),Power(x,n))),Not(IntegerQ(p)),NeQ(n,j)))),
IIntegrate(2450,Integrate(Times($p("§pq"),Power(Plus(Times(a_DEFAULT,Power(x_,j_DEFAULT)),Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times($s("§pq"),Power(Plus(Times(a,Power(x,j)),Times(b,Power(x,n))),p)),x),x),And(FreeQ(List(a,b,j,n,p),x),Or(PolyQ($s("§pq"),x),PolyQ($s("§pq"),Power(x,n))),Not(IntegerQ(p)),NeQ(n,j)))),
IIntegrate(2451,Integrate(Times($p("§pq"),Power(u_,p_DEFAULT),Power(Times(c_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(c,x),m),$s("§pq"),Power(ExpandToSum(u,x),p)),x),And(FreeQ(list(c,m,p),x),PolyQ($s("§pq"),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x))))),
IIntegrate(2452,Integrate(Times($p("§pq"),Power(u_,p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times($s("§pq"),Power(ExpandToSum(u,x),p)),x),And(FreeQ(p,x),PolyQ($s("§pq"),x),QuadraticQ(u,x),Not(QuadraticMatchQ(u,x))))),
IIntegrate(2453,Integrate(Times($p("§pq"),Power(u_,m_DEFAULT),Power(v_,p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),m),$s("§pq"),Power(ExpandToSum(v,x),p)),x),And(FreeQ(list(m,p),x),PolyQ($s("§pq"),x),LinearQ(u,x),QuadraticQ(v,x),Not(And(LinearMatchQ(u,x),QuadraticMatchQ(v,x)))))),
IIntegrate(2454,Integrate(Times($p("§pq"),Power(u_,p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times($s("§pq"),Power(ExpandToSum(u,x),p)),x),And(FreeQ(p,x),PolyQ($s("§pq"),x),TrinomialQ(u,x),Not(TrinomialMatchQ(u,x))))),
IIntegrate(2455,Integrate(Times($p("§pq"),Power(u_,p_DEFAULT),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(d,x),m),$s("§pq"),Power(ExpandToSum(u,x),p)),x),And(FreeQ(list(d,m,p),x),PolyQ($s("§pq"),x),TrinomialQ(u,x),Not(TrinomialMatchQ(u,x))))),
IIntegrate(2456,Integrate(Times(u_DEFAULT,Power($p("§px"),p_),Power($p("§qx"),q_)),x_Symbol),
    Condition(Module(list(Set($s("§rx"),PolyGCD($s("§px"),$s("§qx"),x))),Condition(Integrate(Times(u,Power($s("§rx"),Plus(p,q)),Power(PolynomialQuotient($s("§px"),$s("§rx"),x),p),Power(PolynomialQuotient($s("§qx"),$s("§rx"),x),q)),x),NeQ($s("§rx"),C1))),And(IGtQ(p,C0),ILtQ(q,C0),PolyQ($s("§px"),x),PolyQ($s("§qx"),x)))),
IIntegrate(2457,Integrate(Times(u_DEFAULT,$p("§px"),Power($p("§qx"),q_)),x_Symbol),
    Condition(Module(list(Set($s("§rx"),PolyGCD($s("§px"),$s("§qx"),x))),Condition(Integrate(Times(u,Power($s("§rx"),Plus(q,C1)),PolynomialQuotient($s("§px"),$s("§rx"),x),Power(PolynomialQuotient($s("§qx"),$s("§rx"),x),q)),x),NeQ($s("§rx"),C1))),And(ILtQ(q,C0),PolyQ($s("§px"),x),PolyQ($s("§qx"),x)))),
IIntegrate(2458,Integrate(Power($p("§pn"),p_DEFAULT),x_Symbol),
    Condition(With(list(Set($s("S"),Times(Coeff($s("§pn"),x,Subtract(Expon($s("§pn"),x),C1)),Power(Times(Expon($s("§pn"),x),Coeff($s("§pn"),x,Expon($s("§pn"),x))),CN1)))),Condition(Subst(Integrate(Power(ExpandToSum(ReplaceAll($s("§pn"),Rule(x,Subtract(x,$s("S")))),x),p),x),x,Plus(x,$s("S"))),Or(BinomialQ(ReplaceAll($s("§pn"),Rule(x,Subtract(x,$s("S")))),x),And(IntegerQ(Times(C1D2,Expon($s("§pn"),x))),TrinomialQ(ReplaceAll($s("§pn"),Rule(x,Subtract(x,$s("S")))),x))))),And(FreeQ(p,x),PolyQ($s("§pn"),x),GtQ(Expon($s("§pn"),x),C2),NeQ(Coeff($s("§pn"),x,Subtract(Expon($s("§pn"),x),C1)),C0)))),
IIntegrate(2459,Integrate(Times(Power($p("§pn"),p_DEFAULT),$p("§qx")),x_Symbol),
    Condition(With(list(Set($s("S"),Times(Coeff($s("§pn"),x,Subtract(Expon($s("§pn"),x),C1)),Power(Times(Expon($s("§pn"),x),Coeff($s("§pn"),x,Expon($s("§pn"),x))),CN1)))),Condition(Subst(Integrate(Times(Power(ExpandToSum(ReplaceAll($s("§pn"),Rule(x,Subtract(x,$s("S")))),x),p),ExpandToSum(ReplaceAll($s("§qx"),Rule(x,Subtract(x,$s("S")))),x)),x),x,Plus(x,$s("S"))),Or(BinomialQ(ReplaceAll($s("§pn"),Rule(x,Subtract(x,$s("S")))),x),And(IntegerQ(Times(C1D2,Expon($s("§pn"),x))),TrinomialQ(ReplaceAll($s("§pn"),Rule(x,Subtract(x,$s("S")))),x))))),And(FreeQ(p,x),PolyQ($s("§pn"),x),GtQ(Expon($s("§pn"),x),C2),NeQ(Coeff($s("§pn"),x,Subtract(Expon($s("§pn"),x),C1)),C0),PolyQ($s("§qx"),x),Not(And(MonomialQ($s("§qx"),x),IGtQ(p,C0)))))),
IIntegrate(2460,Integrate(Times(u_DEFAULT,Power($p("§px"),p_)),x_Symbol),
    Condition(With(list(Set($s("§qx"),Factor(ReplaceAll($s("§px"),Rule(x,Sqrt(x)))))),Condition(Integrate(ExpandIntegrand(Times(u,Power(ReplaceAll($s("§qx"),Rule(x,Sqr(x))),p)),x),x),Not(SumQ(NonfreeFactors($s("§qx"),x))))),And(PolyQ($s("§px"),Sqr(x)),GtQ(Expon($s("§px"),x),C2),Not(BinomialQ($s("§px"),x)),Not(TrinomialQ($s("§px"),x)),ILtQ(p,C0),RationalFunctionQ(u,x))))
  );
}
