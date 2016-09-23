package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

public class KOrderlessPartitions extends AbstractFunctionEvaluator {

	public KOrderlessPartitions() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);
		if (ast.arg1().isAST() && ast.arg2().isInteger()) {
			final IAST listArg0 = (IAST) ast.arg1();
			final ISymbol sym = listArg0.topHead();
			final int n = listArg0.size() - 1;
			final int k = ((IInteger) ast.arg2()).toBigNumerator().intValue();
			final IAST result = F.ast(F.List);
			final Permutations.KPermutationsIterable permutationIterator = new Permutations.KPermutationsIterable(
					listArg0, n, 1);
			final KPartitions.KPartitionsIterable partitionIterator = new KPartitions.KPartitionsIterable(n, k);
			IAST partition;

			// first generate all permutations:
			for (int permutationsIndex[] : permutationIterator) {
				// second generate all partitions:
				for (int partitionsIndex[] : partitionIterator) {
					partition = createSinglePartition(listArg0, sym, permutationsIndex, partitionsIndex);
					if (partition.isPresent()) {
						result.append(partition);
					}
				}
				partitionIterator.reset();
			}
			return result;
		}
		return F.NIL;
	}

	private IAST createSinglePartition(final IAST listArg0, final ISymbol sym, final int[] permutationsIndex,
			final int[] partitionsIndex) {
		IAST partition;
		IAST partitionElement;
		int partitionStartIndex;
		partition = F.List();

		final int n = listArg0.size() - 1;
		// 0 is always the first index of a partition
		partitionStartIndex = 0;
		for (int i = 1; i < partitionsIndex.length; i++) {
			partitionElement = F.ast(sym);
			if (partitionStartIndex + 1 == partitionsIndex[i]) {
				// OneIdentity check here
				if (sym.hasOneIdentityAttribute()) {
					partition.append(listArg0.get(permutationsIndex[partitionStartIndex] + 1));
				} else {
					partitionElement.append(listArg0.get(permutationsIndex[partitionStartIndex] + 1));
					partition.append(partitionElement);
				}
			} else {
				for (int m = partitionStartIndex; m < partitionsIndex[i]; m++) {
					if (m + 1 < partitionsIndex[i]) {
						if ((listArg0.get(permutationsIndex[m + 1] + 1))
								.isLTOrdered(listArg0.get(permutationsIndex[m] + 1))) {
							return F.NIL;
						}
					}
					partitionElement.append(listArg0.get(permutationsIndex[m] + 1));
				}
				partition.append(partitionElement);
			}
			partitionStartIndex = partitionsIndex[i];

		}
		// generate all elements for the last partitionElement of a partition:
		partitionElement = F.ast(sym);
		if (partitionStartIndex + 1 == n) {
			// OneIdentity check here
			if ((sym.getAttributes() & ISymbol.ONEIDENTITY) == ISymbol.ONEIDENTITY) {
				partition.append(listArg0.get(permutationsIndex[partitionStartIndex] + 1));
			} else {
				partitionElement.append(listArg0.get(permutationsIndex[partitionStartIndex] + 1));
				partition.append(partitionElement);
			}
		} else {
			for (int m = partitionStartIndex; m < n; m++) {
				if (m + 1 < n) {
					if ((listArg0.get(permutationsIndex[m + 1] + 1))
							.isLTOrdered(listArg0.get(permutationsIndex[m] + 1))) {
						return F.NIL;
					}
				}
				partitionElement.append(listArg0.get(permutationsIndex[m] + 1));
			}
			partition.append(partitionElement);
		}

		return partition;
	}

}
