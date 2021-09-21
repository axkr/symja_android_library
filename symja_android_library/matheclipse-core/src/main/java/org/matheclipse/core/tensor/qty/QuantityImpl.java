package org.matheclipse.core.tensor.qty;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.math.MathException;

public class QuantityImpl extends DataExpr<IUnit> implements IQuantity, Externalizable {
  /**
   * @param value is assumed to be not instance of {@link IQuantity}
   * @param unit
   * @return
   */
  /* package */ static IExpr of(IExpr value, IUnit unit) {
    return IUnit.ONE.equals(unit)
        ? value
        : new QuantityImpl(value, unit);
  }

  private IExpr arg1;

  public QuantityImpl() {
    super(S.Quantity, null);
  }

  /* package */ QuantityImpl(IExpr value, IUnit unit) {
    super(S.Quantity, unit);
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
      if (fData.equals(quantity.unit())) return S.ArcTan.of(quantity.value(), arg1);
    }
    throw MathException.of(x, this);
  }

  public IExpr arg() {
    return S.Arg.of(arg1);
  }

  // @Override
  @Override
  public String unitString() {
    return fData.toString();
  }

  public IExpr ceiling() {
    return ofUnit(S.Ceiling.of(arg1));
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
  public IExpr conjugate() {
    if (arg1.isRealResult()) {
      return this;
    }
    return new QuantityImpl(F.Conjugate(arg1), fData);
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
    if (arg1.isIndeterminate()) {
      return arg1;
    }
    if (engine.isDoubleMode()
        && //
        !arg1.isInexactNumber()) {
      try {
        double qDouble = arg1.evalDouble();
        return new QuantityImpl(F.num(qDouble), fData); // setAtCopy(1, F.num(qDouble));
      } catch (RuntimeException rex) {
      }
    }

    return F.NIL;
  }

  public IExpr floor() {
    return ofUnit(S.Floor.of(arg1));
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
    if (arg1.isRealResult()) {
      return new QuantityImpl(F.C0, fData);
    }
    return new QuantityImpl(F.Im(arg1), fData);
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
  public boolean isNumericFunction(boolean allowList) {
    return arg1.isNumericFunction(true);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isInexactNumber() {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isOne() {
    return false; // arg1.isOne();
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
    return times(scalar, false);
    //    if (scalar instanceof IQuantity) {
    //      IQuantity quantity = (IQuantity) scalar;
    //      return of(arg1.times(quantity.value()), fData.add(quantity.unit()));
    //    }
    //    if (scalar.isReal()) {
    //      return ofUnit(arg1.times(scalar));
    //    }
    //    return F.Times(this, scalar);
  }

  @Override
  public IExpr times(IExpr scalar, boolean nilIfUnevaluated) {
    if (scalar instanceof IQuantity) {
      IQuantity quantity = (IQuantity) scalar;
      return of(arg1.times(quantity.value()), fData.add(quantity.unit()));
    }
    if (scalar.isReal()) {
      return ofUnit(arg1.times(scalar));
    }
    return nilIfUnevaluated ? F.NIL : F.Times(this, scalar);
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
    return plus(scalar, false);
    //    boolean azero = isZero();
    //    boolean bzero = scalar.isZero();
    //    if (azero && !bzero) {
    //      return scalar; // 0[m] + X(X!=0) gives X(X!=0)
    //    }
    //    if (!azero && bzero) {
    //      return this; // X(X!=0) + 0[m] gives X(X!=0)
    //    }
    //    /** at this point the implication holds: azero == bzero */
    //    if (scalar instanceof IQuantity) {
    //      IQuantity quantity = (IQuantity) scalar;
    //      IUnit unit = quantity.unit();
    //      if (!fData.equals(unit)) {
    //        IExpr lhs = UnitSystem.SI().apply(this);
    //        IExpr rhs = UnitSystem.SI().apply(quantity);
    //        if (!this.equals(lhs) || !quantity.equals(rhs)) {
    //          return lhs.plus(rhs);
    //        }
    //        String str =
    //            IOFunctions.getMessage(
    //                "compat", F.List(F.stringx(fData.toString()), F.stringx(unit.toString())));
    //        throw new ArgumentTypeException(str);
    //        // quantity = (IQuantity) UnitConvert.SI().to(unit).apply(quantity);
    //      }
    //      if (fData.equals(unit)) {
    //        return ofUnit(arg1.plus(quantity.value())); // 0[m] + 0[m] gives 0[m]
    //      } else if (azero) {
    //        // explicit addition of zeros to ensure symmetry
    //        // for instance when numeric precision is different
    //        return arg1.plus(quantity.value()); // 0[m] + 0[s] gives 0
    //      }
    //    } else // <- scalar is not an instance of Quantity
    //    if (azero) {
    //      // return of value.add(scalar) is not required for symmetry
    //      // precision of this.value prevails over given scalar
    //      return this; // 0[kg] + 0 gives 0[kg]
    //    }
    //    return F.NIL;
  }

  @Override
  public IExpr plus(final IExpr scalar, boolean nilIfUnevaluated) {
    boolean azero = isZero();
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
        String str =
            IOFunctions.getMessage(
                "compat", F.List(F.stringx(fData.toString()), F.stringx(unit.toString())));
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
    return nilIfUnevaluated ? F.NIL : F.Plus(this, scalar);
  }

  @Override
  public IExpr power(final IExpr exponent) {
    if (exponent instanceof IQuantity) {
      throw MathException.of(this, exponent);
    }
    IUnit product = fData.multiply(exponent);
    if (product == null) {
      return F.NIL;
    }
    return of(S.Power.of(arg1, exponent), product);
  }

  @Override
  public IExpr re() {
    if (arg1.isRealResult()) {
      return this;
    }
    return new QuantityImpl(F.Re(arg1), fData);
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

  public IExpr roundExpr() {
    return ofUnit(S.Round.of(arg1));
  }

  @Override
  public IExpr sqrt() {
    IUnit product = fData.multiply(F.C1D2);
    if (product == null) {
      return F.NIL;
    }
    return of(S.Sqrt.of(arg1), product);
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

  private Object writeReplace() {
    return optional();
  }
}
