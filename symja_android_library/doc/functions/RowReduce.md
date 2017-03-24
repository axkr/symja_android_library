## RowReduce

```
RowReduce(matrix)
```

> get the row echelon form of the `matrix`.

See:   
* [Wikipedia - Row echelon form](http://en.wikipedia.org/wiki/Row_echelon_form)

### Examples
```
>>> RowReduce({{1,1,0,1,5},{1,0,0,2,2},{0,0,1,4,-1},{0,0,0,0,0}})
{{1,0,0,2,2},  
 {0,1,0,-1,3},
 {0,0,1,4,-1},
 {0,0,0,0,0}}
```  