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
 * Just a message used to store dense solution.
 * This is used by the additional_solutions field.
 * </pre>
 *
 * <p>
 * Plain Java replacement of the protobuf message {@code operations_research.sat.CpSolverSolution} of OR-Tools (generated from ortools/sat/cp_model.proto, no protobuf runtime needed). Instances are immutable; use {@link Builder} to create or modify them.
 */
public class CpSolverSolution implements ProtoMessage {
  private static final CpSolverSolution DEFAULT_INSTANCE = new CpSolverSolution();

  java.util.ArrayList<Long> values_;

  private CpSolverSolution() {}

  /** Returns the immutable default instance (all fields unset). */
  public static CpSolverSolution getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  /** Returns the default instance. */
  public CpSolverSolution getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
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

  void copyFieldsFrom(CpSolverSolution src) {
    values_ = src.values_ == null || src.values_.isEmpty() ? null : new java.util.ArrayList<>(src.values_);
  }

  /** Returns this message if it is immutable, otherwise a built copy. */
  CpSolverSolution immutableCopy() {
    if (this instanceof Builder) {
      CpSolverSolution r = new CpSolverSolution();
      r.copyFieldsFrom(this);
      return r;
    }
    return this;
  }

  @Override
  public void printTo(ProtoPrinter printer) {
    if (values_ != null) {
      for (Long v : values_) {
        printer.printScalar("values", v);
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
    if (!(obj instanceof CpSolverSolution)) {
      return false;
    }
    CpSolverSolution other = (CpSolverSolution) obj;
    return getValuesList().equals(other.getValuesList());
  }

  @Override
  public int hashCode() {
    int h = 599;
    h = 31 * h + getValuesList().hashCode();
    return h;
  }

  /** Appends the fields of this message in the protobuf binary format. */
  void writeTo(ProtoWire.Writer w) {
    if (values_ != null && !values_.isEmpty()) {
      w.packedInt64(1, values_);
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
  public static CpSolverSolution parseFrom(byte[] data) {
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
  public static Builder newBuilder(CpSolverSolution prototype) {
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
   * Builder of {@link CpSolverSolution}. A builder is also a (mutable) {@code CpSolverSolution}, so all getters are
   * available on it.
   */
  public static final class Builder extends CpSolverSolution implements Cloneable {
    Builder() {}

    /** Returns an immutable copy of the current content. */
    public CpSolverSolution build() {
      CpSolverSolution r = new CpSolverSolution();
      r.copyFieldsFrom(this);
      return r;
    }

    /** Same as {@link #build()}. */
    public CpSolverSolution buildPartial() {
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
    public Builder mergeFrom(CpSolverSolution other) {
      if (other == DEFAULT_INSTANCE) {
        return this;
      }
      if (other.values_ != null) {
        addAllValues(other.values_);
      }
      return this;
    }

    /** Replaces the content of this builder by a deep copy of {@code other}. */
    public void copyFrom(CpSolverSolution other) {
      copyFieldsFrom(other);
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

    void mergeWire(ProtoWire.Reader r) {
      while (!r.atEnd()) {
        int tag = r.tag();
        switch (tag) {
          case 10: {
            ProtoWire.Reader p = r.packed();
            while (!p.atEnd()) {
              addValues(p.varint());
            }
            break;
          }
          case 8:
            addValues(r.varint());
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
          default:
            throw ProtoWire.unknownField(t, "operations_research.sat.CpSolverSolution", name);
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

  }
}
