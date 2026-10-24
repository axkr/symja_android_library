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
// ortools/lp_data/mps_reader_template.h/.cc and mps_reader.cc C++ to Java, see NOTICE in the module
// root.

package org.matheclipse.ortools.linearsolver.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Reads a model in MPS format, fixed or free (the format is detected automatically: fixed first,
 * then free). Supported sections: NAME, OBJSENSE (also inline), ROWS, LAZYCONS, COLUMNS (with the
 * INTORG/INTEND markers), RHS (the right hand side of the objective row is minus the objective
 * offset), RANGES, BOUNDS (LO, UP, FX, FR, MI, PL, BV, LI, UI, SC), INDICATORS and ENDATA.
 */
public final class MpsReader {

  /** Thrown on a syntax error. */
  public static final class MpsException extends Exception {
    private static final long serialVersionUID = 1L;

    MpsException(String message, int lineNum, String line) {
      super(message + " Line " + lineNum + ": \"" + line + "\".");
    }
  }

  private static final int K_NUM_MPS_FIELDS = 6;
  private static final int[] K_FIELD_START_POS = {1, 4, 14, 24, 39, 49};
  private static final int[] K_FIELD_LENGTH = {2, 8, 8, 12, 8, 12};
  private static final int[] K_SPACE_POS = {12, 13, 22, 23, 36, 37, 38, 47, 48, 61, 62, 63};

  private enum Section {
    UNKNOWN, NAME, OBJSENSE, ROWS, LAZYCONS, COLUMNS, RHS, RANGES, BOUNDS, INDICATORS, ENDATA
  }

  private boolean freeForm;
  private Section section = Section.UNKNOWN;
  private int lineNum;
  private boolean inIntegerSection;
  private String objectiveName = "";
  private final List<Boolean> isBinaryByDefault = new ArrayList<>();
  private MPModel model;
  private final Map<String, Integer> variableIndicesByName = new HashMap<>();
  private final Map<String, Integer> constraintIndicesByName = new HashMap<>();
  private final TreeSet<Integer> constraintsToDelete = new TreeSet<>();
  private final List<Integer> semiContinuousVariables = new ArrayList<>();

  /** Parses MPS data, the format being detected automatically. */
  public static MPModel parse(String mpsData) throws MpsException {
    try {
      return new MpsReader().parseString(mpsData, false);
    } catch (MpsException e) {
      return new MpsReader().parseString(mpsData, true);
    }
  }

  private MPModel parseString(String source, boolean free) throws MpsException {
    freeForm = free;
    model = new MPModel();
    for (String line : source.split("\n", -1)) {
      processLine(line);
    }
    cleanUp();
    return model;
  }

  /** The fields of a line. */
  private final class LineInfo {
    final String line;
    final List<String> fields = new ArrayList<>();

    LineInfo(String rawLine) throws MpsException {
      line = stripTrailing(rawLine);
      if (!freeForm && line.indexOf('\t') >= 0) {
        throw new MpsException("File contains tabs.", lineNum, line);
      }
      if (!freeForm && !isFixedFormat()) {
        throw new MpsException("Line is not in fixed format.", lineNum, line);
      }
      if (!isCommentOrBlank()) {
        splitLineIntoFields();
      }
    }

    boolean isCommentOrBlank() {
      return line.isEmpty() || line.charAt(0) == '*';
    }

    boolean isNewSection() {
      return !line.isEmpty() && line.charAt(0) != ' ';
    }

    String getFirstWord() {
      final int pos = line.indexOf(' ');
      return pos < 0 ? line : line.substring(0, pos);
    }

    boolean isFixedFormat() {
      if (isCommentOrBlank()) {
        return true;
      }
      if (isNewSection()) {
        final String firstWord = getFirstWord();
        return firstWord.equals(line) || firstWord.equals("NAME");
      }
      final int kMaxLineSize =
          K_FIELD_START_POS[K_NUM_MPS_FIELDS - 1] + K_FIELD_LENGTH[K_NUM_MPS_FIELDS - 1];
      final int lineSize = line.length();
      if (lineSize > kMaxLineSize) {
        return false;
      }
      for (int i : K_SPACE_POS) {
        if (i >= lineSize) {
          break;
        }
        if (line.charAt(i) != ' ') {
          return false;
        }
      }
      return true;
    }

