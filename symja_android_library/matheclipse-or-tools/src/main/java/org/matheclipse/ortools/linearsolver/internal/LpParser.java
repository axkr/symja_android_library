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
// ortools/lp_data/lp_parser.h/.cc and proto_utils.cc C++ to Java, see NOTICE in the module root.

package org.matheclipse.ortools.linearsolver.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.matheclipse.ortools.lpdata.LinearProgram;
import org.matheclipse.ortools.lpdata.LpTypes;
import org.matheclipse.ortools.lpdata.SparseColumn;
import org.matheclipse.ortools.lpdata.SparseMatrix;

/**
 * A simple parser of a linear program in the lp_solve like format of
 * <code>LinearProgram::Dump()</code>, for example
 *
 * <pre>
 * min: 1 + x1 + 2 * x2;
 * 0 &lt;= x1 &lt;= 1;
 * x2 &gt;= 2;
 * r1: 1 &lt;= x1 - x2 &lt;= 2;
 * 0 &lt;= x1 + x2 &lt;= inf;
 * int x1, x3;
 * bin x2;
 * </pre>
 *
 * An unnamed constraint with one variable of coefficient 1 defines bounds; variables without bounds
 * are free. Plus and multiplication signs are optional.
 */
public final class LpParser {

  private enum TokenType {
    ERROR, END, ADDAND, VALUE, INF, NAME, SIGN_LE, SIGN_EQ, SIGN_GE, COMA
  }

  private static final Pattern END_PATTERN = Pattern.compile("\\s*");
  private static final Pattern NAME_PATTERN_1 = Pattern.compile("\\s*(\\w[\\w\\[\\]]*):");
  private static final Pattern NAME_PATTERN_2 = Pattern.compile("(?i)\\s*(int)\\s*:?");
  private static final Pattern NAME_PATTERN_3 = Pattern.compile("(?i)\\s*(bin)\\s*:?");
  private static final Pattern LE_PATTERN = Pattern.compile("\\s*<=?");
  private static final Pattern EQ_PATTERN = Pattern.compile("\\s*=");
  private static final Pattern GE_PATTERN = Pattern.compile("\\s*>=?");
  private static final Pattern COMA_PATTERN = Pattern.compile("\\s*\\,");
  private static final Pattern SIGN_PATTERN = Pattern.compile("\\s*([-+]{1})");
  private static final Pattern INF_PATTERN = Pattern.compile("(?i)\\s*inf");
  private static final Pattern VALUE_PATTERN =
      Pattern.compile("\\s*([0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?)");
  private static final Pattern ADDAND_PATTERN =
      Pattern.compile("\\s*(\\*?)\\s*([a-zA-Z_)][\\w\\[\\])]*)");

  /** A cursor on the remaining text with the consumed name and coefficient. */
  private static final class Tokenizer {
    String sp;
    String consumedName;
    double consumedCoeff;

    Tokenizer(String sp) {
      this.sp = sp;
    }

    private Matcher consume(Pattern p) {
      final Matcher m = p.matcher(sp);
      if (m.lookingAt()) {
        sp = sp.substring(m.end());
        return m;
      }
      return null;
    }

    TokenType consumeToken() {
      if (sp.isEmpty() || END_PATTERN.matcher(sp).matches()) {
        return TokenType.END;
      }
      Matcher m = consume(NAME_PATTERN_1);
      if (m == null) {
        m = consume(NAME_PATTERN_2);
      }
      if (m == null) {
        m = consume(NAME_PATTERN_3);
      }
      if (m != null) {
        consumedName = m.group(1);
        return TokenType.NAME;
      }
      if (consume(LE_PATTERN) != null) {
        return TokenType.SIGN_LE;
      }
      if (consume(EQ_PATTERN) != null) {
        return TokenType.SIGN_EQ;
      }
      if (consume(GE_PATTERN) != null) {
        return TokenType.SIGN_GE;
      }
      if (consume(COMA_PATTERN) != null) {
        return TokenType.COMA;
      }
      int minusCount = 0;
      while ((m = consume(SIGN_PATTERN)) != null) {
        if (m.group(1).equals("-")) {
          minusCount++;
        }
      }
      if (consume(INF_PATTERN) != null) {
        consumedCoeff = minusCount % 2 == 0 ? LpTypes.INFINITY : -LpTypes.INFINITY;
        return TokenType.INF;
      }
      boolean hasValue = false;
      m = consume(VALUE_PATTERN);
      if (m != null) {
        try {
          consumedCoeff = Double.parseDouble(m.group(1));
        } catch (NumberFormatException e) {
          return TokenType.ERROR;
        }
        if (!LpTypes.isFinite(consumedCoeff)) {
          return TokenType.INF;
        }
        hasValue = true;
      } else {
        consumedCoeff = 1.0;
      }
      if (minusCount % 2 != 0) {
        consumedCoeff *= -1.0;
      }
      m = consume(ADDAND_PATTERN);
      if (m != null) {
        if (!m.group(1).isEmpty() && !hasValue) {
          return TokenType.ERROR;
        }
        consumedName = m.group(2);
        return TokenType.ADDAND;
      } else if (hasValue) {
        return TokenType.VALUE;
      }
      return TokenType.ERROR;
    }
  }

