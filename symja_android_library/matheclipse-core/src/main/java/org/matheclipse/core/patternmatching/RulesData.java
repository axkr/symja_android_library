package org.matheclipse.core.patternmatching;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IMatch;
import org.matheclipse.core.eval.util.OpenIntToIExprHashMap;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IEvalStepListener;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.AbstractVisitor;
import org.matheclipse.parser.trie.TrieMatch;
import it.unimi.dsi.fastutil.ints.IntArrayList;

/**
 * Container for the <b>Transformation Rules</b> associated with a specific {@link ISymbol}.
 * <p>
 * {@code RulesData} is the data structure that holds the "definitions" of a user-defined function.
 * When you define a function like {@code f[x_] := x^2} or {@code f[1] = 0}, Symja stores these
 * definitions inside the {@code RulesData} object attached to the symbol {@code f}.
 * </p>
 *
 * <h3>1. Types of Rules Stored</h3>
 * <p>
 * This class optimizes storage by distinguishing between two types of rules:
 * </p>
 * <ul>
 * <li><b>Equal Rules (Constant Keys):</b> Rules where the Left-Hand Side (LHS) contains no patterns
 * (e.g., {@code f[1] = 0}, {@code f["a"] = 5}). These are stored in a Hash Map for O(1) fast
 * lookup.</li>
 * <li><b>Pattern Rules:</b> Rules containing patterns (e.g., {@code f[x_Int] := ...}). These are
 * stored in a sorted list and checked sequentially based on specificity and priority.</li>
 * </ul>
 *
 * <h3>2. Evaluation Flow</h3>
 * <p>
 * When the {@link EvalEngine} evaluates an expression like {@code f[arg]}:
 * </p>
 * <ol>
 * <li>It retrieves the {@code RulesData} from the symbol {@code f}.</li>
 * <li>It first checks the <b>Equal Rules</b> map to see if {@code f[arg]} matches a known constant
 * definition exactly.</li>
 * <li>If no constant match is found, it iterates through the <b>Pattern Rules</b>.</li>
 * <li>The first pattern rule that matches (and satisfies any conditions) is applied.</li>
 * </ol>
 *
 * <h3>3. Usage Examples</h3>
 *
 * <h4>Accessing Rules Programmatically</h4>
 * 
 * <pre>
 * ISymbol f = F.Dummy("f");
 *
 * // Define f[1] = 10 (Constant Rule)
 * engine.evaluate(F.Set(F.unary(f, F.C1), F.C10));
 *
 * // Define f[x_] := x^2 (Pattern Rule)
 * ISymbol x = F.Dummy("x");
 * engine.evaluate(F.SetDelayed(F.unary(f, F.Pattern(x, null)), F.Sqr(x)));
 *
 * // Inspect RulesData
 * RulesData rules = f.getRulesData();
 * if (rules != null) {
 *   // Print all definitions for f
 *   System.out.println(rules.toString());
 * }
 * </pre>
 *
 * @see org.matheclipse.core.interfaces.ISymbol
 * @see org.matheclipse.core.patternmatching.PatternMatcherAndEvaluator
 * @see org.matheclipse.core.patternmatching.IPatternMatcher
 */
public final class RulesData implements Serializable {
  private static final long serialVersionUID = -7747268035549814899L;

  public static final int DEFAULT_VALUE_INDEX = Integer.MIN_VALUE;

  /**
   * If this method returns <code>false</code>, the matching can try to match the <code>lhs</code>
   * with a hash value in a step before the &quot;real structural pattern matching&quot;.
   *
   * @param lhs the left-hand-side of pattern matching definition
   */
  public static boolean isComplicatedPatternRule(final IExpr lhs) {
    if (lhs.isASTOrAssociation()) {
      final IAST lhsAST = ((IAST) lhs);
      if (lhsAST.size() > 1) {
        if (lhsAST.topHead().hasOrderlessAttribute()) {
          return true;
        }

        IExpr a1 = lhsAST.arg1();
        if (isComplicatedPatternExpr(a1) || !a1.head().isFreeOfPatterns()) {
          return true;
        }
        if (lhsAST.exists(x -> x.isPatternDefault() || x.isPatternSequence(false))) {
          return true;
        }
      }
      return !lhs.head().isFreeOfPatterns();
    }
    return isComplicatedPatternExpr(lhs);
  }

