## ZernikeR

```
ZernikeR(n,m,p)
```

> returns the radial Zernike polynomial 
 
 
See: 
* [Wikipedia - Zernike polynomials](https://en.wikipedia.org/wiki/Zernike_polynomials) 

### Examples

```
>> ZernikeR(0,0,p) 
1 

>> ZernikeR(1,1,p)
p

>> ZernikeR(2,0,p)
-1+2*p^2

>> ZernikeR(2,2,p)
p^2
   
>> ZernikeR(3,1,p)
-2*p+3*p^3

>> ZernikeR(3,3,p)
p^3

>> ZernikeR(6,4,p)
-5*p^4+6*p^6
```

### Implementation status

* &#x1F9EA; - experimental

### Github

* [Implementation of ZernikeR](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PolynomialFunctions.java#L3020) 
