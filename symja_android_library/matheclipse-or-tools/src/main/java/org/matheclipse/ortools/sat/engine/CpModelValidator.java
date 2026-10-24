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
// Modified for the Symja matheclipse-or-tools module: ported from ortools/sat/cp_model_checker.cc C++ to Java, see NOTICE in the module root.

package org.matheclipse.ortools.sat.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.matheclipse.ortools.sat.ConstraintProto;
import org.matheclipse.ortools.sat.CpModelProto;
import org.matheclipse.ortools.sat.CpObjectiveProto;
import org.matheclipse.ortools.sat.DecisionStrategyProto;
import org.matheclipse.ortools.sat.ElementConstraintProto;
import org.matheclipse.ortools.sat.FloatObjectiveProto;
import org.matheclipse.ortools.sat.IntegerVariableProto;
import org.matheclipse.ortools.sat.IntervalConstraintProto;
import org.matheclipse.ortools.sat.LinearExpressionProto;
import org.matheclipse.ortools.sat.PartialVariableAssignment;
import org.matheclipse.ortools.sat.RoutesConstraintProto;
import org.matheclipse.ortools.sat.TableConstraintProto;
import org.matheclipse.ortools.sat.TextFormat;
import org.matheclipse.ortools.util.Domain;

/**
 * Validation of a {@link CpModelProto}: returns an empty string for a valid model and otherwise a
 * message describing the first problem, with the messages of the C++ CP-SAT model checker.
 */
public final class CpModelValidator {
  private static final long MAX = Long.MAX_VALUE;

  private final CpModelProto model;

  private CpModelValidator(CpModelProto model) {
    this.model = model;
  }

  /** Returns "" if the model is valid, otherwise an error message. */
  public static String validate(CpModelProto model) {
    return new CpModelValidator(model).validateModel();
  }

  /** Same as {@link #validate(CpModelProto)} with the extra checks of the floating objective. */
  public static String validateInput(CpModelProto model, double maxValidMagnitude) {
    String r = validate(model);
    if (!r.isEmpty()) {
      return r;
    }
    if (model.hasFloatingPointObjective()) {
      return new CpModelValidator(model).validateFloatingPointObjective(maxValidMagnitude,
          model.getFloatingPointObjective());
    }
    return "";
  }

  private static String sds(org.matheclipse.ortools.sat.ProtoMessage m) {
    return TextFormat.shortDebugString(m);
  }

  private static String ds(org.matheclipse.ortools.sat.ProtoMessage m) {
    return TextFormat.printToString(m);
  }

  private static boolean domainInProtoIsValid(List<Long> d) {
    if (d.size() % 2 != 0) {
      return false;
    }
    for (int i = 0; i < d.size(); i += 2) {
      if (d.get(i) > d.get(i + 1)) {
        return false;
      }
      if (i + 2 < d.size()) {
        long end = d.get(i + 1);
        if (end >= d.get(i + 2) || end + 1 >= d.get(i + 2)) {
          return false;
        }
      }
    }
    return true;
  }

  private boolean variableReferenceIsValid(int reference) {
    if (reference >= model.getVariablesCount()) {
      return false;
    }
    return reference >= -model.getVariablesCount();
  }

  private boolean variableIndexIsValid(int var) {
    return var >= 0 && var < model.getVariablesCount();
  }

  private boolean literalReferenceIsValid(int reference) {
    if (!variableReferenceIsValid(reference)) {
      return false;
    }
    IntegerVariableProto v = model.getVariables(positiveRef(reference));
    if (v.getDomainCount() == 0) {
      return false;
    }
    return v.getDomain(0) >= 0 && v.getDomain(v.getDomainCount() - 1) <= 1;
  }

  static int positiveRef(int ref) {
    return ref >= 0 ? ref : -ref - 1;
  }

  private String validateIntegerVariable(int v) {
    IntegerVariableProto proto = model.getVariables(v);
    if (proto.getDomainCount() == 0) {
      return "var #" + v + " has no domain(): " + sds(proto);
    }
    if (proto.getDomainCount() % 2 != 0) {
      return "var #" + v + " has an odd domain() size: " + sds(proto);
    }
    if (!domainInProtoIsValid(proto.getDomainList())) {
      return "var #" + v + " has an invalid domain() format: " + sds(proto);
    }
    long lb = proto.getDomain(0);
    long ub = proto.getDomain(proto.getDomainCount() - 1);
    if (lb < -MAX / 2 || ub > MAX / 2) {
      return "var #" + v + " domain does not fall in [-kint64max / 2, kint64max / 2]. " + sds(proto);
    }
    if (lb < 0 && lb + MAX < ub) {
      return "var #" + v
          + " has a domain that is too large, i.e. |UB - LB| overflows an int64_t: " + sds(proto);
    }
    return "";
  }

  private long minOfRef(int ref) {
    IntegerVariableProto v = model.getVariables(positiveRef(ref));
    return ref >= 0 ? v.getDomain(0) : -v.getDomain(v.getDomainCount() - 1);
  }

  private long maxOfRef(int ref) {
    IntegerVariableProto v = model.getVariables(positiveRef(ref));
    return ref >= 0 ? v.getDomain(v.getDomainCount() - 1) : -v.getDomain(0);
  }

  long minOfExpression(LinearExpressionProto e) {
    long sum = e.getOffset();
    for (int i = 0; i < e.getVarsCount(); i++) {
      long c = e.getCoeffs(i);
      sum = Domain.capAdd(sum, c >= 0 ? Domain.capProd(minOfRef(e.getVars(i)), c)
          : Domain.capProd(maxOfRef(e.getVars(i)), c));
    }
    return sum;
  }

  long maxOfExpression(LinearExpressionProto e) {
    long sum = e.getOffset();
    for (int i = 0; i < e.getVarsCount(); i++) {
      long c = e.getCoeffs(i);
      sum = Domain.capAdd(sum, c >= 0 ? Domain.capProd(maxOfRef(e.getVars(i)), c)
          : Domain.capProd(minOfRef(e.getVars(i)), c));
    }
    return sum;
  }

