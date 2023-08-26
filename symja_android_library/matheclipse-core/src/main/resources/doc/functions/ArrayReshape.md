## ArrayReshape

``` 
ArrayReshape(list-of-values, list-of-dimension)
```

> returns the `list-of-values` elements reshaped as nested list with dimensions according to the `list-of-dimension`.
	
```
ArrayReshape(list-of-values, list-of-dimension, expr)
```

> Use `expr` to fill up elements, if there are too little elements in the `list-of-values`.
 

### Examples

A list of non-negative integers is expected at position 2. The optional  third argument `x` is used to fill up the structure:

```
>> ArrayReshape({a, b, c, d, e, f}, {2, 3, 3, 2}, x)
{{{{a,b},{c,d},{e,f}},{{x,x},{x,x},{x,x}},{{x,x},{x,x},{x,x}}},{{{x,x},{x,x},{x,x}},{{x,x},{x,x},{x,x}},{{x,x},{x,x},{x,x}}}}
```

Ignore unnecessary elements

```
>> ArrayReshape(Range(1000), {3, 2, 2})
{{{1,2},{3,4}},{{5,6},{7,8}},{{9,10},{11,12}}}
```

 
 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of ArrayReshape](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/TensorFunctions.java#L103) 
