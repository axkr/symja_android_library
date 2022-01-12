package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$rubi;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Equal;
import static org.matheclipse.core.expression.F.Head;
import static org.matheclipse.core.expression.F.ISetDelayed;
import static org.matheclipse.core.expression.F.If;
import static org.matheclipse.core.expression.F.Insert;
import static org.matheclipse.core.expression.F.Length;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.MemberQ;
import static org.matheclipse.core.expression.F.SameQ;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.Unevaluated;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.S.Integrate;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FixRhsIntRule;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class UtilityFunctions39 { 
  public static IAST RULES = List( 
ISetDelayed(708,FixRhsIntRule(Times(a_,u_),x_),
    Condition(Dist(a,u,x),MemberQ(List(Integrate,$rubi("Subst")),Head(Unevaluated(u))))),
ISetDelayed(709,FixRhsIntRule(u_,x_),
          If(And(SameQ(Head(Unevaluated(u)), $rubi("Dist")), Equal(Length(Unevaluated(u)), C2)),
              Insert(Unevaluated(u), x, C3),
              If(MemberQ(List(Integrate, $rubi("Unintegrable"), $rubi("CannotIntegrate"),
                  $rubi("Subst"), $rubi("Simp"), $rubi("Dist")), Head(Unevaluated(u))), u,
                  Simp(u, x))))
  );
}
