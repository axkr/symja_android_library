package org.matheclipse.core.patternmatching;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ThrowException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.WildPattern;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPatternObject;

/**
 * A pattern matcher that implements SymPy-like wildcard matching semantics, including support for
 * commutative matching and exclusion constraints. This is designed to be used with
 * {@link WildPattern} objects that can carry exclusion conditions. The matcher recursively
 * traverses the pattern and expression trees, handling literal matches, wildcard matches, and
 * commutative structures like Plus and Times. It builds a replacement dictionary that maps pattern
 * wildcards to matched subexpressions.
 */
public class WildMatcher extends PatternMatcher implements Externalizable {

  private static class MatchContext {
    public IAST exprAST;
    public IAST pureWilds;
    public boolean isPlus;
    public IExpr identity;
    public boolean allowPartialExponent;
    public EvalEngine engine;

    public MatchContext(IAST exprAST, IAST pureWilds, boolean isPlus, IExpr identity,
        boolean allowPartialExponent, EvalEngine engine) {
      this.exprAST = exprAST;
      this.pureWilds = pureWilds;
      this.isPlus = isPlus;
      this.identity = identity;
      this.allowPartialExponent = allowPartialExponent;
      this.engine = engine;
    }

    public MatchContext copy(IAST modifiedExpr) {
      return new MatchContext(modifiedExpr, pureWilds, isPlus, identity, allowPartialExponent,
          engine);
    }
  }

  private IExpr pattern;

  public WildMatcher() {
    super();
  }

  public WildMatcher(IExpr pattern) {
    this.pattern = pattern;
  }

  @Override
  public IPatternMatcher copy() {
    return new WildMatcher(this.pattern);
  }

  private IAST createModifiedAST(IAST originalAST, boolean[] available, IExpr extraTerm,
      boolean isPlus) {
    IASTAppendable modifiedExpr =
        isPlus ? F.PlusAlloc(originalAST.argSize() + 1) : F.TimesAlloc(originalAST.argSize() + 1);
    for (int k = 1; k < available.length; k++) {
      if (available[k]) {
        modifiedExpr.append(originalAST.get(k));
      }
    }
    modifiedExpr.append(extraTerm);
    return modifiedExpr;
  }

  @Override
  public int equivalentLHS(IPatternMatcher obj) {
    return 0;
  }

  @Override
  public int equivalentTo(IPatternMatcher patternMatcher) {
    return 0;
  }

  @Override
  public IExpr eval(IExpr leftHandSide, EvalEngine engine) {
    return F.NIL;
  }

  @Override
  public IAST getAsAST() {
    return pattern.isAST() ? (IAST) pattern : F.NIL;
  }

  @Override
  public int getLHSPriority() {
    return 0;
  }

  @Override
  public int getPatternHash() {
    return pattern.hashCode();
  }

  @Override
  public void getPatterns(List<IExpr> resultList, IExpr patternExpr) {}

  private boolean hasWild(IExpr expr) {
    if (expr instanceof IPatternObject) {
      return true;
    }
    if (expr.isAST()) {
      return ((IAST) expr).exists(this::hasWild);
    }
    return false;
  }

  @Override
  public boolean isPatternHashAllowed(int patternHash) {
    return true;
  }

  public Map<IExpr, IExpr> match(IExpr pat, IExpr expr, EvalEngine engine) {
    return match(pat, expr, new HashMap<IExpr, IExpr>(), engine, true);
  }


  public Map<IExpr, IExpr> match(IExpr pat, IExpr expr, Map<IExpr, IExpr> replDict,
      EvalEngine engine) {
    return match(pat, expr, replDict, engine, true);
  }

