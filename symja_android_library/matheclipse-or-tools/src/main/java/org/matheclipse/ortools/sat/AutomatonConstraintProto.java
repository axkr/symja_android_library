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
 * This constraint forces a sequence of expressions to be accepted by an
 * automaton.
 * </pre>
 *
 * <p>
 * Plain Java replacement of the protobuf message {@code operations_research.sat.AutomatonConstraintProto} of OR-Tools (generated from ortools/sat/cp_model.proto, no protobuf runtime needed). Instances are immutable; use {@link Builder} to create or modify them.
 */
public class AutomatonConstraintProto implements ProtoMessage {
  private static final AutomatonConstraintProto DEFAULT_INSTANCE = new AutomatonConstraintProto();

  long startingState_ = 0L;
  java.util.ArrayList<Long> finalStates_;
  java.util.ArrayList<Long> transitionTail_;
  java.util.ArrayList<Long> transitionHead_;
  java.util.ArrayList<Long> transitionLabel_;
  java.util.ArrayList<Integer> vars_;
  java.util.ArrayList<LinearExpressionProto> exprs_;

  private AutomatonConstraintProto() {}

  /** Returns the immutable default instance (all fields unset). */
  public static AutomatonConstraintProto getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  /** Returns the default instance. */
  public AutomatonConstraintProto getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

  /**
   * <pre>
   * A state is identified by a non-negative number. It is preferable to keep
   * all the states dense in say [0, num_states). The automaton starts at
   * starting_state and must finish in any of the final states.
   * </pre>
   */
  public long getStartingState() {
    return startingState_;
  }

  /** Returns the elements of {@code final_states}. */
  public java.util.List<Long> getFinalStatesList() {
    return finalStates_ == null ? java.util.Collections.<Long>emptyList() : java.util.Collections.unmodifiableList(finalStates_);
  }

  /** Returns the number of elements of {@code final_states}. */
  public int getFinalStatesCount() {
    return finalStates_ == null ? 0 : finalStates_.size();
  }

