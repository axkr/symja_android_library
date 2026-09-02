package org.matheclipse.core.builtin;

import java.util.Map;
import java.util.TreeMap;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * {@link S#FormulaData} - named scientific formulas, written as equations over
 * {@link S#QuantityVariable}.
 *
 * <p>
 * The catalogue here is a curated subset.
 */
public class FormulaDataFunctions {

  private static class Initializer {

    private static void init() {
      S.FormulaData.setEvaluator(new FormulaData());
    }
  }

  /**
   * The formula catalogue, built on first use.
   *
   * <p>
   * Built lazily rather than in a static initializer, because every entry evaluates {@code F.}
   * factory calls and the symbol table has to be up before that happens.
   */
  private static final class Catalogue {

    static final Map<String, IAST> FORMULAS = build();

    private static Map<String, IAST> build() {
      Map<String, IAST> formulas = new TreeMap<>();
      // electricity
      formulas.put("OhmsLaw", F.Equal(variable("V", "ElectricPotential"),
          F.Times(variable("I", "ElectricCurrent"), variable("R", "ElectricResistance"))));
      formulas.put("ElectricPower", F.Equal(variable("P", "Power"),
          F.Times(variable("V", "ElectricPotential"), variable("I", "ElectricCurrent"))));
      formulas.put("ElectricCharge", F.Equal(variable("Q", "ElectricCharge"),
          F.Times(variable("I", "ElectricCurrent"), variable("t", "Time"))));
      formulas.put("Capacitance", F.Equal(variable("C", "Capacitance"),
          F.Divide(variable("Q", "ElectricCharge"), variable("V", "ElectricPotential"))));
      // mechanics
      formulas.put("NewtonsSecondLaw", F.Equal(variable("F", "Force"),
          F.Times(variable("m", "Mass"), variable("a", "Acceleration"))));
      formulas.put("Momentum", F.Equal(variable("p", "Momentum"),
          F.Times(variable("m", "Mass"), variable("v", "Speed"))));
      formulas.put("KineticEnergy", F.Equal(variable("E", "Energy"),
          F.Times(F.C1D2, variable("m", "Mass"), F.Sqr(variable("v", "Speed")))));
      formulas.put("Work", F.Equal(variable("W", "Energy"),
          F.Times(variable("F", "Force"), variable("d", "Length"))));
      formulas.put("AverageSpeed", F.Equal(variable("v", "Speed"),
          F.Divide(variable("d", "Length"), variable("t", "Time"))));
      formulas.put("Pressure", F.Equal(variable("P", "Pressure"),
          F.Divide(variable("F", "Force"), variable("A", "Area"))));
      // waves
      formulas.put("WaveSpeed", F.Equal(variable("v", "Speed"),
          F.Times(variable("f", "Frequency"), variable("lambda", "Length"))));
      return formulas;
    }

    private static IExpr variable(String identifier, String physicalQuantity) {
      return F.binaryAST2(S.QuantityVariable, F.stringx(identifier), F.stringx(physicalQuantity));
    }
  }

  /**
   * <code>FormulaData(name)</code> - the equation of the named formula.
   *
   * <p>
   * <code>FormulaData()</code> lists the known names, <code>FormulaData(name, {var -> quantity,
   * ...})</code> substitutes those values and solves for the one that is left, and
   * <code>FormulaData(name, "property")</code> reads a property of the formula.
   */
  private static final class FormulaData extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        IASTAppendable names = F.ListAlloc(Catalogue.FORMULAS.size());
        for (String name : Catalogue.FORMULAS.keySet()) {
          names.append(F.stringx(name));
        }
        return names;
      }
      IExpr arg1 = ast.arg1();
      if (!arg1.isString()) {
        return F.NIL;
      }
      if (arg1.isString("Properties")) {
        return F.list(F.stringx("Equation"), F.stringx("QuantityVariableNames"),
            F.stringx("QuantityVariables"));
      }
      IAST equation = Catalogue.FORMULAS.get(arg1.toString());
      if (equation == null) {
        return F.NIL;
      }
      if (ast.isAST1()) {
        return equation;
      }

      IExpr arg2 = ast.arg2();
      if (arg2.isString()) {
        return property(equation, arg2.toString());
      }
      IAST rules =
          arg2.isRuleAST() ? F.List(arg2) : (arg2.isListOfRules(false) ? (IAST) arg2 : F.NIL);
      if (rules.isNIL()) {
        return F.NIL;
      }
      return solve(equation, rules, engine);
    }

    /** A named property of a formula. */
    private static IExpr property(IAST equation, String name) {
      IAST variables = quantityVariables(equation);
      switch (name) {
        case "Equation":
          return equation;
        case "QuantityVariables":
          return variables;
        case "QuantityVariableNames":
          return variables.mapThread(F.unaryAST1(S.QuantityVariableIdentifier, F.Slot1), 1);
        default:
          return F.NIL;
      }
    }

    /**
     * Substitute the given quantities and solve for the single variable that is left.
     *
     * <p>
     * The solving happens on plain magnitudes, not on quantities: each given value is expressed in
     * its own variable's canonical unit, the equation is solved over ordinary numbers, and the
     * canonical unit of the unknown is attached to the answer. Solving the quantity equation
     * directly is not an option - <code>Solve</code> cannot do it.
     *
     * @return the solved equation, or the substituted equation when the unknown is not unique
     */
    private static IExpr solve(IAST equation, IAST rules, EvalEngine engine) {
      IAST variables = quantityVariables(equation);
      IASTAppendable substitutions = F.ListAlloc(rules.size());
      IASTAppendable unknowns = F.ListAlloc(variables.size());

      for (int i = 1; i < variables.size(); i++) {
        IExpr variable = variables.get(i);
        IExpr identifier = engine.evaluate(F.unaryAST1(S.QuantityVariableIdentifier, variable));
        IExpr given = F.NIL;
        for (int j = 1; j < rules.size(); j++) {
          IAST rule = (IAST) rules.get(j);
          if (rule.arg1().equals(identifier)) {
            given = rule.arg2();
            break;
          }
        }
        if (given.isNIL()) {
          unknowns.append(variable);
          continue;
        }
        IExpr unit = engine.evaluate(F.unaryAST1(S.QuantityVariableCanonicalUnit, variable));
        IExpr magnitude =
            given.isQuantity() ? engine.evaluate(F.QuantityMagnitude(given, unit)) : given;
        if (!magnitude.isNumericFunction(true)) {
          return F.NIL;
        }
        substitutions.append(F.Rule(variable, magnitude));
      }

      IExpr substituted = engine.evaluate(F.ReplaceAll(equation, substitutions));
      if (unknowns.argSize() != 1) {
        return substituted;
      }

      IExpr unknown = unknowns.arg1();
      ISymbol x = F.Dummy("x");
      IExpr solutions = engine.evaluate(F.Solve(F.subst(substituted, unknown, x), x));
      if (!solutions.isListOfLists() || solutions.argSize() != 1) {
        return substituted;
      }
      IAST solution = (IAST) ((IAST) solutions).arg1();
      if (solution.argSize() != 1 || !solution.arg1().isRuleAST()) {
        return substituted;
      }
      IExpr value = ((IAST) solution.arg1()).arg2();
      IExpr unit = engine.evaluate(F.unaryAST1(S.QuantityVariableCanonicalUnit, unknown));
      return F.Equal(unknown, engine.evaluate(F.Quantity(value, unit)));
    }

    /** The {@link S#QuantityVariable} expressions of a formula, in the order they appear. */
    private static IAST quantityVariables(IAST equation) {
      IASTAppendable variables = F.ListAlloc(4);
      equation.forAll(x -> collect(x, variables), 0);
      return variables;
    }

    private static boolean collect(IExpr expr, IASTAppendable variables) {
      if (expr.isAST(S.QuantityVariable)) {
        if (!variables.contains(expr)) {
          variables.append(expr);
        }
        return true;
      }
      if (expr.isAST()) {
        ((IAST) expr).forAll(x -> collect(x, variables), 0);
      }
      return true;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private FormulaDataFunctions() {}
}
