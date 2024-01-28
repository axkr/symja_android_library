package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.D;
import static org.matheclipse.core.expression.F.Denominator;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Module;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.list;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.q_DEFAULT;
import static org.matheclipse.core.expression.F.r_DEFAULT;
import static org.matheclipse.core.expression.F.s_DEFAULT;
import static org.matheclipse.core.expression.F.t_DEFAULT;
import static org.matheclipse.core.expression.F.u_DEFAULT;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.Min;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.k;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.r;
import static org.matheclipse.core.expression.S.s;
import static org.matheclipse.core.expression.S.t;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.AlgebraicFunctionQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Coeff;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandToSum;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Expon;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FracPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FractionQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ILtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.MonomialQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PolyQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PosQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Star;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class IntRules101 { 
  public static IAST RULES = List( 
IIntegrate(2021,Integrate(Times($p("§pp"),Power($p("§qq"),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(p,Expon($s("§pp"),x)),Set(q,Expon($s("§qq"),x))),Condition(Simp(Times(Coeff($s("§pp"),x,p),Power(x,Plus(p,Negate(q),C1)),Power($s("§qq"),Plus(m,C1)),Power(Times(Plus(p,Times(m,q),C1),Coeff($s("§qq"),x,q)),CN1)),x),And(NeQ(Plus(p,Times(m,q),C1),C0),EqQ(Times(Plus(p,Times(m,q),C1),Coeff($s("§qq"),x,q),$s("§pp")),Times(Coeff($s("§pp"),x,p),Power(x,Subtract(p,q)),Plus(Times(Plus(p,Negate(q),C1),$s("§qq")),Times(Plus(m,C1),x,D($s("§qq"),x)))))))),And(FreeQ(m,x),PolyQ($s("§pp"),x),PolyQ($s("§qq"),x),NeQ(m,CN1)))),
IIntegrate(2022,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus($p("a1"),Times($p("b1",true),Power(x_,n_DEFAULT))),p_),Power(Plus($p("a2"),Times($p("b2",true),Power(x_,n_DEFAULT))),p_)),x_Symbol),
    Condition(Simp(Times(Power(Plus($s("a1"),Times($s("b1"),Power(x,n))),Plus(p,C1)),Power(Plus($s("a2"),Times($s("b2"),Power(x,n))),Plus(p,C1)),Power(Times(C2,$s("b1"),$s("b2"),n,Plus(p,C1)),CN1)),x),And(FreeQ(List($s("a1"),$s("b1"),$s("a2"),$s("b2"),m,n,p),x),EqQ(Plus(Times($s("a2"),$s("b1")),Times($s("a1"),$s("b2"))),C0),EqQ(Plus(m,Times(CN1,C2,n),C1),C0),NeQ(p,CN1)))),
IIntegrate(2023,Integrate(Times($p("§pp"),Power($p("§qq"),m_DEFAULT),Power($p("§rr"),n_DEFAULT)),x_Symbol),
    Condition(With(list(Set(p,Expon($s("§pp"),x)),Set(q,Expon($s("§qq"),x)),Set(r,Expon($s("§rr"),x))),Condition(Simp(Times(Coeff($s("§pp"),x,p),Power(x,Plus(p,Negate(q),Negate(r),C1)),Power($s("§qq"),Plus(m,C1)),Power($s("§rr"),Plus(n,C1)),Power(Times(Plus(p,Times(m,q),Times(n,r),C1),Coeff($s("§qq"),x,q),Coeff($s("§rr"),x,r)),CN1)),x),And(NeQ(Plus(p,Times(m,q),Times(n,r),C1),C0),EqQ(Times(Plus(p,Times(m,q),Times(n,r),C1),Coeff($s("§qq"),x,q),Coeff($s("§rr"),x,r),$s("§pp")),Times(Coeff($s("§pp"),x,p),Power(x,Subtract(Subtract(p,q),r)),Plus(Times(Plus(p,Negate(q),Negate(r),C1),$s("§qq"),$s("§rr")),Times(Plus(m,C1),x,$s("§rr"),D($s("§qq"),x)),Times(Plus(n,C1),x,$s("§qq"),D($s("§rr"),x)))))))),And(FreeQ(list(m,n),x),PolyQ($s("§pp"),x),PolyQ($s("§qq"),x),PolyQ($s("§rr"),x),NeQ(m,CN1),NeQ(n,CN1)))),
