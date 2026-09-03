package org.matheclipse.core.patternmatching;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayDeque;
import java.util.List;
import org.matheclipse.core.combinatoric.MultisetPartitionsIterator;
import org.matheclipse.core.combinatoric.NumberPartitionsIterator;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ConditionException;
import org.matheclipse.core.eval.exception.ResultException;
import org.matheclipse.core.eval.exception.ReturnException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.PatternNested;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The standard <b>Pattern Matching Engine</b> implementation.
 * <p>
 * {@code PatternMatcher} is responsible for verifying if a given input expression matches a
 * specific "Left-Hand Side" (LHS) pattern. Unlike the abstract {@link IPatternMatcher}, this class
 * contains the concrete logic to:
 * </p>
 * <ol>
 * <li><b>Compare Structures:</b> Recursively traverse the input and the pattern to ensure they
 * match (e.g., matching heads and arguments).</li>
 * <li><b>Bind Variables:</b> When a {@link org.matheclipse.core.expression.Pattern} (like
 * {@code x_}) is encountered, extract the corresponding part of the input and store it in an
 * {@link IPatternMap}.</li>
 * <li><b>Check Conditions:</b> Verify that any attached conditions ({@code /; condition}) evaluate
 * to {@code True}.</li>
 * </ol>
 *
 * <h3>1. Optimization for Constants</h3>
 * <p>
 * The class distinguishes between rules that contain patterns and rules that are purely constant.
 * </p>
 * <ul>
 * <li><b>Constant Rules (e.g., {@code f[1] -> 0}):</b> The matcher uses a fast {@code equals()}
 * check, bypassing the overhead of the pattern mapping system.</li>
 * <li><b>Pattern Rules (e.g., {@code f[x_] -> x^2}):</b> The matcher initializes an
 * {@link IPatternMap} and performs full structural matching.</li>
 * </ul>
 *
 * <h3>2. Usage Examples</h3>
 *
 * <h4>Basic Structural Check</h4>
 * 
 * <pre>
 * // Define LHS pattern: f[x_Integer]
 * IAST lhs = F.unary(F.Dummy("f"), F.$p(F.Dummy("x"), S.Integer));
 * PatternMatcher matcher = new PatternMatcher(lhs);
 *
 * // Test inputs
 * matcher.test(F.unary(F.Dummy("f"), F.C10)); // Returns true
 * matcher.test(F.unary(F.Dummy("f"), F.a)); // Returns false
 * </pre>
 *
 * <h4>Variable Extraction</h4>
 * 
 * <pre>
 * // Pattern: Sin[x_]
 * IPatternObject xVar = F.$p(F.Dummy("x"));
 * IAST lhs = F.Sin(xVar);
 * PatternMatcher matcher = new PatternMatcher(lhs);
 *
 * IExpr input = F.Sin(F.Pi);
 *
 * if (matcher.test(input)) {
 *   // Retrieve the map containing bindings
 *   IPatternMap bindings = matcher.getPatternMap();
 *   IExpr xValue = bindings.getValue(xVar); // Returns Pi
 * }
 * </pre>
 *
 * @see org.matheclipse.core.patternmatching.IPatternMatcher
 * @see org.matheclipse.core.patternmatching.IPatternMap
 * @see org.matheclipse.core.expression.Pattern
 */
public class PatternMatcher extends IPatternMatcher implements Externalizable {

  /**
   * There are two kinds of matching <code>Entry</code> pairs in the {@link StackMatcher}.
   *
   * <ul>
   * <li>The first expression of the pair must pattern-match the second expression of the pair.
   * <li>If the second expression of the pair is {@link F#NIL}, substitute the symbols in the first
   * expression of the pair and try to evaluate to <code>True</code>.
   * </ul>
   */
  private static final class Entry {
    final IExpr fPatternExpr;
    final IExpr fEvalExpr;

    /**
     * Constructor for the <code>Entry</code>, there the second expression of the pair is
     * {@link F#NIL}. For this type the matcher substitutes the symbols in the first expression of
     * the pair with the matched values and tries to evaluate it to <code>True </code> in
     * {@link StackMatcher}.
     *
     * @param patternExpr
     */
    public Entry(IExpr patternExpr) {
      this.fPatternExpr = patternExpr;
      this.fEvalExpr = F.NIL;
    }

    /**
     * Constructor for the <code>Entry</code>, there the first expression of the pair must
     * pattern-match the second expression of the pair in {@link StackMatcher}.
     *
     * @param patternExpr
     * @param evalExpr
     */
    public Entry(IExpr patternExpr, IExpr evalExpr) {
      this.fPatternExpr = patternExpr;
      this.fEvalExpr = evalExpr;
    }
  }

  /**
   * Matches an <code>IAST</code> with header attribute {@link ISymbol#ORDERLESS}.
   * 
   */
  private final class OrderlessMatcher {

    private final IAST fLHSPatternAST;

    private final IAST fLHSEvalAST;

    /**
     * The used (i.e. matched) expression indexes in the LHS evaluation expression; <code>-1</code>
     * indicates an unused index.
     */
    private int[] fUsedIndex;

    /**
     * Match a pattern expression against an evaluation expression, there the arguments are
     * commutative (i.e. the head of the AST expression has attribute {@link ISymbol#ORDERLESS}
     *
     * @param lhsPatternAST the pattern AST
     * @param lhsEvalAST the evaluation AST
     */
    public OrderlessMatcher(final IAST lhsPatternAST, final IAST lhsEvalAST) {
      this.fLHSPatternAST = lhsPatternAST;
      this.fLHSEvalAST = lhsEvalAST;
      this.fUsedIndex = new int[fLHSPatternAST.argSize()];
      for (int l = 0; l < fUsedIndex.length; l++) {
        fUsedIndex[l] = -1;
      }
    }

    /**
     * Remove the used (i.e. matched) expression indexes in the LHS evaluation expression.
     * 
     * @return an {@link IASTAppendable} of <code>fLHSEvalAST</code> with used indexes removed
     */
    public IASTAppendable removeUsedIndexPositions() {
      return fLHSEvalAST.removePositionsAtCopy(fUsedIndex, fUsedIndex.length);
    }

    /**
     * @param lhsPosition the position in the LHS expression which should actually be matched.
     * @param stackMatcher
     * @return
     */
    public boolean matchOrderlessAST(int lhsPosition, StackMatcher stackMatcher,
        EvalEngine engine) {
      if (lhsPosition >= fLHSPatternAST.size()) {
        return stackMatcher.matchRest();
      }
      final IExpr subPattern = fLHSPatternAST.get(lhsPosition);
      final IExpr[] patternValues = fPatternMap.copyPattern();
      return fLHSEvalAST.exists((arg, i) -> isSubPatternMatched(arg, i, lhsPosition, subPattern,
          patternValues, stackMatcher, engine));
    }

    private boolean isSubPatternMatched(IExpr arg, int i, int lhsPosition, final IExpr subPattern,
        final IExpr[] patternValues, StackMatcher stackMatcher, EvalEngine engine) {
      // plain loop instead of a capturing lambda over the pattern AST - fUsedIndex has one entry
      // per pattern argument
      for (int k = 0; k < fUsedIndex.length; k++) {
        if (fUsedIndex[k] == i) {
          return false;
        }
      }
      boolean matched = false;
      // the caller always passes a non-null stackMatcher
      int lastStackSize = stackMatcher.size();
      try {
        if (stackMatcher.push(subPattern, arg)) {
          fUsedIndex[lhsPosition - 1] = i;
          if (matchOrderlessAST(lhsPosition + 1, stackMatcher, engine)) {
            matched = true;
            return true;
          }
        }
      } finally {
        if (!matched) {
          fPatternMap.resetPattern(patternValues);
          fUsedIndex[lhsPosition - 1] = -1;
        }
        stackMatcher.removeFrom(lastStackSize);
      }
      return false;
    }
  }

  /**
   * Manage a stack of entry-pairs of expressions, which have to match each other or evaluate to
   * <code>true</code>.
   *
   * <p>
   * There are two kinds of matching each entry-pair in the stack:
   *
   * <ul>
   * <li>The first expression of the pair must pattern-match the second expression of the pair.
   * <li>If the second expression of the pair is {@link F#NIL}, substitute the symbols in the first
   * expression of the pair and try to evaluate to <code>True</code>.
   * </ul>
   */
  /* package private */ final class StackMatcher extends ArrayDeque<Entry> {
    private static final long serialVersionUID = 6051475896607762506L;

    final EvalEngine fEngine;

    /**
     * If <code>false</code> an empty stack matches without evaluating
     * {@link PatternMatcher#checkRHSCondition(EvalEngine)}. Used for &quot;probe&quot; matches
     * which only ask <i>whether</i> an expression matches and must not trigger the right-hand-side
     * of the rule.
     */
    final boolean fCheckRHSCondition;

    public StackMatcher(EvalEngine engine) {
      this(engine, true);
    }

    public StackMatcher(EvalEngine engine, boolean checkRHSCondition) {
      fEngine = engine;
      fCheckRHSCondition = checkRHSCondition;
    }

    /**
     * Match the entries of the stack recursively starting from the top entry.
     *
     * <p>
     * There are two kinds of matching each entry-pair in the stack:
     *
     * <ul>
     * <li>The first expression of the pair must pattern-match the second expression of the pair.
     * <li>If the second expression of the pair is {@link F#NIL}, substitute the symbols in the
     * first expression of the pair and try to evaluate to <code>True</code>.
     * </ul>
     *
     * The entry will be popped from the stack if the match succeeds. Otherwise it will be left on
     * the stack.
     *
     * @return <code>true</code> if all entry-pairs on the stack could be matched.
     */
    public boolean matchRest() {
      if (isEmpty()) {
        return fCheckRHSCondition ? checkRHSCondition(fEngine) : true;
      }
      boolean matched = true;
      Entry entry = pop();
      try {
        IExpr evalExpr = entry.fEvalExpr;
        if (evalExpr.isPresent()) {
          matched = matchExpr(entry.fPatternExpr, evalExpr, fEngine, this);
        } else {
          matched = matchTrue(entry.fPatternExpr, fEngine, this);
        }
        return matched;
      } finally {
        if (!matched) {
          push(entry);
        }
      }
    }

    public boolean push(IExpr patternExpr, IExpr evalExpr) {
      if (patternExpr == evalExpr) {
        return true;
      }
      if (patternExpr.isASTOrAssociation()) {
        if (!patternExpr.isFreeOfPatterns()) {
          // insert for delayed evaluation in matchRest() method
          push(new Entry(patternExpr, evalExpr));
          return true;
        }
      } else if (patternExpr instanceof IPatternObject) {
        return matchPattern((IPatternObject) patternExpr, evalExpr, this, fEngine);
      }
      return patternExpr.equals(evalExpr);
    }

    /**
     * Remove all elements starting at the given <code>fromPosition</code>.
     *
     * @param fromPosition
     */
    public void removeFrom(int fromPosition) {
      int len = size();
      while (len > fromPosition) {
        pop();
        len--;
      }
    }
  }

  private static final IASTAppendable[] UNEVALED = new IASTAppendable[] {};

  private static final int[] EMPTY_INT_ARRAY = new int[0];

  /** */
  private static final long serialVersionUID = -6708462090303928690L;

  /**
   * Check if the two left-hand-side pattern expressions are equivalent. (i.e. <code>f[x_,y_]</code>
   * is equivalent to <code>f[a_,b_]</code> )
   *
   * @param patternExpr1
   * @param patternExpr2
   * @param pm1
   * @param pm2
   * @return
   */
  public static boolean equivalent(final IExpr patternExpr1, final IExpr patternExpr2,
      final IPatternMap pm1, IPatternMap pm2) {
    if (!patternExpr1.isPatternExpr()) {
      if (!patternExpr2.isPatternExpr()) {
        return patternExpr1.equals(patternExpr2);
      }
      return false;
    }
    if (patternExpr1.isASTOrAssociation()) {

      if (patternExpr2.isASTOrAssociation()) {
        if (patternExpr1.isAssociation() != patternExpr2.isAssociation()) {
          return false;
        }
        if (patternExpr1.size() != patternExpr2.size()) {
          return false;
        }
        final IAST ast1 = (IAST) patternExpr1;
        final IAST ast2 = (IAST) patternExpr2;
        // use getRule() on both sides - for an association get(i) is the value but getRule(i) the
        // whole rule
        for (int i = 0; i < ast1.size(); i++) {
          if (!isEquivalent(ast1.getRule(i), ast2.getRule(i), pm1, pm2)) {
            return false;
          }
        }
        return true;
      }
      return false;
    }
    if (patternExpr1 instanceof IPatternObject) {
      if (patternExpr2 instanceof IPatternObject) {
        return ((IPatternObject) patternExpr1).equivalent((IPatternObject) patternExpr2, pm1, pm2);
      }
      return false;
    }
    return patternExpr1.equals(patternExpr2);
  }

