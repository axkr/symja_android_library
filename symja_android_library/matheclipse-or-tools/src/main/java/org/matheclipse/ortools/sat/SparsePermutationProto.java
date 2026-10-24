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
 * A permutation of integers encoded as a list of cycles, hence the "sparse"
 * format. The image of an element cycle[i] is cycle[(i + 1) % cycle_length].
 * </pre>
 *
 * <p>
 * Plain Java replacement of the protobuf message {@code operations_research.sat.SparsePermutationProto} of OR-Tools (generated from ortools/sat/cp_model.proto, no protobuf runtime needed). Instances are immutable; use {@link Builder} to create or modify them.
 */
public class SparsePermutationProto implements ProtoMessage {
  private static final SparsePermutationProto DEFAULT_INSTANCE = new SparsePermutationProto();

  java.util.ArrayList<Integer> support_;
  java.util.ArrayList<Integer> cycleSizes_;

  private SparsePermutationProto() {}

  /** Returns the immutable default instance (all fields unset). */
  public static SparsePermutationProto getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  /** Returns the default instance. */
  public SparsePermutationProto getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

  /**
   * <pre>
   * Each cycle is listed one after the other in the support field.
   * The size of each cycle is given (in order) in the cycle_sizes field.
   * </pre>
   */
  public java.util.List<Integer> getSupportList() {
    return support_ == null ? java.util.Collections.<Integer>emptyList() : java.util.Collections.unmodifiableList(support_);
  }

  /** Returns the number of elements of {@code support}. */
  public int getSupportCount() {
    return support_ == null ? 0 : support_.size();
  }

