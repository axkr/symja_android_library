package org.matheclipse.core.form.tex;

import java.util.HashMap;
import java.util.Map;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

public class TeXParser extends TeXScanner {

  // public static String[] FUNCTION_NAMES = new String[] {"f", "g", "h", //
  // "C", "D", "F", "G", "H", "P"};
  //
  // public static String[] FUNCTION_NAME_MAP = new String[] {//
  // "max", "Max", //
  // "min", "Min", //
  // };
  //
  // public static Map<String, String> FUNCTION_NAMES_MAP = new HashMap<String, String>();

  Map<Integer, IExpr> fMapOfVariables = new HashMap<Integer, IExpr>();

  static {
    TeXSegmentParser.initialize();
    // for (int i = 0; i < FUNCTION_NAMES.length; i++) {
    // FUNCTION_NAMES_MAP.put(FUNCTION_NAMES[i], FUNCTION_NAMES[i]);
    // }
    // int i = 0;
    // while (i < FUNCTION_NAME_MAP.length) {
    // FUNCTION_NAMES_MAP.put(FUNCTION_NAME_MAP[i], FUNCTION_NAME_MAP[i + 1]);
    // i = i + 2;
    // }
  }

  int fVariableCounter = 1;

  public TeXParser() {
    super(false, false);
  }

  private IExpr toExpr(String texStr) {
    TeXSegmentParser texParser = new TeXSegmentParser();
    return texParser.toExpression(texStr);
  }

  public IExpr parseArrayAsList(int lastIndex) {
    IASTAppendable list = F.ListAlloc();
    IASTAppendable subList = F.ListAlloc();
    int numberOfColumns = 0;
    if (fToken == TT_LIST_OPEN) {
      getNextToken();
      while (fToken == TT_IDENTIFIER) {
        String identifier = getIdentifier();
        numberOfColumns += identifier.length();
        getNextToken();
        if (fToken == TT_LIST_CLOSE) {
          if (numberOfColumns == 0) {
            return list;
          }
        }
      }
    }
    while (fToken != TT_EOF) {
      getNextToken();
      if (fToken == TT_LIST_OPEN) {
        int startOfSubExpr = fCurrentPosition;
        getNextToken();

        int endOfSubExpr = indexOfToken(TT_LIST_OPEN, TT_LIST_CLOSE);
        if (endOfSubExpr < 0) {
          return S.Null;
        }

        String exprStr = new String(fInputString, startOfSubExpr, endOfSubExpr - startOfSubExpr);
        // IExpr expr = toExpr(exprStr);
        IExpr expr = TeXParser.convert(exprStr);
        if (numberOfColumns == 1) {
          list.append(expr);
        } else {
          subList.append(expr);
        }
        continue;
      }
      if (fToken == TT_DOUBLE_BACKSLASH) {
        if (numberOfColumns > 1) {
          list.append(subList);
          subList = F.ListAlloc();
        }
        continue;
      }
      if (fToken == TT_END) {
        getNextToken();
        if (numberOfColumns > 1) {
          list.append(subList);
          subList = F.ListAlloc();
        }
        if (fToken == TT_LIST_OPEN) {
          getNextToken();
          if (fToken == TT_IDENTIFIER) {
            String identifier = getIdentifier();
            getNextToken();
            if (fToken == TT_LIST_CLOSE) {
              if (identifier.equals("array")) {
                return list;
              }
            }
          }
        }
      }
    }
    return F.Null;
  }

  public IExpr parseMatrixAsList(String typeOfMatrix, int lastIndex) {
    try {
      fPackageMode = true;
      IASTAppendable list = F.ListAlloc();
      IASTAppendable subList = F.ListAlloc();
      int lastTeXIndex = lastIndex;
      int endTeXIndex = -1;
      int texIndex = -1;
      int columns = -1;
      getNextToken();
      int lastToken = TT_EOF;
      while (fToken != TT_EOF) {
        int token = fToken;
        if (token == TT_AMPERSAND || token == TT_DOUBLE_BACKSLASH || token == TT_END) {
          if (token == TT_END) {
            endTeXIndex = fCurrentPosition - "\\end".length();
          } else {
            endTeXIndex = fCurrentPosition - 2;
          }
          texIndex = fCurrentPosition;
          getNextToken();

          String exprStr = new String(fInputString, lastTeXIndex, endTeXIndex - lastTeXIndex);
          lastTeXIndex = texIndex + 1;
          // IExpr expr = toExpr(exprStr);
          IExpr expr = TeXParser.convert(exprStr);
          subList.append(expr);
          if (token == TT_DOUBLE_BACKSLASH) {
            if (columns < 0) {
              columns = subList.argSize();
            }
            if (columns == subList.argSize()) {
              list.append(subList);
            }
            subList = F.ListAlloc();
          } else if (token == TT_END) {
            if (columns < 0) {
              columns = subList.argSize();
            }
            if (lastToken != TT_DOUBLE_BACKSLASH && columns == subList.argSize()) {
              list.append(subList);
            }
            subList = F.ListAlloc();
            if (fToken == TT_LIST_OPEN) {
              getNextToken();
              if (fToken == TT_IDENTIFIER) {
                String identifier = getIdentifier();
                getNextToken();
                if (fToken == TT_LIST_CLOSE) {
                  if (identifier.equals(typeOfMatrix)) {
                    return list;
                  }
                }
              }
            }
          }
          lastToken = token;
          continue;
        }
        getNextToken();
      }
    } finally {
      fPackageMode = false;
    }
    return F.Null;
  }

