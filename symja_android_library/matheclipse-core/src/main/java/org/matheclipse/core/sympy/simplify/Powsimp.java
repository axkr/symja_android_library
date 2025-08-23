package org.matheclipse.core.sympy.simplify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;

public class Powsimp {

  private static final ISymbol _y = F.Dummy("y");

  public static IExpr powsimp(IExpr expr) {
    return powsimp(expr, false, "all", false, null);
  }

  public static IExpr powsimp(IExpr expr, boolean deep, String combine) {
    return powsimp(expr, deep, combine, false, Powsimp::countOps);
  }

  public static IExpr powsimpRecursive(IExpr expr, boolean deep, String combine, boolean force,
      Function<IExpr, Integer> measure) {
    return powsimp(expr, deep, combine, force, measure);
  }

  public static IExpr powsimp(IExpr expr, boolean deep, String combine, boolean force,
      Function<IExpr, Integer> measure) {

    // Default measure function - count operations similar to SymPy's count_ops
    if (measure == null) {
      measure = Powsimp::countOps;
    }

    // Handle atoms and special cases
    if (expr.isAtom() || expr.equals(F.Exp(F.C0)) || expr.equals(F.Exp(F.C1))) {
      return expr;
    }

    // Recursively apply powsimp to arguments if deep=True or for Add/Mul expressions
    if (deep || expr.isPlus() || (expr.isTimes() && !expr.has(_y))) {
      if (expr.isAST()) {
        IAST ast = (IAST) expr;
        IASTAppendable result = F.ast(ast.head(), ast.size());
        for (int i = 1; i < ast.size(); i++) {
          result.append(powsimp(ast.get(i), deep, combine, force, measure));
        }
        expr = result.eval();
      }
    }

    // Handle Power expressions by converting to Mul and back
    if (expr.isPower()) {
      return powsimp(expr.times(_y), false, combine, force, measure).divide(_y);
    }

    // Only process Mul expressions from here
    if (!expr.isTimes()) {
      return expr;
    }

    IAST mulExpr = (IAST) expr;

    // Handle combine='exp' or combine='all' - combine exponents
    if ("exp".equals(combine) || "all".equals(combine)) {
      return combineExponents(mulExpr, deep, combine, force, measure);
    }
    // Handle combine='base' - combine bases
    else if ("base".equals(combine)) {
      return combineBases(mulExpr, deep, force, measure);
    } else {
      throw new IllegalArgumentException("combine must be one of ('all', 'exp', 'base').");
    }
  }

