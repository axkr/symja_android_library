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
package de.tilman_neumann.jml.quaternion;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

import java.math.BigInteger;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.jml.base.BigRational;
import de.tilman_neumann.util.Ensure;

/**
 * Quaternions are an extension of complex numbers to four dimensions defined as Q(i,j,k) = {x + y*i + z*j + w*k : x,y,z,w ∈ R and i^2 = j^2 = k^2 = ijk = -1}.
 * The Hurwitz quaternions are quaternions with all x,y,z,w ∈ Z or all x,y,z,w ∈ Z/2. That is, either x,y,z,w are all integers, or they are all half-integers.
 * Unlike the general quaternions, the Hurwitz quaternions form an Euclidean ring, i.e. have an Euclidean algorithm.
 * Actually they have two of them because multiplication is not commutative.
 * 
 * @author Tilman Neumann
 * 
 * @see https://de.wikipedia.org/wiki/Hurwitzquaternion (most detailed but in german)
 * @see https://en.wikipedia.org/wiki/Quaternion
 * @see https://en.wikipedia.org/wiki/Hurwitz_quaternion
 */
public class HurwitzQuaternion {

	private static final Logger LOG = LogManager.getLogger(HurwitzQuaternion.class);
	
	private static final boolean DEBUG = false;
	
	/** The coefficients (twice the true coefficients if isLipschitz==false) */
	private BigInteger x, y, z, w;
	
	/** true if this is a Lipschitz integer, i.e. all coefficients are integer */
	private boolean isLipschitz;
	
	/**
	 * Constructor for a Hurwitz quaternion with real part only,
	 * h = (x, 0, 0, 0), if isLipschitz is true, or
	 * h = (x/2, 0, 0, 0), if isLipschitz is false.
	 * 
	 * @param x real part
	 * @param isLipschitz
	 */
	public HurwitzQuaternion(BigInteger x, boolean isLipschitz) {
		this.x = x;
		this.y = I_0;
		this.z = I_0;
		this.w = I_0;
		this.isLipschitz = isLipschitz;
		if (!isLipschitz && !x.testBit(0)) {
			throw new IllegalArgumentException(this + " is not a valid Hurwitz quaternion, integer and half-integer coefficients are mixed");
		}
	}

