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
 * The sum of the demands of the intervals at each interval point cannot exceed
 * a capacity. Note that intervals are interpreted as [start, end) and as
 * such intervals like [2,3) and [3,4) do not overlap from the point of view of
 * this constraint. Moreover, intervals of size zero are ignored.
 *
 * All demands must not contain any negative value in their domains. This is
 * checked at validation. Even if there are no intervals, this constraint
 * implicitly enforces capacity &gt;= 0. In other words, a negative capacity is
 * considered valid but always infeasible.
 * </pre>
 *
 * <p>
 * Plain Java replacement of the protobuf message {@code operations_research.sat.CumulativeConstraintProto} of OR-Tools (generated from ortools/sat/cp_model.proto, no protobuf runtime needed). Instances are immutable; use {@link Builder} to create or modify them.
 */
public class CumulativeConstraintProto implements ProtoMessage {
  private static final CumulativeConstraintProto DEFAULT_INSTANCE = new CumulativeConstraintProto();

  LinearExpressionProto capacity_;
  java.util.ArrayList<Integer> intervals_;
  java.util.ArrayList<LinearExpressionProto> demands_;

  private CumulativeConstraintProto() {}

  /** Returns the immutable default instance (all fields unset). */
  public static CumulativeConstraintProto getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  /** Returns the default instance. */
  public CumulativeConstraintProto getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

  /** Returns true if {@code capacity} is set. */
  public boolean hasCapacity() {
    return capacity_ != null;
  }