  /**
   * Combine terms with same base by adding their exponents
   */
  private static IExpr combineExponents(IAST expr, boolean deep, String combine, boolean force,
      Function<IExpr, Integer> measure) {

    // Collect base/exp data, maintaining order for non-commutative parts
    Map<IExpr, List<IExpr>> cPowers = new HashMap<>();
    List<IExpr> ncPart = new ArrayList<>();
    List<IExpr> newExpr = new ArrayList<>();
    IExpr coeff = F.C1;

    EvalEngine engine = EvalEngine.get();

    for (int i = 1; i < expr.size(); i++) {
      IExpr term = expr.get(i);

      // Handle rational coefficients
      if (term.isRational()) {
        coeff = engine.evaluate(F.Times(coeff, term));
        continue;
      }

      // Apply denesting if term is a power
      if (term.isPower()) {
        term = denestPow(term);
      }

      if (term.isOrderlessAST()) {
        IExpr base = term.isAtom() ? term : term.base();
        IExpr exp = term.isAtom() ? F.C1 : term.exponent();

        if (deep) {
          base = powsimp(base, deep, combine, force, measure);
          exp = powsimp(exp, deep, combine, force, measure);
        }

        // Don't split nested powers like sqrt(x**a) into x**a, 1/2
        if (base.isPower() || base.isExp()) {
          base = F.Power(base, exp);
          exp = F.C1;
        }

        cPowers.computeIfAbsent(base, k -> new ArrayList<>()).add(exp);
      } else {
        // Handle non-commutative terms - combine if bases are equal
        if (!ncPart.isEmpty()) {
          IExpr lastTerm = ncPart.get(ncPart.size() - 1);
          IExpr b1 = lastTerm.isAtom() ? lastTerm : lastTerm.base();
          IExpr e1 = lastTerm.isAtom() ? F.C1 : lastTerm.exponent();
          IExpr b2 = term.isAtom() ? term : term.base();
          IExpr e2 = term.isAtom() ? F.C1 : term.exponent();

          if (b1.equals(b2) && e1.isOrderlessAST() && e2.isOrderlessAST()) {
            ncPart.set(ncPart.size() - 1, F.Power(b1, F.Plus(e1, e2)));
            continue;
          }
        }
        ncPart.add(term);
      }
    }

    // Add up exponents of common bases
    for (Map.Entry<IExpr, List<IExpr>> entry : cPowers.entrySet()) {
      IExpr base = entry.getKey();
      List<IExpr> exponents = entry.getValue();

      // Handle rational base with non-number exponents
      if (base.isRational() && !coeff.isOne() && !base.isOne() && !base.isMinusOne()) {
        boolean hasNonNumber = exponents.stream().anyMatch(e -> !e.isNumber());
        if (hasNonNumber) {
          // Try to extract multiplicity from coefficient
          // This is a simplified version of SymPy's multiplicity extraction
          IRational baseRat = (IRational) base;
          if (baseRat.isPositive()) {
            // Simple case: try to see if coeff is divisible by powers of base
            IExpr absBase = baseRat.abs();
            int m = 0;
            IExpr tempCoeff = coeff;
            while (tempCoeff.isRational() && F.Mod(tempCoeff, absBase).isZero()
                && !tempCoeff.isZero()) {
              tempCoeff = F.Divide(tempCoeff, absBase);
              m++;
            }
            if (m > 0) {
              exponents.add(F.ZZ(m));
              coeff = tempCoeff;
            }
          }
        }
      }

      IExpr combinedExp =
          exponents.size() == 1 ? exponents.get(0) : F.Plus(exponents.toArray(new IExpr[0]));
      cPowers.put(base, List.of(combinedExp));
    }

    // Add coefficient as a base if it's not 1
    if (!coeff.isOne()) {
      if (cPowers.containsKey(coeff)) {
        IExpr existingExp = cPowers.get(coeff).get(0);
        cPowers.put(coeff, List.of(F.Plus(existingExp, F.C1)));
      } else {
        cPowers.put(coeff, List.of(F.C1));
      }
    }

    // Handle base and inverted base pairs
    handleInvertedBases(cPowers);

    // Handle base and negated base pairs
    handleNegatedBases(cPowers);

    // Convert back to list of powers
    List<IExpr> cPart = new ArrayList<>();
    for (Map.Entry<IExpr, List<IExpr>> entry : cPowers.entrySet()) {
      IExpr base = entry.getKey();
      IExpr exp = entry.getValue().get(0);
      if (!exp.isZero()) {
        cPart.add(exp.isOne() ? base : F.Power(base, exp));
      }
    }

    // Rebuild expression
    List<IExpr> allParts = new ArrayList<>();
    allParts.addAll(newExpr);
    allParts.addAll(cPart);

    if ("exp".equals(combine)) {
      // Return with non-commutative part separate
      allParts.addAll(ncPart);
      return allParts.size() == 1 ? allParts.get(0) : F.Times(allParts.toArray(new IExpr[0]));
    } else {
      // Apply base combination to both parts
      IExpr ncResult = ncPart.isEmpty() ? F.C1
          : powsimp(F.Times(ncPart.toArray(new IExpr[0])), false, "base", force, measure);
      IExpr cResult = cPart.isEmpty() ? F.C1
          : powsimp(F.Times(cPart.toArray(new IExpr[0])), false, "base", force, measure);
      return F.Times(ncResult, cResult);
    }
  }

