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
 * All the intervals (index of IntervalConstraintProto) must be disjoint. More
 * formally, there must exist a sequence so that for each consecutive interval,
 * we have end_i &lt;= start_&#123;i+1&#125;. In particular, intervals of size zero do matter
 * for this constraint. This is also known as a disjunctive constraint in
 * scheduling.
 * </pre>
 *
 * <p>
 * Plain Java replacement of the protobuf message {@code operations_research.sat.NoOverlapConstraintProto} of OR-Tools (generated from ortools/sat/cp_model.proto, no protobuf runtime needed). Instances are immutable; use {@link Builder} to create or modify them.
 */
public class NoOverlapConstraintProto implements ProtoMessage {
  private static final NoOverlapConstraintProto DEFAULT_INSTANCE = new NoOverlapConstraintProto();

  java.util.ArrayList<Integer> intervals_;

  private NoOverlapConstraintProto() {}

  /** Returns the immutable default instance (all fields unset). */
  public static NoOverlapConstraintProto getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  /** Returns the default instance. */
  public NoOverlapConstraintProto getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

  /** Returns the elements of {@code intervals}. */
  public java.util.List<Integer> getIntervalsList() {
    return intervals_ == null ? java.util.Collections.<Integer>emptyList() : java.util.Collections.unmodifiableList(intervals_);
  }

  /** Returns the number of elements of {@code intervals}. */
  public int getIntervalsCount() {
    return intervals_ == null ? 0 : intervals_.size();
  }

  /** Returns the element {@code index} of {@code intervals}. */
  public int getIntervals(int index) {
    if (intervals_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return intervals_.get(index);
  }

  void copyFieldsFrom(NoOverlapConstraintProto src) {
    intervals_ = src.intervals_ == null || src.intervals_.isEmpty() ? null : new java.util.ArrayList<>(src.intervals_);
  }

  /** Returns this message if it is immutable, otherwise a built copy. */
  NoOverlapConstraintProto immutableCopy() {
    if (this instanceof Builder) {
      NoOverlapConstraintProto r = new NoOverlapConstraintProto();
      r.copyFieldsFrom(this);
      return r;
    }
    return this;
  }

  @Override
  public void printTo(ProtoPrinter printer) {
    if (intervals_ != null) {
      for (Integer v : intervals_) {
        printer.printScalar("intervals", v);
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
    if (!(obj instanceof NoOverlapConstraintProto)) {
      return false;
    }
    NoOverlapConstraintProto other = (NoOverlapConstraintProto) obj;
    return getIntervalsList().equals(other.getIntervalsList());
  }

  @Override
  public int hashCode() {
    int h = 268;
    h = 31 * h + getIntervalsList().hashCode();
    return h;
  }

  /** Appends the fields of this message in the protobuf binary format. */
  void writeTo(ProtoWire.Writer w) {
    if (intervals_ != null && !intervals_.isEmpty()) {
      w.packedInt32(1, intervals_);
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
  public static NoOverlapConstraintProto parseFrom(byte[] data) {
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
  public static Builder newBuilder(NoOverlapConstraintProto prototype) {
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
   * Builder of {@link NoOverlapConstraintProto}. A builder is also a (mutable) {@code NoOverlapConstraintProto}, so all getters are
   * available on it.
   */
  public static final class Builder extends NoOverlapConstraintProto implements Cloneable {
    Builder() {}

    /** Returns an immutable copy of the current content. */
    public NoOverlapConstraintProto build() {
      NoOverlapConstraintProto r = new NoOverlapConstraintProto();
      r.copyFieldsFrom(this);
      return r;
    }

    /** Same as {@link #build()}. */
    public NoOverlapConstraintProto buildPartial() {
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
    public Builder mergeFrom(NoOverlapConstraintProto other) {
      if (other == DEFAULT_INSTANCE) {
        return this;
      }
      if (other.intervals_ != null) {
        addAllIntervals(other.intervals_);
      }
      return this;
    }

    /** Replaces the content of this builder by a deep copy of {@code other}. */
    public void copyFrom(NoOverlapConstraintProto other) {
      copyFieldsFrom(other);
    }

    /**
     * Returns a modifiable view of {@code intervals}: elements added or set through the list are
     * stored in this builder (messages as immutable copies). The view is detached by
     * {@code clearIntervals()} and {@code clear()}.
     */
    @Override
    public java.util.List<Integer> getIntervalsList() {
      if (intervals_ == null) {
        intervals_ = new java.util.ArrayList<>();
      }
      return new ProtoWire.ListView<>(intervals_, null);
    }

    void mergeWire(ProtoWire.Reader r) {
      while (!r.atEnd()) {
        int tag = r.tag();
        switch (tag) {
          case 10: {
            ProtoWire.Reader p = r.packed();
            while (!p.atEnd()) {
              addIntervals((int) p.varint());
            }
            break;
          }
          case 8:
            addIntervals((int) r.varint());
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
          case "intervals": {
            t.consume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addIntervals((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
                } while (t.nextInList());
              }
            } else {
              addIntervals((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            }
            break;
          }
          default:
            throw ProtoWire.unknownField(t, "operations_research.sat.NoOverlapConstraintProto", name);
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

    /** Replaces the element {@code index} of {@code intervals}. */
    public Builder setIntervals(int index, int value) {
      if (intervals_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      intervals_.set(index, value);
      return this;
    }

    /** Appends a value to {@code intervals}. */
    public Builder addIntervals(int value) {
      if (intervals_ == null) {
        intervals_ = new java.util.ArrayList<>();
      }
      intervals_.add(value);
      return this;
    }

    /** Appends all values to {@code intervals}. */
    public Builder addAllIntervals(Iterable<? extends Integer> values) {
      if (intervals_ == null) {
        intervals_ = new java.util.ArrayList<>();
      }
      for (Integer v : values) {
        intervals_.add(v);
      }
      return this;
    }

    /** Removes all values of {@code intervals}. */
    public Builder clearIntervals() {
      intervals_ = null;
      return this;
    }

  }
}
