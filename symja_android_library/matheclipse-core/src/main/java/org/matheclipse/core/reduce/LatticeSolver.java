package org.matheclipse.core.reduce;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.expression.F;

/**
 * The integer solution set of a system of linear equations.
 *
 * <p>
 * The solutions of <code>A x = b</code> over the integers form a coset <code>p + L</code> of a
 * lattice <code>L</code>. Expressing one unknown in terms of the others, which is what elimination
 * over a field does, is wrong here: <code>2 x == 4 y</code> would give <code>y == x/2</code>, which
 * is not an integer for odd <code>x</code>. The solution set has to be parametrized instead, with
 * one fresh integer parameter per degree of freedom.
 *
 * <p>
 * The parametrization is computed from the Hermite normal form of the coefficient matrix and then
 * canonicalized, so that the same solution set is always reported the same way: the basis is in
 * echelon form with positive pivots and reduced entries above them, and the offset is slid along
 * the lattice until every pivot coordinate is its smallest non negative representative.
 */
public final class LatticeSolver {

  private LatticeSolver() {}

  /** The parametrized integer solution set of a system of linear equations. */
  public static final class Solution {

    private final List<Variable> variables;

    private final BigInteger[] offset;

    private final BigInteger[][] basis;

    Solution(List<Variable> variables, BigInteger[] offset, BigInteger[][] basis) {
      this.variables = variables;
      this.offset = offset;
      this.basis = basis;
    }

    /** The variables of the system, in the order the caller asked for them. */
    public List<Variable> variables() {
      return variables;
    }

    /** One particular solution, indexed like {@link #variables()}. */
    public BigInteger[] offset() {
      return offset;
    }

    /** One row per free parameter, each indexed like {@link #variables()}. */
    public BigInteger[][] basis() {
      return basis;
    }

    /** The number of free integer parameters of the solution set. */
    public int parameterCount() {
      return basis.length;
    }

    /**
     * The value of one variable as an affine term in the given parameters.
     *
     * @param index the index into {@link #variables()}
     * @param parameters one variable per row of {@link #basis()}
     */
    public AffineTerm substitution(int index, List<Variable> parameters) {
      AffineTerm term = AffineTerm.integer(offset[index]);
      for (int row = 0; row < basis.length; row++) {
        BigInteger coefficient = basis[row][index];
        if (coefficient.signum() != 0) {
          term = term.add(AffineTerm.variable(parameters.get(row)).scale(F.ZZ(coefficient)));
        }
      }
      return term;
    }
  }

