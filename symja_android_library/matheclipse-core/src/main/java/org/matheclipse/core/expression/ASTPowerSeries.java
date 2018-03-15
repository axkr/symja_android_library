package org.matheclipse.core.expression;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.util.HashMap;
import java.util.RandomAccess;
import java.util.Set;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;

public class ASTPowerSeries extends AST implements Cloneable, Externalizable, RandomAccess {

	public ASTPowerSeries() {
		super(F.SeriesData);
		// When Externalizable objects are deserialized, they first need to be constructed by invoking the void
		// constructor. Since this class does not have one, serialization and deserialization will fail at runtime.
	}

	/**
	 * 
	 * Returns a new ASTPowerSeries where each element is mapped by the given function.
	 *
	 * @param astVector
	 *            an AST which could be converted into <code>double[]</code>
	 * @param function
	 *            Function to apply to each entry.
	 * @return a new vector.
	 */
	public static ASTPowerSeries map(final IAST astVector, DoubleUnaryOperator function) {
		HashMap<Integer, IExpr> vector = new HashMap<Integer, IExpr>();
		ASTPowerSeries series = new ASTPowerSeries(vector, false);
		for (int i = 1; i < astVector.size(); i++) {
			vector.put(i, astVector.get(i));
		}
		return series;
	}

	/**
	 * The underlying matrix
	 */
	private Function<Integer, IExpr> f;
	// private HashMap<Integer, IExpr> vals;
	// -1 => undetermined
	private int power = -1;

	private IExpr x;

	public IExpr getX() {
		return x;
	}

	public void setX(IExpr x) {
		this.x = x;
	}

	public IExpr getX0() {
		return x0;
	}

	/** {@inheritDoc} */
	@Override
	public int hierarchy() {
		return SERIESID;
	}

	public void setX0(IExpr x0) {
		this.x0 = x0;
	}

	public long getNMin() {
		return nMin;
	}

	public void setNMin(long nMin) {
		this.nMin = nMin;
	}

	public long getNMax() {
		return nMax;
	}

	public void setNMax(long nMax) {
		this.nMax = nMax;
	}

	public long getDenominator() {
		return denominator;
	}

	public void setDenominator(long denominator) {
		this.denominator = denominator;
	}

	private IExpr x0;
	private long nMin;
	private long nMax;
	private long denominator;

	public ASTPowerSeries(IExpr x, IExpr x0, long nMin, long nMax, long denominator) {
		super(F.SeriesData);
		this.x = x;
		this.x0 = x0;
		this.nMin = nMin;
		this.nMax = nMax;
		this.denominator = denominator;
	}

	/**
	 * 
	 * @param vals
	 * @param deepCopy
	 */
	// public ASTPowerSeries(double[] vector, boolean deepCopy) {
	// this.vector = new ArrayRealVector(vector, deepCopy);
	// }

	IExpr ring;

	public ASTPowerSeries(Function<Integer, IExpr> f, int order, IExpr ring) {
		super(F.SeriesData);
		this.power = order;
		this.f = f;
		// this.vals = new HashMap<Integer, IExpr>();
		this.ring = ring;
	}

	public ASTPowerSeries(Function<Integer, IExpr> f, HashMap<Integer, IExpr> map, int order, IExpr ring) {
		super(F.SeriesData);
		this.power = order;
		this.f = f;
		// this.vals = map;
		this.ring = ring;
	}

	public ASTPowerSeries(Function<Integer, IExpr> f, IExpr ring) {
		super(F.SeriesData);
		this.f = f;
		// this.vals = new HashMap<Integer, IExpr>();
		this.ring = ring;
	}

	public ASTPowerSeries(Function<Integer, IExpr> f, HashMap<Integer, IExpr> map, IExpr ring) {
		super(F.SeriesData);
		this.f = f;
		// this.vals = map;
		this.ring = ring;
	}