  private static boolean isComplicatedPatternExpr(IExpr a1) {
    if (a1 instanceof IPatternObject) {
      return true;
    } else if (a1.isASTOrAssociation()) {

      if (a1.isPatternMatchingFunction()) {
        return true;
      }

      IAST arg1 = (IAST) a1;
      IExpr head = arg1.head();
      if (!head.isSymbol() && isComplicatedPatternExpr(head)) {
        // the head contains a pattern F_(a1, a2,...) or complicated expression
        return true;
      }
      // the left hand side is associated with the first argument
      // see if one of the arguments contain a pattern with default
      // value
      return arg1.exists(x -> x.isPatternDefault(), 1);
    }
    return false;
  }

  /**
   * Default values for a symbol which could be determined with the {@link S#Default} function
   */
  private OpenIntToIExprHashMap<IExpr> fDefaultValues;

  /**
   * Messages associated with this symbol which could be defined with {@link S#MessageName} function
   */
  private Map<String, IStringX> fMessages;

  /**
   * Matches rules which contain no patterns and are defined with {@link S#Set} or
   * {@link S#SetDelayed} function
   */
  private Map<IExpr, PatternMatcherEquals> fEqualDownRules;

  /**
   * Pattern matcher for decision tree pattern matching (used for small IAST sizes only), defined by
   * the experimental <code>org.matheclipse.core.decisiontree.RulesToDecionTree</code> class.
   */
  private transient IMatch fMatcher;

  /**
   * List of pattern matchers which are defined with {@link S#Set} or {@link S#SetDelayed} function.
   * The corresponding priority is stored in <code>fPriorityDownRules
   * </code>.
   */
  private List<IPatternMatcher> fPatternDownRules;

  /**
   * Sorted int array of the priorities of the corresponding <code>fPatternDownRules</code> matcher.
   */
  private IntArrayList fPriorityDownRules;

  /**
   * Matches rules which contain no patterns and are defined with {@link S#UpSet} or
   * {@link S#UpSetDelayed} function
   */
  private Map<IExpr, PatternMatcherEquals> fEqualUpRules;

  /**
   * Matches rules which are defined with {@link S#UpSet} or {@link S#UpSetDelayed} function
   */
  private List<IPatternMatcher> fSimplePatternUpRules;

  public RulesData() {
    clear();
  }

  public RulesData(int[] sizes) {
    // this.context = context;
    clear();
    if (sizes.length > 0) {
      int capacity;
      if (sizes[0] > 0) {
        capacity = sizes[0];
        if (capacity < 8) {
          capacity = 8;
        }
        fEqualDownRules = new HashMap<IExpr, PatternMatcherEquals>(capacity);
      }
    }
  }

  public RulesData(int[] sizes, IBuiltInSymbol head) {
    // this.context = context;
    clear();
    IEvaluator evaluator = head.getEvaluator();
    if (evaluator instanceof IMatch) {
      fMatcher = (IMatch) evaluator;
    }

    if (sizes.length > 0) {
      int capacity;
      if (sizes[0] > 0) {
        capacity = sizes[0];
        if (capacity < 8) {
          capacity = 8;
        }
        fEqualDownRules = new HashMap<IExpr, PatternMatcherEquals>(capacity);
      }
    }
  }

  /**
   * Run the given visitor on every IAST stored in the rule database. Example: optimize internal
   * memory usage by sharing common objects.
   *
   * @param visitor the visitor which manipulates the IAST objects
   */
  public void accept(AbstractVisitor visitor) {
    Iterator<Map.Entry<IExpr, PatternMatcherEquals>> iter;
    PatternMatcherEquals pmEquals;
    IAST ast;
    PatternMatcherAndEvaluator pmEvaluator;
    if (fEqualUpRules != null && fEqualUpRules.size() > 0) {
      iter = fEqualUpRules.entrySet().iterator();
      while (iter.hasNext()) {
        Map.Entry<IExpr, PatternMatcherEquals> next = iter.next();
        IExpr key = next.getKey();
        pmEquals = next.getValue();
        if (key.isASTOrAssociation()) {
          key.accept(visitor);
        }
        if (pmEquals.getRHS().isASTOrAssociation()) {
          pmEquals.getRHS().accept(visitor);
        }
      }
    }
    if (fSimplePatternUpRules != null && fSimplePatternUpRules.size() > 0) {
      List<IPatternMatcher> upRules = fSimplePatternUpRules;
      for (int i = 0; i < upRules.size(); i++) {
        if (upRules.get(i) != null) {
          IPatternMatcher elem = upRules.get(i);

          if (elem instanceof PatternMatcherAndEvaluator) {
            pmEvaluator = (PatternMatcherAndEvaluator) elem;
            if (pmEvaluator.getLHS().isASTOrAssociation()) {
              pmEvaluator.getLHS().accept(visitor);
            }
            if (pmEvaluator.getRHS().isASTOrAssociation()) {
              pmEvaluator.getRHS().accept(visitor);
            }
          }
        }
      }
    }

    if (fEqualDownRules != null && fEqualDownRules.size() > 0) {
      iter = fEqualDownRules.entrySet().iterator();
      while (iter.hasNext()) {
        Map.Entry<IExpr, PatternMatcherEquals> next = iter.next();
        IExpr key = next.getKey();
        pmEquals = next.getValue();
        ast = pmEquals.getAsAST();
        if (key.isASTOrAssociation()) {
          key.accept(visitor);
        }
        ast.accept(visitor);
      }
    }

    if (fPatternDownRules != null && fPatternDownRules.size() > 0) {
      IPatternMatcher[] list = fPatternDownRules.toArray(new IPatternMatcher[0]);
      final int length = list.length;
      for (int i = 0; i < length; i++) {
        if (list[i] instanceof PatternMatcherAndEvaluator) {
          pmEvaluator = (PatternMatcherAndEvaluator) list[i];
          ast = pmEvaluator.getAsAST();
          ast.accept(visitor);
        }
      }
    }
  }

