package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.A_;
import static org.matheclipse.core.expression.F.A_DEFAULT;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.B_DEFAULT;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.C_DEFAULT;
import static org.matheclipse.core.expression.F.Coefficient;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.D;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.MatchQ;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.PolynomialQuotient;
import static org.matheclipse.core.expression.F.PolynomialRemainder;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Simplify;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.j_;
import static org.matheclipse.core.expression.F.j_DEFAULT;
import static org.matheclipse.core.expression.F.k_DEFAULT;
import static org.matheclipse.core.expression.F.list;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.q_;
import static org.matheclipse.core.expression.F.q_DEFAULT;
import static org.matheclipse.core.expression.F.r_DEFAULT;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.u_DEFAULT;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.ASymbol;
import static org.matheclipse.core.expression.S.BSymbol;
import static org.matheclipse.core.expression.S.CSymbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.j;
import static org.matheclipse.core.expression.S.k;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.r;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Coeff;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Expon;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntSum;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.InverseFunctionQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NegQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PolyQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PosQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RemoveContent;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Rt;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SimplerQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Star;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SumQ;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class IntRules100 { 
  public static IAST RULES = List( 
IIntegrate(2001,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(Times(a_DEFAULT,Power(x_,j_DEFAULT)),Times(b_DEFAULT,Power(x_,k_DEFAULT)),Times(c_DEFAULT,Power(x_,n_DEFAULT))),p_),Plus(A_,Times(B_DEFAULT,Power(x_,q_)))),x_Symbol),
    Condition(Simp(Star(Times(Power(Plus(Times(a,Power(x,j)),Times(b,Power(x,k)),Times(c,Power(x,n))),p),Power(Times(Power(x,Times(j,p)),Power(Plus(a,Times(b,Power(x,Subtract(k,j))),Times(c,Power(x,Times(C2,Subtract(k,j))))),p)),CN1)),Integrate(Times(Power(x,Plus(m,Times(j,p))),Plus(ASymbol,Times(BSymbol,Power(x,Subtract(k,j)))),Power(Plus(a,Times(b,Power(x,Subtract(k,j))),Times(c,Power(x,Times(C2,Subtract(k,j))))),p)),x)),x),And(FreeQ(List(a,b,c,ASymbol,BSymbol,j,k,m,p),x),EqQ(q,Subtract(k,j)),EqQ(n,Subtract(Times(C2,k),j)),Not(IntegerQ(p)),PosQ(Subtract(k,j))))),
