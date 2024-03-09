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
      if (fLHSPatternAST.forAll((x, j) -> fUsedIndex[j - 1] != i)) {
        boolean matched = false;
        final StackMatcher localStackMatcher =
            stackMatcher == null ? new StackMatcher(engine) : stackMatcher;
        int lastStackSize = localStackMatcher.size();
        try {
          if (localStackMatcher.push(subPattern, arg)) {
            fUsedIndex[lhsPosition - 1] = i;
            if (matchOrderlessAST(lhsPosition + 1, localStackMatcher, engine)) {
              matched = true;
              return true;
            }
          }
        } finally {
          if (!matched) {
            fPatternMap.resetPattern(patternValues);
            fUsedIndex[lhsPosition - 1] = -1;
          }
          localStackMatcher.removeFrom(lastStackSize);
        }
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

    public StackMatcher(EvalEngine engine) {
      fEngine = engine;
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
        return checkRHSCondition(fEngine);
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
        return ast1.forAll((arg, i) -> isEquivalent(arg, i, ast2.getRule(i), pm1, pm2), 0);
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

  private static boolean isEquivalent(IExpr arg1, int i, IExpr arg2, final IPatternMap pm1,
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
   * @param lhsPattern the expression which can contain pattern-matching objects
   * @param lhsEval the expression which can contain no patterns
   * @return <code>null</code> if the no parts could be removed
   */
  private static IAST[] removeFlat(final IAST lhsPattern, final IAST lhsEval) {
    IASTAppendable lhsPatternAST = lhsPattern.copyAppendable();
    IASTAppendable lhsEvalAST = lhsEval.copyAppendable();

    // start from the beginning
    int iIndex = 1;
    while (iIndex < lhsPatternAST.size()) {
      IExpr temp = lhsPatternAST.get(iIndex);
      if (!(temp instanceof IPatternObject) && temp.isFreeOfPatterns()) {
        if (iIndex < lhsEvalAST.size()) {
          if (lhsEvalAST.get(iIndex).equals(temp)) {
            lhsPatternAST.remove(iIndex);
            lhsEvalAST.remove(iIndex);
            continue;
          }
        }
        return null;
      }
      break;
    }

    // now start from the end
    iIndex = lhsPatternAST.size() - 1;
    int jIndex = lhsEvalAST.size() - 1;
    while (iIndex > 0) {
      IExpr temp = lhsPatternAST.get(iIndex);
      if (!(temp instanceof IPatternObject) && temp.isFreeOfPatterns()) {
        if (jIndex < lhsEvalAST.size()) {
          if (lhsEvalAST.get(jIndex).equals(temp)) {
            lhsPatternAST.remove(iIndex--);
            lhsEvalAST.remove(jIndex--);
            continue;
          }
        }
        return null;
      }
      break;
    }
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
        // System.out.println(temp + " <-> " + lhsPattern + " <-> " + lhsEval);
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
          // System.out.println(temp + " <-> " + lhsPatternAST + " <-> " + lhsEval);
          return null;
        }
        iIndex++;
      }
      return new IAST[] {lhsPatternAST, lhsEvalAST};
    }
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

  protected int fSetFlags;

  /**
   * Are the given flags disabled ?
   *
   * @param flags
   * @return
   * @see IAST#NO_FLAG
   */
  public final boolean isFlagOff(final int flags) {
    return (fSetFlags & flags) == 0;
  }

  /**
   * Are the given flags enabled ?
   *
   * @param flags
   * @return
   * @see IAST#NO_FLAG
   */
  public final boolean isFlagOn(int flags) {
    return (fSetFlags & flags) == flags;
  }

  /**
   * Add a flag to the existing ones.
   *
   * @param i
   */
  public final void addFlags(final int i) {
    fSetFlags |= i;
  }

  /**
   * Get the flags for this matcher.
   *
   * @return
   */
  public int getFlags() {
    return fSetFlags;
  }


  /**
   * Set the evaluation flags for this list (i.e. replace all existing flags).
   *
   * @param i
   */
  public void setFlags(int i) {
    fSetFlags = i;
  }


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
    v.fLHSPriority = fLHSPriority;
    v.fThrowIfTrue = fThrowIfTrue;
    v.fLhsPatternExpr = fLhsPatternExpr;
    if (fPatternMap != null) {
      v.fPatternMap = fPatternMap.copy();
    }
    v.fLhsExprToMatch = fLhsExprToMatch;
    v.fSetFlags = fSetFlags;
    return v;
  }

  public IPatternMap createPatternMap() {
    if (fPatternMap == null) {
      int[] priority = new int[] {IPatternMap.DEFAULT_RULE_PRIORITY};
      fPatternMap = IPatternMap.determinePatterns(fLhsPatternExpr, priority, null);
    }
    return fPatternMap;
  }

  public IPatternMap determinePatterns(int[] priority) {
    return IPatternMap.determinePatterns(fLhsPatternExpr, priority, null);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    return fSetFlags == ((PatternMatcher) obj).fSetFlags;
  }

  // package private
  int equivalent(final IPatternMatcher obj) {
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
    final PatternMatcher pm = (PatternMatcher) obj;
    if (fPatternMap.size() != pm.fPatternMap.size()) {
      return (fPatternMap.size() < pm.fPatternMap.size()) ? -1 : 1;
    }
    if (isRuleWithoutPatterns()) {
      return fLhsPatternExpr.compareTo(pm.fLhsPatternExpr);
    }
    if (equivalent(fLhsPatternExpr, pm.fLhsPatternExpr, fPatternMap, pm.fPatternMap)) {
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
   * @return
   */
  @Override
  public final boolean isRuleWithoutPatterns() {
    return createPatternMap().isRuleWithoutPatterns();
  }

  public boolean isThrowIfTrue() {
    return fThrowIfTrue;
  }

  protected boolean matchAST(IAST lhsPatternAST, final IExpr lhsEvalExpr, EvalEngine engine,
      StackMatcher stackMatcher) {
    if (lhsEvalExpr instanceof IAST) {
      if (lhsPatternAST.isFreeOfPatterns() && lhsPatternAST.equals(lhsEvalExpr)) {
        return stackMatcher.matchRest();
      }

      IAST lhsEvalAST = (IAST) lhsEvalExpr;
      final ISymbol sym = lhsPatternAST.topHead();

      if (lhsPatternAST.size() <= lhsEvalAST.size()) {
        if (lhsPatternAST.head().equals(lhsEvalAST.head())) {
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
            IExpr temp = fPatternMap.substituteASTPatternOrSymbols(lhsPatternAST, engine)
                .orElse(lhsPatternAST);
            if (temp.isAST(lhsPatternAST.head())) {
              IAST[] removed = removeFlat((IAST) temp, lhsEvalAST);
              if (removed != null) {
                lhsPatternAST = removed[0];
                lhsEvalAST = removed[1];
              }
            }
          }
        }

        if ((lhsPatternAST.isFlatAST()) && sym.equals(lhsEvalAST.topHead())
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

      int lhsEvalSize = lhsEvalAST.size();
      if (lhsPatternAST.isEvalFlagOn(IAST.CONTAINS_PATTERN_SEQUENCE)) {
        if (!matchHeads(lhsPatternAST, lhsEvalAST, engine)) {
          return false;
        }
        if (lhsPatternAST.isEmpty() && lhsEvalAST.isEmpty()) {
          return stackMatcher.matchRest();
        }
        final int lastPosition = lhsPatternAST.argSize();
        if (lastPosition == 1 && lhsPatternAST.get(lastPosition).isAST(S.PatternTest, 3)) {
          if (lhsPatternAST.size() <= lhsEvalSize) {
            IAST patternTest = (IAST) lhsPatternAST.get(lastPosition);
            if (patternTest.arg1().isPatternSequence(false)) {
              // TODO only the special case, where the last element is
              // a pattern sequence, is handled here
              IASTAppendable seq = F.Sequence();
              seq.appendAll(lhsEvalAST, lastPosition, lhsEvalSize);
              if (((IPatternSequence) patternTest.arg1()).matchPatternSequence(seq, fPatternMap,
                  lhsPatternAST.topHead())) {
                if (matchAST(lhsPatternAST.removeFromEnd(lastPosition),
                    lhsEvalAST.removeFromEnd(lastPosition), engine, stackMatcher)) {
                  return fPatternMap.isPatternTest(patternTest.arg1(), patternTest.arg2(), engine);
                }
                return false;
              }
            }
          }
        } else if (lhsPatternAST.size() > 1 && lhsPatternAST.arg1().isPatternSequence(false)) {
          IPatternSequence patternSequence = (IPatternSequence) lhsPatternAST.arg1();
          return matchBlankSequence(patternSequence, lhsPatternAST, 1, lhsEvalAST, engine,
              stackMatcher);
        } else {
          if (lhsPatternAST.size() > 1 && lhsEvalSize > 1) {
            if (matchExpr(lhsPatternAST.arg1(), lhsEvalAST.arg1(), engine)) {
              return matchAST(lhsPatternAST.rest().addEvalFlags(IAST.CONTAINS_PATTERN_SEQUENCE),
                  lhsEvalAST.rest(), engine, stackMatcher);
            }
          }
        }
        return false;
      }

      if (lhsPatternAST.size() != lhsEvalSize || !matchHeads(lhsPatternAST, lhsEvalAST, engine)) {
        return false;
      }

      if (lhsPatternAST.isOrderlessAST() && lhsPatternAST.size() > 2) {
        // only "pure Orderless" and "FlatOrderless with same size()" will be handled here:
        OrderlessStepVisitor visitor = new OrderlessStepVisitor(sym, lhsPatternAST, lhsEvalAST,
            stackMatcher, fPatternMap, (sym.hasOneIdentityAttribute() || sym.hasFlatAttribute())
                // if FLAT isn't set and the Orderless ASTs have
                // same size ==> use OneIdentity in pattern matching
                || (lhsPatternAST.size() == lhsEvalSize && !sym.hasFlatAttribute()));
        MultisetPartitionsIterator iter =
            new MultisetPartitionsIterator(visitor, lhsPatternAST.argSize());
        return !iter.execute();
      }

      return matchASTSequence(lhsPatternAST, lhsEvalAST, 0, engine, stackMatcher);
    }
    return false;
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
    IExpr evaledHead = evaledAST.head();
    if (patternHead.isSymbol()) {
      // this is the 99 % case
      return patternHead.equals(evaledHead);
    }
    return matchExpr(patternHead, evaledHead, engine);
  }

  private boolean matchASTExpr(IAST lhsPatternAST, final IExpr lhsEvalExpr, EvalEngine engine,
      StackMatcher stackMatcher) {
    boolean matched = false;
    IExpr[] patternValues = fPatternMap.copyPattern();
    try {
      matched = matchAST(lhsPatternAST, lhsEvalExpr, engine, stackMatcher);
      if (!matched) {
        fPatternMap.resetPattern(patternValues);
        if ((lhsPatternAST.getEvalFlags()
            & IAST.CONTAINS_DEFAULT_PATTERN) == IAST.CONTAINS_DEFAULT_PATTERN) {
          if (lhsEvalExpr.isASTOrAssociation() && lhsPatternAST.hasOptionalArgument()
              && !lhsPatternAST.isOrderlessAST()) {
            // TODO for Power[x_, y_.] matching Power[a,b] test both cases Power[a,b] &&
            // Power[Power[a,b],1]
            IExpr temp = matchOptionalArgumentsAST(lhsPatternAST.topHead(), lhsPatternAST,
                (IAST) lhsEvalExpr, engine);
            if (temp.isPresent()) {
              matched = matchExpr(temp, lhsEvalExpr, engine, stackMatcher);
            }
          } else {
            IExpr temp = matchDefaultArgumentsAST(lhsPatternAST.topHead(), lhsPatternAST, engine);
            if (temp.isPresent()) {
              matched = matchExpr(temp, lhsEvalExpr, engine, stackMatcher);
            }
          }
        }
      }
    } finally {
      if (!matched) {
        fPatternMap.resetPattern(patternValues);
      }
    }
    return matched;
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
      boolean oneIdentity = head.isSymbol() ? ((ISymbol) head).isOneIdentityAttribute() : false;
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
    if (functionID >= ID.Association && functionID <= ID.Verbatim) {
      if (functionID == ID.Association) {
        return matchAssociation(lhsPatternAST, lhsEvalExpr, stackMatcher, engine);
      }
      if (lhsPatternAST.size() == 2) {
        switch (functionID) {
          case ID.Except:
            return matchExcept1(lhsPatternAST, lhsEvalExpr, stackMatcher, engine);
          case ID.HoldPattern:
          case ID.Literal:
            return matchHoldPattern(lhsPatternAST, lhsEvalExpr, stackMatcher, engine);
          case ID.Optional:
            return matchOptional(lhsPatternAST, lhsEvalExpr, stackMatcher, engine);
          case ID.Verbatim:
            return lhsPatternAST.arg1().equals(lhsEvalExpr);
          default:
        }
      } else if (lhsPatternAST.size() == 3) {
        if (functionID >= ID.Complex && functionID <= ID.Rational) {
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
        }
      }
    } else {
      if (lhsPatternAST.isAlternatives()) {
        return matchAlternatives(lhsPatternAST, lhsEvalExpr, engine);
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
          ((IASTMutable) lhsPatternList).setEvalFlags(lhsPatternAST.getEvalFlags());
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
   * Match an IAST with head {@link S#Except} with 1 argument.
   * 
   * @param lhsPatternAST
   * @param lhsEvalExpr
   * @param stackMatcher
   * @param engine
   * @return
   */
  private boolean matchExcept1(IAST lhsPatternAST, final IExpr lhsEvalExpr,
      StackMatcher stackMatcher, EvalEngine engine) {
    boolean matched = false;
    final IExpr[] patternValues;
    patternValues = fPatternMap.copyPattern();
    try {
      matched = !matchExpr(lhsPatternAST.arg1(), lhsEvalExpr, engine, stackMatcher);
    } finally {
      if (!matched) {
        fPatternMap.resetPattern(patternValues);
      }
    }
    return matched;
  }

  /**
   * Match an IAST with head {@link S#Except} with 2 arguments.
   * 
   * @param lhsPatternAST
   * @param lhsEvalExpr
   * @param stackMatcher
   * @param engine
   * @return
   */
  private boolean matchExcept2(IAST lhsPatternAST, final IExpr lhsEvalExpr,
      StackMatcher stackMatcher, EvalEngine engine) {
    boolean matched = false;
    final IExpr[] patternValues;
    patternValues = fPatternMap.copyPattern();
    try {
      matched = !matchExpr(lhsPatternAST.arg1(), lhsEvalExpr, engine, stackMatcher)
          && matchExpr(lhsPatternAST.arg2(), lhsEvalExpr, engine, stackMatcher);
    } finally {
      if (!matched) {
        fPatternMap.resetPattern(patternValues);
      }
    }
    return matched;
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
   * @param lhsPatternAlternatives a <code>Alternatives(...)</code> expression
   * @param lhsEvalExpr the value which should be matched
   * @param engine
   * @return
   */
  private boolean matchAlternatives(IAST lhsPatternAlternatives, final IExpr lhsEvalExpr,
      EvalEngine engine) {
    boolean matched = false;
    final IExpr[] patternValues = fPatternMap.copyPattern();
    try {
      matched = lhsPatternAlternatives.exists(x -> matchExpr(x, lhsEvalExpr, engine));
      return matched;
    } finally {
      if (!matched) {
        fPatternMap.resetPattern(patternValues);
      }
    }
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
    try {
      stackMatcher.push(new Entry(lhsPatternCondition.second()));
      return matched = matchExpr(lhsPatternCondition.first(), lhsEvalExpr, engine, stackMatcher);
    } finally {
      if (!matched) {
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
    IAST reducedLHSPatternAST =
        lhsPatternAST.removeFromStart(position + 1).addEvalFlags(IAST.CONTAINS_PATTERN_SEQUENCE);
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
    if (lhsPatternAST.exists((temp, i) -> isOptonalOrDefaultPattern(temp, i, symbolWithDefaultValue,
        cloned, defaultValueMatched, engine))) {
      return F.NIL;
    }
    if (defaultValueMatched[0]) {
      if (cloned.isOneIdentityAST1()) {
        return cloned.arg1();
      }
      return cloned;
    }
    return F.NIL;
  }

  private boolean isOptonalOrDefaultPattern(IExpr temp, int i, ISymbol symbolWithDefaultValue,
      IASTAppendable cloned, boolean[] defaultValueMatched, EvalEngine engine) {
    if (temp.isPatternDefault()) {
      if (temp.isOptional()) {
        IAST optional = (IAST) temp;
        IExpr optionalValue =
            (optional.isAST2()) ? optional.arg2() : symbolWithDefaultValue.getDefaultValue();
        if (optionalValue.isPresent()) {
          if (!(matchExpr(temp.first(), optionalValue, engine))) {
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
   * Checks if the two expressions match each other
   *
   * @return
   */
  protected boolean matchExpr(IExpr lhsPatternExpr, final IExpr lhsEvalExpr, EvalEngine engine) {
    return matchExpr(lhsPatternExpr, lhsEvalExpr, engine, new StackMatcher(engine));
  }

  /**
   * Checks if the two expressions match each other. If <code>true</code> match the rest of the
   * <code>stackMatcher</code>.
   *
   * @param lhsPatternExpr the left-hand-side pattern expression
   * @param lhsEvalExpr the left-hand-side expression which should match <code>lhsPatternExpr</code>
   * @param engine
   * @param stackMatcher a stack of entrie of expressions, which have to match each other.
   * @return
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
    if (matched) {
      return stackMatcher.matchRest();
    }
    return false;
  }

  private boolean matchTrue(IExpr lhsPatternExpr, EvalEngine engine, StackMatcher stackMatcher) {
    IExpr lhsTest = fPatternMap.substituteSymbols(lhsPatternExpr, F.NIL);
    if (engine.evalTrue(lhsTest)) {
      return stackMatcher.matchRest();
    }
    return false;
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
      int lhsEvalSize = lhsEval.size();
      if (lhsPattern.arg1().isPatternSequence(false)) {
        // TODO only the special case, where the last element is
        // a pattern sequence, is handled here
        IASTAppendable seq = F.Sequence();
        seq.appendAll(lhsEval, 1, lhsEvalSize);
        if (((IPatternSequence) lhsPattern.arg1()).matchPatternSequence(seq, fPatternMap,
            lhsPattern.topHead())) {
          return true;
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
              fPatternMap.resetPattern(patternValues);
            }
          }
        }
        if (evaled) {
          return false;
        }
      }
    }

    FlatOrderlessStepVisitor visitor = new FlatOrderlessStepVisitor(sym, lhsPatternFinal,
        lhsEvalFinal, stackMatcher, fPatternMap, sym.hasFlatAttribute());
    MultisetPartitionsIterator iter = new MultisetPartitionsIterator(visitor, lhsPattern.argSize());
    return !iter.execute();
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
  private IExpr matchOptionalArgumentsAST(ISymbol symbolWithDefaultValue, IAST lhsPatternAST,
      IAST lhsEvalAST, EvalEngine engine) {
    int lhsSize = lhsEvalAST.size();
    IASTAppendable cloned = F.ast(lhsPatternAST.head(), lhsPatternAST.size());
    boolean defaultValueMatched = false;
    for (int i = 1; i < lhsPatternAST.size(); i++) {
      IExpr temp = lhsPatternAST.get(i);
      if (temp.isPatternDefault()) {
        if (temp.isOptional()) {
          IAST optional = (IAST) temp;
          if (i < lhsSize) {
            cloned.append(optional.arg1());
            continue;
          }
          IExpr optionalValue =
              (optional.isAST2()) ? optional.arg2() : symbolWithDefaultValue.getDefaultValue();
          if (optionalValue.isPresent()) {
            if (!(matchExpr(optional.arg1(), optionalValue, engine))) {
              return F.NIL;
            }
            defaultValueMatched = true;
            continue;
          }
        } else {
          IPattern pattern = (IPattern) temp;
          IExpr positionDefaultValue = symbolWithDefaultValue.getDefaultValue(i);
          if (positionDefaultValue.isPresent()) {
            if (!((IPatternObject) temp).matchPattern(positionDefaultValue, fPatternMap)) {
              return F.NIL;
            }
            defaultValueMatched = true;
            continue;
          } else {
            if (i < lhsSize) {
              cloned.append(pattern);
              continue;
            }
            IExpr commonDefaultValue = symbolWithDefaultValue.getDefaultValue();
            if (commonDefaultValue.isPresent()) {
              if (!((IPatternObject) temp).matchPattern(commonDefaultValue, fPatternMap)) {
                return F.NIL;
              }
              defaultValueMatched = true;
              continue;
            }
          }
        }
      }
      cloned.append(temp);
    }
    if (defaultValueMatched) {
      if (cloned.isOneIdentityAST1()) {
        return cloned.arg1();
      }
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
      IExpr[] allReplaceExprs, int[] allReplaceIndex, int[] allRemovePositions,
      int[] allRemoveIndex, EvalEngine engine) {
    IPatternMap patternMap = null;
    patternMap = createPatternMap();
    patternMap.initPattern();
    setLHSExprToMatch(lhsEvalAST);

    int lhsPatternSize = lhsPatternAST.size();
    int lhsEvalSize = lhsEvalAST.size();
    if (lhsPatternSize > lhsEvalSize) {
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
            // if (patternArg.isPatternSequence(false)) {
            // IPatternSequence patternSequence = (IPatternSequence) patternArg;
            // matched = matchBlankSequence(patternSequence, lhsPatternAST, j, lhsEvalAST, engine,
            // stackMatcher);
            // } else {
            matched = matchExpr(patternArg, evalArg, engine, stackMatcher);
            // }
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
          // IExpr rhs = replacePatternMatch(lhsEvalAST, fPatternMap, engine, false);
          allReplacePositions[allReplaceIndex[0]] = replacePosition;
          // allReplaceExprs[allReplaceIndex[0]] = rhs;
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
    fSetFlags = objectInput.readShort();
    fLhsPatternExpr = (IExpr) objectInput.readObject();
    if (fLhsPatternExpr != null) {
      int[] priority = new int[] {IPatternMap.DEFAULT_RULE_PRIORITY};
      this.fPatternMap = IPatternMap.determinePatterns(fLhsPatternExpr, priority, null);
      fLHSPriority = priority[0];
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
    int[] removedPositionsArray = new int[lhsPattern.size() - 1];
    int removedPosition = 0;
    boolean matchedPattern = false;
    for (int i = 1; i < lhsPattern.size(); i++) {
      IExpr lhs = lhsPattern.getRule(i);
      IExpr rhs = lhsEval.getRule(i);
      if (lhs instanceof IPatternObject) {
        if (lhs instanceof IPatternSequence) {
          if (i == lhsPattern.size() - 1) {
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
            result = engine.evaluate(result);
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
          result = engine.evaluate(result);
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
