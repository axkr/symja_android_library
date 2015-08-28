package org.matheclipse.core.integrate.rubi45;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi45.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi45.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class IntRules119 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,ArcCosh(Times(c_DEFAULT,x_)))),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Times(Sqrt(Plus(d,Times(e,Sqr(x)))),Power(Times(Sqrt(Plus(d,Times(CN1,c,d,x))),Sqrt(Plus(C1,Times(c,x)))),-1),Int(Times(Power(x,m),Power(Plus(d,Times(CN1,c,d,x)),p),Power(Plus(C1,Times(c,x)),p),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),n)),x)),And(And(FreeQ(List(a,b,c,d,e,m,n),x),ZeroQ(Plus(Times(Sqr(c),d),e))),PositiveIntegerQ(Plus(p,C1D2))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,ArcCosh(Times(c_DEFAULT,x_)))),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Times(Sqrt(Plus(d,Times(CN1,c,d,x))),Sqrt(Plus(C1,Times(c,x))),Power(Plus(d,Times(e,Sqr(x))),CN1D2),Int(Times(Power(x,m),Power(Plus(d,Times(CN1,c,d,x)),p),Power(Plus(C1,Times(c,x)),p),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),n)),x)),And(And(FreeQ(List(a,b,c,d,e,m,n),x),ZeroQ(Plus(Times(Sqr(c),d),e))),NegativeIntegerQ(Plus(p,Negate(C1D2)))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,ArcCosh(Times(c_DEFAULT,x_)))),n_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Int(ExpandIntegrand(Times(Power(x,m),Power(Plus(d,Times(e,Sqr(x))),p),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),n)),x),x),And(FreeQ(List(a,b,c,d,e,m,n),x),PositiveIntegerQ(p)))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,ArcCosh(Times(c_DEFAULT,x_)))),n_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(x,m),Power(Plus(d,Times(e,Sqr(x))),p),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),n)),x),FreeQ(List(a,b,c,d,e,m,n,p),x))),
ISetDelayed(Int(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,ArcCosh(Times(c_DEFAULT,x_)))),n_),Power(Plus(h_DEFAULT,Times(i_DEFAULT,x_)),m_DEFAULT),Power(Times(Sqrt(Plus(d_,Times(e_DEFAULT,x_))),Sqrt(Plus(f_,Times(g_DEFAULT,x_)))),-1)),x_Symbol),
    Condition(Plus(Times(Power(Plus(h,Times(i,x)),m),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),Plus(n,C1)),Power(Times(b,c,Sqrt(d),Sqrt(Negate(f)),Plus(n,C1)),-1)),Times(CN1,i,m,Power(Times(b,c,Sqrt(d),Sqrt(Negate(f)),Plus(n,C1)),-1),Int(Times(Power(Plus(h,Times(i,x)),Plus(m,Negate(C1))),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),Plus(n,C1))),x))),And(And(And(And(And(And(FreeQ(List(a,b,c,d,e,f,g,h,i,m),x),ZeroQ(Plus(e,Times(CN1,c,d)))),ZeroQ(Plus(g,Times(c,f)))),PositiveQ(d)),NegativeQ(f)),RationalQ(n)),Less(n,CN1)))),
ISetDelayed(Int(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,ArcCosh(Times(c_DEFAULT,x_)))),n_DEFAULT),Power(Plus(h_DEFAULT,Times(i_DEFAULT,x_)),m_DEFAULT),Power(Times(Sqrt(Plus(d_,Times(e_DEFAULT,x_))),Sqrt(Plus(f_,Times(g_DEFAULT,x_)))),-1)),x_Symbol),
    Condition(Times(Power(Times(Power(c,Plus(m,C1)),Sqrt(d),Sqrt(Negate(f))),-1),Subst(Int(Times(Power(Plus(a,Times(b,x)),n),Power(Plus(Times(c,h),Times(i,Cosh(x))),m)),x),x,ArcCosh(Times(c,x)))),And(And(And(And(And(FreeQ(List(a,b,c,d,e,f,g,h,i,n),x),ZeroQ(Plus(e,Times(CN1,c,d)))),ZeroQ(Plus(g,Times(c,f)))),PositiveQ(d)),NegativeQ(f)),IntegerQ(m)))),
