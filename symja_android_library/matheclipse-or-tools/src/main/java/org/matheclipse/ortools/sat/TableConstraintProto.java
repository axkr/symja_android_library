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
 * The values of the n-tuple formed by the given expression can only be one of
 * the listed n-tuples in values. The n-tuples are encoded in a flattened way:
 *     [tuple0_v0, tuple0_v1, ..., tuple0_v&#123;n-1&#125;, tuple1_v0, ...].
 * Corner cases:
 *  - If all `vars`, `values` and `exprs` are empty, the constraint is trivially
 *    true, irrespective of the value of `negated`.
 *  - If `values` is empty but either vars or exprs is not, the constraint is
 *    trivially false if `negated` is false, and trivially true if `negated` is
 *    true.
 *  - If `vars` and `exprs` are empty but `values` is not, the model is invalid.
 * </pre>
 *
 * <p>
 * Plain Java replacement of the protobuf message {@code operations_research.sat.TableConstraintProto} of OR-Tools (generated from ortools/sat/cp_model.proto, no protobuf runtime needed). Instances are immutable; use {@link Builder} to create or modify them.
 */
public class TableConstraintProto implements ProtoMessage {
  private static final TableConstraintProto DEFAULT_INSTANCE = new TableConstraintProto();

  java.util.ArrayList<Integer> vars_;
  java.util.ArrayList<Long> values_;
  java.util.ArrayList<LinearExpressionProto> exprs_;
  boolean negated_ = false;

  private TableConstraintProto() {}

  /** Returns the immutable default instance (all fields unset). */
  public static TableConstraintProto getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  /** Returns the default instance. */
  public TableConstraintProto getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

  /**
   * <pre>
   * Legacy field.
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

  /** Returns the elements of {@code values}. */
  public java.util.List<Long> getValuesList() {
    return values_ == null ? java.util.Collections.<Long>emptyList() : java.util.Collections.unmodifiableList(values_);
  }

  /** Returns the number of elements of {@code values}. */
  public int getValuesCount() {
    return values_ == null ? 0 : values_.size();
  }

  /** Returns the element {@code index} of {@code values}. */
  public long getValues(int index) {
    if (values_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return values_.get(index);
  }

  /** Returns the elements of {@code exprs}. */
  public java.util.List<LinearExpressionProto> getExprsList() {
    return exprs_ == null ? java.util.Collections.<LinearExpressionProto>emptyList() : java.util.Collections.unmodifiableList(exprs_);
  }

  /** Returns the number of elements of {@code exprs}. */
  public int getExprsCount() {
    return exprs_ == null ? 0 : exprs_.size();
  }

  /** Returns the element {@code index} of {@code exprs}. */
  public LinearExpressionProto getExprs(int index) {
    if (exprs_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return exprs_.get(index);
  }

  /**
   * <pre>
   * If true, the meaning is "negated", that is we forbid any of the given
   * tuples from a feasible assignment.
   * </pre>
   */
  public boolean getNegated() {
    return negated_;
  }

  void copyFieldsFrom(TableConstraintProto src) {
    vars_ = src.vars_ == null || src.vars_.isEmpty() ? null : new java.util.ArrayList<>(src.vars_);
    values_ = src.values_ == null || src.values_.isEmpty() ? null : new java.util.ArrayList<>(src.values_);
    exprs_ = buildListLinearExpressionProto(src.exprs_);
    negated_ = src.negated_;
  }

  /** Returns this message if it is immutable, otherwise a built copy. */
  TableConstraintProto immutableCopy() {
    if (this instanceof Builder) {
      TableConstraintProto r = new TableConstraintProto();
      r.copyFieldsFrom(this);
      return r;
    }
    return this;
  }

  private static java.util.ArrayList<LinearExpressionProto> buildListLinearExpressionProto(java.util.List<LinearExpressionProto> src) {
    if (src == null || src.isEmpty()) {
      return null;
    }
    java.util.ArrayList<LinearExpressionProto> r = new java.util.ArrayList<>(src.size());
    for (LinearExpressionProto e : src) {
      r.add(e.immutableCopy());
    }
    return r;
  }

  @Override
  public void printTo(ProtoPrinter printer) {
    if (vars_ != null) {
      for (Integer v : vars_) {
        printer.printScalar("vars", v);
      }
    }
    if (values_ != null) {
      for (Long v : values_) {
        printer.printScalar("values", v);
      }
    }
    if (negated_) {
      printer.printScalar("negated", negated_);
    }
    if (exprs_ != null) {
      for (LinearExpressionProto v : exprs_) {
        printer.printMessage("exprs", v);
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
    if (!(obj instanceof TableConstraintProto)) {
      return false;
    }
    TableConstraintProto other = (TableConstraintProto) obj;
    return getVarsList().equals(other.getVarsList())
        && getValuesList().equals(other.getValuesList())
        && getExprsList().equals(other.getExprsList())
        && negated_ == other.negated_;
  }

  @Override
  public int hashCode() {
    int h = 290;
    h = 31 * h + getVarsList().hashCode();
    h = 31 * h + getValuesList().hashCode();
    h = 31 * h + getExprsList().hashCode();
    h = 31 * h + Boolean.hashCode(negated_);
    return h;
  }

  /** Appends the fields of this message in the protobuf binary format. */
  void writeTo(ProtoWire.Writer w) {
    if (vars_ != null && !vars_.isEmpty()) {
      w.packedInt32(1, vars_);
    }
    if (values_ != null && !values_.isEmpty()) {
      w.packedInt64(2, values_);
    }
    if (negated_) {
      w.bool(3, negated_);
    }
    if (exprs_ != null) {
      for (LinearExpressionProto v : exprs_) {
        ProtoWire.Writer sub = new ProtoWire.Writer();
        v.writeTo(sub);
        w.bytes(4, sub);
      }
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
  public static TableConstraintProto parseFrom(byte[] data) {
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
  public static Builder newBuilder(TableConstraintProto prototype) {
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
   * Builder of {@link TableConstraintProto}. A builder is also a (mutable) {@code TableConstraintProto}, so all getters are
   * available on it.
   */
  public static final class Builder extends TableConstraintProto implements Cloneable {
    Builder() {}

    /** Returns an immutable copy of the current content. */
    public TableConstraintProto build() {
      TableConstraintProto r = new TableConstraintProto();
      r.copyFieldsFrom(this);
      return r;
    }

    /** Same as {@link #build()}. */
    public TableConstraintProto buildPartial() {
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
    public Builder mergeFrom(TableConstraintProto other) {
      if (other == DEFAULT_INSTANCE) {
        return this;
      }
      if (other.vars_ != null) {
        addAllVars(other.vars_);
      }
      if (other.values_ != null) {
        addAllValues(other.values_);
      }
      if (other.exprs_ != null) {
        addAllExprs(other.exprs_);
      }
      if (other.negated_) {
        setNegated(other.negated_);
      }
      return this;
    }

    /** Replaces the content of this builder by a deep copy of {@code other}. */
    public void copyFrom(TableConstraintProto other) {
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
     * Returns a modifiable view of {@code values}: elements added or set through the list are
     * stored in this builder (messages as immutable copies). The view is detached by
     * {@code clearValues()} and {@code clear()}.
     */
    @Override
    public java.util.List<Long> getValuesList() {
      if (values_ == null) {
        values_ = new java.util.ArrayList<>();
      }
      return new ProtoWire.ListView<>(values_, null);
    }

    /**
     * Returns a modifiable view of {@code exprs}: elements added or set through the list are
     * stored in this builder (messages as immutable copies). The view is detached by
     * {@code clearExprs()} and {@code clear()}.
     */
    @Override
    public java.util.List<LinearExpressionProto> getExprsList() {
      if (exprs_ == null) {
        exprs_ = new java.util.ArrayList<>();
      }
      return new ProtoWire.ListView<>(exprs_, LinearExpressionProto::immutableCopy);
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
              addValues(p.varint());
            }
            break;
          }
          case 16:
            addValues(r.varint());
            break;
          case 24:
            setNegated(r.varint() != 0);
            break;
          case 34:
            addExprsBuilder().mergeWire(r.message());
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
          case "values": {
            t.consume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addValues(t.integer(Long.MIN_VALUE, Long.MAX_VALUE));
                } while (t.nextInList());
              }
            } else {
              addValues(t.integer(Long.MIN_VALUE, Long.MAX_VALUE));
            }
            break;
          }
          case "exprs": {
            t.tryConsume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addExprsBuilder().mergeText(t, t.openMessage(), strict);
                } while (t.nextInList());
              }
            } else {
              addExprsBuilder().mergeText(t, t.openMessage(), strict);
            }
            break;
          }
          case "negated": {
            if (strict && negated_) {
              throw ProtoWire.duplicate(t, "negated");
            }
            t.consume(":");
            setNegated(t.bool());
            break;
          }
          default:
            throw ProtoWire.unknownField(t, "operations_research.sat.TableConstraintProto", name);
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

    /** Replaces the element {@code index} of {@code values}. */
    public Builder setValues(int index, long value) {
      if (values_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      values_.set(index, value);
      return this;
    }

    /** Appends a value to {@code values}. */
    public Builder addValues(long value) {
      if (values_ == null) {
        values_ = new java.util.ArrayList<>();
      }
      values_.add(value);
      return this;
    }

    /** Appends all values to {@code values}. */
    public Builder addAllValues(Iterable<? extends Long> values) {
      if (values_ == null) {
        values_ = new java.util.ArrayList<>();
      }
      for (Long v : values) {
        values_.add(v);
      }
      return this;
    }

    /** Removes all values of {@code values}. */
    public Builder clearValues() {
      values_ = null;
      return this;
    }

    /** Replaces the element {@code index} of {@code exprs}. */
    public Builder setExprs(int index, LinearExpressionProto value) {
      if (exprs_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      exprs_.set(index, java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Appends a value to {@code exprs}. */
    public Builder addExprs(LinearExpressionProto value) {
      if (exprs_ == null) {
        exprs_ = new java.util.ArrayList<>();
      }
      exprs_.add(java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Appends all values to {@code exprs}. */
    public Builder addAllExprs(Iterable<? extends LinearExpressionProto> values) {
      if (exprs_ == null) {
        exprs_ = new java.util.ArrayList<>();
      }
      for (LinearExpressionProto v : values) {
        exprs_.add(java.util.Objects.requireNonNull(v).immutableCopy());
      }
      return this;
    }

    /** Removes all values of {@code exprs}. */
    public Builder clearExprs() {
      exprs_ = null;
      return this;
    }

    /** Replaces the element {@code index} of {@code exprs} by the content of a builder. */
    public Builder setExprs(int index, LinearExpressionProto.Builder builderForValue) {
      return setExprs(index, builderForValue.build());
    }

    /** Appends the content of a builder to {@code exprs}. */
    public Builder addExprs(LinearExpressionProto.Builder builderForValue) {
      return addExprs(builderForValue.build());
    }

    /** Inserts a value into {@code exprs}. */
    public Builder addExprs(int index, LinearExpressionProto value) {
      if (exprs_ == null) {
        exprs_ = new java.util.ArrayList<>();
      }
      exprs_.add(index, java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Removes the element {@code index} of {@code exprs}. */
    public Builder removeExprs(int index) {
      if (exprs_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      exprs_.remove(index);
      return this;
    }

    /** Returns a builder for the element {@code index} of {@code exprs}; changes to it are reflected in this builder. */
    public LinearExpressionProto.Builder getExprsBuilder(int index) {
      if (exprs_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      LinearExpressionProto e = exprs_.get(index);
      if (e instanceof LinearExpressionProto.Builder) {
        return (LinearExpressionProto.Builder) e;
      }
      LinearExpressionProto.Builder b = LinearExpressionProto.newBuilder(e);
      exprs_.set(index, b);
      return b;
    }

    /** Appends a new empty element to {@code exprs} and returns its builder. */
    public LinearExpressionProto.Builder addExprsBuilder() {
      if (exprs_ == null) {
        exprs_ = new java.util.ArrayList<>();
      }
      LinearExpressionProto.Builder b = LinearExpressionProto.newBuilder();
      exprs_.add(b);
      return b;
    }

    /** Returns the builders of all elements of {@code exprs}. */
    public java.util.List<LinearExpressionProto.Builder> getExprsBuilderList() {
      java.util.List<LinearExpressionProto.Builder> r = new java.util.ArrayList<>();
      for (int k = 0; k < getExprsCount(); k++) {
        r.add(getExprsBuilder(k));
      }
      return r;
    }

    /** Sets {@code negated}. */
    public Builder setNegated(boolean value) {
      negated_ = value;
      return this;
    }

    /** Resets {@code negated} to its default value. */
    public Builder clearNegated() {
      negated_ = false;
      return this;
    }

  }
}
