package org.matheclipse.core.patternmatching.hash;

import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.util.OpenIntToList;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.visit.HashValueVisitor;

/** Match two arguments of an <code>Orderless</code> AST into a new resulting expression. */
public class HashedOrderlessMatcher {
  /**
   * Maximum size of an argument in an <code>Orderless</code> expression (i.e. <code>
   * Times(...), Plus(...)</code>), which should be matched with these matching rules. This pruning
   * reduces the number of tries in the search tree of matching rules.
   */
  private static int MAX_AST_SIZE_ARGUMENT = 4;

  protected OpenIntToList<AbstractHashedPatternRules> fHashRuleMap;
  protected OpenIntToList<AbstractHashedPatternRules> fPatternHashRuleMap;

  public HashedOrderlessMatcher() {
    this.fHashRuleMap = new OpenIntToList<AbstractHashedPatternRules>();
    this.fPatternHashRuleMap = new OpenIntToList<AbstractHashedPatternRules>();
  }

  /**
   * Define the rule for the <code>Orderless</code> operator <b>OP</b>. <code>
   * OP[lhs1, lhs2, ....] := OP[rhs, ...]</code>
   *
   * @param lhs1
   * @param lhs2
   * @param rhs
   */
  public void defineHashRule(final IExpr lhs1, final IExpr lhs2, final IExpr rhs) {
    defineHashRule(lhs1, lhs2, rhs, null);
  }

  public void defineHashRule(AbstractHashedPatternRules hashRule) {
    fHashRuleMap.put(hashRule.hashCode(), hashRule);
  }

  /**
   * Define the rule for the <code>Orderless</code> operator <b>OP</b>. <code>
   * OP[lhs1, lhs2, ....] := OP[rhs, ...] /; condition</code>
   *
   * @param lhs1
   * @param lhs2
   * @param rhs
   * @param condition
   */
  public void defineHashRule(final IExpr lhs1, final IExpr lhs2, final IExpr rhs,
      final IExpr condition) {
    AbstractHashedPatternRules hashRule =
        new HashedPatternRules(lhs1, lhs2, rhs, false, condition, true);
    fHashRuleMap.put(hashRule.hashCode(), hashRule);
  }

  /**
   * Define the rule for the <code>Orderless</code> operator <b>OP</b>. <code>
   * OP[lhs1, lhs2, ....] := OP[rhs, ...] /; condition</code>
   *
   * @param lhs1
   * @param lhs2
   * @param rhs
   */
  public void definePatternHashRule(final IExpr lhs1, final IExpr lhs2, final IExpr rhs) {
    definePatternHashRule(lhs1, lhs2, rhs, null);
  }

  /**
   * Define the rule for the <code>Orderless</code> operator <b>OP</b>. <code>
   * OP[lhs1, lhs2, ....] := OP[rhs, ...] /; condition</code>
   *
   * @param lhs1 first left-hand-side pattern
   * @param lhs2 second left-hand-side pattern
   * @param rhs the right-hand-side result
   * @param lhsNegate if <code>true</code> this rule needs a negative integer factor to be true
   */
  public void definePatternHashRule(final IExpr lhs1, final IExpr lhs2, final IExpr rhs,
      final boolean lhsNegate) {
    definePatternHashRule(lhs1, lhs2, rhs, lhsNegate, null);
  }

  /**
   * Define the rule for the <code>Orderless</code> operator <b>OP</b>. <code>
   * OP[lhs1, lhs2, ....] := OP[rhs, ...] /; condition</code>
   *
   * @param lhs1 first left-hand-side pattern
   * @param lhs2 second left-hand-side pattern
   * @param rhs the right-hand-side result
   * @param condition if <code>!= null</code> do a condition test for the matched 2 left-hand-sides
   */
  public void definePatternHashRule(final IExpr lhs1, final IExpr lhs2, final IExpr rhs,
      final IExpr condition) {
    AbstractHashedPatternRules hashRule =
        new HashedPatternRules(lhs1, lhs2, rhs, false, condition, false);
    fPatternHashRuleMap.put(hashRule.hashCode(), hashRule);
  }

