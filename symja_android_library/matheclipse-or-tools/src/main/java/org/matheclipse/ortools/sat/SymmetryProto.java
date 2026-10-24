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
 * EXPERIMENTAL. For now, this is meant to be used by the solver and not filled
 * by clients.
 *
 * Hold symmetry information about the set of feasible solutions. If we permute
 * the variable values of any feasible solution using one of the permutations
 * described here, we should always get another feasible solution.
 *
 * We usually also enforce that the objective of the new solution is the same.
 *
 * The group of permutations encoded here is usually computed from the encoding
 * of the model, so it is not meant to be a complete representation of the
 * feasible solution symmetries, just a valid subgroup.
 * </pre>
 *
 * <p>
 * Plain Java replacement of the protobuf message {@code operations_research.sat.SymmetryProto} of OR-Tools (generated from ortools/sat/cp_model.proto, no protobuf runtime needed). Instances are immutable; use {@link Builder} to create or modify them.
 */
public class SymmetryProto implements ProtoMessage {
  private static final SymmetryProto DEFAULT_INSTANCE = new SymmetryProto();

  java.util.ArrayList<SparsePermutationProto> permutations_;
  java.util.ArrayList<DenseMatrixProto> orbitopes_;

  private SymmetryProto() {}

  /** Returns the immutable default instance (all fields unset). */
  public static SymmetryProto getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  /** Returns the default instance. */
  public SymmetryProto getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

  /**
   * <pre>
   * A list of variable indices permutations that leave the feasible space of
   * solutions invariant. Usually, we only encode a set of generators of the
   * group.
   * </pre>
   */
  public java.util.List<SparsePermutationProto> getPermutationsList() {
    return permutations_ == null ? java.util.Collections.<SparsePermutationProto>emptyList() : java.util.Collections.unmodifiableList(permutations_);
  }

  /** Returns the number of elements of {@code permutations}. */
  public int getPermutationsCount() {
    return permutations_ == null ? 0 : permutations_.size();
  }

