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
 * Define the strategy to follow when the solver needs to take a new decision.
 * Note that this strategy is only defined on a subset of variables.
 * </pre>
 *
 * <p>
 * Plain Java replacement of the protobuf message {@code operations_research.sat.DecisionStrategyProto} of OR-Tools (generated from ortools/sat/cp_model.proto, no protobuf runtime needed). Instances are immutable; use {@link Builder} to create or modify them.
 */
public class DecisionStrategyProto implements ProtoMessage {
  private static final DecisionStrategyProto DEFAULT_INSTANCE = new DecisionStrategyProto();

  /**
   * <pre>
   * The order in which the variables (resp. affine expressions) above should be
   * considered. Note that only variables that are not already fixed are
   * considered.
   *
   * TODO(user): extend as needed.
   * </pre>
   */
  public enum VariableSelectionStrategy {
    CHOOSE_FIRST(0),
    CHOOSE_LOWEST_MIN(1),
    CHOOSE_HIGHEST_MAX(2),
    CHOOSE_MIN_DOMAIN_SIZE(3),
    CHOOSE_MAX_DOMAIN_SIZE(4),
    /** A value unknown to this version of the enum. */
    UNRECOGNIZED(-1);

    /** The number of {@link #CHOOSE_FIRST}. */
    public static final int CHOOSE_FIRST_VALUE = 0;
    /** The number of {@link #CHOOSE_LOWEST_MIN}. */
    public static final int CHOOSE_LOWEST_MIN_VALUE = 1;
    /** The number of {@link #CHOOSE_HIGHEST_MAX}. */
    public static final int CHOOSE_HIGHEST_MAX_VALUE = 2;
    /** The number of {@link #CHOOSE_MIN_DOMAIN_SIZE}. */
    public static final int CHOOSE_MIN_DOMAIN_SIZE_VALUE = 3;
    /** The number of {@link #CHOOSE_MAX_DOMAIN_SIZE}. */
    public static final int CHOOSE_MAX_DOMAIN_SIZE_VALUE = 4;

    private final int value;

    VariableSelectionStrategy(int value) {
      this.value = value;
    }

    /** Returns the proto number of this value. */
    public final int getNumber() {
      if (this == UNRECOGNIZED) {
        throw new IllegalArgumentException("Can't get the number of an unknown enum value.");
      }
      return value;
    }

    /** Returns the value with the given proto number, or {@code null} if there is none. */
    public static VariableSelectionStrategy forNumber(int value) {
      switch (value) {
        case 0:
          return CHOOSE_FIRST;
        case 1:
          return CHOOSE_LOWEST_MIN;
        case 2:
          return CHOOSE_HIGHEST_MAX;
        case 3:
          return CHOOSE_MIN_DOMAIN_SIZE;
        case 4:
          return CHOOSE_MAX_DOMAIN_SIZE;
        default:
          return null;
      }
    }

    /**
     * Returns the value with the given proto number, or {@code null} if there is none.
     *
     * @deprecated use {@link #forNumber(int)}
     */
    @Deprecated
    public static VariableSelectionStrategy valueOf(int value) {
      return forNumber(value);
    }
  }

  /**
   * <pre>
   * Once a variable (resp. affine expressions) has been chosen, this enum
   * describes what decision is taken on its domain.
   *
   * TODO(user): extend as needed.
   * </pre>
   */
  public enum DomainReductionStrategy {
    SELECT_MIN_VALUE(0),
    SELECT_MAX_VALUE(1),
    SELECT_LOWER_HALF(2),
    SELECT_UPPER_HALF(3),
    SELECT_MEDIAN_VALUE(4),
    SELECT_RANDOM_HALF(5),
    /** A value unknown to this version of the enum. */
    UNRECOGNIZED(-1);

    /** The number of {@link #SELECT_MIN_VALUE}. */
    public static final int SELECT_MIN_VALUE_VALUE = 0;
    /** The number of {@link #SELECT_MAX_VALUE}. */
    public static final int SELECT_MAX_VALUE_VALUE = 1;
    /** The number of {@link #SELECT_LOWER_HALF}. */
    public static final int SELECT_LOWER_HALF_VALUE = 2;
    /** The number of {@link #SELECT_UPPER_HALF}. */
    public static final int SELECT_UPPER_HALF_VALUE = 3;
    /** The number of {@link #SELECT_MEDIAN_VALUE}. */
    public static final int SELECT_MEDIAN_VALUE_VALUE = 4;
    /** The number of {@link #SELECT_RANDOM_HALF}. */
    public static final int SELECT_RANDOM_HALF_VALUE = 5;

