package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$ps;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.Block;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.Catch;
import static org.matheclipse.core.expression.F.CompoundExpression;
import static org.matheclipse.core.expression.F.Denominator;
import static org.matheclipse.core.expression.F.Do;
import static org.matheclipse.core.expression.F.Equal;
import static org.matheclipse.core.expression.F.Function;
import static org.matheclipse.core.expression.F.Head;
import static org.matheclipse.core.expression.F.ISetDelayed;
import static org.matheclipse.core.expression.F.If;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.Length;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Module;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Part;
import static org.matheclipse.core.expression.F.Reap;
import static org.matheclipse.core.expression.F.Return;
import static org.matheclipse.core.expression.F.SameQ;
import static org.matheclipse.core.expression.F.Scan;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Slot1;
import static org.matheclipse.core.expression.F.Sow;
import static org.matheclipse.core.expression.F.Throw;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.list;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.False;
import static org.matheclipse.core.expression.S.Null;
import static org.matheclipse.core.expression.S.Rational;
import static org.matheclipse.core.expression.S.True;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EveryQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.HalfIntegerQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntHide;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Map2;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ReapList;
import org.matheclipse.core.interfaces.IAST;

/**
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 * 
 */
class UtilityFunctions0 {
  public static IAST RULES =
      List(
          ISetDelayed(1, IntHide(u_, x_Symbol),
              Block(
                  list(Set($s("§$showsteps"), False),
                      Set($s("§$stepcounter"), Null)),
                  Integrate(u, x))),
          ISetDelayed(
              2, EveryQ($p("func"), $p(
                  "lst")),
              Catch(
                  CompoundExpression(
                      Scan(Function(If($($s("func"), Slot1), Null, Throw(False))),
                          $s("lst")),
                      True))),
          ISetDelayed(
              3, Map2($p("func"), $p("lst1"), $p(
                  "lst2")),
              Module(
                  list($s(
                      "ii")),
                  ReapList(
                      Do(Sow($($s("func"), Part($s("lst1"), $s("ii")), Part($s("lst2"), $s("ii")))),
                          list($s("ii"), Length($s("lst1"))))))),
          ISetDelayed(4, ReapList(u_),
              With(
                  list(Set($s("lst"),
                      Part(Reap(u), C2))),
                  If(SameQ($s("lst"), List()), $s("lst"), Part($s("lst"), C1)))),
          ISetDelayed(5, HalfIntegerQ($ps("u")),
              SameQ(
                  Scan(Function(If(And(SameQ(Head(Slot1), Rational), Equal(Denominator(Slot1), C2)),
                      Null, Return(False))), list(u)),
                  Null)),
          ISetDelayed(6, RationalQ($ps("u")),
              SameQ(Scan(
                  Function(
                      If(Or(IntegerQ(Slot1), SameQ(Head(Slot1), Rational)), Null, Return(False))),
                  list(u)), Null)));
}
