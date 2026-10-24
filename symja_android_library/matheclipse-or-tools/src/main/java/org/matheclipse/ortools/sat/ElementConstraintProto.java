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
 * The constraint linear_target = exprs[linear_index].
 * This enforces that index takes one of the values in [0, vars_size()).
 * </pre>
 *
 * <p>
 * Plain Java replacement of the protobuf message {@code operations_research.sat.ElementConstraintProto} of OR-Tools (generated from ortools/sat/cp_model.proto, no protobuf runtime needed). Instances are immutable; use {@link Builder} to create or modify them.
 */
public class ElementConstraintProto implements ProtoMessage {
  private static final ElementConstraintProto DEFAULT_INSTANCE = new ElementConstraintProto();

  int index_ = 0;
  int target_ = 0;
  java.util.ArrayList<Integer> vars_;
  LinearExpressionProto linearIndex_;
  LinearExpressionProto linearTarget_;
  java.util.ArrayList<LinearExpressionProto> exprs_;

  private ElementConstraintProto() {}

  /** Returns the immutable default instance (all fields unset). */
  public static ElementConstraintProto getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  /** Returns the default instance. */
  public ElementConstraintProto getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

  /**
   * <pre>
   * Legacy field.
   * </pre>
   */
  public int getIndex() {
    return index_;
  }