  /**
   * Core SymPy match logic that recursively compares expression trees and builds a replacement
   * dictionary.
   */
  public Map<IExpr, IExpr> match(IExpr pat, IExpr expr, Map<IExpr, IExpr> replDict,
      EvalEngine engine, boolean allowPartialExponent) {
    if (replDict == null) {
      return null;
    }

    // 1. Literal match
    if (pat.equals(expr)) {
      return replDict;
    }

    // 2. Wildcard match (SymPy's Wild)
    if (pat instanceof IPatternObject) {

      // Execute exclusion check if it is a WildPattern
      if (pat instanceof WildPattern) {
        WildPattern sympyPat = (WildPattern) pat;
        if (!sympyPat.isConditionMatched(expr, engine)) {
          return null; // Exclusion constraint violated
        }
      }

      // Standard wildcard dictionary binding
      if (replDict.containsKey(pat)) {
        if (replDict.get(pat).equals(expr)) {
          return replDict;
        }
        return null;
      }
      replDict.put(pat, expr);
      return replDict;
    }

    // 3. AST structural and commutative matching
    if (pat.isAST()) {
      IAST patAST = (IAST) pat;
      IExpr head = patAST.head();
      // 3a. Same-head matching
      if (expr.isAST() && head.equals(expr.head())) {
        IAST exprAST = (IAST) expr;
        if (patAST.isValidBuiltInFunction()) {
          switch (((IBuiltInSymbol) head).ordinal()) {
            case ID.Plus:
            case ID.Times:
              return matchCommutative(patAST, exprAST, replDict, engine, allowPartialExponent);
            case ID.Power: {
              Map<IExpr, IExpr> powerResult =
                  matchPower(patAST, exprAST, new HashMap<>(replDict), engine);
              if (powerResult != null) {
                return powerResult;
              }
              // matchPower failed (e.g., exponents don't match like y^2 vs x^e).
              // Fall back to Power-inverse: treat expr as a whole and solve
              // base = expr^(1/patExp). E.g., y^2 matching x^e → y = (x^e)^(1/2).
              Map<IExpr, IExpr> invResult = matchPowerInverse(patAST, exprAST, replDict, engine);
              if (invResult != null) {
                return invResult;
              }
            }

            default:
              break;
          }
        }

        // Standard sequence match
        if (patAST.argSize() != exprAST.argSize()) {
          return null;
        }

        for (int i = 1; i <= patAST.argSize(); i++) {
          replDict = match(patAST.get(i), exprAST.get(i), replDict, engine);
          if (replDict == null) {
            return null;
          }
        }
        return replDict;

      }

      // 3a-bis: Power pattern matching a non-Power expression.
      if (head.isBuiltInSymbol() && ((IBuiltInSymbol) head).ordinal() == ID.Power
          && patAST.argSize() == 2) {

        // Treat non-Power expression as expr^1 for exact match of exponents like 'l' -> 1
        if (!expr.isPower()) {
          Map<IExpr, IExpr> asPowerDict =
              matchPower(patAST, F.Power(expr, F.C1), new HashMap<>(replDict), engine);
          if (asPowerDict != null) {
            return asPowerDict;
          }
        }

        Map<IExpr, IExpr> result = matchPowerInverse(patAST, expr, replDict, engine);
        if (result != null) {
          return result;
        }
      }

      // 3b. Pattern is Times/Plus but expr has a different head or is not an AST.
      if (head.isBuiltInSymbol()) {
        int ordinal = ((IBuiltInSymbol) head).ordinal();
        if (ordinal == ID.Times || ordinal == ID.Plus) {
          boolean isPlus = ordinal == ID.Plus;

          // Simulate SymPy's auto-distribution of numeric coefficients over Add.
          // Example: Pattern is Plus(a, b), Expression is Times(1/2, Plus(x, y))
          if (isPlus && expr.isTimes()) {
            IAST exprTimes = (IAST) expr;
            IExpr firstArg = exprTimes.first();

            if (firstArg.isNumber()) {
              IExpr expanded = engine.evaluate(F.Expand(exprTimes));
              if (expanded.isPlus() && !expanded.equals(expr)) {
                Map<IExpr, IExpr> expandedResult = matchCommutative(patAST, (IAST) expanded,
                    replDict, engine, allowPartialExponent);
                if (expandedResult != null) {
                  return expandedResult;
                }
              }
            }
          }

          // For a Times pattern matching a Power expression with integer exponent,
          // decompose Power(base, n) into a 2-element Times:
          // n > 1: Times(base, Power(base, n-1))
          // n < -1: Times(Power(base, -1), Power(base, n+1))
          // This mirrors SymPy's Power-to-Mul decomposition in _matches_commutative.
          if (!isPlus && expr.isPower()) {
            IAST exprPow = (IAST) expr;
            IExpr base = exprPow.base();
            IExpr exp = exprPow.exponent();
            int expInt = exp.toIntDefault(Integer.MIN_VALUE);
            if (expInt != Integer.MIN_VALUE && (expInt > 1 || expInt < -1)) {
              IASTAppendable decomposed = F.TimesAlloc(3);
              if (expInt > 0) {
                decomposed.append(base);
                decomposed.append(engine.evaluate(F.Power(base, F.ZZ(expInt - 1))));
              } else {
                decomposed.append(engine.evaluate(F.Power(base, F.CN1)));
                decomposed.append(engine.evaluate(F.Power(base, F.ZZ(expInt + 1))));
              }
              return matchCommutative(patAST, decomposed, replDict, engine, allowPartialExponent);
            }
          }

          // For a Plus pattern matching a Times expression with integer coefficient,
          // decompose coeff*base into a 2-element Plus:
          // coeff > 1: Plus(base, (coeff-1)*base)
          // coeff < -1: Plus(-base, (coeff+1)*base)
          // This mirrors SymPy's coeff-to-Add decomposition in _matches_commutative.
          if (isPlus && expr.isTimes()) {
            IAST exprTimes = (IAST) expr;
            IExpr firstArg = exprTimes.first();
            if (firstArg.isInteger()) {
              int coeff = firstArg.toIntDefault(Integer.MIN_VALUE);
              if (coeff != Integer.MIN_VALUE && (coeff > 1 || coeff < -1)) {
                // Extract the non-coefficient base: Times(coeff, a, b, ...) → base
                IExpr base = exprTimes.argSize() == 2 ? //
                    exprTimes.second() : //
                    engine.evaluate(exprTimes.rest());
                IASTAppendable decomposed = F.PlusAlloc(3);
                if (coeff > 0) {
                  // coeff*base → Plus(base, (coeff-1)*base)
                  decomposed.append(base);
                  decomposed.append(engine.evaluate(F.Times(F.ZZ(coeff - 1), base)));
                } else {
                  // coeff*base → Plus(-base, (coeff+1)*base)
                  decomposed.append(engine.evaluate(F.Negate(base)));
                  decomposed.append(engine.evaluate(F.Times(F.ZZ(coeff + 1), base)));
                }
                return matchCommutative(patAST, decomposed, replDict, engine, allowPartialExponent);
              }
            }
          }

          IASTAppendable syntheticExpr = isPlus ? F.PlusAlloc(2) : F.TimesAlloc(2);
          syntheticExpr.append(expr);
          return matchCommutative(patAST, syntheticExpr, replDict, engine, allowPartialExponent);
        }
      }
    }

    return null;
  }