	/**
	 * 
	 * 
	 * @param vector
	 *            the vector which should be wrapped in this object.
	 * @param deepCopy
	 *            TODO
	 */
	public ASTPowerSeries(HashMap<Integer, IExpr> vector, boolean deepCopy) {
		super(F.SeriesData);
		// if (deepCopy) {
		// this.vals = (HashMap<Integer, IExpr>) vector.clone();
		// } else {
		// this.vals = vector;
		// }
	}

	/**
	 * Order of a power series
	 * 
	 * @return null if power series .isProbableZero() is true or the order of the power series
	 */
	public Integer order() {
		for (Integer i = 0; i < 30; i++) {
			if (!getCoef(i).isZero()) {
				return i;
			}
		}
		return null;
	}

	public Integer getOrder() {
		return power;
	}

	private void normalize() {
		Integer ord = order();
		if (ord == null) {
			power = 0;
		} else {
			shift(-ord);
			power = power - ord;
		}
	}
	/**
	 * Adds the objects in the specified collection to this {@code ArrayList}.
	 * 
	 * @param collection
	 *            the collection of objects.
	 * @return {@code true} if this {@code ArrayList} is modified, {@code false} otherwise.
	 */
	// @Override
	// public boolean appendAll(Collection<? extends IExpr> collection) {
	// hashValue = 0;
	// throw new UnsupportedOperationException();
	// }

	// @Override
	// public boolean appendAll(IAST ast, int startPosition, int endPosition) {
	// throw new UnsupportedOperationException();
	// }

	/**
	 * Inserts the objects in the specified collection at the specified location in this List. The objects are added in
	 * the order they are returned from the collection's iterator.
	 * 
	 * @param location
	 *            the index at which to insert.
	 * @param collection
	 *            the collection of objects.
	 * @return {@code true} if this {@code ArrayList} is modified, {@code false} otherwise.
	 * @throws IndexOutOfBoundsException
	 *             when {@code location < 0 || > size()}
	 */
	// @Override
	// public boolean appendAll(int location, Collection<? extends IExpr> collection) {
	// hashValue = 0;
	// throw new UnsupportedOperationException();
	// }

	// @Override
	// public boolean addAll(List<? extends IExpr> list) {
	// throw new UnsupportedOperationException();
	// }

	// @Override
	// public boolean appendAll(List<? extends IExpr> ast, int startPosition, int endPosition) {
	// throw new UnsupportedOperationException();
	// }

	// @Override
	// public boolean appendArgs(IAST ast) {
	// throw new UnsupportedOperationException();
	// }

	// @Override
	// public final boolean appendArgs(IAST ast, int untilPosition) {
	// throw new UnsupportedOperationException();
	// }

	// @Override
	// public IAST appendOneIdentity(IAST subAST) {
	// throw new UnsupportedOperationException();
	// }

	/**
	 * Adds the specified object at the end of this {@code ArrayList}.
	 * 
	 * @param object
	 *            the object to add.
	 * @return always true
	 */
	// @Override
	// public boolean append(IExpr object) {
	// hashValue = 0;
	// throw new UnsupportedOperationException();
	// }

	/**
	 * Inserts the specified object into this {@code ArrayList} at the specified location. The object is inserted before
	 * any previous element at the specified location. If the location is equal to the size of this {@code ArrayList},
	 * the object is added at the end.
	 * 
	 * @param location
	 *            the index at which to insert the object.
	 * @param object
	 *            the object to add.
	 * @throws IndexOutOfBoundsException
	 *             when {@code location < 0 || > size()}
	 */
	// @Override
	// public void append(int location, IExpr object) {
	// hashValue = 0;
	// throw new UnsupportedOperationException();
	// }

	@Override
	public Set<IExpr> asSet() {
		throw new UnsupportedOperationException();
		// empty set:
		// return new HashSet<IExpr>();
	}

	/**
	 * Removes all elements from this {@code ArrayList}, leaving it empty.
	 * 
	 * @see #isEmpty
	 * @see #size
	 */
	// @Override
	// public void clear() {
	// hashValue = 0;
	// throw new UnsupportedOperationException();
	// }