  /**
   * Create a pattern hash value for the left-hand-side expression and insert the left-hand-side as
   * a simple pattern rule to the <code>fSimplePatternRules</code>.
   *
   * @param leftHandSide
   * @param pmEvaluator
   * @return
   */
  private PatternMatcher addSimplePatternUpRule(final IExpr leftHandSide,
      final PatternMatcher pmEvaluator) {
    // IExpr head = ((IAST) leftHandSide).head();
    // if (head.isFreeOfPatterns()) {
    // final int hash = ((IAST) leftHandSide).topHead().hashCode();
    // if (F.isSystemInitialized()) {
    // int indx = fSimplePatternUpRules.indexOf(pmEvaluator);
    // if (indx >= 0) {
    // fSimplePatternUpRules.remove(indx);
    // }
    // }
    // fSimplePatternUpRules.add(pmEvaluator);
    // return pmEvaluator;
    // }

    if (F.isSystemInitialized()) {
      int indx = fSimplePatternUpRules.indexOf(pmEvaluator);
      if (indx >= 0) {
        fSimplePatternUpRules.remove(indx);
      }
    }
    fSimplePatternUpRules.add(pmEvaluator);
    return pmEvaluator;
  }

  public void clear() {
    fEqualDownRules = null;
    fPatternDownRules = null;
    fPriorityDownRules = null;
    fEqualUpRules = null;
    fSimplePatternUpRules = null;
  }

  public void clearAll() {
    clear();
    fMessages = null;
  }

  public List<IAST> definition() {
    int size = 4;
    if (fEqualUpRules != null) {
      size += fEqualUpRules.size();
    }
    if (fSimplePatternUpRules != null) {
      size += fSimplePatternUpRules.size();
    }
    if (fEqualDownRules != null) {
      size += fEqualDownRules.size();
    }
    if (fPatternDownRules != null) {
      size += fPatternDownRules.size();
    }
    ArrayList<IAST> definitionList = new ArrayList<IAST>(size);
    Iterator<Map.Entry<IExpr, PatternMatcherEquals>> iter;
    PatternMatcherEquals pmEquals;
    PatternMatcherAndEvaluator pmEvaluator;
    if (fEqualUpRules != null && fEqualUpRules.size() > 0) {
      iter = fEqualUpRules.entrySet().iterator();
      while (iter.hasNext()) {
        pmEquals = iter.next().getValue();
        definitionList.add(pmEquals.getAsAST());
      }
    }
    if (fSimplePatternUpRules != null) {
      for (int i = 0; i < fSimplePatternUpRules.size(); i++) {
        IPatternMatcher elem = fSimplePatternUpRules.get(i);
        if (elem instanceof PatternMatcherAndEvaluator) {
          pmEvaluator = (PatternMatcherAndEvaluator) elem;
          definitionList.add(pmEvaluator.getAsAST());
        }
      }
    }

    if (fEqualDownRules != null && fEqualDownRules.size() > 0) {
      iter = fEqualDownRules.entrySet().iterator();
      while (iter.hasNext()) {
        pmEquals = iter.next().getValue();
        definitionList.add(pmEquals.getAsAST());
      }
    }

    if (fPatternDownRules != null && fPatternDownRules.size() > 0) {
      IPatternMatcher[] list = fPatternDownRules.toArray(new IPatternMatcher[0]);
      final int length = list.length;
      for (int i = 0; i < length; i++) {
        if (list[i] instanceof PatternMatcherAndEvaluator) {
          pmEvaluator = (PatternMatcherAndEvaluator) list[i];
          definitionList.add(pmEvaluator.getAsAST());
        }
      }
    }

    return definitionList;
  }

