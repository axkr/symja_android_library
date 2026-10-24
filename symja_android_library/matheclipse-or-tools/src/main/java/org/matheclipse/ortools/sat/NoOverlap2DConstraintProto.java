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
 * The boxes defined by [start_x, end_x) * [start_y, end_y) cannot overlap.
 * Furthermore, one box is optional if at least one of the x or y interval is
 * optional.
 *
 * Note that the case of boxes of size zero is special. The following cases
 * violate the constraint:
 *   - a point box inside a box with a non-zero area
 *   - a line box overlapping a box with a non-zero area
 *   - one vertical line box crossing a horizontal line box.
 * </pre>
 *
 * <p>
 * Plain Java replacement of the protobuf message {@code operations_research.sat.NoOverlap2DConstraintProto} of OR-Tools (generated from ortools/sat/cp_model.proto, no protobuf runtime needed). Instances are immutable; use {@link Builder} to create or modify them.
 */
public class NoOverlap2DConstraintProto implements ProtoMessage {
  private static final NoOverlap2DConstraintProto DEFAULT_INSTANCE = new NoOverlap2DConstraintProto();

  java.util.ArrayList<Integer> xIntervals_;
  java.util.ArrayList<Integer> yIntervals_;

  private NoOverlap2DConstraintProto() {}

  /** Returns the immutable default instance (all fields unset). */
  public static NoOverlap2DConstraintProto getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  /** Returns the default instance. */
  public NoOverlap2DConstraintProto getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

  /** Returns the elements of {@code x_intervals}. */
  public java.util.List<Integer> getXIntervalsList() {
    return xIntervals_ == null ? java.util.Collections.<Integer>emptyList() : java.util.Collections.unmodifiableList(xIntervals_);
  }

  /** Returns the number of elements of {@code x_intervals}. */
  public int getXIntervalsCount() {
    return xIntervals_ == null ? 0 : xIntervals_.size();
  }