  /**
   * Solve <code>equations == 0</code> over the integers.
   *
   * <p>
   * Every variable of every equation has to be one of <code>variables</code>: a system with a free
   * parameter has a solution set which depends on the parameter's value, which this
   * parametrization cannot express.
   *
   * @param equations the left hand sides of the equations, each an integral affine term
   * @param variables the unknowns, in the order the caller asked for them
   * @return the parametrized solution set, or <code>null</code> if the system has no integer
   *         solution, a term is not integral, or a term mentions a variable outside
   *         <code>variables</code>
   */
  public static Solution solve(List<AffineTerm> equations, List<Variable> variables) {
    final int columns = variables.size();
    final int rows = equations.size();
    BigInteger[][] matrix = new BigInteger[rows][columns];
    BigInteger[] constants = new BigInteger[rows];
    for (int row = 0; row < rows; row++) {
      AffineTerm equation = equations.get(row);
      if (!equation.isIntegral()) {
        return null;
      }
      for (int column = 0; column < columns; column++) {
        matrix[row][column] =
            equation.coefficient(variables.get(column)).numerator().toBigNumerator();
      }
      constants[row] = equation.constant().numerator().toBigNumerator();
      // a variable outside the requested list makes this not a system in these unknowns alone
      if (equation.variables().size() > countOccurring(equation, variables)) {
        return null;
      }
    }

    // column reduction: transform the matrix to echelon form with unimodular column operations,
    // recording them in `transform`, so that x = transform * y solves the system in y
    BigInteger[][] transform = identity(columns);
    int[] pivotColumn = new int[rows];
    int rank = 0;
    for (int row = 0; row < rows; row++) {
      pivotColumn[row] = -1;
      for (int column = rank; column < columns; column++) {
        if (matrix[row][column].signum() == 0) {
          continue;
        }
        if (pivotColumn[row] < 0) {
          swapColumns(matrix, transform, rank, column);
          pivotColumn[row] = rank;
          continue;
        }
        // replace the pair of columns by (gcd combination, complementary combination), which
        // spans the same lattice and zeroes this row in the second column
        BigInteger[] extended = IntegerMath.extendedGcd(matrix[row][rank], matrix[row][column]);
        BigInteger divisor = extended[0];
        BigInteger left = matrix[row][rank].divide(divisor);
        BigInteger right = matrix[row][column].divide(divisor);
        combineColumns(matrix, transform, rank, column, extended[1], extended[2], right.negate(),
            left);
      }
      if (pivotColumn[row] >= 0) {
        rank++;
      }
    }

    // forward substitution for the pivot coordinates of y; every pivot must divide exactly
    BigInteger[] pivotValues = new BigInteger[columns];
    for (int index = 0; index < columns; index++) {
      pivotValues[index] = BigInteger.ZERO;
    }
    for (int row = 0; row < rows; row++) {
      BigInteger value = constants[row].negate();
      for (int column = 0; column < columns; column++) {
        if (pivotColumn[row] < 0 || column != pivotColumn[row]) {
          value = value.subtract(matrix[row][column].multiply(pivotValues[column]));
        }
      }
      if (pivotColumn[row] < 0) {
        if (value.signum() != 0) {
          // 0 == c with c != 0
          return null;
        }
        continue;
      }
      BigInteger pivot = matrix[row][pivotColumn[row]];
      BigInteger[] division = value.divideAndRemainder(pivot);
      if (division[1].signum() != 0) {
        // the pivot does not divide the right hand side, so no integer solution exists
        return null;
      }
      pivotValues[pivotColumn[row]] = division[0];
    }

    BigInteger[] offset = new BigInteger[columns];
    for (int index = 0; index < columns; index++) {
      offset[index] = BigInteger.ZERO;
      for (int column = 0; column < rank; column++) {
        offset[index] =
            offset[index].add(transform[index][column].multiply(pivotValues[column]));
      }
    }
    BigInteger[][] basis = new BigInteger[columns - rank][columns];
    for (int parameter = 0; parameter < columns - rank; parameter++) {
      for (int index = 0; index < columns; index++) {
        basis[parameter][index] = transform[index][rank + parameter];
      }
    }
    return new Solution(variables, offset, canonicalize(basis, offset));
  }

  private static int countOccurring(AffineTerm term, List<Variable> variables) {
    int count = 0;
    for (Variable variable : variables) {
      if (!term.coefficient(variable).isZero()) {
        count++;
      }
    }
    return count;
  }

  private static BigInteger[][] identity(int size) {
    BigInteger[][] matrix = new BigInteger[size][size];
    for (int row = 0; row < size; row++) {
      for (int column = 0; column < size; column++) {
        matrix[row][column] = row == column ? BigInteger.ONE : BigInteger.ZERO;
      }
    }
    return matrix;
  }

  private static void swapColumns(BigInteger[][] matrix, BigInteger[][] transform, int left,
      int right) {
    for (BigInteger[] row : matrix) {
      BigInteger temporary = row[left];
      row[left] = row[right];
      row[right] = temporary;
    }
    for (BigInteger[] row : transform) {
      BigInteger temporary = row[left];
      row[left] = row[right];
      row[right] = temporary;
    }
  }

