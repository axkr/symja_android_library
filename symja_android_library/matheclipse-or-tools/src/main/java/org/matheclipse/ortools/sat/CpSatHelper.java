package org.matheclipse.ortools.sat;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;
import org.matheclipse.ortools.sat.engine.CpModelValidator;
import org.matheclipse.ortools.util.Domain;

/**
 * Static helpers on models and responses; replaces the SWIG class {@code CpSatHelper} of the
 * OR-Tools Java wrapper.
 */
public class CpSatHelper {

  /** Creates a helper (all methods are static). */
  public CpSatHelper() {}

  /** Returns some statistics on the model as a string. */
  public static String modelStats(CpModelProto model) {
    StringBuilder sb = new StringBuilder();
    String name = model.getName();
    sb.append("CpModelProto");
    if (!name.isEmpty()) {
      sb.append(" '").append(name).append("'");
    }
    sb.append(":\n");
    long numBools = 0;
    long numInObjective = 0;
    java.util.Set<Integer> objectiveVars = new java.util.HashSet<>(model.getObjective().getVarsList());
    objectiveVars.addAll(model.getFloatingPointObjective().getVarsList());
    Map<String, Integer> domains = new TreeMap<>();
    for (int v = 0; v < model.getVariablesCount(); v++) {
      IntegerVariableProto var = model.getVariables(v);
      Domain d = variableDomain(var);
      if (!d.isEmpty() && d.min() >= 0 && d.max() <= 1) {
        numBools++;
      }
      if (objectiveVars.contains(v)) {
        numInObjective++;
      }
      domains.merge(d.toString(), 1, Integer::sum);
    }
    if (model.hasObjective() || model.hasFloatingPointObjective()) {
      sb.append("#Variables: ").append(model.getVariablesCount()).append(" (")
          .append(numInObjective).append(" in objective)\n");
    } else {
      sb.append("#Variables: ").append(model.getVariablesCount()).append('\n');
    }
    sb.append("  - ").append(numBools).append(" Booleans\n");
    for (Map.Entry<String, Integer> e : domains.entrySet()) {
      sb.append("  - ").append(e.getValue()).append(" in ").append(e.getKey()).append('\n');
    }
    Map<String, Integer> constraints = new TreeMap<>();
    for (ConstraintProto ct : model.getConstraintsList()) {
      String kind = constraintName(ct.getConstraintCase());
      if (ct.getEnforcementLiteralCount() > 0) {
        kind += " (enforced)";
      }
      constraints.merge(kind, 1, Integer::sum);
    }
    for (Map.Entry<String, Integer> e : constraints.entrySet()) {
      sb.append('#').append(e.getKey()).append(": ").append(e.getValue()).append('\n');
    }
    return sb.toString();
  }

  private static String constraintName(ConstraintProto.ConstraintCase c) {
    StringBuilder sb = new StringBuilder("k");
    boolean upper = true;
    for (char ch : c.name().toLowerCase(java.util.Locale.ROOT).toCharArray()) {
      if (ch == '_') {
        upper = true;
      } else if (upper) {
        sb.append(Character.toUpperCase(ch));
        upper = false;
      } else {
        sb.append(ch);
      }
    }
    return sb.toString();
  }

  /** Returns some statistics on the solver response as a string. */
  public static String solverResponseStats(CpSolverResponse response) {
    StringBuilder sb = new StringBuilder();
    sb.append("CpSolverResponse summary:\n");
    sb.append("status: ").append(response.getStatus().name()).append('\n');
    boolean hasSolution = response.getStatus() == CpSolverStatus.OPTIMAL
        || response.getStatus() == CpSolverStatus.FEASIBLE;
    if (hasSolution && response.getSolutionCount() > 0) {
      sb.append("objective: ").append(ProtoPrinter.formatDouble(response.getObjectiveValue()))
          .append('\n');
      sb.append("best_bound: ")
          .append(ProtoPrinter.formatDouble(response.getBestObjectiveBound())).append('\n');
    } else {
      sb.append("objective: NA\n");
      sb.append("best_bound: NA\n");
    }
    sb.append("integers: ").append(response.getNumIntegers()).append('\n');
    sb.append("booleans: ").append(response.getNumBooleans()).append('\n');
    sb.append("conflicts: ").append(response.getNumConflicts()).append('\n');
    sb.append("branches: ").append(response.getNumBranches()).append('\n');
    sb.append("propagations: ").append(response.getNumBinaryPropagations()).append('\n');
    sb.append("integer_propagations: ").append(response.getNumIntegerPropagations()).append('\n');
    sb.append("restarts: ").append(response.getNumRestarts()).append('\n');
    sb.append("lp_iterations: ").append(response.getNumLpIterations()).append('\n');
    sb.append("walltime: ").append(response.getWallTime()).append('\n');
    sb.append("usertime: ").append(response.getUserTime()).append('\n');
    sb.append("deterministic_time: ").append(response.getDeterministicTime()).append('\n');
    sb.append("gap_integral: ").append(response.getGapIntegral()).append('\n');
    if (response.getSolutionCount() > 0) {
      sb.append("solution_fingerprint: 0x")
          .append(Long.toHexString(response.getSolutionList().hashCode() & 0xffffffffL))
          .append('\n');
    }
    return sb.toString();
  }

  /** Returns "" if the model is valid, otherwise a message describing the first problem. */
  public static String validateModel(CpModelProto model) {
    return CpModelValidator.validate(model);
  }

  /** Returns the domain of a variable. */
  public static Domain variableDomain(IntegerVariableProto variable) {
    long[] flat = new long[variable.getDomainCount()];
    for (int i = 0; i < flat.length; i++) {
      flat[i] = variable.getDomain(i);
    }
    return Domain.fromFlatIntervals(flat);
  }

  /**
   * Writes the model to the file: in protobuf text format if the file name ends with "txt" or
   * "textproto", otherwise in the protobuf binary format. Returns false if the file cannot be
   * written.
   */
  public static boolean writeModelToFile(CpModelProto model, String filename) {
    if (filename.endsWith("txt") || filename.endsWith("textproto")) {
      try (Writer w = Files.newBufferedWriter(Paths.get(filename), StandardCharsets.UTF_8)) {
        w.write(TextFormat.printToString(model));
        return true;
      } catch (IOException | RuntimeException e) {
        return false;
      }
    }
    try {
      Files.write(Paths.get(filename), model.toByteArray());
      return true;
    } catch (IOException | RuntimeException e) {
      return false;
    }
  }
}
