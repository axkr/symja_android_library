## PadLeft 

```
PadLeft(list, n)
```

> pads `list` to length `n` by adding `0` on the left. 

```
PadLeft(list, n, x)
```

> pads `list` to length `n` by adding `x` on the left. 

```
PadLeft(list)
```

> turns the ragged list `list` into a regular list by adding '0' on the left. 
 
### Examples 

```
>> PadLeft({1, 2, 3}, 5)    
{0,0,1,2,3}   

>> PadLeft(x(a, b, c), 5)    
x(0,0,a,b,c)    

>> PadLeft({1, 2, 3}, 2)    
{2, 3}    

>> PadLeft({{}, {1, 2}, {1, 2, 3}})    
{{0,0,0},{0,1,2},{1,2,3}}
```

[OEIS - A196023](https://oeis.org/A196023):

```
>> Select(Table(FromDigits@Join(Flatten@IntegerDigits@PadLeft({666}, 2, n), Reverse@IntegerDigits(n)), {n, 397}), PrimeQ)
{16661,76667,3166613,3466643,7466647,7666667,145666541,148666841,152666251, 
155666551,169666961,176666671,181666181,304666403,305666503,307666703,308666803, 
329666923,347666743,349666943,373666373,374666473,383666383,391666193,397666793}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of PadLeft](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ListFunctions.java#L4443) 