  /**
   * Match commutative (Plus/Times) ASTs by separating pattern terms into pure wildcards and
   * non-wildcard terms, then using backtracking to find a valid assignment of expression terms to
   * pattern terms.
   */
  private Map<IExpr, IExpr> matchCommutative(IAST patAST, IAST exprAST, Map<IExpr, IExpr> replDict,
      EvalEngine engine, boolean allowPartialExponent) {

    boolean isPlus = patAST.head().equals(F.Plus);
    IExpr identity = isPlus ? F.C0 : F.C1;

    // Separate pattern terms into pure wildcards (bare IPatternObject) and all others
    IASTAppendable pureWilds =
        isPlus ? F.PlusAlloc(patAST.argSize()) : F.TimesAlloc(patAST.argSize());
    java.util.List<IExpr> nonWildPats = new java.util.ArrayList<>();

    for (int i = 1; i <= patAST.argSize(); i++) {
      IExpr term = patAST.get(i);
      if (term instanceof IPatternObject) {
        pureWilds.append(term);
      } else {
        nonWildPats.add(term);
      }
    }

    // Track which expression terms are available for matching
    boolean[] available = new boolean[exprAST.argSize() + 1];
    java.util.Arrays.fill(available, 1, available.length, true);

    return matchNonWildTermsBacktrack(nonWildPats, 0, available, replDict,
        new MatchContext(exprAST, pureWilds, isPlus, identity, allowPartialExponent, engine));
  }

