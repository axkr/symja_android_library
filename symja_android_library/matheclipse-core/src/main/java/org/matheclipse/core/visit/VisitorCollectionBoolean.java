package org.matheclipse.core.visit;

import java.util.Collection;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class VisitorCollectionBoolean extends AbstractVisitorBoolean {
	int fHeadOffset;

	private Collection<IExpr> fCollection;

	public VisitorCollectionBoolean(int hOffset, Collection<IExpr> collection) {
		super();
		fHeadOffset = hOffset;
		fCollection = collection;
	}

	public boolean visit(IAST list) {
		for (int i = fHeadOffset; i < list.size(); i++) {
			if (list.get(i).accept(this)) {
				fCollection.add(list.get(i));
			}
		}
		return false;
	}
}