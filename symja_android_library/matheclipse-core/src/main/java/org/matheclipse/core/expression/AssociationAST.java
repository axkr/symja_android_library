package org.matheclipse.core.expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;

public class AssociationAST extends AST {

	/**
	 * Map the <code>IExpr()</code> keys to the index of the values in this AST. For <code>Rule()</code> the index is
	 * greater 0 and <code>get(index)</code> returns the value of the <code>Rule()</code. For <code>RuleDelyed()</code>
	 * the index is less 0 and must be multiplied by -1 and <code>get(index * (-1))</code> returns the value of the
	 * <code>RuleDelayed()</code>.
	 */
	private HashMap<IExpr, Integer> map;

	/**
	 * Public no-arg constructor only needed for serialization
	 * 
	 */
	public AssociationAST() {
		super();
		map = new HashMap<IExpr, Integer>();
	}

	public AssociationAST(IAST listOfRules) {
		super(listOfRules.size(), false);
		map = new HashMap<IExpr, Integer>();
		append(F.Association);

		int index = 1;
		for (int i = 1; i < listOfRules.size(); i++) {
			IExpr rule = listOfRules.get(i);
			if (rule.isRule()) {
				Integer value = getIndex(rule.first());
				if (value == null) {
					append(rule.second());
					map.put(rule.first(), index++);
				} else {
					set(value, rule.second());
					map.put(rule.first(), value);
				}
			} else if (rule.isRuleDelayed()) {
				Integer value = getIndex(rule.first());
				if (value == null) {
					append(rule.second());
					map.put(rule.first(), -index);
					index++;
				} else {
					set(value, rule.second());
					map.put(rule.first(), -value);
				}
			} else if (rule.isAST(F.List, 1)) {
				// ignore empty list entries
			} else {
				throw new WrongArgumentType(F.Association, "Rule expected");
			}
		}
	}

	/**
	 * Adds the specified rule at the end of this association.
	 * 
	 * @param object
	 *            the object to add.
	 * @return always true
	 */
	public final void appendRule(IAST rule) {
		int index = size();
		if (rule.isRule()) {
			Integer value = getIndex(rule.first());
			if (value == null) {
				append(rule.second());
				map.put(rule.first(), index++);
			} else {
				set(value, rule.second());
				map.put(rule.first(), value);
			}
		} else if (rule.isRuleDelayed()) {
			Integer value = getIndex(rule.first());
			if (value == null) {
				append(rule.second());
				map.put(rule.first(), -index);
				index++;
			} else {
				set(value, rule.second());
				map.put(rule.first(), -value);
			}
		} else if (rule.isAST(F.List, 1)) {
			// ignore empty list entries
		} else {
			throw new WrongArgumentType(F.Association, "Rule expected");
		}
	}

	protected AssociationAST(final int initialCapacity, final boolean setLength) {
		super(initialCapacity, setLength);
		map = new HashMap<IExpr, Integer>();
		append(F.Association);
	}

	private Integer getIndex(IExpr expr) {
		Integer value = map.get(expr);
		if (value != null) {
			if (value < 0) {
				return -value;
			}
		}
		return value;
	}

	@Override
	public IAST clone() {
		AssociationAST ast = new AssociationAST();
		// ast.fProperties = null;
		ast.array = array.clone();
		ast.hashValue = 0;
		ast.firstIndex = firstIndex;
		ast.lastIndex = lastIndex;
		ast.map = (HashMap<IExpr, Integer>) map.clone();
		return ast;
	}

	@Override
	public IASTAppendable copyAppendable() {
		AssociationAST ast = new AssociationAST();
		// ast.fProperties = null;
		ast.array = array.clone();
		ast.hashValue = 0;
		ast.firstIndex = firstIndex;
		ast.lastIndex = lastIndex;
		ast.map = (HashMap<IExpr, Integer>) map.clone();
		return ast;
	}

	/** {@inheritDoc} */
	@Override
	public IASTAppendable copyHead(final int intialCapacity) {
		return new AssociationAST(intialCapacity, false);
	}

