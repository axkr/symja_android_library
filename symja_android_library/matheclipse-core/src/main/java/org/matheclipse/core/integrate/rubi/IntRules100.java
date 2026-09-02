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
class IntRules100 { 
  public static IAST RULES = List( 
IIntegrate(2001,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(Times(a_DEFAULT,Power(x_,j_DEFAULT)),Times(b_DEFAULT,Power(x_,k_DEFAULT)),Times(c_DEFAULT,Power(x_,n_DEFAULT))),p_),Plus(A_,Times(B_DEFAULT,Power(x_,q_)))),x_Symbol),
    Condition(Simp(Dist(Times(Power(Plus(Times(a,Power(x,j)),Times(b,Power(x,k)),Times(c,Power(x,n))),p),Power(Times(Power(x,Times(j,p)),Power(Plus(a,Times(b,Power(x,Subtract(k,j))),Times(c,Power(x,Times(C2,Subtract(k,j))))),p)),CN1)),Integrate(Times(Power(x,Plus(m,Times(j,p))),Plus(ASymbol,Times(BSymbol,Power(x,Subtract(k,j)))),Power(Plus(a,Times(b,Power(x,Subtract(k,j))),Times(c,Power(x,Times(C2,Subtract(k,j))))),p)),x),x),x),And(FreeQ(List(a,b,c,ASymbol,BSymbol,j,k,m,p),x),EqQ(q,Subtract(k,j)),EqQ(n,Subtract(Times(C2,k),j)),Not(IntegerQ(p)),PosQ(Subtract(k,j))))),
IIntegrate(2002,Integrate(Times(Power(u_,m_DEFAULT),Plus(A_,Times(B_DEFAULT,Power(u_,j_DEFAULT))),Power(Plus(Times(b_DEFAULT,Power(u_,n_DEFAULT)),Times(a_DEFAULT,Power(u_,q_DEFAULT)),Times(c_DEFAULT,Power(u_,r_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Coefficient(u,x,C1),CN1),Subst(Integrate(Times(Power(x,m),Plus(ASymbol,Times(BSymbol,Power(x,Subtract(n,q)))),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n)),Times(c,Power(x,Subtract(Times(C2,n),q)))),p)),x),x,u),x),x),And(FreeQ(List(a,b,c,ASymbol,BSymbol,m,n,p,q),x),EqQ(j,Subtract(n,q)),EqQ(r,Subtract(Times(C2,n),q)),LinearQ(u,x),NeQ(u,x)))),
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
    Condition(With(list(Set(a,Rt(Coeff($s("§px"),x,C0),Expon($s("§px"),x))),Set(b,Rt(Coeff($s("§px"),x,Expon($s("§px"),x)),Expon($s("§px"),x)))),Condition(Simp(Dist(Times(Power(Power(Plus(a,Times(b,x)),Expon($s("§px"),x)),p),Power(Power(Plus(a,Times(b,x)),Times(Expon($s("§px"),x),p)),CN1)),Integrate(Times(u,Power(Plus(a,Times(b,x)),Times(Expon($s("§px"),x),p))),x),x),x),EqQ($s("§px"),Power(Plus(a,Times(b,x)),Expon($s("§px"),x))))),And(Not(IntegerQ(p)),PolyQ($s("§px"),x),GtQ(Expon($s("§px"),x),C1),NeQ(Coeff($s("§px"),x,C0),C0)))),
IIntegrate(2009,Integrate(u_,x_Symbol),
    Condition(Simp(IntSum(u,x),x),SumQ(u))),
IIntegrate(2010,Integrate(Times(u_,Power(Times(c_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times(Power(Times(c,x),m),u),x),x),And(FreeQ(list(c,m),x),SumQ(u),Not(LinearQ(u,x)),Not(MatchQ(u,Condition(Plus(a_,Times(b_DEFAULT,v_)),And(FreeQ(list(a,b),x),InverseFunctionQ(v)))))))),
IIntegrate(2011,Integrate(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,v_)),m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,v_)),n_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Times(b,Power(d,CN1)),m),Integrate(Times(u,Power(Plus(c,Times(d,v)),Plus(m,n))),x),x),x),And(FreeQ(List(a,b,c,d,n),x),EqQ(Subtract(Times(b,c),Times(a,d)),C0),IntegerQ(m),Or(Not(IntegerQ(n)),SimplerQ(Plus(c,Times(d,x)),Plus(a,Times(b,x))))))),
IIntegrate(2012,Integrate(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,v_)),m_),Power(Plus(c_,Times(d_DEFAULT,v_)),n_)),x_Symbol),
    Condition(Simp(Dist(Power(Times(b,Power(d,CN1)),m),Integrate(Times(u,Power(Plus(c,Times(d,v)),Plus(m,n))),x),x),x),And(FreeQ(List(a,b,c,d,m,n),x),EqQ(Subtract(Times(b,c),Times(a,d)),C0),GtQ(Times(b,Power(d,CN1)),C0),Not(Or(IntegerQ(m),IntegerQ(n)))))),
