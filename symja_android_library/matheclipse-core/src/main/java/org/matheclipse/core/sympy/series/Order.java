package org.matheclipse.core.sympy.series;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Utility class containing a static factory method to create Order expressions, directly
 * translating the logic from SymPy's Order.__new__.
 */
public class Order {

  private static final Comparator<IAST> VAR_POINT_COMPARATOR =
      Comparator.comparing(pair -> (ISymbol) pair.arg1());

  /**
   * Creates an Order expression (IExpr) based on SymPy's Order constructor logic.
   *
   * @param inputExpr The main expression.
   * @param engine The EvalEngine for evaluating sub-expressions (Expand, Series, etc.).
   * @param args Variable arguments mimicking Python's *args. Can contain: - Nothing (defaults
   *        extracted from inputExpr) - ISymbol objects (representing variables, point defaults to
   *        0) - IAST objects of the form F.List(ISymbol, IExpr) (variable-point pairs) - A single
   *        IAST List containing the above (e.g., F.List(v1, v2) or F.List(F.List(v1,p1), ...))
   * @return The resulting Order IExpr (e.g., F.Order(leadTerm, F.List(F.List(v,p),...))) or S.NaN
   *         or F.C0.
   * @throws IllegalArgumentException for invalid arguments or unsupported scenarios.
   */
  public static IExpr create(IExpr inputExpr, IExpr... args) {
    EvalEngine engine = EvalEngine.get();
    // --- 0. Initial Evaluation ---
    IExpr expr = engine.evaluate(inputExpr); // Equivalent to initial sympify

    Map<ISymbol, IExpr> varPointMap = new LinkedHashMap<>();
    List<ISymbol> variables;
    List<IExpr> points;

    // --- 1. Argument Parsing & Defaulting (Mimics Python logic) ---
    List<IExpr> argList = List.of(args); // Easier processing

    if (argList.isEmpty()) {
      if (expr.isAST(S.Order)) {
        // Inherit from inner Order if no args provided
        varPointMap = listToMap(getVariablePointList(expr));
      } else {
        // Default: all free symbols at point 0
        Set<ISymbol> freeSymbols = expr.variables();
        for (ISymbol sym : freeSymbols) {
          varPointMap.put(sym, F.C0);
        }
      }
    } else {
      // --- Parse explicit variables/points ---
      // Check if the first arg is a sequence (List in Symja) containing the actual args
      if (argList.size() == 1 && argList.get(0).isList()) {
        IAST argListAst = (IAST) argList.get(0);
        // Treat the elements of the inner list as the arguments
        argList = new ArrayList<>();
        for (int i = 1; i < argListAst.size(); i++) {
          argList.add(argListAst.get(i));
        }
      }

      if (!argList.isEmpty()) {
        IExpr firstRealArg = argList.get(0);
        boolean isListOfPairs = firstRealArg.isList() && firstRealArg.size() == 3;
        // Note: Python's is_sequence is broader; we check for specific List structure

        if (isListOfPairs) {
          // Format: (List(v1, p1), List(v2, p2), ...)
          for (IExpr item : argList) {
            if (item.isList() && item.size() == 3) {
              IAST pairList = (IAST) item;
              IExpr vExpr = engine.evaluate(pairList.arg1());
              IExpr pExpr = engine.evaluate(pairList.arg2());
              if (!vExpr.isSymbol()) {
                throw new IllegalArgumentException(
                    "Expected symbol as variable, got: " + vExpr + " in " + pairList);
              }
              ISymbol var = (ISymbol) vExpr;
              // Check uniqueness later
              varPointMap.put(var, pExpr);
            } else {
              throw new IllegalArgumentException("Expected List(variable, point), got: " + item);
            }
          }
        } else {
          // Format: (v1, v2, ...)
          for (IExpr item : argList) {
            // Evaluate symbol in case it's wrapped or needs simplification? No, sympify happens at
            // start.
            if (item instanceof ISymbol) {
              // Check uniqueness later
              varPointMap.put((ISymbol) item, F.C0); // Default point is 0
            } else if (item.isSymbol()) {
              // Handle case where it's IExpr but actually a symbol
              ISymbol var = (ISymbol) item;
              varPointMap.put(var, F.C0);
            } else {
              throw new IllegalArgumentException("Expected symbol as variable, got: "
                  + item.getClass().getSimpleName() + " -> " + item);
            }
          }
        }
      }
    }

    // --- 2. Variable Validation ---
    variables = new ArrayList<>(varPointMap.keySet());
    points = new ArrayList<>(varPointMap.values());

    // Check all variables are symbols (redundant if parsing is strict, but good practice)
    for (ISymbol v : variables) {
      if (v == null)
        throw new IllegalArgumentException("Internal error: null variable encountered.");
      // instanceof ISymbol check was implicitly done during parsing
    }

    // Check uniqueness
    if (new HashSet<>(variables).size() != variables.size()) {
      throw new IllegalArgumentException(
          "Variables are supposed to be unique symbols, got duplicates in: " + variables);
    }

    // --- 3. O(Order(...)) Merging ---
    if (expr.isAST(S.Order)) {
      Map<ISymbol, IExpr> innerMap = listToMap(getVariablePointList(expr));
      Map<ISymbol, IExpr> currentMap = new LinkedHashMap<>(varPointMap); // Explicit args parsed
                                                                         // above
      Map<ISymbol, IExpr> mergedMap = new LinkedHashMap<>(innerMap); // Start with inner

      for (Map.Entry<ISymbol, IExpr> entry : currentMap.entrySet()) {
        ISymbol v = entry.getKey();
        IExpr p = entry.getValue();
        if (mergedMap.containsKey(v)) {
          if (!p.equals(mergedMap.get(v))) {
            // SymPy raises NotImplementedError
            throw new UnsupportedOperationException(
                "Mixing Order at different points for variable " + v + " is not supported.");
          }
        } else {
          mergedMap.put(v, p);
        }
      }

      if (mapsEqual(innerMap, mergedMap)) {
        return expr; // Return the original inner Order expression if no change
      }

      // Update state based on the merged map
      varPointMap = mergedMap;
      variables = new ArrayList<>(varPointMap.keySet());
      points = new ArrayList<>(varPointMap.values());
      expr = getExpr(expr); // Use the expression from the inner Order
    }

    // --- 4. Handle Base Cases & Validate Points ---
    if (expr.isNaN()) {
      return S.Indeterminate;
    }
    // O(0, ...) handled after potential variable processing

    // Check points don't contain variables (any(x in p.free_symbols for x in variables for p in
    // point))
    Set<ISymbol> allVarsSet = new HashSet<>(variables);
    for (IExpr p : points) {
      Set<ISymbol> pointVars = p.variables();
      if (pointVars.stream().anyMatch(allVarsSet::contains)) {
        throw new IllegalArgumentException(
            "Limit point " + p + " cannot contain any Order variable from " + variables);
      }
    }

    IExpr leadingTermExpr = expr; // Start iterative refinement from here

    // --- 5. Point Transformation & Leading Term Calculation ---
    if (!variables.isEmpty()) {
      // Check for uniform point (SymPy limitation)
      IExpr point = points.get(0);
      for (int i = 1; i < points.size(); i++) {
        if (!points.get(i).equals(point)) {
          throw new UnsupportedOperationException(
              "Multivariable orders at different points (" + points + ") are not supported.");
        }
      }

      Map<IExpr, IExpr> subsRule = new HashMap<>();
      Map<IExpr, IExpr> revSubsRule = new HashMap<>();
      List<ISymbol> effectiveVars = new ArrayList<>(variables); // Vars used in limit calc
      IExpr effectivePoint = point; // Point used in limit calc
      List<IExpr> effectivePointsList = points; // used for pts tuple later

      // Create substitution rules
      if (point.isInfinity() || point.equals(F.ComplexInfinity)) {
        effectivePoint = F.C0;
        effectivePointsList = Collections.nCopies(variables.size(), F.C0);
        for (ISymbol v : variables) {
          ISymbol dummy = F.Dummy();
          subsRule.put(v, F.Power(dummy, F.CN1));
          revSubsRule.put(dummy, F.Power(v, F.CN1));
        }
        effectiveVars =
            revSubsRule.keySet().stream().map(k -> (ISymbol) k).collect(Collectors.toList());
      } else if (point.isNegativeInfinity()) {
        effectivePoint = F.C0;
        effectivePointsList = Collections.nCopies(variables.size(), F.C0);
        for (ISymbol v : variables) {
          ISymbol dummy = F.Dummy();
          subsRule.put(v, F.Negate(F.Power(dummy, F.CN1)));
          revSubsRule.put(dummy, F.Negate(F.Power(v, F.CN1)));
        }
        effectiveVars =
            revSubsRule.keySet().stream().map(k -> (ISymbol) k).collect(Collectors.toList());
      } else if (!point.isZero()) {
        effectivePoint = F.C0;
        effectivePointsList = Collections.nCopies(variables.size(), F.C0);
        for (ISymbol v : variables) {
          ISymbol dummy = F.Dummy();
          subsRule.put(v, F.Plus(dummy, point));
          // SymPy uses (v - p).together() - F.Together might be needed?
          // Using simple subtraction for now.
          revSubsRule.put(dummy, F.Subtract(v, point));
        }
        effectiveVars =
            revSubsRule.keySet().stream().map(k -> (ISymbol) k).collect(Collectors.toList());
      }
      // else: point is 0, no substitution needed

      // Apply substitution if needed
      if (!subsRule.isEmpty()) {
        IASTAppendable ruleList = F.ListAlloc(subsRule.size());
        subsRule.forEach((k, v) -> ruleList.append(F.Rule(k, v)));
        leadingTermExpr = leadingTermExpr.replaceAll(ruleList);
      }

      // Pre-processing before loop
      if (leadingTermExpr.isPlus()) {
        leadingTermExpr = engine.evaluate(F.Factor(leadingTermExpr)); // factor()
      }
      if (variables.size() > 1) {
        leadingTermExpr = engine.evaluate(F.ExpandAll(leadingTermExpr)); // expand()
      }

      // Iterative Leading Term Calculation (Mimicking SymPy's while loop)
      IExpr currentExpr = leadingTermExpr;
      IExpr previousExpr = null;
      int iterations = 0;
      final int MAX_ITERATIONS = 10; // Safety break

      List<ISymbol> argsForLeadTerm = effectiveVars; // Use effective (dummy) vars for calculation

      while (!currentExpr.equals(previousExpr) && iterations < MAX_ITERATIONS) {
        previousExpr = currentExpr;
        iterations++;

        if (currentExpr.isPlus()) {
          // TODO --- Approximation of extract_leading_order ---
          // SymPy's version is complex. It finds the leading terms of the summands.
          // Simple approach: Just find the leading term of the whole sum.
          // This matches the evaluator logic but is NOT a direct translation of
          // extract_leading_order.
          // try {
          // currentExpr = calculateLeadingTermApproximation(currentExpr, argsForLeadTerm,
          // effectivePoint, engine);
          // } catch (LimitException | ArithmeticException e) {
          // System.err.println("Order.create Warning: Error calculating leading term for sum "
          // + currentExpr + ": " + e.getMessage());
          // break; // Cannot simplify further
          // }

        } else if (!currentExpr.isZero()) {
          // TODO --- Approximation of as_leading_term ---
          // try {
          // currentExpr = calculateLeadingTermApproximation(currentExpr, argsForLeadTerm,
          // effectivePoint, engine);
          // } catch (LimitException | ArithmeticException e) {
          // // SymPy has complex fallback for PoleError based on Function types.
          // // We can't easily replicate that. We just log and break.
          // System.err.println("Order.create Warning: Error calculating leading term for "
          // + currentExpr + ": " + e.getMessage());
          // break; // Cannot simplify further
          // }
        }

        // --- Remove independent factors & Expand ---
        if (!currentExpr.isZero()) {
          // TODO: Equivalent to expr.as_independent(*args, as_Add=False)[1]
          // currentExpr = currentExpr.getDependentPart(argsForLeadTerm);

          // Equivalent to expand_power_base, expand_log (using general expansion)
          currentExpr = engine.evaluate(F.ExpandAll(currentExpr));

          // SymPy's specific power simplification (x**p * (-x)**q) is omitted.
          // We rely on Symja's ExpandAll and simplification engine.
        }

        if (currentExpr.isZero())
          break;
      } // End while loop

      if (iterations == MAX_ITERATIONS) {
        System.err
            .println("Order.create Warning: Max iterations reached simplifying leading term for "
                + leadingTermExpr);
      }

      leadingTermExpr = currentExpr; // Result of the iterative process

      // Apply reverse substitution if needed
      if (!revSubsRule.isEmpty()) {
        IASTAppendable revRuleList = F.ListAlloc(revSubsRule.size());
        revSubsRule.forEach((k, v) -> revRuleList.append(F.Rule(k, v)));
        leadingTermExpr = leadingTermExpr.replaceAll(revRuleList);
      }
    } // End if variables not empty

    // --- 6. Final Cleanup ---
    if (leadingTermExpr.isAST(S.Order)) {
      // Should not happen if O(O(...)) merging worked, but safeguard
      leadingTermExpr = getExpr(leadingTermExpr);
    }

    if (leadingTermExpr.isZero()) {
      return F.C0;
    }

    // If expression no longer depends on variables, make it O(1)
    // (if not expr.has(*variables) and not expr.is_zero: expr = S.One)
    boolean dependsOnVars = false;
    if (!variables.isEmpty()) {
      Set<ISymbol> termVars = leadingTermExpr.variables();
      dependsOnVars = variables.stream().anyMatch(termVars::contains);
      if (!dependsOnVars) {
        leadingTermExpr = F.C1;
      }
    } else {
      // Case where initial args were empty or O(expr, List())
      if (!(leadingTermExpr.isReal() || leadingTermExpr.isConstantAttribute())) {
        leadingTermExpr = F.C1; // e.g. O(y) becomes O(1)
      } else if (!leadingTermExpr.isOne()) {
        leadingTermExpr = F.C1; // e.g. O(5) becomes O(1)
      }
    }


    // --- 7. Construct Final Order Object ---
    // Create the canonical List(List(v,p),...) sorted by variable name
    IAST finalVarPointList = mapToList(varPointMap);

    // Mimics: obj = Expr.__new__(cls, *args) where args = (expr,) + Tuple(*zip(variables, point))
    return F.binaryAST2(S.Order, leadingTermExpr, finalVarPointList);
  }


