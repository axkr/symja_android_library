## PrincipleComponents

```
PrincipleComponents(matrix)
```

> compute the principle components of the `matrix`.
  
```
PrincipleComponents(matrix, Method->"Correlation")
```

> compute the principle components of the `matrix` with the `Correlation` method. The default computing method is `Covariance`.
  
See
* [Wikipedia - Principal component analysis](https://en.wikipedia.org/wiki/Principal_component_analysis)

### Examples

``` 
>> PrincipalComponents({{90.0, 60, 90},{90, 90, 30},{60, 60, 60},{60, 60, 90},{30, 30, 30}})
{{34.37098,-13.66927,-10.38202},
 {9.98346,47.68821,1.47161},
 {-3.93481,-2.31599,3.89274}, 
 {14.69692,-25.24923,9.08167}, 
 {-55.11655,-6.45371,-4.06399}}
 
>> PrincipalComponents({{90.0, 60, 90},{90, 90, 30},{60, 60, 60},{60, 60, 90},{30, 30, 30}}, Method->"Correlation")
{{0.911826,-0.942809,0.440421},
 {1.38323,1.41421,-0.0309834},
 {-0.169031,0.0,-0.169031},
 {0.0666714,-0.942809,-0.404733},
 {-2.1927,0.471405,0.164326}}
```

### Related terms 
[Standardize](Standardize.md)