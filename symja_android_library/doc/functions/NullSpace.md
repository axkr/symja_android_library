## NullSpace

```
NullSpace(matrix)
```

> returns a list of vectors that span the nullspace of the `matrix`.

See:     
* [Wikipedia - Kernel (linear algebra)](http://en.wikipedia.org/wiki/Kernel_%28linear_algebra%29)
* [Youtube - Inverse matrices, column space and null space | Essence of linear algebra, chapter 7](https://youtu.be/uQhTuRlWMxw)

### Examples

```
>> NullSpace({{1,0,-3,0,2,-8},{0,1,5,0,-1,4},{0,0,0,1,7,-9},{0,0,0,0,0,0}})
{{3,-5,1,0,0,0},
 {-2,1,0,-7,1,0},
 {8,-4,0,9,0,1}}
```
 
```
>> NullSpace({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}})   
{{1,-2,1}}
  
>> A = {{1, 1, 0}, {1, 0, 1}, {0, 1, 1}}   
>> NullSpace(A)   
{}   

>> MatrixRank(A)   
3   
```

Argument {1, {2}} at position 1 is not a non-empty rectangular matrix.
  
```
>> NullSpace({1, {2}})    
NullSpace({1, {2}})   
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of NullSpace](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/LinearAlgebra.java#L4309) 
