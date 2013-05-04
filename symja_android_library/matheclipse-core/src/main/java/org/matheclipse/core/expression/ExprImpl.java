package org.matheclipse.core.expression;

import java.util.List;
import java.util.Map;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.core.visit.VisitorReplaceAll;
import org.matheclipse.core.visit.VisitorReplacePart;
import org.matheclipse.core.visit.VisitorReplaceSlots;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

import edu.jas.structure.ElemFactory;

/**
 * Abstract base class for atomic expression objects.
 * 
 */
@SuppressWarnings("serial")
public abstract class ExprImpl implements IExpr {

	public IExpr opposite() {
		if (this.isNumber()) {
			return F.eval(F.Times(F.CN1, this));
		}
		return F.Times(F.CN1, this);
	}

	/**
	 * Additional negative method, which works like opposite to fulfill groovy's
	 * method signature
	 * 
	 * @return
	 */
	public final IExpr negative() {
		return opposite();
	}

	public IExpr minus(final IExpr that) {
		if (this.isNumber() && that.isNumber()) {
			return F.eval(F.Plus(this, ((INumber) that).opposite()));
		}
		if (that.isNumber()) {
			return F.Plus(this, ((INumber) that).opposite());
		}
		return F.Plus(this, F.Times(F.CN1, that));
	}

	public IExpr plus(final IExpr that) {
		if (this.isNumber() && that.isNumber()) {
			return F.eval(F.Plus(this, that));
		}
		return F.Plus(this, that);
	}

	public IExpr inverse() {
		if (this.isNumber()) {
			return F.eval(F.Power(this, F.CN1));
		}
		return F.Power(this, F.CN1);
	}

	public IExpr times(final IExpr that) {
		if (this.isNumber() && that.isNumber()) {
			return F.eval(F.Times(this, that));
		}
		return F.Times(this, that);
	}

	/**
	 * Additional multiply method which works like times to fulfill groovy's
	 * method signature
	 * 
	 * @param that
	 * @return
	 */
	public final IExpr multiply(final IExpr that) {
		return times(that);
	}

	public final IExpr power(final Integer n) {
		if (this.isNumber()) {
			return F.eval(F.Power(this, F.integer(n)));
		}
		return F.Power(this, F.integer(n));
	}

	public final IExpr power(final IExpr that) {
		if (this.isNumber() && that.isNumber()) {
			return F.eval(F.Power(this, that));
		}
		return F.Power(this, that);
	}

	public IExpr div(final IExpr that) {
		if (that.isNumber()) {
			return F.eval(F.Times(this, that.inverse()));
		}
		return F.eval(F.Times(this, F.Power(that, F.CN1)));
	}

	public IExpr mod(final IExpr that) {
		return F.Mod(this, that);
	}

	public IExpr and(final IExpr that) {
		return F.And(this, that);
	}

	public IExpr or(final IExpr that) {
		return F.Or(this, that);
	}

	public IExpr getAt(final int index) {
		return F.Part(this, F.integer(index));
	}

