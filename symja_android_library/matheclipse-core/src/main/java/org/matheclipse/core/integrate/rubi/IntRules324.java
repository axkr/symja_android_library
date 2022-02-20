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
class IntRules324 { 
  public static IAST RULES = List( 
IIntegrate(6481,Integrate(Times(ExpIntegralE(n_,Plus(a_,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(c,Times(d,x)),m),ExpIntegralE(n,Plus(a,Times(b,x)))),x),FreeQ(List(a,b,c,d,m,n),x))),
IIntegrate(6482,Integrate(ExpIntegralEi(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),ExpIntegralEi(Plus(a,Times(b,x))),Power(b,CN1)),x),Simp(Times(Exp(Plus(a,Times(b,x))),Power(b,CN1)),x)),FreeQ(list(a,b),x))),
IIntegrate(6483,Integrate(Times(ExpIntegralEi(Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Log(x),Plus(ExpIntegralEi(Times(b,x)),ExpIntegralE(C1,Times(CN1,b,x)))),x),Integrate(Times(ExpIntegralE(C1,Times(CN1,b,x)),Power(x,CN1)),x)),FreeQ(b,x))),
IIntegrate(6484,Integrate(Times(ExpIntegralEi(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Unintegrable(Times(ExpIntegralEi(Plus(a,Times(b,x))),Power(Plus(c,Times(d,x)),CN1)),x),FreeQ(List(a,b,c,d),x))),
IIntegrate(6485,Integrate(Times(ExpIntegralEi(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),ExpIntegralEi(Plus(a,Times(b,x))),Power(Times(d,Plus(m,C1)),CN1)),x),Dist(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Exp(Plus(a,Times(b,x))),Power(Plus(a,Times(b,x)),CN1)),x),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1)))),
IIntegrate(6486,Integrate(Sqr(ExpIntegralEi(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),Sqr(ExpIntegralEi(Plus(a,Times(b,x)))),Power(b,CN1)),x),Dist(C2,Integrate(Times(Exp(Plus(a,Times(b,x))),ExpIntegralEi(Plus(a,Times(b,x)))),x),x)),FreeQ(list(a,b),x))),
IIntegrate(6487,Integrate(Times(Sqr(ExpIntegralEi(Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Sqr(ExpIntegralEi(Times(b,x))),Power(Plus(m,C1),CN1)),x),Dist(Times(C2,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,m),Exp(Times(b,x)),ExpIntegralEi(Times(b,x))),x),x)),And(FreeQ(b,x),IGtQ(m,C0)))),
IIntegrate(6488,Integrate(Times(Sqr(ExpIntegralEi(Plus(a_,Times(b_DEFAULT,x_)))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Sqr(ExpIntegralEi(Plus(a,Times(b,x)))),Power(Plus(m,C1),CN1)),x),Negate(Dist(Times(C2,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,m),Exp(Plus(a,Times(b,x))),ExpIntegralEi(Plus(a,Times(b,x)))),x),x)),Negate(Dist(Times(a,m,Power(Times(b,Plus(m,C1)),CN1)),Integrate(Times(Power(x,Subtract(m,C1)),Sqr(ExpIntegralEi(Plus(a,Times(b,x))))),x),x)),Simp(Times(a,Power(x,m),Sqr(ExpIntegralEi(Plus(a,Times(b,x)))),Power(Times(b,Plus(m,C1)),CN1)),x)),And(FreeQ(list(a,b),x),IGtQ(m,C0)))),
IIntegrate(6489,Integrate(Times(Exp(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),ExpIntegralEi(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Exp(Plus(a,Times(b,x))),ExpIntegralEi(Plus(c,Times(d,x))),Power(b,CN1)),x),Dist(Times(d,Power(b,CN1)),Integrate(Times(Exp(Plus(a,c,Times(Plus(b,d),x))),Power(Plus(c,Times(d,x)),CN1)),x),x)),FreeQ(List(a,b,c,d),x))),
IIntegrate(6490,Integrate(Times(Exp(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),ExpIntegralEi(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,m),Exp(Plus(a,Times(b,x))),ExpIntegralEi(Plus(c,Times(d,x))),Power(b,CN1)),x),Negate(Dist(Times(d,Power(b,CN1)),Integrate(Times(Power(x,m),Exp(Plus(a,c,Times(Plus(b,d),x))),Power(Plus(c,Times(d,x)),CN1)),x),x)),Negate(Dist(Times(m,Power(b,CN1)),Integrate(Times(Power(x,Subtract(m,C1)),Exp(Plus(a,Times(b,x))),ExpIntegralEi(Plus(c,Times(d,x)))),x),x))),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0)))),
IIntegrate(6491,Integrate(Times(Exp(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),ExpIntegralEi(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(x_,m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Exp(Plus(a,Times(b,x))),ExpIntegralEi(Plus(c,Times(d,x))),Power(Plus(m,C1),CN1)),x),Negate(Dist(Times(b,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Exp(Plus(a,Times(b,x))),ExpIntegralEi(Plus(c,Times(d,x)))),x),x)),Negate(Dist(Times(d,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Exp(Plus(a,c,Times(Plus(b,d),x))),Power(Plus(c,Times(d,x)),CN1)),x),x))),And(FreeQ(List(a,b,c,d),x),ILtQ(m,CN1)))),
IIntegrate(6492,Integrate(ExpIntegralEi(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(x,ExpIntegralEi(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),Dist(Times(b,n,Exp(Times(a,d))),Integrate(Times(Power(Times(c,Power(x,n)),Times(b,d)),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),CN1)),x),x)),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(6493,Integrate(Times(ExpIntegralEi(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),Power(x_,CN1)),x_Symbol),
    Condition(Dist(Power(n,CN1),Subst(ExpIntegralEi(Times(d,Plus(a,Times(b,x)))),x,Log(Times(c,Power(x,n)))),x),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(6494,Integrate(Times(ExpIntegralEi(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(e,x),Plus(m,C1)),ExpIntegralEi(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(e,Plus(m,C1)),CN1)),x),Dist(Times(b,n,Exp(Times(a,d)),Power(Times(c,Power(x,n)),Times(b,d)),Power(Times(Plus(m,C1),Power(Times(e,x),Times(b,d,n))),CN1)),Integrate(Times(Power(Times(e,x),Plus(m,Times(b,d,n))),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,m,n),x),NeQ(m,CN1)))),
IIntegrate(6495,Integrate(LogIntegral(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),LogIntegral(Plus(a,Times(b,x))),Power(b,CN1)),x),Simp(Times(ExpIntegralEi(Times(C2,Log(Plus(a,Times(b,x))))),Power(b,CN1)),x)),FreeQ(list(a,b),x))),
IIntegrate(6496,Integrate(Times(LogIntegral(Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(b,x),x)),Simp(Times(Log(Times(b,x)),LogIntegral(Times(b,x))),x)),FreeQ(b,x))),
IIntegrate(6497,Integrate(Times(LogIntegral(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Unintegrable(Times(LogIntegral(Plus(a,Times(b,x))),Power(Plus(c,Times(d,x)),CN1)),x),FreeQ(List(a,b,c,d),x))),
IIntegrate(6498,Integrate(Times(LogIntegral(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),LogIntegral(Plus(a,Times(b,x))),Power(Times(d,Plus(m,C1)),CN1)),x),Dist(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Power(Log(Plus(a,Times(b,x))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1)))),
IIntegrate(6499,Integrate(SinIntegral(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Times(Plus(a,Times(b,x)),SinIntegral(Plus(a,Times(b,x))),Power(b,CN1)),x),Simp(Times(Cos(Plus(a,Times(b,x))),Power(b,CN1)),x)),FreeQ(list(a,b),x))),
IIntegrate(6500,Integrate(CosIntegral(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),CosIntegral(Plus(a,Times(b,x))),Power(b,CN1)),x),Simp(Times(Sin(Plus(a,Times(b,x))),Power(b,CN1)),x)),FreeQ(list(a,b),x)))
  );
}
