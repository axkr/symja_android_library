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
class IntRules309 { 
  public static IAST RULES = List( 
IIntegrate(6181,Integrate(Times(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Coth(Plus(c_,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(b,x,Power(d,CN1)),x),Simp(Star(Cosh(Times(Subtract(Times(b,c),Times(a,d)),Power(d,CN1))),Integrate(Times(Csch(Plus(a,Times(b,x))),Csch(Plus(c,Times(d,x)))),x)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Subtract(Sqr(b),Sqr(d)),C0),NeQ(Subtract(Times(b,c),Times(a,d)),C0)))),
IIntegrate(6182,Integrate(Times(u_DEFAULT,Power(Plus(Times(Cosh(v_),a_DEFAULT),Times(b_DEFAULT,Sinh(v_))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(u,Power(Times(a,Exp(Times(a,Power(b,CN1),v))),n)),x),And(FreeQ(list(a,b,n),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0)))),
IIntegrate(6183,Integrate(Sinh(Times(Sqr(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT))),d_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Star(Negate(C1D2),Integrate(Exp(Times(CN1,d,Sqr(Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x)),x),Simp(Star(C1D2,Integrate(Exp(Times(d,Sqr(Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x)),x)),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(6184,Integrate(Cosh(Times(Sqr(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT))),d_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Star(C1D2,Integrate(Exp(Times(CN1,d,Sqr(Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x)),x),Simp(Star(C1D2,Integrate(Exp(Times(d,Sqr(Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x)),x)),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(6185,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Sinh(Times(Sqr(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT))),d_DEFAULT))),x_Symbol),
    Condition(Plus(Simp(Star(Negate(C1D2),Integrate(Times(Power(Times(e,x),m),Power(Exp(Times(d,Sqr(Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),CN1)),x)),x),Simp(Star(C1D2,Integrate(Times(Power(Times(e,x),m),Exp(Times(d,Sqr(Plus(a,Times(b,Log(Times(c,Power(x,n))))))))),x)),x)),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(6186,Integrate(Times(Cosh(Times(Sqr(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT))),d_DEFAULT)),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Star(C1D2,Integrate(Times(Power(Times(e,x),m),Power(Exp(Times(d,Sqr(Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),CN1)),x)),x),Simp(Star(C1D2,Integrate(Times(Power(Times(e,x),m),Exp(Times(d,Sqr(Plus(a,Times(b,Log(Times(c,Power(x,n))))))))),x)),x)),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(6187,Integrate(Power(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),x_Symbol),
    Condition(Subtract(Simp(Times(x,Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),n)),x),Simp(Star(Times(b,c,n),Integrate(Times(x,Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),Subtract(n,C1)),Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x)),x)),And(FreeQ(list(a,b,c),x),GtQ(n,C0)))),
IIntegrate(6188,Integrate(Power(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),x_Symbol),
    Condition(Subtract(Simp(Times(Sqrt(Plus(C1,Times(Sqr(c),Sqr(x)))),Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),Plus(n,C1)),Power(Times(b,c,Plus(n,C1)),CN1)),x),Simp(Star(Times(c,Power(Times(b,Plus(n,C1)),CN1)),Integrate(Times(x,Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),Plus(n,C1)),Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x)),x)),And(FreeQ(list(a,b,c),x),LtQ(n,CN1)))),
IIntegrate(6189,Integrate(Power(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),x_Symbol),
    Condition(Simp(Star(Power(Times(b,c),CN1),Subst(Integrate(Times(Power(x,n),Cosh(Plus(Times(CN1,a,Power(b,CN1)),Times(x,Power(b,CN1))))),x),x,Plus(a,Times(b,ArcSinh(Times(c,x)))))),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(6190,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(x_,CN1)),x_Symbol),
    Condition(Simp(Star(Power(b,CN1),Subst(Integrate(Times(Power(x,n),Coth(Plus(Times(CN1,a,Power(b,CN1)),Times(x,Power(b,CN1))))),x),x,Plus(a,Times(b,ArcSinh(Times(c,x)))))),x),And(FreeQ(list(a,b,c),x),IGtQ(n,C0)))),
IIntegrate(6191,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),Plus(m,C1)),Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),n),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,c,n,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(d,x),Plus(m,C1)),Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),Subtract(n,C1)),Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x)),x)),And(FreeQ(List(a,b,c,d,m),x),IGtQ(n,C0),NeQ(m,CN1)))),
IIntegrate(6192,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),n),Power(Plus(m,C1),CN1)),x),Simp(Star(Times(b,c,n,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),Subtract(n,C1)),Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x)),x)),And(FreeQ(list(a,b,c),x),IGtQ(m,C0),GtQ(n,C0)))),
IIntegrate(6193,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,m),Sqrt(Plus(C1,Times(Sqr(c),Sqr(x)))),Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),Plus(n,C1)),Power(Times(b,c,Plus(n,C1)),CN1)),x),Simp(Star(Power(Times(Sqr(b),Power(c,Plus(m,C1)),Plus(n,C1)),CN1),Subst(Integrate(ExpandTrigReduce(Power(x,Plus(n,C1)),Times(Power(Sinh(Plus(Times(CN1,a,Power(b,CN1)),Times(x,Power(b,CN1)))),Subtract(m,C1)),Plus(m,Times(Plus(m,C1),Sqr(Sinh(Plus(Times(CN1,a,Power(b,CN1)),Times(x,Power(b,CN1)))))))),x),x),x,Plus(a,Times(b,ArcSinh(Times(c,x)))))),x)),And(FreeQ(list(a,b,c),x),IGtQ(m,C0),GeQ(n,CN2),LtQ(n,CN1)))),
IIntegrate(6194,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,m),Sqrt(Plus(C1,Times(Sqr(c),Sqr(x)))),Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),Plus(n,C1)),Power(Times(b,c,Plus(n,C1)),CN1)),x),Negate(Simp(Star(Times(c,Plus(m,C1),Power(Times(b,Plus(n,C1)),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),Plus(n,C1)),Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x)),x)),Negate(Simp(Star(Times(m,Power(Times(b,c,Plus(n,C1)),CN1)),Integrate(Times(Power(x,Subtract(m,C1)),Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),Plus(n,C1)),Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x)),x))),And(FreeQ(list(a,b,c),x),IGtQ(m,C0),LtQ(n,CN2)))),
IIntegrate(6195,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Times(b,Power(c,Plus(m,C1))),CN1),Subst(Integrate(Times(Power(x,n),Power(Sinh(Plus(Times(CN1,a,Power(b,CN1)),Times(x,Power(b,CN1)))),m),Cosh(Plus(Times(CN1,a,Power(b,CN1)),Times(x,Power(b,CN1))))),x),x,Plus(a,Times(b,ArcSinh(Times(c,x)))))),x),And(FreeQ(List(a,b,c,n),x),IGtQ(m,C0)))),
IIntegrate(6196,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(d,x),m),Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),n)),x),FreeQ(List(a,b,c,d,m,n),x))),
IIntegrate(6197,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),CN1),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Times(Power(Times(b,c),CN1),Simp(Times(Sqrt(Plus(C1,Times(Sqr(c),Sqr(x)))),Power(Plus(d,Times(e,Sqr(x))),CN1D2))),Log(Plus(a,Times(b,ArcSinh(Times(c,x)))))),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d))))),
IIntegrate(6198,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Times(Power(Times(b,c,Plus(n,C1)),CN1),Simp(Times(Sqrt(Plus(C1,Times(Sqr(c),Sqr(x)))),Power(Plus(d,Times(e,Sqr(x))),CN1D2))),Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),Plus(n,C1))),x),And(FreeQ(List(a,b,c,d,e,n),x),EqQ(e,Times(Sqr(c),d)),NeQ(n,CN1)))),
IIntegrate(6199,Integrate(Times(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Power(Plus(d,Times(e,Sqr(x))),p),x))),Subtract(Simp(Star(Plus(a,Times(b,ArcSinh(Times(c,x)))),u),x),Simp(Star(Times(b,c),Integrate(SimplifyIntegrand(Times(u,Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x)),x))),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),IGtQ(p,C0)))),
IIntegrate(6200,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Sqrt(Plus(d_,Times(e_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Simp(Times(x,Sqrt(Plus(d,Times(e,Sqr(x)))),C1D2,Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),n)),x),Simp(Star(Times(C1D2,Simp(Times(Sqrt(Plus(d,Times(e,Sqr(x)))),Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1D2)))),Integrate(Times(Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),n),Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x)),x),Negate(Simp(Star(Times(b,c,C1D2,n,Simp(Times(Sqrt(Plus(d,Times(e,Sqr(x)))),Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1D2)))),Integrate(Times(x,Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),Subtract(n,C1))),x)),x))),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),GtQ(n,C0))))
  );
}