  private Domain domainOfRef(int ref) {
    Domain d = SolutionChecker.domainOf(model.getVariables(positiveRef(ref)));
    return ref >= 0 ? d : d.negation();
  }

  private static boolean atMinOrMax(long v) {
    return v == Long.MIN_VALUE || v == Long.MAX_VALUE;
  }

  /**
   * Port of PossibleIntegerOverflow() with the term check of LinearOverflowChecker::AddTerm();
   * bounds receives {sum_min, sum_max} when not null. The offset must be in
   * [kint64min / 2, kint64max / 2]; the variables are positive references (the callers check
   * this first).
   */
  boolean possibleIntegerOverflow(List<Integer> vars, List<Long> coeffs, long offset,
      long[] bounds) {
    if (offset < Long.MIN_VALUE / 2 || offset > MAX / 2) {
      return true;
    }
    long sumMin = -Math.abs(offset);
    long sumMax = Math.abs(offset);
    for (int i = 0; i < vars.size(); i++) {
      int ref = vars.get(i);
      IntegerVariableProto v = model.getVariables(positiveRef(ref));
      long minDomain = v.getDomain(0);
      long maxDomain = v.getDomain(v.getDomainCount() - 1);
      long coeff = coeffs.get(i);
      if (coeff == Long.MIN_VALUE) {
        return true;
      }
      if (ref < 0) {
        coeff = -coeff;
      }
      long prod1 = Domain.capProd(minDomain, coeff);
      long prod2 = Domain.capProd(maxDomain, coeff);
      if (atMinOrMax(prod1) || atMinOrMax(prod2)) {
        return true;
      }
      // min/max with zero disallow "alternative" terms, so that the computation cannot overflow
      // in another order
      sumMin = Domain.capAdd(sumMin, Math.min(0, Math.min(prod1, prod2)));
      sumMax = Domain.capAdd(sumMax, Math.max(0, Math.max(prod1, prod2)));
      // max - min must not overflow either; symmetric so that expr and -expr behave the same
      if (sumMin < -MAX / 2 || sumMax > MAX / 2) {
        return true;
      }
    }
    if (bounds != null) {
      bounds[0] = sumMin;
      bounds[1] = sumMax;
    }
    return false;
  }

  private String validateLinearExpression(LinearExpressionProto e) {
    if (e.getCoeffsCount() != e.getVarsCount()) {
      return "coeffs_size() != vars_size() in linear expression: " + sds(e);
    }
    for (int var : e.getVarsList()) {
      if (!variableReferenceIsValid(var)) {
        return "Out of bound integer variable " + var + " in linear expression: " + sds(e);
      }
    }
    for (int var : e.getVarsList()) {
      if (var < 0) {
        return "Invalid negated variable in linear expression: " + sds(e);
      }
    }
    if (possibleIntegerOverflow(e.getVarsList(), e.getCoeffsList(), e.getOffset(), null)) {
      return "Possible overflow in linear expression: " + sds(e);
    }
    return "";
  }

  private String validateLinearConstraint(ConstraintProto ct) {
    if (!domainInProtoIsValid(ct.getLinear().getDomainList())) {
      return "Invalid domain in constraint : " + sds(ct);
    }
    if (ct.getLinear().getCoeffsCount() != ct.getLinear().getVarsCount()) {
      return "coeffs_size() != vars_size() in constraint: " + sds(ct);
    }
    for (int var : ct.getLinear().getVarsList()) {
      if (var < 0) {
        return "Invalid negated variable in linear constraint: " + sds(ct);
      }
    }
    if (possibleIntegerOverflow(ct.getLinear().getVarsList(), ct.getLinear().getCoeffsList(), 0,
        null)) {
      return "Possible integer overflow in constraint: " + ds(ct);
    }
    return "";
  }

  // variable references, literal references and intervals used by a constraint
  private void collectReferences(ConstraintProto ct, List<Integer> variables,
      List<Integer> literals) {
    switch (ct.getConstraintCase()) {
      case BOOL_OR:
        literals.addAll(ct.getBoolOr().getLiteralsList());
        break;
      case BOOL_AND:
        literals.addAll(ct.getBoolAnd().getLiteralsList());
        break;
      case AT_MOST_ONE:
        literals.addAll(ct.getAtMostOne().getLiteralsList());
        break;
      case EXACTLY_ONE:
        literals.addAll(ct.getExactlyOne().getLiteralsList());
        break;
      case BOOL_XOR:
        literals.addAll(ct.getBoolXor().getLiteralsList());
        break;
      case INT_DIV:
      case INT_MOD:
      case INT_PROD:
      case LIN_MAX: {
        org.matheclipse.ortools.sat.LinearArgumentProto a =
            ct.getConstraintCase() == ConstraintProto.ConstraintCase.INT_DIV ? ct.getIntDiv()
                : ct.getConstraintCase() == ConstraintProto.ConstraintCase.INT_MOD
                    ? ct.getIntMod()
                    : ct.getConstraintCase() == ConstraintProto.ConstraintCase.INT_PROD
                        ? ct.getIntProd()
                        : ct.getLinMax();
        variables.addAll(a.getTarget().getVarsList());
        for (LinearExpressionProto e : a.getExprsList()) {
          variables.addAll(e.getVarsList());
        }
        break;
      }
      case LINEAR:
        variables.addAll(ct.getLinear().getVarsList());
        break;
      case ALL_DIFF:
        for (LinearExpressionProto e : ct.getAllDiff().getExprsList()) {
          variables.addAll(e.getVarsList());
        }
        break;
      case ELEMENT: {
        ElementConstraintProto el = ct.getElement();
        if (el.getVarsCount() > 0 || el.getIndex() != 0 || el.getTarget() != 0) {
          variables.add(el.getIndex());
          variables.add(el.getTarget());
          variables.addAll(el.getVarsList());
        }
        variables.addAll(el.getLinearIndex().getVarsList());
        variables.addAll(el.getLinearTarget().getVarsList());
        for (LinearExpressionProto e : el.getExprsList()) {
          variables.addAll(e.getVarsList());
        }
        break;
      }
      case CIRCUIT:
        literals.addAll(ct.getCircuit().getLiteralsList());
        break;
      case ROUTES:
        literals.addAll(ct.getRoutes().getLiteralsList());
        break;
      case TABLE:
        variables.addAll(ct.getTable().getVarsList());
        for (LinearExpressionProto e : ct.getTable().getExprsList()) {
          variables.addAll(e.getVarsList());
        }
        break;
      case AUTOMATON:
        variables.addAll(ct.getAutomaton().getVarsList());
        for (LinearExpressionProto e : ct.getAutomaton().getExprsList()) {
          variables.addAll(e.getVarsList());
        }
        break;
      case INVERSE:
        if (ct.getInverse().getFDirectCount() > 0) {
          variables.addAll(ct.getInverse().getFDirectList());
          variables.addAll(ct.getInverse().getFInverseList());
        } else {
          for (LinearExpressionProto e : ct.getInverse().getFExprDirectList()) {
            variables.addAll(e.getVarsList());
          }
          for (LinearExpressionProto e : ct.getInverse().getFExprInverseList()) {
            variables.addAll(e.getVarsList());
          }
        }
        break;
      case RESERVOIR:
        for (LinearExpressionProto e : ct.getReservoir().getTimeExprsList()) {
          variables.addAll(e.getVarsList());
        }
        for (LinearExpressionProto e : ct.getReservoir().getLevelChangesList()) {
          variables.addAll(e.getVarsList());
        }
        literals.addAll(ct.getReservoir().getActiveLiteralsList());
        break;
      case INTERVAL:
        variables.addAll(ct.getInterval().getStart().getVarsList());
        variables.addAll(ct.getInterval().getSize().getVarsList());
        variables.addAll(ct.getInterval().getEnd().getVarsList());
        break;
      case CUMULATIVE:
        variables.addAll(ct.getCumulative().getCapacity().getVarsList());
        for (LinearExpressionProto e : ct.getCumulative().getDemandsList()) {
          variables.addAll(e.getVarsList());
        }
        break;
      case DUMMY_CONSTRAINT:
        variables.addAll(ct.getDummyConstraint().getVarsList());
        break;
      default:
        break;
    }
  }