  /**
   * Combine bases with same exponent
   */
  private static IExpr combineBases(IAST expr, boolean deep, boolean force,
      Function<IExpr, Integer> measure) {

    // Build c_powers and nc_part as lists (not dicts because exps are not combined)
    List<IExpr[]> cPowers = new ArrayList<>();
    List<IExpr> ncPart = new ArrayList<>();

    for (int i = 1; i < expr.size(); i++) {
      IExpr term = expr.get(i);
      if (term.isOrderlessAST()) {
        IExpr base = term.isAtom() ? term : term.base();
        IExpr exp = term.isAtom() ? F.C1 : term.exponent();
        cPowers.add(new IExpr[] {base, exp});
      } else {
        ncPart.add(term);
      }
    }

    // Pull out numerical coefficients from exponent if assumptions allow
    // e.g., 2**(2*x) => 4**x
    for (int i = 0; i < cPowers.size(); i++) {
      IExpr base = cPowers.get(i)[0];
      IExpr exp = cPowers.get(i)[1];

      if (!force && !exp.isInteger() && !base.isPositive()) {
        continue;
      }

      if (exp.isTimes() && exp.isAST()) {
        IAST expMul = (IAST) exp;
        IExpr expCoeff = F.C1;
        IASTAppendable expTerms = F.TimesAlloc(expMul.size());

        for (int j = 1; j < expMul.size(); j++) {
          IExpr factor = expMul.get(j);
          if (factor.isRational()) {
            expCoeff = F.Times(expCoeff, factor);
          } else {
            expTerms.append(factor);
          }
        }

        if (!expCoeff.isOne() && expTerms.size() > 1) {
          IExpr newExp = expTerms.size() == 2 ? expTerms.arg1() : expTerms;
          cPowers.set(i, new IExpr[] {F.Power(base, expCoeff), newExp});
        }
      }
    }

    // Combine bases whenever they have the same exponent
    Map<IExpr, List<IExpr>> cExp = new HashMap<>();
    for (IExpr[] basePow : cPowers) {
      IExpr base = basePow[0];
      IExpr exp = basePow[1];

      if (deep) {
        exp = powsimp(exp, deep, "all", force, measure);
      }

      // Handle negative coefficients in Add expressions
      if (exp.isPlus() && (base.isPositive() || exp.isInteger())) {
        exp = F.FactorTerms(exp);
        if (F.eval(F.Less(exp.first(), F.C0)).isTrue()) {
          exp = F.Negate(exp);
          base = F.Power(base, F.CN1);
        }
      }

      cExp.computeIfAbsent(exp, k -> new ArrayList<>()).add(base);
    }

    // Merge back the results to form new product
    Map<IExpr, List<IExpr>> finalPowers = new HashMap<>();
    for (Map.Entry<IExpr, List<IExpr>> entry : cExp.entrySet()) {
      IExpr exp = entry.getKey();
      List<IExpr> bases = entry.getValue();

      IExpr newBase;
      if (bases.size() == 1) {
        newBase = bases.get(0);
      } else if (exp.isInteger() || force) {
        newBase = F.Times(bases.toArray(new IExpr[0]));
      } else {
        // Separate by sign assumptions
        List<IExpr> unknown = new ArrayList<>();
        List<IExpr> nonneg = new ArrayList<>();
        List<IExpr> neg = new ArrayList<>();

        for (IExpr base : bases) {
          if (base.isNegative()) {
            neg.add(base);
          } else if (base.isNonNegativeResult()) {
            nonneg.add(base);
          } else {
            unknown.add(base);
          }
        }

        // Combine what we can
        if ((unknown.size() == 1 && neg.isEmpty()) || (neg.size() == 1 && unknown.isEmpty())) {
          nonneg.addAll(unknown);
          nonneg.addAll(neg);
          unknown.clear();
          neg.clear();
        } else if (!neg.isEmpty()) {
          // Handle negative signs
          if (exp.isRational()) {
            for (int i = 0; i < neg.size(); i++) {
              neg.set(i, F.Negate(neg.get(i)));
            }
            for (int i = 0; i < neg.size(); i++) {
              unknown.add(F.CN1);
            }
            neg.clear();
          } else {
            unknown.addAll(neg);
            neg.clear();
          }
        }

        // Add unknowns as separate powers
        for (IExpr base : unknown) {
          finalPowers.computeIfAbsent(base, k -> new ArrayList<>()).add(exp);
        }

        // Create new combined base
        List<IExpr> combinedBases = new ArrayList<>();
        combinedBases.addAll(nonneg);
        combinedBases.addAll(neg);

        if (!combinedBases.isEmpty()) {
          newBase = combinedBases.size() == 1 ? combinedBases.get(0)
              : F.Times(combinedBases.toArray(new IExpr[0]));

          // Try to factor/expand if beneficial
          IExpr expandedBase = F.ExpandAll(newBase);
          if (countOps(expandedBase) < countOps(newBase)) {
            newBase = F.FactorTerms(expandedBase);
          }
        } else {
          continue; // Skip if no combinable bases
        }
      }

      finalPowers.computeIfAbsent(newBase, k -> new ArrayList<>()).add(exp);
    }

    // Break out the powers and build result
    List<IExpr> cPart = new ArrayList<>();
    for (Map.Entry<IExpr, List<IExpr>> entry : finalPowers.entrySet()) {
      IExpr base = entry.getKey();
      for (IExpr exp : entry.getValue()) {
        cPart.add(exp.isOne() ? base : F.Power(base, exp));
      }
    }

    // Combine all parts
    List<IExpr> allParts = new ArrayList<>();
    allParts.addAll(cPart);
    allParts.addAll(ncPart);

    return allParts.size() == 1 ? allParts.get(0) : F.Times(allParts.toArray(new IExpr[0]));
  }

