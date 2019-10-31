package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class IntRules106 { 
  public static IAST RULES = List( 
IIntegrate(2651,Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Negate(Simp(Times(Power(C2,Plus(n,C1D2)),Power(a,Subtract(n,C1D2)),b,Cos(Plus(c,Times(d,x))),Hypergeometric2F1(C1D2,Subtract(C1D2,n),QQ(3L,2L),Times(C1D2,C1,Subtract(C1,Times(b,Sin(Plus(c,Times(d,x))),Power(a,CN1))))),Power(Times(d,Sqrt(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))))),CN1)),x)),And(FreeQ(List(a,b,c,d,n),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0),Not(IntegerQ(Times(C2,n))),GtQ(a,C0)))),
IIntegrate(2652,Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Dist(Times(Power(a,IntPart(n)),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),FracPart(n)),Power(Power(Plus(C1,Times(b,Sin(Plus(c,Times(d,x))),Power(a,CN1))),FracPart(n)),CN1)),Int(Power(Plus(C1,Times(b,Sin(Plus(c,Times(d,x))),Power(a,CN1))),n),x),x),And(FreeQ(List(a,b,c,d,n),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0),Not(IntegerQ(Times(C2,n))),Not(GtQ(a,C0))))),
IIntegrate(2653,Int(Sqrt(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Simp(Times(C2,Sqrt(Plus(a,b)),EllipticE(Times(C1D2,C1,Plus(c,Times(CN1,C1D2,Pi),Times(d,x))),Times(C2,b,Power(Plus(a,b),CN1))),Power(d,CN1)),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0),GtQ(Plus(a,b),C0)))),
IIntegrate(2654,Int(Sqrt(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Simp(Times(C2,Sqrt(Subtract(a,b)),EllipticE(Times(C1D2,C1,Plus(c,CPiHalf,Times(d,x))),Times(CN2,b,Power(Subtract(a,b),CN1))),Power(d,CN1)),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0),GtQ(Subtract(a,b),C0)))),
IIntegrate(2655,Int(Sqrt(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Dist(Times(Sqrt(Plus(a,Times(b,Sin(Plus(c,Times(d,x)))))),Power(Times(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Power(Plus(a,b),CN1)),CN1D2)),Int(Sqrt(Plus(Times(a,Power(Plus(a,b),CN1)),Times(b,Sin(Plus(c,Times(d,x))),Power(Plus(a,b),CN1)))),x),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0),Not(GtQ(Plus(a,b),C0))))),
IIntegrate(2656,Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Negate(Simp(Times(b,Cos(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Subtract(n,C1)),Power(Times(d,n),CN1)),x)),Dist(Power(n,CN1),Int(Times(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Subtract(n,C2)),Simp(Plus(Times(Sqr(a),n),Times(Sqr(b),Subtract(n,C1)),Times(a,b,Subtract(Times(C2,n),C1),Sin(Plus(c,Times(d,x))))),x)),x),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0),GtQ(n,C1),IntegerQ(Times(C2,n))))),
IIntegrate(2657,Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1),x_Symbol),
    Condition(With(List(Set(q,Rt(Subtract(Sqr(a),Sqr(b)),C2))),Plus(Simp(Times(x,Power(q,CN1)),x),Simp(Times(C2,ArcTan(Times(b,Cos(Plus(c,Times(d,x))),Power(Plus(a,q,Times(b,Sin(Plus(c,Times(d,x))))),CN1))),Power(Times(d,q),CN1)),x))),And(FreeQ(List(a,b,c,d),x),GtQ(Subtract(Sqr(a),Sqr(b)),C0),PosQ(a)))),
IIntegrate(2658,Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1),x_Symbol),
    Condition(With(List(Set(q,Rt(Subtract(Sqr(a),Sqr(b)),C2))),Subtract(Negate(Simp(Times(x,Power(q,CN1)),x)),Simp(Times(C2,ArcTan(Times(b,Cos(Plus(c,Times(d,x))),Power(Plus(a,Negate(q),Times(b,Sin(Plus(c,Times(d,x))))),CN1))),Power(Times(d,q),CN1)),x))),And(FreeQ(List(a,b,c,d),x),GtQ(Subtract(Sqr(a),Sqr(b)),C0),NegQ(a)))),
IIntegrate(2659,Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(CPiHalf,c_DEFAULT,Times(d_DEFAULT,x_))))),CN1),x_Symbol),
    Condition(With(List(Set(e,FreeFactors(Tan(Times(C1D2,Plus(c,Times(d,x)))),x))),Dist(Times(C2,e,Power(d,CN1)),Subst(Int(Power(Plus(a,b,Times(Subtract(a,b),Sqr(e),Sqr(x))),CN1),x),x,Times(Tan(Times(C1D2,Plus(c,Times(d,x)))),Power(e,CN1))),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0)))),
