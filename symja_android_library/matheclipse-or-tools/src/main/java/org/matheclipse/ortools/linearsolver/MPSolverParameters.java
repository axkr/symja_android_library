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
// Modified for the Symja matheclipse-or-tools module: adapted from the OR-Tools Java wrapper
// (ortools/linear_solver/linear_solver.h and linear_solver.cc ported to Java), see NOTICE in the
// module root.

package org.matheclipse.ortools.linearsolver;

import java.util.logging.Logger;

/**
 * This class stores parameter settings for LP and MIP solvers. Some parameters are marked as
 * advanced: do not change their values unless you know what you are doing!
 *
 * <p>
 * For developers: how to add a new parameter:
 * <ul>
 * <li>Add the new Foo parameter in the DoubleParam or IntegerParam enum.
 * <li>If it is a categorical param, add a FooValues enum.
 * <li>Decide if the wrapper should define a default value for it: yes if it controls the
 * properties of the solution (example: tolerances) or if it consistently improves performance,
 * no otherwise. If yes, define kDefaultFoo.
 * </ul>
 */
public class MPSolverParameters {

  private static final Logger LOGGER = Logger.getLogger(MPSolverParameters.class.getName());

  /** The default value of a double parameter which has no default. */
  static final double K_DEFAULT_DOUBLE_PARAM_VALUE = -1.0;
  /** The default value of an integer parameter which has no default. */
  static final int K_DEFAULT_INTEGER_PARAM_VALUE = -1;
  /** The value returned for an unknown double parameter. */
  static final double K_UNKNOWN_DOUBLE_PARAM_VALUE = -2.0;
  /** The value returned for an unknown integer parameter. */
  static final int K_UNKNOWN_INTEGER_PARAM_VALUE = -2;

  private double relativeMipGapValue;
  private double primalToleranceValue;
  private double dualToleranceValue;
  private int presolveValue;
  private int scalingValue;
  private int lpAlgorithmValue;
  private int incrementalityValue;
  private boolean lpAlgorithmIsDefault;

  /** The default relative MIP gap, 1e-4. */
  public static double getKDefaultRelativeMipGap() {
    return 1e-4;
  }

  /** The default primal tolerance, 1e-7. */
  public static double getKDefaultPrimalTolerance() {
    return 1e-7;
  }

  /** The default dual tolerance, 1e-7. */
  public static double getKDefaultDualTolerance() {
    return 1e-7;
  }

  /** The default presolve mode, PRESOLVE_ON. */
  public static MPSolverParameters.PresolveValues getKDefaultPresolve() {
    return PresolveValues.PRESOLVE_ON;
  }

  /** The default incrementality, INCREMENTALITY_ON. */
  public static MPSolverParameters.IncrementalityValues getKDefaultIncrementality() {
    return IncrementalityValues.INCREMENTALITY_ON;
  }

  /** The constructor sets all parameters to their default value. */
  public MPSolverParameters() {
    reset();
  }

  /** Does nothing: the memory is managed by the garbage collector. */
  public void delete() {
    // nothing to release
  }

  /** Sets a double parameter to a specific value. */
  public void setDoubleParam(MPSolverParameters.DoubleParam param, double value) {
    switch (param) {
      case RELATIVE_MIP_GAP:
        relativeMipGapValue = value;
        break;
      case PRIMAL_TOLERANCE:
        primalToleranceValue = value;
        break;
      case DUAL_TOLERANCE:
        dualToleranceValue = value;
        break;
      default:
        LOGGER.severe("Trying to set an unknown parameter: " + param + ".");
    }
  }

  /** Sets a integer parameter to a specific value. */
  public void setIntegerParam(MPSolverParameters.IntegerParam param, int value) {
    switch (param) {
      case PRESOLVE:
        if (value != PresolveValues.PRESOLVE_OFF.swigValue()
            && value != PresolveValues.PRESOLVE_ON.swigValue()) {
          LOGGER.severe("Trying to set a supported parameter: " + param + " to an unknown value: "
              + value);
        }
        presolveValue = value;
        break;
      case SCALING:
        if (value != ScalingValues.SCALING_OFF.swigValue()
            && value != ScalingValues.SCALING_ON.swigValue()) {
          LOGGER.severe("Trying to set a supported parameter: " + param + " to an unknown value: "
              + value);
        }
        scalingValue = value;
        break;
      case LP_ALGORITHM:
        if (value != LpAlgorithmValues.DUAL.swigValue()
            && value != LpAlgorithmValues.PRIMAL.swigValue()
            && value != LpAlgorithmValues.BARRIER.swigValue()) {
          LOGGER.severe("Trying to set a supported parameter: " + param + " to an unknown value: "
              + value);
        }
        lpAlgorithmValue = value;
        lpAlgorithmIsDefault = false;
        break;
      case INCREMENTALITY:
        if (value != IncrementalityValues.INCREMENTALITY_OFF.swigValue()
            && value != IncrementalityValues.INCREMENTALITY_ON.swigValue()) {
          LOGGER.severe("Trying to set a supported parameter: " + param + " to an unknown value: "
              + value);
        }
        incrementalityValue = value;
        break;
      default:
        LOGGER.severe("Trying to set an unknown parameter: " + param + ".");
    }
  }

