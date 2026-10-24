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
 * The circuit constraint is defined on a graph where the arc presence is
 * controlled by literals. Each arc is given by an index in the
 * tails/heads/literals lists that must have the same size.
 *
 * For now, we ignore node indices with no incident arc. All the other nodes
 * must have exactly one incoming and one outgoing selected arc (i.e. literal at
 * true). All the selected arcs that are not self-loops must form a single
 * circuit. Note that multi-arcs are allowed, but only one of them will be true
 * at the same time. Multi-self loops are disallowed though.
 * </pre>
 *
 * <p>
 * Plain Java replacement of the protobuf message {@code operations_research.sat.CircuitConstraintProto} of OR-Tools (generated from ortools/sat/cp_model.proto, no protobuf runtime needed). Instances are immutable; use {@link Builder} to create or modify them.
 */
public class CircuitConstraintProto implements ProtoMessage {
  private static final CircuitConstraintProto DEFAULT_INSTANCE = new CircuitConstraintProto();

  java.util.ArrayList<Integer> tails_;
  java.util.ArrayList<Integer> heads_;
  java.util.ArrayList<Integer> literals_;

  private CircuitConstraintProto() {}

  /** Returns the immutable default instance (all fields unset). */
  public static CircuitConstraintProto getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  /** Returns the default instance. */
  public CircuitConstraintProto getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

  /** Returns the elements of {@code tails}. */
  public java.util.List<Integer> getTailsList() {
    return tails_ == null ? java.util.Collections.<Integer>emptyList() : java.util.Collections.unmodifiableList(tails_);
  }

  /** Returns the number of elements of {@code tails}. */
  public int getTailsCount() {
    return tails_ == null ? 0 : tails_.size();
  }

