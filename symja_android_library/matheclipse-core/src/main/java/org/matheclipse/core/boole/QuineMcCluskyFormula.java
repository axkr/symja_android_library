package org.matheclipse.core.boole;

/**
 Copyright (c) 2012 the authors listed at the following URL, and/or
 the authors of referenced articles or incorporated external code:
 http://en.literateprograms.org/Quine-McCluskey_algorithm_(Java)?action=history&offset=20110925122251

 Permission is hereby granted, free of charge, to any person obtaining
 a copy of this software and associated documentation files (the
 "Software"), to deal in the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish,
 distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to
 the following conditions:

 The above copyright notice and this permission notice shall be
 included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 Retrieved from: http://en.literateprograms.org/Quine-McCluskey_algorithm_(Java)?oldid=17357
 */

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.exception.BooleanFunctionConversionException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Implementation of the <a
 * href="http://en.wikipedia.org/wiki/Quine%E2%80%93McCluskey_algorithm">Quine
 * McCluskey algorithm</a>.
 */
public class QuineMcCluskyFormula {

	private List<QuineMcCluskyTerm> termList;
	private List<QuineMcCluskyTerm> originalTermList;
	private final IAST variables;

	public QuineMcCluskyFormula(List<QuineMcCluskyTerm> termList, IAST variables) {
		this.termList = termList;
		this.variables = variables;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(termList.size() + " terms, " + termList.get(0).getNumVars() + " variables\n");
		for (int i = 0; i < termList.size(); i++) {
			result.append(termList.get(i) + "\n");
		}
		return result.toString();
	}

	public IExpr toExpr() throws BooleanFunctionConversionException {
		IAST result = F.ast(F.Or);
		for (int i = 0; i < termList.size(); i++) {
			result.append(termList.get(i).toExpr(variables));
		}
		if (result.isAST0()) {
			return F.True;
		}
		if (result.isAST1()) {
			return result.arg1();
		}
		return result;
	}

	public void reduceToPrimeImplicants() {
		originalTermList = new ArrayList<QuineMcCluskyTerm>(termList);
		int numVars = termList.get(0).getNumVars();
		ArrayList<QuineMcCluskyTerm>[][] table = new ArrayList[numVars + 1][numVars + 1];
		for (int dontKnows = 0; dontKnows <= numVars; dontKnows++) {
			for (int ones = 0; ones <= numVars; ones++) {
				table[dontKnows][ones] = new ArrayList<QuineMcCluskyTerm>();
			}
		}
		for (int i = 0; i < termList.size(); i++) {
			int dontCares = termList.get(i).countValues(QuineMcCluskyTerm.NIL);
			int ones = termList.get(i).countValues((byte) 1);
			table[dontCares][ones].add(termList.get(i));
		}
		for (int dontKnows = 0; dontKnows <= numVars - 1; dontKnows++) {
			for (int ones = 0; ones <= numVars - 1; ones++) {
				ArrayList<QuineMcCluskyTerm> left = table[dontKnows][ones];
				ArrayList<QuineMcCluskyTerm> right = table[dontKnows][ones + 1];
				ArrayList<QuineMcCluskyTerm> out = table[dontKnows + 1][ones];
				for (int leftIdx = 0; leftIdx < left.size(); leftIdx++) {
					for (int rightIdx = 0; rightIdx < right.size(); rightIdx++) {
						QuineMcCluskyTerm combined = left.get(leftIdx).combine(right.get(rightIdx));
						if (combined != null) {
							if (!out.contains(combined)) {
								out.add(combined);
							}
							termList.remove(left.get(leftIdx));
							termList.remove(right.get(rightIdx));
							if (!termList.contains(combined)) {
								termList.add(combined);
							}
						}
					}
				}
			}
		}
	}

	public void reducePrimeImplicantsToSubset() {
		int numPrimeImplicants = termList.size();
		int numOriginalTerms = originalTermList.size();
		boolean[][] table = new boolean[numPrimeImplicants][numOriginalTerms];
		for (int impl = 0; impl < numPrimeImplicants; impl++) {
			for (int term = 0; term < numOriginalTerms; term++) {
				table[impl][term] = termList.get(impl).implies(originalTermList.get(term));
			}
		}
		ArrayList<QuineMcCluskyTerm> newTermList = new ArrayList<QuineMcCluskyTerm>();
		boolean done = false;
		int impl;
		while (!done) {
			impl = extractEssentialImplicant(table);
			if (impl != -1) {
				newTermList.add(termList.get(impl));
			} else {
				impl = extractLargestImplicant(table);
				if (impl != -1) {
					newTermList.add(termList.get(impl));
				} else {
					done = true;
				}
			}
		}
		termList = newTermList;
		originalTermList = null;
	}

	public static QuineMcCluskyFormula read(Reader reader) throws IOException {
		ArrayList<QuineMcCluskyTerm> terms = new ArrayList<QuineMcCluskyTerm>();
		QuineMcCluskyTerm term;
		while ((term = QuineMcCluskyTerm.read(reader)) != null) {
			terms.add(term);
		}
		return new QuineMcCluskyFormula(terms, null);
	}

	public static QuineMcCluskyFormula read(String str) {
		ArrayList<QuineMcCluskyTerm> terms = QuineMcCluskyTerm.read(str);
		return new QuineMcCluskyFormula(terms, null);
	}

	public static QuineMcCluskyFormula read(IAST orAST) throws BooleanFunctionConversionException {
		VariablesSet exVar = new VariablesSet(orAST);
		IAST vars = exVar.getVarList();
		if (vars.isAST0()) {
			throw new BooleanFunctionConversionException();
		}
		ArrayList<QuineMcCluskyTerm> terms = QuineMcCluskyTerm.convertToTerms(orAST, vars);
		return new QuineMcCluskyFormula(terms, vars);
	}

	private int extractEssentialImplicant(boolean[][] table) {
		for (int term = 0; term < table[0].length; term++) {
			int lastImplFound = -1;
			for (int impl = 0; impl < table.length; impl++) {
				if (table[impl][term]) {
					if (lastImplFound == -1) {
						lastImplFound = impl;
					} else {
						// This term has multiple implications
						lastImplFound = -1;
						break;
					}
				}
			}
			if (lastImplFound != -1) {
				extractImplicant(table, lastImplFound);
				return lastImplFound;
			}
		}
		return -1;
	}

	private void extractImplicant(boolean[][] table, int impl) {
		for (int term = 0; term < table[0].length; term++) {
			if (table[impl][term]) {
				for (int impl2 = 0; impl2 < table.length; impl2++) {
					table[impl2][term] = false;
				}
			}
		}
	}

	private int extractLargestImplicant(boolean[][] table) {
		int maxNumTerms = 0;
		int maxNumTermsImpl = -1;
		for (int impl = 0; impl < table.length; impl++) {
			int numTerms = 0;
			for (int term = 0; term < table[0].length; term++) {
				if (table[impl][term]) {
					numTerms++;
				}
			}
			if (numTerms > maxNumTerms) {
				maxNumTerms = numTerms;
				maxNumTermsImpl = impl;
			}
		}
		if (maxNumTermsImpl != -1) {
			extractImplicant(table, maxNumTermsImpl);
			return maxNumTermsImpl;
		}
		return -1;
	}

}