IIntegrate(2002,Integrate(Times(Power(u_,m_DEFAULT),Plus(A_,Times(B_DEFAULT,Power(u_,j_DEFAULT))),Power(Plus(Times(b_DEFAULT,Power(u_,n_DEFAULT)),Times(a_DEFAULT,Power(u_,q_DEFAULT)),Times(c_DEFAULT,Power(u_,r_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Coefficient(u,x,C1),CN1),Subst(Integrate(Times(Power(x,m),Plus(ASymbol,Times(BSymbol,Power(x,Subtract(n,q)))),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n)),Times(c,Power(x,Subtract(Times(C2,n),q)))),p)),x),x,u)),x),And(FreeQ(List(a,b,c,ASymbol,BSymbol,m,n,p,q),x),EqQ(j,Subtract(n,q)),EqQ(r,Subtract(Times(C2,n),q)),LinearQ(u,x),NeQ(u,x)))),
IIntegrate(2003,Integrate(Times(u_,Power(Plus(c_,Times(d_DEFAULT,x_)),n_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(u,Power(Plus(c,Times(d,x)),Plus(n,p)),Power(Plus(Times(a,Power(c,CN1)),Times(b,Power(d,CN1),x)),p)),x),And(FreeQ(List(a,b,c,d,n,p),x),EqQ(Plus(Times(b,Sqr(c)),Times(a,Sqr(d))),C0),Or(IntegerQ(p),And(GtQ(a,C0),GtQ(c,C0),Not(IntegerQ(n))))))),
IIntegrate(2004,Integrate(Times(u_,Power(Plus(d_,Times(e_DEFAULT,x_)),q_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(u,Power(Plus(d,Times(e,x)),Plus(p,q)),Power(Plus(Times(a,Power(d,CN1)),Times(c,Power(e,CN1),x)),p)),x),And(FreeQ(List(a,b,c,d,e,q),x),EqQ(Plus(Times(c,Sqr(d)),Times(CN1,b,d,e),Times(a,Sqr(e))),C0),IntegerQ(p)))),
IIntegrate(2005,Integrate(Times($p("§fx"),Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(x,Plus(m,Times(n,p))),Power(Plus(b,Times(a,Power(Power(x,n),CN1))),p),$s("§fx")),x),And(FreeQ(List(a,b,m,n),x),IntegerQ(p),NegQ(n)))),
IIntegrate(2006,Integrate(Times(u_DEFAULT,$p("§px")),x_Symbol),
    Condition(With(list(Set(a,Rt(Coeff($s("§px"),x,C0),Expon($s("§px"),x))),Set(b,Rt(Coeff($s("§px"),x,Expon($s("§px"),x)),Expon($s("§px"),x)))),Condition(Integrate(Times(u,Power(Plus(a,Times(b,x)),Expon($s("§px"),x))),x),EqQ($s("§px"),Power(Plus(a,Times(b,x)),Expon($s("§px"),x))))),And(PolyQ($s("§px"),x),GtQ(Expon($s("§px"),x),C1),NeQ(Coeff($s("§px"),x,C0),C0),Not(MatchQ($s("§px"),Condition(Times(a_DEFAULT,Power(v_,Expon($s("§px"),x))),And(FreeQ(a,x),LinearQ(v,x)))))))),
IIntegrate(2007,Integrate(Times(u_DEFAULT,Power($p("§px"),p_)),x_Symbol),
    Condition(With(list(Set(a,Rt(Coeff($s("§px"),x,C0),Expon($s("§px"),x))),Set(b,Rt(Coeff($s("§px"),x,Expon($s("§px"),x)),Expon($s("§px"),x)))),Condition(Integrate(Times(u,Power(Plus(a,Times(b,x)),Times(Expon($s("§px"),x),p))),x),EqQ($s("§px"),Power(Plus(a,Times(b,x)),Expon($s("§px"),x))))),And(IntegerQ(p),PolyQ($s("§px"),x),GtQ(Expon($s("§px"),x),C1),NeQ(Coeff($s("§px"),x,C0),C0)))),
IIntegrate(2008,Integrate(Times(u_DEFAULT,Power($p("§px"),p_)),x_Symbol),
    Condition(With(list(Set(a,Rt(Coeff($s("§px"),x,C0),Expon($s("§px"),x))),Set(b,Rt(Coeff($s("§px"),x,Expon($s("§px"),x)),Expon($s("§px"),x)))),Condition(Simp(Star(Times(Power(Power(Plus(a,Times(b,x)),Expon($s("§px"),x)),p),Power(Power(Plus(a,Times(b,x)),Times(Expon($s("§px"),x),p)),CN1)),Integrate(Times(u,Power(Plus(a,Times(b,x)),Times(Expon($s("§px"),x),p))),x)),x),EqQ($s("§px"),Power(Plus(a,Times(b,x)),Expon($s("§px"),x))))),And(Not(IntegerQ(p)),PolyQ($s("§px"),x),GtQ(Expon($s("§px"),x),C1),NeQ(Coeff($s("§px"),x,C0),C0)))),
      IIntegrate(2009, Integrate(u_, x_Symbol), Condition(Simp(IntSum(u, x), x), SumQ(u))),
IIntegrate(2010,Integrate(Times(u_,Power(Times(c_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times(Power(Times(c,x),m),u),x),x),And(FreeQ(list(c,m),x),SumQ(u),Not(LinearQ(u,x)),Not(MatchQ(u,Condition(Plus(a_,Times(b_DEFAULT,v_)),And(FreeQ(list(a,b),x),InverseFunctionQ(v)))))))),
IIntegrate(2011,Integrate(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,v_)),m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,v_)),n_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Times(b,Power(d,CN1)),m),Integrate(Times(u,Power(Plus(c,Times(d,v)),Plus(m,n))),x)),x),And(FreeQ(List(a,b,c,d,n),x),EqQ(Subtract(Times(b,c),Times(a,d)),C0),IntegerQ(m),Or(Not(IntegerQ(n)),SimplerQ(Plus(c,Times(d,x)),Plus(a,Times(b,x))))))),
IIntegrate(2012,Integrate(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,v_)),m_),Power(Plus(c_,Times(d_DEFAULT,v_)),n_)),x_Symbol),
    Condition(Simp(Star(Power(Times(b,Power(d,CN1)),m),Integrate(Times(u,Power(Plus(c,Times(d,v)),Plus(m,n))),x)),x),And(FreeQ(List(a,b,c,d,m,n),x),EqQ(Subtract(Times(b,c),Times(a,d)),C0),GtQ(Times(b,Power(d,CN1)),C0),Not(Or(IntegerQ(m),IntegerQ(n)))))),
IIntegrate(2013,Integrate(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,v_)),m_),Power(Plus(c_,Times(d_DEFAULT,v_)),n_)),x_Symbol),
    Condition(Simp(Star(Times(Power(Plus(a,Times(b,v)),m),Power(Power(Plus(c,Times(d,v)),m),CN1)),Integrate(Times(u,Power(Plus(c,Times(d,v)),Plus(m,n))),x)),x),And(FreeQ(List(a,b,c,d,m,n),x),EqQ(Subtract(Times(b,c),Times(a,d)),C0),Not(Or(IntegerQ(m),IntegerQ(n),GtQ(Times(b,Power(d,CN1)),C0)))))),
