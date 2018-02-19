package org.matheclipse.core.visit;

import com.duy.lambda.Predicate;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;

public class VisitorPredicate implements IVisitorBoolean {
	int fHeadOffset;

	final Predicate<IExpr> fMatcher;

	public VisitorPredicate(final Predicate<IExpr> matcher) {
		this(1, matcher);
	}

	public VisitorPredicate(int hOffset, final Predicate<IExpr> matcher) {
		fHeadOffset = hOffset;
		fMatcher = matcher;
	}

	@Override
	public boolean visit(IInteger element) {
		return fMatcher.test(element);
	}

	@Override
	public boolean visit(IFraction element) {
		return fMatcher.test(element);
	}

	@Override
	public boolean visit(IComplex element) {
		return fMatcher.test(element);
	}

	@Override
	public boolean visit(INum element) {
		return fMatcher.test(element);
	}

	@Override
	public boolean visit(IComplexNum element) {
		return fMatcher.test(element);
	}

	@Override
	public boolean visit(ISymbol element) {
		return fMatcher.test(element);
	}

	@Override
	public boolean visit(IPattern element) {
		return fMatcher.test(element);
	}

	@Override
	public boolean visit(IPatternSequence element) {
		return fMatcher.test(element);
	}

	@Override
	public boolean visit(IStringX element) {
		return fMatcher.test(element);
	}

	@Override
	public boolean visit(IAST list) {
		if (fMatcher.test(list)) {
			return true;
		}
		return list.exists(new Predicate<IExpr>() {
            @Override
            public boolean test(IExpr x) {
                return x.accept(VisitorPredicate.this);
            }
        }, fHeadOffset);
	}
}