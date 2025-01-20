/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018 Tilman Neumann - tilman.neumann@web.de
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program;
 * if not, see <http://www.gnu.org/licenses/>.
 */
package de.tilman_neumann.jml.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.tilman_neumann.util.StringUtil;

/**
 * A two-dimensional number grid with pretty-print method.
 * 
 * @author Tilman Neumann
 *
 * @param <U> element class
 */
public class NumberGrid<U> implements Serializable {

	private static final long serialVersionUID = 3264871495134558547L;
	
	private String xLabel = null;
	private int xStart = 0;
	private int xIncrement = 1;
	private String yLabel = null;
	private int yStart = 0;
	private int yIncrement = 1;

	private LinkedList<List<U>> rows = null;

	/**
	 * Simplified constructor with offsets 1.
	 * 
	 * @param xLabel The letter to use for the x-axis
	 * @param xStart The start value for the x-axis-values
	 * @param yLabel The letter to use for the y-axis
	 * @param yStart The start value for the y-axis-values
	 */
	public NumberGrid(String xLabel, int xStart, String yLabel, int yStart) {
		this(xLabel, xStart, 1, yLabel, yStart, 1);
	}
	
	/**
	 * Full constructor with all options.
	 * 
	 * @param xLabel The letter to use for the x-axis
	 * @param xStart The start value for the x-axis-values
	 * @param xIncrement The increment for the x-axis-values
	 * @param yLabel The letter to use for the y-axis
	 * @param yStart The start value for the y-axis-values
	 * @param yIncrement The increment for the y-axis-values
	 */
	public NumberGrid(String xLabel, int xStart, int xIncrement, String yLabel, int yStart, int yIncrement) {
		this.xLabel = xLabel;
		this.yLabel = yLabel;
		this.xStart = xStart;
		this.xIncrement = xIncrement;
		this.yStart = yStart;
		this.yIncrement = yIncrement;
		this.rows = new LinkedList<List<U>>();
	}

	/**
	 * Adds a new row of numbers to this grid.
	 * 
	 * @param row The new row.
	 */
	public void add(List<U> row) {
		this.rows.add(row);
	}

	/**
	 * @return Maximum number of elements in a row.
	 */
	public int getNumberOfColumns() {
		int numberOfColumns = 0;
		if (rows != null) {
			for (List<U> row : rows) {
				if (row != null) {
					int rowLength = row.size();
					if (rowLength > numberOfColumns) {
						numberOfColumns = rowLength;
					}
				}
			}
		}
		return numberOfColumns;
	}

	/**
	 * @return list of rows
	 */
	public LinkedList<List<U>> getRows() {
		return rows;
	}
	
	/**
	 * @return this array as a most compressed print table
	 */
	@Override
	public String toString() {
		int numberOfColumns = this.getNumberOfColumns();
		int[] columnWidths = new int[numberOfColumns];
		for (int j=0; j<columnWidths.length; j++) {
			columnWidths[j] = 0;
		}
		// Determine maximal lengths of strings in the several columns:
		for (List<U> row : this.rows) {
			if (row != null) {
				int j=0;
				for (Object rowValue : row) {
					if (rowValue != null) {
						int strLen = rowValue.toString().length();
						if (strLen > columnWidths[j]) {
							columnWidths[j] = strLen;
						}
					}
					j++;
				} // row values
			}
		} // rows
		String[] columnMasks = new String[numberOfColumns];
		for (int j=0; j<numberOfColumns; j++) {
			columnMasks[j] = StringUtil.repeat(" ", columnWidths[j]+1);
		}
		StringBuffer buffer = new StringBuffer();
		String header = xLabel + "\\" + yLabel + "\t";
		for (int j=0; j<numberOfColumns; j++) {
			header += StringUtil.formatLeft(String.valueOf(yStart+j*yIncrement), columnMasks[j]);
		}
		buffer.append(header + "\n");
		int i = 0;
		for (List<U> row : rows) {
			String line = (xStart+i*xIncrement + "\t");
			int j=0;
			for (Object value : row) {
				String valueStr = (value!=null) ? value.toString() : "";
				line += StringUtil.formatLeft(valueStr, columnMasks[j]);
				j++;
			}
			buffer.append(line + "\n");
			i++;
		}
		return buffer.toString();
	}
	
	/**
	 * @return This triangle converted into a list read by rows.
	 */
	public ArrayList<U> toList() {
		int n = rows.size();
		int number = n*(n+1)/2;
		ArrayList<U> list = new ArrayList<U>(number);
		for (List<U> row : this.getRows()) {
			list.addAll(row);
		}
		return list;
	}
}
