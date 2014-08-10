package org.matheclipse.core.expression;

import org.matheclipse.core.eval.exception.DimensionException;
import org.matheclipse.core.generic.BinaryMap;

public class Vector extends ASTDelegate {
	public Vector(AST ast) {
		super(ast);
	}

	public Vector(int size) {
		super();
		fAst = createAST(size);
	}

	public Vector(int[] values) {
		super();
		fAst = createAST(values);
	}

	public Vector plus(final Vector that) {
		if (that.size() != fAst.size()) {
			throw new DimensionException("Vector#plus([" + fAst.size() + "],[" + that.size() + "])");
		}
		AST resultAST = createAST(fAst.size() - 1);
		fAst.map(resultAST, that.fAst, new BinaryMap(F.Plus()));
		return new Vector(resultAST);
	}

	public Vector minus(final Vector that) {
		if (that.size() != fAst.size()) {
			throw new DimensionException("Vector#minus([" + fAst.size() + "],[" + that.size() + "])");
		}
		return null;
	}

	public Vector multiply(final Vector that) {
		if (that.size() != fAst.size()) {
			throw new DimensionException("Vector#multiply([" + fAst.size() + "],[" + that.size() + "])");
		}
		return null;
	}

	public Vector power(final Integer n) {
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof Vector) {
			return fAst.equals(((Vector) obj).fAst);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return fAst.hashCode() * 61;
	}
}