  /**
   * Define the rule for the <code>Orderless</code> operator <b>OP</b>. <code>
   * OP[lhs1, lhs2, ....] := OP[rhs, ...] /; condition</code>
   *
   * @param lhs1 first left-hand-side pattern
   * @param lhs2 second left-hand-side pattern
   * @param rhs the right-hand-side result
   * @param lhsNegate if <code>true</code> this rule needs a negative integer factor to be true
   * @param condition if <code>!= null</code> do a condition test for the matched 2 left-hand-sides
   */
  public void definePatternHashRule(final IExpr lhs1, final IExpr lhs2, final IExpr rhs,
      final boolean lhsNegate, final IExpr condition) {
    AbstractHashedPatternRules hashRule =
        new HashedPatternRules(lhs1, lhs2, rhs, lhsNegate, condition, false);
    fPatternHashRuleMap.put(hashRule.hashCode(), hashRule);
  }

  /**
   * Evaluate an <code>Orderless</code> AST with the defined <code>HashedPatternRules</code> as long
   * as the header of the given expression equals the evaluated expression. If
   * <code>orderlessAST</code> has flag {@link IAST#IS_HASH_EVALED} enable, {@link F#NIL} will be
   * returned.
   *
   * @param orderlessAST
   * @return {@link F#NIL} if the flag {@link IAST#IS_HASH_EVALED} is enabled
   * @see HashedPatternRules
   */
  public IAST evaluateRepeated(final IAST orderlessAST, EvalEngine engine) {
    if (orderlessAST.isEvalFlagOn(IAST.IS_HASH_EVALED)) {
      return F.NIL;
    }

    return evaluateRepeatedNoCache(orderlessAST, engine);
  }

  /**
   * Evaluate an <code>Orderless</code> AST &quot;uncached&quot; with the defined
   * <code>HashedPatternRules</code> as long as the header of the given expression equals the
   * evaluated expression.
   *
   * @param orderlessAST
   * @return
   * @see HashedPatternRules
   */
  public IAST evaluateRepeatedNoCache(final IAST orderlessAST, EvalEngine engine) {
    if (exists2ASTArguments(orderlessAST)) {
      IAST temp = orderlessAST;
      boolean evaled = false;
      int[] hashValues;
      if (!fPatternHashRuleMap.isEmpty()) {
        IExpr head = orderlessAST.head();
        IAST result = F.NIL;
        while (temp.isPresent()) {
          int size = temp.argSize();
          hashValues = new int[size];
          createSpecialHashValues(temp, hashValues);
          // for (int i = 0; i < size; i++) {
          // hashValues[i] = temp.get(i + 1).accept(HashValueVisitor.HASH_VALUE_VISITOR);
          // }

          result = evaluateHashedValues(temp, fPatternHashRuleMap, hashValues, engine);
          if (result.isPresent()) {
            temp = result;
            evaled = true;
            if (!temp.head().equals(head)) {
              return setIsHashEvaledFlag(temp);
            }
          } else {
            break;
          }
        }
      }
      if (!fHashRuleMap.isEmpty()) {
        int size = temp.argSize();
        hashValues = new int[size];
        createHashValues(temp, hashValues);
        IAST result = evaluateHashedValues(temp, fHashRuleMap, hashValues, engine);
        if (result.isPresent()) {
          return setIsHashEvaledFlag(result);
        }
      }
      if (evaled) {
        return setIsHashEvaledFlag(temp);
      }
    }
    orderlessAST.addEvalFlags(IAST.IS_HASH_EVALED);
    return F.NIL;
  }

  /**
   * Set the <code>IAST.IS_HASH_EVALED</code> for the given <code>ast</code>. And return the <code>
   * ast</code>
   *
   * @param ast
   * @return
   */
  protected static IAST setIsHashEvaledFlag(IAST ast) {
    ast.setEvalFlags(IAST.IS_HASH_EVALED);
    return ast;
  }

  /**
   * Check if there are at least two IAST arguments available in <code>ast</code>.
   *
   * @param ast
   * @return
   */
  protected static boolean exists2ASTArguments(IAST ast) {
    final int[] counter = {0};
    return ast.exists(x -> x.isAST() && x.size() < MAX_AST_SIZE_ARGUMENT && ++counter[0] == 2);
  }