  /**
   * Recursively match non-wildcard pattern terms against expression terms using backtracking. Each
   * pattern term is tried against each available expression term via recursive {@link #match}. If a
   * pattern term contains a wild coefficient and cannot match any expression term, we try binding
   * that wild to identity (0 for Plus) so the term vanishes.
   */
  private Map<IExpr, IExpr> matchNonWildTermsBacktrack(java.util.List<IExpr> patterns, int patIdx,
      boolean[] available, Map<IExpr, IExpr> dict, MatchContext context) {

    if (patIdx >= patterns.size()) {
      // All non-wild patterns matched; match pure wilds against remaining expression terms
      return matchPureWildsAgainstRemaining(available, dict, context);
    }

    IExpr pat = patterns.get(patIdx);

    // Strategy 1: Try direct matching against each available expression term.
    // When inside a Plus context, disable partial exponent in the inner Times sub-match
    // to prevent a pattern like b*x^2 from "stealing" a 4*x^3 expression term.
    for (int j = 1; j < available.length; j++) {
      if (!available[j]) {
        continue;
      }

      Map<IExpr, IExpr> tempDict = match(pat, context.exprAST.get(j), new HashMap<>(dict),
          context.engine, /* allowPartialExponent= */ !context.isPlus);
      if (tempDict != null) {
        available[j] = false;
        Map<IExpr, IExpr> result =
            matchNonWildTermsBacktrack(patterns, patIdx + 1, available, tempDict, context);
        if (result != null) {
          return result;
        }
        available[j] = true;
      }
    }

    // Strategy 1.5 (Plus only): Factor extraction for Times(Wild, nonWildRest)
    // Groups all terms in the expression that contain nonWildRest as a factor.
    // Enables finding polynomial factors distributed over multiple expression terms.
    if (context.isPlus && pat.isTimes()) {
      IAST timesPat = (IAST) pat;
      IExpr wildPart = null;
      IASTAppendable nonWildRest = F.TimesAlloc(timesPat.argSize());
      for (int i = 1; i <= timesPat.argSize(); i++) {
        if (timesPat.get(i) instanceof IPatternObject) {
          if (wildPart == null) {
            wildPart = timesPat.get(i);
          } else {
            wildPart = null; // Multiple wilds, abort strategy
            break;
          }
        } else {
          nonWildRest.append(timesPat.get(i));
        }
      }

      // Ensure it triggers for components with 1 or more arguments
      if (wildPart != null && nonWildRest.argSize() >= 1) {

        // Extract appropriately depending on arg size
        IExpr nonWildExpr =
            nonWildRest.argSize() == 1 ? nonWildRest.get(1) : context.engine.evaluate(nonWildRest);

        IASTAppendable groupedCoeffs = F.PlusAlloc(available.length);
        boolean[] nextAvailable = available.clone();
        boolean foundFactor = false;

        for (int j = 1; j < available.length; j++) {
          if (!available[j])
            continue;
          IExpr eTerm = context.exprAST.get(j);

          if (eTerm.equals(nonWildExpr)) {
            groupedCoeffs.append(F.C1);
            nextAvailable[j] = false;
            foundFactor = true;
          } else if (eTerm.isTimes()) {
            IAST eTimes = (IAST) eTerm;
            int idx = eTimes.indexOf(nonWildExpr);
            if (idx > 0) {
              IASTAppendable coeff = F.TimesAlloc(eTimes.argSize());
              for (int k = 1; k <= eTimes.argSize(); k++) {
                if (k != idx)
                  coeff.append(eTimes.get(k));
              }
              // Append raw term if argSize() == 1, rather than extracting the WRONG index
              groupedCoeffs
                  .append(coeff.argSize() == 1 ? coeff.get(1) : context.engine.evaluate(coeff));
              nextAvailable[j] = false;
              foundFactor = true;
            }
          }
        }

        if (foundFactor) {
          // Evaluate appropriately depending on arg size
          IExpr sumOfCoeffs = groupedCoeffs.argSize() == 1 ? groupedCoeffs.get(1)
              : context.engine.evaluate(groupedCoeffs);

          Map<IExpr, IExpr> tempDict =
              match(wildPart, sumOfCoeffs, new HashMap<>(dict), context.engine);
          if (tempDict != null) {
            Map<IExpr, IExpr> result =
                matchNonWildTermsBacktrack(patterns, patIdx + 1, nextAvailable, tempDict, context);
            if (result != null) {
              return result;
            }
          }
        }
      }
    }

    // Strategy 1b (Times only): Partial exponent match.
    // Only fires when allowPartialExponent is true (i.e., top-level Times match,
    // NOT when called from within a Plus-level sub-match).
    if (!context.isPlus && context.allowPartialExponent && pat.isPower()) {
      IExpr patBase = pat.base();
      IExpr patExp = pat.exponent();

      for (int j = 1; j < available.length; j++) {
        if (!available[j])
          continue;
        IExpr eTerm = context.exprAST.get(j);

        // Check if expression term has same base (could be Power or bare symbol)
        IExpr eBase = eTerm.isPower() ? eTerm.base() : eTerm;
        IExpr eExp = eTerm.isPower() ? eTerm.exponent() : F.C1;

        // Try to match the bases
        Map<IExpr, IExpr> baseDict = match(patBase, eBase, new HashMap<>(dict), context.engine);
        if (baseDict != null) {
          // Exact match for the exponent (prevents unresolved wilds in remainder)
          Map<IExpr, IExpr> exactExpDict =
              match(patExp, eExp, new HashMap<>(baseDict), context.engine);
          if (exactExpDict != null) {
            available[j] = false;
            Map<IExpr, IExpr> result =
                matchNonWildTermsBacktrack(patterns, patIdx + 1, available, exactExpDict, context);
            if (result != null) {
              return result;
            }
            available[j] = true;
          }

          // Compute remainder exponent: eExp - patExp
          IExpr evaluatedPatExp = context.engine.evaluate(F.subst(patExp, baseDict));
          IExpr remainderExp = context.engine.evaluate(F.Plus(eExp, F.Negate(evaluatedPatExp)));

          // Prevent putting unbound wilds back into the expression stream
          if (!remainderExp.isZero() && !hasWild(remainderExp)) {
            // Replace exprAST[j] with Power(eBase, remainderExp) for the wild matching
            available[j] = false;

            // Create a modified exprAST that has the remainder in place of the original
            IExpr remainder = context.engine.evaluate(F.Power(eBase, remainderExp));

            // Add remainder back as available for pure wild matching
            IAST modifiedExpr = createModifiedAST(context.exprAST, available, remainder, false);

            boolean[] newAvailable = new boolean[modifiedExpr.argSize() + 1];
            java.util.Arrays.fill(newAvailable, 1, newAvailable.length, true);

            Map<IExpr, IExpr> result = matchNonWildTermsBacktrack(patterns, patIdx + 1,
                newAvailable, baseDict, context.copy(modifiedExpr));
            if (result != null) {
              return result;
            }
            available[j] = true; // backtrack
          }
        }
      }
    }

    // Strategy 2 (Times only): Numeric factor absorption.
    // When a non-wild exact pattern term can't match any expression term directly,
    // try dividing each expression term by the pattern term. If the quotient is
    // a simple number (not 1), absorb the pattern by consuming that expression term
    // and putting the numeric quotient back for the wilds.
    // This handles cases like I matching -I (quotient = -1).
    if (!context.isPlus && context.allowPartialExponent && !hasWild(pat)) {
      for (int j = 1; j < available.length; j++) {
        if (!available[j])
          continue;
        IExpr eTerm = context.exprAST.get(j);
        IExpr quotient = context.engine.evaluate(F.Times(eTerm, F.Power(pat, F.CN1)));
        if (quotient.isNumber() && !quotient.isOne()) {
          available[j] = false;

          // Build modified expression: remaining available terms + the numeric quotient
          IAST modifiedExpr = createModifiedAST(context.exprAST, available, quotient, false);

          boolean[] newAvailable = new boolean[modifiedExpr.argSize() + 1];
          java.util.Arrays.fill(newAvailable, 1, newAvailable.length, true);

          Map<IExpr, IExpr> result = matchNonWildTermsBacktrack(patterns, patIdx + 1, newAvailable,
              new HashMap<>(dict), context.copy(modifiedExpr));
          if (result != null) {
            return result;
          }
          available[j] = true; // backtrack
        }
      }
    }

    // Strategy 3: If pattern contains wilds, try binding a wild coefficient to make the
    // term equal to the identity element (e.g. setting wild=0 in Times(wild, base) for Plus)
    if (context.isPlus && hasWild(pat)) {
      Map<IExpr, IExpr> tempDict = tryBindCoefficientToZero(pat, dict, context.engine);
      if (tempDict != null) {
        Map<IExpr, IExpr> result =
            matchNonWildTermsBacktrack(patterns, patIdx + 1, available, tempDict, context);
        if (result != null) {
          return result;
        }
      }
    }

    // Strategy 4 (Times only): If pattern term is Power(base, wild) and no expression term
    // matched, try binding the wild exponent to 0, making Power(base, 0) = 1 (Times identity).
    // This handles patterns like f(x)**c where c=0 makes the term vanish from multiplication.
    if (!context.isPlus && hasWild(pat)) {
      Map<IExpr, IExpr> tempDict = tryBindExponentToZero(pat, dict, context.engine);
      if (tempDict != null) {
        Map<IExpr, IExpr> result =
            matchNonWildTermsBacktrack(patterns, patIdx + 1, available, tempDict, context);
        if (result != null) {
          return result;
        }
      }
    }

    return null;
  }

