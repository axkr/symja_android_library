package org.matheclipse.core.eval.util;

/**
 * A level specification definition for levels in nested lists.
 * 
 * Example: the nested list <code>[x,[y]]</code> has depth <code>3</code>
 * 
 */
public class LevelSpec {
	protected int fFromLevel;

	protected int fToLevel;

	protected int fFromDepth;

	protected int fToDepth;

	protected boolean fIncludeHeads;

	protected int fCurrentLevel;

	protected int fCurrentDepth;

	public LevelSpec() {
		this(0);
	}

	/**
	 * Define a level specification for all elements on level <code>level</code>.
	 * 
	 * @param level
	 */
	public LevelSpec(final int level) {
		this(level, true);
	}

	public LevelSpec(final int level, final boolean includeHeads) {
		this(level, level, includeHeads);
	}

	public LevelSpec(final int fromLevel, final int toLevel) {
		this(fromLevel, toLevel, true);
	}

	public LevelSpec(final int fromLevel, final int toLevel, final boolean includeHeads) {
		this(fromLevel, toLevel, Integer.MIN_VALUE, -1, includeHeads);
	}

	/**
	 * 
	 * Example value set for including all levels:<br>
	 * <code>fromLevel = 0;</code><br>
	 * <code>toLevel = Integer.MAX_VALUE;</code><br>
	 * <code>fromDepth = Integer.MIN_VALUE;</code><br>
	 * <code>toDepth = -1;</code><br>
	 * 
	 * 
	 * @param fromLevel
	 * @param toLevel
	 * @param fromDepth
	 * @param toDepth
	 * @param includeHeads
	 */
	public LevelSpec(final int fromLevel, final int toLevel, final int fromDepth, final int toDepth, final boolean includeHeads) {
		fFromLevel = fromLevel;
		fToLevel = toLevel;
		fCurrentLevel = 0;
		fIncludeHeads = includeHeads;
		fFromDepth = fromDepth;
		fCurrentDepth = -1;
		fToDepth = toDepth;
	}

	/**
	 * Get the current level
	 * 
	 */
	public final int getCurrentLevel() {
		return fCurrentLevel;
	}

	public final void resetCurrentLevel() {
		fCurrentLevel = 0;
	}

	public final void setFromLevelAsCurrent() {
		fCurrentLevel = fFromLevel;
	}

	public final void setToLevelAsCurrent() {
		fCurrentLevel = fToLevel;
	}

	public final void decCurrentDepth() {
		fCurrentDepth--;
	}

	/**
	 * Increments the current level
	 * 
	 */
	public final void incCurrentLevel() {
		fCurrentLevel++;
	}

	/**
	 * Decrements the current level
	 * 
	 */
	public final void decCurrentLevel() {
		fCurrentLevel--;
	}

	public boolean isInRange() {
		return (fCurrentLevel >= fFromLevel) && (fCurrentLevel <= fToLevel) && (fCurrentDepth >= fFromDepth)
				&& (fCurrentDepth <= fToDepth);
	}

	public boolean isInScope() {
		return (fCurrentLevel <= fToLevel) && (fCurrentDepth <= fToDepth);
	}

	public final int getCurrentDepth() {
		return fCurrentDepth;
	}

	public final void setCurrentDepth(final int currentDepth) {
		fCurrentDepth = currentDepth;
	}

	public final void setCurrentLevel(final int currentLevel) {
		fCurrentLevel = currentLevel;
	}

	/**
	 * @return the from level
	 */
	public int getFromLevel() {
		return fFromLevel;
	}

	/**
	 * @return the to level
	 */
	public int getToLevel() {
		return fToLevel;
	}

	/**
	 * @return the from depth
	 */
	public int getFromDepth() {
		return fFromDepth;
	}

	/**
	 * @return the to depth
	 */
	public int getToDepth() {
		return fToDepth;
	}

	/**
	 * @return if the level includes the heads of an expression
	 */
	public boolean isIncludeHeads() {
		return fIncludeHeads;
	}
}
