package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Denominator;
import static org.matheclipse.core.expression.F.ExpIntegralEi;
import static org.matheclipse.core.expression.F.Expand;
import static org.matheclipse.core.expression.F.F_;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.FullSimplify;
import static org.matheclipse.core.expression.F.G_;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Numerator;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.g_DEFAULT;
import static org.matheclipse.core.expression.F.h_DEFAULT;
import static org.matheclipse.core.expression.F.i_DEFAULT;
import static org.matheclipse.core.expression.F.j_DEFAULT;
import static org.matheclipse.core.expression.F.list;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.FSymbol;
import static org.matheclipse.core.expression.S.GSymbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.g;
import static org.matheclipse.core.expression.S.h;
import static org.matheclipse.core.expression.S.i;
import static org.matheclipse.core.expression.S.j;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandToSum;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ILtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntHide;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.QuadraticMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.QuadraticQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Star;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Unintegrable;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class IntRules133 { 
  public static IAST RULES = List( 
IIntegrate(2661,Integrate(Times(Power(F_,Plus(e_DEFAULT,Times(f_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1)))),Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Subtract(Simp(Star(Times(d,Power(h,CN1)),Integrate(Times(Power(FSymbol,Plus(e,Times(f,Plus(a,Times(b,x)),Power(Plus(c,Times(d,x)),CN1)))),Power(Plus(c,Times(d,x)),CN1)),x)),x),Simp(Star(Times(Subtract(Times(d,g),Times(c,h)),Power(h,CN1)),Integrate(Times(Power(FSymbol,Plus(e,Times(f,Plus(a,Times(b,x)),Power(Plus(c,Times(d,x)),CN1)))),Power(Times(Plus(c,Times(d,x)),Plus(g,Times(h,x))),CN1)),x)),x)),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g,h),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),NeQ(Subtract(Times(d,g),Times(c,h)),C0)))),
IIntegrate(2662,Integrate(Times(Power(F_,Plus(e_DEFAULT,Times(f_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1)))),Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(g,Times(h,x)),Plus(m,C1)),Power(FSymbol,Plus(e,Times(f,Plus(a,Times(b,x)),Power(Plus(c,Times(d,x)),CN1)))),Power(Times(h,Plus(m,C1)),CN1)),x),Simp(Star(Times(f,Subtract(Times(b,c),Times(a,d)),Log(FSymbol),Power(Times(h,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(g,Times(h,x)),Plus(m,C1)),Power(FSymbol,Plus(e,Times(f,Plus(a,Times(b,x)),Power(Plus(c,Times(d,x)),CN1)))),Power(Plus(c,Times(d,x)),CN2)),x)),x)),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g,h),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),NeQ(Subtract(Times(d,g),Times(c,h)),C0),ILtQ(m,CN1)))),
IIntegrate(2663,Integrate(Times(Power(F_,Plus(e_DEFAULT,Times(f_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1)))),Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),CN1),Power(Plus(i_DEFAULT,Times(j_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Simp(Star(Times(CN1,d,Power(Times(h,Subtract(Times(d,i),Times(c,j))),CN1)),Subst(Integrate(Times(Power(FSymbol,Subtract(Plus(e,Times(f,Subtract(Times(b,i),Times(a,j)),Power(Subtract(Times(d,i),Times(c,j)),CN1))),Times(Subtract(Times(b,c),Times(a,d)),f,x,Power(Subtract(Times(d,i),Times(c,j)),CN1)))),Power(x,CN1)),x),x,Times(Plus(i,Times(j,x)),Power(Plus(c,Times(d,x)),CN1)))),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g,h),x),EqQ(Subtract(Times(d,g),Times(c,h)),C0)))),
IIntegrate(2664,Integrate(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Simp(Star(Power(FSymbol,Subtract(a,Times(Sqr(b),Power(Times(C4,c),CN1)))),Integrate(Power(FSymbol,Times(Sqr(Plus(b,Times(C2,c,x))),Power(Times(C4,c),CN1))),x)),x),FreeQ(List(FSymbol,a,b,c),x))),
IIntegrate(2665,Integrate(Power(F_,v_),x_Symbol),
    Condition(Integrate(Power(FSymbol,ExpandToSum(v,x)),x),And(FreeQ(FSymbol,x),QuadraticQ(v,x),Not(QuadraticMatchQ(v,x))))),
IIntegrate(2666,Integrate(Times(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),x_Symbol),
    Condition(Simp(Times(e,Power(FSymbol,Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c,Log(FSymbol)),CN1)),x),And(FreeQ(List(FSymbol,a,b,c,d,e),x),EqQ(Subtract(Times(b,e),Times(C2,c,d)),C0)))),