  /** Returns the value of a double parameter. */
  public double getDoubleParam(MPSolverParameters.DoubleParam param) {
    switch (param) {
      case RELATIVE_MIP_GAP:
        return relativeMipGapValue;
      case PRIMAL_TOLERANCE:
        return primalToleranceValue;
      case DUAL_TOLERANCE:
        return dualToleranceValue;
      default:
        LOGGER.severe("Trying to get an unknown parameter: " + param + ".");
        return K_UNKNOWN_DOUBLE_PARAM_VALUE;
    }
  }

  /** Returns the value of an integer parameter. */
  public int getIntegerParam(MPSolverParameters.IntegerParam param) {
    switch (param) {
      case PRESOLVE:
        return presolveValue;
      case LP_ALGORITHM:
        if (lpAlgorithmIsDefault) {
          return K_DEFAULT_INTEGER_PARAM_VALUE;
        }
        return lpAlgorithmValue;
      case INCREMENTALITY:
        return incrementalityValue;
      case SCALING:
        return scalingValue;
      default:
        LOGGER.severe("Trying to get an unknown parameter: " + param + ".");
        return K_UNKNOWN_INTEGER_PARAM_VALUE;
    }
  }

  /** Sets a double parameter to its default value. */
  public void resetDoubleParam(MPSolverParameters.DoubleParam param) {
    switch (param) {
      case RELATIVE_MIP_GAP:
        relativeMipGapValue = getKDefaultRelativeMipGap();
        break;
      case PRIMAL_TOLERANCE:
        primalToleranceValue = getKDefaultPrimalTolerance();
        break;
      case DUAL_TOLERANCE:
      default:
        dualToleranceValue = getKDefaultDualTolerance();
        break;
    }
  }

  /** Sets an integer parameter to its default value. */
  public void resetIntegerParam(MPSolverParameters.IntegerParam param) {
    switch (param) {
      case PRESOLVE:
        presolveValue = getKDefaultPresolve().swigValue();
        break;
      case SCALING:
        scalingValue = K_DEFAULT_INTEGER_PARAM_VALUE;
        break;
      case LP_ALGORITHM:
        lpAlgorithmIsDefault = true;
        break;
      case INCREMENTALITY:
      default:
        incrementalityValue = getKDefaultIncrementality().swigValue();
        break;
    }
  }

  /** Sets all parameters to their default value. */
  public void reset() {
    relativeMipGapValue = getKDefaultRelativeMipGap();
    primalToleranceValue = getKDefaultPrimalTolerance();
    dualToleranceValue = getKDefaultDualTolerance();
    presolveValue = getKDefaultPresolve().swigValue();
    scalingValue = K_DEFAULT_INTEGER_PARAM_VALUE;
    lpAlgorithmValue = K_DEFAULT_INTEGER_PARAM_VALUE;
    lpAlgorithmIsDefault = true;
    incrementalityValue = getKDefaultIncrementality().swigValue();
  }

  /** Enumeration of parameters that take continuous values. */
  public enum DoubleParam {
    /** Limit for relative MIP gap. */
    RELATIVE_MIP_GAP(0),
    /**
     * Advanced usage: tolerance for primal feasibility of basic solutions.
     *
     * <p>
     * This does not control the integer feasibility tolerance of integer solutions for MIP or the
     * tolerance used during presolve.
     */
    PRIMAL_TOLERANCE(1),
    /** Advanced usage: tolerance for dual feasibility of basic solutions. */
    DUAL_TOLERANCE(2);

    private final int swigValue;

    DoubleParam(int swigValue) {
      this.swigValue = swigValue;
    }

    /** The C++ value of the enum. */
    public final int swigValue() {
      return swigValue;
    }

