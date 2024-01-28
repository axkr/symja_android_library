package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.A_;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.B_DEFAULT;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.Coefficient;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.QQ;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.j_DEFAULT;
import static org.matheclipse.core.expression.F.list;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.q_DEFAULT;
import static org.matheclipse.core.expression.F.r_DEFAULT;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.ASymbol;
import static org.matheclipse.core.expression.S.BSymbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.j;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.r;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PosQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Star;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class IntRules98 { 
  public static IAST RULES = List( 
IIntegrate(1961,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(c_DEFAULT,Power(x_,r_DEFAULT))),CN1D2)),x_Symbol),
          Condition(
              Simp(
                  Star(
                      Times(
                          Power(x, Times(C1D2,
                              q)),
                          Sqrt(
                              Plus(a, Times(b, Power(x, Subtract(n, q))),
                                  Times(c, Power(x, Times(C2, Subtract(n, q)))))),
                          Power(Plus(Times(a, Power(x, q)), Times(b, Power(x, n)),
                              Times(c, Power(x, Subtract(Times(C2, n), q)))), CN1D2)),
                      Integrate(Times(Power(x, Subtract(m, Times(C1D2, q))),
                          Power(Plus(a, Times(b, Power(x, Subtract(n, q))),
                              Times(c, Power(x, Times(C2, Subtract(n, q))))), CN1D2)),
                          x)),
                  x),
              And(FreeQ(List(a, b, c, m, n, q), x), EqQ(r, Subtract(Times(C2, n), q)),
                  PosQ(Subtract(n, q)),
                  Or(And(EqQ(m, C1), EqQ(n, C3), EqQ(q, C2)),
                      And(Or(EqQ(m, QQ(3L, 2L)), EqQ(m, C1D2),
                          EqQ(m, QQ(5L, 2L))), EqQ(n, C3), EqQ(q, C1)))))),
IIntegrate(1962,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(c_DEFAULT,Power(x_,r_DEFAULT))),QQ(-3L,2L))),x_Symbol),
    Condition(Simp(Times(CN2,Power(x,Times(C1D2,Subtract(n,C1))),Plus(b,Times(C2,c,x)),Power(Times(Subtract(Sqr(b),Times(C4,a,c)),Sqrt(Plus(Times(a,Power(x,Subtract(n,C1))),Times(b,Power(x,n)),Times(c,Power(x,Plus(n,C1)))))),CN1)),x),And(FreeQ(List(a,b,c,n),x),EqQ(m,Times(C3,C1D2,Subtract(n,C1))),EqQ(q,Subtract(n,C1)),EqQ(r,Plus(n,C1)),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0)))),
IIntegrate(1963,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(c_DEFAULT,Power(x_,r_DEFAULT))),QQ(-3L,2L))),x_Symbol),
    Condition(Simp(Times(Power(x,Times(C1D2,Subtract(n,C1))),Plus(Times(C4,a),Times(C2,b,x)),Power(Times(Subtract(Sqr(b),Times(C4,a,c)),Sqrt(Plus(Times(a,Power(x,Subtract(n,C1))),Times(b,Power(x,n)),Times(c,Power(x,Plus(n,C1)))))),CN1)),x),And(FreeQ(List(a,b,c,n),x),EqQ(m,Times(C1D2,Subtract(Times(C3,n),C1))),EqQ(q,Subtract(n,C1)),EqQ(r,Plus(n,C1)),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0)))),