  private static boolean isEquivalent(IExpr arg1, IExpr arg2, final IPatternMap pm1,
      IPatternMap pm2) {
    if (arg1 == arg2) {
      return true;
    }
    if (arg1.hashCode() != arg2.hashCode()) {
      if (arg1.isPatternExpr() && arg2.isPatternExpr()) {
        return equivalent(arg1, arg2, pm1, pm2);
      }
      return false;
    }
    if (!arg1.isPatternExpr() || !arg2.isPatternExpr()) {
      return arg1.equals(arg2);
    }
    return equivalent(arg1, arg2, pm1, pm2);
  }

  /**
   * Remove parts which are "free of patterns" at the start or the end positions in <code>lhsPattern
   * </code> and <code>lhsEval</code>.
   *
   * <p>
   * A leading (trailing) argument of the pattern which contains no patterns must be equal to the
   * leading (trailing) argument of the evaluation expression, because a {@link ISymbol#FLAT}
   * function argument can only match a contiguous segment.
   *
   * <p>
   * <b>Note:</b> both loops test that the evaluation expression is not already exhausted before
   * accessing it - index <code>0</code> would address the header. The callers currently guarantee
   * <code>lhsPattern.size() &lt;= lhsEval.size()</code> and the substitution of already matched
   * values does not splice sequences into the arguments, so the pattern should never outgrow the
   * evaluation expression; the guards are defensive.
   *
   * @param lhsPattern the expression which can contain pattern-matching objects
   * @param lhsEval the expression which can contain no patterns
   * @return <code>null</code> if a "free of patterns" argument of <code>lhsPattern</code> could not
   *         be removed together with an equal argument of <code>lhsEval</code>; matching is
   *         impossible in that case. Otherwise a pair of copies with the removable parts removed,
   *         or the unchanged input pair if nothing was removable.
   */
  private static IAST[] removeFlat(final IAST lhsPattern, final IAST lhsEval) {
    final int patternArgs = lhsPattern.argSize();
    final int evalArgs = lhsEval.argSize();

    // scan pass - determine the number of removable leading and trailing arguments without
    // copying anything yet
    int front = 0;
    while (front < patternArgs) {
      IExpr temp = lhsPattern.get(front + 1);
      if (!(temp instanceof IPatternObject) && temp.isFreeOfPatterns()) {
        if (front < evalArgs && lhsEval.get(front + 1).equals(temp)) {
          front++;
          continue;
        }
        return null;
      }
      break;
    }

    int end = 0;
    int pIndex = patternArgs;
    int eIndex = evalArgs;
    while (pIndex > front) {
      IExpr temp = lhsPattern.get(pIndex);
      if (!(temp instanceof IPatternObject) && temp.isFreeOfPatterns()) {
        if (eIndex > front && lhsEval.get(eIndex).equals(temp)) {
          pIndex--;
          eIndex--;
          end++;
          continue;
        }
        return null;
      }
      break;
    }

    if (front == 0 && end == 0) {
      // nothing to remove - avoid the copies; only (re)compute the pattern flags like the
      // copying branch below does
      IPatternMap.setPatternFlags(lhsPattern);
      return new IAST[] {lhsPattern, lhsEval};
    }

    IASTAppendable lhsPatternAST = lhsPattern.copyAppendable();
    IASTAppendable lhsEvalAST = lhsEval.copyAppendable();
    for (int k = 0; k < end; k++) {
      lhsPatternAST.remove(lhsPatternAST.argSize());
      lhsEvalAST.remove(lhsEvalAST.argSize());
    }
    for (int k = 0; k < front; k++) {
      lhsPatternAST.remove(1);
      lhsEvalAST.remove(1);
    }
    IPatternMap.setPatternFlags(lhsPatternAST);
    return new IASTAppendable[] {lhsPatternAST, lhsEvalAST};
  }

  /**
   * Remove parts which are "free of patterns" in <code>lhsPattern</code> and <code>lhsEval</code>.
   *
   * @param lhsPattern the expression which can contain pattern-matching objects
   * @param lhsEval the expression which can contain no patterns
   * @return <code>null</code> if no parts could be removed
   */
  private static IAST[] removeOrderless(final IAST lhsPattern, final IAST lhsEval) {
    int iIndex = 1;
    int jIndex = -1;
    while (iIndex < lhsPattern.size()) {
      IExpr temp = lhsPattern.get(iIndex);
      if (!(temp instanceof IPatternObject) && temp.isFreeOfPatterns()) {
        jIndex = lhsEval.indexOf(temp);
        if (jIndex > 0) {
          break;
        }
        return null;
      }
      iIndex++;
    }
    if (jIndex > 0) {
      IASTAppendable lhsPatternAST = lhsPattern.copyAppendable();
      IASTAppendable lhsEvalAST = lhsEval.copyAppendable();
      lhsPatternAST.remove(iIndex);
      lhsEvalAST.remove(jIndex);
      while (iIndex < lhsPatternAST.size()) {
        final IExpr temp = lhsPatternAST.get(iIndex);
        if (!(temp instanceof IPatternObject) && temp.isFreeOfPatterns()) {
          int indx = lhsEvalAST.indexOf(temp);
          if (indx > 0) {
            lhsPatternAST.remove(iIndex);
            lhsEvalAST.remove(indx);
            continue;
          }
          return null;
        }
        iIndex++;
      }
      IPatternMap.setPatternFlags(lhsPatternAST);
      return new IAST[] {lhsPatternAST, lhsEvalAST};
    }
    // nothing to remove - like removeFlat() only (re)compute the pattern flags of the (possibly
    // freshly substituted) pattern
    IPatternMap.setPatternFlags(lhsPattern);
    return new IAST[] {lhsPattern, lhsEval};
  }

  /** priority of the left-hand-side of this matcher */
  protected transient int fLHSPriority;

  protected transient int fPatterHash = 0;

  /**
   * A map from a pattern to a possibly found value during pattern-matching. Will be set to <code>
   * null</code> if the left-hand-side pattern expression contains no pattern.
   */
  protected transient IPatternMap fPatternMap;

  protected transient boolean fThrowIfTrue;


  /** Needed for serialization */
  public PatternMatcher() {
    super(null);
    this.fSetFlags = NOFLAG;
    this.fLHSPriority = IPatternMap.DEFAULT_RULE_PRIORITY;
    this.fThrowIfTrue = false;
    this.fLhsPatternExpr = null;
    this.fPatternMap = null;
  }

  public PatternMatcher(final IExpr patternExpr) {
    this(NOFLAG, patternExpr, true);
  }

  public PatternMatcher(final int setSymbol, final IExpr patternExpr, boolean initAll) {
    super(patternExpr);
    this.fSetFlags = setSymbol;
    this.fLHSPriority = IPatternMap.DEFAULT_RULE_PRIORITY;
    this.fThrowIfTrue = false;
    if (initAll) {
      int[] priority = new int[] {IPatternMap.DEFAULT_RULE_PRIORITY};
      fPatternMap = determinePatterns(priority);
      this.fLHSPriority = priority[0];
      if (this.fLhsPatternExpr.isEvalFlagOn(IAST.CONTAINS_PATTERN_SEQUENCE)) {
        this.fLHSPriority = IPatternMap.DEFAULT_RULE_PRIORITY;
      }
      if (patternExpr.isCondition()) {
        this.fLHSPriority -= 100;
      }
    }
  }

  /**
   * Check if the condition for the right-hand-sides <code>Module()</code> or
   * <code>Condition()</code> expressions evaluates to <code>true</code>. Override it in subclasses.
   *
   * @return <code>true</code>
   * @see PatternMatcherAndEvaluator#checkRHSCondition(EvalEngine)
   */
  public boolean checkRHSCondition(EvalEngine engine) {
    return true;
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    PatternMatcher v = (PatternMatcher) super.clone();
    IPatternMap patternMap = createPatternMap();
    v.fPatternMap = patternMap.copy();
    v.fLHSPriority = fLHSPriority;
    v.fSetFlags = fSetFlags;
    return v;
  }

  @Override
  public IPatternMatcher copy() {
    PatternMatcher v = new PatternMatcher();
    copyBaseFieldsTo(v);
    return v;
  }

  /**
   * Copy the fields declared in {@link IPatternMatcher} and {@link PatternMatcher} into
   * <code>v</code>; the pattern map is copied, the per-match "in use" state is not. Used by the
   * <code>copy()</code> methods of all subclasses.
   *
   * @param v the fresh copy
   */
  protected final void copyBaseFieldsTo(PatternMatcher v) {
    v.fLHSPriority = fLHSPriority;
    v.fThrowIfTrue = fThrowIfTrue;
    v.fLhsPatternExpr = fLhsPatternExpr;
    if (fPatternMap != null) {
      v.fPatternMap = fPatternMap.copy();
    }
    v.fLhsExprToMatch = fLhsExprToMatch;
    v.fSetFlags = fSetFlags;
    v.fPatterHash = fPatterHash;
  }

  public IPatternMap createPatternMap() {
    if (fPatternMap == null) {
      int[] priority = new int[] {IPatternMap.DEFAULT_RULE_PRIORITY};
      fPatternMap = IPatternMap.determinePatterns(fLhsPatternExpr, priority, null);
    }
    return fPatternMap;
  }

  /**
   * Restore the <code>transient</code> fields which binary deserialization (for example Kryo)
   * cannot restore: {@link #fPatternMap}, {@link #fLHSPriority} and {@link #fPatterHash}. The
   * left-hand-side priority is derived from the left-hand-side pattern expression.
   *
   * <p>
   * <b>Note:</b> the pattern hash must be <i>recomputed</i> and never restored from a serialized
   * value, because {@link org.matheclipse.core.interfaces.IAST#patternHashCode()} is derived from
   * {@link org.matheclipse.core.expression.BuiltInSymbol#hashCode()}, which returns the symbol's
   * ordinal in <code>ID</code>. A stale hash would silently prevent a rule from ever matching.
   *
   * @param patternHash the recomputed pattern hash of this rule or <code>0</code> if no hash
   *        pre-filtering is allowed for this rule
   * @see #initTransientState(int, int)
   */
  public void initTransientState(int patternHash) {
    int[] priority = new int[] {IPatternMap.DEFAULT_RULE_PRIORITY};
    fPatternMap = IPatternMap.determinePatterns(fLhsPatternExpr, priority, null);
    fLHSPriority = priority[0];
    fPatterHash = patternHash;
  }

  /**
   * Restore the <code>transient</code> fields which binary deserialization (for example Kryo)
   * cannot restore: {@link #fPatternMap}, {@link #fLHSPriority} and {@link #fPatterHash}.
   *
   * <p>
   * Without this step the pattern map is only rebuilt lazily in {@link #createPatternMap()}, the
   * priority stays <code>0</code> and the pattern hash stays <code>0</code>. A zero pattern hash
   * silently disables the hash pre-filter in {@link IPatternMatcher#isPatternHashAllowed(int)},
   * which keeps the rules correct but forces every rule to be tried on every evaluation.
   *
   * @param lhsPriority the authoritative priority of this rule
   * @param patternHash the recomputed pattern hash of this rule or <code>0</code> if no hash
   *        pre-filtering is allowed for this rule
   * @see #initTransientState(int)
   */
  public void initTransientState(int lhsPriority, int patternHash) {
    int[] priority = new int[] {IPatternMap.DEFAULT_RULE_PRIORITY};
    fPatternMap = IPatternMap.determinePatterns(fLhsPatternExpr, priority, null);
    fLHSPriority = lhsPriority;
    fPatterHash = patternHash;
  }

  public IPatternMap determinePatterns(int[] priority) {
    return IPatternMap.determinePatterns(fLhsPatternExpr, priority, null);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    // IPatternMatcher#equals() already compares the classes and the left-hand-sides
    return super.equals(obj) && fSetFlags == ((PatternMatcher) obj).fSetFlags;
  }

  private int equivalent(final IPatternMatcher obj) {
    if (this == obj) {
      return 0;
    }
    if (obj instanceof PatternMatcher) {
      return equivalentLHS(obj);
    }
    return fLhsPatternExpr.compareTo(obj.fLhsPatternExpr);
  }

