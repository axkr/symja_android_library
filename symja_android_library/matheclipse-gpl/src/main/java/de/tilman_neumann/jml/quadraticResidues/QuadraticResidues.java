/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018-2024 Tilman Neumann - tilman.neumann@web.de
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
package de.tilman_neumann.jml.quadraticResidues;

import java.util.TreeSet;

/**
 * Methods to generate quadratic residues or test for quadratic residuosity for general moduli m.
 * 
 * @author Tilman Neumann
 */
public class QuadraticResidues {

    /**
     * Return all quadratic residues modulo m, computed by brute force.
     * 
     * @param m
     * @return set of quadratic residues modulo m, sorted bottom up.
     */
    public static TreeSet<Long> getQuadraticResidues(long m) {
    	TreeSet<Long> quadraticResidues = new TreeSet<Long>();
    	for (long k=0; k<=m/2; k++) {
    		quadraticResidues.add(k*k % m);
    	}
    	return quadraticResidues;
    }
    
    /**
     * Get the quadratic residues of even "k" modulo m, computed by brute force.
     * 
     * @param m
     * @return square residues generated from even k^2 modulo m
     */
    public static TreeSet<Long> getEvenQuadraticResidues(long m) {
    	TreeSet<Long> quadraticResidues = new TreeSet<Long>();
    	for (long k=0; k<=m/2; k+=2) {
    		quadraticResidues.add(k*k % m);
    	}
    	return quadraticResidues;
    }
}