  /** Returns the element {@code index} of {@code x_intervals}. */
  public int getXIntervals(int index) {
    if (xIntervals_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return xIntervals_.get(index);
  }

  /**
   * <pre>
   * Same size as x_intervals.
   * </pre>
   */
  public java.util.List<Integer> getYIntervalsList() {
    return yIntervals_ == null ? java.util.Collections.<Integer>emptyList() : java.util.Collections.unmodifiableList(yIntervals_);
  }

  /** Returns the number of elements of {@code y_intervals}. */
  public int getYIntervalsCount() {
    return yIntervals_ == null ? 0 : yIntervals_.size();
  }

  /** Returns the element {@code index} of {@code y_intervals}. */
  public int getYIntervals(int index) {
    if (yIntervals_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return yIntervals_.get(index);
  }

  void copyFieldsFrom(NoOverlap2DConstraintProto src) {
    xIntervals_ = src.xIntervals_ == null || src.xIntervals_.isEmpty() ? null : new java.util.ArrayList<>(src.xIntervals_);
    yIntervals_ = src.yIntervals_ == null || src.yIntervals_.isEmpty() ? null : new java.util.ArrayList<>(src.yIntervals_);
  }

  /** Returns this message if it is immutable, otherwise a built copy. */
  NoOverlap2DConstraintProto immutableCopy() {
    if (this instanceof Builder) {
      NoOverlap2DConstraintProto r = new NoOverlap2DConstraintProto();
      r.copyFieldsFrom(this);
      return r;
    }
    return this;
  }

  @Override
  public void printTo(ProtoPrinter printer) {
    if (xIntervals_ != null) {
      for (Integer v : xIntervals_) {
        printer.printScalar("x_intervals", v);
      }
    }
    if (yIntervals_ != null) {
      for (Integer v : yIntervals_) {
        printer.printScalar("y_intervals", v);
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
    if (!(obj instanceof NoOverlap2DConstraintProto)) {
      return false;
    }
    NoOverlap2DConstraintProto other = (NoOverlap2DConstraintProto) obj;
    return getXIntervalsList().equals(other.getXIntervalsList())
        && getYIntervalsList().equals(other.getYIntervalsList());
  }

  @Override
  public int hashCode() {
    int h = 393;
    h = 31 * h + getXIntervalsList().hashCode();
    h = 31 * h + getYIntervalsList().hashCode();
    return h;
  }

  /** Appends the fields of this message in the protobuf binary format. */
  void writeTo(ProtoWire.Writer w) {
    if (xIntervals_ != null && !xIntervals_.isEmpty()) {
      w.packedInt32(1, xIntervals_);
    }
    if (yIntervals_ != null && !yIntervals_.isEmpty()) {
      w.packedInt32(2, yIntervals_);
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
  public static NoOverlap2DConstraintProto parseFrom(byte[] data) {
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
  public static Builder newBuilder(NoOverlap2DConstraintProto prototype) {
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
   * Builder of {@link NoOverlap2DConstraintProto}. A builder is also a (mutable) {@code NoOverlap2DConstraintProto}, so all getters are
   * available on it.
   */
  public static final class Builder extends NoOverlap2DConstraintProto implements Cloneable {
    Builder() {}

    /** Returns an immutable copy of the current content. */
    public NoOverlap2DConstraintProto build() {
      NoOverlap2DConstraintProto r = new NoOverlap2DConstraintProto();
      r.copyFieldsFrom(this);
      return r;
    }

    /** Same as {@link #build()}. */
    public NoOverlap2DConstraintProto buildPartial() {
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
    public Builder mergeFrom(NoOverlap2DConstraintProto other) {
      if (other == DEFAULT_INSTANCE) {
        return this;
      }
      if (other.xIntervals_ != null) {
        addAllXIntervals(other.xIntervals_);
      }
      if (other.yIntervals_ != null) {
        addAllYIntervals(other.yIntervals_);
      }
      return this;
    }

    /** Replaces the content of this builder by a deep copy of {@code other}. */
    public void copyFrom(NoOverlap2DConstraintProto other) {
      copyFieldsFrom(other);
    }

    /**
     * Returns a modifiable view of {@code x_intervals}: elements added or set through the list are
     * stored in this builder (messages as immutable copies). The view is detached by
     * {@code clearXIntervals()} and {@code clear()}.
     */
    @Override
    public java.util.List<Integer> getXIntervalsList() {
      if (xIntervals_ == null) {
        xIntervals_ = new java.util.ArrayList<>();
      }
      return new ProtoWire.ListView<>(xIntervals_, null);
    }

    /**
     * Returns a modifiable view of {@code y_intervals}: elements added or set through the list are
     * stored in this builder (messages as immutable copies). The view is detached by
     * {@code clearYIntervals()} and {@code clear()}.
     */
    @Override
    public java.util.List<Integer> getYIntervalsList() {
      if (yIntervals_ == null) {
        yIntervals_ = new java.util.ArrayList<>();
      }
      return new ProtoWire.ListView<>(yIntervals_, null);
    }

    void mergeWire(ProtoWire.Reader r) {
      while (!r.atEnd()) {
        int tag = r.tag();
        switch (tag) {
          case 10: {
            ProtoWire.Reader p = r.packed();
            while (!p.atEnd()) {
              addXIntervals((int) p.varint());
            }
            break;
          }
          case 8:
            addXIntervals((int) r.varint());
            break;
          case 18: {
            ProtoWire.Reader p = r.packed();
            while (!p.atEnd()) {
              addYIntervals((int) p.varint());
            }
            break;
          }
          case 16:
            addYIntervals((int) r.varint());
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
          case "x_intervals": {
            t.consume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addXIntervals((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
                } while (t.nextInList());
              }
            } else {
              addXIntervals((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            }
            break;
          }
          case "y_intervals": {
            t.consume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addYIntervals((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
                } while (t.nextInList());
              }
            } else {
              addYIntervals((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            }
            break;
          }
          default:
            throw ProtoWire.unknownField(t, "operations_research.sat.NoOverlap2DConstraintProto", name);
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

    /** Replaces the element {@code index} of {@code x_intervals}. */
    public Builder setXIntervals(int index, int value) {
      if (xIntervals_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      xIntervals_.set(index, value);
      return this;
    }

    /** Appends a value to {@code x_intervals}. */
    public Builder addXIntervals(int value) {
      if (xIntervals_ == null) {
        xIntervals_ = new java.util.ArrayList<>();
      }
      xIntervals_.add(value);
      return this;
    }

    /** Appends all values to {@code x_intervals}. */
    public Builder addAllXIntervals(Iterable<? extends Integer> values) {
      if (xIntervals_ == null) {
        xIntervals_ = new java.util.ArrayList<>();
      }
      for (Integer v : values) {
        xIntervals_.add(v);
      }
      return this;
    }

    /** Removes all values of {@code x_intervals}. */
    public Builder clearXIntervals() {
      xIntervals_ = null;
      return this;
    }

    /** Replaces the element {@code index} of {@code y_intervals}. */
    public Builder setYIntervals(int index, int value) {
      if (yIntervals_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      yIntervals_.set(index, value);
      return this;
    }

    /** Appends a value to {@code y_intervals}. */
    public Builder addYIntervals(int value) {
      if (yIntervals_ == null) {
        yIntervals_ = new java.util.ArrayList<>();
      }
      yIntervals_.add(value);
      return this;
    }

    /** Appends all values to {@code y_intervals}. */
    public Builder addAllYIntervals(Iterable<? extends Integer> values) {
      if (yIntervals_ == null) {
        yIntervals_ = new java.util.ArrayList<>();
      }
      for (Integer v : values) {
        yIntervals_.add(v);
      }
      return this;
    }

    /** Removes all values of {@code y_intervals}. */
    public Builder clearYIntervals() {
      yIntervals_ = null;
      return this;
    }

  }
}
