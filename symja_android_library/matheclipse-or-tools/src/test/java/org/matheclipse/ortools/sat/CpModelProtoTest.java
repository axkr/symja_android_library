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
// Modified for the Symja matheclipse-or-tools module: ported from ortools/javatests/com/google/ortools/sat/CpModelProtoTest.java (Truth assertions replaced by JUnit 5 assertions), see NOTICE in the module root.

package org.matheclipse.ortools.sat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.ortools.Loader;

/** Tests for the Java CpModelProto. */
public final class CpModelProtoTest {
  @BeforeEach
  public void setUp() {
    Loader.loadNativeLibraries();
  }

  @Test
  public void testCpModelProtoCreation() {
    CpModelProto.Builder model = CpModelProto.newBuilder();
    model.setName("test_model");

    IntegerVariableProto.Builder var = model.addVariablesBuilder();
    var.setName("x");
    var.addDomain(0);
    var.addDomain(10);

    assertEquals("test_model", model.getName());
    assertEquals(1, model.getVariablesCount());
    assertEquals("x", model.getVariables(0).getName());
    assertEquals(Arrays.asList(0L, 10L), model.getVariables(0).getDomainList());
  }

  @Test
  public void testConstraintProtoModification() {
    ConstraintProto.Builder constraint = ConstraintProto.newBuilder();
    constraint.setName("bool_or_constraint");
    constraint.getBoolOrBuilder().addLiterals(1).addLiterals(-2);

    assertEquals("bool_or_constraint", constraint.getName());
    assertTrue(constraint.hasBoolOr());
    assertEquals(Arrays.asList(1, -2), constraint.getBoolOr().getLiteralsList());

    // Modification
    constraint.getBoolOrBuilder().setLiterals(0, 5);
    assertEquals(5, constraint.getBoolOr().getLiterals(0));

    // Enforcement literal
    constraint.addEnforcementLiteral(10);
    assertEquals(1, constraint.getEnforcementLiteralCount());
    assertEquals(10, constraint.getEnforcementLiteral(0));
  }

  @Test
  public void testLinearConstraint() {
    LinearConstraintProto.Builder linear = LinearConstraintProto.newBuilder();
    linear.addVars(1).addVars(2);
    linear.addCoeffs(10).addCoeffs(20);
    linear.addDomain(0).addDomain(100);

    assertEquals(2, linear.getVarsCount());
    assertEquals(Arrays.asList(10L, 20L), linear.getCoeffsList());
    assertEquals(Arrays.asList(0L, 100L), linear.getDomainList());
  }

  @Test
  public void testEnums() {
    // VariableSelectionStrategy enum
    DecisionStrategyProto.VariableSelectionStrategy strategy =
        DecisionStrategyProto.VariableSelectionStrategy.CHOOSE_LOWEST_MIN;
    assertEquals(1, strategy.getNumber());
    assertEquals(strategy, DecisionStrategyProto.VariableSelectionStrategy.forNumber(1));
    assertEquals(strategy,
        DecisionStrategyProto.VariableSelectionStrategy.valueOf("CHOOSE_LOWEST_MIN"));

    // DomainReductionStrategy enum
    DecisionStrategyProto.DomainReductionStrategy domainStrategy =
        DecisionStrategyProto.DomainReductionStrategy.SELECT_MAX_VALUE;
    assertEquals(1, domainStrategy.getNumber());

    // CpSolverStatus enum
    CpSolverStatus status = CpSolverStatus.OPTIMAL;
    assertEquals(4, status.getNumber());
    assertEquals(status, CpSolverStatus.forNumber(4));
  }

  @Test
  public void testNestedMessages() {
    CpModelProto.Builder model = CpModelProto.newBuilder();
    ConstraintProto.Builder ct = model.addConstraintsBuilder();
    ct.getLinearBuilder().addVars(0).addCoeffs(1).addDomain(0).addDomain(1);

    assertEquals(1, model.getConstraintsCount());
    assertTrue(model.getConstraints(0).hasLinear());
    assertEquals(0, model.getConstraints(0).getLinear().getVars(0));
  }

  @Test
  public void testObjectiveModification() {
    CpObjectiveProto.Builder objective = CpObjectiveProto.newBuilder();
    objective.addVars(1).addCoeffs(5L);
    objective.setOffset(10.0);
    objective.setScalingFactor(-1.0);

    assertEquals(1, objective.getVars(0));
    assertEquals(5L, objective.getCoeffs(0));
    assertEquals(10.0, objective.getOffset());
    assertEquals(-1.0, objective.getScalingFactor());
  }

  @Test
  public void testIntervalConstraint() {
    IntervalConstraintProto.Builder interval = IntervalConstraintProto.newBuilder();
    interval.getStartBuilder().addVars(1).addCoeffs(1).setOffset(0);
    interval.getSizeBuilder().setOffset(10);
    interval.getEndBuilder().addVars(1).addCoeffs(1).setOffset(10);

    assertTrue(interval.hasStart());
    assertEquals(1, interval.getStart().getVars(0));
    assertEquals(10L, interval.getSize().getOffset());
    assertEquals(10L, interval.getEnd().getOffset());
  }

  @Test
  public void testPartialVariableAssignment() {
    PartialVariableAssignment.Builder assignment = PartialVariableAssignment.newBuilder();
    assignment.addVars(1).addValues(10L);
    assignment.addVars(2).addValues(20L);

    assertEquals(2, assignment.getVarsCount());
    assertEquals(Arrays.asList(10L, 20L), assignment.getValuesList());
  }

  @Test
  public void testCpSolverResponse() {
    CpSolverResponse.Builder response = CpSolverResponse.newBuilder();
    response.setStatus(CpSolverStatus.FEASIBLE);
    response.addSolution(1).addSolution(2).addSolution(3);
    response.setObjectiveValue(100.5);

    assertEquals(CpSolverStatus.FEASIBLE, response.getStatus());
    assertEquals(3, response.getSolutionCount());
    assertEquals(100.5, response.getObjectiveValue());
  }
}