  private static List<Integer> usedIntervals(ConstraintProto ct) {
    List<Integer> r = new ArrayList<>();
    switch (ct.getConstraintCase()) {
      case NO_OVERLAP:
        r.addAll(ct.getNoOverlap().getIntervalsList());
        break;
      case NO_OVERLAP_2D:
        r.addAll(ct.getNoOverlap2D().getXIntervalsList());
        r.addAll(ct.getNoOverlap2D().getYIntervalsList());
        break;
      case CUMULATIVE:
        r.addAll(ct.getCumulative().getIntervalsList());
        break;
      default:
        break;
    }
    return r;
  }

  private String validateVariablesUsedInConstraint(int c) {
    ConstraintProto ct = model.getConstraints(c);
    List<Integer> variables = new ArrayList<>();
    List<Integer> literals = new ArrayList<>();
    collectReferences(ct, variables, literals);
    for (int v : variables) {
      if (!variableReferenceIsValid(v)) {
        return "Out of bound integer variable " + v + " in constraint #" + c + " : " + sds(ct);
      }
    }
    for (int lit : ct.getEnforcementLiteralList()) {
      if (!literalReferenceIsValid(lit)) {
        return "Invalid enforcement literal " + lit + " in constraint #" + c + " : " + sds(ct);
      }
    }
    for (int lit : literals) {
      if (!literalReferenceIsValid(lit)) {
        return "Invalid literal " + lit + " in constraint #" + c + " : " + sds(ct);
      }
    }
    return "";
  }

  private String validateIntervalsUsedInConstraint(int c) {
    ConstraintProto ct = model.getConstraints(c);
    for (int i : usedIntervals(ct)) {
      if (i < 0 || i >= model.getConstraintsCount()) {
        return "Out of bound interval " + i + " in constraint #" + c + " : " + sds(ct);
      }
      if (model.getConstraints(i).getConstraintCase() != ConstraintProto.ConstraintCase.INTERVAL) {
        return "Interval " + i + " does not refer to an interval constraint. Problematic constraint #"
            + c + " : " + sds(ct);
      }
    }
    return "";
  }

  private String validateArgument(String kind, org.matheclipse.ortools.sat.LinearArgumentProto a,
      ConstraintProto ct, boolean exactlyTwo) {
    if (exactlyTwo && a.getExprsCount() != 2) {
      return "An " + kind + " constraint should have exactly 2 terms: " + sds(ct);
    }
    if (!a.hasTarget()) {
      return "An " + kind + " constraint should have a target: " + sds(ct);
    }
    for (LinearExpressionProto e : a.getExprsList()) {
      String r = validateLinearExpression(e);
      if (!r.isEmpty()) {
        return r;
      }
    }
    return validateLinearExpression(a.getTarget());
  }

  private String validateIntProd(ConstraintProto ct) {
    String r = validateArgument("int_prod", ct.getIntProd(), ct, false);
    if (!r.isEmpty()) {
      return r;
    }
    Domain productDomain = new Domain(1);
    for (LinearExpressionProto e : ct.getIntProd().getExprsList()) {
      long min = minOfExpression(e);
      long max = maxOfExpression(e);
      if (min == 0 && max == 0) {
        continue;
      }
      productDomain = productDomain.continuousMultiplicationBy(new Domain(min, max));
    }
    if (productDomain.max() <= -MAX || productDomain.min() >= MAX) {
      return "integer overflow in constraint: " + sds(ct);
    }
    if (ct.getIntProd().getExprsCount() > 2
        && (productDomain.max() >= MAX || productDomain.min() <= -MAX)) {
      return "Potential integer overflow in constraint: " + sds(ct);
    }
    return "";
  }

