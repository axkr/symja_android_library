package org.matheclipse.core.expression;

import org.apache.commons.math4.util.MathUtils;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;
import org.matheclipse.core.visit.IVisitorLong;

/**
 * <code>INum</code> implementation which wraps a <code>double</code> value to represent a numeric floating-point number.
 */
public class Num extends ExprImpl implements INum {
	/**
	 * 
	 */
	private static final long serialVersionUID = 188084692735007429L;

	
	double fDouble;

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

	@Override
	public int hierarchy() {
		return DOUBLEID;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isNumEqualInteger(IInteger value) throws ArithmeticException {
		return F.isNumEqualInteger(fDouble, value);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isNumEqualRational(IRational value) throws ArithmeticException {
		return F.isNumEqualRational(fDouble, value);
	}
	
	/** {@inheritDoc} */
	@Override
	public boolean isNumIntValue() {
		return F.isNumIntValue(fDouble);
		// return fDouble == Math.floor(fDouble);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isNegative() {
		return fDouble < 0.0;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isPositive() {
		return fDouble > 0.0;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isRationalValue(IRational value){
		return F.isZero(fDouble - value.doubleValue()); 
	}
	
	@Override
	public boolean equalsInt(final int i) {
		return fDouble == i;
	}

	@Override
	public IExpr evaluate(EvalEngine engine) {
		if (engine.isNumericMode() && engine.isApfloat()) {
			return ApfloatNum.valueOf(fDouble, engine.getNumericPrecision());
		}
		return null;
	}

	@Override
	public INum add(final INum val) {
		return valueOf(fDouble + val.getRealPart());
	}

	@Override
	public INum multiply(final INum val) {
		return valueOf(fDouble * val.getRealPart());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.matheclipse.parser.interfaces.IDouble#pow(org.matheclipse.parser. interfaces .IDouble)
	 */
	@Override
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

	/** {@inheritDoc} */
	@Override
	public Num eabs() {
		return newInstance(Math.abs(fDouble));
	}

	/** {@inheritDoc} */
	@Override
	public int compareAbsValueToOne() {
		double temp = Math.abs(fDouble);
		return Double.compare(temp, 1.0);
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
		if (that instanceof ApfloatNum) {
			return add(ApfloatNum.valueOf(fDouble, ((ApfloatNum) that).fApfloat.precision()));
		}
		if (that instanceof Num) {
			return newInstance(fDouble + ((Num) that).fDouble);
		}
		if (that instanceof ApcomplexNum) {
			return ApcomplexNum.valueOf(fDouble, ((ApcomplexNum) that).fApcomplex.precision()).add((ApcomplexNum) that);
		}
		if (that instanceof ComplexNum) {
			return ComplexNum.valueOf(fDouble).add((ComplexNum) that);
		}
		return super.plus(that);
	}

	@Override
	public ISignedNumber divideBy(ISignedNumber that) {
		return Num.valueOf(doubleValue() / that.doubleValue());
	}

	@Override
	public ISignedNumber subtractFrom(ISignedNumber that) {
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
	@Override
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
	public final int hashCode() {
		return MathUtils.hash(fDouble);
	}

	/**
	 * @return
	 */
	@Override
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
		if (that instanceof ApfloatNum) {
			return multiply(ApfloatNum.valueOf(fDouble, ((ApfloatNum) that).fApfloat.precision()));
		}
		if (that instanceof Num) {
			return newInstance(fDouble * ((Num) that).fDouble);
		}
		if (that instanceof ApcomplexNum) {
			return ApcomplexNum.valueOf(fDouble, ((ApcomplexNum) that).fApcomplex.precision()).multiply((ApcomplexNum) that);
		}
		if (that instanceof ComplexNum) {
			return ComplexNum.valueOf(fDouble).multiply((ComplexNum) that);
		}
		return super.times(that);
	}

	/**
	 * @return
	 */
	@Override
	public ISignedNumber negate() {
		return newInstance(-fDouble);
	}

	/**
	 * @return
	 */
	@Override
	public ISignedNumber opposite() {
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
	public ISignedNumber inverse() {
		if (isOne()) {
			return this;
		}
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

	@Override
	public double getRealPart() {
		double temp = fDouble;
		if (temp == (-0.0)) {
			temp = 0.0;
		}
		return temp;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isE() {
		return F.isZero(fDouble - Math.E);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isOne() {
		return F.isZero(fDouble - 1.0);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isMinusOne() {
		return F.isZero(fDouble + 1.0);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isPi() {
		return F.isZero(fDouble - Math.PI);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isZero() {
		return F.isZero(fDouble);
	}

	@Override
	public IInteger round() {
		return F.integer(NumberUtil.toLong(Math.rint(fDouble)));
	}

	@Override
	public int sign() {
		return (int) Math.signum(fDouble);
	}

	/** {@inheritDoc} */
	@Override
	public int complexSign() {
		return sign();
	}

	/** {@inheritDoc} */
	@Override
	public IInteger ceil() {
		return F.integer(NumberUtil.toLong(Math.ceil(fDouble)));
	}

	/** {@inheritDoc} */
	@Override
	public IInteger floor() {
		return F.integer(NumberUtil.toLong(Math.floor(fDouble)));
	}

	/**
	 * Compares this expression with the specified expression for order. Returns a negative integer, zero, or a positive integer as
	 * this expression is canonical less than, equal to, or greater than the specified expression.
	 */
	@Override
	public int compareTo(final IExpr expr) {
		if (expr instanceof Num) {
			return Double.compare(fDouble, ((Num) expr).fDouble);
		}
		return super.compareTo(expr);
	}

	@Override
	public boolean isLessThan(ISignedNumber that) {
		return fDouble < that.doubleValue();
	}

	@Override
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
	@Override
	public <T> T accept(IVisitor<T> visitor) {
		return visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean accept(IVisitorBoolean visitor) {
		return visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int accept(IVisitorInt visitor) {
		return visitor.visit(this);
	}

	/** {@inheritDoc} */
	@Override
	public long accept(IVisitorLong visitor) {
		return visitor.visit(this);
	}

	/** {@inheritDoc} */
	@Override
	public ISignedNumber getIm() {
		return F.CD0;
	}

	/** {@inheritDoc} */
	@Override
	public ISignedNumber getRe() {
		return this;
	}

	@Override
	public ApfloatNum apfloatNumValue(long precision) {
		return ApfloatNum.valueOf(fDouble, precision);
	}

	@Override
	public Num numValue() {
		return this;
	}

	public Apcomplex apcomplexValue(long precision) {
		return new Apcomplex(new Apfloat(fDouble, precision));
	}

	@Override
	public ApcomplexNum apcomplexNumValue(long precision) {
		return ApcomplexNum.valueOf(apcomplexValue(precision));
	}

	@Override
	public ComplexNum complexNumValue() {
		// double precision complex number
		return ComplexNum.valueOf(doubleValue(), 0.0);
	}
}