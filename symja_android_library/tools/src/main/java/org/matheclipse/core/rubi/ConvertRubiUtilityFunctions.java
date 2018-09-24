package org.matheclipse.core.rubi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.ast.ASTNode;

/**
 * Convert the Rubi UtilityFunctions from <a href="http://www.apmaths.uwo.ca/~arich/">Rubi - Indefinite Integration
 * Reduction Rules</a>
 * 
 */
public class ConvertRubiUtilityFunctions {
	private final static String HEADER = "package org.matheclipse.core.integrate.rubi;\n" + "\n" + "\n"
			+ "import static org.matheclipse.core.expression.F.*;\n"
			+ "import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;\n" + "\n"
			+ "import org.matheclipse.core.interfaces.IAST;\n" + "/** \n"
			+ " * UtilityFunctions rules from the <a href=\"http://www.apmaths.uwo.ca/~arich/\">Rubi -\n"
			+ " * rule-based integrator</a>.\n" + " *  \n" + " */\n" + "public class UtilityFunctions";

	private static final String FOOTER = "}\n";

	private static int NUMBER_OF_RULES_PER_FILE = 90;

	public static List<ASTNode> parseFileToList(String fileName) {
		try {
			File file = new File(fileName);
			final BufferedReader f = new BufferedReader(new FileReader(file));
			final StringBuffer buff = new StringBuffer(1024);
			String line;
			while ((line = f.readLine()) != null) {
				buff.append(line);
				buff.append('\n');
			}
			f.close();
			String inputString = buff.toString();
			Parser p = new Parser(false, true);
			return p.parsePackage(inputString);
			// return p.parsePackage(inputString);

			// assertEquals(obj.toString(),
			// "Plus[Plus[Times[-1, a], Times[-1, Times[b, Factorial2[c]]]], d]");
		} catch (Exception e) {
			e.printStackTrace();
			// assertEquals("", e.getMessage());
		}
		return null;
	}

	public static void writeFile(String fileName, StringBuffer buffer) {
		try {
			File file = new File(fileName);
			final BufferedWriter f = new BufferedWriter(new FileWriter(file));
			f.append(buffer);
			f.close();
		} catch (Exception e) {
			e.printStackTrace();
			// assertEquals("", e.getMessage());
		}
	}

