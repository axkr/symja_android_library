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
package de.tilman_neumann.jml.factor.base.congruence;

import java.util.Map;

import de.tilman_neumann.util.Multiset;

public class CongruenceCollectorReport {
	private int partialCount;
	private int smoothCount;
	private int[] smoothFromPartialCounts;
	private int[] partialCounts;
	private int perfectSmoothCount;
	private Multiset<Integer> oddExpBigFactorSizes;
	private Multiset<Integer> oddExpBigFactorSizes4Smooth;
	private int partialWithPositiveQCount;
	private int smoothWithPositiveQCount;
	
	public CongruenceCollectorReport(int partialCount, int smoothCount, int[] smoothFromPartialCounts, int[] partialCounts, int perfectSmoothCount,
			                         Multiset<Integer> oddExpBigFactorSizes, Multiset<Integer> oddExpBigFactorSizes4Smooth,
			                         int partialWithPositiveQCount, int smoothWithPositiveQCount) {
		
		this.partialCount = partialCount;
		this.smoothCount = smoothCount;
		this.smoothFromPartialCounts = smoothFromPartialCounts;
		this.partialCounts = partialCounts;
		this.perfectSmoothCount = perfectSmoothCount;
		this.oddExpBigFactorSizes = oddExpBigFactorSizes;
		this.oddExpBigFactorSizes4Smooth = oddExpBigFactorSizes4Smooth;
		this.partialWithPositiveQCount = partialWithPositiveQCount;
		this.smoothWithPositiveQCount = smoothWithPositiveQCount;
	}
	
	public String getOperationDetails() {
		String smoothFromPartialsStr = smoothFromPartialCounts[0] + " from 1-partials";
		if (smoothFromPartialCounts[1]>0) smoothFromPartialsStr += ", " + smoothFromPartialCounts[1] + " involving 2-partials";
		if (smoothFromPartialCounts[2]>0) smoothFromPartialsStr += ", " + smoothFromPartialCounts[2] + " involving 3-partials";
		String partialsStr = partialCounts[0] + " 1-partials";
		if (partialCounts[1]>0) partialsStr += ", " + partialCounts[1] + " 2-partials";
		if (partialCounts[2]>0) partialsStr += ", " + partialCounts[2] + " 3-partials";
		return "found " + smoothCount + " smooth congruences (" + perfectSmoothCount + " perfect, " 
			   + smoothFromPartialsStr + ") and " + partialCount + " partials (" + partialsStr + ")";
	}
	
	public String getPartialBigFactorSizes() {
		return "Big factor sizes of collected partials: " + oddExpBigFactorSizes;
	}
	
	public String getSmoothBigFactorSizes() {
		return "Big factor sizes of discovered smooths: " + oddExpBigFactorSizes4Smooth;
	}
	
	public String getNonIntFactorPercentages() {
		int totalPartialBigFactorCount = 0;
		int nonIntPartialBigFactorCount = 0;
		for (Map.Entry<Integer, Integer> entry : oddExpBigFactorSizes.entrySet()) {
			int size = entry.getKey();
			int count = entry.getValue();
			totalPartialBigFactorCount += count;
			if (size > 31) {
				nonIntPartialBigFactorCount += count;
			}
		}
		float partialPercentage = (nonIntPartialBigFactorCount*100.0F) / totalPartialBigFactorCount;
		
		int totalSmoothBigFactorCount = 0;
		int nonIntSmoothBigFactorCount = 0;
		for (Map.Entry<Integer, Integer> entry : oddExpBigFactorSizes4Smooth.entrySet()) {
			int size = entry.getKey();
			int count = entry.getValue();
			totalSmoothBigFactorCount += count;
			if (size > 31) {
				nonIntSmoothBigFactorCount += count;
			}
		}
		float smoothPercentage = (nonIntSmoothBigFactorCount*100.0F) / totalSmoothBigFactorCount;
		return String.format("%.2f", smoothPercentage) + "% of smooths' big factors and " + String.format("%.2f", partialPercentage) + "% of partials' big factors are > 31 bit";
	}
	
	public String getPartialQSignCounts() {
		float partialWithPositiveQPercentage = partialWithPositiveQCount*100.0F / partialCount;
		return partialWithPositiveQCount + " partials (" + String.format("%.2f", partialWithPositiveQPercentage) + "%) had positive Q, " + (partialCount-partialWithPositiveQCount) + " partials (" + String.format("%.2f", 100-partialWithPositiveQPercentage) + "%) had negative Q";
	}
	
	public String getSmoothQSignCounts() {
		float smoothWithPositiveQPercentage = smoothWithPositiveQCount*100.0F / smoothCount;
		return smoothWithPositiveQCount + " smooths (" + String.format("%.2f", smoothWithPositiveQPercentage) + "%) had positive Q, " + (smoothCount-smoothWithPositiveQCount) + " smooths (" + String.format("%.2f", 100-smoothWithPositiveQPercentage) + "%) had negative Q";
	}
}