IIntegrate(2660,Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1),x_Symbol),
    Condition(With(List(Set(e,FreeFactors(Tan(Times(C1D2,Plus(c,Times(d,x)))),x))),Dist(Times(C2,e,Power(d,CN1)),Subst(Int(Power(Plus(a,Times(C2,b,e,x),Times(a,Sqr(e),Sqr(x))),CN1),x),x,Times(Tan(Times(C1D2,Plus(c,Times(d,x)))),Power(e,CN1))),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0)))),
IIntegrate(2661,Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1D2),x_Symbol),
    Condition(Simp(Times(C2,EllipticF(Times(C1D2,C1,Plus(c,Times(CN1,C1D2,Pi),Times(d,x))),Times(C2,b,Power(Plus(a,b),CN1))),Power(Times(d,Sqrt(Plus(a,b))),CN1)),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0),GtQ(Plus(a,b),C0)))),
IIntegrate(2662,Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1D2),x_Symbol),
    Condition(Simp(Times(C2,EllipticF(Times(C1D2,C1,Plus(c,CPiHalf,Times(d,x))),Times(CN2,b,Power(Subtract(a,b),CN1))),Power(Times(d,Sqrt(Subtract(a,b))),CN1)),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0),GtQ(Subtract(a,b),C0)))),
IIntegrate(2663,Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1D2),x_Symbol),
    Condition(Dist(Times(Sqrt(Times(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Power(Plus(a,b),CN1))),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),CN1D2)),Int(Power(Plus(Times(a,Power(Plus(a,b),CN1)),Times(b,Sin(Plus(c,Times(d,x))),Power(Plus(a,b),CN1))),CN1D2),x),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0),Not(GtQ(Plus(a,b),C0))))),
IIntegrate(2664,Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Negate(Simp(Times(b,Cos(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Plus(n,C1)),Power(Times(d,Plus(n,C1),Subtract(Sqr(a),Sqr(b))),CN1)),x)),Dist(Power(Times(Plus(n,C1),Subtract(Sqr(a),Sqr(b))),CN1),Int(Times(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Plus(n,C1)),Simp(Subtract(Times(a,Plus(n,C1)),Times(b,Plus(n,C2),Sin(Plus(c,Times(d,x))))),x)),x),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0),LtQ(n,CN1),IntegerQ(Times(C2,n))))),
IIntegrate(2665,Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Dist(Times(Cos(Plus(c,Times(d,x))),Power(Times(d,Sqrt(Plus(C1,Sin(Plus(c,Times(d,x))))),Sqrt(Subtract(C1,Sin(Plus(c,Times(d,x)))))),CN1)),Subst(Int(Times(Power(Plus(a,Times(b,x)),n),Power(Times(Sqrt(Plus(C1,x)),Sqrt(Subtract(C1,x))),CN1)),x),x,Sin(Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d,n),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0),Not(IntegerQ(Times(C2,n)))))),
IIntegrate(2666,Int(Power(Plus(a_,Times($($s("§cos"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Int(Power(Plus(a,Times(C1D2,b,Sin(Plus(Times(C2,c),Times(C2,d,x))))),n),x),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(2667,Int(Times(Power($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),p_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Times(Power(b,p),f),CN1),Subst(Int(Times(Power(Plus(a,x),Plus(m,Times(C1D2,Subtract(p,C1)))),Power(Subtract(a,x),Times(C1D2,Subtract(p,C1)))),x),x,Times(b,Sin(Plus(e,Times(f,x))))),x),And(FreeQ(List(a,b,e,f,m),x),IntegerQ(Times(C1D2,Subtract(p,C1))),EqQ(Subtract(Sqr(a),Sqr(b)),C0),Or(GeQ(p,CN1),Not(IntegerQ(Plus(m,C1D2))))))),
IIntegrate(2668,Int(Times(Power($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),p_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Times(Power(b,p),f),CN1),Subst(Int(Times(Power(Plus(a,x),m),Power(Subtract(Sqr(b),Sqr(x)),Times(C1D2,Subtract(p,C1)))),x),x,Times(b,Sin(Plus(e,Times(f,x))))),x),And(FreeQ(List(a,b,e,f,m),x),IntegerQ(Times(C1D2,Subtract(p,C1))),NeQ(Subtract(Sqr(a),Sqr(b)),C0)))),
IIntegrate(2669,Int(Times(Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),g_DEFAULT),p_),Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Negate(Simp(Times(b,Power(Times(g,Cos(Plus(e,Times(f,x)))),Plus(p,C1)),Power(Times(f,g,Plus(p,C1)),CN1)),x)),Dist(a,Int(Power(Times(g,Cos(Plus(e,Times(f,x)))),p),x),x)),And(FreeQ(List(a,b,e,f,g,p),x),Or(IntegerQ(Times(C2,p)),NeQ(Subtract(Sqr(a),Sqr(b)),C0))))),
IIntegrate(2670,Int(Times(Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),g_DEFAULT),p_),Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),m_)),x_Symbol),
    Condition(Dist(Power(Times(a,Power(g,CN1)),Times(C2,m)),Int(Times(Power(Times(g,Cos(Plus(e,Times(f,x)))),Plus(Times(C2,m),p)),Power(Power(Subtract(a,Times(b,Sin(Plus(e,Times(f,x))))),m),CN1)),x),x),And(FreeQ(List(a,b,e,f,g),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0),IntegerQ(m),LtQ(p,CN1),GeQ(Plus(Times(C2,m),p),C0)))),
