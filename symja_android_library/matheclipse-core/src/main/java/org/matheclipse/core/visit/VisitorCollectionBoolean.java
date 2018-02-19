package org.matheclipse.core.visit;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

import java.util.Collection;
import com.duy.lambda.Consumer;

abstract public class VisitorCollectionBoolean<T extends IExpr> extends AbstractVisitorBoolean {
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

	@Override
	public boolean visit(IAST list) {
		int size = list.size();
		list.forEach(fHeadOffset, size, new Consumer<IExpr>() {
            @Override
            public void accept(IExpr x) {
                x.accept(VisitorCollectionBoolean.this);
            }
        });
		return false;
	}
}