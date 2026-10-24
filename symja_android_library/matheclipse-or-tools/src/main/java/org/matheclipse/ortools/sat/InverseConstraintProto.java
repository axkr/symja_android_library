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
 * The two arrays of expressions each represent a function, the second is the
 * inverse of the first: f_expr_direct[i] == j &lt;=&gt; f_expr_inverse[j] == i.
 * </pre>
 *
 * <p>
 * Plain Java replacement of the protobuf message {@code operations_research.sat.InverseConstraintProto} of OR-Tools (generated from ortools/sat/cp_model.proto, no protobuf runtime needed). Instances are immutable; use {@link Builder} to create or modify them.
 */
public class InverseConstraintProto implements ProtoMessage {
  private static final InverseConstraintProto DEFAULT_INSTANCE = new InverseConstraintProto();

  java.util.ArrayList<Integer> fDirect_;
  java.util.ArrayList<Integer> fInverse_;
  java.util.ArrayList<LinearExpressionProto> fExprDirect_;
  java.util.ArrayList<LinearExpressionProto> fExprInverse_;

  private InverseConstraintProto() {}

  /** Returns the immutable default instance (all fields unset). */
  public static InverseConstraintProto getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  /** Returns the default instance. */
  public InverseConstraintProto getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

  /**
   * <pre>
   * Legacy field.
   * </pre>
   */
  public java.util.List<Integer> getFDirectList() {
    return fDirect_ == null ? java.util.Collections.<Integer>emptyList() : java.util.Collections.unmodifiableList(fDirect_);
  }

  /** Returns the number of elements of {@code f_direct}. */
  public int getFDirectCount() {
    return fDirect_ == null ? 0 : fDirect_.size();
  }