  /** Returns {@code capacity}, or the default instance if it is not set. */
  public LinearExpressionProto getCapacity() {
    return capacity_ == null ? LinearExpressionProto.getDefaultInstance() : capacity_;
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

  /**
   * <pre>
   * Same size as intervals.
   * </pre>
   */
  public java.util.List<LinearExpressionProto> getDemandsList() {
    return demands_ == null ? java.util.Collections.<LinearExpressionProto>emptyList() : java.util.Collections.unmodifiableList(demands_);
  }

  /** Returns the number of elements of {@code demands}. */
  public int getDemandsCount() {
    return demands_ == null ? 0 : demands_.size();
  }

  /** Returns the element {@code index} of {@code demands}. */
  public LinearExpressionProto getDemands(int index) {
    if (demands_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return demands_.get(index);
  }

  void copyFieldsFrom(CumulativeConstraintProto src) {
    capacity_ = src.capacity_ == null ? null : src.capacity_.immutableCopy();
    intervals_ = src.intervals_ == null || src.intervals_.isEmpty() ? null : new java.util.ArrayList<>(src.intervals_);
    demands_ = buildListLinearExpressionProto(src.demands_);
  }

  /** Returns this message if it is immutable, otherwise a built copy. */
  CumulativeConstraintProto immutableCopy() {
    if (this instanceof Builder) {
      CumulativeConstraintProto r = new CumulativeConstraintProto();
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
    if (capacity_ != null) {
      printer.printMessage("capacity", capacity_);
    }
    if (intervals_ != null) {
      for (Integer v : intervals_) {
        printer.printScalar("intervals", v);
      }
    }
    if (demands_ != null) {
      for (LinearExpressionProto v : demands_) {
        printer.printMessage("demands", v);
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
    if (!(obj instanceof CumulativeConstraintProto)) {
      return false;
    }
    CumulativeConstraintProto other = (CumulativeConstraintProto) obj;
    return java.util.Objects.equals(capacity_, other.capacity_)
        && getIntervalsList().equals(other.getIntervalsList())
        && getDemandsList().equals(other.getDemandsList());
  }

  @Override
  public int hashCode() {
    int h = 1033;
    h = 31 * h + java.util.Objects.hashCode(capacity_);
    h = 31 * h + getIntervalsList().hashCode();
    h = 31 * h + getDemandsList().hashCode();
    return h;
  }

  /** Appends the fields of this message in the protobuf binary format. */
  void writeTo(ProtoWire.Writer w) {
    if (capacity_ != null) {
      ProtoWire.Writer sub = new ProtoWire.Writer();
      capacity_.writeTo(sub);
      w.bytes(1, sub);
    }
    if (intervals_ != null && !intervals_.isEmpty()) {
      w.packedInt32(2, intervals_);
    }
    if (demands_ != null) {
      for (LinearExpressionProto v : demands_) {
        ProtoWire.Writer sub = new ProtoWire.Writer();
        v.writeTo(sub);
        w.bytes(3, sub);
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
  public static CumulativeConstraintProto parseFrom(byte[] data) {
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
  public static Builder newBuilder(CumulativeConstraintProto prototype) {
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
   * Builder of {@link CumulativeConstraintProto}. A builder is also a (mutable) {@code CumulativeConstraintProto}, so all getters are
   * available on it.
   */
  public static final class Builder extends CumulativeConstraintProto implements Cloneable {
    Builder() {}

    /** Returns an immutable copy of the current content. */
    public CumulativeConstraintProto build() {
      CumulativeConstraintProto r = new CumulativeConstraintProto();
      r.copyFieldsFrom(this);
      return r;
    }

    /** Same as {@link #build()}. */
    public CumulativeConstraintProto buildPartial() {
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
    public Builder mergeFrom(CumulativeConstraintProto other) {
      if (other == DEFAULT_INSTANCE) {
        return this;
      }
      if (other.capacity_ != null) {
        mergeCapacity(other.capacity_);
      }
      if (other.intervals_ != null) {
        addAllIntervals(other.intervals_);
      }
      if (other.demands_ != null) {
        addAllDemands(other.demands_);
      }
      return this;
    }

    /** Replaces the content of this builder by a deep copy of {@code other}. */
    public void copyFrom(CumulativeConstraintProto other) {
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

    /**
     * Returns a modifiable view of {@code demands}: elements added or set through the list are
     * stored in this builder (messages as immutable copies). The view is detached by
     * {@code clearDemands()} and {@code clear()}.
     */
    @Override
    public java.util.List<LinearExpressionProto> getDemandsList() {
      if (demands_ == null) {
        demands_ = new java.util.ArrayList<>();
      }
      return new ProtoWire.ListView<>(demands_, LinearExpressionProto::immutableCopy);
    }

    void mergeWire(ProtoWire.Reader r) {
      while (!r.atEnd()) {
        int tag = r.tag();
        switch (tag) {
          case 10:
            getCapacityBuilder().mergeWire(r.message());
            break;
          case 18: {
            ProtoWire.Reader p = r.packed();
            while (!p.atEnd()) {
              addIntervals((int) p.varint());
            }
            break;
          }
          case 16:
            addIntervals((int) r.varint());
            break;
          case 26:
            addDemandsBuilder().mergeWire(r.message());
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
          case "capacity": {
            if (strict && capacity_ != null) {
              throw ProtoWire.duplicate(t, "capacity");
            }
            t.tryConsume(":");
            getCapacityBuilder().mergeText(t, t.openMessage(), strict);
            break;
          }
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
          case "demands": {
            t.tryConsume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addDemandsBuilder().mergeText(t, t.openMessage(), strict);
                } while (t.nextInList());
              }
            } else {
              addDemandsBuilder().mergeText(t, t.openMessage(), strict);
            }
            break;
          }
          default:
            throw ProtoWire.unknownField(t, "operations_research.sat.CumulativeConstraintProto", name);
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

    /** Sets {@code capacity}. */
    public Builder setCapacity(LinearExpressionProto value) {
      if (value == null) {
        throw new NullPointerException();
      }
      capacity_ = value.immutableCopy();
      return this;
    }

    /** Sets {@code capacity} to the content of a builder. */
    public Builder setCapacity(LinearExpressionProto.Builder builderForValue) {
      return setCapacity(builderForValue.build());
    }

    /** Merges {@code value} into {@code capacity}. */
    public Builder mergeCapacity(LinearExpressionProto value) {
      if (capacity_ != null) {
        getCapacityBuilder().mergeFrom(value);
      } else {
        setCapacity(value.immutableCopy());
      }
      return this;
    }

    /** Clears {@code capacity}. */
    public Builder clearCapacity() {
      if (true) {
        capacity_ = null;
      }
      return this;
    }

    /** Returns a builder for {@code capacity}, setting it if needed; changes to it are reflected in this builder. */
    public LinearExpressionProto.Builder getCapacityBuilder() {
      if (capacity_ != null && capacity_ instanceof LinearExpressionProto.Builder) {
        return (LinearExpressionProto.Builder) capacity_;
      }
      LinearExpressionProto.Builder b = capacity_ != null ? LinearExpressionProto.newBuilder(capacity_) : LinearExpressionProto.newBuilder();
      setCapacityRaw(b);
      return b;
    }

    private void setCapacityRaw(LinearExpressionProto.Builder b) {
      capacity_ = b;
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

    /** Replaces the element {@code index} of {@code demands}. */
    public Builder setDemands(int index, LinearExpressionProto value) {
      if (demands_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      demands_.set(index, java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Appends a value to {@code demands}. */
    public Builder addDemands(LinearExpressionProto value) {
      if (demands_ == null) {
        demands_ = new java.util.ArrayList<>();
      }
      demands_.add(java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Appends all values to {@code demands}. */
    public Builder addAllDemands(Iterable<? extends LinearExpressionProto> values) {
      if (demands_ == null) {
        demands_ = new java.util.ArrayList<>();
      }
      for (LinearExpressionProto v : values) {
        demands_.add(java.util.Objects.requireNonNull(v).immutableCopy());
      }
      return this;
    }

    /** Removes all values of {@code demands}. */
    public Builder clearDemands() {
      demands_ = null;
      return this;
    }

    /** Replaces the element {@code index} of {@code demands} by the content of a builder. */
    public Builder setDemands(int index, LinearExpressionProto.Builder builderForValue) {
      return setDemands(index, builderForValue.build());
    }

    /** Appends the content of a builder to {@code demands}. */
    public Builder addDemands(LinearExpressionProto.Builder builderForValue) {
      return addDemands(builderForValue.build());
    }

    /** Inserts a value into {@code demands}. */
    public Builder addDemands(int index, LinearExpressionProto value) {
      if (demands_ == null) {
        demands_ = new java.util.ArrayList<>();
      }
      demands_.add(index, java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Removes the element {@code index} of {@code demands}. */
    public Builder removeDemands(int index) {
      if (demands_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      demands_.remove(index);
      return this;
    }

    /** Returns a builder for the element {@code index} of {@code demands}; changes to it are reflected in this builder. */
    public LinearExpressionProto.Builder getDemandsBuilder(int index) {
      if (demands_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      LinearExpressionProto e = demands_.get(index);
      if (e instanceof LinearExpressionProto.Builder) {
        return (LinearExpressionProto.Builder) e;
      }
      LinearExpressionProto.Builder b = LinearExpressionProto.newBuilder(e);
      demands_.set(index, b);
      return b;
    }

    /** Appends a new empty element to {@code demands} and returns its builder. */
    public LinearExpressionProto.Builder addDemandsBuilder() {
      if (demands_ == null) {
        demands_ = new java.util.ArrayList<>();
      }
      LinearExpressionProto.Builder b = LinearExpressionProto.newBuilder();
      demands_.add(b);
      return b;
    }

    /** Returns the builders of all elements of {@code demands}. */
    public java.util.List<LinearExpressionProto.Builder> getDemandsBuilderList() {
      java.util.List<LinearExpressionProto.Builder> r = new java.util.ArrayList<>();
      for (int k = 0; k < getDemandsCount(); k++) {
        r.add(getDemandsBuilder(k));
      }
      return r;
    }

  }
}