  /**
   * <pre>
   * Legacy field.
   * </pre>
   */
  public int getTarget() {
    return target_;
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

  /** Returns true if {@code linear_index} is set. */
  public boolean hasLinearIndex() {
    return linearIndex_ != null;
  }

  /** Returns {@code linear_index}, or the default instance if it is not set. */
  public LinearExpressionProto getLinearIndex() {
    return linearIndex_ == null ? LinearExpressionProto.getDefaultInstance() : linearIndex_;
  }

  /** Returns true if {@code linear_target} is set. */
  public boolean hasLinearTarget() {
    return linearTarget_ != null;
  }

  /** Returns {@code linear_target}, or the default instance if it is not set. */
  public LinearExpressionProto getLinearTarget() {
    return linearTarget_ == null ? LinearExpressionProto.getDefaultInstance() : linearTarget_;
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

  void copyFieldsFrom(ElementConstraintProto src) {
    index_ = src.index_;
    target_ = src.target_;
    vars_ = src.vars_ == null || src.vars_.isEmpty() ? null : new java.util.ArrayList<>(src.vars_);
    linearIndex_ = src.linearIndex_ == null ? null : src.linearIndex_.immutableCopy();
    linearTarget_ = src.linearTarget_ == null ? null : src.linearTarget_.immutableCopy();
    exprs_ = buildListLinearExpressionProto(src.exprs_);
  }

  /** Returns this message if it is immutable, otherwise a built copy. */
  ElementConstraintProto immutableCopy() {
    if (this instanceof Builder) {
      ElementConstraintProto r = new ElementConstraintProto();
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
    if (index_ != 0) {
      printer.printScalar("index", index_);
    }
    if (target_ != 0) {
      printer.printScalar("target", target_);
    }
    if (vars_ != null) {
      for (Integer v : vars_) {
        printer.printScalar("vars", v);
      }
    }
    if (linearIndex_ != null) {
      printer.printMessage("linear_index", linearIndex_);
    }
    if (linearTarget_ != null) {
      printer.printMessage("linear_target", linearTarget_);
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
    if (!(obj instanceof ElementConstraintProto)) {
      return false;
    }
    ElementConstraintProto other = (ElementConstraintProto) obj;
    return index_ == other.index_
        && target_ == other.target_
        && getVarsList().equals(other.getVarsList())
        && java.util.Objects.equals(linearIndex_, other.linearIndex_)
        && java.util.Objects.equals(linearTarget_, other.linearTarget_)
        && getExprsList().equals(other.getExprsList());
  }

  @Override
  public int hashCode() {
    int h = 237;
    h = 31 * h + Integer.hashCode(index_);
    h = 31 * h + Integer.hashCode(target_);
    h = 31 * h + getVarsList().hashCode();
    h = 31 * h + java.util.Objects.hashCode(linearIndex_);
    h = 31 * h + java.util.Objects.hashCode(linearTarget_);
    h = 31 * h + getExprsList().hashCode();
    return h;
  }

  /** Appends the fields of this message in the protobuf binary format. */
  void writeTo(ProtoWire.Writer w) {
    if (index_ != 0) {
      w.int32(1, index_);
    }
    if (target_ != 0) {
      w.int32(2, target_);
    }
    if (vars_ != null && !vars_.isEmpty()) {
      w.packedInt32(3, vars_);
    }
    if (linearIndex_ != null) {
      ProtoWire.Writer sub = new ProtoWire.Writer();
      linearIndex_.writeTo(sub);
      w.bytes(4, sub);
    }
    if (linearTarget_ != null) {
      ProtoWire.Writer sub = new ProtoWire.Writer();
      linearTarget_.writeTo(sub);
      w.bytes(5, sub);
    }
    if (exprs_ != null) {
      for (LinearExpressionProto v : exprs_) {
        ProtoWire.Writer sub = new ProtoWire.Writer();
        v.writeTo(sub);
        w.bytes(6, sub);
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
  public static ElementConstraintProto parseFrom(byte[] data) {
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
  public static Builder newBuilder(ElementConstraintProto prototype) {
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
   * Builder of {@link ElementConstraintProto}. A builder is also a (mutable) {@code ElementConstraintProto}, so all getters are
   * available on it.
   */
  public static final class Builder extends ElementConstraintProto implements Cloneable {
    Builder() {}

    /** Returns an immutable copy of the current content. */
    public ElementConstraintProto build() {
      ElementConstraintProto r = new ElementConstraintProto();
      r.copyFieldsFrom(this);
      return r;
    }

    /** Same as {@link #build()}. */
    public ElementConstraintProto buildPartial() {
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
    public Builder mergeFrom(ElementConstraintProto other) {
      if (other == DEFAULT_INSTANCE) {
        return this;
      }
      if (other.index_ != 0) {
        setIndex(other.index_);
      }
      if (other.target_ != 0) {
        setTarget(other.target_);
      }
      if (other.vars_ != null) {
        addAllVars(other.vars_);
      }
      if (other.linearIndex_ != null) {
        mergeLinearIndex(other.linearIndex_);
      }
      if (other.linearTarget_ != null) {
        mergeLinearTarget(other.linearTarget_);
      }
      if (other.exprs_ != null) {
        addAllExprs(other.exprs_);
      }
      return this;
    }

    /** Replaces the content of this builder by a deep copy of {@code other}. */
    public void copyFrom(ElementConstraintProto other) {
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
          case 8:
            setIndex((int) r.varint());
            break;
          case 16:
            setTarget((int) r.varint());
            break;
          case 26: {
            ProtoWire.Reader p = r.packed();
            while (!p.atEnd()) {
              addVars((int) p.varint());
            }
            break;
          }
          case 24:
            addVars((int) r.varint());
            break;
          case 34:
            getLinearIndexBuilder().mergeWire(r.message());
            break;
          case 42:
            getLinearTargetBuilder().mergeWire(r.message());
            break;
          case 50:
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
          case "index": {
            if (strict && index_ != 0) {
              throw ProtoWire.duplicate(t, "index");
            }
            t.consume(":");
            setIndex((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "target": {
            if (strict && target_ != 0) {
              throw ProtoWire.duplicate(t, "target");
            }
            t.consume(":");
            setTarget((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
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
          case "linear_index": {
            if (strict && linearIndex_ != null) {
              throw ProtoWire.duplicate(t, "linear_index");
            }
            t.tryConsume(":");
            getLinearIndexBuilder().mergeText(t, t.openMessage(), strict);
            break;
          }
          case "linear_target": {
            if (strict && linearTarget_ != null) {
              throw ProtoWire.duplicate(t, "linear_target");
            }
            t.tryConsume(":");
            getLinearTargetBuilder().mergeText(t, t.openMessage(), strict);
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
          default:
            throw ProtoWire.unknownField(t, "operations_research.sat.ElementConstraintProto", name);
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

    /** Sets {@code index}. */
    public Builder setIndex(int value) {
      index_ = value;
      return this;
    }

    /** Resets {@code index} to its default value. */
    public Builder clearIndex() {
      index_ = 0;
      return this;
    }

    /** Sets {@code target}. */
    public Builder setTarget(int value) {
      target_ = value;
      return this;
    }

    /** Resets {@code target} to its default value. */
    public Builder clearTarget() {
      target_ = 0;
      return this;
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

    /** Sets {@code linear_index}. */
    public Builder setLinearIndex(LinearExpressionProto value) {
      if (value == null) {
        throw new NullPointerException();
      }
      linearIndex_ = value.immutableCopy();
      return this;
    }

    /** Sets {@code linear_index} to the content of a builder. */
    public Builder setLinearIndex(LinearExpressionProto.Builder builderForValue) {
      return setLinearIndex(builderForValue.build());
    }

    /** Merges {@code value} into {@code linear_index}. */
    public Builder mergeLinearIndex(LinearExpressionProto value) {
      if (linearIndex_ != null) {
        getLinearIndexBuilder().mergeFrom(value);
      } else {
        setLinearIndex(value.immutableCopy());
      }
      return this;
    }

    /** Clears {@code linear_index}. */
    public Builder clearLinearIndex() {
      if (true) {
        linearIndex_ = null;
      }
      return this;
    }

    /** Returns a builder for {@code linear_index}, setting it if needed; changes to it are reflected in this builder. */
    public LinearExpressionProto.Builder getLinearIndexBuilder() {
      if (linearIndex_ != null && linearIndex_ instanceof LinearExpressionProto.Builder) {
        return (LinearExpressionProto.Builder) linearIndex_;
      }
      LinearExpressionProto.Builder b = linearIndex_ != null ? LinearExpressionProto.newBuilder(linearIndex_) : LinearExpressionProto.newBuilder();
      setLinearIndexRaw(b);
      return b;
    }

    private void setLinearIndexRaw(LinearExpressionProto.Builder b) {
      linearIndex_ = b;
    }

    /** Sets {@code linear_target}. */
    public Builder setLinearTarget(LinearExpressionProto value) {
      if (value == null) {
        throw new NullPointerException();
      }
      linearTarget_ = value.immutableCopy();
      return this;
    }

    /** Sets {@code linear_target} to the content of a builder. */
    public Builder setLinearTarget(LinearExpressionProto.Builder builderForValue) {
      return setLinearTarget(builderForValue.build());
    }

    /** Merges {@code value} into {@code linear_target}. */
    public Builder mergeLinearTarget(LinearExpressionProto value) {
      if (linearTarget_ != null) {
        getLinearTargetBuilder().mergeFrom(value);
      } else {
        setLinearTarget(value.immutableCopy());
      }
      return this;
    }

    /** Clears {@code linear_target}. */
    public Builder clearLinearTarget() {
      if (true) {
        linearTarget_ = null;
      }
      return this;
    }

    /** Returns a builder for {@code linear_target}, setting it if needed; changes to it are reflected in this builder. */
    public LinearExpressionProto.Builder getLinearTargetBuilder() {
      if (linearTarget_ != null && linearTarget_ instanceof LinearExpressionProto.Builder) {
        return (LinearExpressionProto.Builder) linearTarget_;
      }
      LinearExpressionProto.Builder b = linearTarget_ != null ? LinearExpressionProto.newBuilder(linearTarget_) : LinearExpressionProto.newBuilder();
      setLinearTargetRaw(b);
      return b;
    }

    private void setLinearTargetRaw(LinearExpressionProto.Builder b) {
      linearTarget_ = b;
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

  }
}
