## HessenbergDecomposition

```
HessenbergDecomposition(matrix)
```

> calculate the Hessenberg-decomposition as a list `{p, h}` of a square `matrix`.
 
See:    
* [Wikipedia - Hessenberg-Verfahren](https://de.wikipedia.org/wiki/Hessenberg-Verfahren) 
 
### Examples

```
>> {p, h} = HessenbergDecomposition({{2.0,5,8,7},{5,2,2,8},{7,5,6,6},{5,4,4,8}})", //
{{{1.0,0.0,0.0,0.0},{0.0,-0.502519,-0.475751,-0.721897},{0.0,-0.703526,-0.260306,0.66128},{0.0,-0.502519,0.84018,-0.203895}},{{2.0,-11.65844,1.42005,0.253491},{-9.94987,14.53535,-5.31022,2.43082},{0.0,-1.83299,0.3897,-0.51527},{0.0,0.0,-3.8319,1.07495}}}
```

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of HessenbergDecomposition](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/LinearAlgebra.java#L2872) 