    void splitLineIntoFields() throws MpsException {
      if (freeForm) {
        for (String f : line.trim().split("[ \t]+")) {
          if (f.isEmpty()) {
            continue;
          }
          if (fields.size() == K_NUM_MPS_FIELDS) {
            throw new MpsException("Found too many fields.", lineNum, line);
          }
          fields.add(f);
        }
      } else {
        final int lineSize = line.length();
        for (int i = 0; i < K_NUM_MPS_FIELDS; ++i) {
          if (K_FIELD_START_POS[i] >= lineSize) {
            break;
          }
          final int end = Math.min(lineSize, K_FIELD_START_POS[i] + K_FIELD_LENGTH[i]);
          fields.add(stripTrailing(line.substring(K_FIELD_START_POS[i], end)));
        }
      }
    }

    int fieldOffset() {
      return freeForm ? (fields.size() & 1) : 0;
    }

    MpsException error(String message) {
      return new MpsException(message, lineNum, line);
    }
  }

  private static String stripTrailing(String s) {
    int end = s.length();
    while (end > 0 && Character.isWhitespace(s.charAt(end - 1))) {
      --end;
    }
    return s.substring(0, end);
  }

  private void processLine(String rawLine) throws MpsException {
    ++lineNum;
    final LineInfo info = new LineInfo(rawLine);
    if (info.isCommentOrBlank()) {
      return;
    }
    if (info.isNewSection()) {
      try {
        section = Section.valueOf(info.getFirstWord());
      } catch (IllegalArgumentException e) {
        throw info.error("Unknown section.");
      }
      if (section == Section.UNKNOWN) {
        throw info.error("Unknown section.");
      }
      if (section == Section.NAME) {
        if (freeForm) {
          if (info.fields.size() >= 2) {
            model.name = info.fields.get(1);
          }
        } else {
          final String[] freeFields = info.line.trim().split("[ \t]+");
          final String freeName = freeFields.length >= 2 ? freeFields[1] : "";
          final String fixedName = info.fields.size() >= 3 ? info.fields.get(2) : "";
          if (!freeName.equals(fixedName)) {
            throw info.error("Fixed form invalid: name differs between free and fixed forms.");
          }
          model.name = fixedName;
        }
      }
      if (section == Section.OBJSENSE && info.fields.size() == 2 && freeForm) {
        if (info.fields.get(1).contains("MIN")) {
          model.maximize = false;
        } else if (info.fields.get(1).contains("MAX")) {
          model.maximize = true;
        } else {
          throw info.error("Invalid inline objective direction.");
        }
      }
      return;
    }
    switch (section) {
      case NAME:
        throw info.error("Second NAME field.");
      case OBJSENSE: {
        final String field = info.line.trim();
        if (!field.equals("MIN") && !field.equals("MAX")) {
          throw info.error("Expected objective sense (MAX or MIN).");
        }
        model.maximize = field.equals("MAX");
        break;
      }
      case ROWS:
        processRowsSection(info, false);
        break;
      case LAZYCONS:
        processRowsSection(info, true);
        break;
      case COLUMNS:
        processColumnsSection(info);
        break;
      case RHS:
      case RANGES: {
        final int startIndex = freeForm ? 0 : 2;
        final int offset = startIndex + info.fieldOffset();
        if (info.fields.size() < offset + 2) {
          throw info.error("Not enough fields in RHS section.");
        }
        final boolean rhs = section == Section.RHS;
        store(info, rhs, info.fields.get(offset), info.fields.get(offset + 1));
        if (info.fields.size() - startIndex >= 4) {
          store(info, rhs, info.fields.get(offset + 2), info.fields.get(offset + 3));
        }
        break;
      }
      case BOUNDS: {
        if (info.fields.size() < 3) {
          throw info.error("Not enough fields in BOUNDS section.");
        }
        storeBound(info, info.fields.get(0), info.fields.get(2),
            info.fields.size() >= 4 ? info.fields.get(3) : "");
        break;
      }
      case INDICATORS:
        processIndicatorsSection(info);
        break;
      case ENDATA:
        break;
      default:
        throw info.error("Unknown section.");
    }
  }

  private void store(LineInfo info, boolean rhs, String rowName, String value)
      throws MpsException {
    if (rhs) {
      storeRightHandSide(info, rowName, value);
    } else {
      storeRange(info, rowName, value);
    }
  }