IIntegrate(2667,Integrate(Times(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Subtract(Simp(Times(e,Power(Plus(d,Times(e,x)),Subtract(m,C1)),Power(FSymbol,Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c,Log(FSymbol)),CN1)),x),Simp(Star(Times(Subtract(m,C1),Sqr(e),Power(Times(C2,c,Log(FSymbol)),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Subtract(m,C2)),Power(FSymbol,Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),x)),And(FreeQ(List(FSymbol,a,b,c,d,e),x),EqQ(Subtract(Times(b,e),Times(C2,c,d)),C0),GtQ(m,C1)))),
IIntegrate(2668,Integrate(Times(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Simp(Times(Power(Times(C2,e),CN1),Power(FSymbol,Subtract(a,Times(Sqr(b),Power(Times(C4,c),CN1)))),ExpIntegralEi(Times(Sqr(Plus(b,Times(C2,c,x))),Log(FSymbol),Power(Times(C4,c),CN1)))),x),And(FreeQ(List(FSymbol,a,b,c,d,e),x),EqQ(Subtract(Times(b,e),Times(C2,c,d)),C0)))),
IIntegrate(2669,Integrate(Times(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Power(FSymbol,Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Star(Times(C2,c,Log(FSymbol),Power(Times(Sqr(e),Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Plus(m,C2)),Power(FSymbol,Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),x)),And(FreeQ(List(FSymbol,a,b,c,d,e),x),EqQ(Subtract(Times(b,e),Times(C2,c,d)),C0),LtQ(m,CN1)))),
IIntegrate(2670,Integrate(Times(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Times(e,Power(FSymbol,Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c,Log(FSymbol)),CN1)),x),Simp(Star(Times(Subtract(Times(b,e),Times(C2,c,d)),Power(Times(C2,c),CN1)),Integrate(Power(FSymbol,Plus(a,Times(b,x),Times(c,Sqr(x)))),x)),x)),And(FreeQ(List(FSymbol,a,b,c,d,e),x),NeQ(Subtract(Times(b,e),Times(C2,c,d)),C0)))),
      IIntegrate(2671,
          Integrate(
              Times(
                  Power(F_, Plus(a_DEFAULT, Times(b_DEFAULT, x_),
                      Times(c_DEFAULT, Sqr(x_)))),
                  Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_)),
              x_Symbol),
          Condition(
              Plus(
                  Simp(
                      Times(e, Power(Plus(d, Times(e, x)), Subtract(m, C1)),
                          Power(FSymbol, Plus(a, Times(b, x),
                              Times(c, Sqr(x)))),
                          Power(Times(C2, c, Log(FSymbol)), CN1)),
                      x),
                  Negate(
                      Simp(
                          Star(
                              Times(Subtract(Times(b, e), Times(C2, c,
                                  d)), Power(Times(C2, c),
                                      CN1)),
                              Integrate(Times(Power(Plus(d, Times(e, x)), Subtract(m, C1)),
                                  Power(FSymbol, Plus(a, Times(b, x), Times(c, Sqr(x))))), x)),
                          x)),
                  Negate(Simp(
                      Star(Times(Subtract(m, C1), Sqr(e), Power(Times(C2, c, Log(FSymbol)), CN1)),
                          Integrate(Times(Power(Plus(d, Times(e, x)), Subtract(m, C2)),
                              Power(FSymbol, Plus(a, Times(b, x), Times(c, Sqr(x))))), x)),
                      x))),
              And(FreeQ(List(FSymbol, a, b, c, d, e), x),
                  NeQ(Subtract(Times(b, e), Times(C2, c, d)), C0), GtQ(m, C1)))),
IIntegrate(2672,Integrate(Times(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Power(FSymbol,Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(e,Plus(m,C1)),CN1)),x),Negate(Simp(Star(Times(C2,c,Log(FSymbol),Power(Times(Sqr(e),Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Plus(m,C2)),Power(FSymbol,Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),x)),Negate(Simp(Star(Times(Subtract(Times(b,e),Times(C2,c,d)),Log(FSymbol),Power(Times(Sqr(e),Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Power(FSymbol,Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),x))),And(FreeQ(List(FSymbol,a,b,c,d,e),x),NeQ(Subtract(Times(b,e),Times(C2,c,d)),C0),LtQ(m,CN1)))),
IIntegrate(2673,Integrate(Times(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(FSymbol,Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Plus(d,Times(e,x)),m)),x),FreeQ(List(FSymbol,a,b,c,d,e,m),x))),
IIntegrate(2674,Integrate(Times(Power(F_,v_),Power(u_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),m),Power(FSymbol,ExpandToSum(v,x))),x),And(FreeQ(list(FSymbol,m),x),LinearQ(u,x),QuadraticQ(v,x),Not(And(LinearMatchQ(u,x),QuadraticMatchQ(v,x)))))),
IIntegrate(2675,Integrate(Times(Power(F_,Times(e_DEFAULT,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(F_,v_))),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(FSymbol,Times(e,Plus(c,Times(d,x)))),Power(Plus(a,Times(b,Power(FSymbol,v))),p)),x))),Subtract(Simp(Star(Power(x,m),u),x),Simp(Star(m,Integrate(Times(Power(x,Subtract(m,C1)),u),x)),x))),And(FreeQ(List(FSymbol,a,b,c,d,e),x),EqQ(v,Times(C2,e,Plus(c,Times(d,x)))),GtQ(m,C0),ILtQ(p,C0)))),
IIntegrate(2676,Integrate(Times(Power(Power(F_,Times(e_DEFAULT,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),n_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(Power(F_,Times(e_DEFAULT,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),n_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Times(d,e,n,Log(FSymbol)),CN1),Subst(Integrate(Power(Plus(a,Times(b,x)),p),x),x,Power(Power(FSymbol,Times(e,Plus(c,Times(d,x)))),n))),x),FreeQ(List(FSymbol,a,b,c,d,e,n,p),x))),
IIntegrate(2677,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Power(Power(F_,Times(e_DEFAULT,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),n_DEFAULT))),p_DEFAULT),Power(Power(G_,Times(h_DEFAULT,Plus(f_DEFAULT,Times(g_DEFAULT,x_)))),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(Power(GSymbol,Times(h,Plus(f,Times(g,x)))),m),Power(Power(Power(FSymbol,Times(e,Plus(c,Times(d,x)))),n),CN1)),Integrate(Times(Power(Power(FSymbol,Times(e,Plus(c,Times(d,x)))),n),Power(Plus(a,Times(b,Power(Power(FSymbol,Times(e,Plus(c,Times(d,x)))),n))),p)),x)),x),And(FreeQ(List(FSymbol,GSymbol,a,b,c,d,e,f,g,h,m,n,p),x),EqQ(Times(d,e,n,Log(FSymbol)),Times(g,h,m,Log(GSymbol)))))),
IIntegrate(2678,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Power(F_,Times(e_DEFAULT,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),p_DEFAULT),Power(G_,Times(h_DEFAULT,Plus(f_DEFAULT,Times(g_DEFAULT,x_))))),x_Symbol),
    Condition(With(list(Set(m,FullSimplify(Times(g,h,Log(GSymbol),Power(Times(d,e,Log(FSymbol)),CN1))))),Condition(Simp(Star(Times(Denominator(m),Power(GSymbol,Subtract(Times(f,h),Times(c,g,h,Power(d,CN1)))),Power(Times(d,e,Log(FSymbol)),CN1)),Subst(Integrate(Times(Power(x,Subtract(Numerator(m),C1)),Power(Plus(a,Times(b,Power(x,Denominator(m)))),p)),x),x,Power(FSymbol,Times(e,Plus(c,Times(d,x)),Power(Denominator(m),CN1))))),x),Or(LeQ(m,CN1),GeQ(m,C1)))),FreeQ(List(FSymbol,GSymbol,a,b,c,d,e,f,g,h,p),x))),