  private String validateIntDiv(ConstraintProto ct) {
    String r = validateArgument("int_div", ct.getIntDiv(), ct, true);
    if (!r.isEmpty()) {
      return r;
    }
    // Canonicalize the divisor: merge repeated variables and fold fixed variables into the
    // offset, so that the checks below see the expression the solver will actually divide by.
    LinearExpressionProto denom = ct.getIntDiv().getExprs(1);
    long offset = denom.getOffset();
    java.util.TreeMap<Integer, Long> terms = new java.util.TreeMap<>();
    for (int i = 0; i < denom.getVarsCount(); i++) {
      int var = denom.getVars(i);
      long coeff = denom.getCoeffs(i);
      if (coeff == 0) {
        continue;
      }
      if (minOfRef(var) == maxOfRef(var)) {
        offset += coeff * minOfRef(var);
      } else {
        terms.merge(var, coeff, Long::sum);
      }
    }
    terms.values().removeIf(c -> c == 0);
    boolean divisorCanBeZero;
    if (terms.isEmpty()) {
      if (offset == 0) {
        return "Division by 0: " + sds(ct);
      }
      divisorCanBeZero = false;
    } else if (terms.size() == 1) {
      // exact check for an affine divisor, so that a domain with a hole at zero is accepted
      Map.Entry<Integer, Long> t = terms.firstEntry();
      long coeff = t.getValue();
      long inverseOfZero = -offset / coeff;
      divisorCanBeZero =
          inverseOfZero * coeff + offset == 0 && domainOfRef(t.getKey()).contains(inverseOfZero);
    } else if (terms.size() == 2) {
      // also exact with two terms
      Map.Entry<Integer, Long> ta = terms.firstEntry();
      Map.Entry<Integer, Long> tb = terms.lastEntry();
      divisorCanBeZero = SatUtil.diophantineEquationOfSizeTwoHasSolutionInDomain(
          domainOfRef(ta.getKey()), ta.getValue(), domainOfRef(tb.getKey()), tb.getValue(),
          -offset);
    } else {
      // with more terms, the divisor can only be 0 if the gcd of the coefficients divides the
      // offset and 0 is within its bounds
      long gcd = 0;
      long min = offset;
      long max = offset;
      for (Map.Entry<Integer, Long> t : terms.entrySet()) {
        long coeff = t.getValue();
        gcd = SatUtil.gcd(gcd, Math.abs(coeff));
        min += coeff > 0 ? coeff * minOfRef(t.getKey()) : coeff * maxOfRef(t.getKey());
        max += coeff > 0 ? coeff * maxOfRef(t.getKey()) : coeff * minOfRef(t.getKey());
      }
      divisorCanBeZero = offset % gcd == 0 && min <= 0 && max >= 0;
    }
    if (divisorCanBeZero) {
      return "The domain of the divisor cannot contain 0: " + sds(ct);
    }
    return "";
  }

  private String validateIntMod(ConstraintProto ct) {
    String r = validateArgument("int_mod", ct.getIntMod(), ct, true);
    if (!r.isEmpty()) {
      return r;
    }
    if (minOfExpression(ct.getIntMod().getExprs(1)) <= 0) {
      return "An int_mod must have a strictly positive modulo argument: " + sds(ct);
    }
    return "";
  }

  private String validateElement(ConstraintProto ct) {
    ElementConstraintProto element = ct.getElement();
    boolean linearFormat = element.hasLinearIndex() || element.hasLinearTarget()
        || element.getExprsCount() > 0;
    boolean legacyFormat = element.getVarsCount() > 0 || element.getIndex() != 0
        || element.getTarget() != 0;
    if (linearFormat && legacyFormat) {
      return "Inconsistent element with both legacy and new format defined" + sds(ct);
    }
    if (element.getVarsCount() == 0 && element.getExprsCount() == 0) {
      return "Empty element constraint is interpreted as vars[], thus invalid since the index will be out of bounds.";
    }
    if (legacyFormat) {
      if (!variableIndexIsValid(element.getIndex()) || !variableIndexIsValid(element.getTarget())) {
        return "Element constraint index and target must be valid variables: " + sds(ct);
      }
    }
    if (element.getVarsCount() > 0) {
      for (int ref : element.getVarsList()) {
        if (!variableIndexIsValid(ref)) {
          return "Element vars must be valid variables: " + sds(ct);
        }
        List<Integer> vars = new ArrayList<>();
        vars.add(element.getTarget());
        vars.add(ref);
        List<Long> coeffs = new ArrayList<>();
        coeffs.add(1L);
        coeffs.add(-1L);
        if (possibleIntegerOverflow(vars, coeffs, 0, null)) {
          return "Domain of the variables involved in element constraint may cause overflow"
              + sds(ct);
        }
      }
    }
    if (linearFormat) {
      String r = validateLinearExpression(element.getLinearIndex());
      if (!r.isEmpty()) {
        return r;
      }
      r = validateLinearExpression(element.getLinearTarget());
      if (!r.isEmpty()) {
        return r;
      }
      for (LinearExpressionProto e : element.getExprsList()) {
        r = validateLinearExpression(e);
        if (!r.isEmpty()) {
          return r;
        }
        List<Integer> vars = new ArrayList<>(element.getLinearTarget().getVarsList());
        List<Long> coeffs = new ArrayList<>(element.getLinearTarget().getCoeffsList());
        vars.addAll(e.getVarsList());
        for (long c : e.getCoeffsList()) {
          coeffs.add(-c);
        }
        // the offset of AppendToOverflowValidator() minus the offset of the expression
        long offset = Domain.capSub(Domain.capAdd(Math.abs(element.getLinearTarget().getOffset()),
            Math.abs(e.getOffset())), e.getOffset());
        if (possibleIntegerOverflow(vars, coeffs, offset, null)) {
          return "Domain of the variables involved in element constraint may cause overflow";
        }
      }
    }
    return "";
  }

  private String validateInverse(ConstraintProto ct) {
    org.matheclipse.ortools.sat.InverseConstraintProto inverse = ct.getInverse();
    if (inverse.getFDirectCount() != inverse.getFInverseCount()
        || inverse.getFExprDirectCount() != inverse.getFExprInverseCount()) {
      return "Non-matching fields size in inverse: " + sds(ct);
    }
    if (inverse.getFInverseCount() > 0 && inverse.getFExprInverseCount() > 0) {
      return "Inconsistent inverse with both legacy and new format defined: " + sds(ct);
    }
    if (inverse.getFDirectCount() > 0) {
      for (int var : inverse.getFDirectList()) {
        if (!variableIndexIsValid(var)) {
          return "Invalid variable index in inverse constraint: " + var;
        }
      }
      for (int var : inverse.getFInverseList()) {
        if (!variableIndexIsValid(var)) {
          return "Invalid variable index in inverse constraint: " + var;
        }
      }
    }
    if (inverse.getFExprDirectCount() > 0) {
      for (LinearExpressionProto e : inverse.getFExprDirectList()) {
        String r = validateLinearExpression(e);
        if (!r.isEmpty()) {
          return r;
        }
      }
      for (LinearExpressionProto e : inverse.getFExprInverseList()) {
        String r = validateLinearExpression(e);
        if (!r.isEmpty()) {
          return r;
        }
      }
    }
    return "";
  }

