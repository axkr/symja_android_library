// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.qty;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;
import java.util.function.Function;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.util.SourceCodeProperties;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.ParserConfig;
import org.matheclipse.parser.client.math.MathException;

public class QuantityImpl extends DataExpr<IUnit> implements IQuantity, Externalizable {
  /**
   * @param value is assumed to be not instance of {@link IQuantity}
   * @param unit
   * @return
   */
  /* package */ static IExpr of(IExpr value, IUnit unit) {
    return IUnit.ONE.equals(unit) ? value : new QuantityImpl(value, unit);
  }

  private IExpr value;

  public QuantityImpl() {
    super(S.Quantity, null);
  }

  /* package */ QuantityImpl(IExpr value, IUnit unit) {
    super(S.Quantity, Objects.requireNonNull(unit, "Unit must not be null"));
    this.value = value;
  }

  @Override
  public IExpr abs() {
    return ofUnit(value.abs());
  }

  @Override
  public String unitString() {
    return fData.toString();
  }

  @Override
  public IExpr ceil() {
    return ofUnit(value.ceil());
  }

  @Override
  public int compareTo(IExpr scalar) {
    if (scalar instanceof IQuantity) {
      IQuantity quantity = (IQuantity) scalar;
      IUnit unit = quantity.unit();
      if (fData.equals(unit)) {
        return value.compareTo(quantity.value());
      }
      return fData.compareTo(unit);
    }
    return super.compareTo(scalar);
  }

  @Override
  public IExpr conjugate() {
    return ofUnit(value.conjugate());
  }

  /** {@inheritDoc} */
  @Override
  public IExpr copy() {
    return new QuantityImpl(value, fData);
  }

  @Override
  public IExpr divide(IExpr scalar) {
    if (scalar instanceof IQuantity) {
      IQuantity quantity = (IQuantity) scalar;
      return of(value.divide(quantity.value()), fData.add(quantity.unit().negate()));
    }
    return ofUnit(value.divide(scalar));
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof IQuantity) {
      IQuantity quantity = (IQuantity) object;
      return value.equals(quantity.value()) && fData.equals(quantity.unit()); // 2[kg] == 2[kg]
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr evaluate(EvalEngine engine) {
    if (value.isIndeterminate()) {
      return value;
    }
    if (engine.isDoubleMode() && !value.isInexactNumber()) {
      try {
        double qDouble = value.evalf();
        return ofUnit(F.num(qDouble));
      } catch (RuntimeException rex) {
      }
    }
    return F.NIL;
  }

  @Override
  public IReal evalReal() {
    return value.evalReal();
  }

  @Override
  public IExpr floor() {
    return ofUnit(value.floor());
  }

  /** {@inheritDoc} */
  @Override
  public String fullFormString() {
    final String sep = ", ";
    StringBuilder text = new StringBuilder();
    text.append("Quantity");
    text.append(ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS ? '(' : '[');
    text.append(value.fullFormString());
    text.append(sep + "\"");
    text.append(fData.toString());
    text.append("\"");
    text.append(ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS ? ')' : ']');
    return text.toString();
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, fData);
  }

  /** {@inheritDoc} */
  @Override
  public int hierarchy() {
    return QUANTITYID;
  }

  @Override
  public IExpr im() {
    return ofUnit(value.im());
  }

