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
 * The "VRP" (Vehicle Routing Problem) constraint.
 *
 * The direct graph where arc #i (from tails[i] to head[i]) is present iff
 * literals[i] is true must satisfy this set of properties:
 * - #incoming arcs == 1 except for node 0.
 * - #outgoing arcs == 1 except for node 0.
 * - for node zero, #incoming arcs == #outgoing arcs.
 * - There are no duplicate arcs.
 * - Self-arcs are allowed except for node 0.
 * - There is no cycle in this graph, except through node 0.
 *
 * Note: Currently this constraint expects all the nodes in [0, num_nodes) to
 * have at least one incident arc. The model will be considered invalid if it
 * is not the case. You can add self-arc fixed to one to ignore some nodes if
 * needed.
 *
 * TODO(user): It is probably possible to generalize this constraint to a
 * no-cycle in a general graph, or a no-cycle with sum incoming &lt;= 1 and sum
 * outgoing &lt;= 1 (more efficient implementation). On the other hand, having this
 * specific constraint allows us to add specific "cuts" to a VRP problem.
 * </pre>
 *
 * <p>
 * Plain Java replacement of the protobuf message {@code operations_research.sat.RoutesConstraintProto} of OR-Tools (generated from ortools/sat/cp_model.proto, no protobuf runtime needed). Instances are immutable; use {@link Builder} to create or modify them.
 */
public class RoutesConstraintProto implements ProtoMessage {
  private static final RoutesConstraintProto DEFAULT_INSTANCE = new RoutesConstraintProto();

  /**
   * <pre>
   * A set of linear expressions associated with the nodes.
   * </pre>
   *
   * <p>
   * Plain Java replacement of the protobuf message {@code operations_research.sat.RoutesConstraintProto.NodeExpressions} of OR-Tools (generated from ortools/sat/cp_model.proto, no protobuf runtime needed). Instances are immutable; use {@link Builder} to create or modify them.
   */
  public static class NodeExpressions implements ProtoMessage {
    private static final NodeExpressions DEFAULT_INSTANCE = new NodeExpressions();

    java.util.ArrayList<LinearExpressionProto> exprs_;

    private NodeExpressions() {}

    /** Returns the immutable default instance (all fields unset). */
    public static NodeExpressions getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    /** Returns the default instance. */
    public NodeExpressions getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

    /**
     * <pre>
     * The i-th element is the linear expression associated with the i-th node.
     * </pre>
     */
    public java.util.List<LinearExpressionProto> getExprsList() {
      return exprs_ == null ? java.util.Collections.<LinearExpressionProto>emptyList() : java.util.Collections.unmodifiableList(exprs_);
    }

    /** Returns the number of elements of {@code exprs}. */
    public int getExprsCount() {
      return exprs_ == null ? 0 : exprs_.size();
    }

