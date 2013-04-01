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

public abstract class AbstractVisitorBoolean implements IVisitorBoolean {

	public AbstractVisitorBoolean() {
		super();
	}

	public boolean visit(IInteger element) {
		return false;
	}

	public boolean visit(IFraction element) {
		return false;
	}

	public boolean visit(IComplex element) {
		return false;
	}

	public boolean visit(INum element) {
		return false;
	}

	public boolean visit(IComplexNum element) {
		return false;
	}

	public boolean visit(ISymbol element) {
		return false;
	}

	public boolean visit(IPattern element) {
		return false;
	}

	public boolean visit(IPatternSequence element) {
		return false;
	}
	
	public boolean visit(IStringX element) {
		return false;
	}

}