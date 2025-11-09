package org.matheclipse.core.expression.data;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.WL;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Wrapper expression that holds a raw byte array as data.
 * <p>
 * Instances represent a `ByteArray` data object and implement {@link Externalizable} to provide
 * compact custom serialization of the underlying byte data.
 * </p>
 */
public class ByteArrayExpr extends DataExpr<byte[]> implements Externalizable {

  /** */
  private static final long serialVersionUID = 5799157739970931450L;

  /**
   * Factory method to create a new {@code ByteArrayExpr} wrapping the given byte array. Used in
   * function {@link S#ByteArray}.
   *
   * @param array the byte array to wrap
   * @return a new {@code ByteArrayExpr} instance
   */
  public static ByteArrayExpr newInstance(final byte[] array) {
    return new ByteArrayExpr(array);
  }

  /**
   * No-argument constructor required for {@link Externalizable} deserialization. Initializes the
   * expression with the {@link S#ByteArray} head and a null data payload.
   */
  public ByteArrayExpr() {
    super(S.ByteArray, null);
  }

  /**
   * Protected constructor that wraps the provided byte array.
   *
   * @param array the byte array to store in this expression
   */
  protected ByteArrayExpr(final byte[] array) {
    super(S.ByteArray, array);
  }

  /**
   * Create a shallow copy of this expression. The underlying byte array reference is copied (no
   * deep clone of the array is performed).
   *
   * @return a new {@code ByteArrayExpr} with the same internal data reference
   */
  @Override
  public IExpr copy() {
    return new ByteArrayExpr(fData);
  }

  /**
   * Equality is based on the contents of the wrapped byte array. Two {@code ByteArrayExpr}
   * instances are equal if their byte arrays are equal.
   *
   * @param obj the other object to compare
   * @return {@code true} if equal, {@code false} otherwise
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof ByteArrayExpr) {
      return Arrays.equals(fData, ((ByteArrayExpr) obj).fData);
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr get(int location) {
    if (location == 0) {
      return head();
    }
    byte[] bArray = toData();
    if (bArray.length > 0) {
      return F.ZZ(bArray[location - 1]);
    }
    return super.get(location);
  }

  @Override
  public int hashCode() {
    return (fData == null) ? 541 : 541 + Arrays.hashCode(fData);
  }


  /**
   * Return the internal hierarchy id for this expression type.
   *
   * @return the hierarchy id {@link IExpr#BYTEARRAYID}
   */
  @Override
  public int hierarchy() {
    return BYTEARRAYID;
  }


  /**
   * Convert the wrapped byte array into a MathEclipse list representation. This method is used to
   * present the data in a normalized AST form.
   *
   * @param nilIfUnevaluated ignored for this data-holder implementation
   * @return an {@link IAST} list representation of the byte array
   */
  @Override
  public IAST normal(boolean nilIfUnevaluated) {
    byte[] bArray = toData();
    return WL.toList(bArray);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException {
    final int len = in.readInt();
    fData = new byte[len];
    in.read(fData);
  }

  /** {@inheritDoc} */
  @Override
  public int size() {
    return toData().length + 1;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr subList(int startPosition, int endPosition) {
    byte[] bArray = toData();
    if (bArray.length > 0) {
      return newInstance(Arrays.copyOfRange(bArray, startPosition - 1, endPosition - 1));
    }
    return super.subList(startPosition, endPosition);
  }

  /**
   * Provide a succinct textual representation for debugging.
   *
   * @return a string like "ByteArray[<length> Bytes]"
   */
  @Override
  public String toString() {
    return fHead.toString() + "[" + fData.length + " Bytes]";
  }

  @Override
  public void writeExternal(ObjectOutput output) throws IOException {
    output.writeInt(fData.length);
    output.write(fData);
  }

}
