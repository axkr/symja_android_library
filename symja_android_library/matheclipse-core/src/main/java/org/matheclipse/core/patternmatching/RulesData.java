package org.matheclipse.core.patternmatching;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.util.OpenIntToIExprHashMap;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IEvalStepListener;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.ruleindex.RuleDispatchStats;
import org.matheclipse.core.patternmatching.ruleindex.RuleFeatureIndex;
import org.matheclipse.core.patternmatching.ruleindex.RuleIndexValidation;
import org.matheclipse.core.visit.AbstractVisitor;
import org.matheclipse.parser.trie.TrieMatch;
import org.matheclipse.external.fastutil.ints.IntArrayList;

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
   * Prefilter for {@link #fPatternDownRules}, built lazily as soon as the list is longer than
   * {@link Config#RULE_INDEX_MIN_RULES}. Invalidated by {@link #invalidateRuleIndex()} whenever the
   * rule list changes, and never serialized - see {@link #initTransientState()}.
   */
  private transient volatile RuleFeatureIndex fRuleIndex;

  /**
   * <code>true</code> if building a {@link RuleFeatureIndex} for {@link #fPatternDownRules} was
   * tried and did not produce a usable index; prevents rebuilding it on every dispatch.
   */
  private transient volatile boolean fRuleIndexUnusable;

  /**
   * Incremented by {@link #invalidateRuleIndex()}. A {@link RuleFeatureIndex} is only published if
   * the epoch did not change while it was built.
   */
  private transient volatile int fRuleIndexEpoch;

  /**
   * <code>true</code> as soon as one up-value was installed anywhere in this JVM.
   * <p>
   * Up-values are created exclusively by {@link S#UpSet}, {@link S#UpSetDelayed}, {@link S#TagSet}
   * and {@link S#TagSetDelayed}, which all end up in {@link #putUpRule(int, boolean, IAST, IExpr)}
   * - none of the built-in symbols defines one. As long as this stays <code>false</code>,
   * {@link EvalEngine#evalUpRules(IAST)} cannot find anything and does not have to look at the
   * arguments at all.
   * <p>
   * The flag is never reset: a stale <code>true</code> only costs the up-value lookup which would
   * have been done anyway, while a stale <code>false</code> would silently drop a rule.
   */
  private static volatile boolean UP_RULES_DEFINED = false;

  /**
   * Test if any up-value was installed since this JVM started.
   *
   * @return <code>false</code> if no symbol anywhere can have an up-value
   * @see #UP_RULES_DEFINED
   */
  public static boolean isUpRulesDefined() {
    return UP_RULES_DEFINED;
  }

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

  /**
   * @param sizes <code>sizes[0]</code> is the expected number of pattern-free rules
   */
  public RulesData(int[] sizes) {
    clear();
    if (sizes.length > 0 && sizes[0] > 0) {
      fEqualDownRules = new HashMap<IExpr, PatternMatcherEquals>(Math.max(8, sizes[0]));
    }
  }

  /**
   * Run the given visitor on every IAST stored in the rule database. Example: optimize internal
   * memory usage by sharing common objects.
   *
   * @param visitor the visitor which manipulates the IAST objects
   */
  public void accept(AbstractVisitor visitor) {
    forEachUpRule(matcher -> {
      IExpr lhs = matcher.getLHS();
      if (lhs.isASTOrAssociation()) {
        lhs.accept(visitor);
      }
      IExpr rhs = matcher.getRHS();
      if (rhs.isASTOrAssociation()) {
        rhs.accept(visitor);
      }
    });
    forEachDownRule(matcher -> {
      IExpr lhs = matcher.getLHS();
      if (lhs.isASTOrAssociation()) {
        lhs.accept(visitor);
      }
      matcher.getAsAST().accept(visitor);
    });
  }

  /**
   * Replace an existing rule with an equivalent left-hand-side, otherwise append the rule.
   *
   * @param upRules the list of up-rules
   * @param pmEvaluator the new rule
   */
  private static IPatternMatcher addSimplePatternUpRule(List<IPatternMatcher> upRules,
      final PatternMatcher pmEvaluator) {
    for (int i = 0; i < upRules.size(); i++) {
      if (upRules.get(i).equivalentLHS(pmEvaluator) == 0) {
        // same left-hand-side: the new definition replaces the old one
        upRules.set(i, pmEvaluator);
        return pmEvaluator;
      }
    }
    upRules.add(pmEvaluator);
    return pmEvaluator;
  }

  public void clear() {
    invalidateRuleIndex();
    fEqualDownRules = null;
    fPatternDownRules = null;
    fPriorityDownRules = null;
    fEqualUpRules = null;
    fSimplePatternUpRules = null;
  }

  /**
   * The pattern-free rules of <code>map</code> sorted by their left-hand-side, so that the printed
   * definitions have a stable order independent of the hash map iteration order.
   */
  private static List<PatternMatcherEquals> sortedEqualRules(
      Map<IExpr, PatternMatcherEquals> map) {
    if (map == null || map.isEmpty()) {
      return java.util.Collections.emptyList();
    }
    List<PatternMatcherEquals> list = new ArrayList<PatternMatcherEquals>(map.values());
    if (list.size() > 1) {
      list.sort((a, b) -> a.getLHS().compareTo(b.getLHS()));
    }
    return list;
  }

  /**
   * Call <code>consumer</code> for every up-rule: first the pattern-free rules (sorted), then the
   * pattern rules in evaluation order.
   */
  private void forEachUpRule(java.util.function.Consumer<IPatternMatcher> consumer) {
    for (PatternMatcherEquals matcher : sortedEqualRules(fEqualUpRules)) {
      consumer.accept(matcher);
    }
    if (fSimplePatternUpRules != null) {
      for (IPatternMatcher matcher : fSimplePatternUpRules.toArray(new IPatternMatcher[0])) {
        consumer.accept(matcher);
      }
    }
  }

  /**
   * Call <code>consumer</code> for every down-rule: first the pattern-free rules (sorted), then
   * the pattern rules in evaluation order.
   */
  private void forEachDownRule(java.util.function.Consumer<IPatternMatcher> consumer) {
    for (PatternMatcherEquals matcher : sortedEqualRules(fEqualDownRules)) {
      consumer.accept(matcher);
    }
    if (fPatternDownRules != null) {
      for (IPatternMatcher matcher : fPatternDownRules.toArray(new IPatternMatcher[0])) {
        consumer.accept(matcher);
      }
    }
  }

  private int numberOfRules() {
    int size = 0;
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
    return size;
  }

  public List<IAST> definition() {
    ArrayList<IAST> definitionList = new ArrayList<IAST>(numberOfRules());
    forEachUpRule(matcher -> definitionList.add(matcher.getAsAST()));
    forEachDownRule(matcher -> definitionList.add(matcher.getAsAST()));
    return definitionList;
  }

  /**
   * Give the <code>DefaultValues()</code> of a symbol as a list of <code>RuleDelayed</code>
   * (delayed rules) with the left-hand-side wrapped in a <code>HoldPattern()</code> expression.
   *
   * @param symbol the symbol whose default values are requested
   * @return a list of <code>RuleDelayed(HoldPattern(Default(symbol[, n])), value)</code> rules, or
   *         {@link F#NIL} if no defaults are defined
   */
  public IAST defaultValues(ISymbol symbol) {
    if (fDefaultValues == null || fDefaultValues.size() == 0) {
      return F.NIL;
    }
    IASTAppendable result = F.ListAlloc(fDefaultValues.size());
    OpenIntToIExprHashMap<IExpr>.Iterator iter = fDefaultValues.iterator();
    while (iter.hasNext()) {
      iter.advance();
      int key = iter.key();
      IExpr value = iter.value();
      if (key == DEFAULT_VALUE_INDEX) {
        // General default: Default(symbol) :> value
        result.append(F.RuleDelayed(F.HoldPattern(F.unary(S.Default, symbol)), value));
      } else {
        // Positional default: Default(symbol, n) :> value
        result.append(F.RuleDelayed(F.HoldPattern(F.binary(S.Default, symbol, F.ZZ(key))), value));
      }
    }
    return result;
  }


  /**
   * Give the <code>DownValues()</code> of a symbol as a list of <code>RuleDelayed</code> (delayed
   * rules) with the left-hand-side wrapped in a <code>HoldPattern()</code> expression.
   *
   * @return a list of <code>RuleDelayed(HoldPattern(lhs), rhs)</code> rules
   */
  public IAST downValues() {
    IASTAppendable result = F.ListAlloc(numberOfRules() + 1);
    forEachDownRule(
        matcher -> result.append(F.RuleDelayed(F.HoldPattern(matcher.getLHS()), matcher.getRHS())));
    return result;
  }

  /**
   * Give the <code>UpValues()</code> of a symbol as a list of <code>RuleDelayed</code> (delayed
   * rules) with the left-hand-side wrapped in a <code>HoldPattern()</code> expression.
   *
   * @return a list of <code>RuleDelayed(HoldPattern(lhs), rhs)</code> rules
   */
  public IAST upValues() {
    IASTAppendable result = F.ListAlloc(numberOfRules() + 1);
    forEachUpRule(
        matcher -> result.append(F.RuleDelayed(F.HoldPattern(matcher.getLHS()), matcher.getRHS())));
    return result;
  }

  /**
   * Two rule sets are equal if they contain equal rules; used by the serialization tests.
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    RulesData other = (RulesData) obj;
    return java.util.Objects.equals(fEqualDownRules, other.fEqualDownRules)
        && java.util.Objects.equals(fEqualUpRules, other.fEqualUpRules)
        && java.util.Objects.equals(fPatternDownRules, other.fPatternDownRules)
        && java.util.Objects.equals(fSimplePatternUpRules, other.fSimplePatternUpRules);
  }

  @Override
  public int hashCode() {
    return java.util.Objects.hash(fEqualDownRules, fEqualUpRules, fPatternDownRules,
        fSimplePatternUpRules);
  }

  /**
   * Evaluate the pattern matcher against <code>expr</code>, reusing the shared matcher instance
   * when possible.
   *
   * <p>
   * The matcher instances stored in the rule lists hold mutable per-match state, so they formerly
   * were {@link IPatternMatcher#copy() copied} before every single evaluation attempt - the
   * dominating allocation cost for large rule sets. The copy is only really needed when the shared
   * instance is already in use: by another thread, or re-entrantly when the evaluation of a rules
   * right-hand-side (or condition) recurses into the same rule (for example nested
   * <code>Integrate</code> rules). {@link IPatternMatcher#tryAcquire()} detects exactly these two
   * cases, so the fast path works without any allocation.
   *
   * @param patternEvaluator the shared matcher instance from the rule list
   * @param expr the expression which should be matched
   * @param engine the evaluation engine
   * @return {@link F#NIL} if no matching/evaluation was possible
   */
  private static IExpr evalMatcher(final IPatternMatcher patternEvaluator, final IExpr expr,
      EvalEngine engine) {
    if (patternEvaluator.tryAcquire()) {
      try {
        return patternEvaluator.eval(expr, engine);
      } finally {
        patternEvaluator.release();
      }
    }
    // the instance is in use by a recursive evaluation or by another thread - work on a copy
    return patternEvaluator.copy().eval(expr, engine);
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

      if (fPatternDownRules != null) {
        final int patternHash = ((IAST) expr).patternHashCode();
        IEvalStepListener stepListener = engine.getStepListener();
        final boolean isTraceMode =
            Config.TRACE_REWRITE_RULE && engine.isTraceMode() && stepListener != null;

        final List<IPatternMatcher> rules = fPatternDownRules;
        final RuleFeatureIndex index = ruleIndex();
        // rule sets below the index threshold are scanned linearly with or without the index, so
        // they are excluded from the selectivity counter
        final boolean indexable = rules.size() >= RuleFeatureIndex.INDEXABLE_RULES;
        if (index == null || index.ruleCount() != rules.size()) {
          RuleDispatchStats.dispatch(false);
          // index based loop: the right-hand-side of a rule may define a new rule for the same
          // symbol, which an iterator would report as a ConcurrentModificationException
          for (int i = 0; i < rules.size(); i++) {
            IExpr result = evalPatternDownRule(rules.get(i), expr, engine, patternHash,
                stepListener, isTraceMode, indexable);
            if (result.isPresent()) {
              return result;
            }
          }
        } else if (Config.RULE_INDEX_VALIDATE) {
          IExpr result =
              evalValidated(expr, engine, patternHash, stepListener, isTraceMode, index);
          if (result.isPresent()) {
            return result;
          }
        } else {
          RuleDispatchStats.dispatch(true);
          final RuleFeatureIndex.Cursor cursor = index.cursor(expr);
          int ruleIndex;
          while ((ruleIndex = cursor.next()) >= 0) {
            IPatternMatcher patternEvaluator = rules.get(ruleIndex);
            IExpr result = evalPatternDownRule(patternEvaluator, expr, engine, patternHash,
                stepListener, isTraceMode, true);
            if (result.isPresent()) {
              return result;
            }
          }
        }
      }
    } finally {
      engine.setEvalRHSMode(evalRHSMode);
    }
    return F.NIL;
  }

  /**
   * Apply a single pattern down-rule to <code>expr</code>.
   *
   * @return the rewritten expression, or {@link F#NIL} if this rule did not apply and the dispatch
   *         should continue with the next rule
   */
  private IExpr evalPatternDownRule(IPatternMatcher patternEvaluator, final IExpr expr,
      EvalEngine engine, int patternHash, IEvalStepListener stepListener, boolean isTraceMode,
      boolean indexable) {
    RuleDispatchStats.ruleVisited(indexable);
    if (!patternEvaluator.isPatternHashAllowed(patternHash)) {
      return F.NIL;
    }
    RuleDispatchStats.matchAttempt();

    if (isTraceMode) {
      IExpr result = F.NIL;
      stepListener.setUp(expr, engine.getRecursionCounter(), expr);
      try {
        result = evalMatcher(patternEvaluator, expr, engine);
        return result;
      } finally {
        if (result.isPresent()) {
          stepListener.tearDown(result, engine.getRecursionCounter(), true, expr);
        } else {
          stepListener.tearDown(F.NIL, engine.getRecursionCounter(), false, expr);
        }
      }
    }

    IExpr result = evalMatcher(patternEvaluator, expr, engine);
    if (result.isPresent()) {
      if (patternEvaluator.getLHS().isAST(S.Integrate)) {
        if (!expr.equals(result)) {
          return result;
        }
        boolean quietMode = engine.isQuietMode();
        try {
          engine.setQuietMode(false);
          // Endless iteration detected in `1` (rule number `2`) for Rubi pattern-matching rules.
          Errors.printMessage(S.Integrate, "rubiendless",
              F.list(expr, F.ZZ(patternEvaluator.getLHSPriority())), engine);
        } finally {
          engine.setQuietMode(quietMode);
        }
        // The rule rewrote the integral to itself. Returning the unevaluated integral stops the
        // search here. Trying the remaining rules instead lets the expression cycle through the
        // evaluation loop until $IterationLimit is reached, which costs the same result but a
        // multiple of the time.
        return expr;
      }
      return result;
    }
    return F.NIL;
  }

  /**
   * Dispatch with a full linear scan and verify that the {@link RuleFeatureIndex} would have
   * reported the rule which fired. The linear scan determines the result, so an unsound index
   * cannot change the outcome while this mode is enabled.
   *
   * @see Config#RULE_INDEX_VALIDATE
   */
  private IExpr evalValidated(final IExpr expr, EvalEngine engine, int patternHash,
      IEvalStepListener stepListener, boolean isTraceMode, RuleFeatureIndex index) {
    RuleDispatchStats.dispatch(true);
    BitSet candidates = index.candidates(expr);
    final int size = fPatternDownRules.size();
    for (int i = 0; i < size; i++) {
      IPatternMatcher patternEvaluator = fPatternDownRules.get(i);
      IExpr result = evalPatternDownRule(patternEvaluator, expr, engine, patternHash, stepListener,
          isTraceMode, true);
      if (result.isPresent()) {
        RuleIndexValidation.checked(candidates.get(i), expr, patternEvaluator);
        return result;
      }
    }
    return F.NIL;
  }

  /**
   * The prefilter for {@link #fPatternDownRules}, built on first use.
   *
   * @return the index, or <code>null</code> if the rule list is too short to profit from one or no
   *         rule requires any symbol
   */
  private RuleFeatureIndex ruleIndex() {
    final List<IPatternMatcher> rules = fPatternDownRules;
    if (rules == null || rules.size() < Config.RULE_INDEX_MIN_RULES) {
      // checked before the cached index is returned, so that raising the threshold switches the
      // index off even after it was built
      return null;
    }
    RuleFeatureIndex index = fRuleIndex;
    if (index != null) {
      // a rule added or removed by another thread shifts the positions the index refers to; fall
      // back to the linear scan until the index was rebuilt
      return index.ruleCount() == rules.size() ? index : null;
    }
    if (fRuleIndexUnusable) {
      return null;
    }
    final int epoch = fRuleIndexEpoch;
    index = RuleFeatureIndex.build(rules);
    if (index == null) {
      fRuleIndexUnusable = true;
      return null;
    }
    if (epoch != fRuleIndexEpoch || rules != fPatternDownRules
        || index.ruleCount() != rules.size()) {
      // the rule list changed while the index was built - use it for this dispatch only
      return null;
    }
    fRuleIndex = index;
    return index;
  }

  /** Number of pattern down-rules; for diagnostics and benchmarks. */
  public int patternDownRulesSize() {
    return fPatternDownRules == null ? 0 : fPatternDownRules.size();
  }

  /** The pattern down-rules in evaluation order; for diagnostics and benchmarks. */
  public List<IPatternMatcher> patternDownRules() {
    return fPatternDownRules == null ? java.util.Collections.<IPatternMatcher>emptyList()
        : java.util.Collections.unmodifiableList(fPatternDownRules);
  }

  /**
   * The rule index, built if necessary; for diagnostics and benchmarks.
   *
   * @return the index or <code>null</code> if this symbol does not use one
   */
  public RuleFeatureIndex diagnosticRuleIndex() {
    return ruleIndex();
  }

  /** Discard the rule index; called by everything which changes {@link #fPatternDownRules}. */
  private void invalidateRuleIndex() {
    fRuleIndexEpoch++;
    fRuleIndex = null;
    fRuleIndexUnusable = false;
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

    if ((fSimplePatternUpRules != null) && (expression.isASTOrAssociation())) {
      IExpr result;
      for (int i = 0; i < fSimplePatternUpRules.size(); i++) {
        result = evalMatcher(fSimplePatternUpRules.get(i), expression, engine);
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

  /**
   * The pattern-free down-rules, keyed by their left-hand-side.
   *
   * <p>
   * This is a hash map on purpose: a {@link java.util.TreeMap} would use
   * {@link IExpr#compareTo(IExpr)}, which reports <code>1</code> and <code>1.0</code> as equal, so
   * <code>f(1)=a</code> would also answer <code>f(1.0)</code>.
   *
   * @return the map, created if necessary
   */
  public final Map<IExpr, PatternMatcherEquals> getEqualDownRules() {
    if (fEqualDownRules == null) {
      fEqualDownRules = new HashMap<IExpr, PatternMatcherEquals>();
    }
    return fEqualDownRules;
  }

  private Map<IExpr, PatternMatcherEquals> getEqualUpRules() {
    if (fEqualUpRules == null) {
      fEqualUpRules = new HashMap<IExpr, PatternMatcherEquals>();
    }
    return fEqualUpRules;
  }

  private List<IPatternMatcher> getSimplePatternUpRules() {
    if (fSimplePatternUpRules == null) {
      fSimplePatternUpRules = new ArrayList<IPatternMatcher>();
    }
    return fSimplePatternUpRules;
  }

  public final IPatternMatcher putDownRule(final IExpr leftHandSide, final IExpr rightHandSide) {
    return putDownRule(IPatternMatcher.SET_DELAYED, false, leftHandSide, rightHandSide,
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

    final PatternMatcherAndEvaluator pmEvaluator = new PatternMatcherAndEvaluator(setSymbol,
        leftHandSide, rightHandSide, true, recomputePatternHash(leftHandSide));
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
    pmEvaluator.setLHSPriority(priority);
    invalidateRuleIndex();
    if (fPatternDownRules == null) {
      fPatternDownRules = new ArrayList<IPatternMatcher>(8000);
      fPriorityDownRules = new IntArrayList(8000);
    }
    fPatternDownRules.add(pmEvaluator);
    fPriorityDownRules.add(priority);
    return pmEvaluator;
  }

  /**
   * Restore the <code>transient</code> state of all contained pattern matchers after binary
   * deserialization (for example Kryo). {@link PatternMatcher#fPatternMap},
   * {@link PatternMatcher#fLHSPriority} and {@link PatternMatcher#fPatterHash} are declared
   * <code>transient</code> and are therefore not written by a field based serializer.
   *
   * <p>
   * The priority is taken from {@link #fPriorityDownRules}, which is serialized and is exactly
   * aligned with {@link #fPatternDownRules}. The pattern hash is <i>recomputed</i> from the
   * left-hand-side with the same rules used in
   * {@link #putDownRule(int, boolean, IExpr, IExpr, int)}, because a serialized hash would become
   * stale as soon as the <code>ID</code> ordinals of the built-in symbols change.
   *
   * <p>
   * This method is idempotent.
   */
  public void initTransientState() {
    if (fPatternDownRules != null) {
      final int prioritySize = fPriorityDownRules == null ? 0 : fPriorityDownRules.size();
      for (int i = 0; i < fPatternDownRules.size(); i++) {
        IPatternMatcher matcher = fPatternDownRules.get(i);
        if (matcher instanceof PatternMatcher) {
          int patternHash = recomputePatternHash(matcher.getLHS());
          if (i < prioritySize) {
            ((PatternMatcher) matcher).initTransientState(fPriorityDownRules.getInt(i),
                patternHash);
          } else {
            ((PatternMatcher) matcher).initTransientState(patternHash);
          }
        }
      }
    }
    if (fSimplePatternUpRules != null) {
      for (int i = 0; i < fSimplePatternUpRules.size(); i++) {
        IPatternMatcher matcher = fSimplePatternUpRules.get(i);
        if (matcher instanceof PatternMatcher) {
          ((PatternMatcher) matcher).initTransientState(recomputePatternHash(matcher.getLHS()));
        }
      }
    }
  }

  /**
   * Recompute the pattern hash of a rule's left-hand-side. Returns <code>0</code> if this rule must
   * not be pre-filtered by its hash value.
   *
   * @param leftHandSide the left-hand-side of a pattern matching definition
   */
  private static int recomputePatternHash(final IExpr leftHandSide) {
    if (leftHandSide != null && leftHandSide.isAST() //
        && !isComplicatedPatternRule(leftHandSide) //
        && !leftHandSide.isCondition()) {
      return ((IAST) leftHandSide).patternHashCode();
    }
    return 0;
  }

  public final boolean isDefinitionsPresent() {
    return (fEqualDownRules != null && fEqualDownRules.size() > 0) //
        || (fPatternDownRules != null && fPatternDownRules.size() > 0) //
        || (fEqualUpRules != null && fEqualUpRules.size() > 0) //
        || (fSimplePatternUpRules != null && fSimplePatternUpRules.size() > 0);
  }

  /**
   * Insert a new (or replace an old equivalent) pattern matching rule in the rules data structure.
   *
   * <p>
   * <code>fPriorityDownRules</code> is sorted ascending, so the insert position is found by
   * binary search. Rules with the same priority keep their insertion order; within that run an
   * equivalent rule is replaced instead of appended.
   *
   * @param newPatternMatcher the new pattern matching rule
   */
  public final PatternMatcher insertMatcher(final PatternMatcher newPatternMatcher) {
    invalidateRuleIndex();
    if (fPatternDownRules == null) {
      fPatternDownRules = new ArrayList<IPatternMatcher>();
      fPriorityDownRules = new IntArrayList();
      fPatternDownRules.add(newPatternMatcher);
      fPriorityDownRules.add(newPatternMatcher.getLHSPriority());
      return newPatternMatcher;
    }
    final int lhsPriority = newPatternMatcher.getLHSPriority();
    // first index whose priority is greater than lhsPriority
    int low = 0;
    int high = fPriorityDownRules.size();
    while (low < high) {
      int mid = (low + high) >>> 1;
      if (fPriorityDownRules.getInt(mid) <= lhsPriority) {
        low = mid + 1;
      } else {
        high = mid;
      }
    }
    final int insertPosition = low;
    // the run of rules with the same priority ends at insertPosition
    int runStart = insertPosition;
    while (runStart > 0 && fPriorityDownRules.getInt(runStart - 1) == lhsPriority) {
      runStart--;
    }
    if (runStart < insertPosition) {
      final int patternHash = newPatternMatcher.getPatternHash();
      IPatternMap pmSlotValuesMap = null;
      IExpr pmRHS = null;
      for (int i = runStart; i < insertPosition; i++) {
        final IPatternMatcher matcher = fPatternDownRules.get(i);
        // a hash of 0 means "no pre-filtering" on either side
        if (patternHash == 0 || matcher.getPatternHash() == 0
            || matcher.getPatternHash() == patternHash) {
          if (IPatternMatcher.EQUIVALENCE_COMPARATOR.compare(newPatternMatcher, matcher) == 0) {
            if (pmSlotValuesMap == null) {
              pmSlotValuesMap = newPatternMatcher.getPatternMap().copy();
              pmSlotValuesMap.initSlotValues();
              pmRHS = pmSlotValuesMap.substituteSymbols(newPatternMatcher.getRHS(), F.NIL);
            }
            if (equivalentSlots(matcher, pmSlotValuesMap.size(), pmRHS)) {
              fPatternDownRules.set(i, newPatternMatcher);
              return newPatternMatcher;
            }
          }
        }
      }
    }
    fPatternDownRules.add(insertPosition, newPatternMatcher);
    fPriorityDownRules.add(insertPosition, lhsPriority);
    return newPatternMatcher;
  }

  /**
   * Test if the right-hand-side conditions of two matchers with equivalent left-hand-sides are
   * equivalent too, with named patterns replaced by slot values <code>#1, #2, #3,...</code>.
   *
   * <p>
   * The left-hand-sides were already compared by {@link IPatternMatcher#EQUIVALENCE_COMPARATOR},
   * which also handles patterns inside associations; only the conditions of the right-hand-sides
   * still have to be compared here, because <code>f(x_):=1/;x&gt;0</code> and
   * <code>f(x_):=2/;x&lt;0</code> are two different rules.
   *
   * @param matcher the existing pattern matcher in the RulesData structure
   * @param newNumberOfPatterns the number of patterns which the new rule contains
   * @param newSlotValuesRHS the right-hand-side of the new rule with pattern symbols replaced by
   *        slot values
   * @return <code>true</code> if the <code>matcher</code>'s RHS-condition is equivalent to the new
   *         matcher parameters
   */
  private static boolean equivalentSlots(IPatternMatcher matcher, int newNumberOfPatterns,
      IExpr newSlotValuesRHS) {
    IPatternMap oldMap = matcher.getPatternMap();
    if (oldMap.size() != newNumberOfPatterns) {
      return false;
    }
    IExpr rhs = matcher.getRHS();
    if (newSlotValuesRHS.isCondition() && rhs.isCondition()) {
      oldMap = oldMap.copy();
      oldMap.initSlotValues();
      IExpr oldSlotValuesRHS = oldMap.substituteSymbols(rhs.second(), F.NIL);
      return newSlotValuesRHS.second().equals(oldSlotValuesRHS);
    }
    return !(rhs.isCondition() || newSlotValuesRHS.isCondition());
  }

  public void putDefaultValues(int pos, IExpr expr) {
    if (this.fDefaultValues == null) {
      this.fDefaultValues = new OpenIntToIExprHashMap<IExpr>();
    }
    fDefaultValues.put(pos, expr);
  }

  public IPatternMatcher putUpRule(final int setSymbol, final boolean equalRule,
      final IAST leftHandSide, final IExpr rightHandSide) {
    // the only place where fEqualUpRules / fSimplePatternUpRules become non-null
    UP_RULES_DEFINED = true;
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

    return addSimplePatternUpRule(getSimplePatternUpRules(), pmEvaluator);
  }

  /**
   * Remove all rules whose left-hand-side is equivalent to <code>leftHandSide</code>.
   *
   * @param setSymbol the flags of the definition; if one of {@link IPatternMatcher#UPSET},
   *        {@link IPatternMatcher#UPSET_DELAYED}, {@link IPatternMatcher#TAGSET} or
   *        {@link IPatternMatcher#TAGSET_DELAYED} is set, the up-rules are searched, otherwise the
   *        down-rules
   * @param equalRule <code>true</code> if the left-hand-side contains no patterns
   * @param leftHandSide the left-hand-side of the definition
   * @return <code>true</code> if at least one rule was removed
   */
  public boolean removeRule(final int setSymbol, final boolean equalRule,
      final IExpr leftHandSide) {
    final boolean upRule = (setSymbol & (IPatternMatcher.UPSET | IPatternMatcher.UPSET_DELAYED
        | IPatternMatcher.TAGSET | IPatternMatcher.TAGSET_DELAYED)) != 0;
    final Map<IExpr, PatternMatcherEquals> equalRules = upRule ? fEqualUpRules : fEqualDownRules;
    if (equalRule) {
      return equalRules != null && equalRules.remove(leftHandSide) != null;
    }

    final PatternMatcherAndEvaluator pmEvaluator =
        new PatternMatcherAndEvaluator(setSymbol, leftHandSide, F.NIL);
    if (pmEvaluator.isRuleWithoutPatterns()) {
      return equalRules != null && equalRules.remove(leftHandSide) != null;
    }

    if (upRule) {
      return removeEquivalentRules(fSimplePatternUpRules, null, pmEvaluator);
    }
    if (fPatternDownRules != null) {
      invalidateRuleIndex();
    }
    return removeEquivalentRules(fPatternDownRules, fPriorityDownRules, pmEvaluator);
  }

  /**
   * Remove every matcher of <code>rules</code> whose left-hand-side is equivalent to the one of
   * <code>pmEvaluator</code>; <code>priorities</code> (if not <code>null</code>) is kept aligned.
   */
  private static boolean removeEquivalentRules(List<IPatternMatcher> rules,
      IntArrayList priorities, IPatternMatcher pmEvaluator) {
    if (rules == null) {
      return false;
    }
    boolean evaled = false;
    int i = 0;
    while (i < rules.size()) {
      if (rules.get(i).equivalentLHS(pmEvaluator) == 0) {
        rules.remove(i);
        if (priorities != null && i < priorities.size()) {
          priorities.removeInt(i);
        }
        evaled = true;
      } else {
        i++;
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
