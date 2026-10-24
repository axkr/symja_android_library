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
 * Next id: 31
 * </pre>
 *
 * <p>
 * Plain Java replacement of the protobuf message {@code operations_research.sat.ConstraintProto} of OR-Tools (generated from ortools/sat/cp_model.proto, no protobuf runtime needed). Instances are immutable; use {@link Builder} to create or modify them.
 */
public class ConstraintProto implements ProtoMessage {
  private static final ConstraintProto DEFAULT_INSTANCE = new ConstraintProto();

  /** The member of the oneof {@code constraint} which is set. */
  public enum ConstraintCase {
    BOOL_OR(3),
    BOOL_AND(4),
    AT_MOST_ONE(26),
    EXACTLY_ONE(29),
    BOOL_XOR(5),
    INT_DIV(7),
    INT_MOD(8),
    INT_PROD(11),
    LIN_MAX(27),
    LINEAR(12),
    ALL_DIFF(13),
    ELEMENT(14),
    CIRCUIT(15),
    ROUTES(23),
    TABLE(16),
    AUTOMATON(17),
    INVERSE(18),
    RESERVOIR(24),
    INTERVAL(19),
    NO_OVERLAP(20),
    NO_OVERLAP_2D(21),
    CUMULATIVE(22),
    DUMMY_CONSTRAINT(30),
    CONSTRAINT_NOT_SET(0);

    private final int value;

    ConstraintCase(int value) {
      this.value = value;
    }

    /** Returns the field number of the member, 0 if none is set. */
    public int getNumber() {
      return value;
    }

    /** Returns the case of the given field number, or {@code null}. */
    public static ConstraintCase forNumber(int value) {
      for (ConstraintCase c : values()) {
        if (c.value == value) {
          return c;
        }
      }
      return null;
    }
  }

  String name_ = "";
  java.util.ArrayList<Integer> enforcementLiteral_;
  int constraintCase_;
  Object constraint_;

  private ConstraintProto() {}

  /** Returns the immutable default instance (all fields unset). */
  public static ConstraintProto getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  /** Returns the default instance. */
  public ConstraintProto getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

  /**
   * <pre>
   * The actual constraint with its arguments.
   * </pre>
   */
  public ConstraintCase getConstraintCase() {
    return ConstraintCase.forNumber(constraintCase_);
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
   * The constraint will be enforced iff all literals listed here are true. If
   * this is empty, then the constraint will always be enforced. An enforced
   * constraint must be satisfied, and an un-enforced one will simply be
   * ignored.
   *
   * This is also called half-reification. To have an equivalence between a
   * literal and a constraint (full reification), one must add both a constraint
   * (controlled by a literal l) and its negation (controlled by the negation of
   * l).
   *
   * Important: as of September 2025, some constraints might be less efficient
   * with enforcement than without: circuit, routes, no_overlap, no_overlap_2d,
   * and cumulative. If performance is not great, consider using a model without
   * these constraints enforced.
   * </pre>
   */
  public java.util.List<Integer> getEnforcementLiteralList() {
    return enforcementLiteral_ == null ? java.util.Collections.<Integer>emptyList() : java.util.Collections.unmodifiableList(enforcementLiteral_);
  }

  /** Returns the number of elements of {@code enforcement_literal}. */
  public int getEnforcementLiteralCount() {
    return enforcementLiteral_ == null ? 0 : enforcementLiteral_.size();
  }

  /** Returns the element {@code index} of {@code enforcement_literal}. */
  public int getEnforcementLiteral(int index) {
    if (enforcementLiteral_ == null) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
    }
    return enforcementLiteral_.get(index);
  }

  /** Returns true if the oneof {@code constraint} holds {@code bool_or}. */
  public boolean hasBoolOr() {
    return constraintCase_ == 3;
  }

  /**
   * <pre>
   * The bool_or constraint forces at least one literal to be true.
   * </pre>
   */
  public BoolArgumentProto getBoolOr() {
    if (constraintCase_ == 3) {
      return (BoolArgumentProto) constraint_;
    }
    return BoolArgumentProto.getDefaultInstance();
  }

  /** Returns true if the oneof {@code constraint} holds {@code bool_and}. */
  public boolean hasBoolAnd() {
    return constraintCase_ == 4;
  }

  /**
   * <pre>
   * The bool_and constraint forces all of the literals to be true.
   *
   * This is a "redundant" constraint in the sense that this can easily be
   * encoded with many bool_or or at_most_one. It is just more space efficient
   * and handled slightly differently internally.
   * </pre>
   */
  public BoolArgumentProto getBoolAnd() {
    if (constraintCase_ == 4) {
      return (BoolArgumentProto) constraint_;
    }
    return BoolArgumentProto.getDefaultInstance();
  }

  /** Returns true if the oneof {@code constraint} holds {@code at_most_one}. */
  public boolean hasAtMostOne() {
    return constraintCase_ == 26;
  }

  /**
   * <pre>
   * The at_most_one constraint enforces that no more than one literal is
   * true at the same time.
   *
   * Note that an at most one constraint of length n could be encoded with n
   * bool_and constraint with n-1 terms on the right hand side. So in a sense,
   * this constraint contributes directly to the "implication-graph" or the
   * 2-SAT part of the model.
   * </pre>
   */
  public BoolArgumentProto getAtMostOne() {
    if (constraintCase_ == 26) {
      return (BoolArgumentProto) constraint_;
    }
    return BoolArgumentProto.getDefaultInstance();
  }

  /** Returns true if the oneof {@code constraint} holds {@code exactly_one}. */
  public boolean hasExactlyOne() {
    return constraintCase_ == 29;
  }

  /**
   * <pre>
   * The exactly_one constraint forces exactly one literal to true and no
   * more.
   *
   * Anytime a bool_or (it could have been called at_least_one) is included
   * into an at_most_one, then the bool_or is actually an exactly one
   * constraint, and the extra literal in the at_most_one can be set to false.
   * So in this sense, this constraint is not really needed. It is just here
   * for a better description of the problem structure and to facilitate some
   * algorithms.
   * </pre>
   */
  public BoolArgumentProto getExactlyOne() {
    if (constraintCase_ == 29) {
      return (BoolArgumentProto) constraint_;
    }
    return BoolArgumentProto.getDefaultInstance();
  }

  /** Returns true if the oneof {@code constraint} holds {@code bool_xor}. */
  public boolean hasBoolXor() {
    return constraintCase_ == 5;
  }

  /**
   * <pre>
   * The bool_xor constraint forces an odd number of the literals to be true.
   * </pre>
   */
  public BoolArgumentProto getBoolXor() {
    if (constraintCase_ == 5) {
      return (BoolArgumentProto) constraint_;
    }
    return BoolArgumentProto.getDefaultInstance();
  }

  /** Returns true if the oneof {@code constraint} holds {@code int_div}. */
  public boolean hasIntDiv() {
    return constraintCase_ == 7;
  }

  /**
   * <pre>
   * The int_div constraint forces the target to equal exprs[0] / exprs[1].
   * The division is "rounded" towards zero, so we can have for instance
   * (2 = 12 / 5) or (-3 = -10 / 3). If you only want exact integer division,
   * then you should use instead of t = a / b, the int_prod constraint
   * a = b * t.
   *
   * If 0 belongs to the domain of exprs[1], then the model is deemed invalid.
   * </pre>
   */
  public LinearArgumentProto getIntDiv() {
    if (constraintCase_ == 7) {
      return (LinearArgumentProto) constraint_;
    }
    return LinearArgumentProto.getDefaultInstance();
  }

  /** Returns true if the oneof {@code constraint} holds {@code int_mod}. */
  public boolean hasIntMod() {
    return constraintCase_ == 8;
  }

  /**
   * <pre>
   * The int_mod constraint forces the target to equal exprs[0] % exprs[1].
   * The domain of exprs[1] must be strictly positive. The sign of the target
   * is the same as the sign of exprs[0].
   * </pre>
   */
  public LinearArgumentProto getIntMod() {
    if (constraintCase_ == 8) {
      return (LinearArgumentProto) constraint_;
    }
    return LinearArgumentProto.getDefaultInstance();
  }

  /** Returns true if the oneof {@code constraint} holds {@code int_prod}. */
  public boolean hasIntProd() {
    return constraintCase_ == 11;
  }

  /**
   * <pre>
   * The int_prod constraint forces the target to equal the product of all
   * variables. By convention, because we can just remove terms equal to one,
   * the empty product forces the target to be one.
   *
   * Note that the solver checks for potential integer overflow. So the
   * product of the maximum absolute value of all the terms (using the initial
   * domain) should fit on an int64. Otherwise the model will be declared
   * invalid.
   * </pre>
   */
  public LinearArgumentProto getIntProd() {
    if (constraintCase_ == 11) {
      return (LinearArgumentProto) constraint_;
    }
    return LinearArgumentProto.getDefaultInstance();
  }

  /** Returns true if the oneof {@code constraint} holds {@code lin_max}. */
  public boolean hasLinMax() {
    return constraintCase_ == 27;
  }

  /**
   * <pre>
   * The lin_max constraint forces the target to equal the maximum of all
   * linear expressions.
   * Note that this can model a minimum simply by negating all expressions.
   * </pre>
   */
  public LinearArgumentProto getLinMax() {
    if (constraintCase_ == 27) {
      return (LinearArgumentProto) constraint_;
    }
    return LinearArgumentProto.getDefaultInstance();
  }

  /** Returns true if the oneof {@code constraint} holds {@code linear}. */
  public boolean hasLinear() {
    return constraintCase_ == 12;
  }

  /**
   * <pre>
   * The linear constraint enforces a linear inequality among the variables,
   * such as 0 &lt;= x + 2y &lt;= 10.
   * </pre>
   */
  public LinearConstraintProto getLinear() {
    if (constraintCase_ == 12) {
      return (LinearConstraintProto) constraint_;
    }
    return LinearConstraintProto.getDefaultInstance();
  }

  /** Returns true if the oneof {@code constraint} holds {@code all_diff}. */
  public boolean hasAllDiff() {
    return constraintCase_ == 13;
  }

  /**
   * <pre>
   * The all_diff constraint forces all variables to take different values.
   * </pre>
   */
  public AllDifferentConstraintProto getAllDiff() {
    if (constraintCase_ == 13) {
      return (AllDifferentConstraintProto) constraint_;
    }
    return AllDifferentConstraintProto.getDefaultInstance();
  }

  /** Returns true if the oneof {@code constraint} holds {@code element}. */
  public boolean hasElement() {
    return constraintCase_ == 14;
  }

  /**
   * <pre>
   * The element constraint forces the variable with the given index
   * to be equal to the target.
   * </pre>
   */
  public ElementConstraintProto getElement() {
    if (constraintCase_ == 14) {
      return (ElementConstraintProto) constraint_;
    }
    return ElementConstraintProto.getDefaultInstance();
  }

  /** Returns true if the oneof {@code constraint} holds {@code circuit}. */
  public boolean hasCircuit() {
    return constraintCase_ == 15;
  }

