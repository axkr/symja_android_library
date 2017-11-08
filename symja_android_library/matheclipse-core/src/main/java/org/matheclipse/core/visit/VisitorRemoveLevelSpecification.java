package org.matheclipse.core.visit;

import java.util.function.Function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.math.MathException;

/**
 * A level specification visitor for levels in abstract syntax trees (AST),
 * which removes elements from a (cloned) AST in the
 * <code>visit(IAST clonedAST)</code> method.
 * 
 * Example: the nested list <code>{x,{y}}</code> has depth <code>3</code>
 * 
 */
public class VisitorRemoveLevelSpecification extends VisitorLevelSpecification {
	/**
	 * StopException will be thrown, if maximum number of Cases results are
	 * reached
	 *
	 */
	@SuppressWarnings("serial")
	public static class StopException extends MathException {
		public StopException() {
			super("Stop VisitorDeleteLevelSpecification evaluation");
		}
	}

	final int maximumRemoved;
	private int removedCounter;

	/**
	 * Create a LevelSpecification from an IInteger or IAST list-object.<br>
	 * <br>
	 * 
	 * An <code>expr</code> is interpreted as a <i>level specification</i> for
	 * the allowed levels in an AST.<br>
	 * If <code>expr</code> is a non-negative IInteger iValue set Level
	 * {1,iValue};<br>
	 * If <code>expr</code> is a negative IInteger iValue set Level {iValue, 0};
	 * <br>
	 * If <code>expr</code> is a List {i0Value, i1Value} set Level {i0Value,
	 * i1Value};<br>
	 * 
	 * @param function
	 *            the function which should be applied for an element
	 * @param expr
	 *            the given <i>level specification</i>
	 * @param maximumRemoved
	 *            maximum number of elements, which are allowed to remove
	 * @param includeHeads
	 *            set to <code>true</code>, if the header of an AST expression
	 *            should be included
	 * @throws MathException
	 *             if the <code>expr</code> is not a <i>level specification</i>
	 * @see
	 */
	public VisitorRemoveLevelSpecification(final Function<IExpr, IExpr> function, final IExpr expr, int maximumRemoved,
			boolean includeHeads, final EvalEngine engine) {
		super(function, expr, includeHeads, engine);
		this.maximumRemoved = maximumRemoved;
		this.removedCounter = 0;
	}

	/**
	 * Define a level specification for all elements on level <code>level</code>
	 * .
	 * 
	 * @param level
	 */
	public VisitorRemoveLevelSpecification(final Function<IExpr, IExpr> function, final int level) {
		this(function, level, true);
	}

	public VisitorRemoveLevelSpecification(final Function<IExpr, IExpr> function, final int level,
			final boolean includeHeads) {
		this(function, level, level, includeHeads);
	}

	public VisitorRemoveLevelSpecification(final Function<IExpr, IExpr> function, final int fromLevel,
			final int toLevel) {
		this(function, fromLevel, toLevel, true);
	}

	public VisitorRemoveLevelSpecification(final Function<IExpr, IExpr> function, final int fromLevel,
			final int toLevel, final boolean includeHeads) {
		this(function, fromLevel, toLevel, Integer.MIN_VALUE, -1, -1, includeHeads);
	}

	/**
	 * Example value set for including all levels:<br>
	 * <code>fromLevel = 0;</code><br>
	 * <code>toLevel = Integer.MAX_VALUE;</code><br>
	 * <code>fromDepth = Integer.MIN_VALUE;</code><br>
	 * <code>toDepth = -1;</code><br>
	 * 
	 * @param function
	 *            the function which should be applied for an element
	 * @param fromLevel
	 * @param toLevel
	 * @param fromDepth
	 * @param toDepth
	 * @param maximumRemoved
	 *            maximum number of elements, which are allowed to remove
	 * @param includeHeads
	 */
	public VisitorRemoveLevelSpecification(final Function<IExpr, IExpr> function, final int fromLevel,
			final int toLevel, final int fromDepth, final int toDepth, int maximumRemoved, final boolean includeHeads) {
		super(function, fromLevel, toLevel, fromDepth, toDepth, includeHeads);
		this.maximumRemoved = maximumRemoved;
		this.removedCounter = 0;
	}