    /** The enum with the given C++ value. */
    public static DoubleParam swigToEnum(int swigValue) {
      for (DoubleParam e : values()) {
        if (e.swigValue == swigValue) {
          return e;
        }
      }
      throw new IllegalArgumentException(
          "No enum " + DoubleParam.class + " with value " + swigValue);
    }
  }

  /** Enumeration of parameters that take integer or categorical values. */
  public enum IntegerParam {
    /** Advanced usage: presolve mode. */
    PRESOLVE(1000),
    /** Algorithm to solve linear programs. */
    LP_ALGORITHM(1001),
    /** Advanced usage: incrementality from one solve to the next. */
    INCREMENTALITY(1002),
    /** Advanced usage: enable or disable matrix scaling. */
    SCALING(1003);

    private final int swigValue;

    IntegerParam(int swigValue) {
      this.swigValue = swigValue;
    }

    /** The C++ value of the enum. */
    public final int swigValue() {
      return swigValue;
    }

    /** The enum with the given C++ value. */
    public static IntegerParam swigToEnum(int swigValue) {
      for (IntegerParam e : values()) {
        if (e.swigValue == swigValue) {
          return e;
        }
      }
      throw new IllegalArgumentException(
          "No enum " + IntegerParam.class + " with value " + swigValue);
    }
  }

  /** For each categorical parameter, enumeration of possible values. */
  public enum PresolveValues {
    /** Presolve is off. */
    PRESOLVE_OFF(0),
    /** Presolve is on. */
    PRESOLVE_ON(1);

    private final int swigValue;

    PresolveValues(int swigValue) {
      this.swigValue = swigValue;
    }

    /** The C++ value of the enum. */
    public final int swigValue() {
      return swigValue;
    }

    /** The enum with the given C++ value. */
    public static PresolveValues swigToEnum(int swigValue) {
      for (PresolveValues e : values()) {
        if (e.swigValue == swigValue) {
          return e;
        }
      }
      throw new IllegalArgumentException(
          "No enum " + PresolveValues.class + " with value " + swigValue);
    }
  }

  /** LP algorithm to use. */
  public enum LpAlgorithmValues {
    /** Dual simplex. */
    DUAL(10),
    /** Primal simplex. */
    PRIMAL(11),
    /** Barrier algorithm. */
    BARRIER(12);

    private final int swigValue;

    LpAlgorithmValues(int swigValue) {
      this.swigValue = swigValue;
    }

    /** The C++ value of the enum. */
    public final int swigValue() {
      return swigValue;
    }

    /** The enum with the given C++ value. */
    public static LpAlgorithmValues swigToEnum(int swigValue) {
      for (LpAlgorithmValues e : values()) {
        if (e.swigValue == swigValue) {
          return e;
        }
      }
      throw new IllegalArgumentException(
          "No enum " + LpAlgorithmValues.class + " with value " + swigValue);
    }
  }

  /** Advanced usage: Incrementality options. */
  public enum IncrementalityValues {
    /** Start solve from scratch. */
    INCREMENTALITY_OFF(0),
    /** Reuse results from previous solve as much as the underlying solver allows. */
    INCREMENTALITY_ON(1);

    private final int swigValue;

    IncrementalityValues(int swigValue) {
      this.swigValue = swigValue;
    }

    /** The C++ value of the enum. */
    public final int swigValue() {
      return swigValue;
    }

    /** The enum with the given C++ value. */
    public static IncrementalityValues swigToEnum(int swigValue) {
      for (IncrementalityValues e : values()) {
        if (e.swigValue == swigValue) {
          return e;
        }
      }
      throw new IllegalArgumentException(
          "No enum " + IncrementalityValues.class + " with value " + swigValue);
    }
  }

  /** Advanced usage: Scaling options. */
  public enum ScalingValues {
    /** Scaling is off. */
    SCALING_OFF(0),
    /** Scaling is on. */
    SCALING_ON(1);

    private final int swigValue;

    ScalingValues(int swigValue) {
      this.swigValue = swigValue;
    }

    /** The C++ value of the enum. */
    public final int swigValue() {
      return swigValue;
    }

    /** The enum with the given C++ value. */
    public static ScalingValues swigToEnum(int swigValue) {
      for (ScalingValues e : values()) {
        if (e.swigValue == swigValue) {
          return e;
        }
      }
      throw new IllegalArgumentException(
          "No enum " + ScalingValues.class + " with value " + swigValue);
    }
  }
}
