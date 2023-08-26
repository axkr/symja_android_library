## Definition 

```
Definition(symbol)
```
> prints user-defined values and rules associated with `symbol`.
  
### Examples

``` 
>> Definition(ArcSinh)
{ArcSinh(0)=0,
 ArcSinh(I*1/2)=I*1/6*Pi,
 ArcSinh(I)=I*1/2*Pi,
 ArcSinh(1)=Log(1+Sqrt(2)),
 ArcSinh(I*1/2*Sqrt(2))=I*1/4*Pi,
 ArcSinh(I*1/2*Sqrt(3))=I*1/3*Pi,
 ArcSinh(Infinity)=Infinity,
 ArcSinh(I*Infinity)=Infinity,
 ArcSinh(ComplexInfinity)=ComplexInfinity}
 
>> a=2
2

>> Definition(a)
{a=2}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Definition](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PatternMatching.java#L497) 
