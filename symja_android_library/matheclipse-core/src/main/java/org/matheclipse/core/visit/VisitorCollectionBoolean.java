package org.matheclipse.core.visit;

import java.util.Collection;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class VisitorCollectionBoolean<T extends IExpr> extends AbstractVisitorBoolean {
	protected int fHeadOffset;

	protected Collection<T> fCollection;

	public VisitorCollectionBoolean(Collection<T> collection) {
		super();
		fHeadOffset = 1;
		fCollection = collection;
	}

	public VisitorCollectionBoolean(int hOffset, Collection<T> collection) {
		super();
		fHeadOffset = hOffset;
		fCollection = collection;
	}

	public boolean visit(IAST list) {
		int size=list.size();
		for (int i = fHeadOffset; i < size; i++) {
			list.get(i).accept(this);
		}
		return false;
	}
}