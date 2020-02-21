## GegenbauerC

```
GegenbauerC(n, a, x)
```

> returns the GegenbauerC polynomial.


See:  
* [Wikipedia - Gegenbauer polynomials](https://en.wikipedia.org/wiki/Gegenbauer_polynomials)

### Examples

```   
>> GegenbauerC(2,a,z) 
-a+2*a*(1+a)*z^2
				
>> GegenbauerC(8, z)    
1/4-8*z^2+40*z^4-64*z^6+32*z^8
 
>> GegenbauerC(3, 1 + I)
-22/3+I*10/3
```
    