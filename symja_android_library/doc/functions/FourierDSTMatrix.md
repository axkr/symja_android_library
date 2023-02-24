## FourierDSTMatrix

``` 
FourierDSTMatrix(n)
```

> gives a discrete sine transform matrix with the dimension `(n,n)` and method `DST-2`

``` 
FourierDSTMatrix(n, methodNumber)
```

> gives a discrete sine transform matrix with the dimension `(n,n)` and method `DST`-`methodNumber`

See
* [Wikipedia - Discrete sine transform](https://en.wikipedia.org/wiki/Discrete_sine_transform) 

### Examples

```
>> FourierDSTMatrix(3, 1)
{{1/2,1/Sqrt(2),1/2},
 {1/Sqrt(2),0,-1/Sqrt(2)},
 {1/2,-1/Sqrt(2),1/2}} 
```

