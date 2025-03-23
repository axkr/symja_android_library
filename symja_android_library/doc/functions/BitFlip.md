## BitFlip

```
BitFlip(i, n)
```

> flips the `n`th bit in the integer `i`. 
 
### Examples

``` 
>> IntegerDigits(36,2)
{1,0,0,1,0,0}

>> BitFlip(36,2) 
32

>> IntegerDigits(32,2) 
{1,0,0,0,0,0}  
```

If `n` is negative the bits of the integer `i` are counted from the left. 

```
>> BitFlip(36,-4)
32
```





### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of BitFlip](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/IntegerFunctions.java#L411) 
