package org.matheclipse.core.patternmatching;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayDeque;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;

public class PatternMatcher extends IPatternMatcher implements Externalizable {
  private static final Logger LOGGER = LogManager.getLogger();

  /**
   * There are two kinds of matching <code>Entry</code> pairs in the {@link StackMatcher}.
   *
   * <ul>
   *   <li>The first expression of the pair must pattern-match the second expression of the pair.
   *   <li>If the second expression of the pair is {@link F#NIL}, substitute the symbols in the
   *       first expression of the pair and try to evaluate to <code>True</code>.
   * </ul>
   */
  private static final class Entry {
    final IExpr fPatternExpr;
    final IExpr fEvalExpr;

    /**
     * Constructor for the <code>Entry</code>, there the second expression of the pair is {@link
     * F#NIL}. For this type the matcher substitutes the symbols in the first expression of the pair
     * with the matched values and tries to evaluate it to <code>True </code> in {@link
     * StackMatcher}.
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
   * Matches an <code>IAST</code> with header attribute <code>Orderless</code> .
   *
   * @see ISymbol#ORDERLESS
   */
  private final class OrderlessMatcher {

    private IAST fLHSPatternAST;

    private IAST fLHSEvalAST;

    /**
     * The used (i.e. matched) expression indexes in the LHS evaluation expression; <code>-1</code>
     * indicates an unused index.
     */
    private int[] fUsedIndex;

