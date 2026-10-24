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
 * The linear sum vars[i] * coeffs[i] must fall in the given domain. The domain
 * has the same format as the one in IntegerVariableProto.
 *
 * Note that the validation code currently checks using the domain of the
 * involved variables that the sum can always be computed without integer
 * overflow and throws an error otherwise.
 * </pre>
 *
 * <p>
 * Plain Java replacement of the protobuf message {@code operations_research.sat.LinearConstraintProto} of OR-Tools (generated from ortools/sat/cp_model.proto, no protobuf runtime needed). Instances are immutable; use {@link Builder} to create or modify them.
 */
public class LinearConstraintProto implements ProtoMessage {
  private static final LinearConstraintProto DEFAULT_INSTANCE = new LinearConstraintProto();

  java.util.ArrayList<Integer> vars_;
  java.util.ArrayList<Long> coeffs_;
  java.util.ArrayList<Long> domain_;

  private LinearConstraintProto() {}

  /** Returns the immutable default instance (all fields unset). */
  public static LinearConstraintProto getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  /** Returns the default instance. */
  public LinearConstraintProto getDefaultInstanceForType() {
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

  /**
   * <pre>
   * Same size as vars.
   * </pre>
   */
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

  /** Returns the elements of {@code domain}. */
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

  void copyFieldsFrom(LinearConstraintProto src) {
    vars_ = src.vars_ == null || src.vars_.isEmpty() ? null : new java.util.ArrayList<>(src.vars_);
    coeffs_ = src.coeffs_ == null || src.coeffs_.isEmpty() ? null : new java.util.ArrayList<>(src.coeffs_);
    domain_ = src.domain_ == null || src.domain_.isEmpty() ? null : new java.util.ArrayList<>(src.domain_);
  }

  /** Returns this message if it is immutable, otherwise a built copy. */
  LinearConstraintProto immutableCopy() {
    if (this instanceof Builder) {
      LinearConstraintProto r = new LinearConstraintProto();
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
      for (Long v : coeffs_) {
        printer.printScalar("coeffs", v);
      }
    }
    if (domain_ != null) {
      for (Long v : domain_) {
        printer.printScalar("domain", v);
      }
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
    if (!(obj instanceof LinearConstraintProto)) {
      return false;
    }
    LinearConstraintProto other = (LinearConstraintProto) obj;
    return getVarsList().equals(other.getVarsList())
        && getCoeffsList().equals(other.getCoeffsList())
        && getDomainList().equals(other.getDomainList());
  }

  @Override
  public int hashCode() {
    int h = 676;
    h = 31 * h + getVarsList().hashCode();
    h = 31 * h + getCoeffsList().hashCode();
    h = 31 * h + getDomainList().hashCode();
    return h;
  }

  /** Appends the fields of this message in the protobuf binary format. */
  void writeTo(ProtoWire.Writer w) {
    if (vars_ != null && !vars_.isEmpty()) {
      w.packedInt32(1, vars_);
    }
    if (coeffs_ != null && !coeffs_.isEmpty()) {
      w.packedInt64(2, coeffs_);
    }
    if (domain_ != null && !domain_.isEmpty()) {
      w.packedInt64(3, domain_);
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
  public static LinearConstraintProto parseFrom(byte[] data) {
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
  public static Builder newBuilder(LinearConstraintProto prototype) {
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
   * Builder of {@link LinearConstraintProto}. A builder is also a (mutable) {@code LinearConstraintProto}, so all getters are
   * available on it.
   */
  public static final class Builder extends LinearConstraintProto implements Cloneable {
    Builder() {}

    /** Returns an immutable copy of the current content. */
    public LinearConstraintProto build() {
      LinearConstraintProto r = new LinearConstraintProto();
      r.copyFieldsFrom(this);
      return r;
    }

    /** Same as {@link #build()}. */
    public LinearConstraintProto buildPartial() {
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
    public Builder mergeFrom(LinearConstraintProto other) {
      if (other == DEFAULT_INSTANCE) {
        return this;
      }
      if (other.vars_ != null) {
        addAllVars(other.vars_);
      }
      if (other.coeffs_ != null) {
        addAllCoeffs(other.coeffs_);
      }
      if (other.domain_ != null) {
        addAllDomain(other.domain_);
      }
      return this;
    }

    /** Replaces the content of this builder by a deep copy of {@code other}. */
    public void copyFrom(LinearConstraintProto other) {
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
          case 18: {
            ProtoWire.Reader p = r.packed();
            while (!p.atEnd()) {
              addCoeffs(p.varint());
            }
            break;
          }
          case 16:
            addCoeffs(r.varint());
            break;
          case 26: {
            ProtoWire.Reader p = r.packed();
            while (!p.atEnd()) {
              addDomain(p.varint());
            }
            break;
          }
          case 24:
            addDomain(r.varint());
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
          default:
            throw ProtoWire.unknownField(t, "operations_research.sat.LinearConstraintProto", name);
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

  }
}
