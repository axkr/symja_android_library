package org.matheclipse.core.expression;

import java.io.Reader;
import java.math.BigInteger;
import java.util.List;
import java.util.Random;

import org.matheclipse.core.interfaces.IExpr;

import edu.jas.structure.RingFactory;

/**
 * Singleton ring factory class. Defines tests for field and query of
 * characteristic.
 * 
 */
public class ExprRingFactory implements RingFactory<IExpr> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6146597389011632638L;

	public final static ExprRingFactory CONST = new ExprRingFactory();

	private ExprRingFactory() {
		super();
	}

	/**
	 * Query if this ring is a field.
	 * 
	 * @return true.
	 */
	@Override
	public boolean isField() {
		return false;
	}

	/**
	 * Characteristic of this ring.
	 * 
	 * @return characteristic of this ring.
	 */
	@Override
	public java.math.BigInteger characteristic() {
		return java.math.BigInteger.ZERO;
	}

	/**
	 * Get a BigRational element from a long.
	 * 
	 * @param a
	 *            long.
	 * @return BigRational from a.
	 */
	@Override
	public IExpr fromInteger(long a) {
		return F.integer(a);
	}

	/**
	 * Get a BigRational element from a long.
	 * 
	 * @param a
	 *            long.
	 * @return BigRational from a.
	 */
	public static IExpr valueOf(long a) {
		return F.integer(a);
	}

	@Override
	public IExpr getZERO() {
		return F.C0;
	}

	@Override
	public IExpr copy(IExpr c) {
		return null;
	}

	@Override
	public IExpr fromInteger(BigInteger a) {
		return F.integer(a);
	}

	@Override
	public List<IExpr> generators() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isFinite() {
		return false;
	}

	@Override
	public IExpr parse(String s) {
		return F.Null;
	}

	@Override
	public IExpr parse(Reader r) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IExpr random(int n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IExpr random(int n, Random random) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toScript() {
		return "ExprRingFactory";
	}

	@Override
	public IExpr getONE() {
		return F.C1;
	}

	@Override
	public boolean isAssociative() {
		return true;
	}

	@Override
	public boolean isCommutative() {
		return true;
	}
}