  private Map<IExpr, IExpr> matchPower(IAST patAST, IAST exprAST, Map<IExpr, IExpr> replDict,
      EvalEngine engine) {
    replDict = match(patAST.base(), exprAST.base(), replDict, engine);
    if (replDict != null) {
      return match(patAST.exponent(), exprAST.exponent(), replDict, engine);
    }
    return null;
  }

  private Map<IExpr, IExpr> matchPowerInverse(IAST patAST, IExpr expr, Map<IExpr, IExpr> replDict,
      EvalEngine engine) {
    IExpr patExp = patAST.exponent();
    if (!hasWild(patExp) && patExp.isNumber() && !patExp.isZero()) {
      IExpr recipExp = engine.evaluate(F.Power(patExp, F.CN1));

      // If expr is Power(base, exprExp) and exprExp is rational (or assumed rational),
      // merge the exponents directly: candidate = base^(exprExp * recipExp).
      // This produces e.g. x^(r/2) instead of Sqrt(x^r) when r is rational.
      IExpr candidate;
      if (expr.isPower() && expr.exponent().isRationalResult()) {
        IExpr base = expr.base();
        IExpr exprExp = expr.exponent();
        candidate = engine.evaluate(F.Power(base, F.Times(exprExp, recipExp)));
      } else {
        candidate = engine.evaluate(F.Power(expr, recipExp));
      }

      if (candidate.isPresent() && !candidate.isIndeterminate()
          && !candidate.isDirectedInfinity()) {
        IExpr verification = engine.evaluate(F.Power(candidate, patExp));
        if (verification.equals(expr)) {
          return match(patAST.base(), candidate, new HashMap<>(replDict), engine);
        }
      }
    }
    return null;
  }

