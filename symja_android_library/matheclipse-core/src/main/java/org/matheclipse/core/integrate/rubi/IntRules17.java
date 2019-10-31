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
public class IntRules17 { 
  public static IAST RULES = List( 
IIntegrate(426,Int(Times(Sqrt(Plus(a_,Times(b_DEFAULT,Sqr(x_)))),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Dist(Times(Sqrt(Plus(a,Times(b,Sqr(x)))),Power(Plus(C1,Times(b,Sqr(x),Power(a,CN1))),CN1D2)),Int(Times(Sqrt(Plus(C1,Times(b,Sqr(x),Power(a,CN1)))),Power(Plus(c,Times(d,Sqr(x))),CN1D2)),x),x),And(FreeQ(List(a,b,c,d),x),NegQ(Times(d,Power(c,CN1))),GtQ(c,C0),Not(GtQ(a,C0))))),
IIntegrate(427,Int(Times(Sqrt(Plus(a_,Times(b_DEFAULT,Sqr(x_)))),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Dist(Times(Sqrt(Plus(C1,Times(d,Sqr(x),Power(c,CN1)))),Power(Plus(c,Times(d,Sqr(x))),CN1D2)),Int(Times(Sqrt(Plus(a,Times(b,Sqr(x)))),Power(Plus(C1,Times(d,Sqr(x),Power(c,CN1))),CN1D2)),x),x),And(FreeQ(List(a,b,c,d),x),NegQ(Times(d,Power(c,CN1))),Not(GtQ(c,C0))))),
IIntegrate(428,Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_))),q_)),x_Symbol),
    Condition(Int(ExpandIntegrand(Times(Power(Plus(a,Times(b,Power(x,n))),p),Power(Plus(c,Times(d,Power(x,n))),q)),x),x),And(FreeQ(List(a,b,c,d,n,q),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),IGtQ(p,C0)))),
IIntegrate(429,Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_))),q_)),x_Symbol),
    Condition(Simp(Times(Power(a,p),Power(c,q),x,AppellF1(Power(n,CN1),Negate(p),Negate(q),Plus(C1,Power(n,CN1)),Times(CN1,b,Power(x,n),Power(a,CN1)),Times(CN1,d,Power(x,n),Power(c,CN1)))),x),And(FreeQ(List(a,b,c,d,n,p,q),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),NeQ(n,CN1),Or(IntegerQ(p),GtQ(a,C0)),Or(IntegerQ(q),GtQ(c,C0))))),
IIntegrate(430,Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_))),q_)),x_Symbol),
    Condition(Dist(Times(Power(a,IntPart(p)),Power(Plus(a,Times(b,Power(x,n))),FracPart(p)),Power(Power(Plus(C1,Times(b,Power(x,n),Power(a,CN1))),FracPart(p)),CN1)),Int(Times(Power(Plus(C1,Times(b,Power(x,n),Power(a,CN1))),p),Power(Plus(c,Times(d,Power(x,n))),q)),x),x),And(FreeQ(List(a,b,c,d,n,p,q),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),NeQ(n,CN1),Not(Or(IntegerQ(p),GtQ(a,C0)))))),