  public static IExpr convert(String texStr) {
    return new TeXParser().parse(texStr);
  }

  public IExpr parse(String texStr) {
    IExpr expression = parseTeXExpression(texStr);
    // IExpr expression = Lambda.replaceSlots(expression, x->.TeXSliceParser.class.);
    return expression.replaceAll(x -> {
      if (x.isSlot()) {
        int slot = x.first().toIntDefault();
        if (slot >= 1) {
          IExpr replacement = fMapOfVariables.get(slot);
          if (replacement != null) {
            return replacement;
          }
        }
      }
      return F.NIL;
    }).orElse(expression);

  }

  /**
   * Try to determine the nested slices of TeX <code>\\left</code> and <code>\\right</code>
   * expressions and of <code>\\begin{array}</code> and <code>\\end{array}</code> and create a new
   * input string for the modified SnuggleTeX parser.
   * 
   * @param texStr
   * @return
   */
  private IExpr parseTeXExpression(String texStr) {
    initialize(texStr);
    if (fToken == TT_EOF) {
      // empty expression string
      return S.Null;
    }
    StringBuilder ptBuf = new StringBuilder();
    int lastTeXIndex = 0;
    int endTeXIndex = -1;
    while (fToken != TT_EOF) {
      if (fToken == TT_BACKSLASH_SPACE) {
        endTeXIndex = fCurrentPosition - 2;
        ptBuf.append(texStr.substring(lastTeXIndex, endTeXIndex));
        lastTeXIndex = fCurrentPosition;
      } else if (fToken == TT_CHARACTER) {
        if (fCurrentChar == 0x2026) { // ellipsis
          endTeXIndex = fCurrentPosition - 1;
          ptBuf.append(texStr.substring(lastTeXIndex, endTeXIndex));
          lastTeXIndex = fCurrentPosition;
        } else if (fCurrentChar == 0x2032) { // derivative
          endTeXIndex = fCurrentPosition - 1;
          ptBuf.append(texStr.substring(lastTeXIndex, endTeXIndex));
          ptBuf.append("'");
          lastTeXIndex = fCurrentPosition;
        } else if (fCurrentChar == 0x2061) { // apply function
          endTeXIndex = fCurrentPosition - 1;
          ptBuf.append(texStr.substring(lastTeXIndex, endTeXIndex));
          lastTeXIndex = fCurrentPosition;
        } else if (fCurrentChar == 0x221E) { // infinity
          endTeXIndex = fCurrentPosition - 1;
          ptBuf.append(texStr.substring(lastTeXIndex, endTeXIndex));
          ptBuf.append(" \\infty ");
          lastTeXIndex = fCurrentPosition;
        } else if (fCurrentChar == 0x00B0) { // degree
          endTeXIndex = fCurrentPosition - 1;
          ptBuf.append(texStr.substring(lastTeXIndex, endTeXIndex));
          ptBuf.append(" \\degree ");
          lastTeXIndex = fCurrentPosition;
        }

      } else if (fToken == TT_PERCENT) {
        endTeXIndex = fCurrentPosition - 2;
        ptBuf.append(texStr.substring(lastTeXIndex, endTeXIndex));
        ptBuf.append(" \\frac{1}{100} ");
        lastTeXIndex = fCurrentPosition;
      } else if (fToken == TT_IDENTIFIER) {
        endTeXIndex = fCurrentPosition - 1;
        String identifier = getIdentifier();
        IExpr functionName = TeXSegmentParser.FUNCTION_HEADER_MAP.get(identifier);
        if (functionName != null) {
          IExpr head = functionName;// TeXSegmentParser.createFunction(functionName);
          getNextToken();
          int derivativeCounter = 0;
          while (fToken == TT_CHARACTER //
              && (fCurrentChar == 0x2032 || fCurrentChar == '\'')) { // derivative
            derivativeCounter++;
            getNextToken();
          }
          if (derivativeCounter > 0) {
            head = F.unaryAST1(F.Derivative(F.ZZ(derivativeCounter)), head);
          }
          if (fToken == TT_COMMAND) {
            if (fCommandString.equals("left")) {
              IExpr temp = convertLeftRight(texStr, lastTeXIndex);
              if (temp.isPresent()) {
                temp = F.unaryAST1(head, temp);
                int endOfSubExpr = fCurrentPosition - 1;
                // ptBuf.append(texStr.substring(lastTeXIndex, endTeXIndex));
                lastTeXIndex =
                    addSlotValue(temp, texStr, lastTeXIndex, endTeXIndex, endOfSubExpr + 1, ptBuf);
              } else {
                continue;
              }
            }
          } else {
            continue;
          }
        }
      } else if (fToken == TT_COMMAND) {
        if (fCommandString.equals("Huge")//
            || fCommandString.equals("huge") //
            || fCommandString.equals("LARGE") //
            || fCommandString.equals("Large") //
            || fCommandString.equals("large") //
            || fCommandString.equals("normalsize") //
            || fCommandString.equals("small") //
            || fCommandString.equals("footnotesize") //
            || fCommandString.equals("scriptsize") //
            || fCommandString.equals("tiny") //
            || fCommandString.equals("limits") //
            || fCommandString.equals("quad") // spacing
            || fCommandString.equals("qquad") // spacing
        ) {
          endTeXIndex = fCurrentPosition - fCommandString.length() - 1;
          ptBuf.append(texStr.substring(lastTeXIndex, endTeXIndex));
          lastTeXIndex = fCurrentPosition;
        } else if (fCommandString.equals("text")) {
          // create Symja string
          endTeXIndex = fCurrentPosition - fCommandString.length() - 1;
          getNextToken();
          if (fToken == TT_LIST_OPEN) {
            int startText = fCurrentPosition;
            int endOfSubExpr = indexOfToken(TT_LIST_OPEN, TT_LIST_CLOSE);
            if (endOfSubExpr < 0) {
              return S.Null;
            }
            String textString = texStr.substring(startText, endOfSubExpr);
            ptBuf.append(texStr.substring(lastTeXIndex, endTeXIndex));
            lastTeXIndex = addSlotValue(F.stringx(textString), texStr, lastTeXIndex, endTeXIndex,
                endOfSubExpr + 1, ptBuf);
          }
        } else if (fCommandString.equals("left")) {
          if (fCurrentChar == '.') {
            // ignore \left.
            endTeXIndex = fCurrentPosition - fCommandString.length() - 1;
            ptBuf.append(texStr.substring(lastTeXIndex, endTeXIndex));
            lastTeXIndex = fCurrentPosition + 1;
          } else {
            lastTeXIndex = parseLeftRight(texStr, ptBuf, lastTeXIndex);
          }
        } else if (fCommandString.equals("right")) {
          if (fCurrentChar == '.') {
            // ignore \right.
            endTeXIndex = fCurrentPosition - fCommandString.length() - 1;
            ptBuf.append(texStr.substring(lastTeXIndex, endTeXIndex));
            lastTeXIndex = fCurrentPosition + 1;
          }
        } else {
          // String functionName = FUNCTION_NAMES_MAP.get(fCommandString);
          IExpr functionName = TeXSegmentParser.FUNCTION_HEADER_MAP.get(fCommandString);
          if (functionName != null) {
            endTeXIndex = fCurrentPosition - fCommandString.length() - 1;
            getNextToken();
            if (fToken == TT_COMMAND) {
              if (fCommandString.equals("left")) {
                IExpr temp = convertLeftRight(texStr, lastTeXIndex);
                if (temp.isPresent()) {
                  IExpr head = functionName;// TeXSegmentParser.createFunction(functionName);
                  temp = F.unaryAST1(head, temp);
                  int endOfSubExpr = fCurrentPosition - 1;
                  // ptBuf.append(texStr.substring(lastTeXIndex, endTeXIndex));
                  lastTeXIndex = addSlotValue(temp, texStr, lastTeXIndex, endTeXIndex,
                      endOfSubExpr + 1, ptBuf);
                }
              }
            }
          }
        }
      } else if (fToken == TT_BEGIN) {
        endTeXIndex = fCurrentPosition - "\\begin".length();
        getNextToken();
        if (fToken == TT_LIST_OPEN) {
          getNextToken();
          if (fToken == TT_IDENTIFIER) {
            String identifier = getIdentifier();
            getNextToken();
            if (fToken == TT_LIST_CLOSE) {
              int startIndex = fCurrentPosition;
              getNextToken();
              String typeOfList = identifier;
              if (typeOfList.equals("array")) {
                IExpr temp = parseArrayAsList(startIndex);
                int endOfSubExpr = fCurrentPosition;
                lastTeXIndex =
                    addSlotValue(temp, texStr, lastTeXIndex, endTeXIndex, endOfSubExpr, ptBuf);
              } else if (typeOfList.equals("bmatrix") //
                  || typeOfList.equals("matrix") //
                  || typeOfList.equals("pmatrix")) {
                IExpr temp = parseMatrixAsList(typeOfList, startIndex);
                int endOfSubExpr = fCurrentPosition;
                lastTeXIndex =
                    addSlotValue(temp, texStr, lastTeXIndex, endTeXIndex, endOfSubExpr, ptBuf);
              }
            }
          }
        }
      }
      getNextToken();
    }
    // if (ptBuf.length() == 0) {
    // ptBuf.append(texStr);
    // } else
    if (lastTeXIndex < texStr.length()) {
      ptBuf.append(texStr.substring(lastTeXIndex));
    }
    IExpr result = parseMathExpr(ptBuf.toString());
    if (result == S.$Aborted) {
      return parseMathExpr(texStr);
    }
    return result;
  }

