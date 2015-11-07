package org.matheclipse.core.visit;
 
import java.util.function.Function;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;

public class VisitorFunction<T> extends AbstractVisitor<T> {
	int fHeadOffset;

	final Function<IExpr,T> fFunction;

	public VisitorFunction(final Function<IExpr,T> function) {
		this(1, function);
	}

	public VisitorFunction(int hOffset, final Function<IExpr,T> function) {
		fHeadOffset = hOffset;
		fFunction = function;
	}

	public T visit(IInteger element) {
		return fFunction.apply(element);
	}

	public T visit(IFraction element) {
		return fFunction.apply(element);
	}

	public T visit(IComplex element) {
		return fFunction.apply(element);
	}

	public T visit(INum element) {
		return fFunction.apply(element);
	}

	public T visit(IComplexNum element) {
		return fFunction.apply(element);
	}

	public T visit(ISymbol element) {
		return fFunction.apply(element);
	}

	public T visit(IPattern element) {
		return fFunction.apply(element);
	}

	public T visit(IStringX element) {
		return fFunction.apply(element);
	}

	public T visit(IAST list) {
		T temp;
		for (int i = fHeadOffset; i < list.size(); i++) {
			temp = list.get(i).accept(this);
			if (temp!=null) {
				return temp;
			}
		}
		return null;
	}
}