IIntegrate(431,Int(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(u_,n_))),p_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_))),q_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Coefficient(u,x,C1),CN1),Subst(Int(Times(Power(Plus(a,Times(b,Power(x,n))),p),Power(Plus(c,Times(d,Power(x,n))),q)),x),x,u),x),And(FreeQ(List(a,b,c,d,n,p,q),x),LinearQ(u,x),NeQ(u,x)))),
IIntegrate(432,Int(Times(Power(u_,p_DEFAULT),Power(v_,q_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(NormalizePseudoBinomial(u,x),p),Power(NormalizePseudoBinomial(v,x),q)),x),And(FreeQ(List(p,q),x),PseudoBinomialPairQ(u,v,x)))),
IIntegrate(433,Int(Times(Power(u_,p_DEFAULT),Power(v_,q_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(NormalizePseudoBinomial(Times(Power(x,Times(m,Power(p,CN1))),u),x),p),Power(NormalizePseudoBinomial(v,x),q)),x),And(FreeQ(List(p,q),x),IntegersQ(p,Times(m,Power(p,CN1))),PseudoBinomialPairQ(Times(Power(x,Times(m,Power(p,CN1))),u),v,x)))),
IIntegrate(434,Int(Times(Power(Plus(c_,Times(d_DEFAULT,Power(x_,$p("mn",true)))),q_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Plus(a,Times(b,Power(x,n))),p),Power(Plus(d,Times(c,Power(x,n))),q),Power(Power(x,Times(n,q)),CN1)),x),And(FreeQ(List(a,b,c,d,n,p),x),EqQ($s("mn"),Negate(n)),IntegerQ(q),Or(PosQ(n),Not(IntegerQ(p)))))),
IIntegrate(435,Int(Times(Power(Plus(c_,Times(d_DEFAULT,Power(x_,$p("mn",true)))),q_),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_DEFAULT))),p_)),x_Symbol),
    Condition(Dist(Times(Power(x,Times(n,FracPart(q))),Power(Plus(c,Times(d,Power(Power(x,n),CN1))),FracPart(q)),Power(Power(Plus(d,Times(c,Power(x,n))),FracPart(q)),CN1)),Int(Times(Power(Plus(a,Times(b,Power(x,n))),p),Power(Plus(d,Times(c,Power(x,n))),q),Power(Power(x,Times(n,q)),CN1)),x),x),And(FreeQ(List(a,b,c,d,n,p,q),x),EqQ($s("mn"),Negate(n)),Not(IntegerQ(q)),Not(IntegerQ(p))))),