  private int parseLeftRight(String texStr, StringBuilder ptBuf, int lastTeXIndex) {
    boolean isAbs = fCurrentChar == '|';
    int endTeXIndex;
    endTeXIndex = fCurrentPosition - fCommandString.length() - 1;
    getNextToken();
    int startOfSubExpr = fCurrentPosition;
    int endOfSubExpr = indexOfCommand("left", "right");
    if (endOfSubExpr > 0) {
      getNextToken();
      String exprStr = new String(fInputString, startOfSubExpr, endOfSubExpr - startOfSubExpr);
      IExpr temp = TeXParser.convert(exprStr);
      if (temp.isSequence()) {
        temp = ((IAST) temp).setAtCopy(0, S.List);
      }
      if (!temp.isList() && isAbs) {
        temp = F.Abs(temp);
      }
      endOfSubExpr = fCurrentPosition;

      lastTeXIndex = addSlotValue(temp, texStr, lastTeXIndex, endTeXIndex, endOfSubExpr, ptBuf);
    }
    return lastTeXIndex;
  }

  private IExpr convertLeftRight(String texStr, int lastTeXIndex) {
    getNextToken();
    int startOfSubExpr = fCurrentPosition;
    getNextToken();
    int endOfSubExpr = indexOfCommand("left", "right");
    if (endOfSubExpr > 0) {
      getNextToken();
      String exprStr = new String(fInputString, startOfSubExpr, endOfSubExpr - startOfSubExpr);
      return TeXParser.convert(exprStr);
    }
    return F.NIL;
  }