  private String validateTable(ConstraintProto ct) {
    TableConstraintProto arg = ct.getTable();
    if (arg.getVarsCount() > 0 && arg.getExprsCount() > 0) {
      return "Inconsistent table with both legacy and new format defined: " + sds(ct);
    }
    if (arg.getVarsCount() == 0 && arg.getExprsCount() == 0 && arg.getValuesCount() > 0) {
      return "Inconsistent table with empty expressions and non-empty tuples: " + sds(ct);
    }
    if (arg.getVarsCount() == 0 && arg.getExprsCount() == 0) {
      return "";
    }
    int arity = arg.getVarsCount() == 0 ? arg.getExprsCount() : arg.getVarsCount();
    if (arg.getValuesCount() % arity != 0) {
      return "The flat encoding of table constraint tuples must be a multiple of the number of expressions: "
          + ds(ct);
    }
    for (int var : arg.getVarsList()) {
      if (!variableIndexIsValid(var)) {
        return "Invalid variable index in table constraint: " + var;
      }
    }
    for (LinearExpressionProto e : arg.getExprsList()) {
      String r = validateLinearExpression(e);
      if (!r.isEmpty()) {
        return r;
      }
      if (e.getVarsCount() > 1) {
        // supported through an auxiliary variable
        continue;
      }
    }
    return "";
  }

  private String validateAutomaton(ConstraintProto ct) {
    org.matheclipse.ortools.sat.AutomatonConstraintProto a = ct.getAutomaton();
    if (a.getVarsCount() > 0 && a.getExprsCount() > 0) {
      return "Inconsistent automaton with both legacy and new format defined: " + sds(ct);
    }
    int n = a.getTransitionTailCount();
    if (n != a.getTransitionHeadCount() || n != a.getTransitionLabelCount()) {
      return "The transitions repeated fields must have the same size: " + sds(ct);
    }
    for (int var : a.getVarsList()) {
      if (!variableIndexIsValid(var)) {
        return "Invalid variable index in automaton constraint: " + var;
      }
    }
    for (LinearExpressionProto e : a.getExprsList()) {
      String r = validateLinearExpression(e);
      if (!r.isEmpty()) {
        return r;
      }
    }
    Map<List<Long>, Long> tailLabelToHead = new HashMap<>();
    for (int i = 0; i < n; i++) {
      long tail = a.getTransitionTail(i);
      long head = a.getTransitionHead(i);
      long label = a.getTransitionLabel(i);
      if (label <= Long.MIN_VALUE + 1 || label == Long.MAX_VALUE) {
        return "labels in the automaton constraint are too big: " + label;
      }
      List<Long> key = java.util.Arrays.asList(tail, label);
      Long previous = tailLabelToHead.putIfAbsent(key, head);
      if (previous != null) {
        if (previous == head) {
          return "automaton: duplicate transition " + tail + " --(" + label + ")--> " + head;
        }
        return "automaton: incompatible transitions " + tail + " --(" + label + ")--> " + head
            + " and " + tail + " --(" + label + ")--> " + previous;
      }
    }
    return "";
  }

  private String validateGraphInput(boolean isRoute, List<Integer> tails, List<Integer> heads,
      List<Integer> literals, org.matheclipse.ortools.sat.ProtoMessage graph) {
    int size = tails.size();
    if (heads.size() != size || literals.size() != size) {
      return "Wrong field sizes in graph: " + sds(graph);
    }
    Set<Integer> selfLoops = new HashSet<>();
    for (int i = 0; i < size; i++) {
      if (!heads.get(i).equals(tails.get(i))) {
        continue;
      }
      if (!selfLoops.add(heads.get(i))) {
        return "Circuit/Route constraint contains multiple self-loops involving node "
            + heads.get(i);
      }
      if (isRoute && tails.get(i) == 0) {
        return "A route constraint cannot have a self-loop on the depot (node 0)";
      }
    }
    for (int i = 0; i < size; i++) {
      if (tails.get(i) < 0 || heads.get(i) < 0) {
        return "Negative node index in graph: " + sds(graph);
      }
    }
    return "";
  }

  private String validateRoutes(ConstraintProto ct) {
    RoutesConstraintProto routes = ct.getRoutes();
    int maxNode = 0;
    Set<Integer> nodes = new HashSet<>();
    for (int node : routes.getTailsList()) {
      if (node < 0) {
        return "All nodes in a route constraint must be in [0, num_nodes)";
      }
      nodes.add(node);
      maxNode = Math.max(maxNode, node);
    }
    for (int node : routes.getHeadsList()) {
      if (node < 0) {
        return "All nodes in a route constraint must be in [0, num_nodes)";
      }
      nodes.add(node);
      maxNode = Math.max(maxNode, node);
    }
    if (!nodes.isEmpty() && maxNode != nodes.size() - 1) {
      return "All nodes in a route constraint must have incident arcs";
    }
    for (RoutesConstraintProto.NodeExpressions dimension : routes.getDimensionsList()) {
      if (dimension.getExprsCount() != nodes.size()) {
        return "If the dimensions field in a route constraint is set, its elements must be of size num_nodes:"
            + nodes.size();
      }
      for (LinearExpressionProto e : dimension.getExprsList()) {
        for (int v : e.getVarsList()) {
          if (!variableReferenceIsValid(v)) {
            return "Out of bound integer variable " + v + " in route constraint " + sds(ct);
          }
        }
        String r = validateLinearExpression(e);
        if (!r.isEmpty()) {
          return r;
        }
      }
    }
    return validateGraphInput(true, routes.getTailsList(), routes.getHeadsList(),
        routes.getLiteralsList(), routes);
  }

