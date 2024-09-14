## SudokuSolve 

```
SudokuSolve(matrix)
```

> In Sudoku, the objective is to fill a 9 × 9 `matrix` with digits so that each column, each row, and each of the nine 3 × 3 subgrids that compose the grid (also called "boxes", "blocks", or "regions") contains all of the digits from 1 to 9. Every input which is not a number between 1 and 9 will be replaced with the correct number to fully solve the sudoku.

See
* [Wikipedia - Sudoku](https://en.wikipedia.org/wiki/Sudoku)

 
### Examples


```
>> SudokuSolve({{0,0,3,0,0,0,0,0,0},{4,0,0,0,8,0,0,3,6},{0,0,8,0,0,0,1,0,0},{0,4,0,0,6,0,0,7,3},{0,0,0,9,0,0,0,0,0},{0,0,0,0,0,2,0,0,5},{0,0,4,0,7,0,0,6,8},{6,0,0,0,0,0,0,0,0},{7,0,0,6,0,0,5,0,0}})
{{1,2,3,4,5,6,7,8,9},
 {4,5,7,1,8,9,2,3,6},
 {9,6,8,3,2,7,1,5,4},
 {2,4,9,5,6,1,8,7,3},
 {5,7,6,9,3,8,4,1,2},
 {8,3,1,7,4,2,6,9,5},
 {3,1,4,2,7,5,9,6,8},
 {6,9,5,8,1,4,3,2,7},
 {7,8,2,6,9,3,5,4,1}}
```

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of SudokuSolve](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/SudokuSolve.java#L13) 
