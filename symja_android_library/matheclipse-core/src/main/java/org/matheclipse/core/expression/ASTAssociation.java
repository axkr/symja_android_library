package org.matheclipse.core.expression;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

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

		int index = 1;
		for (int i = 1; i < listOfRules.size(); i++) {
			IExpr rule = listOfRules.get(i);
			if (rule.isRule()) {
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
			} else if (rule.isAST(F.List, 1)) {
				// ignore empty list entries
			} else {
				throw new ArgumentTypeException("rule expression expected instead of " + rule.toString());
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
			int value = getIndex(rule.first());
			if (value == 0) {
				append(rule.second());
				map.put(rule.first(), index++);
			} else {
				set(value, rule.second());
				map.put(rule.first(), value);
			}
			normalCache = null;
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
			normalCache = null;
		} else if (rule.isAST(F.List, 1)) {
			// ignore empty list entries
		} else {
			throw new ArgumentTypeException("rule expression expected instead of " + rule.toString());
		}
	}

	public ASTAssociation(final int initialCapacity, final boolean setLength) {
		super(initialCapacity, setLength);
		map = new Object2IntOpenHashMap<IExpr>();
		append(F.Association);
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
		ast.map = (Object2IntOpenHashMap<IExpr>) map.clone();
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
		ast.map = (Object2IntOpenHashMap<IExpr>) map.clone();
		return ast;
	}

	/** {@inheritDoc} */
	@Override
	public IASTAppendable copyHead(final int intialCapacity) {
		return new ASTAssociation(intialCapacity, false);
	}

	/** {@inheritDoc} */
	@Override
	public IExpr evaluate(EvalEngine engine) {
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
		ast.map = (Object2IntOpenHashMap<IExpr>) map.clone();
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

	public IAST normal() {
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

	public boolean isKey(IExpr key) {
		return map.containsKey(key);
	}

	public ArrayList<String> keyNames() {
		ArrayList<String> list = new ArrayList<String>();
		for (Object2IntMap.Entry<IExpr> element : map.object2IntEntrySet()) {
			list.add(element.getKey().toString());
		}
		return list;
	}

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

	public IASTMutable values() {
		return values(F.List);
	}

	protected IASTMutable values(IBuiltInSymbol symbol) {
		IASTMutable list = copyAST();
		list.set(0, symbol);
		return list;
	}

	public IExpr getKey(int position) {
		IAST ast = normal();
		IExpr temp = ast.get(position).first();
		if (temp.isPresent()) {
			return F.Key(temp);
		}
		return F.C0;
	}

	public IExpr getValue(IExpr key) {
		return getValue(key, F.Missing(F.stringx("KeyAbsent"), key));
	}

	public IExpr getValue(IExpr key, IExpr defaultValue) {
		int index = map.getInt(key);
		if (index == 0) {
			return defaultValue;// F.Missing(F.stringx("KeyAbsent"), key);
		}
		if (index < 0) {
			index *= -1;
		}
		return get(index);
	}

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

			IAST ast = normal();
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
			IAST list = normal();
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
			IAST list = normal();
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

	public String fullFormString() {
		return normal(F.Association).fullFormString();
	}

	public IAssociation sort() {
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
		ASTAssociation result = new ASTAssociation(size(), false);
		for (Object2IntMap.Entry<IExpr> element : map.object2IntEntrySet()) {
			// for (Map.Entry<IExpr, Integer> element : map.entrySet()) {
			int value = element.getIntValue();
			if (value < 0) {
				value *= -1;
			}
			int newValue = indices.get(value - 1);
			result.append(get(newValue));
			result.map.put(element.getKey(), newValue);
		}
		return result;
	}

	public IAssociation keySort() {
		return keySort(null);
	}

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
		append(F.Association);
		IAST ast = (IAST) objectInput.readObject();
		for (int i = 1; i < ast.size(); i++) {
			appendRule((IAST) ast.get(i));
		}
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		IAST ast = normal();
		objectOutput.writeObject(ast);
	}
}
