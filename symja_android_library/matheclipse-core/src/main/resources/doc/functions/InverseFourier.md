## InverseFourier

```
InverseFourier(vector-of-complex-numbers)
```

> Inverse discrete Fourier transform of a `vector-of-complex-numbers`. Fourier transform is restricted to vectors with length of power of 2.  

See
* [Wikipedia - Discrete Fourier transform - Inverse transform](https://en.wikipedia.org/wiki/Discrete_Fourier_transform#Inverse_transform)

### Examples 

```
>> InverseFourier({2.82843+I*9.19239,-1.41421+I*(-6.36396)})
{1.0+I*2.0,3.0+I*11.0}

>> InverseFourier({1.5,0.5+I*1.0,-0.5,0.5+I*(-1.0)})
{1.0,2.0+I*2.22045*10^-16,0.0,I*(-2.22045*10^-16)}
```

The first argument is restricted to vectors with a length of power of 2.

```
>> InverseFourier({1,2,0,0,7})
InverseFourier({1,2,0,0,7}) 
```

### Related terms

[Fourier](Fourier.md) 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of InverseFourier](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/InverseFourier.java#L15) 