IIntegrate(1964,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(c_DEFAULT,Power(x_,r_DEFAULT))),p_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Subtract(m,n)),Power(Plus(Times(a,Power(x,Subtract(n,C1))),Times(b,Power(x,n)),Times(c,Power(x,Plus(n,C1)))),Plus(p,C1)),Power(Times(C2,c,Plus(p,C1)),CN1)),x),Simp(Star(Times(b,Power(Times(C2,c),CN1)),Integrate(Times(Power(x,Subtract(m,C1)),Power(Plus(Times(a,Power(x,Subtract(n,C1))),Times(b,Power(x,n)),Times(c,Power(x,Plus(n,C1)))),p)),x)),x)),And(FreeQ(list(a,b,c),x),EqQ(r,Subtract(Times(C2,n),q)),PosQ(Subtract(n,q)),Not(IntegerQ(p)),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),IGtQ(n,C0),RationalQ(m,p,q),EqQ(Subtract(Plus(m,Times(p,Subtract(n,C1))),C1),C0)))),
IIntegrate(1965,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(c_DEFAULT,Power(x_,r_DEFAULT))),p_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,Negate(n),q,C1)),Plus(b,Times(C2,c,Power(x,Subtract(n,q)))),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n)),Times(c,Power(x,Subtract(Times(C2,n),q)))),p),Power(Times(C2,c,Subtract(n,q),Plus(Times(C2,p),C1)),CN1)),x),Simp(Star(Times(p,Subtract(Sqr(b),Times(C4,a,c)),Power(Times(C2,c,Plus(Times(C2,p),C1)),CN1)),Integrate(Times(Power(x,Plus(m,q)),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n)),Times(c,Power(x,Subtract(Times(C2,n),q)))),Subtract(p,C1))),x)),x)),And(FreeQ(list(a,b,c),x),EqQ(r,Subtract(Times(C2,n),q)),PosQ(Subtract(n,q)),Not(IntegerQ(p)),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),IGtQ(n,C0),GtQ(p,C0),RationalQ(m,q),EqQ(Plus(m,Times(p,q),C1),Subtract(n,q))))),
IIntegrate(1966,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(c_DEFAULT,Power(x_,r_DEFAULT))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,Negate(n),q,C1)),Plus(Times(b,Subtract(n,q),p),Times(c,Plus(m,Times(p,q),Times(Subtract(n,q),Subtract(Times(C2,p),C1)),C1),Power(x,Subtract(n,q)))),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n)),Times(c,Power(x,Subtract(Times(C2,n),q)))),p),Power(Times(c,Plus(m,Times(p,Subtract(Times(C2,n),q)),C1),Plus(m,Times(p,q),Times(Subtract(n,q),Subtract(Times(C2,p),C1)),C1)),CN1)),x),Simp(Star(Times(Subtract(n,q),p,Power(Times(c,Plus(m,Times(p,Subtract(Times(C2,n),q)),C1),Plus(m,Times(p,q),Times(Subtract(n,q),Subtract(Times(C2,p),C1)),C1)),CN1)),Integrate(Times(Power(x,Subtract(m,Subtract(n,Times(C2,q)))),Simp(Plus(Times(CN1,a,b,Plus(m,Times(p,q),Negate(n),q,C1)),Times(Subtract(Times(C2,a,c,Plus(m,Times(p,q),Times(Subtract(n,q),Subtract(Times(C2,p),C1)),C1)),Times(Sqr(b),Plus(m,Times(p,q),Times(Subtract(n,q),Subtract(p,C1)),C1))),Power(x,Subtract(n,q)))),x),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n)),Times(c,Power(x,Subtract(Times(C2,n),q)))),Subtract(p,C1))),x)),x)),And(FreeQ(list(a,b,c),x),EqQ(r,Subtract(Times(C2,n),q)),PosQ(Subtract(n,q)),Not(IntegerQ(p)),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),IGtQ(n,C0),GtQ(p,C0),RationalQ(m,q),GtQ(Plus(m,Times(p,q),C1),Subtract(n,q)),NeQ(Plus(m,Times(p,Subtract(Times(C2,n),q)),C1),C0),NeQ(Plus(m,Times(p,q),Times(Subtract(n,q),Subtract(Times(C2,p),C1)),C1),C0)))),
IIntegrate(1967,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(c_DEFAULT,Power(x_,r_DEFAULT))),p_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n)),Times(c,Power(x,Subtract(Times(C2,n),q)))),p),Power(Plus(m,Times(p,q),C1),CN1)),x),Simp(Star(Times(Subtract(n,q),p,Power(Plus(m,Times(p,q),C1),CN1)),Integrate(Times(Power(x,Plus(m,n)),Plus(b,Times(C2,c,Power(x,Subtract(n,q)))),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n)),Times(c,Power(x,Subtract(Times(C2,n),q)))),Subtract(p,C1))),x)),x)),And(FreeQ(list(a,b,c),x),EqQ(r,Subtract(Times(C2,n),q)),PosQ(Subtract(n,q)),Not(IntegerQ(p)),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),IGtQ(n,C0),GtQ(p,C0),RationalQ(m,q),LeQ(Plus(m,Times(p,q),C1),Plus(Negate(Subtract(n,q)),C1)),NeQ(Plus(m,Times(p,q),C1),C0)))),
IIntegrate(1968,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(c_DEFAULT,Power(x_,r_DEFAULT))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n)),Times(c,Power(x,Subtract(Times(C2,n),q)))),p),Power(Plus(m,Times(p,Subtract(Times(C2,n),q)),C1),CN1)),x),Simp(Star(Times(Subtract(n,q),p,Power(Plus(m,Times(p,Subtract(Times(C2,n),q)),C1),CN1)),Integrate(Times(Power(x,Plus(m,q)),Plus(Times(C2,a),Times(b,Power(x,Subtract(n,q)))),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n)),Times(c,Power(x,Subtract(Times(C2,n),q)))),Subtract(p,C1))),x)),x)),And(FreeQ(list(a,b,c),x),EqQ(r,Subtract(Times(C2,n),q)),PosQ(Subtract(n,q)),Not(IntegerQ(p)),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),IGtQ(n,C0),GtQ(p,C0),RationalQ(m,q),GtQ(Plus(m,Times(p,q),C1),Negate(Subtract(n,q))),NeQ(Plus(m,Times(p,Subtract(Times(C2,n),q)),C1),C0)))),
IIntegrate(1969,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(c_DEFAULT,Power(x_,r_DEFAULT))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(x,Plus(m,Negate(q),C1)),Plus(Sqr(b),Times(CN1,C2,a,c),Times(b,c,Power(x,Subtract(n,q)))),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n)),Times(c,Power(x,Subtract(Times(C2,n),q)))),Plus(p,C1)),Power(Times(a,Subtract(n,q),Plus(p,C1),Subtract(Sqr(b),Times(C4,a,c))),CN1)),x),Simp(Star(Times(Subtract(Times(C2,a,c),Times(Sqr(b),Plus(p,C2))),Power(Times(a,Plus(p,C1),Subtract(Sqr(b),Times(C4,a,c))),CN1)),Integrate(Times(Power(x,Subtract(m,q)),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n)),Times(c,Power(x,Subtract(Times(C2,n),q)))),Plus(p,C1))),x)),x)),And(FreeQ(list(a,b,c),x),EqQ(r,Subtract(Times(C2,n),q)),PosQ(Subtract(n,q)),Not(IntegerQ(p)),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),IGtQ(n,C0),LtQ(p,CN1),RationalQ(m,p,q),EqQ(Plus(m,Times(p,q),C1),Times(CN1,Subtract(n,q),Plus(Times(C2,p),C3)))))),
IIntegrate(1970,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(c_DEFAULT,Power(x_,r_DEFAULT))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(x,Plus(m,Times(CN1,C2,n),q,C1)),Plus(Times(C2,a),Times(b,Power(x,Subtract(n,q)))),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n)),Times(c,Power(x,Subtract(Times(C2,n),q)))),Plus(p,C1)),Power(Times(Subtract(n,q),Plus(p,C1),Subtract(Sqr(b),Times(C4,a,c))),CN1)),x),Simp(Star(Power(Times(Subtract(n,q),Plus(p,C1),Subtract(Sqr(b),Times(C4,a,c))),CN1),Integrate(Times(Power(x,Plus(m,Times(CN1,C2,n),q)),Plus(Times(C2,a,Plus(m,Times(p,q),Times(CN1,C2,Subtract(n,q)),C1)),Times(b,Plus(m,Times(p,q),Times(Subtract(n,q),Plus(Times(C2,p),C1)),C1),Power(x,Subtract(n,q)))),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n)),Times(c,Power(x,Subtract(Times(C2,n),q)))),Plus(p,C1))),x)),x)),And(FreeQ(list(a,b,c),x),EqQ(r,Subtract(Times(C2,n),q)),PosQ(Subtract(n,q)),Not(IntegerQ(p)),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),IGtQ(n,C0),LtQ(p,CN1),RationalQ(m,q),GtQ(Plus(m,Times(p,q),C1),Times(C2,Subtract(n,q)))))),
IIntegrate(1971,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(c_DEFAULT,Power(x_,r_DEFAULT))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(x,Plus(m,Negate(q),C1)),Plus(Sqr(b),Times(CN1,C2,a,c),Times(b,c,Power(x,Subtract(n,q)))),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n)),Times(c,Power(x,Subtract(Times(C2,n),q)))),Plus(p,C1)),Power(Times(a,Subtract(n,q),Plus(p,C1),Subtract(Sqr(b),Times(C4,a,c))),CN1)),x),Simp(Star(Power(Times(a,Subtract(n,q),Plus(p,C1),Subtract(Sqr(b),Times(C4,a,c))),CN1),Integrate(Times(Power(x,Subtract(m,q)),Plus(Times(Sqr(b),Plus(m,Times(p,q),Times(Subtract(n,q),Plus(p,C1)),C1)),Times(CN1,C2,a,c,Plus(m,Times(p,q),Times(C2,Subtract(n,q),Plus(p,C1)),C1)),Times(b,c,Plus(m,Times(p,q),Times(Subtract(n,q),Plus(Times(C2,p),C3)),C1),Power(x,Subtract(n,q)))),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n)),Times(c,Power(x,Subtract(Times(C2,n),q)))),Plus(p,C1))),x)),x)),And(FreeQ(list(a,b,c),x),EqQ(r,Subtract(Times(C2,n),q)),PosQ(Subtract(n,q)),Not(IntegerQ(p)),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),IGtQ(n,C0),LtQ(p,CN1),RationalQ(m,q),LtQ(Plus(m,Times(p,q),C1),Subtract(n,q))))),
IIntegrate(1972,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(c_DEFAULT,Power(x_,r_DEFAULT))),p_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,Negate(n),C1)),Plus(b,Times(C2,c,Power(x,Subtract(n,q)))),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n)),Times(c,Power(x,Subtract(Times(C2,n),q)))),Plus(p,C1)),Power(Times(Subtract(n,q),Plus(p,C1),Subtract(Sqr(b),Times(C4,a,c))),CN1)),x),Simp(Star(Power(Times(Subtract(n,q),Plus(p,C1),Subtract(Sqr(b),Times(C4,a,c))),CN1),Integrate(Times(Power(x,Subtract(m,n)),Plus(Times(b,Plus(m,Times(p,q),Negate(n),q,C1)),Times(C2,c,Plus(m,Times(p,q),Times(C2,Subtract(n,q),Plus(p,C1)),C1),Power(x,Subtract(n,q)))),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n)),Times(c,Power(x,Subtract(Times(C2,n),q)))),Plus(p,C1))),x)),x)),And(FreeQ(list(a,b,c),x),EqQ(r,Subtract(Times(C2,n),q)),PosQ(Subtract(n,q)),Not(IntegerQ(p)),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),IGtQ(n,C0),LtQ(p,CN1),RationalQ(m,q),LtQ(Subtract(n,q),Plus(m,Times(p,q),C1),Times(C2,Subtract(n,q)))))),
IIntegrate(1973,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(c_DEFAULT,Power(x_,r_DEFAULT))),p_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,Times(CN1,C2,n),q,C1)),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n)),Times(c,Power(x,Subtract(Times(C2,n),q)))),Plus(p,C1)),Power(Times(C2,c,Subtract(n,q),Plus(p,C1)),CN1)),x),Simp(Star(Times(b,Power(Times(C2,c),CN1)),Integrate(Times(Power(x,Plus(m,Negate(n),q)),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n)),Times(c,Power(x,Subtract(Times(C2,n),q)))),p)),x)),x)),And(FreeQ(list(a,b,c),x),EqQ(r,Subtract(Times(C2,n),q)),PosQ(Subtract(n,q)),Not(IntegerQ(p)),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),IGtQ(n,C0),GeQ(p,CN1),LtQ(p,C0),RationalQ(m,q),EqQ(Plus(m,Times(p,q),C1),Times(C2,Subtract(n,q)))))),
IIntegrate(1974,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(c_DEFAULT,Power(x_,r_DEFAULT))),p_)),x_Symbol),
    Condition(Subtract(Simp(Times(CN1,Power(x,Plus(m,Negate(q),C1)),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n)),Times(c,Power(x,Subtract(Times(C2,n),q)))),Plus(p,C1)),Power(Times(C2,a,Subtract(n,q),Plus(p,C1)),CN1)),x),Simp(Star(Times(b,Power(Times(C2,a),CN1)),Integrate(Times(Power(x,Subtract(Plus(m,n),q)),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n)),Times(c,Power(x,Subtract(Times(C2,n),q)))),p)),x)),x)),And(FreeQ(list(a,b,c),x),EqQ(r,Subtract(Times(C2,n),q)),PosQ(Subtract(n,q)),Not(IntegerQ(p)),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),IGtQ(n,C0),GeQ(p,CN1),LtQ(p,C0),RationalQ(m,q),EqQ(Plus(m,Times(p,q),C1),Times(CN2,Subtract(n,q),Plus(p,C1)))))),
