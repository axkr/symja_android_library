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
package de.tilman_neumann.jml.factor.siqs.poly;

/**
 * Reports about a polynomial generator.
 * @author Tilman Neumann
 */
public class PolyReport {
	private int aParamCount;
	private int bParamCount;
	
	private long aDuration;
	private long firstBDuration;
	private long filterPBDuration;
	private long firstXArrayDuration;
	private long nextBDuration;
	private long nextXArrayDuration;
	
	public PolyReport(int aParamCount, int bParamCount, long aDuration, long firstBDuration, long filterPBDuration, long firstXArrayDuration, long nextBDuration, long nextXArrayDuration) {
		this.aParamCount = aParamCount;
		this.bParamCount = bParamCount;
		this.aDuration = aDuration;
		this.firstBDuration = firstBDuration;
		this.filterPBDuration = filterPBDuration;
		this.firstXArrayDuration = firstXArrayDuration;
		this.nextBDuration = nextBDuration;
		this.nextXArrayDuration = nextXArrayDuration;
	}
	
	/**
	 * Add two reports.
	 * @param other another report added to this
	 */
	public void add(PolyReport other) {
		this.aParamCount += other.aParamCount;
		this.bParamCount += other.bParamCount;
		this.aDuration += other.aDuration;
		this.firstBDuration += other.firstBDuration;
		this.filterPBDuration += other.filterPBDuration;
		this.firstXArrayDuration += other.firstXArrayDuration;
		this.nextBDuration += other.nextBDuration;
		this.nextXArrayDuration += other.nextXArrayDuration;
	}
	
	public String getOperationDetails() {
		return "#a-parameters = " + aParamCount + ", #processed polynomials = " + bParamCount;
	}
	
	public long getTotalDuration(int numberOfThreads) {
		return (aDuration + firstBDuration + filterPBDuration + firstXArrayDuration + nextBDuration + nextXArrayDuration)/numberOfThreads;
	}
	
	public String getPhaseTimings(int numberOfThreads) {
		return "a-param=" + aDuration/numberOfThreads + "ms, first b-param=" + firstBDuration/numberOfThreads
				+ "ms, filter prime base=" + filterPBDuration/numberOfThreads + "ms, first x-arrays=" + firstXArrayDuration/numberOfThreads 
				+ "ms, next b-params=" + nextBDuration/numberOfThreads + "ms, next x-arrays=" + nextXArrayDuration/numberOfThreads + "ms";
	}
}