IIntegrate(2013,Integrate(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,v_)),m_),Power(Plus(c_,Times(d_DEFAULT,v_)),n_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Plus(a,Times(b,v)),m),Power(Power(Plus(c,Times(d,v)),m),CN1)),Integrate(Times(u,Power(Plus(c,Times(d,v)),Plus(m,n))),x),x),x),And(FreeQ(List(a,b,c,d,m,n),x),EqQ(Subtract(Times(b,c),Times(a,d)),C0),Not(Or(IntegerQ(m),IntegerQ(n),GtQ(Times(b,Power(d,CN1)),C0)))))),
IIntegrate(2014,Integrate(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,v_)),m_),Plus(A_DEFAULT,Times(B_DEFAULT,v_),Times(C_DEFAULT,Sqr(v_)))),x_Symbol),
    Condition(Simp(Dist(Power(b,CN2),Integrate(Times(u,Power(Plus(a,Times(b,v)),Plus(m,C1)),Simp(Plus(Times(b,BSymbol),Times(CN1,a,CSymbol),Times(b,CSymbol,v)),x)),x),x),x),And(FreeQ(List(a,b,ASymbol,BSymbol,CSymbol),x),EqQ(Plus(Times(ASymbol,Sqr(b)),Times(CN1,a,b,BSymbol),Times(Sqr(a),CSymbol)),C0),LeQ(m,CN1)))),
IIntegrate(2015,Integrate(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_DEFAULT))),m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Power(x_,q_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Times(d,Power(a,CN1)),p),Integrate(Times(u,Power(Plus(a,Times(b,Power(x,n))),Plus(m,p)),Power(Power(x,Times(n,p)),CN1)),x),x),x),And(FreeQ(List(a,b,c,d,m,n),x),EqQ(q,Negate(n)),IntegerQ(p),EqQ(Subtract(Times(a,c),Times(b,d)),C0),Not(And(IntegerQ(m),NegQ(n)))))),
IIntegrate(2016,Integrate(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_DEFAULT))),m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Power(x_,j_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Times(CN1,Sqr(b),Power(d,CN1)),m),Integrate(Times(u,Power(Power(Subtract(a,Times(b,Power(x,n))),m),CN1)),x),x),x),And(FreeQ(List(a,b,c,d,m,n,p),x),EqQ(j,Times(C2,n)),EqQ(p,Negate(m)),EqQ(Plus(Times(Sqr(b),c),Times(Sqr(a),d)),C0),GtQ(a,C0),LtQ(d,C0),GtQ(Sqr(b),C0)))),
IIntegrate(2017,Integrate(Times($p("§px"),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(Coeff($s("§px"),x,Plus(n,CN1)),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x),Integrate(Times(Subtract($s("§px"),Times(Coeff($s("§px"),x,Plus(n,CN1)),Power(x,Plus(n,CN1)))),Power(Plus(a,Times(b,Power(x,n))),p)),x)),And(FreeQ(list(a,b),x),PolyQ($s("§px"),x),IGtQ(p,C1),IGtQ(n,C1),NeQ(Coeff($s("§px"),x,Plus(n,CN1)),C0),NeQ($s("§px"),Times(Coeff($s("§px"),x,Plus(n,CN1)),Power(x,Plus(n,CN1)))),Not(MatchQ($s("§px"),Condition(Times($p("§qx",true),Power(Plus(c_,Times(d_DEFAULT,Power(x,m_))),q_)),And(FreeQ(list(c,d),x),PolyQ($s("§qx"),x),IGtQ(q,C1),IGtQ(m,C1),NeQ(Coeff(Times($s("§qx"),Power(Plus(a,Times(b,Power(x,n))),p)),x,Plus(m,CN1)),C0),GtQ(Times(m,q),Times(n,p))))))))),
IIntegrate(2018,Integrate(Times($p("§px"),Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_DEFAULT))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(Coeff($s("§px"),x,Plus(n,Negate(m),CN1)),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x),Integrate(Times(Subtract($s("§px"),Times(Coeff($s("§px"),x,Plus(n,Negate(m),CN1)),Power(x,Plus(n,Negate(m),CN1)))),Power(x,m),Power(Plus(a,Times(b,Power(x,n))),p)),x)),And(FreeQ(List(a,b,m,n),x),PolyQ($s("§px"),x),IGtQ(p,C1),IGtQ(Subtract(n,m),C0),NeQ(Coeff($s("§px"),x,Plus(n,Negate(m),CN1)),C0)))),
IIntegrate(2019,Integrate(Times(u_DEFAULT,Power($p("§px"),p_DEFAULT),Power($p("§qx"),q_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(u,Power(PolynomialQuotient($s("§px"),$s("§qx"),x),p),Power($s("§qx"),Plus(p,q))),x),And(FreeQ(q,x),PolyQ($s("§px"),x),PolyQ($s("§qx"),x),EqQ(PolynomialRemainder($s("§px"),$s("§qx"),x),C0),IntegerQ(p),LtQ(Times(p,q),C0)))),
IIntegrate(2020,Integrate(Times($p("§pp"),Power($p("§qq"),CN1)),x_Symbol),
    Condition(With(list(Set(p,Expon($s("§pp"),x)),Set(q,Expon($s("§qq"),x))),Condition(Simp(Times(Coeff($s("§pp"),x,p),Log(RemoveContent($s("§qq"),x)),Power(Times(q,Coeff($s("§qq"),x,q)),CN1)),x),And(EqQ(p,Plus(q,CN1)),EqQ($s("§pp"),Simplify(Times(Coeff($s("§pp"),x,p),Power(Times(q,Coeff($s("§qq"),x,q)),CN1),D($s("§qq"),x))))))),And(PolyQ($s("§pp"),x),PolyQ($s("§qq"),x))))
  );
}
