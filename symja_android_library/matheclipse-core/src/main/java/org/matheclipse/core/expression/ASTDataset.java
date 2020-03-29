package org.matheclipse.core.expression;

import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Object2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.MemoryLimitExceeded;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IDataExpr;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.IVisitorLong;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.html.HtmlWriteOptions;

public class ASTDataset extends AbstractAST implements IDataExpr<Table>, Externalizable {

	private static final long serialVersionUID = 7276828936929270780L;

	/**
	 * 
	 * @param listOfAssociations
	 * @return
	 * @deprecated szub only
	 */
	// private static DatasetExpr newInstance(IAST listOfAssociations) {
	// Table table = Table.create();
	// if (listOfAssociations.size() > 1) {
	// // for (int i = 1; i < listOfAssociations.size(); i++) {
	// // IAssociation assoc = (IAssociation) listOfAssociations.get(i);
	// // // Row row = table.appendRow();
	// // }
	// IAssociation assoc = (IAssociation) listOfAssociations.get(1);
	// ArrayList<String> names = assoc.keyNames();
	//
	// for (int i = 1; i < listOfAssociations.size(); i++) {
	// assoc = (IAssociation) listOfAssociations.get(i);
	//
	// }
	// }
	// return new DatasetExpr(table);
	// }

	/**
	 * 
	 * @param value
	 * @return
	 */
	public static ASTDataset newInstance(Table value) {
		return new ASTDataset(value);
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

	protected transient Table fTable;

	public ASTDataset() {
		// default ctor for serialization
	}

	protected ASTDataset(final Table table) {
		fTable = table;
	}

	public void csv(Writer writer) throws IOException {
		fTable.write().csv(writer);
	}

	/** {@inheritDoc} */
	@Override
	public long accept(IVisitorLong visitor) {
		return 0L;
	}

	@Override
	public ASTDataset copy() {
		return new ASTDataset(fTable);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof ASTDataset) {
			return fTable.equals(((ASTDataset) obj).fTable);
		}
		return false;
	}

	@Override
	public IExpr evaluate(EvalEngine engine) {
		return F.NIL;
	}

	/** {@inheritDoc} */
	@Override
	public String fullFormString() {
		return head() + "(" + fTable.toString() + ")";
	}

	@Override
	public IExpr get(int location) {
		if (location == 0) {
			return head();
		}
		if (fTable.rowCount() == 1) {
			return getColumnValue(0, location - 1);
		}
		return newInstance(fTable.rows(location - 1));
	}

	private IExpr getColumnValue(int rowPosition, int columnPosition) {
		Column<?> column = fTable.column(columnPosition);
		ColumnType t = column.type();
		Object obj = fTable.get(rowPosition, columnPosition);
		if (t.equals(ColumnType.BOOLEAN)) {
			Boolean b = (Boolean) obj;
			if (b.booleanValue()) {
				return F.True;
			} else {
				return F.False;
			}
		} else if (t.equals(ColumnType.SHORT)) {
			short sValue = (Short) obj;
			return F.ZZ(sValue);
		} else if (t.equals(ColumnType.INTEGER)) {
			int iValue = (Integer) obj;
			return F.ZZ(iValue);
		} else if (t.equals(ColumnType.LONG)) {
			long lValue = (Long) obj;
			return F.ZZ(lValue);
		} else if (t.equals(ColumnType.FLOAT)) {
			float fValue = (Float) obj;
			return F.num(fValue);
		} else if (t.equals(ColumnType.DOUBLE)) {
			double dValue = (Double) obj;
			return F.num(dValue);
		} else if (t.equals(ColumnType.STRING)) {
			return F.stringx((String) obj);
			// } else if (t.equals(ColumnType.SKIP)) {
			// ruleCache(cache, assoc, F.Rule(colName, F.Missing));
		}
		IExpr valueStr = F.stringx(obj.toString());
		return valueStr;
	}

	@Override
	public IAST getItems(int[] items, int length) {
		int[] rows = new int[length];
		for (int i = 0; i < length; i++) {
			rows[i] = items[i] - 1;
		}
		return newInstance(fTable.rows(rows));
	}

	/**
	 * Return the value associated to the <code>key</code>. If no value is available return
	 * <code>Missing("KeyAbsent", key)</code>
	 * 
	 * @param key
	 * @return
	 */
	public IExpr getValue(IExpr key) {
		return getValue(key, F.Missing(F.stringx("KeyAbsent"), key));
	}

