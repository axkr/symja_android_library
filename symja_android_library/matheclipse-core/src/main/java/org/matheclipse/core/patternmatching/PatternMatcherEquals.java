package org.matheclipse.core.patternmatching;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.interfaces.ISymbol.RuleType;

/**
 * Matches a given expression by simply comparing the left-hand-side expression of this pattern matcher with the
 * <code>equals()</code> method.
 * 
 */
public class PatternMatcherEquals extends IPatternMatcher implements Externalizable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3566534441225675728L;

	/**
	 * Contains the "pattern-matching" expression
	 * 
	 */
	// protected IExpr fLhsPatternExpr;

	protected IExpr fRightHandSide;

	/**
	 * Contains the "set" symbol used to define this pattern matcher
	 * 
	 */
	private ISymbol.RuleType fSetSymbol;

	/**
	 * Public constructor for serialization.
	 */
	public PatternMatcherEquals() {
		
	}
	/**
	 * 
	 * @param setSymbol
	 *            the symbol which defines this pattern-matching rule (i.e. Set, SetDelayed,...)
	 * @param leftHandSide
	 *            could contain pattern expressions for "pattern-matching"
	 * @param rightHandSide
	 *            the result which should be evaluated if the "pattern-matching" succeeds
	 */
	public PatternMatcherEquals(final ISymbol.RuleType setSymbol, final IExpr leftHandSide, final IExpr rightHandSide) {
		super(leftHandSide);
		fSetSymbol = setSymbol;
		fRightHandSide = rightHandSide;
	}

	@Override
	public boolean apply(IExpr lhsEvalExpr) {
		return fLhsPatternExpr.equals(lhsEvalExpr);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		PatternMatcherEquals v = (PatternMatcherEquals) super.clone();
		v.fRightHandSide = fRightHandSide;
		v.fSetSymbol = fSetSymbol;
		return v;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PatternMatcherEquals) {
			return fLhsPatternExpr.equals(((PatternMatcherEquals) obj).fLhsPatternExpr);
		}
		return super.equals(obj);
	}

	@Override
	public IExpr eval(IExpr lhsEvalExpr) {
		if (apply(lhsEvalExpr)) {
			return fRightHandSide;
		}
		return null;
	}

	@Override
	public void getPatterns(List<IExpr> resultList, IExpr patternExpr) {
	}

	/** {@inheritDoc} */
	@Override
	public IExpr getRHS() {
		return fRightHandSide;
	}

	/**
	 * Return <code>Set</code> or <code>SetDelayed</code> symbol.
	 * 
	 * @return <code>null</code> if no symbol was defined
	 */
	public ISymbol getSetSymbol() {
		if (fSetSymbol == ISymbol.RuleType.SET_DELAYED) {
			return F.SetDelayed;
		}
		if (fSetSymbol == ISymbol.RuleType.SET) {
			return F.Set;
		}
		if (fSetSymbol == ISymbol.RuleType.UPSET_DELAYED) {
			return F.UpSetDelayed;
		}
		if (fSetSymbol == ISymbol.RuleType.UPSET) {
			return F.UpSet;
		}
		return null;
	}

	@Override
	public int hashCode() {
		return fLhsPatternExpr.hashCode();
	}

	@Override
	public boolean isRuleWithoutPatterns() {
		return true;
	}

	public void setRHS(IExpr rightHandSide) {
		fRightHandSide = rightHandSide;
	}

	@Override
	public int compareTo(IPatternMatcher o) {
		if (getPriority() < o.getPriority()) {
			return -1;
		}
		if (getPriority() > o.getPriority()) {
			return 1;
		}
		return 0;
	}

	@Override
	public int getPriority() {
		return 0;
	}

	public IAST getAsAST() {
		ISymbol setSymbol;
		IAST ast;
		setSymbol = getSetSymbol();
		ast = F.ast(setSymbol);
		ast.add(fLhsPatternExpr);
		ast.add(getRHS());
		return ast;
	}

	@Override
	public String toString() {
		return getAsAST().toString();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeShort(fSetSymbol.ordinal());
		objectOutput.writeObject(fLhsPatternExpr);
		objectOutput.writeObject(fRightHandSide);
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
		fSetSymbol = RuleType.values()[objectInput.readShort()];
		fLhsPatternExpr = (IExpr) objectInput.readObject();
		fRightHandSide = (IExpr) objectInput.readObject();
	}
}
