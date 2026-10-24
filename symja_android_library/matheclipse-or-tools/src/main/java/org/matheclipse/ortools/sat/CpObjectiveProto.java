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
 * Optimization objective.
 * </pre>
 *
 * <p>
 * Plain Java replacement of the protobuf message {@code operations_research.sat.CpObjectiveProto} of OR-Tools (generated from ortools/sat/cp_model.proto, no protobuf runtime needed). Instances are immutable; use {@link Builder} to create or modify them.
 */
public class CpObjectiveProto implements ProtoMessage {
  private static final CpObjectiveProto DEFAULT_INSTANCE = new CpObjectiveProto();

  java.util.ArrayList<Integer> vars_;
  java.util.ArrayList<Long> coeffs_;
  double offset_ = 0.0;
  double scalingFactor_ = 0.0;
  java.util.ArrayList<Long> domain_;
  boolean scalingWasExact_ = false;
  long integerBeforeOffset_ = 0L;
  long integerAfterOffset_ = 0L;
  long integerScalingFactor_ = 0L;

  private CpObjectiveProto() {}

  /** Returns the immutable default instance (all fields unset). */
  public static CpObjectiveProto getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  /** Returns the default instance. */
  public CpObjectiveProto getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

  /**
   * <pre>
   * The linear terms of the objective to minimize.
   * For a maximization problem, one can negate all coefficients in the
   * objective and set scaling_factor to -1.
   * </pre>
   */
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
  public java.util.List<Long> getCoeffsList() {
    return coeffs_ == null ? java.util.Collections.<Long>emptyList() : java.util.Collections.unmodifiableList(coeffs_);
  }

  /** Returns the number of elements of {@code coeffs}. */
  public int getCoeffsCount() {
    return coeffs_ == null ? 0 : coeffs_.size();
  }