IIntegrate(2024,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power($p("§pq"),n_DEFAULT))),p_DEFAULT),$p("§qr")),x_Symbol),
    Condition(With(list(Set(q,Expon($s("§pq"),x)),Set(r,Expon($s("§qr"),x))),Condition(Simp(Star(Times(Coeff($s("§qr"),x,r),Power(Times(q,Coeff($s("§pq"),x,q)),CN1)),Subst(Integrate(Power(Plus(a,Times(b,Power(x,n))),p),x),x,$s("§pq"))),x),And(EqQ(r,Subtract(q,C1)),EqQ(Times(Coeff($s("§qr"),x,r),D($s("§pq"),x)),Times(q,Coeff($s("§pq"),x,q),$s("§qr")))))),And(FreeQ(List(a,b,n,p),x),PolyQ($s("§pq"),x),PolyQ($s("§qr"),x)))),
IIntegrate(2025,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power($p("§pq"),n_DEFAULT)),Times(c_DEFAULT,Power($p("§pq"),$p("n2",true)))),p_DEFAULT),$p("§qr")),x_Symbol),
    Condition(Module(list(Set(q,Expon($s("§pq"),x)),Set(r,Expon($s("§qr"),x))),Condition(Simp(Star(Times(Coeff($s("§qr"),x,r),Power(Times(q,Coeff($s("§pq"),x,q)),CN1)),Subst(Integrate(Power(Plus(a,Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n)))),p),x),x,$s("§pq"))),x),And(EqQ(r,Subtract(q,C1)),EqQ(Times(Coeff($s("§qr"),x,r),D($s("§pq"),x)),Times(q,Coeff($s("§pq"),x,q),$s("§qr")))))),And(FreeQ(List(a,b,c,n,p),x),EqQ($s("n2"),Times(C2,n)),PolyQ($s("§pq"),x),PolyQ($s("§qr"),x)))),
IIntegrate(2026,Integrate(Times($p("§fx",true),Power($p("§px"),p_DEFAULT)),x_Symbol),
    Condition(With(list(Set(r,Expon($s("§px"),x,Min))),Condition(Integrate(Times(Power(x,Times(p,r)),Power(ExpandToSum(Times($s("§px"),Power(Power(x,r),CN1)),x),p),$s("§fx")),x),IGtQ(r,C0))),And(PolyQ($s("§px"),x),IntegerQ(p),Not(MonomialQ($s("§px"),x)),Or(ILtQ(p,C0),Not(PolyQ(u,x)))))),