  /**
   * <pre>
   * The circuit constraint takes a graph and forces the arcs present
   * (with arc presence indicated by a literal) to form a unique cycle.
   * </pre>
   */
  public CircuitConstraintProto getCircuit() {
    if (constraintCase_ == 15) {
      return (CircuitConstraintProto) constraint_;
    }
    return CircuitConstraintProto.getDefaultInstance();
  }

  /** Returns true if the oneof {@code constraint} holds {@code routes}. */
  public boolean hasRoutes() {
    return constraintCase_ == 23;
  }

  /**
   * <pre>
   * The routes constraint implements the vehicle routing problem.
   * </pre>
   */
  public RoutesConstraintProto getRoutes() {
    if (constraintCase_ == 23) {
      return (RoutesConstraintProto) constraint_;
    }
    return RoutesConstraintProto.getDefaultInstance();
  }

  /** Returns true if the oneof {@code constraint} holds {@code table}. */
  public boolean hasTable() {
    return constraintCase_ == 16;
  }

  /**
   * <pre>
   * The table constraint enforces what values a tuple of variables may
   * take.
   * </pre>
   */
  public TableConstraintProto getTable() {
    if (constraintCase_ == 16) {
      return (TableConstraintProto) constraint_;
    }
    return TableConstraintProto.getDefaultInstance();
  }

  /** Returns true if the oneof {@code constraint} holds {@code automaton}. */
  public boolean hasAutomaton() {
    return constraintCase_ == 17;
  }

  /**
   * <pre>
   * The automaton constraint forces a sequence of variables to be accepted
   * by an automaton.
   * </pre>
   */
  public AutomatonConstraintProto getAutomaton() {
    if (constraintCase_ == 17) {
      return (AutomatonConstraintProto) constraint_;
    }
    return AutomatonConstraintProto.getDefaultInstance();
  }

  /** Returns true if the oneof {@code constraint} holds {@code inverse}. */
  public boolean hasInverse() {
    return constraintCase_ == 18;
  }

  /**
   * <pre>
   * The inverse constraint forces two arrays to be inverses of each other:
   * the values of one are the indices of the other, and vice versa.
   * </pre>
   */
  public InverseConstraintProto getInverse() {
    if (constraintCase_ == 18) {
      return (InverseConstraintProto) constraint_;
    }
    return InverseConstraintProto.getDefaultInstance();
  }

  /** Returns true if the oneof {@code constraint} holds {@code reservoir}. */
  public boolean hasReservoir() {
    return constraintCase_ == 24;
  }

  /**
   * <pre>
   * The reservoir constraint forces the sum of a set of active demands
   * to always be between a specified minimum and maximum value during
   * specific times.
   * </pre>
   */
  public ReservoirConstraintProto getReservoir() {
    if (constraintCase_ == 24) {
      return (ReservoirConstraintProto) constraint_;
    }
    return ReservoirConstraintProto.getDefaultInstance();
  }

  /** Returns true if the oneof {@code constraint} holds {@code interval}. */
  public boolean hasInterval() {
    return constraintCase_ == 19;
  }

  /**
   * <pre>
   * The interval constraint takes a start, end, and size, and forces
   * start + size == end.
   * </pre>
   */
  public IntervalConstraintProto getInterval() {
    if (constraintCase_ == 19) {
      return (IntervalConstraintProto) constraint_;
    }
    return IntervalConstraintProto.getDefaultInstance();
  }

  /** Returns true if the oneof {@code constraint} holds {@code no_overlap}. */
  public boolean hasNoOverlap() {
    return constraintCase_ == 20;
  }

  /**
   * <pre>
   * The no_overlap constraint prevents a set of intervals from
   * overlapping; in scheduling, this is called a disjunctive
   * constraint.
   * </pre>
   */
  public NoOverlapConstraintProto getNoOverlap() {
    if (constraintCase_ == 20) {
      return (NoOverlapConstraintProto) constraint_;
    }
    return NoOverlapConstraintProto.getDefaultInstance();
  }

  /** Returns true if the oneof {@code constraint} holds {@code no_overlap_2d}. */
  public boolean hasNoOverlap2D() {
    return constraintCase_ == 21;
  }

  /**
   * <pre>
   * The no_overlap_2d constraint prevents a set of boxes from overlapping.
   * </pre>
   */
  public NoOverlap2DConstraintProto getNoOverlap2D() {
    if (constraintCase_ == 21) {
      return (NoOverlap2DConstraintProto) constraint_;
    }
    return NoOverlap2DConstraintProto.getDefaultInstance();
  }

  /** Returns true if the oneof {@code constraint} holds {@code cumulative}. */
  public boolean hasCumulative() {
    return constraintCase_ == 22;
  }

  /**
   * <pre>
   * The cumulative constraint ensures that for any integer point, the sum
   * of the demands of the intervals containing that point does not exceed
   * the capacity.
   * </pre>
   */
  public CumulativeConstraintProto getCumulative() {
    if (constraintCase_ == 22) {
      return (CumulativeConstraintProto) constraint_;
    }
    return CumulativeConstraintProto.getDefaultInstance();
  }

  /** Returns true if the oneof {@code constraint} holds {@code dummy_constraint}. */
  public boolean hasDummyConstraint() {
    return constraintCase_ == 30;
  }

  /**
   * <pre>
   * This constraint is not meant to be used and will be rejected by the
   * solver. It is meant to mark variables when testing the presolve code.
   * </pre>
   */
  public ListOfVariablesProto getDummyConstraint() {
    if (constraintCase_ == 30) {
      return (ListOfVariablesProto) constraint_;
    }
    return ListOfVariablesProto.getDefaultInstance();
  }

  void copyFieldsFrom(ConstraintProto src) {
    name_ = src.name_;
    enforcementLiteral_ = src.enforcementLiteral_ == null || src.enforcementLiteral_.isEmpty() ? null : new java.util.ArrayList<>(src.enforcementLiteral_);
    constraintCase_ = src.constraintCase_;
    constraint_ = src.constraint_ instanceof ProtoMessage ? immutableCopyOf(src.constraint_) : src.constraint_;
  }

  /** Returns this message if it is immutable, otherwise a built copy. */
  ConstraintProto immutableCopy() {
    if (this instanceof Builder) {
      ConstraintProto r = new ConstraintProto();
      r.copyFieldsFrom(this);
      return r;
    }
    return this;
  }

  private static Object immutableCopyOf(Object o) {
    if (o instanceof BoolArgumentProto) {
      return ((BoolArgumentProto) o).immutableCopy();
    }
    if (o instanceof LinearArgumentProto) {
      return ((LinearArgumentProto) o).immutableCopy();
    }
    if (o instanceof LinearConstraintProto) {
      return ((LinearConstraintProto) o).immutableCopy();
    }
    if (o instanceof AllDifferentConstraintProto) {
      return ((AllDifferentConstraintProto) o).immutableCopy();
    }
    if (o instanceof ElementConstraintProto) {
      return ((ElementConstraintProto) o).immutableCopy();
    }
    if (o instanceof CircuitConstraintProto) {
      return ((CircuitConstraintProto) o).immutableCopy();
    }
    if (o instanceof RoutesConstraintProto) {
      return ((RoutesConstraintProto) o).immutableCopy();
    }
    if (o instanceof TableConstraintProto) {
      return ((TableConstraintProto) o).immutableCopy();
    }
    if (o instanceof AutomatonConstraintProto) {
      return ((AutomatonConstraintProto) o).immutableCopy();
    }
    if (o instanceof InverseConstraintProto) {
      return ((InverseConstraintProto) o).immutableCopy();
    }
    if (o instanceof ReservoirConstraintProto) {
      return ((ReservoirConstraintProto) o).immutableCopy();
    }
    if (o instanceof IntervalConstraintProto) {
      return ((IntervalConstraintProto) o).immutableCopy();
    }
    if (o instanceof NoOverlapConstraintProto) {
      return ((NoOverlapConstraintProto) o).immutableCopy();
    }
    if (o instanceof NoOverlap2DConstraintProto) {
      return ((NoOverlap2DConstraintProto) o).immutableCopy();
    }
    if (o instanceof CumulativeConstraintProto) {
      return ((CumulativeConstraintProto) o).immutableCopy();
    }
    if (o instanceof ListOfVariablesProto) {
      return ((ListOfVariablesProto) o).immutableCopy();
    }
    return o;
  }

