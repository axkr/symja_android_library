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
class IntRules14 { 
  public static IAST RULES = List( 
IIntegrate(281,Integrate(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_))),q_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Times(b,Power(d,CN1)),p),Integrate(Times(u,Power(Plus(c,Times(d,Power(x,n))),Plus(p,q))),x)),x),And(FreeQ(List(a,b,c,d,n,p,q),x),EqQ(Subtract(Times(b,c),Times(a,d)),C0),IntegerQ(p),Not(And(IntegerQ(q),SimplerQ(Plus(a,Times(b,Power(x,n))),Plus(c,Times(d,Power(x,n))))))))),
IIntegrate(282,Integrate(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_))),q_)),x_Symbol),
    Condition(Simp(Star(Power(Times(b,Power(d,CN1)),p),Integrate(Times(u,Power(Plus(c,Times(d,Power(x,n))),Plus(p,q))),x)),x),And(FreeQ(List(a,b,c,d,n,p,q),x),EqQ(Subtract(Times(b,c),Times(a,d)),C0),GtQ(Times(b,Power(d,CN1)),C0),Not(SimplerQ(Plus(a,Times(b,Power(x,n))),Plus(c,Times(d,Power(x,n)))))))),
IIntegrate(283,Integrate(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_))),q_)),x_Symbol),
    Condition(Simp(Star(Times(Power(Plus(a,Times(b,Power(x,n))),p),Power(Power(Plus(c,Times(d,Power(x,n))),p),CN1)),Integrate(Times(u,Power(Plus(c,Times(d,Power(x,n))),Plus(p,q))),x)),x),And(FreeQ(List(a,b,c,d,n,p,q),x),EqQ(Subtract(Times(b,c),Times(a,d)),C0),Not(SimplerQ(Plus(a,Times(b,Power(x,n))),Plus(c,Times(d,Power(x,n)))))))),
