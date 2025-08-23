package org.matheclipse.core.eval;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.ast.ASTNode;
import org.matheclipse.parser.client.ast.FunctionNode;

public class PackageUtil {

  public static IExpr evaluatePackage(final List<ASTNode> node, final EvalEngine engine) {
    AST2Expr ast2Expr = new AST2Expr(engine.isRelaxedSyntax(), engine);
    String compoundExpression =
        engine.isRelaxedSyntax() ? "compoundexpression" : "CompoundExpression";
    return evaluatePackageRecursive(node, 0, compoundExpression, ast2Expr, engine);
  }

  private static IExpr evaluatePackageRecursive(final List<ASTNode> node, int i,
      String compoundExpression, AST2Expr ast2Expr, final EvalEngine engine) {
    IExpr temp;
    IExpr result = S.Null;
    while (i < node.size()) {
      ASTNode astNode = node.get(i);
      if (astNode instanceof FunctionNode && //
          ((FunctionNode) astNode).get(0).getString().equals(compoundExpression)) {
        result = evaluatePackageRecursive(((FunctionNode) astNode), 1, compoundExpression, ast2Expr,
            engine);
      } else {
        try {
          temp = ast2Expr.convert(astNode);
          engine.setDeterminePrecision(temp, true);
          result = engine.evaluate(temp);
        } catch (final RuntimeException rex) {
          result = S.Null;
          Errors.printMessage(S.Get, rex, engine);
        }
      }
      i++;
    }
    return result;
  }

  /**
   * Load a package from the given reader
   *
   * @param engine
   * @param is
   * @return the last evaluated expression result
   */
  public static IExpr loadPackage(final EvalEngine engine, final BufferedReader is) {
    try (final BufferedReader r = is) {
      final List<ASTNode> node = parseReader(r, engine);

      return evaluatePackage(node, engine);
    } catch (final Exception e) {
      Errors.printMessage(S.Get, e, engine);
    }
    return S.Null;
  }

  /**
   * Load a package from the given file
   *
   * @param engine
   * @param file
   * @return the last evaluated expression result
   * @throws FileNotFoundException
   * @throws UnsupportedEncodingException
   */
  public static IExpr loadPackage(final EvalEngine engine, final File file)
      throws FileNotFoundException {
    BufferedReader reader = new BufferedReader(new FileReader(file));
    return loadPackage(engine, reader);
  }

  /**
   * Parse the <code>reader</code> input.
   *
   * <p>
   * This method ignores the first line of the script if it starts with the <code>#!</code>
   * characters (i.e. Unix Script Executables)
   *
   * <p>
   * <b>Note</b>: uses the <code>ASTNode</code> parser and not the <code>ExprParser</code>, because
   * otherwise the symbols couldn't be assigned to the contexts.
   *
   * @param reader
   * @param engine
   * @return
   * @throws IOException
   */
  public static List<ASTNode> parseReader(final BufferedReader reader, final EvalEngine engine)
      throws IOException {
    String record;
    StringBuilder builder = new StringBuilder(2048);
    if ((record = reader.readLine()) != null) {
      // ignore the first line of the script if it starts with the #!
      // characters (i.e. Unix Script Executables)
      if (!record.startsWith("!#")) {
        builder.append(record);
        builder.append('\n');
      }
    }
    while ((record = reader.readLine()) != null) {
      builder.append(record);
      builder.append('\n');
    }

    final Parser parser = new Parser(engine.isRelaxedSyntax(), true);
    final List<ASTNode> node = parser.parsePackage(builder.toString());
    return node;
  }

  /**
   * Parse the <code>reader</code> input.
   *
   * <p>
   * This method ignores the first line of the script if it starts with the <code>#!</code>
   * characters (i.e. Unix Script Executables)
   *
   * <p>
   * <b>Note</b>: uses the <code>ASTNode</code> parser and not the <code>ExprParser</code>, because
   * otherwise the symbols couldn't be assigned to the contexts.
   *
   * @param reader
   * @param engine
   * @return
   */
  public static List<ASTNode> parseReader(final String reader, final EvalEngine engine) {
    final Parser parser = new Parser(engine.isRelaxedSyntax(), true);
    final List<ASTNode> node = parser.parsePackage(reader);
    return node;
  }

  private PackageUtil() {
    // private constructor to avoid instantiation
  }
}
