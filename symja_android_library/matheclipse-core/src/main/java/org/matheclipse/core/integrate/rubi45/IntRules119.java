package org.matheclipse.core.integrate.rubi45;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi45.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi45.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/** 
 * IndefiniteIntegrationRules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class IntRules119 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Times(Power(Plus(Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT),a_DEFAULT),pn_DEFAULT),Power(Plus(Times(Sqr(x_),pe_DEFAULT),pd_),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Times(Sqrt(Plus(pd,Times(pe,Sqr(x)))),Power(Times(Sqrt(Plus(pd,Times(CN1,c,pd,x))),Sqrt(Plus(C1,Times(c,x)))),CN1),Int(Times(Power(x,m),Power(Plus(pd,Times(CN1,c,pd,x)),p),Power(Plus(C1,Times(c,x)),p),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),pn)),x)),And(And(FreeQ(List(a,b,c,pd,pe,m,pn),x),ZeroQ(Plus(Times(Sqr(c),pd),pe))),PositiveIntegerQ(Plus(p,C1D2))))),
ISetDelayed(Int(Times(Power(Plus(Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT),a_DEFAULT),pn_DEFAULT),Power(Plus(Times(Sqr(x_),pe_DEFAULT),pd_),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Times(Sqrt(Plus(pd,Times(CN1,c,pd,x))),Sqrt(Plus(C1,Times(c,x))),Power(Plus(pd,Times(pe,Sqr(x))),CN1D2),Int(Times(Power(x,m),Power(Plus(pd,Times(CN1,c,pd,x)),p),Power(Plus(C1,Times(c,x)),p),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),pn)),x)),And(And(FreeQ(List(a,b,c,pd,pe,m,pn),x),ZeroQ(Plus(Times(Sqr(c),pd),pe))),NegativeIntegerQ(Plus(p,Times(CN1,C1D2)))))),
ISetDelayed(Int(Times(Power(Plus(Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT),a_DEFAULT),pn_DEFAULT),Power(Plus(Times(Sqr(x_),pe_DEFAULT),pd_DEFAULT),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Int(ExpandIntegrand(Times(Power(x,m),Power(Plus(pd,Times(pe,Sqr(x))),p),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),pn)),x),x),And(FreeQ(List(a,b,c,pd,pe,m,pn),x),PositiveIntegerQ(p)))),
ISetDelayed(Int(Times(Power(Plus(Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT),a_DEFAULT),pn_DEFAULT),Power(Plus(Times(Sqr(x_),pe_DEFAULT),pd_DEFAULT),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition($(Defer($s("Int")),Times(Power(x,m),Power(Plus(pd,Times(pe,Sqr(x))),p),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),pn)),x),FreeQ(List(a,b,c,pd,pe,m,pn,p),x))),
ISetDelayed(Int(Times(Power(Plus(Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT),a_DEFAULT),pn_),Power(Plus(Times(x_,pi_DEFAULT),h_DEFAULT),m_DEFAULT),Power(Times(Sqrt(Plus(Times(g_DEFAULT,x_),f_)),Sqrt(Plus(Times(x_,pe_DEFAULT),pd_))),CN1)),x_Symbol),
    Condition(Plus(Times(Power(Plus(h,Times(pi,x)),m),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),Plus(pn,C1)),Power(Times(b,c,Sqrt(pd),Sqrt(Times(CN1,f)),Plus(pn,C1)),CN1)),Times(CN1,pi,m,Power(Times(b,c,Sqrt(pd),Sqrt(Times(CN1,f)),Plus(pn,C1)),CN1),Int(Times(Power(Plus(h,Times(pi,x)),Plus(m,Times(CN1,C1))),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),Plus(pn,C1))),x))),And(And(And(And(And(And(FreeQ(List(a,b,c,pd,pe,f,g,h,pi,m),x),ZeroQ(Plus(pe,Times(CN1,c,pd)))),ZeroQ(Plus(g,Times(c,f)))),PositiveQ(pd)),NegativeQ(f)),RationalQ(pn)),Less(pn,CN1)))),
ISetDelayed(Int(Times(Power(Plus(Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT),a_DEFAULT),pn_DEFAULT),Power(Plus(Times(x_,pi_DEFAULT),h_DEFAULT),m_DEFAULT),Power(Times(Sqrt(Plus(Times(g_DEFAULT,x_),f_)),Sqrt(Plus(Times(x_,pe_DEFAULT),pd_))),CN1)),x_Symbol),
    Condition(Times(Power(Times(Power(c,Plus(m,C1)),Sqrt(pd),Sqrt(Times(CN1,f))),CN1),Subst(Int(Times(Power(Plus(a,Times(b,x)),pn),Power(Plus(Times(c,h),Times(pi,Cosh(x))),m)),x),x,ArcCosh(Times(c,x)))),And(And(And(And(And(FreeQ(List(a,b,c,pd,pe,f,g,h,pi,pn),x),ZeroQ(Plus(pe,Times(CN1,c,pd)))),ZeroQ(Plus(g,Times(c,f)))),PositiveQ(pd)),NegativeQ(f)),IntegerQ(m)))),
ISetDelayed(Int(Times(Power(Plus(Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT),a_DEFAULT),pn_DEFAULT),Power(Plus(Times(x_,pi_DEFAULT),h_DEFAULT),m_DEFAULT),Power(Times(Sqrt(Plus(Times(g_DEFAULT,x_),f_)),Sqrt(Plus(Times(x_,pe_DEFAULT),pd_))),CN1)),x_Symbol),
    Condition(Times(Sqrt(Plus(CN1,Times(c,x))),Sqrt(Plus(C1,Times(c,x))),Power(Times(Sqrt(Plus(pd,Times(pe,x))),Sqrt(Plus(f,Times(g,x)))),CN1),Int(Times(Power(Plus(h,Times(pi,x)),m),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),pn),Power(Times(Sqrt(Plus(CN1,Times(c,x))),Sqrt(Plus(C1,Times(c,x)))),CN1)),x)),And(And(And(FreeQ(List(a,b,c,pd,pe,f,g,h,pi,m,pn),x),ZeroQ(Plus(pe,Times(CN1,c,pd)))),ZeroQ(Plus(g,Times(c,f)))),Not(And(PositiveQ(pd),NegativeQ(f)))))),
ISetDelayed(Int(Times(Power(Plus(Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT),a_DEFAULT),pn_DEFAULT),Power(Plus(Times(g_DEFAULT,x_),f_),p_DEFAULT),Power(Plus(Times(x_,pe_DEFAULT),pd_),p_DEFAULT),Power(Times(x_,pi_),m_)),x_Symbol),
    Condition(Times(Power(Times(pi,x),m),Power(Power(x,m),CN1),Int(Times(Power(x,m),Power(Plus(pd,Times(pe,x)),p),Power(Plus(f,Times(g,x)),p),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),pn)),x)),FreeQ(List(a,b,c,pd,pe,f,g,pi,m,pn,p),x))),
ISetDelayed(Int(Times(Power(Plus(Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT),a_DEFAULT),pn_DEFAULT),Power(Plus(Times(g_DEFAULT,x_),f_),p_DEFAULT),Power(Plus(Times(x_,pe_DEFAULT),pd_),p_DEFAULT),Power(Plus(Times(x_,pi_DEFAULT),h_DEFAULT),m_DEFAULT)),x_Symbol),
    Condition($(Defer($s("Int")),Times(Power(Plus(h,Times(pi,x)),m),Power(Plus(pd,Times(pe,x)),p),Power(Plus(f,Times(g,x)),p),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),pn)),x),FreeQ(List(a,b,c,pd,pe,f,g,h,pi,m,pn,p),x))),
ISetDelayed(Int(Times(Power(Plus(Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT),a_DEFAULT),pn_DEFAULT),Power(Plus(Times(g_DEFAULT,x_),f_DEFAULT),m_DEFAULT),Power(Plus(Times(Sqr(x_),pe_DEFAULT),pd_),CN1D2)),x_Symbol),
    Condition(Times(Sqrt(Plus(pd,Times(CN1,c,pd,x))),Sqrt(Plus(C1,Times(c,x))),Power(Plus(pd,Times(pe,Sqr(x))),CN1D2),Int(Times(Power(Plus(f,Times(g,x)),m),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),pn),Power(Times(Sqrt(Plus(pd,Times(CN1,c,pd,x))),Sqrt(Plus(C1,Times(c,x)))),CN1)),x)),And(FreeQ(List(a,b,c,pd,pe,f,g,m,pn),x),ZeroQ(Plus(Times(Sqr(c),pd),pe))))),
ISetDelayed(Int(Times(Power(Plus(Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT),a_DEFAULT),pn_DEFAULT),Power(Plus(Times(Sqr(x_),pe_DEFAULT),pd_DEFAULT),p_DEFAULT),Power(Times(g_,x_),m_)),x_Symbol),
    Condition(Times(Power(Times(g,x),m),Power(Power(x,m),CN1),Int(Times(Power(x,m),Power(Plus(pd,Times(pe,Sqr(x))),p),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),pn)),x)),FreeQ(List(a,b,c,pd,pe,g,m,pn,p),x))),
ISetDelayed(Int(Times(Power(Plus(Times(ArcCosh(Times(c_DEFAULT,x_)),b_DEFAULT),a_DEFAULT),pn_DEFAULT),Power(Plus(Times(g_DEFAULT,x_),f_DEFAULT),m_DEFAULT),Power(Plus(Times(Sqr(x_),pe_DEFAULT),pd_DEFAULT),p_DEFAULT)),x_Symbol),
    Condition($(Defer($s("Int")),Times(Power(Plus(f,Times(g,x)),m),Power(Plus(pd,Times(pe,Sqr(x))),p),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),pn)),x),FreeQ(List(a,b,c,pd,pe,f,g,m,pn,p),x)))
  );
}
