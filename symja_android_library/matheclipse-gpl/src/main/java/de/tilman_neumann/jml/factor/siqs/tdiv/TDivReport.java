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
package de.tilman_neumann.jml.factor.siqs.tdiv;

import static de.tilman_neumann.jml.factor.base.GlobalFactoringOptions.*;

public class TDivReport {
	private long testCount;
	private long sufficientSmoothCount;
	private long aqDuration;
	private long pass1Duration;
	private long pass2Duration;
	private long primeTestDuration;
	private long factorDuration;
	
	public TDivReport(long testCount, long sufficientSmoothCount, long aqDuration, long pass1Duration, long pass2Duration, long primeTestDuration, long factorDuration) {
		if (ANALYZE) {
			this.testCount = testCount;
			this.sufficientSmoothCount = sufficientSmoothCount;
			this.aqDuration = aqDuration;
			this.pass1Duration = pass1Duration;
			this.pass2Duration = pass2Duration;
			this.primeTestDuration = primeTestDuration;
			this.factorDuration = factorDuration;
		}
	}
	
	/**
	 * Add two reports.
	 * @param other another report added to this
	 */
	public void add(TDivReport other) {
		if (ANALYZE) {
			this.testCount += other.testCount;
			this.sufficientSmoothCount += other.sufficientSmoothCount;
			this.aqDuration += other.aqDuration;
			this.pass1Duration += other.pass1Duration;
			this.pass2Duration += other.pass2Duration;
			this.primeTestDuration += other.primeTestDuration;
			this.factorDuration += other.factorDuration;
		}
	}

	public String getOperationDetails() {
		float percentage = ((int) (0.5F + sufficientSmoothCount*10000 / (float) testCount)) / 100F; // 2 after-comma digits
		return "tested " + testCount + " congruence candidates and let " + sufficientSmoothCount + " (" + percentage + "%) pass";
	}
	
	public long getTotalDuration(int numberOfThreads) {
		return (aqDuration + pass1Duration + pass2Duration + primeTestDuration + factorDuration) / numberOfThreads;
	}
	
	public String getPhaseTimings(int numberOfThreads) {
		return "AQ=" + aqDuration/numberOfThreads + "ms, pass1=" + pass1Duration/numberOfThreads + "ms, pass2=" + pass2Duration/numberOfThreads + "ms, primeTest=" + primeTestDuration/numberOfThreads + "ms, factor=" + factorDuration/numberOfThreads + "ms";
	}
}
