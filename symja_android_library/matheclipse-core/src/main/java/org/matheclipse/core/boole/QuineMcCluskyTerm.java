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
import java.util.Arrays;

import org.matheclipse.core.eval.exception.BooleanFunctionConversionException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * A term of the QuineMcCluskyFormula
 * 
 */
class QuineMcCluskyTerm {
	public static final byte NIL = 2;

	private byte[] varVals;

	public QuineMcCluskyTerm(byte[] varVals) {
		this.varVals = varVals;
	}

	public int getNumVars() {
		return varVals.length;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("{");
		for (int i = 0; i < varVals.length; i++) {
			if (varVals[i] == NIL) {
				result.append("X");
			} else {
				result.append(varVals[i]);
			}
			result.append(" ");
		}
		result.append("}");
		return result.toString();
	}

	public IExpr toExpr(IAST variables) throws BooleanFunctionConversionException {
		int length = varVals.length;
		if (length == 1) {
			if (varVals[0] == NIL) {
				// nothing to do
			} else if (varVals[0] == (byte) 0) {
				return F.Not(variables.arg1());
			} else if (varVals[0] == (byte) 1) {
				return variables.arg1();
			} else {
				throw new BooleanFunctionConversionException();
			}
		}
		IASTAppendable result = F.ast(F.And, length, false);
		for (int i = 0; i < length; i++) {
			if (varVals[i] == NIL) {
				// nothing to do
			} else if (varVals[i] == (byte) 0) {
				result.append(F.Not(variables.get(i + 1)));
			} else if (varVals[i] == (byte) 1) {
				result.append(variables.get(i + 1));
			} else {
				throw new BooleanFunctionConversionException();
			}
		}
		if (result.isAST0()) {
			return F.False;
		}
		if (result.isAST1()) {
			return result.arg1();
		}
		return result;
	}

	public QuineMcCluskyTerm combine(QuineMcCluskyTerm term) {
		int diffVarNum = -1; // The position where they differ
		for (int i = 0; i < varVals.length; i++) {
			if (this.varVals[i] != term.varVals[i]) {
				if (diffVarNum == -1) {
					diffVarNum = i;
				} else {
					// They're different in at least two places
					return null;
				}
			}
		}
		if (diffVarNum == -1) {
			// They're identical
			return null;
		}
		byte[] resultVars = varVals.clone();
		resultVars[diffVarNum] = NIL;
		return new QuineMcCluskyTerm(resultVars);
	}

	public int countValues(byte value) {
		int result = 0;
		for (int i = 0; i < varVals.length; i++) {
			if (varVals[i] == value) {
				result++;
			}
		}
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (o == null || !getClass().equals(o.getClass())) {
			return false;
		} else {
			QuineMcCluskyTerm rhs = (QuineMcCluskyTerm) o;
			return Arrays.equals(this.varVals, rhs.varVals);
		}
	}

	@Override
	public int hashCode() {
		return java.util.Arrays.hashCode(varVals);
	}

	boolean implies(QuineMcCluskyTerm term) {
		for (int i = 0; i < varVals.length; i++) {
			if (this.varVals[i] != NIL && this.varVals[i] != term.varVals[i]) {
				return false;
			}
		}
		return true;
	}

	public static QuineMcCluskyTerm read(Reader reader) throws IOException {
		int c = '\0';
		ArrayList<Byte> t = new ArrayList<Byte>();
		while (c != '\n' && c != -1) {
			c = reader.read();
			if (c == '0') {
				t.add((byte) 0);
			} else if (c == '1') {
				t.add((byte) 1);
			}
		}
		if (!t.isEmpty()) {
			byte[] resultBytes = new byte[t.size()];
			for (int i = 0; i < t.size(); i++) {
				resultBytes[i] = t.get(i);
			}
			return new QuineMcCluskyTerm(resultBytes);
		} else {
			return null;
		}
	}

	public static ArrayList<QuineMcCluskyTerm> read(String str) {
		ArrayList<QuineMcCluskyTerm> terms = new ArrayList<QuineMcCluskyTerm>();
		int j = 0;

		ArrayList<Byte> t;
		do {
			t = new ArrayList<Byte>();
			char c = '\0';
			while (c != '\n' && j < str.length()) {
				c = str.charAt(j++);
				if (c == '0') {
					t.add((byte) 0);
				} else if (c == '1') {
					t.add((byte) 1);
				}
			}
			if (!t.isEmpty()) {
				addBytes(terms, t);
			}
		} while (!t.isEmpty());
		return terms;
	}

	public static ArrayList<QuineMcCluskyTerm> convertToTerms(IAST orAST, final IAST vars)
			throws BooleanFunctionConversionException {
		ArrayList<QuineMcCluskyTerm> terms = new ArrayList<QuineMcCluskyTerm>();
		IExpr temp;
		IExpr a;

		for (int i = 1; i < orAST.size(); i++) {
			temp = orAST.get(i);
			ArrayList<Byte> t = new ArrayList<Byte>();
			for (int j = 1; j < vars.size(); j++) {
				t.add(NIL);
			}
			if (temp.isNot()) {
				addAndElement(vars, temp, t);
			} else if (temp.isSymbol()) {
				addAndElement(vars, temp, t);
			} else if (temp.isAST(F.And)) {
				IAST andAST = (IAST) temp;
				for (int j = 1; j < andAST.size(); j++) {
					a = andAST.get(j);
					addAndElement(vars, a, t);
				}
			} else {
				throw new BooleanFunctionConversionException();
			}
			if (!t.isEmpty()) {
				addBytes(terms, t);
			}
		}
		return terms;
	}

	public static void addAndElement(final IAST vars, IExpr a, ArrayList<Byte> t)
			throws BooleanFunctionConversionException {
		if (a.isNot()) {
			IExpr arg1 = a.first();
			if (!arg1.isSymbol()) {
				throw new BooleanFunctionConversionException();
			}
			for (int j2 = 1; j2 < vars.size(); j2++) {
				if (arg1.equals(vars.get(j2))) {
					t.set(j2 - 1, (byte) 0);
					return;
				}
			}
		} else if (a.isSymbol()) {
			for (int j2 = 1; j2 < vars.size(); j2++) {
				if (a.equals(vars.get(j2))) {
					t.set(j2 - 1, (byte) 1);
					return;
				}
			}
		}
		throw new BooleanFunctionConversionException();
	}

	private static void addBytes(ArrayList<QuineMcCluskyTerm> terms, final ArrayList<Byte> t) {
		QuineMcCluskyTerm term;
		byte[] resultBytes = new byte[t.size()];
		for (int j3 = 0; j3 < t.size(); j3++) {
			if (t.get(j3).equals(NIL)) {
				// recursive call
				t.set(j3, (byte) 0);
				addBytes(terms, t);
				t.set(j3, (byte) 1);
				addBytes(terms, t);
				return;
			}
			resultBytes[j3] = t.get(j3);
		}
		term = new QuineMcCluskyTerm(resultBytes);
		terms.add(term);
	}

}