  /** Returns the element {@code index} of {@code final_states}. */
  public long getFinalStates(int index) {
    if (finalStates_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return finalStates_.get(index);
  }

  /**
   * <pre>
   * List of transitions (all 3 vectors have the same size). Both tail and head
   * are states, label is any variable value. No two outgoing transitions from
   * the same state can have the same label.
   * </pre>
   */
  public java.util.List<Long> getTransitionTailList() {
    return transitionTail_ == null ? java.util.Collections.<Long>emptyList() : java.util.Collections.unmodifiableList(transitionTail_);
  }

  /** Returns the number of elements of {@code transition_tail}. */
  public int getTransitionTailCount() {
    return transitionTail_ == null ? 0 : transitionTail_.size();
  }

  /** Returns the element {@code index} of {@code transition_tail}. */
  public long getTransitionTail(int index) {
    if (transitionTail_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return transitionTail_.get(index);
  }

  /** Returns the elements of {@code transition_head}. */
  public java.util.List<Long> getTransitionHeadList() {
    return transitionHead_ == null ? java.util.Collections.<Long>emptyList() : java.util.Collections.unmodifiableList(transitionHead_);
  }

  /** Returns the number of elements of {@code transition_head}. */
  public int getTransitionHeadCount() {
    return transitionHead_ == null ? 0 : transitionHead_.size();
  }

  /** Returns the element {@code index} of {@code transition_head}. */
  public long getTransitionHead(int index) {
    if (transitionHead_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return transitionHead_.get(index);
  }

  /** Returns the elements of {@code transition_label}. */
  public java.util.List<Long> getTransitionLabelList() {
    return transitionLabel_ == null ? java.util.Collections.<Long>emptyList() : java.util.Collections.unmodifiableList(transitionLabel_);
  }

  /** Returns the number of elements of {@code transition_label}. */
  public int getTransitionLabelCount() {
    return transitionLabel_ == null ? 0 : transitionLabel_.size();
  }

  /** Returns the element {@code index} of {@code transition_label}. */
  public long getTransitionLabel(int index) {
    if (transitionLabel_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return transitionLabel_.get(index);
  }

  /**
   * <pre>
   * Legacy field.
   * </pre>
   */
  public java.util.List<Integer> getVarsList() {
    return vars_ == null ? java.util.Collections.<Integer>emptyList() : java.util.Collections.unmodifiableList(vars_);
  }

  /** Returns the number of elements of {@code vars}. */
  public int getVarsCount() {
    return vars_ == null ? 0 : vars_.size();
  }

  /** Returns the element {@code index} of {@code vars}. */
  public int getVars(int index) {
    if (vars_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return vars_.get(index);
  }

  /**
   * <pre>
   * The sequence of expressions. The automaton is run for exprs_size() "steps"
   * and the value of exprs[i] corresponds to the transition label at step i.
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

  void copyFieldsFrom(AutomatonConstraintProto src) {
    startingState_ = src.startingState_;
    finalStates_ = src.finalStates_ == null || src.finalStates_.isEmpty() ? null : new java.util.ArrayList<>(src.finalStates_);
    transitionTail_ = src.transitionTail_ == null || src.transitionTail_.isEmpty() ? null : new java.util.ArrayList<>(src.transitionTail_);
    transitionHead_ = src.transitionHead_ == null || src.transitionHead_.isEmpty() ? null : new java.util.ArrayList<>(src.transitionHead_);
    transitionLabel_ = src.transitionLabel_ == null || src.transitionLabel_.isEmpty() ? null : new java.util.ArrayList<>(src.transitionLabel_);
    vars_ = src.vars_ == null || src.vars_.isEmpty() ? null : new java.util.ArrayList<>(src.vars_);
    exprs_ = buildListLinearExpressionProto(src.exprs_);
  }

  /** Returns this message if it is immutable, otherwise a built copy. */
  AutomatonConstraintProto immutableCopy() {
    if (this instanceof Builder) {
      AutomatonConstraintProto r = new AutomatonConstraintProto();
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
    if (startingState_ != 0) {
      printer.printScalar("starting_state", startingState_);
    }
    if (finalStates_ != null) {
      for (Long v : finalStates_) {
        printer.printScalar("final_states", v);
      }
    }
    if (transitionTail_ != null) {
      for (Long v : transitionTail_) {
        printer.printScalar("transition_tail", v);
      }
    }
    if (transitionHead_ != null) {
      for (Long v : transitionHead_) {
        printer.printScalar("transition_head", v);
      }
    }
    if (transitionLabel_ != null) {
      for (Long v : transitionLabel_) {
        printer.printScalar("transition_label", v);
      }
    }
    if (vars_ != null) {
      for (Integer v : vars_) {
        printer.printScalar("vars", v);
      }
    }
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
    if (!(obj instanceof AutomatonConstraintProto)) {
      return false;
    }
    AutomatonConstraintProto other = (AutomatonConstraintProto) obj;
    return startingState_ == other.startingState_
        && getFinalStatesList().equals(other.getFinalStatesList())
        && getTransitionTailList().equals(other.getTransitionTailList())
        && getTransitionHeadList().equals(other.getTransitionHeadList())
        && getTransitionLabelList().equals(other.getTransitionLabelList())
        && getVarsList().equals(other.getVarsList())
        && getExprsList().equals(other.getExprsList());
  }

  @Override
  public int hashCode() {
    int h = 754;
    h = 31 * h + Long.hashCode(startingState_);
    h = 31 * h + getFinalStatesList().hashCode();
    h = 31 * h + getTransitionTailList().hashCode();
    h = 31 * h + getTransitionHeadList().hashCode();
    h = 31 * h + getTransitionLabelList().hashCode();
    h = 31 * h + getVarsList().hashCode();
    h = 31 * h + getExprsList().hashCode();
    return h;
  }

  /** Appends the fields of this message in the protobuf binary format. */
  void writeTo(ProtoWire.Writer w) {
    if (startingState_ != 0) {
      w.int64(2, startingState_);
    }
    if (finalStates_ != null && !finalStates_.isEmpty()) {
      w.packedInt64(3, finalStates_);
    }
    if (transitionTail_ != null && !transitionTail_.isEmpty()) {
      w.packedInt64(4, transitionTail_);
    }
    if (transitionHead_ != null && !transitionHead_.isEmpty()) {
      w.packedInt64(5, transitionHead_);
    }
    if (transitionLabel_ != null && !transitionLabel_.isEmpty()) {
      w.packedInt64(6, transitionLabel_);
    }
    if (vars_ != null && !vars_.isEmpty()) {
      w.packedInt32(7, vars_);
    }
    if (exprs_ != null) {
      for (LinearExpressionProto v : exprs_) {
        ProtoWire.Writer sub = new ProtoWire.Writer();
        v.writeTo(sub);
        w.bytes(8, sub);
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
  public static AutomatonConstraintProto parseFrom(byte[] data) {
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
  public static Builder newBuilder(AutomatonConstraintProto prototype) {
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
   * Builder of {@link AutomatonConstraintProto}. A builder is also a (mutable) {@code AutomatonConstraintProto}, so all getters are
   * available on it.
   */
  public static final class Builder extends AutomatonConstraintProto implements Cloneable {
    Builder() {}

    /** Returns an immutable copy of the current content. */
    public AutomatonConstraintProto build() {
      AutomatonConstraintProto r = new AutomatonConstraintProto();
      r.copyFieldsFrom(this);
      return r;
    }

    /** Same as {@link #build()}. */
    public AutomatonConstraintProto buildPartial() {
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
    public Builder mergeFrom(AutomatonConstraintProto other) {
      if (other == DEFAULT_INSTANCE) {
        return this;
      }
      if (other.startingState_ != 0) {
        setStartingState(other.startingState_);
      }
      if (other.finalStates_ != null) {
        addAllFinalStates(other.finalStates_);
      }
      if (other.transitionTail_ != null) {
        addAllTransitionTail(other.transitionTail_);
      }
      if (other.transitionHead_ != null) {
        addAllTransitionHead(other.transitionHead_);
      }
      if (other.transitionLabel_ != null) {
        addAllTransitionLabel(other.transitionLabel_);
      }
      if (other.vars_ != null) {
        addAllVars(other.vars_);
      }
      if (other.exprs_ != null) {
        addAllExprs(other.exprs_);
      }
      return this;
    }

    /** Replaces the content of this builder by a deep copy of {@code other}. */
    public void copyFrom(AutomatonConstraintProto other) {
      copyFieldsFrom(other);
    }

    /**
     * Returns a modifiable view of {@code final_states}: elements added or set through the list are
     * stored in this builder (messages as immutable copies). The view is detached by
     * {@code clearFinalStates()} and {@code clear()}.
     */
    @Override
    public java.util.List<Long> getFinalStatesList() {
      if (finalStates_ == null) {
        finalStates_ = new java.util.ArrayList<>();
      }
      return new ProtoWire.ListView<>(finalStates_, null);
    }

    /**
     * Returns a modifiable view of {@code transition_tail}: elements added or set through the list are
     * stored in this builder (messages as immutable copies). The view is detached by
     * {@code clearTransitionTail()} and {@code clear()}.
     */
    @Override
    public java.util.List<Long> getTransitionTailList() {
      if (transitionTail_ == null) {
        transitionTail_ = new java.util.ArrayList<>();
      }
      return new ProtoWire.ListView<>(transitionTail_, null);
    }

    /**
     * Returns a modifiable view of {@code transition_head}: elements added or set through the list are
     * stored in this builder (messages as immutable copies). The view is detached by
     * {@code clearTransitionHead()} and {@code clear()}.
     */
    @Override
    public java.util.List<Long> getTransitionHeadList() {
      if (transitionHead_ == null) {
        transitionHead_ = new java.util.ArrayList<>();
      }
      return new ProtoWire.ListView<>(transitionHead_, null);
    }

    /**
     * Returns a modifiable view of {@code transition_label}: elements added or set through the list are
     * stored in this builder (messages as immutable copies). The view is detached by
     * {@code clearTransitionLabel()} and {@code clear()}.
     */
    @Override
    public java.util.List<Long> getTransitionLabelList() {
      if (transitionLabel_ == null) {
        transitionLabel_ = new java.util.ArrayList<>();
      }
      return new ProtoWire.ListView<>(transitionLabel_, null);
    }

    /**
     * Returns a modifiable view of {@code vars}: elements added or set through the list are
     * stored in this builder (messages as immutable copies). The view is detached by
     * {@code clearVars()} and {@code clear()}.
     */
    @Override
    public java.util.List<Integer> getVarsList() {
      if (vars_ == null) {
        vars_ = new java.util.ArrayList<>();
      }
      return new ProtoWire.ListView<>(vars_, null);
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
          case 16:
            setStartingState(r.varint());
            break;
          case 26: {
            ProtoWire.Reader p = r.packed();
            while (!p.atEnd()) {
              addFinalStates(p.varint());
            }
            break;
          }
          case 24:
            addFinalStates(r.varint());
            break;
          case 34: {
            ProtoWire.Reader p = r.packed();
            while (!p.atEnd()) {
              addTransitionTail(p.varint());
            }
            break;
          }
          case 32:
            addTransitionTail(r.varint());
            break;
          case 42: {
            ProtoWire.Reader p = r.packed();
            while (!p.atEnd()) {
              addTransitionHead(p.varint());
            }
            break;
          }
          case 40:
            addTransitionHead(r.varint());
            break;
          case 50: {
            ProtoWire.Reader p = r.packed();
            while (!p.atEnd()) {
              addTransitionLabel(p.varint());
            }
            break;
          }
          case 48:
            addTransitionLabel(r.varint());
            break;
          case 58: {
            ProtoWire.Reader p = r.packed();
            while (!p.atEnd()) {
              addVars((int) p.varint());
            }
            break;
          }
          case 56:
            addVars((int) r.varint());
            break;
          case 66:
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
          case "starting_state": {
            if (strict && startingState_ != 0) {
              throw ProtoWire.duplicate(t, "starting_state");
            }
            t.consume(":");
            setStartingState(t.integer(Long.MIN_VALUE, Long.MAX_VALUE));
            break;
          }
          case "final_states": {
            t.consume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addFinalStates(t.integer(Long.MIN_VALUE, Long.MAX_VALUE));
                } while (t.nextInList());
              }
            } else {
              addFinalStates(t.integer(Long.MIN_VALUE, Long.MAX_VALUE));
            }
            break;
          }
          case "transition_tail": {
            t.consume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addTransitionTail(t.integer(Long.MIN_VALUE, Long.MAX_VALUE));
                } while (t.nextInList());
              }
            } else {
              addTransitionTail(t.integer(Long.MIN_VALUE, Long.MAX_VALUE));
            }
            break;
          }
          case "transition_head": {
            t.consume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addTransitionHead(t.integer(Long.MIN_VALUE, Long.MAX_VALUE));
                } while (t.nextInList());
              }
            } else {
              addTransitionHead(t.integer(Long.MIN_VALUE, Long.MAX_VALUE));
            }
            break;
          }
          case "transition_label": {
            t.consume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addTransitionLabel(t.integer(Long.MIN_VALUE, Long.MAX_VALUE));
                } while (t.nextInList());
              }
            } else {
              addTransitionLabel(t.integer(Long.MIN_VALUE, Long.MAX_VALUE));
            }
            break;
          }
          case "vars": {
            t.consume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addVars((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
                } while (t.nextInList());
              }
            } else {
              addVars((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            }
            break;
          }
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
            throw ProtoWire.unknownField(t, "operations_research.sat.AutomatonConstraintProto", name);
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

    /** Sets {@code starting_state}. */
    public Builder setStartingState(long value) {
      startingState_ = value;
      return this;
    }

    /** Resets {@code starting_state} to its default value. */
    public Builder clearStartingState() {
      startingState_ = 0L;
      return this;
    }

    /** Replaces the element {@code index} of {@code final_states}. */
    public Builder setFinalStates(int index, long value) {
      if (finalStates_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      finalStates_.set(index, value);
      return this;
    }

    /** Appends a value to {@code final_states}. */
    public Builder addFinalStates(long value) {
      if (finalStates_ == null) {
        finalStates_ = new java.util.ArrayList<>();
      }
      finalStates_.add(value);
      return this;
    }

    /** Appends all values to {@code final_states}. */
    public Builder addAllFinalStates(Iterable<? extends Long> values) {
      if (finalStates_ == null) {
        finalStates_ = new java.util.ArrayList<>();
      }
      for (Long v : values) {
        finalStates_.add(v);
      }
      return this;
    }

    /** Removes all values of {@code final_states}. */
    public Builder clearFinalStates() {
      finalStates_ = null;
      return this;
    }

    /** Replaces the element {@code index} of {@code transition_tail}. */
    public Builder setTransitionTail(int index, long value) {
      if (transitionTail_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      transitionTail_.set(index, value);
      return this;
    }

    /** Appends a value to {@code transition_tail}. */
    public Builder addTransitionTail(long value) {
      if (transitionTail_ == null) {
        transitionTail_ = new java.util.ArrayList<>();
      }
      transitionTail_.add(value);
      return this;
    }

    /** Appends all values to {@code transition_tail}. */
    public Builder addAllTransitionTail(Iterable<? extends Long> values) {
      if (transitionTail_ == null) {
        transitionTail_ = new java.util.ArrayList<>();
      }
      for (Long v : values) {
        transitionTail_.add(v);
      }
      return this;
    }

    /** Removes all values of {@code transition_tail}. */
    public Builder clearTransitionTail() {
      transitionTail_ = null;
      return this;
    }

    /** Replaces the element {@code index} of {@code transition_head}. */
    public Builder setTransitionHead(int index, long value) {
      if (transitionHead_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      transitionHead_.set(index, value);
      return this;
    }

    /** Appends a value to {@code transition_head}. */
    public Builder addTransitionHead(long value) {
      if (transitionHead_ == null) {
        transitionHead_ = new java.util.ArrayList<>();
      }
      transitionHead_.add(value);
      return this;
    }

    /** Appends all values to {@code transition_head}. */
    public Builder addAllTransitionHead(Iterable<? extends Long> values) {
      if (transitionHead_ == null) {
        transitionHead_ = new java.util.ArrayList<>();
      }
      for (Long v : values) {
        transitionHead_.add(v);
      }
      return this;
    }

    /** Removes all values of {@code transition_head}. */
    public Builder clearTransitionHead() {
      transitionHead_ = null;
      return this;
    }

    /** Replaces the element {@code index} of {@code transition_label}. */
    public Builder setTransitionLabel(int index, long value) {
      if (transitionLabel_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      transitionLabel_.set(index, value);
      return this;
    }

    /** Appends a value to {@code transition_label}. */
    public Builder addTransitionLabel(long value) {
      if (transitionLabel_ == null) {
        transitionLabel_ = new java.util.ArrayList<>();
      }
      transitionLabel_.add(value);
      return this;
    }

    /** Appends all values to {@code transition_label}. */
    public Builder addAllTransitionLabel(Iterable<? extends Long> values) {
      if (transitionLabel_ == null) {
        transitionLabel_ = new java.util.ArrayList<>();
      }
      for (Long v : values) {
        transitionLabel_.add(v);
      }
      return this;
    }

    /** Removes all values of {@code transition_label}. */
    public Builder clearTransitionLabel() {
      transitionLabel_ = null;
      return this;
    }

    /** Replaces the element {@code index} of {@code vars}. */
    public Builder setVars(int index, int value) {
      if (vars_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      vars_.set(index, value);
      return this;
    }

    /** Appends a value to {@code vars}. */
    public Builder addVars(int value) {
      if (vars_ == null) {
        vars_ = new java.util.ArrayList<>();
      }
      vars_.add(value);
      return this;
    }

    /** Appends all values to {@code vars}. */
    public Builder addAllVars(Iterable<? extends Integer> values) {
      if (vars_ == null) {
        vars_ = new java.util.ArrayList<>();
      }
      for (Integer v : values) {
        vars_.add(v);
      }
      return this;
    }

    /** Removes all values of {@code vars}. */
    public Builder clearVars() {
      vars_ = null;
      return this;
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
