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
 * A dense matrix of numbers encoded in a flat way, row by row.
 * That is matrix[i][j] = entries[i * num_cols + j];
 * </pre>
 *
 * <p>
 * Plain Java replacement of the protobuf message {@code operations_research.sat.DenseMatrixProto} of OR-Tools (generated from ortools/sat/cp_model.proto, no protobuf runtime needed). Instances are immutable; use {@link Builder} to create or modify them.
 */
public class DenseMatrixProto implements ProtoMessage {
  private static final DenseMatrixProto DEFAULT_INSTANCE = new DenseMatrixProto();

  int numRows_ = 0;
  int numCols_ = 0;
  java.util.ArrayList<Integer> entries_;

  private DenseMatrixProto() {}

  /** Returns the immutable default instance (all fields unset). */
  public static DenseMatrixProto getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  /** Returns the default instance. */
  public DenseMatrixProto getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

  /** Returns {@code num_rows}. */
  public int getNumRows() {
    return numRows_;
  }

  /** Returns {@code num_cols}. */
  public int getNumCols() {
    return numCols_;
  }

  /** Returns the elements of {@code entries}. */
  public java.util.List<Integer> getEntriesList() {
    return entries_ == null ? java.util.Collections.<Integer>emptyList() : java.util.Collections.unmodifiableList(entries_);
  }

  /** Returns the number of elements of {@code entries}. */
  public int getEntriesCount() {
    return entries_ == null ? 0 : entries_.size();
  }

  /** Returns the element {@code index} of {@code entries}. */
  public int getEntries(int index) {
    if (entries_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return entries_.get(index);
  }

  void copyFieldsFrom(DenseMatrixProto src) {
    numRows_ = src.numRows_;
    numCols_ = src.numCols_;
    entries_ = src.entries_ == null || src.entries_.isEmpty() ? null : new java.util.ArrayList<>(src.entries_);
  }

  /** Returns this message if it is immutable, otherwise a built copy. */
  DenseMatrixProto immutableCopy() {
    if (this instanceof Builder) {
      DenseMatrixProto r = new DenseMatrixProto();
      r.copyFieldsFrom(this);
      return r;
    }
    return this;
  }

  @Override
  public void printTo(ProtoPrinter printer) {
    if (numRows_ != 0) {
      printer.printScalar("num_rows", numRows_);
    }
    if (numCols_ != 0) {
      printer.printScalar("num_cols", numCols_);
    }
    if (entries_ != null) {
      for (Integer v : entries_) {
        printer.printScalar("entries", v);
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
    if (!(obj instanceof DenseMatrixProto)) {
      return false;
    }
    DenseMatrixProto other = (DenseMatrixProto) obj;
    return numRows_ == other.numRows_
        && numCols_ == other.numCols_
        && getEntriesList().equals(other.getEntriesList());
  }

  @Override
  public int hashCode() {
    int h = 578;
    h = 31 * h + Integer.hashCode(numRows_);
    h = 31 * h + Integer.hashCode(numCols_);
    h = 31 * h + getEntriesList().hashCode();
    return h;
  }

  /** Appends the fields of this message in the protobuf binary format. */
  void writeTo(ProtoWire.Writer w) {
    if (numRows_ != 0) {
      w.int32(1, numRows_);
    }
    if (numCols_ != 0) {
      w.int32(2, numCols_);
    }
    if (entries_ != null && !entries_.isEmpty()) {
      w.packedInt32(3, entries_);
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
  public static DenseMatrixProto parseFrom(byte[] data) {
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
  public static Builder newBuilder(DenseMatrixProto prototype) {
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
   * Builder of {@link DenseMatrixProto}. A builder is also a (mutable) {@code DenseMatrixProto}, so all getters are
   * available on it.
   */
  public static final class Builder extends DenseMatrixProto implements Cloneable {
    Builder() {}

    /** Returns an immutable copy of the current content. */
    public DenseMatrixProto build() {
      DenseMatrixProto r = new DenseMatrixProto();
      r.copyFieldsFrom(this);
      return r;
    }

    /** Same as {@link #build()}. */
    public DenseMatrixProto buildPartial() {
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
    public Builder mergeFrom(DenseMatrixProto other) {
      if (other == DEFAULT_INSTANCE) {
        return this;
      }
      if (other.numRows_ != 0) {
        setNumRows(other.numRows_);
      }
      if (other.numCols_ != 0) {
        setNumCols(other.numCols_);
      }
      if (other.entries_ != null) {
        addAllEntries(other.entries_);
      }
      return this;
    }

    /** Replaces the content of this builder by a deep copy of {@code other}. */
    public void copyFrom(DenseMatrixProto other) {
      copyFieldsFrom(other);
    }

    /**
     * Returns a modifiable view of {@code entries}: elements added or set through the list are
     * stored in this builder (messages as immutable copies). The view is detached by
     * {@code clearEntries()} and {@code clear()}.
     */
    @Override
    public java.util.List<Integer> getEntriesList() {
      if (entries_ == null) {
        entries_ = new java.util.ArrayList<>();
      }
      return new ProtoWire.ListView<>(entries_, null);
    }

    void mergeWire(ProtoWire.Reader r) {
      while (!r.atEnd()) {
        int tag = r.tag();
        switch (tag) {
          case 8:
            setNumRows((int) r.varint());
            break;
          case 16:
            setNumCols((int) r.varint());
            break;
          case 26: {
            ProtoWire.Reader p = r.packed();
            while (!p.atEnd()) {
              addEntries((int) p.varint());
            }
            break;
          }
          case 24:
            addEntries((int) r.varint());
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
          case "num_rows": {
            if (strict && numRows_ != 0) {
              throw ProtoWire.duplicate(t, "num_rows");
            }
            t.consume(":");
            setNumRows((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "num_cols": {
            if (strict && numCols_ != 0) {
              throw ProtoWire.duplicate(t, "num_cols");
            }
            t.consume(":");
            setNumCols((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            break;
          }
          case "entries": {
            t.consume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addEntries((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
                } while (t.nextInList());
              }
            } else {
              addEntries((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            }
            break;
          }
          default:
            throw ProtoWire.unknownField(t, "operations_research.sat.DenseMatrixProto", name);
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

    /** Sets {@code num_rows}. */
    public Builder setNumRows(int value) {
      numRows_ = value;
      return this;
    }

    /** Resets {@code num_rows} to its default value. */
    public Builder clearNumRows() {
      numRows_ = 0;
      return this;
    }

    /** Sets {@code num_cols}. */
    public Builder setNumCols(int value) {
      numCols_ = value;
      return this;
    }

    /** Resets {@code num_cols} to its default value. */
    public Builder clearNumCols() {
      numCols_ = 0;
      return this;
    }

    /** Replaces the element {@code index} of {@code entries}. */
    public Builder setEntries(int index, int value) {
      if (entries_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      entries_.set(index, value);
      return this;
    }

    /** Appends a value to {@code entries}. */
    public Builder addEntries(int value) {
      if (entries_ == null) {
        entries_ = new java.util.ArrayList<>();
      }
      entries_.add(value);
      return this;
    }

    /** Appends all values to {@code entries}. */
    public Builder addAllEntries(Iterable<? extends Integer> values) {
      if (entries_ == null) {
        entries_ = new java.util.ArrayList<>();
      }
      for (Integer v : values) {
        entries_.add(v);
      }
      return this;
    }

    /** Removes all values of {@code entries}. */
    public Builder clearEntries() {
      entries_ = null;
      return this;
    }

  }
}