  @Override
  public void printTo(ProtoPrinter printer) {
    if (!name_.isEmpty()) {
      printer.printScalar("name", name_);
    }
    if (enforcementLiteral_ != null) {
      for (Integer v : enforcementLiteral_) {
        printer.printScalar("enforcement_literal", v);
      }
    }
    if (constraintCase_ == 3) {
      printer.printMessage("bool_or", (ProtoMessage) constraint_);
    }
    if (constraintCase_ == 4) {
      printer.printMessage("bool_and", (ProtoMessage) constraint_);
    }
    if (constraintCase_ == 5) {
      printer.printMessage("bool_xor", (ProtoMessage) constraint_);
    }
    if (constraintCase_ == 7) {
      printer.printMessage("int_div", (ProtoMessage) constraint_);
    }
    if (constraintCase_ == 8) {
      printer.printMessage("int_mod", (ProtoMessage) constraint_);
    }
    if (constraintCase_ == 11) {
      printer.printMessage("int_prod", (ProtoMessage) constraint_);
    }
    if (constraintCase_ == 12) {
      printer.printMessage("linear", (ProtoMessage) constraint_);
    }
    if (constraintCase_ == 13) {
      printer.printMessage("all_diff", (ProtoMessage) constraint_);
    }
    if (constraintCase_ == 14) {
      printer.printMessage("element", (ProtoMessage) constraint_);
    }
    if (constraintCase_ == 15) {
      printer.printMessage("circuit", (ProtoMessage) constraint_);
    }
    if (constraintCase_ == 16) {
      printer.printMessage("table", (ProtoMessage) constraint_);
    }
    if (constraintCase_ == 17) {
      printer.printMessage("automaton", (ProtoMessage) constraint_);
    }
    if (constraintCase_ == 18) {
      printer.printMessage("inverse", (ProtoMessage) constraint_);
    }
    if (constraintCase_ == 19) {
      printer.printMessage("interval", (ProtoMessage) constraint_);
    }
    if (constraintCase_ == 20) {
      printer.printMessage("no_overlap", (ProtoMessage) constraint_);
    }
    if (constraintCase_ == 21) {
      printer.printMessage("no_overlap_2d", (ProtoMessage) constraint_);
    }
    if (constraintCase_ == 22) {
      printer.printMessage("cumulative", (ProtoMessage) constraint_);
    }
    if (constraintCase_ == 23) {
      printer.printMessage("routes", (ProtoMessage) constraint_);
    }
    if (constraintCase_ == 24) {
      printer.printMessage("reservoir", (ProtoMessage) constraint_);
    }
    if (constraintCase_ == 26) {
      printer.printMessage("at_most_one", (ProtoMessage) constraint_);
    }
    if (constraintCase_ == 27) {
      printer.printMessage("lin_max", (ProtoMessage) constraint_);
    }
    if (constraintCase_ == 29) {
      printer.printMessage("exactly_one", (ProtoMessage) constraint_);
    }
    if (constraintCase_ == 30) {
      printer.printMessage("dummy_constraint", (ProtoMessage) constraint_);
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
    if (!(obj instanceof ConstraintProto)) {
      return false;
    }
    ConstraintProto other = (ConstraintProto) obj;
    return name_.equals(other.name_)
        && getEnforcementLiteralList().equals(other.getEnforcementLiteralList())
        && constraintCase_ == other.constraintCase_
        && java.util.Objects.equals(constraint_, other.constraint_);
  }

  @Override
  public int hashCode() {
    int h = 553;
    h = 31 * h + name_.hashCode();
    h = 31 * h + getEnforcementLiteralList().hashCode();
    h = 31 * h + constraintCase_;
    h = 31 * h + java.util.Objects.hashCode(constraint_);
    return h;
  }

  /** Appends the fields of this message in the protobuf binary format. */
  void writeTo(ProtoWire.Writer w) {
    if (!name_.isEmpty()) {
      w.string(1, name_);
    }
    if (enforcementLiteral_ != null && !enforcementLiteral_.isEmpty()) {
      w.packedInt32(2, enforcementLiteral_);
    }
    if (constraintCase_ == 3) {
      ProtoWire.Writer sub = new ProtoWire.Writer();
      ((BoolArgumentProto) constraint_).writeTo(sub);
      w.bytes(3, sub);
    }
    if (constraintCase_ == 4) {
      ProtoWire.Writer sub = new ProtoWire.Writer();
      ((BoolArgumentProto) constraint_).writeTo(sub);
      w.bytes(4, sub);
    }
    if (constraintCase_ == 5) {
      ProtoWire.Writer sub = new ProtoWire.Writer();
      ((BoolArgumentProto) constraint_).writeTo(sub);
      w.bytes(5, sub);
    }
    if (constraintCase_ == 7) {
      ProtoWire.Writer sub = new ProtoWire.Writer();
      ((LinearArgumentProto) constraint_).writeTo(sub);
      w.bytes(7, sub);
    }
    if (constraintCase_ == 8) {
      ProtoWire.Writer sub = new ProtoWire.Writer();
      ((LinearArgumentProto) constraint_).writeTo(sub);
      w.bytes(8, sub);
    }
    if (constraintCase_ == 11) {
      ProtoWire.Writer sub = new ProtoWire.Writer();
      ((LinearArgumentProto) constraint_).writeTo(sub);
      w.bytes(11, sub);
    }
    if (constraintCase_ == 12) {
      ProtoWire.Writer sub = new ProtoWire.Writer();
      ((LinearConstraintProto) constraint_).writeTo(sub);
      w.bytes(12, sub);
    }
    if (constraintCase_ == 13) {
      ProtoWire.Writer sub = new ProtoWire.Writer();
      ((AllDifferentConstraintProto) constraint_).writeTo(sub);
      w.bytes(13, sub);
    }
    if (constraintCase_ == 14) {
      ProtoWire.Writer sub = new ProtoWire.Writer();
      ((ElementConstraintProto) constraint_).writeTo(sub);
      w.bytes(14, sub);
    }
    if (constraintCase_ == 15) {
      ProtoWire.Writer sub = new ProtoWire.Writer();
      ((CircuitConstraintProto) constraint_).writeTo(sub);
      w.bytes(15, sub);
    }
    if (constraintCase_ == 16) {
      ProtoWire.Writer sub = new ProtoWire.Writer();
      ((TableConstraintProto) constraint_).writeTo(sub);
      w.bytes(16, sub);
    }
    if (constraintCase_ == 17) {
      ProtoWire.Writer sub = new ProtoWire.Writer();
      ((AutomatonConstraintProto) constraint_).writeTo(sub);
      w.bytes(17, sub);
    }
    if (constraintCase_ == 18) {
      ProtoWire.Writer sub = new ProtoWire.Writer();
      ((InverseConstraintProto) constraint_).writeTo(sub);
      w.bytes(18, sub);
    }
    if (constraintCase_ == 19) {
      ProtoWire.Writer sub = new ProtoWire.Writer();
      ((IntervalConstraintProto) constraint_).writeTo(sub);
      w.bytes(19, sub);
    }
    if (constraintCase_ == 20) {
      ProtoWire.Writer sub = new ProtoWire.Writer();
      ((NoOverlapConstraintProto) constraint_).writeTo(sub);
      w.bytes(20, sub);
    }
    if (constraintCase_ == 21) {
      ProtoWire.Writer sub = new ProtoWire.Writer();
      ((NoOverlap2DConstraintProto) constraint_).writeTo(sub);
      w.bytes(21, sub);
    }
    if (constraintCase_ == 22) {
      ProtoWire.Writer sub = new ProtoWire.Writer();
      ((CumulativeConstraintProto) constraint_).writeTo(sub);
      w.bytes(22, sub);
    }
    if (constraintCase_ == 23) {
      ProtoWire.Writer sub = new ProtoWire.Writer();
      ((RoutesConstraintProto) constraint_).writeTo(sub);
      w.bytes(23, sub);
    }
    if (constraintCase_ == 24) {
      ProtoWire.Writer sub = new ProtoWire.Writer();
      ((ReservoirConstraintProto) constraint_).writeTo(sub);
      w.bytes(24, sub);
    }
    if (constraintCase_ == 26) {
      ProtoWire.Writer sub = new ProtoWire.Writer();
      ((BoolArgumentProto) constraint_).writeTo(sub);
      w.bytes(26, sub);
    }
    if (constraintCase_ == 27) {
      ProtoWire.Writer sub = new ProtoWire.Writer();
      ((LinearArgumentProto) constraint_).writeTo(sub);
      w.bytes(27, sub);
    }
    if (constraintCase_ == 29) {
      ProtoWire.Writer sub = new ProtoWire.Writer();
      ((BoolArgumentProto) constraint_).writeTo(sub);
      w.bytes(29, sub);
    }
    if (constraintCase_ == 30) {
      ProtoWire.Writer sub = new ProtoWire.Writer();
      ((ListOfVariablesProto) constraint_).writeTo(sub);
      w.bytes(30, sub);
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
  public static ConstraintProto parseFrom(byte[] data) {
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
  public static Builder newBuilder(ConstraintProto prototype) {
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
   * Builder of {@link ConstraintProto}. A builder is also a (mutable) {@code ConstraintProto}, so all getters are
   * available on it.
   */
  public static final class Builder extends ConstraintProto implements Cloneable {
    Builder() {}

    /** Returns an immutable copy of the current content. */
    public ConstraintProto build() {
      ConstraintProto r = new ConstraintProto();
      r.copyFieldsFrom(this);
      return r;
    }

    /** Same as {@link #build()}. */
    public ConstraintProto buildPartial() {
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
    public Builder mergeFrom(ConstraintProto other) {
      if (other == DEFAULT_INSTANCE) {
        return this;
      }
      if (!other.name_.isEmpty()) {
        setName(other.name_);
      }
      if (other.enforcementLiteral_ != null) {
        addAllEnforcementLiteral(other.enforcementLiteral_);
      }
      if (other.hasBoolOr()) {
        mergeBoolOr(other.getBoolOr());
      }
      if (other.hasBoolAnd()) {
        mergeBoolAnd(other.getBoolAnd());
      }
      if (other.hasAtMostOne()) {
        mergeAtMostOne(other.getAtMostOne());
      }
      if (other.hasExactlyOne()) {
        mergeExactlyOne(other.getExactlyOne());
      }
      if (other.hasBoolXor()) {
        mergeBoolXor(other.getBoolXor());
      }
      if (other.hasIntDiv()) {
        mergeIntDiv(other.getIntDiv());
      }
      if (other.hasIntMod()) {
        mergeIntMod(other.getIntMod());
      }
      if (other.hasIntProd()) {
        mergeIntProd(other.getIntProd());
      }
      if (other.hasLinMax()) {
        mergeLinMax(other.getLinMax());
      }
      if (other.hasLinear()) {
        mergeLinear(other.getLinear());
      }
      if (other.hasAllDiff()) {
        mergeAllDiff(other.getAllDiff());
      }
      if (other.hasElement()) {
        mergeElement(other.getElement());
      }
      if (other.hasCircuit()) {
        mergeCircuit(other.getCircuit());
      }
      if (other.hasRoutes()) {
        mergeRoutes(other.getRoutes());
      }
      if (other.hasTable()) {
        mergeTable(other.getTable());
      }
      if (other.hasAutomaton()) {
        mergeAutomaton(other.getAutomaton());
      }
      if (other.hasInverse()) {
        mergeInverse(other.getInverse());
      }
      if (other.hasReservoir()) {
        mergeReservoir(other.getReservoir());
      }
      if (other.hasInterval()) {
        mergeInterval(other.getInterval());
      }
      if (other.hasNoOverlap()) {
        mergeNoOverlap(other.getNoOverlap());
      }
      if (other.hasNoOverlap2D()) {
        mergeNoOverlap2D(other.getNoOverlap2D());
      }
      if (other.hasCumulative()) {
        mergeCumulative(other.getCumulative());
      }
      if (other.hasDummyConstraint()) {
        mergeDummyConstraint(other.getDummyConstraint());
      }
      return this;
    }

    /** Replaces the content of this builder by a deep copy of {@code other}. */
    public void copyFrom(ConstraintProto other) {
      copyFieldsFrom(other);
    }

    /**
     * Returns a modifiable view of {@code enforcement_literal}: elements added or set through the list are
     * stored in this builder (messages as immutable copies). The view is detached by
     * {@code clearEnforcementLiteral()} and {@code clear()}.
     */
    @Override
    public java.util.List<Integer> getEnforcementLiteralList() {
      if (enforcementLiteral_ == null) {
        enforcementLiteral_ = new java.util.ArrayList<>();
      }
      return new ProtoWire.ListView<>(enforcementLiteral_, null);
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
              addEnforcementLiteral((int) p.varint());
            }
            break;
          }
          case 16:
            addEnforcementLiteral((int) r.varint());
            break;
          case 26:
            getBoolOrBuilder().mergeWire(r.message());
            break;
          case 34:
            getBoolAndBuilder().mergeWire(r.message());
            break;
          case 42:
            getBoolXorBuilder().mergeWire(r.message());
            break;
          case 58:
            getIntDivBuilder().mergeWire(r.message());
            break;
          case 66:
            getIntModBuilder().mergeWire(r.message());
            break;
          case 90:
            getIntProdBuilder().mergeWire(r.message());
            break;
          case 98:
            getLinearBuilder().mergeWire(r.message());
            break;
          case 106:
            getAllDiffBuilder().mergeWire(r.message());
            break;
          case 114:
            getElementBuilder().mergeWire(r.message());
            break;
          case 122:
            getCircuitBuilder().mergeWire(r.message());
            break;
          case 130:
            getTableBuilder().mergeWire(r.message());
            break;
          case 138:
            getAutomatonBuilder().mergeWire(r.message());
            break;
          case 146:
            getInverseBuilder().mergeWire(r.message());
            break;
          case 154:
            getIntervalBuilder().mergeWire(r.message());
            break;
          case 162:
            getNoOverlapBuilder().mergeWire(r.message());
            break;
          case 170:
            getNoOverlap2DBuilder().mergeWire(r.message());
            break;
          case 178:
            getCumulativeBuilder().mergeWire(r.message());
            break;
          case 186:
            getRoutesBuilder().mergeWire(r.message());
            break;
          case 194:
            getReservoirBuilder().mergeWire(r.message());
            break;
          case 210:
            getAtMostOneBuilder().mergeWire(r.message());
            break;
          case 218:
            getLinMaxBuilder().mergeWire(r.message());
            break;
          case 234:
            getExactlyOneBuilder().mergeWire(r.message());
            break;
          case 242:
            getDummyConstraintBuilder().mergeWire(r.message());
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
          case "enforcement_literal": {
            t.consume(":");
            if (t.startList()) {
              if (!t.emptyList()) {
                do {
                  addEnforcementLiteral((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
                } while (t.nextInList());
              }
            } else {
              addEnforcementLiteral((int) t.integer(Integer.MIN_VALUE, Integer.MAX_VALUE));
            }
            break;
          }
          case "bool_or": {
            if (constraintCase_ != 0 && constraintCase_ != 3) {
              throw ProtoWire.oneofConflict(t, "bool_or", ConstraintCase.forNumber(constraintCase_).name().toLowerCase(java.util.Locale.ROOT), "constraint");
            }
            if (strict && constraintCase_ == 3) {
              throw ProtoWire.duplicate(t, "bool_or");
            }
            t.tryConsume(":");
            getBoolOrBuilder().mergeText(t, t.openMessage(), strict);
            break;
          }
          case "bool_and": {
            if (constraintCase_ != 0 && constraintCase_ != 4) {
              throw ProtoWire.oneofConflict(t, "bool_and", ConstraintCase.forNumber(constraintCase_).name().toLowerCase(java.util.Locale.ROOT), "constraint");
            }
            if (strict && constraintCase_ == 4) {
              throw ProtoWire.duplicate(t, "bool_and");
            }
            t.tryConsume(":");
            getBoolAndBuilder().mergeText(t, t.openMessage(), strict);
            break;
          }
          case "at_most_one": {
            if (constraintCase_ != 0 && constraintCase_ != 26) {
              throw ProtoWire.oneofConflict(t, "at_most_one", ConstraintCase.forNumber(constraintCase_).name().toLowerCase(java.util.Locale.ROOT), "constraint");
            }
            if (strict && constraintCase_ == 26) {
              throw ProtoWire.duplicate(t, "at_most_one");
            }
            t.tryConsume(":");
            getAtMostOneBuilder().mergeText(t, t.openMessage(), strict);
            break;
          }
          case "exactly_one": {
            if (constraintCase_ != 0 && constraintCase_ != 29) {
              throw ProtoWire.oneofConflict(t, "exactly_one", ConstraintCase.forNumber(constraintCase_).name().toLowerCase(java.util.Locale.ROOT), "constraint");
            }
            if (strict && constraintCase_ == 29) {
              throw ProtoWire.duplicate(t, "exactly_one");
            }
            t.tryConsume(":");
            getExactlyOneBuilder().mergeText(t, t.openMessage(), strict);
            break;
          }
          case "bool_xor": {
            if (constraintCase_ != 0 && constraintCase_ != 5) {
              throw ProtoWire.oneofConflict(t, "bool_xor", ConstraintCase.forNumber(constraintCase_).name().toLowerCase(java.util.Locale.ROOT), "constraint");
            }
            if (strict && constraintCase_ == 5) {
              throw ProtoWire.duplicate(t, "bool_xor");
            }
            t.tryConsume(":");
            getBoolXorBuilder().mergeText(t, t.openMessage(), strict);
            break;
          }
          case "int_div": {
            if (constraintCase_ != 0 && constraintCase_ != 7) {
              throw ProtoWire.oneofConflict(t, "int_div", ConstraintCase.forNumber(constraintCase_).name().toLowerCase(java.util.Locale.ROOT), "constraint");
            }
            if (strict && constraintCase_ == 7) {
              throw ProtoWire.duplicate(t, "int_div");
            }
            t.tryConsume(":");
            getIntDivBuilder().mergeText(t, t.openMessage(), strict);
            break;
          }
          case "int_mod": {
            if (constraintCase_ != 0 && constraintCase_ != 8) {
              throw ProtoWire.oneofConflict(t, "int_mod", ConstraintCase.forNumber(constraintCase_).name().toLowerCase(java.util.Locale.ROOT), "constraint");
            }
            if (strict && constraintCase_ == 8) {
              throw ProtoWire.duplicate(t, "int_mod");
            }
            t.tryConsume(":");
            getIntModBuilder().mergeText(t, t.openMessage(), strict);
            break;
          }
          case "int_prod": {
            if (constraintCase_ != 0 && constraintCase_ != 11) {
              throw ProtoWire.oneofConflict(t, "int_prod", ConstraintCase.forNumber(constraintCase_).name().toLowerCase(java.util.Locale.ROOT), "constraint");
            }
            if (strict && constraintCase_ == 11) {
              throw ProtoWire.duplicate(t, "int_prod");
            }
            t.tryConsume(":");
            getIntProdBuilder().mergeText(t, t.openMessage(), strict);
            break;
          }
          case "lin_max": {
            if (constraintCase_ != 0 && constraintCase_ != 27) {
              throw ProtoWire.oneofConflict(t, "lin_max", ConstraintCase.forNumber(constraintCase_).name().toLowerCase(java.util.Locale.ROOT), "constraint");
            }
            if (strict && constraintCase_ == 27) {
              throw ProtoWire.duplicate(t, "lin_max");
            }
            t.tryConsume(":");
            getLinMaxBuilder().mergeText(t, t.openMessage(), strict);
            break;
          }
          case "linear": {
            if (constraintCase_ != 0 && constraintCase_ != 12) {
              throw ProtoWire.oneofConflict(t, "linear", ConstraintCase.forNumber(constraintCase_).name().toLowerCase(java.util.Locale.ROOT), "constraint");
            }
            if (strict && constraintCase_ == 12) {
              throw ProtoWire.duplicate(t, "linear");
            }
            t.tryConsume(":");
            getLinearBuilder().mergeText(t, t.openMessage(), strict);
            break;
          }
          case "all_diff": {
            if (constraintCase_ != 0 && constraintCase_ != 13) {
              throw ProtoWire.oneofConflict(t, "all_diff", ConstraintCase.forNumber(constraintCase_).name().toLowerCase(java.util.Locale.ROOT), "constraint");
            }
            if (strict && constraintCase_ == 13) {
              throw ProtoWire.duplicate(t, "all_diff");
            }
            t.tryConsume(":");
            getAllDiffBuilder().mergeText(t, t.openMessage(), strict);
            break;
          }
          case "element": {
            if (constraintCase_ != 0 && constraintCase_ != 14) {
              throw ProtoWire.oneofConflict(t, "element", ConstraintCase.forNumber(constraintCase_).name().toLowerCase(java.util.Locale.ROOT), "constraint");
            }
            if (strict && constraintCase_ == 14) {
              throw ProtoWire.duplicate(t, "element");
            }
            t.tryConsume(":");
            getElementBuilder().mergeText(t, t.openMessage(), strict);
            break;
          }
          case "circuit": {
            if (constraintCase_ != 0 && constraintCase_ != 15) {
              throw ProtoWire.oneofConflict(t, "circuit", ConstraintCase.forNumber(constraintCase_).name().toLowerCase(java.util.Locale.ROOT), "constraint");
            }
            if (strict && constraintCase_ == 15) {
              throw ProtoWire.duplicate(t, "circuit");
            }
            t.tryConsume(":");
            getCircuitBuilder().mergeText(t, t.openMessage(), strict);
            break;
          }
          case "routes": {
            if (constraintCase_ != 0 && constraintCase_ != 23) {
              throw ProtoWire.oneofConflict(t, "routes", ConstraintCase.forNumber(constraintCase_).name().toLowerCase(java.util.Locale.ROOT), "constraint");
            }
            if (strict && constraintCase_ == 23) {
              throw ProtoWire.duplicate(t, "routes");
            }
            t.tryConsume(":");
            getRoutesBuilder().mergeText(t, t.openMessage(), strict);
            break;
          }
          case "table": {
            if (constraintCase_ != 0 && constraintCase_ != 16) {
              throw ProtoWire.oneofConflict(t, "table", ConstraintCase.forNumber(constraintCase_).name().toLowerCase(java.util.Locale.ROOT), "constraint");
            }
            if (strict && constraintCase_ == 16) {
              throw ProtoWire.duplicate(t, "table");
            }
            t.tryConsume(":");
            getTableBuilder().mergeText(t, t.openMessage(), strict);
            break;
          }
          case "automaton": {
            if (constraintCase_ != 0 && constraintCase_ != 17) {
              throw ProtoWire.oneofConflict(t, "automaton", ConstraintCase.forNumber(constraintCase_).name().toLowerCase(java.util.Locale.ROOT), "constraint");
            }
            if (strict && constraintCase_ == 17) {
              throw ProtoWire.duplicate(t, "automaton");
            }
            t.tryConsume(":");
            getAutomatonBuilder().mergeText(t, t.openMessage(), strict);
            break;
          }
          case "inverse": {
            if (constraintCase_ != 0 && constraintCase_ != 18) {
              throw ProtoWire.oneofConflict(t, "inverse", ConstraintCase.forNumber(constraintCase_).name().toLowerCase(java.util.Locale.ROOT), "constraint");
            }
            if (strict && constraintCase_ == 18) {
              throw ProtoWire.duplicate(t, "inverse");
            }
            t.tryConsume(":");
            getInverseBuilder().mergeText(t, t.openMessage(), strict);
            break;
          }
          case "reservoir": {
            if (constraintCase_ != 0 && constraintCase_ != 24) {
              throw ProtoWire.oneofConflict(t, "reservoir", ConstraintCase.forNumber(constraintCase_).name().toLowerCase(java.util.Locale.ROOT), "constraint");
            }
            if (strict && constraintCase_ == 24) {
              throw ProtoWire.duplicate(t, "reservoir");
            }
            t.tryConsume(":");
            getReservoirBuilder().mergeText(t, t.openMessage(), strict);
            break;
          }
          case "interval": {
            if (constraintCase_ != 0 && constraintCase_ != 19) {
              throw ProtoWire.oneofConflict(t, "interval", ConstraintCase.forNumber(constraintCase_).name().toLowerCase(java.util.Locale.ROOT), "constraint");
            }
            if (strict && constraintCase_ == 19) {
              throw ProtoWire.duplicate(t, "interval");
            }
            t.tryConsume(":");
            getIntervalBuilder().mergeText(t, t.openMessage(), strict);
            break;
          }
          case "no_overlap": {
            if (constraintCase_ != 0 && constraintCase_ != 20) {
              throw ProtoWire.oneofConflict(t, "no_overlap", ConstraintCase.forNumber(constraintCase_).name().toLowerCase(java.util.Locale.ROOT), "constraint");
            }
            if (strict && constraintCase_ == 20) {
              throw ProtoWire.duplicate(t, "no_overlap");
            }
            t.tryConsume(":");
            getNoOverlapBuilder().mergeText(t, t.openMessage(), strict);
            break;
          }
          case "no_overlap_2d": {
            if (constraintCase_ != 0 && constraintCase_ != 21) {
              throw ProtoWire.oneofConflict(t, "no_overlap_2d", ConstraintCase.forNumber(constraintCase_).name().toLowerCase(java.util.Locale.ROOT), "constraint");
            }
            if (strict && constraintCase_ == 21) {
              throw ProtoWire.duplicate(t, "no_overlap_2d");
            }
            t.tryConsume(":");
            getNoOverlap2DBuilder().mergeText(t, t.openMessage(), strict);
            break;
          }
          case "cumulative": {
            if (constraintCase_ != 0 && constraintCase_ != 22) {
              throw ProtoWire.oneofConflict(t, "cumulative", ConstraintCase.forNumber(constraintCase_).name().toLowerCase(java.util.Locale.ROOT), "constraint");
            }
            if (strict && constraintCase_ == 22) {
              throw ProtoWire.duplicate(t, "cumulative");
            }
            t.tryConsume(":");
            getCumulativeBuilder().mergeText(t, t.openMessage(), strict);
            break;
          }
          case "dummy_constraint": {
            if (constraintCase_ != 0 && constraintCase_ != 30) {
              throw ProtoWire.oneofConflict(t, "dummy_constraint", ConstraintCase.forNumber(constraintCase_).name().toLowerCase(java.util.Locale.ROOT), "constraint");
            }
            if (strict && constraintCase_ == 30) {
              throw ProtoWire.duplicate(t, "dummy_constraint");
            }
            t.tryConsume(":");
            getDummyConstraintBuilder().mergeText(t, t.openMessage(), strict);
            break;
          }
          default:
            throw ProtoWire.unknownField(t, "operations_research.sat.ConstraintProto", name);
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

    /** Clears the oneof {@code constraint}. */
    public Builder clearConstraint() {
      constraintCase_ = 0;
      constraint_ = null;
      return this;
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

    /** Replaces the element {@code index} of {@code enforcement_literal}. */
    public Builder setEnforcementLiteral(int index, int value) {
      if (enforcementLiteral_ == null) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
      }
      enforcementLiteral_.set(index, value);
      return this;
    }

    /** Appends a value to {@code enforcement_literal}. */
    public Builder addEnforcementLiteral(int value) {
      if (enforcementLiteral_ == null) {
        enforcementLiteral_ = new java.util.ArrayList<>();
      }
      enforcementLiteral_.add(value);
      return this;
    }

    /** Appends all values to {@code enforcement_literal}. */
    public Builder addAllEnforcementLiteral(Iterable<? extends Integer> values) {
      if (enforcementLiteral_ == null) {
        enforcementLiteral_ = new java.util.ArrayList<>();
      }
      for (Integer v : values) {
        enforcementLiteral_.add(v);
      }
      return this;
    }

    /** Removes all values of {@code enforcement_literal}. */
    public Builder clearEnforcementLiteral() {
      enforcementLiteral_ = null;
      return this;
    }

    /** Sets {@code bool_or}. */
    public Builder setBoolOr(BoolArgumentProto value) {
      if (value == null) {
        throw new NullPointerException();
      }
      constraintCase_ = 3;
      constraint_ = value.immutableCopy();
      return this;
    }

    /** Sets {@code bool_or} to the content of a builder. */
    public Builder setBoolOr(BoolArgumentProto.Builder builderForValue) {
      return setBoolOr(builderForValue.build());
    }

    /** Merges {@code value} into {@code bool_or}. */
    public Builder mergeBoolOr(BoolArgumentProto value) {
      if (constraintCase_ == 3) {
        getBoolOrBuilder().mergeFrom(value);
      } else {
        setBoolOr(value.immutableCopy());
      }
      return this;
    }

    /** Clears {@code bool_or}. */
    public Builder clearBoolOr() {
      if (constraintCase_ == 3) {
        constraintCase_ = 0;
        constraint_ = null;
      }
      return this;
    }

    /** Returns a builder for {@code bool_or}, setting it if needed; changes to it are reflected in this builder. */
    public BoolArgumentProto.Builder getBoolOrBuilder() {
      if (constraintCase_ == 3 && (BoolArgumentProto) constraint_ instanceof BoolArgumentProto.Builder) {
        return (BoolArgumentProto.Builder) (BoolArgumentProto) constraint_;
      }
      BoolArgumentProto.Builder b = constraintCase_ == 3 ? BoolArgumentProto.newBuilder((BoolArgumentProto) constraint_) : BoolArgumentProto.newBuilder();
      setBoolOrRaw(b);
      return b;
    }

    private void setBoolOrRaw(BoolArgumentProto.Builder b) {
      constraintCase_ = 3;
      constraint_ = b;
    }

    /** Sets {@code bool_and}. */
    public Builder setBoolAnd(BoolArgumentProto value) {
      if (value == null) {
        throw new NullPointerException();
      }
      constraintCase_ = 4;
      constraint_ = value.immutableCopy();
      return this;
    }

    /** Sets {@code bool_and} to the content of a builder. */
    public Builder setBoolAnd(BoolArgumentProto.Builder builderForValue) {
      return setBoolAnd(builderForValue.build());
    }

    /** Merges {@code value} into {@code bool_and}. */
    public Builder mergeBoolAnd(BoolArgumentProto value) {
      if (constraintCase_ == 4) {
        getBoolAndBuilder().mergeFrom(value);
      } else {
        setBoolAnd(value.immutableCopy());
      }
      return this;
    }

    /** Clears {@code bool_and}. */
    public Builder clearBoolAnd() {
      if (constraintCase_ == 4) {
        constraintCase_ = 0;
        constraint_ = null;
      }
      return this;
    }

    /** Returns a builder for {@code bool_and}, setting it if needed; changes to it are reflected in this builder. */
    public BoolArgumentProto.Builder getBoolAndBuilder() {
      if (constraintCase_ == 4 && (BoolArgumentProto) constraint_ instanceof BoolArgumentProto.Builder) {
        return (BoolArgumentProto.Builder) (BoolArgumentProto) constraint_;
      }
      BoolArgumentProto.Builder b = constraintCase_ == 4 ? BoolArgumentProto.newBuilder((BoolArgumentProto) constraint_) : BoolArgumentProto.newBuilder();
      setBoolAndRaw(b);
      return b;
    }

    private void setBoolAndRaw(BoolArgumentProto.Builder b) {
      constraintCase_ = 4;
      constraint_ = b;
    }

    /** Sets {@code at_most_one}. */
    public Builder setAtMostOne(BoolArgumentProto value) {
      if (value == null) {
        throw new NullPointerException();
      }
      constraintCase_ = 26;
      constraint_ = value.immutableCopy();
      return this;
    }

    /** Sets {@code at_most_one} to the content of a builder. */
    public Builder setAtMostOne(BoolArgumentProto.Builder builderForValue) {
      return setAtMostOne(builderForValue.build());
    }

    /** Merges {@code value} into {@code at_most_one}. */
    public Builder mergeAtMostOne(BoolArgumentProto value) {
      if (constraintCase_ == 26) {
        getAtMostOneBuilder().mergeFrom(value);
      } else {
        setAtMostOne(value.immutableCopy());
      }
      return this;
    }

    /** Clears {@code at_most_one}. */
    public Builder clearAtMostOne() {
      if (constraintCase_ == 26) {
        constraintCase_ = 0;
        constraint_ = null;
      }
      return this;
    }

    /** Returns a builder for {@code at_most_one}, setting it if needed; changes to it are reflected in this builder. */
    public BoolArgumentProto.Builder getAtMostOneBuilder() {
      if (constraintCase_ == 26 && (BoolArgumentProto) constraint_ instanceof BoolArgumentProto.Builder) {
        return (BoolArgumentProto.Builder) (BoolArgumentProto) constraint_;
      }
      BoolArgumentProto.Builder b = constraintCase_ == 26 ? BoolArgumentProto.newBuilder((BoolArgumentProto) constraint_) : BoolArgumentProto.newBuilder();
      setAtMostOneRaw(b);
      return b;
    }

    private void setAtMostOneRaw(BoolArgumentProto.Builder b) {
      constraintCase_ = 26;
      constraint_ = b;
    }

    /** Sets {@code exactly_one}. */
    public Builder setExactlyOne(BoolArgumentProto value) {
      if (value == null) {
        throw new NullPointerException();
      }
      constraintCase_ = 29;
      constraint_ = value.immutableCopy();
      return this;
    }

    /** Sets {@code exactly_one} to the content of a builder. */
    public Builder setExactlyOne(BoolArgumentProto.Builder builderForValue) {
      return setExactlyOne(builderForValue.build());
    }

    /** Merges {@code value} into {@code exactly_one}. */
    public Builder mergeExactlyOne(BoolArgumentProto value) {
      if (constraintCase_ == 29) {
        getExactlyOneBuilder().mergeFrom(value);
      } else {
        setExactlyOne(value.immutableCopy());
      }
      return this;
    }

    /** Clears {@code exactly_one}. */
    public Builder clearExactlyOne() {
      if (constraintCase_ == 29) {
        constraintCase_ = 0;
        constraint_ = null;
      }
      return this;
    }

    /** Returns a builder for {@code exactly_one}, setting it if needed; changes to it are reflected in this builder. */
    public BoolArgumentProto.Builder getExactlyOneBuilder() {
      if (constraintCase_ == 29 && (BoolArgumentProto) constraint_ instanceof BoolArgumentProto.Builder) {
        return (BoolArgumentProto.Builder) (BoolArgumentProto) constraint_;
      }
      BoolArgumentProto.Builder b = constraintCase_ == 29 ? BoolArgumentProto.newBuilder((BoolArgumentProto) constraint_) : BoolArgumentProto.newBuilder();
      setExactlyOneRaw(b);
      return b;
    }

    private void setExactlyOneRaw(BoolArgumentProto.Builder b) {
      constraintCase_ = 29;
      constraint_ = b;
    }

    /** Sets {@code bool_xor}. */
    public Builder setBoolXor(BoolArgumentProto value) {
      if (value == null) {
        throw new NullPointerException();
      }
      constraintCase_ = 5;
      constraint_ = value.immutableCopy();
      return this;
    }

    /** Sets {@code bool_xor} to the content of a builder. */
    public Builder setBoolXor(BoolArgumentProto.Builder builderForValue) {
      return setBoolXor(builderForValue.build());
    }

    /** Merges {@code value} into {@code bool_xor}. */
    public Builder mergeBoolXor(BoolArgumentProto value) {
      if (constraintCase_ == 5) {
        getBoolXorBuilder().mergeFrom(value);
      } else {
        setBoolXor(value.immutableCopy());
      }
      return this;
    }

    /** Clears {@code bool_xor}. */
    public Builder clearBoolXor() {
      if (constraintCase_ == 5) {
        constraintCase_ = 0;
        constraint_ = null;
      }
      return this;
    }

    /** Returns a builder for {@code bool_xor}, setting it if needed; changes to it are reflected in this builder. */
    public BoolArgumentProto.Builder getBoolXorBuilder() {
      if (constraintCase_ == 5 && (BoolArgumentProto) constraint_ instanceof BoolArgumentProto.Builder) {
        return (BoolArgumentProto.Builder) (BoolArgumentProto) constraint_;
      }
      BoolArgumentProto.Builder b = constraintCase_ == 5 ? BoolArgumentProto.newBuilder((BoolArgumentProto) constraint_) : BoolArgumentProto.newBuilder();
      setBoolXorRaw(b);
      return b;
    }

    private void setBoolXorRaw(BoolArgumentProto.Builder b) {
      constraintCase_ = 5;
      constraint_ = b;
    }

    /** Sets {@code int_div}. */
    public Builder setIntDiv(LinearArgumentProto value) {
      if (value == null) {
        throw new NullPointerException();
      }
      constraintCase_ = 7;
      constraint_ = value.immutableCopy();
      return this;
    }

    /** Sets {@code int_div} to the content of a builder. */
    public Builder setIntDiv(LinearArgumentProto.Builder builderForValue) {
      return setIntDiv(builderForValue.build());
    }

    /** Merges {@code value} into {@code int_div}. */
    public Builder mergeIntDiv(LinearArgumentProto value) {
      if (constraintCase_ == 7) {
        getIntDivBuilder().mergeFrom(value);
      } else {
        setIntDiv(value.immutableCopy());
      }
      return this;
    }

    /** Clears {@code int_div}. */
    public Builder clearIntDiv() {
      if (constraintCase_ == 7) {
        constraintCase_ = 0;
        constraint_ = null;
      }
      return this;
    }

    /** Returns a builder for {@code int_div}, setting it if needed; changes to it are reflected in this builder. */
    public LinearArgumentProto.Builder getIntDivBuilder() {
      if (constraintCase_ == 7 && (LinearArgumentProto) constraint_ instanceof LinearArgumentProto.Builder) {
        return (LinearArgumentProto.Builder) (LinearArgumentProto) constraint_;
      }
      LinearArgumentProto.Builder b = constraintCase_ == 7 ? LinearArgumentProto.newBuilder((LinearArgumentProto) constraint_) : LinearArgumentProto.newBuilder();
      setIntDivRaw(b);
      return b;
    }

    private void setIntDivRaw(LinearArgumentProto.Builder b) {
      constraintCase_ = 7;
      constraint_ = b;
    }

    /** Sets {@code int_mod}. */
    public Builder setIntMod(LinearArgumentProto value) {
      if (value == null) {
        throw new NullPointerException();
      }
      constraintCase_ = 8;
      constraint_ = value.immutableCopy();
      return this;
    }

    /** Sets {@code int_mod} to the content of a builder. */
    public Builder setIntMod(LinearArgumentProto.Builder builderForValue) {
      return setIntMod(builderForValue.build());
    }

    /** Merges {@code value} into {@code int_mod}. */
    public Builder mergeIntMod(LinearArgumentProto value) {
      if (constraintCase_ == 8) {
        getIntModBuilder().mergeFrom(value);
      } else {
        setIntMod(value.immutableCopy());
      }
      return this;
    }

    /** Clears {@code int_mod}. */
    public Builder clearIntMod() {
      if (constraintCase_ == 8) {
        constraintCase_ = 0;
        constraint_ = null;
      }
      return this;
    }

    /** Returns a builder for {@code int_mod}, setting it if needed; changes to it are reflected in this builder. */
    public LinearArgumentProto.Builder getIntModBuilder() {
      if (constraintCase_ == 8 && (LinearArgumentProto) constraint_ instanceof LinearArgumentProto.Builder) {
        return (LinearArgumentProto.Builder) (LinearArgumentProto) constraint_;
      }
      LinearArgumentProto.Builder b = constraintCase_ == 8 ? LinearArgumentProto.newBuilder((LinearArgumentProto) constraint_) : LinearArgumentProto.newBuilder();
      setIntModRaw(b);
      return b;
    }

    private void setIntModRaw(LinearArgumentProto.Builder b) {
      constraintCase_ = 8;
      constraint_ = b;
    }

    /** Sets {@code int_prod}. */
    public Builder setIntProd(LinearArgumentProto value) {
      if (value == null) {
        throw new NullPointerException();
      }
      constraintCase_ = 11;
      constraint_ = value.immutableCopy();
      return this;
    }

    /** Sets {@code int_prod} to the content of a builder. */
    public Builder setIntProd(LinearArgumentProto.Builder builderForValue) {
      return setIntProd(builderForValue.build());
    }

    /** Merges {@code value} into {@code int_prod}. */
    public Builder mergeIntProd(LinearArgumentProto value) {
      if (constraintCase_ == 11) {
        getIntProdBuilder().mergeFrom(value);
      } else {
        setIntProd(value.immutableCopy());
      }
      return this;
    }

    /** Clears {@code int_prod}. */
    public Builder clearIntProd() {
      if (constraintCase_ == 11) {
        constraintCase_ = 0;
        constraint_ = null;
      }
      return this;
    }

    /** Returns a builder for {@code int_prod}, setting it if needed; changes to it are reflected in this builder. */
    public LinearArgumentProto.Builder getIntProdBuilder() {
      if (constraintCase_ == 11 && (LinearArgumentProto) constraint_ instanceof LinearArgumentProto.Builder) {
        return (LinearArgumentProto.Builder) (LinearArgumentProto) constraint_;
      }
      LinearArgumentProto.Builder b = constraintCase_ == 11 ? LinearArgumentProto.newBuilder((LinearArgumentProto) constraint_) : LinearArgumentProto.newBuilder();
      setIntProdRaw(b);
      return b;
    }

    private void setIntProdRaw(LinearArgumentProto.Builder b) {
      constraintCase_ = 11;
      constraint_ = b;
    }

    /** Sets {@code lin_max}. */
    public Builder setLinMax(LinearArgumentProto value) {
      if (value == null) {
        throw new NullPointerException();
      }
      constraintCase_ = 27;
      constraint_ = value.immutableCopy();
      return this;
    }

    /** Sets {@code lin_max} to the content of a builder. */
    public Builder setLinMax(LinearArgumentProto.Builder builderForValue) {
      return setLinMax(builderForValue.build());
    }

    /** Merges {@code value} into {@code lin_max}. */
    public Builder mergeLinMax(LinearArgumentProto value) {
      if (constraintCase_ == 27) {
        getLinMaxBuilder().mergeFrom(value);
      } else {
        setLinMax(value.immutableCopy());
      }
      return this;
    }

    /** Clears {@code lin_max}. */
    public Builder clearLinMax() {
      if (constraintCase_ == 27) {
        constraintCase_ = 0;
        constraint_ = null;
      }
      return this;
    }

    /** Returns a builder for {@code lin_max}, setting it if needed; changes to it are reflected in this builder. */
    public LinearArgumentProto.Builder getLinMaxBuilder() {
      if (constraintCase_ == 27 && (LinearArgumentProto) constraint_ instanceof LinearArgumentProto.Builder) {
        return (LinearArgumentProto.Builder) (LinearArgumentProto) constraint_;
      }
      LinearArgumentProto.Builder b = constraintCase_ == 27 ? LinearArgumentProto.newBuilder((LinearArgumentProto) constraint_) : LinearArgumentProto.newBuilder();
      setLinMaxRaw(b);
      return b;
    }

    private void setLinMaxRaw(LinearArgumentProto.Builder b) {
      constraintCase_ = 27;
      constraint_ = b;
    }

    /** Sets {@code linear}. */
    public Builder setLinear(LinearConstraintProto value) {
      if (value == null) {
        throw new NullPointerException();
      }
      constraintCase_ = 12;
      constraint_ = value.immutableCopy();
      return this;
    }

    /** Sets {@code linear} to the content of a builder. */
    public Builder setLinear(LinearConstraintProto.Builder builderForValue) {
      return setLinear(builderForValue.build());
    }

    /** Merges {@code value} into {@code linear}. */
    public Builder mergeLinear(LinearConstraintProto value) {
      if (constraintCase_ == 12) {
        getLinearBuilder().mergeFrom(value);
      } else {
        setLinear(value.immutableCopy());
      }
      return this;
    }

    /** Clears {@code linear}. */
    public Builder clearLinear() {
      if (constraintCase_ == 12) {
        constraintCase_ = 0;
        constraint_ = null;
      }
      return this;
    }

    /** Returns a builder for {@code linear}, setting it if needed; changes to it are reflected in this builder. */
    public LinearConstraintProto.Builder getLinearBuilder() {
      if (constraintCase_ == 12 && (LinearConstraintProto) constraint_ instanceof LinearConstraintProto.Builder) {
        return (LinearConstraintProto.Builder) (LinearConstraintProto) constraint_;
      }
      LinearConstraintProto.Builder b = constraintCase_ == 12 ? LinearConstraintProto.newBuilder((LinearConstraintProto) constraint_) : LinearConstraintProto.newBuilder();
      setLinearRaw(b);
      return b;
    }

    private void setLinearRaw(LinearConstraintProto.Builder b) {
      constraintCase_ = 12;
      constraint_ = b;
    }

    /** Sets {@code all_diff}. */
    public Builder setAllDiff(AllDifferentConstraintProto value) {
      if (value == null) {
        throw new NullPointerException();
      }
      constraintCase_ = 13;
      constraint_ = value.immutableCopy();
      return this;
    }

    /** Sets {@code all_diff} to the content of a builder. */
    public Builder setAllDiff(AllDifferentConstraintProto.Builder builderForValue) {
      return setAllDiff(builderForValue.build());
    }

    /** Merges {@code value} into {@code all_diff}. */
    public Builder mergeAllDiff(AllDifferentConstraintProto value) {
      if (constraintCase_ == 13) {
        getAllDiffBuilder().mergeFrom(value);
      } else {
        setAllDiff(value.immutableCopy());
      }
      return this;
    }

    /** Clears {@code all_diff}. */
    public Builder clearAllDiff() {
      if (constraintCase_ == 13) {
        constraintCase_ = 0;
        constraint_ = null;
      }
      return this;
    }

    /** Returns a builder for {@code all_diff}, setting it if needed; changes to it are reflected in this builder. */
    public AllDifferentConstraintProto.Builder getAllDiffBuilder() {
      if (constraintCase_ == 13 && (AllDifferentConstraintProto) constraint_ instanceof AllDifferentConstraintProto.Builder) {
        return (AllDifferentConstraintProto.Builder) (AllDifferentConstraintProto) constraint_;
      }
      AllDifferentConstraintProto.Builder b = constraintCase_ == 13 ? AllDifferentConstraintProto.newBuilder((AllDifferentConstraintProto) constraint_) : AllDifferentConstraintProto.newBuilder();
      setAllDiffRaw(b);
      return b;
    }

    private void setAllDiffRaw(AllDifferentConstraintProto.Builder b) {
      constraintCase_ = 13;
      constraint_ = b;
    }

    /** Sets {@code element}. */
    public Builder setElement(ElementConstraintProto value) {
      if (value == null) {
        throw new NullPointerException();
      }
      constraintCase_ = 14;
      constraint_ = value.immutableCopy();
      return this;
    }

    /** Sets {@code element} to the content of a builder. */
    public Builder setElement(ElementConstraintProto.Builder builderForValue) {
      return setElement(builderForValue.build());
    }

    /** Merges {@code value} into {@code element}. */
    public Builder mergeElement(ElementConstraintProto value) {
      if (constraintCase_ == 14) {
        getElementBuilder().mergeFrom(value);
      } else {
        setElement(value.immutableCopy());
      }
      return this;
    }

    /** Clears {@code element}. */
    public Builder clearElement() {
      if (constraintCase_ == 14) {
        constraintCase_ = 0;
        constraint_ = null;
      }
      return this;
    }

    /** Returns a builder for {@code element}, setting it if needed; changes to it are reflected in this builder. */
    public ElementConstraintProto.Builder getElementBuilder() {
      if (constraintCase_ == 14 && (ElementConstraintProto) constraint_ instanceof ElementConstraintProto.Builder) {
        return (ElementConstraintProto.Builder) (ElementConstraintProto) constraint_;
      }
      ElementConstraintProto.Builder b = constraintCase_ == 14 ? ElementConstraintProto.newBuilder((ElementConstraintProto) constraint_) : ElementConstraintProto.newBuilder();
      setElementRaw(b);
      return b;
    }

    private void setElementRaw(ElementConstraintProto.Builder b) {
      constraintCase_ = 14;
      constraint_ = b;
    }

    /** Sets {@code circuit}. */
    public Builder setCircuit(CircuitConstraintProto value) {
      if (value == null) {
        throw new NullPointerException();
      }
      constraintCase_ = 15;
      constraint_ = value.immutableCopy();
      return this;
    }

    /** Sets {@code circuit} to the content of a builder. */
    public Builder setCircuit(CircuitConstraintProto.Builder builderForValue) {
      return setCircuit(builderForValue.build());
    }

    /** Merges {@code value} into {@code circuit}. */
    public Builder mergeCircuit(CircuitConstraintProto value) {
      if (constraintCase_ == 15) {
        getCircuitBuilder().mergeFrom(value);
      } else {
        setCircuit(value.immutableCopy());
      }
      return this;
    }

    /** Clears {@code circuit}. */
    public Builder clearCircuit() {
      if (constraintCase_ == 15) {
        constraintCase_ = 0;
        constraint_ = null;
      }
      return this;
    }

    /** Returns a builder for {@code circuit}, setting it if needed; changes to it are reflected in this builder. */
    public CircuitConstraintProto.Builder getCircuitBuilder() {
      if (constraintCase_ == 15 && (CircuitConstraintProto) constraint_ instanceof CircuitConstraintProto.Builder) {
        return (CircuitConstraintProto.Builder) (CircuitConstraintProto) constraint_;
      }
      CircuitConstraintProto.Builder b = constraintCase_ == 15 ? CircuitConstraintProto.newBuilder((CircuitConstraintProto) constraint_) : CircuitConstraintProto.newBuilder();
      setCircuitRaw(b);
      return b;
    }

    private void setCircuitRaw(CircuitConstraintProto.Builder b) {
      constraintCase_ = 15;
      constraint_ = b;
    }

    /** Sets {@code routes}. */
    public Builder setRoutes(RoutesConstraintProto value) {
      if (value == null) {
        throw new NullPointerException();
      }
      constraintCase_ = 23;
      constraint_ = value.immutableCopy();
      return this;
    }

    /** Sets {@code routes} to the content of a builder. */
    public Builder setRoutes(RoutesConstraintProto.Builder builderForValue) {
      return setRoutes(builderForValue.build());
    }

    /** Merges {@code value} into {@code routes}. */
    public Builder mergeRoutes(RoutesConstraintProto value) {
      if (constraintCase_ == 23) {
        getRoutesBuilder().mergeFrom(value);
      } else {
        setRoutes(value.immutableCopy());
      }
      return this;
    }

    /** Clears {@code routes}. */
    public Builder clearRoutes() {
      if (constraintCase_ == 23) {
        constraintCase_ = 0;
        constraint_ = null;
      }
      return this;
    }

    /** Returns a builder for {@code routes}, setting it if needed; changes to it are reflected in this builder. */
    public RoutesConstraintProto.Builder getRoutesBuilder() {
      if (constraintCase_ == 23 && (RoutesConstraintProto) constraint_ instanceof RoutesConstraintProto.Builder) {
        return (RoutesConstraintProto.Builder) (RoutesConstraintProto) constraint_;
      }
      RoutesConstraintProto.Builder b = constraintCase_ == 23 ? RoutesConstraintProto.newBuilder((RoutesConstraintProto) constraint_) : RoutesConstraintProto.newBuilder();
      setRoutesRaw(b);
      return b;
    }

    private void setRoutesRaw(RoutesConstraintProto.Builder b) {
      constraintCase_ = 23;
      constraint_ = b;
    }

    /** Sets {@code table}. */
    public Builder setTable(TableConstraintProto value) {
      if (value == null) {
        throw new NullPointerException();
      }
      constraintCase_ = 16;
      constraint_ = value.immutableCopy();
      return this;
    }

    /** Sets {@code table} to the content of a builder. */
    public Builder setTable(TableConstraintProto.Builder builderForValue) {
      return setTable(builderForValue.build());
    }

    /** Merges {@code value} into {@code table}. */
    public Builder mergeTable(TableConstraintProto value) {
      if (constraintCase_ == 16) {
        getTableBuilder().mergeFrom(value);
      } else {
        setTable(value.immutableCopy());
      }
      return this;
    }

    /** Clears {@code table}. */
    public Builder clearTable() {
      if (constraintCase_ == 16) {
        constraintCase_ = 0;
        constraint_ = null;
      }
      return this;
    }

    /** Returns a builder for {@code table}, setting it if needed; changes to it are reflected in this builder. */
    public TableConstraintProto.Builder getTableBuilder() {
      if (constraintCase_ == 16 && (TableConstraintProto) constraint_ instanceof TableConstraintProto.Builder) {
        return (TableConstraintProto.Builder) (TableConstraintProto) constraint_;
      }
      TableConstraintProto.Builder b = constraintCase_ == 16 ? TableConstraintProto.newBuilder((TableConstraintProto) constraint_) : TableConstraintProto.newBuilder();
      setTableRaw(b);
      return b;
    }

    private void setTableRaw(TableConstraintProto.Builder b) {
      constraintCase_ = 16;
      constraint_ = b;
    }

    /** Sets {@code automaton}. */
    public Builder setAutomaton(AutomatonConstraintProto value) {
      if (value == null) {
        throw new NullPointerException();
      }
      constraintCase_ = 17;
      constraint_ = value.immutableCopy();
      return this;
    }

    /** Sets {@code automaton} to the content of a builder. */
    public Builder setAutomaton(AutomatonConstraintProto.Builder builderForValue) {
      return setAutomaton(builderForValue.build());
    }

    /** Merges {@code value} into {@code automaton}. */
    public Builder mergeAutomaton(AutomatonConstraintProto value) {
      if (constraintCase_ == 17) {
        getAutomatonBuilder().mergeFrom(value);
      } else {
        setAutomaton(value.immutableCopy());
      }
      return this;
    }

    /** Clears {@code automaton}. */
    public Builder clearAutomaton() {
      if (constraintCase_ == 17) {
        constraintCase_ = 0;
        constraint_ = null;
      }
      return this;
    }

    /** Returns a builder for {@code automaton}, setting it if needed; changes to it are reflected in this builder. */
    public AutomatonConstraintProto.Builder getAutomatonBuilder() {
      if (constraintCase_ == 17 && (AutomatonConstraintProto) constraint_ instanceof AutomatonConstraintProto.Builder) {
        return (AutomatonConstraintProto.Builder) (AutomatonConstraintProto) constraint_;
      }
      AutomatonConstraintProto.Builder b = constraintCase_ == 17 ? AutomatonConstraintProto.newBuilder((AutomatonConstraintProto) constraint_) : AutomatonConstraintProto.newBuilder();
      setAutomatonRaw(b);
      return b;
    }

    private void setAutomatonRaw(AutomatonConstraintProto.Builder b) {
      constraintCase_ = 17;
      constraint_ = b;
    }

    /** Sets {@code inverse}. */
    public Builder setInverse(InverseConstraintProto value) {
      if (value == null) {
        throw new NullPointerException();
      }
      constraintCase_ = 18;
      constraint_ = value.immutableCopy();
      return this;
    }

    /** Sets {@code inverse} to the content of a builder. */
    public Builder setInverse(InverseConstraintProto.Builder builderForValue) {
      return setInverse(builderForValue.build());
    }

    /** Merges {@code value} into {@code inverse}. */
    public Builder mergeInverse(InverseConstraintProto value) {
      if (constraintCase_ == 18) {
        getInverseBuilder().mergeFrom(value);
      } else {
        setInverse(value.immutableCopy());
      }
      return this;
    }

    /** Clears {@code inverse}. */
    public Builder clearInverse() {
      if (constraintCase_ == 18) {
        constraintCase_ = 0;
        constraint_ = null;
      }
      return this;
    }

    /** Returns a builder for {@code inverse}, setting it if needed; changes to it are reflected in this builder. */
    public InverseConstraintProto.Builder getInverseBuilder() {
      if (constraintCase_ == 18 && (InverseConstraintProto) constraint_ instanceof InverseConstraintProto.Builder) {
        return (InverseConstraintProto.Builder) (InverseConstraintProto) constraint_;
      }
      InverseConstraintProto.Builder b = constraintCase_ == 18 ? InverseConstraintProto.newBuilder((InverseConstraintProto) constraint_) : InverseConstraintProto.newBuilder();
      setInverseRaw(b);
      return b;
    }

    private void setInverseRaw(InverseConstraintProto.Builder b) {
      constraintCase_ = 18;
      constraint_ = b;
    }

    /** Sets {@code reservoir}. */
    public Builder setReservoir(ReservoirConstraintProto value) {
      if (value == null) {
        throw new NullPointerException();
      }
      constraintCase_ = 24;
      constraint_ = value.immutableCopy();
      return this;
    }

    /** Sets {@code reservoir} to the content of a builder. */
    public Builder setReservoir(ReservoirConstraintProto.Builder builderForValue) {
      return setReservoir(builderForValue.build());
    }

    /** Merges {@code value} into {@code reservoir}. */
    public Builder mergeReservoir(ReservoirConstraintProto value) {
      if (constraintCase_ == 24) {
        getReservoirBuilder().mergeFrom(value);
      } else {
        setReservoir(value.immutableCopy());
      }
      return this;
    }

    /** Clears {@code reservoir}. */
    public Builder clearReservoir() {
      if (constraintCase_ == 24) {
        constraintCase_ = 0;
        constraint_ = null;
      }
      return this;
    }

    /** Returns a builder for {@code reservoir}, setting it if needed; changes to it are reflected in this builder. */
    public ReservoirConstraintProto.Builder getReservoirBuilder() {
      if (constraintCase_ == 24 && (ReservoirConstraintProto) constraint_ instanceof ReservoirConstraintProto.Builder) {
        return (ReservoirConstraintProto.Builder) (ReservoirConstraintProto) constraint_;
      }
      ReservoirConstraintProto.Builder b = constraintCase_ == 24 ? ReservoirConstraintProto.newBuilder((ReservoirConstraintProto) constraint_) : ReservoirConstraintProto.newBuilder();
      setReservoirRaw(b);
      return b;
    }

    private void setReservoirRaw(ReservoirConstraintProto.Builder b) {
      constraintCase_ = 24;
      constraint_ = b;
    }

    /** Sets {@code interval}. */
    public Builder setInterval(IntervalConstraintProto value) {
      if (value == null) {
        throw new NullPointerException();
      }
      constraintCase_ = 19;
      constraint_ = value.immutableCopy();
      return this;
    }

    /** Sets {@code interval} to the content of a builder. */
    public Builder setInterval(IntervalConstraintProto.Builder builderForValue) {
      return setInterval(builderForValue.build());
    }

    /** Merges {@code value} into {@code interval}. */
    public Builder mergeInterval(IntervalConstraintProto value) {
      if (constraintCase_ == 19) {
        getIntervalBuilder().mergeFrom(value);
      } else {
        setInterval(value.immutableCopy());
      }
      return this;
    }

    /** Clears {@code interval}. */
    public Builder clearInterval() {
      if (constraintCase_ == 19) {
        constraintCase_ = 0;
        constraint_ = null;
      }
      return this;
    }

    /** Returns a builder for {@code interval}, setting it if needed; changes to it are reflected in this builder. */
    public IntervalConstraintProto.Builder getIntervalBuilder() {
      if (constraintCase_ == 19 && (IntervalConstraintProto) constraint_ instanceof IntervalConstraintProto.Builder) {
        return (IntervalConstraintProto.Builder) (IntervalConstraintProto) constraint_;
      }
      IntervalConstraintProto.Builder b = constraintCase_ == 19 ? IntervalConstraintProto.newBuilder((IntervalConstraintProto) constraint_) : IntervalConstraintProto.newBuilder();
      setIntervalRaw(b);
      return b;
    }

    private void setIntervalRaw(IntervalConstraintProto.Builder b) {
      constraintCase_ = 19;
      constraint_ = b;
    }

    /** Sets {@code no_overlap}. */
    public Builder setNoOverlap(NoOverlapConstraintProto value) {
      if (value == null) {
        throw new NullPointerException();
      }
      constraintCase_ = 20;
      constraint_ = value.immutableCopy();
      return this;
    }

    /** Sets {@code no_overlap} to the content of a builder. */
    public Builder setNoOverlap(NoOverlapConstraintProto.Builder builderForValue) {
      return setNoOverlap(builderForValue.build());
    }

    /** Merges {@code value} into {@code no_overlap}. */
    public Builder mergeNoOverlap(NoOverlapConstraintProto value) {
      if (constraintCase_ == 20) {
        getNoOverlapBuilder().mergeFrom(value);
      } else {
        setNoOverlap(value.immutableCopy());
      }
      return this;
    }

    /** Clears {@code no_overlap}. */
    public Builder clearNoOverlap() {
      if (constraintCase_ == 20) {
        constraintCase_ = 0;
        constraint_ = null;
      }
      return this;
    }

    /** Returns a builder for {@code no_overlap}, setting it if needed; changes to it are reflected in this builder. */
    public NoOverlapConstraintProto.Builder getNoOverlapBuilder() {
      if (constraintCase_ == 20 && (NoOverlapConstraintProto) constraint_ instanceof NoOverlapConstraintProto.Builder) {
        return (NoOverlapConstraintProto.Builder) (NoOverlapConstraintProto) constraint_;
      }
      NoOverlapConstraintProto.Builder b = constraintCase_ == 20 ? NoOverlapConstraintProto.newBuilder((NoOverlapConstraintProto) constraint_) : NoOverlapConstraintProto.newBuilder();
      setNoOverlapRaw(b);
      return b;
    }

    private void setNoOverlapRaw(NoOverlapConstraintProto.Builder b) {
      constraintCase_ = 20;
      constraint_ = b;
    }

    /** Sets {@code no_overlap_2d}. */
    public Builder setNoOverlap2D(NoOverlap2DConstraintProto value) {
      if (value == null) {
        throw new NullPointerException();
      }
      constraintCase_ = 21;
      constraint_ = value.immutableCopy();
      return this;
    }

    /** Sets {@code no_overlap_2d} to the content of a builder. */
    public Builder setNoOverlap2D(NoOverlap2DConstraintProto.Builder builderForValue) {
      return setNoOverlap2D(builderForValue.build());
    }

    /** Merges {@code value} into {@code no_overlap_2d}. */
    public Builder mergeNoOverlap2D(NoOverlap2DConstraintProto value) {
      if (constraintCase_ == 21) {
        getNoOverlap2DBuilder().mergeFrom(value);
      } else {
        setNoOverlap2D(value.immutableCopy());
      }
      return this;
    }

    /** Clears {@code no_overlap_2d}. */
    public Builder clearNoOverlap2D() {
      if (constraintCase_ == 21) {
        constraintCase_ = 0;
        constraint_ = null;
      }
      return this;
    }

    /** Returns a builder for {@code no_overlap_2d}, setting it if needed; changes to it are reflected in this builder. */
    public NoOverlap2DConstraintProto.Builder getNoOverlap2DBuilder() {
      if (constraintCase_ == 21 && (NoOverlap2DConstraintProto) constraint_ instanceof NoOverlap2DConstraintProto.Builder) {
        return (NoOverlap2DConstraintProto.Builder) (NoOverlap2DConstraintProto) constraint_;
      }
      NoOverlap2DConstraintProto.Builder b = constraintCase_ == 21 ? NoOverlap2DConstraintProto.newBuilder((NoOverlap2DConstraintProto) constraint_) : NoOverlap2DConstraintProto.newBuilder();
      setNoOverlap2DRaw(b);
      return b;
    }

    private void setNoOverlap2DRaw(NoOverlap2DConstraintProto.Builder b) {
      constraintCase_ = 21;
      constraint_ = b;
    }

    /** Sets {@code cumulative}. */
    public Builder setCumulative(CumulativeConstraintProto value) {
      if (value == null) {
        throw new NullPointerException();
      }
      constraintCase_ = 22;
      constraint_ = value.immutableCopy();
      return this;
    }

    /** Sets {@code cumulative} to the content of a builder. */
    public Builder setCumulative(CumulativeConstraintProto.Builder builderForValue) {
      return setCumulative(builderForValue.build());
    }

    /** Merges {@code value} into {@code cumulative}. */
    public Builder mergeCumulative(CumulativeConstraintProto value) {
      if (constraintCase_ == 22) {
        getCumulativeBuilder().mergeFrom(value);
      } else {
        setCumulative(value.immutableCopy());
      }
      return this;
    }

    /** Clears {@code cumulative}. */
    public Builder clearCumulative() {
      if (constraintCase_ == 22) {
        constraintCase_ = 0;
        constraint_ = null;
      }
      return this;
    }

    /** Returns a builder for {@code cumulative}, setting it if needed; changes to it are reflected in this builder. */
    public CumulativeConstraintProto.Builder getCumulativeBuilder() {
      if (constraintCase_ == 22 && (CumulativeConstraintProto) constraint_ instanceof CumulativeConstraintProto.Builder) {
        return (CumulativeConstraintProto.Builder) (CumulativeConstraintProto) constraint_;
      }
      CumulativeConstraintProto.Builder b = constraintCase_ == 22 ? CumulativeConstraintProto.newBuilder((CumulativeConstraintProto) constraint_) : CumulativeConstraintProto.newBuilder();
      setCumulativeRaw(b);
      return b;
    }

    private void setCumulativeRaw(CumulativeConstraintProto.Builder b) {
      constraintCase_ = 22;
      constraint_ = b;
    }

    /** Sets {@code dummy_constraint}. */
    public Builder setDummyConstraint(ListOfVariablesProto value) {
      if (value == null) {
        throw new NullPointerException();
      }
      constraintCase_ = 30;
      constraint_ = value.immutableCopy();
      return this;
    }

    /** Sets {@code dummy_constraint} to the content of a builder. */
    public Builder setDummyConstraint(ListOfVariablesProto.Builder builderForValue) {
      return setDummyConstraint(builderForValue.build());
    }

    /** Merges {@code value} into {@code dummy_constraint}. */
    public Builder mergeDummyConstraint(ListOfVariablesProto value) {
      if (constraintCase_ == 30) {
        getDummyConstraintBuilder().mergeFrom(value);
      } else {
        setDummyConstraint(value.immutableCopy());
      }
      return this;
    }

    /** Clears {@code dummy_constraint}. */
    public Builder clearDummyConstraint() {
      if (constraintCase_ == 30) {
        constraintCase_ = 0;
        constraint_ = null;
      }
      return this;
    }

    /** Returns a builder for {@code dummy_constraint}, setting it if needed; changes to it are reflected in this builder. */
    public ListOfVariablesProto.Builder getDummyConstraintBuilder() {
      if (constraintCase_ == 30 && (ListOfVariablesProto) constraint_ instanceof ListOfVariablesProto.Builder) {
        return (ListOfVariablesProto.Builder) (ListOfVariablesProto) constraint_;
      }
      ListOfVariablesProto.Builder b = constraintCase_ == 30 ? ListOfVariablesProto.newBuilder((ListOfVariablesProto) constraint_) : ListOfVariablesProto.newBuilder();
      setDummyConstraintRaw(b);
      return b;
    }

    private void setDummyConstraintRaw(ListOfVariablesProto.Builder b) {
      constraintCase_ = 30;
      constraint_ = b;
    }

  }
}