  /**
   * Handle base and inverted base pairs (b and 1/b)
   */
  private static void handleInvertedBases(Map<IExpr, List<IExpr>> cPowers) {
    List<IExpr> toRemove = new ArrayList<>();
    Map<IExpr, List<IExpr>> toAdd = new HashMap<>();

    for (Map.Entry<IExpr, List<IExpr>> entry : cPowers.entrySet()) {
      IExpr base = entry.getKey();
      List<IExpr> exp = entry.getValue();

      if (toRemove.contains(base))
        continue;

      if (base.isPositive()) {
        IExpr binv = base.inverse();
        if (!base.equals(binv) && cPowers.containsKey(binv)) {
          // Handle 1.0 special case
          if (base.isOne()) {
            cPowers.put(base, List.of(F.C1));
            continue;
          }

          // Combine b^e1 * (1/b)^e2 = b^(e1-e2)
          IExpr e1 = exp.get(0);
          IExpr e2 = cPowers.get(binv).get(0);

          if (F.eval(F.Equal(F.Numerator(base), F.C1)).isTrue()) {
            // Base is 1/something, remove it and subtract from inverse
            toRemove.add(base);
            toAdd.put(binv, List.of(F.Subtract(e2, e1)));
          } else {
            // Base is something, keep it and subtract inverse exponent
            toRemove.add(binv);
            toAdd.put(base, List.of(F.Subtract(e1, e2)));
          }
        }
      }
    }

    // Apply changes
    for (IExpr base : toRemove) {
      cPowers.remove(base);
    }
    for (Map.Entry<IExpr, List<IExpr>> entry : toAdd.entrySet()) {
      cPowers.put(entry.getKey(), entry.getValue());
    }
  }

  /**
   * Handle base and negated base pairs (b and -b)
   */
  private static void handleNegatedBases(Map<IExpr, List<IExpr>> cPowers) {
    IExpr negOne = F.CN1;
    List<Map.Entry<IExpr, List<IExpr>>> entries = new ArrayList<>(cPowers.entrySet());

    for (Map.Entry<IExpr, List<IExpr>> entry : entries) {
      IExpr base = entry.getKey();
      List<IExpr> exp = entry.getValue();

      IExpr negBase = F.Negate(base);
      if ((base.isSymbol() || base.isPlus()) && cPowers.containsKey(negBase)) {
        IExpr e1 = exp.get(0);
        IExpr e2 = cPowers.get(negBase).get(0);

        if (base.isPositive() || e1.isInteger()) {
          if (e1.isInteger() || base.isNegative()) {
            // Move exponent to -b
            cPowers.put(negBase, List.of(F.Plus(e2, e1)));
            cPowers.remove(base);
          } else {
            // (-b).is_positive so use its exponent
            cPowers.put(base, List.of(F.Plus(e1, e2)));
            cPowers.remove(negBase);
          }

          // Update -1 exponent
          if (cPowers.containsKey(negOne)) {
            IExpr negOneExp = cPowers.get(negOne).get(0);
            cPowers.put(negOne, List.of(F.Plus(negOneExp, e1)));
          } else {
            cPowers.put(negOne, List.of(e1));
          }
        }
      }
    }
  }

  /**
   * Simple power denesting - placeholder for more complex denesting
   */
  private static IExpr denestPow(IExpr expr) {
    if (!expr.isPower())
      return expr;

    IExpr base = expr.base();
    IExpr exp = expr.exponent();

    // Handle (a^b)^c = a^(b*c) in simple cases
    if (base.isPower() && (base.base().isPositive() || exp.isInteger())) {
      return F.Power(base.base(), F.Times(base.exponent(), exp));
    }

    return expr;
  }

  /**
   * Count operations in an expression (simplified version of SymPy's count_ops)
   */
  private static int countOps(IExpr expr) {
    if (expr.isAtom()) {
      return 0;
    }
    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      int count = ast.argSize();
      for (int i = 1; i < ast.size(); i++) {
        int opCount = countOps(ast.get(i));
        count += opCount;
      }
      return count;
    }
    return 1;
  }
}