  /**
   * Evaluate an <code>Orderless</code> AST only once with the defined <code>HashedPatternRules
   * </code>.
   *
   * @param orderlessAST
   * @return
   * @see HashedPatternRules
   * @return {@link F#NIL} if no evaluation found
   */
  public IAST evaluate(final IAST orderlessAST, EvalEngine engine) {
    // Compute hash values for each argument of the orderlessAST
    // if hashValues[i]==0 then the corresponding argument is evaluated and not
    // available anymore
    int[] hashValues = new int[(orderlessAST.argSize())];
    if (!fPatternHashRuleMap.isEmpty()) {
      createSpecialHashValues(orderlessAST, hashValues);
      IAST result = evaluateHashedValues(orderlessAST, fPatternHashRuleMap, hashValues, engine);
      if (result.isPresent()) {
        return result;
      }
    }
    if (!fHashRuleMap.isEmpty()) {
      createHashValues(orderlessAST, hashValues);
      return evaluateHashedValues(orderlessAST, fHashRuleMap, hashValues, engine);
    }
    return F.NIL;
  }

  protected void createHashValues(final IAST orderlessAST, int[] hashValues) {
    for (int i = 0; i < hashValues.length; i++) {
      hashValues[i] = orderlessAST.get(i + 1).head().hashCode();
    }
  }

  protected void createSpecialHashValues(final IAST orderlessAST, int[] hashValues) {
    for (int i = 0; i < hashValues.length; i++) {
      hashValues[i] = orderlessAST.get(i + 1).accept(HashValueVisitor.HASH_VALUE_VISITOR);
    }
  }

  protected IAST evaluateHashedValues(final IAST orderlessAST,
      OpenIntToList<AbstractHashedPatternRules> hashRuleMap, int[] hashValues, EvalEngine engine) {
    boolean evaled = false;
    IASTAppendable result = orderlessAST.copyHead();
    for (int i = 0; i < hashValues.length - 1; i++) {
      if (hashValues[i] == 0 || orderlessAST.get(i + 1).size() >= MAX_AST_SIZE_ARGUMENT) {
        // already used entry OR size() of expression to big
        continue;
      }
      evaled: for (int j = i + 1; j < hashValues.length; j++) {
        if (hashValues[j] == 0 || orderlessAST.get(j + 1).size() >= MAX_AST_SIZE_ARGUMENT) {
          // already used entry OR size() of expression to big
          continue;
        }
        final List<AbstractHashedPatternRules> hashRuleList = hashRuleMap
            .get(AbstractHashedPatternRules.calculateHashcode(hashValues[i], hashValues[j]));
        if (hashRuleList != null) {
          for (AbstractHashedPatternRules hashRule : hashRuleList) {

            if (!hashRule.isPattern1() && !hashRule.isPattern2()) {
              if (hashValues[i] != hashRule.getHash1() || hashValues[j] != hashRule.getHash2()) {
                if (hashValues[i] != hashRule.getHash2() || hashValues[j] != hashRule.getHash1()) {
                  // hash code of both entries aren't matching
                  continue;
                }

                if (updateHashValues(result, orderlessAST, hashRule, hashValues, j, i, engine)) {
                  evaled = true;
                  break evaled;
                }
                continue;
              }

              if (updateHashValues(result, orderlessAST, hashRule, hashValues, i, j, engine)) {
                evaled = true;
                break evaled;
              }

              if (hashValues[i] != hashRule.getHash2() || hashValues[j] != hashRule.getHash1()) {
                // hash code of both entries aren't matching
                continue;
              }

              if (updateHashValues(result, orderlessAST, hashRule, hashValues, j, i, engine)) {
                evaled = true;
                break evaled;
              }
              continue;
            }

            if (updateHashValues(result, orderlessAST, hashRule, hashValues, i, j, engine)) {
              evaled = true;
              break evaled;
            }

            if (updateHashValues(result, orderlessAST, hashRule, hashValues, j, i, engine)) {
              evaled = true;
              break evaled;
            }
          }
        }
      }
    }

    if (evaled) {
      // append the rest of the unevaluated arguments
      for (int i = 0; i < hashValues.length; i++) {
        if (hashValues[i] != 0) {
          result.append(orderlessAST.get(i + 1));
        }
      }
      return result;
    }
    return F.NIL;
  }

  protected boolean updateHashValues(IASTAppendable result, final IAST orderlessAST,
      AbstractHashedPatternRules hashRule, int[] hashValues, int i, int j, EvalEngine engine) {
    IExpr temp;
    if ((temp =
        hashRule.evalDownRule(orderlessAST.get(i + 1), null, orderlessAST.get(j + 1), null, engine))
            .isPresent()) {
      hashValues[i] = 0;
      hashValues[j] = 0;
      result.append(temp);
      return true;
    }
    return false;
  }
}
