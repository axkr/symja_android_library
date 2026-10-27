package org.matheclipse.core.visit;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

public abstract class AbstractLevelVisitor extends AbstractVisitor {

  protected int fFromLevel;

  protected int fToLevel;

  protected int fFromDepth;

  protected int fToDepth;

  protected final boolean fIncludeHeads;

  protected int fCurrentLevel;

  protected int fCurrentDepth;

  protected EvalEngine fEngine;

  /**
   * Create a LevelSpecification from an IInteger or IAST list-object.<br>
   * <br>
   * An <code>unevaledLevelExpr</code> is interpreted as a <i>level specification</i> for the
   * allowed levels in an AST.<br>
   * If <code>unevaledLevelExpr</code> is a non-negative IInteger iValue set Level {1,iValue};<br>
   * If <code>unevaledLevelExpr</code> is a negative IInteger iValue set Level {iValue, 0}; <br>
   * If <code>unevaledLevelExpr</code> is a List {i0Value, i1Value} set Level {i0Value,
   * i1Value};<br>
   * 
   * @param unevaledLevelExpr
   * @param includeHeads
   * @param startValueForAll
   * @param engine
   */
  public AbstractLevelVisitor(final IExpr unevaledLevelExpr, boolean includeHeads,
      int startValueForAll, final EvalEngine engine) {
    this.fIncludeHeads = includeHeads;
    this.fEngine = engine;
    LevelBounds bounds = new LevelBounds(unevaledLevelExpr.eval(engine), startValueForAll, true,
        i -> Validate.throwIntType(i, Integer.MIN_VALUE, engine), engine);
    fFromLevel = bounds.fromLevel;
    fToLevel = bounds.toLevel;
    fFromDepth = bounds.fromDepth;
    fToDepth = bounds.toDepth;
  }

  /**
   * Example value set for including all levels:<br>
   * <code>fromLevel = 0;</code><br>
   * <code>toLevel = Integer.MAX_VALUE;</code><br>
   * <code>fromDepth = Integer.MIN_VALUE;</code><br>
   * <code>toDepth = -1;</code><br>
   *
   * @param fromLevel
   * @param toLevel
   * @param fromDepth
   * @param toDepth
   * @param includeHeads
   */
  public AbstractLevelVisitor(final int fromLevel, final int toLevel, final int fromDepth,
      final int toDepth, final boolean includeHeads) {
    fIncludeHeads = includeHeads;
    fFromLevel = fromLevel;
    fToLevel = toLevel;
    fCurrentLevel = 0;
    fFromDepth = fromDepth;
    fCurrentDepth = -1;
    fToDepth = toDepth;
    fEngine = EvalEngine.get();
  }

  public void incCurrentLevel() {
    fCurrentLevel++;
  }

  public void decCurrentLevel() {
    fCurrentLevel--;
  }

  public boolean isInRange(int level, int depth) {
    return (level >= fFromLevel) && (level <= fToLevel) && (depth >= fFromDepth)
        && (depth <= fToDepth);
  }

  /**
   * Return a shallow copy of the <code>ast</code>.
   *
   * @param ast
   * @param x
   * @return
   */
  public IASTMutable createResult(IAST ast, final IExpr x) {
    // can't use copy() here, because index 0 will be set in some scenarios
    return ast.copyAppendable();
  }
}