  @Override
  public int equivalentLHS(final IPatternMatcher obj) {
    if (!(obj instanceof PatternMatcher)) {
      return fLhsPatternExpr.compareTo(obj.fLhsPatternExpr);
    }
    final PatternMatcher pm = (PatternMatcher) obj;
    // use createPatternMap() - the field is still null for matchers created with initAll==false or
    // by deserialization
    final IPatternMap thisMap = createPatternMap();
    final IPatternMap otherMap = pm.createPatternMap();
    if (thisMap.size() != otherMap.size()) {
      return (thisMap.size() < otherMap.size()) ? -1 : 1;
    }
    if (isRuleWithoutPatterns()) {
      return fLhsPatternExpr.compareTo(pm.fLhsPatternExpr);
    }
    if (equivalent(fLhsPatternExpr, pm.fLhsPatternExpr, thisMap, otherMap)) {
      return 0;
    }
    return fLhsPatternExpr.compareTo(obj.fLhsPatternExpr);
  }

  @Override
  public int equivalentTo(IPatternMatcher patternMatcher) {
    if (fLHSPriority < patternMatcher.getLHSPriority()) {
      return -1;
    }
    if (fLHSPriority > patternMatcher.getLHSPriority()) {
      return 1;
    }
    return equivalent(patternMatcher);
  }

  /** {@inheritDoc} */
  @Override
  public IExpr eval(final IExpr leftHandSide, EvalEngine engine) {
    return F.NIL;
  }

  /**
   * Get the priority of this pattern-matcher. Lower values have higher priorities.
   *
   * @return the priority
   */
  @Override
  public int getLHSPriority() {
    return fLHSPriority;
  }

  @Override
  public int getPatternHash() {
    return fPatterHash;
  }

  @Override
  public IPatternMap getPatternMap() {
    return fPatternMap;
  }