  private String validateInterval(ConstraintProto ct) {
    IntervalConstraintProto arg = ct.getInterval();
    if (!arg.hasStart()) {
      return "Interval must have a start expression: " + sds(ct);
    }
    if (!arg.hasSize()) {
      return "Interval must have a size expression: " + sds(ct);
    }
    if (!arg.hasEnd()) {
      return "Interval must have an end expression: " + sds(ct);
    }
    if (arg.getStart().getVarsCount() > 1) {
      return "Intervals with a start expression containing more than one variable are currently not supported.";
    }
    String r = validateLinearExpression(arg.getStart());
    if (!r.isEmpty()) {
      return r;
    }
    if (arg.getSize().getVarsCount() > 1) {
      return "Intervals with a size expression containing more than one variable are currently not supported.";
    }
    r = validateLinearExpression(arg.getSize());
    if (!r.isEmpty()) {
      return r;
    }
    if (ct.getEnforcementLiteralCount() == 0 && minOfExpression(arg.getSize()) < 0) {
      return "The size of a performed interval must be >= 0 in constraint: " + ds(ct);
    }
    if (arg.getEnd().getVarsCount() > 1) {
      return "Intervals with an end expression containing more than one variable are currently not supported.";
    }
    r = validateLinearExpression(arg.getEnd());
    if (!r.isEmpty()) {
      return r;
    }
    List<Integer> vars = new ArrayList<>();
    List<Long> coeffs = new ArrayList<>();
    long offset = 0;
    LinearExpressionProto[] parts = {arg.getStart(), arg.getSize(), arg.getEnd()};
    for (int k = 0; k < 3; k++) {
      long prod = k == 2 ? -1 : 1;
      vars.addAll(parts[k].getVarsList());
      for (long c : parts[k].getCoeffsList()) {
        coeffs.add(c * prod);
      }
      offset = Domain.capAdd(Math.abs(offset), Math.abs(parts[k].getOffset()));
    }
    if (possibleIntegerOverflow(vars, coeffs, offset, null)) {
      return "Possible overflow in interval: " + sds(arg);
    }
    return "";
  }

  private String validateCumulative(ConstraintProto ct) {
    org.matheclipse.ortools.sat.CumulativeConstraintProto cu = ct.getCumulative();
    if (cu.getIntervalsCount() != cu.getDemandsCount()) {
      return "intervals_size() != demands_size() in constraint: " + sds(ct);
    }
    String r = validateLinearExpression(cu.getCapacity());
    if (!r.isEmpty()) {
      return r;
    }
    for (LinearExpressionProto d : cu.getDemandsList()) {
      r = validateLinearExpression(d);
      if (!r.isEmpty()) {
        return r;
      }
    }
    for (LinearExpressionProto d : cu.getDemandsList()) {
      if (minOfExpression(d) < 0) {
        return "Demand " + ds(d) + " must be positive in constraint: " + ds(ct);
      }
      if (d.getVarsCount() > 1) {
        return "Demand " + ds(d) + " must be affine or constant in constraint: " + ds(ct);
      }
    }
    if (cu.getCapacity().getVarsCount() > 1) {
      return "capacity " + ds(cu.getCapacity()) + " must be affine or constant in constraint: "
          + ds(ct);
    }
    long sumMaxDemands = 0;
    for (LinearExpressionProto d : cu.getDemandsList()) {
      sumMaxDemands = Domain.capAdd(sumMaxDemands, maxOfExpression(d));
      if (sumMaxDemands == Long.MAX_VALUE) {
        return "The sum of max demands does not fit on an int64_t in constraint: " + ds(ct);
      }
    }
    return "";
  }

  private long intervalSizeMax(int index) {
    return maxOfExpression(model.getConstraints(index).getInterval().getSize());
  }

  private String validateNoOverlap2D(ConstraintProto ct) {
    int sizeX = ct.getNoOverlap2D().getXIntervalsCount();
    int sizeY = ct.getNoOverlap2D().getYIntervalsCount();
    if (sizeX != sizeY) {
      return "The two lists of intervals must have the same size: " + sds(ct);
    }
    long sumMaxAreas = 0;
    for (int i = 0; i < sizeX; i++) {
      long mx = intervalSizeMax(ct.getNoOverlap2D().getXIntervals(i));
      long my = intervalSizeMax(ct.getNoOverlap2D().getYIntervals(i));
      sumMaxAreas = Domain.capAdd(sumMaxAreas, Domain.capProd(mx, my));
      if (sumMaxAreas == Long.MAX_VALUE) {
        return "Integer overflow when summing all areas in constraint: " + ds(ct);
      }
    }
    return "";
  }

  private String validateReservoir(ConstraintProto ct) {
    org.matheclipse.ortools.sat.ReservoirConstraintProto res = ct.getReservoir();
    if (res.getTimeExprsCount() != res.getLevelChangesCount()) {
      return "time_exprs and level_changes fields must be of the same size: " + sds(ct);
    }
    for (LinearExpressionProto e : res.getTimeExprsList()) {
      String r = validateLinearExpression(e);
      if (!r.isEmpty()) {
        return r;
      }
      if (minOfExpression(e) <= -MAX / 4 || maxOfExpression(e) >= MAX / 4) {
        return "Potential integer overflow on time_expr of a reservoir: " + sds(ct);
      }
    }
    for (LinearExpressionProto e : res.getLevelChangesList()) {
      String r = validateLinearExpression(e);
      if (!r.isEmpty()) {
        return r;
      }
    }
    if (res.getMinLevel() > 0) {
      return "The min level of a reservoir must be <= 0. Please use fixed events to set up initial state: "
          + sds(ct);
    }
    if (res.getMaxLevel() < 0) {
      return "The max level of a reservoir must be >= 0. Please use fixed events to set up initial state: "
          + sds(ct);
    }
    long sumAbs = 0;
    for (LinearExpressionProto d : res.getLevelChangesList()) {
      long dmin = minOfExpression(d);
      long dmax = maxOfExpression(d);
      long absMin = dmin == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(dmin);
      long absMax = dmax == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(dmax);
      sumAbs = Domain.capAdd(sumAbs, Math.max(absMin, absMax));
      if (sumAbs == Long.MAX_VALUE) {
        return "Possible integer overflow in constraint: " + ds(ct);
      }
    }
    if (res.getActiveLiteralsCount() > 0
        && res.getActiveLiteralsCount() != res.getTimeExprsCount()) {
      return "Wrong array length of active_literals variables";
    }
    if (res.getLevelChangesCount() > 0 && res.getLevelChangesCount() != res.getTimeExprsCount()) {
      return "Wrong array length of level_changes variables";
    }
    return "";
  }