  /**
   * Match pure wildcard patterns against remaining (unmatched) expression terms. Remaining terms
   * are combined into a single Plus/Times expression and distributed among the wildcards.
   */
  private Map<IExpr, IExpr> matchPureWildsAgainstRemaining(boolean[] available,
      Map<IExpr, IExpr> dict, MatchContext context) {
    IAST exprAST = context.exprAST;
    // Collect remaining expression terms
    IASTAppendable remaining =
        context.isPlus ? F.PlusAlloc(exprAST.argSize()) : F.TimesAlloc(exprAST.argSize());
    for (int i = 1; i < available.length; i++) {
      if (available[i]) {
        remaining.append(exprAST.get(i));
      }
    }

    IAST pureWilds = context.pureWilds;
    if (pureWilds.argSize() == 0) {
      return remaining.argSize() == 0 ? dict : null;
    }

    EvalEngine engine = context.engine;
    if (remaining.argSize() == 0) {
      // No remaining terms - match all wildcards to identity
      for (int i = 1; i <= pureWilds.argSize(); i++) {
        dict = match(pureWilds.get(i), context.identity, dict, engine);
        if (dict == null) {
          return null;
        }
      }
      return dict;
    }

    IExpr leftover;
    if (remaining.argSize() == 1) {
      leftover = remaining.get(1);
    } else {
      leftover = engine.evaluate(remaining);
    }

    return matchWildsWithBacktracking(pureWilds, leftover, dict, context.isPlus, context.identity,
        engine);
  }

