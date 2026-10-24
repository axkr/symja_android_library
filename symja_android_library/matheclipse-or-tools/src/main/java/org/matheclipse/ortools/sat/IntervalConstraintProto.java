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
 * This is not really a constraint. It is there so it can be referred by other
 * constraints using this "interval" concept.
 *
 * IMPORTANT: For now, this constraint does not enforce any relations on the
 * components, and it is up to the client to add in the model:
 * - enforcement =&gt; start + size == end.
 * - enforcement =&gt; size &gt;= 0  // Only needed if size is not already &gt;= 0.
 * </pre>
 *
 * <p>
 * Plain Java replacement of the protobuf message {@code operations_research.sat.IntervalConstraintProto} of OR-Tools (generated from ortools/sat/cp_model.proto, no protobuf runtime needed). Instances are immutable; use {@link Builder} to create or modify them.
 */
public class IntervalConstraintProto implements ProtoMessage {
  private static final IntervalConstraintProto DEFAULT_INSTANCE = new IntervalConstraintProto();

  LinearExpressionProto start_;
  LinearExpressionProto end_;
  LinearExpressionProto size_;

  private IntervalConstraintProto() {}

  /** Returns the immutable default instance (all fields unset). */
  public static IntervalConstraintProto getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  /** Returns the default instance. */
  public IntervalConstraintProto getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

  /** Returns true if {@code start} is set. */
  public boolean hasStart() {
    return start_ != null;
  }

  /** Returns {@code start}, or the default instance if it is not set. */
  public LinearExpressionProto getStart() {
    return start_ == null ? LinearExpressionProto.getDefaultInstance() : start_;
  }

  /** Returns true if {@code end} is set. */
  public boolean hasEnd() {
    return end_ != null;
  }

  /** Returns {@code end}, or the default instance if it is not set. */
  public LinearExpressionProto getEnd() {
    return end_ == null ? LinearExpressionProto.getDefaultInstance() : end_;
  }

  /** Returns true if {@code size} is set. */
  public boolean hasSize() {
    return size_ != null;
  }

  /** Returns {@code size}, or the default instance if it is not set. */
  public LinearExpressionProto getSize() {
    return size_ == null ? LinearExpressionProto.getDefaultInstance() : size_;
  }

  void copyFieldsFrom(IntervalConstraintProto src) {
    start_ = src.start_ == null ? null : src.start_.immutableCopy();
    end_ = src.end_ == null ? null : src.end_.immutableCopy();
    size_ = src.size_ == null ? null : src.size_.immutableCopy();
  }

  /** Returns this message if it is immutable, otherwise a built copy. */
  IntervalConstraintProto immutableCopy() {
    if (this instanceof Builder) {
      IntervalConstraintProto r = new IntervalConstraintProto();
      r.copyFieldsFrom(this);
      return r;
    }
    return this;
  }

