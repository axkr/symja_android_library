package org.matheclipse.core.visit;

import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;

public abstract class AbstractVisitorLong implements IVisitorLong {

	public AbstractVisitorLong() {
		super();
	}

	@Override
	public long visit(IInteger element) {
		return 1;
	}

	@Override
	public long visit(IFraction element) {
		return 1;
	}

	@Override
	public long visit(IComplex element) {
		return 1;
	}

	@Override
	public long visit(INum element) {
		return 1;
	}

	@Override
	public long visit(IComplexNum element) {
		return 1;
	}

	@Override
	public long visit(ISymbol element) {
		return 1;
	}

	@Override
	public long visit(IPattern element) {
		return 1;
	}

	@Override
	public long visit(IPatternSequence element) {
		return 1;
	}

	@Override
	public long visit(IStringX element) {
		return 1;
	}

}