	/**
	 * Returns a new {@code HMArrayList} with the same elements, the same size and the same capacity as this
	 * {@code HMArrayList}.
	 * 
	 * @return a shallow copy of this {@code ArrayList}
	 * @see java.lang.Cloneable
	 */
	@Override
	public IAST clone() {
		IASTAppendable series = new ASTPowerSeries(x, x0, nMin, nMax, denominator);
		series.appendArgs(this);
		return series;
	}

	/** {@inheritDoc} */
	@Override
	public ASTPowerSeries copy() {
		ASTPowerSeries series = new ASTPowerSeries(x, x0, nMin, nMax, denominator);
		series.appendArgs(this);
		return series;
	}

	@Override
	public IASTAppendable copyAppendable() {
		return null;// Convert.vector2List(vector);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof ASTPowerSeries) {
			if (obj == this) {
				return true;
			}
			ASTPowerSeries that = (ASTPowerSeries) obj;
			if (!x.equals(that.x)) {
				return false;
			}
			if (!x0.equals(that.x0)) {
				return false;
			}
			if (nMin != that.nMin) {
				return false;
			}
			if (denominator != that.denominator) {
				return false;
			}
			if (power != that.power) {
				return false;
			}
			if (super.equals(obj)) {
				return true;
			}
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public IExpr evaluate(EvalEngine engine) {
		return F.NIL;
	}

	/** {@inheritDoc} */
	@Override
	public final String fullFormString() {
		return fullFormString(F.List);
	}

	/** {@inheritDoc} */
	@Override
	public IAST filter(IASTAppendable filterAST, Predicate<? super IExpr> predicate) {
		return filterAST;
	}

	// @Override
	// public IExpr get(int location) {
	// IExpr val = vals.get(location);
	// return val;
	// }

	public IExpr getCoef(int k) {
		// if (vals.containsKey(k))
		// return vals.get(k);
		// IExpr c = f.apply(k);
		// vals.put(k, c);
		return get(k + 1);
	}

	public boolean isInvertible() {
		return size() > 1 && !getCoef(0).isZero();
	}

	// @Override
	// public int hashCode() {
	// if (hashValue == 0) {
	// hashValue = vals.hashCode();
	// }
	// return hashValue;
	// }

	/** {@inheritDoc} */
	@Override
	public boolean isAST0() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isAST1() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isAST2() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isAST3() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isList() {
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isRealVector() {
		return true;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
		throw new UnsupportedOperationException();
		// this.fEvalFlags = objectInput.readShort();
		//
		// int size;
		// byte attributeFlags = objectInput.readByte();
		// if (attributeFlags != 0) {
		// size = attributeFlags;
		// int exprIDSize = objectInput.readByte();
		// for (int i = 0; i < exprIDSize; i++) {
		// set(i, F.GLOBAL_IDS[objectInput.readShort()]);
		// }
		// for (int i = exprIDSize; i < size; i++) {
		// set(i, (IExpr) objectInput.readObject());
		// }
		// return;
		// }
		//
		// size = objectInput.readInt();
		// for (int i = 0; i < size; i++) {
		// set(i, (IExpr) objectInput.readObject());
		// }
	}

	/**
	 * Returns the number of elements in this {@code ASTPowerSeries}.
	 * 
	 * @return the number of elements in this {@code ASTPowerSeries}.
	 */
	// @Override
	// public int size() {
	// return vals.size();
	// }

	@Override
	public ASTPowerSeries plus(IExpr b) {
		ASTPowerSeries series = copy();
		if (size() > 1) {
			series.set(1, this.get(1).subtract(b));
		} else {
			series.append(b);
		}
		return series;
		// return new ASTPowerSeries(n -> n == 0L ? this.getCoef(n).plus(b) : this.getCoef(n), ring);
	}

	@Override
	public ASTPowerSeries subtract(IExpr b) {
		ASTPowerSeries series = copy();
		if (size() > 1) {
			series.set(1, this.get(1).subtract(b));
		} else {
			series.append(b.negate());
		}
		return series;
		// return new ASTPowerSeries(n -> n == 0L ? this.getCoef(n).subtract(b) : this.getCoef(n), ring);
	}

	@Override
	public ASTPowerSeries times(IExpr b) {
		ASTPowerSeries series = copy();
		for (int i = 1; i < size(); i++) {
			series.set(i, this.get(i).times(b));
		}
		return series;
		// return new ASTPowerSeries(n -> this.getCoef(n).times(b), ring);
	}

	public ASTPowerSeries plus(ASTPowerSeries b) {
		ASTPowerSeries series = copy();
		for (int i = 1; i < size(); i++) {
			series.set(i, this.get(i).plus(b.get(i)));
		}
		return series;
		// return new ASTPowerSeries(n -> this.getCoef(n).plus(b.getCoef(n)), ring);
	}

	@Override
	public ASTPowerSeries negate() {
		ASTPowerSeries series = copy();
		for (int i = 1; i < size(); i++) {
			series.set(i, get(i).negate());
		}
		return series;
	}

	/**
	 * Shifts the power series
	 * 
	 * @param s
	 *            an integer
	 * @return shifted series (to the right)
	 */
	public ASTPowerSeries shift(int s) {
		ASTPowerSeries series = new ASTPowerSeries(x, x0, nMin, nMax, denominator);
		for (int i = 0; i < s; i++) {
			series.append(F.C0);
		}
		series.appendArgs(this);
		return series;
		// return new ASTPowerSeries(n -> n < s ? F.C0 : this.getCoef(n - s), ring);
	}

	public ASTPowerSeries subtract(ASTPowerSeries b) {
		return new ASTPowerSeries(n -> this.getCoef(n).subtract(b.getCoef(n)), ring);
	}

	public ASTPowerSeries times(ASTPowerSeries b) { 
		Function<Integer, IExpr> g = k -> {
			IExpr sum = F.C0;
			for (Integer i = 0; i <= k; i++) {
				sum = sum.plus(this.getCoef(i).times(b.getCoef(k - i)));
			}
			return sum;
		};
		return new ASTPowerSeries(g, ring);
	}

	@Override
	public ASTPowerSeries inverse() {
		if (!isInvertible()) {
			int coefficientLength = argSize();
			if (coefficientLength > 8) {
				return null;
			}
			if (coefficientLength > 1 && !this.getCoef(1).isZero()) {

				ASTPowerSeries ps = new ASTPowerSeries(x, x0, nMin, nMax, denominator);
				ps.append(F.C0);

				ps.append(getCoef(1).inverse());
				if (coefficientLength > 2) {
					// -a1^(-3) * a2
					ps.append(getCoef(1).power(-3).times(getCoef(2)).negate());
					if (coefficientLength > 3) {
						// a1^(-5) * (2*a2^2-a1*a3)
						ps.append(F.Times.of(F.Power(getCoef(1), -5), //
								F.Subtract(F.Times(F.C2, F.Sqr(getCoef(2))), F.Times(getCoef(1), getCoef(3)))));
						if (coefficientLength > 4) {
							// a1^(-7) * (5*a1*a2*a3-a1*a4-5*a2^3)
							ps.append(F.Times.of(F.Power(getCoef(1), -7), //
									F.Plus(F.Times(F.CN5, F.Power(getCoef(2), 3)),
											F.Times(F.C5, getCoef(1), getCoef(2), getCoef(3)),
											F.Times(F.CN1, getCoef(1), getCoef(4)))));
							if (coefficientLength > 5) {
								// a1^(-9) * (6*a1^2*a2*a4+3*a1^2*a3^2+14*a2^4-a1^3*a5-21*a1*a2^2*a3)
								ps.append(F.Times.of(F.Power(getCoef(1), -9), //
										F.Plus(F.Times(F.ZZ(14L), F.Power(getCoef(2), 4)),
												F.Times(F.ZZ(-21L), getCoef(1), F.Sqr(getCoef(2)), getCoef(3)),
												F.Times(F.C3, F.Sqr(getCoef(1)), F.Sqr(getCoef(3))),
												F.Times(F.C6, F.Sqr(getCoef(1)), getCoef(2), getCoef(4)),
												F.Times(F.CN1, F.Power(getCoef(1), 3), getCoef(5)))));
								if (coefficientLength > 6) {
									// a1^(-11) *
									// (7*a1^3*a2*a5+7*a1^3*a3*a4+84*a1*a2^3*a3-a1^4*a6-28*a1^2*a2*a3^2-42*a2^5-28*a1^2*a2^2*a4)
									ps.append(F.Times.of(F.Power(getCoef(1), -11), //
											F.Plus(F.Times(F.ZZ(-42L), F.Power(getCoef(2), 5)),
													F.Times(F.ZZ(84L), getCoef(1), F.Power(getCoef(2), 3), getCoef(3)),
													F.Times(F.ZZ(-28L), F.Sqr(getCoef(1)), getCoef(2),
															F.Sqr(getCoef(3))),
													F.Times(F.ZZ(-28L), F.Sqr(getCoef(1)), F.Sqr(getCoef(2)),
															getCoef(4)),
													F.Times(F.C7, F.Power(getCoef(1), 3), getCoef(3), getCoef(4)),
													F.Times(F.C7, F.Power(getCoef(1), 3), getCoef(2), getCoef(5)),
													F.Times(F.CN1, F.Power(getCoef(1), 4), getCoef(6)))));
									if (coefficientLength > 7) {
										// a1^(-13) * (8*a1^4*a2*a6 + 8*a1^4*a3*a5 + 4*a1^4*a4^2 + 120*a1^2*a2^3*a4 +
										// 180*a1^2*a2^2*a3^2 +
										// 132*a2^6 - a1^5*a7 - 36*a1^3*a2^2*a5 - 72*a1^3*a2*a3*a4 - 12*a1^3*a3^3 -
										// 330*a1*a2^4*a3)
										ps.append(F.Times.of(F.Power(getCoef(1), -13), F.Plus(
												F.Times(F.ZZ(132L), F.Power(getCoef(2), 6)),
												F.Times(F.ZZ(-330L), getCoef(1), F.Power(getCoef(2), 4), getCoef(3)),
												F.Times(F.ZZ(180L), F.Sqr(getCoef(1)), F.Sqr(getCoef(2)),
														F.Sqr(getCoef(3))),
												F.Times(F.ZZ(-12L), F.Power(getCoef(1), 3), F.Power(getCoef(3), 3)),
												F.Times(F.ZZ(120L), F.Sqr(getCoef(1)), F.Power(getCoef(2), 3),
														getCoef(4)),
												F.Times(F.ZZ(-72L), F.Power(getCoef(1), 3), getCoef(2), getCoef(3),
														getCoef(4)),
												F.Times(F.C4, F.Power(getCoef(1), 4), F.Sqr(getCoef(4))),
												F.Times(F.ZZ(-36L), F.Power(getCoef(1), 3), F.Sqr(getCoef(2)),
														getCoef(5)),
												F.Times(F.C8, F.Power(getCoef(1), 4), getCoef(3), getCoef(5)),
												F.Times(F.C8, F.Power(getCoef(1), 4), getCoef(2), getCoef(6)),
												F.Times(F.CN1, F.Power(getCoef(1), 5), getCoef(7)))));
									}
								}
							}
						}
					}
				}
				return ps;
			}
			throw new IllegalStateException("PowerSeries cannot be inverted");
		}
		IExpr d = this.getCoef(0).power(-1L);
		// HashMap<Integer, IExpr> bTable = new HashMap<>();
		// bTable.put(0, a);
		ASTPowerSeries ps = new ASTPowerSeries(x, x0, nMin, nMax, denominator);
		ps.append(d);
		for (int i = 1; i < size(); i++) {
			IExpr c = F.C0;
			for (int k = 0; k < i; k++) {
				IExpr m = this.getCoef(k).times(this.getCoef(i - k));
				c = c.plus(m);
			}
			IExpr b = c.times(d.times(F.CN1));
			ps.append(b);
		}
		return ps;

		// C d = leadingCoefficient().inverse(); // may fail
		// if (i == 0) {
		// return d;
		// }
		// C c = null; //fac.getZERO();
		// for (int k = 0; k < i; k++) {
		// C m = get(k).multiply(coefficient(i - k));
		// if (c == null) {
		// c = m;
		// } else {
		// c = c.sum(m);
		// }
		// }
		// c = c.multiply(d.negate());
		// return c;

		// BiFunction<BiFunction, Integer, IExpr> g = (s, k) -> {
		// if (bTable.containsKey(k)) {
		// return bTable.get(k);
		// }
		// IExpr sum = F.C0;
		// for (Integer n = 0; n < k; n++) {
		// sum = sum.plus(this.getCoef(k - n).times((IExpr) s.apply(s, n)));
		// }
		// IExpr b = sum.times(a.times(F.CN1));
		// bTable.put(k, b);
		// return b;
		// };
		// Function<Integer, IExpr> h = k -> g.apply(g, k);
		// return new ASTPowerSeries(h, bTable, -power, ring);
	}

	// @Override
	// public String toString() {
	// final StringBuilder buf = new StringBuilder();
	// toString(buf);
	// return buf.toString();
	// }
	//
	// public void toString(Appendable buf) {
	// try {
	// buf.append('{');
	// int size = vals.size();
	// for (int i = 1; i < size + 1; i++) {
	// buf.append(vals.get(i).toString());
	// if (i < size - 1) {
	// buf.append(",");
	// }
	// }
	// buf.append('}');
	// } catch (IOException e) {
	// if (Config.DEBUG) {
	// e.printStackTrace();
	// }
	// }
	// }

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		throw new UnsupportedOperationException();
		// objectOutput.writeShort(fEvalFlags);
		//
		// int size = size();
		// byte attributeFlags = (byte) 0;
		//
		// ExprID temp = F.GLOBAL_IDS_MAP.get(head());
		// if (temp != null) {
		// short exprID = temp.getExprID();
		// if (exprID <= Short.MAX_VALUE) {
		// int exprIDSize = 1;
		// short[] exprIDArray = new short[size];
		// exprIDArray[0] = exprID;
		// for (int i = 1; i < size; i++) {
		// temp = F.GLOBAL_IDS_MAP.get(get(i));
		// if (temp == null) {
		// break;
		// }
		// exprID = temp.getExprID();
		// if (exprID <= Short.MAX_VALUE) {
		// exprIDArray[i] = exprID;
		// exprIDSize++;
		// } else {
		// break;
		// }
		// }
		// // optimized path
		// attributeFlags = (byte) size;
		// objectOutput.writeByte(attributeFlags);
		// objectOutput.writeByte((byte) exprIDSize);
		// for (int i = 0; i < exprIDSize; i++) {
		// objectOutput.writeShort(exprIDArray[i]);
		// }
		// for (int i = exprIDSize; i < size; i++) {
		// objectOutput.writeObject(get(i));
		// }
		// return;
		// }
		// }
		//
		// objectOutput.writeByte(attributeFlags);
		// objectOutput.writeInt(size);
		// for (int i = 0; i < size; i++) {
		// objectOutput.writeObject(get(i));
		// }
	}

	private Object writeReplace() throws ObjectStreamException {
		return optional(F.GLOBAL_IDS_MAP.get(this));
	}

	public boolean isProbableZero() {
		for (int i = 1; i < size(); i++) {
			if (!get(i).isZero()) {
				return false;
			}
		}
		// for (Map.Entry<Integer, IExpr> element : vals.entrySet()) {
		// if (!getCoef(element.getKey()).isZero()) {
		// return false;
		// }
		// }
		return true;
	}

	public boolean isProbableOne() {
		if (size() > 1) {
			if (!get(1).isOne()) {
				return false;
			}
			for (int i = 2; i < size(); i++) {
				if (!get(i).isZero()) {
					return false;
				}
			}
			// for (Map.Entry<Integer, IExpr> element : vals.entrySet()) {
			// Integer key = element.getKey();
			// if (key != 0) {
			// if (!getCoef(key).isZero()) {
			// return false;
			// }
			// }
			// }
			return true;
		}
		return false;
	}
}