ISetDelayed(Int(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,ArcCosh(Times(c_DEFAULT,x_)))),n_DEFAULT),Power(Plus(h_DEFAULT,Times(i_DEFAULT,x_)),m_DEFAULT),Power(Times(Sqrt(Plus(d_,Times(e_DEFAULT,x_))),Sqrt(Plus(f_,Times(g_DEFAULT,x_)))),-1)),x_Symbol),
    Condition(Times(Sqrt(Plus(CN1,Times(c,x))),Sqrt(Plus(C1,Times(c,x))),Power(Times(Sqrt(Plus(d,Times(e,x))),Sqrt(Plus(f,Times(g,x)))),-1),Int(Times(Power(Plus(h,Times(i,x)),m),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),n),Power(Times(Sqrt(Plus(CN1,Times(c,x))),Sqrt(Plus(C1,Times(c,x)))),-1)),x)),And(And(And(FreeQ(List(a,b,c,d,e,f,g,h,i,m,n),x),ZeroQ(Plus(e,Times(CN1,c,d)))),ZeroQ(Plus(g,Times(c,f)))),Not(And(PositiveQ(d),NegativeQ(f)))))),
ISetDelayed(Int(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,ArcCosh(Times(c_DEFAULT,x_)))),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),p_DEFAULT),Power(Plus(f_,Times(g_DEFAULT,x_)),p_DEFAULT),Power(Times(i_,x_),m_)),x_Symbol),
    Condition(Times(Power(Times(i,x),m),Power(Power(x,m),-1),Int(Times(Power(x,m),Power(Plus(d,Times(e,x)),p),Power(Plus(f,Times(g,x)),p),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),n)),x)),FreeQ(List(a,b,c,d,e,f,g,i,m,n,p),x))),
ISetDelayed(Int(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,ArcCosh(Times(c_DEFAULT,x_)))),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),p_DEFAULT),Power(Plus(f_,Times(g_DEFAULT,x_)),p_DEFAULT),Power(Plus(h_DEFAULT,Times(i_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Plus(h,Times(i,x)),m),Power(Plus(d,Times(e,x)),p),Power(Plus(f,Times(g,x)),p),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),n)),x),FreeQ(List(a,b,c,d,e,f,g,h,i,m,n,p),x))),
ISetDelayed(Int(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,ArcCosh(Times(c_DEFAULT,x_)))),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Times(Sqrt(Plus(d,Times(CN1,c,d,x))),Sqrt(Plus(C1,Times(c,x))),Power(Plus(d,Times(e,Sqr(x))),CN1D2),Int(Times(Power(Plus(f,Times(g,x)),m),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),n),Power(Times(Sqrt(Plus(d,Times(CN1,c,d,x))),Sqrt(Plus(C1,Times(c,x)))),-1)),x)),And(FreeQ(List(a,b,c,d,e,f,g,m,n),x),ZeroQ(Plus(Times(Sqr(c),d),e))))),
ISetDelayed(Int(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,ArcCosh(Times(c_DEFAULT,x_)))),n_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT),Power(Times(g_,x_),m_)),x_Symbol),
    Condition(Times(Power(Times(g,x),m),Power(Power(x,m),-1),Int(Times(Power(x,m),Power(Plus(d,Times(e,Sqr(x))),p),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),n)),x)),FreeQ(List(a,b,c,d,e,g,m,n,p),x))),
ISetDelayed(Int(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,ArcCosh(Times(c_DEFAULT,x_)))),n_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Plus(f,Times(g,x)),m),Power(Plus(d,Times(e,Sqr(x))),p),Power(Plus(a,Times(b,ArcCosh(Times(c,x)))),n)),x),FreeQ(List(a,b,c,d,e,f,g,m,n,p),x)))
  );
}