  /** A parsed constraint. */
  static final class ParsedConstraint {
    String name = "";
    final List<String> variableNames = new ArrayList<>();
    final List<Double> coefficients = new ArrayList<>();
    double lowerBound;
    double upperBound;
  }

  private static boolean tokenIsBound(TokenType t) {
    return t == TokenType.VALUE || t == TokenType.INF;
  }

  /** Parses a constraint; returns <code>null</code> if it is not a valid constraint. */
  static ParsedConstraint parseConstraint(String constraint) {
    final ParsedConstraint parsed = new ParsedConstraint();
    final Tokenizer copy = new Tokenizer(constraint);
    if (copy.consumeToken() == TokenType.NAME) {
      parsed.name = copy.consumedName;
      constraint = copy.sp;
    }
    final Tokenizer t = new Tokenizer(constraint);
    double leftBound = 0.0;
    double rightBound = 0.0;
    TokenType leftSign = TokenType.END;
    TokenType rightSign = TokenType.END;
    final Set<String> usedVariables = new HashSet<>();
    TokenType tokenType = t.consumeToken();
    if (tokenIsBound(tokenType)) {
      leftBound = t.consumedCoeff;
      leftSign = t.consumeToken();
      if (leftSign != TokenType.SIGN_LE && leftSign != TokenType.SIGN_EQ
          && leftSign != TokenType.SIGN_GE) {
        return null;
      }
      tokenType = t.consumeToken();
    }
    while (tokenType == TokenType.ADDAND) {
      if (!usedVariables.add(t.consumedName)) {
        return null;
      }
      parsed.variableNames.add(t.consumedName);
      parsed.coefficients.add(t.consumedCoeff);
      tokenType = t.consumeToken();
    }
    if (leftSign == TokenType.SIGN_EQ && tokenType != TokenType.END) {
      return null;
    }
    if (tokenType != TokenType.END) {
      rightSign = tokenType;
      if (rightSign != TokenType.SIGN_LE && rightSign != TokenType.SIGN_EQ
          && rightSign != TokenType.SIGN_GE) {
        return null;
      }
      if (leftSign != TokenType.END && rightSign == TokenType.SIGN_EQ) {
        return null;
      }
      if (!tokenIsBound(t.consumeToken())) {
        return null;
      }
      rightBound = t.consumedCoeff;
      if (t.consumeToken() != TokenType.END) {
        return null;
      }
    }
    if (leftSign == TokenType.END && rightSign == TokenType.END) {
      return null;
    }
    parsed.lowerBound = -LpTypes.INFINITY;
    parsed.upperBound = LpTypes.INFINITY;
    if (leftSign == TokenType.SIGN_LE || leftSign == TokenType.SIGN_EQ) {
      parsed.lowerBound = leftBound;
    }
    if (leftSign == TokenType.SIGN_GE || leftSign == TokenType.SIGN_EQ) {
      parsed.upperBound = leftBound;
    }
    if (rightSign == TokenType.SIGN_LE || rightSign == TokenType.SIGN_EQ) {
      parsed.upperBound = Math.min(parsed.upperBound, rightBound);
    }
    if (rightSign == TokenType.SIGN_GE || rightSign == TokenType.SIGN_EQ) {
      parsed.lowerBound = Math.max(parsed.lowerBound, rightBound);
    }
    return parsed;
  }

  private LinearProgram lp;
  private final Set<Integer> boundedVariables = new HashSet<>();

  private LpParser() {}

  /** Parses the model into a linear program; returns false on a syntax error. */
  public static boolean parseLp(String model, LinearProgram lp) {
    return new LpParser().parse(model, lp);
  }

  /** Parses the model; returns <code>null</code> on a syntax error. */
  public static MPModel modelFromLpFormat(String model) {
    final LinearProgram lp = new LinearProgram();
    if (!parseLp(model, lp)) {
      return null;
    }
    return linearProgramToMPModel(lp);
  }

  /** Converts a linear program to an {@link MPModel}. */
  public static MPModel linearProgramToMPModel(LinearProgram input) {
    final MPModel output = new MPModel();
    output.name = input.name();
    output.maximize = input.isMaximizationProblem();
    output.objectiveOffset = input.objectiveOffset();
    for (int col = 0; col < input.numVariables(); ++col) {
      final MPModel.Variable v = new MPModel.Variable();
      v.lowerBound = input.variableLowerBounds()[col];
      v.upperBound = input.variableUpperBounds()[col];
      v.name = input.getVariableName(col);
      v.isInteger = input.isVariableInteger(col);
      v.objectiveCoefficient = input.objectiveCoefficients()[col];
      output.variables.add(v);
    }
    final SparseMatrix transpose = new SparseMatrix();
    transpose.populateFromTranspose(input.getSparseMatrix());
    for (int row = 0; row < input.numConstraints(); ++row) {
      final MPModel.Constraint c = new MPModel.Constraint();
      c.lowerBound = input.constraintLowerBounds()[row];
      c.upperBound = input.constraintUpperBounds()[row];
      c.name = input.getConstraintName(row);
      final SparseColumn r = transpose.column(row);
      for (int i = 0; i < r.numEntries(); ++i) {
        c.varIndex.add(r.entryRow(i));
        c.coefficient.add(r.entryCoefficient(i));
      }
      output.constraints.add(c);
    }
    return output;
  }