IIntegrate(2014,Integrate(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,v_)),m_),Plus(A_DEFAULT,Times(B_DEFAULT,v_),Times(C_DEFAULT,Sqr(v_)))),x_Symbol),
    Condition(Simp(Star(Power(b,CN2),Integrate(Times(u,Power(Plus(a,Times(b,v)),Plus(m,C1)),Simp(Plus(Times(b,BSymbol),Times(CN1,a,CSymbol),Times(b,CSymbol,v)),x)),x)),x),And(FreeQ(List(a,b,ASymbol,BSymbol,CSymbol),x),EqQ(Plus(Times(ASymbol,Sqr(b)),Times(CN1,a,b,BSymbol),Times(Sqr(a),CSymbol)),C0),LeQ(m,CN1)))),
IIntegrate(2015,Integrate(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_DEFAULT))),m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Power(x_,q_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Times(d,Power(a,CN1)),p),Integrate(Times(u,Power(Plus(a,Times(b,Power(x,n))),Plus(m,p)),Power(Power(x,Times(n,p)),CN1)),x)),x),And(FreeQ(List(a,b,c,d,m,n),x),EqQ(q,Negate(n)),IntegerQ(p),EqQ(Subtract(Times(a,c),Times(b,d)),C0),Not(And(IntegerQ(m),NegQ(n)))))),
IIntegrate(2016,Integrate(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_DEFAULT))),m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Power(x_,j_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Times(CN1,Sqr(b),Power(d,CN1)),m),Integrate(Times(u,Power(Power(Subtract(a,Times(b,Power(x,n))),m),CN1)),x)),x),And(FreeQ(List(a,b,c,d,m,n,p),x),EqQ(j,Times(C2,n)),EqQ(p,Negate(m)),EqQ(Plus(Times(Sqr(b),c),Times(Sqr(a),d)),C0),GtQ(a,C0),LtQ(d,C0),GtQ(Sqr(b),C0)))),
IIntegrate(2017,Integrate(Times($p("§px"),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(Coeff($s("§px"),x,Subtract(n,C1)),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x),Integrate(Times(Subtract($s("§px"),Times(Coeff($s("§px"),x,Subtract(n,C1)),Power(x,Subtract(n,C1)))),Power(Plus(a,Times(b,Power(x,n))),p)),x)),And(FreeQ(list(a,b),x),PolyQ($s("§px"),x),IGtQ(p,C1),IGtQ(n,C1),NeQ(Coeff($s("§px"),x,Subtract(n,C1)),C0),NeQ($s("§px"),Times(Coeff($s("§px"),x,Subtract(n,C1)),Power(x,Subtract(n,C1)))),Not(MatchQ($s("§px"),Condition(Times($p("§qx",true),Power(Plus(c_,Times(d_DEFAULT,Power(x,m_))),q_)),And(FreeQ(list(c,d),x),PolyQ($s("§qx"),x),IGtQ(q,C1),IGtQ(m,C1),NeQ(Coeff(Times($s("§qx"),Power(Plus(a,Times(b,Power(x,n))),p)),x,Subtract(m,C1)),C0),GtQ(Times(m,q),Times(n,p))))))))),
IIntegrate(2018,Integrate(Times($p("§px"),Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_DEFAULT))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(Coeff($s("§px"),x,Subtract(Subtract(n,m),C1)),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x),Integrate(Times(Subtract($s("§px"),Times(Coeff($s("§px"),x,Subtract(Subtract(n,m),C1)),Power(x,Subtract(Subtract(n,m),C1)))),Power(x,m),Power(Plus(a,Times(b,Power(x,n))),p)),x)),And(FreeQ(List(a,b,m,n),x),PolyQ($s("§px"),x),IGtQ(p,C1),IGtQ(Subtract(n,m),C0),NeQ(Coeff($s("§px"),x,Subtract(Subtract(n,m),C1)),C0)))),
IIntegrate(2019,Integrate(Times(u_DEFAULT,Power($p("§px"),p_DEFAULT),Power($p("§qx"),q_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(u,Power(PolynomialQuotient($s("§px"),$s("§qx"),x),p),Power($s("§qx"),Plus(p,q))),x),And(FreeQ(q,x),PolyQ($s("§px"),x),PolyQ($s("§qx"),x),EqQ(PolynomialRemainder($s("§px"),$s("§qx"),x),C0),IntegerQ(p),LtQ(Times(p,q),C0)))),
IIntegrate(2020,Integrate(Times($p("§pp"),Power($p("§qq"),CN1)),x_Symbol),
    Condition(With(list(Set(p,Expon($s("§pp"),x)),Set(q,Expon($s("§qq"),x))),Condition(Simp(Times(Coeff($s("§pp"),x,p),Log(RemoveContent($s("§qq"),x)),Power(Times(q,Coeff($s("§qq"),x,q)),CN1)),x),And(EqQ(p,Subtract(q,C1)),EqQ($s("§pp"),Simplify(Times(Coeff($s("§pp"),x,p),Power(Times(q,Coeff($s("§qq"),x,q)),CN1),D($s("§qq"),x))))))),And(PolyQ($s("§pp"),x),PolyQ($s("§qq"),x))))
  );
}
