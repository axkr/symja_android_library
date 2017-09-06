package org.matheclipse.core.generic;

import javax.annotation.Nonnull;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IUnaryIndexFunction;

/**
 * Clone a given AST and set the i-th argument of the new AST to {@code arg} in
 * the {@code apply} method.
 * 
 */
public class BinaryBindIth1st implements IUnaryIndexFunction<IExpr, IExpr> {
	protected final IAST fConstant1;
	protected final IAST fConstant2;

	/**
	 * The {@code constant1} and {@code constant2} AST will be cloned in the
	 * {@code apply} method.
	 * 
	 * @param constant1
	 *          a &quot;template AST&quot; with all arguments set with a
	 *          predefined value.
	 * @param constant2
	 *          a &quot;template AST&quot; with all arguments set with a
	 *          predefined value.
	 */
	public BinaryBindIth1st(@Nonnull final IAST constant1, @Nonnull final IAST constant2) {
		super();
		fConstant1 = constant1;
		fConstant2 = constant2;
	}

	/**
	 * Clone the given AST and set the i-th argument of the new AST to {@code arg}
	 * .
	 * 
	 * @param index
	 *          the i-th index should be used
	 * @param firstArg
	 *          the i-th argument in the new AST
	 */
	@Override
	public IExpr apply(int index, final IExpr firstArg) {
		final IAST ast2 = fConstant2.setAtCopy(1, firstArg);
		return fConstant1.setAtCopy(index, ast2);
	}

}