	public static void convert(ASTNode node, StringBuffer buffer, boolean last, Set<String> functionSet) {
		try {
			// convert ASTNode to an IExpr node
			IExpr expr = new AST2Expr(false, EvalEngine.get()).convert(node);

			if (expr.isAST(F.CompoundExpression)) {
				IAST ast=(IAST)expr;
				ast.forEach(x->convertExpr(x, buffer, last, functionSet));
			} else {
				convertExpr(expr, buffer, last, functionSet);
			}
		} catch (UnsupportedOperationException uoe) {
			System.out.println(uoe.getMessage());
			System.out.println(node.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void convertExpr(IExpr expr, StringBuffer buffer, boolean last, Set<String> functionSet) {
		// ISymbol module = F.$s("Module");
		// if (expr.isFree(module, true)) {
		if (expr.isAST(F.SetDelayed, 3)) {
			IAST ast = (IAST) expr;
			addToFunctionSet(ast, functionSet);
			ConvertRubi.appendSetDelayedToBuffer(ast, buffer, last);
		} else if (expr.isAST(F.If, 4)) {
			IAST ast = (IAST) expr;
			if (ast.get(1).toString().equals("§showsteps")) {
				expr = ast.get(3);
				if (expr.isAST(F.SetDelayed, 3)) {
					ast = (IAST) expr;
					addToFunctionSet(ast, functionSet);
					ConvertRubi.appendSetDelayedToBuffer(ast, buffer, last);
				}
			}
		}
		// }
	}

	private static void addToFunctionSet(IAST ast, Set<String> functionSet) {
		if (ast.get(1).isAST()) {
			IAST lhsAST = (IAST) ast.arg1();
			if (lhsAST.head().isSymbol()) {
				ISymbol sym = (ISymbol) lhsAST.head();
				if (Character.isUpperCase(sym.toString().charAt(0))) {
					String entry = sym.toString() + "," + lhsAST.size();
					if (lhsAST.size() == 2 && lhsAST.get(1).isPatternSequence(false)) {
						entry = sym.toString() + "," + Integer.MAX_VALUE;
					}
					functionSet.add(entry);
				}
			}
		}
	}

	public final static String INTEGRATE_PREFIX = "Integrate::";

	public static void main(String[] args) {
		Config.SERVER_MODE = false;
		Config.PARSER_USE_LOWERCASE_SYMBOLS = false;
		Config.RUBI_CONVERT_SYMBOLS = true;
		EvalEngine.set(new EvalEngine(false));
		ConvertRubi.addPredefinedSymbols();

		String[] fileNames = { "./Rubi/IntegrationUtilityFunctions.m" };
		for (int i = 0; i < fileNames.length; i++) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");
			System.out.println(">>>>> File name: " + fileNames[i]);
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");

			StringBuffer buffer = new StringBuffer(100000);
			List<ASTNode> list = parseFileToList(fileNames[i]);
			if (list != null) {
				int cnt = 0;
				int fcnt = 0;
				TreeSet<String> functionSet = new TreeSet<String>();
				TreeSet<String> uniqueFunctionSet = new TreeSet<String>();
				for (int j = 0; j < list.size(); j++) {
					if (cnt == 0) {
						buffer = new StringBuffer(100000);
						buffer.append(HEADER + fcnt + " { \n  public static IAST RULES = List( \n");
					}
					ASTNode astNode = list.get(j);

					cnt++;
					convert(astNode, buffer, cnt == NUMBER_OF_RULES_PER_FILE || j == list.size(), functionSet);

					if (cnt == NUMBER_OF_RULES_PER_FILE) {
						buffer.append("  );\n" + FOOTER);
						writeFile("C:/temp/rubi/UtilityFunctions" + fcnt + ".java", buffer);
						fcnt++;
						cnt = 0;
					}
				}
				if (cnt != 0) {
					// System.out.println(");");
					buffer.append("  );\n" + FOOTER);
					writeFile("C:/temp/rubi/UtilityFunctions" + fcnt + ".java", buffer);
				}
				buffer = new StringBuffer(100000);
				for (String str : functionSet) {
					String[] spl = str.split(",");
					String functionName = spl[0];
					uniqueFunctionSet.add(functionName);
				}
				for (String str : uniqueFunctionSet) {
					String functionName = str;
					buffer.append("    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(\"" + functionName
							+ "\",INTEGRATE_PREFIX+\"" + functionName + "\");\n");
				}
				System.out.println(buffer.toString());
				buffer = new StringBuffer(100000);
				for (String str : functionSet) {
					String[] spl = str.split(",");
					String functionName = spl[0];
					int numberOfArguments = Integer.valueOf(spl[1]).intValue() - 1;
					switch (numberOfArguments) {
					case 1:
						buffer.append("  public static IAST " + functionName + "(final IExpr a0) {\n");
						buffer.append("    return unary($rubi(INTEGRATE_PREFIX+\"" + functionName + "\"), a0);\n");
						buffer.append("  }\n\n");
						break;
					case 2:
						buffer.append("  public static IAST " + functionName + "(final IExpr a0, final IExpr a1) {\n");
						buffer.append("    return binary($rubi(INTEGRATE_PREFIX+\"" + functionName + "\"), a0, a1);\n");
						buffer.append("  }\n\n");
						break;
					case 3:
						buffer.append("  public static IAST " + functionName
								+ "(final IExpr a0, final IExpr a1, final IExpr a2) {\n");
						buffer.append(
								"    return ternary($rubi(INTEGRATE_PREFIX+\"" + functionName + "\"), a0, a1, a2);\n");
						buffer.append("  }\n\n");
						break;
					case 4:
						buffer.append("  public static IAST " + functionName
								+ "(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {\n");
						buffer.append("    return quaternary($rubi(INTEGRATE_PREFIX+\"" + functionName
								+ "\"), a0, a1, a2, a3);\n");
						buffer.append("  }\n\n");
						break;
					case 5:
						buffer.append("  public static IAST " + functionName
								+ "(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3, final IExpr a4) {\n");
						buffer.append("    return quinary($rubi(INTEGRATE_PREFIX+\"" + functionName
								+ "\"), a0, a1, a2, a3, a4);\n");
						buffer.append("  }\n\n");
						break;
					case 6:
						buffer.append("  public static IAST " + functionName
								+ "(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3, final IExpr a4, final IExpr a5) {\n");
						buffer.append("    return senary($rubi(INTEGRATE_PREFIX+\"" + functionName
								+ "\"), a0, a1, a2, a3, a4, a5);\n");
						buffer.append("  }\n\n");
						break;
					case Integer.MAX_VALUE:
						// MAX_VALUE indicates a left-hand-side form with a PetternSequence (i.e. f[x__]:=...)
						buffer.append("  public static IAST " + functionName + "(final IExpr... a) {\n");
						buffer.append("    return ast(a, $rubi(INTEGRATE_PREFIX+\"" + functionName + "\"));\n");
						buffer.append("  }\n\n");
						break;
					default:
						// System.out.println("ERROR in SWITCH:" + functionName + " - " + numberOfArguments);
						// public static IAST <functionName>(final IExpr... a) {
						// return ast(a, Part);
						// }
						buffer.append("  public static IAST " + functionName + "(final IExpr... a) {\n");
						buffer.append("    return ast(a, $rubi(INTEGRATE_PREFIX+\"" + functionName + "\"));\n");
						buffer.append("  }\n\n");
					}
				}
				System.out.println(buffer.toString());
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");
				System.out.println(">>>>> Number of entries: " + list.size());
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");

				// for (int j = 0; j < list.size(); j++) {
				// System.out.println(list.get(j).toString());
				// }
			}
		}
		// which built-in symbols are used how often?
		for (Map.Entry<String, Integer> entry : AST2Expr.RUBI_STATISTICS_MAP.entrySet()) {
			System.out.println(entry.getKey() + "  >>>  " + entry.getValue());
		}
	}
}