  @Override
  public void printTo(ProtoPrinter printer) {
    if (start_ != null) {
      printer.printMessage("start", start_);
    }
    if (end_ != null) {
      printer.printMessage("end", end_);
    }
    if (size_ != null) {
      printer.printMessage("size", size_);
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
    if (!(obj instanceof IntervalConstraintProto)) {
      return false;
    }
    IntervalConstraintProto other = (IntervalConstraintProto) obj;
    return java.util.Objects.equals(start_, other.start_)
        && java.util.Objects.equals(end_, other.end_)
        && java.util.Objects.equals(size_, other.size_);
  }

  @Override
  public int hashCode() {
    int h = 354;
    h = 31 * h + java.util.Objects.hashCode(start_);
    h = 31 * h + java.util.Objects.hashCode(end_);
    h = 31 * h + java.util.Objects.hashCode(size_);
    return h;
  }

  /** Appends the fields of this message in the protobuf binary format. */
  void writeTo(ProtoWire.Writer w) {
    if (start_ != null) {
      ProtoWire.Writer sub = new ProtoWire.Writer();
      start_.writeTo(sub);
      w.bytes(4, sub);
    }
    if (end_ != null) {
      ProtoWire.Writer sub = new ProtoWire.Writer();
      end_.writeTo(sub);
      w.bytes(5, sub);
    }
    if (size_ != null) {
      ProtoWire.Writer sub = new ProtoWire.Writer();
      size_.writeTo(sub);
      w.bytes(6, sub);
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
  public static IntervalConstraintProto parseFrom(byte[] data) {
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
  public static Builder newBuilder(IntervalConstraintProto prototype) {
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
   * Builder of {@link IntervalConstraintProto}. A builder is also a (mutable) {@code IntervalConstraintProto}, so all getters are
   * available on it.
   */
  public static final class Builder extends IntervalConstraintProto implements Cloneable {
    Builder() {}

    /** Returns an immutable copy of the current content. */
    public IntervalConstraintProto build() {
      IntervalConstraintProto r = new IntervalConstraintProto();
      r.copyFieldsFrom(this);
      return r;
    }

    /** Same as {@link #build()}. */
    public IntervalConstraintProto buildPartial() {
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
    public Builder mergeFrom(IntervalConstraintProto other) {
      if (other == DEFAULT_INSTANCE) {
        return this;
      }
      if (other.start_ != null) {
        mergeStart(other.start_);
      }
      if (other.end_ != null) {
        mergeEnd(other.end_);
      }
      if (other.size_ != null) {
        mergeSize(other.size_);
      }
      return this;
    }

    /** Replaces the content of this builder by a deep copy of {@code other}. */
    public void copyFrom(IntervalConstraintProto other) {
      copyFieldsFrom(other);
    }

    void mergeWire(ProtoWire.Reader r) {
      while (!r.atEnd()) {
        int tag = r.tag();
        switch (tag) {
          case 34:
            getStartBuilder().mergeWire(r.message());
            break;
          case 42:
            getEndBuilder().mergeWire(r.message());
            break;
          case 50:
            getSizeBuilder().mergeWire(r.message());
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
          case "start": {
            if (strict && start_ != null) {
              throw ProtoWire.duplicate(t, "start");
            }
            t.tryConsume(":");
            getStartBuilder().mergeText(t, t.openMessage(), strict);
            break;
          }
          case "end": {
            if (strict && end_ != null) {
              throw ProtoWire.duplicate(t, "end");
            }
            t.tryConsume(":");
            getEndBuilder().mergeText(t, t.openMessage(), strict);
            break;
          }
          case "size": {
            if (strict && size_ != null) {
              throw ProtoWire.duplicate(t, "size");
            }
            t.tryConsume(":");
            getSizeBuilder().mergeText(t, t.openMessage(), strict);
            break;
          }
          default:
            throw ProtoWire.unknownField(t, "operations_research.sat.IntervalConstraintProto", name);
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

    /** Sets {@code start}. */
    public Builder setStart(LinearExpressionProto value) {
      if (value == null) {
        throw new NullPointerException();
      }
      start_ = value.immutableCopy();
      return this;
    }

    /** Sets {@code start} to the content of a builder. */
    public Builder setStart(LinearExpressionProto.Builder builderForValue) {
      return setStart(builderForValue.build());
    }

    /** Merges {@code value} into {@code start}. */
    public Builder mergeStart(LinearExpressionProto value) {
      if (start_ != null) {
        getStartBuilder().mergeFrom(value);
      } else {
        setStart(value.immutableCopy());
      }
      return this;
    }

    /** Clears {@code start}. */
    public Builder clearStart() {
      if (true) {
        start_ = null;
      }
      return this;
    }

    /** Returns a builder for {@code start}, setting it if needed; changes to it are reflected in this builder. */
    public LinearExpressionProto.Builder getStartBuilder() {
      if (start_ != null && start_ instanceof LinearExpressionProto.Builder) {
        return (LinearExpressionProto.Builder) start_;
      }
      LinearExpressionProto.Builder b = start_ != null ? LinearExpressionProto.newBuilder(start_) : LinearExpressionProto.newBuilder();
      setStartRaw(b);
      return b;
    }

    private void setStartRaw(LinearExpressionProto.Builder b) {
      start_ = b;
    }

    /** Sets {@code end}. */
    public Builder setEnd(LinearExpressionProto value) {
      if (value == null) {
        throw new NullPointerException();
      }
      end_ = value.immutableCopy();
      return this;
    }

    /** Sets {@code end} to the content of a builder. */
    public Builder setEnd(LinearExpressionProto.Builder builderForValue) {
      return setEnd(builderForValue.build());
    }

    /** Merges {@code value} into {@code end}. */
    public Builder mergeEnd(LinearExpressionProto value) {
      if (end_ != null) {
        getEndBuilder().mergeFrom(value);
      } else {
        setEnd(value.immutableCopy());
      }
      return this;
    }

    /** Clears {@code end}. */
    public Builder clearEnd() {
      if (true) {
        end_ = null;
      }
      return this;
    }

    /** Returns a builder for {@code end}, setting it if needed; changes to it are reflected in this builder. */
    public LinearExpressionProto.Builder getEndBuilder() {
      if (end_ != null && end_ instanceof LinearExpressionProto.Builder) {
        return (LinearExpressionProto.Builder) end_;
      }
      LinearExpressionProto.Builder b = end_ != null ? LinearExpressionProto.newBuilder(end_) : LinearExpressionProto.newBuilder();
      setEndRaw(b);
      return b;
    }

    private void setEndRaw(LinearExpressionProto.Builder b) {
      end_ = b;
    }

    /** Sets {@code size}. */
    public Builder setSize(LinearExpressionProto value) {
      if (value == null) {
        throw new NullPointerException();
      }
      size_ = value.immutableCopy();
      return this;
    }

    /** Sets {@code size} to the content of a builder. */
    public Builder setSize(LinearExpressionProto.Builder builderForValue) {
      return setSize(builderForValue.build());
    }

    /** Merges {@code value} into {@code size}. */
    public Builder mergeSize(LinearExpressionProto value) {
      if (size_ != null) {
        getSizeBuilder().mergeFrom(value);
      } else {
        setSize(value.immutableCopy());
      }
      return this;
    }

    /** Clears {@code size}. */
    public Builder clearSize() {
      if (true) {
        size_ = null;
      }
      return this;
    }

    /** Returns a builder for {@code size}, setting it if needed; changes to it are reflected in this builder. */
    public LinearExpressionProto.Builder getSizeBuilder() {
      if (size_ != null && size_ instanceof LinearExpressionProto.Builder) {
        return (LinearExpressionProto.Builder) size_;
      }
      LinearExpressionProto.Builder b = size_ != null ? LinearExpressionProto.newBuilder(size_) : LinearExpressionProto.newBuilder();
      setSizeRaw(b);
      return b;
    }

    private void setSizeRaw(LinearExpressionProto.Builder b) {
      size_ = b;
    }

  }
}
