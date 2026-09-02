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
class IntRules249 { 
  public static IAST RULES = List( 
IIntegrate(4981,Integrate(Times(Power(Cos(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),n_DEFAULT),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Times(f_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Module(list(Set(u,IntHide(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Power(Cos(Plus(d,Times(e,x))),n)),x))),Subtract(Simp(Dist(Power(Times(f,x),m),u,x),x),Simp(Dist(Times(f,m),Integrate(Times(Power(Times(f,x),Plus(m,CN1)),u),x),x),x))),And(FreeQ(List(FSymbol,a,b,c,d,e,f),x),IGtQ(n,C0),GtQ(m,C0)))),
IIntegrate(4982,Integrate(Times(Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Times(f_DEFAULT,x_),m_),Sin(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(f,x),Plus(m,C1)),Power(Times(f,Plus(m,C1)),CN1),Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Sin(Plus(d,Times(e,x)))),x),Negate(Simp(Dist(Times(e,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(f,x),Plus(m,C1)),Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Cos(Plus(d,Times(e,x)))),x),x),x)),Negate(Simp(Dist(Times(b,c,Log(FSymbol),Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(f,x),Plus(m,C1)),Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Sin(Plus(d,Times(e,x)))),x),x),x))),And(FreeQ(List(FSymbol,a,b,c,d,e,f,m),x),Or(LtQ(m,CN1),SumSimplerQ(m,C1))))),
IIntegrate(4983,Integrate(Times(Cos(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Times(f_DEFAULT,x_),m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(f,x),Plus(m,C1)),Power(Times(f,Plus(m,C1)),CN1),Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Cos(Plus(d,Times(e,x)))),x),Simp(Dist(Times(e,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(f,x),Plus(m,C1)),Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Sin(Plus(d,Times(e,x)))),x),x),x),Negate(Simp(Dist(Times(b,c,Log(FSymbol),Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(f,x),Plus(m,C1)),Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Cos(Plus(d,Times(e,x)))),x),x),x))),And(FreeQ(List(FSymbol,a,b,c,d,e,f,m),x),Or(LtQ(m,CN1),SumSimplerQ(m,C1))))),
IIntegrate(4984,Integrate(Times(Power(Cos(Plus(f_DEFAULT,Times(g_DEFAULT,x_))),n_DEFAULT),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Sin(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Times(Power(Sin(Plus(d,Times(e,x))),m),Power(Cos(Plus(f,Times(g,x))),n)),x),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g),x),IGtQ(m,C0),IGtQ(n,C0)))),
IIntegrate(4985,Integrate(Times(Power(Cos(Plus(f_DEFAULT,Times(g_DEFAULT,x_))),n_DEFAULT),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(x_,p_DEFAULT),Power(Sin(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Times(Power(x,p),Power(FSymbol,Times(c,Plus(a,Times(b,x))))),Times(Power(Sin(Plus(d,Times(e,x))),m),Power(Cos(Plus(f,Times(g,x))),n)),x),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g),x),IGtQ(m,C0),IGtQ(n,C0),IGtQ(p,C0)))),
IIntegrate(4986,Integrate(Times(Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power($(G_,Plus(d_DEFAULT,Times(e_DEFAULT,x_))),m_DEFAULT),Power($($p("H"),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigToExp(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Times(Power(G(Plus(d,Times(e,x))),m),Power(H(Plus(d,Times(e,x))),n)),x),x),And(FreeQ(List(FSymbol,a,b,c,d,e),x),IGtQ(m,C0),IGtQ(n,C0),TrigQ(GSymbol),TrigQ($s("H"))))),
IIntegrate(4987,Integrate(Times(Power(F_,u_),Power(Sin(v_),n_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigToExp(Power(FSymbol,u),Power(Sin(v),n),x),x),And(FreeQ(FSymbol,x),Or(LinearQ(u,x),PolyQ(u,x,C2)),Or(LinearQ(v,x),PolyQ(v,x,C2)),IGtQ(n,C0)))),
IIntegrate(4988,Integrate(Times(Power(Cos(v_),n_DEFAULT),Power(F_,u_)),x_Symbol),
    Condition(Integrate(ExpandTrigToExp(Power(FSymbol,u),Power(Cos(v),n),x),x),And(FreeQ(FSymbol,x),Or(LinearQ(u,x),PolyQ(u,x,C2)),Or(LinearQ(v,x),PolyQ(v,x,C2)),IGtQ(n,C0)))),
IIntegrate(4989,Integrate(Times(Power(Cos(v_),n_DEFAULT),Power(F_,u_),Power(Sin(v_),m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigToExp(Power(FSymbol,u),Times(Power(Sin(v),m),Power(Cos(v),n)),x),x),And(FreeQ(FSymbol,x),Or(LinearQ(u,x),PolyQ(u,x,C2)),Or(LinearQ(v,x),PolyQ(v,x,C2)),IGtQ(m,C0),IGtQ(n,C0)))),
IIntegrate(4990,Integrate(Sin(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(x,Sin(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Plus(Times(Sqr(b),Sqr(d),Sqr(n)),C1),CN1)),x),Simp(Times(b,d,n,x,Cos(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Plus(Times(Sqr(b),Sqr(d),Sqr(n)),C1),CN1)),x)),And(FreeQ(List(a,b,c,d,n),x),NeQ(Plus(Times(Sqr(b),Sqr(d),Sqr(n)),C1),C0)))),
IIntegrate(4991,Integrate(Cos(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(x,Cos(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Plus(Times(Sqr(b),Sqr(d),Sqr(n)),C1),CN1)),x),Simp(Times(b,d,n,x,Sin(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Plus(Times(Sqr(b),Sqr(d),Sqr(n)),C1),CN1)),x)),And(FreeQ(List(a,b,c,d,n),x),NeQ(Plus(Times(Sqr(b),Sqr(d),Sqr(n)),C1),C0)))),
IIntegrate(4992,Integrate(Power(Sin(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),p_),x_Symbol),
    Condition(Plus(Simp(Times(x,Power(Sin(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),p),Power(Plus(Times(Sqr(b),Sqr(d),Sqr(n),Sqr(p)),C1),CN1)),x),Negate(Simp(Times(b,d,n,p,x,Cos(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Sin(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Plus(p,CN1)),Power(Plus(Times(Sqr(b),Sqr(d),Sqr(n),Sqr(p)),C1),CN1)),x)),Simp(Dist(Times(Sqr(b),Sqr(d),Sqr(n),p,Plus(p,CN1),Power(Plus(Times(Sqr(b),Sqr(d),Sqr(n),Sqr(p)),C1),CN1)),Integrate(Power(Sin(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Plus(p,CN2)),x),x),x)),And(FreeQ(List(a,b,c,d,n),x),IGtQ(p,C1),NeQ(Plus(Times(Sqr(b),Sqr(d),Sqr(n),Sqr(p)),C1),C0)))),
IIntegrate(4993,Integrate(Power(Cos(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),p_),x_Symbol),
    Condition(Plus(Simp(Times(x,Power(Cos(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),p),Power(Plus(Times(Sqr(b),Sqr(d),Sqr(n),Sqr(p)),C1),CN1)),x),Simp(Times(b,d,n,p,x,Power(Cos(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Plus(p,CN1)),Sin(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Plus(Times(Sqr(b),Sqr(d),Sqr(n),Sqr(p)),C1),CN1)),x),Simp(Dist(Times(Sqr(b),Sqr(d),Sqr(n),p,Plus(p,CN1),Power(Plus(Times(Sqr(b),Sqr(d),Sqr(n),Sqr(p)),C1),CN1)),Integrate(Power(Cos(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Plus(p,CN2)),x),x),x)),And(FreeQ(List(a,b,c,d,n),x),IGtQ(p,C1),NeQ(Plus(Times(Sqr(b),Sqr(d),Sqr(n),Sqr(p)),C1),C0)))),
IIntegrate(4994,Integrate(Power(Sin(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Power(Times(Power(C2,p),Power(b,p),Power(d,p),Power(p,p)),CN1),Integrate(ExpandIntegrand(Power(Subtract(Times(Exp(Times(a,b,Sqr(d),p)),Power(Power(x,Power(p,CN1)),CN1)),Times(Power(x,Power(p,CN1)),Power(Exp(Times(a,b,Sqr(d),p)),CN1))),p),x),x),x),x),And(FreeQ(list(a,b,d),x),IGtQ(p,C0),EqQ(Plus(Times(Sqr(b),Sqr(d),Sqr(p)),C1),C0)))),
IIntegrate(4995,Integrate(Power(Cos(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Power(Power(C2,p),CN1),Integrate(ExpandIntegrand(Power(Plus(Times(Exp(Times(a,b,Sqr(d),p)),Power(Power(x,Power(p,CN1)),CN1)),Times(Power(x,Power(p,CN1)),Power(Exp(Times(a,b,Sqr(d),p)),CN1))),p),x),x),x),x),And(FreeQ(list(a,b,d),x),IGtQ(p,C0),EqQ(Plus(Times(Sqr(b),Sqr(d),Sqr(p)),C1),C0)))),
IIntegrate(4996,Integrate(Power(Sin(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_),x_Symbol),
    Condition(Simp(Dist(Times(Power(Sin(Times(d,Plus(a,Times(b,Log(x))))),p),Power(x,Times(CI,b,d,p)),Power(Power(Subtract(C1,Times(Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),p),CN1)),Integrate(Times(Power(Subtract(C1,Times(Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),p),Power(Power(x,Times(CI,b,d,p)),CN1)),x),x),x),And(FreeQ(List(a,b,d,p),x),Not(IntegerQ(p))))),
IIntegrate(4997,Integrate(Power(Cos(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_),x_Symbol),
    Condition(Simp(Dist(Times(Power(Cos(Times(d,Plus(a,Times(b,Log(x))))),p),Power(x,Times(CI,b,d,p)),Power(Power(Plus(C1,Times(Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),p),CN1)),Integrate(Times(Power(Plus(C1,Times(Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),p),Power(Power(x,Times(CI,b,d,p)),CN1)),x),x),x),And(FreeQ(List(a,b,d,p),x),Not(IntegerQ(p))))),
IIntegrate(4998,Integrate(Power(Sin(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Times(x,Power(Times(n,Power(Times(c,Power(x,n)),Power(n,CN1))),CN1)),Subst(Integrate(Times(Power(x,Plus(Power(n,CN1),CN1)),Power(Sin(Times(d,Plus(a,Times(b,Log(x))))),p)),x),x,Times(c,Power(x,n))),x),x),And(FreeQ(List(a,b,c,d,n,p),x),Or(NeQ(c,C1),NeQ(n,C1))))),
IIntegrate(4999,Integrate(Power(Cos(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Times(x,Power(Times(n,Power(Times(c,Power(x,n)),Power(n,CN1))),CN1)),Subst(Integrate(Times(Power(x,Plus(Power(n,CN1),CN1)),Power(Cos(Times(d,Plus(a,Times(b,Log(x))))),p)),x),x,Times(c,Power(x,n))),x),x),And(FreeQ(List(a,b,c,d,n,p),x),Or(NeQ(c,C1),NeQ(n,C1))))),
IIntegrate(5000,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Sin(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(m,C1),Power(Times(e,x),Plus(m,C1)),Sin(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Plus(Times(Sqr(b),Sqr(d),e,Sqr(n)),Times(e,Sqr(Plus(m,C1)))),CN1)),x),Simp(Times(b,d,n,Power(Times(e,x),Plus(m,C1)),Cos(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Plus(Times(Sqr(b),Sqr(d),e,Sqr(n)),Times(e,Sqr(Plus(m,C1)))),CN1)),x)),And(FreeQ(List(a,b,c,d,e,m,n),x),NeQ(Plus(Times(Sqr(b),Sqr(d),Sqr(n)),Sqr(Plus(m,C1))),C0))))
  );
}
