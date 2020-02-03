package org.matheclipse.core.expression.data;

import java.util.ArrayList;
import java.util.List;

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
	public DataSetExpr retainColumns(IAST list) {
		// System.out.println(fData.columnNames().toString());
		String[] strList = new String[list.argSize()];
		int[] vector = list.toIntVector();
		Table table = fData.copy();
		if (vector == null) {
			for (int i = 0; i < strList.length; i++) {
				strList[i] = list.get(i + 1).toString();
			}
			table.retainColumns(strList);
			return newInstance(table);
		}
		for (int i = 0; i < vector.length; i++) {
			strList[i] = table.columnNames().get(vector[i] - 1);
		}
		table.retainColumns(strList);
		return newInstance(table);
	}

	/**
	 * Removes all columns except for those given in the <code>column</code>.
	 * 
	 * @param column
	 * @return
	 */
	public DataSetExpr retainColumns(int column) {
		String[] strList = new String[1];
		Table table = fData.copy();
		strList[0] = table.columnNames().get(column - 1);
		table.retainColumns(strList);
		return newInstance(table);
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
