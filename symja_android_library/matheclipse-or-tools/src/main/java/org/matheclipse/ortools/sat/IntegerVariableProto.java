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
 * An integer variable.
 *
 * It will be referred to by an int32 corresponding to its index in a
 * CpModelProto variables field.
 *
 * Depending on the context, a reference to a variable whose domain is in [0, 1]
 * can also be seen as a Boolean that will be true if the variable value is 1
 * and false if it is 0. When used in this context, the field name will always
 * contain the word "literal".
 *
 * Negative reference (advanced usage): to simplify the creation of a model and
 * for efficiency reasons, all the "literal" or "variable" fields can also
 * contain a negative index. A negative index i will refer to the negation of
 * the integer variable at index -i -1 or to NOT the literal at the same index.
 *
 * Ex: A variable index 4 will refer to the integer variable model.variables(4)
 * and an index of -5 will refer to the negation of the same variable. A literal
 * index 4 will refer to the logical fact that model.variable(4) == 1 and a
 * literal index of -5 will refer to the logical fact model.variable(4) == 0.
 * </pre>
 *
 * <p>
 * Plain Java replacement of the protobuf message {@code operations_research.sat.IntegerVariableProto} of OR-Tools (generated from ortools/sat/cp_model.proto, no protobuf runtime needed). Instances are immutable; use {@link Builder} to create or modify them.
 */
public class IntegerVariableProto implements ProtoMessage {
  private static final IntegerVariableProto DEFAULT_INSTANCE = new IntegerVariableProto();

  String name_ = "";
  java.util.ArrayList<Long> domain_;

  private IntegerVariableProto() {}

  /** Returns the immutable default instance (all fields unset). */
  public static IntegerVariableProto getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  /** Returns the default instance. */
  public IntegerVariableProto getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

  /**
   * <pre>
   * For debug/logging only. Can be empty.
   * </pre>
   */
  public String getName() {
    return name_;
  }

  /**
   * <pre>
   * The variable domain given as a sorted list of n disjoint intervals
   * [min, max] and encoded as [min_0, max_0,  ..., min_&#123;n-1&#125;, max_&#123;n-1&#125;].
   *
   * The most common example being just [min, max].
   * If min == max, then this is a constant variable.
   *
   * We have:
   *  - domain_size() is always even.
   *  - min == domain.front();
   *  - max == domain.back();
   *  - for all i &lt; n   :      min_i &lt;= max_i
   *  - for all i &lt; n-1 :  max_i + 1 &lt; min_&#123;i+1&#125;.
   *
   * Note that we check at validation that a variable domain is small enough so
   * that we don't run into integer overflow in our algorithms. Because of that,
   * you cannot just have "unbounded" variable like [0, kint64max] and should
   * try to specify tighter domains.
   * </pre>
   */
  public java.util.List<Long> getDomainList() {
    return domain_ == null ? java.util.Collections.<Long>emptyList() : java.util.Collections.unmodifiableList(domain_);
  }

  /** Returns the number of elements of {@code domain}. */
  public int getDomainCount() {
    return domain_ == null ? 0 : domain_.size();
  }