  private int addSlotValue(IExpr expr, String texStr, int lastTeXIndex, int nextLastTeXIndex,
      int endOfSubExpr, StringBuilder ptBuf) {
    fMapOfVariables.put(fVariableCounter, expr);
    ptBuf.append(texStr.substring(lastTeXIndex, nextLastTeXIndex));
    ptBuf.append("{\\Slot{" + fVariableCounter + "}}");
    fVariableCounter++;
    return endOfSubExpr;
  }

  private IExpr parseMathExpr(String texStr) {
    StringBuilder buf = new StringBuilder();
    char ch;
    int i = 0;
    while (i < texStr.length()) {
      ch = texStr.charAt(i);
      if (ch == '\\' && i < texStr.length() - 1) {
        // command
        int commandStart = i;
        StringBuilder command = new StringBuilder();
        ch = texStr.charAt(++i);
        while (Character.isLetter(ch) && i < texStr.length() - 1) {
          command.append(ch);
          ch = texStr.charAt(++i);
        }
        String commandStr = command.toString();

        if (commandStr.equals("left")) {
          if (ch == '.') {
            // ignore command
            i++;
            continue;
          }
          if (ch == '|') {
            buf.append("| {"); // simulate opening Abs()
            i++;
            continue;
          }
        } else if (commandStr.equals("right")) {
          if (ch == '.') {
            // ignore command
            i++;
            continue;
          }
          if (ch == '|') {
            buf.append("} |"); // simulate closing Abs()
            i++;
            continue;
          }
        }
        if (i < texStr.length() - 1) {
          IExpr function = TeXSegmentParser.FUNCTION_HEADER_MAP_ARG1.get(commandStr);
          if (function != null) {
            // TODO make this for all one argument numeric builtin commands?
            boolean isVariable = false;
            while (ch == ' ' || TeXScanner.isTeXIdentifierPart(ch)) {
              if (ch != ' ') {
                isVariable = true;
              }
              if (i >= texStr.length() - 1) {
                break;
              }
              ch = texStr.charAt(++i);
            }
            if (isVariable) {
              buf.append("{");
              buf.append(texStr.substring(commandStart, i));
              buf.append("}");
              continue;
            }
          }
        }
        buf.append("\\" + command.toString());
      } else {
        buf.append(ch);
        i++;
      }
    }
    return toExpr(buf.toString());
  }

}
