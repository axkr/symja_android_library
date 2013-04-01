package org.matheclipse.core.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import junit.framework.TestCase;

import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.ast.ASTNode;

public class TestSQLExpressionMap extends TestCase {
	public final static String TEST_EXPR = "D[Sin[x], x]";

	public void testSQL() {
		// --
		// -- Database: `matheclipse`
		// --
		//
		// --
		// -- Table structure for table `expr_map`
		// --
		//
		// CREATE TABLE `expr_map` (
		// `id` int(11) NOT NULL auto_increment,
		// `hvalue` int(11) NOT NULL default '0',
		// `lhs` varchar(255) NOT NULL default '',
		// `rhs` longtext NOT NULL,
		// PRIMARY KEY (`id`),
		// KEY `hash` (`hvalue`,`lhs`)
		// ) TYPE=MyISAM;
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://localhost:3306/matheclipse";
			conn = DriverManager.getConnection(url, "root", "");

			// setup the evaluation engine (and bind to current thread)
			EvalEngine engine = EvalEngine.get();
			engine.setSessionID("TestSQLExpressionMap");
			engine.setRecursionLimit(256);
			EvalUtilities util = new EvalUtilities(engine, false);
			// setup a parser for the math expressions

			Parser parser = new Parser();
			// setup the expression factory (and bind to current thread)
//			parser.setFactory(ExpressionFactory.get());
 
			ASTNode parsedAST = parser.parse(TEST_EXPR);
			IExpr parsedExpression = AST2Expr.CONST.convert(parsedAST);
			if (parsedExpression != null) {
				String lhsString = parsedExpression.toString();
				int lhsHash = parsedExpression.hashCode();

				System.out.println("Select an expression");
				IExpr expr = SQLExpressionMap.select(conn, parser, lhsHash, lhsString);
				if (expr != null) {
					assertEquals("Cos[x]", expr.toString());
				} else {
					System.out.println("Insert an expression");
					IExpr result = util.evaluate(parsedExpression);
					SQLExpressionMap.insert(conn, lhsHash, lhsString, result);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