  /**
   * Approximates SymPy's as_leading_term / extract_leading_order using Symja's LeadTerm and Series
   * functions. Tries LeadTerm, falls back to Series if needed. This is an INTERPRETATION, not a
   * direct equivalent.
   */
  // private static IExpr calculateLeadingTermApproximation(IExpr expr, List<ISymbol> effectiveVars,
  // IExpr effectivePoint, EvalEngine engine) throws LimitException, ArithmeticException {
  // if (expr.isZero())
  // return F.C0;
  // if (effectiveVars.isEmpty())
  // return expr;
  //
  // IExpr result = expr;
  // for (ISymbol v : effectiveVars) {
  // IExpr leadTerm = F.NIL;
  // try {
  // leadTerm = engine.evaluate(F.LeadTerm(result, v, effectivePoint));
  // } catch (Exception e) { // Catch broader exceptions from LeadTerm if necessary
  // System.err.println("Order.create Warning: F.LeadTerm failed for " + result + ", var " + v
  // + ": " + e.getMessage());
  // leadTerm = F.NIL; // Ensure it's NIL on failure
  // }
  //
  // if (leadTerm != null && leadTerm != F.NIL) {
  // if (leadTerm.isZero() && !result.isZero()) {
  // // LeadTerm returning 0 for non-zero expr might mean higher order. Try Series.
  // leadTerm = getLeadingTermFromSeriesApproximation(result, v, effectivePoint, engine);
  // }
  // } else {
  // // LeadTerm failed or returned NIL, try Series as fallback
  // leadTerm = getLeadingTermFromSeriesApproximation(result, v, effectivePoint, engine);
  // }
  //
  // if (leadTerm != null && leadTerm != F.NIL) {
  // result = leadTerm;
  // } else {
  // System.err.println("Order.create Warning: Could not determine leading term for " + result
  // + " w.r.t. " + v);
  // // Keep 'result' and continue trying with other variables if any
  // }
  // if (result.isZero())
  // return F.C0;
  // }
  // return result;
  // }