  /**
   * Returns the matched pattern in the order they appear in the pExpr
   *
   * @param resultList
   * @param pExpr
   */
  @Override
  public void getPatterns(final List<IExpr> resultList, final IExpr pExpr) {
    if (pExpr.isASTOrAssociation()) {
      ((IAST) pExpr).forEach(x -> getPatterns(resultList, x), 0);
    } else if (pExpr.isPattern()) {
      resultList.add(fPatternMap.getValue((IPattern) pExpr));
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + fSetFlags;
    return result;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPatternHashAllowed(int patternHash) {
    return true;
  }

  /**
   * Returns true if the given expression contains no patterns
   *
   */
  @Override
  public final boolean isRuleWithoutPatterns() {
    return createPatternMap().isRuleWithoutPatterns();
  }

  protected boolean matchAST(IAST lhsPatternAST, final IExpr lhsEvalExpr, EvalEngine engine,
      StackMatcher stackMatcher) {
    if (!(lhsEvalExpr instanceof IAST)) {
      return false;
    }

    if (lhsPatternAST.isFreeOfPatterns() && lhsPatternAST.equals(lhsEvalExpr)) {
      return stackMatcher.matchRest();
    }

    IAST lhsEvalAST = (IAST) lhsEvalExpr;
    final ISymbol sym = lhsPatternAST.topHead();

    // Try to remove/evaluate orderless/flat parts when pattern length <= eval length
    if (lhsPatternAST.size() <= lhsEvalAST.size()) {
      if (lhsPatternAST.head().equals(lhsEvalAST.head())) {
        // try Orderless substitution/removal
        if (lhsPatternAST.isOrderlessAST()) {
          IExpr temp = fPatternMap.substituteASTPatternOrSymbols(lhsPatternAST, engine)
              .orElse(lhsPatternAST);
          if (temp.isAST(lhsPatternAST.head())) {
            lhsPatternAST = (IAST) temp;
            IAST[] removed = removeOrderless(lhsPatternAST, lhsEvalAST);
            if (removed != null) {
              lhsPatternAST = removed[0];
              lhsEvalAST = removed[1];
            }
          }
        } else if (lhsPatternAST.isFlatAST()) {
          // try Flat substitution/removal
          IExpr temp = fPatternMap.substituteASTPatternOrSymbols(lhsPatternAST, engine)
              .orElse(lhsPatternAST);
          if (temp.isAST(lhsPatternAST.head())) {
            // keep the substituted pattern like in the Orderless branch above, even if removeFlat()
            // removes nothing - the already bound values should not be re-matched from scratch
            lhsPatternAST = (IAST) temp;
            IAST[] removed = removeFlat(lhsPatternAST, lhsEvalAST);
            if (removed != null) {
              lhsPatternAST = removed[0];
              lhsEvalAST = removed[1];
            }
          }
        }
      }

      // handle Flat (including Flat+Orderless special cases)
      if (lhsPatternAST.isFlatAST() && sym.equals(lhsEvalAST.topHead())
          && !(lhsPatternAST.isOrderlessAST() && lhsPatternAST.size() == lhsEvalAST.size())) {
        if (!matchHeads(lhsPatternAST, lhsEvalAST, engine)) {
          return false;
        }
        if (lhsPatternAST.size() == 1 && lhsEvalAST.size() == 1) {
          return stackMatcher.matchRest();
        }
        return matchFlatAndFlatOrderless(sym, lhsPatternAST, lhsEvalAST, engine, stackMatcher);
      }
    }

    final int lhsEvalSize = lhsEvalAST.size();

    // handle pattern sequences (contains Sequence objects)
    if (lhsPatternAST.isEvalFlagOn(IAST.CONTAINS_PATTERN_SEQUENCE)) {
      if (!matchHeads(lhsPatternAST, lhsEvalAST, engine)) {
        return false;
      }

      // consume the leading arguments which are neither a pattern sequence nor the trailing
      // PatternTest special case pairwise; this replaces the former self-recursion which created a
      // rest() copy of both AST's for every consumed argument
      int pOffset = 1;
      int eOffset = 1;
      while (lhsPatternAST.size() - pOffset > 0 && lhsEvalAST.size() - eOffset > 0) {
        final IExpr firstPattern = lhsPatternAST.get(pOffset);
        if (firstPattern.isPatternSequence(false)) {
          break;
        }
        if (lhsPatternAST.size() - pOffset == 1 && firstPattern.isAST(S.PatternTest, 3)) {
          break;
        }
        if (!matchSubExpr(firstPattern, lhsEvalAST.get(eOffset), engine)) {
          return false;
        }
        pOffset++;
        eOffset++;
      }
      if (pOffset > 1) {
        // materialize the remaining slices once
        IASTAppendable patternTail = lhsPatternAST.copyFrom(pOffset);
        patternTail.addEvalFlags(IAST.CONTAINS_PATTERN_SEQUENCE);
        lhsPatternAST = patternTail;
        lhsEvalAST = lhsEvalAST.copyFrom(eOffset);
      }

      if (lhsPatternAST.isEmpty() && lhsEvalAST.isEmpty()) {
        return stackMatcher.matchRest();
      }

      final int lastPosition = lhsPatternAST.argSize();
      if (lastPosition == 1 && lhsPatternAST.get(lastPosition).isAST(S.PatternTest, 3)) {
        if (lhsPatternAST.size() <= lhsEvalAST.size()) {
          IAST patternTest = (IAST) lhsPatternAST.get(lastPosition);
          if (patternTest.arg1().isPatternSequence(false)) {
            IASTAppendable seq = F.Sequence();
            seq.appendAll(lhsEvalAST, lastPosition, lhsEvalAST.size());
            if (((IPatternSequence) patternTest.arg1()).matchPatternSequence(seq, fPatternMap,
                lhsPatternAST.topHead())
                // test the sequence before the rest is matched - matchAST() evaluates the
                // right-hand-side condition of the rule at the end, which must not run for a
                // sequence which fails its pattern test
                && fPatternMap.isPatternTest(patternTest.arg1(), patternTest.arg2(), engine)) {
              IAST lhsPatternEndRemoved = lhsPatternAST.removeFromEnd(lastPosition);
              IPatternMap.setPatternFlags(lhsPatternEndRemoved);
              return matchAST(lhsPatternEndRemoved, lhsEvalAST.removeFromEnd(lastPosition),
                  engine, stackMatcher);
            }
          }
        }
      } else if (lhsPatternAST.size() > 1 && lhsPatternAST.arg1().isPatternSequence(false)) {
        IPatternSequence patternSequence = (IPatternSequence) lhsPatternAST.arg1();
        return matchBlankSequence(patternSequence, lhsPatternAST, 1, lhsEvalAST, engine,
            stackMatcher);
      }
      return false;
    }

    // size and head must match for the remaining cases
    if (lhsPatternAST.size() != lhsEvalSize || !matchHeads(lhsPatternAST, lhsEvalAST, engine)) {
      return false;
    }

    // Orderless with more than 2 elements: use partition iterator
    if (lhsPatternAST.isOrderlessAST() && lhsPatternAST.size() > 2) {
      // both AST's have the same size here, so every pattern argument is matched against exactly
      // one evaluated argument and no argument has to be wrapped in the head
      OrderlessStepVisitor visitor =
          new OrderlessStepVisitor(sym, lhsPatternAST, lhsEvalAST, stackMatcher, fPatternMap);
      MultisetPartitionsIterator iter =
          new MultisetPartitionsIterator(visitor, lhsPatternAST.argSize());
      return !iter.execute();
    }

    // fallback: match sequentially
    return matchASTSequence(lhsPatternAST, lhsEvalAST, 0, engine, stackMatcher);
  }


  /**
   * Return <code>true</code> if the {@link IAST#head()} expressions of the <code>evaledAST</code>
   * and the <code>patternAST</code> match each other.
   *
   * @param patternAST the expression which contains the patterns
   * @param evaledAST the expression which should match the pattern
   * @param engine
   * @return <code>true</code> if the {@link IAST#head()} expressions match; <code>false</code>
   *         otherwise
   */
  private boolean matchHeads(IAST patternAST, IAST evaledAST, EvalEngine engine) {
    IExpr patternHead = patternAST.head();
    if (patternHead.isSymbol()) {
      // this is the 99 % case
      return patternHead == evaledAST.head();
    }
    return matchExpr(patternHead, evaledAST.head(), engine);
  }

  private boolean matchASTExpr(IAST lhsPatternAST, final IExpr lhsEvalExpr, EvalEngine engine,
      StackMatcher stackMatcher) {
    boolean matched = false;
    IExpr[] patternValues = fPatternMap.copyPattern();
    int lastStackSize = stackMatcher.size();

    try {
      matched = matchAST(lhsPatternAST, lhsEvalExpr, engine, stackMatcher);
      if (!matched) {
        fPatternMap.resetPattern(patternValues);
        // The CONTAINS_* pattern flags are computed lazily. A sub-pattern that was rebuilt while
        // substituting an already bound variable (Power(y_,n_.) -> Power(x,n_.), once y is bound)
        // carries none of them yet, so reading the flags raw would skip the default-value branch
        // below and Power(x,n_.) would fail to match x. Compute them first.
        lhsPatternAST.isFreeOfPatterns();
        if ((lhsPatternAST.getEvalFlags()
            & IAST.CONTAINS_DEFAULT_PATTERN) == IAST.CONTAINS_DEFAULT_PATTERN) {
          if (lhsEvalExpr.isASTOrAssociation() //
              && lhsPatternAST.hasOptionalArgument() //
              && !lhsPatternAST.isOrderlessAST()) {
            // TODO for Power[x_, y_.] matching Power[a,b] test both cases Power[a,b] &&
            // Power[Power[a,b],1]
            IExpr temp = matchOptionalArgumentsAST(lhsPatternAST.topHead(), lhsPatternAST,
                (IAST) lhsEvalExpr, engine);
            if (temp.isPresent()) {
              matched = matchExpr(temp, lhsEvalExpr, engine, stackMatcher);
            }
          } else {
            IExpr head = lhsPatternAST.head();
            if (head.isSymbol()) {
              ISymbol patternHead = (ISymbol) head;
              IExpr evalHead = lhsEvalExpr.head();
              if ((lhsPatternAST.getEvalFlags()
                  & IAST.CONTAINS_ALL_DEFAULT_PATTERN) == IAST.CONTAINS_ALL_DEFAULT_PATTERN
                  && patternHead.hasOneIdentityAttribute() && lhsPatternAST.isOrderlessAST()) {
                if (patternHead.equals(evalHead) && lhsEvalExpr.isAST()) {
                  if (lhsPatternAST.argSize() >= lhsEvalExpr.size()) {
                    IAST lhsEvalAST = (IAST) lhsEvalExpr;
                    int[] ignoredPositions = new int[lhsEvalAST.size()];
                    matched = matchDefaultArgsRecursive(patternHead, evalHead, lhsPatternAST,
                        lhsEvalAST, 1, ignoredPositions, engine, stackMatcher);
                  }
                } else {
                  IAST lhsEvalAST = F.unaryAST1(patternHead, lhsEvalExpr);
                  int[] ignoredPositions = new int[lhsEvalAST.size()];
                  matched = matchDefaultArgsRecursive(patternHead, evalHead, lhsPatternAST,
                      lhsEvalAST, 1, ignoredPositions, engine, stackMatcher);
                }
              }
              if (!matched) {
                IExpr temp = matchDefaultArgumentsAST(patternHead, lhsPatternAST, engine);
                if (temp.isPresent()) {
                  matched = matchExpr(temp, lhsEvalExpr, engine, stackMatcher);
                }
              }
            }
          }
        }
      }
    } finally {
      if (!matched) {
        fPatternMap.resetPattern(patternValues);
        stackMatcher.removeFrom(lastStackSize);
      }
    }
    return matched;
  }

  /**
   * Assign the arguments of <code>lhsEvalAST</code> one by one to a not yet used argument of
   * <code>lhsPatternAST</code>; the pattern arguments which are left over at the end must all be
   * matchable by their {@link S#Default} value.
   *
   * <p>
   * <code>ignoredPositions[evalPosition - 1]</code> holds the position of the pattern argument
   * which was assigned to the left-hand-side evaluation argument at <code>evalPosition</code>, or
   * <code>0</code> if no assignment was made (yet). A pattern argument may be used only once, which
   * is what {@link #isIgnoredPosition(int[], int)} tests.
   *
   * @param patternHead the head symbol of the left-hand-side pattern, used to look up the default
   *        values
   * @param evalHead the head of the left-hand-side evaluation expression
   * @param lhsPatternAST left-hand-side which contains only patterns with default values
   * @param lhsEvalAST the expression which should be matched
   * @param lhsEvalStartPosition the position of the left-hand-side evaluation argument which should
   *        be assigned in this step
   * @param ignoredPositions the pattern positions already assigned to an evaluation argument
   * @param engine the evaluation engine
   * @param stackMatcher a stack matcher
   * @return <code>true</code> if a complete assignment could be found
   */
  private boolean matchDefaultArgsRecursive(ISymbol patternHead, IExpr evalHead, IAST lhsPatternAST,
      final IAST lhsEvalAST, int lhsEvalStartPosition, int[] ignoredPositions, EvalEngine engine,
      StackMatcher stackMatcher) {
    if (lhsEvalStartPosition >= lhsEvalAST.size()) {
      if (matchDefaultsCompleteRecursive(patternHead, lhsPatternAST, ignoredPositions, engine)) {
        if (stackMatcher.matchRest()) {
          return true;
        }
      }
      // the caller restores the pattern values and the stack of this attempt
      return false;
    }
    final IExpr lhsEvalExpr = lhsEvalAST.get(lhsEvalStartPosition);
    int defaultAndOptionalCounter = 0;
    for (int i = 1; i < lhsPatternAST.size(); i++) {
      if (isIgnoredPosition(ignoredPositions, i)) {
        continue;
      }
      // 1. step search for same header expressions in the pattern
      if (lhsPatternAST.get(i).isPatternDefault()) {
        defaultAndOptionalCounter++;
        continue;
      }
      if (tryMatchDefaultArg(patternHead, evalHead, lhsPatternAST, lhsEvalAST, lhsEvalStartPosition,
          lhsEvalExpr, i, ignoredPositions, engine, stackMatcher)) {
        return true;
      }
    }

    if (defaultAndOptionalCounter <= lhsEvalStartPosition) {
      // no more optional/default values
      return false;
    }
    for (int i = 1; i < lhsPatternAST.size(); i++) {
      if (isIgnoredPosition(ignoredPositions, i)) {
        continue;
      }
      // 2. step search for unequal header expressions in the pattern
      if (lhsPatternAST.get(i).isPatternDefault()) {
        if (tryMatchDefaultArg(patternHead, evalHead, lhsPatternAST, lhsEvalAST,
            lhsEvalStartPosition, lhsEvalExpr, i, ignoredPositions, engine, stackMatcher)) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * Assign the pattern argument at <code>patternPosition</code> to the left-hand-side evaluation
   * argument at <code>lhsEvalStartPosition</code> and continue with the next evaluation argument.
   *
   * <p>
   * If the attempt fails, the pattern values, the stack matcher and the
   * <code>ignoredPositions</code> entry of this step are restored, so that the caller can try the
   * next pattern argument on a clean state. Without that, a pattern position consumed by an
   * abandoned attempt would stay marked as used and the bindings of the abandoned attempt would be
   * visible to the next one.
   *
   * @return <code>true</code> if this assignment leads to a complete match
   * @see #matchDefaultArgsRecursive(ISymbol, IExpr, IAST, IAST, int, int[], EvalEngine,
   *      StackMatcher)
   */
  private boolean tryMatchDefaultArg(ISymbol patternHead, IExpr evalHead, IAST lhsPatternAST,
      final IAST lhsEvalAST, int lhsEvalStartPosition, IExpr lhsEvalExpr, int patternPosition,
      int[] ignoredPositions, EvalEngine engine, StackMatcher stackMatcher) {
    final IExpr[] patternValues = fPatternMap.copyPattern();
    final int lastStackSize = stackMatcher.size();
    final int lastIgnoredPosition = ignoredPositions[lhsEvalStartPosition - 1];
    boolean matched = false;
    try {
      if (matchSubExpr(lhsPatternAST.get(patternPosition), lhsEvalExpr, engine)) {
        ignoredPositions[lhsEvalStartPosition - 1] = patternPosition;
        matched = matchDefaultArgsRecursive(patternHead, evalHead, lhsPatternAST, lhsEvalAST,
            lhsEvalStartPosition + 1, ignoredPositions, engine, stackMatcher);
      }
      return matched;
    } finally {
      if (!matched) {
        ignoredPositions[lhsEvalStartPosition - 1] = lastIgnoredPosition;
        stackMatcher.removeFrom(lastStackSize);
        fPatternMap.resetPattern(patternValues);
      }
    }
  }

  /**
   * Test if the pattern argument at <code>patternPosition</code> was already assigned to one of the
   * left-hand-side evaluation arguments.
   *
   * @param ignoredPositions the pattern positions already assigned to an evaluation argument;
   *        <code>0</code> marks a not yet assigned entry
   * @param patternPosition the position of a pattern argument (always <code>&gt;= 1</code>)
   */
  private static boolean isIgnoredPosition(int[] ignoredPositions, int patternPosition) {
    for (int k = 0; k < ignoredPositions.length; k++) {
      if (patternPosition == ignoredPositions[k]) {
        return true;
      }
    }
    return false;
  }

  private boolean matchDefaultsCompleteRecursive(ISymbol patternHead, IAST lhsPatternAST,
      int[] ignoredPositions, EvalEngine engine) {

    for (int j = 1; j < lhsPatternAST.size(); j++) {
      if (isIgnoredPosition(ignoredPositions, j)) {
        continue;
      }

      IExpr temp = lhsPatternAST.get(j);
      if (temp.isPatternDefault()) {
        if (temp.isOptional()) {
          IAST optional = (IAST) temp;
          IExpr optionalValue =
              (optional.isAST2()) ? optional.arg2() : patternHead.getDefaultValue();
          if (optionalValue.isPresent()) {
            if (matchSubExpr(temp.first(), optionalValue, engine)) {
              continue;
            }
          }
          return false;
        }
        IExpr positionDefaultValue = patternHead.getDefaultValue(j);
        if (positionDefaultValue.isPresent()) {
          if (((IPatternObject) temp).matchPattern(positionDefaultValue, fPatternMap)) {
            continue;
          }
          return false;
        }
        IExpr commonDefaultValue = patternHead.getDefaultValue();
        if (commonDefaultValue.isPresent()) {
          if (((IPatternObject) temp).matchPattern(commonDefaultValue, fPatternMap)) {
            continue;
          }
          return false;
        }
      } else if (temp.isASTOrAssociation()) {
        if (!matchDefaultsCompleteRecursive(patternHead, (IAST) temp, EMPTY_INT_ARRAY, engine)) {
          return false;
        }
        continue;
      }
      return false;
    }
    return true;
  }

  /**
   * Match all sub-expressions which contain no pattern objects if possible (i.e. no Flat or
   * Orderless expressions,...)
   *
   * <p>
   * Distinguishes between "equally" matched list-expressions and list expressions with <code>
   * expr.isPatternExpr()==true</code>.
   *
   * @param lhsPatternAST
   * @param lhsEvalAST
   * @param lhsEvalOffset
   * @param stackMatcher
   * @return
   */
  private boolean matchASTSequence(IAST lhsPatternAST, IAST lhsEvalAST, final int lhsEvalOffset,
      EvalEngine engine, StackMatcher stackMatcher) {
    // distinguish between "equally" matched list-expressions and AST expressions with
    // "CONTAINS_PATTERN" flag
    IExpr[] patternValues = fPatternMap.copyPattern();
    int lastStackSize = stackMatcher.size();
    boolean matched = false;
    try {
      IExpr head = lhsPatternAST.head();
      boolean flat = lhsPatternAST.isFlatAST();
      boolean oneIdentity = head.isSymbol() ? ((ISymbol) head).hasOneIdentityAttribute() : false;
      if (lhsPatternAST.size() == lhsEvalAST.size()) {
        IAST[] removed = remove(lhsPatternAST, lhsEvalAST, engine, stackMatcher);
        if (removed == null) {
          return false;
        }
        if (removed.length > 0) {
          lhsPatternAST = removed[0];
          lhsEvalAST = removed[1];
          if (lhsPatternAST.size() == 2) {
            matched = matchExpr(lhsPatternAST.arg1(), lhsEvalAST.arg1(), engine, stackMatcher);
            if (!matched) {
              return false;
            }
            matched = stackMatcher.matchRest();
            return matched;
          } else if (lhsPatternAST.isEmpty()) {
            matched = stackMatcher.matchRest();
            return matched;
          }
        }
      }

      for (int i = 1; i < lhsPatternAST.size(); i++) {
        IExpr patternArg = lhsPatternAST.getRule(i);
        IExpr evalArg = lhsEvalAST.getRule(lhsEvalOffset + i);
        if (!oneIdentity && flat && patternArg instanceof IPatternObject) {
          // wrap each argument of the Flat expression with the head symbol because of missing
          // OneIdentity attribute
          evalArg = F.unaryAST1(head, evalArg);
        }
        if (!stackMatcher.push(patternArg, evalArg)) {
          matched = false;
          return false;
        }
      }
      matched = stackMatcher.matchRest();
      return matched;
    } finally {
      if (!matched) {
        stackMatcher.removeFrom(lastStackSize);
        fPatternMap.resetPattern(patternValues);
      }
    }
  }

  /**
   * Test first if <code>functionID = lhsPatternAST.headID()</code> is a special pattern-matching
   * construct (i.e. <code>
   * Association, HoldPattern, Literal, Condition, Alternatives, Except, Complex, Rational, Optional, PatternTest, Verbatim
   * </code>). If <code>true</code> evaluate the special pattern-matching construct otherwise
   * continue with <code>lhsPatternAST</code> pattern matching.
   *
   * @param lhsPatternAST left-hand-side pattern AST
   * @param lhsEvalExpr left-hand-side expression which should be matched by the pattern expression
   * @param engine the evaluation engine
   * @param stackMatcher a stack matcher
   * @return
   */
  private boolean matchASTSpecialBuiltIn(IAST lhsPatternAST, final IExpr lhsEvalExpr,
      EvalEngine engine, StackMatcher stackMatcher) {
    int functionID = lhsPatternAST.headID();
    if (functionID >= ID.Alternatives && functionID <= ID.Verbatim) {
      if (lhsPatternAST.size() == 2 && functionID >= ID.Except) {
        switch (functionID) {
          case ID.Except:
            return matchExcept1(lhsPatternAST, lhsEvalExpr, stackMatcher, engine);
          case ID.HoldPattern:
          case ID.Literal:
            return matchHoldPattern(lhsPatternAST, lhsEvalExpr, stackMatcher, engine);
          case ID.KeyValuePattern:
            return matchKeyValuePattern(lhsPatternAST, lhsEvalExpr, stackMatcher, engine);
          case ID.Optional:
            return matchOptional(lhsPatternAST, lhsEvalExpr, stackMatcher, engine);
          case ID.Verbatim:
            return matchVerbatim(lhsPatternAST, lhsEvalExpr, stackMatcher);
          default:
        }
      } else if (lhsPatternAST.size() == 3 && functionID >= ID.Complex
          && functionID <= ID.Rational) {
        switch (functionID) {
          case ID.Complex:
            return matchComplex(lhsPatternAST, lhsEvalExpr, stackMatcher, engine);
          case ID.Condition:
            return matchCondition(lhsPatternAST, lhsEvalExpr, engine, stackMatcher);
          case ID.Except:
            return matchExcept2(lhsPatternAST, lhsEvalExpr, stackMatcher, engine);
          case ID.Optional:
            return matchOptional(lhsPatternAST, lhsEvalExpr, stackMatcher, engine);
          case ID.PatternTest:
            return matchPatternTest(lhsPatternAST, lhsEvalExpr, stackMatcher, engine);
          case ID.Rational:
            return matchRational(lhsPatternAST, lhsEvalExpr, stackMatcher, engine);
          default:
        }
      } else if (functionID == ID.Alternatives) {
        return matchAlternatives(lhsPatternAST, lhsEvalExpr, engine, stackMatcher);
      } else if (functionID == ID.Association) {
        return matchAssociation(lhsPatternAST, lhsEvalExpr, stackMatcher, engine);
      }
    }

    return matchASTExpr(lhsPatternAST, lhsEvalExpr, engine, stackMatcher);
  }

  /**
   * Match an IAST with head {@link S#HoldPattern} or {@link S#Literal}.
   * 
   * @param lhsPatternAST
   * @param lhsEvalExpr
   * @param stackMatcher
   * @param engine
   * @return
   */
  private boolean matchHoldPattern(IAST lhsPatternAST, final IExpr lhsEvalExpr,
      StackMatcher stackMatcher, EvalEngine engine) {
    boolean matched = false;
    final IExpr[] patternValues = fPatternMap.copyPattern();
    try {
      matched = matchExpr(lhsPatternAST.arg1(), lhsEvalExpr, engine, stackMatcher);
    } finally {
      if (!matched) {
        fPatternMap.resetPattern(patternValues);
      }
    }
    return matched;
  }

  /**
   * Match an IAST with head {@link S#Verbatim} with 1 argument.
   *
   * <p>
   * <code>Verbatim(expr)</code> matches <code>expr</code> literally, even if <code>expr</code>
   * contains pattern objects. It binds nothing, so on success matching continues with the remaining
   * entries of the <code>stackMatcher</code>.
   *
   * @param lhsPatternAST
   * @param lhsEvalExpr
   * @param stackMatcher
   * @return
   */
  private boolean matchVerbatim(IAST lhsPatternAST, final IExpr lhsEvalExpr,
      StackMatcher stackMatcher) {
    if (lhsPatternAST.arg1().equals(lhsEvalExpr)) {
      return stackMatcher.matchRest();
    }
    return false;
  }

  /**
   * Match an IAST with head {@link S#KeyValuePattern}.
   *
   * <p>
   * Every pattern of <code>arg1</code> must match one of the rules of the association or list of
   * rules, and no rule may be used twice. The assignment is searched with backtracking - a rule
   * consumed by an earlier pattern is released again if the remaining patterns cannot be matched
   * with it (for example <code>KeyValuePattern({x_ -> 1, a -> y_})</code> matching
   * <code>&lt;|a -> 1, b -> 1|&gt;</code> must not let <code>x_ -> 1</code> consume
   * <code>a -> 1</code>). The single patterns are matched with
   * {@link #matchSubExpr(IExpr, IExpr, EvalEngine)} so that the callers stack is consumed exactly
   * once, by the {@link StackMatcher#matchRest()} at the end.
   *
   * @param lhsPatternAST an expression <code>KeyValuePattern(arg1)</code>
   * @param lhsEvalExpr an association {@link IAssociation} or list of rules
   * @param stackMatcher
   * @param engine
   * @return
   */
  private boolean matchKeyValuePattern(IAST lhsPatternAST, final IExpr lhsEvalExpr,
      StackMatcher stackMatcher, EvalEngine engine) {

    IAST listOfPatterns = lhsPatternAST.arg1().makeList();
    IAST lhsEvalList = F.NIL;
    if (lhsEvalExpr.isAssociation()) {
      lhsEvalList = ((IAssociation) lhsEvalExpr).normal(false);
    } else if (lhsEvalExpr.isListOfRules()) {
      lhsEvalList = (IAST) lhsEvalExpr;
    }

    if (lhsEvalList.isList()) {
      if (listOfPatterns.isEmpty()) {
        // empty list matches any association or list of rules
        return stackMatcher.matchRest();
      }

      boolean matched = false;
      final IExpr[] patternValues = fPatternMap.copyPattern();
      try {
        final boolean[] usedIndices = new boolean[lhsEvalList.size()];
        matched = matchKeyValueRecursive(listOfPatterns, 1, lhsEvalList, usedIndices, stackMatcher,
            engine);
        return matched;
      } finally {
        if (!matched) {
          fPatternMap.resetPattern(patternValues);
        }
      }
    }

    return false;
  }

  /**
   * Recursively assign the pattern at <code>patternPosition</code> of <code>listOfPatterns</code>
   * to a not yet used rule of <code>lhsEvalList</code> and continue with the next pattern. If no
   * assignment of the remaining patterns is possible, the rule is released again (backtracking).
   *
   * @return <code>true</code> if all remaining patterns could be assigned and the rest of the
   *         <code>stackMatcher</code> matched
   * @see #matchKeyValuePattern(IAST, IExpr, StackMatcher, EvalEngine)
   */
  private boolean matchKeyValueRecursive(IAST listOfPatterns, int patternPosition, IAST lhsEvalList,
      boolean[] usedIndices, StackMatcher stackMatcher, EvalEngine engine) {
    if (patternPosition >= listOfPatterns.size()) {
      return stackMatcher.matchRest();
    }
    final IExpr patternArg = listOfPatterns.get(patternPosition);
    for (int i = 1; i < lhsEvalList.size(); i++) {
      if (usedIndices[i]) {
        continue;
      }
      boolean matched = false;
      final IExpr[] patternValues = fPatternMap.copyPattern();
      try {
        if (matchSubExpr(patternArg, lhsEvalList.getRule(i), engine)) {
          usedIndices[i] = true;
          matched = matchKeyValueRecursive(listOfPatterns, patternPosition + 1, lhsEvalList,
              usedIndices, stackMatcher, engine);
          if (matched) {
            return true;
          }
          usedIndices[i] = false;
        }
      } finally {
        if (!matched) {
          fPatternMap.resetPattern(patternValues);
        }
      }
    }
    return false;
  }

  /**
   * Match an IAST with head {@link S#Association}.
   * 
   * @param lhsPatternAST
   * @param lhsEvalExpr
   * @param stackMatcher
   * @param engine
   * @return
   */
  private boolean matchAssociation(IAST lhsPatternAST, final IExpr lhsEvalExpr,
      StackMatcher stackMatcher, EvalEngine engine) {
    boolean matched = false;
    final IExpr[] patternValues;
    patternValues = fPatternMap.copyPattern();
    try {
      if (lhsEvalExpr.isAssociation()) {
        IAST lhsPatternList = lhsPatternAST;
        if (lhsPatternAST.isAssociation()) {
          lhsPatternList = ((IAssociation) lhsPatternAST).normal(false);
          lhsPatternList.setEvalFlags(lhsPatternAST.getEvalFlags());
          ((IASTMutable) lhsPatternList).set(0, S.Association);
        }
        IAssociation lhsEvalAssociation = (IAssociation) lhsEvalExpr;
        IASTMutable lhsEvalList = lhsEvalAssociation.normal(false);
        lhsEvalList.set(0, S.Association);
        matched = matchASTExpr(lhsPatternList, lhsEvalList, engine, stackMatcher);
        return matched;
      }
      matched = matchASTExpr(lhsPatternAST, lhsEvalExpr, engine, stackMatcher);
    } finally {
      if (!matched) {
        fPatternMap.resetPattern(patternValues);
      }
    }
    return matched;
  }

  /**
   * Match an IAST with head {@link S#Complex} with 2 arguments.
   * 
   * @param lhsPatternAST
   * @param lhsEvalExpr
   * @param stackMatcher
   * @param engine
   * @return
   */
  private boolean matchComplex(IAST lhsPatternAST, final IExpr lhsEvalExpr,
      StackMatcher stackMatcher, EvalEngine engine) {
    boolean matched = false;
    final IExpr[] patternValues;
    patternValues = fPatternMap.copyPattern();
    try {
      if (lhsEvalExpr.isNumber()) {
        INumber number = (INumber) lhsEvalExpr;
        matched = matchExpr(lhsPatternAST.arg1(), number.re(), engine, stackMatcher) //
            && matchExpr(lhsPatternAST.arg2(), number.im(), engine, stackMatcher);
        return matched;
      }
      matched = matchASTExpr(lhsPatternAST, lhsEvalExpr, engine, stackMatcher);
    } finally {
      if (!matched) {
        fPatternMap.resetPattern(patternValues);
      }
    }
    return matched;
  }

  /**
   * Test if <code>lhsPatternExpr</code> matches <code>lhsEvalExpr</code> <b>without</b> changing
   * the state of this pattern matcher.
   *
   * <p>
   * The match runs on its own {@link StackMatcher}, so no entry of the callers stack is consumed,
   * the pattern values bound during the attempt are always restored and
   * {@link #checkRHSCondition(EvalEngine)} is not evaluated. This is what the excluded pattern of
   * {@link S#Except} needs: it only asks <i>whether</i> the expression matches and must have no
   * observable effect, whatever the answer is.
   *
   * @param lhsPatternExpr the left-hand-side pattern expression
   * @param lhsEvalExpr the expression which should match <code>lhsPatternExpr</code>
   * @param engine the evaluation engine
   * @return <code>true</code> if the two expressions match each other
   */
  private boolean matchProbe(IExpr lhsPatternExpr, final IExpr lhsEvalExpr, EvalEngine engine) {
    final IExpr[] patternValues = fPatternMap.copyPattern();
    try {
      return matchSubExpr(lhsPatternExpr, lhsEvalExpr, engine);
    } finally {
      fPatternMap.resetPattern(patternValues);
    }
  }

  /**
   * Match <code>lhsPatternExpr</code> against <code>lhsEvalExpr</code> on a private
   * {@link StackMatcher}.
   *
   * <p>
   * No entry of the callers stack is consumed and {@link #checkRHSCondition(EvalEngine)} is not
   * evaluated, but the pattern values bound by a successful match are <b>kept</b>. Use this for a
   * sub-expression which has to be matched on its own before the enclosing pattern can decide
   * whether it matches; the caller is responsible for restoring the pattern values if it later
   * fails. Use {@link #matchProbe(IExpr, IExpr, EvalEngine)} instead if the bindings must be
   * discarded in any case.
   *
   * @param lhsPatternExpr the left-hand-side pattern expression
   * @param lhsEvalExpr the expression which should match <code>lhsPatternExpr</code>
   * @param engine the evaluation engine
   * @return <code>true</code> if the two expressions match each other
   */
  private boolean matchSubExpr(IExpr lhsPatternExpr, final IExpr lhsEvalExpr, EvalEngine engine) {
    final StackMatcher stackMatcher = new StackMatcher(engine, false);
    if (lhsPatternExpr.isASTOrAssociation()) {
      return matchASTSpecialBuiltIn((IAST) lhsPatternExpr, lhsEvalExpr, engine, stackMatcher);
    }
    if (lhsPatternExpr instanceof IPatternObject) {
      return matchPattern((IPatternObject) lhsPatternExpr, lhsEvalExpr, stackMatcher, engine)
          && stackMatcher.matchRest();
    }
    return lhsPatternExpr.equals(lhsEvalExpr);
  }

  /**
   * Match an IAST with head {@link S#Except} with 1 argument.
   *
   * <p>
   * <code>Except(pattern)</code> matches every expression which is <b>not</b> matched by
   * <code>pattern</code>. It binds nothing, so the excluded pattern is tested in isolation with
   * {@link #matchProbe(IExpr, IExpr, EvalEngine)}. If the exclusion holds, matching continues with
   * the remaining entries of the <code>stackMatcher</code>.
   *
   * @param lhsPatternAST
   * @param lhsEvalExpr
   * @param stackMatcher
   * @param engine
   * @return
   */
  private boolean matchExcept1(IAST lhsPatternAST, final IExpr lhsEvalExpr,
      StackMatcher stackMatcher, EvalEngine engine) {
    if (matchProbe(lhsPatternAST.arg1(), lhsEvalExpr, engine)) {
      return false;
    }
    return stackMatcher.matchRest();
  }

  /**
   * Match an IAST with head {@link S#Except} with 2 arguments.
   *
   * <p>
   * <code>Except(pattern, allowed)</code> matches every expression which is <b>not</b> matched by
   * <code>pattern</code> but is matched by <code>allowed</code>. Only <code>allowed</code> may bind
   * pattern values, therefore <code>pattern</code> is tested in isolation with
   * {@link #matchProbe(IExpr, IExpr, EvalEngine)} and <code>allowed</code> is matched against the
   * callers <code>stackMatcher</code> as usual.
   *
   * @param lhsPatternAST
   * @param lhsEvalExpr
   * @param stackMatcher
   * @param engine
   * @return
   */
  private boolean matchExcept2(IAST lhsPatternAST, final IExpr lhsEvalExpr,
      StackMatcher stackMatcher, EvalEngine engine) {
    if (matchProbe(lhsPatternAST.arg1(), lhsEvalExpr, engine)) {
      return false;
    }
    boolean matched = false;
    final IExpr[] patternValues = fPatternMap.copyPattern();
    try {
      matched = matchExpr(lhsPatternAST.arg2(), lhsEvalExpr, engine, stackMatcher);
      return matched;
    } finally {
      if (!matched) {
        fPatternMap.resetPattern(patternValues);
      }
    }
  }

  /**
   * Match an IAST with head {@link S#Rational} with 2 arguments.
   * 
   * @param lhsPatternAST
   * @param lhsEvalExpr
   * @param stackMatcher
   * @param engine
   * @return
   */
  private boolean matchRational(IAST lhsPatternAST, final IExpr lhsEvalExpr,
      StackMatcher stackMatcher, EvalEngine engine) {
    boolean matched = false;
    final IExpr[] patternValues;
    patternValues = fPatternMap.copyPattern();
    try {
      // check for fractions (and no integers) here to be compatible with MMA
      if (lhsEvalExpr.isFraction()) {
        IFraction rational = (IFraction) lhsEvalExpr;
        matched = matchExpr(lhsPatternAST.arg1(), rational.numerator(), engine, stackMatcher) //
            && matchExpr(lhsPatternAST.arg2(), rational.denominator(), engine, stackMatcher);
        return matched;
      }
      matched = matchASTExpr(lhsPatternAST, lhsEvalExpr, engine, stackMatcher);
      return matched;
    } finally {
      if (!matched) {
        fPatternMap.resetPattern(patternValues);
      }
    }
  }

  /**
   * Match an IAST with head {@link S#PatternTest} with 2 arguments.
   * 
   * @param lhsPatternAST
   * @param lhsEvalExpr
   * @param stackMatcher
   * @param engine
   * @return
   */
  private boolean matchPatternTest(IAST lhsPatternAST, final IExpr lhsEvalExpr,
      StackMatcher stackMatcher, EvalEngine engine) {
    boolean matched = false;
    final IExpr[] patternValues;
    patternValues = fPatternMap.copyPattern();
    try {
      final IExpr lhsPatternExpr = lhsPatternAST.arg1();
      final IExpr patternTest = lhsPatternAST.arg2();
      if (lhsPatternExpr instanceof IPatternObject && patternTest.isFreeOfPatterns()) {
        // isPatternTest() can be done immediately, because patternTest contains no
        // other pattern symbol
        if (matchPattern((IPatternObject) lhsPatternExpr, lhsEvalExpr, stackMatcher, engine)) {
          if (fPatternMap.isPatternTest(lhsPatternExpr, patternTest, engine)) {
            matched = stackMatcher.matchRest();
          }
        }
      } else if (matchExpr(lhsPatternExpr, lhsEvalExpr, engine, stackMatcher)) {
        matched = fPatternMap.isPatternTest(lhsPatternExpr, patternTest, engine);
      }

    } finally {
      if (!matched) {
        fPatternMap.resetPattern(patternValues);
      }
    }
    return matched;
  }

  /**
   * Match a left-hand-side <code>Alternatives(p1, p2, ..., p_i)</code>.
   *
   * <pre>
   * Alternatives(p1, p2, ..., p_i)
   * </pre>
   *
   * or
   *
   * <pre>
   * p1 | p2 | ... | p_i
   * </pre>
   *
   * <p>
   * is a pattern that matches any of the patterns <code>p1, p2,...., p_i</code>.
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; a+b+c+d/.(a|b)-&gt;t
   * c + d + 2 t
   * </pre>
   *
   * <p>
   * Each alternative is matched against the callers <code>stackMatcher</code>, so a named pattern
   * inside the matching alternative stays bound and the remaining entries of the stack are matched
   * exactly once. If the rest of the stack cannot be matched, the next alternative is tried.
   *
   * @param lhsPatternAlternatives a <code>Alternatives(...)</code> expression
   * @param lhsEvalExpr the value which should be matched
   * @param engine
   * @param stackMatcher
   * @return
   */
  private boolean matchAlternatives(IAST lhsPatternAlternatives, final IExpr lhsEvalExpr,
      EvalEngine engine, StackMatcher stackMatcher) {
    final IExpr[] patternValues = fPatternMap.copyPattern();
    final int lastStackSize = stackMatcher.size();
    for (int i = 1; i < lhsPatternAlternatives.size(); i++) {
      boolean matched = false;
      try {
        matched = matchExpr(lhsPatternAlternatives.get(i), lhsEvalExpr, engine, stackMatcher);
        if (matched) {
          return true;
        }
      } finally {
        if (!matched) {
          stackMatcher.removeFrom(lastStackSize);
          fPatternMap.resetPattern(patternValues);
        }
      }
    }
    return false;
  }

  /**
   * Match a left-hand-side <code>Condition(pattern, expr)</code>.
   *
   * <pre>
   * <code>Condition(pattern, expr)
   * </code>
   * </pre>
   *
   * <p>
   * or
   *
   * <pre>
   * <code>pattern /; expr
   * </code>
   * </pre>
   *
   * <p>
   * places an additional constraint on <code>pattern</code> that only allows it to match if
   * <code>expr</code> evaluates to <code>True</code>.
   *
   * <h3>Examples</h3>
   *
   * <p>
   * The controlling expression of a <code>Condition</code> can use variables from the pattern:
   *
   * <pre>
   * <code>&gt;&gt; f(3) /. f(x_) /; x&gt;0 -&gt; t
   * t
   *
   * &gt;&gt; f(-3) /. f(x_) /; x&gt;0 -&gt; t
   * f(-3)
   * </code>
   * </pre>
   *
   * <p>
   * <code>Condition</code> can be used in an assignment:
   *
   * <pre>
   * <code>&gt;&gt; f(x_) := p(x) /; x&gt;0
   * &gt;&gt; f(3)
   * p(3)
   *
   * &gt;&gt; f(-3)
   * f(-3)
   * </code>
   * </pre>
   *
   * @param lhsPatternCondition a <code>Condition(pattern-expr, test)</code> expression
   * @param lhsEvalExpr the value which should be matched
   * @param engine
   * @param stackMatcher
   * @return
   */
  private boolean matchCondition(final IAST lhsPatternCondition, final IExpr lhsEvalExpr,
      EvalEngine engine, StackMatcher stackMatcher) {
    boolean matched = false;
    final IExpr[] patternValues = fPatternMap.copyPattern();
    final int lastStackSize = stackMatcher.size();
    try {
      stackMatcher.push(new Entry(lhsPatternCondition.second()));
      return matched = matchExpr(lhsPatternCondition.first(), lhsEvalExpr, engine, stackMatcher);
    } finally {
      if (!matched) {
        // also remove the pushed condition entry, otherwise it leaks into the next attempt of
        // the caller
        stackMatcher.removeFrom(lastStackSize);
        fPatternMap.resetPattern(patternValues);
      }
    }
  }

  /**
   * Match a left-hand-side <code>Optional(patt, default)</code>.
   *
   * <pre>
   * <code>Optional(patt, default)
   * </code>
   * </pre>
   *
   * <p>
   * or
   *
   * <pre>
   * <code>patt : default
   * </code>
   * </pre>
   *
   * <p>
   * is a pattern which matches <code>patt</code>, which if omitted should be replaced by <code>
   * default</code>.
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; f(x_, y_:1) := {x, y}
   *
   * &gt;&gt; f(1, 2)
   * {1,2}
   *
   * &gt;&gt; f(a)
   * {a,1}
   * </code>
   * </pre>
   *
   * @param lhsPatternOptional a <code>Optional(patt, default</code> expression
   * @param lhsEvalExpr the value which should be matched
   * @param stackMatcher
   * @param engine
   * @return
   */
  private boolean matchOptional(final IAST lhsPatternOptional, final IExpr lhsEvalExpr,
      StackMatcher stackMatcher, EvalEngine engine) {
    return matchHoldPattern(lhsPatternOptional, lhsEvalExpr, stackMatcher, engine);
  }

  private boolean matchBlankSequence(final IPatternSequence patternSequence,
      final IAST lhsPatternAST, final int position, final IAST lhsEvalAST, EvalEngine engine,
      StackMatcher stackMatcher) {

    boolean isNullSequence = patternSequence.isNullSequence();
    if (position == lhsPatternAST.argSize()) {
      boolean matched = false;
      final IExpr[] patternValues = fPatternMap.copyPattern();
      try {
        IASTAppendable seq = F.Sequence();
        seq.appendAll(lhsEvalAST, 1, lhsEvalAST.size());
        if (patternSequence.matchPatternSequence(seq, fPatternMap, lhsPatternAST.topHead())) {
          matched = stackMatcher.matchRest();
          if (matched) {
            return true;
          }
        }
        return false;
      } finally {
        if (!matched) {
          fPatternMap.resetPattern(patternValues);
        }
      }
    }
    int lhsEvalIndex = 2;
    IAST reducedLHSPatternAST = lhsPatternAST.removeFromStart(position + 1);
    IPatternMap.setPatternFlags(reducedLHSPatternAST);
    boolean matched = false;
    final IExpr[] patternValues = fPatternMap.copyPattern();
    final int lhsEvalSize = lhsEvalAST.size();
    int startPosition = 1;
    if (isNullSequence) {
      startPosition = 1;
      lhsEvalIndex = 1;
    }
    while (lhsEvalIndex <= lhsEvalSize) {
      try {
        IASTAppendable seq = F.ast(S.Sequence, lhsEvalIndex - startPosition);
        seq.appendAll(lhsEvalAST, startPosition, lhsEvalIndex);

        if (patternSequence.matchPatternSequence(seq, fPatternMap, lhsPatternAST.topHead())) {
          matched = matchAST(reducedLHSPatternAST, lhsEvalAST.copyFrom(lhsEvalIndex), engine,
              stackMatcher);
          if (matched) {
            return true;
          }
        }
      } finally {
        if (!matched) {
          fPatternMap.resetPattern(patternValues);
        }
      }
      lhsEvalIndex++;
    }
    return false;
  }

  /**
   * Match the <code>lhsPatternAST</code> with its <code>Default[]</code> values.
   *
   * @param symbolWithDefaultValue the symbol for getting the associated default values from
   * @param lhsPatternAST left-hand-side which may contain patterns with default values
   * @param engine the evaluation engine
   * @return <code>F.NIL</code> if the given <code>lhsPatternAST</code> could not be matched or
   *         contains no pattern with default value.
   */
  private IExpr matchDefaultArgumentsAST(ISymbol symbolWithDefaultValue, IAST lhsPatternAST,
      EvalEngine engine) {
    IASTAppendable cloned = F.ast(lhsPatternAST.head(), lhsPatternAST.size());
    boolean[] defaultValueMatched = new boolean[] {false};
    if (lhsPatternAST.exists((temp, i) -> isOptionalOrDefaultPattern(temp, i,
        symbolWithDefaultValue, cloned, defaultValueMatched, engine))) {
      return F.NIL;
    }
    if (defaultValueMatched[0]) {
      if (cloned.isOneIdentityAST1()) {
        return cloned.arg1();
      }
      IPatternMap.setPatternFlags(cloned);
      return cloned;
    }
    return F.NIL;
  }

  private boolean isOptionalOrDefaultPattern(IExpr temp, int i, ISymbol symbolWithDefaultValue,
      IASTAppendable cloned, boolean[] defaultValueMatched, EvalEngine engine) {
    if (temp.isPatternDefault()) {
      if (temp.isOptional()) {
        IAST optional = (IAST) temp;
        IExpr optionalValue =
            (optional.isAST2()) ? optional.arg2() : symbolWithDefaultValue.getDefaultValue();
        if (optionalValue.isPresent()) {
          if (!(matchSubExpr(temp.first(), optionalValue, engine))) {
            return true;
          }
          defaultValueMatched[0] = true;
        }
        return false;
      }
      IExpr positionDefaultValue = symbolWithDefaultValue.getDefaultValue(i);
      if (positionDefaultValue.isPresent()) {
        if (!((IPatternObject) temp).matchPattern(positionDefaultValue, fPatternMap)) {
          return true;
        }
        defaultValueMatched[0] = true;
        return false;
      }
      IExpr commonDefaultValue = symbolWithDefaultValue.getDefaultValue();
      if (commonDefaultValue.isPresent()) {
        if (!((IPatternObject) temp).matchPattern(commonDefaultValue, fPatternMap)) {
          return true;
        }
        defaultValueMatched[0] = true;
        return false;
      }
    }
    cloned.append(temp);
    return false;
  }

  /**
   * Checks if the two expressions match each other.
   * 
   * 
   * @param lhsPatternExpr the left-hand-side pattern expression
   * @param lhsEvalExpr the left-hand-side expression which should match <code>lhsPatternExpr</code>
   * @param engine the evaluation engine
   * @return <code>true</code> if the two expressions match each other
   */
  protected boolean matchExpr(IExpr lhsPatternExpr, final IExpr lhsEvalExpr, EvalEngine engine) {
    if (lhsPatternExpr instanceof IPatternObject) {
      if (!(lhsPatternExpr instanceof PatternNested)) {
        // fast path without allocating a StackMatcher: a plain pattern object binds directly and
        // matchRest() of a fresh empty stack is just checkRHSCondition()
        return ((IPatternObject) lhsPatternExpr).matchPattern(lhsEvalExpr, fPatternMap)
            && checkRHSCondition(engine);
      }
      StackMatcher stackMatcher = new StackMatcher(engine);
      boolean matched =
          matchPattern((IPatternObject) lhsPatternExpr, lhsEvalExpr, stackMatcher, engine);
      return matched ? stackMatcher.matchRest() : false;
    } else if (lhsPatternExpr.isASTOrAssociation()) {
      StackMatcher stackMatcher = new StackMatcher(engine);
      return matchASTSpecialBuiltIn((IAST) lhsPatternExpr, lhsEvalExpr, engine, stackMatcher);
    }
    return lhsPatternExpr.equals(lhsEvalExpr);
  }

  /**
   * Checks if the two expressions match each other. If <code>true</code> match the rest of the
   * <code>stackMatcher</code>.
   *
   * @param lhsPatternExpr the left-hand-side pattern expression
   * @param lhsEvalExpr the left-hand-side expression which should match <code>lhsPatternExpr</code>
   * @param engine the evaluation engine
   * @param stackMatcher a stack of entries of expressions, which have to match each other.
   * @return <code>true</code> if the two expressions match each other
   */
  protected boolean matchExpr(IExpr lhsPatternExpr, final IExpr lhsEvalExpr, EvalEngine engine,
      StackMatcher stackMatcher) {
    boolean matched = false;
    if (lhsPatternExpr.isASTOrAssociation()) {
      return matchASTSpecialBuiltIn((IAST) lhsPatternExpr, lhsEvalExpr, engine, stackMatcher);
    } else if (lhsPatternExpr instanceof IPatternObject) {
      matched = matchPattern((IPatternObject) lhsPatternExpr, lhsEvalExpr, stackMatcher, engine);
    } else {
      matched = lhsPatternExpr.equals(lhsEvalExpr);
    }
    return matched ? stackMatcher.matchRest() : false;
  }

  private boolean matchTrue(IExpr lhsPatternExpr, EvalEngine engine, StackMatcher stackMatcher) {
    IExpr lhsTest = fPatternMap.substituteSymbols(lhsPatternExpr, F.NIL);
    return engine.evalTrue(lhsTest) ? stackMatcher.matchRest() : false;
  }

  /**
   * Match <code>Flat</code> or <code>Orderless</code> LHS pattern expressions. It's assumed that
   * the headers of the expressions already matched.
   *
   * @param sym
   * @param lhsPattern
   * @param lhsEval
   * @param engine
   * @param stackMatcher
   * @return
   */
  private boolean matchFlatAndFlatOrderless(final ISymbol sym, IAST lhsPattern, IAST lhsEval,
      EvalEngine engine, StackMatcher stackMatcher) {
    if (sym.hasOrderlessAttribute()) {
      return matchFlatOrderless(sym, lhsPattern, lhsEval, engine, stackMatcher);
    } else {
      return matchFlat(sym, lhsPattern, lhsEval, engine, stackMatcher);
    }
  }

  /**
   * Match <code>Flat</code> LHS pattern expressions. It's assumed that the headers of the
   * expressions already matched.
   *
   * @param sym
   * @param lhsPattern
   * @param lhsEval
   * @param engine
   * @param stackMatcher
   * @return
   */
  private boolean matchFlat(final ISymbol sym, final IAST lhsPattern, final IAST lhsEval,
      EvalEngine engine, StackMatcher stackMatcher) {
    if (lhsPattern.isAST1()) {
      if (lhsPattern.arg1().isPatternSequence(false)) {
        // TODO only the special case, where the last element is
        // a pattern sequence, is handled here
        boolean matched = false;
        final IExpr[] patternValues = fPatternMap.copyPattern();
        try {
          IASTAppendable seq = F.Sequence();
          seq.appendAll(lhsEval, 1, lhsEval.size());
          if (((IPatternSequence) lhsPattern.arg1()).matchPatternSequence(seq, fPatternMap,
              lhsPattern.topHead())) {
            // the remaining entries of the stack (a Condition() of the left-hand-side) and the
            // condition of the right-hand-side still have to hold
            matched = stackMatcher.matchRest();
            if (matched) {
              return true;
            }
          }
        } finally {
          if (!matched) {
            fPatternMap.resetPattern(patternValues);
          }
        }
      }
      if (lhsPattern.size() == lhsEval.size()) {
        return matchASTSequence(lhsPattern, lhsEval, 0, engine, stackMatcher);
      }
      return false;
    }

    IAST lhsPatternAST = lhsPattern;
    IAST lhsEvalAST = lhsEval;

    // removeFlat already called a level up
    FlatStepVisitor visitor =
        new FlatStepVisitor(sym, lhsPatternAST, lhsEvalAST, stackMatcher, fPatternMap);
    NumberPartitionsIterator iter =
        new NumberPartitionsIterator(visitor, lhsEvalAST.argSize(), lhsPatternAST.argSize());
    return !iter.execute();
  }

  /**
   * Match <code>Flat</code> and <code>Orderless</code> LHS pattern expressions. It's assumed that
   * the headers of the expressions already matched.
   *
   * @param sym
   * @param lhsPattern
   * @param lhsEval
   * @param engine
   * @param stackMatcher
   * @return
   */
  private boolean matchFlatOrderless(final ISymbol sym, IAST lhsPattern, IAST lhsEval,
      EvalEngine engine, StackMatcher stackMatcher) {
    if (lhsPattern.isAST1()) {
      return matchExpr(lhsPattern.arg1(), lhsEval, engine, stackMatcher);
    }

    IAST lhsPatternAST = lhsPattern;
    IAST lhsEvalAST = lhsEval;

    // removeOrderless already called a level up
    boolean matched = false;
    IExpr[] patternValues = fPatternMap.copyPattern();

    if (lhsPatternAST.size() <= 2) {
      try {
        if (lhsPatternAST.isAST1()) {
          matched = matchExpr(lhsPatternAST.arg1(), lhsEvalAST, engine, stackMatcher);
          return matched;
        }
        if (lhsPatternAST.isEmpty() && lhsEvalAST.size() > 1) {
          matched = false;
          return matched;
        }
        matched = stackMatcher.matchRest();
        return matched;
      } finally {
        if (!matched) {
          fPatternMap.resetPattern(patternValues);
        }
      }
    }
    lhsPattern = lhsPatternAST;
    lhsEval = lhsEvalAST;

    final IAST lhsPatternFinal = lhsPattern;
    final IAST lhsEvalFinal = lhsEval;
    for (int i = 1; i < lhsPatternFinal.size(); i++) {
      IExpr patternArg = lhsPatternFinal.get(i);
      if (!(patternArg instanceof IPatternObject)) {
        final int index = i;
        IAST reduced = lhsPatternFinal.splice(index);
        boolean evaled = false;
        for (int k = 1; k < lhsEvalFinal.size(); k++) {
          final int lastStackSize = stackMatcher.size();
          try {
            IExpr evalArg = lhsEvalFinal.get(k);
            if (!(patternArg.head() instanceof IPatternObject)) {
              if (patternArg.isASTOrAssociation()) {
                if ((((IAST) patternArg).getEvalFlags()
                    & IAST.CONTAINS_DEFAULT_PATTERN) == IAST.CONTAINS_DEFAULT_PATTERN) {
                  continue;
                }
              }

              if (patternArg.head().equals(evalArg.head())
                  && patternArg.isFree(x -> x.isOrderlessAST(), true)) {
                evaled = true;
                matched = matchExpr(patternArg, evalArg, engine, stackMatcher);
              }

              if (matched) {
                matched = matchFlatAndFlatOrderless(sym, reduced, lhsEvalFinal.removeAtCopy(k),
                    engine, stackMatcher);
                if (matched) {
                  return true;
                }
              }
            }
          } finally {
            if (!matched) {
              // remove the entries of this failed attempt; a sub-matcher may have left entries on
              // the stack (for example the Condition entry pushed in matchCondition()), which would
              // pollute the next attempt and the step visitor fallback below
              stackMatcher.removeFrom(lastStackSize);
              fPatternMap.resetPattern(patternValues);
            }
          }
        }
        if (evaled && !sym.equals(patternArg.head())) {
          // No single evaluation argument with the same head matched patternArg (including the
          // recursive match of the remaining arguments). The MultisetPartitionsIterator fallback
          // below could only offer patternArg a single argument again - or a multi-element segment
          // wrapped in `sym`, which cannot match the different head of patternArg - so it would
          // retry exactly the assignments which already failed.
          // The `sym` head check is defensive: a multi-element segment could only match a
          // patternArg with head `sym`. Currently such a patternArg never sets `evaled`, because
          // `sym` is Orderless here and the isFree(... isOrderlessAST ...) guard above already
          // excludes it.
          return false;
        }
      }
    }
    FlatOrderlessStepVisitor visitor = new FlatOrderlessStepVisitor(sym, lhsPatternFinal,
        lhsEvalFinal, stackMatcher, fPatternMap, sym.hasFlatAttribute());
    MultisetPartitionsIterator iter =
        new MultisetPartitionsIterator(visitor, lhsPatternFinal.argSize());
    return !iter.execute();
  }

  /**
   * Match the <code>lhsPatternAST</code> with its {@link S#Optional} and {@link S#Default} values,
   * where the pattern may have more arguments than <code>lhsEvalAST</code>.
   *
   * @param symbolWithDefaultValue the symbol for getting the associated default values from
   * @param lhsPatternAST left-hand-side which may contain patterns with default values
   * @param lhsEvalAST the expression which should be matched
   * @param engine the evaluation engine
   * @return {@link F#NIL} if the given <code>lhsPatternAST</code> could not be matched or contains
   *         no pattern with default value.
   */
  private IExpr matchOptionalArgumentsAST(ISymbol symbolWithDefaultValue, IAST lhsPatternAST,
      IAST lhsEvalAST, EvalEngine engine) {
    final boolean greaterSize = lhsPatternAST.size() > lhsEvalAST.size();
    final int lhsEvalSize = lhsEvalAST.size();
    IASTAppendable cloned = F.ast(lhsPatternAST.head(), lhsPatternAST.size());
    boolean defaultValueMatched = false;
    for (int i = 1; i < lhsPatternAST.size(); i++) {
      IExpr patternArg = lhsPatternAST.get(i);
      if (patternArg.isPatternDefault()) {
        if (patternArg.isOptional()) {
          IAST optional = (IAST) patternArg;
          if (i < lhsEvalSize) {
            cloned.append(optional.arg1());
            continue;
          }
          IExpr optionalValue =
              (optional.isAST2()) ? optional.arg2() : symbolWithDefaultValue.getDefaultValue();
          if (optionalValue.isPresent()) {
            if (!(matchSubExpr(optional.arg1(), optionalValue, engine))) {
              return F.NIL;
            }
            defaultValueMatched = true;
            continue;
          }
        } else {
          IPattern pattern = (IPattern) patternArg;
          if (greaterSize && i < lhsEvalSize) {
            cloned.append(pattern);
            continue;
          }
          IExpr positionDefaultValue = symbolWithDefaultValue.getDefaultValue(i);
          if (positionDefaultValue.isPresent()) {
            if (!((IPatternObject) patternArg).matchPattern(positionDefaultValue, fPatternMap)) {
              return F.NIL;
            }
            defaultValueMatched = true;
            continue;
          } else {
            if (i < lhsEvalSize) {
              cloned.append(pattern);
              continue;
            }
            IExpr commonDefaultValue = symbolWithDefaultValue.getDefaultValue();
            if (commonDefaultValue.isPresent()) {
              if (!((IPatternObject) patternArg).matchPattern(commonDefaultValue, fPatternMap)) {
                return F.NIL;
              }
              defaultValueMatched = true;
              continue;
            }
          }
        }
      }
      cloned.append(patternArg);
    }
    if (defaultValueMatched) {
      if (cloned.isOneIdentityAST1()) {
        return cloned.arg1();
      }
      IPatternMap.setPatternFlags(cloned);
      return cloned;
    }
    return F.NIL;
  }

  private boolean matchPattern(IPatternObject lhsPatternExpr, final IExpr lhsEvalExpr,
      StackMatcher stackMatcher, EvalEngine engine) {
    if (lhsPatternExpr instanceof PatternNested) {
      PatternNested pattern2 = (PatternNested) lhsPatternExpr;
      IExpr patternExpr = pattern2.getPatternExpr();
      if (matchExpr(patternExpr, lhsEvalExpr, engine, stackMatcher)) {
        return pattern2.matchPattern(lhsEvalExpr, fPatternMap);
      }
      return false;
    }
    return lhsPatternExpr.matchPattern(lhsEvalExpr, fPatternMap);
  }

  public boolean matchASTSubset(IAST lhsPatternAST, IAST lhsEvalAST, int[] allReplacePositions,
      int[] allReplaceIndex, int[] allRemovePositions, int[] allRemoveIndex, EvalEngine engine) {
    if (!lhsPatternAST.isList()) {
      return false;
    }
    createPatternMap().initPattern();
    setLHSExprToMatch(lhsEvalAST);

    int lhsPatternSize = lhsPatternAST.size();
    int lhsEvalSize = lhsEvalAST.size();
    if (lhsPatternSize > lhsEvalSize || lhsPatternSize < 2
        || allReplaceIndex[0] >= allReplacePositions.length
        || allRemoveIndex[0] + lhsPatternSize - 2 > allRemovePositions.length) {
      // no room left for another match
      return false;
    }

    boolean matched = false;
    IExpr[] patternValuesStart = fPatternMap.copyPattern();
    try {

      int replacePosition = -1;
      int[] removePositions = new int[lhsPatternSize - 2];
      StackMatcher stackMatcher = new StackMatcher(engine);
      for (int i = 1; i < lhsPatternSize; i++) {
        IExpr patternArg = lhsPatternAST.getRule(i);

        for (int j = 1; j < lhsEvalSize; j++) {
          if (replacePosition == j //
              || isUsedIndex(removePositions, j) //
              || isUsedIndex(allReplacePositions, j) //
              || isUsedIndex(allRemovePositions, j)) {
            continue;
          }
          IExpr evalArg = lhsEvalAST.getRule(j);
          IExpr[] patternValues = fPatternMap.copyPattern();
          try {
            matched = matchExpr(patternArg, evalArg, engine, stackMatcher);
            if (matched) {
              if (replacePosition < 0) {
                replacePosition = j;
              } else {
                removePositions[i - 2] = j;
              }
              break;
            }
          } finally {
            if (!matched) {
              fPatternMap.resetPattern(patternValues);
            }
          }
        }
        if (!matched) {
          return false;
        }
      }
      if (matched) {
        matched = stackMatcher.matchRest();
        if (matched) {
          allReplacePositions[allReplaceIndex[0]] = replacePosition;
          for (int i = 0; i < removePositions.length; i++) {
            allRemovePositions[allRemoveIndex[0]++] = removePositions[i];
          }
          return true;
        }
      }
    } finally {
      if (!matched) {
        fPatternMap.resetPattern(patternValuesStart);
      }
    }
    return false;
  }

  private static boolean isUsedIndex(int[] removePositions, int j) {
    for (int k = 0; k < removePositions.length; k++) {
      if (removePositions[k] <= 0) {
        return false;
      }
      if (j == removePositions[k]) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
    // mask the sign extension - the flags were written with writeShort()
    fSetFlags = objectInput.readShort() & 0xFFFF;
    fLhsPatternExpr = (IExpr) objectInput.readObject();
    if (fLhsPatternExpr != null) {
      initPriorityFromLhs();
    }
  }

  /**
   * Rebuild {@link #fPatternMap} and {@link #fLHSPriority} from {@link #fLhsPatternExpr} with the
   * same steps as the {@link #PatternMatcher(int, IExpr, boolean)} constructor. Used to restore the
   * <code>transient</code> fields after deserialization.
   */
  protected void initPriorityFromLhs() {
    int[] priority = new int[] {IPatternMap.DEFAULT_RULE_PRIORITY};
    this.fPatternMap = IPatternMap.determinePatterns(fLhsPatternExpr, priority, null);
    fLHSPriority = priority[0];
    if (fLhsPatternExpr.isEvalFlagOn(IAST.CONTAINS_PATTERN_SEQUENCE)) {
      fLHSPriority = IPatternMap.DEFAULT_RULE_PRIORITY;
    }
    if (fLhsPatternExpr.isCondition()) {
      fLHSPriority -= 100;
    }
  }

  /**
   * Remove parts which are "free of patterns" in <code>lhsPattern</code> and <code>lhsEval</code>.
   *
   * @param lhsPattern the expression which can contain pattern-matching objects
   * @param lhsEval the expression which can contain no patterns
   * @return <code>null</code> if the matching isn't possible.
   */
  private IAST[] remove(final IAST lhsPattern, final IAST lhsEval, EvalEngine engine,
      StackMatcher stackMatcher) {
    int[] removedPositionsArray = new int[lhsPattern.argSize()];
    int removedPosition = 0;
    boolean matchedPattern = false;
    for (int i = 1; i < lhsPattern.size(); i++) {
      IExpr lhs = lhsPattern.getRule(i);
      IExpr rhs = lhsEval.getRule(i);
      if (lhs instanceof IPatternObject) {
        if (lhs instanceof IPatternSequence) {
          if (i == lhsPattern.argSize()) {
            IPatternSequence pattern = (IPatternSequence) lhs;
            if (pattern.getSymbol() != null && !pattern.isPatternDefault()) {
              if (matchPattern((IPatternSequence) lhs, rhs, stackMatcher, engine)) {
                removedPositionsArray[removedPosition++] = i;
                matchedPattern = true;
                continue;
              } else {
                return null;
              }
            }
          }
          return UNEVALED;
        }
        IPatternObject pattern = (IPatternObject) lhs;
        if (pattern.getSymbol() != null && !pattern.isPatternDefault()) {
          if (matchPattern((IPatternObject) lhs, rhs, stackMatcher, engine)) {
            removedPositionsArray[removedPosition++] = i;
            matchedPattern = true;
          } else {
            return null;
          }
        }
      } else if (lhs.isFreeOfPatterns()) {
        if (lhs.equals(rhs)) {
          removedPositionsArray[removedPosition++] = i;
        } else {
          return null;
        }
      }
    }

    if (removedPosition > 0) {
      IAST lhsPatternAST = lhsPattern.removePositionsAtCopy(removedPositionsArray, removedPosition);
      IAST lhsEvalAST = lhsEval.removePositionsAtCopy(removedPositionsArray, removedPosition);
      if (matchedPattern) {
        lhsPatternAST =
            fPatternMap.substituteASTPatternOrSymbols(lhsPatternAST, engine).orElse(lhsPatternAST);
      }
      return new IAST[] {lhsPatternAST, lhsEvalAST};
    }
    return UNEVALED;

  }

  /**
   * Replace subexpressions for <code>Rule</code> or <code>RuleDelayed</code> in Flat or Orderless
   * expressions.
   *
   * <pre>
   * >> f(a, b, c) /. f(a, b) -> d
   * f(d,c)
   * </pre>
   *
   * @param lhsPatternAST
   * @param lhsEvalAST
   * @param rhsExpr
   * @param engine
   * @return <code>F.NIL</code> if no match was found.
   */
  protected IExpr replaceSubExpressionOrderlessFlat(final IAST lhsPatternAST, final IAST lhsEvalAST,
      final IExpr rhsExpr, EvalEngine engine) {

    if (lhsPatternAST.size() < lhsEvalAST.size()) {
      if (lhsPatternAST.isOrderlessAST() && lhsPatternAST.isFlatAST()) {
        if (!matchHeads(lhsPatternAST, lhsEvalAST, engine)) {
          return F.NIL;
        }
        final OrderlessMatcher foMatcher = new OrderlessMatcher(lhsPatternAST, lhsEvalAST);
        boolean matched = foMatcher.matchOrderlessAST(1, new StackMatcher(engine), engine);
        if (matched) {
          IASTAppendable lhsResultAST = foMatcher.removeUsedIndexPositions();
          IExpr result = fPatternMap.substituteSymbols(rhsExpr, F.NIL);
          try {
            result = result.eval(engine);
            lhsResultAST.append(result);
            return lhsResultAST;
          } catch (final ConditionException e) {
            // fall through
          } catch (final ReturnException e) {
            lhsResultAST.append(e.getValue());
            return lhsResultAST;
          }
        }
        return F.NIL;
      }
      if (lhsPatternAST.isFlatAST()) {
        if (!matchHeads(lhsPatternAST, lhsEvalAST, engine)) {
          return F.NIL;
        }
        return matchFlatSequenceFromIndex(lhsPatternAST, lhsEvalAST, rhsExpr, engine);
      }
    }

    return F.NIL;
  }

  /**
   * Match two {@link ISymbol#FLAT} <code>ASTs</code> where the <code>lhsEvalFlatAST</code> sequence
   * length can be greater equal than the <code>lhsPatternFlatAST</code> sequence length.
   *
   * <p>
   * Example:
   *
   * <pre>
   * >> SetAttributes(fl, Flat)
   *
   * >> fl(fl(a, b), c)", //
   * fl(a,b,c)
   *
   * >> fl(x_, x_) := fl(x)
   *
   * >> fl(b, b, b, c, c)
   * fl(b,c)
   *
   * >> fl(a, a, a, b, b, b, c, c)
   * fl(a,b,c)
   * </pre>
   *
   * @param lhsPatternFlatAST
   * @param lhsEvalFlatAST
   * @param rhsExpr
   * @param engine
   * @return
   */
  private IExpr matchFlatSequenceFromIndex(final IAST lhsPatternFlatAST, final IAST lhsEvalFlatAST,
      final IExpr rhsExpr, EvalEngine engine) {
    final int len = lhsEvalFlatAST.size() - lhsPatternFlatAST.size() + 1;
    for (int i = 0; i < len; i++) {
      if (matchASTSequence(lhsPatternFlatAST, lhsEvalFlatAST, i, engine,
          new StackMatcher(engine))) {
        IASTAppendable lhsResultAST = lhsEvalFlatAST.copyAppendable();
        for (int j = 1; j < lhsPatternFlatAST.size(); j++) {
          lhsResultAST.remove(i + 1);
        }
        try {
          IExpr result = fPatternMap.substituteSymbols(rhsExpr, F.CEmptySequence);
          result = result.eval(engine);
          lhsResultAST.append(i + 1, result);
          return lhsResultAST;
        } catch (final ConditionException e) {
          // fall through
        } catch (final ReturnException e) {
          lhsResultAST.append(i + 1, e.getValue());
          return lhsResultAST;
        }
        return F.NIL;
      }
    }
    return F.NIL;
  }

  public void setLHSPriority(final int priority) {
    fLHSPriority = priority;
  }

  @Override
  public void throwExceptionArgIfMatched(boolean throwIfMatched) {
    this.fThrowIfTrue = throwIfMatched;
  }

  @Override
  public boolean test(final IExpr leftHandSide) {
    return test(leftHandSide, EvalEngine.get());
  }

  @Override
  public boolean test(final IExpr leftHandSide, EvalEngine engine) {

    boolean matched = false;
    if (isRuleWithoutPatterns()) {
      // no patterns found match equally:
      matched = fLhsPatternExpr.equals(leftHandSide);
    } else {

      fPatternMap.initPattern();
      matched = matchExpr(fLhsPatternExpr, leftHandSide, engine);
    }
    if (matched && fThrowIfTrue) {
      throw new ResultException(leftHandSide);
    }
    return matched;
  }

  @Override
  public boolean testBlank(final IExpr leftHandSide, EvalEngine engine) {
    if (isRuleWithoutPatterns()) {
      // no patterns found match equally:
      return fLhsPatternExpr.equals(leftHandSide);
    }
    fPatternMap.initPatternBlank();
    return matchExpr(fLhsPatternExpr, leftHandSide, engine);
  }

  @Override
  public void writeExternal(ObjectOutput objectOutput) throws IOException {
    objectOutput.writeShort((short) fSetFlags);
    objectOutput.writeObject(fLhsPatternExpr);
  }
}