  private boolean parse(String model, LinearProgram target) {
    this.lp = target;
    boundedVariables.clear();
    lp.clear();
    boolean hasObjective = false;
    for (String line : model.split(";")) {
      if (line.isEmpty()) {
        continue;
      }
      if (!hasObjective && parseObjective(line)) {
        hasObjective = true;
      } else if (!parseConstraintLine(line) && !parseIntegerVariablesList(line)
          && !parseEmptyLine(line)) {
        return false;
      }
    }
    for (int col = 0; col < lp.numVariables(); ++col) {
      if (!boundedVariables.contains(col)) {
        lp.setVariableBounds(col, -LpTypes.INFINITY, LpTypes.INFINITY);
      }
    }
    lp.cleanUp();
    return true;
  }

  private boolean parseEmptyLine(String line) {
    return new Tokenizer(line).consumeToken() == TokenType.END;
  }

  private boolean parseObjective(String objective) {
    final Tokenizer t = new Tokenizer(objective);
    if (t.consumeToken() != TokenType.NAME) {
      return false;
    }
    if (t.consumedName.equalsIgnoreCase("min")) {
      lp.setMaximizationProblem(false);
    } else if (t.consumedName.equalsIgnoreCase("max")) {
      lp.setMaximizationProblem(true);
    } else {
      return false;
    }
    TokenType tokenType = t.consumeToken();
    if (tokenType == TokenType.VALUE) {
      lp.setObjectiveOffset(t.consumedCoeff);
      tokenType = t.consumeToken();
    } else {
      lp.setObjectiveOffset(0.0);
    }
    while (tokenType == TokenType.ADDAND) {
      final int col = lp.findOrCreateVariable(t.consumedName);
      if (lp.objectiveCoefficients()[col] != 0.0) {
        return false;
      }
      lp.setObjectiveCoefficient(col, t.consumedCoeff);
      tokenType = t.consumeToken();
    }
    return tokenType == TokenType.END;
  }

  private boolean parseIntegerVariablesList(String line) {
    final Tokenizer t = new Tokenizer(line);
    boolean binaryList = false;
    if (t.consumeToken() != TokenType.NAME) {
      return false;
    }
    if (t.consumedName.equalsIgnoreCase("bin")) {
      binaryList = true;
    } else if (!t.consumedName.equalsIgnoreCase("int")) {
      return false;
    }
    TokenType tokenType = t.consumeToken();
    while (tokenType == TokenType.ADDAND) {
      if (t.consumedCoeff != 1.0) {
        return false;
      }
      final int col = lp.findOrCreateVariable(t.consumedName);
      lp.setVariableType(col, LinearProgram.VariableType.INTEGER);
      if (binaryList && !setVariableBounds(col, 0.0, 1.0)) {
        return false;
      }
      tokenType = t.consumeToken();
      if (tokenType == TokenType.COMA) {
        tokenType = t.consumeToken();
      }
    }
    return tokenType == TokenType.END;
  }

  private boolean parseConstraintLine(String constraint) {
    final ParsedConstraint parsed = parseConstraint(constraint);
    if (parsed == null) {
      return false;
    }
    if (parsed.name.isEmpty() && parsed.coefficients.size() == 1
        && parsed.coefficients.get(0) == 1.0) {
      final int col = lp.findOrCreateVariable(parsed.variableNames.get(0));
      return setVariableBounds(col, parsed.lowerBound, parsed.upperBound);
    }
    final int numConstraintsBefore = lp.numConstraints();
    final int row = parsed.name.isEmpty() ? lp.createNewConstraint()
        : lp.findOrCreateConstraint(parsed.name);
    if (lp.numConstraints() == numConstraintsBefore) {
      return false;
    }
    if (!LinearProgram.areBoundsValid(parsed.lowerBound, parsed.upperBound)) {
      return false;
    }
    lp.setConstraintBounds(row, parsed.lowerBound, parsed.upperBound);
    for (int i = 0; i < parsed.variableNames.size(); ++i) {
      final int variable = lp.findOrCreateVariable(parsed.variableNames.get(i));
      lp.setCoefficient(row, variable, parsed.coefficients.get(i));
    }
    return true;
  }

  private boolean setVariableBounds(int col, double lb, double ub) {
    if (!boundedVariables.contains(col)) {
      boundedVariables.add(col);
      lp.setVariableBounds(col, -LpTypes.INFINITY, LpTypes.INFINITY);
    }
    lb = Math.max(lb, lp.variableLowerBounds()[col]);
    ub = Math.min(ub, lp.variableUpperBounds()[col]);
    if (!LinearProgram.areBoundsValid(lb, ub)) {
      return false;
    }
    lp.setVariableBounds(col, lb, ub);
    return true;
  }
}