  /** Returns the element {@code index} of {@code tails}. */
  public int getTails(int index) {
    if (tails_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return tails_.get(index);
  }

  /** Returns the elements of {@code heads}. */
  public java.util.List<Integer> getHeadsList() {
    return heads_ == null ? java.util.Collections.<Integer>emptyList() : java.util.Collections.unmodifiableList(heads_);
  }

  /** Returns the number of elements of {@code heads}. */
  public int getHeadsCount() {
    return heads_ == null ? 0 : heads_.size();
  }

  /** Returns the element {@code index} of {@code heads}. */
  public int getHeads(int index) {
    if (heads_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return heads_.get(index);
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

  void copyFieldsFrom(CircuitConstraintProto src) {
    tails_ = src.tails_ == null || src.tails_.isEmpty() ? null : new java.util.ArrayList<>(src.tails_);
    heads_ = src.heads_ == null || src.heads_.isEmpty() ? null : new java.util.ArrayList<>(src.heads_);
    literals_ = src.literals_ == null || src.literals_.isEmpty() ? null : new java.util.ArrayList<>(src.literals_);
  }

  /** Returns this message if it is immutable, otherwise a built copy. */
  CircuitConstraintProto immutableCopy() {
    if (this instanceof Builder) {
      CircuitConstraintProto r = new CircuitConstraintProto();
      r.copyFieldsFrom(this);
      return r;
    }
    return this;
  }

  @Override
  public void printTo(ProtoPrinter printer) {
    if (tails_ != null) {
      for (Integer v : tails_) {
        printer.printScalar("tails", v);
      }
    }
    if (heads_ != null) {
      for (Integer v : heads_) {
        printer.printScalar("heads", v);
      }
    }
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
    if (!(obj instanceof CircuitConstraintProto)) {
      return false;
    }
    CircuitConstraintProto other = (CircuitConstraintProto) obj;
    return getTailsList().equals(other.getTailsList())
        && getHeadsList().equals(other.getHeadsList())
        && getLiteralsList().equals(other.getLiteralsList());
  }

  @Override
  public int hashCode() {
    int h = 459;
    h = 31 * h + getTailsList().hashCode();
    h = 31 * h + getHeadsList().hashCode();
    h = 31 * h + getLiteralsList().hashCode();
    return h;
  }

  /** Appends the fields of this message in the protobuf binary format. */
  void writeTo(ProtoWire.Writer w) {
    if (tails_ != null && !tails_.isEmpty()) {
      w.packedInt32(3, tails_);
    }
    if (heads_ != null && !heads_.isEmpty()) {
      w.packedInt32(4, heads_);
    }
    if (literals_ != null && !literals_.isEmpty()) {
      w.packedInt32(5, literals_);
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
  public static CircuitConstraintProto parseFrom(byte[] data) {
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
  public static Builder newBuilder(CircuitConstraintProto prototype) {
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
   * Builder of {@link CircuitConstraintProto}. A builder is also a (mutable) {@code CircuitConstraintProto}, so all getters are
   * available on it.
   */
  public static final class Builder extends CircuitConstraintProto implements Cloneable {
    Builder() {}

    /** Returns an immutable copy of the current content. */
    public CircuitConstraintProto build() {
      CircuitConstraintProto r = new CircuitConstraintProto();
      r.copyFieldsFrom(this);
      return r;
    }

    /** Same as {@link #build()}. */
    public CircuitConstraintProto buildPartial() {
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
    public Builder mergeFrom(CircuitConstraintProto other) {
      if (other == DEFAULT_INSTANCE) {
        return this;
      }
      if (other.tails_ != null) {
        addAllTails(other.tails_);
      }
      if (other.heads_ != null) {
        addAllHeads(other.heads_);
      }
      if (other.literals_ != null) {
        addAllLiterals(other.literals_);
      }
      return this;
    }

    /** Replaces the content of this builder by a deep copy of {@code other}. */
    public void copyFrom(CircuitConstraintProto other) {
      copyFieldsFrom(other);
    }

    /**
     * Returns a modifiable view of {@code tails}: elements added or set through the list are
     * stored in this builder (messages as immutable copies). The view is detached by
     * {@code clearTails()} and {@code clear()}.
     */
    @Override
    public java.util.List<Integer> getTailsList() {
      if (tails_ == null) {
        tails_ = new java.util.ArrayList<>();
      }
      return new ProtoWire.ListView<>(tails_, null);
    }

    /**
     * Returns a modifiable view of {@code heads}: elements added or set through the list are
     * stored in this builder (messages as immutable copies). The view is detached by
     * {@code clearHeads()} and {@code clear()}.
     */
    @Override
    public java.util.List<Integer> getHeadsList() {
      if (heads_ == null) {
        heads_ = new java.util.ArrayList<>();
      }
      return new ProtoWire.ListView<>(heads_, null);
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
          case 26: {
            ProtoWire.Reader p = r.packed();
            while (!p.atEnd()) {
              addTails((int) p.varint());
            }
            break;
          }
          case 24:
            addTails((int) r.varint());
            break;
          case 34: {
            ProtoWire.Reader p = r.packed();
            while (!p.atEnd()) {
              addHeads((int) p.varint());
            }
            break;
          }
          case 32:
            addHeads((int) r.varint());
            break;
          case 42: {
            ProtoWire.Reader p = r.packed();
            while (!p.atEnd()) {
              addLiterals((int) p.varint());
            }
            break;
          }
          case 40:
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
          case "tails": {
            t.consume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addTails((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
                } while (t.nextInList());
              }
            } else {
              addTails((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            }
            break;
          }
          case "heads": {
            t.consume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addHeads((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
                } while (t.nextInList());
              }
            } else {
              addHeads((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            }
            break;
          }
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
            throw ProtoWire.unknownField(t, "operations_research.sat.CircuitConstraintProto", name);
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

    /** Replaces the element {@code index} of {@code tails}. */
    public Builder setTails(int index, int value) {
      if (tails_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      tails_.set(index, value);
      return this;
    }

    /** Appends a value to {@code tails}. */
    public Builder addTails(int value) {
      if (tails_ == null) {
        tails_ = new java.util.ArrayList<>();
      }
      tails_.add(value);
      return this;
    }

    /** Appends all values to {@code tails}. */
    public Builder addAllTails(Iterable<? extends Integer> values) {
      if (tails_ == null) {
        tails_ = new java.util.ArrayList<>();
      }
      for (Integer v : values) {
        tails_.add(v);
      }
      return this;
    }

    /** Removes all values of {@code tails}. */
    public Builder clearTails() {
      tails_ = null;
      return this;
    }

    /** Replaces the element {@code index} of {@code heads}. */
    public Builder setHeads(int index, int value) {
      if (heads_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      heads_.set(index, value);
      return this;
    }

    /** Appends a value to {@code heads}. */
    public Builder addHeads(int value) {
      if (heads_ == null) {
        heads_ = new java.util.ArrayList<>();
      }
      heads_.add(value);
      return this;
    }

    /** Appends all values to {@code heads}. */
    public Builder addAllHeads(Iterable<? extends Integer> values) {
      if (heads_ == null) {
        heads_ = new java.util.ArrayList<>();
      }
      for (Integer v : values) {
        heads_.add(v);
      }
      return this;
    }

    /** Removes all values of {@code heads}. */
    public Builder clearHeads() {
      heads_ = null;
      return this;
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