  private int findOrCreateConstraint(String name) {
    final Integer index = constraintIndicesByName.get(name);
    if (index != null) {
      return index;
    }
    final int newIndex = model.constraints.size();
    final MPModel.Constraint c = new MPModel.Constraint();
    c.lowerBound = 0.0;
    c.upperBound = 0.0;
    c.name = name;
    model.constraints.add(c);
    constraintIndicesByName.put(name, newIndex);
    return newIndex;
  }

  private int findOrCreateVariable(String name) {
    final Integer index = variableIndicesByName.get(name);
    if (index != null) {
      return index;
    }
    final int newIndex = model.variables.size();
    final MPModel.Variable v = new MPModel.Variable();
    v.lowerBound = 0.0;
    v.name = name;
    model.variables.add(v);
    variableIndicesByName.put(name, newIndex);
    return newIndex;
  }

  private void processRowsSection(LineInfo info, boolean isLazy) throws MpsException {
    if (info.fields.size() < 2) {
      throw info.error("Not enough fields in ROWS section.");
    }
    final String rowTypeName = info.fields.get(0);
    final String rowName = info.fields.get(1);
    if (!rowTypeName.equals("E") && !rowTypeName.equals("L") && !rowTypeName.equals("G")
        && !rowTypeName.equals("N")) {
      throw info.error("Unknown row type.");
    }
    if (objectiveName.isEmpty() && rowTypeName.equals("N")) {
      objectiveName = rowName;
      return;
    }
    final int row = findOrCreateConstraint(rowName);
    final MPModel.Constraint c = model.constraints.get(row);
    if (isLazy) {
      c.isLazy = true;
    }
    switch (rowTypeName) {
      case "L":
        c.lowerBound = Double.NEGATIVE_INFINITY;
        break;
      case "G":
        c.upperBound = Double.POSITIVE_INFINITY;
        break;
      case "N":
        c.lowerBound = Double.NEGATIVE_INFINITY;
        c.upperBound = Double.POSITIVE_INFINITY;
        break;
      default:
        break;
    }
  }

  private void extendBinaryByDefault(int col) {
    while (isBinaryByDefault.size() <= col) {
      isBinaryByDefault.add(false);
    }
  }

  private void processColumnsSection(LineInfo info) throws MpsException {
    if (info.line.contains("'MARKER'")) {
      if (info.line.contains("'INTORG'")) {
        if (inIntegerSection) {
          throw info.error("Found INTORG inside the integer section.");
        }
        inIntegerSection = true;
      } else if (info.line.contains("'INTEND'")) {
        if (!inIntegerSection) {
          throw info.error("Found INTEND without corresponding INTORG.");
        }
        inIntegerSection = false;
      }
      return;
    }
    final int startIndex = freeForm ? 0 : 1;
    if (info.fields.size() < startIndex + 3) {
      throw info.error("Not enough fields in COLUMNS section.");
    }
    final String columnName = info.fields.get(startIndex);
    final String row1Name = info.fields.get(startIndex + 1);
    final String row1Value = info.fields.get(startIndex + 2);
    final int col = findOrCreateVariable(columnName);
    extendBinaryByDefault(col);
    final MPModel.Variable v = model.variables.get(col);
    if (inIntegerSection) {
      v.isInteger = true;
      v.lowerBound = 0.0;
      v.upperBound = 1.0;
      isBinaryByDefault.set(col, true);
    } else {
      v.lowerBound = 0.0;
      v.upperBound = Double.POSITIVE_INFINITY;
    }
    storeCoefficient(info, col, row1Name, row1Value);
    if (info.fields.size() == startIndex + 4) {
      throw info.error("Unexpected number of fields.");
    }
    if (info.fields.size() - startIndex > 4) {
      storeCoefficient(info, col, info.fields.get(startIndex + 3),
          info.fields.get(startIndex + 4));
    }
  }

