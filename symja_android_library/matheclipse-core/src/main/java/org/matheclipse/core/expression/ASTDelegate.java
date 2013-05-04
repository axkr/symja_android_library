package org.matheclipse.core.expression;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public abstract class ASTDelegate {

	protected IAST fAst;

	protected ASTDelegate() {
		this(null);
	}

	public ASTDelegate(IAST ast) {
		super();
		fAst = ast;
	}

	/**
	 * Create a new AST List instance
	 * 
	 * @param size
	 *          the initial number of elements
	 * @return a new AST List instance
	 */
	protected AST createAST(int size) {
		return (AST) F.ast(F.List, size, false);
	}

	protected AST createAST(int[] values) {
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
