package org.matheclipse.core.expression;

import java.math.BigInteger;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;

/**
 * 
 */
public class Num extends ExprImpl implements INum {
	/**
	 * 
	 */
	private static final long serialVersionUID = 188084692735007429L;

	double fDouble;

	// Double d;

	// private static final ObjectFactory<Num> FACTORY = new ObjectFactory<Num>()
	// {
	// @Override
	// protected Num create() {
	// if (Config.SERVER_MODE && currentQueue().getSize() >=
	// Config.DOUBLE_MAX_POOL_SIZE) {
	// throw new PoolMemoryExceededException("DoubleImpl",
	// currentQueue().getSize());
	// }
	// return new Num(0.0);
	// }
	//
	// };

	/**
	 * Be cautious with this method, no new internal rational is created
	 * 
	 * @param numerator
	 * @return
	 */
	protected static Num newInstance(final double value) {
		Num d;
		// if (Config.SERVER_MODE) {
		// d = FACTORY.object();
		// } else {
		d = new Num(0.0);
		// }
		d.fDouble = value;
		return d;
	}

	// public DoubleImpl(final double value) {
	// fDouble = value;
	// }

	private Num(final double value) {
		fDouble = value;
	}

	public int hierarchy() {
		return DOUBLEID;
	}

	public boolean isNumIntValue() {
		return fDouble == Math.floor(fDouble);
	}

	public boolean isNegative() {
		return fDouble < 0.0;
	}

	public boolean isPositive() {
		return fDouble > 0.0;
	}

	public boolean equalsInt(final int i) {
		return fDouble == i;
	}

	public INum add(final INum val) {
		return valueOf(fDouble + val.getRealPart());
	}

	public INum multiply(final INum val) {
		return valueOf(fDouble * val.getRealPart());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.matheclipse.parser.interfaces.IDouble#pow(org.matheclipse.parser.interfaces
	 * .IDouble)
	 */
	public INum pow(final INum val) {
		return valueOf(Math.pow(fDouble, val.getRealPart()));
	}

	// protected static final XmlFormat FRACTION_XML = new
	// XmlFormat(DoubleImpl.class) {
	// public void format(Object obj, XmlElement xml) {
	// DoubleImpl expr = (DoubleImpl) obj;
	// xml.setAttribute("val", String.valueOf(expr.fValue));
	// }
	//
	// public Object parse(XmlElement xml) {
	// DoubleImpl expr = (DoubleImpl) xml.object();
	// expr.fValue = Double.parseDouble(xml.getAttribute("val", ""));
	// return expr;
	// }
	// };
	//

	/**
	 * @param chars
	 * @return
	 */
	public static double valueOf(final String chars) {
		return Double.parseDouble(chars);
	}

	/**
	 * @param doubleValue
	 * @return
	 */
	public static Num valueOf(final double doubleValue) {
		return newInstance(doubleValue);
	}

	/**
	 * @return
	 */
	public Num eabs() {
		return newInstance(Math.abs(fDouble));
	}

	/**
	 * @param that
	 * @return
	 */
	public double plus(final double that) {
		return fDouble + that;
	}

	@Override
	public IExpr plus(final IExpr that) {
		if (that instanceof Num) {
			return add((Num) that);
		}
		return super.plus(that);
	}

	public ISignedNumber minus(ISignedNumber that) {
		return Num.valueOf(doubleValue() - that.doubleValue());
	}

	/**
	 * @return
	 */
	// public byte byteValue() {
	// return fDouble.byteValue();
	// }
	/**
	 * @param that
	 * @return
	 */
	public int compareTo(final double that) {
		return Double.compare(fDouble, that);
	}

	/**
	 * @param that
	 * @return
	 */
	public double divide(final double that) {
		return fDouble / that;
	}

	/**
	 * @return
	 */
	public double doubleValue() {
		return fDouble;
	}

	@Override
	public boolean equals(final Object arg0) {
		if (this == arg0) {
			return true;
		}
		if (arg0 instanceof Num) {
			return fDouble == ((Num) arg0).fDouble;
		}
		return false;
	}

	@Override
	public boolean isSame(IExpr expression, double epsilon) {
		if (expression instanceof Num) {
			return F.isZero(fDouble - ((Num) expression).fDouble, epsilon);
		}
		return false;
	}

	public double exp() {
		return Math.exp(fDouble);
	}

	// public float floatValue() {
	// return fDouble.floatValue();
	// }

	@Override
	public int hashCode() {
		Double d;
		return Double.valueOf(fDouble).hashCode();
	}