  /**
   * Give the <code>DownValues()</code> of a symbol as a list of <code>RuleDelayed</code> (delayed
   * rules) with the left-hand-side wrapped in a <code>HoldPattern()</code> expression.
   *
   * @return a list of <code>RuleDelayed(HoldPattern(lhs), rhs)</code> rules
   */
  public IAST downValues() {
    int size = 1;
    if (fEqualDownRules != null) {
      size += fEqualDownRules.size();
    }
    if (fPatternDownRules != null) {
      size += fPatternDownRules.size();
    }
    IASTAppendable result = F.ListAlloc(size);
    if (fEqualDownRules != null) {
      for (Map.Entry<IExpr, PatternMatcherEquals> entry : fEqualDownRules.entrySet()) {
        PatternMatcherEquals value = entry.getValue();
        result.append(F.RuleDelayed(F.HoldPattern(value.getLHS()), value.getRHS()));
      }
    }
    if (fPatternDownRules != null) {
      for (int i = 0; i < fPatternDownRules.size(); i++) {
        IPatternMatcher matcher = fPatternDownRules.get(i);
        result.append(F.RuleDelayed(F.HoldPattern(matcher.getLHS()), matcher.getRHS()));
      }
    }
    return result;
  }

  /**
   * Give the <code>UpValues()</code> of a symbol as a list of <code>RuleDelayed</code> (delayed
   * rules) with the left-hand-side wrapped in a <code>HoldPattern()</code> expression.
   *
   * @return a list of <code>RuleDelayed(HoldPattern(lhs), rhs)</code> rules
   */
  public IAST upValues() {
    int size = 1;
    if (fEqualUpRules != null) {
      size += fEqualUpRules.size();
    }
    if (fSimplePatternUpRules != null) {
      size += fSimplePatternUpRules.size();
    }
    IASTAppendable result = F.ListAlloc(size);
    if (fEqualUpRules != null) {
      for (Map.Entry<IExpr, PatternMatcherEquals> entry : fEqualUpRules.entrySet()) {
        PatternMatcherEquals value = entry.getValue();
        result.append(F.RuleDelayed(F.HoldPattern(value.getLHS()), value.getRHS()));
      }
    }
    if (fSimplePatternUpRules != null) {
      for (int i = 0; i < fSimplePatternUpRules.size(); i++) {
        IPatternMatcher matcher = fSimplePatternUpRules.get(i);
        result.append(F.RuleDelayed(F.HoldPattern(matcher.getLHS()), matcher.getRHS()));
      }
    }
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;

    RulesData other = (RulesData) obj;

    if (fEqualDownRules == null) {
      if (other.fEqualDownRules != null)
        return false;
    } else if (!fEqualDownRules.equals(other.fEqualDownRules))
      return false;
    if (fEqualUpRules == null) {
      if (other.fEqualUpRules != null)
        return false;
    } else if (!fEqualUpRules.equals(other.fEqualUpRules))
      return false;

    if (fPatternDownRules == null) {
      if (other.fPatternDownRules != null)
        return false;
    } else if (!fPatternDownRules.equals(other.fPatternDownRules))
      return false;

    if (fSimplePatternUpRules == null) {
      if (other.fSimplePatternUpRules != null)
        return false;
    } else if (!fSimplePatternUpRules.equals(other.fSimplePatternUpRules))
      return false;

    return true;
  }

