// Copyright 2010-2025 Google LLC
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
// Modified for the Symja matheclipse-or-tools module: ported from ortools/sat/cp_model.proto to Java (a plain Java class replaces the generated protobuf message), see NOTICE in the module root.

package org.matheclipse.ortools.sat;

/**
 * <pre>
 * A linear floating point objective: sum coeffs[i] * vars[i] + offset.
 * Note that the variable can still only take integer values.
 * </pre>
 *
 * <p>
 * Plain Java replacement of the protobuf message {@code operations_research.sat.FloatObjectiveProto} of OR-Tools (generated from ortools/sat/cp_model.proto, no protobuf runtime needed). Instances are immutable; use {@link Builder} to create or modify them.
 */
public class FloatObjectiveProto implements ProtoMessage {
  private static final FloatObjectiveProto DEFAULT_INSTANCE = new FloatObjectiveProto();

  java.util.ArrayList<Integer> vars_;
  java.util.ArrayList<Double> coeffs_;
  double offset_ = 0.0;
  boolean maximize_ = false;

  private FloatObjectiveProto() {}

  /** Returns the immutable default instance (all fields unset). */
  public static FloatObjectiveProto getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  /** Returns the default instance. */
  public FloatObjectiveProto getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

  /** Returns the elements of {@code vars}. */
  public java.util.List<Integer> getVarsList() {
    return vars_ == null ? java.util.Collections.<Integer>emptyList() : java.util.Collections.unmodifiableList(vars_);
  }

  /** Returns the number of elements of {@code vars}. */
  public int getVarsCount() {
    return vars_ == null ? 0 : vars_.size();
  }