  /**
   * Match multiple pure wildcards against remaining expression using backtracking. This tries
   * different permutations of assigning expression terms to wildcards.
   */
  private Map<IExpr, IExpr> matchWildsWithBacktracking(IAST pureWilds, IExpr leftover,
      Map<IExpr, IExpr> replDict, boolean isPlus, IExpr identity, EvalEngine engine) {

    // If leftover is a Plus/Times AST, we need to try different ways of distributing terms
    if (leftover.isAST() && leftover.head().equals(isPlus ? F.Plus : F.Times)) {
      IAST leftoverAST = (IAST) leftover;
      return tryMatchPermutations(pureWilds, leftoverAST, replDict, isPlus, identity, engine);
    }

    // If leftover is a single term, greedily match the first wild to it
    if (pureWilds.argSize() == 1) {
      return match(pureWilds.get(1), leftover, replDict, engine);
    }

    // Try to match first wild to leftover, remaining wildcards to identity
    Map<IExpr, IExpr> dict = match(pureWilds.get(1), leftover, replDict, engine);
    if (dict == null) {
      return null;
    }

    for (int i = 2; i <= pureWilds.argSize(); i++) {
      dict = match(pureWilds.get(i), identity, dict, engine);
      if (dict == null) {
        return null;
      }
    }

    return dict;
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    pattern = (IExpr) in.readObject();
  }

  @Override
  public boolean test(IExpr expr) throws ThrowException {
    return test(expr, EvalEngine.get());
  }

  @Override
  public boolean test(IExpr expr, EvalEngine engine) throws ThrowException {
    Map<IExpr, IExpr> result = match(this.pattern, expr, new HashMap<>(), engine);
    return result != null;
  }

  /**
   * Try to bind a wild coefficient inside a Times pattern term to 0, making the entire term equal
   * to 0 (the Plus identity). This handles cases like matching pattern {@code b*x^2} when no
   * corresponding term exists in the expression (so b=0).
   */
  private Map<IExpr, IExpr> tryBindCoefficientToZero(IExpr pat, Map<IExpr, IExpr> dict,
      EvalEngine engine) {
    if (pat.isTimes()) {
      IAST ast = (IAST) pat;
      for (int i = 1; i <= ast.argSize(); i++) {
        if (ast.get(i) instanceof IPatternObject) {
          Map<IExpr, IExpr> tempDict = match(ast.get(i), F.C0, new HashMap<>(dict), engine);
          if (tempDict != null) {
            return tempDict;
          }
        }
      }
    }
    return null;
  }

  /**
   * Try to bind a wild exponent inside a Power pattern term to 0, making the entire term equal to 1
   * (the Times identity). This handles cases like matching pattern {@code f(x)**c} when no
   * corresponding term exists in the expression (so c=0, making f(x)**0 = 1).
   */
  private Map<IExpr, IExpr> tryBindExponentToZero(IExpr pat, Map<IExpr, IExpr> dict,
      EvalEngine engine) {
    if (pat.isPower()) {
      IAST powerAST = (IAST) pat;
      IExpr exponent = powerAST.exponent();
      if (exponent instanceof IPatternObject) {
        return match(exponent, F.C0, new HashMap<>(dict), engine);
      }
    }
    // Also handle Times(literal, Power(base, wild)) — a non-wild pattern term that contains
    // a Power with wild exponent. Try binding the wild exponent to zero and check if the
    // resulting term evaluates to 1 (Times identity).
    if (pat.isTimes()) {
      IAST timesAST = (IAST) pat;
      for (int i = 1; i <= timesAST.argSize(); i++) {
        IExpr arg = timesAST.get(i);
        if (arg.isPower()) {
          IExpr exponent = arg.exponent();
          if (exponent instanceof IPatternObject) {
            Map<IExpr, IExpr> tempDict = match(exponent, F.C0, new HashMap<>(dict), engine);
            if (tempDict != null) {
              // Verify the whole term evaluates to identity (1) with this binding
              IExpr substituted = engine.evaluate(F.subst(pat, tempDict));
              if (substituted.isOne()) {
                return tempDict;
              }
            }
          }
        }
      }
    }
    return null;
  }

