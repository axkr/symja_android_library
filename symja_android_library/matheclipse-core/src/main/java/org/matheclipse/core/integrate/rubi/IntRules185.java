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
public class IntRules185 { 
  public static IAST RULES = List( 
IIntegrate(4626,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(x_,CN1)),x_Symbol),
    Condition(Negate(Subst(Int(Times(Power(Plus(a,Times(b,x)),n),Power(Cot(x),CN1)),x),x,ArcCos(Times(c,x)))),And(FreeQ(List(a,b,c),x),IGtQ(n,C0)))),
IIntegrate(4627,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),Plus(m,C1)),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n),Power(Times(d,Plus(m,C1)),CN1)),x),Dist(Times(b,c,n,Power(Times(d,Plus(m,C1)),CN1)),Int(Times(Power(Times(d,x),Plus(m,C1)),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Subtract(n,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x)),And(FreeQ(List(a,b,c,d,m),x),IGtQ(n,C0),NeQ(m,CN1)))),
IIntegrate(4628,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(d,x),Plus(m,C1)),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n),Power(Times(d,Plus(m,C1)),CN1)),x),Dist(Times(b,c,n,Power(Times(d,Plus(m,C1)),CN1)),Int(Times(Power(Times(d,x),Plus(m,C1)),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Subtract(n,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x)),And(FreeQ(List(a,b,c,d,m),x),IGtQ(n,C0),NeQ(m,CN1)))),
IIntegrate(4629,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n),Power(Plus(m,C1),CN1)),x),Dist(Times(b,c,n,Power(Plus(m,C1),CN1)),Int(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Subtract(n,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x)),And(FreeQ(List(a,b,c),x),IGtQ(m,C0),GtQ(n,C0)))),
IIntegrate(4630,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n),Power(Plus(m,C1),CN1)),x),Dist(Times(b,c,n,Power(Plus(m,C1),CN1)),Int(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Subtract(n,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x)),And(FreeQ(List(a,b,c),x),IGtQ(m,C0),GtQ(n,C0)))),
IIntegrate(4631,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,m),Sqrt(Subtract(C1,Times(Sqr(c),Sqr(x)))),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Plus(n,C1)),Power(Times(b,c,Plus(n,C1)),CN1)),x),Dist(Power(Times(b,Power(c,Plus(m,C1)),Plus(n,C1)),CN1),Subst(Int(ExpandTrigReduce(Power(Plus(a,Times(b,x)),Plus(n,C1)),Times(Power(Sin(x),Subtract(m,C1)),Subtract(m,Times(Plus(m,C1),Sqr(Sin(x))))),x),x),x,ArcSin(Times(c,x))),x)),And(FreeQ(List(a,b,c),x),IGtQ(m,C0),GeQ(n,CN2),LtQ(n,CN1)))),
IIntegrate(4632,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Negate(Simp(Times(Power(x,m),Sqrt(Subtract(C1,Times(Sqr(c),Sqr(x)))),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Plus(n,C1)),Power(Times(b,c,Plus(n,C1)),CN1)),x)),Dist(Power(Times(b,Power(c,Plus(m,C1)),Plus(n,C1)),CN1),Subst(Int(ExpandTrigReduce(Power(Plus(a,Times(b,x)),Plus(n,C1)),Times(Power(Cos(x),Subtract(m,C1)),Subtract(m,Times(Plus(m,C1),Sqr(Cos(x))))),x),x),x,ArcCos(Times(c,x))),x)),And(FreeQ(List(a,b,c),x),IGtQ(m,C0),GeQ(n,CN2),LtQ(n,CN1)))),
IIntegrate(4633,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,m),Sqrt(Subtract(C1,Times(Sqr(c),Sqr(x)))),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Plus(n,C1)),Power(Times(b,c,Plus(n,C1)),CN1)),x),Dist(Times(c,Plus(m,C1),Power(Times(b,Plus(n,C1)),CN1)),Int(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Plus(n,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),Negate(Dist(Times(m,Power(Times(b,c,Plus(n,C1)),CN1)),Int(Times(Power(x,Subtract(m,C1)),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Plus(n,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x))),And(FreeQ(List(a,b,c),x),IGtQ(m,C0),LtQ(n,CN2)))),
IIntegrate(4634,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Power(x,m),Sqrt(Subtract(C1,Times(Sqr(c),Sqr(x)))),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Plus(n,C1)),Power(Times(b,c,Plus(n,C1)),CN1)),x)),Negate(Dist(Times(c,Plus(m,C1),Power(Times(b,Plus(n,C1)),CN1)),Int(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Plus(n,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x)),Dist(Times(m,Power(Times(b,c,Plus(n,C1)),CN1)),Int(Times(Power(x,Subtract(m,C1)),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Plus(n,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x)),And(FreeQ(List(a,b,c),x),IGtQ(m,C0),LtQ(n,CN2)))),
IIntegrate(4635,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Power(c,Plus(m,C1)),CN1),Subst(Int(Times(Power(Plus(a,Times(b,x)),n),Power(Sin(x),m),Cos(x)),x),x,ArcSin(Times(c,x))),x),And(FreeQ(List(a,b,c,n),x),IGtQ(m,C0)))),
IIntegrate(4636,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Negate(Dist(Power(Power(c,Plus(m,C1)),CN1),Subst(Int(Times(Power(Plus(a,Times(b,x)),n),Power(Cos(x),m),Sin(x)),x),x,ArcCos(Times(c,x))),x)),And(FreeQ(List(a,b,c,n),x),IGtQ(m,C0)))),
IIntegrate(4637,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(d,x),m),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n)),x),FreeQ(List(a,b,c,d,m,n),x))),
IIntegrate(4638,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(d,x),m),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n)),x),FreeQ(List(a,b,c,d,m,n),x))),
IIntegrate(4639,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),CN1),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Times(Log(Plus(a,Times(b,ArcSin(Times(c,x))))),Power(Times(b,c,Sqrt(d)),CN1)),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0),GtQ(d,C0)))),
IIntegrate(4640,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),CN1),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Negate(Simp(Times(Log(Plus(a,Times(b,ArcCos(Times(c,x))))),Power(Times(b,c,Sqrt(d)),CN1)),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0),GtQ(d,C0)))),
IIntegrate(4641,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Times(Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Plus(n,C1)),Power(Times(b,c,Sqrt(d),Plus(n,C1)),CN1)),x),And(FreeQ(List(a,b,c,d,e,n),x),EqQ(Plus(Times(Sqr(c),d),e),C0),GtQ(d,C0),NeQ(n,CN1)))),
IIntegrate(4642,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Negate(Simp(Times(Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Plus(n,C1)),Power(Times(b,c,Sqrt(d),Plus(n,C1)),CN1)),x)),And(FreeQ(List(a,b,c,d,e,n),x),EqQ(Plus(Times(Sqr(c),d),e),C0),GtQ(d,C0),NeQ(n,CN1)))),
IIntegrate(4643,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Dist(Times(Sqrt(Subtract(C1,Times(Sqr(c),Sqr(x)))),Power(Plus(d,Times(e,Sqr(x))),CN1D2)),Int(Times(Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),And(FreeQ(List(a,b,c,d,e,n),x),EqQ(Plus(Times(Sqr(c),d),e),C0),Not(GtQ(d,C0))))),
IIntegrate(4644,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Dist(Times(Sqrt(Subtract(C1,Times(Sqr(c),Sqr(x)))),Power(Plus(d,Times(e,Sqr(x))),CN1D2)),Int(Times(Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),And(FreeQ(List(a,b,c,d,e,n),x),EqQ(Plus(Times(Sqr(c),d),e),C0),Not(GtQ(d,C0))))),
IIntegrate(4645,Int(Times(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(With(List(Set(u,IntHide(Power(Plus(d,Times(e,Sqr(x))),p),x))),Subtract(Dist(Plus(a,Times(b,ArcSin(Times(c,x)))),u,x),Dist(Times(b,c),Int(SimplifyIntegrand(Times(u,Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x))),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0),IGtQ(p,C0)))),
IIntegrate(4646,Int(Times(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(With(List(Set(u,IntHide(Power(Plus(d,Times(e,Sqr(x))),p),x))),Plus(Dist(Plus(a,Times(b,ArcCos(Times(c,x)))),u,x),Dist(Times(b,c),Int(SimplifyIntegrand(Times(u,Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x))),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0),IGtQ(p,C0)))),
IIntegrate(4647,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Sqrt(Plus(d_,Times(e_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Simp(Times(C1D2,x,Sqrt(Plus(d,Times(e,Sqr(x)))),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n)),x),Dist(Times(Sqrt(Plus(d,Times(e,Sqr(x)))),Power(Times(C2,Sqrt(Subtract(C1,Times(Sqr(c),Sqr(x))))),CN1)),Int(Times(Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),Negate(Dist(Times(b,c,n,Sqrt(Plus(d,Times(e,Sqr(x)))),Power(Times(C2,Sqrt(Subtract(C1,Times(Sqr(c),Sqr(x))))),CN1)),Int(Times(x,Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Subtract(n,C1))),x),x))),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0),GtQ(n,C0)))),
IIntegrate(4648,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Sqrt(Plus(d_,Times(e_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Simp(Times(C1D2,x,Sqrt(Plus(d,Times(e,Sqr(x)))),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n)),x),Dist(Times(Sqrt(Plus(d,Times(e,Sqr(x)))),Power(Times(C2,Sqrt(Subtract(C1,Times(Sqr(c),Sqr(x))))),CN1)),Int(Times(Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),Dist(Times(b,c,n,Sqrt(Plus(d,Times(e,Sqr(x)))),Power(Times(C2,Sqrt(Subtract(C1,Times(Sqr(c),Sqr(x))))),CN1)),Int(Times(x,Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Subtract(n,C1))),x),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0),GtQ(n,C0)))),
IIntegrate(4649,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(x,Power(Plus(d,Times(e,Sqr(x))),p),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n),Power(Plus(Times(C2,p),C1),CN1)),x),Dist(Times(C2,d,p,Power(Plus(Times(C2,p),C1),CN1)),Int(Times(Power(Plus(d,Times(e,Sqr(x))),Subtract(p,C1)),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n)),x),x),Negate(Dist(Times(b,c,n,Power(d,IntPart(p)),Power(Plus(d,Times(e,Sqr(x))),FracPart(p)),Power(Times(Plus(Times(C2,p),C1),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),FracPart(p))),CN1)),Int(Times(x,Power(Subtract(C1,Times(Sqr(c),Sqr(x))),Subtract(p,C1D2)),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Subtract(n,C1))),x),x))),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0),GtQ(n,C0),GtQ(p,C0)))),
IIntegrate(4650,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(x,Power(Plus(d,Times(e,Sqr(x))),p),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n),Power(Plus(Times(C2,p),C1),CN1)),x),Dist(Times(C2,d,p,Power(Plus(Times(C2,p),C1),CN1)),Int(Times(Power(Plus(d,Times(e,Sqr(x))),Subtract(p,C1)),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n)),x),x),Dist(Times(b,c,n,Power(d,IntPart(p)),Power(Plus(d,Times(e,Sqr(x))),FracPart(p)),Power(Times(Plus(Times(C2,p),C1),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),FracPart(p))),CN1)),Int(Times(x,Power(Subtract(C1,Times(Sqr(c),Sqr(x))),Subtract(p,C1D2)),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Subtract(n,C1))),x),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0),GtQ(n,C0),GtQ(p,C0))))
  );
}
