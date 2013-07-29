package org.matheclipse.core.patternmatching;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IEvaluationEngine;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.ISymbol;

import com.google.common.collect.ArrayListMultimap;

/**
 * The pattern matching rules associated with a symbol.
 */
public class RulesData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8843909916823779295L;

	// private transient Map<IExpr, Pair<ISymbol, IExpr>> fEqualRules;
	private transient Map<IExpr, PatternMatcherEquals> fEqualDownRules;
	private transient ArrayListMultimap<Integer, IPatternMatcher> fSimplePatternDownRules;
	private transient List<IPatternMatcher> fPatternDownRules;

	private transient Map<IExpr, PatternMatcherEquals> fEqualUpRules;
	private transient ArrayListMultimap<Integer, IPatternMatcher> fSimplePatternUpRules;
	private transient List<IPatternMatcher> fPatternUpRules;

	public RulesData() {
		this.fEqualDownRules = null;
		this.fSimplePatternDownRules = null;
		this.fPatternDownRules = null;
		this.fEqualUpRules = null;
		this.fSimplePatternUpRules = null;
		this.fPatternUpRules = null;
	}

	public void clear() {
		fEqualDownRules = null;
		fSimplePatternDownRules = null;
		fPatternDownRules = null;
		fEqualUpRules = null;
		fSimplePatternUpRules = null;
		fPatternUpRules = null;
	}

	public IExpr evalDownRule(final IExpr expression) {
		return evalDownRule(EvalEngine.get(), expression);
	}

	public IExpr evalDownRule(final IEvaluationEngine ee, final IExpr expression) {
		PatternMatcherEquals res;
		if (fEqualDownRules != null) {
			res = fEqualDownRules.get(expression);
			if (res != null) {
				return res.getRHS();
			}
		}

		try {
			IExpr result;
			IPatternMatcher pmEvaluator;
			if ((fSimplePatternDownRules != null) && (expression instanceof IAST)) {
				final Integer hash = Integer.valueOf(((IAST) expression).patternHashCode());
				final List<IPatternMatcher> list = fSimplePatternDownRules.get(hash);
				if (list != null) {
					for (int i = 0; i < list.size(); i++) {
						pmEvaluator = (IPatternMatcher) list.get(i).clone();
						result = pmEvaluator.eval(expression);
						if (result != null) {
							return result;
						}
					}
				}
			}

			if (fPatternDownRules != null) {
				for (int i = 0; i < fPatternDownRules.size(); i++) {
					pmEvaluator = (IPatternMatcher) fPatternDownRules.get(i).clone();
					result = pmEvaluator.eval(expression);
					if (result != null) {
						return result;
					}
				}
			}
		} catch (CloneNotSupportedException cnse) {
			cnse.printStackTrace();
		}
		return null;
	}

	public IPatternMatcher putDownRule(ISymbol setSymbol, final boolean equalRule, final IExpr leftHandSide,
			final IExpr rightHandSide, final int priority) {
		if (equalRule) {
			fEqualDownRules = getEqualDownRules();
			// fEqualRules.put(leftHandSide, new Pair<ISymbol, IExpr>(setSymbol,
			// rightHandSide));
			PatternMatcherEquals pmEquals = new PatternMatcherEquals(setSymbol, leftHandSide, rightHandSide);
			fEqualDownRules.put(leftHandSide, pmEquals);
			return pmEquals;
		}

		final PatternMatcherAndEvaluator pmEvaluator = new PatternMatcherAndEvaluator(setSymbol, leftHandSide, rightHandSide);

		if (pmEvaluator.isRuleWithoutPatterns()) {
			fEqualDownRules = getEqualDownRules();
			// fEqualRules.put(leftHandSide, new Pair<ISymbol, IExpr>(setSymbol,
			// rightHandSide));
			PatternMatcherEquals pmEquals = new PatternMatcherEquals(setSymbol, leftHandSide, rightHandSide);
			fEqualDownRules.put(leftHandSide, pmEquals);
			return pmEquals;
		}

		// pmEvaluator.setCondition(condition);
		if (!isComplicatedPatternRule(leftHandSide)) {

			fSimplePatternDownRules = getSimplePatternDownRules();
			return addSimplePatternDownRule(leftHandSide, pmEvaluator);

		} else {

			fPatternDownRules = getPatternDownRules();
			if (F.isSystemInitialized) {
				for (int i = 0; i < fPatternDownRules.size(); i++) {
					if (pmEvaluator.equals(fPatternDownRules.get(i))) {
						fPatternDownRules.set(i, pmEvaluator);

						return pmEvaluator;
					}
				}
			}
			fPatternDownRules.add(pmEvaluator);
			return pmEvaluator;

		}

	}

	public IExpr evalUpRule(final IEvaluationEngine ee, final IExpr expression) {
		PatternMatcherEquals res;
		if (fEqualUpRules != null) {
			res = fEqualUpRules.get(expression);
			if (res != null) {
				return res.getRHS();
			}
		}

		try {
			IExpr result;
			IPatternMatcher pmEvaluator;
			if ((fSimplePatternUpRules != null) && (expression.isAST())) {
				final Integer hash = Integer.valueOf(((IAST) expression).patternHashCode());
				final List<IPatternMatcher> list = fSimplePatternUpRules.get(hash);
				if (list != null) {
					for (int i = 0; i < list.size(); i++) {
						pmEvaluator = (IPatternMatcher) list.get(i).clone();
						result = pmEvaluator.eval(expression);
						if (result != null) {
							return result;
						}
					}
				}
			}

			if (fPatternUpRules != null) {
				for (int i = 0; i < fPatternUpRules.size(); i++) {
					pmEvaluator = (IPatternMatcher) fPatternUpRules.get(i).clone();
					result = pmEvaluator.eval(expression);
					if (result != null) {
						return result;
					}
				}
			}
		} catch (CloneNotSupportedException cnse) {
			cnse.printStackTrace();
		}
		return null;
	}

	public IPatternMatcher putUpRule(ISymbol setSymbol, final boolean equalRule, final IAST leftHandSide,
			final IExpr rightHandSide, final int priority) {
		if (equalRule) {
			fEqualUpRules = getEqualUpRules();
			PatternMatcherEquals pmEquals = new PatternMatcherEquals(setSymbol, leftHandSide, rightHandSide);
			fEqualUpRules.put(leftHandSide, pmEquals);
			return pmEquals;
		}

		final PatternMatcherAndEvaluator pmEvaluator = new PatternMatcherAndEvaluator(setSymbol, leftHandSide, rightHandSide);

		if (pmEvaluator.isRuleWithoutPatterns()) {
			fEqualUpRules = getEqualUpRules();
			PatternMatcherEquals pmEquals = new PatternMatcherEquals(setSymbol, leftHandSide, rightHandSide);
			fEqualUpRules.put(leftHandSide, pmEquals);
			return pmEquals;
		}

		if (!isComplicatedPatternRule(leftHandSide)) {
			fSimplePatternUpRules = getSimplePatternUpRules();
			return addSimplePatternUpRule(leftHandSide, pmEvaluator);
		} else {

			fPatternUpRules = getPatternUpRules();
			if (F.isSystemInitialized) {
				for (int i = 0; i < fPatternUpRules.size(); i++) {
					if (pmEvaluator.equals(fPatternUpRules.get(i))) {
						fPatternUpRules.set(i, pmEvaluator);
						return pmEvaluator;
					}
				}
			}
			fPatternUpRules.add(pmEvaluator);
			return pmEvaluator;

		}

	}

	/**
	 * Create a pattern hash value for the left-hand-side expression and insert
	 * the left-hand-side as a simple pattern rule to the
	 * <code>fSimplePatternRules</code>.
	 * 
	 * @param leftHandSide
	 * @param pmEvaluator
	 * @return
	 */
	private PatternMatcher addSimplePatternDownRule(final IExpr leftHandSide, final PatternMatcher pmEvaluator) {
		final Integer hash = Integer.valueOf(((IAST) leftHandSide).patternHashCode());
		if (F.isSystemInitialized && fSimplePatternDownRules.containsEntry(hash, pmEvaluator)) {
			fSimplePatternDownRules.remove(hash, pmEvaluator);
		}
		fSimplePatternDownRules.put(hash, pmEvaluator);
		return pmEvaluator;
	}

	/**
	 * Create a pattern hash value for the left-hand-side expression and insert
	 * the left-hand-side as a simple pattern rule to the
	 * <code>fSimplePatternRules</code>.
	 * 
	 * @param leftHandSide
	 * @param pmEvaluator
	 * @return
	 */
	private PatternMatcher addSimplePatternUpRule(final IExpr leftHandSide, final PatternMatcher pmEvaluator) {
		final Integer hash = Integer.valueOf(((IAST) leftHandSide).patternHashCode());
		if (F.isSystemInitialized && fSimplePatternUpRules.containsEntry(hash, pmEvaluator)) {
			fSimplePatternUpRules.remove(hash, pmEvaluator);
		}
		fSimplePatternUpRules.put(hash, pmEvaluator);
		return pmEvaluator;
	}

	/** {@inheritDoc} */
	public PatternMatcher putDownRule(final PatternMatcherAndInvoker pmEvaluator) {
		final IExpr leftHandSide = pmEvaluator.getLHS();
		if (!isComplicatedPatternRule(leftHandSide)) {

			fSimplePatternDownRules = getSimplePatternDownRules();
			return addSimplePatternDownRule(leftHandSide, pmEvaluator);

		} else {

			fPatternDownRules = getPatternDownRules();
			for (int i = 0; i < fPatternDownRules.size(); i++) {
				if (pmEvaluator.equals(fPatternDownRules.get(i))) {
					fPatternDownRules.set(i, pmEvaluator);
					return pmEvaluator;
				}
			}
			fPatternDownRules.add(pmEvaluator);
			return pmEvaluator;
		}
	}

	private boolean isComplicatedPatternRule(final IExpr lhsExpr) {
		if (lhsExpr.isAST()) {
			final IAST lhsAST = ((IAST) lhsExpr);
			if (lhsAST.size() > 1) {
				final int attr = lhsAST.topHead().getAttributes();
				if ((ISymbol.ORDERLESS & attr) == ISymbol.ORDERLESS) {
					return true;
				}
				if (lhsAST.get(1).isAST()) {
					IAST arg1 = (IAST) lhsAST.get(1);
					if (arg1.isCondition()) {
						return true;
					}
					// the left hand side is associated with the first argument
					// see if the first argument is complicated
					for (int i = 2; i < arg1.size(); i++) {
						if (arg1.get(i).isPattern() && ((IPattern) arg1.get(i)).isDefault()) {
							return true;
						}
					}
				} else if (lhsAST.get(1).isPattern()) {
					return true;
				} else if (lhsAST.get(1).isPatternSequence()) {
					return true;
				}
				for (int i = 2; i < lhsAST.size(); i++) {
					if (lhsAST.get(i).isPattern() && ((IPattern) lhsAST.get(i)).isDefault()) {
						return true;
					}
				}
			}
		} else if (lhsExpr.isPattern()) {
			return true;
		} else if (lhsExpr.isPatternSequence()) {
			return true;
		}
		return false;
	}

	/**
	 * @return Returns the equalRules.
	 */
	public Map<IExpr, PatternMatcherEquals> getEqualDownRules() {
		if (fEqualDownRules == null) {
			fEqualDownRules = new HashMap<IExpr, PatternMatcherEquals>();
		}
		return fEqualDownRules;
	}

	private List<IPatternMatcher> getPatternDownRules() {
		if (fPatternDownRules == null) {
			fPatternDownRules = new ArrayList<IPatternMatcher>();
		}
		return fPatternDownRules;
	}

	private ArrayListMultimap<Integer, IPatternMatcher> getSimplePatternDownRules() {
		if (fSimplePatternDownRules == null) {
			fSimplePatternDownRules = ArrayListMultimap.create();
		}
		return fSimplePatternDownRules;
	}

	/**
	 * @return Returns the equalRules.
	 */
	public Map<IExpr, PatternMatcherEquals> getEqualUpRules() {
		if (fEqualUpRules == null) {
			fEqualUpRules = new HashMap<IExpr, PatternMatcherEquals>();
		}
		return fEqualUpRules;
	}

	private List<IPatternMatcher> getPatternUpRules() {
		if (fPatternUpRules == null) {
			fPatternUpRules = new ArrayList<IPatternMatcher>();
		}
		return fPatternUpRules;
	}

	private ArrayListMultimap<Integer, IPatternMatcher> getSimplePatternUpRules() {
		if (fSimplePatternUpRules == null) {
			fSimplePatternUpRules = ArrayListMultimap.create();
		}
		return fSimplePatternUpRules;
	}

	public List<IAST> definition() {
		ArrayList<IAST> definitionList = new ArrayList<IAST>();
		Iterator<IExpr> iter;
		IExpr key;
		PatternMatcherEquals pme;
		IExpr condition;
		ISymbol setSymbol;
		IAST ast;
		PatternMatcherAndEvaluator pmEvaluator;
		if (fEqualDownRules != null && fEqualDownRules.size() > 0) {
			iter = fEqualDownRules.keySet().iterator();
			while (iter.hasNext()) {
				key = iter.next();
				pme = fEqualDownRules.get(key);
				setSymbol = pme.getSetSymbol();
				ast = F.ast(setSymbol);
				ast.add(key);
				ast.add(pme.getRHS());
				definitionList.add(ast);
			}
		}
		if (fSimplePatternDownRules != null && fSimplePatternDownRules.size() > 0) {
			Iterator<IPatternMatcher> listIter = fSimplePatternDownRules.values().iterator();
			IPatternMatcher elem;
			while (listIter.hasNext()) {
				elem = listIter.next();
				if (elem instanceof PatternMatcherAndEvaluator) {
					pmEvaluator = (PatternMatcherAndEvaluator) elem;
					setSymbol = pmEvaluator.getSetSymbol();

					ast = F.ast(setSymbol);
					ast.add(pmEvaluator.getLHS());
					condition = pmEvaluator.getCondition();
					if (condition != null) {
						ast.add(F.Condition(pmEvaluator.getRHS(), condition));
					} else {
						ast.add(pmEvaluator.getRHS());
					}
					definitionList.add(ast);
				}
				// if (elem instanceof PatternMatcherAndInvoker) {
				// don't show internal methods associated with a pattern
				// }
			}
		}
		if (fPatternDownRules != null && fPatternDownRules.size() > 0) {
			for (int i = 0; i < fPatternDownRules.size(); i++) {
				if (fPatternDownRules.get(i) instanceof PatternMatcherAndEvaluator) {
					pmEvaluator = (PatternMatcherAndEvaluator) fPatternDownRules.get(i);
					setSymbol = pmEvaluator.getSetSymbol();
					ast = F.ast(setSymbol);
					ast.add(pmEvaluator.getLHS());
					condition = pmEvaluator.getCondition();
					if (condition != null) {
						ast.add(F.Condition(pmEvaluator.getRHS(), condition));
					} else {
						ast.add(pmEvaluator.getRHS());
					}
					definitionList.add(ast);
				}
			}

		}

		return definitionList;
	}

	public void readSymbol(java.io.ObjectInputStream stream) throws IOException {

		String astString;
		IExpr key;
		IExpr value;
		EvalEngine engine = EvalEngine.get();
		ISymbol setSymbol;
		int len = stream.read();
		if (len > 0) {
			// fEqualRules = new HashMap<IExpr, Pair<ISymbol, IExpr>>();
			// for (int i = 0; i < len; i++) {
			// astString = stream.readUTF();
			// setSymbol = F.$s(astString);
			//
			// astString = stream.readUTF();
			// key = engine.parse(astString);
			// astString = stream.readUTF();
			// value = engine.parse(astString);
			// fEqualRules.put(key, new Pair<ISymbol, IExpr>(setSymbol, value));
			// }
			fEqualDownRules = new HashMap<IExpr, PatternMatcherEquals>();
			for (int i = 0; i < len; i++) {
				astString = stream.readUTF();
				setSymbol = F.$s(astString);

				astString = stream.readUTF();
				key = engine.parse(astString);
				astString = stream.readUTF();
				value = engine.parse(astString);
				fEqualDownRules.put(key, new PatternMatcherEquals(setSymbol, key, value));
			}
		}

		len = stream.read();
		IExpr lhs;
		IExpr rhs;
		IExpr condition;
		int listLength;
		int condLength;
		PatternMatcherAndEvaluator pmEvaluator;
		if (len > 0) {
			fSimplePatternDownRules = ArrayListMultimap.create();
			for (int i = 0; i < len; i++) {
				astString = stream.readUTF();
				setSymbol = F.$s(astString);

				astString = stream.readUTF();
				lhs = engine.parse(astString);
				astString = stream.readUTF();
				rhs = engine.parse(astString);
				pmEvaluator = new PatternMatcherAndEvaluator(setSymbol, lhs, rhs);

				condLength = stream.read();
				if (condLength == 0) {
					condition = null;
				} else {
					astString = stream.readUTF();
					condition = engine.parse(astString);
					pmEvaluator.setCondition(condition);
				}
				addSimplePatternDownRule(lhs, pmEvaluator);
			}

		}

		len = stream.read();
		if (len > 0) {
			fPatternDownRules = new ArrayList<IPatternMatcher>();
			listLength = stream.read();
			for (int j = 0; j < listLength; j++) {
				astString = stream.readUTF();
				setSymbol = F.$s(astString);

				astString = stream.readUTF();
				lhs = engine.parse(astString);
				astString = stream.readUTF();
				rhs = engine.parse(astString);
				pmEvaluator = new PatternMatcherAndEvaluator(setSymbol, lhs, rhs);

				condLength = stream.read();
				if (condLength == 0) {
					condition = null;
				} else {
					astString = stream.readUTF();
					condition = engine.parse(astString);
					pmEvaluator.setCondition(condition);
				}
				addSimplePatternDownRule(lhs, pmEvaluator);
			}
		}
	}

	public void writeSymbol(java.io.ObjectOutputStream stream) throws java.io.IOException {
		Iterator<IExpr> iter;
		IExpr key;
		IExpr condition;
		// Pair<ISymbol, IExpr> pair;
		// ISymbol setSymbol;
		// PatternMatcherAndEvaluator pmEvaluator;
		// if (fEqualRules == null || fEqualRules.size() == 0) {
		// stream.write(0);
		// } else {
		// stream.write(fEqualRules.size());
		// iter = fEqualRules.keySet().iterator();
		// while (iter.hasNext()) {
		// key = iter.next();
		// pair = fEqualRules.get(key);
		// stream.writeUTF(pair.getFirst().toString());
		// stream.writeUTF(key.fullFormString());
		// stream.writeUTF(pair.getSecond().fullFormString());
		// }
		// }
		PatternMatcherEquals pme;
		ISymbol setSymbol;
		PatternMatcherAndEvaluator pmEvaluator;
		if (fEqualDownRules == null || fEqualDownRules.size() == 0) {
			stream.write(0);
		} else {
			stream.write(fEqualDownRules.size());
			iter = fEqualDownRules.keySet().iterator();
			while (iter.hasNext()) {
				key = iter.next();
				pme = fEqualDownRules.get(key);
				stream.writeUTF(pme.getLHS().toString());
				stream.writeUTF(key.fullFormString());
				stream.writeUTF(pme.getRHS().fullFormString());
			}
		}

		if (fSimplePatternDownRules == null || fSimplePatternDownRules.size() == 0) {
			stream.write(0);
		} else {
			stream.write(fSimplePatternDownRules.size());
			Iterator<IPatternMatcher> listIter = fSimplePatternDownRules.values().iterator();
			IPatternMatcher elem;
			while (listIter.hasNext()) {
				elem = listIter.next();
				pmEvaluator = (PatternMatcherAndEvaluator) elem;
				setSymbol = pmEvaluator.getSetSymbol();
				stream.writeUTF(setSymbol.toString());
				stream.writeUTF(pmEvaluator.getLHS().fullFormString());
				stream.writeUTF(pmEvaluator.getRHS().fullFormString());
				condition = pmEvaluator.getCondition();
				if (condition == null) {
					stream.write(0);
				} else {
					stream.write(1);
					stream.writeUTF(condition.fullFormString());
				}
			}
		}
		if (fPatternDownRules == null || fPatternDownRules.size() == 0) {
			stream.write(0);
		} else {
			stream.write(fPatternDownRules.size());

			for (int i = 0; i < fPatternDownRules.size(); i++) {
				pmEvaluator = (PatternMatcherAndEvaluator) fPatternDownRules.get(i);
				setSymbol = pmEvaluator.getSetSymbol();
				stream.writeUTF(setSymbol.toString());
				stream.writeUTF(pmEvaluator.getLHS().fullFormString());
				stream.writeUTF(pmEvaluator.getRHS().fullFormString());
				condition = pmEvaluator.getCondition();
				if (condition == null) {
					stream.write(0);
				} else {
					stream.write(1);
					stream.writeUTF(condition.fullFormString());
				}
			}

		}
	}
}