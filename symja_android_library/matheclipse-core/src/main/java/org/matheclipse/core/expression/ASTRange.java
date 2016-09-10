package org.matheclipse.core.expression;

import java.util.ArrayList;
import java.util.List;

import org.matheclipse.core.eval.Range;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Create a range for a given <code>IAST</code> instance.
 * 
 */
final public class ASTRange extends Range {
	/**
	 * Construct a range <code>[start..end[</code> for a given <code>IAST</code>
	 * instance.
	 * 
	 * @param list
	 * @param start
	 * @param end
	 * @throws IndexOutOfBoundsException
	 *             if <code>start</code> or <code>end</code> aren't valid.
	 */
	public ASTRange(final IAST list, final int start, final int end) {
		super(list, start, end);
	}

	/**
	 * Construct a range <code>[start..list.size()[</code> for a given
	 * <code>IAST</code> instance.
	 * 
	 * @param list
	 * @param start
	 * @throws IndexOutOfBoundsException
	 *             if <code>start</code> isn't valid.
	 */
	public ASTRange(final IAST list, final int start) {
		super(list, start);
	}

	/**
	 * ASTRange <code>[0..list.size()[</code>. This range includes all arguments
	 * of a function. The <code>head</code> of the function is stored at index 0
	 * 
	 * @param list
	 */
	public ASTRange(final IAST list) {
		super(list, 0);
	}

	/**
	 * Append this ranges elements to a new created List
	 * 
	 * @return a new created List
	 */
	public List<IExpr> toList() {
		ArrayList<IExpr> list = new ArrayList<IExpr>();
		return super.toList(list);
	}

}
