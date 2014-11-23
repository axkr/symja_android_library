package org.matheclipse.core.visit;

import java.util.Collection;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class VisitorCollectionBoolean extends AbstractVisitorBoolean {
	protected int fHeadOffset;

	protected Collection<IExpr> fCollection;

	public VisitorCollectionBoolean(int hOffset, Collection<IExpr> collection) {
		super();
		fHeadOffset = hOffset;
		fCollection = collection;
	}

	public boolean visit(IAST list) {
		for (int i = fHeadOffset; i < list.size(); i++) {
			list.get(i).accept(this);
		}
		return false;
	}
}