    /**
     * Match a pattern expression against an evaluation expression, there the arguments are
     * commutative (i.e. the head of the AST expression has attribute <code>Orderless</code>)
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

    public void filterResult(IASTAppendable result) {
      for (int i = 0; i < fUsedIndex.length; i++) {
        result.set(fUsedIndex[i], null);
      }
      int indx = 1;
      while (indx < result.size()) {
        if (result.get(indx) == null) {
          result.remove(indx);
        } else {
          indx++;
        }
      }
    }

    /**
     * @param lhsPosition the position in the LHS expression which should actually be matched.
     * @param stackMatcher
     * @return
     */
    public boolean matchOrderlessAST(
        int lhsPosition, StackMatcher stackMatcher, EvalEngine engine) {
      if (lhsPosition >= fLHSPatternAST.size()) {
        return stackMatcher.matchRest();
      }
      final IExpr subPattern = fLHSPatternAST.get(lhsPosition);
      final IExpr[] patternValues = fPatternMap.copyPattern();
      return fLHSEvalAST.exists(
          (temp, j) -> {
            if (fLHSPatternAST.forAll((x, i) -> fUsedIndex[i - 1] != j)) {
              boolean matched = false;
              final StackMatcher localStackMatcher =
                  stackMatcher == null ? new StackMatcher(engine) : stackMatcher;
              int lastStackSize = localStackMatcher.size();
              try {
                if (localStackMatcher.push(subPattern, temp)) {
                  fUsedIndex[lhsPosition - 1] = j;
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
          });
    }
  }

  /**
   * Manage a stack of entry-pairs of expressions, which have to match each other or evaluate to
   * <code>true</code>.
   *
   * <p>There are two kinds of matching each entry-pair in the stack:
   *
   * <ul>
   *   <li>The first expression of the pair must pattern-match the second expression of the pair.
   *   <li>If the second expression of the pair is {@link F#NIL}, substitute the symbols in the
   *       first expression of the pair and try to evaluate to <code>True</code>.
   * </ul>
   */
  @SuppressWarnings("serial")
  /* package private */ final class StackMatcher extends ArrayDeque<Entry> {
    final EvalEngine fEngine;

    public StackMatcher(EvalEngine engine) {
      fEngine = engine;
    }

    /**
     * Match the entries of the stack recursively starting from the top entry.
     *
     * <p>There are two kinds of matching each entry-pair in the stack:
     *
     * <ul>
     *   <li>The first expression of the pair must pattern-match the second expression of the pair.
     *   <li>If the second expression of the pair is {@link F#NIL}, substitute the symbols in the
     *       first expression of the pair and try to evaluate to <code>True</code>.
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
      // if (fPatternMap.isValueAssigned()) {
      // patternExpr = fPatternMap.substitutePatternOrSymbols(patternExpr, true);
      // }
      if (patternExpr.isASTOrAssociation()) {
        if (!patternExpr.isFreeOfPatterns()) {
          // insert for delayed evaluation in matchRest() method
          push(new Entry(patternExpr, evalExpr));
          return true;
        }
      } else if (patternExpr instanceof IPatternObject) {
        return matchPattern((IPatternObject) patternExpr, evalExpr, fEngine, this);
        // return ((IPatternObject) patternExpr).matchPattern(evalExpr, fPatternMap);
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
  public static boolean equivalent(
      final IExpr patternExpr1, final IExpr patternExpr2, final IPatternMap pm1, IPatternMap pm2) {
    if (!patternExpr1.isPatternExpr()) {
      if (!patternExpr2.isPatternExpr()) {
        return patternExpr1.equals(patternExpr2);
      }
      return false;
    }
    if (patternExpr1.isASTOrAssociation()) {
      if (patternExpr2.isASTOrAssociation()) {
        final IAST l1 = (IAST) patternExpr1;
        final IAST l2 = (IAST) patternExpr2;
        if (l1.size() != l2.size()) {
          return false;
        }
        return l1.forAll(
            (temp1, i) -> {
              IExpr temp2 = l2.get(i);
              if (temp1 == temp2) {
                return true;
              }
              if (temp1.hashCode() != temp2.hashCode()) {
                if (temp1.isPatternExpr() && temp2.isPatternExpr()) {
                  return equivalent(temp1, temp2, pm1, pm2);
                }
                return false;
              }
              if (!temp1.isPatternExpr() || !temp2.isPatternExpr()) {
                if (!temp1.equals(temp2)) {
                  return false;
                }
              }
              return equivalent(temp1, temp2, pm1, pm2);
            },
            0);
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

  /**
   * Remove parts which are "free of patterns" at the start or the end positions in <code>lhsPattern
   * </code> and <code>lhsEval</code>.
   *
   * @param lhsPattern the expression which can contain pattern-matching objects
   * @param lhsEval the expression which can contain no patterns
   * @return <code>null</code> if the matching isn't possible.
   */
  private static IAST[] removeFlat(final IAST lhsPattern, final IAST lhsEval) {
    IASTAppendable lhsPatternAST = lhsPattern.copyAppendable();
    IASTAppendable lhsEvalAST = lhsEval.copyAppendable();
    // start from the beginning
    int iIndex = 1;
    while (iIndex < lhsPatternAST.size()) {
      IExpr temp = lhsPatternAST.get(iIndex);
      if (temp.isFreeOfPatterns()) {
        boolean evaled = false;
        if (iIndex < lhsEvalAST.size()) {
          if (lhsEvalAST.get(iIndex).equals(temp)) {
            lhsPatternAST.remove(iIndex);
            lhsEvalAST.remove(iIndex);
            evaled = true;
          }
        }
        if (!evaled) {
          return null;
        }
        continue;
      }
      break;
    }

    // now start from the end
    iIndex = lhsPatternAST.size() - 1;
    int jIndex = lhsEvalAST.size() - 1;
    while (iIndex > 0) {
      IExpr temp = lhsPatternAST.get(iIndex);
      if (temp.isFreeOfPatterns()) {
        boolean evaled = false;
        if (jIndex < lhsEvalAST.size()) {
          if (lhsEvalAST.get(jIndex).equals(temp)) {
            lhsPatternAST.remove(iIndex--);
            lhsEvalAST.remove(jIndex--);
            evaled = true;
          }
        }
        if (!evaled) {
          return null;
        }
        continue;
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
   * @return <code>null</code> if the matching isn't possible.
   */
  private static IAST[] removeOrderless(final IAST lhsPattern, final IAST lhsEval) {
    int iIndex = 1;
    int jIndex = -1;
    while (iIndex < lhsPattern.size()) {
      IExpr temp = lhsPattern.get(iIndex);
      if (temp.isFreeOfPatterns()) {
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
        if (temp.isFreeOfPatterns()) {
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

  /** Needed for serialization */
  public PatternMatcher() {
    super(null);
    this.fLHSPriority = IPatternMap.DEFAULT_RULE_PRIORITY;
    this.fThrowIfTrue = false;
    this.fLhsPatternExpr = null;
    this.fPatternMap = null;
  }

  public PatternMatcher(final IExpr patternExpr) {
    this(patternExpr, true);
  }

  public PatternMatcher(final IExpr patternExpr, boolean initAll) {
    super(patternExpr);
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
   * Check if the condition for the right-hand-sides <code>Module[] or Condition[]</code>
   * expressions evaluates to <code>true</code>. Override it in subclasses.
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

  // @Override
  // public int compareTo(IPatternMatcher pm) {
  // if (fPriority < pm.getPriority()) {
  // return -1;
  // }
  // if (fPriority > pm.getPriority()) {
  // return 1;
  // }
  // return equivalent(pm);
  // }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    PatternMatcher other = (PatternMatcher) obj;
    // if (fPatternMap == null) {
    // if (other.fPatternMap != null)
    // return false;
    // } else if (!fPatternMap.equals(other.fPatternMap))
    // return false;
    // if (fPriority != other.fPriority)
    // return false;
    return true;
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
    // if (fLHSPriority+getRHSPriority() <
    // patternMatcher.getLHSPriority()+patternMatcher.getRHSPriority()) {
    // return -1;
    // }
    // if (fLHSPriority+getRHSPriority() >
    // patternMatcher.getLHSPriority()+patternMatcher.getRHSPriority()) {
    // return 1;
    // }

    if (fLHSPriority < patternMatcher.getLHSPriority()) {
      return -1;
    }
    if (fLHSPriority > patternMatcher.getLHSPriority()) {
      return 1;
    }

    // if (getRHSPriority() < patternMatcher.getRHSPriority()) {
    // return -1;
    // }
    // if (getRHSPriority() > patternMatcher.getRHSPriority()) {
    // return 1;
    // }

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
    result = prime * result;
    // result = prime * result + ((fPatternMap == null) ? 0 :
    // fPatternMap.hashCode());
    // result = prime * result + fPriority;
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

  protected void logConditionFalse(
      final IExpr lhsEvalAST, final IExpr lhsPatternAST, IExpr rhsAST) {
    // System.out.println("\nCONDITION false: " + lhsEvalAST.toString() +
    // "\n >>> " + lhsPatternAST.toString() + " := "
    // + rhsAST.toString());
  }

  protected boolean matchAST(
      IAST lhsPatternAST, final IExpr lhsEvalExpr, EvalEngine engine, StackMatcher stackMatcher) {
    if (lhsEvalExpr instanceof IAST) {
      if (lhsPatternAST.isFreeOfPatterns() && lhsPatternAST.equals(lhsEvalExpr)) {
        return stackMatcher.matchRest();
      }

      IAST lhsEvalAST = (IAST) lhsEvalExpr;
      final ISymbol sym = lhsPatternAST.topHead();

      if (lhsPatternAST.size() <= lhsEvalAST.size()) {
        if (lhsPatternAST.isOrderlessAST()) {
          IExpr temp =
              fPatternMap.substituteASTPatternOrSymbols(lhsPatternAST, false).orElse(lhsPatternAST);
          if (temp.isAST(lhsPatternAST.head())) {
            lhsPatternAST = (IAST) temp;
            IAST[] removed = removeOrderless(lhsPatternAST, lhsEvalAST);
            if (removed == null) {
              return false;
            }
            lhsPatternAST = removed[0];
            lhsEvalAST = removed[1];
          }
        } else if (lhsPatternAST.isFlatAST()) {
          IExpr temp =
              fPatternMap.substituteASTPatternOrSymbols(lhsPatternAST, false).orElse(lhsPatternAST);
          if (temp.isAST(lhsPatternAST.head())) {
            IAST[] removed = removeFlat((IAST) temp, lhsEvalAST);
            if (removed == null) {
              return false;
            }
            lhsPatternAST = removed[0];
            lhsEvalAST = removed[1];
          }
        }

        if ((lhsPatternAST.isFlatAST())
            && sym.equals(lhsEvalAST.topHead())
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
      // if (lhsPatternAST.size() < lhsEvalSize) {
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
              if (((IPatternSequence) patternTest.arg1())
                  .matchPatternSequence(seq, fPatternMap, lhsPatternAST.topHead())) {
                if (matchAST(
                    lhsPatternAST.removeFromEnd(lastPosition),
                    lhsEvalAST.removeFromEnd(lastPosition),
                    engine,
                    stackMatcher)) {
                  return fPatternMap.isPatternTest(patternTest.arg1(), patternTest.arg2(), engine);
                }
                return false;
              }
            }
          }
        } else if (lhsPatternAST.size() > 1 && lhsPatternAST.arg1().isPatternSequence(false)) {
          IPatternSequence patternSequence = (IPatternSequence) lhsPatternAST.arg1();
          return matchBlankSequence(
              patternSequence, lhsPatternAST, 1, lhsEvalAST, engine, stackMatcher);
        } else {
          if (lhsPatternAST.size() > 1 && lhsEvalSize > 1) {
            if (matchExpr(lhsPatternAST.arg1(), lhsEvalAST.arg1(), engine)) {
              boolean matched =
                  matchAST(
                      lhsPatternAST.rest().addEvalFlags(IAST.CONTAINS_PATTERN_SEQUENCE),
                      lhsEvalAST.rest(),
                      engine,
                      stackMatcher);
              if (matched) {
                return true;
              }
            }
          }
        }
        return false;
      }

      if (lhsPatternAST.size() != lhsEvalSize //
          || !matchHeads(lhsPatternAST, lhsEvalAST, engine)) {
        return false;
      }

      if (lhsPatternAST.isOrderlessAST() && lhsPatternAST.size() > 2) {
        // only "pure Orderless" and "FlatOrderless with same size()" will be handled here:
        OrderlessStepVisitor visitor =
            new OrderlessStepVisitor(
                sym,
                lhsPatternAST,
                lhsEvalAST,
                stackMatcher,
                fPatternMap,
                (sym.hasOneIdentityAttribute() || sym.hasFlatAttribute())
                    // if FLAT isn't set and the Orderless ASTs have
                    // same size ==> use OneIdentity in pattern
                    // matching
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
   *     otherwise
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

  private boolean matchASTExpr(
      IAST lhsPatternAST, final IExpr lhsEvalExpr, EvalEngine engine, StackMatcher stackMatcher) {
    boolean matched = false;
    IExpr[] patternValues = fPatternMap.copyPattern();
    try {
      matched = matchAST(lhsPatternAST, lhsEvalExpr, engine, stackMatcher);
      if (!matched) {
        fPatternMap.resetPattern(patternValues);
        if ((lhsPatternAST.getEvalFlags() & IAST.CONTAINS_DEFAULT_PATTERN)
            == IAST.CONTAINS_DEFAULT_PATTERN) {
          // if (!lhsPatternAST.isFreeOfDefaultPatterns()) {
          if (lhsEvalExpr.isASTOrAssociation()
              && lhsPatternAST.hasOptionalArgument()
              && !lhsPatternAST.isOrderlessAST()) {
            // TODO for Power[x_, y_.] matching Power[a,b] test both cases Power[a,b] &&
            // Power[Power[a,b],1]
            IExpr temp =
                matchOptionalArgumentsAST(
                    lhsPatternAST.topHead(), lhsPatternAST, (IAST) lhsEvalExpr, engine);
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
   * <p>Distinguishes between "equally" matched list-expressions and list expressions with <code>
   * expr.isPatternExpr()==true</code>.
   *
   * @param lhsPatternAST
   * @param lhsEvalAST
   * @param lhsEvalOffset
   * @param stackMatcher
   * @return
   */
  private boolean matchASTSequence(
      IAST lhsPatternAST,
      IAST lhsEvalAST,
      final int lhsEvalOffset,
      EvalEngine engine,
      StackMatcher stackMatcher) {
    // distinguish between "equally" matched list-expressions and AST expressions with
    // "CONTAINS_PATTERN" flag
    IExpr[] patternValues = fPatternMap.copyPattern();
    int lastStackSize = stackMatcher.size();
    boolean matched = false;
    try {
      if (lhsPatternAST.size() == lhsEvalAST.size()) {
        IAST[] removed = remove(lhsPatternAST, lhsEvalAST, engine, stackMatcher);
        if (removed == null) {
          return false;
        } else {
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
      }

      for (int i = 1; i < lhsPatternAST.size(); i++) {
        if (!stackMatcher.push(lhsPatternAST.get(i), lhsEvalAST.get(lhsEvalOffset + i))) {
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
  private boolean matchASTSpecialBuiltIn(
      IAST lhsPatternAST, final IExpr lhsEvalExpr, EvalEngine engine, StackMatcher stackMatcher) {
    int functionID = lhsPatternAST.headID();
    if (functionID >= ID.Association && functionID <= ID.Verbatim) {
      boolean matched = false;
      if (lhsPatternAST.size() == 2) {
        final IExpr[] patternValues;
        switch (functionID) {
          case ID.Association:
            patternValues = fPatternMap.copyPattern();
            try {
              if (lhsEvalExpr.isAssociation()) {
                IAST lhsPatternAssociation = lhsPatternAST;
                // TODO set/determine pattern matching flags?
                IASTMutable lhsPatternList = (IASTMutable) lhsPatternAssociation.normal(false);
                lhsPatternList.set(0, S.Association);
                IAssociation lhsEvalAssociation = (IAssociation) lhsEvalExpr;
                IASTMutable lhsEvalList = lhsEvalAssociation.normal(false);
                lhsEvalList.set(0, S.Association);
                matched = matchExpr(lhsPatternList, lhsEvalList, engine, stackMatcher);
                return matched;
              }
              matched = matchASTExpr(lhsPatternAST, lhsEvalExpr, engine, stackMatcher);
            } finally {
              if (!matched) {
                fPatternMap.resetPattern(patternValues);
              }
            }
            return matched;
          case ID.Except:
            patternValues = fPatternMap.copyPattern();
            try {
              matched = !matchExpr(lhsPatternAST.arg1(), lhsEvalExpr, engine, stackMatcher);
            } finally {
              if (!matched) {
                fPatternMap.resetPattern(patternValues);
              }
            }
            return matched;
          case ID.HoldPattern:
          case ID.Literal:
            patternValues = fPatternMap.copyPattern();
            try {
              matched = matchExpr(lhsPatternAST.arg1(), lhsEvalExpr, engine, stackMatcher);
            } finally {
              if (!matched) {
                fPatternMap.resetPattern(patternValues);
              }
            }
            return matched;
          case ID.Optional:
            return matchOptional(lhsPatternAST, lhsEvalExpr, engine, stackMatcher);
          case ID.Verbatim:
            return lhsPatternAST.arg1().equals(lhsEvalExpr);
          default:
        }
      } else if (lhsPatternAST.size() == 3) {
        if (functionID >= ID.Complex && functionID <= ID.Rational) {
          final IExpr[] patternValues;
          switch (functionID) {
            case ID.Complex:
              patternValues = fPatternMap.copyPattern();
              try {
                if (lhsEvalExpr.isNumber()) {
                  INumber number = (INumber) lhsEvalExpr;
                  matched =
                      matchExpr(lhsPatternAST.arg1(), number.re(), engine, stackMatcher)
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
            case ID.Condition:
              return matchCondition(lhsPatternAST, lhsEvalExpr, engine, stackMatcher);
            case ID.Except:
              patternValues = fPatternMap.copyPattern();
              try {
                matched =
                    !matchExpr(lhsPatternAST.arg1(), lhsEvalExpr, engine, stackMatcher)
                        && matchExpr(lhsPatternAST.arg2(), lhsEvalExpr, engine, stackMatcher);
              } finally {
                if (!matched) {
                  fPatternMap.resetPattern(patternValues);
                }
              }
              return matched;
            case ID.Optional:
              return matchOptional(lhsPatternAST, lhsEvalExpr, engine, stackMatcher);
            case ID.PatternTest:
              patternValues = fPatternMap.copyPattern();
              try {
                final IExpr lhsPatternExpr = lhsPatternAST.arg1();
                final IExpr patternTest = lhsPatternAST.arg2();
                if (lhsPatternExpr instanceof IPatternObject && patternTest.isFreeOfPatterns()) {
                  // isPatternTest() can be done immediately, because patternTest contains no
                  // other pattern symbol
                  if (matchPattern(
                      (IPatternObject) lhsPatternExpr, lhsEvalExpr, engine, stackMatcher)) {
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
            case ID.Rational:
              patternValues = fPatternMap.copyPattern();
              try {
                if (lhsEvalExpr.isRational()) {
                  IRational rational = (IRational) lhsEvalExpr;
                  matched =
                      matchExpr(lhsPatternAST.arg1(), rational.numerator(), engine, stackMatcher)
                          && matchExpr(
                              lhsPatternAST.arg2(), rational.denominator(), engine, stackMatcher);
                  return matched;
                }
                matched = matchASTExpr(lhsPatternAST, lhsEvalExpr, engine, stackMatcher);
                return matched;
              } finally {
                if (!matched) {
                  fPatternMap.resetPattern(patternValues);
                }
              }

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
   * Match a left-hand-side <code>Alternatives(p1, p2, ..., p_i)</code>.
   *
   * <pre>Alternatives(p1, p2, ..., p_i)
   * </pre>
   *
   * or
   *
   * <pre>p1 | p2 | ... | p_i
   * </pre>
   *
   * <p>is a pattern that matches any of the patterns <code>p1, p2,...., p_i</code>.
   *
   * <h3>Examples</h3>
   *
   * <pre>&gt;&gt; a+b+c+d/.(a|b)-&gt;t
   * c + d + 2 t
   * </pre>
   *
   * @param lhsPatternAlternatives a <code>Alternatives(...)</code> expression
   * @param lhsEvalExpr the value which should be matched
   * @param engine
   * @return
   */
  private boolean matchAlternatives(
      IAST lhsPatternAlternatives, final IExpr lhsEvalExpr, EvalEngine engine) {
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
   * <pre><code>Condition(pattern, expr)
   * </code></pre>
   *
   * <p>or
   *
   * <pre><code>pattern /; expr
   * </code></pre>
   *
   * <p>places an additional constraint on <code>pattern</code> that only allows it to match if
   * <code>expr</code> evaluates to <code>True</code>.
   *
   * <h3>Examples</h3>
   *
   * <p>The controlling expression of a <code>Condition</code> can use variables from the pattern:
   *
   * <pre><code>&gt;&gt; f(3) /. f(x_) /; x&gt;0 -&gt; t
   * t
   *
   * &gt;&gt; f(-3) /. f(x_) /; x&gt;0 -&gt; t
   * f(-3)
   * </code></pre>
   *
   * <p><code>Condition</code> can be used in an assignment:
   *
   * <pre><code>&gt;&gt; f(x_) := p(x) /; x&gt;0
   * &gt;&gt; f(3)
   * p(3)
   *
   * &gt;&gt; f(-3)
   * f(-3)
   * </code></pre>
   *
   * @param lhsPatternCondition a <code>Condition(pattern-expr, test)</code> expression
   * @param lhsEvalExpr the value which should be matched
   * @param engine
   * @param stackMatcher
   * @return
   */
  private boolean matchCondition(
      final IAST lhsPatternCondition,
      final IExpr lhsEvalExpr,
      EvalEngine engine,
      StackMatcher stackMatcher) {
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
   * <pre><code>Optional(patt, default)
   * </code></pre>
   *
   * <p>or
   *
   * <pre><code>patt : default
   * </code></pre>
   *
   * <p>is a pattern which matches <code>patt</code>, which if omitted should be replaced by <code>
   * default</code>.
   *
   * <h3>Examples</h3>
   *
   * <pre><code>&gt;&gt; f(x_, y_:1) := {x, y}
   *
   * &gt;&gt; f(1, 2)
   * {1,2}
   *
   * &gt;&gt; f(a)
   * {a,1}
   * </code></pre>
   *
   * @param lhsPatternOptional a <code>Optional(patt, default</code> expression
   * @param lhsEvalExpr the value which should be matched
   * @param engine
   * @param stackMatcher
   * @return
   */
  private boolean matchOptional(
      final IAST lhsPatternOptional,
      final IExpr lhsEvalExpr,
      EvalEngine engine,
      StackMatcher stackMatcher) {
    boolean matched = false;
    final IExpr[] patternValues = fPatternMap.copyPattern();
    try {
      matched = matchExpr(lhsPatternOptional.arg1(), lhsEvalExpr, engine, stackMatcher);

    } finally {
      if (!matched) {
        fPatternMap.resetPattern(patternValues);
      }
    }
    return matched;
  }

  private boolean matchBlankSequence(
      final IPatternSequence patternSequence,
      final IAST lhsPatternAST,
      final int position,
      final IAST lhsEvalAST,
      EvalEngine engine,
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
    int lhsEvalIndex = 2; // lastPosition;
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
          matched =
              matchAST(
                  reducedLHSPatternAST, lhsEvalAST.copyFrom(lhsEvalIndex), engine, stackMatcher);
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
   * If possible substitute Orderless or Flat expressions and check if the two expressions match
   * each other
   *
   * @return
   */
  // private boolean matchSubstExpr(IExpr lhsPatternExpr, final IExpr lhsEvalExpr, EvalEngine
  // engine,
  // StackMatcher stackMatcher) {
  // if (fPatternMap.isValueAssigned() && lhsPatternExpr.size() > 1) {
  // if (lhsPatternExpr.isOrderlessAST() || lhsPatternExpr.isFlatAST()) {
  // IExpr temp = fPatternMap.substituteASTPatternOrSymbols((IAST) lhsPatternExpr, true);
  // if (temp.isPresent()) {
  // lhsPatternExpr = temp;
  // }
  // }
  // }
  // return matchExpr(lhsPatternExpr, lhsEvalExpr, engine, stackMatcher);
  //
  // }

  /**
   * Match the <code>lhsPatternAST</code> with its <code>Default[]</code> values.
   *
   * @param symbolWithDefaultValue the symbol for getting the associated default values from
   * @param lhsPatternAST left-hand-side which may contain patterns with default values
   * @param engine the evaluation engine
   * @return <code>F.NIL</code> if the given <code>lhsPatternAST</code> could not be matched or
   *     contains no pattern with default value.
   */
  private IExpr matchDefaultArgumentsAST(
      ISymbol symbolWithDefaultValue, IAST lhsPatternAST, EvalEngine engine) {
    IASTAppendable cloned = F.ast(lhsPatternAST.head(), lhsPatternAST.size());
    boolean[] defaultValueMatched = new boolean[] {false};
    if (lhsPatternAST.exists(
        (temp, i) -> {
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
            } else {
              IExpr commonDefaultValue = symbolWithDefaultValue.getDefaultValue();
              if (commonDefaultValue.isPresent()) {
                if (!((IPatternObject) temp).matchPattern(commonDefaultValue, fPatternMap)) {
                  return true;
                }
                defaultValueMatched[0] = true;
                return false;
              }
            }
          }
          cloned.append(temp);
          return false;
        })) {
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
  protected boolean matchExpr(
      IExpr lhsPatternExpr, final IExpr lhsEvalExpr, EvalEngine engine, StackMatcher stackMatcher) {
    boolean matched = false;
    if (lhsPatternExpr.isASTOrAssociation()) {
      // IAST lhsPatternAST = (IAST) lhsPatternExpr;
      // int functionID = lhsPatternAST.headID();
      // if (functionID >= ID.Alternatives && functionID <= ID.Rational) {
      return matchASTSpecialBuiltIn((IAST) lhsPatternExpr, lhsEvalExpr, engine, stackMatcher);
      // } else {
      // matched = matchASTExpr(lhsPatternAST, lhsEvalExpr, engine, stackMatcher);
      // }
    } else if (lhsPatternExpr instanceof IPatternObject) {
      matched = matchPattern((IPatternObject) lhsPatternExpr, lhsEvalExpr, engine, stackMatcher);
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
  private boolean matchFlatAndFlatOrderless(
      final ISymbol sym,
      IAST lhsPattern,
      IAST lhsEval,
      EvalEngine engine,
      StackMatcher stackMatcher) {
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
  private boolean matchFlat(
      final ISymbol sym,
      final IAST lhsPattern,
      final IAST lhsEval,
      EvalEngine engine,
      StackMatcher stackMatcher) {
    if (lhsPattern.isAST1()) {
      int lhsEvalSize = lhsEval.size();
      if (lhsPattern.arg1().isPatternSequence(false)) {
        // TODO only the special case, where the last element is
        // a pattern sequence, is handled here
        IASTAppendable seq = F.Sequence();
        seq.appendAll(lhsEval, 1, lhsEvalSize);
        if (((IPatternSequence) lhsPattern.arg1())
            .matchPatternSequence(seq, fPatternMap, lhsPattern.topHead())) {
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
  private boolean matchFlatOrderless(
      final ISymbol sym,
      IAST lhsPattern,
      IAST lhsEval,
      EvalEngine engine,
      StackMatcher stackMatcher) {
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
                // if ((((IAST) patternArg).getEvalFlags()
                // & IAST.CONTAINS_DEFAULT_PATTERN) == IAST.CONTAINS_DEFAULT_PATTERN) {
                // if (patternArg.isFree(x -> x.isOrderlessAST(), true)) {
                // matched = matchExpr(patternArg, evalArg, engine, stackMatcher);
                // evaled = true;
                //
                // if (matched) {
                // matched = matchFlatAndFlatOrderlessAST(sym, reduced,
                // lhsEvalFinal.removeAtCopy(k), engine, stackMatcher);
                // if (matched) {
                // return true;
                // }
                // }
                // }
                // continue;
                // }

                if ((((IAST) patternArg).getEvalFlags() & IAST.CONTAINS_DEFAULT_PATTERN)
                    == IAST.CONTAINS_DEFAULT_PATTERN) {
                  continue;
                }
              }

              if (patternArg.head().equals(evalArg.head())
                  && patternArg.isFree(x -> x.isOrderlessAST(), true)) {
                evaled = true;
                matched = matchExpr(patternArg, evalArg, engine, stackMatcher);
              }

              if (matched) {
                matched =
                    matchFlatAndFlatOrderless(
                        sym, reduced, lhsEvalFinal.removeAtCopy(k), engine, stackMatcher);
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

    FlatOrderlessStepVisitor visitor =
        new FlatOrderlessStepVisitor(
            sym, lhsPatternFinal, lhsEvalFinal, stackMatcher, fPatternMap, sym.hasFlatAttribute());
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
   *     contains no pattern with default value.
   */
  private IExpr matchOptionalArgumentsAST(
      ISymbol symbolWithDefaultValue, IAST lhsPatternAST, IAST lhsEvalAST, EvalEngine engine) {
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

  private boolean matchPattern(
      IPatternObject lhsPatternExpr,
      final IExpr lhsEvalExpr,
      EvalEngine engine,
      StackMatcher stackMatcher) {
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

  @Override
  public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
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
  private IAST[] remove(
      final IAST lhsPattern, final IAST lhsEval, EvalEngine engine, StackMatcher stackMatcher) {
    IASTAppendable lhsPatternAST = lhsPattern.copyAppendable();
    IASTAppendable lhsEvalAST = lhsEval.copyAppendable();

    int iIndex = 1;
    boolean evaled = false;
    boolean matched = false;
    while (iIndex < lhsPatternAST.size()) {
      IExpr lhs = lhsPatternAST.get(iIndex);
      IExpr rhs = lhsEvalAST.get(iIndex);

      if (lhs.isFreeOfPatterns()) {
        if (lhs.equals(rhs)) {
          lhsPatternAST.remove(iIndex);
          lhsEvalAST.remove(iIndex);
          evaled = true;
          continue;
        }
        return null;
      }
      if (lhs instanceof IPattern) {
        matched = matchPattern((IPatternObject) lhs, rhs, engine, stackMatcher);
        // matched = ((IPattern) lhs).matchPattern(rhs, fPatternMap);
        if (matched) {
          lhsPatternAST.remove(iIndex);
          lhsEvalAST.remove(iIndex);
          evaled = true;
          continue;
        }
        return null;
      }
      if (lhs instanceof IPatternSequence) {
        return UNEVALED;
      }
      // if (lhs.isASTOrAssociation() && lhs.isFree(x -> x.isOrderlessAST(), true)) {
      // matched = matchExpr(lhs, rhs, engine);
      // if (matched) {
      // lhsPatternAST.remove(iIndex);
      // lhsEvalAST.remove(iIndex);
      // evaled = true;
      // continue;
      // }
      // return null;
      // }
      iIndex++;
    }
    if (!evaled) {
      return UNEVALED;
    }
    // if (lhsPatternAST.size() > 1) {
    // IExpr temp = fPatternMap.substituteASTPatternOrSymbols(lhsPatternAST, true);
    // if (temp.isAST(lhsPatternAST.head())) {
    // return new IAST[] { (IAST) temp, lhsEvalAST };
    // }
    // }
    return new IAST[] {lhsPatternAST, lhsEvalAST};
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
  protected IExpr replaceSubExpressionOrderlessFlat(
      final IAST lhsPatternAST, final IAST lhsEvalAST, final IExpr rhsExpr, EvalEngine engine) {

    if (lhsPatternAST.size() < lhsEvalAST.size()) {
      if (lhsPatternAST.isOrderlessAST() && lhsPatternAST.isFlatAST()) {
        if (!matchHeads(lhsPatternAST, lhsEvalAST, engine)) {
          return F.NIL;
        }
        final OrderlessMatcher foMatcher = new OrderlessMatcher(lhsPatternAST, lhsEvalAST);
        boolean matched = foMatcher.matchOrderlessAST(1, new StackMatcher(engine), engine);
        if (matched) {
          IASTAppendable lhsResultAST = (lhsEvalAST).copyAppendable();
          foMatcher.filterResult(lhsResultAST);
          IExpr result = fPatternMap.substituteSymbols(rhsExpr, F.NIL);
          try {
            result = engine.evaluate(result);
            lhsResultAST.append(result);
            return lhsResultAST;
          } catch (final ConditionException e) {
            if (LOGGER.isDebugEnabled()) {
              logConditionFalse(lhsEvalAST, lhsPatternAST, rhsExpr);
            }
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
   * <p>Example:
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
  private IExpr matchFlatSequenceFromIndex(
      final IAST lhsPatternFlatAST,
      final IAST lhsEvalFlatAST,
      final IExpr rhsExpr,
      EvalEngine engine) {
    final int len = lhsEvalFlatAST.size() - lhsPatternFlatAST.size() + 1;
    for (int i = 0; i < len; i++) {
      if (matchASTSequence(
          lhsPatternFlatAST, lhsEvalFlatAST, i, engine, new StackMatcher(engine))) {
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
          if (LOGGER.isDebugEnabled()) {
            logConditionFalse(lhsEvalFlatAST, lhsPatternFlatAST, rhsExpr);
          }
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

  // @SuppressWarnings("unchecked")
  // private void readObject(ObjectInputStream stream) throws IOException,
  // ClassNotFoundException {
  // ObjectInputStream.GetField fields = stream.readFields();
  // this.fPatternMap = new PatternMap();
  // if (fLhsPatternExpr != null) {
  // init(fLhsPatternExpr);
  // }
  // }

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
    objectOutput.writeObject(fLhsPatternExpr);
  }
}