  private String validateObjective(CpObjectiveProto obj) {
    if (!domainInProtoIsValid(obj.getDomainList())) {
      return "The objective has an invalid domain() format: " + sds(obj);
    }
    if (obj.getVarsCount() != obj.getCoeffsCount()) {
      return "vars and coeffs sizes do not match in objective: " + sds(obj);
    }
    for (int v : obj.getVarsList()) {
      if (!variableIndexIsValid(v)) {
        return "Out of bound integer variable " + v + " in objective: " + sds(obj);
      }
    }
    long[] bounds = new long[2];
    if (possibleIntegerOverflow(obj.getVarsList(), obj.getCoeffsList(), 0, bounds)) {
      return "Possible integer overflow in objective: " + ds(obj);
    }
    if (!Double.isFinite(obj.getOffset())) {
      return "Objective offset must be finite: " + ds(obj);
    }
    double f = obj.getScalingFactor();
    if (f != 0 && f != 1 && f != -1) {
      if (!Double.isFinite(Math.abs(f * bounds[0]) + Math.abs(obj.getOffset()))
          || !Double.isFinite(Math.abs(f * bounds[1]) + Math.abs(obj.getOffset()))) {
        return "Possible floating point overflow in objective when multiplied by the scaling factor: "
            + ds(obj);
      }
    }
    return "";
  }

  String validateFloatingPointObjective(double maxValidMagnitude, FloatObjectiveProto obj) {
    if (obj.getVarsCount() != obj.getCoeffsCount()) {
      return "vars and coeffs sizes do not match in objective: " + sds(obj);
    }
    for (int v : obj.getVarsList()) {
      if (!variableIndexIsValid(v)) {
        return "Out of bound integer variable " + v + " in objective: " + sds(obj);
      }
    }
    for (double c : obj.getCoeffsList()) {
      if (!Double.isFinite(c)) {
        return "Coefficients must be finite in objective: " + sds(obj);
      }
      if (Math.abs(c) > maxValidMagnitude) {
        return "Coefficients larger than params.mip_max_valid_magnitude() [value = "
            + maxValidMagnitude + "] in objective: " + sds(obj);
      }
    }
    if (!Double.isFinite(obj.getOffset())) {
      return "Offset must be finite in objective: " + sds(obj);
    }
    double sumMin = obj.getOffset();
    double sumMax = obj.getOffset();
    for (int i = 0; i < obj.getVarsCount(); i++) {
      int ref = obj.getVars(i);
      IntegerVariableProto v = model.getVariables(positiveRef(ref));
      long minDomain = v.getDomain(0);
      long maxDomain = v.getDomain(v.getDomainCount() - 1);
      double coeff = ref >= 0 ? obj.getCoeffs(i) : -obj.getCoeffs(i);
      double p1 = minDomain * coeff;
      double p2 = maxDomain * coeff;
      sumMin += Math.min(0.0, Math.min(p1, p2));
      sumMax += Math.max(0.0, Math.max(p1, p2));
    }
    if (!Double.isFinite(2.0 * sumMin) || !Double.isFinite(2.0 * sumMax)) {
      return "Possible floating point overflow in objective: " + sds(obj);
    }
    return "";
  }

  private String validateSearchStrategies() {
    for (DecisionStrategyProto strategy : model.getSearchStrategyList()) {
      int vss = strategy.getVariableSelectionStrategyValue();
      if (vss < 0 || vss > 4) {
        return "Unknown or unsupported variable_selection_strategy: " + vss;
      }
      int drs = strategy.getDomainReductionStrategyValue();
      if (drs < 0 || drs > 5) {
        return "Unknown or unsupported domain_reduction_strategy: " + drs;
      }
      if (strategy.getVariablesCount() > 0 && strategy.getExprsCount() > 0) {
        return "Strategy can't have both variables and exprs: " + sds(strategy);
      }
      for (int ref : strategy.getVariablesList()) {
        if (!variableReferenceIsValid(ref)) {
          return "Invalid variable reference in strategy: " + sds(strategy);
        }
        if (drs == DecisionStrategyProto.DomainReductionStrategy.SELECT_MEDIAN_VALUE_VALUE
            && SolutionChecker.domainOf(model.getVariables(positiveRef(ref))).size() > 100000) {
          return "Variable #" + positiveRef(ref)
              + " has a domain too large to be used in a SELECT_MEDIAN_VALUE value selection strategy";
        }
      }
      for (LinearExpressionProto e : strategy.getExprsList()) {
        if (e.getVarsCount() > 1) {
          return "expression must be affine in strategy: " + sds(strategy);
        }
        for (int var : e.getVarsList()) {
          if (!variableReferenceIsValid(var)) {
            return "Invalid variable reference in strategy: " + sds(strategy);
          }
        }
        if (!validateLinearExpression(e).isEmpty()) {
          return "Invalid affine expr in strategy: " + sds(strategy);
        }
        if (drs == DecisionStrategyProto.DomainReductionStrategy.SELECT_MEDIAN_VALUE_VALUE) {
          for (int var : e.getVarsList()) {
            if (SolutionChecker.domainOf(model.getVariables(var)).size() > 100000) {
              return "Variable #" + var
                  + " has a domain too large to be used in a SELECT_MEDIAN_VALUE value selection strategy";
            }
          }
        }
      }
    }
    return "";
  }