    private final int value;

    DomainReductionStrategy(int value) {
      this.value = value;
    }

    /** Returns the proto number of this value. */
    public final int getNumber() {
      if (this == UNRECOGNIZED) {
        throw new IllegalArgumentException("Can't get the number of an unknown enum value.");
      }
      return value;
    }

    /** Returns the value with the given proto number, or {@code null} if there is none. */
    public static DomainReductionStrategy forNumber(int value) {
      switch (value) {
        case 0:
          return SELECT_MIN_VALUE;
        case 1:
          return SELECT_MAX_VALUE;
        case 2:
          return SELECT_LOWER_HALF;
        case 3:
          return SELECT_UPPER_HALF;
        case 4:
          return SELECT_MEDIAN_VALUE;
        case 5:
          return SELECT_RANDOM_HALF;
        default:
          return null;
      }
    }

    /**
     * Returns the value with the given proto number, or {@code null} if there is none.
     *
     * @deprecated use {@link #forNumber(int)}
     */
    @Deprecated
    public static DomainReductionStrategy valueOf(int value) {
      return forNumber(value);
    }
  }

  java.util.ArrayList<Integer> variables_;
  java.util.ArrayList<LinearExpressionProto> exprs_;
  int variableSelectionStrategy_ = 0;
  int domainReductionStrategy_ = 0;

  private DecisionStrategyProto() {}

  /** Returns the immutable default instance (all fields unset). */
  public static DecisionStrategyProto getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  /** Returns the default instance. */
  public DecisionStrategyProto getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

  /**
   * <pre>
   * The variables to be considered for the next decision. The order matters and
   * is always used as a tie-breaker after the variable selection strategy
   * criteria defined below.
   * </pre>
   */
  public java.util.List<Integer> getVariablesList() {
    return variables_ == null ? java.util.Collections.<Integer>emptyList() : java.util.Collections.unmodifiableList(variables_);
  }

  /** Returns the number of elements of {@code variables}. */
  public int getVariablesCount() {
    return variables_ == null ? 0 : variables_.size();
  }