  private void processIndicatorsSection(LineInfo info) throws MpsException {
    if (info.fields.size() < 4) {
      throw info.error("Not enough fields in INDICATORS section.");
    }
    if (!info.fields.get(0).equals("IF")) {
      throw info.error("Indicator constraints must start with \"IF\".");
    }
    final String rowName = info.fields.get(1);
    final String columnName = info.fields.get(2);
    final int value;
    try {
      value = Integer.parseInt(info.fields.get(3));
    } catch (NumberFormatException e) {
      throw info.error("Failed to convert \"" + info.fields.get(3) + "\" to bool.");
    }
    if (value < 0 || value > 1) {
      throw info.error("Failed to convert \"" + info.fields.get(3) + "\" to bool.");
    }
    final int col = findOrCreateVariable(columnName);
    final MPModel.Variable v = model.variables.get(col);
    v.isInteger = true;
    v.lowerBound = Math.max(0.0, v.lowerBound);
    v.upperBound = Math.min(1.0, v.upperBound);
    final Integer ct = constraintIndicesByName.get(rowName);
    if (ct == null) {
      throw info.error("Constraint \"" + rowName + "\" doesn't exist.");
    }
    final MPModel.IndicatorConstraint ind = new MPModel.IndicatorConstraint();
    ind.name = "ind_" + model.constraints.get(ct).name;
    ind.constraint = model.constraints.get(ct).copy();
    ind.varIndex = col;
    ind.varValue = value;
    model.generalConstraints.add(ind);
    constraintsToDelete.add(ct);
  }

  private static double parseDouble(LineInfo info, String str) throws MpsException {
    final double result;
    try {
      final String s = str.trim();
      if (s.equalsIgnoreCase("inf") || s.equalsIgnoreCase("+inf")
          || s.equalsIgnoreCase("infinity") || s.equalsIgnoreCase("+infinity")) {
        return Double.POSITIVE_INFINITY;
      }
      if (s.equalsIgnoreCase("-inf") || s.equalsIgnoreCase("-infinity")) {
        return Double.NEGATIVE_INFINITY;
      }
      result = Double.parseDouble(s);
    } catch (NumberFormatException e) {
      throw info.error("Failed to convert \"" + str + "\" to double.");
    }
    if (Double.isNaN(result)) {
      throw info.error("Found NaN value.");
    }
    return result;
  }

  private void storeCoefficient(LineInfo info, int col, String rowName, String rowValue)
      throws MpsException {
    if (rowName.isEmpty() || rowName.equals("$")) {
      return;
    }
    final double value = parseDouble(info, rowValue);
    if (Double.isInfinite(value)) {
      throw info.error("Constraint coefficients cannot be infinity.");
    }
    if (value == 0.0) {
      return;
    }
    if (rowName.equals(objectiveName)) {
      model.variables.get(col).objectiveCoefficient = value;
    } else {
      final int row = findOrCreateConstraint(rowName);
      model.constraints.get(row).varIndex.add(col);
      model.constraints.get(row).coefficient.add(value);
    }
  }

  private void storeRightHandSide(LineInfo info, String rowName, String rowValue)
      throws MpsException {
    if (rowName.isEmpty()) {
      return;
    }
    final double value = parseDouble(info, rowValue);
    if (!rowName.equals(objectiveName)) {
      final MPModel.Constraint c = model.constraints.get(findOrCreateConstraint(rowName));
      final double lowerBound =
          (c.lowerBound == Double.NEGATIVE_INFINITY) ? Double.NEGATIVE_INFINITY : value;
      final double upperBound =
          (c.upperBound == Double.POSITIVE_INFINITY) ? Double.POSITIVE_INFINITY : value;
      c.lowerBound = lowerBound;
      c.upperBound = upperBound;
    } else {
      model.objectiveOffset = -value;
    }
  }

  private void storeRange(LineInfo info, String rowName, String rangeValue)
      throws MpsException {
    if (rowName.isEmpty()) {
      return;
    }
    final MPModel.Constraint c = model.constraints.get(findOrCreateConstraint(rowName));
    final double range = parseDouble(info, rangeValue);
    double lowerBound = c.lowerBound;
    double upperBound = c.upperBound;
    if (lowerBound == upperBound) {
      if (range < 0.0) {
        lowerBound += range;
      } else {
        upperBound += range;
      }
    }
    if (lowerBound == Double.NEGATIVE_INFINITY) {
      lowerBound = upperBound - Math.abs(range);
    }
    if (upperBound == Double.POSITIVE_INFINITY) {
      upperBound = lowerBound + Math.abs(range);
    }
    c.lowerBound = lowerBound;
    c.upperBound = upperBound;
  }