IIntegrate(436,Int(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Times(b_DEFAULT,Power(x_,n_)),p_),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_))),q_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(e,m),Power(Times(n,Power(b,Subtract(Simplify(Times(Plus(m,C1),Power(n,CN1))),C1))),CN1)),Subst(Int(Times(Power(Times(b,x),Subtract(Plus(p,Simplify(Times(Plus(m,C1),Power(n,CN1)))),C1)),Power(Plus(c,Times(d,x)),q)),x),x,Power(x,n)),x),And(FreeQ(List(b,c,d,e,m,n,p,q),x),Or(IntegerQ(m),GtQ(e,C0)),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1))))))),
IIntegrate(437,Int(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Times(b_DEFAULT,Power(x_,n_DEFAULT)),p_),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_))),q_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(e,m),Power(b,IntPart(p)),Power(Times(b,Power(x,n)),FracPart(p)),Power(Power(x,Times(n,FracPart(p))),CN1)),Int(Times(Power(x,Plus(m,Times(n,p))),Power(Plus(c,Times(d,Power(x,n))),q)),x),x),And(FreeQ(List(b,c,d,e,m,n,p,q),x),Or(IntegerQ(m),GtQ(e,C0)),Not(IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1)))))))),
IIntegrate(438,Int(Times(Power(Times(e_,x_),m_),Power(Times(b_DEFAULT,Power(x_,n_DEFAULT)),p_),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_))),q_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Int(Times(Power(x,m),Power(Times(b,Power(x,n)),p),Power(Plus(c,Times(d,Power(x,n))),q)),x),x),And(FreeQ(List(b,c,d,e,m,n,p,q),x),Not(IntegerQ(m))))),
IIntegrate(439,Int(Times(x_,Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D4),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Subtract(Negate(Simp(Times(ArcTan(Times(Subtract(Sqr(Rt(a,C4)),Sqrt(Plus(a,Times(b,Sqr(x))))),Power(Times(CSqrt2,Rt(a,C4),Power(Plus(a,Times(b,Sqr(x))),C1D4)),CN1))),Power(Times(CSqrt2,Rt(a,C4),d),CN1)),x)),Simp(Times(C1,ArcTanh(Times(Plus(Sqr(Rt(a,C4)),Sqrt(Plus(a,Times(b,Sqr(x))))),Power(Times(CSqrt2,Rt(a,C4),Power(Plus(a,Times(b,Sqr(x))),C1D4)),CN1))),Power(Times(CSqrt2,Rt(a,C4),d),CN1)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Subtract(Times(b,c),Times(C2,a,d)),C0),PosQ(a)))),
IIntegrate(440,Int(Times(Power(x_,m_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D4),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Int(ExpandIntegrand(Times(Power(x,m),Power(Times(Power(Plus(a,Times(b,Sqr(x))),C1D4),Plus(c,Times(d,Sqr(x)))),CN1)),x),x),And(FreeQ(List(a,b,c,d),x),EqQ(Subtract(Times(b,c),Times(C2,a,d)),C0),IntegerQ(m),Or(PosQ(a),IntegerQ(Times(C1D2,m)))))),
IIntegrate(441,Int(Times(Sqr(x_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),QQ(-3L,4L)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(b,ArcTan(Times(Plus(b,Times(Sqr(Rt(Times(Sqr(b),Power(a,CN1)),C4)),Sqrt(Plus(a,Times(b,Sqr(x)))))),Power(Times(Power(Rt(Times(Sqr(b),Power(a,CN1)),C4),C3),x,Power(Plus(a,Times(b,Sqr(x))),C1D4)),CN1))),Power(Times(a,d,Power(Rt(Times(Sqr(b),Power(a,CN1)),C4),C3)),CN1)),x)),Simp(Times(b,ArcTanh(Times(Subtract(b,Times(Sqr(Rt(Times(Sqr(b),Power(a,CN1)),C4)),Sqrt(Plus(a,Times(b,Sqr(x)))))),Power(Times(Power(Rt(Times(Sqr(b),Power(a,CN1)),C4),C3),x,Power(Plus(a,Times(b,Sqr(x))),C1D4)),CN1))),Power(Times(a,d,Power(Rt(Times(Sqr(b),Power(a,CN1)),C4),C3)),CN1)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Subtract(Times(b,c),Times(C2,a,d)),C0),PosQ(Times(Sqr(b),Power(a,CN1)))))),
IIntegrate(442,Int(Times(Sqr(x_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),QQ(-3L,4L)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(b,ArcTan(Times(Rt(Times(CN1,Sqr(b),Power(a,CN1)),C4),x,Power(Times(CSqrt2,Power(Plus(a,Times(b,Sqr(x))),C1D4)),CN1))),Power(Times(CSqrt2,a,d,Power(Rt(Times(CN1,Sqr(b),Power(a,CN1)),C4),C3)),CN1)),x)),Simp(Times(b,ArcTanh(Times(Rt(Times(CN1,Sqr(b),Power(a,CN1)),C4),x,Power(Times(CSqrt2,Power(Plus(a,Times(b,Sqr(x))),C1D4)),CN1))),Power(Times(CSqrt2,a,d,Power(Rt(Times(CN1,Sqr(b),Power(a,CN1)),C4),C3)),CN1)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Subtract(Times(b,c),Times(C2,a,d)),C0),NegQ(Times(Sqr(b),Power(a,CN1)))))),
IIntegrate(443,Int(Times(Power(x_,m_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),QQ(-3L,4L)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Int(ExpandIntegrand(Times(Power(x,m),Power(Times(Power(Plus(a,Times(b,Sqr(x))),QQ(3L,4L)),Plus(c,Times(d,Sqr(x)))),CN1)),x),x),And(FreeQ(List(a,b,c,d),x),EqQ(Subtract(Times(b,c),Times(C2,a,d)),C0),IntegerQ(m),Or(PosQ(a),IntegerQ(Times(C1D2,m)))))),
IIntegrate(444,Int(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_))),q_DEFAULT)),x_Symbol),
    Condition(Dist(Power(n,CN1),Subst(Int(Times(Power(Plus(a,Times(b,x)),p),Power(Plus(c,Times(d,x)),q)),x),x,Power(x,n)),x),And(FreeQ(List(a,b,c,d,m,n,p,q),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),EqQ(Plus(m,Negate(n),C1),C0)))),
