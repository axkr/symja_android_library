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
public class IntRules24 { 
  public static IAST RULES = List( 
IIntegrate(601,Int(Times(Power(x_,m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Power(x_,$p("mn",true)))),q_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),p_DEFAULT),Power(Plus(e_,Times(f_DEFAULT,Power(x_,n_DEFAULT))),r_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(x,Times(n,FracPart(q))),Power(Plus(c,Times(d,Power(Power(x,n),CN1))),FracPart(q)),Power(Power(Plus(d,Times(c,Power(x,n))),FracPart(q)),CN1)),Int(Times(Power(x,Subtract(m,Times(n,q))),Power(Plus(a,Times(b,Power(x,n))),p),Power(Plus(d,Times(c,Power(x,n))),q),Power(Plus(e,Times(f,Power(x,n))),r)),x),x),And(FreeQ(List(a,b,c,d,e,f,m,n,p,q,r),x),EqQ($s("mn"),Negate(n)),Not(IntegerQ(q))))),
IIntegrate(602,Int(Times(Power(Times(g_,x_),m_),Power(Plus(c_,Times(d_DEFAULT,Power(x_,$p("mn",true)))),q_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_DEFAULT))),p_DEFAULT),Power(Plus(e_,Times(f_DEFAULT,Power(x_,n_DEFAULT))),r_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(g,IntPart(m)),Power(Times(g,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Int(Times(Power(x,m),Power(Plus(a,Times(b,Power(x,n))),p),Power(Plus(c,Times(d,Power(Power(x,n),CN1))),q),Power(Plus(e,Times(f,Power(x,n))),r)),x),x),And(FreeQ(List(a,b,c,d,e,f,g,m,n,p,q,r),x),EqQ($s("mn"),Negate(n))))),
IIntegrate(603,Int(Times(Power(Times(g_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_))),q_DEFAULT),Power(Plus(e_,Times(f_DEFAULT,Power(x_,n_))),r_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(g,x),m),Power(Plus(a,Times(b,Power(x,n))),p),Power(Plus(c,Times(d,Power(x,n))),q),Power(Plus(e,Times(f,Power(x,n))),r)),x),FreeQ(List(a,b,c,d,e,f,g,m,n,p,q,r),x))),
IIntegrate(604,Int(Times(Power(u_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(v_,n_))),p_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,Power(v_,n_))),q_DEFAULT),Power(Plus(e_,Times(f_DEFAULT,Power(v_,n_))),r_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(u,m),Power(Times(Coefficient(v,x,C1),Power(v,m)),CN1)),Subst(Int(Times(Power(x,m),Power(Plus(a,Times(b,Power(x,n))),p),Power(Plus(c,Times(d,Power(x,n))),q),Power(Plus(e,Times(f,Power(x,n))),r)),x),x,v),x),And(FreeQ(List(a,b,c,d,e,f,m,n,p,q,r),x),LinearPairQ(u,v,x)))),
IIntegrate(605,Int(Times(Power(Times(g_DEFAULT,x_),m_DEFAULT),Power(Plus($p("e1"),Times($p("f1",true),Power(x_,$p("n2",true)))),r_DEFAULT),Power(Plus($p("e2"),Times($p("f2",true),Power(x_,$p("n2",true)))),r_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_))),q_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Times(g,x),m),Power(Plus(a,Times(b,Power(x,n))),p),Power(Plus(c,Times(d,Power(x,n))),q),Power(Plus(Times($s("e1"),$s("e2")),Times($s("f1"),$s("f2"),Power(x,n))),r)),x),And(FreeQ(List(a,b,c,d,$s("e1"),$s("f1"),$s("e2"),$s("f2"),g,m,n,p,q,r),x),EqQ($s("n2"),Times(C1D2,n)),EqQ(Plus(Times($s("e2"),$s("f1")),Times($s("e1"),$s("f2"))),C0),Or(IntegerQ(r),And(GtQ($s("e1"),C0),GtQ($s("e2"),C0)))))),
IIntegrate(606,Int(Times(Power(Times(g_DEFAULT,x_),m_DEFAULT),Power(Plus($p("e1"),Times($p("f1",true),Power(x_,$p("n2",true)))),r_DEFAULT),Power(Plus($p("e2"),Times($p("f2",true),Power(x_,$p("n2",true)))),r_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_))),q_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(Plus($s("e1"),Times($s("f1"),Power(x,Times(C1D2,n)))),FracPart(r)),Power(Plus($s("e2"),Times($s("f2"),Power(x,Times(C1D2,n)))),FracPart(r)),Power(Power(Plus(Times($s("e1"),$s("e2")),Times($s("f1"),$s("f2"),Power(x,n))),FracPart(r)),CN1)),Int(Times(Power(Times(g,x),m),Power(Plus(a,Times(b,Power(x,n))),p),Power(Plus(c,Times(d,Power(x,n))),q),Power(Plus(Times($s("e1"),$s("e2")),Times($s("f1"),$s("f2"),Power(x,n))),r)),x),x),And(FreeQ(List(a,b,c,d,$s("e1"),$s("f1"),$s("e2"),$s("f2"),g,m,n,p,q,r),x),EqQ($s("n2"),Times(C1D2,n)),EqQ(Plus(Times($s("e2"),$s("f1")),Times($s("e1"),$s("f2"))),C0)))),
IIntegrate(607,Int(Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),x_Symbol),
    Condition(Simp(Times(C2,Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(p,C1)),Power(Times(Plus(Times(C2,p),C1),Plus(b,Times(C2,c,x))),CN1)),x),And(FreeQ(List(a,b,c,p),x),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0),LtQ(p,CN1)))),
