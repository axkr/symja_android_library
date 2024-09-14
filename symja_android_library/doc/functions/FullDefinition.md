## FullDefinition 

```
FullDefinition(symbol)
```
> prints value and rule definitions associated with `symbol` and dependent symbols without attribute `Protected` recursively.
  
### Examples

``` 
>> FullDefinition(ArcSinh)
Attributes(ArcSinh)={Listable,NumericFunction,Protected}

ArcSinh(I/Sqrt(2))=I*1/4*Pi

ArcSinh(Undefined)=Undefined

ArcSinh(Infinity)=Infinity

ArcSinh(I*Infinity)=Infinity

ArcSinh(I)=I*1/2*Pi

ArcSinh(0)=0

ArcSinh(I*1/2)=I*1/6*Pi

ArcSinh(I*1/2*Sqrt(3))=I*1/3*Pi

ArcSinh(ComplexInfinity)=ComplexInfinity
```

```
>> a(x_):=b(x,y);b[u_,v_]:={{u,v},a} 

>> FullDefinition(a) 
a(x_):=b(x,y)

b(u_,v_):={{u,v},a}
```


### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of FullDefinition](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PatternMatching.java#L744) 