	/** {@inheritDoc} */
	@Override
	public IExpr evaluate(EvalEngine engine) {
		return F.NIL;
	}

	@Override
	public IASTMutable copy() {
		AssociationAST ast = new AssociationAST();
		// ast.fProperties = null;
		ast.array = array.clone();
		ast.hashValue = 0;
		ast.firstIndex = firstIndex;
		ast.lastIndex = lastIndex;
		ast.map = (HashMap<IExpr, Integer>) map.clone();
		return ast;
	}

	@Override
	public IASTMutable copyAST() {
		return super.copy();
	}

	public IASTMutable normal() {
		IASTMutable list = normal(F.List);
		return list;
	}

	protected IASTMutable normal(IBuiltInSymbol symbol) {
		IASTMutable list = F.ast(symbol, argSize(), true);

		for (Map.Entry<IExpr, Integer> element : map.entrySet()) {
			Integer value = element.getValue();
			if (value < 0) {
				value *= -1;
				list.set(value, F.RuleDelayed(element.getKey(), get(value)));
			} else {
				list.set(value, F.Rule(element.getKey(), get(value)));
			}
		}
		return list;
	}

	public IASTMutable keys() {
		return keys(F.List);
	}

	protected IASTMutable keys(IBuiltInSymbol symbol) {
		IASTMutable list = F.ast(symbol, argSize(), true);
		for (Map.Entry<IExpr, Integer> element : map.entrySet()) {
			Integer value = element.getValue();
			if (value < 0) {
				value *= -1;
			}
			list.set(value, element.getKey());
		}
		return list;
	}

	public IASTMutable values() {
		return values(F.List);
	}

	protected IASTMutable values(IBuiltInSymbol symbol) {
		IASTMutable list = copyAST();
		list.set(0, symbol);
		return list;
	}

	public IExpr getValue(IExpr key) {
		Integer index = map.get(key);
		if (index == null) {
			return F.Missing(F.stringx("KeyAbsent"), key);
		}
		if (index < 0) {
			index *= -1;
		}
		return get(index);
	}

	/** {@inheritDoc} */
	@Override
	public IAST filter(IASTAppendable filterAST, Predicate<? super IExpr> predicate) {
		if (filterAST instanceof AssociationAST) {
			IAST list = normal();
			for (int i = 1; i < size(); i++) {
				if (predicate.test(get(i))) {
					((AssociationAST) filterAST).appendRule((IAST) list.get(i));
				}
			}
			return filterAST;
		}
		return super.filter(filterAST, predicate);
	}

	/** {@inheritDoc} */
	@Override
	public IAST filter(IASTAppendable filterAST, Predicate<? super IExpr> predicate, int maxMatches) {
		if (filterAST instanceof AssociationAST) {
			int[] count = new int[1];
			if (count[0] >= maxMatches) {
				return filterAST;
			}
			IAST list = normal();
			for (int i = 1; i < size(); i++) {
				if (predicate.test(get(i))) {
					if (++count[0] == maxMatches) {
						((AssociationAST) filterAST).appendRule((IAST) list.get(i));
						break;
					}
					((AssociationAST) filterAST).appendRule((IAST) list.get(i));
				}
			}
			return filterAST;
		}
		return super.filter(filterAST, predicate, maxMatches);
	}

	public String fullFormString() {
		return normal(F.Association).fullFormString();
	}

	public AssociationAST sort() {
		List<Integer> indices = new ArrayList<Integer>(argSize());
		for (int i = 1; i < size(); i++) {
			indices.add(i);
		}
		Comparator<Integer> comparator = new Comparator<Integer>() {
			public int compare(Integer i, Integer j) {
				return get(i).compareTo(get(j));
			}
		};
		Collections.sort(indices, comparator);
		AssociationAST result = new AssociationAST(size(), false);
		for (Map.Entry<IExpr, Integer> element : map.entrySet()) {
			Integer value = element.getValue();
			if (value < 0) {
				value *= -1;
			}
			Integer newValue = indices.get(value - 1);
			result.append(get(newValue));
			result.map.put(element.getKey(), newValue);
		}
		return result;
	}
}