  /** Replace columns <code>(l, r)</code> by <code>(a*l + b*r, c*l + d*r)</code>. */
  private static void combineColumns(BigInteger[][] matrix, BigInteger[][] transform, int left,
      int right, BigInteger a, BigInteger b, BigInteger c, BigInteger d) {
    combine(matrix, left, right, a, b, c, d);
    combine(transform, left, right, a, b, c, d);
  }

  private static void combine(BigInteger[][] rows, int left, int right, BigInteger a, BigInteger b,
      BigInteger c, BigInteger d) {
    for (BigInteger[] row : rows) {
      BigInteger newLeft = a.multiply(row[left]).add(b.multiply(row[right]));
      BigInteger newRight = c.multiply(row[left]).add(d.multiply(row[right]));
      row[left] = newLeft;
      row[right] = newRight;
    }
  }

  /**
   * Put the basis into Hermite normal form and reduce the offset against it.
   *
   * <p>
   * Any basis of the lattice and any point on it describe the same solution set, and the
   * elimination above lands on an arbitrary one. Canonicalizing makes the reported answer a
   * function of the solution set alone, so that <code>3 x + 5 y == 7</code> reports
   * <code>x == 4 + 5 C(1)</code> rather than the equally correct <code>x == 14 + 5 C(1)</code>.
   */
  private static BigInteger[][] canonicalize(BigInteger[][] basis, BigInteger[] offset) {
    final int columns = offset.length;
    List<int[]> pivots = new ArrayList<int[]>();
    int pivotRow = 0;
    for (int column = 0; column < columns && pivotRow < basis.length; column++) {
      for (int row = pivotRow + 1; row < basis.length; row++) {
        if (basis[row][column].signum() == 0) {
          continue;
        }
        if (basis[pivotRow][column].signum() == 0) {
          BigInteger[] temporary = basis[pivotRow];
          basis[pivotRow] = basis[row];
          basis[row] = temporary;
          continue;
        }
        BigInteger[] extended =
            IntegerMath.extendedGcd(basis[pivotRow][column], basis[row][column]);
        BigInteger divisor = extended[0];
        BigInteger left = basis[pivotRow][column].divide(divisor);
        BigInteger right = basis[row][column].divide(divisor);
        for (int index = 0; index < columns; index++) {
          BigInteger keep = extended[1].multiply(basis[pivotRow][index])
              .add(extended[2].multiply(basis[row][index]));
          BigInteger zeroed = left.multiply(basis[row][index])
              .subtract(right.multiply(basis[pivotRow][index]));
          basis[pivotRow][index] = keep;
          basis[row][index] = zeroed;
        }
      }
      if (basis[pivotRow][column].signum() == 0) {
        continue;
      }
      if (basis[pivotRow][column].signum() < 0) {
        for (int index = 0; index < columns; index++) {
          basis[pivotRow][index] = basis[pivotRow][index].negate();
        }
      }
      BigInteger pivot = basis[pivotRow][column];
      for (int row = 0; row < pivotRow; row++) {
        BigInteger factor = IntegerMath.floorDiv(basis[row][column], pivot);
        if (factor.signum() != 0) {
          for (int index = 0; index < columns; index++) {
            basis[row][index] =
                basis[row][index].subtract(factor.multiply(basis[pivotRow][index]));
          }
        }
      }
      pivots.add(new int[] {pivotRow, column});
      pivotRow++;
    }
    if (pivotRow < basis.length) {
      // drop the rows which reduced to zero: they are not degrees of freedom
      BigInteger[][] reduced = new BigInteger[pivotRow][];
      System.arraycopy(basis, 0, reduced, 0, pivotRow);
      basis = reduced;
    }
    for (int[] pivot : pivots) {
      BigInteger factor = IntegerMath.floorDiv(offset[pivot[1]], basis[pivot[0]][pivot[1]]);
      if (factor.signum() != 0) {
        for (int index = 0; index < columns; index++) {
          offset[index] = offset[index].subtract(factor.multiply(basis[pivot[0]][index]));
        }
      }
    }
    return basis;
  }
}
