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
class IntRules231 { 
  public static IAST RULES = List( 
IIntegrate(4621,Integrate(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),x_Symbol),
    Condition(Plus(Simp(Times(Sqrt(Subtract(C1,Times(Sqr(c),Sqr(x)))),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Plus(n,C1)),Power(Times(b,c,Plus(n,C1)),CN1)),x),Dist(Times(c,Power(Times(b,Plus(n,C1)),CN1)),Integrate(Times(x,Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Plus(n,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x)),And(FreeQ(list(a,b,c),x),LtQ(n,CN1)))),
IIntegrate(4622,Integrate(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),x_Symbol),
    Condition(Subtract(Negate(Simp(Times(Sqrt(Subtract(C1,Times(Sqr(c),Sqr(x)))),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Plus(n,C1)),Power(Times(b,c,Plus(n,C1)),CN1)),x)),Dist(Times(c,Power(Times(b,Plus(n,C1)),CN1)),Integrate(Times(x,Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Plus(n,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x)),And(FreeQ(list(a,b,c),x),LtQ(n,CN1)))),
IIntegrate(4623,Integrate(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),x_Symbol),
    Condition(Dist(Power(Times(b,c),CN1),Subst(Integrate(Times(Power(x,n),Cos(Subtract(Times(a,Power(b,CN1)),Times(x,Power(b,CN1))))),x),x,Plus(a,Times(b,ArcSin(Times(c,x))))),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(4624,Integrate(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),x_Symbol),
    Condition(Dist(Power(Times(b,c),CN1),Subst(Integrate(Times(Power(x,n),Sin(Subtract(Times(a,Power(b,CN1)),Times(x,Power(b,CN1))))),x),x,Plus(a,Times(b,ArcCos(Times(c,x))))),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(4625,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(x_,CN1)),x_Symbol),
    Condition(Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Power(Tan(x),CN1)),x),x,ArcSin(Times(c,x))),And(FreeQ(list(a,b,c),x),IGtQ(n,C0)))),
IIntegrate(4626,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(x_,CN1)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Power(Cot(x),CN1)),x),x,ArcCos(Times(c,x)))),And(FreeQ(list(a,b,c),x),IGtQ(n,C0)))),
IIntegrate(4627,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),Plus(m,C1)),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n),Power(Times(d,Plus(m,C1)),CN1)),x),Dist(Times(b,c,n,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(d,x),Plus(m,C1)),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Subtract(n,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x)),And(FreeQ(List(a,b,c,d,m),x),IGtQ(n,C0),NeQ(m,CN1)))),
IIntegrate(4628,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(d,x),Plus(m,C1)),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n),Power(Times(d,Plus(m,C1)),CN1)),x),Dist(Times(b,c,n,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(d,x),Plus(m,C1)),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Subtract(n,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x)),And(FreeQ(List(a,b,c,d,m),x),IGtQ(n,C0),NeQ(m,CN1)))),
IIntegrate(4629,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n),Power(Plus(m,C1),CN1)),x),Dist(Times(b,c,n,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Subtract(n,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x)),And(FreeQ(list(a,b,c),x),IGtQ(m,C0),GtQ(n,C0)))),
IIntegrate(4630,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n),Power(Plus(m,C1),CN1)),x),Dist(Times(b,c,n,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Subtract(n,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x)),And(FreeQ(list(a,b,c),x),IGtQ(m,C0),GtQ(n,C0)))),
IIntegrate(4631,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,m),Sqrt(Subtract(C1,Times(Sqr(c),Sqr(x)))),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Plus(n,C1)),Power(Times(b,c,Plus(n,C1)),CN1)),x),Dist(Power(Times(b,Power(c,Plus(m,C1)),Plus(n,C1)),CN1),Subst(Integrate(ExpandTrigReduce(Power(Plus(a,Times(b,x)),Plus(n,C1)),Times(Power(Sin(x),Subtract(m,C1)),Subtract(m,Times(Plus(m,C1),Sqr(Sin(x))))),x),x),x,ArcSin(Times(c,x))),x)),And(FreeQ(list(a,b,c),x),IGtQ(m,C0),GeQ(n,CN2),LtQ(n,CN1)))),
IIntegrate(4632,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Negate(Simp(Times(Power(x,m),Sqrt(Subtract(C1,Times(Sqr(c),Sqr(x)))),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Plus(n,C1)),Power(Times(b,c,Plus(n,C1)),CN1)),x)),Dist(Power(Times(b,Power(c,Plus(m,C1)),Plus(n,C1)),CN1),Subst(Integrate(ExpandTrigReduce(Power(Plus(a,Times(b,x)),Plus(n,C1)),Times(Power(Cos(x),Subtract(m,C1)),Subtract(m,Times(Plus(m,C1),Sqr(Cos(x))))),x),x),x,ArcCos(Times(c,x))),x)),And(FreeQ(list(a,b,c),x),IGtQ(m,C0),GeQ(n,CN2),LtQ(n,CN1)))),
IIntegrate(4633,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,m),Sqrt(Subtract(C1,Times(Sqr(c),Sqr(x)))),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Plus(n,C1)),Power(Times(b,c,Plus(n,C1)),CN1)),x),Dist(Times(c,Plus(m,C1),Power(Times(b,Plus(n,C1)),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Plus(n,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),Negate(Dist(Times(m,Power(Times(b,c,Plus(n,C1)),CN1)),Integrate(Times(Power(x,Subtract(m,C1)),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Plus(n,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x))),And(FreeQ(list(a,b,c),x),IGtQ(m,C0),LtQ(n,CN2)))),
IIntegrate(4634,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Power(x,m),Sqrt(Subtract(C1,Times(Sqr(c),Sqr(x)))),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Plus(n,C1)),Power(Times(b,c,Plus(n,C1)),CN1)),x)),Negate(Dist(Times(c,Plus(m,C1),Power(Times(b,Plus(n,C1)),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Plus(n,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x)),Dist(Times(m,Power(Times(b,c,Plus(n,C1)),CN1)),Integrate(Times(Power(x,Subtract(m,C1)),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Plus(n,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x)),And(FreeQ(list(a,b,c),x),IGtQ(m,C0),LtQ(n,CN2)))),
IIntegrate(4635,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Power(c,Plus(m,C1)),CN1),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Power(Sin(x),m),Cos(x)),x),x,ArcSin(Times(c,x))),x),And(FreeQ(List(a,b,c,n),x),IGtQ(m,C0)))),
IIntegrate(4636,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Negate(Dist(Power(Power(c,Plus(m,C1)),CN1),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Power(Cos(x),m),Sin(x)),x),x,ArcCos(Times(c,x))),x)),And(FreeQ(List(a,b,c,n),x),IGtQ(m,C0)))),
IIntegrate(4637,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(d,x),m),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n)),x),FreeQ(List(a,b,c,d,m,n),x))),
IIntegrate(4638,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(d,x),m),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n)),x),FreeQ(List(a,b,c,d,m,n),x))),
IIntegrate(4639,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),CN1),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Times(Log(Plus(a,Times(b,ArcSin(Times(c,x))))),Power(Times(b,c,Sqrt(d)),CN1)),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0),GtQ(d,C0)))),
IIntegrate(4640,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),CN1),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Negate(Simp(Times(Log(Plus(a,Times(b,ArcCos(Times(c,x))))),Power(Times(b,c,Sqrt(d)),CN1)),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0),GtQ(d,C0))))
  );
}