IIntegrate(445,Int(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_))),q_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(x,Plus(m,Times(n,Plus(p,q)))),Power(Plus(b,Times(a,Power(Power(x,n),CN1))),p),Power(Plus(d,Times(c,Power(Power(x,n),CN1))),q)),x),And(FreeQ(List(a,b,c,d,m,n),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),IntegersQ(p,q),NegQ(n)))),
IIntegrate(446,Int(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_))),q_DEFAULT)),x_Symbol),
    Condition(Dist(Power(n,CN1),Subst(Int(Times(Power(x,Subtract(Simplify(Times(Plus(m,C1),Power(n,CN1))),C1)),Power(Plus(a,Times(b,x)),p),Power(Plus(c,Times(d,x)),q)),x),x,Power(x,n)),x),And(FreeQ(List(a,b,c,d,m,n,p,q),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1))))))),
IIntegrate(447,Int(Times(Power(Times(e_,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_))),q_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Int(Times(Power(x,m),Power(Plus(a,Times(b,Power(x,n))),p),Power(Plus(c,Times(d,Power(x,n))),q)),x),x),And(FreeQ(List(a,b,c,d,e,m,n,p,q),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1))))))),
IIntegrate(448,Int(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_))),q_DEFAULT)),x_Symbol),
    Condition(Int(ExpandIntegrand(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Power(x,n))),p),Power(Plus(c,Times(d,Power(x,n))),q)),x),x),And(FreeQ(List(a,b,c,d,e,m,n),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),IGtQ(p,C0),IGtQ(q,C0)))),
IIntegrate(449,Int(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT),Plus(c_,Times(d_DEFAULT,Power(x_,n_)))),x_Symbol),
    Condition(Simp(Times(c,Power(Times(e,x),Plus(m,C1)),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Power(Times(a,e,Plus(m,C1)),CN1)),x),And(FreeQ(List(a,b,c,d,e,m,n,p),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),EqQ(Subtract(Times(a,d,Plus(m,C1)),Times(b,c,Plus(m,Times(n,Plus(p,C1)),C1))),C0),NeQ(m,CN1)))),
IIntegrate(450,Int(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus($p("a1"),Times($p("b1",true),Power(x_,$p("non2",true)))),p_DEFAULT),Power(Plus($p("a2"),Times($p("b2",true),Power(x_,$p("non2",true)))),p_DEFAULT),Plus(c_,Times(d_DEFAULT,Power(x_,n_)))),x_Symbol),
    Condition(Simp(Times(c,Power(Times(e,x),Plus(m,C1)),Power(Plus($s("a1"),Times($s("b1"),Power(x,Times(C1D2,n)))),Plus(p,C1)),Power(Plus($s("a2"),Times($s("b2"),Power(x,Times(C1D2,n)))),Plus(p,C1)),Power(Times($s("a1"),$s("a2"),e,Plus(m,C1)),CN1)),x),And(FreeQ(List($s("a1"),$s("b1"),$s("a2"),$s("b2"),c,d,e,m,n,p),x),EqQ($s("non2"),Times(C1D2,n)),EqQ(Plus(Times($s("a2"),$s("b1")),Times($s("a1"),$s("b2"))),C0),EqQ(Subtract(Times($s("a1"),$s("a2"),d,Plus(m,C1)),Times($s("b1"),$s("b2"),c,Plus(m,Times(n,Plus(p,C1)),C1))),C0),NeQ(m,CN1))))
  );
}