	/**
	 * @return
	 */
	public int intValue() {
		return Double.valueOf(fDouble).intValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int toInt() throws ArithmeticException {
		return NumberUtil.toInt(fDouble);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long toLong() throws ArithmeticException {
		return NumberUtil.toLong(fDouble);
	}
	
	/**
	 * @return
	 */
	public boolean isInfinite() {
		return Double.isInfinite(fDouble);
	}

	/**
	 * @return
	 */
	public boolean isNaN() {
		return Double.isNaN(fDouble);
	}

	/**
	 * @return
	 */
	public double log() {
		return Math.log(fDouble);
	}

	/**
	 * @return
	 */
	public long longValue() {
		return Double.valueOf(fDouble).longValue();
	}

	/**
	 * @param cs
	 */
	// @Override
	// public boolean move(final ObjectSpace os) {
	// if (super.move(os)) {
	// fDouble.move(os);
	// return true;
	// }
	// return false;
	// }
	// public Num copy() {
	// Num d;
	// // if (Config.SERVER_MODE) {
	// // d = FACTORY.object();
	// // } else {
	// d = new Num(0.0);
	// // }
	// d.fDouble = fDouble.copy();
	// return d;
	// }
	//
	// public Num copyNew() {
	// Num di = new Num(0.0);
	// di.fDouble = new double(fDouble);
	// return di;
	// }
	// @Override
	// public void recycle() {
	// FACTORY.recycle(this);
	// }
	/**
	 * @param that
	 * @return
	 */
	public double times(final double that) {
		return fDouble * that;
	}

	@Override
	public IExpr times(final IExpr that) {
		if (that instanceof Num) {
			return newInstance(fDouble * ((Num) that).fDouble);
		}
		return super.times(that);
	}

	/**
	 * @return
	 */
	public ISignedNumber negate() {
		return newInstance(-fDouble);
	}

	/**
	 * @return
	 */
	@Override
	public IExpr opposite() {
		return newInstance(-fDouble);
	}

	/**
	 * @param that
	 * @return
	 */
	// public Operable plus(Operable that) {
	// return fDouble.plus(that);
	// }
	/**
	 * @param that
	 * @return
	 */
	public double pow(final double that) {
		return Math.pow(fDouble, that);
	}

	/**
	 * @param exp
	 * @return
	 */
	public double pow(final int exp) {
		return Math.pow(fDouble, exp);
	}

	@Override
	public IExpr inverse() {
		return newInstance(1 / fDouble);
	}

	/**
	 * @return
	 */
	// public short shortValue() {
	// return fDouble.shortValue();
	// }
	/**
	 * @return
	 */
	public double sqrt() {
		return Math.sqrt(fDouble);
	}

	/**
	 * @param that
	 * @return
	 */
	public double minus(final double that) {
		return fDouble - that;
	}

	/**
	 * @return
	 */
	// public Text toText() {
	// return fDouble.toText();
	// }
	public double getRealPart() {
		double temp = fDouble;
		if (temp == (-0.0)) {
			temp = 0.0;
		}
		return temp;
	}

	@Override
	public boolean isZero() {
		return F.isZero(fDouble);
		// return fDouble == 0.0;
	}

	@Override
	public boolean isOne() {
		return F.isZero(fDouble - 1.0);
	}

	@Override
	public boolean isMinusOne() {
		return F.isZero(fDouble + 1.0);
	}

	public ISignedNumber round() {
		return valueOf(Math.rint(fDouble));
	}

	public int sign() {
		return (int) Math.signum(fDouble);
	}

	public int complexSign() {
		return sign();
	}

	public ISignedNumber ceil() {
		return valueOf(Math.ceil(fDouble));
	}

	public ISignedNumber floor() {
		return valueOf(Math.floor(fDouble));
	}

	/**
	 * Compares this expression with the specified expression for order. Returns a
	 * negative integer, zero, or a positive integer as this expression is
	 * canonical less than, equal to, or greater than the specified expression.
	 */
	public int compareTo(final IExpr obj) {
		if (obj instanceof Num) {
			return Double.compare(fDouble, ((Num) obj).fDouble);
		}
		return (hierarchy() - (obj).hierarchy());
	}

	public boolean isLessThan(ISignedNumber that) {
		return fDouble < that.doubleValue();
	}

	public boolean isGreaterThan(ISignedNumber that) {
		return fDouble > that.doubleValue();
	}

	@Override
	public ISymbol head() {
		return F.RealHead;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		if (fDouble == (-0.0)) {
			return "0.0";
		}
		return Double.valueOf(fDouble).toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> T accept(IVisitor<T> visitor) {
		return visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean accept(IVisitorBoolean visitor) {
		return visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public int accept(IVisitorInt visitor) {
		return visitor.visit(this);
	}
}