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
 * Plain Java replacement of the protobuf message {@code operations_research.sat.LinearArgumentProto} of OR-Tools (generated from ortools/sat/cp_model.proto, no protobuf runtime needed). Instances are immutable; use {@link Builder} to create or modify them.
 */
public class LinearArgumentProto implements ProtoMessage {
  private static final LinearArgumentProto DEFAULT_INSTANCE = new LinearArgumentProto();

  LinearExpressionProto target_;
  java.util.ArrayList<LinearExpressionProto> exprs_;

  private LinearArgumentProto() {}

  /** Returns the immutable default instance (all fields unset). */
  public static LinearArgumentProto getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  /** Returns the default instance. */
  public LinearArgumentProto getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

  /** Returns true if {@code target} is set. */
  public boolean hasTarget() {
    return target_ != null;
  }

  /** Returns {@code target}, or the default instance if it is not set. */
  public LinearExpressionProto getTarget() {
    return target_ == null ? LinearExpressionProto.getDefaultInstance() : target_;
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

  void copyFieldsFrom(LinearArgumentProto src) {
    target_ = src.target_ == null ? null : src.target_.immutableCopy();
    exprs_ = buildListLinearExpressionProto(src.exprs_);
  }

  /** Returns this message if it is immutable, otherwise a built copy. */
  LinearArgumentProto immutableCopy() {
    if (this instanceof Builder) {
      LinearArgumentProto r = new LinearArgumentProto();
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
    if (target_ != null) {
      printer.printMessage("target", target_);
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
    if (!(obj instanceof LinearArgumentProto)) {
      return false;
    }
    LinearArgumentProto other = (LinearArgumentProto) obj;
    return java.util.Objects.equals(target_, other.target_)
        && getExprsList().equals(other.getExprsList());
  }

  @Override
  public int hashCode() {
    int h = 149;
    h = 31 * h + java.util.Objects.hashCode(target_);
    h = 31 * h + getExprsList().hashCode();
    return h;
  }

  /** Appends the fields of this message in the protobuf binary format. */
  void writeTo(ProtoWire.Writer w) {
    if (target_ != null) {
      ProtoWire.Writer sub = new ProtoWire.Writer();
      target_.writeTo(sub);
      w.bytes(1, sub);
    }
    if (exprs_ != null) {
      for (LinearExpressionProto v : exprs_) {
        ProtoWire.Writer sub = new ProtoWire.Writer();
        v.writeTo(sub);
        w.bytes(2, sub);
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
  public static LinearArgumentProto parseFrom(byte[] data) {
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
  public static Builder newBuilder(LinearArgumentProto prototype) {
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
   * Builder of {@link LinearArgumentProto}. A builder is also a (mutable) {@code LinearArgumentProto}, so all getters are
   * available on it.
   */
  public static final class Builder extends LinearArgumentProto implements Cloneable {
    Builder() {}

    /** Returns an immutable copy of the current content. */
    public LinearArgumentProto build() {
      LinearArgumentProto r = new LinearArgumentProto();
      r.copyFieldsFrom(this);
      return r;
    }

    /** Same as {@link #build()}. */
    public LinearArgumentProto buildPartial() {
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
    public Builder mergeFrom(LinearArgumentProto other) {
      if (other == DEFAULT_INSTANCE) {
        return this;
      }
      if (other.target_ != null) {
        mergeTarget(other.target_);
      }
      if (other.exprs_ != null) {
        addAllExprs(other.exprs_);
      }
      return this;
    }

    /** Replaces the content of this builder by a deep copy of {@code other}. */
    public void copyFrom(LinearArgumentProto other) {
      copyFieldsFrom(other);
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
          case 10:
            getTargetBuilder().mergeWire(r.message());
            break;
          case 18:
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
          case "target": {
            if (strict && target_ != null) {
              throw ProtoWire.duplicate(t, "target");
            }
            t.tryConsume(":");
            getTargetBuilder().mergeText(t, t.openMessage(), strict);
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
            throw ProtoWire.unknownField(t, "operations_research.sat.LinearArgumentProto", name);
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

    /** Sets {@code target}. */
    public Builder setTarget(LinearExpressionProto value) {
      if (value == null) {
        throw new NullPointerException();
      }
      target_ = value.immutableCopy();
      return this;
    }

    /** Sets {@code target} to the content of a builder. */
    public Builder setTarget(LinearExpressionProto.Builder builderForValue) {
      return setTarget(builderForValue.build());
    }

    /** Merges {@code value} into {@code target}. */
    public Builder mergeTarget(LinearExpressionProto value) {
      if (target_ != null) {
        getTargetBuilder().mergeFrom(value);
      } else {
        setTarget(value.immutableCopy());
      }
      return this;
    }

    /** Clears {@code target}. */
    public Builder clearTarget() {
      if (true) {
        target_ = null;
      }
      return this;
    }

    /** Returns a builder for {@code target}, setting it if needed; changes to it are reflected in this builder. */
    public LinearExpressionProto.Builder getTargetBuilder() {
      if (target_ != null && target_ instanceof LinearExpressionProto.Builder) {
        return (LinearExpressionProto.Builder) target_;
      }
      LinearExpressionProto.Builder b = target_ != null ? LinearExpressionProto.newBuilder(target_) : LinearExpressionProto.newBuilder();
      setTargetRaw(b);
      return b;
    }

    private void setTargetRaw(LinearExpressionProto.Builder b) {
      target_ = b;
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
