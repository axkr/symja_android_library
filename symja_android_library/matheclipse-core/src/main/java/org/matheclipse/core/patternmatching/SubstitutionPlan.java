package org.matheclipse.core.patternmatching;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IDataExpr;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.ruleindex.SubstitutionPlanStats;
import org.matheclipse.core.patternmatching.ruleindex.SubstitutionPlanStats.Refusal;

/**
 * A precompiled substitution for one fixed expression - in practice the right-hand-side of a
 * rewrite rule.
 *
 * <p>
 * {@link IPatternMap#substituteSymbols(IExpr, IExpr)} walks <i>every</i> node of the expression on
 * every substitution and asks a replacement function about each one, even though the expression and
 * the set of replaceable symbols are both fixed when the rule is installed. This class records that
 * answer once: it mirrors only the <b>spine</b> of the expression - the nodes on a path from the
 * root to a symbol which will be replaced - and remembers, per replaced position, the <i>slot
 * index</i> of the value in the {@link IPatternMap}.
 *
 * <p>
 * Substituting then rebuilds the spine and hands back every other subtree by reference. The
 * copy-on-write behaviour is the same as the visitor's - untouched subtrees were already shared -
 * what disappears is the search for them. Rebuilding uses the same
 * {@link IAST#setAtCopy(int, IExpr)} and {@link IASTMutable#set(int, IExpr)} calls as
 * {@code VisitorReplaceAll.visitAST}, so the evaluation flags of the result are identical by
 * construction.
 *
 * <p>
 * A plan is immutable, so it can be shared by a matcher and all of its copies, and it is only ever
 * built for an expression the builder fully understands - see {@link #build(IExpr, IPatternMap)}.
 */
final class SubstitutionPlan {

  /** The node this plan rebuilds. */
  private final IAST original;

  /** Positions to rebuild, ascending; <code>0</code> addresses the head. */
  private final int[] childIndex;

  /** Plan for the child at the same position, or <code>null</code> if that child is a slot. */
  private final SubstitutionPlan[] child;

  /** Pattern map slot of the child at the same position, or <code>-1</code> if it has a plan. */
  private final int[] childSlot;

  private SubstitutionPlan(IAST original, int[] childIndex, SubstitutionPlan[] child,
      int[] childSlot) {
    this.original = original;
    this.childIndex = childIndex;
    this.child = child;
    this.childSlot = childSlot;
  }

  /**
   * Compile a substitution plan for <code>expr</code>.
   *
   * @param expr the expression which will be substituted repeatedly
   * @param patternMap the pattern map whose slot layout the plan is built against; the layout is
   *        preserved by {@link IPatternMap#copy()}, so the plan stays valid for copies of the
   *        matcher
   * @return the plan, or <code>null</code> if the expression contains something whose substitution
   *         semantics this class does not model and which therefore has to keep using
   *         {@link IPatternMap#substituteSymbols(IExpr, IExpr)}
   */
  static SubstitutionPlan build(IExpr expr, IPatternMap patternMap) {
    if (!expr.isAST()) {
      // a bare symbol or number as the whole expression - rare, and the root would have to be
      // replaced as a whole, which this plan shape does not express
      SubstitutionPlanStats.planRefused(Refusal.ROOT_NOT_AST);
      return null;
    }
    IdentityHashMap<IExpr, Integer> slots = new IdentityHashMap<IExpr, Integer>();
    for (int i = 0; i < patternMap.size(); i++) {
      IExpr key = patternMap.getKey(i);
      // substituteSymbols() only ever replaces symbols; a pattern object key can never match
      if (key instanceof ISymbol) {
        slots.put(key, Integer.valueOf(i));
      }
    }
    Builder builder = new Builder(slots);
    Object compiled = builder.compile(expr);
    if (builder.refused != null) {
      SubstitutionPlanStats.planRefused(builder.refused);
      return null;
    }
    SubstitutionPlanStats.planBuilt();
    if (compiled == null) {
      // no replaceable symbol occurs at all: substitution is the identity
      return new SubstitutionPlan((IAST) expr, EMPTY_INT, EMPTY_PLAN, EMPTY_INT);
    }
    // the root itself cannot be a bare replaceable symbol - excluded by the isAST() test above
    return (SubstitutionPlan) compiled;
  }

  private static final int[] EMPTY_INT = new int[0];

  private static final SubstitutionPlan[] EMPTY_PLAN = new SubstitutionPlan[0];

