## JordanDecomposition

```
JordanDecomposition(matrix)
```

> calculate the Jordan-decomposition as a list `{s, j}` of a square `matrix` with the property `s.j.Inverse(s) == matrix`, where `s` is the similarity matrix and `j` is the Jordan normal form of the `matrix`.
 
See:    
* [Wikipedia - Jordan normal form](https://en.wikipedia.org/wiki/Jordan_normal_form) 
 
### Examples

```
>> JordanDecomposition({{5,4,2,1},{0,1,-1,-1},{-1,-1,3,0},{1,1,-1,2}}) 
{{{-1,1,1,1},{1,-1,0,0},{0,0,-1,0},{0,1,1,0}},{{1,0,0,0},{0,2,0,0},{0,0,4,1},{0,0,0,4}}}
```

### Related terms 
[MatrixExp](MatrixExp.md), [MatrixFunction](MatrixFunction.md), [MatrixLog](MatrixLog.md), [SchurDecomposition](SchurDecomposition.md)