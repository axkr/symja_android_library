package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.AtomQ;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.Catch;
import static org.matheclipse.core.expression.F.CompoundExpression;
import static org.matheclipse.core.expression.F.Function;
import static org.matheclipse.core.expression.F.Head;
import static org.matheclipse.core.expression.F.ISetDelayed;
import static org.matheclipse.core.expression.F.If;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.MemberQ;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Part;
import static org.matheclipse.core.expression.F.Return;
import static org.matheclipse.core.expression.F.SameQ;
import static org.matheclipse.core.expression.F.Scan;
import static org.matheclipse.core.expression.F.Slot1;
import static org.matheclipse.core.expression.F.Throw;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.S.False;
import static org.matheclipse.core.expression.S.Log;
import static org.matheclipse.core.expression.S.Null;
import static org.matheclipse.core.expression.S.True;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ComplexFreeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ComplexNumberQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FractionalPowerFreeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FractionalPowerQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.HyperbolicQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LogQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.TrigQ;
import org.matheclipse.core.interfaces.IAST;

/**
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 * 
 */
class UtilityFunctions2 {
  public static IAST RULES = List(
      ISetDelayed(13, FractionalPowerFreeQ(u_),
          If(AtomQ(u), True, If(And(FractionalPowerQ(u), Not(AtomQ(Part(u, C1)))), False,
              Catch(CompoundExpression(
                  Scan(Function(If(FractionalPowerFreeQ(Slot1), Null, Throw(False))), u), True))))),
      ISetDelayed(14, ComplexFreeQ(u_),
          If(AtomQ(u), Not(ComplexNumberQ(u)),
              SameQ(Scan(Function(If(ComplexFreeQ(Slot1), Null, Return(False))), u), Null))),
      ISetDelayed(15, LogQ(u_), SameQ(Head(u), Log)),
      ISetDelayed(16, TrigQ(u_), MemberQ($s("§$trigfunctions"), If(AtomQ(u), u, Head(u)))),
      ISetDelayed(17, HyperbolicQ(u_),
          MemberQ($s("§$hyperbolicfunctions"), If(AtomQ(u), u, Head(u)))));
}