IIntegrate(284,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Power(Plus(Times(a,c),Times(b,d,Power(x,C4))),p),x),And(FreeQ(List(a,b,c,d,p),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),Or(IntegerQ(p),And(GtQ(a,C0),GtQ(c,C0)))))),
IIntegrate(285,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(x,Power(Plus(a,Times(b,Sqr(x))),p),Power(Plus(c,Times(d,Sqr(x))),p),Power(Plus(Times(C4,p),C1),CN1)),x),Simp(Star(Times(C4,a,c,p,Power(Plus(Times(C4,p),C1),CN1)),Integrate(Times(Power(Plus(a,Times(b,Sqr(x))),Subtract(p,C1)),Power(Plus(c,Times(d,Sqr(x))),Subtract(p,C1))),x)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),GtQ(p,C0)))),
IIntegrate(286,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,x,Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1)),Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Power(Times(C4,a,c,Plus(p,C1)),CN1)),x),Simp(Star(Times(Plus(Times(C4,p),C5),Power(Times(C4,a,c,Plus(p,C1)),CN1)),Integrate(Times(Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1)),Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1))),x)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),LtQ(p,CN1)))),
IIntegrate(287,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D2),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Times(Power(Times(C2,a,d),CN1D2),EllipticF(ArcSin(Times(Sqrt(Times(C2,d)),x,Power(Plus(c,Times(d,Sqr(x))),CN1D2))),C1D2)),x),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),GtQ(a,C0),GtQ(d,C0)))),
IIntegrate(288,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(Plus(c,Times(d,Sqr(x))),FracPart(p)),Power(Times(Power(CN1,IntPart(p)),Power(Subtract(Negate(c),Times(d,Sqr(x))),FracPart(p))),CN1)),Integrate(Power(Subtract(Times(CN1,a,c),Times(b,d,Power(x,C4))),p),x)),x),And(FreeQ(List(a,b,c,d,p),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),GtQ(a,C0),LtQ(c,C0)))),
IIntegrate(289,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(Plus(a,Times(b,Sqr(x))),FracPart(p)),Power(Plus(c,Times(d,Sqr(x))),FracPart(p)),Power(Power(Plus(Times(a,c),Times(b,d,Power(x,C4))),FracPart(p)),CN1)),Integrate(Power(Plus(Times(a,c),Times(b,d,Power(x,C4))),p),x)),x),And(FreeQ(List(a,b,c,d,p),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),Not(IntegerQ(p))))),
IIntegrate(290,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times(Power(Plus(a,Times(b,Sqr(x))),p),Power(Plus(c,Times(d,Sqr(x))),q)),x),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),IGtQ(p,C0),IGtQ(q,C0)))),
IIntegrate(291,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D2),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Subst(Integrate(Power(Subtract(c,Times(Subtract(Times(b,c),Times(a,d)),Sqr(x))),CN1),x),x,Times(x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0)))),
IIntegrate(292,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(CN1,x,Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1)),Power(Plus(c,Times(d,Sqr(x))),q),Power(Times(C2,a,Plus(p,C1)),CN1)),x),Simp(Star(Times(c,q,Power(Times(a,Plus(p,C1)),CN1)),Integrate(Times(Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1)),Power(Plus(c,Times(d,Sqr(x))),Subtract(q,C1))),x)),x)),And(FreeQ(List(a,b,c,d,p),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),EqQ(Plus(Times(C2,Plus(p,q,C1)),C1),C0),GtQ(q,C0),NeQ(p,CN1)))),
IIntegrate(293,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Simp(Times(Power(a,p),x,Power(Times(Power(c,Plus(p,C1)),Sqrt(Plus(c,Times(d,Sqr(x))))),CN1),Hypergeometric2F1(C1D2,Negate(p),QQ(3L,2L),Times(CN1,Subtract(Times(b,c),Times(a,d)),Sqr(x),Power(Times(a,Plus(c,Times(d,Sqr(x)))),CN1)))),x),And(FreeQ(List(a,b,c,d,q),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),EqQ(Plus(Times(C2,Plus(p,q,C1)),C1),C0),ILtQ(p,C0)))),
IIntegrate(294,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Simp(Times(x,Power(Plus(a,Times(b,Sqr(x))),p),Power(Times(c,Power(Times(c,Plus(a,Times(b,Sqr(x))),Power(Times(a,Plus(c,Times(d,Sqr(x)))),CN1)),p),Power(Plus(c,Times(d,Sqr(x))),Plus(C1D2,p))),CN1),Hypergeometric2F1(C1D2,Negate(p),QQ(3L,2L),Times(CN1,Subtract(Times(b,c),Times(a,d)),Sqr(x),Power(Times(a,Plus(c,Times(d,Sqr(x)))),CN1)))),x),And(FreeQ(List(a,b,c,d,p,q),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),EqQ(Plus(Times(C2,Plus(p,q,C1)),C1),C0)))),
IIntegrate(295,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Simp(Times(x,Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1)),Power(Plus(c,Times(d,Sqr(x))),Plus(q,C1)),Power(Times(a,c),CN1)),x),And(FreeQ(List(a,b,c,d,p,q),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),EqQ(Plus(Times(C2,Plus(p,q,C2)),C1),C0),EqQ(Plus(Times(a,d,Plus(p,C1)),Times(b,c,Plus(q,C1))),C0)))),
IIntegrate(296,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,b,x,Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1)),Power(Plus(c,Times(d,Sqr(x))),Plus(q,C1)),Power(Times(C2,a,Plus(p,C1),Subtract(Times(b,c),Times(a,d))),CN1)),x),Simp(Star(Times(Plus(Times(b,c),Times(C2,Plus(p,C1),Subtract(Times(b,c),Times(a,d)))),Power(Times(C2,a,Plus(p,C1),Subtract(Times(b,c),Times(a,d))),CN1)),Integrate(Times(Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1)),Power(Plus(c,Times(d,Sqr(x))),q)),x)),x)),And(FreeQ(List(a,b,c,d,q),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),EqQ(Plus(Times(C2,Plus(p,q,C2)),C1),C0),Or(LtQ(p,CN1),Not(LtQ(q,CN1))),NeQ(p,CN1)))),
IIntegrate(297,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_DEFAULT),Plus(c_,Times(d_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Simp(Times(c,x,Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1)),Power(a,CN1)),x),And(FreeQ(List(a,b,c,d,p),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),EqQ(Subtract(Times(a,d),Times(b,c,Plus(Times(C2,p),C3))),C0)))),
IIntegrate(298,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_),Plus(c_,Times(d_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(CN1,Subtract(Times(b,c),Times(a,d)),x,Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1)),Power(Times(C2,a,b,Plus(p,C1)),CN1)),x),Simp(Star(Times(Subtract(Times(a,d),Times(b,c,Plus(Times(C2,p),C3))),Power(Times(C2,a,b,Plus(p,C1)),CN1)),Integrate(Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1)),x)),x)),And(FreeQ(List(a,b,c,d,p),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),Or(LtQ(p,CN1),ILtQ(Plus(C1D2,p),C0))))),
IIntegrate(299,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_),Plus(c_,Times(d_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(d,x,Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1)),Power(Times(b,Plus(Times(C2,p),C3)),CN1)),x),Simp(Star(Times(Subtract(Times(a,d),Times(b,c,Plus(Times(C2,p),C3))),Power(Times(b,Plus(Times(C2,p),C3)),CN1)),Integrate(Power(Plus(a,Times(b,Sqr(x))),p),x)),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),NeQ(Plus(Times(C2,p),C3),C0)))),
IIntegrate(300,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Integrate(PolynomialDivide(Power(Plus(a,Times(b,Sqr(x))),p),Power(Plus(c,Times(d,Sqr(x))),Negate(q)),x),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),IGtQ(p,C0),ILtQ(q,C0),GeQ(p,Negate(q)))))
  );
}
