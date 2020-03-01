package org.matheclipse.core.builtin;

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
import org.matheclipse.core.eval.exception.DialogReturnException;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.trie.SuggestTree;
import org.matheclipse.core.trie.SuggestTree.Node;

public class IOFunctions {

	/**
	 * 
	 * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation in static
	 * initializer</a>
	 */
	private static class Initializer {

		private static void init() {
			if (Config.FILESYSTEM_ENABLED) {
				F.Button.setEvaluator(new Button());
				F.DefaultButton.setEvaluator(new DefaultButton());
				F.Dynamic.setEvaluator(new Dynamic());
				F.CancelButton.setEvaluator(new CancelButton());
				F.DialogInput.setEvaluator(new DialogInput());
				F.DialogReturn.setEvaluator(new DialogReturn());
				F.Input.setEvaluator(new Input());
				F.InputString.setEvaluator(new InputString());
				F.SystemDialogInput.setEvaluator(new SystemDialogInput());
			}
			// F.General.setEvaluator(new General());
			F.Message.setEvaluator(new Message());
			F.Names.setEvaluator(new Names());
			for (int i = 0; i < MESSAGES.length; i += 2) {
				F.General.putMessage(IPatternMatcher.SET, MESSAGES[i], F.stringx(MESSAGES[i + 1]));
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

	private static class Message extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() > 1) {
				if (ast.arg1().isString()) {
					String message = ast.arg1().toString();
					for (int i = 2; i < ast.size(); i++) {
						message = message.replaceAll("`" + (i - 1) + "`", ast.get(i).toString());
					}
					return F.stringx(": " + message);
				}
				if (ast.arg1().isAST(F.MessageName, 3)) {
					IAST messageName = (IAST) ast.arg1();
					String messageShortcut = messageName.arg2().toString();
					if (messageName.arg1().isSymbol()) {
						IExpr temp = message((ISymbol) messageName.arg1(), messageShortcut, ast);
						if (temp.isPresent()) {
							return temp;
						}
					}
					return message(F.General, messageShortcut, ast);
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDFIRST);
		}

	}