IIntegrate(608,Int(Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),CN1D2),x_Symbol),
    Condition(Dist(Times(Plus(Times(C1D2,b),Times(c,x)),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),CN1D2)),Int(Power(Plus(Times(C1D2,b),Times(c,x)),CN1),x),x),And(FreeQ(List(a,b,c),x),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0)))),
IIntegrate(609,Int(Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),x_Symbol),
    Condition(Simp(Times(Plus(b,Times(C2,c,x)),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),p),Power(Times(C2,c,Plus(Times(C2,p),C1)),CN1)),x),And(FreeQ(List(a,b,c,p),x),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0),NeQ(p,Negate(C1D2))))),
IIntegrate(610,Int(Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),x_Symbol),
    Condition(With(List(Set(q,Rt(Subtract(Sqr(b),Times(C4,a,c)),C2))),Dist(Power(Power(c,p),CN1),Int(Times(Power(Simp(Plus(Times(C1D2,b),Times(CN1,C1D2,q),Times(c,x)),x),p),Power(Simp(Plus(Times(C1D2,b),Times(C1D2,q),Times(c,x)),x),p)),x),x)),And(FreeQ(List(a,b,c),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),IGtQ(p,C0),PerfectSquareQ(Subtract(Sqr(b),Times(C4,a,c)))))),
IIntegrate(611,Int(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),x_Symbol),
    Condition(Int(ExpandIntegrand(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),p),x),x),And(FreeQ(List(a,b,c),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),IGtQ(p,C0),Or(EqQ(a,C0),Not(PerfectSquareQ(Subtract(Sqr(b),Times(C4,a,c)))))))),
IIntegrate(612,Int(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(b,Times(C2,c,x)),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),p),Power(Times(C2,c,Plus(Times(C2,p),C1)),CN1)),x),Dist(Times(p,Subtract(Sqr(b),Times(C4,a,c)),Power(Times(C2,c,Plus(Times(C2,p),C1)),CN1)),Int(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Subtract(p,C1)),x),x)),And(FreeQ(List(a,b,c),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),GtQ(p,C0),IntegerQ(Times(C4,p))))),
IIntegrate(613,Int(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),QQ(-3L,2L)),x_Symbol),
    Condition(Simp(Times(CN2,Plus(b,Times(C2,c,x)),Power(Times(Subtract(Sqr(b),Times(C4,a,c)),Sqrt(Plus(a,Times(b,x),Times(c,Sqr(x))))),CN1)),x),And(FreeQ(List(a,b,c),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0)))),
IIntegrate(614,Int(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(b,Times(C2,c,x)),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(p,C1)),Power(Times(Plus(p,C1),Subtract(Sqr(b),Times(C4,a,c))),CN1)),x),Dist(Times(C2,c,Plus(Times(C2,p),C3),Power(Times(Plus(p,C1),Subtract(Sqr(b),Times(C4,a,c))),CN1)),Int(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(p,C1)),x),x)),And(FreeQ(List(a,b,c),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),LtQ(p,CN1),NeQ(p,QQ(-3L,2L)),IntegerQ(Times(C4,p))))),
IIntegrate(615,Int(Power(Plus(Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),CN1),x_Symbol),
    Condition(Subtract(Simp(Times(Log(x),Power(b,CN1)),x),Simp(Times(Log(RemoveContent(Plus(b,Times(c,x)),x)),Power(b,CN1)),x)),FreeQ(List(b,c),x))),
IIntegrate(616,Int(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),CN1),x_Symbol),
    Condition(With(List(Set(q,Rt(Subtract(Sqr(b),Times(C4,a,c)),C2))),Subtract(Dist(Times(c,Power(q,CN1)),Int(Power(Simp(Plus(Times(C1D2,b),Times(CN1,C1D2,q),Times(c,x)),x),CN1),x),x),Dist(Times(c,Power(q,CN1)),Int(Power(Simp(Plus(Times(C1D2,b),Times(C1D2,q),Times(c,x)),x),CN1),x),x))),And(FreeQ(List(a,b,c),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),PosQ(Subtract(Sqr(b),Times(C4,a,c))),PerfectSquareQ(Subtract(Sqr(b),Times(C4,a,c)))))),
