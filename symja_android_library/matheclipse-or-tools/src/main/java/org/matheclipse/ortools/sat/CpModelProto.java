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
 * A constraint programming problem.
 * </pre>
 *
 * <p>
 * Plain Java replacement of the protobuf message {@code operations_research.sat.CpModelProto} of OR-Tools (generated from ortools/sat/cp_model.proto, no protobuf runtime needed). Instances are immutable; use {@link Builder} to create or modify them.
 */
public class CpModelProto implements ProtoMessage {
  private static final CpModelProto DEFAULT_INSTANCE = new CpModelProto();

  String name_ = "";
  java.util.ArrayList<IntegerVariableProto> variables_;
  java.util.ArrayList<ConstraintProto> constraints_;
  CpObjectiveProto objective_;
  FloatObjectiveProto floatingPointObjective_;
  java.util.ArrayList<DecisionStrategyProto> searchStrategy_;
  PartialVariableAssignment solutionHint_;
  java.util.ArrayList<Integer> assumptions_;
  SymmetryProto symmetry_;

  private CpModelProto() {}

  /** Returns the immutable default instance (all fields unset). */
  public static CpModelProto getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  /** Returns the default instance. */
  public CpModelProto getDefaultInstanceForType() {
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
   * The associated Protos should be referred by their index in these fields.
   * </pre>
   */
  public java.util.List<IntegerVariableProto> getVariablesList() {
    return variables_ == null ? java.util.Collections.<IntegerVariableProto>emptyList() : java.util.Collections.unmodifiableList(variables_);
  }

  /** Returns the number of elements of {@code variables}. */
  public int getVariablesCount() {
    return variables_ == null ? 0 : variables_.size();
  }

  /** Returns the element {@code index} of {@code variables}. */
  public IntegerVariableProto getVariables(int index) {
    if (variables_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return variables_.get(index);
  }

  /** Returns the elements of {@code constraints}. */
  public java.util.List<ConstraintProto> getConstraintsList() {
    return constraints_ == null ? java.util.Collections.<ConstraintProto>emptyList() : java.util.Collections.unmodifiableList(constraints_);
  }

  /** Returns the number of elements of {@code constraints}. */
  public int getConstraintsCount() {
    return constraints_ == null ? 0 : constraints_.size();
  }

  /** Returns the element {@code index} of {@code constraints}. */
  public ConstraintProto getConstraints(int index) {
    if (constraints_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return constraints_.get(index);
  }

  /** Returns true if {@code objective} is set. */
  public boolean hasObjective() {
    return objective_ != null;
  }

  /**
   * <pre>
   * The objective to minimize. Can be empty for pure decision problems.
   * </pre>
   */
  public CpObjectiveProto getObjective() {
    return objective_ == null ? CpObjectiveProto.getDefaultInstance() : objective_;
  }

  /** Returns true if {@code floating_point_objective} is set. */
  public boolean hasFloatingPointObjective() {
    return floatingPointObjective_ != null;
  }

  /**
   * <pre>
   * Advanced usage.
   * It is invalid to have both an objective and a floating point objective.
   *
   * The objective of the model, in floating point format. The solver will
   * automatically scale this to integer during expansion and thus convert it to
   * a normal CpObjectiveProto. See the mip* parameters to control how this is
   * scaled. In most situations the precision will be good enough, but you can
   * see the logs to see what are the precision guaranteed when this is
   * converted to a fixed point representation.
   *
   * Note that even if the precision is bad, the returned objective_value and
   * best_objective_bound will be computed correctly. So at the end of the solve
   * you can check the gap if you only want precise optimal.
   * </pre>
   */
  public FloatObjectiveProto getFloatingPointObjective() {
    return floatingPointObjective_ == null ? FloatObjectiveProto.getDefaultInstance() : floatingPointObjective_;
  }

  /**
   * <pre>
   * Defines the strategy that the solver should follow when the
   * search_branching parameter is set to FIXED_SEARCH. Note that this strategy
   * is also used as a heuristic when we are not in fixed search.
   *
   * Advanced Usage: if not all variables appear and the parameter
   * "instantiate_all_variables" is set to false, then the solver will not try
   * to instantiate the variables that do not appear. Thus, at the end of the
   * search, not all variables may be fixed. Currently, we will set them to
   * their lower bound in the solution.
   * </pre>
   */
  public java.util.List<DecisionStrategyProto> getSearchStrategyList() {
    return searchStrategy_ == null ? java.util.Collections.<DecisionStrategyProto>emptyList() : java.util.Collections.unmodifiableList(searchStrategy_);
  }

  /** Returns the number of elements of {@code search_strategy}. */
  public int getSearchStrategyCount() {
    return searchStrategy_ == null ? 0 : searchStrategy_.size();
  }

  /** Returns the element {@code index} of {@code search_strategy}. */
  public DecisionStrategyProto getSearchStrategy(int index) {
    if (searchStrategy_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return searchStrategy_.get(index);
  }

  /** Returns true if {@code solution_hint} is set. */
  public boolean hasSolutionHint() {
    return solutionHint_ != null;
  }

  /**
   * <pre>
   * Solution hint.
   *
   * If a feasible or almost-feasible solution to the problem is already known,
   * it may be helpful to pass it to the solver so that it can be used. The
   * solver will try to use this information to create its initial feasible
   * solution.
   *
   * Note that it may not always be faster to give a hint like this to the
   * solver. There is also no guarantee that the solver will use this hint or
   * try to return a solution "close" to this assignment in case of multiple
   * optimal solutions.
   * </pre>
   */
  public PartialVariableAssignment getSolutionHint() {
    return solutionHint_ == null ? PartialVariableAssignment.getDefaultInstance() : solutionHint_;
  }

  /**
   * <pre>
   * A list of literals. The model will be solved assuming all these literals
   * are true. Compared to just fixing the domain of these literals, using this
   * mechanism is slower but allows in case the model is INFEASIBLE to get a
   * potentially small subset of them that can be used to explain the
   * infeasibility.
   *
   * Think (IIS), except when you are only concerned by the provided
   * assumptions. This is powerful as it allows grouping a set of logically
   * related constraints under only one enforcement literal which can
   * potentially give you a good and interpretable explanation for
   * infeasibility.
   *
   * Such infeasibility explanation will be available in the
   * sufficient_assumptions_for_infeasibility response field.
   * </pre>
   */
  public java.util.List<Integer> getAssumptionsList() {
    return assumptions_ == null ? java.util.Collections.<Integer>emptyList() : java.util.Collections.unmodifiableList(assumptions_);
  }

  /** Returns the number of elements of {@code assumptions}. */
  public int getAssumptionsCount() {
    return assumptions_ == null ? 0 : assumptions_.size();
  }

  /** Returns the element {@code index} of {@code assumptions}. */
  public int getAssumptions(int index) {
    if (assumptions_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return assumptions_.get(index);
  }

  /** Returns true if {@code symmetry} is set. */
  public boolean hasSymmetry() {
    return symmetry_ != null;
  }

  /**
   * <pre>
   * For now, this is not meant to be filled by a client writing a model, but
   * by our preprocessing step.
   *
   * Information about the symmetries of the feasible solution space.
   * These usually leave the objective invariant.
   * </pre>
   */
  public SymmetryProto getSymmetry() {
    return symmetry_ == null ? SymmetryProto.getDefaultInstance() : symmetry_;
  }

  void copyFieldsFrom(CpModelProto src) {
    name_ = src.name_;
    variables_ = buildListIntegerVariableProto(src.variables_);
    constraints_ = buildListConstraintProto(src.constraints_);
    objective_ = src.objective_ == null ? null : src.objective_.immutableCopy();
    floatingPointObjective_ = src.floatingPointObjective_ == null ? null : src.floatingPointObjective_.immutableCopy();
    searchStrategy_ = buildListDecisionStrategyProto(src.searchStrategy_);
    solutionHint_ = src.solutionHint_ == null ? null : src.solutionHint_.immutableCopy();
    assumptions_ = src.assumptions_ == null || src.assumptions_.isEmpty() ? null : new java.util.ArrayList<>(src.assumptions_);
    symmetry_ = src.symmetry_ == null ? null : src.symmetry_.immutableCopy();
  }

  /** Returns this message if it is immutable, otherwise a built copy. */
  CpModelProto immutableCopy() {
    if (this instanceof Builder) {
      CpModelProto r = new CpModelProto();
      r.copyFieldsFrom(this);
      return r;
    }
    return this;
  }

  private static java.util.ArrayList<IntegerVariableProto> buildListIntegerVariableProto(java.util.List<IntegerVariableProto> src) {
    if (src == null || src.isEmpty()) {
      return null;
    }
    java.util.ArrayList<IntegerVariableProto> r = new java.util.ArrayList<>(src.size());
    for (IntegerVariableProto e : src) {
      r.add(e.immutableCopy());
    }
    return r;
  }

  private static java.util.ArrayList<ConstraintProto> buildListConstraintProto(java.util.List<ConstraintProto> src) {
    if (src == null || src.isEmpty()) {
      return null;
    }
    java.util.ArrayList<ConstraintProto> r = new java.util.ArrayList<>(src.size());
    for (ConstraintProto e : src) {
      r.add(e.immutableCopy());
    }
    return r;
  }

  private static java.util.ArrayList<DecisionStrategyProto> buildListDecisionStrategyProto(java.util.List<DecisionStrategyProto> src) {
    if (src == null || src.isEmpty()) {
      return null;
    }
    java.util.ArrayList<DecisionStrategyProto> r = new java.util.ArrayList<>(src.size());
    for (DecisionStrategyProto e : src) {
      r.add(e.immutableCopy());
    }
    return r;
  }

  @Override
  public void printTo(ProtoPrinter printer) {
    if (!name_.isEmpty()) {
      printer.printScalar("name", name_);
    }
    if (variables_ != null) {
      for (IntegerVariableProto v : variables_) {
        printer.printMessage("variables", v);
      }
    }
    if (constraints_ != null) {
      for (ConstraintProto v : constraints_) {
        printer.printMessage("constraints", v);
      }
    }
    if (objective_ != null) {
      printer.printMessage("objective", objective_);
    }
    if (searchStrategy_ != null) {
      for (DecisionStrategyProto v : searchStrategy_) {
        printer.printMessage("search_strategy", v);
      }
    }
    if (solutionHint_ != null) {
      printer.printMessage("solution_hint", solutionHint_);
    }
    if (assumptions_ != null) {
      for (Integer v : assumptions_) {
        printer.printScalar("assumptions", v);
      }
    }
    if (symmetry_ != null) {
      printer.printMessage("symmetry", symmetry_);
    }
    if (floatingPointObjective_ != null) {
      printer.printMessage("floating_point_objective", floatingPointObjective_);
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
    if (!(obj instanceof CpModelProto)) {
      return false;
    }
    CpModelProto other = (CpModelProto) obj;
    return name_.equals(other.name_)
        && getVariablesList().equals(other.getVariablesList())
        && getConstraintsList().equals(other.getConstraintsList())
        && java.util.Objects.equals(objective_, other.objective_)
        && java.util.Objects.equals(floatingPointObjective_, other.floatingPointObjective_)
        && getSearchStrategyList().equals(other.getSearchStrategyList())
        && java.util.Objects.equals(solutionHint_, other.solutionHint_)
        && getAssumptionsList().equals(other.getAssumptionsList())
        && java.util.Objects.equals(symmetry_, other.symmetry_);
  }

  @Override
  public int hashCode() {
    int h = 586;
    h = 31 * h + name_.hashCode();
    h = 31 * h + getVariablesList().hashCode();
    h = 31 * h + getConstraintsList().hashCode();
    h = 31 * h + java.util.Objects.hashCode(objective_);
    h = 31 * h + java.util.Objects.hashCode(floatingPointObjective_);
    h = 31 * h + getSearchStrategyList().hashCode();
    h = 31 * h + java.util.Objects.hashCode(solutionHint_);
    h = 31 * h + getAssumptionsList().hashCode();
    h = 31 * h + java.util.Objects.hashCode(symmetry_);
    return h;
  }

  /** Appends the fields of this message in the protobuf binary format. */
  void writeTo(ProtoWire.Writer w) {
    if (!name_.isEmpty()) {
      w.string(1, name_);
    }
    if (variables_ != null) {
      for (IntegerVariableProto v : variables_) {
        ProtoWire.Writer sub = new ProtoWire.Writer();
        v.writeTo(sub);
        w.bytes(2, sub);
      }
    }
    if (constraints_ != null) {
      for (ConstraintProto v : constraints_) {
        ProtoWire.Writer sub = new ProtoWire.Writer();
        v.writeTo(sub);
        w.bytes(3, sub);
      }
    }
    if (objective_ != null) {
      ProtoWire.Writer sub = new ProtoWire.Writer();
      objective_.writeTo(sub);
      w.bytes(4, sub);
    }
    if (searchStrategy_ != null) {
      for (DecisionStrategyProto v : searchStrategy_) {
        ProtoWire.Writer sub = new ProtoWire.Writer();
        v.writeTo(sub);
        w.bytes(5, sub);
      }
    }
    if (solutionHint_ != null) {
      ProtoWire.Writer sub = new ProtoWire.Writer();
      solutionHint_.writeTo(sub);
      w.bytes(6, sub);
    }
    if (assumptions_ != null && !assumptions_.isEmpty()) {
      w.packedInt32(7, assumptions_);
    }
    if (symmetry_ != null) {
      ProtoWire.Writer sub = new ProtoWire.Writer();
      symmetry_.writeTo(sub);
      w.bytes(8, sub);
    }
    if (floatingPointObjective_ != null) {
      ProtoWire.Writer sub = new ProtoWire.Writer();
      floatingPointObjective_.writeTo(sub);
      w.bytes(9, sub);
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
  public static CpModelProto parseFrom(byte[] data) {
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
  public static Builder newBuilder(CpModelProto prototype) {
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
   * Builder of {@link CpModelProto}. A builder is also a (mutable) {@code CpModelProto}, so all getters are
   * available on it.
   */
  public static final class Builder extends CpModelProto implements Cloneable {
    Builder() {}

    /** Returns an immutable copy of the current content. */
    public CpModelProto build() {
      CpModelProto r = new CpModelProto();
      r.copyFieldsFrom(this);
      return r;
    }

    /** Same as {@link #build()}. */
    public CpModelProto buildPartial() {
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
    public Builder mergeFrom(CpModelProto other) {
      if (other == DEFAULT_INSTANCE) {
        return this;
      }
      if (!other.name_.isEmpty()) {
        setName(other.name_);
      }
      if (other.variables_ != null) {
        addAllVariables(other.variables_);
      }
      if (other.constraints_ != null) {
        addAllConstraints(other.constraints_);
      }
      if (other.objective_ != null) {
        mergeObjective(other.objective_);
      }
      if (other.floatingPointObjective_ != null) {
        mergeFloatingPointObjective(other.floatingPointObjective_);
      }
      if (other.searchStrategy_ != null) {
        addAllSearchStrategy(other.searchStrategy_);
      }
      if (other.solutionHint_ != null) {
        mergeSolutionHint(other.solutionHint_);
      }
      if (other.assumptions_ != null) {
        addAllAssumptions(other.assumptions_);
      }
      if (other.symmetry_ != null) {
        mergeSymmetry(other.symmetry_);
      }
      return this;
    }

    /** Replaces the content of this builder by a deep copy of {@code other}. */
    public void copyFrom(CpModelProto other) {
      copyFieldsFrom(other);
    }

    /**
     * Returns a modifiable view of {@code variables}: elements added or set through the list are
     * stored in this builder (messages as immutable copies). The view is detached by
     * {@code clearVariables()} and {@code clear()}.
     */
    @Override
    public java.util.List<IntegerVariableProto> getVariablesList() {
      if (variables_ == null) {
        variables_ = new java.util.ArrayList<>();
      }
      return new ProtoWire.ListView<>(variables_, IntegerVariableProto::immutableCopy);
    }

    /**
     * Returns a modifiable view of {@code constraints}: elements added or set through the list are
     * stored in this builder (messages as immutable copies). The view is detached by
     * {@code clearConstraints()} and {@code clear()}.
     */
    @Override
    public java.util.List<ConstraintProto> getConstraintsList() {
      if (constraints_ == null) {
        constraints_ = new java.util.ArrayList<>();
      }
      return new ProtoWire.ListView<>(constraints_, ConstraintProto::immutableCopy);
    }

    /**
     * Returns a modifiable view of {@code search_strategy}: elements added or set through the list are
     * stored in this builder (messages as immutable copies). The view is detached by
     * {@code clearSearchStrategy()} and {@code clear()}.
     */
    @Override
    public java.util.List<DecisionStrategyProto> getSearchStrategyList() {
      if (searchStrategy_ == null) {
        searchStrategy_ = new java.util.ArrayList<>();
      }
      return new ProtoWire.ListView<>(searchStrategy_, DecisionStrategyProto::immutableCopy);
    }

    /**
     * Returns a modifiable view of {@code assumptions}: elements added or set through the list are
     * stored in this builder (messages as immutable copies). The view is detached by
     * {@code clearAssumptions()} and {@code clear()}.
     */
    @Override
    public java.util.List<Integer> getAssumptionsList() {
      if (assumptions_ == null) {
        assumptions_ = new java.util.ArrayList<>();
      }
      return new ProtoWire.ListView<>(assumptions_, null);
    }

    void mergeWire(ProtoWire.Reader r) {
      while (!r.atEnd()) {
        int tag = r.tag();
        switch (tag) {
          case 10:
            setName(r.string(true));
            break;
          case 18:
            addVariablesBuilder().mergeWire(r.message());
            break;
          case 26:
            addConstraintsBuilder().mergeWire(r.message());
            break;
          case 34:
            getObjectiveBuilder().mergeWire(r.message());
            break;
          case 42:
            addSearchStrategyBuilder().mergeWire(r.message());
            break;
          case 50:
            getSolutionHintBuilder().mergeWire(r.message());
            break;
          case 58: {
            ProtoWire.Reader p = r.packed();
            while (!p.atEnd()) {
              addAssumptions((int) p.varint());
            }
            break;
          }
          case 56:
            addAssumptions((int) r.varint());
            break;
          case 66:
            getSymmetryBuilder().mergeWire(r.message());
            break;
          case 74:
            getFloatingPointObjectiveBuilder().mergeWire(r.message());
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
          case "variables": {
            t.tryConsume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addVariablesBuilder().mergeText(t, t.openMessage(), strict);
                } while (t.nextInList());
              }
            } else {
              addVariablesBuilder().mergeText(t, t.openMessage(), strict);
            }
            break;
          }
          case "constraints": {
            t.tryConsume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addConstraintsBuilder().mergeText(t, t.openMessage(), strict);
                } while (t.nextInList());
              }
            } else {
              addConstraintsBuilder().mergeText(t, t.openMessage(), strict);
            }
            break;
          }
          case "objective": {
            if (strict && objective_ != null) {
              throw ProtoWire.duplicate(t, "objective");
            }
            t.tryConsume(":");
            getObjectiveBuilder().mergeText(t, t.openMessage(), strict);
            break;
          }
          case "floating_point_objective": {
            if (strict && floatingPointObjective_ != null) {
              throw ProtoWire.duplicate(t, "floating_point_objective");
            }
            t.tryConsume(":");
            getFloatingPointObjectiveBuilder().mergeText(t, t.openMessage(), strict);
            break;
          }
          case "search_strategy": {
            t.tryConsume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addSearchStrategyBuilder().mergeText(t, t.openMessage(), strict);
                } while (t.nextInList());
              }
            } else {
              addSearchStrategyBuilder().mergeText(t, t.openMessage(), strict);
            }
            break;
          }
          case "solution_hint": {
            if (strict && solutionHint_ != null) {
              throw ProtoWire.duplicate(t, "solution_hint");
            }
            t.tryConsume(":");
            getSolutionHintBuilder().mergeText(t, t.openMessage(), strict);
            break;
          }
          case "assumptions": {
            t.consume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addAssumptions((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
                } while (t.nextInList());
              }
            } else {
              addAssumptions((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            }
            break;
          }
          case "symmetry": {
            if (strict && symmetry_ != null) {
              throw ProtoWire.duplicate(t, "symmetry");
            }
            t.tryConsume(":");
            getSymmetryBuilder().mergeText(t, t.openMessage(), strict);
            break;
          }
          default:
            throw ProtoWire.unknownField(t, "operations_research.sat.CpModelProto", name);
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

    /** Replaces the element {@code index} of {@code variables}. */
    public Builder setVariables(int index, IntegerVariableProto value) {
      if (variables_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      variables_.set(index, java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Appends a value to {@code variables}. */
    public Builder addVariables(IntegerVariableProto value) {
      if (variables_ == null) {
        variables_ = new java.util.ArrayList<>();
      }
      variables_.add(java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Appends all values to {@code variables}. */
    public Builder addAllVariables(Iterable<? extends IntegerVariableProto> values) {
      if (variables_ == null) {
        variables_ = new java.util.ArrayList<>();
      }
      for (IntegerVariableProto v : values) {
        variables_.add(java.util.Objects.requireNonNull(v).immutableCopy());
      }
      return this;
    }

    /** Removes all values of {@code variables}. */
    public Builder clearVariables() {
      variables_ = null;
      return this;
    }

    /** Replaces the element {@code index} of {@code variables} by the content of a builder. */
    public Builder setVariables(int index, IntegerVariableProto.Builder builderForValue) {
      return setVariables(index, builderForValue.build());
    }

    /** Appends the content of a builder to {@code variables}. */
    public Builder addVariables(IntegerVariableProto.Builder builderForValue) {
      return addVariables(builderForValue.build());
    }

    /** Inserts a value into {@code variables}. */
    public Builder addVariables(int index, IntegerVariableProto value) {
      if (variables_ == null) {
        variables_ = new java.util.ArrayList<>();
      }
      variables_.add(index, java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Removes the element {@code index} of {@code variables}. */
    public Builder removeVariables(int index) {
      if (variables_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      variables_.remove(index);
      return this;
    }

    /** Returns a builder for the element {@code index} of {@code variables}; changes to it are reflected in this builder. */
    public IntegerVariableProto.Builder getVariablesBuilder(int index) {
      if (variables_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      IntegerVariableProto e = variables_.get(index);
      if (e instanceof IntegerVariableProto.Builder) {
        return (IntegerVariableProto.Builder) e;
      }
      IntegerVariableProto.Builder b = IntegerVariableProto.newBuilder(e);
      variables_.set(index, b);
      return b;
    }

    /** Appends a new empty element to {@code variables} and returns its builder. */
    public IntegerVariableProto.Builder addVariablesBuilder() {
      if (variables_ == null) {
        variables_ = new java.util.ArrayList<>();
      }
      IntegerVariableProto.Builder b = IntegerVariableProto.newBuilder();
      variables_.add(b);
      return b;
    }

    /** Returns the builders of all elements of {@code variables}. */
    public java.util.List<IntegerVariableProto.Builder> getVariablesBuilderList() {
      java.util.List<IntegerVariableProto.Builder> r = new java.util.ArrayList<>();
      for (int k = 0; k < getVariablesCount(); k++) {
        r.add(getVariablesBuilder(k));
      }
      return r;
    }

    /** Replaces the element {@code index} of {@code constraints}. */
    public Builder setConstraints(int index, ConstraintProto value) {
      if (constraints_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      constraints_.set(index, java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Appends a value to {@code constraints}. */
    public Builder addConstraints(ConstraintProto value) {
      if (constraints_ == null) {
        constraints_ = new java.util.ArrayList<>();
      }
      constraints_.add(java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Appends all values to {@code constraints}. */
    public Builder addAllConstraints(Iterable<? extends ConstraintProto> values) {
      if (constraints_ == null) {
        constraints_ = new java.util.ArrayList<>();
      }
      for (ConstraintProto v : values) {
        constraints_.add(java.util.Objects.requireNonNull(v).immutableCopy());
      }
      return this;
    }

    /** Removes all values of {@code constraints}. */
    public Builder clearConstraints() {
      constraints_ = null;
      return this;
    }

    /** Replaces the element {@code index} of {@code constraints} by the content of a builder. */
    public Builder setConstraints(int index, ConstraintProto.Builder builderForValue) {
      return setConstraints(index, builderForValue.build());
    }

    /** Appends the content of a builder to {@code constraints}. */
    public Builder addConstraints(ConstraintProto.Builder builderForValue) {
      return addConstraints(builderForValue.build());
    }

    /** Inserts a value into {@code constraints}. */
    public Builder addConstraints(int index, ConstraintProto value) {
      if (constraints_ == null) {
        constraints_ = new java.util.ArrayList<>();
      }
      constraints_.add(index, java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Removes the element {@code index} of {@code constraints}. */
    public Builder removeConstraints(int index) {
      if (constraints_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      constraints_.remove(index);
      return this;
    }

    /** Returns a builder for the element {@code index} of {@code constraints}; changes to it are reflected in this builder. */
    public ConstraintProto.Builder getConstraintsBuilder(int index) {
      if (constraints_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      ConstraintProto e = constraints_.get(index);
      if (e instanceof ConstraintProto.Builder) {
        return (ConstraintProto.Builder) e;
      }
      ConstraintProto.Builder b = ConstraintProto.newBuilder(e);
      constraints_.set(index, b);
      return b;
    }

    /** Appends a new empty element to {@code constraints} and returns its builder. */
    public ConstraintProto.Builder addConstraintsBuilder() {
      if (constraints_ == null) {
        constraints_ = new java.util.ArrayList<>();
      }
      ConstraintProto.Builder b = ConstraintProto.newBuilder();
      constraints_.add(b);
      return b;
    }

    /** Returns the builders of all elements of {@code constraints}. */
    public java.util.List<ConstraintProto.Builder> getConstraintsBuilderList() {
      java.util.List<ConstraintProto.Builder> r = new java.util.ArrayList<>();
      for (int k = 0; k < getConstraintsCount(); k++) {
        r.add(getConstraintsBuilder(k));
      }
      return r;
    }

    /** Sets {@code objective}. */
    public Builder setObjective(CpObjectiveProto value) {
      if (value == null) {
        throw new NullPointerException();
      }
      objective_ = value.immutableCopy();
      return this;
    }

    /** Sets {@code objective} to the content of a builder. */
    public Builder setObjective(CpObjectiveProto.Builder builderForValue) {
      return setObjective(builderForValue.build());
    }

    /** Merges {@code value} into {@code objective}. */
    public Builder mergeObjective(CpObjectiveProto value) {
      if (objective_ != null) {
        getObjectiveBuilder().mergeFrom(value);
      } else {
        setObjective(value.immutableCopy());
      }
      return this;
    }

    /** Clears {@code objective}. */
    public Builder clearObjective() {
      if (true) {
        objective_ = null;
      }
      return this;
    }

    /** Returns a builder for {@code objective}, setting it if needed; changes to it are reflected in this builder. */
    public CpObjectiveProto.Builder getObjectiveBuilder() {
      if (objective_ != null && objective_ instanceof CpObjectiveProto.Builder) {
        return (CpObjectiveProto.Builder) objective_;
      }
      CpObjectiveProto.Builder b = objective_ != null ? CpObjectiveProto.newBuilder(objective_) : CpObjectiveProto.newBuilder();
      setObjectiveRaw(b);
      return b;
    }

    private void setObjectiveRaw(CpObjectiveProto.Builder b) {
      objective_ = b;
    }

    /** Sets {@code floating_point_objective}. */
    public Builder setFloatingPointObjective(FloatObjectiveProto value) {
      if (value == null) {
        throw new NullPointerException();
      }
      floatingPointObjective_ = value.immutableCopy();
      return this;
    }

    /** Sets {@code floating_point_objective} to the content of a builder. */
    public Builder setFloatingPointObjective(FloatObjectiveProto.Builder builderForValue) {
      return setFloatingPointObjective(builderForValue.build());
    }

    /** Merges {@code value} into {@code floating_point_objective}. */
    public Builder mergeFloatingPointObjective(FloatObjectiveProto value) {
      if (floatingPointObjective_ != null) {
        getFloatingPointObjectiveBuilder().mergeFrom(value);
      } else {
        setFloatingPointObjective(value.immutableCopy());
      }
      return this;
    }

    /** Clears {@code floating_point_objective}. */
    public Builder clearFloatingPointObjective() {
      if (true) {
        floatingPointObjective_ = null;
      }
      return this;
    }

    /** Returns a builder for {@code floating_point_objective}, setting it if needed; changes to it are reflected in this builder. */
    public FloatObjectiveProto.Builder getFloatingPointObjectiveBuilder() {
      if (floatingPointObjective_ != null && floatingPointObjective_ instanceof FloatObjectiveProto.Builder) {
        return (FloatObjectiveProto.Builder) floatingPointObjective_;
      }
      FloatObjectiveProto.Builder b = floatingPointObjective_ != null ? FloatObjectiveProto.newBuilder(floatingPointObjective_) : FloatObjectiveProto.newBuilder();
      setFloatingPointObjectiveRaw(b);
      return b;
    }

    private void setFloatingPointObjectiveRaw(FloatObjectiveProto.Builder b) {
      floatingPointObjective_ = b;
    }

    /** Replaces the element {@code index} of {@code search_strategy}. */
    public Builder setSearchStrategy(int index, DecisionStrategyProto value) {
      if (searchStrategy_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      searchStrategy_.set(index, java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Appends a value to {@code search_strategy}. */
    public Builder addSearchStrategy(DecisionStrategyProto value) {
      if (searchStrategy_ == null) {
        searchStrategy_ = new java.util.ArrayList<>();
      }
      searchStrategy_.add(java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Appends all values to {@code search_strategy}. */
    public Builder addAllSearchStrategy(Iterable<? extends DecisionStrategyProto> values) {
      if (searchStrategy_ == null) {
        searchStrategy_ = new java.util.ArrayList<>();
      }
      for (DecisionStrategyProto v : values) {
        searchStrategy_.add(java.util.Objects.requireNonNull(v).immutableCopy());
      }
      return this;
    }

    /** Removes all values of {@code search_strategy}. */
    public Builder clearSearchStrategy() {
      searchStrategy_ = null;
      return this;
    }

    /** Replaces the element {@code index} of {@code search_strategy} by the content of a builder. */
    public Builder setSearchStrategy(int index, DecisionStrategyProto.Builder builderForValue) {
      return setSearchStrategy(index, builderForValue.build());
    }

    /** Appends the content of a builder to {@code search_strategy}. */
    public Builder addSearchStrategy(DecisionStrategyProto.Builder builderForValue) {
      return addSearchStrategy(builderForValue.build());
    }

    /** Inserts a value into {@code search_strategy}. */
    public Builder addSearchStrategy(int index, DecisionStrategyProto value) {
      if (searchStrategy_ == null) {
        searchStrategy_ = new java.util.ArrayList<>();
      }
      searchStrategy_.add(index, java.util.Objects.requireNonNull(value).immutableCopy());
      return this;
    }

    /** Removes the element {@code index} of {@code search_strategy}. */
    public Builder removeSearchStrategy(int index) {
      if (searchStrategy_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      searchStrategy_.remove(index);
      return this;
    }

    /** Returns a builder for the element {@code index} of {@code search_strategy}; changes to it are reflected in this builder. */
    public DecisionStrategyProto.Builder getSearchStrategyBuilder(int index) {
      if (searchStrategy_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      DecisionStrategyProto e = searchStrategy_.get(index);
      if (e instanceof DecisionStrategyProto.Builder) {
        return (DecisionStrategyProto.Builder) e;
      }
      DecisionStrategyProto.Builder b = DecisionStrategyProto.newBuilder(e);
      searchStrategy_.set(index, b);
      return b;
    }

    /** Appends a new empty element to {@code search_strategy} and returns its builder. */
    public DecisionStrategyProto.Builder addSearchStrategyBuilder() {
      if (searchStrategy_ == null) {
        searchStrategy_ = new java.util.ArrayList<>();
      }
      DecisionStrategyProto.Builder b = DecisionStrategyProto.newBuilder();
      searchStrategy_.add(b);
      return b;
    }

    /** Returns the builders of all elements of {@code search_strategy}. */
    public java.util.List<DecisionStrategyProto.Builder> getSearchStrategyBuilderList() {
      java.util.List<DecisionStrategyProto.Builder> r = new java.util.ArrayList<>();
      for (int k = 0; k < getSearchStrategyCount(); k++) {
        r.add(getSearchStrategyBuilder(k));
      }
      return r;
    }

    /** Sets {@code solution_hint}. */
    public Builder setSolutionHint(PartialVariableAssignment value) {
      if (value == null) {
        throw new NullPointerException();
      }
      solutionHint_ = value.immutableCopy();
      return this;
    }

    /** Sets {@code solution_hint} to the content of a builder. */
    public Builder setSolutionHint(PartialVariableAssignment.Builder builderForValue) {
      return setSolutionHint(builderForValue.build());
    }

    /** Merges {@code value} into {@code solution_hint}. */
    public Builder mergeSolutionHint(PartialVariableAssignment value) {
      if (solutionHint_ != null) {
        getSolutionHintBuilder().mergeFrom(value);
      } else {
        setSolutionHint(value.immutableCopy());
      }
      return this;
    }

    /** Clears {@code solution_hint}. */
    public Builder clearSolutionHint() {
      if (true) {
        solutionHint_ = null;
      }
      return this;
    }

    /** Returns a builder for {@code solution_hint}, setting it if needed; changes to it are reflected in this builder. */
    public PartialVariableAssignment.Builder getSolutionHintBuilder() {
      if (solutionHint_ != null && solutionHint_ instanceof PartialVariableAssignment.Builder) {
        return (PartialVariableAssignment.Builder) solutionHint_;
      }
      PartialVariableAssignment.Builder b = solutionHint_ != null ? PartialVariableAssignment.newBuilder(solutionHint_) : PartialVariableAssignment.newBuilder();
      setSolutionHintRaw(b);
      return b;
    }

    private void setSolutionHintRaw(PartialVariableAssignment.Builder b) {
      solutionHint_ = b;
    }

    /** Replaces the element {@code index} of {@code assumptions}. */
    public Builder setAssumptions(int index, int value) {
      if (assumptions_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      assumptions_.set(index, value);
      return this;
    }

    /** Appends a value to {@code assumptions}. */
    public Builder addAssumptions(int value) {
      if (assumptions_ == null) {
        assumptions_ = new java.util.ArrayList<>();
      }
      assumptions_.add(value);
      return this;
    }

    /** Appends all values to {@code assumptions}. */
    public Builder addAllAssumptions(Iterable<? extends Integer> values) {
      if (assumptions_ == null) {
        assumptions_ = new java.util.ArrayList<>();
      }
      for (Integer v : values) {
        assumptions_.add(v);
      }
      return this;
    }

    /** Removes all values of {@code assumptions}. */
    public Builder clearAssumptions() {
      assumptions_ = null;
      return this;
    }

    /** Sets {@code symmetry}. */
    public Builder setSymmetry(SymmetryProto value) {
      if (value == null) {
        throw new NullPointerException();
      }
      symmetry_ = value.immutableCopy();
      return this;
    }

    /** Sets {@code symmetry} to the content of a builder. */
    public Builder setSymmetry(SymmetryProto.Builder builderForValue) {
      return setSymmetry(builderForValue.build());
    }

    /** Merges {@code value} into {@code symmetry}. */
    public Builder mergeSymmetry(SymmetryProto value) {
      if (symmetry_ != null) {
        getSymmetryBuilder().mergeFrom(value);
      } else {
        setSymmetry(value.immutableCopy());
      }
      return this;
    }

    /** Clears {@code symmetry}. */
    public Builder clearSymmetry() {
      if (true) {
        symmetry_ = null;
      }
      return this;
    }

    /** Returns a builder for {@code symmetry}, setting it if needed; changes to it are reflected in this builder. */
    public SymmetryProto.Builder getSymmetryBuilder() {
      if (symmetry_ != null && symmetry_ instanceof SymmetryProto.Builder) {
        return (SymmetryProto.Builder) symmetry_;
      }
      SymmetryProto.Builder b = symmetry_ != null ? SymmetryProto.newBuilder(symmetry_) : SymmetryProto.newBuilder();
      setSymmetryRaw(b);
      return b;
    }

    private void setSymmetryRaw(SymmetryProto.Builder b) {
      symmetry_ = b;
    }

  }
}
