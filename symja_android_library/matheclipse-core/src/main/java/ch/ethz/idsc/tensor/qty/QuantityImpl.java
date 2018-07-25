package ch.ethz.idsc.tensor.qty;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.util.Objects;
import java.util.Set;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.AbstractAST;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.parser.client.math.MathException;

public class QuantityImpl extends AbstractAST implements IQuantity, Externalizable {
	/**
	 * @param value
	 *            is assumed to be not instance of {@link IQuantity}
	 * @param unit
	 * @return
	 */
	/* package */ static IExpr of(IExpr value, IUnit unit) {
		return Units.isOne(unit) ? value : new QuantityImpl(value, null, unit);
	}

	private IExpr arg1;

	private String unitString;
	private IUnit unit; // not Unit.ONE

	private QuantityImpl(IExpr value, String unitString, IUnit unit) {
		this.arg1 = value;
		this.unitString = unitString;
		this.unit = unit;
	}

	@Override
	public IExpr abs() {
		return ofUnit(arg1.abs());
	}

	public IExpr arcTan(IExpr x) {
		if (x instanceof IQuantity) {
			IQuantity quantity = (IQuantity) x;
			if (unit.equals(quantity.unit()))
				return F.ArcTan.of(quantity.value(), arg1);
		}
		throw MathException.of(x, this);
	}

	public IExpr arg() {
		return F.Arg.of(arg1);
	}

	@Override
	public IExpr arg1() {
		return arg1;
	}

	@Override
	public IExpr arg2() {
		return F.stringx(unit.toString());
	}

	@Override
	public IExpr arg3() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IExpr arg4() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IExpr arg5() {
		throw new UnsupportedOperationException();
	}

	// public Number number() {
	// return value.number();
	// }

	// public IExpr zero() {
	// return ofUnit(value.zero());
	// }

	@Override
	public Set<IExpr> asSet() {
		throw new UnsupportedOperationException();
	}

	public IExpr ceiling() {
		return ofUnit(F.Ceiling.of(arg1));
	}

	@Override
	public IAST clone() throws CloneNotSupportedException {
		return new QuantityImpl(arg1, unitString, unit);
	}

	@Override
	public int compareTo(IExpr scalar) {
		if (scalar instanceof IQuantity) {
			IQuantity quantity = (IQuantity) scalar;
			if (unit.equals(quantity.unit()))
				return arg1.compareTo(quantity.value());
		}
		return super.compareTo(scalar);
	}

	@Override
	public INumber conjugate() {
		return (INumber) ofUnit(F.Conjugate.of(arg1));
	}

	/** {@inheritDoc} */
	@Override
	public IASTMutable copy() {
		return new QuantityImpl(arg1, unitString, unit);
	}

	@Override
	public IASTAppendable copyAppendable() {
		return F.NIL;
	}

	@Override
	public IExpr divide(IExpr scalar) {
		if (scalar instanceof IQuantity) {
			IQuantity quantity = (IQuantity) scalar;
			return of(arg1.divide(quantity.value()), unit.add(quantity.unit().negate()));
		}
		return ofUnit(arg1.divide(scalar));
	}

	// public IExpr under(IExpr scalar) {
	// if (scalar instanceof IQuantity) {
	// IQuantity quantity = (IQuantity) scalar;
	// return of(arg1.under(quantity.value()), unit.negate().add(quantity.unit()));
	// }
	// return new QuantityImpl(arg1.under(scalar), unit.negate());
	// }