  /** Attempts to get the leading term expression c*(x-x0)^p from SeriesData */
  private static IExpr getLeadingTermFromSeriesApproximation(IExpr expr, ISymbol var, IExpr point,
      EvalEngine engine) {
    try {
      IAST seriesAST = F.Series(expr, F.List(var, point, F.C1)); // lowest order term
      IExpr seriesResult = engine.evaluate(seriesAST);

      if (seriesResult.isAST(S.SeriesData)) {
        IAST sd = (IAST) seriesResult;
        IAST coeffs = (IAST) sd.arg3();
        IExpr nmin = sd.arg4();
        if (coeffs.size() > 1 && !coeffs.arg1().isZero()) {
          IExpr coeff = coeffs.arg1();
          IExpr termVar = sd.arg1();
          IExpr termPoint = sd.arg2();
          IExpr powerExpr = termPoint.isZero() ? termVar : F.Subtract(termVar, termPoint);
          IExpr exponent = nmin;
          if (sd.size() > 6) {
            IExpr den = sd.get(6);
            if (!den.isOne())
              exponent = F.Divide(nmin, den);
          }
          return F.Times(coeff, F.Power(powerExpr, exponent));
        } else {
          // First coeff 0 or empty: return power part representing the order
          IExpr powerExpr = point.isZero() ? var : F.Subtract(var, point);
          IExpr exponent = nmin;
          if (sd.size() > 6) {
            IExpr den = sd.get(6);
            if (!den.isOne())
              exponent = F.Divide(nmin, den);
          }
          return F.Power(powerExpr, exponent);
        }
      } else if (seriesResult.isZero()) {
        return F.C0;
      } else {
        return seriesResult; // Series simplified differently
      }
    } catch (Exception e) {
      System.err.println("Order.create Warning: Error during Series calculation for " + expr + ": "
          + e.getMessage());
      return F.NIL; // Indicate failure
    }
  }


