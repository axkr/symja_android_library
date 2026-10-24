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
 * Argument of the constraints of the form OP(literals).
 * </pre>
 *
 * <p>
 * Plain Java replacement of the protobuf message {@code operations_research.sat.BoolArgumentProto} of OR-Tools (generated from ortools/sat/cp_model.proto, no protobuf runtime needed). Instances are immutable; use {@link Builder} to create or modify them.
 */
public class BoolArgumentProto implements ProtoMessage {
  private static final BoolArgumentProto DEFAULT_INSTANCE = new BoolArgumentProto();

  java.util.ArrayList<Integer> literals_;

  private BoolArgumentProto() {}

  /** Returns the immutable default instance (all fields unset). */
  public static BoolArgumentProto getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  /** Returns the default instance. */
  public BoolArgumentProto getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

  /** Returns the elements of {@code literals}. */
  public java.util.List<Integer> getLiteralsList() {
    return literals_ == null ? java.util.Collections.<Integer>emptyList() : java.util.Collections.unmodifiableList(literals_);
  }

  /** Returns the number of elements of {@code literals}. */
  public int getLiteralsCount() {
    return literals_ == null ? 0 : literals_.size();
  }

  /** Returns the element {@code index} of {@code literals}. */
  public int getLiterals(int index) {
    if (literals_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return literals_.get(index);
  }

  void copyFieldsFrom(BoolArgumentProto src) {
    literals_ = src.literals_ == null || src.literals_.isEmpty() ? null : new java.util.ArrayList<>(src.literals_);
  }

  /** Returns this message if it is immutable, otherwise a built copy. */
  BoolArgumentProto immutableCopy() {
    if (this instanceof Builder) {
      BoolArgumentProto r = new BoolArgumentProto();
      r.copyFieldsFrom(this);
      return r;
    }
    return this;
  }

  @Override
  public void printTo(ProtoPrinter printer) {
    if (literals_ != null) {
      for (Integer v : literals_) {
        printer.printScalar("literals", v);
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
    if (!(obj instanceof BoolArgumentProto)) {
      return false;
    }
    BoolArgumentProto other = (BoolArgumentProto) obj;
    return getLiteralsList().equals(other.getLiteralsList());
  }

  @Override
  public int hashCode() {
    int h = 939;
    h = 31 * h + getLiteralsList().hashCode();
    return h;
  }

  /** Appends the fields of this message in the protobuf binary format. */
  void writeTo(ProtoWire.Writer w) {
    if (literals_ != null && !literals_.isEmpty()) {
      w.packedInt32(1, literals_);
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
  public static BoolArgumentProto parseFrom(byte[] data) {
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
  public static Builder newBuilder(BoolArgumentProto prototype) {
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
   * Builder of {@link BoolArgumentProto}. A builder is also a (mutable) {@code BoolArgumentProto}, so all getters are
   * available on it.
   */
  public static final class Builder extends BoolArgumentProto implements Cloneable {
    Builder() {}

    /** Returns an immutable copy of the current content. */
    public BoolArgumentProto build() {
      BoolArgumentProto r = new BoolArgumentProto();
      r.copyFieldsFrom(this);
      return r;
    }

    /** Same as {@link #build()}. */
    public BoolArgumentProto buildPartial() {
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
    public Builder mergeFrom(BoolArgumentProto other) {
      if (other == DEFAULT_INSTANCE) {
        return this;
      }
      if (other.literals_ != null) {
        addAllLiterals(other.literals_);
      }
      return this;
    }

    /** Replaces the content of this builder by a deep copy of {@code other}. */
    public void copyFrom(BoolArgumentProto other) {
      copyFieldsFrom(other);
    }

    /**
     * Returns a modifiable view of {@code literals}: elements added or set through the list are
     * stored in this builder (messages as immutable copies). The view is detached by
     * {@code clearLiterals()} and {@code clear()}.
     */
    @Override
    public java.util.List<Integer> getLiteralsList() {
      if (literals_ == null) {
        literals_ = new java.util.ArrayList<>();
      }
      return new ProtoWire.ListView<>(literals_, null);
    }

    void mergeWire(ProtoWire.Reader r) {
      while (!r.atEnd()) {
        int tag = r.tag();
        switch (tag) {
          case 10: {
            ProtoWire.Reader p = r.packed();
            while (!p.atEnd()) {
              addLiterals((int) p.varint());
            }
            break;
          }
          case 8:
            addLiterals((int) r.varint());
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
          case "literals": {
            t.consume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addLiterals((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
                } while (t.nextInList());
              }
            } else {
              addLiterals((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            }
            break;
          }
          default:
            throw ProtoWire.unknownField(t, "operations_research.sat.BoolArgumentProto", name);
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

    /** Replaces the element {@code index} of {@code literals}. */
    public Builder setLiterals(int index, int value) {
      if (literals_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      literals_.set(index, value);
      return this;
    }

    /** Appends a value to {@code literals}. */
    public Builder addLiterals(int value) {
      if (literals_ == null) {
        literals_ = new java.util.ArrayList<>();
      }
      literals_.add(value);
      return this;
    }

    /** Appends all values to {@code literals}. */
    public Builder addAllLiterals(Iterable<? extends Integer> values) {
      if (literals_ == null) {
        literals_ = new java.util.ArrayList<>();
      }
      for (Integer v : values) {
        literals_.add(v);
      }
      return this;
    }

    /** Removes all values of {@code literals}. */
    public Builder clearLiterals() {
      literals_ = null;
      return this;
    }

  }
}
