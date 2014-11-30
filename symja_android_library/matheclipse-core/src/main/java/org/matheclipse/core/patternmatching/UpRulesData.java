package org.matheclipse.core.patternmatching;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
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
import org.matheclipse.core.interfaces.ISymbol;

import com.google.common.collect.TreeMultimap;

/**
 * The pattern matching rules associated with a symbol.
 */
public class UpRulesData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 779382003637253257L;

	private transient Map<IExpr, PatternMatcherEquals> fEqualUpRules;
	private transient TreeMultimap<Integer, IPatternMatcher> fSimplePatternUpRules;

	public UpRulesData() {
		this.fEqualUpRules = null;
		this.fSimplePatternUpRules = null;
	}

	public void clear() {
		fEqualUpRules = null;
		fSimplePatternUpRules = null;
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
				final IPatternMatcher[] list = fSimplePatternUpRules.get(hash).toArray(new IPatternMatcher[0]);
				if (list != null) {
					for (int i = 0; i < list.length; i++) {
						pmEvaluator = (IPatternMatcher) list[i].clone();
						result = pmEvaluator.eval(expression);
						if (result != null) {
							return result;
						}
					}
				}
			}

		} catch (CloneNotSupportedException cnse) {
			cnse.printStackTrace();
		}
		return null;
	}

	public IPatternMatcher putUpRule(ISymbol setSymbol, final boolean equalRule, final IAST leftHandSide,
			final IExpr rightHandSide) {
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

		fSimplePatternUpRules = getSimplePatternUpRules();
		return addSimplePatternUpRule(leftHandSide, pmEvaluator);

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

	/**
	 * @return Returns the equalRules.
	 */
	public Map<IExpr, PatternMatcherEquals> getEqualUpRules() {
		if (fEqualUpRules == null) {
			fEqualUpRules = new HashMap<IExpr, PatternMatcherEquals>();
		}
		return fEqualUpRules;
	}

	private TreeMultimap<Integer, IPatternMatcher> getSimplePatternUpRules() {
		if (fSimplePatternUpRules == null) {
			fSimplePatternUpRules = TreeMultimap.create();
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
		if (fEqualUpRules != null && fEqualUpRules.size() > 0) {
			iter = fEqualUpRules.keySet().iterator();
			while (iter.hasNext()) {
				key = iter.next();
				pme = fEqualUpRules.get(key);
				setSymbol = pme.getSetSymbol();
				ast = F.ast(setSymbol);
				ast.add(key);
				ast.add(pme.getRHS());
				definitionList.add(ast);
			}
		}
		if (fSimplePatternUpRules != null && fSimplePatternUpRules.size() > 0) {
			Iterator<IPatternMatcher> listIter = fSimplePatternUpRules.values().iterator();
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
			fEqualUpRules = new HashMap<IExpr, PatternMatcherEquals>();
			for (int i = 0; i < len; i++) {
				astString = stream.readUTF();
				setSymbol = F.$s(astString);

				astString = stream.readUTF();
				key = engine.parse(astString);
				astString = stream.readUTF();
				value = engine.parse(astString);
				fEqualUpRules.put(key, new PatternMatcherEquals(setSymbol, key, value));
			}
		}

		len = stream.read();
		IExpr lhs;
		IExpr rhs;
		IExpr condition;
		int condLength;
		PatternMatcherAndEvaluator pmEvaluator;
		if (len > 0) {
			fSimplePatternUpRules = TreeMultimap.create();
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
				addSimplePatternUpRule(lhs, pmEvaluator);
			}

		}
	}

	@Override
	public String toString() {
		StringWriter buf = new StringWriter();
		List<IAST> list = definition();
		for (int i = 0; i < list.size(); i++) {
			buf.append(list.get(i).toString());
			if (i < list.size() - 1) {
				buf.append(",\n ");
			}
		}
		return buf.toString();
	}
	
	public void writeSymbol(java.io.ObjectOutputStream stream) throws java.io.IOException {
		Iterator<IExpr> iter;
		IExpr key;
		IExpr condition;

		PatternMatcherEquals pme;
		ISymbol setSymbol;
		PatternMatcherAndEvaluator pmEvaluator;
		if (fEqualUpRules == null || fEqualUpRules.size() == 0) {
			stream.write(0);
		} else {
			stream.write(fEqualUpRules.size());
			iter = fEqualUpRules.keySet().iterator();
			while (iter.hasNext()) {
				key = iter.next();
				pme = fEqualUpRules.get(key);
				stream.writeUTF(pme.getLHS().toString());
				stream.writeUTF(key.fullFormString());
				stream.writeUTF(pme.getRHS().fullFormString());
			}
		}

		if (fSimplePatternUpRules == null || fSimplePatternUpRules.size() == 0) {
			stream.write(0);
		} else {
			stream.write(fSimplePatternUpRules.size());
			Iterator<IPatternMatcher> listIter = fSimplePatternUpRules.values().iterator();
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
	}
}