  /**
   * Rebuild the expression with the current values of the pattern map.
   *
   * @param patternMap the values to substitute
   * @param nilOrEmptySequence what an unassigned slot substitutes to: {@link F#NIL} leaves the
   *        symbol in place, {@link F#CEmptySequence} splices it away
   * @return {@link F#NIL} if nothing was replaced, exactly like the visitor
   */
  IExpr substitute(IPatternMap patternMap, IExpr nilOrEmptySequence) {
    IASTMutable result = null;
    for (int i = 0; i < childIndex.length; i++) {
      final IExpr value;
      if (child[i] != null) {
        value = child[i].substitute(patternMap, nilOrEmptySequence);
      } else {
        IExpr slotValue = patternMap.getValue(childSlot[i]);
        value = slotValue != null ? slotValue : nilOrEmptySequence;
      }
      if (!value.isPresent()) {
        // this subtree did not change - keep the original child
        continue;
      }
      if (result == null) {
        result = original.setAtCopy(childIndex[i], value);
      } else {
        result.set(childIndex[i], value);
      }
    }
    return result == null ? F.NIL : result;
  }

  /**
   * Compare two substitution results for the dual run check of
   * {@link org.matheclipse.core.basic.Config#SUBSTITUTION_PLAN_VALIDATE}.
   *
   * <p>
   * Structural equality alone would not catch the failure mode this class can plausibly have: a
   * rebuilt node which is equal but carries different evaluation flags, because the evaluator would
   * then skip work it should do (or redo work it should not). Only
   * {@link IAST#IS_FLATTENED_OR_SORTED_MASK} is compared - the other flags are memoized answers to
   * questions like {@link IAST#CONTAINS_NO_SPECIAL_ARG}, which one path may have been asked and the
   * other not, so comparing them would report differences which mean nothing.
   *
   * @return <code>true</code> if both expressions are equal and agree on their flattened/sorted
   *         state at every level
   */
  static boolean equalWithFlags(IExpr planned, IExpr generic) {
    if (!planned.equals(generic)) {
      return false;
    }
    if (!planned.isAST() || !generic.isAST()) {
      return true;
    }
    final IAST left = (IAST) planned;
    final IAST right = (IAST) generic;
    if ((left.getEvalFlags() & IAST.IS_FLATTENED_OR_SORTED_MASK) != (right.getEvalFlags()
        & IAST.IS_FLATTENED_OR_SORTED_MASK)) {
      return false;
    }
    if (left.size() != right.size()) {
      return false;
    }
    for (int i = 0; i < left.size(); i++) {
      if (!equalWithFlags(left.get(i), right.get(i))) {
        return false;
      }
    }
    return true;
  }

  /**
   * Compiles an expression into plan nodes. {@link #compile(IExpr)} returns <code>null</code> for a
   * subtree without any replaceable symbol, an {@link Integer} slot for a replaceable symbol, and a
   * {@link SubstitutionPlan} for a spine node.
   */
  private static final class Builder {

    private final IdentityHashMap<IExpr, Integer> slots;

    /** Set to the reason if the expression contains something this class does not model. */
    Refusal refused;

    Builder(IdentityHashMap<IExpr, Integer> slots) {
      this.slots = slots;
    }

    Object compile(IExpr expr) {
      if (expr instanceof IPatternObject) {
        // VisitorReplaceAll.visit(IPattern) rebuilds a new pattern object from a substituted
        // symbol - not modelled here
        refused = Refusal.PATTERN_OBJECT;
        return null;
      }
      if (expr instanceof IAssociation || expr instanceof IDataExpr) {
        // associations substitute into their rule values only
        refused = Refusal.ASSOCIATION_OR_DATA;
        return null;
      }
      if (expr.isSymbol()) {
        return slots.get(expr);
      }
      if (!expr.isAST()) {
        return null;
      }
      final IAST ast = (IAST) expr;
      if (ast.isAST(S.OptionValue, 2, 4)) {
        // replaced with an engine dependent value at substitution time
        refused = Refusal.OPTION_VALUE;
        return null;
      }
      List<Integer> indices = null;
      List<Object> parts = null;
      final int size = ast.size();
      for (int i = 0; i < size; i++) {
        Object compiled = compile(ast.get(i));
        if (refused != null) {
          return null;
        }
        if (compiled == null) {
          continue;
        }
        if (indices == null) {
          indices = new ArrayList<Integer>(4);
          parts = new ArrayList<Object>(4);
        }
        indices.add(Integer.valueOf(i));
        parts.add(compiled);
      }
      if (indices == null) {
        return null;
      }
      final int count = indices.size();
      int[] childIndex = new int[count];
      SubstitutionPlan[] child = new SubstitutionPlan[count];
      int[] childSlot = new int[count];
      for (int i = 0; i < count; i++) {
        childIndex[i] = indices.get(i).intValue();
        Object part = parts.get(i);
        if (part instanceof SubstitutionPlan) {
          child[i] = (SubstitutionPlan) part;
          childSlot[i] = -1;
        } else {
          child[i] = null;
          childSlot[i] = ((Integer) part).intValue();
        }
      }
      return new SubstitutionPlan(ast, childIndex, child, childSlot);
    }
  }
}