  /**
   * Try matching the <code>expr</code> expression with this pattern-matching rules and if matching
   * rule was found, return the evaluated right-hand-side of that matching rule, otherwise return
   * {@link F#NIL}.
   *
   * @param expr the expression which will be tested for matching an existing pattern-matching rule
   * @param engine the evaluation engine
   * @return {@link F#NIL} if no matching/evaluation was possible
   */
  public IExpr evalDownRule(final IExpr expr, EvalEngine engine) {
    if (fEqualDownRules != null) {
      PatternMatcherEquals res = fEqualDownRules.get(expr);
      if (res != null) {
        return res.getRHS();
      }
    }
    if (!expr.isASTOrAssociation()) {
      return F.NIL;
    }
    boolean evalRHSMode = engine.isEvalRHSMode();
    try {
      engine.setEvalRHSMode(true);

      if (fMatcher != null && expr.isAST() && expr.size() < 6) {
        IExpr temp = evalDecisionTree(expr, engine);
        if (temp.isPresent()) {
          return temp;
        }
      }

      IPatternMatcher pmEvaluator;
      if (fPatternDownRules != null) {
        int patternHash = 0;
        if (expr.isASTOrAssociation()) {
          patternHash = ((IAST) expr).patternHashCode();
        }
        final IExpr integrateVar =
            expr.isAST(S.Integrate, 3) && expr.second().isSymbol() ? expr.second() : null;
        IEvalStepListener stepListener = engine.getStepListener();
        final boolean isTraceMode =
            Config.TRACE_REWRITE_RULE && engine.isTraceMode() && stepListener != null;
        for (IPatternMatcher patternEvaluator : fPatternDownRules) {
          // if (patternEvaluator.fLhsPatternExpr.isAST(S.Integrate)) {
          // // System.out.println("Rule: " + patternEvaluator.getLHSPriority());
          // // if (patternEvaluator.getLHSPriority() >= 7301) {
          // // debug rule from here
          // // rule 7301 is CannotIntegrate on 27th JAN 2024
          // if (patternEvaluator.getLHSPriority() == 7301) {
          // System.out.println("Rule " + patternEvaluator.getLHSPriority() + "found!\n"
          // + patternEvaluator.toString());
          // }
          // }
          if (patternEvaluator.isPatternHashAllowed(patternHash)) {
            pmEvaluator = patternEvaluator.copy();
            IExpr result = F.NIL;
            if (isTraceMode) {
              stepListener.setUp(expr, engine.getRecursionCounter(), expr);
              try {
                result = pmEvaluator.eval(expr, engine);
                if (result.isPresent()) {
                  return result;
                }
              } finally {
                if (result.isPresent()) {
                  stepListener.tearDown(result, engine.getRecursionCounter(), true, expr);
                } else {
                  stepListener.tearDown(F.NIL, engine.getRecursionCounter(), false, expr);
                }
              }
              continue;
            }


            if (integrateVar != null //
                && pmEvaluator instanceof PatternMatcherAndEvaluator) {

              PatternMatcherAndEvaluator patternMatcher = (PatternMatcherAndEvaluator) pmEvaluator;
              IExpr lhs = patternMatcher.getLHS();
              if (lhs.isAST(S.Integrate, 3) && lhs.second().equals(F.x_Symbol)) {
                IPatternMap patternMap = patternMatcher.createPatternMap();
                patternMap.setValue(F.x_, integrateVar);
                // compare with '==' operator because F.x_ has unique address:
                patternMatcher.fLhsPatternExpr =
                    F.subst(patternMatcher.fLhsPatternExpr, v -> v == F.x_, integrateVar);
                if (patternMatcher.fLhsPatternExpr instanceof IASTMutable) {
                  IPatternMap.setPatternFlags((IASTMutable) patternMatcher.fLhsPatternExpr);
                }
                result = patternMatcher.matchIntegrateFunction(expr, patternMap, engine);
              } else {
                result = pmEvaluator.eval(expr, engine);
              }
            } else {
              result = pmEvaluator.eval(expr, engine);
            }

            if (result.isPresent()) {
              if (patternEvaluator.fLhsPatternExpr.isAST(S.Integrate)) {
                if (!expr.equals(result)) {
                  return result;
                }
                boolean quietMode = engine.isQuietMode();
                try {
                  engine.setQuietMode(false);
                  // Endless iteration detected in `1` (rule number `2`) for Rubi pattern-matching
                  // rules.
                  Errors.printMessage(S.Integrate, "rubiendless",
                      F.list(expr, F.ZZ(patternEvaluator.getLHSPriority())), engine);
                } finally {
                  engine.setQuietMode(quietMode);
                }
              } else {
                return result;
              }
            }
            // } catch (Exception ex) {
            // // For Integrate:
            // // org.matheclipse.core.eval.exception.TimeoutException
            // System.out.println(ex.toString());
            // ex.printStackTrace();
            // throw ex;
            // }
          }
        }
      }
    } finally {
      engine.setEvalRHSMode(evalRHSMode);
    }
    return F.NIL;
  }

  /**
   * Try matching the <code>expr</code> expression with the pattern-matching rules create with the
   * <code>org.matheclipse.core.decisiontree.RulesToDecionTree</code> if a matching rule was found,
   * return the evaluated right-hand-side of that matching rule, otherwise return {@link F#NIL}.
   *
   * @param expr the expression which will be tested for matching an existing pattern-matching rule
   * @param engine the evaluation engine
   * @return {@link F#NIL} if no matching/evaluation was possible
   */
  private IExpr evalDecisionTree(final IExpr expr, EvalEngine engine) {
    IExpr match = F.NIL;
    switch (expr.size()) {
      case 2:
        match = fMatcher.match2((IAST) expr, engine);
        break;
      case 3:
        match = fMatcher.match3((IAST) expr, engine);
        break;
      case 4:
        match = fMatcher.match4((IAST) expr, engine);
        break;
      case 5:
        match = fMatcher.match5((IAST) expr, engine);
        break;
      default:
        break;
    }
    return match;
  }