  /** Returns the element {@code index} of {@code f_direct}. */
  public int getFDirect(int index) {
    if (fDirect_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return fDirect_.get(index);
  }

  /**
   * <pre>
   * Legacy field.
   * </pre>
   */
  public java.util.List<Integer> getFInverseList() {
    return fInverse_ == null ? java.util.Collections.<Integer>emptyList() : java.util.Collections.unmodifiableList(fInverse_);
  }

  /** Returns the number of elements of {@code f_inverse}. */
  public int getFInverseCount() {
    return fInverse_ == null ? 0 : fInverse_.size();
  }

  /** Returns the element {@code index} of {@code f_inverse}. */
  public int getFInverse(int index) {
    if (fInverse_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return fInverse_.get(index);
  }

  /** Returns the elements of {@code f_expr_direct}. */
  public java.util.List<LinearExpressionProto> getFExprDirectList() {
    return fExprDirect_ == null ? java.util.Collections.<LinearExpressionProto>emptyList() : java.util.Collections.unmodifiableList(fExprDirect_);
  }

  /** Returns the number of elements of {@code f_expr_direct}. */
  public int getFExprDirectCount() {
    return fExprDirect_ == null ? 0 : fExprDirect_.size();
  }

  /** Returns the element {@code index} of {@code f_expr_direct}. */
  public LinearExpressionProto getFExprDirect(int index) {
    if (fExprDirect_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return fExprDirect_.get(index);
  }

  /** Returns the elements of {@code f_expr_inverse}. */
  public java.util.List<LinearExpressionProto> getFExprInverseList() {
    return fExprInverse_ == null ? java.util.Collections.<LinearExpressionProto>emptyList() : java.util.Collections.unmodifiableList(fExprInverse_);
  }

  /** Returns the number of elements of {@code f_expr_inverse}. */
  public int getFExprInverseCount() {
    return fExprInverse_ == null ? 0 : fExprInverse_.size();
  }

  /** Returns the element {@code index} of {@code f_expr_inverse}. */
  public LinearExpressionProto getFExprInverse(int index) {
    if (fExprInverse_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return fExprInverse_.get(index);
  }

  void copyFieldsFrom(InverseConstraintProto src) {
    fDirect_ = src.fDirect_ == null || src.fDirect_.isEmpty() ? null : new java.util.ArrayList<>(src.fDirect_);
    fInverse_ = src.fInverse_ == null || src.fInverse_.isEmpty() ? null : new java.util.ArrayList<>(src.fInverse_);
    fExprDirect_ = buildListLinearExpressionProto(src.fExprDirect_);
    fExprInverse_ = buildListLinearExpressionProto(src.fExprInverse_);
  }

  /** Returns this message if it is immutable, otherwise a built copy. */
  InverseConstraintProto immutableCopy() {
    if (this instanceof Builder) {
      InverseConstraintProto r = new InverseConstraintProto();
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
    if (fDirect_ != null) {
      for (Integer v : fDirect_) {
        printer.printScalar("f_direct", v);
      }
    }
    if (fInverse_ != null) {
      for (Integer v : fInverse_) {
        printer.printScalar("f_inverse", v);
      }
    }
    if (fExprDirect_ != null) {
      for (LinearExpressionProto v : fExprDirect_) {
        printer.printMessage("f_expr_direct", v);
      }
    }
    if (fExprInverse_ != null) {
      for (LinearExpressionProto v : fExprInverse_) {
        printer.printMessage("f_expr_inverse", v);
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
    if (!(obj instanceof InverseConstraintProto)) {
      return false;
    }
    InverseConstraintProto other = (InverseConstraintProto) obj;
    return getFDirectList().equals(other.getFDirectList())
        && getFInverseList().equals(other.getFInverseList())
        && getFExprDirectList().equals(other.getFExprDirectList())
        && getFExprInverseList().equals(other.getFExprInverseList());
  }

  @Override
  public int hashCode() {
    int h = 1008;
    h = 31 * h + getFDirectList().hashCode();
    h = 31 * h + getFInverseList().hashCode();
    h = 31 * h + getFExprDirectList().hashCode();
    h = 31 * h + getFExprInverseList().hashCode();
    return h;
  }

  /** Appends the fields of this message in the protobuf binary format. */
  void writeTo(ProtoWire.Writer w) {
    if (fDirect_ != null && !fDirect_.isEmpty()) {
      w.packedInt32(1, fDirect_);
    }
    if (fInverse_ != null && !fInverse_.isEmpty()) {
      w.packedInt32(2, fInverse_);
    }
    if (fExprDirect_ != null) {
      for (LinearExpressionProto v : fExprDirect_) {
        ProtoWire.Writer sub = new ProtoWire.Writer();
        v.writeTo(sub);
        w.bytes(3, sub);
      }
    }
    if (fExprInverse_ != null) {
      for (LinearExpressionProto v : fExprInverse_) {
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
  public static InverseConstraintProto parseFrom(byte[] data) {
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
  public static Builder newBuilder(InverseConstraintProto prototype) {
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
   * Builder of {@link InverseConstraintProto}. A builder is also a (mutable) {@code InverseConstraintProto}, so all getters are
   * available on it.
   */
  public static final class Builder extends InverseConstraintProto implements Cloneable {
    Builder() {}

    /** Returns an immutable copy of the current content. */
    public InverseConstraintProto build() {
      InverseConstraintProto r = new InverseConstraintProto();
      r.copyFieldsFrom(this);
      return r;
    }

    /** Same as {@link #build()}. */
    public InverseConstraintProto buildPartial() {
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
    public Builder mergeFrom(InverseConstraintProto other) {
      if (other == DEFAULT_INSTANCE) {
        return this;
      }
      if (other.fDirect_ != null) {
        addAllFDirect(other.fDirect_);
      }
      if (other.fInverse_ != null) {
        addAllFInverse(other.fInverse_);
      }
      if (other.fExprDirect_ != null) {
        addAllFExprDirect(other.fExprDirect_);
      }
      if (other.fExprInverse_ != null) {
        addAllFExprInverse(other.fExprInverse_);
      }
      return this;
    }

    /** Replaces the content of this builder by a deep copy of {@code other}. */
    public void copyFrom(InverseConstraintProto other) {
      copyFieldsFrom(other);
    }

    /**
     * Returns a modifiable view of {@code f_direct}: elements added or set through the list are
     * stored in this builder (messages as immutable copies). The view is detached by
     * {@code clearFDirect()} and {@code clear()}.
     */
    @Override
    public java.util.List<Integer> getFDirectList() {
      if (fDirect_ == null) {
        fDirect_ = new java.util.ArrayList<>();
      }
      return new ProtoWire.ListView<>(fDirect_, null);
    }

    /**
     * Returns a modifiable view of {@code f_inverse}: elements added or set through the list are
     * stored in this builder (messages as immutable copies). The view is detached by
     * {@code clearFInverse()} and {@code clear()}.
     */
    @Override
    public java.util.List<Integer> getFInverseList() {
      if (fInverse_ == null) {
        fInverse_ = new java.util.ArrayList<>();
      }
      return new ProtoWire.ListView<>(fInverse_, null);
    }

    /**
     * Returns a modifiable view of {@code f_expr_direct}: elements added or set through the list are
     * stored in this builder (messages as immutable copies). The view is detached by
     * {@code clearFExprDirect()} and {@code clear()}.
     */
    @Override
    public java.util.List<LinearExpressionProto> getFExprDirectList() {
      if (fExprDirect_ == null) {
        fExprDirect_ = new java.util.ArrayList<>();
      }
      return new ProtoWire.ListView<>(fExprDirect_, LinearExpressionProto::immutableCopy);
    }

    /**
     * Returns a modifiable view of {@code f_expr_inverse}: elements added or set through the list are
     * stored in this builder (messages as immutable copies). The view is detached by
     * {@code clearFExprInverse()} and {@code clear()}.
     */
    @Override
    public java.util.List<LinearExpressionProto> getFExprInverseList() {
      if (fExprInverse_ == null) {
        fExprInverse_ = new java.util.ArrayList<>();
      }
      return new ProtoWire.ListView<>(fExprInverse_, LinearExpressionProto::immutableCopy);
    }

    void mergeWire(ProtoWire.Reader r) {
      while (!r.atEnd()) {
        int tag = r.tag();
        switch (tag) {
          case 10: {
            ProtoWire.Reader p = r.packed();
            while (!p.atEnd()) {
              addFDirect((int) p.varint());
            }
            break;
          }
          case 8:
            addFDirect((int) r.varint());
            break;
          case 18: {
            ProtoWire.Reader p = r.packed();
            while (!p.atEnd()) {
              addFInverse((int) p.varint());
            }
            break;
          }
          case 16:
            addFInverse((int) r.varint());
            break;
          case 26:
            addFExprDirectBuilder().mergeWire(r.message());
            break;
          case 34:
            addFExprInverseBuilder().mergeWire(r.message());
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
          case "f_direct": {
            t.consume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addFDirect((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
                } while (t.nextInList());
              }
            } else {
              addFDirect((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            }
            break;
          }
          case "f_inverse": {
            t.consume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addFInverse((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
                } while (t.nextInList());
              }
            } else {
              addFInverse((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            }
            break;
          }
          case "f_expr_direct": {
            t.tryConsume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addFExprDirectBuilder().mergeText(t, t.openMessage(), strict);
                } while (t.nextInList());
              }
            } else {
              addFExprDirectBuilder().mergeText(t, t.openMessage(), strict);
            }
            break;
          }
          case "f_expr_inverse": {
            t.tryConsume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addFExprInverseBuilder().mergeText(t, t.openMessage(), strict);
                } while (t.nextInList());
              }
            } else {
              addFExprInverseBuilder().mergeText(t, t.openMessage(), strict);
            }
            break;
          }
          default:
            throw ProtoWire.unknownField(t, "operations_research.sat.InverseConstraintProto", name);
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

    /** Replaces the element {@code index} of {@code f_direct}. */
    public Builder setFDirect(int index, int value) {
      if (fDirect_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      fDirect_.set(index, value);
      return this;
    }

    /** Appends a value to {@code f_direct}. */
    public Builder addFDirect(int value) {
      if (fDirect_ == null) {
        fDirect_ = new java.util.ArrayList<>();
      }
      fDirect_.add(value);
      return this;
    }

    /** Appends all values to {@code f_direct}. */
    public Builder addAllFDirect(Iterable<? extends Integer> values) {
      if (fDirect_ == null) {
        fDirect_ = new java.util.ArrayList<>();
      }
      for (Integer v : values) {
        fDirect_.add(v);
      }
      return this;
    }

    /** Removes all values of {@code f_direct}. */
    public Builder clearFDirect() {
      fDirect_ = null;
      return this;
    }

    /** Replaces the element {@code index} of {@code f_inverse}. */
    public Builder setFInverse(int index, int value) {
      if (fInverse_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      fInverse_.set(index, value);
      return this;
    }

    /** Appends a value to {@code f_inverse}. */
    public Builder addFInverse(int value) {
      if (fInverse_ == null) {
        fInverse_ = new java.util.ArrayList<>();
      }
      fInverse_.add(value);
      return this;
    }

    /** Appends all values to {@code f_inverse}. */
    public Builder addAllFInverse(Iterable<? extends Integer> values) {
      if (fInverse_ == null) {
        fInverse_ = new java.util.ArrayList<>();
      }
      for (Integer v : values) {
        fInverse_.add(v);
      }
      return this;
    }

    /** Removes all values of {@code f_inverse}. */
    public Builder clearFInverse() {
      fInverse_ = null;
      return this;
    }

    /** Replaces the element {@code index} of {@code f_expr_direct}. */
    public Builder setFExprDirect(int index, LinearExpressionProto value) {
      if (fExprDirect_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      fExprDirect_.set(index, java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Appends a value to {@code f_expr_direct}. */
    public Builder addFExprDirect(LinearExpressionProto value) {
      if (fExprDirect_ == null) {
        fExprDirect_ = new java.util.ArrayList<>();
      }
      fExprDirect_.add(java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Appends all values to {@code f_expr_direct}. */
    public Builder addAllFExprDirect(Iterable<? extends LinearExpressionProto> values) {
      if (fExprDirect_ == null) {
        fExprDirect_ = new java.util.ArrayList<>();
      }
      for (LinearExpressionProto v : values) {
        fExprDirect_.add(java.util.Objects.requireNonNull(v).immutableCopy());
      }
      return this;
    }

    /** Removes all values of {@code f_expr_direct}. */
    public Builder clearFExprDirect() {
      fExprDirect_ = null;
      return this;
    }

    /** Replaces the element {@code index} of {@code f_expr_direct} by the content of a builder. */
    public Builder setFExprDirect(int index, LinearExpressionProto.Builder builderForValue) {
      return setFExprDirect(index, builderForValue.build());
    }

    /** Appends the content of a builder to {@code f_expr_direct}. */
    public Builder addFExprDirect(LinearExpressionProto.Builder builderForValue) {
      return addFExprDirect(builderForValue.build());
    }

    /** Inserts a value into {@code f_expr_direct}. */
    public Builder addFExprDirect(int index, LinearExpressionProto value) {
      if (fExprDirect_ == null) {
        fExprDirect_ = new java.util.ArrayList<>();
      }
      fExprDirect_.add(index, java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Removes the element {@code index} of {@code f_expr_direct}. */
    public Builder removeFExprDirect(int index) {
      if (fExprDirect_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      fExprDirect_.remove(index);
      return this;
    }

    /** Returns a builder for the element {@code index} of {@code f_expr_direct}; changes to it are reflected in this builder. */
    public LinearExpressionProto.Builder getFExprDirectBuilder(int index) {
      if (fExprDirect_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      LinearExpressionProto e = fExprDirect_.get(index);
      if (e instanceof LinearExpressionProto.Builder) {
        return (LinearExpressionProto.Builder) e;
      }
      LinearExpressionProto.Builder b = LinearExpressionProto.newBuilder(e);
      fExprDirect_.set(index, b);
      return b;
    }

    /** Appends a new empty element to {@code f_expr_direct} and returns its builder. */
    public LinearExpressionProto.Builder addFExprDirectBuilder() {
      if (fExprDirect_ == null) {
        fExprDirect_ = new java.util.ArrayList<>();
      }
      LinearExpressionProto.Builder b = LinearExpressionProto.newBuilder();
      fExprDirect_.add(b);
      return b;
    }

    /** Returns the builders of all elements of {@code f_expr_direct}. */
    public java.util.List<LinearExpressionProto.Builder> getFExprDirectBuilderList() {
      java.util.List<LinearExpressionProto.Builder> r = new java.util.ArrayList<>();
      for (int k = 0; k < getFExprDirectCount(); k++) {
        r.add(getFExprDirectBuilder(k));
      }
      return r;
    }

    /** Replaces the element {@code index} of {@code f_expr_inverse}. */
    public Builder setFExprInverse(int index, LinearExpressionProto value) {
      if (fExprInverse_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      fExprInverse_.set(index, java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Appends a value to {@code f_expr_inverse}. */
    public Builder addFExprInverse(LinearExpressionProto value) {
      if (fExprInverse_ == null) {
        fExprInverse_ = new java.util.ArrayList<>();
      }
      fExprInverse_.add(java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Appends all values to {@code f_expr_inverse}. */
    public Builder addAllFExprInverse(Iterable<? extends LinearExpressionProto> values) {
      if (fExprInverse_ == null) {
        fExprInverse_ = new java.util.ArrayList<>();
      }
      for (LinearExpressionProto v : values) {
        fExprInverse_.add(java.util.Objects.requireNonNull(v).immutableCopy());
      }
      return this;
    }

    /** Removes all values of {@code f_expr_inverse}. */
    public Builder clearFExprInverse() {
      fExprInverse_ = null;
      return this;
    }

    /** Replaces the element {@code index} of {@code f_expr_inverse} by the content of a builder. */
    public Builder setFExprInverse(int index, LinearExpressionProto.Builder builderForValue) {
      return setFExprInverse(index, builderForValue.build());
    }

    /** Appends the content of a builder to {@code f_expr_inverse}. */
    public Builder addFExprInverse(LinearExpressionProto.Builder builderForValue) {
      return addFExprInverse(builderForValue.build());
    }

    /** Inserts a value into {@code f_expr_inverse}. */
    public Builder addFExprInverse(int index, LinearExpressionProto value) {
      if (fExprInverse_ == null) {
        fExprInverse_ = new java.util.ArrayList<>();
      }
      fExprInverse_.add(index, java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Removes the element {@code index} of {@code f_expr_inverse}. */
    public Builder removeFExprInverse(int index) {
      if (fExprInverse_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      fExprInverse_.remove(index);
      return this;
    }

    /** Returns a builder for the element {@code index} of {@code f_expr_inverse}; changes to it are reflected in this builder. */
    public LinearExpressionProto.Builder getFExprInverseBuilder(int index) {
      if (fExprInverse_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      LinearExpressionProto e = fExprInverse_.get(index);
      if (e instanceof LinearExpressionProto.Builder) {
        return (LinearExpressionProto.Builder) e;
      }
      LinearExpressionProto.Builder b = LinearExpressionProto.newBuilder(e);
      fExprInverse_.set(index, b);
      return b;
    }

    /** Appends a new empty element to {@code f_expr_inverse} and returns its builder. */
    public LinearExpressionProto.Builder addFExprInverseBuilder() {
      if (fExprInverse_ == null) {
        fExprInverse_ = new java.util.ArrayList<>();
      }
      LinearExpressionProto.Builder b = LinearExpressionProto.newBuilder();
      fExprInverse_.add(b);
      return b;
    }

    /** Returns the builders of all elements of {@code f_expr_inverse}. */
    public java.util.List<LinearExpressionProto.Builder> getFExprInverseBuilderList() {
      java.util.List<LinearExpressionProto.Builder> r = new java.util.ArrayList<>();
      for (int k = 0; k < getFExprInverseCount(); k++) {
        r.add(getFExprInverseBuilder(k));
      }
      return r;
    }

  }
}
