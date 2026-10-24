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
// ortools/linear_solver/wrappers/model_builder_helper.h/.cc C++ to Java (replaces the SWIG
// generated class), see NOTICE in the module root.

package org.matheclipse.ortools.modelbuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.matheclipse.ortools.linearsolver.internal.LpParser;
import org.matheclipse.ortools.linearsolver.internal.MPModel;
import org.matheclipse.ortools.linearsolver.internal.ModelExporter;
import org.matheclipse.ortools.linearsolver.internal.MpsReader;

/**
 * The storage of a ModelBuilder model: a plain Java version of the C++ helper holding an
 * <code>MPModelProto</code>. The reading and writing of protocol buffer files is not available
 * (the corresponding methods return false).
 */
public class ModelBuilderHelper {

  private MPModel model = new MPModel();

  /** Creates an empty model. */
  public ModelBuilderHelper() {}

  /** Does nothing: the memory is managed by the garbage collector. */
  public void delete() {
    // nothing to release
  }

  /** The underlying model (shared, not a copy). */
  public MPModel model() {
    return model;
  }

  /** Replaces this model by a copy of the model of <code>other_helper</code>. */
  public void overwriteModel(ModelBuilderHelper other_helper) {
    model = other_helper.model.copy();
  }

  /** Not available without protocol buffers: returns false. */
  public boolean readModelFromProtoFile(String filename) {
    return false;
  }

  /** Not available without protocol buffers: returns false. */
  public boolean writeModelToProtoFile(String filename) {
    return false;
  }

  /** Replaces the model by the one given in MPS format; returns false on a parse error. */
  public boolean importFromMpsString(String mps_string) {
    try {
      model = MpsReader.parse(mps_string);
      return true;
    } catch (MpsReader.MpsException e) {
      return false;
    }
  }

  /** Replaces the model by the one of the given MPS file; returns false on an error. */
  public boolean importFromMpsFile(String mps_file) {
    try {
      return importFromMpsString(
          new String(Files.readAllBytes(Paths.get(mps_file)), StandardCharsets.UTF_8));
    } catch (IOException | RuntimeException e) {
      return false;
    }
  }

  /** Replaces the model by the one given in LP format; returns false on a parse error. */
  public boolean importFromLpString(String lp_string) {
    final MPModel m = LpParser.modelFromLpFormat(lp_string);
    if (m == null) {
      return false;
    }
    model = m;
    return true;
  }

  /** Replaces the model by the one of the given LP file; returns false on an error. */
  public boolean importFromLpFile(String lp_file) {
    try {
      return importFromLpString(
          new String(Files.readAllBytes(Paths.get(lp_file)), StandardCharsets.UTF_8));
    } catch (IOException | RuntimeException e) {
      return false;
    }
  }

  /** Adds a variable with bounds [-inf, +inf] and returns its index. */
  public int addVar() {
    final int index = model.variables.size();
    model.variables.add(new MPModel.Variable());
    return index;
  }

  /** Sets the lower bound of a variable. */
  public void setVarLowerBound(int var_index, double lb) {
    model.variables.get(var_index).lowerBound = lb;
  }

  /** Sets the upper bound of a variable. */
  public void setVarUpperBound(int var_index, double ub) {
    model.variables.get(var_index).upperBound = ub;
  }

  /** Sets the integrality of a variable. */
  public void setVarIntegrality(int var_index, boolean is_integer) {
    model.variables.get(var_index).isInteger = is_integer;
  }

  /** Sets the objective coefficient of a variable. */
  public void setVarObjectiveCoefficient(int var_index, double coeff) {
    model.variables.get(var_index).objectiveCoefficient = coeff;
  }

  /** Sets the name of a variable. */
  public void setVarName(int var_index, String name) {
    model.variables.get(var_index).name = name;
  }

  /** The lower bound of a variable. */
  public double getVarLowerBound(int var_index) {
    return model.variables.get(var_index).lowerBound;
  }

  /** The upper bound of a variable. */
  public double getVarUpperBound(int var_index) {
    return model.variables.get(var_index).upperBound;
  }

  /** The integrality of a variable. */
  public boolean getVarIntegrality(int var_index) {
    return model.variables.get(var_index).isInteger;
  }

  /** The objective coefficient of a variable. */
  public double getVarObjectiveCoefficient(int var_index) {
    return model.variables.get(var_index).objectiveCoefficient;
  }

  /** The name of a variable ("" if it has none). */
  public String getVarName(int var_index) {
    final String name = model.variables.get(var_index).name;
    return name == null ? "" : name;
  }

  /** The lower bound of a constraint. */
  public double getConstraintLowerBound(int ct_index) {
    return model.constraints.get(ct_index).lowerBound;
  }

  /** The upper bound of a constraint. */
  public double getConstraintUpperBound(int ct_index) {
    return model.constraints.get(ct_index).upperBound;
  }

  /** Adds a linear constraint with bounds [-inf, +inf] and returns its index. */
  public int addLinearConstraint() {
    final int index = model.constraints.size();
    model.constraints.add(new MPModel.Constraint());
    return index;
  }

