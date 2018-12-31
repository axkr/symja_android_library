/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018 Tilman Neumann (www.tilman-neumann.de)
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

import java.math.BigInteger;
import java.util.ArrayList;

//import org.apache.log4j.Logger;

/**
 * A triangle of integers.
 * 
 * @author Tilman Neumann
 */
// TODO: Let first entry be T[0,0]
// TODO: Use BigInteger[] for rows ?
// TODO: Improve pretty printing
public class BigIntTriangle {
	
//	@SuppressWarnings("unused")
//	private static final Logger LOG = Logger.getLogger(BigIntTriangle.class);

	private static long count = 0;
	private static Object syncObject = new Object();
	
	private ArrayList<ArrayList<BigInteger>> rows = null;
	private long id;
	
	/**
	 * Create a triangle without data.
	 * Rows may be added with the addRow() method.
	 */
	public BigIntTriangle() {
		this.rows = new ArrayList<ArrayList<BigInteger>>();
		synchronized (syncObject) {
			this.id = count++;
		}
		//LOG.debug("created triangle " + id + " with 0 rows");
	}
	
	/**
	 * Creates an initialized number triangle with n rows.
	 * 
	 * @param n
	 * @param init The value with which the triangle entries are initialized.
	 */
	public BigIntTriangle(int n, BigInteger init) {
		this.rows = new ArrayList<ArrayList<BigInteger>>(n);
		for (int i=0; i<n; i++) {
			int k = i+1;
			ArrayList<BigInteger> ithRow = new ArrayList<BigInteger>(k);
			for (int j=0; j<k; j++) {
				ithRow.add(init);
			}
			rows.add(i, ithRow);
		}
		synchronized (syncObject) {
			this.id = count++;
		}
		//LOG.debug("created triangle " + id + " with " + rows.size() + " zero-initialized rows");
	}
	
	/**
	 * Returns the entry T[n,k], with T[1,1] being the very first element.
	 * @param n
	 * @param k
	 * @return T[n,k]
	 */
	public BigInteger get(int n, int k) {
		return rows.get(n-1).get(k-1);
	}
	
	/**
	 * Sets the value T[n,k].
	 * @param n
	 * @param k
	 * @param value
	 */
	public void set(int n, int k, BigInteger value) {
		rows.get(n-1).set(k-1, value);
	}
	
	public void addRow(ArrayList<BigInteger> row) {
		if (row==null) throw new NullPointerException("row");
		// row size must be current number of rows + 1
		int rowSize = row.size();
		int expectedRowSize = rows.size() + 1;
		if (rowSize != expectedRowSize) throw new IllegalStateException("triangle " + id + ": new row has " + rowSize + " entries, but " + expectedRowSize + " are required!");
		rows.add(row);
		//LOG.debug("added row " + rows.size() + " to triangle " + uid);
	}
	
	/**
	 * return n.th row, where the first row has index 1.
	 * @param n
	 * @return
	 */
	public ArrayList<BigInteger> getRow(int n) {
		return rows.get(n-1);
	}
	
	/**
	 * Returns the sum over all entries of the n.th row, 
	 * where the first row has index 1.
	 * @param n
	 * @return
	 */
	public BigInteger getRowSum(int n) {
		BigInteger rowSum = BigInteger.ZERO;
		for (int k=1;k<=n;k++) {
			BigInteger entry = this.get(n, k);
			rowSum = rowSum.add(entry);
		}
		return rowSum;
	}
	
	/**
	 * @return List of row sums of this triangle.
	 */
	public ArrayList<BigInteger> getRowSums() {
		int n = rows.size();
		ArrayList<BigInteger> rowSums = new ArrayList<BigInteger>(n);
		for (int m=1;m<=n;m++) {
			BigInteger rowSum = getRowSum(m);
			rowSums.add(rowSum);
		}
		return rowSums;
	}
	
	/**
	 * @return This triangle converted into a list read by rows.
	 * Better suited for OEIS lookups.
	 */
	public ArrayList<BigInteger> toList() {
		int n = rows.size();
		int number = n*(n+1)/2;
		ArrayList<BigInteger> list = new ArrayList<BigInteger>(number);
		for (int m=1;m<=n;m++) {
			for (int k=1;k<=m;k++) {
				BigInteger entry = this.get(m, k);
				list.add(entry);
			}
		}
		return list;
	}
	
	@Override
	public String toString() {
		// max row width is estimated from last row
		int maxWidth = rows.size()-1;
		for (BigInteger elem : rows.get(maxWidth)) {
			maxWidth += elem.toString().length();
		}
		StringBuffer buf = new StringBuffer();
		for (ArrayList<BigInteger> row : rows) {
			StringBuffer rowBuf = new StringBuffer();
			for (BigInteger elem : row) {
				rowBuf.append(elem.toString() + " ");
			}
			int offset = (1 + maxWidth - rowBuf.length())/2;
			while (offset-->0) {
				buf.append(' ');
			}
			buf.append(rowBuf.toString());
			buf.append('\n'); // line feed
		}
		return buf.toString();
	}
	
//	@Override
//	public String toString() {
//		int n = rows.size();
//		int[] sizes = new int[2*n-1];
//		int m=1;
//		for (ArrayList<BigInteger> row : rows) {
//			int k=1;
//			for (BigInteger elem : row) {
//				int i = n-m+2*(k-1);
//				int lastSize = sizes[i];
//				int size = elem.toString().length();
//				if (size > lastSize) {
//					sizes[i] = size;
//				}
//				k++;
//			}
//			m++;
//		}
//		
//		StringBuffer buf = new StringBuffer();
//		m=1;
//		for (ArrayList<BigInteger> row : rows) {
//			StringBuffer rowBuf = new StringBuffer();
//			for (int i=0;i<n-m;i++) {
//				appendSpaces(rowBuf, sizes[i]);
//			}
//			
//			int k=1;
//			for (BigInteger elem : row) {
//				int i = n-m+2*(k-1);
//				int size = sizes[i];
//				appendCentered(rowBuf, size, elem);
//				k++;
//			}
//			buf.append(rowBuf.toString());
//			buf.append('\n'); // line feed
//			m++;
//		}
//		return buf.toString();
//	}
//
//	private static void appendCentered(StringBuffer buf, int totalSize, BigInteger x) {
//		String xStr = x.toString();
//		int xSize = xStr.length();
//		int diff = totalSize-xSize;
//		appendSpaces(buf, diff/2);
//		if (diff%2 != 0) {
//			buf.append(' ');
//		}
//		buf.append(xStr);
//		appendSpaces(buf, 1+diff/2);
//	}
//	
//	private static void appendSpaces(StringBuffer buf, int num) {
//		while (num >= 0) {
//			buf.append(' ');
//			num--;
//		}
//	}
}
