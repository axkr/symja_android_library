package org.matheclipse.core.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.ast.ASTNode;

public class SQLExpressionMap {
	/**
	 * Max number of characters in expr_map lhs database field
	 *
	 */
	private final static int MAX_DB_KEY_CHARACTERS = 250;

	private final static String INSERT_STR = "INSERT INTO expr_map (hvalue,lhs,rhs) VALUES (?, ?, ?)";

	private final static String SELECT_STR = "SELECT rhs FROM expr_map WHERE hvalue = ? and lhs = ?";

	private final static String INSERT_LOG_STR = "INSERT INTO log (expr,addr,host,sessionid) VALUES (?, ?, ?, ?)";
//	 --
//	 -- Database: `matheclipse`
//	 --
//
//	 --
//	 -- Table structure for table `expr_map`
//	 --

//	 CREATE TABLE `expr_map` (
//	 `id` int(11) NOT NULL auto_increment,
//	 `hvalue` int(11) NOT NULL default '0',
//	 `lhs` varchar(255) NOT NULL default '',
//	 `rhs` longtext NOT NULL,
//	 PRIMARY KEY (`id`),
//	 KEY `hash` (`hvalue`,`lhs`)
//	 ) TYPE=MyISAM;
//
//	CREATE TABLE `log` (
//		  `id` int(11) NOT NULL auto_increment,
//		  `expr` longtext NOT NULL,
//		  `addr` varchar(255) NOT NULL default '',
//		  `host` varchar(255) NOT NULL default '',
//		  `sessionid` varchar(255) NOT NULL default '',
//		  PRIMARY KEY  (`id`)
//		) TYPE=MyISAM;
 
	/**
	 * Insert a new <i>left-hand-side (lhs) -> right-hand-side (rhs)</i>
	 * conversion rule into the database
	 */
	public static void insert(final Connection conn, final int lhsHash, final String lhsString, final IExpr rhs) throws SQLException {
		final PreparedStatement insertStmt = conn.prepareStatement(INSERT_STR);
		try {
			insertStmt.setInt(1, lhsHash);
			insertStmt.setString(2, lhsString);
			insertStmt.setString(3, rhs.toString());
			insertStmt.executeUpdate();
		} finally {
			insertStmt.close();
		}
	}

	/**
	 * Select a right-hand-side (rhs) expression from a given left-hand-side (lhs)
	 * expression
	 */
	public static IExpr select(final Connection conn, final Parser parser, final int lhsHash, final String lhsString) throws SQLException, SyntaxError {
		if (lhsString.length() > MAX_DB_KEY_CHARACTERS) {
			return null;
		}
		final PreparedStatement selectStmt = conn.prepareStatement(SELECT_STR);
		selectStmt.setInt(1, lhsHash);
		selectStmt.setString(2, lhsString);
		final ResultSet resultSet = selectStmt.executeQuery();
		try {
			while (resultSet.next()) {
				final String rhs = resultSet.getString(1);
				final ASTNode parsedAST = parser.parse(rhs);
				return AST2Expr.CONST.convert(parsedAST);
			}
		} finally {
			resultSet.close();
			selectStmt.close();
		}
		return null;
	}

	/**
	 * Insert a new <i>left-hand-side (lhs) -> right-hand-side (rhs)</i>
	 * conversion rule into the database
	 */
	public static void insertLog(final Connection conn, final String expr, final String addr, final String host, final String sessionId) throws SQLException {
		final PreparedStatement insertStmt = conn.prepareStatement(INSERT_LOG_STR);
		try {
			insertStmt.setString(1, expr);
			insertStmt.setString(2, addr);
			insertStmt.setString(3, host);
			insertStmt.setString(4, sessionId);
			insertStmt.executeUpdate();
		} finally {
			insertStmt.close();
		}
	}
}