  private static boolean isShowSteps(IPatternMatcher pmEvaluator) {
    IExpr head = pmEvaluator.getLHS().head();
    if (head.isSymbol() && ((ISymbol) head).isContext(Context.RUBI)) {
      return true;
    }
    return head.equals(S.Integrate);
  }

  private static boolean isShowPriority(IPatternMatcher pmEvaluator) {
    IExpr head = pmEvaluator.getLHS().head();
    return head.equals(S.Integrate);
  }

  /**
   * Try matching the <code>expression</code> with this pattern-matching up-rules.
   * 
   * @param expression
   * @param engine
   * @return
   */
  public IExpr evalUpRule(final IExpr expression, EvalEngine engine) {
    PatternMatcherEquals res;
    if (fEqualUpRules != null) {
      res = fEqualUpRules.get(expression);
      if (res != null) {
        return res.getRHS();
      }
    }

    IPatternMatcher pmEvaluator;
    if ((fSimplePatternUpRules != null) && (expression.isASTOrAssociation())) {
      IExpr result;
      for (int i = 0; i < fSimplePatternUpRules.size(); i++) {
        pmEvaluator = fSimplePatternUpRules.get(i).copy();
        result = pmEvaluator.eval(expression, engine);
        if (result.isPresent()) {
          return result;
        }
      }
    }
    return F.NIL;
  }

  /**
   * Default values for a symbol which could be determined with the {@link S#Default} function
   * 
   * @param pos
   * @return <code>null</code> if no values are defined
   */
  public final IExpr getDefaultValue(int pos) {
    if (fDefaultValues == null) {
      return null;
    }
    return fDefaultValues.get(pos);
  }

  /** @return Returns the equalRules. */
  public final Map<String, IStringX> getMessages() {
    if (fMessages == null) {
      fMessages = Config.TRIE_STRING2STRINGX_BUILDER.withMatch(TrieMatch.EXACT).build(); // Tries.forStrings();
    }
    return fMessages;
  }

  /** @return Returns the equalRules. */
  public final Map<IExpr, PatternMatcherEquals> getEqualDownRules() {
    if (fEqualDownRules == null) {
      fEqualDownRules = new TreeMap<IExpr, PatternMatcherEquals>();
    }
    return fEqualDownRules;
  }

  /** @return Returns the equalRules. */
  public final Map<IExpr, PatternMatcherEquals> getEqualUpRules() {
    if (fEqualUpRules == null) {
      fEqualUpRules = new TreeMap<IExpr, PatternMatcherEquals>();
    }
    return fEqualUpRules;
  }

