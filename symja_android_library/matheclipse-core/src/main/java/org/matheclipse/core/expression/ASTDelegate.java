package org.matheclipse.core.expression;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * 
 * 
 * @deprecated
 */
@Deprecated
public abstract class ASTDelegate {

	protected IAST fAst;

	/**
	 * @deprecated
	 */
	@Deprecated
	protected ASTDelegate() {
		this(null);
	}

	/**
	 * 
	 * @param ast
	 * @deprecated
	 */
	@Deprecated
	public ASTDelegate(IAST ast) {
		super();
		fAst = ast;
	}

	/**
	 * Create a new AST List instance
	 * 
	 * @param size
	 *            the initial number of elements
	 * @return a new AST List instance
	 */
	protected IAST createAST(int size) {
		return F.ast(F.List, size, false);
	}

	protected IAST createAST(int[] values) {
		return AST.newInstance(F.List, values);
	}

	/**
	 * Get the number of rows (i.e. size()-1)
	 * 
	 * @return
	 */
	public int getRows() {
		return fAst.size() - 1;
	}

	public IExpr getAt(final int index) {
		return fAst.get(index);
	}

	public int size() {
		return fAst.size();
	}

	public String toFullForm() {
		return fAst.fullFormString();
	}

	@Override
	public String toString() {
		return fAst.toString();
	}

	// public Text toText() {
	// return fAst.toText();
	// }

	public IAST getAST() {
		return fAst;
	}
}