  /** Returns the element {@code index} of {@code vars}. */
  public int getVars(int index) {
    if (vars_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return vars_.get(index);
  }

  /** Returns the elements of {@code coeffs}. */
  public java.util.List<Double> getCoeffsList() {
    return coeffs_ == null ? java.util.Collections.<Double>emptyList() : java.util.Collections.unmodifiableList(coeffs_);
  }

  /** Returns the number of elements of {@code coeffs}. */
  public int getCoeffsCount() {
    return coeffs_ == null ? 0 : coeffs_.size();
  }

  /** Returns the element {@code index} of {@code coeffs}. */
  public double getCoeffs(int index) {
    if (coeffs_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return coeffs_.get(index);
  }

  /** Returns {@code offset}. */
  public double getOffset() {
    return offset_;
  }

  /**
   * <pre>
   * The optimization direction. The default is to minimize.
   * </pre>
   */
  public boolean getMaximize() {
    return maximize_;
  }

  void copyFieldsFrom(FloatObjectiveProto src) {
    vars_ = src.vars_ == null || src.vars_.isEmpty() ? null : new java.util.ArrayList<>(src.vars_);
    coeffs_ = src.coeffs_ == null || src.coeffs_.isEmpty() ? null : new java.util.ArrayList<>(src.coeffs_);
    offset_ = src.offset_;
    maximize_ = src.maximize_;
  }

  /** Returns this message if it is immutable, otherwise a built copy. */
  FloatObjectiveProto immutableCopy() {
    if (this instanceof Builder) {
      FloatObjectiveProto r = new FloatObjectiveProto();
      r.copyFieldsFrom(this);
      return r;
    }
    return this;
  }

  @Override
  public void printTo(ProtoPrinter printer) {
    if (vars_ != null) {
      for (Integer v : vars_) {
        printer.printScalar("vars", v);
      }
    }
    if (coeffs_ != null) {
      for (Double v : coeffs_) {
        printer.printScalar("coeffs", v);
      }
    }
    if (Double.doubleToRawLongBits(offset_) != 0L) {
      printer.printScalar("offset", offset_);
    }
    if (maximize_) {
      printer.printScalar("maximize", maximize_);
    }
  }

  /** Returns the message in protobuf text format. */
  @Override
  public String toString() {
    return TextFormat.printToString(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof FloatObjectiveProto)) {
      return false;
    }
    FloatObjectiveProto other = (FloatObjectiveProto) obj;
    return getVarsList().equals(other.getVarsList())
        && getCoeffsList().equals(other.getCoeffsList())
        && Double.compare(offset_, other.offset_) == 0
        && maximize_ == other.maximize_;
  }

  @Override
  public int hashCode() {
    int h = 614;
    h = 31 * h + getVarsList().hashCode();
    h = 31 * h + getCoeffsList().hashCode();
    h = 31 * h + Double.hashCode(offset_);
    h = 31 * h + Boolean.hashCode(maximize_);
    return h;
  }

  /** Appends the fields of this message in the protobuf binary format. */
  void writeTo(ProtoWire.Writer w) {
    if (vars_ != null && !vars_.isEmpty()) {
      w.packedInt32(1, vars_);
    }
    if (coeffs_ != null && !coeffs_.isEmpty()) {
      w.packedDouble(2, coeffs_);
    }
    if (Double.doubleToRawLongBits(offset_) != 0L) {
      w.dbl(3, offset_);
    }
    if (maximize_) {
      w.bool(4, maximize_);
    }
  }

  /** Returns the message serialized in the protobuf binary format. */
  public byte[] toByteArray() {
    ProtoWire.Writer w = new ProtoWire.Writer();
    writeTo(w);
    return w.toByteArray();
  }

  /**
   * Parses a message in the protobuf binary format; returns {@code null} if the data is not a valid
   * serialized message. Unknown fields are skipped.
   */
  public static FloatObjectiveProto parseFrom(byte[] data) {
    Builder b = new Builder();
    try {
      b.mergeWire(new ProtoWire.Reader(data));
    } catch (ProtoWire.ParseError e) {
      return null;
    }
    return b.build();
  }

  /** Returns a new builder. */
  public static Builder newBuilder() {
    return new Builder();
  }

  /** Returns a new builder initialized with a copy of {@code prototype}. */
  public static Builder newBuilder(FloatObjectiveProto prototype) {
    Builder b = new Builder();
    b.copyFieldsFrom(prototype);
    return b;
  }

  /** Returns a new builder initialized with a copy of this message. */
  public Builder toBuilder() {
    return newBuilder(this);
  }

  /** Returns a new builder. */
  public Builder newBuilderForType() {
    return new Builder();
  }

  /**
   * Builder of {@link FloatObjectiveProto}. A builder is also a (mutable) {@code FloatObjectiveProto}, so all getters are
   * available on it.
   */
  public static final class Builder extends FloatObjectiveProto implements Cloneable {
    Builder() {}

    /** Returns an immutable copy of the current content. */
    public FloatObjectiveProto build() {
      FloatObjectiveProto r = new FloatObjectiveProto();
      r.copyFieldsFrom(this);
      return r;
    }

    /** Same as {@link #build()}. */
    public FloatObjectiveProto buildPartial() {
      return build();
    }

    /** Resets all fields to their default values. */
    public Builder clear() {
      copyFieldsFrom(DEFAULT_INSTANCE);
      return this;
    }

    /** Returns a deep copy of this builder. */
    @Override
    public Builder clone() {
      Builder b = new Builder();
      b.copyFieldsFrom(this);
      return b;
    }

    /**
     * Merges {@code other} into this builder with the protobuf semantics: set scalar fields
     * overwrite, repeated fields are appended, message fields are merged recursively.
     */
    public Builder mergeFrom(FloatObjectiveProto other) {
      if (other == DEFAULT_INSTANCE) {
        return this;
      }
      if (other.vars_ != null) {
        addAllVars(other.vars_);
      }
      if (other.coeffs_ != null) {
        addAllCoeffs(other.coeffs_);
      }
      if (Double.doubleToRawLongBits(other.offset_) != 0L) {
        setOffset(other.offset_);
      }
      if (other.maximize_) {
        setMaximize(other.maximize_);
      }
      return this;
    }

    /** Replaces the content of this builder by a deep copy of {@code other}. */
    public void copyFrom(FloatObjectiveProto other) {
      copyFieldsFrom(other);
    }

    /**
     * Returns a modifiable view of {@code vars}: elements added or set through the list are
     * stored in this builder (messages as immutable copies). The view is detached by
     * {@code clearVars()} and {@code clear()}.
     */
    @Override
    public java.util.List<Integer> getVarsList() {
      if (vars_ == null) {
        vars_ = new java.util.ArrayList<>();
      }
      return new ProtoWire.ListView<>(vars_, null);
    }

    /**
     * Returns a modifiable view of {@code coeffs}: elements added or set through the list are
     * stored in this builder (messages as immutable copies). The view is detached by
     * {@code clearCoeffs()} and {@code clear()}.
     */
    @Override
    public java.util.List<Double> getCoeffsList() {
      if (coeffs_ == null) {
        coeffs_ = new java.util.ArrayList<>();
      }
      return new ProtoWire.ListView<>(coeffs_, null);
    }

    void mergeWire(ProtoWire.Reader r) {
      while (!r.atEnd()) {
        int tag = r.tag();
        switch (tag) {
          case 10: {
            ProtoWire.Reader p = r.packed();
            while (!p.atEnd()) {
              addVars((int) p.varint());
            }
            break;
          }
          case 8:
            addVars((int) r.varint());
            break;
          case 18: {
            ProtoWire.Reader p = r.packed();
            while (!p.atEnd()) {
              addCoeffs(p.dbl());
            }
            break;
          }
          case 17:
            addCoeffs(r.dbl());
            break;
          case 25:
            setOffset(r.dbl());
            break;
          case 32:
            setMaximize(r.varint() != 0);
            break;
          default:
            r.skip(tag);
        }
      }
    }

    void mergeText(ProtoWire.Text t, String end, boolean strict) {
      while (!t.atMessageEnd(end)) {
        String name = t.fieldName();
        switch (name) {
          case "vars": {
            t.consume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addVars((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
                } while (t.nextInList());
              }
            } else {
              addVars((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            }
            break;
          }
          case "coeffs": {
            t.consume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addCoeffs(t.floating());
                } while (t.nextInList());
              }
            } else {
              addCoeffs(t.floating());
            }
            break;
          }
          case "offset": {
            if (strict && Double.doubleToRawLongBits(offset_) != 0L) {
              throw ProtoWire.duplicate(t, "offset");
            }
            t.consume(":");
            setOffset(t.floating());
            break;
          }
          case "maximize": {
            if (strict && maximize_) {
              throw ProtoWire.duplicate(t, "maximize");
            }
            t.consume(":");
            setMaximize(t.bool());
            break;
          }
          default:
            throw ProtoWire.unknownField(t, "operations_research.sat.FloatObjectiveProto", name);
        }
        t.separator();
      }
    }

