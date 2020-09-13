package org.matheclipse.core.expression;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ObjIntConsumer;
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
import org.matheclipse.core.visit.IVisitor;

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
		append(S.Association);

		appendRules(listOfRules);
	}

	public ASTAssociation(final int initialCapacity, final boolean setLength) {
		super(initialCapacity, setLength);
		map = new Object2IntOpenHashMap<IExpr>();
		if (setLength) {
			set(0, S.Association);
		} else {
			append(S.Association);
		}
	}

	/** {@inheritDoc} */
	@Override
	public IExpr accept(IVisitor visitor) {
		return visitor.visit(this);
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
		if (rule.isRuleAST()) {
			int value = map.getInt(rule.first());
			if (value == 0) {
				append(rule);
				map.put(rule.first(), index++);
			} else {
				set(value, rule);
				map.put(rule.first(), value);
			}
		} else if (rule.isEmptyList()) {
			// ignore empty list entries
		} else {
			throw new ArgumentTypeException("rule expression expected instead of " + rule.toString());
		}
	}

	@Override
	public void appendRules(IAST listOfRules) {
		appendRules(listOfRules, 1, listOfRules.size());
	}

	@Override
	public void appendRules(IAST listOfRules, int startPosition, int endPosition) {
		if (listOfRules.isRuleAST()) {
			appendRule(listOfRules);
			// int value = map.getInt(listOfRules.first());
			// if (value == 0) {
			// append(listOfRules);
			// map.put(listOfRules.first(), index++);
			// } else {
			// set(value, listOfRules);
			// map.put(listOfRules.first(), value);
			// }
		} else {
			for (int i = startPosition; i < endPosition; i++) {
				IExpr rule = listOfRules.getRule(i);
				if (rule.isAssociation()) {
					ASTAssociation assoc = (ASTAssociation) rule;
					for (int j = 1; j < assoc.size(); j++) {
						rule = assoc.getRule(j);
						appendRule(rule);
						// int value = map.getInt(rule.first());
						// if (value == 0) {
						// append(rule);
						// map.put(rule.first(), index++);
						// } else {
						// set(value, rule);
						// map.put(rule.first(), value);
						// }
					}
				} else if (rule.isRuleAST()) {
					appendRule(rule);
					// int value = map.getInt(rule.first());
					// if (value == 0) {
					// append(rule);
					// map.put(rule.first(), index++);
					// } else {
					// set(value, rule);
					// map.put(rule.first(), value);
					// }
				} else if (rule.isList()) {
					IAST list = (IAST) rule;
					appendRules(list, 1, list.size());
				} else {
					throw new ArgumentTypeException("rule expression expected instead of " + rule.toString());
				}
			}
		}
	}

	@Override
	public IExpr arg1() {
		return get(1);
	}

	@Override
	public IExpr arg2() {
		return get(2);
	}

	@Override
	public IExpr arg3() {
		return get(3);
	}

	@Override
	public IExpr arg4() {
		return get(4);
	}

	@Override
	public IExpr arg5() {
		return get(5);
	}

	/**
	 * Get the index of the left-hand-side of a rule. If the returned value is<code>0</code> no value was found.
	 * 
	 * @param expr
	 * @return if <code>0</code> no value was found
	 */
	// private int getIndex(IExpr expr) {
	// return map.getInt(expr);
	// }

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

	@Override
	public IASTMutable copyAST() {
		IASTMutable result = super.copy();
		for (int i = 1; i < size(); i++) {
			result.set(i, getValue(i));
		}
		return result;
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ASTAssociation)) {
			return false;
		}
		return super.equals(obj);
		// map doesn't matter for equals
		// ASTAssociation other = (ASTAssociation) obj;
		// if (map == null || other.map == null) {
		// return map == other.map;
		// }
		// return map.equals(other.map);
	}

	/** {@inheritDoc} */
	@Override
	public IExpr evaluate(EvalEngine engine) {
		if (isEvalFlagOff(IAST.BUILT_IN_EVALED)) {
			addEvalFlags(IAST.BUILT_IN_EVALED);
			ASTAssociation result = null;
			for (int i = 1; i < size(); i++) {
				IExpr arg = getRule(i);
				if (arg.isRule()) {
					// for Rules eval rhs / for RuleDelayed don't
					IExpr temp = engine.evaluateNull(arg.second());
					if (temp.isPresent()) {
						if (result == null) {
							result = copy();
						}
						result.set(i, getRule(i).setAtCopy(2, temp));
					}
				}
			}
			if (result != null) {
				return result;
			}
		}
		return F.NIL;
	}

	/** {@inheritDoc} */
	@Override
	public IAST filter(IASTAppendable filterAST, Predicate<? super IExpr> predicate) {
		if (filterAST instanceof ASTAssociation) {
			for (int i = 1; i < size(); i++) {
				if (predicate.test(getValue(i))) {
					((ASTAssociation) filterAST).appendRule(getRule(i));
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
			for (int i = 1; i < size(); i++) {
				if (predicate.test(getValue(i))) {
					if (++count[0] == maxMatches) {
						((ASTAssociation) filterAST).appendRule(getRule(i));
						break;
					}
					((ASTAssociation) filterAST).appendRule(getRule(i));
				}
			}
			return filterAST;
		}
		return super.filter(filterAST, predicate, maxMatches);
	}

	/** {@inheritDoc} */
	@Override
	public void forEach(Consumer<? super IExpr> action, int startOffset) {
		for (int i = startOffset; i < size(); i++) {
			action.accept(getValue(i));
		}
	}

	/** {@inheritDoc} */
	@Override
	public void forEach(int startOffset, int endOffset, Consumer<? super IExpr> action) {
		for (int i = startOffset; i < endOffset; i++) {
			action.accept(getValue(i));
		}
	}

	/** {@inheritDoc} */
	@Override
	public void forEach(int startOffset, int endOffset, ObjIntConsumer<? super IExpr> action) {
		for (int i = startOffset; i < endOffset; i++) {
			action.accept(getValue(i), i);
		}
	}

	@Override
	public String fullFormString() {
		return normal(S.Association).fullFormString();
	}

	@Override
	public IExpr get(int position) {
		if (position == 0) {
			return head();
		}
		return super.get(position).second();
	}

	@Override
	public IAST getItems(int[] items, int length) {
		ASTAssociation assoc = new ASTAssociation(length, false);
		if (length > 0) {
			for (int i = 0; i < length; i++) {
				assoc.appendRule(getRule(items[i]));
			}
		}
		return assoc;
	}

	@Override
	public IExpr getKey(int position) {
		IExpr temp = getRule(position).first();
		if (temp.isPresent()) {
			return F.Key(temp);
		}
		return F.C0;
	}

	@Override
	public IAST getRule(int position) {
		IExpr temp = super.get(position);
		if (temp.isRuleAST()) {
			return (IAST) temp;
		}
		return F.NIL;
		// return (IAST) super.get(position);
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
		return getValue(index);
	}

	public IExpr getValue(int position) {
		if (position == 0) {
			return super.get(position);
		}
		return super.get(position).second();
	}

	@Override
	public int hashCode() {
		return super.hashCode() * 19;
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
	public boolean isKey(IExpr key) {
		return map.containsKey(key);
	}
	
	/** {@inheritDoc} */
	@Override
	public boolean isListOrAssociation() {
		return true;
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
		return keys(S.List);
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
			assoc.appendRule(getRule(value));
		}
		return assoc;
	}

	/** {@inheritDoc} */
	@Override
	public IAST map(final Function<IExpr, IExpr> function, final int startOffset) {
		IExpr temp;
		ASTAssociation result = null;
		int i = startOffset;
		int size = size();
		while (i < size) {
			temp = function.apply(getValue(i));
			if (temp.isPresent()) {
				// something was evaluated - return a new IAST:
				result = copy();
				result.set(i, getRule(i).setAtCopy(2, temp));
				i++;
				break;
			}
			i++;
		}
		if (result != null) {
			while (i < size) {
				temp = function.apply(getValue(i));
				if (temp.isPresent()) {
					result.set(i, getRule(i).setAtCopy(2, temp));
				}
				i++;
			}
		}
		if (result != null) {
			return result;
		}
		return this;
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
				list.append(F.List(key, getValue(value)));
			}
			return list;
		} else {
			IASTAppendable list = F.ListAlloc(size());
			for (int i = 1; i < size(); i++) {
				list.append(getValue(i));
			}
			return list;
		}
	}

	@Override
	public IAST normal(boolean nilIfUnevaluated) {
		return normal(S.List);
	}

	protected IAST normal(IBuiltInSymbol symbol) {
		IExpr[] arr = new IExpr[size() - 1];
		System.arraycopy(array, 1, arr, 0, size() - 1);
		return F.ast(arr, symbol);
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
		append(S.Association);
		IAST ast = (IAST) objectInput.readObject();
		for (int i = 1; i < ast.size(); i++) {
			appendRule(ast.get(i));
		}
	}

	@Override
	public IExpr remove(int location) {
		IExpr result = super.remove(location);
		map.remove(result.first(), location);
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

	@Override
	public IAssociation reverse(IAssociation newAssoc) {
		for (int i = argSize(); i >= 1; i--) {
			newAssoc.appendRule(getRule(i));
		}
		return newAssoc;
	}

	@Override
	public IExpr set(int location, IExpr object) {
		if (location != 0 && !object.isRuleAST()) {
			ArgumentTypeException.throwArg(object, S.Association);
		}
		return super.set(location, object);
	}

	@Override
	public IAssociation sort() {
		return sort(null);
	}

	@Override
	public IAssociation sort(Comparator<IExpr> comp) {
		List<Integer> indices = new ArrayList<Integer>(argSize());
		for (int i = 1; i < size(); i++) {
			indices.add(i);
		}
		Comparator<Integer> comparator;
		if (comp == null) {
			comparator = new Comparator<Integer>() {
				@Override
				public int compare(Integer i, Integer j) {
					return getValue(i).compareTo(getValue(j));
				}
			};
		} else {
			comparator = new Comparator<Integer>() {
				@Override
				public int compare(Integer i, Integer j) {
					return comp.compare(getValue(i), getValue(j));
				}
			};
		}
		Collections.sort(indices, comparator);
		ASTAssociation result = new ASTAssociation(argSize(), true);
		for (Object2IntMap.Entry<IExpr> element : map.object2IntEntrySet()) {
			// for (Map.Entry<IExpr, Integer> element : map.entrySet()) {
			int indx = element.getIntValue();
			for (int i = 0; i < indices.size(); i++) {
				if (indices.get(i) == indx) {
					indx = i + 1;
					break;
				}
			}
			int newValue = indices.get(indx - 1);
			result.set(indx, getRule(newValue));
			result.map.put(element.getKey(), indx);
		}
		return result;
	}

	@Override
	public IASTMutable values() {
		return values(S.List);
	}

	protected IASTMutable values(IBuiltInSymbol symbol) {
		IASTMutable list = copyAST();
		list.set(0, symbol);
		return list;
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		IAST ast = normal(false);
		objectOutput.writeObject(ast);
	}
}
