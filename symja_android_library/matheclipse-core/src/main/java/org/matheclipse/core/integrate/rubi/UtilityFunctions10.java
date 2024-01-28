package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.Coefficient;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Denominator;
import static org.matheclipse.core.expression.F.First;
import static org.matheclipse.core.expression.F.ISetDelayed;
import static org.matheclipse.core.expression.F.If;
import static org.matheclipse.core.expression.F.Im;
import static org.matheclipse.core.expression.F.Less;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Numerator;
import static org.matheclipse.core.expression.F.Part;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Re;
import static org.matheclipse.core.expression.F.Rest;
import static org.matheclipse.core.expression.F.SameQ;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Simplify;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.Together;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.list;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.S.Integer;
import static org.matheclipse.core.expression.S.Rational;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Coeff;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ComplexNumberQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LeadBase;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LeadDegree;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LeadFactor;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LeadTerm;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PowerQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ProductQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RemainingFactors;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RemainingTerms;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SumQ;
import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class UtilityFunctions10 { 
  public static IAST RULES = List( 
ISetDelayed(99,Coeff($p("expr"),$p("form"),n_),
    With(list(Set($s("coef1"),Coefficient($s("expr"),$s("form"),n)),Set($s("coef2"),Coefficient(Together($s("expr")),$s("form"),n))),If(SameQ(Simplify(Subtract($s("coef1"),$s("coef2"))),C0),$s("coef1"),$s("coef2")))),
ISetDelayed(100,LeadTerm(u_),
    If(SumQ(u),First(u),u)),
ISetDelayed(101,RemainingTerms(u_),
    If(SumQ(u),Rest(u),C0)),
ISetDelayed(102,LeadFactor(u_),
    If(ProductQ(u),LeadFactor(First(u)),If(And(ComplexNumberQ(u),SameQ(Re(u),C0)),If(SameQ(Im(u),C1),u,LeadFactor(Im(u))),u))),
ISetDelayed(103,RemainingFactors(u_),
    If(ProductQ(u),Times(RemainingFactors(First(u)),Rest(u)),If(And(ComplexNumberQ(u),SameQ(Re(u),C0)),If(SameQ(Im(u),C1),C1,Times(CI,RemainingFactors(Im(u)))),C1))),
ISetDelayed(104,LeadBase(u_),
    With(list(Set(v,LeadFactor(u))),If(PowerQ(v),Part(v,C1),v))),
ISetDelayed(105,LeadDegree(u_),
    With(list(Set(v,LeadFactor(u))),If(PowerQ(v),Part(v,C2),C1))),
ISetDelayed(106,$($s("§numer"),Power($p(m, Integer),$p(n,Rational))),
    Condition(C1,Less(n,C0))),
ISetDelayed(107,$($s("§numer"),Times(u_,v_)),
    Times($($s("§numer"),u),$($s("§numer"),v))),
ISetDelayed(108,$($s("§numer"),u_),
    Numerator(u)),
ISetDelayed(109,$($s("§denom"),Power($p(m, Integer),$p(n,Rational))),
    Condition(Power(m,Negate(n)),Less(n,C0))),
ISetDelayed(110,$($s("§denom"),Times(u_,v_)),
    Times($($s("§denom"),u),$($s("§denom"),v))),
ISetDelayed(111,$($s("§denom"),u_),
          Denominator(u))
  );
}