  /** Returns the element {@code index} of {@code domain}. */
  public long getDomain(int index) {
    if (domain_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return domain_.get(index);
  }

  void copyFieldsFrom(IntegerVariableProto src) {
    name_ = src.name_;
    domain_ = src.domain_ == null || src.domain_.isEmpty() ? null : new java.util.ArrayList<>(src.domain_);
  }

  /** Returns this message if it is immutable, otherwise a built copy. */
  IntegerVariableProto immutableCopy() {
    if (this instanceof Builder) {
      IntegerVariableProto r = new IntegerVariableProto();
      r.copyFieldsFrom(this);
      return r;
    }
    return this;
  }

  @Override
  public void printTo(ProtoPrinter printer) {
    if (!name_.isEmpty()) {
      printer.printScalar("name", name_);
    }
    if (domain_ != null) {
      for (Long v : domain_) {
        printer.printScalar("domain", v);
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
    if (!(obj instanceof IntegerVariableProto)) {
      return false;
    }
    IntegerVariableProto other = (IntegerVariableProto) obj;
    return name_.equals(other.name_)
        && getDomainList().equals(other.getDomainList());
  }

  @Override
  public int hashCode() {
    int h = 460;
    h = 31 * h + name_.hashCode();
    h = 31 * h + getDomainList().hashCode();
    return h;
  }

  /** Appends the fields of this message in the protobuf binary format. */
  void writeTo(ProtoWire.Writer w) {
    if (!name_.isEmpty()) {
      w.string(1, name_);
    }
    if (domain_ != null && !domain_.isEmpty()) {
      w.packedInt64(2, domain_);
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
  public static IntegerVariableProto parseFrom(byte[] data) {
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
  public static Builder newBuilder(IntegerVariableProto prototype) {
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
   * Builder of {@link IntegerVariableProto}. A builder is also a (mutable) {@code IntegerVariableProto}, so all getters are
   * available on it.
   */
  public static final class Builder extends IntegerVariableProto implements Cloneable {
    Builder() {}

    /** Returns an immutable copy of the current content. */
    public IntegerVariableProto build() {
      IntegerVariableProto r = new IntegerVariableProto();
      r.copyFieldsFrom(this);
      return r;
    }

    /** Same as {@link #build()}. */
    public IntegerVariableProto buildPartial() {
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
    public Builder mergeFrom(IntegerVariableProto other) {
      if (other == DEFAULT_INSTANCE) {
        return this;
      }
      if (!other.name_.isEmpty()) {
        setName(other.name_);
      }
      if (other.domain_ != null) {
        addAllDomain(other.domain_);
      }
      return this;
    }

    /** Replaces the content of this builder by a deep copy of {@code other}. */
    public void copyFrom(IntegerVariableProto other) {
      copyFieldsFrom(other);
    }

    /**
     * Returns a modifiable view of {@code domain}: elements added or set through the list are
     * stored in this builder (messages as immutable copies). The view is detached by
     * {@code clearDomain()} and {@code clear()}.
     */
    @Override
    public java.util.List<Long> getDomainList() {
      if (domain_ == null) {
        domain_ = new java.util.ArrayList<>();
      }
      return new ProtoWire.ListView<>(domain_, null);
    }

    void mergeWire(ProtoWire.Reader r) {
      while (!r.atEnd()) {
        int tag = r.tag();
        switch (tag) {
          case 10:
            setName(r.string(true));
            break;
          case 18: {
            ProtoWire.Reader p = r.packed();
            while (!p.atEnd()) {
              addDomain(p.varint());
            }
            break;
          }
          case 16:
            addDomain(r.varint());
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
          case "name": {
            if (strict && !name_.isEmpty()) {
              throw ProtoWire.duplicate(t, "name");
            }
            t.consume(":");
            setName(t.string());
            break;
          }
          case "domain": {
            t.consume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addDomain(t.integer(Long.MIN_VALUE, Long.MAX_VALUE));
                } while (t.nextInList());
              }
            } else {
              addDomain(t.integer(Long.MIN_VALUE, Long.MAX_VALUE));
            }
            break;
          }
          default:
            throw ProtoWire.unknownField(t, "operations_research.sat.IntegerVariableProto", name);
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

    /** Sets {@code name}. */
    public Builder setName(String value) {
      if (value == null) {
        throw new NullPointerException();
      }
      name_ = value;
      return this;
    }

    /** Resets {@code name} to its default value. */
    public Builder clearName() {
      name_ = "";
      return this;
    }

    /** Replaces the element {@code index} of {@code domain}. */
    public Builder setDomain(int index, long value) {
      if (domain_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      domain_.set(index, value);
      return this;
    }

    /** Appends a value to {@code domain}. */
    public Builder addDomain(long value) {
      if (domain_ == null) {
        domain_ = new java.util.ArrayList<>();
      }
      domain_.add(value);
      return this;
    }

    /** Appends all values to {@code domain}. */
    public Builder addAllDomain(Iterable<? extends Long> values) {
      if (domain_ == null) {
        domain_ = new java.util.ArrayList<>();
      }
      for (Long v : values) {
        domain_.add(v);
      }
      return this;
    }

    /** Removes all values of {@code domain}. */
    public Builder clearDomain() {
      domain_ = null;
      return this;
    }

  }
}
