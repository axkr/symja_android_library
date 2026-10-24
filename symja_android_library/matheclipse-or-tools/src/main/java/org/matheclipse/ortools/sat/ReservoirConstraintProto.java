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
 * Maintain a reservoir level within bounds. The water level starts at 0, and at
 * any time, it must be within [min_level, max_level].
 *
 * If the variable active_literals[i] is true, and if the expression
 * time_exprs[i] is assigned a value t, then the current level changes by
 * level_changes[i] at the time t. Therefore, at any time t:
 *
 * sum(level_changes[i] * active_literals[i] if time_exprs[i] &lt;= t)
 *   in [min_level, max_level]
 *
 * Note that min level must be &lt;= 0, and the max level must be &gt;= 0. Please use
 * fixed level_changes to simulate an initial state.
 *
 * The array of Boolean variables 'actives', if defined, indicates which actions
 * are actually performed. If this array is not defined, then it is assumed that
 * all actions will be performed.
 * </pre>
 *
 * <p>
 * Plain Java replacement of the protobuf message {@code operations_research.sat.ReservoirConstraintProto} of OR-Tools (generated from ortools/sat/cp_model.proto, no protobuf runtime needed). Instances are immutable; use {@link Builder} to create or modify them.
 */
public class ReservoirConstraintProto implements ProtoMessage {
  private static final ReservoirConstraintProto DEFAULT_INSTANCE = new ReservoirConstraintProto();

  long minLevel_ = 0L;
  long maxLevel_ = 0L;
  java.util.ArrayList<LinearExpressionProto> timeExprs_;
  java.util.ArrayList<LinearExpressionProto> levelChanges_;
  java.util.ArrayList<Integer> activeLiterals_;

  private ReservoirConstraintProto() {}

  /** Returns the immutable default instance (all fields unset). */
  public static ReservoirConstraintProto getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  /** Returns the default instance. */
  public ReservoirConstraintProto getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

  /** Returns {@code min_level}. */
  public long getMinLevel() {
    return minLevel_;
  }

  /** Returns {@code max_level}. */
  public long getMaxLevel() {
    return maxLevel_;
  }

  /** Returns the elements of {@code time_exprs}. */
  public java.util.List<LinearExpressionProto> getTimeExprsList() {
    return timeExprs_ == null ? java.util.Collections.<LinearExpressionProto>emptyList() : java.util.Collections.unmodifiableList(timeExprs_);
  }

  /** Returns the number of elements of {@code time_exprs}. */
  public int getTimeExprsCount() {
    return timeExprs_ == null ? 0 : timeExprs_.size();
  }