IIntegrate(2027,Integrate(Times($p("§fx",true),Power(Plus(Times(a_DEFAULT,Power(x_,r_DEFAULT)),Times(b_DEFAULT,Power(x_,s_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(x,Times(p,r)),Power(Plus(a,Times(b,Power(x,Subtract(s,r)))),p),$s("§fx")),x),And(FreeQ(List(a,b,r,s),x),IntegerQ(p),PosQ(Subtract(s,r)),Not(And(EqQ(p,C1),EqQ(u,C1)))))),
IIntegrate(2028,Integrate(Times($p("§fx",true),Power(Plus(Times(a_DEFAULT,Power(x_,r_DEFAULT)),Times(b_DEFAULT,Power(x_,s_DEFAULT)),Times(c_DEFAULT,Power(x_,t_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(x,Times(p,r)),Power(Plus(a,Times(b,Power(x,Subtract(s,r))),Times(c,Power(x,Subtract(t,r)))),p),$s("§fx")),x),And(FreeQ(List(a,b,c,r,s,t),x),IntegerQ(p),PosQ(Subtract(s,r)),PosQ(Subtract(t,r)),Not(And(EqQ(p,C1),EqQ(u,C1)))))),
      IIntegrate(2029,
          Integrate(Times($p("§fx", true), Power(
              Plus(Times(d_DEFAULT, Power(x_, q_DEFAULT)), Times(a_DEFAULT, Power(x_, r_DEFAULT)),
                  Times(b_DEFAULT, Power(x_, s_DEFAULT)), Times(c_DEFAULT, Power(x_, t_DEFAULT))),
              p_DEFAULT)), x_Symbol),
          Condition(
              Integrate(
                  Times(Power(x, Times(p, r)),
                      Power(Plus(a, Times(b, Power(x, Subtract(s, r))),
                          Times(c, Power(x, Subtract(t, r))), Times(d, Power(x, Subtract(q, r)))),
                          p),
                      $s("§fx")),
                  x),
              And(FreeQ(List(a, b, c, d, r, s, t, q), x), IntegerQ(p), PosQ(Subtract(s, r)),
                  PosQ(Subtract(t, r)), PosQ(Subtract(q, r)), Not(And(EqQ(p, C1), EqQ(u, C1)))))),
IIntegrate(2030,Integrate(Times($p("§fx",true),Power(v_,m_DEFAULT),Power(Times(b_,v_),n_)),x_Symbol),
    Condition(Simp(Star(Power(Power(b,m),CN1),Integrate(Times(Power(Times(b,v),Plus(m,n)),$s("§fx")),x)),x),And(FreeQ(list(b,n),x),IntegerQ(m)))),
IIntegrate(2031,Integrate(Times($p("§fx",true),Power(Times(a_DEFAULT,v_),m_),Power(Times(b_DEFAULT,v_),n_)),x_Symbol),
    Condition(Simp(Star(Times(Power(a,Plus(m,C1D2)),Power(b,Subtract(n,C1D2)),Sqrt(Times(b,v)),Power(Times(a,v),CN1D2)),Integrate(Times(Power(v,Plus(m,n)),$s("§fx")),x)),x),And(FreeQ(list(a,b,m),x),Not(IntegerQ(m)),IGtQ(Plus(n,C1D2),C0),IntegerQ(Plus(m,n))))),
IIntegrate(2032,Integrate(Times($p("§fx",true),Power(Times(a_DEFAULT,v_),m_),Power(Times(b_DEFAULT,v_),n_)),x_Symbol),
    Condition(Simp(Star(Times(Power(a,Subtract(m,C1D2)),Power(b,Plus(n,C1D2)),Sqrt(Times(a,v)),Power(Times(b,v),CN1D2)),Integrate(Times(Power(v,Plus(m,n)),$s("§fx")),x)),x),And(FreeQ(list(a,b,m),x),Not(IntegerQ(m)),ILtQ(Subtract(n,C1D2),C0),IntegerQ(Plus(m,n))))),
IIntegrate(2033,Integrate(Times($p("§fx",true),Power(Times(a_DEFAULT,v_),m_),Power(Times(b_DEFAULT,v_),n_)),x_Symbol),
    Condition(Simp(Star(Times(Power(a,Plus(m,n)),Power(Times(b,v),n),Power(Power(Times(a,v),n),CN1)),Integrate(Times(Power(v,Plus(m,n)),$s("§fx")),x)),x),And(FreeQ(List(a,b,m,n),x),Not(IntegerQ(m)),Not(IntegerQ(n)),IntegerQ(Plus(m,n))))),
IIntegrate(2034,Integrate(Times($p("§fx",true),Power(Times(a_DEFAULT,v_),m_),Power(Times(b_DEFAULT,v_),n_)),x_Symbol),
    Condition(Simp(Star(Times(Power(b,IntPart(n)),Power(Times(b,v),FracPart(n)),Power(Times(Power(a,IntPart(n)),Power(Times(a,v),FracPart(n))),CN1)),Integrate(Times(Power(Times(a,v),Plus(m,n)),$s("§fx")),x)),x),And(FreeQ(List(a,b,m,n),x),Not(IntegerQ(m)),Not(IntegerQ(n)),Not(IntegerQ(Plus(m,n)))))),
IIntegrate(2035,Integrate(Times($p("§fx"),Power(x_,m_)),x_Symbol),
    Condition(With(list(Set(k,Denominator(m))),Simp(Star(k,Subst(Integrate(Times(Power(x,Subtract(Times(k,Plus(m,C1)),C1)),$($s("§substpower"),$s("§fx"),x,k)),x),x,Power(x,Power(k,CN1)))),x)),And(FractionQ(m),AlgebraicFunctionQ($s("§fx"),x)))),
IIntegrate(2036,Integrate(Times(u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_DEFAULT))),q_DEFAULT),Power(Plus($p("a1"),Times($p("b1",true),Power(x_,$p("non2",true)))),p_DEFAULT),Power(Plus($p("a2"),Times($p("b2",true),Power(x_,$p("non2",true)))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(u,Power(Plus(Times($s("a1"),$s("a2")),Times($s("b1"),$s("b2"),Power(x,n))),p),Power(Plus(c,Times(d,Power(x,n))),q)),x),And(FreeQ(List($s("a1"),$s("b1"),$s("a2"),$s("b2"),c,d,n,p,q),x),EqQ($s("non2"),Times(C1D2,n)),EqQ(Plus(Times($s("a2"),$s("b1")),Times($s("a1"),$s("b2"))),C0),Or(IntegerQ(p),And(GtQ($s("a1"),C0),GtQ($s("a2"),C0)))))),
IIntegrate(2037,Integrate(Times(u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_DEFAULT)),Times(e_DEFAULT,Power(x_,$p("n2",true)))),q_DEFAULT),Power(Plus($p("a1"),Times($p("b1",true),Power(x_,$p("non2",true)))),p_DEFAULT),Power(Plus($p("a2"),Times($p("b2",true),Power(x_,$p("non2",true)))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(u,Power(Plus(Times($s("a1"),$s("a2")),Times($s("b1"),$s("b2"),Power(x,n))),p),Power(Plus(c,Times(d,Power(x,n)),Times(e,Power(x,Times(C2,n)))),q)),x),And(FreeQ(List($s("a1"),$s("b1"),$s("a2"),$s("b2"),c,d,e,n,p,q),x),EqQ($s("non2"),Times(C1D2,n)),EqQ($s("n2"),Times(C2,n)),EqQ(Plus(Times($s("a2"),$s("b1")),Times($s("a1"),$s("b2"))),C0),Or(IntegerQ(p),And(GtQ($s("a1"),C0),GtQ($s("a2"),C0)))))),
IIntegrate(2038,Integrate(Times(u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_DEFAULT))),q_DEFAULT),Power(Plus($p("a1"),Times($p("b1",true),Power(x_,$p("non2",true)))),p_),Power(Plus($p("a2"),Times($p("b2",true),Power(x_,$p("non2",true)))),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(Plus($s("a1"),Times($s("b1"),Power(x,Times(C1D2,n)))),FracPart(p)),Power(Plus($s("a2"),Times($s("b2"),Power(x,Times(C1D2,n)))),FracPart(p)),Power(Power(Plus(Times($s("a1"),$s("a2")),Times($s("b1"),$s("b2"),Power(x,n))),FracPart(p)),CN1)),Integrate(Times(u,Power(Plus(Times($s("a1"),$s("a2")),Times($s("b1"),$s("b2"),Power(x,n))),p),Power(Plus(c,Times(d,Power(x,n))),q)),x)),x),And(FreeQ(List($s("a1"),$s("b1"),$s("a2"),$s("b2"),c,d,n,p,q),x),EqQ($s("non2"),Times(C1D2,n)),EqQ(Plus(Times($s("a2"),$s("b1")),Times($s("a1"),$s("b2"))),C0),Not(And(EqQ(n,C2),IGtQ(q,C0)))))),
IIntegrate(2039,Integrate(Times(u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_DEFAULT)),Times(e_DEFAULT,Power(x_,$p("n2",true)))),q_DEFAULT),Power(Plus($p("a1"),Times($p("b1",true),Power(x_,$p("non2",true)))),p_DEFAULT),Power(Plus($p("a2"),Times($p("b2",true),Power(x_,$p("non2",true)))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(Plus($s("a1"),Times($s("b1"),Power(x,Times(C1D2,n)))),FracPart(p)),Power(Plus($s("a2"),Times($s("b2"),Power(x,Times(C1D2,n)))),FracPart(p)),Power(Power(Plus(Times($s("a1"),$s("a2")),Times($s("b1"),$s("b2"),Power(x,n))),FracPart(p)),CN1)),Integrate(Times(u,Power(Plus(Times($s("a1"),$s("a2")),Times($s("b1"),$s("b2"),Power(x,n))),p),Power(Plus(c,Times(d,Power(x,n)),Times(e,Power(x,Times(C2,n)))),q)),x)),x),And(FreeQ(List($s("a1"),$s("b1"),$s("a2"),$s("b2"),c,d,e,n,p,q),x),EqQ($s("non2"),Times(C1D2,n)),EqQ($s("n2"),Times(C2,n)),EqQ(Plus(Times($s("a2"),$s("b1")),Times($s("a1"),$s("b2"))),C0)))),
IIntegrate(2040,Integrate(Times(Power(Plus($p("e1"),Times($p("f1",true),Power(x_,$p("n2",true)))),r_DEFAULT),Power(Plus($p("e2"),Times($p("f2",true),Power(x_,$p("n2",true)))),r_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_))),q_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Plus(a,Times(b,Power(x,n))),p),Power(Plus(c,Times(d,Power(x,n))),q),Power(Plus(Times($s("e1"),$s("e2")),Times($s("f1"),$s("f2"),Power(x,n))),r)),x),And(FreeQ(List(a,b,c,d,$s("e1"),$s("f1"),$s("e2"),$s("f2"),n,p,q,r),x),EqQ($s("n2"),Times(C1D2,n)),EqQ(Plus(Times($s("e2"),$s("f1")),Times($s("e1"),$s("f2"))),C0),Or(IntegerQ(r),And(GtQ($s("e1"),C0),GtQ($s("e2"),C0))))))
  );
}
