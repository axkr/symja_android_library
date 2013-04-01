package org.matheclipse.core.expression;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.generic.nested.NestedAlgorithms;

/**
 * Interface for nested list. I.e. for a list which contains elements of type
 * IElement lists
 */
public class ASTCopy extends NestedAlgorithms<IExpr, IAST> {

	final private Class<IAST> fType;

	public ASTCopy(Class<IAST> type) {
		fType = type;
	}

	public IAST clone(IAST list) {
		return list.clone();
	}

	/**
	 * Create a copy of this <code>IAST</code>, which only contains the head
	 * element of the list (i.e. the element with index 0).
	 */
	public IAST newInstance(IAST list) {
		return list.copyHead();
	}

	public boolean isInstance(IExpr object) {
		return fType.isInstance(object);
	}

}