  private String validateSolutionHint() {
    if (!model.hasSolutionHint()) {
      return "";
    }
    PartialVariableAssignment hint = model.getSolutionHint();
    if (hint.getVarsCount() != hint.getValuesCount()) {
      return "Invalid solution hint: vars and values do not have the same size.";
    }
    for (int var : hint.getVarsList()) {
      if (!variableIndexIsValid(var)) {
        return "Invalid variable in solution hint: " + var;
      }
    }
    Set<Integer> indices = new HashSet<>();
    for (int var : hint.getVarsList()) {
      if (!indices.add(positiveRef(var))) {
        return "The solution hint contains duplicate variables like the variable with index #"
            + positiveRef(var);
      }
    }
    for (long value : hint.getValuesList()) {
      if (value == Long.MIN_VALUE || value == Long.MAX_VALUE) {
        return "The solution hint cannot contain the INT_MIN or INT_MAX values.";
      }
    }
    return "";
  }

  private String validateModel() {
    long int128Overflow = 0;
    for (int v = 0; v < model.getVariablesCount(); v++) {
      String r = validateIntegerVariable(v);
      if (!r.isEmpty()) {
        return r;
      }
      List<Long> d = model.getVariables(v).getDomainList();
      long min = d.get(0);
      long max = d.get(d.size() - 1);
      int128Overflow = Domain.capAdd(int128Overflow,
          Math.max(Math.max(Math.abs(min), Math.abs(max)), max - min));
    }
    if (int128Overflow == Long.MAX_VALUE) {
      return "The sum of all variable domains does not fit on an int64_t. This is needed to prevent overflows.";
    }
    List<Integer> usingIntervals = new ArrayList<>();
    for (int c = 0; c < model.getConstraintsCount(); c++) {
      String r = validateVariablesUsedInConstraint(c);
      if (!r.isEmpty()) {
        return r;
      }
      ConstraintProto ct = model.getConstraints(c);
      switch (ct.getConstraintCase()) {
        case LINEAR:
          r = validateLinearConstraint(ct);
          break;
        case LIN_MAX:
          r = validateLinearExpression(ct.getLinMax().getTarget());
          for (int i = 0; r.isEmpty() && i < ct.getLinMax().getExprsCount(); i++) {
            r = validateLinearExpression(ct.getLinMax().getExprs(i));
          }
          break;
        case INT_PROD:
          r = validateIntProd(ct);
          break;
        case INT_DIV:
          r = validateIntDiv(ct);
          break;
        case INT_MOD:
          r = validateIntMod(ct);
          break;
        case INVERSE:
          r = validateInverse(ct);
          break;
        case ALL_DIFF:
          for (int i = 0; r.isEmpty() && i < ct.getAllDiff().getExprsCount(); i++) {
            r = validateLinearExpression(ct.getAllDiff().getExprs(i));
          }
          break;
        case ELEMENT:
          r = validateElement(ct);
          break;
        case TABLE:
          r = validateTable(ct);
          break;
        case AUTOMATON:
          r = validateAutomaton(ct);
          break;
        case CIRCUIT:
          r = validateGraphInput(false, ct.getCircuit().getTailsList(),
              ct.getCircuit().getHeadsList(), ct.getCircuit().getLiteralsList(), ct.getCircuit());
          break;
        case ROUTES:
          r = validateRoutes(ct);
          break;
        case INTERVAL:
          r = validateInterval(ct);
          break;
        case CUMULATIVE:
        case NO_OVERLAP:
        case NO_OVERLAP_2D:
          usingIntervals.add(c);
          break;
        case RESERVOIR:
          r = validateReservoir(ct);
          break;
        case DUMMY_CONSTRAINT:
          return "The dummy constraint should never appear in a model.";
        default:
          break;
      }
      if (!r.isEmpty()) {
        return r;
      }
    }
    for (int c : usingIntervals) {
      String r = validateIntervalsUsedInConstraint(c);
      if (!r.isEmpty()) {
        return r;
      }
      ConstraintProto ct = model.getConstraints(c);
      if (ct.getConstraintCase() == ConstraintProto.ConstraintCase.CUMULATIVE) {
        r = validateCumulative(ct);
      } else if (ct.getConstraintCase() == ConstraintProto.ConstraintCase.NO_OVERLAP_2D) {
        r = validateNoOverlap2D(ct);
      }
      if (!r.isEmpty()) {
        return r;
      }
    }
    if (model.hasObjective() && model.hasFloatingPointObjective()) {
      return "A model cannot have both an objective and a floating point objective.";
    }
    if (model.hasObjective()) {
      double f = model.getObjective().getScalingFactor();
      if (f != 0 && (Double.isNaN(f) || Double.isInfinite(f) || Math.abs(f) < Double.MIN_NORMAL)) {
        return "A model cannot have an objective with a nan, inf or subnormal scaling factor";
      }
      String r = validateObjective(model.getObjective());
      if (!r.isEmpty()) {
        return r;
      }
      CpObjectiveProto obj = model.getObjective();
      if (obj.getIntegerScalingFactor() != 0 || obj.getIntegerBeforeOffset() != 0
          || obj.getIntegerAfterOffset() != 0) {
        if (obj.getDomainCount() == 0) {
          return "Objective integer scaling or offset is set without an objective domain.";
        }
        for (long v : obj.getDomainList()) {
          long t = Domain.capAdd(v, obj.getIntegerBeforeOffset());
          boolean overflow = atMinOrMax(t);
          if (!overflow) {
            t = Domain.capProd(t, obj.getIntegerScalingFactor());
            overflow = atMinOrMax(t);
          }
          if (!overflow) {
            t = Domain.capAdd(t, obj.getIntegerAfterOffset());
            overflow = atMinOrMax(t);
          }
          if (overflow) {
            return "Internal fields related to the postsolve of the integer objective are causing a potential integer overflow: "
                + sds(obj);
          }
        }
      }
    }
    String r = validateSearchStrategies();
    if (!r.isEmpty()) {
      return r;
    }
    r = validateSolutionHint();
    if (!r.isEmpty()) {
      return r;
    }
    for (int ref : model.getAssumptionsList()) {
      if (!literalReferenceIsValid(ref)) {
        return "Invalid literal reference " + ref + " in the 'assumptions' field.";
      }
    }
    return "";
  }
}
