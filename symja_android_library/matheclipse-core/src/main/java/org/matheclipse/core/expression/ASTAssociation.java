package org.matheclipse.core.expression;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public class ASTAssociation extends AST implements IAssociation {

	/**
	 * Map the <code>IExpr()</code> keys to the index of the values in this AST. For <code>Rule()</code> the index is
	 * greater 0 and <code>get(index)</code> returns the value of the <code>Rule()</code. For <code>RuleDelyed()</code>
	 * the index is less 0 and must be multiplied by -1 and <code>get(index * (-1))</code> returns the value of the
	 * <code>RuleDelayed()</code>.
	 */
	private transient Object2IntOpenHashMap<IExpr> map;
	// private transient HashMap<IExpr, Integer> map;

	private transient IAST normalCache = null;

	/**
	 * Public no-arg constructor only needed for serialization
	 * 
	 */
	public ASTAssociation() {
		super(10, false);
		map = new Object2IntOpenHashMap<IExpr>();
	}

	public ASTAssociation(IAST listOfRules) {
		super(listOfRules.size(), false);
		map = new Object2IntOpenHashMap<IExpr>();
		append(F.Association);

		appendRules(listOfRules);
	}

	public void appendRules(IAST listOfRules) {
		appendRules(listOfRules, 1, listOfRules.size());
	}

	public void appendRules(IAST listOfRules, int startPosition, int endPosition) {
		int index = size();
		normalCache = null;
		if (listOfRules.isRule()) {
			int value = getIndex(listOfRules.first());
			if (value == 0) {
				append(listOfRules.second());
				map.put(listOfRules.first(), index++);
			} else {
				set(value, listOfRules.second());
				map.put(listOfRules.first(), value);
			}
		} else if (listOfRules.isRuleDelayed()) {
			int value = getIndex(listOfRules.first());
			if (value == 0) {
				append(listOfRules.second());
				map.put(listOfRules.first(), -index);
				index++;
			} else {
				set(value, listOfRules.second());
				map.put(listOfRules.first(), -value);
			}
		} else {
			for (int i = startPosition; i < endPosition; i++) {

				IExpr rule = listOfRules.getRule(i);
				if (rule.isAssociation()) {
					ASTAssociation assoc = (ASTAssociation) rule;
					for (int j = 1; j < assoc.size(); j++) {
						appendRule(assoc.getRule(j));
					}
				} else if (rule.isRule()) {
					int value = getIndex(rule.first());
					if (value == 0) {
						append(rule.second());
						map.put(rule.first(), index++);
					} else {
						set(value, rule.second());
						map.put(rule.first(), value);
					}
				} else if (rule.isRuleDelayed()) {
					int value = getIndex(rule.first());
					if (value == 0) {
						append(rule.second());
						map.put(rule.first(), -index);
						index++;
					} else {
						set(value, rule.second());
						map.put(rule.first(), -value);
					}
				} else if (rule.isList()) {
					IAST list = (IAST) rule;
					appendRules(list, 1, list.size());
				} else {
					throw new ArgumentTypeException("rule expression expected instead of " + rule.toString());
				}
			}
		}
	}

	/**
	 * Adds the specified rule at the end of this association.
	 * 
	 * @param rule
	 *            the rule to add at the end of this association
	 * @return always true
	 */
	@Override
	public final void appendRule(IExpr rule) {
		int index = size();
		if (rule.isRule()) {
			normalCache = null;
			int value = getIndex(rule.first());
			if (value == 0) {
				append(rule.second());
				map.put(rule.first(), index++);
			} else {
				set(value, rule.second());
				map.put(rule.first(), value);
			}
		} else if (rule.isRuleDelayed()) {
			normalCache = null;
			int value = getIndex(rule.first());
			if (value == 0) {
				append(rule.second());
				map.put(rule.first(), -index);
				index++;
			} else {
				set(value, rule.second());
				map.put(rule.first(), -value);
			}
		} else if (rule.isEmptyList()) {
			// ignore empty list entries
		} else {
			throw new ArgumentTypeException("rule expression expected instead of " + rule.toString());
		}
	}

	public ASTAssociation(final int initialCapacity, final boolean setLength) {
		super(initialCapacity, setLength);
		map = new Object2IntOpenHashMap<IExpr>();
		if (setLength) {
			set(0, F.Association);
		} else {
			append(F.Association);
		}
	}

	private Integer getIndex(IExpr expr) {
		int value = map.getInt(expr);
		return value < 0 ? -value : value;
	}

	@Override
	public IAST clone() {
		ASTAssociation ast = new ASTAssociation();
		// ast.fProperties = null;
		ast.array = array.clone();
		ast.hashValue = 0;
		ast.firstIndex = firstIndex;
		ast.lastIndex = lastIndex;
		ast.map = map.clone();
		return ast;
	}

	@Override
	public IASTAppendable copyAppendable() {
		ASTAssociation ast = new ASTAssociation();
		// ast.fProperties = null;
		ast.array = array.clone();
		ast.hashValue = 0;
		ast.firstIndex = firstIndex;
		ast.lastIndex = lastIndex;
		ast.map = map.clone();
		return ast;
	}

	@Override
	public IASTAppendable copyAppendable(int additionalCapacity) {
		ASTAssociation ast = new ASTAssociation();
		// ast.fProperties = null;
		if (size() + additionalCapacity > array.length) {
			ast.array = new IExpr[size() + additionalCapacity];
		} else {
			ast.array = array.clone();
		}
		ast.hashValue = 0;
		ast.firstIndex = firstIndex;
		ast.lastIndex = lastIndex;
		ast.map = map.clone();
		return ast;
	}

	/** {@inheritDoc} */
	@Override
	public IAssociation copyHead() {
		return new ASTAssociation(size(), false);
	}

	/** {@inheritDoc} */
	@Override
	public IAssociation copyHead(final int intialCapacity) {
		return new ASTAssociation(intialCapacity, false);
	}

	/** {@inheritDoc} */
	@Override
	public IASTAppendable copyUntil(int index) {
		return copyUntil(index, index);
	}

	/** {@inheritDoc} */
	@Override
	public final IASTAppendable copyUntil(final int intialCapacity, int index) {
		ASTAssociation result = new ASTAssociation(intialCapacity, false);
		result.appendRules(this.normal(false), 1, index);
		return result;
	}

	// public boolean appendAllRules(ASTAssociation ast, int startPosition, int endPosition) {
	// if (ast.size() > 0 && startPosition < endPosition) {
	// normalCache = null;
	// appendAll(ast, startPosition, endPosition);
	// for (Object2IntMap.Entry<IExpr> element : ast.map.object2IntEntrySet()) {
	// int value = element.getIntValue();
	// if (Math.abs(value) >= startPosition && Math.abs(value) < endPosition) {
	// map.put(element.getKey(), value);
	// }
	// }
	// return true;
	// }
	// return false;
	// }

	/** {@inheritDoc} */
	@Override
	public IExpr evaluate(EvalEngine engine) {
		if (isEvalFlagOff(IAST.BUILT_IN_EVALED)) {
			addEvalFlags(IAST.BUILT_IN_EVALED);
			ASTAssociation result = null;
			for (Object2IntMap.Entry<IExpr> element : map.object2IntEntrySet()) {
				int value = element.getIntValue();
				if (value > 0) {
					// for Rules eval rhs / for RuleDelayed don't
					IExpr temp = engine.evaluateNull(get(value));
					if (temp.isPresent()) {
						if (result == null) {
							result = copy();
						}
						result.set(value, temp);
					}
				}
			}
			if (result != null) {
				return result;
			}
		}
		return F.NIL;
	}

	@Override
	public ASTAssociation copy() {
		ASTAssociation ast = new ASTAssociation();
		// ast.fProperties = null;
		ast.array = array.clone();
		ast.hashValue = 0;
		ast.firstIndex = firstIndex;
		ast.lastIndex = lastIndex;
		ast.map = map.clone();
		return ast;
	}

	@Override
	public IASTMutable copyAST() {
		return super.copy();
	}

	/**
	 * Test if this AST is an association <code>&lt;|a-&gt;b, c-&gt;d|&gt;</code>(i.e. type <code>AssociationAST</code>)
	 * 
	 * @return
	 */
	@Override
	public boolean isAssociation() {
		return true;
	}

	@Override
	public IAST normal(boolean nilIfUnevaluated) {
		if (normalCache != null) {
			return normalCache;
		}
		normalCache = normal(F.List);
		return normalCache;
	}

	protected IAST normal(IBuiltInSymbol symbol) {
		IASTMutable list = F.ast(symbol, argSize(), true);

		for (Object2IntMap.Entry<IExpr> element : map.object2IntEntrySet()) {
			int value = element.getIntValue();
			if (value < 0) {
				value *= -1;
				list.set(value, F.RuleDelayed(element.getKey(), get(value)));
			} else {
				list.set(value, F.Rule(element.getKey(), get(value)));
			}
		}
		return list;
	}

	@Override
	public IAST matrixOrList() {

		boolean numericKeys = true;
		try {
			for (Object2IntMap.Entry<IExpr> element : map.object2IntEntrySet()) {
				IExpr key = element.getKey();
				if (!key.isReal()) {
					double d = key.evalDouble();
					numericKeys = false;
					break;
				}
			}
		} catch (RuntimeException rex) {
			numericKeys = false;
		}
		if (numericKeys) {
			IASTAppendable list = F.ListAlloc(map.size());
			for (Object2IntMap.Entry<IExpr> element : map.object2IntEntrySet()) {
				IExpr key = element.getKey();
				int value = element.getIntValue();
				if (value < 0) {
					value *= -1;
				}
				list.append(F.List(key, get(value)));
			}
			return list;
		} else {
			IASTAppendable list = F.ListAlloc(size());
			for (int i = 1; i < size(); i++) {
				list.append(get(i));
			}
			return list;
		}
	}

	@Override
	public boolean isKey(IExpr key) {
		return map.containsKey(key);
	}

	@Override
	public ArrayList<String> keyNames() {
		ArrayList<String> list = new ArrayList<String>();
		for (Object2IntMap.Entry<IExpr> element : map.object2IntEntrySet()) {
			list.add(element.getKey().toString());
		}
		return list;
	}

	@Override
	public IASTMutable keys() {
		return keys(F.List);
	}

	protected IASTMutable keys(IBuiltInSymbol symbol) {
		IASTMutable list = F.ast(symbol, argSize(), true);
		for (Object2IntMap.Entry<IExpr> element : map.object2IntEntrySet()) {
			// for (Map.Entry<IExpr, Integer> element : map.entrySet()) {
			int value = element.getIntValue();
			if (value < 0) {
				value *= -1;
			}
			list.set(value, element.getKey());
		}
		return list;
	}

	@Override
	public IASTMutable values() {
		return values(F.List);
	}

	protected IASTMutable values(IBuiltInSymbol symbol) {
		IASTMutable list = copyAST();
		list.set(0, symbol);
		return list;
	}

	@Override
	public IExpr getKey(int position) {
		IAST ast = normal(false);
		IExpr temp = ast.get(position).first();
		if (temp.isPresent()) {
			return F.Key(temp);
		}
		return F.C0;
	}

	@Override
	public IAST getRule(int position) {
		IAST ast = normal(false);
		return (IAST) ast.get(position);
	}

	@Override
	public IExpr getValue(IExpr key) {
		return getValue(key, () -> F.Missing(F.stringx("KeyAbsent"), key));
	}

	@Override
	public IExpr getValue(IExpr key, Supplier<IExpr> defaultValue) {
		int index = map.getInt(key);
		if (index == 0) {
			return defaultValue.get();// F.Missing(F.stringx("KeyAbsent"), key);
		}
		if (index < 0) {
			index *= -1;
		}
		return get(index);
	}

	@Override
	public IAST getItems(int[] items, int length) {
		ASTAssociation assoc = new ASTAssociation(length, false);
		if (length > 0) {
			if (length <= 5) {
				for (int i = 0; i < length; i++) {
					for (Object2IntMap.Entry<IExpr> element : map.object2IntEntrySet()) {
						int value = element.getIntValue();
						if (value < 0) {
							value *= -1;
							if (value == items[i]) {
								assoc.appendRule(F.RuleDelayed(element.getKey(), get(value)));
								break;
							}
						} else {
							if (value == items[i]) {
								assoc.appendRule(F.Rule(element.getKey(), get(value)));
								break;
							}
						}
					}
				}
				return assoc;
			}

			IAST ast = normal(false);
			for (int i = 0; i < length; i++) {
				assoc.appendRule((IAST) ast.get(items[i]));
			}
		}
		return assoc;
	}

	/** {@inheritDoc} */
	@Override
	public IAST filter(IASTAppendable filterAST, Predicate<? super IExpr> predicate) {
		if (filterAST instanceof ASTAssociation) {
			IAST list = normal(false);
			for (int i = 1; i < size(); i++) {
				if (predicate.test(get(i))) {
					((ASTAssociation) filterAST).appendRule((IAST) list.get(i));
				}
			}
			return filterAST;
		}
		return super.filter(filterAST, predicate);
	}

	/** {@inheritDoc} */
	@Override
	public IAST filter(IASTAppendable filterAST, Predicate<? super IExpr> predicate, int maxMatches) {
		if (filterAST instanceof ASTAssociation) {
			int[] count = new int[1];
			if (count[0] >= maxMatches) {
				return filterAST;
			}
			IAST list = normal(false);
			for (int i = 1; i < size(); i++) {
				if (predicate.test(get(i))) {
					if (++count[0] == maxMatches) {
						((ASTAssociation) filterAST).appendRule((IAST) list.get(i));
						break;
					}
					((ASTAssociation) filterAST).appendRule((IAST) list.get(i));
				}
			}
			return filterAST;
		}
		return super.filter(filterAST, predicate, maxMatches);
	}

	@Override
	public String fullFormString() {
		return normal(F.Association).fullFormString();
	}

	@Override
	public IExpr remove(int location) {
		normalCache = null;
		IExpr result = super.remove(location);
		for (Object2IntMap.Entry<IExpr> element : map.object2IntEntrySet()) {
			int value = element.getIntValue();
			int indx = value;
			if (indx < 0) {
				indx *= -1;
			}
			if (indx > location) {
				element.setValue(value > 0 ? --value : ++value);
			} else if (indx == location) {
				map.remove(element.getKey(), value);
			}
		}
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public final IASTMutable removeAtCopy(int position) {
		ASTAssociation assoc = copy();
		assoc.remove(position);
		return assoc;
	}

	/** {@inheritDoc} */
	@Override
	public IAST rest() {
		if (size() > 1) {
			return removeAtCopy(1);
		}
		return this;
	}

	public IAssociation reverse(IAssociation newAssoc) {
		for (int i = argSize(); i >= 1; i--) {
			newAssoc.appendRule(getRule(i));
		}
		return newAssoc;
	}

	@Override
	public IAssociation sort() {
		return sort(null);
	}

	@Override
	public IAssociation sort(Comparator<IExpr> comp) {
		normalCache = null;
		List<Integer> indices = new ArrayList<Integer>(argSize());
		for (int i = 1; i < size(); i++) {
			indices.add(i);
		}
		Comparator<Integer> comparator;
		if (comp == null) {
			comparator = new Comparator<Integer>() {
				@Override
				public int compare(Integer i, Integer j) {
					return get(i).compareTo(get(j));
				}
			};
		} else {
			comparator = new Comparator<Integer>() {
				@Override
				public int compare(Integer i, Integer j) {
					return comp.compare(get(i), get(j));
				}
			};
		}
		Collections.sort(indices, comparator);
		ASTAssociation result = new ASTAssociation(argSize(), true);
		for (Object2IntMap.Entry<IExpr> element : map.object2IntEntrySet()) {
			// for (Map.Entry<IExpr, Integer> element : map.entrySet()) {
			int value = element.getIntValue();
			int indx = value;
			if (indx < 0) {
				indx *= -1;
			}
			for (int i = 0; i < indices.size(); i++) {
				if (indices.get(i) == indx) {
					indx = i + 1;
					break;
				}
			}
			int newValue = indices.get(indx - 1);
			result.set(indx, get(newValue));
			if (value < 0) {
				result.map.put(element.getKey(), -indx);
			} else {
				result.map.put(element.getKey(), indx);
			}
		}
		return result;
	}

	@Override
	public IAssociation keySort() {
		return keySort(null);
	}

	@Override
	public IAssociation keySort(Comparator<IExpr> comparator) {
		IASTMutable list = keys();
		if (comparator == null) {
			EvalAttributes.sort(list);
		} else {
			EvalAttributes.sort(list, comparator);
		}
		ASTAssociation assoc = new ASTAssociation(list.argSize(), false);
		for (int i = 1; i < list.size(); i++) {
			IExpr key = list.get(i);
			int value = map.getInt(key);
			if (value < 0) {
				value *= -1;
				assoc.appendRule(F.RuleDelayed(key, get(value)));
			} else {
				assoc.appendRule(F.Rule(key, get(value)));
			}
		}
		return assoc;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
		normalCache = null;
		append(F.Association);
		IAST ast = (IAST) objectInput.readObject();
		for (int i = 1; i < ast.size(); i++) {
			appendRule((IAST) ast.get(i));
		}
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		IAST ast = normal(false);
		objectOutput.writeObject(ast);
	}
}