  /** Returns the element {@code index} of {@code support}. */
  public int getSupport(int index) {
    if (support_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return support_.get(index);
  }

  /** Returns the elements of {@code cycle_sizes}. */
  public java.util.List<Integer> getCycleSizesList() {
    return cycleSizes_ == null ? java.util.Collections.<Integer>emptyList() : java.util.Collections.unmodifiableList(cycleSizes_);
  }

  /** Returns the number of elements of {@code cycle_sizes}. */
  public int getCycleSizesCount() {
    return cycleSizes_ == null ? 0 : cycleSizes_.size();
  }

  /** Returns the element {@code index} of {@code cycle_sizes}. */
  public int getCycleSizes(int index) {
    if (cycleSizes_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return cycleSizes_.get(index);
  }

  void copyFieldsFrom(SparsePermutationProto src) {
    support_ = src.support_ == null || src.support_.isEmpty() ? null : new java.util.ArrayList<>(src.support_);
    cycleSizes_ = src.cycleSizes_ == null || src.cycleSizes_.isEmpty() ? null : new java.util.ArrayList<>(src.cycleSizes_);
  }

  /** Returns this message if it is immutable, otherwise a built copy. */
  SparsePermutationProto immutableCopy() {
    if (this instanceof Builder) {
      SparsePermutationProto r = new SparsePermutationProto();
      r.copyFieldsFrom(this);
      return r;
    }
    return this;
  }

  @Override
  public void printTo(ProtoPrinter printer) {
    if (support_ != null) {
      for (Integer v : support_) {
        printer.printScalar("support", v);
      }
    }
    if (cycleSizes_ != null) {
      for (Integer v : cycleSizes_) {
        printer.printScalar("cycle_sizes", v);
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
    if (!(obj instanceof SparsePermutationProto)) {
      return false;
    }
    SparsePermutationProto other = (SparsePermutationProto) obj;
    return getSupportList().equals(other.getSupportList())
        && getCycleSizesList().equals(other.getCycleSizesList());
  }

  @Override
  public int hashCode() {
    int h = 838;
    h = 31 * h + getSupportList().hashCode();
    h = 31 * h + getCycleSizesList().hashCode();
    return h;
  }

  /** Appends the fields of this message in the protobuf binary format. */
  void writeTo(ProtoWire.Writer w) {
    if (support_ != null && !support_.isEmpty()) {
      w.packedInt32(1, support_);
    }
    if (cycleSizes_ != null && !cycleSizes_.isEmpty()) {
      w.packedInt32(2, cycleSizes_);
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
  public static SparsePermutationProto parseFrom(byte[] data) {
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
  public static Builder newBuilder(SparsePermutationProto prototype) {
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
   * Builder of {@link SparsePermutationProto}. A builder is also a (mutable) {@code SparsePermutationProto}, so all getters are
   * available on it.
   */
  public static final class Builder extends SparsePermutationProto implements Cloneable {
    Builder() {}

    /** Returns an immutable copy of the current content. */
    public SparsePermutationProto build() {
      SparsePermutationProto r = new SparsePermutationProto();
      r.copyFieldsFrom(this);
      return r;
    }

    /** Same as {@link #build()}. */
    public SparsePermutationProto buildPartial() {
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
    public Builder mergeFrom(SparsePermutationProto other) {
      if (other == DEFAULT_INSTANCE) {
        return this;
      }
      if (other.support_ != null) {
        addAllSupport(other.support_);
      }
      if (other.cycleSizes_ != null) {
        addAllCycleSizes(other.cycleSizes_);
      }
      return this;
    }

    /** Replaces the content of this builder by a deep copy of {@code other}. */
    public void copyFrom(SparsePermutationProto other) {
      copyFieldsFrom(other);
    }

    /**
     * Returns a modifiable view of {@code support}: elements added or set through the list are
     * stored in this builder (messages as immutable copies). The view is detached by
     * {@code clearSupport()} and {@code clear()}.
     */
    @Override
    public java.util.List<Integer> getSupportList() {
      if (support_ == null) {
        support_ = new java.util.ArrayList<>();
      }
      return new ProtoWire.ListView<>(support_, null);
    }

    /**
     * Returns a modifiable view of {@code cycle_sizes}: elements added or set through the list are
     * stored in this builder (messages as immutable copies). The view is detached by
     * {@code clearCycleSizes()} and {@code clear()}.
     */
    @Override
    public java.util.List<Integer> getCycleSizesList() {
      if (cycleSizes_ == null) {
        cycleSizes_ = new java.util.ArrayList<>();
      }
      return new ProtoWire.ListView<>(cycleSizes_, null);
    }

    void mergeWire(ProtoWire.Reader r) {
      while (!r.atEnd()) {
        int tag = r.tag();
        switch (tag) {
          case 10: {
            ProtoWire.Reader p = r.packed();
            while (!p.atEnd()) {
              addSupport((int) p.varint());
            }
            break;
          }
          case 8:
            addSupport((int) r.varint());
            break;
          case 18: {
            ProtoWire.Reader p = r.packed();
            while (!p.atEnd()) {
              addCycleSizes((int) p.varint());
            }
            break;
          }
          case 16:
            addCycleSizes((int) r.varint());
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
          case "support": {
            t.consume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addSupport((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
                } while (t.nextInList());
              }
            } else {
              addSupport((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            }
            break;
          }
          case "cycle_sizes": {
            t.consume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addCycleSizes((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
                } while (t.nextInList());
              }
            } else {
              addCycleSizes((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            }
            break;
          }
          default:
            throw ProtoWire.unknownField(t, "operations_research.sat.SparsePermutationProto", name);
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

    /** Replaces the element {@code index} of {@code support}. */
    public Builder setSupport(int index, int value) {
      if (support_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      support_.set(index, value);
      return this;
    }

    /** Appends a value to {@code support}. */
    public Builder addSupport(int value) {
      if (support_ == null) {
        support_ = new java.util.ArrayList<>();
      }
      support_.add(value);
      return this;
    }

    /** Appends all values to {@code support}. */
    public Builder addAllSupport(Iterable<? extends Integer> values) {
      if (support_ == null) {
        support_ = new java.util.ArrayList<>();
      }
      for (Integer v : values) {
        support_.add(v);
      }
      return this;
    }

    /** Removes all values of {@code support}. */
    public Builder clearSupport() {
      support_ = null;
      return this;
    }

    /** Replaces the element {@code index} of {@code cycle_sizes}. */
    public Builder setCycleSizes(int index, int value) {
      if (cycleSizes_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      cycleSizes_.set(index, value);
      return this;
    }

    /** Appends a value to {@code cycle_sizes}. */
    public Builder addCycleSizes(int value) {
      if (cycleSizes_ == null) {
        cycleSizes_ = new java.util.ArrayList<>();
      }
      cycleSizes_.add(value);
      return this;
    }

    /** Appends all values to {@code cycle_sizes}. */
    public Builder addAllCycleSizes(Iterable<? extends Integer> values) {
      if (cycleSizes_ == null) {
        cycleSizes_ = new java.util.ArrayList<>();
      }
      for (Integer v : values) {
        cycleSizes_.add(v);
      }
      return this;
    }

    /** Removes all values of {@code cycle_sizes}. */
    public Builder clearCycleSizes() {
      cycleSizes_ = null;
      return this;
    }

  }
}