    /** Returns the element {@code index} of {@code exprs}. */
    public LinearExpressionProto getExprs(int index) {
      if (exprs_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      return exprs_.get(index);
    }

    void copyFieldsFrom(NodeExpressions src) {
      exprs_ = buildListLinearExpressionProto(src.exprs_);
    }

    /** Returns this message if it is immutable, otherwise a built copy. */
    NodeExpressions immutableCopy() {
      if (this instanceof Builder) {
        NodeExpressions r = new NodeExpressions();
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
      if (exprs_ != null) {
        for (LinearExpressionProto v : exprs_) {
          printer.printMessage("exprs", v);
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
      if (!(obj instanceof NodeExpressions)) {
        return false;
      }
      NodeExpressions other = (NodeExpressions) obj;
      return getExprsList().equals(other.getExprsList());
    }

    @Override
    public int hashCode() {
      int h = 385;
      h = 31 * h + getExprsList().hashCode();
      return h;
    }

    /** Appends the fields of this message in the protobuf binary format. */
    void writeTo(ProtoWire.Writer w) {
      if (exprs_ != null) {
        for (LinearExpressionProto v : exprs_) {
          ProtoWire.Writer sub = new ProtoWire.Writer();
          v.writeTo(sub);
          w.bytes(1, sub);
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
    public static NodeExpressions parseFrom(byte[] data) {
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
    public static Builder newBuilder(NodeExpressions prototype) {
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
     * Builder of {@link NodeExpressions}. A builder is also a (mutable) {@code NodeExpressions}, so all getters are
     * available on it.
     */
    public static final class Builder extends NodeExpressions implements Cloneable {
      Builder() {}

      /** Returns an immutable copy of the current content. */
      public NodeExpressions build() {
        NodeExpressions r = new NodeExpressions();
        r.copyFieldsFrom(this);
        return r;
      }

      /** Same as {@link #build()}. */
      public NodeExpressions buildPartial() {
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
      public Builder mergeFrom(NodeExpressions other) {
        if (other == DEFAULT_INSTANCE) {
          return this;
        }
        if (other.exprs_ != null) {
          addAllExprs(other.exprs_);
        }
        return this;
      }

      /** Replaces the content of this builder by a deep copy of {@code other}. */
      public void copyFrom(NodeExpressions other) {
        copyFieldsFrom(other);
      }

      /**
       * Returns a modifiable view of {@code exprs}: elements added or set through the list are
       * stored in this builder (messages as immutable copies). The view is detached by
       * {@code clearExprs()} and {@code clear()}.
       */
      @Override
      public java.util.List<LinearExpressionProto> getExprsList() {
        if (exprs_ == null) {
          exprs_ = new java.util.ArrayList<>();
        }
        return new ProtoWire.ListView<>(exprs_, LinearExpressionProto::immutableCopy);
      }

      void mergeWire(ProtoWire.Reader r) {
        while (!r.atEnd()) {
          int tag = r.tag();
          switch (tag) {
            case 10:
              addExprsBuilder().mergeWire(r.message());
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
            case "exprs": {
              t.tryConsume(":");
              if (t.startList()) {
                if (!t.emptyList()) {
                  do {
                    addExprsBuilder().mergeText(t, t.openMessage(), strict);
                  } while (t.nextInList());
                }
              } else {
                addExprsBuilder().mergeText(t, t.openMessage(), strict);
              }
              break;
            }
            default:
              throw ProtoWire.unknownField(t, "operations_research.sat.RoutesConstraintProto.NodeExpressions", name);
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

      /** Replaces the element {@code index} of {@code exprs}. */
      public Builder setExprs(int index, LinearExpressionProto value) {
        if (exprs_ == null) {
          throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
        }
        exprs_.set(index, java.util.Objects.requireNonNull(value).immutableCopy());
        return this;
      }

      /** Appends a value to {@code exprs}. */
      public Builder addExprs(LinearExpressionProto value) {
        if (exprs_ == null) {
          exprs_ = new java.util.ArrayList<>();
        }
        exprs_.add(java.util.Objects.requireNonNull(value).immutableCopy());
        return this;
      }

      /** Appends all values to {@code exprs}. */
      public Builder addAllExprs(Iterable<? extends LinearExpressionProto> values) {
        if (exprs_ == null) {
          exprs_ = new java.util.ArrayList<>();
        }
        for (LinearExpressionProto v : values) {
          exprs_.add(java.util.Objects.requireNonNull(v).immutableCopy());
        }
        return this;
      }

      /** Removes all values of {@code exprs}. */
      public Builder clearExprs() {
        exprs_ = null;
        return this;
      }

      /** Replaces the element {@code index} of {@code exprs} by the content of a builder. */
      public Builder setExprs(int index, LinearExpressionProto.Builder builderForValue) {
        return setExprs(index, builderForValue.build());
      }

      /** Appends the content of a builder to {@code exprs}. */
      public Builder addExprs(LinearExpressionProto.Builder builderForValue) {
        return addExprs(builderForValue.build());
      }

      /** Inserts a value into {@code exprs}. */
      public Builder addExprs(int index, LinearExpressionProto value) {
        if (exprs_ == null) {
          exprs_ = new java.util.ArrayList<>();
        }
        exprs_.add(index, java.util.Objects.requireNonNull(value).immutableCopy());
        return this;
      }

      /** Removes the element {@code index} of {@code exprs}. */
      public Builder removeExprs(int index) {
        if (exprs_ == null) {
          throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
        }
        exprs_.remove(index);
        return this;
      }

      /** Returns a builder for the element {@code index} of {@code exprs}; changes to it are reflected in this builder. */
      public LinearExpressionProto.Builder getExprsBuilder(int index) {
        if (exprs_ == null) {
          throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
        }
        LinearExpressionProto e = exprs_.get(index);
        if (e instanceof LinearExpressionProto.Builder) {
          return (LinearExpressionProto.Builder) e;
        }
        LinearExpressionProto.Builder b = LinearExpressionProto.newBuilder(e);
        exprs_.set(index, b);
        return b;
      }

      /** Appends a new empty element to {@code exprs} and returns its builder. */
      public LinearExpressionProto.Builder addExprsBuilder() {
        if (exprs_ == null) {
          exprs_ = new java.util.ArrayList<>();
        }
        LinearExpressionProto.Builder b = LinearExpressionProto.newBuilder();
        exprs_.add(b);
        return b;
      }

      /** Returns the builders of all elements of {@code exprs}. */
      public java.util.List<LinearExpressionProto.Builder> getExprsBuilderList() {
        java.util.List<LinearExpressionProto.Builder> r = new java.util.ArrayList<>();
        for (int k = 0; k < getExprsCount(); k++) {
          r.add(getExprsBuilder(k));
        }
        return r;
      }

    }
  }

  java.util.ArrayList<Integer> tails_;
  java.util.ArrayList<Integer> heads_;
  java.util.ArrayList<Integer> literals_;
  java.util.ArrayList<Integer> demands_;
  long capacity_ = 0L;
  java.util.ArrayList<RoutesConstraintProto.NodeExpressions> dimensions_;

  private RoutesConstraintProto() {}

  /** Returns the immutable default instance (all fields unset). */
  public static RoutesConstraintProto getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  /** Returns the default instance. */
  public RoutesConstraintProto getDefaultInstanceForType() {
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

  /**
   * <pre>
   * DEPRECATED. These fields are no longer used. The solver ignores them.
   * </pre>
   */
  public java.util.List<Integer> getDemandsList() {
    return demands_ == null ? java.util.Collections.<Integer>emptyList() : java.util.Collections.unmodifiableList(demands_);
  }

  /** Returns the number of elements of {@code demands}. */
  public int getDemandsCount() {
    return demands_ == null ? 0 : demands_.size();
  }

  /** Returns the element {@code index} of {@code demands}. */
  public int getDemands(int index) {
    if (demands_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return demands_.get(index);
  }

  /** Returns {@code capacity}. */
  public long getCapacity() {
    return capacity_;
  }

  /**
   * <pre>
   * Expressions associated with the nodes of the graph, such as the load of the
   * vehicle arriving at a node, or the time at which a vehicle arrives at a
   * node. Expressions with the same "dimension" (such as "load" or "time") must
   * be listed together.
   * This field is optional. If it is set, the linear constraints of size 1 or 2
   * between the variables in these expressions will be used to derive cuts for
   * this constraint. If it is not set, the solver will try to automatically
   * derive it, from the linear constraints of size 1 or 2 in the model (this
   * can fail in complex cases).
   * </pre>
   */
  public java.util.List<RoutesConstraintProto.NodeExpressions> getDimensionsList() {
    return dimensions_ == null ? java.util.Collections.<RoutesConstraintProto.NodeExpressions>emptyList() : java.util.Collections.unmodifiableList(dimensions_);
  }

  /** Returns the number of elements of {@code dimensions}. */
  public int getDimensionsCount() {
    return dimensions_ == null ? 0 : dimensions_.size();
  }

  /** Returns the element {@code index} of {@code dimensions}. */
  public RoutesConstraintProto.NodeExpressions getDimensions(int index) {
    if (dimensions_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return dimensions_.get(index);
  }

  void copyFieldsFrom(RoutesConstraintProto src) {
    tails_ = src.tails_ == null || src.tails_.isEmpty() ? null : new java.util.ArrayList<>(src.tails_);
    heads_ = src.heads_ == null || src.heads_.isEmpty() ? null : new java.util.ArrayList<>(src.heads_);
    literals_ = src.literals_ == null || src.literals_.isEmpty() ? null : new java.util.ArrayList<>(src.literals_);
    demands_ = src.demands_ == null || src.demands_.isEmpty() ? null : new java.util.ArrayList<>(src.demands_);
    capacity_ = src.capacity_;
    dimensions_ = buildListRoutesConstraintProtoNodeExpressions(src.dimensions_);
  }

  /** Returns this message if it is immutable, otherwise a built copy. */
  RoutesConstraintProto immutableCopy() {
    if (this instanceof Builder) {
      RoutesConstraintProto r = new RoutesConstraintProto();
      r.copyFieldsFrom(this);
      return r;
    }
    return this;
  }

  private static java.util.ArrayList<RoutesConstraintProto.NodeExpressions> buildListRoutesConstraintProtoNodeExpressions(java.util.List<RoutesConstraintProto.NodeExpressions> src) {
    if (src == null || src.isEmpty()) {
      return null;
    }
    java.util.ArrayList<RoutesConstraintProto.NodeExpressions> r = new java.util.ArrayList<>(src.size());
    for (RoutesConstraintProto.NodeExpressions e : src) {
      r.add(e.immutableCopy());
    }
    return r;
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
    if (demands_ != null) {
      for (Integer v : demands_) {
        printer.printScalar("demands", v);
      }
    }
    if (capacity_ != 0) {
      printer.printScalar("capacity", capacity_);
    }
    if (dimensions_ != null) {
      for (RoutesConstraintProto.NodeExpressions v : dimensions_) {
        printer.printMessage("dimensions", v);
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
    if (!(obj instanceof RoutesConstraintProto)) {
      return false;
    }
    RoutesConstraintProto other = (RoutesConstraintProto) obj;
    return getTailsList().equals(other.getTailsList())
        && getHeadsList().equals(other.getHeadsList())
        && getLiteralsList().equals(other.getLiteralsList())
        && getDemandsList().equals(other.getDemandsList())
        && capacity_ == other.capacity_
        && getDimensionsList().equals(other.getDimensionsList());
  }

  @Override
  public int hashCode() {
    int h = 612;
    h = 31 * h + getTailsList().hashCode();
    h = 31 * h + getHeadsList().hashCode();
    h = 31 * h + getLiteralsList().hashCode();
    h = 31 * h + getDemandsList().hashCode();
    h = 31 * h + Long.hashCode(capacity_);
    h = 31 * h + getDimensionsList().hashCode();
    return h;
  }

  /** Appends the fields of this message in the protobuf binary format. */
  void writeTo(ProtoWire.Writer w) {
    if (tails_ != null && !tails_.isEmpty()) {
      w.packedInt32(1, tails_);
    }
    if (heads_ != null && !heads_.isEmpty()) {
      w.packedInt32(2, heads_);
    }
    if (literals_ != null && !literals_.isEmpty()) {
      w.packedInt32(3, literals_);
    }
    if (demands_ != null && !demands_.isEmpty()) {
      w.packedInt32(4, demands_);
    }
    if (capacity_ != 0) {
      w.int64(5, capacity_);
    }
    if (dimensions_ != null) {
      for (RoutesConstraintProto.NodeExpressions v : dimensions_) {
        ProtoWire.Writer sub = new ProtoWire.Writer();
        v.writeTo(sub);
        w.bytes(6, sub);
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
  public static RoutesConstraintProto parseFrom(byte[] data) {
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
  public static Builder newBuilder(RoutesConstraintProto prototype) {
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
   * Builder of {@link RoutesConstraintProto}. A builder is also a (mutable) {@code RoutesConstraintProto}, so all getters are
   * available on it.
   */
  public static final class Builder extends RoutesConstraintProto implements Cloneable {
    Builder() {}

    /** Returns an immutable copy of the current content. */
    public RoutesConstraintProto build() {
      RoutesConstraintProto r = new RoutesConstraintProto();
      r.copyFieldsFrom(this);
      return r;
    }

    /** Same as {@link #build()}. */
    public RoutesConstraintProto buildPartial() {
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
    public Builder mergeFrom(RoutesConstraintProto other) {
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
      if (other.demands_ != null) {
        addAllDemands(other.demands_);
      }
      if (other.capacity_ != 0) {
        setCapacity(other.capacity_);
      }
      if (other.dimensions_ != null) {
        addAllDimensions(other.dimensions_);
      }
      return this;
    }

    /** Replaces the content of this builder by a deep copy of {@code other}. */
    public void copyFrom(RoutesConstraintProto other) {
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

    /**
     * Returns a modifiable view of {@code demands}: elements added or set through the list are
     * stored in this builder (messages as immutable copies). The view is detached by
     * {@code clearDemands()} and {@code clear()}.
     */
    @Override
    public java.util.List<Integer> getDemandsList() {
      if (demands_ == null) {
        demands_ = new java.util.ArrayList<>();
      }
      return new ProtoWire.ListView<>(demands_, null);
    }

    /**
     * Returns a modifiable view of {@code dimensions}: elements added or set through the list are
     * stored in this builder (messages as immutable copies). The view is detached by
     * {@code clearDimensions()} and {@code clear()}.
     */
    @Override
    public java.util.List<RoutesConstraintProto.NodeExpressions> getDimensionsList() {
      if (dimensions_ == null) {
        dimensions_ = new java.util.ArrayList<>();
      }
      return new ProtoWire.ListView<>(dimensions_, RoutesConstraintProto.NodeExpressions::immutableCopy);
    }

    void mergeWire(ProtoWire.Reader r) {
      while (!r.atEnd()) {
        int tag = r.tag();
        switch (tag) {
          case 10: {
            ProtoWire.Reader p = r.packed();
            while (!p.atEnd()) {
              addTails((int) p.varint());
            }
            break;
          }
          case 8:
            addTails((int) r.varint());
            break;
          case 18: {
            ProtoWire.Reader p = r.packed();
            while (!p.atEnd()) {
              addHeads((int) p.varint());
            }
            break;
          }
          case 16:
            addHeads((int) r.varint());
            break;
          case 26: {
            ProtoWire.Reader p = r.packed();
            while (!p.atEnd()) {
              addLiterals((int) p.varint());
            }
            break;
          }
          case 24:
            addLiterals((int) r.varint());
            break;
          case 34: {
            ProtoWire.Reader p = r.packed();
            while (!p.atEnd()) {
              addDemands((int) p.varint());
            }
            break;
          }
          case 32:
            addDemands((int) r.varint());
            break;
          case 40:
            setCapacity(r.varint());
            break;
          case 50:
            addDimensionsBuilder().mergeWire(r.message());
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
          case "demands": {
            t.consume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addDemands((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
                } while (t.nextInList());
              }
            } else {
              addDemands((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            }
            break;
          }
          case "capacity": {
            if (strict && capacity_ != 0) {
              throw ProtoWire.duplicate(t, "capacity");
            }
            t.consume(":");
            setCapacity(t.integer(Long.MIN_VALUE, Long.MAX_VALUE));
            break;
          }
          case "dimensions": {
            t.tryConsume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addDimensionsBuilder().mergeText(t, t.openMessage(), strict);
                } while (t.nextInList());
              }
            } else {
              addDimensionsBuilder().mergeText(t, t.openMessage(), strict);
            }
            break;
          }
          default:
            throw ProtoWire.unknownField(t, "operations_research.sat.RoutesConstraintProto", name);
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

    /** Replaces the element {@code index} of {@code demands}. */
    public Builder setDemands(int index, int value) {
      if (demands_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      demands_.set(index, value);
      return this;
    }

    /** Appends a value to {@code demands}. */
    public Builder addDemands(int value) {
      if (demands_ == null) {
        demands_ = new java.util.ArrayList<>();
      }
      demands_.add(value);
      return this;
    }

    /** Appends all values to {@code demands}. */
    public Builder addAllDemands(Iterable<? extends Integer> values) {
      if (demands_ == null) {
        demands_ = new java.util.ArrayList<>();
      }
      for (Integer v : values) {
        demands_.add(v);
      }
      return this;
    }

    /** Removes all values of {@code demands}. */
    public Builder clearDemands() {
      demands_ = null;
      return this;
    }

    /** Sets {@code capacity}. */
    public Builder setCapacity(long value) {
      capacity_ = value;
      return this;
    }

    /** Resets {@code capacity} to its default value. */
    public Builder clearCapacity() {
      capacity_ = 0L;
      return this;
    }

    /** Replaces the element {@code index} of {@code dimensions}. */
    public Builder setDimensions(int index, RoutesConstraintProto.NodeExpressions value) {
      if (dimensions_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      dimensions_.set(index, java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Appends a value to {@code dimensions}. */
    public Builder addDimensions(RoutesConstraintProto.NodeExpressions value) {
      if (dimensions_ == null) {
        dimensions_ = new java.util.ArrayList<>();
      }
      dimensions_.add(java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Appends all values to {@code dimensions}. */
    public Builder addAllDimensions(Iterable<? extends RoutesConstraintProto.NodeExpressions> values) {
      if (dimensions_ == null) {
        dimensions_ = new java.util.ArrayList<>();
      }
      for (RoutesConstraintProto.NodeExpressions v : values) {
        dimensions_.add(java.util.Objects.requireNonNull(v).immutableCopy());
      }
      return this;
    }

    /** Removes all values of {@code dimensions}. */
    public Builder clearDimensions() {
      dimensions_ = null;
      return this;
    }

    /** Replaces the element {@code index} of {@code dimensions} by the content of a builder. */
    public Builder setDimensions(int index, RoutesConstraintProto.NodeExpressions.Builder builderForValue) {
      return setDimensions(index, builderForValue.build());
    }

    /** Appends the content of a builder to {@code dimensions}. */
    public Builder addDimensions(RoutesConstraintProto.NodeExpressions.Builder builderForValue) {
      return addDimensions(builderForValue.build());
    }

    /** Inserts a value into {@code dimensions}. */
    public Builder addDimensions(int index, RoutesConstraintProto.NodeExpressions value) {
      if (dimensions_ == null) {
        dimensions_ = new java.util.ArrayList<>();
      }
      dimensions_.add(index, java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Removes the element {@code index} of {@code dimensions}. */
    public Builder removeDimensions(int index) {
      if (dimensions_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      dimensions_.remove(index);
      return this;
    }

    /** Returns a builder for the element {@code index} of {@code dimensions}; changes to it are reflected in this builder. */
    public RoutesConstraintProto.NodeExpressions.Builder getDimensionsBuilder(int index) {
      if (dimensions_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      RoutesConstraintProto.NodeExpressions e = dimensions_.get(index);
      if (e instanceof RoutesConstraintProto.NodeExpressions.Builder) {
        return (RoutesConstraintProto.NodeExpressions.Builder) e;
      }
      RoutesConstraintProto.NodeExpressions.Builder b = RoutesConstraintProto.NodeExpressions.newBuilder(e);
      dimensions_.set(index, b);
      return b;
    }

    /** Appends a new empty element to {@code dimensions} and returns its builder. */
    public RoutesConstraintProto.NodeExpressions.Builder addDimensionsBuilder() {
      if (dimensions_ == null) {
        dimensions_ = new java.util.ArrayList<>();
      }
      RoutesConstraintProto.NodeExpressions.Builder b = RoutesConstraintProto.NodeExpressions.newBuilder();
      dimensions_.add(b);
      return b;
    }

    /** Returns the builders of all elements of {@code dimensions}. */
    public java.util.List<RoutesConstraintProto.NodeExpressions.Builder> getDimensionsBuilderList() {
      java.util.List<RoutesConstraintProto.NodeExpressions.Builder> r = new java.util.ArrayList<>();
      for (int k = 0; k < getDimensionsCount(); k++) {
        r.add(getDimensionsBuilder(k));
      }
      return r;
    }

  }
}
