package org.matheclipse.core.expression;

import org.hipparchus.complex.Complex;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.VisitorReplaceAll;
import org.matheclipse.core.visit.VisitorReplacePart;
import org.matheclipse.core.visit.VisitorReplaceSlots;

import edu.jas.structure.ElemFactory;

/**
 * Abstract base class for atomic expression objects.
 */
public abstract class ExprImpl implements IExpr, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3346614106664542983L;

	@Override
	public IExpr and(final IExpr that) {
		return F.And(this, that);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IExpr apply(IExpr... leaves) {
		final IAST ast = F.ast(head());
		for (int i = 0; i < leaves.length; i++) {
			ast.append(leaves[i]);
		}
		return ast;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IExpr apply(List<? extends IExpr> leaves) {
		final IAST ast = F.ast(head());
		for (int i = 0; i < leaves.size(); i++) {
			ast.append(leaves.get(i));
		}
		return ast;
	}

	@Override
	public Object asType(Class<?> clazz) {
		if (clazz.equals(Boolean.class)) {
			if (this.equals(F.True)) {
				return Boolean.TRUE;
			}
			if (this.equals(F.False)) {
				return Boolean.FALSE;
			}
		} else if (clazz.equals(Integer.class)) {
			if (isSignedNumber()) {
				try {
					return Integer.valueOf(((ISignedNumber) this).toInt());
				} catch (final ArithmeticException e) {
				}
			}
		} else if (clazz.equals(java.math.BigInteger.class)) {
			if (this instanceof IInteger) {
				return new java.math.BigInteger(((IInteger) this).toByteArray());
			}
		} else if (clazz.equals(String.class)) {
			return toString();
		}
		throw new UnsupportedOperationException("ExprImpl.asType() - cast not supported.");
	}

	@Override
	public int compareTo(IExpr expr) {
		if (expr.isAST()) {
			if (!expr.isDirectedInfinity()) {
				return -1 * expr.compareTo(this);
			}
		}
		int x = hierarchy();
		int y = expr.hierarchy();
		return (x < y) ? -1 : ((x == y) ? 0 : 1);
	}

	@Override
	public IExpr copy() {
		try {
			return (IExpr) clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public INumber conjugate() {
		if (isSignedNumber()) {
			return ((INumber) this);
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public double evalDouble() {
		if (isSignedNumber()) {
			return ((ISignedNumber) this).doubleValue();
		}
		throw new WrongArgumentType(this, "Conversion into a double numeric value is not possible!");
	}

	/** {@inheritDoc} */
	@Override
	public Complex evalComplex() {
		if (isNumber()) {
			return ((INumber) this).complexNumValue().complexValue();
		}
		throw new WrongArgumentType(this, "Conversion into a complex numeric value is not possible!");
	}

	/** {@inheritDoc} */
	@Override
	public INumber evalNumber() {
		if (isNumber()) {
			return (INumber) this;
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public ISignedNumber evalSignedNumber() {
		if (isSignedNumber()) {
			return (ISignedNumber) this;
		}
		return null;
	}

	@Override
	public ElemFactory<IExpr> factory() {
		throw new UnsupportedOperationException(toString());
	}

	@Override
	public String fullFormString() {
		return toString();
	}

	/** {@inheritDoc} */
	@Override
	public IExpr getAt(final int index) {
		return F.Part(this, F.integer(index));
	}

	/** {@inheritDoc} */
	@Override
	public String internalFormString(boolean symbolsAsFactoryMethod, int depth) {
		return toString();
	}

	/** {@inheritDoc} */
	@Override
	public String internalScalaString(boolean symbolsAsFactoryMethod, int depth) {
		return toString();
	}

	/** {@inheritDoc} */
	@Override
	public String internalJavaString(boolean symbolsAsFactoryMethod, int depth, boolean useOperators) {
		return toString();
	}

	/** {@inheritDoc} */
	@Override
	public final long leafCount() {
		return isAtom() ? 1L : 0L;
	}

	/** {@inheritDoc} */
	@Override
	public final IExpr or(final IExpr that) {
		return F.Or(this, that);
	}

}
