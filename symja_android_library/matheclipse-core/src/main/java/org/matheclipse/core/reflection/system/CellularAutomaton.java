package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntUnaryOperator;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISparseArray;
import org.matheclipse.core.interfaces.IStringX;

public class CellularAutomaton extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    int argSize = ast.argSize();
    if (argSize >= 1 && argSize <= 3) {
      IExpr rule = ast.arg1();

      try {
        // Throw "nspecnl" if rule is not one of the allowed explicit types.
        if (!isValidRule(rule)) {
          return Errors.printMessage(ast.topHead(), "nspecnl", F.List(rule));
        }

        // Operator form: CellularAutomaton(rule)
        if (argSize == 1 || ast.head() != S.CellularAutomaton) {
          if (ast.head().isAST(S.CellularAutomaton, 2)) {
            return F.CellularAutomaton(ast.head().first(), rule);
          }
          return F.NIL;
        }

        IExpr init = ast.arg2();
        if (init.isSparseArray()) {
          // a sparse initial condition is the list of its cells
          init = ((ISparseArray) init).normal(false);
        }
        if (init.isList2() && init.first().isSparseArray()) {
          // {cells, background} with the cells given as a sparse array
          init = ((IAST) init).setAtCopy(1, ((ISparseArray) init.first()).normal(false));
        }

        // Resolve Named Rules (e.g., "Rule30" -> 30)
        if (rule instanceof IStringX) {
          String ruleName = rule.toString();
          if (ruleName.startsWith("Rule")) {
            try {
              int num = Integer.parseInt(ruleName.substring(4));
              rule = F.ZZ(num);
            } catch (NumberFormatException e) {
              // Keep rule as is if parsing fails
            }
          }
        }

        int tStart = 0;
        int tEnd = -1;
        int dt = 1;
        boolean returnSingleStep = false;
        boolean isOperatorForm = false;
        // the offsets {x1, x2} of the cells to return, relative to the first given cell
        int[] xWindow = null;

        IExpr tSpec = (argSize >= 3) ? ast.arg3() : F.C1;

        if (argSize == 3) {
          if (tSpec.isInteger()) {
            tStart = 0;
            tEnd = tSpec.toIntDefault(-1);
          } else if (tSpec.isList()) {
            IAST tList = (IAST) tSpec;
            if (tList.argSize() >= 2 && tList.arg2().isList2()
                && tList.arg2().first().isInteger() && tList.arg2().second().isInteger()) {
              xWindow = new int[] {tList.arg2().first().toIntDefault(),
                  tList.arg2().second().toIntDefault()};
              if (F.isNotPresent(xWindow[0]) || F.isNotPresent(xWindow[1])
                  || xWindow[1] - xWindow[0] > Config.MAX_AST_SIZE) {
                return F.NIL;
              }
            }
            if (tList.argSize() >= 1) {
              IExpr tTime = tList.arg1();

              if (tTime.isInteger()) {
                tStart = 0;
                tEnd = tTime.toIntDefault(-1);
              } else if (tTime.isList()) {
                IAST tTimeList = (IAST) tTime;
                int tTimeSize = tTimeList.argSize();

                if (tTimeSize == 1) {
                  IExpr inner = tTimeList.arg1();
                  if (inner.isInteger()) {
                    // {{t}} -> tspec = {t} (Returns sequence of just step t)
                    tStart = inner.toIntDefault(-1);
                    tEnd = tStart;
                  } else if (inner.isList() && ((IAST) inner).argSize() == 1
                      && ((IAST) inner).arg1().isInteger()) {
                    // {{{t}}} -> tspec = {{t}} (Returns step t alone unwrapped)
                    tStart = ((IAST) inner).arg1().toIntDefault(-1);
                    tEnd = tStart;
                    returnSingleStep = true;
                  }
                } else if (tTimeSize == 2) {
                  if (tTimeList.arg1().isInteger() && tTimeList.arg2().isInteger()) {
                    // {{-1, 4}} -> tspec = {-1, 4}
                    tStart = tTimeList.arg1().toIntDefault(0);
                    tEnd = tTimeList.arg2().toIntDefault(-1);
                  }
                } else if (tTimeSize == 3) {
                  if (tTimeList.arg1().isInteger() && tTimeList.arg2().isInteger()
                      && tTimeList.arg3().isInteger()) {
                    // {{-1, 4, 2}} -> tspec = {-1, 4, 2}
                    tStart = tTimeList.arg1().toIntDefault(0);
                    tEnd = tTimeList.arg2().toIntDefault(-1);
                    dt = tTimeList.arg3().toIntDefault(1);
                  }
                }
              }
            }
          }
        } else if (argSize == 2) {
          tStart = 1;
          tEnd = 1;
          returnSingleStep = true;
          isOperatorForm = true;
        }

        if (!init.isList()) {
          return F.NIL;
        } else {
          int stepsToGenerate = Math.max(0, tEnd);
          IExpr result = F.NIL;

          // Try Core Implementation: 1D and 2D Integer Rule CA fast-path
          IInteger baseRule = null;
          int k = 2;
          int r = 1;
          int rY = 1;
          int rX = 1;
          int s = 1;
          boolean isTotalistic = false;
          boolean is2D = false;
          int[][] explicitWeights = null;
          // the cells of an explicit neighbourhood {{o1}, {o2}, ...}, or null
          int[] offsets = null;

          if (rule.isList() && rule.argSize() >= 3 && rule.first().isInteger()
              && rule.getAt(3).isList3() && ((IAST) rule.getAt(3)).forAll(IExpr::isInteger)) {
            // {n, k, {r1, r2, r3}}: a rule on three-dimensional arrays
            return evaluateInteger3D((IAST) rule, (IAST) init, stepsToGenerate, tStart, tEnd, dt,
                returnSingleStep, isOperatorForm);
          }

          if (rule.isInteger()) {
            baseRule = (IInteger) rule;
          } else if (rule.isList()) {
            IAST ruleList = (IAST) rule;
            int rSize = ruleList.argSize();
            if (rSize >= 1 && ruleList.arg1().isInteger()) {
              boolean validRule = true;

              if (rSize >= 3) {
                IExpr rspec = ruleList.arg3();
                if (rspec.isInteger()) {
                  r = rspec.toIntDefault(1);
                } else if (rspec.isList()) {
                  int rspecSize = rspec.argSize();
                  if (rspecSize == 1 && ((IAST) rspec).arg1().isInteger()) {
                    r = ((IAST) rspec).arg1().toIntDefault(1);
                  } else if (rspecSize == 2 && ((IAST) rspec).arg1().isInteger()
                      && ((IAST) rspec).arg2().isInteger()) {
                    rY = ((IAST) rspec).arg1().toIntDefault(1);
                    rX = ((IAST) rspec).arg2().toIntDefault(1);
                    is2D = true;
                  } else {
                    offsets = offsetNeighbourhood((IAST) rspec);
                    if (offsets == null) {
                      validRule = false; // Possibly complex offset
                    } else {
                      r = 0;
                      for (int offset : offsets) {
                        r = Math.max(r, Math.abs(offset));
                      }
                    }
                  }
                } else {
                  validRule = false;
                }
              }

              if (validRule && rSize >= 2) {
                IExpr kSpec = ruleList.arg2();
                if (kSpec.isInteger()) {
                  k = kSpec.toIntDefault(2);
                } else if (kSpec.isList() && ((IAST) kSpec).argSize() == 2) {
                  IAST kList = (IAST) kSpec;
                  if (kList.arg1().isInteger()) {
                    k = kList.arg1().toIntDefault(2);
                    IExpr wspec = kList.arg2();

                    if (wspec.isInteger()) {
                      int w = wspec.toIntDefault(-1);
                      if (w == 1) {
                        isTotalistic = true;
                      } else if (w >= 0) {
                        if (is2D) {
                          explicitWeights = new int[2 * rY + 1][2 * rX + 1];
                          for (int i = 0; i < 2 * rY + 1; i++) {
                            for (int j = 0; j < 2 * rX + 1; j++) {
                              explicitWeights[i][j] = w;
                            }
                          }
                        } else {
                          explicitWeights = new int[1][2 * r + 1];
                          for (int j = 0; j < 2 * r + 1; j++) {
                            explicitWeights[0][j] = w;
                          }
                        }
                      } else {
                        validRule = false;
                      }
                    } else if (wspec.isList()) {
                      IAST wList = (IAST) wspec;
                      if (is2D) {
                        boolean validWeights = wList.argSize() == 2 * rY + 1;
                        if (validWeights) {
                          for (int i = 1; i <= wList.argSize(); i++) {
                            IExpr rowE = wList.get(i);
                            if (!rowE.isList() || ((IAST) rowE).argSize() != 2 * rX + 1) {
                              validWeights = false;
                              break;
                            } else {
                              IAST rowAST = (IAST) rowE;
                              for (int j = 1; j <= rowAST.argSize(); j++) {
                                IExpr we = rowAST.get(j);
                                if (!we.isInteger() || we.toIntDefault(-1) < 0) {
                                  validWeights = false;
                                  break;
                                }
                              }
                            }
                            if (!validWeights)
                              break;
                          }
                        }
                        if (!validWeights) {
                          return Errors.printMessage(S.CellularAutomaton, "wtspec",
                              F.List(F.List(F.C2, F.C2), ruleList, F.C1,
                                  F.List(F.ZZ(2 * rY + 1), F.ZZ(2 * rX + 1))));
                        }
                        explicitWeights = new int[2 * rY + 1][2 * rX + 1];
                        for (int i = 0; i < 2 * rY + 1; i++) {
                          IAST rowAST = (IAST) wList.get(i + 1);
                          for (int j = 0; j < 2 * rX + 1; j++) {
                            explicitWeights[i][j] = rowAST.get(j + 1).toIntDefault(0);
                          }
                        }
                      } else {
                        // 1D check
                        boolean validWeights = wList.argSize() == 2 * r + 1;
                        if (validWeights) {
                          for (int j = 1; j <= wList.argSize(); j++) {
                            IExpr we = wList.get(j);
                            if (!we.isInteger() || we.toIntDefault(-1) < 0) {
                              validWeights = false;
                              break;
                            }
                          }
                        }
                        if (!validWeights) {
                          return Errors.printMessage(S.CellularAutomaton, "wtspec",
                              F.List(F.List(F.C2, F.C2), ruleList, F.C1, F.List(F.ZZ(2 * r + 1))));
                        }
                        explicitWeights = new int[1][2 * r + 1];
                        for (int j = 0; j < 2 * r + 1; j++) {
                          explicitWeights[0][j] = wList.get(j + 1).toIntDefault(0);
                        }
                      }
                    } else {
                      validRule = false;
                    }
                  } else {
                    validRule = false;
                  }
                } else {
                  validRule = false;
                }
              }

              if (validRule && rSize >= 4 && ruleList.arg4().isInteger()) {
                s = ruleList.arg4().toIntDefault(1);
              }
              if (validRule) {
                baseRule = (IInteger) ruleList.arg1();
              }
              if (argSize == 2 && s > 1) {
                tStart = 2 - s; // e.g. s=2 → tStart=0, so hIdx=s-1+0=1 (last initial)
                returnSingleStep = false; // result is a multi-row list, not unwrapped
              }
            }
          }

          if (baseRule != null && offsets != null) {
            if (explicitWeights != null || isTotalistic || is2D) {
              return F.NIL;
            }
            // the cell at the j-th offset is the j-th digit of the neighbourhood's number, which
            // is the weights mechanism with the weights k^(m-j)
            final int m = offsets.length;
            if (m * Math.log(k) > Math.log(Config.MAX_AST_SIZE)) {
              return F.NIL;
            }
            explicitWeights = new int[1][2 * r + 1];
            int weight = 1;
            for (int j = m - 1; j >= 0; j--) {
              explicitWeights[0][offsets[j] + r] = weight;
              weight *= k;
            }
            // weight is k^m now, the number of neighbourhoods
            if (weight <= 4096) {
              java.math.BigInteger largest =
                  java.math.BigInteger.valueOf(k).pow(weight).subtract(java.math.BigInteger.ONE);
              if (baseRule.toBigNumerator().compareTo(largest) > 0) {
                // The specified rule number `1` is greater than the largest possible rule number
                // (`2`).
                return Errors.printMessage(S.CellularAutomaton, "rsize",
                    F.List(baseRule, F.ZZ(largest)));
              }
            }
          }

          if (baseRule != null) {
            if (baseRule.isNegative()) {
              // The specified rule number `1` should be non-negative.
              return Errors.printMessage(S.CellularAutomaton, "rneg", F.List(baseRule));
            }
            if (is2D) {
              result = evaluateInteger2D(baseRule, k, rY, rX, s, isTotalistic, explicitWeights,
                  (IAST) init, stepsToGenerate, tStart, tEnd, dt, returnSingleStep, isOperatorForm);
            } else {
              result = evaluateInteger1D(baseRule, k, r, s, isTotalistic, explicitWeights,
                  (IAST) init, stepsToGenerate, tStart, tEnd, dt, returnSingleStep, isOperatorForm,
                  xWindow);
            }
          }

          // Fallback to General CA (Explicit rules, functions, or symbolic states)
          if (!result.isPresent()) {
            if (!is2D && rule.isList() && rule.argSize() >= 3 && !rule.first().isInteger()
                && isGrid2D((IAST) init)) {
              IExpr rspec = rule.getAt(3);
              if (offsetNeighbourhood2D(rspec) != null) {
                // {f, {}, {{dy1, dx1}, ...}}: a neighbourhood of offsets in the plane
                is2D = true;
              } else if (rspec.isList2() && rspec.first().isInteger()
                  && rspec.second().isInteger()) {
                // {f, {}, {ry, rx}}: the (2 ry + 1) x (2 rx + 1) block around a cell
                rY = rspec.first().toIntDefault();
                rX = rspec.second().toIntDefault();
                if (rY < 0 || rX < 0 || rY > 1000 || rX > 1000) {
                  return F.NIL;
                }
                is2D = true;
              }
            }
            if (is2D) {
              result = evaluateGeneral2D(rule, (IAST) init, stepsToGenerate, tStart, tEnd, dt,
                  returnSingleStep, isOperatorForm, rY, rX, engine);
            } else {
              result = evaluateGeneral1D(rule, (IAST) init, stepsToGenerate, tStart, tEnd, dt,
                  returnSingleStep, isOperatorForm, engine);
            }
          }

          if (result.isPresent()) {
            return result;
          }
        }
      } catch (RuntimeException e) {
      }
    }
    return F.NIL;
  }

  /**
   * Helper that evaluates if a rule matches explicit rule constraint typings.
   */
  private boolean isValidRule(IExpr rule) {
    return (rule.isInteger() || rule.isList() || rule.isString() || rule.isAssociation()
        || rule.isFunction() || rule.isBooleanFormula());
  }

  /**
   * Evaluates k-color, range-r, order-s generalized integer cellular automata. Supports 1D grids
   * and flat cyclic lists and superimposed states with infinite backgrounds {{...}, bg}.
   */
  /**
   * The offsets of an explicit one-dimensional neighbourhood <code>{{o1}, {o2}, ...}</code>, or
   * <code>null</code> if the specification is none: every entry is a list of one integer and no
   * offset occurs twice.
   */
  private static int[] offsetNeighbourhood(IAST rspec) {
    if (rspec.argSize() < 1) {
      return null;
    }
    int[] offsets = new int[rspec.argSize()];
    for (int i = 1; i <= rspec.argSize(); i++) {
      IExpr entry = rspec.get(i);
      if (!entry.isList1() || !entry.first().isInteger()) {
        return null;
      }
      int offset = entry.first().toIntDefault();
      if (F.isNotPresent(offset) || Math.abs(offset) > 1000) {
        return null;
      }
      for (int j = 0; j < i - 1; j++) {
        if (offsets[j] == offset) {
          return null;
        }
      }
      offsets[i - 1] = offset;
    }
    return offsets;
  }

  /**
   * <code>true</code> if the initial condition is a two-dimensional array, by itself or as
   * <code>{array, background}</code>.
   */
  private static boolean isGrid2D(IAST init) {
    IExpr grid = init.argSize() == 2 && init.arg1().isList()
        && (init.arg2().isInteger() || background2D(init.arg2()).isPresent()) ? init.arg1() : init;
    return grid.isList() && grid.argSize() > 0 && grid.first().isList();
  }

  /**
   * The value <code>b</code> of a one-cell background <code>{b}</code>, or {@link F#NIL}.
   */
  private static IExpr background1D(IExpr background) {
    return background.isList1() && !background.first().isList() ? background.first() : F.NIL;
  }

  /**
   * The value <code>b</code> of a one-cell plane background <code>{{b}}</code>, or {@link F#NIL}.
   */
  private static IExpr background2D(IExpr background) {
    return background.isList1() && background.first().isList1()
        && !background.first().first().isList() ? background.first().first() : F.NIL;
  }

  /**
   * The offsets of an explicit two-dimensional neighbourhood <code>{{dy1, dx1}, {dy2, dx2},
   * ...}</code>, or {@code null} if <code>rspec</code> isn't one.
   */
  private static int[][] offsetNeighbourhood2D(IExpr rspec) {
    if (!rspec.isList() || rspec.argSize() == 0) {
      return null;
    }
    int[][] offsets = new int[rspec.argSize()][];
    for (int i = 1; i <= rspec.argSize(); i++) {
      IExpr offset = ((IAST) rspec).get(i);
      if (!offset.isList2() || !offset.first().isInteger() || !offset.second().isInteger()) {
        return null;
      }
      int dy = offset.first().toIntDefault();
      int dx = offset.second().toIntDefault();
      if (F.isNotPresent(dy) || F.isNotPresent(dx) || Math.abs(dy) > 1000
          || Math.abs(dx) > 1000) {
        return null;
      }
      offsets[i - 1] = new int[] {dy, dx};
    }
    return offsets;
  }

  /**
   * The new cell value of every transition of an integer rule: the base {@code k} digits of
   * {@code ruleNum}, lowest first.
   *
   * <p>
   * A rule with explicit weights has a transition for each weighted sum of the cells, a totalistic
   * rule one for each sum of the cells, and any other rule one for each pattern of the cells of its
   * {@code s} preceding steps.
   *
   * @param cells the number of cells in the neighbourhood
   * @param weighted whether the rule has explicit weights
   * @param weightTotal the sum of the explicit weights
   * @return {@code null} for weights with a negative sum
   */
  private static int[] ruleTable(IInteger ruleNum, int k, int s, int cells, boolean weighted,
      long weightTotal,
      boolean isTotalistic) {
    long numTransitions;
    if (weighted) {
      if (weightTotal < 0) {
        return null;
      }
      numTransitions = weightTotal * (k - 1) * s + 1;
    } else if (isTotalistic) {
      numTransitions = (long) s * cells * (k - 1) + 1;
    } else {
      double count = Math.pow(k, (double) s * cells);
      if (count > Config.MAX_AST_SIZE) {
        ASTElementLimitExceeded.throwIt((long) Math.min(count, Long.MAX_VALUE));
      }
      numTransitions = (long) count;
    }
    if (numTransitions > Config.MAX_AST_SIZE) {
      ASTElementLimitExceeded.throwIt(numTransitions);
    }
    int[] ruleBits = new int[(int) numTransitions];
    IInteger temp = ruleNum;
    IInteger ZZk = F.ZZ(k);
    for (int i = 0; i < numTransitions; i++) {
      IExpr[] divMod = temp.divideAndRemainder(ZZk);
      ruleBits[i] = divMod[1].toIntDefault(0);
      temp = (IInteger) divMod[0];
    }
    return ruleBits;
  }

  /** Whether <code>init</code> is <code>{aspec, background}</code> with an integer background. */
  private static boolean hasBackground(IAST init) {
    return init.argSize() == 2 && init.get(2).isInteger() && init.get(1).isList();
  }

  /**
   * The initial states of the <code>s</code> preceding steps of an integer rule: each one
   * <code>{aspec, background}</code> or a cyclic array.
   *
   * @param oneDimensional unwrap a 1D <code>{{cells}}</code> of a rule of order 1
   * @return <code>null</code> if <code>init</code> has not the form of <code>s</code> initial
   *         states
   */
  private static List<IAST> initialStates(IAST init, int s, boolean oneDimensional) {
    List<IAST> initStates = new ArrayList<>();
    if (hasBackground(init)) {
      IAST aspecList = (IAST) init.get(1);
      int bg = init.get(2).toIntDefault(0);
      if (oneDimensional && s == 1 && aspecList.argSize() == 1 && aspecList.arg1().isList()) {
        aspecList = (IAST) aspecList.arg1();
      }
      if (s > 1) {
        if (aspecList.argSize() != s || !aspecList.arg1().isList()) {
          return null;
        }
        for (int i = 1; i <= s; i++) {
          IASTAppendable paddedStep = F.ListAlloc(2);
          paddedStep.append(aspecList.get(i));
          paddedStep.append(F.ZZ(bg));
          initStates.add(paddedStep);
        }
      } else {
        IASTAppendable paddedStep = F.ListAlloc(2);
        paddedStep.append(aspecList);
        paddedStep.append(F.ZZ(bg));
        initStates.add(paddedStep);
      }
    } else if (s > 1) {
      if (init.argSize() != s || !init.arg1().isList()) {
        return null;
      }
      for (int i = 1; i <= s; i++) {
        IExpr stepVal = init.get(i);
        if (!stepVal.isList()) {
          return null;
        }
        initStates.add((IAST) stepVal);
      }
    } else {
      initStates.add(init);
    }
    return initStates;
  }

  private IExpr evaluateInteger1D(IInteger ruleNum, int k, int r, int s, boolean isTotalistic,
      int[][] explicitWeights, IAST init, int steps, int tStart, int tEnd, int dt,
      boolean returnSingleStep, boolean isOperatorForm, int[] xWindow) {
    if (k < 2 || r < 0 || s < 1)
      return F.NIL;

    long weightTotal = 0;
    if (explicitWeights != null) {
      for (int j = 0; j < 2 * r + 1; j++) {
        weightTotal += explicitWeights[0][j];
      }
    }
    int[] ruleBits = ruleTable(ruleNum, k, s, 2 * r + 1, explicitWeights != null, weightTotal,
        isTotalistic);
    if (ruleBits == null) {
      return F.NIL;
    }

    if (s == 1 && !isOperatorForm && init.argSize() == 2 && init.arg1().isList()
        && (init.arg2().isList() || (init.arg2().isInteger() && xWindow != null))) {
      // {cells, {b1, b2, ...}}: the cells on a background which repeats the block
      int[] block = integers(init.arg2().makeList(), k);
      IAST cells = (IAST) init.arg1();
      if (cells.argSize() == 1 && cells.arg1().isList()) {
        cells = (IAST) cells.arg1();
      }
      int[] seed = integers(cells, k);
      if (block == null || block.length == 0 || seed == null || seed.length == 0) {
        return F.NIL;
      }
      return evaluateOnBackground(ruleBits, k, r, isTotalistic, explicitWeights, seed, block,
          steps, tStart, tEnd, dt, returnSingleStep, xWindow);
    }

    if (s == 1 && init.argSize() == 1 && init.arg1().isList()) {
      init = (IAST) init.arg1();
    }
    boolean isCyclic = !hasBackground(init);
    List<IAST> initStates = initialStates(init, s, true);
    if (initStates == null) {
      return F.NIL;
    }

    IAST firstInit = initStates.get(0);
    int aspecSize = 0;

    if (!isCyclic) {
      aspecSize = ((IAST) firstInit.get(1)).argSize();
    } else {
      aspecSize = firstInit.argSize();
    }

    if (aspecSize == 0)
      return F.NIL;

    int padding = isCyclic ? 0 : steps * r;
    int gridSize = aspecSize + 2 * padding;
    if (gridSize <= 0)
      return F.NIL;

    List<int[]> history = new ArrayList<>();
    List<Integer> bgHistory = new ArrayList<>();

    for (int step = 0; step < s; step++) {
      IAST stateExpr = initStates.get(step);
      IAST aspec;
      int bgForThisStep = 0;

      if (!isCyclic) {
        if (stateExpr.argSize() == 2 && stateExpr.get(1).isList()) {
          aspec = (IAST) stateExpr.get(1);
          bgForThisStep = stateExpr.get(2).toIntDefault(0);
        } else {
          return F.NIL;
        }
      } else {
        aspec = stateExpr;
      }

      int[] stateArr = new int[gridSize];
      for (int i = 0; i < gridSize; i++)
        stateArr[i] = bgForThisStep;

      for (int i = 1; i <= aspec.argSize(); i++) {
        int idx = padding + i - 1;
        if (idx < gridSize) {
          IExpr val = aspec.get(i);
          if (val.isInteger()) {
            stateArr[idx] = val.toIntDefault(0);
          } else {
            return F.NIL;
          }
        }
      }

      history.add(stateArr);
      bgHistory.add(bgForThisStep);
    }

    for (int t = 0; t < steps; t++) {
      int[] nextState = new int[gridSize];

      for (int i = 0; i < gridSize; i++) {
        int v = 0;
        for (int step = s; step >= 1; step--) {
          int[] state = history.get(history.size() - step);
          for (int di = -r; di <= r; di++) {
            int idx = i + di;
            int cellVal;
            if (isCyclic) {
              int wrappedIdx = (idx % gridSize + gridSize) % gridSize;
              cellVal = state[wrappedIdx];
            } else {
              if (idx < 0 || idx >= gridSize) {
                cellVal = bgHistory.get(bgHistory.size() - step);
              } else {
                cellVal = state[idx];
              }
            }
            if (explicitWeights != null) {
              v += explicitWeights[0][di + r] * cellVal;
            } else if (isTotalistic) {
              v += cellVal;
            } else {
              v = v * k + cellVal;
            }
          }
        }
        nextState[i] = ruleBits[v];
      }

      int nextBg = 0;
      if (!isCyclic) {
        int v = 0;
        for (int step = s; step >= 1; step--) {
          for (int di = -r; di <= r; di++) {
            int bgVal = bgHistory.get(bgHistory.size() - step);
            if (explicitWeights != null) {
              v += explicitWeights[0][di + r] * bgVal;
            } else if (isTotalistic) {
              v += bgVal;
            } else {
              v = v * k + bgVal;
            }
          }
        }
        nextBg = ruleBits[v];
      } else {
        nextBg = bgHistory.get(bgHistory.size() - 1);
      }

      history.add(nextState);
      bgHistory.add(nextBg);
    }

    int minX = (isOperatorForm && !isCyclic) ? 0 : padding;
    int maxX = (isOperatorForm && !isCyclic) ? gridSize - 1 : padding + aspecSize - 1;

    // CPD-OFF int and expression states are filled and trimmed separately
    int localDt = (dt == 0) ? 1 : dt;

    if (!isCyclic && !isOperatorForm) {
      for (int tIdx = tStart; (localDt > 0 ? tIdx <= tEnd : tIdx >= tEnd); tIdx += localDt) {
        int hIdx = s - 1 + tIdx;
        if (hIdx >= 0 && hIdx < history.size()) {
          int[] state = history.get(hIdx);
          int b = bgHistory.get(hIdx);
          for (int i = 0; i < gridSize; i++) {
          // CPD-ON
            if (state[i] != b) {
              if (i < minX)
                minX = i;
              if (i > maxX)
                maxX = i;
            // CPD-OFF int and expression states are filled and trimmed separately
            }
          }
        }
      }
    }

    IASTAppendable resultList = F.ListAlloc();
    for (int tIdx = tStart; (localDt > 0 ? tIdx <= tEnd : tIdx >= tEnd); tIdx += localDt) {
      int hIdx = s - 1 + tIdx;
      if (hIdx >= 0 && hIdx < history.size()) {
        int[] state = history.get(hIdx);
        IASTAppendable row = F.ListAlloc(maxX - minX + 1);
        // CPD-ON
        for (int i = minX; i <= maxX; i++) {
          row.append(F.ZZ(state[i]));
        }
        resultList.append(row);
      }
    }

    if (isOperatorForm && resultList.argSize() == 1) {
      if (!isCyclic) {
        // For infinite-padding init, return {{cells}, {bg}} like WMA
        int hIdx = s - 1 + tStart; // tStart==tEnd==1 in single-step mode
        int bg = bgHistory.get(hIdx);
        IASTAppendable pair = F.ListAlloc(2);
        pair.append(resultList.arg1());
        pair.append(F.List(F.ZZ(bg)));
        return pair;
      }
      return resultList.arg1();
    } else if (returnSingleStep && resultList.argSize() == 1) {
      return resultList.arg1(); // {{{t}}} tSpec: plain unwrap, no bg wrapper
    }

    return resultList;
  }

  /** The cells of a list of integer states <code>0 &lt;= c &lt; k</code>, or <code>null</code>. */
  private static int[] integers(IAST list, int k) {
    int[] values = new int[list.argSize()];
    for (int i = 1; i < list.size(); i++) {
      int value = list.get(i).toIntDefault();
      if (value < 0 || value >= k) {
        return null;
      }
      values[i - 1] = value;
    }
    return values;
  }

  /** The new value of a cell from the values <code>cellAt(-r), ..., cellAt(r)</code>. */
  private static int transition(int[] ruleBits, int k, int r, boolean isTotalistic,
      int[][] explicitWeights, IntUnaryOperator cellAt) {
    int v = 0;
    for (int di = -r; di <= r; di++) {
      int cellVal = cellAt.applyAsInt(di);
      if (explicitWeights != null) {
        v += explicitWeights[0][di + r] * cellVal;
      } else if (isTotalistic) {
        v += cellVal;
      } else {
        v = v * k + cellVal;
      }
    }
    return ruleBits[v];
  }

  /**
   * The evolution of the cells <code>seed</code>, the first one at offset 0, on a background in
   * which offset <code>x</code> holds <code>block[x mod p]</code>. The background evolves on its
   * own period; the seeded line is computed on a window so wide that every cell outside of it still
   * follows the background. Without <code>xWindow</code> the rows keep the cells which differ from
   * the background at one of the requested steps.
   */
  private static IExpr evaluateOnBackground(int[] ruleBits, int k, int r, boolean isTotalistic,
      int[][] explicitWeights, int[] seed, int[] block, int steps, int tStart, int tEnd, int dt,
      boolean returnSingleStep, int[] xWindow) {
    final int p = block.length;
    final int localDt = (dt == 0) ? 1 : dt;
    if (tStart < 0 || tEnd < 0) {
      return F.NIL;
    }
    final long reach = (long) r * steps + 1;
    final long width = seed.length + 2 * reach;
    if (width * (steps + 1) > Config.MAX_AST_SIZE) {
      ASTElementLimitExceeded.throwIt(width * (steps + 1));
    }
    final int left = (int) -reach;
    List<int[]> backgrounds = new ArrayList<>(steps + 1);
    List<int[]> lines = new ArrayList<>(steps + 1);
    int[] background = block.clone();
    int[] line = new int[(int) width];
    for (int i = 0; i < line.length; i++) {
      int x = left + i;
      line[i] = x >= 0 && x < seed.length ? seed[x] : block[Math.floorMod(x, p)];
    }
    backgrounds.add(background);
    lines.add(line);
    for (int t = 0; t < steps; t++) {
      final int[] bg = background;
      final int[] current = line;
      int[] nextBackground = new int[p];
      for (int i = 0; i < p; i++) {
        final int x = i;
        nextBackground[i] = transition(ruleBits, k, r, isTotalistic, explicitWeights,
            di -> bg[Math.floorMod(x + di, p)]);
      }
      int[] nextLine = new int[current.length];
      for (int i = 0; i < current.length; i++) {
        final int j = i;
        nextLine[i] = transition(ruleBits, k, r, isTotalistic, explicitWeights, di -> {
          int index = j + di;
          return index >= 0 && index < current.length ? current[index]
              : bg[Math.floorMod(left + index, p)];
        });
      }
      background = nextBackground;
      line = nextLine;
      backgrounds.add(background);
      lines.add(line);
    }

    int minX;
    int maxX;
    if (xWindow != null) {
      minX = xWindow[0];
      maxX = xWindow[1];
    } else {
      minX = Integer.MAX_VALUE;
      maxX = Integer.MIN_VALUE;
      for (int t = tStart; (localDt > 0 ? t <= tEnd : t >= tEnd); t += localDt) {
        if (t < lines.size()) {
          int[] cells = lines.get(t);
          int[] bg = backgrounds.get(t);
          for (int i = 0; i < cells.length; i++) {
            if (cells[i] != bg[Math.floorMod(left + i, p)]) {
              minX = Math.min(minX, left + i);
              maxX = Math.max(maxX, left + i);
            }
          }
        }
      }
    }
    IASTAppendable resultList = F.ListAlloc();
    for (int t = tStart; (localDt > 0 ? t <= tEnd : t >= tEnd); t += localDt) {
      if (t < lines.size()) {
        int[] cells = lines.get(t);
        int[] bg = backgrounds.get(t);
        IASTAppendable row = F.ListAlloc(maxX >= minX ? maxX - minX + 1 : 0);
        for (int x = minX; x <= maxX; x++) {
          int i = x - left;
          row.append(F.ZZ(i >= 0 && i < cells.length ? cells[i] : bg[Math.floorMod(x, p)]));
        }
        resultList.append(row);
      }
    }
    if (returnSingleStep && resultList.argSize() == 1) {
      return resultList.arg1();
    }
    return resultList;
  }

  /**
   * Evaluates k-color, range-r, order-s generalized integer cellular automata for 2D grids.
   */
  private IExpr evaluateInteger2D(IInteger ruleNum, int k, int rY, int rX, int s,
      boolean isTotalistic, int[][] explicitWeights, IAST init, int steps, int tStart, int tEnd,
      int dt, boolean returnSingleStep, boolean isOperatorForm) {
    if (k < 2 || rY < 0 || rX < 0 || s < 1)
      return F.NIL;

    long weightTotal = 0;
    if (explicitWeights != null) {
      for (int i = 0; i < 2 * rY + 1; i++) {
        for (int j = 0; j < 2 * rX + 1; j++) {
          weightTotal += explicitWeights[i][j];
        }
      }
    }
    int[] ruleBits = ruleTable(ruleNum, k, s, (2 * rY + 1) * (2 * rX + 1),
        explicitWeights != null, weightTotal, isTotalistic);
    if (ruleBits == null) {
      return F.NIL;
    }

    boolean isCyclic = !hasBackground(init);
    List<IAST> initStates = initialStates(init, s, false);
    if (initStates == null) {
      return F.NIL;
    // CPD-OFF int and expression states are filled and trimmed separately
    }

    IAST firstInit = initStates.get(0);
    int[] extent = aspecExtent2D(firstInit, isCyclic);
    if (extent == null) {
      return F.NIL;
    }
    int aspecSizeY = extent[0];
    int aspecSizeX = extent[1];

    int paddingY = isCyclic ? 0 : steps * rY;
    int paddingX = isCyclic ? 0 : steps * rX;
    int gridY = aspecSizeY + 2 * paddingY;
    int gridX = aspecSizeX + 2 * paddingX;

    List<int[]> history = new ArrayList<>();
    // CPD-ON
    List<Integer> bgHistory = new ArrayList<>();

    for (int step = 0; step < s; step++) {
      IAST stateExpr = initStates.get(step);
      IAST currentAspec;
      int bgForThisStep = 0;

      if (!isCyclic) {
        currentAspec = (IAST) stateExpr.arg1();
        bgForThisStep = stateExpr.arg2().toIntDefault(0);
      } else {
        currentAspec = stateExpr;
      }

      // CPD-OFF int and expression states are filled and trimmed separately
      int[] stateArr = new int[gridY * gridX];
      for (int i = 0; i < stateArr.length; i++)
        stateArr[i] = bgForThisStep;

      for (int y = 1; y <= currentAspec.argSize(); y++) {
        if (currentAspec.get(y).isList()) {
          IAST row = (IAST) currentAspec.get(y);
          for (int x = 1; x <= row.argSize(); x++) {
            int gY = paddingY + y - 1;
            int gX = paddingX + x - 1;
            if (gY < gridY && gX < gridX) {
            // CPD-ON
              IExpr val = row.get(x);
              if (val.isInteger()) {
                stateArr[gY * gridX + gX] = val.toIntDefault(0);
              }
            }
          }
        }
      }
      history.add(stateArr);
      bgHistory.add(bgForThisStep);
    }

    for (int t = 0; t < steps; t++) {
      int[] nextState = new int[gridY * gridX];
      for (int y = 0; y < gridY; y++) {
        for (int x = 0; x < gridX; x++) {
          int v = 0;
          for (int stepIdx = s; stepIdx >= 1; stepIdx--) {
            int[] state = history.get(history.size() - stepIdx);
            for (int dy = -rY; dy <= rY; dy++) {
              for (int dx = -rX; dx <= rX; dx++) {
                int ny = y + dy;
                int nx = x + dx;
                int cellVal;
                if (isCyclic) {
                  ny = (ny % gridY + gridY) % gridY;
                  nx = (nx % gridX + gridX) % gridX;
                  cellVal = state[ny * gridX + nx];
                } else {
                  if (ny < 0 || ny >= gridY || nx < 0 || nx >= gridX) {
                    cellVal = bgHistory.get(bgHistory.size() - stepIdx);
                  } else {
                    cellVal = state[ny * gridX + nx];
                  }
                }
                if (explicitWeights != null) {
                  v += explicitWeights[dy + rY][dx + rX] * cellVal;
                } else if (isTotalistic) {
                  v += cellVal;
                } else {
                  v = v * k + cellVal;
                }
              }
            }
          }
          nextState[y * gridX + x] = ruleBits[v];
        }
      }

      int nextBg = 0;
      if (!isCyclic) {
        int v = 0;
        for (int stepIdx = s; stepIdx >= 1; stepIdx--) {
          int oldBg = bgHistory.get(bgHistory.size() - stepIdx);
          for (int dy = -rY; dy <= rY; dy++) {
            for (int dx = -rX; dx <= rX; dx++) {
              if (explicitWeights != null) {
                v += explicitWeights[dy + rY][dx + rX] * oldBg;
              } else if (isTotalistic) {
                v += oldBg;
              } else {
                v = v * k + oldBg;
              }
            }
          }
        }
        nextBg = ruleBits[v];
      // CPD-OFF int and expression states are trimmed separately
      } else {
        nextBg = bgHistory.get(bgHistory.size() - 1);
      }

      history.add(nextState);
      bgHistory.add(nextBg);
    }

    int minY = (isOperatorForm && !isCyclic) ? 0 : paddingY;
    int maxY = (isOperatorForm && !isCyclic) ? gridY - 1 : paddingY + aspecSizeY - 1;
    int minX = (isOperatorForm && !isCyclic) ? 0 : paddingX;
    int maxX = (isOperatorForm && !isCyclic) ? gridX - 1 : paddingX + aspecSizeX - 1;

    int localDt = (dt == 0) ? 1 : dt;

    if (!isCyclic && !isOperatorForm) {
      for (int tIdx = tStart; (localDt > 0 ? tIdx <= tEnd : tIdx >= tEnd); tIdx += localDt) {
      // CPD-ON
        int hIdx = s - 1 + tIdx;
        if (hIdx >= 0 && hIdx < history.size()) {
          int[] state = history.get(hIdx);
          int b = bgHistory.get(hIdx);
          for (int y = 0; y < gridY; y++) {
            for (int x = 0; x < gridX; x++) {
              // CPD-OFF int and expression states are filled and trimmed separately
              if (state[y * gridX + x] != b) {
                if (y < minY)
                  minY = y;
                if (y > maxY)
                  maxY = y;
                if (x < minX)
                  minX = x;
                if (x > maxX)
                  maxX = x;
              }
            }
          }
        }
      }
    }

    IASTAppendable resultList = F.ListAlloc();
    for (int tIdx = tStart; (localDt > 0 ? tIdx <= tEnd : tIdx >= tEnd); tIdx += localDt) {
      int hIdx = s - 1 + tIdx;
      if (hIdx >= 0 && hIdx < history.size()) {
        int[] state = history.get(hIdx);
        IASTAppendable matrix = F.ListAlloc(maxY - minY + 1);
        // CPD-ON
        for (int y = minY; y <= maxY; y++) {
          IASTAppendable row = F.ListAlloc(maxX - minX + 1);
          for (int x = minX; x <= maxX; x++) {
            row.append(F.ZZ(state[y * gridX + x]));
          }
          matrix.append(row);
        }
        resultList.append(matrix);
      }
    }

    if (isOperatorForm && resultList.argSize() == 1) {
      if (!isCyclic) {
        // For infinite-padding 2D init, return {matrix, {{bg}}}
        int hIdx = s - 1 + tStart;
        int bg = bgHistory.get(hIdx);
        IASTAppendable pair = F.ListAlloc(2);
        pair.append(resultList.arg1());
        pair.append(F.List(F.List(F.ZZ(bg)))); // {{bg}}
        return pair;
      }
      return resultList.arg1();
    } else if (returnSingleStep && resultList.argSize() == 1) {
      return resultList.arg1(); // {{{t}}} tSpec: plain unwrap, no bg wrapper
    }
    return resultList;
  }

  /**
   * <code>CellularAutomaton({n, kspec, {r1, r2, r3}}, init, t)</code> and the order
   * <code>s</code> form <code>{n, kspec, {r1, r2, r3}, s}</code>: a <code>k</code>-colour rule on
   * three-dimensional arrays, whose new cell depends on the <code>(2 r1 + 1) x (2 r2 + 1) x (2 r3 +
   * 1)</code> block around it. <code>kspec</code> is <code>k</code> (the block's cells are the
   * digits of the number of the transition), <code>{k, 1}</code> (the transition is the total of
   * the block), <code>{k, w}</code> with the same weight <code>w</code> for every cell, or
   * <code>{k, weights}</code> with an array of the block's weights. The initial condition is a
   * cyclic array of rank 3, or <code>{array, b}</code> on a background of the value
   * <code>b</code>, which is returned as the box of all cells which differ from the background.
   * Each case behaves like the two-dimensional rules, so a radius <code>r1 = 0</code> evolves
   * every slice of the array on its own.
   */
  private IExpr evaluateInteger3D(IAST rule, IAST init, int steps, int tStart, int tEnd, int dt,
      boolean returnSingleStep, boolean isOperatorForm) {
    IInteger ruleNum = (IInteger) rule.arg1();
    if (ruleNum.isNegative()) {
      // The specified rule number `1` should be non-negative.
      return Errors.printMessage(S.CellularAutomaton, "rneg", F.List(ruleNum));
    }
    IAST rspec = (IAST) rule.arg3();
    final int[] radius = new int[3];
    for (int d = 0; d < 3; d++) {
      radius[d] = rspec.get(d + 1).toIntDefault(-1);
      if (radius[d] < 0 || radius[d] > 100) {
        return F.NIL;
      }
    }
    final int[] block = {2 * radius[0] + 1, 2 * radius[1] + 1, 2 * radius[2] + 1};
    final int blockSize = block[0] * block[1] * block[2];
    int k = 2;
    boolean isTotalistic = false;
    int[] weights = null;
    IExpr kSpec = rule.arg2();
    if (kSpec.isInteger()) {
      k = kSpec.toIntDefault(-1);
    } else if (kSpec.isList2() && kSpec.first().isInteger()) {
      k = kSpec.first().toIntDefault(-1);
      IExpr wspec = kSpec.second();
      if (wspec.isInteger()) {
        int w = wspec.toIntDefault(-1);
        if (w == 1) {
          isTotalistic = true;
        } else if (w >= 0) {
          weights = new int[blockSize];
          java.util.Arrays.fill(weights, w);
        } else {
          return F.NIL;
        }
      } else {
        weights = weights3D(wspec, block);
        if (weights == null) {
          return F.NIL;
        }
      }
    } else {
      return F.NIL;
    }
    int s = 1;
    if (rule.argSize() >= 4) {
      s = rule.arg4().toIntDefault(-1);
    }
    if (k < 2 || s < 1) {
      return F.NIL;
    }
    if (isOperatorForm && s > 1) {
      tStart = 2 - s;
      returnSingleStep = false;
    }

    long weightTotal = 0;
    if (weights != null) {
      for (int w : weights) {
        weightTotal += w;
      }
    }
    int[] ruleBits =
        ruleTable(ruleNum, k, s, blockSize, weights != null, weightTotal, isTotalistic);
    if (ruleBits == null) {
      return F.NIL;
    }

    // the initial arrays (s of them for a rule of order s) and their background
    boolean isCyclic = true;
    int background = 0;
    IAST arrays = init;
    if (init.argSize() == 2 && init.arg2().isInteger() && init.arg1().isList()) {
      isCyclic = false;
      background = init.arg2().toIntDefault(0);
      arrays = (IAST) init.arg1();
    }
    List<IAST> initStates = new ArrayList<>();
    if (s > 1) {
      if (arrays.argSize() != s) {
        return F.NIL;
      }
      for (int i = 1; i <= s; i++) {
        if (!arrays.get(i).isList()) {
          return F.NIL;
        }
        initStates.add((IAST) arrays.get(i));
      }
    } else {
      initStates.add(arrays);
    }
    // the extent of the initial arrays
    final int[] size = new int[3];
    for (IAST array : initStates) {
      if (!extent3D(array, size)) {
        return F.NIL;
      }
    }
    if (size[0] == 0 || size[1] == 0 || size[2] == 0) {
      return F.NIL;
    }
    final int[] padding = new int[3];
    final int[] grid = new int[3];
    for (int d = 0; d < 3; d++) {
      padding[d] = isCyclic ? 0 : steps * radius[d];
      grid[d] = size[d] + 2 * padding[d];
    }
    long cellCount = (long) grid[0] * grid[1] * grid[2];
    if (cellCount * Math.max(1, steps + s) > Config.MAX_AST_SIZE) {
      ASTElementLimitExceeded.throwIt(cellCount * Math.max(1, steps + s));
      return F.NIL;
    }
    final int cells = (int) cellCount;

    List<int[]> history = new ArrayList<>();
    List<Integer> bgHistory = new ArrayList<>();
    for (IAST array : initStates) {
      int[] state = new int[cells];
      java.util.Arrays.fill(state, background);
      for (int z = 1; z <= array.argSize(); z++) {
        IExpr slice = array.get(z);
        for (int y = 1; y <= slice.argSize(); y++) {
          IExpr row = slice.getAt(y);
          for (int x = 1; x <= row.argSize(); x++) {
            int value = row.getAt(x).toIntDefault(-1);
            if (value < 0 || value >= k) {
              return F.NIL;
            }
            state[((padding[0] + z - 1) * grid[1] + padding[1] + y - 1) * grid[2] + padding[2]
                + x - 1] = value;
          }
        }
      }
      history.add(state);
      bgHistory.add(background);
    }

    for (int t = 0; t < steps; t++) {
      int[] next = new int[cells];
      for (int z = 0; z < grid[0]; z++) {
        for (int y = 0; y < grid[1]; y++) {
          for (int x = 0; x < grid[2]; x++) {
            int v = 0;
            for (int stepIdx = s; stepIdx >= 1; stepIdx--) {
              int[] state = history.get(history.size() - stepIdx);
              int bg = bgHistory.get(bgHistory.size() - stepIdx);
              int w = 0;
              for (int dz = -radius[0]; dz <= radius[0]; dz++) {
                for (int dy = -radius[1]; dy <= radius[1]; dy++) {
                  for (int dx = -radius[2]; dx <= radius[2]; dx++) {
                    int nz = z + dz;
                    int ny = y + dy;
                    int nx = x + dx;
                    int cell;
                    if (isCyclic) {
                      nz = Math.floorMod(nz, grid[0]);
                      ny = Math.floorMod(ny, grid[1]);
                      nx = Math.floorMod(nx, grid[2]);
                      cell = state[(nz * grid[1] + ny) * grid[2] + nx];
                    } else if (nz < 0 || nz >= grid[0] || ny < 0 || ny >= grid[1] || nx < 0
                        || nx >= grid[2]) {
                      cell = bg;
                    } else {
                      cell = state[(nz * grid[1] + ny) * grid[2] + nx];
                    }
                    if (weights != null) {
                      v += weights[w] * cell;
                    } else if (isTotalistic) {
                      v += cell;
                    } else {
                      v = v * k + cell;
                    }
                    w++;
                  }
                }
              }
            }
            next[(z * grid[1] + y) * grid[2] + x] = ruleBits[v];
          }
        }
      }
      int nextBg = bgHistory.get(bgHistory.size() - 1);
      if (!isCyclic) {
        int v = 0;
        for (int stepIdx = s; stepIdx >= 1; stepIdx--) {
          int oldBg = bgHistory.get(bgHistory.size() - stepIdx);
          for (int w = 0; w < blockSize; w++) {
            if (weights != null) {
              v += weights[w] * oldBg;
            } else if (isTotalistic) {
              v += oldBg;
            } else {
              v = v * k + oldBg;
            }
          }
        }
        nextBg = ruleBits[v];
      }
      history.add(next);
      bgHistory.add(nextBg);
    }

    // the box of the cells to return: the initial array, and on a background every cell of the
    // returned steps which differs from it
    final boolean wholeGrid = isOperatorForm && !isCyclic;
    final int[] min = new int[3];
    final int[] max = new int[3];
    for (int d = 0; d < 3; d++) {
      min[d] = wholeGrid ? 0 : padding[d];
      max[d] = wholeGrid ? grid[d] - 1 : padding[d] + size[d] - 1;
    }
    // CPD-OFF int and expression states are filled and trimmed separately
    final int localDt = (dt == 0) ? 1 : dt;
    if (!isCyclic && !isOperatorForm) {
      for (int tIdx = tStart; (localDt > 0 ? tIdx <= tEnd : tIdx >= tEnd); tIdx += localDt) {
        int hIdx = s - 1 + tIdx;
        if (hIdx >= 0 && hIdx < history.size()) {
          int[] state = history.get(hIdx);
          int b = bgHistory.get(hIdx);
          for (int z = 0; z < grid[0]; z++) {
            for (int y = 0; y < grid[1]; y++) {
            // CPD-ON
              for (int x = 0; x < grid[2]; x++) {
                if (state[(z * grid[1] + y) * grid[2] + x] != b) {
                  min[0] = Math.min(min[0], z);
                  max[0] = Math.max(max[0], z);
                  min[1] = Math.min(min[1], y);
                  max[1] = Math.max(max[1], y);
                  min[2] = Math.min(min[2], x);
                  max[2] = Math.max(max[2], x);
                }
              // CPD-OFF int and expression states are filled and trimmed separately
              }
            }
          }
        }
      }
    }

    IASTAppendable resultList = F.ListAlloc();
    for (int tIdx = tStart; (localDt > 0 ? tIdx <= tEnd : tIdx >= tEnd); tIdx += localDt) {
      int hIdx = s - 1 + tIdx;
      if (hIdx >= 0 && hIdx < history.size()) {
        int[] state = history.get(hIdx);
        IASTAppendable array = F.ListAlloc(max[0] - min[0] + 1);
        // CPD-ON
        for (int z = min[0]; z <= max[0]; z++) {
          IASTAppendable matrix = F.ListAlloc(max[1] - min[1] + 1);
          for (int y = min[1]; y <= max[1]; y++) {
            IASTAppendable row = F.ListAlloc(max[2] - min[2] + 1);
            for (int x = min[2]; x <= max[2]; x++) {
              row.append(F.ZZ(state[(z * grid[1] + y) * grid[2] + x]));
            }
            matrix.append(row);
          }
          array.append(matrix);
        }
        resultList.append(array);
      }
    }

    if (isOperatorForm && resultList.argSize() == 1) {
      if (!isCyclic) {
        // on a background the operator form gives {array, {{{b}}}}
        int bg = bgHistory.get(s - 1 + tStart);
        return F.List(resultList.arg1(), F.List(F.List(F.List(F.ZZ(bg)))));
      }
      return resultList.arg1();
    } else if (returnSingleStep && resultList.argSize() == 1) {
      return resultList.arg1();
    }
    return resultList;
  }

  /**
   * The weights of the cells of a <code>block[0] x block[1] x block[2]</code> neighbourhood as a
   * flat array, or <code>null</code> if <code>wspec</code> isn't an array of that shape of
   * non-negative integers.
   */
  private static int[] weights3D(IExpr wspec, int[] block) {
    if (!wspec.isList() || wspec.argSize() != block[0]) {
      return null;
    }
    int[] weights = new int[block[0] * block[1] * block[2]];
    int w = 0;
    for (int z = 1; z <= block[0]; z++) {
      IExpr slice = wspec.getAt(z);
      if (!slice.isList() || slice.argSize() != block[1]) {
        return null;
      }
      for (int y = 1; y <= block[1]; y++) {
        IExpr row = slice.getAt(y);
        if (!row.isList() || row.argSize() != block[2]) {
          return null;
        }
        for (int x = 1; x <= block[2]; x++) {
          int value = row.getAt(x).isInteger() ? row.getAt(x).toIntDefault(-1) : -1;
          if (value < 0) {
            return null;
          }
          weights[w++] = value;
        }
      }
    }
    return weights;
  }

  /**
   * Widen <code>size</code> to the extent <code>{slices, rows, columns}</code> of an array of
   * rank 3.
   *
   * @return <code>false</code> if <code>array</code> isn't a list of lists of lists
   */
  private static boolean extent3D(IAST array, int[] size) {
    size[0] = Math.max(size[0], array.argSize());
    for (int z = 1; z <= array.argSize(); z++) {
      IExpr slice = array.get(z);
      if (!slice.isList()) {
        return false;
      }
      size[1] = Math.max(size[1], slice.argSize());
      for (int y = 1; y <= slice.argSize(); y++) {
        IExpr row = slice.getAt(y);
        if (!row.isList()) {
          return false;
        }
        size[2] = Math.max(size[2], row.argSize());
      }
    }
    return true;
  }

  /**
   * Evaluates general symbolic 1D rules such as explicit replacement lists and pure functions.
   */
  private IExpr evaluateGeneral1D(IExpr rule, IAST init, int steps, int tStart, int tEnd, int dt,
      boolean returnSingleStep, boolean isOperatorForm, EvalEngine engine) {

    if (init.argSize() == 1 && init.arg1().isList()) {
      init = (IAST) init.arg1();
    }

    boolean isExplicitRule = rule.isList() && rule.argSize() > 0 && rule.get(1).isRuleAST();
    boolean isGeneralFunList =
        rule.isList() && rule.argSize() >= 3 && rule.get(2).isList() && rule.get(2).argSize() == 0;
    boolean isBfun = rule.isAST(F.Function) || rule.isBuiltInSymbol();

    if (!isExplicitRule && !isGeneralFunList && !isBfun) {
      return F.NIL;
    }

    int[] explicitOffsets = null;
    int rMin = -1;
    int rMax = 1;
    IExpr fun = rule;

    if (isExplicitRule) {
      IAST firstRule = (IAST) rule.get(1);
      if (firstRule.arg1().isList()) {
        int size = firstRule.arg1().argSize();
        rMin = -(size / 2);
        rMax = rMin + size - 1;
      }
    } else if (isGeneralFunList) {
      fun = rule.get(1); // Extract pure function
      IExpr rspec = rule.get(3);

      if (rspec.isNumber()) {
        double rVal = rspec.evalfNaN();
        if (Double.isNaN(rVal)) {
          return F.NIL;
        }
        int size = (int) Math.round(2 * rVal + 1);
        rMin = -(int) Math.floor(rVal + 0.5);
        rMax = rMin + size - 1;
      } else if (rspec.isList()) {
        if (rspec.argSize() == 1 && rspec.get(1).isNumber()) { // {r}
          double rVal = rspec.get(1).evalfNaN();
          if (Double.isNaN(rVal)) {
            return F.NIL;
          }
          int size = (int) Math.round(2 * rVal + 1);
          rMin = -(int) Math.floor(rVal + 0.5);
          rMax = rMin + size - 1;
        } else {
          boolean isOffsets = true;
          int[] tempOffsets = new int[rspec.argSize()];
          for (int i = 1; i <= rspec.argSize(); i++) {
            IExpr item = rspec.get(i);
            if (item.isList() && item.argSize() == 1 && item.get(1).isInteger()) {
              tempOffsets[i - 1] = item.get(1).toIntDefault(0);
            } else {
              isOffsets = false;
              break;
            }
          }
          if (isOffsets)
            explicitOffsets = tempOffsets;
        }
      }
    } else if (isBfun) {
      int v = getBooleanFunctionVariables(rule);
      if (v <= 0)
        v = 3;
      rMin = -(v / 2);
      rMax = rMin + v - 1;
    }

    int maxAbsOffset = 0;
    if (explicitOffsets != null) {
      for (int off : explicitOffsets) {
        if (Math.abs(off) > maxAbsOffset)
          maxAbsOffset = Math.abs(off);
      }
    } else {
      maxAbsOffset = Math.max(Math.abs(rMin), Math.abs(rMax));
    }

    IExpr[] aspec;
    IExpr bg = F.NIL;
    boolean isCyclic = true;

    if (init.argSize() == 2 && init.get(1).isList()) {
      IAST aspecAST = (IAST) init.get(1);
      if (aspecAST.argSize() == 1 && aspecAST.arg1().isList()) {
        aspecAST = (IAST) aspecAST.arg1();
      }
      aspec = new IExpr[aspecAST.argSize()];
      for (int i = 0; i < aspecAST.argSize(); i++) {
        aspec[i] = aspecAST.get(i + 1);
      }
      bg = init.get(2);
      // a background block {b} of one cell is the background b
      bg = background1D(bg).orElse(bg);
      isCyclic = false;
    } else if (init.isList()) {
      aspec = new IExpr[init.argSize()];
      for (int i = 0; i < init.argSize(); i++) {
        aspec[i] = init.get(i + 1);
      }
      isCyclic = true;
    } else {
      return F.NIL;
    }

    if (aspec.length == 0)
      return F.NIL;

    int padding = isCyclic ? 0 : steps * maxAbsOffset;
    int gridSize = aspec.length + 2 * padding;

    IExpr[] currentState = new IExpr[gridSize];
    IExpr currentBg = bg;

    for (int i = 0; i < gridSize; i++)
      currentState[i] = currentBg;
    for (int i = 0; i < aspec.length; i++)
      currentState[padding + i] = aspec[i];

    List<IExpr[]> history = new ArrayList<>();
    history.add(currentState.clone());

    List<IExpr> bgHistory = new ArrayList<>();
    bgHistory.add(currentBg);

    for (int t = 0; t < steps; t++) {
      IExpr[] nextState = new IExpr[gridSize];
      for (int i = 0; i < gridSize; i++) {
        IASTAppendable neighborhood;

        if (explicitOffsets != null) {
          neighborhood = F.ListAlloc(explicitOffsets.length);
          for (int off : explicitOffsets) {
            int idx = i + off;
            if (isCyclic) {
              idx = (idx % gridSize + gridSize) % gridSize;
              neighborhood.append(currentState[idx]);
            } else {
              if (idx < 0 || idx >= gridSize) {
                neighborhood.append(currentBg);
              } else {
                neighborhood.append(currentState[idx]);
              }
            }
          }
        } else {
          neighborhood = F.ListAlloc(rMax - rMin + 1);
          for (int di = rMin; di <= rMax; di++) {
            int idx = i + di;
            if (isCyclic) {
              idx = (idx % gridSize + gridSize) % gridSize;
              neighborhood.append(currentState[idx]);
            } else {
              if (idx < 0 || idx >= gridSize) {
                neighborhood.append(currentBg);
              } else {
                neighborhood.append(currentState[idx]);
              }
            }
          }
        }

        if (isExplicitRule) {
          nextState[i] = engine.evaluate(F.Replace(neighborhood, rule));
        } else if (isGeneralFunList) {
          nextState[i] = engine.evaluate(F.binaryAST2(fun, neighborhood, F.ZZ(t + 1)));
        } else if (isBfun) {
          IASTAppendable apply = F.ast(fun);
          for (int j = 1; j <= neighborhood.argSize(); j++) {
            apply.append(toBoolean(neighborhood.get(j)));
          }
          nextState[i] = fromBoolean(engine.evaluate(apply), engine);
        }
      }

      if (!isCyclic) {
        IASTAppendable bgNeighborhood;

        if (explicitOffsets != null) {
          bgNeighborhood = F.ListAlloc(explicitOffsets.length);
          for (int i = 0; i < explicitOffsets.length; i++) {
            bgNeighborhood.append(currentBg);
          }
        } else {
          bgNeighborhood = F.ListAlloc(rMax - rMin + 1);
          for (int di = rMin; di <= rMax; di++)
            bgNeighborhood.append(currentBg);
        }

        if (isExplicitRule) {
          currentBg = engine.evaluate(F.Replace(bgNeighborhood, rule));
        } else if (isGeneralFunList) {
          currentBg = engine.evaluate(F.binaryAST2(fun, bgNeighborhood, F.ZZ(t + 1)));
        } else if (isBfun) {
          IASTAppendable apply = F.ast(fun);
          for (int j = 1; j <= bgNeighborhood.argSize(); j++) {
            apply.append(toBoolean(bgNeighborhood.get(j)));
          }
          currentBg = fromBoolean(engine.evaluate(apply), engine);
        }
      }

      currentState = nextState;
      history.add(currentState.clone());
      bgHistory.add(currentBg);
    }

    int minX = (isOperatorForm && !isCyclic) ? 0 : padding;
    int maxX = (isOperatorForm && !isCyclic) ? gridSize - 1 : padding + aspec.length - 1;

    int localDt = (dt == 0) ? 1 : dt;

    if (!isCyclic && !isOperatorForm) {
      for (int tIdx = tStart; (localDt > 0 ? tIdx <= tEnd : tIdx >= tEnd); tIdx += localDt) {
        int hIdx = tIdx;
        if (hIdx >= 0 && hIdx < history.size()) {
          IExpr[] state = history.get(hIdx);
          IExpr b = bgHistory.get(hIdx);
          for (int i = 0; i < gridSize; i++) {
            if (!state[i].equals(b)) {
              if (i < minX)
                minX = i;
              if (i > maxX)
                maxX = i;
            }
          }
        }
      }
    }

    IASTAppendable resultList = F.ListAlloc();
    for (int tIdx = tStart; (localDt > 0 ? tIdx <= tEnd : tIdx >= tEnd); tIdx += localDt) {
      int hIdx = tIdx;
      if (hIdx >= 0 && hIdx < history.size()) {
        IExpr[] state = history.get(hIdx);
        IASTAppendable row = F.ListAlloc(maxX - minX + 1);
        for (int i = minX; i <= maxX; i++) {
          row.append(state[i]);
        }
        resultList.append(row);
      }
    }

    if (returnSingleStep && resultList.argSize() == 1) {
      if (isOperatorForm && !isCyclic) {
        IExpr bgVal = bgHistory.get(tStart); // hIdx=tIdx for general1D
        IASTAppendable pair = F.ListAlloc(2);
        pair.append(resultList.arg1());
        pair.append(F.List(bgVal));
        return pair;
      }
      return resultList.arg1();
    }
    return resultList;
  }

  /**
   * Evaluates general symbolic rules such as explicit replacement lists and pure functions in 2D.
   */
  private IExpr evaluateGeneral2D(IExpr rule, IAST init, int steps, int tStart, int tEnd, int dt,
      boolean returnSingleStep, boolean isOperatorForm, int rY, int rX, EvalEngine engine) {

    boolean isExplicitRule = rule.isList() && rule.argSize() > 0 && rule.get(1).isRuleAST();
    boolean isGeneralFunList =
        rule.isList() && rule.argSize() >= 3 && rule.get(2).isList() && rule.get(2).argSize() == 0;

    if (!isExplicitRule && !isGeneralFunList) {
      return F.NIL;
    }

    IExpr fun = rule;
    // {f, {}, {{dy1, dx1}, {dy2, dx2}, ...}}: f gets the values at these offsets, in this order
    int[][] offsets = null;
    if (isGeneralFunList) {
      fun = rule.get(1);
      offsets = offsetNeighbourhood2D(rule.get(3));
      if (offsets != null) {
        rY = 0;
        rX = 0;
        for (int[] offset : offsets) {
          rY = Math.max(rY, Math.abs(offset[0]));
          rX = Math.max(rX, Math.abs(offset[1]));
        }
      }
    }

    boolean isCyclic = true;
    List<IAST> initStates = new ArrayList<>();

    IExpr planeBackground = init.argSize() == 2 && init.get(1).isList() ? //
        (init.get(2).isInteger() ? init.get(2) : background2D(init.get(2))) : F.NIL;
    if (planeBackground.isPresent()) {
      // {array, b} or {array, {{b}}}: the array on a background of b
      IAST aspecList = (IAST) init.get(1);
      isCyclic = false;

      IASTAppendable paddedStep = F.ListAlloc(2);
      paddedStep.append(aspecList);
      paddedStep.append(planeBackground);
      initStates.add(paddedStep);
    } else {
      isCyclic = true;
      initStates.add(init);
    // CPD-OFF int and expression states are filled and trimmed separately
    }

    IAST firstInit = initStates.get(0);
    int[] extent = aspecExtent2D(firstInit, isCyclic);
    if (extent == null) {
      return F.NIL;
    }
    int aspecSizeY = extent[0];
    int aspecSizeX = extent[1];

    int paddingY = isCyclic ? 0 : steps * rY;
    int paddingX = isCyclic ? 0 : steps * rX;
    int gridY = aspecSizeY + 2 * paddingY;
    int gridX = aspecSizeX + 2 * paddingX;

    List<IExpr[]> history = new ArrayList<>();
    // CPD-ON
    List<IExpr> bgHistory = new ArrayList<>();

    IAST stateExpr = initStates.get(0);
    IAST currentAspec;
    IExpr bgForThisStep = F.NIL;

    if (!isCyclic) {
      currentAspec = (IAST) stateExpr.arg1();
      bgForThisStep = stateExpr.arg2();
    } else {
      currentAspec = stateExpr;
    }

    // CPD-OFF int and expression states are filled and trimmed separately
    IExpr[] stateArr = new IExpr[gridY * gridX];
    for (int i = 0; i < stateArr.length; i++)
      stateArr[i] = bgForThisStep;

    for (int y = 1; y <= currentAspec.argSize(); y++) {
      if (currentAspec.get(y).isList()) {
        IAST row = (IAST) currentAspec.get(y);
        for (int x = 1; x <= row.argSize(); x++) {
          int gY = paddingY + y - 1;
          int gX = paddingX + x - 1;
          if (gY < gridY && gX < gridX) {
          // CPD-ON
            stateArr[gY * gridX + gX] = row.get(x);
          }
        }
      }
    }
    history.add(stateArr);
    bgHistory.add(bgForThisStep);

    for (int t = 0; t < steps; t++) {
      IExpr[] nextState = new IExpr[gridY * gridX];
      for (int y = 0; y < gridY; y++) {
        for (int x = 0; x < gridX; x++) {
          IExpr[] state = history.get(history.size() - 1);
          if (offsets != null) {
            IASTAppendable values = F.ListAlloc(offsets.length);
            for (int[] offset : offsets) {
              int ny = y + offset[0];
              int nx = x + offset[1];
              if (isCyclic) {
                ny = (ny % gridY + gridY) % gridY;
                nx = (nx % gridX + gridX) % gridX;
                values.append(state[ny * gridX + nx]);
              } else if (ny < 0 || ny >= gridY || nx < 0 || nx >= gridX) {
                values.append(bgHistory.get(bgHistory.size() - 1));
              } else {
                values.append(state[ny * gridX + nx]);
              }
            }
            nextState[y * gridX + x] = engine.evaluate(F.binaryAST2(fun, values, F.ZZ(t + 1)));
            continue;
          }
          IASTAppendable neighborhood = F.ListAlloc(2 * rY + 1);
          for (int dy = -rY; dy <= rY; dy++) {
            IASTAppendable row = F.ListAlloc(2 * rX + 1);
            for (int dx = -rX; dx <= rX; dx++) {
              int ny = y + dy;
              int nx = x + dx;
              if (isCyclic) {
                ny = (ny % gridY + gridY) % gridY;
                nx = (nx % gridX + gridX) % gridX;
                row.append(state[ny * gridX + nx]);
              } else {
                if (ny < 0 || ny >= gridY || nx < 0 || nx >= gridX) {
                  row.append(bgHistory.get(bgHistory.size() - 1));
                } else {
                  row.append(state[ny * gridX + nx]);
                }
              }
            }
            neighborhood.append(row);
          }
          if (isExplicitRule) {
            nextState[y * gridX + x] = engine.evaluate(F.Replace(neighborhood, rule));
          } else {
            nextState[y * gridX + x] =
                engine.evaluate(F.binaryAST2(fun, neighborhood, F.ZZ(t + 1)));
          }
        }
      }

      IExpr nextBg = F.NIL;
      if (!isCyclic) {
        IExpr oldBg = bgHistory.get(bgHistory.size() - 1);
        IASTAppendable bgNeighborhood = F.ListAlloc(2 * rY + 1);
        if (offsets != null) {
          for (int i = 0; i < offsets.length; i++) {
            bgNeighborhood.append(oldBg);
          }
        } else {
          for (int dy = -rY; dy <= rY; dy++) {
            IASTAppendable row = F.ListAlloc(2 * rX + 1);
            for (int dx = -rX; dx <= rX; dx++) {
              row.append(oldBg);
            }
            bgNeighborhood.append(row);
          }
        }
        if (isExplicitRule) {
          nextBg = engine.evaluate(F.Replace(bgNeighborhood, rule));
        } else {
          nextBg = engine.evaluate(F.binaryAST2(fun, bgNeighborhood, F.ZZ(t + 1)));
        }
      // CPD-OFF int and expression states are trimmed separately
      } else {
        nextBg = bgHistory.get(bgHistory.size() - 1);
      }

      history.add(nextState);
      bgHistory.add(nextBg);
    }

    int minY = (isOperatorForm && !isCyclic) ? 0 : paddingY;
    int maxY = (isOperatorForm && !isCyclic) ? gridY - 1 : paddingY + aspecSizeY - 1;
    int minX = (isOperatorForm && !isCyclic) ? 0 : paddingX;
    int maxX = (isOperatorForm && !isCyclic) ? gridX - 1 : paddingX + aspecSizeX - 1;

    int localDt = (dt == 0) ? 1 : dt;

    if (!isCyclic && !isOperatorForm) {
      for (int tIdx = tStart; (localDt > 0 ? tIdx <= tEnd : tIdx >= tEnd); tIdx += localDt) {
      // CPD-ON
        if (tIdx >= 0 && tIdx < history.size()) {
          IExpr[] state = history.get(tIdx);
          IExpr b = bgHistory.get(tIdx);
          for (int y = 0; y < gridY; y++) {
            for (int x = 0; x < gridX; x++) {
              // CPD-OFF int and expression states are filled and trimmed separately
              if (!state[y * gridX + x].equals(b)) {
                if (y < minY)
                  minY = y;
                if (y > maxY)
                  maxY = y;
                if (x < minX)
                  minX = x;
                if (x > maxX)
                  maxX = x;
              }
            }
          }
        }
      }
    }

    IASTAppendable resultList = F.ListAlloc();
    for (int tIdx = tStart; (localDt > 0 ? tIdx <= tEnd : tIdx >= tEnd); tIdx += localDt) {
    // CPD-ON
      if (tIdx >= 0 && tIdx < history.size()) {
        IExpr[] state = history.get(tIdx);
        IASTAppendable matrix = F.ListAlloc(maxY - minY + 1);
        for (int y = minY; y <= maxY; y++) {
          IASTAppendable row = F.ListAlloc(maxX - minX + 1);
          for (int x = minX; x <= maxX; x++) {
            row.append(state[y * gridX + x]);
          }
          matrix.append(row);
        }
        resultList.append(matrix);
      }
    }

    if (returnSingleStep && resultList.argSize() == 1) {
      if (isOperatorForm && !isCyclic) {
        IExpr bgVal = bgHistory.get(tStart);
        IASTAppendable pair = F.ListAlloc(2);
        pair.append(resultList.arg1());
        pair.append(F.List(F.List(bgVal))); // {{bg}} for 2D
        return pair;
      }
      return resultList.arg1();
    }
    return resultList;
  }

  /**
   * Helper to map integers to Boolean bounds for pure boolean functions.
   */
  private IExpr toBoolean(IExpr expr) {
    if (expr.equals(F.C1))
      return F.True;
    if (expr.equals(F.C0))
      return F.False;
    return expr;
  }

  /**
   * Helper to evaluate Booleans back into integer bounds (1 and 0).
   */
  private IExpr fromBoolean(IExpr expr, EvalEngine engine) {
    if (expr == S.True)
      return F.C1;
    if (expr == S.False)
      return F.C0;
    if (expr.isInteger())
      return expr;
    IExpr boole = engine.evaluate(F.Boole(expr));
    return boole.isPresent() ? boole : expr;
  }

  /**
   * Helper to determine variables in a pure boolean function (bfun).
   */
  private int getBooleanFunctionVariables(IExpr expr) {
    if (expr.isAST(F.Function, 3)) {
      IExpr vars = ((IAST) expr).arg1();
      if (vars.isList()) {
        return vars.argSize();
      } else if (vars.isSymbol()) {
        return 1;
      }
    }
    return getMaxSlot(expr);
  }

  /**
   * Helper to scan down the AST and fetch the highest slot integer.
   */
  private int getMaxSlot(IExpr expr) {
    if (expr.isAST(F.Slot, 2)) {
      IExpr arg = ((IAST) expr).arg1();
      if (arg.isInteger()) {
        return arg.toIntDefault(0);
      }
    }
    int max = 0;
    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      for (int i = 1; i <= ast.argSize(); i++) {
        int s = getMaxSlot(ast.get(i));
        if (s > max)
          max = s;
      }
    }
    return max;
  }

  /**
   * The extent of the initial 2D state: the number of rows and the length of the longest row.
   *
   * @param firstInit the first initial state, <code>{aspec, background}</code> unless cyclic
   * @return <code>{rows, columns}</code>, or <code>null</code> if either is zero
   */
  private static int[] aspecExtent2D(IAST firstInit, boolean isCyclic) {
    IAST aspec = isCyclic ? firstInit : (IAST) firstInit.arg1();
    int aspecSizeY = aspec.argSize();
    if (aspecSizeY == 0) {
      return null;
    }
    int aspecSizeX = 0;
    for (int i = 1; i <= aspecSizeY; i++) {
      if (aspec.get(i).isList()) {
        int cols = ((IAST) aspec.get(i)).argSize();
        if (cols > aspecSizeX) {
          aspecSizeX = cols;
        }
      }
    }
    if (aspecSizeX == 0) {
      return null;
    }
    return new int[] {aspecSizeY, aspecSizeX};
  }
}