  @Override
  public CharSequence internalJavaString(SourceCodeProperties properties, int depth,
      Function<ISymbol, ? extends CharSequence> variables) {

    CharSequence value = value().internalJavaString(properties, depth, variables);

    StringBuilder javaForm = new StringBuilder();
    boolean fullName = properties.prefix == SourceCodeProperties.Prefix.FULLY_QUALIFIED_CLASS_NAME;
    String pPrefix = fullName ? "org.matheclipse.core.tensor.qty." : "";
    javaForm.append(pPrefix).append("IQuantity.of(").append(value).append(",");
    if (IUnit.ONE.equals(unit())) {
      javaForm.append(pPrefix).append("IUnit.ONE");
    } else {
      javaForm.append(pPrefix).append("IUnit.ofPutIfAbsent(\"").append(unitString()).append("\")");
    }
    return javaForm.append(")");
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
    return value.isExactNumber();
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNegative() {
    return value.isNegative();
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNegativeInfinity() {
    return value.isNegativeInfinity();
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNegativeResult() {
    return value.isNegativeResult();
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNonNegativeResult() {
    return value.isNonNegativeResult();
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNumericFunction(boolean allowList) {
    return value.isNumericFunction(true);
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
    return value.isPositive();
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPositiveResult() {
    return value.isPositiveResult();
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
    // if (scalar instanceof IQuantity) {
    // IQuantity quantity = (IQuantity) scalar;
    // return of(arg1.times(quantity.value()), fData.add(quantity.unit()));
    // }
    // if (scalar.isReal()) {
    // return ofUnit(arg1.times(scalar));
    // }
    // return F.Times(this, scalar);
  }

  @Override
  public IExpr times(IExpr scalar, boolean nilIfUnevaluated) {
    if (scalar instanceof IQuantity) {
      IQuantity quantity = (IQuantity) scalar;
      return of(value.times(quantity.value()), fData.add(quantity.unit()));
    }
    if (scalar.isReal()) {
      return ofUnit(value.times(scalar));
    }
    return nilIfUnevaluated ? F.NIL : F.Times(this, scalar);
  }

  public IExpr n() {
    return ofUnit(EvalEngine.get().evalN(value));
  }

  @Override
  public IExpr negate() {
    return ofUnit(value.negate());
  }

  @Override
  public IQuantity ofUnit(IExpr scalar) {
    return new QuantityImpl(scalar, fData);
  }

  @Override
  public IExpr plus(final IExpr scalar) {
    return plus(scalar, false);
    // boolean azero = isZero();
    // boolean bzero = scalar.isZero();
    // if (azero && !bzero) {
    // return scalar; // 0[m] + X(X!=0) gives X(X!=0)
    // }
    // if (!azero && bzero) {
    // return this; // X(X!=0) + 0[m] gives X(X!=0)
    // }
    // /** at this point the implication holds: azero == bzero */
    // if (scalar instanceof IQuantity) {
    // IQuantity quantity = (IQuantity) scalar;
    // IUnit unit = quantity.unit();
    // if (!fData.equals(unit)) {
    // IExpr lhs = UnitSystem.SI().apply(this);
    // IExpr rhs = UnitSystem.SI().apply(quantity);
    // if (!this.equals(lhs) || !quantity.equals(rhs)) {
    // return lhs.plus(rhs);
    // }
    // String str =
    // IOFunctions.getMessage(
    // "compat", F.list(F.stringx(fData.toString()), F.stringx(unit.toString())));
    // throw new ArgumentTypeException(str);
    // // quantity = (IQuantity) UnitConvert.SI().to(unit).apply(quantity);
    // }
    // if (fData.equals(unit)) {
    // return ofUnit(arg1.plus(quantity.value())); // 0[m] + 0[m] gives 0[m]
    // } else if (azero) {
    // // explicit addition of zeros to ensure symmetry
    // // for instance when numeric precision is different
    // return arg1.plus(quantity.value()); // 0[m] + 0[s] gives 0
    // }
    // } else // <- scalar is not an instance of Quantity
    // if (azero) {
    // // return of value.add(scalar) is not required for symmetry
    // // precision of this.value prevails over given scalar
    // return this; // 0[kg] + 0 gives 0[kg]
    // }
    // return F.NIL;
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
        // `1` and `2` are incompatible units
        throw new ArgumentTypeException("compat",
            F.list(F.stringx(fData.toString()), F.stringx(unit.toString())));
      }
      if (fData.equals(unit)) {
        return ofUnit(value.plus(quantity.value())); // 0[m] + 0[m] gives 0[m]
      } else if (azero) {
        // explicit addition of zeros to ensure symmetry
        // for instance when numeric precision is different
        return value.plus(quantity.value()); // 0[m] + 0[s] gives 0
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
    return of(S.Power.of(value, exponent), product);
  }

  @Override
  public IExpr re() {
    return ofUnit(value.re());
  }

  @Override
  public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
    // this.fEvalFlags = objectInput.readShort();
    this.value = (IExpr) objectInput.readObject();
    this.fData = (IUnit) objectInput.readObject();
  }

  @Override
  public IExpr reciprocal() {
    return new QuantityImpl(value.reciprocal(), fData.negate());
  }

  @Override
  public IExpr roundExpr() {
    return ofUnit(value.roundExpr());
  }

  @Override
  public IExpr sqrt() {
    IUnit product = fData.multiply(F.C1D2);
    if (product == null) {
      return F.NIL;
    }
    return of(S.Sqrt.of(value), product);
  }

  @Override
  public String toString() {
    return value.toString() + UNIT_OPENING_BRACKET + fData + UNIT_CLOSING_BRACKET;
  }

  @Override
  public IUnit unit() {
    return fData;
  }

  @Override
  public IExpr value() {
    return value;
  }

  @Override
  public void writeExternal(ObjectOutput objectOutput) throws IOException {
    objectOutput.writeObject(value);
    objectOutput.writeObject(fData);
  }

  private Object writeReplace() {
    return optional();
  }

  @Override
  public IExpr zero() {
    return ofUnit(value.zero());
  }

  @Override
  public IExpr one() {
    return value.one();
  }
}