	/**
	 * Get the number of remove operations.
	 * 
	 * @return the removed counter
	 */
	public int getRemovedCounter() {
		return removedCounter;
	}

	@Override
	public IExpr visit(IInteger element) {
		fCurrentDepth = -1;
		if (isInRange(fCurrentLevel, -1)) {
			return fFunction.apply(element);
		}
		return F.NIL;
	}

	@Override
	public IExpr visit(IFraction element) {
		fCurrentDepth = -1;
		if (isInRange(fCurrentLevel, -1)) {
			return fFunction.apply(element);
		}
		return F.NIL;
	}

	@Override
	public IExpr visit(IComplex element) {
		fCurrentDepth = -1;
		if (isInRange(fCurrentLevel, -1)) {
			return fFunction.apply(element);
		}
		return F.NIL;
	}

	@Override
	public IExpr visit(INum element) {
		fCurrentDepth = -1;
		if (isInRange(fCurrentLevel, -1)) {
			return fFunction.apply(element);
		}
		return F.NIL;
	}

	@Override
	public IExpr visit(IComplexNum element) {
		fCurrentDepth = -1;
		if (isInRange(fCurrentLevel, -1)) {
			return fFunction.apply(element);
		}
		return F.NIL;
	}

	@Override
	public IExpr visit(ISymbol element) {
		fCurrentDepth = -1;
		if (isInRange(fCurrentLevel, -1)) {
			return fFunction.apply(element);
		}
		return F.NIL;
	}

	@Override
	public IExpr visit(IPattern element) {
		fCurrentDepth = -1;
		if (isInRange(fCurrentLevel, -1)) {
			return fFunction.apply(element);
		}
		return F.NIL;
	}

	@Override
	public IExpr visit(IPatternSequence element) {
		fCurrentDepth = -1;
		if (isInRange(fCurrentLevel, -1)) {
			return fFunction.apply(element);
		}
		return F.NIL;
	}

	@Override
	public IExpr visit(IStringX element) {
		fCurrentDepth = -1;
		if (isInRange(fCurrentLevel, -1)) {
			return fFunction.apply(element);
		}
		return F.NIL;
	}

	@Override
	public IExpr visitExpr(IExpr element) {
		if (isInRange(fCurrentLevel, -1)) {
			return fFunction.apply(element);
		}
		return F.NIL;
	}

	/**
	 * <b>Note:</b> the given AST will be modified, i.e. some elements may be
	 * removed!
	 * 
	 * @param clonedAST
	 *            an AST where arguments could be removed.
	 */
	@Override
	public IExpr visit(IASTMutable clonedAST) {
		int minDepth = -1;
		IExpr arg;
		IExpr temp;
		try {
			fCurrentLevel++;
			if (fIncludeHeads) {
				arg = clonedAST.get(0);
				if (arg.isAST()) {
					arg = ((IAST) arg).clone();
				}
				temp = arg.accept(this);
				if (temp.isPresent()) {
					clonedAST.remove(0);
					removedCounter++;
					if (maximumRemoved >= 0) {
						if (removedCounter >= maximumRemoved) {
							throw new StopException();
						}
					}
				} else {
					clonedAST.set(0, arg);
				}
				if (fCurrentDepth < minDepth) {
					minDepth = fCurrentDepth;
				}

			}
			int i = 1;
			while (i < clonedAST.size()) {
				arg = clonedAST.get(i);
				if (arg.isAST()) {
					arg = ((IAST) arg).clone();
				}
				temp = arg.accept(this);
				if (temp.isPresent()) {
					clonedAST.remove(i);
					removedCounter++;
					if (maximumRemoved >= 0) {
						if (removedCounter >= maximumRemoved) {
							throw new StopException();
						}
					}
					continue;
				} else {
					clonedAST.set(i, arg);
				}
				i++;
				if (fCurrentDepth < minDepth) {
					minDepth = fCurrentDepth;
				}
			}
		} finally {
			fCurrentLevel--;
		}
		fCurrentDepth = --minDepth;
		if (isInRange(fCurrentLevel, minDepth)) {
			return fFunction.apply(clonedAST);
		}
		return F.NIL;

	}
}