IIntegrate(617,Int(Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),CN1),x_Symbol),
    Condition(With(List(Set(q,Subtract(C1,Times(C4,Simplify(Times(a,c,Power(b,CN2))))))),Condition(Dist(Times(CN2,Power(b,CN1)),Subst(Int(Power(Subtract(q,Sqr(x)),CN1),x),x,Plus(C1,Times(C2,c,x,Power(b,CN1)))),x),And(RationalQ(q),Or(EqQ(Sqr(q),C1),Not(RationalQ(Subtract(Sqr(b),Times(C4,a,c)))))))),And(FreeQ(List(a,b,c),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0)))),
IIntegrate(618,Int(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),CN1),x_Symbol),
    Condition(Dist(CN2,Subst(Int(Power(Simp(Subtract(Subtract(Sqr(b),Times(C4,a,c)),Sqr(x)),x),CN1),x),x,Plus(b,Times(C2,c,x))),x),And(FreeQ(List(a,b,c),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0)))),
IIntegrate(619,Int(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),x_Symbol),
    Condition(Dist(Power(Times(C2,c,Power(Times(CN4,c,Power(Subtract(Sqr(b),Times(C4,a,c)),CN1)),p)),CN1),Subst(Int(Power(Simp(Subtract(C1,Times(Sqr(x),Power(Subtract(Sqr(b),Times(C4,a,c)),CN1))),x),p),x),x,Plus(b,Times(C2,c,x))),x),And(FreeQ(List(a,b,c,p),x),GtQ(Subtract(Times(C4,a),Times(Sqr(b),Power(c,CN1))),C0)))),
IIntegrate(620,Int(Power(Plus(Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),CN1D2),x_Symbol),
    Condition(Dist(C2,Subst(Int(Power(Subtract(C1,Times(c,Sqr(x))),CN1),x),x,Times(x,Power(Plus(Times(b,x),Times(c,Sqr(x))),CN1D2))),x),FreeQ(List(b,c),x))),
IIntegrate(621,Int(Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),CN1D2),x_Symbol),
    Condition(Dist(C2,Subst(Int(Power(Subtract(Times(C4,c),Sqr(x)),CN1),x),x,Times(Plus(b,Times(C2,c,x)),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),CN1D2))),x),And(FreeQ(List(a,b,c),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0)))),
IIntegrate(622,Int(Power(Plus(Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),x_Symbol),
    Condition(Dist(Times(Power(Plus(Times(b,x),Times(c,Sqr(x))),p),Power(Power(Times(CN1,c,Plus(Times(b,x),Times(c,Sqr(x))),Power(b,CN2)),p),CN1)),Int(Power(Subtract(Times(CN1,c,x,Power(b,CN1)),Times(Sqr(c),Sqr(x),Power(b,CN2))),p),x),x),And(FreeQ(List(b,c),x),RationalQ(p),LessEqual(C3,Denominator(p),C4)))),
IIntegrate(623,Int(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),x_Symbol),
    Condition(With(List(Set(d,Denominator(p))),Condition(Dist(Times(d,Sqrt(Sqr(Plus(b,Times(C2,c,x)))),Power(Plus(b,Times(C2,c,x)),CN1)),Subst(Int(Times(Power(x,Subtract(Times(d,Plus(p,C1)),C1)),Power(Plus(Sqr(b),Times(CN1,C4,a,c),Times(C4,c,Power(x,d))),CN1D2)),x),x,Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Power(d,CN1))),x),LessEqual(C3,d,C4))),And(FreeQ(List(a,b,c),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),RationalQ(p)))),
IIntegrate(624,Int(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),x_Symbol),
    Condition(With(List(Set(q,Rt(Subtract(Sqr(b),Times(C4,a,c)),C2))),Negate(Simp(Times(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(p,C1)),Hypergeometric2F1(Negate(p),Plus(p,C1),Plus(p,C2),Times(Plus(b,q,Times(C2,c,x)),Power(Times(C2,q),CN1))),Power(Times(q,Plus(p,C1),Power(Times(Subtract(Subtract(q,b),Times(C2,c,x)),Power(Times(C2,q),CN1)),Plus(p,C1))),CN1)),x))),And(FreeQ(List(a,b,c,p),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),Not(IntegerQ(Times(C4,p)))))),
IIntegrate(625,Int(Power(Plus(a_DEFAULT,Times(b_DEFAULT,u_),Times(c_DEFAULT,Sqr(u_))),p_),x_Symbol),
    Condition(Dist(Power(Coefficient(u,x,C1),CN1),Subst(Int(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),p),x),x,u),x),And(FreeQ(List(a,b,c,p),x),LinearQ(u,x),NeQ(u,x))))
  );
}
