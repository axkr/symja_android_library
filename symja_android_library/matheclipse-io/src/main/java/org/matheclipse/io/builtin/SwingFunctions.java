package org.matheclipse.io.builtin;

import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileSystemView;
import org.apache.commons.lang3.StringUtils;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.DialogReturnException;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.ParserConfig;
import org.matheclipse.parser.trie.SuggestTree;
import org.matheclipse.parser.trie.SuggestTree.Node;

public class SwingFunctions {

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      if (!Config.FUZZY_PARSER) {
        if (Config.FILESYSTEM_ENABLED) {
          S.Button.setEvaluator(new Button());
          S.DefaultButton.setEvaluator(new DefaultButton());
          S.Dynamic.setEvaluator(new Dynamic());
          S.CancelButton.setEvaluator(new CancelButton());
          S.DialogInput.setEvaluator(new DialogInput());
          S.DialogReturn.setEvaluator(new DialogReturn());
          S.Input.setEvaluator(new Input());
          S.InputString.setEvaluator(new InputString());
          S.SystemDialogInput.setEvaluator(new SystemDialogInput());
        }
      }
    }
  }

  private static class Button extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  private static class CancelButton extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  private static class DefaultButton extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  private static class Dynamic extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  private static final class InputString extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return inputString(ast, engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return IFunctionEvaluator.ARGS_0_1;
    }
  }

  private static final class SystemDialogInput extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (Desktop.isDesktopSupported() && ast.arg1().isString()) {
        String type = ast.arg1().toString().toLowerCase();
        if (type.equals("fileopen")) {
          JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
          j.setApproveButtonText("Open");
          // FileNameExtensionFilter restrict = new FileNameExtensionFilter("Only .txt files",
          // "txt");
          // j.addChoosableFileFilter(restrict);
          int r = j.showSaveDialog(null);
          if (r == JFileChooser.APPROVE_OPTION) {
            return F.stringx(j.getSelectedFile().getAbsolutePath());
          }

        } else if (type.equals("filesave")) {
          JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
          j.setApproveButtonText("Save");
          int r = j.showSaveDialog(null);
          if (r == JFileChooser.APPROVE_OPTION) {
            return F.stringx(j.getSelectedFile().getAbsolutePath());
          }
        } else if (type.equals("directory")) {
          JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
          j.setApproveButtonText("Select");
          j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
          int r = j.showSaveDialog(null);
          if (r == JFileChooser.APPROVE_OPTION) {
            return F.stringx(j.getSelectedFile().getAbsolutePath());
          }
        } else if (type.equals("color")) {
          Color color = JColorChooser.showDialog(null, "ColorChooser", null);
          if (color != null) {
            return F.stringx(Integer.toString(color.getRGB()));
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return IFunctionEvaluator.ARGS_1_2;
    }
  }

  private static final class Input extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        IExpr str = inputString(ast, engine);
        if (str.isPresent()) {
          return engine.evaluate(str.toString());
        }
      } catch (final RuntimeException e1) {
        //
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return IFunctionEvaluator.ARGS_0_1;
    }
  }

  private static IExpr inputString(final IAST ast, EvalEngine engine) {
    final BufferedReader in =
        new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
    try {
      if (ast.isAST1()) {
        engine.getOutPrintStream().print(ast.arg1().toString());
      }
      final String str = in.readLine();
      if (str != null) {
        return F.stringx(str);
      }
    } catch (final Exception e1) {
      //
    }
    return F.NIL;
  }

  private static final class DialogInput extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (Desktop.isDesktopSupported()) {
        IAST dialogNoteBook = null;
        if (ast.isAST2() && ast.arg2().isAST(S.DialogNotebook, 2)) {
          dialogNoteBook = (IAST) ast.arg2();
        } else if (ast.isAST1() && ast.arg1().isAST(S.DialogNotebook, 2)) {
          dialogNoteBook = (IAST) ast.arg1();
        }

        IAST list;
        if (dialogNoteBook == null) {
          if (ast.isAST1()) {
            list = ast.arg1().orNewList();
          } else {
            return F.NIL;
          }
        } else {
          list = dialogNoteBook.arg1().orNewList();
        }

        JDialog dialog = new JDialog();
        dialog.setTitle("DialogInput");
        dialog.setSize(320, 200);
        dialog.setModal(true);
        // dialog.setLayout(new FlowLayout(FlowLayout.LEFT));
        // dialog.setLayout(new GridLayout(list.argSize(), 1));
        IExpr[] result = new IExpr[] {F.NIL};
        if (addComponents(dialog, list, dialog, engine, result)) {
          dialog.setVisible(true);
          if (result[0].isPresent()) {
            return result[0];
          }
        }
      }
      return F.NIL;
    }

    private static boolean addComponents(Container container, IAST list, JDialog dialog,
        EvalEngine engine, IExpr result[]) {
      final Consumer<IExpr> consumer = x -> {
        try {
          engine.evaluate(x);
        } catch (DialogReturnException rex) {
          result[0] = rex.getValue();
          dialog.dispose();
        } catch (RuntimeException rex) {
          //
        }
      };
      for (int i = 1; i < list.size(); i++) {
        IExpr arg = list.get(i);
        int headID = list.get(i).headID();
        if (headID > 0) {
          switch (headID) {
            case ID.Button:
              if (arg.size() == 3) {
                final String buttonLabel = arg.first().toString();
                createButton(dialog, container, buttonLabel, arg.second(), consumer, result,
                    engine);
              } else {
                return false;
              }
              continue;
            case ID.CancelButton:
              String cancelLabel = "Cancel";
              final IExpr cancelAction;
              if (arg.size() == 1) {
                cancelAction = F.DialogReturn(S.$Cancel);
              } else if (arg.size() == 2) {
                cancelAction = F.DialogReturn(arg.first());
              } else if (arg.size() == 3) {
                cancelLabel = arg.first().toString();
                cancelAction = F.DialogReturn(arg.second());
              } else {
                return false;
              }
              createButton(dialog, container, cancelLabel, cancelAction, consumer, result, engine);
              dialog.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                  .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "CANCEL");
              dialog.getRootPane().getActionMap().put("CANCEL", new AbstractAction() {
                private static final long serialVersionUID = 1293680722774511195L;

                @Override
                public void actionPerformed(ActionEvent e) {
                  consumer.accept(cancelAction);
                }
              });
              continue;
            case ID.DefaultButton:
              String buttonLabel = "Ok";
              IExpr buttonAction = F.DialogReturn();
              if (arg.size() == 1) {
              } else if (arg.size() == 2) {
                buttonAction = F.DialogReturn(arg.first());
              } else if (arg.size() == 3) {
                buttonLabel = arg.first().toString();
                buttonAction = F.DialogReturn(arg.second());
              } else {
                return false;
              }
              JButton db = createButton(dialog, container, buttonLabel, buttonAction, consumer,
                  result, engine);
              dialog.getRootPane().setDefaultButton(db);
              continue;
            case ID.Column:
              if (arg.size() == 2) {
                IAST column = arg.first().orNewList();
                JPanel columnPanel = new JPanel();
                columnPanel.setLayout(new GridLayout(column.argSize(), 1));
                container.add(columnPanel);
                if (!addComponents(columnPanel, column, dialog, engine, result)) {
                  return false;
                }
              } else {
                return false;
              }
              continue;
            case ID.InputField:
              IExpr input = S.Null;
              int inputType = ID.String;
              if (arg.size() == 1) {
              } else if (arg.size() == 2) {
                input = arg.first();
              } else if (arg.size() == 3) {
                input = arg.first();
                if (arg.second().isBuiltInSymbol()) {
                  inputType = ((IBuiltInSymbol) arg.second()).ordinal();
                }
              } else {
                return false;
              }
              createInputField(dialog, container, input, inputType, result, engine);
              continue;
            case ID.Row:
              if (arg.size() == 2) {
                IAST row = arg.first().orNewList();
                JPanel rowPanel = new JPanel();
                rowPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
                container.add(rowPanel);
                if (!addComponents(rowPanel, row, dialog, engine, result)) {
                  return false;
                }
              } else {
                return false;
              }
              continue;
            case ID.TextCell:
              if (arg.size() == 2) {
                JLabel label = new JLabel(arg.first().toString());
                container.add(label);
              } else {
                return false;
              }
              continue;
            default:
          }
        }
        // fallback
        JLabel label = new JLabel(arg.toString());
        container.add(label);
      }
      return true;
    }

    private static JButton createButton(JDialog dialog, Container container, String label,
        IExpr action, final Consumer<IExpr> consumer, IExpr[] result, EvalEngine engine) {
      JButton button = new JButton(label);
      container.add(button);
      button.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          consumer.accept(action);
        }
      });
      return button;
    }

    private static class MyDocumentListener implements DocumentListener {
      JTextField inputField;
      ISymbol dynamic;
      int headID;

      public MyDocumentListener(JTextField inputField, ISymbol dynamic, int headID) {
        if (dynamic != null && //
            (!dynamic.isVariable() || dynamic.isBuiltInSymbol())) {
          // Cannot assign to raw object `1`.
          throw new ArgumentTypeException(
              SwingFunctions.getMessage("setraw", F.List(dynamic), EvalEngine.get()));
        }
        this.inputField = inputField;
        this.dynamic = dynamic;
        this.headID = headID;
      }

      @Override
      public void insertUpdate(DocumentEvent e) {
        updateFieldState();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        updateFieldState();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        updateFieldState();
      }

      protected void updateFieldState() {
        if (dynamic != null) {
          String text = inputField.getText();
          IExpr expr = F.NIL;
          if (headID == ID.String) {
            expr = F.stringx(text);
          } else if (headID == ID.Expression) {
            expr = F.eval(F.ToExpression(F.stringx(text)));
          } else if (headID == ID.Number) {
            expr = F.eval(F.ToExpression(F.stringx(text)));
            if (!expr.isNumber()) {
              expr = F.NIL;
            }
          }
          if (expr.isPresent()) {
            dynamic.assignValue(expr, false);
          }
        }
      }
    }

    private static void createInputField(JDialog dialog, Container container, final IExpr action,
        int headID, IExpr[] result, EvalEngine engine) {
      String defaultInput = action.toString();
      ISymbol dynamic = null;

      if (action == S.Null) {
        defaultInput = "";
      } else if (action.isAST(S.Dynamic, 2) && action.first().isSymbol()
          && !action.first().isBuiltInSymbol()) {
        dynamic = (ISymbol) action.first();
        defaultInput = dynamic.toString();
      }
      JTextField inputField = new JTextField(defaultInput, 10);
      container.add(inputField);

      MyDocumentListener dl = new MyDocumentListener(inputField, dynamic, headID);
      inputField.getDocument().addDocumentListener(dl);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return IFunctionEvaluator.ARGS_1_2;
    }
  }

  private static final class DialogReturn extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        IExpr arg1 = ast.arg1();
        if (arg1.isFalse()) {
          throw DialogReturnException.DIALOG_RETURN_FALSE;
        }
        if (arg1.isTrue()) {
          throw DialogReturnException.DIALOG_RETURN_TRUE;
        }
        arg1 = engine.evaluate(arg1);
        if (arg1.isFalse()) {
          throw DialogReturnException.DIALOG_RETURN_FALSE;
        }
        if (arg1.isTrue()) {
          throw DialogReturnException.DIALOG_RETURN_TRUE;
        }
        throw new DialogReturnException(arg1);
      }
      throw new DialogReturnException();
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return IFunctionEvaluator.ARGS_0_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  public static void initialize() {
    Initializer.init();
  }

  public static IExpr message(ISymbol symbol, String messageShortcut, final IAST list) {
    IExpr temp = symbol.evalMessage(messageShortcut);
    String message = null;
    if (temp.isPresent()) {
      message = temp.toString();
    } else {
      temp = S.General.evalMessage(messageShortcut);
      if (temp.isPresent()) {
        message = temp.toString();
      }
    }
    if (message != null) {
      message = rawMessage(list, message);
      return F.stringx(symbol.toString() + ": " + message);
    }
    return F.NIL;
  }

  public static String getMessage(String messageShortcut, final IAST listOfArgs) {
    return getMessage(messageShortcut, listOfArgs, EvalEngine.get());
  }

  public static String getMessage(String messageShortcut, final IAST listOfArgs,
      EvalEngine engine) {
    IExpr temp = S.General.evalMessage(messageShortcut);
    String message = null;
    if (temp.isPresent()) {
      message = temp.toString();
    }
    if (message == null) {
      message = "Undefined message shortcut: " + messageShortcut;
      engine.setMessageShortcut(messageShortcut);
      return message;
    }
    for (int i = 1; i < listOfArgs.size(); i++) {
      message = StringUtils.replace(message, "`" + (i) + "`", shorten(listOfArgs.get(i)));
    }
    engine.setMessageShortcut(messageShortcut);
    return message;
  }

  private static String rawMessage(final IAST list, String message) {
    for (int i = 2; i < list.size(); i++) {
      message = StringUtils.replace(message, "`" + (i - 1) + "`", shorten(list.get(i)));
    }
    return message;
  }

  /**
   * Shorten the output string generated from <code>expr</code> to a maximum length of <code>80
   * </code> characters. Print <<SHORT>> as substitute of the middle of the expression if necessary.
   *
   * @param expr
   * @return
   */
  public static String shorten(IExpr expr) {
    return shorten(expr, 80);
  }

  /**
   * Shorten the output string generated from <code>expr</code> to a maximum length of <code>
   * maximuLength</code> characters. Print <<SHORT>> as substitute of the middle of the expression
   * if necessary.
   *
   * @param expr
   * @param maximuLength the maximum length of the result string.
   * @return
   */
  public static String shorten(IExpr expr, int maximuLength) {
    String str = expr.toString();
    if (str.length() > maximuLength) {
      StringBuilder buf = new StringBuilder(maximuLength);
      int halfLength = (maximuLength / 2) - 14;
      buf.append(str.substring(0, halfLength));
      buf.append("<<SHORT>>");
      buf.append(str.substring(str.length() - halfLength));
      return buf.toString();
    }
    return str;
  }

  public static IAST getNamesByPrefix(String name) {

    if (name.length() == 0) {
      return F.List();
    }
    boolean exact = true;
    if (name.charAt(name.length() - 1) == '*') {
      name = name.substring(0, name.length() - 1);
      if (name.length() == 0) {
        return getAllNames();
      }
      exact = false;
    }
    SuggestTree suggestTree = AST2Expr.getSuggestTree();
    name = ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS ? name.toLowerCase() : name;
    Node n = suggestTree.getAutocompleteSuggestions(name);
    if (n != null) {
      IASTAppendable list = F.ListAlloc(n.listLength());
      for (int i = 0; i < n.listLength(); i++) {
        if (exact) {
          if (name.equals(n.getSuggestion(i).getTerm())) {
            list.append(F.$s(n.getSuggestion(i).getTerm()));
          }
        } else {
          list.append(F.$s(n.getSuggestion(i).getTerm()));
        }
      }
      return list;
    }
    return F.List();
  }

  public static List<String> getAutoCompletionList(String namePrefix) {
    List<String> list = new ArrayList<String>();
    if (namePrefix.length() == 0) {
      return list;
    }
    SuggestTree suggestTree = AST2Expr.getSuggestTree();
    namePrefix = ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS ? namePrefix.toLowerCase() : namePrefix;
    Node n = suggestTree.getAutocompleteSuggestions(namePrefix);
    if (n != null) {
      for (int i = 0; i < n.listLength(); i++) {
        list.add(n.getSuggestion(i).getTerm());
      }
    }
    return list;
  }

  public static IAST getAllNames() {
    int size = AST2Expr.FUNCTION_STRINGS.length;
    IASTAppendable list = F.ListAlloc(size);
    return list.appendArgs(0, size, i -> F.$s(AST2Expr.FUNCTION_STRINGS[i]));
  }

  private SwingFunctions() {}
}