	@Override
	public boolean equals(Object object) {
		if (object instanceof IQuantity) {
			IQuantity quantity = (IQuantity) object;
			return arg1.equals(quantity.value()) && unit.equals(quantity.unit()); // 2[kg] == 2[kg]
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public IExpr evaluate(EvalEngine engine) {
		return F.NIL;
	}

	public IExpr floor() {
		return ofUnit(F.Floor.of(arg1));
	}

	@Override
	public IExpr get(int location) {
		if (location >= 0 && location <= 2) {
			switch (location) {
			case 0:
				return head();
			case 1:
				// x
				return arg1();
			case 2:
				// x0
				return arg2();
			}
		}
		throw new IndexOutOfBoundsException("Index: " + Integer.valueOf(location) + ", Size: 1");
	}

	@Override
	public int hashCode() {
		return Objects.hash(arg1, unit);
	}

	@Override
	public IExpr head() {
		return F.Quantity;
	}

	/** {@inheritDoc} */
	@Override
	public int hierarchy() {
		return QUANTITYID;
	}

	@Override
	public IExpr im() {
		return ofUnit(F.Im.of(arg1));
	}

	/** {@inheritDoc} */
	@Override
	public boolean isAST0() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isAST1() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isAST2() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isAST3() {
		return false;
	}

	public boolean isExactNumber() {
		return arg1.isExactNumber();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isNegative() {
		return arg1.isNegative();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isNegativeInfinity() {
		return arg1.isNegativeInfinity();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isNegativeResult() {
		return arg1.isNegativeResult();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isNonNegativeResult() {
		return arg1.isNonNegativeResult();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isOne() {
		return arg1.isOne();
	}

	// @Override
	// public IExpr n(MathContext mathContext) {
	// N n = N.in(mathContext.getPrecision());
	// return ofUnit(n.apply(value));
	// }

	// @Override
	// public int signInt() {
	// SignInterface signInterface = (SignInterface) value;
	// return signInterface.signInt();
	// }

	/** {@inheritDoc} */
	@Override
	public boolean isPositive() {
		return arg1.isPositive();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isPositiveResult() {
		return arg1.isPositiveResult();
	}

	// @Override
	// public int compareTo(IExpr scalar) {
	// if (scalar instanceof IQuantity) {
	// IQuantity quantity = (IQuantity) scalar;
	// if (unit.equals(quantity.unit()))
	// return Scalars.compare(value, quantity.value());
	// }
	// throw MathException.of(this, scalar);
	// }

	@Override
	public boolean isQuantity() {
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isZero() {
		return arg1.isZero();
	}

	@Override
	public IExpr times(IExpr scalar) {
		if (scalar instanceof IQuantity) {
			IQuantity quantity = (IQuantity) scalar;
			return of(arg1.multiply(quantity.value()), unit.add(quantity.unit()));
		}
		return ofUnit(arg1.multiply(scalar));
	}

	public IExpr n() {
		return ofUnit(EvalEngine.get().evalN(arg1));
	}

	@Override
	public IExpr negate() {
		return ofUnit(arg1.negate());
	}

	public IQuantity ofUnit(IExpr scalar) {
		return new QuantityImpl(scalar, unitString, unit);
	}

	@Override
	public IExpr plus(final IExpr scalar) {
		boolean azero = arg1.isZero();
		boolean bzero = scalar.isZero();
		if (azero && !bzero)
			return scalar; // 0[m] + X(X!=0) gives X(X!=0)
		if (!azero && bzero)
			return this; // X(X!=0) + 0[m] gives X(X!=0)
		/** at this point the implication holds: azero == bzero */
		if (scalar instanceof IQuantity) {
			IQuantity quantity = (IQuantity) scalar;
			if (unit.equals(quantity.unit()))
				return ofUnit(arg1.add(quantity.value())); // 0[m] + 0[m] gives 0[m]
			else if (azero)
				// explicit addition of zeros to ensure symmetry
				// for instance when numeric precision is different
				return arg1.add(quantity.value()); // 0[m] + 0[s] gives 0
		} else // <- scalar is not an instance of Quantity
		if (azero)
			// return of value.add(scalar) is not required for symmetry
			// precision of this.value prevails over given scalar
			return this; // 0[kg] + 0 gives 0[kg]
		throw MathException.of(this, scalar);
	}

	@Override
	public IExpr power(final IExpr exponent) {
		if (exponent instanceof IQuantity) {
			throw MathException.of(this, exponent);
		}
		return of(F.Power.of(arg1, exponent), unit.multiply(exponent));
	}

	@Override
	public IExpr re() {
		return ofUnit(F.Re.of(arg1));
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
		this.fEvalFlags = objectInput.readShort();
		this.arg1 = (IExpr) objectInput.readObject();
		this.unitString = (String) objectInput.readObject();
		if (unitString.length() == 0) {
			this.unitString = null;
		}
		this.unit = (IUnit) objectInput.readObject();
	}

	@Override
	public IExpr reciprocal() {
		return new QuantityImpl(arg1.reciprocal(), null, unit.negate());
	}

	public IExpr round() {
		return ofUnit(F.Round.of(arg1));
	}

	@Override
	public IExpr set(int i, IExpr object) {
		return null;
	}

	@Override
	public int size() {
		return 3;
	}

	@Override
	public IExpr sqrt() {
		return of(F.Sqrt.of(arg1), unit.multiply(F.C1D2));
	}

	@Override
	public IExpr[] toArray() {
		IExpr[] result = new IExpr[3];
		result[0] = head();
		result[1] = arg1();
		result[2] = arg2();
		return result;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder(32); // initial buffer size
		stringBuilder.append(arg1);
		stringBuilder.append(UNIT_OPENING_BRACKET);
		stringBuilder.append(unit);
		stringBuilder.append(UNIT_CLOSING_BRACKET);
		return stringBuilder.toString();
	}

	@Override
	public IUnit unit() {
		return unit;
	}

	@Override
	public IExpr value() {
		return arg1;
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeShort(fEvalFlags);
		objectOutput.writeObject(arg1);
		if (unitString == null) {
			objectOutput.writeObject("");
		} else {
			objectOutput.writeObject(unitString);
		}
		objectOutput.writeObject(unit);
	}

	private Object writeReplace() throws ObjectStreamException {
		return optional(F.GLOBAL_IDS_MAP.get(this));
	}

}