    /**
     * Replaces the content of this builder by the message serialized in {@code data} (protobuf
     * binary format; unknown fields are skipped). Like the OR-Tools 10.0 wrapper, the builder is
     * cleared first.
     *
     * @throws RuntimeException {@code "parse failure"} if the data is not a valid serialized message
     */
    public void mergeFrom(byte[] data) {
      clear();
      try {
        mergeWire(new ProtoWire.Reader(data));
      } catch (ProtoWire.ParseError e) {
        throw new RuntimeException("parse failure", e);
      }
    }

    /**
     * Replaces the content of this builder by the message given in protobuf text format; returns
     * false on a syntax error, an unknown field or enum value, or a non-repeated field given twice
     * (the builder then holds the fields parsed before the error).
     */
    public boolean parseTextFormat(String text) {
      clear();
      try {
        mergeText(new ProtoWire.Text(text), null, true);
        return true;
      } catch (ProtoWire.ParseError e) {
        return false;
      }
    }

    /** Replaces the element {@code index} of {@code vars}. */
    public Builder setVars(int index, int value) {
      if (vars_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      vars_.set(index, value);
      return this;
    }

    /** Appends a value to {@code vars}. */
    public Builder addVars(int value) {
      if (vars_ == null) {
        vars_ = new java.util.ArrayList<>();
      }
      vars_.add(value);
      return this;
    }

    /** Appends all values to {@code vars}. */
    public Builder addAllVars(Iterable<? extends Integer> values) {
      if (vars_ == null) {
        vars_ = new java.util.ArrayList<>();
      }
      for (Integer v : values) {
        vars_.add(v);
      }
      return this;
    }

    /** Removes all values of {@code vars}. */
    public Builder clearVars() {
      vars_ = null;
      return this;
    }

    /** Replaces the element {@code index} of {@code coeffs}. */
    public Builder setCoeffs(int index, double value) {
      if (coeffs_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      coeffs_.set(index, value);
      return this;
    }

    /** Appends a value to {@code coeffs}. */
    public Builder addCoeffs(double value) {
      if (coeffs_ == null) {
        coeffs_ = new java.util.ArrayList<>();
      }
      coeffs_.add(value);
      return this;
    }

    /** Appends all values to {@code coeffs}. */
    public Builder addAllCoeffs(Iterable<? extends Double> values) {
      if (coeffs_ == null) {
        coeffs_ = new java.util.ArrayList<>();
      }
      for (Double v : values) {
        coeffs_.add(v);
      }
      return this;
    }

    /** Removes all values of {@code coeffs}. */
    public Builder clearCoeffs() {
      coeffs_ = null;
      return this;
    }

    /** Sets {@code offset}. */
    public Builder setOffset(double value) {
      offset_ = value;
      return this;
    }

    /** Resets {@code offset} to its default value. */
    public Builder clearOffset() {
      offset_ = 0.0;
      return this;
    }

    /** Sets {@code maximize}. */
    public Builder setMaximize(boolean value) {
      maximize_ = value;
      return this;
    }

    /** Resets {@code maximize} to its default value. */
    public Builder clearMaximize() {
      maximize_ = false;
      return this;
    }

  }
}
