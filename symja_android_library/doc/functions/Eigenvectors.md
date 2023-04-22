## Eigenvectors

```
Eigenvectors(matrix)
```

> get the numerical eigenvectors of the `matrix`.

See
* [Wikipedia - Eigenvalue](http://en.wikipedia.org/wiki/Eigenvalue)
* [Youtube - Eigenvectors and eigenvalues | Essence of linear algebra, chapter 14](https://youtu.be/PFDu9oVAE-g)

### Examples

```
>> Eigenvectors({{1,0,0},{0,1,0},{0,0,1}})
{{1.0,0.0,0.0},{0.0,1.0,0.0},{0.0,0.0,1.0}}
```

**Note:** Symjas implementation of the `Eigenvectors` function adds zero vectors when the geometric multiplicity of the eigenvalue is smaller than its algebraic multiplicity (hence the regular eigenvector matrix should be non-square).
With these additional null vectors, the `Eigenvectors` result matrix becomes square. 
This happens for example with the following square matrix:

```
>> Eigenvectors({{1,0,0},{-2,1,0},{0,0,1}}) 
{{-2.50055*10^-13,1.0,0.0},{0.0,0.0,1.0},{0.0,0.0,0.0}} 

>> Eigenvalues({{1,0,0},{-2,1,0},{0,0,1}}) 
{1.0,1.0,1.0}
```

### Related terms 
[Eigenvalues](Eigenvalues.md), [CharacteristicPolynomial](CharacteristicPolynomial.md)