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
class IntRules257 { 
  public static IAST RULES = List( 
IIntegrate(5141,Integrate(Times(Cos(Times(Sqr(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT))),d_DEFAULT)),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Dist(C1D2,Integrate(Times(Power(Times(e,x),m),Power(Exp(Times(CI,d,Sqr(Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),CN1)),x),x),x),Simp(Dist(C1D2,Integrate(Times(Power(Times(e,x),m),Exp(Times(CI,d,Sqr(Plus(a,Times(b,Log(Times(c,Power(x,n))))))))),x),x),x)),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(5142,Integrate(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),x_Symbol),
    Condition(Subtract(Simp(Times(x,Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n)),x),Simp(Dist(Times(b,c,n),Integrate(Times(x,Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Plus(n,CN1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x)),And(FreeQ(list(a,b,c),x),GtQ(n,C0)))),
IIntegrate(5143,Integrate(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),x_Symbol),
    Condition(Plus(Simp(Times(x,Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n)),x),Simp(Dist(Times(b,c,n),Integrate(Times(x,Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Plus(n,CN1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x)),And(FreeQ(list(a,b,c),x),GtQ(n,C0)))),
IIntegrate(5144,Integrate(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),x_Symbol),
    Condition(Plus(Simp(Times(Sqrt(Subtract(C1,Times(Sqr(c),Sqr(x)))),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Plus(n,C1)),Power(Times(b,c,Plus(n,C1)),CN1)),x),Simp(Dist(Times(c,Power(Times(b,Plus(n,C1)),CN1)),Integrate(Times(x,Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Plus(n,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x)),And(FreeQ(list(a,b,c),x),LtQ(n,CN1)))),
IIntegrate(5145,Integrate(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),x_Symbol),
    Condition(Subtract(Simp(Times(CN1,Sqrt(Subtract(C1,Times(Sqr(c),Sqr(x)))),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Plus(n,C1)),Power(Times(b,c,Plus(n,C1)),CN1)),x),Simp(Dist(Times(c,Power(Times(b,Plus(n,C1)),CN1)),Integrate(Times(x,Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Plus(n,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x)),And(FreeQ(list(a,b,c),x),LtQ(n,CN1)))),
IIntegrate(5146,Integrate(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),x_Symbol),
    Condition(Simp(Dist(Power(Times(b,c),CN1),Subst(Integrate(Times(Power(x,n),Cos(Plus(Times(CN1,a,Power(b,CN1)),Times(x,Power(b,CN1))))),x),x,Plus(a,Times(b,ArcSin(Times(c,x))))),x),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(5147,Integrate(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),x_Symbol),
    Condition(Simp(Dist(Negate(Power(Times(b,c),CN1)),Subst(Integrate(Times(Power(x,n),Sin(Plus(Times(CN1,a,Power(b,CN1)),Times(x,Power(b,CN1))))),x),x,Plus(a,Times(b,ArcCos(Times(c,x))))),x),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(5148,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(x_,CN1)),x_Symbol),
    Condition(Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Cot(x)),x),x,ArcSin(Times(c,x))),And(FreeQ(list(a,b,c),x),IGtQ(n,C0)))),
IIntegrate(5149,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(x_,CN1)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Tan(x)),x),x,ArcCos(Times(c,x)))),And(FreeQ(list(a,b,c),x),IGtQ(n,C0)))),
IIntegrate(5150,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),Plus(m,C1)),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,c,n,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(d,x),Plus(m,C1)),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Plus(n,CN1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x)),And(FreeQ(List(a,b,c,d,m),x),IGtQ(n,C0),NeQ(m,CN1)))),
IIntegrate(5151,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(d,x),Plus(m,C1)),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,c,n,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(d,x),Plus(m,C1)),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Plus(n,CN1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x)),And(FreeQ(List(a,b,c,d,m),x),IGtQ(n,C0),NeQ(m,CN1)))),
IIntegrate(5152,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n),Power(Plus(m,C1),CN1)),x),Simp(Dist(Times(b,c,n,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Plus(n,CN1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x)),And(FreeQ(list(a,b,c),x),IGtQ(m,C0),GtQ(n,C0)))),
IIntegrate(5153,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n),Power(Plus(m,C1),CN1)),x),Simp(Dist(Times(b,c,n,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Plus(n,CN1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x)),And(FreeQ(list(a,b,c),x),IGtQ(m,C0),GtQ(n,C0)))),
IIntegrate(5154,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,m),Sqrt(Subtract(C1,Times(Sqr(c),Sqr(x)))),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Plus(n,C1)),Power(Times(b,c,Plus(n,C1)),CN1)),x),Simp(Dist(Power(Times(Sqr(b),Power(c,Plus(m,C1)),Plus(n,C1)),CN1),Subst(Integrate(ExpandTrigReduce(Power(x,Plus(n,C1)),Times(Power(Sin(Plus(Times(CN1,a,Power(b,CN1)),Times(x,Power(b,CN1)))),Plus(m,CN1)),Subtract(m,Times(Plus(m,C1),Sqr(Sin(Plus(Times(CN1,a,Power(b,CN1)),Times(x,Power(b,CN1)))))))),x),x),x,Plus(a,Times(b,ArcSin(Times(c,x))))),x),x)),And(FreeQ(list(a,b,c),x),IGtQ(m,C0),GeQ(n,CN2),LtQ(n,CN1)))),
IIntegrate(5155,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(CN1,Power(x,m),Sqrt(Subtract(C1,Times(Sqr(c),Sqr(x)))),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Plus(n,C1)),Power(Times(b,c,Plus(n,C1)),CN1)),x),Simp(Dist(Power(Times(Sqr(b),Power(c,Plus(m,C1)),Plus(n,C1)),CN1),Subst(Integrate(ExpandTrigReduce(Power(x,Plus(n,C1)),Times(Power(Cos(Plus(Times(CN1,a,Power(b,CN1)),Times(x,Power(b,CN1)))),Plus(m,CN1)),Subtract(m,Times(Plus(m,C1),Sqr(Cos(Plus(Times(CN1,a,Power(b,CN1)),Times(x,Power(b,CN1)))))))),x),x),x,Plus(a,Times(b,ArcCos(Times(c,x))))),x),x)),And(FreeQ(list(a,b,c),x),IGtQ(m,C0),GeQ(n,CN2),LtQ(n,CN1)))),
IIntegrate(5156,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,m),Sqrt(Subtract(C1,Times(Sqr(c),Sqr(x)))),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Plus(n,C1)),Power(Times(b,c,Plus(n,C1)),CN1)),x),Simp(Dist(Times(c,Plus(m,C1),Power(Times(b,Plus(n,C1)),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Plus(n,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x),Negate(Simp(Dist(Times(m,Power(Times(b,c,Plus(n,C1)),CN1)),Integrate(Times(Power(x,Plus(m,CN1)),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Plus(n,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x))),And(FreeQ(list(a,b,c),x),IGtQ(m,C0),LtQ(n,CN2)))),
IIntegrate(5157,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(x,m),Sqrt(Subtract(C1,Times(Sqr(c),Sqr(x)))),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Plus(n,C1)),Power(Times(b,c,Plus(n,C1)),CN1)),x),Negate(Simp(Dist(Times(c,Plus(m,C1),Power(Times(b,Plus(n,C1)),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Plus(n,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x)),Simp(Dist(Times(m,Power(Times(b,c,Plus(n,C1)),CN1)),Integrate(Times(Power(x,Plus(m,CN1)),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Plus(n,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x)),And(FreeQ(list(a,b,c),x),IGtQ(m,C0),LtQ(n,CN2)))),
IIntegrate(5158,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Times(b,Power(c,Plus(m,C1))),CN1),Subst(Integrate(Times(Power(x,n),Power(Sin(Plus(Times(CN1,a,Power(b,CN1)),Times(x,Power(b,CN1)))),m),Cos(Plus(Times(CN1,a,Power(b,CN1)),Times(x,Power(b,CN1))))),x),x,Plus(a,Times(b,ArcSin(Times(c,x))))),x),x),And(FreeQ(List(a,b,c,n),x),IGtQ(m,C0)))),
IIntegrate(5159,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Negate(Power(Times(b,Power(c,Plus(m,C1))),CN1)),Subst(Integrate(Times(Power(x,n),Power(Cos(Plus(Times(CN1,a,Power(b,CN1)),Times(x,Power(b,CN1)))),m),Sin(Plus(Times(CN1,a,Power(b,CN1)),Times(x,Power(b,CN1))))),x),x,Plus(a,Times(b,ArcCos(Times(c,x))))),x),x),And(FreeQ(List(a,b,c,n),x),IGtQ(m,C0)))),
IIntegrate(5160,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(d,x),m),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n)),x),FreeQ(List(a,b,c,d,m,n),x)))
  );
}