  /** Returns the element {@code index} of {@code variables}. */
  public int getVariables(int index) {
    if (variables_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return variables_.get(index);
  }

  /**
   * <pre>
   * If this is set, then the variables field must be empty.
   * We currently only support affine expressions.
   *
   * Note that this is needed so that if a variable has an affine
   * representative, we can properly transform a DecisionStrategyProto through
   * presolve.
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

  /** Returns {@code variable_selection_strategy}. */
  public DecisionStrategyProto.VariableSelectionStrategy getVariableSelectionStrategy() {
    DecisionStrategyProto.VariableSelectionStrategy result = DecisionStrategyProto.VariableSelectionStrategy.forNumber(variableSelectionStrategy_);
    return result == null ? DecisionStrategyProto.VariableSelectionStrategy.UNRECOGNIZED : result;
  }

  /** Returns the number of {@code variable_selection_strategy}. */
  public int getVariableSelectionStrategyValue() {
    return variableSelectionStrategy_;
  }

  /** Returns {@code domain_reduction_strategy}. */
  public DecisionStrategyProto.DomainReductionStrategy getDomainReductionStrategy() {
    DecisionStrategyProto.DomainReductionStrategy result = DecisionStrategyProto.DomainReductionStrategy.forNumber(domainReductionStrategy_);
    return result == null ? DecisionStrategyProto.DomainReductionStrategy.UNRECOGNIZED : result;
  }

  /** Returns the number of {@code domain_reduction_strategy}. */
  public int getDomainReductionStrategyValue() {
    return domainReductionStrategy_;
  }

  void copyFieldsFrom(DecisionStrategyProto src) {
    variables_ = src.variables_ == null || src.variables_.isEmpty() ? null : new java.util.ArrayList<>(src.variables_);
    exprs_ = buildListLinearExpressionProto(src.exprs_);
    variableSelectionStrategy_ = src.variableSelectionStrategy_;
    domainReductionStrategy_ = src.domainReductionStrategy_;
  }

  /** Returns this message if it is immutable, otherwise a built copy. */
  DecisionStrategyProto immutableCopy() {
    if (this instanceof Builder) {
      DecisionStrategyProto r = new DecisionStrategyProto();
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
    if (variables_ != null) {
      for (Integer v : variables_) {
        printer.printScalar("variables", v);
      }
    }
    if (variableSelectionStrategy_ != 0) {
      DecisionStrategyProto.VariableSelectionStrategy e = DecisionStrategyProto.VariableSelectionStrategy.forNumber(variableSelectionStrategy_);
      printer.printEnum("variable_selection_strategy", e == null ? Integer.toString(variableSelectionStrategy_) : e.name());
    }
    if (domainReductionStrategy_ != 0) {
      DecisionStrategyProto.DomainReductionStrategy e = DecisionStrategyProto.DomainReductionStrategy.forNumber(domainReductionStrategy_);
      printer.printEnum("domain_reduction_strategy", e == null ? Integer.toString(domainReductionStrategy_) : e.name());
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
    if (!(obj instanceof DecisionStrategyProto)) {
      return false;
    }
    DecisionStrategyProto other = (DecisionStrategyProto) obj;
    return getVariablesList().equals(other.getVariablesList())
        && getExprsList().equals(other.getExprsList())
        && variableSelectionStrategy_ == other.variableSelectionStrategy_
        && domainReductionStrategy_ == other.domainReductionStrategy_;
  }

  @Override
  public int hashCode() {
    int h = 149;
    h = 31 * h + getVariablesList().hashCode();
    h = 31 * h + getExprsList().hashCode();
    h = 31 * h + Integer.hashCode(variableSelectionStrategy_);
    h = 31 * h + Integer.hashCode(domainReductionStrategy_);
    return h;
  }

  /** Appends the fields of this message in the protobuf binary format. */
  void writeTo(ProtoWire.Writer w) {
    if (variables_ != null && !variables_.isEmpty()) {
      w.packedInt32(1, variables_);
    }
    if (variableSelectionStrategy_ != 0) {
      w.int32(2, variableSelectionStrategy_);
    }
    if (domainReductionStrategy_ != 0) {
      w.int32(3, domainReductionStrategy_);
    }
    if (exprs_ != null) {
      for (LinearExpressionProto v : exprs_) {
        ProtoWire.Writer sub = new ProtoWire.Writer();
        v.writeTo(sub);
        w.bytes(5, sub);
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
  public static DecisionStrategyProto parseFrom(byte[] data) {
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
  public static Builder newBuilder(DecisionStrategyProto prototype) {
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
   * Builder of {@link DecisionStrategyProto}. A builder is also a (mutable) {@code DecisionStrategyProto}, so all getters are
   * available on it.
   */
  public static final class Builder extends DecisionStrategyProto implements Cloneable {
    Builder() {}

    /** Returns an immutable copy of the current content. */
    public DecisionStrategyProto build() {
      DecisionStrategyProto r = new DecisionStrategyProto();
      r.copyFieldsFrom(this);
      return r;
    }

    /** Same as {@link #build()}. */
    public DecisionStrategyProto buildPartial() {
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
    public Builder mergeFrom(DecisionStrategyProto other) {
      if (other == DEFAULT_INSTANCE) {
        return this;
      }
      if (other.variables_ != null) {
        addAllVariables(other.variables_);
      }
      if (other.exprs_ != null) {
        addAllExprs(other.exprs_);
      }
      if (other.variableSelectionStrategy_ != 0) {
        setVariableSelectionStrategyValue(other.variableSelectionStrategy_);
      }
      if (other.domainReductionStrategy_ != 0) {
        setDomainReductionStrategyValue(other.domainReductionStrategy_);
      }
      return this;
    }

    /** Replaces the content of this builder by a deep copy of {@code other}. */
    public void copyFrom(DecisionStrategyProto other) {
      copyFieldsFrom(other);
    }

    /**
     * Returns a modifiable view of {@code variables}: elements added or set through the list are
     * stored in this builder (messages as immutable copies). The view is detached by
     * {@code clearVariables()} and {@code clear()}.
     */
    @Override
    public java.util.List<Integer> getVariablesList() {
      if (variables_ == null) {
        variables_ = new java.util.ArrayList<>();
      }
      return new ProtoWire.ListView<>(variables_, null);
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
          case 10: {
            ProtoWire.Reader p = r.packed();
            while (!p.atEnd()) {
              addVariables((int) p.varint());
            }
            break;
          }
          case 8:
            addVariables((int) r.varint());
            break;
          case 16:
            setVariableSelectionStrategyValue((int) r.varint());
            break;
          case 24:
            setDomainReductionStrategyValue((int) r.varint());
            break;
          case 42:
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
          case "variables": {
            t.consume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addVariables((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
                } while (t.nextInList());
              }
            } else {
              addVariables((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
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
          case "variable_selection_strategy": {
            if (strict && variableSelectionStrategy_ != 0) {
              throw ProtoWire.duplicate(t, "variable_selection_strategy");
            }
            t.consume(":");
            setVariableSelectionStrategyValue(t.enumValue(DecisionStrategyProto.VariableSelectionStrategy.class, DecisionStrategyProto.VariableSelectionStrategy::forNumber, DecisionStrategyProto.VariableSelectionStrategy::getNumber, false, "variable_selection_strategy"));
            break;
          }
          case "domain_reduction_strategy": {
            if (strict && domainReductionStrategy_ != 0) {
              throw ProtoWire.duplicate(t, "domain_reduction_strategy");
            }
            t.consume(":");
            setDomainReductionStrategyValue(t.enumValue(DecisionStrategyProto.DomainReductionStrategy.class, DecisionStrategyProto.DomainReductionStrategy::forNumber, DecisionStrategyProto.DomainReductionStrategy::getNumber, false, "domain_reduction_strategy"));
            break;
          }
          default:
            throw ProtoWire.unknownField(t, "operations_research.sat.DecisionStrategyProto", name);
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

    /** Replaces the element {@code index} of {@code variables}. */
    public Builder setVariables(int index, int value) {
      if (variables_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      variables_.set(index, value);
      return this;
    }

    /** Appends a value to {@code variables}. */
    public Builder addVariables(int value) {
      if (variables_ == null) {
        variables_ = new java.util.ArrayList<>();
      }
      variables_.add(value);
      return this;
    }

    /** Appends all values to {@code variables}. */
    public Builder addAllVariables(Iterable<? extends Integer> values) {
      if (variables_ == null) {
        variables_ = new java.util.ArrayList<>();
      }
      for (Integer v : values) {
        variables_.add(v);
      }
      return this;
    }

    /** Removes all values of {@code variables}. */
    public Builder clearVariables() {
      variables_ = null;
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

    /** Sets {@code variable_selection_strategy}. */
    public Builder setVariableSelectionStrategy(DecisionStrategyProto.VariableSelectionStrategy value) {
      if (value == null) {
        throw new NullPointerException();
      }
      variableSelectionStrategy_ = value.getNumber();
      return this;
    }

    /** Sets the number of {@code variable_selection_strategy}. */
    public Builder setVariableSelectionStrategyValue(int value) {
      variableSelectionStrategy_ = value;
      return this;
    }

    /** Resets {@code variable_selection_strategy} to its default value. */
    public Builder clearVariableSelectionStrategy() {
      variableSelectionStrategy_ = 0;
      return this;
    }

    /** Sets {@code domain_reduction_strategy}. */
    public Builder setDomainReductionStrategy(DecisionStrategyProto.DomainReductionStrategy value) {
      if (value == null) {
        throw new NullPointerException();
      }
      domainReductionStrategy_ = value.getNumber();
      return this;
    }

    /** Sets the number of {@code domain_reduction_strategy}. */
    public Builder setDomainReductionStrategyValue(int value) {
      domainReductionStrategy_ = value;
      return this;
    }

    /** Resets {@code domain_reduction_strategy} to its default value. */
    public Builder clearDomainReductionStrategy() {
      domainReductionStrategy_ = 0;
      return this;
    }

  }
}
