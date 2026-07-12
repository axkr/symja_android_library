package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;
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

        IExpr tSpec = (argSize >= 3) ? ast.arg3() : F.C1;

        if (argSize == 3) {
          if (tSpec.isInteger()) {
            tStart = 0;
            tEnd = tSpec.toIntDefault(-1);
          } else if (tSpec.isList()) {
            IAST tList = (IAST) tSpec;
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
                    validRule = false; // Possibly complex offset
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
                  (IAST) init, stepsToGenerate, tStart, tEnd, dt, returnSingleStep, isOperatorForm);
            }
          }

          // Fallback to General CA (Explicit rules, functions, or symbolic states)
          if (!result.isPresent()) {
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
  private IExpr evaluateInteger1D(IInteger ruleNum, int k, int r, int s, boolean isTotalistic,
      int[][] explicitWeights, IAST init, int steps, int tStart, int tEnd, int dt,
      boolean returnSingleStep, boolean isOperatorForm) {
    if (k < 2 || r < 0 || s < 1)
      return F.NIL;

    long numTransitions;
    if (explicitWeights != null) {
      int weightTotal = 0;
      for (int j = 0; j < 2 * r + 1; j++) {
        weightTotal += explicitWeights[0][j];
      }
      numTransitions = (long) weightTotal * (k - 1) * s + 1;
    } else if (isTotalistic) {
      numTransitions = (long) s * (2 * r + 1) * (k - 1) + 1;
    } else {
      double numTransDouble = Math.pow(k, s * (2 * r + 1));
      if (numTransDouble > Config.MAX_AST_SIZE) {
        ASTElementLimitExceeded.throwIt((long) numTransDouble);
        return F.NIL;
      }
      numTransitions = (long) numTransDouble;
    }

    if (numTransitions > Config.MAX_AST_SIZE) {
      ASTElementLimitExceeded.throwIt(numTransitions);
      return F.NIL;
    }
    int[] ruleBits = new int[(int) numTransitions];

    IInteger temp = ruleNum;
    IInteger ZZk = F.ZZ(k);
    for (int i = 0; i < numTransitions; i++) {
      IExpr[] divMod = temp.divideAndRemainder(ZZk);
      ruleBits[i] = divMod[1].toIntDefault(0);
      temp = (IInteger) divMod[0];
    }

    boolean isCyclic = true;
    List<IAST> initStates = new ArrayList<>();

    if (s == 1 && init.argSize() == 1 && init.arg1().isList()) {
      init = (IAST) init.arg1();
    }

    if (init.argSize() == 2 && init.get(2).isInteger() && init.get(1).isList()) {
      IAST aspecList = (IAST) init.get(1);
      int bg = init.get(2).toIntDefault(0);
      isCyclic = false;

      if (s == 1 && aspecList.argSize() == 1 && aspecList.arg1().isList()) {
        aspecList = (IAST) aspecList.arg1();
      }

      if (s > 1) {
        if (aspecList.argSize() == s && aspecList.arg1().isList()) {
          for (int i = 1; i <= s; i++) {
            IASTAppendable paddedStep = F.ListAlloc(2);
            paddedStep.append(aspecList.get(i));
            paddedStep.append(F.ZZ(bg));
            initStates.add(paddedStep);
          }
        } else {
          return F.NIL;
        }
      } else {
        IASTAppendable paddedStep = F.ListAlloc(2);
        paddedStep.append(aspecList);
        paddedStep.append(F.ZZ(bg));
        initStates.add(paddedStep);
      }
    } else {
      isCyclic = true;
      if (s > 1) {
        if (init.argSize() == s && init.arg1().isList()) {
          for (int i = 1; i <= s; i++) {
            IExpr stepVal = init.get(i);
            if (stepVal.isList()) {
              initStates.add((IAST) stepVal);
            } else {
              return F.NIL;
            }
          }
        } else {
          return F.NIL;
        }
      } else {
        initStates.add(init);
      }
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

    int localDt = (dt == 0) ? 1 : dt;

    if (!isCyclic && !isOperatorForm) {
      for (int tIdx = tStart; (localDt > 0 ? tIdx <= tEnd : tIdx >= tEnd); tIdx += localDt) {
        int hIdx = s - 1 + tIdx;
        if (hIdx >= 0 && hIdx < history.size()) {
          int[] state = history.get(hIdx);
          int b = bgHistory.get(hIdx);
          for (int i = 0; i < gridSize; i++) {
            if (state[i] != b) {
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
      int hIdx = s - 1 + tIdx;
      if (hIdx >= 0 && hIdx < history.size()) {
        int[] state = history.get(hIdx);
        IASTAppendable row = F.ListAlloc(maxX - minX + 1);
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

  /**
   * Evaluates k-color, range-r, order-s generalized integer cellular automata for 2D grids.
   */
  private IExpr evaluateInteger2D(IInteger ruleNum, int k, int rY, int rX, int s,
      boolean isTotalistic, int[][] explicitWeights, IAST init, int steps, int tStart, int tEnd,
      int dt, boolean returnSingleStep, boolean isOperatorForm) {
    if (k < 2 || rY < 0 || rX < 0 || s < 1)
      return F.NIL;

    long numTransitions;
    if (explicitWeights != null) {
      int weightTotal = 0;
      for (int i = 0; i < 2 * rY + 1; i++) {
        for (int j = 0; j < 2 * rX + 1; j++) {
          weightTotal += explicitWeights[i][j];
        }
      }
      numTransitions = (long) weightTotal * (k - 1) * s + 1;
    } else if (isTotalistic) {
      numTransitions = (long) s * ((2 * rY + 1) * (2 * rX + 1)) * (k - 1) + 1;
    } else {
      double numTransDouble = Math.pow(k, s * (2 * rY + 1) * (2 * rX + 1));
      if (numTransDouble > Config.MAX_AST_SIZE) {
        ASTElementLimitExceeded.throwIt((long) numTransDouble);
        return F.NIL;
      }
      numTransitions = (long) numTransDouble;
    }

    if (numTransitions > Config.MAX_AST_SIZE) {
      ASTElementLimitExceeded.throwIt(numTransitions);
      return F.NIL;
    }
    int[] ruleBits = new int[(int) numTransitions];

    IInteger temp = ruleNum;
    IInteger ZZk = F.ZZ(k);
    for (int i = 0; i < numTransitions; i++) {
      IExpr[] divMod = temp.divideAndRemainder(ZZk);
      ruleBits[i] = divMod[1].toIntDefault(0);
      temp = (IInteger) divMod[0];
    }

    boolean isCyclic = true;
    List<IAST> initStates = new ArrayList<>();

    if (init.argSize() == 2 && init.get(2).isInteger() && init.get(1).isList()) {
      IAST aspecList = (IAST) init.get(1);
      int bg = init.get(2).toIntDefault(0);
      isCyclic = false;

      if (s > 1) {
        if (aspecList.argSize() == s) {
          for (int i = 1; i <= s; i++) {
            IASTAppendable paddedStep = F.ListAlloc(2);
            paddedStep.append(aspecList.get(i));
            paddedStep.append(F.ZZ(bg));
            initStates.add(paddedStep);
          }
        } else {
          return F.NIL;
        }
      } else {
        IASTAppendable paddedStep = F.ListAlloc(2);
        paddedStep.append(aspecList);
        paddedStep.append(F.ZZ(bg));
        initStates.add(paddedStep);
      }
    } else {
      isCyclic = true;
      if (s > 1) {
        if (init.argSize() == s) {
          for (int i = 1; i <= s; i++) {
            initStates.add((IAST) init.get(i));
          }
        } else {
          return F.NIL;
        }
      } else {
        initStates.add(init);
      }
    }

    IAST firstInit = initStates.get(0);
    IAST aspec;

    if (!isCyclic) {
      aspec = (IAST) firstInit.arg1();
    } else {
      aspec = firstInit;
    }

    int aspecSizeY = aspec.argSize();
    if (aspecSizeY == 0)
      return F.NIL;
    int aspecSizeX = 0;
    for (int i = 1; i <= aspecSizeY; i++) {
      if (aspec.get(i).isList()) {
        int cols = ((IAST) aspec.get(i)).argSize();
        if (cols > aspecSizeX)
          aspecSizeX = cols;
      }
    }
    if (aspecSizeX == 0)
      return F.NIL;

    int paddingY = isCyclic ? 0 : steps * rY;
    int paddingX = isCyclic ? 0 : steps * rX;
    int gridY = aspecSizeY + 2 * paddingY;
    int gridX = aspecSizeX + 2 * paddingX;

    List<int[]> history = new ArrayList<>();
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
        int hIdx = s - 1 + tIdx;
        if (hIdx >= 0 && hIdx < history.size()) {
          int[] state = history.get(hIdx);
          int b = bgHistory.get(hIdx);
          for (int y = 0; y < gridY; y++) {
            for (int x = 0; x < gridX; x++) {
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
        double rVal = rspec.evalf();
        int size = (int) Math.round(2 * rVal + 1);
        rMin = -(int) Math.floor(rVal + 0.5);
        rMax = rMin + size - 1;
      } else if (rspec.isList()) {
        if (rspec.argSize() == 1 && rspec.get(1).isNumber()) { // {r}
          double rVal = rspec.get(1).evalf();
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
    if (isGeneralFunList) {
      fun = rule.get(1);
    }

    boolean isCyclic = true;
    List<IAST> initStates = new ArrayList<>();

    if (init.argSize() == 2 && init.get(2).isInteger() && init.get(1).isList()) {
      IAST aspecList = (IAST) init.get(1);
      int bg = init.get(2).toIntDefault(0);
      isCyclic = false;

      IASTAppendable paddedStep = F.ListAlloc(2);
      paddedStep.append(aspecList);
      paddedStep.append(F.ZZ(bg));
      initStates.add(paddedStep);
    } else {
      isCyclic = true;
      initStates.add(init);
    }

    IAST firstInit = initStates.get(0);
    IAST aspec;
    if (!isCyclic) {
      aspec = (IAST) firstInit.arg1();
    } else {
      aspec = firstInit;
    }

    int aspecSizeY = aspec.argSize();
    if (aspecSizeY == 0)
      return F.NIL;
    int aspecSizeX = 0;
    for (int i = 1; i <= aspecSizeY; i++) {
      if (aspec.get(i).isList()) {
        int cols = ((IAST) aspec.get(i)).argSize();
        if (cols > aspecSizeX)
          aspecSizeX = cols;
      }
    }
    if (aspecSizeX == 0)
      return F.NIL;

    int paddingY = isCyclic ? 0 : steps * rY;
    int paddingX = isCyclic ? 0 : steps * rX;
    int gridY = aspecSizeY + 2 * paddingY;
    int gridX = aspecSizeX + 2 * paddingX;

    List<IExpr[]> history = new ArrayList<>();
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
        for (int dy = -rY; dy <= rY; dy++) {
          IASTAppendable row = F.ListAlloc(2 * rX + 1);
          for (int dx = -rX; dx <= rX; dx++) {
            row.append(oldBg);
          }
          bgNeighborhood.append(row);
        }
        if (isExplicitRule) {
          nextBg = engine.evaluate(F.Replace(bgNeighborhood, rule));
        } else {
          nextBg = engine.evaluate(F.binaryAST2(fun, bgNeighborhood, F.ZZ(t + 1)));
        }
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
        if (tIdx >= 0 && tIdx < history.size()) {
          IExpr[] state = history.get(tIdx);
          IExpr b = bgHistory.get(tIdx);
          for (int y = 0; y < gridY; y++) {
            for (int x = 0; x < gridX; x++) {
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
    if (expr.equals(F.True))
      return F.C1;
    if (expr.equals(F.False))
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
}
