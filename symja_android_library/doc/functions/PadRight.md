## PadRight

```
PadRight(list, n)
```

> pads `list` to length `n` by adding `0` on the right. 

```
PadRight(list, n, x)
```

> pads `list` to length `n` by adding `x` on the right. 

```
PadRight(list)
```

> turns the ragged list `list` into a regular list by adding '0' on the right. 
 
### Examples 

```
>> PadRight({1, 2, 3}, 5)    
{1,2,3,0,0}    

>> PadRight(x(a, b, c), 5)    
x(a,b,c,0,0)  

>> PadRight({1, 2, 3}, 2)    
{1,2}   

>> PadRight({{}, {1, 2}, {1, 2, 3}})    
{{0,0,0},{1,2,0},{1,2,3}}
```

[OEIS - A134860](https://oeis.org/A134860):

```
>> With({r = Map(Fibonacci, Range(2, 14))}, Position(#, {1, 0, 1})[[All, 1]] &@ Table(If(Length@ # < 3, {}, Take(#, -3)) &@ IntegerDigits@ Total@ Map(FromDigits@ PadRight({1}, Flatten@ #) &@ Reverse@ Position(r, #) &, Abs@ Differences@ NestWhileList(Function(k, k - SelectFirst(Reverse@ r, # < k &)), n + 1, # > 1 &)), {n, 373}))

{4,12,17,25,33,38,46,51,59,67,72,80,88,93,101,106,114,122,127,135,140,148,156,
161,169,177,182,190,195,203,211,216,224,232,237,245,250,258,266,271,279,284,292,
300,305,313,321,326,334,339,347,355,360,368,373}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of PadRight](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ListFunctions.java#L4673) 
