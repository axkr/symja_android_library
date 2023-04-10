package org.matheclipse.core.form.tex;

import java.util.HashMap;
import java.util.Map;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class TeXSliceParser extends TeXScanner {

  public static String[] FUNCTION_NAMES = new String[] {"f", "g", "h"};

  public static String[] FUNCTION_NAME_MAP = new String[] {//
      "sin", "Sin", //
      "cos", "Cos", //
      "cot", "Cot", //
      "csc", "Csc", //
      "ln", "Log", //
      "log", "Log10", //
      "sec", "Sec", //
      "tan", "Tan"};

  public static Map<String, String> FUNCTION_NAMES_MAP = new HashMap<String, String>();

  protected EvalEngine fEngine;

  // protected IParserFactory fFactory;

  Map<Integer, IExpr> fMapOfVariables = new HashMap<Integer, IExpr>();



  static {
    for (int i = 0; i < FUNCTION_NAMES.length; i++) {
      FUNCTION_NAMES_MAP.put(FUNCTION_NAMES[i], FUNCTION_NAMES[i]);
    }
    int i = 0;
    while (i < FUNCTION_NAME_MAP.length) {
      FUNCTION_NAMES_MAP.put(FUNCTION_NAME_MAP[i], FUNCTION_NAME_MAP[i + 1]);
      i = i + 2;
    }
  }

  int fVariableCounter = 1;

  public TeXSliceParser() {
    super(false, false);
    // this.fFactory = ExprParserFactory.MMA_STYLE_FACTORY;
    this.fEngine = EvalEngine.get();
  }

  private IExpr toExpr(String texStr) {
    TeXParser texParser = new TeXParser(fEngine);
    return texParser.toExpression(texStr);
  }

  public IExpr parseArrayAsList(int lastIndex) {
    IASTAppendable list = F.ListAlloc();
    IASTAppendable subList = F.ListAlloc();
    int numberOfColumns = 0;
    if (fToken == TT_LIST_OPEN) {
      getNextToken();
      if (fToken == TT_IDENTIFIER) {
        String identifier = getIdentifier();
        getNextToken();
        if (fToken == TT_LIST_CLOSE) {
          numberOfColumns = identifier.length();
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
        IExpr expr = TeXSliceParser.convert(exprStr);
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
          IExpr expr = TeXSliceParser.convert(exprStr);
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
    return new TeXSliceParser().parse(texStr);
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
        if (fCurrentChar == 0x2061) { // apply function
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
      } else if (fToken == TT_IDENTIFIER) {
        endTeXIndex = fCurrentPosition - 1;
        String identifier = getIdentifier();
        String functionName = FUNCTION_NAMES_MAP.get(identifier);
        if (functionName != null) {
          getNextToken();
          if (fToken == TT_COMMAND) {
            if (fCommandString.equals("left")) {
              IExpr temp = convertLeftRight(texStr, lastTeXIndex);
              if (temp.isPresent()) {
                ISymbol head = TeXParser.createFunction(functionName);
                temp = F.unaryAST1(head, temp);
                int endOfSubExpr = fCurrentPosition - 1;
                // ptBuf.append(texStr.substring(lastTeXIndex, endTeXIndex));
                lastTeXIndex =
                    addSlotValue(temp, texStr, lastTeXIndex, endTeXIndex, endOfSubExpr + 1, ptBuf);
              }
            }
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
          lastTeXIndex = parseLeftRight(texStr, ptBuf, lastTeXIndex);
        } else {
          String functionName = FUNCTION_NAMES_MAP.get(fCommandString);
          if (functionName != null) {
            endTeXIndex = fCurrentPosition - fCommandString.length() - 1;
            getNextToken();
            if (fToken == TT_COMMAND) {
              if (fCommandString.equals("left")) {
                IExpr temp = convertLeftRight(texStr, lastTeXIndex);
                if (temp.isPresent()) {
                  ISymbol head = TeXParser.createFunction(functionName);
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
    int endTeXIndex;
    endTeXIndex = fCurrentPosition - fCommandString.length() - 1;
    getNextToken();
    int startOfSubExpr = fCurrentPosition;
    getNextToken();
    int endOfSubExpr = indexOfCommand("left", "right");
    if (endOfSubExpr > 0) {
      getNextToken();
      String exprStr = new String(fInputString, startOfSubExpr, endOfSubExpr - startOfSubExpr);
      IExpr temp = TeXSliceParser.convert(exprStr);
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
      return TeXSliceParser.convert(exprStr);
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
    int curlyBracesLevel = 0;
    StringBuilder eqCommand = null;
    while (i < texStr.length()) {
      ch = texStr.charAt(i);
      if (ch == '{') {
        curlyBracesLevel++;
      } else if (ch == '}') {
        curlyBracesLevel--;
      }
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
          if (commandStr.equals("log") && texStr.charAt(i) == '_') {
            StringBuilder number = new StringBuilder();
            ch = texStr.charAt(++i);
            int numberStart = i - 1;
            while (Character.isDigit(ch)) {
              number.append(ch);
              if (i >= texStr.length() - 1) {
                break;
              }
              ch = texStr.charAt(++i);
            }
            if (number.length() > 1) {
              String numStr = number.toString();
              buf.append(texStr.substring(commandStart, numberStart + 1));
              buf.append(numStr.charAt(0));
              buf.append("{");
              buf.append(numStr.substring(1));
              buf.append("}");
            } else {
              buf.append(texStr.substring(commandStart, i));
            }
            continue;
          } else if (commandStr.equals("sin") || commandStr.equals("cos")) {
            // TODO make this for all numeric builtin commands?
            StringBuilder variable = new StringBuilder();
            boolean isVariable = false;
            while (ch == ' ' || Character.isJavaIdentifierPart(ch)) {
              if (ch != ' ') {
                isVariable = true;
              }
              variable.append(ch);
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
          if (commandStr.equals("operatorname")) {
            // getCommand()
          }
        }
        buf.append("\\" + command.toString());
      } else if (curlyBracesLevel == 0 && ch == '=' && i < texStr.length() - 1) {
        int indexOf = texStr.indexOf('=', i + 1);
        if (indexOf < 0 && eqCommand == null) {
          eqCommand = new StringBuilder();
          eqCommand.append("{");
          eqCommand.append(buf.toString());
          eqCommand.append("} = ");
          eqCommand.append("{");
          buf = eqCommand;
        }
        i++;
      } else {
        buf.append(ch);
        i++;
      }
    }
    if (eqCommand != null) {
      buf.append("}");
    }
    return toExpr(buf.toString());
  }

  // protected final List<Operator> getOperator() {
  // char lastChar = fCurrentChar;
  // final int startPosition = fCurrentPosition - 1;
  // fOperatorString = new String(fInputString, startPosition, fCurrentPosition - startPosition);
  // List<Operator> list = fFactory.getOperatorList(fOperatorString);
  // List<Operator> lastList = null;
  // int lastOperatorPosition = -1;
  // if (list != null) {
  // lastList = list;
  // lastOperatorPosition = fCurrentPosition;
  // }
  // getChar();
  // while (fFactory.isOperatorChar(fCurrentChar)) {
  // if (fCurrentChar == '.' && isValidPosition() && Character.isDigit(charAtPosition())) {
  // // special case "dot is start of floating number" -- 1/.2 => 0.5
  // break;
  // }
  // lastChar = fCurrentChar;
  // fOperatorString = new String(fInputString, startPosition, fCurrentPosition - startPosition);
  // list = fFactory.getOperatorList(fOperatorString);
  // if (list != null) {
  // lastList = list;
  // lastOperatorPosition = fCurrentPosition;
  // }
  // getChar();
  // if (lastChar == ';' && fCurrentChar != ';') {
  // break;
  // }
  // }
  // if (lastOperatorPosition > 0) {
  // fCurrentPosition = lastOperatorPosition;
  // return lastList;
  // }
  // final int endPosition = fCurrentPosition--;
  // fCurrentPosition = startPosition;
  // throwSyntaxError("Operator token not found: "
  // + new String(fInputString, startPosition, endPosition - 1 - startPosition));
  // return null;
  // }

  // protected boolean isOperatorCharacters() {
  // return fFactory.isOperatorChar(fCurrentChar);
  // }

  // protected boolean isOperatorCharacters(char ch) {
  // return fFactory.isOperatorChar(ch);
  // }

}
