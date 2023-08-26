## RotateLeft

```
RotateLeft(list)
```

> rotates the items of `list` by one item to the left.
 
```
RotateLeft(list, n)
```

> rotates the items of `list` by `n` items to the left.

### Examples

```
>> RotateLeft({1, 2, 3})
{2,3,1}

>> RotateLeft(Range(10), 3)
{4,5,6,7,8,9,10,1,2,3}

>> RotateLeft(x(a, b, c), 2)
x(c,a,b)
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of RotateLeft](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ListFunctions.java#L6435) 