  /** Returns the element {@code index} of {@code coeffs}. */
  public long getCoeffs(int index) {
    if (coeffs_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return coeffs_.get(index);
  }

  /**
   * <pre>
   * The displayed objective is always:
   *   scaling_factor * (sum(coefficients[i] * objective_vars[i]) + offset).
   * This is needed to have a consistent objective after presolve or when
   * scaling a double problem to express it with integers.
   *
   * Note that if scaling_factor is zero, then it is assumed to be 1, so that by
   * default these fields have no effect.
   * </pre>
   */
  public double getOffset() {
    return offset_;
  }

  /** Returns {@code scaling_factor}. */
  public double getScalingFactor() {
    return scalingFactor_;
  }

  /**
   * <pre>
   * If non-empty, only look for an objective value in the given domain.
   * Note that this does not depend on the offset or scaling factor, it is a
   * domain on the sum of the objective terms only.
   * </pre>
   */
  public java.util.List<Long> getDomainList() {
    return domain_ == null ? java.util.Collections.<Long>emptyList() : java.util.Collections.unmodifiableList(domain_);
  }

  /** Returns the number of elements of {@code domain}. */
  public int getDomainCount() {
    return domain_ == null ? 0 : domain_.size();
  }

  /** Returns the element {@code index} of {@code domain}. */
  public long getDomain(int index) {
    if (domain_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return domain_.get(index);
  }

  /** Returns {@code scaling_was_exact}. */
  @Deprecated
  public boolean getScalingWasExact() {
    return scalingWasExact_;
  }

  /**
   * <pre>
   * Internal fields to recover a bound on the original integer objective from
   * the presolved one. Basically, initially the integer objective fits in an
   * int64 and is in [Initial_lb, Initial_ub]. During presolve, we might change
   * the linear expression to have a new domain [Presolved_lb, Presolved_ub]
   * that will also always fit on an int64.
   *
   * The two domains will always be linked with an affine transformation between
   * the two of the form:
   *   old = (new + before_offset) * integer_scaling_factor + after_offset.
   * Note that we use both offsets to always be able to do the computation while
   * staying in the int64 domain. In particular, the after_offset will always
   * be in (-integer_scaling_factor, integer_scaling_factor).
   * </pre>
   */
  public long getIntegerBeforeOffset() {
    return integerBeforeOffset_;
  }

  /** Returns {@code integer_after_offset}. */
  public long getIntegerAfterOffset() {
    return integerAfterOffset_;
  }

  /** Returns {@code integer_scaling_factor}. */
  public long getIntegerScalingFactor() {
    return integerScalingFactor_;
  }

  void copyFieldsFrom(CpObjectiveProto src) {
    vars_ = src.vars_ == null || src.vars_.isEmpty() ? null : new java.util.ArrayList<>(src.vars_);
    coeffs_ = src.coeffs_ == null || src.coeffs_.isEmpty() ? null : new java.util.ArrayList<>(src.coeffs_);
    offset_ = src.offset_;
    scalingFactor_ = src.scalingFactor_;
    domain_ = src.domain_ == null || src.domain_.isEmpty() ? null : new java.util.ArrayList<>(src.domain_);
    scalingWasExact_ = src.scalingWasExact_;
    integerBeforeOffset_ = src.integerBeforeOffset_;
    integerAfterOffset_ = src.integerAfterOffset_;
    integerScalingFactor_ = src.integerScalingFactor_;
  }

  /** Returns this message if it is immutable, otherwise a built copy. */
  CpObjectiveProto immutableCopy() {
    if (this instanceof Builder) {
      CpObjectiveProto r = new CpObjectiveProto();
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
    if (Double.doubleToRawLongBits(offset_) != 0L) {
      printer.printScalar("offset", offset_);
    }
    if (Double.doubleToRawLongBits(scalingFactor_) != 0L) {
      printer.printScalar("scaling_factor", scalingFactor_);
    }
    if (coeffs_ != null) {
      for (Long v : coeffs_) {
        printer.printScalar("coeffs", v);
      }
    }
    if (domain_ != null) {
      for (Long v : domain_) {
        printer.printScalar("domain", v);
      }
    }
    if (scalingWasExact_) {
      printer.printScalar("scaling_was_exact", scalingWasExact_);
    }
    if (integerBeforeOffset_ != 0) {
      printer.printScalar("integer_before_offset", integerBeforeOffset_);
    }
    if (integerScalingFactor_ != 0) {
      printer.printScalar("integer_scaling_factor", integerScalingFactor_);
    }
    if (integerAfterOffset_ != 0) {
      printer.printScalar("integer_after_offset", integerAfterOffset_);
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
    if (!(obj instanceof CpObjectiveProto)) {
      return false;
    }
    CpObjectiveProto other = (CpObjectiveProto) obj;
    return getVarsList().equals(other.getVarsList())
        && getCoeffsList().equals(other.getCoeffsList())
        && Double.compare(offset_, other.offset_) == 0
        && Double.compare(scalingFactor_, other.scalingFactor_) == 0
        && getDomainList().equals(other.getDomainList())
        && scalingWasExact_ == other.scalingWasExact_
        && integerBeforeOffset_ == other.integerBeforeOffset_
        && integerAfterOffset_ == other.integerAfterOffset_
        && integerScalingFactor_ == other.integerScalingFactor_;
  }

  @Override
  public int hashCode() {
    int h = 281;
    h = 31 * h + getVarsList().hashCode();
    h = 31 * h + getCoeffsList().hashCode();
    h = 31 * h + Double.hashCode(offset_);
    h = 31 * h + Double.hashCode(scalingFactor_);
    h = 31 * h + getDomainList().hashCode();
    h = 31 * h + Boolean.hashCode(scalingWasExact_);
    h = 31 * h + Long.hashCode(integerBeforeOffset_);
    h = 31 * h + Long.hashCode(integerAfterOffset_);
    h = 31 * h + Long.hashCode(integerScalingFactor_);
    return h;
  }

  /** Appends the fields of this message in the protobuf binary format. */
  void writeTo(ProtoWire.Writer w) {
    if (vars_ != null && !vars_.isEmpty()) {
      w.packedInt32(1, vars_);
    }
    if (Double.doubleToRawLongBits(offset_) != 0L) {
      w.dbl(2, offset_);
    }
    if (Double.doubleToRawLongBits(scalingFactor_) != 0L) {
      w.dbl(3, scalingFactor_);
    }
    if (coeffs_ != null && !coeffs_.isEmpty()) {
      w.packedInt64(4, coeffs_);
    }
    if (domain_ != null && !domain_.isEmpty()) {
      w.packedInt64(5, domain_);
    }
    if (scalingWasExact_) {
      w.bool(6, scalingWasExact_);
    }
    if (integerBeforeOffset_ != 0) {
      w.int64(7, integerBeforeOffset_);
    }
    if (integerScalingFactor_ != 0) {
      w.int64(8, integerScalingFactor_);
    }
    if (integerAfterOffset_ != 0) {
      w.int64(9, integerAfterOffset_);
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
  public static CpObjectiveProto parseFrom(byte[] data) {
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
  public static Builder newBuilder(CpObjectiveProto prototype) {
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
   * Builder of {@link CpObjectiveProto}. A builder is also a (mutable) {@code CpObjectiveProto}, so all getters are
   * available on it.
   */
  public static final class Builder extends CpObjectiveProto implements Cloneable {
    Builder() {}

    /** Returns an immutable copy of the current content. */
    public CpObjectiveProto build() {
      CpObjectiveProto r = new CpObjectiveProto();
      r.copyFieldsFrom(this);
      return r;
    }

    /** Same as {@link #build()}. */
    public CpObjectiveProto buildPartial() {
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
    public Builder mergeFrom(CpObjectiveProto other) {
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
      if (Double.doubleToRawLongBits(other.scalingFactor_) != 0L) {
        setScalingFactor(other.scalingFactor_);
      }
      if (other.domain_ != null) {
        addAllDomain(other.domain_);
      }
      if (other.scalingWasExact_) {
        setScalingWasExact(other.scalingWasExact_);
      }
      if (other.integerBeforeOffset_ != 0) {
        setIntegerBeforeOffset(other.integerBeforeOffset_);
      }
      if (other.integerAfterOffset_ != 0) {
        setIntegerAfterOffset(other.integerAfterOffset_);
      }
      if (other.integerScalingFactor_ != 0) {
        setIntegerScalingFactor(other.integerScalingFactor_);
      }
      return this;
    }

    /** Replaces the content of this builder by a deep copy of {@code other}. */
    public void copyFrom(CpObjectiveProto other) {
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
    public java.util.List<Long> getCoeffsList() {
      if (coeffs_ == null) {
        coeffs_ = new java.util.ArrayList<>();
      }
      return new ProtoWire.ListView<>(coeffs_, null);
    }

    /**
     * Returns a modifiable view of {@code domain}: elements added or set through the list are
     * stored in this builder (messages as immutable copies). The view is detached by
     * {@code clearDomain()} and {@code clear()}.
     */
    @Override
    public java.util.List<Long> getDomainList() {
      if (domain_ == null) {
        domain_ = new java.util.ArrayList<>();
      }
      return new ProtoWire.ListView<>(domain_, null);
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
          case 17:
            setOffset(r.dbl());
            break;
          case 25:
            setScalingFactor(r.dbl());
            break;
          case 34: {
            ProtoWire.Reader p = r.packed();
            while (!p.atEnd()) {
              addCoeffs(p.varint());
            }
            break;
          }
          case 32:
            addCoeffs(r.varint());
            break;
          case 42: {
            ProtoWire.Reader p = r.packed();
            while (!p.atEnd()) {
              addDomain(p.varint());
            }
            break;
          }
          case 40:
            addDomain(r.varint());
            break;
          case 48:
            setScalingWasExact(r.varint() != 0);
            break;
          case 56:
            setIntegerBeforeOffset(r.varint());
            break;
          case 64:
            setIntegerScalingFactor(r.varint());
            break;
          case 72:
            setIntegerAfterOffset(r.varint());
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
                  addCoeffs(t.integer(Long.MIN_VALUE, Long.MAX_VALUE));
                } while (t.nextInList());
              }
            } else {
              addCoeffs(t.integer(Long.MIN_VALUE, Long.MAX_VALUE));
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
          case "scaling_factor": {
            if (strict && Double.doubleToRawLongBits(scalingFactor_) != 0L) {
              throw ProtoWire.duplicate(t, "scaling_factor");
            }
            t.consume(":");
            setScalingFactor(t.floating());
            break;
          }
          case "domain": {
            t.consume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addDomain(t.integer(Long.MIN_VALUE, Long.MAX_VALUE));
                } while (t.nextInList());
              }
            } else {
              addDomain(t.integer(Long.MIN_VALUE, Long.MAX_VALUE));
            }
            break;
          }
          case "scaling_was_exact": {
            if (strict && scalingWasExact_) {
              throw ProtoWire.duplicate(t, "scaling_was_exact");
            }
            t.consume(":");
            setScalingWasExact(t.bool());
            break;
          }
          case "integer_before_offset": {
            if (strict && integerBeforeOffset_ != 0) {
              throw ProtoWire.duplicate(t, "integer_before_offset");
            }
            t.consume(":");
            setIntegerBeforeOffset(t.integer(Long.MIN_VALUE, Long.MAX_VALUE));
            break;
          }
          case "integer_after_offset": {
            if (strict && integerAfterOffset_ != 0) {
              throw ProtoWire.duplicate(t, "integer_after_offset");
            }
            t.consume(":");
            setIntegerAfterOffset(t.integer(Long.MIN_VALUE, Long.MAX_VALUE));
            break;
          }
          case "integer_scaling_factor": {
            if (strict && integerScalingFactor_ != 0) {
              throw ProtoWire.duplicate(t, "integer_scaling_factor");
            }
            t.consume(":");
            setIntegerScalingFactor(t.integer(Long.MIN_VALUE, Long.MAX_VALUE));
            break;
          }
          default:
            throw ProtoWire.unknownField(t, "operations_research.sat.CpObjectiveProto", name);
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
    public Builder setCoeffs(int index, long value) {
      if (coeffs_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      coeffs_.set(index, value);
      return this;
    }

    /** Appends a value to {@code coeffs}. */
    public Builder addCoeffs(long value) {
      if (coeffs_ == null) {
        coeffs_ = new java.util.ArrayList<>();
      }
      coeffs_.add(value);
      return this;
    }

    /** Appends all values to {@code coeffs}. */
    public Builder addAllCoeffs(Iterable<? extends Long> values) {
      if (coeffs_ == null) {
        coeffs_ = new java.util.ArrayList<>();
      }
      for (Long v : values) {
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

    /** Sets {@code scaling_factor}. */
    public Builder setScalingFactor(double value) {
      scalingFactor_ = value;
      return this;
    }

    /** Resets {@code scaling_factor} to its default value. */
    public Builder clearScalingFactor() {
      scalingFactor_ = 0.0;
      return this;
    }

    /** Replaces the element {@code index} of {@code domain}. */
    public Builder setDomain(int index, long value) {
      if (domain_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      domain_.set(index, value);
      return this;
    }

    /** Appends a value to {@code domain}. */
    public Builder addDomain(long value) {
      if (domain_ == null) {
        domain_ = new java.util.ArrayList<>();
      }
      domain_.add(value);
      return this;
    }

    /** Appends all values to {@code domain}. */
    public Builder addAllDomain(Iterable<? extends Long> values) {
      if (domain_ == null) {
        domain_ = new java.util.ArrayList<>();
      }
      for (Long v : values) {
        domain_.add(v);
      }
      return this;
    }

    /** Removes all values of {@code domain}. */
    public Builder clearDomain() {
      domain_ = null;
      return this;
    }

    /** Sets {@code scaling_was_exact}. */
    @Deprecated
    public Builder setScalingWasExact(boolean value) {
      scalingWasExact_ = value;
      return this;
    }

    /** Resets {@code scaling_was_exact} to its default value. */
    @Deprecated
    public Builder clearScalingWasExact() {
      scalingWasExact_ = false;
      return this;
    }

    /** Sets {@code integer_before_offset}. */
    public Builder setIntegerBeforeOffset(long value) {
      integerBeforeOffset_ = value;
      return this;
    }

    /** Resets {@code integer_before_offset} to its default value. */
    public Builder clearIntegerBeforeOffset() {
      integerBeforeOffset_ = 0L;
      return this;
    }

    /** Sets {@code integer_after_offset}. */
    public Builder setIntegerAfterOffset(long value) {
      integerAfterOffset_ = value;
      return this;
    }

    /** Resets {@code integer_after_offset} to its default value. */
    public Builder clearIntegerAfterOffset() {
      integerAfterOffset_ = 0L;
      return this;
    }

    /** Sets {@code integer_scaling_factor}. */
    public Builder setIntegerScalingFactor(long value) {
      integerScalingFactor_ = value;
      return this;
    }

    /** Resets {@code integer_scaling_factor} to its default value. */
    public Builder clearIntegerScalingFactor() {
      integerScalingFactor_ = 0L;
      return this;
    }

  }
}