IIntegrate(1975,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(c_DEFAULT,Power(x_,r_DEFAULT))),p_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,Times(CN1,C2,n),q,C1)),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n)),Times(c,Power(x,Subtract(Times(C2,n),q)))),Plus(p,C1)),Power(Times(c,Plus(m,Times(p,q),Times(C2,Subtract(n,q),p),C1)),CN1)),x),Simp(Star(Power(Times(c,Plus(m,Times(p,q),Times(C2,Subtract(n,q),p),C1)),CN1),Integrate(Times(Power(x,Subtract(m,Times(C2,Subtract(n,q)))),Plus(Times(a,Plus(m,Times(p,q),Times(CN1,C2,Subtract(n,q)),C1)),Times(b,Plus(m,Times(p,q),Times(Subtract(n,q),Subtract(p,C1)),C1),Power(x,Subtract(n,q)))),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n)),Times(c,Power(x,Subtract(Times(C2,n),q)))),p)),x)),x)),And(FreeQ(list(a,b,c),x),EqQ(r,Subtract(Times(C2,n),q)),PosQ(Subtract(n,q)),Not(IntegerQ(p)),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),IGtQ(n,C0),GeQ(p,CN1),LtQ(p,C0),RationalQ(m,q),GtQ(Plus(m,Times(p,q),C1),Times(C2,Subtract(n,q)))))),
IIntegrate(1976,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(c_DEFAULT,Power(x_,r_DEFAULT))),p_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,Negate(q),C1)),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n)),Times(c,Power(x,Subtract(Times(C2,n),q)))),Plus(p,C1)),Power(Times(a,Plus(m,Times(p,q),C1)),CN1)),x),Simp(Star(Power(Times(a,Plus(m,Times(p,q),C1)),CN1),Integrate(Times(Power(x,Subtract(Plus(m,n),q)),Plus(Times(b,Plus(m,Times(p,q),Times(Subtract(n,q),Plus(p,C1)),C1)),Times(c,Plus(m,Times(p,q),Times(C2,Subtract(n,q),Plus(p,C1)),C1),Power(x,Subtract(n,q)))),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n)),Times(c,Power(x,Subtract(Times(C2,n),q)))),p)),x)),x)),And(FreeQ(list(a,b,c),x),EqQ(r,Subtract(Times(C2,n),q)),PosQ(Subtract(n,q)),Not(IntegerQ(p)),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),IGtQ(n,C0),GeQ(p,CN1),LtQ(p,C0),RationalQ(m,q),LtQ(Plus(m,Times(p,q),C1),C0)))),
IIntegrate(1977,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(c_DEFAULT,Power(x_,r_DEFAULT))),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n)),Times(c,Power(x,Subtract(Times(C2,n),q)))),p),Power(Times(Power(x,Times(p,q)),Power(Plus(a,Times(b,Power(x,Subtract(n,q))),Times(c,Power(x,Times(C2,Subtract(n,q))))),p)),CN1)),Integrate(Times(Power(x,Plus(m,Times(p,q))),Power(Plus(a,Times(b,Power(x,Subtract(n,q))),Times(c,Power(x,Times(C2,Subtract(n,q))))),p)),x)),x),And(FreeQ(List(a,b,c,m,n,p,q),x),EqQ(r,Subtract(Times(C2,n),q)),Not(IntegerQ(p)),PosQ(Subtract(n,q))))),
IIntegrate(1978,Integrate(Times(Power(u_,m_DEFAULT),Power(Plus(Times(b_DEFAULT,Power(u_,n_DEFAULT)),Times(a_DEFAULT,Power(u_,q_DEFAULT)),Times(c_DEFAULT,Power(u_,r_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Coefficient(u,x,C1),CN1),Subst(Integrate(Times(Power(x,m),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n)),Times(c,Power(x,Subtract(Times(C2,n),q)))),p)),x),x,u)),x),And(FreeQ(List(a,b,c,m,n,p,q),x),EqQ(r,Subtract(Times(C2,n),q)),LinearQ(u,x),NeQ(u,x)))),
IIntegrate(1979,Integrate(Times(Power(Plus(Times(c_DEFAULT,Power(x_,j_DEFAULT)),Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT))),p_DEFAULT),Plus(A_,Times(B_DEFAULT,Power(x_,r_DEFAULT)))),x_Symbol),
    Condition(Integrate(Times(Power(x,Times(p,q)),Plus(ASymbol,Times(BSymbol,Power(x,Subtract(n,q)))),Power(Plus(a,Times(b,Power(x,Subtract(n,q))),Times(c,Power(x,Times(C2,Subtract(n,q))))),p)),x),And(FreeQ(List(a,b,c,ASymbol,BSymbol,n,q),x),EqQ(r,Subtract(n,q)),EqQ(j,Subtract(Times(C2,n),q)),IntegerQ(p),PosQ(Subtract(n,q))))),
IIntegrate(1980,Integrate(Times(Plus(A_,Times(B_DEFAULT,Power(x_,j_DEFAULT))),Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(c_DEFAULT,Power(x_,r_DEFAULT))),CN1D2)),x_Symbol),
    Condition(Simp(Star(Times(Power(x,Times(C1D2,q)),Sqrt(Plus(a,Times(b,Power(x,Subtract(n,q))),Times(c,Power(x,Times(C2,Subtract(n,q)))))),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n)),Times(c,Power(x,Subtract(Times(C2,n),q)))),CN1D2)),Integrate(Times(Plus(ASymbol,Times(BSymbol,Power(x,Subtract(n,q)))),Power(Times(Power(x,Times(C1D2,q)),Sqrt(Plus(a,Times(b,Power(x,Subtract(n,q))),Times(c,Power(x,Times(C2,Subtract(n,q))))))),CN1)),x)),x),And(FreeQ(List(a,b,c,ASymbol,BSymbol,n,q),x),EqQ(j,Subtract(n,q)),EqQ(r,Subtract(Times(C2,n),q)),PosQ(Subtract(n,q)),EqQ(n,C3),EqQ(q,C2))))
  );
}
