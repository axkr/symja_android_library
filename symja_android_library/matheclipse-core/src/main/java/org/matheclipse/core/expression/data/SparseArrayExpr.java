package org.matheclipse.core.expression.data;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;

import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISparseArray;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.FEConfig;
import org.matheclipse.parser.trie.Trie;
import org.matheclipse.parser.trie.TrieNode;
import org.matheclipse.parser.trie.Tries;

/**
 * <p>
 * Sparse array implementation.
 * </p>
 * 
 */
public class SparseArrayExpr extends DataExpr<Trie<int[], IExpr>> implements ISparseArray, Externalizable {

	private static int[] checkPositions(IAST ast, IExpr arg, EvalEngine engine) {
		if (arg.isNonEmptyList()) {
			IAST list = (IAST) arg;
			int[] result = new int[list.argSize()];
			try {
				IExpr expr;
				for (int i = 1; i < list.size(); i++) {
					expr = list.get(i);
					int intValue = expr.toIntDefault();
					if (intValue == Integer.MIN_VALUE) {
						return null;
					}
					if (intValue <= 0) {
						return null;
					}
					result[i - 1] = intValue;
				}
				return result;
			} catch (RuntimeException rex) {
				if (FEConfig.SHOW_STACKTRACE) {
					rex.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @param arrayRulesList
	 * @param defaultDimension
	 * @param defaultValue
	 *            default value for positions not specified in arrayRulesList. If <code>F.NIL</code> determine from '_'
	 *            (Blank) rule.
	 * @param engine
	 * @return <code>null</code> if a new <code>SparseArrayExpr</code> cannot be created.
	 */
	public static SparseArrayExpr newInstance(IAST arrayRulesList, int defaultDimension, IExpr defaultValue,
			EvalEngine engine) {
		IExpr[] defValue = new IExpr[] { defaultValue };
		final Trie<int[], IExpr> value = Tries.forInts();
		int[] determinedDimension = createTrie(arrayRulesList, value, defaultDimension, defValue, engine);
		if (determinedDimension != null) {
			return new SparseArrayExpr(value, determinedDimension, defValue[0].orElse(F.C0));
		}
		return null;
	}

	private static int[] createTrie(IAST arrayRulesList, final Trie<int[], IExpr> value, int defaultDimension,
			IExpr[] defaultValue, EvalEngine engine) {
		int[] determinedDimension = null;
		IExpr arg1 = arrayRulesList.arg1();
		IAST rule1 = (IAST) arg1;
		int[] positions = null;
		int depth = 1;
		if (rule1.arg1().isList()) {
			IAST positionList = (IAST) rule1.arg1();
			depth = positionList.argSize();
			positions = checkPositions(arrayRulesList, positionList, engine);
			if (positions == null) {
				if (positionList.forAll(x -> x.isBlank())) {
					if (!defaultValue[0].isPresent()) {
						defaultValue[0] = rule1.arg2();
					} else if (!defaultValue[0].equals(rule1.arg2())) {
						// The left hand side of `2` in `1` doesn't match an int-array of depth `3`.
						IOFunctions.printMessage(S.SparseArray, "posr",
								F.List(arrayRulesList, rule1.arg1(), F.ZZ(depth)), engine);
						return null;
					}
				} else {
					// The left hand side of `2` in `1` doesn't match an int-array of depth `3`.
					IOFunctions.printMessage(S.SparseArray, "posr", F.List(arrayRulesList, positionList, F.ZZ(depth)),
							EvalEngine.get());
					return null;
				}
			}
		} else {
			int n = rule1.arg1().toIntDefault();
			if (n <= 0) {
				return null;
			}
			positions = new int[1];
			positions[0] = n;
		}

		if (positions != null) {

			determinedDimension = new int[depth];
			if (defaultDimension > 0) {
				for (int i = 0; i < depth; i++) {
					determinedDimension[i] = defaultDimension;
				}
			} else {
				for (int i = 0; i < depth; i++) {
					if (positions[i] > determinedDimension[i]) {
						determinedDimension[i] = positions[i];
					}
				}
			}

			value.put(positions, rule1.arg2());
		}
		for (int j = 2; j < arrayRulesList.size(); j++) {
			IExpr arg = arrayRulesList.get(j);
			if (arg.isRule()) {
				IAST rule = (IAST) arg;
				if (rule.arg1().isList()) {
					IAST positionList = (IAST) rule.arg1();
					positions = checkPositions(arrayRulesList, positionList, engine);
					if (positions == null) {
						if (positionList.forAll(x -> x.isBlank())) {
							if (!defaultValue[0].isPresent()) {
								defaultValue[0] = rule.arg2();
								continue;
							} else if (!defaultValue[0].equals(rule.arg2())) {
								// The left hand side of `2` in `1` doesn't match an int-array of depth `3`.
								IOFunctions.printMessage(S.SparseArray, "posr",
										F.List(arrayRulesList, rule.arg1(), F.ZZ(depth)), engine);
								return null;
							}
						} else {
							// The left hand side of `2` in `1` doesn't match an int-array of depth `3`.
							IOFunctions.printMessage(S.SparseArray, "posr",
									F.List(arrayRulesList, rule.arg1(), F.ZZ(depth)), engine);
							return null;
						}
					} else {
						if (positions.length != depth) {
							// The left hand side of `2` in `1` doesn't match an int-array of depth `3`.
							IOFunctions.printMessage(S.SparseArray, "posr",
									F.List(arrayRulesList, rule.arg1(), F.ZZ(depth)), engine);
							return null;
						}
						for (int i = 0; i < depth; i++) {
							if (positions[i] > determinedDimension[i]) {
								determinedDimension[i] = positions[i];
							}
						}
						value.putIfAbsent(positions, rule.arg2());
					}
				} else {
					int n = rule.arg1().toIntDefault();
					if (n <= 0) {
						return null;
					}
					positions = new int[1];
					positions[0] = n;
					if (n > determinedDimension[0]) {
						determinedDimension[0] = n;
					}
					value.putIfAbsent(positions, rule.arg2());
				}
			}
		}
		return determinedDimension;
	}

	private transient IAST normalCache = null;

	/**
	 * The dimension of the sparse array.
	 */
	int[] dimension;

	/**
	 * The default value for the positions with no entry in the map. Usually <code>0</code>.
	 */
	IExpr defaultValue;

	/**
	 * Constructor for serialization.
	 */
	public SparseArrayExpr() {
		super(S.SparseArray, null);
	}

	/**
	 * See <a href="https://en.wikipedia.org/wiki/Trie">Wikipedia - Trie</a>.
	 * 
	 * @param trie
	 *            map positions of a sparse array to a value
	 * @param dimension
	 *            the dimensions of the positions
	 * @param defaultValue
	 *            default value for positions not specified in the trie
	 */
	protected SparseArrayExpr(final Trie<int[], IExpr> trie, int[] dimension, IExpr defaultValue) {
		super(S.SparseArray, trie);
		this.dimension = dimension;
		this.defaultValue = defaultValue;
		// this.addEvalFlags(IAST.SEQUENCE_FLATTENED);
	}

	/**
	 * Convert this sparse array to array rules list format.
	 */
	public IAST arrayRules() {
		IASTAppendable result = F.ListAlloc(fData.size() + 1);

		for (TrieNode<int[], IExpr> entry : fData.nodeSet()) {
			int[] key = entry.getKey();
			IExpr value = entry.getValue();
			IAST lhs = F.ast(F.List, key);
			result.append(F.Rule(lhs, value));
		}
		result.append(F.Rule(F.constantArray(F.$b(), dimension.length), defaultValue));
		return result;
	}

	@Override
	public IExpr copy() {
		return new SparseArrayExpr(fData, dimension, defaultValue);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof SparseArrayExpr) {
			SparseArrayExpr s = (SparseArrayExpr) obj;
			if (Arrays.equals(dimension, s.dimension) && //
					defaultValue.equals(s.defaultValue)) {
				Trie<int[], IExpr> sData = s.fData;
				if (fData.size() == sData.size()) {
					for (TrieNode<int[], IExpr> entry : fData.nodeSet()) {
						int[] sequence = entry.getKey();
						IExpr sValue = sData.get(sequence);
						if (sValue == null) {
							return false;
						}
						IExpr value = entry.getValue();
						if (!value.equals(sValue)) {
							return false;
						}
					}
					return true;
				}
			}
		}
		return false;
	}

	public IExpr getDefaultValue() {
		return defaultValue;
	}

	public int[] getDimension() {
		return dimension;
	}

	public IExpr getPart(IAST ast, int startPosition) {
		int[] dims = getDimension();

		if (dims.length >= ast.size() - startPosition) {
			int len = 0;
			int[] partIndex = new int[ast.size() - startPosition];
			int count = 0;
			for (int i = startPosition; i < ast.size(); i++) {
				partIndex[i - startPosition] = ast.get(i).toIntDefault(-1);
				if (partIndex[i - startPosition] == -1) {
					count++;
				}
			}
			if (count == 0 && partIndex.length == dims.length) {
				IExpr temp = fData.get(partIndex);
				if (temp == null) {
					return defaultValue;
				}
				return temp;
			}
			int[] newDimension = new int[count];
			count = 0;
			for (int i = 0; i < partIndex.length; i++) {
				if (partIndex[i] == (-1)) {
					len++;
					newDimension[count++] = dims[i];
				}
			}
			final Trie<int[], IExpr> value = Tries.forInts();
			for (TrieNode<int[], IExpr> entry : fData.nodeSet()) {
				int[] key = entry.getKey();
				boolean evaled = true;
				for (int i = 0; i < partIndex.length; i++) {
					if (partIndex[i] == (-1)) {
						continue;
					}
					if (partIndex[i] != key[i]) {
						evaled = false;
						break;
					}
				}
				if (evaled) {
					int[] newKey = new int[len];
					int j = 0;
					for (int i = 0; i < partIndex.length; i++) {
						if (partIndex[i] == (-1)) {
							newKey[j++] = key[i];
						}
					}
					value.put(newKey, entry.getValue());
				}
			}
			return new SparseArrayExpr(value, newDimension, defaultValue.orElse(F.C0));

		}
		return IOFunctions.printMessage(F.Part, "partd", F.List(ast), EvalEngine.get());

	}

	@Override
	public int hashCode() {
		return (fData == null) ? 541 : 541 + fData.hashCode();
	}

	@Override
	public ISymbol head() {
		return S.SparseArray;
	}

	@Override
	public int hierarchy() {
		return SPARSEARRAYID;
	}

	@Override
	public int[] isMatrix(boolean setMatrixFormat) {
		return (dimension.length == 2) ? dimension : null;
	}

	@Override
	public boolean isSparseArray() {
		return true;
	}

	@Override
	public int isVector() {
		return (dimension.length == 1) ? dimension[0] : -1;
	}

	@Override
	public IAST normal(boolean nilIfUnevaluated) {
		if (dimension.length > 0) {
			if (normalCache != null) {
				return normalCache;
			}
			IASTAppendable list = normalAppendable();
			normalCache = list;
			return normalCache;
		}
		return F.headAST0(S.List);
	}

	private void normal(Trie<int[], IExpr> map, IASTMutable list, int[] dimension, int position, int[] index) {
		if (dimension.length - 1 == position) {
			int size = dimension[position];
			for (int i = 1; i <= size; i++) {
				index[position] = i;
				IExpr expr = map.get(index);
				if (expr == null) {
					list.set(i, defaultValue);
				} else {
					list.set(i, expr);
				}
			}
			return;
		}
		int size1 = dimension[position];
		int size2 = dimension[position + 1];
		for (int i = 1; i <= size1; i++) {
			index[position] = i;
			IASTAppendable currentList = F.ast(S.List, size2, true);
			list.set(i, currentList);
			normal(map, currentList, dimension, position + 1, index);
		}
	}

	private IASTAppendable normalAppendable() {
		IASTAppendable list = F.ast(S.List, dimension[0], true);
		int[] index = new int[dimension.length];
		for (int i = 0; i < index.length; i++) {
			index[i] = 1;
		}
		normal(fData, list, dimension, 0, index);
		return list;
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		defaultValue = (IExpr) in.readObject();
		final int len = in.readInt();
		dimension = new int[len];
		for (int i = 0; i < len; i++) {
			dimension[i] = in.readInt();
		}
		IAST arrayRulesList = (IAST) in.readObject();
		fData = Tries.forInts();
		IExpr[] defValue = new IExpr[] { defaultValue };
		int[] determinedDimension = createTrie(arrayRulesList, fData, -1, defValue, EvalEngine.get());
		if (determinedDimension == null) {
			throw new java.io.InvalidClassException("no valid Trie creation");
		}
	}

	public IExpr set(int i, IExpr value) {
		if (dimension.length == 1 && i > 0 && i <= dimension[0]) {
			int[] positions = new int[1];
			positions[0] = i;
			IExpr old = fData.get(positions);
			fData.put(positions, value);
			return (old == null) ? defaultValue : old;
		}
		throw new IndexOutOfBoundsException("Index: " + i + ", Size: " + dimension[0]);
	}

	@Override
	public int size() {
		return dimension[0];
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("SparseArray(Number of elements: ");
		buf.append(fData.size());
		buf.append(" Dimensions: {");
		for (int i = 0; i < dimension.length; i++) {
			buf.append(dimension[i]);
			if (i < dimension.length - 1) {
				buf.append(",");
			}
		}
		buf.append("})");

		return buf.toString();
	}

	@Override
	public void writeExternal(ObjectOutput output) throws IOException {
		output.writeObject(defaultValue);
		output.writeInt(dimension.length);
		for (int i = 0; i < dimension.length; i++) {
			output.writeInt(dimension[i]);
		}
		IAST rules = arrayRules();
		output.writeObject(rules);
	}

	/**
	 * Determine the sparse array depth from the first entry, which isn't a <code>List(...)</code>.
	 * 
	 * @param list
	 * @param count
	 * @return
	 */
	private static int depth(IAST list, int count) {
		if (list.size() > 1) {
			if (list.arg1().isList()) {
				return depth((IAST) list.arg1(), ++count);
			}
			return count;
		}
		return -1;
	}

	private static boolean arrayRulesRecursive(IAST nestedListsOfValues, int count, int level, IASTMutable positions,
			IASTAppendable result) {
		final int position = count - level;
		level--;
		for (int i = 1; i < nestedListsOfValues.size(); i++) {
			IExpr arg = nestedListsOfValues.get(i);
			positions.set(position, F.ZZ(i));
			if (level == 0) {
				if (arg.isList()) {
					return false;
				}
				if (!arg.isZero()) {
					result.append(F.Rule(positions.copy(), arg));
				}
			} else {
				if (!arg.isList() || //
						!arrayRulesRecursive((IAST) arg, count, level, positions, result)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Create array rules from the nested lists. From array rules a sparse array can be created.
	 * 
	 * @param nestedListsOfValues
	 * @return
	 */
	public static IAST arrayRules(IAST nestedListsOfValues) {
		int depth = SparseArrayExpr.depth((IAST) nestedListsOfValues, 1);
		if (depth < 0) {
			return F.NIL;
		}
		IASTAppendable result = F.ListAlloc();
		IASTMutable positions = F.constantArray(F.C1, depth);
		if (SparseArrayExpr.arrayRulesRecursive((IAST) nestedListsOfValues, depth + 1, depth, positions, result)) {
			result.append(F.Rule(F.constantArray(F.$b(), depth), F.C0));
			return result;
		}
		return F.NIL;
	}
}
