package org.matheclipse.core.expression.data;

import java.util.ArrayList;
import java.util.List;

import org.matheclipse.core.convert.Object2Expr;
import org.matheclipse.core.expression.AssociationAST;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

public class DataSetExpr extends DataExpr<Table> {

	private static final long serialVersionUID = 7276828936929270780L;

	/**
	 * 
	 * @param value
	 * @return
	 */
	public static DataSetExpr newInstance(Table value) {
		return new DataSetExpr(value);
	}

	protected DataSetExpr(final Table value) {
		super(F.DataSet, value);
	}

	/**
	 * Removes all columns except for those given in the <code>list</code>.
	 * 
	 * @param list
	 * @return
	 */
	private DataSetExpr selectColumns(IAST list) {
		// System.out.println(fData.columnNames().toString());
		String[] strList = new String[list.argSize()];
		int[] vector = list.toIntVector();
		Table table = fData;
		if (vector == null) {
			for (int i = 0; i < strList.length; i++) {
				strList[i] = list.get(i + 1).toString();
			}
			return newInstance(table.select(strList));
		}
		List<String> columnNames = table.columnNames();
		for (int i = 0; i < vector.length; i++) {
			strList[i] = columnNames.get(vector[i] - 1);
		}
		return newInstance(table.select(strList));
	}

	/**
	 * Removes all columns except for those given in the <code>column</code>.
	 * 
	 * @param column
	 * @return
	 */
	private DataSetExpr selectColumns(int column) {
		String[] strList = new String[1];
		Table table = fData;
		strList[0] = table.columnNames().get(column - 1);
		return newInstance(table.select(strList));
	}

	private IExpr select(int row, int column) {
		Table table = fData;
		return Object2Expr.convert(table.column(column - 1).get(row - 1));
	}

	public IExpr select(IExpr row, IExpr column) {
		Table table = fData;

		int[] span = column.isSpan(table.columnCount() - 1);
		if (span != null && span[2] == 1) {
			int columnStart = span[0] - 1;
			int columnEnd = span[1];
			String[] strList = new String[columnEnd - columnStart];
			List<String> columnNames = table.columnNames();
			for (int i = 0; i < strList.length; i++) {  
				strList[i] = columnNames.get(i + columnStart);
			}
			table = table.select(strList);
		} else if (column.equals(F.All)) {
		} else if (column.isString()) {
			table = table.select(column.toString());
		} else if (column.isList()) {
			IAST list = (IAST) column;
			String[] strList = new String[list.argSize()];
			int[] vector = list.toIntVector();
			if (vector == null) {
				for (int i = 0; i < strList.length; i++) {
					strList[i] = list.get(i + 1).toString();
				}
			} else {
				List<String> columnNames = table.columnNames();
				for (int i = 0; i < vector.length; i++) {
					strList[i] = columnNames.get(vector[i] - 1);
				}
			}
			table = table.select(strList);
		} else {
			int colIndex = column.toIntDefault();
			if (colIndex > 0) {
				table = fData;
				table = table.select(table.columnNames().get(colIndex - 1));
			} else {
				return F.NIL;
			}
		}

		span = row.isSpan(table.rowCount() - 1);
		if (span != null && span[2] == 1) {
			int rowStart = span[0] - 1;
			int rowEnd = span[1];
			table = table.inRange(rowStart, rowEnd);
			return newInstance(table);
		} else if (row.equals(F.All)) {
			return newInstance(table);
		} else if (row.isList()) {
			IAST list = (IAST) row;
			int[] iList = new int[list.argSize()];
			for (int i = 1; i < list.size(); i++) {
				iList[i - 1] = list.get(i).toIntDefault();
				if (iList[i - 1] <= 0) {
					return F.NIL;
				}
				iList[i - 1]--;
			}
			table = table.rows(iList);
			if (table.columnCount() == 1) {
				return Object2Expr.convert(table.get(0, 0));
			}
			return newInstance(table);
		} else {
			int rowIndex = row.toIntDefault();
			if (rowIndex > 0) {
				table = table.rows(rowIndex - 1);
				if (table.columnCount() == 1) {
					return Object2Expr.convert(table.get(0, 0));
				}
				return newInstance(table);
			}
		}
		return F.NIL;
	}

	@Override
	public IExpr copy() {
		return new DataSetExpr(fData);
	}

	public IAST normal() {
		Cache<IAST, IAST> cache = CacheBuilder.newBuilder().maximumSize(500).build();
		final List<String> names = fData.columnNames();
		List<IStringX> namesStr = new ArrayList<IStringX>(names.size());
		for (int i = 0; i < names.size(); i++) {
			namesStr.add(F.stringx(names.get(i)));
		}
		IASTAppendable dataSet = F.ast(F.DataSet, names.size() + 1, false);
		int size = fData.rowCount();
		for (int k = 0; k < size; k++) {
			Row row = fData.row(k);
			IAssociation assoc = new AssociationAST(row.columnCount(), false);
			for (int j = 0; j < row.columnCount(); j++) {
				String columnName = names.get(j);
				IStringX colName = namesStr.get(j);
				ColumnType t = row.getColumnType(columnName);
				Object obj = row.getObject(j);
				if (t.equals(ColumnType.BOOLEAN)) {
					Boolean b = row.getBoolean(j);
					if (b.booleanValue()) {
						ruleCache(cache, assoc, F.Rule(colName, F.True));
					} else {
						ruleCache(cache, assoc, F.Rule(colName, F.False));
					}
				} else if (t.equals(ColumnType.SHORT)) {
					short sValue = row.getShort(j);
					ruleCache(cache, assoc, F.Rule(colName, F.ZZ(sValue)));
				} else if (t.equals(ColumnType.INTEGER)) {
					int iValue = row.getInt(j);
					ruleCache(cache, assoc, F.Rule(colName, F.ZZ(iValue)));
				} else if (t.equals(ColumnType.LONG)) {
					long lValue = row.getLong(j);
					ruleCache(cache, assoc, F.Rule(colName, F.ZZ(lValue)));
				} else if (t.equals(ColumnType.FLOAT)) {
					float fValue = row.getFloat(j);
					ruleCache(cache, assoc, F.Rule(colName, F.num(fValue)));
				} else if (t.equals(ColumnType.DOUBLE)) {
					double dValue = row.getDouble(j);
					ruleCache(cache, assoc, F.Rule(colName, F.num(dValue)));
				} else if (t.equals(ColumnType.STRING)) {
					ruleCache(cache, assoc, F.Rule(colName, F.stringx(row.getString(j))));
				} else if (t.equals(ColumnType.SKIP)) {
					// ruleCache(cache, assoc, F.Rule(colName, F.Missing));
				} else {
					IExpr valueStr = F.stringx(obj.toString());
					ruleCache(cache, assoc, F.Rule(colName, valueStr));
				}
			}
			if (size == 1) {
				return assoc;
			}
			dataSet.append(assoc);
		}
		return dataSet;
	}

	private static void ruleCache(Cache<IAST, IAST> cache, IAssociation assoc, IAST rule) {
		IAST result = cache.getIfPresent(rule);
		if (result != null) {
			assoc.appendRule(result);
			return;
		}
		cache.put(rule, rule);
		assoc.appendRule(rule);
	}
}
