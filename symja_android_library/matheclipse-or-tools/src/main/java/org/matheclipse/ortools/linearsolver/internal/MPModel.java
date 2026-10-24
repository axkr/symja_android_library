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
// Modified for the Symja matheclipse-or-tools module: ported from
// ortools/linear_solver/linear_solver.proto (MPModelProto and its messages) to a plain Java class,
// see NOTICE in the module root.

package org.matheclipse.ortools.linearsolver.internal;

import java.util.ArrayList;
import java.util.List;

/**
 * A plain Java version of the <code>MPModelProto</code> protocol buffer: variables with bounds,
 * integrality and objective coefficients, linear constraints, indicator (enforced) constraints,
 * the optimization direction, the objective offset and a solution hint. It is the exchange format
 * between the MPSolver API, the ModelBuilder API, the solvers and the LP/MPS exporter.
 *
 * <p>
 * As in the protocol buffer, a <code>null</code> name means "no name"; the default bounds are
 * [-inf, +inf].
 */
public final class MPModel {

  /** A variable (MPVariableProto). */
  public static final class Variable {
    /** The name or <code>null</code>. */
    public String name;
    /** The lower bound. */
    public double lowerBound = Double.NEGATIVE_INFINITY;
    /** The upper bound. */
    public double upperBound = Double.POSITIVE_INFINITY;
    /** The objective coefficient. */
    public double objectiveCoefficient;
    /** Whether the variable is integer. */
    public boolean isInteger;

    /** Returns a deep copy. */
    public Variable copy() {
      Variable v = new Variable();
      v.name = name;
      v.lowerBound = lowerBound;
      v.upperBound = upperBound;
      v.objectiveCoefficient = objectiveCoefficient;
      v.isInteger = isInteger;
      return v;
    }
  }

  /** A linear constraint (MPConstraintProto). */
  public static final class Constraint {
    /** The name or <code>null</code>. */
    public String name;
    /** The lower bound. */
    public double lowerBound = Double.NEGATIVE_INFINITY;
    /** The upper bound. */
    public double upperBound = Double.POSITIVE_INFINITY;
    /** The variable indices of the terms. */
    public final List<Integer> varIndex = new ArrayList<>();
    /** The coefficients of the terms. */
    public final List<Double> coefficient = new ArrayList<>();
    /** Whether the constraint is lazy. */
    public boolean isLazy;

    /** Returns a deep copy. */
    public Constraint copy() {
      Constraint c = new Constraint();
      c.name = name;
      c.lowerBound = lowerBound;
      c.upperBound = upperBound;
      c.varIndex.addAll(varIndex);
      c.coefficient.addAll(coefficient);
      c.isLazy = isLazy;
      return c;
    }
  }

  /**
   * An indicator constraint (MPGeneralConstraintProto with an MPIndicatorConstraint): if the
   * variable <code>varIndex</code> takes the value <code>varValue</code>, the constraint must hold.
   */
  public static final class IndicatorConstraint {
    /** The name or <code>null</code>. */
    public String name;
    /** The index of the binary indicator variable. */
    public int varIndex;
    /** The value (0 or 1) of the indicator variable which enforces the constraint. */
    public int varValue;
    /** The enforced constraint. */
    public Constraint constraint = new Constraint();

    /** Returns a deep copy. */
    public IndicatorConstraint copy() {
      IndicatorConstraint c = new IndicatorConstraint();
      c.name = name;
      c.varIndex = varIndex;
      c.varValue = varValue;
      c.constraint = constraint.copy();
      return c;
    }
  }

  /** The model name or <code>null</code>. */
  public String name;
  /** Whether the objective is maximized. */
  public boolean maximize;
  /** The objective offset. */
  public double objectiveOffset;
  /** The variables. */
  public final List<Variable> variables = new ArrayList<>();
  /** The linear constraints. */
  public final List<Constraint> constraints = new ArrayList<>();
  /** The indicator constraints. */
  public final List<IndicatorConstraint> generalConstraints = new ArrayList<>();
  /** The variable indices of the solution hint. */
  public final List<Integer> hintVarIndex = new ArrayList<>();
  /** The values of the solution hint. */
  public final List<Double> hintVarValue = new ArrayList<>();

  /** Returns a deep copy. */
  public MPModel copy() {
    MPModel m = new MPModel();
    m.name = name;
    m.maximize = maximize;
    m.objectiveOffset = objectiveOffset;
    for (Variable v : variables) {
      m.variables.add(v.copy());
    }
    for (Constraint c : constraints) {
      m.constraints.add(c.copy());
    }
    for (IndicatorConstraint c : generalConstraints) {
      m.generalConstraints.add(c.copy());
    }
    m.hintVarIndex.addAll(hintVarIndex);
    m.hintVarValue.addAll(hintVarValue);
    return m;
  }

  /** Returns true if one of the variables is integer. */
  public boolean hasIntegerVariables() {
    for (Variable v : variables) {
      if (v.isInteger) {
        return true;
      }
    }
    return false;
  }
}