  /**
   * Try different permutations of assigning expression terms to wildcards.
   */
  private Map<IExpr, IExpr> tryMatchPermutations(IAST pureWilds, IAST exprTerms,
      Map<IExpr, IExpr> replDict, boolean isPlus, IExpr identity, EvalEngine engine) {
    return tryMatchPermutationsHelper(pureWilds, exprTerms, replDict, 1, 1, isPlus, identity,
        engine);
  }

  private Map<IExpr, IExpr> tryMatchPermutationsHelper(IAST pureWilds, IAST exprTerms,
      Map<IExpr, IExpr> currentDict, int wildIndex, int termStartIndex, boolean isPlus,
      IExpr identity, EvalEngine engine) {

    int numWilds = pureWilds.argSize();
    int numTerms = exprTerms.argSize();

    if (wildIndex > numWilds) {
      if (termStartIndex > numTerms) {
        return currentDict;
      }
      return null;
    }

    IExpr currentWild = pureWilds.get(wildIndex);

    // Last wildcard should match all remaining terms
    if (wildIndex == numWilds) {
      if (termStartIndex > numTerms) {
        return match(currentWild, identity, currentDict, engine);
      } else if (termStartIndex == numTerms) {
        return match(currentWild, exprTerms.get(numTerms), currentDict, engine);
      } else {
        // Multiple remaining terms - combine them
        IASTAppendable combined = isPlus ? F.PlusAlloc(numTerms - termStartIndex + 2)
            : F.TimesAlloc(numTerms - termStartIndex + 2);
        for (int i = termStartIndex; i <= numTerms; i++) {
          combined.append(exprTerms.get(i));
        }
        IExpr combinedExpr = combined.argSize() == 1 ? combined.get(1) : engine.evaluate(combined);
        return match(currentWild, combinedExpr, currentDict, engine);
      }
    }

    // Try matching to actual terms FIRST
    for (int numTermsToConsume = 1; numTermsToConsume <= numTerms - termStartIndex
        + 1; numTermsToConsume++) {
      int endIndex = termStartIndex + numTermsToConsume - 1;
      if (endIndex > numTerms) {
        break;
      }

      IExpr termToMatch;
      if (numTermsToConsume == 1) {
        termToMatch = exprTerms.get(termStartIndex);
      } else {
        IASTAppendable combined =
            isPlus ? F.PlusAlloc(numTermsToConsume + 1) : F.TimesAlloc(numTermsToConsume + 1);
        for (int i = termStartIndex; i <= endIndex; i++) {
          combined.append(exprTerms.get(i));
        }
        termToMatch = combined.argSize() == 1 ? combined.get(1) : engine.evaluate(combined);
      }

      Map<IExpr, IExpr> result =
          match(currentWild, termToMatch, new HashMap<>(currentDict), engine);
      if (result != null) {
        Map<IExpr, IExpr> finalResult = tryMatchPermutationsHelper(pureWilds, exprTerms, result,
            wildIndex + 1, endIndex + 1, isPlus, identity, engine);
        if (finalResult != null) {
          return finalResult;
        }
      }
    }

    // Try identity as a LAST RESORT
    if (termStartIndex <= numTerms) {
      Map<IExpr, IExpr> result = match(currentWild, identity, currentDict, engine);
      if (result != null) {
        Map<IExpr, IExpr> finalResult = tryMatchPermutationsHelper(pureWilds, exprTerms, result,
            wildIndex + 1, termStartIndex, isPlus, identity, engine);
        if (finalResult != null) {
          return finalResult;
        }
      }
    }

    return null;
  }

  // --- Standard IPatternMatcher interface overrides ---
  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeObject(pattern);
  }
}