  // --- Helper methods (getExpr, getVariablePointList, mapToList, listToMap, mapsEqual) ---
  // (Include the implementations from the previous answer here)
  public static IExpr getExpr(IExpr orderExpr) {
    if (orderExpr.isAST(S.Order) && orderExpr.size() >= 2) {
      return orderExpr.first();
    }
    throw new IllegalArgumentException("Not a valid Order expression for getExpr: " + orderExpr);
  }

  public static IAST getVariablePointList(IExpr orderExpr) {
    if (orderExpr.isAST(S.Order)) {
      if (orderExpr.size() == 2) { // O(expr) -> Need to parse implicit vars/points
        Map<ISymbol, IExpr> map = new LinkedHashMap<>();
        Set<ISymbol> freeSymbols = orderExpr.first().variables();
        for (ISymbol sym : freeSymbols) {
          map.put(sym, F.C0);
        }
        return mapToList(map);
      } else if (orderExpr.size() == 3 && orderExpr.second().isList()) {
        // Assume canonical List(List(...)) format stored at arg2
        return (IAST) orderExpr.second();
      } else {
        throw new IllegalArgumentException(
            "Unexpected Order format (expected arg2 to be List): " + orderExpr);
      }
    }
    throw new IllegalArgumentException(
        "Not a valid Order expression for getVariablePointList: " + orderExpr);
  }