IIntegrate(2679,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Power(F_,Times(e_DEFAULT,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),p_DEFAULT),Power(G_,Times(h_DEFAULT,Plus(f_DEFAULT,Times(g_DEFAULT,x_))))),x_Symbol),
    Condition(With(list(Set(m,FullSimplify(Times(d,e,Log(FSymbol),Power(Times(g,h,Log(GSymbol)),CN1))))),Condition(Simp(Star(Times(Denominator(m),Power(Times(g,h,Log(GSymbol)),CN1)),Subst(Integrate(Times(Power(x,Subtract(Denominator(m),C1)),Power(Plus(a,Times(b,Power(FSymbol,Subtract(Times(c,e),Times(d,e,f,Power(g,CN1)))),Power(x,Numerator(m)))),p)),x),x,Power(GSymbol,Times(h,Plus(f,Times(g,x)),Power(Denominator(m),CN1))))),x),Or(LtQ(m,CN1),GtQ(m,C1)))),FreeQ(List(FSymbol,GSymbol,a,b,c,d,e,f,g,h,p),x))),
IIntegrate(2680,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Power(F_,Times(e_DEFAULT,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),p_DEFAULT),Power(G_,Times(h_DEFAULT,Plus(f_DEFAULT,Times(g_DEFAULT,x_))))),x_Symbol),
    Condition(Integrate(Expand(Times(Power(GSymbol,Times(h,Plus(f,Times(g,x)))),Power(Plus(a,Times(b,Power(FSymbol,Times(e,Plus(c,Times(d,x)))))),p)),x),x),And(FreeQ(List(FSymbol,GSymbol,a,b,c,d,e,f,g,h),x),IGtQ(p,C0))))
  );
}