IIntegrate(2671,Int(Times(Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),g_DEFAULT),p_),Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),m_)),x_Symbol),
    Condition(Simp(Times(b,Power(Times(g,Cos(Plus(e,Times(f,x)))),Plus(p,C1)),Power(Plus(a,Times(b,Sin(Plus(e,Times(f,x))))),m),Power(Times(a,f,g,m),CN1)),x),And(FreeQ(List(a,b,e,f,g,m,p),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0),EqQ(Simplify(Plus(m,p,C1)),C0),Not(ILtQ(p,C0))))),
IIntegrate(2672,Int(Times(Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),g_DEFAULT),p_),Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),m_)),x_Symbol),
    Condition(Plus(Simp(Times(b,Power(Times(g,Cos(Plus(e,Times(f,x)))),Plus(p,C1)),Power(Plus(a,Times(b,Sin(Plus(e,Times(f,x))))),m),Power(Times(a,f,g,Simplify(Plus(Times(C2,m),p,C1))),CN1)),x),Dist(Times(Simplify(Plus(m,p,C1)),Power(Times(a,Simplify(Plus(Times(C2,m),p,C1))),CN1)),Int(Times(Power(Times(g,Cos(Plus(e,Times(f,x)))),p),Power(Plus(a,Times(b,Sin(Plus(e,Times(f,x))))),Plus(m,C1))),x),x)),And(FreeQ(List(a,b,e,f,g,m,p),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0),ILtQ(Simplify(Plus(m,p,C1)),C0),NeQ(Plus(Times(C2,m),p,C1),C0),Not(IGtQ(m,C0))))),
IIntegrate(2673,Int(Times(Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),g_DEFAULT),p_),Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),m_)),x_Symbol),
    Condition(Simp(Times(b,Power(Times(g,Cos(Plus(e,Times(f,x)))),Plus(p,C1)),Power(Plus(a,Times(b,Sin(Plus(e,Times(f,x))))),Subtract(m,C1)),Power(Times(f,g,Subtract(m,C1)),CN1)),x),And(FreeQ(List(a,b,e,f,g,m,p),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0),EqQ(Subtract(Plus(Times(C2,m),p),C1),C0),NeQ(m,C1)))),
IIntegrate(2674,Int(Times(Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),g_DEFAULT),p_),Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),m_)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(b,Power(Times(g,Cos(Plus(e,Times(f,x)))),Plus(p,C1)),Power(Plus(a,Times(b,Sin(Plus(e,Times(f,x))))),Subtract(m,C1)),Power(Times(f,g,Plus(m,p)),CN1)),x)),Dist(Times(a,Subtract(Plus(Times(C2,m),p),C1),Power(Plus(m,p),CN1)),Int(Times(Power(Times(g,Cos(Plus(e,Times(f,x)))),p),Power(Plus(a,Times(b,Sin(Plus(e,Times(f,x))))),Subtract(m,C1))),x),x)),And(FreeQ(List(a,b,e,f,g,m,p),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0),IGtQ(Simplify(Times(C1D2,Subtract(Plus(Times(C2,m),p),C1))),C0),NeQ(Plus(m,p),C0)))),
IIntegrate(2675,Int(Times(Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),g_DEFAULT),p_),Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),m_)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(b,Power(Times(g,Cos(Plus(e,Times(f,x)))),Plus(p,C1)),Power(Plus(a,Times(b,Sin(Plus(e,Times(f,x))))),m),Power(Times(a,f,g,Plus(p,C1)),CN1)),x)),Dist(Times(a,Plus(m,p,C1),Power(Times(Sqr(g),Plus(p,C1)),CN1)),Int(Times(Power(Times(g,Cos(Plus(e,Times(f,x)))),Plus(p,C2)),Power(Plus(a,Times(b,Sin(Plus(e,Times(f,x))))),Subtract(m,C1))),x),x)),And(FreeQ(List(a,b,e,f,g),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0),GtQ(m,C0),LeQ(p,Times(CN2,m)),IntegersQ(Plus(m,C1D2),Times(C2,p)))))
  );
}
