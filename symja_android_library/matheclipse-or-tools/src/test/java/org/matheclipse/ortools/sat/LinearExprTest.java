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
// Modified for the Symja matheclipse-or-tools module: adapted from the OR-Tools Java wrapper, see NOTICE in the module root.

package org.matheclipse.ortools.sat;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.matheclipse.ortools.Loader;
import org.matheclipse.ortools.util.Domain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class LinearExprTest {
  @BeforeEach
  public void setUp() {
    Loader.loadNativeLibraries();
  }

  @Test
  public void testLinearExprAdd() {
    final CpModel model = new CpModel();
    assertNotNull(model);
    final Domain domain = new Domain(0, 10);
    final IntVar y = model.newIntVarFromDomain(domain, "y");

    final LinearExpr expr = LinearExpr.newBuilder().add(y).build();
    assertNotNull(expr);

    assertEquals(1, expr.numElements());
    assertEquals(y.getIndex(), expr.getVariableIndex(0));
    assertEquals(1, expr.getCoefficient(0));
    assertEquals(0, expr.getOffset());
  }

  @Test
  public void testLinearExprAddLiteral() {
    final CpModel model = new CpModel();
    assertNotNull(model);
    final BoolVar y = model.newBoolVar("y");

    final LinearExpr expr = LinearExpr.newBuilder().add(y).build();
    assertNotNull(expr);

    assertEquals(1, expr.numElements());
    assertEquals(y.getIndex(), expr.getVariableIndex(0));
    assertEquals(1, expr.getCoefficient(0));
    assertEquals(0, expr.getOffset());
  }

  @Test
  public void testLinearExprAddNegatedLiteral() {
    final CpModel model = new CpModel();
    assertNotNull(model);
    final BoolVar y = model.newBoolVar("y");

    final LinearExpr expr = LinearExpr.newBuilder().add(y.not()).build();
    assertNotNull(expr);

    assertEquals(1, expr.numElements());
    assertEquals(y.getIndex(), expr.getVariableIndex(0));
    assertEquals(-1, expr.getCoefficient(0));
    assertEquals(1, expr.getOffset());
  }

  @Test
  public void testLinearExprTerm() {
    final CpModel model = new CpModel();
    assertNotNull(model);
    final Domain domain = new Domain(0, 10);
    final IntVar y = model.newIntVarFromDomain(domain, "y");

    final LinearExpr expr = LinearExpr.newBuilder().addTerm(y, 12).build();
    assertNotNull(expr);

    assertEquals(1, expr.numElements());
    assertEquals(y.getIndex(), expr.getVariableIndex(0));
    assertEquals(12, expr.getCoefficient(0));
    assertEquals(0, expr.getOffset());
  }

  @Test
  public void testLinearExprBooleanTerm() {
    final CpModel model = new CpModel();
    assertNotNull(model);
    final Literal y = model.newBoolVar("y");

    final LinearExpr expr = LinearExpr.newBuilder().addTerm(y.not(), 12).build();
    assertNotNull(expr);

    assertEquals(1, expr.numElements());
    assertEquals(y.getIndex(), expr.getVariableIndex(0));
    assertEquals(-12, expr.getCoefficient(0));
    assertEquals(12, expr.getOffset());
  }

  @Test
  public void testLinearExprAffine() {
    final CpModel model = new CpModel();
    assertNotNull(model);
    final Domain domain = new Domain(0, 10);
    final IntVar y = model.newIntVarFromDomain(domain, "y");

    final LinearExpr expr = LinearExpr.newBuilder().addTerm(y, 12).add(5).build();
    assertNotNull(expr);

    assertEquals(1, expr.numElements());
    assertEquals(y.getIndex(), expr.getVariableIndex(0));
    assertEquals(12, expr.getCoefficient(0));
    assertEquals(5, expr.getOffset());
  }

  @Test
  public void testLinearExprBooleanAffine() {
    final CpModel model = new CpModel();
    assertNotNull(model);
    final Literal y = model.newBoolVar("y");

    final LinearExpr expr = LinearExpr.newBuilder().addTerm(y.not(), 12).add(5).build();
    assertNotNull(expr);

    assertEquals(1, expr.numElements());
    assertEquals(y.getIndex(), expr.getVariableIndex(0));
    assertEquals(-12, expr.getCoefficient(0));
    assertEquals(17, expr.getOffset());
  }

  @Test
  public void testLinearExprSum() {
    final CpModel model = new CpModel();
    assertNotNull(model);
    final Domain domain = new Domain(0, 10);
    final IntVar x = model.newIntVarFromDomain(domain, "x");
    final IntVar y = model.newIntVarFromDomain(domain, "y");

    final LinearExpr expr = LinearExpr.newBuilder().add(x).add(y).build();
    assertNotNull(expr);

    assertEquals(2, expr.numElements());
    assertEquals(x.getIndex(), expr.getVariableIndex(0));
    assertEquals(1, expr.getCoefficient(0));
    assertEquals(y.getIndex(), expr.getVariableIndex(1));
    assertEquals(1, expr.getCoefficient(1));
    assertEquals(0, expr.getOffset());
  }

  @Test
  public void testLinearExprWeightedSum() {
    final CpModel model = new CpModel();
    assertNotNull(model);
    final Domain domain = new Domain(0, 10);
    final IntVar x = model.newIntVarFromDomain(domain, "x");
    final IntVar y = model.newIntVarFromDomain(domain, "y");

    final LinearExpr expr = LinearExpr.newBuilder().addTerm(x, 3).addTerm(y, 5).build();
    assertNotNull(expr);

    assertEquals(2, expr.numElements());
    assertEquals(x.getIndex(), expr.getVariableIndex(0));
    assertEquals(3, expr.getCoefficient(0));
    assertEquals(y.getIndex(), expr.getVariableIndex(1));
    assertEquals(5, expr.getCoefficient(1));
    assertEquals(0, expr.getOffset());
  }

  @Test
  public void testLinearExprBooleanSum() {
    final CpModel model = new CpModel();
    assertNotNull(model);
    final Literal x = model.newBoolVar("x");
    final Literal y = model.newBoolVar("y");

    final LinearExpr expr = LinearExpr.newBuilder().add(x).add(y.not()).build();
    assertNotNull(expr);

    assertEquals(2, expr.numElements());
    assertEquals(x.getIndex(), expr.getVariableIndex(0));
    assertEquals(1, expr.getCoefficient(0));
    assertEquals(y.getIndex(), expr.getVariableIndex(1));
    assertEquals(-1, expr.getCoefficient(1));
    assertEquals(1, expr.getOffset());
  }

  @Test
  public void testLinearExprBooleanWeightedSum() {
    final CpModel model = new CpModel();
    assertNotNull(model);
    final Literal x = model.newBoolVar("x");
    final Literal y = model.newBoolVar("y");

    final LinearExpr expr = LinearExpr.newBuilder().addTerm(x, 3).addTerm(y.not(), 5).build();
    assertNotNull(expr);

    assertEquals(2, expr.numElements());
    assertEquals(x.getIndex(), expr.getVariableIndex(0));
    assertEquals(3, expr.getCoefficient(0));
    assertEquals(y.getIndex(), expr.getVariableIndex(1));
    assertEquals(-5, expr.getCoefficient(1));
    assertEquals(5, expr.getOffset());
  }
}
