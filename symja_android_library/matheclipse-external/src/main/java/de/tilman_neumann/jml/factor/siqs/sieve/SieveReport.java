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
package de.tilman_neumann.jml.factor.siqs.sieve;

public class SieveReport {
	private long initDuration;
	private long sieveDuration;
	private long collectDuration;

	public SieveReport(long initDuration, long sieveDuration, long collectDuration) {
		this.initDuration = initDuration;
		this.sieveDuration = sieveDuration;
		this.collectDuration = collectDuration;
	}
	
	/**
	 * Add two reports.
	 * @param other another report added to this
	 */
	public void add(SieveReport other) {
		this.initDuration += other.initDuration;
		this.sieveDuration += other.sieveDuration;
		this.collectDuration += other.collectDuration;
	}
	
	public long getTotalDuration(int numberOfThreads) {
		return (initDuration + sieveDuration + collectDuration)/numberOfThreads;
	}

	public String getPhaseTimings(int numberOfThreads) {
		return "init=" + initDuration/numberOfThreads + "ms, sieve=" + sieveDuration/numberOfThreads + "ms, collect=" + collectDuration/numberOfThreads + "ms";
	}
}
