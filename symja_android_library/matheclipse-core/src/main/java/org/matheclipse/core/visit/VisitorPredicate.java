package org.matheclipse.core.visit;

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

import com.google.common.base.Predicate;

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

	public boolean visit(IInteger element) {
		return fMatcher.apply(element);
	}

	public boolean visit(IFraction element) {
		return fMatcher.apply(element);
	}

	public boolean visit(IComplex element) {
		return fMatcher.apply(element);
	}

	public boolean visit(INum element) {
		return fMatcher.apply(element);
	}

	public boolean visit(IComplexNum element) {
		return fMatcher.apply(element);
	}

	public boolean visit(ISymbol element) {
		return fMatcher.apply(element);
	}

	public boolean visit(IPattern element) {
		return fMatcher.apply(element);
	}

	public boolean visit(IPatternSequence element) {
		return fMatcher.apply(element);
	}
	
	public boolean visit(IStringX element) {
		return fMatcher.apply(element);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.matheclipse.core.expression.IVisitorBoolean#visit(org.matheclipse.core.expression.AST)
	 */
	public boolean visit(IAST list) {
		if (fMatcher.apply(list)) {
			return true;
		}
		for (int i = fHeadOffset; i < list.size(); i++) {
			if (list.get(i).accept(this)) {
				return true;
			}
		}
		return false;
	}
}