	private final static class InputString extends AbstractFunctionEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			return inputString(ast, engine);
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_0_1;
		}
	}

	private final static class SystemDialogInput extends AbstractFunctionEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (Desktop.isDesktopSupported() && ast.arg1().isString()) {
				String type = ast.arg1().toString().toLowerCase();
				if (type.equals("fileopen")) {
					JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
					j.setApproveButtonText("Open");
					// FileNameExtensionFilter restrict = new FileNameExtensionFilter("Only .txt files", "txt");
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

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_2;
		}
	}

	private final static class Input extends AbstractFunctionEvaluator {
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

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_0_1;
		}
	}

	private static IExpr inputString(final IAST ast, EvalEngine engine) {
		final BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
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

	private final static class DialogInput extends AbstractFunctionEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (Desktop.isDesktopSupported()) {
				IAST dialogNoteBook = null;
				if (ast.isAST2() && ast.arg2().isAST(F.DialogNotebook, 2)) {
					dialogNoteBook = (IAST) ast.arg2();
				} else if (ast.isAST1() && ast.arg1().isAST(F.DialogNotebook, 2)) {
					dialogNoteBook = (IAST) ast.arg1();
				}

				IAST list;
				if (dialogNoteBook == null) {
					if (ast.isAST1()) {
						if (ast.arg1().isList()) {
							list = (IAST) ast.arg1();
						} else {
							list = F.List(ast.arg1());
						}
					} else {
						return F.NIL;
					}
				} else {
					if (dialogNoteBook.arg1().isList()) {
						list = (IAST) dialogNoteBook.arg1();
					} else {
						list = F.List(dialogNoteBook.arg1());
					}
				}

				JDialog dialog = new JDialog();
				dialog.setTitle("DialogInput");
				dialog.setSize(320, 200);
				dialog.setModal(true);
				// dialog.setLayout(new FlowLayout(FlowLayout.LEFT));
				// dialog.setLayout(new GridLayout(list.argSize(), 1));
				IExpr[] result = new IExpr[] { F.NIL };
				if (addComponents(dialog, list, dialog, engine, result)) {
					dialog.setVisible(true);
					if (result[0].isPresent()) {
						return result[0];
					}
				}
			}
			return F.NIL;
		}

		private static boolean addComponents(Container container, IAST list, JDialog dialog, EvalEngine engine,
				IExpr result[]) {
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
							createButton(dialog, container, buttonLabel, arg.second(), consumer, result, engine);
						} else {
							return false;
						}
						continue;
					case ID.CancelButton:
						String cancelLabel = "Cancel";
						final IExpr cancelAction;
						if (arg.size() == 1) {
							cancelAction = F.DialogReturn(F.$Cancel);
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
						JButton db = createButton(dialog, container, buttonLabel, buttonAction, consumer, result,
								engine);
						dialog.getRootPane().setDefaultButton(db);
						continue;
					case ID.Column:
						if (arg.size() == 2) {
							IAST column;
							if (arg.first().isList()) {
								column = (IAST) arg.first();
							} else {
								column = F.List(arg.first());
							}
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
						IExpr input = F.Null;
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
							IAST row;
							if (arg.first().isList()) {
								row = (IAST) arg.first();
							} else {
								row = F.List(arg.first());
							}
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

		private static JButton createButton(JDialog dialog, Container container, String label, IExpr action,
				final Consumer<IExpr> consumer, IExpr[] result, EvalEngine engine) {
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
				String text = inputField.getText();
				if (dynamic != null) {
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
						dynamic.assign(expr);
					}
					// System.out.println(F.eval(dynamic).toString());
				}
			}
		};

		private static void createInputField(JDialog dialog, Container container, final IExpr action, int headID,
				IExpr[] result, EvalEngine engine) {
			String defaultInput = action.toString();
			ISymbol dynamic = null;

			if (action == F.Null) {
				defaultInput = "";
			} else if (action.isAST(F.Dynamic, 2) && action.first().isSymbol() && !action.first().isBuiltInSymbol()) {
				dynamic = (ISymbol) action.first();
				defaultInput = dynamic.toString();
			}
			JTextField inputField = new JTextField(defaultInput, 10);
			container.add(inputField);

			MyDocumentListener dl = new MyDocumentListener(inputField, dynamic, headID);
			inputField.getDocument().addDocumentListener(dl);
			// inputField.addActionListener(new ActionListener() {
			// @Override
			// public void actionPerformed(ActionEvent e) {
			// try {
			// System.out.println(inputField.getText());
			// } catch (DialogReturnException rex) {
			// result[0] = rex.getValue();
			// dialog.dispose();
			// } catch (RuntimeException rex) {
			// //
			// }
			// }
			// });
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_2;
		}
	}

	private final static class DialogReturn extends AbstractCoreFunctionEvaluator {

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

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_0_1;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	private final static class Names extends AbstractFunctionEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST0()) {
				return getAllNames();
			}

			if (ast.arg1() instanceof IStringX) {
				return getNamesByPrefix(ast.arg1().toString());
			}
			return F.NIL;
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_0_1;
		}

	}

	public static final int[] ARGS_0_0 = new int[] { 0, 0 };

	public static final int[] ARGS_0_1 = new int[] { 0, 1 };

	public static final int[] ARGS_0_2 = new int[] { 0, 2 };

	public static final int[] ARGS_1_1 = new int[] { 1, 1 };

	public static final int[] ARGS_1_2 = new int[] { 1, 2 };

	public static final int[] ARGS_1_5 = new int[] { 1, 5 };

	public static final int[] ARGS_2_2 = new int[] { 2, 2 };

	public static final int[] ARGS_1_3 = new int[] { 1, 3 };

	public static final int[] ARGS_1_4 = new int[] { 1, 4 };

	public static final int[] ARGS_2_3 = new int[] { 2, 3 };

	public static final int[] ARGS_2_4 = new int[] { 2, 4 };

	public static final int[] ARGS_3_3 = new int[] { 3, 3 };

	public static final int[] ARGS_3_4 = new int[] { 3, 4 };

	public static final int[] ARGS_4_4 = new int[] { 4, 4 };

	public static final int[] ARGS_5_5 = new int[] { 5, 5 };

	public static final int[] ARGS_1_INFINITY = new int[] { 1, Integer.MAX_VALUE };

	public static final int[] ARGS_2_INFINITY = new int[] { 2, Integer.MAX_VALUE };

	public static final int[] ARGS_3_INFINITY = new int[] { 3, Integer.MAX_VALUE };

	private final static String[] MESSAGES = { //
			"argillegal", "illegal arguments: \"`1`\" in `2`", //
			"argb", "`1` called with `2` arguments; between `3` and `4` arguments are expected.", //
			"argct", "`1` called with `2` arguments.", //
			"argctu", "`1` called with 1 argument.", //
			"argr", "`1` called with 1 argument; `2` arguments are expected.", //
			"argrx", "`1` called with `2` arguments; `3` arguments are expected.", //
			"argx", "`1` called with `2` arguments; 1 argument is expected.", //
			"argt", "`1` called with `2` arguments; `3` or `4` arguments are expected.", //
			"argtu", "`1` called with 1 argument; `2` or `3` arguments are expected.", //
			"base", "Requested base `1` in `2` should be between 2 and `3`.", //
			"boxfmt", "`1` is not a box formatting type.", //
			"color", "`1` is not a valid color or gray-level specification.", //
			"compat", "`1` and `2` are incompatible units", //
			"cxt", "`1` is not a valid context name.", //
			"divz", "The argument `1` should be nonzero.", //
			"digit", "Digit at position `1` in `2` is too large to be used in base `3`.", //
			"eqf", "`1` is not a well-formed equation.", //
			"exact", "Argument `1` is not an exact number.", //
			"fftl", "Argument `1` is not a non-empty list or rectangular array of numeric quantities.", //
			"fpct", "To many parameters in `1` to be filled from `2`.", //
			"fnsym", "First argument in `1` is not a symbol or a string naming a symbol.", //
			"heads", "Heads `1` and `2` are expected to be the same.", //
			"ilsnn", "Single or list of non-negative integers expected at position `1`.", //
			"indet", "Indeterminate expression `1` encountered.", //
			"infy", "Infinite expression `1` encountered.", //
			"innf", "Non-negative integer or Infinity expected at position `1`.", //
			"int", "Integer expected.", //
			"intjava", "Java int value greater equal `1` expected instead of `2`.", //
			"intlevel", "Level specification value greater equal `1` expected instead of `2`.", //
			"intp", "Positive integer expected.", //
			"intnn", "Non-negative integer expected.", //
			"intnm", "Non-negative machine-sized integer expected at position `2` in `1`.", //
			"intpm", "Positive integer (less equal 2147483647) expected at position `2` in `1`.", //
			"iterb", "Iterator does not have appropriate bounds.", //
			"itform", "Argument `1` at position `2` does not have the correct form for an iterator.", //
			"ivar", "`1` is not a valid variable.", //
			"level", "Level specification `1` is not of the form n, {n}, or {m, n}.", //
			"list", "List expected at position `1` in `2`.", //
			"listofbigints", "List of Java BigInteger numbers expected in `1`.", //
			"listofints", "List of Java int numbers expected in `1`.", //
			"listoflongs", "List of Java long numbers expected in `1`.", //
			"locked", "Symbol `1` is locked.", //
			"matsq", "Argument `1` is not a non-empty square matrix.", //
			"noopen", "Cannot open `1`.", //
			"nonopt",
			"Options expected (instead of `1`) beyond position `2` in `3`. An option must be a rule or a list of rules.", //
			"nord", "Invalid comparison with `1` attempted.", //
			"normal", "Nonatomic expression expected at position `1` in `2`.",
			"nquan", "The Quantile specification `1` should be a number between `2` and `3`.",
			"nvld", "The expression `1` is not a valid interval.", //
			"notunicode",
			"A character unicode, which should be a non-negative integer less than 1114112, is expected at position `2` in `1`.", //
			"noval", "Symbol `1` in part assignment does not have an immediate value.", //
			"noval", "Symbol `1` in part assignment does not have an immediate value.", //
			"openx", "`1` is not open.", //
			"optb", "Optional object `1` in `2` is not a single blank.", //
			"ovfl", "Overflow occurred in computation.", //
			"partd", "Part specification `1` is longer than depth of object.", //
			"partw", "Part `1` of `2` does not exist.", //
			"plld", "Endpoints in `1` must be distinct machine-size real numbers.", //
			"plln", "Limiting value `1` in `2` is not a machine-size real number.", //
			"pspec", "Part specification `1` is neither an integer nor a list of integer.", //
			"poly", "`1` is not a polynomial.", //
			"polynomial", "Polynomial expected at position `1` in `2`.", //
			"pkspec1", "The expression `1` cannot be used as a part specification.", // "
			"rvalue", "`1` is not a variable with a value, so its value cannot be changed.", //
			"seqs", "Sequence specification expected, but got `1`.", //
			"setp", "Part assignment to `1` could not be made", //
			"setraw", "Cannot assign to raw object `1`.", //
			"setps", "`1` in the part assignment is not a symbol.", //
			"span", "`1` is not a valid Span specification.", //
			"stream", "`1` is not string, InputStream[], or OutputStream[]", //
			"string", "String expected at position `1` in `2`.", //
			"sym", "Argument `1` at position `2` is expected to be a symbol.", //
			"tag", "Rule for `1` can only be attached to `2`.", //
			"take", "Cannot take positions `1` through `2` in `3`.", //
			"usraw", "Cannot unset object `1`.", //
			"vpow2", "Argument `1` is restricted to vectors with a length of power of 2.", //
			"vrule", "Cannot set `1` to `2`, which is not a valid list of replacement rules.", //
			"write", "Tag `1` in `2` is Protected.", //
			"wrsym", "Symbol `1` is Protected.", //
			"ucdec", "An invalid unicode sequence was encountered and ignored." //
	};

	public static void initialize() {
		Initializer.init();
	}

	public static IExpr message(ISymbol symbol, String messageShortcut, final IAST ast) {
		IExpr temp = symbol.evalMessage(messageShortcut);
		String message = null;
		if (temp.isPresent()) {
			message = temp.toString();
		} else {
			temp = F.General.evalMessage(messageShortcut);
			if (temp.isPresent()) {
				message = temp.toString();
			}
		}
		if (message != null) {
			message = rawMessage(ast, message);
			return F.stringx(symbol.toString() + ": " + message);
		}
		return F.NIL;
	}

	public static IExpr printArgMessage(IAST ast, int[] expected, EvalEngine engine) {
		final ISymbol topHead = ast.topHead();
		int argSize = ast.argSize();
		if (expected[0] == expected[1]) {
			if (expected[0] == 1) {
				return printMessage(topHead, "argx", F.List(topHead, F.ZZ(argSize), F.ZZ(expected[0])), engine);
			}
			if (argSize == 1) {
				return printMessage(topHead, "argr", F.List(topHead, F.ZZ(expected[0])), engine);
			}
			return printMessage(topHead, "argrx", F.List(topHead, F.ZZ(argSize), F.ZZ(expected[0])), engine);
		}
		return printMessage(topHead, "argt", F.List(topHead, F.ZZ(argSize), F.ZZ(expected[0]), F.ZZ(expected[1])),
				engine);
	}

	/**
	 * 
	 * @param symbol
	 * @param messageShortcut
	 *            the message shortcut defined in <code>MESSAGES</code> array
	 * @param listOfArgs
	 *            a list of arguments which should be inserted into the message shortcuts placeholder
	 * @param engine
	 * @return always <code>F.NIL</code>
	 */
	public static IAST printMessage(ISymbol symbol, String messageShortcut, final IAST listOfArgs, EvalEngine engine) {
		IExpr temp = symbol.evalMessage(messageShortcut);
		String message = null;
		if (temp.isPresent()) {
			message = temp.toString();
		} else {
			temp = F.General.evalMessage(messageShortcut);
			if (temp.isPresent()) {
				message = temp.toString();
			}
		}
		if (message == null) {
			message = "Undefined message shortcut: " + messageShortcut;
			engine.setMessageShortcut(messageShortcut);
			engine.printMessage(symbol.toString() + ": " + message);
		} else {
			for (int i = 1; i < listOfArgs.size(); i++) {
				message = StringUtils.replace(message, "`" + (i) + "`", listOfArgs.get(i).toString());
			}
			engine.setMessageShortcut(messageShortcut);
			engine.printMessage(symbol.toString() + ": " + message);
		}
		return F.NIL;
	}

	public static String getMessage(String messageShortcut, final IAST listOfArgs, EvalEngine engine) {
		IExpr temp = F.General.evalMessage(messageShortcut);
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
			message = StringUtils.replace(message, "`" + (i) + "`", listOfArgs.get(i).toString());
		}
		engine.setMessageShortcut(messageShortcut);
		return message;
	}

	public static IAST printMessage(ISymbol symbol, Exception ex, EvalEngine engine) {
		String message = ex.getMessage();
		if (message != null) {
			engine.printMessage(symbol.toString() + ": " + message);
		} else {
			engine.printMessage(symbol.toString() + ": " + ex.getClass().toString());
		}

		return F.NIL;
	}

	private static String rawMessage(final IAST ast, String message) {
		for (int i = 2; i < ast.size(); i++) {
			message = StringUtils.replace(message, "`" + (i - 1) + "`", ast.get(i).toString());
		}
		return message;
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
		name = Config.PARSER_USE_LOWERCASE_SYMBOLS ? name.toLowerCase() : name;
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
		namePrefix = Config.PARSER_USE_LOWERCASE_SYMBOLS ? namePrefix.toLowerCase() : namePrefix;
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
		// for (int i = 0; i < size; i++) {
		// list.append(F.$s(AST2Expr.FUNCTION_STRINGS[i]));
		// }
		// return list;
	}

	private IOFunctions() {

	}

}
