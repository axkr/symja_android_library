## Fourier

```
Fourier(vector-of-complex-numbers)
```

> Discrete Fourier transform of a `vector-of-complex-numbers`. Fourier transform is restricted to vectors with length of power of 2.  

See
* [Wikipedia - Discrete Fourier transform](https://en.wikipedia.org/wiki/Discrete_Fourier_transform)

### Examples 

```
>> Fourier({1 + 2*I, 3 + 11*I})
{2.82843+I*9.19239,-1.41421+I*(-6.36396)}

>> Fourier({1,2,0,0})
{1.5,0.5+I*1.0,-0.5,0.5+I*(-1.0)}
```

The first argument is restricted to vectors with a length of power of 2.

```
>> Fourier({1,2,0,0,7})
Fourier({1,2,0,0,7}) 
```
				
### Related terms

[InverseFourier](InverseFourier.md) 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Fourier](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/Fourier.java#L15) 