  private static IAST mapToList(Map<ISymbol, IExpr> map) {
    List<IAST> pairs = new ArrayList<>(map.size());
    for (Map.Entry<ISymbol, IExpr> entry : map.entrySet()) {
      pairs.add(F.List(entry.getKey(), entry.getValue()));
    }
    Collections.sort(pairs, VAR_POINT_COMPARATOR); // Sort by variable name
    IASTAppendable resultList = F.ListAlloc(pairs.size() + 1);
    // resultList.append(S.List);
    resultList.appendAll(pairs);
    return resultList;
  }

  private static Map<ISymbol, IExpr> listToMap(IAST list) {
    Map<ISymbol, IExpr> map = new LinkedHashMap<>(); // Preserve order from list initially
    if (!list.isList())
      throw new IllegalArgumentException("Expected List(...) for listToMap, got: " + list);
    for (int i = 1; i < list.size(); i++) {
      if (list.get(i).isList() && list.get(i).size() == 3 && list.get(i).first().isSymbol()) {
        map.put((ISymbol) list.get(i).first(), list.get(i).second());
      } else {
        throw new IllegalArgumentException("Invalid variable-point pair in list: " + list.get(i));
      }
    }
    return map;
  }

  private static boolean mapsEqual(Map<ISymbol, IExpr> map1, Map<ISymbol, IExpr> map2) {
    return map1.equals(map2);
  }
}
