package org.matheclipse.core.expression;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.math4.complex.Complex;
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

	public static IExpr replaceRepeated(final IExpr expr, VisitorReplaceAll visitor) {
		IExpr result = expr;
		IExpr temp = expr.accept(visitor);
		final int iterationLimit = EvalEngine.get().getIterationLimit();
		int iterationCounter = 1;
		while (temp.isPresent()) {
			result = temp;
			temp = result.accept(visitor);
			if (iterationLimit >= 0 && iterationLimit <= ++iterationCounter) {
				IterationLimitExceeded.throwIt(iterationCounter, result);
			}
		}
		return result;
	}

	@Override
	public IExpr abs() {
		if (this instanceof INumber) {
			return ((INumber) this).eabs();
		}
		throw new UnsupportedOperationException(toString());
	}

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
			ast.add(leaves[i]);
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
			ast.add(leaves.get(i));
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
	public IExpr divide(IExpr that) {
		if (isNumber() && that.isNumber()) {
			if (that.isOne()) {
				return this;
			}
			return times(that.inverse());
		}
		return F.eval(F.Times(this, F.Power(that, F.CN1)));
	}

	@Override
	public IExpr[] egcd(IExpr b) {
		throw new UnsupportedOperationException(toString());
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

	@Override
	public IExpr gcd(IExpr that) {
		throw new UnsupportedOperationException("gcd(" + toString() + ", " + that.toString() + ")");
		// if (equals(that)) {
		// return that;
		// }
		// return F.C1;
	}

	/** {@inheritDoc} */
	@Override
	public IExpr getAt(final int index) {
		return F.Part(this, F.integer(index));
	}

	/** {@inheritDoc} */
	@Override
	public abstract ISymbol head();

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
	public IExpr inverse() {
		return power(F.CN1);
	}

	/** {@inheritDoc} */
	public final long leafCount() {
		return isAtom() ? 1L : 0L;
	}

	/** {@inheritDoc} */
	@Override
	public final List<IExpr> leaves() {
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public final IExpr minus(final IExpr that) {
		if (this.isNumber() && that.isNumber()) {
			return F.eval(F.Plus(this, ((INumber) that).opposite()));
		}
		if (that.isNumber()) {
			return F.eval(F.Plus(this, ((INumber) that).opposite()));
		}
		return F.eval(F.Plus(this, F.Times(F.CN1, that)));
	}

	@Override
	public final IExpr mod(final IExpr that) {
		return F.Mod(this, that);
	}

	/** {@inheritDoc} */
	@Override
	public final IExpr multiply(final IExpr that) {
		return times(that);
	}

	/** {@inheritDoc} */
	@Override
	public IExpr negate() {
		return opposite();
	}

	/** {@inheritDoc} */
	@Override
	public final IExpr negative() {
		return opposite();
	}

	/** {@inheritDoc} */
	public IExpr opposite() {
		return times(F.CN1);
	}

	/** {@inheritDoc} */
	public final IExpr optional(final IExpr that) {
		if (that != null) {
			return that;
		}
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public final IExpr or(final IExpr that) {
		return F.Or(this, that);
	}

	/** {@inheritDoc} */
	@Override
	public IExpr plus(final IExpr that) {
		return F.eval(F.Plus(this, that));
	}

	/** {@inheritDoc} */
	@Override
	public final IExpr inc() {
		return plus(F.C1);
	}

	/** {@inheritDoc} */
	@Override
	public final IExpr dec() {
		return plus(F.CN1);
	}

	/** {@inheritDoc} */
	@Override
	public final IExpr power(final IExpr that) {
		if (that.isZero()) {
			if (!this.isZero()) {
				return F.C1;
			}
		} else if (that.isOne()) {
			return this;
		}
		if (this.isNumber() && that.isNumber()) {
			return F.eval(F.Power(this, that));
		}
		return F.Power(this, that);
	}

	/** {@inheritDoc} */
	@Override
	public IExpr power(final long n) {
		if (n == 0L) {
			if (!this.isZero()) {
				return F.C1;
			}
			// don't return F.Indeterminate here! The evaluation of F.Power()
			// returns Indeterminate
			return F.Power(this, F.C0);
		} else if (n == 1L) {
			return this;
		} else if (this.isNumber()) {
			long exp = n;
			if (n < 0) {
				exp *= -1;
			}
			int b2pow = 0;

			while ((exp & 1) == 0) {
				b2pow++;
				exp >>= 1;
			}

			INumber r = (INumber) this;
			INumber x = r;

			while ((exp >>= 1) > 0) {
				x = (INumber) x.multiply(x);
				if ((exp & 1) != 0) {
					r = (INumber) r.multiply(x);
				}
			}

			while (b2pow-- > 0) {
				r = (INumber) r.multiply(r);
			}
			if (n < 0) {
				return r.inverse();
			}
			return r;
		}
		return F.Power(this, F.integer(n));
	}

	@Override
	public IExpr remainder(IExpr that) {
		throw new UnsupportedOperationException(toString());
		// if (equals(that)) {
		// return F.C0;
		// }
		// return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final IExpr replaceAll(final Function<IExpr, IExpr> function) {
		return this.accept(new VisitorReplaceAll(function));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final IExpr replaceAll(final IAST astRules) {
		return this.accept(new VisitorReplaceAll(astRules));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final IExpr replacePart(final IAST astRules) {
		return this.accept(new VisitorReplacePart(astRules));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final IExpr replaceRepeated(final Function<IExpr, IExpr> function) {
		return replaceRepeated(this, new VisitorReplaceAll(function));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final IExpr replaceRepeated(final IAST astRules) {
		return replaceRepeated(this, new VisitorReplaceAll(astRules));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final IExpr replaceSlots(final IAST astSlots) {
		return this.accept(new VisitorReplaceSlots(astSlots));
	}

	/**
	 * Signum functionality is used in JAS toString() method, don't use it as
	 * math signum function.
	 * 
	 * @deprecated
	 */
	@Deprecated
	@Override
	public int signum() {
		if (isZERO()) {
			return 0;
		}
		if (isSignedNumber()) {
			return ((ISignedNumber) this).sign();
		}
		return 1;
	}

	@Override
	public final IExpr subtract(IExpr that) {
		return plus(that.negate());
	}

	@Override
	public final IExpr sum(IExpr that) {
		return this.plus(that);
	}

	/** {@inheritDoc} */
	@Override
	public IExpr times(final IExpr that) {
		return F.eval(F.Times(this, that));
	}

	@Override
	public final ISymbol topHead() {
		return head();
	}

	@Override
	public final String toScript() {
		return toString();
	}

	@Override
	public final String toScriptFactory() {
		throw new UnsupportedOperationException(toString());
	}

	@Override
	public IExpr variables2Slots(final Map<IExpr, IExpr> map, final List<IExpr> variableList) {
		return this;
	}

	@Override
	public final IExpr $div(final IExpr that) {
		return divide(that);
	}

	@Override
	public final IExpr $minus(final IExpr that) {
		return minus(that);
	}

	@Override
	public final IExpr $plus(final IExpr that) {
		return plus(that);
	}

	@Override
	public final IExpr $times(final IExpr that) {
		return times(that);
	}

	@Override
	public final IExpr $up(final IExpr that) {
		return power(that);
	}
}