	/**
	 * Return the value associated to the <code>key</code>. If no value is available return the
	 * <code>defaultValue</code>
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public IExpr getValue(IExpr key, IExpr defaultValue) {
		final String keyName = key.toString();
		if (fTable.rowCount() == 1) {
			int columnIndex = fTable.columnIndex(keyName);
			if (columnIndex < 0) {
				return defaultValue;
			}
			return select(1, columnIndex + 1);
		}
		String[] strList = new String[] { keyName };
		Table table = fTable.select(strList);
		if (table.columnCount() == 0) {
			return defaultValue;
		}
		return newInstance(table);
	}

	public IExpr groupBy(List<String> group) {
		String[] strings = new String[group.size()];
		for (int i = 0; i < strings.length; i++) {
			strings[i] = group.get(i);
		}
		Table table = fTable.sortAscendingOn(strings);
		return newInstance(table);
	}

	@Override
	public int hashCode() {
		return (fTable == null) ? 59 : 59 + fTable.hashCode();
	}

	@Override
	public ISymbol head() {
		return F.Dataset;
	}

	@Override
	public int hierarchy() {
		return DATASETID;
	}

	public IASTAppendable normal() {
		Cache<IAST, IAST> cache = CacheBuilder.newBuilder().maximumSize(500).build();
		final List<String> names = fTable.columnNames();
		List<IStringX> namesStr = new ArrayList<IStringX>(names.size());
		for (int i = 0; i < names.size(); i++) {
			namesStr.add(F.stringx(names.get(i)));
		}
		if (names.size() == 1) {
			Column<?> column = fTable.column(names.get(0));
			ColumnType t = column.type();
			IASTAppendable resultList = F.ListAlloc(column.size());
			for (int j = 0; j < column.size(); j++) {
				Object obj = column.get(j);
				if (t.equals(ColumnType.BOOLEAN)) {
					Boolean b = (Boolean) obj;
					if (b.booleanValue()) {
						resultList.append(F.True);
					} else {
						resultList.append(F.False);
					}
				} else if (t.equals(ColumnType.SHORT)) {
					short sValue = (Short) obj;
					resultList.append(F.ZZ(sValue));
				} else if (t.equals(ColumnType.INTEGER)) {
					int iValue = (Integer) obj;
					resultList.append(F.ZZ(iValue));
				} else if (t.equals(ColumnType.LONG)) {
					long lValue = (Long) obj;
					resultList.append(F.ZZ(lValue));
				} else if (t.equals(ColumnType.FLOAT)) {
					float fValue = (Float) obj;
					resultList.append(F.num(fValue));
				} else if (t.equals(ColumnType.DOUBLE)) {
					double dValue = (Double) obj;
					resultList.append(F.num(dValue));
				} else if (t.equals(ColumnType.STRING)) {
					resultList.append(F.stringx((String) obj));
				} else if (t.equals(ColumnType.SKIP)) {
					// ruleCache(cache, assoc, F.Rule(colName, F.Missing));
				} else {
					IExpr valueStr = F.stringx(obj.toString());
					resultList.append(valueStr);
				}
			}
			return resultList;
		}

		IASTAppendable list = F.ListAlloc(names.size());
		int size = fTable.rowCount();
		for (int k = 0; k < size; k++) {
			Row row = fTable.row(k);
			IAssociation assoc = new ASTAssociation(row.columnCount(), false);
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
			list.append(assoc);
		}
		return list;
	}

	public IExpr select(IExpr row, IExpr column) {
		Table table = fTable;

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
				table = fTable;
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

	private IExpr select(int row, int column) {
		Table table = fTable;
		return Object2Expr.convert(table.column(column - 1).get(row - 1));
	}

	/**
	 * Removes all columns except for those given in the <code>list</code>.
	 * 
	 * @param list
	 * @return
	 */
	private ASTDataset selectColumns(IAST list) {
		// System.out.println(fData.columnNames().toString());
		String[] strList = new String[list.argSize()];
		int[] vector = list.toIntVector();
		Table table = fTable;
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
	private ASTDataset selectColumns(int column) {
		String[] strList = new String[1];
		Table table = fTable;
		strList[0] = table.columnNames().get(column - 1);
		return newInstance(table.select(strList));
	}

	@Override
	public Table toData() {
		return fTable;
	}

	@Override
	public String toString() {
		return head() + "[" + fTable.toString() + "]";
	}

	@Override
	public IExpr set(int i, IExpr object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		if (fTable.rowCount() == 1) {
			return fTable.columnCount() + 1;
		}
		return fTable.rowCount() + 1;
	}

	@Override
	public IExpr arg1() {
		return get(1);
	}

	@Override
	public IExpr arg2() {
		return get(2);
	}

	@Override
	public IExpr arg3() {
		return get(31);
	}

	@Override
	public IExpr arg4() {
		return get(4);
	}

	@Override
	public IExpr arg5() {
		return get(5);
	}

	@Override
	public Set<IExpr> asSet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IASTAppendable copyAppendable() {
		return normal();
	}

	@Override
	public IExpr[] toArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ASTDataset clone() throws CloneNotSupportedException {
		return copy();
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
		String str = objectInput.readUTF();
		this.fTable = Table.read().csv(new StringReader(str));
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		StringWriter sw = new StringWriter();
		this.fTable.write().csv(sw);
		String str = sw.toString();
		if (str.length() >= Config.MAX_OUTPUT_SIZE) {
			throw new MemoryLimitExceeded("String length to big: " + str.length());
		}
		objectOutput.writeUTF(str);
	}

	public static String datasetToJSForm(ASTDataset dataset) throws IOException {
		Table table = dataset.fTable;
		OutputStream baos = new ByteArrayOutputStream();
		table.write().usingOptions(HtmlWriteOptions.builder(baos).escapeText(true).build());
		return baos.toString();
	}
}