  private List<IPatternMatcher> getSimplePatternUpRules() {
    if (fSimplePatternUpRules == null) {
      fSimplePatternUpRules = new ArrayList<IPatternMatcher>(); // IPatternMatcher.EQUIVALENCE_COMPARATOR);
    }
    return fSimplePatternUpRules;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fEqualDownRules == null) ? 0 : fEqualDownRules.hashCode());
    result = prime * result + ((fEqualUpRules == null) ? 0 : fEqualUpRules.hashCode());
    result = prime * result + ((fPatternDownRules == null) ? 0 : fPatternDownRules.hashCode());
    result =
        prime * result + ((fSimplePatternUpRules == null) ? 0 : fSimplePatternUpRules.hashCode());
    return result;
  }

  public final IPatternMatcher putDownRule(final IExpr leftHandSide, final IExpr rightHandSide) {
    return putDownRule(IPatternMatcher.SET_DELAYED, false, leftHandSide, rightHandSide,
        IPatternMap.DEFAULT_RULE_PRIORITY);
  }

  public final IPatternMatcher putDownRule(final int setSymbol, final boolean equalRule,
      final IExpr leftHandSide, final IExpr rightHandSide) {
    return putDownRule(setSymbol, false, leftHandSide, rightHandSide,
        IPatternMap.DEFAULT_RULE_PRIORITY);
  }

  public final IPatternMatcher putDownRule(final int setSymbol, final boolean equalRule,
      final IExpr leftHandSide, final IExpr rightHandSide, final int priority) {
    if (equalRule || leftHandSide.isSymbol()) {
      fEqualDownRules = getEqualDownRules();
      PatternMatcherEquals pmEquals =
          new PatternMatcherEquals(setSymbol, leftHandSide, rightHandSide);
      fEqualDownRules.put(leftHandSide, pmEquals);
      return pmEquals;
    }

    final PatternMatcherAndEvaluator pmEvaluator;
    int patternHash = 0;
    if (!isComplicatedPatternRule(leftHandSide) && !leftHandSide.isCondition()) {
      patternHash = ((IAST) leftHandSide).patternHashCode();
    }
    pmEvaluator =
        new PatternMatcherAndEvaluator(setSymbol, leftHandSide, rightHandSide, true, patternHash);
    if (pmEvaluator.isRuleWithoutPatterns()) {
      fEqualDownRules = getEqualDownRules();
      PatternMatcherEquals pmEquals =
          new PatternMatcherEquals(setSymbol, leftHandSide, rightHandSide);
      fEqualDownRules.put(leftHandSide, pmEquals);
      return pmEquals;
    }

    if (IPatternMap.DEFAULT_RULE_PRIORITY != priority) {
      pmEvaluator.setLHSPriority(priority);
    }

    return insertMatcher(pmEvaluator);
  }

  /**
   * Create a <code>Integrate</code> pattern matching rule.
   *
   * @param leftHandSide left hand side rule with patterns
   * @param rightHandSide right hand side term rewriting rule
   * @param priority the priority of the rule
   */
  public final IPatternMatcher integrate(final IAST leftHandSide, final IExpr rightHandSide,
      final int priority) {
    int patternHash = 0;
    if (!isComplicatedPatternRule(leftHandSide)) {
      patternHash = leftHandSide.patternHashCode();
    }
    final PatternMatcher pmEvaluator = new PatternMatcherAndEvaluator(IPatternMatcher.SET_DELAYED,
        leftHandSide, rightHandSide, false, patternHash);
    // final PatternMatcher pmEvaluator = new PatternMatcherAndEvaluatorMemoize(
    // IPatternMatcher.SET_DELAYED, leftHandSide, Suppliers.memoize(() -> rightHandSide), false,
    // patternHash);
    pmEvaluator.setLHSPriority(priority);
    if (fPatternDownRules == null) {
      fPatternDownRules = new ArrayList<IPatternMatcher>(8000);
      fPriorityDownRules = new IntArrayList(8000);
    }
    fPatternDownRules.add(pmEvaluator);
    fPriorityDownRules.add(priority);
    return pmEvaluator;
  }

  /**
   * Insert a new (or replace an old equivalent) pattern matching rule in the rules data structure.
   *
   * @param newPatternMatcher the new pattern matching rule
   */
  public final PatternMatcher insertMatcher(final PatternMatcher newPatternMatcher) {
    if (fPatternDownRules == null) {
      fPatternDownRules = new ArrayList<IPatternMatcher>();
      fPriorityDownRules = new IntArrayList();
      fPatternDownRules.add(newPatternMatcher);
      fPriorityDownRules.add(newPatternMatcher.getLHSPriority());
      return newPatternMatcher;
    }
    final int size = fPatternDownRules.size();
    final int patternHash = newPatternMatcher.getPatternHash();
    final int lhsPriority = newPatternMatcher.getLHSPriority();
    IPatternMap pmSlotValuesMap = null;
    IExpr pmRHS = null;
    IExpr pmSlotValuesLHS = null;
    // TODO use a binary search in fPriorityDownRules to find the first equal getLHSPriority()
    for (int i = 0; i < size; i++) {
      final int priority = fPriorityDownRules.getInt(i);
      if (priority > lhsPriority) {
        fPatternDownRules.add(i, newPatternMatcher);
        fPriorityDownRules.add(i, lhsPriority);
        return newPatternMatcher;
      } else {
        if (priority == lhsPriority) {
          // There can be "multiple rules" with the same priority
          // Append the new rule matcher behind the last one or replace an existing equivalent rule
          // matcher
          final IPatternMatcher matcher = fPatternDownRules.get(i);
          if (matcher.isPatternHashAllowed(patternHash)) {
            if (IPatternMatcher.EQUIVALENCE_COMPARATOR.compare(newPatternMatcher, matcher) == 0) {
              if (pmSlotValuesMap == null) {
                pmSlotValuesMap = newPatternMatcher.getPatternMap().copy();
                pmSlotValuesMap.initSlotValues();
                pmRHS = pmSlotValuesMap.substituteSymbols(newPatternMatcher.getRHS(), F.NIL);
                pmSlotValuesLHS =
                    pmSlotValuesMap.substitutePatternOrSymbols(newPatternMatcher.getLHS(), true);
              }
              if (equivalentSlots(matcher, pmSlotValuesMap.size(), pmSlotValuesLHS, pmRHS)) {
                fPatternDownRules.set(i, newPatternMatcher);
                fPriorityDownRules.set(i, lhsPriority);
                return newPatternMatcher;
              }
            }
          }
        }
      }
    }

    fPatternDownRules.add(newPatternMatcher);
    fPriorityDownRules.add(lhsPriority);
    return newPatternMatcher;
  }

  /**
   * Test if the matchers are equivalent, comparing the LHS (and possibly RHS-condition), with named
   * patterns replaced by slot values <code>#1, #2, #3,...</code>.
   *
   * @param matcher the existing pattern matcher in the RulesData structure
   * @param newNumberOfPatterns the number of patterns which the new rule contains
   * @param newSlotValuesLHS the left-hand-side of the new rule with patterns replaced by slot
   *        values
   * @param newSlotValuesRHS the right-hand-side of the new rule with pattern symbols replaced by
   *        slot values
   * @return <code>true</code> if the <code>matcher</code>'s LHS and RHS-condition are equivalent to
   *         the new matcher parameters
   */
  private static boolean equivalentSlots(IPatternMatcher matcher, int newNumberOfPatterns,
      IExpr newSlotValuesLHS, IExpr newSlotValuesRHS) {
    IPatternMap oldMap = matcher.getPatternMap();
    if (oldMap.size() != newNumberOfPatterns) {
      return false;
    }
    oldMap = oldMap.copy();
    oldMap.initSlotValues();
    IExpr oldSlotValuesLHS = oldMap.substitutePatternOrSymbols(matcher.getLHS(), true);
    if (oldSlotValuesLHS.equals(newSlotValuesLHS)) {
      IExpr rhs = matcher.getRHS();
      if (newSlotValuesRHS.isCondition() && rhs.isCondition()) {
        IExpr oldSlotValuesRHS = oldMap.substituteSymbols(rhs.second(), F.NIL);
        return newSlotValuesRHS.second().equals(oldSlotValuesRHS);
      }
      return !(rhs.isCondition() || newSlotValuesRHS.isCondition());
    }
    return false;
  }

  public void putfDefaultValues(IExpr expr) {
    putfDefaultValues(DEFAULT_VALUE_INDEX, expr);
  }

  public void putfDefaultValues(int pos, IExpr expr) {
    if (this.fDefaultValues == null) {
      this.fDefaultValues = new OpenIntToIExprHashMap<IExpr>();
    }
    fDefaultValues.put(pos, expr);
  }

  public IPatternMatcher putUpRule(final int setSymbol, final boolean equalRule,
      final IAST leftHandSide, final IExpr rightHandSide) {
    if (equalRule) {
      fEqualUpRules = getEqualUpRules();
      PatternMatcherEquals pmEquals =
          new PatternMatcherEquals(setSymbol, leftHandSide, rightHandSide);
      fEqualUpRules.put(leftHandSide, pmEquals);
      return pmEquals;
    }

    final PatternMatcherAndEvaluator pmEvaluator =
        new PatternMatcherAndEvaluator(setSymbol, leftHandSide, rightHandSide);

    if (pmEvaluator.isRuleWithoutPatterns()) {
      fEqualUpRules = getEqualUpRules();
      PatternMatcherEquals pmEquals =
          new PatternMatcherEquals(setSymbol, leftHandSide, rightHandSide);
      fEqualUpRules.put(leftHandSide, pmEquals);
      return pmEquals;
    }

    fSimplePatternUpRules = getSimplePatternUpRules();
    return addSimplePatternUpRule(leftHandSide, pmEvaluator);
  }

  public boolean removeRule(final int setSymbol, final boolean equalRule,
      final IExpr leftHandSide) {
    if (equalRule) {
      if (fEqualDownRules != null) {
        return fEqualDownRules.remove(leftHandSide) != null;
      }
    }

    final PatternMatcherAndEvaluator pmEvaluator =
        new PatternMatcherAndEvaluator(setSymbol, leftHandSide, null);
    if (pmEvaluator.isRuleWithoutPatterns()) {
      if (fEqualDownRules != null) {
        return fEqualDownRules.remove(leftHandSide) != null;
      }
    }

    boolean evaled = false;
    if (fPatternDownRules != null) {
      int i = 0;
      while (i < fPatternDownRules.size()) {
        IPatternMatcher pm = fPatternDownRules.get(i);
        if (pm.equivalentLHS(pmEvaluator) == 0) {
          fPatternDownRules.remove(i);
          fPriorityDownRules.removeInt(i);
          evaled = true;
        } else {
          i++;
        }
      }
    }
    return evaled;
  }

  @Override
  public String toString() {
    List<IAST> list = definition();
    final int size = list.size();
    if (size == 0) {
      return "";
    }
    // Heuristic capacity: assume ~64 chars per entry to reduce reallocations
    StringBuilder buf = new StringBuilder(size * 64);
    for (int i = 0; i < size; i++) {
      buf.append(list.get(i).toString());
      if (i < size - 1) {
        buf.append(",\n ");
      }
    }
    return buf.toString();
  }
}