	/**
	 * Constructor for a Hurwitz quaternion
	 * h = (x, y, z, w), if isLipschitz is true, or
	 * h = (x/2, y/2, z/2, w/2), if isLipschitz is false.
	 * 
	 * @param x real part
	 * @param y 
	 * @param z
	 * @param w
	 * @param isLipschitz
	 */
	public HurwitzQuaternion(BigInteger x, BigInteger y, BigInteger z, BigInteger w, boolean isLipschitz) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		this.isLipschitz = isLipschitz;
		if (!isLipschitz && (!x.testBit(0) || !y.testBit(0) || !z.testBit(0) || !w.testBit(0))) {
			throw new IllegalArgumentException(this + " is not a valid Hurwitz quaternion, integer and half-integer coefficients are mixed");
		}
	}
	
	/**
	 * Constructor for a Hurwitz quaternion from a general quaterion with rational coefficients.
	 * @param r general quaterion with rational coefficients
	 */
	public HurwitzQuaternion(RationalQuaternion r) {
		BigRational rx = r.getX().normalize();
		BigRational ry = r.getY().normalize();
		BigRational rz = r.getZ().normalize();
		BigRational rw = r.getW().normalize();
		BigInteger rxDen = rx.getDenominator();
		BigInteger ryDen = ry.getDenominator();
		BigInteger rzDen = rz.getDenominator();
		BigInteger rwDen = rw.getDenominator();

		if (rxDen.equals(I_1) && ryDen.equals(I_1) && rzDen.equals(I_1) && rwDen.equals(I_1)) {
			this.x = rx.getNumerator();
			this.y = ry.getNumerator();
			this.z = rz.getNumerator();
			this.w = rw.getNumerator();
			this.isLipschitz = true;
			return;
		}
		if (rxDen.equals(I_2) && ryDen.equals(I_2) && rzDen.equals(I_2) && rwDen.equals(I_2)) {
			this.x = rx.getNumerator();
			this.y = ry.getNumerator();
			this.z = rz.getNumerator();
			this.w = rw.getNumerator();
			this.isLipschitz = false;
			return;
		}

		throw new IllegalArgumentException(r + " is not a valid Hurwitz quaternion, integer and half-integer coefficients are mixed");
	}

	public BigInteger getX() {
		return x;
	}
	
	public BigInteger getY() {
		return y;
	}
	
	public BigInteger getZ() {
		return z;
	}
	
	public BigInteger getW() {
		return w;
	}
	
	public HurwitzQuaternion conjugate() {
		return new HurwitzQuaternion(x, y.negate(), z.negate(), w.negate(), isLipschitz);
	}
	
	public HurwitzQuaternion negate() {
		return new HurwitzQuaternion(x.negate(), y.negate(), z.negate(), w.negate(), isLipschitz);
	}
	
	/**
	 * The norm of a Hurwitz quaternion is its product with its conjugate (and in this special case the multiplication is commutative):
	 * N(x+yi+zj+wk) = (x+yi+zj+wk)*(x-yi-zj-wk) = x^2 + y^2 + z^2 + w^2, a pure integer.
	 * 
	 * @return the norm of this
	 */
	public BigInteger norm() {
		BigInteger squareSum = x.multiply(x).add(y.multiply(y)).add(z.multiply(z)).add(w.multiply(w));
		return isLipschitz ? squareSum : squareSum.shiftRight(2);
	}

	public boolean isZero() {
		return x.equals(I_0) && y.equals(I_0) && z.equals(I_0) && w.equals(I_0);
	}
	
	/**
	 * The group of units in H is a nonabelian group of order 24 known as the binary tetrahedral group.
	 * The elements of this group include the 8 elements of the quaternion group Q = {±1, ±i, ±j, ±k} along with the 16 quaternions {(±1 ± i ± j ± k)/2},
	 * where signs may be taken in any combination.
	 * 
	 * @return true if this is one of the 24 units of H.
	 */
	public boolean isUnit() {
		return norm().equals(I_1);
	}
	
	public HurwitzQuaternion add(HurwitzQuaternion b) {
		if (this.isLipschitz) {
			if (b.isLipschitz) {
				// all coefficients of inputs and outputs are integer
				return new HurwitzQuaternion(x.add(b.x), y.add(b.y), z.add(b.z), w.add(b.w), true);
			} else {
				BigInteger cx = x.shiftLeft(1).add(b.x);
				BigInteger cy = y.shiftLeft(1).add(b.y);
				BigInteger cz = z.shiftLeft(1).add(b.z);
				BigInteger cw = w.shiftLeft(1).add(b.w);
				// all computed c-coefficients are odd, and the true coefficients are the half of them
				if (DEBUG) {
					Ensure.ensureTrue(cx.testBit(0));
					Ensure.ensureTrue(cy.testBit(0));
					Ensure.ensureTrue(cz.testBit(0));
					Ensure.ensureTrue(cw.testBit(0));
				}
				return new HurwitzQuaternion(cx, cy, cz, cw, false);
			}
		} else {
			if (b.isLipschitz) {
				BigInteger cx = x.add(b.x.shiftLeft(1));
				BigInteger cy = y.add(b.y.shiftLeft(1));
				BigInteger cz = z.add(b.z.shiftLeft(1));
				BigInteger cw = w.add(b.w.shiftLeft(1));
				// all computed c-coefficients are odd, and the true coefficients are the half of them
				if (DEBUG) {
					Ensure.ensureTrue(cx.testBit(0));
					Ensure.ensureTrue(cy.testBit(0));
					Ensure.ensureTrue(cz.testBit(0));
					Ensure.ensureTrue(cw.testBit(0));
				}
				return new HurwitzQuaternion(cx, cy, cz, cw, false);
			} else {
				BigInteger cx = x.add(b.x);
				BigInteger cy = y.add(b.y);
				BigInteger cz = z.add(b.z);
				BigInteger cw = w.add(b.w);
				// all computed c-coefficients are divisible by 2, and the true coefficients are the half of them
				if (DEBUG) {
					Ensure.ensureFalse(cx.testBit(0));
					Ensure.ensureFalse(cy.testBit(0));
					Ensure.ensureFalse(cz.testBit(0));
					Ensure.ensureFalse(cw.testBit(0));
				}
				return new HurwitzQuaternion(cx.shiftRight(1), cy.shiftRight(1), cz.shiftRight(1), cw.shiftRight(1), true);
			}
		}
	}
	
	public HurwitzQuaternion subtract(HurwitzQuaternion b) {
		if (this.isLipschitz) {
			if (b.isLipschitz) {
				// all coefficients of inputs and outputs are integer
				return new HurwitzQuaternion(x.subtract(b.x), y.subtract(b.y), z.subtract(b.z), w.subtract(b.w), true);
			} else {
				BigInteger cx = x.shiftLeft(1).subtract(b.x);
				BigInteger cy = y.shiftLeft(1).subtract(b.y);
				BigInteger cz = z.shiftLeft(1).subtract(b.z);
				BigInteger cw = w.shiftLeft(1).subtract(b.w);
				// all computed c-coefficients are odd, and the true coefficients are the half of them
				if (DEBUG) {
					Ensure.ensureTrue(cx.testBit(0));
					Ensure.ensureTrue(cy.testBit(0));
					Ensure.ensureTrue(cz.testBit(0));
					Ensure.ensureTrue(cw.testBit(0));
				}
				return new HurwitzQuaternion(cx, cy, cz, cw, false);
			}
		} else {
			if (b.isLipschitz) {
				BigInteger cx = x.subtract(b.x.shiftLeft(1));
				BigInteger cy = y.subtract(b.y.shiftLeft(1));
				BigInteger cz = z.subtract(b.z.shiftLeft(1));
				BigInteger cw = w.subtract(b.w.shiftLeft(1));
				// all computed c-coefficients are odd, and the true coefficients are the half of them
				if (DEBUG) {
					Ensure.ensureTrue(cx.testBit(0));
					Ensure.ensureTrue(cy.testBit(0));
					Ensure.ensureTrue(cz.testBit(0));
					Ensure.ensureTrue(cw.testBit(0));
				}
				return new HurwitzQuaternion(cx, cy, cz, cw, false);
			} else {
				BigInteger cx = x.subtract(b.x);
				BigInteger cy = y.subtract(b.y);
				BigInteger cz = z.subtract(b.z);
				BigInteger cw = w.subtract(b.w);
				// all computed c-coefficients are divisible by 2, and the true coefficients are the half of them
				if (DEBUG) {
					Ensure.ensureFalse(cx.testBit(0));
					Ensure.ensureFalse(cy.testBit(0));
					Ensure.ensureFalse(cz.testBit(0));
					Ensure.ensureFalse(cw.testBit(0));
				}
				return new HurwitzQuaternion(cx.shiftRight(1), cy.shiftRight(1), cz.shiftRight(1), cw.shiftRight(1), true);
			}
		}
	}

	public HurwitzQuaternion multiply(BigInteger b) {
		BigInteger cx = this.x.multiply(b);
		BigInteger cy = this.y.multiply(b);
		BigInteger cz = this.z.multiply(b);
		BigInteger cw = this.w.multiply(b);
		if (this.isLipschitz) {
			return new HurwitzQuaternion(cx, cy, cz, cw, true);
		} else {
			if (cx.testBit(0) == false) {
				// all coefficients must be even. We divide them by 2 and set isLipschitz==true
				if (DEBUG) {
					Ensure.ensureFalse(cx.testBit(0));
					Ensure.ensureFalse(cy.testBit(0));
					Ensure.ensureFalse(cz.testBit(0));
					Ensure.ensureFalse(cw.testBit(0));
				}
				return new HurwitzQuaternion(cx.shiftRight(1), cy.shiftRight(1), cz.shiftRight(1), cw.shiftRight(1), true);
			} else {
				// all coefficients must be odd
				if (DEBUG) {
					Ensure.ensureTrue(cx.testBit(0));
					Ensure.ensureTrue(cy.testBit(0));
					Ensure.ensureTrue(cz.testBit(0));
					Ensure.ensureTrue(cw.testBit(0));
				}
				return new HurwitzQuaternion(cx, cy, cz, cw, false);
			}
		}
	}
	
	public HurwitzQuaternion multiply(HurwitzQuaternion b) {
		BigInteger ax=x, ay=y, az=z, aw=w;
		BigInteger bx=b.x, by=b.y, bz=b.z, bw=b.w;
		// The (Hamilton) product of two ordinary quaternions a=(ax, ay, az, aw) and b=(bx, by, bz, bw) is
		//    ax*bx - ay*by - az*bz - aw*bw
		// + (ax*by + ay*bx + az*bw - aw*bz) * i
		// + (ax*bz - ay*bw + az*bx + aw*by) * j
		// + (ax*bw + ay*bz - az*by + aw*bx) * k
		BigInteger cx = ax.multiply(bx).subtract(ay.multiply(by)).subtract(az.multiply(bz)).subtract(aw.multiply(bw));
		BigInteger cy = ax.multiply(by).add(     ay.multiply(bx)).add(     az.multiply(bw)).subtract(aw.multiply(bz));
		BigInteger cz = ax.multiply(bz).subtract(ay.multiply(bw)).add(     az.multiply(bx)).add(     aw.multiply(by));
		BigInteger cw = ax.multiply(bw).add(     ay.multiply(bz)).subtract(az.multiply(by)).add(     aw.multiply(bx));
		
		// For Hurwitz quaternions we have to take into account that the true coefficients may be half-integers, but our stored a,b,c,d-values are the double of them.
		int rightShifts = (this.isLipschitz ? 0 : 1) + (b.isLipschitz ? 0 : 1);
		if (rightShifts == 2) {
			// The true values are 1/4 of the values computed so far.
			// The values computed so far must be even because in each line there are 3 (1) positive and 1 (3) negative odd summands.
			if (DEBUG) {
				Ensure.ensureFalse(cx.testBit(0));
				Ensure.ensureFalse(cy.testBit(0));
				Ensure.ensureFalse(cz.testBit(0));
				Ensure.ensureFalse(cw.testBit(0));
			}
			// Do one right shift
			cx = cx.shiftRight(1);
			cy = cy.shiftRight(1);
			cz = cz.shiftRight(1);
			cw = cw.shiftRight(1);
		}
		
		if (rightShifts > 0) {
			// Now all coefficients must be even or all coefficients must be odd. Depending on that we do another right shift or declare the current coefficients as the numerators of half-integers
			if (cx.testBit(0) == false) {
				if (DEBUG) {
					Ensure.ensureFalse(cx.testBit(0));
					Ensure.ensureFalse(cy.testBit(0));
					Ensure.ensureFalse(cz.testBit(0));
					Ensure.ensureFalse(cw.testBit(0));
				}
				cx = cx.shiftRight(1);
				cy = cy.shiftRight(1);
				cz = cz.shiftRight(1);
				cw = cw.shiftRight(1);
				return new HurwitzQuaternion(cx, cy, cz, cw, true);
			} else {
				if (DEBUG) {
					Ensure.ensureTrue(cx.testBit(0));
					Ensure.ensureTrue(cy.testBit(0));
					Ensure.ensureTrue(cz.testBit(0));
					Ensure.ensureTrue(cw.testBit(0));
				}
				return new HurwitzQuaternion(cx, cy, cz, cw, false);
			}
		}
		
		// rightShifts == 0
		return new HurwitzQuaternion(cx, cy, cz, cw, true);
	}
	
	public HurwitzQuaternion square() {
		// The (Hamilton) square of an ordinary quaternion a=(ax, ay, az, aw) is
		//    ax*ax - ay*ay - az*az - aw*aw
		// + (ax*ay + ay*ax + az*aw - aw*az) * i
		// + (ax*az - ay*aw + az*ax + aw*ay) * j
		// + (ax*aw + ay*az - az*ay + aw*ax) * k
		//
		// = (ax*ax - ay*ay - az*az - aw*aw) + (2*ax*ay) * i + (2*ax*az) * j + (2*ax*aw) * k

		BigInteger ax = this.x;
		BigInteger ay = this.y;
		BigInteger az = this.z;
		BigInteger aw = this.w;
		
		BigInteger cx = ax.multiply(ax).subtract(ay.multiply(ay)).subtract(az.multiply(az)).subtract(aw.multiply(aw));
		// For cy, cz and cw we do not multiply by 2 yet because they would get reduced by 2 again if this is not a Lipschitz integer
		BigInteger cy = ax.multiply(ay);
		BigInteger cz = ax.multiply(az);
		BigInteger cw = ax.multiply(aw);
		
		if (isLipschitz) {
			// We need the standard quaternion coefficients, i.e. have to catch up on the doubling of cy, cz and cw
			return new HurwitzQuaternion(cx, cy.shiftLeft(1), cz.shiftLeft(1), cw.shiftLeft(1), true);
		} else {
			// Do one right shift for cx; the other coefficients are already reduced by 2
			cx = cx.shiftRight(1);
			
			// Now all coefficients must be odd
			if (DEBUG) {
				Ensure.ensureTrue(cx.testBit(0));
				Ensure.ensureTrue(cy.testBit(0));
				Ensure.ensureTrue(cz.testBit(0));
				Ensure.ensureTrue(cw.testBit(0));
			}
			return new HurwitzQuaternion(cx, cy, cz, cw, false);
		}
	}
	
	/**
	 * "left-divide" this by b. In quaternions, there are two division algorithms, because multiplication is not commutative.
	 * The left-division variant computes a/b = conjugate(b)*a / N(b).
	 * It's resulting quotient is apt for the <em>right</em> term in a complementary test multiplication.
	 * 
	 * @param b
	 * @return [quotient, remainder] of left division
	 */
	public HurwitzQuaternion[] leftDivide(HurwitzQuaternion b) {
		if (DEBUG) LOG.debug("leftDivide (" + this + ") / (" + b + ") with N(" + b + ") = " + b.norm());
		
		if (b.isZero()) throw new ArithmeticException("division by zero");
				
		// Theorem: Let a, b ∈ Z[i] with b != 0. Then there are c, d ∈ Z[i] for which a = b*c + d and N(d) < N(b).
		// Notes: * The norm gives the required notion that the remainder d be smaller than the divisor b.
		//        * Unlike with the division algorithm in Z, we make no claim that c and d are unique
		
		// cExact = left divide a/b = conjugate(b)*a / (conjugate(b)*b) = conjugate(b)*a / N(b)
		HurwitzQuaternion cExactNum = b.conjugate().multiply(this);
		BigInteger cExactDen = b.norm();
		
		// The exact quotient c lies within 24 elements of the Hurwitz quaternion lattice.
		// We want to choose the surrounding lattice element with minimal N(d).
		// We start with the 8 Lipschitz lattice elements, for which the best value can be computed independently in each lattice dimension.
		BigInteger cExactDen2 = cExactNum.isLipschitz ? cExactDen : cExactDen.shiftLeft(1);
		BigInteger cx = new BigRational(cExactNum.x, cExactDen2).round();
		BigInteger cy = new BigRational(cExactNum.y, cExactDen2).round();
		BigInteger cz = new BigRational(cExactNum.z, cExactDen2).round();
		BigInteger cw = new BigRational(cExactNum.w, cExactDen2).round();
		HurwitzQuaternion cLipschitz = new HurwitzQuaternion(cx, cy, cz, cw, true);
		
		if (DEBUG) {
			// compare with solution computed in rational quaternions
			RationalQuaternion aR = this.toRationalQuaternion();
			RationalQuaternion bR = b.toRationalQuaternion();
			RationalQuaternion cR = bR.inverse().multiply(aR);
			BigInteger cRx = cR.getX().round();
			BigInteger cRy = cR.getY().round();
			BigInteger cRz = cR.getZ().round();
			BigInteger cRw = cR.getW().round();
			HurwitzQuaternion cRLipschitz = new HurwitzQuaternion(cRx, cRy, cRz, cRw, true);
			// cRLipschitz == cLipschitz
			Ensure.ensureEquals(cRLipschitz.x, cLipschitz.x);
			Ensure.ensureEquals(cRLipschitz.y, cLipschitz.y);
			Ensure.ensureEquals(cRLipschitz.z, cLipschitz.z);
			Ensure.ensureEquals(cRLipschitz.w, cLipschitz.w);
			Ensure.ensureEquals(cRLipschitz.isLipschitz, cLipschitz.isLipschitz);
		}
		
		// Compute d = a - c*b for the best Lipschitz solution
		HurwitzQuaternion d = this.subtract(b.multiply(cLipschitz));
		BigInteger dNorm = d.norm();
		
		// Now we have to compare the best Lipschitz lattice element to the 16 surrounding lattice elements with half-integer coefficients
		// XXX The approach from https://de.wikipedia.org/wiki/Hurwitzquaternion might be slightly faster
		HurwitzQuaternion c = cLipschitz;
		for (HurwitzQuaternion halfIntegerUnit : HurwitzQuaternionConstants.HALF_INTEGER_UNITS) {
			HurwitzQuaternion c2 = cLipschitz.add(halfIntegerUnit);
			if (DEBUG) Ensure.ensureFalse(c2.isLipschitz);
			HurwitzQuaternion d2 = this.subtract(b.multiply(c2));
			BigInteger d2Norm = d2.norm();
			if (d2Norm.compareTo(dNorm) < 0) {
				// Found a better choice
				c = c2;
				d = d2;
				dNorm = d2Norm;
			}
		}
		
		if (DEBUG) LOG.debug("result: c = " + c + ", d = " + d + ", N(d) = " + dNorm);

		// Make sure that N(d) < N(b)
		if (DEBUG) Ensure.ensureSmaller(dNorm, b.norm());
		
		return new HurwitzQuaternion[] {c, d};
	}

	/**
	 * "right-divide" this by b. In quaternions, there are two division algorithms, because multiplication is not commutative.
	 * The right-division variant computes a/b = a*conjugate(b) / N(b).
	 * It's resulting quotient is apt for the <em>left</em> term in a complementary test multiplication.
	 * 
	 * @param b
	 * @return [quotient, remainder]
	 */
	public HurwitzQuaternion[] rightDivide(HurwitzQuaternion b) {
		if (DEBUG) LOG.debug("leftDivide (" + this + ") / (" + b + ") with N(" + b + ") = " + b.norm());
		
		if (b.isZero()) throw new ArithmeticException("division by zero");
		
		// Theorem: Let a, b ∈ Z[i] with b != 0. Then there are c, d ∈ Z[i] for which a = b*c + d and N(d) < N(b).
		// Notes: * The norm gives the required notion that the remainder d be smaller than the divisor b.
		//        * Unlike with the division algorithm in Z, we make no claim that c and d are unique
		
		// cExact = right divde a/b = a*conjugate(b) / (b*conjugate(b)) = a*conjugate(b) / N(b)
		HurwitzQuaternion cExactNum = this.multiply(b.conjugate());
		BigInteger cExactDen = b.norm();
		
		// The exact quotient c lies within 24 elements of the Hurwitz quaternion lattice.
		// We want to choose the surrounding lattice element with minimal N(d).
		// We start with the 8 Lipschitz lattice elements, for which the best value can be computed independently in each lattice dimension.
		BigInteger cExactDen2 = cExactNum.isLipschitz ? cExactDen : cExactDen.shiftLeft(1);
		BigInteger cx = new BigRational(cExactNum.x, cExactDen2).round();
		BigInteger cy = new BigRational(cExactNum.y, cExactDen2).round();
		BigInteger cz = new BigRational(cExactNum.z, cExactDen2).round();
		BigInteger cw = new BigRational(cExactNum.w, cExactDen2).round();
		HurwitzQuaternion cLipschitz = new HurwitzQuaternion(cx, cy, cz, cw, true);
		
		// Compute d = a - c*b for the best Lipschitz solution
		HurwitzQuaternion d = this.subtract(cLipschitz.multiply(b));
		BigInteger dNorm = d.norm();
		
		// Now we have to compare the best Lipschitz lattice element to the 16 surrounding lattice elements with half-integer coefficients
		HurwitzQuaternion c = cLipschitz;
		for (HurwitzQuaternion halfIntegerUnit : HurwitzQuaternionConstants.HALF_INTEGER_UNITS) {
			HurwitzQuaternion c2 = cLipschitz.add(halfIntegerUnit);
			if (DEBUG) Ensure.ensureFalse(c2.isLipschitz);
			HurwitzQuaternion d2 = this.subtract(c2.multiply(b));
			BigInteger d2Norm = d2.norm();
			if (d2Norm.compareTo(dNorm) < 0) {
				// Found a better choice
				c = c2;
				d = d2;
				dNorm = d2Norm;
			}
		}

		if (DEBUG) LOG.debug("result: c = " + c + ", d = " + d + ", N(d) = " + d.norm());

		// Make sure that N(d) < N(b)
		if (DEBUG) Ensure.ensureSmaller(d.norm(), b.norm());
		
		return new HurwitzQuaternion[] {c, d};
	}
	
	/**
	 * Computes the left Hurwitz quaternion gcd of this and b.
	 * The result is determined except for a right-sided multiplication with one of the 24 Hurwitz units.
	 * 
	 * @param b
	 * @return left gcd(this, b)
	 */
	public HurwitzQuaternion leftGcd(HurwitzQuaternion b) {
		HurwitzQuaternion d;
		
		if (this.isZero()) {
			d = b; // gcd(0, n) = n
		} else {
			HurwitzQuaternion a = this;
			while (!b.isZero()) {
				if (DEBUG) LOG.debug("gcd: a = " + a + ", b = " + b);
				HurwitzQuaternion[] divRem = a.leftDivide(b);
				a = b;
				b = divRem[1];
			}
			d = a; // preliminary result (up to units)
		}
		
		// Here we could choose any particular result obtainable by right multiplications with Hurwitz units;
		// in particular we could always choose a Lipschitz quaternion.
		return d;
	}
	
	/**
	 * Computes the right Hurwitz quaternion gcd of this and b.
	 * The result is determined except for a left-sided multiplication with one of the 24 Hurwitz units.
	 * 
	 * @param b
	 * @return right gcd(this, b)
	 */
	public HurwitzQuaternion rightGcd(HurwitzQuaternion b) {
		HurwitzQuaternion d;
		
		if (this.isZero()) {
			d = b; // gcd(0, n) = n
		} else {
			HurwitzQuaternion a = this;
			while (!b.isZero()) {
				if (DEBUG) LOG.debug("gcd: a = " + a + ", b = " + b);
				HurwitzQuaternion[] divRem = a.rightDivide(b);
				a = b;
				b = divRem[1];
			}
			d = a; // preliminary result (up to units)
		}
		
		// Here we could choose any particular result obtainable by right multiplications with Hurwitz units;
		// in particular we could always choose a Lipschitz quaternion.
		return d;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o==null || !(o instanceof HurwitzQuaternion)) return false;
		HurwitzQuaternion b = (HurwitzQuaternion) o;
		return x.equals(b.x) && y.equals(b.y) && z.equals(b.z) && w.equals(b.w) && isLipschitz==b.isLipschitz;
	}
	
	@Override
	public int hashCode() {
		long ret = x.longValue();
		ret = 31*ret + y.longValue();
		ret = 31*ret + z.longValue();
		ret = 31*ret + w.longValue();
		ret = 31*ret + (isLipschitz ? 0 : 1);
		return (int) (ret);
	}
	
	public RationalQuaternion toRationalQuaternion() {
		BigRational rx = new BigRational(x);
		BigRational ry = new BigRational(y);
		BigRational rz = new BigRational(z);
		BigRational rw = new BigRational(w);
		if (!isLipschitz) {
			rx = rx.divide(I_2);
			ry = ry.divide(I_2);
			rz = rz.divide(I_2);
			rw = rw.divide(I_2);
		}
		return new RationalQuaternion(rx, ry, rz, rw);
	}
	
	@Override
	public String toString() {
		String str = x.toString() + (isLipschitz ? "" : "/2");
		str += " " + (y.signum() < 0 ? "-" : "+") + " " + y.abs() + (isLipschitz ? "" : "/2") + "*i";
		str += " " + (z.signum() < 0 ? "-" : "+") + " " + z.abs() + (isLipschitz ? "" : "/2") + "*j";
		str += " " + (w.signum() < 0 ? "-" : "+") + " " + w.abs() + (isLipschitz ? "" : "/2") + "*k";
		return str;
	}
}
