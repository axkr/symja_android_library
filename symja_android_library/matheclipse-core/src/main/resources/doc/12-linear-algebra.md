## Linear Algebra

Let’s consider the matrix

```
>> A = {{1, 1, 0}, {1, 0, 1}, {0, 1, 1}};
```

The derivatives are

```
>> MatrixForm(A)
```

We can compute its eigenvalues and eigenvectors:

```
>> Eigenvalues(A)

>> Eigenvectors(A)
```

This yields the diagonalization of `A`:

```
>> T = Transpose(Eigenvectors(A)); MatrixForm(T)
```

```
>> Inverse(T) . A . T // MatrixForm
 
>> % == DiagonalMatrix(Eigenvalues(A))
```

We can solve linear systems:

```
>> LinearSolve(A, {1, 2, 3})
 
>> A . %
```

In this case, the solution is unique:

```
>> NullSpace(A)
```

Let’s consider a singular matrix:

```
>> B = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};

>> MatrixRank(B)
 
>> s = LinearSolve(B, {1, 2, 3})
 
>> NullSpace(B)

>> B . (RandomInteger(100) * %[[1]] + s)
```