	@SuppressWarnings("unchecked")
	public Object asType(Class clazz) {
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
			if (this instanceof IntegerSym) {
				return new java.math.BigInteger(((IntegerSym) this).toByteArray());
			}
		} else if (clazz.equals(String.class)) {
			return toString();
		}
		throw new UnsupportedOperationException("ExprImpl.asType() - cast not supported.");
	}

	public abstract ISymbol head();

	public ISymbol topHead() {
		return head();
	}

	public boolean isList() {
		return false;
	}

	public boolean isSequence() {
		return false;
	}

	public boolean isListOfLists() {
		return false;
	}

	public boolean isTrue() {
		return false;
	}

	public boolean isFalse() {
		return false;
	}

	public boolean isSame(IExpr expression) {
		return isSame(expression, Config.DOUBLE_EPSILON);
	}

	public boolean isSame(IExpr expression, double epsilon) {
		return equals(expression);
	}

	public int[] isMatrix() {
		// default: no matrix
		return null;
	}

	public int isVector() {
		// default: no vector
		return -1;
	}

	/** {@inheritDoc} */
	public boolean isAST() {
		return false;
	}

	/** {@inheritDoc} */
	public boolean isOrderlessAST() {
		return false;
	}

	/** {@inheritDoc} */
	public boolean isFlatAST() {
		return false;
	}

	/** {@inheritDoc} */
	public boolean isAST(final IExpr header) {
		return false;
	}

	/** {@inheritDoc} */
	public boolean isAST(final IExpr header, final int sz) {
		return false;
	}

	/** {@inheritDoc} */
	public boolean isASTSizeGE(final IExpr header, final int length) {
		return false;
	}

	/** {@inheritDoc} */
	public boolean isAST(final String symbol) {
		return false;
	}

	/** {@inheritDoc} */
	public boolean isAST(final String symbol, final int length) {
		return false;
	}

	public boolean isPlus() {
		return false;
	}

	public boolean isPower() {
		return false;
	}

	/** {@inheritDoc} */
	public boolean isRuleAST() {
		return false;
	}

	public boolean isTimes() {
		return false;
	}

	public boolean isSin() {
		return false;
	}

	public boolean isCos() {
		return false;
	}

	public boolean isTan() {
		return false;
	}

	public boolean isArcSin() {
		return false;
	}

	public boolean isAnd() {
		return false;
	}

	public boolean isArcCos() {
		return false;
	}

	public boolean isArcTan() {
		return false;
	}

	public boolean isSinh() {
		return false;
	}

	public boolean isCosh() {
		return false;
	}

	public boolean isTanh() {
		return false;
	}

	public boolean isArcSinh() {
		return false;
	}

	public boolean isArcCosh() {
		return false;
	}

	public boolean isArcTanh() {
		return false;
	}

	public boolean isLog() {
		return false;
	}

	public boolean isOne() {
		return false;
	}

	/** {@inheritDoc} */
	public boolean isOr() {
		return false;
	}

	public boolean isMinusOne() {
		return false;
	}

	public boolean isZero() {
		return false;
	}

	public boolean isSlot() {
		return false;
	}

	public boolean isSlotSequence() {
		return false;
	}

	public boolean isFree(final IExpr pattern, boolean heads) {
		final PatternMatcher matcher = new PatternMatcher(pattern);
		return !matcher.apply(this);
	}

	public boolean isFree(Predicate<IExpr> predicate, boolean heads) {
		return !predicate.apply(this);
	}

	public boolean isMember(Predicate<IExpr> predicate, boolean heads) {
		return predicate.apply(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isFunction() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isPattern() {
		return this instanceof IPattern;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isPatternSequence() {
		return this instanceof IPatternSequence;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isCondition() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isModule() {
		return false;
	}

	public boolean isSymbol() {
		return this instanceof ISymbol;
	}

	public boolean isComplex() {
		return this instanceof IComplex;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isFraction() {
		return this instanceof IFraction;
	}

	/** {@inheritDoc} */
	public boolean isInteger() {
		return this instanceof IInteger;
	}

	/** {@inheritDoc} */
	public boolean isNumIntValue() {
		return false;
	}

	/** {@inheritDoc} */
	public boolean isRational() {
		return this instanceof IRational;
	}

	/** {@inheritDoc} */
	public boolean isSignedNumber() {
		return this instanceof ISignedNumber;
	}

	/** {@inheritDoc} */
	public boolean isNot() {
		return false;
	}

	/** {@inheritDoc} */
	public boolean isNumeric() {
		return this instanceof INum || this instanceof IComplexNum;
	}

	/** {@inheritDoc} */
	public boolean isNumber() {
		return this instanceof INumber;
	}

	/** {@inheritDoc} */
	public boolean isLTOrdered(final IExpr obj) {
		return compareTo(obj) < 0;
	}

	/** {@inheritDoc} */
	public boolean isLEOrdered(final IExpr obj) {
		return compareTo(obj) <= 0;
	}

	/** {@inheritDoc} */
	public boolean isGTOrdered(final IExpr obj) {
		return compareTo(obj) > 0;
	}

	/** {@inheritDoc} */
	public boolean isGEOrdered(final IExpr obj) {
		return compareTo(obj) >= 0;
	}

	public boolean isAtom() {
		return true;
	}

	// public INestedList castTo() {
	// return null;
	// }

	public IExpr variables2Slots(final Map<IExpr, IExpr> map, final List<IExpr> variableList) {
		return this;
	}

	// public IExpr save() {
	// return (IExpr) super.export();
	// }
	//
	// public IExpr saveHeap() {
	// return (IExpr) super.moveHeap();
	// }

	public String fullFormString() {
		return toString();
	}

	public String internalFormString(boolean symbolsAsFactoryMethod, int depth) {
		return toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IExpr> leaves() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public IExpr apply(List<? extends IExpr> leaves) {
		final IAST ast = F.ast(head());
		for (int i = 0; i < leaves.size(); i++) {
			ast.add(leaves.get(i));
		}
		return ast;
	}

	/**
	 * {@inheritDoc}
	 */
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
	public IExpr replaceAll(final IAST astRules) {
		return this.accept(new VisitorReplaceAll(astRules));
	}

	/**
	 * {@inheritDoc}
	 */
	public IExpr replaceAll(final Function<IExpr, IExpr> function) {
		return this.accept(new VisitorReplaceAll(function));
	}

	/**
	 * {@inheritDoc}
	 */
	public IExpr replacePart(final IAST astRules) {
		return this.accept(new VisitorReplacePart(astRules));
	}

	/**
	 * {@inheritDoc}
	 */
	public IExpr replaceRepeated(final IAST astRules) {
		return replaceRepeated(this, new VisitorReplaceAll(astRules));
	}

	/**
	 * {@inheritDoc}
	 */
	public IExpr replaceSlots(final IAST astSlots) {
		return this.accept(new VisitorReplaceSlots(astSlots));
	}

	/**
	 * {@inheritDoc}
	 */
	public IExpr replaceRepeated(final Function<IExpr, IExpr> function) {
		return replaceRepeated(this, new VisitorReplaceAll(function));
	}

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
	public IExpr[] egcd(IExpr b) {
		throw new UnsupportedOperationException(toString());
	}

	@Override
	public IExpr evaluate(EvalEngine engine) {
		return null;
	}

	@Override
	public IExpr gcd(IExpr b) {
		throw new UnsupportedOperationException(toString());
	}

	@Override
	public IExpr abs() {
		if (this instanceof INumber) {
			return ((INumber) this).eabs();
		}
		throw new UnsupportedOperationException(toString());
	}

	@Override
	public boolean isZERO() {
		return isZero();
	}

	/**
	 * Signum functionality is used in JAS toString() method, don't use it as math
	 * signum function.
	 * 
	 * @deprecated
	 */
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
	public IExpr subtract(IExpr that) {
		return this.plus(that.negate());
	}

	@Override
	public IExpr sum(IExpr that) {
		return this.plus(that);
	}

	@Override
	public ElemFactory<IExpr> factory() {
		throw new UnsupportedOperationException(toString());
	}

	@Override
	public String toScript() {
		return toString();
	}

	@Override
	public String toScriptFactory() {
		throw new UnsupportedOperationException(toString());
	}

	@Override
	public IExpr divide(IExpr that) {
		return this.div(that);
	}

	@Override
	public boolean isONE() {
		return isOne();
	}

	@Override
	public boolean isUnit() {
		return isOne();
	}

	@Override
	public IExpr remainder(IExpr S) {
		throw new UnsupportedOperationException(toString());
	}

	@Override
	public IExpr negate() {
		throw new UnsupportedOperationException(toString());
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

}