  private void storeBound(LineInfo info, String mnemonic, String columnName, String boundValue)
      throws MpsException {
    switch (mnemonic) {
      case "LO":
      case "UP":
      case "FX":
      case "FR":
      case "MI":
      case "PL":
      case "BV":
      case "LI":
      case "UI":
      case "SC":
        break;
      default:
        throw info.error("Unknown bound type.");
    }
    final int col = findOrCreateVariable(columnName);
    final MPModel.Variable v = model.variables.get(col);
    if (mnemonic.equals("BV") || mnemonic.equals("LI") || mnemonic.equals("UI")) {
      v.isInteger = true;
    }
    extendBinaryByDefault(col);
    double lowerBound = v.lowerBound;
    double upperBound = v.upperBound;
    if (isBinaryByDefault.get(col)) {
      lowerBound = 0.0;
      upperBound = Double.POSITIVE_INFINITY;
    }
    switch (mnemonic) {
      case "LO":
      case "LI":
        lowerBound = parseDouble(info, boundValue);
        if (mnemonic.equals("LI") && lowerBound == 0.0) {
          upperBound = Double.POSITIVE_INFINITY;
        }
        break;
      case "UP":
      case "UI":
        upperBound = parseDouble(info, boundValue);
        break;
      case "SC":
        upperBound = parseDouble(info, boundValue);
        semiContinuousVariables.add(col);
        break;
      case "FX":
        lowerBound = parseDouble(info, boundValue);
        upperBound = lowerBound;
        break;
      case "FR":
        lowerBound = Double.NEGATIVE_INFINITY;
        upperBound = Double.POSITIVE_INFINITY;
        break;
      case "MI":
        lowerBound = Double.NEGATIVE_INFINITY;
        break;
      case "PL":
        upperBound = Double.POSITIVE_INFINITY;
        break;
      case "BV":
      default:
        lowerBound = 0.0;
        upperBound = 1.0;
        break;
    }
    isBinaryByDefault.set(col, false);
    v.lowerBound = lowerBound;
    v.upperBound = upperBound;
  }

  private void cleanUp() {
    if (!constraintsToDelete.isEmpty()) {
      final List<MPModel.Constraint> kept = new ArrayList<>();
      for (int i = 0; i < model.constraints.size(); ++i) {
        if (!constraintsToDelete.contains(i)) {
          kept.add(model.constraints.get(i));
        }
      }
      model.constraints.clear();
      model.constraints.addAll(kept);
    }
    for (int index : semiContinuousVariables) {
      final MPModel.Variable v = model.variables.get(index);
      final double lb = v.lowerBound == 0 ? 1.0 : v.lowerBound;
      final double ub = v.upperBound;
      v.lowerBound = 0.0;
      final int boolVarIndex = model.variables.size();
      final MPModel.Variable b = new MPModel.Variable();
      b.lowerBound = 0.0;
      b.upperBound = 1.0;
      b.isInteger = true;
      model.variables.add(b);
      if (ub >= 1e8) {
        final MPModel.IndicatorConstraint zero = new MPModel.IndicatorConstraint();
        zero.varIndex = boolVarIndex;
        zero.varValue = 0;
        zero.constraint.lowerBound = 0.0;
        zero.constraint.upperBound = 0.0;
        zero.constraint.varIndex.add(index);
        zero.constraint.coefficient.add(1.0);
        model.generalConstraints.add(zero);
        final MPModel.IndicatorConstraint one = new MPModel.IndicatorConstraint();
        one.varIndex = boolVarIndex;
        one.varValue = 1;
        one.constraint.lowerBound = lb;
        one.constraint.upperBound = ub;
        one.constraint.varIndex.add(index);
        one.constraint.coefficient.add(1.0);
        model.generalConstraints.add(one);
      } else {
        final MPModel.Constraint lower = new MPModel.Constraint();
        lower.lowerBound = 0.0;
        lower.upperBound = Double.POSITIVE_INFINITY;
        lower.varIndex.add(index);
        lower.coefficient.add(1.0);
        lower.varIndex.add(boolVarIndex);
        lower.coefficient.add(-lb);
        model.constraints.add(lower);
        final MPModel.Constraint upper = new MPModel.Constraint();
        upper.lowerBound = Double.NEGATIVE_INFINITY;
        upper.upperBound = 0.0;
        upper.varIndex.add(index);
        upper.coefficient.add(1.0);
        upper.varIndex.add(boolVarIndex);
        upper.coefficient.add(-ub);
        model.constraints.add(upper);
      }
    }
  }
}