  /** The name of a constraint ("" if it has none). */
  public String getConstraintName(int ct_index) {
    final String name = model.constraints.get(ct_index).name;
    return name == null ? "" : name;
  }

  /** The coefficients of a constraint. */
  public double[] getConstraintCoefficients(int ct_index) {
    final MPModel.Constraint c = model.constraints.get(ct_index);
    final double[] result = new double[c.coefficient.size()];
    for (int i = 0; i < result.length; ++i) {
      result[i] = c.coefficient.get(i);
    }
    return result;
  }

  /** The variable indices of a constraint. */
  public int[] getConstraintVarIndices(int ct_index) {
    final MPModel.Constraint c = model.constraints.get(ct_index);
    final int[] result = new int[c.varIndex.size()];
    for (int i = 0; i < result.length; ++i) {
      result[i] = c.varIndex.get(i);
    }
    return result;
  }

  /** Appends a term to a constraint (zero coefficients are ignored). */
  public void addConstraintTerm(int ct_index, int var_index, double coeff) {
    addTerm(model.constraints.get(ct_index), var_index, coeff);
  }

  /** Removes all the terms of a constraint. */
  public void clearConstraintTerms(int ct_index) {
    final MPModel.Constraint c = model.constraints.get(ct_index);
    c.varIndex.clear();
    c.coefficient.clear();
  }

  /** Adds a term to a constraint, merging it with an existing term of the same variable. */
  public void safeAddConstraintTerm(int ct_index, int var_index, double coeff) {
    safeAddTerm(model.constraints.get(ct_index), var_index, coeff);
  }

  /** Sets the coefficient of a variable in a constraint. */
  public void setConstraintCoefficient(int ct_index, int var_index, double coeff) {
    setCoefficient(model.constraints.get(ct_index), var_index, coeff);
  }

  /** Sets the lower bound of a constraint. */
  public void setConstraintLowerBound(int ct_index, double lb) {
    model.constraints.get(ct_index).lowerBound = lb;
  }

  /** Sets the name of a constraint. */
  public void setConstraintName(int ct_index, String name) {
    model.constraints.get(ct_index).name = name;
  }

  /** Sets the upper bound of a constraint. */
  public void setConstraintUpperBound(int ct_index, double ub) {
    model.constraints.get(ct_index).upperBound = ub;
  }

  /** The value of the indicator variable which enforces the constraint. */
  public boolean getEnforcedIndicatorValue(int ct_index) {
    return model.generalConstraints.get(ct_index).varValue != 0;
  }

  /** Returns true: all the general constraints of this helper are indicator constraints. */
  public boolean isEnforcedConstraint(int ct_index) {
    return ct_index >= 0 && ct_index < model.generalConstraints.size();
  }

  /** The lower bound of an enforced constraint. */
  public double getEnforcedConstraintLowerBound(int ct_index) {
    return model.generalConstraints.get(ct_index).constraint.lowerBound;
  }

  /** The upper bound of an enforced constraint. */
  public double getEnforcedConstraintUpperBound(int ct_index) {
    return model.generalConstraints.get(ct_index).constraint.upperBound;
  }

  /** Adds an enforced (indicator) constraint and returns its index. */
  public int addEnforcedLinearConstraint() {
    final int index = model.generalConstraints.size();
    model.generalConstraints.add(new MPModel.IndicatorConstraint());
    return index;
  }

  /** The index of the indicator variable of an enforced constraint. */
  public int getEnforcedIndicatorVariableIndex(int ct_index) {
    return model.generalConstraints.get(ct_index).varIndex;
  }

  /** The name of an enforced constraint ("" if it has none). */
  public String getEnforcedConstraintName(int ct_index) {
    final String name = model.generalConstraints.get(ct_index).name;
    return name == null ? "" : name;
  }

  /** The coefficients of an enforced constraint. */
  public double[] getEnforcedConstraintCoefficients(int ct_index) {
    final MPModel.Constraint c = model.generalConstraints.get(ct_index).constraint;
    final double[] result = new double[c.coefficient.size()];
    for (int i = 0; i < result.length; ++i) {
      result[i] = c.coefficient.get(i);
    }
    return result;
  }

  /** The variable indices of an enforced constraint. */
  public int[] getEnforcedConstraintVarIndices(int ct_index) {
    final MPModel.Constraint c = model.generalConstraints.get(ct_index).constraint;
    final int[] result = new int[c.varIndex.size()];
    for (int i = 0; i < result.length; ++i) {
      result[i] = c.varIndex.get(i);
    }
    return result;
  }

  /** Appends a term to an enforced constraint (zero coefficients are ignored). */
  public void addEnforcedConstraintTerm(int ct_index, int var_index, double coeff) {
    addTerm(model.generalConstraints.get(ct_index).constraint, var_index, coeff);
  }

  /** Removes all the terms of an enforced constraint. */
  public void clearEnforcedConstraintTerms(int ct_index) {
    final MPModel.Constraint c = model.generalConstraints.get(ct_index).constraint;
    c.varIndex.clear();
    c.coefficient.clear();
  }

