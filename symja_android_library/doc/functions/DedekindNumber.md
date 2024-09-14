## DedekindNumber

```
DedekindNumber(n)
```

> returns the `n`th Dedekind number. Currently `0 <= n <= 9` can be computed, otherwise the function returns unevaluated.

See
* [Wikipedia - Dedekind number](https://en.wikipedia.org/wiki/Dedekind_number)
* [OEIS - A000372](https://oeis.org/A000372)

### Examples

```
>> Table(DedekindNumber(i), {i,0,11})
{2,3,6,20,168,7581,7828354,2414682040998,56130437228687557907788,286386577668298411128469151667598498812366,DedekindNumber(10),DedekindNumber(11)}
```


### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of DedekindNumber](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/NumberTheory.java#L1413) 