  /** Returns the element {@code index} of {@code time_exprs}. */
  public LinearExpressionProto getTimeExprs(int index) {
    if (timeExprs_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return timeExprs_.get(index);
  }

  /** Returns the elements of {@code level_changes}. */
  public java.util.List<LinearExpressionProto> getLevelChangesList() {
    return levelChanges_ == null ? java.util.Collections.<LinearExpressionProto>emptyList() : java.util.Collections.unmodifiableList(levelChanges_);
  }

  /** Returns the number of elements of {@code level_changes}. */
  public int getLevelChangesCount() {
    return levelChanges_ == null ? 0 : levelChanges_.size();
  }

  /** Returns the element {@code index} of {@code level_changes}. */
  public LinearExpressionProto getLevelChanges(int index) {
    if (levelChanges_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return levelChanges_.get(index);
  }

  /** Returns the elements of {@code active_literals}. */
  public java.util.List<Integer> getActiveLiteralsList() {
    return activeLiterals_ == null ? java.util.Collections.<Integer>emptyList() : java.util.Collections.unmodifiableList(activeLiterals_);
  }

  /** Returns the number of elements of {@code active_literals}. */
  public int getActiveLiteralsCount() {
    return activeLiterals_ == null ? 0 : activeLiterals_.size();
  }

  /** Returns the element {@code index} of {@code active_literals}. */
  public int getActiveLiterals(int index) {
    if (activeLiterals_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return activeLiterals_.get(index);
  }

  void copyFieldsFrom(ReservoirConstraintProto src) {
    minLevel_ = src.minLevel_;
    maxLevel_ = src.maxLevel_;
    timeExprs_ = buildListLinearExpressionProto(src.timeExprs_);
    levelChanges_ = buildListLinearExpressionProto(src.levelChanges_);
    activeLiterals_ = src.activeLiterals_ == null || src.activeLiterals_.isEmpty() ? null : new java.util.ArrayList<>(src.activeLiterals_);
  }

  /** Returns this message if it is immutable, otherwise a built copy. */
  ReservoirConstraintProto immutableCopy() {
    if (this instanceof Builder) {
      ReservoirConstraintProto r = new ReservoirConstraintProto();
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
    if (minLevel_ != 0) {
      printer.printScalar("min_level", minLevel_);
    }
    if (maxLevel_ != 0) {
      printer.printScalar("max_level", maxLevel_);
    }
    if (timeExprs_ != null) {
      for (LinearExpressionProto v : timeExprs_) {
        printer.printMessage("time_exprs", v);
      }
    }
    if (activeLiterals_ != null) {
      for (Integer v : activeLiterals_) {
        printer.printScalar("active_literals", v);
      }
    }
    if (levelChanges_ != null) {
      for (LinearExpressionProto v : levelChanges_) {
        printer.printMessage("level_changes", v);
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
    if (!(obj instanceof ReservoirConstraintProto)) {
      return false;
    }
    ReservoirConstraintProto other = (ReservoirConstraintProto) obj;
    return minLevel_ == other.minLevel_
        && maxLevel_ == other.maxLevel_
        && getTimeExprsList().equals(other.getTimeExprsList())
        && getLevelChangesList().equals(other.getLevelChangesList())
        && getActiveLiteralsList().equals(other.getActiveLiteralsList());
  }

  @Override
  public int hashCode() {
    int h = 231;
    h = 31 * h + Long.hashCode(minLevel_);
    h = 31 * h + Long.hashCode(maxLevel_);
    h = 31 * h + getTimeExprsList().hashCode();
    h = 31 * h + getLevelChangesList().hashCode();
    h = 31 * h + getActiveLiteralsList().hashCode();
    return h;
  }

  /** Appends the fields of this message in the protobuf binary format. */
  void writeTo(ProtoWire.Writer w) {
    if (minLevel_ != 0) {
      w.int64(1, minLevel_);
    }
    if (maxLevel_ != 0) {
      w.int64(2, maxLevel_);
    }
    if (timeExprs_ != null) {
      for (LinearExpressionProto v : timeExprs_) {
        ProtoWire.Writer sub = new ProtoWire.Writer();
        v.writeTo(sub);
        w.bytes(3, sub);
      }
    }
    if (activeLiterals_ != null && !activeLiterals_.isEmpty()) {
      w.packedInt32(5, activeLiterals_);
    }
    if (levelChanges_ != null) {
      for (LinearExpressionProto v : levelChanges_) {
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
  public static ReservoirConstraintProto parseFrom(byte[] data) {
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
  public static Builder newBuilder(ReservoirConstraintProto prototype) {
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
   * Builder of {@link ReservoirConstraintProto}. A builder is also a (mutable) {@code ReservoirConstraintProto}, so all getters are
   * available on it.
   */
  public static final class Builder extends ReservoirConstraintProto implements Cloneable {
    Builder() {}

    /** Returns an immutable copy of the current content. */
    public ReservoirConstraintProto build() {
      ReservoirConstraintProto r = new ReservoirConstraintProto();
      r.copyFieldsFrom(this);
      return r;
    }

    /** Same as {@link #build()}. */
    public ReservoirConstraintProto buildPartial() {
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
    public Builder mergeFrom(ReservoirConstraintProto other) {
      if (other == DEFAULT_INSTANCE) {
        return this;
      }
      if (other.minLevel_ != 0) {
        setMinLevel(other.minLevel_);
      }
      if (other.maxLevel_ != 0) {
        setMaxLevel(other.maxLevel_);
      }
      if (other.timeExprs_ != null) {
        addAllTimeExprs(other.timeExprs_);
      }
      if (other.levelChanges_ != null) {
        addAllLevelChanges(other.levelChanges_);
      }
      if (other.activeLiterals_ != null) {
        addAllActiveLiterals(other.activeLiterals_);
      }
      return this;
    }

    /** Replaces the content of this builder by a deep copy of {@code other}. */
    public void copyFrom(ReservoirConstraintProto other) {
      copyFieldsFrom(other);
    }

    /**
     * Returns a modifiable view of {@code time_exprs}: elements added or set through the list are
     * stored in this builder (messages as immutable copies). The view is detached by
     * {@code clearTimeExprs()} and {@code clear()}.
     */
    @Override
    public java.util.List<LinearExpressionProto> getTimeExprsList() {
      if (timeExprs_ == null) {
        timeExprs_ = new java.util.ArrayList<>();
      }
      return new ProtoWire.ListView<>(timeExprs_, LinearExpressionProto::immutableCopy);
    }

    /**
     * Returns a modifiable view of {@code level_changes}: elements added or set through the list are
     * stored in this builder (messages as immutable copies). The view is detached by
     * {@code clearLevelChanges()} and {@code clear()}.
     */
    @Override
    public java.util.List<LinearExpressionProto> getLevelChangesList() {
      if (levelChanges_ == null) {
        levelChanges_ = new java.util.ArrayList<>();
      }
      return new ProtoWire.ListView<>(levelChanges_, LinearExpressionProto::immutableCopy);
    }

    /**
     * Returns a modifiable view of {@code active_literals}: elements added or set through the list are
     * stored in this builder (messages as immutable copies). The view is detached by
     * {@code clearActiveLiterals()} and {@code clear()}.
     */
    @Override
    public java.util.List<Integer> getActiveLiteralsList() {
      if (activeLiterals_ == null) {
        activeLiterals_ = new java.util.ArrayList<>();
      }
      return new ProtoWire.ListView<>(activeLiterals_, null);
    }

    void mergeWire(ProtoWire.Reader r) {
      while (!r.atEnd()) {
        int tag = r.tag();
        switch (tag) {
          case 8:
            setMinLevel(r.varint());
            break;
          case 16:
            setMaxLevel(r.varint());
            break;
          case 26:
            addTimeExprsBuilder().mergeWire(r.message());
            break;
          case 42: {
            ProtoWire.Reader p = r.packed();
            while (!p.atEnd()) {
              addActiveLiterals((int) p.varint());
            }
            break;
          }
          case 40:
            addActiveLiterals((int) r.varint());
            break;
          case 50:
            addLevelChangesBuilder().mergeWire(r.message());
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
          case "min_level": {
            if (strict && minLevel_ != 0) {
              throw ProtoWire.duplicate(t, "min_level");
            }
            t.consume(":");
            setMinLevel(t.integer(Long.MIN_VALUE, Long.MAX_VALUE));
            break;
          }
          case "max_level": {
            if (strict && maxLevel_ != 0) {
              throw ProtoWire.duplicate(t, "max_level");
            }
            t.consume(":");
            setMaxLevel(t.integer(Long.MIN_VALUE, Long.MAX_VALUE));
            break;
          }
          case "time_exprs": {
            t.tryConsume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addTimeExprsBuilder().mergeText(t, t.openMessage(), strict);
                } while (t.nextInList());
              }
            } else {
              addTimeExprsBuilder().mergeText(t, t.openMessage(), strict);
            }
            break;
          }
          case "level_changes": {
            t.tryConsume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addLevelChangesBuilder().mergeText(t, t.openMessage(), strict);
                } while (t.nextInList());
              }
            } else {
              addLevelChangesBuilder().mergeText(t, t.openMessage(), strict);
            }
            break;
          }
          case "active_literals": {
            t.consume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addActiveLiterals((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
                } while (t.nextInList());
              }
            } else {
              addActiveLiterals((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            }
            break;
          }
          default:
            throw ProtoWire.unknownField(t, "operations_research.sat.ReservoirConstraintProto", name);
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

    /** Sets {@code min_level}. */
    public Builder setMinLevel(long value) {
      minLevel_ = value;
      return this;
    }

    /** Resets {@code min_level} to its default value. */
    public Builder clearMinLevel() {
      minLevel_ = 0L;
      return this;
    }

    /** Sets {@code max_level}. */
    public Builder setMaxLevel(long value) {
      maxLevel_ = value;
      return this;
    }

    /** Resets {@code max_level} to its default value. */
    public Builder clearMaxLevel() {
      maxLevel_ = 0L;
      return this;
    }

    /** Replaces the element {@code index} of {@code time_exprs}. */
    public Builder setTimeExprs(int index, LinearExpressionProto value) {
      if (timeExprs_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      timeExprs_.set(index, java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Appends a value to {@code time_exprs}. */
    public Builder addTimeExprs(LinearExpressionProto value) {
      if (timeExprs_ == null) {
        timeExprs_ = new java.util.ArrayList<>();
      }
      timeExprs_.add(java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Appends all values to {@code time_exprs}. */
    public Builder addAllTimeExprs(Iterable<? extends LinearExpressionProto> values) {
      if (timeExprs_ == null) {
        timeExprs_ = new java.util.ArrayList<>();
      }
      for (LinearExpressionProto v : values) {
        timeExprs_.add(java.util.Objects.requireNonNull(v).immutableCopy());
      }
      return this;
    }

    /** Removes all values of {@code time_exprs}. */
    public Builder clearTimeExprs() {
      timeExprs_ = null;
      return this;
    }

    /** Replaces the element {@code index} of {@code time_exprs} by the content of a builder. */
    public Builder setTimeExprs(int index, LinearExpressionProto.Builder builderForValue) {
      return setTimeExprs(index, builderForValue.build());
    }

    /** Appends the content of a builder to {@code time_exprs}. */
    public Builder addTimeExprs(LinearExpressionProto.Builder builderForValue) {
      return addTimeExprs(builderForValue.build());
    }

    /** Inserts a value into {@code time_exprs}. */
    public Builder addTimeExprs(int index, LinearExpressionProto value) {
      if (timeExprs_ == null) {
        timeExprs_ = new java.util.ArrayList<>();
      }
      timeExprs_.add(index, java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Removes the element {@code index} of {@code time_exprs}. */
    public Builder removeTimeExprs(int index) {
      if (timeExprs_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      timeExprs_.remove(index);
      return this;
    }

    /** Returns a builder for the element {@code index} of {@code time_exprs}; changes to it are reflected in this builder. */
    public LinearExpressionProto.Builder getTimeExprsBuilder(int index) {
      if (timeExprs_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      LinearExpressionProto e = timeExprs_.get(index);
      if (e instanceof LinearExpressionProto.Builder) {
        return (LinearExpressionProto.Builder) e;
      }
      LinearExpressionProto.Builder b = LinearExpressionProto.newBuilder(e);
      timeExprs_.set(index, b);
      return b;
    }

    /** Appends a new empty element to {@code time_exprs} and returns its builder. */
    public LinearExpressionProto.Builder addTimeExprsBuilder() {
      if (timeExprs_ == null) {
        timeExprs_ = new java.util.ArrayList<>();
      }
      LinearExpressionProto.Builder b = LinearExpressionProto.newBuilder();
      timeExprs_.add(b);
      return b;
    }

    /** Returns the builders of all elements of {@code time_exprs}. */
    public java.util.List<LinearExpressionProto.Builder> getTimeExprsBuilderList() {
      java.util.List<LinearExpressionProto.Builder> r = new java.util.ArrayList<>();
      for (int k = 0; k < getTimeExprsCount(); k++) {
        r.add(getTimeExprsBuilder(k));
      }
      return r;
    }

    /** Replaces the element {@code index} of {@code level_changes}. */
    public Builder setLevelChanges(int index, LinearExpressionProto value) {
      if (levelChanges_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      levelChanges_.set(index, java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Appends a value to {@code level_changes}. */
    public Builder addLevelChanges(LinearExpressionProto value) {
      if (levelChanges_ == null) {
        levelChanges_ = new java.util.ArrayList<>();
      }
      levelChanges_.add(java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Appends all values to {@code level_changes}. */
    public Builder addAllLevelChanges(Iterable<? extends LinearExpressionProto> values) {
      if (levelChanges_ == null) {
        levelChanges_ = new java.util.ArrayList<>();
      }
      for (LinearExpressionProto v : values) {
        levelChanges_.add(java.util.Objects.requireNonNull(v).immutableCopy());
      }
      return this;
    }

    /** Removes all values of {@code level_changes}. */
    public Builder clearLevelChanges() {
      levelChanges_ = null;
      return this;
    }

    /** Replaces the element {@code index} of {@code level_changes} by the content of a builder. */
    public Builder setLevelChanges(int index, LinearExpressionProto.Builder builderForValue) {
      return setLevelChanges(index, builderForValue.build());
    }

    /** Appends the content of a builder to {@code level_changes}. */
    public Builder addLevelChanges(LinearExpressionProto.Builder builderForValue) {
      return addLevelChanges(builderForValue.build());
    }

    /** Inserts a value into {@code level_changes}. */
    public Builder addLevelChanges(int index, LinearExpressionProto value) {
      if (levelChanges_ == null) {
        levelChanges_ = new java.util.ArrayList<>();
      }
      levelChanges_.add(index, java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Removes the element {@code index} of {@code level_changes}. */
    public Builder removeLevelChanges(int index) {
      if (levelChanges_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      levelChanges_.remove(index);
      return this;
    }

    /** Returns a builder for the element {@code index} of {@code level_changes}; changes to it are reflected in this builder. */
    public LinearExpressionProto.Builder getLevelChangesBuilder(int index) {
      if (levelChanges_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      LinearExpressionProto e = levelChanges_.get(index);
      if (e instanceof LinearExpressionProto.Builder) {
        return (LinearExpressionProto.Builder) e;
      }
      LinearExpressionProto.Builder b = LinearExpressionProto.newBuilder(e);
      levelChanges_.set(index, b);
      return b;
    }

    /** Appends a new empty element to {@code level_changes} and returns its builder. */
    public LinearExpressionProto.Builder addLevelChangesBuilder() {
      if (levelChanges_ == null) {
        levelChanges_ = new java.util.ArrayList<>();
      }
      LinearExpressionProto.Builder b = LinearExpressionProto.newBuilder();
      levelChanges_.add(b);
      return b;
    }

    /** Returns the builders of all elements of {@code level_changes}. */
    public java.util.List<LinearExpressionProto.Builder> getLevelChangesBuilderList() {
      java.util.List<LinearExpressionProto.Builder> r = new java.util.ArrayList<>();
      for (int k = 0; k < getLevelChangesCount(); k++) {
        r.add(getLevelChangesBuilder(k));
      }
      return r;
    }

    /** Replaces the element {@code index} of {@code active_literals}. */
    public Builder setActiveLiterals(int index, int value) {
      if (activeLiterals_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      activeLiterals_.set(index, value);
      return this;
    }

    /** Appends a value to {@code active_literals}. */
    public Builder addActiveLiterals(int value) {
      if (activeLiterals_ == null) {
        activeLiterals_ = new java.util.ArrayList<>();
      }
      activeLiterals_.add(value);
      return this;
    }

    /** Appends all values to {@code active_literals}. */
    public Builder addAllActiveLiterals(Iterable<? extends Integer> values) {
      if (activeLiterals_ == null) {
        activeLiterals_ = new java.util.ArrayList<>();
      }
      for (Integer v : values) {
        activeLiterals_.add(v);
      }
      return this;
    }

    /** Removes all values of {@code active_literals}. */
    public Builder clearActiveLiterals() {
      activeLiterals_ = null;
      return this;
    }

  }
}
