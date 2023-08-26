## LogisticSigmoid

```
LogisticSigmoid(z)
```

> returns the logistic sigmoid of `z`.

See
* [Wikipedia - Logistic function](https://en.wikipedia.org/wiki/Logistic_function)

### Examples

```
>> LogisticSigmoid(0.5)
0.6224593312018546
 
>> LogisticSigmoid(0.5 + 2.3*I)
1.0647505893884985+I*0.8081774171575826
 
>> LogisticSigmoid({-0.2, 0.1, 0.3})
{0.45016600268752216,0.52497918747894,0.574442516811659} 
 
>> LogisticSigmoid(I*Pi)
LogisticSigmoid(I*Pi)
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of LogisticSigmoid](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ExpTrigsFunctions.java#L2522) 