  /** Adds a term to an enforced constraint, merging it with an existing term. */
  public void safeAddEnforcedConstraintTerm(int ct_index, int var_index, double coeff) {
    safeAddTerm(model.generalConstraints.get(ct_index).constraint, var_index, coeff);
  }

  /** Sets the coefficient of a variable in an enforced constraint. */
  public void setEnforcedConstraintCoefficient(int ct_index, int var_index, double coeff) {
    setCoefficient(model.generalConstraints.get(ct_index).constraint, var_index, coeff);
  }

  /** Sets the lower bound of an enforced constraint. */
  public void setEnforcedConstraintLowerBound(int ct_index, double lb) {
    model.generalConstraints.get(ct_index).constraint.lowerBound = lb;
  }

  /** Sets the name of an enforced constraint. */
  public void setEnforcedConstraintName(int ct_index, String name) {
    model.generalConstraints.get(ct_index).name = name;
  }

  /** Sets the upper bound of an enforced constraint. */
  public void setEnforcedConstraintUpperBound(int ct_index, double ub) {
    model.generalConstraints.get(ct_index).constraint.upperBound = ub;
  }

  /** Sets the value of the indicator variable which enforces the constraint. */
  public void setEnforcedIndicatorValue(int ct_index, boolean positive) {
    model.generalConstraints.get(ct_index).varValue = positive ? 1 : 0;
  }

  /** Sets the indicator variable of an enforced constraint. */
  public void setEnforcedIndicatorVariableIndex(int ct_index, int var_index) {
    model.generalConstraints.get(ct_index).varIndex = var_index;
  }

  /** The number of constraints, linear and enforced. */
  public int numConstraints() {
    return model.constraints.size() + model.generalConstraints.size();
  }

  /** The number of variables. */
  public int numVariables() {
    return model.variables.size();
  }

  /** The name of the model ("" if it has none). */
  public String getName() {
    return model.name == null ? "" : model.name;
  }

  /** Sets the name of the model. */
  public void setName(String name) {
    model.name = name;
  }

  /** Clears all the objective coefficients. */
  public void clearObjective() {
    for (MPModel.Variable v : model.variables) {
      v.objectiveCoefficient = 0.0;
    }
  }

  /** Whether the objective is maximized. */
  public boolean getMaximize() {
    return model.maximize;
  }

  /** Sets the optimization direction. */
  public void setMaximize(boolean maximize) {
    model.maximize = maximize;
  }

  /** The objective offset. */
  public double getObjectiveOffset() {
    return model.objectiveOffset;
  }

  /** Sets the objective offset. */
  public void setObjectiveOffset(double offset) {
    model.objectiveOffset = offset;
  }

  /** Removes the solution hint. */
  public void clearHints() {
    model.hintVarIndex.clear();
    model.hintVarValue.clear();
  }

  /** Adds var == value to the solution hint. */
  public void addHint(int var_index, double var_value) {
    model.hintVarIndex.add(var_index);
    model.hintVarValue.add(var_value);
  }

  /** The model in MPS format ("" if it cannot be exported). */
  public String exportToMpsString(boolean obfuscate) {
    final ModelExporter.Options options = new ModelExporter.Options();
    options.obfuscate = obfuscate;
    final String result = ModelExporter.exportModelAsMpsFormat(model, options);
    return result == null ? "" : result;
  }

  /** The model in LP format ("" if it cannot be exported). */
  public String exportToLpString(boolean obfuscate) {
    final ModelExporter.Options options = new ModelExporter.Options();
    options.obfuscate = obfuscate;
    final String result = ModelExporter.exportModelAsLpFormat(model, options);
    return result == null ? "" : result;
  }

  /** Writes the model to a file in MPS format; returns false on an error. */
  public boolean writeToMpsFile(String filename, boolean obfuscate) {
    final ModelExporter.Options options = new ModelExporter.Options();
    options.obfuscate = obfuscate;
    final String result = ModelExporter.exportModelAsMpsFormat(model, options);
    if (result == null) {
      return false;
    }
    try {
      Files.write(Paths.get(filename), result.getBytes(StandardCharsets.UTF_8));
      return true;
    } catch (IOException | RuntimeException e) {
      return false;
    }
  }

  private static void addTerm(MPModel.Constraint c, int varIndex, double coeff) {
    if (coeff == 0.0) {
      return;
    }
    c.varIndex.add(varIndex);
    c.coefficient.add(coeff);
  }

  private static void safeAddTerm(MPModel.Constraint c, int varIndex, double coeff) {
    if (coeff == 0.0) {
      return;
    }
    for (int i = 0; i < c.varIndex.size(); ++i) {
      if (c.varIndex.get(i) == varIndex) {
        c.coefficient.set(i, coeff + c.coefficient.get(i));
        return;
      }
    }
    c.varIndex.add(varIndex);
    c.coefficient.add(coeff);
  }

  private static void setCoefficient(MPModel.Constraint c, int varIndex, double coeff) {
    for (int i = 0; i < c.varIndex.size(); ++i) {
      if (c.varIndex.get(i) == varIndex) {
        c.coefficient.set(i, coeff);
        return;
      }
    }
    c.varIndex.add(varIndex);
    c.coefficient.add(coeff);
  }
}