  /** Returns the element {@code index} of {@code permutations}. */
  public SparsePermutationProto getPermutations(int index) {
    if (permutations_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return permutations_.get(index);
  }

  /**
   * <pre>
   * An orbitope is a special symmetry structure of the solution space. If the
   * variable indices are arranged in a matrix (with no duplicates), then any
   * permutation of the columns will be a valid permutation of the feasible
   * space.
   *
   * This arises quite often. The typical example is a graph coloring problem
   * where for each node i, you have j booleans to indicate its color. If the
   * variables color_of_i_is_j are arranged in a matrix[i][j], then any column
   * permutations leave the problem invariant.
   * </pre>
   */
  public java.util.List<DenseMatrixProto> getOrbitopesList() {
    return orbitopes_ == null ? java.util.Collections.<DenseMatrixProto>emptyList() : java.util.Collections.unmodifiableList(orbitopes_);
  }

  /** Returns the number of elements of {@code orbitopes}. */
  public int getOrbitopesCount() {
    return orbitopes_ == null ? 0 : orbitopes_.size();
  }

  /** Returns the element {@code index} of {@code orbitopes}. */
  public DenseMatrixProto getOrbitopes(int index) {
    if (orbitopes_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return orbitopes_.get(index);
  }

  void copyFieldsFrom(SymmetryProto src) {
    permutations_ = buildListSparsePermutationProto(src.permutations_);
    orbitopes_ = buildListDenseMatrixProto(src.orbitopes_);
  }

  /** Returns this message if it is immutable, otherwise a built copy. */
  SymmetryProto immutableCopy() {
    if (this instanceof Builder) {
      SymmetryProto r = new SymmetryProto();
      r.copyFieldsFrom(this);
      return r;
    }
    return this;
  }

  private static java.util.ArrayList<SparsePermutationProto> buildListSparsePermutationProto(java.util.List<SparsePermutationProto> src) {
    if (src == null || src.isEmpty()) {
      return null;
    }
    java.util.ArrayList<SparsePermutationProto> r = new java.util.ArrayList<>(src.size());
    for (SparsePermutationProto e : src) {
      r.add(e.immutableCopy());
    }
    return r;
  }

  private static java.util.ArrayList<DenseMatrixProto> buildListDenseMatrixProto(java.util.List<DenseMatrixProto> src) {
    if (src == null || src.isEmpty()) {
      return null;
    }
    java.util.ArrayList<DenseMatrixProto> r = new java.util.ArrayList<>(src.size());
    for (DenseMatrixProto e : src) {
      r.add(e.immutableCopy());
    }
    return r;
  }

  @Override
  public void printTo(ProtoPrinter printer) {
    if (permutations_ != null) {
      for (SparsePermutationProto v : permutations_) {
        printer.printMessage("permutations", v);
      }
    }
    if (orbitopes_ != null) {
      for (DenseMatrixProto v : orbitopes_) {
        printer.printMessage("orbitopes", v);
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
    if (!(obj instanceof SymmetryProto)) {
      return false;
    }
    SymmetryProto other = (SymmetryProto) obj;
    return getPermutationsList().equals(other.getPermutationsList())
        && getOrbitopesList().equals(other.getOrbitopesList());
  }

  @Override
  public int hashCode() {
    int h = 576;
    h = 31 * h + getPermutationsList().hashCode();
    h = 31 * h + getOrbitopesList().hashCode();
    return h;
  }

  /** Appends the fields of this message in the protobuf binary format. */
  void writeTo(ProtoWire.Writer w) {
    if (permutations_ != null) {
      for (SparsePermutationProto v : permutations_) {
        ProtoWire.Writer sub = new ProtoWire.Writer();
        v.writeTo(sub);
        w.bytes(1, sub);
      }
    }
    if (orbitopes_ != null) {
      for (DenseMatrixProto v : orbitopes_) {
        ProtoWire.Writer sub = new ProtoWire.Writer();
        v.writeTo(sub);
        w.bytes(2, sub);
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
  public static SymmetryProto parseFrom(byte[] data) {
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
  public static Builder newBuilder(SymmetryProto prototype) {
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
   * Builder of {@link SymmetryProto}. A builder is also a (mutable) {@code SymmetryProto}, so all getters are
   * available on it.
   */
  public static final class Builder extends SymmetryProto implements Cloneable {
    Builder() {}

    /** Returns an immutable copy of the current content. */
    public SymmetryProto build() {
      SymmetryProto r = new SymmetryProto();
      r.copyFieldsFrom(this);
      return r;
    }

    /** Same as {@link #build()}. */
    public SymmetryProto buildPartial() {
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
    public Builder mergeFrom(SymmetryProto other) {
      if (other == DEFAULT_INSTANCE) {
        return this;
      }
      if (other.permutations_ != null) {
        addAllPermutations(other.permutations_);
      }
      if (other.orbitopes_ != null) {
        addAllOrbitopes(other.orbitopes_);
      }
      return this;
    }

    /** Replaces the content of this builder by a deep copy of {@code other}. */
    public void copyFrom(SymmetryProto other) {
      copyFieldsFrom(other);
    }

    /**
     * Returns a modifiable view of {@code permutations}: elements added or set through the list are
     * stored in this builder (messages as immutable copies). The view is detached by
     * {@code clearPermutations()} and {@code clear()}.
     */
    @Override
    public java.util.List<SparsePermutationProto> getPermutationsList() {
      if (permutations_ == null) {
        permutations_ = new java.util.ArrayList<>();
      }
      return new ProtoWire.ListView<>(permutations_, SparsePermutationProto::immutableCopy);
    }

    /**
     * Returns a modifiable view of {@code orbitopes}: elements added or set through the list are
     * stored in this builder (messages as immutable copies). The view is detached by
     * {@code clearOrbitopes()} and {@code clear()}.
     */
    @Override
    public java.util.List<DenseMatrixProto> getOrbitopesList() {
      if (orbitopes_ == null) {
        orbitopes_ = new java.util.ArrayList<>();
      }
      return new ProtoWire.ListView<>(orbitopes_, DenseMatrixProto::immutableCopy);
    }

    void mergeWire(ProtoWire.Reader r) {
      while (!r.atEnd()) {
        int tag = r.tag();
        switch (tag) {
          case 10:
            addPermutationsBuilder().mergeWire(r.message());
            break;
          case 18:
            addOrbitopesBuilder().mergeWire(r.message());
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
          case "permutations": {
            t.tryConsume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addPermutationsBuilder().mergeText(t, t.openMessage(), strict);
                } while (t.nextInList());
              }
            } else {
              addPermutationsBuilder().mergeText(t, t.openMessage(), strict);
            }
            break;
          }
          case "orbitopes": {
            t.tryConsume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addOrbitopesBuilder().mergeText(t, t.openMessage(), strict);
                } while (t.nextInList());
              }
            } else {
              addOrbitopesBuilder().mergeText(t, t.openMessage(), strict);
            }
            break;
          }
          default:
            throw ProtoWire.unknownField(t, "operations_research.sat.SymmetryProto", name);
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

    /** Replaces the element {@code index} of {@code permutations}. */
    public Builder setPermutations(int index, SparsePermutationProto value) {
      if (permutations_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      permutations_.set(index, java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Appends a value to {@code permutations}. */
    public Builder addPermutations(SparsePermutationProto value) {
      if (permutations_ == null) {
        permutations_ = new java.util.ArrayList<>();
      }
      permutations_.add(java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Appends all values to {@code permutations}. */
    public Builder addAllPermutations(Iterable<? extends SparsePermutationProto> values) {
      if (permutations_ == null) {
        permutations_ = new java.util.ArrayList<>();
      }
      for (SparsePermutationProto v : values) {
        permutations_.add(java.util.Objects.requireNonNull(v).immutableCopy());
      }
      return this;
    }

    /** Removes all values of {@code permutations}. */
    public Builder clearPermutations() {
      permutations_ = null;
      return this;
    }

    /** Replaces the element {@code index} of {@code permutations} by the content of a builder. */
    public Builder setPermutations(int index, SparsePermutationProto.Builder builderForValue) {
      return setPermutations(index, builderForValue.build());
    }

    /** Appends the content of a builder to {@code permutations}. */
    public Builder addPermutations(SparsePermutationProto.Builder builderForValue) {
      return addPermutations(builderForValue.build());
    }

    /** Inserts a value into {@code permutations}. */
    public Builder addPermutations(int index, SparsePermutationProto value) {
      if (permutations_ == null) {
        permutations_ = new java.util.ArrayList<>();
      }
      permutations_.add(index, java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Removes the element {@code index} of {@code permutations}. */
    public Builder removePermutations(int index) {
      if (permutations_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      permutations_.remove(index);
      return this;
    }

    /** Returns a builder for the element {@code index} of {@code permutations}; changes to it are reflected in this builder. */
    public SparsePermutationProto.Builder getPermutationsBuilder(int index) {
      if (permutations_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      SparsePermutationProto e = permutations_.get(index);
      if (e instanceof SparsePermutationProto.Builder) {
        return (SparsePermutationProto.Builder) e;
      }
      SparsePermutationProto.Builder b = SparsePermutationProto.newBuilder(e);
      permutations_.set(index, b);
      return b;
    }

    /** Appends a new empty element to {@code permutations} and returns its builder. */
    public SparsePermutationProto.Builder addPermutationsBuilder() {
      if (permutations_ == null) {
        permutations_ = new java.util.ArrayList<>();
      }
      SparsePermutationProto.Builder b = SparsePermutationProto.newBuilder();
      permutations_.add(b);
      return b;
    }

    /** Returns the builders of all elements of {@code permutations}. */
    public java.util.List<SparsePermutationProto.Builder> getPermutationsBuilderList() {
      java.util.List<SparsePermutationProto.Builder> r = new java.util.ArrayList<>();
      for (int k = 0; k < getPermutationsCount(); k++) {
        r.add(getPermutationsBuilder(k));
      }
      return r;
    }

    /** Replaces the element {@code index} of {@code orbitopes}. */
    public Builder setOrbitopes(int index, DenseMatrixProto value) {
      if (orbitopes_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      orbitopes_.set(index, java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Appends a value to {@code orbitopes}. */
    public Builder addOrbitopes(DenseMatrixProto value) {
      if (orbitopes_ == null) {
        orbitopes_ = new java.util.ArrayList<>();
      }
      orbitopes_.add(java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Appends all values to {@code orbitopes}. */
    public Builder addAllOrbitopes(Iterable<? extends DenseMatrixProto> values) {
      if (orbitopes_ == null) {
        orbitopes_ = new java.util.ArrayList<>();
      }
      for (DenseMatrixProto v : values) {
        orbitopes_.add(java.util.Objects.requireNonNull(v).immutableCopy());
      }
      return this;
    }

    /** Removes all values of {@code orbitopes}. */
    public Builder clearOrbitopes() {
      orbitopes_ = null;
      return this;
    }

    /** Replaces the element {@code index} of {@code orbitopes} by the content of a builder. */
    public Builder setOrbitopes(int index, DenseMatrixProto.Builder builderForValue) {
      return setOrbitopes(index, builderForValue.build());
    }

    /** Appends the content of a builder to {@code orbitopes}. */
    public Builder addOrbitopes(DenseMatrixProto.Builder builderForValue) {
      return addOrbitopes(builderForValue.build());
    }

    /** Inserts a value into {@code orbitopes}. */
    public Builder addOrbitopes(int index, DenseMatrixProto value) {
      if (orbitopes_ == null) {
        orbitopes_ = new java.util.ArrayList<>();
      }
      orbitopes_.add(index, java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Removes the element {@code index} of {@code orbitopes}. */
    public Builder removeOrbitopes(int index) {
      if (orbitopes_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      orbitopes_.remove(index);
      return this;
    }

    /** Returns a builder for the element {@code index} of {@code orbitopes}; changes to it are reflected in this builder. */
    public DenseMatrixProto.Builder getOrbitopesBuilder(int index) {
      if (orbitopes_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      DenseMatrixProto e = orbitopes_.get(index);
      if (e instanceof DenseMatrixProto.Builder) {
        return (DenseMatrixProto.Builder) e;
      }
      DenseMatrixProto.Builder b = DenseMatrixProto.newBuilder(e);
      orbitopes_.set(index, b);
      return b;
    }

    /** Appends a new empty element to {@code orbitopes} and returns its builder. */
    public DenseMatrixProto.Builder addOrbitopesBuilder() {
      if (orbitopes_ == null) {
        orbitopes_ = new java.util.ArrayList<>();
      }
      DenseMatrixProto.Builder b = DenseMatrixProto.newBuilder();
      orbitopes_.add(b);
      return b;
    }

    /** Returns the builders of all elements of {@code orbitopes}. */
    public java.util.List<DenseMatrixProto.Builder> getOrbitopesBuilderList() {
      java.util.List<DenseMatrixProto.Builder> r = new java.util.ArrayList<>();
      for (int k = 0; k < getOrbitopesCount(); k++) {
        r.add(getOrbitopesBuilder(k));
      }
      return r;
    }

  }
}
