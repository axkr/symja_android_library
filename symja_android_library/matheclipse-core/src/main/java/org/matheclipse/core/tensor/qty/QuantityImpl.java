package org.matheclipse.core.tensor.qty;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.util.Objects;

import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.parser.client.math.MathException;

public class QuantityImpl extends DataExpr<IUnit> implements IQuantity, Externalizable {
	/**
	 * @param value
	 *            is assumed to be not instance of {@link IQuantity}
	 * @param unit
	 * @return
	 */
	/* package */ static IExpr of(IExpr value, IUnit unit) {
		return IUnit.ONE.equals(unit) ? value : new QuantityImpl(value, unit);
	}

	private IExpr arg1;

	public QuantityImpl() {
		super(F.Quantity, null);
	}

	/* package */ QuantityImpl(IExpr value, IUnit unit) {
		super(F.Quantity, unit);
		this.arg1 = value;
		this.fData = unit;
	}

	@Override
	public IExpr abs() {
		return ofUnit(arg1.abs());
	}

	public IExpr arcTan(IExpr x) {
		if (x instanceof IQuantity) {
			IQuantity quantity = (IQuantity) x;
			if (fData.equals(quantity.unit()))
				return F.ArcTan.of(quantity.value(), arg1);
		}
		throw MathException.of(x, this);
	}

	public IExpr arg() {
		return F.Arg.of(arg1);
	}

	// @Override
	public String unitString() {
		return fData.toString();
	}

	public IExpr ceiling() {
		return ofUnit(F.Ceiling.of(arg1));
	}

	@Override
	public int compareTo(IExpr scalar) {
		if (scalar instanceof IQuantity) {
			IQuantity quantity = (IQuantity) scalar;
			IUnit unit = quantity.unit();
			if (fData.equals(unit)) {
				return arg1.compareTo(quantity.value());
			}
			return fData.compareTo(unit);
		}
		return super.compareTo(scalar);
	}

	@Override
	public INumber conjugate() {
		return (INumber) ofUnit(F.Conjugate.of(arg1));
	}

	/** {@inheritDoc} */
	@Override
	public IExpr copy() {
		return new QuantityImpl(arg1, fData);
	}

	@Override
	public IExpr divide(IExpr scalar) {
		if (scalar instanceof IQuantity) {
			IQuantity quantity = (IQuantity) scalar;
			return of(arg1.divide(quantity.value()), fData.add(quantity.unit().negate()));
		}
		return ofUnit(arg1.divide(scalar));
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof IQuantity) {
			IQuantity quantity = (IQuantity) object;
			return arg1.equals(quantity.value()) && fData.equals(quantity.unit()); // 2[kg] == 2[kg]
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public IExpr evaluate(EvalEngine engine) {
		if (engine.isDoubleMode() && //
				!arg1.isInexactNumber()) {
			try {
				double qDouble = arg1.evalDouble();
				return new QuantityImpl(F.num(qDouble), fData);// setAtCopy(1, F.num(qDouble));
			} catch (RuntimeException rex) {
			}
		}

		return F.NIL;
	}

	public IExpr floor() {
		return ofUnit(F.Floor.of(arg1));
	}

	@Override
	public int hashCode() {
		return Objects.hash(arg1, fData);
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

	@Override
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
	public boolean isNumericFunction() {
		return arg1.isNumericFunction();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isInexactNumber() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isOne() {
		return false;// arg1.isOne();
	}

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

	@Override
	public boolean isQuantity() {
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isZero() {
		return false; // arg1.isZero();
	}

	@Override
	public IExpr times(IExpr scalar) {
		if (scalar instanceof IQuantity) {
			IQuantity quantity = (IQuantity) scalar;
			return of(arg1.times(quantity.value()), fData.add(quantity.unit()));
		}
		return ofUnit(arg1.times(scalar));
	}

	public IExpr n() {
		return ofUnit(EvalEngine.get().evalN(arg1));
	}

	@Override
	public IExpr negate() {
		return ofUnit(arg1.negate());
	}

	@Override
	public IQuantity ofUnit(IExpr scalar) {
		return new QuantityImpl(scalar, fData);
	}

	@Override
	public IExpr plus(final IExpr scalar) {
		boolean azero =  isZero();
		boolean bzero = scalar.isZero();
		if (azero && !bzero) {
			return scalar; // 0[m] + X(X!=0) gives X(X!=0)
		}
		if (!azero && bzero) {
			return this; // X(X!=0) + 0[m] gives X(X!=0)
		}
		/** at this point the implication holds: azero == bzero */
		if (scalar instanceof IQuantity) {
			IQuantity quantity = (IQuantity) scalar;
			IUnit unit = quantity.unit();
			if (!fData.equals(unit)) {
				IExpr lhs = UnitSystem.SI().apply(this);
				IExpr rhs = UnitSystem.SI().apply(quantity);
				if (!this.equals(lhs) || !quantity.equals(rhs)) {
					return lhs.plus(rhs);
				}
				String str = IOFunctions.getMessage("compat",
						F.List(F.stringx(fData.toString()), F.stringx(unit.toString())));
				throw new ArgumentTypeException(str); 
				// quantity = (IQuantity) UnitConvert.SI().to(unit).apply(quantity);
			}
			if (fData.equals(unit)) {
				return ofUnit(arg1.plus(quantity.value())); // 0[m] + 0[m] gives 0[m]
			} else if (azero) {
				// explicit addition of zeros to ensure symmetry
				// for instance when numeric precision is different
				return arg1.plus(quantity.value()); // 0[m] + 0[s] gives 0
			}
		} else // <- scalar is not an instance of Quantity
		if (azero) {
			// return of value.add(scalar) is not required for symmetry
			// precision of this.value prevails over given scalar
			return this; // 0[kg] + 0 gives 0[kg]
		}
		return F.NIL;
	}

	@Override
	public IExpr power(final IExpr exponent) {
		if (exponent instanceof IQuantity) {
			throw MathException.of(this, exponent);
		}
		return of(F.Power.of(arg1, exponent), fData.multiply(exponent));
	}

	@Override
	public IExpr re() {
		return ofUnit(F.Re.of(arg1));
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
		// this.fEvalFlags = objectInput.readShort();
		this.arg1 = (IExpr) objectInput.readObject();
		this.fData = (IUnit) objectInput.readObject();
	}

	@Override
	public IExpr reciprocal() {
		return new QuantityImpl(arg1.reciprocal(), fData.negate());
	}

	public IExpr round() {
		return ofUnit(F.Round.of(arg1));
	}

	@Override
	public IExpr sqrt() {
		return of(F.Sqrt.of(arg1), fData.multiply(F.C1D2));
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder(32); // initial buffer size
		stringBuilder.append(arg1);
		stringBuilder.append(UNIT_OPENING_BRACKET);
		stringBuilder.append(fData);
		stringBuilder.append(UNIT_CLOSING_BRACKET);
		return stringBuilder.toString();
	}

	@Override
	public IUnit unit() {
		return fData;
	}

	@Override
	public IExpr value() {
		return arg1;
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeObject(arg1);
		objectOutput.writeObject(fData);
	}

	private Object writeReplace() throws ObjectStreamException {
		return optional();
	}

}
