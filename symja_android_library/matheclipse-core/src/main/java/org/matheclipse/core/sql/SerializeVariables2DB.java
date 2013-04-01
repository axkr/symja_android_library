package org.matheclipse.core.sql;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import org.matheclipse.core.interfaces.ISymbol;

public class SerializeVariables2DB {

	public static void write(Connection con, String sessionID, Set<ISymbol> list) throws SQLException, IOException {
		PreparedStatement deleteStatement = con.prepareStatement("DELETE FROM variables WHERE session='?' AND name ='?'");
		PreparedStatement insertStatement = con.prepareStatement("INSERT INTO variables(session, name, symbol_data) VALUES(?,?,?)");
		ByteArrayOutputStream baos;
		ObjectOutputStream out;
		// for (int i = 0; i < list.size(); i++) {
		for (ISymbol symbol : list) {
			baos = new ByteArrayOutputStream();
			out = new ObjectOutputStream(baos);
			// symbol = list.get(i);
			symbol.writeSymbol(out);
			out.close();
			deleteStatement.setString(1, sessionID);
			deleteStatement.setString(2, symbol.toString());
			deleteStatement.execute();
			insertStatement.setString(1, sessionID);
			insertStatement.setString(2, symbol.toString());
			insertStatement.setBytes(3, baos.toByteArray());
			insertStatement.execute();
			baos.close();
		}
		con.commit();
	}

	public static void read(Connection con, String sessionID, ISymbol symbol) throws SQLException, IOException {
		PreparedStatement selectStatement = con
				.prepareStatement("SELECT session, name, symbol_data FROM variables WHERE session='?' AND name ='?'");
		selectStatement.setString(1, sessionID);
		selectStatement.setString(2, symbol.toString());
		ResultSet result = selectStatement.executeQuery();
		while (result.next()) {
			ObjectInputStream in = new ObjectInputStream(result.getBinaryStream(3));
			symbol.readSymbol(in);
			in.close();
			break;
		}
	}

	/**
	 * Delete the complete session variables information from the database
	 * 
	 * @param con
	 * @param sessionID
	 * @throws SQLException
	 * @throws IOException
	 */
	public static void deleteSession(Connection con, String sessionID) throws SQLException, IOException {
		PreparedStatement deleteStatement = con.prepareStatement("DELETE FROM variables WHERE session='?'");
		deleteStatement.setString(1, sessionID);
		deleteStatement.execute();
		con.commit();
	}
}
