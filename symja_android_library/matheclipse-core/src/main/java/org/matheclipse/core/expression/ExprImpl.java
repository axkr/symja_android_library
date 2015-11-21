package org.matheclipse.core.expression;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.math4.complex.Complex;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.util.AbstractAssumptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcher;
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
		while (temp != null) {
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
			if (this instanceof AbstractIntegerSym) {
				return new java.math.BigInteger(((AbstractIntegerSym) this).toByteArray());
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
	public IExpr evaluate(EvalEngine engine) {
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
	@Override
	public final boolean isAnd() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isArcCos() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isArcCosh() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isArcSin() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isArcSinh() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isArcTan() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isArcTanh() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isAST() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isAST(final IExpr header) {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isAST(final IExpr header, final int length) {
		return false;
	}

	@Override
	public final boolean isAST(IExpr header, int minLength, int maxLength) {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isAST(IExpr header, int length, IExpr... args) {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isAST(final String symbol) {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isAST(final String symbol, final int length) {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isASTSizeGE(final IExpr header, final int length) {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isAtom() {
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isBlank() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isComplex() {
		return this instanceof IComplex;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isComplexInfinity() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isComplexNumeric() {
		return this instanceof IComplexNum;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isCondition() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isConstant() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isCos() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isCosh() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final IAST[] isDerivative() {
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isDirectedInfinity() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isE() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isExpanded() {
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isAllExpanded() {
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isPlusTimesPower() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isFalse() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isFlatAST() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isFraction() {
		return this instanceof IFraction;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isFree(final IExpr pattern) {
		return isFree(pattern, true);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isFree(final IExpr pattern, boolean heads) {
		final IPatternMatcher matcher = new PatternMatcher(pattern);
		return !matcher.test(this);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isFree(Predicate<IExpr> predicate, boolean heads) {
		return !predicate.test(this);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isFreeAST(final IExpr pattern) {
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isFreeAST(Predicate<IExpr> predicate) {
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isFreeOfPatterns() {
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isFunction() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isGEOrdered(final IExpr obj) {
		return compareTo(obj) >= 0;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isGTOrdered(final IExpr obj) {
		return compareTo(obj) > 0;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isIndeterminate() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isInfinity() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isInteger() {
		return this instanceof IInteger;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isIntegerResult() {
		if (F.True.equals(AbstractAssumptions.assumeInteger(this))) {
			return true;
		}
		return this instanceof IInteger;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isRationalResult() {
		if (F.True.equals(AbstractAssumptions.assumeRational(this))) {
			return true;
		}
		return this instanceof IRational;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isRealResult() {
		if (F.True.equals(AbstractAssumptions.assumeReal(this))) {
			return true;
		}
		return this instanceof ISignedNumber;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isNegativeResult() {
		return AbstractAssumptions.assumeNegative(this);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isPositiveResult() {
		return AbstractAssumptions.assumePositive(this);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isNonNegativeResult() {
		return AbstractAssumptions.assumeNonNegative(this);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isLEOrdered(final IExpr obj) {
		return compareTo(obj) <= 0;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isList() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isListOfLists() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isLog() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isLTOrdered(final IExpr obj) {
		return compareTo(obj) < 0;
	}

	/** {@inheritDoc} */
	@Override
	public final int[] isMatrix() {
		// default: no matrix
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isMember(final IExpr pattern, boolean heads) {
		final IPatternMatcher matcher = new PatternMatcher(pattern);
		return isMember(matcher, heads);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isMember(Predicate<IExpr> predicate, boolean heads) {
		return predicate.test(this);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isMinusOne() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isModule() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isNegative() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isNegativeInfinity() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isNot() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isNumber() {
		return this instanceof INumber;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isNumEqualInteger(IInteger value) throws ArithmeticException {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isNumEqualRational(IRational value) throws ArithmeticException {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isNumeric() {
		return this instanceof INum || this instanceof IComplexNum;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isNumericFunction() {
		return isNumber() || isConstant();
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isNumericMode() {
		return isNumeric();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isNumIntValue() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isOne() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isONE() {
		return isOne();
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isOr() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isOrderlessAST() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isPattern() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isPatternDefault() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isPatternExpr() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isPatternSequence() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isPi() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isPlus() {
		return false;
	}

	/** {@inheritDoc} */
	public boolean isPolynomial(ISymbol variable) {
		return isNumber();
	}

	/** {@inheritDoc} */
	public boolean isPolynomial(IAST variables) {
		return isNumber();
	}

	/** {@inheritDoc} */
	public boolean isPolynomialOfMaxDegree(ISymbol variable, long maxDegree) {
		return isPolynomial(variable);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isPositive() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isPower() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isRational() {
		return this instanceof IRational;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isRuleAST() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isSame(IExpr expression) {
		return isSame(expression, Config.DOUBLE_EPSILON);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isSame(IExpr expression, double epsilon) {
		return equals(expression);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isSequence() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isSignedNumber() {
		return this instanceof ISignedNumber;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isSin() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isSinh() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isSlot() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isSlotSequence() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isSymbol() {
		return this instanceof ISymbol;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isRationalValue(IRational value) {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isTan() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isTanh() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isTimes() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isTrue() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isUnit() {
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isValue() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isVariable() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final int isVector() {
		// default: no vector
		return -1;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isZero() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isZERO() {
		return isZero();
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
