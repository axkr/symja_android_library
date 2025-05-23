## ArrayReduce

``` 
ArrayReduce(function, list-of-values, n)
```

> returns the `list-of-values` structure reduced for dimension `n`.
	 

### Examples 

```
>> arr = ArrayReshape(Range(24),{2,3,4}) 
{{{1,2,3,4},{5,6,7,8},{9,10,11,12}},{{13,14,15,16},{17,18,19,20},{21,22,23,24}}} 

>> ArrayReduce(f, arr, 1) 
{{f({1,13}),f({2,14}),f({3,15}),f({4,16})},{f({5,17}),f({6,18}),f({7,19}),f({8,20})},{f({9,21}),f({10,22}),f({11,23}),f({12,24})}}

>> ArrayReduce(f, arr, 2) 
{{f({1,5,9}),f({2,6,10}),f({3,7,11}),f({4,8,12})},{f({13,17,21}),f({14,18,22}),f({15,19,23}),f({16,20,24})}} 

>> ArrayReduce(f, arr, 3) 
{{f({1,2,3,4}),f({5,6,7,8}),f({9,10,11,12})},{f({13,14,15,16}),f({17,18,19,20}),f({21,22,23,24})}}
```

### Implementation status

* &#x1F9EA; - experimental

### Github

* [Implementation of ArrayReduce](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/TensorFunctions.java#L60) 
