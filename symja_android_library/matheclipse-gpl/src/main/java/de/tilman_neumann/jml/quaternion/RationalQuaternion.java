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

/**
 * Quaternions are an extension of complex numbers to four dimensions defined as Q(i,j,k) = {x + y*i + z*j + w*k : x,y,z,w ∈ R and i^2 = j^2 = k^2 = ijk = -1}.
 * 
 * @author Tilman Neumann
 */
public class RationalQuaternion {

	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(RationalQuaternion.class);
	
	/** The coefficients */
	private BigRational x, y, z, w;
	
	public RationalQuaternion(BigRational x, BigRational y, BigRational z, BigRational w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	public BigRational getX() {
		return x;
	}
	
	public BigRational getY() {
		return y;
	}
	
	public BigRational getZ() {
		return z;
	}
	
	public BigRational getW() {
		return w;
	}
	
	public RationalQuaternion conjugate() {
		return new RationalQuaternion(x, y.negate(), z.negate(), w.negate());
	}
	
	public RationalQuaternion negate() {
		return new RationalQuaternion(x.negate(), y.negate(), z.negate(), w.negate());
	}
	
	public RationalQuaternion normalize() {
		return new RationalQuaternion(x.normalize(), y.normalize(), z.normalize(), w.normalize());
	}
	
	/**
	 * @return the norm of this
	 */
	public BigRational norm() {
		return x.multiply(x).add(y.multiply(y)).add(z.multiply(z)).add(w.multiply(w));
	}

	public boolean isZero() {
		return x.equals(I_0) && y.equals(I_0) && z.equals(I_0) && w.equals(I_0);
	}
	
	/**
	 * @return true if this is a unit
	 */
	public boolean isUnit() {
		return norm().equals(I_1);
	}
	
	public RationalQuaternion add(RationalQuaternion b) {
		return new RationalQuaternion(x.add(b.x), y.add(b.y), z.add(b.z), w.add(b.w));
	}
	
	public RationalQuaternion subtract(RationalQuaternion b) {
		return new RationalQuaternion(x.subtract(b.x), y.subtract(b.y), z.subtract(b.z), w.subtract(b.w));
	}

	public RationalQuaternion multiply(BigInteger b) {
		BigRational cx = this.x.multiply(b);
		BigRational cy = this.y.multiply(b);
		BigRational cz = this.z.multiply(b);
		BigRational cw = this.w.multiply(b);
		return new RationalQuaternion(cx, cy, cz, cw);
	}

	public RationalQuaternion multiply(BigRational b) {
		BigRational cx = this.x.multiply(b);
		BigRational cy = this.y.multiply(b);
		BigRational cz = this.z.multiply(b);
		BigRational cw = this.w.multiply(b);
		return new RationalQuaternion(cx, cy, cz, cw);
	}
	
	public RationalQuaternion multiply(RationalQuaternion b) {
		BigRational ax=x, ay=y, az=z, aw=w;
		BigRational bx=b.x, by=b.y, bz=b.z, bw=b.w;
		// The (Hamilton) product of two ordinary quaternions a=(ax, ay, az, aw) and b=(bx, by, bz, bw) is
		//    ax*bx - ay*by - az*bz - aw*bw
		// + (ax*by + ay*bx + az*bw - aw*bz) * i
		// + (ax*bz - ay*bw + az*bx + aw*by) * j
		// + (ax*bw + ay*bz - az*by + aw*bx) * k
		BigRational cx = ax.multiply(bx).subtract(ay.multiply(by)).subtract(az.multiply(bz)).subtract(aw.multiply(bw));
		BigRational cy = ax.multiply(by).add(     ay.multiply(bx)).add(     az.multiply(bw)).subtract(aw.multiply(bz));
		BigRational cz = ax.multiply(bz).subtract(ay.multiply(bw)).add(     az.multiply(bx)).add(     aw.multiply(by));
		BigRational cw = ax.multiply(bw).add(     ay.multiply(bz)).subtract(az.multiply(by)).add(     aw.multiply(bx));

		return new RationalQuaternion(cx, cy, cz, cw);
	}
	
	public RationalQuaternion square() {
		// The (Hamilton) square of an ordinary quaternion a=(ax, ay, az, aw) is
		//    ax*ax - ay*ay - az*az - aw*aw
		// + (ax*ay + ay*ax + az*aw - aw*az) * i
		// + (ax*az - ay*aw + az*ax + aw*ay) * j
		// + (ax*aw + ay*az - az*ay + aw*ax) * k
		//
		// = (ax*ax - ay*ay - az*az - aw*aw) + (2*ax*ay) * i + (2*ax*az) * j + (2*ax*aw) * k

		BigRational ax = this.x;
		BigRational ay = this.y;
		BigRational az = this.z;
		BigRational aw = this.w;
		
		BigRational cx = ax.multiply(ax).subtract(ay.multiply(ay)).subtract(az.multiply(az)).subtract(aw.multiply(aw));
		BigRational cy = ax.multiply(ay).multiply(I_2);
		BigRational cz = ax.multiply(az).multiply(I_2);
		BigRational cw = ax.multiply(aw).multiply(I_2);

		return new RationalQuaternion(cx, cy, cz, cw);
	}
	
	/**
	 * The inverse of a quaternion q is the conjugate of q divided coefficient-wise by the norm of q.
	 * 
	 * @return the inverse of this
	 */
	public RationalQuaternion inverse() {
		BigRational reciprocalNorm = this.norm().reciprocal();
		return new RationalQuaternion(x.multiply(reciprocalNorm), y.negate().multiply(reciprocalNorm), z.negate().multiply(reciprocalNorm), w.negate().multiply(reciprocalNorm));
	}
	
	/**
	 * "left-divide" this by b. In quaternions, there are two division algorithms, because multiplication is not commutative.
	 * The left-division variant computes a/b = inverse(b)*a = conjugate(b)*a / N(b).
	 * Its resulting quotient is apt for the <em>right</em> term in a complementary test multiplication.
	 * The division in rational coefficients is exact, i.e. there is no remainder.
	 * 
	 * @param b
	 * @return left division quotient
	 */
	public RationalQuaternion leftDivide(RationalQuaternion b) {
		if (b.isZero()) throw new ArithmeticException("division by zero");
		return b.inverse().multiply(this);
	}

	/**
	 * "right-divide" this by b. In quaternions, there are two division algorithms, because multiplication is not commutative.
	 * The right-division variant computes a/b = a*inverse(b) = a*conjugate(b) / N(b).
	 * Its resulting quotient is apt for the <em>left></em> term in a complementary test multiplication.
	 * The division in rational coefficients is exact, i.e. there is no remainder.
	 * 
	 * @param b
	 * @return right division quotient
	 */
	public RationalQuaternion rightDivide(RationalQuaternion b) {
		if (b.isZero()) throw new ArithmeticException("division by zero");
		return this.multiply(b.inverse());
	}
	
	// TODO built upon a rational gcd, this class could have gcd's, too
	
	@Override
	public boolean equals(Object o) {
		if (o==null || !(o instanceof RationalQuaternion)) return false;
		RationalQuaternion b = (RationalQuaternion) o;
		return x.equals(b.x) && y.equals(b.y) && z.equals(b.z) && w.equals(b.w);
	}
	
	@Override
	public int hashCode() {
		long ret = x.longValue();
		ret = 31*ret + y.longValue();
		ret = 31*ret + z.longValue();
		ret = 31*ret + w.longValue();
		return (int) (ret);
	}
	
	@Override
	public String toString() {
		String str = x.toString();
		str += " " + (y.signum() < 0 ? "-" : "+") + " " + y.abs() + "*i";
		str += " " + (z.signum() < 0 ? "-" : "+") + " " + z.abs() + "*j";
		str += " " + (w.signum() < 0 ? "-" : "+") + " " + w.abs() + "*k";
		return str;
	}
}
